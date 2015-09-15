package java.util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;

public class Vector<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, Serializable {
    private static final int DEFAULT_SIZE = 10;
    private static final long serialVersionUID = -2767605614048989439L;
    protected int capacityIncrement;
    protected int elementCount;
    protected Object[] elementData;

    public Vector() {
        this(DEFAULT_SIZE, 0);
    }

    public Vector(int capacity) {
        this(capacity, 0);
    }

    public Vector(int capacity, int capacityIncrement) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity < 0: " + capacity);
        }
        this.elementData = newElementArray(capacity);
        this.elementCount = 0;
        this.capacityIncrement = capacityIncrement;
    }

    public Vector(Collection<? extends E> collection) {
        this(collection.size(), 0);
        for (Object obj : collection) {
            Object[] objArr = this.elementData;
            int i = this.elementCount;
            this.elementCount = i + 1;
            objArr[i] = obj;
        }
    }

    private E[] newElementArray(int size) {
        return new Object[size];
    }

    public void add(int location, E object) {
        insertElementAt(object, location);
    }

    public synchronized boolean add(E object) {
        if (this.elementCount == this.elementData.length) {
            growByOne();
        }
        Object[] objArr = this.elementData;
        int i = this.elementCount;
        this.elementCount = i + 1;
        objArr[i] = object;
        this.modCount++;
        return true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized boolean addAll(int r9, java.util.Collection<? extends E> r10) {
        /*
        r8 = this;
        monitor-enter(r8);
        if (r9 < 0) goto L_0x0051;
    L_0x0003:
        r5 = r8.elementCount;	 Catch:{ all -> 0x0058 }
        if (r9 > r5) goto L_0x0051;
    L_0x0007:
        r4 = r10.size();	 Catch:{ all -> 0x0058 }
        if (r4 != 0) goto L_0x0010;
    L_0x000d:
        r5 = 0;
    L_0x000e:
        monitor-exit(r8);
        return r5;
    L_0x0010:
        r5 = r8.elementData;	 Catch:{ all -> 0x0058 }
        r5 = r5.length;	 Catch:{ all -> 0x0058 }
        r6 = r8.elementCount;	 Catch:{ all -> 0x0058 }
        r5 = r5 - r6;
        r3 = r4 - r5;
        if (r3 <= 0) goto L_0x001d;
    L_0x001a:
        r8.growBy(r3);	 Catch:{ all -> 0x0058 }
    L_0x001d:
        r5 = r8.elementCount;	 Catch:{ all -> 0x0058 }
        r0 = r5 - r9;
        if (r0 <= 0) goto L_0x002c;
    L_0x0023:
        r5 = r8.elementData;	 Catch:{ all -> 0x0058 }
        r6 = r8.elementData;	 Catch:{ all -> 0x0058 }
        r7 = r9 + r4;
        java.lang.System.arraycopy(r5, r9, r6, r7, r0);	 Catch:{ all -> 0x0058 }
    L_0x002c:
        r1 = r10.iterator();	 Catch:{ all -> 0x0058 }
        r2 = r9;
    L_0x0031:
        r5 = r1.hasNext();	 Catch:{ all -> 0x005b }
        if (r5 == 0) goto L_0x0043;
    L_0x0037:
        r5 = r8.elementData;	 Catch:{ all -> 0x005b }
        r9 = r2 + 1;
        r6 = r1.next();	 Catch:{ all -> 0x0058 }
        r5[r2] = r6;	 Catch:{ all -> 0x0058 }
        r2 = r9;
        goto L_0x0031;
    L_0x0043:
        r5 = r8.elementCount;	 Catch:{ all -> 0x005b }
        r5 = r5 + r4;
        r8.elementCount = r5;	 Catch:{ all -> 0x005b }
        r5 = r8.modCount;	 Catch:{ all -> 0x005b }
        r5 = r5 + 1;
        r8.modCount = r5;	 Catch:{ all -> 0x005b }
        r5 = 1;
        r9 = r2;
        goto L_0x000e;
    L_0x0051:
        r5 = r8.elementCount;	 Catch:{ all -> 0x0058 }
        r5 = arrayIndexOutOfBoundsException(r9, r5);	 Catch:{ all -> 0x0058 }
        throw r5;	 Catch:{ all -> 0x0058 }
    L_0x0058:
        r5 = move-exception;
    L_0x0059:
        monitor-exit(r8);
        throw r5;
    L_0x005b:
        r5 = move-exception;
        r9 = r2;
        goto L_0x0059;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.Vector.addAll(int, java.util.Collection):boolean");
    }

    public synchronized boolean addAll(Collection<? extends E> collection) {
        return addAll(this.elementCount, collection);
    }

    public synchronized void addElement(E object) {
        if (this.elementCount == this.elementData.length) {
            growByOne();
        }
        Object[] objArr = this.elementData;
        int i = this.elementCount;
        this.elementCount = i + 1;
        objArr[i] = object;
        this.modCount++;
    }

    public synchronized int capacity() {
        return this.elementData.length;
    }

    public void clear() {
        removeAllElements();
    }

    public synchronized Object clone() {
        Vector<E> vector;
        try {
            vector = (Vector) super.clone();
            vector.elementData = (Object[]) this.elementData.clone();
        } catch (Object e) {
            throw new AssertionError(e);
        }
        return vector;
    }

    public boolean contains(Object object) {
        return indexOf(object, 0) != -1;
    }

    public synchronized boolean containsAll(Collection<?> collection) {
        return super.containsAll(collection);
    }

    public synchronized void copyInto(Object[] elements) {
        System.arraycopy(this.elementData, 0, (Object) elements, 0, this.elementCount);
    }

    public synchronized E elementAt(int location) {
        if (location < this.elementCount) {
        } else {
            throw arrayIndexOutOfBoundsException(location, this.elementCount);
        }
        return this.elementData[location];
    }

    public Enumeration<E> elements() {
        return new Enumeration<E>() {
            int pos;

            {
                this.pos = 0;
            }

            public boolean hasMoreElements() {
                return this.pos < Vector.this.elementCount;
            }

            public E nextElement() {
                synchronized (Vector.this) {
                    if (this.pos < Vector.this.elementCount) {
                        Object[] objArr = Vector.this.elementData;
                        int i = this.pos;
                        this.pos = i + 1;
                        E e = objArr[i];
                        return e;
                    }
                    throw new NoSuchElementException();
                }
            }
        };
    }

    public synchronized void ensureCapacity(int minimumCapacity) {
        if (this.elementData.length < minimumCapacity) {
            int next = (this.capacityIncrement <= 0 ? this.elementData.length : this.capacityIncrement) + this.elementData.length;
            if (minimumCapacity <= next) {
                minimumCapacity = next;
            }
            grow(minimumCapacity);
        }
    }

    public synchronized boolean equals(Object object) {
        boolean z = true;
        synchronized (this) {
            if (this != object) {
                if (object instanceof List) {
                    List<?> list = (List) object;
                    if (list.size() != this.elementCount) {
                        z = false;
                    } else {
                        int index = 0;
                        for (Object e2 : list) {
                            int index2 = index + 1;
                            Object e1 = this.elementData[index];
                            if (e1 == null) {
                                if (e2 != null) {
                                    z = false;
                                    break;
                                }
                            } else if (!e1.equals(e2)) {
                                z = false;
                                break;
                            }
                            index = index2;
                        }
                    }
                } else {
                    z = false;
                }
            }
        }
        return z;
    }

    public synchronized E firstElement() {
        if (this.elementCount > 0) {
        } else {
            throw new NoSuchElementException();
        }
        return this.elementData[0];
    }

    public E get(int location) {
        return elementAt(location);
    }

    private void grow(int newCapacity) {
        Object newData = newElementArray(newCapacity);
        System.arraycopy(this.elementData, 0, newData, 0, this.elementCount);
        this.elementData = newData;
    }

    private void growByOne() {
        int adding;
        if (this.capacityIncrement <= 0) {
            adding = this.elementData.length;
            if (adding == 0) {
                adding = 1;
            }
        } else {
            adding = this.capacityIncrement;
        }
        Object newData = newElementArray(this.elementData.length + adding);
        System.arraycopy(this.elementData, 0, newData, 0, this.elementCount);
        this.elementData = newData;
    }

    private void growBy(int required) {
        int adding;
        if (this.capacityIncrement <= 0) {
            adding = this.elementData.length;
            if (adding == 0) {
                adding = required;
            }
            while (adding < required) {
                adding += adding;
            }
        } else {
            adding = (required / this.capacityIncrement) * this.capacityIncrement;
            if (adding < required) {
                adding += this.capacityIncrement;
            }
        }
        Object newData = newElementArray(this.elementData.length + adding);
        System.arraycopy(this.elementData, 0, newData, 0, this.elementCount);
        this.elementData = newData;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int hashCode() {
        /*
        r4 = this;
        monitor-enter(r4);
        r1 = 1;
        r0 = 0;
    L_0x0003:
        r2 = r4.elementCount;	 Catch:{ all -> 0x0020 }
        if (r0 >= r2) goto L_0x001e;
    L_0x0007:
        r3 = r1 * 31;
        r2 = r4.elementData;	 Catch:{ all -> 0x0020 }
        r2 = r2[r0];	 Catch:{ all -> 0x0020 }
        if (r2 != 0) goto L_0x0015;
    L_0x000f:
        r2 = 0;
    L_0x0010:
        r1 = r3 + r2;
        r0 = r0 + 1;
        goto L_0x0003;
    L_0x0015:
        r2 = r4.elementData;	 Catch:{ all -> 0x0020 }
        r2 = r2[r0];	 Catch:{ all -> 0x0020 }
        r2 = r2.hashCode();	 Catch:{ all -> 0x0020 }
        goto L_0x0010;
    L_0x001e:
        monitor-exit(r4);
        return r1;
    L_0x0020:
        r2 = move-exception;
        monitor-exit(r4);
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.Vector.hashCode():int");
    }

    public int indexOf(Object object) {
        return indexOf(object, 0);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int indexOf(java.lang.Object r3, int r4) {
        /*
        r2 = this;
        monitor-enter(r2);
        if (r3 == 0) goto L_0x0018;
    L_0x0003:
        r0 = r4;
    L_0x0004:
        r1 = r2.elementCount;	 Catch:{ all -> 0x002a }
        if (r0 >= r1) goto L_0x0028;
    L_0x0008:
        r1 = r2.elementData;	 Catch:{ all -> 0x002a }
        r1 = r1[r0];	 Catch:{ all -> 0x002a }
        r1 = r3.equals(r1);	 Catch:{ all -> 0x002a }
        if (r1 == 0) goto L_0x0015;
    L_0x0012:
        r1 = r0;
    L_0x0013:
        monitor-exit(r2);
        return r1;
    L_0x0015:
        r0 = r0 + 1;
        goto L_0x0004;
    L_0x0018:
        r0 = r4;
    L_0x0019:
        r1 = r2.elementCount;	 Catch:{ all -> 0x002a }
        if (r0 >= r1) goto L_0x0028;
    L_0x001d:
        r1 = r2.elementData;	 Catch:{ all -> 0x002a }
        r1 = r1[r0];	 Catch:{ all -> 0x002a }
        if (r1 != 0) goto L_0x0025;
    L_0x0023:
        r1 = r0;
        goto L_0x0013;
    L_0x0025:
        r0 = r0 + 1;
        goto L_0x0019;
    L_0x0028:
        r1 = -1;
        goto L_0x0013;
    L_0x002a:
        r1 = move-exception;
        monitor-exit(r2);
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.Vector.indexOf(java.lang.Object, int):int");
    }

    public synchronized void insertElementAt(E object, int location) {
        if (location >= 0) {
            if (location <= this.elementCount) {
                if (this.elementCount == this.elementData.length) {
                    growByOne();
                }
                int count = this.elementCount - location;
                if (count > 0) {
                    System.arraycopy(this.elementData, location, this.elementData, location + 1, count);
                }
                this.elementData[location] = object;
                this.elementCount++;
                this.modCount++;
            }
        }
        throw arrayIndexOutOfBoundsException(location, this.elementCount);
    }

    public synchronized boolean isEmpty() {
        return this.elementCount == 0;
    }

    public synchronized E lastElement() {
        try {
        } catch (IndexOutOfBoundsException e) {
            throw new NoSuchElementException();
        }
        return this.elementData[this.elementCount - 1];
    }

    public synchronized int lastIndexOf(Object object) {
        return lastIndexOf(object, this.elementCount - 1);
    }

    public synchronized int lastIndexOf(Object object, int location) {
        int i;
        if (location >= this.elementCount) {
            throw arrayIndexOutOfBoundsException(location, this.elementCount);
        } else if (object != null) {
            for (i = location; i >= 0; i--) {
                if (object.equals(this.elementData[i])) {
                    i = i;
                    break;
                }
            }
            i = -1;
        } else {
            for (i = location; i >= 0; i--) {
                if (this.elementData[i] == null) {
                    i = i;
                    break;
                }
            }
            i = -1;
        }
        return i;
    }

    public synchronized E remove(int location) {
        E result;
        if (location < this.elementCount) {
            result = this.elementData[location];
            this.elementCount--;
            int size = this.elementCount - location;
            if (size > 0) {
                System.arraycopy(this.elementData, location + 1, this.elementData, location, size);
            }
            this.elementData[this.elementCount] = null;
            this.modCount++;
        } else {
            throw arrayIndexOutOfBoundsException(location, this.elementCount);
        }
        return result;
    }

    public boolean remove(Object object) {
        return removeElement(object);
    }

    public synchronized boolean removeAll(Collection<?> collection) {
        return super.removeAll(collection);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void removeAllElements() {
        /*
        r3 = this;
        monitor-enter(r3);
        r0 = 0;
    L_0x0002:
        r1 = r3.elementCount;	 Catch:{ all -> 0x0019 }
        if (r0 >= r1) goto L_0x000e;
    L_0x0006:
        r1 = r3.elementData;	 Catch:{ all -> 0x0019 }
        r2 = 0;
        r1[r0] = r2;	 Catch:{ all -> 0x0019 }
        r0 = r0 + 1;
        goto L_0x0002;
    L_0x000e:
        r1 = r3.modCount;	 Catch:{ all -> 0x0019 }
        r1 = r1 + 1;
        r3.modCount = r1;	 Catch:{ all -> 0x0019 }
        r1 = 0;
        r3.elementCount = r1;	 Catch:{ all -> 0x0019 }
        monitor-exit(r3);
        return;
    L_0x0019:
        r1 = move-exception;
        monitor-exit(r3);
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.Vector.removeAllElements():void");
    }

    public synchronized boolean removeElement(Object object) {
        boolean z = false;
        synchronized (this) {
            int index = indexOf(object, 0);
            if (index != -1) {
                removeElementAt(index);
                z = true;
            }
        }
        return z;
    }

    public synchronized void removeElementAt(int location) {
        if (location >= 0) {
            if (location < this.elementCount) {
                this.elementCount--;
                int size = this.elementCount - location;
                if (size > 0) {
                    System.arraycopy(this.elementData, location + 1, this.elementData, location, size);
                }
                this.elementData[this.elementCount] = null;
                this.modCount++;
            }
        }
        throw arrayIndexOutOfBoundsException(location, this.elementCount);
    }

    protected void removeRange(int start, int end) {
        if (start < 0 || start > end || end > this.elementCount) {
            throw new IndexOutOfBoundsException();
        } else if (start != end) {
            if (end != this.elementCount) {
                System.arraycopy(this.elementData, end, this.elementData, start, this.elementCount - end);
                int newCount = this.elementCount - (end - start);
                Arrays.fill(this.elementData, newCount, this.elementCount, null);
                this.elementCount = newCount;
            } else {
                Arrays.fill(this.elementData, start, this.elementCount, null);
                this.elementCount = start;
            }
            this.modCount++;
        }
    }

    public synchronized boolean retainAll(Collection<?> collection) {
        return super.retainAll(collection);
    }

    public synchronized E set(int location, E object) {
        E result;
        if (location < this.elementCount) {
            result = this.elementData[location];
            this.elementData[location] = object;
        } else {
            throw arrayIndexOutOfBoundsException(location, this.elementCount);
        }
        return result;
    }

    public synchronized void setElementAt(E object, int location) {
        if (location < this.elementCount) {
            this.elementData[location] = object;
        } else {
            throw arrayIndexOutOfBoundsException(location, this.elementCount);
        }
    }

    private static ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException(int index, int size) {
        throw new ArrayIndexOutOfBoundsException(size, index);
    }

    public synchronized void setSize(int length) {
        if (length != this.elementCount) {
            ensureCapacity(length);
            if (this.elementCount > length) {
                Arrays.fill(this.elementData, length, this.elementCount, null);
            }
            this.elementCount = length;
            this.modCount++;
        }
    }

    public synchronized int size() {
        return this.elementCount;
    }

    public synchronized List<E> subList(int start, int end) {
        return new SynchronizedRandomAccessList(super.subList(start, end), this);
    }

    public synchronized Object[] toArray() {
        Object result;
        result = new Object[this.elementCount];
        System.arraycopy(this.elementData, 0, result, 0, this.elementCount);
        return result;
    }

    public synchronized <T> T[] toArray(T[] contents) {
        Object contents2;
        if (this.elementCount > contents.length) {
            contents2 = (Object[]) Array.newInstance(contents.getClass().getComponentType(), this.elementCount);
        }
        System.arraycopy(this.elementData, 0, contents2, 0, this.elementCount);
        if (this.elementCount < contents2.length) {
            contents2[this.elementCount] = null;
        }
        return contents2;
    }

    public synchronized String toString() {
        String str;
        if (this.elementCount == 0) {
            str = "[]";
        } else {
            int length = this.elementCount - 1;
            StringBuilder buffer = new StringBuilder(this.elementCount * 16);
            buffer.append('[');
            for (int i = 0; i < length; i++) {
                if (this.elementData[i] == this) {
                    buffer.append("(this Collection)");
                } else {
                    buffer.append(this.elementData[i]);
                }
                buffer.append(", ");
            }
            if (this.elementData[length] == this) {
                buffer.append("(this Collection)");
            } else {
                buffer.append(this.elementData[length]);
            }
            buffer.append(']');
            str = buffer.toString();
        }
        return str;
    }

    public synchronized void trimToSize() {
        if (this.elementData.length != this.elementCount) {
            grow(this.elementCount);
        }
    }

    private synchronized void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }
}
