package java.util;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.Map.Entry;
import libcore.util.Objects;

public class HashMap<K, V> extends AbstractMap<K, V> implements Cloneable, Serializable {
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final Entry[] EMPTY_TABLE;
    private static final int MAXIMUM_CAPACITY = 1073741824;
    private static final int MINIMUM_CAPACITY = 4;
    private static final ObjectStreamField[] serialPersistentFields;
    private static final long serialVersionUID = 362498820763181265L;
    transient HashMapEntry<K, V> entryForNullKey;
    private transient Set<Entry<K, V>> entrySet;
    private transient Set<K> keySet;
    transient int modCount;
    transient int size;
    transient HashMapEntry<K, V>[] table;
    private transient int threshold;
    private transient Collection<V> values;

    private abstract class HashIterator {
        int expectedModCount;
        HashMapEntry<K, V> lastEntryReturned;
        HashMapEntry<K, V> nextEntry;
        int nextIndex;

        HashIterator() {
            this.nextEntry = HashMap.this.entryForNullKey;
            this.expectedModCount = HashMap.this.modCount;
            if (this.nextEntry == null) {
                HashMapEntry<K, V>[] tab = HashMap.this.table;
                HashMapEntry<K, V> next = null;
                while (next == null && this.nextIndex < tab.length) {
                    int i = this.nextIndex;
                    this.nextIndex = i + 1;
                    next = tab[i];
                }
                this.nextEntry = next;
            }
        }

        public boolean hasNext() {
            return this.nextEntry != null;
        }

        HashMapEntry<K, V> nextEntry() {
            if (HashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            } else if (this.nextEntry == null) {
                throw new NoSuchElementException();
            } else {
                HashMapEntry<K, V> entryToReturn = this.nextEntry;
                HashMapEntry<K, V>[] tab = HashMap.this.table;
                HashMapEntry<K, V> next = entryToReturn.next;
                while (next == null && this.nextIndex < tab.length) {
                    int i = this.nextIndex;
                    this.nextIndex = i + 1;
                    next = tab[i];
                }
                this.nextEntry = next;
                this.lastEntryReturned = entryToReturn;
                return entryToReturn;
            }
        }

        public void remove() {
            if (this.lastEntryReturned == null) {
                throw new IllegalStateException();
            } else if (HashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            } else {
                HashMap.this.remove(this.lastEntryReturned.key);
                this.lastEntryReturned = null;
                this.expectedModCount = HashMap.this.modCount;
            }
        }
    }

    private final class EntryIterator extends HashIterator implements Iterator<Entry<K, V>> {
        private EntryIterator() {
            super();
        }

        public Entry<K, V> next() {
            return nextEntry();
        }
    }

    private final class EntrySet extends AbstractSet<Entry<K, V>> {
        private EntrySet() {
        }

        public Iterator<Entry<K, V>> iterator() {
            return HashMap.this.newEntryIterator();
        }

        public boolean contains(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry<?, ?> e = (Entry) o;
            return HashMap.this.containsMapping(e.getKey(), e.getValue());
        }

        public boolean remove(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry<?, ?> e = (Entry) o;
            return HashMap.this.removeMapping(e.getKey(), e.getValue());
        }

        public int size() {
            return HashMap.this.size;
        }

        public boolean isEmpty() {
            return HashMap.this.size == 0;
        }

        public void clear() {
            HashMap.this.clear();
        }
    }

    static class HashMapEntry<K, V> implements Entry<K, V> {
        final int hash;
        final K key;
        HashMapEntry<K, V> next;
        V value;

        HashMapEntry(K key, V value, int hash, HashMapEntry<K, V> next) {
            this.key = key;
            this.value = value;
            this.hash = hash;
            this.next = next;
        }

        public final K getKey() {
            return this.key;
        }

        public final V getValue() {
            return this.value;
        }

        public final V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public final boolean equals(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry<?, ?> e = (Entry) o;
            if (Objects.equal(e.getKey(), this.key) && Objects.equal(e.getValue(), this.value)) {
                return true;
            }
            return false;
        }

        public final int hashCode() {
            int i = 0;
            int hashCode = this.key == null ? 0 : this.key.hashCode();
            if (this.value != null) {
                i = this.value.hashCode();
            }
            return hashCode ^ i;
        }

        public final String toString() {
            return this.key + "=" + this.value;
        }
    }

    private final class KeyIterator extends HashIterator implements Iterator<K> {
        private KeyIterator() {
            super();
        }

        public K next() {
            return nextEntry().key;
        }
    }

    private final class KeySet extends AbstractSet<K> {
        private KeySet() {
        }

        public Iterator<K> iterator() {
            return HashMap.this.newKeyIterator();
        }

        public int size() {
            return HashMap.this.size;
        }

        public boolean isEmpty() {
            return HashMap.this.size == 0;
        }

        public boolean contains(Object o) {
            return HashMap.this.containsKey(o);
        }

        public boolean remove(Object o) {
            int oldSize = HashMap.this.size;
            HashMap.this.remove(o);
            return HashMap.this.size != oldSize;
        }

        public void clear() {
            HashMap.this.clear();
        }
    }

    private final class ValueIterator extends HashIterator implements Iterator<V> {
        private ValueIterator() {
            super();
        }

        public V next() {
            return nextEntry().value;
        }
    }

    private final class Values extends AbstractCollection<V> {
        private Values() {
        }

        public Iterator<V> iterator() {
            return HashMap.this.newValueIterator();
        }

        public int size() {
            return HashMap.this.size;
        }

        public boolean isEmpty() {
            return HashMap.this.size == 0;
        }

        public boolean contains(Object o) {
            return HashMap.this.containsValue(o);
        }

        public void clear() {
            HashMap.this.clear();
        }
    }

    static {
        EMPTY_TABLE = new HashMapEntry[2];
        serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("loadFactor", Float.TYPE)};
    }

    public HashMap() {
        this.table = (HashMapEntry[]) EMPTY_TABLE;
        this.threshold = -1;
    }

    public HashMap(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity: " + capacity);
        } else if (capacity == 0) {
            this.table = (HashMapEntry[]) ((HashMapEntry[]) EMPTY_TABLE);
            this.threshold = -1;
        } else {
            if (capacity < MINIMUM_CAPACITY) {
                capacity = MINIMUM_CAPACITY;
            } else if (capacity > MAXIMUM_CAPACITY) {
                capacity = MAXIMUM_CAPACITY;
            } else {
                capacity = Collections.roundUpToPowerOfTwo(capacity);
            }
            makeTable(capacity);
        }
    }

    public HashMap(int capacity, float loadFactor) {
        this(capacity);
        if (loadFactor <= 0.0f || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Load factor: " + loadFactor);
        }
    }

    public HashMap(Map<? extends K, ? extends V> map) {
        this(capacityForInitSize(map.size()));
        constructorPutAll(map);
    }

    final void constructorPutAll(Map<? extends K, ? extends V> map) {
        if (this.table == EMPTY_TABLE) {
            doubleCapacity();
        }
        for (Entry<? extends K, ? extends V> e : map.entrySet()) {
            constructorPut(e.getKey(), e.getValue());
        }
    }

    static int capacityForInitSize(int size) {
        int result = (size >> 1) + size;
        return (-1073741824 & result) == 0 ? result : MAXIMUM_CAPACITY;
    }

    public Object clone() {
        try {
            HashMap<K, V> result = (HashMap) super.clone();
            result.makeTable(this.table.length);
            result.entryForNullKey = null;
            result.size = 0;
            result.keySet = null;
            result.entrySet = null;
            result.values = null;
            result.init();
            result.constructorPutAll(this);
            return result;
        } catch (Object e) {
            throw new AssertionError(e);
        }
    }

    void init() {
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public int size() {
        return this.size;
    }

    public V get(Object key) {
        HashMapEntry<K, V> e;
        if (key == null) {
            e = this.entryForNullKey;
            if (e == null) {
                return null;
            }
            return e.value;
        }
        int hash = Collections.secondaryHash(key);
        HashMapEntry<K, V>[] tab = this.table;
        e = tab[(tab.length - 1) & hash];
        while (e != null) {
            K eKey = e.key;
            if (eKey == key || (e.hash == hash && key.equals(eKey))) {
                return e.value;
            }
            e = e.next;
        }
        return null;
    }

    public boolean containsKey(Object key) {
        if (key != null) {
            int hash = Collections.secondaryHash(key);
            HashMapEntry<K, V>[] tab = this.table;
            for (HashMapEntry<K, V> e = tab[(tab.length - 1) & hash]; e != null; e = e.next) {
                K eKey = e.key;
                if (eKey == key) {
                    return true;
                }
                if (e.hash == hash && key.equals(eKey)) {
                    return true;
                }
            }
            return false;
        } else if (this.entryForNullKey != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean containsValue(Object value) {
        HashMapEntry[] tab = this.table;
        HashMapEntry e;
        if (value == null) {
            for (HashMapEntry e2 : tab) {
                for (e2 = tab[i]; e2 != null; e2 = e2.next) {
                    if (e2.value == null) {
                        return true;
                    }
                }
            }
            if (this.entryForNullKey == null || this.entryForNullKey.value != null) {
                return false;
            }
            return true;
        }
        for (HashMapEntry e22 : tab) {
            for (e22 = tab[i]; e22 != null; e22 = e22.next) {
                if (value.equals(e22.value)) {
                    return true;
                }
            }
        }
        if (this.entryForNullKey == null || !value.equals(this.entryForNullKey.value)) {
            return false;
        }
        return true;
    }

    public V put(K key, V value) {
        if (key == null) {
            return putValueForNullKey(value);
        }
        int hash = Collections.secondaryHash((Object) key);
        HashMapEntry<K, V>[] tab = this.table;
        int index = hash & (tab.length - 1);
        HashMapEntry<K, V> e = tab[index];
        while (e != null) {
            if (e.hash == hash && key.equals(e.key)) {
                preModify(e);
                V oldValue = e.value;
                e.value = value;
                return oldValue;
            }
            e = e.next;
        }
        this.modCount++;
        int i = this.size;
        this.size = i + 1;
        if (i > this.threshold) {
            index = hash & (doubleCapacity().length - 1);
        }
        addNewEntry(key, value, hash, index);
        return null;
    }

    private V putValueForNullKey(V value) {
        HashMapEntry<K, V> entry = this.entryForNullKey;
        if (entry == null) {
            addNewEntryForNullKey(value);
            this.size++;
            this.modCount++;
            return null;
        }
        preModify(entry);
        V oldValue = entry.value;
        entry.value = value;
        return oldValue;
    }

    void preModify(HashMapEntry<K, V> hashMapEntry) {
    }

    private void constructorPut(K key, V value) {
        if (key == null) {
            HashMapEntry<K, V> entry = this.entryForNullKey;
            if (entry == null) {
                this.entryForNullKey = constructorNewEntry(null, value, 0, null);
                this.size++;
                return;
            }
            entry.value = value;
            return;
        }
        int hash = Collections.secondaryHash((Object) key);
        HashMapEntry<K, V>[] tab = this.table;
        int index = hash & (tab.length - 1);
        HashMapEntry<K, V> first = tab[index];
        HashMapEntry<K, V> e = first;
        while (e != null) {
            if (e.hash == hash && key.equals(e.key)) {
                e.value = value;
                return;
            }
            e = e.next;
        }
        tab[index] = constructorNewEntry(key, value, hash, first);
        this.size++;
    }

    void addNewEntry(K key, V value, int hash, int index) {
        this.table[index] = new HashMapEntry(key, value, hash, this.table[index]);
    }

    void addNewEntryForNullKey(V value) {
        this.entryForNullKey = new HashMapEntry(null, value, 0, null);
    }

    HashMapEntry<K, V> constructorNewEntry(K key, V value, int hash, HashMapEntry<K, V> first) {
        return new HashMapEntry(key, value, hash, first);
    }

    public void putAll(Map<? extends K, ? extends V> map) {
        ensureCapacity(map.size());
        super.putAll(map);
    }

    private void ensureCapacity(int numMappings) {
        int newCapacity = Collections.roundUpToPowerOfTwo(capacityForInitSize(numMappings));
        if (newCapacity > oldCapacity) {
            if (newCapacity == oldCapacity * 2) {
                doubleCapacity();
                return;
            }
            HashMapEntry<K, V>[] newTable = makeTable(newCapacity);
            if (this.size != 0) {
                int newMask = newCapacity - 1;
                for (HashMapEntry<K, V> e : this.table) {
                    HashMapEntry<K, V> e2;
                    while (e2 != null) {
                        HashMapEntry<K, V> oldNext = e2.next;
                        int newIndex = e2.hash & newMask;
                        HashMapEntry<K, V> newNext = newTable[newIndex];
                        newTable[newIndex] = e2;
                        e2.next = newNext;
                        e2 = oldNext;
                    }
                }
            }
        }
    }

    private HashMapEntry<K, V>[] makeTable(int newCapacity) {
        HashMapEntry[] newTable = (HashMapEntry[]) new HashMapEntry[newCapacity];
        this.table = newTable;
        this.threshold = (newCapacity >> 1) + (newCapacity >> 2);
        return newTable;
    }

    private HashMapEntry<K, V>[] doubleCapacity() {
        HashMapEntry<K, V>[] oldTable = this.table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY) {
            return oldTable;
        }
        HashMapEntry<K, V>[] newTable = makeTable(oldCapacity * 2);
        if (this.size == 0) {
            return newTable;
        }
        for (int j = 0; j < oldCapacity; j++) {
            HashMapEntry<K, V> e = oldTable[j];
            if (e != null) {
                int highBit = e.hash & oldCapacity;
                HashMapEntry<K, V> broken = null;
                newTable[j | highBit] = e;
                for (HashMapEntry<K, V> n = e.next; n != null; n = n.next) {
                    int nextHighBit = n.hash & oldCapacity;
                    if (nextHighBit != highBit) {
                        if (broken == null) {
                            newTable[j | nextHighBit] = n;
                        } else {
                            broken.next = n;
                        }
                        broken = e;
                        highBit = nextHighBit;
                    }
                    e = n;
                }
                if (broken != null) {
                    broken.next = null;
                }
            }
        }
        return newTable;
    }

    public V remove(Object key) {
        if (key == null) {
            return removeNullKey();
        }
        int hash = Collections.secondaryHash(key);
        HashMapEntry<K, V>[] tab = this.table;
        int index = hash & (tab.length - 1);
        HashMapEntry<K, V> e = tab[index];
        HashMapEntry<K, V> prev = null;
        while (e != null) {
            if (e.hash == hash && key.equals(e.key)) {
                if (prev == null) {
                    tab[index] = e.next;
                } else {
                    prev.next = e.next;
                }
                this.modCount++;
                this.size--;
                postRemove(e);
                return e.value;
            }
            prev = e;
            e = e.next;
        }
        return null;
    }

    private V removeNullKey() {
        HashMapEntry<K, V> e = this.entryForNullKey;
        if (e == null) {
            return null;
        }
        this.entryForNullKey = null;
        this.modCount++;
        this.size--;
        postRemove(e);
        return e.value;
    }

    void postRemove(HashMapEntry<K, V> hashMapEntry) {
    }

    public void clear() {
        if (this.size != 0) {
            Arrays.fill(this.table, null);
            this.entryForNullKey = null;
            this.modCount++;
            this.size = 0;
        }
    }

    public Set<K> keySet() {
        Set<K> ks = this.keySet;
        if (ks != null) {
            return ks;
        }
        ks = new KeySet();
        this.keySet = ks;
        return ks;
    }

    public Collection<V> values() {
        Collection<V> vs = this.values;
        if (vs != null) {
            return vs;
        }
        vs = new Values();
        this.values = vs;
        return vs;
    }

    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> es = this.entrySet;
        if (es != null) {
            return es;
        }
        es = new EntrySet();
        this.entrySet = es;
        return es;
    }

    private boolean containsMapping(Object key, Object value) {
        HashMapEntry<K, V> e;
        if (key == null) {
            e = this.entryForNullKey;
            if (e == null || !Objects.equal(value, e.value)) {
                return false;
            }
            return true;
        }
        int hash = Collections.secondaryHash(key);
        HashMapEntry<K, V>[] tab = this.table;
        e = tab[hash & (tab.length - 1)];
        while (e != null) {
            if (e.hash == hash && key.equals(e.key)) {
                return Objects.equal(value, e.value);
            }
            e = e.next;
        }
        return false;
    }

    private boolean removeMapping(Object key, Object value) {
        HashMapEntry<K, V> e;
        if (key == null) {
            e = this.entryForNullKey;
            if (e == null || !Objects.equal(value, e.value)) {
                return false;
            }
            this.entryForNullKey = null;
            this.modCount++;
            this.size--;
            postRemove(e);
            return true;
        }
        int hash = Collections.secondaryHash(key);
        HashMapEntry<K, V>[] tab = this.table;
        int index = hash & (tab.length - 1);
        e = tab[index];
        HashMapEntry<K, V> prev = null;
        while (e != null) {
            if (e.hash != hash || !key.equals(e.key)) {
                prev = e;
                e = e.next;
            } else if (!Objects.equal(value, e.value)) {
                return false;
            } else {
                if (prev == null) {
                    tab[index] = e.next;
                } else {
                    prev.next = e.next;
                }
                this.modCount++;
                this.size--;
                postRemove(e);
                return true;
            }
        }
        return false;
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

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.putFields().put("loadFactor", (float) DEFAULT_LOAD_FACTOR);
        stream.writeFields();
        stream.writeInt(this.table.length);
        stream.writeInt(this.size);
        for (Entry<K, V> e : entrySet()) {
            stream.writeObject(e.getKey());
            stream.writeObject(e.getValue());
        }
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        int capacity = stream.readInt();
        if (capacity < 0) {
            throw new InvalidObjectException("Capacity: " + capacity);
        }
        if (capacity < MINIMUM_CAPACITY) {
            capacity = MINIMUM_CAPACITY;
        } else if (capacity > MAXIMUM_CAPACITY) {
            capacity = MAXIMUM_CAPACITY;
        } else {
            capacity = Collections.roundUpToPowerOfTwo(capacity);
        }
        makeTable(capacity);
        int size = stream.readInt();
        if (size < 0) {
            throw new InvalidObjectException("Size: " + size);
        }
        init();
        for (int i = 0; i < size; i++) {
            constructorPut(stream.readObject(), stream.readObject());
        }
    }
}
