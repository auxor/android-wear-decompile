package java.util.concurrent.locks;

import sun.misc.Unsafe;

public class LockSupport {
    private static final long parkBlockerOffset;
    private static final Unsafe unsafe;

    private LockSupport() {
    }

    static {
        unsafe = Unsafe.getUnsafe();
        try {
            parkBlockerOffset = unsafe.objectFieldOffset(Thread.class.getDeclaredField("parkBlocker"));
        } catch (Throwable ex) {
            throw new Error(ex);
        }
    }

    private static void setBlocker(Thread t, Object arg) {
        unsafe.putObject(t, parkBlockerOffset, arg);
    }

    public static void unpark(Thread thread) {
        if (thread != null) {
            unsafe.unpark(thread);
        }
    }

    public static void park(Object blocker) {
        Thread t = Thread.currentThread();
        setBlocker(t, blocker);
        unsafe.park(false, 0);
        setBlocker(t, null);
    }

    public static void parkNanos(Object blocker, long nanos) {
        if (nanos > 0) {
            Thread t = Thread.currentThread();
            setBlocker(t, blocker);
            unsafe.park(false, nanos);
            setBlocker(t, null);
        }
    }

    public static void parkUntil(Object blocker, long deadline) {
        Thread t = Thread.currentThread();
        setBlocker(t, blocker);
        unsafe.park(true, deadline);
        setBlocker(t, null);
    }

    public static Object getBlocker(Thread t) {
        if (t != null) {
            return unsafe.getObjectVolatile(t, parkBlockerOffset);
        }
        throw new NullPointerException();
    }

    public static void park() {
        unsafe.park(false, 0);
    }

    public static void parkNanos(long nanos) {
        if (nanos > 0) {
            unsafe.park(false, nanos);
        }
    }

    public static void parkUntil(long deadline) {
        unsafe.park(true, deadline);
    }
}
