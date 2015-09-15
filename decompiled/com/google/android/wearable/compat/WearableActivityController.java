package com.google.android.wearable.compat;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.wearable.ambient.AmbientComponent;
import com.google.android.wearable.ambient.AmbientConfiguration;
import com.google.android.wearable.ambient.AmbientConstants;
import com.google.android.wearable.ambient.activity.IAmbientActivityCallbacks.Stub;
import com.google.android.wearable.ambient.activity.IAmbientActivityRegistrationService;
import com.google.android.wearable.ambient.activity.IAmbientActivityService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;

public class WearableActivityController {
    public static final String EXTRA_BURN_IN_PROTECTION = "com.google.android.wearable.compat.extra.BURN_IN_PROTECTION";
    public static final String EXTRA_LOWBIT_AMBIENT = "com.google.android.wearable.compat.extra.LOWBIT_AMBIENT";
    private static final String LOGGABLE_TAG = "WearableActivityCont";
    private static final int STATE_CREATED = 1;
    private static final int STATE_DESTROYED = 5;
    private static final int STATE_PAUSED = 3;
    private static final int STATE_RESUMED = 2;
    private static final int STATE_STOPPED = 4;
    private Activity mActivity;
    private volatile boolean mAmbient;
    private AmbientCallback mAmbientCallback;
    private boolean mBound;
    private final AmbientComponent mComponent;
    private final AmbientConfiguration mConfiguration;
    private long mEnterAmbientDeliveredTimestamp;
    private long mEnterAmbientTimestamp;
    private long mExitAmbientDeliveredTimestamp;
    private final Runnable mExitAmbientRunnable;
    private long mExitAmbientTimestamp;
    private final Object mLock;
    private boolean mPendingSetConfiguration;
    private AmbientServiceConnection mServiceConnection;
    private int mState;
    private final String mTag;
    private long mUpdateAmbientDeliveredTimestamp;
    private final Runnable mUpdateAmbientRunnable;
    private long mUpdateAmbientTimestamp;
    private final WakeLock mWakeLock;

    /* renamed from: com.google.android.wearable.compat.WearableActivityController.3 */
    class AnonymousClass3 implements Runnable {
        final /* synthetic */ Bundle val$ambientDetails;

        AnonymousClass3(Bundle bundle) {
            this.val$ambientDetails = bundle;
        }

        public void run() {
            WearableActivityController.this.doEnterAmbient(this.val$ambientDetails);
        }
    }

    private static class AmbientActivityCallbacksStub extends Stub {
        private final WeakReference<WearableActivityController> mController;
        private final String mTag;

        public AmbientActivityCallbacksStub(String tag, WearableActivityController controller) {
            this.mTag = tag;
            this.mController = new WeakReference(controller);
        }

        public void enterAmbient(Bundle ambientDetails) throws RemoteException {
            WearableActivityController controller = (WearableActivityController) this.mController.get();
            if (controller != null) {
                controller.postEnterAmbient(ambientDetails);
            } else if (Log.isLoggable(WearableActivityController.LOGGABLE_TAG, WearableActivityController.STATE_PAUSED)) {
                Log.d(this.mTag, "enterAmbient: Called with already released controller.");
            }
        }

        public void updateAmbient() throws RemoteException {
            WearableActivityController controller = (WearableActivityController) this.mController.get();
            if (controller != null) {
                controller.postUpdateAmbient();
            } else if (Log.isLoggable(WearableActivityController.LOGGABLE_TAG, WearableActivityController.STATE_PAUSED)) {
                Log.d(this.mTag, "updateAmbient: Called with already released controller.");
            }
        }

        public void exitAmbient() throws RemoteException {
            WearableActivityController controller = (WearableActivityController) this.mController.get();
            if (controller != null) {
                controller.postExitAmbient();
            } else if (Log.isLoggable(WearableActivityController.LOGGABLE_TAG, WearableActivityController.STATE_PAUSED)) {
                Log.d(this.mTag, "exitAmbient: Called with already released controller.");
            }
        }
    }

    public static abstract class AmbientCallback {
        public void onEnterAmbient(Bundle ambientDetails) {
        }

        public void onUpdateAmbient() {
        }

        public void onExitAmbient() {
        }
    }

    private static class AmbientServiceConnection implements ServiceConnection {
        IAmbientActivityService activityService;
        private final WeakReference<WearableActivityController> mController;
        private final String mTag;

        public AmbientServiceConnection(String tag, WearableActivityController controller) {
            this.mTag = tag;
            this.mController = new WeakReference(controller);
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            if (Log.isLoggable(WearableActivityController.LOGGABLE_TAG, WearableActivityController.STATE_PAUSED)) {
                Log.d(this.mTag, "onServiceConnected");
            }
            WearableActivityController controller = (WearableActivityController) this.mController.get();
            if (controller != null) {
                try {
                    this.activityService = IAmbientActivityRegistrationService.Stub.asInterface(service).attach(controller.mComponent.getBundle(), new AmbientActivityCallbacksStub(this.mTag, controller));
                } catch (RemoteException e) {
                    if (Log.isLoggable(WearableActivityController.LOGGABLE_TAG, WearableActivityController.STATE_PAUSED)) {
                        Log.d(this.mTag, "Failed during registration", e);
                    }
                }
                controller.update();
            } else if (Log.isLoggable(WearableActivityController.LOGGABLE_TAG, WearableActivityController.STATE_PAUSED)) {
                Log.d(this.mTag, "onServiceConnected: Called with already released controller.");
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            Log.w(this.mTag, "onServiceDisconnected");
        }

        public void detach(Context context) {
            if (this.activityService != null) {
                try {
                    this.activityService.detach();
                    if (Log.isLoggable(WearableActivityController.LOGGABLE_TAG, WearableActivityController.STATE_PAUSED)) {
                        Log.d(this.mTag, "detach: detached from activityService.");
                    }
                } catch (RemoteException e) {
                }
            }
            this.activityService = null;
            if (context != null) {
                try {
                    context.unbindService(this);
                    if (Log.isLoggable(WearableActivityController.LOGGABLE_TAG, WearableActivityController.STATE_PAUSED)) {
                        Log.d(this.mTag, "detach: unbound activityService.");
                    }
                } catch (IllegalArgumentException e2) {
                }
            }
        }
    }

    public WearableActivityController(String tag, Activity activity, AmbientCallback callback) {
        this.mLock = new Object();
        this.mUpdateAmbientRunnable = new Runnable() {
            public void run() {
                WearableActivityController.this.doUpdateAmbient();
            }
        };
        this.mExitAmbientRunnable = new Runnable() {
            public void run() {
                WearableActivityController.this.doExitAmbient();
            }
        };
        this.mTag = tag;
        this.mActivity = activity;
        this.mAmbientCallback = callback;
        this.mComponent = new AmbientComponent(activity.getComponentName());
        this.mConfiguration = AmbientConfiguration.create();
        this.mWakeLock = ((PowerManager) activity.getSystemService("power")).newWakeLock(STATE_CREATED, getClass().getName() + "-ambient");
        this.mServiceConnection = new AmbientServiceConnection(tag, this);
    }

    public void onCreate() {
        this.mState = STATE_CREATED;
        Intent bindIntent = new Intent(AmbientConstants.ACTION_BIND_AMBIACTIVE_ACTIVITY).setPackage(AmbientConstants.AMBIACTIVE_SERVICE_PACKAGE);
        Activity activity = this.mActivity;
        if (activity == null) {
            Log.w(this.mTag, "onCreate called when activity has already been dereferenced.");
            return;
        }
        this.mBound = activity.bindService(bindIntent, this.mServiceConnection, STATE_CREATED);
        if (!this.mBound) {
            activity.unbindService(this.mServiceConnection);
            throw new IllegalStateException("Unable to bind to AmbientService!");
        }
    }

    public void onResume() {
        this.mState = STATE_RESUMED;
        update();
    }

    public void onPause() {
        this.mState = STATE_PAUSED;
        update();
    }

    public void onStop() {
        this.mState = STATE_STOPPED;
        update();
    }

    public void onDestroy() {
        this.mState = STATE_DESTROYED;
        update();
        this.mWakeLock.setReferenceCounted(false);
        this.mWakeLock.release();
    }

    public final void setAmbientEnabled() {
        synchronized (this.mLock) {
            this.mConfiguration.setAmbientEnabled(true);
            this.mPendingSetConfiguration = true;
        }
        update();
    }

    public final boolean isAmbient() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mAmbient;
        }
        return z;
    }

    private boolean update() {
        if (Log.isLoggable(LOGGABLE_TAG, STATE_PAUSED)) {
            boolean z;
            String str = this.mTag;
            StringBuilder append = new StringBuilder().append("update: ").append(getStateAsString()).append(" attached: ");
            if (this.mServiceConnection.activityService != null) {
                z = true;
            } else {
                z = false;
            }
            Log.d(str, append.append(z).toString());
        }
        IAmbientActivityService service = this.mServiceConnection.activityService;
        if (service == null) {
            return false;
        }
        try {
            synchronized (this.mLock) {
                if (this.mPendingSetConfiguration) {
                    service.setConfiguration(this.mConfiguration.getBundle());
                    this.mPendingSetConfiguration = false;
                }
            }
            if (this.mState == STATE_DESTROYED) {
                detach();
            } else if (this.mState == STATE_STOPPED) {
                service.onStopped();
            } else if (this.mState == STATE_PAUSED) {
                service.onPaused();
            } else if (this.mState == STATE_RESUMED) {
                service.onResumed();
            } else {
                Log.w(this.mTag, "update: unknown state! " + this.mState);
            }
            return true;
        } catch (RemoteException e) {
            if (!Log.isLoggable(LOGGABLE_TAG, STATE_PAUSED)) {
                return false;
            }
            Log.d(this.mTag, "Failed during update", e);
            return false;
        }
    }

    private void postEnterAmbient(Bundle ambientDetails) {
        if (Log.isLoggable(LOGGABLE_TAG, STATE_PAUSED)) {
            Log.d(this.mTag, "postEnterAmbient");
        }
        Activity activity = this.mActivity;
        if (activity == null) {
            Log.w(this.mTag, "postEnterAmbient: Called when activity has already been dereferenced.");
            return;
        }
        this.mEnterAmbientTimestamp = System.currentTimeMillis();
        this.mWakeLock.acquire();
        activity.runOnUiThread(new AnonymousClass3(ambientDetails));
    }

    private void doEnterAmbient(Bundle ambientDetails) {
        if (this.mState != STATE_DESTROYED) {
            IAmbientActivityService service = this.mServiceConnection.activityService;
            if (service != null) {
                AmbientCallback callback = this.mAmbientCallback;
                if (callback == null) {
                    Log.w(this.mTag, "enterAmbient: Called when already dereferenced.");
                    return;
                }
                this.mEnterAmbientDeliveredTimestamp = System.currentTimeMillis();
                try {
                    this.mAmbient = true;
                    callback.onEnterAmbient(ambientDetails);
                    service.onAmbientEntered();
                    this.mWakeLock.release();
                } catch (RemoteException e) {
                    if (Log.isLoggable(LOGGABLE_TAG, STATE_PAUSED)) {
                        Log.d(this.mTag, "enterAmbient: Service died.", e);
                    }
                    this.mWakeLock.release();
                } catch (Throwable th) {
                    this.mWakeLock.release();
                }
            } else if (Log.isLoggable(LOGGABLE_TAG, STATE_PAUSED)) {
                Log.d(this.mTag, "enterAmbient: Service is not connected.");
            }
        } else if (Log.isLoggable(LOGGABLE_TAG, STATE_PAUSED)) {
            Log.d(this.mTag, "enterAmbient: run() called after activity was destroyed");
        }
    }

    private void postUpdateAmbient() {
        if (Log.isLoggable(LOGGABLE_TAG, STATE_PAUSED)) {
            Log.d(this.mTag, "postUpdateAmbient");
        }
        Activity activity = this.mActivity;
        if (activity == null) {
            Log.w(this.mTag, "postUpdateAmbient: Called when activity has already been dereferenced.");
            return;
        }
        this.mUpdateAmbientTimestamp = System.currentTimeMillis();
        this.mWakeLock.acquire();
        activity.runOnUiThread(this.mUpdateAmbientRunnable);
    }

    private void doUpdateAmbient() {
        if (this.mState != STATE_DESTROYED) {
            IAmbientActivityService service = this.mServiceConnection.activityService;
            if (service != null) {
                AmbientCallback callback = this.mAmbientCallback;
                if (callback == null) {
                    Log.w(this.mTag, "updateAmbient: Called when already dereferenced.");
                } else if (this.mAmbient) {
                    this.mUpdateAmbientDeliveredTimestamp = System.currentTimeMillis();
                    try {
                        callback.onUpdateAmbient();
                        service.onAmbientUpdated();
                        this.mWakeLock.release();
                    } catch (RemoteException e) {
                        if (Log.isLoggable(LOGGABLE_TAG, STATE_PAUSED)) {
                            Log.d(this.mTag, "updateAmbient: Service died.", e);
                        }
                        this.mWakeLock.release();
                    } catch (Throwable th) {
                        this.mWakeLock.release();
                    }
                } else {
                    Log.w(this.mTag, "updateAmbient: Called while no longer ambient.");
                }
            } else if (Log.isLoggable(LOGGABLE_TAG, STATE_PAUSED)) {
                Log.d(this.mTag, "updateAmbient: Service is not connected.");
            }
        } else if (Log.isLoggable(LOGGABLE_TAG, STATE_PAUSED)) {
            Log.d(this.mTag, "updateAmbient: run() called after activity was destroyed");
        }
    }

    private void postExitAmbient() {
        if (Log.isLoggable(LOGGABLE_TAG, STATE_PAUSED)) {
            Log.d(this.mTag, "postExitAmbient");
        }
        Activity activity = this.mActivity;
        if (activity == null) {
            Log.w(this.mTag, "postExitAmbient called when activity has already been dereferenced.");
            return;
        }
        this.mExitAmbientTimestamp = System.currentTimeMillis();
        this.mWakeLock.acquire();
        activity.runOnUiThread(this.mExitAmbientRunnable);
    }

    private void doExitAmbient() {
        if (this.mState != STATE_DESTROYED) {
            IAmbientActivityService service = this.mServiceConnection.activityService;
            if (service != null) {
                AmbientCallback callback = this.mAmbientCallback;
                if (callback == null) {
                    Log.w(this.mTag, "exitAmbient: Called when already dereferenced.");
                    return;
                }
                this.mExitAmbientDeliveredTimestamp = System.currentTimeMillis();
                try {
                    this.mAmbient = false;
                    callback.onExitAmbient();
                    service.onAmbientExited();
                    this.mWakeLock.release();
                } catch (RemoteException e) {
                    if (Log.isLoggable(LOGGABLE_TAG, STATE_PAUSED)) {
                        Log.d(this.mTag, "exitAmbient: Service died.");
                    }
                    this.mWakeLock.release();
                } catch (Throwable th) {
                    this.mWakeLock.release();
                }
            } else if (Log.isLoggable(LOGGABLE_TAG, STATE_PAUSED)) {
                Log.d(this.mTag, "exitAmbient: Service is not connected.");
            }
        } else if (Log.isLoggable(LOGGABLE_TAG, STATE_PAUSED)) {
            Log.d(this.mTag, "exitAmbient: run() called after activity was destroyed");
        }
    }

    private void detach() {
        this.mServiceConnection.detach(this.mActivity);
        this.mActivity = null;
        this.mAmbientCallback = null;
    }

    private String getStateAsString() {
        switch (this.mState) {
            case STATE_CREATED /*1*/:
                return "CREATED";
            case STATE_RESUMED /*2*/:
                return "RESUMED";
            case STATE_PAUSED /*3*/:
                return "PAUSED";
            case STATE_STOPPED /*4*/:
                return "STOPPED";
            case STATE_DESTROYED /*5*/:
                return "DESTROYED";
            default:
                return "UNKNOWN";
        }
    }

    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        try {
            writer.print(prefix);
            writer.println("Activity: " + this.mActivity);
            writer.print(prefix);
            writer.println("Callback: " + this.mAmbientCallback);
            writer.print(prefix);
            writer.println("State: " + getStateAsString() + " bound: " + this.mBound + " attached: " + this.mServiceConnection.activityService);
            writer.print(prefix);
            writer.println("Ambient: " + isAmbient());
            writer.print(prefix);
            writer.println("enterAmbient: " + this.mEnterAmbientTimestamp + " " + this.mEnterAmbientDeliveredTimestamp + " " + (this.mEnterAmbientDeliveredTimestamp - this.mEnterAmbientTimestamp) + "ms");
            writer.print(prefix);
            writer.println("updateAmbient: " + this.mUpdateAmbientTimestamp + " " + this.mUpdateAmbientDeliveredTimestamp + " " + (this.mUpdateAmbientDeliveredTimestamp - this.mUpdateAmbientTimestamp) + "ms");
            writer.print(prefix);
            writer.println("exitAmbient: " + this.mExitAmbientTimestamp + " " + this.mExitAmbientDeliveredTimestamp + " " + (this.mExitAmbientDeliveredTimestamp - this.mExitAmbientTimestamp) + "ms");
        } catch (Throwable e) {
            writer.print(prefix);
            writer.println("caught exception while dumping" + e.getMessage());
        }
    }
}
