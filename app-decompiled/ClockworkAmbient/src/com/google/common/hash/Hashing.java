package com.google.common.hash;

public final class Hashing {
    private static final int GOOD_FAST_HASH_SEED;

    static final class ConcatenatedHashFunction extends AbstractCompositeHashFunction {
        private final int bits;

        public boolean equals(Object obj) {
            if (!(obj instanceof ConcatenatedHashFunction)) {
                return false;
            }
            throw new VerifyError("bad dex opcode");
        }

        public int hashCode() {
            int i = this.bits;
            for (Object hashCode : this.functions) {
                i ^= hashCode.hashCode();
            }
            return i;
        }
    }

    static {
        GOOD_FAST_HASH_SEED = (int) System.currentTimeMillis();
    }
}
