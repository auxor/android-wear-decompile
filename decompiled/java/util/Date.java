package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import libcore.icu.LocaleData;

public class Date implements Serializable, Cloneable, Comparable<Date> {
    private static final int CREATION_YEAR;
    private static final long serialVersionUID = 7523967970034938905L;
    private transient long milliseconds;

    static {
        CREATION_YEAR = new Date().getYear();
    }

    public Date() {
        this(System.currentTimeMillis());
    }

    @Deprecated
    public Date(int year, int month, int day) {
        GregorianCalendar cal = new GregorianCalendar(false);
        cal.set(year + 1900, month, day);
        this.milliseconds = cal.getTimeInMillis();
    }

    @Deprecated
    public Date(int year, int month, int day, int hour, int minute) {
        GregorianCalendar cal = new GregorianCalendar(false);
        cal.set(year + 1900, month, day, hour, minute);
        this.milliseconds = cal.getTimeInMillis();
    }

    @Deprecated
    public Date(int year, int month, int day, int hour, int minute, int second) {
        GregorianCalendar cal = new GregorianCalendar(false);
        cal.set(year + 1900, month, day, hour, minute, second);
        this.milliseconds = cal.getTimeInMillis();
    }

    public Date(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    @Deprecated
    public Date(String string) {
        this.milliseconds = parse(string);
    }

    public boolean after(Date date) {
        return this.milliseconds > date.milliseconds;
    }

    public boolean before(Date date) {
        return this.milliseconds < date.milliseconds;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (Object e) {
            throw new AssertionError(e);
        }
    }

    public int compareTo(Date date) {
        if (this.milliseconds < date.milliseconds) {
            return -1;
        }
        if (this.milliseconds == date.milliseconds) {
            return CREATION_YEAR;
        }
        return 1;
    }

    public boolean equals(Object object) {
        return object == this || ((object instanceof Date) && this.milliseconds == ((Date) object).milliseconds);
    }

    @Deprecated
    public int getDate() {
        return new GregorianCalendar(this.milliseconds).get(5);
    }

    @Deprecated
    public int getDay() {
        return new GregorianCalendar(this.milliseconds).get(7) - 1;
    }

    @Deprecated
    public int getHours() {
        return new GregorianCalendar(this.milliseconds).get(11);
    }

    @Deprecated
    public int getMinutes() {
        return new GregorianCalendar(this.milliseconds).get(12);
    }

    @Deprecated
    public int getMonth() {
        return new GregorianCalendar(this.milliseconds).get(2);
    }

    @Deprecated
    public int getSeconds() {
        return new GregorianCalendar(this.milliseconds).get(13);
    }

    public long getTime() {
        return this.milliseconds;
    }

    @Deprecated
    public int getTimezoneOffset() {
        GregorianCalendar cal = new GregorianCalendar(this.milliseconds);
        return (-(cal.get(15) + cal.get(16))) / Grego.MILLIS_PER_MINUTE;
    }

    @Deprecated
    public int getYear() {
        return new GregorianCalendar(this.milliseconds).get(1) - 1900;
    }

    public int hashCode() {
        return ((int) (this.milliseconds >>> 32)) ^ ((int) this.milliseconds);
    }

    private static int parse(String string, String[] array) {
        int alength = array.length;
        int slength = string.length();
        for (int i = CREATION_YEAR; i < alength; i++) {
            if (string.regionMatches(true, CREATION_YEAR, array[i], CREATION_YEAR, slength)) {
                return i;
            }
        }
        return -1;
    }

    private static IllegalArgumentException parseError(String string) {
        throw new IllegalArgumentException("Parse error: " + string);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @java.lang.Deprecated
    public static long parse(java.lang.String r36) {
        /*
        if (r36 != 0) goto L_0x000a;
    L_0x0002:
        r2 = new java.lang.IllegalArgumentException;
        r8 = "The string argument is null";
        r2.<init>(r8);
        throw r2;
    L_0x000a:
        r27 = 0;
        r19 = 0;
        r26 = 0;
        r21 = r36.length();
        r28 = 0;
        r33 = -1;
        r3 = -1;
        r4 = -1;
        r5 = -1;
        r6 = -1;
        r7 = -1;
        r35 = 0;
        r22 = 0;
        r34 = 0;
        r17 = 0;
        r15 = 1;
        r16 = 2;
        r18 = new java.lang.StringBuilder;
        r18.<init>();
    L_0x002d:
        r0 = r26;
        r1 = r21;
        if (r0 > r1) goto L_0x02bb;
    L_0x0033:
        r0 = r26;
        r1 = r21;
        if (r0 >= r1) goto L_0x00f1;
    L_0x0039:
        r0 = r36;
        r1 = r26;
        r24 = r0.charAt(r1);
    L_0x0041:
        r26 = r26 + 1;
        r2 = 40;
        r0 = r24;
        if (r0 != r2) goto L_0x004b;
    L_0x0049:
        r19 = r19 + 1;
    L_0x004b:
        if (r19 <= 0) goto L_0x0059;
    L_0x004d:
        r2 = 41;
        r0 = r24;
        if (r0 != r2) goto L_0x0055;
    L_0x0053:
        r19 = r19 + -1;
    L_0x0055:
        if (r19 != 0) goto L_0x002d;
    L_0x0057:
        r24 = 32;
    L_0x0059:
        r25 = 0;
        r2 = 97;
        r0 = r24;
        if (r2 > r0) goto L_0x0067;
    L_0x0061:
        r2 = 122; // 0x7a float:1.71E-43 double:6.03E-322;
        r0 = r24;
        if (r0 <= r2) goto L_0x0073;
    L_0x0067:
        r2 = 65;
        r0 = r24;
        if (r2 > r0) goto L_0x00f5;
    L_0x006d:
        r2 = 90;
        r0 = r24;
        if (r0 > r2) goto L_0x00f5;
    L_0x0073:
        r25 = 1;
    L_0x0075:
        r2 = 2;
        r0 = r28;
        if (r0 != r2) goto L_0x01e5;
    L_0x007a:
        r2 = 2;
        r0 = r25;
        if (r0 == r2) goto L_0x01e5;
    L_0x007f:
        r2 = r18.toString();
        r20 = java.lang.Integer.parseInt(r2);
        r2 = 0;
        r0 = r18;
        r0.setLength(r2);
        r2 = 43;
        r0 = r27;
        if (r0 == r2) goto L_0x0099;
    L_0x0093:
        r2 = 45;
        r0 = r27;
        if (r0 != r2) goto L_0x0132;
    L_0x0099:
        if (r35 != 0) goto L_0x012d;
    L_0x009b:
        r34 = 1;
        r2 = 58;
        r0 = r24;
        if (r0 != r2) goto L_0x00bc;
    L_0x00a3:
        r2 = 45;
        r0 = r27;
        if (r0 != r2) goto L_0x011b;
    L_0x00a9:
        r2 = r26 + 2;
        r0 = r36;
        r1 = r26;
        r2 = r0.substring(r1, r2);
        r2 = java.lang.Integer.parseInt(r2);
        r0 = -r2;
        r22 = r0;
    L_0x00ba:
        r26 = r26 + 2;
    L_0x00bc:
        r2 = 45;
        r0 = r27;
        if (r0 != r2) goto L_0x012a;
    L_0x00c2:
        r0 = r20;
        r0 = -r0;
        r35 = r0;
    L_0x00c7:
        r27 = 0;
    L_0x00c9:
        r2 = 43;
        r0 = r24;
        if (r0 == r2) goto L_0x00da;
    L_0x00cf:
        r2 = -1;
        r0 = r33;
        if (r0 == r2) goto L_0x02a6;
    L_0x00d4:
        r2 = 45;
        r0 = r24;
        if (r0 != r2) goto L_0x02a6;
    L_0x00da:
        r27 = r24;
    L_0x00dc:
        r2 = 1;
        r0 = r25;
        if (r0 == r2) goto L_0x00e6;
    L_0x00e1:
        r2 = 2;
        r0 = r25;
        if (r0 != r2) goto L_0x00ed;
    L_0x00e6:
        r0 = r18;
        r1 = r24;
        r0.append(r1);
    L_0x00ed:
        r28 = r25;
        goto L_0x002d;
    L_0x00f1:
        r24 = 13;
        goto L_0x0041;
    L_0x00f5:
        r2 = 48;
        r0 = r24;
        if (r2 > r0) goto L_0x0105;
    L_0x00fb:
        r2 = 57;
        r0 = r24;
        if (r0 > r2) goto L_0x0105;
    L_0x0101:
        r25 = 2;
        goto L_0x0075;
    L_0x0105:
        r2 = java.lang.Character.isSpace(r24);
        if (r2 != 0) goto L_0x0075;
    L_0x010b:
        r2 = ",+-:/";
        r0 = r24;
        r2 = r2.indexOf(r0);
        r8 = -1;
        if (r2 != r8) goto L_0x0075;
    L_0x0116:
        r2 = parseError(r36);
        throw r2;
    L_0x011b:
        r2 = r26 + 2;
        r0 = r36;
        r1 = r26;
        r2 = r0.substring(r1, r2);
        r22 = java.lang.Integer.parseInt(r2);
        goto L_0x00ba;
    L_0x012a:
        r35 = r20;
        goto L_0x00c7;
    L_0x012d:
        r2 = parseError(r36);
        throw r2;
    L_0x0132:
        r2 = 70;
        r0 = r20;
        if (r0 < r2) goto L_0x015e;
    L_0x0138:
        r2 = -1;
        r0 = r33;
        if (r0 != r2) goto L_0x0159;
    L_0x013d:
        r2 = java.lang.Character.isSpace(r24);
        if (r2 != 0) goto L_0x0155;
    L_0x0143:
        r2 = 44;
        r0 = r24;
        if (r0 == r2) goto L_0x0155;
    L_0x0149:
        r2 = 47;
        r0 = r24;
        if (r0 == r2) goto L_0x0155;
    L_0x014f:
        r2 = 13;
        r0 = r24;
        if (r0 != r2) goto L_0x0159;
    L_0x0155:
        r33 = r20;
        goto L_0x00c9;
    L_0x0159:
        r2 = parseError(r36);
        throw r2;
    L_0x015e:
        r2 = 58;
        r0 = r24;
        if (r0 != r2) goto L_0x0177;
    L_0x0164:
        r2 = -1;
        if (r5 != r2) goto L_0x016b;
    L_0x0167:
        r5 = r20;
        goto L_0x00c9;
    L_0x016b:
        r2 = -1;
        if (r6 != r2) goto L_0x0172;
    L_0x016e:
        r6 = r20;
        goto L_0x00c9;
    L_0x0172:
        r2 = parseError(r36);
        throw r2;
    L_0x0177:
        r2 = 47;
        r0 = r24;
        if (r0 != r2) goto L_0x0190;
    L_0x017d:
        r2 = -1;
        if (r3 != r2) goto L_0x0184;
    L_0x0180:
        r3 = r20 + -1;
        goto L_0x00c9;
    L_0x0184:
        r2 = -1;
        if (r4 != r2) goto L_0x018b;
    L_0x0187:
        r4 = r20;
        goto L_0x00c9;
    L_0x018b:
        r2 = parseError(r36);
        throw r2;
    L_0x0190:
        r2 = java.lang.Character.isSpace(r24);
        if (r2 != 0) goto L_0x01a8;
    L_0x0196:
        r2 = 44;
        r0 = r24;
        if (r0 == r2) goto L_0x01a8;
    L_0x019c:
        r2 = 45;
        r0 = r24;
        if (r0 == r2) goto L_0x01a8;
    L_0x01a2:
        r2 = 13;
        r0 = r24;
        if (r0 != r2) goto L_0x01d1;
    L_0x01a8:
        r2 = -1;
        if (r5 == r2) goto L_0x01b2;
    L_0x01ab:
        r2 = -1;
        if (r6 != r2) goto L_0x01b2;
    L_0x01ae:
        r6 = r20;
        goto L_0x00c9;
    L_0x01b2:
        r2 = -1;
        if (r6 == r2) goto L_0x01bc;
    L_0x01b5:
        r2 = -1;
        if (r7 != r2) goto L_0x01bc;
    L_0x01b8:
        r7 = r20;
        goto L_0x00c9;
    L_0x01bc:
        r2 = -1;
        if (r4 != r2) goto L_0x01c3;
    L_0x01bf:
        r4 = r20;
        goto L_0x00c9;
    L_0x01c3:
        r2 = -1;
        r0 = r33;
        if (r0 != r2) goto L_0x01cc;
    L_0x01c8:
        r33 = r20;
        goto L_0x00c9;
    L_0x01cc:
        r2 = parseError(r36);
        throw r2;
    L_0x01d1:
        r2 = -1;
        r0 = r33;
        if (r0 != r2) goto L_0x01e0;
    L_0x01d6:
        r2 = -1;
        if (r3 == r2) goto L_0x01e0;
    L_0x01d9:
        r2 = -1;
        if (r4 == r2) goto L_0x01e0;
    L_0x01dc:
        r33 = r20;
        goto L_0x00c9;
    L_0x01e0:
        r2 = parseError(r36);
        throw r2;
    L_0x01e5:
        r2 = 1;
        r0 = r28;
        if (r0 != r2) goto L_0x00c9;
    L_0x01ea:
        r2 = 1;
        r0 = r25;
        if (r0 == r2) goto L_0x00c9;
    L_0x01ef:
        r2 = r18.toString();
        r8 = java.util.Locale.US;
        r30 = r2.toUpperCase(r8);
        r2 = 0;
        r0 = r18;
        r0.setLength(r2);
        r2 = r30.length();
        r8 = 1;
        if (r2 != r8) goto L_0x020b;
    L_0x0206:
        r2 = parseError(r36);
        throw r2;
    L_0x020b:
        r2 = "AM";
        r0 = r30;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x0228;
    L_0x0215:
        r2 = 12;
        if (r5 != r2) goto L_0x021c;
    L_0x0219:
        r5 = 0;
        goto L_0x00c9;
    L_0x021c:
        r2 = 1;
        if (r5 < r2) goto L_0x0223;
    L_0x021f:
        r2 = 12;
        if (r5 <= r2) goto L_0x00c9;
    L_0x0223:
        r2 = parseError(r36);
        throw r2;
    L_0x0228:
        r2 = "PM";
        r0 = r30;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x0247;
    L_0x0232:
        r2 = 12;
        if (r5 != r2) goto L_0x023b;
    L_0x0236:
        r5 = 0;
    L_0x0237:
        r5 = r5 + 12;
        goto L_0x00c9;
    L_0x023b:
        r2 = 1;
        if (r5 < r2) goto L_0x0242;
    L_0x023e:
        r2 = 12;
        if (r5 <= r2) goto L_0x0237;
    L_0x0242:
        r2 = parseError(r36);
        throw r2;
    L_0x0247:
        r29 = new java.text.DateFormatSymbols;
        r2 = java.util.Locale.US;
        r0 = r29;
        r0.<init>(r2);
        r32 = r29.getWeekdays();
        r23 = r29.getMonths();
        r0 = r30;
        r1 = r32;
        r2 = parse(r0, r1);
        r8 = -1;
        if (r2 != r8) goto L_0x00c9;
    L_0x0263:
        r2 = -1;
        if (r3 != r2) goto L_0x0271;
    L_0x0266:
        r0 = r30;
        r1 = r23;
        r3 = parse(r0, r1);
        r2 = -1;
        if (r3 != r2) goto L_0x00c9;
    L_0x0271:
        r2 = "GMT";
        r0 = r30;
        r2 = r0.equals(r2);
        if (r2 != 0) goto L_0x028f;
    L_0x027b:
        r2 = "UT";
        r0 = r30;
        r2 = r0.equals(r2);
        if (r2 != 0) goto L_0x028f;
    L_0x0285:
        r2 = "UTC";
        r0 = r30;
        r2 = r0.equals(r2);
        if (r2 == 0) goto L_0x0295;
    L_0x028f:
        r34 = 1;
        r35 = 0;
        goto L_0x00c9;
    L_0x0295:
        r31 = zone(r30);
        if (r31 == 0) goto L_0x02a1;
    L_0x029b:
        r34 = 1;
        r35 = r31;
        goto L_0x00c9;
    L_0x02a1:
        r2 = parseError(r36);
        throw r2;
    L_0x02a6:
        r2 = java.lang.Character.isSpace(r24);
        if (r2 != 0) goto L_0x00dc;
    L_0x02ac:
        r2 = 44;
        r0 = r24;
        if (r0 == r2) goto L_0x00dc;
    L_0x02b2:
        r2 = 2;
        r0 = r25;
        if (r0 == r2) goto L_0x00dc;
    L_0x02b7:
        r27 = 0;
        goto L_0x00dc;
    L_0x02bb:
        r2 = -1;
        r0 = r33;
        if (r0 == r2) goto L_0x0322;
    L_0x02c0:
        r2 = -1;
        if (r3 == r2) goto L_0x0322;
    L_0x02c3:
        r2 = -1;
        if (r4 == r2) goto L_0x0322;
    L_0x02c6:
        r2 = -1;
        if (r5 != r2) goto L_0x02ca;
    L_0x02c9:
        r5 = 0;
    L_0x02ca:
        r2 = -1;
        if (r6 != r2) goto L_0x02ce;
    L_0x02cd:
        r6 = 0;
    L_0x02ce:
        r2 = -1;
        if (r7 != r2) goto L_0x02d2;
    L_0x02d1:
        r7 = 0;
    L_0x02d2:
        r2 = CREATION_YEAR;
        r2 = r2 + -80;
        r0 = r33;
        if (r0 >= r2) goto L_0x02ff;
    L_0x02da:
        r0 = r33;
        r0 = r0 + 2000;
        r33 = r0;
    L_0x02e0:
        r6 = r6 - r22;
        if (r34 == 0) goto L_0x030f;
    L_0x02e4:
        r2 = 24;
        r0 = r35;
        if (r0 >= r2) goto L_0x02f0;
    L_0x02ea:
        r2 = -24;
        r0 = r35;
        if (r0 > r2) goto L_0x030c;
    L_0x02f0:
        r2 = r35 / 100;
        r5 = r5 - r2;
        r2 = r35 % 100;
        r6 = r6 - r2;
    L_0x02f6:
        r0 = r33;
        r2 = r0 + -1900;
        r8 = UTC(r2, r3, r4, r5, r6, r7);
    L_0x02fe:
        return r8;
    L_0x02ff:
        r2 = 100;
        r0 = r33;
        if (r0 >= r2) goto L_0x02e0;
    L_0x0305:
        r0 = r33;
        r0 = r0 + 1900;
        r33 = r0;
        goto L_0x02e0;
    L_0x030c:
        r5 = r5 - r35;
        goto L_0x02f6;
    L_0x030f:
        r8 = new java.util.Date;
        r0 = r33;
        r9 = r0 + -1900;
        r10 = r3;
        r11 = r4;
        r12 = r5;
        r13 = r6;
        r14 = r7;
        r8.<init>(r9, r10, r11, r12, r13, r14);
        r8 = r8.getTime();
        goto L_0x02fe;
    L_0x0322:
        r2 = parseError(r36);
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.Date.parse(java.lang.String):long");
    }

    @Deprecated
    public void setDate(int day) {
        GregorianCalendar cal = new GregorianCalendar(this.milliseconds);
        cal.set(5, day);
        this.milliseconds = cal.getTimeInMillis();
    }

    @Deprecated
    public void setHours(int hour) {
        GregorianCalendar cal = new GregorianCalendar(this.milliseconds);
        cal.set(11, hour);
        this.milliseconds = cal.getTimeInMillis();
    }

    @Deprecated
    public void setMinutes(int minute) {
        GregorianCalendar cal = new GregorianCalendar(this.milliseconds);
        cal.set(12, minute);
        this.milliseconds = cal.getTimeInMillis();
    }

    @Deprecated
    public void setMonth(int month) {
        GregorianCalendar cal = new GregorianCalendar(this.milliseconds);
        cal.set(2, month);
        this.milliseconds = cal.getTimeInMillis();
    }

    @Deprecated
    public void setSeconds(int second) {
        GregorianCalendar cal = new GregorianCalendar(this.milliseconds);
        cal.set(13, second);
        this.milliseconds = cal.getTimeInMillis();
    }

    public void setTime(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    @Deprecated
    public void setYear(int year) {
        GregorianCalendar cal = new GregorianCalendar(this.milliseconds);
        cal.set(1, year + 1900);
        this.milliseconds = cal.getTimeInMillis();
    }

    @Deprecated
    public String toGMTString() {
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM y HH:mm:ss 'GMT'", Locale.US);
        TimeZone gmtZone = TimeZone.getTimeZone("GMT");
        sdf.setTimeZone(gmtZone);
        new GregorianCalendar(gmtZone).setTimeInMillis(this.milliseconds);
        return sdf.format(this);
    }

    @Deprecated
    public String toLocaleString() {
        return DateFormat.getDateTimeInstance().format(this);
    }

    public String toString() {
        LocaleData localeData = LocaleData.get(Locale.US);
        Calendar cal = new GregorianCalendar(this.milliseconds);
        TimeZone tz = cal.getTimeZone();
        StringBuilder result = new StringBuilder();
        result.append(localeData.shortWeekdayNames[cal.get(7)]);
        result.append(' ');
        result.append(localeData.shortMonthNames[cal.get(2)]);
        result.append(' ');
        appendTwoDigits(result, cal.get(5));
        result.append(' ');
        appendTwoDigits(result, cal.get(11));
        result.append(':');
        appendTwoDigits(result, cal.get(12));
        result.append(':');
        appendTwoDigits(result, cal.get(13));
        result.append(' ');
        result.append(tz.getDisplayName(tz.inDaylightTime(this), CREATION_YEAR));
        result.append(' ');
        result.append(cal.get(1));
        return result.toString();
    }

    private static void appendTwoDigits(StringBuilder sb, int n) {
        if (n < 10) {
            sb.append('0');
        }
        sb.append(n);
    }

    @Deprecated
    public static long UTC(int year, int month, int day, int hour, int minute, int second) {
        GregorianCalendar cal = new GregorianCalendar(false);
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.set(year + 1900, month, day, hour, minute, second);
        return cal.getTimeInMillis();
    }

    private static int zone(String text) {
        if (text.equals("EST")) {
            return -5;
        }
        if (text.equals("EDT")) {
            return -4;
        }
        if (text.equals("CST")) {
            return -6;
        }
        if (text.equals("CDT")) {
            return -5;
        }
        if (text.equals("MST")) {
            return -7;
        }
        if (text.equals("MDT")) {
            return -6;
        }
        if (text.equals("PST")) {
            return -8;
        }
        if (text.equals("PDT")) {
            return -7;
        }
        return CREATION_YEAR;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeLong(getTime());
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        setTime(stream.readLong());
    }
}
