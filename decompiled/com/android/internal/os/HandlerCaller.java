package com.android.internal.os;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class HandlerCaller {
    final Callback mCallback;
    final Handler mH;
    final Looper mMainLooper;

    public interface Callback {
        void executeMessage(Message message);
    }

    class MyHandler extends Handler {
        MyHandler(Looper looper, boolean async) {
            super(looper, null, async);
        }

        public void handleMessage(Message msg) {
            HandlerCaller.this.mCallback.executeMessage(msg);
        }
    }

    public HandlerCaller(Context context, Looper looper, Callback callback, boolean asyncHandler) {
        if (looper == null) {
            looper = context.getMainLooper();
        }
        this.mMainLooper = looper;
        this.mH = new MyHandler(this.mMainLooper, asyncHandler);
        this.mCallback = callback;
    }

    public Handler getHandler() {
        return this.mH;
    }

    public void executeOrSendMessage(Message msg) {
        if (Looper.myLooper() == this.mMainLooper) {
            this.mCallback.executeMessage(msg);
            msg.recycle();
            return;
        }
        this.mH.sendMessage(msg);
    }

    public void sendMessageDelayed(Message msg, long delayMillis) {
        this.mH.sendMessageDelayed(msg, delayMillis);
    }

    public boolean hasMessages(int what) {
        return this.mH.hasMessages(what);
    }

    public void removeMessages(int what) {
        this.mH.removeMessages(what);
    }

    public void removeMessages(int what, Object obj) {
        this.mH.removeMessages(what, obj);
    }

    public void sendMessage(Message msg) {
        this.mH.sendMessage(msg);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.internal.os.SomeArgs sendMessageAndWait(android.os.Message r6) {
        /*
        r5 = this;
        r4 = 1;
        r2 = android.os.Looper.myLooper();
        r3 = r5.mH;
        r3 = r3.getLooper();
        if (r2 != r3) goto L_0x0015;
    L_0x000d:
        r2 = new java.lang.IllegalStateException;
        r3 = "Can't wait on same thread as looper";
        r2.<init>(r3);
        throw r2;
    L_0x0015:
        r0 = r6.obj;
        r0 = (com.android.internal.os.SomeArgs) r0;
        r0.mWaitState = r4;
        r2 = r5.mH;
        r2.sendMessage(r6);
        monitor-enter(r0);
    L_0x0021:
        r2 = r0.mWaitState;	 Catch:{ all -> 0x0033 }
        if (r2 != r4) goto L_0x002e;
    L_0x0025:
        r0.wait();	 Catch:{ InterruptedException -> 0x0029 }
        goto L_0x0021;
    L_0x0029:
        r1 = move-exception;
        r2 = 0;
        monitor-exit(r0);	 Catch:{ all -> 0x0033 }
        r0 = r2;
    L_0x002d:
        return r0;
    L_0x002e:
        monitor-exit(r0);	 Catch:{ all -> 0x0033 }
        r2 = 0;
        r0.mWaitState = r2;
        goto L_0x002d;
    L_0x0033:
        r2 = move-exception;
        monitor-exit(r0);	 Catch:{ all -> 0x0033 }
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.HandlerCaller.sendMessageAndWait(android.os.Message):com.android.internal.os.SomeArgs");
    }

    public Message obtainMessage(int what) {
        return this.mH.obtainMessage(what);
    }

    public Message obtainMessageBO(int what, boolean arg1, Object arg2) {
        return this.mH.obtainMessage(what, arg1 ? 1 : 0, 0, arg2);
    }

    public Message obtainMessageBOO(int what, boolean arg1, Object arg2, Object arg3) {
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = arg2;
        args.arg2 = arg3;
        return this.mH.obtainMessage(what, arg1 ? 1 : 0, 0, args);
    }

    public Message obtainMessageO(int what, Object arg1) {
        return this.mH.obtainMessage(what, 0, 0, arg1);
    }

    public Message obtainMessageI(int what, int arg1) {
        return this.mH.obtainMessage(what, arg1, 0);
    }

    public Message obtainMessageII(int what, int arg1, int arg2) {
        return this.mH.obtainMessage(what, arg1, arg2);
    }

    public Message obtainMessageIO(int what, int arg1, Object arg2) {
        return this.mH.obtainMessage(what, arg1, 0, arg2);
    }

    public Message obtainMessageIIO(int what, int arg1, int arg2, Object arg3) {
        return this.mH.obtainMessage(what, arg1, arg2, arg3);
    }

    public Message obtainMessageIIOO(int what, int arg1, int arg2, Object arg3, Object arg4) {
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = arg3;
        args.arg2 = arg4;
        return this.mH.obtainMessage(what, arg1, arg2, args);
    }

    public Message obtainMessageIOO(int what, int arg1, Object arg2, Object arg3) {
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = arg2;
        args.arg2 = arg3;
        return this.mH.obtainMessage(what, arg1, 0, args);
    }

    public Message obtainMessageIOOO(int what, int arg1, Object arg2, Object arg3, Object arg4) {
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = arg2;
        args.arg2 = arg3;
        args.arg3 = arg4;
        return this.mH.obtainMessage(what, arg1, 0, args);
    }

    public Message obtainMessageOO(int what, Object arg1, Object arg2) {
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = arg1;
        args.arg2 = arg2;
        return this.mH.obtainMessage(what, 0, 0, args);
    }

    public Message obtainMessageOOO(int what, Object arg1, Object arg2, Object arg3) {
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = arg1;
        args.arg2 = arg2;
        args.arg3 = arg3;
        return this.mH.obtainMessage(what, 0, 0, args);
    }

    public Message obtainMessageOOOO(int what, Object arg1, Object arg2, Object arg3, Object arg4) {
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = arg1;
        args.arg2 = arg2;
        args.arg3 = arg3;
        args.arg4 = arg4;
        return this.mH.obtainMessage(what, 0, 0, args);
    }

    public Message obtainMessageOOOOO(int what, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = arg1;
        args.arg2 = arg2;
        args.arg3 = arg3;
        args.arg4 = arg4;
        args.arg5 = arg5;
        return this.mH.obtainMessage(what, 0, 0, args);
    }

    public Message obtainMessageIIII(int what, int arg1, int arg2, int arg3, int arg4) {
        SomeArgs args = SomeArgs.obtain();
        args.argi1 = arg1;
        args.argi2 = arg2;
        args.argi3 = arg3;
        args.argi4 = arg4;
        return this.mH.obtainMessage(what, 0, 0, args);
    }

    public Message obtainMessageIIIIII(int what, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6) {
        SomeArgs args = SomeArgs.obtain();
        args.argi1 = arg1;
        args.argi2 = arg2;
        args.argi3 = arg3;
        args.argi4 = arg4;
        args.argi5 = arg5;
        args.argi6 = arg6;
        return this.mH.obtainMessage(what, 0, 0, args);
    }

    public Message obtainMessageIIIIO(int what, int arg1, int arg2, int arg3, int arg4, Object arg5) {
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = arg5;
        args.argi1 = arg1;
        args.argi2 = arg2;
        args.argi3 = arg3;
        args.argi4 = arg4;
        return this.mH.obtainMessage(what, 0, 0, args);
    }
}
