package java.lang;

public final class Float extends Number implements Comparable<Float> {
    static final int EXPONENT_BIAS = 127;
    static final int EXPONENT_BITS = 9;
    static final int EXPONENT_MASK = 2139095040;
    static final int MANTISSA_BITS = 23;
    static final int MANTISSA_MASK = 8388607;
    public static final int MAX_EXPONENT = 127;
    public static final float MAX_VALUE = Float.MAX_VALUE;
    public static final int MIN_EXPONENT = -126;
    public static final float MIN_NORMAL = Float.MIN_NORMAL;
    public static final float MIN_VALUE = Float.MIN_VALUE;
    public static final float NEGATIVE_INFINITY = Float.NEGATIVE_INFINITY;
    static final int NON_MANTISSA_BITS = 9;
    public static final float NaN = Float.NaN;
    public static final float POSITIVE_INFINITY = Float.POSITIVE_INFINITY;
    static final int SIGN_MASK = Integer.MIN_VALUE;
    public static final int SIZE = 32;
    public static final Class<Float> TYPE;
    private static final long serialVersionUID = -2671257302660747028L;
    private final float value;

    public static native int floatToRawIntBits(float f);

    public static native float intBitsToFloat(int i);

    static {
        TYPE = float[].class.getComponentType();
    }

    public Float(float value) {
        this.value = value;
    }

    public Float(double value) {
        this.value = (float) value;
    }

    public Float(String string) throws NumberFormatException {
        this(parseFloat(string));
    }

    public int compareTo(Float object) {
        return compare(this.value, object.value);
    }

    public byte byteValue() {
        return (byte) ((int) this.value);
    }

    public double doubleValue() {
        return (double) this.value;
    }

    public boolean equals(Object object) {
        return (object instanceof Float) && floatToIntBits(this.value) == floatToIntBits(((Float) object).value);
    }

    public static int floatToIntBits(float value) {
        if (value != value) {
            return 2143289344;
        }
        return floatToRawIntBits(value);
    }

    public float floatValue() {
        return this.value;
    }

    public int hashCode() {
        return floatToIntBits(this.value);
    }

    public int intValue() {
        return (int) this.value;
    }

    public boolean isInfinite() {
        return isInfinite(this.value);
    }

    public static boolean isInfinite(float f) {
        return f == POSITIVE_INFINITY || f == NEGATIVE_INFINITY;
    }

    public boolean isNaN() {
        return isNaN(this.value);
    }

    public static boolean isNaN(float f) {
        return f != f;
    }

    public long longValue() {
        return (long) this.value;
    }

    public static float parseFloat(String string) throws NumberFormatException {
        return StringToReal.parseFloat(string);
    }

    public short shortValue() {
        return (short) ((int) this.value);
    }

    public String toString() {
        return toString(this.value);
    }

    public static String toString(float f) {
        return RealToString.getInstance().floatToString(f);
    }

    public static Float valueOf(String string) throws NumberFormatException {
        return valueOf(parseFloat(string));
    }

    public static int compare(float float1, float float2) {
        if (float1 > float2) {
            return 1;
        }
        if (float2 > float1) {
            return -1;
        }
        if (float1 == float2 && 0.0f != float1) {
            return 0;
        }
        if (isNaN(float1)) {
            if (isNaN(float2)) {
                return 0;
            }
            return 1;
        } else if (isNaN(float2)) {
            return -1;
        } else {
            return (floatToRawIntBits(float1) >> 31) - (floatToRawIntBits(float2) >> 31);
        }
    }

    public static Float valueOf(float f) {
        return new Float(f);
    }

    public static String toHexString(float f) {
        if (f != f) {
            return "NaN";
        }
        if (f == POSITIVE_INFINITY) {
            return "Infinity";
        }
        if (f == NEGATIVE_INFINITY) {
            return "-Infinity";
        }
        int bitValue = floatToIntBits(f);
        boolean negative = (SIGN_MASK & bitValue) != 0;
        int exponent = (EXPONENT_MASK & bitValue) >>> MANTISSA_BITS;
        int significand = (MANTISSA_MASK & bitValue) << 1;
        if (exponent != 0 || significand != 0) {
            StringBuilder hexString = new StringBuilder(10);
            if (negative) {
                hexString.append("-0x");
            } else {
                hexString.append("0x");
            }
            int fractionDigits;
            String hexSignificand;
            int digitDiff;
            int digitDiff2;
            if (exponent == 0) {
                hexString.append("0.");
                fractionDigits = 6;
                while (significand != 0 && (significand & 15) == 0) {
                    significand >>>= 4;
                    fractionDigits--;
                }
                hexSignificand = Integer.toHexString(significand);
                if (significand != 0 && fractionDigits > hexSignificand.length()) {
                    digitDiff = fractionDigits - hexSignificand.length();
                    while (true) {
                        digitDiff2 = digitDiff - 1;
                        if (digitDiff == 0) {
                            break;
                        }
                        hexString.append('0');
                        digitDiff = digitDiff2;
                    }
                }
                hexString.append(hexSignificand);
                hexString.append("p-126");
            } else {
                hexString.append("1.");
                fractionDigits = 6;
                while (significand != 0 && (significand & 15) == 0) {
                    significand >>>= 4;
                    fractionDigits--;
                }
                hexSignificand = Integer.toHexString(significand);
                if (significand != 0 && fractionDigits > hexSignificand.length()) {
                    digitDiff = fractionDigits - hexSignificand.length();
                    while (true) {
                        digitDiff2 = digitDiff - 1;
                        if (digitDiff == 0) {
                            break;
                        }
                        hexString.append('0');
                        digitDiff = digitDiff2;
                    }
                }
                hexString.append(hexSignificand);
                hexString.append('p');
                hexString.append(exponent - 127);
            }
            return hexString.toString();
        } else if (negative) {
            return "-0x0.0p0";
        } else {
            return "0x0.0p0";
        }
    }
}
