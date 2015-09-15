package com.android.org.bouncycastle.crypto.modes.gcm;

import com.android.org.bouncycastle.crypto.util.Pack;
import com.android.org.bouncycastle.util.Arrays;

abstract class GCMUtil {
    private static final int E1 = -520093696;
    private static final byte E1B = (byte) -31;
    private static final long E1L = 63331869759897600L;
    private static final int[] LOOKUP;

    GCMUtil() {
    }

    private static int[] generateLookup() {
        int[] lookup = new int[256];
        for (int c = 0; c < 256; c++) {
            int v = 0;
            for (int i = 7; i >= 0; i--) {
                if (((1 << i) & c) != 0) {
                    v ^= E1 >>> (7 - i);
                }
            }
            lookup[c] = v;
        }
        return lookup;
    }

    static {
        LOOKUP = generateLookup();
    }

    static byte[] oneAsBytes() {
        byte[] tmp = new byte[16];
        tmp[0] = Byte.MIN_VALUE;
        return tmp;
    }

    static int[] oneAsInts() {
        int[] tmp = new int[4];
        tmp[0] = Integer.MIN_VALUE;
        return tmp;
    }

    static long[] oneAsLongs() {
        long[] tmp = new long[2];
        tmp[0] = Long.MIN_VALUE;
        return tmp;
    }

    static byte[] asBytes(int[] x) {
        byte[] z = new byte[16];
        Pack.intToBigEndian(x, z, 0);
        return z;
    }

    static void asBytes(int[] x, byte[] z) {
        Pack.intToBigEndian(x, z, 0);
    }

    static byte[] asBytes(long[] x) {
        byte[] z = new byte[16];
        Pack.longToBigEndian(x, z, 0);
        return z;
    }

    static void asBytes(long[] x, byte[] z) {
        Pack.longToBigEndian(x, z, 0);
    }

    static int[] asInts(byte[] x) {
        int[] z = new int[4];
        Pack.bigEndianToInt(x, 0, z);
        return z;
    }

    static void asInts(byte[] x, int[] z) {
        Pack.bigEndianToInt(x, 0, z);
    }

    static long[] asLongs(byte[] x) {
        long[] z = new long[2];
        Pack.bigEndianToLong(x, 0, z);
        return z;
    }

    static void asLongs(byte[] x, long[] z) {
        Pack.bigEndianToLong(x, 0, z);
    }

    static void multiply(byte[] x, byte[] y) {
        byte[] r0 = Arrays.clone(x);
        byte[] r1 = new byte[16];
        for (int i = 0; i < 16; i++) {
            byte bits = y[i];
            for (int j = 7; j >= 0; j--) {
                if (((1 << j) & bits) != 0) {
                    xor(r1, r0);
                }
                if (shiftRight(r0) != null) {
                    r0[0] = (byte) (r0[0] ^ -31);
                }
            }
        }
        System.arraycopy(r1, 0, x, 0, 16);
    }

    static void multiply(int[] x, int[] y) {
        int[] r0 = Arrays.clone(x);
        int[] r1 = new int[4];
        for (int i = 0; i < 4; i++) {
            int bits = y[i];
            for (int j = 31; j >= 0; j--) {
                if (((1 << j) & bits) != 0) {
                    xor(r1, r0);
                }
                if (shiftRight(r0) != 0) {
                    r0[0] = r0[0] ^ E1;
                }
            }
        }
        System.arraycopy(r1, 0, x, 0, 4);
    }

    static void multiply(long[] x, long[] y) {
        long[] r0 = new long[]{x[0], x[1]};
        long[] r1 = new long[2];
        for (int i = 0; i < 2; i++) {
            long bits = y[i];
            for (int j = 63; j >= 0; j--) {
                if (((1 << j) & bits) != 0) {
                    xor(r1, r0);
                }
                if (shiftRight(r0) != 0) {
                    r0[0] = r0[0] ^ E1L;
                }
            }
        }
        x[0] = r1[0];
        x[1] = r1[1];
    }

    static void multiplyP(int[] x) {
        if (shiftRight(x) != 0) {
            x[0] = x[0] ^ E1;
        }
    }

    static void multiplyP(int[] x, int[] y) {
        if (shiftRight(x, y) != 0) {
            y[0] = y[0] ^ E1;
        }
    }

    static void multiplyP8(int[] x) {
        x[0] = x[0] ^ LOOKUP[shiftRightN(x, 8) >>> 24];
    }

    static void multiplyP8(int[] x, int[] y) {
        y[0] = y[0] ^ LOOKUP[shiftRightN(x, 8, y) >>> 24];
    }

    static byte shiftRight(byte[] x) {
        int i = 0;
        int c = 0;
        do {
            int b = x[i] & 255;
            int i2 = i + 1;
            x[i] = (byte) ((b >>> 1) | c);
            c = (b & 1) << 7;
            b = x[i2] & 255;
            i = i2 + 1;
            x[i2] = (byte) ((b >>> 1) | c);
            c = (b & 1) << 7;
            b = x[i] & 255;
            i2 = i + 1;
            x[i] = (byte) ((b >>> 1) | c);
            c = (b & 1) << 7;
            b = x[i2] & 255;
            i = i2 + 1;
            x[i2] = (byte) ((b >>> 1) | c);
            c = (b & 1) << 7;
        } while (i < 16);
        return (byte) c;
    }

    static byte shiftRight(byte[] x, byte[] z) {
        int i = 0;
        int c = 0;
        do {
            int b = x[i] & 255;
            int i2 = i + 1;
            z[i] = (byte) ((b >>> 1) | c);
            c = (b & 1) << 7;
            b = x[i2] & 255;
            i = i2 + 1;
            z[i2] = (byte) ((b >>> 1) | c);
            c = (b & 1) << 7;
            b = x[i] & 255;
            i2 = i + 1;
            z[i] = (byte) ((b >>> 1) | c);
            c = (b & 1) << 7;
            b = x[i2] & 255;
            i = i2 + 1;
            z[i2] = (byte) ((b >>> 1) | c);
            c = (b & 1) << 7;
        } while (i < 16);
        return (byte) c;
    }

    static int shiftRight(int[] x) {
        int b = x[0];
        x[0] = b >>> 1;
        int c = b << 31;
        b = x[1];
        x[1] = (b >>> 1) | c;
        c = b << 31;
        b = x[2];
        x[2] = (b >>> 1) | c;
        c = b << 31;
        b = x[3];
        x[3] = (b >>> 1) | c;
        return b << 31;
    }

    static int shiftRight(int[] x, int[] z) {
        int b = x[0];
        z[0] = b >>> 1;
        int c = b << 31;
        b = x[1];
        z[1] = (b >>> 1) | c;
        c = b << 31;
        b = x[2];
        z[2] = (b >>> 1) | c;
        c = b << 31;
        b = x[3];
        z[3] = (b >>> 1) | c;
        return b << 31;
    }

    static long shiftRight(long[] x) {
        long b = x[0];
        x[0] = b >>> 1;
        long c = b << 63;
        b = x[1];
        x[1] = (b >>> 1) | c;
        return b << 63;
    }

    static long shiftRight(long[] x, long[] z) {
        long b = x[0];
        z[0] = b >>> 1;
        long c = b << 63;
        b = x[1];
        z[1] = (b >>> 1) | c;
        return b << 63;
    }

    static int shiftRightN(int[] x, int n) {
        int b = x[0];
        int nInv = 32 - n;
        x[0] = b >>> n;
        int c = b << nInv;
        b = x[1];
        x[1] = (b >>> n) | c;
        c = b << nInv;
        b = x[2];
        x[2] = (b >>> n) | c;
        c = b << nInv;
        b = x[3];
        x[3] = (b >>> n) | c;
        return b << nInv;
    }

    static int shiftRightN(int[] x, int n, int[] z) {
        int b = x[0];
        int nInv = 32 - n;
        z[0] = b >>> n;
        int c = b << nInv;
        b = x[1];
        z[1] = (b >>> n) | c;
        c = b << nInv;
        b = x[2];
        z[2] = (b >>> n) | c;
        c = b << nInv;
        b = x[3];
        z[3] = (b >>> n) | c;
        return b << nInv;
    }

    static void xor(byte[] x, byte[] y) {
        int i = 0;
        do {
            x[i] = (byte) (x[i] ^ y[i]);
            i++;
            x[i] = (byte) (x[i] ^ y[i]);
            i++;
            x[i] = (byte) (x[i] ^ y[i]);
            i++;
            x[i] = (byte) (x[i] ^ y[i]);
            i++;
        } while (i < 16);
    }

    static void xor(byte[] x, byte[] y, int yOff, int yLen) {
        int yLen2 = yLen;
        while (true) {
            yLen = yLen2 - 1;
            if (yLen2 > 0) {
                x[yLen] = (byte) (x[yLen] ^ y[yOff + yLen]);
                yLen2 = yLen;
            } else {
                return;
            }
        }
    }

    static void xor(byte[] x, byte[] y, byte[] z) {
        int i = 0;
        do {
            z[i] = (byte) (x[i] ^ y[i]);
            i++;
            z[i] = (byte) (x[i] ^ y[i]);
            i++;
            z[i] = (byte) (x[i] ^ y[i]);
            i++;
            z[i] = (byte) (x[i] ^ y[i]);
            i++;
        } while (i < 16);
    }

    static void xor(int[] x, int[] y) {
        x[0] = x[0] ^ y[0];
        x[1] = x[1] ^ y[1];
        x[2] = x[2] ^ y[2];
        x[3] = x[3] ^ y[3];
    }

    static void xor(int[] x, int[] y, int[] z) {
        z[0] = x[0] ^ y[0];
        z[1] = x[1] ^ y[1];
        z[2] = x[2] ^ y[2];
        z[3] = x[3] ^ y[3];
    }

    static void xor(long[] x, long[] y) {
        x[0] = x[0] ^ y[0];
        x[1] = x[1] ^ y[1];
    }

    static void xor(long[] x, long[] y, long[] z) {
        z[0] = x[0] ^ y[0];
        z[1] = x[1] ^ y[1];
    }
}
