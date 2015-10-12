package com.google.common.collect;

import java.util.Map.Entry;

final class RegularImmutableMap<K, V> extends ImmutableMap<K, V> {
    private static final long serialVersionUID = 0;
    private final transient ImmutableMapEntry<K, V>[] entries;
    private final transient int mask;
    private final transient ImmutableMapEntry<K, V>[] table;

    private class EntrySet extends ImmutableMapEntrySet<K, V> {
        private EntrySet() {
            throw new VerifyError("bad dex opcode");
        }

        ImmutableList<Entry<K, V>> createAsList() {
            return new RegularImmutableAsList((ImmutableCollection) this, RegularImmutableMap.this.entries);
        }

        public UnmodifiableIterator<Entry<K, V>> iterator() {
            return asList().iterator();
        }

        ImmutableMap<K, V> map() {
            return RegularImmutableMap.this;
        }
    }

    private static final class NonTerminalMapEntry<K, V> extends ImmutableMapEntry<K, V> {
        private final ImmutableMapEntry<K, V> nextInKeyBucket;

        NonTerminalMapEntry(ImmutableMapEntry<K, V> immutableMapEntry, ImmutableMapEntry<K, V> immutableMapEntry2) {
            super(immutableMapEntry);
            this.nextInKeyBucket = immutableMapEntry2;
            throw new VerifyError("bad dex opcode");
        }

        NonTerminalMapEntry(K k, V v, ImmutableMapEntry<K, V> immutableMapEntry) {
            super(k, v);
            this.nextInKeyBucket = immutableMapEntry;
            throw new VerifyError("bad dex opcode");
        }

        ImmutableMapEntry<K, V> getNextInKeyBucket() {
            return this.nextInKeyBucket;
        }

        ImmutableMapEntry<K, V> getNextInValueBucket() {
            return null;
        }
    }

    RegularImmutableMap(int i, TerminalEntry<?, ?>[] terminalEntryArr) {
        createEntryArray(i);
        throw new VerifyError("bad dex opcode");
    }

    RegularImmutableMap(Entry<?, ?>[] entryArr) {
        createEntryArray(entryArr.length);
        throw new VerifyError("bad dex opcode");
    }

    private void checkNoConflictInBucket(K k, ImmutableMapEntry<K, V> immutableMapEntry, ImmutableMapEntry<K, V> immutableMapEntry2) {
        Object nextInKeyBucket;
        while (nextInKeyBucket != null) {
            ImmutableMap.checkNoConflict(!k.equals(nextInKeyBucket.getKey()), "key", immutableMapEntry, nextInKeyBucket);
            nextInKeyBucket = nextInKeyBucket.getNextInKeyBucket();
        }
    }

    private ImmutableMapEntry<K, V>[] createEntryArray(int i) {
        return new ImmutableMapEntry[i];
    }

    ImmutableSet<Entry<K, V>> createEntrySet() {
        return new EntrySet();
    }

    public V get(Object obj) {
        if (obj != null) {
            for (ImmutableMapEntry immutableMapEntry = this.table[Hashing.smear(obj.hashCode()) & this.mask]; immutableMapEntry != null; immutableMapEntry = immutableMapEntry.getNextInKeyBucket()) {
                if (obj.equals(immutableMapEntry.getKey())) {
                    return immutableMapEntry.getValue();
                }
            }
        }
        return null;
    }

    boolean isPartialView() {
        return false;
    }

    public int size() {
        return this.entries.length;
    }
}
