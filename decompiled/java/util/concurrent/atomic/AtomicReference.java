package java.util.concurrent.atomic;

import java.io.Serializable;
import sun.misc.Unsafe;

public class AtomicReference<V> implements Serializable {
    private static final long serialVersionUID = -1848883965231344442L;
    private static final Unsafe unsafe;
    private static final long valueOffset;
    private volatile V value;

    static {
        unsafe = Unsafe.getUnsafe();
        try {
            valueOffset = unsafe.objectFieldOffset(AtomicReference.class.getDeclaredField("value"));
        } catch (Throwable ex) {
            throw new Error(ex);
        }
    }

    public AtomicReference(V initialValue) {
        this.value = initialValue;
    }

    public final V get() {
        return this.value;
    }

    public final void set(V newValue) {
        this.value = newValue;
    }

    public final void lazySet(V newValue) {
        unsafe.putOrderedObject(this, valueOffset, newValue);
    }

    public final boolean compareAndSet(V expect, V update) {
        return unsafe.compareAndSwapObject(this, valueOffset, expect, update);
    }

    public final boolean weakCompareAndSet(V expect, V update) {
        return unsafe.compareAndSwapObject(this, valueOffset, expect, update);
    }

    public final V getAndSet(V newValue) {
        V x;
        do {
            x = get();
        } while (!compareAndSet(x, newValue));
        return x;
    }

    public String toString() {
        return String.valueOf(get());
    }
}
