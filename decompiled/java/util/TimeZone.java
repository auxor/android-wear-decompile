package java.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import libcore.icu.TimeZoneNames;
import libcore.io.IoUtils;
import libcore.util.ZoneInfoDB;
import org.apache.harmony.luni.internal.util.TimezoneGetter;

public abstract class TimeZone implements Serializable, Cloneable {
    private static final Pattern CUSTOM_ZONE_ID_PATTERN;
    private static final TimeZone GMT;
    public static final int LONG = 1;
    public static final int SHORT = 0;
    private static final TimeZone UTC;
    private static TimeZone defaultTimeZone = null;
    private static final long serialVersionUID = 3581463369166924961L;
    private String ID;

    public abstract int getOffset(int i, int i2, int i3, int i4, int i5, int i6);

    public abstract int getRawOffset();

    public abstract boolean inDaylightTime(Date date);

    public abstract void setRawOffset(int i);

    public abstract boolean useDaylightTime();

    static {
        CUSTOM_ZONE_ID_PATTERN = Pattern.compile("^GMT[-+](\\d{1,2})(:?(\\d\\d))?$");
        GMT = new SimpleTimeZone(SHORT, "GMT");
        UTC = new SimpleTimeZone(SHORT, "UTC");
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (Object e) {
            throw new AssertionError(e);
        }
    }

    public static synchronized String[] getAvailableIDs() {
        String[] availableIDs;
        synchronized (TimeZone.class) {
            availableIDs = ZoneInfoDB.getInstance().getAvailableIDs();
        }
        return availableIDs;
    }

    public static synchronized String[] getAvailableIDs(int offsetMillis) {
        String[] availableIDs;
        synchronized (TimeZone.class) {
            availableIDs = ZoneInfoDB.getInstance().getAvailableIDs(offsetMillis);
        }
        return availableIDs;
    }

    public static synchronized TimeZone getDefault() {
        TimeZone timeZone;
        synchronized (TimeZone.class) {
            if (defaultTimeZone == null) {
                TimezoneGetter tzGetter = TimezoneGetter.getInstance();
                String zoneName = tzGetter != null ? tzGetter.getId() : null;
                if (zoneName != null) {
                    zoneName = zoneName.trim();
                }
                if (zoneName == null || zoneName.isEmpty()) {
                    try {
                        zoneName = IoUtils.readFileAsString("/etc/timezone");
                    } catch (IOException e) {
                        zoneName = "GMT";
                    }
                }
                defaultTimeZone = getTimeZone(zoneName);
            }
            timeZone = (TimeZone) defaultTimeZone.clone();
        }
        return timeZone;
    }

    public final String getDisplayName() {
        return getDisplayName(false, LONG, Locale.getDefault());
    }

    public final String getDisplayName(Locale locale) {
        return getDisplayName(false, LONG, locale);
    }

    public final String getDisplayName(boolean daylightTime, int style) {
        return getDisplayName(daylightTime, style, Locale.getDefault());
    }

    public String getDisplayName(boolean daylightTime, int style, Locale locale) {
        if (style == 0 || style == LONG) {
            String result = TimeZoneNames.getDisplayName(TimeZoneNames.getZoneStrings(locale), getID(), daylightTime, style);
            if (result != null) {
                return result;
            }
            int offsetMillis = getRawOffset();
            if (daylightTime) {
                offsetMillis += getDSTSavings();
            }
            return createGmtOffsetString(true, true, offsetMillis);
        }
        throw new IllegalArgumentException("Bad style: " + style);
    }

    public static String createGmtOffsetString(boolean includeGmt, boolean includeMinuteSeparator, int offsetMillis) {
        int offsetMinutes = offsetMillis / Grego.MILLIS_PER_MINUTE;
        char sign = '+';
        if (offsetMinutes < 0) {
            sign = '-';
            offsetMinutes = -offsetMinutes;
        }
        StringBuilder builder = new StringBuilder(9);
        if (includeGmt) {
            builder.append("GMT");
        }
        builder.append(sign);
        appendNumber(builder, 2, offsetMinutes / 60);
        if (includeMinuteSeparator) {
            builder.append(':');
        }
        appendNumber(builder, 2, offsetMinutes % 60);
        return builder.toString();
    }

    private static void appendNumber(StringBuilder builder, int count, int value) {
        String string = Integer.toString(value);
        for (int i = SHORT; i < count - string.length(); i += LONG) {
            builder.append('0');
        }
        builder.append(string);
    }

    public String getID() {
        return this.ID;
    }

    public int getDSTSavings() {
        return useDaylightTime() ? Grego.MILLIS_PER_HOUR : SHORT;
    }

    public int getOffset(long time) {
        if (inDaylightTime(new Date(time))) {
            return getRawOffset() + getDSTSavings();
        }
        return getRawOffset();
    }

    public static synchronized TimeZone getTimeZone(String id) {
        TimeZone timeZone;
        synchronized (TimeZone.class) {
            if (id == null) {
                throw new NullPointerException("id == null");
            }
            if (id.length() == 3) {
                if (id.equals("GMT")) {
                    timeZone = (TimeZone) GMT.clone();
                } else if (id.equals("UTC")) {
                    timeZone = (TimeZone) UTC.clone();
                }
            }
            TimeZone zone = null;
            try {
                zone = ZoneInfoDB.getInstance().makeTimeZone(id);
            } catch (IOException e) {
            }
            if (zone == null) {
                if (id.length() > 3 && id.startsWith("GMT")) {
                    zone = getCustomTimeZone(id);
                }
            }
            if (zone == null) {
                zone = (TimeZone) GMT.clone();
            }
            timeZone = zone;
        }
        return timeZone;
    }

    private static TimeZone getCustomTimeZone(String id) {
        Matcher m = CUSTOM_ZONE_ID_PATTERN.matcher(id);
        if (!m.matches()) {
            return null;
        }
        int minute = SHORT;
        try {
            int hour = Integer.parseInt(m.group(LONG));
            if (m.group(3) != null) {
                minute = Integer.parseInt(m.group(3));
            }
            if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                return null;
            }
            int raw = (Grego.MILLIS_PER_HOUR * hour) + (Grego.MILLIS_PER_MINUTE * minute);
            if (id.charAt(3) == '-') {
                raw = -raw;
            }
            return new SimpleTimeZone(raw, String.format("GMT%c%02d:%02d", Character.valueOf(id.charAt(3)), Integer.valueOf(hour), Integer.valueOf(minute)));
        } catch (Object impossible) {
            throw new AssertionError(impossible);
        }
    }

    public boolean hasSameRules(TimeZone timeZone) {
        if (timeZone != null && getRawOffset() == timeZone.getRawOffset()) {
            return true;
        }
        return false;
    }

    public static synchronized void setDefault(TimeZone timeZone) {
        synchronized (TimeZone.class) {
            defaultTimeZone = timeZone != null ? (TimeZone) timeZone.clone() : null;
        }
    }

    public void setID(String id) {
        if (id == null) {
            throw new NullPointerException("id == null");
        }
        this.ID = id;
    }
}
