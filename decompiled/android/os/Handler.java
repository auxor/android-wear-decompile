package android.os;

import android.os.IMessenger.Stub;
import android.util.Log;
import android.util.Printer;

public class Handler {
    private static final boolean FIND_POTENTIAL_LEAKS = false;
    private static final String TAG = "Handler";
    final boolean mAsynchronous;
    final Callback mCallback;
    final Looper mLooper;
    IMessenger mMessenger;
    final MessageQueue mQueue;

    public interface Callback {
        boolean handleMessage(Message message);
    }

    private static final class BlockingRunnable implements Runnable {
        private boolean mDone;
        private final Runnable mTask;

        public BlockingRunnable(Runnable task) {
            this.mTask = task;
        }

        public void run() {
            try {
                this.mTask.run();
                synchronized (this) {
                    this.mDone = true;
                    notifyAll();
                }
            } catch (Throwable th) {
                synchronized (this) {
                }
                this.mDone = true;
                notifyAll();
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean postAndWait(android.os.Handler r11, long r12) {
            /*
            r10 = this;
            r8 = 0;
            r4 = 0;
            r5 = r11.post(r10);
            if (r5 != 0) goto L_0x000a;
        L_0x0009:
            return r4;
        L_0x000a:
            monitor-enter(r10);
            r5 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1));
            if (r5 <= 0) goto L_0x002e;
        L_0x000f:
            r6 = android.os.SystemClock.uptimeMillis();	 Catch:{ all -> 0x0025 }
            r2 = r6 + r12;
        L_0x0015:
            r5 = r10.mDone;	 Catch:{ all -> 0x0025 }
            if (r5 != 0) goto L_0x0038;
        L_0x0019:
            r6 = android.os.SystemClock.uptimeMillis();	 Catch:{ all -> 0x0025 }
            r0 = r2 - r6;
            r5 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1));
            if (r5 > 0) goto L_0x0028;
        L_0x0023:
            monitor-exit(r10);	 Catch:{ all -> 0x0025 }
            goto L_0x0009;
        L_0x0025:
            r4 = move-exception;
            monitor-exit(r10);	 Catch:{ all -> 0x0025 }
            throw r4;
        L_0x0028:
            r10.wait(r0);	 Catch:{ InterruptedException -> 0x002c }
            goto L_0x0015;
        L_0x002c:
            r5 = move-exception;
            goto L_0x0015;
        L_0x002e:
            r4 = r10.mDone;	 Catch:{ all -> 0x0025 }
            if (r4 != 0) goto L_0x0038;
        L_0x0032:
            r10.wait();	 Catch:{ InterruptedException -> 0x0036 }
            goto L_0x002e;
        L_0x0036:
            r4 = move-exception;
            goto L_0x002e;
        L_0x0038:
            monitor-exit(r10);	 Catch:{ all -> 0x0025 }
            r4 = 1;
            goto L_0x0009;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.os.Handler.BlockingRunnable.postAndWait(android.os.Handler, long):boolean");
        }
    }

    private final class MessengerImpl extends Stub {
        private MessengerImpl() {
        }

        public void send(Message msg) {
            msg.sendingUid = Binder.getCallingUid();
            Handler.this.sendMessage(msg);
        }
    }

    public void handleMessage(Message msg) {
    }

    public void dispatchMessage(Message msg) {
        if (msg.callback != null) {
            handleCallback(msg);
        } else if (this.mCallback == null || !this.mCallback.handleMessage(msg)) {
            handleMessage(msg);
        }
    }

    public Handler() {
        this(null, (boolean) FIND_POTENTIAL_LEAKS);
    }

    public Handler(Callback callback) {
        this(callback, (boolean) FIND_POTENTIAL_LEAKS);
    }

    public Handler(Looper looper) {
        this(looper, null, FIND_POTENTIAL_LEAKS);
    }

    public Handler(Looper looper, Callback callback) {
        this(looper, callback, FIND_POTENTIAL_LEAKS);
    }

    public Handler(boolean async) {
        this(null, async);
    }

    public Handler(Callback callback, boolean async) {
        this.mLooper = Looper.myLooper();
        if (this.mLooper == null) {
            throw new RuntimeException("Can't create handler inside thread that has not called Looper.prepare()");
        }
        this.mQueue = this.mLooper.mQueue;
        this.mCallback = callback;
        this.mAsynchronous = async;
    }

    public Handler(Looper looper, Callback callback, boolean async) {
        this.mLooper = looper;
        this.mQueue = looper.mQueue;
        this.mCallback = callback;
        this.mAsynchronous = async;
    }

    public String getMessageName(Message message) {
        if (message.callback != null) {
            return message.callback.getClass().getName();
        }
        return "0x" + Integer.toHexString(message.what);
    }

    public final Message obtainMessage() {
        return Message.obtain(this);
    }

    public final Message obtainMessage(int what) {
        return Message.obtain(this, what);
    }

    public final Message obtainMessage(int what, Object obj) {
        return Message.obtain(this, what, obj);
    }

    public final Message obtainMessage(int what, int arg1, int arg2) {
        return Message.obtain(this, what, arg1, arg2);
    }

    public final Message obtainMessage(int what, int arg1, int arg2, Object obj) {
        return Message.obtain(this, what, arg1, arg2, obj);
    }

    public final boolean post(Runnable r) {
        return sendMessageDelayed(getPostMessage(r), 0);
    }

    public final boolean postAtTime(Runnable r, long uptimeMillis) {
        return sendMessageAtTime(getPostMessage(r), uptimeMillis);
    }

    public final boolean postAtTime(Runnable r, Object token, long uptimeMillis) {
        return sendMessageAtTime(getPostMessage(r, token), uptimeMillis);
    }

    public final boolean postDelayed(Runnable r, long delayMillis) {
        return sendMessageDelayed(getPostMessage(r), delayMillis);
    }

    public final boolean postAtFrontOfQueue(Runnable r) {
        return sendMessageAtFrontOfQueue(getPostMessage(r));
    }

    public final boolean runWithScissors(Runnable r, long timeout) {
        if (r == null) {
            throw new IllegalArgumentException("runnable must not be null");
        } else if (timeout < 0) {
            throw new IllegalArgumentException("timeout must be non-negative");
        } else if (Looper.myLooper() != this.mLooper) {
            return new BlockingRunnable(r).postAndWait(this, timeout);
        } else {
            r.run();
            return true;
        }
    }

    public final void removeCallbacks(Runnable r) {
        this.mQueue.removeMessages(this, r, null);
    }

    public final void removeCallbacks(Runnable r, Object token) {
        this.mQueue.removeMessages(this, r, token);
    }

    public final boolean sendMessage(Message msg) {
        return sendMessageDelayed(msg, 0);
    }

    public final boolean sendEmptyMessage(int what) {
        return sendEmptyMessageDelayed(what, 0);
    }

    public final boolean sendEmptyMessageDelayed(int what, long delayMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        return sendMessageDelayed(msg, delayMillis);
    }

    public final boolean sendEmptyMessageAtTime(int what, long uptimeMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        return sendMessageAtTime(msg, uptimeMillis);
    }

    public final boolean sendMessageDelayed(Message msg, long delayMillis) {
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        return sendMessageAtTime(msg, SystemClock.uptimeMillis() + delayMillis);
    }

    public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
        MessageQueue queue = this.mQueue;
        if (queue != null) {
            return enqueueMessage(queue, msg, uptimeMillis);
        }
        RuntimeException e = new RuntimeException(this + " sendMessageAtTime() called with no mQueue");
        Log.w("Looper", e.getMessage(), e);
        return FIND_POTENTIAL_LEAKS;
    }

    public final boolean sendMessageAtFrontOfQueue(Message msg) {
        MessageQueue queue = this.mQueue;
        if (queue != null) {
            return enqueueMessage(queue, msg, 0);
        }
        RuntimeException e = new RuntimeException(this + " sendMessageAtTime() called with no mQueue");
        Log.w("Looper", e.getMessage(), e);
        return FIND_POTENTIAL_LEAKS;
    }

    private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
        msg.target = this;
        if (this.mAsynchronous) {
            msg.setAsynchronous(true);
        }
        return queue.enqueueMessage(msg, uptimeMillis);
    }

    public final void removeMessages(int what) {
        this.mQueue.removeMessages(this, what, null);
    }

    public final void removeMessages(int what, Object object) {
        this.mQueue.removeMessages(this, what, object);
    }

    public final void removeCallbacksAndMessages(Object token) {
        this.mQueue.removeCallbacksAndMessages(this, token);
    }

    public final boolean hasMessages(int what) {
        return this.mQueue.hasMessages(this, what, null);
    }

    public final boolean hasMessages(int what, Object object) {
        return this.mQueue.hasMessages(this, what, object);
    }

    public final boolean hasCallbacks(Runnable r) {
        return this.mQueue.hasMessages(this, r, null);
    }

    public final Looper getLooper() {
        return this.mLooper;
    }

    public final void dump(Printer pw, String prefix) {
        pw.println(prefix + this + " @ " + SystemClock.uptimeMillis());
        if (this.mLooper == null) {
            pw.println(prefix + "looper uninitialized");
        } else {
            this.mLooper.dump(pw, prefix + "  ");
        }
    }

    public String toString() {
        return "Handler (" + getClass().getName() + ") {" + Integer.toHexString(System.identityHashCode(this)) + "}";
    }

    final IMessenger getIMessenger() {
        IMessenger iMessenger;
        synchronized (this.mQueue) {
            if (this.mMessenger != null) {
                iMessenger = this.mMessenger;
            } else {
                this.mMessenger = new MessengerImpl();
                iMessenger = this.mMessenger;
            }
        }
        return iMessenger;
    }

    private static Message getPostMessage(Runnable r) {
        Message m = Message.obtain();
        m.callback = r;
        return m;
    }

    private static Message getPostMessage(Runnable r, Object token) {
        Message m = Message.obtain();
        m.obj = token;
        m.callback = r;
        return m;
    }

    private static void handleCallback(Message message) {
        message.callback.run();
    }
}
