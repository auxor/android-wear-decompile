package android.database.sqlite;

import android.database.sqlite.SQLiteDebug.DbStats;
import android.hardware.SensorManager;
import android.os.CancellationSignal;
import android.os.CancellationSignal.OnCancelListener;
import android.os.OperationCanceledException;
import android.os.SystemClock;
import android.util.Log;
import android.util.PrefixPrinter;
import android.util.Printer;
import android.view.inputmethod.EditorInfo;
import dalvik.system.CloseGuard;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

public final class SQLiteConnectionPool implements Closeable {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static final int CONNECTION_FLAG_INTERACTIVE = 4;
    public static final int CONNECTION_FLAG_PRIMARY_CONNECTION_AFFINITY = 2;
    public static final int CONNECTION_FLAG_READ_ONLY = 1;
    private static final long CONNECTION_POOL_BUSY_MILLIS = 30000;
    private static final String TAG = "SQLiteConnectionPool";
    private final WeakHashMap<SQLiteConnection, AcquiredConnectionStatus> mAcquiredConnections;
    private final ArrayList<SQLiteConnection> mAvailableNonPrimaryConnections;
    private SQLiteConnection mAvailablePrimaryConnection;
    private final CloseGuard mCloseGuard;
    private final SQLiteDatabaseConfiguration mConfiguration;
    private final AtomicBoolean mConnectionLeaked;
    private ConnectionWaiter mConnectionWaiterPool;
    private ConnectionWaiter mConnectionWaiterQueue;
    private boolean mIsOpen;
    private final Object mLock;
    private int mMaxConnectionPoolSize;
    private int mNextConnectionId;

    /* renamed from: android.database.sqlite.SQLiteConnectionPool.1 */
    class AnonymousClass1 implements OnCancelListener {
        final /* synthetic */ SQLiteConnectionPool this$0;
        final /* synthetic */ int val$nonce;
        final /* synthetic */ ConnectionWaiter val$waiter;

        AnonymousClass1(android.database.sqlite.SQLiteConnectionPool r1, android.database.sqlite.SQLiteConnectionPool.ConnectionWaiter r2, int r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.database.sqlite.SQLiteConnectionPool.1.<init>(android.database.sqlite.SQLiteConnectionPool, android.database.sqlite.SQLiteConnectionPool$ConnectionWaiter, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.database.sqlite.SQLiteConnectionPool.1.<init>(android.database.sqlite.SQLiteConnectionPool, android.database.sqlite.SQLiteConnectionPool$ConnectionWaiter, int):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.database.sqlite.SQLiteConnectionPool.1.<init>(android.database.sqlite.SQLiteConnectionPool, android.database.sqlite.SQLiteConnectionPool$ConnectionWaiter, int):void");
        }

        public void onCancel() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.database.sqlite.SQLiteConnectionPool.1.onCancel():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.database.sqlite.SQLiteConnectionPool.1.onCancel():void
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
            throw new UnsupportedOperationException("Method not decompiled: android.database.sqlite.SQLiteConnectionPool.1.onCancel():void");
        }
    }

    enum AcquiredConnectionStatus {
        NORMAL,
        RECONFIGURE,
        DISCARD
    }

    private static final class ConnectionWaiter {
        public SQLiteConnection mAssignedConnection;
        public int mConnectionFlags;
        public RuntimeException mException;
        public ConnectionWaiter mNext;
        public int mNonce;
        public int mPriority;
        public String mSql;
        public long mStartTime;
        public Thread mThread;
        public boolean mWantPrimaryConnection;

        private ConnectionWaiter() {
        }

        /* synthetic */ ConnectionWaiter(AnonymousClass1 x0) {
            this();
        }
    }

    static {
        $assertionsDisabled = !SQLiteConnectionPool.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    private SQLiteConnectionPool(SQLiteDatabaseConfiguration configuration) {
        this.mCloseGuard = CloseGuard.get();
        this.mLock = new Object();
        this.mConnectionLeaked = new AtomicBoolean();
        this.mAvailableNonPrimaryConnections = new ArrayList();
        this.mAcquiredConnections = new WeakHashMap();
        this.mConfiguration = new SQLiteDatabaseConfiguration(configuration);
        setMaxConnectionPoolSizeLocked();
    }

    protected void finalize() throws Throwable {
        try {
            dispose(true);
        } finally {
            super.finalize();
        }
    }

    public static SQLiteConnectionPool open(SQLiteDatabaseConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("configuration must not be null.");
        }
        SQLiteConnectionPool pool = new SQLiteConnectionPool(configuration);
        pool.open();
        return pool;
    }

    private void open() {
        this.mAvailablePrimaryConnection = openConnectionLocked(this.mConfiguration, true);
        this.mIsOpen = true;
        this.mCloseGuard.open("close");
    }

    public void close() {
        dispose($assertionsDisabled);
    }

    private void dispose(boolean finalized) {
        if (this.mCloseGuard != null) {
            if (finalized) {
                this.mCloseGuard.warnIfOpen();
            }
            this.mCloseGuard.close();
        }
        if (!finalized) {
            synchronized (this.mLock) {
                throwIfClosedLocked();
                this.mIsOpen = $assertionsDisabled;
                closeAvailableConnectionsAndLogExceptionsLocked();
                int pendingCount = this.mAcquiredConnections.size();
                if (pendingCount != 0) {
                    Log.i(TAG, "The connection pool for " + this.mConfiguration.label + " has been closed but there are still " + pendingCount + " connections in use.  They will be closed " + "as they are released back to the pool.");
                }
                wakeConnectionWaitersLocked();
            }
        }
    }

    public void reconfigure(SQLiteDatabaseConfiguration configuration) {
        boolean foreignKeyModeChanged = true;
        if (configuration == null) {
            throw new IllegalArgumentException("configuration must not be null.");
        }
        synchronized (this.mLock) {
            throwIfClosedLocked();
            boolean walModeChanged = ((configuration.openFlags ^ this.mConfiguration.openFlags) & EditorInfo.IME_FLAG_NO_ACCESSORY_ACTION) != 0 ? true : $assertionsDisabled;
            if (walModeChanged) {
                if (this.mAcquiredConnections.isEmpty()) {
                    closeAvailableNonPrimaryConnectionsAndLogExceptionsLocked();
                    if (!($assertionsDisabled || this.mAvailableNonPrimaryConnections.isEmpty())) {
                        throw new AssertionError();
                    }
                }
                throw new IllegalStateException("Write Ahead Logging (WAL) mode cannot be enabled or disabled while there are transactions in progress.  Finish all transactions and release all active database connections first.");
            }
            if (configuration.foreignKeyConstraintsEnabled == this.mConfiguration.foreignKeyConstraintsEnabled) {
                foreignKeyModeChanged = $assertionsDisabled;
            }
            if (!foreignKeyModeChanged || this.mAcquiredConnections.isEmpty()) {
                if (this.mConfiguration.openFlags != configuration.openFlags) {
                    if (walModeChanged) {
                        closeAvailableConnectionsAndLogExceptionsLocked();
                    }
                    SQLiteConnection newPrimaryConnection = openConnectionLocked(configuration, true);
                    closeAvailableConnectionsAndLogExceptionsLocked();
                    discardAcquiredConnectionsLocked();
                    this.mAvailablePrimaryConnection = newPrimaryConnection;
                    this.mConfiguration.updateParametersFrom(configuration);
                    setMaxConnectionPoolSizeLocked();
                } else {
                    this.mConfiguration.updateParametersFrom(configuration);
                    setMaxConnectionPoolSizeLocked();
                    closeExcessConnectionsAndLogExceptionsLocked();
                    reconfigureAllConnectionsLocked();
                }
                wakeConnectionWaitersLocked();
            } else {
                throw new IllegalStateException("Foreign Key Constraints cannot be enabled or disabled while there are transactions in progress.  Finish all transactions and release all active database connections first.");
            }
        }
    }

    public SQLiteConnection acquireConnection(String sql, int connectionFlags, CancellationSignal cancellationSignal) {
        return waitForConnection(sql, connectionFlags, cancellationSignal);
    }

    public void releaseConnection(SQLiteConnection connection) {
        synchronized (this.mLock) {
            AcquiredConnectionStatus status = (AcquiredConnectionStatus) this.mAcquiredConnections.remove(connection);
            if (status == null) {
                throw new IllegalStateException("Cannot perform this operation because the specified connection was not acquired from this pool or has already been released.");
            }
            if (!this.mIsOpen) {
                closeConnectionAndLogExceptionsLocked(connection);
            } else if (connection.isPrimaryConnection()) {
                if (recycleConnectionLocked(connection, status)) {
                    if ($assertionsDisabled || this.mAvailablePrimaryConnection == null) {
                        this.mAvailablePrimaryConnection = connection;
                    } else {
                        throw new AssertionError();
                    }
                }
                wakeConnectionWaitersLocked();
            } else if (this.mAvailableNonPrimaryConnections.size() >= this.mMaxConnectionPoolSize - 1) {
                closeConnectionAndLogExceptionsLocked(connection);
            } else {
                if (recycleConnectionLocked(connection, status)) {
                    this.mAvailableNonPrimaryConnections.add(connection);
                }
                wakeConnectionWaitersLocked();
            }
        }
    }

    private boolean recycleConnectionLocked(SQLiteConnection connection, AcquiredConnectionStatus status) {
        if (status == AcquiredConnectionStatus.RECONFIGURE) {
            try {
                connection.reconfigure(this.mConfiguration);
            } catch (RuntimeException ex) {
                Log.e(TAG, "Failed to reconfigure released connection, closing it: " + connection, ex);
                status = AcquiredConnectionStatus.DISCARD;
            }
        }
        if (status != AcquiredConnectionStatus.DISCARD) {
            return true;
        }
        closeConnectionAndLogExceptionsLocked(connection);
        return $assertionsDisabled;
    }

    public boolean shouldYieldConnection(SQLiteConnection connection, int connectionFlags) {
        boolean isSessionBlockingImportantConnectionWaitersLocked;
        synchronized (this.mLock) {
            if (!this.mAcquiredConnections.containsKey(connection)) {
                throw new IllegalStateException("Cannot perform this operation because the specified connection was not acquired from this pool or has already been released.");
            } else if (this.mIsOpen) {
                isSessionBlockingImportantConnectionWaitersLocked = isSessionBlockingImportantConnectionWaitersLocked(connection.isPrimaryConnection(), connectionFlags);
            } else {
                isSessionBlockingImportantConnectionWaitersLocked = $assertionsDisabled;
            }
        }
        return isSessionBlockingImportantConnectionWaitersLocked;
    }

    public void collectDbStats(ArrayList<DbStats> dbStatsList) {
        synchronized (this.mLock) {
            if (this.mAvailablePrimaryConnection != null) {
                this.mAvailablePrimaryConnection.collectDbStats(dbStatsList);
            }
            Iterator i$ = this.mAvailableNonPrimaryConnections.iterator();
            while (i$.hasNext()) {
                ((SQLiteConnection) i$.next()).collectDbStats(dbStatsList);
            }
            for (SQLiteConnection connection : this.mAcquiredConnections.keySet()) {
                connection.collectDbStatsUnsafe(dbStatsList);
            }
        }
    }

    private SQLiteConnection openConnectionLocked(SQLiteDatabaseConfiguration configuration, boolean primaryConnection) {
        int connectionId = this.mNextConnectionId;
        this.mNextConnectionId = connectionId + CONNECTION_FLAG_READ_ONLY;
        return SQLiteConnection.open(this, configuration, connectionId, primaryConnection);
    }

    void onConnectionLeaked() {
        Log.w(TAG, "A SQLiteConnection object for database '" + this.mConfiguration.label + "' was leaked!  Please fix your application " + "to end transactions in progress properly and to close the database " + "when it is no longer needed.");
        this.mConnectionLeaked.set(true);
    }

    private void closeAvailableConnectionsAndLogExceptionsLocked() {
        closeAvailableNonPrimaryConnectionsAndLogExceptionsLocked();
        if (this.mAvailablePrimaryConnection != null) {
            closeConnectionAndLogExceptionsLocked(this.mAvailablePrimaryConnection);
            this.mAvailablePrimaryConnection = null;
        }
    }

    private void closeAvailableNonPrimaryConnectionsAndLogExceptionsLocked() {
        int count = this.mAvailableNonPrimaryConnections.size();
        for (int i = 0; i < count; i += CONNECTION_FLAG_READ_ONLY) {
            closeConnectionAndLogExceptionsLocked((SQLiteConnection) this.mAvailableNonPrimaryConnections.get(i));
        }
        this.mAvailableNonPrimaryConnections.clear();
    }

    private void closeExcessConnectionsAndLogExceptionsLocked() {
        int size = this.mAvailableNonPrimaryConnections.size();
        while (true) {
            int availableCount = size - 1;
            if (size > this.mMaxConnectionPoolSize - 1) {
                closeConnectionAndLogExceptionsLocked((SQLiteConnection) this.mAvailableNonPrimaryConnections.remove(availableCount));
                size = availableCount;
            } else {
                return;
            }
        }
    }

    private void closeConnectionAndLogExceptionsLocked(SQLiteConnection connection) {
        try {
            connection.close();
        } catch (RuntimeException ex) {
            Log.e(TAG, "Failed to close connection, its fate is now in the hands of the merciful GC: " + connection, ex);
        }
    }

    private void discardAcquiredConnectionsLocked() {
        markAcquiredConnectionsLocked(AcquiredConnectionStatus.DISCARD);
    }

    private void reconfigureAllConnectionsLocked() {
        if (this.mAvailablePrimaryConnection != null) {
            try {
                this.mAvailablePrimaryConnection.reconfigure(this.mConfiguration);
            } catch (RuntimeException ex) {
                Log.e(TAG, "Failed to reconfigure available primary connection, closing it: " + this.mAvailablePrimaryConnection, ex);
                closeConnectionAndLogExceptionsLocked(this.mAvailablePrimaryConnection);
                this.mAvailablePrimaryConnection = null;
            }
        }
        int count = this.mAvailableNonPrimaryConnections.size();
        int i = 0;
        while (i < count) {
            int i2;
            SQLiteConnection connection = (SQLiteConnection) this.mAvailableNonPrimaryConnections.get(i);
            try {
                connection.reconfigure(this.mConfiguration);
                i2 = i;
            } catch (RuntimeException ex2) {
                Log.e(TAG, "Failed to reconfigure available non-primary connection, closing it: " + connection, ex2);
                closeConnectionAndLogExceptionsLocked(connection);
                i2 = i - 1;
                this.mAvailableNonPrimaryConnections.remove(i);
                count--;
            }
            i = i2 + CONNECTION_FLAG_READ_ONLY;
        }
        markAcquiredConnectionsLocked(AcquiredConnectionStatus.RECONFIGURE);
    }

    private void markAcquiredConnectionsLocked(AcquiredConnectionStatus status) {
        if (!this.mAcquiredConnections.isEmpty()) {
            ArrayList<SQLiteConnection> keysToUpdate = new ArrayList(this.mAcquiredConnections.size());
            for (Entry<SQLiteConnection, AcquiredConnectionStatus> entry : this.mAcquiredConnections.entrySet()) {
                AcquiredConnectionStatus oldStatus = (AcquiredConnectionStatus) entry.getValue();
                if (!(status == oldStatus || oldStatus == AcquiredConnectionStatus.DISCARD)) {
                    keysToUpdate.add(entry.getKey());
                }
            }
            int updateCount = keysToUpdate.size();
            for (int i = 0; i < updateCount; i += CONNECTION_FLAG_READ_ONLY) {
                this.mAcquiredConnections.put(keysToUpdate.get(i), status);
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.database.sqlite.SQLiteConnection waitForConnection(java.lang.String r26, int r27, android.os.CancellationSignal r28) {
        /*
        r25 = this;
        r4 = r27 & 2;
        if (r4 == 0) goto L_0x0029;
    L_0x0004:
        r9 = 1;
    L_0x0005:
        r0 = r25;
        r0 = r0.mLock;
        r24 = r0;
        monitor-enter(r24);
        r25.throwIfClosedLocked();	 Catch:{ all -> 0x00da }
        if (r28 == 0) goto L_0x0014;
    L_0x0011:
        r28.throwIfCanceled();	 Catch:{ all -> 0x00da }
    L_0x0014:
        r14 = 0;
        if (r9 != 0) goto L_0x001b;
    L_0x0017:
        r14 = r25.tryAcquireNonPrimaryConnectionLocked(r26, r27);	 Catch:{ all -> 0x00da }
    L_0x001b:
        if (r14 != 0) goto L_0x0025;
    L_0x001d:
        r0 = r25;
        r1 = r27;
        r14 = r0.tryAcquirePrimaryConnectionLocked(r1);	 Catch:{ all -> 0x00da }
    L_0x0025:
        if (r14 == 0) goto L_0x002b;
    L_0x0027:
        monitor-exit(r24);	 Catch:{ all -> 0x00da }
    L_0x0028:
        return r14;
    L_0x0029:
        r9 = 0;
        goto L_0x0005;
    L_0x002b:
        r8 = getPriority(r27);	 Catch:{ all -> 0x00da }
        r6 = android.os.SystemClock.uptimeMillis();	 Catch:{ all -> 0x00da }
        r5 = java.lang.Thread.currentThread();	 Catch:{ all -> 0x00da }
        r4 = r25;
        r10 = r26;
        r11 = r27;
        r23 = r4.obtainConnectionWaiterLocked(r5, r6, r8, r9, r10, r11);	 Catch:{ all -> 0x00da }
        r19 = 0;
        r0 = r25;
        r0 = r0.mConnectionWaiterQueue;	 Catch:{ all -> 0x00da }
        r22 = r0;
    L_0x0049:
        if (r22 == 0) goto L_0x0057;
    L_0x004b:
        r0 = r22;
        r4 = r0.mPriority;	 Catch:{ all -> 0x00da }
        if (r8 <= r4) goto L_0x00c9;
    L_0x0051:
        r0 = r22;
        r1 = r23;
        r1.mNext = r0;	 Catch:{ all -> 0x00da }
    L_0x0057:
        if (r19 == 0) goto L_0x00d3;
    L_0x0059:
        r0 = r23;
        r1 = r19;
        r1.mNext = r0;	 Catch:{ all -> 0x00da }
    L_0x005f:
        r0 = r23;
        r0 = r0.mNonce;	 Catch:{ all -> 0x00da }
        r18 = r0;
        monitor-exit(r24);	 Catch:{ all -> 0x00da }
        if (r28 == 0) goto L_0x0078;
    L_0x0068:
        r4 = new android.database.sqlite.SQLiteConnectionPool$1;
        r0 = r25;
        r1 = r23;
        r2 = r18;
        r4.<init>(r0, r1, r2);
        r0 = r28;
        r0.setOnCancelListener(r4);
    L_0x0078:
        r12 = 30000; // 0x7530 float:4.2039E-41 double:1.4822E-319;
        r0 = r23;
        r4 = r0.mStartTime;	 Catch:{ all -> 0x00e0 }
        r16 = r4 + r12;
    L_0x0080:
        r0 = r25;
        r4 = r0.mConnectionLeaked;	 Catch:{ all -> 0x00e0 }
        r5 = 1;
        r10 = 0;
        r4 = r4.compareAndSet(r5, r10);	 Catch:{ all -> 0x00e0 }
        if (r4 == 0) goto L_0x0095;
    L_0x008c:
        r0 = r25;
        r5 = r0.mLock;	 Catch:{ all -> 0x00e0 }
        monitor-enter(r5);	 Catch:{ all -> 0x00e0 }
        r25.wakeConnectionWaitersLocked();	 Catch:{ all -> 0x00dd }
        monitor-exit(r5);	 Catch:{ all -> 0x00dd }
    L_0x0095:
        r4 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r4 = r4 * r12;
        r0 = r25;
        java.util.concurrent.locks.LockSupport.parkNanos(r0, r4);	 Catch:{ all -> 0x00e0 }
        java.lang.Thread.interrupted();	 Catch:{ all -> 0x00e0 }
        r0 = r25;
        r5 = r0.mLock;	 Catch:{ all -> 0x00e0 }
        monitor-enter(r5);	 Catch:{ all -> 0x00e0 }
        r25.throwIfClosedLocked();	 Catch:{ all -> 0x00eb }
        r0 = r23;
        r14 = r0.mAssignedConnection;	 Catch:{ all -> 0x00eb }
        r0 = r23;
        r15 = r0.mException;	 Catch:{ all -> 0x00eb }
        if (r14 != 0) goto L_0x00b5;
    L_0x00b3:
        if (r15 == 0) goto L_0x00ee;
    L_0x00b5:
        r0 = r25;
        r1 = r23;
        r0.recycleConnectionWaiterLocked(r1);	 Catch:{ all -> 0x00eb }
        if (r14 == 0) goto L_0x00ea;
    L_0x00be:
        monitor-exit(r5);	 Catch:{ all -> 0x00eb }
        if (r28 == 0) goto L_0x0028;
    L_0x00c1:
        r4 = 0;
        r0 = r28;
        r0.setOnCancelListener(r4);
        goto L_0x0028;
    L_0x00c9:
        r19 = r22;
        r0 = r22;
        r0 = r0.mNext;	 Catch:{ all -> 0x00da }
        r22 = r0;
        goto L_0x0049;
    L_0x00d3:
        r0 = r23;
        r1 = r25;
        r1.mConnectionWaiterQueue = r0;	 Catch:{ all -> 0x00da }
        goto L_0x005f;
    L_0x00da:
        r4 = move-exception;
        monitor-exit(r24);	 Catch:{ all -> 0x00da }
        throw r4;
    L_0x00dd:
        r4 = move-exception;
        monitor-exit(r5);	 Catch:{ all -> 0x00dd }
        throw r4;	 Catch:{ all -> 0x00e0 }
    L_0x00e0:
        r4 = move-exception;
        if (r28 == 0) goto L_0x00e9;
    L_0x00e3:
        r5 = 0;
        r0 = r28;
        r0.setOnCancelListener(r5);
    L_0x00e9:
        throw r4;
    L_0x00ea:
        throw r15;	 Catch:{ all -> 0x00eb }
    L_0x00eb:
        r4 = move-exception;
        monitor-exit(r5);	 Catch:{ all -> 0x00eb }
        throw r4;	 Catch:{ all -> 0x00e0 }
    L_0x00ee:
        r20 = android.os.SystemClock.uptimeMillis();	 Catch:{ all -> 0x00eb }
        r4 = (r20 > r16 ? 1 : (r20 == r16 ? 0 : -1));
        if (r4 >= 0) goto L_0x00fa;
    L_0x00f6:
        r12 = r20 - r16;
    L_0x00f8:
        monitor-exit(r5);	 Catch:{ all -> 0x00eb }
        goto L_0x0080;
    L_0x00fa:
        r0 = r23;
        r10 = r0.mStartTime;	 Catch:{ all -> 0x00eb }
        r10 = r20 - r10;
        r0 = r25;
        r1 = r27;
        r0.logConnectionPoolBusyLocked(r10, r1);	 Catch:{ all -> 0x00eb }
        r12 = 30000; // 0x7530 float:4.2039E-41 double:1.4822E-319;
        r16 = r20 + r12;
        goto L_0x00f8;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.database.sqlite.SQLiteConnectionPool.waitForConnection(java.lang.String, int, android.os.CancellationSignal):android.database.sqlite.SQLiteConnection");
    }

    private void cancelConnectionWaiterLocked(ConnectionWaiter waiter) {
        if (waiter.mAssignedConnection == null && waiter.mException == null) {
            ConnectionWaiter predecessor = null;
            ConnectionWaiter current = this.mConnectionWaiterQueue;
            while (current != waiter) {
                if ($assertionsDisabled || current != null) {
                    predecessor = current;
                    current = current.mNext;
                } else {
                    throw new AssertionError();
                }
            }
            if (predecessor != null) {
                predecessor.mNext = waiter.mNext;
            } else {
                this.mConnectionWaiterQueue = waiter.mNext;
            }
            waiter.mException = new OperationCanceledException();
            LockSupport.unpark(waiter.mThread);
            wakeConnectionWaitersLocked();
        }
    }

    private void logConnectionPoolBusyLocked(long waitMillis, int connectionFlags) {
        Iterator i$;
        Thread thread = Thread.currentThread();
        StringBuilder msg = new StringBuilder();
        msg.append("The connection pool for database '").append(this.mConfiguration.label);
        msg.append("' has been unable to grant a connection to thread ");
        msg.append(thread.getId()).append(" (").append(thread.getName()).append(") ");
        msg.append("with flags 0x").append(Integer.toHexString(connectionFlags));
        msg.append(" for ").append(((float) waitMillis) * SensorManager.LIGHT_NO_MOON).append(" seconds.\n");
        ArrayList<String> requests = new ArrayList();
        int activeConnections = 0;
        int idleConnections = 0;
        if (!this.mAcquiredConnections.isEmpty()) {
            for (SQLiteConnection connection : this.mAcquiredConnections.keySet()) {
                String description = connection.describeCurrentOperationUnsafe();
                if (description != null) {
                    requests.add(description);
                    activeConnections += CONNECTION_FLAG_READ_ONLY;
                } else {
                    idleConnections += CONNECTION_FLAG_READ_ONLY;
                }
            }
        }
        int availableConnections = this.mAvailableNonPrimaryConnections.size();
        if (this.mAvailablePrimaryConnection != null) {
            availableConnections += CONNECTION_FLAG_READ_ONLY;
        }
        msg.append("Connections: ").append(activeConnections).append(" active, ");
        msg.append(idleConnections).append(" idle, ");
        msg.append(availableConnections).append(" available.\n");
        if (!requests.isEmpty()) {
            msg.append("\nRequests in progress:\n");
            i$ = requests.iterator();
            while (i$.hasNext()) {
                msg.append("  ").append((String) i$.next()).append("\n");
            }
        }
        Log.w(TAG, msg.toString());
    }

    private void wakeConnectionWaitersLocked() {
        ConnectionWaiter predecessor = null;
        ConnectionWaiter waiter = this.mConnectionWaiterQueue;
        boolean primaryConnectionNotAvailable = $assertionsDisabled;
        boolean nonPrimaryConnectionNotAvailable = $assertionsDisabled;
        while (waiter != null) {
            boolean unpark = $assertionsDisabled;
            if (this.mIsOpen) {
                SQLiteConnection connection = null;
                try {
                    if (!(waiter.mWantPrimaryConnection || nonPrimaryConnectionNotAvailable)) {
                        connection = tryAcquireNonPrimaryConnectionLocked(waiter.mSql, waiter.mConnectionFlags);
                        if (connection == null) {
                            nonPrimaryConnectionNotAvailable = true;
                        }
                    }
                    if (connection == null && !primaryConnectionNotAvailable) {
                        connection = tryAcquirePrimaryConnectionLocked(waiter.mConnectionFlags);
                        if (connection == null) {
                            primaryConnectionNotAvailable = true;
                        }
                    }
                    if (connection != null) {
                        waiter.mAssignedConnection = connection;
                        unpark = true;
                    } else if (nonPrimaryConnectionNotAvailable && primaryConnectionNotAvailable) {
                        return;
                    }
                } catch (RuntimeException ex) {
                    waiter.mException = ex;
                    unpark = true;
                }
            } else {
                unpark = true;
            }
            ConnectionWaiter successor = waiter.mNext;
            if (unpark) {
                if (predecessor != null) {
                    predecessor.mNext = successor;
                } else {
                    this.mConnectionWaiterQueue = successor;
                }
                waiter.mNext = null;
                LockSupport.unpark(waiter.mThread);
            } else {
                predecessor = waiter;
            }
            waiter = successor;
        }
    }

    private SQLiteConnection tryAcquirePrimaryConnectionLocked(int connectionFlags) {
        SQLiteConnection connection = this.mAvailablePrimaryConnection;
        if (connection != null) {
            this.mAvailablePrimaryConnection = null;
            finishAcquireConnectionLocked(connection, connectionFlags);
            return connection;
        }
        for (SQLiteConnection acquiredConnection : this.mAcquiredConnections.keySet()) {
            if (acquiredConnection.isPrimaryConnection()) {
                return null;
            }
        }
        connection = openConnectionLocked(this.mConfiguration, true);
        finishAcquireConnectionLocked(connection, connectionFlags);
        return connection;
    }

    private SQLiteConnection tryAcquireNonPrimaryConnectionLocked(String sql, int connectionFlags) {
        SQLiteConnection connection;
        int availableCount = this.mAvailableNonPrimaryConnections.size();
        if (availableCount > CONNECTION_FLAG_READ_ONLY && sql != null) {
            for (int i = 0; i < availableCount; i += CONNECTION_FLAG_READ_ONLY) {
                connection = (SQLiteConnection) this.mAvailableNonPrimaryConnections.get(i);
                if (connection.isPreparedStatementInCache(sql)) {
                    this.mAvailableNonPrimaryConnections.remove(i);
                    finishAcquireConnectionLocked(connection, connectionFlags);
                    return connection;
                }
            }
        }
        if (availableCount > 0) {
            connection = (SQLiteConnection) this.mAvailableNonPrimaryConnections.remove(availableCount - 1);
            finishAcquireConnectionLocked(connection, connectionFlags);
            return connection;
        }
        int openConnections = this.mAcquiredConnections.size();
        if (this.mAvailablePrimaryConnection != null) {
            openConnections += CONNECTION_FLAG_READ_ONLY;
        }
        if (openConnections >= this.mMaxConnectionPoolSize) {
            return null;
        }
        connection = openConnectionLocked(this.mConfiguration, $assertionsDisabled);
        finishAcquireConnectionLocked(connection, connectionFlags);
        return connection;
    }

    private void finishAcquireConnectionLocked(SQLiteConnection connection, int connectionFlags) {
        try {
            connection.setOnlyAllowReadOnlyOperations((connectionFlags & CONNECTION_FLAG_READ_ONLY) != 0 ? true : $assertionsDisabled);
            this.mAcquiredConnections.put(connection, AcquiredConnectionStatus.NORMAL);
        } catch (RuntimeException ex) {
            Log.e(TAG, "Failed to prepare acquired connection for session, closing it: " + connection + ", connectionFlags=" + connectionFlags);
            closeConnectionAndLogExceptionsLocked(connection);
            throw ex;
        }
    }

    private boolean isSessionBlockingImportantConnectionWaitersLocked(boolean holdingPrimaryConnection, int connectionFlags) {
        ConnectionWaiter waiter = this.mConnectionWaiterQueue;
        if (waiter != null) {
            int priority = getPriority(connectionFlags);
            while (priority <= waiter.mPriority) {
                if (!holdingPrimaryConnection && waiter.mWantPrimaryConnection) {
                    waiter = waiter.mNext;
                    if (waiter == null) {
                        break;
                    }
                }
                return true;
            }
        }
        return $assertionsDisabled;
    }

    private static int getPriority(int connectionFlags) {
        return (connectionFlags & CONNECTION_FLAG_INTERACTIVE) != 0 ? CONNECTION_FLAG_READ_ONLY : 0;
    }

    private void setMaxConnectionPoolSizeLocked() {
        if ((this.mConfiguration.openFlags & EditorInfo.IME_FLAG_NO_ACCESSORY_ACTION) != 0) {
            this.mMaxConnectionPoolSize = SQLiteGlobal.getWALConnectionPoolSize();
        } else {
            this.mMaxConnectionPoolSize = CONNECTION_FLAG_READ_ONLY;
        }
    }

    private void throwIfClosedLocked() {
        if (!this.mIsOpen) {
            throw new IllegalStateException("Cannot perform this operation because the connection pool has been closed.");
        }
    }

    private ConnectionWaiter obtainConnectionWaiterLocked(Thread thread, long startTime, int priority, boolean wantPrimaryConnection, String sql, int connectionFlags) {
        ConnectionWaiter waiter = this.mConnectionWaiterPool;
        if (waiter != null) {
            this.mConnectionWaiterPool = waiter.mNext;
            waiter.mNext = null;
        } else {
            waiter = new ConnectionWaiter();
        }
        waiter.mThread = thread;
        waiter.mStartTime = startTime;
        waiter.mPriority = priority;
        waiter.mWantPrimaryConnection = wantPrimaryConnection;
        waiter.mSql = sql;
        waiter.mConnectionFlags = connectionFlags;
        return waiter;
    }

    private void recycleConnectionWaiterLocked(ConnectionWaiter waiter) {
        waiter.mNext = this.mConnectionWaiterPool;
        waiter.mThread = null;
        waiter.mSql = null;
        waiter.mAssignedConnection = null;
        waiter.mException = null;
        waiter.mNonce += CONNECTION_FLAG_READ_ONLY;
        this.mConnectionWaiterPool = waiter;
    }

    public void dump(Printer printer, boolean verbose) {
        Printer indentedPrinter = PrefixPrinter.create(printer, "    ");
        synchronized (this.mLock) {
            int i;
            printer.println("Connection pool for " + this.mConfiguration.path + ":");
            printer.println("  Open: " + this.mIsOpen);
            printer.println("  Max connections: " + this.mMaxConnectionPoolSize);
            printer.println("  Available primary connection:");
            if (this.mAvailablePrimaryConnection != null) {
                this.mAvailablePrimaryConnection.dump(indentedPrinter, verbose);
            } else {
                indentedPrinter.println("<none>");
            }
            printer.println("  Available non-primary connections:");
            if (this.mAvailableNonPrimaryConnections.isEmpty()) {
                indentedPrinter.println("<none>");
            } else {
                int count = this.mAvailableNonPrimaryConnections.size();
                for (i = 0; i < count; i += CONNECTION_FLAG_READ_ONLY) {
                    ((SQLiteConnection) this.mAvailableNonPrimaryConnections.get(i)).dump(indentedPrinter, verbose);
                }
            }
            printer.println("  Acquired connections:");
            if (this.mAcquiredConnections.isEmpty()) {
                indentedPrinter.println("<none>");
            } else {
                for (Entry<SQLiteConnection, AcquiredConnectionStatus> entry : this.mAcquiredConnections.entrySet()) {
                    ((SQLiteConnection) entry.getKey()).dumpUnsafe(indentedPrinter, verbose);
                    indentedPrinter.println("  Status: " + entry.getValue());
                }
            }
            printer.println("  Connection waiters:");
            if (this.mConnectionWaiterQueue != null) {
                i = 0;
                long now = SystemClock.uptimeMillis();
                ConnectionWaiter waiter = this.mConnectionWaiterQueue;
                while (waiter != null) {
                    indentedPrinter.println(i + ": waited for " + (((float) (now - waiter.mStartTime)) * SensorManager.LIGHT_NO_MOON) + " ms - thread=" + waiter.mThread + ", priority=" + waiter.mPriority + ", sql='" + waiter.mSql + "'");
                    waiter = waiter.mNext;
                    i += CONNECTION_FLAG_READ_ONLY;
                }
            } else {
                indentedPrinter.println("<none>");
            }
        }
    }

    public String toString() {
        return "SQLiteConnectionPool: " + this.mConfiguration.path;
    }
}
