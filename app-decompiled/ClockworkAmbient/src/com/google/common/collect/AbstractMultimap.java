package com.google.common.collect;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

abstract class AbstractMultimap<K, V> implements Multimap<K, V> {
    private transient Map<K, Collection<V>> asMap;
    private transient Collection<Entry<K, V>> entries;
    private transient Set<K> keySet;

    private class Entries extends Entries<K, V> {
        private Entries() {
            throw new VerifyError("bad dex opcode");
        }

        public Iterator<Entry<K, V>> iterator() {
            return AbstractMultimap.this.entryIterator();
        }

        Multimap<K, V> multimap() {
            return AbstractMultimap.this;
        }
    }

    private class EntrySet extends Entries implements Set<Entry<K, V>> {
        private EntrySet() {
            super(null);
            throw new VerifyError("bad dex opcode");
        }

        public boolean equals(Object obj) {
            return Sets.equalsImpl(this, obj);
        }

        public int hashCode() {
            return Sets.hashCodeImpl(this);
        }
    }

    public Map<K, Collection<V>> asMap() {
        Map<K, Collection<V>> map = this.asMap;
        if (map != null) {
            return map;
        }
        map = createAsMap();
        this.asMap = map;
        return map;
    }

    public boolean containsEntry(Object obj, Object obj2) {
        throw new VerifyError("bad dex opcode");
    }

    abstract Map<K, Collection<V>> createAsMap();

    Collection<Entry<K, V>> createEntries() {
        return this instanceof SetMultimap ? new EntrySet() : new Entries();
    }

    Set<K> createKeySet() {
        return new KeySet(asMap());
    }

    public Collection<Entry<K, V>> entries() {
        Collection<Entry<K, V>> collection = this.entries;
        if (collection != null) {
            return collection;
        }
        collection = createEntries();
        this.entries = collection;
        return collection;
    }

    abstract Iterator<Entry<K, V>> entryIterator();

    public boolean equals(Object obj) {
        return Multimaps.equalsImpl(this, obj);
    }

    public int hashCode() {
        return asMap().hashCode();
    }

    public Set<K> keySet() {
        Set<K> set = this.keySet;
        if (set != null) {
            return set;
        }
        set = createKeySet();
        this.keySet = set;
        return set;
    }

    public boolean remove(Object obj, Object obj2) {
        throw new VerifyError("bad dex opcode");
    }

    public String toString() {
        return asMap().toString();
    }
}
