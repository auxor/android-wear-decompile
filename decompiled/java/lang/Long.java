package java.lang;

import java.util.Locale;
import org.w3c.dom.traversal.NodeFilter;

@FindBugsSuppressWarnings({"DM_NUMBER_CTOR"})
public final class Long extends Number implements Comparable<Long> {
    public static final long MAX_VALUE = Long.MAX_VALUE;
    public static final long MIN_VALUE = Long.MIN_VALUE;
    public static final int SIZE = 64;
    private static final Long[] SMALL_VALUES = null;
    public static final Class<Long> TYPE = null;
    private static final long serialVersionUID = 4290774380558885855L;
    private final long value;

    static {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.lang.Long.<clinit>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.lang.Long.<clinit>():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: java.lang.Long.<clinit>():void");
    }

    public Long(long value) {
        this.value = value;
    }

    public Long(String string) throws NumberFormatException {
        this(parseLong(string));
    }

    public byte byteValue() {
        return (byte) ((int) this.value);
    }

    public int compareTo(Long object) {
        return compare(this.value, object.value);
    }

    public static int compare(long lhs, long rhs) {
        if (lhs < rhs) {
            return -1;
        }
        return lhs == rhs ? 0 : 1;
    }

    private static NumberFormatException invalidLong(String s) {
        throw new NumberFormatException("Invalid long: \"" + s + "\"");
    }

    public static Long decode(String string) throws NumberFormatException {
        int length = string.length();
        if (length == 0) {
            throw invalidLong(string);
        }
        int i = 0;
        char firstDigit = string.charAt(0);
        boolean negative = firstDigit == '-';
        if (negative || firstDigit == '+') {
            if (length == 1) {
                throw invalidLong(string);
            }
            i = 0 + 1;
            firstDigit = string.charAt(i);
        }
        int base = 10;
        if (firstDigit == '0') {
            i++;
            if (i == length) {
                return valueOf(0);
            }
            firstDigit = string.charAt(i);
            if (firstDigit != Locale.PRIVATE_USE_EXTENSION && firstDigit != 'X') {
                base = 8;
            } else if (i == length) {
                throw invalidLong(string);
            } else {
                i++;
                base = 16;
            }
        } else if (firstDigit == '#') {
            if (i == length) {
                throw invalidLong(string);
            }
            i++;
            base = 16;
        }
        return valueOf(parse(string, i, base, negative));
    }

    public double doubleValue() {
        return (double) this.value;
    }

    public boolean equals(Object o) {
        return (o instanceof Long) && ((Long) o).value == this.value;
    }

    public float floatValue() {
        return (float) this.value;
    }

    public static Long getLong(String string) {
        Long l = null;
        if (!(string == null || string.length() == 0)) {
            String prop = System.getProperty(string);
            if (prop != null) {
                try {
                    l = decode(prop);
                } catch (NumberFormatException e) {
                }
            }
        }
        return l;
    }

    public static Long getLong(String string, long defaultValue) {
        if (string == null || string.length() == 0) {
            return valueOf(defaultValue);
        }
        String prop = System.getProperty(string);
        if (prop == null) {
            return valueOf(defaultValue);
        }
        try {
            return decode(prop);
        } catch (NumberFormatException e) {
            return valueOf(defaultValue);
        }
    }

    public static Long getLong(String string, Long defaultValue) {
        if (!(string == null || string.length() == 0)) {
            String prop = System.getProperty(string);
            if (prop != null) {
                try {
                    defaultValue = decode(prop);
                } catch (NumberFormatException e) {
                }
            }
        }
        return defaultValue;
    }

    public int hashCode() {
        return (int) (this.value ^ (this.value >>> 32));
    }

    public int intValue() {
        return (int) this.value;
    }

    public long longValue() {
        return this.value;
    }

    public static long parseLong(String string) throws NumberFormatException {
        return parseLong(string, 10);
    }

    public static long parseLong(String string, int radix) throws NumberFormatException {
        boolean z = true;
        if (radix < 2 || radix > 36) {
            throw new NumberFormatException("Invalid radix: " + radix);
        } else if (string == null || string.isEmpty()) {
            throw invalidLong(string);
        } else {
            int firstDigitIndex;
            char firstChar = string.charAt(0);
            if (firstChar == '-' || firstChar == '+') {
                firstDigitIndex = 1;
            } else {
                firstDigitIndex = 0;
            }
            if (firstDigitIndex == string.length()) {
                throw invalidLong(string);
            }
            if (firstChar != '-') {
                z = false;
            }
            return parse(string, firstDigitIndex, radix, z);
        }
    }

    private static long parse(String string, int offset, int radix, boolean negative) {
        long max = MIN_VALUE / ((long) radix);
        long result = 0;
        int length = string.length();
        int i = offset;
        while (i < length) {
            offset = i + 1;
            int digit = Character.digit(string.charAt(i), radix);
            if (digit == -1) {
                throw invalidLong(string);
            } else if (max > result) {
                throw invalidLong(string);
            } else {
                long next = (((long) radix) * result) - ((long) digit);
                if (next > result) {
                    throw invalidLong(string);
                }
                result = next;
                i = offset;
            }
        }
        if (!negative) {
            result = -result;
            if (result < 0) {
                throw invalidLong(string);
            }
        }
        return result;
    }

    public static long parsePositiveLong(String string) throws NumberFormatException {
        return parsePositiveLong(string, 10);
    }

    public static long parsePositiveLong(String string, int radix) throws NumberFormatException {
        if (radix < 2 || radix > 36) {
            throw new NumberFormatException("Invalid radix: " + radix);
        } else if (string != null && string.length() != 0) {
            return parse(string, 0, radix, false);
        } else {
            throw invalidLong(string);
        }
    }

    public short shortValue() {
        return (short) ((int) this.value);
    }

    public static String toBinaryString(long v) {
        return IntegralToString.longToBinaryString(v);
    }

    public static String toHexString(long v) {
        return IntegralToString.longToHexString(v);
    }

    public static String toOctalString(long v) {
        return IntegralToString.longToOctalString(v);
    }

    public String toString() {
        return toString(this.value);
    }

    public static String toString(long n) {
        return IntegralToString.longToString(n);
    }

    public static String toString(long v, int radix) {
        return IntegralToString.longToString(v, radix);
    }

    public static Long valueOf(String string) throws NumberFormatException {
        return valueOf(parseLong(string));
    }

    public static Long valueOf(String string, int radix) throws NumberFormatException {
        return valueOf(parseLong(string, radix));
    }

    public static long highestOneBit(long v) {
        v |= v >> 1;
        v |= v >> 2;
        v |= v >> 4;
        v |= v >> 8;
        v |= v >> 16;
        v |= v >> 32;
        return v - (v >>> 1);
    }

    public static long lowestOneBit(long v) {
        return (-v) & v;
    }

    public static int numberOfLeadingZeros(long v) {
        if (v < 0) {
            return 0;
        }
        if (v == 0) {
            return SIZE;
        }
        int n = 1;
        int i = (int) (v >>> 32);
        if (i == 0) {
            n = 1 + 32;
            i = (int) v;
        }
        if ((i >>> 16) == 0) {
            n += 16;
            i <<= 16;
        }
        if ((i >>> 24) == 0) {
            n += 8;
            i <<= 8;
        }
        if ((i >>> 28) == 0) {
            n += 4;
            i <<= 4;
        }
        if ((i >>> 30) == 0) {
            n += 2;
            i <<= 2;
        }
        return n - (i >>> 31);
    }

    public static int numberOfTrailingZeros(long v) {
        int low = (int) v;
        return low != 0 ? Integer.numberOfTrailingZeros(low) : Integer.numberOfTrailingZeros((int) (v >>> 32)) + 32;
    }

    public static int bitCount(long v) {
        v -= (v >>> 1) & 6148914691236517205L;
        v = (v & 3689348814741910323L) + ((v >>> 2) & 3689348814741910323L);
        int i = ((int) (v >>> 32)) + ((int) v);
        i = (i & 252645135) + ((i >>> 4) & 252645135);
        i += i >>> 8;
        return (i + (i >>> 16)) & Float.MAX_EXPONENT;
    }

    public static long rotateLeft(long v, int distance) {
        return (v << distance) | (v >>> (-distance));
    }

    public static long rotateRight(long v, int distance) {
        return (v >>> distance) | (v << (-distance));
    }

    public static long reverseBytes(long v) {
        v = ((v >>> 8) & 71777214294589695L) | ((71777214294589695L & v) << 8);
        v = ((v >>> 16) & 281470681808895L) | ((v & 281470681808895L) << 16);
        return (v >>> 32) | (v << 32);
    }

    public static long reverse(long v) {
        v = ((v >>> 1) & 6148914691236517205L) | ((6148914691236517205L & v) << 1);
        v = ((v >>> 2) & 3689348814741910323L) | ((3689348814741910323L & v) << 2);
        v = ((v >>> 4) & 1085102592571150095L) | ((1085102592571150095L & v) << 4);
        v = ((v >>> 8) & 71777214294589695L) | ((71777214294589695L & v) << 8);
        v = ((v >>> 16) & 281470681808895L) | ((281470681808895L & v) << 16);
        return (v >>> 32) | (v << 32);
    }

    public static int signum(long v) {
        if (v < 0) {
            return -1;
        }
        return v == 0 ? 0 : 1;
    }

    public static Long valueOf(long v) {
        return (v >= 128 || v < -128) ? new Long(v) : SMALL_VALUES[((int) v) + NodeFilter.SHOW_COMMENT];
    }
}
