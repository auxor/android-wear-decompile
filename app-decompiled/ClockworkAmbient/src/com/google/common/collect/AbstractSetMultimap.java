package com.google.common.collect;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

abstract class AbstractSetMultimap<K, V> extends AbstractMapBasedMultimap<K, V> implements SetMultimap<K, V> {
    private static final long serialVersionUID = 7431625294878419160L;

    public Map<K, Collection<V>> asMap() {
        return super.asMap();
    }

    abstract Set<V> createCollection();

    public Set<Entry<K, V>> entries() {
        return (Set) super.entries();
    }

    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
