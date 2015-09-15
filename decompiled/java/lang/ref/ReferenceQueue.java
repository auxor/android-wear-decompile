package java.lang.ref;

public class ReferenceQueue<T> {
    private static final int NANOS_PER_MILLI = 1000000;
    public static Reference<?> unenqueued;
    private Reference<? extends T> head;
    private Reference<? extends T> tail;

    public synchronized Reference<? extends T> poll() {
        Reference<? extends T> reference = null;
        synchronized (this) {
            if (this.head != null) {
                reference = this.head;
                if (this.head == this.tail) {
                    this.tail = null;
                    this.head = null;
                } else {
                    this.head = this.head.queueNext;
                }
                reference.queueNext = null;
            }
        }
        return reference;
    }

    public Reference<? extends T> remove() throws InterruptedException {
        return remove(0);
    }

    public synchronized Reference<? extends T> remove(long timeoutMillis) throws InterruptedException {
        Reference<? extends T> poll;
        if (timeoutMillis < 0) {
            throw new IllegalArgumentException("timeout < 0: " + timeoutMillis);
        } else if (this.head != null) {
            poll = poll();
        } else if (timeoutMillis == 0 || timeoutMillis > 9223372036854L) {
            do {
                wait(0);
            } while (this.head == null);
            poll = poll();
        } else {
            long nanosToWait = timeoutMillis * 1000000;
            int timeoutNanos = 0;
            long startTime = System.nanoTime();
            while (true) {
                wait(timeoutMillis, timeoutNanos);
                if (this.head == null) {
                    long nanosRemaining = nanosToWait - (System.nanoTime() - startTime);
                    if (nanosRemaining <= 0) {
                        break;
                    }
                    timeoutMillis = nanosRemaining / 1000000;
                    timeoutNanos = (int) (nanosRemaining - (1000000 * timeoutMillis));
                } else {
                    break;
                }
            }
            poll = poll();
        }
        return poll;
    }

    synchronized void enqueue(Reference<? extends T> reference) {
        if (this.tail == null) {
            this.head = reference;
        } else {
            this.tail.queueNext = reference;
        }
        this.tail = reference;
        this.tail.queueNext = reference;
        notify();
    }

    static {
        unenqueued = null;
    }

    static void add(Reference<?> list) {
        synchronized (ReferenceQueue.class) {
            if (unenqueued == null) {
                unenqueued = list;
            } else {
                Reference<?> last = unenqueued;
                while (last.pendingNext != unenqueued) {
                    last = last.pendingNext;
                }
                last.pendingNext = list;
                last = list;
                while (last.pendingNext != list) {
                    last = last.pendingNext;
                }
                last.pendingNext = unenqueued;
            }
            ReferenceQueue.class.notifyAll();
        }
    }
}
