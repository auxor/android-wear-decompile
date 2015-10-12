package com.google.common.primitives;

import java.util.Arrays;

public final class Ints {
    private static final byte[] asciiDigits;

    static {
        int i = 0;
        asciiDigits = new byte[128];
        Arrays.fill(asciiDigits, (byte) -1);
        for (int i2 = 0; i2 <= 9; i2++) {
            asciiDigits[i2 + 48] = (byte) i2;
        }
        while (i <= 26) {
            asciiDigits[i + 65] = (byte) (i + 10);
            asciiDigits[i + 97] = (byte) (i + 10);
            i++;
        }
    }

    public static int saturatedCast(long j) {
        return j > 2147483647L ? Integer.MAX_VALUE : j < -2147483648L ? Integer.MIN_VALUE : (int) j;
    }
}
