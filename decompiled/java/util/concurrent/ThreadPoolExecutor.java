package java.util.concurrent;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPoolExecutor extends AbstractExecutorService {
    private static final int CAPACITY = 536870911;
    private static final int COUNT_BITS = 29;
    private static final boolean ONLY_ONE = true;
    private static final int RUNNING = -536870912;
    private static final int SHUTDOWN = 0;
    private static final int STOP = 536870912;
    private static final int TERMINATED = 1610612736;
    private static final int TIDYING = 1073741824;
    private static final RejectedExecutionHandler defaultHandler;
    private static final RuntimePermission shutdownPerm;
    private volatile boolean allowCoreThreadTimeOut;
    private long completedTaskCount;
    private volatile int corePoolSize;
    private final AtomicInteger ctl;
    private volatile RejectedExecutionHandler handler;
    private volatile long keepAliveTime;
    private int largestPoolSize;
    private final ReentrantLock mainLock;
    private volatile int maximumPoolSize;
    private final Condition termination;
    private volatile ThreadFactory threadFactory;
    private final BlockingQueue<Runnable> workQueue;
    private final HashSet<Worker> workers;

    public static class AbortPolicy implements RejectedExecutionHandler {
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            throw new RejectedExecutionException("Task " + r.toString() + " rejected from " + e.toString());
        }
    }

    public static class CallerRunsPolicy implements RejectedExecutionHandler {
        public void rejectedExecution(java.lang.Runnable r1, java.util.concurrent.ThreadPoolExecutor r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy.rejectedExecution(java.lang.Runnable, java.util.concurrent.ThreadPoolExecutor):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy.rejectedExecution(java.lang.Runnable, java.util.concurrent.ThreadPoolExecutor):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy.rejectedExecution(java.lang.Runnable, java.util.concurrent.ThreadPoolExecutor):void");
        }
    }

    public static class DiscardOldestPolicy implements RejectedExecutionHandler {
        public void rejectedExecution(java.lang.Runnable r1, java.util.concurrent.ThreadPoolExecutor r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy.rejectedExecution(java.lang.Runnable, java.util.concurrent.ThreadPoolExecutor):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy.rejectedExecution(java.lang.Runnable, java.util.concurrent.ThreadPoolExecutor):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy.rejectedExecution(java.lang.Runnable, java.util.concurrent.ThreadPoolExecutor):void");
        }
    }

    public static class DiscardPolicy implements RejectedExecutionHandler {
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        }
    }

    private final class Worker extends AbstractQueuedSynchronizer implements Runnable {
        private static final long serialVersionUID = 6138294804551838833L;
        volatile long completedTasks;
        Runnable firstTask;
        final Thread thread;

        Worker(Runnable firstTask) {
            setState(-1);
            this.firstTask = firstTask;
            this.thread = ThreadPoolExecutor.this.getThreadFactory().newThread(this);
        }

        public void run() {
            ThreadPoolExecutor.this.runWorker(this);
        }

        protected boolean isHeldExclusively() {
            return getState() != 0 ? ThreadPoolExecutor.ONLY_ONE : false;
        }

        protected boolean tryAcquire(int unused) {
            if (!compareAndSetState(ThreadPoolExecutor.SHUTDOWN, 1)) {
                return false;
            }
            setExclusiveOwnerThread(Thread.currentThread());
            return ThreadPoolExecutor.ONLY_ONE;
        }

        protected boolean tryRelease(int unused) {
            setExclusiveOwnerThread(null);
            setState(ThreadPoolExecutor.SHUTDOWN);
            return ThreadPoolExecutor.ONLY_ONE;
        }

        public void lock() {
            acquire(1);
        }

        public boolean tryLock() {
            return tryAcquire(1);
        }

        public void unlock() {
            release(1);
        }

        public boolean isLocked() {
            return isHeldExclusively();
        }

        void interruptIfStarted() {
            if (getState() >= 0) {
                Thread t = this.thread;
                if (t != null && !t.isInterrupted()) {
                    try {
                        t.interrupt();
                    } catch (SecurityException e) {
                    }
                }
            }
        }
    }

    private static int runStateOf(int c) {
        return RUNNING & c;
    }

    private static int workerCountOf(int c) {
        return CAPACITY & c;
    }

    private static int ctlOf(int rs, int wc) {
        return rs | wc;
    }

    private static boolean runStateLessThan(int c, int s) {
        return c < s ? ONLY_ONE : false;
    }

    private static boolean runStateAtLeast(int c, int s) {
        return c >= s ? ONLY_ONE : false;
    }

    private static boolean isRunning(int c) {
        return c < 0 ? ONLY_ONE : false;
    }

    private boolean compareAndIncrementWorkerCount(int expect) {
        return this.ctl.compareAndSet(expect, expect + 1);
    }

    private boolean compareAndDecrementWorkerCount(int expect) {
        return this.ctl.compareAndSet(expect, expect - 1);
    }

    private void decrementWorkerCount() {
        do {
        } while (!compareAndDecrementWorkerCount(this.ctl.get()));
    }

    static {
        defaultHandler = new AbortPolicy();
        shutdownPerm = new RuntimePermission("modifyThread");
    }

    private void advanceRunState(int targetState) {
        int c;
        do {
            c = this.ctl.get();
            if (runStateAtLeast(c, targetState)) {
                return;
            }
        } while (!this.ctl.compareAndSet(c, ctlOf(targetState, workerCountOf(c))));
    }

    final void tryTerminate() {
        while (true) {
            int c = this.ctl.get();
            if (!isRunning(c) && !runStateAtLeast(c, TIDYING)) {
                if (runStateOf(c) == 0 && !this.workQueue.isEmpty()) {
                    return;
                }
                if (workerCountOf(c) != 0) {
                    interruptIdleWorkers(ONLY_ONE);
                    return;
                }
                ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    if (this.ctl.compareAndSet(c, ctlOf(TIDYING, SHUTDOWN))) {
                        terminated();
                        this.ctl.set(ctlOf(TERMINATED, SHUTDOWN));
                        this.termination.signalAll();
                        mainLock.unlock();
                        return;
                    }
                    mainLock.unlock();
                } catch (Throwable th) {
                    mainLock.unlock();
                }
            } else {
                return;
            }
        }
    }

    private void checkShutdownAccess() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(shutdownPerm);
            ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                Iterator i$ = this.workers.iterator();
                while (i$.hasNext()) {
                    security.checkAccess(((Worker) i$.next()).thread);
                }
            } finally {
                mainLock.unlock();
            }
        }
    }

    private void interruptWorkers() {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            Iterator i$ = this.workers.iterator();
            while (i$.hasNext()) {
                ((Worker) i$.next()).interruptIfStarted();
            }
        } finally {
            mainLock.unlock();
        }
    }

    private void interruptIdleWorkers(boolean onlyOne) {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        Iterator i$ = this.workers.iterator();
        while (i$.hasNext()) {
            Worker w = (Worker) i$.next();
            Thread t = w.thread;
            if (!t.isInterrupted() && w.tryLock()) {
                try {
                    t.interrupt();
                    w.unlock();
                } catch (SecurityException e) {
                    w.unlock();
                    continue;
                } catch (Throwable th) {
                    mainLock.unlock();
                }
            }
            if (onlyOne) {
                break;
            }
        }
        mainLock.unlock();
    }

    private void interruptIdleWorkers() {
        interruptIdleWorkers(false);
    }

    final void reject(Runnable command) {
        this.handler.rejectedExecution(command, this);
    }

    void onShutdown() {
    }

    final boolean isRunningOrShutdown(boolean shutdownOK) {
        int rs = runStateOf(this.ctl.get());
        return (rs == RUNNING || (rs == 0 && shutdownOK)) ? ONLY_ONE : false;
    }

    private List<Runnable> drainQueue() {
        BlockingQueue<Runnable> q = this.workQueue;
        ArrayList<Runnable> taskList = new ArrayList();
        q.drainTo(taskList);
        if (!q.isEmpty()) {
            Runnable[] arr$ = (Runnable[]) q.toArray(new Runnable[SHUTDOWN]);
            int len$ = arr$.length;
            for (int i$ = SHUTDOWN; i$ < len$; i$++) {
                Runnable r = arr$[i$];
                if (q.remove(r)) {
                    taskList.add(r);
                }
            }
        }
        return taskList;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean addWorker(java.lang.Runnable r12, boolean r13) {
        /*
        r11 = this;
        r9 = 0;
    L_0x0001:
        r10 = r11.ctl;
        r0 = r10.get();
        r2 = runStateOf(r0);
        if (r2 < 0) goto L_0x001a;
    L_0x000d:
        if (r2 != 0) goto L_0x0019;
    L_0x000f:
        if (r12 != 0) goto L_0x0019;
    L_0x0011:
        r10 = r11.workQueue;
        r10 = r10.isEmpty();
        if (r10 == 0) goto L_0x001a;
    L_0x0019:
        return r9;
    L_0x001a:
        r7 = workerCountOf(r0);
        r10 = 536870911; // 0x1fffffff float:1.0842021E-19 double:2.652494734E-315;
        if (r7 >= r10) goto L_0x0019;
    L_0x0023:
        if (r13 == 0) goto L_0x0069;
    L_0x0025:
        r10 = r11.corePoolSize;
    L_0x0027:
        if (r7 >= r10) goto L_0x0019;
    L_0x0029:
        r10 = r11.compareAndIncrementWorkerCount(r0);
        if (r10 == 0) goto L_0x006c;
    L_0x002f:
        r9 = 0;
        r8 = 0;
        r5 = 0;
        r6 = new java.util.concurrent.ThreadPoolExecutor$Worker;	 Catch:{ all -> 0x009a }
        r6.<init>(r12);	 Catch:{ all -> 0x009a }
        r4 = r6.thread;	 Catch:{ all -> 0x0061 }
        if (r4 == 0) goto L_0x0094;
    L_0x003b:
        r1 = r11.mainLock;	 Catch:{ all -> 0x0061 }
        r1.lock();	 Catch:{ all -> 0x0061 }
        r10 = r11.ctl;	 Catch:{ all -> 0x005c }
        r10 = r10.get();	 Catch:{ all -> 0x005c }
        r2 = runStateOf(r10);	 Catch:{ all -> 0x005c }
        if (r2 < 0) goto L_0x0050;
    L_0x004c:
        if (r2 != 0) goto L_0x008b;
    L_0x004e:
        if (r12 != 0) goto L_0x008b;
    L_0x0050:
        r10 = r4.isAlive();	 Catch:{ all -> 0x005c }
        if (r10 == 0) goto L_0x0079;
    L_0x0056:
        r10 = new java.lang.IllegalThreadStateException;	 Catch:{ all -> 0x005c }
        r10.<init>();	 Catch:{ all -> 0x005c }
        throw r10;	 Catch:{ all -> 0x005c }
    L_0x005c:
        r10 = move-exception;
        r1.unlock();	 Catch:{ all -> 0x0061 }
        throw r10;	 Catch:{ all -> 0x0061 }
    L_0x0061:
        r10 = move-exception;
        r5 = r6;
    L_0x0063:
        if (r9 != 0) goto L_0x0068;
    L_0x0065:
        r11.addWorkerFailed(r5);
    L_0x0068:
        throw r10;
    L_0x0069:
        r10 = r11.maximumPoolSize;
        goto L_0x0027;
    L_0x006c:
        r10 = r11.ctl;
        r0 = r10.get();
        r10 = runStateOf(r0);
        if (r10 == r2) goto L_0x001a;
    L_0x0078:
        goto L_0x0001;
    L_0x0079:
        r10 = r11.workers;	 Catch:{ all -> 0x005c }
        r10.add(r6);	 Catch:{ all -> 0x005c }
        r10 = r11.workers;	 Catch:{ all -> 0x005c }
        r3 = r10.size();	 Catch:{ all -> 0x005c }
        r10 = r11.largestPoolSize;	 Catch:{ all -> 0x005c }
        if (r3 <= r10) goto L_0x008a;
    L_0x0088:
        r11.largestPoolSize = r3;	 Catch:{ all -> 0x005c }
    L_0x008a:
        r8 = 1;
    L_0x008b:
        r1.unlock();	 Catch:{ all -> 0x0061 }
        if (r8 == 0) goto L_0x0094;
    L_0x0090:
        r4.start();	 Catch:{ all -> 0x0061 }
        r9 = 1;
    L_0x0094:
        if (r9 != 0) goto L_0x0019;
    L_0x0096:
        r11.addWorkerFailed(r6);
        goto L_0x0019;
    L_0x009a:
        r10 = move-exception;
        goto L_0x0063;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ThreadPoolExecutor.addWorker(java.lang.Runnable, boolean):boolean");
    }

    private void addWorkerFailed(Worker w) {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        if (w != null) {
            try {
                this.workers.remove(w);
            } catch (Throwable th) {
                mainLock.unlock();
            }
        }
        decrementWorkerCount();
        tryTerminate();
        mainLock.unlock();
    }

    private void processWorkerExit(Worker w, boolean completedAbruptly) {
        if (completedAbruptly) {
            decrementWorkerCount();
        }
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            this.completedTaskCount += w.completedTasks;
            this.workers.remove(w);
            tryTerminate();
            int c = this.ctl.get();
            if (runStateLessThan(c, STOP)) {
                if (!completedAbruptly) {
                    int min = this.allowCoreThreadTimeOut ? SHUTDOWN : this.corePoolSize;
                    if (min == 0 && !this.workQueue.isEmpty()) {
                        min = 1;
                    }
                    if (workerCountOf(c) >= min) {
                        return;
                    }
                }
                addWorker(null, false);
            }
        } finally {
            mainLock.unlock();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.Runnable getTask() {
        /*
        r13 = this;
        r9 = 0;
        r8 = 1;
        r5 = 0;
    L_0x0003:
        r7 = r13.ctl;
        r0 = r7.get();
        r3 = runStateOf(r0);
        if (r3 < 0) goto L_0x0020;
    L_0x000f:
        r7 = 536870912; // 0x20000000 float:1.0842022E-19 double:2.652494739E-315;
        if (r3 >= r7) goto L_0x001b;
    L_0x0013:
        r7 = r13.workQueue;
        r7 = r7.isEmpty();
        if (r7 == 0) goto L_0x0020;
    L_0x001b:
        r13.decrementWorkerCount();
        r1 = r9;
    L_0x001f:
        return r1;
    L_0x0020:
        r6 = workerCountOf(r0);
        r7 = r13.allowCoreThreadTimeOut;
        if (r7 != 0) goto L_0x002c;
    L_0x0028:
        r7 = r13.corePoolSize;
        if (r6 <= r7) goto L_0x0047;
    L_0x002c:
        r4 = r8;
    L_0x002d:
        r7 = r13.maximumPoolSize;
        if (r6 > r7) goto L_0x0035;
    L_0x0031:
        if (r4 == 0) goto L_0x0049;
    L_0x0033:
        if (r5 == 0) goto L_0x0049;
    L_0x0035:
        if (r6 > r8) goto L_0x003f;
    L_0x0037:
        r7 = r13.workQueue;
        r7 = r7.isEmpty();
        if (r7 == 0) goto L_0x0049;
    L_0x003f:
        r7 = r13.compareAndDecrementWorkerCount(r0);
        if (r7 == 0) goto L_0x0003;
    L_0x0045:
        r1 = r9;
        goto L_0x001f;
    L_0x0047:
        r4 = 0;
        goto L_0x002d;
    L_0x0049:
        if (r4 == 0) goto L_0x005c;
    L_0x004b:
        r7 = r13.workQueue;	 Catch:{ InterruptedException -> 0x0066 }
        r10 = r13.keepAliveTime;	 Catch:{ InterruptedException -> 0x0066 }
        r12 = java.util.concurrent.TimeUnit.NANOSECONDS;	 Catch:{ InterruptedException -> 0x0066 }
        r7 = r7.poll(r10, r12);	 Catch:{ InterruptedException -> 0x0066 }
        r7 = (java.lang.Runnable) r7;	 Catch:{ InterruptedException -> 0x0066 }
        r1 = r7;
    L_0x0058:
        if (r1 != 0) goto L_0x001f;
    L_0x005a:
        r5 = 1;
        goto L_0x0003;
    L_0x005c:
        r7 = r13.workQueue;	 Catch:{ InterruptedException -> 0x0066 }
        r7 = r7.take();	 Catch:{ InterruptedException -> 0x0066 }
        r7 = (java.lang.Runnable) r7;	 Catch:{ InterruptedException -> 0x0066 }
        r1 = r7;
        goto L_0x0058;
    L_0x0066:
        r2 = move-exception;
        r5 = 0;
        goto L_0x0003;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ThreadPoolExecutor.getTask():java.lang.Runnable");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final void runWorker(java.util.concurrent.ThreadPoolExecutor.Worker r11) {
        /*
        r10 = this;
        r8 = 1;
        r3 = java.lang.Thread.currentThread();
        r1 = r11.firstTask;
        r5 = 0;
        r11.firstTask = r5;
        r11.unlock();
        r0 = 1;
    L_0x000f:
        if (r1 != 0) goto L_0x0017;
    L_0x0011:
        r1 = r10.getTask();	 Catch:{ all -> 0x0059 }
        if (r1 == 0) goto L_0x007c;
    L_0x0017:
        r11.lock();	 Catch:{ all -> 0x0059 }
        r5 = r10.ctl;	 Catch:{ all -> 0x0059 }
        r5 = r5.get();	 Catch:{ all -> 0x0059 }
        r6 = 536870912; // 0x20000000 float:1.0842022E-19 double:2.652494739E-315;
        r5 = runStateAtLeast(r5, r6);	 Catch:{ all -> 0x0059 }
        if (r5 != 0) goto L_0x003c;
    L_0x0028:
        r5 = java.lang.Thread.interrupted();	 Catch:{ all -> 0x0059 }
        if (r5 == 0) goto L_0x0045;
    L_0x002e:
        r5 = r10.ctl;	 Catch:{ all -> 0x0059 }
        r5 = r5.get();	 Catch:{ all -> 0x0059 }
        r6 = 536870912; // 0x20000000 float:1.0842022E-19 double:2.652494739E-315;
        r5 = runStateAtLeast(r5, r6);	 Catch:{ all -> 0x0059 }
        if (r5 == 0) goto L_0x0045;
    L_0x003c:
        r5 = r3.isInterrupted();	 Catch:{ all -> 0x0059 }
        if (r5 != 0) goto L_0x0045;
    L_0x0042:
        r3.interrupt();	 Catch:{ all -> 0x0059 }
    L_0x0045:
        r10.beforeExecute(r3, r1);	 Catch:{ all -> 0x0066 }
        r2 = 0;
        r1.run();	 Catch:{ RuntimeException -> 0x005e, Error -> 0x0071, Throwable -> 0x0074 }
        r10.afterExecute(r1, r2);	 Catch:{ all -> 0x0066 }
        r1 = 0;
        r6 = r11.completedTasks;	 Catch:{ all -> 0x0059 }
        r6 = r6 + r8;
        r11.completedTasks = r6;	 Catch:{ all -> 0x0059 }
        r11.unlock();	 Catch:{ all -> 0x0059 }
        goto L_0x000f;
    L_0x0059:
        r5 = move-exception;
        r10.processWorkerExit(r11, r0);
        throw r5;
    L_0x005e:
        r4 = move-exception;
        r2 = r4;
        throw r4;	 Catch:{ all -> 0x0061 }
    L_0x0061:
        r5 = move-exception;
        r10.afterExecute(r1, r2);	 Catch:{ all -> 0x0066 }
        throw r5;	 Catch:{ all -> 0x0066 }
    L_0x0066:
        r5 = move-exception;
        r1 = 0;
        r6 = r11.completedTasks;	 Catch:{ all -> 0x0059 }
        r6 = r6 + r8;
        r11.completedTasks = r6;	 Catch:{ all -> 0x0059 }
        r11.unlock();	 Catch:{ all -> 0x0059 }
        throw r5;	 Catch:{ all -> 0x0059 }
    L_0x0071:
        r4 = move-exception;
        r2 = r4;
        throw r4;	 Catch:{ all -> 0x0061 }
    L_0x0074:
        r4 = move-exception;
        r2 = r4;
        r5 = new java.lang.Error;	 Catch:{ all -> 0x0061 }
        r5.<init>(r4);	 Catch:{ all -> 0x0061 }
        throw r5;	 Catch:{ all -> 0x0061 }
    L_0x007c:
        r0 = 0;
        r10.processWorkerExit(r11, r0);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ThreadPoolExecutor.runWorker(java.util.concurrent.ThreadPoolExecutor$Worker):void");
    }

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, Executors.defaultThreadFactory(), defaultHandler);
    }

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, defaultHandler);
    }

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, Executors.defaultThreadFactory(), handler);
    }

    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        this.ctl = new AtomicInteger(ctlOf(RUNNING, SHUTDOWN));
        this.mainLock = new ReentrantLock();
        this.workers = new HashSet();
        this.termination = this.mainLock.newCondition();
        if (corePoolSize < 0 || maximumPoolSize <= 0 || maximumPoolSize < corePoolSize || keepAliveTime < 0) {
            throw new IllegalArgumentException();
        } else if (workQueue == null || threadFactory == null || handler == null) {
            throw new NullPointerException();
        } else {
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.workQueue = workQueue;
            this.keepAliveTime = unit.toNanos(keepAliveTime);
            this.threadFactory = threadFactory;
            this.handler = handler;
        }
    }

    public void execute(Runnable command) {
        if (command == null) {
            throw new NullPointerException();
        }
        int c = this.ctl.get();
        if (workerCountOf(c) < this.corePoolSize) {
            if (!addWorker(command, ONLY_ONE)) {
                c = this.ctl.get();
            } else {
                return;
            }
        }
        if (isRunning(c) && this.workQueue.offer(command)) {
            int recheck = this.ctl.get();
            if (!isRunning(recheck) && remove(command)) {
                reject(command);
            } else if (workerCountOf(recheck) == 0) {
                addWorker(null, false);
            }
        } else if (!addWorker(command, false)) {
            reject(command);
        }
    }

    public void shutdown() {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            checkShutdownAccess();
            advanceRunState(SHUTDOWN);
            interruptIdleWorkers();
            onShutdown();
            tryTerminate();
        } finally {
            mainLock.unlock();
        }
    }

    public List<Runnable> shutdownNow() {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            checkShutdownAccess();
            advanceRunState(STOP);
            interruptWorkers();
            List<Runnable> tasks = drainQueue();
            tryTerminate();
            return tasks;
        } finally {
            mainLock.unlock();
        }
    }

    public boolean isShutdown() {
        return !isRunning(this.ctl.get()) ? ONLY_ONE : false;
    }

    public boolean isTerminating() {
        int c = this.ctl.get();
        return (isRunning(c) || !runStateLessThan(c, TERMINATED)) ? false : ONLY_ONE;
    }

    public boolean isTerminated() {
        return runStateAtLeast(this.ctl.get(), TERMINATED);
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        while (!runStateAtLeast(this.ctl.get(), TERMINATED)) {
            if (nanos <= 0) {
                return false;
            }
            try {
                nanos = this.termination.awaitNanos(nanos);
            } finally {
                mainLock.unlock();
            }
        }
        mainLock.unlock();
        return ONLY_ONE;
    }

    protected void finalize() {
        shutdown();
    }

    public void setThreadFactory(ThreadFactory threadFactory) {
        if (threadFactory == null) {
            throw new NullPointerException();
        }
        this.threadFactory = threadFactory;
    }

    public ThreadFactory getThreadFactory() {
        return this.threadFactory;
    }

    public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
        if (handler == null) {
            throw new NullPointerException();
        }
        this.handler = handler;
    }

    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return this.handler;
    }

    public void setCorePoolSize(int corePoolSize) {
        if (corePoolSize < 0) {
            throw new IllegalArgumentException();
        }
        int delta = corePoolSize - this.corePoolSize;
        this.corePoolSize = corePoolSize;
        if (workerCountOf(this.ctl.get()) > corePoolSize) {
            interruptIdleWorkers();
        } else if (delta > 0) {
            int k = Math.min(delta, this.workQueue.size());
            while (true) {
                int k2 = k - 1;
                if (k > 0 && addWorker(null, ONLY_ONE) && !this.workQueue.isEmpty()) {
                    k = k2;
                } else {
                    return;
                }
            }
        }
    }

    public int getCorePoolSize() {
        return this.corePoolSize;
    }

    public boolean prestartCoreThread() {
        return (workerCountOf(this.ctl.get()) >= this.corePoolSize || !addWorker(null, ONLY_ONE)) ? false : ONLY_ONE;
    }

    void ensurePrestart() {
        int wc = workerCountOf(this.ctl.get());
        if (wc < this.corePoolSize) {
            addWorker(null, ONLY_ONE);
        } else if (wc == 0) {
            addWorker(null, false);
        }
    }

    public int prestartAllCoreThreads() {
        int n = SHUTDOWN;
        while (addWorker(null, ONLY_ONE)) {
            n++;
        }
        return n;
    }

    public boolean allowsCoreThreadTimeOut() {
        return this.allowCoreThreadTimeOut;
    }

    public void allowCoreThreadTimeOut(boolean value) {
        if (value && this.keepAliveTime <= 0) {
            throw new IllegalArgumentException("Core threads must have nonzero keep alive times");
        } else if (value != this.allowCoreThreadTimeOut) {
            this.allowCoreThreadTimeOut = value;
            if (value) {
                interruptIdleWorkers();
            }
        }
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        if (maximumPoolSize <= 0 || maximumPoolSize < this.corePoolSize) {
            throw new IllegalArgumentException();
        }
        this.maximumPoolSize = maximumPoolSize;
        if (workerCountOf(this.ctl.get()) > maximumPoolSize) {
            interruptIdleWorkers();
        }
    }

    public int getMaximumPoolSize() {
        return this.maximumPoolSize;
    }

    public void setKeepAliveTime(long time, TimeUnit unit) {
        if (time < 0) {
            throw new IllegalArgumentException();
        } else if (time == 0 && allowsCoreThreadTimeOut()) {
            throw new IllegalArgumentException("Core threads must have nonzero keep alive times");
        } else {
            long keepAliveTime = unit.toNanos(time);
            long delta = keepAliveTime - this.keepAliveTime;
            this.keepAliveTime = keepAliveTime;
            if (delta < 0) {
                interruptIdleWorkers();
            }
        }
    }

    public long getKeepAliveTime(TimeUnit unit) {
        return unit.convert(this.keepAliveTime, TimeUnit.NANOSECONDS);
    }

    public BlockingQueue<Runnable> getQueue() {
        return this.workQueue;
    }

    public boolean remove(Runnable task) {
        boolean removed = this.workQueue.remove(task);
        tryTerminate();
        return removed;
    }

    public void purge() {
        BlockingQueue<Runnable> q = this.workQueue;
        try {
            Iterator<Runnable> it = q.iterator();
            while (it.hasNext()) {
                Runnable r = (Runnable) it.next();
                if ((r instanceof Future) && ((Future) r).isCancelled()) {
                    it.remove();
                }
            }
        } catch (ConcurrentModificationException e) {
            Object[] arr$ = q.toArray();
            int len$ = arr$.length;
            for (int i$ = SHUTDOWN; i$ < len$; i$++) {
                Object r2 = arr$[i$];
                if ((r2 instanceof Future) && ((Future) r2).isCancelled()) {
                    q.remove(r2);
                }
            }
        }
        tryTerminate();
    }

    public int getPoolSize() {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            int size = runStateAtLeast(this.ctl.get(), TIDYING) ? SHUTDOWN : this.workers.size();
            mainLock.unlock();
            return size;
        } catch (Throwable th) {
            mainLock.unlock();
        }
    }

    public int getActiveCount() {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        int n = SHUTDOWN;
        try {
            Iterator i$ = this.workers.iterator();
            while (i$.hasNext()) {
                if (((Worker) i$.next()).isLocked()) {
                    n++;
                }
            }
            return n;
        } finally {
            mainLock.unlock();
        }
    }

    public int getLargestPoolSize() {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            int i = this.largestPoolSize;
            return i;
        } finally {
            mainLock.unlock();
        }
    }

    public long getTaskCount() {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            long n = this.completedTaskCount;
            Iterator i$ = this.workers.iterator();
            while (i$.hasNext()) {
                Worker w = (Worker) i$.next();
                n += w.completedTasks;
                if (w.isLocked()) {
                    n++;
                }
            }
            long size = ((long) this.workQueue.size()) + n;
            return size;
        } finally {
            mainLock.unlock();
        }
    }

    public long getCompletedTaskCount() {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            long n = this.completedTaskCount;
            Iterator i$ = this.workers.iterator();
            while (i$.hasNext()) {
                n += ((Worker) i$.next()).completedTasks;
            }
            return n;
        } finally {
            mainLock.unlock();
        }
    }

    public String toString() {
        ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            long ncompleted = this.completedTaskCount;
            int nactive = SHUTDOWN;
            int nworkers = this.workers.size();
            Iterator i$ = this.workers.iterator();
            while (i$.hasNext()) {
                Worker w = (Worker) i$.next();
                ncompleted += w.completedTasks;
                if (w.isLocked()) {
                    nactive++;
                }
            }
            int c = this.ctl.get();
            String rs = runStateLessThan(c, SHUTDOWN) ? "Running" : runStateAtLeast(c, TERMINATED) ? "Terminated" : "Shutting down";
            return super.toString() + "[" + rs + ", pool size = " + nworkers + ", active threads = " + nactive + ", queued tasks = " + this.workQueue.size() + ", completed tasks = " + ncompleted + "]";
        } finally {
            mainLock.unlock();
        }
    }

    protected void beforeExecute(Thread t, Runnable r) {
    }

    protected void afterExecute(Runnable r, Throwable t) {
    }

    protected void terminated() {
    }
}
