package java.util.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractExecutorService implements ExecutorService {
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !AbstractExecutorService.class.desiredAssertionStatus();
    }

    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new FutureTask(runnable, value);
    }

    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new FutureTask(callable);
    }

    public Future<?> submit(Runnable task) {
        if (task == null) {
            throw new NullPointerException();
        }
        RunnableFuture<Void> ftask = newTaskFor(task, null);
        execute(ftask);
        return ftask;
    }

    public <T> Future<T> submit(Runnable task, T result) {
        if (task == null) {
            throw new NullPointerException();
        }
        RunnableFuture<T> ftask = newTaskFor(task, result);
        execute(ftask);
        return ftask;
    }

    public <T> Future<T> submit(Callable<T> task) {
        if (task == null) {
            throw new NullPointerException();
        }
        RunnableFuture<T> ftask = newTaskFor(task);
        execute(ftask);
        return ftask;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private <T> T doInvokeAny(java.util.Collection<? extends java.util.concurrent.Callable<T>> r22, boolean r23, long r24) throws java.lang.InterruptedException, java.util.concurrent.ExecutionException, java.util.concurrent.TimeoutException {
        /*
        r21 = this;
        if (r22 != 0) goto L_0x0008;
    L_0x0002:
        r18 = new java.lang.NullPointerException;
        r18.<init>();
        throw r18;
    L_0x0008:
        r15 = r22.size();
        if (r15 != 0) goto L_0x0014;
    L_0x000e:
        r18 = new java.lang.IllegalArgumentException;
        r18.<init>();
        throw r18;
    L_0x0014:
        r12 = new java.util.ArrayList;
        r12.<init>(r15);
        r5 = new java.util.concurrent.ExecutorCompletionService;
        r0 = r21;
        r5.<init>(r0);
        r8 = 0;
        if (r23 == 0) goto L_0x0082;
    L_0x0023:
        r18 = java.lang.System.nanoTime();	 Catch:{ all -> 0x008f }
        r6 = r18 + r24;
    L_0x0029:
        r14 = r22.iterator();	 Catch:{ all -> 0x008f }
        r18 = r14.next();	 Catch:{ all -> 0x008f }
        r18 = (java.util.concurrent.Callable) r18;	 Catch:{ all -> 0x008f }
        r0 = r18;
        r18 = r5.submit(r0);	 Catch:{ all -> 0x008f }
        r0 = r18;
        r12.add(r0);	 Catch:{ all -> 0x008f }
        r15 = r15 + -1;
        r4 = 1;
        r9 = r8;
    L_0x0042:
        r11 = r5.poll();	 Catch:{ all -> 0x00c1 }
        if (r11 != 0) goto L_0x005f;
    L_0x0048:
        if (r15 <= 0) goto L_0x0085;
    L_0x004a:
        r15 = r15 + -1;
        r18 = r14.next();	 Catch:{ all -> 0x00c1 }
        r18 = (java.util.concurrent.Callable) r18;	 Catch:{ all -> 0x00c1 }
        r0 = r18;
        r18 = r5.submit(r0);	 Catch:{ all -> 0x00c1 }
        r0 = r18;
        r12.add(r0);	 Catch:{ all -> 0x00c1 }
        r4 = r4 + 1;
    L_0x005f:
        if (r11 == 0) goto L_0x00e4;
    L_0x0061:
        r4 = r4 + -1;
        r19 = r11.get();	 Catch:{ ExecutionException -> 0x00d2, RuntimeException -> 0x00d7 }
        r13 = 0;
        r17 = r12.size();
    L_0x006c:
        r0 = r17;
        if (r13 >= r0) goto L_0x00e1;
    L_0x0070:
        r18 = r12.get(r13);
        r18 = (java.util.concurrent.Future) r18;
        r20 = 1;
        r0 = r18;
        r1 = r20;
        r0.cancel(r1);
        r13 = r13 + 1;
        goto L_0x006c;
    L_0x0082:
        r6 = 0;
        goto L_0x0029;
    L_0x0085:
        if (r4 != 0) goto L_0x00ad;
    L_0x0087:
        if (r9 != 0) goto L_0x00e2;
    L_0x0089:
        r8 = new java.util.concurrent.ExecutionException;	 Catch:{ all -> 0x00c1 }
        r8.<init>();	 Catch:{ all -> 0x00c1 }
    L_0x008e:
        throw r8;	 Catch:{ all -> 0x008f }
    L_0x008f:
        r18 = move-exception;
        r19 = r18;
    L_0x0092:
        r13 = 0;
        r17 = r12.size();
    L_0x0097:
        r0 = r17;
        if (r13 >= r0) goto L_0x00e0;
    L_0x009b:
        r18 = r12.get(r13);
        r18 = (java.util.concurrent.Future) r18;
        r20 = 1;
        r0 = r18;
        r1 = r20;
        r0.cancel(r1);
        r13 = r13 + 1;
        goto L_0x0097;
    L_0x00ad:
        if (r23 == 0) goto L_0x00cd;
    L_0x00af:
        r18 = java.util.concurrent.TimeUnit.NANOSECONDS;	 Catch:{ all -> 0x00c1 }
        r0 = r24;
        r2 = r18;
        r11 = r5.poll(r0, r2);	 Catch:{ all -> 0x00c1 }
        if (r11 != 0) goto L_0x00c6;
    L_0x00bb:
        r18 = new java.util.concurrent.TimeoutException;	 Catch:{ all -> 0x00c1 }
        r18.<init>();	 Catch:{ all -> 0x00c1 }
        throw r18;	 Catch:{ all -> 0x00c1 }
    L_0x00c1:
        r18 = move-exception;
        r19 = r18;
        r8 = r9;
        goto L_0x0092;
    L_0x00c6:
        r18 = java.lang.System.nanoTime();	 Catch:{ all -> 0x00c1 }
        r24 = r6 - r18;
        goto L_0x005f;
    L_0x00cd:
        r11 = r5.take();	 Catch:{ all -> 0x00c1 }
        goto L_0x005f;
    L_0x00d2:
        r10 = move-exception;
        r8 = r10;
    L_0x00d4:
        r9 = r8;
        goto L_0x0042;
    L_0x00d7:
        r16 = move-exception;
        r8 = new java.util.concurrent.ExecutionException;	 Catch:{ all -> 0x00c1 }
        r0 = r16;
        r8.<init>(r0);	 Catch:{ all -> 0x00c1 }
        goto L_0x00d4;
    L_0x00e0:
        throw r19;
    L_0x00e1:
        return r19;
    L_0x00e2:
        r8 = r9;
        goto L_0x008e;
    L_0x00e4:
        r8 = r9;
        goto L_0x00d4;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.AbstractExecutorService.doInvokeAny(java.util.Collection, boolean, long):T");
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        try {
            return doInvokeAny(tasks, false, 0);
        } catch (TimeoutException e) {
            if ($assertionsDisabled) {
                return null;
            }
            throw new AssertionError();
        }
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return doInvokeAny(tasks, true, unit.toNanos(timeout));
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        int i;
        if (tasks == null) {
            throw new NullPointerException();
        }
        ArrayList<Future<T>> futures = new ArrayList(tasks.size());
        int size;
        try {
            for (Callable<T> t : tasks) {
                RunnableFuture<T> f = newTaskFor(t);
                futures.add(f);
                execute(f);
            }
            size = futures.size();
            for (i = 0; i < size; i++) {
                Future<T> f2 = (Future) futures.get(i);
                if (!f2.isDone()) {
                    try {
                        f2.get();
                    } catch (CancellationException e) {
                    } catch (ExecutionException e2) {
                    }
                }
            }
            if (!true) {
                size = futures.size();
                for (i = 0; i < size; i++) {
                    ((Future) futures.get(i)).cancel(true);
                }
            }
            return futures;
        } catch (Throwable th) {
            Throwable th2 = th;
            if (!false) {
                size = futures.size();
                for (i = 0; i < size; i++) {
                    ((Future) futures.get(i)).cancel(true);
                }
            }
        }
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        if (tasks == null) {
            throw new NullPointerException();
        }
        int i;
        long nanos = unit.toNanos(timeout);
        ArrayList<Future<T>> futures = new ArrayList(tasks.size());
        for (Callable<T> t : tasks) {
            futures.add(newTaskFor(t));
        }
        long deadline = System.nanoTime() + nanos;
        int size = futures.size();
        for (i = 0; i < size; i++) {
            execute((Runnable) futures.get(i));
            nanos = deadline - System.nanoTime();
            if (nanos <= 0) {
                if (null == null) {
                    size = futures.size();
                    for (i = 0; i < size; i++) {
                        ((Future) futures.get(i)).cancel(true);
                    }
                }
                return futures;
            }
        }
        i = 0;
        while (i < size) {
            try {
                Future<T> f = (Future) futures.get(i);
                if (!f.isDone()) {
                    if (nanos <= 0) {
                        if (null == null) {
                            size = futures.size();
                            for (i = 0; i < size; i++) {
                                ((Future) futures.get(i)).cancel(true);
                            }
                        }
                        return futures;
                    }
                    try {
                        f.get(nanos, TimeUnit.NANOSECONDS);
                    } catch (CancellationException e) {
                    } catch (ExecutionException e2) {
                    } catch (TimeoutException e3) {
                        if (null == null) {
                            size = futures.size();
                            for (i = 0; i < size; i++) {
                                ((Future) futures.get(i)).cancel(true);
                            }
                        }
                    }
                    nanos = deadline - System.nanoTime();
                }
                i++;
            } catch (Throwable th) {
                Throwable th2 = th;
                if (null == null) {
                    size = futures.size();
                    for (i = 0; i < size; i++) {
                        ((Future) futures.get(i)).cancel(true);
                    }
                }
            }
        }
        if (!true) {
            size = futures.size();
            for (i = 0; i < size; i++) {
                ((Future) futures.get(i)).cancel(true);
            }
        }
        return futures;
    }
}
