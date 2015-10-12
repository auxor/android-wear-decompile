package com.google.common.collect;

import com.google.common.base.Function;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Preconditions;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class Maps {
    static final MapJoiner STANDARD_JOINER;

    static abstract class EntrySet<K, V> extends ImprovedAbstractSet<Entry<K, V>> {
        EntrySet() {
        }

        public void clear() {
            map().clear();
        }

        public boolean contains(Object obj) {
            if (!(obj instanceof Entry)) {
                return false;
            }
            ((Entry) obj).getKey();
            throw new VerifyError("bad dex opcode");
        }

        public boolean isEmpty() {
            return map().isEmpty();
        }

        abstract Map<K, V> map();

        public boolean remove(Object obj) {
            throw new VerifyError("bad dex opcode");
        }

        public boolean removeAll(Collection<?> collection) {
            try {
                return super.removeAll((Collection) Preconditions.checkNotNull(collection));
            } catch (UnsupportedOperationException e) {
                return Sets.removeAllImpl((Set) this, collection.iterator());
            }
        }

        public boolean retainAll(Collection<?> collection) {
            try {
                return super.retainAll((Collection) Preconditions.checkNotNull(collection));
            } catch (UnsupportedOperationException e) {
                Sets.newHashSetWithExpectedSize(collection.size());
                Iterator it = collection.iterator();
                if (it.hasNext()) {
                    it.next();
                    throw new VerifyError("bad dex opcode");
                }
                throw new VerifyError("bad dex opcode");
            }
        }

        public int size() {
            return map().size();
        }
    }

    static abstract class ImprovedAbstractMap<K, V> extends AbstractMap<K, V> {
        private transient Set<Entry<K, V>> entrySet;
        private transient Set<K> keySet;
        private transient Collection<V> values;

        ImprovedAbstractMap() {
        }

        abstract Set<Entry<K, V>> createEntrySet();

        Set<K> createKeySet() {
            return new KeySet(this);
        }

        Collection<V> createValues() {
            return new Values(this);
        }

        public Set<Entry<K, V>> entrySet() {
            Set<Entry<K, V>> set = this.entrySet;
            if (set != null) {
                return set;
            }
            set = createEntrySet();
            this.entrySet = set;
            return set;
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

        public Collection<V> values() {
            Collection<V> collection = this.values;
            if (collection != null) {
                return collection;
            }
            collection = createValues();
            this.values = collection;
            return collection;
        }
    }

    static class KeySet<K, V> extends ImprovedAbstractSet<K> {
        final Map<K, V> map;

        KeySet(Map<K, V> map) {
            Map map2 = (Map) Preconditions.checkNotNull(map);
            throw new VerifyError("bad dex opcode");
        }

        public void clear() {
            map().clear();
        }

        public boolean contains(Object obj) {
            return map().containsKey(obj);
        }

        public boolean isEmpty() {
            return map().isEmpty();
        }

        public Iterator<K> iterator() {
            return Maps.keyIterator(map().entrySet().iterator());
        }

        Map<K, V> map() {
            return this.map;
        }

        public boolean remove(Object obj) {
            if (!contains(obj)) {
                return false;
            }
            map().remove(obj);
            return true;
        }

        public int size() {
            return map().size();
        }
    }

    /* renamed from: com.google.common.collect.Maps.1 */
    static final class AnonymousClass1 extends UnmodifiableIterator<V> {
        final /* synthetic */ UnmodifiableIterator val$entryIterator;

        AnonymousClass1(UnmodifiableIterator unmodifiableIterator) {
            this.val$entryIterator = unmodifiableIterator;
            throw new VerifyError("bad dex opcode");
        }

        public boolean hasNext() {
            return this.val$entryIterator.hasNext();
        }

        public V next() {
            throw new VerifyError("bad dex opcode");
        }
    }

    private enum EntryFunction implements Function<Entry<?, ?>, Object> {
        KEY {
            public Object apply(Entry<?, ?> entry) {
                return entry.getKey();
            }
        },
        VALUE {
            public Object apply(Entry<?, ?> entry) {
                return entry.getValue();
            }
        };
    }

    static class Values<K, V> extends AbstractCollection<V> {
        final Map<K, V> map;

        Values(Map<K, V> map) {
            Map map2 = (Map) Preconditions.checkNotNull(map);
            throw new VerifyError("bad dex opcode");
        }

        public void clear() {
            map().clear();
        }

        public boolean contains(Object obj) {
            return map().containsValue(obj);
        }

        public boolean isEmpty() {
            return map().isEmpty();
        }

        public Iterator<V> iterator() {
            return Maps.valueIterator(map().entrySet().iterator());
        }

        final Map<K, V> map() {
            return this.map;
        }

        public boolean remove(Object obj) {
            try {
                return super.remove(obj);
            } catch (UnsupportedOperationException e) {
                throw new VerifyError("bad dex opcode");
            }
        }

        public boolean removeAll(Collection<?> collection) {
            try {
                return super.removeAll((Collection) Preconditions.checkNotNull(collection));
            } catch (UnsupportedOperationException e) {
                Sets.newHashSet();
                throw new VerifyError("bad dex opcode");
            }
        }

        public boolean retainAll(Collection<?> collection) {
            try {
                return super.retainAll((Collection) Preconditions.checkNotNull(collection));
            } catch (UnsupportedOperationException e) {
                Sets.newHashSet();
                throw new VerifyError("bad dex opcode");
            }
        }

        public int size() {
            return map().size();
        }
    }

    static {
        STANDARD_JOINER = Collections2.STANDARD_JOINER.withKeyValueSeparator("=");
    }

    static int capacity(int i) {
        if (i >= 3) {
            return i < 1073741824 ? (i / 3) + i : Integer.MAX_VALUE;
        } else {
            CollectPreconditions.checkNonnegative(i, "expectedSize");
            return i + 1;
        }
    }

    static boolean equalsImpl(Map<?, ?> map, Object obj) {
        if (map == obj) {
            return true;
        }
        if (!(obj instanceof Map)) {
            return false;
        }
        return map.entrySet().equals(((Map) obj).entrySet());
    }

    public static <K, V> Entry<K, V> immutableEntry(K k, V v) {
        return new ImmutableEntry(k, v);
    }

    static <K> Function<Entry<K, ?>, K> keyFunction() {
        return EntryFunction.KEY;
    }

    static <K, V> Iterator<K> keyIterator(Iterator<Entry<K, V>> it) {
        return Iterators.transform(it, keyFunction());
    }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
        return new LinkedHashMap();
    }

    static boolean safeContainsKey(Map<?, ?> map, Object obj) {
        boolean z = false;
        Preconditions.checkNotNull(map);
        try {
            z = map.containsKey(obj);
        } catch (ClassCastException e) {
        } catch (NullPointerException e2) {
        }
        return z;
    }

    static <V> V safeGet(Map<?, V> map, Object obj) {
        V v = null;
        Preconditions.checkNotNull(map);
        try {
            v = map.get(obj);
        } catch (ClassCastException e) {
        } catch (NullPointerException e2) {
        }
        return v;
    }

    static <V> V safeRemove(Map<?, V> map, Object obj) {
        V v = null;
        Preconditions.checkNotNull(map);
        try {
            v = map.remove(obj);
        } catch (ClassCastException e) {
        } catch (NullPointerException e2) {
        }
        return v;
    }

    static String toStringImpl(Map<?, ?> map) {
        StringBuilder append = Collections2.newStringBuilderForCollection(map.size()).append('{');
        STANDARD_JOINER.appendTo(append, (Map) map);
        return append.append('}').toString();
    }

    static <V> Function<Entry<?, V>, V> valueFunction() {
        return EntryFunction.VALUE;
    }

    static <K, V> UnmodifiableIterator<V> valueIterator(UnmodifiableIterator<Entry<K, V>> unmodifiableIterator) {
        return new AnonymousClass1(unmodifiableIterator);
    }

    static <K, V> Iterator<V> valueIterator(Iterator<Entry<K, V>> it) {
        return Iterators.transform(it, valueFunction());
    }
}
