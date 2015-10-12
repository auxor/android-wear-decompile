package com.google.common.collect;

import java.io.Serializable;

final class ImmutableMapValues<K, V> extends ImmutableCollection<V> {
    private final ImmutableMap<K, V> map;

    /* renamed from: com.google.common.collect.ImmutableMapValues.1 */
    class AnonymousClass1 extends ImmutableAsList<V> {
        final /* synthetic */ ImmutableList val$entryList;

        AnonymousClass1(ImmutableList immutableList) {
            this.val$entryList = immutableList;
            throw new VerifyError("bad dex opcode");
        }

        ImmutableCollection<V> delegateCollection() {
            return ImmutableMapValues.this;
        }

        public V get(int i) {
            throw new VerifyError("bad dex opcode");
        }
    }

    private static class SerializedForm<V> implements Serializable {
        private static final long serialVersionUID = 0;
        final ImmutableMap<?, V> map;

        SerializedForm(ImmutableMap<?, V> immutableMap) {
            this.map = immutableMap;
            throw new VerifyError("bad dex opcode");
        }

        Object readResolve() {
            return this.map.values();
        }
    }

    ImmutableMapValues(ImmutableMap<K, V> immutableMap) {
        this.map = immutableMap;
        throw new VerifyError("bad dex opcode");
    }

    public boolean contains(Object obj) {
        return obj != null && Iterators.contains(iterator(), obj);
    }

    ImmutableList<V> createAsList() {
        return new AnonymousClass1(this.map.entrySet().asList());
    }

    boolean isPartialView() {
        return true;
    }

    public UnmodifiableIterator<V> iterator() {
        return Maps.valueIterator(this.map.entrySet().iterator());
    }

    public int size() {
        return this.map.size();
    }

    Object writeReplace() {
        return new SerializedForm(this.map);
    }
}
