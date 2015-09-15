package android.os;

import android.content.Context;
import android.util.Log;

public final class PowerManager {
    public static final int ACQUIRE_CAUSES_WAKEUP = 268435456;
    public static final String ACTION_POWER_SAVE_MODE_CHANGED = "android.os.action.POWER_SAVE_MODE_CHANGED";
    public static final String ACTION_POWER_SAVE_MODE_CHANGING = "android.os.action.POWER_SAVE_MODE_CHANGING";
    public static final String ACTION_SCREEN_BRIGHTNESS_BOOST_CHANGED = "android.os.action.SCREEN_BRIGHTNESS_BOOST_CHANGED";
    public static final int BRIGHTNESS_DEFAULT = -1;
    public static final int BRIGHTNESS_OFF = 0;
    public static final int BRIGHTNESS_ON = 255;
    public static final int DOZE_WAKE_LOCK = 64;
    public static final int DRAW_WAKE_LOCK = 128;
    public static final String EXTRA_POWER_SAVE_MODE = "mode";
    @Deprecated
    public static final int FULL_WAKE_LOCK = 26;
    public static final int GO_TO_SLEEP_FLAG_NO_DOZE = 1;
    public static final int GO_TO_SLEEP_REASON_APPLICATION = 0;
    public static final int GO_TO_SLEEP_REASON_DEVICE_ADMIN = 1;
    public static final int GO_TO_SLEEP_REASON_HDMI = 5;
    public static final int GO_TO_SLEEP_REASON_LID_SWITCH = 3;
    public static final int GO_TO_SLEEP_REASON_POWER_BUTTON = 4;
    public static final int GO_TO_SLEEP_REASON_SLEEP_BUTTON = 6;
    public static final int GO_TO_SLEEP_REASON_TIMEOUT = 2;
    public static final int ON_AFTER_RELEASE = 536870912;
    public static final int PARTIAL_WAKE_LOCK = 1;
    public static final int PROXIMITY_SCREEN_OFF_WAKE_LOCK = 32;
    public static final String REBOOT_RECOVERY = "recovery";
    public static final int RELEASE_FLAG_WAIT_FOR_NO_PROXIMITY = 1;
    @Deprecated
    public static final int SCREEN_BRIGHT_WAKE_LOCK = 10;
    @Deprecated
    public static final int SCREEN_DIM_WAKE_LOCK = 6;
    private static final String TAG = "PowerManager";
    public static final int UNIMPORTANT_FOR_LOGGING = 1073741824;
    public static final int USER_ACTIVITY_EVENT_BUTTON = 1;
    public static final int USER_ACTIVITY_EVENT_OTHER = 0;
    public static final int USER_ACTIVITY_EVENT_TOUCH = 2;
    public static final int USER_ACTIVITY_FLAG_INDIRECT = 2;
    public static final int USER_ACTIVITY_FLAG_NO_CHANGE_LIGHTS = 1;
    public static final int WAKE_LOCK_LEVEL_MASK = 65535;
    final Context mContext;
    final Handler mHandler;
    final IPowerManager mService;

    public final class WakeLock {
        private int mCount;
        private int mFlags;
        private boolean mHeld;
        private String mHistoryTag;
        private final String mPackageName;
        private boolean mRefCounted;
        private final Runnable mReleaser;
        private String mTag;
        private final IBinder mToken;
        private final String mTraceName;
        private WorkSource mWorkSource;

        WakeLock(int flags, String tag, String packageName) {
            this.mRefCounted = true;
            this.mReleaser = new Runnable() {
                public void run() {
                    WakeLock.this.release();
                }
            };
            this.mFlags = flags;
            this.mTag = tag;
            this.mPackageName = packageName;
            this.mToken = new Binder();
            this.mTraceName = "WakeLock (" + this.mTag + ")";
        }

        protected void finalize() throws Throwable {
            synchronized (this.mToken) {
                if (this.mHeld) {
                    Log.wtf(PowerManager.TAG, "WakeLock finalized while still held: " + this.mTag);
                    Trace.asyncTraceEnd(Trace.TRACE_TAG_POWER, this.mTraceName, PowerManager.USER_ACTIVITY_EVENT_OTHER);
                    try {
                        PowerManager.this.mService.releaseWakeLock(this.mToken, PowerManager.USER_ACTIVITY_EVENT_OTHER);
                    } catch (RemoteException e) {
                    }
                }
            }
        }

        public void setReferenceCounted(boolean value) {
            synchronized (this.mToken) {
                this.mRefCounted = value;
            }
        }

        public void acquire() {
            synchronized (this.mToken) {
                acquireLocked();
            }
        }

        public void acquire(long timeout) {
            synchronized (this.mToken) {
                acquireLocked();
                PowerManager.this.mHandler.postDelayed(this.mReleaser, timeout);
            }
        }

        private void acquireLocked() {
            if (this.mRefCounted) {
                int i = this.mCount;
                this.mCount = i + PowerManager.USER_ACTIVITY_FLAG_NO_CHANGE_LIGHTS;
                if (i != 0) {
                    return;
                }
            }
            PowerManager.this.mHandler.removeCallbacks(this.mReleaser);
            Trace.asyncTraceBegin(Trace.TRACE_TAG_POWER, this.mTraceName, PowerManager.USER_ACTIVITY_EVENT_OTHER);
            try {
                PowerManager.this.mService.acquireWakeLock(this.mToken, this.mFlags, this.mTag, this.mPackageName, this.mWorkSource, this.mHistoryTag);
            } catch (RemoteException e) {
            }
            this.mHeld = true;
        }

        public void release() {
            release(PowerManager.USER_ACTIVITY_EVENT_OTHER);
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void release(int r6) {
            /*
            r5 = this;
            r1 = r5.mToken;
            monitor-enter(r1);
            r0 = r5.mRefCounted;	 Catch:{ all -> 0x0050 }
            if (r0 == 0) goto L_0x000f;
        L_0x0007:
            r0 = r5.mCount;	 Catch:{ all -> 0x0050 }
            r0 = r0 + -1;
            r5.mCount = r0;	 Catch:{ all -> 0x0050 }
            if (r0 != 0) goto L_0x0031;
        L_0x000f:
            r0 = android.os.PowerManager.this;	 Catch:{ all -> 0x0050 }
            r0 = r0.mHandler;	 Catch:{ all -> 0x0050 }
            r2 = r5.mReleaser;	 Catch:{ all -> 0x0050 }
            r0.removeCallbacks(r2);	 Catch:{ all -> 0x0050 }
            r0 = r5.mHeld;	 Catch:{ all -> 0x0050 }
            if (r0 == 0) goto L_0x0031;
        L_0x001c:
            r2 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
            r0 = r5.mTraceName;	 Catch:{ all -> 0x0050 }
            r4 = 0;
            android.os.Trace.asyncTraceEnd(r2, r0, r4);	 Catch:{ all -> 0x0050 }
            r0 = android.os.PowerManager.this;	 Catch:{ RemoteException -> 0x0055 }
            r0 = r0.mService;	 Catch:{ RemoteException -> 0x0055 }
            r2 = r5.mToken;	 Catch:{ RemoteException -> 0x0055 }
            r0.releaseWakeLock(r2, r6);	 Catch:{ RemoteException -> 0x0055 }
        L_0x002e:
            r0 = 0;
            r5.mHeld = r0;	 Catch:{ all -> 0x0050 }
        L_0x0031:
            r0 = r5.mCount;	 Catch:{ all -> 0x0050 }
            if (r0 >= 0) goto L_0x0053;
        L_0x0035:
            r0 = new java.lang.RuntimeException;	 Catch:{ all -> 0x0050 }
            r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0050 }
            r2.<init>();	 Catch:{ all -> 0x0050 }
            r3 = "WakeLock under-locked ";
            r2 = r2.append(r3);	 Catch:{ all -> 0x0050 }
            r3 = r5.mTag;	 Catch:{ all -> 0x0050 }
            r2 = r2.append(r3);	 Catch:{ all -> 0x0050 }
            r2 = r2.toString();	 Catch:{ all -> 0x0050 }
            r0.<init>(r2);	 Catch:{ all -> 0x0050 }
            throw r0;	 Catch:{ all -> 0x0050 }
        L_0x0050:
            r0 = move-exception;
            monitor-exit(r1);	 Catch:{ all -> 0x0050 }
            throw r0;
        L_0x0053:
            monitor-exit(r1);	 Catch:{ all -> 0x0050 }
            return;
        L_0x0055:
            r0 = move-exception;
            goto L_0x002e;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.os.PowerManager.WakeLock.release(int):void");
        }

        public boolean isHeld() {
            boolean z;
            synchronized (this.mToken) {
                z = this.mHeld;
            }
            return z;
        }

        public void setWorkSource(WorkSource ws) {
            synchronized (this.mToken) {
                boolean changed;
                if (ws != null) {
                    if (ws.size() == 0) {
                        ws = null;
                    }
                }
                if (ws == null) {
                    changed = this.mWorkSource != null;
                    this.mWorkSource = null;
                } else if (this.mWorkSource == null) {
                    changed = true;
                    this.mWorkSource = new WorkSource(ws);
                } else {
                    changed = this.mWorkSource.diff(ws);
                    if (changed) {
                        this.mWorkSource.set(ws);
                    }
                }
                if (changed && this.mHeld) {
                    try {
                        PowerManager.this.mService.updateWakeLockWorkSource(this.mToken, this.mWorkSource, this.mHistoryTag);
                    } catch (RemoteException e) {
                    }
                }
            }
        }

        public void setTag(String tag) {
            this.mTag = tag;
        }

        public void setHistoryTag(String tag) {
            this.mHistoryTag = tag;
        }

        public void setUnimportantForLogging(boolean state) {
            if (state) {
                this.mFlags |= PowerManager.UNIMPORTANT_FOR_LOGGING;
            } else {
                this.mFlags &= -1073741825;
            }
        }

        public String toString() {
            String str;
            synchronized (this.mToken) {
                str = "WakeLock{" + Integer.toHexString(System.identityHashCode(this)) + " held=" + this.mHeld + ", refCount=" + this.mCount + "}";
            }
            return str;
        }
    }

    public PowerManager(Context context, IPowerManager service, Handler handler) {
        this.mContext = context;
        this.mService = service;
        this.mHandler = handler;
    }

    public int getMinimumScreenBrightnessSetting() {
        return this.mContext.getResources().getInteger(17694803);
    }

    public int getMaximumScreenBrightnessSetting() {
        return this.mContext.getResources().getInteger(17694804);
    }

    public int getDefaultScreenBrightnessSetting() {
        return this.mContext.getResources().getInteger(17694805);
    }

    public static boolean useTwilightAdjustmentFeature() {
        return SystemProperties.getBoolean("persist.power.usetwilightadj", false);
    }

    public WakeLock newWakeLock(int levelAndFlags, String tag) {
        validateWakeLockParameters(levelAndFlags, tag);
        return new WakeLock(levelAndFlags, tag, this.mContext.getOpPackageName());
    }

    public static void validateWakeLockParameters(int levelAndFlags, String tag) {
        switch (WAKE_LOCK_LEVEL_MASK & levelAndFlags) {
            case USER_ACTIVITY_FLAG_NO_CHANGE_LIGHTS /*1*/:
            case SCREEN_DIM_WAKE_LOCK /*6*/:
            case SCREEN_BRIGHT_WAKE_LOCK /*10*/:
            case FULL_WAKE_LOCK /*26*/:
            case PROXIMITY_SCREEN_OFF_WAKE_LOCK /*32*/:
            case DOZE_WAKE_LOCK /*64*/:
            case DRAW_WAKE_LOCK /*128*/:
                if (tag == null) {
                    throw new IllegalArgumentException("The tag must not be null.");
                }
            default:
                throw new IllegalArgumentException("Must specify a valid wake lock level.");
        }
    }

    @Deprecated
    public void userActivity(long when, boolean noChangeLights) {
        int i;
        if (noChangeLights) {
            i = USER_ACTIVITY_FLAG_NO_CHANGE_LIGHTS;
        } else {
            i = USER_ACTIVITY_EVENT_OTHER;
        }
        userActivity(when, USER_ACTIVITY_EVENT_OTHER, i);
    }

    public void userActivity(long when, int event, int flags) {
        try {
            this.mService.userActivity(when, event, flags);
        } catch (RemoteException e) {
        }
    }

    public void goToSleep(long time) {
        goToSleep(time, USER_ACTIVITY_EVENT_OTHER, USER_ACTIVITY_EVENT_OTHER);
    }

    public void goToSleep(long time, int reason, int flags) {
        try {
            this.mService.goToSleep(time, reason, flags);
        } catch (RemoteException e) {
        }
    }

    public void wakeUp(long time) {
        try {
            this.mService.wakeUp(time);
        } catch (RemoteException e) {
        }
    }

    public void nap(long time) {
        try {
            this.mService.nap(time);
        } catch (RemoteException e) {
        }
    }

    public void boostScreenBrightness(long time) {
        try {
            this.mService.boostScreenBrightness(time);
        } catch (RemoteException e) {
        }
    }

    public boolean isScreenBrightnessBoosted() {
        try {
            return this.mService.isScreenBrightnessBoosted();
        } catch (RemoteException e) {
            return false;
        }
    }

    public void setBacklightBrightness(int brightness) {
        try {
            this.mService.setTemporaryScreenBrightnessSettingOverride(brightness);
        } catch (RemoteException e) {
        }
    }

    public boolean isWakeLockLevelSupported(int level) {
        try {
            return this.mService.isWakeLockLevelSupported(level);
        } catch (RemoteException e) {
            return false;
        }
    }

    @Deprecated
    public boolean isScreenOn() {
        return isInteractive();
    }

    public boolean isInteractive() {
        try {
            return this.mService.isInteractive();
        } catch (RemoteException e) {
            return false;
        }
    }

    public void reboot(String reason) {
        try {
            this.mService.reboot(false, reason, true);
        } catch (RemoteException e) {
        }
    }

    public boolean isPowerSaveMode() {
        try {
            return this.mService.isPowerSaveMode();
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean setPowerSaveMode(boolean mode) {
        try {
            return this.mService.setPowerSaveMode(mode);
        } catch (RemoteException e) {
            return false;
        }
    }

    public void shutdown(boolean confirm, boolean wait) {
        try {
            this.mService.shutdown(confirm, wait);
        } catch (RemoteException e) {
        }
    }
}
