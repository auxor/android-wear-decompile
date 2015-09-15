package java.lang;

public final class Double extends Number implements Comparable<Double> {
    static final int EXPONENT_BIAS = 1023;
    static final int EXPONENT_BITS = 12;
    static final long EXPONENT_MASK = 9218868437227405312L;
    static final int MANTISSA_BITS = 52;
    static final long MANTISSA_MASK = 4503599627370495L;
    public static final int MAX_EXPONENT = 1023;
    public static final double MAX_VALUE = Double.MAX_VALUE;
    public static final int MIN_EXPONENT = -1022;
    public static final double MIN_NORMAL = Double.MIN_NORMAL;
    public static final double MIN_VALUE = Double.MIN_VALUE;
    public static final double NEGATIVE_INFINITY = Double.NEGATIVE_INFINITY;
    static final int NON_MANTISSA_BITS = 12;
    public static final double NaN = Double.NaN;
    public static final double POSITIVE_INFINITY = Double.POSITIVE_INFINITY;
    static final long SIGN_MASK = Long.MIN_VALUE;
    public static final int SIZE = 64;
    public static final Class<Double> TYPE;
    private static final long serialVersionUID = -9172774392245257468L;
    private final double value;

    public static native long doubleToRawLongBits(double d);

    public static native double longBitsToDouble(long j);

    static {
        TYPE = double[].class.getComponentType();
    }

    public Double(double value) {
        this.value = value;
    }

    public Double(String string) throws NumberFormatException {
        this(parseDouble(string));
    }

    public int compareTo(Double object) {
        return compare(this.value, object.value);
    }

    public byte byteValue() {
        return (byte) ((int) this.value);
    }

    public static long doubleToLongBits(double value) {
        if (value != value) {
            return 9221120237041090560L;
        }
        return doubleToRawLongBits(value);
    }

    public double doubleValue() {
        return this.value;
    }

    public boolean equals(Object object) {
        return (object instanceof Double) && doubleToLongBits(this.value) == doubleToLongBits(((Double) object).value);
    }

    public float floatValue() {
        return (float) this.value;
    }

    public int hashCode() {
        long v = doubleToLongBits(this.value);
        return (int) ((v >>> 32) ^ v);
    }

    public int intValue() {
        return (int) this.value;
    }

    public boolean isInfinite() {
        return isInfinite(this.value);
    }

    public static boolean isInfinite(double d) {
        return d == POSITIVE_INFINITY || d == NEGATIVE_INFINITY;
    }

    public boolean isNaN() {
        return isNaN(this.value);
    }

    public static boolean isNaN(double d) {
        return d != d;
    }

    public long longValue() {
        return (long) this.value;
    }

    public static double parseDouble(String string) throws NumberFormatException {
        return StringToReal.parseDouble(string);
    }

    public short shortValue() {
        return (short) ((int) this.value);
    }

    public String toString() {
        return toString(this.value);
    }

    public static String toString(double d) {
        return RealToString.getInstance().doubleToString(d);
    }

    public static Double valueOf(String string) throws NumberFormatException {
        return valueOf(parseDouble(string));
    }

    public static int compare(double double1, double double2) {
        if (double1 > double2) {
            return 1;
        }
        if (double2 > double1) {
            return -1;
        }
        if (double1 == double2 && 0.0d != double1) {
            return 0;
        }
        if (isNaN(double1)) {
            if (isNaN(double2)) {
                return 0;
            }
            return 1;
        } else if (isNaN(double2)) {
            return -1;
        } else {
            return (int) ((doubleToRawLongBits(double1) >> 63) - (doubleToRawLongBits(double2) >> 63));
        }
    }

    public static Double valueOf(double d) {
        return new Double(d);
    }

    public static String toHexString(double d) {
        if (d != d) {
            return "NaN";
        }
        if (d == POSITIVE_INFINITY) {
            return "Infinity";
        }
        if (d == NEGATIVE_INFINITY) {
            return "-Infinity";
        }
        long bitValue = doubleToLongBits(d);
        boolean negative = (SIGN_MASK & bitValue) != 0;
        long exponent = (EXPONENT_MASK & bitValue) >>> MANTISSA_BITS;
        long significand = bitValue & MANTISSA_MASK;
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
                fractionDigits = 13;
                while (significand != 0 && (15 & significand) == 0) {
                    significand >>>= 4;
                    fractionDigits--;
                }
                hexSignificand = Long.toHexString(significand);
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
                hexString.append("p-1022");
            } else {
                hexString.append("1.");
                fractionDigits = 13;
                while (significand != 0 && (15 & significand) == 0) {
                    significand >>>= 4;
                    fractionDigits--;
                }
                hexSignificand = Long.toHexString(significand);
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
                hexString.append(Long.toString(exponent - 1023));
            }
            return hexString.toString();
        } else if (negative) {
            return "-0x0.0p0";
        } else {
            return "0x0.0p0";
        }
    }
}
