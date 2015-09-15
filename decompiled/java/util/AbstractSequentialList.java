package java.util;

public abstract class AbstractSequentialList<E> extends AbstractList<E> {
    public abstract ListIterator<E> listIterator(int i);

    protected AbstractSequentialList() {
    }

    public void add(int location, E object) {
        listIterator(location).add(object);
    }

    public boolean addAll(int location, Collection<? extends E> collection) {
        ListIterator<E> it = listIterator(location);
        int next = it.nextIndex();
        for (Object add : collection) {
            it.add(add);
        }
        return next != it.nextIndex();
    }

    public E get(int location) {
        try {
            return listIterator(location).next();
        } catch (NoSuchElementException e) {
            throw new IndexOutOfBoundsException();
        }
    }

    public Iterator<E> iterator() {
        return listIterator(0);
    }

    public E remove(int location) {
        try {
            ListIterator<E> it = listIterator(location);
            E result = it.next();
            it.remove();
            return result;
        } catch (NoSuchElementException e) {
            throw new IndexOutOfBoundsException();
        }
    }

    public E set(int location, E object) {
        ListIterator<E> it = listIterator(location);
        if (it.hasNext()) {
            E result = it.next();
            it.set(object);
            return result;
        }
        throw new IndexOutOfBoundsException();
    }
}
