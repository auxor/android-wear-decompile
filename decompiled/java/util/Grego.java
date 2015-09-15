package java.util;

import dalvik.bytecode.Opcodes;
import java.net.HttpURLConnection;

class Grego {
    private static final int[] DAYS_BEFORE;
    private static final int JULIAN_1970_CE = 2440588;
    private static final int JULIAN_1_CE = 1721426;
    public static final long MAX_MILLIS = 183882168921600000L;
    public static final int MILLIS_PER_DAY = 86400000;
    public static final int MILLIS_PER_HOUR = 3600000;
    public static final int MILLIS_PER_MINUTE = 60000;
    public static final int MILLIS_PER_SECOND = 1000;
    public static final long MIN_MILLIS = -184303902528000000L;
    private static final int[] MONTH_LENGTH;

    Grego() {
    }

    static {
        MONTH_LENGTH = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        DAYS_BEFORE = new int[]{0, 31, 59, 90, Opcodes.OP_INVOKE_INTERFACE_RANGE, Opcodes.OP_XOR_INT, Opcodes.OP_AND_INT_2ADDR, Opcodes.OP_REM_INT_LIT16, Opcodes.OP_IGET_WIDE_QUICK, 273, HttpURLConnection.HTTP_NOT_MODIFIED, 334, 0, 31, 60, 91, 121, Opcodes.OP_SHL_INT, Opcodes.OP_OR_INT_2ADDR, Opcodes.OP_AND_INT_LIT16, Opcodes.OP_IGET_OBJECT_QUICK, 274, HttpURLConnection.HTTP_USE_PROXY, 335};
    }

    public static final boolean isLeapYear(int year) {
        return (year & 3) == 0 && (year % 100 != 0 || year % HttpURLConnection.HTTP_BAD_REQUEST == 0);
    }

    public static final int monthLength(int year, int month) {
        return MONTH_LENGTH[(isLeapYear(year) ? 12 : 0) + month];
    }

    public static final int previousMonthLength(int year, int month) {
        return month > 0 ? monthLength(year, month - 1) : 31;
    }

    public static long fieldsToDay(int year, int month, int dom) {
        int y = year - 1;
        return (((((((((long) (y * 365)) + floorDivide((long) y, 4)) + 1721423) + floorDivide((long) y, 400)) - floorDivide((long) y, 100)) + 2) + ((long) DAYS_BEFORE[(isLeapYear(year) ? 12 : 0) + month])) + ((long) dom)) - 2440588;
    }

    public static int dayOfWeek(long day) {
        long[] remainder = new long[1];
        floorDivide(5 + day, 7, remainder);
        int dayOfWeek = (int) remainder[0];
        if (dayOfWeek == 0) {
            return 7;
        }
        return dayOfWeek;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int[] dayToFields(long r26, int[] r28) {
        /*
        if (r28 == 0) goto L_0x000f;
    L_0x0002:
        r0 = r28;
        r0 = r0.length;
        r21 = r0;
        r22 = 5;
        r0 = r21;
        r1 = r22;
        if (r0 >= r1) goto L_0x0017;
    L_0x000f:
        r21 = 5;
        r0 = r21;
        r0 = new int[r0];
        r28 = r0;
    L_0x0017:
        r22 = 719162; // 0xaf93a float:1.00776E-39 double:3.55313E-318;
        r26 = r26 + r22;
        r21 = 1;
        r0 = r21;
        r11 = new long[r0];
        r22 = 146097; // 0x23ab1 float:2.04726E-40 double:7.21815E-319;
        r0 = r26;
        r2 = r22;
        r18 = floorDivide(r0, r2, r11);
        r21 = 0;
        r22 = r11[r21];
        r24 = 36524; // 0x8eac float:5.1181E-41 double:1.80453E-319;
        r0 = r22;
        r2 = r24;
        r14 = floorDivide(r0, r2, r11);
        r21 = 0;
        r22 = r11[r21];
        r24 = 1461; // 0x5b5 float:2.047E-42 double:7.22E-321;
        r0 = r22;
        r2 = r24;
        r16 = floorDivide(r0, r2, r11);
        r21 = 0;
        r22 = r11[r21];
        r24 = 365; // 0x16d float:5.11E-43 double:1.803E-321;
        r0 = r22;
        r2 = r24;
        r12 = floorDivide(r0, r2, r11);
        r22 = 400; // 0x190 float:5.6E-43 double:1.976E-321;
        r22 = r22 * r18;
        r24 = 100;
        r24 = r24 * r14;
        r22 = r22 + r24;
        r24 = 4;
        r24 = r24 * r16;
        r22 = r22 + r24;
        r22 = r22 + r12;
        r0 = r22;
        r0 = (int) r0;
        r20 = r0;
        r21 = 0;
        r22 = r11[r21];
        r0 = r22;
        r7 = (int) r0;
        r22 = 4;
        r21 = (r14 > r22 ? 1 : (r14 == r22 ? 0 : -1));
        if (r21 == 0) goto L_0x0082;
    L_0x007c:
        r22 = 4;
        r21 = (r12 > r22 ? 1 : (r12 == r22 ? 0 : -1));
        if (r21 != 0) goto L_0x00d2;
    L_0x0082:
        r7 = 365; // 0x16d float:5.11E-43 double:1.803E-321;
    L_0x0084:
        r8 = isLeapYear(r20);
        r4 = 0;
        if (r8 == 0) goto L_0x00d5;
    L_0x008b:
        r9 = 60;
    L_0x008d:
        if (r7 < r9) goto L_0x0092;
    L_0x008f:
        if (r8 == 0) goto L_0x00d8;
    L_0x0091:
        r4 = 1;
    L_0x0092:
        r21 = r7 + r4;
        r21 = r21 * 12;
        r21 = r21 + 6;
        r0 = r21;
        r10 = r0 / 367;
        r22 = DAYS_BEFORE;
        if (r8 == 0) goto L_0x00da;
    L_0x00a0:
        r21 = r10 + 12;
    L_0x00a2:
        r21 = r22[r21];
        r21 = r7 - r21;
        r5 = r21 + 1;
        r22 = 2;
        r22 = r22 + r26;
        r24 = 7;
        r22 = r22 % r24;
        r0 = r22;
        r6 = (int) r0;
        r21 = 1;
        r0 = r21;
        if (r6 >= r0) goto L_0x00bb;
    L_0x00b9:
        r6 = r6 + 7;
    L_0x00bb:
        r7 = r7 + 1;
        r21 = 0;
        r28[r21] = r20;
        r21 = 1;
        r28[r21] = r10;
        r21 = 2;
        r28[r21] = r5;
        r21 = 3;
        r28[r21] = r6;
        r21 = 4;
        r28[r21] = r7;
        return r28;
    L_0x00d2:
        r20 = r20 + 1;
        goto L_0x0084;
    L_0x00d5:
        r9 = 59;
        goto L_0x008d;
    L_0x00d8:
        r4 = 2;
        goto L_0x0092;
    L_0x00da:
        r21 = r10;
        goto L_0x00a2;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.Grego.dayToFields(long, int[]):int[]");
    }

    public static int[] timeToFields(long time, int[] fields) {
        if (fields == null || fields.length < 6) {
            fields = new int[6];
        }
        long[] remainder = new long[1];
        dayToFields(floorDivide(time, 86400000, remainder), fields);
        fields[5] = (int) remainder[0];
        return fields;
    }

    public static long floorDivide(long numerator, long denominator) {
        return numerator >= 0 ? numerator / denominator : ((numerator + 1) / denominator) - 1;
    }

    private static long floorDivide(long numerator, long denominator, long[] remainder) {
        if (numerator >= 0) {
            remainder[0] = numerator % denominator;
            return numerator / denominator;
        }
        long quotient = ((numerator + 1) / denominator) - 1;
        remainder[0] = numerator - (quotient * denominator);
        return quotient;
    }

    public static int getDayOfWeekInMonth(int year, int month, int dayOfMonth) {
        int weekInMonth = (dayOfMonth + 6) / 7;
        if (weekInMonth == 4) {
            if (dayOfMonth + 7 > monthLength(year, month)) {
                return -1;
            }
            return weekInMonth;
        } else if (weekInMonth == 5) {
            return -1;
        } else {
            return weekInMonth;
        }
    }
}
