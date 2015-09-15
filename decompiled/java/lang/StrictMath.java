package java.lang;

import javax.xml.datatype.DatatypeConstants;

public final class StrictMath {
    public static final double E = 2.718281828459045d;
    public static final double PI = 3.141592653589793d;

    public static native double IEEEremainder(double d, double d2);

    public static native double acos(double d);

    public static native double asin(double d);

    public static native double atan(double d);

    public static native double atan2(double d, double d2);

    public static native double cbrt(double d);

    public static native double ceil(double d);

    public static native double cos(double d);

    public static native double cosh(double d);

    public static native double exp(double d);

    public static native double expm1(double d);

    public static native double floor(double d);

    public static native double hypot(double d, double d2);

    public static native double log(double d);

    public static native double log10(double d);

    public static native double log1p(double d);

    private static native double nextafter(double d, double d2);

    public static native double pow(double d, double d2);

    public static native double rint(double d);

    public static native double sin(double d);

    public static native double sinh(double d);

    public static native double sqrt(double d);

    public static native double tan(double d);

    public static native double tanh(double d);

    private StrictMath() {
    }

    public static double abs(double d) {
        return Math.abs(d);
    }

    public static float abs(float f) {
        return Math.abs(f);
    }

    public static int abs(int i) {
        return Math.abs(i);
    }

    public static long abs(long l) {
        return Math.abs(l);
    }

    public static double max(double d1, double d2) {
        if (d1 > d2) {
            return d1;
        }
        if (d1 < d2) {
            return d2;
        }
        if (d1 != d2) {
            return Double.NaN;
        }
        if (d1 == 0.0d && ((Double.doubleToLongBits(d1) & Double.doubleToLongBits(d2)) & Long.MIN_VALUE) == 0) {
            return 0.0d;
        }
        return d1;
    }

    public static float max(float f1, float f2) {
        if (f1 > f2) {
            return f1;
        }
        if (f1 < f2) {
            return f2;
        }
        if (f1 != f2) {
            return Float.NaN;
        }
        if (f1 == 0.0f && ((Float.floatToIntBits(f1) & Float.floatToIntBits(f2)) & DatatypeConstants.FIELD_UNDEFINED) == 0) {
            return 0.0f;
        }
        return f1;
    }

    public static int max(int i1, int i2) {
        return Math.max(i1, i2);
    }

    public static long max(long l1, long l2) {
        return l1 > l2 ? l1 : l2;
    }

    public static double min(double d1, double d2) {
        if (d1 > d2) {
            return d2;
        }
        if (d1 < d2) {
            return d1;
        }
        if (d1 != d2) {
            return Double.NaN;
        }
        if (d1 != 0.0d || ((Double.doubleToLongBits(d1) | Double.doubleToLongBits(d2)) & Long.MIN_VALUE) == 0) {
            return d1;
        }
        return -0.0d;
    }

    public static float min(float f1, float f2) {
        if (f1 > f2) {
            return f2;
        }
        if (f1 < f2) {
            return f1;
        }
        if (f1 != f2) {
            return Float.NaN;
        }
        if (f1 != 0.0f || ((Float.floatToIntBits(f1) | Float.floatToIntBits(f2)) & DatatypeConstants.FIELD_UNDEFINED) == 0) {
            return f1;
        }
        return -0.0f;
    }

    public static int min(int i1, int i2) {
        return Math.min(i1, i2);
    }

    public static long min(long l1, long l2) {
        return l1 < l2 ? l1 : l2;
    }

    public static double random() {
        return Math.random();
    }

    public static long round(double d) {
        return Math.round(d);
    }

    public static int round(float f) {
        return Math.round(f);
    }

    public static double signum(double d) {
        return Math.signum(d);
    }

    public static float signum(float f) {
        return Math.signum(f);
    }

    public static double toDegrees(double angrad) {
        return Math.toDegrees(angrad);
    }

    public static double toRadians(double angdeg) {
        return Math.toRadians(angdeg);
    }

    public static double ulp(double d) {
        if (Double.isInfinite(d)) {
            return Double.POSITIVE_INFINITY;
        }
        if (d == Double.MAX_VALUE || d == -1.7976931348623157E308d) {
            return pow(2.0d, 971.0d);
        }
        d = Math.abs(d);
        return nextafter(d, Double.MAX_VALUE) - d;
    }

    public static float ulp(float f) {
        return Math.ulp(f);
    }

    public static double copySign(double magnitude, double sign) {
        long magnitudeBits = Double.doubleToRawLongBits(magnitude);
        if (sign != sign) {
            sign = 1.0d;
        }
        return Double.longBitsToDouble((Long.MAX_VALUE & magnitudeBits) | (Long.MIN_VALUE & Double.doubleToRawLongBits(sign)));
    }

    public static float copySign(float magnitude, float sign) {
        int magnitudeBits = Float.floatToRawIntBits(magnitude);
        if (sign != sign) {
            sign = 1.0f;
        }
        return Float.intBitsToFloat((Integer.MAX_VALUE & magnitudeBits) | (DatatypeConstants.FIELD_UNDEFINED & Float.floatToRawIntBits(sign)));
    }

    public static int getExponent(float f) {
        return Math.getExponent(f);
    }

    public static int getExponent(double d) {
        return Math.getExponent(d);
    }

    public static double nextAfter(double start, double direction) {
        return (start == 0.0d && direction == 0.0d) ? direction : nextafter(start, direction);
    }

    public static float nextAfter(float start, double direction) {
        return Math.nextAfter(start, direction);
    }

    public static double nextUp(double d) {
        return Math.nextUp(d);
    }

    public static float nextUp(float f) {
        return Math.nextUp(f);
    }

    public static double scalb(double d, int scaleFactor) {
        if (Double.isNaN(d) || Double.isInfinite(d) || d == 0.0d) {
            return d;
        }
        long bits = Double.doubleToLongBits(d);
        long sign = bits & Long.MIN_VALUE;
        long factor = (long) ((((int) ((9218868437227405312L & bits) >> 52)) - 1023) + scaleFactor);
        int subNormalFactor = Long.numberOfLeadingZeros(Long.MAX_VALUE & bits) - 12;
        if (subNormalFactor < 0) {
            subNormalFactor = 0;
        }
        if (Math.abs(d) < Double.MIN_NORMAL) {
            factor -= (long) subNormalFactor;
        }
        if (factor > 1023) {
            return d > 0.0d ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        } else {
            long result;
            if (factor < -1023) {
                long digits = (1023 + factor) + ((long) subNormalFactor);
                if (Math.abs(d) < Double.MIN_NORMAL) {
                    result = shiftLongBits(4503599627370495L & bits, digits);
                } else {
                    result = shiftLongBits((4503599627370495L & bits) | 4503599627370496L, digits - 1);
                }
            } else if (Math.abs(d) >= Double.MIN_NORMAL) {
                result = ((1023 + factor) << 52) | (4503599627370495L & bits);
            } else {
                result = ((1023 + factor) << 52) | ((bits << (subNormalFactor + 1)) & 4503599627370495L);
            }
            return Double.longBitsToDouble(result | sign);
        }
    }

    public static float scalb(float d, int scaleFactor) {
        if (Float.isNaN(d) || Float.isInfinite(d) || d == 0.0f) {
            return d;
        }
        int bits = Float.floatToIntBits(d);
        int sign = bits & DatatypeConstants.FIELD_UNDEFINED;
        int factor = (((2139095040 & bits) >> 23) - 127) + scaleFactor;
        int subNormalFactor = Integer.numberOfLeadingZeros(Integer.MAX_VALUE & bits) - 9;
        if (subNormalFactor < 0) {
            subNormalFactor = 0;
        }
        if (Math.abs(d) < Float.MIN_NORMAL) {
            factor -= subNormalFactor;
        }
        if (factor > Float.MAX_EXPONENT) {
            return d > 0.0f ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
        } else {
            int result;
            if (factor < -127) {
                int digits = (factor + Float.MAX_EXPONENT) + subNormalFactor;
                if (Math.abs(d) < Float.MIN_NORMAL) {
                    result = shiftIntBits(bits & 8388607, digits);
                } else {
                    result = shiftIntBits((bits & 8388607) | 8388608, digits - 1);
                }
            } else if (Math.abs(d) >= Float.MIN_NORMAL) {
                result = ((factor + Float.MAX_EXPONENT) << 23) | (bits & 8388607);
            } else {
                result = ((factor + Float.MAX_EXPONENT) << 23) | ((bits << (subNormalFactor + 1)) & 8388607);
            }
            return Float.intBitsToFloat(result | sign);
        }
    }

    private static int shiftIntBits(int bits, int digits) {
        if (digits > 0) {
            return bits << digits;
        }
        int absDigits = -digits;
        if (Integer.numberOfLeadingZeros(Integer.MAX_VALUE & bits) > 32 - absDigits) {
            return 0;
        }
        if (((bits >> (absDigits - 1)) & 1) == 0 || Integer.numberOfTrailingZeros(bits) == absDigits - 1) {
            return bits >> absDigits;
        }
        return (bits >> absDigits) + 1;
    }

    private static long shiftLongBits(long bits, long digits) {
        if (digits > 0) {
            return bits << ((int) digits);
        }
        long absDigits = -digits;
        if (((long) Long.numberOfLeadingZeros(Long.MAX_VALUE & bits)) > 64 - absDigits) {
            return 0;
        }
        if (((bits >> ((int) (absDigits - 1))) & 1) == 0 || ((long) Long.numberOfTrailingZeros(bits)) == absDigits - 1) {
            return bits >> ((int) absDigits);
        }
        return (bits >> ((int) absDigits)) + 1;
    }
}
