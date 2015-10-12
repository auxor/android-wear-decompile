package com.google.android.wearable.ambient;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.service.dreams.DreamService;
import android.util.Log;
import android.view.MotionEvent;
import com.android.internal.util.IndentingPrintWriter;
import com.google.android.clockwork.settings.AmbientConfig;
import com.google.android.clockwork.settings.SettingsContract;
import com.google.android.wearable.ambient.dream.IAmbientDreamCallbacks;
import com.google.android.wearable.ambient.dream.IAmbientDreamRegistrationService.Stub;
import com.google.android.wearable.ambient.dream.IAmbientDreamService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public class AmbientDream extends DreamService {
    private static String TAG;
    private boolean mAbortedOnCreate;
    private AlarmManager mAlarmManager;
    private AmbientServiceConnection mAmbientServiceConnection;
    private BrightnessInterpolator mBrightnessInterpolator;
    private IAmbientDreamCallbacks mCallbacks;
    private AmbientComponent mComponent;
    private AmbientConfig mConfig;
    private boolean mDisableAmbientInTheaterMode;
    private boolean mDreaming;
    private PendingIntent mEarlyFinishPendingIntent;
    private BroadcastReceiver mEarlyFinishReceiver;
    private boolean mEarlyFinishReceiverRegistered;
    private Handler mHandler;
    private long mLastTouchTime;
    private BroadcastReceiver mPowerSaveModeReceiver;
    private boolean mPowerSaveModeReceiverRegistered;
    private boolean mStopped;
    private boolean mTheaterModeOn;
    private Runnable mTheaterModeRunnable;
    private int mTheaterTouchCount;
    private boolean mTouchWakeUpInTheaterMode;

    private class AmbientServiceConnection implements ServiceConnection {
        private IAmbientDreamService mAmbientService;

        private AmbientServiceConnection() {
            throw new VerifyError("bad dex opcode");
        }

        public IAmbientDreamService getService() {
            return this.mAmbientService;
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (Log.isLoggable(AmbientDream.TAG, 3)) {
                Log.d(AmbientDream.TAG, "onServiceConnected: " + iBinder);
            }
            if (!AmbientDream.this.mStopped) {
                try {
                    this.mAmbientService = Stub.asInterface(iBinder).attach(AmbientDream.this.mComponent.getBundle(), AmbientDream.this.mCallbacks);
                    this.mAmbientService.onCreate();
                } catch (Throwable e) {
                    Log.w("onServiceConnected: Failed during registration.", e);
                }
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            Log.w(AmbientDream.TAG, "onServiceDisconnected: " + componentName);
        }
    }

    private class Callbacks extends IAmbientDreamCallbacks.Stub {
        private Callbacks() {
            throw new VerifyError("bad dex opcode");
        }

        public void doze() throws RemoteException {
            AmbientDream.this.mHandler.post(new Runnable() {
                {
                    throw new VerifyError("bad dex opcode");
                }

                public void run() {
                    if (AmbientDream.this.mDreaming) {
                        if (Log.isLoggable(AmbientDream.TAG, 3)) {
                            Log.d(AmbientDream.TAG, "doze");
                        }
                        AmbientDream ambientDream = AmbientDream.this;
                        int i = (AmbientDream.this.mTheaterModeOn && AmbientDream.this.mDisableAmbientInTheaterMode) ? 1 : 4;
                        ambientDream.setDozeScreenState(i);
                        AmbientDream.this.startDozing();
                        return;
                    }
                    Log.w(AmbientDream.TAG, "doze: Already finished dreaming.");
                }
            });
        }

        public void exitAmbient() throws RemoteException {
            AmbientDream.this.mHandler.post(new Runnable() {
                {
                    throw new VerifyError("bad dex opcode");
                }

                public void run() {
                    if (AmbientDream.this.mDreaming) {
                        if (Log.isLoggable(AmbientDream.TAG, 3)) {
                            Log.d(AmbientDream.TAG, "exitAmbient");
                        }
                        AmbientDream.this.finish();
                        return;
                    }
                    Log.w(AmbientDream.TAG, "exitAmbient: Already finished dreaming.");
                }
            });
        }
    }

    static {
        TAG = "AmbientDream";
    }

    public AmbientDream() {
        this.mDreaming = false;
        this.mStopped = false;
        this.mTheaterModeOn = false;
        this.mTouchWakeUpInTheaterMode = false;
        this.mDisableAmbientInTheaterMode = false;
        this.mHandler = new Handler();
        this.mTheaterModeRunnable = new Runnable() {
            {
                throw new VerifyError("bad dex opcode");
            }

            public void run() {
                AmbientDream.this.setDozeScreenState(1);
            }
        };
        this.mEarlyFinishReceiver = new BroadcastReceiver() {
            {
                throw new VerifyError("bad dex opcode");
            }

            public void onReceive(Context context, Intent intent) {
                if (Log.isLoggable(AmbientDream.TAG, 3)) {
                    Log.d(AmbientDream.TAG, "mEarlyFinishReceiver " + intent);
                }
                AmbientDream.this.finish();
            }
        };
        this.mEarlyFinishReceiverRegistered = false;
        this.mPowerSaveModeReceiver = new BroadcastReceiver() {
            {
                throw new VerifyError("bad dex opcode");
            }

            public void onReceive(Context context, Intent intent) {
                if (Log.isLoggable(AmbientDream.TAG, 3)) {
                    Log.d(AmbientDream.TAG, "mPowerSaveModeReceiver " + intent);
                }
                AmbientDream.this.finish();
            }
        };
        this.mPowerSaveModeReceiverRegistered = false;
    }

    private void detachAndDisconnect() {
        this.mStopped = true;
        if (this.mAmbientServiceConnection.getService() != null) {
            try {
                this.mAmbientServiceConnection.getService().detach();
            } catch (Throwable e) {
                Log.e(TAG, "detachAndDisconnect failed", e);
            }
        }
        try {
            unbindService(this.mAmbientServiceConnection);
        } catch (IllegalArgumentException e2) {
        }
    }

    private boolean shouldDisableAmbientInTheaterMode() {
        Cursor query = getContentResolver().query(SettingsContract.DISABLE_AMBIENT_IN_THEATER_MODE_URI, null, null, null, null);
        if (query == null) {
            return false;
        }
        do {
            try {
                if (!query.moveToNext()) {
                    query.close();
                    return false;
                }
            } catch (Throwable th) {
                query.close();
            }
        } while (!"disable_ambient_in_theater_mode".equals(query.getString(0)));
        boolean z = query.getInt(1) == 1;
        query.close();
        return z;
    }

    private boolean shouldTouchWakeUpInTheaterMode() {
        Cursor query = getContentResolver().query(SettingsContract.THEATER_MODE_URI, null, null, null, null);
        if (query == null) {
            return false;
        }
        do {
            try {
                if (!query.moveToNext()) {
                    query.close();
                    return false;
                }
            } catch (Throwable th) {
                query.close();
            }
        } while (!"theater_mode_touch_to_wake".equals(query.getString(0)));
        boolean z = query.getInt(1) == 1;
        query.close();
        return z;
    }

    private void updateBrightness() {
        setDozeScreenBrightness(this.mBrightnessInterpolator.getValue(System.getInt(getContentResolver(), "screen_brightness", 0)));
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (!this.mTheaterModeOn || !this.mTouchWakeUpInTheaterMode || getDozeScreenState() != 1) {
            return super.dispatchTouchEvent(motionEvent);
        }
        if (motionEvent.getAction() == 0) {
            long uptimeMillis = SystemClock.uptimeMillis();
            if (uptimeMillis - this.mLastTouchTime > 5000) {
                this.mTheaterTouchCount = 0;
            }
            this.mTheaterTouchCount++;
            this.mLastTouchTime = uptimeMillis;
            if (this.mTheaterTouchCount == 3) {
                this.mTheaterTouchCount = 0;
                this.mLastTouchTime = 0;
                ((PowerManager) getSystemService("power")).wakeUp(SystemClock.uptimeMillis());
            }
        }
        return true;
    }

    protected void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(fileDescriptor, printWriter, strArr);
        try {
            IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
            indentingPrintWriter.println("Service: " + this.mAmbientServiceConnection);
            indentingPrintWriter.println("Aborted: " + this.mAbortedOnCreate);
            indentingPrintWriter.flush();
        } catch (Throwable th) {
            printWriter.println("caught exception while dumping" + th.getMessage());
        }
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getWindow().setBackgroundDrawable(null);
    }

    public void onCreate() {
        super.onCreate();
        this.mTouchWakeUpInTheaterMode = shouldTouchWakeUpInTheaterMode();
        this.mTheaterModeOn = Global.getInt(getContentResolver(), "theater_mode_on", 0) == 1;
        boolean z = this.mTouchWakeUpInTheaterMode && this.mTheaterModeOn;
        Intent registerReceiver = registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        boolean z2 = (registerReceiver == null || registerReceiver.getIntExtra("plugged", 0) == 0 || !SystemProperties.getBoolean("ro.ambient.enable_when_plugged", false)) ? false : true;
        if (!(z || z2)) {
            if (((PowerManager) getSystemService("power")).isPowerSaveMode()) {
                if (Log.isLoggable(TAG, 3)) {
                    Log.d(TAG, "Power save mode enabled, finishing.");
                }
                this.mAbortedOnCreate = true;
                finish();
                return;
            }
            this.mConfig = AmbientConfig.getInstance(this);
            if (!this.mConfig.isAmbientEnabled()) {
                if (Log.isLoggable(TAG, 3)) {
                    Log.d(TAG, "Ambient mode disabled by settings, finishing.");
                }
                this.mAbortedOnCreate = true;
                finish();
                return;
            }
        }
        this.mDisableAmbientInTheaterMode = shouldDisableAmbientInTheaterMode();
        this.mBrightnessInterpolator = new BrightnessInterpolator(getResources().getIntArray(2130837504), getResources().getInteger(17694806));
        setDebug(true);
        setInteractive(false);
        setFullscreen(true);
        setScreenBright(false);
        updateBrightness();
        setWindowless(!z);
        this.mDreaming = false;
        this.mStopped = false;
        this.mComponent = new AmbientComponent(new ComponentName(this, AmbientDream.class));
        this.mAmbientServiceConnection = new AmbientServiceConnection();
        this.mCallbacks = new Callbacks();
        registerReceiver = new Intent("com.google.android.wearable.action.BIND_AMBIACTIVE_DREAM").setPackage(getPackageName());
        if (bindService(registerReceiver, this.mAmbientServiceConnection, 1)) {
            if (!this.mPowerSaveModeReceiverRegistered) {
                registerReceiver(this.mPowerSaveModeReceiver, new IntentFilter("android.os.action.POWER_SAVE_MODE_CHANGED"));
                this.mPowerSaveModeReceiverRegistered = true;
            }
            if (!this.mEarlyFinishReceiverRegistered) {
                registerReceiver(this.mEarlyFinishReceiver, new IntentFilter("com.google.android.wearable.action.AMBIENT_FINISH"), "com.google.android.wearable.permission.STOP_AMBIENT_DREAM", null);
                this.mEarlyFinishReceiverRegistered = true;
            }
            int pluggedTimeoutMin = AmbientConfig.getInstance(this).getPluggedTimeoutMin();
            if (pluggedTimeoutMin != -1) {
                registerReceiver = registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
                if (!(registerReceiver == null || registerReceiver.getIntExtra("plugged", 0) == 0)) {
                    if (Log.isLoggable(TAG, 3)) {
                        Log.d(TAG, "entering ambient while plugged in; will be finished in " + pluggedTimeoutMin + " minutes.");
                    }
                    this.mAlarmManager = (AlarmManager) getSystemService("alarm");
                    this.mEarlyFinishPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent("com.google.android.wearable.action.AMBIENT_FINISH"), 134217728);
                    this.mAlarmManager.set(2, SystemClock.elapsedRealtime() + TimeUnit.MINUTES.toMillis((long) pluggedTimeoutMin), this.mEarlyFinishPendingIntent);
                }
            }
            sendBroadcast(new Intent("com.google.android.wearable.action.AMBIENT_STARTED"));
            return;
        }
        unbindService(this.mAmbientServiceConnection);
        throw new IllegalStateException("Could not bind to required service: " + registerReceiver);
    }

    public void onDestroy() {
        if (this.mAbortedOnCreate) {
            super.onDestroy();
            return;
        }
        detachAndDisconnect();
        sendBroadcast(new Intent("com.google.android.wearable.action.AMBIENT_STOPPED"), "com.google.android.wearable.permission.RECEIVE_AMBIENT_DREAM_STATE");
        if (this.mHandler != null) {
            this.mHandler.removeCallbacks(this.mTheaterModeRunnable);
        }
        if (this.mAlarmManager != null) {
            this.mAlarmManager.cancel(this.mEarlyFinishPendingIntent);
        }
        if (this.mEarlyFinishReceiverRegistered) {
            unregisterReceiver(this.mEarlyFinishReceiver);
            this.mEarlyFinishReceiverRegistered = false;
        }
        if (this.mPowerSaveModeReceiverRegistered) {
            unregisterReceiver(this.mPowerSaveModeReceiver);
            this.mPowerSaveModeReceiverRegistered = false;
        }
        super.onDestroy();
    }

    public void onDreamingStarted() {
        if (!this.mAbortedOnCreate && this.mAmbientServiceConnection.getService() != null) {
            try {
                this.mAmbientServiceConnection.getService().onDreamingStarted();
                this.mDreaming = true;
            } catch (RemoteException e) {
            }
        }
    }

    public void onDreamingStopped() {
        if (!this.mAbortedOnCreate) {
            this.mDreaming = false;
            detachAndDisconnect();
        }
    }
}
