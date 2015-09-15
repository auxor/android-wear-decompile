package android.os;

import android.animation.ValueAnimator;
import android.app.ActivityManagerNative;
import android.app.ActivityThread;
import android.app.ApplicationErrorReport.CrashInfo;
import android.app.IActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.LinkQualityInfo;
import android.opengl.GLES20;
import android.os.MessageQueue.IdleHandler;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Printer;
import android.util.Singleton;
import android.util.Slog;
import android.view.IWindowManager;
import android.view.IWindowManager.Stub;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import com.android.internal.os.RuntimeInit;
import com.android.internal.util.FastPrintWriter;
import dalvik.system.BlockGuard;
import dalvik.system.BlockGuard.BlockGuardPolicyException;
import dalvik.system.BlockGuard.Policy;
import dalvik.system.CloseGuard;
import dalvik.system.CloseGuard.Reporter;
import dalvik.system.VMDebug;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

public final class StrictMode {
    private static final int ALL_THREAD_DETECT_BITS = 15;
    private static final int ALL_VM_DETECT_BITS = 32256;
    public static final int DETECT_CUSTOM = 8;
    public static final int DETECT_DISK_READ = 2;
    public static final int DETECT_DISK_WRITE = 1;
    public static final int DETECT_NETWORK = 4;
    public static final int DETECT_VM_ACTIVITY_LEAKS = 2048;
    public static final int DETECT_VM_CLOSABLE_LEAKS = 1024;
    public static final int DETECT_VM_CURSOR_LEAKS = 512;
    private static final int DETECT_VM_FILE_URI_EXPOSURE = 16384;
    private static final int DETECT_VM_INSTANCE_LEAKS = 4096;
    public static final int DETECT_VM_REGISTRATION_LEAKS = 8192;
    public static final String DISABLE_PROPERTY = "persist.sys.strictmode.disable";
    private static final HashMap<Class, Integer> EMPTY_CLASS_LIMIT_MAP;
    private static final boolean IS_ENG_BUILD;
    private static final boolean IS_USER_BUILD;
    private static final boolean LOG_V;
    private static final int MAX_OFFENSES_PER_LOOP = 10;
    private static final int MAX_SPAN_TAGS = 20;
    private static final long MIN_DIALOG_INTERVAL_MS = 30000;
    private static final long MIN_LOG_INTERVAL_MS = 1000;
    private static final Span NO_OP_SPAN;
    public static final int PENALTY_DEATH = 64;
    public static final int PENALTY_DEATH_ON_NETWORK = 512;
    public static final int PENALTY_DIALOG = 32;
    public static final int PENALTY_DROPBOX = 128;
    public static final int PENALTY_FLASH = 2048;
    public static final int PENALTY_GATHER = 256;
    public static final int PENALTY_LOG = 16;
    private static final String TAG = "StrictMode";
    private static final int THREAD_PENALTY_MASK = 3056;
    public static final String VISUAL_PROPERTY = "persist.sys.strictmode.visual";
    private static final int VM_PENALTY_MASK = 208;
    private static final ThreadLocal<ArrayList<ViolationInfo>> gatheredViolations;
    private static final AtomicInteger sDropboxCallsInFlight;
    private static final HashMap<Class, Integer> sExpectedActivityInstanceCount;
    private static boolean sIsIdlerRegistered;
    private static long sLastInstanceCountCheckMillis;
    private static final HashMap<Integer, Long> sLastVmViolationTime;
    private static final IdleHandler sProcessIdleHandler;
    private static final ThreadLocal<ThreadSpanState> sThisThreadSpanState;
    private static volatile VmPolicy sVmPolicy;
    private static volatile int sVmPolicyMask;
    private static Singleton<IWindowManager> sWindowManager;
    private static final ThreadLocal<AndroidBlockGuardPolicy> threadAndroidPolicy;
    private static final ThreadLocal<Handler> threadHandler;
    private static final ThreadLocal<ArrayList<ViolationInfo>> violationsBeingTimed;

    /* renamed from: android.os.StrictMode.5 */
    static class AnonymousClass5 extends Thread {
        final /* synthetic */ ViolationInfo val$info;
        final /* synthetic */ int val$violationMaskSubset;

        AnonymousClass5(String x0, int i, ViolationInfo violationInfo) {
            this.val$violationMaskSubset = i;
            this.val$info = violationInfo;
            super(x0);
        }

        public void run() {
            Process.setThreadPriority(StrictMode.MAX_OFFENSES_PER_LOOP);
            try {
                IActivityManager am = ActivityManagerNative.getDefault();
                if (am == null) {
                    Log.d(StrictMode.TAG, "No activity manager; failed to Dropbox violation.");
                } else {
                    am.handleApplicationStrictModeViolation(RuntimeInit.getApplicationObject(), this.val$violationMaskSubset, this.val$info);
                }
            } catch (RemoteException e) {
                Log.e(StrictMode.TAG, "RemoteException handling StrictMode violation", e);
            }
            int outstanding = StrictMode.sDropboxCallsInFlight.decrementAndGet();
            if (StrictMode.LOG_V) {
                Log.d(StrictMode.TAG, "Dropbox complete; in-flight=" + outstanding);
            }
        }
    }

    public static class Span {
        private final ThreadSpanState mContainerState;
        private long mCreateMillis;
        private String mName;
        private Span mNext;
        private Span mPrev;

        Span(ThreadSpanState threadState) {
            this.mContainerState = threadState;
        }

        protected Span() {
            this.mContainerState = null;
        }

        public void finish() {
            ThreadSpanState state = this.mContainerState;
            synchronized (state) {
                if (this.mName == null) {
                    return;
                }
                if (this.mPrev != null) {
                    this.mPrev.mNext = this.mNext;
                }
                if (this.mNext != null) {
                    this.mNext.mPrev = this.mPrev;
                }
                if (state.mActiveHead == this) {
                    state.mActiveHead = this.mNext;
                }
                state.mActiveSize--;
                if (StrictMode.LOG_V) {
                    Log.d(StrictMode.TAG, "Span finished=" + this.mName + "; size=" + state.mActiveSize);
                }
                this.mCreateMillis = -1;
                this.mName = null;
                this.mPrev = null;
                this.mNext = null;
                if (state.mFreeListSize < 5) {
                    this.mNext = state.mFreeListHead;
                    state.mFreeListHead = this;
                    state.mFreeListSize += StrictMode.DETECT_DISK_WRITE;
                }
            }
        }
    }

    private static class AndroidBlockGuardPolicy implements Policy {
        private ArrayMap<Integer, Long> mLastViolationTime;
        private int mPolicyMask;

        /* renamed from: android.os.StrictMode.AndroidBlockGuardPolicy.1 */
        class AnonymousClass1 implements Runnable {
            final /* synthetic */ ArrayList val$records;
            final /* synthetic */ IWindowManager val$windowManager;

            AnonymousClass1(IWindowManager iWindowManager, ArrayList arrayList) {
                this.val$windowManager = iWindowManager;
                this.val$records = arrayList;
            }

            public void run() {
                long loopFinishTime = SystemClock.uptimeMillis();
                if (this.val$windowManager != null) {
                    try {
                        this.val$windowManager.showStrictModeViolation(StrictMode.LOG_V);
                    } catch (RemoteException e) {
                    }
                }
                for (int n = 0; n < this.val$records.size(); n += StrictMode.DETECT_DISK_WRITE) {
                    ViolationInfo v = (ViolationInfo) this.val$records.get(n);
                    v.violationNumThisLoop = n + StrictMode.DETECT_DISK_WRITE;
                    v.durationMillis = (int) (loopFinishTime - v.violationUptimeMillis);
                    AndroidBlockGuardPolicy.this.handleViolation(v);
                }
                this.val$records.clear();
            }
        }

        public AndroidBlockGuardPolicy(int policyMask) {
            this.mPolicyMask = policyMask;
        }

        public String toString() {
            return "AndroidBlockGuardPolicy; mPolicyMask=" + this.mPolicyMask;
        }

        public int getPolicyMask() {
            return this.mPolicyMask;
        }

        public void onWriteToDisk() {
            if ((this.mPolicyMask & StrictMode.DETECT_DISK_WRITE) != 0 && !StrictMode.tooManyViolationsThisLoop()) {
                BlockGuardPolicyException e = new StrictModeDiskWriteViolation(this.mPolicyMask);
                e.fillInStackTrace();
                startHandlingViolationException(e);
            }
        }

        void onCustomSlowCall(String name) {
            if ((this.mPolicyMask & StrictMode.DETECT_CUSTOM) != 0 && !StrictMode.tooManyViolationsThisLoop()) {
                BlockGuardPolicyException e = new StrictModeCustomViolation(this.mPolicyMask, name);
                e.fillInStackTrace();
                startHandlingViolationException(e);
            }
        }

        public void onReadFromDisk() {
            if ((this.mPolicyMask & StrictMode.DETECT_DISK_READ) != 0 && !StrictMode.tooManyViolationsThisLoop()) {
                BlockGuardPolicyException e = new StrictModeDiskReadViolation(this.mPolicyMask);
                e.fillInStackTrace();
                startHandlingViolationException(e);
            }
        }

        public void onNetwork() {
            if ((this.mPolicyMask & StrictMode.DETECT_NETWORK) != 0) {
                if ((this.mPolicyMask & StrictMode.PENALTY_DEATH_ON_NETWORK) != 0) {
                    throw new NetworkOnMainThreadException();
                } else if (!StrictMode.tooManyViolationsThisLoop()) {
                    BlockGuardPolicyException e = new StrictModeNetworkViolation(this.mPolicyMask);
                    e.fillInStackTrace();
                    startHandlingViolationException(e);
                }
            }
        }

        public void setPolicyMask(int policyMask) {
            this.mPolicyMask = policyMask;
        }

        void startHandlingViolationException(BlockGuardPolicyException e) {
            ViolationInfo info = new ViolationInfo((Throwable) e, e.getPolicy());
            info.violationUptimeMillis = SystemClock.uptimeMillis();
            handleViolationWithTimingAttempt(info);
        }

        void handleViolationWithTimingAttempt(ViolationInfo info) {
            if (Looper.myLooper() == null || (info.policy & StrictMode.THREAD_PENALTY_MASK) == StrictMode.PENALTY_DEATH) {
                info.durationMillis = -1;
                handleViolation(info);
                return;
            }
            ArrayList<ViolationInfo> records = (ArrayList) StrictMode.violationsBeingTimed.get();
            if (records.size() < StrictMode.MAX_OFFENSES_PER_LOOP) {
                records.add(info);
                if (records.size() <= StrictMode.DETECT_DISK_WRITE) {
                    IWindowManager windowManager = (info.policy & StrictMode.PENALTY_FLASH) != 0 ? (IWindowManager) StrictMode.sWindowManager.get() : null;
                    if (windowManager != null) {
                        try {
                            windowManager.showStrictModeViolation(true);
                        } catch (RemoteException e) {
                        }
                    }
                    ((Handler) StrictMode.threadHandler.get()).postAtFrontOfQueue(new AnonymousClass1(windowManager, records));
                }
            }
        }

        void handleViolation(ViolationInfo info) {
            if (!(info == null || info.crashInfo == null)) {
                if (info.crashInfo.stackTrace != null) {
                    if (StrictMode.LOG_V) {
                        Log.d(StrictMode.TAG, "handleViolation; policy=" + info.policy);
                    }
                    if ((info.policy & StrictMode.PENALTY_GATHER) != 0) {
                        ArrayList<ViolationInfo> violations = (ArrayList) StrictMode.gatheredViolations.get();
                        if (violations == null) {
                            ArrayList<ViolationInfo> arrayList = new ArrayList(StrictMode.DETECT_DISK_WRITE);
                            StrictMode.gatheredViolations.set(arrayList);
                        } else if (violations.size() >= 5) {
                            return;
                        }
                        Iterator i$ = violations.iterator();
                        while (i$.hasNext()) {
                            if (info.crashInfo.stackTrace.equals(((ViolationInfo) i$.next()).crashInfo.stackTrace)) {
                                return;
                            }
                        }
                        violations.add(info);
                        return;
                    }
                    int i;
                    Integer crashFingerprint = Integer.valueOf(info.hashCode());
                    long lastViolationTime = 0;
                    if (this.mLastViolationTime != null) {
                        Long vtime = (Long) this.mLastViolationTime.get(crashFingerprint);
                        if (vtime != null) {
                            lastViolationTime = vtime.longValue();
                        }
                    } else {
                        this.mLastViolationTime = new ArrayMap(StrictMode.DETECT_DISK_WRITE);
                    }
                    long now = SystemClock.uptimeMillis();
                    this.mLastViolationTime.put(crashFingerprint, Long.valueOf(now));
                    long timeSinceLastViolationMillis = lastViolationTime == 0 ? LinkQualityInfo.UNKNOWN_LONG : now - lastViolationTime;
                    if ((info.policy & StrictMode.PENALTY_LOG) != 0 && timeSinceLastViolationMillis > StrictMode.MIN_LOG_INTERVAL_MS) {
                        i = info.durationMillis;
                        if (r0 != -1) {
                            Log.d(StrictMode.TAG, "StrictMode policy violation; ~duration=" + info.durationMillis + " ms: " + info.crashInfo.stackTrace);
                        } else {
                            Log.d(StrictMode.TAG, "StrictMode policy violation: " + info.crashInfo.stackTrace);
                        }
                    }
                    int violationMaskSubset = 0;
                    if ((info.policy & StrictMode.PENALTY_DIALOG) != 0 && timeSinceLastViolationMillis > StrictMode.MIN_DIALOG_INTERVAL_MS) {
                        violationMaskSubset = 0 | StrictMode.PENALTY_DIALOG;
                    }
                    if ((info.policy & StrictMode.PENALTY_DROPBOX) != 0 && lastViolationTime == 0) {
                        violationMaskSubset |= StrictMode.PENALTY_DROPBOX;
                    }
                    if (violationMaskSubset != 0) {
                        violationMaskSubset |= StrictMode.parseViolationFromMessage(info.crashInfo.exceptionMessage);
                        int savedPolicyMask = StrictMode.getThreadPolicyMask();
                        i = info.policy & StrictMode.THREAD_PENALTY_MASK;
                        if (r0 == StrictMode.PENALTY_DROPBOX ? true : StrictMode.LOG_V) {
                            StrictMode.dropboxViolationAsync(violationMaskSubset, info);
                            return;
                        }
                        try {
                            StrictMode.setThreadPolicyMask(0);
                            ActivityManagerNative.getDefault().handleApplicationStrictModeViolation(RuntimeInit.getApplicationObject(), violationMaskSubset, info);
                        } catch (RemoteException e) {
                            Log.e(StrictMode.TAG, "RemoteException trying to handle StrictMode violation", e);
                        } finally {
                            StrictMode.setThreadPolicyMask(savedPolicyMask);
                        }
                    }
                    if ((info.policy & StrictMode.PENALTY_DEATH) != 0) {
                        StrictMode.executeDeathPenalty(info);
                        return;
                    }
                    return;
                }
            }
            Log.wtf(StrictMode.TAG, "unexpected null stacktrace");
        }
    }

    private static class AndroidCloseGuardReporter implements Reporter {
        private AndroidCloseGuardReporter() {
        }

        public void report(String message, Throwable allocationSite) {
            StrictMode.onVmPolicyViolation(message, allocationSite);
        }
    }

    private static class InstanceCountViolation extends Throwable {
        private static final StackTraceElement[] FAKE_STACK;
        final Class mClass;
        final long mInstances;
        final int mLimit;

        static {
            StackTraceElement[] stackTraceElementArr = new StackTraceElement[StrictMode.DETECT_DISK_WRITE];
            stackTraceElementArr[0] = new StackTraceElement("android.os.StrictMode", "setClassInstanceLimit", "StrictMode.java", StrictMode.DETECT_DISK_WRITE);
            FAKE_STACK = stackTraceElementArr;
        }

        public InstanceCountViolation(Class klass, long instances, int limit) {
            super(klass.toString() + "; instances=" + instances + "; limit=" + limit);
            setStackTrace(FAKE_STACK);
            this.mClass = klass;
            this.mInstances = instances;
            this.mLimit = limit;
        }
    }

    private static final class InstanceTracker {
        private static final HashMap<Class<?>, Integer> sInstanceCounts;
        private final Class<?> mKlass;

        static {
            sInstanceCounts = new HashMap();
        }

        public InstanceTracker(Object instance) {
            this.mKlass = instance.getClass();
            synchronized (sInstanceCounts) {
                Integer value = (Integer) sInstanceCounts.get(this.mKlass);
                sInstanceCounts.put(this.mKlass, Integer.valueOf(value != null ? value.intValue() + StrictMode.DETECT_DISK_WRITE : StrictMode.DETECT_DISK_WRITE));
            }
        }

        protected void finalize() throws Throwable {
            try {
                synchronized (sInstanceCounts) {
                    Integer value = (Integer) sInstanceCounts.get(this.mKlass);
                    if (value != null) {
                        int newValue = value.intValue() - 1;
                        if (newValue > 0) {
                            sInstanceCounts.put(this.mKlass, Integer.valueOf(newValue));
                        } else {
                            sInstanceCounts.remove(this.mKlass);
                        }
                    }
                }
            } finally {
                super.finalize();
            }
        }

        public static int getInstanceCount(Class<?> klass) {
            int intValue;
            synchronized (sInstanceCounts) {
                Integer value = (Integer) sInstanceCounts.get(klass);
                intValue = value != null ? value.intValue() : 0;
            }
            return intValue;
        }
    }

    private static class LogStackTrace extends Exception {
        private LogStackTrace() {
        }
    }

    public static class StrictModeViolation extends BlockGuardPolicyException {
        public StrictModeViolation(int policyState, int policyViolated, String message) {
            super(policyState, policyViolated, message);
        }
    }

    private static class StrictModeCustomViolation extends StrictModeViolation {
        public StrictModeCustomViolation(int policyMask, String name) {
            super(policyMask, StrictMode.DETECT_CUSTOM, name);
        }
    }

    private static class StrictModeDiskReadViolation extends StrictModeViolation {
        public StrictModeDiskReadViolation(int policyMask) {
            super(policyMask, StrictMode.DETECT_DISK_READ, null);
        }
    }

    private static class StrictModeDiskWriteViolation extends StrictModeViolation {
        public StrictModeDiskWriteViolation(int policyMask) {
            super(policyMask, StrictMode.DETECT_DISK_WRITE, null);
        }
    }

    public static class StrictModeNetworkViolation extends StrictModeViolation {
        public StrictModeNetworkViolation(int policyMask) {
            super(policyMask, StrictMode.DETECT_NETWORK, null);
        }
    }

    public static final class ThreadPolicy {
        public static final ThreadPolicy LAX;
        final int mask;

        public static final class Builder {
            private int mMask;

            public Builder() {
                this.mMask = 0;
                this.mMask = 0;
            }

            public Builder(ThreadPolicy policy) {
                this.mMask = 0;
                this.mMask = policy.mask;
            }

            public Builder detectAll() {
                return enable(StrictMode.ALL_THREAD_DETECT_BITS);
            }

            public Builder permitAll() {
                return disable(StrictMode.ALL_THREAD_DETECT_BITS);
            }

            public Builder detectNetwork() {
                return enable(StrictMode.DETECT_NETWORK);
            }

            public Builder permitNetwork() {
                return disable(StrictMode.DETECT_NETWORK);
            }

            public Builder detectDiskReads() {
                return enable(StrictMode.DETECT_DISK_READ);
            }

            public Builder permitDiskReads() {
                return disable(StrictMode.DETECT_DISK_READ);
            }

            public Builder detectCustomSlowCalls() {
                return enable(StrictMode.DETECT_CUSTOM);
            }

            public Builder permitCustomSlowCalls() {
                return disable(StrictMode.DETECT_CUSTOM);
            }

            public Builder detectDiskWrites() {
                return enable(StrictMode.DETECT_DISK_WRITE);
            }

            public Builder permitDiskWrites() {
                return disable(StrictMode.DETECT_DISK_WRITE);
            }

            public Builder penaltyDialog() {
                return enable(StrictMode.PENALTY_DIALOG);
            }

            public Builder penaltyDeath() {
                return enable(StrictMode.PENALTY_DEATH);
            }

            public Builder penaltyDeathOnNetwork() {
                return enable(StrictMode.PENALTY_DEATH_ON_NETWORK);
            }

            public Builder penaltyFlashScreen() {
                return enable(StrictMode.PENALTY_FLASH);
            }

            public Builder penaltyLog() {
                return enable(StrictMode.PENALTY_LOG);
            }

            public Builder penaltyDropBox() {
                return enable(StrictMode.PENALTY_DROPBOX);
            }

            private Builder enable(int bit) {
                this.mMask |= bit;
                return this;
            }

            private Builder disable(int bit) {
                this.mMask &= bit ^ -1;
                return this;
            }

            public ThreadPolicy build() {
                if (this.mMask != 0 && (this.mMask & LayoutParams.SOFT_INPUT_MASK_ADJUST) == 0) {
                    penaltyLog();
                }
                return new ThreadPolicy(null);
            }
        }

        static {
            LAX = new ThreadPolicy(0);
        }

        private ThreadPolicy(int mask) {
            this.mask = mask;
        }

        public String toString() {
            return "[StrictMode.ThreadPolicy; mask=" + this.mask + "]";
        }
    }

    private static class ThreadSpanState {
        public Span mActiveHead;
        public int mActiveSize;
        public Span mFreeListHead;
        public int mFreeListSize;

        private ThreadSpanState() {
        }
    }

    public static class ViolationInfo {
        public String broadcastIntentAction;
        public final CrashInfo crashInfo;
        public int durationMillis;
        public int numAnimationsRunning;
        public long numInstances;
        public final int policy;
        public String[] tags;
        public int violationNumThisLoop;
        public long violationUptimeMillis;

        public ViolationInfo() {
            this.durationMillis = -1;
            this.numAnimationsRunning = 0;
            this.numInstances = -1;
            this.crashInfo = null;
            this.policy = 0;
        }

        public ViolationInfo(Throwable tr, int policy) {
            this.durationMillis = -1;
            this.numAnimationsRunning = 0;
            this.numInstances = -1;
            this.crashInfo = new CrashInfo(tr);
            this.violationUptimeMillis = SystemClock.uptimeMillis();
            this.policy = policy;
            this.numAnimationsRunning = ValueAnimator.getCurrentAnimationsCount();
            Intent broadcastIntent = ActivityThread.getIntentBeingBroadcast();
            if (broadcastIntent != null) {
                this.broadcastIntentAction = broadcastIntent.getAction();
            }
            ThreadSpanState state = (ThreadSpanState) StrictMode.sThisThreadSpanState.get();
            if (tr instanceof InstanceCountViolation) {
                this.numInstances = ((InstanceCountViolation) tr).mInstances;
            }
            synchronized (state) {
                int spanActiveCount = state.mActiveSize;
                if (spanActiveCount > StrictMode.MAX_SPAN_TAGS) {
                    spanActiveCount = StrictMode.MAX_SPAN_TAGS;
                }
                if (spanActiveCount != 0) {
                    this.tags = new String[spanActiveCount];
                    int index = 0;
                    for (Span iter = state.mActiveHead; iter != null && index < spanActiveCount; iter = iter.mNext) {
                        this.tags[index] = iter.mName;
                        index += StrictMode.DETECT_DISK_WRITE;
                    }
                }
            }
        }

        public int hashCode() {
            int result = this.crashInfo.stackTrace.hashCode() + 629;
            if (this.numAnimationsRunning != 0) {
                result *= 37;
            }
            if (this.broadcastIntentAction != null) {
                result = (result * 37) + this.broadcastIntentAction.hashCode();
            }
            if (this.tags != null) {
                String[] arr$ = this.tags;
                for (int i$ = 0; i$ < arr$.length; i$ += StrictMode.DETECT_DISK_WRITE) {
                    result = (result * 37) + arr$[i$].hashCode();
                }
            }
            return result;
        }

        public ViolationInfo(Parcel in) {
            this(in, (boolean) StrictMode.LOG_V);
        }

        public ViolationInfo(Parcel in, boolean unsetGatheringBit) {
            this.durationMillis = -1;
            this.numAnimationsRunning = 0;
            this.numInstances = -1;
            this.crashInfo = new CrashInfo(in);
            int rawPolicy = in.readInt();
            if (unsetGatheringBit) {
                this.policy = rawPolicy & -257;
            } else {
                this.policy = rawPolicy;
            }
            this.durationMillis = in.readInt();
            this.violationNumThisLoop = in.readInt();
            this.numAnimationsRunning = in.readInt();
            this.violationUptimeMillis = in.readLong();
            this.numInstances = in.readLong();
            this.broadcastIntentAction = in.readString();
            this.tags = in.readStringArray();
        }

        public void writeToParcel(Parcel dest, int flags) {
            this.crashInfo.writeToParcel(dest, flags);
            int start = dest.dataPosition();
            dest.writeInt(this.policy);
            dest.writeInt(this.durationMillis);
            dest.writeInt(this.violationNumThisLoop);
            dest.writeInt(this.numAnimationsRunning);
            dest.writeLong(this.violationUptimeMillis);
            dest.writeLong(this.numInstances);
            dest.writeString(this.broadcastIntentAction);
            dest.writeStringArray(this.tags);
            if (dest.dataPosition() - start > GLES20.GL_TEXTURE_MAG_FILTER) {
                Slog.d(StrictMode.TAG, "VIO: policy=" + this.policy + " dur=" + this.durationMillis + " numLoop=" + this.violationNumThisLoop + " anim=" + this.numAnimationsRunning + " uptime=" + this.violationUptimeMillis + " numInst=" + this.numInstances);
                Slog.d(StrictMode.TAG, "VIO: action=" + this.broadcastIntentAction);
                Slog.d(StrictMode.TAG, "VIO: tags=" + Arrays.toString(this.tags));
                Slog.d(StrictMode.TAG, "VIO: TOTAL BYTES WRITTEN: " + (dest.dataPosition() - start));
            }
        }

        public void dump(Printer pw, String prefix) {
            this.crashInfo.dump(pw, prefix);
            pw.println(prefix + "policy: " + this.policy);
            if (this.durationMillis != -1) {
                pw.println(prefix + "durationMillis: " + this.durationMillis);
            }
            if (this.numInstances != -1) {
                pw.println(prefix + "numInstances: " + this.numInstances);
            }
            if (this.violationNumThisLoop != 0) {
                pw.println(prefix + "violationNumThisLoop: " + this.violationNumThisLoop);
            }
            if (this.numAnimationsRunning != 0) {
                pw.println(prefix + "numAnimationsRunning: " + this.numAnimationsRunning);
            }
            pw.println(prefix + "violationUptimeMillis: " + this.violationUptimeMillis);
            if (this.broadcastIntentAction != null) {
                pw.println(prefix + "broadcastIntentAction: " + this.broadcastIntentAction);
            }
            if (this.tags != null) {
                String[] arr$ = this.tags;
                int len$ = arr$.length;
                int i$ = 0;
                int index = 0;
                while (i$ < len$) {
                    int index2 = index + StrictMode.DETECT_DISK_WRITE;
                    pw.println(prefix + "tag[" + index + "]: " + arr$[i$]);
                    i$ += StrictMode.DETECT_DISK_WRITE;
                    index = index2;
                }
            }
        }
    }

    public static final class VmPolicy {
        public static final VmPolicy LAX;
        final HashMap<Class, Integer> classInstanceLimit;
        final int mask;

        public static final class Builder {
            private HashMap<Class, Integer> mClassInstanceLimit;
            private boolean mClassInstanceLimitNeedCow;
            private int mMask;

            public Builder() {
                this.mClassInstanceLimitNeedCow = StrictMode.LOG_V;
                this.mMask = 0;
            }

            public Builder(VmPolicy base) {
                this.mClassInstanceLimitNeedCow = StrictMode.LOG_V;
                this.mMask = base.mask;
                this.mClassInstanceLimitNeedCow = true;
                this.mClassInstanceLimit = base.classInstanceLimit;
            }

            public Builder setClassInstanceLimit(Class klass, int instanceLimit) {
                if (klass == null) {
                    throw new NullPointerException("klass == null");
                }
                if (!this.mClassInstanceLimitNeedCow) {
                    if (this.mClassInstanceLimit == null) {
                        this.mClassInstanceLimit = new HashMap();
                    }
                    this.mMask |= StrictMode.DETECT_VM_INSTANCE_LEAKS;
                    this.mClassInstanceLimit.put(klass, Integer.valueOf(instanceLimit));
                } else if (!(this.mClassInstanceLimit.containsKey(klass) && ((Integer) this.mClassInstanceLimit.get(klass)).intValue() == instanceLimit)) {
                    this.mClassInstanceLimitNeedCow = StrictMode.LOG_V;
                    this.mClassInstanceLimit = (HashMap) this.mClassInstanceLimit.clone();
                    this.mMask |= StrictMode.DETECT_VM_INSTANCE_LEAKS;
                    this.mClassInstanceLimit.put(klass, Integer.valueOf(instanceLimit));
                }
                return this;
            }

            public Builder detectActivityLeaks() {
                return enable(StrictMode.PENALTY_FLASH);
            }

            public Builder detectAll() {
                return enable(28160);
            }

            public Builder detectLeakedSqlLiteObjects() {
                return enable(StrictMode.PENALTY_DEATH_ON_NETWORK);
            }

            public Builder detectLeakedClosableObjects() {
                return enable(StrictMode.DETECT_VM_CLOSABLE_LEAKS);
            }

            public Builder detectLeakedRegistrationObjects() {
                return enable(StrictMode.DETECT_VM_REGISTRATION_LEAKS);
            }

            public Builder detectFileUriExposure() {
                return enable(StrictMode.DETECT_VM_FILE_URI_EXPOSURE);
            }

            public Builder penaltyDeath() {
                return enable(StrictMode.PENALTY_DEATH);
            }

            public Builder penaltyLog() {
                return enable(StrictMode.PENALTY_LOG);
            }

            public Builder penaltyDropBox() {
                return enable(StrictMode.PENALTY_DROPBOX);
            }

            private Builder enable(int bit) {
                this.mMask |= bit;
                return this;
            }

            public VmPolicy build() {
                if (this.mMask != 0 && (this.mMask & LayoutParams.SOFT_INPUT_MASK_ADJUST) == 0) {
                    penaltyLog();
                }
                return new VmPolicy(this.mClassInstanceLimit != null ? this.mClassInstanceLimit : StrictMode.EMPTY_CLASS_LIMIT_MAP, null);
            }
        }

        static {
            LAX = new VmPolicy(0, StrictMode.EMPTY_CLASS_LIMIT_MAP);
        }

        private VmPolicy(int mask, HashMap<Class, Integer> classInstanceLimit) {
            if (classInstanceLimit == null) {
                throw new NullPointerException("classInstanceLimit == null");
            }
            this.mask = mask;
            this.classInstanceLimit = classInstanceLimit;
        }

        public String toString() {
            return "[StrictMode.VmPolicy; mask=" + this.mask + "]";
        }
    }

    static {
        LOG_V = Log.isLoggable(TAG, DETECT_DISK_READ);
        IS_USER_BUILD = Context.USER_SERVICE.equals(Build.TYPE);
        IS_ENG_BUILD = "eng".equals(Build.TYPE);
        EMPTY_CLASS_LIMIT_MAP = new HashMap();
        sVmPolicyMask = 0;
        sVmPolicy = VmPolicy.LAX;
        sDropboxCallsInFlight = new AtomicInteger(0);
        gatheredViolations = new ThreadLocal<ArrayList<ViolationInfo>>() {
            protected ArrayList<ViolationInfo> initialValue() {
                return null;
            }
        };
        violationsBeingTimed = new ThreadLocal<ArrayList<ViolationInfo>>() {
            protected ArrayList<ViolationInfo> initialValue() {
                return new ArrayList();
            }
        };
        threadHandler = new ThreadLocal<Handler>() {
            protected Handler initialValue() {
                return new Handler();
            }
        };
        threadAndroidPolicy = new ThreadLocal<AndroidBlockGuardPolicy>() {
            protected AndroidBlockGuardPolicy initialValue() {
                return new AndroidBlockGuardPolicy(0);
            }
        };
        sLastInstanceCountCheckMillis = 0;
        sIsIdlerRegistered = LOG_V;
        sProcessIdleHandler = new IdleHandler() {
            public boolean queueIdle() {
                long now = SystemClock.uptimeMillis();
                if (now - StrictMode.sLastInstanceCountCheckMillis > StrictMode.MIN_DIALOG_INTERVAL_MS) {
                    StrictMode.sLastInstanceCountCheckMillis = now;
                    StrictMode.conditionallyCheckInstanceCounts();
                }
                return true;
            }
        };
        sLastVmViolationTime = new HashMap();
        NO_OP_SPAN = new Span() {
            public void finish() {
            }
        };
        sThisThreadSpanState = new ThreadLocal<ThreadSpanState>() {
            protected ThreadSpanState initialValue() {
                return new ThreadSpanState();
            }
        };
        sWindowManager = new Singleton<IWindowManager>() {
            protected IWindowManager create() {
                return Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE));
            }
        };
        sExpectedActivityInstanceCount = new HashMap();
    }

    private StrictMode() {
    }

    public static void setThreadPolicy(ThreadPolicy policy) {
        setThreadPolicyMask(policy.mask);
    }

    private static void setThreadPolicyMask(int policyMask) {
        setBlockGuardPolicy(policyMask);
        Binder.setThreadStrictModePolicy(policyMask);
    }

    private static void setBlockGuardPolicy(int policyMask) {
        if (policyMask == 0) {
            BlockGuard.setThreadPolicy(BlockGuard.LAX_POLICY);
            return;
        }
        AndroidBlockGuardPolicy androidPolicy;
        Policy policy = BlockGuard.getThreadPolicy();
        if (policy instanceof AndroidBlockGuardPolicy) {
            androidPolicy = (AndroidBlockGuardPolicy) policy;
        } else {
            androidPolicy = (AndroidBlockGuardPolicy) threadAndroidPolicy.get();
            BlockGuard.setThreadPolicy(androidPolicy);
        }
        androidPolicy.setPolicyMask(policyMask);
    }

    private static void setCloseGuardEnabled(boolean enabled) {
        if (!(CloseGuard.getReporter() instanceof AndroidCloseGuardReporter)) {
            CloseGuard.setReporter(new AndroidCloseGuardReporter());
        }
        CloseGuard.setEnabled(enabled);
    }

    public static int getThreadPolicyMask() {
        return BlockGuard.getThreadPolicy().getPolicyMask();
    }

    public static ThreadPolicy getThreadPolicy() {
        return new ThreadPolicy(null);
    }

    public static ThreadPolicy allowThreadDiskWrites() {
        int oldPolicyMask = getThreadPolicyMask();
        int newPolicyMask = oldPolicyMask & -4;
        if (newPolicyMask != oldPolicyMask) {
            setThreadPolicyMask(newPolicyMask);
        }
        return new ThreadPolicy(null);
    }

    public static ThreadPolicy allowThreadDiskReads() {
        int oldPolicyMask = getThreadPolicyMask();
        int newPolicyMask = oldPolicyMask & -3;
        if (newPolicyMask != oldPolicyMask) {
            setThreadPolicyMask(newPolicyMask);
        }
        return new ThreadPolicy(null);
    }

    private static boolean amTheSystemServerProcess() {
        if (Process.myUid() != LayoutParams.TYPE_APPLICATION_PANEL) {
            return LOG_V;
        }
        Throwable stack = new Throwable();
        stack.fillInStackTrace();
        StackTraceElement[] arr$ = stack.getStackTrace();
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; i$ += DETECT_DISK_WRITE) {
            String clsName = arr$[i$].getClassName();
            if (clsName != null && clsName.startsWith("com.android.server.")) {
                return true;
            }
        }
        return LOG_V;
    }

    public static boolean conditionallyEnableDebugLogging() {
        boolean doFlashes;
        if (!SystemProperties.getBoolean(VISUAL_PROPERTY, LOG_V) || amTheSystemServerProcess()) {
            doFlashes = LOG_V;
        } else {
            doFlashes = true;
        }
        boolean suppress = SystemProperties.getBoolean(DISABLE_PROPERTY, LOG_V);
        if (doFlashes || !(IS_USER_BUILD || suppress)) {
            if (IS_ENG_BUILD) {
                doFlashes = true;
            }
            int threadPolicyMask = 7;
            if (!IS_USER_BUILD) {
                threadPolicyMask = 7 | PENALTY_DROPBOX;
            }
            if (doFlashes) {
                threadPolicyMask |= PENALTY_FLASH;
            }
            setThreadPolicyMask(threadPolicyMask);
            if (IS_USER_BUILD) {
                setCloseGuardEnabled(LOG_V);
            } else {
                Builder policyBuilder = new Builder().detectAll().penaltyDropBox();
                if (IS_ENG_BUILD) {
                    policyBuilder.penaltyLog();
                }
                setVmPolicy(policyBuilder.build());
                setCloseGuardEnabled(vmClosableObjectLeaksEnabled());
            }
            return true;
        }
        setCloseGuardEnabled(LOG_V);
        return LOG_V;
    }

    public static void enableDeathOnNetwork() {
        setThreadPolicyMask((getThreadPolicyMask() | DETECT_NETWORK) | PENALTY_DEATH_ON_NETWORK);
    }

    private static int parsePolicyFromMessage(String message) {
        int i = 0;
        if (message != null && message.startsWith("policy=")) {
            int spaceIndex = message.indexOf(PENALTY_DIALOG);
            if (spaceIndex != -1) {
                try {
                    i = Integer.valueOf(message.substring(7, spaceIndex)).intValue();
                } catch (NumberFormatException e) {
                }
            }
        }
        return i;
    }

    private static int parseViolationFromMessage(String message) {
        int i = 0;
        if (message != null) {
            int violationIndex = message.indexOf("violation=");
            if (violationIndex != -1) {
                int numberStartIndex = violationIndex + "violation=".length();
                int numberEndIndex = message.indexOf(PENALTY_DIALOG, numberStartIndex);
                if (numberEndIndex == -1) {
                    numberEndIndex = message.length();
                }
                try {
                    i = Integer.valueOf(message.substring(numberStartIndex, numberEndIndex)).intValue();
                } catch (NumberFormatException e) {
                }
            }
        }
        return i;
    }

    private static boolean tooManyViolationsThisLoop() {
        return ((ArrayList) violationsBeingTimed.get()).size() >= MAX_OFFENSES_PER_LOOP ? true : LOG_V;
    }

    private static void executeDeathPenalty(ViolationInfo info) {
        throw new StrictModeViolation(info.policy, parseViolationFromMessage(info.crashInfo.exceptionMessage), null);
    }

    private static void dropboxViolationAsync(int violationMaskSubset, ViolationInfo info) {
        int outstanding = sDropboxCallsInFlight.incrementAndGet();
        if (outstanding > MAX_SPAN_TAGS) {
            sDropboxCallsInFlight.decrementAndGet();
            return;
        }
        if (LOG_V) {
            Log.d(TAG, "Dropboxing async; in-flight=" + outstanding);
        }
        new AnonymousClass5("callActivityManagerForStrictModeDropbox", violationMaskSubset, info).start();
    }

    static boolean hasGatheredViolations() {
        return gatheredViolations.get() != null ? true : LOG_V;
    }

    static void clearGatheredViolations() {
        gatheredViolations.set(null);
    }

    public static void conditionallyCheckInstanceCounts() {
        VmPolicy policy = getVmPolicy();
        if (policy.classInstanceLimit.size() != 0) {
            System.gc();
            System.runFinalization();
            System.gc();
            for (Entry<Class, Integer> entry : policy.classInstanceLimit.entrySet()) {
                Class klass = (Class) entry.getKey();
                int limit = ((Integer) entry.getValue()).intValue();
                long instances = VMDebug.countInstancesOfClass(klass, LOG_V);
                if (instances > ((long) limit)) {
                    Throwable tr = new InstanceCountViolation(klass, instances, limit);
                    onVmPolicyViolation(tr.getMessage(), tr);
                }
            }
        }
    }

    public static void setVmPolicy(VmPolicy policy) {
        synchronized (StrictMode.class) {
            sVmPolicy = policy;
            sVmPolicyMask = policy.mask;
            setCloseGuardEnabled(vmClosableObjectLeaksEnabled());
            Looper looper = Looper.getMainLooper();
            if (looper != null) {
                MessageQueue mq = looper.mQueue;
                if (policy.classInstanceLimit.size() == 0 || (sVmPolicyMask & VM_PENALTY_MASK) == 0) {
                    mq.removeIdleHandler(sProcessIdleHandler);
                    sIsIdlerRegistered = LOG_V;
                } else if (!sIsIdlerRegistered) {
                    mq.addIdleHandler(sProcessIdleHandler);
                    sIsIdlerRegistered = true;
                }
            }
        }
    }

    public static VmPolicy getVmPolicy() {
        VmPolicy vmPolicy;
        synchronized (StrictMode.class) {
            vmPolicy = sVmPolicy;
        }
        return vmPolicy;
    }

    public static void enableDefaults() {
        setThreadPolicy(new Builder().detectAll().penaltyLog().build());
        setVmPolicy(new Builder().detectAll().penaltyLog().build());
    }

    public static boolean vmSqliteObjectLeaksEnabled() {
        return (sVmPolicyMask & PENALTY_DEATH_ON_NETWORK) != 0 ? true : LOG_V;
    }

    public static boolean vmClosableObjectLeaksEnabled() {
        return (sVmPolicyMask & DETECT_VM_CLOSABLE_LEAKS) != 0 ? true : LOG_V;
    }

    public static boolean vmRegistrationLeaksEnabled() {
        return (sVmPolicyMask & DETECT_VM_REGISTRATION_LEAKS) != 0 ? true : LOG_V;
    }

    public static boolean vmFileUriExposureEnabled() {
        return (sVmPolicyMask & DETECT_VM_FILE_URI_EXPOSURE) != 0 ? true : LOG_V;
    }

    public static void onSqliteObjectLeaked(String message, Throwable originStack) {
        onVmPolicyViolation(message, originStack);
    }

    public static void onWebViewMethodCalledOnWrongThread(Throwable originStack) {
        onVmPolicyViolation(null, originStack);
    }

    public static void onIntentReceiverLeaked(Throwable originStack) {
        onVmPolicyViolation(null, originStack);
    }

    public static void onServiceConnectionLeaked(Throwable originStack) {
        onVmPolicyViolation(null, originStack);
    }

    public static void onFileUriExposed(String location) {
        String message = "file:// Uri exposed through " + location;
        onVmPolicyViolation(message, new Throwable(message));
    }

    public static void onVmPolicyViolation(String message, Throwable originStack) {
        boolean penaltyDropbox = (sVmPolicyMask & PENALTY_DROPBOX) != 0 ? true : LOG_V;
        boolean penaltyDeath = (sVmPolicyMask & PENALTY_DEATH) != 0 ? true : LOG_V;
        boolean penaltyLog = (sVmPolicyMask & PENALTY_LOG) != 0 ? true : LOG_V;
        ViolationInfo info = new ViolationInfo(originStack, sVmPolicyMask);
        info.numAnimationsRunning = 0;
        info.tags = null;
        info.broadcastIntentAction = null;
        Integer fingerprint = Integer.valueOf(info.hashCode());
        long now = SystemClock.uptimeMillis();
        long lastViolationTime = 0;
        long timeSinceLastViolationMillis = LinkQualityInfo.UNKNOWN_LONG;
        synchronized (sLastVmViolationTime) {
            if (sLastVmViolationTime.containsKey(fingerprint)) {
                lastViolationTime = ((Long) sLastVmViolationTime.get(fingerprint)).longValue();
                timeSinceLastViolationMillis = now - lastViolationTime;
            }
            if (timeSinceLastViolationMillis > MIN_LOG_INTERVAL_MS) {
                sLastVmViolationTime.put(fingerprint, Long.valueOf(now));
            }
        }
        if (penaltyLog && timeSinceLastViolationMillis > MIN_LOG_INTERVAL_MS) {
            Log.e(TAG, message, originStack);
        }
        int violationMaskSubset = (sVmPolicyMask & ALL_VM_DETECT_BITS) | PENALTY_DROPBOX;
        if (!penaltyDropbox || penaltyDeath) {
            if (penaltyDropbox && lastViolationTime == 0) {
                int savedPolicyMask = getThreadPolicyMask();
                try {
                    setThreadPolicyMask(0);
                    ActivityManagerNative.getDefault().handleApplicationStrictModeViolation(RuntimeInit.getApplicationObject(), violationMaskSubset, info);
                } catch (RemoteException e) {
                    Log.e(TAG, "RemoteException trying to handle StrictMode violation", e);
                } finally {
                    setThreadPolicyMask(savedPolicyMask);
                }
            }
            if (penaltyDeath) {
                System.err.println("StrictMode VmPolicy violation with POLICY_DEATH; shutting down.");
                Process.killProcess(Process.myPid());
                System.exit(MAX_OFFENSES_PER_LOOP);
                return;
            }
            return;
        }
        dropboxViolationAsync(violationMaskSubset, info);
    }

    static void writeGatheredViolationsToParcel(Parcel p) {
        ArrayList<ViolationInfo> violations = (ArrayList) gatheredViolations.get();
        if (violations == null) {
            p.writeInt(0);
        } else {
            p.writeInt(violations.size());
            for (int i = 0; i < violations.size(); i += DETECT_DISK_WRITE) {
                int start = p.dataPosition();
                ((ViolationInfo) violations.get(i)).writeToParcel(p, 0);
                if (p.dataPosition() - start > GLES20.GL_TEXTURE_MAG_FILTER) {
                    Slog.d(TAG, "Wrote violation #" + i + " of " + violations.size() + ": " + (p.dataPosition() - start) + " bytes");
                }
            }
            if (LOG_V) {
                Log.d(TAG, "wrote violations to response parcel; num=" + violations.size());
            }
            violations.clear();
        }
        gatheredViolations.set(null);
    }

    static void readAndHandleBinderCallViolations(Parcel p) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new FastPrintWriter(sw, LOG_V, PENALTY_GATHER);
        new LogStackTrace().printStackTrace(pw);
        pw.flush();
        String ourStack = sw.toString();
        int policyMask = getThreadPolicyMask();
        boolean currentlyGathering = (policyMask & PENALTY_GATHER) != 0 ? true : LOG_V;
        int numViolations = p.readInt();
        int i = 0;
        while (i < numViolations) {
            if (LOG_V) {
                Log.d(TAG, "strict mode violation stacks read from binder call.  i=" + i);
            }
            ViolationInfo info = new ViolationInfo(p, !currentlyGathering ? true : LOG_V);
            if (info.crashInfo.stackTrace == null || info.crashInfo.stackTrace.length() <= Window.PROGRESS_END) {
                StringBuilder stringBuilder = new StringBuilder();
                CrashInfo crashInfo = info.crashInfo;
                crashInfo.stackTrace = stringBuilder.append(crashInfo.stackTrace).append("# via Binder call with stack:\n").append(ourStack).toString();
                Policy policy = BlockGuard.getThreadPolicy();
                if (policy instanceof AndroidBlockGuardPolicy) {
                    ((AndroidBlockGuardPolicy) policy).handleViolationWithTimingAttempt(info);
                }
                i += DETECT_DISK_WRITE;
            } else {
                String front = info.crashInfo.stackTrace.substring(PENALTY_GATHER);
                while (i < numViolations) {
                    info = new ViolationInfo(p, !currentlyGathering ? true : LOG_V);
                    i += DETECT_DISK_WRITE;
                }
                clearGatheredViolations();
                Slog.wtfStack(TAG, "Stack is too large: numViolations=" + numViolations + " policy=#" + Integer.toHexString(policyMask) + " front=" + front);
                return;
            }
        }
    }

    private static void onBinderStrictModePolicyChange(int newPolicy) {
        setBlockGuardPolicy(newPolicy);
    }

    public static Span enterCriticalSpan(String name) {
        if (IS_USER_BUILD) {
            return NO_OP_SPAN;
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name must be non-null and non-empty");
        }
        Span span;
        ThreadSpanState state = (ThreadSpanState) sThisThreadSpanState.get();
        synchronized (state) {
            if (state.mFreeListHead != null) {
                span = state.mFreeListHead;
                state.mFreeListHead = span.mNext;
                state.mFreeListSize--;
            } else {
                span = new Span(state);
            }
            span.mName = name;
            span.mCreateMillis = SystemClock.uptimeMillis();
            span.mNext = state.mActiveHead;
            span.mPrev = null;
            state.mActiveHead = span;
            state.mActiveSize += DETECT_DISK_WRITE;
            if (span.mNext != null) {
                span.mNext.mPrev = span;
            }
            if (LOG_V) {
                Log.d(TAG, "Span enter=" + name + "; size=" + state.mActiveSize);
            }
        }
        return span;
    }

    public static void noteSlowCall(String name) {
        Policy policy = BlockGuard.getThreadPolicy();
        if (policy instanceof AndroidBlockGuardPolicy) {
            ((AndroidBlockGuardPolicy) policy).onCustomSlowCall(name);
        }
    }

    public static void noteDiskRead() {
        Policy policy = BlockGuard.getThreadPolicy();
        if (policy instanceof AndroidBlockGuardPolicy) {
            ((AndroidBlockGuardPolicy) policy).onReadFromDisk();
        }
    }

    public static void noteDiskWrite() {
        Policy policy = BlockGuard.getThreadPolicy();
        if (policy instanceof AndroidBlockGuardPolicy) {
            ((AndroidBlockGuardPolicy) policy).onWriteToDisk();
        }
    }

    public static Object trackActivity(Object instance) {
        return new InstanceTracker(instance);
    }

    public static void incrementExpectedActivityCount(Class klass) {
        if (klass != null) {
            synchronized (StrictMode.class) {
                if ((sVmPolicy.mask & PENALTY_FLASH) == 0) {
                    return;
                }
                Integer expected = (Integer) sExpectedActivityInstanceCount.get(klass);
                sExpectedActivityInstanceCount.put(klass, Integer.valueOf(expected == null ? DETECT_DISK_WRITE : expected.intValue() + DETECT_DISK_WRITE));
            }
        }
    }

    public static void decrementExpectedActivityCount(Class klass) {
        if (klass != null) {
            synchronized (StrictMode.class) {
                if ((sVmPolicy.mask & PENALTY_FLASH) == 0) {
                    return;
                }
                Integer expected = (Integer) sExpectedActivityInstanceCount.get(klass);
                int newExpected = (expected == null || expected.intValue() == 0) ? 0 : expected.intValue() - 1;
                if (newExpected == 0) {
                    sExpectedActivityInstanceCount.remove(klass);
                } else {
                    sExpectedActivityInstanceCount.put(klass, Integer.valueOf(newExpected));
                }
                int limit = newExpected + DETECT_DISK_WRITE;
                if (InstanceTracker.getInstanceCount(klass) > limit) {
                    System.gc();
                    System.runFinalization();
                    System.gc();
                    long instances = VMDebug.countInstancesOfClass(klass, LOG_V);
                    if (instances > ((long) limit)) {
                        Throwable tr = new InstanceCountViolation(klass, instances, limit);
                        onVmPolicyViolation(tr.getMessage(), tr);
                    }
                }
            }
        }
    }
}
