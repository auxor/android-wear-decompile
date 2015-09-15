package java.text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;
import libcore.icu.ICU;
import libcore.icu.LocaleData;
import libcore.icu.TimeZoneNames;

public class DateFormatSymbols implements Serializable, Cloneable {
    private static final long serialVersionUID = -5987973545549424702L;
    String[] ampms;
    String[] eras;
    private String localPatternChars;
    private final Locale locale;
    transient LocaleData localeData;
    String[] months;
    String[] shortMonths;
    String[] shortWeekdays;
    String[] weekdays;
    private String[][] zoneStrings;

    synchronized String[][] internalZoneStrings() {
        if (this.zoneStrings == null) {
            this.zoneStrings = TimeZoneNames.getZoneStrings(this.locale);
        }
        return this.zoneStrings;
    }

    public DateFormatSymbols() {
        this(Locale.getDefault());
    }

    public DateFormatSymbols(Locale locale) {
        this.locale = LocaleData.mapInvalidAndNullLocales(locale);
        this.localPatternChars = "GyMdkHmsSEDFwWahKzZLc";
        this.localeData = LocaleData.get(locale);
        this.ampms = this.localeData.amPm;
        this.eras = this.localeData.eras;
        this.months = this.localeData.longMonthNames;
        this.shortMonths = this.localeData.shortMonthNames;
        this.weekdays = this.localeData.longWeekdayNames;
        this.shortWeekdays = this.localeData.shortWeekdayNames;
    }

    public static final DateFormatSymbols getInstance() {
        return getInstance(Locale.getDefault());
    }

    public static final DateFormatSymbols getInstance(Locale locale) {
        if (locale != null) {
            return new DateFormatSymbols(locale);
        }
        throw new NullPointerException("locale == null");
    }

    public static Locale[] getAvailableLocales() {
        return ICU.getAvailableDateFormatSymbolsLocales();
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        Locale locale = this.locale;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        this.localeData = LocaleData.get(locale);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        internalZoneStrings();
        oos.defaultWriteObject();
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof DateFormatSymbols)) {
            return false;
        }
        DateFormatSymbols rhs = (DateFormatSymbols) object;
        if (this.localPatternChars.equals(rhs.localPatternChars) && Arrays.equals(this.ampms, rhs.ampms) && Arrays.equals(this.eras, rhs.eras) && Arrays.equals(this.months, rhs.months) && Arrays.equals(this.shortMonths, rhs.shortMonths) && Arrays.equals(this.shortWeekdays, rhs.shortWeekdays) && Arrays.equals(this.weekdays, rhs.weekdays) && timeZoneStringsEqual(this, rhs)) {
            return true;
        }
        return false;
    }

    private static boolean timeZoneStringsEqual(DateFormatSymbols lhs, DateFormatSymbols rhs) {
        if (lhs.zoneStrings == null && rhs.zoneStrings == null && lhs.locale.equals(rhs.locale)) {
            return true;
        }
        return Arrays.deepEquals(lhs.internalZoneStrings(), rhs.internalZoneStrings());
    }

    public String toString() {
        return getClass().getName() + "[amPmStrings=" + Arrays.toString(this.ampms) + ",eras=" + Arrays.toString(this.eras) + ",localPatternChars=" + this.localPatternChars + ",months=" + Arrays.toString(this.months) + ",shortMonths=" + Arrays.toString(this.shortMonths) + ",shortWeekdays=" + Arrays.toString(this.shortWeekdays) + ",weekdays=" + Arrays.toString(this.weekdays) + ",zoneStrings=[" + Arrays.toString(internalZoneStrings()[0]) + "...]" + "]";
    }

    public String[] getAmPmStrings() {
        return (String[]) this.ampms.clone();
    }

    public String[] getEras() {
        return (String[]) this.eras.clone();
    }

    public String getLocalPatternChars() {
        return this.localPatternChars;
    }

    public String[] getMonths() {
        return (String[]) this.months.clone();
    }

    public String[] getShortMonths() {
        return (String[]) this.shortMonths.clone();
    }

    public String[] getShortWeekdays() {
        return (String[]) this.shortWeekdays.clone();
    }

    public String[] getWeekdays() {
        return (String[]) this.weekdays.clone();
    }

    public String[][] getZoneStrings() {
        String[][] result = clone2dStringArray(internalZoneStrings());
        for (String[] zone : result) {
            String id = zone[0];
            if (zone[1] == null) {
                zone[1] = TimeZone.getTimeZone(id).getDisplayName(false, 1, this.locale);
            }
            if (zone[2] == null) {
                zone[2] = TimeZone.getTimeZone(id).getDisplayName(false, 0, this.locale);
            }
            if (zone[3] == null) {
                zone[3] = TimeZone.getTimeZone(id).getDisplayName(true, 1, this.locale);
            }
            if (zone[4] == null) {
                zone[4] = TimeZone.getTimeZone(id).getDisplayName(true, 0, this.locale);
            }
        }
        return result;
    }

    private static String[][] clone2dStringArray(String[][] array) {
        String[][] result = new String[array.length][];
        for (int i = 0; i < array.length; i++) {
            result[i] = (String[]) array[i].clone();
        }
        return result;
    }

    public int hashCode() {
        String[][] zoneStrings = internalZoneStrings();
        int hashCode = this.localPatternChars.hashCode();
        for (String element : this.ampms) {
            hashCode += element.hashCode();
        }
        for (String element2 : this.eras) {
            hashCode += element2.hashCode();
        }
        for (String element22 : this.months) {
            hashCode += element22.hashCode();
        }
        for (String element222 : this.shortMonths) {
            hashCode += element222.hashCode();
        }
        for (String element2222 : this.shortWeekdays) {
            hashCode += element2222.hashCode();
        }
        for (String element22222 : this.weekdays) {
            hashCode += element22222.hashCode();
        }
        for (String[] element3 : zoneStrings) {
            for (int j = 0; j < element3.length; j++) {
                if (element3[j] != null) {
                    hashCode += element3[j].hashCode();
                }
            }
        }
        return hashCode;
    }

    public void setAmPmStrings(String[] data) {
        this.ampms = (String[]) data.clone();
    }

    public void setEras(String[] data) {
        this.eras = (String[]) data.clone();
    }

    public void setLocalPatternChars(String data) {
        if (data == null) {
            throw new NullPointerException("data == null");
        }
        this.localPatternChars = data;
    }

    public void setMonths(String[] data) {
        this.months = (String[]) data.clone();
    }

    public void setShortMonths(String[] data) {
        this.shortMonths = (String[]) data.clone();
    }

    public void setShortWeekdays(String[] data) {
        this.shortWeekdays = (String[]) data.clone();
    }

    public void setWeekdays(String[] data) {
        this.weekdays = (String[]) data.clone();
    }

    public void setZoneStrings(String[][] zoneStrings) {
        if (zoneStrings == null) {
            throw new NullPointerException("zoneStrings == null");
        }
        for (Object[] row : zoneStrings) {
            if (row.length < 5) {
                throw new IllegalArgumentException(Arrays.toString(row) + ".length < 5");
            }
        }
        this.zoneStrings = clone2dStringArray(zoneStrings);
    }

    String getTimeZoneDisplayName(TimeZone tz, boolean daylight, int style) {
        if (style == 0 || style == 1) {
            return TimeZoneNames.getDisplayName(internalZoneStrings(), tz.getID(), daylight, style);
        }
        throw new IllegalArgumentException("Bad style: " + style);
    }
}
