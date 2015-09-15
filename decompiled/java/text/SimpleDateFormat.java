package java.text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.text.Format.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import libcore.icu.ICU;
import libcore.icu.LocaleData;
import org.apache.harmony.security.asn1.ASN1UTCTime;
import org.w3c.dom.traversal.NodeFilter;
import org.xmlpull.v1.XmlPullParser;

public class SimpleDateFormat extends DateFormat {
    static final String PATTERN_CHARS = "GyMdkHmsSEDFwWahKzZLc";
    private static final int RFC_822_TIMEZONE_FIELD = 18;
    private static final int STAND_ALONE_DAY_OF_WEEK_FIELD = 20;
    private static final int STAND_ALONE_MONTH_FIELD = 19;
    private static final ObjectStreamField[] serialPersistentFields;
    private static final long serialVersionUID = 4774881970558875024L;
    private transient int creationYear;
    private Date defaultCenturyStart;
    private DateFormatSymbols formatData;
    private String pattern;

    public SimpleDateFormat() {
        this(Locale.getDefault());
        this.pattern = defaultPattern();
        this.formatData = new DateFormatSymbols(Locale.getDefault());
    }

    public SimpleDateFormat(String pattern) {
        this(pattern, Locale.getDefault());
    }

    private void validatePattern(String template) {
        boolean quote = false;
        int last = -1;
        int count = 0;
        int patternLength = template.length();
        for (int i = 0; i < patternLength; i++) {
            int next = template.charAt(i);
            if (next == 39) {
                if (count > 0) {
                    validatePatternCharacter((char) last);
                    count = 0;
                }
                if (last == next) {
                    last = -1;
                } else {
                    last = next;
                }
                if (quote) {
                    quote = false;
                } else {
                    quote = true;
                }
            } else if (quote || (last != next && ((next < 97 || next > 122) && (next < 65 || next > 90)))) {
                if (count > 0) {
                    validatePatternCharacter((char) last);
                    count = 0;
                }
                last = -1;
            } else if (last == next) {
                count++;
            } else {
                if (count > 0) {
                    validatePatternCharacter((char) last);
                }
                last = next;
                count = 1;
            }
        }
        if (count > 0) {
            validatePatternCharacter((char) last);
        }
        if (quote) {
            throw new IllegalArgumentException("Unterminated quote");
        }
    }

    private void validatePatternCharacter(char format) {
        if (PATTERN_CHARS.indexOf((int) format) == -1) {
            throw new IllegalArgumentException("Unknown pattern character '" + format + "'");
        }
    }

    public SimpleDateFormat(String template, DateFormatSymbols value) {
        this(Locale.getDefault());
        validatePattern(template);
        this.pattern = template;
        this.formatData = (DateFormatSymbols) value.clone();
    }

    public SimpleDateFormat(String template, Locale locale) {
        this(locale);
        validatePattern(template);
        this.pattern = template;
        this.formatData = new DateFormatSymbols(locale);
    }

    private SimpleDateFormat(Locale locale) {
        this.numberFormat = NumberFormat.getInstance(locale);
        this.numberFormat.setParseIntegerOnly(true);
        this.numberFormat.setGroupingUsed(false);
        this.calendar = new GregorianCalendar(locale);
        this.calendar.add(1, -80);
        this.creationYear = this.calendar.get(1);
        this.defaultCenturyStart = this.calendar.getTime();
    }

    public void applyLocalizedPattern(String template) {
        this.pattern = convertPattern(template, this.formatData.getLocalPatternChars(), PATTERN_CHARS, true);
    }

    public void applyPattern(String template) {
        validatePattern(template);
        this.pattern = template;
    }

    public Object clone() {
        SimpleDateFormat clone = (SimpleDateFormat) super.clone();
        clone.formatData = (DateFormatSymbols) this.formatData.clone();
        clone.defaultCenturyStart = new Date(this.defaultCenturyStart.getTime());
        return clone;
    }

    private static String defaultPattern() {
        LocaleData localeData = LocaleData.get(Locale.getDefault());
        return localeData.getDateFormat(3) + " " + localeData.getTimeFormat(3);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof SimpleDateFormat)) {
            return false;
        }
        SimpleDateFormat simple = (SimpleDateFormat) object;
        if (super.equals(object) && this.pattern.equals(simple.pattern) && this.formatData.equals(simple.formatData)) {
            return true;
        }
        return false;
    }

    public AttributedCharacterIterator formatToCharacterIterator(Object object) {
        if (object == null) {
            throw new NullPointerException("object == null");
        } else if (object instanceof Date) {
            return formatToCharacterIteratorImpl((Date) object);
        } else {
            if (object instanceof Number) {
                return formatToCharacterIteratorImpl(new Date(((Number) object).longValue()));
            }
            throw new IllegalArgumentException("Bad class: " + object.getClass());
        }
    }

    private AttributedCharacterIterator formatToCharacterIteratorImpl(Date date) {
        StringBuffer buffer = new StringBuffer();
        ArrayList<FieldPosition> fields = new ArrayList();
        formatImpl(date, buffer, null, fields);
        AttributedString as = new AttributedString(buffer.toString());
        Iterator i$ = fields.iterator();
        while (i$.hasNext()) {
            FieldPosition pos = (FieldPosition) i$.next();
            Field attribute = pos.getFieldAttribute();
            as.addAttribute(attribute, attribute, pos.getBeginIndex(), pos.getEndIndex());
        }
        return as.getIterator();
    }

    private StringBuffer formatImpl(Date date, StringBuffer buffer, FieldPosition field, List<FieldPosition> fields) {
        boolean quote = false;
        int last = -1;
        int count = 0;
        this.calendar.setTime(date);
        if (field != null) {
            field.setBeginIndex(0);
            field.setEndIndex(0);
        }
        int patternLength = this.pattern.length();
        for (int i = 0; i < patternLength; i++) {
            int next = this.pattern.charAt(i);
            if (next == 39) {
                if (count > 0) {
                    append(buffer, field, fields, (char) last, count);
                    count = 0;
                }
                if (last == next) {
                    buffer.append('\'');
                    last = -1;
                } else {
                    last = next;
                }
                if (quote) {
                    quote = false;
                } else {
                    quote = true;
                }
            } else if (quote || (last != next && ((next < 97 || next > 122) && (next < 65 || next > 90)))) {
                if (count > 0) {
                    append(buffer, field, fields, (char) last, count);
                    count = 0;
                }
                last = -1;
                buffer.append((char) next);
            } else if (last == next) {
                count++;
            } else {
                if (count > 0) {
                    append(buffer, field, fields, (char) last, count);
                }
                last = next;
                count = 1;
            }
        }
        if (count > 0) {
            append(buffer, field, fields, (char) last, count);
        }
        return buffer;
    }

    private void append(StringBuffer buffer, FieldPosition position, List<FieldPosition> fields, char format, int count) {
        int field = -1;
        int index = PATTERN_CHARS.indexOf((int) format);
        if (index == -1) {
            throw new IllegalArgumentException("Unknown pattern character '" + format + "'");
        }
        int beginPosition = buffer.length();
        Field dateFormatField = null;
        int hour;
        switch (index) {
            case XmlPullParser.START_DOCUMENT /*0*/:
                dateFormatField = DateFormat.Field.ERA;
                buffer.append(this.formatData.eras[this.calendar.get(0)]);
                break;
            case NodeFilter.SHOW_ELEMENT /*1*/:
                dateFormatField = DateFormat.Field.YEAR;
                int year = this.calendar.get(1);
                if (count != 2) {
                    appendNumber(buffer, count, year);
                    break;
                } else {
                    appendNumber(buffer, 2, year % 100);
                    break;
                }
            case NodeFilter.SHOW_ATTRIBUTE /*2*/:
                dateFormatField = DateFormat.Field.MONTH;
                appendMonth(buffer, count, false);
                break;
            case XmlPullParser.END_TAG /*3*/:
                dateFormatField = DateFormat.Field.DAY_OF_MONTH;
                field = 5;
                break;
            case NodeFilter.SHOW_TEXT /*4*/:
                dateFormatField = DateFormat.Field.HOUR_OF_DAY1;
                hour = this.calendar.get(11);
                if (hour == 0) {
                    hour = 24;
                }
                appendNumber(buffer, count, hour);
                break;
            case XmlPullParser.CDSECT /*5*/:
                dateFormatField = DateFormat.Field.HOUR_OF_DAY0;
                field = 11;
                break;
            case XmlPullParser.ENTITY_REF /*6*/:
                dateFormatField = DateFormat.Field.MINUTE;
                field = 12;
                break;
            case XmlPullParser.IGNORABLE_WHITESPACE /*7*/:
                dateFormatField = DateFormat.Field.SECOND;
                field = 13;
                break;
            case NodeFilter.SHOW_CDATA_SECTION /*8*/:
                dateFormatField = DateFormat.Field.MILLISECOND;
                appendMilliseconds(buffer, count, this.calendar.get(14));
                break;
            case XmlPullParser.COMMENT /*9*/:
                dateFormatField = DateFormat.Field.DAY_OF_WEEK;
                appendDayOfWeek(buffer, count, false);
                break;
            case XmlPullParser.DOCDECL /*10*/:
                dateFormatField = DateFormat.Field.DAY_OF_YEAR;
                field = 6;
                break;
            case ASN1UTCTime.UTC_HM /*11*/:
                dateFormatField = DateFormat.Field.DAY_OF_WEEK_IN_MONTH;
                field = 8;
                break;
            case ICU.U_ILLEGAL_CHAR_FOUND /*12*/:
                dateFormatField = DateFormat.Field.WEEK_OF_YEAR;
                field = 3;
                break;
            case ASN1UTCTime.UTC_HMS /*13*/:
                dateFormatField = DateFormat.Field.WEEK_OF_MONTH;
                field = 4;
                break;
            case ZipConstants.LOCCRC /*14*/:
                dateFormatField = DateFormat.Field.AM_PM;
                buffer.append(this.formatData.ampms[this.calendar.get(9)]);
                break;
            case ASN1UTCTime.UTC_LOCAL_HM /*15*/:
                dateFormatField = DateFormat.Field.HOUR1;
                hour = this.calendar.get(10);
                if (hour == 0) {
                    hour = 12;
                }
                appendNumber(buffer, count, hour);
                break;
            case NodeFilter.SHOW_ENTITY_REFERENCE /*16*/:
                dateFormatField = DateFormat.Field.HOUR0;
                field = 10;
                break;
            case ASN1UTCTime.UTC_LOCAL_HMS /*17*/:
                dateFormatField = DateFormat.Field.TIME_ZONE;
                appendTimeZone(buffer, count, true);
                break;
            case RFC_822_TIMEZONE_FIELD /*18*/:
                dateFormatField = DateFormat.Field.TIME_ZONE;
                appendNumericTimeZone(buffer, count, false);
                break;
            case STAND_ALONE_MONTH_FIELD /*19*/:
                dateFormatField = DateFormat.Field.MONTH;
                appendMonth(buffer, count, true);
                break;
            case STAND_ALONE_DAY_OF_WEEK_FIELD /*20*/:
                dateFormatField = DateFormat.Field.DAY_OF_WEEK;
                appendDayOfWeek(buffer, count, true);
                break;
        }
        if (field != -1) {
            appendNumber(buffer, count, this.calendar.get(field));
        }
        if (fields != null) {
            position = new FieldPosition(dateFormatField);
            position.setBeginIndex(beginPosition);
            position.setEndIndex(buffer.length());
            fields.add(position);
        } else if ((position.getFieldAttribute() == dateFormatField || (position.getFieldAttribute() == null && position.getField() == index)) && position.getEndIndex() == 0) {
            position.setBeginIndex(beginPosition);
            position.setEndIndex(buffer.length());
        }
    }

    private void appendDayOfWeek(StringBuffer buffer, int count, boolean standAlone) {
        LocaleData ld = this.formatData.localeData;
        String[] days = count == 4 ? standAlone ? ld.longStandAloneWeekdayNames : this.formatData.weekdays : count == 5 ? standAlone ? ld.tinyStandAloneWeekdayNames : this.formatData.localeData.tinyWeekdayNames : standAlone ? ld.shortStandAloneWeekdayNames : this.formatData.shortWeekdays;
        buffer.append(days[this.calendar.get(7)]);
    }

    private void appendMonth(StringBuffer buffer, int count, boolean standAlone) {
        int month = this.calendar.get(2);
        if (count <= 2) {
            appendNumber(buffer, count, month + 1);
            return;
        }
        LocaleData ld = this.formatData.localeData;
        String[] months = count == 4 ? standAlone ? ld.longStandAloneMonthNames : this.formatData.months : count == 5 ? standAlone ? ld.tinyStandAloneMonthNames : ld.tinyMonthNames : standAlone ? ld.shortStandAloneMonthNames : this.formatData.shortMonths;
        buffer.append(months[month]);
    }

    private void appendTimeZone(StringBuffer buffer, int count, boolean generalTimeZone) {
        int style = 0;
        if (generalTimeZone) {
            boolean daylight;
            TimeZone tz = this.calendar.getTimeZone();
            if (this.calendar.get(16) != 0) {
                daylight = true;
            } else {
                daylight = false;
            }
            if (count >= 4) {
                style = 1;
            }
            String zoneString = this.formatData.getTimeZoneDisplayName(tz, daylight, style);
            if (zoneString != null) {
                buffer.append(zoneString);
                return;
            }
        }
        appendNumericTimeZone(buffer, count, generalTimeZone);
    }

    private void appendNumericTimeZone(StringBuffer buffer, int count, boolean generalTimeZone) {
        boolean includeGmt;
        boolean includeMinuteSeparator = false;
        int offsetMillis = this.calendar.get(15) + this.calendar.get(16);
        if (generalTimeZone || count == 4) {
            includeGmt = true;
        } else {
            includeGmt = false;
        }
        if (generalTimeZone || count >= 4) {
            includeMinuteSeparator = true;
        }
        buffer.append(TimeZone.createGmtOffsetString(includeGmt, includeMinuteSeparator, offsetMillis));
    }

    private void appendMilliseconds(StringBuffer buffer, int count, int value) {
        int i;
        NumberFormat numberFormat = this.numberFormat;
        if (count > 3) {
            i = 3;
        } else {
            i = count;
        }
        numberFormat.setMinimumIntegerDigits(i);
        this.numberFormat.setMaximumIntegerDigits(10);
        if (count == 1) {
            value /= 100;
        } else if (count == 2) {
            value /= 10;
        }
        FieldPosition p = new FieldPosition(0);
        this.numberFormat.format(Integer.valueOf(value), buffer, p);
        if (count > 3) {
            this.numberFormat.setMinimumIntegerDigits(count - 3);
            this.numberFormat.format(Integer.valueOf(0), buffer, p);
        }
    }

    private void appendNumber(StringBuffer buffer, int count, int value) {
        int minimumIntegerDigits = this.numberFormat.getMinimumIntegerDigits();
        this.numberFormat.setMinimumIntegerDigits(count);
        this.numberFormat.format(Integer.valueOf(value), buffer, new FieldPosition(0));
        this.numberFormat.setMinimumIntegerDigits(minimumIntegerDigits);
    }

    private Date error(ParsePosition position, int offset, TimeZone zone) {
        position.setErrorIndex(offset);
        this.calendar.setTimeZone(zone);
        return null;
    }

    public StringBuffer format(Date date, StringBuffer buffer, FieldPosition fieldPos) {
        return formatImpl(date, buffer, fieldPos, null);
    }

    public Date get2DigitYearStart() {
        return (Date) this.defaultCenturyStart.clone();
    }

    public DateFormatSymbols getDateFormatSymbols() {
        return (DateFormatSymbols) this.formatData.clone();
    }

    public int hashCode() {
        return ((super.hashCode() + this.pattern.hashCode()) + this.formatData.hashCode()) + this.creationYear;
    }

    private int parse(String string, int offset, char format, int count) {
        int index = PATTERN_CHARS.indexOf((int) format);
        if (index == -1) {
            throw new IllegalArgumentException("Unknown pattern character '" + format + "'");
        }
        int field = -1;
        int absolute = 0;
        if (count < 0) {
            count = -count;
            absolute = count;
        }
        ParsePosition position;
        Number result;
        int hour;
        switch (index) {
            case XmlPullParser.START_DOCUMENT /*0*/:
                return parseText(string, offset, this.formatData.eras, 0);
            case NodeFilter.SHOW_ELEMENT /*1*/:
                if (count >= 3) {
                    field = 1;
                    break;
                }
                position = new ParsePosition(offset);
                result = parseNumber(absolute, string, position);
                if (result == null) {
                    return (-position.getErrorIndex()) - 1;
                }
                int year = result.intValue();
                if (position.getIndex() - offset == 2 && year >= 0) {
                    year += (this.creationYear / 100) * 100;
                    if (year < this.creationYear) {
                        year += 100;
                    }
                }
                this.calendar.set(1, year);
                return position.getIndex();
            case NodeFilter.SHOW_ATTRIBUTE /*2*/:
                return parseMonth(string, offset, count, absolute, false);
            case XmlPullParser.END_TAG /*3*/:
                field = 5;
                break;
            case NodeFilter.SHOW_TEXT /*4*/:
                position = new ParsePosition(offset);
                result = parseNumber(absolute, string, position);
                if (result == null) {
                    return (-position.getErrorIndex()) - 1;
                }
                hour = result.intValue();
                if (hour == 24) {
                    hour = 0;
                }
                this.calendar.set(11, hour);
                return position.getIndex();
            case XmlPullParser.CDSECT /*5*/:
                field = 11;
                break;
            case XmlPullParser.ENTITY_REF /*6*/:
                field = 12;
                break;
            case XmlPullParser.IGNORABLE_WHITESPACE /*7*/:
                field = 13;
                break;
            case NodeFilter.SHOW_CDATA_SECTION /*8*/:
                return parseFractionalSeconds(string, offset, absolute);
            case XmlPullParser.COMMENT /*9*/:
                return parseDayOfWeek(string, offset, false);
            case XmlPullParser.DOCDECL /*10*/:
                field = 6;
                break;
            case ASN1UTCTime.UTC_HM /*11*/:
                field = 8;
                break;
            case ICU.U_ILLEGAL_CHAR_FOUND /*12*/:
                field = 3;
                break;
            case ASN1UTCTime.UTC_HMS /*13*/:
                field = 4;
                break;
            case ZipConstants.LOCCRC /*14*/:
                return parseText(string, offset, this.formatData.ampms, 9);
            case ASN1UTCTime.UTC_LOCAL_HM /*15*/:
                position = new ParsePosition(offset);
                result = parseNumber(absolute, string, position);
                if (result == null) {
                    return (-position.getErrorIndex()) - 1;
                }
                hour = result.intValue();
                if (hour == 12) {
                    hour = 0;
                }
                this.calendar.set(10, hour);
                return position.getIndex();
            case NodeFilter.SHOW_ENTITY_REFERENCE /*16*/:
                field = 10;
                break;
            case ASN1UTCTime.UTC_LOCAL_HMS /*17*/:
                return parseTimeZone(string, offset);
            case RFC_822_TIMEZONE_FIELD /*18*/:
                return parseTimeZone(string, offset);
            case STAND_ALONE_MONTH_FIELD /*19*/:
                return parseMonth(string, offset, count, absolute, true);
            case STAND_ALONE_DAY_OF_WEEK_FIELD /*20*/:
                return parseDayOfWeek(string, offset, true);
        }
        if (field != -1) {
            return parseNumber(absolute, string, offset, field, 0);
        }
        return offset;
    }

    private int parseFractionalSeconds(String string, int offset, int count) {
        ParsePosition parsePosition = new ParsePosition(offset);
        Number fractionalSeconds = parseNumber(count, string, parsePosition);
        if (fractionalSeconds == null) {
            return (-parsePosition.getErrorIndex()) - 1;
        }
        this.calendar.set(14, (int) ((fractionalSeconds.doubleValue() / Math.pow(10.0d, (double) (parsePosition.getIndex() - offset))) * 1000.0d));
        return parsePosition.getIndex();
    }

    private int parseDayOfWeek(String string, int offset, boolean standAlone) {
        LocaleData ld = this.formatData.localeData;
        int index = parseText(string, offset, standAlone ? ld.longStandAloneWeekdayNames : this.formatData.weekdays, 7);
        if (index >= 0) {
            return index;
        }
        return parseText(string, offset, standAlone ? ld.shortStandAloneWeekdayNames : this.formatData.shortWeekdays, 7);
    }

    private int parseMonth(String string, int offset, int count, int absolute, boolean standAlone) {
        if (count <= 2) {
            return parseNumber(absolute, string, offset, 2, -1);
        }
        LocaleData ld = this.formatData.localeData;
        int index = parseText(string, offset, standAlone ? ld.longStandAloneMonthNames : this.formatData.months, 2);
        if (index >= 0) {
            return index;
        }
        return parseText(string, offset, standAlone ? ld.shortStandAloneMonthNames : this.formatData.shortMonths, 2);
    }

    public Date parse(String string, ParsePosition position) {
        boolean quote = false;
        char last = '\uffff';
        int count = 0;
        int offset = position.getIndex();
        int length = string.length();
        this.calendar.clear();
        TimeZone zone = this.calendar.getTimeZone();
        int patternLength = this.pattern.length();
        for (int i = 0; i < patternLength; i++) {
            char next = this.pattern.charAt(i);
            if (next == '\'') {
                if (count > 0) {
                    offset = parse(string, offset, (char) last, count);
                    if (offset < 0) {
                        return error(position, (-offset) - 1, zone);
                    }
                    count = 0;
                }
                if (last != next) {
                    last = next;
                } else if (offset >= length || string.charAt(offset) != '\'') {
                    return error(position, offset, zone);
                } else {
                    offset++;
                    last = '\uffff';
                }
                if (quote) {
                    quote = false;
                } else {
                    quote = true;
                }
            } else if (quote || (last != next && ((next < 'a' || next > 'z') && (next < 'A' || next > 'Z')))) {
                if (count > 0) {
                    offset = parse(string, offset, (char) last, count);
                    if (offset < 0) {
                        return error(position, (-offset) - 1, zone);
                    }
                    count = 0;
                }
                last = '\uffff';
                if (offset >= length || string.charAt(offset) != next) {
                    return error(position, offset, zone);
                }
                offset++;
            } else if (last == next) {
                count++;
            } else {
                if (count > 0) {
                    offset = parse(string, offset, (char) last, -count);
                    if (offset < 0) {
                        return error(position, (-offset) - 1, zone);
                    }
                }
                last = next;
                count = 1;
            }
        }
        if (count > 0) {
            offset = parse(string, offset, (char) last, count);
            if (offset < 0) {
                return error(position, (-offset) - 1, zone);
            }
        }
        try {
            Date date = this.calendar.getTime();
            position.setIndex(offset);
            this.calendar.setTimeZone(zone);
            return date;
        } catch (IllegalArgumentException e) {
            return error(position, offset, zone);
        }
    }

    private Number parseNumber(int max, String string, ParsePosition position) {
        int length = string.length();
        int index = position.getIndex();
        if (max > 0 && max < length - index) {
            length = index + max;
        }
        while (index < length && (string.charAt(index) == ' ' || string.charAt(index) == '\t')) {
            index++;
        }
        if (max == 0) {
            position.setIndex(index);
            return this.numberFormat.parse(string, position);
        }
        int result = 0;
        while (index < length) {
            int digit = Character.digit(string.charAt(index), 10);
            if (digit == -1) {
                break;
            }
            result = (result * 10) + digit;
            index++;
        }
        if (index == position.getIndex()) {
            position.setErrorIndex(index);
            return null;
        }
        position.setIndex(index);
        return Integer.valueOf(result);
    }

    private int parseNumber(int max, String string, int offset, int field, int skew) {
        ParsePosition position = new ParsePosition(offset);
        Number result = parseNumber(max, string, position);
        if (result == null) {
            return (-position.getErrorIndex()) - 1;
        }
        this.calendar.set(field, result.intValue() + skew);
        return position.getIndex();
    }

    private int parseText(String string, int offset, String[] options, int field) {
        int bestIndex = -1;
        int bestLength = -1;
        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            int optionLength = option.length();
            if (optionLength != 0) {
                if (string.regionMatches(true, offset, option, 0, optionLength)) {
                    if (bestIndex == -1 || optionLength > bestLength) {
                        bestIndex = i;
                        bestLength = optionLength;
                    }
                } else if (option.charAt(optionLength - 1) == '.') {
                    if (string.regionMatches(true, offset, option, 0, optionLength - 1) && (bestIndex == -1 || optionLength - 1 > bestLength)) {
                        bestIndex = i;
                        bestLength = optionLength - 1;
                    }
                }
            }
        }
        if (bestIndex == -1) {
            return (-offset) - 1;
        }
        this.calendar.set(field, bestIndex);
        return offset + bestLength;
    }

    private int parseTimeZone(String string, int offset) {
        int raw;
        boolean foundGMT = string.regionMatches(offset, "GMT", 0, 3);
        if (foundGMT) {
            offset += 3;
        }
        if (offset < string.length()) {
            char sign = string.charAt(offset);
            if (sign == '+' || sign == '-') {
                ParsePosition parsePosition = new ParsePosition(offset + 1);
                Number result = this.numberFormat.parse(string, parsePosition);
                if (result == null) {
                    return (-parsePosition.getErrorIndex()) - 1;
                }
                int hour = result.intValue();
                raw = hour * Grego.MILLIS_PER_HOUR;
                int index = parsePosition.getIndex();
                if (index < string.length() && string.charAt(index) == ':') {
                    parsePosition.setIndex(index + 1);
                    result = this.numberFormat.parse(string, parsePosition);
                    if (result == null) {
                        return (-parsePosition.getErrorIndex()) - 1;
                    }
                    raw += Grego.MILLIS_PER_MINUTE * result.intValue();
                } else if (hour >= 24) {
                    raw = ((hour / 100) * Grego.MILLIS_PER_HOUR) + ((hour % 100) * Grego.MILLIS_PER_MINUTE);
                }
                if (sign == '-') {
                    raw = -raw;
                }
                this.calendar.setTimeZone(new SimpleTimeZone(raw, XmlPullParser.NO_NAMESPACE));
                return parsePosition.getIndex();
            }
        }
        if (foundGMT) {
            this.calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
            return offset;
        }
        for (String[] row : this.formatData.internalZoneStrings()) {
            int i = 1;
            while (i < 5) {
                if (row[i] != null) {
                    if (string.regionMatches(true, offset, row[i], 0, row[i].length())) {
                        TimeZone zone = TimeZone.getTimeZone(row[0]);
                        if (zone == null) {
                            return (-offset) - 1;
                        }
                        raw = zone.getRawOffset();
                        if (i == 3 || i == 4) {
                            int dstSavings = zone.getDSTSavings();
                            if (dstSavings == 0) {
                                dstSavings = Grego.MILLIS_PER_HOUR;
                            }
                            raw += dstSavings;
                        }
                        this.calendar.setTimeZone(new SimpleTimeZone(raw, XmlPullParser.NO_NAMESPACE));
                        return offset + row[i].length();
                    }
                }
                i++;
            }
        }
        return (-offset) - 1;
    }

    public void set2DigitYearStart(Date date) {
        this.defaultCenturyStart = (Date) date.clone();
        Calendar cal = new GregorianCalendar();
        cal.setTime(this.defaultCenturyStart);
        this.creationYear = cal.get(1);
    }

    public void setDateFormatSymbols(DateFormatSymbols value) {
        this.formatData = (DateFormatSymbols) value.clone();
    }

    public String toLocalizedPattern() {
        return convertPattern(this.pattern, PATTERN_CHARS, this.formatData.getLocalPatternChars(), false);
    }

    private static String convertPattern(String template, String fromChars, String toChars, boolean check) {
        if (!check && fromChars.equals(toChars)) {
            return template;
        }
        boolean quote = false;
        StringBuilder output = new StringBuilder();
        int length = template.length();
        int i = 0;
        while (i < length) {
            char next = template.charAt(i);
            if (next == '\'') {
                quote = !quote;
            }
            if (!quote) {
                int index = fromChars.indexOf((int) next);
                if (index != -1) {
                    output.append(toChars.charAt(index));
                    i++;
                }
            }
            if (!check || quote || ((next < 'a' || next > 'z') && (next < 'A' || next > 'Z'))) {
                output.append(next);
                i++;
            } else {
                throw new IllegalArgumentException("Invalid pattern character '" + next + "' in " + "'" + template + "'");
            }
        }
        if (!quote) {
            return output.toString();
        }
        throw new IllegalArgumentException("Unterminated quote");
    }

    public String toPattern() {
        return this.pattern;
    }

    static {
        serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("defaultCenturyStart", Date.class), new ObjectStreamField("formatData", DateFormatSymbols.class), new ObjectStreamField("pattern", String.class), new ObjectStreamField("serialVersionOnStream", Integer.TYPE)};
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        PutField fields = stream.putFields();
        fields.put("defaultCenturyStart", this.defaultCenturyStart);
        fields.put("formatData", this.formatData);
        fields.put("pattern", this.pattern);
        fields.put("serialVersionOnStream", 1);
        stream.writeFields();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        Date date;
        GetField fields = stream.readFields();
        if (fields.get("serialVersionOnStream", 0) > 0) {
            date = (Date) fields.get("defaultCenturyStart", new Date());
        } else {
            date = new Date();
        }
        set2DigitYearStart(date);
        this.formatData = (DateFormatSymbols) fields.get("formatData", null);
        this.pattern = (String) fields.get("pattern", XmlPullParser.NO_NAMESPACE);
    }
}
