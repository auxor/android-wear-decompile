package java.util;

import org.w3c.dom.traversal.NodeFilter;

public class Timer {
    private static long timerId;
    private final FinalizerHelper finalizer;
    private final TimerImpl impl;

    private static final class FinalizerHelper {
        private final TimerImpl impl;

        FinalizerHelper(TimerImpl impl) {
            this.impl = impl;
        }

        protected void finalize() throws Throwable {
            try {
                synchronized (this.impl) {
                    this.impl.finished = true;
                    this.impl.notify();
                }
            } finally {
                super.finalize();
            }
        }
    }

    private static final class TimerImpl extends Thread {
        private boolean cancelled;
        private boolean finished;
        private TimerHeap tasks;

        private static final class TimerHeap {
            private int DEFAULT_HEAP_SIZE;
            private int deletedCancelledNumber;
            private int size;
            private TimerTask[] timers;

            private TimerHeap() {
                this.DEFAULT_HEAP_SIZE = NodeFilter.SHOW_DOCUMENT;
                this.timers = new TimerTask[this.DEFAULT_HEAP_SIZE];
                this.size = 0;
                this.deletedCancelledNumber = 0;
            }

            public TimerTask minimum() {
                return this.timers[0];
            }

            public boolean isEmpty() {
                return this.size == 0;
            }

            public void insert(TimerTask task) {
                if (this.timers.length == this.size) {
                    Object appendedTimers = new TimerTask[(this.size * 2)];
                    System.arraycopy(this.timers, 0, appendedTimers, 0, this.size);
                    this.timers = appendedTimers;
                }
                TimerTask[] timerTaskArr = this.timers;
                int i = this.size;
                this.size = i + 1;
                timerTaskArr[i] = task;
                upHeap();
            }

            public void delete(int pos) {
                if (pos >= 0 && pos < this.size) {
                    TimerTask[] timerTaskArr = this.timers;
                    TimerTask[] timerTaskArr2 = this.timers;
                    int i = this.size - 1;
                    this.size = i;
                    timerTaskArr[pos] = timerTaskArr2[i];
                    this.timers[this.size] = null;
                    downHeap(pos);
                }
            }

            private void upHeap() {
                int current = this.size - 1;
                int parent = (current - 1) / 2;
                while (this.timers[current].when < this.timers[parent].when) {
                    TimerTask tmp = this.timers[current];
                    this.timers[current] = this.timers[parent];
                    this.timers[parent] = tmp;
                    current = parent;
                    parent = (current - 1) / 2;
                }
            }

            private void downHeap(int pos) {
                int current = pos;
                int child = (current * 2) + 1;
                while (child < this.size && this.size > 0) {
                    if (child + 1 < this.size && this.timers[child + 1].when < this.timers[child].when) {
                        child++;
                    }
                    if (this.timers[current].when >= this.timers[child].when) {
                        TimerTask tmp = this.timers[current];
                        this.timers[current] = this.timers[child];
                        this.timers[child] = tmp;
                        current = child;
                        child = (current * 2) + 1;
                    } else {
                        return;
                    }
                }
            }

            public void reset() {
                this.timers = new TimerTask[this.DEFAULT_HEAP_SIZE];
                this.size = 0;
            }

            public void adjustMinimum() {
                downHeap(0);
            }

            public void deleteIfCancelled() {
                int i = 0;
                while (i < this.size) {
                    if (this.timers[i].cancelled) {
                        this.deletedCancelledNumber++;
                        delete(i);
                        i--;
                    }
                    i++;
                }
            }

            private int getTask(TimerTask task) {
                for (int i = 0; i < this.timers.length; i++) {
                    if (this.timers[i] == task) {
                        return i;
                    }
                }
                return -1;
            }
        }

        TimerImpl(String name, boolean isDaemon) {
            this.tasks = new TimerHeap();
            setName(name);
            setDaemon(isDaemon);
            start();
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r14 = this;
        L_0x0000:
            monitor-enter(r14);
            r5 = r14.cancelled;	 Catch:{ all -> 0x0015 }
            if (r5 == 0) goto L_0x0007;
        L_0x0005:
            monitor-exit(r14);	 Catch:{ all -> 0x0015 }
        L_0x0006:
            return;
        L_0x0007:
            r5 = r14.tasks;	 Catch:{ all -> 0x0015 }
            r5 = r5.isEmpty();	 Catch:{ all -> 0x0015 }
            if (r5 == 0) goto L_0x001d;
        L_0x000f:
            r5 = r14.finished;	 Catch:{ all -> 0x0015 }
            if (r5 == 0) goto L_0x0018;
        L_0x0013:
            monitor-exit(r14);	 Catch:{ all -> 0x0015 }
            goto L_0x0006;
        L_0x0015:
            r5 = move-exception;
            monitor-exit(r14);	 Catch:{ all -> 0x0015 }
            throw r5;
        L_0x0018:
            r14.wait();	 Catch:{ InterruptedException -> 0x00c5 }
        L_0x001b:
            monitor-exit(r14);	 Catch:{ all -> 0x0015 }
            goto L_0x0000;
        L_0x001d:
            r0 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x0015 }
            r5 = r14.tasks;	 Catch:{ all -> 0x0015 }
            r3 = r5.minimum();	 Catch:{ all -> 0x0015 }
            r8 = r3.lock;	 Catch:{ all -> 0x0015 }
            monitor-enter(r8);	 Catch:{ all -> 0x0015 }
            r5 = r3.cancelled;	 Catch:{ all -> 0x0047 }
            if (r5 == 0) goto L_0x0037;
        L_0x002e:
            r5 = r14.tasks;	 Catch:{ all -> 0x0047 }
            r9 = 0;
            r5.delete(r9);	 Catch:{ all -> 0x0047 }
            monitor-exit(r8);	 Catch:{ all -> 0x0047 }
            monitor-exit(r14);	 Catch:{ all -> 0x0015 }
            goto L_0x0000;
        L_0x0037:
            r10 = r3.when;	 Catch:{ all -> 0x0047 }
            r6 = r10 - r0;
            monitor-exit(r8);	 Catch:{ all -> 0x0047 }
            r8 = 0;
            r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
            if (r5 <= 0) goto L_0x004a;
        L_0x0042:
            r14.wait(r6);	 Catch:{ InterruptedException -> 0x00c8 }
        L_0x0045:
            monitor-exit(r14);	 Catch:{ all -> 0x0015 }
            goto L_0x0000;
        L_0x0047:
            r5 = move-exception;
            monitor-exit(r8);	 Catch:{ all -> 0x0047 }
            throw r5;	 Catch:{ all -> 0x0015 }
        L_0x004a:
            r8 = r3.lock;	 Catch:{ all -> 0x0015 }
            monitor-enter(r8);	 Catch:{ all -> 0x0015 }
            r2 = 0;
            r5 = r14.tasks;	 Catch:{ all -> 0x00b1 }
            r5 = r5.minimum();	 Catch:{ all -> 0x00b1 }
            r10 = r5.when;	 Catch:{ all -> 0x00b1 }
            r12 = r3.when;	 Catch:{ all -> 0x00b1 }
            r5 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1));
            if (r5 == 0) goto L_0x0062;
        L_0x005c:
            r5 = r14.tasks;	 Catch:{ all -> 0x00b1 }
            r2 = r5.getTask(r3);	 Catch:{ all -> 0x00b1 }
        L_0x0062:
            r5 = r3.cancelled;	 Catch:{ all -> 0x00b1 }
            if (r5 == 0) goto L_0x0074;
        L_0x0066:
            r5 = r14.tasks;	 Catch:{ all -> 0x00b1 }
            r9 = r14.tasks;	 Catch:{ all -> 0x00b1 }
            r9 = r9.getTask(r3);	 Catch:{ all -> 0x00b1 }
            r5.delete(r9);	 Catch:{ all -> 0x00b1 }
            monitor-exit(r8);	 Catch:{ all -> 0x00b1 }
            monitor-exit(r14);	 Catch:{ all -> 0x0015 }
            goto L_0x0000;
        L_0x0074:
            r10 = r3.when;	 Catch:{ all -> 0x00b1 }
            r3.setScheduledTime(r10);	 Catch:{ all -> 0x00b1 }
            r5 = r14.tasks;	 Catch:{ all -> 0x00b1 }
            r5.delete(r2);	 Catch:{ all -> 0x00b1 }
            r10 = r3.period;	 Catch:{ all -> 0x00b1 }
            r12 = 0;
            r5 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1));
            if (r5 < 0) goto L_0x00b4;
        L_0x0086:
            r5 = r3.fixedRate;	 Catch:{ all -> 0x00b1 }
            if (r5 == 0) goto L_0x00a7;
        L_0x008a:
            r10 = r3.when;	 Catch:{ all -> 0x00b1 }
            r12 = r3.period;	 Catch:{ all -> 0x00b1 }
            r10 = r10 + r12;
            r3.when = r10;	 Catch:{ all -> 0x00b1 }
        L_0x0091:
            r14.insertTask(r3);	 Catch:{ all -> 0x00b1 }
        L_0x0094:
            monitor-exit(r8);	 Catch:{ all -> 0x00b1 }
            monitor-exit(r14);	 Catch:{ all -> 0x0015 }
            r4 = 0;
            r3.run();	 Catch:{ all -> 0x00b9 }
            r4 = 1;
            if (r4 != 0) goto L_0x0000;
        L_0x009d:
            monitor-enter(r14);
            r5 = 1;
            r14.cancelled = r5;	 Catch:{ all -> 0x00a4 }
            monitor-exit(r14);	 Catch:{ all -> 0x00a4 }
            goto L_0x0000;
        L_0x00a4:
            r5 = move-exception;
            monitor-exit(r14);	 Catch:{ all -> 0x00a4 }
            throw r5;
        L_0x00a7:
            r10 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x00b1 }
            r12 = r3.period;	 Catch:{ all -> 0x00b1 }
            r10 = r10 + r12;
            r3.when = r10;	 Catch:{ all -> 0x00b1 }
            goto L_0x0091;
        L_0x00b1:
            r5 = move-exception;
            monitor-exit(r8);	 Catch:{ all -> 0x00b1 }
            throw r5;	 Catch:{ all -> 0x0015 }
        L_0x00b4:
            r10 = 0;
            r3.when = r10;	 Catch:{ all -> 0x00b1 }
            goto L_0x0094;
        L_0x00b9:
            r5 = move-exception;
            if (r4 != 0) goto L_0x00c1;
        L_0x00bc:
            monitor-enter(r14);
            r8 = 1;
            r14.cancelled = r8;	 Catch:{ all -> 0x00c2 }
            monitor-exit(r14);	 Catch:{ all -> 0x00c2 }
        L_0x00c1:
            throw r5;
        L_0x00c2:
            r5 = move-exception;
            monitor-exit(r14);	 Catch:{ all -> 0x00c2 }
            throw r5;
        L_0x00c5:
            r5 = move-exception;
            goto L_0x001b;
        L_0x00c8:
            r5 = move-exception;
            goto L_0x0045;
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.Timer.TimerImpl.run():void");
        }

        private void insertTask(TimerTask newTask) {
            this.tasks.insert(newTask);
            notify();
        }

        public synchronized void cancel() {
            this.cancelled = true;
            this.tasks.reset();
            notify();
        }

        public int purge() {
            if (this.tasks.isEmpty()) {
                return 0;
            }
            this.tasks.deletedCancelledNumber = 0;
            this.tasks.deleteIfCancelled();
            return this.tasks.deletedCancelledNumber;
        }
    }

    private static synchronized long nextId() {
        long j;
        synchronized (Timer.class) {
            j = timerId;
            timerId = 1 + j;
        }
        return j;
    }

    public Timer(String name, boolean isDaemon) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        this.impl = new TimerImpl(name, isDaemon);
        this.finalizer = new FinalizerHelper(this.impl);
    }

    public Timer(String name) {
        this(name, false);
    }

    public Timer(boolean isDaemon) {
        this("Timer-" + nextId(), isDaemon);
    }

    public Timer() {
        this(false);
    }

    public void cancel() {
        this.impl.cancel();
    }

    public int purge() {
        int purge;
        synchronized (this.impl) {
            purge = this.impl.purge();
        }
        return purge;
    }

    public void schedule(TimerTask task, Date when) {
        long j = 0;
        if (when.getTime() < 0) {
            throw new IllegalArgumentException("when < 0: " + when.getTime());
        }
        long delay = when.getTime() - System.currentTimeMillis();
        if (delay >= 0) {
            j = delay;
        }
        scheduleImpl(task, j, -1, false);
    }

    public void schedule(TimerTask task, long delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("delay < 0: " + delay);
        }
        scheduleImpl(task, delay, -1, false);
    }

    public void schedule(TimerTask task, long delay, long period) {
        if (delay < 0 || period <= 0) {
            throw new IllegalArgumentException();
        }
        scheduleImpl(task, delay, period, false);
    }

    public void schedule(TimerTask task, Date when, long period) {
        long j = 0;
        if (period <= 0 || when.getTime() < 0) {
            throw new IllegalArgumentException();
        }
        long delay = when.getTime() - System.currentTimeMillis();
        if (delay >= 0) {
            j = delay;
        }
        scheduleImpl(task, j, period, false);
    }

    public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
        if (delay < 0 || period <= 0) {
            throw new IllegalArgumentException();
        }
        scheduleImpl(task, delay, period, true);
    }

    public void scheduleAtFixedRate(TimerTask task, Date when, long period) {
        if (period <= 0 || when.getTime() < 0) {
            throw new IllegalArgumentException();
        }
        scheduleImpl(task, when.getTime() - System.currentTimeMillis(), period, true);
    }

    private void scheduleImpl(TimerTask task, long delay, long period, boolean fixed) {
        synchronized (this.impl) {
            if (this.impl.cancelled) {
                throw new IllegalStateException("Timer was canceled");
            }
            long when = delay + System.currentTimeMillis();
            if (when < 0) {
                throw new IllegalArgumentException("Illegal delay to start the TimerTask: " + when);
            }
            synchronized (task.lock) {
                if (task.isScheduled()) {
                    throw new IllegalStateException("TimerTask is scheduled already");
                } else if (task.cancelled) {
                    throw new IllegalStateException("TimerTask is canceled");
                } else {
                    task.when = when;
                    task.period = period;
                    task.fixedRate = fixed;
                }
            }
            this.impl.insertTask(task);
        }
    }
}
