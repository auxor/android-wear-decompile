package java.lang;

import dalvik.bytecode.Opcodes;
import javax.xml.datatype.DatatypeConstants;

final class RealToString {
    private static final ThreadLocal<RealToString> INSTANCE;
    private static final double invLogOfTenBaseTwo;
    private int digitCount;
    private final int[] digits;
    private int firstK;

    private native void bigIntDigitGenerator(long j, int i, boolean z, int i2);

    static {
        INSTANCE = new ThreadLocal<RealToString>() {
            protected RealToString initialValue() {
                return new RealToString();
            }
        };
        invLogOfTenBaseTwo = Math.log(2.0d) / Math.log(10.0d);
    }

    private RealToString() {
        this.digits = new int[64];
    }

    public static RealToString getInstance() {
        return (RealToString) INSTANCE.get();
    }

    private static String resultOrSideEffect(AbstractStringBuilder sb, String s) {
        if (sb == null) {
            return s;
        }
        sb.append0(s);
        return null;
    }

    public String doubleToString(double d) {
        return convertDouble(null, d);
    }

    public void appendDouble(AbstractStringBuilder sb, double d) {
        convertDouble(sb, d);
    }

    private String convertDouble(AbstractStringBuilder sb, double inputNumber) {
        long inputNumberBits = Double.doubleToRawLongBits(inputNumber);
        boolean positive = (Long.MIN_VALUE & inputNumberBits) == 0;
        int e = (int) ((9218868437227405312L & inputNumberBits) >> 52);
        long f = inputNumberBits & 4503599627370495L;
        boolean mantissaIsZero = f == 0;
        String quickResult = null;
        if (e == Opcodes.OP_IGET_WIDE_JUMBO) {
            quickResult = mantissaIsZero ? positive ? "Infinity" : "-Infinity" : "NaN";
        } else if (e == 0) {
            if (mantissaIsZero) {
                quickResult = positive ? "0.0" : "-0.0";
            } else if (f == 1) {
                quickResult = positive ? "4.9E-324" : "-4.9E-324";
            }
        }
        if (quickResult != null) {
            return resultOrSideEffect(sb, quickResult);
        }
        int pow;
        int numBits = 52;
        if (e == 0) {
            pow = 1 - 1075;
            long ff = f;
            while ((4503599627370496L & ff) == 0) {
                ff <<= 1;
                numBits--;
            }
        } else {
            f |= 4503599627370496L;
            pow = e - 1075;
        }
        this.digitCount = 0;
        this.firstK = 0;
        if ((-59 >= pow || pow >= 6) && (pow != -59 || mantissaIsZero)) {
            bigIntDigitGenerator(f, pow, e == 0, numBits);
        } else {
            longDigitGenerator(f, pow, e == 0, mantissaIsZero, numBits);
        }
        AbstractStringBuilder dst = sb != null ? sb : new StringBuilder(26);
        if (inputNumber >= 1.0E7d || inputNumber <= -1.0E7d || (inputNumber > -0.001d && inputNumber < 0.001d)) {
            freeFormatExponential(dst, positive);
        } else {
            freeFormat(dst, positive);
        }
        if (sb != null) {
            return null;
        }
        return dst.toString();
    }

    public String floatToString(float f) {
        return convertFloat(null, f);
    }

    public void appendFloat(AbstractStringBuilder sb, float f) {
        convertFloat(sb, f);
    }

    public String convertFloat(AbstractStringBuilder sb, float inputNumber) {
        int inputNumberBits = Float.floatToRawIntBits(inputNumber);
        boolean positive = (DatatypeConstants.FIELD_UNDEFINED & inputNumberBits) == 0;
        int e = (2139095040 & inputNumberBits) >> 23;
        int f = inputNumberBits & 8388607;
        boolean mantissaIsZero = f == 0;
        String quickResult = null;
        if (e == Opcodes.OP_CONST_CLASS_JUMBO) {
            quickResult = mantissaIsZero ? positive ? "Infinity" : "-Infinity" : "NaN";
        } else if (e == 0 && mantissaIsZero) {
            quickResult = positive ? "0.0" : "-0.0";
        }
        if (quickResult != null) {
            return resultOrSideEffect(sb, quickResult);
        }
        int pow;
        int numBits = 23;
        if (e == 0) {
            pow = 1 - Opcodes.OP_OR_INT;
            if (f < 8) {
                f <<= 2;
                pow -= 2;
            }
            int ff = f;
            while ((8388608 & ff) == 0) {
                ff <<= 1;
                numBits--;
            }
        } else {
            f |= 8388608;
            pow = e - Opcodes.OP_OR_INT;
        }
        this.digitCount = 0;
        this.firstK = 0;
        if ((-59 >= pow || pow >= 35) && (pow != -59 || mantissaIsZero)) {
            bigIntDigitGenerator((long) f, pow, e == 0, numBits);
        } else {
            longDigitGenerator((long) f, pow, e == 0, mantissaIsZero, numBits);
        }
        AbstractStringBuilder dst = sb != null ? sb : new StringBuilder(26);
        if (inputNumber >= 1.0E7f || inputNumber <= -1.0E7f || (inputNumber > -0.001f && inputNumber < 0.001f)) {
            freeFormatExponential(dst, positive);
        } else {
            freeFormat(dst, positive);
        }
        if (sb != null) {
            return null;
        }
        return dst.toString();
    }

    private void freeFormatExponential(AbstractStringBuilder sb, boolean positive) {
        if (!positive) {
            sb.append0('-');
        }
        int digitIndex = 0 + 1;
        sb.append0((char) (this.digits[0] + 48));
        sb.append0('.');
        int k = this.firstK;
        int exponent = k;
        int digitIndex2 = digitIndex;
        while (true) {
            k--;
            if (digitIndex2 >= this.digitCount) {
                break;
            }
            digitIndex = digitIndex2 + 1;
            sb.append0((char) (this.digits[digitIndex2] + 48));
            digitIndex2 = digitIndex;
        }
        if (k == exponent - 1) {
            sb.append0('0');
        }
        sb.append0('E');
        IntegralToString.appendInt(sb, exponent);
    }

    private void freeFormat(AbstractStringBuilder sb, boolean positive) {
        if (!positive) {
            sb.append0('-');
        }
        int k = this.firstK;
        if (k < 0) {
            sb.append0('0');
            sb.append0('.');
            for (int i = k + 1; i < 0; i++) {
                sb.append0('0');
            }
        }
        int digitIndex = 0 + 1;
        int U = this.digits[0];
        while (true) {
            int digitIndex2;
            if (U != -1) {
                sb.append0((char) (U + 48));
            } else if (k >= -1) {
                sb.append0('0');
            }
            if (k == 0) {
                sb.append0('.');
            }
            k--;
            if (digitIndex < this.digitCount) {
                digitIndex2 = digitIndex + 1;
                U = this.digits[digitIndex];
            } else {
                U = -1;
                digitIndex2 = digitIndex;
            }
            if (U != -1 || k >= -1) {
                digitIndex = digitIndex2;
            } else {
                return;
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void longDigitGenerator(long r24, int r26, boolean r27, boolean r28, int r29) {
        /*
        r23 = this;
        if (r26 < 0) goto L_0x0055;
    L_0x0002:
        r18 = 1;
        r2 = r18 << r26;
        if (r28 != 0) goto L_0x004e;
    L_0x0008:
        r13 = r26 + 1;
        r4 = r24 << r13;
        r6 = 2;
    L_0x000e:
        r13 = r26 + r29;
        r13 = r13 + -1;
        r0 = (double) r13;
        r18 = r0;
        r20 = invLogOfTenBaseTwo;
        r18 = r18 * r20;
        r20 = 4457293557087583675; // 0x3ddb7cdfd9d7bdbb float:-7.5907164E15 double:1.0E-10;
        r18 = r18 - r20;
        r18 = java.lang.Math.ceil(r18);
        r0 = r18;
        r11 = (int) r0;
        if (r11 <= 0) goto L_0x006f;
    L_0x0029:
        r13 = libcore.math.MathUtils.LONG_POWERS_OF_TEN;
        r18 = r13[r11];
        r6 = r6 * r18;
    L_0x002f:
        r18 = r4 + r2;
        r13 = (r18 > r6 ? 1 : (r18 == r6 ? 0 : -1));
        if (r13 <= 0) goto L_0x0086;
    L_0x0035:
        r0 = r23;
        r0.firstK = r11;
    L_0x0039:
        r8 = 0;
        r10 = 3;
    L_0x003b:
        if (r10 < 0) goto L_0x0095;
    L_0x003d:
        r18 = r6 << r10;
        r14 = r4 - r18;
        r18 = 0;
        r13 = (r14 > r18 ? 1 : (r14 == r18 ? 0 : -1));
        if (r13 < 0) goto L_0x004b;
    L_0x0047:
        r4 = r14;
        r13 = 1;
        r13 = r13 << r10;
        r8 = r8 + r13;
    L_0x004b:
        r10 = r10 + -1;
        goto L_0x003b;
    L_0x004e:
        r13 = r26 + 2;
        r4 = r24 << r13;
        r6 = 4;
        goto L_0x000e;
    L_0x0055:
        r2 = 1;
        if (r27 != 0) goto L_0x005b;
    L_0x0059:
        if (r28 != 0) goto L_0x0065;
    L_0x005b:
        r13 = 1;
        r4 = r24 << r13;
        r18 = 1;
        r13 = 1 - r26;
        r6 = r18 << r13;
        goto L_0x000e;
    L_0x0065:
        r13 = 2;
        r4 = r24 << r13;
        r18 = 1;
        r13 = 2 - r26;
        r6 = r18 << r13;
        goto L_0x000e;
    L_0x006f:
        if (r11 >= 0) goto L_0x002f;
    L_0x0071:
        r13 = libcore.math.MathUtils.LONG_POWERS_OF_TEN;
        r0 = -r11;
        r18 = r0;
        r16 = r13[r18];
        r4 = r4 * r16;
        r18 = 1;
        r13 = (r2 > r18 ? 1 : (r2 == r18 ? 0 : -1));
        if (r13 != 0) goto L_0x0083;
    L_0x0080:
        r2 = r16;
    L_0x0082:
        goto L_0x002f;
    L_0x0083:
        r2 = r2 * r16;
        goto L_0x0082;
    L_0x0086:
        r13 = r11 + -1;
        r0 = r23;
        r0.firstK = r13;
        r18 = 10;
        r4 = r4 * r18;
        r18 = 10;
        r2 = r2 * r18;
        goto L_0x0039;
    L_0x0095:
        r13 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1));
        if (r13 >= 0) goto L_0x00be;
    L_0x0099:
        r12 = 1;
    L_0x009a:
        r18 = r4 + r2;
        r13 = (r18 > r6 ? 1 : (r18 == r6 ? 0 : -1));
        if (r13 <= 0) goto L_0x00c0;
    L_0x00a0:
        r9 = 1;
    L_0x00a1:
        if (r12 != 0) goto L_0x00a5;
    L_0x00a3:
        if (r9 == 0) goto L_0x00c2;
    L_0x00a5:
        if (r12 == 0) goto L_0x00e0;
    L_0x00a7:
        if (r9 != 0) goto L_0x00e0;
    L_0x00a9:
        r0 = r23;
        r13 = r0.digits;
        r0 = r23;
        r0 = r0.digitCount;
        r18 = r0;
        r19 = r18 + 1;
        r0 = r19;
        r1 = r23;
        r1.digitCount = r0;
        r13[r18] = r8;
    L_0x00bd:
        return;
    L_0x00be:
        r12 = 0;
        goto L_0x009a;
    L_0x00c0:
        r9 = 0;
        goto L_0x00a1;
    L_0x00c2:
        r18 = 10;
        r4 = r4 * r18;
        r18 = 10;
        r2 = r2 * r18;
        r0 = r23;
        r13 = r0.digits;
        r0 = r23;
        r0 = r0.digitCount;
        r18 = r0;
        r19 = r18 + 1;
        r0 = r19;
        r1 = r23;
        r1.digitCount = r0;
        r13[r18] = r8;
        goto L_0x0039;
    L_0x00e0:
        if (r9 == 0) goto L_0x00fb;
    L_0x00e2:
        if (r12 != 0) goto L_0x00fb;
    L_0x00e4:
        r0 = r23;
        r13 = r0.digits;
        r0 = r23;
        r0 = r0.digitCount;
        r18 = r0;
        r19 = r18 + 1;
        r0 = r19;
        r1 = r23;
        r1.digitCount = r0;
        r19 = r8 + 1;
        r13[r18] = r19;
        goto L_0x00bd;
    L_0x00fb:
        r13 = 1;
        r18 = r4 << r13;
        r13 = (r18 > r6 ? 1 : (r18 == r6 ? 0 : -1));
        if (r13 >= 0) goto L_0x0117;
    L_0x0102:
        r0 = r23;
        r13 = r0.digits;
        r0 = r23;
        r0 = r0.digitCount;
        r18 = r0;
        r19 = r18 + 1;
        r0 = r19;
        r1 = r23;
        r1.digitCount = r0;
        r13[r18] = r8;
        goto L_0x00bd;
    L_0x0117:
        r0 = r23;
        r13 = r0.digits;
        r0 = r23;
        r0 = r0.digitCount;
        r18 = r0;
        r19 = r18 + 1;
        r0 = r19;
        r1 = r23;
        r1.digitCount = r0;
        r19 = r8 + 1;
        r13[r18] = r19;
        goto L_0x00bd;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.lang.RealToString.longDigitGenerator(long, int, boolean, boolean, int):void");
    }
}
