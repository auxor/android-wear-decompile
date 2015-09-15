package android.app;

import android.R;
import android.app.IActivityManager.ContentProviderHolder;
import android.app.backup.BackupAgent;
import android.content.BroadcastReceiver;
import android.content.BroadcastReceiver.PendingResult;
import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.Context;
import android.content.IContentProvider;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageManager.Stub;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.res.AssetManager;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDebug;
import android.database.sqlite.SQLiteDebug.DbStats;
import android.database.sqlite.SQLiteDebug.PagerStats;
import android.ddm.DdmHandleAppName;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.hardware.display.DisplayManagerGlobal;
import android.net.ConnectivityManager;
import android.net.IConnectivityManager;
import android.net.Proxy;
import android.net.ProxyInfo;
import android.net.Uri;
import android.opengl.GLUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Debug.MemoryInfo;
import android.os.DropBoxManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue.IdleHandler;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.PersistableBundle;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.renderscript.RenderScript;
import android.security.AndroidKeyStoreProvider;
import android.service.notification.ZenModeConfig;
import android.telephony.PhoneNumberUtils;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.EventLog;
import android.util.Log;
import android.util.Pair;
import android.util.PrintWriterPrinter;
import android.util.Slog;
import android.util.SuperNotCalledException;
import android.view.HardwareRenderer;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewManager;
import android.view.ViewRootImpl;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerGlobal;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.content.ReferrerIntent;
import com.android.internal.os.BinderInternal;
import com.android.internal.os.RuntimeInit;
import com.android.internal.os.SamplingProfilerIntegration;
import com.android.internal.util.FastPrintWriter;
import com.android.org.conscrypt.OpenSSLSocketImpl;
import com.android.org.conscrypt.TrustedCertificateStore;
import com.google.android.collect.Lists;
import dalvik.system.CloseGuard;
import dalvik.system.VMDebug;
import dalvik.system.VMRuntime;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.security.Security;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Pattern;
import libcore.io.DropBox;
import libcore.io.DropBox.Reporter;
import libcore.io.EventLogger;
import libcore.io.IoUtils;
import libcore.net.event.NetworkEventDispatcher;

public final class ActivityThread {
    private static final int ACTIVITY_THREAD_CHECKIN_VERSION = 3;
    private static final boolean DEBUG_BACKUP = false;
    public static final boolean DEBUG_BROADCAST = false;
    public static final boolean DEBUG_CONFIGURATION = false;
    private static final boolean DEBUG_MEMORY_TRIM = false;
    static final boolean DEBUG_MESSAGES = false;
    private static final boolean DEBUG_PROVIDER = false;
    private static final boolean DEBUG_RESULTS = false;
    private static final boolean DEBUG_SERVICE = false;
    private static final String HEAP_COLUMN = "%13s %8s %8s %8s %8s %8s %8s %8s";
    private static final String HEAP_FULL_COLUMN = "%13s %8s %8s %8s %8s %8s %8s %8s %8s %8s %8s";
    private static final int LOG_ON_PAUSE_CALLED = 30021;
    private static final int LOG_ON_RESUME_CALLED = 30022;
    private static final long MIN_TIME_BETWEEN_GCS = 5000;
    private static final Pattern PATTERN_SEMICOLON;
    public static final int SERVICE_DONE_EXECUTING_ANON = 0;
    public static final int SERVICE_DONE_EXECUTING_START = 1;
    public static final int SERVICE_DONE_EXECUTING_STOP = 2;
    private static final int SQLITE_MEM_RELEASED_EVENT_LOG_TAG = 75003;
    public static final String TAG = "ActivityThread";
    private static final Config THUMBNAIL_FORMAT;
    static final boolean localLOGV = false;
    private static ActivityThread sCurrentActivityThread;
    private static final ThreadLocal<Intent> sCurrentBroadcastIntent;
    static Handler sMainThreadHandler;
    static IPackageManager sPackageManager;
    final ArrayMap<IBinder, ActivityClientRecord> mActivities;
    final ArrayList<Application> mAllApplications;
    final ApplicationThread mAppThread;
    private Bitmap mAvailThumbnailBitmap;
    final ArrayMap<String, BackupAgent> mBackupAgents;
    AppBindData mBoundApplication;
    Configuration mCompatConfiguration;
    Configuration mConfiguration;
    Bundle mCoreSettings;
    int mCurDefaultDisplayDpi;
    boolean mDensityCompatMode;
    final GcIdler mGcIdler;
    boolean mGcIdlerScheduled;
    final H mH;
    Application mInitialApplication;
    Instrumentation mInstrumentation;
    String mInstrumentationAppDir;
    String mInstrumentationLibDir;
    String mInstrumentationPackageName;
    String[] mInstrumentationSplitAppDirs;
    String mInstrumentedAppDir;
    String mInstrumentedLibDir;
    String[] mInstrumentedSplitAppDirs;
    boolean mJitEnabled;
    final ArrayMap<IBinder, ProviderClientRecord> mLocalProviders;
    final ArrayMap<ComponentName, ProviderClientRecord> mLocalProvidersByName;
    final Looper mLooper;
    private Configuration mMainThreadConfig;
    ActivityClientRecord mNewActivities;
    int mNumVisibleActivities;
    final ArrayMap<Activity, ArrayList<OnActivityPausedListener>> mOnPauseListeners;
    final ArrayMap<String, WeakReference<LoadedApk>> mPackages;
    Configuration mPendingConfiguration;
    Profiler mProfiler;
    final ArrayMap<ProviderKey, ProviderClientRecord> mProviderMap;
    final ArrayMap<IBinder, ProviderRefCount> mProviderRefCountMap;
    final ArrayList<ActivityClientRecord> mRelaunchingActivities;
    final ArrayMap<String, WeakReference<LoadedApk>> mResourcePackages;
    private final ResourcesManager mResourcesManager;
    final ArrayMap<IBinder, Service> mServices;
    boolean mSomeActivitiesChanged;
    private ContextImpl mSystemContext;
    boolean mSystemThread;
    private Canvas mThumbnailCanvas;
    private int mThumbnailHeight;
    private int mThumbnailWidth;

    /* renamed from: android.app.ActivityThread.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ IActivityManager val$mgr;

        AnonymousClass2(IActivityManager iActivityManager) {
            this.val$mgr = iActivityManager;
        }

        public void run() {
            if (ActivityThread.this.mSomeActivitiesChanged) {
                Runtime runtime = Runtime.getRuntime();
                if (runtime.totalMemory() - runtime.freeMemory() > (3 * runtime.maxMemory()) / 4) {
                    ActivityThread.this.mSomeActivitiesChanged = ActivityThread.DEBUG_SERVICE;
                    try {
                        this.val$mgr.releaseSomeActivities(ActivityThread.this.mAppThread);
                    } catch (RemoteException e) {
                    }
                }
            }
        }
    }

    static final class ActivityClientRecord {
        Activity activity;
        ActivityInfo activityInfo;
        CompatibilityInfo compatInfo;
        Configuration createdConfig;
        String embeddedID;
        boolean hideForNow;
        int ident;
        Intent intent;
        boolean isForward;
        NonConfigurationInstances lastNonConfigurationInstances;
        View mPendingRemoveWindow;
        WindowManager mPendingRemoveWindowManager;
        Configuration newConfig;
        ActivityClientRecord nextIdle;
        boolean onlyLocalRequest;
        LoadedApk packageInfo;
        Activity parent;
        boolean paused;
        int pendingConfigChanges;
        List<ReferrerIntent> pendingIntents;
        List<ResultInfo> pendingResults;
        PersistableBundle persistentState;
        ProfilerInfo profilerInfo;
        String referrer;
        boolean startsNotResumed;
        Bundle state;
        boolean stopped;
        IBinder token;
        IVoiceInteractor voiceInteractor;
        Window window;

        ActivityClientRecord() {
            this.parent = null;
            this.embeddedID = null;
            this.paused = ActivityThread.DEBUG_SERVICE;
            this.stopped = ActivityThread.DEBUG_SERVICE;
            this.hideForNow = ActivityThread.DEBUG_SERVICE;
            this.nextIdle = null;
        }

        public boolean isPreHoneycomb() {
            if (this.activity == null || this.activity.getApplicationInfo().targetSdkVersion >= 11) {
                return ActivityThread.DEBUG_SERVICE;
            }
            return true;
        }

        public boolean isPersistable() {
            return this.activityInfo.persistableMode == ActivityThread.SERVICE_DONE_EXECUTING_STOP ? true : ActivityThread.DEBUG_SERVICE;
        }

        public String toString() {
            ComponentName componentName = this.intent != null ? this.intent.getComponent() : null;
            return "ActivityRecord{" + Integer.toHexString(System.identityHashCode(this)) + " token=" + this.token + " " + (componentName == null ? "no component name" : componentName.toShortString()) + "}";
        }
    }

    static final class AppBindData {
        ApplicationInfo appInfo;
        CompatibilityInfo compatInfo;
        Configuration config;
        int debugMode;
        boolean enableOpenGlTrace;
        LoadedApk info;
        ProfilerInfo initProfilerInfo;
        Bundle instrumentationArgs;
        ComponentName instrumentationName;
        IUiAutomationConnection instrumentationUiAutomationConnection;
        IInstrumentationWatcher instrumentationWatcher;
        boolean persistent;
        String processName;
        List<ProviderInfo> providers;
        boolean restrictedBackupMode;

        AppBindData() {
        }

        public String toString() {
            return "AppBindData{appInfo=" + this.appInfo + "}";
        }
    }

    private class ApplicationThread extends ApplicationThreadNative {
        private static final String DB_INFO_FORMAT = "  %8s %8s %14s %14s  %s";
        private static final String ONE_COUNT_COLUMN = "%21s %8d";
        private static final String TWO_COUNT_COLUMNS = "%21s %8d %21s %8d";
        private int mLastProcessState;

        private ApplicationThread() {
            this.mLastProcessState = -1;
        }

        private void updatePendingConfiguration(Configuration config) {
            synchronized (ActivityThread.this.mResourcesManager) {
                if (ActivityThread.this.mPendingConfiguration == null || ActivityThread.this.mPendingConfiguration.isOtherSeqNewer(config)) {
                    ActivityThread.this.mPendingConfiguration = config;
                }
            }
        }

        public final void schedulePauseActivity(IBinder token, boolean finished, boolean userLeaving, int configChanges, boolean dontReport) {
            int i = ActivityThread.SERVICE_DONE_EXECUTING_ANON;
            ActivityThread activityThread = ActivityThread.this;
            int i2 = finished ? KeyEvent.KEYCODE_BUTTON_L1 : KeyEvent.KEYCODE_BUTTON_Z;
            int i3 = userLeaving ? ActivityThread.SERVICE_DONE_EXECUTING_START : ActivityThread.SERVICE_DONE_EXECUTING_ANON;
            if (dontReport) {
                i = ActivityThread.SERVICE_DONE_EXECUTING_STOP;
            }
            activityThread.sendMessage(i2, token, i | i3, configChanges);
        }

        public final void scheduleStopActivity(IBinder token, boolean showWindow, int configChanges) {
            ActivityThread.this.sendMessage(showWindow ? KeyEvent.KEYCODE_BUTTON_R1 : KeyEvent.KEYCODE_BUTTON_L2, token, ActivityThread.SERVICE_DONE_EXECUTING_ANON, configChanges);
        }

        public final void scheduleWindowVisibility(IBinder token, boolean showWindow) {
            ActivityThread.this.sendMessage(showWindow ? KeyEvent.KEYCODE_BUTTON_R2 : KeyEvent.KEYCODE_BUTTON_THUMBL, token);
        }

        public final void scheduleSleeping(IBinder token, boolean sleeping) {
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_F7, token, sleeping ? ActivityThread.SERVICE_DONE_EXECUTING_START : ActivityThread.SERVICE_DONE_EXECUTING_ANON);
        }

        public final void scheduleResumeActivity(IBinder token, int processState, boolean isForward, Bundle resumeArgs) {
            int i = ActivityThread.SERVICE_DONE_EXECUTING_ANON;
            updateProcessState(processState, ActivityThread.DEBUG_SERVICE);
            ActivityThread activityThread = ActivityThread.this;
            if (isForward) {
                i = ActivityThread.SERVICE_DONE_EXECUTING_START;
            }
            activityThread.sendMessage(KeyEvent.KEYCODE_BUTTON_THUMBR, token, i);
        }

        public final void scheduleSendResult(IBinder token, List<ResultInfo> results) {
            ResultData res = new ResultData();
            res.token = token;
            res.results = results;
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_BUTTON_START, res);
        }

        public final void scheduleLaunchActivity(Intent intent, IBinder token, int ident, ActivityInfo info, Configuration curConfig, CompatibilityInfo compatInfo, String referrer, IVoiceInteractor voiceInteractor, int procState, Bundle state, PersistableBundle persistentState, List<ResultInfo> pendingResults, List<ReferrerIntent> pendingNewIntents, boolean notResumed, boolean isForward, ProfilerInfo profilerInfo) {
            updateProcessState(procState, ActivityThread.DEBUG_SERVICE);
            ActivityClientRecord r = new ActivityClientRecord();
            r.token = token;
            r.ident = ident;
            r.intent = intent;
            r.referrer = referrer;
            r.voiceInteractor = voiceInteractor;
            r.activityInfo = info;
            r.compatInfo = compatInfo;
            r.state = state;
            r.persistentState = persistentState;
            r.pendingResults = pendingResults;
            r.pendingIntents = pendingNewIntents;
            r.startsNotResumed = notResumed;
            r.isForward = isForward;
            r.profilerInfo = profilerInfo;
            updatePendingConfiguration(curConfig);
            ActivityThread.this.sendMessage(100, r);
        }

        public final void scheduleRelaunchActivity(IBinder token, List<ResultInfo> pendingResults, List<ReferrerIntent> pendingNewIntents, int configChanges, boolean notResumed, Configuration config) {
            ActivityThread.this.requestRelaunchActivity(token, pendingResults, pendingNewIntents, configChanges, notResumed, config, true);
        }

        public final void scheduleNewIntent(List<ReferrerIntent> intents, IBinder token) {
            NewIntentData data = new NewIntentData();
            data.intents = intents;
            data.token = token;
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_FORWARD_DEL, data);
        }

        public final void scheduleDestroyActivity(IBinder token, boolean finishing, int configChanges) {
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_BUTTON_SELECT, token, finishing ? ActivityThread.SERVICE_DONE_EXECUTING_START : ActivityThread.SERVICE_DONE_EXECUTING_ANON, configChanges);
        }

        public final void scheduleReceiver(Intent intent, ActivityInfo info, CompatibilityInfo compatInfo, int resultCode, String data, Bundle extras, boolean sync, int sendingUser, int processState) {
            updateProcessState(processState, ActivityThread.DEBUG_SERVICE);
            ReceiverData r = new ReceiverData(intent, resultCode, data, extras, sync, ActivityThread.DEBUG_SERVICE, ActivityThread.this.mAppThread.asBinder(), sendingUser);
            r.info = info;
            r.compatInfo = compatInfo;
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_CTRL_LEFT, r);
        }

        public final void scheduleCreateBackupAgent(ApplicationInfo app, CompatibilityInfo compatInfo, int backupMode) {
            CreateBackupAgentData d = new CreateBackupAgentData();
            d.appInfo = app;
            d.compatInfo = compatInfo;
            d.backupMode = backupMode;
            ActivityThread.this.sendMessage(AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS, d);
        }

        public final void scheduleDestroyBackupAgent(ApplicationInfo app, CompatibilityInfo compatInfo) {
            CreateBackupAgentData d = new CreateBackupAgentData();
            d.appInfo = app;
            d.compatInfo = compatInfo;
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_MEDIA_EJECT, d);
        }

        public final void scheduleCreateService(IBinder token, ServiceInfo info, CompatibilityInfo compatInfo, int processState) {
            updateProcessState(processState, ActivityThread.DEBUG_SERVICE);
            CreateServiceData s = new CreateServiceData();
            s.token = token;
            s.info = info;
            s.compatInfo = compatInfo;
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_CTRL_RIGHT, s);
        }

        public final void scheduleBindService(IBinder token, Intent intent, boolean rebind, int processState) {
            updateProcessState(processState, ActivityThread.DEBUG_SERVICE);
            BindServiceData s = new BindServiceData();
            s.token = token;
            s.intent = intent;
            s.rebind = rebind;
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_BREAK, s);
        }

        public final void scheduleUnbindService(IBinder token, Intent intent) {
            BindServiceData s = new BindServiceData();
            s.token = token;
            s.intent = intent;
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_MOVE_HOME, s);
        }

        public final void scheduleServiceArgs(IBinder token, boolean taskRemoved, int startId, int flags, Intent args) {
            ServiceArgsData s = new ServiceArgsData();
            s.token = token;
            s.taskRemoved = taskRemoved;
            s.startId = startId;
            s.flags = flags;
            s.args = args;
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_CAPS_LOCK, s);
        }

        public final void scheduleStopService(IBinder token) {
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_SCROLL_LOCK, token);
        }

        public final void bindApplication(String processName, ApplicationInfo appInfo, List<ProviderInfo> providers, ComponentName instrumentationName, ProfilerInfo profilerInfo, Bundle instrumentationArgs, IInstrumentationWatcher instrumentationWatcher, IUiAutomationConnection instrumentationUiConnection, int debugMode, boolean enableOpenGlTrace, boolean isRestrictedBackupMode, boolean persistent, Configuration config, CompatibilityInfo compatInfo, Map<String, IBinder> services, Bundle coreSettings) {
            if (services != null) {
                ServiceManager.initServiceCache(services);
            }
            setCoreSettings(coreSettings);
            PackageInfo pi = null;
            try {
                pi = ActivityThread.getPackageManager().getPackageInfo(appInfo.packageName, ActivityThread.SERVICE_DONE_EXECUTING_ANON, UserHandle.myUserId());
            } catch (RemoteException e) {
            }
            if (pi != null) {
                boolean sharedUserIdSet = pi.sharedUserId != null ? true : ActivityThread.DEBUG_SERVICE;
                boolean processNameNotDefault = (pi.applicationInfo == null || appInfo.packageName.equals(pi.applicationInfo.processName)) ? ActivityThread.DEBUG_SERVICE : true;
                boolean sharable = (sharedUserIdSet || processNameNotDefault) ? true : ActivityThread.DEBUG_SERVICE;
                if (!sharable) {
                    VMRuntime.registerAppInfo(appInfo.packageName, appInfo.dataDir, appInfo.processName);
                }
            }
            AppBindData data = new AppBindData();
            data.processName = processName;
            data.appInfo = appInfo;
            data.providers = providers;
            data.instrumentationName = instrumentationName;
            data.instrumentationArgs = instrumentationArgs;
            data.instrumentationWatcher = instrumentationWatcher;
            data.instrumentationUiAutomationConnection = instrumentationUiConnection;
            data.debugMode = debugMode;
            data.enableOpenGlTrace = enableOpenGlTrace;
            data.restrictedBackupMode = isRestrictedBackupMode;
            data.persistent = persistent;
            data.config = config;
            data.compatInfo = compatInfo;
            data.initProfilerInfo = profilerInfo;
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_BUTTON_MODE, data);
        }

        public final void scheduleExit() {
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_ESCAPE, null);
        }

        public final void scheduleSuicide() {
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_MEDIA_RECORD, null);
        }

        public void scheduleConfigurationChanged(Configuration config) {
            updatePendingConfiguration(config);
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_META_RIGHT, config);
        }

        public void updateTimeZone() {
            TimeZone.setDefault(null);
        }

        public void clearDnsCache() {
            InetAddress.clearDnsCache();
            NetworkEventDispatcher.getInstance().onNetworkConfigurationChanged();
        }

        public void setHttpProxy(String host, String port, String exclList, Uri pacFileUrl) {
            if (ConnectivityManager.getProcessDefaultNetwork() != null) {
                Proxy.setHttpProxySystemProperty(ConnectivityManager.from(ActivityThread.this.getSystemContext()).getDefaultProxy());
            } else {
                Proxy.setHttpProxySystemProperty(host, port, exclList, pacFileUrl);
            }
        }

        public void processInBackground() {
            ActivityThread.this.mH.removeMessages(KeyEvent.KEYCODE_SYSRQ);
            ActivityThread.this.mH.sendMessage(ActivityThread.this.mH.obtainMessage(KeyEvent.KEYCODE_SYSRQ));
        }

        public void dumpService(FileDescriptor fd, IBinder servicetoken, String[] args) {
            DumpComponentInfo data = new DumpComponentInfo();
            try {
                data.fd = ParcelFileDescriptor.dup(fd);
                data.token = servicetoken;
                data.args = args;
                ActivityThread.this.sendMessage(KeyEvent.KEYCODE_MOVE_END, data, ActivityThread.SERVICE_DONE_EXECUTING_ANON, ActivityThread.SERVICE_DONE_EXECUTING_ANON, true);
            } catch (IOException e) {
                Slog.w(ActivityThread.TAG, "dumpService failed", e);
            }
        }

        public void scheduleRegisteredReceiver(IIntentReceiver receiver, Intent intent, int resultCode, String dataStr, Bundle extras, boolean ordered, boolean sticky, int sendingUser, int processState) throws RemoteException {
            updateProcessState(processState, ActivityThread.DEBUG_SERVICE);
            receiver.performReceive(intent, resultCode, dataStr, extras, ordered, sticky, sendingUser);
        }

        public void scheduleLowMemory() {
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_INSERT, null);
        }

        public void scheduleActivityConfigurationChanged(IBinder token) {
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_FORWARD, token);
        }

        public void profilerControl(boolean start, ProfilerInfo profilerInfo, int profileType) {
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_MEDIA_PAUSE, profilerInfo, start ? ActivityThread.SERVICE_DONE_EXECUTING_START : ActivityThread.SERVICE_DONE_EXECUTING_ANON, profileType);
        }

        public void dumpHeap(boolean managed, String path, ParcelFileDescriptor fd) {
            int i;
            DumpHeapData dhd = new DumpHeapData();
            dhd.path = path;
            dhd.fd = fd;
            ActivityThread activityThread = ActivityThread.this;
            if (managed) {
                i = ActivityThread.SERVICE_DONE_EXECUTING_START;
            } else {
                i = ActivityThread.SERVICE_DONE_EXECUTING_ANON;
            }
            activityThread.sendMessage(KeyEvent.KEYCODE_F5, dhd, i, ActivityThread.SERVICE_DONE_EXECUTING_ANON, true);
        }

        public void setSchedulingGroup(int group) {
            try {
                Process.setProcessGroup(Process.myPid(), group);
            } catch (Exception e) {
                Slog.w(ActivityThread.TAG, "Failed setting process group to " + group, e);
            }
        }

        public void dispatchPackageBroadcast(int cmd, String[] packages) {
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_F3, packages, cmd);
        }

        public void scheduleCrash(String msg) {
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_F4, msg);
        }

        public void dumpActivity(FileDescriptor fd, IBinder activitytoken, String prefix, String[] args) {
            DumpComponentInfo data = new DumpComponentInfo();
            try {
                data.fd = ParcelFileDescriptor.dup(fd);
                data.token = activitytoken;
                data.prefix = prefix;
                data.args = args;
                ActivityThread.this.sendMessage(KeyEvent.KEYCODE_F6, data, ActivityThread.SERVICE_DONE_EXECUTING_ANON, ActivityThread.SERVICE_DONE_EXECUTING_ANON, true);
            } catch (IOException e) {
                Slog.w(ActivityThread.TAG, "dumpActivity failed", e);
            }
        }

        public void dumpProvider(FileDescriptor fd, IBinder providertoken, String[] args) {
            DumpComponentInfo data = new DumpComponentInfo();
            try {
                data.fd = ParcelFileDescriptor.dup(fd);
                data.token = providertoken;
                data.args = args;
                ActivityThread.this.sendMessage(KeyEvent.KEYCODE_F11, data, ActivityThread.SERVICE_DONE_EXECUTING_ANON, ActivityThread.SERVICE_DONE_EXECUTING_ANON, true);
            } catch (IOException e) {
                Slog.w(ActivityThread.TAG, "dumpProvider failed", e);
            }
        }

        public void dumpMemInfo(FileDescriptor fd, MemoryInfo mem, boolean checkin, boolean dumpFullInfo, boolean dumpDalvik, String[] args) {
            PrintWriter pw = new FastPrintWriter(new FileOutputStream(fd));
            try {
                dumpMemInfo(pw, mem, checkin, dumpFullInfo, dumpDalvik);
            } finally {
                pw.flush();
            }
        }

        private void dumpMemInfo(PrintWriter pw, MemoryInfo memInfo, boolean checkin, boolean dumpFullInfo, boolean dumpDalvik) {
            long nativeMax = Debug.getNativeHeapSize() / Trace.TRACE_TAG_CAMERA;
            long nativeAllocated = Debug.getNativeHeapAllocatedSize() / Trace.TRACE_TAG_CAMERA;
            long nativeFree = Debug.getNativeHeapFreeSize() / Trace.TRACE_TAG_CAMERA;
            Runtime runtime = Runtime.getRuntime();
            long dalvikMax = runtime.totalMemory() / Trace.TRACE_TAG_CAMERA;
            long dalvikFree = runtime.freeMemory() / Trace.TRACE_TAG_CAMERA;
            long dalvikAllocated = dalvikMax - dalvikFree;
            long viewInstanceCount = ViewDebug.getViewInstanceCount();
            long viewRootInstanceCount = ViewDebug.getViewRootImplCount();
            long appContextInstanceCount = Debug.countInstancesOfClass(ContextImpl.class);
            long activityInstanceCount = Debug.countInstancesOfClass(Activity.class);
            int globalAssetCount = AssetManager.getGlobalAssetCount();
            int globalAssetManagerCount = AssetManager.getGlobalAssetManagerCount();
            int binderLocalObjectCount = Debug.getBinderLocalObjectCount();
            int binderProxyObjectCount = Debug.getBinderProxyObjectCount();
            int binderDeathObjectCount = Debug.getBinderDeathObjectCount();
            long parcelSize = Parcel.getGlobalAllocSize();
            long parcelCount = Parcel.getGlobalAllocCount();
            long openSslSocketCount = Debug.countInstancesOfClass(OpenSSLSocketImpl.class);
            PagerStats stats = SQLiteDebug.getDatabaseInfo();
            ActivityThread.dumpMemInfoTable(pw, memInfo, checkin, dumpFullInfo, dumpDalvik, Process.myPid(), ActivityThread.this.mBoundApplication != null ? ActivityThread.this.mBoundApplication.processName : Environment.MEDIA_UNKNOWN, nativeMax, nativeAllocated, nativeFree, dalvikMax, dalvikAllocated, dalvikFree);
            int i;
            if (checkin) {
                pw.print(viewInstanceCount);
                pw.print(PhoneNumberUtils.PAUSE);
                pw.print(viewRootInstanceCount);
                pw.print(PhoneNumberUtils.PAUSE);
                pw.print(appContextInstanceCount);
                pw.print(PhoneNumberUtils.PAUSE);
                pw.print(activityInstanceCount);
                pw.print(PhoneNumberUtils.PAUSE);
                pw.print(globalAssetCount);
                pw.print(PhoneNumberUtils.PAUSE);
                pw.print(globalAssetManagerCount);
                pw.print(PhoneNumberUtils.PAUSE);
                pw.print(binderLocalObjectCount);
                pw.print(PhoneNumberUtils.PAUSE);
                pw.print(binderProxyObjectCount);
                pw.print(PhoneNumberUtils.PAUSE);
                pw.print(binderDeathObjectCount);
                pw.print(PhoneNumberUtils.PAUSE);
                pw.print(openSslSocketCount);
                pw.print(PhoneNumberUtils.PAUSE);
                pw.print(stats.memoryUsed / AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT);
                pw.print(PhoneNumberUtils.PAUSE);
                pw.print(stats.memoryUsed / AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT);
                pw.print(PhoneNumberUtils.PAUSE);
                pw.print(stats.pageCacheOverflow / AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT);
                pw.print(PhoneNumberUtils.PAUSE);
                pw.print(stats.largestMemAlloc / AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT);
                for (i = ActivityThread.SERVICE_DONE_EXECUTING_ANON; i < stats.dbStats.size(); i += ActivityThread.SERVICE_DONE_EXECUTING_START) {
                    DbStats dbStats = (DbStats) stats.dbStats.get(i);
                    pw.print(PhoneNumberUtils.PAUSE);
                    pw.print(dbStats.dbName);
                    pw.print(PhoneNumberUtils.PAUSE);
                    pw.print(dbStats.pageSize);
                    pw.print(PhoneNumberUtils.PAUSE);
                    pw.print(dbStats.dbSize);
                    pw.print(PhoneNumberUtils.PAUSE);
                    pw.print(dbStats.lookaside);
                    pw.print(PhoneNumberUtils.PAUSE);
                    pw.print(dbStats.cache);
                    pw.print(PhoneNumberUtils.PAUSE);
                    pw.print(dbStats.cache);
                }
                pw.println();
                return;
            }
            pw.println(" ");
            pw.println(" Objects");
            ActivityThread.printRow(pw, TWO_COUNT_COLUMNS, "Views:", Long.valueOf(viewInstanceCount), "ViewRootImpl:", Long.valueOf(viewRootInstanceCount));
            ActivityThread.printRow(pw, TWO_COUNT_COLUMNS, "AppContexts:", Long.valueOf(appContextInstanceCount), "Activities:", Long.valueOf(activityInstanceCount));
            ActivityThread.printRow(pw, TWO_COUNT_COLUMNS, "Assets:", Integer.valueOf(globalAssetCount), "AssetManagers:", Integer.valueOf(globalAssetManagerCount));
            ActivityThread.printRow(pw, TWO_COUNT_COLUMNS, "Local Binders:", Integer.valueOf(binderLocalObjectCount), "Proxy Binders:", Integer.valueOf(binderProxyObjectCount));
            ActivityThread.printRow(pw, TWO_COUNT_COLUMNS, "Parcel memory:", Long.valueOf(parcelSize / Trace.TRACE_TAG_CAMERA), "Parcel count:", Long.valueOf(parcelCount));
            ActivityThread.printRow(pw, TWO_COUNT_COLUMNS, "Death Recipients:", Integer.valueOf(binderDeathObjectCount), "OpenSSL Sockets:", Long.valueOf(openSslSocketCount));
            pw.println(" ");
            pw.println(" SQL");
            String str = ONE_COUNT_COLUMN;
            Object[] objArr = new Object[ActivityThread.SERVICE_DONE_EXECUTING_STOP];
            objArr[ActivityThread.SERVICE_DONE_EXECUTING_ANON] = "MEMORY_USED:";
            objArr[ActivityThread.SERVICE_DONE_EXECUTING_START] = Integer.valueOf(stats.memoryUsed / AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT);
            ActivityThread.printRow(pw, str, objArr);
            ActivityThread.printRow(pw, TWO_COUNT_COLUMNS, "PAGECACHE_OVERFLOW:", Integer.valueOf(stats.pageCacheOverflow / AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT), "MALLOC_SIZE:", Integer.valueOf(stats.largestMemAlloc / AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT));
            pw.println(" ");
            int N = stats.dbStats.size();
            if (N > 0) {
                pw.println(" DATABASES");
                ActivityThread.printRow(pw, DB_INFO_FORMAT, "pgsz", "dbsz", "Lookaside(b)", "cache", "Dbname");
                for (i = ActivityThread.SERVICE_DONE_EXECUTING_ANON; i < N; i += ActivityThread.SERVICE_DONE_EXECUTING_START) {
                    dbStats = (DbStats) stats.dbStats.get(i);
                    String str2 = DB_INFO_FORMAT;
                    Object[] objArr2 = new Object[5];
                    objArr2[ActivityThread.SERVICE_DONE_EXECUTING_ANON] = dbStats.pageSize > 0 ? String.valueOf(dbStats.pageSize) : " ";
                    objArr2[ActivityThread.SERVICE_DONE_EXECUTING_START] = dbStats.dbSize > 0 ? String.valueOf(dbStats.dbSize) : " ";
                    objArr2[ActivityThread.SERVICE_DONE_EXECUTING_STOP] = dbStats.lookaside > 0 ? String.valueOf(dbStats.lookaside) : " ";
                    objArr2[ActivityThread.ACTIVITY_THREAD_CHECKIN_VERSION] = dbStats.cache;
                    objArr2[4] = dbStats.dbName;
                    ActivityThread.printRow(pw, str2, objArr2);
                }
            }
            String assetAlloc = AssetManager.getAssetAllocations();
            if (assetAlloc != null) {
                pw.println(" ");
                pw.println(" Asset Allocations");
                pw.print(assetAlloc);
            }
        }

        public void dumpGfxInfo(FileDescriptor fd, String[] args) {
            ActivityThread.this.dumpGraphicsInfo(fd);
            WindowManagerGlobal.getInstance().dumpGfxInfo(fd);
        }

        public void dumpDbInfo(FileDescriptor fd, String[] args) {
            PrintWriter pw = new FastPrintWriter(new FileOutputStream(fd));
            SQLiteDebug.dump(new PrintWriterPrinter(pw), args);
            pw.flush();
        }

        public void unstableProviderDied(IBinder provider) {
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_F12, provider);
        }

        public void requestAssistContextExtras(IBinder activityToken, IBinder requestToken, int requestType) {
            RequestAssistContextExtras cmd = new RequestAssistContextExtras();
            cmd.activityToken = activityToken;
            cmd.requestToken = requestToken;
            cmd.requestType = requestType;
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_NUM_LOCK, cmd);
        }

        public void setCoreSettings(Bundle coreSettings) {
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_F8, coreSettings);
        }

        public void updatePackageCompatibilityInfo(String pkg, CompatibilityInfo info) {
            UpdateCompatibilityData ucd = new UpdateCompatibilityData();
            ucd.pkg = pkg;
            ucd.info = info;
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_F9, ucd);
        }

        public void scheduleTrimMemory(int level) {
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_F10, null, level);
        }

        public void scheduleTranslucentConversionComplete(IBinder token, boolean drawComplete) {
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_NUMPAD_0, token, drawComplete ? ActivityThread.SERVICE_DONE_EXECUTING_START : ActivityThread.SERVICE_DONE_EXECUTING_ANON);
        }

        public void scheduleOnNewActivityOptions(IBinder token, ActivityOptions options) {
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_NUMPAD_2, new Pair(token, options));
        }

        public void setProcessState(int state) {
            updateProcessState(state, true);
        }

        public void updateProcessState(int processState, boolean fromIpc) {
            synchronized (this) {
                if (this.mLastProcessState != processState) {
                    this.mLastProcessState = processState;
                    int dalvikProcessState = ActivityThread.SERVICE_DONE_EXECUTING_START;
                    if (processState <= ActivityThread.ACTIVITY_THREAD_CHECKIN_VERSION) {
                        dalvikProcessState = ActivityThread.SERVICE_DONE_EXECUTING_ANON;
                    }
                    VMRuntime.getRuntime().updateProcessState(dalvikProcessState);
                }
            }
        }

        public void scheduleInstallProvider(ProviderInfo provider) {
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_NUMPAD_1, provider);
        }

        public final void updateTimePrefs(boolean is24Hour) {
            DateFormat.set24HourTimePref(is24Hour);
        }

        public void scheduleCancelVisibleBehind(IBinder token) {
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_NUMPAD_3, token);
        }

        public void scheduleBackgroundVisibleBehindChanged(IBinder token, boolean visible) {
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_NUMPAD_4, token, visible ? ActivityThread.SERVICE_DONE_EXECUTING_START : ActivityThread.SERVICE_DONE_EXECUTING_ANON);
        }

        public void scheduleEnterAnimationComplete(IBinder token) {
            ActivityThread.this.sendMessage(KeyEvent.KEYCODE_NUMPAD_5, token);
        }
    }

    static final class BindServiceData {
        Intent intent;
        boolean rebind;
        IBinder token;

        BindServiceData() {
        }

        public String toString() {
            return "BindServiceData{token=" + this.token + " intent=" + this.intent + "}";
        }
    }

    static final class ContextCleanupInfo {
        ContextImpl context;
        String what;
        String who;

        ContextCleanupInfo() {
        }
    }

    static final class CreateBackupAgentData {
        ApplicationInfo appInfo;
        int backupMode;
        CompatibilityInfo compatInfo;

        public java.lang.String toString() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.ActivityThread.CreateBackupAgentData.toString():java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.ActivityThread.CreateBackupAgentData.toString():java.lang.String
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.app.ActivityThread.CreateBackupAgentData.toString():java.lang.String");
        }

        CreateBackupAgentData() {
        }
    }

    static final class CreateServiceData {
        CompatibilityInfo compatInfo;
        ServiceInfo info;
        Intent intent;
        IBinder token;

        CreateServiceData() {
        }

        public String toString() {
            return "CreateServiceData{token=" + this.token + " className=" + this.info.name + " packageName=" + this.info.packageName + " intent=" + this.intent + "}";
        }
    }

    private class DropBoxReporter implements Reporter {
        private DropBoxManager dropBox;

        public DropBoxReporter() {
            this.dropBox = (DropBoxManager) ActivityThread.this.getSystemContext().getSystemService(Context.DROPBOX_SERVICE);
        }

        public void addData(String tag, byte[] data, int flags) {
            this.dropBox.addData(tag, data, flags);
        }

        public void addText(String tag, String data) {
            this.dropBox.addText(tag, data);
        }
    }

    static final class DumpComponentInfo {
        String[] args;
        ParcelFileDescriptor fd;
        String prefix;
        IBinder token;

        DumpComponentInfo() {
        }
    }

    static final class DumpHeapData {
        ParcelFileDescriptor fd;
        String path;

        DumpHeapData() {
        }
    }

    private static class EventLoggingReporter implements EventLogger.Reporter {
        private EventLoggingReporter() {
        }

        public void report(int code, Object... list) {
            EventLog.writeEvent(code, list);
        }
    }

    final class GcIdler implements IdleHandler {
        GcIdler() {
        }

        public final boolean queueIdle() {
            ActivityThread.this.doGcIfNeeded();
            return ActivityThread.DEBUG_SERVICE;
        }
    }

    private class H extends Handler {
        public static final int ACTIVITY_CONFIGURATION_CHANGED = 125;
        public static final int BACKGROUND_VISIBLE_BEHIND_CHANGED = 148;
        public static final int BIND_APPLICATION = 110;
        public static final int BIND_SERVICE = 121;
        public static final int CANCEL_VISIBLE_BEHIND = 147;
        public static final int CLEAN_UP_CONTEXT = 119;
        public static final int CONFIGURATION_CHANGED = 118;
        public static final int CREATE_BACKUP_AGENT = 128;
        public static final int CREATE_SERVICE = 114;
        public static final int DESTROY_ACTIVITY = 109;
        public static final int DESTROY_BACKUP_AGENT = 129;
        public static final int DISPATCH_PACKAGE_BROADCAST = 133;
        public static final int DUMP_ACTIVITY = 136;
        public static final int DUMP_HEAP = 135;
        public static final int DUMP_PROVIDER = 141;
        public static final int DUMP_SERVICE = 123;
        public static final int ENABLE_JIT = 132;
        public static final int ENTER_ANIMATION_COMPLETE = 149;
        public static final int EXIT_APPLICATION = 111;
        public static final int GC_WHEN_IDLE = 120;
        public static final int HIDE_WINDOW = 106;
        public static final int INSTALL_PROVIDER = 145;
        public static final int LAUNCH_ACTIVITY = 100;
        public static final int LOW_MEMORY = 124;
        public static final int NEW_INTENT = 112;
        public static final int ON_NEW_ACTIVITY_OPTIONS = 146;
        public static final int PAUSE_ACTIVITY = 101;
        public static final int PAUSE_ACTIVITY_FINISHING = 102;
        public static final int PROFILER_CONTROL = 127;
        public static final int RECEIVER = 113;
        public static final int RELAUNCH_ACTIVITY = 126;
        public static final int REMOVE_PROVIDER = 131;
        public static final int REQUEST_ASSIST_CONTEXT_EXTRAS = 143;
        public static final int RESUME_ACTIVITY = 107;
        public static final int SCHEDULE_CRASH = 134;
        public static final int SEND_RESULT = 108;
        public static final int SERVICE_ARGS = 115;
        public static final int SET_CORE_SETTINGS = 138;
        public static final int SHOW_WINDOW = 105;
        public static final int SLEEPING = 137;
        public static final int STOP_ACTIVITY_HIDE = 104;
        public static final int STOP_ACTIVITY_SHOW = 103;
        public static final int STOP_SERVICE = 116;
        public static final int SUICIDE = 130;
        public static final int TRANSLUCENT_CONVERSION_COMPLETE = 144;
        public static final int TRIM_MEMORY = 140;
        public static final int UNBIND_SERVICE = 122;
        public static final int UNSTABLE_PROVIDER_DIED = 142;
        public static final int UPDATE_PACKAGE_COMPATIBILITY_INFO = 139;

        private H() {
        }

        String codeToString(int code) {
            return Integer.toString(code);
        }

        public void handleMessage(Message msg) {
            boolean z = ActivityThread.DEBUG_SERVICE;
            boolean z2 = true;
            ActivityThread activityThread;
            IBinder iBinder;
            boolean z3;
            ActivityThread activityThread2;
            IBinder iBinder2;
            switch (msg.what) {
                case LAUNCH_ACTIVITY /*100*/:
                    Trace.traceBegin(64, "activityStart");
                    ActivityClientRecord r = msg.obj;
                    r.packageInfo = ActivityThread.this.getPackageInfoNoCheck(r.activityInfo.applicationInfo, r.compatInfo);
                    ActivityThread.this.handleLaunchActivity(r, null);
                    Trace.traceEnd(64);
                case PAUSE_ACTIVITY /*101*/:
                    Trace.traceBegin(64, "activityPause");
                    activityThread = ActivityThread.this;
                    iBinder = (IBinder) msg.obj;
                    if ((msg.arg1 & ActivityThread.SERVICE_DONE_EXECUTING_START) != 0) {
                        z3 = true;
                    } else {
                        z3 = ActivityThread.DEBUG_SERVICE;
                    }
                    activityThread.handlePauseActivity(iBinder, ActivityThread.DEBUG_SERVICE, z3, msg.arg2, (msg.arg1 & ActivityThread.SERVICE_DONE_EXECUTING_STOP) != 0 ? true : ActivityThread.DEBUG_SERVICE);
                    maybeSnapshot();
                    Trace.traceEnd(64);
                case PAUSE_ACTIVITY_FINISHING /*102*/:
                    Trace.traceBegin(64, "activityPause");
                    activityThread = ActivityThread.this;
                    iBinder = (IBinder) msg.obj;
                    if ((msg.arg1 & ActivityThread.SERVICE_DONE_EXECUTING_START) != 0) {
                        z3 = true;
                    } else {
                        z3 = ActivityThread.DEBUG_SERVICE;
                    }
                    activityThread.handlePauseActivity(iBinder, true, z3, msg.arg2, (msg.arg1 & ActivityThread.SERVICE_DONE_EXECUTING_START) != 0 ? true : ActivityThread.DEBUG_SERVICE);
                    Trace.traceEnd(64);
                case STOP_ACTIVITY_SHOW /*103*/:
                    Trace.traceBegin(64, "activityStop");
                    ActivityThread.this.handleStopActivity((IBinder) msg.obj, true, msg.arg2);
                    Trace.traceEnd(64);
                case STOP_ACTIVITY_HIDE /*104*/:
                    Trace.traceBegin(64, "activityStop");
                    ActivityThread.this.handleStopActivity((IBinder) msg.obj, ActivityThread.DEBUG_SERVICE, msg.arg2);
                    Trace.traceEnd(64);
                case SHOW_WINDOW /*105*/:
                    Trace.traceBegin(64, "activityShowWindow");
                    ActivityThread.this.handleWindowVisibility((IBinder) msg.obj, true);
                    Trace.traceEnd(64);
                case HIDE_WINDOW /*106*/:
                    Trace.traceBegin(64, "activityHideWindow");
                    ActivityThread.this.handleWindowVisibility((IBinder) msg.obj, ActivityThread.DEBUG_SERVICE);
                    Trace.traceEnd(64);
                case RESUME_ACTIVITY /*107*/:
                    Trace.traceBegin(64, "activityResume");
                    activityThread2 = ActivityThread.this;
                    iBinder2 = (IBinder) msg.obj;
                    if (msg.arg1 != 0) {
                        z = true;
                    }
                    activityThread2.handleResumeActivity(iBinder2, true, z, true);
                    Trace.traceEnd(64);
                case SEND_RESULT /*108*/:
                    Trace.traceBegin(64, "activityDeliverResult");
                    ActivityThread.this.handleSendResult((ResultData) msg.obj);
                    Trace.traceEnd(64);
                case DESTROY_ACTIVITY /*109*/:
                    Trace.traceBegin(64, "activityDestroy");
                    activityThread2 = ActivityThread.this;
                    iBinder2 = (IBinder) msg.obj;
                    if (msg.arg1 == 0) {
                        z2 = ActivityThread.DEBUG_SERVICE;
                    }
                    activityThread2.handleDestroyActivity(iBinder2, z2, msg.arg2, ActivityThread.DEBUG_SERVICE);
                    Trace.traceEnd(64);
                case BIND_APPLICATION /*110*/:
                    Trace.traceBegin(64, "bindApplication");
                    ActivityThread.this.handleBindApplication(msg.obj);
                    Trace.traceEnd(64);
                case EXIT_APPLICATION /*111*/:
                    if (ActivityThread.this.mInitialApplication != null) {
                        ActivityThread.this.mInitialApplication.onTerminate();
                    }
                    Looper.myLooper().quit();
                case NEW_INTENT /*112*/:
                    Trace.traceBegin(64, "activityNewIntent");
                    ActivityThread.this.handleNewIntent((NewIntentData) msg.obj);
                    Trace.traceEnd(64);
                case RECEIVER /*113*/:
                    Trace.traceBegin(64, "broadcastReceiveComp");
                    ActivityThread.this.handleReceiver((ReceiverData) msg.obj);
                    maybeSnapshot();
                    Trace.traceEnd(64);
                case CREATE_SERVICE /*114*/:
                    Trace.traceBegin(64, "serviceCreate");
                    ActivityThread.this.handleCreateService((CreateServiceData) msg.obj);
                    Trace.traceEnd(64);
                case SERVICE_ARGS /*115*/:
                    Trace.traceBegin(64, "serviceStart");
                    ActivityThread.this.handleServiceArgs((ServiceArgsData) msg.obj);
                    Trace.traceEnd(64);
                case STOP_SERVICE /*116*/:
                    Trace.traceBegin(64, "serviceStop");
                    ActivityThread.this.handleStopService((IBinder) msg.obj);
                    maybeSnapshot();
                    Trace.traceEnd(64);
                case CONFIGURATION_CHANGED /*118*/:
                    Trace.traceBegin(64, "configChanged");
                    ActivityThread.this.mCurDefaultDisplayDpi = ((Configuration) msg.obj).densityDpi;
                    ActivityThread.this.handleConfigurationChanged((Configuration) msg.obj, null);
                    Trace.traceEnd(64);
                case CLEAN_UP_CONTEXT /*119*/:
                    ContextCleanupInfo cci = msg.obj;
                    cci.context.performFinalCleanup(cci.who, cci.what);
                case GC_WHEN_IDLE /*120*/:
                    ActivityThread.this.scheduleGcIdler();
                case BIND_SERVICE /*121*/:
                    Trace.traceBegin(64, "serviceBind");
                    ActivityThread.this.handleBindService((BindServiceData) msg.obj);
                    Trace.traceEnd(64);
                case UNBIND_SERVICE /*122*/:
                    Trace.traceBegin(64, "serviceUnbind");
                    ActivityThread.this.handleUnbindService((BindServiceData) msg.obj);
                    Trace.traceEnd(64);
                case DUMP_SERVICE /*123*/:
                    ActivityThread.this.handleDumpService((DumpComponentInfo) msg.obj);
                case LOW_MEMORY /*124*/:
                    Trace.traceBegin(64, "lowMemory");
                    ActivityThread.this.handleLowMemory();
                    Trace.traceEnd(64);
                case ACTIVITY_CONFIGURATION_CHANGED /*125*/:
                    Trace.traceBegin(64, "activityConfigChanged");
                    ActivityThread.this.handleActivityConfigurationChanged((IBinder) msg.obj);
                    Trace.traceEnd(64);
                case RELAUNCH_ACTIVITY /*126*/:
                    Trace.traceBegin(64, "activityRestart");
                    ActivityThread.this.handleRelaunchActivity((ActivityClientRecord) msg.obj);
                    Trace.traceEnd(64);
                case PROFILER_CONTROL /*127*/:
                    activityThread2 = ActivityThread.this;
                    if (msg.arg1 == 0) {
                        z2 = ActivityThread.DEBUG_SERVICE;
                    }
                    activityThread2.handleProfilerControl(z2, (ProfilerInfo) msg.obj, msg.arg2);
                case CREATE_BACKUP_AGENT /*128*/:
                    Trace.traceBegin(64, "backupCreateAgent");
                    ActivityThread.this.handleCreateBackupAgent((CreateBackupAgentData) msg.obj);
                    Trace.traceEnd(64);
                case DESTROY_BACKUP_AGENT /*129*/:
                    Trace.traceBegin(64, "backupDestroyAgent");
                    ActivityThread.this.handleDestroyBackupAgent((CreateBackupAgentData) msg.obj);
                    Trace.traceEnd(64);
                case SUICIDE /*130*/:
                    Process.killProcess(Process.myPid());
                case REMOVE_PROVIDER /*131*/:
                    Trace.traceBegin(64, "providerRemove");
                    ActivityThread.this.completeRemoveProvider((ProviderRefCount) msg.obj);
                    Trace.traceEnd(64);
                case ENABLE_JIT /*132*/:
                    ActivityThread.this.ensureJitEnabled();
                case DISPATCH_PACKAGE_BROADCAST /*133*/:
                    Trace.traceBegin(64, "broadcastPackage");
                    ActivityThread.this.handleDispatchPackageBroadcast(msg.arg1, (String[]) msg.obj);
                    Trace.traceEnd(64);
                case SCHEDULE_CRASH /*134*/:
                    throw new RemoteServiceException((String) msg.obj);
                case DUMP_HEAP /*135*/:
                    if (msg.arg1 == 0) {
                        z2 = ActivityThread.DEBUG_SERVICE;
                    }
                    ActivityThread.handleDumpHeap(z2, (DumpHeapData) msg.obj);
                case DUMP_ACTIVITY /*136*/:
                    ActivityThread.this.handleDumpActivity((DumpComponentInfo) msg.obj);
                case SLEEPING /*137*/:
                    Trace.traceBegin(64, "sleeping");
                    activityThread2 = ActivityThread.this;
                    iBinder2 = (IBinder) msg.obj;
                    if (msg.arg1 == 0) {
                        z2 = ActivityThread.DEBUG_SERVICE;
                    }
                    activityThread2.handleSleeping(iBinder2, z2);
                    Trace.traceEnd(64);
                case SET_CORE_SETTINGS /*138*/:
                    Trace.traceBegin(64, "setCoreSettings");
                    ActivityThread.this.handleSetCoreSettings((Bundle) msg.obj);
                    Trace.traceEnd(64);
                case UPDATE_PACKAGE_COMPATIBILITY_INFO /*139*/:
                    ActivityThread.this.handleUpdatePackageCompatibilityInfo((UpdateCompatibilityData) msg.obj);
                case TRIM_MEMORY /*140*/:
                    Trace.traceBegin(64, "trimMemory");
                    ActivityThread.this.handleTrimMemory(msg.arg1);
                    Trace.traceEnd(64);
                case DUMP_PROVIDER /*141*/:
                    ActivityThread.this.handleDumpProvider((DumpComponentInfo) msg.obj);
                case UNSTABLE_PROVIDER_DIED /*142*/:
                    ActivityThread.this.handleUnstableProviderDied((IBinder) msg.obj, ActivityThread.DEBUG_SERVICE);
                case REQUEST_ASSIST_CONTEXT_EXTRAS /*143*/:
                    ActivityThread.this.handleRequestAssistContextExtras((RequestAssistContextExtras) msg.obj);
                case TRANSLUCENT_CONVERSION_COMPLETE /*144*/:
                    activityThread2 = ActivityThread.this;
                    iBinder2 = (IBinder) msg.obj;
                    if (msg.arg1 != ActivityThread.SERVICE_DONE_EXECUTING_START) {
                        z2 = ActivityThread.DEBUG_SERVICE;
                    }
                    activityThread2.handleTranslucentConversionComplete(iBinder2, z2);
                case INSTALL_PROVIDER /*145*/:
                    ActivityThread.this.handleInstallProvider((ProviderInfo) msg.obj);
                case ON_NEW_ACTIVITY_OPTIONS /*146*/:
                    Pair<IBinder, ActivityOptions> pair = msg.obj;
                    ActivityThread.this.onNewActivityOptions((IBinder) pair.first, (ActivityOptions) pair.second);
                case CANCEL_VISIBLE_BEHIND /*147*/:
                    ActivityThread.this.handleCancelVisibleBehind((IBinder) msg.obj);
                case BACKGROUND_VISIBLE_BEHIND_CHANGED /*148*/:
                    activityThread2 = ActivityThread.this;
                    iBinder2 = (IBinder) msg.obj;
                    if (msg.arg1 <= 0) {
                        z2 = ActivityThread.DEBUG_SERVICE;
                    }
                    activityThread2.handleOnBackgroundVisibleBehindChanged(iBinder2, z2);
                case ENTER_ANIMATION_COMPLETE /*149*/:
                    ActivityThread.this.handleEnterAnimationComplete((IBinder) msg.obj);
                default:
            }
        }

        private void maybeSnapshot() {
            if (ActivityThread.this.mBoundApplication != null && SamplingProfilerIntegration.isEnabled()) {
                String packageName = ActivityThread.this.mBoundApplication.info.mPackageName;
                PackageInfo packageInfo = null;
                try {
                    Context context = ActivityThread.this.getSystemContext();
                    if (context == null) {
                        Log.e(ActivityThread.TAG, "cannot get a valid context");
                        return;
                    }
                    PackageManager pm = context.getPackageManager();
                    if (pm == null) {
                        Log.e(ActivityThread.TAG, "cannot get a valid PackageManager");
                        return;
                    }
                    packageInfo = pm.getPackageInfo(packageName, ActivityThread.SERVICE_DONE_EXECUTING_START);
                    SamplingProfilerIntegration.writeSnapshot(ActivityThread.this.mBoundApplication.processName, packageInfo);
                } catch (NameNotFoundException e) {
                    Log.e(ActivityThread.TAG, "cannot get package info for " + packageName, e);
                }
            }
        }
    }

    private class Idler implements IdleHandler {
        private Idler() {
        }

        public final boolean queueIdle() {
            ActivityClientRecord a = ActivityThread.this.mNewActivities;
            boolean stopProfiling = ActivityThread.DEBUG_SERVICE;
            if (!(ActivityThread.this.mBoundApplication == null || ActivityThread.this.mProfiler.profileFd == null || !ActivityThread.this.mProfiler.autoStopProfiler)) {
                stopProfiling = true;
            }
            if (a != null) {
                ActivityThread.this.mNewActivities = null;
                IActivityManager am = ActivityManagerNative.getDefault();
                do {
                    if (!(a.activity == null || a.activity.mFinished)) {
                        try {
                            am.activityIdle(a.token, a.createdConfig, stopProfiling);
                            a.createdConfig = null;
                        } catch (RemoteException e) {
                        }
                    }
                    ActivityClientRecord prev = a;
                    a = a.nextIdle;
                    prev.nextIdle = null;
                } while (a != null);
            }
            if (stopProfiling) {
                ActivityThread.this.mProfiler.stopProfiling();
            }
            ActivityThread.this.ensureJitEnabled();
            return ActivityThread.DEBUG_SERVICE;
        }
    }

    static final class NewIntentData {
        List<ReferrerIntent> intents;
        IBinder token;

        NewIntentData() {
        }

        public String toString() {
            return "NewIntentData{intents=" + this.intents + " token=" + this.token + "}";
        }
    }

    static final class Profiler {
        boolean autoStopProfiler;
        boolean handlingProfiling;
        ParcelFileDescriptor profileFd;
        String profileFile;
        boolean profiling;
        int samplingInterval;

        Profiler() {
        }

        public void setProfiler(ProfilerInfo profilerInfo) {
            ParcelFileDescriptor fd = profilerInfo.profileFd;
            if (!this.profiling) {
                if (this.profileFd != null) {
                    try {
                        this.profileFd.close();
                    } catch (IOException e) {
                    }
                }
                this.profileFile = profilerInfo.profileFile;
                this.profileFd = fd;
                this.samplingInterval = profilerInfo.samplingInterval;
                this.autoStopProfiler = profilerInfo.autoStopProfiler;
            } else if (fd != null) {
                try {
                    fd.close();
                } catch (IOException e2) {
                }
            }
        }

        public void startProfiling() {
            boolean z = true;
            if (this.profileFd != null && !this.profiling) {
                try {
                    String str = this.profileFile;
                    FileDescriptor fileDescriptor = this.profileFd.getFileDescriptor();
                    if (this.samplingInterval == 0) {
                        z = ActivityThread.DEBUG_SERVICE;
                    }
                    VMDebug.startMethodTracing(str, fileDescriptor, LayoutParams.FLAG_SPLIT_TOUCH, ActivityThread.SERVICE_DONE_EXECUTING_ANON, z, this.samplingInterval);
                    this.profiling = true;
                } catch (RuntimeException e) {
                    Slog.w(ActivityThread.TAG, "Profiling failed on path " + this.profileFile);
                    try {
                        this.profileFd.close();
                        this.profileFd = null;
                    } catch (IOException e2) {
                        Slog.w(ActivityThread.TAG, "Failure closing profile fd", e2);
                    }
                }
            }
        }

        public void stopProfiling() {
            if (this.profiling) {
                this.profiling = ActivityThread.DEBUG_SERVICE;
                Debug.stopMethodTracing();
                if (this.profileFd != null) {
                    try {
                        this.profileFd.close();
                    } catch (IOException e) {
                    }
                }
                this.profileFd = null;
                this.profileFile = null;
            }
        }
    }

    final class ProviderClientRecord {
        final ContentProviderHolder mHolder;
        final ContentProvider mLocalProvider;
        final String[] mNames;
        final IContentProvider mProvider;

        ProviderClientRecord(String[] names, IContentProvider provider, ContentProvider localProvider, ContentProviderHolder holder) {
            this.mNames = names;
            this.mProvider = provider;
            this.mLocalProvider = localProvider;
            this.mHolder = holder;
        }
    }

    private static final class ProviderKey {
        final String authority;
        final int userId;

        public ProviderKey(String authority, int userId) {
            this.authority = authority;
            this.userId = userId;
        }

        public boolean equals(Object o) {
            if (!(o instanceof ProviderKey)) {
                return ActivityThread.DEBUG_SERVICE;
            }
            ProviderKey other = (ProviderKey) o;
            if (Objects.equals(this.authority, other.authority) && this.userId == other.userId) {
                return true;
            }
            return ActivityThread.DEBUG_SERVICE;
        }

        public int hashCode() {
            return (this.authority != null ? this.authority.hashCode() : ActivityThread.SERVICE_DONE_EXECUTING_ANON) ^ this.userId;
        }
    }

    private static final class ProviderRefCount {
        public final ProviderClientRecord client;
        public final ContentProviderHolder holder;
        public boolean removePending;
        public int stableCount;
        public int unstableCount;

        ProviderRefCount(ContentProviderHolder inHolder, ProviderClientRecord inClient, int sCount, int uCount) {
            this.holder = inHolder;
            this.client = inClient;
            this.stableCount = sCount;
            this.unstableCount = uCount;
        }
    }

    static final class ReceiverData extends PendingResult {
        CompatibilityInfo compatInfo;
        ActivityInfo info;
        Intent intent;

        public ReceiverData(Intent intent, int resultCode, String resultData, Bundle resultExtras, boolean ordered, boolean sticky, IBinder token, int sendingUser) {
            super(resultCode, resultData, resultExtras, ActivityThread.SERVICE_DONE_EXECUTING_ANON, ordered, sticky, token, sendingUser);
            this.intent = intent;
        }

        public String toString() {
            return "ReceiverData{intent=" + this.intent + " packageName=" + this.info.packageName + " resultCode=" + getResultCode() + " resultData=" + getResultData() + " resultExtras=" + getResultExtras(ActivityThread.DEBUG_SERVICE) + "}";
        }
    }

    static final class RequestAssistContextExtras {
        IBinder activityToken;
        IBinder requestToken;
        int requestType;

        RequestAssistContextExtras() {
        }
    }

    static final class ResultData {
        List<ResultInfo> results;
        IBinder token;

        ResultData() {
        }

        public String toString() {
            return "ResultData{token=" + this.token + " results" + this.results + "}";
        }
    }

    static final class ServiceArgsData {
        Intent args;
        int flags;
        int startId;
        boolean taskRemoved;
        IBinder token;

        ServiceArgsData() {
        }

        public String toString() {
            return "ServiceArgsData{token=" + this.token + " startId=" + this.startId + " args=" + this.args + "}";
        }
    }

    private static class StopInfo implements Runnable {
        ActivityClientRecord activity;
        CharSequence description;
        PersistableBundle persistentState;
        Bundle state;

        private StopInfo() {
        }

        public void run() {
            try {
                ActivityManagerNative.getDefault().activityStopped(this.activity.token, this.state, this.persistentState, this.description);
            } catch (RemoteException e) {
            }
        }
    }

    static final class UpdateCompatibilityData {
        CompatibilityInfo info;
        String pkg;

        UpdateCompatibilityData() {
        }
    }

    private native void dumpGraphicsInfo(FileDescriptor fileDescriptor);

    static {
        THUMBNAIL_FORMAT = Config.RGB_565;
        PATTERN_SEMICOLON = Pattern.compile(";");
        sCurrentBroadcastIntent = new ThreadLocal();
    }

    public static ActivityThread currentActivityThread() {
        return sCurrentActivityThread;
    }

    public static String currentPackageName() {
        ActivityThread am = currentActivityThread();
        return (am == null || am.mBoundApplication == null) ? null : am.mBoundApplication.appInfo.packageName;
    }

    public static String currentProcessName() {
        ActivityThread am = currentActivityThread();
        return (am == null || am.mBoundApplication == null) ? null : am.mBoundApplication.processName;
    }

    public static Application currentApplication() {
        ActivityThread am = currentActivityThread();
        return am != null ? am.mInitialApplication : null;
    }

    public static IPackageManager getPackageManager() {
        if (sPackageManager != null) {
            return sPackageManager;
        }
        sPackageManager = Stub.asInterface(ServiceManager.getService("package"));
        return sPackageManager;
    }

    Configuration applyConfigCompatMainThread(int displayDensity, Configuration config, CompatibilityInfo compat) {
        if (config == null) {
            return null;
        }
        if (!compat.supportsScreen()) {
            this.mMainThreadConfig.setTo(config);
            config = this.mMainThreadConfig;
            compat.applyToConfiguration(displayDensity, config);
        }
        return config;
    }

    Resources getTopLevelResources(String resDir, String[] splitResDirs, String[] overlayDirs, String[] libDirs, int displayId, Configuration overrideConfiguration, LoadedApk pkgInfo) {
        return this.mResourcesManager.getTopLevelResources(resDir, splitResDirs, overlayDirs, libDirs, displayId, overrideConfiguration, pkgInfo.getCompatibilityInfo(), null);
    }

    final Handler getHandler() {
        return this.mH;
    }

    public final LoadedApk getPackageInfo(String packageName, CompatibilityInfo compatInfo, int flags) {
        return getPackageInfo(packageName, compatInfo, flags, UserHandle.myUserId());
    }

    public final LoadedApk getPackageInfo(String packageName, CompatibilityInfo compatInfo, int flags, int userId) {
        synchronized (this.mResourcesManager) {
            WeakReference<LoadedApk> ref;
            LoadedApk packageInfo;
            if ((flags & SERVICE_DONE_EXECUTING_START) != 0) {
                ref = (WeakReference) this.mPackages.get(packageName);
            } else {
                ref = (WeakReference) this.mResourcePackages.get(packageName);
            }
            if (ref != null) {
                packageInfo = (LoadedApk) ref.get();
            } else {
                packageInfo = null;
            }
            if (packageInfo == null || !(packageInfo.mResources == null || packageInfo.mResources.getAssets().isUpToDate())) {
                ApplicationInfo ai = null;
                try {
                    ai = getPackageManager().getApplicationInfo(packageName, AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT, userId);
                } catch (RemoteException e) {
                }
                return ai != null ? getPackageInfo(ai, compatInfo, flags) : null;
            } else if (packageInfo.isSecurityViolation() && (flags & SERVICE_DONE_EXECUTING_STOP) == 0) {
                throw new SecurityException("Requesting code from " + packageName + " to be run in process " + this.mBoundApplication.processName + "/" + this.mBoundApplication.appInfo.uid);
            } else {
                return packageInfo;
            }
        }
    }

    public final LoadedApk getPackageInfo(ApplicationInfo ai, CompatibilityInfo compatInfo, int flags) {
        boolean includeCode;
        boolean securityViolation;
        boolean registerPackage = DEBUG_SERVICE;
        if ((flags & SERVICE_DONE_EXECUTING_START) != 0) {
            includeCode = true;
        } else {
            includeCode = DEBUG_SERVICE;
        }
        if (!includeCode || ai.uid == 0 || ai.uid == LayoutParams.TYPE_APPLICATION_PANEL || (this.mBoundApplication != null && UserHandle.isSameApp(ai.uid, this.mBoundApplication.appInfo.uid))) {
            securityViolation = DEBUG_SERVICE;
        } else {
            securityViolation = true;
        }
        if (includeCode && (EditorInfo.IME_FLAG_NO_ENTER_ACTION & flags) != 0) {
            registerPackage = true;
        }
        if ((flags & ACTIVITY_THREAD_CHECKIN_VERSION) != SERVICE_DONE_EXECUTING_START || !securityViolation) {
            return getPackageInfo(ai, compatInfo, null, securityViolation, includeCode, registerPackage);
        }
        String msg = "Requesting code from " + ai.packageName + " (with uid " + ai.uid + ")";
        if (this.mBoundApplication != null) {
            msg = msg + " to be run in process " + this.mBoundApplication.processName + " (with uid " + this.mBoundApplication.appInfo.uid + ")";
        }
        throw new SecurityException(msg);
    }

    public final LoadedApk getPackageInfoNoCheck(ApplicationInfo ai, CompatibilityInfo compatInfo) {
        return getPackageInfo(ai, compatInfo, null, DEBUG_SERVICE, true, DEBUG_SERVICE);
    }

    public final LoadedApk peekPackageInfo(String packageName, boolean includeCode) {
        LoadedApk loadedApk;
        synchronized (this.mResourcesManager) {
            WeakReference<LoadedApk> ref;
            if (includeCode) {
                ref = (WeakReference) this.mPackages.get(packageName);
            } else {
                ref = (WeakReference) this.mResourcePackages.get(packageName);
            }
            loadedApk = ref != null ? (LoadedApk) ref.get() : null;
        }
        return loadedApk;
    }

    private LoadedApk getPackageInfo(ApplicationInfo aInfo, CompatibilityInfo compatInfo, ClassLoader baseLoader, boolean securityViolation, boolean includeCode, boolean registerPackage) {
        LoadedApk packageInfo;
        synchronized (this.mResourcesManager) {
            WeakReference<LoadedApk> ref;
            if (includeCode) {
                ref = (WeakReference) this.mPackages.get(aInfo.packageName);
            } else {
                ref = (WeakReference) this.mResourcePackages.get(aInfo.packageName);
            }
            packageInfo = ref != null ? (LoadedApk) ref.get() : null;
            if (packageInfo == null || !(packageInfo.mResources == null || packageInfo.mResources.getAssets().isUpToDate())) {
                boolean z = (!includeCode || (aInfo.flags & 4) == 0) ? DEBUG_SERVICE : true;
                packageInfo = new LoadedApk(this, aInfo, compatInfo, baseLoader, securityViolation, z, registerPackage);
                if (this.mSystemThread && ZenModeConfig.SYSTEM_AUTHORITY.equals(aInfo.packageName)) {
                    packageInfo.installSystemApplicationInfo(aInfo, getSystemContext().mPackageInfo.getClassLoader());
                }
                if (includeCode) {
                    this.mPackages.put(aInfo.packageName, new WeakReference(packageInfo));
                } else {
                    this.mResourcePackages.put(aInfo.packageName, new WeakReference(packageInfo));
                }
            }
        }
        return packageInfo;
    }

    ActivityThread() {
        this.mAppThread = new ApplicationThread();
        this.mLooper = Looper.myLooper();
        this.mH = new H();
        this.mActivities = new ArrayMap();
        this.mNewActivities = null;
        this.mNumVisibleActivities = SERVICE_DONE_EXECUTING_ANON;
        this.mServices = new ArrayMap();
        this.mAllApplications = new ArrayList();
        this.mBackupAgents = new ArrayMap();
        this.mInstrumentationPackageName = null;
        this.mInstrumentationAppDir = null;
        this.mInstrumentationSplitAppDirs = null;
        this.mInstrumentationLibDir = null;
        this.mInstrumentedAppDir = null;
        this.mInstrumentedSplitAppDirs = null;
        this.mInstrumentedLibDir = null;
        this.mSystemThread = DEBUG_SERVICE;
        this.mJitEnabled = DEBUG_SERVICE;
        this.mSomeActivitiesChanged = DEBUG_SERVICE;
        this.mPackages = new ArrayMap();
        this.mResourcePackages = new ArrayMap();
        this.mRelaunchingActivities = new ArrayList();
        this.mPendingConfiguration = null;
        this.mProviderMap = new ArrayMap();
        this.mProviderRefCountMap = new ArrayMap();
        this.mLocalProviders = new ArrayMap();
        this.mLocalProvidersByName = new ArrayMap();
        this.mOnPauseListeners = new ArrayMap();
        this.mGcIdler = new GcIdler();
        this.mGcIdlerScheduled = DEBUG_SERVICE;
        this.mCoreSettings = null;
        this.mMainThreadConfig = new Configuration();
        this.mThumbnailWidth = -1;
        this.mThumbnailHeight = -1;
        this.mAvailThumbnailBitmap = null;
        this.mThumbnailCanvas = null;
        this.mResourcesManager = ResourcesManager.getInstance();
    }

    public ApplicationThread getApplicationThread() {
        return this.mAppThread;
    }

    public Instrumentation getInstrumentation() {
        return this.mInstrumentation;
    }

    public boolean isProfiling() {
        return (this.mProfiler == null || this.mProfiler.profileFile == null || this.mProfiler.profileFd != null) ? DEBUG_SERVICE : true;
    }

    public String getProfileFilePath() {
        return this.mProfiler.profileFile;
    }

    public Looper getLooper() {
        return this.mLooper;
    }

    public Application getApplication() {
        return this.mInitialApplication;
    }

    public String getProcessName() {
        return this.mBoundApplication.processName;
    }

    public ContextImpl getSystemContext() {
        ContextImpl contextImpl;
        synchronized (this) {
            if (this.mSystemContext == null) {
                this.mSystemContext = ContextImpl.createSystemContext(this);
            }
            contextImpl = this.mSystemContext;
        }
        return contextImpl;
    }

    public void installSystemApplicationInfo(ApplicationInfo info, ClassLoader classLoader) {
        synchronized (this) {
            getSystemContext().installSystemApplicationInfo(info, classLoader);
            this.mProfiler = new Profiler();
        }
    }

    void ensureJitEnabled() {
        if (!this.mJitEnabled) {
            this.mJitEnabled = true;
            VMRuntime.getRuntime().startJitCompilation();
        }
    }

    void scheduleGcIdler() {
        if (!this.mGcIdlerScheduled) {
            this.mGcIdlerScheduled = true;
            Looper.myQueue().addIdleHandler(this.mGcIdler);
        }
        this.mH.removeMessages(KeyEvent.KEYCODE_SYSRQ);
    }

    void unscheduleGcIdler() {
        if (this.mGcIdlerScheduled) {
            this.mGcIdlerScheduled = DEBUG_SERVICE;
            Looper.myQueue().removeIdleHandler(this.mGcIdler);
        }
        this.mH.removeMessages(KeyEvent.KEYCODE_SYSRQ);
    }

    void doGcIfNeeded() {
        this.mGcIdlerScheduled = DEBUG_SERVICE;
        if (BinderInternal.getLastGcTime() + MIN_TIME_BETWEEN_GCS < SystemClock.uptimeMillis()) {
            BinderInternal.forceGc("bg");
        }
    }

    static void printRow(PrintWriter pw, String format, Object... objs) {
        pw.println(String.format(format, objs));
    }

    public static void dumpMemInfoTable(PrintWriter pw, MemoryInfo memInfo, boolean checkin, boolean dumpFullInfo, boolean dumpDalvik, int pid, String processName, long nativeMax, long nativeAllocated, long nativeFree, long dalvikMax, long dalvikAllocated, long dalvikFree) {
        int i;
        if (checkin) {
            pw.print(ACTIVITY_THREAD_CHECKIN_VERSION);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(pid);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(processName);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(nativeMax);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(dalvikMax);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print("N/A,");
            pw.print(nativeMax + dalvikMax);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(nativeAllocated);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(dalvikAllocated);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print("N/A,");
            pw.print(nativeAllocated + dalvikAllocated);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(nativeFree);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(dalvikFree);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print("N/A,");
            pw.print(nativeFree + dalvikFree);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(memInfo.nativePss);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(memInfo.dalvikPss);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(memInfo.otherPss);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(memInfo.getTotalPss());
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(memInfo.nativeSwappablePss);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(memInfo.dalvikSwappablePss);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(memInfo.otherSwappablePss);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(memInfo.getTotalSwappablePss());
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(memInfo.nativeSharedDirty);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(memInfo.dalvikSharedDirty);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(memInfo.otherSharedDirty);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(memInfo.getTotalSharedDirty());
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(memInfo.nativeSharedClean);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(memInfo.dalvikSharedClean);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(memInfo.otherSharedClean);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(memInfo.getTotalSharedClean());
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(memInfo.nativePrivateDirty);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(memInfo.dalvikPrivateDirty);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(memInfo.otherPrivateDirty);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(memInfo.getTotalPrivateDirty());
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(memInfo.nativePrivateClean);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(memInfo.dalvikPrivateClean);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(memInfo.otherPrivateClean);
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(memInfo.getTotalPrivateClean());
            pw.print(PhoneNumberUtils.PAUSE);
            for (i = SERVICE_DONE_EXECUTING_ANON; i < 17; i += SERVICE_DONE_EXECUTING_START) {
                pw.print(MemoryInfo.getOtherLabel(i));
                pw.print(PhoneNumberUtils.PAUSE);
                pw.print(memInfo.getOtherPss(i));
                pw.print(PhoneNumberUtils.PAUSE);
                pw.print(memInfo.getOtherSwappablePss(i));
                pw.print(PhoneNumberUtils.PAUSE);
                pw.print(memInfo.getOtherSharedDirty(i));
                pw.print(PhoneNumberUtils.PAUSE);
                pw.print(memInfo.getOtherSharedClean(i));
                pw.print(PhoneNumberUtils.PAUSE);
                pw.print(memInfo.getOtherPrivateDirty(i));
                pw.print(PhoneNumberUtils.PAUSE);
                pw.print(memInfo.getOtherPrivateClean(i));
                pw.print(PhoneNumberUtils.PAUSE);
            }
            return;
        }
        String str;
        String[] strArr;
        if (dumpFullInfo) {
            printRow(pw, HEAP_FULL_COLUMN, ProxyInfo.LOCAL_EXCL_LIST, "Pss", "Pss", "Shared", "Private", "Shared", "Private", "Swapped", "Heap", "Heap", "Heap");
            printRow(pw, HEAP_FULL_COLUMN, ProxyInfo.LOCAL_EXCL_LIST, "Total", "Clean", "Dirty", "Dirty", "Clean", "Clean", "Dirty", "Size", "Alloc", "Free");
            printRow(pw, HEAP_FULL_COLUMN, ProxyInfo.LOCAL_EXCL_LIST, "------", "------", "------", "------", "------", "------", "------", "------", "------", "------");
            str = HEAP_FULL_COLUMN;
            strArr = new Object[11];
            strArr[SERVICE_DONE_EXECUTING_START] = Integer.valueOf(memInfo.nativePss);
            strArr[SERVICE_DONE_EXECUTING_STOP] = Integer.valueOf(memInfo.nativeSwappablePss);
            strArr[ACTIVITY_THREAD_CHECKIN_VERSION] = Integer.valueOf(memInfo.nativeSharedDirty);
            strArr[4] = Integer.valueOf(memInfo.nativePrivateDirty);
            strArr[5] = Integer.valueOf(memInfo.nativeSharedClean);
            strArr[6] = Integer.valueOf(memInfo.nativePrivateClean);
            strArr[7] = Integer.valueOf(memInfo.nativeSwappedOut);
            strArr[8] = Long.valueOf(nativeMax);
            strArr[9] = Long.valueOf(nativeAllocated);
            strArr[10] = Long.valueOf(nativeFree);
            printRow(pw, str, strArr);
            str = HEAP_FULL_COLUMN;
            strArr = new Object[11];
            strArr[SERVICE_DONE_EXECUTING_START] = Integer.valueOf(memInfo.dalvikPss);
            strArr[SERVICE_DONE_EXECUTING_STOP] = Integer.valueOf(memInfo.dalvikSwappablePss);
            strArr[ACTIVITY_THREAD_CHECKIN_VERSION] = Integer.valueOf(memInfo.dalvikSharedDirty);
            strArr[4] = Integer.valueOf(memInfo.dalvikPrivateDirty);
            strArr[5] = Integer.valueOf(memInfo.dalvikSharedClean);
            strArr[6] = Integer.valueOf(memInfo.dalvikPrivateClean);
            strArr[7] = Integer.valueOf(memInfo.dalvikSwappedOut);
            strArr[8] = Long.valueOf(dalvikMax);
            strArr[9] = Long.valueOf(dalvikAllocated);
            strArr[10] = Long.valueOf(dalvikFree);
            printRow(pw, str, strArr);
        } else {
            printRow(pw, HEAP_COLUMN, ProxyInfo.LOCAL_EXCL_LIST, "Pss", "Private", "Private", "Swapped", "Heap", "Heap", "Heap");
            printRow(pw, HEAP_COLUMN, ProxyInfo.LOCAL_EXCL_LIST, "Total", "Dirty", "Clean", "Dirty", "Size", "Alloc", "Free");
            printRow(pw, HEAP_COLUMN, ProxyInfo.LOCAL_EXCL_LIST, "------", "------", "------", "------", "------", "------", "------", "------");
            str = HEAP_COLUMN;
            strArr = new Object[8];
            strArr[SERVICE_DONE_EXECUTING_ANON] = "Native Heap";
            strArr[SERVICE_DONE_EXECUTING_START] = Integer.valueOf(memInfo.nativePss);
            strArr[SERVICE_DONE_EXECUTING_STOP] = Integer.valueOf(memInfo.nativePrivateDirty);
            strArr[ACTIVITY_THREAD_CHECKIN_VERSION] = Integer.valueOf(memInfo.nativePrivateClean);
            strArr[4] = Integer.valueOf(memInfo.nativeSwappedOut);
            strArr[5] = Long.valueOf(nativeMax);
            strArr[6] = Long.valueOf(nativeAllocated);
            strArr[7] = Long.valueOf(nativeFree);
            printRow(pw, str, strArr);
            str = HEAP_COLUMN;
            strArr = new Object[8];
            strArr[SERVICE_DONE_EXECUTING_ANON] = "Dalvik Heap";
            strArr[SERVICE_DONE_EXECUTING_START] = Integer.valueOf(memInfo.dalvikPss);
            strArr[SERVICE_DONE_EXECUTING_STOP] = Integer.valueOf(memInfo.dalvikPrivateDirty);
            strArr[ACTIVITY_THREAD_CHECKIN_VERSION] = Integer.valueOf(memInfo.dalvikPrivateClean);
            strArr[4] = Integer.valueOf(memInfo.dalvikSwappedOut);
            strArr[5] = Long.valueOf(dalvikMax);
            strArr[6] = Long.valueOf(dalvikAllocated);
            strArr[7] = Long.valueOf(dalvikFree);
            printRow(pw, str, strArr);
        }
        int otherPss = memInfo.otherPss;
        int otherSwappablePss = memInfo.otherSwappablePss;
        int otherSharedDirty = memInfo.otherSharedDirty;
        int otherPrivateDirty = memInfo.otherPrivateDirty;
        int otherSharedClean = memInfo.otherSharedClean;
        int otherPrivateClean = memInfo.otherPrivateClean;
        int otherSwappedOut = memInfo.otherSwappedOut;
        for (i = SERVICE_DONE_EXECUTING_ANON; i < 17; i += SERVICE_DONE_EXECUTING_START) {
            int myPss = memInfo.getOtherPss(i);
            int mySwappablePss = memInfo.getOtherSwappablePss(i);
            int mySharedDirty = memInfo.getOtherSharedDirty(i);
            int myPrivateDirty = memInfo.getOtherPrivateDirty(i);
            int mySharedClean = memInfo.getOtherSharedClean(i);
            int myPrivateClean = memInfo.getOtherPrivateClean(i);
            int mySwappedOut = memInfo.getOtherSwappedOut(i);
            if (myPss != 0 || mySharedDirty != 0 || myPrivateDirty != 0 || mySharedClean != 0 || myPrivateClean != 0 || mySwappedOut != 0) {
                if (dumpFullInfo) {
                    printRow(pw, HEAP_FULL_COLUMN, MemoryInfo.getOtherLabel(i), Integer.valueOf(myPss), Integer.valueOf(mySwappablePss), Integer.valueOf(mySharedDirty), Integer.valueOf(myPrivateDirty), Integer.valueOf(mySharedClean), Integer.valueOf(myPrivateClean), Integer.valueOf(mySwappedOut), ProxyInfo.LOCAL_EXCL_LIST, ProxyInfo.LOCAL_EXCL_LIST, ProxyInfo.LOCAL_EXCL_LIST);
                } else {
                    printRow(pw, HEAP_COLUMN, MemoryInfo.getOtherLabel(i), Integer.valueOf(myPss), Integer.valueOf(myPrivateDirty), Integer.valueOf(myPrivateClean), Integer.valueOf(mySwappedOut), ProxyInfo.LOCAL_EXCL_LIST, ProxyInfo.LOCAL_EXCL_LIST, ProxyInfo.LOCAL_EXCL_LIST);
                }
                otherPss -= myPss;
                otherSwappablePss -= mySwappablePss;
                otherSharedDirty -= mySharedDirty;
                otherPrivateDirty -= myPrivateDirty;
                otherSharedClean -= mySharedClean;
                otherPrivateClean -= myPrivateClean;
                otherSwappedOut -= mySwappedOut;
            }
        }
        if (dumpFullInfo) {
            printRow(pw, HEAP_FULL_COLUMN, "Unknown", Integer.valueOf(otherPss), Integer.valueOf(otherSwappablePss), Integer.valueOf(otherSharedDirty), Integer.valueOf(otherPrivateDirty), Integer.valueOf(otherSharedClean), Integer.valueOf(otherPrivateClean), Integer.valueOf(otherSwappedOut), ProxyInfo.LOCAL_EXCL_LIST, ProxyInfo.LOCAL_EXCL_LIST, ProxyInfo.LOCAL_EXCL_LIST);
            printRow(pw, HEAP_FULL_COLUMN, "TOTAL", Integer.valueOf(memInfo.getTotalPss()), Integer.valueOf(memInfo.getTotalSwappablePss()), Integer.valueOf(memInfo.getTotalSharedDirty()), Integer.valueOf(memInfo.getTotalPrivateDirty()), Integer.valueOf(memInfo.getTotalSharedClean()), Integer.valueOf(memInfo.getTotalPrivateClean()), Integer.valueOf(memInfo.getTotalSwappedOut()), Long.valueOf(nativeMax + dalvikMax), Long.valueOf(nativeAllocated + dalvikAllocated), Long.valueOf(nativeFree + dalvikFree));
        } else {
            printRow(pw, HEAP_COLUMN, "Unknown", Integer.valueOf(otherPss), Integer.valueOf(otherPrivateDirty), Integer.valueOf(otherPrivateClean), Integer.valueOf(otherSwappedOut), ProxyInfo.LOCAL_EXCL_LIST, ProxyInfo.LOCAL_EXCL_LIST, ProxyInfo.LOCAL_EXCL_LIST);
            printRow(pw, HEAP_COLUMN, "TOTAL", Integer.valueOf(memInfo.getTotalPss()), Integer.valueOf(memInfo.getTotalPrivateDirty()), Integer.valueOf(memInfo.getTotalPrivateClean()), Integer.valueOf(memInfo.getTotalSwappedOut()), Long.valueOf(nativeMax + dalvikMax), Long.valueOf(nativeAllocated + dalvikAllocated), Long.valueOf(nativeFree + dalvikFree));
        }
        if (dumpDalvik) {
            pw.println(" ");
            pw.println(" Dalvik Details");
            for (i = 17; i < 25; i += SERVICE_DONE_EXECUTING_START) {
                myPss = memInfo.getOtherPss(i);
                mySwappablePss = memInfo.getOtherSwappablePss(i);
                mySharedDirty = memInfo.getOtherSharedDirty(i);
                myPrivateDirty = memInfo.getOtherPrivateDirty(i);
                mySharedClean = memInfo.getOtherSharedClean(i);
                myPrivateClean = memInfo.getOtherPrivateClean(i);
                mySwappedOut = memInfo.getOtherSwappedOut(i);
                if (myPss != 0 || mySharedDirty != 0 || myPrivateDirty != 0 || mySharedClean != 0 || myPrivateClean != 0) {
                    if (dumpFullInfo) {
                        printRow(pw, HEAP_FULL_COLUMN, MemoryInfo.getOtherLabel(i), Integer.valueOf(myPss), Integer.valueOf(mySwappablePss), Integer.valueOf(mySharedDirty), Integer.valueOf(myPrivateDirty), Integer.valueOf(mySharedClean), Integer.valueOf(myPrivateClean), Integer.valueOf(mySwappedOut), ProxyInfo.LOCAL_EXCL_LIST, ProxyInfo.LOCAL_EXCL_LIST, ProxyInfo.LOCAL_EXCL_LIST);
                    } else {
                        printRow(pw, HEAP_COLUMN, MemoryInfo.getOtherLabel(i), Integer.valueOf(myPss), Integer.valueOf(myPrivateDirty), Integer.valueOf(myPrivateClean), Integer.valueOf(mySwappedOut), ProxyInfo.LOCAL_EXCL_LIST, ProxyInfo.LOCAL_EXCL_LIST, ProxyInfo.LOCAL_EXCL_LIST);
                    }
                }
            }
        }
    }

    public void registerOnActivityPausedListener(Activity activity, OnActivityPausedListener listener) {
        synchronized (this.mOnPauseListeners) {
            ArrayList<OnActivityPausedListener> list = (ArrayList) this.mOnPauseListeners.get(activity);
            if (list == null) {
                list = new ArrayList();
                this.mOnPauseListeners.put(activity, list);
            }
            list.add(listener);
        }
    }

    public void unregisterOnActivityPausedListener(Activity activity, OnActivityPausedListener listener) {
        synchronized (this.mOnPauseListeners) {
            ArrayList<OnActivityPausedListener> list = (ArrayList) this.mOnPauseListeners.get(activity);
            if (list != null) {
                list.remove(listener);
            }
        }
    }

    public final ActivityInfo resolveActivityInfo(Intent intent) {
        ActivityInfo aInfo = intent.resolveActivityInfo(this.mInitialApplication.getPackageManager(), AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT);
        if (aInfo == null) {
            Instrumentation.checkStartActivityResult(-2, intent);
        }
        return aInfo;
    }

    public final Activity startActivityNow(Activity parent, String id, Intent intent, ActivityInfo activityInfo, IBinder token, Bundle state, NonConfigurationInstances lastNonConfigurationInstances) {
        ActivityClientRecord r = new ActivityClientRecord();
        r.token = token;
        r.ident = SERVICE_DONE_EXECUTING_ANON;
        r.intent = intent;
        r.state = state;
        r.parent = parent;
        r.embeddedID = id;
        r.activityInfo = activityInfo;
        r.lastNonConfigurationInstances = lastNonConfigurationInstances;
        return performLaunchActivity(r, null);
    }

    public final Activity getActivity(IBinder token) {
        return ((ActivityClientRecord) this.mActivities.get(token)).activity;
    }

    public final void sendActivityResult(IBinder token, String id, int requestCode, int resultCode, Intent data) {
        ArrayList<ResultInfo> list = new ArrayList();
        list.add(new ResultInfo(id, requestCode, resultCode, data));
        this.mAppThread.scheduleSendResult(token, list);
    }

    private void sendMessage(int what, Object obj) {
        sendMessage(what, obj, SERVICE_DONE_EXECUTING_ANON, SERVICE_DONE_EXECUTING_ANON, DEBUG_SERVICE);
    }

    private void sendMessage(int what, Object obj, int arg1) {
        sendMessage(what, obj, arg1, SERVICE_DONE_EXECUTING_ANON, DEBUG_SERVICE);
    }

    private void sendMessage(int what, Object obj, int arg1, int arg2) {
        sendMessage(what, obj, arg1, arg2, DEBUG_SERVICE);
    }

    private void sendMessage(int what, Object obj, int arg1, int arg2, boolean async) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        if (async) {
            msg.setAsynchronous(true);
        }
        this.mH.sendMessage(msg);
    }

    final void scheduleContextCleanup(ContextImpl context, String who, String what) {
        ContextCleanupInfo cci = new ContextCleanupInfo();
        cci.context = context;
        cci.who = who;
        cci.what = what;
        sendMessage(KeyEvent.KEYCODE_FUNCTION, cci);
    }

    private Activity performLaunchActivity(ActivityClientRecord r, Intent customIntent) {
        ActivityInfo aInfo = r.activityInfo;
        if (r.packageInfo == null) {
            r.packageInfo = getPackageInfo(aInfo.applicationInfo, r.compatInfo, (int) SERVICE_DONE_EXECUTING_START);
        }
        ComponentName component = r.intent.getComponent();
        if (component == null) {
            component = r.intent.resolveActivity(this.mInitialApplication.getPackageManager());
            r.intent.setComponent(component);
        }
        if (r.activityInfo.targetActivity != null) {
            ComponentName componentName = new ComponentName(r.activityInfo.packageName, r.activityInfo.targetActivity);
        }
        Activity activity = null;
        try {
            ClassLoader cl = r.packageInfo.getClassLoader();
            activity = this.mInstrumentation.newActivity(cl, component.getClassName(), r.intent);
            StrictMode.incrementExpectedActivityCount(activity.getClass());
            r.intent.setExtrasClassLoader(cl);
            r.intent.prepareToEnterProcess();
            if (r.state != null) {
                r.state.setClassLoader(cl);
            }
        } catch (Throwable e) {
            if (!this.mInstrumentation.onException(null, e)) {
                throw new RuntimeException("Unable to instantiate activity " + component + ": " + e.toString(), e);
            }
        }
        try {
            Application app = r.packageInfo.makeApplication(DEBUG_SERVICE, this.mInstrumentation);
            if (activity != null) {
                Context appContext = createBaseContextForActivity(r, activity);
                activity.attach(appContext, this, getInstrumentation(), r.token, r.ident, app, r.intent, r.activityInfo, r.activityInfo.loadLabel(appContext.getPackageManager()), r.parent, r.embeddedID, r.lastNonConfigurationInstances, new Configuration(this.mCompatConfiguration), r.referrer, r.voiceInteractor);
                if (customIntent != null) {
                    activity.mIntent = customIntent;
                }
                r.lastNonConfigurationInstances = null;
                activity.mStartedActivity = DEBUG_SERVICE;
                int theme = r.activityInfo.getThemeResource();
                if (theme != 0) {
                    activity.setTheme(theme);
                }
                activity.mCalled = DEBUG_SERVICE;
                if (r.isPersistable()) {
                    this.mInstrumentation.callActivityOnCreate(activity, r.state, r.persistentState);
                } else {
                    this.mInstrumentation.callActivityOnCreate(activity, r.state);
                }
                if (activity.mCalled) {
                    r.activity = activity;
                    r.stopped = true;
                    if (!r.activity.mFinished) {
                        activity.performStart();
                        r.stopped = DEBUG_SERVICE;
                    }
                    if (!r.activity.mFinished) {
                        if (r.isPersistable()) {
                            if (!(r.state == null && r.persistentState == null)) {
                                this.mInstrumentation.callActivityOnRestoreInstanceState(activity, r.state, r.persistentState);
                            }
                        } else if (r.state != null) {
                            this.mInstrumentation.callActivityOnRestoreInstanceState(activity, r.state);
                        }
                    }
                    if (!r.activity.mFinished) {
                        activity.mCalled = DEBUG_SERVICE;
                        if (r.isPersistable()) {
                            this.mInstrumentation.callActivityOnPostCreate(activity, r.state, r.persistentState);
                        } else {
                            this.mInstrumentation.callActivityOnPostCreate(activity, r.state);
                        }
                        if (!activity.mCalled) {
                            throw new SuperNotCalledException("Activity " + r.intent.getComponent().toShortString() + " did not call through to super.onPostCreate()");
                        }
                    }
                }
                throw new SuperNotCalledException("Activity " + r.intent.getComponent().toShortString() + " did not call through to super.onCreate()");
            }
            r.paused = true;
            this.mActivities.put(r.token, r);
        } catch (SuperNotCalledException e2) {
            throw e2;
        } catch (Throwable e3) {
            if (!this.mInstrumentation.onException(activity, e3)) {
                throw new RuntimeException("Unable to start activity " + component + ": " + e3.toString(), e3);
            }
        }
        return activity;
    }

    private Context createBaseContextForActivity(ActivityClientRecord r, Activity activity) {
        int displayId;
        Context appContext = ContextImpl.createActivityContext(this, r.packageInfo, r.token);
        appContext.setOuterContext(activity);
        Context baseContext = appContext;
        DisplayManagerGlobal dm = DisplayManagerGlobal.getInstance();
        try {
            displayId = ActivityManagerNative.getDefault().getActivityDisplayId(r.token);
            if (displayId > 0) {
                baseContext = appContext.createDisplayContext(dm.getRealDisplay(displayId, r.token));
            }
        } catch (RemoteException e) {
        }
        String pkgName = SystemProperties.get("debug.second-display.pkg");
        if (pkgName == null || pkgName.isEmpty() || !r.packageInfo.mPackageName.contains(pkgName)) {
            return baseContext;
        }
        int[] arr$ = dm.getDisplayIds();
        int len$ = arr$.length;
        for (int i$ = SERVICE_DONE_EXECUTING_ANON; i$ < len$; i$ += SERVICE_DONE_EXECUTING_START) {
            displayId = arr$[i$];
            if (displayId != 0) {
                return appContext.createDisplayContext(dm.getRealDisplay(displayId, r.token));
            }
        }
        return baseContext;
    }

    private void handleLaunchActivity(ActivityClientRecord r, Intent customIntent) {
        unscheduleGcIdler();
        this.mSomeActivitiesChanged = true;
        if (r.profilerInfo != null) {
            this.mProfiler.setProfiler(r.profilerInfo);
            this.mProfiler.startProfiling();
        }
        handleConfigurationChanged(null, null);
        WindowManagerGlobal.initialize();
        if (performLaunchActivity(r, customIntent) != null) {
            boolean z;
            r.createdConfig = new Configuration(this.mConfiguration);
            Bundle oldState = r.state;
            IBinder iBinder = r.token;
            boolean z2 = r.isForward;
            if (r.activity.mFinished || r.startsNotResumed) {
                z = DEBUG_SERVICE;
            } else {
                z = true;
            }
            handleResumeActivity(iBinder, DEBUG_SERVICE, z2, z);
            if (!r.activity.mFinished && r.startsNotResumed) {
                try {
                    r.activity.mCalled = DEBUG_SERVICE;
                    this.mInstrumentation.callActivityOnPause(r.activity);
                    if (r.isPreHoneycomb()) {
                        r.state = oldState;
                    }
                    if (!r.activity.mCalled) {
                        throw new SuperNotCalledException("Activity " + r.intent.getComponent().toShortString() + " did not call through to super.onPause()");
                    }
                } catch (SuperNotCalledException e) {
                    throw e;
                } catch (Exception e2) {
                    if (!this.mInstrumentation.onException(r.activity, e2)) {
                        throw new RuntimeException("Unable to pause activity " + r.intent.getComponent().toShortString() + ": " + e2.toString(), e2);
                    }
                }
                r.paused = true;
                return;
            }
            return;
        }
        try {
            ActivityManagerNative.getDefault().finishActivity(r.token, SERVICE_DONE_EXECUTING_ANON, null, DEBUG_SERVICE);
        } catch (RemoteException e3) {
        }
    }

    private void deliverNewIntents(ActivityClientRecord r, List<ReferrerIntent> intents) {
        int N = intents.size();
        for (int i = SERVICE_DONE_EXECUTING_ANON; i < N; i += SERVICE_DONE_EXECUTING_START) {
            ReferrerIntent intent = (ReferrerIntent) intents.get(i);
            intent.setExtrasClassLoader(r.activity.getClassLoader());
            intent.prepareToEnterProcess();
            r.activity.mFragments.noteStateNotSaved();
            this.mInstrumentation.callActivityOnNewIntent(r.activity, intent);
        }
    }

    public final void performNewIntents(IBinder token, List<ReferrerIntent> intents) {
        ActivityClientRecord r = (ActivityClientRecord) this.mActivities.get(token);
        if (r != null) {
            boolean resumed = !r.paused ? true : DEBUG_SERVICE;
            if (resumed) {
                r.activity.mTemporaryPause = true;
                this.mInstrumentation.callActivityOnPause(r.activity);
            }
            deliverNewIntents(r, intents);
            if (resumed) {
                r.activity.performResume();
                r.activity.mTemporaryPause = DEBUG_SERVICE;
            }
        }
    }

    private void handleNewIntent(NewIntentData data) {
        performNewIntents(data.token, data.intents);
    }

    public void handleRequestAssistContextExtras(RequestAssistContextExtras cmd) {
        Bundle data = new Bundle();
        ActivityClientRecord r = (ActivityClientRecord) this.mActivities.get(cmd.activityToken);
        if (r != null) {
            r.activity.getApplication().dispatchOnProvideAssistData(r.activity, data);
            r.activity.onProvideAssistData(data);
        }
        if (data.isEmpty()) {
            data = null;
        }
        try {
            ActivityManagerNative.getDefault().reportAssistContextExtras(cmd.requestToken, data);
        } catch (RemoteException e) {
        }
    }

    public void handleTranslucentConversionComplete(IBinder token, boolean drawComplete) {
        ActivityClientRecord r = (ActivityClientRecord) this.mActivities.get(token);
        if (r != null) {
            r.activity.onTranslucentConversionComplete(drawComplete);
        }
    }

    public void onNewActivityOptions(IBinder token, ActivityOptions options) {
        ActivityClientRecord r = (ActivityClientRecord) this.mActivities.get(token);
        if (r != null) {
            r.activity.onNewActivityOptions(options);
        }
    }

    public void handleCancelVisibleBehind(IBinder token) {
        ActivityClientRecord r = (ActivityClientRecord) this.mActivities.get(token);
        if (r != null) {
            this.mSomeActivitiesChanged = true;
            Activity activity = r.activity;
            if (activity.mVisibleBehind) {
                activity.mCalled = DEBUG_SERVICE;
                activity.onVisibleBehindCanceled();
                if (activity.mCalled) {
                    activity.mVisibleBehind = DEBUG_SERVICE;
                } else {
                    throw new SuperNotCalledException("Activity " + activity.getLocalClassName() + " did not call through to super.onVisibleBehindCanceled()");
                }
            }
        }
        try {
            ActivityManagerNative.getDefault().backgroundResourcesReleased(token);
        } catch (RemoteException e) {
        }
    }

    public void handleOnBackgroundVisibleBehindChanged(IBinder token, boolean visible) {
        ActivityClientRecord r = (ActivityClientRecord) this.mActivities.get(token);
        if (r != null) {
            r.activity.onBackgroundVisibleBehindChanged(visible);
        }
    }

    public void handleInstallProvider(ProviderInfo info) {
        ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        try {
            Context context = this.mInitialApplication;
            ProviderInfo[] providerInfoArr = new ProviderInfo[SERVICE_DONE_EXECUTING_START];
            providerInfoArr[SERVICE_DONE_EXECUTING_ANON] = info;
            installContentProviders(context, Lists.newArrayList(providerInfoArr));
        } finally {
            StrictMode.setThreadPolicy(oldPolicy);
        }
    }

    private void handleEnterAnimationComplete(IBinder token) {
        ActivityClientRecord r = (ActivityClientRecord) this.mActivities.get(token);
        if (r != null) {
            r.activity.dispatchEnterAnimationComplete();
        }
    }

    public static Intent getIntentBeingBroadcast() {
        return (Intent) sCurrentBroadcastIntent.get();
    }

    private void handleReceiver(ReceiverData data) {
        unscheduleGcIdler();
        String component = data.intent.getComponent().getClassName();
        LoadedApk packageInfo = getPackageInfoNoCheck(data.info.applicationInfo, data.compatInfo);
        IActivityManager mgr = ActivityManagerNative.getDefault();
        try {
            ClassLoader cl = packageInfo.getClassLoader();
            data.intent.setExtrasClassLoader(cl);
            data.intent.prepareToEnterProcess();
            data.setExtrasClassLoader(cl);
            BroadcastReceiver receiver = (BroadcastReceiver) cl.loadClass(component).newInstance();
            try {
                ContextImpl context = (ContextImpl) packageInfo.makeApplication(DEBUG_SERVICE, this.mInstrumentation).getBaseContext();
                sCurrentBroadcastIntent.set(data.intent);
                receiver.setPendingResult(data);
                receiver.onReceive(context.getReceiverRestrictedContext(), data.intent);
            } catch (Exception e) {
                data.sendFinished(mgr);
                if (!this.mInstrumentation.onException(receiver, e)) {
                    throw new RuntimeException("Unable to start receiver " + component + ": " + e.toString(), e);
                }
            } finally {
                sCurrentBroadcastIntent.set(null);
            }
            if (receiver.getPendingResult() != null) {
                data.finish();
            }
        } catch (Exception e2) {
            data.sendFinished(mgr);
            throw new RuntimeException("Unable to instantiate receiver " + component + ": " + e2.toString(), e2);
        }
    }

    private void handleCreateBackupAgent(CreateBackupAgentData data) {
        try {
            if (getPackageManager().getPackageInfo(data.appInfo.packageName, SERVICE_DONE_EXECUTING_ANON, UserHandle.myUserId()).applicationInfo.uid != Process.myUid()) {
                Slog.w(TAG, "Asked to instantiate non-matching package " + data.appInfo.packageName);
                return;
            }
            unscheduleGcIdler();
            LoadedApk packageInfo = getPackageInfoNoCheck(data.appInfo, data.compatInfo);
            String packageName = packageInfo.mPackageName;
            if (packageName == null) {
                Slog.d(TAG, "Asked to create backup agent for nonexistent package");
                return;
            }
            String classname = data.appInfo.backupAgentName;
            if (classname == null && (data.backupMode == SERVICE_DONE_EXECUTING_START || data.backupMode == ACTIVITY_THREAD_CHECKIN_VERSION)) {
                classname = "android.app.backup.FullBackupAgent";
            }
            IBinder binder = null;
            try {
                BackupAgent agent = (BackupAgent) this.mBackupAgents.get(packageName);
                if (agent != null) {
                    binder = agent.onBind();
                } else {
                    agent = (BackupAgent) packageInfo.getClassLoader().loadClass(classname).newInstance();
                    ContextImpl context = ContextImpl.createAppContext(this, packageInfo);
                    context.setOuterContext(agent);
                    agent.attach(context);
                    agent.onCreate();
                    binder = agent.onBind();
                    this.mBackupAgents.put(packageName, agent);
                }
            } catch (Exception e) {
                Slog.e(TAG, "Agent threw during creation: " + e);
                if (!(data.backupMode == SERVICE_DONE_EXECUTING_STOP || data.backupMode == ACTIVITY_THREAD_CHECKIN_VERSION)) {
                    throw e;
                }
            } catch (Exception e2) {
                throw new RuntimeException("Unable to create BackupAgent " + classname + ": " + e2.toString(), e2);
            }
            try {
                ActivityManagerNative.getDefault().backupAgentCreated(packageName, binder);
            } catch (RemoteException e3) {
            }
        } catch (RemoteException e4) {
            Slog.e(TAG, "Can't reach package manager", e4);
        }
    }

    private void handleDestroyBackupAgent(CreateBackupAgentData data) {
        String packageName = getPackageInfoNoCheck(data.appInfo, data.compatInfo).mPackageName;
        BackupAgent agent = (BackupAgent) this.mBackupAgents.get(packageName);
        if (agent != null) {
            try {
                agent.onDestroy();
            } catch (Exception e) {
                Slog.w(TAG, "Exception thrown in onDestroy by backup agent of " + data.appInfo);
                e.printStackTrace();
            }
            this.mBackupAgents.remove(packageName);
            return;
        }
        Slog.w(TAG, "Attempt to destroy unknown backup agent " + data);
    }

    private void handleCreateService(CreateServiceData data) {
        unscheduleGcIdler();
        LoadedApk packageInfo = getPackageInfoNoCheck(data.info.applicationInfo, data.compatInfo);
        Service service = null;
        try {
            service = (Service) packageInfo.getClassLoader().loadClass(data.info.name).newInstance();
        } catch (Exception e) {
            if (!this.mInstrumentation.onException(service, e)) {
                throw new RuntimeException("Unable to instantiate service " + data.info.name + ": " + e.toString(), e);
            }
        }
        try {
            ContextImpl context = ContextImpl.createAppContext(this, packageInfo);
            context.setOuterContext(service);
            service.attach(context, this, data.info.name, data.token, packageInfo.makeApplication(DEBUG_SERVICE, this.mInstrumentation), ActivityManagerNative.getDefault());
            service.onCreate();
            this.mServices.put(data.token, service);
            try {
                ActivityManagerNative.getDefault().serviceDoneExecuting(data.token, SERVICE_DONE_EXECUTING_ANON, SERVICE_DONE_EXECUTING_ANON, SERVICE_DONE_EXECUTING_ANON);
            } catch (RemoteException e2) {
            }
        } catch (Exception e3) {
            if (!this.mInstrumentation.onException(service, e3)) {
                throw new RuntimeException("Unable to create service " + data.info.name + ": " + e3.toString(), e3);
            }
        }
    }

    private void handleBindService(BindServiceData data) {
        Service s = (Service) this.mServices.get(data.token);
        if (s != null) {
            try {
                data.intent.setExtrasClassLoader(s.getClassLoader());
                data.intent.prepareToEnterProcess();
                try {
                    if (data.rebind) {
                        s.onRebind(data.intent);
                        ActivityManagerNative.getDefault().serviceDoneExecuting(data.token, SERVICE_DONE_EXECUTING_ANON, SERVICE_DONE_EXECUTING_ANON, SERVICE_DONE_EXECUTING_ANON);
                    } else {
                        ActivityManagerNative.getDefault().publishService(data.token, data.intent, s.onBind(data.intent));
                    }
                    ensureJitEnabled();
                } catch (RemoteException e) {
                }
            } catch (Exception e2) {
                if (!this.mInstrumentation.onException(s, e2)) {
                    throw new RuntimeException("Unable to bind to service " + s + " with " + data.intent + ": " + e2.toString(), e2);
                }
            }
        }
    }

    private void handleUnbindService(BindServiceData data) {
        Service s = (Service) this.mServices.get(data.token);
        if (s != null) {
            try {
                data.intent.setExtrasClassLoader(s.getClassLoader());
                data.intent.prepareToEnterProcess();
                boolean doRebind = s.onUnbind(data.intent);
                if (doRebind) {
                    try {
                        ActivityManagerNative.getDefault().unbindFinished(data.token, data.intent, doRebind);
                        return;
                    } catch (RemoteException e) {
                        return;
                    }
                }
                ActivityManagerNative.getDefault().serviceDoneExecuting(data.token, SERVICE_DONE_EXECUTING_ANON, SERVICE_DONE_EXECUTING_ANON, SERVICE_DONE_EXECUTING_ANON);
            } catch (Exception e2) {
                if (!this.mInstrumentation.onException(s, e2)) {
                    throw new RuntimeException("Unable to unbind to service " + s + " with " + data.intent + ": " + e2.toString(), e2);
                }
            }
        }
    }

    private void handleDumpService(DumpComponentInfo info) {
        ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        try {
            Service s = (Service) this.mServices.get(info.token);
            if (s != null) {
                PrintWriter pw = new FastPrintWriter(new FileOutputStream(info.fd.getFileDescriptor()));
                s.dump(info.fd.getFileDescriptor(), pw, info.args);
                pw.flush();
            }
            IoUtils.closeQuietly(info.fd);
            StrictMode.setThreadPolicy(oldPolicy);
        } catch (Throwable th) {
            IoUtils.closeQuietly(info.fd);
            StrictMode.setThreadPolicy(oldPolicy);
        }
    }

    private void handleDumpActivity(DumpComponentInfo info) {
        ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        try {
            ActivityClientRecord r = (ActivityClientRecord) this.mActivities.get(info.token);
            if (!(r == null || r.activity == null)) {
                PrintWriter pw = new FastPrintWriter(new FileOutputStream(info.fd.getFileDescriptor()));
                r.activity.dump(info.prefix, info.fd.getFileDescriptor(), pw, info.args);
                pw.flush();
            }
            IoUtils.closeQuietly(info.fd);
            StrictMode.setThreadPolicy(oldPolicy);
        } catch (Throwable th) {
            IoUtils.closeQuietly(info.fd);
            StrictMode.setThreadPolicy(oldPolicy);
        }
    }

    private void handleDumpProvider(DumpComponentInfo info) {
        ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        try {
            ProviderClientRecord r = (ProviderClientRecord) this.mLocalProviders.get(info.token);
            if (!(r == null || r.mLocalProvider == null)) {
                PrintWriter pw = new FastPrintWriter(new FileOutputStream(info.fd.getFileDescriptor()));
                r.mLocalProvider.dump(info.fd.getFileDescriptor(), pw, info.args);
                pw.flush();
            }
            IoUtils.closeQuietly(info.fd);
            StrictMode.setThreadPolicy(oldPolicy);
        } catch (Throwable th) {
            IoUtils.closeQuietly(info.fd);
            StrictMode.setThreadPolicy(oldPolicy);
        }
    }

    private void handleServiceArgs(ServiceArgsData data) {
        Service s = (Service) this.mServices.get(data.token);
        if (s != null) {
            try {
                int res;
                if (data.args != null) {
                    data.args.setExtrasClassLoader(s.getClassLoader());
                    data.args.prepareToEnterProcess();
                }
                if (data.taskRemoved) {
                    s.onTaskRemoved(data.args);
                    res = LayoutParams.TYPE_APPLICATION_PANEL;
                } else {
                    res = s.onStartCommand(data.args, data.flags, data.startId);
                }
                QueuedWork.waitToFinish();
                try {
                    ActivityManagerNative.getDefault().serviceDoneExecuting(data.token, SERVICE_DONE_EXECUTING_START, data.startId, res);
                } catch (RemoteException e) {
                }
                ensureJitEnabled();
            } catch (Exception e2) {
                if (!this.mInstrumentation.onException(s, e2)) {
                    throw new RuntimeException("Unable to start service " + s + " with " + data.args + ": " + e2.toString(), e2);
                }
            }
        }
    }

    private void handleStopService(IBinder token) {
        Service s = (Service) this.mServices.remove(token);
        if (s != null) {
            try {
                s.onDestroy();
                Context context = s.getBaseContext();
                if (context instanceof ContextImpl) {
                    ((ContextImpl) context).scheduleFinalCleanup(s.getClassName(), "Service");
                }
                QueuedWork.waitToFinish();
                try {
                    ActivityManagerNative.getDefault().serviceDoneExecuting(token, SERVICE_DONE_EXECUTING_STOP, SERVICE_DONE_EXECUTING_ANON, SERVICE_DONE_EXECUTING_ANON);
                    return;
                } catch (RemoteException e) {
                    Slog.i(TAG, "handleStopService: unable to execute serviceDoneExecuting for " + token, e);
                    return;
                }
            } catch (Exception e2) {
                if (this.mInstrumentation.onException(s, e2)) {
                    Slog.i(TAG, "handleStopService: exception for " + token, e2);
                    return;
                }
                throw new RuntimeException("Unable to stop service " + s + ": " + e2.toString(), e2);
            }
        }
        Slog.i(TAG, "handleStopService: token=" + token + " not found.");
    }

    public final ActivityClientRecord performResumeActivity(IBinder token, boolean clearHide) {
        ActivityClientRecord r = (ActivityClientRecord) this.mActivities.get(token);
        if (!(r == null || r.activity.mFinished)) {
            if (clearHide) {
                r.hideForNow = DEBUG_SERVICE;
                r.activity.mStartedActivity = DEBUG_SERVICE;
            }
            try {
                r.activity.mFragments.noteStateNotSaved();
                if (r.pendingIntents != null) {
                    deliverNewIntents(r, r.pendingIntents);
                    r.pendingIntents = null;
                }
                if (r.pendingResults != null) {
                    deliverResults(r, r.pendingResults);
                    r.pendingResults = null;
                }
                r.activity.performResume();
                Object[] objArr = new Object[SERVICE_DONE_EXECUTING_STOP];
                objArr[SERVICE_DONE_EXECUTING_ANON] = Integer.valueOf(UserHandle.myUserId());
                objArr[SERVICE_DONE_EXECUTING_START] = r.activity.getComponentName().getClassName();
                EventLog.writeEvent(LOG_ON_RESUME_CALLED, objArr);
                r.paused = DEBUG_SERVICE;
                r.stopped = DEBUG_SERVICE;
                r.state = null;
                r.persistentState = null;
            } catch (Exception e) {
                if (!this.mInstrumentation.onException(r.activity, e)) {
                    throw new RuntimeException("Unable to resume activity " + r.intent.getComponent().toShortString() + ": " + e.toString(), e);
                }
            }
        }
        return r;
    }

    static final void cleanUpPendingRemoveWindows(ActivityClientRecord r) {
        if (r.mPendingRemoveWindow != null) {
            r.mPendingRemoveWindowManager.removeViewImmediate(r.mPendingRemoveWindow);
            IBinder wtoken = r.mPendingRemoveWindow.getWindowToken();
            if (wtoken != null) {
                WindowManagerGlobal.getInstance().closeAll(wtoken, r.activity.getClass().getName(), "Activity");
            }
        }
        r.mPendingRemoveWindow = null;
        r.mPendingRemoveWindowManager = null;
    }

    final void handleResumeActivity(IBinder token, boolean clearHide, boolean isForward, boolean reallyResume) {
        unscheduleGcIdler();
        this.mSomeActivitiesChanged = true;
        ActivityClientRecord r = performResumeActivity(token, clearHide);
        if (r != null) {
            LayoutParams l;
            Activity a = r.activity;
            int forwardBit = isForward ? InputMethodManager.CONTROL_START_INITIAL : SERVICE_DONE_EXECUTING_ANON;
            boolean willBeVisible = !a.mStartedActivity ? true : DEBUG_SERVICE;
            if (!willBeVisible) {
                try {
                    willBeVisible = ActivityManagerNative.getDefault().willActivityBeVisible(a.getActivityToken());
                } catch (RemoteException e) {
                }
            }
            if (r.window == null && !a.mFinished && willBeVisible) {
                r.window = r.activity.getWindow();
                View decor = r.window.getDecorView();
                decor.setVisibility(4);
                ViewManager wm = a.getWindowManager();
                l = r.window.getAttributes();
                a.mDecor = decor;
                l.type = SERVICE_DONE_EXECUTING_START;
                l.softInputMode |= forwardBit;
                if (a.mVisibleFromClient) {
                    a.mWindowAdded = true;
                    wm.addView(decor, l);
                }
            } else if (!willBeVisible) {
                r.hideForNow = true;
            }
            cleanUpPendingRemoveWindows(r);
            if (!(r.activity.mFinished || !willBeVisible || r.activity.mDecor == null || r.hideForNow)) {
                if (r.newConfig != null) {
                    performConfigurationChanged(r.activity, r.newConfig);
                    freeTextLayoutCachesIfNeeded(r.activity.mCurrentConfig.diff(r.newConfig));
                    r.newConfig = null;
                }
                l = r.window.getAttributes();
                if ((l.softInputMode & InputMethodManager.CONTROL_START_INITIAL) != forwardBit) {
                    l.softInputMode = (l.softInputMode & -257) | forwardBit;
                    if (r.activity.mVisibleFromClient) {
                        a.getWindowManager().updateViewLayout(r.window.getDecorView(), l);
                    }
                }
                r.activity.mVisibleFromServer = true;
                this.mNumVisibleActivities += SERVICE_DONE_EXECUTING_START;
                if (r.activity.mVisibleFromClient) {
                    r.activity.makeVisible();
                }
            }
            if (!r.onlyLocalRequest) {
                r.nextIdle = this.mNewActivities;
                this.mNewActivities = r;
                Looper.myQueue().addIdleHandler(new Idler());
            }
            r.onlyLocalRequest = DEBUG_SERVICE;
            if (reallyResume) {
                try {
                    ActivityManagerNative.getDefault().activityResumed(token);
                    return;
                } catch (RemoteException e2) {
                    return;
                }
            }
            return;
        }
        try {
            ActivityManagerNative.getDefault().finishActivity(token, SERVICE_DONE_EXECUTING_ANON, null, DEBUG_SERVICE);
        } catch (RemoteException e3) {
        }
    }

    private Bitmap createThumbnailBitmap(ActivityClientRecord r) {
        Bitmap thumbnail = this.mAvailThumbnailBitmap;
        if (thumbnail == null) {
            try {
                int h;
                int w = this.mThumbnailWidth;
                if (w < 0) {
                    Resources res = r.activity.getResources();
                    w = res.getDimensionPixelSize(R.dimen.thumbnail_width);
                    this.mThumbnailWidth = w;
                    h = res.getDimensionPixelSize(R.dimen.thumbnail_height);
                    this.mThumbnailHeight = h;
                } else {
                    h = this.mThumbnailHeight;
                }
                if (w > 0 && h > 0) {
                    thumbnail = Bitmap.createBitmap(r.activity.getResources().getDisplayMetrics(), w, h, THUMBNAIL_FORMAT);
                    thumbnail.eraseColor(SERVICE_DONE_EXECUTING_ANON);
                }
            } catch (Exception e) {
                if (this.mInstrumentation.onException(r.activity, e)) {
                    return null;
                }
                throw new RuntimeException("Unable to create thumbnail of " + r.intent.getComponent().toShortString() + ": " + e.toString(), e);
            }
        }
        if (thumbnail == null) {
            return thumbnail;
        }
        Canvas cv = this.mThumbnailCanvas;
        if (cv == null) {
            cv = new Canvas();
            this.mThumbnailCanvas = cv;
        }
        cv.setBitmap(thumbnail);
        if (!r.activity.onCreateThumbnail(thumbnail, cv)) {
            this.mAvailThumbnailBitmap = thumbnail;
            thumbnail = null;
        }
        cv.setBitmap(null);
        return thumbnail;
    }

    private void handlePauseActivity(IBinder token, boolean finished, boolean userLeaving, int configChanges, boolean dontReport) {
        ActivityClientRecord r = (ActivityClientRecord) this.mActivities.get(token);
        if (r != null) {
            if (userLeaving) {
                performUserLeavingActivity(r);
            }
            Activity activity = r.activity;
            activity.mConfigChangeFlags |= configChanges;
            performPauseActivity(token, finished, r.isPreHoneycomb());
            if (r.isPreHoneycomb()) {
                QueuedWork.waitToFinish();
            }
            if (!dontReport) {
                try {
                    ActivityManagerNative.getDefault().activityPaused(token);
                } catch (RemoteException e) {
                }
            }
            this.mSomeActivitiesChanged = true;
        }
    }

    final void performUserLeavingActivity(ActivityClientRecord r) {
        this.mInstrumentation.callActivityOnUserLeaving(r.activity);
    }

    final Bundle performPauseActivity(IBinder token, boolean finished, boolean saveState) {
        ActivityClientRecord r = (ActivityClientRecord) this.mActivities.get(token);
        return r != null ? performPauseActivity(r, finished, saveState) : null;
    }

    final Bundle performPauseActivity(ActivityClientRecord r, boolean finished, boolean saveState) {
        int size = SERVICE_DONE_EXECUTING_ANON;
        if (r.paused) {
            if (r.activity.mFinished) {
                return null;
            }
            RuntimeException e = new RuntimeException("Performing pause of activity that is not resumed: " + r.intent.getComponent().toShortString());
            Slog.e(TAG, e.getMessage(), e);
        }
        if (finished) {
            r.activity.mFinished = true;
        }
        try {
            if (!r.activity.mFinished && saveState) {
                callCallActivityOnSaveInstanceState(r);
            }
            r.activity.mCalled = DEBUG_SERVICE;
            this.mInstrumentation.callActivityOnPause(r.activity);
            Object[] objArr = new Object[SERVICE_DONE_EXECUTING_STOP];
            objArr[SERVICE_DONE_EXECUTING_ANON] = Integer.valueOf(UserHandle.myUserId());
            objArr[SERVICE_DONE_EXECUTING_START] = r.activity.getComponentName().getClassName();
            EventLog.writeEvent(LOG_ON_PAUSE_CALLED, objArr);
            if (!r.activity.mCalled) {
                throw new SuperNotCalledException("Activity " + r.intent.getComponent().toShortString() + " did not call through to super.onPause()");
            }
        } catch (SuperNotCalledException e2) {
            throw e2;
        } catch (Exception e3) {
            if (!this.mInstrumentation.onException(r.activity, e3)) {
                throw new RuntimeException("Unable to pause activity " + r.intent.getComponent().toShortString() + ": " + e3.toString(), e3);
            }
        }
        r.paused = true;
        synchronized (this.mOnPauseListeners) {
            ArrayList<OnActivityPausedListener> listeners = (ArrayList) this.mOnPauseListeners.remove(r.activity);
        }
        if (listeners != null) {
            size = listeners.size();
        }
        for (int i = SERVICE_DONE_EXECUTING_ANON; i < size; i += SERVICE_DONE_EXECUTING_START) {
            ((OnActivityPausedListener) listeners.get(i)).onPaused(r.activity);
        }
        Bundle bundle = (r.activity.mFinished || !saveState) ? null : r.state;
        return bundle;
    }

    final void performStopActivity(IBinder token, boolean saveState) {
        performStopActivityInner((ActivityClientRecord) this.mActivities.get(token), null, DEBUG_SERVICE, saveState);
    }

    private void performStopActivityInner(ActivityClientRecord r, StopInfo info, boolean keepShown, boolean saveState) {
        if (r != null) {
            if (!keepShown && r.stopped) {
                if (!r.activity.mFinished) {
                    RuntimeException e = new RuntimeException("Performing stop of activity that is not resumed: " + r.intent.getComponent().toShortString());
                    Slog.e(TAG, e.getMessage(), e);
                } else {
                    return;
                }
            }
            if (info != null) {
                try {
                    info.description = r.activity.onCreateDescription();
                } catch (Exception e2) {
                    if (!this.mInstrumentation.onException(r.activity, e2)) {
                        throw new RuntimeException("Unable to save state of activity " + r.intent.getComponent().toShortString() + ": " + e2.toString(), e2);
                    }
                }
            }
            if (!r.activity.mFinished && saveState && r.state == null) {
                callCallActivityOnSaveInstanceState(r);
            }
            if (!keepShown) {
                try {
                    r.activity.performStop();
                } catch (Exception e22) {
                    if (!this.mInstrumentation.onException(r.activity, e22)) {
                        throw new RuntimeException("Unable to stop activity " + r.intent.getComponent().toShortString() + ": " + e22.toString(), e22);
                    }
                }
                r.stopped = true;
            }
            r.paused = true;
        }
    }

    private void updateVisibility(ActivityClientRecord r, boolean show) {
        View v = r.activity.mDecor;
        if (v == null) {
            return;
        }
        if (show) {
            if (!r.activity.mVisibleFromServer) {
                r.activity.mVisibleFromServer = true;
                this.mNumVisibleActivities += SERVICE_DONE_EXECUTING_START;
                if (r.activity.mVisibleFromClient) {
                    r.activity.makeVisible();
                }
            }
            if (r.newConfig != null) {
                performConfigurationChanged(r.activity, r.newConfig);
                freeTextLayoutCachesIfNeeded(r.activity.mCurrentConfig.diff(r.newConfig));
                r.newConfig = null;
            }
        } else if (r.activity.mVisibleFromServer) {
            r.activity.mVisibleFromServer = DEBUG_SERVICE;
            this.mNumVisibleActivities--;
            v.setVisibility(4);
        }
    }

    private void handleStopActivity(IBinder token, boolean show, int configChanges) {
        ActivityClientRecord r = (ActivityClientRecord) this.mActivities.get(token);
        Activity activity = r.activity;
        activity.mConfigChangeFlags |= configChanges;
        StopInfo info = new StopInfo();
        performStopActivityInner(r, info, show, true);
        updateVisibility(r, show);
        if (!r.isPreHoneycomb()) {
            QueuedWork.waitToFinish();
        }
        info.activity = r;
        info.state = r.state;
        info.persistentState = r.persistentState;
        this.mH.post(info);
        this.mSomeActivitiesChanged = true;
    }

    final void performRestartActivity(IBinder token) {
        ActivityClientRecord r = (ActivityClientRecord) this.mActivities.get(token);
        if (r.stopped) {
            r.activity.performRestart();
            r.stopped = DEBUG_SERVICE;
        }
    }

    private void handleWindowVisibility(IBinder token, boolean show) {
        ActivityClientRecord r = (ActivityClientRecord) this.mActivities.get(token);
        if (r == null) {
            Log.w(TAG, "handleWindowVisibility: no activity for token " + token);
            return;
        }
        if (!show && !r.stopped) {
            performStopActivityInner(r, null, show, DEBUG_SERVICE);
        } else if (show && r.stopped) {
            unscheduleGcIdler();
            r.activity.performRestart();
            r.stopped = DEBUG_SERVICE;
        }
        if (r.activity.mDecor != null) {
            updateVisibility(r, show);
        }
        this.mSomeActivitiesChanged = true;
    }

    private void handleSleeping(IBinder token, boolean sleeping) {
        ActivityClientRecord r = (ActivityClientRecord) this.mActivities.get(token);
        if (r == null) {
            Log.w(TAG, "handleSleeping: no activity for token " + token);
        } else if (sleeping) {
            if (!(r.stopped || r.isPreHoneycomb())) {
                try {
                    r.activity.performStop();
                } catch (Exception e) {
                    if (!this.mInstrumentation.onException(r.activity, e)) {
                        throw new RuntimeException("Unable to stop activity " + r.intent.getComponent().toShortString() + ": " + e.toString(), e);
                    }
                }
                r.stopped = true;
            }
            if (!r.isPreHoneycomb()) {
                QueuedWork.waitToFinish();
            }
            try {
                ActivityManagerNative.getDefault().activitySlept(r.token);
            } catch (RemoteException e2) {
            }
        } else if (r.stopped && r.activity.mVisibleFromServer) {
            r.activity.performRestart();
            r.stopped = DEBUG_SERVICE;
        }
    }

    private void handleSetCoreSettings(Bundle coreSettings) {
        synchronized (this.mResourcesManager) {
            this.mCoreSettings = coreSettings;
        }
        onCoreSettingsChange();
    }

    private void onCoreSettingsChange() {
        boolean debugViewAttributes;
        if (this.mCoreSettings.getInt(Global.DEBUG_VIEW_ATTRIBUTES, SERVICE_DONE_EXECUTING_ANON) != 0) {
            debugViewAttributes = true;
        } else {
            debugViewAttributes = DEBUG_SERVICE;
        }
        if (debugViewAttributes != View.mDebugViewAttributes) {
            View.mDebugViewAttributes = debugViewAttributes;
            for (Entry<IBinder, ActivityClientRecord> entry : this.mActivities.entrySet()) {
                requestRelaunchActivity((IBinder) entry.getKey(), null, null, SERVICE_DONE_EXECUTING_ANON, DEBUG_SERVICE, null, DEBUG_SERVICE);
            }
        }
    }

    private void handleUpdatePackageCompatibilityInfo(UpdateCompatibilityData data) {
        LoadedApk apk = peekPackageInfo(data.pkg, DEBUG_SERVICE);
        if (apk != null) {
            apk.setCompatibilityInfo(data.info);
        }
        apk = peekPackageInfo(data.pkg, true);
        if (apk != null) {
            apk.setCompatibilityInfo(data.info);
        }
        handleConfigurationChanged(this.mConfiguration, data.info);
        WindowManagerGlobal.getInstance().reportNewConfiguration(this.mConfiguration);
    }

    private void deliverResults(ActivityClientRecord r, List<ResultInfo> results) {
        int N = results.size();
        for (int i = SERVICE_DONE_EXECUTING_ANON; i < N; i += SERVICE_DONE_EXECUTING_START) {
            ResultInfo ri = (ResultInfo) results.get(i);
            try {
                if (ri.mData != null) {
                    ri.mData.setExtrasClassLoader(r.activity.getClassLoader());
                    ri.mData.prepareToEnterProcess();
                }
                r.activity.dispatchActivityResult(ri.mResultWho, ri.mRequestCode, ri.mResultCode, ri.mData);
            } catch (Exception e) {
                if (!this.mInstrumentation.onException(r.activity, e)) {
                    throw new RuntimeException("Failure delivering result " + ri + " to activity " + r.intent.getComponent().toShortString() + ": " + e.toString(), e);
                }
            }
        }
    }

    private void handleSendResult(ResultData res) {
        ActivityClientRecord r = (ActivityClientRecord) this.mActivities.get(res.token);
        if (r != null) {
            boolean resumed;
            if (r.paused) {
                resumed = DEBUG_SERVICE;
            } else {
                resumed = true;
            }
            if (!r.activity.mFinished && r.activity.mDecor != null && r.hideForNow && resumed) {
                updateVisibility(r, true);
            }
            if (resumed) {
                try {
                    r.activity.mCalled = DEBUG_SERVICE;
                    r.activity.mTemporaryPause = true;
                    this.mInstrumentation.callActivityOnPause(r.activity);
                    if (!r.activity.mCalled) {
                        throw new SuperNotCalledException("Activity " + r.intent.getComponent().toShortString() + " did not call through to super.onPause()");
                    }
                } catch (SuperNotCalledException e) {
                    throw e;
                } catch (Exception e2) {
                    if (!this.mInstrumentation.onException(r.activity, e2)) {
                        throw new RuntimeException("Unable to pause activity " + r.intent.getComponent().toShortString() + ": " + e2.toString(), e2);
                    }
                }
            }
            deliverResults(r, res.results);
            if (resumed) {
                r.activity.performResume();
                r.activity.mTemporaryPause = DEBUG_SERVICE;
            }
        }
    }

    public final ActivityClientRecord performDestroyActivity(IBinder token, boolean finishing) {
        return performDestroyActivity(token, finishing, SERVICE_DONE_EXECUTING_ANON, DEBUG_SERVICE);
    }

    private ActivityClientRecord performDestroyActivity(IBinder token, boolean finishing, int configChanges, boolean getNonConfigInstance) {
        ActivityClientRecord r = (ActivityClientRecord) this.mActivities.get(token);
        Class<? extends Activity> activityClass = null;
        if (r != null) {
            activityClass = r.activity.getClass();
            Activity activity = r.activity;
            activity.mConfigChangeFlags |= configChanges;
            if (finishing) {
                r.activity.mFinished = true;
            }
            if (!r.paused) {
                try {
                    r.activity.mCalled = DEBUG_SERVICE;
                    this.mInstrumentation.callActivityOnPause(r.activity);
                    Object[] objArr = new Object[SERVICE_DONE_EXECUTING_STOP];
                    objArr[SERVICE_DONE_EXECUTING_ANON] = Integer.valueOf(UserHandle.myUserId());
                    objArr[SERVICE_DONE_EXECUTING_START] = r.activity.getComponentName().getClassName();
                    EventLog.writeEvent(LOG_ON_PAUSE_CALLED, objArr);
                    if (!r.activity.mCalled) {
                        throw new SuperNotCalledException("Activity " + safeToComponentShortString(r.intent) + " did not call through to super.onPause()");
                    }
                } catch (SuperNotCalledException e) {
                    throw e;
                } catch (Exception e2) {
                    if (!this.mInstrumentation.onException(r.activity, e2)) {
                        throw new RuntimeException("Unable to pause activity " + safeToComponentShortString(r.intent) + ": " + e2.toString(), e2);
                    }
                }
                r.paused = true;
            }
            if (!r.stopped) {
                try {
                    r.activity.performStop();
                } catch (SuperNotCalledException e3) {
                    throw e3;
                } catch (Exception e22) {
                    if (!this.mInstrumentation.onException(r.activity, e22)) {
                        throw new RuntimeException("Unable to stop activity " + safeToComponentShortString(r.intent) + ": " + e22.toString(), e22);
                    }
                }
                r.stopped = true;
            }
            if (getNonConfigInstance) {
                try {
                    r.lastNonConfigurationInstances = r.activity.retainNonConfigurationInstances();
                } catch (Exception e222) {
                    if (!this.mInstrumentation.onException(r.activity, e222)) {
                        throw new RuntimeException("Unable to retain activity " + r.intent.getComponent().toShortString() + ": " + e222.toString(), e222);
                    }
                }
            }
            try {
                r.activity.mCalled = DEBUG_SERVICE;
                this.mInstrumentation.callActivityOnDestroy(r.activity);
                if (!r.activity.mCalled) {
                    throw new SuperNotCalledException("Activity " + safeToComponentShortString(r.intent) + " did not call through to super.onDestroy()");
                } else if (r.window != null) {
                    r.window.closeAllPanels();
                }
            } catch (SuperNotCalledException e32) {
                throw e32;
            } catch (Exception e2222) {
                if (!this.mInstrumentation.onException(r.activity, e2222)) {
                    throw new RuntimeException("Unable to destroy activity " + safeToComponentShortString(r.intent) + ": " + e2222.toString(), e2222);
                }
            }
        }
        this.mActivities.remove(token);
        StrictMode.decrementExpectedActivityCount(activityClass);
        return r;
    }

    private static String safeToComponentShortString(Intent intent) {
        ComponentName component = intent.getComponent();
        return component == null ? "[Unknown]" : component.toShortString();
    }

    private void handleDestroyActivity(IBinder token, boolean finishing, int configChanges, boolean getNonConfigInstance) {
        ActivityClientRecord r = performDestroyActivity(token, finishing, configChanges, getNonConfigInstance);
        if (r != null) {
            cleanUpPendingRemoveWindows(r);
            WindowManager wm = r.activity.getWindowManager();
            View v = r.activity.mDecor;
            if (v != null) {
                if (r.activity.mVisibleFromServer) {
                    this.mNumVisibleActivities--;
                }
                IBinder wtoken = v.getWindowToken();
                if (r.activity.mWindowAdded) {
                    if (r.onlyLocalRequest) {
                        r.mPendingRemoveWindow = v;
                        r.mPendingRemoveWindowManager = wm;
                    } else {
                        wm.removeViewImmediate(v);
                    }
                }
                if (wtoken != null && r.mPendingRemoveWindow == null) {
                    WindowManagerGlobal.getInstance().closeAll(wtoken, r.activity.getClass().getName(), "Activity");
                }
                r.activity.mDecor = null;
            }
            if (r.mPendingRemoveWindow == null) {
                WindowManagerGlobal.getInstance().closeAll(token, r.activity.getClass().getName(), "Activity");
            }
            Context c = r.activity.getBaseContext();
            if (c instanceof ContextImpl) {
                ((ContextImpl) c).scheduleFinalCleanup(r.activity.getClass().getName(), "Activity");
            }
        }
        if (finishing) {
            try {
                ActivityManagerNative.getDefault().activityDestroyed(token);
            } catch (RemoteException e) {
            }
        }
        this.mSomeActivitiesChanged = true;
    }

    public final void requestRelaunchActivity(IBinder token, List<ResultInfo> pendingResults, List<ReferrerIntent> pendingNewIntents, int configChanges, boolean notResumed, Configuration config, boolean fromServer) {
        ActivityClientRecord target = null;
        synchronized (this.mResourcesManager) {
            ActivityClientRecord target2;
            int i = SERVICE_DONE_EXECUTING_ANON;
            while (i < this.mRelaunchingActivities.size()) {
                ActivityClientRecord existing;
                try {
                    ActivityClientRecord r = (ActivityClientRecord) this.mRelaunchingActivities.get(i);
                    if (r.token == token) {
                        target = r;
                        if (pendingResults != null) {
                            if (r.pendingResults != null) {
                                r.pendingResults.addAll(pendingResults);
                            } else {
                                r.pendingResults = pendingResults;
                            }
                        }
                        if (pendingNewIntents != null) {
                            if (r.pendingIntents != null) {
                                r.pendingIntents.addAll(pendingNewIntents);
                                target2 = target;
                            } else {
                                r.pendingIntents = pendingNewIntents;
                                target2 = target;
                            }
                            if (target2 == null) {
                                try {
                                    target = new ActivityClientRecord();
                                    target.token = token;
                                    target.pendingResults = pendingResults;
                                    target.pendingIntents = pendingNewIntents;
                                    if (!fromServer) {
                                        existing = (ActivityClientRecord) this.mActivities.get(token);
                                        if (existing != null) {
                                            target.startsNotResumed = existing.paused;
                                        }
                                        target.onlyLocalRequest = true;
                                    }
                                    this.mRelaunchingActivities.add(target);
                                    sendMessage(KeyEvent.KEYCODE_MEDIA_PLAY, target);
                                } catch (Throwable th) {
                                    Throwable th2 = th;
                                    target = target2;
                                    throw th2;
                                }
                            }
                            target = target2;
                            if (fromServer) {
                                target.startsNotResumed = notResumed;
                                target.onlyLocalRequest = DEBUG_SERVICE;
                            }
                            if (config != null) {
                                target.createdConfig = config;
                            }
                            target.pendingConfigChanges |= configChanges;
                        }
                        target2 = target;
                        if (target2 == null) {
                            target = target2;
                        } else {
                            target = new ActivityClientRecord();
                            target.token = token;
                            target.pendingResults = pendingResults;
                            target.pendingIntents = pendingNewIntents;
                            if (fromServer) {
                                existing = (ActivityClientRecord) this.mActivities.get(token);
                                if (existing != null) {
                                    target.startsNotResumed = existing.paused;
                                }
                                target.onlyLocalRequest = true;
                            }
                            this.mRelaunchingActivities.add(target);
                            sendMessage(KeyEvent.KEYCODE_MEDIA_PLAY, target);
                        }
                        if (fromServer) {
                            target.startsNotResumed = notResumed;
                            target.onlyLocalRequest = DEBUG_SERVICE;
                        }
                        if (config != null) {
                            target.createdConfig = config;
                        }
                        target.pendingConfigChanges |= configChanges;
                    }
                    i += SERVICE_DONE_EXECUTING_START;
                } catch (Throwable th3) {
                    th2 = th3;
                }
            }
            target2 = target;
            if (target2 == null) {
                target = new ActivityClientRecord();
                target.token = token;
                target.pendingResults = pendingResults;
                target.pendingIntents = pendingNewIntents;
                if (fromServer) {
                    existing = (ActivityClientRecord) this.mActivities.get(token);
                    if (existing != null) {
                        target.startsNotResumed = existing.paused;
                    }
                    target.onlyLocalRequest = true;
                }
                this.mRelaunchingActivities.add(target);
                sendMessage(KeyEvent.KEYCODE_MEDIA_PLAY, target);
            } else {
                target = target2;
            }
            if (fromServer) {
                target.startsNotResumed = notResumed;
                target.onlyLocalRequest = DEBUG_SERVICE;
            }
            if (config != null) {
                target.createdConfig = config;
            }
            target.pendingConfigChanges |= configChanges;
        }
    }

    private void handleRelaunchActivity(ActivityClientRecord tmp) {
        unscheduleGcIdler();
        this.mSomeActivitiesChanged = true;
        Configuration changedConfig = null;
        int configChanges = SERVICE_DONE_EXECUTING_ANON;
        synchronized (this.mResourcesManager) {
            int N = this.mRelaunchingActivities.size();
            IBinder token = tmp.token;
            tmp = null;
            int i = SERVICE_DONE_EXECUTING_ANON;
            while (i < N) {
                ActivityClientRecord r = (ActivityClientRecord) this.mRelaunchingActivities.get(i);
                if (r.token == token) {
                    tmp = r;
                    configChanges |= tmp.pendingConfigChanges;
                    this.mRelaunchingActivities.remove(i);
                    i--;
                    N--;
                }
                i += SERVICE_DONE_EXECUTING_START;
            }
            if (tmp == null) {
                return;
            }
            if (this.mPendingConfiguration != null) {
                changedConfig = this.mPendingConfiguration;
                this.mPendingConfiguration = null;
            }
            if (tmp.createdConfig != null && ((this.mConfiguration == null || (tmp.createdConfig.isOtherSeqNewer(this.mConfiguration) && this.mConfiguration.diff(tmp.createdConfig) != 0)) && (changedConfig == null || tmp.createdConfig.isOtherSeqNewer(changedConfig)))) {
                changedConfig = tmp.createdConfig;
            }
            if (changedConfig != null) {
                this.mCurDefaultDisplayDpi = changedConfig.densityDpi;
                updateDefaultDensity();
                handleConfigurationChanged(changedConfig, null);
            }
            r = (ActivityClientRecord) this.mActivities.get(tmp.token);
            if (r != null) {
                Activity activity = r.activity;
                activity.mConfigChangeFlags |= configChanges;
                r.onlyLocalRequest = tmp.onlyLocalRequest;
                Intent currentIntent = r.activity.mIntent;
                r.activity.mChangingConfigurations = true;
                if (!r.paused) {
                    performPauseActivity(r.token, (boolean) DEBUG_SERVICE, r.isPreHoneycomb());
                }
                if (!(r.state != null || r.stopped || r.isPreHoneycomb())) {
                    callCallActivityOnSaveInstanceState(r);
                }
                handleDestroyActivity(r.token, DEBUG_SERVICE, configChanges, true);
                r.activity = null;
                r.window = null;
                r.hideForNow = DEBUG_SERVICE;
                r.nextIdle = null;
                if (tmp.pendingResults != null) {
                    if (r.pendingResults == null) {
                        r.pendingResults = tmp.pendingResults;
                    } else {
                        r.pendingResults.addAll(tmp.pendingResults);
                    }
                }
                if (tmp.pendingIntents != null) {
                    if (r.pendingIntents == null) {
                        r.pendingIntents = tmp.pendingIntents;
                    } else {
                        r.pendingIntents.addAll(tmp.pendingIntents);
                    }
                }
                r.startsNotResumed = tmp.startsNotResumed;
                handleLaunchActivity(r, currentIntent);
            }
        }
    }

    private void callCallActivityOnSaveInstanceState(ActivityClientRecord r) {
        r.state = new Bundle();
        r.state.setAllowFds(DEBUG_SERVICE);
        if (r.isPersistable()) {
            r.persistentState = new PersistableBundle();
            this.mInstrumentation.callActivityOnSaveInstanceState(r.activity, r.state, r.persistentState);
            return;
        }
        this.mInstrumentation.callActivityOnSaveInstanceState(r.activity, r.state);
    }

    ArrayList<ComponentCallbacks2> collectComponentCallbacks(boolean allActivities, Configuration newConfig) {
        ArrayList<ComponentCallbacks2> callbacks = new ArrayList();
        synchronized (this.mResourcesManager) {
            int i;
            int NAPP = this.mAllApplications.size();
            for (i = SERVICE_DONE_EXECUTING_ANON; i < NAPP; i += SERVICE_DONE_EXECUTING_START) {
                callbacks.add(this.mAllApplications.get(i));
            }
            int NACT = this.mActivities.size();
            for (i = SERVICE_DONE_EXECUTING_ANON; i < NACT; i += SERVICE_DONE_EXECUTING_START) {
                ActivityClientRecord ar = (ActivityClientRecord) this.mActivities.valueAt(i);
                Activity a = ar.activity;
                if (a != null) {
                    Configuration thisConfig = applyConfigCompatMainThread(this.mCurDefaultDisplayDpi, newConfig, ar.packageInfo.getCompatibilityInfo());
                    if (!ar.activity.mFinished && (allActivities || !ar.paused)) {
                        callbacks.add(a);
                    } else if (thisConfig != null) {
                        ar.newConfig = thisConfig;
                    }
                }
            }
            int NSVC = this.mServices.size();
            for (i = SERVICE_DONE_EXECUTING_ANON; i < NSVC; i += SERVICE_DONE_EXECUTING_START) {
                callbacks.add(this.mServices.valueAt(i));
            }
        }
        synchronized (this.mProviderMap) {
            int NPRV = this.mLocalProviders.size();
            for (i = SERVICE_DONE_EXECUTING_ANON; i < NPRV; i += SERVICE_DONE_EXECUTING_START) {
                callbacks.add(((ProviderClientRecord) this.mLocalProviders.valueAt(i)).mLocalProvider);
            }
        }
        return callbacks;
    }

    private static void performConfigurationChanged(ComponentCallbacks2 cb, Configuration config) {
        Activity activity = cb instanceof Activity ? (Activity) cb : null;
        if (activity != null) {
            activity.mCalled = DEBUG_SERVICE;
        }
        boolean shouldChangeConfig = DEBUG_SERVICE;
        if (activity == null || activity.mCurrentConfig == null) {
            shouldChangeConfig = true;
        } else {
            int diff = activity.mCurrentConfig.diff(config);
            if (diff != 0 && ((activity.mActivityInfo.getRealConfigChanged() ^ -1) & diff) == 0) {
                shouldChangeConfig = true;
            }
        }
        if (shouldChangeConfig) {
            cb.onConfigurationChanged(config);
            if (activity == null) {
                return;
            }
            if (activity.mCalled) {
                activity.mConfigChangeFlags = SERVICE_DONE_EXECUTING_ANON;
                activity.mCurrentConfig = new Configuration(config);
                return;
            }
            throw new SuperNotCalledException("Activity " + activity.getLocalClassName() + " did not call through to super.onConfigurationChanged()");
        }
    }

    public final void applyConfigurationToResources(Configuration config) {
        synchronized (this.mResourcesManager) {
            this.mResourcesManager.applyConfigurationToResourcesLocked(config, null);
        }
    }

    final Configuration applyCompatConfiguration(int displayDensity) {
        Configuration config = this.mConfiguration;
        if (this.mCompatConfiguration == null) {
            this.mCompatConfiguration = new Configuration();
        }
        this.mCompatConfiguration.setTo(this.mConfiguration);
        if (this.mResourcesManager.applyCompatConfiguration(displayDensity, this.mCompatConfiguration)) {
            return this.mCompatConfiguration;
        }
        return config;
    }

    final void handleConfigurationChanged(Configuration config, CompatibilityInfo compat) {
        synchronized (this.mResourcesManager) {
            if (this.mPendingConfiguration != null) {
                if (!this.mPendingConfiguration.isOtherSeqNewer(config)) {
                    config = this.mPendingConfiguration;
                    this.mCurDefaultDisplayDpi = config.densityDpi;
                    updateDefaultDensity();
                }
                this.mPendingConfiguration = null;
            }
            if (config == null) {
                return;
            }
            this.mResourcesManager.applyConfigurationToResourcesLocked(config, compat);
            if (this.mConfiguration == null) {
                this.mConfiguration = new Configuration();
            }
            if (this.mConfiguration.isOtherSeqNewer(config) || compat != null) {
                int configDiff = this.mConfiguration.diff(config);
                this.mConfiguration.updateFrom(config);
                config = applyCompatConfiguration(this.mCurDefaultDisplayDpi);
                ArrayList<ComponentCallbacks2> callbacks = collectComponentCallbacks(DEBUG_SERVICE, config);
                freeTextLayoutCachesIfNeeded(configDiff);
                if (callbacks != null) {
                    int N = callbacks.size();
                    for (int i = SERVICE_DONE_EXECUTING_ANON; i < N; i += SERVICE_DONE_EXECUTING_START) {
                        performConfigurationChanged((ComponentCallbacks2) callbacks.get(i), config);
                    }
                    return;
                }
                return;
            }
        }
    }

    static void freeTextLayoutCachesIfNeeded(int configDiff) {
        if (configDiff != 0) {
            if ((configDiff & 4) != 0 ? true : DEBUG_SERVICE) {
                Canvas.freeTextLayoutCaches();
            }
        }
    }

    final void handleActivityConfigurationChanged(IBinder token) {
        ActivityClientRecord r = (ActivityClientRecord) this.mActivities.get(token);
        if (r != null && r.activity != null) {
            performConfigurationChanged(r.activity, this.mCompatConfiguration);
            freeTextLayoutCachesIfNeeded(r.activity.mCurrentConfig.diff(this.mCompatConfiguration));
            this.mSomeActivitiesChanged = true;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final void handleProfilerControl(boolean r5, android.app.ProfilerInfo r6, int r7) {
        /*
        r4 = this;
        if (r5 == 0) goto L_0x005b;
    L_0x0002:
        r1 = r4.mProfiler;	 Catch:{ RuntimeException -> 0x001b }
        r1.setProfiler(r6);	 Catch:{ RuntimeException -> 0x001b }
        r1 = r4.mProfiler;	 Catch:{ RuntimeException -> 0x001b }
        r1.startProfiling();	 Catch:{ RuntimeException -> 0x001b }
        r1 = r6.profileFd;	 Catch:{ IOException -> 0x0012 }
        r1.close();	 Catch:{ IOException -> 0x0012 }
    L_0x0011:
        return;
    L_0x0012:
        r0 = move-exception;
        r1 = "ActivityThread";
        r2 = "Failure closing profile fd";
        android.util.Slog.w(r1, r2, r0);
        goto L_0x0011;
    L_0x001b:
        r0 = move-exception;
        r1 = "ActivityThread";
        r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x004b }
        r2.<init>();	 Catch:{ all -> 0x004b }
        r3 = "Profiling failed on path ";
        r2 = r2.append(r3);	 Catch:{ all -> 0x004b }
        r3 = r6.profileFile;	 Catch:{ all -> 0x004b }
        r2 = r2.append(r3);	 Catch:{ all -> 0x004b }
        r3 = " -- can the process access this path?";
        r2 = r2.append(r3);	 Catch:{ all -> 0x004b }
        r2 = r2.toString();	 Catch:{ all -> 0x004b }
        android.util.Slog.w(r1, r2);	 Catch:{ all -> 0x004b }
        r1 = r6.profileFd;	 Catch:{ IOException -> 0x0042 }
        r1.close();	 Catch:{ IOException -> 0x0042 }
        goto L_0x0011;
    L_0x0042:
        r0 = move-exception;
        r1 = "ActivityThread";
        r2 = "Failure closing profile fd";
        android.util.Slog.w(r1, r2, r0);
        goto L_0x0011;
    L_0x004b:
        r1 = move-exception;
        r2 = r6.profileFd;	 Catch:{ IOException -> 0x0052 }
        r2.close();	 Catch:{ IOException -> 0x0052 }
    L_0x0051:
        throw r1;
    L_0x0052:
        r0 = move-exception;
        r2 = "ActivityThread";
        r3 = "Failure closing profile fd";
        android.util.Slog.w(r2, r3, r0);
        goto L_0x0051;
    L_0x005b:
        r1 = r4.mProfiler;
        r1.stopProfiling();
        goto L_0x0011;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.app.ActivityThread.handleProfilerControl(boolean, android.app.ProfilerInfo, int):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static final void handleDumpHeap(boolean r4, android.app.ActivityThread.DumpHeapData r5) {
        /*
        if (r4 == 0) goto L_0x005c;
    L_0x0002:
        r1 = r5.path;	 Catch:{ IOException -> 0x001c }
        r2 = r5.fd;	 Catch:{ IOException -> 0x001c }
        r2 = r2.getFileDescriptor();	 Catch:{ IOException -> 0x001c }
        android.os.Debug.dumpHprofData(r1, r2);	 Catch:{ IOException -> 0x001c }
        r1 = r5.fd;	 Catch:{ IOException -> 0x0013 }
        r1.close();	 Catch:{ IOException -> 0x0013 }
    L_0x0012:
        return;
    L_0x0013:
        r0 = move-exception;
        r1 = "ActivityThread";
        r2 = "Failure closing profile fd";
        android.util.Slog.w(r1, r2, r0);
        goto L_0x0012;
    L_0x001c:
        r0 = move-exception;
        r1 = "ActivityThread";
        r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x004c }
        r2.<init>();	 Catch:{ all -> 0x004c }
        r3 = "Managed heap dump failed on path ";
        r2 = r2.append(r3);	 Catch:{ all -> 0x004c }
        r3 = r5.path;	 Catch:{ all -> 0x004c }
        r2 = r2.append(r3);	 Catch:{ all -> 0x004c }
        r3 = " -- can the process access this path?";
        r2 = r2.append(r3);	 Catch:{ all -> 0x004c }
        r2 = r2.toString();	 Catch:{ all -> 0x004c }
        android.util.Slog.w(r1, r2);	 Catch:{ all -> 0x004c }
        r1 = r5.fd;	 Catch:{ IOException -> 0x0043 }
        r1.close();	 Catch:{ IOException -> 0x0043 }
        goto L_0x0012;
    L_0x0043:
        r0 = move-exception;
        r1 = "ActivityThread";
        r2 = "Failure closing profile fd";
        android.util.Slog.w(r1, r2, r0);
        goto L_0x0012;
    L_0x004c:
        r1 = move-exception;
        r2 = r5.fd;	 Catch:{ IOException -> 0x0053 }
        r2.close();	 Catch:{ IOException -> 0x0053 }
    L_0x0052:
        throw r1;
    L_0x0053:
        r0 = move-exception;
        r2 = "ActivityThread";
        r3 = "Failure closing profile fd";
        android.util.Slog.w(r2, r3, r0);
        goto L_0x0052;
    L_0x005c:
        r1 = r5.fd;
        r1 = r1.getFileDescriptor();
        android.os.Debug.dumpNativeHeap(r1);
        goto L_0x0012;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.app.ActivityThread.handleDumpHeap(boolean, android.app.ActivityThread$DumpHeapData):void");
    }

    final void handleDispatchPackageBroadcast(int cmd, String[] packages) {
        boolean hasPkgInfo = DEBUG_SERVICE;
        if (packages != null) {
            for (int i = packages.length - 1; i >= 0; i--) {
                if (!hasPkgInfo) {
                    WeakReference<LoadedApk> ref = (WeakReference) this.mPackages.get(packages[i]);
                    if (ref == null || ref.get() == null) {
                        ref = (WeakReference) this.mResourcePackages.get(packages[i]);
                        if (!(ref == null || ref.get() == null)) {
                            hasPkgInfo = true;
                        }
                    } else {
                        hasPkgInfo = true;
                    }
                }
                this.mPackages.remove(packages[i]);
                this.mResourcePackages.remove(packages[i]);
            }
        }
        ApplicationPackageManager.handlePackageBroadcast(cmd, packages, hasPkgInfo);
    }

    final void handleLowMemory() {
        ArrayList<ComponentCallbacks2> callbacks = collectComponentCallbacks(true, null);
        int N = callbacks.size();
        for (int i = SERVICE_DONE_EXECUTING_ANON; i < N; i += SERVICE_DONE_EXECUTING_START) {
            ((ComponentCallbacks2) callbacks.get(i)).onLowMemory();
        }
        if (Process.myUid() != LayoutParams.TYPE_APPLICATION_PANEL) {
            EventLog.writeEvent(SQLITE_MEM_RELEASED_EVENT_LOG_TAG, SQLiteDatabase.releaseMemory());
        }
        Canvas.freeCaches();
        Canvas.freeTextLayoutCaches();
        BinderInternal.forceGc("mem");
    }

    final void handleTrimMemory(int level) {
        ArrayList<ComponentCallbacks2> callbacks = collectComponentCallbacks(true, null);
        int N = callbacks.size();
        for (int i = SERVICE_DONE_EXECUTING_ANON; i < N; i += SERVICE_DONE_EXECUTING_START) {
            ((ComponentCallbacks2) callbacks.get(i)).onTrimMemory(level);
        }
        WindowManagerGlobal.getInstance().trimMemory(level);
    }

    private void setupGraphicsSupport(LoadedApk info, File cacheDir) {
        if (!Process.isIsolated()) {
            try {
                String[] packages = getPackageManager().getPackagesForUid(Process.myUid());
                if (packages != null && packages.length == SERVICE_DONE_EXECUTING_START) {
                    HardwareRenderer.setupDiskCache(cacheDir);
                    RenderScript.setupDiskCache(cacheDir);
                }
            } catch (RemoteException e) {
            }
        }
    }

    private void updateDefaultDensity() {
        if (this.mCurDefaultDisplayDpi != 0 && this.mCurDefaultDisplayDpi != DisplayMetrics.DENSITY_DEVICE && !this.mDensityCompatMode) {
            Slog.i(TAG, "Switching default density from " + DisplayMetrics.DENSITY_DEVICE + " to " + this.mCurDefaultDisplayDpi);
            DisplayMetrics.DENSITY_DEVICE = this.mCurDefaultDisplayDpi;
            Bitmap.setDefaultDensity(KeyEvent.KEYCODE_NUMPAD_ENTER);
        }
    }

    private void handleBindApplication(AppBindData data) {
        this.mBoundApplication = data;
        this.mConfiguration = new Configuration(data.config);
        this.mCompatConfiguration = new Configuration(data.config);
        this.mProfiler = new Profiler();
        if (data.initProfilerInfo != null) {
            this.mProfiler.profileFile = data.initProfilerInfo.profileFile;
            this.mProfiler.profileFd = data.initProfilerInfo.profileFd;
            this.mProfiler.samplingInterval = data.initProfilerInfo.samplingInterval;
            this.mProfiler.autoStopProfiler = data.initProfilerInfo.autoStopProfiler;
        }
        Process.setArgV0(data.processName);
        DdmHandleAppName.setAppName(data.processName, UserHandle.myUserId());
        if (data.persistent && !ActivityManager.isHighEndGfx()) {
            HardwareRenderer.disable(DEBUG_SERVICE);
        }
        if (this.mProfiler.profileFd != null) {
            this.mProfiler.startProfiling();
        }
        if (data.appInfo.targetSdkVersion <= 12) {
            AsyncTask.setDefaultExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        Message.updateCheckRecycle(data.appInfo.targetSdkVersion);
        TimeZone.setDefault(null);
        Locale.setDefault(data.config.locale);
        this.mResourcesManager.applyConfigurationToResourcesLocked(data.config, data.compatInfo);
        this.mCurDefaultDisplayDpi = data.config.densityDpi;
        applyCompatConfiguration(this.mCurDefaultDisplayDpi);
        data.info = getPackageInfoNoCheck(data.appInfo, data.compatInfo);
        if ((data.appInfo.flags & AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD) == 0) {
            this.mDensityCompatMode = true;
            Bitmap.setDefaultDensity(KeyEvent.KEYCODE_NUMPAD_ENTER);
        }
        updateDefaultDensity();
        Context appContext = ContextImpl.createAppContext(this, data.info);
        if (!Process.isIsolated()) {
            File cacheDir = appContext.getCacheDir();
            if (cacheDir != null) {
                System.setProperty("java.io.tmpdir", cacheDir.getAbsolutePath());
                setupGraphicsSupport(data.info, cacheDir);
            } else {
                Log.e(TAG, "Unable to setupGraphicsSupport due to missing cache directory");
            }
        }
        DateFormat.set24HourTimePref("24".equals(this.mCoreSettings.getString(System.TIME_12_24)));
        View.mDebugViewAttributes = this.mCoreSettings.getInt(Global.DEBUG_VIEW_ATTRIBUTES, SERVICE_DONE_EXECUTING_ANON) != 0 ? true : DEBUG_SERVICE;
        if ((data.appInfo.flags & KeyEvent.KEYCODE_MEDIA_EJECT) != 0) {
            StrictMode.conditionallyEnableDebugLogging();
        }
        if (data.appInfo.targetSdkVersion > 9) {
            StrictMode.enableDeathOnNetwork();
        }
        if (data.debugMode != 0) {
            Debug.changeDebugPort(8100);
            if (data.debugMode == SERVICE_DONE_EXECUTING_STOP) {
                Slog.w(TAG, "Application " + data.info.getPackageName() + " is waiting for the debugger on port 8100...");
                IActivityManager mgr = ActivityManagerNative.getDefault();
                try {
                    mgr.showWaitingForDebugger(this.mAppThread, true);
                } catch (RemoteException e) {
                }
                Debug.waitForDebugger();
                try {
                    mgr.showWaitingForDebugger(this.mAppThread, DEBUG_SERVICE);
                } catch (RemoteException e2) {
                }
            } else {
                Slog.w(TAG, "Application " + data.info.getPackageName() + " can be debugged on port 8100...");
            }
        }
        if (data.enableOpenGlTrace) {
            GLUtils.setTracingLevel(SERVICE_DONE_EXECUTING_START);
        }
        Trace.setAppTracingAllowed((data.appInfo.flags & SERVICE_DONE_EXECUTING_STOP) != 0 ? true : DEBUG_SERVICE);
        IBinder b = ServiceManager.getService(Context.CONNECTIVITY_SERVICE);
        if (b != null) {
            try {
                Proxy.setHttpProxySystemProperty(IConnectivityManager.Stub.asInterface(b).getDefaultProxy());
            } catch (RemoteException e3) {
            }
        }
        if (data.instrumentationName != null) {
            InstrumentationInfo ii = null;
            try {
                ii = appContext.getPackageManager().getInstrumentationInfo(data.instrumentationName, SERVICE_DONE_EXECUTING_ANON);
            } catch (NameNotFoundException e4) {
            }
            if (ii == null) {
                throw new RuntimeException("Unable to find instrumentation info for: " + data.instrumentationName);
            }
            this.mInstrumentationPackageName = ii.packageName;
            this.mInstrumentationAppDir = ii.sourceDir;
            this.mInstrumentationSplitAppDirs = ii.splitSourceDirs;
            this.mInstrumentationLibDir = ii.nativeLibraryDir;
            this.mInstrumentedAppDir = data.info.getAppDir();
            this.mInstrumentedSplitAppDirs = data.info.getSplitAppDirs();
            this.mInstrumentedLibDir = data.info.getLibDir();
            ApplicationInfo instrApp = new ApplicationInfo();
            instrApp.packageName = ii.packageName;
            instrApp.sourceDir = ii.sourceDir;
            instrApp.publicSourceDir = ii.publicSourceDir;
            instrApp.splitSourceDirs = ii.splitSourceDirs;
            instrApp.splitPublicSourceDirs = ii.splitPublicSourceDirs;
            instrApp.dataDir = ii.dataDir;
            instrApp.nativeLibraryDir = ii.nativeLibraryDir;
            ContextImpl instrContext = ContextImpl.createAppContext(this, getPackageInfo(instrApp, data.compatInfo, appContext.getClassLoader(), DEBUG_SERVICE, true, DEBUG_SERVICE));
            try {
                this.mInstrumentation = (Instrumentation) instrContext.getClassLoader().loadClass(data.instrumentationName.getClassName()).newInstance();
                this.mInstrumentation.init(this, instrContext, appContext, new ComponentName(ii.packageName, ii.name), data.instrumentationWatcher, data.instrumentationUiAutomationConnection);
                if (!(this.mProfiler.profileFile == null || ii.handleProfiling || this.mProfiler.profileFd != null)) {
                    this.mProfiler.handlingProfiling = true;
                    File file = new File(this.mProfiler.profileFile);
                    file.getParentFile().mkdirs();
                    Debug.startMethodTracing(file.toString(), LayoutParams.FLAG_SPLIT_TOUCH);
                }
            } catch (Throwable e5) {
                throw new RuntimeException("Unable to instantiate instrumentation " + data.instrumentationName + ": " + e5.toString(), e5);
            }
        }
        this.mInstrumentation = new Instrumentation();
        if ((data.appInfo.flags & AccessibilityNodeInfo.ACTION_DISMISS) != 0) {
            VMRuntime.getRuntime().clearGrowthLimit();
        }
        ThreadPolicy savedPolicy = StrictMode.allowThreadDiskWrites();
        Application app;
        try {
            app = data.info.makeApplication(data.restrictedBackupMode, null);
            this.mInitialApplication = app;
            if (!data.restrictedBackupMode) {
                List<ProviderInfo> providers = data.providers;
                if (providers != null) {
                    installContentProviders(app, providers);
                    this.mH.sendEmptyMessageDelayed(KeyEvent.KEYCODE_F2, 10000);
                }
            }
            this.mInstrumentation.onCreate(data.instrumentationArgs);
            this.mInstrumentation.callApplicationOnCreate(app);
        } catch (Throwable e52) {
            if (!this.mInstrumentation.onException(app, e52)) {
                throw new RuntimeException("Unable to create application " + app.getClass().getName() + ": " + e52.toString(), e52);
            }
        } catch (Throwable e522) {
            throw new RuntimeException("Exception thrown in onCreate() of " + data.instrumentationName + ": " + e522.toString(), e522);
        } catch (Throwable th) {
            StrictMode.setThreadPolicy(savedPolicy);
        }
        StrictMode.setThreadPolicy(savedPolicy);
    }

    final void finishInstrumentation(int resultCode, Bundle results) {
        IActivityManager am = ActivityManagerNative.getDefault();
        if (this.mProfiler.profileFile != null && this.mProfiler.handlingProfiling && this.mProfiler.profileFd == null) {
            Debug.stopMethodTracing();
        }
        try {
            am.finishInstrumentation(this.mAppThread, resultCode, results);
        } catch (RemoteException e) {
        }
    }

    private void installContentProviders(Context context, List<ProviderInfo> providers) {
        ArrayList<ContentProviderHolder> results = new ArrayList();
        for (ProviderInfo cpi : providers) {
            ContentProviderHolder cph = installProvider(context, null, cpi, DEBUG_SERVICE, true, true);
            if (cph != null) {
                cph.noReleaseNeeded = true;
                results.add(cph);
            }
        }
        try {
            ActivityManagerNative.getDefault().publishContentProviders(getApplicationThread(), results);
        } catch (RemoteException e) {
        }
    }

    public final IContentProvider acquireProvider(Context c, String auth, int userId, boolean stable) {
        IContentProvider provider = acquireExistingProvider(c, auth, userId, stable);
        if (provider != null) {
            return provider;
        }
        ContentProviderHolder holder = null;
        try {
            holder = ActivityManagerNative.getDefault().getContentProvider(getApplicationThread(), auth, userId, stable);
        } catch (RemoteException e) {
        }
        if (holder == null) {
            Slog.e(TAG, "Failed to find provider info for " + auth);
            return null;
        }
        return installProvider(c, holder, holder.info, true, holder.noReleaseNeeded, stable).provider;
    }

    private final void incProviderRefLocked(ProviderRefCount prc, boolean stable) {
        if (stable) {
            prc.stableCount += SERVICE_DONE_EXECUTING_START;
            if (prc.stableCount == SERVICE_DONE_EXECUTING_START) {
                int unstableDelta;
                if (prc.removePending) {
                    unstableDelta = -1;
                    prc.removePending = DEBUG_SERVICE;
                    this.mH.removeMessages(KeyEvent.KEYCODE_F1, prc);
                } else {
                    unstableDelta = SERVICE_DONE_EXECUTING_ANON;
                }
                try {
                    ActivityManagerNative.getDefault().refContentProvider(prc.holder.connection, SERVICE_DONE_EXECUTING_START, unstableDelta);
                    return;
                } catch (RemoteException e) {
                    return;
                }
            }
            return;
        }
        prc.unstableCount += SERVICE_DONE_EXECUTING_START;
        if (prc.unstableCount != SERVICE_DONE_EXECUTING_START) {
            return;
        }
        if (prc.removePending) {
            prc.removePending = DEBUG_SERVICE;
            this.mH.removeMessages(KeyEvent.KEYCODE_F1, prc);
            return;
        }
        try {
            ActivityManagerNative.getDefault().refContentProvider(prc.holder.connection, SERVICE_DONE_EXECUTING_ANON, SERVICE_DONE_EXECUTING_START);
        } catch (RemoteException e2) {
        }
    }

    public final IContentProvider acquireExistingProvider(Context c, String auth, int userId, boolean stable) {
        synchronized (this.mProviderMap) {
            ProviderClientRecord pr = (ProviderClientRecord) this.mProviderMap.get(new ProviderKey(auth, userId));
            if (pr == null) {
                return null;
            }
            IContentProvider provider = pr.mProvider;
            IBinder jBinder = provider.asBinder();
            if (jBinder.isBinderAlive()) {
                ProviderRefCount prc = (ProviderRefCount) this.mProviderRefCountMap.get(jBinder);
                if (prc != null) {
                    incProviderRefLocked(prc, stable);
                }
                return provider;
            }
            Log.i(TAG, "Acquiring provider " + auth + " for user " + userId + ": existing object's process dead");
            handleUnstableProviderDiedLocked(jBinder, true);
            return null;
        }
    }

    public final boolean releaseProvider(IContentProvider provider, boolean stable) {
        int i = SERVICE_DONE_EXECUTING_ANON;
        if (provider == null) {
            return DEBUG_SERVICE;
        }
        IBinder jBinder = provider.asBinder();
        synchronized (this.mProviderMap) {
            ProviderRefCount prc = (ProviderRefCount) this.mProviderRefCountMap.get(jBinder);
            if (prc == null) {
                return DEBUG_SERVICE;
            }
            boolean lastRef = DEBUG_SERVICE;
            if (stable) {
                if (prc.stableCount == 0) {
                    return DEBUG_SERVICE;
                }
                prc.stableCount--;
                if (prc.stableCount == 0) {
                    lastRef = prc.unstableCount == 0 ? true : DEBUG_SERVICE;
                    try {
                        IActivityManager iActivityManager = ActivityManagerNative.getDefault();
                        IBinder iBinder = prc.holder.connection;
                        if (lastRef) {
                            i = SERVICE_DONE_EXECUTING_START;
                        }
                        iActivityManager.refContentProvider(iBinder, -1, i);
                    } catch (RemoteException e) {
                    }
                }
            } else if (prc.unstableCount == 0) {
                return DEBUG_SERVICE;
            } else {
                prc.unstableCount--;
                if (prc.unstableCount == 0) {
                    lastRef = prc.stableCount == 0 ? true : DEBUG_SERVICE;
                    if (!lastRef) {
                        try {
                            ActivityManagerNative.getDefault().refContentProvider(prc.holder.connection, SERVICE_DONE_EXECUTING_ANON, -1);
                        } catch (RemoteException e2) {
                        }
                    }
                }
            }
            if (lastRef) {
                if (prc.removePending) {
                    Slog.w(TAG, "Duplicate remove pending of provider " + prc.holder.info.name);
                } else {
                    prc.removePending = true;
                    this.mH.sendMessage(this.mH.obtainMessage(KeyEvent.KEYCODE_F1, prc));
                }
            }
            return true;
        }
    }

    final void completeRemoveProvider(ProviderRefCount prc) {
        synchronized (this.mProviderMap) {
            if (prc.removePending) {
                prc.removePending = DEBUG_SERVICE;
                IBinder jBinder = prc.holder.provider.asBinder();
                if (((ProviderRefCount) this.mProviderRefCountMap.get(jBinder)) == prc) {
                    this.mProviderRefCountMap.remove(jBinder);
                }
                for (int i = this.mProviderMap.size() - 1; i >= 0; i--) {
                    if (((ProviderClientRecord) this.mProviderMap.valueAt(i)).mProvider.asBinder() == jBinder) {
                        this.mProviderMap.removeAt(i);
                    }
                }
                try {
                    ActivityManagerNative.getDefault().removeContentProvider(prc.holder.connection, DEBUG_SERVICE);
                    return;
                } catch (RemoteException e) {
                    return;
                }
            }
        }
    }

    final void handleUnstableProviderDied(IBinder provider, boolean fromClient) {
        synchronized (this.mProviderMap) {
            handleUnstableProviderDiedLocked(provider, fromClient);
        }
    }

    final void handleUnstableProviderDiedLocked(IBinder provider, boolean fromClient) {
        ProviderRefCount prc = (ProviderRefCount) this.mProviderRefCountMap.get(provider);
        if (prc != null) {
            this.mProviderRefCountMap.remove(provider);
            for (int i = this.mProviderMap.size() - 1; i >= 0; i--) {
                ProviderClientRecord pr = (ProviderClientRecord) this.mProviderMap.valueAt(i);
                if (pr != null && pr.mProvider.asBinder() == provider) {
                    Slog.i(TAG, "Removing dead content provider:" + pr.mProvider.toString());
                    this.mProviderMap.removeAt(i);
                }
            }
            if (fromClient) {
                try {
                    ActivityManagerNative.getDefault().unstableProviderDied(prc.holder.connection);
                } catch (RemoteException e) {
                }
            }
        }
    }

    final void appNotRespondingViaProvider(IBinder provider) {
        synchronized (this.mProviderMap) {
            ProviderRefCount prc = (ProviderRefCount) this.mProviderRefCountMap.get(provider);
            if (prc != null) {
                try {
                    ActivityManagerNative.getDefault().appNotRespondingViaProvider(prc.holder.connection);
                } catch (RemoteException e) {
                }
            }
        }
    }

    private ProviderClientRecord installProviderAuthoritiesLocked(IContentProvider provider, ContentProvider localProvider, ContentProviderHolder holder) {
        String[] auths = PATTERN_SEMICOLON.split(holder.info.authority);
        int userId = UserHandle.getUserId(holder.info.applicationInfo.uid);
        ProviderClientRecord pcr = new ProviderClientRecord(auths, provider, localProvider, holder);
        String[] arr$ = auths;
        int len$ = arr$.length;
        for (int i$ = SERVICE_DONE_EXECUTING_ANON; i$ < len$; i$ += SERVICE_DONE_EXECUTING_START) {
            String auth = arr$[i$];
            ProviderKey key = new ProviderKey(auth, userId);
            if (((ProviderClientRecord) this.mProviderMap.get(key)) != null) {
                Slog.w(TAG, "Content provider " + pcr.mHolder.info.name + " already published as " + auth);
            } else {
                this.mProviderMap.put(key, pcr);
            }
        }
        return pcr;
    }

    private ContentProviderHolder installProvider(Context context, ContentProviderHolder holder, ProviderInfo info, boolean noisy, boolean noReleaseNeeded, boolean stable) {
        IContentProvider provider;
        Throwable th;
        ContentProvider localProvider = null;
        if (holder == null || holder.provider == null) {
            if (noisy) {
                Slog.d(TAG, "Loading provider " + info.authority + ": " + info.name);
            }
            Context c = null;
            ApplicationInfo ai = info.applicationInfo;
            if (context.getPackageName().equals(ai.packageName)) {
                c = context;
            } else {
                if (this.mInitialApplication != null) {
                    if (this.mInitialApplication.getPackageName().equals(ai.packageName)) {
                        c = this.mInitialApplication;
                    }
                }
                try {
                    c = context.createPackageContext(ai.packageName, SERVICE_DONE_EXECUTING_START);
                } catch (NameNotFoundException e) {
                }
            }
            if (c == null) {
                Slog.w(TAG, "Unable to get context for package " + ai.packageName + " while loading content provider " + info.name);
                return null;
            }
            try {
                localProvider = (ContentProvider) c.getClassLoader().loadClass(info.name).newInstance();
                provider = localProvider.getIContentProvider();
                if (provider == null) {
                    Slog.e(TAG, "Failed to instantiate class " + info.name + " from sourceDir " + info.applicationInfo.sourceDir);
                    return null;
                }
                localProvider.attachInfo(c, info);
            } catch (Exception e2) {
                if (this.mInstrumentation.onException(null, e2)) {
                    return null;
                }
                throw new RuntimeException("Unable to get provider " + info.name + ": " + e2.toString(), e2);
            }
        }
        provider = holder.provider;
        synchronized (this.mProviderMap) {
            try {
                ContentProviderHolder retHolder;
                IBinder jBinder = provider.asBinder();
                if (localProvider != null) {
                    ComponentName cname = new ComponentName(info.packageName, info.name);
                    ProviderClientRecord pr = (ProviderClientRecord) this.mLocalProvidersByName.get(cname);
                    if (pr != null) {
                        provider = pr.mProvider;
                    } else {
                        ContentProviderHolder holder2 = new ContentProviderHolder(info);
                        try {
                            holder2.provider = provider;
                            holder2.noReleaseNeeded = true;
                            pr = installProviderAuthoritiesLocked(provider, localProvider, holder2);
                            this.mLocalProviders.put(jBinder, pr);
                            this.mLocalProvidersByName.put(cname, pr);
                            holder = holder2;
                        } catch (Throwable th2) {
                            th = th2;
                            holder = holder2;
                            throw th;
                        }
                    }
                    retHolder = pr.mHolder;
                } else {
                    ProviderRefCount prc = (ProviderRefCount) this.mProviderRefCountMap.get(jBinder);
                    if (prc == null) {
                        ProviderClientRecord client = installProviderAuthoritiesLocked(provider, localProvider, holder);
                        if (noReleaseNeeded) {
                            prc = new ProviderRefCount(holder, client, LayoutParams.TYPE_APPLICATION_PANEL, LayoutParams.TYPE_APPLICATION_PANEL);
                        } else {
                            prc = stable ? new ProviderRefCount(holder, client, SERVICE_DONE_EXECUTING_START, SERVICE_DONE_EXECUTING_ANON) : new ProviderRefCount(holder, client, SERVICE_DONE_EXECUTING_ANON, SERVICE_DONE_EXECUTING_START);
                        }
                        this.mProviderRefCountMap.put(jBinder, prc);
                    } else if (!noReleaseNeeded) {
                        incProviderRefLocked(prc, stable);
                        try {
                            ActivityManagerNative.getDefault().removeContentProvider(holder.connection, stable);
                        } catch (RemoteException e3) {
                        }
                    }
                    retHolder = prc.holder;
                }
                return retHolder;
            } catch (Throwable th3) {
                th = th3;
                throw th;
            }
        }
    }

    private void attach(boolean system) {
        sCurrentActivityThread = this;
        this.mSystemThread = system;
        if (system) {
            DdmHandleAppName.setAppName("system_process", UserHandle.myUserId());
            try {
                this.mInstrumentation = new Instrumentation();
                this.mInitialApplication = ContextImpl.createAppContext(this, getSystemContext().mPackageInfo).mPackageInfo.makeApplication(true, null);
                this.mInitialApplication.onCreate();
            } catch (Exception e) {
                throw new RuntimeException("Unable to instantiate Application():" + e.toString(), e);
            }
        }
        ViewRootImpl.addFirstDrawHandler(new Runnable() {
            public void run() {
                ActivityThread.this.ensureJitEnabled();
            }
        });
        DdmHandleAppName.setAppName("<pre-initialized>", UserHandle.myUserId());
        RuntimeInit.setApplicationObject(this.mAppThread.asBinder());
        IActivityManager mgr = ActivityManagerNative.getDefault();
        try {
            mgr.attachApplication(this.mAppThread);
        } catch (RemoteException e2) {
        }
        BinderInternal.addGcWatcher(new AnonymousClass2(mgr));
        DropBox.setReporter(new DropBoxReporter());
        ViewRootImpl.addConfigCallback(new ComponentCallbacks2() {
            public void onConfigurationChanged(Configuration newConfig) {
                synchronized (ActivityThread.this.mResourcesManager) {
                    if (ActivityThread.this.mResourcesManager.applyConfigurationToResourcesLocked(newConfig, null) && (ActivityThread.this.mPendingConfiguration == null || ActivityThread.this.mPendingConfiguration.isOtherSeqNewer(newConfig))) {
                        ActivityThread.this.mPendingConfiguration = newConfig;
                        ActivityThread.this.sendMessage(KeyEvent.KEYCODE_META_RIGHT, newConfig);
                    }
                }
            }

            public void onLowMemory() {
            }

            public void onTrimMemory(int level) {
            }
        });
    }

    public static ActivityThread systemMain() {
        if (ActivityManager.isHighEndGfx()) {
            HardwareRenderer.enableForegroundTrimming();
        } else {
            HardwareRenderer.disable(true);
        }
        ActivityThread thread = new ActivityThread();
        thread.attach(true);
        return thread;
    }

    public final void installSystemProviders(List<ProviderInfo> providers) {
        if (providers != null) {
            installContentProviders(this.mInitialApplication, providers);
        }
    }

    public int getIntCoreSetting(String key, int defaultValue) {
        synchronized (this.mResourcesManager) {
            if (this.mCoreSettings != null) {
                defaultValue = this.mCoreSettings.getInt(key, defaultValue);
            }
        }
        return defaultValue;
    }

    public static void main(String[] args) {
        SamplingProfilerIntegration.start();
        CloseGuard.setEnabled(DEBUG_SERVICE);
        Environment.initForCurrentUser();
        EventLogger.setReporter(new EventLoggingReporter());
        Security.addProvider(new AndroidKeyStoreProvider());
        TrustedCertificateStore.setDefaultUserDirectory(Environment.getUserConfigDirectory(UserHandle.myUserId()));
        Process.setArgV0("<pre-initialized>");
        Looper.prepareMainLooper();
        ActivityThread thread = new ActivityThread();
        thread.attach(DEBUG_SERVICE);
        if (sMainThreadHandler == null) {
            sMainThreadHandler = thread.getHandler();
        }
        Looper.loop();
        throw new RuntimeException("Main thread loop unexpectedly exited");
    }
}
