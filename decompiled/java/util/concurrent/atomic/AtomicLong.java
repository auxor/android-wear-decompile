package java.util.concurrent.atomic;

import java.io.Serializable;
import sun.misc.Unsafe;

public class AtomicLong extends Number implements Serializable {
    static final boolean VM_SUPPORTS_LONG_CAS;
    private static final long serialVersionUID = 1927816293512124184L;
    private static final Unsafe unsafe;
    private static final long valueOffset;
    private volatile long value;

    private static native boolean VMSupportsCS8();

    static {
        unsafe = Unsafe.getUnsafe();
        VM_SUPPORTS_LONG_CAS = VMSupportsCS8();
        try {
            valueOffset = unsafe.objectFieldOffset(AtomicLong.class.getDeclaredField("value"));
        } catch (Throwable ex) {
            throw new Error(ex);
        }
    }

    public AtomicLong(long initialValue) {
        this.value = initialValue;
    }

    public final long get() {
        return this.value;
    }

    public final void set(long newValue) {
        this.value = newValue;
    }

    public final void lazySet(long newValue) {
        unsafe.putOrderedLong(this, valueOffset, newValue);
    }

    public final long getAndSet(long newValue) {
        long current;
        do {
            current = get();
        } while (!compareAndSet(current, newValue));
        return current;
    }

    public final boolean compareAndSet(long expect, long update) {
        return unsafe.compareAndSwapLong(this, valueOffset, expect, update);
    }

    public final boolean weakCompareAndSet(long expect, long update) {
        return unsafe.compareAndSwapLong(this, valueOffset, expect, update);
    }

    public final long getAndIncrement() {
        long current;
        do {
            current = get();
        } while (!compareAndSet(current, current + 1));
        return current;
    }

    public final long getAndDecrement() {
        long current;
        do {
            current = get();
        } while (!compareAndSet(current, current - 1));
        return current;
    }

    public final long getAndAdd(long delta) {
        long current;
        do {
            current = get();
        } while (!compareAndSet(current, current + delta));
        return current;
    }

    public final long incrementAndGet() {
        long next;
        long current;
        do {
            current = get();
            next = current + 1;
        } while (!compareAndSet(current, next));
        return next;
    }

    public final long decrementAndGet() {
        long next;
        long current;
        do {
            current = get();
            next = current - 1;
        } while (!compareAndSet(current, next));
        return next;
    }

    public final long addAndGet(long delta) {
        long next;
        long current;
        do {
            current = get();
            next = current + delta;
        } while (!compareAndSet(current, next));
        return next;
    }

    public String toString() {
        return Long.toString(get());
    }

    public int intValue() {
        return (int) get();
    }

    public long longValue() {
        return get();
    }

    public float floatValue() {
        return (float) get();
    }

    public double doubleValue() {
        return (double) get();
    }
}
