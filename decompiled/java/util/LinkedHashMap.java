package java.util;

import java.util.Map.Entry;

public class LinkedHashMap<K, V> extends HashMap<K, V> {
    private static final long serialVersionUID = 3801124242820219131L;
    private final boolean accessOrder;
    transient LinkedEntry<K, V> header;

    private abstract class LinkedHashIterator<T> implements Iterator<T> {
        int expectedModCount;
        LinkedEntry<K, V> lastReturned;
        LinkedEntry<K, V> next;

        private LinkedHashIterator() {
            this.next = LinkedHashMap.this.header.nxt;
            this.lastReturned = null;
            this.expectedModCount = LinkedHashMap.this.modCount;
        }

        public final boolean hasNext() {
            return this.next != LinkedHashMap.this.header;
        }

        final LinkedEntry<K, V> nextEntry() {
            if (LinkedHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            LinkedEntry<K, V> e = this.next;
            if (e == LinkedHashMap.this.header) {
                throw new NoSuchElementException();
            }
            this.next = e.nxt;
            this.lastReturned = e;
            return e;
        }

        public final void remove() {
            if (LinkedHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            } else if (this.lastReturned == null) {
                throw new IllegalStateException();
            } else {
                LinkedHashMap.this.remove(this.lastReturned.key);
                this.lastReturned = null;
                this.expectedModCount = LinkedHashMap.this.modCount;
            }
        }
    }

    private final class EntryIterator extends LinkedHashIterator<Entry<K, V>> {
        private EntryIterator() {
            super(null);
        }

        public final Entry<K, V> next() {
            return nextEntry();
        }
    }

    private final class KeyIterator extends LinkedHashIterator<K> {
        private KeyIterator() {
            super(null);
        }

        public final K next() {
            return nextEntry().key;
        }
    }

    static class LinkedEntry<K, V> extends HashMapEntry<K, V> {
        LinkedEntry<K, V> nxt;
        LinkedEntry<K, V> prv;

        LinkedEntry() {
            super(null, null, 0, null);
            this.prv = this;
            this.nxt = this;
        }

        LinkedEntry(K key, V value, int hash, HashMapEntry<K, V> next, LinkedEntry<K, V> nxt, LinkedEntry<K, V> prv) {
            super(key, value, hash, next);
            this.nxt = nxt;
            this.prv = prv;
        }
    }

    private final class ValueIterator extends LinkedHashIterator<V> {
        private ValueIterator() {
            super(null);
        }

        public final V next() {
            return nextEntry().value;
        }
    }

    public LinkedHashMap() {
        init();
        this.accessOrder = false;
    }

    public LinkedHashMap(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    public LinkedHashMap(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, false);
    }

    public LinkedHashMap(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor);
        init();
        this.accessOrder = accessOrder;
    }

    public LinkedHashMap(Map<? extends K, ? extends V> map) {
        this(HashMap.capacityForInitSize(map.size()));
        constructorPutAll(map);
    }

    void init() {
        this.header = new LinkedEntry();
    }

    public Entry<K, V> eldest() {
        LinkedEntry<K, V> eldest = this.header.nxt;
        return eldest != this.header ? eldest : null;
    }

    void addNewEntry(K key, V value, int hash, int index) {
        LinkedEntry<K, V> header = this.header;
        LinkedEntry<K, V> eldest = header.nxt;
        if (eldest != header && removeEldestEntry(eldest)) {
            remove(eldest.key);
        }
        LinkedEntry<K, V> oldTail = header.prv;
        LinkedEntry<K, V> newTail = new LinkedEntry(key, value, hash, this.table[index], header, oldTail);
        HashMapEntry[] hashMapEntryArr = this.table;
        header.prv = newTail;
        oldTail.nxt = newTail;
        hashMapEntryArr[index] = newTail;
    }

    void addNewEntryForNullKey(V value) {
        LinkedEntry<K, V> header = this.header;
        LinkedEntry<K, V> eldest = header.nxt;
        if (eldest != header && removeEldestEntry(eldest)) {
            remove(eldest.key);
        }
        LinkedEntry<K, V> oldTail = header.prv;
        LinkedEntry<K, V> newTail = new LinkedEntry(null, value, 0, null, header, oldTail);
        header.prv = newTail;
        oldTail.nxt = newTail;
        this.entryForNullKey = newTail;
    }

    HashMapEntry<K, V> constructorNewEntry(K key, V value, int hash, HashMapEntry<K, V> next) {
        LinkedEntry<K, V> header = this.header;
        LinkedEntry<K, V> oldTail = header.prv;
        LinkedEntry<K, V> newTail = new LinkedEntry(key, value, hash, next, header, oldTail);
        header.prv = newTail;
        oldTail.nxt = newTail;
        return newTail;
    }

    public V get(Object key) {
        HashMapEntry<K, V> e;
        if (key == null) {
            e = this.entryForNullKey;
            if (e == null) {
                return null;
            }
            if (this.accessOrder) {
                makeTail((LinkedEntry) e);
            }
            return e.value;
        }
        int hash = Collections.secondaryHash(key);
        HashMapEntry<K, V>[] tab = this.table;
        e = tab[(tab.length - 1) & hash];
        while (e != null) {
            K eKey = e.key;
            if (eKey == key || (e.hash == hash && key.equals(eKey))) {
                if (this.accessOrder) {
                    makeTail((LinkedEntry) e);
                }
                return e.value;
            }
            e = e.next;
        }
        return null;
    }

    private void makeTail(LinkedEntry<K, V> e) {
        e.prv.nxt = e.nxt;
        e.nxt.prv = e.prv;
        LinkedEntry<K, V> header = this.header;
        LinkedEntry<K, V> oldTail = header.prv;
        e.nxt = header;
        e.prv = oldTail;
        header.prv = e;
        oldTail.nxt = e;
        this.modCount++;
    }

    void preModify(HashMapEntry<K, V> e) {
        if (this.accessOrder) {
            makeTail((LinkedEntry) e);
        }
    }

    void postRemove(HashMapEntry<K, V> e) {
        LinkedEntry<K, V> le = (LinkedEntry) e;
        le.prv.nxt = le.nxt;
        le.nxt.prv = le.prv;
        le.prv = null;
        le.nxt = null;
    }

    public boolean containsValue(Object value) {
        LinkedEntry<K, V> header;
        LinkedEntry<K, V> e;
        if (value == null) {
            header = this.header;
            for (e = header.nxt; e != header; e = e.nxt) {
                if (e.value == null) {
                    return true;
                }
            }
            return false;
        }
        header = this.header;
        for (e = header.nxt; e != header; e = e.nxt) {
            if (value.equals(e.value)) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        super.clear();
        LinkedEntry<K, V> header = this.header;
        LinkedEntry<K, V> e = header.nxt;
        while (e != header) {
            LinkedEntry<K, V> nxt = e.nxt;
            e.prv = null;
            e.nxt = null;
            e = nxt;
        }
        header.prv = header;
        header.nxt = header;
    }

    Iterator<K> newKeyIterator() {
        return new KeyIterator();
    }

    Iterator<V> newValueIterator() {
        return new ValueIterator();
    }

    Iterator<Entry<K, V>> newEntryIterator() {
        return new EntryIterator();
    }

    protected boolean removeEldestEntry(Entry<K, V> entry) {
        return false;
    }
}
