package java.util;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import libcore.util.EmptyArray;

public class ArrayList<E> extends AbstractList<E> implements Cloneable, Serializable, RandomAccess {
    private static final int MIN_CAPACITY_INCREMENT = 12;
    private static final long serialVersionUID = 8683452581122892189L;
    transient Object[] array;
    int size;

    private class ArrayListIterator implements Iterator<E> {
        private int expectedModCount;
        private int remaining;
        private int removalIndex;

        private ArrayListIterator() {
            this.remaining = ArrayList.this.size;
            this.removalIndex = -1;
            this.expectedModCount = ArrayList.this.modCount;
        }

        public boolean hasNext() {
            return this.remaining != 0;
        }

        public E next() {
            ArrayList<E> ourList = ArrayList.this;
            int rem = this.remaining;
            if (ourList.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            } else if (rem == 0) {
                throw new NoSuchElementException();
            } else {
                this.remaining = rem - 1;
                Object[] objArr = ourList.array;
                int i = ourList.size - rem;
                this.removalIndex = i;
                return objArr[i];
            }
        }

        public void remove() {
            Object a = ArrayList.this.array;
            int removalIdx = this.removalIndex;
            if (ArrayList.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            } else if (removalIdx < 0) {
                throw new IllegalStateException();
            } else {
                System.arraycopy(a, removalIdx + 1, a, removalIdx, this.remaining);
                ArrayList arrayList = ArrayList.this;
                int i = arrayList.size - 1;
                arrayList.size = i;
                a[i] = null;
                this.removalIndex = -1;
                arrayList = ArrayList.this;
                i = arrayList.modCount + 1;
                arrayList.modCount = i;
                this.expectedModCount = i;
            }
        }
    }

    public ArrayList(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity < 0: " + capacity);
        }
        this.array = capacity == 0 ? EmptyArray.OBJECT : new Object[capacity];
    }

    public ArrayList() {
        this.array = EmptyArray.OBJECT;
    }

    public ArrayList(Collection<? extends E> collection) {
        if (collection == null) {
            throw new NullPointerException("collection == null");
        }
        Object[] a = collection.toArray();
        if (a.getClass() != Object[].class) {
            Object newArray = new Object[a.length];
            System.arraycopy((Object) a, 0, newArray, 0, a.length);
            a = newArray;
        }
        this.array = a;
        this.size = a.length;
    }

    public boolean add(E object) {
        Object[] a = this.array;
        int s = this.size;
        if (s == a.length) {
            Object newArray = new Object[((s < 6 ? MIN_CAPACITY_INCREMENT : s >> 1) + s)];
            System.arraycopy((Object) a, 0, newArray, 0, s);
            a = newArray;
            this.array = newArray;
        }
        a[s] = object;
        this.size = s + 1;
        this.modCount++;
        return true;
    }

    public void add(int index, E object) {
        Object a = this.array;
        int s = this.size;
        if (index > s || index < 0) {
            throwIndexOutOfBoundsException(index, s);
        }
        if (s < a.length) {
            System.arraycopy(a, index, a, index + 1, s - index);
        } else {
            Object newArray = new Object[newCapacity(s)];
            System.arraycopy(a, 0, newArray, 0, index);
            System.arraycopy(a, index, newArray, index + 1, s - index);
            a = newArray;
            this.array = newArray;
        }
        a[index] = object;
        this.size = s + 1;
        this.modCount++;
    }

    private static int newCapacity(int currentCapacity) {
        return currentCapacity + (currentCapacity < 6 ? MIN_CAPACITY_INCREMENT : currentCapacity >> 1);
    }

    public boolean addAll(Collection<? extends E> collection) {
        Object newPart = collection.toArray();
        int newPartSize = newPart.length;
        if (newPartSize == 0) {
            return false;
        }
        Object a = this.array;
        int s = this.size;
        int newSize = s + newPartSize;
        if (newSize > a.length) {
            Object newArray = new Object[newCapacity(newSize - 1)];
            System.arraycopy(a, 0, newArray, 0, s);
            a = newArray;
            this.array = newArray;
        }
        System.arraycopy(newPart, 0, a, s, newPartSize);
        this.size = newSize;
        this.modCount++;
        return true;
    }

    public boolean addAll(int index, Collection<? extends E> collection) {
        int s = this.size;
        if (index > s || index < 0) {
            throwIndexOutOfBoundsException(index, s);
        }
        Object newPart = collection.toArray();
        int newPartSize = newPart.length;
        if (newPartSize == 0) {
            return false;
        }
        Object a = this.array;
        int newSize = s + newPartSize;
        if (newSize <= a.length) {
            System.arraycopy(a, index, a, index + newPartSize, s - index);
        } else {
            Object newArray = new Object[newCapacity(newSize - 1)];
            System.arraycopy(a, 0, newArray, 0, index);
            System.arraycopy(a, index, newArray, index + newPartSize, s - index);
            a = newArray;
            this.array = newArray;
        }
        System.arraycopy(newPart, 0, a, index, newPartSize);
        this.size = newSize;
        this.modCount++;
        return true;
    }

    static IndexOutOfBoundsException throwIndexOutOfBoundsException(int index, int size) {
        throw new IndexOutOfBoundsException("Invalid index " + index + ", size is " + size);
    }

    public void clear() {
        if (this.size != 0) {
            Arrays.fill(this.array, 0, this.size, null);
            this.size = 0;
            this.modCount++;
        }
    }

    public Object clone() {
        try {
            ArrayList<?> result = (ArrayList) super.clone();
            result.array = (Object[]) this.array.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public void ensureCapacity(int minimumCapacity) {
        Object a = this.array;
        if (a.length < minimumCapacity) {
            Object newArray = new Object[minimumCapacity];
            System.arraycopy(a, 0, newArray, 0, this.size);
            this.array = newArray;
            this.modCount++;
        }
    }

    public E get(int index) {
        if (index >= this.size) {
            throwIndexOutOfBoundsException(index, this.size);
        }
        return this.array[index];
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public boolean contains(Object object) {
        Object[] a = this.array;
        int s = this.size;
        int i;
        if (object != null) {
            for (i = 0; i < s; i++) {
                if (object.equals(a[i])) {
                    return true;
                }
            }
        } else {
            for (i = 0; i < s; i++) {
                if (a[i] == null) {
                    return true;
                }
            }
        }
        return false;
    }

    public int indexOf(Object object) {
        Object[] a = this.array;
        int s = this.size;
        int i;
        if (object != null) {
            for (i = 0; i < s; i++) {
                if (object.equals(a[i])) {
                    return i;
                }
            }
        } else {
            for (i = 0; i < s; i++) {
                if (a[i] == null) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int lastIndexOf(Object object) {
        Object[] a = this.array;
        int i;
        if (object != null) {
            for (i = this.size - 1; i >= 0; i--) {
                if (object.equals(a[i])) {
                    return i;
                }
            }
        } else {
            for (i = this.size - 1; i >= 0; i--) {
                if (a[i] == null) {
                    return i;
                }
            }
        }
        return -1;
    }

    public E remove(int index) {
        Object a = this.array;
        int s = this.size;
        if (index >= s) {
            throwIndexOutOfBoundsException(index, s);
        }
        E result = a[index];
        s--;
        System.arraycopy(a, index + 1, a, index, s - index);
        a[s] = null;
        this.size = s;
        this.modCount++;
        return result;
    }

    public boolean remove(Object object) {
        Object a = this.array;
        int s = this.size;
        int i;
        if (object != null) {
            for (i = 0; i < s; i++) {
                if (object.equals(a[i])) {
                    s--;
                    System.arraycopy(a, i + 1, a, i, s - i);
                    a[s] = null;
                    this.size = s;
                    this.modCount++;
                    return true;
                }
            }
        } else {
            for (i = 0; i < s; i++) {
                if (a[i] == null) {
                    s--;
                    System.arraycopy(a, i + 1, a, i, s - i);
                    a[s] = null;
                    this.size = s;
                    this.modCount++;
                    return true;
                }
            }
        }
        return false;
    }

    protected void removeRange(int fromIndex, int toIndex) {
        if (fromIndex != toIndex) {
            Object[] a = this.array;
            int s = this.size;
            if (fromIndex >= s) {
                throw new IndexOutOfBoundsException("fromIndex " + fromIndex + " >= size " + this.size);
            } else if (toIndex > s) {
                throw new IndexOutOfBoundsException("toIndex " + toIndex + " > size " + this.size);
            } else if (fromIndex > toIndex) {
                throw new IndexOutOfBoundsException("fromIndex " + fromIndex + " > toIndex " + toIndex);
            } else {
                System.arraycopy((Object) a, toIndex, (Object) a, fromIndex, s - toIndex);
                int rangeSize = toIndex - fromIndex;
                Arrays.fill(a, s - rangeSize, s, null);
                this.size = s - rangeSize;
                this.modCount++;
            }
        }
    }

    public E set(int index, E object) {
        Object[] a = this.array;
        if (index >= this.size) {
            throwIndexOutOfBoundsException(index, this.size);
        }
        E result = a[index];
        a[index] = object;
        return result;
    }

    public Object[] toArray() {
        int s = this.size;
        Object result = new Object[s];
        System.arraycopy(this.array, 0, result, 0, s);
        return result;
    }

    public <T> T[] toArray(T[] contents) {
        Object contents2;
        int s = this.size;
        if (contents.length < s) {
            contents2 = (Object[]) ((Object[]) Array.newInstance(contents.getClass().getComponentType(), s));
        }
        System.arraycopy(this.array, 0, contents2, 0, s);
        if (contents2.length > s) {
            contents2[s] = null;
        }
        return contents2;
    }

    public void trimToSize() {
        int s = this.size;
        if (s != this.array.length) {
            if (s == 0) {
                this.array = EmptyArray.OBJECT;
            } else {
                Object newArray = new Object[s];
                System.arraycopy(this.array, 0, newArray, 0, s);
                this.array = newArray;
            }
            this.modCount++;
        }
    }

    public Iterator<E> iterator() {
        return new ArrayListIterator();
    }

    public int hashCode() {
        Object[] a = this.array;
        int hashCode = 1;
        int s = this.size;
        for (int i = 0; i < s; i++) {
            Object e = a[i];
            hashCode = (hashCode * 31) + (e == null ? 0 : e.hashCode());
        }
        return hashCode;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof List)) {
            return false;
        }
        List<?> that = (List) o;
        int s = this.size;
        if (that.size() != s) {
            return false;
        }
        Object[] a = this.array;
        int i;
        Object eThis;
        if (that instanceof RandomAccess) {
            i = 0;
            while (i < s) {
                eThis = a[i];
                Object ethat = that.get(i);
                if (eThis == null) {
                    if (ethat == null) {
                        i++;
                    }
                } else if (eThis.equals(ethat)) {
                    i++;
                }
                return false;
            }
            return true;
        }
        Iterator<?> it = that.iterator();
        i = 0;
        while (i < s) {
            eThis = a[i];
            Object eThat = it.next();
            if (eThis == null) {
                if (eThat == null) {
                    i++;
                }
            } else if (eThis.equals(eThat)) {
                i++;
            }
            return false;
        }
        return true;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeInt(this.array.length);
        for (int i = 0; i < this.size; i++) {
            stream.writeObject(this.array[i]);
        }
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        int cap = stream.readInt();
        if (cap < this.size) {
            throw new InvalidObjectException("Capacity: " + cap + " < size: " + this.size);
        }
        this.array = cap == 0 ? EmptyArray.OBJECT : new Object[cap];
        for (int i = 0; i < this.size; i++) {
            this.array[i] = stream.readObject();
        }
    }
}
