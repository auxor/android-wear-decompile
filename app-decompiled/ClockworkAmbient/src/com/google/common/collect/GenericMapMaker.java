package com.google.common.collect;

@Deprecated
public abstract class GenericMapMaker<K0, V0> {
    RemovalListener<K0, V0> removalListener;

    enum NullListener implements RemovalListener<Object, Object> {
        INSTANCE;

        public void onRemoval(RemovalNotification<Object, Object> removalNotification) {
        }
    }

    GenericMapMaker() {
    }

    <K extends K0, V extends V0> RemovalListener<K, V> getRemovalListener() {
        throw new VerifyError("bad dex opcode");
    }
}
