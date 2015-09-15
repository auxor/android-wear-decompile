package java.lang;

import java.util.Locale;
import libcore.icu.DateIntervalFormat;

public final class IntegralToString {
    private static final ThreadLocal<char[]> BUFFER;
    private static final char[] DIGITS;
    private static final char[] MOD_10_TABLE;
    private static final char[] ONES;
    private static final String[] SMALL_NEGATIVE_VALUES;
    private static final String[] SMALL_NONNEGATIVE_VALUES;
    private static final char[] TENS;
    private static final char[] UPPER_CASE_DIGITS;

    static {
        BUFFER = new ThreadLocal<char[]>() {
            protected char[] initialValue() {
                return new char[20];
            }
        };
        SMALL_NONNEGATIVE_VALUES = new String[100];
        SMALL_NEGATIVE_VALUES = new String[100];
        TENS = new char[]{'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', '2', '2', '2', '2', '2', '2', '2', '2', '2', '2', '3', '3', '3', '3', '3', '3', '3', '3', '3', '3', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '5', '5', '5', '5', '5', '5', '5', '5', '5', '5', '6', '6', '6', '6', '6', '6', '6', '6', '6', '6', '7', '7', '7', '7', '7', '7', '7', '7', '7', '7', '8', '8', '8', '8', '8', '8', '8', '8', '8', '8', '9', '9', '9', '9', '9', '9', '9', '9', '9', '9'};
        ONES = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        MOD_10_TABLE = new char[]{'\u0000', '\u0001', '\u0002', '\u0002', '\u0003', '\u0003', '\u0004', '\u0005', '\u0005', '\u0006', '\u0007', '\u0007', '\b', '\b', '\t', '\u0000'};
        DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', Locale.UNICODE_LOCALE_EXTENSION, 'v', 'w', Locale.PRIVATE_USE_EXTENSION, 'y', 'z'};
        UPPER_CASE_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    }

    private IntegralToString() {
    }

    public static String intToString(int i, int radix) {
        if (radix < 2 || radix > 36) {
            radix = 10;
        }
        if (radix == 10) {
            return intToString(i);
        }
        boolean negative = false;
        if (i < 0) {
            negative = true;
        } else {
            i = -i;
        }
        int bufLen = radix < 8 ? 33 : 12;
        char[] buf = new char[bufLen];
        int cursor = bufLen;
        do {
            int q = i / radix;
            cursor--;
            buf[cursor] = DIGITS[(radix * q) - i];
            i = q;
        } while (i != 0);
        if (negative) {
            cursor--;
            buf[cursor] = '-';
        }
        return new String(cursor, bufLen - cursor, buf);
    }

    public static String intToString(int i) {
        return convertInt(null, i);
    }

    public static void appendInt(AbstractStringBuilder sb, int i) {
        convertInt(sb, i);
    }

    private static String convertInt(AbstractStringBuilder sb, int i) {
        boolean negative = false;
        String quickResult = null;
        String[] strArr;
        if (i < 0) {
            negative = true;
            i = -i;
            if (i < 100) {
                if (i < 0) {
                    quickResult = "-2147483648";
                } else {
                    quickResult = SMALL_NEGATIVE_VALUES[i];
                    if (quickResult == null) {
                        strArr = SMALL_NEGATIVE_VALUES;
                        quickResult = i < 10 ? stringOf('-', ONES[i]) : stringOf('-', TENS[i], ONES[i]);
                        strArr[i] = quickResult;
                    }
                }
            }
        } else if (i < 100) {
            quickResult = SMALL_NONNEGATIVE_VALUES[i];
            if (quickResult == null) {
                strArr = SMALL_NONNEGATIVE_VALUES;
                quickResult = i < 10 ? stringOf(ONES[i]) : stringOf(TENS[i], ONES[i]);
                strArr[i] = quickResult;
            }
        }
        if (quickResult == null) {
            int q;
            char[] buf = sb != null ? (char[]) BUFFER.get() : new char[11];
            int cursor = 11;
            while (i >= DateIntervalFormat.FORMAT_ABBREV_MONTH) {
                q = (int) ((1374389535 * ((long) i)) >>> 37);
                int r = i - (q * 100);
                cursor--;
                buf[cursor] = ONES[r];
                cursor--;
                buf[cursor] = TENS[r];
                i = q;
            }
            while (i != 0) {
                q = (52429 * i) >>> 19;
                cursor--;
                buf[cursor] = DIGITS[i - (q * 10)];
                i = q;
            }
            if (negative) {
                cursor--;
                buf[cursor] = '-';
            }
            if (sb == null) {
                return new String(cursor, 11 - cursor, buf);
            }
            sb.append0(buf, cursor, 11 - cursor);
            return null;
        } else if (sb == null) {
            return quickResult;
        } else {
            sb.append0(quickResult);
            return null;
        }
    }

    public static String longToString(long v, int radix) {
        int i = (int) v;
        if (((long) i) == v) {
            return intToString(i, radix);
        }
        if (radix < 2 || radix > 36) {
            radix = 10;
        }
        if (radix == 10) {
            return longToString(v);
        }
        boolean negative = false;
        if (v < 0) {
            negative = true;
        } else {
            v = -v;
        }
        int bufLen = radix < 8 ? 65 : 23;
        char[] buf = new char[bufLen];
        int cursor = bufLen;
        do {
            long q = v / ((long) radix);
            cursor--;
            buf[cursor] = DIGITS[(int) ((((long) radix) * q) - v)];
            v = q;
        } while (v != 0);
        if (negative) {
            cursor--;
            buf[cursor] = '-';
        }
        return new String(cursor, bufLen - cursor, buf);
    }

    public static String longToString(long l) {
        return convertLong(null, l);
    }

    public static void appendLong(AbstractStringBuilder sb, long l) {
        convertLong(sb, l);
    }

    private static String convertLong(AbstractStringBuilder sb, long n) {
        int i = (int) n;
        if (((long) i) == n) {
            return convertInt(sb, i);
        }
        boolean negative = n < 0;
        if (negative) {
            n = -n;
            if (n < 0) {
                String quickResult = "-9223372036854775808";
                if (sb == null) {
                    return quickResult;
                }
                sb.append0(quickResult);
                return null;
            }
        }
        char[] buf = sb != null ? (char[]) BUFFER.get() : new char[20];
        int low = (int) (n % 1000000000);
        int cursor = intIntoCharArray(buf, 20, low);
        while (cursor != 11) {
            cursor--;
            buf[cursor] = '0';
        }
        n = ((n - ((long) low)) >>> 9) * -8194354213138031507L;
        if ((-4294967296L & n) == 0) {
            cursor = intIntoCharArray(buf, cursor, (int) n);
        } else {
            int lo32 = (int) n;
            int midDigit = MOD_10_TABLE[(((429496729 * lo32) + (lo32 >>> 1)) + (lo32 >>> 3)) >>> 28] - (((int) (n >>> 32)) << 2);
            if (midDigit < 0) {
                midDigit += 10;
            }
            cursor--;
            buf[cursor] = DIGITS[midDigit];
            cursor = intIntoCharArray(buf, cursor, ((int) ((n - ((long) midDigit)) >>> 1)) * -858993459);
        }
        if (negative) {
            cursor--;
            buf[cursor] = '-';
        }
        if (sb == null) {
            return new String(cursor, 20 - cursor, buf);
        }
        sb.append0(buf, cursor, 20 - cursor);
        return null;
    }

    private static int intIntoCharArray(char[] buf, int cursor, int n) {
        while ((-65536 & n) != 0) {
            int q = (int) ((1374389535 * ((long) (n >>> 2))) >>> 35);
            int r = n - (q * 100);
            cursor--;
            buf[cursor] = ONES[r];
            cursor--;
            buf[cursor] = TENS[r];
            n = q;
        }
        while (n != 0) {
            q = (52429 * n) >>> 19;
            cursor--;
            buf[cursor] = DIGITS[n - (q * 10)];
            n = q;
        }
        return cursor;
    }

    public static String intToBinaryString(int i) {
        char[] buf = new char[32];
        int cursor = 32;
        do {
            cursor--;
            buf[cursor] = DIGITS[i & 1];
            i >>>= 1;
        } while (i != 0);
        return new String(cursor, 32 - cursor, buf);
    }

    public static String longToBinaryString(long v) {
        int i = (int) v;
        if (v >= 0 && ((long) i) == v) {
            return intToBinaryString(i);
        }
        char[] buf = new char[64];
        int cursor = 64;
        do {
            cursor--;
            buf[cursor] = DIGITS[((int) v) & 1];
            v >>>= 1;
        } while (v != 0);
        return new String(cursor, 64 - cursor, buf);
    }

    public static StringBuilder appendByteAsHex(StringBuilder sb, byte b, boolean upperCase) {
        char[] digits = upperCase ? UPPER_CASE_DIGITS : DIGITS;
        sb.append(digits[(b >> 4) & 15]);
        sb.append(digits[b & 15]);
        return sb;
    }

    public static String byteToHexString(byte b, boolean upperCase) {
        char[] digits = upperCase ? UPPER_CASE_DIGITS : DIGITS;
        return new String(0, 2, new char[]{digits[(b >> 4) & 15], digits[b & 15]});
    }

    public static String bytesToHexString(byte[] bytes, boolean upperCase) {
        char[] digits = upperCase ? UPPER_CASE_DIGITS : DIGITS;
        char[] buf = new char[(bytes.length * 2)];
        int c = 0;
        for (byte b : bytes) {
            int i = c + 1;
            buf[c] = digits[(b >> 4) & 15];
            c = i + 1;
            buf[i] = digits[b & 15];
        }
        return new String(buf);
    }

    public static String intToHexString(int i, boolean upperCase, int minWidth) {
        char[] buf = new char[8];
        int cursor = 8;
        char[] digits = upperCase ? UPPER_CASE_DIGITS : DIGITS;
        while (true) {
            cursor--;
            buf[cursor] = digits[i & 15];
            i >>>= 4;
            if (i == 0 && 8 - cursor >= minWidth) {
                return new String(cursor, 8 - cursor, buf);
            }
        }
    }

    public static String longToHexString(long v) {
        int i = (int) v;
        if (v >= 0 && ((long) i) == v) {
            return intToHexString(i, false, 0);
        }
        char[] buf = new char[16];
        int cursor = 16;
        do {
            cursor--;
            buf[cursor] = DIGITS[((int) v) & 15];
            v >>>= 4;
        } while (v != 0);
        return new String(cursor, 16 - cursor, buf);
    }

    public static String intToOctalString(int i) {
        char[] buf = new char[11];
        int cursor = 11;
        do {
            cursor--;
            buf[cursor] = DIGITS[i & 7];
            i >>>= 3;
        } while (i != 0);
        return new String(cursor, 11 - cursor, buf);
    }

    public static String longToOctalString(long v) {
        int i = (int) v;
        if (v >= 0 && ((long) i) == v) {
            return intToOctalString(i);
        }
        char[] buf = new char[22];
        int cursor = 22;
        do {
            cursor--;
            buf[cursor] = DIGITS[((int) v) & 7];
            v >>>= 3;
        } while (v != 0);
        return new String(cursor, 22 - cursor, buf);
    }

    private static String stringOf(char... args) {
        return new String(0, args.length, args);
    }
}
