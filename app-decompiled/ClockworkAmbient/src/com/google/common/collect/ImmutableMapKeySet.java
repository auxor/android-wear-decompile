package com.google.common.collect;

import java.io.Serializable;

final class ImmutableMapKeySet<K, V> extends ImmutableSet<K> {
    private final ImmutableMap<K, V> map;

    /* renamed from: com.google.common.collect.ImmutableMapKeySet.1 */
    class AnonymousClass1 extends ImmutableAsList<K> {
        final /* synthetic */ ImmutableList val$entryList;

        AnonymousClass1(ImmutableList immutableList) {
            this.val$entryList = immutableList;
            throw new VerifyError("bad dex opcode");
        }

        ImmutableCollection<K> delegateCollection() {
            return ImmutableMapKeySet.this;
        }

        public K get(int i) {
            throw new VerifyError("bad dex opcode");
        }
    }

    private static class KeySetSerializedForm<K> implements Serializable {
        private static final long serialVersionUID = 0;
        final ImmutableMap<K, ?> map;

        KeySetSerializedForm(ImmutableMap<K, ?> immutableMap) {
            this.map = immutableMap;
            throw new VerifyError("bad dex opcode");
        }

        Object readResolve() {
            return this.map.keySet();
        }
    }

    ImmutableMapKeySet(ImmutableMap<K, V> immutableMap) {
        this.map = immutableMap;
        throw new VerifyError("bad dex opcode");
    }

    public boolean contains(Object obj) {
        return this.map.containsKey(obj);
    }

    ImmutableList<K> createAsList() {
        return new AnonymousClass1(this.map.entrySet().asList());
    }

    boolean isPartialView() {
        return true;
    }

    public UnmodifiableIterator<K> iterator() {
        return asList().iterator();
    }

    public int size() {
        return this.map.size();
    }

    Object writeReplace() {
        return new KeySetSerializedForm(this.map);
    }
}
