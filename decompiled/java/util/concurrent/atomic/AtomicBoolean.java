package java.util.concurrent.atomic;

import java.io.Serializable;
import sun.misc.Unsafe;

public class AtomicBoolean implements Serializable {
    private static final long serialVersionUID = 4654671469794556979L;
    private static final Unsafe unsafe;
    private static final long valueOffset;
    private volatile int value;

    static {
        unsafe = Unsafe.getUnsafe();
        try {
            valueOffset = unsafe.objectFieldOffset(AtomicBoolean.class.getDeclaredField("value"));
        } catch (Throwable ex) {
            throw new Error(ex);
        }
    }

    public AtomicBoolean(boolean initialValue) {
        this.value = initialValue ? 1 : 0;
    }

    public final boolean get() {
        return this.value != 0;
    }

    public final boolean compareAndSet(boolean expect, boolean update) {
        int e;
        int u = 1;
        if (expect) {
            e = 1;
        } else {
            e = 0;
        }
        if (!update) {
            u = 0;
        }
        return unsafe.compareAndSwapInt(this, valueOffset, e, u);
    }

    public boolean weakCompareAndSet(boolean expect, boolean update) {
        int e;
        int u = 1;
        if (expect) {
            e = 1;
        } else {
            e = 0;
        }
        if (!update) {
            u = 0;
        }
        return unsafe.compareAndSwapInt(this, valueOffset, e, u);
    }

    public final void set(boolean newValue) {
        this.value = newValue ? 1 : 0;
    }

    public final void lazySet(boolean newValue) {
        unsafe.putOrderedInt(this, valueOffset, newValue ? 1 : 0);
    }

    public final boolean getAndSet(boolean newValue) {
        boolean current;
        do {
            current = get();
        } while (!compareAndSet(current, newValue));
        return current;
    }

    public String toString() {
        return Boolean.toString(get());
    }
}
