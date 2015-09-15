package java.util.concurrent.atomic;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import sun.misc.Unsafe;

public class AtomicReferenceArray<E> implements Serializable {
    private static final long arrayFieldOffset;
    private static final int base;
    private static final long serialVersionUID = -6209656149925076980L;
    private static final int shift;
    private static final Unsafe unsafe;
    private final Object[] array;

    static {
        try {
            unsafe = Unsafe.getUnsafe();
            arrayFieldOffset = unsafe.objectFieldOffset(AtomicReferenceArray.class.getDeclaredField("array"));
            base = unsafe.arrayBaseOffset(Object[].class);
            int scale = unsafe.arrayIndexScale(Object[].class);
            if (((scale - 1) & scale) != 0) {
                throw new Error("data type scale not a power of two");
            }
            shift = 31 - Integer.numberOfLeadingZeros(scale);
        } catch (Throwable e) {
            throw new Error(e);
        }
    }

    private long checkedByteOffset(int i) {
        if (i >= 0 && i < this.array.length) {
            return byteOffset(i);
        }
        throw new IndexOutOfBoundsException("index " + i);
    }

    private static long byteOffset(int i) {
        return (((long) i) << shift) + ((long) base);
    }

    public AtomicReferenceArray(int length) {
        this.array = new Object[length];
    }

    public AtomicReferenceArray(E[] array) {
        this.array = Arrays.copyOf(array, array.length, Object[].class);
    }

    public final int length() {
        return this.array.length;
    }

    public final E get(int i) {
        return getRaw(checkedByteOffset(i));
    }

    private E getRaw(long offset) {
        return unsafe.getObjectVolatile(this.array, offset);
    }

    public final void set(int i, E newValue) {
        unsafe.putObjectVolatile(this.array, checkedByteOffset(i), newValue);
    }

    public final void lazySet(int i, E newValue) {
        unsafe.putOrderedObject(this.array, checkedByteOffset(i), newValue);
    }

    public final E getAndSet(int i, E newValue) {
        E current;
        long offset = checkedByteOffset(i);
        do {
            current = getRaw(offset);
        } while (!compareAndSetRaw(offset, current, newValue));
        return current;
    }

    public final boolean compareAndSet(int i, E expect, E update) {
        return compareAndSetRaw(checkedByteOffset(i), expect, update);
    }

    private boolean compareAndSetRaw(long offset, E expect, E update) {
        return unsafe.compareAndSwapObject(this.array, offset, expect, update);
    }

    public final boolean weakCompareAndSet(int i, E expect, E update) {
        return compareAndSet(i, expect, update);
    }

    public String toString() {
        int iMax = this.array.length - 1;
        if (iMax == -1) {
            return "[]";
        }
        StringBuilder b = new StringBuilder();
        b.append('[');
        int i = base;
        while (true) {
            b.append(getRaw(byteOffset(i)));
            if (i == iMax) {
                return b.append(']').toString();
            }
            b.append(',').append(' ');
            i++;
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException, InvalidObjectException {
        Object a = s.readFields().get("array", null);
        if (a == null || !a.getClass().isArray()) {
            throw new InvalidObjectException("Not array type");
        }
        if (a.getClass() != Object[].class) {
            a = Arrays.copyOf((Object[]) a, Array.getLength(a), Object[].class);
        }
        unsafe.putObjectVolatile(this, arrayFieldOffset, a);
    }
}
