package java.lang.ref;

public final class FinalizerReference<T> extends Reference<T> {
    private static final Object LIST_LOCK;
    private static FinalizerReference<?> head;
    public static final ReferenceQueue<Object> queue;
    private FinalizerReference<?> next;
    private FinalizerReference<?> prev;
    private T zombie;

    private static class Sentinel {
        boolean finalized;

        private Sentinel() {
            this.finalized = false;
        }

        protected synchronized void finalize() throws Throwable {
            if (this.finalized) {
                throw new AssertionError();
            }
            this.finalized = true;
            notifyAll();
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        synchronized void awaitFinalization() throws java.lang.InterruptedException {
            /*
            r1 = this;
            monitor-enter(r1);
        L_0x0001:
            r0 = r1.finalized;	 Catch:{ all -> 0x0009 }
            if (r0 != 0) goto L_0x000c;
        L_0x0005:
            r1.wait();	 Catch:{ all -> 0x0009 }
            goto L_0x0001;
        L_0x0009:
            r0 = move-exception;
            monitor-exit(r1);
            throw r0;
        L_0x000c:
            monitor-exit(r1);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: java.lang.ref.FinalizerReference.Sentinel.awaitFinalization():void");
        }
    }

    private native boolean makeCircularListIfUnenqueued();

    static {
        queue = new ReferenceQueue();
        LIST_LOCK = new Object();
        head = null;
    }

    public FinalizerReference(T r, ReferenceQueue<? super T> q) {
        super(r, q);
    }

    public T get() {
        return this.zombie;
    }

    public void clear() {
        this.zombie = null;
    }

    public static void add(Object referent) {
        FinalizerReference<?> reference = new FinalizerReference(referent, queue);
        synchronized (LIST_LOCK) {
            reference.prev = null;
            reference.next = head;
            if (head != null) {
                head.prev = reference;
            }
            head = reference;
        }
    }

    public static void remove(FinalizerReference<?> reference) {
        synchronized (LIST_LOCK) {
            FinalizerReference<?> next = reference.next;
            FinalizerReference<?> prev = reference.prev;
            reference.next = null;
            reference.prev = null;
            if (prev != null) {
                prev.next = next;
            } else {
                head = next;
            }
            if (next != null) {
                next.prev = prev;
            }
        }
    }

    public static void finalizeAllEnqueued() throws InterruptedException {
        Sentinel sentinel;
        do {
            sentinel = new Sentinel();
        } while (!enqueueSentinelReference(sentinel));
        sentinel.awaitFinalization();
    }

    private static boolean enqueueSentinelReference(Sentinel sentinel) {
        synchronized (LIST_LOCK) {
            for (FinalizerReference<?> r = head; r != null; r = r.next) {
                if (r.referent == sentinel) {
                    boolean z;
                    FinalizerReference<Sentinel> sentinelReference = r;
                    sentinelReference.referent = null;
                    sentinelReference.zombie = sentinel;
                    if (sentinelReference.makeCircularListIfUnenqueued()) {
                        ReferenceQueue.add(sentinelReference);
                        z = true;
                    } else {
                        z = false;
                    }
                    return z;
                }
            }
            throw new AssertionError((Object) "newly-created live Sentinel not on list!");
        }
    }
}
