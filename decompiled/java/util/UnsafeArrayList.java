package java.util;

import java.lang.reflect.Array;

public class UnsafeArrayList<T> extends AbstractList<T> {
    private T[] array;
    private final Class<T> elementType;
    private int size;

    public UnsafeArrayList(Class<T> elementType, int initialCapacity) {
        this.array = (Object[]) Array.newInstance((Class) elementType, initialCapacity);
        this.elementType = elementType;
    }

    public boolean add(T element) {
        if (this.size == this.array.length) {
            Object newArray = (Object[]) ((Object[]) Array.newInstance(this.elementType, this.size * 2));
            System.arraycopy(this.array, 0, newArray, 0, this.size);
            this.array = newArray;
        }
        Object[] objArr = this.array;
        int i = this.size;
        this.size = i + 1;
        objArr[i] = element;
        this.modCount++;
        return true;
    }

    public T[] array() {
        return this.array;
    }

    public T get(int i) {
        return this.array[i];
    }

    public int size() {
        return this.size;
    }
}
