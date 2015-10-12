package com.google.common.primitives;

import java.util.Comparator;

public final class UnsignedBytes {

    static class LexicographicalComparatorHolder {
        static final Comparator<byte[]> BEST_COMPARATOR;

        enum PureJavaComparator implements Comparator<byte[]> {
            INSTANCE;

            public int compare(byte[] bArr, byte[] bArr2) {
                int min = Math.min(bArr.length, bArr2.length);
                for (int i = 0; i < min; i++) {
                    int compare = UnsignedBytes.compare(bArr[i], bArr2[i]);
                    if (compare != 0) {
                        return compare;
                    }
                }
                return bArr.length - bArr2.length;
            }
        }

        static {
            BEST_COMPARATOR = UnsignedBytes.lexicographicalComparatorJavaImpl();
        }

        LexicographicalComparatorHolder() {
        }
    }

    public static int compare(byte b, byte b2) {
        return toInt(b) - toInt(b2);
    }

    static Comparator<byte[]> lexicographicalComparatorJavaImpl() {
        return PureJavaComparator.INSTANCE;
    }

    public static int toInt(byte b) {
        return b & 255;
    }
}
