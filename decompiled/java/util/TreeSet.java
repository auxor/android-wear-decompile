package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map.Entry;

public class TreeSet<E> extends AbstractSet<E> implements NavigableSet<E>, Cloneable, Serializable {
    private static final long serialVersionUID = -2479143000061671589L;
    private transient NavigableMap<E, Object> backingMap;
    private transient NavigableSet<E> descendingSet;

    TreeSet(NavigableMap<E, Object> map) {
        this.backingMap = map;
    }

    public TreeSet() {
        this.backingMap = new TreeMap();
    }

    public TreeSet(Collection<? extends E> collection) {
        this();
        addAll(collection);
    }

    public TreeSet(Comparator<? super E> comparator) {
        this.backingMap = new TreeMap((Comparator) comparator);
    }

    public TreeSet(SortedSet<E> set) {
        this(set.comparator());
        for (E add : set) {
            add(add);
        }
    }

    public boolean add(E object) {
        return this.backingMap.put(object, Boolean.TRUE) == null;
    }

    public boolean addAll(Collection<? extends E> collection) {
        return super.addAll(collection);
    }

    public void clear() {
        this.backingMap.clear();
    }

    public Object clone() {
        try {
            TreeSet<E> clone = (TreeSet) super.clone();
            if (this.backingMap instanceof TreeMap) {
                clone.backingMap = (NavigableMap) ((TreeMap) this.backingMap).clone();
            } else {
                clone.backingMap = new TreeMap(this.backingMap);
            }
            return clone;
        } catch (Object e) {
            throw new AssertionError(e);
        }
    }

    public Comparator<? super E> comparator() {
        return this.backingMap.comparator();
    }

    public boolean contains(Object object) {
        return this.backingMap.containsKey(object);
    }

    public boolean isEmpty() {
        return this.backingMap.isEmpty();
    }

    public Iterator<E> iterator() {
        return this.backingMap.keySet().iterator();
    }

    public Iterator<E> descendingIterator() {
        return descendingSet().iterator();
    }

    public boolean remove(Object object) {
        return this.backingMap.remove(object) != null;
    }

    public int size() {
        return this.backingMap.size();
    }

    public E first() {
        return this.backingMap.firstKey();
    }

    public E last() {
        return this.backingMap.lastKey();
    }

    public E pollFirst() {
        Entry<E, Object> entry = this.backingMap.pollFirstEntry();
        return entry == null ? null : entry.getKey();
    }

    public E pollLast() {
        Entry<E, Object> entry = this.backingMap.pollLastEntry();
        return entry == null ? null : entry.getKey();
    }

    public E higher(E e) {
        return this.backingMap.higherKey(e);
    }

    public E lower(E e) {
        return this.backingMap.lowerKey(e);
    }

    public E ceiling(E e) {
        return this.backingMap.ceilingKey(e);
    }

    public E floor(E e) {
        return this.backingMap.floorKey(e);
    }

    public NavigableSet<E> descendingSet() {
        if (this.descendingSet != null) {
            return this.descendingSet;
        }
        NavigableSet<E> treeSet = new TreeSet(this.backingMap.descendingMap());
        this.descendingSet = treeSet;
        return treeSet;
    }

    public NavigableSet<E> subSet(E start, boolean startInclusive, E end, boolean endInclusive) {
        Comparator<? super E> c = this.backingMap.comparator();
        if ((c == null ? ((Comparable) start).compareTo(end) : c.compare(start, end)) <= 0) {
            return new TreeSet(this.backingMap.subMap(start, startInclusive, end, endInclusive));
        }
        throw new IllegalArgumentException();
    }

    public NavigableSet<E> headSet(E end, boolean endInclusive) {
        Comparator<? super E> c = this.backingMap.comparator();
        if (c == null) {
            ((Comparable) end).compareTo(end);
        } else {
            c.compare(end, end);
        }
        return new TreeSet(this.backingMap.headMap(end, endInclusive));
    }

    public NavigableSet<E> tailSet(E start, boolean startInclusive) {
        Comparator<? super E> c = this.backingMap.comparator();
        if (c == null) {
            ((Comparable) start).compareTo(start);
        } else {
            c.compare(start, start);
        }
        return new TreeSet(this.backingMap.tailMap(start, startInclusive));
    }

    public SortedSet<E> subSet(E start, E end) {
        return subSet(start, true, end, false);
    }

    public SortedSet<E> headSet(E end) {
        return headSet(end, false);
    }

    public SortedSet<E> tailSet(E start) {
        return tailSet(start, true);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeObject(this.backingMap.comparator());
        int size = this.backingMap.size();
        stream.writeInt(size);
        if (size > 0) {
            for (Object writeObject : this.backingMap.keySet()) {
                stream.writeObject(writeObject);
            }
        }
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        TreeMap<E, Object> map = new TreeMap((Comparator) stream.readObject());
        int size = stream.readInt();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                map.put(stream.readObject(), Boolean.TRUE);
            }
        }
        this.backingMap = map;
    }
}
