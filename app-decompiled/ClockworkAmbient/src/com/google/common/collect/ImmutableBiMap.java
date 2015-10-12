package com.google.common.collect;

import java.util.Map.Entry;

public abstract class ImmutableBiMap<K, V> extends ImmutableMap<K, V> implements BiMap<K, V> {
    private static final Entry<?, ?>[] EMPTY_ENTRY_ARRAY;

    public static final class Builder<K, V> extends com.google.common.collect.ImmutableMap.Builder<K, V> {
        public ImmutableBiMap<K, V> build() {
            switch (this.size) {
                case 0:
                    return ImmutableBiMap.of();
                case 1:
                    return ImmutableBiMap.of(this.entries[0].getKey(), this.entries[0].getValue());
                default:
                    return new RegularImmutableBiMap(this.size, this.entries);
            }
        }

        public Builder<K, V> put(K k, V v) {
            super.put(k, v);
            return this;
        }
    }

    private static class SerializedForm extends SerializedForm {
        private static final long serialVersionUID = 0;

        SerializedForm(ImmutableBiMap<?, ?> immutableBiMap) {
            super(immutableBiMap);
        }

        Object readResolve() {
            return createMap(new Builder());
        }
    }

    static {
        EMPTY_ENTRY_ARRAY = new Entry[0];
    }

    ImmutableBiMap() {
    }

    public static <K, V> ImmutableBiMap<K, V> of() {
        return EmptyImmutableBiMap.INSTANCE;
    }

    public static <K, V> ImmutableBiMap<K, V> of(K k, V v) {
        return new SingletonImmutableBiMap(k, v);
    }

    public abstract ImmutableBiMap<V, K> inverse();

    public ImmutableSet<V> values() {
        return inverse().keySet();
    }

    Object writeReplace() {
        return new SerializedForm(this);
    }
}
