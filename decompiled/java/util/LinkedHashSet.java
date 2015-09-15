package java.util;

import java.io.Serializable;

public class LinkedHashSet<E> extends HashSet<E> implements Set<E>, Cloneable, Serializable {
    private static final long serialVersionUID = -2851667679971038690L;

    public LinkedHashSet() {
        super(new LinkedHashMap());
    }

    public LinkedHashSet(int capacity) {
        super(new LinkedHashMap(capacity));
    }

    public LinkedHashSet(int capacity, float loadFactor) {
        super(new LinkedHashMap(capacity, loadFactor));
    }

    public LinkedHashSet(Collection<? extends E> collection) {
        super(new LinkedHashMap(collection.size() < 6 ? 11 : collection.size() * 2));
        for (E e : collection) {
            add(e);
        }
    }

    HashMap<E, HashSet<E>> createBackingMap(int capacity, float loadFactor) {
        return new LinkedHashMap(capacity, loadFactor);
    }
}
