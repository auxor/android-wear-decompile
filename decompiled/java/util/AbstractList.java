package java.util;

public abstract class AbstractList<E> extends AbstractCollection<E> implements List<E> {
    protected transient int modCount;

    private class SimpleListIterator implements Iterator<E> {
        int expectedModCount;
        int lastPosition;
        int pos;

        SimpleListIterator() {
            this.pos = -1;
            this.lastPosition = -1;
            this.expectedModCount = AbstractList.this.modCount;
        }

        public boolean hasNext() {
            return this.pos + 1 < AbstractList.this.size();
        }

        public E next() {
            if (this.expectedModCount == AbstractList.this.modCount) {
                try {
                    E result = AbstractList.this.get(this.pos + 1);
                    int i = this.pos + 1;
                    this.pos = i;
                    this.lastPosition = i;
                    return result;
                } catch (IndexOutOfBoundsException e) {
                    throw new NoSuchElementException();
                }
            }
            throw new ConcurrentModificationException();
        }

        public void remove() {
            if (this.lastPosition == -1) {
                throw new IllegalStateException();
            } else if (this.expectedModCount != AbstractList.this.modCount) {
                throw new ConcurrentModificationException();
            } else {
                try {
                    AbstractList.this.remove(this.lastPosition);
                    this.expectedModCount = AbstractList.this.modCount;
                    if (this.pos == this.lastPosition) {
                        this.pos--;
                    }
                    this.lastPosition = -1;
                } catch (IndexOutOfBoundsException e) {
                    throw new ConcurrentModificationException();
                }
            }
        }
    }

    private final class FullListIterator extends SimpleListIterator implements ListIterator<E> {
        FullListIterator(int start) {
            super();
            if (start < 0 || start > AbstractList.this.size()) {
                throw new IndexOutOfBoundsException();
            }
            this.pos = start - 1;
        }

        public void add(E object) {
            if (this.expectedModCount == AbstractList.this.modCount) {
                try {
                    AbstractList.this.add(this.pos + 1, object);
                    this.pos++;
                    this.lastPosition = -1;
                    if (AbstractList.this.modCount != this.expectedModCount) {
                        this.expectedModCount = AbstractList.this.modCount;
                        return;
                    }
                    return;
                } catch (IndexOutOfBoundsException e) {
                    throw new NoSuchElementException();
                }
            }
            throw new ConcurrentModificationException();
        }

        public boolean hasPrevious() {
            return this.pos >= 0;
        }

        public int nextIndex() {
            return this.pos + 1;
        }

        public E previous() {
            if (this.expectedModCount == AbstractList.this.modCount) {
                try {
                    E result = AbstractList.this.get(this.pos);
                    this.lastPosition = this.pos;
                    this.pos--;
                    return result;
                } catch (IndexOutOfBoundsException e) {
                    throw new NoSuchElementException();
                }
            }
            throw new ConcurrentModificationException();
        }

        public int previousIndex() {
            return this.pos;
        }

        public void set(E object) {
            if (this.expectedModCount == AbstractList.this.modCount) {
                try {
                    AbstractList.this.set(this.lastPosition, object);
                    return;
                } catch (IndexOutOfBoundsException e) {
                    throw new IllegalStateException();
                }
            }
            throw new ConcurrentModificationException();
        }
    }

    private static class SubAbstractList<E> extends AbstractList<E> {
        private final AbstractList<E> fullList;
        private int offset;
        private int size;

        private static final class SubAbstractListIterator<E> implements ListIterator<E> {
            private int end;
            private final ListIterator<E> iterator;
            private int start;
            private final SubAbstractList<E> subList;

            SubAbstractListIterator(ListIterator<E> it, SubAbstractList<E> list, int offset, int length) {
                this.iterator = it;
                this.subList = list;
                this.start = offset;
                this.end = this.start + length;
            }

            public void add(E object) {
                this.iterator.add(object);
                this.subList.sizeChanged(true);
                this.end++;
            }

            public boolean hasNext() {
                return this.iterator.nextIndex() < this.end;
            }

            public boolean hasPrevious() {
                return this.iterator.previousIndex() >= this.start;
            }

            public E next() {
                if (this.iterator.nextIndex() < this.end) {
                    return this.iterator.next();
                }
                throw new NoSuchElementException();
            }

            public int nextIndex() {
                return this.iterator.nextIndex() - this.start;
            }

            public E previous() {
                if (this.iterator.previousIndex() >= this.start) {
                    return this.iterator.previous();
                }
                throw new NoSuchElementException();
            }

            public int previousIndex() {
                int previous = this.iterator.previousIndex();
                if (previous >= this.start) {
                    return previous - this.start;
                }
                return -1;
            }

            public void remove() {
                this.iterator.remove();
                this.subList.sizeChanged(false);
                this.end--;
            }

            public void set(E object) {
                this.iterator.set(object);
            }
        }

        SubAbstractList(AbstractList<E> list, int start, int end) {
            this.fullList = list;
            this.modCount = this.fullList.modCount;
            this.offset = start;
            this.size = end - start;
        }

        public void add(int location, E object) {
            if (this.modCount != this.fullList.modCount) {
                throw new ConcurrentModificationException();
            } else if (location < 0 || location > this.size) {
                throw new IndexOutOfBoundsException();
            } else {
                this.fullList.add(this.offset + location, object);
                this.size++;
                this.modCount = this.fullList.modCount;
            }
        }

        public boolean addAll(int location, Collection<? extends E> collection) {
            if (this.modCount != this.fullList.modCount) {
                throw new ConcurrentModificationException();
            } else if (location < 0 || location > this.size) {
                throw new IndexOutOfBoundsException();
            } else {
                boolean result = this.fullList.addAll(this.offset + location, collection);
                if (result) {
                    this.size += collection.size();
                    this.modCount = this.fullList.modCount;
                }
                return result;
            }
        }

        public boolean addAll(Collection<? extends E> collection) {
            if (this.modCount == this.fullList.modCount) {
                boolean result = this.fullList.addAll(this.offset + this.size, collection);
                if (result) {
                    this.size += collection.size();
                    this.modCount = this.fullList.modCount;
                }
                return result;
            }
            throw new ConcurrentModificationException();
        }

        public E get(int location) {
            if (this.modCount != this.fullList.modCount) {
                throw new ConcurrentModificationException();
            } else if (location >= 0 && location < this.size) {
                return this.fullList.get(this.offset + location);
            } else {
                throw new IndexOutOfBoundsException();
            }
        }

        public Iterator<E> iterator() {
            return listIterator(0);
        }

        public ListIterator<E> listIterator(int location) {
            if (this.modCount != this.fullList.modCount) {
                throw new ConcurrentModificationException();
            } else if (location >= 0 && location <= this.size) {
                return new SubAbstractListIterator(this.fullList.listIterator(this.offset + location), this, this.offset, this.size);
            } else {
                throw new IndexOutOfBoundsException();
            }
        }

        public E remove(int location) {
            if (this.modCount != this.fullList.modCount) {
                throw new ConcurrentModificationException();
            } else if (location < 0 || location >= this.size) {
                throw new IndexOutOfBoundsException();
            } else {
                E result = this.fullList.remove(this.offset + location);
                this.size--;
                this.modCount = this.fullList.modCount;
                return result;
            }
        }

        protected void removeRange(int start, int end) {
            if (start == end) {
                return;
            }
            if (this.modCount == this.fullList.modCount) {
                this.fullList.removeRange(this.offset + start, this.offset + end);
                this.size -= end - start;
                this.modCount = this.fullList.modCount;
                return;
            }
            throw new ConcurrentModificationException();
        }

        public E set(int location, E object) {
            if (this.modCount != this.fullList.modCount) {
                throw new ConcurrentModificationException();
            } else if (location >= 0 && location < this.size) {
                return this.fullList.set(this.offset + location, object);
            } else {
                throw new IndexOutOfBoundsException();
            }
        }

        public int size() {
            if (this.modCount == this.fullList.modCount) {
                return this.size;
            }
            throw new ConcurrentModificationException();
        }

        void sizeChanged(boolean increment) {
            if (increment) {
                this.size++;
            } else {
                this.size--;
            }
            this.modCount = this.fullList.modCount;
        }
    }

    private static final class SubAbstractListRandomAccess<E> extends SubAbstractList<E> implements RandomAccess {
        SubAbstractListRandomAccess(AbstractList<E> list, int start, int end) {
            super(list, start, end);
        }
    }

    public abstract E get(int i);

    protected AbstractList() {
    }

    public void add(int location, E e) {
        throw new UnsupportedOperationException();
    }

    public boolean add(E object) {
        add(size(), object);
        return true;
    }

    public boolean addAll(int location, Collection<? extends E> collection) {
        for (Object add : collection) {
            int location2 = location + 1;
            add(location, add);
            location = location2;
        }
        return !collection.isEmpty();
    }

    public void clear() {
        removeRange(0, size());
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof List)) {
            return false;
        }
        List<?> list = (List) object;
        if (list.size() != size()) {
            return false;
        }
        Iterator<?> it1 = iterator();
        Iterator<?> it2 = list.iterator();
        while (it1.hasNext()) {
            Object e1 = it1.next();
            Object e2 = it2.next();
            if (e1 == null) {
                if (e2 != null) {
                }
            } else if (e1.equals(e2)) {
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = 1;
        Iterator<?> it = iterator();
        while (it.hasNext()) {
            Object object = it.next();
            result = (result * 31) + (object == null ? 0 : object.hashCode());
        }
        return result;
    }

    public int indexOf(Object object) {
        ListIterator<?> it = listIterator();
        if (object != null) {
            while (it.hasNext()) {
                if (object.equals(it.next())) {
                    return it.previousIndex();
                }
            }
        }
        while (it.hasNext()) {
            if (it.next() == null) {
                return it.previousIndex();
            }
        }
        return -1;
    }

    public Iterator<E> iterator() {
        return new SimpleListIterator();
    }

    public int lastIndexOf(Object object) {
        ListIterator<?> it = listIterator(size());
        if (object != null) {
            while (it.hasPrevious()) {
                if (object.equals(it.previous())) {
                    return it.nextIndex();
                }
            }
        }
        while (it.hasPrevious()) {
            if (it.previous() == null) {
                return it.nextIndex();
            }
        }
        return -1;
    }

    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    public ListIterator<E> listIterator(int location) {
        return new FullListIterator(location);
    }

    public E remove(int location) {
        throw new UnsupportedOperationException();
    }

    protected void removeRange(int start, int end) {
        Iterator<?> it = listIterator(start);
        for (int i = start; i < end; i++) {
            it.next();
            it.remove();
        }
    }

    public E set(int location, E e) {
        throw new UnsupportedOperationException();
    }

    public List<E> subList(int start, int end) {
        if (start < 0 || end > size()) {
            throw new IndexOutOfBoundsException();
        } else if (start > end) {
            throw new IllegalArgumentException();
        } else if (this instanceof RandomAccess) {
            return new SubAbstractListRandomAccess(this, start, end);
        } else {
            return new SubAbstractList(this, start, end);
        }
    }
}
