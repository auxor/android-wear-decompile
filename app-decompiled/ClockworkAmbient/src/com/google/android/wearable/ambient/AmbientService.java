package com.google.android.wearable.ambient;

import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.util.Slog;
import com.android.internal.util.IndentingPrintWriter;
import com.google.android.clockwork.settings.AmbientConfig;
import com.google.android.clockwork.settings.BurnInConfig;
import com.google.android.wearable.ambient.activity.IAmbientActivityCallbacks;
import com.google.android.wearable.ambient.activity.IAmbientActivityRegistrationService.Stub;
import com.google.android.wearable.ambient.activity.IAmbientActivityService;
import com.google.android.wearable.ambient.dream.IAmbientDreamCallbacks;
import com.google.android.wearable.ambient.dream.IAmbientDreamRegistrationService;
import com.google.android.wearable.ambient.dream.IAmbientDreamService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AmbientService extends Service {
    private static final long ACTIVITY_LAUNCH_WAKE_LOCK_TIMEOUT_MS;
    private static final long WAKEUP_INTERVAL_MS;
    private List<AmbientActivity> mActivities;
    private final Object mActivitiesLock;
    private boolean mActivityRunningWhenDreamingStarted;
    private AlarmManager mAlarmManager;
    private AmbientConfig mAmbientConfig;
    private boolean mAmbientStarted;
    private AppOpsManager mAppOps;
    private BurnInConfig mBurnInConfig;
    private IAmbientDreamCallbacks mCurrentDream;
    private final Object mCurrentDreamLock;
    private WakeLock mDefaultActivityWakeLock;
    private boolean mDoOnCreateCalled;
    private boolean mDozeCommandSent;
    private Bundle mLastAmbientDetails;
    private AmbientDreamStub mLastAmbientDreamStub;
    private BroadcastReceiver mScreenOffReceiver;
    private boolean mScreenOffRegistered;
    private PendingIntent mTickAlarmIntent;
    private long mTickAlarmNextTriggerMillis;
    private boolean mTickAlarmRegistered;
    private final BroadcastReceiver mUpdateClockReceiver;

    final class AmbientActivity implements DeathRecipient {
        final AmbientComponent component;
        private boolean mAmbient;
        private final IAmbientActivityCallbacks mCallbacks;
        private AmbientConfiguration mConfiguration;
        private final Object mConfigurationLock;
        private boolean mEnterAmbientConfirmed;
        private boolean mRunning;
        private final Object mStateLock;

        public AmbientActivity(AmbientComponent ambientComponent, IAmbientActivityCallbacks iAmbientActivityCallbacks) {
            this.mStateLock = new Object();
            this.mEnterAmbientConfirmed = false;
            this.mConfigurationLock = new Object();
            this.component = ambientComponent;
            this.mCallbacks = iAmbientActivityCallbacks;
            this.mConfiguration = new AmbientConfiguration();
            throw new VerifyError("bad dex opcode");
        }

        public void binderDied() {
            if (Log.isLoggable("AmbientService", 3)) {
                Log.d("AmbientService", "binderDied: " + this);
            }
            detach();
        }

        public void detach() {
            if (Log.isLoggable("AmbientService", 3)) {
                Log.d("AmbientService", "detach: " + this + " " + AmbientService.this.getThreadInfo());
            }
            synchronized (AmbientService.this.mActivitiesLock) {
                AmbientService.this.mActivities.remove(this);
                if (AmbientService.this.mAmbientStarted && !AmbientService.this.mDozeCommandSent) {
                    AmbientService.this.maybeSendDozeCommand();
                }
            }
        }

        public void dumpState(IndentingPrintWriter indentingPrintWriter) {
            indentingPrintWriter.println(this.component.getComponent());
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.println("Callbacks: @" + Integer.toHexString(this.mCallbacks.hashCode()));
            indentingPrintWriter.println("Running: " + isRunning());
            indentingPrintWriter.println("Ambient: " + isAmbient());
            indentingPrintWriter.decreaseIndent();
        }

        public void enterAmbient(Bundle bundle) {
            try {
                if (Log.isLoggable("AmbientService", 3)) {
                    Log.d("AmbientService", "enterAmbient: " + this + " " + AmbientService.this.getThreadInfo());
                }
                synchronized (this.mStateLock) {
                    this.mAmbient = true;
                    this.mEnterAmbientConfirmed = false;
                    this.mCallbacks.enterAmbient(bundle);
                }
            } catch (Throwable e) {
                Log.w("AmbientService", "enterAmbient: Unable to enterAmbient: " + this, e);
            }
        }

        public void exitAmbient() {
            try {
                if (Log.isLoggable("AmbientService", 3)) {
                    Log.d("AmbientService", "exitAmbient: " + this + " " + AmbientService.this.getThreadInfo());
                }
                synchronized (this.mStateLock) {
                    this.mCallbacks.exitAmbient();
                    this.mAmbient = false;
                }
            } catch (Throwable e) {
                Log.w("AmbientService", "exitAmbient: Unable to exitAmbient: " + this, e);
            }
        }

        public boolean isAmbient() {
            boolean z;
            synchronized (this.mStateLock) {
                z = this.mAmbient;
            }
            return z;
        }

        public boolean isAmbientEnabled() {
            boolean isAmbientEnabled;
            synchronized (this.mConfigurationLock) {
                isAmbientEnabled = this.mConfiguration.isAmbientEnabled();
            }
            return isAmbientEnabled;
        }

        public boolean isEnterAmbientConfirmed() {
            boolean z;
            synchronized (this.mStateLock) {
                z = this.mEnterAmbientConfirmed;
            }
            return z;
        }

        public boolean isRunning() {
            boolean z;
            synchronized (this.mStateLock) {
                z = this.mRunning;
            }
            return z;
        }

        public void onAmbientEntered() {
            if (Log.isLoggable("AmbientService", 3)) {
                Log.d("AmbientService", "onAmbientEntered: " + this + AmbientService.this.getThreadInfo());
            }
            this.mEnterAmbientConfirmed = true;
            AmbientService.this.onAmbientEntered(this);
        }

        public void onAmbientExited() {
            if (Log.isLoggable("AmbientService", 3)) {
                Log.d("AmbientService", "onAmbientExited: " + this + " " + AmbientService.this.getThreadInfo());
            }
            IAmbientDreamCallbacks access$2100 = AmbientService.this.getCurrentDream();
            if (access$2100 == null) {
                Log.w("AmbientService", "onAmbientExited: Unable to call exitAmbient. No dream");
                return;
            }
            try {
                access$2100.exitAmbient();
            } catch (Throwable e) {
                Log.w("AmbientService", "onExitAmbient: Unable to call exitAmbient for: " + access$2100, e);
            }
        }

        public void onAmbientUpdated() {
            if (Log.isLoggable("AmbientService", 3)) {
                Log.d("AmbientService", "onAmbientUpdated: " + this + AmbientService.this.getThreadInfo());
            }
        }

        public void onPaused() {
            if (Log.isLoggable("AmbientService", 3)) {
                Log.d("AmbientService", "onPaused: " + this + " " + AmbientService.this.getThreadInfo());
            }
            synchronized (this.mStateLock) {
                this.mRunning = false;
            }
        }

        public void onResumed() {
            if (Log.isLoggable("AmbientService", 3)) {
                Log.d("AmbientService", "onResumed: " + this + " " + AmbientService.this.getThreadInfo());
            }
            synchronized (this.mStateLock) {
                this.mRunning = true;
            }
            AmbientService.this.onActivityResumed(this);
        }

        public void onStopped() {
            if (Log.isLoggable("AmbientService", 3)) {
                Log.d("AmbientService", "onStopped: " + this + " " + AmbientService.this.getThreadInfo());
            }
            synchronized (this.mStateLock) {
                this.mRunning = false;
            }
        }

        public void setConfiguration(Bundle bundle) {
            if (Log.isLoggable("AmbientService", 3)) {
                Log.d("AmbientService", "setConfiguration: " + this + AmbientService.this.getThreadInfo());
            }
            synchronized (this.mConfigurationLock) {
                this.mConfiguration = AmbientConfiguration.fromBundle(bundle);
            }
        }

        public String toString() {
            String str;
            synchronized (this.mStateLock) {
                str = "AmbientActivity[component=" + this.component.getComponent() + ", callbacks=@" + Integer.toHexString(this.mCallbacks.hashCode()) + ", running=" + this.mRunning + ", ambient=" + this.mAmbient + "]";
            }
            return str;
        }

        public void updateAmbient() {
            try {
                if (Log.isLoggable("AmbientService", 3)) {
                    Log.d("AmbientService", "updateAmbient: " + this + " " + AmbientService.this.getThreadInfo());
                }
                this.mCallbacks.updateAmbient();
            } catch (Throwable e) {
                Log.w("AmbientService", "updateAmbient: Unable to updateAmbient: " + this, e);
            }
        }
    }

    private class AmbientActivityRegistrationServiceStub extends Stub {
        private AmbientActivityRegistrationServiceStub() {
        }

        public IAmbientActivityService attach(Bundle bundle, IAmbientActivityCallbacks iAmbientActivityCallbacks) throws RemoteException {
            if (bundle == null || iAmbientActivityCallbacks == null) {
                Log.w("AmbientService", "Received a malformed attach request with bundle: " + bundle + " and callbacks: " + iAmbientActivityCallbacks);
                return null;
            }
            AmbientComponent ambientComponent = new AmbientComponent(bundle);
            AmbientService.this.mAppOps.checkPackage(getCallingUid(), ambientComponent.getComponent().getPackageName());
            DeathRecipient ambientActivity = new AmbientActivity(ambientComponent, iAmbientActivityCallbacks);
            AmbientServiceActivityStub ambientServiceActivityStub = new AmbientServiceActivityStub(ambientActivity);
            if (AmbientService.this.mAmbientStarted) {
                ambientActivity.enterAmbient(AmbientService.this.mLastAmbientDetails);
            }
            synchronized (AmbientService.this.mActivitiesLock) {
                AmbientService.this.mActivities.add(ambientActivity);
                iAmbientActivityCallbacks.asBinder().linkToDeath(ambientActivity, 0);
            }
            return ambientServiceActivityStub;
        }
    }

    private final class AmbientDreamRegistrationServiceStub extends IAmbientDreamRegistrationService.Stub {
        private AmbientDreamRegistrationServiceStub() {
            throw new VerifyError("bad dex opcode");
        }

        public IAmbientDreamService attach(Bundle bundle, IAmbientDreamCallbacks iAmbientDreamCallbacks) throws RemoteException {
            if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
                throw new IllegalStateException("AmbientDreamRegistrationServiceStub.attach called on the wrong thread: " + AmbientService.this.getThreadInfo());
            }
            AmbientComponent ambientComponent = new AmbientComponent(bundle);
            if (Log.isLoggable("AmbientService", 3)) {
                Log.d("AmbientService", "AmbientDreamRegistrationServiceStub.attach: dream: " + ambientComponent);
            }
            AmbientService.this.mAppOps.checkPackage(getCallingUid(), "com.google.android.wearable.ambient");
            synchronized (AmbientService.this.mCurrentDreamLock) {
                AmbientService.this.mCurrentDream = iAmbientDreamCallbacks;
            }
            if (AmbientService.this.mLastAmbientDreamStub != null) {
                AmbientService.this.mLastAmbientDreamStub.disable();
            }
            AmbientService.this.mLastAmbientDreamStub = new AmbientDreamStub(null);
            return AmbientService.this.mLastAmbientDreamStub;
        }
    }

    private final class AmbientDreamStub extends IAmbientDreamService.Stub {
        private boolean mEnabled;

        private AmbientDreamStub() {
            this.mEnabled = true;
            throw new VerifyError("bad dex opcode");
        }

        public void detach() {
            if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
                throw new IllegalStateException("AmbientDreamStub.detach called on the wrong thread: " + AmbientService.this.getThreadInfo());
            } else if (this.mEnabled) {
                AmbientService.this.doOnDreamingStopped();
            } else if (Log.isLoggable("AmbientService", 3)) {
                Log.d("AmbientService", "AmbientDreamStub.detach called while disabled");
            }
        }

        void disable() {
            this.mEnabled = false;
        }

        public void onCreate() {
            if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
                throw new IllegalStateException("AmbientDreamStub.onCreate called on the wrong thread: " + AmbientService.this.getThreadInfo());
            } else if (this.mEnabled) {
                AmbientService.this.doOnCreate();
            } else if (Log.isLoggable("AmbientService", 3)) {
                Log.d("AmbientService", "AmbientDreamStub.onCreate called while disabled");
            }
        }

        public void onDreamingStarted() {
            if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
                throw new IllegalStateException("AmbientDreamStub.onDreamingStarted called on the wrong thread: " + AmbientService.this.getThreadInfo());
            } else if (this.mEnabled) {
                AmbientService.this.doOnDreamingStarted();
            } else if (Log.isLoggable("AmbientService", 3)) {
                Log.d("AmbientService", "AmbientDreamStub.onDreamingStarted called while disabled");
            }
        }

        public void onDreamingStopped() {
            if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
                throw new IllegalStateException("AmbientDreamStub.onDreamingStopped called on the wrong thread: " + AmbientService.this.getThreadInfo());
            } else if (this.mEnabled) {
                AmbientService.this.doOnDreamingStopped();
            } else if (Log.isLoggable("AmbientService", 3)) {
                Log.d("AmbientService", "AmbientDreamStub.onDreamingStopped called while disabled");
            }
        }
    }

    private final class UpdateClockBroadcastReceiver extends BroadcastReceiver {
        private UpdateClockBroadcastReceiver() {
            throw new VerifyError("bad dex opcode");
        }

        public void onReceive(Context context, Intent intent) {
            if ("com.google.android.wearable.action.TICK".equals(intent.getAction())) {
                AmbientService.this.doUpdateAmbient();
            }
        }
    }

    static {
        WAKEUP_INTERVAL_MS = TimeUnit.SECONDS.toMillis(60);
        ACTIVITY_LAUNCH_WAKE_LOCK_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(5);
    }

    public AmbientService() {
        this.mUpdateClockReceiver = new UpdateClockBroadcastReceiver();
        this.mActivities = new LinkedList();
        this.mActivitiesLock = new Object();
        this.mCurrentDreamLock = new Object();
        this.mTickAlarmRegistered = false;
        this.mTickAlarmNextTriggerMillis = -1;
        this.mActivityRunningWhenDreamingStarted = true;
        this.mScreenOffReceiver = new BroadcastReceiver() {
            {
                throw new VerifyError("bad dex opcode");
            }

            public void onReceive(Context context, Intent intent) {
                if (Log.isLoggable("AmbientService", 3)) {
                    Log.d("AmbientService", "onReceive " + intent + " " + AmbientService.this.mAmbientConfig.isAmbientEnabled());
                }
                if (!AmbientService.this.mAmbientConfig.isAmbientEnabled()) {
                    synchronized (AmbientService.this.mActivitiesLock) {
                        if (!AmbientService.this.findRunningActivities()) {
                            AmbientService.this.launchDefaultActivity();
                        }
                    }
                }
            }
        };
        this.mScreenOffRegistered = false;
        throw new VerifyError("bad dex opcode");
    }

    private void doOnCreate() {
        if (!this.mDoOnCreateCalled) {
            this.mDoOnCreateCalled = true;
            this.mAmbientStarted = false;
            synchronized (this.mActivitiesLock) {
                if (findRunningActivities()) {
                    Slog.i("AmbientService", "Ambient Created");
                } else {
                    Slog.i("AmbientService", "Ambient Created: launching default");
                    launchDefaultActivity();
                }
            }
        }
    }

    private void doOnDreamingStarted() {
        if (!this.mDoOnCreateCalled) {
            doOnCreate();
        }
        if (this.mAmbientStarted) {
            Slog.w("AmbientService", "Starting while already ambient. Ignoring.");
            return;
        }
        Slog.i("AmbientService", "Ambient Starting");
        this.mAmbientStarted = true;
        scheduleNextWakeup();
        this.mLastAmbientDetails = AmbientDetails.create(this.mBurnInConfig, this.mAmbientConfig);
        synchronized (this.mActivitiesLock) {
            this.mActivityRunningWhenDreamingStarted = false;
            for (AmbientActivity ambientActivity : this.mActivities) {
                if (ambientActivity.isRunning()) {
                    ambientActivity.enterAmbient(this.mLastAmbientDetails);
                    this.mActivityRunningWhenDreamingStarted = true;
                }
            }
        }
    }

    private void doOnDreamingStopped() {
        if (this.mAmbientStarted) {
            Slog.i("AmbientService", "Ambient Stopping");
            this.mDoOnCreateCalled = false;
            this.mAmbientStarted = false;
            this.mAlarmManager.cancel(this.mTickAlarmIntent);
            this.mTickAlarmNextTriggerMillis = 0;
            if (this.mLastAmbientDreamStub != null) {
                this.mLastAmbientDreamStub.disable();
                this.mLastAmbientDreamStub = null;
            }
            synchronized (this.mActivitiesLock) {
                for (AmbientActivity ambientActivity : this.mActivities) {
                    if (ambientActivity.isAmbient()) {
                        ambientActivity.exitAmbient();
                    }
                }
            }
            return;
        }
        Slog.w("AmbientService", "Stopping while not ambient. Ignoring.");
    }

    private boolean findRunningActivities() {
        for (AmbientActivity ambientActivity : this.mActivities) {
            if (ambientActivity.isRunning() && ambientActivity.isAmbientEnabled()) {
                if (Log.isLoggable("AmbientService", 3)) {
                    Log.d("AmbientService", "findRunningActivities: found: " + ambientActivity);
                }
                return true;
            }
        }
        if (Log.isLoggable("AmbientService", 3)) {
            Log.d("AmbientService", "findRunningActivities: No activities found");
        }
        return false;
    }

    private IAmbientDreamCallbacks getCurrentDream() {
        synchronized (this.mCurrentDreamLock) {
            if (this.mCurrentDream == null) {
                return null;
            }
            IAmbientDreamCallbacks iAmbientDreamCallbacks = this.mCurrentDream;
            return iAmbientDreamCallbacks;
        }
    }

    private String getThreadInfo() {
        return Thread.currentThread().getName() + " " + Thread.currentThread().getId();
    }

    private void launchDefaultActivity() {
        Intent flags = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME").setFlags(872480768);
        this.mDefaultActivityWakeLock.acquire(ACTIVITY_LAUNCH_WAKE_LOCK_TIMEOUT_MS);
        startActivity(flags);
    }

    private void maybeSendDozeCommand() {
        synchronized (this.mActivitiesLock) {
            int size = this.mActivities.size() - 1;
            while (size >= 0) {
                AmbientActivity ambientActivity = (AmbientActivity) this.mActivities.get(size);
                if (!ambientActivity.isAmbient() || ambientActivity.isEnterAmbientConfirmed()) {
                    size--;
                } else {
                    return;
                }
            }
            this.mDozeCommandSent = true;
            if (this.mDefaultActivityWakeLock != null && this.mDefaultActivityWakeLock.isHeld()) {
                this.mDefaultActivityWakeLock.release();
            }
            IAmbientDreamCallbacks currentDream = getCurrentDream();
            if (currentDream == null) {
                Log.w("AmbientService", "onAmbientEntered: Unable to call doze. No dream");
                return;
            }
            try {
                currentDream.doze();
            } catch (Throwable e) {
                Log.w("AmbientService", "onAmbientEntered: Unable to call doze for: " + currentDream, e);
            }
        }
    }

    private void onActivityResumed(AmbientActivity ambientActivity) {
        synchronized (this.mActivitiesLock) {
            if (!(this.mActivityRunningWhenDreamingStarted || !ambientActivity.isRunning() || ambientActivity.isAmbient())) {
                ambientActivity.enterAmbient(AmbientDetails.create(this.mBurnInConfig, this.mAmbientConfig));
                this.mActivityRunningWhenDreamingStarted = true;
            }
        }
    }

    private void onAmbientEntered(AmbientActivity ambientActivity) {
        maybeSendDozeCommand();
    }

    private void scheduleNextWakeup() {
        long currentTimeMillis = System.currentTimeMillis();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long j = (currentTimeMillis - (currentTimeMillis % WAKEUP_INTERVAL_MS)) + WAKEUP_INTERVAL_MS;
        long j2 = (j - currentTimeMillis) + elapsedRealtime;
        if (Log.isLoggable("AmbientService", 3)) {
            Log.d("AmbientService", "scheduling next alarm, nowWall: " + currentTimeMillis + ", nextWall: " + j + ", nowElapsed: " + elapsedRealtime + ", nextElapsed: " + j2);
        }
        this.mAlarmManager.setExact(2, j2, this.mTickAlarmIntent);
    }

    void doUpdateAmbient() {
        if (this.mAmbientStarted) {
            Slog.i("AmbientService", "Ambient Updating");
            scheduleNextWakeup();
            synchronized (this.mActivitiesLock) {
                for (AmbientActivity ambientActivity : this.mActivities) {
                    if (ambientActivity.isRunning() && ambientActivity.isAmbient()) {
                        ambientActivity.updateAmbient();
                    }
                }
            }
            return;
        }
        Slog.w("AmbientService", "Updating while not ambient. Ignoring.");
    }

    protected void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        try {
            IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
            IAmbientDreamCallbacks currentDream = getCurrentDream();
            indentingPrintWriter.println("Dream: " + currentDream);
            if (currentDream != null) {
                indentingPrintWriter.increaseIndent();
                indentingPrintWriter.println("Alive: " + currentDream.asBinder().isBinderAlive());
                indentingPrintWriter.decreaseIndent();
            }
            indentingPrintWriter.println("Activities:");
            indentingPrintWriter.increaseIndent();
            synchronized (this.mActivitiesLock) {
                for (AmbientActivity dumpState : this.mActivities) {
                    dumpState.dumpState(indentingPrintWriter);
                }
            }
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println("Next Alarm: " + this.mTickAlarmNextTriggerMillis);
            indentingPrintWriter.flush();
        } catch (Throwable th) {
            printWriter.println("caught exception while dumping" + th.getMessage());
        }
    }

    public IBinder onBind(Intent intent) {
        return "com.google.android.wearable.action.BIND_AMBIACTIVE_ACTIVITY".equals(intent.getAction()) ? new AmbientActivityRegistrationServiceStub() : "com.google.android.wearable.action.BIND_AMBIACTIVE_DREAM".equals(intent.getAction()) ? new AmbientDreamRegistrationServiceStub() : null;
    }

    public void onCreate() {
        super.onCreate();
        this.mAppOps = (AppOpsManager) getSystemService("appops");
        this.mAlarmManager = (AlarmManager) getSystemService("alarm");
        this.mDefaultActivityWakeLock = ((PowerManager) getSystemService("power")).newWakeLock(1, getClass().getName() + "-activity-launch");
        this.mDefaultActivityWakeLock.setReferenceCounted(false);
        this.mBurnInConfig = BurnInConfig.newInstance(this);
        this.mBurnInConfig.register();
        this.mAmbientConfig = AmbientConfig.newInstance(this);
        this.mAmbientConfig.register();
        this.mDoOnCreateCalled = false;
        this.mAmbientStarted = false;
        this.mDozeCommandSent = false;
        this.mLastAmbientDetails = AmbientDetails.create(this.mBurnInConfig, this.mAmbientConfig);
        Intent intent = new Intent("com.google.android.wearable.action.TICK");
        intent.setPackage(getPackageName());
        this.mTickAlarmIntent = PendingIntent.getBroadcast(this, 0, intent, 134217728);
        registerReceiver(this.mUpdateClockReceiver, new IntentFilter("com.google.android.wearable.action.TICK"));
        this.mTickAlarmRegistered = true;
        registerReceiver(this.mScreenOffReceiver, new IntentFilter("android.intent.action.SCREEN_OFF"));
        this.mScreenOffRegistered = true;
    }

    public void onDestroy() {
        if (this.mScreenOffRegistered) {
            unregisterReceiver(this.mScreenOffReceiver);
        }
        if (this.mTickAlarmRegistered) {
            unregisterReceiver(this.mUpdateClockReceiver);
        }
        if (this.mDefaultActivityWakeLock != null && this.mDefaultActivityWakeLock.isHeld()) {
            this.mDefaultActivityWakeLock.release();
        }
        this.mBurnInConfig.release();
        this.mAmbientConfig.release();
        super.onDestroy();
    }
}
