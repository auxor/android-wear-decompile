package com.google.common.collect;

import java.io.Serializable;
import java.util.Map.Entry;

abstract class ImmutableMapEntrySet<K, V> extends ImmutableSet<Entry<K, V>> {

    private static class EntrySetSerializedForm<K, V> implements Serializable {
        private static final long serialVersionUID = 0;
        final ImmutableMap<K, V> map;

        EntrySetSerializedForm(ImmutableMap<K, V> immutableMap) {
            this.map = immutableMap;
            throw new VerifyError("bad dex opcode");
        }

        Object readResolve() {
            return this.map.entrySet();
        }
    }

    ImmutableMapEntrySet() {
    }

    public boolean contains(Object obj) {
        if (!(obj instanceof Entry)) {
            return false;
        }
        Entry entry = (Entry) obj;
        throw new VerifyError("bad dex opcode");
    }

    boolean isPartialView() {
        return map().isPartialView();
    }

    abstract ImmutableMap<K, V> map();

    public int size() {
        return map().size();
    }

    Object writeReplace() {
        return new EntrySetSerializedForm(map());
    }
}
