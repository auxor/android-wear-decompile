package android.content;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.QueuedWork;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public abstract class BroadcastReceiver {
    private boolean mDebugUnregister;
    private PendingResult mPendingResult;

    public static class PendingResult {
        public static final int TYPE_COMPONENT = 0;
        public static final int TYPE_REGISTERED = 1;
        public static final int TYPE_UNREGISTERED = 2;
        boolean mAbortBroadcast;
        boolean mFinished;
        final boolean mInitialStickyHint;
        final boolean mOrderedHint;
        int mResultCode;
        String mResultData;
        Bundle mResultExtras;
        final int mSendingUser;
        final IBinder mToken;
        final int mType;

        /* renamed from: android.content.BroadcastReceiver.PendingResult.1 */
        class AnonymousClass1 implements Runnable {
            final /* synthetic */ IActivityManager val$mgr;

            AnonymousClass1(IActivityManager iActivityManager) {
                this.val$mgr = iActivityManager;
            }

            public void run() {
                PendingResult.this.sendFinished(this.val$mgr);
            }
        }

        public PendingResult(int resultCode, String resultData, Bundle resultExtras, int type, boolean ordered, boolean sticky, IBinder token, int userId) {
            this.mResultCode = resultCode;
            this.mResultData = resultData;
            this.mResultExtras = resultExtras;
            this.mType = type;
            this.mOrderedHint = ordered;
            this.mInitialStickyHint = sticky;
            this.mToken = token;
            this.mSendingUser = userId;
        }

        public final void setResultCode(int code) {
            checkSynchronousHint();
            this.mResultCode = code;
        }

        public final int getResultCode() {
            return this.mResultCode;
        }

        public final void setResultData(String data) {
            checkSynchronousHint();
            this.mResultData = data;
        }

        public final String getResultData() {
            return this.mResultData;
        }

        public final void setResultExtras(Bundle extras) {
            checkSynchronousHint();
            this.mResultExtras = extras;
        }

        public final Bundle getResultExtras(boolean makeMap) {
            Bundle e = this.mResultExtras;
            if (!makeMap) {
                return e;
            }
            if (e == null) {
                e = new Bundle();
                this.mResultExtras = e;
            }
            return e;
        }

        public final void setResult(int code, String data, Bundle extras) {
            checkSynchronousHint();
            this.mResultCode = code;
            this.mResultData = data;
            this.mResultExtras = extras;
        }

        public final boolean getAbortBroadcast() {
            return this.mAbortBroadcast;
        }

        public final void abortBroadcast() {
            checkSynchronousHint();
            this.mAbortBroadcast = true;
        }

        public final void clearAbortBroadcast() {
            this.mAbortBroadcast = false;
        }

        public final void finish() {
            if (this.mType == 0) {
                IActivityManager mgr = ActivityManagerNative.getDefault();
                if (QueuedWork.hasPendingWork()) {
                    QueuedWork.singleThreadExecutor().execute(new AnonymousClass1(mgr));
                } else {
                    sendFinished(mgr);
                }
            } else if (this.mOrderedHint && this.mType != TYPE_UNREGISTERED) {
                sendFinished(ActivityManagerNative.getDefault());
            }
        }

        public void setExtrasClassLoader(ClassLoader cl) {
            if (this.mResultExtras != null) {
                this.mResultExtras.setClassLoader(cl);
            }
        }

        public void sendFinished(IActivityManager am) {
            synchronized (this) {
                if (this.mFinished) {
                    throw new IllegalStateException("Broadcast already finished");
                }
                this.mFinished = true;
                try {
                    if (this.mResultExtras != null) {
                        this.mResultExtras.setAllowFds(false);
                    }
                    if (this.mOrderedHint) {
                        am.finishReceiver(this.mToken, this.mResultCode, this.mResultData, this.mResultExtras, this.mAbortBroadcast);
                    } else {
                        am.finishReceiver(this.mToken, TYPE_COMPONENT, null, null, false);
                    }
                } catch (RemoteException e) {
                }
            }
        }

        public int getSendingUserId() {
            return this.mSendingUser;
        }

        void checkSynchronousHint() {
            if (!this.mOrderedHint && !this.mInitialStickyHint) {
                RuntimeException e = new RuntimeException("BroadcastReceiver trying to return result during a non-ordered broadcast");
                e.fillInStackTrace();
                Log.e("BroadcastReceiver", e.getMessage(), e);
            }
        }
    }

    public abstract void onReceive(Context context, Intent intent);

    public final PendingResult goAsync() {
        PendingResult res = this.mPendingResult;
        this.mPendingResult = null;
        return res;
    }

    public IBinder peekService(Context myContext, Intent service) {
        IActivityManager am = ActivityManagerNative.getDefault();
        IBinder binder = null;
        try {
            service.prepareToLeaveProcess();
            binder = am.peekService(service, service.resolveTypeIfNeeded(myContext.getContentResolver()));
        } catch (RemoteException e) {
        }
        return binder;
    }

    public final void setResultCode(int code) {
        checkSynchronousHint();
        this.mPendingResult.mResultCode = code;
    }

    public final int getResultCode() {
        return this.mPendingResult != null ? this.mPendingResult.mResultCode : 0;
    }

    public final void setResultData(String data) {
        checkSynchronousHint();
        this.mPendingResult.mResultData = data;
    }

    public final String getResultData() {
        return this.mPendingResult != null ? this.mPendingResult.mResultData : null;
    }

    public final void setResultExtras(Bundle extras) {
        checkSynchronousHint();
        this.mPendingResult.mResultExtras = extras;
    }

    public final Bundle getResultExtras(boolean makeMap) {
        if (this.mPendingResult == null) {
            return null;
        }
        Bundle e = this.mPendingResult.mResultExtras;
        if (!makeMap || e != null) {
            return e;
        }
        PendingResult pendingResult = this.mPendingResult;
        e = new Bundle();
        pendingResult.mResultExtras = e;
        return e;
    }

    public final void setResult(int code, String data, Bundle extras) {
        checkSynchronousHint();
        this.mPendingResult.mResultCode = code;
        this.mPendingResult.mResultData = data;
        this.mPendingResult.mResultExtras = extras;
    }

    public final boolean getAbortBroadcast() {
        return this.mPendingResult != null ? this.mPendingResult.mAbortBroadcast : false;
    }

    public final void abortBroadcast() {
        checkSynchronousHint();
        this.mPendingResult.mAbortBroadcast = true;
    }

    public final void clearAbortBroadcast() {
        if (this.mPendingResult != null) {
            this.mPendingResult.mAbortBroadcast = false;
        }
    }

    public final boolean isOrderedBroadcast() {
        return this.mPendingResult != null ? this.mPendingResult.mOrderedHint : false;
    }

    public final boolean isInitialStickyBroadcast() {
        return this.mPendingResult != null ? this.mPendingResult.mInitialStickyHint : false;
    }

    public final void setOrderedHint(boolean isOrdered) {
    }

    public final void setPendingResult(PendingResult result) {
        this.mPendingResult = result;
    }

    public final PendingResult getPendingResult() {
        return this.mPendingResult;
    }

    public int getSendingUserId() {
        return this.mPendingResult.mSendingUser;
    }

    public final void setDebugUnregister(boolean debug) {
        this.mDebugUnregister = debug;
    }

    public final boolean getDebugUnregister() {
        return this.mDebugUnregister;
    }

    void checkSynchronousHint() {
        if (this.mPendingResult == null) {
            throw new IllegalStateException("Call while result is not pending");
        } else if (!this.mPendingResult.mOrderedHint && !this.mPendingResult.mInitialStickyHint) {
            RuntimeException e = new RuntimeException("BroadcastReceiver trying to return result during a non-ordered broadcast");
            e.fillInStackTrace();
            Log.e("BroadcastReceiver", e.getMessage(), e);
        }
    }
}
