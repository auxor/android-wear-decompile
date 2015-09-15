package android.database.sqlite;

import android.database.CursorWindow;
import android.database.DatabaseUtils;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;

public final class SQLiteSession {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static final int TRANSACTION_MODE_DEFERRED = 0;
    public static final int TRANSACTION_MODE_EXCLUSIVE = 2;
    public static final int TRANSACTION_MODE_IMMEDIATE = 1;
    private SQLiteConnection mConnection;
    private int mConnectionFlags;
    private final SQLiteConnectionPool mConnectionPool;
    private int mConnectionUseCount;
    private Transaction mTransactionPool;
    private Transaction mTransactionStack;

    private static final class Transaction {
        public boolean mChildFailed;
        public SQLiteTransactionListener mListener;
        public boolean mMarkedSuccessful;
        public int mMode;
        public Transaction mParent;

        private Transaction() {
        }
    }

    static {
        $assertionsDisabled = !SQLiteSession.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    public SQLiteSession(SQLiteConnectionPool connectionPool) {
        if (connectionPool == null) {
            throw new IllegalArgumentException("connectionPool must not be null");
        }
        this.mConnectionPool = connectionPool;
    }

    public boolean hasTransaction() {
        return this.mTransactionStack != null ? true : $assertionsDisabled;
    }

    public boolean hasNestedTransaction() {
        return (this.mTransactionStack == null || this.mTransactionStack.mParent == null) ? $assertionsDisabled : true;
    }

    public boolean hasConnection() {
        return this.mConnection != null ? true : $assertionsDisabled;
    }

    public void beginTransaction(int transactionMode, SQLiteTransactionListener transactionListener, int connectionFlags, CancellationSignal cancellationSignal) {
        throwIfTransactionMarkedSuccessful();
        beginTransactionUnchecked(transactionMode, transactionListener, connectionFlags, cancellationSignal);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void beginTransactionUnchecked(int r6, android.database.sqlite.SQLiteTransactionListener r7, int r8, android.os.CancellationSignal r9) {
        /*
        r5 = this;
        r3 = 0;
        if (r9 == 0) goto L_0x0006;
    L_0x0003:
        r9.throwIfCanceled();
    L_0x0006:
        r2 = r5.mTransactionStack;
        if (r2 != 0) goto L_0x000d;
    L_0x000a:
        r5.acquireConnection(r3, r8, r9);
    L_0x000d:
        r2 = r5.mTransactionStack;	 Catch:{ all -> 0x003c }
        if (r2 != 0) goto L_0x001c;
    L_0x0011:
        switch(r6) {
            case 1: goto L_0x0033;
            case 2: goto L_0x0045;
            default: goto L_0x0014;
        };	 Catch:{ all -> 0x003c }
    L_0x0014:
        r2 = r5.mConnection;	 Catch:{ all -> 0x003c }
        r3 = "BEGIN;";
        r4 = 0;
        r2.execute(r3, r4, r9);	 Catch:{ all -> 0x003c }
    L_0x001c:
        if (r7 == 0) goto L_0x0021;
    L_0x001e:
        r7.onBegin();	 Catch:{ RuntimeException -> 0x004e }
    L_0x0021:
        r1 = r5.obtainTransaction(r6, r7);	 Catch:{ all -> 0x003c }
        r2 = r5.mTransactionStack;	 Catch:{ all -> 0x003c }
        r1.mParent = r2;	 Catch:{ all -> 0x003c }
        r5.mTransactionStack = r1;	 Catch:{ all -> 0x003c }
        r2 = r5.mTransactionStack;
        if (r2 != 0) goto L_0x0032;
    L_0x002f:
        r5.releaseConnection();
    L_0x0032:
        return;
    L_0x0033:
        r2 = r5.mConnection;	 Catch:{ all -> 0x003c }
        r3 = "BEGIN IMMEDIATE;";
        r4 = 0;
        r2.execute(r3, r4, r9);	 Catch:{ all -> 0x003c }
        goto L_0x001c;
    L_0x003c:
        r2 = move-exception;
        r3 = r5.mTransactionStack;
        if (r3 != 0) goto L_0x0044;
    L_0x0041:
        r5.releaseConnection();
    L_0x0044:
        throw r2;
    L_0x0045:
        r2 = r5.mConnection;	 Catch:{ all -> 0x003c }
        r3 = "BEGIN EXCLUSIVE;";
        r4 = 0;
        r2.execute(r3, r4, r9);	 Catch:{ all -> 0x003c }
        goto L_0x001c;
    L_0x004e:
        r0 = move-exception;
        r2 = r5.mTransactionStack;	 Catch:{ all -> 0x003c }
        if (r2 != 0) goto L_0x005b;
    L_0x0053:
        r2 = r5.mConnection;	 Catch:{ all -> 0x003c }
        r3 = "ROLLBACK;";
        r4 = 0;
        r2.execute(r3, r4, r9);	 Catch:{ all -> 0x003c }
    L_0x005b:
        throw r0;	 Catch:{ all -> 0x003c }
        */
        throw new UnsupportedOperationException("Method not decompiled: android.database.sqlite.SQLiteSession.beginTransactionUnchecked(int, android.database.sqlite.SQLiteTransactionListener, int, android.os.CancellationSignal):void");
    }

    public void setTransactionSuccessful() {
        throwIfNoTransaction();
        throwIfTransactionMarkedSuccessful();
        this.mTransactionStack.mMarkedSuccessful = true;
    }

    public void endTransaction(CancellationSignal cancellationSignal) {
        throwIfNoTransaction();
        if ($assertionsDisabled || this.mConnection != null) {
            endTransactionUnchecked(cancellationSignal, $assertionsDisabled);
            return;
        }
        throw new AssertionError();
    }

    private void endTransactionUnchecked(CancellationSignal cancellationSignal, boolean yielding) {
        if (cancellationSignal != null) {
            cancellationSignal.throwIfCanceled();
        }
        Transaction top = this.mTransactionStack;
        boolean successful = ((top.mMarkedSuccessful || yielding) && !top.mChildFailed) ? true : $assertionsDisabled;
        RuntimeException listenerException = null;
        SQLiteTransactionListener listener = top.mListener;
        if (listener != null) {
            if (successful) {
                try {
                    listener.onCommit();
                } catch (RuntimeException ex) {
                    listenerException = ex;
                    successful = $assertionsDisabled;
                }
            } else {
                listener.onRollback();
            }
        }
        this.mTransactionStack = top.mParent;
        recycleTransaction(top);
        if (this.mTransactionStack == null) {
            if (successful) {
                try {
                    this.mConnection.execute("COMMIT;", null, cancellationSignal);
                } catch (Throwable th) {
                    releaseConnection();
                }
            } else {
                this.mConnection.execute("ROLLBACK;", null, cancellationSignal);
            }
            releaseConnection();
        } else if (!successful) {
            this.mTransactionStack.mChildFailed = true;
        }
        if (listenerException != null) {
            throw listenerException;
        }
    }

    public boolean yieldTransaction(long sleepAfterYieldDelayMillis, boolean throwIfUnsafe, CancellationSignal cancellationSignal) {
        if (throwIfUnsafe) {
            throwIfNoTransaction();
            throwIfTransactionMarkedSuccessful();
            throwIfNestedTransaction();
        } else if (this.mTransactionStack == null || this.mTransactionStack.mMarkedSuccessful || this.mTransactionStack.mParent != null) {
            return $assertionsDisabled;
        }
        if (!$assertionsDisabled && this.mConnection == null) {
            throw new AssertionError();
        } else if (this.mTransactionStack.mChildFailed) {
            return $assertionsDisabled;
        } else {
            return yieldTransactionUnchecked(sleepAfterYieldDelayMillis, cancellationSignal);
        }
    }

    private boolean yieldTransactionUnchecked(long sleepAfterYieldDelayMillis, CancellationSignal cancellationSignal) {
        if (cancellationSignal != null) {
            cancellationSignal.throwIfCanceled();
        }
        if (!this.mConnectionPool.shouldYieldConnection(this.mConnection, this.mConnectionFlags)) {
            return $assertionsDisabled;
        }
        int transactionMode = this.mTransactionStack.mMode;
        SQLiteTransactionListener listener = this.mTransactionStack.mListener;
        int connectionFlags = this.mConnectionFlags;
        endTransactionUnchecked(cancellationSignal, true);
        if (sleepAfterYieldDelayMillis > 0) {
            try {
                Thread.sleep(sleepAfterYieldDelayMillis);
            } catch (InterruptedException e) {
            }
        }
        beginTransactionUnchecked(transactionMode, listener, connectionFlags, cancellationSignal);
        return true;
    }

    public void prepare(String sql, int connectionFlags, CancellationSignal cancellationSignal, SQLiteStatementInfo outStatementInfo) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must not be null.");
        }
        if (cancellationSignal != null) {
            cancellationSignal.throwIfCanceled();
        }
        acquireConnection(sql, connectionFlags, cancellationSignal);
        try {
            this.mConnection.prepare(sql, outStatementInfo);
        } finally {
            releaseConnection();
        }
    }

    public void execute(String sql, Object[] bindArgs, int connectionFlags, CancellationSignal cancellationSignal) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must not be null.");
        } else if (!executeSpecial(sql, bindArgs, connectionFlags, cancellationSignal)) {
            acquireConnection(sql, connectionFlags, cancellationSignal);
            try {
                this.mConnection.execute(sql, bindArgs, cancellationSignal);
            } finally {
                releaseConnection();
            }
        }
    }

    public long executeForLong(String sql, Object[] bindArgs, int connectionFlags, CancellationSignal cancellationSignal) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must not be null.");
        } else if (executeSpecial(sql, bindArgs, connectionFlags, cancellationSignal)) {
            return 0;
        } else {
            acquireConnection(sql, connectionFlags, cancellationSignal);
            try {
                long executeForLong = this.mConnection.executeForLong(sql, bindArgs, cancellationSignal);
                return executeForLong;
            } finally {
                releaseConnection();
            }
        }
    }

    public String executeForString(String sql, Object[] bindArgs, int connectionFlags, CancellationSignal cancellationSignal) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must not be null.");
        } else if (executeSpecial(sql, bindArgs, connectionFlags, cancellationSignal)) {
            return null;
        } else {
            acquireConnection(sql, connectionFlags, cancellationSignal);
            try {
                String executeForString = this.mConnection.executeForString(sql, bindArgs, cancellationSignal);
                return executeForString;
            } finally {
                releaseConnection();
            }
        }
    }

    public ParcelFileDescriptor executeForBlobFileDescriptor(String sql, Object[] bindArgs, int connectionFlags, CancellationSignal cancellationSignal) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must not be null.");
        } else if (executeSpecial(sql, bindArgs, connectionFlags, cancellationSignal)) {
            return null;
        } else {
            acquireConnection(sql, connectionFlags, cancellationSignal);
            try {
                ParcelFileDescriptor executeForBlobFileDescriptor = this.mConnection.executeForBlobFileDescriptor(sql, bindArgs, cancellationSignal);
                return executeForBlobFileDescriptor;
            } finally {
                releaseConnection();
            }
        }
    }

    public int executeForChangedRowCount(String sql, Object[] bindArgs, int connectionFlags, CancellationSignal cancellationSignal) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must not be null.");
        } else if (executeSpecial(sql, bindArgs, connectionFlags, cancellationSignal)) {
            return TRANSACTION_MODE_DEFERRED;
        } else {
            acquireConnection(sql, connectionFlags, cancellationSignal);
            try {
                int executeForChangedRowCount = this.mConnection.executeForChangedRowCount(sql, bindArgs, cancellationSignal);
                return executeForChangedRowCount;
            } finally {
                releaseConnection();
            }
        }
    }

    public long executeForLastInsertedRowId(String sql, Object[] bindArgs, int connectionFlags, CancellationSignal cancellationSignal) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must not be null.");
        } else if (executeSpecial(sql, bindArgs, connectionFlags, cancellationSignal)) {
            return 0;
        } else {
            acquireConnection(sql, connectionFlags, cancellationSignal);
            try {
                long executeForLastInsertedRowId = this.mConnection.executeForLastInsertedRowId(sql, bindArgs, cancellationSignal);
                return executeForLastInsertedRowId;
            } finally {
                releaseConnection();
            }
        }
    }

    public int executeForCursorWindow(String sql, Object[] bindArgs, CursorWindow window, int startPos, int requiredPos, boolean countAllRows, int connectionFlags, CancellationSignal cancellationSignal) {
        if (sql == null) {
            throw new IllegalArgumentException("sql must not be null.");
        } else if (window == null) {
            throw new IllegalArgumentException("window must not be null.");
        } else if (executeSpecial(sql, bindArgs, connectionFlags, cancellationSignal)) {
            window.clear();
            return TRANSACTION_MODE_DEFERRED;
        } else {
            acquireConnection(sql, connectionFlags, cancellationSignal);
            try {
                int executeForCursorWindow = this.mConnection.executeForCursorWindow(sql, bindArgs, window, startPos, requiredPos, countAllRows, cancellationSignal);
                return executeForCursorWindow;
            } finally {
                releaseConnection();
            }
        }
    }

    private boolean executeSpecial(String sql, Object[] bindArgs, int connectionFlags, CancellationSignal cancellationSignal) {
        if (cancellationSignal != null) {
            cancellationSignal.throwIfCanceled();
        }
        switch (DatabaseUtils.getSqlStatementType(sql)) {
            case ViewGroupAction.TAG /*4*/:
                beginTransaction(TRANSACTION_MODE_EXCLUSIVE, null, connectionFlags, cancellationSignal);
                return true;
            case ReflectionActionWithoutParams.TAG /*5*/:
                setTransactionSuccessful();
                endTransaction(cancellationSignal);
                return true;
            case SetEmptyView.TAG /*6*/:
                endTransaction(cancellationSignal);
                return true;
            default:
                return $assertionsDisabled;
        }
    }

    private void acquireConnection(String sql, int connectionFlags, CancellationSignal cancellationSignal) {
        if (this.mConnection == null) {
            if ($assertionsDisabled || this.mConnectionUseCount == 0) {
                this.mConnection = this.mConnectionPool.acquireConnection(sql, connectionFlags, cancellationSignal);
                this.mConnectionFlags = connectionFlags;
            } else {
                throw new AssertionError();
            }
        }
        this.mConnectionUseCount += TRANSACTION_MODE_IMMEDIATE;
    }

    private void releaseConnection() {
        if (!$assertionsDisabled && this.mConnection == null) {
            throw new AssertionError();
        } else if ($assertionsDisabled || this.mConnectionUseCount > 0) {
            int i = this.mConnectionUseCount - 1;
            this.mConnectionUseCount = i;
            if (i == 0) {
                try {
                    this.mConnectionPool.releaseConnection(this.mConnection);
                } finally {
                    this.mConnection = null;
                }
            }
        } else {
            throw new AssertionError();
        }
    }

    private void throwIfNoTransaction() {
        if (this.mTransactionStack == null) {
            throw new IllegalStateException("Cannot perform this operation because there is no current transaction.");
        }
    }

    private void throwIfTransactionMarkedSuccessful() {
        if (this.mTransactionStack != null && this.mTransactionStack.mMarkedSuccessful) {
            throw new IllegalStateException("Cannot perform this operation because the transaction has already been marked successful.  The only thing you can do now is call endTransaction().");
        }
    }

    private void throwIfNestedTransaction() {
        if (hasNestedTransaction()) {
            throw new IllegalStateException("Cannot perform this operation because a nested transaction is in progress.");
        }
    }

    private Transaction obtainTransaction(int mode, SQLiteTransactionListener listener) {
        Transaction transaction = this.mTransactionPool;
        if (transaction != null) {
            this.mTransactionPool = transaction.mParent;
            transaction.mParent = null;
            transaction.mMarkedSuccessful = $assertionsDisabled;
            transaction.mChildFailed = $assertionsDisabled;
        } else {
            transaction = new Transaction();
        }
        transaction.mMode = mode;
        transaction.mListener = listener;
        return transaction;
    }

    private void recycleTransaction(Transaction transaction) {
        transaction.mParent = this.mTransactionPool;
        transaction.mListener = null;
        this.mTransactionPool = transaction;
    }
}
