package java.lang;

import dalvik.system.VMStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import libcore.util.EmptyArray;
import org.w3c.dom.traversal.NodeFilter;

public class Thread implements Runnable {
    public static final int MAX_PRIORITY = 10;
    public static final int MIN_PRIORITY = 1;
    private static final int NANOS_PER_MILLI = 1000000;
    public static final int NORM_PRIORITY = 5;
    private static int count;
    private static UncaughtExceptionHandler defaultUncaughtHandler;
    private ClassLoader contextClassLoader;
    volatile boolean daemon;
    volatile ThreadGroup group;
    boolean hasBeenStarted;
    private long id;
    Values inheritableValues;
    private final List<Runnable> interruptActions;
    Values localValues;
    private final Object lock;
    volatile String name;
    private volatile long nativePeer;
    private Object parkBlocker;
    private int parkState;
    volatile int priority;
    volatile long stackSize;
    Runnable target;
    private UncaughtExceptionHandler uncaughtHandler;

    private static class ParkState {
        private static final int PARKED = 3;
        private static final int PREEMPTIVELY_UNPARKED = 2;
        private static final int UNPARKED = 1;

        private ParkState() {
        }
    }

    public enum State {
        NEW,
        RUNNABLE,
        BLOCKED,
        WAITING,
        TIMED_WAITING,
        TERMINATED
    }

    public interface UncaughtExceptionHandler {
        void uncaughtException(Thread thread, Throwable th);
    }

    public static native Thread currentThread();

    public static native boolean interrupted();

    private static native void nativeCreate(Thread thread, long j, boolean z);

    private native int nativeGetStatus(boolean z);

    private native boolean nativeHoldsLock(Object obj);

    private native void nativeInterrupt();

    private native void nativeSetName(String str);

    private native void nativeSetPriority(int i);

    private static native void sleep(Object obj, long j, int i);

    public static native void yield();

    public native boolean isInterrupted();

    static {
        count = 0;
    }

    public Thread() {
        this.interruptActions = new ArrayList();
        this.hasBeenStarted = false;
        this.parkState = MIN_PRIORITY;
        this.lock = new Object();
        create(null, null, null, 0);
    }

    public Thread(Runnable runnable) {
        this.interruptActions = new ArrayList();
        this.hasBeenStarted = false;
        this.parkState = MIN_PRIORITY;
        this.lock = new Object();
        create(null, runnable, null, 0);
    }

    public Thread(Runnable runnable, String threadName) {
        this.interruptActions = new ArrayList();
        this.hasBeenStarted = false;
        this.parkState = MIN_PRIORITY;
        this.lock = new Object();
        if (threadName == null) {
            throw new NullPointerException("threadName == null");
        }
        create(null, runnable, threadName, 0);
    }

    public Thread(String threadName) {
        this.interruptActions = new ArrayList();
        this.hasBeenStarted = false;
        this.parkState = MIN_PRIORITY;
        this.lock = new Object();
        if (threadName == null) {
            throw new NullPointerException("threadName == null");
        }
        create(null, null, threadName, 0);
    }

    public Thread(ThreadGroup group, Runnable runnable) {
        this.interruptActions = new ArrayList();
        this.hasBeenStarted = false;
        this.parkState = MIN_PRIORITY;
        this.lock = new Object();
        create(group, runnable, null, 0);
    }

    public Thread(ThreadGroup group, Runnable runnable, String threadName) {
        this.interruptActions = new ArrayList();
        this.hasBeenStarted = false;
        this.parkState = MIN_PRIORITY;
        this.lock = new Object();
        if (threadName == null) {
            throw new NullPointerException("threadName == null");
        }
        create(group, runnable, threadName, 0);
    }

    public Thread(ThreadGroup group, String threadName) {
        this.interruptActions = new ArrayList();
        this.hasBeenStarted = false;
        this.parkState = MIN_PRIORITY;
        this.lock = new Object();
        if (threadName == null) {
            throw new NullPointerException("threadName == null");
        }
        create(group, null, threadName, 0);
    }

    public Thread(ThreadGroup group, Runnable runnable, String threadName, long stackSize) {
        this.interruptActions = new ArrayList();
        this.hasBeenStarted = false;
        this.parkState = MIN_PRIORITY;
        this.lock = new Object();
        if (threadName == null) {
            throw new NullPointerException("threadName == null");
        }
        create(group, runnable, threadName, stackSize);
    }

    Thread(ThreadGroup group, String name, int priority, boolean daemon) {
        this.interruptActions = new ArrayList();
        this.hasBeenStarted = false;
        this.parkState = MIN_PRIORITY;
        this.lock = new Object();
        synchronized (Thread.class) {
            int i = count + MIN_PRIORITY;
            count = i;
            this.id = (long) i;
        }
        if (name == null) {
            this.name = "Thread-" + this.id;
        } else {
            this.name = name;
        }
        if (group == null) {
            throw new InternalError("group == null");
        }
        this.group = group;
        this.target = null;
        this.stackSize = 0;
        this.priority = priority;
        this.daemon = daemon;
        this.group.addThread(this);
    }

    private void create(ThreadGroup group, Runnable runnable, String threadName, long stackSize) {
        Thread currentThread = currentThread();
        if (group == null) {
            group = currentThread.getThreadGroup();
        }
        if (group.isDestroyed()) {
            throw new IllegalThreadStateException("Group already destroyed");
        }
        this.group = group;
        synchronized (Thread.class) {
            int i = count + MIN_PRIORITY;
            count = i;
            this.id = (long) i;
        }
        if (threadName == null) {
            this.name = "Thread-" + this.id;
        } else {
            this.name = threadName;
        }
        this.target = runnable;
        this.stackSize = stackSize;
        this.priority = currentThread.getPriority();
        this.contextClassLoader = currentThread.contextClassLoader;
        if (currentThread.inheritableValues != null) {
            this.inheritableValues = new Values(currentThread.inheritableValues);
        }
        this.group.addThread(this);
    }

    public static int activeCount() {
        return currentThread().getThreadGroup().activeCount();
    }

    public final void checkAccess() {
    }

    @Deprecated
    public int countStackFrames() {
        return getStackTrace().length;
    }

    @Deprecated
    public void destroy() {
        throw new UnsupportedOperationException();
    }

    public static void dumpStack() {
        new Throwable("stack dump").printStackTrace();
    }

    public static int enumerate(Thread[] threads) {
        return currentThread().getThreadGroup().enumerate(threads);
    }

    public static Map<Thread, StackTraceElement[]> getAllStackTraces() {
        Map<Thread, StackTraceElement[]> map = new HashMap();
        int count = ThreadGroup.systemThreadGroup.activeCount();
        Thread[] threads = new Thread[((count / 2) + count)];
        count = ThreadGroup.systemThreadGroup.enumerate(threads);
        for (int i = 0; i < count; i += MIN_PRIORITY) {
            map.put(threads[i], threads[i].getStackTrace());
        }
        return map;
    }

    public ClassLoader getContextClassLoader() {
        return this.contextClassLoader;
    }

    public static UncaughtExceptionHandler getDefaultUncaughtExceptionHandler() {
        return defaultUncaughtHandler;
    }

    public long getId() {
        return this.id;
    }

    public final String getName() {
        return this.name;
    }

    public final int getPriority() {
        return this.priority;
    }

    public StackTraceElement[] getStackTrace() {
        StackTraceElement[] ste = VMStack.getThreadStackTrace(this);
        return ste != null ? ste : EmptyArray.STACK_TRACE_ELEMENT;
    }

    public State getState() {
        return State.values()[nativeGetStatus(this.hasBeenStarted)];
    }

    public final ThreadGroup getThreadGroup() {
        if (getState() == State.TERMINATED) {
            return null;
        }
        return this.group;
    }

    public UncaughtExceptionHandler getUncaughtExceptionHandler() {
        if (this.uncaughtHandler != null) {
            return this.uncaughtHandler;
        }
        return this.group;
    }

    public void interrupt() {
        nativeInterrupt();
        synchronized (this.interruptActions) {
            for (int i = this.interruptActions.size() - 1; i >= 0; i--) {
                ((Runnable) this.interruptActions.get(i)).run();
            }
        }
    }

    public final boolean isAlive() {
        return this.nativePeer != 0;
    }

    public final boolean isDaemon() {
        return this.daemon;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void join() throws java.lang.InterruptedException {
        /*
        r2 = this;
        r1 = r2.lock;
        monitor-enter(r1);
    L_0x0003:
        r0 = r2.isAlive();	 Catch:{ all -> 0x000f }
        if (r0 == 0) goto L_0x0012;
    L_0x0009:
        r0 = r2.lock;	 Catch:{ all -> 0x000f }
        r0.wait();	 Catch:{ all -> 0x000f }
        goto L_0x0003;
    L_0x000f:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x000f }
        throw r0;
    L_0x0012:
        monitor-exit(r1);	 Catch:{ all -> 0x000f }
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.lang.Thread.join():void");
    }

    public final void join(long millis) throws InterruptedException {
        join(millis, 0);
    }

    public final void join(long millis, int nanos) throws InterruptedException {
        if (millis < 0 || nanos < 0 || nanos >= NANOS_PER_MILLI) {
            throw new IllegalArgumentException("bad timeout: millis=" + millis + ",nanos=" + nanos);
        }
        if ((((((long) nanos) | millis) == 0) | (millis >= (Long.MAX_VALUE - ((long) nanos)) / 1000000)) != 0) {
            join();
            return;
        }
        synchronized (this.lock) {
            if (isAlive()) {
                long nanosToWait = (1000000 * millis) + ((long) nanos);
                long start = System.nanoTime();
                while (true) {
                    this.lock.wait(millis, nanos);
                    if (!isAlive()) {
                        break;
                    }
                    long nanosRemaining = nanosToWait - (System.nanoTime() - start);
                    if (nanosRemaining <= 0) {
                        break;
                    }
                    millis = nanosRemaining / 1000000;
                    nanos = (int) (nanosRemaining - (1000000 * millis));
                }
                return;
            }
        }
    }

    @Deprecated
    public final void resume() {
        throw new UnsupportedOperationException();
    }

    public void run() {
        if (this.target != null) {
            this.target.run();
        }
    }

    public void setContextClassLoader(ClassLoader cl) {
        this.contextClassLoader = cl;
    }

    public final void setDaemon(boolean isDaemon) {
        checkNotStarted();
        if (this.nativePeer == 0) {
            this.daemon = isDaemon;
        }
    }

    private void checkNotStarted() {
        if (this.hasBeenStarted) {
            throw new IllegalThreadStateException("Thread already started");
        }
    }

    public static void setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler handler) {
        defaultUncaughtHandler = handler;
    }

    public final void pushInterruptAction$(Runnable interruptAction) {
        synchronized (this.interruptActions) {
            this.interruptActions.add(interruptAction);
        }
        if (interruptAction != null && isInterrupted()) {
            interruptAction.run();
        }
    }

    public final void popInterruptAction$(Runnable interruptAction) {
        synchronized (this.interruptActions) {
            Object removed = (Runnable) this.interruptActions.remove(this.interruptActions.size() - 1);
            if (interruptAction != removed) {
                throw new IllegalArgumentException("Expected " + interruptAction + " but was " + removed);
            }
        }
    }

    public final void setName(String threadName) {
        if (threadName == null) {
            throw new NullPointerException("threadName == null");
        }
        synchronized (this) {
            this.name = threadName;
            if (isAlive()) {
                nativeSetName(threadName);
            }
        }
    }

    public final void setPriority(int priority) {
        if (priority < MIN_PRIORITY || priority > MAX_PRIORITY) {
            throw new IllegalArgumentException("Priority out of range: " + priority);
        }
        if (priority > this.group.getMaxPriority()) {
            priority = this.group.getMaxPriority();
        }
        synchronized (this) {
            this.priority = priority;
            if (isAlive()) {
                nativeSetPriority(priority);
            }
        }
    }

    public void setUncaughtExceptionHandler(UncaughtExceptionHandler handler) {
        this.uncaughtHandler = handler;
    }

    public static void sleep(long time) throws InterruptedException {
        sleep(time, 0);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void sleep(long r18, int r20) throws java.lang.InterruptedException {
        /*
        r14 = 0;
        r9 = (r18 > r14 ? 1 : (r18 == r14 ? 0 : -1));
        if (r9 >= 0) goto L_0x0021;
    L_0x0006:
        r9 = new java.lang.IllegalArgumentException;
        r14 = new java.lang.StringBuilder;
        r14.<init>();
        r15 = "millis < 0: ";
        r14 = r14.append(r15);
        r0 = r18;
        r14 = r14.append(r0);
        r14 = r14.toString();
        r9.<init>(r14);
        throw r9;
    L_0x0021:
        if (r20 >= 0) goto L_0x003e;
    L_0x0023:
        r9 = new java.lang.IllegalArgumentException;
        r14 = new java.lang.StringBuilder;
        r14.<init>();
        r15 = "nanos < 0: ";
        r14 = r14.append(r15);
        r0 = r20;
        r14 = r14.append(r0);
        r14 = r14.toString();
        r9.<init>(r14);
        throw r9;
    L_0x003e:
        r9 = 999999; // 0xf423f float:1.401297E-39 double:4.94065E-318;
        r0 = r20;
        if (r0 <= r9) goto L_0x0060;
    L_0x0045:
        r9 = new java.lang.IllegalArgumentException;
        r14 = new java.lang.StringBuilder;
        r14.<init>();
        r15 = "nanos > 999999: ";
        r14 = r14.append(r15);
        r0 = r20;
        r14 = r14.append(r0);
        r14 = r14.toString();
        r9.<init>(r14);
        throw r9;
    L_0x0060:
        r14 = 0;
        r9 = (r18 > r14 ? 1 : (r18 == r14 ? 0 : -1));
        if (r9 != 0) goto L_0x0074;
    L_0x0066:
        if (r20 != 0) goto L_0x0074;
    L_0x0068:
        r9 = interrupted();
        if (r9 == 0) goto L_0x009d;
    L_0x006e:
        r9 = new java.lang.InterruptedException;
        r9.<init>();
        throw r9;
    L_0x0074:
        r12 = java.lang.System.nanoTime();
        r14 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r14 = r14 * r18;
        r0 = r20;
        r0 = (long) r0;
        r16 = r0;
        r4 = r14 + r16;
        r9 = currentThread();
        r8 = r9.lock;
        monitor-enter(r8);
    L_0x008b:
        r0 = r18;
        r2 = r20;
        sleep(r8, r0, r2);	 Catch:{ all -> 0x00ae }
        r10 = java.lang.System.nanoTime();	 Catch:{ all -> 0x00ae }
        r6 = r10 - r12;
        r9 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1));
        if (r9 < 0) goto L_0x009e;
    L_0x009c:
        monitor-exit(r8);	 Catch:{ all -> 0x00ae }
    L_0x009d:
        return;
    L_0x009e:
        r4 = r4 - r6;
        r12 = r10;
        r14 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r18 = r4 / r14;
        r14 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r14 = r4 % r14;
        r0 = (int) r14;	 Catch:{ all -> 0x00ae }
        r20 = r0;
        goto L_0x008b;
    L_0x00ae:
        r9 = move-exception;
        monitor-exit(r8);	 Catch:{ all -> 0x00ae }
        throw r9;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.lang.Thread.sleep(long, int):void");
    }

    public synchronized void start() {
        checkNotStarted();
        this.hasBeenStarted = true;
        nativeCreate(this, this.stackSize, this.daemon);
    }

    @Deprecated
    public final void stop() {
        stop(new ThreadDeath());
    }

    @Deprecated
    public final synchronized void stop(Throwable throwable) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public final void suspend() {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        return "Thread[" + this.name + "," + this.priority + "," + this.group.getName() + "]";
    }

    public static boolean holdsLock(Object object) {
        return currentThread().nativeHoldsLock(object);
    }

    public void unpark() {
        synchronized (this.lock) {
            switch (this.parkState) {
                case MIN_PRIORITY /*1*/:
                    this.parkState = 2;
                    break;
                case NodeFilter.SHOW_ATTRIBUTE /*2*/:
                    break;
                default:
                    this.parkState = MIN_PRIORITY;
                    this.lock.notifyAll();
                    break;
            }
        }
    }

    public void parkFor(long nanos) {
        synchronized (this.lock) {
            switch (this.parkState) {
                case MIN_PRIORITY /*1*/:
                    long millis = nanos / 1000000;
                    nanos %= 1000000;
                    this.parkState = 3;
                    try {
                        this.lock.wait(millis, (int) nanos);
                        if (this.parkState == 3) {
                            this.parkState = MIN_PRIORITY;
                        }
                        break;
                    } catch (InterruptedException e) {
                        interrupt();
                        if (this.parkState == 3) {
                            this.parkState = MIN_PRIORITY;
                        }
                    } catch (Throwable th) {
                        if (this.parkState == 3) {
                            this.parkState = MIN_PRIORITY;
                        }
                    }
                case NodeFilter.SHOW_ATTRIBUTE /*2*/:
                    this.parkState = MIN_PRIORITY;
                    break;
                default:
                    throw new AssertionError((Object) "Attempt to repark");
            }
        }
    }

    public void parkUntil(long time) {
        synchronized (this.lock) {
            long delayMillis = time - System.currentTimeMillis();
            if (delayMillis <= 0) {
                this.parkState = MIN_PRIORITY;
            } else {
                parkFor(1000000 * delayMillis);
            }
        }
    }
}
