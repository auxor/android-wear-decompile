package java.util.concurrent;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class CopyOnWriteArraySet<E> extends AbstractSet<E> implements Serializable {
    private static final long serialVersionUID = 5457747651344034263L;
    private final CopyOnWriteArrayList<E> al;

    public CopyOnWriteArraySet() {
        this.al = new CopyOnWriteArrayList();
    }

    public CopyOnWriteArraySet(Collection<? extends E> c) {
        this.al = new CopyOnWriteArrayList();
        this.al.addAllAbsent(c);
    }

    public int size() {
        return this.al.size();
    }

    public boolean isEmpty() {
        return this.al.isEmpty();
    }

    public boolean contains(Object o) {
        return this.al.contains(o);
    }

    public Object[] toArray() {
        return this.al.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return this.al.toArray(a);
    }

    public void clear() {
        this.al.clear();
    }

    public boolean remove(Object o) {
        return this.al.remove(o);
    }

    public boolean add(E e) {
        return this.al.addIfAbsent(e);
    }

    public boolean containsAll(Collection<?> c) {
        return this.al.containsAll(c);
    }

    public boolean addAll(Collection<? extends E> c) {
        return this.al.addAllAbsent(c) > 0;
    }

    public boolean removeAll(Collection<?> c) {
        return this.al.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return this.al.retainAll(c);
    }

    public Iterator<E> iterator() {
        return this.al.iterator();
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (o == this) {
            return true;
        }
        if (!(o instanceof Set)) {
            return false;
        }
        Object[] elements = this.al.getArray();
        int len = elements.length;
        boolean[] matched = new boolean[len];
        int k = 0;
        for (Object x : (Set) o) {
            k++;
            if (k > len) {
                return false;
            }
            int i = 0;
            while (i < len) {
                if (matched[i] || !eq(x, elements[i])) {
                    i++;
                } else {
                    matched[i] = true;
                }
            }
            return false;
        }
        if (k != len) {
            z = false;
        }
        return z;
    }

    private static boolean eq(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        } else {
            return o1.equals(o2);
        }
    }
}
