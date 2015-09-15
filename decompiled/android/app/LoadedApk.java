package android.app;

import android.content.BroadcastReceiver;
import android.content.BroadcastReceiver.PendingResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.IIntentReceiver.Stub;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.CompatibilityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Process;
import android.os.RemoteException;
import android.os.Trace;
import android.os.UserHandle;
import android.service.notification.ZenModeConfig;
import android.util.ArrayMap;
import android.util.Slog;
import android.util.SparseArray;
import android.view.DisplayAdjustments;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import dalvik.system.VMRuntime;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Enumeration;

public final class LoadedApk {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final String TAG = "LoadedApk";
    private final ActivityThread mActivityThread;
    private final String mAppDir;
    private Application mApplication;
    private ApplicationInfo mApplicationInfo;
    private final ClassLoader mBaseClassLoader;
    private ClassLoader mClassLoader;
    int mClientCount;
    private final String mDataDir;
    private final File mDataDirFile;
    private final DisplayAdjustments mDisplayAdjustments;
    private final boolean mIncludeCode;
    private final String mLibDir;
    private final String[] mOverlayDirs;
    final String mPackageName;
    private final ArrayMap<Context, ArrayMap<BroadcastReceiver, ReceiverDispatcher>> mReceivers;
    private final boolean mRegisterPackage;
    private final String mResDir;
    Resources mResources;
    private final boolean mSecurityViolation;
    private final ArrayMap<Context, ArrayMap<ServiceConnection, ServiceDispatcher>> mServices;
    private final String[] mSharedLibraries;
    private final String[] mSplitAppDirs;
    private final String[] mSplitResDirs;
    private final ArrayMap<Context, ArrayMap<ServiceConnection, ServiceDispatcher>> mUnboundServices;
    private final ArrayMap<Context, ArrayMap<BroadcastReceiver, ReceiverDispatcher>> mUnregisteredReceivers;

    static final class ReceiverDispatcher {
        final Handler mActivityThread;
        final Context mContext;
        boolean mForgotten;
        final Stub mIIntentReceiver;
        final Instrumentation mInstrumentation;
        final IntentReceiverLeaked mLocation;
        final BroadcastReceiver mReceiver;
        final boolean mRegistered;
        RuntimeException mUnregisterLocation;

        final class Args extends PendingResult implements Runnable {
            private Intent mCurIntent;
            private final boolean mOrdered;

            public Args(Intent intent, int resultCode, String resultData, Bundle resultExtras, boolean ordered, boolean sticky, int sendingUser) {
                super(resultCode, resultData, resultExtras, ReceiverDispatcher.this.mRegistered ? 1 : 2, ordered, sticky, ReceiverDispatcher.this.mIIntentReceiver.asBinder(), sendingUser);
                this.mCurIntent = intent;
                this.mOrdered = ordered;
            }

            public void run() {
                BroadcastReceiver receiver = ReceiverDispatcher.this.mReceiver;
                boolean ordered = this.mOrdered;
                IActivityManager mgr = ActivityManagerNative.getDefault();
                Intent intent = this.mCurIntent;
                this.mCurIntent = null;
                if (receiver != null && !ReceiverDispatcher.this.mForgotten) {
                    Trace.traceBegin(64, "broadcastReceiveReg");
                    try {
                        ClassLoader cl = ReceiverDispatcher.this.mReceiver.getClass().getClassLoader();
                        intent.setExtrasClassLoader(cl);
                        setExtrasClassLoader(cl);
                        receiver.setPendingResult(this);
                        receiver.onReceive(ReceiverDispatcher.this.mContext, intent);
                    } catch (Exception e) {
                        if (ReceiverDispatcher.this.mRegistered && ordered) {
                            sendFinished(mgr);
                        }
                        if (ReceiverDispatcher.this.mInstrumentation == null || !ReceiverDispatcher.this.mInstrumentation.onException(ReceiverDispatcher.this.mReceiver, e)) {
                            Trace.traceEnd(64);
                            throw new RuntimeException("Error receiving broadcast " + intent + " in " + ReceiverDispatcher.this.mReceiver, e);
                        }
                    }
                    if (receiver.getPendingResult() != null) {
                        finish();
                    }
                    Trace.traceEnd(64);
                } else if (ReceiverDispatcher.this.mRegistered && ordered) {
                    sendFinished(mgr);
                }
            }
        }

        static final class InnerReceiver extends Stub {
            final WeakReference<ReceiverDispatcher> mDispatcher;
            final ReceiverDispatcher mStrongRef;

            InnerReceiver(ReceiverDispatcher rd, boolean strong) {
                this.mDispatcher = new WeakReference(rd);
                if (!strong) {
                    rd = null;
                }
                this.mStrongRef = rd;
            }

            public void performReceive(Intent intent, int resultCode, String data, Bundle extras, boolean ordered, boolean sticky, int sendingUser) {
                ReceiverDispatcher rd = (ReceiverDispatcher) this.mDispatcher.get();
                if (rd != null) {
                    rd.performReceive(intent, resultCode, data, extras, ordered, sticky, sendingUser);
                    return;
                }
                IActivityManager mgr = ActivityManagerNative.getDefault();
                if (extras != null) {
                    try {
                        extras.setAllowFds(LoadedApk.$assertionsDisabled);
                    } catch (RemoteException e) {
                        Slog.w(ActivityThread.TAG, "Couldn't finish broadcast to unregistered receiver");
                        return;
                    }
                }
                mgr.finishReceiver(this, resultCode, data, extras, LoadedApk.$assertionsDisabled);
            }
        }

        ReceiverDispatcher(BroadcastReceiver receiver, Context context, Handler activityThread, Instrumentation instrumentation, boolean registered) {
            if (activityThread == null) {
                throw new NullPointerException("Handler must not be null");
            }
            this.mIIntentReceiver = new InnerReceiver(this, !registered ? true : LoadedApk.$assertionsDisabled);
            this.mReceiver = receiver;
            this.mContext = context;
            this.mActivityThread = activityThread;
            this.mInstrumentation = instrumentation;
            this.mRegistered = registered;
            this.mLocation = new IntentReceiverLeaked(null);
            this.mLocation.fillInStackTrace();
        }

        void validate(Context context, Handler activityThread) {
            if (this.mContext != context) {
                throw new IllegalStateException("Receiver " + this.mReceiver + " registered with differing Context (was " + this.mContext + " now " + context + ")");
            } else if (this.mActivityThread != activityThread) {
                throw new IllegalStateException("Receiver " + this.mReceiver + " registered with differing handler (was " + this.mActivityThread + " now " + activityThread + ")");
            }
        }

        IntentReceiverLeaked getLocation() {
            return this.mLocation;
        }

        BroadcastReceiver getIntentReceiver() {
            return this.mReceiver;
        }

        IIntentReceiver getIIntentReceiver() {
            return this.mIIntentReceiver;
        }

        void setUnregisterLocation(RuntimeException ex) {
            this.mUnregisterLocation = ex;
        }

        RuntimeException getUnregisterLocation() {
            return this.mUnregisterLocation;
        }

        public void performReceive(Intent intent, int resultCode, String data, Bundle extras, boolean ordered, boolean sticky, int sendingUser) {
            Args args = new Args(intent, resultCode, data, extras, ordered, sticky, sendingUser);
            if (!this.mActivityThread.post(args) && this.mRegistered && ordered) {
                args.sendFinished(ActivityManagerNative.getDefault());
            }
        }
    }

    static final class ServiceDispatcher {
        private final ArrayMap<ComponentName, ConnectionInfo> mActiveConnections;
        private final Handler mActivityThread;
        private final ServiceConnection mConnection;
        private final Context mContext;
        private boolean mDied;
        private final int mFlags;
        private boolean mForgotten;
        private final InnerConnection mIServiceConnection;
        private final ServiceConnectionLeaked mLocation;
        private RuntimeException mUnbindLocation;

        private static class ConnectionInfo {
            IBinder binder;
            DeathRecipient deathMonitor;

            private ConnectionInfo() {
            }
        }

        private final class DeathMonitor implements DeathRecipient {
            final ComponentName mName;
            final IBinder mService;

            DeathMonitor(ComponentName name, IBinder service) {
                this.mName = name;
                this.mService = service;
            }

            public void binderDied() {
                ServiceDispatcher.this.death(this.mName, this.mService);
            }
        }

        private static class InnerConnection extends IServiceConnection.Stub {
            final WeakReference<ServiceDispatcher> mDispatcher;

            InnerConnection(ServiceDispatcher sd) {
                this.mDispatcher = new WeakReference(sd);
            }

            public void connected(ComponentName name, IBinder service) throws RemoteException {
                ServiceDispatcher sd = (ServiceDispatcher) this.mDispatcher.get();
                if (sd != null) {
                    sd.connected(name, service);
                }
            }
        }

        private final class RunConnection implements Runnable {
            final int mCommand;
            final ComponentName mName;
            final IBinder mService;

            RunConnection(ComponentName name, IBinder service, int command) {
                this.mName = name;
                this.mService = service;
                this.mCommand = command;
            }

            public void run() {
                if (this.mCommand == 0) {
                    ServiceDispatcher.this.doConnected(this.mName, this.mService);
                } else if (this.mCommand == 1) {
                    ServiceDispatcher.this.doDeath(this.mName, this.mService);
                }
            }
        }

        ServiceDispatcher(ServiceConnection conn, Context context, Handler activityThread, int flags) {
            this.mActiveConnections = new ArrayMap();
            this.mIServiceConnection = new InnerConnection(this);
            this.mConnection = conn;
            this.mContext = context;
            this.mActivityThread = activityThread;
            this.mLocation = new ServiceConnectionLeaked(null);
            this.mLocation.fillInStackTrace();
            this.mFlags = flags;
        }

        void validate(Context context, Handler activityThread) {
            if (this.mContext != context) {
                throw new RuntimeException("ServiceConnection " + this.mConnection + " registered with differing Context (was " + this.mContext + " now " + context + ")");
            } else if (this.mActivityThread != activityThread) {
                throw new RuntimeException("ServiceConnection " + this.mConnection + " registered with differing handler (was " + this.mActivityThread + " now " + activityThread + ")");
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void doForget() {
            /*
            r5 = this;
            monitor-enter(r5);
            r1 = 0;
        L_0x0002:
            r2 = r5.mActiveConnections;	 Catch:{ all -> 0x0027 }
            r2 = r2.size();	 Catch:{ all -> 0x0027 }
            if (r1 >= r2) goto L_0x001d;
        L_0x000a:
            r2 = r5.mActiveConnections;	 Catch:{ all -> 0x0027 }
            r0 = r2.valueAt(r1);	 Catch:{ all -> 0x0027 }
            r0 = (android.app.LoadedApk.ServiceDispatcher.ConnectionInfo) r0;	 Catch:{ all -> 0x0027 }
            r2 = r0.binder;	 Catch:{ all -> 0x0027 }
            r3 = r0.deathMonitor;	 Catch:{ all -> 0x0027 }
            r4 = 0;
            r2.unlinkToDeath(r3, r4);	 Catch:{ all -> 0x0027 }
            r1 = r1 + 1;
            goto L_0x0002;
        L_0x001d:
            r2 = r5.mActiveConnections;	 Catch:{ all -> 0x0027 }
            r2.clear();	 Catch:{ all -> 0x0027 }
            r2 = 1;
            r5.mForgotten = r2;	 Catch:{ all -> 0x0027 }
            monitor-exit(r5);	 Catch:{ all -> 0x0027 }
            return;
        L_0x0027:
            r2 = move-exception;
            monitor-exit(r5);	 Catch:{ all -> 0x0027 }
            throw r2;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.app.LoadedApk.ServiceDispatcher.doForget():void");
        }

        ServiceConnectionLeaked getLocation() {
            return this.mLocation;
        }

        ServiceConnection getServiceConnection() {
            return this.mConnection;
        }

        IServiceConnection getIServiceConnection() {
            return this.mIServiceConnection;
        }

        int getFlags() {
            return this.mFlags;
        }

        void setUnbindLocation(RuntimeException ex) {
            this.mUnbindLocation = ex;
        }

        RuntimeException getUnbindLocation() {
            return this.mUnbindLocation;
        }

        public void connected(ComponentName name, IBinder service) {
            if (this.mActivityThread != null) {
                this.mActivityThread.post(new RunConnection(name, service, 0));
            } else {
                doConnected(name, service);
            }
        }

        public void death(ComponentName name, IBinder service) {
            synchronized (this) {
                this.mDied = true;
                ConnectionInfo old = (ConnectionInfo) this.mActiveConnections.remove(name);
                if (old == null || old.binder != service) {
                    return;
                }
                old.binder.unlinkToDeath(old.deathMonitor, 0);
                if (this.mActivityThread != null) {
                    this.mActivityThread.post(new RunConnection(name, service, 1));
                } else {
                    doDeath(name, service);
                }
            }
        }

        public void doConnected(ComponentName name, IBinder service) {
            synchronized (this) {
                if (this.mForgotten) {
                    return;
                }
                ConnectionInfo old = (ConnectionInfo) this.mActiveConnections.get(name);
                if (old == null || old.binder != service) {
                    if (service != null) {
                        this.mDied = LoadedApk.$assertionsDisabled;
                        ConnectionInfo info = new ConnectionInfo();
                        info.binder = service;
                        info.deathMonitor = new DeathMonitor(name, service);
                        try {
                            service.linkToDeath(info.deathMonitor, 0);
                            this.mActiveConnections.put(name, info);
                        } catch (RemoteException e) {
                            this.mActiveConnections.remove(name);
                            return;
                        }
                    }
                    this.mActiveConnections.remove(name);
                    if (old != null) {
                        old.binder.unlinkToDeath(old.deathMonitor, 0);
                    }
                    if (old != null) {
                        this.mConnection.onServiceDisconnected(name);
                    }
                    if (service != null) {
                        this.mConnection.onServiceConnected(name, service);
                        return;
                    }
                    return;
                }
            }
        }

        public void doDeath(ComponentName name, IBinder service) {
            this.mConnection.onServiceDisconnected(name);
        }
    }

    private static class WarningContextClassLoader extends ClassLoader {
        private static boolean warned;

        private WarningContextClassLoader() {
        }

        static {
            warned = LoadedApk.$assertionsDisabled;
        }

        private void warn(String methodName) {
            if (!warned) {
                warned = true;
                Thread.currentThread().setContextClassLoader(getParent());
                Slog.w(ActivityThread.TAG, "ClassLoader." + methodName + ": " + "The class loader returned by " + "Thread.getContextClassLoader() may fail for processes " + "that host multiple applications. You should explicitly " + "specify a context class loader. For example: " + "Thread.setContextClassLoader(getClass().getClassLoader());");
            }
        }

        public URL getResource(String resName) {
            warn("getResource");
            return getParent().getResource(resName);
        }

        public Enumeration<URL> getResources(String resName) throws IOException {
            warn("getResources");
            return getParent().getResources(resName);
        }

        public InputStream getResourceAsStream(String resName) {
            warn("getResourceAsStream");
            return getParent().getResourceAsStream(resName);
        }

        public Class<?> loadClass(String className) throws ClassNotFoundException {
            warn("loadClass");
            return getParent().loadClass(className);
        }

        public void setClassAssertionStatus(String cname, boolean enable) {
            warn("setClassAssertionStatus");
            getParent().setClassAssertionStatus(cname, enable);
        }

        public void setPackageAssertionStatus(String pname, boolean enable) {
            warn("setPackageAssertionStatus");
            getParent().setPackageAssertionStatus(pname, enable);
        }

        public void setDefaultAssertionStatus(boolean enable) {
            warn("setDefaultAssertionStatus");
            getParent().setDefaultAssertionStatus(enable);
        }

        public void clearAssertionStatus() {
            warn("clearAssertionStatus");
            getParent().clearAssertionStatus();
        }
    }

    static {
        $assertionsDisabled = !LoadedApk.class.desiredAssertionStatus() ? true : $assertionsDisabled;
    }

    Application getApplication() {
        return this.mApplication;
    }

    public LoadedApk(ActivityThread activityThread, ApplicationInfo aInfo, CompatibilityInfo compatInfo, ClassLoader baseLoader, boolean securityViolation, boolean includeCode, boolean registerPackage) {
        this.mDisplayAdjustments = new DisplayAdjustments();
        this.mReceivers = new ArrayMap();
        this.mUnregisteredReceivers = new ArrayMap();
        this.mServices = new ArrayMap();
        this.mUnboundServices = new ArrayMap();
        this.mClientCount = 0;
        int myUid = Process.myUid();
        aInfo = adjustNativeLibraryPaths(aInfo);
        this.mActivityThread = activityThread;
        this.mApplicationInfo = aInfo;
        this.mPackageName = aInfo.packageName;
        this.mAppDir = aInfo.sourceDir;
        this.mResDir = aInfo.uid == myUid ? aInfo.sourceDir : aInfo.publicSourceDir;
        this.mSplitAppDirs = aInfo.splitSourceDirs;
        this.mSplitResDirs = aInfo.uid == myUid ? aInfo.splitSourceDirs : aInfo.splitPublicSourceDirs;
        this.mOverlayDirs = aInfo.resourceDirs;
        if (!(UserHandle.isSameUser(aInfo.uid, myUid) || Process.isIsolated())) {
            aInfo.dataDir = PackageManager.getDataDirForUser(UserHandle.getUserId(myUid), this.mPackageName);
        }
        this.mSharedLibraries = aInfo.sharedLibraryFiles;
        this.mDataDir = aInfo.dataDir;
        this.mDataDirFile = this.mDataDir != null ? new File(this.mDataDir) : null;
        this.mLibDir = aInfo.nativeLibraryDir;
        this.mBaseClassLoader = baseLoader;
        this.mSecurityViolation = securityViolation;
        this.mIncludeCode = includeCode;
        this.mRegisterPackage = registerPackage;
        this.mDisplayAdjustments.setCompatibilityInfo(compatInfo);
    }

    private static ApplicationInfo adjustNativeLibraryPaths(ApplicationInfo info) {
        if (info.primaryCpuAbi == null || info.secondaryCpuAbi == null || !VMRuntime.getRuntime().vmInstructionSet().equals(VMRuntime.getInstructionSet(info.secondaryCpuAbi))) {
            return info;
        }
        ApplicationInfo modified = new ApplicationInfo(info);
        modified.nativeLibraryDir = modified.secondaryNativeLibraryDir;
        return modified;
    }

    LoadedApk(ActivityThread activityThread) {
        this.mDisplayAdjustments = new DisplayAdjustments();
        this.mReceivers = new ArrayMap();
        this.mUnregisteredReceivers = new ArrayMap();
        this.mServices = new ArrayMap();
        this.mUnboundServices = new ArrayMap();
        this.mClientCount = 0;
        this.mActivityThread = activityThread;
        this.mApplicationInfo = new ApplicationInfo();
        this.mApplicationInfo.packageName = ZenModeConfig.SYSTEM_AUTHORITY;
        this.mPackageName = ZenModeConfig.SYSTEM_AUTHORITY;
        this.mAppDir = null;
        this.mResDir = null;
        this.mSplitAppDirs = null;
        this.mSplitResDirs = null;
        this.mOverlayDirs = null;
        this.mSharedLibraries = null;
        this.mDataDir = null;
        this.mDataDirFile = null;
        this.mLibDir = null;
        this.mBaseClassLoader = null;
        this.mSecurityViolation = $assertionsDisabled;
        this.mIncludeCode = true;
        this.mRegisterPackage = $assertionsDisabled;
        this.mClassLoader = ClassLoader.getSystemClassLoader();
        this.mResources = Resources.getSystem();
    }

    void installSystemApplicationInfo(ApplicationInfo info, ClassLoader classLoader) {
        if ($assertionsDisabled || info.packageName.equals(ZenModeConfig.SYSTEM_AUTHORITY)) {
            this.mApplicationInfo = info;
            this.mClassLoader = classLoader;
            return;
        }
        throw new AssertionError();
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public ApplicationInfo getApplicationInfo() {
        return this.mApplicationInfo;
    }

    public boolean isSecurityViolation() {
        return this.mSecurityViolation;
    }

    public CompatibilityInfo getCompatibilityInfo() {
        return this.mDisplayAdjustments.getCompatibilityInfo();
    }

    public void setCompatibilityInfo(CompatibilityInfo compatInfo) {
        this.mDisplayAdjustments.setCompatibilityInfo(compatInfo);
    }

    private static String[] getLibrariesFor(String packageName) {
        try {
            ApplicationInfo ai = ActivityThread.getPackageManager().getApplicationInfo(packageName, AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT, UserHandle.myUserId());
            if (ai == null) {
                return null;
            }
            return ai.sharedLibraryFiles;
        } catch (RemoteException e) {
            throw new AssertionError(e);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.ClassLoader getClassLoader() {
        /*
        r22 = this;
        monitor-enter(r22);
        r0 = r22;
        r0 = r0.mClassLoader;	 Catch:{ all -> 0x01ac }
        r20 = r0;
        if (r20 == 0) goto L_0x0011;
    L_0x0009:
        r0 = r22;
        r0 = r0.mClassLoader;	 Catch:{ all -> 0x01ac }
        r20 = r0;
        monitor-exit(r22);	 Catch:{ all -> 0x01ac }
    L_0x0010:
        return r20;
    L_0x0011:
        r0 = r22;
        r0 = r0.mIncludeCode;	 Catch:{ all -> 0x01ac }
        r20 = r0;
        if (r20 == 0) goto L_0x01af;
    L_0x0019:
        r0 = r22;
        r0 = r0.mPackageName;	 Catch:{ all -> 0x01ac }
        r20 = r0;
        r21 = "android";
        r20 = r20.equals(r21);	 Catch:{ all -> 0x01ac }
        if (r20 != 0) goto L_0x01af;
    L_0x0027:
        r0 = r22;
        r0 = r0.mPackageName;	 Catch:{ all -> 0x01ac }
        r20 = r0;
        r21 = android.app.ActivityThread.currentPackageName();	 Catch:{ all -> 0x01ac }
        r20 = java.util.Objects.equals(r20, r21);	 Catch:{ all -> 0x01ac }
        if (r20 != 0) goto L_0x0050;
    L_0x0037:
        r20 = dalvik.system.VMRuntime.getRuntime();	 Catch:{ all -> 0x01ac }
        r13 = r20.vmInstructionSet();	 Catch:{ all -> 0x01ac }
        r20 = android.app.ActivityThread.getPackageManager();	 Catch:{ RemoteException -> 0x01d2 }
        r0 = r22;
        r0 = r0.mPackageName;	 Catch:{ RemoteException -> 0x01d2 }
        r21 = r0;
        r0 = r20;
        r1 = r21;
        r0.performDexOptIfNeeded(r1, r13);	 Catch:{ RemoteException -> 0x01d2 }
    L_0x0050:
        r19 = new java.util.ArrayList;	 Catch:{ all -> 0x01ac }
        r19.<init>();	 Catch:{ all -> 0x01ac }
        r16 = new java.util.ArrayList;	 Catch:{ all -> 0x01ac }
        r16.<init>();	 Catch:{ all -> 0x01ac }
        r0 = r22;
        r0 = r0.mRegisterPackage;	 Catch:{ all -> 0x01ac }
        r20 = r0;
        if (r20 == 0) goto L_0x006f;
    L_0x0062:
        r20 = android.app.ActivityManagerNative.getDefault();	 Catch:{ RemoteException -> 0x01cf }
        r0 = r22;
        r0 = r0.mPackageName;	 Catch:{ RemoteException -> 0x01cf }
        r21 = r0;
        r20.addPackageDependency(r21);	 Catch:{ RemoteException -> 0x01cf }
    L_0x006f:
        r0 = r22;
        r0 = r0.mAppDir;	 Catch:{ all -> 0x01ac }
        r20 = r0;
        r19.add(r20);	 Catch:{ all -> 0x01ac }
        r0 = r22;
        r0 = r0.mSplitAppDirs;	 Catch:{ all -> 0x01ac }
        r20 = r0;
        if (r20 == 0) goto L_0x0089;
    L_0x0080:
        r0 = r22;
        r0 = r0.mSplitAppDirs;	 Catch:{ all -> 0x01ac }
        r20 = r0;
        java.util.Collections.addAll(r19, r20);	 Catch:{ all -> 0x01ac }
    L_0x0089:
        r0 = r22;
        r0 = r0.mLibDir;	 Catch:{ all -> 0x01ac }
        r20 = r0;
        r0 = r16;
        r1 = r20;
        r0.add(r1);	 Catch:{ all -> 0x01ac }
        r0 = r22;
        r0 = r0.mActivityThread;	 Catch:{ all -> 0x01ac }
        r20 = r0;
        r0 = r20;
        r8 = r0.mInstrumentationPackageName;	 Catch:{ all -> 0x01ac }
        r0 = r22;
        r0 = r0.mActivityThread;	 Catch:{ all -> 0x01ac }
        r20 = r0;
        r0 = r20;
        r5 = r0.mInstrumentationAppDir;	 Catch:{ all -> 0x01ac }
        r0 = r22;
        r0 = r0.mActivityThread;	 Catch:{ all -> 0x01ac }
        r20 = r0;
        r0 = r20;
        r9 = r0.mInstrumentationSplitAppDirs;	 Catch:{ all -> 0x01ac }
        r0 = r22;
        r0 = r0.mActivityThread;	 Catch:{ all -> 0x01ac }
        r20 = r0;
        r0 = r20;
        r6 = r0.mInstrumentationLibDir;	 Catch:{ all -> 0x01ac }
        r0 = r22;
        r0 = r0.mActivityThread;	 Catch:{ all -> 0x01ac }
        r20 = r0;
        r0 = r20;
        r10 = r0.mInstrumentedAppDir;	 Catch:{ all -> 0x01ac }
        r0 = r22;
        r0 = r0.mActivityThread;	 Catch:{ all -> 0x01ac }
        r20 = r0;
        r0 = r20;
        r12 = r0.mInstrumentedSplitAppDirs;	 Catch:{ all -> 0x01ac }
        r0 = r22;
        r0 = r0.mActivityThread;	 Catch:{ all -> 0x01ac }
        r20 = r0;
        r0 = r20;
        r11 = r0.mInstrumentedLibDir;	 Catch:{ all -> 0x01ac }
        r7 = 0;
        r0 = r22;
        r0 = r0.mAppDir;	 Catch:{ all -> 0x01ac }
        r20 = r0;
        r0 = r20;
        r20 = r0.equals(r5);	 Catch:{ all -> 0x01ac }
        if (r20 != 0) goto L_0x00f9;
    L_0x00eb:
        r0 = r22;
        r0 = r0.mAppDir;	 Catch:{ all -> 0x01ac }
        r20 = r0;
        r0 = r20;
        r20 = r0.equals(r10);	 Catch:{ all -> 0x01ac }
        if (r20 == 0) goto L_0x012b;
    L_0x00f9:
        r19.clear();	 Catch:{ all -> 0x01ac }
        r0 = r19;
        r0.add(r5);	 Catch:{ all -> 0x01ac }
        if (r9 == 0) goto L_0x0108;
    L_0x0103:
        r0 = r19;
        java.util.Collections.addAll(r0, r9);	 Catch:{ all -> 0x01ac }
    L_0x0108:
        r0 = r19;
        r0.add(r10);	 Catch:{ all -> 0x01ac }
        if (r12 == 0) goto L_0x0114;
    L_0x010f:
        r0 = r19;
        java.util.Collections.addAll(r0, r12);	 Catch:{ all -> 0x01ac }
    L_0x0114:
        r16.clear();	 Catch:{ all -> 0x01ac }
        r0 = r16;
        r0.add(r6);	 Catch:{ all -> 0x01ac }
        r0 = r16;
        r0.add(r11);	 Catch:{ all -> 0x01ac }
        r20 = r10.equals(r5);	 Catch:{ all -> 0x01ac }
        if (r20 != 0) goto L_0x012b;
    L_0x0127:
        r7 = getLibrariesFor(r8);	 Catch:{ all -> 0x01ac }
    L_0x012b:
        r0 = r22;
        r0 = r0.mSharedLibraries;	 Catch:{ all -> 0x01ac }
        r20 = r0;
        if (r20 == 0) goto L_0x0151;
    L_0x0133:
        r0 = r22;
        r3 = r0.mSharedLibraries;	 Catch:{ all -> 0x01ac }
        r14 = r3.length;	 Catch:{ all -> 0x01ac }
        r4 = 0;
    L_0x0139:
        if (r4 >= r14) goto L_0x0151;
    L_0x013b:
        r15 = r3[r4];	 Catch:{ all -> 0x01ac }
        r0 = r19;
        r20 = r0.contains(r15);	 Catch:{ all -> 0x01ac }
        if (r20 != 0) goto L_0x014e;
    L_0x0145:
        r20 = 0;
        r0 = r19;
        r1 = r20;
        r0.add(r1, r15);	 Catch:{ all -> 0x01ac }
    L_0x014e:
        r4 = r4 + 1;
        goto L_0x0139;
    L_0x0151:
        if (r7 == 0) goto L_0x016e;
    L_0x0153:
        r3 = r7;
        r14 = r3.length;	 Catch:{ all -> 0x01ac }
        r4 = 0;
    L_0x0156:
        if (r4 >= r14) goto L_0x016e;
    L_0x0158:
        r15 = r3[r4];	 Catch:{ all -> 0x01ac }
        r0 = r19;
        r20 = r0.contains(r15);	 Catch:{ all -> 0x01ac }
        if (r20 != 0) goto L_0x016b;
    L_0x0162:
        r20 = 0;
        r0 = r19;
        r1 = r20;
        r0.add(r1, r15);	 Catch:{ all -> 0x01ac }
    L_0x016b:
        r4 = r4 + 1;
        goto L_0x0156;
    L_0x016e:
        r20 = java.io.File.pathSeparator;	 Catch:{ all -> 0x01ac }
        r0 = r20;
        r1 = r19;
        r18 = android.text.TextUtils.join(r0, r1);	 Catch:{ all -> 0x01ac }
        r20 = java.io.File.pathSeparator;	 Catch:{ all -> 0x01ac }
        r0 = r20;
        r1 = r16;
        r15 = android.text.TextUtils.join(r0, r1);	 Catch:{ all -> 0x01ac }
        r17 = android.os.StrictMode.allowThreadDiskReads();	 Catch:{ all -> 0x01ac }
        r20 = android.app.ApplicationLoaders.getDefault();	 Catch:{ all -> 0x01ac }
        r0 = r22;
        r0 = r0.mBaseClassLoader;	 Catch:{ all -> 0x01ac }
        r21 = r0;
        r0 = r20;
        r1 = r18;
        r2 = r21;
        r20 = r0.getClassLoader(r1, r15, r2);	 Catch:{ all -> 0x01ac }
        r0 = r20;
        r1 = r22;
        r1.mClassLoader = r0;	 Catch:{ all -> 0x01ac }
        android.os.StrictMode.setThreadPolicy(r17);	 Catch:{ all -> 0x01ac }
    L_0x01a3:
        r0 = r22;
        r0 = r0.mClassLoader;	 Catch:{ all -> 0x01ac }
        r20 = r0;
        monitor-exit(r22);	 Catch:{ all -> 0x01ac }
        goto L_0x0010;
    L_0x01ac:
        r20 = move-exception;
        monitor-exit(r22);	 Catch:{ all -> 0x01ac }
        throw r20;
    L_0x01af:
        r0 = r22;
        r0 = r0.mBaseClassLoader;	 Catch:{ all -> 0x01ac }
        r20 = r0;
        if (r20 != 0) goto L_0x01c2;
    L_0x01b7:
        r20 = java.lang.ClassLoader.getSystemClassLoader();	 Catch:{ all -> 0x01ac }
        r0 = r20;
        r1 = r22;
        r1.mClassLoader = r0;	 Catch:{ all -> 0x01ac }
        goto L_0x01a3;
    L_0x01c2:
        r0 = r22;
        r0 = r0.mBaseClassLoader;	 Catch:{ all -> 0x01ac }
        r20 = r0;
        r0 = r20;
        r1 = r22;
        r1.mClassLoader = r0;	 Catch:{ all -> 0x01ac }
        goto L_0x01a3;
    L_0x01cf:
        r20 = move-exception;
        goto L_0x006f;
    L_0x01d2:
        r20 = move-exception;
        goto L_0x0050;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.app.LoadedApk.getClassLoader():java.lang.ClassLoader");
    }

    private void initializeJavaContextClassLoader() {
        try {
            PackageInfo pi = ActivityThread.getPackageManager().getPackageInfo(this.mPackageName, 0, UserHandle.myUserId());
            if (pi == null) {
                throw new IllegalStateException("Unable to get package info for " + this.mPackageName + "; is package not installed?");
            }
            boolean sharedUserIdSet;
            boolean sharable;
            if (pi.sharedUserId != null) {
                sharedUserIdSet = true;
            } else {
                sharedUserIdSet = $assertionsDisabled;
            }
            boolean processNameNotDefault;
            if (pi.applicationInfo == null || this.mPackageName.equals(pi.applicationInfo.processName)) {
                processNameNotDefault = $assertionsDisabled;
            } else {
                processNameNotDefault = true;
            }
            if (sharedUserIdSet || processNameNotDefault) {
                sharable = true;
            } else {
                sharable = $assertionsDisabled;
            }
            Thread.currentThread().setContextClassLoader(sharable ? new WarningContextClassLoader() : this.mClassLoader);
        } catch (RemoteException e) {
            throw new IllegalStateException("Unable to get package info for " + this.mPackageName + "; is system dying?", e);
        }
    }

    public String getAppDir() {
        return this.mAppDir;
    }

    public String getLibDir() {
        return this.mLibDir;
    }

    public String getResDir() {
        return this.mResDir;
    }

    public String[] getSplitAppDirs() {
        return this.mSplitAppDirs;
    }

    public String[] getSplitResDirs() {
        return this.mSplitResDirs;
    }

    public String[] getOverlayDirs() {
        return this.mOverlayDirs;
    }

    public String getDataDir() {
        return this.mDataDir;
    }

    public File getDataDirFile() {
        return this.mDataDirFile;
    }

    public AssetManager getAssets(ActivityThread mainThread) {
        return getResources(mainThread).getAssets();
    }

    public Resources getResources(ActivityThread mainThread) {
        if (this.mResources == null) {
            this.mResources = mainThread.getTopLevelResources(this.mResDir, this.mSplitResDirs, this.mOverlayDirs, this.mApplicationInfo.sharedLibraryFiles, 0, null, this);
        }
        return this.mResources;
    }

    public Application makeApplication(boolean forceDefaultAppClass, Instrumentation instrumentation) {
        if (this.mApplication != null) {
            return this.mApplication;
        }
        Application app = null;
        String appClass = this.mApplicationInfo.className;
        if (forceDefaultAppClass || appClass == null) {
            appClass = "android.app.Application";
        }
        try {
            ClassLoader cl = getClassLoader();
            if (!this.mPackageName.equals(ZenModeConfig.SYSTEM_AUTHORITY)) {
                initializeJavaContextClassLoader();
            }
            ContextImpl appContext = ContextImpl.createAppContext(this.mActivityThread, this);
            app = this.mActivityThread.mInstrumentation.newApplication(cl, appClass, appContext);
            appContext.setOuterContext(app);
        } catch (Exception e) {
            if (!this.mActivityThread.mInstrumentation.onException(null, e)) {
                throw new RuntimeException("Unable to instantiate application " + appClass + ": " + e.toString(), e);
            }
        }
        this.mActivityThread.mAllApplications.add(app);
        this.mApplication = app;
        if (instrumentation != null) {
            try {
                instrumentation.callApplicationOnCreate(app);
            } catch (Exception e2) {
                if (!instrumentation.onException(app, e2)) {
                    throw new RuntimeException("Unable to create application " + app.getClass().getName() + ": " + e2.toString(), e2);
                }
            }
        }
        SparseArray<String> packageIdentifiers = getAssets(this.mActivityThread).getAssignedPackageIdentifiers();
        int N = packageIdentifiers.size();
        for (int i = 0; i < N; i++) {
            int id = packageIdentifiers.keyAt(i);
            if (!(id == 1 || id == KeyEvent.KEYCODE_MEDIA_PAUSE)) {
                rewriteRValues(getClassLoader(), (String) packageIdentifiers.valueAt(i), id);
            }
        }
        return app;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void rewriteRValues(java.lang.ClassLoader r9, java.lang.String r10, int r11) {
        /*
        r8 = this;
        r4 = new java.lang.StringBuilder;	 Catch:{ ClassNotFoundException -> 0x0035 }
        r4.<init>();	 Catch:{ ClassNotFoundException -> 0x0035 }
        r4 = r4.append(r10);	 Catch:{ ClassNotFoundException -> 0x0035 }
        r5 = ".R";
        r4 = r4.append(r5);	 Catch:{ ClassNotFoundException -> 0x0035 }
        r4 = r4.toString();	 Catch:{ ClassNotFoundException -> 0x0035 }
        r3 = r9.loadClass(r4);	 Catch:{ ClassNotFoundException -> 0x0035 }
        r4 = "onResourcesLoaded";
        r5 = 1;
        r5 = new java.lang.Class[r5];	 Catch:{ NoSuchMethodException -> 0x004f }
        r6 = 0;
        r7 = java.lang.Integer.TYPE;	 Catch:{ NoSuchMethodException -> 0x004f }
        r5[r6] = r7;	 Catch:{ NoSuchMethodException -> 0x004f }
        r0 = r3.getMethod(r4, r5);	 Catch:{ NoSuchMethodException -> 0x004f }
        r4 = 0;
        r5 = 1;
        r5 = new java.lang.Object[r5];	 Catch:{ IllegalAccessException -> 0x0051, InvocationTargetException -> 0x006c }
        r6 = 0;
        r7 = java.lang.Integer.valueOf(r11);	 Catch:{ IllegalAccessException -> 0x0051, InvocationTargetException -> 0x006c }
        r5[r6] = r7;	 Catch:{ IllegalAccessException -> 0x0051, InvocationTargetException -> 0x006c }
        r0.invoke(r4, r5);	 Catch:{ IllegalAccessException -> 0x0051, InvocationTargetException -> 0x006c }
    L_0x0034:
        return;
    L_0x0035:
        r2 = move-exception;
        r4 = "LoadedApk";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "No resource references to update in package ";
        r5 = r5.append(r6);
        r5 = r5.append(r10);
        r5 = r5.toString();
        android.util.Log.i(r4, r5);
        goto L_0x0034;
    L_0x004f:
        r2 = move-exception;
        goto L_0x0034;
    L_0x0051:
        r2 = move-exception;
        r1 = r2;
    L_0x0053:
        r4 = new java.lang.RuntimeException;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Failed to rewrite resource references for ";
        r5 = r5.append(r6);
        r5 = r5.append(r10);
        r5 = r5.toString();
        r4.<init>(r5, r1);
        throw r4;
    L_0x006c:
        r2 = move-exception;
        r1 = r2.getCause();
        goto L_0x0053;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.app.LoadedApk.rewriteRValues(java.lang.ClassLoader, java.lang.String, int):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void removeContextRegistrations(android.content.Context r11, java.lang.String r12, java.lang.String r13) {
        /*
        r10 = this;
        r3 = android.os.StrictMode.vmRegistrationLeaksEnabled();
        r8 = r10.mReceivers;
        monitor-enter(r8);
        r7 = r10.mReceivers;	 Catch:{ all -> 0x00fc }
        r4 = r7.remove(r11);	 Catch:{ all -> 0x00fc }
        r4 = (android.util.ArrayMap) r4;	 Catch:{ all -> 0x00fc }
        if (r4 == 0) goto L_0x0082;
    L_0x0011:
        r0 = 0;
    L_0x0012:
        r7 = r4.size();	 Catch:{ all -> 0x00fc }
        if (r0 >= r7) goto L_0x0082;
    L_0x0018:
        r2 = r4.valueAt(r0);	 Catch:{ all -> 0x00fc }
        r2 = (android.app.LoadedApk.ReceiverDispatcher) r2;	 Catch:{ all -> 0x00fc }
        r1 = new android.app.IntentReceiverLeaked;	 Catch:{ all -> 0x00fc }
        r7 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00fc }
        r7.<init>();	 Catch:{ all -> 0x00fc }
        r7 = r7.append(r13);	 Catch:{ all -> 0x00fc }
        r9 = " ";
        r7 = r7.append(r9);	 Catch:{ all -> 0x00fc }
        r7 = r7.append(r12);	 Catch:{ all -> 0x00fc }
        r9 = " has leaked IntentReceiver ";
        r7 = r7.append(r9);	 Catch:{ all -> 0x00fc }
        r9 = r2.getIntentReceiver();	 Catch:{ all -> 0x00fc }
        r7 = r7.append(r9);	 Catch:{ all -> 0x00fc }
        r9 = " that was ";
        r7 = r7.append(r9);	 Catch:{ all -> 0x00fc }
        r9 = "originally registered here. Are you missing a ";
        r7 = r7.append(r9);	 Catch:{ all -> 0x00fc }
        r9 = "call to unregisterReceiver()?";
        r7 = r7.append(r9);	 Catch:{ all -> 0x00fc }
        r7 = r7.toString();	 Catch:{ all -> 0x00fc }
        r1.<init>(r7);	 Catch:{ all -> 0x00fc }
        r7 = r2.getLocation();	 Catch:{ all -> 0x00fc }
        r7 = r7.getStackTrace();	 Catch:{ all -> 0x00fc }
        r1.setStackTrace(r7);	 Catch:{ all -> 0x00fc }
        r7 = "ActivityThread";
        r9 = r1.getMessage();	 Catch:{ all -> 0x00fc }
        android.util.Slog.e(r7, r9, r1);	 Catch:{ all -> 0x00fc }
        if (r3 == 0) goto L_0x0074;
    L_0x0071:
        android.os.StrictMode.onIntentReceiverLeaked(r1);	 Catch:{ all -> 0x00fc }
    L_0x0074:
        r7 = android.app.ActivityManagerNative.getDefault();	 Catch:{ RemoteException -> 0x010b }
        r9 = r2.getIIntentReceiver();	 Catch:{ RemoteException -> 0x010b }
        r7.unregisterReceiver(r9);	 Catch:{ RemoteException -> 0x010b }
    L_0x007f:
        r0 = r0 + 1;
        goto L_0x0012;
    L_0x0082:
        r7 = r10.mUnregisteredReceivers;	 Catch:{ all -> 0x00fc }
        r7.remove(r11);	 Catch:{ all -> 0x00fc }
        monitor-exit(r8);	 Catch:{ all -> 0x00fc }
        r8 = r10.mServices;
        monitor-enter(r8);
        r7 = r10.mServices;	 Catch:{ all -> 0x0106 }
        r6 = r7.remove(r11);	 Catch:{ all -> 0x0106 }
        r6 = (android.util.ArrayMap) r6;	 Catch:{ all -> 0x0106 }
        if (r6 == 0) goto L_0x00ff;
    L_0x0095:
        r0 = 0;
    L_0x0096:
        r7 = r6.size();	 Catch:{ all -> 0x0106 }
        if (r0 >= r7) goto L_0x00ff;
    L_0x009c:
        r5 = r6.valueAt(r0);	 Catch:{ all -> 0x0106 }
        r5 = (android.app.LoadedApk.ServiceDispatcher) r5;	 Catch:{ all -> 0x0106 }
        r1 = new android.app.ServiceConnectionLeaked;	 Catch:{ all -> 0x0106 }
        r7 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0106 }
        r7.<init>();	 Catch:{ all -> 0x0106 }
        r7 = r7.append(r13);	 Catch:{ all -> 0x0106 }
        r9 = " ";
        r7 = r7.append(r9);	 Catch:{ all -> 0x0106 }
        r7 = r7.append(r12);	 Catch:{ all -> 0x0106 }
        r9 = " has leaked ServiceConnection ";
        r7 = r7.append(r9);	 Catch:{ all -> 0x0106 }
        r9 = r5.getServiceConnection();	 Catch:{ all -> 0x0106 }
        r7 = r7.append(r9);	 Catch:{ all -> 0x0106 }
        r9 = " that was originally bound here";
        r7 = r7.append(r9);	 Catch:{ all -> 0x0106 }
        r7 = r7.toString();	 Catch:{ all -> 0x0106 }
        r1.<init>(r7);	 Catch:{ all -> 0x0106 }
        r7 = r5.getLocation();	 Catch:{ all -> 0x0106 }
        r7 = r7.getStackTrace();	 Catch:{ all -> 0x0106 }
        r1.setStackTrace(r7);	 Catch:{ all -> 0x0106 }
        r7 = "ActivityThread";
        r9 = r1.getMessage();	 Catch:{ all -> 0x0106 }
        android.util.Slog.e(r7, r9, r1);	 Catch:{ all -> 0x0106 }
        if (r3 == 0) goto L_0x00eb;
    L_0x00e8:
        android.os.StrictMode.onServiceConnectionLeaked(r1);	 Catch:{ all -> 0x0106 }
    L_0x00eb:
        r7 = android.app.ActivityManagerNative.getDefault();	 Catch:{ RemoteException -> 0x0109 }
        r9 = r5.getIServiceConnection();	 Catch:{ RemoteException -> 0x0109 }
        r7.unbindService(r9);	 Catch:{ RemoteException -> 0x0109 }
    L_0x00f6:
        r5.doForget();	 Catch:{ all -> 0x0106 }
        r0 = r0 + 1;
        goto L_0x0096;
    L_0x00fc:
        r7 = move-exception;
        monitor-exit(r8);	 Catch:{ all -> 0x00fc }
        throw r7;
    L_0x00ff:
        r7 = r10.mUnboundServices;	 Catch:{ all -> 0x0106 }
        r7.remove(r11);	 Catch:{ all -> 0x0106 }
        monitor-exit(r8);	 Catch:{ all -> 0x0106 }
        return;
    L_0x0106:
        r7 = move-exception;
        monitor-exit(r8);	 Catch:{ all -> 0x0106 }
        throw r7;
    L_0x0109:
        r7 = move-exception;
        goto L_0x00f6;
    L_0x010b:
        r7 = move-exception;
        goto L_0x007f;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.app.LoadedApk.removeContextRegistrations(android.content.Context, java.lang.String, java.lang.String):void");
    }

    public IIntentReceiver getReceiverDispatcher(BroadcastReceiver r, Context context, Handler handler, Instrumentation instrumentation, boolean registered) {
        ReceiverDispatcher rd;
        Throwable th;
        synchronized (this.mReceivers) {
            ArrayMap<BroadcastReceiver, ReceiverDispatcher> map;
            ReceiverDispatcher rd2;
            IIntentReceiver iIntentReceiver;
            ArrayMap<BroadcastReceiver, ReceiverDispatcher> map2 = null;
            if (registered) {
                try {
                    map2 = (ArrayMap) this.mReceivers.get(context);
                    if (map2 != null) {
                        map = map2;
                        rd = (ReceiverDispatcher) map2.get(r);
                        if (rd != null) {
                            try {
                                rd2 = new ReceiverDispatcher(r, context, handler, instrumentation, registered);
                                if (registered) {
                                } else {
                                    if (map != null) {
                                        try {
                                            map2 = new ArrayMap();
                                            this.mReceivers.put(context, map2);
                                        } catch (Throwable th2) {
                                            th = th2;
                                            map2 = map;
                                            throw th;
                                        }
                                    }
                                    map2 = map;
                                    map2.put(r, rd2);
                                }
                            } catch (Throwable th3) {
                                th = th3;
                                map2 = map;
                                rd2 = rd;
                                throw th;
                            }
                        }
                        rd.validate(context, handler);
                        map2 = map;
                        rd2 = rd;
                        rd2.mForgotten = $assertionsDisabled;
                        iIntentReceiver = rd2.getIIntentReceiver();
                        return iIntentReceiver;
                    }
                } catch (Throwable th4) {
                    th = th4;
                    throw th;
                }
            }
            map = map2;
            rd = null;
            if (rd != null) {
                rd.validate(context, handler);
                map2 = map;
                rd2 = rd;
            } else {
                rd2 = new ReceiverDispatcher(r, context, handler, instrumentation, registered);
                if (registered) {
                } else {
                    if (map != null) {
                        map2 = map;
                    } else {
                        map2 = new ArrayMap();
                        this.mReceivers.put(context, map2);
                    }
                    map2.put(r, rd2);
                }
            }
            rd2.mForgotten = $assertionsDisabled;
            iIntentReceiver = rd2.getIIntentReceiver();
            return iIntentReceiver;
        }
    }

    public IIntentReceiver forgetReceiverDispatcher(Context context, BroadcastReceiver r) {
        IIntentReceiver iIntentReceiver;
        synchronized (this.mReceivers) {
            ReceiverDispatcher rd;
            ArrayMap<BroadcastReceiver, ReceiverDispatcher> holder;
            ArrayMap<BroadcastReceiver, ReceiverDispatcher> map = (ArrayMap) this.mReceivers.get(context);
            if (map != null) {
                rd = (ReceiverDispatcher) map.get(r);
                if (rd != null) {
                    map.remove(r);
                    if (map.size() == 0) {
                        this.mReceivers.remove(context);
                    }
                    if (r.getDebugUnregister()) {
                        holder = (ArrayMap) this.mUnregisteredReceivers.get(context);
                        if (holder == null) {
                            holder = new ArrayMap();
                            this.mUnregisteredReceivers.put(context, holder);
                        }
                        RuntimeException ex = new IllegalArgumentException("Originally unregistered here:");
                        ex.fillInStackTrace();
                        rd.setUnregisterLocation(ex);
                        holder.put(r, rd);
                    }
                    rd.mForgotten = true;
                    iIntentReceiver = rd.getIIntentReceiver();
                }
            }
            holder = (ArrayMap) this.mUnregisteredReceivers.get(context);
            if (holder != null) {
                rd = (ReceiverDispatcher) holder.get(r);
                if (rd != null) {
                    throw new IllegalArgumentException("Unregistering Receiver " + r + " that was already unregistered", rd.getUnregisterLocation());
                }
            }
            if (context == null) {
                throw new IllegalStateException("Unbinding Receiver " + r + " from Context that is no longer in use: " + context);
            }
            throw new IllegalArgumentException("Receiver not registered: " + r);
        }
        return iIntentReceiver;
    }

    public final IServiceConnection getServiceDispatcher(ServiceConnection c, Context context, Handler handler, int flags) {
        ServiceDispatcher sd;
        Throwable th;
        synchronized (this.mServices) {
            try {
                ServiceDispatcher sd2;
                ArrayMap<ServiceConnection, ServiceDispatcher> map = (ArrayMap) this.mServices.get(context);
                if (map != null) {
                    sd = (ServiceDispatcher) map.get(c);
                } else {
                    sd = null;
                }
                if (sd == null) {
                    try {
                        sd2 = new ServiceDispatcher(c, context, handler, flags);
                        if (map == null) {
                            map = new ArrayMap();
                            this.mServices.put(context, map);
                        }
                        map.put(c, sd2);
                    } catch (Throwable th2) {
                        th = th2;
                        sd2 = sd;
                        throw th;
                    }
                }
                sd.validate(context, handler);
                sd2 = sd;
                IServiceConnection iServiceConnection = sd2.getIServiceConnection();
                return iServiceConnection;
            } catch (Throwable th3) {
                th = th3;
                throw th;
            }
        }
    }

    public final IServiceConnection forgetServiceDispatcher(Context context, ServiceConnection c) {
        IServiceConnection iServiceConnection;
        synchronized (this.mServices) {
            ServiceDispatcher sd;
            ArrayMap<ServiceConnection, ServiceDispatcher> holder;
            ArrayMap<ServiceConnection, ServiceDispatcher> map = (ArrayMap) this.mServices.get(context);
            if (map != null) {
                sd = (ServiceDispatcher) map.get(c);
                if (sd != null) {
                    map.remove(c);
                    sd.doForget();
                    if (map.size() == 0) {
                        this.mServices.remove(context);
                    }
                    if ((sd.getFlags() & 2) != 0) {
                        holder = (ArrayMap) this.mUnboundServices.get(context);
                        if (holder == null) {
                            holder = new ArrayMap();
                            this.mUnboundServices.put(context, holder);
                        }
                        RuntimeException ex = new IllegalArgumentException("Originally unbound here:");
                        ex.fillInStackTrace();
                        sd.setUnbindLocation(ex);
                        holder.put(c, sd);
                    }
                    iServiceConnection = sd.getIServiceConnection();
                }
            }
            holder = (ArrayMap) this.mUnboundServices.get(context);
            if (holder != null) {
                sd = (ServiceDispatcher) holder.get(c);
                if (sd != null) {
                    throw new IllegalArgumentException("Unbinding Service " + c + " that was already unbound", sd.getUnbindLocation());
                }
            }
            if (context == null) {
                throw new IllegalStateException("Unbinding Service " + c + " from Context that is no longer in use: " + context);
            }
            throw new IllegalArgumentException("Service not registered: " + c);
        }
        return iServiceConnection;
    }
}
