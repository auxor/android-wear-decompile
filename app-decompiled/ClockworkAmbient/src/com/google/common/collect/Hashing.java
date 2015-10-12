package com.google.common.collect;

final class Hashing {
    private static int MAX_TABLE_SIZE;

    static {
        MAX_TABLE_SIZE = 1073741824;
    }

    static int closedTableSize(int i, double d) {
        int max = Math.max(i, 2);
        int highestOneBit = Integer.highestOneBit(max);
        if (max <= ((int) (((double) highestOneBit) * d))) {
            return highestOneBit;
        }
        highestOneBit <<= 1;
        return highestOneBit > 0 ? highestOneBit : MAX_TABLE_SIZE;
    }

    static boolean needsResizing(int i, int i2, double d) {
        return ((double) i) > ((double) i2) * d && i2 < MAX_TABLE_SIZE;
    }

    static int smear(int i) {
        return 461845907 * Integer.rotateLeft(-862048943 * i, 15);
    }

    static int smearedHash(Object obj) {
        return smear(obj == null ? 0 : obj.hashCode());
    }
}
