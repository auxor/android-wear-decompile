package java.lang;

import dalvik.system.VMRuntime;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.ref.FinalizerReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import libcore.util.EmptyArray;

public final class Daemons {
    private static final long MAX_FINALIZE_NANOS = 10000000000L;
    private static final int NANOS_PER_MILLI = 1000000;
    private static final int NANOS_PER_SECOND = 1000000000;

    private static abstract class Daemon implements Runnable {
        private Thread thread;

        public abstract void run();

        private Daemon() {
        }

        public synchronized void start() {
            if (this.thread != null) {
                throw new IllegalStateException("already running");
            }
            this.thread = new Thread(ThreadGroup.systemThreadGroup, this, getClass().getSimpleName());
            this.thread.setDaemon(true);
            this.thread.start();
        }

        protected synchronized boolean isRunning() {
            return this.thread != null;
        }

        public synchronized void interrupt() {
            if (this.thread == null) {
                throw new IllegalStateException("not running");
            }
            this.thread.interrupt();
        }

        public void stop() {
            synchronized (this) {
                Thread threadToStop = this.thread;
                this.thread = null;
            }
            if (threadToStop == null) {
                throw new IllegalStateException("not running");
            }
            threadToStop.interrupt();
            while (true) {
                try {
                    threadToStop.join();
                    break;
                } catch (InterruptedException e) {
                }
            }
        }

        public synchronized StackTraceElement[] getStackTrace() {
            return this.thread != null ? this.thread.getStackTrace() : EmptyArray.STACK_TRACE_ELEMENT;
        }
    }

    private static class FinalizerDaemon extends Daemon {
        private static final FinalizerDaemon INSTANCE;
        private volatile Object finalizingObject;
        private volatile long finalizingStartedNanos;
        private final ReferenceQueue<Object> queue;

        private FinalizerDaemon() {
            super();
            this.queue = FinalizerReference.queue;
        }

        static {
            INSTANCE = new FinalizerDaemon();
        }

        public void run() {
            while (isRunning()) {
                try {
                    doFinalize((FinalizerReference) this.queue.remove());
                } catch (InterruptedException e) {
                }
            }
        }

        @FindBugsSuppressWarnings({"FI_EXPLICIT_INVOCATION"})
        private void doFinalize(FinalizerReference<?> reference) {
            FinalizerReference.remove(reference);
            Object object = reference.get();
            reference.clear();
            try {
                this.finalizingStartedNanos = System.nanoTime();
                this.finalizingObject = object;
                synchronized (FinalizerWatchdogDaemon.INSTANCE) {
                    FinalizerWatchdogDaemon.INSTANCE.notify();
                }
                object.finalize();
                this.finalizingObject = null;
            } catch (Throwable ex) {
                try {
                    System.logE("Uncaught exception thrown by finalizer", ex);
                } finally {
                    this.finalizingObject = null;
                }
            }
        }
    }

    private static class FinalizerWatchdogDaemon extends Daemon {
        private static final FinalizerWatchdogDaemon INSTANCE;

        private FinalizerWatchdogDaemon() {
            super();
        }

        static {
            INSTANCE = new FinalizerWatchdogDaemon();
        }

        public void run() {
            while (isRunning()) {
                if (!(!waitForObject() || waitForFinalization() || VMRuntime.getRuntime().isDebuggerActive())) {
                    Object finalizedObject = FinalizerDaemon.INSTANCE.finalizingObject;
                    if (finalizedObject != null) {
                        finalizerTimedOut(finalizedObject);
                        return;
                    }
                }
            }
        }

        private boolean waitForObject() {
            while (FinalizerDaemon.INSTANCE.finalizingObject == null) {
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        return false;
                    }
                }
            }
            return true;
        }

        private void sleepFor(long startNanos, long durationNanos) {
            while (true) {
                long sleepMills = (durationNanos - (System.nanoTime() - startNanos)) / 1000000;
                if (sleepMills > 0) {
                    try {
                        Thread.sleep(sleepMills);
                    } catch (InterruptedException e) {
                        if (!isRunning()) {
                            return;
                        }
                    }
                } else {
                    return;
                }
            }
        }

        private boolean waitForFinalization() {
            long startTime = FinalizerDaemon.INSTANCE.finalizingStartedNanos;
            sleepFor(startTime, Daemons.MAX_FINALIZE_NANOS);
            return FinalizerDaemon.INSTANCE.finalizingObject == null || FinalizerDaemon.INSTANCE.finalizingStartedNanos != startTime;
        }

        private static void finalizerTimedOut(Object object) {
            String message = object.getClass().getName() + ".finalize() timed out after " + 10 + " seconds";
            Exception syntheticException = new TimeoutException(message);
            syntheticException.setStackTrace(FinalizerDaemon.INSTANCE.getStackTrace());
            UncaughtExceptionHandler h = Thread.getDefaultUncaughtExceptionHandler();
            if (h == null) {
                System.logE(message, syntheticException);
                System.exit(2);
            }
            h.uncaughtException(Thread.currentThread(), syntheticException);
        }
    }

    private static class GCDaemon extends Daemon {
        private static final GCDaemon INSTANCE;
        private static final AtomicBoolean atomicBoolean;

        private GCDaemon() {
            super();
        }

        static {
            INSTANCE = new GCDaemon();
            atomicBoolean = new AtomicBoolean();
        }

        public void requestGC() {
            if (!atomicBoolean.getAndSet(true)) {
                synchronized (this) {
                    notify();
                }
                atomicBoolean.set(false);
            }
        }

        public void run() {
            while (isRunning()) {
                try {
                    synchronized (this) {
                        wait();
                    }
                    VMRuntime.getRuntime().concurrentGC();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private static class HeapTrimmerDaemon extends Daemon {
        private static final HeapTrimmerDaemon INSTANCE;

        private HeapTrimmerDaemon() {
            super();
        }

        static {
            INSTANCE = new HeapTrimmerDaemon();
        }

        public void run() {
            while (isRunning()) {
                try {
                    synchronized (this) {
                        wait();
                    }
                    VMRuntime.getRuntime().trimHeap();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private static class ReferenceQueueDaemon extends Daemon {
        private static final ReferenceQueueDaemon INSTANCE;

        private ReferenceQueueDaemon() {
            super();
        }

        static {
            INSTANCE = new ReferenceQueueDaemon();
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r4 = this;
        L_0x0000:
            r2 = r4.isRunning();
            if (r2 == 0) goto L_0x0022;
        L_0x0006:
            r3 = java.lang.ref.ReferenceQueue.class;
            monitor-enter(r3);	 Catch:{ InterruptedException -> 0x0016 }
        L_0x0009:
            r2 = java.lang.ref.ReferenceQueue.unenqueued;	 Catch:{ all -> 0x0013 }
            if (r2 != 0) goto L_0x0018;
        L_0x000d:
            r2 = java.lang.ref.ReferenceQueue.class;
            r2.wait();	 Catch:{ all -> 0x0013 }
            goto L_0x0009;
        L_0x0013:
            r2 = move-exception;
            monitor-exit(r3);	 Catch:{ all -> 0x0013 }
            throw r2;	 Catch:{ InterruptedException -> 0x0016 }
        L_0x0016:
            r0 = move-exception;
            goto L_0x0000;
        L_0x0018:
            r1 = java.lang.ref.ReferenceQueue.unenqueued;	 Catch:{ all -> 0x0013 }
            r2 = 0;
            java.lang.ref.ReferenceQueue.unenqueued = r2;	 Catch:{ all -> 0x0013 }
            monitor-exit(r3);	 Catch:{ all -> 0x0013 }
            r4.enqueue(r1);
            goto L_0x0000;
        L_0x0022:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: java.lang.Daemons.ReferenceQueueDaemon.run():void");
        }

        private void enqueue(Reference<?> list) {
            while (list != null) {
                Reference<?> reference;
                if (list == list.pendingNext) {
                    reference = list;
                    reference.pendingNext = null;
                    list = null;
                } else {
                    reference = list.pendingNext;
                    list.pendingNext = reference.pendingNext;
                    reference.pendingNext = null;
                }
                reference.enqueueInternal();
            }
        }
    }

    public static void start() {
        ReferenceQueueDaemon.INSTANCE.start();
        FinalizerDaemon.INSTANCE.start();
        FinalizerWatchdogDaemon.INSTANCE.start();
        HeapTrimmerDaemon.INSTANCE.start();
        GCDaemon.INSTANCE.start();
    }

    public static void stop() {
        ReferenceQueueDaemon.INSTANCE.stop();
        FinalizerDaemon.INSTANCE.stop();
        FinalizerWatchdogDaemon.INSTANCE.stop();
        HeapTrimmerDaemon.INSTANCE.stop();
        GCDaemon.INSTANCE.stop();
    }

    public static void requestHeapTrim() {
        synchronized (HeapTrimmerDaemon.INSTANCE) {
            HeapTrimmerDaemon.INSTANCE.notify();
        }
    }

    public static void requestGC() {
        GCDaemon.INSTANCE.requestGC();
    }
}
