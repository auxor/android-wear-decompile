package com.android.internal.os;

public final class SomeArgs {
    private static final int MAX_POOL_SIZE = 10;
    static final int WAIT_FINISHED = 2;
    static final int WAIT_NONE = 0;
    static final int WAIT_WAITING = 1;
    private static SomeArgs sPool;
    private static Object sPoolLock;
    private static int sPoolSize;
    public Object arg1;
    public Object arg2;
    public Object arg3;
    public Object arg4;
    public Object arg5;
    public Object arg6;
    public int argi1;
    public int argi2;
    public int argi3;
    public int argi4;
    public int argi5;
    public int argi6;
    private boolean mInPool;
    private SomeArgs mNext;
    int mWaitState;

    static {
        sPoolLock = new Object();
    }

    private SomeArgs() {
        this.mWaitState = WAIT_NONE;
    }

    public static SomeArgs obtain() {
        SomeArgs args;
        synchronized (sPoolLock) {
            if (sPoolSize > 0) {
                args = sPool;
                sPool = sPool.mNext;
                args.mNext = null;
                args.mInPool = false;
                sPoolSize--;
            } else {
                args = new SomeArgs();
            }
        }
        return args;
    }

    public void recycle() {
        if (this.mInPool) {
            throw new IllegalStateException("Already recycled.");
        } else if (this.mWaitState == 0) {
            synchronized (sPoolLock) {
                clear();
                if (sPoolSize < MAX_POOL_SIZE) {
                    this.mNext = sPool;
                    this.mInPool = true;
                    sPool = this;
                    sPoolSize += WAIT_WAITING;
                }
            }
        }
    }

    private void clear() {
        this.arg1 = null;
        this.arg2 = null;
        this.arg3 = null;
        this.arg4 = null;
        this.arg5 = null;
        this.arg6 = null;
        this.argi1 = WAIT_NONE;
        this.argi2 = WAIT_NONE;
        this.argi3 = WAIT_NONE;
        this.argi4 = WAIT_NONE;
        this.argi5 = WAIT_NONE;
        this.argi6 = WAIT_NONE;
    }
}
