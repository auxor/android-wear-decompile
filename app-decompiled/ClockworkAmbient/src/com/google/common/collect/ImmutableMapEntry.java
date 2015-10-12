package com.google.common.collect;

abstract class ImmutableMapEntry<K, V> extends ImmutableEntry<K, V> {

    static final class TerminalEntry<K, V> extends ImmutableMapEntry<K, V> {
        TerminalEntry(K k, V v) {
            super(k, v);
        }

        ImmutableMapEntry<K, V> getNextInKeyBucket() {
            return null;
        }

        ImmutableMapEntry<K, V> getNextInValueBucket() {
            return null;
        }
    }

    ImmutableMapEntry(ImmutableMapEntry<K, V> immutableMapEntry) {
        super(immutableMapEntry.getKey(), immutableMapEntry.getValue());
    }

    ImmutableMapEntry(K k, V v) {
        super(k, v);
        CollectPreconditions.checkEntryNotNull(k, v);
    }

    abstract ImmutableMapEntry<K, V> getNextInKeyBucket();

    abstract ImmutableMapEntry<K, V> getNextInValueBucket();
}
