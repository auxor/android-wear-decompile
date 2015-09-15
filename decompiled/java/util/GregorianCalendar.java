package java.util;

import dalvik.bytecode.Opcodes;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import libcore.icu.ICU;
import org.apache.harmony.security.asn1.ASN1UTCTime;
import org.w3c.dom.traversal.NodeFilter;
import org.xmlpull.v1.XmlPullParser;

public class GregorianCalendar extends Calendar {
    public static final int AD = 1;
    public static final int BC = 0;
    static byte[] DaysInMonth = null;
    private static int[] DaysInYear = null;
    private static final long defaultGregorianCutover = -12219292800000L;
    private static int[] leastMaximums = null;
    private static int[] maximums = null;
    private static int[] minimums = null;
    private static final long serialVersionUID = -8125100834729963327L;
    private transient int changeYear;
    private int currentYearSkew;
    private long gregorianCutover;
    private transient int julianSkew;
    private int lastYearSkew;

    static {
        DaysInMonth = new byte[]{(byte) 31, Character.OTHER_SYMBOL, (byte) 31, Character.FINAL_QUOTE_PUNCTUATION, (byte) 31, Character.FINAL_QUOTE_PUNCTUATION, (byte) 31, (byte) 31, Character.FINAL_QUOTE_PUNCTUATION, (byte) 31, Character.FINAL_QUOTE_PUNCTUATION, (byte) 31};
        DaysInYear = new int[]{BC, 31, 59, 90, Opcodes.OP_INVOKE_INTERFACE_RANGE, Opcodes.OP_XOR_INT, Opcodes.OP_AND_INT_2ADDR, Opcodes.OP_REM_INT_LIT16, Opcodes.OP_IGET_WIDE_QUICK, 273, HttpURLConnection.HTTP_NOT_MODIFIED, 334};
        maximums = new int[]{AD, 292278994, 11, 53, 6, 31, 366, 7, 6, AD, 11, 23, 59, 59, 999, 50400000, 7200000};
        minimums = new int[]{BC, AD, BC, AD, BC, AD, AD, AD, AD, BC, BC, BC, BC, BC, BC, -46800000, BC};
        leastMaximums = new int[]{AD, 292269054, 11, 50, 3, 28, 355, 7, 3, AD, 11, 23, 59, 59, 999, 50400000, 1200000};
    }

    public GregorianCalendar() {
        this(TimeZone.getDefault(), Locale.getDefault());
    }

    public GregorianCalendar(int year, int month, int day) {
        super(TimeZone.getDefault(), Locale.getDefault());
        this.gregorianCutover = defaultGregorianCutover;
        this.changeYear = 1582;
        this.julianSkew = (((this.changeYear - 2000) / HttpURLConnection.HTTP_BAD_REQUEST) + julianError()) - ((this.changeYear - 2000) / 100);
        this.currentYearSkew = 10;
        this.lastYearSkew = BC;
        set(year, month, day);
    }

    public GregorianCalendar(int year, int month, int day, int hour, int minute) {
        super(TimeZone.getDefault(), Locale.getDefault());
        this.gregorianCutover = defaultGregorianCutover;
        this.changeYear = 1582;
        this.julianSkew = (((this.changeYear - 2000) / HttpURLConnection.HTTP_BAD_REQUEST) + julianError()) - ((this.changeYear - 2000) / 100);
        this.currentYearSkew = 10;
        this.lastYearSkew = BC;
        set(year, month, day, hour, minute);
    }

    public GregorianCalendar(int year, int month, int day, int hour, int minute, int second) {
        super(TimeZone.getDefault(), Locale.getDefault());
        this.gregorianCutover = defaultGregorianCutover;
        this.changeYear = 1582;
        this.julianSkew = (((this.changeYear - 2000) / HttpURLConnection.HTTP_BAD_REQUEST) + julianError()) - ((this.changeYear - 2000) / 100);
        this.currentYearSkew = 10;
        this.lastYearSkew = BC;
        set(year, month, day, hour, minute, second);
    }

    GregorianCalendar(long milliseconds) {
        this(false);
        setTimeInMillis(milliseconds);
    }

    public GregorianCalendar(Locale locale) {
        this(TimeZone.getDefault(), locale);
    }

    public GregorianCalendar(TimeZone timezone) {
        this(timezone, Locale.getDefault());
    }

    public GregorianCalendar(TimeZone timezone, Locale locale) {
        super(timezone, locale);
        this.gregorianCutover = defaultGregorianCutover;
        this.changeYear = 1582;
        this.julianSkew = (((this.changeYear - 2000) / HttpURLConnection.HTTP_BAD_REQUEST) + julianError()) - ((this.changeYear - 2000) / 100);
        this.currentYearSkew = 10;
        this.lastYearSkew = BC;
        setTimeInMillis(System.currentTimeMillis());
    }

    public GregorianCalendar(boolean ignored) {
        super(TimeZone.getDefault());
        this.gregorianCutover = defaultGregorianCutover;
        this.changeYear = 1582;
        this.julianSkew = (((this.changeYear - 2000) / HttpURLConnection.HTTP_BAD_REQUEST) + julianError()) - ((this.changeYear - 2000) / 100);
        this.currentYearSkew = 10;
        this.lastYearSkew = BC;
        setFirstDayOfWeek(AD);
        setMinimalDaysInFirstWeek(AD);
    }

    public void add(int field, int value) {
        if (value != 0) {
            if (field < 0 || field >= 15) {
                throw new IllegalArgumentException();
            } else if (field == 0) {
                complete();
                if (this.fields[BC] == AD) {
                    if (value < 0) {
                        set(BC, BC);
                    } else {
                        return;
                    }
                } else if (value > 0) {
                    set(BC, AD);
                } else {
                    return;
                }
                complete();
            } else if (field == AD || field == 2) {
                complete();
                if (field == 2) {
                    int month = this.fields[2] + value;
                    if (month < 0) {
                        value = (month - 11) / 12;
                        month = (month % 12) + 12;
                    } else {
                        value = month / 12;
                    }
                    set(2, month % 12);
                }
                set(AD, this.fields[AD] + value);
                int days = daysInMonth(isLeapYear(this.fields[AD]), this.fields[2]);
                if (this.fields[5] > days) {
                    set(5, days);
                }
                complete();
            } else {
                long multiplier = 0;
                getTimeInMillis();
                switch (field) {
                    case XmlPullParser.END_TAG /*3*/:
                    case NodeFilter.SHOW_TEXT /*4*/:
                    case NodeFilter.SHOW_CDATA_SECTION /*8*/:
                        multiplier = 604800000;
                        break;
                    case XmlPullParser.CDSECT /*5*/:
                    case XmlPullParser.ENTITY_REF /*6*/:
                    case XmlPullParser.IGNORABLE_WHITESPACE /*7*/:
                        multiplier = 86400000;
                        break;
                    case XmlPullParser.COMMENT /*9*/:
                        multiplier = 43200000;
                        break;
                    case XmlPullParser.DOCDECL /*10*/:
                    case ASN1UTCTime.UTC_HM /*11*/:
                        this.time += ((long) value) * 3600000;
                        break;
                    case ICU.U_ILLEGAL_CHAR_FOUND /*12*/:
                        this.time += ((long) value) * 60000;
                        break;
                    case ASN1UTCTime.UTC_HMS /*13*/:
                        this.time += ((long) value) * 1000;
                        break;
                    case ZipConstants.LOCCRC /*14*/:
                        this.time += (long) value;
                        break;
                }
                if (multiplier == 0) {
                    this.areFieldsSet = false;
                    complete();
                    return;
                }
                long delta = ((long) value) * multiplier;
                int zoneOffset = getTimeZone().getRawOffset();
                int offsetBefore = getOffset(this.time + ((long) zoneOffset));
                int offsetAfter = getOffset((this.time + ((long) zoneOffset)) + delta);
                int dstDelta = offsetBefore - offsetAfter;
                if (getOffset(((this.time + ((long) zoneOffset)) + delta) + ((long) dstDelta)) == offsetAfter) {
                    delta += (long) dstDelta;
                }
                this.time += delta;
                this.areFieldsSet = false;
                complete();
            }
        }
    }

    private void fullFieldsCalc() {
        int dstOffset;
        int millis = (int) (this.time % 86400000);
        long days = this.time / 86400000;
        if (millis < 0) {
            millis += Grego.MILLIS_PER_DAY;
            days--;
        }
        millis += this.fields[15];
        while (millis < 0) {
            millis += Grego.MILLIS_PER_DAY;
            days--;
        }
        while (millis >= Grego.MILLIS_PER_DAY) {
            millis -= Grego.MILLIS_PER_DAY;
            days++;
        }
        int dayOfYear = computeYearAndDay(days, this.time + ((long) this.fields[15]));
        this.fields[6] = dayOfYear;
        if (this.fields[AD] == this.changeYear) {
            if (this.gregorianCutover <= this.time + ((long) this.fields[15])) {
                dayOfYear += this.currentYearSkew;
            }
        }
        int month = dayOfYear / 32;
        boolean leapYear = isLeapYear(this.fields[AD]);
        int date = dayOfYear - daysInYear(leapYear, month);
        if (date > daysInMonth(leapYear, month)) {
            date -= daysInMonth(leapYear, month);
            month += AD;
        }
        this.fields[7] = mod7(days - 3) + AD;
        if (this.fields[AD] <= 0) {
            dstOffset = BC;
        } else {
            dstOffset = getTimeZone().getOffset(AD, this.fields[AD], month, date, this.fields[7], millis);
        }
        if (this.fields[AD] > 0) {
            dstOffset -= this.fields[15];
        }
        this.fields[16] = dstOffset;
        if (dstOffset != 0) {
            long oldDays = days;
            millis += dstOffset;
            if (millis < 0) {
                millis += Grego.MILLIS_PER_DAY;
                days--;
            } else if (millis >= Grego.MILLIS_PER_DAY) {
                millis -= Grego.MILLIS_PER_DAY;
                days++;
            }
            if (oldDays != days) {
                dayOfYear = computeYearAndDay(days, (this.time - ((long) this.fields[15])) + ((long) dstOffset));
                this.fields[6] = dayOfYear;
                if (this.fields[AD] == this.changeYear) {
                    if (this.gregorianCutover <= (this.time - ((long) this.fields[15])) + ((long) dstOffset)) {
                        dayOfYear += this.currentYearSkew;
                    }
                }
                month = dayOfYear / 32;
                leapYear = isLeapYear(this.fields[AD]);
                date = dayOfYear - daysInYear(leapYear, month);
                if (date > daysInMonth(leapYear, month)) {
                    date -= daysInMonth(leapYear, month);
                    month += AD;
                }
                this.fields[7] = mod7(days - 3) + AD;
            }
        }
        this.fields[14] = millis % Grego.MILLIS_PER_SECOND;
        millis /= Grego.MILLIS_PER_SECOND;
        this.fields[13] = millis % 60;
        millis /= 60;
        this.fields[12] = millis % 60;
        this.fields[11] = (millis / 60) % 24;
        this.fields[9] = this.fields[11] > 11 ? AD : BC;
        this.fields[10] = this.fields[11] % 12;
        if (this.fields[AD] <= 0) {
            this.fields[BC] = BC;
            this.fields[AD] = (-this.fields[AD]) + AD;
        } else {
            this.fields[BC] = AD;
        }
        this.fields[2] = month;
        this.fields[5] = date;
        this.fields[8] = ((date - 1) / 7) + AD;
        this.fields[4] = (((date - 1) + mod7(((days - ((long) date)) - 2) - ((long) (getFirstDayOfWeek() - 1)))) / 7) + AD;
        int daysFromStart = mod7(((days - 3) - ((long) (this.fields[6] - 1))) - ((long) (getFirstDayOfWeek() - 1)));
        int week = (((this.fields[6] - 1) + daysFromStart) / 7) + (7 - daysFromStart >= getMinimalDaysInFirstWeek() ? AD : BC);
        if (week == 0) {
            this.fields[3] = 7 - mod7((long) (daysFromStart - (isLeapYear(this.fields[AD] + -1) ? 2 : AD))) >= getMinimalDaysInFirstWeek() ? 53 : 52;
            return;
        }
        if (this.fields[6] >= (leapYear ? 367 : 366) - mod7((long) ((leapYear ? 2 : AD) + daysFromStart))) {
            int[] iArr = this.fields;
            if (7 - mod7((long) ((leapYear ? 2 : AD) + daysFromStart)) >= getMinimalDaysInFirstWeek()) {
                week = AD;
            }
            iArr[3] = week;
            return;
        }
        this.fields[3] = week;
    }

    protected void computeFields() {
        TimeZone timeZone = getTimeZone();
        int dstOffset = timeZone.inDaylightTime(new Date(this.time)) ? timeZone.getDSTSavings() : BC;
        int zoneOffset = timeZone.getRawOffset();
        this.fields[16] = dstOffset;
        this.fields[15] = zoneOffset;
        fullFieldsCalc();
        for (int i = BC; i < 17; i += AD) {
            this.isSet[i] = true;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected void computeTime() {
        /*
        r28 = this;
        r21 = r28.isLenient();
        if (r21 != 0) goto L_0x02d8;
    L_0x0006:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 11;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x0036;
    L_0x0012:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 11;
        r21 = r21[r22];
        if (r21 < 0) goto L_0x0030;
    L_0x001e:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 11;
        r21 = r21[r22];
        r22 = 23;
        r0 = r21;
        r1 = r22;
        if (r0 <= r1) goto L_0x0066;
    L_0x0030:
        r21 = new java.lang.IllegalArgumentException;
        r21.<init>();
        throw r21;
    L_0x0036:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 10;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x0066;
    L_0x0042:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 10;
        r21 = r21[r22];
        if (r21 < 0) goto L_0x0060;
    L_0x004e:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 10;
        r21 = r21[r22];
        r22 = 11;
        r0 = r21;
        r1 = r22;
        if (r0 <= r1) goto L_0x0066;
    L_0x0060:
        r21 = new java.lang.IllegalArgumentException;
        r21.<init>();
        throw r21;
    L_0x0066:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 12;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x0096;
    L_0x0072:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 12;
        r21 = r21[r22];
        if (r21 < 0) goto L_0x0090;
    L_0x007e:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 12;
        r21 = r21[r22];
        r22 = 59;
        r0 = r21;
        r1 = r22;
        if (r0 <= r1) goto L_0x0096;
    L_0x0090:
        r21 = new java.lang.IllegalArgumentException;
        r21.<init>();
        throw r21;
    L_0x0096:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 13;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x00c6;
    L_0x00a2:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 13;
        r21 = r21[r22];
        if (r21 < 0) goto L_0x00c0;
    L_0x00ae:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 13;
        r21 = r21[r22];
        r22 = 59;
        r0 = r21;
        r1 = r22;
        if (r0 <= r1) goto L_0x00c6;
    L_0x00c0:
        r21 = new java.lang.IllegalArgumentException;
        r21.<init>();
        throw r21;
    L_0x00c6:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 14;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x00f6;
    L_0x00d2:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 14;
        r21 = r21[r22];
        if (r21 < 0) goto L_0x00f0;
    L_0x00de:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 14;
        r21 = r21[r22];
        r22 = 999; // 0x3e7 float:1.4E-42 double:4.936E-321;
        r0 = r21;
        r1 = r22;
        if (r0 <= r1) goto L_0x00f6;
    L_0x00f0:
        r21 = new java.lang.IllegalArgumentException;
        r21.<init>();
        throw r21;
    L_0x00f6:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 3;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x012c;
    L_0x0102:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 3;
        r21 = r21[r22];
        r22 = 1;
        r0 = r21;
        r1 = r22;
        if (r0 < r1) goto L_0x0126;
    L_0x0114:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 3;
        r21 = r21[r22];
        r22 = 53;
        r0 = r21;
        r1 = r22;
        if (r0 <= r1) goto L_0x012c;
    L_0x0126:
        r21 = new java.lang.IllegalArgumentException;
        r21.<init>();
        throw r21;
    L_0x012c:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 7;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x0162;
    L_0x0138:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 7;
        r21 = r21[r22];
        r22 = 1;
        r0 = r21;
        r1 = r22;
        if (r0 < r1) goto L_0x015c;
    L_0x014a:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 7;
        r21 = r21[r22];
        r22 = 7;
        r0 = r21;
        r1 = r22;
        if (r0 <= r1) goto L_0x0162;
    L_0x015c:
        r21 = new java.lang.IllegalArgumentException;
        r21.<init>();
        throw r21;
    L_0x0162:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 8;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x0198;
    L_0x016e:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 8;
        r21 = r21[r22];
        r22 = 1;
        r0 = r21;
        r1 = r22;
        if (r0 < r1) goto L_0x0192;
    L_0x0180:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 8;
        r21 = r21[r22];
        r22 = 6;
        r0 = r21;
        r1 = r22;
        if (r0 <= r1) goto L_0x0198;
    L_0x0192:
        r21 = new java.lang.IllegalArgumentException;
        r21.<init>();
        throw r21;
    L_0x0198:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 4;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x01ce;
    L_0x01a4:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 4;
        r21 = r21[r22];
        r22 = 1;
        r0 = r21;
        r1 = r22;
        if (r0 < r1) goto L_0x01c8;
    L_0x01b6:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 4;
        r21 = r21[r22];
        r22 = 6;
        r0 = r21;
        r1 = r22;
        if (r0 <= r1) goto L_0x01ce;
    L_0x01c8:
        r21 = new java.lang.IllegalArgumentException;
        r21.<init>();
        throw r21;
    L_0x01ce:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 9;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x01fe;
    L_0x01da:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 9;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x01fe;
    L_0x01e6:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 9;
        r21 = r21[r22];
        r22 = 1;
        r0 = r21;
        r1 = r22;
        if (r0 == r1) goto L_0x01fe;
    L_0x01f8:
        r21 = new java.lang.IllegalArgumentException;
        r21.<init>();
        throw r21;
    L_0x01fe:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 10;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x022e;
    L_0x020a:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 10;
        r21 = r21[r22];
        if (r21 < 0) goto L_0x0228;
    L_0x0216:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 10;
        r21 = r21[r22];
        r22 = 11;
        r0 = r21;
        r1 = r22;
        if (r0 <= r1) goto L_0x022e;
    L_0x0228:
        r21 = new java.lang.IllegalArgumentException;
        r21.<init>();
        throw r21;
    L_0x022e:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 1;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x02a8;
    L_0x023a:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 0;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x027d;
    L_0x0246:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 0;
        r21 = r21[r22];
        if (r21 != 0) goto L_0x027d;
    L_0x0252:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 1;
        r21 = r21[r22];
        r22 = 1;
        r0 = r21;
        r1 = r22;
        if (r0 < r1) goto L_0x0277;
    L_0x0264:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 1;
        r21 = r21[r22];
        r22 = 292269054; // 0x116babfe float:1.859123E-28 double:1.44400099E-315;
        r0 = r21;
        r1 = r22;
        if (r0 <= r1) goto L_0x027d;
    L_0x0277:
        r21 = new java.lang.IllegalArgumentException;
        r21.<init>();
        throw r21;
    L_0x027d:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 1;
        r21 = r21[r22];
        r22 = 1;
        r0 = r21;
        r1 = r22;
        if (r0 < r1) goto L_0x02a2;
    L_0x028f:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 1;
        r21 = r21[r22];
        r22 = 292278994; // 0x116bd2d2 float:1.8603195E-28 double:1.4440501E-315;
        r0 = r21;
        r1 = r22;
        if (r0 <= r1) goto L_0x02a8;
    L_0x02a2:
        r21 = new java.lang.IllegalArgumentException;
        r21.<init>();
        throw r21;
    L_0x02a8:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 2;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x02d8;
    L_0x02b4:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 2;
        r21 = r21[r22];
        if (r21 < 0) goto L_0x02d2;
    L_0x02c0:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 2;
        r21 = r21[r22];
        r22 = 11;
        r0 = r21;
        r1 = r22;
        if (r0 <= r1) goto L_0x02d8;
    L_0x02d2:
        r21 = new java.lang.IllegalArgumentException;
        r21.<init>();
        throw r21;
    L_0x02d8:
        r8 = 0;
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 11;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x03ac;
    L_0x02e6:
        r0 = r28;
        r0 = r0.lastTimeFieldSet;
        r21 = r0;
        r22 = 10;
        r0 = r21;
        r1 = r22;
        if (r0 == r1) goto L_0x03ac;
    L_0x02f4:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 11;
        r21 = r21[r22];
        r0 = r21;
        r8 = (long) r0;
    L_0x0301:
        r22 = 3600000; // 0x36ee80 float:5.044674E-39 double:1.7786363E-317;
        r12 = r8 * r22;
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 12;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x0328;
    L_0x0312:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 12;
        r21 = r21[r22];
        r0 = r21;
        r0 = (long) r0;
        r22 = r0;
        r24 = 60000; // 0xea60 float:8.4078E-41 double:2.9644E-319;
        r22 = r22 * r24;
        r12 = r12 + r22;
    L_0x0328:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 13;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x0349;
    L_0x0334:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 13;
        r21 = r21[r22];
        r0 = r21;
        r0 = (long) r0;
        r22 = r0;
        r24 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r22 = r22 * r24;
        r12 = r12 + r22;
    L_0x0349:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 14;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x0366;
    L_0x0355:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 14;
        r21 = r21[r22];
        r0 = r21;
        r0 = (long) r0;
        r22 = r0;
        r12 = r12 + r22;
    L_0x0366:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 1;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x03d5;
    L_0x0372:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 1;
        r20 = r21[r22];
    L_0x037c:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 0;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x03e6;
    L_0x0388:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 0;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x03d8;
    L_0x0394:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 0;
        r21 = r21[r22];
        r22 = 1;
        r0 = r21;
        r1 = r22;
        if (r0 == r1) goto L_0x03d8;
    L_0x03a6:
        r21 = new java.lang.IllegalArgumentException;
        r21.<init>();
        throw r21;
    L_0x03ac:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 10;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x0301;
    L_0x03b8:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 9;
        r21 = r21[r22];
        r21 = r21 * 12;
        r0 = r28;
        r0 = r0.fields;
        r22 = r0;
        r23 = 10;
        r22 = r22[r23];
        r21 = r21 + r22;
        r0 = r21;
        r8 = (long) r0;
        goto L_0x0301;
    L_0x03d5:
        r20 = 1970; // 0x7b2 float:2.76E-42 double:9.733E-321;
        goto L_0x037c;
    L_0x03d8:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 0;
        r21 = r21[r22];
        if (r21 != 0) goto L_0x03e6;
    L_0x03e4:
        r20 = 1 - r20;
    L_0x03e6:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 4;
        r21 = r21[r22];
        if (r21 != 0) goto L_0x03fe;
    L_0x03f2:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 8;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x0526;
    L_0x03fe:
        r19 = 1;
    L_0x0400:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 5;
        r21 = r21[r22];
        if (r21 != 0) goto L_0x041a;
    L_0x040c:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 2;
        r21 = r21[r22];
        if (r21 != 0) goto L_0x041a;
    L_0x0418:
        if (r19 == 0) goto L_0x052a;
    L_0x041a:
        r0 = r28;
        r0 = r0.lastDateFieldSet;
        r21 = r0;
        r22 = 6;
        r0 = r21;
        r1 = r22;
        if (r0 == r1) goto L_0x052a;
    L_0x0428:
        r17 = 1;
    L_0x042a:
        if (r17 == 0) goto L_0x0470;
    L_0x042c:
        r0 = r28;
        r0 = r0.lastDateFieldSet;
        r21 = r0;
        r22 = 7;
        r0 = r21;
        r1 = r22;
        if (r0 == r1) goto L_0x0448;
    L_0x043a:
        r0 = r28;
        r0 = r0.lastDateFieldSet;
        r21 = r0;
        r22 = 3;
        r0 = r21;
        r1 = r22;
        if (r0 != r1) goto L_0x0470;
    L_0x0448:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 3;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x0540;
    L_0x0454:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 7;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x0540;
    L_0x0460:
        r0 = r28;
        r0 = r0.lastDateFieldSet;
        r21 = r0;
        r22 = 3;
        r0 = r21;
        r1 = r22;
        if (r0 != r1) goto L_0x052e;
    L_0x046e:
        r17 = 0;
    L_0x0470:
        if (r17 == 0) goto L_0x0756;
    L_0x0472:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 2;
        r10 = r21[r22];
        r21 = r10 / 12;
        r20 = r20 + r21;
        r10 = r10 % 12;
        if (r10 >= 0) goto L_0x0488;
    L_0x0484:
        r20 = r20 + -1;
        r10 = r10 + 12;
    L_0x0488:
        r0 = r28;
        r1 = r20;
        r5 = r0.isLeapYear(r1);
        r0 = r20;
        r0 = (long) r0;
        r22 = r0;
        r0 = r28;
        r1 = r22;
        r22 = r0.daysFromBaseYear(r1);
        r0 = r28;
        r21 = r0.daysInYear(r5, r10);
        r0 = r21;
        r0 = (long) r0;
        r24 = r0;
        r6 = r22 + r24;
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 5;
        r16 = r21[r22];
        if (r16 == 0) goto L_0x04f0;
    L_0x04b6:
        r0 = r28;
        r0 = r0.lastDateFieldSet;
        r21 = r0;
        r22 = 7;
        r0 = r21;
        r1 = r22;
        if (r0 == r1) goto L_0x04e0;
    L_0x04c4:
        r0 = r28;
        r0 = r0.lastDateFieldSet;
        r21 = r0;
        r22 = 4;
        r0 = r21;
        r1 = r22;
        if (r0 == r1) goto L_0x04e0;
    L_0x04d2:
        r0 = r28;
        r0 = r0.lastDateFieldSet;
        r21 = r0;
        r22 = 8;
        r0 = r21;
        r1 = r22;
        if (r0 != r1) goto L_0x04f0;
    L_0x04e0:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 7;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x04ee;
    L_0x04ec:
        if (r19 != 0) goto L_0x056b;
    L_0x04ee:
        r16 = 1;
    L_0x04f0:
        if (r16 == 0) goto L_0x0603;
    L_0x04f2:
        r21 = r28.isLenient();
        if (r21 != 0) goto L_0x056e;
    L_0x04f8:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 5;
        r21 = r21[r22];
        r22 = 1;
        r0 = r21;
        r1 = r22;
        if (r0 < r1) goto L_0x0520;
    L_0x050a:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 5;
        r21 = r21[r22];
        r0 = r28;
        r22 = r0.daysInMonth(r5, r10);
        r0 = r21;
        r1 = r22;
        if (r0 <= r1) goto L_0x056e;
    L_0x0520:
        r21 = new java.lang.IllegalArgumentException;
        r21.<init>();
        throw r21;
    L_0x0526:
        r19 = 0;
        goto L_0x0400;
    L_0x052a:
        r17 = 0;
        goto L_0x042a;
    L_0x052e:
        r0 = r28;
        r0 = r0.lastDateFieldSet;
        r21 = r0;
        r22 = 7;
        r0 = r21;
        r1 = r22;
        if (r0 != r1) goto L_0x0470;
    L_0x053c:
        r17 = r19;
        goto L_0x0470;
    L_0x0540:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 6;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x0470;
    L_0x054c:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 5;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x0568;
    L_0x0558:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 2;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x0568;
    L_0x0564:
        r17 = 1;
    L_0x0566:
        goto L_0x0470;
    L_0x0568:
        r17 = 0;
        goto L_0x0566;
    L_0x056b:
        r16 = 0;
        goto L_0x04f0;
    L_0x056e:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 5;
        r21 = r21[r22];
        r21 = r21 + -1;
        r0 = r21;
        r0 = (long) r0;
        r22 = r0;
        r6 = r6 + r22;
    L_0x0581:
        r21 = 0;
        r0 = r21;
        r1 = r28;
        r1.lastDateFieldSet = r0;
        r22 = 86400000; // 0x5265c00 float:7.82218E-36 double:4.2687272E-316;
        r22 = r22 * r6;
        r12 = r12 + r22;
        r0 = r28;
        r0 = r0.changeYear;
        r21 = r0;
        r0 = r20;
        r1 = r21;
        if (r0 != r1) goto L_0x05c6;
    L_0x059c:
        r0 = r28;
        r0 = r0.gregorianCutover;
        r22 = r0;
        r21 = r28.julianError();
        r0 = r21;
        r0 = (long) r0;
        r24 = r0;
        r26 = 86400000; // 0x5265c00 float:7.82218E-36 double:4.2687272E-316;
        r24 = r24 * r26;
        r22 = r22 + r24;
        r21 = (r12 > r22 ? 1 : (r12 == r22 ? 0 : -1));
        if (r21 < 0) goto L_0x05c6;
    L_0x05b6:
        r21 = r28.julianError();
        r0 = r21;
        r0 = (long) r0;
        r22 = r0;
        r24 = 86400000; // 0x5265c00 float:7.82218E-36 double:4.2687272E-316;
        r22 = r22 * r24;
        r12 = r12 - r22;
    L_0x05c6:
        r0 = r28;
        r21 = r0.getOffset(r12);
        r0 = r21;
        r0 = (long) r0;
        r22 = r0;
        r22 = r12 - r22;
        r21 = r28.getTimeZone();
        r21 = r21.getRawOffset();
        r0 = r21;
        r0 = (long) r0;
        r24 = r0;
        r14 = r22 + r24;
        r0 = r28;
        r21 = r0.getOffset(r14);
        r0 = r21;
        r0 = (long) r0;
        r22 = r0;
        r12 = r12 - r22;
        r0 = r28;
        r0.time = r12;
        r21 = (r14 > r12 ? 1 : (r14 == r12 ? 0 : -1));
        if (r21 == 0) goto L_0x0602;
    L_0x05f7:
        r28.computeFields();
        r21 = 1;
        r0 = r21;
        r1 = r28;
        r1.areFieldsSet = r0;
    L_0x0602:
        return;
    L_0x0603:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 7;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x067e;
    L_0x060f:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 7;
        r21 = r21[r22];
        r4 = r21 + -1;
    L_0x061b:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 4;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x0685;
    L_0x0627:
        r0 = r28;
        r0 = r0.lastDateFieldSet;
        r21 = r0;
        r22 = 8;
        r0 = r21;
        r1 = r22;
        if (r0 == r1) goto L_0x0685;
    L_0x0635:
        r22 = 3;
        r22 = r6 - r22;
        r21 = r28.getFirstDayOfWeek();
        r21 = r21 + -1;
        r0 = r21;
        r0 = (long) r0;
        r24 = r0;
        r22 = r22 - r24;
        r0 = r28;
        r1 = r22;
        r11 = r0.mod7(r1);
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 4;
        r21 = r21[r22];
        r21 = r21 + -1;
        r21 = r21 * 7;
        r22 = r11 + r4;
        r0 = r22;
        r0 = (long) r0;
        r22 = r0;
        r24 = 3;
        r24 = r6 - r24;
        r22 = r22 - r24;
        r0 = r28;
        r1 = r22;
        r22 = r0.mod7(r1);
        r21 = r21 + r22;
        r21 = r21 - r11;
        r0 = r21;
        r0 = (long) r0;
        r22 = r0;
        r6 = r6 + r22;
        goto L_0x0581;
    L_0x067e:
        r21 = r28.getFirstDayOfWeek();
        r4 = r21 + -1;
        goto L_0x061b;
    L_0x0685:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 8;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x0704;
    L_0x0691:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 8;
        r21 = r21[r22];
        if (r21 < 0) goto L_0x06c7;
    L_0x069d:
        r0 = (long) r4;
        r22 = r0;
        r24 = 3;
        r24 = r6 - r24;
        r22 = r22 - r24;
        r0 = r28;
        r1 = r22;
        r21 = r0.mod7(r1);
        r0 = r28;
        r0 = r0.fields;
        r22 = r0;
        r23 = 8;
        r22 = r22[r23];
        r22 = r22 + -1;
        r22 = r22 * 7;
        r21 = r21 + r22;
        r0 = r21;
        r0 = (long) r0;
        r22 = r0;
        r6 = r6 + r22;
        goto L_0x0581;
    L_0x06c7:
        r0 = r28;
        r21 = r0.daysInMonth(r5, r10);
        r0 = (long) r4;
        r22 = r0;
        r0 = r28;
        r24 = r0.daysInMonth(r5, r10);
        r0 = r24;
        r0 = (long) r0;
        r24 = r0;
        r24 = r24 + r6;
        r26 = 3;
        r24 = r24 - r26;
        r22 = r22 - r24;
        r0 = r28;
        r1 = r22;
        r22 = r0.mod7(r1);
        r21 = r21 + r22;
        r0 = r28;
        r0 = r0.fields;
        r22 = r0;
        r23 = 8;
        r22 = r22[r23];
        r22 = r22 * 7;
        r21 = r21 + r22;
        r0 = r21;
        r0 = (long) r0;
        r22 = r0;
        r6 = r6 + r22;
        goto L_0x0581;
    L_0x0704:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 7;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x0581;
    L_0x0710:
        r22 = 3;
        r22 = r6 - r22;
        r21 = r28.getFirstDayOfWeek();
        r21 = r21 + -1;
        r0 = r21;
        r0 = (long) r0;
        r24 = r0;
        r22 = r22 - r24;
        r0 = r28;
        r1 = r22;
        r11 = r0.mod7(r1);
        r21 = r11 + r4;
        r0 = r21;
        r0 = (long) r0;
        r22 = r0;
        r24 = 3;
        r24 = r6 - r24;
        r22 = r22 - r24;
        r0 = r28;
        r1 = r22;
        r21 = r0.mod7(r1);
        r21 = r21 - r11;
        r0 = r21;
        r0 = (long) r0;
        r22 = r0;
        r0 = r28;
        r1 = r22;
        r21 = r0.mod7(r1);
        r0 = r21;
        r0 = (long) r0;
        r22 = r0;
        r6 = r6 + r22;
        goto L_0x0581;
    L_0x0756:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 3;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x080a;
    L_0x0762:
        r0 = r28;
        r0 = r0.lastDateFieldSet;
        r21 = r0;
        r22 = 6;
        r0 = r21;
        r1 = r22;
        if (r0 == r1) goto L_0x080a;
    L_0x0770:
        r18 = 1;
    L_0x0772:
        if (r18 == 0) goto L_0x078a;
    L_0x0774:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 6;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x078a;
    L_0x0780:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 7;
        r18 = r21[r22];
    L_0x078a:
        r0 = r20;
        r0 = (long) r0;
        r22 = r0;
        r0 = r28;
        r1 = r22;
        r6 = r0.daysFromBaseYear(r1);
        if (r18 == 0) goto L_0x0815;
    L_0x0799:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 7;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x080e;
    L_0x07a5:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 7;
        r21 = r21[r22];
        r4 = r21 + -1;
    L_0x07b1:
        r22 = 3;
        r22 = r6 - r22;
        r21 = r28.getFirstDayOfWeek();
        r21 = r21 + -1;
        r0 = r21;
        r0 = (long) r0;
        r24 = r0;
        r22 = r22 - r24;
        r0 = r28;
        r1 = r22;
        r11 = r0.mod7(r1);
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 3;
        r21 = r21[r22];
        r21 = r21 + -1;
        r21 = r21 * 7;
        r22 = r11 + r4;
        r0 = r22;
        r0 = (long) r0;
        r22 = r0;
        r24 = 3;
        r24 = r6 - r24;
        r22 = r22 - r24;
        r0 = r28;
        r1 = r22;
        r22 = r0.mod7(r1);
        r21 = r21 + r22;
        r21 = r21 - r11;
        r0 = r21;
        r0 = (long) r0;
        r22 = r0;
        r6 = r6 + r22;
        r21 = 7 - r11;
        r22 = r28.getMinimalDaysInFirstWeek();
        r0 = r21;
        r1 = r22;
        if (r0 >= r1) goto L_0x0581;
    L_0x0804:
        r22 = 7;
        r6 = r6 + r22;
        goto L_0x0581;
    L_0x080a:
        r18 = 0;
        goto L_0x0772;
    L_0x080e:
        r21 = r28.getFirstDayOfWeek();
        r4 = r21 + -1;
        goto L_0x07b1;
    L_0x0815:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 6;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x0879;
    L_0x0821:
        r21 = r28.isLenient();
        if (r21 != 0) goto L_0x0864;
    L_0x0827:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 6;
        r21 = r21[r22];
        r22 = 1;
        r0 = r21;
        r1 = r22;
        if (r0 < r1) goto L_0x085b;
    L_0x0839:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 6;
        r22 = r21[r22];
        r0 = r28;
        r1 = r20;
        r21 = r0.isLeapYear(r1);
        if (r21 == 0) goto L_0x0861;
    L_0x084d:
        r21 = 1;
    L_0x084f:
        r0 = r21;
        r0 = r0 + 365;
        r21 = r0;
        r0 = r22;
        r1 = r21;
        if (r0 <= r1) goto L_0x0864;
    L_0x085b:
        r21 = new java.lang.IllegalArgumentException;
        r21.<init>();
        throw r21;
    L_0x0861:
        r21 = 0;
        goto L_0x084f;
    L_0x0864:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 6;
        r21 = r21[r22];
        r21 = r21 + -1;
        r0 = r21;
        r0 = (long) r0;
        r22 = r0;
        r6 = r6 + r22;
        goto L_0x0581;
    L_0x0879:
        r0 = r28;
        r0 = r0.isSet;
        r21 = r0;
        r22 = 7;
        r21 = r21[r22];
        if (r21 == 0) goto L_0x0581;
    L_0x0885:
        r0 = r28;
        r0 = r0.fields;
        r21 = r0;
        r22 = 7;
        r21 = r21[r22];
        r21 = r21 + -1;
        r0 = r21;
        r0 = (long) r0;
        r22 = r0;
        r24 = 3;
        r24 = r6 - r24;
        r22 = r22 - r24;
        r0 = r28;
        r1 = r22;
        r21 = r0.mod7(r1);
        r0 = r21;
        r0 = (long) r0;
        r22 = r0;
        r6 = r6 + r22;
        goto L_0x0581;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.GregorianCalendar.computeTime():void");
    }

    private int computeYearAndDay(long dayCount, long localTime) {
        int year = 1970;
        long days = dayCount;
        if (localTime < this.gregorianCutover) {
            days -= (long) this.julianSkew;
        }
        while (true) {
            int approxYears = (int) (days / 365);
            if (approxYears == 0) {
                break;
            }
            year += approxYears;
            days = dayCount - daysFromBaseYear((long) year);
        }
        if (days < 0) {
            year--;
            days += (long) daysInYear(year);
        }
        this.fields[AD] = year;
        return ((int) days) + AD;
    }

    private long daysFromBaseYear(long year) {
        if (year >= 1970) {
            long days = ((year - 1970) * 365) + ((year - 1969) / 4);
            if (year > ((long) this.changeYear)) {
                return days - (((year - 1901) / 100) - ((year - 1601) / 400));
            }
            if (year == ((long) this.changeYear)) {
                return days + ((long) this.currentYearSkew);
            }
            if (year == ((long) (this.changeYear - 1))) {
                return days + ((long) this.lastYearSkew);
            }
            return days + ((long) this.julianSkew);
        } else if (year <= ((long) this.changeYear)) {
            return (((year - 1970) * 365) + ((year - 1972) / 4)) + ((long) this.julianSkew);
        } else {
            return ((((year - 1970) * 365) + ((year - 1972) / 4)) - ((year - 2000) / 100)) + ((year - 2000) / 400);
        }
    }

    private int daysInMonth() {
        return daysInMonth(isLeapYear(this.fields[AD]), this.fields[2]);
    }

    private int daysInMonth(boolean leapYear, int month) {
        if (leapYear && month == AD) {
            return DaysInMonth[month] + AD;
        }
        return DaysInMonth[month];
    }

    private int daysInYear(int year) {
        int daysInYear = isLeapYear(year) ? 366 : 365;
        if (year == this.changeYear) {
            daysInYear -= this.currentYearSkew;
        }
        if (year == this.changeYear - 1) {
            return daysInYear - this.lastYearSkew;
        }
        return daysInYear;
    }

    private int daysInYear(boolean leapYear, int month) {
        if (!leapYear || month <= AD) {
            return DaysInYear[month];
        }
        return DaysInYear[month] + AD;
    }

    public boolean equals(Object object) {
        if (!(object instanceof GregorianCalendar)) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (super.equals(object) && this.gregorianCutover == ((GregorianCalendar) object).gregorianCutover) {
            return true;
        }
        return false;
    }

    public int getActualMaximum(int field) {
        int value = maximums[field];
        if (value == leastMaximums[field]) {
            return value;
        }
        complete();
        long orgTime = this.time;
        int result = BC;
        switch (field) {
            case AD /*1*/:
                GregorianCalendar clone = (GregorianCalendar) clone();
                if (get(BC) == AD) {
                    clone.setTimeInMillis(Long.MAX_VALUE);
                } else {
                    clone.setTimeInMillis(Long.MIN_VALUE);
                }
                result = clone.get(AD);
                clone.set(AD, get(AD));
                if (clone.before(this)) {
                    result--;
                    break;
                }
                break;
            case XmlPullParser.END_TAG /*3*/:
                set(5, 31);
                set(2, 11);
                result = get(3);
                if (result == AD) {
                    set(5, 24);
                    result = get(3);
                }
                this.areFieldsSet = false;
                break;
            case NodeFilter.SHOW_TEXT /*4*/:
                set(5, daysInMonth());
                result = get(4);
                this.areFieldsSet = false;
                break;
            case XmlPullParser.CDSECT /*5*/:
                return daysInMonth();
            case XmlPullParser.ENTITY_REF /*6*/:
                return daysInYear(this.fields[AD]);
            case NodeFilter.SHOW_CDATA_SECTION /*8*/:
                result = get(8) + ((daysInMonth() - get(5)) / 7);
                break;
            case NodeFilter.SHOW_ENTITY_REFERENCE /*16*/:
                result = getMaximum(16);
                break;
        }
        this.time = orgTime;
        return result;
    }

    public int getActualMinimum(int field) {
        return getMinimum(field);
    }

    public int getGreatestMinimum(int field) {
        return minimums[field];
    }

    public final Date getGregorianChange() {
        return new Date(this.gregorianCutover);
    }

    public int getLeastMaximum(int field) {
        if (this.gregorianCutover == defaultGregorianCutover || field != 3) {
            return leastMaximums[field];
        }
        long currentTimeInMillis = this.time;
        setTimeInMillis(this.gregorianCutover);
        int actual = getActualMaximum(field);
        setTimeInMillis(currentTimeInMillis);
        return actual;
    }

    public int getMaximum(int field) {
        return maximums[field];
    }

    public int getMinimum(int field) {
        return minimums[field];
    }

    private int getOffset(long localTime) {
        TimeZone timeZone = getTimeZone();
        long dayCount = localTime / 86400000;
        int millis = (int) (localTime % 86400000);
        if (millis < 0) {
            millis += Grego.MILLIS_PER_DAY;
            dayCount--;
        }
        int year = 1970;
        long days = dayCount;
        if (localTime < this.gregorianCutover) {
            days -= (long) this.julianSkew;
        }
        while (true) {
            int approxYears = (int) (days / 365);
            if (approxYears == 0) {
                break;
            }
            year += approxYears;
            days = dayCount - daysFromBaseYear((long) year);
        }
        if (days < 0) {
            year--;
            days = (365 + days) + ((long) (isLeapYear(year) ? AD : BC));
            if (year == this.changeYear) {
                if (localTime < this.gregorianCutover) {
                    days -= (long) julianError();
                }
            }
        }
        if (year <= 0) {
            return timeZone.getRawOffset();
        }
        int dayOfYear = ((int) days) + AD;
        int month = dayOfYear / 32;
        boolean leapYear = isLeapYear(year);
        int date = dayOfYear - daysInYear(leapYear, month);
        if (date > daysInMonth(leapYear, month)) {
            date -= daysInMonth(leapYear, month);
            month += AD;
        }
        return timeZone.getOffset(AD, year, month, date, mod7(dayCount - 3) + AD, millis);
    }

    public int hashCode() {
        return super.hashCode() + (((int) (this.gregorianCutover >>> 32)) ^ ((int) this.gregorianCutover));
    }

    public boolean isLeapYear(int year) {
        if (year > this.changeYear) {
            if (year % 4 != 0 || (year % 100 == 0 && year % HttpURLConnection.HTTP_BAD_REQUEST != 0)) {
                return false;
            }
            return true;
        } else if (year % 4 != 0) {
            return false;
        } else {
            return true;
        }
    }

    private int julianError() {
        return ((this.changeYear / 100) - (this.changeYear / HttpURLConnection.HTTP_BAD_REQUEST)) - 2;
    }

    private int mod(int value, int mod) {
        int rem = value % mod;
        if (value >= 0 || rem >= 0) {
            return rem;
        }
        return rem + mod;
    }

    private int mod7(long num1) {
        int rem = (int) (num1 % 7);
        if (num1 >= 0 || rem >= 0) {
            return rem;
        }
        return rem + 7;
    }

    public void roll(int field, int value) {
        if (value != 0) {
            if (field < 0 || field >= 15) {
                throw new IllegalArgumentException();
            }
            complete();
            int max = -1;
            int days;
            int mod;
            int maxWeeks;
            int newWeek;
            switch (field) {
                case BC /*0*/:
                case NodeFilter.SHOW_ATTRIBUTE /*2*/:
                case XmlPullParser.COMMENT /*9*/:
                case XmlPullParser.DOCDECL /*10*/:
                case ASN1UTCTime.UTC_HM /*11*/:
                case ICU.U_ILLEGAL_CHAR_FOUND /*12*/:
                case ASN1UTCTime.UTC_HMS /*13*/:
                case ZipConstants.LOCCRC /*14*/:
                    set(field, mod(this.fields[field] + value, maximums[field] + AD));
                    if (field != 2 || this.fields[5] <= daysInMonth()) {
                        if (field == 9) {
                            this.lastTimeFieldSet = 10;
                            break;
                        }
                    }
                    set(5, daysInMonth());
                    break;
                    break;
                case AD /*1*/:
                    max = maximums[field];
                    break;
                case XmlPullParser.END_TAG /*3*/:
                    days = daysInYear(this.fields[AD]);
                    mod = mod7((long) ((this.fields[7] - this.fields[6]) - (getFirstDayOfWeek() - 1)));
                    maxWeeks = (((days - 1) + mod) / 7) + AD;
                    newWeek = mod((this.fields[field] - 1) + value, maxWeeks) + AD;
                    if (newWeek != maxWeeks) {
                        if (newWeek == AD) {
                            if (((((this.fields[6] - (((this.fields[6] - 1) / 7) * 7)) - 1) + mod) / 7) + AD <= AD) {
                                set(field, newWeek);
                                break;
                            } else {
                                set(field, AD);
                                break;
                            }
                        }
                        set(field, newWeek);
                        break;
                    }
                    int addDays = (newWeek - this.fields[field]) * 7;
                    if (this.fields[6] > addDays && this.fields[6] + addDays > days) {
                        set(field, AD);
                        break;
                    } else {
                        set(field, newWeek - 1);
                        break;
                    }
                    break;
                case NodeFilter.SHOW_TEXT /*4*/:
                    days = daysInMonth();
                    mod = mod7((long) ((this.fields[7] - this.fields[5]) - (getFirstDayOfWeek() - 1)));
                    maxWeeks = (((days - 1) + mod) / 7) + AD;
                    newWeek = mod((this.fields[field] - 1) + value, maxWeeks) + AD;
                    if (newWeek != maxWeeks) {
                        if (newWeek == AD) {
                            if (((((this.fields[5] - (((this.fields[5] - 1) / 7) * 7)) - 1) + mod) / 7) + AD <= AD) {
                                set(field, newWeek);
                                break;
                            } else {
                                set(5, AD);
                                break;
                            }
                        }
                        set(field, newWeek);
                        break;
                    } else if (this.fields[5] + ((newWeek - this.fields[field]) * 7) <= days) {
                        set(field, newWeek);
                        break;
                    } else {
                        set(5, days);
                        break;
                    }
                case XmlPullParser.CDSECT /*5*/:
                    max = daysInMonth();
                    break;
                case XmlPullParser.ENTITY_REF /*6*/:
                    max = daysInYear(this.fields[AD]);
                    break;
                case XmlPullParser.IGNORABLE_WHITESPACE /*7*/:
                    max = maximums[field];
                    this.lastDateFieldSet = 4;
                    break;
                case NodeFilter.SHOW_CDATA_SECTION /*8*/:
                    max = (((this.fields[5] + (((daysInMonth() - this.fields[5]) / 7) * 7)) - 1) / 7) + AD;
                    break;
            }
            if (max != -1) {
                set(field, mod((this.fields[field] - 1) + value, max) + AD);
            }
            complete();
        }
    }

    public void roll(int field, boolean increment) {
        roll(field, increment ? AD : -1);
    }

    public void setGregorianChange(Date date) {
        this.gregorianCutover = date.getTime();
        GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        cal.setTime(date);
        this.changeYear = cal.get(AD);
        if (cal.get(BC) == 0) {
            this.changeYear = 1 - this.changeYear;
        }
        this.julianSkew = (((this.changeYear - 2000) / HttpURLConnection.HTTP_BAD_REQUEST) + julianError()) - ((this.changeYear - 2000) / 100);
        int dayOfYear = cal.get(6);
        if (dayOfYear < this.julianSkew) {
            this.currentYearSkew = dayOfYear - 1;
            this.lastYearSkew = (this.julianSkew - dayOfYear) + AD;
            return;
        }
        this.lastYearSkew = BC;
        this.currentYearSkew = this.julianSkew;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        setGregorianChange(new Date(this.gregorianCutover));
    }
}
