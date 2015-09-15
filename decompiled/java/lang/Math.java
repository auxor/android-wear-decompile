package java.lang;

import java.util.Random;
import javax.xml.datatype.DatatypeConstants;

public final class Math {
    public static final double E = 2.718281828459045d;
    public static final double PI = 3.141592653589793d;
    private static Random random;

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

    private Math() {
    }

    public static double abs(double d) {
        return Double.longBitsToDouble(Double.doubleToRawLongBits(d) & Long.MAX_VALUE);
    }

    public static float abs(float f) {
        return Float.intBitsToFloat(Float.floatToRawIntBits(f) & Integer.MAX_VALUE);
    }

    public static int abs(int i) {
        return i >= 0 ? i : -i;
    }

    public static long abs(long l) {
        return l >= 0 ? l : -l;
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
        return Double.doubleToRawLongBits(d1) == 0 ? 0.0d : d2;
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
        return Float.floatToRawIntBits(f1) == 0 ? 0.0f : f2;
    }

    public static int max(int i1, int i2) {
        return i1 > i2 ? i1 : i2;
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
        if (Double.doubleToRawLongBits(d1) == Long.MIN_VALUE) {
            return -0.0d;
        }
        return d2;
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
        if (Float.floatToRawIntBits(f1) == DatatypeConstants.FIELD_UNDEFINED) {
            return -0.0f;
        }
        return f2;
    }

    public static int min(int i1, int i2) {
        return i1 < i2 ? i1 : i2;
    }

    public static long min(long l1, long l2) {
        return l1 < l2 ? l1 : l2;
    }

    public static long round(double d) {
        if (d != d) {
            return 0;
        }
        return (long) floor(0.5d + d);
    }

    public static int round(float f) {
        if (f != f) {
            return 0;
        }
        return (int) floor((double) (0.5f + f));
    }

    public static double signum(double d) {
        if (Double.isNaN(d)) {
            return Double.NaN;
        }
        double sig = d;
        if (d > 0.0d) {
            return 1.0d;
        }
        if (d < 0.0d) {
            return -1.0d;
        }
        return sig;
    }

    public static float signum(float f) {
        if (Float.isNaN(f)) {
            return Float.NaN;
        }
        float sig = f;
        if (f > 0.0f) {
            return 1.0f;
        }
        if (f < 0.0f) {
            return -1.0f;
        }
        return sig;
    }

    public static synchronized double random() {
        double nextDouble;
        synchronized (Math.class) {
            if (random == null) {
                random = new Random();
            }
            nextDouble = random.nextDouble();
        }
        return nextDouble;
    }

    public static double toRadians(double angdeg) {
        return (angdeg / 180.0d) * PI;
    }

    public static double toDegrees(double angrad) {
        return (180.0d * angrad) / PI;
    }

    public static double ulp(double d) {
        if (Double.isInfinite(d)) {
            return Double.POSITIVE_INFINITY;
        }
        if (d == Double.MAX_VALUE || d == -1.7976931348623157E308d) {
            return pow(2.0d, 971.0d);
        }
        d = abs(d);
        return nextafter(d, Double.MAX_VALUE) - d;
    }

    public static float ulp(float f) {
        int i = 1;
        if (Float.isNaN(f)) {
            return Float.NaN;
        }
        if (Float.isInfinite(f)) {
            return Float.POSITIVE_INFINITY;
        }
        if (f == Float.MAX_VALUE || f == -3.4028235E38f) {
            return (float) pow(2.0d, 104.0d);
        }
        f = abs(f);
        int hx = Float.floatToRawIntBits(f);
        int hy = Float.floatToRawIntBits(Float.MAX_VALUE);
        if ((Integer.MAX_VALUE & hx) == 0) {
            return Float.intBitsToFloat((DatatypeConstants.FIELD_UNDEFINED & hy) | 1);
        }
        int i2 = hx > 0 ? 1 : 0;
        if (hx <= hy) {
            i = 0;
        }
        if ((i ^ i2) != 0) {
            hx++;
        } else {
            hx--;
        }
        return Float.intBitsToFloat(hx) - f;
    }

    public static double copySign(double magnitude, double sign) {
        return Double.longBitsToDouble((Long.MAX_VALUE & Double.doubleToRawLongBits(magnitude)) | (Long.MIN_VALUE & Double.doubleToRawLongBits(sign)));
    }

    public static float copySign(float magnitude, float sign) {
        return Float.intBitsToFloat((Integer.MAX_VALUE & Float.floatToRawIntBits(magnitude)) | (DatatypeConstants.FIELD_UNDEFINED & Float.floatToRawIntBits(sign)));
    }

    public static int getExponent(float f) {
        return ((2139095040 & Float.floatToRawIntBits(f)) >> 23) - 127;
    }

    public static int getExponent(double d) {
        return ((int) ((9218868437227405312L & Double.doubleToRawLongBits(d)) >> 52)) - 1023;
    }

    public static double nextAfter(double start, double direction) {
        return (start == 0.0d && direction == 0.0d) ? direction : nextafter(start, direction);
    }

    public static float nextAfter(float start, double direction) {
        if (Float.isNaN(start) || Double.isNaN(direction)) {
            return Float.NaN;
        }
        if (start == 0.0f && direction == 0.0d) {
            return (float) direction;
        }
        if ((start != Float.MIN_VALUE || direction >= ((double) start)) && (start != -1.4E-45f || direction <= ((double) start))) {
            if (Float.isInfinite(start) && direction != ((double) start)) {
                return start > 0.0f ? Float.MAX_VALUE : -3.4028235E38f;
            } else {
                if ((start == Float.MAX_VALUE && direction > ((double) start)) || (start == -3.4028235E38f && direction < ((double) start))) {
                    return start > 0.0f ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
                } else {
                    if (direction > ((double) start)) {
                        if (start > 0.0f) {
                            return Float.intBitsToFloat(Float.floatToIntBits(start) + 1);
                        }
                        return start < 0.0f ? Float.intBitsToFloat(Float.floatToIntBits(start) - 1) : Float.MIN_VALUE;
                    } else if (direction >= ((double) start)) {
                        return (float) direction;
                    } else {
                        if (start > 0.0f) {
                            return Float.intBitsToFloat(Float.floatToIntBits(start) - 1);
                        }
                        return start < 0.0f ? Float.intBitsToFloat(Float.floatToIntBits(start) + 1) : -1.4E-45f;
                    }
                }
            }
        } else if (start <= 0.0f) {
            return -0.0f;
        } else {
            return 0.0f;
        }
    }

    public static double nextUp(double d) {
        if (Double.isNaN(d)) {
            return Double.NaN;
        }
        if (d == Double.POSITIVE_INFINITY) {
            return Double.POSITIVE_INFINITY;
        }
        if (d == 0.0d) {
            return Double.MIN_VALUE;
        }
        if (d > 0.0d) {
            return Double.longBitsToDouble(Double.doubleToLongBits(d) + 1);
        }
        return Double.longBitsToDouble(Double.doubleToLongBits(d) - 1);
    }

    public static float nextUp(float f) {
        if (Float.isNaN(f)) {
            return Float.NaN;
        }
        if (f == Float.POSITIVE_INFINITY) {
            return Float.POSITIVE_INFINITY;
        }
        if (f == 0.0f) {
            return Float.MIN_VALUE;
        }
        if (f > 0.0f) {
            return Float.intBitsToFloat(Float.floatToIntBits(f) + 1);
        }
        return Float.intBitsToFloat(Float.floatToIntBits(f) - 1);
    }

    public static double scalb(double d, int scaleFactor) {
        if (Double.isNaN(d) || Double.isInfinite(d) || d == 0.0d) {
            return d;
        }
        long bits = Double.doubleToLongBits(d);
        long sign = bits & Long.MIN_VALUE;
        long factor = (((9218868437227405312L & bits) >> 52) - 1023) + ((long) scaleFactor);
        int subNormalFactor = Long.numberOfLeadingZeros(Long.MAX_VALUE & bits) - 12;
        if (subNormalFactor < 0) {
            subNormalFactor = 0;
        } else {
            factor -= (long) subNormalFactor;
        }
        if (factor > 1023) {
            return d > 0.0d ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        } else {
            long result;
            if (factor <= -1023) {
                long digits = (1023 + factor) + ((long) subNormalFactor);
                if (abs(d) < Double.MIN_NORMAL) {
                    result = shiftLongBits(4503599627370495L & bits, digits);
                } else {
                    result = shiftLongBits((4503599627370495L & bits) | 4503599627370496L, digits - 1);
                }
            } else if (abs(d) >= Double.MIN_NORMAL) {
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
        } else {
            factor -= subNormalFactor;
        }
        if (factor > Float.MAX_EXPONENT) {
            return d > 0.0f ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
        } else {
            int result;
            if (factor <= -127) {
                int digits = (factor + Float.MAX_EXPONENT) + subNormalFactor;
                if (abs(d) < Float.MIN_NORMAL) {
                    result = shiftIntBits(bits & 8388607, digits);
                } else {
                    result = shiftIntBits((bits & 8388607) | 8388608, digits - 1);
                }
            } else if (abs(d) >= Float.MIN_NORMAL) {
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
        boolean halfBit;
        int ret = bits >> absDigits;
        if (((bits >> (absDigits - 1)) & 1) == 1) {
            halfBit = true;
        } else {
            halfBit = false;
        }
        if (!halfBit) {
            return ret;
        }
        if (Integer.numberOfTrailingZeros(bits) < absDigits - 1) {
            ret++;
        }
        if (Integer.numberOfTrailingZeros(bits) == absDigits - 1 && (ret & 1) == 1) {
            return ret + 1;
        }
        return ret;
    }

    private static long shiftLongBits(long bits, long digits) {
        if (digits > 0) {
            return bits << ((int) digits);
        }
        long absDigits = -digits;
        if (((long) Long.numberOfLeadingZeros(Long.MAX_VALUE & bits)) > 64 - absDigits) {
            return 0;
        }
        long ret = bits >> ((int) absDigits);
        if (!(((bits >> ((int) (absDigits - 1))) & 1) == 1)) {
            return ret;
        }
        if (((long) Long.numberOfTrailingZeros(bits)) < absDigits - 1) {
            ret++;
        }
        if (((long) Long.numberOfTrailingZeros(bits)) == absDigits - 1 && (ret & 1) == 1) {
            return ret + 1;
        }
        return ret;
    }
}
