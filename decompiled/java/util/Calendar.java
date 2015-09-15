package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.text.DateFormatSymbols;
import libcore.icu.ICU;
import libcore.icu.LocaleData;

public abstract class Calendar implements Serializable, Cloneable, Comparable<Calendar> {
    public static final int ALL_STYLES = 0;
    public static final int AM = 0;
    public static final int AM_PM = 9;
    public static final int APRIL = 3;
    public static final int AUGUST = 7;
    public static final int DATE = 5;
    public static final int DAY_OF_MONTH = 5;
    public static final int DAY_OF_WEEK = 7;
    public static final int DAY_OF_WEEK_IN_MONTH = 8;
    public static final int DAY_OF_YEAR = 6;
    public static final int DECEMBER = 11;
    public static final int DST_OFFSET = 16;
    public static final int ERA = 0;
    public static final int FEBRUARY = 1;
    public static final int FIELD_COUNT = 17;
    private static final String[] FIELD_NAMES;
    public static final int FRIDAY = 6;
    public static final int HOUR = 10;
    public static final int HOUR_OF_DAY = 11;
    public static final int JANUARY = 0;
    public static final int JULY = 6;
    public static final int JUNE = 5;
    public static final int LONG = 2;
    public static final int MARCH = 2;
    public static final int MAY = 4;
    public static final int MILLISECOND = 14;
    public static final int MINUTE = 12;
    public static final int MONDAY = 2;
    public static final int MONTH = 2;
    public static final int NOVEMBER = 10;
    public static final int OCTOBER = 9;
    public static final int PM = 1;
    public static final int SATURDAY = 7;
    public static final int SECOND = 13;
    public static final int SEPTEMBER = 8;
    public static final int SHORT = 1;
    public static final int SUNDAY = 1;
    public static final int THURSDAY = 5;
    public static final int TUESDAY = 3;
    public static final int UNDECIMBER = 12;
    public static final int WEDNESDAY = 4;
    public static final int WEEK_OF_MONTH = 4;
    public static final int WEEK_OF_YEAR = 3;
    public static final int YEAR = 1;
    public static final int ZONE_OFFSET = 15;
    private static final ObjectStreamField[] serialPersistentFields;
    private static final long serialVersionUID = -1807547505821590642L;
    protected boolean areFieldsSet;
    protected int[] fields;
    private int firstDayOfWeek;
    protected boolean[] isSet;
    protected boolean isTimeSet;
    transient int lastDateFieldSet;
    transient int lastTimeFieldSet;
    private boolean lenient;
    private int minimalDaysInFirstWeek;
    protected long time;
    private TimeZone zone;

    public abstract void add(int i, int i2);

    protected abstract void computeFields();

    protected abstract void computeTime();

    public abstract int getGreatestMinimum(int i);

    public abstract int getLeastMaximum(int i);

    public abstract int getMaximum(int i);

    public abstract int getMinimum(int i);

    public abstract void roll(int i, boolean z);

    static {
        String[] strArr = new String[FIELD_COUNT];
        strArr[JANUARY] = "ERA";
        strArr[YEAR] = "YEAR";
        strArr[MONTH] = "MONTH";
        strArr[WEEK_OF_YEAR] = "WEEK_OF_YEAR";
        strArr[WEEK_OF_MONTH] = "WEEK_OF_MONTH";
        strArr[THURSDAY] = "DAY_OF_MONTH";
        strArr[JULY] = "DAY_OF_YEAR";
        strArr[SATURDAY] = "DAY_OF_WEEK";
        strArr[SEPTEMBER] = "DAY_OF_WEEK_IN_MONTH";
        strArr[OCTOBER] = "AM_PM";
        strArr[NOVEMBER] = "HOUR";
        strArr[HOUR_OF_DAY] = "HOUR_OF_DAY";
        strArr[UNDECIMBER] = "MINUTE";
        strArr[SECOND] = "SECOND";
        strArr[MILLISECOND] = "MILLISECOND";
        strArr[ZONE_OFFSET] = "ZONE_OFFSET";
        strArr[DST_OFFSET] = "DST_OFFSET";
        FIELD_NAMES = strArr;
        ObjectStreamField[] objectStreamFieldArr = new ObjectStreamField[HOUR_OF_DAY];
        objectStreamFieldArr[JANUARY] = new ObjectStreamField("areFieldsSet", Boolean.TYPE);
        objectStreamFieldArr[YEAR] = new ObjectStreamField("fields", int[].class);
        objectStreamFieldArr[MONTH] = new ObjectStreamField("firstDayOfWeek", Integer.TYPE);
        objectStreamFieldArr[WEEK_OF_YEAR] = new ObjectStreamField("isSet", boolean[].class);
        objectStreamFieldArr[WEEK_OF_MONTH] = new ObjectStreamField("isTimeSet", Boolean.TYPE);
        objectStreamFieldArr[THURSDAY] = new ObjectStreamField("lenient", Boolean.TYPE);
        objectStreamFieldArr[JULY] = new ObjectStreamField("minimalDaysInFirstWeek", Integer.TYPE);
        objectStreamFieldArr[SATURDAY] = new ObjectStreamField("nextStamp", Integer.TYPE);
        objectStreamFieldArr[SEPTEMBER] = new ObjectStreamField("serialVersionOnStream", Integer.TYPE);
        objectStreamFieldArr[OCTOBER] = new ObjectStreamField("time", Long.TYPE);
        objectStreamFieldArr[NOVEMBER] = new ObjectStreamField("zone", TimeZone.class);
        serialPersistentFields = objectStreamFieldArr;
    }

    protected Calendar() {
        this(TimeZone.getDefault(), Locale.getDefault());
    }

    Calendar(TimeZone timezone) {
        this.fields = new int[FIELD_COUNT];
        this.isSet = new boolean[FIELD_COUNT];
        this.isTimeSet = false;
        this.areFieldsSet = false;
        setLenient(true);
        setTimeZone(timezone);
    }

    protected Calendar(TimeZone timezone, Locale locale) {
        this(timezone);
        LocaleData localeData = LocaleData.get(LocaleData.mapInvalidAndNullLocales(locale));
        setFirstDayOfWeek(localeData.firstDayOfWeek.intValue());
        setMinimalDaysInFirstWeek(localeData.minimalDaysInFirstWeek.intValue());
    }

    public boolean after(Object calendar) {
        if ((calendar instanceof Calendar) && getTimeInMillis() > ((Calendar) calendar).getTimeInMillis()) {
            return true;
        }
        return false;
    }

    public boolean before(Object calendar) {
        if ((calendar instanceof Calendar) && getTimeInMillis() < ((Calendar) calendar).getTimeInMillis()) {
            return true;
        }
        return false;
    }

    public final void clear() {
        for (int i = JANUARY; i < FIELD_COUNT; i += YEAR) {
            this.fields[i] = JANUARY;
            this.isSet[i] = false;
        }
        this.isTimeSet = false;
        this.areFieldsSet = false;
    }

    public final void clear(int field) {
        this.fields[field] = JANUARY;
        this.isSet[field] = false;
        this.isTimeSet = false;
        this.areFieldsSet = false;
    }

    public Object clone() {
        try {
            Calendar clone = (Calendar) super.clone();
            clone.fields = (int[]) this.fields.clone();
            clone.isSet = (boolean[]) this.isSet.clone();
            clone.zone = (TimeZone) this.zone.clone();
            return clone;
        } catch (Object e) {
            throw new AssertionError(e);
        }
    }

    protected void complete() {
        if (!this.isTimeSet) {
            computeTime();
            this.isTimeSet = true;
        }
        if (!this.areFieldsSet) {
            computeFields();
            this.areFieldsSet = true;
        }
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Calendar)) {
            return false;
        }
        Calendar cal = (Calendar) object;
        if (getTimeInMillis() == cal.getTimeInMillis() && isLenient() == cal.isLenient() && getFirstDayOfWeek() == cal.getFirstDayOfWeek() && getMinimalDaysInFirstWeek() == cal.getMinimalDaysInFirstWeek() && getTimeZone().equals(cal.getTimeZone())) {
            return true;
        }
        return false;
    }

    public int get(int field) {
        complete();
        return this.fields[field];
    }

    public int getActualMaximum(int field) {
        int maximum = getMaximum(field);
        int next = getLeastMaximum(field);
        if (maximum == next) {
            return next;
        }
        int value;
        complete();
        long orgTime = this.time;
        set(field, next);
        do {
            value = next;
            roll(field, true);
            next = get(field);
        } while (next > value);
        this.time = orgTime;
        this.areFieldsSet = false;
        return value;
    }

    public int getActualMinimum(int field) {
        int minimum = getMinimum(field);
        int next = getGreatestMinimum(field);
        if (minimum == next) {
            return next;
        }
        int value;
        complete();
        long orgTime = this.time;
        set(field, next);
        do {
            value = next;
            roll(field, false);
            next = get(field);
        } while (next < value);
        this.time = orgTime;
        this.areFieldsSet = false;
        return value;
    }

    public static synchronized Locale[] getAvailableLocales() {
        Locale[] availableCalendarLocales;
        synchronized (Calendar.class) {
            availableCalendarLocales = ICU.getAvailableCalendarLocales();
        }
        return availableCalendarLocales;
    }

    public int getFirstDayOfWeek() {
        return this.firstDayOfWeek;
    }

    public static synchronized Calendar getInstance() {
        Calendar gregorianCalendar;
        synchronized (Calendar.class) {
            gregorianCalendar = new GregorianCalendar();
        }
        return gregorianCalendar;
    }

    public static synchronized Calendar getInstance(Locale locale) {
        Calendar gregorianCalendar;
        synchronized (Calendar.class) {
            gregorianCalendar = new GregorianCalendar(locale);
        }
        return gregorianCalendar;
    }

    public static synchronized Calendar getInstance(TimeZone timezone) {
        Calendar gregorianCalendar;
        synchronized (Calendar.class) {
            gregorianCalendar = new GregorianCalendar(timezone);
        }
        return gregorianCalendar;
    }

    public static synchronized Calendar getInstance(TimeZone timezone, Locale locale) {
        Calendar gregorianCalendar;
        synchronized (Calendar.class) {
            gregorianCalendar = new GregorianCalendar(timezone, locale);
        }
        return gregorianCalendar;
    }

    public int getMinimalDaysInFirstWeek() {
        return this.minimalDaysInFirstWeek;
    }

    public final Date getTime() {
        return new Date(getTimeInMillis());
    }

    public long getTimeInMillis() {
        if (!this.isTimeSet) {
            computeTime();
            this.isTimeSet = true;
        }
        return this.time;
    }

    public TimeZone getTimeZone() {
        return this.zone;
    }

    public int hashCode() {
        return (((isLenient() ? 1237 : 1231) + getFirstDayOfWeek()) + getMinimalDaysInFirstWeek()) + getTimeZone().hashCode();
    }

    protected final int internalGet(int field) {
        return this.fields[field];
    }

    public boolean isLenient() {
        return this.lenient;
    }

    public final boolean isSet(int field) {
        return this.isSet[field];
    }

    public void roll(int field, int value) {
        boolean increment = value >= 0;
        int count = increment ? value : -value;
        for (int i = JANUARY; i < count; i += YEAR) {
            roll(field, increment);
        }
    }

    public void set(int field, int value) {
        this.fields[field] = value;
        this.isSet[field] = true;
        this.isTimeSet = false;
        this.areFieldsSet = false;
        if (field > MONTH && field < OCTOBER) {
            this.lastDateFieldSet = field;
        }
        if (field == NOVEMBER || field == HOUR_OF_DAY) {
            this.lastTimeFieldSet = field;
        }
        if (field == OCTOBER) {
            this.lastTimeFieldSet = NOVEMBER;
        }
    }

    public final void set(int year, int month, int day) {
        set(YEAR, year);
        set(MONTH, month);
        set(THURSDAY, day);
    }

    public final void set(int year, int month, int day, int hourOfDay, int minute) {
        set(year, month, day);
        set(HOUR_OF_DAY, hourOfDay);
        set(UNDECIMBER, minute);
    }

    public final void set(int year, int month, int day, int hourOfDay, int minute, int second) {
        set(year, month, day, hourOfDay, minute);
        set(SECOND, second);
    }

    public void setFirstDayOfWeek(int value) {
        this.firstDayOfWeek = value;
    }

    public void setLenient(boolean value) {
        this.lenient = value;
    }

    public void setMinimalDaysInFirstWeek(int value) {
        this.minimalDaysInFirstWeek = value;
    }

    public final void setTime(Date date) {
        setTimeInMillis(date.getTime());
    }

    public void setTimeInMillis(long milliseconds) {
        if (!this.isTimeSet || !this.areFieldsSet || this.time != milliseconds) {
            this.time = milliseconds;
            this.isTimeSet = true;
            this.areFieldsSet = false;
            complete();
        }
    }

    public void setTimeZone(TimeZone timezone) {
        this.zone = timezone;
        this.areFieldsSet = false;
    }

    public String toString() {
        StringBuilder result = new StringBuilder(getClass().getName() + "[time=" + (this.isTimeSet ? String.valueOf(this.time) : "?") + ",areFieldsSet=" + this.areFieldsSet + ",lenient=" + this.lenient + ",zone=" + this.zone.getID() + ",firstDayOfWeek=" + this.firstDayOfWeek + ",minimalDaysInFirstWeek=" + this.minimalDaysInFirstWeek);
        for (int i = JANUARY; i < FIELD_COUNT; i += YEAR) {
            result.append(',');
            result.append(FIELD_NAMES[i]);
            result.append('=');
            if (this.isSet[i]) {
                result.append(this.fields[i]);
            } else {
                result.append('?');
            }
        }
        result.append(']');
        return result.toString();
    }

    public int compareTo(Calendar anotherCalendar) {
        if (anotherCalendar == null) {
            throw new NullPointerException("anotherCalendar == null");
        }
        long timeInMillis = getTimeInMillis();
        long anotherTimeInMillis = anotherCalendar.getTimeInMillis();
        if (timeInMillis > anotherTimeInMillis) {
            return YEAR;
        }
        if (timeInMillis == anotherTimeInMillis) {
            return JANUARY;
        }
        return -1;
    }

    public String getDisplayName(int field, int style, Locale locale) {
        if (style == 0) {
            style = YEAR;
        }
        String[] array = getDisplayNameArray(field, style, locale);
        return array != null ? array[get(field)] : null;
    }

    private String[] getDisplayNameArray(int field, int style, Locale locale) {
        if (field < 0 || field >= FIELD_COUNT) {
            throw new IllegalArgumentException("bad field " + field);
        }
        checkStyle(style);
        DateFormatSymbols dfs = DateFormatSymbols.getInstance(locale);
        switch (field) {
            case JANUARY /*0*/:
                return dfs.getEras();
            case MONTH /*2*/:
                return style == MONTH ? dfs.getMonths() : dfs.getShortMonths();
            case SATURDAY /*7*/:
                return style == MONTH ? dfs.getWeekdays() : dfs.getShortWeekdays();
            case OCTOBER /*9*/:
                return dfs.getAmPmStrings();
            default:
                return null;
        }
    }

    private static void checkStyle(int style) {
        if (style != 0 && style != YEAR && style != MONTH) {
            throw new IllegalArgumentException("bad style " + style);
        }
    }

    public Map<String, Integer> getDisplayNames(int field, int style, Locale locale) {
        checkStyle(style);
        complete();
        Map<String, Integer> result = new HashMap();
        if (style == YEAR || style == 0) {
            insertValuesInMap(result, getDisplayNameArray(field, YEAR, locale));
        }
        if (style == MONTH || style == 0) {
            insertValuesInMap(result, getDisplayNameArray(field, MONTH, locale));
        }
        return result.isEmpty() ? null : result;
    }

    private static void insertValuesInMap(Map<String, Integer> map, String[] values) {
        if (values != null) {
            int i = JANUARY;
            while (i < values.length) {
                if (!(values[i] == null || values[i].isEmpty())) {
                    map.put(values[i], Integer.valueOf(i));
                }
                i += YEAR;
            }
        }
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        complete();
        PutField putFields = stream.putFields();
        putFields.put("areFieldsSet", this.areFieldsSet);
        putFields.put("fields", this.fields);
        putFields.put("firstDayOfWeek", this.firstDayOfWeek);
        putFields.put("isSet", this.isSet);
        putFields.put("isTimeSet", this.isTimeSet);
        putFields.put("lenient", this.lenient);
        putFields.put("minimalDaysInFirstWeek", this.minimalDaysInFirstWeek);
        putFields.put("nextStamp", (int) MONTH);
        putFields.put("serialVersionOnStream", (int) YEAR);
        putFields.put("time", this.time);
        putFields.put("zone", this.zone);
        stream.writeFields();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        GetField readFields = stream.readFields();
        this.areFieldsSet = readFields.get("areFieldsSet", false);
        this.fields = (int[]) readFields.get("fields", null);
        this.firstDayOfWeek = readFields.get("firstDayOfWeek", (int) YEAR);
        this.isSet = (boolean[]) readFields.get("isSet", null);
        this.isTimeSet = readFields.get("isTimeSet", false);
        this.lenient = readFields.get("lenient", true);
        this.minimalDaysInFirstWeek = readFields.get("minimalDaysInFirstWeek", (int) YEAR);
        this.time = readFields.get("time", 0);
        this.zone = (TimeZone) readFields.get("zone", null);
    }
}
