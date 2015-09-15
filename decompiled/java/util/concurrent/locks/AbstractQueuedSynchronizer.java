package java.util.concurrent.locks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.xmlpull.v1.XmlPullParser;
import sun.misc.Unsafe;

public abstract class AbstractQueuedSynchronizer extends AbstractOwnableSynchronizer implements Serializable {
    private static final long headOffset;
    private static final long nextOffset;
    private static final long serialVersionUID = 7373984972572414691L;
    static final long spinForTimeoutThreshold = 1000;
    private static final long stateOffset;
    private static final long tailOffset;
    private static final Unsafe unsafe;
    private static final long waitStatusOffset;
    private volatile transient Node head;
    private volatile int state;
    private volatile transient Node tail;

    public class ConditionObject implements Condition, Serializable {
        private static final int REINTERRUPT = 1;
        private static final int THROW_IE = -1;
        private static final long serialVersionUID = 1173984872572414699L;
        private transient Node firstWaiter;
        private transient Node lastWaiter;

        private Node addConditionWaiter() {
            Node t = this.lastWaiter;
            if (!(t == null || t.waitStatus == -2)) {
                unlinkCancelledWaiters();
                t = this.lastWaiter;
            }
            Node node = new Node(Thread.currentThread(), -2);
            if (t == null) {
                this.firstWaiter = node;
            } else {
                t.nextWaiter = node;
            }
            this.lastWaiter = node;
            return node;
        }

        private void doSignal(Node first) {
            do {
                Node node = first.nextWaiter;
                this.firstWaiter = node;
                if (node == null) {
                    this.lastWaiter = null;
                }
                first.nextWaiter = null;
                if (!AbstractQueuedSynchronizer.this.transferForSignal(first)) {
                    first = this.firstWaiter;
                } else {
                    return;
                }
            } while (first != null);
        }

        private void doSignalAll(Node first) {
            this.firstWaiter = null;
            this.lastWaiter = null;
            do {
                Node next = first.nextWaiter;
                first.nextWaiter = null;
                AbstractQueuedSynchronizer.this.transferForSignal(first);
                first = next;
            } while (first != null);
        }

        private void unlinkCancelledWaiters() {
            Node t = this.firstWaiter;
            Node trail = null;
            while (t != null) {
                Node next = t.nextWaiter;
                if (t.waitStatus != -2) {
                    t.nextWaiter = null;
                    if (trail == null) {
                        this.firstWaiter = next;
                    } else {
                        trail.nextWaiter = next;
                    }
                    if (next == null) {
                        this.lastWaiter = trail;
                    }
                } else {
                    trail = t;
                }
                t = next;
            }
        }

        public final void signal() {
            if (AbstractQueuedSynchronizer.this.isHeldExclusively()) {
                Node first = this.firstWaiter;
                if (first != null) {
                    doSignal(first);
                    return;
                }
                return;
            }
            throw new IllegalMonitorStateException();
        }

        public final void signalAll() {
            if (AbstractQueuedSynchronizer.this.isHeldExclusively()) {
                Node first = this.firstWaiter;
                if (first != null) {
                    doSignalAll(first);
                    return;
                }
                return;
            }
            throw new IllegalMonitorStateException();
        }

        public final void awaitUninterruptibly() {
            Node node = addConditionWaiter();
            int savedState = AbstractQueuedSynchronizer.this.fullyRelease(node);
            boolean interrupted = false;
            while (!AbstractQueuedSynchronizer.this.isOnSyncQueue(node)) {
                LockSupport.park(this);
                if (Thread.interrupted()) {
                    interrupted = true;
                }
            }
            if (AbstractQueuedSynchronizer.this.acquireQueued(node, savedState) || interrupted) {
                AbstractQueuedSynchronizer.selfInterrupt();
            }
        }

        private int checkInterruptWhileWaiting(Node node) {
            if (Thread.interrupted()) {
                return AbstractQueuedSynchronizer.this.transferAfterCancelledWait(node) ? THROW_IE : REINTERRUPT;
            } else {
                return 0;
            }
        }

        private void reportInterruptAfterWait(int interruptMode) throws InterruptedException {
            if (interruptMode == THROW_IE) {
                throw new InterruptedException();
            } else if (interruptMode == REINTERRUPT) {
                AbstractQueuedSynchronizer.selfInterrupt();
            }
        }

        public final void await() throws InterruptedException {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            Node node = addConditionWaiter();
            int savedState = AbstractQueuedSynchronizer.this.fullyRelease(node);
            int interruptMode = 0;
            while (!AbstractQueuedSynchronizer.this.isOnSyncQueue(node)) {
                LockSupport.park(this);
                interruptMode = checkInterruptWhileWaiting(node);
                if (interruptMode != 0) {
                    break;
                }
            }
            if (AbstractQueuedSynchronizer.this.acquireQueued(node, savedState) && interruptMode != THROW_IE) {
                interruptMode = REINTERRUPT;
            }
            if (node.nextWaiter != null) {
                unlinkCancelledWaiters();
            }
            if (interruptMode != 0) {
                reportInterruptAfterWait(interruptMode);
            }
        }

        public final long awaitNanos(long nanosTimeout) throws InterruptedException {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            Node node = addConditionWaiter();
            int savedState = AbstractQueuedSynchronizer.this.fullyRelease(node);
            long deadline = System.nanoTime() + nanosTimeout;
            int interruptMode = 0;
            while (!AbstractQueuedSynchronizer.this.isOnSyncQueue(node)) {
                if (nanosTimeout > AbstractQueuedSynchronizer.nextOffset) {
                    if (nanosTimeout >= AbstractQueuedSynchronizer.spinForTimeoutThreshold) {
                        LockSupport.parkNanos(this, nanosTimeout);
                    }
                    interruptMode = checkInterruptWhileWaiting(node);
                    if (interruptMode != 0) {
                        break;
                    }
                    nanosTimeout = deadline - System.nanoTime();
                } else {
                    AbstractQueuedSynchronizer.this.transferAfterCancelledWait(node);
                    break;
                }
            }
            if (AbstractQueuedSynchronizer.this.acquireQueued(node, savedState) && interruptMode != THROW_IE) {
                interruptMode = REINTERRUPT;
            }
            if (node.nextWaiter != null) {
                unlinkCancelledWaiters();
            }
            if (interruptMode != 0) {
                reportInterruptAfterWait(interruptMode);
            }
            return deadline - System.nanoTime();
        }

        public final boolean awaitUntil(Date deadline) throws InterruptedException {
            long abstime = deadline.getTime();
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            Node node = addConditionWaiter();
            int savedState = AbstractQueuedSynchronizer.this.fullyRelease(node);
            boolean timedout = false;
            int interruptMode = 0;
            while (!AbstractQueuedSynchronizer.this.isOnSyncQueue(node)) {
                if (System.currentTimeMillis() <= abstime) {
                    LockSupport.parkUntil(this, abstime);
                    interruptMode = checkInterruptWhileWaiting(node);
                    if (interruptMode != 0) {
                        break;
                    }
                }
                timedout = AbstractQueuedSynchronizer.this.transferAfterCancelledWait(node);
                break;
            }
            if (AbstractQueuedSynchronizer.this.acquireQueued(node, savedState) && interruptMode != THROW_IE) {
                interruptMode = REINTERRUPT;
            }
            if (node.nextWaiter != null) {
                unlinkCancelledWaiters();
            }
            if (interruptMode != 0) {
                reportInterruptAfterWait(interruptMode);
            }
            if (timedout) {
                return false;
            }
            return true;
        }

        public final boolean await(long time, TimeUnit unit) throws InterruptedException {
            long nanosTimeout = unit.toNanos(time);
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            Node node = addConditionWaiter();
            int savedState = AbstractQueuedSynchronizer.this.fullyRelease(node);
            long deadline = System.nanoTime() + nanosTimeout;
            boolean timedout = false;
            int interruptMode = 0;
            while (!AbstractQueuedSynchronizer.this.isOnSyncQueue(node)) {
                if (nanosTimeout > AbstractQueuedSynchronizer.nextOffset) {
                    if (nanosTimeout >= AbstractQueuedSynchronizer.spinForTimeoutThreshold) {
                        LockSupport.parkNanos(this, nanosTimeout);
                    }
                    interruptMode = checkInterruptWhileWaiting(node);
                    if (interruptMode != 0) {
                        break;
                    }
                    nanosTimeout = deadline - System.nanoTime();
                } else {
                    timedout = AbstractQueuedSynchronizer.this.transferAfterCancelledWait(node);
                    break;
                }
            }
            if (AbstractQueuedSynchronizer.this.acquireQueued(node, savedState) && interruptMode != THROW_IE) {
                interruptMode = REINTERRUPT;
            }
            if (node.nextWaiter != null) {
                unlinkCancelledWaiters();
            }
            if (interruptMode != 0) {
                reportInterruptAfterWait(interruptMode);
            }
            if (timedout) {
                return false;
            }
            return true;
        }

        final boolean isOwnedBy(AbstractQueuedSynchronizer sync) {
            return sync == AbstractQueuedSynchronizer.this;
        }

        protected final boolean hasWaiters() {
            if (AbstractQueuedSynchronizer.this.isHeldExclusively()) {
                for (Node w = this.firstWaiter; w != null; w = w.nextWaiter) {
                    if (w.waitStatus == -2) {
                        return true;
                    }
                }
                return false;
            }
            throw new IllegalMonitorStateException();
        }

        protected final int getWaitQueueLength() {
            if (AbstractQueuedSynchronizer.this.isHeldExclusively()) {
                int n = 0;
                for (Node w = this.firstWaiter; w != null; w = w.nextWaiter) {
                    if (w.waitStatus == -2) {
                        n += REINTERRUPT;
                    }
                }
                return n;
            }
            throw new IllegalMonitorStateException();
        }

        protected final Collection<Thread> getWaitingThreads() {
            if (AbstractQueuedSynchronizer.this.isHeldExclusively()) {
                ArrayList<Thread> list = new ArrayList();
                for (Node w = this.firstWaiter; w != null; w = w.nextWaiter) {
                    if (w.waitStatus == -2) {
                        Thread t = w.thread;
                        if (t != null) {
                            list.add(t);
                        }
                    }
                }
                return list;
            }
            throw new IllegalMonitorStateException();
        }
    }

    static final class Node {
        static final int CANCELLED = 1;
        static final int CONDITION = -2;
        static final Node EXCLUSIVE;
        static final int PROPAGATE = -3;
        static final Node SHARED;
        static final int SIGNAL = -1;
        volatile Node next;
        Node nextWaiter;
        volatile Node prev;
        volatile Thread thread;
        volatile int waitStatus;

        static {
            SHARED = new Node();
            EXCLUSIVE = null;
        }

        final boolean isShared() {
            return this.nextWaiter == SHARED;
        }

        final Node predecessor() throws NullPointerException {
            Node p = this.prev;
            if (p != null) {
                return p;
            }
            throw new NullPointerException();
        }

        Node() {
        }

        Node(Thread thread, Node mode) {
            this.nextWaiter = mode;
            this.thread = thread;
        }

        Node(Thread thread, int waitStatus) {
            this.waitStatus = waitStatus;
            this.thread = thread;
        }
    }

    protected AbstractQueuedSynchronizer() {
    }

    protected final int getState() {
        return this.state;
    }

    protected final void setState(int newState) {
        this.state = newState;
    }

    protected final boolean compareAndSetState(int expect, int update) {
        return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
    }

    private Node enq(Node node) {
        while (true) {
            Node t = this.tail;
            if (t != null) {
                node.prev = t;
                if (compareAndSetTail(t, node)) {
                    t.next = node;
                    return t;
                }
            } else if (compareAndSetHead(new Node())) {
                this.tail = this.head;
            }
        }
    }

    private Node addWaiter(Node mode) {
        Node node = new Node(Thread.currentThread(), mode);
        Node pred = this.tail;
        if (pred != null) {
            node.prev = pred;
            if (compareAndSetTail(pred, node)) {
                pred.next = node;
                return node;
            }
        }
        enq(node);
        return node;
    }

    private void setHead(Node node) {
        this.head = node;
        node.thread = null;
        node.prev = null;
    }

    private void unparkSuccessor(Node node) {
        int ws = node.waitStatus;
        if (ws < 0) {
            compareAndSetWaitStatus(node, ws, 0);
        }
        Node s = node.next;
        if (s == null || s.waitStatus > 0) {
            s = null;
            Node t = this.tail;
            while (t != null && t != node) {
                if (t.waitStatus <= 0) {
                    s = t;
                }
                t = t.prev;
            }
        }
        if (s != null) {
            LockSupport.unpark(s.thread);
        }
    }

    private void doReleaseShared() {
        while (true) {
            Node h = this.head;
            if (!(h == null || h == this.tail)) {
                int ws = h.waitStatus;
                if (ws == -1) {
                    if (compareAndSetWaitStatus(h, -1, 0)) {
                        unparkSuccessor(h);
                    } else {
                        continue;
                    }
                } else if (ws == 0 && !compareAndSetWaitStatus(h, 0, -3)) {
                }
            }
            if (h == this.head) {
                return;
            }
        }
    }

    private void setHeadAndPropagate(Node node, int propagate) {
        Node h = this.head;
        setHead(node);
        if (propagate > 0 || h == null || h.waitStatus < 0) {
            Node s = node.next;
            if (s == null || s.isShared()) {
                doReleaseShared();
            }
        }
    }

    private void cancelAcquire(Node node) {
        if (node != null) {
            node.thread = null;
            Node pred = node.prev;
            while (pred.waitStatus > 0) {
                pred = pred.prev;
                node.prev = pred;
            }
            Node predNext = pred.next;
            node.waitStatus = 1;
            if (node == this.tail && compareAndSetTail(node, pred)) {
                compareAndSetNext(pred, predNext, null);
                return;
            }
            if (pred != this.head) {
                int ws = pred.waitStatus;
                if ((ws == -1 || (ws <= 0 && compareAndSetWaitStatus(pred, ws, -1))) && pred.thread != null) {
                    Node next = node.next;
                    if (next != null && next.waitStatus <= 0) {
                        compareAndSetNext(pred, predNext, next);
                    }
                    node.next = node;
                }
            }
            unparkSuccessor(node);
            node.next = node;
        }
    }

    private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
        int ws = pred.waitStatus;
        if (ws == -1) {
            return true;
        }
        if (ws > 0) {
            do {
                pred = pred.prev;
                node.prev = pred;
            } while (pred.waitStatus > 0);
            pred.next = node;
        } else {
            compareAndSetWaitStatus(pred, ws, -1);
        }
        return false;
    }

    static void selfInterrupt() {
        Thread.currentThread().interrupt();
    }

    private final boolean parkAndCheckInterrupt() {
        LockSupport.park(this);
        return Thread.interrupted();
    }

    final boolean acquireQueued(Node node, int arg) {
        Node p;
        boolean failed = true;
        boolean interrupted = false;
        while (true) {
            try {
                p = node.predecessor();
                if (p == this.head && tryAcquire(arg)) {
                    break;
                } else if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt()) {
                    interrupted = true;
                }
            } finally {
                if (failed) {
                    cancelAcquire(node);
                }
            }
        }
        setHead(node);
        p.next = null;
        failed = false;
        return interrupted;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void doAcquireInterruptibly(int r5) throws java.lang.InterruptedException {
        /*
        r4 = this;
        r3 = java.util.concurrent.locks.AbstractQueuedSynchronizer.Node.EXCLUSIVE;
        r1 = r4.addWaiter(r3);
        r0 = 1;
    L_0x0007:
        r2 = r1.predecessor();	 Catch:{ all -> 0x0034 }
        r3 = r4.head;	 Catch:{ all -> 0x0034 }
        if (r2 != r3) goto L_0x0022;
    L_0x000f:
        r3 = r4.tryAcquire(r5);	 Catch:{ all -> 0x0034 }
        if (r3 == 0) goto L_0x0022;
    L_0x0015:
        r4.setHead(r1);	 Catch:{ all -> 0x0034 }
        r3 = 0;
        r2.next = r3;	 Catch:{ all -> 0x0034 }
        r0 = 0;
        if (r0 == 0) goto L_0x0021;
    L_0x001e:
        r4.cancelAcquire(r1);
    L_0x0021:
        return;
    L_0x0022:
        r3 = shouldParkAfterFailedAcquire(r2, r1);	 Catch:{ all -> 0x0034 }
        if (r3 == 0) goto L_0x0007;
    L_0x0028:
        r3 = r4.parkAndCheckInterrupt();	 Catch:{ all -> 0x0034 }
        if (r3 == 0) goto L_0x0007;
    L_0x002e:
        r3 = new java.lang.InterruptedException;	 Catch:{ all -> 0x0034 }
        r3.<init>();	 Catch:{ all -> 0x0034 }
        throw r3;	 Catch:{ all -> 0x0034 }
    L_0x0034:
        r3 = move-exception;
        if (r0 == 0) goto L_0x003a;
    L_0x0037:
        r4.cancelAcquire(r1);
    L_0x003a:
        throw r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.locks.AbstractQueuedSynchronizer.doAcquireInterruptibly(int):void");
    }

    private boolean doAcquireNanos(int arg, long nanosTimeout) throws InterruptedException {
        boolean z = false;
        if (nanosTimeout > nextOffset) {
            long deadline = System.nanoTime() + nanosTimeout;
            Node node = addWaiter(Node.EXCLUSIVE);
            do {
                Node p = node.predecessor();
                if (p == this.head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null;
                    z = true;
                    if (false) {
                        cancelAcquire(node);
                    }
                } else {
                    try {
                        nanosTimeout = deadline - System.nanoTime();
                        if (nanosTimeout <= nextOffset) {
                            if (1 != null) {
                                cancelAcquire(node);
                            }
                        } else if (shouldParkAfterFailedAcquire(p, node) && nanosTimeout > spinForTimeoutThreshold) {
                            LockSupport.parkNanos(this, nanosTimeout);
                        }
                    } catch (Throwable th) {
                        if (1 != null) {
                            cancelAcquire(node);
                        }
                    }
                }
            } while (!Thread.interrupted());
            throw new InterruptedException();
        }
        return z;
    }

    private void doAcquireShared(int arg) {
        Node p;
        int r;
        Node node = addWaiter(Node.SHARED);
        boolean interrupted = false;
        while (true) {
            try {
                p = node.predecessor();
                if (p == this.head) {
                    r = tryAcquireShared(arg);
                    if (r >= 0) {
                        break;
                    }
                }
                if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt()) {
                    interrupted = true;
                }
            } catch (Throwable th) {
                if (true) {
                    cancelAcquire(node);
                }
            }
        }
        setHeadAndPropagate(node, r);
        p.next = null;
        if (interrupted) {
            selfInterrupt();
        }
        if (false) {
            cancelAcquire(node);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void doAcquireSharedInterruptibly(int r6) throws java.lang.InterruptedException {
        /*
        r5 = this;
        r4 = java.util.concurrent.locks.AbstractQueuedSynchronizer.Node.SHARED;
        r1 = r5.addWaiter(r4);
        r0 = 1;
    L_0x0007:
        r2 = r1.predecessor();	 Catch:{ all -> 0x0034 }
        r4 = r5.head;	 Catch:{ all -> 0x0034 }
        if (r2 != r4) goto L_0x0022;
    L_0x000f:
        r3 = r5.tryAcquireShared(r6);	 Catch:{ all -> 0x0034 }
        if (r3 < 0) goto L_0x0022;
    L_0x0015:
        r5.setHeadAndPropagate(r1, r3);	 Catch:{ all -> 0x0034 }
        r4 = 0;
        r2.next = r4;	 Catch:{ all -> 0x0034 }
        r0 = 0;
        if (r0 == 0) goto L_0x0021;
    L_0x001e:
        r5.cancelAcquire(r1);
    L_0x0021:
        return;
    L_0x0022:
        r4 = shouldParkAfterFailedAcquire(r2, r1);	 Catch:{ all -> 0x0034 }
        if (r4 == 0) goto L_0x0007;
    L_0x0028:
        r4 = r5.parkAndCheckInterrupt();	 Catch:{ all -> 0x0034 }
        if (r4 == 0) goto L_0x0007;
    L_0x002e:
        r4 = new java.lang.InterruptedException;	 Catch:{ all -> 0x0034 }
        r4.<init>();	 Catch:{ all -> 0x0034 }
        throw r4;	 Catch:{ all -> 0x0034 }
    L_0x0034:
        r4 = move-exception;
        if (r0 == 0) goto L_0x003a;
    L_0x0037:
        r5.cancelAcquire(r1);
    L_0x003a:
        throw r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.locks.AbstractQueuedSynchronizer.doAcquireSharedInterruptibly(int):void");
    }

    private boolean doAcquireSharedNanos(int arg, long nanosTimeout) throws InterruptedException {
        boolean z = false;
        if (nanosTimeout > nextOffset) {
            long deadline = System.nanoTime() + nanosTimeout;
            Node node = addWaiter(Node.SHARED);
            do {
                Node p = node.predecessor();
                if (p == this.head) {
                    int r = tryAcquireShared(arg);
                    if (r >= 0) {
                        setHeadAndPropagate(node, r);
                        p.next = null;
                        z = true;
                        if (false) {
                            cancelAcquire(node);
                        }
                    }
                }
                try {
                    nanosTimeout = deadline - System.nanoTime();
                    if (nanosTimeout <= nextOffset) {
                        if (1 != null) {
                            cancelAcquire(node);
                        }
                    } else if (shouldParkAfterFailedAcquire(p, node) && nanosTimeout > spinForTimeoutThreshold) {
                        LockSupport.parkNanos(this, nanosTimeout);
                    }
                } catch (Throwable th) {
                    if (1 != null) {
                        cancelAcquire(node);
                    }
                }
            } while (!Thread.interrupted());
            throw new InterruptedException();
        }
        return z;
    }

    protected boolean tryAcquire(int arg) {
        throw new UnsupportedOperationException();
    }

    protected boolean tryRelease(int arg) {
        throw new UnsupportedOperationException();
    }

    protected int tryAcquireShared(int arg) {
        throw new UnsupportedOperationException();
    }

    protected boolean tryReleaseShared(int arg) {
        throw new UnsupportedOperationException();
    }

    protected boolean isHeldExclusively() {
        throw new UnsupportedOperationException();
    }

    public final void acquire(int arg) {
        if (!tryAcquire(arg) && acquireQueued(addWaiter(Node.EXCLUSIVE), arg)) {
            selfInterrupt();
        }
    }

    public final void acquireInterruptibly(int arg) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        } else if (!tryAcquire(arg)) {
            doAcquireInterruptibly(arg);
        }
    }

    public final boolean tryAcquireNanos(int arg, long nanosTimeout) throws InterruptedException {
        if (!Thread.interrupted()) {
            return tryAcquire(arg) || doAcquireNanos(arg, nanosTimeout);
        } else {
            throw new InterruptedException();
        }
    }

    public final boolean release(int arg) {
        if (!tryRelease(arg)) {
            return false;
        }
        Node h = this.head;
        if (!(h == null || h.waitStatus == 0)) {
            unparkSuccessor(h);
        }
        return true;
    }

    public final void acquireShared(int arg) {
        if (tryAcquireShared(arg) < 0) {
            doAcquireShared(arg);
        }
    }

    public final void acquireSharedInterruptibly(int arg) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        } else if (tryAcquireShared(arg) < 0) {
            doAcquireSharedInterruptibly(arg);
        }
    }

    public final boolean tryAcquireSharedNanos(int arg, long nanosTimeout) throws InterruptedException {
        if (!Thread.interrupted()) {
            return tryAcquireShared(arg) >= 0 || doAcquireSharedNanos(arg, nanosTimeout);
        } else {
            throw new InterruptedException();
        }
    }

    public final boolean releaseShared(int arg) {
        if (!tryReleaseShared(arg)) {
            return false;
        }
        doReleaseShared();
        return true;
    }

    public final boolean hasQueuedThreads() {
        return this.head != this.tail;
    }

    public final boolean hasContended() {
        return this.head != null;
    }

    public final Thread getFirstQueuedThread() {
        return this.head == this.tail ? null : fullGetFirstQueuedThread();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.Thread fullGetFirstQueuedThread() {
        /*
        r8 = this;
        r1 = r8.head;
        if (r1 == 0) goto L_0x0012;
    L_0x0004:
        r2 = r1.next;
        if (r2 == 0) goto L_0x0012;
    L_0x0008:
        r6 = r2.prev;
        r7 = r8.head;
        if (r6 != r7) goto L_0x0012;
    L_0x000e:
        r3 = r2.thread;
        if (r3 != 0) goto L_0x0024;
    L_0x0012:
        r1 = r8.head;
        if (r1 == 0) goto L_0x0026;
    L_0x0016:
        r2 = r1.next;
        if (r2 == 0) goto L_0x0026;
    L_0x001a:
        r6 = r2.prev;
        r7 = r8.head;
        if (r6 != r7) goto L_0x0026;
    L_0x0020:
        r3 = r2.thread;
        if (r3 == 0) goto L_0x0026;
    L_0x0024:
        r0 = r3;
    L_0x0025:
        return r0;
    L_0x0026:
        r4 = r8.tail;
        r0 = 0;
    L_0x0029:
        if (r4 == 0) goto L_0x0025;
    L_0x002b:
        r6 = r8.head;
        if (r4 == r6) goto L_0x0025;
    L_0x002f:
        r5 = r4.thread;
        if (r5 == 0) goto L_0x0034;
    L_0x0033:
        r0 = r5;
    L_0x0034:
        r4 = r4.prev;
        goto L_0x0029;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.locks.AbstractQueuedSynchronizer.fullGetFirstQueuedThread():java.lang.Thread");
    }

    public final boolean isQueued(Thread thread) {
        if (thread == null) {
            throw new NullPointerException();
        }
        for (Node p = this.tail; p != null; p = p.prev) {
            if (p.thread == thread) {
                return true;
            }
        }
        return false;
    }

    final boolean apparentlyFirstQueuedIsExclusive() {
        Node h = this.head;
        if (h != null) {
            Node s = h.next;
            if (!(s == null || s.isShared() || s.thread == null)) {
                return true;
            }
        }
        return false;
    }

    public final boolean hasQueuedPredecessors() {
        Node t = this.tail;
        Node h = this.head;
        if (h != t) {
            Node s = h.next;
            if (s == null || s.thread != Thread.currentThread()) {
                return true;
            }
        }
        return false;
    }

    public final int getQueueLength() {
        int n = 0;
        for (Node p = this.tail; p != null; p = p.prev) {
            if (p.thread != null) {
                n++;
            }
        }
        return n;
    }

    public final Collection<Thread> getQueuedThreads() {
        ArrayList<Thread> list = new ArrayList();
        for (Node p = this.tail; p != null; p = p.prev) {
            Thread t = p.thread;
            if (t != null) {
                list.add(t);
            }
        }
        return list;
    }

    public final Collection<Thread> getExclusiveQueuedThreads() {
        ArrayList<Thread> list = new ArrayList();
        for (Node p = this.tail; p != null; p = p.prev) {
            if (!p.isShared()) {
                Thread t = p.thread;
                if (t != null) {
                    list.add(t);
                }
            }
        }
        return list;
    }

    public final Collection<Thread> getSharedQueuedThreads() {
        ArrayList<Thread> list = new ArrayList();
        for (Node p = this.tail; p != null; p = p.prev) {
            if (p.isShared()) {
                Thread t = p.thread;
                if (t != null) {
                    list.add(t);
                }
            }
        }
        return list;
    }

    public String toString() {
        return super.toString() + "[State = " + getState() + ", " + (hasQueuedThreads() ? "non" : XmlPullParser.NO_NAMESPACE) + "empty queue]";
    }

    final boolean isOnSyncQueue(Node node) {
        if (node.waitStatus == -2 || node.prev == null) {
            return false;
        }
        if (node.next != null) {
            return true;
        }
        return findNodeFromTail(node);
    }

    private boolean findNodeFromTail(Node node) {
        for (Node t = this.tail; t != node; t = t.prev) {
            if (t == null) {
                return false;
            }
        }
        return true;
    }

    final boolean transferForSignal(Node node) {
        if (!compareAndSetWaitStatus(node, -2, 0)) {
            return false;
        }
        Node p = enq(node);
        int ws = p.waitStatus;
        if (ws > 0 || !compareAndSetWaitStatus(p, ws, -1)) {
            LockSupport.unpark(node.thread);
        }
        return true;
    }

    final boolean transferAfterCancelledWait(Node node) {
        if (compareAndSetWaitStatus(node, -2, 0)) {
            enq(node);
            return true;
        }
        while (!isOnSyncQueue(node)) {
            Thread.yield();
        }
        return false;
    }

    final int fullyRelease(Node node) {
        boolean failed = true;
        try {
            int savedState = getState();
            if (release(savedState)) {
                failed = false;
                return savedState;
            }
            throw new IllegalMonitorStateException();
        } finally {
            if (failed) {
                node.waitStatus = 1;
            }
        }
    }

    public final boolean owns(ConditionObject condition) {
        return condition.isOwnedBy(this);
    }

    public final boolean hasWaiters(ConditionObject condition) {
        if (owns(condition)) {
            return condition.hasWaiters();
        }
        throw new IllegalArgumentException("Not owner");
    }

    public final int getWaitQueueLength(ConditionObject condition) {
        if (owns(condition)) {
            return condition.getWaitQueueLength();
        }
        throw new IllegalArgumentException("Not owner");
    }

    public final Collection<Thread> getWaitingThreads(ConditionObject condition) {
        if (owns(condition)) {
            return condition.getWaitingThreads();
        }
        throw new IllegalArgumentException("Not owner");
    }

    static {
        unsafe = Unsafe.getUnsafe();
        try {
            stateOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("state"));
            headOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("head"));
            tailOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("tail"));
            waitStatusOffset = unsafe.objectFieldOffset(Node.class.getDeclaredField("waitStatus"));
            nextOffset = unsafe.objectFieldOffset(Node.class.getDeclaredField("next"));
        } catch (Throwable ex) {
            throw new Error(ex);
        }
    }

    private final boolean compareAndSetHead(Node update) {
        return unsafe.compareAndSwapObject(this, headOffset, null, update);
    }

    private final boolean compareAndSetTail(Node expect, Node update) {
        return unsafe.compareAndSwapObject(this, tailOffset, expect, update);
    }

    private static final boolean compareAndSetWaitStatus(Node node, int expect, int update) {
        return unsafe.compareAndSwapInt(node, waitStatusOffset, expect, update);
    }

    private static final boolean compareAndSetNext(Node node, Node expect, Node update) {
        return unsafe.compareAndSwapObject(node, nextOffset, expect, update);
    }
}
