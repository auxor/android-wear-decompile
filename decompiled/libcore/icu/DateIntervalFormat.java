package libcore.icu;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import libcore.util.BasicLruCache;

public final class DateIntervalFormat {
    private static final FormatterCache CACHED_FORMATTERS;
    private static final int DAY_IN_MS = 86400000;
    private static final int EPOCH_JULIAN_DAY = 2440588;
    public static final int FORMAT_12HOUR = 64;
    public static final int FORMAT_24HOUR = 128;
    public static final int FORMAT_ABBREV_ALL = 524288;
    public static final int FORMAT_ABBREV_MONTH = 65536;
    public static final int FORMAT_ABBREV_TIME = 16384;
    public static final int FORMAT_ABBREV_WEEKDAY = 32768;
    public static final int FORMAT_NO_MONTH_DAY = 32;
    public static final int FORMAT_NO_YEAR = 8;
    public static final int FORMAT_NUMERIC_DATE = 131072;
    public static final int FORMAT_SHOW_DATE = 16;
    public static final int FORMAT_SHOW_TIME = 1;
    public static final int FORMAT_SHOW_WEEKDAY = 2;
    public static final int FORMAT_SHOW_YEAR = 4;
    public static final int FORMAT_UTC = 8192;

    static class FormatterCache extends BasicLruCache<String, Long> {
        FormatterCache() {
            super(DateIntervalFormat.FORMAT_NO_YEAR);
        }

        protected void entryEvicted(String key, Long value) {
            DateIntervalFormat.destroyDateIntervalFormat(value.longValue());
        }
    }

    private static native long createDateIntervalFormat(String str, String str2, String str3);

    private static native void destroyDateIntervalFormat(long j);

    private static native String formatDateInterval(long j, long j2, long j3);

    static {
        CACHED_FORMATTERS = new FormatterCache();
    }

    private DateIntervalFormat() {
    }

    public static String formatDateRange(long startMs, long endMs, int flags, String olsonId) {
        if ((flags & FORMAT_UTC) != 0) {
            olsonId = "UTC";
        }
        return formatDateRange(Locale.getDefault(), olsonId != null ? TimeZone.getTimeZone(olsonId) : TimeZone.getDefault(), startMs, endMs, flags);
    }

    public static String formatDateRange(Locale locale, TimeZone tz, long startMs, long endMs, int flags) {
        Calendar endCalendar;
        String formatDateInterval;
        Calendar startCalendar = Calendar.getInstance(tz);
        startCalendar.setTimeInMillis(startMs);
        if (startMs == endMs) {
            endCalendar = startCalendar;
        } else {
            endCalendar = Calendar.getInstance(tz);
            endCalendar.setTimeInMillis(endMs);
        }
        boolean endsAtMidnight = isMidnight(endCalendar);
        if (startMs != endMs && endsAtMidnight && ((flags & FORMAT_SHOW_TIME) == 0 || dayDistance(startCalendar, endCalendar) <= FORMAT_SHOW_TIME)) {
            endCalendar.roll(5, false);
            endMs -= 86400000;
        }
        String skeleton = toSkeleton(startCalendar, endCalendar, flags);
        synchronized (CACHED_FORMATTERS) {
            formatDateInterval = formatDateInterval(getFormatter(skeleton, locale.toString(), tz.getID()), startMs, endMs);
        }
        return formatDateInterval;
    }

    private static long getFormatter(String skeleton, String localeName, String tzName) {
        String key = skeleton + "\t" + localeName + "\t" + tzName;
        Long formatter = (Long) CACHED_FORMATTERS.get(key);
        if (formatter != null) {
            return formatter.longValue();
        }
        long address = createDateIntervalFormat(skeleton, localeName, tzName);
        CACHED_FORMATTERS.put(key, Long.valueOf(address));
        return address;
    }

    private static String toSkeleton(Calendar startCalendar, Calendar endCalendar, int flags) {
        if ((FORMAT_ABBREV_ALL & flags) != 0) {
            flags |= 114688;
        }
        String monthPart = "MMMM";
        if ((FORMAT_NUMERIC_DATE & flags) != 0) {
            monthPart = "M";
        } else if ((FORMAT_ABBREV_MONTH & flags) != 0) {
            monthPart = "MMM";
        }
        String weekPart = "EEEE";
        if ((FORMAT_ABBREV_WEEKDAY & flags) != 0) {
            weekPart = "EEE";
        }
        String timePart = "j";
        if ((flags & FORMAT_24HOUR) != 0) {
            timePart = "H";
        } else if ((flags & FORMAT_12HOUR) != 0) {
            timePart = "h";
        }
        if ((flags & FORMAT_ABBREV_TIME) == 0 || (flags & FORMAT_24HOUR) != 0) {
            timePart = timePart + "m";
        } else if (!(onTheHour(startCalendar) && onTheHour(endCalendar))) {
            timePart = timePart + "m";
        }
        if (fallOnDifferentDates(startCalendar, endCalendar)) {
            flags |= FORMAT_SHOW_DATE;
        }
        if (fallInSameMonth(startCalendar, endCalendar) && (flags & FORMAT_NO_MONTH_DAY) != 0) {
            flags = (flags & -3) & -2;
        }
        if ((flags & 19) == 0) {
            flags |= FORMAT_SHOW_DATE;
        }
        if ((flags & FORMAT_SHOW_DATE) != 0 && (flags & FORMAT_SHOW_YEAR) == 0 && (flags & FORMAT_NO_YEAR) == 0 && !(fallInSameYear(startCalendar, endCalendar) && isThisYear(startCalendar))) {
            flags |= FORMAT_SHOW_YEAR;
        }
        StringBuilder builder = new StringBuilder();
        if ((flags & 48) != 0) {
            if ((flags & FORMAT_SHOW_YEAR) != 0) {
                builder.append("y");
            }
            builder.append(monthPart);
            if ((flags & FORMAT_NO_MONTH_DAY) == 0) {
                builder.append("d");
            }
        }
        if ((flags & FORMAT_SHOW_WEEKDAY) != 0) {
            builder.append(weekPart);
        }
        if ((flags & FORMAT_SHOW_TIME) != 0) {
            builder.append(timePart);
        }
        return builder.toString();
    }

    private static boolean isMidnight(Calendar c) {
        return c.get(11) == 0 && c.get(12) == 0 && c.get(13) == 0 && c.get(14) == 0;
    }

    private static boolean onTheHour(Calendar c) {
        return c.get(12) == 0 && c.get(13) == 0;
    }

    private static boolean fallOnDifferentDates(Calendar c1, Calendar c2) {
        return (c1.get(FORMAT_SHOW_TIME) == c2.get(FORMAT_SHOW_TIME) && c1.get(FORMAT_SHOW_WEEKDAY) == c2.get(FORMAT_SHOW_WEEKDAY) && c1.get(5) == c2.get(5)) ? false : true;
    }

    private static boolean fallInSameMonth(Calendar c1, Calendar c2) {
        return c1.get(FORMAT_SHOW_WEEKDAY) == c2.get(FORMAT_SHOW_WEEKDAY);
    }

    private static boolean fallInSameYear(Calendar c1, Calendar c2) {
        return c1.get(FORMAT_SHOW_TIME) == c2.get(FORMAT_SHOW_TIME);
    }

    private static boolean isThisYear(Calendar c) {
        if (c.get(FORMAT_SHOW_TIME) == Calendar.getInstance(c.getTimeZone()).get(FORMAT_SHOW_TIME)) {
            return true;
        }
        return false;
    }

    private static int dayDistance(Calendar c1, Calendar c2) {
        return julianDay(c2) - julianDay(c1);
    }

    private static int julianDay(Calendar c) {
        return ((int) (((c.getTimeInMillis() + ((long) c.get(15))) + ((long) c.get(FORMAT_SHOW_DATE))) / 86400000)) + EPOCH_JULIAN_DAY;
    }
}
