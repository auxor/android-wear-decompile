package java.util.concurrent;

import java.util.concurrent.locks.LockSupport;
import sun.misc.Unsafe;

public class FutureTask<V> implements RunnableFuture<V> {
    private static final int CANCELLED = 4;
    private static final int COMPLETING = 1;
    private static final int EXCEPTIONAL = 3;
    private static final int INTERRUPTED = 6;
    private static final int INTERRUPTING = 5;
    private static final int NEW = 0;
    private static final int NORMAL = 2;
    private static final Unsafe UNSAFE;
    private static final long runnerOffset;
    private static final long stateOffset;
    private static final long waitersOffset;
    private Callable<V> callable;
    private Object outcome;
    private volatile Thread runner;
    private volatile int state;
    private volatile WaitNode waiters;

    static final class WaitNode {
        volatile WaitNode next;
        volatile Thread thread;

        WaitNode() {
            this.thread = Thread.currentThread();
        }
    }

    private V report(int s) throws ExecutionException {
        Object x = this.outcome;
        if (s == NORMAL) {
            return x;
        }
        if (s >= CANCELLED) {
            throw new CancellationException();
        }
        throw new ExecutionException((Throwable) x);
    }

    public FutureTask(Callable<V> callable) {
        if (callable == null) {
            throw new NullPointerException();
        }
        this.callable = callable;
        this.state = NEW;
    }

    public FutureTask(Runnable runnable, V result) {
        this.callable = Executors.callable(runnable, result);
        this.state = NEW;
    }

    public boolean isCancelled() {
        return this.state >= CANCELLED;
    }

    public boolean isDone() {
        return this.state != 0;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        if (this.state != 0) {
            return false;
        }
        int i;
        Unsafe unsafe = UNSAFE;
        long j = stateOffset;
        if (mayInterruptIfRunning) {
            i = INTERRUPTING;
        } else {
            i = CANCELLED;
        }
        if (!unsafe.compareAndSwapInt(this, j, NEW, i)) {
            return false;
        }
        if (mayInterruptIfRunning) {
            try {
                Thread t = this.runner;
                if (t != null) {
                    t.interrupt();
                }
                UNSAFE.putOrderedInt(this, stateOffset, INTERRUPTED);
            } catch (Throwable th) {
                finishCompletion();
            }
        }
        finishCompletion();
        return true;
    }

    public V get() throws InterruptedException, ExecutionException {
        int s = this.state;
        if (s <= COMPLETING) {
            s = awaitDone(false, 0);
        }
        return report(s);
    }

    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (unit == null) {
            throw new NullPointerException();
        }
        int s = this.state;
        if (s <= COMPLETING) {
            s = awaitDone(true, unit.toNanos(timeout));
            if (s <= COMPLETING) {
                throw new TimeoutException();
            }
        }
        return report(s);
    }

    protected void done() {
    }

    protected void set(V v) {
        if (UNSAFE.compareAndSwapInt(this, stateOffset, NEW, COMPLETING)) {
            this.outcome = v;
            UNSAFE.putOrderedInt(this, stateOffset, NORMAL);
            finishCompletion();
        }
    }

    protected void setException(Throwable t) {
        if (UNSAFE.compareAndSwapInt(this, stateOffset, NEW, COMPLETING)) {
            this.outcome = t;
            UNSAFE.putOrderedInt(this, stateOffset, EXCEPTIONAL);
            finishCompletion();
        }
    }

    public void run() {
        if (this.state == 0 && UNSAFE.compareAndSwapObject(this, runnerOffset, null, Thread.currentThread())) {
            int s;
            Object result;
            boolean ran;
            try {
                Callable<V> c = this.callable;
                if (c != null && this.state == 0) {
                    result = c.call();
                    ran = true;
                    if (ran) {
                        set(result);
                    }
                }
            } catch (Throwable th) {
                this.runner = null;
                s = this.state;
                if (s >= INTERRUPTING) {
                    handlePossibleCancellationInterrupt(s);
                }
            }
            this.runner = null;
            s = this.state;
            if (s >= INTERRUPTING) {
                handlePossibleCancellationInterrupt(s);
            }
        }
    }

    protected boolean runAndReset() {
        if (this.state != 0 || !UNSAFE.compareAndSwapObject(this, runnerOffset, null, Thread.currentThread())) {
            return false;
        }
        boolean ran = false;
        int s = this.state;
        try {
            Callable<V> c = this.callable;
            if (c != null && s == 0) {
                c.call();
                ran = true;
            }
        } catch (Throwable th) {
            this.runner = null;
            s = this.state;
            if (s >= INTERRUPTING) {
                handlePossibleCancellationInterrupt(s);
            }
        }
        this.runner = null;
        s = this.state;
        if (s >= INTERRUPTING) {
            handlePossibleCancellationInterrupt(s);
        }
        if (ran && s == 0) {
            return true;
        }
        return false;
    }

    private void handlePossibleCancellationInterrupt(int s) {
        if (s == INTERRUPTING) {
            while (this.state == INTERRUPTING) {
                Thread.yield();
            }
        }
    }

    private void finishCompletion() {
        WaitNode q;
        do {
            q = this.waiters;
            if (q == null) {
                break;
            }
        } while (!UNSAFE.compareAndSwapObject(this, waitersOffset, q, null));
        while (true) {
            Thread t = q.thread;
            if (t != null) {
                q.thread = null;
                LockSupport.unpark(t);
            }
            WaitNode next = q.next;
            if (next == null) {
                break;
            }
            q.next = null;
            q = next;
        }
        done();
        this.callable = null;
    }

    private int awaitDone(boolean timed, long nanos) throws InterruptedException {
        long deadline = timed ? System.nanoTime() + nanos : 0;
        WaitNode q = null;
        boolean queued = false;
        while (!Thread.interrupted()) {
            int s = this.state;
            if (s > COMPLETING) {
                if (q == null) {
                    return s;
                }
                q.thread = null;
                return s;
            } else if (s == COMPLETING) {
                Thread.yield();
            } else if (q == null) {
                q = new WaitNode();
            } else if (!queued) {
                Unsafe unsafe = UNSAFE;
                long j = waitersOffset;
                WaitNode waitNode = this.waiters;
                q.next = waitNode;
                queued = unsafe.compareAndSwapObject(this, j, waitNode, q);
            } else if (timed) {
                nanos = deadline - System.nanoTime();
                if (nanos <= 0) {
                    removeWaiter(q);
                    return this.state;
                }
                LockSupport.parkNanos(this, nanos);
            } else {
                LockSupport.park(this);
            }
        }
        removeWaiter(q);
        throw new InterruptedException();
    }

    private void removeWaiter(WaitNode node) {
        if (node != null) {
            node.thread = null;
            while (true) {
                WaitNode pred = null;
                WaitNode q = this.waiters;
                while (q != null) {
                    WaitNode s = q.next;
                    if (q.thread != null) {
                        pred = q;
                    } else if (pred != null) {
                        pred.next = s;
                        if (pred.thread == null) {
                        }
                    } else if (UNSAFE.compareAndSwapObject(this, waitersOffset, q, s)) {
                    }
                    q = s;
                }
                return;
            }
        }
    }

    static {
        try {
            UNSAFE = Unsafe.getUnsafe();
            Class<?> k = FutureTask.class;
            stateOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("state"));
            runnerOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("runner"));
            waitersOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("waiters"));
        } catch (Throwable e) {
            throw new Error(e);
        }
    }
}
