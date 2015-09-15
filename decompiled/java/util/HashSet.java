package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class HashSet<E> extends AbstractSet<E> implements Set<E>, Cloneable, Serializable {
    private static final long serialVersionUID = -5024744406713321676L;
    transient HashMap<E, HashSet<E>> backingMap;

    public HashSet() {
        this(new HashMap());
    }

    public HashSet(int capacity) {
        this(new HashMap(capacity));
    }

    public HashSet(int capacity, float loadFactor) {
        this(new HashMap(capacity, loadFactor));
    }

    public HashSet(Collection<? extends E> collection) {
        this(new HashMap(collection.size() < 6 ? 11 : collection.size() * 2));
        for (E e : collection) {
            add(e);
        }
    }

    HashSet(HashMap<E, HashSet<E>> backingMap) {
        this.backingMap = backingMap;
    }

    public boolean add(E object) {
        return this.backingMap.put(object, this) == null;
    }

    public void clear() {
        this.backingMap.clear();
    }

    public Object clone() {
        try {
            HashSet<E> clone = (HashSet) super.clone();
            clone.backingMap = (HashMap) this.backingMap.clone();
            return clone;
        } catch (Object e) {
            throw new AssertionError(e);
        }
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

    public boolean remove(Object object) {
        return this.backingMap.remove(object) != null;
    }

    public int size() {
        return this.backingMap.size();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeInt(this.backingMap.table.length);
        stream.writeFloat(0.75f);
        stream.writeInt(size());
        Iterator i$ = iterator();
        while (i$.hasNext()) {
            stream.writeObject(i$.next());
        }
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.backingMap = createBackingMap(stream.readInt(), stream.readFloat());
        int i = stream.readInt();
        while (true) {
            i--;
            if (i >= 0) {
                this.backingMap.put(stream.readObject(), this);
            } else {
                return;
            }
        }
    }

    HashMap<E, HashSet<E>> createBackingMap(int capacity, float loadFactor) {
        return new HashMap(capacity, loadFactor);
    }
}
