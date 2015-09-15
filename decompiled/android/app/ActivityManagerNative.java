package android.app;

import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.ProcessErrorStateInfo;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.ActivityManager.StackInfo;
import android.app.ActivityManager.TaskDescription;
import android.app.ActivityManager.TaskThumbnail;
import android.app.ApplicationErrorReport.CrashInfo;
import android.app.IActivityManager.ContentProviderHolder;
import android.app.IActivityManager.WaitResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.IIntentReceiver.Stub;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.UriPermission;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.ParceledListSlice;
import android.content.pm.UserInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Debug;
import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.StrictMode.ViolationInfo;
import android.service.voice.IVoiceInteractionSession;
import android.text.TextUtils;
import android.util.Singleton;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RelativeLayout;
import android.widget.SpellChecker;
import com.android.internal.app.IVoiceInteractor;
import java.util.List;

public abstract class ActivityManagerNative extends Binder implements IActivityManager {
    private static final Singleton<IActivityManager> gDefault;
    static boolean sSystemReady;

    public static IActivityManager asInterface(IBinder obj) {
        if (obj == null) {
            return null;
        }
        IActivityManager in = (IActivityManager) obj.queryLocalInterface(IActivityManager.descriptor);
        return in == null ? new ActivityManagerProxy(obj) : in;
    }

    public static IActivityManager getDefault() {
        return (IActivityManager) gDefault.get();
    }

    public static boolean isSystemReady() {
        if (!sSystemReady) {
            sSystemReady = getDefault().testIsSystemReady();
        }
        return sSystemReady;
    }

    static {
        sSystemReady = false;
        gDefault = new Singleton<IActivityManager>() {
            protected IActivityManager create() {
                return ActivityManagerNative.asInterface(ServiceManager.getService(Context.ACTIVITY_SERVICE));
            }
        };
    }

    public static void broadcastStickyIntent(Intent intent, String permission, int userId) {
        try {
            getDefault().broadcastIntent(null, intent, null, null, -1, null, null, null, -1, false, true, userId);
        } catch (RemoteException e) {
        }
    }

    public static void noteWakeupAlarm(PendingIntent ps, int sourceUid, String sourcePkg) {
        try {
            getDefault().noteWakeupAlarm(ps.getTarget(), sourceUid, sourcePkg);
        } catch (RemoteException e) {
        }
    }

    public ActivityManagerNative() {
        attachInterface(this, IActivityManager.descriptor);
    }

    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        int result;
        IBinder token;
        Intent resultData;
        int resultCode;
        boolean res;
        IBinder b;
        IApplicationThread app;
        String packageName;
        Intent intent;
        int res2;
        Configuration config;
        String res3;
        ComponentName cn;
        int N;
        int i;
        ContentProviderHolder cph;
        boolean result2;
        boolean success;
        boolean isit;
        int mode;
        boolean converted;
        Bundle bundle;
        ActivityOptions options;
        ActivityOptions activityOptions;
        switch (code) {
            case Action.MERGE_IGNORE /*2*/:
                data.enforceInterface(IActivityManager.descriptor);
                handleApplicationCrash(data.readStrongBinder(), new CrashInfo(data));
                reply.writeNoException();
                return true;
            case SetDrawableParameters.TAG /*3*/:
                data.enforceInterface(IActivityManager.descriptor);
                result = startActivity(ApplicationThreadNative.asInterface(data.readStrongBinder()), data.readString(), (Intent) Intent.CREATOR.createFromParcel(data), data.readString(), data.readStrongBinder(), data.readString(), data.readInt(), data.readInt(), data.readInt() != 0 ? (ProfilerInfo) ProfilerInfo.CREATOR.createFromParcel(data) : null, data.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(data) : null);
                reply.writeNoException();
                reply.writeInt(result);
                return true;
            case ViewGroupAction.TAG /*4*/:
                data.enforceInterface(IActivityManager.descriptor);
                unhandledBack();
                reply.writeNoException();
                return true;
            case ReflectionActionWithoutParams.TAG /*5*/:
                data.enforceInterface(IActivityManager.descriptor);
                ParcelFileDescriptor pfd = openContentUri(Uri.parse(data.readString()));
                reply.writeNoException();
                if (pfd != null) {
                    reply.writeInt(1);
                    pfd.writeToParcel(reply, 1);
                } else {
                    reply.writeInt(0);
                }
                return true;
            case TextViewDrawableAction.TAG /*11*/:
                data.enforceInterface(IActivityManager.descriptor);
                token = data.readStrongBinder();
                resultData = null;
                resultCode = data.readInt();
                if (data.readInt() != 0) {
                    resultData = (Intent) Intent.CREATOR.createFromParcel(data);
                }
                res = finishActivity(token, resultCode, resultData, data.readInt() != 0);
                reply.writeNoException();
                reply.writeInt(res ? 1 : 0);
                return true;
            case BitmapReflectionAction.TAG /*12*/:
                data.enforceInterface(IActivityManager.descriptor);
                b = data.readStrongBinder();
                app = b != null ? ApplicationThreadNative.asInterface(b) : null;
                packageName = data.readString();
                b = data.readStrongBinder();
                intent = registerReceiver(app, packageName, b != null ? Stub.asInterface(b) : null, (IntentFilter) IntentFilter.CREATOR.createFromParcel(data), data.readString(), data.readInt());
                reply.writeNoException();
                if (intent != null) {
                    reply.writeInt(1);
                    intent.writeToParcel(reply, 0);
                } else {
                    reply.writeInt(0);
                }
                return true;
            case TextViewSizeAction.TAG /*13*/:
                data.enforceInterface(IActivityManager.descriptor);
                b = data.readStrongBinder();
                if (b == null) {
                    return true;
                }
                unregisterReceiver(Stub.asInterface(b));
                reply.writeNoException();
                return true;
            case ViewPaddingAction.TAG /*14*/:
                data.enforceInterface(IActivityManager.descriptor);
                b = data.readStrongBinder();
                app = b != null ? ApplicationThreadNative.asInterface(b) : null;
                intent = (Intent) Intent.CREATOR.createFromParcel(data);
                String resolvedType = data.readString();
                b = data.readStrongBinder();
                res2 = broadcastIntent(app, intent, resolvedType, b != null ? Stub.asInterface(b) : null, data.readInt(), data.readString(), data.readBundle(), data.readString(), data.readInt(), data.readInt() != 0, data.readInt() != 0, data.readInt());
                reply.writeNoException();
                reply.writeInt(res2);
                return true;
            case SetRemoteViewsAdapterList.TAG /*15*/:
                data.enforceInterface(IActivityManager.descriptor);
                b = data.readStrongBinder();
                unbroadcastIntent(b != null ? ApplicationThreadNative.asInterface(b) : null, (Intent) Intent.CREATOR.createFromParcel(data), data.readInt());
                reply.writeNoException();
                return true;
            case RelativeLayout.START_OF /*16*/:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder who = data.readStrongBinder();
                resultCode = data.readInt();
                String resultData2 = data.readString();
                Bundle resultExtras = data.readBundle();
                boolean resultAbort = data.readInt() != 0;
                if (who != null) {
                    finishReceiver(who, resultCode, resultData2, resultExtras, resultAbort);
                }
                reply.writeNoException();
                return true;
            case TextViewDrawableColorFilterAction.TAG /*17*/:
                data.enforceInterface(IActivityManager.descriptor);
                app = ApplicationThreadNative.asInterface(data.readStrongBinder());
                if (app != null) {
                    attachApplication(app);
                }
                reply.writeNoException();
                return true;
            case RelativeLayout.ALIGN_START /*18*/:
                data.enforceInterface(IActivityManager.descriptor);
                token = data.readStrongBinder();
                config = null;
                if (data.readInt() != 0) {
                    config = (Configuration) Configuration.CREATOR.createFromParcel(data);
                }
                boolean stopProfiling = data.readInt() != 0;
                if (token != null) {
                    activityIdle(token, config, stopProfiling);
                }
                reply.writeNoException();
                return true;
            case RelativeLayout.ALIGN_END /*19*/:
                data.enforceInterface(IActivityManager.descriptor);
                activityPaused(data.readStrongBinder());
                reply.writeNoException();
                return true;
            case RelativeLayout.ALIGN_PARENT_START /*20*/:
                data.enforceInterface(IActivityManager.descriptor);
                activityStopped(data.readStrongBinder(), data.readBundle(), data.readPersistableBundle(), (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(data));
                reply.writeNoException();
                return true;
            case RelativeLayout.ALIGN_PARENT_END /*21*/:
                data.enforceInterface(IActivityManager.descriptor);
                token = data.readStrongBinder();
                res3 = token != null ? getCallingPackage(token) : null;
                reply.writeNoException();
                reply.writeString(res3);
                return true;
            case MotionEvent.AXIS_GAS /*22*/:
                data.enforceInterface(IActivityManager.descriptor);
                cn = getCallingActivity(data.readStrongBinder());
                reply.writeNoException();
                ComponentName.writeToParcel(cn, reply);
                return true;
            case MotionEvent.AXIS_BRAKE /*23*/:
                data.enforceInterface(IActivityManager.descriptor);
                List<RunningTaskInfo> list = getTasks(data.readInt(), data.readInt());
                reply.writeNoException();
                N = list != null ? list.size() : -1;
                reply.writeInt(N);
                for (i = 0; i < N; i++) {
                    ((RunningTaskInfo) list.get(i)).writeToParcel(reply, 0);
                }
                return true;
            case MotionEvent.AXIS_DISTANCE /*24*/:
                data.enforceInterface(IActivityManager.descriptor);
                moveTaskToFront(data.readInt(), data.readInt(), data.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(data) : null);
                reply.writeNoException();
                return true;
            case MotionEvent.AXIS_TILT /*25*/:
                data.enforceInterface(IActivityManager.descriptor);
                moveTaskToBack(data.readInt());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_POWER /*26*/:
                data.enforceInterface(IActivityManager.descriptor);
                moveTaskBackwards(data.readInt());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_CAMERA /*27*/:
                data.enforceInterface(IActivityManager.descriptor);
                token = data.readStrongBinder();
                res2 = token != null ? getTaskForActivity(token, data.readInt() != 0) : -1;
                reply.writeNoException();
                reply.writeInt(res2);
                return true;
            case KeyEvent.KEYCODE_A /*29*/:
                data.enforceInterface(IActivityManager.descriptor);
                cph = getContentProvider(ApplicationThreadNative.asInterface(data.readStrongBinder()), data.readString(), data.readInt(), data.readInt() != 0);
                reply.writeNoException();
                if (cph != null) {
                    reply.writeInt(1);
                    cph.writeToParcel(reply, 0);
                } else {
                    reply.writeInt(0);
                }
                return true;
            case KeyEvent.KEYCODE_B /*30*/:
                data.enforceInterface(IActivityManager.descriptor);
                publishContentProviders(ApplicationThreadNative.asInterface(data.readStrongBinder()), data.createTypedArrayList(ContentProviderHolder.CREATOR));
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_C /*31*/:
                data.enforceInterface(IActivityManager.descriptor);
                res = refContentProvider(data.readStrongBinder(), data.readInt(), data.readInt());
                reply.writeNoException();
                reply.writeInt(res ? 1 : 0);
                return true;
            case AccessibilityNodeInfo.ACTION_LONG_CLICK /*32*/:
                data.enforceInterface(IActivityManager.descriptor);
                finishSubActivity(data.readStrongBinder(), data.readString(), data.readInt());
                reply.writeNoException();
                return true;
            case MotionEvent.AXIS_GENERIC_2 /*33*/:
                data.enforceInterface(IActivityManager.descriptor);
                PendingIntent pi = getRunningServiceControlPanel((ComponentName) ComponentName.CREATOR.createFromParcel(data));
                reply.writeNoException();
                PendingIntent.writePendingIntentOrNullToParcel(pi, reply);
                return true;
            case MotionEvent.AXIS_GENERIC_3 /*34*/:
                data.enforceInterface(IActivityManager.descriptor);
                cn = startService(ApplicationThreadNative.asInterface(data.readStrongBinder()), (Intent) Intent.CREATOR.createFromParcel(data), data.readString(), data.readInt());
                reply.writeNoException();
                ComponentName.writeToParcel(cn, reply);
                return true;
            case MotionEvent.AXIS_GENERIC_4 /*35*/:
                data.enforceInterface(IActivityManager.descriptor);
                res2 = stopService(ApplicationThreadNative.asInterface(data.readStrongBinder()), (Intent) Intent.CREATOR.createFromParcel(data), data.readString(), data.readInt());
                reply.writeNoException();
                reply.writeInt(res2);
                return true;
            case MotionEvent.AXIS_GENERIC_5 /*36*/:
                data.enforceInterface(IActivityManager.descriptor);
                res2 = bindService(ApplicationThreadNative.asInterface(data.readStrongBinder()), data.readStrongBinder(), (Intent) Intent.CREATOR.createFromParcel(data), data.readString(), IServiceConnection.Stub.asInterface(data.readStrongBinder()), data.readInt(), data.readInt());
                reply.writeNoException();
                reply.writeInt(res2);
                return true;
            case MotionEvent.AXIS_GENERIC_6 /*37*/:
                data.enforceInterface(IActivityManager.descriptor);
                res = unbindService(IServiceConnection.Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                reply.writeInt(res ? 1 : 0);
                return true;
            case MotionEvent.AXIS_GENERIC_7 /*38*/:
                data.enforceInterface(IActivityManager.descriptor);
                publishService(data.readStrongBinder(), (Intent) Intent.CREATOR.createFromParcel(data), data.readStrongBinder());
                reply.writeNoException();
                return true;
            case MotionEvent.AXIS_GENERIC_8 /*39*/:
                data.enforceInterface(IActivityManager.descriptor);
                activityResumed(data.readStrongBinder());
                reply.writeNoException();
                return true;
            case MotionEvent.AXIS_GENERIC_11 /*42*/:
                data.enforceInterface(IActivityManager.descriptor);
                setDebugApp(data.readString(), data.readInt() != 0, data.readInt() != 0);
                reply.writeNoException();
                return true;
            case MotionEvent.AXIS_GENERIC_12 /*43*/:
                data.enforceInterface(IActivityManager.descriptor);
                setAlwaysFinish(data.readInt() != 0);
                reply.writeNoException();
                return true;
            case MotionEvent.AXIS_GENERIC_13 /*44*/:
                data.enforceInterface(IActivityManager.descriptor);
                res = startInstrumentation(ComponentName.readFromParcel(data), data.readString(), data.readInt(), data.readBundle(), IInstrumentationWatcher.Stub.asInterface(data.readStrongBinder()), IUiAutomationConnection.Stub.asInterface(data.readStrongBinder()), data.readInt(), data.readString());
                reply.writeNoException();
                reply.writeInt(res ? 1 : 0);
                return true;
            case MotionEvent.AXIS_GENERIC_14 /*45*/:
                data.enforceInterface(IActivityManager.descriptor);
                finishInstrumentation(ApplicationThreadNative.asInterface(data.readStrongBinder()), data.readInt(), data.readBundle());
                reply.writeNoException();
                return true;
            case MotionEvent.AXIS_GENERIC_15 /*46*/:
                data.enforceInterface(IActivityManager.descriptor);
                config = getConfiguration();
                reply.writeNoException();
                config.writeToParcel(reply, 0);
                return true;
            case MotionEvent.AXIS_GENERIC_16 /*47*/:
                data.enforceInterface(IActivityManager.descriptor);
                updateConfiguration((Configuration) Configuration.CREATOR.createFromParcel(data));
                reply.writeNoException();
                return true;
            case LayoutParams.SOFT_INPUT_ADJUST_NOTHING /*48*/:
                data.enforceInterface(IActivityManager.descriptor);
                res = stopServiceToken(ComponentName.readFromParcel(data), data.readStrongBinder(), data.readInt());
                reply.writeNoException();
                reply.writeInt(res ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_U /*49*/:
                data.enforceInterface(IActivityManager.descriptor);
                cn = getActivityClassForToken(data.readStrongBinder());
                reply.writeNoException();
                ComponentName.writeToParcel(cn, reply);
                return true;
            case SpellChecker.MAX_NUMBER_OF_WORDS /*50*/:
                data.enforceInterface(IActivityManager.descriptor);
                token = data.readStrongBinder();
                reply.writeNoException();
                reply.writeString(getPackageForToken(token));
                return true;
            case KeyEvent.KEYCODE_W /*51*/:
                data.enforceInterface(IActivityManager.descriptor);
                setProcessLimit(data.readInt());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_X /*52*/:
                data.enforceInterface(IActivityManager.descriptor);
                int limit = getProcessLimit();
                reply.writeNoException();
                reply.writeInt(limit);
                return true;
            case KeyEvent.KEYCODE_Y /*53*/:
                data.enforceInterface(IActivityManager.descriptor);
                res2 = checkPermission(data.readString(), data.readInt(), data.readInt());
                reply.writeNoException();
                reply.writeInt(res2);
                return true;
            case KeyEvent.KEYCODE_Z /*54*/:
                data.enforceInterface(IActivityManager.descriptor);
                res2 = checkUriPermission((Uri) Uri.CREATOR.createFromParcel(data), data.readInt(), data.readInt(), data.readInt(), data.readInt(), data.readStrongBinder());
                reply.writeNoException();
                reply.writeInt(res2);
                return true;
            case KeyEvent.KEYCODE_COMMA /*55*/:
                data.enforceInterface(IActivityManager.descriptor);
                grantUriPermission(ApplicationThreadNative.asInterface(data.readStrongBinder()), data.readString(), (Uri) Uri.CREATOR.createFromParcel(data), data.readInt(), data.readInt());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_PERIOD /*56*/:
                data.enforceInterface(IActivityManager.descriptor);
                revokeUriPermission(ApplicationThreadNative.asInterface(data.readStrongBinder()), (Uri) Uri.CREATOR.createFromParcel(data), data.readInt(), data.readInt());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_ALT_LEFT /*57*/:
                data.enforceInterface(IActivityManager.descriptor);
                setActivityController(IActivityController.Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_ALT_RIGHT /*58*/:
                data.enforceInterface(IActivityManager.descriptor);
                showWaitingForDebugger(ApplicationThreadNative.asInterface(data.readStrongBinder()), data.readInt() != 0);
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_SHIFT_LEFT /*59*/:
                data.enforceInterface(IActivityManager.descriptor);
                signalPersistentProcesses(data.readInt());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_SHIFT_RIGHT /*60*/:
                data.enforceInterface(IActivityManager.descriptor);
                List<RecentTaskInfo> list2 = getRecentTasks(data.readInt(), data.readInt(), data.readInt());
                reply.writeNoException();
                reply.writeTypedList(list2);
                return true;
            case KeyEvent.KEYCODE_TAB /*61*/:
                data.enforceInterface(IActivityManager.descriptor);
                serviceDoneExecuting(data.readStrongBinder(), data.readInt(), data.readInt(), data.readInt());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_SPACE /*62*/:
                data.enforceInterface(IActivityManager.descriptor);
                activityDestroyed(data.readStrongBinder());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_SYM /*63*/:
                Intent[] requestIntents;
                String[] requestResolvedTypes;
                data.enforceInterface(IActivityManager.descriptor);
                int type = data.readInt();
                packageName = data.readString();
                token = data.readStrongBinder();
                String resultWho = data.readString();
                int requestCode = data.readInt();
                if (data.readInt() != 0) {
                    requestIntents = (Intent[]) data.createTypedArray(Intent.CREATOR);
                    requestResolvedTypes = data.createStringArray();
                } else {
                    requestIntents = null;
                    requestResolvedTypes = null;
                }
                IIntentSender res4 = getIntentSender(type, packageName, token, resultWho, requestCode, requestIntents, requestResolvedTypes, data.readInt(), data.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(data) : null, data.readInt());
                reply.writeNoException();
                reply.writeStrongBinder(res4 != null ? res4.asBinder() : null);
                return true;
            case AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS /*64*/:
                data.enforceInterface(IActivityManager.descriptor);
                cancelIntentSender(IIntentSender.Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_ENVELOPE /*65*/:
                data.enforceInterface(IActivityManager.descriptor);
                res3 = getPackageForIntentSender(IIntentSender.Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                reply.writeString(res3);
                return true;
            case KeyEvent.KEYCODE_ENTER /*66*/:
                data.enforceInterface(IActivityManager.descriptor);
                enterSafeMode();
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_DEL /*67*/:
                data.enforceInterface(IActivityManager.descriptor);
                result2 = startNextMatchingActivity(data.readStrongBinder(), (Intent) Intent.CREATOR.createFromParcel(data), data.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(data) : null);
                reply.writeNoException();
                reply.writeInt(result2 ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_GRAVE /*68*/:
                data.enforceInterface(IActivityManager.descriptor);
                noteWakeupAlarm(IIntentSender.Stub.asInterface(data.readStrongBinder()), data.readInt(), data.readString());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_MINUS /*69*/:
                data.enforceInterface(IActivityManager.descriptor);
                removeContentProvider(data.readStrongBinder(), data.readInt() != 0);
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_EQUALS /*70*/:
                data.enforceInterface(IActivityManager.descriptor);
                setRequestedOrientation(data.readStrongBinder(), data.readInt());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_LEFT_BRACKET /*71*/:
                data.enforceInterface(IActivityManager.descriptor);
                int req = getRequestedOrientation(data.readStrongBinder());
                reply.writeNoException();
                reply.writeInt(req);
                return true;
            case KeyEvent.KEYCODE_RIGHT_BRACKET /*72*/:
                data.enforceInterface(IActivityManager.descriptor);
                unbindFinished(data.readStrongBinder(), (Intent) Intent.CREATOR.createFromParcel(data), data.readInt() != 0);
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_BACKSLASH /*73*/:
                data.enforceInterface(IActivityManager.descriptor);
                setProcessForeground(data.readStrongBinder(), data.readInt(), data.readInt() != 0);
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_SEMICOLON /*74*/:
                data.enforceInterface(IActivityManager.descriptor);
                ComponentName className = ComponentName.readFromParcel(data);
                token = data.readStrongBinder();
                int id = data.readInt();
                Notification notification = null;
                if (data.readInt() != 0) {
                    notification = (Notification) Notification.CREATOR.createFromParcel(data);
                }
                setServiceForeground(className, token, id, notification, data.readInt() != 0);
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_APOSTROPHE /*75*/:
                data.enforceInterface(IActivityManager.descriptor);
                res = moveActivityTaskToBack(data.readStrongBinder(), data.readInt() != 0);
                reply.writeNoException();
                reply.writeInt(res ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_SLASH /*76*/:
                data.enforceInterface(IActivityManager.descriptor);
                MemoryInfo mi = new MemoryInfo();
                getMemoryInfo(mi);
                reply.writeNoException();
                mi.writeToParcel(reply, 0);
                return true;
            case KeyEvent.KEYCODE_AT /*77*/:
                data.enforceInterface(IActivityManager.descriptor);
                List<ProcessErrorStateInfo> list3 = getProcessesInErrorState();
                reply.writeNoException();
                reply.writeTypedList(list3);
                return true;
            case KeyEvent.KEYCODE_NUM /*78*/:
                data.enforceInterface(IActivityManager.descriptor);
                res = clearApplicationUserData(data.readString(), IPackageDataObserver.Stub.asInterface(data.readStrongBinder()), data.readInt());
                reply.writeNoException();
                reply.writeInt(res ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_HEADSETHOOK /*79*/:
                data.enforceInterface(IActivityManager.descriptor);
                forceStopPackage(data.readString(), data.readInt());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_FOCUS /*80*/:
                data.enforceInterface(IActivityManager.descriptor);
                res = killPids(data.createIntArray(), data.readString(), data.readInt() != 0);
                reply.writeNoException();
                reply.writeInt(res ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_PLUS /*81*/:
                data.enforceInterface(IActivityManager.descriptor);
                List<RunningServiceInfo> list4 = getServices(data.readInt(), data.readInt());
                reply.writeNoException();
                N = list4 != null ? list4.size() : -1;
                reply.writeInt(N);
                for (i = 0; i < N; i++) {
                    ((RunningServiceInfo) list4.get(i)).writeToParcel(reply, 0);
                }
                return true;
            case KeyEvent.KEYCODE_MENU /*82*/:
                data.enforceInterface(IActivityManager.descriptor);
                TaskThumbnail taskThumbnail = getTaskThumbnail(data.readInt());
                reply.writeNoException();
                if (taskThumbnail != null) {
                    reply.writeInt(1);
                    taskThumbnail.writeToParcel(reply, 1);
                } else {
                    reply.writeInt(0);
                }
                return true;
            case KeyEvent.KEYCODE_NOTIFICATION /*83*/:
                data.enforceInterface(IActivityManager.descriptor);
                List<RunningAppProcessInfo> list5 = getRunningAppProcesses();
                reply.writeNoException();
                reply.writeTypedList(list5);
                return true;
            case KeyEvent.MAX_KEYCODE /*84*/:
                data.enforceInterface(IActivityManager.descriptor);
                ConfigurationInfo config2 = getDeviceConfigurationInfo();
                reply.writeNoException();
                config2.writeToParcel(reply, 0);
                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE /*85*/:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder binder = peekService((Intent) Intent.CREATOR.createFromParcel(data), data.readString());
                reply.writeNoException();
                reply.writeStrongBinder(binder);
                return true;
            case KeyEvent.KEYCODE_MEDIA_STOP /*86*/:
                data.enforceInterface(IActivityManager.descriptor);
                res = profileControl(data.readString(), data.readInt(), data.readInt() != 0, data.readInt() != 0 ? (ProfilerInfo) ProfilerInfo.CREATOR.createFromParcel(data) : null, data.readInt());
                reply.writeNoException();
                reply.writeInt(res ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_MEDIA_NEXT /*87*/:
                data.enforceInterface(IActivityManager.descriptor);
                res = shutdown(data.readInt());
                reply.writeNoException();
                reply.writeInt(res ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS /*88*/:
                data.enforceInterface(IActivityManager.descriptor);
                stopAppSwitches();
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_MEDIA_REWIND /*89*/:
                data.enforceInterface(IActivityManager.descriptor);
                resumeAppSwitches();
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD /*90*/:
                data.enforceInterface(IActivityManager.descriptor);
                success = bindBackupAgent((ApplicationInfo) ApplicationInfo.CREATOR.createFromParcel(data), data.readInt());
                reply.writeNoException();
                reply.writeInt(success ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_MUTE /*91*/:
                data.enforceInterface(IActivityManager.descriptor);
                backupAgentCreated(data.readString(), data.readStrongBinder());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_PAGE_UP /*92*/:
                data.enforceInterface(IActivityManager.descriptor);
                unbindBackupAgent((ApplicationInfo) ApplicationInfo.CREATOR.createFromParcel(data));
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_PAGE_DOWN /*93*/:
                data.enforceInterface(IActivityManager.descriptor);
                res2 = getUidForIntentSender(IIntentSender.Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                reply.writeInt(res2);
                return true;
            case KeyEvent.KEYCODE_PICTSYMBOLS /*94*/:
                data.enforceInterface(IActivityManager.descriptor);
                res2 = handleIncomingUser(data.readInt(), data.readInt(), data.readInt(), data.readInt() != 0, data.readInt() != 0, data.readString(), data.readString());
                reply.writeNoException();
                reply.writeInt(res2);
                return true;
            case KeyEvent.KEYCODE_SWITCH_CHARSET /*95*/:
                data.enforceInterface(IActivityManager.descriptor);
                addPackageDependency(data.readString());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_BUTTON_A /*96*/:
                data.enforceInterface(IActivityManager.descriptor);
                killApplicationWithAppId(data.readString(), data.readInt(), data.readString());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_BUTTON_B /*97*/:
                data.enforceInterface(IActivityManager.descriptor);
                closeSystemDialogs(data.readString());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_BUTTON_C /*98*/:
                data.enforceInterface(IActivityManager.descriptor);
                Debug.MemoryInfo[] res5 = getProcessMemoryInfo(data.createIntArray());
                reply.writeNoException();
                reply.writeTypedArray(res5, 1);
                return true;
            case LayoutParams.LAST_APPLICATION_WINDOW /*99*/:
                data.enforceInterface(IActivityManager.descriptor);
                killApplicationProcess(data.readString(), data.readInt());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_BUTTON_Y /*100*/:
                data.enforceInterface(IActivityManager.descriptor);
                app = ApplicationThreadNative.asInterface(data.readStrongBinder());
                IntentSender intent2 = (IntentSender) IntentSender.CREATOR.createFromParcel(data);
                Intent fillInIntent = null;
                if (data.readInt() != 0) {
                    fillInIntent = (Intent) Intent.CREATOR.createFromParcel(data);
                }
                result = startActivityIntentSender(app, intent2, fillInIntent, data.readString(), data.readStrongBinder(), data.readString(), data.readInt(), data.readInt(), data.readInt(), data.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(data) : null);
                reply.writeNoException();
                reply.writeInt(result);
                return true;
            case KeyEvent.KEYCODE_BUTTON_Z /*101*/:
                data.enforceInterface(IActivityManager.descriptor);
                overridePendingTransition(data.readStrongBinder(), data.readString(), data.readInt(), data.readInt());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_BUTTON_L1 /*102*/:
                data.enforceInterface(IActivityManager.descriptor);
                res = handleApplicationWtf(data.readStrongBinder(), data.readString(), data.readInt() != 0, new CrashInfo(data));
                reply.writeNoException();
                reply.writeInt(res ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_BUTTON_R1 /*103*/:
                data.enforceInterface(IActivityManager.descriptor);
                killBackgroundProcesses(data.readString(), data.readInt());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_BUTTON_L2 /*104*/:
                data.enforceInterface(IActivityManager.descriptor);
                boolean areThey = isUserAMonkey();
                reply.writeNoException();
                reply.writeInt(areThey ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_BUTTON_R2 /*105*/:
                data.enforceInterface(IActivityManager.descriptor);
                WaitResult result3 = startActivityAndWait(ApplicationThreadNative.asInterface(data.readStrongBinder()), data.readString(), (Intent) Intent.CREATOR.createFromParcel(data), data.readString(), data.readStrongBinder(), data.readString(), data.readInt(), data.readInt(), data.readInt() != 0 ? (ProfilerInfo) ProfilerInfo.CREATOR.createFromParcel(data) : null, data.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(data) : null, data.readInt());
                reply.writeNoException();
                result3.writeToParcel(reply, 0);
                return true;
            case KeyEvent.KEYCODE_BUTTON_THUMBL /*106*/:
                data.enforceInterface(IActivityManager.descriptor);
                res = willActivityBeVisible(data.readStrongBinder());
                reply.writeNoException();
                reply.writeInt(res ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_BUTTON_THUMBR /*107*/:
                data.enforceInterface(IActivityManager.descriptor);
                result = startActivityWithConfig(ApplicationThreadNative.asInterface(data.readStrongBinder()), data.readString(), (Intent) Intent.CREATOR.createFromParcel(data), data.readString(), data.readStrongBinder(), data.readString(), data.readInt(), data.readInt(), (Configuration) Configuration.CREATOR.createFromParcel(data), data.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(data) : null, data.readInt());
                reply.writeNoException();
                reply.writeInt(result);
                return true;
            case KeyEvent.KEYCODE_BUTTON_START /*108*/:
                data.enforceInterface(IActivityManager.descriptor);
                List<ApplicationInfo> list6 = getRunningExternalApplications();
                reply.writeNoException();
                reply.writeTypedList(list6);
                return true;
            case KeyEvent.KEYCODE_BUTTON_SELECT /*109*/:
                data.enforceInterface(IActivityManager.descriptor);
                finishHeavyWeightApp();
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_BUTTON_MODE /*110*/:
                data.enforceInterface(IActivityManager.descriptor);
                handleApplicationStrictModeViolation(data.readStrongBinder(), data.readInt(), new ViolationInfo(data));
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_ESCAPE /*111*/:
                data.enforceInterface(IActivityManager.descriptor);
                isit = isImmersive(data.readStrongBinder());
                reply.writeNoException();
                reply.writeInt(isit ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_FORWARD_DEL /*112*/:
                data.enforceInterface(IActivityManager.descriptor);
                setImmersive(data.readStrongBinder(), data.readInt() == 1);
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_CTRL_LEFT /*113*/:
                data.enforceInterface(IActivityManager.descriptor);
                isit = isTopActivityImmersive();
                reply.writeNoException();
                reply.writeInt(isit ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_CTRL_RIGHT /*114*/:
                data.enforceInterface(IActivityManager.descriptor);
                crashApplication(data.readInt(), data.readInt(), data.readString(), data.readString());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_CAPS_LOCK /*115*/:
                data.enforceInterface(IActivityManager.descriptor);
                String type2 = getProviderMimeType((Uri) Uri.CREATOR.createFromParcel(data), data.readInt());
                reply.writeNoException();
                reply.writeString(type2);
                return true;
            case KeyEvent.KEYCODE_SCROLL_LOCK /*116*/:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder perm = newUriPermissionOwner(data.readString());
                reply.writeNoException();
                reply.writeStrongBinder(perm);
                return true;
            case KeyEvent.KEYCODE_META_LEFT /*117*/:
                data.enforceInterface(IActivityManager.descriptor);
                grantUriPermissionFromOwner(data.readStrongBinder(), data.readInt(), data.readString(), (Uri) Uri.CREATOR.createFromParcel(data), data.readInt(), data.readInt(), data.readInt());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_META_RIGHT /*118*/:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder owner = data.readStrongBinder();
                Uri uri = null;
                if (data.readInt() != 0) {
                    uri = (Uri) Uri.CREATOR.createFromParcel(data);
                }
                revokeUriPermissionFromOwner(owner, uri, data.readInt(), data.readInt());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_FUNCTION /*119*/:
                data.enforceInterface(IActivityManager.descriptor);
                res2 = checkGrantUriPermission(data.readInt(), data.readString(), (Uri) Uri.CREATOR.createFromParcel(data), data.readInt(), data.readInt());
                reply.writeNoException();
                reply.writeInt(res2);
                return true;
            case KeyEvent.KEYCODE_SYSRQ /*120*/:
                data.enforceInterface(IActivityManager.descriptor);
                res = dumpHeap(data.readString(), data.readInt(), data.readInt() != 0, data.readString(), data.readInt() != 0 ? (ParcelFileDescriptor) ParcelFileDescriptor.CREATOR.createFromParcel(data) : null);
                reply.writeNoException();
                reply.writeInt(res ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_BREAK /*121*/:
                data.enforceInterface(IActivityManager.descriptor);
                result = startActivities(ApplicationThreadNative.asInterface(data.readStrongBinder()), data.readString(), (Intent[]) data.createTypedArray(Intent.CREATOR), data.createStringArray(), data.readStrongBinder(), data.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(data) : null, data.readInt());
                reply.writeNoException();
                reply.writeInt(result);
                return true;
            case KeyEvent.KEYCODE_MOVE_HOME /*122*/:
                data.enforceInterface(IActivityManager.descriptor);
                result2 = isUserRunning(data.readInt(), data.readInt() != 0);
                reply.writeNoException();
                reply.writeInt(result2 ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_MOVE_END /*123*/:
                data.enforceInterface(IActivityManager.descriptor);
                activitySlept(data.readStrongBinder());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_INSERT /*124*/:
                data.enforceInterface(IActivityManager.descriptor);
                mode = getFrontActivityScreenCompatMode();
                reply.writeNoException();
                reply.writeInt(mode);
                return true;
            case KeyEvent.KEYCODE_FORWARD /*125*/:
                data.enforceInterface(IActivityManager.descriptor);
                mode = data.readInt();
                setFrontActivityScreenCompatMode(mode);
                reply.writeNoException();
                reply.writeInt(mode);
                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY /*126*/:
                data.enforceInterface(IActivityManager.descriptor);
                mode = getPackageScreenCompatMode(data.readString());
                reply.writeNoException();
                reply.writeInt(mode);
                return true;
            case KeyEvent.KEYCODE_MEDIA_PAUSE /*127*/:
                data.enforceInterface(IActivityManager.descriptor);
                setPackageScreenCompatMode(data.readString(), data.readInt());
                reply.writeNoException();
                return true;
            case AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS /*128*/:
                data.enforceInterface(IActivityManager.descriptor);
                boolean ask = getPackageAskScreenCompat(data.readString());
                reply.writeNoException();
                reply.writeInt(ask ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_MEDIA_EJECT /*129*/:
                data.enforceInterface(IActivityManager.descriptor);
                setPackageAskScreenCompat(data.readString(), data.readInt() != 0);
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_MEDIA_RECORD /*130*/:
                data.enforceInterface(IActivityManager.descriptor);
                result2 = switchUser(data.readInt());
                reply.writeNoException();
                reply.writeInt(result2 ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_F2 /*132*/:
                data.enforceInterface(IActivityManager.descriptor);
                result2 = removeTask(data.readInt());
                reply.writeNoException();
                reply.writeInt(result2 ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_F3 /*133*/:
                data.enforceInterface(IActivityManager.descriptor);
                registerProcessObserver(IProcessObserver.Stub.asInterface(data.readStrongBinder()));
                return true;
            case KeyEvent.KEYCODE_F4 /*134*/:
                data.enforceInterface(IActivityManager.descriptor);
                unregisterProcessObserver(IProcessObserver.Stub.asInterface(data.readStrongBinder()));
                return true;
            case KeyEvent.KEYCODE_F5 /*135*/:
                data.enforceInterface(IActivityManager.descriptor);
                res = isIntentSenderTargetedToPackage(IIntentSender.Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                reply.writeInt(res ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_F6 /*136*/:
                data.enforceInterface(IActivityManager.descriptor);
                updatePersistentConfiguration((Configuration) Configuration.CREATOR.createFromParcel(data));
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_F7 /*137*/:
                data.enforceInterface(IActivityManager.descriptor);
                long[] pss = getProcessPss(data.createIntArray());
                reply.writeNoException();
                reply.writeLongArray(pss);
                return true;
            case KeyEvent.KEYCODE_F8 /*138*/:
                data.enforceInterface(IActivityManager.descriptor);
                showBootMessage((CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(data), data.readInt() != 0);
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_F10 /*140*/:
                data.enforceInterface(IActivityManager.descriptor);
                killAllBackgroundProcesses();
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_F11 /*141*/:
                data.enforceInterface(IActivityManager.descriptor);
                cph = getContentProviderExternal(data.readString(), data.readInt(), data.readStrongBinder());
                reply.writeNoException();
                if (cph != null) {
                    reply.writeInt(1);
                    cph.writeToParcel(reply, 0);
                } else {
                    reply.writeInt(0);
                }
                return true;
            case KeyEvent.KEYCODE_F12 /*142*/:
                data.enforceInterface(IActivityManager.descriptor);
                removeContentProviderExternal(data.readString(), data.readStrongBinder());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_NUM_LOCK /*143*/:
                data.enforceInterface(IActivityManager.descriptor);
                RunningAppProcessInfo info = new RunningAppProcessInfo();
                getMyMemoryState(info);
                reply.writeNoException();
                info.writeToParcel(reply, 0);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_0 /*144*/:
                data.enforceInterface(IActivityManager.descriptor);
                res = killProcessesBelowForeground(data.readString());
                reply.writeNoException();
                reply.writeInt(res ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_1 /*145*/:
                data.enforceInterface(IActivityManager.descriptor);
                UserInfo userInfo = getCurrentUser();
                reply.writeNoException();
                userInfo.writeToParcel(reply, 0);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_2 /*146*/:
                data.enforceInterface(IActivityManager.descriptor);
                res = shouldUpRecreateTask(data.readStrongBinder(), data.readString());
                reply.writeNoException();
                reply.writeInt(res ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_3 /*147*/:
                data.enforceInterface(IActivityManager.descriptor);
                token = data.readStrongBinder();
                Intent target = (Intent) Intent.CREATOR.createFromParcel(data);
                resultCode = data.readInt();
                resultData = null;
                if (data.readInt() != 0) {
                    resultData = (Intent) Intent.CREATOR.createFromParcel(data);
                }
                res = navigateUpTo(token, target, resultCode, resultData);
                reply.writeNoException();
                reply.writeInt(res ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_4 /*148*/:
                data.enforceInterface(IActivityManager.descriptor);
                setLockScreenShown(data.readInt() != 0);
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_NUMPAD_5 /*149*/:
                data.enforceInterface(IActivityManager.descriptor);
                res = finishActivityAffinity(data.readStrongBinder());
                reply.writeNoException();
                reply.writeInt(res ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_6 /*150*/:
                data.enforceInterface(IActivityManager.descriptor);
                res2 = getLaunchedFromUid(data.readStrongBinder());
                reply.writeNoException();
                reply.writeInt(res2);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_7 /*151*/:
                data.enforceInterface(IActivityManager.descriptor);
                unstableProviderDied(data.readStrongBinder());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_NUMPAD_8 /*152*/:
                data.enforceInterface(IActivityManager.descriptor);
                res = isIntentSenderAnActivity(IIntentSender.Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                reply.writeInt(res ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_9 /*153*/:
                data.enforceInterface(IActivityManager.descriptor);
                result = startActivityAsUser(ApplicationThreadNative.asInterface(data.readStrongBinder()), data.readString(), (Intent) Intent.CREATOR.createFromParcel(data), data.readString(), data.readStrongBinder(), data.readString(), data.readInt(), data.readInt(), data.readInt() != 0 ? (ProfilerInfo) ProfilerInfo.CREATOR.createFromParcel(data) : null, data.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(data) : null, data.readInt());
                reply.writeNoException();
                reply.writeInt(result);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_DIVIDE /*154*/:
                data.enforceInterface(IActivityManager.descriptor);
                result = stopUser(data.readInt(), IStopUserCallback.Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                reply.writeInt(result);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_MULTIPLY /*155*/:
                data.enforceInterface(IActivityManager.descriptor);
                registerUserSwitchObserver(IUserSwitchObserver.Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_NUMPAD_SUBTRACT /*156*/:
                data.enforceInterface(IActivityManager.descriptor);
                unregisterUserSwitchObserver(IUserSwitchObserver.Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_NUMPAD_ADD /*157*/:
                data.enforceInterface(IActivityManager.descriptor);
                int[] result4 = getRunningUserIds();
                reply.writeNoException();
                reply.writeIntArray(result4);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_DOT /*158*/:
                data.enforceInterface(IActivityManager.descriptor);
                requestBugReport();
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_NUMPAD_COMMA /*159*/:
                data.enforceInterface(IActivityManager.descriptor);
                long res6 = inputDispatchingTimedOut(data.readInt(), data.readInt() != 0, data.readString());
                reply.writeNoException();
                reply.writeLong(res6);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_EQUALS /*161*/:
                data.enforceInterface(IActivityManager.descriptor);
                intent = getIntentForIntentSender(IIntentSender.Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                if (intent != null) {
                    reply.writeInt(1);
                    intent.writeToParcel(reply, 1);
                } else {
                    reply.writeInt(0);
                }
                return true;
            case KeyEvent.KEYCODE_NUMPAD_LEFT_PAREN /*162*/:
                data.enforceInterface(IActivityManager.descriptor);
                Bundle res7 = getAssistContextExtras(data.readInt());
                reply.writeNoException();
                reply.writeBundle(res7);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_RIGHT_PAREN /*163*/:
                data.enforceInterface(IActivityManager.descriptor);
                reportAssistContextExtras(data.readStrongBinder(), data.readBundle());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_VOLUME_MUTE /*164*/:
                data.enforceInterface(IActivityManager.descriptor);
                res3 = getLaunchedFromPackage(data.readStrongBinder());
                reply.writeNoException();
                reply.writeString(res3);
                return true;
            case KeyEvent.KEYCODE_INFO /*165*/:
                data.enforceInterface(IActivityManager.descriptor);
                killUid(data.readInt(), data.readString());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_CHANNEL_UP /*166*/:
                data.enforceInterface(IActivityManager.descriptor);
                setUserIsMonkey(data.readInt() == 1);
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_CHANNEL_DOWN /*167*/:
                data.enforceInterface(IActivityManager.descriptor);
                hang(data.readStrongBinder(), data.readInt() != 0);
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_ZOOM_IN /*168*/:
                data.enforceInterface(IActivityManager.descriptor);
                IActivityContainer activityContainer = createActivityContainer(data.readStrongBinder(), IActivityContainerCallback.Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                if (activityContainer != null) {
                    reply.writeInt(1);
                    reply.writeStrongBinder(activityContainer.asBinder());
                } else {
                    reply.writeInt(0);
                }
                return true;
            case KeyEvent.KEYCODE_ZOOM_OUT /*169*/:
                data.enforceInterface(IActivityManager.descriptor);
                moveTaskToStack(data.readInt(), data.readInt(), data.readInt() != 0);
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_TV /*170*/:
                data.enforceInterface(IActivityManager.descriptor);
                int stackId = data.readInt();
                float weight = data.readFloat();
                resizeStack(stackId, (Rect) Rect.CREATOR.createFromParcel(data));
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_WINDOW /*171*/:
                data.enforceInterface(IActivityManager.descriptor);
                List<StackInfo> list7 = getAllStackInfos();
                reply.writeNoException();
                reply.writeTypedList(list7);
                return true;
            case KeyEvent.KEYCODE_GUIDE /*172*/:
                data.enforceInterface(IActivityManager.descriptor);
                setFocusedStack(data.readInt());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_DVR /*173*/:
                data.enforceInterface(IActivityManager.descriptor);
                StackInfo info2 = getStackInfo(data.readInt());
                reply.writeNoException();
                if (info2 != null) {
                    reply.writeInt(1);
                    info2.writeToParcel(reply, 0);
                } else {
                    reply.writeInt(0);
                }
                return true;
            case KeyEvent.KEYCODE_BOOKMARK /*174*/:
                data.enforceInterface(IActivityManager.descriptor);
                converted = convertFromTranslucent(data.readStrongBinder());
                reply.writeNoException();
                reply.writeInt(converted ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_CAPTIONS /*175*/:
                data.enforceInterface(IActivityManager.descriptor);
                token = data.readStrongBinder();
                if (data.readInt() == 0) {
                    bundle = null;
                } else {
                    bundle = data.readBundle();
                }
                if (bundle == null) {
                    options = null;
                } else {
                    activityOptions = new ActivityOptions(bundle);
                }
                converted = convertToTranslucent(token, options);
                reply.writeNoException();
                reply.writeInt(converted ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_SETTINGS /*176*/:
                data.enforceInterface(IActivityManager.descriptor);
                notifyActivityDrawn(data.readStrongBinder());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_TV_POWER /*177*/:
                data.enforceInterface(IActivityManager.descriptor);
                reportActivityFullyDrawn(data.readStrongBinder());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_TV_INPUT /*178*/:
                data.enforceInterface(IActivityManager.descriptor);
                restart();
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_STB_POWER /*179*/:
                data.enforceInterface(IActivityManager.descriptor);
                performIdleMaintenance();
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_STB_INPUT /*180*/:
                data.enforceInterface(IActivityManager.descriptor);
                takePersistableUriPermission((Uri) Uri.CREATOR.createFromParcel(data), data.readInt(), data.readInt());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_AVR_POWER /*181*/:
                data.enforceInterface(IActivityManager.descriptor);
                releasePersistableUriPermission((Uri) Uri.CREATOR.createFromParcel(data), data.readInt(), data.readInt());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_AVR_INPUT /*182*/:
                data.enforceInterface(IActivityManager.descriptor);
                ParceledListSlice<UriPermission> perms = getPersistedUriPermissions(data.readString(), data.readInt() != 0);
                reply.writeNoException();
                perms.writeToParcel(reply, 1);
                return true;
            case KeyEvent.KEYCODE_PROG_RED /*183*/:
                data.enforceInterface(IActivityManager.descriptor);
                appNotRespondingViaProvider(data.readStrongBinder());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_PROG_GREEN /*184*/:
                data.enforceInterface(IActivityManager.descriptor);
                IBinder homeActivityToken = getHomeActivityToken();
                reply.writeNoException();
                reply.writeStrongBinder(homeActivityToken);
                return true;
            case KeyEvent.KEYCODE_PROG_YELLOW /*185*/:
                data.enforceInterface(IActivityManager.descriptor);
                int displayId = getActivityDisplayId(data.readStrongBinder());
                reply.writeNoException();
                reply.writeInt(displayId);
                return true;
            case KeyEvent.KEYCODE_PROG_BLUE /*186*/:
                data.enforceInterface(IActivityManager.descriptor);
                deleteActivityContainer(IActivityContainer.Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_ZENKAKU_HANKAKU /*211*/:
                data.enforceInterface(IActivityManager.descriptor);
                String tag = getTagForIntentSender(IIntentSender.Stub.asInterface(data.readStrongBinder()), data.readString());
                reply.writeNoException();
                reply.writeString(tag);
                return true;
            case KeyEvent.KEYCODE_EISU /*212*/:
                data.enforceInterface(IActivityManager.descriptor);
                result2 = startUserInBackground(data.readInt());
                reply.writeNoException();
                reply.writeInt(result2 ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_MUHENKAN /*213*/:
                data.enforceInterface(IActivityManager.descriptor);
                boolean isInHomeStack = isInHomeStack(data.readInt());
                reply.writeNoException();
                reply.writeInt(isInHomeStack ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_HENKAN /*214*/:
                data.enforceInterface(IActivityManager.descriptor);
                startLockTaskMode(data.readInt());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_KATAKANA_HIRAGANA /*215*/:
                data.enforceInterface(IActivityManager.descriptor);
                startLockTaskMode(data.readStrongBinder());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_YEN /*216*/:
                data.enforceInterface(IActivityManager.descriptor);
                stopLockTaskMode();
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_RO /*217*/:
                data.enforceInterface(IActivityManager.descriptor);
                boolean isInLockTaskMode = isInLockTaskMode();
                reply.writeNoException();
                reply.writeInt(isInLockTaskMode ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_KANA /*218*/:
                data.enforceInterface(IActivityManager.descriptor);
                setTaskDescription(data.readStrongBinder(), (TaskDescription) TaskDescription.CREATOR.createFromParcel(data));
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_ASSIST /*219*/:
                data.enforceInterface(IActivityManager.descriptor);
                result = startVoiceActivity(data.readString(), data.readInt(), data.readInt(), (Intent) Intent.CREATOR.createFromParcel(data), data.readString(), IVoiceInteractionSession.Stub.asInterface(data.readStrongBinder()), IVoiceInteractor.Stub.asInterface(data.readStrongBinder()), data.readInt(), data.readInt() != 0 ? (ProfilerInfo) ProfilerInfo.CREATOR.createFromParcel(data) : null, data.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(data) : null, data.readInt());
                reply.writeNoException();
                reply.writeInt(result);
                return true;
            case KeyEvent.KEYCODE_BRIGHTNESS_DOWN /*220*/:
                data.enforceInterface(IActivityManager.descriptor);
                options = getActivityOptions(data.readStrongBinder());
                reply.writeNoException();
                reply.writeBundle(options == null ? null : options.toBundle());
                return true;
            case KeyEvent.KEYCODE_BRIGHTNESS_UP /*221*/:
                data.enforceInterface(IActivityManager.descriptor);
                List<IAppTask> list8 = getAppTasks(data.readString());
                reply.writeNoException();
                N = list8 != null ? list8.size() : -1;
                reply.writeInt(N);
                for (i = 0; i < N; i++) {
                    reply.writeStrongBinder(((IAppTask) list8.get(i)).asBinder());
                }
                return true;
            case KeyEvent.KEYCODE_MEDIA_AUDIO_TRACK /*222*/:
                data.enforceInterface(IActivityManager.descriptor);
                startLockTaskModeOnCurrent();
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_SLEEP /*223*/:
                data.enforceInterface(IActivityManager.descriptor);
                stopLockTaskModeOnCurrent();
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_WAKEUP /*224*/:
                data.enforceInterface(IActivityManager.descriptor);
                finishVoiceTask(IVoiceInteractionSession.Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_PAIRING /*225*/:
                data.enforceInterface(IActivityManager.descriptor);
                boolean isTopOfTask = isTopOfTask(data.readStrongBinder());
                reply.writeNoException();
                reply.writeInt(isTopOfTask ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_MEDIA_TOP_MENU /*226*/:
                data.enforceInterface(IActivityManager.descriptor);
                success = requestVisibleBehind(data.readStrongBinder(), data.readInt() > 0);
                reply.writeNoException();
                reply.writeInt(success ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_11 /*227*/:
                data.enforceInterface(IActivityManager.descriptor);
                boolean enabled = isBackgroundVisibleBehind(data.readStrongBinder());
                reply.writeNoException();
                reply.writeInt(enabled ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_12 /*228*/:
                data.enforceInterface(IActivityManager.descriptor);
                backgroundResourcesReleased(data.readStrongBinder());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_LAST_CHANNEL /*229*/:
                data.enforceInterface(IActivityManager.descriptor);
                notifyLaunchTaskBehindComplete(data.readStrongBinder());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_TV_DATA_SERVICE /*230*/:
                data.enforceInterface(IActivityManager.descriptor);
                result = startActivityFromRecents(data.readInt(), data.readInt() == 0 ? null : (Bundle) Bundle.CREATOR.createFromParcel(data));
                reply.writeNoException();
                reply.writeInt(result);
                return true;
            case KeyEvent.KEYCODE_VOICE_ASSIST /*231*/:
                data.enforceInterface(IActivityManager.descriptor);
                notifyEnterAnimationComplete(data.readStrongBinder());
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_TV_RADIO_SERVICE /*232*/:
                data.enforceInterface(IActivityManager.descriptor);
                keyguardWaitingForActivityDrawn();
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_TV_TELETEXT /*233*/:
                data.enforceInterface(IActivityManager.descriptor);
                result = startActivityAsCaller(ApplicationThreadNative.asInterface(data.readStrongBinder()), data.readString(), (Intent) Intent.CREATOR.createFromParcel(data), data.readString(), data.readStrongBinder(), data.readString(), data.readInt(), data.readInt(), data.readInt() != 0 ? (ProfilerInfo) ProfilerInfo.CREATOR.createFromParcel(data) : null, data.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(data) : null, data.readInt());
                reply.writeNoException();
                reply.writeInt(result);
                return true;
            case KeyEvent.KEYCODE_TV_NUMBER_ENTRY /*234*/:
                data.enforceInterface(IActivityManager.descriptor);
                res2 = addAppTask(data.readStrongBinder(), (Intent) Intent.CREATOR.createFromParcel(data), (TaskDescription) TaskDescription.CREATOR.createFromParcel(data), (Bitmap) Bitmap.CREATOR.createFromParcel(data));
                reply.writeNoException();
                reply.writeInt(res2);
                return true;
            case KeyEvent.KEYCODE_TV_TERRESTRIAL_ANALOG /*235*/:
                data.enforceInterface(IActivityManager.descriptor);
                Point size = getAppTaskThumbnailSize();
                reply.writeNoException();
                size.writeToParcel(reply, 0);
                return true;
            case KeyEvent.KEYCODE_TV_TERRESTRIAL_DIGITAL /*236*/:
                data.enforceInterface(IActivityManager.descriptor);
                res = releaseActivityInstance(data.readStrongBinder());
                reply.writeNoException();
                reply.writeInt(res ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_TV_SATELLITE /*237*/:
                data.enforceInterface(IActivityManager.descriptor);
                releaseSomeActivities(ApplicationThreadNative.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_TV_SATELLITE_BS /*238*/:
                data.enforceInterface(IActivityManager.descriptor);
                bootAnimationComplete();
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_TV_SATELLITE_CS /*239*/:
                data.enforceInterface(IActivityManager.descriptor);
                Bitmap icon = getTaskDescriptionIcon(data.readString());
                reply.writeNoException();
                if (icon == null) {
                    reply.writeInt(0);
                } else {
                    reply.writeInt(1);
                    icon.writeToParcel(reply, 0);
                }
                return true;
            case LayoutParams.SOFT_INPUT_MASK_ADJUST /*240*/:
                data.enforceInterface(IActivityManager.descriptor);
                res = launchAssistIntent((Intent) Intent.CREATOR.createFromParcel(data), data.readInt(), data.readString(), data.readInt());
                reply.writeNoException();
                reply.writeInt(res ? 1 : 0);
                return true;
            case KeyEvent.KEYCODE_TV_NETWORK /*241*/:
                data.enforceInterface(IActivityManager.descriptor);
                if (data.readInt() == 0) {
                    bundle = null;
                } else {
                    bundle = data.readBundle();
                }
                if (bundle == null) {
                    options = null;
                } else {
                    activityOptions = new ActivityOptions(bundle);
                }
                startInPlaceAnimationOnFrontMostApplication(options);
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_TV_ANTENNA_CABLE /*242*/:
                data.enforceInterface(IActivityManager.descriptor);
                res2 = checkPermissionWithToken(data.readString(), data.readInt(), data.readInt(), data.readStrongBinder());
                reply.writeNoException();
                reply.writeInt(res2);
                return true;
            case KeyEvent.KEYCODE_TV_INPUT_HDMI_1 /*243*/:
                data.enforceInterface(IActivityManager.descriptor);
                registerTaskStackListener(ITaskStackListener.Stub.asInterface(data.readStrongBinder()));
                reply.writeNoException();
                return true;
            case KeyEvent.KEYCODE_TV_INPUT_HDMI_2 /*244*/:
                data.enforceInterface(IActivityManager.descriptor);
                systemBackupRestored();
                reply.writeNoException();
                return true;
            default:
                return super.onTransact(code, data, reply, flags);
        }
    }

    public IBinder asBinder() {
        return this;
    }
}
