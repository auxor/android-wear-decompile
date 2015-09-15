package java.util.concurrent.atomic;

import java.io.Serializable;
import sun.misc.Unsafe;

public class AtomicInteger extends Number implements Serializable {
    private static final long serialVersionUID = 6214790243416807050L;
    private static final Unsafe unsafe;
    private static final long valueOffset;
    private volatile int value;

    static {
        unsafe = Unsafe.getUnsafe();
        try {
            valueOffset = unsafe.objectFieldOffset(AtomicInteger.class.getDeclaredField("value"));
        } catch (Throwable ex) {
            throw new Error(ex);
        }
    }

    public AtomicInteger(int initialValue) {
        this.value = initialValue;
    }

    public final int get() {
        return this.value;
    }

    public final void set(int newValue) {
        this.value = newValue;
    }

    public final void lazySet(int newValue) {
        unsafe.putOrderedInt(this, valueOffset, newValue);
    }

    public final int getAndSet(int newValue) {
        int current;
        do {
            current = get();
        } while (!compareAndSet(current, newValue));
        return current;
    }

    public final boolean compareAndSet(int expect, int update) {
        return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
    }

    public final boolean weakCompareAndSet(int expect, int update) {
        return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
    }

    public final int getAndIncrement() {
        int current;
        do {
            current = get();
        } while (!compareAndSet(current, current + 1));
        return current;
    }

    public final int getAndDecrement() {
        int current;
        do {
            current = get();
        } while (!compareAndSet(current, current - 1));
        return current;
    }

    public final int getAndAdd(int delta) {
        int current;
        do {
            current = get();
        } while (!compareAndSet(current, current + delta));
        return current;
    }

    public final int incrementAndGet() {
        int next;
        int current;
        do {
            current = get();
            next = current + 1;
        } while (!compareAndSet(current, next));
        return next;
    }

    public final int decrementAndGet() {
        int next;
        int current;
        do {
            current = get();
            next = current - 1;
        } while (!compareAndSet(current, next));
        return next;
    }

    public final int addAndGet(int delta) {
        int next;
        int current;
        do {
            current = get();
            next = current + delta;
        } while (!compareAndSet(current, next));
        return next;
    }

    public String toString() {
        return Integer.toString(get());
    }

    public int intValue() {
        return get();
    }

    public long longValue() {
        return (long) get();
    }

    public float floatValue() {
        return (float) get();
    }

    public double doubleValue() {
        return (double) get();
    }
}
