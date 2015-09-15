package java.lang;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import libcore.util.CollectionUtils;

public class ThreadGroup implements UncaughtExceptionHandler {
    static final ThreadGroup mainThreadGroup;
    static final ThreadGroup systemThreadGroup;
    private final List<ThreadGroup> groups;
    private boolean isDaemon;
    private boolean isDestroyed;
    private int maxPriority;
    private String name;
    final ThreadGroup parent;
    private final List<WeakReference<Thread>> threadRefs;
    private final Iterable<Thread> threads;

    static {
        systemThreadGroup = new ThreadGroup();
        mainThreadGroup = new ThreadGroup(systemThreadGroup, "main");
    }

    public ThreadGroup(String name) {
        this(Thread.currentThread().getThreadGroup(), name);
    }

    public ThreadGroup(ThreadGroup parent, String name) {
        this.maxPriority = 10;
        this.threadRefs = new ArrayList(5);
        this.threads = CollectionUtils.dereferenceIterable(this.threadRefs, true);
        this.groups = new ArrayList(3);
        if (parent == null) {
            throw new NullPointerException("parent == null");
        }
        this.name = name;
        this.parent = parent;
        if (parent != null) {
            parent.add(this);
            setMaxPriority(parent.getMaxPriority());
            if (parent.isDaemon()) {
                setDaemon(true);
            }
        }
    }

    private ThreadGroup() {
        this.maxPriority = 10;
        this.threadRefs = new ArrayList(5);
        this.threads = CollectionUtils.dereferenceIterable(this.threadRefs, true);
        this.groups = new ArrayList(3);
        this.name = "system";
        this.parent = null;
    }

    public int activeCount() {
        int count = 0;
        synchronized (this.threadRefs) {
            for (Thread thread : this.threads) {
                if (thread.isAlive()) {
                    count++;
                }
            }
        }
        synchronized (this.groups) {
            for (ThreadGroup group : this.groups) {
                count += group.activeCount();
            }
        }
        return count;
    }

    public int activeGroupCount() {
        int count = 0;
        synchronized (this.groups) {
            for (ThreadGroup group : this.groups) {
                count += group.activeGroupCount() + 1;
            }
        }
        return count;
    }

    private void add(ThreadGroup g) throws IllegalThreadStateException {
        synchronized (this.groups) {
            if (this.isDestroyed) {
                throw new IllegalThreadStateException();
            }
            this.groups.add(g);
        }
    }

    @Deprecated
    public boolean allowThreadSuspension(boolean b) {
        return true;
    }

    public final void checkAccess() {
    }

    public final void destroy() {
        synchronized (this.threadRefs) {
            synchronized (this.groups) {
                if (this.isDestroyed) {
                    String str;
                    StringBuilder append = new StringBuilder().append("Thread group was already destroyed: ");
                    if (this.name != null) {
                        str = this.name;
                    } else {
                        str = "n/a";
                    }
                    throw new IllegalThreadStateException(append.append(str).toString());
                } else if (this.threads.iterator().hasNext()) {
                    throw new IllegalThreadStateException("Thread group still contains threads: " + (this.name != null ? this.name : "n/a"));
                } else {
                    while (!this.groups.isEmpty()) {
                        ((ThreadGroup) this.groups.get(0)).destroy();
                    }
                    if (this.parent != null) {
                        this.parent.remove(this);
                    }
                    this.isDestroyed = true;
                }
            }
        }
    }

    private void destroyIfEmptyDaemon() {
        synchronized (this.threadRefs) {
            if (!(!this.isDaemon || this.isDestroyed || this.threads.iterator().hasNext())) {
                synchronized (this.groups) {
                    if (this.groups.isEmpty()) {
                        destroy();
                    }
                }
            }
        }
    }

    public int enumerate(Thread[] threads) {
        return enumerate(threads, true);
    }

    public int enumerate(Thread[] threads, boolean recurse) {
        return enumerateGeneric(threads, recurse, 0, true);
    }

    public int enumerate(ThreadGroup[] groups) {
        return enumerate(groups, true);
    }

    public int enumerate(ThreadGroup[] groups, boolean recurse) {
        return enumerateGeneric(groups, recurse, 0, false);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int enumerateGeneric(java.lang.Object[] r8, boolean r9, int r10, boolean r11) {
        /*
        r7 = this;
        if (r11 == 0) goto L_0x0056;
    L_0x0002:
        r6 = r7.threadRefs;
        monitor-enter(r6);
        r5 = r7.threadRefs;	 Catch:{ all -> 0x0053 }
        r5 = r5.size();	 Catch:{ all -> 0x0053 }
        r2 = r5 + -1;
        r0 = r10;
    L_0x000e:
        if (r2 < 0) goto L_0x0034;
    L_0x0010:
        r5 = r7.threadRefs;	 Catch:{ all -> 0x008c }
        r5 = r5.get(r2);	 Catch:{ all -> 0x008c }
        r5 = (java.lang.ref.WeakReference) r5;	 Catch:{ all -> 0x008c }
        r4 = r5.get();	 Catch:{ all -> 0x008c }
        r4 = (java.lang.Thread) r4;	 Catch:{ all -> 0x008c }
        if (r4 == 0) goto L_0x008f;
    L_0x0020:
        r5 = r4.isAlive();	 Catch:{ all -> 0x008c }
        if (r5 == 0) goto L_0x008f;
    L_0x0026:
        r5 = r8.length;	 Catch:{ all -> 0x008c }
        if (r0 < r5) goto L_0x002c;
    L_0x0029:
        monitor-exit(r6);	 Catch:{ all -> 0x008c }
        r10 = r0;
    L_0x002b:
        return r0;
    L_0x002c:
        r10 = r0 + 1;
        r8[r0] = r4;	 Catch:{ all -> 0x0053 }
    L_0x0030:
        r2 = r2 + -1;
        r0 = r10;
        goto L_0x000e;
    L_0x0034:
        monitor-exit(r6);	 Catch:{ all -> 0x008c }
        r10 = r0;
    L_0x0036:
        if (r9 == 0) goto L_0x0084;
    L_0x0038:
        r6 = r7.groups;
        monitor-enter(r6);
        r5 = r7.groups;	 Catch:{ all -> 0x0086 }
        r3 = r5.iterator();	 Catch:{ all -> 0x0086 }
    L_0x0041:
        r5 = r3.hasNext();	 Catch:{ all -> 0x0086 }
        if (r5 == 0) goto L_0x0083;
    L_0x0047:
        r1 = r3.next();	 Catch:{ all -> 0x0086 }
        r1 = (java.lang.ThreadGroup) r1;	 Catch:{ all -> 0x0086 }
        r5 = r8.length;	 Catch:{ all -> 0x0086 }
        if (r10 < r5) goto L_0x007e;
    L_0x0050:
        monitor-exit(r6);	 Catch:{ all -> 0x0086 }
        r0 = r10;
        goto L_0x002b;
    L_0x0053:
        r5 = move-exception;
    L_0x0054:
        monitor-exit(r6);	 Catch:{ all -> 0x0053 }
        throw r5;
    L_0x0056:
        r6 = r7.groups;
        monitor-enter(r6);
        r5 = r7.groups;	 Catch:{ all -> 0x007b }
        r5 = r5.size();	 Catch:{ all -> 0x007b }
        r2 = r5 + -1;
        r0 = r10;
    L_0x0062:
        if (r2 < 0) goto L_0x0078;
    L_0x0064:
        r5 = r8.length;	 Catch:{ all -> 0x0089 }
        if (r0 < r5) goto L_0x006a;
    L_0x0067:
        monitor-exit(r6);	 Catch:{ all -> 0x0089 }
        r10 = r0;
        goto L_0x002b;
    L_0x006a:
        r10 = r0 + 1;
        r5 = r7.groups;	 Catch:{ all -> 0x007b }
        r5 = r5.get(r2);	 Catch:{ all -> 0x007b }
        r8[r0] = r5;	 Catch:{ all -> 0x007b }
        r2 = r2 + -1;
        r0 = r10;
        goto L_0x0062;
    L_0x0078:
        monitor-exit(r6);	 Catch:{ all -> 0x0089 }
        r10 = r0;
        goto L_0x0036;
    L_0x007b:
        r5 = move-exception;
    L_0x007c:
        monitor-exit(r6);	 Catch:{ all -> 0x007b }
        throw r5;
    L_0x007e:
        r10 = r1.enumerateGeneric(r8, r9, r10, r11);	 Catch:{ all -> 0x0086 }
        goto L_0x0041;
    L_0x0083:
        monitor-exit(r6);	 Catch:{ all -> 0x0086 }
    L_0x0084:
        r0 = r10;
        goto L_0x002b;
    L_0x0086:
        r5 = move-exception;
        monitor-exit(r6);	 Catch:{ all -> 0x0086 }
        throw r5;
    L_0x0089:
        r5 = move-exception;
        r10 = r0;
        goto L_0x007c;
    L_0x008c:
        r5 = move-exception;
        r10 = r0;
        goto L_0x0054;
    L_0x008f:
        r10 = r0;
        goto L_0x0030;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.lang.ThreadGroup.enumerateGeneric(java.lang.Object[], boolean, int, boolean):int");
    }

    public final int getMaxPriority() {
        return this.maxPriority;
    }

    public final String getName() {
        return this.name;
    }

    public final ThreadGroup getParent() {
        return this.parent;
    }

    public final void interrupt() {
        synchronized (this.threadRefs) {
            for (Thread thread : this.threads) {
                thread.interrupt();
            }
        }
        synchronized (this.groups) {
            for (ThreadGroup group : this.groups) {
                group.interrupt();
            }
        }
    }

    public final boolean isDaemon() {
        return this.isDaemon;
    }

    public synchronized boolean isDestroyed() {
        return this.isDestroyed;
    }

    public void list() {
        System.out.println();
        list(0);
    }

    private void list(int levels) {
        indent(levels);
        System.out.println(toString());
        levels++;
        synchronized (this.threadRefs) {
            for (Object thread : this.threads) {
                indent(levels);
                System.out.println(thread);
            }
        }
        synchronized (this.groups) {
            for (ThreadGroup group : this.groups) {
                group.list(levels);
            }
        }
    }

    private void indent(int levels) {
        for (int i = 0; i < levels; i++) {
            System.out.print("    ");
        }
    }

    public final boolean parentOf(ThreadGroup g) {
        while (g != null) {
            if (this == g) {
                return true;
            }
            g = g.parent;
        }
        return false;
    }

    private void remove(ThreadGroup g) {
        synchronized (this.groups) {
            Iterator<ThreadGroup> i = this.groups.iterator();
            while (i.hasNext()) {
                if (((ThreadGroup) i.next()).equals(g)) {
                    i.remove();
                    break;
                }
            }
        }
        destroyIfEmptyDaemon();
    }

    @Deprecated
    public final void resume() {
        synchronized (this.threadRefs) {
            for (Thread thread : this.threads) {
                thread.resume();
            }
        }
        synchronized (this.groups) {
            for (ThreadGroup group : this.groups) {
                group.resume();
            }
        }
    }

    public final void setDaemon(boolean isDaemon) {
        this.isDaemon = isDaemon;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void setMaxPriority(int r6) {
        /*
        r5 = this;
        r3 = r5.maxPriority;
        if (r6 > r3) goto L_0x0037;
    L_0x0004:
        r3 = 1;
        if (r6 >= r3) goto L_0x0008;
    L_0x0007:
        r6 = 1;
    L_0x0008:
        r3 = r5.parent;
        if (r3 != 0) goto L_0x002d;
    L_0x000c:
        r2 = r6;
    L_0x000d:
        if (r2 > r6) goto L_0x0034;
    L_0x000f:
        r5.maxPriority = r2;
        r4 = r5.groups;
        monitor-enter(r4);
        r3 = r5.groups;	 Catch:{ all -> 0x002a }
        r1 = r3.iterator();	 Catch:{ all -> 0x002a }
    L_0x001a:
        r3 = r1.hasNext();	 Catch:{ all -> 0x002a }
        if (r3 == 0) goto L_0x0036;
    L_0x0020:
        r0 = r1.next();	 Catch:{ all -> 0x002a }
        r0 = (java.lang.ThreadGroup) r0;	 Catch:{ all -> 0x002a }
        r0.setMaxPriority(r6);	 Catch:{ all -> 0x002a }
        goto L_0x001a;
    L_0x002a:
        r3 = move-exception;
        monitor-exit(r4);	 Catch:{ all -> 0x002a }
        throw r3;
    L_0x002d:
        r3 = r5.parent;
        r2 = r3.getMaxPriority();
        goto L_0x000d;
    L_0x0034:
        r2 = r6;
        goto L_0x000f;
    L_0x0036:
        monitor-exit(r4);	 Catch:{ all -> 0x002a }
    L_0x0037:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.lang.ThreadGroup.setMaxPriority(int):void");
    }

    @Deprecated
    public final void stop() {
        if (stopHelper()) {
            Thread.currentThread().stop();
        }
    }

    private boolean stopHelper() {
        boolean stopCurrent = false;
        synchronized (this.threadRefs) {
            Thread current = Thread.currentThread();
            for (Thread thread : this.threads) {
                if (thread == current) {
                    stopCurrent = true;
                } else {
                    thread.stop();
                }
            }
        }
        synchronized (this.groups) {
            for (ThreadGroup group : this.groups) {
                stopCurrent |= group.stopHelper();
            }
        }
        return stopCurrent;
    }

    @Deprecated
    public final void suspend() {
        if (suspendHelper()) {
            Thread.currentThread().suspend();
        }
    }

    private boolean suspendHelper() {
        boolean suspendCurrent = false;
        synchronized (this.threadRefs) {
            Thread current = Thread.currentThread();
            for (Thread thread : this.threads) {
                if (thread == current) {
                    suspendCurrent = true;
                } else {
                    thread.suspend();
                }
            }
        }
        synchronized (this.groups) {
            for (ThreadGroup group : this.groups) {
                suspendCurrent |= group.suspendHelper();
            }
        }
        return suspendCurrent;
    }

    public String toString() {
        return getClass().getName() + "[name=" + getName() + ",maxPriority=" + getMaxPriority() + "]";
    }

    public void uncaughtException(Thread t, Throwable e) {
        if (this.parent != null) {
            this.parent.uncaughtException(t, e);
        } else if (Thread.getDefaultUncaughtExceptionHandler() != null) {
            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(t, e);
        } else if (!(e instanceof ThreadDeath)) {
            e.printStackTrace(System.err);
        }
    }

    final void addThread(Thread thread) throws IllegalThreadStateException {
        synchronized (this.threadRefs) {
            if (this.isDestroyed) {
                throw new IllegalThreadStateException();
            }
            this.threadRefs.add(new WeakReference(thread));
        }
    }

    final void removeThread(Thread thread) throws IllegalThreadStateException {
        synchronized (this.threadRefs) {
            Iterator<Thread> i = this.threads.iterator();
            while (i.hasNext()) {
                if (((Thread) i.next()).equals(thread)) {
                    i.remove();
                    break;
                }
            }
        }
        destroyIfEmptyDaemon();
    }
}
