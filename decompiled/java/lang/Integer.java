package java.lang;

import java.util.Locale;
import org.w3c.dom.traversal.NodeFilter;

@FindBugsSuppressWarnings({"DM_NUMBER_CTOR"})
public final class Integer extends Number implements Comparable<Integer> {
    public static final int MAX_VALUE = Integer.MAX_VALUE;
    public static final int MIN_VALUE = Integer.MIN_VALUE;
    private static final byte[] NTZ_TABLE = null;
    public static final int SIZE = 32;
    private static final Integer[] SMALL_VALUES = null;
    public static final Class<Integer> TYPE = null;
    private static final long serialVersionUID = 1360826667806852920L;
    private final int value;

    static {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.lang.Integer.<clinit>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.lang.Integer.<clinit>():void
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
        throw new UnsupportedOperationException("Method not decompiled: java.lang.Integer.<clinit>():void");
    }

    public Integer(int value) {
        this.value = value;
    }

    public Integer(String string) throws NumberFormatException {
        this(parseInt(string));
    }

    public byte byteValue() {
        return (byte) this.value;
    }

    public int compareTo(Integer object) {
        return compare(this.value, object.value);
    }

    public static int compare(int lhs, int rhs) {
        if (lhs < rhs) {
            return -1;
        }
        return lhs == rhs ? 0 : 1;
    }

    private static NumberFormatException invalidInt(String s) {
        throw new NumberFormatException("Invalid int: \"" + s + "\"");
    }

    public static Integer decode(String string) throws NumberFormatException {
        int length = string.length();
        if (length == 0) {
            throw invalidInt(string);
        }
        boolean negative;
        int i = 0;
        char firstDigit = string.charAt(0);
        if (firstDigit == '-') {
            negative = true;
        } else {
            negative = false;
        }
        if (negative || firstDigit == '+') {
            if (length == 1) {
                throw invalidInt(string);
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
            if (firstDigit == Locale.PRIVATE_USE_EXTENSION || firstDigit == 'X') {
                i++;
                if (i == length) {
                    throw invalidInt(string);
                }
                base = 16;
            } else {
                base = 8;
            }
        } else if (firstDigit == '#') {
            i++;
            if (i == length) {
                throw invalidInt(string);
            }
            base = 16;
        }
        return valueOf(parse(string, i, base, negative));
    }

    public double doubleValue() {
        return (double) this.value;
    }

    public boolean equals(Object o) {
        return (o instanceof Integer) && ((Integer) o).value == this.value;
    }

    public float floatValue() {
        return (float) this.value;
    }

    public static Integer getInteger(String string) {
        Integer num = null;
        if (!(string == null || string.length() == 0)) {
            String prop = System.getProperty(string);
            if (prop != null) {
                try {
                    num = decode(prop);
                } catch (NumberFormatException e) {
                }
            }
        }
        return num;
    }

    public static Integer getInteger(String string, int defaultValue) {
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

    public static Integer getInteger(String string, Integer defaultValue) {
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
        return this.value;
    }

    public int intValue() {
        return this.value;
    }

    public long longValue() {
        return (long) this.value;
    }

    public static int parseInt(String string) throws NumberFormatException {
        return parseInt(string, 10);
    }

    public static int parseInt(String string, int radix) throws NumberFormatException {
        boolean z = true;
        if (radix < 2 || radix > 36) {
            throw new NumberFormatException("Invalid radix: " + radix);
        } else if (string == null || string.isEmpty()) {
            throw invalidInt(string);
        } else {
            int firstDigitIndex;
            char firstChar = string.charAt(0);
            if (firstChar == '-' || firstChar == '+') {
                firstDigitIndex = 1;
            } else {
                firstDigitIndex = 0;
            }
            if (firstDigitIndex == string.length()) {
                throw invalidInt(string);
            }
            if (firstChar != '-') {
                z = false;
            }
            return parse(string, firstDigitIndex, radix, z);
        }
    }

    public static int parsePositiveInt(String string) throws NumberFormatException {
        return parsePositiveInt(string, 10);
    }

    public static int parsePositiveInt(String string, int radix) throws NumberFormatException {
        if (radix < 2 || radix > 36) {
            throw new NumberFormatException("Invalid radix: " + radix);
        } else if (string != null && string.length() != 0) {
            return parse(string, 0, radix, false);
        } else {
            throw invalidInt(string);
        }
    }

    private static int parse(String string, int offset, int radix, boolean negative) throws NumberFormatException {
        int max = MIN_VALUE / radix;
        int result = 0;
        int length = string.length();
        int offset2 = offset;
        while (offset2 < length) {
            offset = offset2 + 1;
            int digit = Character.digit(string.charAt(offset2), radix);
            if (digit == -1) {
                throw invalidInt(string);
            } else if (max > result) {
                throw invalidInt(string);
            } else {
                int next = (result * radix) - digit;
                if (next > result) {
                    throw invalidInt(string);
                }
                result = next;
                offset2 = offset;
            }
        }
        if (!negative) {
            result = -result;
            if (result < 0) {
                throw invalidInt(string);
            }
        }
        return result;
    }

    public short shortValue() {
        return (short) this.value;
    }

    public static String toBinaryString(int i) {
        return IntegralToString.intToBinaryString(i);
    }

    public static String toHexString(int i) {
        return IntegralToString.intToHexString(i, false, 0);
    }

    public static String toOctalString(int i) {
        return IntegralToString.intToOctalString(i);
    }

    public String toString() {
        return toString(this.value);
    }

    public static String toString(int i) {
        return IntegralToString.intToString(i);
    }

    public static String toString(int i, int radix) {
        return IntegralToString.intToString(i, radix);
    }

    public static Integer valueOf(String string) throws NumberFormatException {
        return valueOf(parseInt(string));
    }

    public static Integer valueOf(String string, int radix) throws NumberFormatException {
        return valueOf(parseInt(string, radix));
    }

    public static int highestOneBit(int i) {
        i |= i >> 1;
        i |= i >> 2;
        i |= i >> 4;
        i |= i >> 8;
        i |= i >> 16;
        return i - (i >>> 1);
    }

    public static int lowestOneBit(int i) {
        return (-i) & i;
    }

    public static int numberOfLeadingZeros(int i) {
        if (i <= 0) {
            return ((i ^ -1) >> 26) & SIZE;
        }
        int n = 1;
        if ((i >> 16) == 0) {
            n = 1 + 16;
            i <<= 16;
        }
        if ((i >> 24) == 0) {
            n += 8;
            i <<= 8;
        }
        if ((i >> 28) == 0) {
            n += 4;
            i <<= 4;
        }
        if ((i >> 30) == 0) {
            n += 2;
            i <<= 2;
        }
        return n - (i >>> 31);
    }

    public static int numberOfTrailingZeros(int i) {
        return NTZ_TABLE[(((-i) & i) * 72416175) >>> 26];
    }

    public static int bitCount(int i) {
        i -= (i >> 1) & 1431655765;
        i = (i & 858993459) + ((i >> 2) & 858993459);
        i = ((i >> 4) + i) & 252645135;
        i += i >> 8;
        return (i + (i >> 16)) & 63;
    }

    public static int rotateLeft(int i, int distance) {
        return (i << distance) | (i >>> (-distance));
    }

    public static int rotateRight(int i, int distance) {
        return (i >>> distance) | (i << (-distance));
    }

    public static int reverseBytes(int i) {
        i = ((i >>> 8) & 16711935) | ((16711935 & i) << 8);
        return (i >>> 16) | (i << 16);
    }

    public static int reverse(int i) {
        i = ((i >>> 1) & 1431655765) | ((1431655765 & i) << 1);
        i = ((i >>> 2) & 858993459) | ((i & 858993459) << 2);
        i = ((i >>> 4) & 252645135) | ((i & 252645135) << 4);
        i = ((i >>> 8) & 16711935) | ((i & 16711935) << 8);
        return (i >>> 16) | (i << 16);
    }

    public static int signum(int i) {
        return (i >> 31) | ((-i) >>> 31);
    }

    public static Integer valueOf(int i) {
        return (i >= NodeFilter.SHOW_COMMENT || i < -128) ? new Integer(i) : SMALL_VALUES[i + NodeFilter.SHOW_COMMENT];
    }
}
