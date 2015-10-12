package com.google.common.collect;

final class Platform {
    static <T> T[] newArray(T[] tArr, int i) {
        throw new VerifyError("bad dex opcode");
    }

    static MapMaker tryWeakKeys(MapMaker mapMaker) {
        return mapMaker.weakKeys();
    }
}
