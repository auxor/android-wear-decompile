package com.android.okhttp;

import com.android.okhttp.internal.Platform;
import com.android.okhttp.internal.Util;
import java.io.Closeable;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ConnectionPool {
    private static final long DEFAULT_KEEP_ALIVE_DURATION_MS = 300000;
    private static final int MAX_CONNECTIONS_TO_CLEANUP = 2;
    private static final ConnectionPool systemDefault = null;
    private CleanMode cleanMode;
    private final LinkedList<Connection> connections;
    private final Runnable connectionsCleanupRunnable;
    private final Runnable drainModeRunnable;
    private final ExecutorService executorService;
    private final long keepAliveDurationNs;
    private final int maxIdleConnections;

    /* renamed from: com.android.okhttp.ConnectionPool.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ ConnectionPool this$0;

        AnonymousClass2(com.android.okhttp.ConnectionPool r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.okhttp.ConnectionPool.2.<init>(com.android.okhttp.ConnectionPool):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.okhttp.ConnectionPool.2.<init>(com.android.okhttp.ConnectionPool):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.okhttp.ConnectionPool.2.<init>(com.android.okhttp.ConnectionPool):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.okhttp.ConnectionPool.2.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.okhttp.ConnectionPool.2.run():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.okhttp.ConnectionPool.2.run():void");
        }
    }

    /* renamed from: com.android.okhttp.ConnectionPool.3 */
    class AnonymousClass3 implements Runnable {
        final /* synthetic */ ConnectionPool this$0;

        AnonymousClass3(com.android.okhttp.ConnectionPool r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.okhttp.ConnectionPool.3.<init>(com.android.okhttp.ConnectionPool):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.okhttp.ConnectionPool.3.<init>(com.android.okhttp.ConnectionPool):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.okhttp.ConnectionPool.3.<init>(com.android.okhttp.ConnectionPool):void");
        }

        public void run() {
        }
    }

    /* renamed from: com.android.okhttp.ConnectionPool.4 */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$com$squareup$okhttp$ConnectionPool$CleanMode = null;

        static {
            $SwitchMap$com$squareup$okhttp$ConnectionPool$CleanMode = new int[CleanMode.values().length];
            try {
                $SwitchMap$com$squareup$okhttp$ConnectionPool$CleanMode[CleanMode.NORMAL.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$squareup$okhttp$ConnectionPool$CleanMode[CleanMode.DRAINING.ordinal()] = ConnectionPool.MAX_CONNECTIONS_TO_CLEANUP;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$squareup$okhttp$ConnectionPool$CleanMode[CleanMode.DRAINED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private enum CleanMode {
        NORMAL,
        DRAINING,
        DRAINED
    }

    static {
        String keepAlive = System.getProperty("http.keepAlive");
        String keepAliveDuration = System.getProperty("http.keepAliveDuration");
        String maxIdleConnections = System.getProperty("http.maxConnections");
        long keepAliveDurationMs = keepAliveDuration != null ? Long.parseLong(keepAliveDuration) : DEFAULT_KEEP_ALIVE_DURATION_MS;
        if (keepAlive != null && !Boolean.parseBoolean(keepAlive)) {
            systemDefault = new ConnectionPool(0, keepAliveDurationMs);
        } else if (maxIdleConnections != null) {
            systemDefault = new ConnectionPool(Integer.parseInt(maxIdleConnections), keepAliveDurationMs);
        } else {
            systemDefault = new ConnectionPool(5, keepAliveDurationMs);
        }
    }

    public ConnectionPool(int maxIdleConnections, long keepAliveDurationMs) {
        this.connections = new LinkedList();
        this.executorService = new ThreadPoolExecutor(0, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue(), Util.threadFactory("OkHttp ConnectionPool", true));
        this.cleanMode = CleanMode.NORMAL;
        this.drainModeRunnable = new Runnable() {
            public void run() {
                ConnectionPool.this.connectionsCleanupRunnable.run();
                synchronized (ConnectionPool.this) {
                    if (ConnectionPool.this.connections.size() > 0) {
                        try {
                            ConnectionPool.this.wait(ConnectionPool.this.keepAliveDurationNs / 1000000);
                        } catch (InterruptedException e) {
                        }
                        ConnectionPool.this.executorService.execute(this);
                    } else {
                        ConnectionPool.this.cleanMode = CleanMode.DRAINED;
                    }
                }
            }
        };
        this.connectionsCleanupRunnable = new AnonymousClass2(this);
        this.maxIdleConnections = maxIdleConnections;
        this.keepAliveDurationNs = (keepAliveDurationMs * 1000) * 1000;
    }

    List<Connection> getConnections() {
        List arrayList;
        waitForCleanupCallableToRun();
        synchronized (this) {
            arrayList = new ArrayList(this.connections);
        }
        return arrayList;
    }

    private void waitForCleanupCallableToRun() {
        try {
            this.executorService.submit(new AnonymousClass3(this)).get();
        } catch (Exception e) {
            throw new AssertionError();
        }
    }

    public static ConnectionPool getDefault() {
        return systemDefault;
    }

    public synchronized int getConnectionCount() {
        return this.connections.size();
    }

    public synchronized int getSpdyConnectionCount() {
        int total;
        total = 0;
        Iterator i$ = this.connections.iterator();
        while (i$.hasNext()) {
            if (((Connection) i$.next()).isSpdy()) {
                total++;
            }
        }
        return total;
    }

    public synchronized int getHttpConnectionCount() {
        int total;
        total = 0;
        Iterator i$ = this.connections.iterator();
        while (i$.hasNext()) {
            if (!((Connection) i$.next()).isSpdy()) {
                total++;
            }
        }
        return total;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized com.android.okhttp.Connection get(com.android.okhttp.Address r9) {
        /*
        r8 = this;
        monitor-enter(r8);
        r2 = 0;
        r4 = r8.connections;	 Catch:{ all -> 0x0083 }
        r5 = r8.connections;	 Catch:{ all -> 0x0083 }
        r5 = r5.size();	 Catch:{ all -> 0x0083 }
        r3 = r4.listIterator(r5);	 Catch:{ all -> 0x0083 }
    L_0x000e:
        r4 = r3.hasPrevious();	 Catch:{ all -> 0x0083 }
        if (r4 == 0) goto L_0x0052;
    L_0x0014:
        r0 = r3.previous();	 Catch:{ all -> 0x0083 }
        r0 = (com.android.okhttp.Connection) r0;	 Catch:{ all -> 0x0083 }
        r4 = r0.getRoute();	 Catch:{ all -> 0x0083 }
        r4 = r4.getAddress();	 Catch:{ all -> 0x0083 }
        r4 = r4.equals(r9);	 Catch:{ all -> 0x0083 }
        if (r4 == 0) goto L_0x000e;
    L_0x0028:
        r4 = r0.isAlive();	 Catch:{ all -> 0x0083 }
        if (r4 == 0) goto L_0x000e;
    L_0x002e:
        r4 = java.lang.System.nanoTime();	 Catch:{ all -> 0x0083 }
        r6 = r0.getIdleStartTimeNs();	 Catch:{ all -> 0x0083 }
        r4 = r4 - r6;
        r6 = r8.keepAliveDurationNs;	 Catch:{ all -> 0x0083 }
        r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r4 >= 0) goto L_0x000e;
    L_0x003d:
        r3.remove();	 Catch:{ all -> 0x0083 }
        r4 = r0.isSpdy();	 Catch:{ all -> 0x0083 }
        if (r4 != 0) goto L_0x0051;
    L_0x0046:
        r4 = com.android.okhttp.internal.Platform.get();	 Catch:{ SocketException -> 0x0064 }
        r5 = r0.getSocket();	 Catch:{ SocketException -> 0x0064 }
        r4.tagSocket(r5);	 Catch:{ SocketException -> 0x0064 }
    L_0x0051:
        r2 = r0;
    L_0x0052:
        if (r2 == 0) goto L_0x005f;
    L_0x0054:
        r4 = r2.isSpdy();	 Catch:{ all -> 0x0083 }
        if (r4 == 0) goto L_0x005f;
    L_0x005a:
        r4 = r8.connections;	 Catch:{ all -> 0x0083 }
        r4.addFirst(r2);	 Catch:{ all -> 0x0083 }
    L_0x005f:
        r8.scheduleCleanupAsRequired();	 Catch:{ all -> 0x0083 }
        monitor-exit(r8);
        return r2;
    L_0x0064:
        r1 = move-exception;
        com.android.okhttp.internal.Util.closeQuietly(r0);	 Catch:{ all -> 0x0083 }
        r4 = com.android.okhttp.internal.Platform.get();	 Catch:{ all -> 0x0083 }
        r5 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0083 }
        r5.<init>();	 Catch:{ all -> 0x0083 }
        r6 = "Unable to tagSocket(): ";
        r5 = r5.append(r6);	 Catch:{ all -> 0x0083 }
        r5 = r5.append(r1);	 Catch:{ all -> 0x0083 }
        r5 = r5.toString();	 Catch:{ all -> 0x0083 }
        r4.logW(r5);	 Catch:{ all -> 0x0083 }
        goto L_0x000e;
    L_0x0083:
        r4 = move-exception;
        monitor-exit(r8);
        throw r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.okhttp.ConnectionPool.get(com.android.okhttp.Address):com.android.okhttp.Connection");
    }

    public void recycle(Connection connection) {
        if (connection.isSpdy() || !connection.clearOwner()) {
            return;
        }
        if (connection.isAlive()) {
            try {
                Platform.get().untagSocket(connection.getSocket());
                synchronized (this) {
                    this.connections.addFirst(connection);
                    connection.incrementRecycleCount();
                    connection.resetIdleStartTime();
                    scheduleCleanupAsRequired();
                }
                return;
            } catch (SocketException e) {
                Platform.get().logW("Unable to untagSocket(): " + e);
                Util.closeQuietly((Closeable) connection);
                return;
            }
        }
        Util.closeQuietly((Closeable) connection);
    }

    public void share(Connection connection) {
        if (!connection.isSpdy()) {
            throw new IllegalArgumentException();
        } else if (connection.isAlive()) {
            synchronized (this) {
                this.connections.addFirst(connection);
                scheduleCleanupAsRequired();
            }
        }
    }

    public void evictAll() {
        List<Connection> connections;
        synchronized (this) {
            connections = new ArrayList(this.connections);
            this.connections.clear();
        }
        int size = connections.size();
        for (int i = 0; i < size; i++) {
            Util.closeQuietly((Closeable) connections.get(i));
        }
    }

    public void enterDrainMode() {
        synchronized (this) {
            this.cleanMode = CleanMode.DRAINING;
            this.executorService.execute(this.drainModeRunnable);
        }
    }

    public boolean isDrained() {
        boolean z;
        synchronized (this) {
            z = this.cleanMode == CleanMode.DRAINED;
        }
        return z;
    }

    private void scheduleCleanupAsRequired() {
        switch (AnonymousClass4.$SwitchMap$com$squareup$okhttp$ConnectionPool$CleanMode[this.cleanMode.ordinal()]) {
            case 1:
                this.executorService.execute(this.connectionsCleanupRunnable);
            case 3:
                this.cleanMode = CleanMode.DRAINING;
                this.executorService.execute(this.drainModeRunnable);
            default:
        }
    }
}
