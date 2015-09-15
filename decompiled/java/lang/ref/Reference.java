package java.lang.ref;

public abstract class Reference<T> {
    private static boolean disableIntrinsic;
    private static boolean slowPathEnabled;
    public volatile Reference<?> pendingNext;
    volatile ReferenceQueue<? super T> queue;
    volatile Reference queueNext;
    volatile T referent;

    private final native T getReferent();

    static {
        disableIntrinsic = false;
        slowPathEnabled = false;
    }

    Reference() {
    }

    Reference(T r, ReferenceQueue<? super T> q) {
        this.referent = r;
        this.queue = q;
    }

    public void clear() {
        this.referent = null;
    }

    public final synchronized boolean enqueueInternal() {
        boolean z;
        if (this.queue == null || this.queueNext != null) {
            z = false;
        } else {
            this.queue.enqueue(this);
            this.queue = null;
            z = true;
        }
        return z;
    }

    public boolean enqueue() {
        return enqueueInternal();
    }

    public T get() {
        return getReferent();
    }

    public boolean isEnqueued() {
        return this.queueNext != null;
    }
}
