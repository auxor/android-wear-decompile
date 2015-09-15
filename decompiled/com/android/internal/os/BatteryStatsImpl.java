package com.android.internal.os;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.content.Context;
import android.media.AudioSystem;
import android.media.audiopolicy.AudioMixingRule;
import android.net.ConnectivityManager;
import android.net.NetworkStats;
import android.net.NetworkStats.Entry;
import android.net.wifi.WifiManager;
import android.os.BadParcelableException;
import android.os.BatteryStats;
import android.os.BatteryStats.HistoryEventTracker;
import android.os.BatteryStats.HistoryItem;
import android.os.BatteryStats.HistoryPrinter;
import android.os.BatteryStats.HistoryTag;
import android.os.BatteryStats.LongCounter;
import android.os.BatteryStats.Uid.Pid;
import android.os.BatteryStats.Uid.Proc.ExcessivePower;
import android.os.Build;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.ParcelFormatException;
import android.os.Parcelable.Creator;
import android.os.Process;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.WorkSource;
import android.telephony.DataConnectionRealTimeInfo;
import android.telephony.SignalStrength;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.LogWriter;
import android.util.MutableInt;
import android.util.Printer;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TimeUtils;
import com.android.internal.R;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.net.NetworkStatsFactory;
import com.android.internal.telephony.RILConstants;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.AsyncService;
import com.android.internal.util.FastPrintWriter;
import com.android.internal.util.JournaledFile;
import com.android.internal.util.Protocol;
import com.android.internal.widget.ExploreByTouchHelper;
import com.android.internal.widget.WaveView.OnTriggerListener;
import com.android.server.NetworkManagementSocketTagger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

public final class BatteryStatsImpl extends BatteryStats {
    private static final int BATTERY_PLUGGED_NONE = 0;
    public static final Creator<BatteryStatsImpl> CREATOR = null;
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_HISTORY = false;
    static final long DELAY_UPDATE_WAKELOCKS = 5000;
    static final int DELTA_BATTERY_LEVEL_FLAG = 524288;
    static final int DELTA_EVENT_FLAG = 8388608;
    static final int DELTA_STATE2_FLAG = 2097152;
    static final int DELTA_STATE_FLAG = 1048576;
    static final int DELTA_STATE_MASK = -16777216;
    static final int DELTA_TIME_ABS = 524285;
    static final int DELTA_TIME_INT = 524286;
    static final int DELTA_TIME_LONG = 524287;
    static final int DELTA_TIME_MASK = 524287;
    static final int DELTA_WAKELOCK_FLAG = 4194304;
    private static final int MAGIC = -1166707595;
    static final int MAX_HISTORY_BUFFER = 262144;
    private static final int MAX_HISTORY_ITEMS = 2000;
    static final int MAX_LEVEL_STEPS = 200;
    static final int MAX_MAX_HISTORY_BUFFER = 327680;
    private static final int MAX_MAX_HISTORY_ITEMS = 3000;
    private static final int MAX_WAKELOCKS_PER_UID = 100;
    static final int MSG_REPORT_POWER_CHANGE = 2;
    static final int MSG_UPDATE_WAKELOCKS = 1;
    static final int NET_UPDATE_ALL = 65535;
    static final int NET_UPDATE_MOBILE = 1;
    static final int NET_UPDATE_WIFI = 2;
    private static final int[] PROC_WAKELOCKS_FORMAT = null;
    static final int STATE_BATTERY_HEALTH_MASK = 7;
    static final int STATE_BATTERY_HEALTH_SHIFT = 26;
    static final int STATE_BATTERY_PLUG_MASK = 3;
    static final int STATE_BATTERY_PLUG_SHIFT = 24;
    static final int STATE_BATTERY_STATUS_MASK = 7;
    static final int STATE_BATTERY_STATUS_SHIFT = 29;
    private static final String TAG = "BatteryStatsImpl";
    private static final boolean USE_OLD_HISTORY = false;
    private static final int VERSION = 116;
    private static final int[] WAKEUP_SOURCES_FORMAT = null;
    private static int sKernelWakelockUpdateVersion;
    private static int sNumSpeedSteps;
    final HistoryEventTracker mActiveEvents;
    int mAudioOnNesting;
    StopwatchTimer mAudioOnTimer;
    final ArrayList<StopwatchTimer> mAudioTurnedOnTimers;
    boolean mBluetoothOn;
    StopwatchTimer mBluetoothOnTimer;
    private int mBluetoothPingCount;
    private int mBluetoothPingStart;
    int mBluetoothState;
    final StopwatchTimer[] mBluetoothStateTimer;
    BluetoothHeadset mBtHeadset;
    private BatteryCallback mCallback;
    int mChangedStates;
    int mChangedStates2;
    final long[] mChargeStepDurations;
    public final AtomicFile mCheckinFile;
    private NetworkStats mCurMobileSnapshot;
    int mCurStepMode;
    private NetworkStats mCurWifiSnapshot;
    int mCurrentBatteryLevel;
    int mDischargeAmountScreenOff;
    int mDischargeAmountScreenOffSinceCharge;
    int mDischargeAmountScreenOn;
    int mDischargeAmountScreenOnSinceCharge;
    int mDischargeCurrentLevel;
    int mDischargePlugLevel;
    int mDischargeScreenOffUnplugLevel;
    int mDischargeScreenOnUnplugLevel;
    int mDischargeStartLevel;
    final long[] mDischargeStepDurations;
    int mDischargeUnplugLevel;
    boolean mDistributeWakelockCpu;
    String mEndPlatformVersion;
    private final JournaledFile mFile;
    boolean mFlashlightOn;
    StopwatchTimer mFlashlightOnTimer;
    final ArrayList<StopwatchTimer> mFullTimers;
    final ArrayList<StopwatchTimer> mFullWifiLockTimers;
    boolean mGlobalWifiRunning;
    StopwatchTimer mGlobalWifiRunningTimer;
    int mGpsNesting;
    public final MyHandler mHandler;
    boolean mHaveBatteryLevel;
    int mHighDischargeAmountSinceCharge;
    HistoryItem mHistory;
    final HistoryItem mHistoryAddTmp;
    long mHistoryBaseTime;
    final Parcel mHistoryBuffer;
    int mHistoryBufferLastPos;
    HistoryItem mHistoryCache;
    final HistoryItem mHistoryCur;
    HistoryItem mHistoryEnd;
    private HistoryItem mHistoryIterator;
    HistoryItem mHistoryLastEnd;
    final HistoryItem mHistoryLastLastWritten;
    final HistoryItem mHistoryLastWritten;
    boolean mHistoryOverflow;
    final HistoryItem mHistoryReadTmp;
    final HashMap<HistoryTag, Integer> mHistoryTagPool;
    int mInitStepMode;
    private String mInitialAcquireWakeName;
    private int mInitialAcquireWakeUid;
    boolean mInteractive;
    StopwatchTimer mInteractiveTimer;
    final SparseIntArray mIsolatedUids;
    private boolean mIteratingHistory;
    private final HashMap<String, SamplingTimer> mKernelWakelockStats;
    int mLastChargeStepLevel;
    long mLastChargeStepTime;
    int mLastDischargeStepLevel;
    long mLastDischargeStepTime;
    long mLastHistoryElapsedRealtime;
    private NetworkStats mLastMobileSnapshot;
    final ArrayList<StopwatchTimer> mLastPartialTimers;
    long mLastRecordedClockRealtime;
    long mLastRecordedClockTime;
    String mLastWakeupReason;
    long mLastWakeupUptimeMs;
    private NetworkStats mLastWifiSnapshot;
    long mLastWriteTime;
    private int mLoadedNumConnectivityChange;
    int mLowDischargeAmountSinceCharge;
    boolean mLowPowerModeEnabled;
    StopwatchTimer mLowPowerModeEnabledTimer;
    int mMaxChargeStepLevel;
    int mMinDischargeStepLevel;
    @GuardedBy("this")
    private String[] mMobileIfaces;
    LongSamplingCounter mMobileRadioActiveAdjustedTime;
    StopwatchTimer mMobileRadioActivePerAppTimer;
    long mMobileRadioActiveStartTime;
    StopwatchTimer mMobileRadioActiveTimer;
    LongSamplingCounter mMobileRadioActiveUnknownCount;
    LongSamplingCounter mMobileRadioActiveUnknownTime;
    int mMobileRadioPowerState;
    int mModStepMode;
    final LongSamplingCounter[] mNetworkByteActivityCounters;
    final LongSamplingCounter[] mNetworkPacketActivityCounters;
    private final NetworkStatsFactory mNetworkStatsFactory;
    int mNextHistoryTagIdx;
    boolean mNoAutoReset;
    int mNumChargeStepDurations;
    private int mNumConnectivityChange;
    int mNumDischargeStepDurations;
    int mNumHistoryItems;
    int mNumHistoryTagChars;
    boolean mOnBattery;
    boolean mOnBatteryInternal;
    final TimeBase mOnBatteryScreenOffTimeBase;
    final TimeBase mOnBatteryTimeBase;
    final ArrayList<StopwatchTimer> mPartialTimers;
    Parcel mPendingWrite;
    int mPhoneDataConnectionType;
    final StopwatchTimer[] mPhoneDataConnectionsTimer;
    boolean mPhoneOn;
    StopwatchTimer mPhoneOnTimer;
    private int mPhoneServiceState;
    private int mPhoneServiceStateRaw;
    StopwatchTimer mPhoneSignalScanningTimer;
    int mPhoneSignalStrengthBin;
    int mPhoneSignalStrengthBinRaw;
    final StopwatchTimer[] mPhoneSignalStrengthsTimer;
    private int mPhoneSimStateRaw;
    private final Map<String, KernelWakelockStats> mProcWakelockFileStats;
    private final long[] mProcWakelocksData;
    private final String[] mProcWakelocksName;
    int mReadHistoryChars;
    String[] mReadHistoryStrings;
    int[] mReadHistoryUids;
    private boolean mReadOverflow;
    long mRealtime;
    long mRealtimeStart;
    boolean mRecordAllHistory;
    boolean mRecordingHistory;
    int mScreenBrightnessBin;
    final StopwatchTimer[] mScreenBrightnessTimer;
    StopwatchTimer mScreenOnTimer;
    int mScreenState;
    int mSensorNesting;
    final SparseArray<ArrayList<StopwatchTimer>> mSensorTimers;
    boolean mShuttingDown;
    long mStartClockTime;
    int mStartCount;
    String mStartPlatformVersion;
    private NetworkStats mTmpNetworkStats;
    private final Entry mTmpNetworkStatsEntry;
    long mTrackRunningHistoryElapsedRealtime;
    long mTrackRunningHistoryUptime;
    final SparseArray<Uid> mUidStats;
    private int mUnpluggedNumConnectivityChange;
    long mUptime;
    long mUptimeStart;
    int mVideoOnNesting;
    StopwatchTimer mVideoOnTimer;
    final ArrayList<StopwatchTimer> mVideoTurnedOnTimers;
    boolean mWakeLockImportant;
    int mWakeLockNesting;
    private final HashMap<String, SamplingTimer> mWakeupReasonStats;
    final SparseArray<ArrayList<StopwatchTimer>> mWifiBatchedScanTimers;
    int mWifiFullLockNesting;
    @GuardedBy("this")
    private String[] mWifiIfaces;
    int mWifiMulticastNesting;
    final ArrayList<StopwatchTimer> mWifiMulticastTimers;
    boolean mWifiOn;
    StopwatchTimer mWifiOnTimer;
    final ArrayList<StopwatchTimer> mWifiRunningTimers;
    int mWifiScanNesting;
    final ArrayList<StopwatchTimer> mWifiScanTimers;
    int mWifiSignalStrengthBin;
    final StopwatchTimer[] mWifiSignalStrengthsTimer;
    int mWifiState;
    final StopwatchTimer[] mWifiStateTimer;
    int mWifiSupplState;
    final StopwatchTimer[] mWifiSupplStateTimer;
    final ArrayList<StopwatchTimer> mWindowTimers;
    final ReentrantLock mWriteLock;

    /* renamed from: com.android.internal.os.BatteryStatsImpl.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ BatteryStatsImpl this$0;
        final /* synthetic */ Parcel val$parcel;

        AnonymousClass1(com.android.internal.os.BatteryStatsImpl r1, android.os.Parcel r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.os.BatteryStatsImpl.1.<init>(com.android.internal.os.BatteryStatsImpl, android.os.Parcel):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.os.BatteryStatsImpl.1.<init>(com.android.internal.os.BatteryStatsImpl, android.os.Parcel):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.1.<init>(com.android.internal.os.BatteryStatsImpl, android.os.Parcel):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.os.BatteryStatsImpl.1.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.os.BatteryStatsImpl.1.run():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.1.run():void");
        }
    }

    /* renamed from: com.android.internal.os.BatteryStatsImpl.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ BatteryStatsImpl this$0;

        AnonymousClass2(BatteryStatsImpl batteryStatsImpl) {
            this.this$0 = batteryStatsImpl;
        }

        public void run() {
            this.this$0.commitPendingDataToDisk();
        }
    }

    /* renamed from: com.android.internal.os.BatteryStatsImpl.3 */
    static class AnonymousClass3 implements Creator<BatteryStatsImpl> {
        AnonymousClass3() {
        }

        public /* bridge */ /* synthetic */ Object m13createFromParcel(Parcel x0) {
            return createFromParcel(x0);
        }

        public /* bridge */ /* synthetic */ Object[] m14newArray(int x0) {
            return newArray(x0);
        }

        public BatteryStatsImpl createFromParcel(Parcel in) {
            return new BatteryStatsImpl(in);
        }

        public BatteryStatsImpl[] newArray(int size) {
            return new BatteryStatsImpl[size];
        }
    }

    public interface TimeBaseObs {
        void onTimeStarted(long j, long j2, long j3);

        void onTimeStopped(long j, long j2, long j3);
    }

    public static abstract class Timer extends android.os.BatteryStats.Timer implements TimeBaseObs {
        int mCount;
        int mLastCount;
        long mLastTime;
        int mLoadedCount;
        long mLoadedTime;
        final TimeBase mTimeBase;
        long mTotalTime;
        final int mType;
        int mUnpluggedCount;
        long mUnpluggedTime;

        protected abstract int computeCurrentCountLocked();

        protected abstract long computeRunTimeLocked(long j);

        Timer(int type, TimeBase timeBase, Parcel in) {
            this.mType = type;
            this.mTimeBase = timeBase;
            this.mCount = in.readInt();
            this.mLoadedCount = in.readInt();
            this.mLastCount = BatteryStatsImpl.BATTERY_PLUGGED_NONE;
            this.mUnpluggedCount = in.readInt();
            this.mTotalTime = in.readLong();
            this.mLoadedTime = in.readLong();
            this.mLastTime = 0;
            this.mUnpluggedTime = in.readLong();
            timeBase.add(this);
        }

        Timer(int type, TimeBase timeBase) {
            this.mType = type;
            this.mTimeBase = timeBase;
            timeBase.add(this);
        }

        boolean reset(boolean detachIfReset) {
            this.mLastTime = 0;
            this.mLoadedTime = 0;
            this.mTotalTime = 0;
            this.mLastCount = BatteryStatsImpl.BATTERY_PLUGGED_NONE;
            this.mLoadedCount = BatteryStatsImpl.BATTERY_PLUGGED_NONE;
            this.mCount = BatteryStatsImpl.BATTERY_PLUGGED_NONE;
            if (detachIfReset) {
                detach();
            }
            return true;
        }

        void detach() {
            this.mTimeBase.remove(this);
        }

        public void writeToParcel(Parcel out, long elapsedRealtimeUs) {
            out.writeInt(this.mCount);
            out.writeInt(this.mLoadedCount);
            out.writeInt(this.mUnpluggedCount);
            out.writeLong(computeRunTimeLocked(this.mTimeBase.getRealtime(elapsedRealtimeUs)));
            out.writeLong(this.mLoadedTime);
            out.writeLong(this.mUnpluggedTime);
        }

        public void onTimeStarted(long elapsedRealtime, long timeBaseUptime, long baseRealtime) {
            this.mUnpluggedTime = computeRunTimeLocked(baseRealtime);
            this.mUnpluggedCount = this.mCount;
        }

        public void onTimeStopped(long elapsedRealtime, long baseUptime, long baseRealtime) {
            this.mTotalTime = computeRunTimeLocked(baseRealtime);
            this.mCount = computeCurrentCountLocked();
        }

        public static void writeTimerToParcel(Parcel out, Timer timer, long elapsedRealtimeUs) {
            if (timer == null) {
                out.writeInt(BatteryStatsImpl.BATTERY_PLUGGED_NONE);
                return;
            }
            out.writeInt(BatteryStatsImpl.NET_UPDATE_MOBILE);
            timer.writeToParcel(out, elapsedRealtimeUs);
        }

        public long getTotalTimeLocked(long elapsedRealtimeUs, int which) {
            long val = computeRunTimeLocked(this.mTimeBase.getRealtime(elapsedRealtimeUs));
            if (which == BatteryStatsImpl.NET_UPDATE_WIFI) {
                return val - this.mUnpluggedTime;
            }
            if (which != 0) {
                return val - this.mLoadedTime;
            }
            return val;
        }

        public int getCountLocked(int which) {
            int val = computeCurrentCountLocked();
            if (which == BatteryStatsImpl.NET_UPDATE_WIFI) {
                return val - this.mUnpluggedCount;
            }
            if (which != 0) {
                return val - this.mLoadedCount;
            }
            return val;
        }

        public void logState(Printer pw, String prefix) {
            pw.println(prefix + "mCount=" + this.mCount + " mLoadedCount=" + this.mLoadedCount + " mLastCount=" + this.mLastCount + " mUnpluggedCount=" + this.mUnpluggedCount);
            pw.println(prefix + "mTotalTime=" + this.mTotalTime + " mLoadedTime=" + this.mLoadedTime);
            pw.println(prefix + "mLastTime=" + this.mLastTime + " mUnpluggedTime=" + this.mUnpluggedTime);
        }

        void writeSummaryFromParcelLocked(Parcel out, long elapsedRealtimeUs) {
            out.writeLong(computeRunTimeLocked(this.mTimeBase.getRealtime(elapsedRealtimeUs)));
            out.writeInt(this.mCount);
        }

        void readSummaryFromParcelLocked(Parcel in) {
            long readLong = in.readLong();
            this.mLoadedTime = readLong;
            this.mTotalTime = readLong;
            this.mLastTime = 0;
            this.mUnpluggedTime = this.mTotalTime;
            int readInt = in.readInt();
            this.mLoadedCount = readInt;
            this.mCount = readInt;
            this.mLastCount = BatteryStatsImpl.BATTERY_PLUGGED_NONE;
            this.mUnpluggedCount = this.mCount;
        }
    }

    public static final class BatchTimer extends Timer {
        boolean mInDischarge;
        long mLastAddedDuration;
        long mLastAddedTime;
        final Uid mUid;

        BatchTimer(Uid uid, int type, TimeBase timeBase, Parcel in) {
            super(type, timeBase, in);
            this.mUid = uid;
            this.mLastAddedTime = in.readLong();
            this.mLastAddedDuration = in.readLong();
            this.mInDischarge = timeBase.isRunning();
        }

        BatchTimer(Uid uid, int type, TimeBase timeBase) {
            super(type, timeBase);
            this.mUid = uid;
            this.mInDischarge = timeBase.isRunning();
        }

        public void writeToParcel(Parcel out, long elapsedRealtimeUs) {
            super.writeToParcel(out, elapsedRealtimeUs);
            out.writeLong(this.mLastAddedTime);
            out.writeLong(this.mLastAddedDuration);
        }

        public void onTimeStopped(long elapsedRealtime, long baseUptime, long baseRealtime) {
            recomputeLastDuration(SystemClock.elapsedRealtime() * 1000, BatteryStatsImpl.USE_OLD_HISTORY);
            this.mInDischarge = BatteryStatsImpl.USE_OLD_HISTORY;
            super.onTimeStopped(elapsedRealtime, baseUptime, baseRealtime);
        }

        public void onTimeStarted(long elapsedRealtime, long baseUptime, long baseRealtime) {
            recomputeLastDuration(elapsedRealtime, BatteryStatsImpl.USE_OLD_HISTORY);
            this.mInDischarge = true;
            if (this.mLastAddedTime == elapsedRealtime) {
                this.mTotalTime += this.mLastAddedDuration;
            }
            super.onTimeStarted(elapsedRealtime, baseUptime, baseRealtime);
        }

        public void logState(Printer pw, String prefix) {
            super.logState(pw, prefix);
            pw.println(prefix + "mLastAddedTime=" + this.mLastAddedTime + " mLastAddedDuration=" + this.mLastAddedDuration);
        }

        private long computeOverage(long curTime) {
            if (this.mLastAddedTime > 0) {
                return (this.mLastTime + this.mLastAddedDuration) - curTime;
            }
            return 0;
        }

        private void recomputeLastDuration(long curTime, boolean abort) {
            long overage = computeOverage(curTime);
            if (overage > 0) {
                if (this.mInDischarge) {
                    this.mTotalTime -= overage;
                }
                if (abort) {
                    this.mLastAddedTime = 0;
                    return;
                }
                this.mLastAddedTime = curTime;
                this.mLastAddedDuration -= overage;
            }
        }

        public void addDuration(BatteryStatsImpl stats, long durationMillis) {
            long now = SystemClock.elapsedRealtime() * 1000;
            recomputeLastDuration(now, true);
            this.mLastAddedTime = now;
            this.mLastAddedDuration = durationMillis * 1000;
            if (this.mInDischarge) {
                this.mTotalTime += this.mLastAddedDuration;
                this.mCount += BatteryStatsImpl.NET_UPDATE_MOBILE;
            }
        }

        public void abortLastDuration(BatteryStatsImpl stats) {
            recomputeLastDuration(SystemClock.elapsedRealtime() * 1000, true);
        }

        protected int computeCurrentCountLocked() {
            return this.mCount;
        }

        protected long computeRunTimeLocked(long curBatteryRealtime) {
            long overage = computeOverage(SystemClock.elapsedRealtime() * 1000);
            if (overage <= 0) {
                return this.mTotalTime;
            }
            this.mTotalTime = overage;
            return overage;
        }

        boolean reset(boolean detachIfReset) {
            boolean stillActive;
            boolean z;
            long now = SystemClock.elapsedRealtime() * 1000;
            recomputeLastDuration(now, true);
            if (this.mLastAddedTime == now) {
                stillActive = true;
            } else {
                stillActive = BatteryStatsImpl.USE_OLD_HISTORY;
            }
            if (stillActive || !detachIfReset) {
                z = BatteryStatsImpl.USE_OLD_HISTORY;
            } else {
                z = true;
            }
            super.reset(z);
            if (stillActive) {
                return BatteryStatsImpl.USE_OLD_HISTORY;
            }
            return true;
        }
    }

    public interface BatteryCallback {
        void batteryNeedsCpuUpdate();

        void batteryPowerChanged(boolean z);
    }

    public static class Counter extends android.os.BatteryStats.Counter implements TimeBaseObs {
        final AtomicInteger mCount;
        int mLastCount;
        int mLoadedCount;
        int mPluggedCount;
        final TimeBase mTimeBase;
        int mUnpluggedCount;

        Counter(TimeBase timeBase, Parcel in) {
            this.mCount = new AtomicInteger();
            this.mTimeBase = timeBase;
            this.mPluggedCount = in.readInt();
            this.mCount.set(this.mPluggedCount);
            this.mLoadedCount = in.readInt();
            this.mLastCount = BatteryStatsImpl.BATTERY_PLUGGED_NONE;
            this.mUnpluggedCount = in.readInt();
            timeBase.add(this);
        }

        Counter(TimeBase timeBase) {
            this.mCount = new AtomicInteger();
            this.mTimeBase = timeBase;
            timeBase.add(this);
        }

        public void writeToParcel(Parcel out) {
            out.writeInt(this.mCount.get());
            out.writeInt(this.mLoadedCount);
            out.writeInt(this.mUnpluggedCount);
        }

        public void onTimeStarted(long elapsedRealtime, long baseUptime, long baseRealtime) {
            this.mUnpluggedCount = this.mPluggedCount;
            this.mCount.set(this.mPluggedCount);
        }

        public void onTimeStopped(long elapsedRealtime, long baseUptime, long baseRealtime) {
            this.mPluggedCount = this.mCount.get();
        }

        public static void writeCounterToParcel(Parcel out, Counter counter) {
            if (counter == null) {
                out.writeInt(BatteryStatsImpl.BATTERY_PLUGGED_NONE);
                return;
            }
            out.writeInt(BatteryStatsImpl.NET_UPDATE_MOBILE);
            counter.writeToParcel(out);
        }

        public int getCountLocked(int which) {
            int val = this.mCount.get();
            if (which == BatteryStatsImpl.NET_UPDATE_WIFI) {
                return val - this.mUnpluggedCount;
            }
            if (which != 0) {
                return val - this.mLoadedCount;
            }
            return val;
        }

        public void logState(Printer pw, String prefix) {
            pw.println(prefix + "mCount=" + this.mCount.get() + " mLoadedCount=" + this.mLoadedCount + " mLastCount=" + this.mLastCount + " mUnpluggedCount=" + this.mUnpluggedCount + " mPluggedCount=" + this.mPluggedCount);
        }

        void stepAtomic() {
            this.mCount.incrementAndGet();
        }

        void reset(boolean detachIfReset) {
            this.mCount.set(BatteryStatsImpl.BATTERY_PLUGGED_NONE);
            this.mUnpluggedCount = BatteryStatsImpl.BATTERY_PLUGGED_NONE;
            this.mPluggedCount = BatteryStatsImpl.BATTERY_PLUGGED_NONE;
            this.mLastCount = BatteryStatsImpl.BATTERY_PLUGGED_NONE;
            this.mLoadedCount = BatteryStatsImpl.BATTERY_PLUGGED_NONE;
            if (detachIfReset) {
                detach();
            }
        }

        void detach() {
            this.mTimeBase.remove(this);
        }

        void writeSummaryFromParcelLocked(Parcel out) {
            out.writeInt(this.mCount.get());
        }

        void readSummaryFromParcelLocked(Parcel in) {
            this.mLoadedCount = in.readInt();
            this.mCount.set(this.mLoadedCount);
            this.mLastCount = BatteryStatsImpl.BATTERY_PLUGGED_NONE;
            int i = this.mLoadedCount;
            this.mPluggedCount = i;
            this.mUnpluggedCount = i;
        }
    }

    private class KernelWakelockStats {
        public int mCount;
        public long mTotalTime;
        public int mVersion;
        final /* synthetic */ BatteryStatsImpl this$0;

        KernelWakelockStats(BatteryStatsImpl batteryStatsImpl, int count, long totalTime, int version) {
            this.this$0 = batteryStatsImpl;
            this.mCount = count;
            this.mTotalTime = totalTime;
            this.mVersion = version;
        }
    }

    public static class LongSamplingCounter extends LongCounter implements TimeBaseObs {
        long mCount;
        long mLastCount;
        long mLoadedCount;
        long mPluggedCount;
        final TimeBase mTimeBase;
        long mUnpluggedCount;

        LongSamplingCounter(TimeBase timeBase, Parcel in) {
            this.mTimeBase = timeBase;
            this.mPluggedCount = in.readLong();
            this.mCount = this.mPluggedCount;
            this.mLoadedCount = in.readLong();
            this.mLastCount = 0;
            this.mUnpluggedCount = in.readLong();
            timeBase.add(this);
        }

        LongSamplingCounter(TimeBase timeBase) {
            this.mTimeBase = timeBase;
            timeBase.add(this);
        }

        public void writeToParcel(Parcel out) {
            out.writeLong(this.mCount);
            out.writeLong(this.mLoadedCount);
            out.writeLong(this.mUnpluggedCount);
        }

        public void onTimeStarted(long elapsedRealtime, long baseUptime, long baseRealtime) {
            this.mUnpluggedCount = this.mPluggedCount;
            this.mCount = this.mPluggedCount;
        }

        public void onTimeStopped(long elapsedRealtime, long baseUptime, long baseRealtime) {
            this.mPluggedCount = this.mCount;
        }

        public long getCountLocked(int which) {
            long val = this.mCount;
            if (which == BatteryStatsImpl.NET_UPDATE_WIFI) {
                return val - this.mUnpluggedCount;
            }
            if (which != 0) {
                return val - this.mLoadedCount;
            }
            return val;
        }

        public void logState(Printer pw, String prefix) {
            pw.println(prefix + "mCount=" + this.mCount + " mLoadedCount=" + this.mLoadedCount + " mLastCount=" + this.mLastCount + " mUnpluggedCount=" + this.mUnpluggedCount + " mPluggedCount=" + this.mPluggedCount);
        }

        void addCountLocked(long count) {
            this.mCount += count;
        }

        void reset(boolean detachIfReset) {
            this.mCount = 0;
            this.mUnpluggedCount = 0;
            this.mPluggedCount = 0;
            this.mLastCount = 0;
            this.mLoadedCount = 0;
            if (detachIfReset) {
                detach();
            }
        }

        void detach() {
            this.mTimeBase.remove(this);
        }

        void writeSummaryFromParcelLocked(Parcel out) {
            out.writeLong(this.mCount);
        }

        void readSummaryFromParcelLocked(Parcel in) {
            this.mLoadedCount = in.readLong();
            this.mCount = this.mLoadedCount;
            this.mLastCount = 0;
            long j = this.mLoadedCount;
            this.mPluggedCount = j;
            this.mUnpluggedCount = j;
        }
    }

    final class MyHandler extends Handler {
        final /* synthetic */ BatteryStatsImpl this$0;

        public MyHandler(BatteryStatsImpl batteryStatsImpl, Looper looper) {
            this.this$0 = batteryStatsImpl;
            super(looper, null, true);
        }

        public void handleMessage(Message msg) {
            BatteryCallback cb = this.this$0.mCallback;
            switch (msg.what) {
                case BatteryStatsImpl.NET_UPDATE_MOBILE /*1*/:
                    if (cb != null) {
                        cb.batteryNeedsCpuUpdate();
                    }
                case BatteryStatsImpl.NET_UPDATE_WIFI /*2*/:
                    if (cb != null) {
                        cb.batteryPowerChanged(msg.arg1 != 0 ? true : BatteryStatsImpl.USE_OLD_HISTORY);
                    }
                default:
            }
        }
    }

    public abstract class OverflowArrayMap<T> {
        private static final String OVERFLOW_NAME = "*overflow*";
        ArrayMap<String, MutableInt> mActiveOverflow;
        T mCurOverflow;
        final ArrayMap<String, T> mMap;
        final /* synthetic */ BatteryStatsImpl this$0;

        public abstract T instantiateObject();

        public OverflowArrayMap(BatteryStatsImpl batteryStatsImpl) {
            this.this$0 = batteryStatsImpl;
            this.mMap = new ArrayMap();
        }

        public ArrayMap<String, T> getMap() {
            return this.mMap;
        }

        public void clear() {
            this.mMap.clear();
            this.mCurOverflow = null;
            this.mActiveOverflow = null;
        }

        public void add(String name, T obj) {
            this.mMap.put(name, obj);
            if (OVERFLOW_NAME.equals(name)) {
                this.mCurOverflow = obj;
            }
        }

        public void cleanup() {
            if (this.mActiveOverflow != null && this.mActiveOverflow.size() == 0) {
                this.mActiveOverflow = null;
            }
            if (this.mActiveOverflow == null) {
                if (this.mMap.containsKey(OVERFLOW_NAME)) {
                    Slog.wtf(BatteryStatsImpl.TAG, "Cleaning up with no active overflow, but have overflow entry " + this.mMap.get(OVERFLOW_NAME));
                    this.mMap.remove(OVERFLOW_NAME);
                }
                this.mCurOverflow = null;
            } else if (this.mCurOverflow == null || !this.mMap.containsKey(OVERFLOW_NAME)) {
                Slog.wtf(BatteryStatsImpl.TAG, "Cleaning up with active overflow, but no overflow entry: cur=" + this.mCurOverflow + " map=" + this.mMap.get(OVERFLOW_NAME));
            }
        }

        public T startObject(String name) {
            T obj = this.mMap.get(name);
            if (obj != null) {
                return obj;
            }
            if (this.mActiveOverflow != null) {
                MutableInt over = (MutableInt) this.mActiveOverflow.get(name);
                if (over != null) {
                    obj = this.mCurOverflow;
                    if (obj == null) {
                        Slog.wtf(BatteryStatsImpl.TAG, "Have active overflow " + name + " but null overflow");
                        obj = instantiateObject();
                        this.mCurOverflow = obj;
                        this.mMap.put(OVERFLOW_NAME, obj);
                    }
                    over.value += BatteryStatsImpl.NET_UPDATE_MOBILE;
                    return obj;
                }
            }
            if (this.mMap.size() >= BatteryStatsImpl.MAX_WAKELOCKS_PER_UID) {
                obj = this.mCurOverflow;
                if (obj == null) {
                    obj = instantiateObject();
                    this.mCurOverflow = obj;
                    this.mMap.put(OVERFLOW_NAME, obj);
                }
                if (this.mActiveOverflow == null) {
                    this.mActiveOverflow = new ArrayMap();
                }
                this.mActiveOverflow.put(name, new MutableInt(BatteryStatsImpl.NET_UPDATE_MOBILE));
                return obj;
            }
            obj = instantiateObject();
            this.mMap.put(name, obj);
            return obj;
        }

        public T stopObject(String name) {
            T obj = this.mMap.get(name);
            if (obj != null) {
                return obj;
            }
            if (this.mActiveOverflow != null) {
                MutableInt over = (MutableInt) this.mActiveOverflow.get(name);
                if (over != null) {
                    obj = this.mCurOverflow;
                    if (obj != null) {
                        over.value--;
                        if (over.value <= 0) {
                            this.mActiveOverflow.remove(name);
                        }
                        return obj;
                    }
                }
            }
            Slog.wtf(BatteryStatsImpl.TAG, "Unable to find object for " + name + " mapsize=" + this.mMap.size() + " activeoverflow=" + this.mActiveOverflow + " curoverflow=" + this.mCurOverflow);
            return null;
        }
    }

    public static class SamplingCounter extends Counter {
        SamplingCounter(TimeBase timeBase, Parcel in) {
            super(timeBase, in);
        }

        SamplingCounter(TimeBase timeBase) {
            super(timeBase);
        }

        public void addCountAtomic(long count) {
            this.mCount.addAndGet((int) count);
        }
    }

    public static final class SamplingTimer extends Timer {
        int mCurrentReportedCount;
        long mCurrentReportedTotalTime;
        boolean mTimeBaseRunning;
        boolean mTrackingReportedValues;
        int mUnpluggedReportedCount;
        long mUnpluggedReportedTotalTime;
        int mUpdateVersion;

        SamplingTimer(TimeBase timeBase, Parcel in) {
            boolean z = true;
            super(BatteryStatsImpl.BATTERY_PLUGGED_NONE, timeBase, in);
            this.mCurrentReportedCount = in.readInt();
            this.mUnpluggedReportedCount = in.readInt();
            this.mCurrentReportedTotalTime = in.readLong();
            this.mUnpluggedReportedTotalTime = in.readLong();
            if (in.readInt() != BatteryStatsImpl.NET_UPDATE_MOBILE) {
                z = BatteryStatsImpl.USE_OLD_HISTORY;
            }
            this.mTrackingReportedValues = z;
            this.mTimeBaseRunning = timeBase.isRunning();
        }

        SamplingTimer(TimeBase timeBase, boolean trackReportedValues) {
            super(BatteryStatsImpl.BATTERY_PLUGGED_NONE, timeBase);
            this.mTrackingReportedValues = trackReportedValues;
            this.mTimeBaseRunning = timeBase.isRunning();
        }

        public void setStale() {
            this.mTrackingReportedValues = BatteryStatsImpl.USE_OLD_HISTORY;
            this.mUnpluggedReportedTotalTime = 0;
            this.mUnpluggedReportedCount = BatteryStatsImpl.BATTERY_PLUGGED_NONE;
        }

        public void setUpdateVersion(int version) {
            this.mUpdateVersion = version;
        }

        public int getUpdateVersion() {
            return this.mUpdateVersion;
        }

        public void updateCurrentReportedCount(int count) {
            if (this.mTimeBaseRunning && this.mUnpluggedReportedCount == 0) {
                this.mUnpluggedReportedCount = count;
                this.mTrackingReportedValues = true;
            }
            this.mCurrentReportedCount = count;
        }

        public void addCurrentReportedCount(int delta) {
            updateCurrentReportedCount(this.mCurrentReportedCount + delta);
        }

        public void updateCurrentReportedTotalTime(long totalTime) {
            if (this.mTimeBaseRunning && this.mUnpluggedReportedTotalTime == 0) {
                this.mUnpluggedReportedTotalTime = totalTime;
                this.mTrackingReportedValues = true;
            }
            this.mCurrentReportedTotalTime = totalTime;
        }

        public void addCurrentReportedTotalTime(long delta) {
            updateCurrentReportedTotalTime(this.mCurrentReportedTotalTime + delta);
        }

        public void onTimeStarted(long elapsedRealtime, long baseUptime, long baseRealtime) {
            super.onTimeStarted(elapsedRealtime, baseUptime, baseRealtime);
            if (this.mTrackingReportedValues) {
                this.mUnpluggedReportedTotalTime = this.mCurrentReportedTotalTime;
                this.mUnpluggedReportedCount = this.mCurrentReportedCount;
            }
            this.mTimeBaseRunning = true;
        }

        public void onTimeStopped(long elapsedRealtime, long baseUptime, long baseRealtime) {
            super.onTimeStopped(elapsedRealtime, baseUptime, baseRealtime);
            this.mTimeBaseRunning = BatteryStatsImpl.USE_OLD_HISTORY;
        }

        public void logState(Printer pw, String prefix) {
            super.logState(pw, prefix);
            pw.println(prefix + "mCurrentReportedCount=" + this.mCurrentReportedCount + " mUnpluggedReportedCount=" + this.mUnpluggedReportedCount + " mCurrentReportedTotalTime=" + this.mCurrentReportedTotalTime + " mUnpluggedReportedTotalTime=" + this.mUnpluggedReportedTotalTime);
        }

        protected long computeRunTimeLocked(long curBatteryRealtime) {
            long j = this.mTotalTime;
            long j2 = (this.mTimeBaseRunning && this.mTrackingReportedValues) ? this.mCurrentReportedTotalTime - this.mUnpluggedReportedTotalTime : 0;
            return j2 + j;
        }

        protected int computeCurrentCountLocked() {
            int i = this.mCount;
            int i2 = (this.mTimeBaseRunning && this.mTrackingReportedValues) ? this.mCurrentReportedCount - this.mUnpluggedReportedCount : BatteryStatsImpl.BATTERY_PLUGGED_NONE;
            return i2 + i;
        }

        public void writeToParcel(Parcel out, long elapsedRealtimeUs) {
            super.writeToParcel(out, elapsedRealtimeUs);
            out.writeInt(this.mCurrentReportedCount);
            out.writeInt(this.mUnpluggedReportedCount);
            out.writeLong(this.mCurrentReportedTotalTime);
            out.writeLong(this.mUnpluggedReportedTotalTime);
            out.writeInt(this.mTrackingReportedValues ? BatteryStatsImpl.NET_UPDATE_MOBILE : BatteryStatsImpl.BATTERY_PLUGGED_NONE);
        }

        boolean reset(boolean detachIfReset) {
            super.reset(detachIfReset);
            setStale();
            return true;
        }

        void writeSummaryFromParcelLocked(Parcel out, long batteryRealtime) {
            super.writeSummaryFromParcelLocked(out, batteryRealtime);
            out.writeLong(this.mCurrentReportedTotalTime);
            out.writeInt(this.mCurrentReportedCount);
            out.writeInt(this.mTrackingReportedValues ? BatteryStatsImpl.NET_UPDATE_MOBILE : BatteryStatsImpl.BATTERY_PLUGGED_NONE);
        }

        void readSummaryFromParcelLocked(Parcel in) {
            boolean z = true;
            super.readSummaryFromParcelLocked(in);
            long readLong = in.readLong();
            this.mCurrentReportedTotalTime = readLong;
            this.mUnpluggedReportedTotalTime = readLong;
            int readInt = in.readInt();
            this.mCurrentReportedCount = readInt;
            this.mUnpluggedReportedCount = readInt;
            if (in.readInt() != BatteryStatsImpl.NET_UPDATE_MOBILE) {
                z = BatteryStatsImpl.USE_OLD_HISTORY;
            }
            this.mTrackingReportedValues = z;
        }
    }

    public static final class StopwatchTimer extends Timer {
        long mAcquireTime;
        boolean mInList;
        int mNesting;
        long mTimeout;
        final ArrayList<StopwatchTimer> mTimerPool;
        final Uid mUid;
        long mUpdateTime;

        StopwatchTimer(Uid uid, int type, ArrayList<StopwatchTimer> timerPool, TimeBase timeBase, Parcel in) {
            super(type, timeBase, in);
            this.mUid = uid;
            this.mTimerPool = timerPool;
            this.mUpdateTime = in.readLong();
        }

        StopwatchTimer(Uid uid, int type, ArrayList<StopwatchTimer> timerPool, TimeBase timeBase) {
            super(type, timeBase);
            this.mUid = uid;
            this.mTimerPool = timerPool;
        }

        void setTimeout(long timeout) {
            this.mTimeout = timeout;
        }

        public void writeToParcel(Parcel out, long elapsedRealtimeUs) {
            super.writeToParcel(out, elapsedRealtimeUs);
            out.writeLong(this.mUpdateTime);
        }

        public void onTimeStopped(long elapsedRealtime, long baseUptime, long baseRealtime) {
            if (this.mNesting > 0) {
                super.onTimeStopped(elapsedRealtime, baseUptime, baseRealtime);
                this.mUpdateTime = baseRealtime;
            }
        }

        public void logState(Printer pw, String prefix) {
            super.logState(pw, prefix);
            pw.println(prefix + "mNesting=" + this.mNesting + " mUpdateTime=" + this.mUpdateTime + " mAcquireTime=" + this.mAcquireTime);
        }

        void startRunningLocked(long elapsedRealtimeMs) {
            int i = this.mNesting;
            this.mNesting = i + BatteryStatsImpl.NET_UPDATE_MOBILE;
            if (i == 0) {
                long batteryRealtime = this.mTimeBase.getRealtime(1000 * elapsedRealtimeMs);
                this.mUpdateTime = batteryRealtime;
                if (this.mTimerPool != null) {
                    refreshTimersLocked(batteryRealtime, this.mTimerPool, null);
                    this.mTimerPool.add(this);
                }
                this.mCount += BatteryStatsImpl.NET_UPDATE_MOBILE;
                this.mAcquireTime = this.mTotalTime;
            }
        }

        boolean isRunningLocked() {
            return this.mNesting > 0 ? true : BatteryStatsImpl.USE_OLD_HISTORY;
        }

        long checkpointRunningLocked(long elapsedRealtimeMs) {
            if (this.mNesting <= 0) {
                return 0;
            }
            long batteryRealtime = this.mTimeBase.getRealtime(1000 * elapsedRealtimeMs);
            if (this.mTimerPool != null) {
                return refreshTimersLocked(batteryRealtime, this.mTimerPool, this);
            }
            long heldTime = batteryRealtime - this.mUpdateTime;
            this.mUpdateTime = batteryRealtime;
            this.mTotalTime += heldTime;
            return heldTime;
        }

        void stopRunningLocked(long elapsedRealtimeMs) {
            if (this.mNesting != 0) {
                int i = this.mNesting - 1;
                this.mNesting = i;
                if (i == 0) {
                    long batteryRealtime = this.mTimeBase.getRealtime(1000 * elapsedRealtimeMs);
                    if (this.mTimerPool != null) {
                        refreshTimersLocked(batteryRealtime, this.mTimerPool, null);
                        this.mTimerPool.remove(this);
                    } else {
                        this.mNesting = BatteryStatsImpl.NET_UPDATE_MOBILE;
                        this.mTotalTime = computeRunTimeLocked(batteryRealtime);
                        this.mNesting = BatteryStatsImpl.BATTERY_PLUGGED_NONE;
                    }
                    if (this.mTotalTime == this.mAcquireTime) {
                        this.mCount--;
                    }
                }
            }
        }

        void stopAllRunningLocked(long elapsedRealtimeMs) {
            if (this.mNesting > 0) {
                this.mNesting = BatteryStatsImpl.NET_UPDATE_MOBILE;
                stopRunningLocked(elapsedRealtimeMs);
            }
        }

        private static long refreshTimersLocked(long batteryRealtime, ArrayList<StopwatchTimer> pool, StopwatchTimer self) {
            long selfTime = 0;
            int N = pool.size();
            for (int i = N - 1; i >= 0; i--) {
                StopwatchTimer t = (StopwatchTimer) pool.get(i);
                long heldTime = batteryRealtime - t.mUpdateTime;
                if (heldTime > 0) {
                    long myTime = heldTime / ((long) N);
                    if (t == self) {
                        selfTime = myTime;
                    }
                    t.mTotalTime += myTime;
                }
                t.mUpdateTime = batteryRealtime;
            }
            return selfTime;
        }

        protected long computeRunTimeLocked(long curBatteryRealtime) {
            long j = 0;
            if (this.mTimeout > 0 && curBatteryRealtime > this.mUpdateTime + this.mTimeout) {
                curBatteryRealtime = this.mUpdateTime + this.mTimeout;
            }
            long j2 = this.mTotalTime;
            if (this.mNesting > 0) {
                j = (curBatteryRealtime - this.mUpdateTime) / ((long) (this.mTimerPool != null ? this.mTimerPool.size() : BatteryStatsImpl.NET_UPDATE_MOBILE));
            }
            return j + j2;
        }

        protected int computeCurrentCountLocked() {
            return this.mCount;
        }

        boolean reset(boolean detachIfReset) {
            boolean canDetach;
            boolean z = true;
            if (this.mNesting <= 0) {
                canDetach = true;
            } else {
                canDetach = BatteryStatsImpl.USE_OLD_HISTORY;
            }
            if (!(canDetach && detachIfReset)) {
                z = BatteryStatsImpl.USE_OLD_HISTORY;
            }
            super.reset(z);
            if (this.mNesting > 0) {
                this.mUpdateTime = this.mTimeBase.getRealtime(SystemClock.elapsedRealtime() * 1000);
            }
            this.mAcquireTime = this.mTotalTime;
            return canDetach;
        }

        void detach() {
            super.detach();
            if (this.mTimerPool != null) {
                this.mTimerPool.remove(this);
            }
        }

        void readSummaryFromParcelLocked(Parcel in) {
            super.readSummaryFromParcelLocked(in);
            this.mNesting = BatteryStatsImpl.BATTERY_PLUGGED_NONE;
        }
    }

    static class TimeBase {
        private final ArrayList<TimeBaseObs> mObservers;
        private long mPastRealtime;
        private long mPastUptime;
        private long mRealtime;
        private long mRealtimeStart;
        private boolean mRunning;
        private long mUnpluggedRealtime;
        private long mUnpluggedUptime;
        private long mUptime;
        private long mUptimeStart;

        TimeBase() {
            this.mObservers = new ArrayList();
        }

        public void dump(PrintWriter pw, String prefix) {
            StringBuilder sb = new StringBuilder(RILConstants.RIL_REQUEST_SET_DATA_PROFILE);
            pw.print(prefix);
            pw.print("mRunning=");
            pw.println(this.mRunning);
            sb.setLength(BatteryStatsImpl.BATTERY_PLUGGED_NONE);
            sb.append(prefix);
            sb.append("mUptime=");
            BatteryStats.formatTimeMs(sb, this.mUptime / 1000);
            pw.println(sb.toString());
            sb.setLength(BatteryStatsImpl.BATTERY_PLUGGED_NONE);
            sb.append(prefix);
            sb.append("mRealtime=");
            BatteryStats.formatTimeMs(sb, this.mRealtime / 1000);
            pw.println(sb.toString());
            sb.setLength(BatteryStatsImpl.BATTERY_PLUGGED_NONE);
            sb.append(prefix);
            sb.append("mPastUptime=");
            BatteryStats.formatTimeMs(sb, this.mPastUptime / 1000);
            sb.append("mUptimeStart=");
            BatteryStats.formatTimeMs(sb, this.mUptimeStart / 1000);
            sb.append("mUnpluggedUptime=");
            BatteryStats.formatTimeMs(sb, this.mUnpluggedUptime / 1000);
            pw.println(sb.toString());
            sb.setLength(BatteryStatsImpl.BATTERY_PLUGGED_NONE);
            sb.append(prefix);
            sb.append("mPastRealtime=");
            BatteryStats.formatTimeMs(sb, this.mPastRealtime / 1000);
            sb.append("mRealtimeStart=");
            BatteryStats.formatTimeMs(sb, this.mRealtimeStart / 1000);
            sb.append("mUnpluggedRealtime=");
            BatteryStats.formatTimeMs(sb, this.mUnpluggedRealtime / 1000);
            pw.println(sb.toString());
        }

        public void add(TimeBaseObs observer) {
            this.mObservers.add(observer);
        }

        public void remove(TimeBaseObs observer) {
            if (!this.mObservers.remove(observer)) {
                Slog.wtf(BatteryStatsImpl.TAG, "Removed unknown observer: " + observer);
            }
        }

        public void init(long uptime, long realtime) {
            this.mRealtime = 0;
            this.mUptime = 0;
            this.mPastUptime = 0;
            this.mPastRealtime = 0;
            this.mUptimeStart = uptime;
            this.mRealtimeStart = realtime;
            this.mUnpluggedUptime = getUptime(this.mUptimeStart);
            this.mUnpluggedRealtime = getRealtime(this.mRealtimeStart);
        }

        public void reset(long uptime, long realtime) {
            if (this.mRunning) {
                this.mUptimeStart = uptime;
                this.mRealtimeStart = realtime;
                this.mUnpluggedUptime = getUptime(uptime);
                this.mUnpluggedRealtime = getRealtime(realtime);
                return;
            }
            this.mPastUptime = 0;
            this.mPastRealtime = 0;
        }

        public long computeUptime(long curTime, int which) {
            switch (which) {
                case BatteryStatsImpl.BATTERY_PLUGGED_NONE /*0*/:
                    return this.mUptime + getUptime(curTime);
                case BatteryStatsImpl.NET_UPDATE_MOBILE /*1*/:
                    return getUptime(curTime);
                case BatteryStatsImpl.NET_UPDATE_WIFI /*2*/:
                    return getUptime(curTime) - this.mUnpluggedUptime;
                default:
                    return 0;
            }
        }

        public long computeRealtime(long curTime, int which) {
            switch (which) {
                case BatteryStatsImpl.BATTERY_PLUGGED_NONE /*0*/:
                    return this.mRealtime + getRealtime(curTime);
                case BatteryStatsImpl.NET_UPDATE_MOBILE /*1*/:
                    return getRealtime(curTime);
                case BatteryStatsImpl.NET_UPDATE_WIFI /*2*/:
                    return getRealtime(curTime) - this.mUnpluggedRealtime;
                default:
                    return 0;
            }
        }

        public long getUptime(long curTime) {
            long time = this.mPastUptime;
            if (this.mRunning) {
                return time + (curTime - this.mUptimeStart);
            }
            return time;
        }

        public long getRealtime(long curTime) {
            long time = this.mPastRealtime;
            if (this.mRunning) {
                return time + (curTime - this.mRealtimeStart);
            }
            return time;
        }

        public long getUptimeStart() {
            return this.mUptimeStart;
        }

        public long getRealtimeStart() {
            return this.mRealtimeStart;
        }

        public boolean isRunning() {
            return this.mRunning;
        }

        public boolean setRunning(boolean running, long uptime, long realtime) {
            if (this.mRunning == running) {
                return BatteryStatsImpl.USE_OLD_HISTORY;
            }
            this.mRunning = running;
            long batteryUptime;
            long batteryRealtime;
            int i;
            if (running) {
                this.mUptimeStart = uptime;
                this.mRealtimeStart = realtime;
                batteryUptime = getUptime(uptime);
                this.mUnpluggedUptime = batteryUptime;
                batteryRealtime = getRealtime(realtime);
                this.mUnpluggedRealtime = batteryRealtime;
                for (i = this.mObservers.size() - 1; i >= 0; i--) {
                    ((TimeBaseObs) this.mObservers.get(i)).onTimeStarted(realtime, batteryUptime, batteryRealtime);
                }
            } else {
                this.mPastUptime += uptime - this.mUptimeStart;
                this.mPastRealtime += realtime - this.mRealtimeStart;
                batteryUptime = getUptime(uptime);
                batteryRealtime = getRealtime(realtime);
                for (i = this.mObservers.size() - 1; i >= 0; i--) {
                    ((TimeBaseObs) this.mObservers.get(i)).onTimeStopped(realtime, batteryUptime, batteryRealtime);
                }
            }
            return true;
        }

        public void readSummaryFromParcel(Parcel in) {
            this.mUptime = in.readLong();
            this.mRealtime = in.readLong();
        }

        public void writeSummaryToParcel(Parcel out, long uptime, long realtime) {
            out.writeLong(computeUptime(uptime, BatteryStatsImpl.BATTERY_PLUGGED_NONE));
            out.writeLong(computeRealtime(realtime, BatteryStatsImpl.BATTERY_PLUGGED_NONE));
        }

        public void readFromParcel(Parcel in) {
            this.mRunning = BatteryStatsImpl.USE_OLD_HISTORY;
            this.mUptime = in.readLong();
            this.mPastUptime = in.readLong();
            this.mUptimeStart = in.readLong();
            this.mRealtime = in.readLong();
            this.mPastRealtime = in.readLong();
            this.mRealtimeStart = in.readLong();
            this.mUnpluggedUptime = in.readLong();
            this.mUnpluggedRealtime = in.readLong();
        }

        public void writeToParcel(Parcel out, long uptime, long realtime) {
            long runningUptime = getUptime(uptime);
            long runningRealtime = getRealtime(realtime);
            out.writeLong(this.mUptime);
            out.writeLong(runningUptime);
            out.writeLong(this.mUptimeStart);
            out.writeLong(this.mRealtime);
            out.writeLong(runningRealtime);
            out.writeLong(this.mRealtimeStart);
            out.writeLong(this.mUnpluggedUptime);
            out.writeLong(this.mUnpluggedRealtime);
        }
    }

    public final class Uid extends android.os.BatteryStats.Uid {
        static final int NO_BATCHED_SCAN_STARTED = -1;
        static final int PROCESS_STATE_NONE = 3;
        StopwatchTimer mAudioTurnedOnTimer;
        StopwatchTimer mForegroundActivityTimer;
        boolean mFullWifiLockOut;
        StopwatchTimer mFullWifiLockTimer;
        final OverflowArrayMap<StopwatchTimer> mJobStats;
        LongSamplingCounter mMobileRadioActiveCount;
        LongSamplingCounter mMobileRadioActiveTime;
        LongSamplingCounter[] mNetworkByteActivityCounters;
        LongSamplingCounter[] mNetworkPacketActivityCounters;
        final ArrayMap<String, Pkg> mPackageStats;
        final SparseArray<Pid> mPids;
        int mProcessState;
        StopwatchTimer[] mProcessStateTimer;
        final ArrayMap<String, Proc> mProcessStats;
        final SparseArray<Sensor> mSensorStats;
        final OverflowArrayMap<StopwatchTimer> mSyncStats;
        final int mUid;
        Counter[] mUserActivityCounters;
        BatchTimer mVibratorOnTimer;
        StopwatchTimer mVideoTurnedOnTimer;
        final OverflowArrayMap<Wakelock> mWakelockStats;
        int mWifiBatchedScanBinStarted;
        StopwatchTimer[] mWifiBatchedScanTimer;
        boolean mWifiMulticastEnabled;
        StopwatchTimer mWifiMulticastTimer;
        boolean mWifiRunning;
        StopwatchTimer mWifiRunningTimer;
        boolean mWifiScanStarted;
        StopwatchTimer mWifiScanTimer;
        final /* synthetic */ BatteryStatsImpl this$0;

        /* renamed from: com.android.internal.os.BatteryStatsImpl.Uid.1 */
        class AnonymousClass1 extends OverflowArrayMap<Wakelock> {
            final /* synthetic */ Uid this$1;

            AnonymousClass1() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r1 = this;
                r1.this$1 = r2;
                r0 = r2.this$0;
                r1.<init>(r0);
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.1.<init>(com.android.internal.os.BatteryStatsImpl$Uid):void");
            }

            public /* bridge */ /* synthetic */ java.lang.Object m17instantiateObject() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r1 = this;
                r0 = r1.instantiateObject();
                return r0;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.1.instantiateObject():java.lang.Object");
            }

            public com.android.internal.os.BatteryStatsImpl.Uid.Wakelock instantiateObject() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r2 = this;
                r0 = new com.android.internal.os.BatteryStatsImpl$Uid$Wakelock;
                r1 = r2.this$1;
                r0.<init>(r1);
                return r0;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.1.instantiateObject():com.android.internal.os.BatteryStatsImpl$Uid$Wakelock");
            }
        }

        /* renamed from: com.android.internal.os.BatteryStatsImpl.Uid.2 */
        class AnonymousClass2 extends OverflowArrayMap<StopwatchTimer> {
            final /* synthetic */ Uid this$1;

            AnonymousClass2(com.android.internal.os.BatteryStatsImpl.Uid r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r1 = this;
                r1.this$1 = r2;
                r0 = r2.this$0;
                r1.<init>(r0);
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.2.<init>(com.android.internal.os.BatteryStatsImpl$Uid):void");
            }

            public /* bridge */ /* synthetic */ java.lang.Object m18instantiateObject() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r1 = this;
                r0 = r1.instantiateObject();
                return r0;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.2.instantiateObject():java.lang.Object");
            }

            public com.android.internal.os.BatteryStatsImpl.StopwatchTimer instantiateObject() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r5 = this;
                r0 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
                r1 = r5.this$1;
                r2 = 13;
                r3 = 0;
                r4 = r5.this$1;
                r4 = r4.this$0;
                r4 = r4.mOnBatteryTimeBase;
                r0.<init>(r1, r2, r3, r4);
                return r0;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.2.instantiateObject():com.android.internal.os.BatteryStatsImpl$StopwatchTimer");
            }
        }

        /* renamed from: com.android.internal.os.BatteryStatsImpl.Uid.3 */
        class AnonymousClass3 extends OverflowArrayMap<StopwatchTimer> {
            final /* synthetic */ Uid this$1;

            AnonymousClass3(com.android.internal.os.BatteryStatsImpl.Uid r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r1 = this;
                r1.this$1 = r2;
                r0 = r2.this$0;
                r1.<init>(r0);
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.3.<init>(com.android.internal.os.BatteryStatsImpl$Uid):void");
            }

            public /* bridge */ /* synthetic */ java.lang.Object m19instantiateObject() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r1 = this;
                r0 = r1.instantiateObject();
                return r0;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.3.instantiateObject():java.lang.Object");
            }

            public com.android.internal.os.BatteryStatsImpl.StopwatchTimer instantiateObject() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r5 = this;
                r0 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
                r1 = r5.this$1;
                r2 = 14;
                r3 = 0;
                r4 = r5.this$1;
                r4 = r4.this$0;
                r4 = r4.mOnBatteryTimeBase;
                r0.<init>(r1, r2, r3, r4);
                return r0;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.3.instantiateObject():com.android.internal.os.BatteryStatsImpl$StopwatchTimer");
            }
        }

        public final class Pkg extends android.os.BatteryStats.Uid.Pkg implements TimeBaseObs {
            int mLastWakeups;
            int mLoadedWakeups;
            final HashMap<String, Serv> mServiceStats;
            int mUnpluggedWakeups;
            int mWakeups;
            final /* synthetic */ Uid this$1;

            public final class Serv extends android.os.BatteryStats.Uid.Pkg.Serv implements TimeBaseObs {
                int mLastLaunches;
                long mLastStartTime;
                int mLastStarts;
                boolean mLaunched;
                long mLaunchedSince;
                long mLaunchedTime;
                int mLaunches;
                int mLoadedLaunches;
                long mLoadedStartTime;
                int mLoadedStarts;
                boolean mRunning;
                long mRunningSince;
                long mStartTime;
                int mStarts;
                int mUnpluggedLaunches;
                long mUnpluggedStartTime;
                int mUnpluggedStarts;
                final /* synthetic */ Pkg this$2;

                Serv(com.android.internal.os.BatteryStatsImpl.Uid.Pkg r2) {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                    /*
                    r1 = this;
                    r1.this$2 = r2;
                    r1.<init>(r2);
                    r0 = r2.this$1;
                    r0 = r0.this$0;
                    r0 = r0.mOnBatteryTimeBase;
                    r0.add(r1);
                    return;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.Serv.<init>(com.android.internal.os.BatteryStatsImpl$Uid$Pkg):void");
                }

                public void onTimeStarted(long r4, long r6, long r8) {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                    /*
                    r3 = this;
                    r0 = r3.getStartTimeToNowLocked(r6);
                    r3.mUnpluggedStartTime = r0;
                    r0 = r3.mStarts;
                    r3.mUnpluggedStarts = r0;
                    r0 = r3.mLaunches;
                    r3.mUnpluggedLaunches = r0;
                    return;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.Serv.onTimeStarted(long, long, long):void");
                }

                public void onTimeStopped(long r1, long r3, long r5) {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                    /*
                    r0 = this;
                    return;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.Serv.onTimeStopped(long, long, long):void");
                }

                void detach() {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                    /*
                    r1 = this;
                    r0 = r1.this$2;
                    r0 = r0.this$1;
                    r0 = r0.this$0;
                    r0 = r0.mOnBatteryTimeBase;
                    r0.remove(r1);
                    return;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.Serv.detach():void");
                }

                void readFromParcelLocked(android.os.Parcel r7) {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                    /*
                    r6 = this;
                    r1 = 1;
                    r2 = 0;
                    r4 = r7.readLong();
                    r6.mStartTime = r4;
                    r4 = r7.readLong();
                    r6.mRunningSince = r4;
                    r0 = r7.readInt();
                    if (r0 == 0) goto L_0x0064;
                L_0x0014:
                    r0 = r1;
                L_0x0015:
                    r6.mRunning = r0;
                    r0 = r7.readInt();
                    r6.mStarts = r0;
                    r4 = r7.readLong();
                    r6.mLaunchedTime = r4;
                    r4 = r7.readLong();
                    r6.mLaunchedSince = r4;
                    r0 = r7.readInt();
                    if (r0 == 0) goto L_0x0066;
                L_0x002f:
                    r6.mLaunched = r1;
                    r0 = r7.readInt();
                    r6.mLaunches = r0;
                    r0 = r7.readLong();
                    r6.mLoadedStartTime = r0;
                    r0 = r7.readInt();
                    r6.mLoadedStarts = r0;
                    r0 = r7.readInt();
                    r6.mLoadedLaunches = r0;
                    r0 = 0;
                    r6.mLastStartTime = r0;
                    r6.mLastStarts = r2;
                    r6.mLastLaunches = r2;
                    r0 = r7.readLong();
                    r6.mUnpluggedStartTime = r0;
                    r0 = r7.readInt();
                    r6.mUnpluggedStarts = r0;
                    r0 = r7.readInt();
                    r6.mUnpluggedLaunches = r0;
                    return;
                L_0x0064:
                    r0 = r2;
                    goto L_0x0015;
                L_0x0066:
                    r1 = r2;
                    goto L_0x002f;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.Serv.readFromParcelLocked(android.os.Parcel):void");
                }

                void writeToParcelLocked(android.os.Parcel r7) {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                    /*
                    r6 = this;
                    r1 = 1;
                    r2 = 0;
                    r4 = r6.mStartTime;
                    r7.writeLong(r4);
                    r4 = r6.mRunningSince;
                    r7.writeLong(r4);
                    r0 = r6.mRunning;
                    if (r0 == 0) goto L_0x004e;
                L_0x0010:
                    r0 = r1;
                L_0x0011:
                    r7.writeInt(r0);
                    r0 = r6.mStarts;
                    r7.writeInt(r0);
                    r4 = r6.mLaunchedTime;
                    r7.writeLong(r4);
                    r4 = r6.mLaunchedSince;
                    r7.writeLong(r4);
                    r0 = r6.mLaunched;
                    if (r0 == 0) goto L_0x0050;
                L_0x0027:
                    r7.writeInt(r1);
                    r0 = r6.mLaunches;
                    r7.writeInt(r0);
                    r0 = r6.mLoadedStartTime;
                    r7.writeLong(r0);
                    r0 = r6.mLoadedStarts;
                    r7.writeInt(r0);
                    r0 = r6.mLoadedLaunches;
                    r7.writeInt(r0);
                    r0 = r6.mUnpluggedStartTime;
                    r7.writeLong(r0);
                    r0 = r6.mUnpluggedStarts;
                    r7.writeInt(r0);
                    r0 = r6.mUnpluggedLaunches;
                    r7.writeInt(r0);
                    return;
                L_0x004e:
                    r0 = r2;
                    goto L_0x0011;
                L_0x0050:
                    r1 = r2;
                    goto L_0x0027;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.Serv.writeToParcelLocked(android.os.Parcel):void");
                }

                long getLaunchTimeToNowLocked(long r6) {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                    /*
                    r5 = this;
                    r0 = r5.mLaunched;
                    if (r0 != 0) goto L_0x0007;
                L_0x0004:
                    r0 = r5.mLaunchedTime;
                L_0x0006:
                    return r0;
                L_0x0007:
                    r0 = r5.mLaunchedTime;
                    r0 = r0 + r6;
                    r2 = r5.mLaunchedSince;
                    r0 = r0 - r2;
                    goto L_0x0006;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.Serv.getLaunchTimeToNowLocked(long):long");
                }

                long getStartTimeToNowLocked(long r6) {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                    /*
                    r5 = this;
                    r0 = r5.mRunning;
                    if (r0 != 0) goto L_0x0007;
                L_0x0004:
                    r0 = r5.mStartTime;
                L_0x0006:
                    return r0;
                L_0x0007:
                    r0 = r5.mStartTime;
                    r0 = r0 + r6;
                    r2 = r5.mRunningSince;
                    r0 = r0 - r2;
                    goto L_0x0006;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.Serv.getStartTimeToNowLocked(long):long");
                }

                public void startLaunchedLocked() {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                    /*
                    r2 = this;
                    r0 = r2.mLaunched;
                    if (r0 != 0) goto L_0x0019;
                L_0x0004:
                    r0 = r2.mLaunches;
                    r0 = r0 + 1;
                    r2.mLaunches = r0;
                    r0 = r2.this$2;
                    r0 = r0.this$1;
                    r0 = r0.this$0;
                    r0 = r0.getBatteryUptimeLocked();
                    r2.mLaunchedSince = r0;
                    r0 = 1;
                    r2.mLaunched = r0;
                L_0x0019:
                    return;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.Serv.startLaunchedLocked():void");
                }

                public void stopLaunchedLocked() {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                    /*
                    r6 = this;
                    r2 = r6.mLaunched;
                    if (r2 == 0) goto L_0x0020;
                L_0x0004:
                    r2 = r6.this$2;
                    r2 = r2.this$1;
                    r2 = r2.this$0;
                    r2 = r2.getBatteryUptimeLocked();
                    r4 = r6.mLaunchedSince;
                    r0 = r2 - r4;
                    r2 = 0;
                    r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
                    if (r2 <= 0) goto L_0x0021;
                L_0x0018:
                    r2 = r6.mLaunchedTime;
                    r2 = r2 + r0;
                    r6.mLaunchedTime = r2;
                L_0x001d:
                    r2 = 0;
                    r6.mLaunched = r2;
                L_0x0020:
                    return;
                L_0x0021:
                    r2 = r6.mLaunches;
                    r2 = r2 + -1;
                    r6.mLaunches = r2;
                    goto L_0x001d;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.Serv.stopLaunchedLocked():void");
                }

                public void startRunningLocked() {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                    /*
                    r2 = this;
                    r0 = r2.mRunning;
                    if (r0 != 0) goto L_0x0019;
                L_0x0004:
                    r0 = r2.mStarts;
                    r0 = r0 + 1;
                    r2.mStarts = r0;
                    r0 = r2.this$2;
                    r0 = r0.this$1;
                    r0 = r0.this$0;
                    r0 = r0.getBatteryUptimeLocked();
                    r2.mRunningSince = r0;
                    r0 = 1;
                    r2.mRunning = r0;
                L_0x0019:
                    return;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.Serv.startRunningLocked():void");
                }

                public void stopRunningLocked() {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                    /*
                    r6 = this;
                    r2 = r6.mRunning;
                    if (r2 == 0) goto L_0x0020;
                L_0x0004:
                    r2 = r6.this$2;
                    r2 = r2.this$1;
                    r2 = r2.this$0;
                    r2 = r2.getBatteryUptimeLocked();
                    r4 = r6.mRunningSince;
                    r0 = r2 - r4;
                    r2 = 0;
                    r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
                    if (r2 <= 0) goto L_0x0021;
                L_0x0018:
                    r2 = r6.mStartTime;
                    r2 = r2 + r0;
                    r6.mStartTime = r2;
                L_0x001d:
                    r2 = 0;
                    r6.mRunning = r2;
                L_0x0020:
                    return;
                L_0x0021:
                    r2 = r6.mStarts;
                    r2 = r2 + -1;
                    r6.mStarts = r2;
                    goto L_0x001d;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.Serv.stopRunningLocked():void");
                }

                public com.android.internal.os.BatteryStatsImpl getBatteryStats() {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                    /*
                    r1 = this;
                    r0 = r1.this$2;
                    r0 = r0.this$1;
                    r0 = r0.this$0;
                    return r0;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.Serv.getBatteryStats():com.android.internal.os.BatteryStatsImpl");
                }

                public int getLaunches(int r3) {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                    /*
                    r2 = this;
                    r0 = r2.mLaunches;
                    r1 = 1;
                    if (r3 != r1) goto L_0x0009;
                L_0x0005:
                    r1 = r2.mLoadedLaunches;
                    r0 = r0 - r1;
                L_0x0008:
                    return r0;
                L_0x0009:
                    r1 = 2;
                    if (r3 != r1) goto L_0x0008;
                L_0x000c:
                    r1 = r2.mUnpluggedLaunches;
                    r0 = r0 - r1;
                    goto L_0x0008;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.Serv.getLaunches(int):int");
                }

                public long getStartTime(long r6, int r8) {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                    /*
                    r5 = this;
                    r0 = r5.getStartTimeToNowLocked(r6);
                    r2 = 1;
                    if (r8 != r2) goto L_0x000b;
                L_0x0007:
                    r2 = r5.mLoadedStartTime;
                    r0 = r0 - r2;
                L_0x000a:
                    return r0;
                L_0x000b:
                    r2 = 2;
                    if (r8 != r2) goto L_0x000a;
                L_0x000e:
                    r2 = r5.mUnpluggedStartTime;
                    r0 = r0 - r2;
                    goto L_0x000a;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.Serv.getStartTime(long, int):long");
                }

                public int getStarts(int r3) {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                    /*
                    r2 = this;
                    r0 = r2.mStarts;
                    r1 = 1;
                    if (r3 != r1) goto L_0x0009;
                L_0x0005:
                    r1 = r2.mLoadedStarts;
                    r0 = r0 - r1;
                L_0x0008:
                    return r0;
                L_0x0009:
                    r1 = 2;
                    if (r3 != r1) goto L_0x0008;
                L_0x000c:
                    r1 = r2.mUnpluggedStarts;
                    r0 = r0 - r1;
                    goto L_0x0008;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.Serv.getStarts(int):int");
                }
            }

            Pkg(com.android.internal.os.BatteryStatsImpl.Uid r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r1 = this;
                r1.this$1 = r2;
                r1.<init>();
                r0 = new java.util.HashMap;
                r0.<init>();
                r1.mServiceStats = r0;
                r0 = r2.this$0;
                r0 = r0.mOnBatteryScreenOffTimeBase;
                r0.add(r1);
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.<init>(com.android.internal.os.BatteryStatsImpl$Uid):void");
            }

            public void onTimeStarted(long r2, long r4, long r6) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r1 = this;
                r0 = r1.mWakeups;
                r1.mUnpluggedWakeups = r0;
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.onTimeStarted(long, long, long):void");
            }

            public void onTimeStopped(long r1, long r3, long r5) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r0 = this;
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.onTimeStopped(long, long, long):void");
            }

            void detach() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r1 = this;
                r0 = r1.this$1;
                r0 = r0.this$0;
                r0 = r0.mOnBatteryScreenOffTimeBase;
                r0.remove(r1);
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.detach():void");
            }

            void readFromParcelLocked(android.os.Parcel r6) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r5 = this;
                r4 = r6.readInt();
                r5.mWakeups = r4;
                r4 = r6.readInt();
                r5.mLoadedWakeups = r4;
                r4 = 0;
                r5.mLastWakeups = r4;
                r4 = r6.readInt();
                r5.mUnpluggedWakeups = r4;
                r1 = r6.readInt();
                r4 = r5.mServiceStats;
                r4.clear();
                r0 = 0;
            L_0x001f:
                if (r0 >= r1) goto L_0x0035;
            L_0x0021:
                r3 = r6.readString();
                r2 = new com.android.internal.os.BatteryStatsImpl$Uid$Pkg$Serv;
                r2.<init>(r5);
                r4 = r5.mServiceStats;
                r4.put(r3, r2);
                r2.readFromParcelLocked(r6);
                r0 = r0 + 1;
                goto L_0x001f;
            L_0x0035:
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.readFromParcelLocked(android.os.Parcel):void");
            }

            void writeToParcelLocked(android.os.Parcel r5) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r4 = this;
                r3 = r4.mWakeups;
                r5.writeInt(r3);
                r3 = r4.mLoadedWakeups;
                r5.writeInt(r3);
                r3 = r4.mUnpluggedWakeups;
                r5.writeInt(r3);
                r3 = r4.mServiceStats;
                r3 = r3.size();
                r5.writeInt(r3);
                r3 = r4.mServiceStats;
                r3 = r3.entrySet();
                r0 = r3.iterator();
            L_0x0022:
                r3 = r0.hasNext();
                if (r3 == 0) goto L_0x0041;
            L_0x0028:
                r2 = r0.next();
                r2 = (java.util.Map.Entry) r2;
                r3 = r2.getKey();
                r3 = (java.lang.String) r3;
                r5.writeString(r3);
                r1 = r2.getValue();
                r1 = (com.android.internal.os.BatteryStatsImpl.Uid.Pkg.Serv) r1;
                r1.writeToParcelLocked(r5);
                goto L_0x0022;
            L_0x0041:
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.writeToParcelLocked(android.os.Parcel):void");
            }

            public java.util.Map<java.lang.String, ? extends android.os.BatteryStats.Uid.Pkg.Serv> getServiceStats() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r1 = this;
                r0 = r1.mServiceStats;
                return r0;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.getServiceStats():java.util.Map<java.lang.String, ? extends android.os.BatteryStats$Uid$Pkg$Serv>");
            }

            public int getWakeups(int r3) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r2 = this;
                r0 = r2.mWakeups;
                r1 = 1;
                if (r3 != r1) goto L_0x0009;
            L_0x0005:
                r1 = r2.mLoadedWakeups;
                r0 = r0 - r1;
            L_0x0008:
                return r0;
            L_0x0009:
                r1 = 2;
                if (r3 != r1) goto L_0x0008;
            L_0x000c:
                r1 = r2.mUnpluggedWakeups;
                r0 = r0 - r1;
                goto L_0x0008;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.getWakeups(int):int");
            }

            public com.android.internal.os.BatteryStatsImpl getBatteryStats() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r1 = this;
                r0 = r1.this$1;
                r0 = r0.this$0;
                return r0;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.getBatteryStats():com.android.internal.os.BatteryStatsImpl");
            }

            public void incWakeupsLocked() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r1 = this;
                r0 = r1.mWakeups;
                r0 = r0 + 1;
                r1.mWakeups = r0;
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.incWakeupsLocked():void");
            }

            final com.android.internal.os.BatteryStatsImpl.Uid.Pkg.Serv newServiceStatsLocked() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r1 = this;
                r0 = new com.android.internal.os.BatteryStatsImpl$Uid$Pkg$Serv;
                r0.<init>(r1);
                return r0;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Pkg.newServiceStatsLocked():com.android.internal.os.BatteryStatsImpl$Uid$Pkg$Serv");
            }
        }

        public final class Proc extends android.os.BatteryStats.Uid.Proc implements TimeBaseObs {
            boolean mActive;
            ArrayList<ExcessivePower> mExcessivePower;
            long mForegroundTime;
            long mLoadedForegroundTime;
            int mLoadedNumAnrs;
            int mLoadedNumCrashes;
            int mLoadedStarts;
            long mLoadedSystemTime;
            long mLoadedUserTime;
            final String mName;
            int mNumAnrs;
            int mNumCrashes;
            int mProcessState;
            SamplingCounter[] mSpeedBins;
            int mStarts;
            long mSystemTime;
            long mUnpluggedForegroundTime;
            int mUnpluggedNumAnrs;
            int mUnpluggedNumCrashes;
            int mUnpluggedStarts;
            long mUnpluggedSystemTime;
            long mUnpluggedUserTime;
            long mUserTime;
            final /* synthetic */ Uid this$1;

            Proc(com.android.internal.os.BatteryStatsImpl.Uid r2, java.lang.String r3) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r1 = this;
                r1.this$1 = r2;
                r1.<init>();
                r0 = 1;
                r1.mActive = r0;
                r0 = 3;
                r1.mProcessState = r0;
                r1.mName = r3;
                r0 = r2.this$0;
                r0 = r0.mOnBatteryTimeBase;
                r0.add(r1);
                r0 = r2.this$0;
                r0 = r0.getCpuSpeedSteps();
                r0 = new com.android.internal.os.BatteryStatsImpl.SamplingCounter[r0];
                r1.mSpeedBins = r0;
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.<init>(com.android.internal.os.BatteryStatsImpl$Uid, java.lang.String):void");
            }

            public void onTimeStarted(long r3, long r5, long r7) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r2 = this;
                r0 = r2.mUserTime;
                r2.mUnpluggedUserTime = r0;
                r0 = r2.mSystemTime;
                r2.mUnpluggedSystemTime = r0;
                r0 = r2.mForegroundTime;
                r2.mUnpluggedForegroundTime = r0;
                r0 = r2.mStarts;
                r2.mUnpluggedStarts = r0;
                r0 = r2.mNumCrashes;
                r2.mUnpluggedNumCrashes = r0;
                r0 = r2.mNumAnrs;
                r2.mUnpluggedNumAnrs = r0;
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.onTimeStarted(long, long, long):void");
            }

            public void onTimeStopped(long r1, long r3, long r5) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r0 = this;
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.onTimeStopped(long, long, long):void");
            }

            void reset() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r6 = this;
                r4 = 0;
                r3 = 0;
                r6.mForegroundTime = r4;
                r6.mSystemTime = r4;
                r6.mUserTime = r4;
                r6.mNumAnrs = r3;
                r6.mNumCrashes = r3;
                r6.mStarts = r3;
                r6.mLoadedForegroundTime = r4;
                r6.mLoadedSystemTime = r4;
                r6.mLoadedUserTime = r4;
                r6.mLoadedNumAnrs = r3;
                r6.mLoadedNumCrashes = r3;
                r6.mLoadedStarts = r3;
                r6.mUnpluggedForegroundTime = r4;
                r6.mUnpluggedSystemTime = r4;
                r6.mUnpluggedUserTime = r4;
                r6.mUnpluggedNumAnrs = r3;
                r6.mUnpluggedNumCrashes = r3;
                r6.mUnpluggedStarts = r3;
                r1 = 0;
            L_0x0028:
                r2 = r6.mSpeedBins;
                r2 = r2.length;
                if (r1 >= r2) goto L_0x0039;
            L_0x002d:
                r2 = r6.mSpeedBins;
                r0 = r2[r1];
                if (r0 == 0) goto L_0x0036;
            L_0x0033:
                r0.reset(r3);
            L_0x0036:
                r1 = r1 + 1;
                goto L_0x0028;
            L_0x0039:
                r2 = 0;
                r6.mExcessivePower = r2;
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.reset():void");
            }

            void detach() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r4 = this;
                r2 = 0;
                r4.mActive = r2;
                r2 = r4.this$1;
                r2 = r2.this$0;
                r2 = r2.mOnBatteryTimeBase;
                r2.remove(r4);
                r1 = 0;
            L_0x000d:
                r2 = r4.mSpeedBins;
                r2 = r2.length;
                if (r1 >= r2) goto L_0x0029;
            L_0x0012:
                r2 = r4.mSpeedBins;
                r0 = r2[r1];
                if (r0 == 0) goto L_0x0026;
            L_0x0018:
                r2 = r4.this$1;
                r2 = r2.this$0;
                r2 = r2.mOnBatteryTimeBase;
                r2.remove(r0);
                r2 = r4.mSpeedBins;
                r3 = 0;
                r2[r1] = r3;
            L_0x0026:
                r1 = r1 + 1;
                goto L_0x000d;
            L_0x0029:
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.detach():void");
            }

            public int countExcessivePowers() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r1 = this;
                r0 = r1.mExcessivePower;
                if (r0 == 0) goto L_0x000b;
            L_0x0004:
                r0 = r1.mExcessivePower;
                r0 = r0.size();
            L_0x000a:
                return r0;
            L_0x000b:
                r0 = 0;
                goto L_0x000a;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.countExcessivePowers():int");
            }

            public android.os.BatteryStats.Uid.Proc.ExcessivePower getExcessivePower(int r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r1 = this;
                r0 = r1.mExcessivePower;
                if (r0 == 0) goto L_0x000d;
            L_0x0004:
                r0 = r1.mExcessivePower;
                r0 = r0.get(r2);
                r0 = (android.os.BatteryStats.Uid.Proc.ExcessivePower) r0;
            L_0x000c:
                return r0;
            L_0x000d:
                r0 = 0;
                goto L_0x000c;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.getExcessivePower(int):android.os.BatteryStats$Uid$Proc$ExcessivePower");
            }

            public void addExcessiveWake(long r4, long r6) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r3 = this;
                r1 = r3.mExcessivePower;
                if (r1 != 0) goto L_0x000b;
            L_0x0004:
                r1 = new java.util.ArrayList;
                r1.<init>();
                r3.mExcessivePower = r1;
            L_0x000b:
                r0 = new android.os.BatteryStats$Uid$Proc$ExcessivePower;
                r0.<init>();
                r1 = 1;
                r0.type = r1;
                r0.overTime = r4;
                r0.usedTime = r6;
                r1 = r3.mExcessivePower;
                r1.add(r0);
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.addExcessiveWake(long, long):void");
            }

            public void addExcessiveCpu(long r4, long r6) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r3 = this;
                r1 = r3.mExcessivePower;
                if (r1 != 0) goto L_0x000b;
            L_0x0004:
                r1 = new java.util.ArrayList;
                r1.<init>();
                r3.mExcessivePower = r1;
            L_0x000b:
                r0 = new android.os.BatteryStats$Uid$Proc$ExcessivePower;
                r0.<init>();
                r1 = 2;
                r0.type = r1;
                r0.overTime = r4;
                r0.usedTime = r6;
                r1 = r3.mExcessivePower;
                r1.add(r0);
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.addExcessiveCpu(long, long):void");
            }

            void writeExcessivePowerToParcelLocked(android.os.Parcel r7) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r6 = this;
                r3 = r6.mExcessivePower;
                if (r3 != 0) goto L_0x0009;
            L_0x0004:
                r3 = 0;
                r7.writeInt(r3);
            L_0x0008:
                return;
            L_0x0009:
                r3 = r6.mExcessivePower;
                r0 = r3.size();
                r7.writeInt(r0);
                r2 = 0;
            L_0x0013:
                if (r2 >= r0) goto L_0x0008;
            L_0x0015:
                r3 = r6.mExcessivePower;
                r1 = r3.get(r2);
                r1 = (android.os.BatteryStats.Uid.Proc.ExcessivePower) r1;
                r3 = r1.type;
                r7.writeInt(r3);
                r4 = r1.overTime;
                r7.writeLong(r4);
                r4 = r1.usedTime;
                r7.writeLong(r4);
                r2 = r2 + 1;
                goto L_0x0013;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.writeExcessivePowerToParcelLocked(android.os.Parcel):void");
            }

            boolean readExcessivePowerFromParcelLocked(android.os.Parcel r7) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r6 = this;
                r3 = 1;
                r0 = r7.readInt();
                if (r0 != 0) goto L_0x000b;
            L_0x0007:
                r4 = 0;
                r6.mExcessivePower = r4;
            L_0x000a:
                return r3;
            L_0x000b:
                r4 = 10000; // 0x2710 float:1.4013E-41 double:4.9407E-320;
                if (r0 <= r4) goto L_0x0029;
            L_0x000f:
                r3 = "BatteryStatsImpl";
                r4 = new java.lang.StringBuilder;
                r4.<init>();
                r5 = "File corrupt: too many excessive power entries ";
                r4 = r4.append(r5);
                r4 = r4.append(r0);
                r4 = r4.toString();
                android.util.Slog.w(r3, r4);
                r3 = 0;
                goto L_0x000a;
            L_0x0029:
                r4 = new java.util.ArrayList;
                r4.<init>();
                r6.mExcessivePower = r4;
                r2 = 0;
            L_0x0031:
                if (r2 >= r0) goto L_0x000a;
            L_0x0033:
                r1 = new android.os.BatteryStats$Uid$Proc$ExcessivePower;
                r1.<init>();
                r4 = r7.readInt();
                r1.type = r4;
                r4 = r7.readLong();
                r1.overTime = r4;
                r4 = r7.readLong();
                r1.usedTime = r4;
                r4 = r6.mExcessivePower;
                r4.add(r1);
                r2 = r2 + 1;
                goto L_0x0031;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.readExcessivePowerFromParcelLocked(android.os.Parcel):boolean");
            }

            void writeToParcelLocked(android.os.Parcel r5) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r4 = this;
                r2 = r4.mUserTime;
                r5.writeLong(r2);
                r2 = r4.mSystemTime;
                r5.writeLong(r2);
                r2 = r4.mForegroundTime;
                r5.writeLong(r2);
                r2 = r4.mStarts;
                r5.writeInt(r2);
                r2 = r4.mNumCrashes;
                r5.writeInt(r2);
                r2 = r4.mNumAnrs;
                r5.writeInt(r2);
                r2 = r4.mLoadedUserTime;
                r5.writeLong(r2);
                r2 = r4.mLoadedSystemTime;
                r5.writeLong(r2);
                r2 = r4.mLoadedForegroundTime;
                r5.writeLong(r2);
                r2 = r4.mLoadedStarts;
                r5.writeInt(r2);
                r2 = r4.mLoadedNumCrashes;
                r5.writeInt(r2);
                r2 = r4.mLoadedNumAnrs;
                r5.writeInt(r2);
                r2 = r4.mUnpluggedUserTime;
                r5.writeLong(r2);
                r2 = r4.mUnpluggedSystemTime;
                r5.writeLong(r2);
                r2 = r4.mUnpluggedForegroundTime;
                r5.writeLong(r2);
                r2 = r4.mUnpluggedStarts;
                r5.writeInt(r2);
                r2 = r4.mUnpluggedNumCrashes;
                r5.writeInt(r2);
                r2 = r4.mUnpluggedNumAnrs;
                r5.writeInt(r2);
                r2 = r4.mSpeedBins;
                r2 = r2.length;
                r5.writeInt(r2);
                r1 = 0;
            L_0x0061:
                r2 = r4.mSpeedBins;
                r2 = r2.length;
                if (r1 >= r2) goto L_0x007b;
            L_0x0066:
                r2 = r4.mSpeedBins;
                r0 = r2[r1];
                if (r0 == 0) goto L_0x0076;
            L_0x006c:
                r2 = 1;
                r5.writeInt(r2);
                r0.writeToParcel(r5);
            L_0x0073:
                r1 = r1 + 1;
                goto L_0x0061;
            L_0x0076:
                r2 = 0;
                r5.writeInt(r2);
                goto L_0x0073;
            L_0x007b:
                r4.writeExcessivePowerToParcelLocked(r5);
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.writeToParcelLocked(android.os.Parcel):void");
            }

            void readFromParcelLocked(android.os.Parcel r7) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r6 = this;
                r4 = r7.readLong();
                r6.mUserTime = r4;
                r4 = r7.readLong();
                r6.mSystemTime = r4;
                r4 = r7.readLong();
                r6.mForegroundTime = r4;
                r3 = r7.readInt();
                r6.mStarts = r3;
                r3 = r7.readInt();
                r6.mNumCrashes = r3;
                r3 = r7.readInt();
                r6.mNumAnrs = r3;
                r4 = r7.readLong();
                r6.mLoadedUserTime = r4;
                r4 = r7.readLong();
                r6.mLoadedSystemTime = r4;
                r4 = r7.readLong();
                r6.mLoadedForegroundTime = r4;
                r3 = r7.readInt();
                r6.mLoadedStarts = r3;
                r3 = r7.readInt();
                r6.mLoadedNumCrashes = r3;
                r3 = r7.readInt();
                r6.mLoadedNumAnrs = r3;
                r4 = r7.readLong();
                r6.mUnpluggedUserTime = r4;
                r4 = r7.readLong();
                r6.mUnpluggedSystemTime = r4;
                r4 = r7.readLong();
                r6.mUnpluggedForegroundTime = r4;
                r3 = r7.readInt();
                r6.mUnpluggedStarts = r3;
                r3 = r7.readInt();
                r6.mUnpluggedNumCrashes = r3;
                r3 = r7.readInt();
                r6.mUnpluggedNumAnrs = r3;
                r0 = r7.readInt();
                r3 = r6.this$1;
                r3 = r3.this$0;
                r2 = r3.getCpuSpeedSteps();
                if (r0 < r2) goto L_0x007b;
            L_0x007a:
                r2 = r0;
            L_0x007b:
                r3 = new com.android.internal.os.BatteryStatsImpl.SamplingCounter[r2];
                r6.mSpeedBins = r3;
                r1 = 0;
            L_0x0080:
                if (r1 >= r0) goto L_0x009a;
            L_0x0082:
                r3 = r7.readInt();
                if (r3 == 0) goto L_0x0097;
            L_0x0088:
                r3 = r6.mSpeedBins;
                r4 = new com.android.internal.os.BatteryStatsImpl$SamplingCounter;
                r5 = r6.this$1;
                r5 = r5.this$0;
                r5 = r5.mOnBatteryTimeBase;
                r4.<init>(r5, r7);
                r3[r1] = r4;
            L_0x0097:
                r1 = r1 + 1;
                goto L_0x0080;
            L_0x009a:
                r6.readExcessivePowerFromParcelLocked(r7);
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.readFromParcelLocked(android.os.Parcel):void");
            }

            public com.android.internal.os.BatteryStatsImpl getBatteryStats() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r1 = this;
                r0 = r1.this$1;
                r0 = r0.this$0;
                return r0;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.getBatteryStats():com.android.internal.os.BatteryStatsImpl");
            }

            public void addCpuTimeLocked(int r5, int r6) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r4 = this;
                r0 = r4.mUserTime;
                r2 = (long) r5;
                r0 = r0 + r2;
                r4.mUserTime = r0;
                r0 = r4.mSystemTime;
                r2 = (long) r6;
                r0 = r0 + r2;
                r4.mSystemTime = r0;
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.addCpuTimeLocked(int, int):void");
            }

            public void addForegroundTimeLocked(long r4) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r3 = this;
                r0 = r3.mForegroundTime;
                r0 = r0 + r4;
                r3.mForegroundTime = r0;
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.addForegroundTimeLocked(long):void");
            }

            public void incStartsLocked() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r1 = this;
                r0 = r1.mStarts;
                r0 = r0 + 1;
                r1.mStarts = r0;
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.incStartsLocked():void");
            }

            public void incNumCrashesLocked() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r1 = this;
                r0 = r1.mNumCrashes;
                r0 = r0 + 1;
                r1.mNumCrashes = r0;
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.incNumCrashesLocked():void");
            }

            public void incNumAnrsLocked() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r1 = this;
                r0 = r1.mNumAnrs;
                r0 = r0 + 1;
                r1.mNumAnrs = r0;
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.incNumAnrsLocked():void");
            }

            public boolean isActive() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r1 = this;
                r0 = r1.mActive;
                return r0;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.isActive():boolean");
            }

            public long getUserTime(int r5) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r4 = this;
                r0 = r4.mUserTime;
                r2 = 1;
                if (r5 != r2) goto L_0x0009;
            L_0x0005:
                r2 = r4.mLoadedUserTime;
                r0 = r0 - r2;
            L_0x0008:
                return r0;
            L_0x0009:
                r2 = 2;
                if (r5 != r2) goto L_0x0008;
            L_0x000c:
                r2 = r4.mUnpluggedUserTime;
                r0 = r0 - r2;
                goto L_0x0008;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.getUserTime(int):long");
            }

            public long getSystemTime(int r5) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r4 = this;
                r0 = r4.mSystemTime;
                r2 = 1;
                if (r5 != r2) goto L_0x0009;
            L_0x0005:
                r2 = r4.mLoadedSystemTime;
                r0 = r0 - r2;
            L_0x0008:
                return r0;
            L_0x0009:
                r2 = 2;
                if (r5 != r2) goto L_0x0008;
            L_0x000c:
                r2 = r4.mUnpluggedSystemTime;
                r0 = r0 - r2;
                goto L_0x0008;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.getSystemTime(int):long");
            }

            public long getForegroundTime(int r5) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r4 = this;
                r0 = r4.mForegroundTime;
                r2 = 1;
                if (r5 != r2) goto L_0x0009;
            L_0x0005:
                r2 = r4.mLoadedForegroundTime;
                r0 = r0 - r2;
            L_0x0008:
                return r0;
            L_0x0009:
                r2 = 2;
                if (r5 != r2) goto L_0x0008;
            L_0x000c:
                r2 = r4.mUnpluggedForegroundTime;
                r0 = r0 - r2;
                goto L_0x0008;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.getForegroundTime(int):long");
            }

            public int getStarts(int r3) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r2 = this;
                r0 = r2.mStarts;
                r1 = 1;
                if (r3 != r1) goto L_0x0009;
            L_0x0005:
                r1 = r2.mLoadedStarts;
                r0 = r0 - r1;
            L_0x0008:
                return r0;
            L_0x0009:
                r1 = 2;
                if (r3 != r1) goto L_0x0008;
            L_0x000c:
                r1 = r2.mUnpluggedStarts;
                r0 = r0 - r1;
                goto L_0x0008;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.getStarts(int):int");
            }

            public int getNumCrashes(int r3) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r2 = this;
                r0 = r2.mNumCrashes;
                r1 = 1;
                if (r3 != r1) goto L_0x0009;
            L_0x0005:
                r1 = r2.mLoadedNumCrashes;
                r0 = r0 - r1;
            L_0x0008:
                return r0;
            L_0x0009:
                r1 = 2;
                if (r3 != r1) goto L_0x0008;
            L_0x000c:
                r1 = r2.mUnpluggedNumCrashes;
                r0 = r0 - r1;
                goto L_0x0008;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.getNumCrashes(int):int");
            }

            public int getNumAnrs(int r3) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r2 = this;
                r0 = r2.mNumAnrs;
                r1 = 1;
                if (r3 != r1) goto L_0x0009;
            L_0x0005:
                r1 = r2.mLoadedNumAnrs;
                r0 = r0 - r1;
            L_0x0008:
                return r0;
            L_0x0009:
                r1 = 2;
                if (r3 != r1) goto L_0x0008;
            L_0x000c:
                r1 = r2.mUnpluggedNumAnrs;
                r0 = r0 - r1;
                goto L_0x0008;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.getNumAnrs(int):int");
            }

            public void addSpeedStepTimes(long[] r7) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r6 = this;
                r3 = 0;
            L_0x0001:
                r4 = r6.mSpeedBins;
                r4 = r4.length;
                if (r3 >= r4) goto L_0x002e;
            L_0x0006:
                r4 = r7.length;
                if (r3 >= r4) goto L_0x002e;
            L_0x0009:
                r0 = r7[r3];
                r4 = 0;
                r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
                if (r4 == 0) goto L_0x002b;
            L_0x0011:
                r4 = r6.mSpeedBins;
                r2 = r4[r3];
                if (r2 != 0) goto L_0x0026;
            L_0x0017:
                r4 = r6.mSpeedBins;
                r2 = new com.android.internal.os.BatteryStatsImpl$SamplingCounter;
                r5 = r6.this$1;
                r5 = r5.this$0;
                r5 = r5.mOnBatteryTimeBase;
                r2.<init>(r5);
                r4[r3] = r2;
            L_0x0026:
                r4 = r7[r3];
                r2.addCountAtomic(r4);
            L_0x002b:
                r3 = r3 + 1;
                goto L_0x0001;
            L_0x002e:
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.addSpeedStepTimes(long[]):void");
            }

            public long getTimeAtCpuSpeedStep(int r5, int r6) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r4 = this;
                r2 = 0;
                r1 = r4.mSpeedBins;
                r1 = r1.length;
                if (r5 >= r1) goto L_0x0012;
            L_0x0007:
                r1 = r4.mSpeedBins;
                r0 = r1[r5];
                if (r0 == 0) goto L_0x0012;
            L_0x000d:
                r1 = r0.getCountLocked(r6);
                r2 = (long) r1;
            L_0x0012:
                return r2;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Proc.getTimeAtCpuSpeedStep(int, int):long");
            }
        }

        public final class Sensor extends android.os.BatteryStats.Uid.Sensor {
            final int mHandle;
            StopwatchTimer mTimer;
            final /* synthetic */ Uid this$1;

            public /* bridge */ /* synthetic */ android.os.BatteryStats.Timer getSensorTime() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r1 = this;
                r0 = r1.getSensorTime();
                return r0;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Sensor.getSensorTime():android.os.BatteryStats$Timer");
            }

            public Sensor(com.android.internal.os.BatteryStatsImpl.Uid r1, int r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r0 = this;
                r0.this$1 = r1;
                r0.<init>();
                r0.mHandle = r2;
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Sensor.<init>(com.android.internal.os.BatteryStatsImpl$Uid, int):void");
            }

            private com.android.internal.os.BatteryStatsImpl.StopwatchTimer readTimerFromParcel(com.android.internal.os.BatteryStatsImpl.TimeBase r7, android.os.Parcel r8) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r6 = this;
                r0 = r8.readInt();
                if (r0 != 0) goto L_0x0008;
            L_0x0006:
                r0 = 0;
            L_0x0007:
                return r0;
            L_0x0008:
                r0 = r6.this$1;
                r0 = r0.this$0;
                r0 = r0.mSensorTimers;
                r1 = r6.mHandle;
                r3 = r0.get(r1);
                r3 = (java.util.ArrayList) r3;
                if (r3 != 0) goto L_0x0028;
            L_0x0018:
                r3 = new java.util.ArrayList;
                r3.<init>();
                r0 = r6.this$1;
                r0 = r0.this$0;
                r0 = r0.mSensorTimers;
                r1 = r6.mHandle;
                r0.put(r1, r3);
            L_0x0028:
                r0 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
                r1 = r6.this$1;
                r2 = 0;
                r4 = r7;
                r5 = r8;
                r0.<init>(r1, r2, r3, r4, r5);
                goto L_0x0007;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Sensor.readTimerFromParcel(com.android.internal.os.BatteryStatsImpl$TimeBase, android.os.Parcel):com.android.internal.os.BatteryStatsImpl$StopwatchTimer");
            }

            boolean reset() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r2 = this;
                r0 = 1;
                r1 = r2.mTimer;
                r1 = r1.reset(r0);
                if (r1 == 0) goto L_0x000d;
            L_0x0009:
                r1 = 0;
                r2.mTimer = r1;
            L_0x000c:
                return r0;
            L_0x000d:
                r0 = 0;
                goto L_0x000c;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Sensor.reset():boolean");
            }

            void readFromParcelLocked(com.android.internal.os.BatteryStatsImpl.TimeBase r2, android.os.Parcel r3) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r1 = this;
                r0 = r1.readTimerFromParcel(r2, r3);
                r1.mTimer = r0;
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Sensor.readFromParcelLocked(com.android.internal.os.BatteryStatsImpl$TimeBase, android.os.Parcel):void");
            }

            void writeToParcelLocked(android.os.Parcel r3, long r4) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r2 = this;
                r0 = r2.mTimer;
                com.android.internal.os.BatteryStatsImpl.Timer.writeTimerToParcel(r3, r0, r4);
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Sensor.writeToParcelLocked(android.os.Parcel, long):void");
            }

            public com.android.internal.os.BatteryStatsImpl.Timer m20getSensorTime() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r1 = this;
                r0 = r1.mTimer;
                return r0;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Sensor.getSensorTime():com.android.internal.os.BatteryStatsImpl$Timer");
            }

            public int getHandle() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r1 = this;
                r0 = r1.mHandle;
                return r0;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Sensor.getHandle():int");
            }
        }

        public final class Wakelock extends android.os.BatteryStats.Uid.Wakelock {
            StopwatchTimer mTimerFull;
            StopwatchTimer mTimerPartial;
            StopwatchTimer mTimerWindow;
            final /* synthetic */ Uid this$1;

            public Wakelock(com.android.internal.os.BatteryStatsImpl.Uid r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r0 = this;
                r0.this$1 = r1;
                r0.<init>();
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Wakelock.<init>(com.android.internal.os.BatteryStatsImpl$Uid):void");
            }

            public /* bridge */ /* synthetic */ android.os.BatteryStats.Timer getWakeTime(int r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r1 = this;
                r0 = r1.getWakeTime(r2);
                return r0;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Wakelock.getWakeTime(int):android.os.BatteryStats$Timer");
            }

            private com.android.internal.os.BatteryStatsImpl.StopwatchTimer readTimerFromParcel(int r7, java.util.ArrayList<com.android.internal.os.BatteryStatsImpl.StopwatchTimer> r8, com.android.internal.os.BatteryStatsImpl.TimeBase r9, android.os.Parcel r10) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r6 = this;
                r0 = r10.readInt();
                if (r0 != 0) goto L_0x0008;
            L_0x0006:
                r0 = 0;
            L_0x0007:
                return r0;
            L_0x0008:
                r0 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
                r1 = r6.this$1;
                r2 = r7;
                r3 = r8;
                r4 = r9;
                r5 = r10;
                r0.<init>(r1, r2, r3, r4, r5);
                goto L_0x0007;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Wakelock.readTimerFromParcel(int, java.util.ArrayList, com.android.internal.os.BatteryStatsImpl$TimeBase, android.os.Parcel):com.android.internal.os.BatteryStatsImpl$StopwatchTimer");
            }

            boolean reset() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r5 = this;
                r4 = 0;
                r2 = 1;
                r3 = 0;
                r0 = 0;
                r1 = r5.mTimerFull;
                if (r1 == 0) goto L_0x0012;
            L_0x0008:
                r1 = r5.mTimerFull;
                r1 = r1.reset(r3);
                if (r1 != 0) goto L_0x0054;
            L_0x0010:
                r1 = r2;
            L_0x0011:
                r0 = r0 | r1;
            L_0x0012:
                r1 = r5.mTimerPartial;
                if (r1 == 0) goto L_0x0020;
            L_0x0016:
                r1 = r5.mTimerPartial;
                r1 = r1.reset(r3);
                if (r1 != 0) goto L_0x0056;
            L_0x001e:
                r1 = r2;
            L_0x001f:
                r0 = r0 | r1;
            L_0x0020:
                r1 = r5.mTimerWindow;
                if (r1 == 0) goto L_0x002e;
            L_0x0024:
                r1 = r5.mTimerWindow;
                r1 = r1.reset(r3);
                if (r1 != 0) goto L_0x0058;
            L_0x002c:
                r1 = r2;
            L_0x002d:
                r0 = r0 | r1;
            L_0x002e:
                if (r0 != 0) goto L_0x0051;
            L_0x0030:
                r1 = r5.mTimerFull;
                if (r1 == 0) goto L_0x003b;
            L_0x0034:
                r1 = r5.mTimerFull;
                r1.detach();
                r5.mTimerFull = r4;
            L_0x003b:
                r1 = r5.mTimerPartial;
                if (r1 == 0) goto L_0x0046;
            L_0x003f:
                r1 = r5.mTimerPartial;
                r1.detach();
                r5.mTimerPartial = r4;
            L_0x0046:
                r1 = r5.mTimerWindow;
                if (r1 == 0) goto L_0x0051;
            L_0x004a:
                r1 = r5.mTimerWindow;
                r1.detach();
                r5.mTimerWindow = r4;
            L_0x0051:
                if (r0 != 0) goto L_0x005a;
            L_0x0053:
                return r2;
            L_0x0054:
                r1 = r3;
                goto L_0x0011;
            L_0x0056:
                r1 = r3;
                goto L_0x001f;
            L_0x0058:
                r1 = r3;
                goto L_0x002d;
            L_0x005a:
                r2 = r3;
                goto L_0x0053;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Wakelock.reset():boolean");
            }

            void readFromParcelLocked(com.android.internal.os.BatteryStatsImpl.TimeBase r3, com.android.internal.os.BatteryStatsImpl.TimeBase r4, android.os.Parcel r5) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r2 = this;
                r0 = 0;
                r1 = r2.this$1;
                r1 = r1.this$0;
                r1 = r1.mPartialTimers;
                r0 = r2.readTimerFromParcel(r0, r1, r4, r5);
                r2.mTimerPartial = r0;
                r0 = 1;
                r1 = r2.this$1;
                r1 = r1.this$0;
                r1 = r1.mFullTimers;
                r0 = r2.readTimerFromParcel(r0, r1, r3, r5);
                r2.mTimerFull = r0;
                r0 = 2;
                r1 = r2.this$1;
                r1 = r1.this$0;
                r1 = r1.mWindowTimers;
                r0 = r2.readTimerFromParcel(r0, r1, r3, r5);
                r2.mTimerWindow = r0;
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Wakelock.readFromParcelLocked(com.android.internal.os.BatteryStatsImpl$TimeBase, com.android.internal.os.BatteryStatsImpl$TimeBase, android.os.Parcel):void");
            }

            void writeToParcelLocked(android.os.Parcel r3, long r4) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r2 = this;
                r0 = r2.mTimerPartial;
                com.android.internal.os.BatteryStatsImpl.Timer.writeTimerToParcel(r3, r0, r4);
                r0 = r2.mTimerFull;
                com.android.internal.os.BatteryStatsImpl.Timer.writeTimerToParcel(r3, r0, r4);
                r0 = r2.mTimerWindow;
                com.android.internal.os.BatteryStatsImpl.Timer.writeTimerToParcel(r3, r0, r4);
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Wakelock.writeToParcelLocked(android.os.Parcel, long):void");
            }

            public com.android.internal.os.BatteryStatsImpl.Timer m21getWakeTime(int r4) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r3 = this;
                switch(r4) {
                    case 0: goto L_0x001f;
                    case 1: goto L_0x001c;
                    case 2: goto L_0x0022;
                    default: goto L_0x0003;
                };
            L_0x0003:
                r0 = new java.lang.IllegalArgumentException;
                r1 = new java.lang.StringBuilder;
                r1.<init>();
                r2 = "type = ";
                r1 = r1.append(r2);
                r1 = r1.append(r4);
                r1 = r1.toString();
                r0.<init>(r1);
                throw r0;
            L_0x001c:
                r0 = r3.mTimerFull;
            L_0x001e:
                return r0;
            L_0x001f:
                r0 = r3.mTimerPartial;
                goto L_0x001e;
            L_0x0022:
                r0 = r3.mTimerWindow;
                goto L_0x001e;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Wakelock.getWakeTime(int):com.android.internal.os.BatteryStatsImpl$Timer");
            }

            public com.android.internal.os.BatteryStatsImpl.StopwatchTimer getStopwatchTimer(int r7) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r6 = this;
                switch(r7) {
                    case 0: goto L_0x001c;
                    case 1: goto L_0x0038;
                    case 2: goto L_0x0054;
                    default: goto L_0x0003;
                };
            L_0x0003:
                r2 = new java.lang.IllegalArgumentException;
                r3 = new java.lang.StringBuilder;
                r3.<init>();
                r4 = "type=";
                r3 = r3.append(r4);
                r3 = r3.append(r7);
                r3 = r3.toString();
                r2.<init>(r3);
                throw r2;
            L_0x001c:
                r0 = r6.mTimerPartial;
                if (r0 != 0) goto L_0x0036;
            L_0x0020:
                r0 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
                r2 = r6.this$1;
                r3 = 0;
                r4 = r6.this$1;
                r4 = r4.this$0;
                r4 = r4.mPartialTimers;
                r5 = r6.this$1;
                r5 = r5.this$0;
                r5 = r5.mOnBatteryScreenOffTimeBase;
                r0.<init>(r2, r3, r4, r5);
                r6.mTimerPartial = r0;
            L_0x0036:
                r1 = r0;
            L_0x0037:
                return r1;
            L_0x0038:
                r0 = r6.mTimerFull;
                if (r0 != 0) goto L_0x0052;
            L_0x003c:
                r0 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
                r2 = r6.this$1;
                r3 = 1;
                r4 = r6.this$1;
                r4 = r4.this$0;
                r4 = r4.mFullTimers;
                r5 = r6.this$1;
                r5 = r5.this$0;
                r5 = r5.mOnBatteryTimeBase;
                r0.<init>(r2, r3, r4, r5);
                r6.mTimerFull = r0;
            L_0x0052:
                r1 = r0;
                goto L_0x0037;
            L_0x0054:
                r0 = r6.mTimerWindow;
                if (r0 != 0) goto L_0x006e;
            L_0x0058:
                r0 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
                r2 = r6.this$1;
                r3 = 2;
                r4 = r6.this$1;
                r4 = r4.this$0;
                r4 = r4.mWindowTimers;
                r5 = r6.this$1;
                r5 = r5.this$0;
                r5 = r5.mOnBatteryTimeBase;
                r0.<init>(r2, r3, r4, r5);
                r6.mTimerWindow = r0;
            L_0x006e:
                r1 = r0;
                goto L_0x0037;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.Wakelock.getStopwatchTimer(int):com.android.internal.os.BatteryStatsImpl$StopwatchTimer");
            }
        }

        public /* bridge */ /* synthetic */ android.os.BatteryStats.Timer getForegroundActivityTimer() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.getForegroundActivityTimer();
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getForegroundActivityTimer():android.os.BatteryStats$Timer");
        }

        public /* bridge */ /* synthetic */ android.os.BatteryStats.Timer getVibratorOnTimer() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.getVibratorOnTimer();
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getVibratorOnTimer():android.os.BatteryStats$Timer");
        }

        public Uid(com.android.internal.os.BatteryStatsImpl r7, int r8) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r6 = this;
            r5 = 5;
            r4 = 3;
            r6.this$0 = r7;
            r6.<init>();
            r0 = -1;
            r6.mWifiBatchedScanBinStarted = r0;
            r6.mProcessState = r4;
            r0 = new com.android.internal.os.BatteryStatsImpl$Uid$1;
            r0.<init>();
            r6.mWakelockStats = r0;
            r0 = new com.android.internal.os.BatteryStatsImpl$Uid$2;
            r0.<init>(r6);
            r6.mSyncStats = r0;
            r0 = new com.android.internal.os.BatteryStatsImpl$Uid$3;
            r0.<init>(r6);
            r6.mJobStats = r0;
            r0 = new android.util.SparseArray;
            r0.<init>();
            r6.mSensorStats = r0;
            r0 = new android.util.ArrayMap;
            r0.<init>();
            r6.mProcessStats = r0;
            r0 = new android.util.ArrayMap;
            r0.<init>();
            r6.mPackageStats = r0;
            r0 = new android.util.SparseArray;
            r0.<init>();
            r6.mPids = r0;
            r6.mUid = r8;
            r0 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
            r1 = 4;
            r2 = r7.mWifiRunningTimers;
            r3 = r7.mOnBatteryTimeBase;
            r0.<init>(r6, r1, r2, r3);
            r6.mWifiRunningTimer = r0;
            r0 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
            r1 = r7.mFullWifiLockTimers;
            r2 = r7.mOnBatteryTimeBase;
            r0.<init>(r6, r5, r1, r2);
            r6.mFullWifiLockTimer = r0;
            r0 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
            r1 = 6;
            r2 = r7.mWifiScanTimers;
            r3 = r7.mOnBatteryTimeBase;
            r0.<init>(r6, r1, r2, r3);
            r6.mWifiScanTimer = r0;
            r0 = new com.android.internal.os.BatteryStatsImpl.StopwatchTimer[r5];
            r6.mWifiBatchedScanTimer = r0;
            r0 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
            r1 = 7;
            r2 = r7.mWifiMulticastTimers;
            r3 = r7.mOnBatteryTimeBase;
            r0.<init>(r6, r1, r2, r3);
            r6.mWifiMulticastTimer = r0;
            r0 = new com.android.internal.os.BatteryStatsImpl.StopwatchTimer[r4];
            r6.mProcessStateTimer = r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.<init>(com.android.internal.os.BatteryStatsImpl, int):void");
        }

        public java.util.Map<java.lang.String, ? extends android.os.BatteryStats.Uid.Wakelock> getWakelockStats() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.mWakelockStats;
            r0 = r0.getMap();
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getWakelockStats():java.util.Map<java.lang.String, ? extends android.os.BatteryStats$Uid$Wakelock>");
        }

        public java.util.Map<java.lang.String, ? extends android.os.BatteryStats.Timer> getSyncStats() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.mSyncStats;
            r0 = r0.getMap();
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getSyncStats():java.util.Map<java.lang.String, ? extends android.os.BatteryStats$Timer>");
        }

        public java.util.Map<java.lang.String, ? extends android.os.BatteryStats.Timer> getJobStats() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.mJobStats;
            r0 = r0.getMap();
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getJobStats():java.util.Map<java.lang.String, ? extends android.os.BatteryStats$Timer>");
        }

        public android.util.SparseArray<? extends android.os.BatteryStats.Uid.Sensor> getSensorStats() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.mSensorStats;
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getSensorStats():android.util.SparseArray<? extends android.os.BatteryStats$Uid$Sensor>");
        }

        public java.util.Map<java.lang.String, ? extends android.os.BatteryStats.Uid.Proc> getProcessStats() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.mProcessStats;
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getProcessStats():java.util.Map<java.lang.String, ? extends android.os.BatteryStats$Uid$Proc>");
        }

        public java.util.Map<java.lang.String, ? extends android.os.BatteryStats.Uid.Pkg> getPackageStats() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.mPackageStats;
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getPackageStats():java.util.Map<java.lang.String, ? extends android.os.BatteryStats$Uid$Pkg>");
        }

        public int getUid() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.mUid;
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getUid():int");
        }

        public void noteWifiRunningLocked(long r6) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r5 = this;
            r0 = r5.mWifiRunning;
            if (r0 != 0) goto L_0x0020;
        L_0x0004:
            r0 = 1;
            r5.mWifiRunning = r0;
            r0 = r5.mWifiRunningTimer;
            if (r0 != 0) goto L_0x001b;
        L_0x000b:
            r0 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
            r1 = 4;
            r2 = r5.this$0;
            r2 = r2.mWifiRunningTimers;
            r3 = r5.this$0;
            r3 = r3.mOnBatteryTimeBase;
            r0.<init>(r5, r1, r2, r3);
            r5.mWifiRunningTimer = r0;
        L_0x001b:
            r0 = r5.mWifiRunningTimer;
            r0.startRunningLocked(r6);
        L_0x0020:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteWifiRunningLocked(long):void");
        }

        public void noteWifiStoppedLocked(long r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.mWifiRunning;
            if (r0 == 0) goto L_0x000c;
        L_0x0004:
            r0 = 0;
            r1.mWifiRunning = r0;
            r0 = r1.mWifiRunningTimer;
            r0.stopRunningLocked(r2);
        L_0x000c:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteWifiStoppedLocked(long):void");
        }

        public void noteFullWifiLockAcquiredLocked(long r6) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r5 = this;
            r0 = r5.mFullWifiLockOut;
            if (r0 != 0) goto L_0x0020;
        L_0x0004:
            r0 = 1;
            r5.mFullWifiLockOut = r0;
            r0 = r5.mFullWifiLockTimer;
            if (r0 != 0) goto L_0x001b;
        L_0x000b:
            r0 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
            r1 = 5;
            r2 = r5.this$0;
            r2 = r2.mFullWifiLockTimers;
            r3 = r5.this$0;
            r3 = r3.mOnBatteryTimeBase;
            r0.<init>(r5, r1, r2, r3);
            r5.mFullWifiLockTimer = r0;
        L_0x001b:
            r0 = r5.mFullWifiLockTimer;
            r0.startRunningLocked(r6);
        L_0x0020:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteFullWifiLockAcquiredLocked(long):void");
        }

        public void noteFullWifiLockReleasedLocked(long r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.mFullWifiLockOut;
            if (r0 == 0) goto L_0x000c;
        L_0x0004:
            r0 = 0;
            r1.mFullWifiLockOut = r0;
            r0 = r1.mFullWifiLockTimer;
            r0.stopRunningLocked(r2);
        L_0x000c:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteFullWifiLockReleasedLocked(long):void");
        }

        public void noteWifiScanStartedLocked(long r6) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r5 = this;
            r0 = r5.mWifiScanStarted;
            if (r0 != 0) goto L_0x0020;
        L_0x0004:
            r0 = 1;
            r5.mWifiScanStarted = r0;
            r0 = r5.mWifiScanTimer;
            if (r0 != 0) goto L_0x001b;
        L_0x000b:
            r0 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
            r1 = 6;
            r2 = r5.this$0;
            r2 = r2.mWifiScanTimers;
            r3 = r5.this$0;
            r3 = r3.mOnBatteryTimeBase;
            r0.<init>(r5, r1, r2, r3);
            r5.mWifiScanTimer = r0;
        L_0x001b:
            r0 = r5.mWifiScanTimer;
            r0.startRunningLocked(r6);
        L_0x0020:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteWifiScanStartedLocked(long):void");
        }

        public void noteWifiScanStoppedLocked(long r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.mWifiScanStarted;
            if (r0 == 0) goto L_0x000c;
        L_0x0004:
            r0 = 0;
            r1.mWifiScanStarted = r0;
            r0 = r1.mWifiScanTimer;
            r0.stopRunningLocked(r2);
        L_0x000c:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteWifiScanStoppedLocked(long):void");
        }

        public void noteWifiBatchedScanStartedLocked(int r5, long r6) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r4 = this;
            r0 = 0;
        L_0x0001:
            r1 = 8;
            if (r5 <= r1) goto L_0x000d;
        L_0x0005:
            r1 = 5;
            if (r0 >= r1) goto L_0x000d;
        L_0x0008:
            r5 = r5 >> 3;
            r0 = r0 + 1;
            goto L_0x0001;
        L_0x000d:
            r1 = r4.mWifiBatchedScanBinStarted;
            if (r1 != r0) goto L_0x0012;
        L_0x0011:
            return;
        L_0x0012:
            r1 = r4.mWifiBatchedScanBinStarted;
            r2 = -1;
            if (r1 == r2) goto L_0x0020;
        L_0x0017:
            r1 = r4.mWifiBatchedScanTimer;
            r2 = r4.mWifiBatchedScanBinStarted;
            r1 = r1[r2];
            r1.stopRunningLocked(r6);
        L_0x0020:
            r4.mWifiBatchedScanBinStarted = r0;
            r1 = r4.mWifiBatchedScanTimer;
            r1 = r1[r0];
            if (r1 != 0) goto L_0x002c;
        L_0x0028:
            r1 = 0;
            r4.makeWifiBatchedScanBin(r0, r1);
        L_0x002c:
            r1 = r4.mWifiBatchedScanTimer;
            r1 = r1[r0];
            r1.startRunningLocked(r6);
            goto L_0x0011;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteWifiBatchedScanStartedLocked(int, long):void");
        }

        public void noteWifiBatchedScanStoppedLocked(long r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r3 = this;
            r2 = -1;
            r0 = r3.mWifiBatchedScanBinStarted;
            if (r0 == r2) goto L_0x0010;
        L_0x0005:
            r0 = r3.mWifiBatchedScanTimer;
            r1 = r3.mWifiBatchedScanBinStarted;
            r0 = r0[r1];
            r0.stopRunningLocked(r4);
            r3.mWifiBatchedScanBinStarted = r2;
        L_0x0010:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteWifiBatchedScanStoppedLocked(long):void");
        }

        public void noteWifiMulticastEnabledLocked(long r6) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r5 = this;
            r0 = r5.mWifiMulticastEnabled;
            if (r0 != 0) goto L_0x0020;
        L_0x0004:
            r0 = 1;
            r5.mWifiMulticastEnabled = r0;
            r0 = r5.mWifiMulticastTimer;
            if (r0 != 0) goto L_0x001b;
        L_0x000b:
            r0 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
            r1 = 7;
            r2 = r5.this$0;
            r2 = r2.mWifiMulticastTimers;
            r3 = r5.this$0;
            r3 = r3.mOnBatteryTimeBase;
            r0.<init>(r5, r1, r2, r3);
            r5.mWifiMulticastTimer = r0;
        L_0x001b:
            r0 = r5.mWifiMulticastTimer;
            r0.startRunningLocked(r6);
        L_0x0020:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteWifiMulticastEnabledLocked(long):void");
        }

        public void noteWifiMulticastDisabledLocked(long r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.mWifiMulticastEnabled;
            if (r0 == 0) goto L_0x000c;
        L_0x0004:
            r0 = 0;
            r1.mWifiMulticastEnabled = r0;
            r0 = r1.mWifiMulticastTimer;
            r0.stopRunningLocked(r2);
        L_0x000c:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteWifiMulticastDisabledLocked(long):void");
        }

        public com.android.internal.os.BatteryStatsImpl.StopwatchTimer createAudioTurnedOnTimerLocked() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r4 = this;
            r0 = r4.mAudioTurnedOnTimer;
            if (r0 != 0) goto L_0x0015;
        L_0x0004:
            r0 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
            r1 = 15;
            r2 = r4.this$0;
            r2 = r2.mAudioTurnedOnTimers;
            r3 = r4.this$0;
            r3 = r3.mOnBatteryTimeBase;
            r0.<init>(r4, r1, r2, r3);
            r4.mAudioTurnedOnTimer = r0;
        L_0x0015:
            r0 = r4.mAudioTurnedOnTimer;
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.createAudioTurnedOnTimerLocked():com.android.internal.os.BatteryStatsImpl$StopwatchTimer");
        }

        public void noteAudioTurnedOnLocked(long r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.createAudioTurnedOnTimerLocked();
            r0.startRunningLocked(r2);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteAudioTurnedOnLocked(long):void");
        }

        public void noteAudioTurnedOffLocked(long r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.mAudioTurnedOnTimer;
            if (r0 == 0) goto L_0x0009;
        L_0x0004:
            r0 = r1.mAudioTurnedOnTimer;
            r0.stopRunningLocked(r2);
        L_0x0009:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteAudioTurnedOffLocked(long):void");
        }

        public void noteResetAudioLocked(long r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.mAudioTurnedOnTimer;
            if (r0 == 0) goto L_0x0009;
        L_0x0004:
            r0 = r1.mAudioTurnedOnTimer;
            r0.stopAllRunningLocked(r2);
        L_0x0009:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteResetAudioLocked(long):void");
        }

        public com.android.internal.os.BatteryStatsImpl.StopwatchTimer createVideoTurnedOnTimerLocked() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r4 = this;
            r0 = r4.mVideoTurnedOnTimer;
            if (r0 != 0) goto L_0x0015;
        L_0x0004:
            r0 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
            r1 = 8;
            r2 = r4.this$0;
            r2 = r2.mVideoTurnedOnTimers;
            r3 = r4.this$0;
            r3 = r3.mOnBatteryTimeBase;
            r0.<init>(r4, r1, r2, r3);
            r4.mVideoTurnedOnTimer = r0;
        L_0x0015:
            r0 = r4.mVideoTurnedOnTimer;
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.createVideoTurnedOnTimerLocked():com.android.internal.os.BatteryStatsImpl$StopwatchTimer");
        }

        public void noteVideoTurnedOnLocked(long r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.createVideoTurnedOnTimerLocked();
            r0.startRunningLocked(r2);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteVideoTurnedOnLocked(long):void");
        }

        public void noteVideoTurnedOffLocked(long r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.mVideoTurnedOnTimer;
            if (r0 == 0) goto L_0x0009;
        L_0x0004:
            r0 = r1.mVideoTurnedOnTimer;
            r0.stopRunningLocked(r2);
        L_0x0009:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteVideoTurnedOffLocked(long):void");
        }

        public void noteResetVideoLocked(long r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.mVideoTurnedOnTimer;
            if (r0 == 0) goto L_0x0009;
        L_0x0004:
            r0 = r1.mVideoTurnedOnTimer;
            r0.stopAllRunningLocked(r2);
        L_0x0009:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteResetVideoLocked(long):void");
        }

        public com.android.internal.os.BatteryStatsImpl.StopwatchTimer createForegroundActivityTimerLocked() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r4 = this;
            r0 = r4.mForegroundActivityTimer;
            if (r0 != 0) goto L_0x0012;
        L_0x0004:
            r0 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
            r1 = 10;
            r2 = 0;
            r3 = r4.this$0;
            r3 = r3.mOnBatteryTimeBase;
            r0.<init>(r4, r1, r2, r3);
            r4.mForegroundActivityTimer = r0;
        L_0x0012:
            r0 = r4.mForegroundActivityTimer;
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.createForegroundActivityTimerLocked():com.android.internal.os.BatteryStatsImpl$StopwatchTimer");
        }

        public void noteActivityResumedLocked(long r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.createForegroundActivityTimerLocked();
            r0.startRunningLocked(r2);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteActivityResumedLocked(long):void");
        }

        public void noteActivityPausedLocked(long r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.mForegroundActivityTimer;
            if (r0 == 0) goto L_0x0009;
        L_0x0004:
            r0 = r1.mForegroundActivityTimer;
            r0.stopRunningLocked(r2);
        L_0x0009:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteActivityPausedLocked(long):void");
        }

        void updateUidProcessStateLocked(int r5, long r6) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r4 = this;
            r2 = 3;
            r0 = r4.mProcessState;
            if (r0 != r5) goto L_0x0006;
        L_0x0005:
            return;
        L_0x0006:
            r0 = r4.mProcessState;
            if (r0 == r2) goto L_0x0013;
        L_0x000a:
            r0 = r4.mProcessStateTimer;
            r1 = r4.mProcessState;
            r0 = r0[r1];
            r0.stopRunningLocked(r6);
        L_0x0013:
            r4.mProcessState = r5;
            if (r5 == r2) goto L_0x0005;
        L_0x0017:
            r0 = r4.mProcessStateTimer;
            r0 = r0[r5];
            if (r0 != 0) goto L_0x0021;
        L_0x001d:
            r0 = 0;
            r4.makeProcessState(r5, r0);
        L_0x0021:
            r0 = r4.mProcessStateTimer;
            r0 = r0[r5];
            r0.startRunningLocked(r6);
            goto L_0x0005;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.updateUidProcessStateLocked(int, long):void");
        }

        public com.android.internal.os.BatteryStatsImpl.BatchTimer createVibratorOnTimerLocked() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r3 = this;
            r0 = r3.mVibratorOnTimer;
            if (r0 != 0) goto L_0x0011;
        L_0x0004:
            r0 = new com.android.internal.os.BatteryStatsImpl$BatchTimer;
            r1 = 9;
            r2 = r3.this$0;
            r2 = r2.mOnBatteryTimeBase;
            r0.<init>(r3, r1, r2);
            r3.mVibratorOnTimer = r0;
        L_0x0011:
            r0 = r3.mVibratorOnTimer;
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.createVibratorOnTimerLocked():com.android.internal.os.BatteryStatsImpl$BatchTimer");
        }

        public void noteVibratorOnLocked(long r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r3 = this;
            r0 = r3.createVibratorOnTimerLocked();
            r1 = r3.this$0;
            r0.addDuration(r1, r4);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteVibratorOnLocked(long):void");
        }

        public void noteVibratorOffLocked() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r2 = this;
            r0 = r2.mVibratorOnTimer;
            if (r0 == 0) goto L_0x000b;
        L_0x0004:
            r0 = r2.mVibratorOnTimer;
            r1 = r2.this$0;
            r0.abortLastDuration(r1);
        L_0x000b:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteVibratorOffLocked():void");
        }

        public long getWifiRunningTime(long r4, int r6) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r3 = this;
            r0 = r3.mWifiRunningTimer;
            if (r0 != 0) goto L_0x0007;
        L_0x0004:
            r0 = 0;
        L_0x0006:
            return r0;
        L_0x0007:
            r0 = r3.mWifiRunningTimer;
            r0 = r0.getTotalTimeLocked(r4, r6);
            goto L_0x0006;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getWifiRunningTime(long, int):long");
        }

        public long getFullWifiLockTime(long r4, int r6) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r3 = this;
            r0 = r3.mFullWifiLockTimer;
            if (r0 != 0) goto L_0x0007;
        L_0x0004:
            r0 = 0;
        L_0x0006:
            return r0;
        L_0x0007:
            r0 = r3.mFullWifiLockTimer;
            r0 = r0.getTotalTimeLocked(r4, r6);
            goto L_0x0006;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getFullWifiLockTime(long, int):long");
        }

        public long getWifiScanTime(long r4, int r6) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r3 = this;
            r0 = r3.mWifiScanTimer;
            if (r0 != 0) goto L_0x0007;
        L_0x0004:
            r0 = 0;
        L_0x0006:
            return r0;
        L_0x0007:
            r0 = r3.mWifiScanTimer;
            r0 = r0.getTotalTimeLocked(r4, r6);
            goto L_0x0006;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getWifiScanTime(long, int):long");
        }

        public long getWifiBatchedScanTime(int r5, long r6, int r8) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r4 = this;
            r0 = 0;
            if (r5 < 0) goto L_0x0007;
        L_0x0004:
            r2 = 5;
            if (r5 < r2) goto L_0x0008;
        L_0x0007:
            return r0;
        L_0x0008:
            r2 = r4.mWifiBatchedScanTimer;
            r2 = r2[r5];
            if (r2 == 0) goto L_0x0007;
        L_0x000e:
            r0 = r4.mWifiBatchedScanTimer;
            r0 = r0[r5];
            r0 = r0.getTotalTimeLocked(r6, r8);
            goto L_0x0007;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getWifiBatchedScanTime(int, long, int):long");
        }

        public long getWifiMulticastTime(long r4, int r6) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r3 = this;
            r0 = r3.mWifiMulticastTimer;
            if (r0 != 0) goto L_0x0007;
        L_0x0004:
            r0 = 0;
        L_0x0006:
            return r0;
        L_0x0007:
            r0 = r3.mWifiMulticastTimer;
            r0 = r0.getTotalTimeLocked(r4, r6);
            goto L_0x0006;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getWifiMulticastTime(long, int):long");
        }

        public long getAudioTurnedOnTime(long r4, int r6) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r3 = this;
            r0 = r3.mAudioTurnedOnTimer;
            if (r0 != 0) goto L_0x0007;
        L_0x0004:
            r0 = 0;
        L_0x0006:
            return r0;
        L_0x0007:
            r0 = r3.mAudioTurnedOnTimer;
            r0 = r0.getTotalTimeLocked(r4, r6);
            goto L_0x0006;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getAudioTurnedOnTime(long, int):long");
        }

        public long getVideoTurnedOnTime(long r4, int r6) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r3 = this;
            r0 = r3.mVideoTurnedOnTimer;
            if (r0 != 0) goto L_0x0007;
        L_0x0004:
            r0 = 0;
        L_0x0006:
            return r0;
        L_0x0007:
            r0 = r3.mVideoTurnedOnTimer;
            r0 = r0.getTotalTimeLocked(r4, r6);
            goto L_0x0006;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getVideoTurnedOnTime(long, int):long");
        }

        public com.android.internal.os.BatteryStatsImpl.Timer m15getForegroundActivityTimer() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.mForegroundActivityTimer;
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getForegroundActivityTimer():com.android.internal.os.BatteryStatsImpl$Timer");
        }

        void makeProcessState(int r8, android.os.Parcel r9) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r7 = this;
            r3 = 0;
            r2 = 12;
            if (r8 < 0) goto L_0x0008;
        L_0x0005:
            r0 = 3;
            if (r8 < r0) goto L_0x0009;
        L_0x0008:
            return;
        L_0x0009:
            if (r9 != 0) goto L_0x0019;
        L_0x000b:
            r0 = r7.mProcessStateTimer;
            r1 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
            r4 = r7.this$0;
            r4 = r4.mOnBatteryTimeBase;
            r1.<init>(r7, r2, r3, r4);
            r0[r8] = r1;
            goto L_0x0008;
        L_0x0019:
            r6 = r7.mProcessStateTimer;
            r0 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
            r1 = r7.this$0;
            r4 = r1.mOnBatteryTimeBase;
            r1 = r7;
            r5 = r9;
            r0.<init>(r1, r2, r3, r4, r5);
            r6[r8] = r0;
            goto L_0x0008;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.makeProcessState(int, android.os.Parcel):void");
        }

        public long getProcessStateTime(int r5, long r6, int r8) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r4 = this;
            r0 = 0;
            if (r5 < 0) goto L_0x0007;
        L_0x0004:
            r2 = 3;
            if (r5 < r2) goto L_0x0008;
        L_0x0007:
            return r0;
        L_0x0008:
            r2 = r4.mProcessStateTimer;
            r2 = r2[r5];
            if (r2 == 0) goto L_0x0007;
        L_0x000e:
            r0 = r4.mProcessStateTimer;
            r0 = r0[r5];
            r0 = r0.getTotalTimeLocked(r6, r8);
            goto L_0x0007;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getProcessStateTime(int, long, int):long");
        }

        public com.android.internal.os.BatteryStatsImpl.Timer m16getVibratorOnTimer() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.mVibratorOnTimer;
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getVibratorOnTimer():com.android.internal.os.BatteryStatsImpl$Timer");
        }

        public void noteUserActivityLocked(int r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r3 = this;
            r0 = r3.mUserActivityCounters;
            if (r0 != 0) goto L_0x0007;
        L_0x0004:
            r3.initUserActivityLocked();
        L_0x0007:
            if (r4 < 0) goto L_0x0014;
        L_0x0009:
            r0 = 3;
            if (r4 >= r0) goto L_0x0014;
        L_0x000c:
            r0 = r3.mUserActivityCounters;
            r0 = r0[r4];
            r0.stepAtomic();
        L_0x0013:
            return;
        L_0x0014:
            r0 = "BatteryStatsImpl";
            r1 = new java.lang.StringBuilder;
            r1.<init>();
            r2 = "Unknown user activity type ";
            r1 = r1.append(r2);
            r1 = r1.append(r4);
            r2 = " was specified.";
            r1 = r1.append(r2);
            r1 = r1.toString();
            r2 = new java.lang.Throwable;
            r2.<init>();
            android.util.Slog.w(r0, r1, r2);
            goto L_0x0013;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteUserActivityLocked(int):void");
        }

        public boolean hasUserActivity() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.mUserActivityCounters;
            if (r0 == 0) goto L_0x0006;
        L_0x0004:
            r0 = 1;
        L_0x0005:
            return r0;
        L_0x0006:
            r0 = 0;
            goto L_0x0005;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.hasUserActivity():boolean");
        }

        public int getUserActivityCount(int r2, int r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.mUserActivityCounters;
            if (r0 != 0) goto L_0x0006;
        L_0x0004:
            r0 = 0;
        L_0x0005:
            return r0;
        L_0x0006:
            r0 = r1.mUserActivityCounters;
            r0 = r0[r2];
            r0 = r0.getCountLocked(r3);
            goto L_0x0005;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getUserActivityCount(int, int):int");
        }

        void makeWifiBatchedScanBin(int r8, android.os.Parcel r9) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r7 = this;
            r2 = 11;
            if (r8 < 0) goto L_0x0007;
        L_0x0004:
            r0 = 5;
            if (r8 < r0) goto L_0x0008;
        L_0x0007:
            return;
        L_0x0008:
            r0 = r7.this$0;
            r0 = r0.mWifiBatchedScanTimers;
            r3 = r0.get(r8);
            r3 = (java.util.ArrayList) r3;
            if (r3 != 0) goto L_0x0020;
        L_0x0014:
            r3 = new java.util.ArrayList;
            r3.<init>();
            r0 = r7.this$0;
            r0 = r0.mWifiBatchedScanTimers;
            r0.put(r8, r3);
        L_0x0020:
            if (r9 != 0) goto L_0x0030;
        L_0x0022:
            r0 = r7.mWifiBatchedScanTimer;
            r1 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
            r4 = r7.this$0;
            r4 = r4.mOnBatteryTimeBase;
            r1.<init>(r7, r2, r3, r4);
            r0[r8] = r1;
            goto L_0x0007;
        L_0x0030:
            r6 = r7.mWifiBatchedScanTimer;
            r0 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
            r1 = r7.this$0;
            r4 = r1.mOnBatteryTimeBase;
            r1 = r7;
            r5 = r9;
            r0.<init>(r1, r2, r3, r4, r5);
            r6[r8] = r0;
            goto L_0x0007;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.makeWifiBatchedScanBin(int, android.os.Parcel):void");
        }

        void initUserActivityLocked() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r5 = this;
            r4 = 3;
            r1 = new com.android.internal.os.BatteryStatsImpl.Counter[r4];
            r5.mUserActivityCounters = r1;
            r0 = 0;
        L_0x0006:
            if (r0 >= r4) goto L_0x0018;
        L_0x0008:
            r1 = r5.mUserActivityCounters;
            r2 = new com.android.internal.os.BatteryStatsImpl$Counter;
            r3 = r5.this$0;
            r3 = r3.mOnBatteryTimeBase;
            r2.<init>(r3);
            r1[r0] = r2;
            r0 = r0 + 1;
            goto L_0x0006;
        L_0x0018:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.initUserActivityLocked():void");
        }

        void noteNetworkActivityLocked(int r5, long r6, long r8) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r4 = this;
            r0 = r4.mNetworkByteActivityCounters;
            if (r0 != 0) goto L_0x0007;
        L_0x0004:
            r4.initNetworkActivityLocked();
        L_0x0007:
            if (r5 < 0) goto L_0x001b;
        L_0x0009:
            r0 = 4;
            if (r5 >= r0) goto L_0x001b;
        L_0x000c:
            r0 = r4.mNetworkByteActivityCounters;
            r0 = r0[r5];
            r0.addCountLocked(r6);
            r0 = r4.mNetworkPacketActivityCounters;
            r0 = r0[r5];
            r0.addCountLocked(r8);
        L_0x001a:
            return;
        L_0x001b:
            r0 = "BatteryStatsImpl";
            r1 = new java.lang.StringBuilder;
            r1.<init>();
            r2 = "Unknown network activity type ";
            r1 = r1.append(r2);
            r1 = r1.append(r5);
            r2 = " was specified.";
            r1 = r1.append(r2);
            r1 = r1.toString();
            r2 = new java.lang.Throwable;
            r2.<init>();
            android.util.Slog.w(r0, r1, r2);
            goto L_0x001a;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteNetworkActivityLocked(int, long, long):void");
        }

        void noteMobileRadioActiveTimeLocked(long r6) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r5 = this;
            r0 = r5.mNetworkByteActivityCounters;
            if (r0 != 0) goto L_0x0007;
        L_0x0004:
            r5.initNetworkActivityLocked();
        L_0x0007:
            r0 = r5.mMobileRadioActiveTime;
            r0.addCountLocked(r6);
            r0 = r5.mMobileRadioActiveCount;
            r2 = 1;
            r0.addCountLocked(r2);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteMobileRadioActiveTimeLocked(long):void");
        }

        public boolean hasNetworkActivity() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.mNetworkByteActivityCounters;
            if (r0 == 0) goto L_0x0006;
        L_0x0004:
            r0 = 1;
        L_0x0005:
            return r0;
        L_0x0006:
            r0 = 0;
            goto L_0x0005;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.hasNetworkActivity():boolean");
        }

        public long getNetworkActivityBytes(int r3, int r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r2 = this;
            r0 = r2.mNetworkByteActivityCounters;
            if (r0 == 0) goto L_0x0014;
        L_0x0004:
            if (r3 < 0) goto L_0x0014;
        L_0x0006:
            r0 = r2.mNetworkByteActivityCounters;
            r0 = r0.length;
            if (r3 >= r0) goto L_0x0014;
        L_0x000b:
            r0 = r2.mNetworkByteActivityCounters;
            r0 = r0[r3];
            r0 = r0.getCountLocked(r4);
        L_0x0013:
            return r0;
        L_0x0014:
            r0 = 0;
            goto L_0x0013;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getNetworkActivityBytes(int, int):long");
        }

        public long getNetworkActivityPackets(int r3, int r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r2 = this;
            r0 = r2.mNetworkPacketActivityCounters;
            if (r0 == 0) goto L_0x0014;
        L_0x0004:
            if (r3 < 0) goto L_0x0014;
        L_0x0006:
            r0 = r2.mNetworkPacketActivityCounters;
            r0 = r0.length;
            if (r3 >= r0) goto L_0x0014;
        L_0x000b:
            r0 = r2.mNetworkPacketActivityCounters;
            r0 = r0[r3];
            r0 = r0.getCountLocked(r4);
        L_0x0013:
            return r0;
        L_0x0014:
            r0 = 0;
            goto L_0x0013;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getNetworkActivityPackets(int, int):long");
        }

        public long getMobileRadioActiveTime(int r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r2 = this;
            r0 = r2.mMobileRadioActiveTime;
            if (r0 == 0) goto L_0x000b;
        L_0x0004:
            r0 = r2.mMobileRadioActiveTime;
            r0 = r0.getCountLocked(r3);
        L_0x000a:
            return r0;
        L_0x000b:
            r0 = 0;
            goto L_0x000a;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getMobileRadioActiveTime(int):long");
        }

        public int getMobileRadioActiveCount(int r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r2 = this;
            r0 = r2.mMobileRadioActiveCount;
            if (r0 == 0) goto L_0x000c;
        L_0x0004:
            r0 = r2.mMobileRadioActiveCount;
            r0 = r0.getCountLocked(r3);
            r0 = (int) r0;
        L_0x000b:
            return r0;
        L_0x000c:
            r0 = 0;
            goto L_0x000b;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getMobileRadioActiveCount(int):int");
        }

        void initNetworkActivityLocked() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r5 = this;
            r4 = 4;
            r1 = new com.android.internal.os.BatteryStatsImpl.LongSamplingCounter[r4];
            r5.mNetworkByteActivityCounters = r1;
            r1 = new com.android.internal.os.BatteryStatsImpl.LongSamplingCounter[r4];
            r5.mNetworkPacketActivityCounters = r1;
            r0 = 0;
        L_0x000a:
            if (r0 >= r4) goto L_0x0029;
        L_0x000c:
            r1 = r5.mNetworkByteActivityCounters;
            r2 = new com.android.internal.os.BatteryStatsImpl$LongSamplingCounter;
            r3 = r5.this$0;
            r3 = r3.mOnBatteryTimeBase;
            r2.<init>(r3);
            r1[r0] = r2;
            r1 = r5.mNetworkPacketActivityCounters;
            r2 = new com.android.internal.os.BatteryStatsImpl$LongSamplingCounter;
            r3 = r5.this$0;
            r3 = r3.mOnBatteryTimeBase;
            r2.<init>(r3);
            r1[r0] = r2;
            r0 = r0 + 1;
            goto L_0x000a;
        L_0x0029:
            r1 = new com.android.internal.os.BatteryStatsImpl$LongSamplingCounter;
            r2 = r5.this$0;
            r2 = r2.mOnBatteryTimeBase;
            r1.<init>(r2);
            r5.mMobileRadioActiveTime = r1;
            r1 = new com.android.internal.os.BatteryStatsImpl$LongSamplingCounter;
            r2 = r5.this$0;
            r2 = r2.mOnBatteryTimeBase;
            r1.<init>(r2);
            r5.mMobileRadioActiveCount = r1;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.initNetworkActivityLocked():void");
        }

        boolean reset() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r24 = this;
            r2 = 0;
            r0 = r24;
            r0 = r0.mWifiRunningTimer;
            r22 = r0;
            if (r22 == 0) goto L_0x0023;
        L_0x0009:
            r0 = r24;
            r0 = r0.mWifiRunningTimer;
            r22 = r0;
            r23 = 0;
            r22 = r22.reset(r23);
            if (r22 != 0) goto L_0x0097;
        L_0x0017:
            r22 = 1;
        L_0x0019:
            r2 = r2 | r22;
            r0 = r24;
            r0 = r0.mWifiRunning;
            r22 = r0;
            r2 = r2 | r22;
        L_0x0023:
            r0 = r24;
            r0 = r0.mFullWifiLockTimer;
            r22 = r0;
            if (r22 == 0) goto L_0x0045;
        L_0x002b:
            r0 = r24;
            r0 = r0.mFullWifiLockTimer;
            r22 = r0;
            r23 = 0;
            r22 = r22.reset(r23);
            if (r22 != 0) goto L_0x009a;
        L_0x0039:
            r22 = 1;
        L_0x003b:
            r2 = r2 | r22;
            r0 = r24;
            r0 = r0.mFullWifiLockOut;
            r22 = r0;
            r2 = r2 | r22;
        L_0x0045:
            r0 = r24;
            r0 = r0.mWifiScanTimer;
            r22 = r0;
            if (r22 == 0) goto L_0x0067;
        L_0x004d:
            r0 = r24;
            r0 = r0.mWifiScanTimer;
            r22 = r0;
            r23 = 0;
            r22 = r22.reset(r23);
            if (r22 != 0) goto L_0x009d;
        L_0x005b:
            r22 = 1;
        L_0x005d:
            r2 = r2 | r22;
            r0 = r24;
            r0 = r0.mWifiScanStarted;
            r22 = r0;
            r2 = r2 | r22;
        L_0x0067:
            r0 = r24;
            r0 = r0.mWifiBatchedScanTimer;
            r22 = r0;
            if (r22 == 0) goto L_0x00b5;
        L_0x006f:
            r3 = 0;
        L_0x0070:
            r22 = 5;
            r0 = r22;
            if (r3 >= r0) goto L_0x00a3;
        L_0x0076:
            r0 = r24;
            r0 = r0.mWifiBatchedScanTimer;
            r22 = r0;
            r22 = r22[r3];
            if (r22 == 0) goto L_0x0094;
        L_0x0080:
            r0 = r24;
            r0 = r0.mWifiBatchedScanTimer;
            r22 = r0;
            r22 = r22[r3];
            r23 = 0;
            r22 = r22.reset(r23);
            if (r22 != 0) goto L_0x00a0;
        L_0x0090:
            r22 = 1;
        L_0x0092:
            r2 = r2 | r22;
        L_0x0094:
            r3 = r3 + 1;
            goto L_0x0070;
        L_0x0097:
            r22 = 0;
            goto L_0x0019;
        L_0x009a:
            r22 = 0;
            goto L_0x003b;
        L_0x009d:
            r22 = 0;
            goto L_0x005d;
        L_0x00a0:
            r22 = 0;
            goto L_0x0092;
        L_0x00a3:
            r0 = r24;
            r0 = r0.mWifiBatchedScanBinStarted;
            r22 = r0;
            r23 = -1;
            r0 = r22;
            r1 = r23;
            if (r0 == r1) goto L_0x0155;
        L_0x00b1:
            r22 = 1;
        L_0x00b3:
            r2 = r2 | r22;
        L_0x00b5:
            r0 = r24;
            r0 = r0.mWifiMulticastTimer;
            r22 = r0;
            if (r22 == 0) goto L_0x00d7;
        L_0x00bd:
            r0 = r24;
            r0 = r0.mWifiMulticastTimer;
            r22 = r0;
            r23 = 0;
            r22 = r22.reset(r23);
            if (r22 != 0) goto L_0x0159;
        L_0x00cb:
            r22 = 1;
        L_0x00cd:
            r2 = r2 | r22;
            r0 = r24;
            r0 = r0.mWifiMulticastEnabled;
            r22 = r0;
            r2 = r2 | r22;
        L_0x00d7:
            r0 = r24;
            r0 = r0.mAudioTurnedOnTimer;
            r22 = r0;
            if (r22 == 0) goto L_0x00f1;
        L_0x00df:
            r0 = r24;
            r0 = r0.mAudioTurnedOnTimer;
            r22 = r0;
            r23 = 0;
            r22 = r22.reset(r23);
            if (r22 != 0) goto L_0x015d;
        L_0x00ed:
            r22 = 1;
        L_0x00ef:
            r2 = r2 | r22;
        L_0x00f1:
            r0 = r24;
            r0 = r0.mVideoTurnedOnTimer;
            r22 = r0;
            if (r22 == 0) goto L_0x010b;
        L_0x00f9:
            r0 = r24;
            r0 = r0.mVideoTurnedOnTimer;
            r22 = r0;
            r23 = 0;
            r22 = r22.reset(r23);
            if (r22 != 0) goto L_0x0160;
        L_0x0107:
            r22 = 1;
        L_0x0109:
            r2 = r2 | r22;
        L_0x010b:
            r0 = r24;
            r0 = r0.mForegroundActivityTimer;
            r22 = r0;
            if (r22 == 0) goto L_0x0125;
        L_0x0113:
            r0 = r24;
            r0 = r0.mForegroundActivityTimer;
            r22 = r0;
            r23 = 0;
            r22 = r22.reset(r23);
            if (r22 != 0) goto L_0x0163;
        L_0x0121:
            r22 = 1;
        L_0x0123:
            r2 = r2 | r22;
        L_0x0125:
            r0 = r24;
            r0 = r0.mProcessStateTimer;
            r22 = r0;
            if (r22 == 0) goto L_0x017b;
        L_0x012d:
            r3 = 0;
        L_0x012e:
            r22 = 3;
            r0 = r22;
            if (r3 >= r0) goto L_0x0169;
        L_0x0134:
            r0 = r24;
            r0 = r0.mProcessStateTimer;
            r22 = r0;
            r22 = r22[r3];
            if (r22 == 0) goto L_0x0152;
        L_0x013e:
            r0 = r24;
            r0 = r0.mProcessStateTimer;
            r22 = r0;
            r22 = r22[r3];
            r23 = 0;
            r22 = r22.reset(r23);
            if (r22 != 0) goto L_0x0166;
        L_0x014e:
            r22 = 1;
        L_0x0150:
            r2 = r2 | r22;
        L_0x0152:
            r3 = r3 + 1;
            goto L_0x012e;
        L_0x0155:
            r22 = 0;
            goto L_0x00b3;
        L_0x0159:
            r22 = 0;
            goto L_0x00cd;
        L_0x015d:
            r22 = 0;
            goto L_0x00ef;
        L_0x0160:
            r22 = 0;
            goto L_0x0109;
        L_0x0163:
            r22 = 0;
            goto L_0x0123;
        L_0x0166:
            r22 = 0;
            goto L_0x0150;
        L_0x0169:
            r0 = r24;
            r0 = r0.mProcessState;
            r22 = r0;
            r23 = 3;
            r0 = r22;
            r1 = r23;
            if (r0 == r1) goto L_0x01c1;
        L_0x0177:
            r22 = 1;
        L_0x0179:
            r2 = r2 | r22;
        L_0x017b:
            r0 = r24;
            r0 = r0.mVibratorOnTimer;
            r22 = r0;
            if (r22 == 0) goto L_0x01a2;
        L_0x0183:
            r0 = r24;
            r0 = r0.mVibratorOnTimer;
            r22 = r0;
            r23 = 0;
            r22 = r22.reset(r23);
            if (r22 == 0) goto L_0x01c4;
        L_0x0191:
            r0 = r24;
            r0 = r0.mVibratorOnTimer;
            r22 = r0;
            r22.detach();
            r22 = 0;
            r0 = r22;
            r1 = r24;
            r1.mVibratorOnTimer = r0;
        L_0x01a2:
            r0 = r24;
            r0 = r0.mUserActivityCounters;
            r22 = r0;
            if (r22 == 0) goto L_0x01c6;
        L_0x01aa:
            r3 = 0;
        L_0x01ab:
            r22 = 3;
            r0 = r22;
            if (r3 >= r0) goto L_0x01c6;
        L_0x01b1:
            r0 = r24;
            r0 = r0.mUserActivityCounters;
            r22 = r0;
            r22 = r22[r3];
            r23 = 0;
            r22.reset(r23);
            r3 = r3 + 1;
            goto L_0x01ab;
        L_0x01c1:
            r22 = 0;
            goto L_0x0179;
        L_0x01c4:
            r2 = 1;
            goto L_0x01a2;
        L_0x01c6:
            r0 = r24;
            r0 = r0.mNetworkByteActivityCounters;
            r22 = r0;
            if (r22 == 0) goto L_0x0208;
        L_0x01ce:
            r3 = 0;
        L_0x01cf:
            r22 = 4;
            r0 = r22;
            if (r3 >= r0) goto L_0x01f2;
        L_0x01d5:
            r0 = r24;
            r0 = r0.mNetworkByteActivityCounters;
            r22 = r0;
            r22 = r22[r3];
            r23 = 0;
            r22.reset(r23);
            r0 = r24;
            r0 = r0.mNetworkPacketActivityCounters;
            r22 = r0;
            r22 = r22[r3];
            r23 = 0;
            r22.reset(r23);
            r3 = r3 + 1;
            goto L_0x01cf;
        L_0x01f2:
            r0 = r24;
            r0 = r0.mMobileRadioActiveTime;
            r22 = r0;
            r23 = 0;
            r22.reset(r23);
            r0 = r24;
            r0 = r0.mMobileRadioActiveCount;
            r22 = r0;
            r23 = 0;
            r22.reset(r23);
        L_0x0208:
            r0 = r24;
            r0 = r0.mWakelockStats;
            r22 = r0;
            r20 = r22.getMap();
            r22 = r20.size();
            r10 = r22 + -1;
        L_0x0218:
            if (r10 < 0) goto L_0x0232;
        L_0x021a:
            r0 = r20;
            r21 = r0.valueAt(r10);
            r21 = (com.android.internal.os.BatteryStatsImpl.Uid.Wakelock) r21;
            r22 = r21.reset();
            if (r22 == 0) goto L_0x0230;
        L_0x0228:
            r0 = r20;
            r0.removeAt(r10);
        L_0x022d:
            r10 = r10 + -1;
            goto L_0x0218;
        L_0x0230:
            r2 = 1;
            goto L_0x022d;
        L_0x0232:
            r0 = r24;
            r0 = r0.mWakelockStats;
            r22 = r0;
            r22.cleanup();
            r0 = r24;
            r0 = r0.mSyncStats;
            r22 = r0;
            r18 = r22.getMap();
            r22 = r18.size();
            r6 = r22 + -1;
        L_0x024b:
            if (r6 < 0) goto L_0x026e;
        L_0x024d:
            r0 = r18;
            r19 = r0.valueAt(r6);
            r19 = (com.android.internal.os.BatteryStatsImpl.StopwatchTimer) r19;
            r22 = 0;
            r0 = r19;
            r1 = r22;
            r22 = r0.reset(r1);
            if (r22 == 0) goto L_0x026c;
        L_0x0261:
            r0 = r18;
            r0.removeAt(r6);
            r19.detach();
        L_0x0269:
            r6 = r6 + -1;
            goto L_0x024b;
        L_0x026c:
            r2 = 1;
            goto L_0x0269;
        L_0x026e:
            r0 = r24;
            r0 = r0.mSyncStats;
            r22 = r0;
            r22.cleanup();
            r0 = r24;
            r0 = r0.mJobStats;
            r22 = r0;
            r11 = r22.getMap();
            r22 = r11.size();
            r4 = r22 + -1;
        L_0x0287:
            if (r4 < 0) goto L_0x02a6;
        L_0x0289:
            r19 = r11.valueAt(r4);
            r19 = (com.android.internal.os.BatteryStatsImpl.StopwatchTimer) r19;
            r22 = 0;
            r0 = r19;
            r1 = r22;
            r22 = r0.reset(r1);
            if (r22 == 0) goto L_0x02a4;
        L_0x029b:
            r11.removeAt(r4);
            r19.detach();
        L_0x02a1:
            r4 = r4 + -1;
            goto L_0x0287;
        L_0x02a4:
            r2 = 1;
            goto L_0x02a1;
        L_0x02a6:
            r0 = r24;
            r0 = r0.mJobStats;
            r22 = r0;
            r22.cleanup();
            r0 = r24;
            r0 = r0.mSensorStats;
            r22 = r0;
            r22 = r22.size();
            r7 = r22 + -1;
        L_0x02bb:
            if (r7 < 0) goto L_0x02e1;
        L_0x02bd:
            r0 = r24;
            r0 = r0.mSensorStats;
            r22 = r0;
            r0 = r22;
            r16 = r0.valueAt(r7);
            r16 = (com.android.internal.os.BatteryStatsImpl.Uid.Sensor) r16;
            r22 = r16.reset();
            if (r22 == 0) goto L_0x02df;
        L_0x02d1:
            r0 = r24;
            r0 = r0.mSensorStats;
            r22 = r0;
            r0 = r22;
            r0.removeAt(r7);
        L_0x02dc:
            r7 = r7 + -1;
            goto L_0x02bb;
        L_0x02df:
            r2 = 1;
            goto L_0x02dc;
        L_0x02e1:
            r0 = r24;
            r0 = r0.mProcessStats;
            r22 = r0;
            r22 = r22.size();
            r5 = r22 + -1;
        L_0x02ed:
            if (r5 < 0) goto L_0x031f;
        L_0x02ef:
            r0 = r24;
            r0 = r0.mProcessStats;
            r22 = r0;
            r0 = r22;
            r15 = r0.valueAt(r5);
            r15 = (com.android.internal.os.BatteryStatsImpl.Uid.Proc) r15;
            r0 = r15.mProcessState;
            r22 = r0;
            r23 = 3;
            r0 = r22;
            r1 = r23;
            if (r0 != r1) goto L_0x031a;
        L_0x0309:
            r15.detach();
            r0 = r24;
            r0 = r0.mProcessStats;
            r22 = r0;
            r0 = r22;
            r0.removeAt(r5);
        L_0x0317:
            r5 = r5 + -1;
            goto L_0x02ed;
        L_0x031a:
            r15.reset();
            r2 = 1;
            goto L_0x0317;
        L_0x031f:
            r0 = r24;
            r0 = r0.mPids;
            r22 = r0;
            r22 = r22.size();
            if (r22 <= 0) goto L_0x035d;
        L_0x032b:
            r0 = r24;
            r0 = r0.mPids;
            r22 = r0;
            r22 = r22.size();
            r3 = r22 + -1;
        L_0x0337:
            if (r3 < 0) goto L_0x035d;
        L_0x0339:
            r0 = r24;
            r0 = r0.mPids;
            r22 = r0;
            r0 = r22;
            r13 = r0.valueAt(r3);
            r13 = (android.os.BatteryStats.Uid.Pid) r13;
            r0 = r13.mWakeNesting;
            r22 = r0;
            if (r22 <= 0) goto L_0x0351;
        L_0x034d:
            r2 = 1;
        L_0x034e:
            r3 = r3 + -1;
            goto L_0x0337;
        L_0x0351:
            r0 = r24;
            r0 = r0.mPids;
            r22 = r0;
            r0 = r22;
            r0.removeAt(r3);
            goto L_0x034e;
        L_0x035d:
            r0 = r24;
            r0 = r0.mPackageStats;
            r22 = r0;
            r22 = r22.size();
            if (r22 <= 0) goto L_0x03c1;
        L_0x0369:
            r0 = r24;
            r0 = r0.mPackageStats;
            r22 = r0;
            r22 = r22.entrySet();
            r8 = r22.iterator();
        L_0x0377:
            r22 = r8.hasNext();
            if (r22 == 0) goto L_0x03b8;
        L_0x037d:
            r14 = r8.next();
            r14 = (java.util.Map.Entry) r14;
            r12 = r14.getValue();
            r12 = (com.android.internal.os.BatteryStatsImpl.Uid.Pkg) r12;
            r12.detach();
            r0 = r12.mServiceStats;
            r22 = r0;
            r22 = r22.size();
            if (r22 <= 0) goto L_0x0377;
        L_0x0396:
            r0 = r12.mServiceStats;
            r22 = r0;
            r22 = r22.entrySet();
            r9 = r22.iterator();
        L_0x03a2:
            r22 = r9.hasNext();
            if (r22 == 0) goto L_0x0377;
        L_0x03a8:
            r17 = r9.next();
            r17 = (java.util.Map.Entry) r17;
            r22 = r17.getValue();
            r22 = (com.android.internal.os.BatteryStatsImpl.Uid.Pkg.Serv) r22;
            r22.detach();
            goto L_0x03a2;
        L_0x03b8:
            r0 = r24;
            r0 = r0.mPackageStats;
            r22 = r0;
            r22.clear();
        L_0x03c1:
            if (r2 != 0) goto L_0x04bf;
        L_0x03c3:
            r0 = r24;
            r0 = r0.mWifiRunningTimer;
            r22 = r0;
            if (r22 == 0) goto L_0x03d4;
        L_0x03cb:
            r0 = r24;
            r0 = r0.mWifiRunningTimer;
            r22 = r0;
            r22.detach();
        L_0x03d4:
            r0 = r24;
            r0 = r0.mFullWifiLockTimer;
            r22 = r0;
            if (r22 == 0) goto L_0x03e5;
        L_0x03dc:
            r0 = r24;
            r0 = r0.mFullWifiLockTimer;
            r22 = r0;
            r22.detach();
        L_0x03e5:
            r0 = r24;
            r0 = r0.mWifiScanTimer;
            r22 = r0;
            if (r22 == 0) goto L_0x03f6;
        L_0x03ed:
            r0 = r24;
            r0 = r0.mWifiScanTimer;
            r22 = r0;
            r22.detach();
        L_0x03f6:
            r3 = 0;
        L_0x03f7:
            r22 = 5;
            r0 = r22;
            if (r3 >= r0) goto L_0x0415;
        L_0x03fd:
            r0 = r24;
            r0 = r0.mWifiBatchedScanTimer;
            r22 = r0;
            r22 = r22[r3];
            if (r22 == 0) goto L_0x0412;
        L_0x0407:
            r0 = r24;
            r0 = r0.mWifiBatchedScanTimer;
            r22 = r0;
            r22 = r22[r3];
            r22.detach();
        L_0x0412:
            r3 = r3 + 1;
            goto L_0x03f7;
        L_0x0415:
            r0 = r24;
            r0 = r0.mWifiMulticastTimer;
            r22 = r0;
            if (r22 == 0) goto L_0x0426;
        L_0x041d:
            r0 = r24;
            r0 = r0.mWifiMulticastTimer;
            r22 = r0;
            r22.detach();
        L_0x0426:
            r0 = r24;
            r0 = r0.mAudioTurnedOnTimer;
            r22 = r0;
            if (r22 == 0) goto L_0x043f;
        L_0x042e:
            r0 = r24;
            r0 = r0.mAudioTurnedOnTimer;
            r22 = r0;
            r22.detach();
            r22 = 0;
            r0 = r22;
            r1 = r24;
            r1.mAudioTurnedOnTimer = r0;
        L_0x043f:
            r0 = r24;
            r0 = r0.mVideoTurnedOnTimer;
            r22 = r0;
            if (r22 == 0) goto L_0x0458;
        L_0x0447:
            r0 = r24;
            r0 = r0.mVideoTurnedOnTimer;
            r22 = r0;
            r22.detach();
            r22 = 0;
            r0 = r22;
            r1 = r24;
            r1.mVideoTurnedOnTimer = r0;
        L_0x0458:
            r0 = r24;
            r0 = r0.mForegroundActivityTimer;
            r22 = r0;
            if (r22 == 0) goto L_0x0471;
        L_0x0460:
            r0 = r24;
            r0 = r0.mForegroundActivityTimer;
            r22 = r0;
            r22.detach();
            r22 = 0;
            r0 = r22;
            r1 = r24;
            r1.mForegroundActivityTimer = r0;
        L_0x0471:
            r0 = r24;
            r0 = r0.mUserActivityCounters;
            r22 = r0;
            if (r22 == 0) goto L_0x048e;
        L_0x0479:
            r3 = 0;
        L_0x047a:
            r22 = 3;
            r0 = r22;
            if (r3 >= r0) goto L_0x048e;
        L_0x0480:
            r0 = r24;
            r0 = r0.mUserActivityCounters;
            r22 = r0;
            r22 = r22[r3];
            r22.detach();
            r3 = r3 + 1;
            goto L_0x047a;
        L_0x048e:
            r0 = r24;
            r0 = r0.mNetworkByteActivityCounters;
            r22 = r0;
            if (r22 == 0) goto L_0x04b6;
        L_0x0496:
            r3 = 0;
        L_0x0497:
            r22 = 4;
            r0 = r22;
            if (r3 >= r0) goto L_0x04b6;
        L_0x049d:
            r0 = r24;
            r0 = r0.mNetworkByteActivityCounters;
            r22 = r0;
            r22 = r22[r3];
            r22.detach();
            r0 = r24;
            r0 = r0.mNetworkPacketActivityCounters;
            r22 = r0;
            r22 = r22[r3];
            r22.detach();
            r3 = r3 + 1;
            goto L_0x0497;
        L_0x04b6:
            r0 = r24;
            r0 = r0.mPids;
            r22 = r0;
            r22.clear();
        L_0x04bf:
            if (r2 != 0) goto L_0x04c4;
        L_0x04c1:
            r22 = 1;
        L_0x04c3:
            return r22;
        L_0x04c4:
            r22 = 0;
            goto L_0x04c3;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.reset():boolean");
        }

        void writeToParcelLocked(android.os.Parcel r27, long r28) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r26 = this;
            r0 = r26;
            r0 = r0.mWakelockStats;
            r25 = r0;
            r23 = r25.getMap();
            r8 = r23.size();
            r0 = r27;
            r0.writeInt(r8);
            r15 = 0;
        L_0x0014:
            if (r15 >= r8) goto L_0x0039;
        L_0x0016:
            r0 = r23;
            r25 = r0.keyAt(r15);
            r25 = (java.lang.String) r25;
            r0 = r27;
            r1 = r25;
            r0.writeString(r1);
            r0 = r23;
            r24 = r0.valueAt(r15);
            r24 = (com.android.internal.os.BatteryStatsImpl.Uid.Wakelock) r24;
            r0 = r24;
            r1 = r27;
            r2 = r28;
            r0.writeToParcelLocked(r1, r2);
            r15 = r15 + 1;
            goto L_0x0014;
        L_0x0039:
            r0 = r26;
            r0 = r0.mSyncStats;
            r25 = r0;
            r21 = r25.getMap();
            r6 = r21.size();
            r0 = r27;
            r0.writeInt(r6);
            r13 = 0;
        L_0x004d:
            if (r13 >= r6) goto L_0x0072;
        L_0x004f:
            r0 = r21;
            r25 = r0.keyAt(r13);
            r25 = (java.lang.String) r25;
            r0 = r27;
            r1 = r25;
            r0.writeString(r1);
            r0 = r21;
            r22 = r0.valueAt(r13);
            r22 = (com.android.internal.os.BatteryStatsImpl.StopwatchTimer) r22;
            r0 = r27;
            r1 = r22;
            r2 = r28;
            com.android.internal.os.BatteryStatsImpl.Timer.writeTimerToParcel(r0, r1, r2);
            r13 = r13 + 1;
            goto L_0x004d;
        L_0x0072:
            r0 = r26;
            r0 = r0.mJobStats;
            r25 = r0;
            r16 = r25.getMap();
            r4 = r16.size();
            r0 = r27;
            r0.writeInt(r4);
            r11 = 0;
        L_0x0086:
            if (r11 >= r4) goto L_0x00ab;
        L_0x0088:
            r0 = r16;
            r25 = r0.keyAt(r11);
            r25 = (java.lang.String) r25;
            r0 = r27;
            r1 = r25;
            r0.writeString(r1);
            r0 = r16;
            r22 = r0.valueAt(r11);
            r22 = (com.android.internal.os.BatteryStatsImpl.StopwatchTimer) r22;
            r0 = r27;
            r1 = r22;
            r2 = r28;
            com.android.internal.os.BatteryStatsImpl.Timer.writeTimerToParcel(r0, r1, r2);
            r11 = r11 + 1;
            goto L_0x0086;
        L_0x00ab:
            r0 = r26;
            r0 = r0.mSensorStats;
            r25 = r0;
            r7 = r25.size();
            r0 = r27;
            r0.writeInt(r7);
            r14 = 0;
        L_0x00bb:
            if (r14 >= r7) goto L_0x00ea;
        L_0x00bd:
            r0 = r26;
            r0 = r0.mSensorStats;
            r25 = r0;
            r0 = r25;
            r25 = r0.keyAt(r14);
            r0 = r27;
            r1 = r25;
            r0.writeInt(r1);
            r0 = r26;
            r0 = r0.mSensorStats;
            r25 = r0;
            r0 = r25;
            r20 = r0.valueAt(r14);
            r20 = (com.android.internal.os.BatteryStatsImpl.Uid.Sensor) r20;
            r0 = r20;
            r1 = r27;
            r2 = r28;
            r0.writeToParcelLocked(r1, r2);
            r14 = r14 + 1;
            goto L_0x00bb;
        L_0x00ea:
            r0 = r26;
            r0 = r0.mProcessStats;
            r25 = r0;
            r5 = r25.size();
            r0 = r27;
            r0.writeInt(r5);
            r12 = 0;
        L_0x00fa:
            if (r12 >= r5) goto L_0x0129;
        L_0x00fc:
            r0 = r26;
            r0 = r0.mProcessStats;
            r25 = r0;
            r0 = r25;
            r25 = r0.keyAt(r12);
            r25 = (java.lang.String) r25;
            r0 = r27;
            r1 = r25;
            r0.writeString(r1);
            r0 = r26;
            r0 = r0.mProcessStats;
            r25 = r0;
            r0 = r25;
            r19 = r0.valueAt(r12);
            r19 = (com.android.internal.os.BatteryStatsImpl.Uid.Proc) r19;
            r0 = r19;
            r1 = r27;
            r0.writeToParcelLocked(r1);
            r12 = r12 + 1;
            goto L_0x00fa;
        L_0x0129:
            r0 = r26;
            r0 = r0.mPackageStats;
            r25 = r0;
            r25 = r25.size();
            r0 = r27;
            r1 = r25;
            r0.writeInt(r1);
            r0 = r26;
            r0 = r0.mPackageStats;
            r25 = r0;
            r25 = r25.entrySet();
            r10 = r25.iterator();
        L_0x0148:
            r25 = r10.hasNext();
            if (r25 == 0) goto L_0x016f;
        L_0x014e:
            r18 = r10.next();
            r18 = (java.util.Map.Entry) r18;
            r25 = r18.getKey();
            r25 = (java.lang.String) r25;
            r0 = r27;
            r1 = r25;
            r0.writeString(r1);
            r17 = r18.getValue();
            r17 = (com.android.internal.os.BatteryStatsImpl.Uid.Pkg) r17;
            r0 = r17;
            r1 = r27;
            r0.writeToParcelLocked(r1);
            goto L_0x0148;
        L_0x016f:
            r0 = r26;
            r0 = r0.mWifiRunningTimer;
            r25 = r0;
            if (r25 == 0) goto L_0x01fd;
        L_0x0177:
            r25 = 1;
            r0 = r27;
            r1 = r25;
            r0.writeInt(r1);
            r0 = r26;
            r0 = r0.mWifiRunningTimer;
            r25 = r0;
            r0 = r25;
            r1 = r27;
            r2 = r28;
            r0.writeToParcel(r1, r2);
        L_0x018f:
            r0 = r26;
            r0 = r0.mFullWifiLockTimer;
            r25 = r0;
            if (r25 == 0) goto L_0x0207;
        L_0x0197:
            r25 = 1;
            r0 = r27;
            r1 = r25;
            r0.writeInt(r1);
            r0 = r26;
            r0 = r0.mFullWifiLockTimer;
            r25 = r0;
            r0 = r25;
            r1 = r27;
            r2 = r28;
            r0.writeToParcel(r1, r2);
        L_0x01af:
            r0 = r26;
            r0 = r0.mWifiScanTimer;
            r25 = r0;
            if (r25 == 0) goto L_0x0211;
        L_0x01b7:
            r25 = 1;
            r0 = r27;
            r1 = r25;
            r0.writeInt(r1);
            r0 = r26;
            r0 = r0.mWifiScanTimer;
            r25 = r0;
            r0 = r25;
            r1 = r27;
            r2 = r28;
            r0.writeToParcel(r1, r2);
        L_0x01cf:
            r9 = 0;
        L_0x01d0:
            r25 = 5;
            r0 = r25;
            if (r9 >= r0) goto L_0x0225;
        L_0x01d6:
            r0 = r26;
            r0 = r0.mWifiBatchedScanTimer;
            r25 = r0;
            r25 = r25[r9];
            if (r25 == 0) goto L_0x021b;
        L_0x01e0:
            r25 = 1;
            r0 = r27;
            r1 = r25;
            r0.writeInt(r1);
            r0 = r26;
            r0 = r0.mWifiBatchedScanTimer;
            r25 = r0;
            r25 = r25[r9];
            r0 = r25;
            r1 = r27;
            r2 = r28;
            r0.writeToParcel(r1, r2);
        L_0x01fa:
            r9 = r9 + 1;
            goto L_0x01d0;
        L_0x01fd:
            r25 = 0;
            r0 = r27;
            r1 = r25;
            r0.writeInt(r1);
            goto L_0x018f;
        L_0x0207:
            r25 = 0;
            r0 = r27;
            r1 = r25;
            r0.writeInt(r1);
            goto L_0x01af;
        L_0x0211:
            r25 = 0;
            r0 = r27;
            r1 = r25;
            r0.writeInt(r1);
            goto L_0x01cf;
        L_0x021b:
            r25 = 0;
            r0 = r27;
            r1 = r25;
            r0.writeInt(r1);
            goto L_0x01fa;
        L_0x0225:
            r0 = r26;
            r0 = r0.mWifiMulticastTimer;
            r25 = r0;
            if (r25 == 0) goto L_0x02d3;
        L_0x022d:
            r25 = 1;
            r0 = r27;
            r1 = r25;
            r0.writeInt(r1);
            r0 = r26;
            r0 = r0.mWifiMulticastTimer;
            r25 = r0;
            r0 = r25;
            r1 = r27;
            r2 = r28;
            r0.writeToParcel(r1, r2);
        L_0x0245:
            r0 = r26;
            r0 = r0.mAudioTurnedOnTimer;
            r25 = r0;
            if (r25 == 0) goto L_0x02de;
        L_0x024d:
            r25 = 1;
            r0 = r27;
            r1 = r25;
            r0.writeInt(r1);
            r0 = r26;
            r0 = r0.mAudioTurnedOnTimer;
            r25 = r0;
            r0 = r25;
            r1 = r27;
            r2 = r28;
            r0.writeToParcel(r1, r2);
        L_0x0265:
            r0 = r26;
            r0 = r0.mVideoTurnedOnTimer;
            r25 = r0;
            if (r25 == 0) goto L_0x02e9;
        L_0x026d:
            r25 = 1;
            r0 = r27;
            r1 = r25;
            r0.writeInt(r1);
            r0 = r26;
            r0 = r0.mVideoTurnedOnTimer;
            r25 = r0;
            r0 = r25;
            r1 = r27;
            r2 = r28;
            r0.writeToParcel(r1, r2);
        L_0x0285:
            r0 = r26;
            r0 = r0.mForegroundActivityTimer;
            r25 = r0;
            if (r25 == 0) goto L_0x02f3;
        L_0x028d:
            r25 = 1;
            r0 = r27;
            r1 = r25;
            r0.writeInt(r1);
            r0 = r26;
            r0 = r0.mForegroundActivityTimer;
            r25 = r0;
            r0 = r25;
            r1 = r27;
            r2 = r28;
            r0.writeToParcel(r1, r2);
        L_0x02a5:
            r9 = 0;
        L_0x02a6:
            r25 = 3;
            r0 = r25;
            if (r9 >= r0) goto L_0x0307;
        L_0x02ac:
            r0 = r26;
            r0 = r0.mProcessStateTimer;
            r25 = r0;
            r25 = r25[r9];
            if (r25 == 0) goto L_0x02fd;
        L_0x02b6:
            r25 = 1;
            r0 = r27;
            r1 = r25;
            r0.writeInt(r1);
            r0 = r26;
            r0 = r0.mProcessStateTimer;
            r25 = r0;
            r25 = r25[r9];
            r0 = r25;
            r1 = r27;
            r2 = r28;
            r0.writeToParcel(r1, r2);
        L_0x02d0:
            r9 = r9 + 1;
            goto L_0x02a6;
        L_0x02d3:
            r25 = 0;
            r0 = r27;
            r1 = r25;
            r0.writeInt(r1);
            goto L_0x0245;
        L_0x02de:
            r25 = 0;
            r0 = r27;
            r1 = r25;
            r0.writeInt(r1);
            goto L_0x0265;
        L_0x02e9:
            r25 = 0;
            r0 = r27;
            r1 = r25;
            r0.writeInt(r1);
            goto L_0x0285;
        L_0x02f3:
            r25 = 0;
            r0 = r27;
            r1 = r25;
            r0.writeInt(r1);
            goto L_0x02a5;
        L_0x02fd:
            r25 = 0;
            r0 = r27;
            r1 = r25;
            r0.writeInt(r1);
            goto L_0x02d0;
        L_0x0307:
            r0 = r26;
            r0 = r0.mVibratorOnTimer;
            r25 = r0;
            if (r25 == 0) goto L_0x0351;
        L_0x030f:
            r25 = 1;
            r0 = r27;
            r1 = r25;
            r0.writeInt(r1);
            r0 = r26;
            r0 = r0.mVibratorOnTimer;
            r25 = r0;
            r0 = r25;
            r1 = r27;
            r2 = r28;
            r0.writeToParcel(r1, r2);
        L_0x0327:
            r0 = r26;
            r0 = r0.mUserActivityCounters;
            r25 = r0;
            if (r25 == 0) goto L_0x035b;
        L_0x032f:
            r25 = 1;
            r0 = r27;
            r1 = r25;
            r0.writeInt(r1);
            r9 = 0;
        L_0x0339:
            r25 = 3;
            r0 = r25;
            if (r9 >= r0) goto L_0x0364;
        L_0x033f:
            r0 = r26;
            r0 = r0.mUserActivityCounters;
            r25 = r0;
            r25 = r25[r9];
            r0 = r25;
            r1 = r27;
            r0.writeToParcel(r1);
            r9 = r9 + 1;
            goto L_0x0339;
        L_0x0351:
            r25 = 0;
            r0 = r27;
            r1 = r25;
            r0.writeInt(r1);
            goto L_0x0327;
        L_0x035b:
            r25 = 0;
            r0 = r27;
            r1 = r25;
            r0.writeInt(r1);
        L_0x0364:
            r0 = r26;
            r0 = r0.mNetworkByteActivityCounters;
            r25 = r0;
            if (r25 == 0) goto L_0x03b8;
        L_0x036c:
            r25 = 1;
            r0 = r27;
            r1 = r25;
            r0.writeInt(r1);
            r9 = 0;
        L_0x0376:
            r25 = 4;
            r0 = r25;
            if (r9 >= r0) goto L_0x039d;
        L_0x037c:
            r0 = r26;
            r0 = r0.mNetworkByteActivityCounters;
            r25 = r0;
            r25 = r25[r9];
            r0 = r25;
            r1 = r27;
            r0.writeToParcel(r1);
            r0 = r26;
            r0 = r0.mNetworkPacketActivityCounters;
            r25 = r0;
            r25 = r25[r9];
            r0 = r25;
            r1 = r27;
            r0.writeToParcel(r1);
            r9 = r9 + 1;
            goto L_0x0376;
        L_0x039d:
            r0 = r26;
            r0 = r0.mMobileRadioActiveTime;
            r25 = r0;
            r0 = r25;
            r1 = r27;
            r0.writeToParcel(r1);
            r0 = r26;
            r0 = r0.mMobileRadioActiveCount;
            r25 = r0;
            r0 = r25;
            r1 = r27;
            r0.writeToParcel(r1);
        L_0x03b7:
            return;
        L_0x03b8:
            r25 = 0;
            r0 = r27;
            r1 = r25;
            r0.writeInt(r1);
            goto L_0x03b7;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.writeToParcelLocked(android.os.Parcel, long):void");
        }

        void readFromParcelLocked(com.android.internal.os.BatteryStatsImpl.TimeBase r32, com.android.internal.os.BatteryStatsImpl.TimeBase r33, android.os.Parcel r34) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r31 = this;
            r20 = r34.readInt();
            r0 = r31;
            r4 = r0.mWakelockStats;
            r4.clear();
            r11 = 0;
        L_0x000c:
            r0 = r20;
            if (r11 >= r0) goto L_0x0036;
        L_0x0010:
            r29 = r34.readString();
            r28 = new com.android.internal.os.BatteryStatsImpl$Uid$Wakelock;
            r0 = r28;
            r1 = r31;
            r0.<init>(r1);
            r0 = r28;
            r1 = r32;
            r2 = r33;
            r3 = r34;
            r0.readFromParcelLocked(r1, r2, r3);
            r0 = r31;
            r4 = r0.mWakelockStats;
            r0 = r29;
            r1 = r28;
            r4.add(r0, r1);
            r11 = r11 + 1;
            goto L_0x000c;
        L_0x0036:
            r19 = r34.readInt();
            r0 = r31;
            r4 = r0.mSyncStats;
            r4.clear();
            r11 = 0;
        L_0x0042:
            r0 = r19;
            if (r11 >= r0) goto L_0x006e;
        L_0x0046:
            r27 = r34.readString();
            r4 = r34.readInt();
            if (r4 == 0) goto L_0x006b;
        L_0x0050:
            r0 = r31;
            r0 = r0.mSyncStats;
            r30 = r0;
            r4 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
            r6 = 13;
            r7 = 0;
            r5 = r31;
            r8 = r32;
            r9 = r34;
            r4.<init>(r5, r6, r7, r8, r9);
            r0 = r30;
            r1 = r27;
            r0.add(r1, r4);
        L_0x006b:
            r11 = r11 + 1;
            goto L_0x0042;
        L_0x006e:
            r15 = r34.readInt();
            r0 = r31;
            r4 = r0.mJobStats;
            r4.clear();
            r11 = 0;
        L_0x007a:
            if (r11 >= r15) goto L_0x00a2;
        L_0x007c:
            r12 = r34.readString();
            r4 = r34.readInt();
            if (r4 == 0) goto L_0x009f;
        L_0x0086:
            r0 = r31;
            r0 = r0.mJobStats;
            r30 = r0;
            r4 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
            r6 = 14;
            r7 = 0;
            r5 = r31;
            r8 = r32;
            r9 = r34;
            r4.<init>(r5, r6, r7, r8, r9);
            r0 = r30;
            r0.add(r12, r4);
        L_0x009f:
            r11 = r11 + 1;
            goto L_0x007a;
        L_0x00a2:
            r18 = r34.readInt();
            r0 = r31;
            r4 = r0.mSensorStats;
            r4.clear();
            r13 = 0;
        L_0x00ae:
            r0 = r18;
            if (r13 >= r0) goto L_0x00dc;
        L_0x00b2:
            r26 = r34.readInt();
            r25 = new com.android.internal.os.BatteryStatsImpl$Uid$Sensor;
            r0 = r25;
            r1 = r31;
            r2 = r26;
            r0.<init>(r1, r2);
            r0 = r31;
            r4 = r0.this$0;
            r4 = r4.mOnBatteryTimeBase;
            r0 = r25;
            r1 = r34;
            r0.readFromParcelLocked(r4, r1);
            r0 = r31;
            r4 = r0.mSensorStats;
            r0 = r26;
            r1 = r25;
            r4.put(r0, r1);
            r13 = r13 + 1;
            goto L_0x00ae;
        L_0x00dc:
            r17 = r34.readInt();
            r0 = r31;
            r4 = r0.mProcessStats;
            r4.clear();
            r13 = 0;
        L_0x00e8:
            r0 = r17;
            if (r13 >= r0) goto L_0x0110;
        L_0x00ec:
            r24 = r34.readString();
            r23 = new com.android.internal.os.BatteryStatsImpl$Uid$Proc;
            r0 = r23;
            r1 = r31;
            r2 = r24;
            r0.<init>(r1, r2);
            r0 = r23;
            r1 = r34;
            r0.readFromParcelLocked(r1);
            r0 = r31;
            r4 = r0.mProcessStats;
            r0 = r24;
            r1 = r23;
            r4.put(r0, r1);
            r13 = r13 + 1;
            goto L_0x00e8;
        L_0x0110:
            r16 = r34.readInt();
            r0 = r31;
            r4 = r0.mPackageStats;
            r4.clear();
            r14 = 0;
        L_0x011c:
            r0 = r16;
            if (r14 >= r0) goto L_0x0142;
        L_0x0120:
            r21 = r34.readString();
            r22 = new com.android.internal.os.BatteryStatsImpl$Uid$Pkg;
            r0 = r22;
            r1 = r31;
            r0.<init>(r1);
            r0 = r22;
            r1 = r34;
            r0.readFromParcelLocked(r1);
            r0 = r31;
            r4 = r0.mPackageStats;
            r0 = r21;
            r1 = r22;
            r4.put(r0, r1);
            r14 = r14 + 1;
            goto L_0x011c;
        L_0x0142:
            r4 = 0;
            r0 = r31;
            r0.mWifiRunning = r4;
            r4 = r34.readInt();
            if (r4 == 0) goto L_0x01ca;
        L_0x014d:
            r4 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
            r6 = 4;
            r0 = r31;
            r5 = r0.this$0;
            r7 = r5.mWifiRunningTimers;
            r0 = r31;
            r5 = r0.this$0;
            r8 = r5.mOnBatteryTimeBase;
            r5 = r31;
            r9 = r34;
            r4.<init>(r5, r6, r7, r8, r9);
            r0 = r31;
            r0.mWifiRunningTimer = r4;
        L_0x0167:
            r4 = 0;
            r0 = r31;
            r0.mFullWifiLockOut = r4;
            r4 = r34.readInt();
            if (r4 == 0) goto L_0x01d0;
        L_0x0172:
            r4 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
            r6 = 5;
            r0 = r31;
            r5 = r0.this$0;
            r7 = r5.mFullWifiLockTimers;
            r0 = r31;
            r5 = r0.this$0;
            r8 = r5.mOnBatteryTimeBase;
            r5 = r31;
            r9 = r34;
            r4.<init>(r5, r6, r7, r8, r9);
            r0 = r31;
            r0.mFullWifiLockTimer = r4;
        L_0x018c:
            r4 = 0;
            r0 = r31;
            r0.mWifiScanStarted = r4;
            r4 = r34.readInt();
            if (r4 == 0) goto L_0x01d6;
        L_0x0197:
            r4 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
            r6 = 6;
            r0 = r31;
            r5 = r0.this$0;
            r7 = r5.mWifiScanTimers;
            r0 = r31;
            r5 = r0.this$0;
            r8 = r5.mOnBatteryTimeBase;
            r5 = r31;
            r9 = r34;
            r4.<init>(r5, r6, r7, r8, r9);
            r0 = r31;
            r0.mWifiScanTimer = r4;
        L_0x01b1:
            r4 = -1;
            r0 = r31;
            r0.mWifiBatchedScanBinStarted = r4;
            r10 = 0;
        L_0x01b7:
            r4 = 5;
            if (r10 >= r4) goto L_0x01e4;
        L_0x01ba:
            r4 = r34.readInt();
            if (r4 == 0) goto L_0x01dc;
        L_0x01c0:
            r0 = r31;
            r1 = r34;
            r0.makeWifiBatchedScanBin(r10, r1);
        L_0x01c7:
            r10 = r10 + 1;
            goto L_0x01b7;
        L_0x01ca:
            r4 = 0;
            r0 = r31;
            r0.mWifiRunningTimer = r4;
            goto L_0x0167;
        L_0x01d0:
            r4 = 0;
            r0 = r31;
            r0.mFullWifiLockTimer = r4;
            goto L_0x018c;
        L_0x01d6:
            r4 = 0;
            r0 = r31;
            r0.mWifiScanTimer = r4;
            goto L_0x01b1;
        L_0x01dc:
            r0 = r31;
            r4 = r0.mWifiBatchedScanTimer;
            r5 = 0;
            r4[r10] = r5;
            goto L_0x01c7;
        L_0x01e4:
            r4 = 0;
            r0 = r31;
            r0.mWifiMulticastEnabled = r4;
            r4 = r34.readInt();
            if (r4 == 0) goto L_0x0280;
        L_0x01ef:
            r4 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
            r6 = 7;
            r0 = r31;
            r5 = r0.this$0;
            r7 = r5.mWifiMulticastTimers;
            r0 = r31;
            r5 = r0.this$0;
            r8 = r5.mOnBatteryTimeBase;
            r5 = r31;
            r9 = r34;
            r4.<init>(r5, r6, r7, r8, r9);
            r0 = r31;
            r0.mWifiMulticastTimer = r4;
        L_0x0209:
            r4 = r34.readInt();
            if (r4 == 0) goto L_0x0286;
        L_0x020f:
            r4 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
            r6 = 15;
            r0 = r31;
            r5 = r0.this$0;
            r7 = r5.mAudioTurnedOnTimers;
            r0 = r31;
            r5 = r0.this$0;
            r8 = r5.mOnBatteryTimeBase;
            r5 = r31;
            r9 = r34;
            r4.<init>(r5, r6, r7, r8, r9);
            r0 = r31;
            r0.mAudioTurnedOnTimer = r4;
        L_0x022a:
            r4 = r34.readInt();
            if (r4 == 0) goto L_0x028c;
        L_0x0230:
            r4 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
            r6 = 8;
            r0 = r31;
            r5 = r0.this$0;
            r7 = r5.mVideoTurnedOnTimers;
            r0 = r31;
            r5 = r0.this$0;
            r8 = r5.mOnBatteryTimeBase;
            r5 = r31;
            r9 = r34;
            r4.<init>(r5, r6, r7, r8, r9);
            r0 = r31;
            r0.mVideoTurnedOnTimer = r4;
        L_0x024b:
            r4 = r34.readInt();
            if (r4 == 0) goto L_0x0292;
        L_0x0251:
            r4 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
            r6 = 10;
            r7 = 0;
            r0 = r31;
            r5 = r0.this$0;
            r8 = r5.mOnBatteryTimeBase;
            r5 = r31;
            r9 = r34;
            r4.<init>(r5, r6, r7, r8, r9);
            r0 = r31;
            r0.mForegroundActivityTimer = r4;
        L_0x0267:
            r4 = 3;
            r0 = r31;
            r0.mProcessState = r4;
            r10 = 0;
        L_0x026d:
            r4 = 3;
            if (r10 >= r4) goto L_0x02a0;
        L_0x0270:
            r4 = r34.readInt();
            if (r4 == 0) goto L_0x0298;
        L_0x0276:
            r0 = r31;
            r1 = r34;
            r0.makeProcessState(r10, r1);
        L_0x027d:
            r10 = r10 + 1;
            goto L_0x026d;
        L_0x0280:
            r4 = 0;
            r0 = r31;
            r0.mWifiMulticastTimer = r4;
            goto L_0x0209;
        L_0x0286:
            r4 = 0;
            r0 = r31;
            r0.mAudioTurnedOnTimer = r4;
            goto L_0x022a;
        L_0x028c:
            r4 = 0;
            r0 = r31;
            r0.mVideoTurnedOnTimer = r4;
            goto L_0x024b;
        L_0x0292:
            r4 = 0;
            r0 = r31;
            r0.mForegroundActivityTimer = r4;
            goto L_0x0267;
        L_0x0298:
            r0 = r31;
            r4 = r0.mProcessStateTimer;
            r5 = 0;
            r4[r10] = r5;
            goto L_0x027d;
        L_0x02a0:
            r4 = r34.readInt();
            if (r4 == 0) goto L_0x02e2;
        L_0x02a6:
            r4 = new com.android.internal.os.BatteryStatsImpl$BatchTimer;
            r5 = 9;
            r0 = r31;
            r6 = r0.this$0;
            r6 = r6.mOnBatteryTimeBase;
            r0 = r31;
            r1 = r34;
            r4.<init>(r0, r5, r6, r1);
            r0 = r31;
            r0.mVibratorOnTimer = r4;
        L_0x02bb:
            r4 = r34.readInt();
            if (r4 == 0) goto L_0x02e8;
        L_0x02c1:
            r4 = 3;
            r4 = new com.android.internal.os.BatteryStatsImpl.Counter[r4];
            r0 = r31;
            r0.mUserActivityCounters = r4;
            r10 = 0;
        L_0x02c9:
            r4 = 3;
            if (r10 >= r4) goto L_0x02ed;
        L_0x02cc:
            r0 = r31;
            r4 = r0.mUserActivityCounters;
            r5 = new com.android.internal.os.BatteryStatsImpl$Counter;
            r0 = r31;
            r6 = r0.this$0;
            r6 = r6.mOnBatteryTimeBase;
            r0 = r34;
            r5.<init>(r6, r0);
            r4[r10] = r5;
            r10 = r10 + 1;
            goto L_0x02c9;
        L_0x02e2:
            r4 = 0;
            r0 = r31;
            r0.mVibratorOnTimer = r4;
            goto L_0x02bb;
        L_0x02e8:
            r4 = 0;
            r0 = r31;
            r0.mUserActivityCounters = r4;
        L_0x02ed:
            r4 = r34.readInt();
            if (r4 == 0) goto L_0x0351;
        L_0x02f3:
            r4 = 4;
            r4 = new com.android.internal.os.BatteryStatsImpl.LongSamplingCounter[r4];
            r0 = r31;
            r0.mNetworkByteActivityCounters = r4;
            r4 = 4;
            r4 = new com.android.internal.os.BatteryStatsImpl.LongSamplingCounter[r4];
            r0 = r31;
            r0.mNetworkPacketActivityCounters = r4;
            r10 = 0;
        L_0x0302:
            r4 = 4;
            if (r10 >= r4) goto L_0x032e;
        L_0x0305:
            r0 = r31;
            r4 = r0.mNetworkByteActivityCounters;
            r5 = new com.android.internal.os.BatteryStatsImpl$LongSamplingCounter;
            r0 = r31;
            r6 = r0.this$0;
            r6 = r6.mOnBatteryTimeBase;
            r0 = r34;
            r5.<init>(r6, r0);
            r4[r10] = r5;
            r0 = r31;
            r4 = r0.mNetworkPacketActivityCounters;
            r5 = new com.android.internal.os.BatteryStatsImpl$LongSamplingCounter;
            r0 = r31;
            r6 = r0.this$0;
            r6 = r6.mOnBatteryTimeBase;
            r0 = r34;
            r5.<init>(r6, r0);
            r4[r10] = r5;
            r10 = r10 + 1;
            goto L_0x0302;
        L_0x032e:
            r4 = new com.android.internal.os.BatteryStatsImpl$LongSamplingCounter;
            r0 = r31;
            r5 = r0.this$0;
            r5 = r5.mOnBatteryTimeBase;
            r0 = r34;
            r4.<init>(r5, r0);
            r0 = r31;
            r0.mMobileRadioActiveTime = r4;
            r4 = new com.android.internal.os.BatteryStatsImpl$LongSamplingCounter;
            r0 = r31;
            r5 = r0.this$0;
            r5 = r5.mOnBatteryTimeBase;
            r0 = r34;
            r4.<init>(r5, r0);
            r0 = r31;
            r0.mMobileRadioActiveCount = r4;
        L_0x0350:
            return;
        L_0x0351:
            r4 = 0;
            r0 = r31;
            r0.mNetworkByteActivityCounters = r4;
            r4 = 0;
            r0 = r31;
            r0.mNetworkPacketActivityCounters = r4;
            goto L_0x0350;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.readFromParcelLocked(com.android.internal.os.BatteryStatsImpl$TimeBase, com.android.internal.os.BatteryStatsImpl$TimeBase, android.os.Parcel):void");
        }

        public com.android.internal.os.BatteryStatsImpl.Uid.Proc getProcessStatsLocked(java.lang.String r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r2 = this;
            r1 = r2.mProcessStats;
            r0 = r1.get(r3);
            r0 = (com.android.internal.os.BatteryStatsImpl.Uid.Proc) r0;
            if (r0 != 0) goto L_0x0014;
        L_0x000a:
            r0 = new com.android.internal.os.BatteryStatsImpl$Uid$Proc;
            r0.<init>(r2, r3);
            r1 = r2.mProcessStats;
            r1.put(r3, r0);
        L_0x0014:
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getProcessStatsLocked(java.lang.String):com.android.internal.os.BatteryStatsImpl$Uid$Proc");
        }

        public void updateProcessStateLocked(java.lang.String r4, int r5, long r6) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r3 = this;
            r1 = 3;
            if (r5 > r1) goto L_0x0008;
        L_0x0003:
            r0 = 0;
        L_0x0004:
            r3.updateRealProcessStateLocked(r4, r0, r6);
            return;
        L_0x0008:
            r1 = 8;
            if (r5 > r1) goto L_0x000e;
        L_0x000c:
            r0 = 1;
            goto L_0x0004;
        L_0x000e:
            r0 = 2;
            goto L_0x0004;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.updateProcessStateLocked(java.lang.String, int, long):void");
        }

        public void updateRealProcessStateLocked(java.lang.String r8, int r9, long r10) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r7 = this;
            r0 = 1;
            r4 = 0;
            r2 = r7.getProcessStatsLocked(r8);
            r5 = r2.mProcessState;
            if (r5 == r9) goto L_0x0040;
        L_0x000a:
            r5 = r2.mProcessState;
            if (r9 >= r5) goto L_0x0034;
        L_0x000e:
            r5 = r7.mProcessState;
            if (r5 <= r9) goto L_0x0032;
        L_0x0012:
            r2.mProcessState = r9;
            if (r0 == 0) goto L_0x0040;
        L_0x0016:
            r3 = 3;
            r4 = r7.mProcessStats;
            r4 = r4.size();
            r1 = r4 + -1;
        L_0x001f:
            if (r1 < 0) goto L_0x003d;
        L_0x0021:
            r4 = r7.mProcessStats;
            r2 = r4.valueAt(r1);
            r2 = (com.android.internal.os.BatteryStatsImpl.Uid.Proc) r2;
            r4 = r2.mProcessState;
            if (r4 >= r3) goto L_0x002f;
        L_0x002d:
            r3 = r2.mProcessState;
        L_0x002f:
            r1 = r1 + -1;
            goto L_0x001f;
        L_0x0032:
            r0 = r4;
            goto L_0x0012;
        L_0x0034:
            r5 = r7.mProcessState;
            r6 = r2.mProcessState;
            if (r5 != r6) goto L_0x003b;
        L_0x003a:
            goto L_0x0012;
        L_0x003b:
            r0 = r4;
            goto L_0x003a;
        L_0x003d:
            r7.updateUidProcessStateLocked(r3, r10);
        L_0x0040:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.updateRealProcessStateLocked(java.lang.String, int, long):void");
        }

        public android.util.SparseArray<? extends android.os.BatteryStats.Uid.Pid> getPidStats() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.mPids;
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getPidStats():android.util.SparseArray<? extends android.os.BatteryStats$Uid$Pid>");
        }

        public android.os.BatteryStats.Uid.Pid getPidStatsLocked(int r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r2 = this;
            r1 = r2.mPids;
            r0 = r1.get(r3);
            r0 = (android.os.BatteryStats.Uid.Pid) r0;
            if (r0 != 0) goto L_0x0014;
        L_0x000a:
            r0 = new android.os.BatteryStats$Uid$Pid;
            r0.<init>(r2);
            r1 = r2.mPids;
            r1.put(r3, r0);
        L_0x0014:
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getPidStatsLocked(int):android.os.BatteryStats$Uid$Pid");
        }

        public com.android.internal.os.BatteryStatsImpl.Uid.Pkg getPackageStatsLocked(java.lang.String r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r2 = this;
            r1 = r2.mPackageStats;
            r0 = r1.get(r3);
            r0 = (com.android.internal.os.BatteryStatsImpl.Uid.Pkg) r0;
            if (r0 != 0) goto L_0x0014;
        L_0x000a:
            r0 = new com.android.internal.os.BatteryStatsImpl$Uid$Pkg;
            r0.<init>(r2);
            r1 = r2.mPackageStats;
            r1.put(r3, r0);
        L_0x0014:
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getPackageStatsLocked(java.lang.String):com.android.internal.os.BatteryStatsImpl$Uid$Pkg");
        }

        public com.android.internal.os.BatteryStatsImpl.Uid.Pkg.Serv getServiceStatsLocked(java.lang.String r4, java.lang.String r5) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r3 = this;
            r0 = r3.getPackageStatsLocked(r4);
            r2 = r0.mServiceStats;
            r1 = r2.get(r5);
            r1 = (com.android.internal.os.BatteryStatsImpl.Uid.Pkg.Serv) r1;
            if (r1 != 0) goto L_0x0017;
        L_0x000e:
            r1 = r0.newServiceStatsLocked();
            r2 = r0.mServiceStats;
            r2.put(r5, r1);
        L_0x0017:
            return r1;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getServiceStatsLocked(java.lang.String, java.lang.String):com.android.internal.os.BatteryStatsImpl$Uid$Pkg$Serv");
        }

        public void readSyncSummaryFromParcelLocked(java.lang.String r3, android.os.Parcel r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r2 = this;
            r1 = r2.mSyncStats;
            r0 = r1.instantiateObject();
            r0 = (com.android.internal.os.BatteryStatsImpl.StopwatchTimer) r0;
            r0.readSummaryFromParcelLocked(r4);
            r1 = r2.mSyncStats;
            r1.add(r3, r0);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.readSyncSummaryFromParcelLocked(java.lang.String, android.os.Parcel):void");
        }

        public void readJobSummaryFromParcelLocked(java.lang.String r3, android.os.Parcel r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r2 = this;
            r1 = r2.mJobStats;
            r0 = r1.instantiateObject();
            r0 = (com.android.internal.os.BatteryStatsImpl.StopwatchTimer) r0;
            r0.readSummaryFromParcelLocked(r4);
            r1 = r2.mJobStats;
            r1.add(r3, r0);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.readJobSummaryFromParcelLocked(java.lang.String, android.os.Parcel):void");
        }

        public void readWakeSummaryFromParcelLocked(java.lang.String r3, android.os.Parcel r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r2 = this;
            r0 = new com.android.internal.os.BatteryStatsImpl$Uid$Wakelock;
            r0.<init>(r2);
            r1 = r2.mWakelockStats;
            r1.add(r3, r0);
            r1 = r4.readInt();
            if (r1 == 0) goto L_0x0018;
        L_0x0010:
            r1 = 1;
            r1 = r0.getStopwatchTimer(r1);
            r1.readSummaryFromParcelLocked(r4);
        L_0x0018:
            r1 = r4.readInt();
            if (r1 == 0) goto L_0x0026;
        L_0x001e:
            r1 = 0;
            r1 = r0.getStopwatchTimer(r1);
            r1.readSummaryFromParcelLocked(r4);
        L_0x0026:
            r1 = r4.readInt();
            if (r1 == 0) goto L_0x0034;
        L_0x002c:
            r1 = 2;
            r1 = r0.getStopwatchTimer(r1);
            r1.readSummaryFromParcelLocked(r4);
        L_0x0034:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.readWakeSummaryFromParcelLocked(java.lang.String, android.os.Parcel):void");
        }

        public com.android.internal.os.BatteryStatsImpl.StopwatchTimer getSensorTimerLocked(int r6, boolean r7) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r5 = this;
            r3 = r5.mSensorStats;
            r0 = r3.get(r6);
            r0 = (com.android.internal.os.BatteryStatsImpl.Uid.Sensor) r0;
            if (r0 != 0) goto L_0x0018;
        L_0x000a:
            if (r7 != 0) goto L_0x000e;
        L_0x000c:
            r1 = 0;
        L_0x000d:
            return r1;
        L_0x000e:
            r0 = new com.android.internal.os.BatteryStatsImpl$Uid$Sensor;
            r0.<init>(r5, r6);
            r3 = r5.mSensorStats;
            r3.put(r6, r0);
        L_0x0018:
            r1 = r0.mTimer;
            if (r1 != 0) goto L_0x000d;
        L_0x001c:
            r3 = r5.this$0;
            r3 = r3.mSensorTimers;
            r2 = r3.get(r6);
            r2 = (java.util.ArrayList) r2;
            if (r2 != 0) goto L_0x0034;
        L_0x0028:
            r2 = new java.util.ArrayList;
            r2.<init>();
            r3 = r5.this$0;
            r3 = r3.mSensorTimers;
            r3.put(r6, r2);
        L_0x0034:
            r1 = new com.android.internal.os.BatteryStatsImpl$StopwatchTimer;
            r3 = 3;
            r4 = r5.this$0;
            r4 = r4.mOnBatteryTimeBase;
            r1.<init>(r5, r3, r2, r4);
            r0.mTimer = r1;
            goto L_0x000d;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getSensorTimerLocked(int, boolean):com.android.internal.os.BatteryStatsImpl$StopwatchTimer");
        }

        public void noteStartSyncLocked(java.lang.String r3, long r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r2 = this;
            r1 = r2.mSyncStats;
            r0 = r1.startObject(r3);
            r0 = (com.android.internal.os.BatteryStatsImpl.StopwatchTimer) r0;
            if (r0 == 0) goto L_0x000d;
        L_0x000a:
            r0.startRunningLocked(r4);
        L_0x000d:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteStartSyncLocked(java.lang.String, long):void");
        }

        public void noteStopSyncLocked(java.lang.String r3, long r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r2 = this;
            r1 = r2.mSyncStats;
            r0 = r1.stopObject(r3);
            r0 = (com.android.internal.os.BatteryStatsImpl.StopwatchTimer) r0;
            if (r0 == 0) goto L_0x000d;
        L_0x000a:
            r0.stopRunningLocked(r4);
        L_0x000d:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteStopSyncLocked(java.lang.String, long):void");
        }

        public void noteStartJobLocked(java.lang.String r3, long r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r2 = this;
            r1 = r2.mJobStats;
            r0 = r1.startObject(r3);
            r0 = (com.android.internal.os.BatteryStatsImpl.StopwatchTimer) r0;
            if (r0 == 0) goto L_0x000d;
        L_0x000a:
            r0.startRunningLocked(r4);
        L_0x000d:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteStartJobLocked(java.lang.String, long):void");
        }

        public void noteStopJobLocked(java.lang.String r3, long r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r2 = this;
            r1 = r2.mJobStats;
            r0 = r1.stopObject(r3);
            r0 = (com.android.internal.os.BatteryStatsImpl.StopwatchTimer) r0;
            if (r0 == 0) goto L_0x000d;
        L_0x000a:
            r0.stopRunningLocked(r4);
        L_0x000d:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteStopJobLocked(java.lang.String, long):void");
        }

        public void noteStartWakeLocked(int r5, java.lang.String r6, int r7, long r8) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r4 = this;
            r2 = r4.mWakelockStats;
            r1 = r2.startObject(r6);
            r1 = (com.android.internal.os.BatteryStatsImpl.Uid.Wakelock) r1;
            if (r1 == 0) goto L_0x0011;
        L_0x000a:
            r2 = r1.getStopwatchTimer(r7);
            r2.startRunningLocked(r8);
        L_0x0011:
            if (r5 < 0) goto L_0x0023;
        L_0x0013:
            if (r7 != 0) goto L_0x0023;
        L_0x0015:
            r0 = r4.getPidStatsLocked(r5);
            r2 = r0.mWakeNesting;
            r3 = r2 + 1;
            r0.mWakeNesting = r3;
            if (r2 != 0) goto L_0x0023;
        L_0x0021:
            r0.mWakeStartMs = r8;
        L_0x0023:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteStartWakeLocked(int, java.lang.String, int, long):void");
        }

        public void noteStopWakeLocked(int r7, java.lang.String r8, int r9, long r10) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r6 = this;
            r2 = r6.mWakelockStats;
            r1 = r2.stopObject(r8);
            r1 = (com.android.internal.os.BatteryStatsImpl.Uid.Wakelock) r1;
            if (r1 == 0) goto L_0x0011;
        L_0x000a:
            r2 = r1.getStopwatchTimer(r9);
            r2.stopRunningLocked(r10);
        L_0x0011:
            if (r7 < 0) goto L_0x0039;
        L_0x0013:
            if (r9 != 0) goto L_0x0039;
        L_0x0015:
            r2 = r6.mPids;
            r0 = r2.get(r7);
            r0 = (android.os.BatteryStats.Uid.Pid) r0;
            if (r0 == 0) goto L_0x0039;
        L_0x001f:
            r2 = r0.mWakeNesting;
            if (r2 <= 0) goto L_0x0039;
        L_0x0023:
            r2 = r0.mWakeNesting;
            r3 = r2 + -1;
            r0.mWakeNesting = r3;
            r3 = 1;
            if (r2 != r3) goto L_0x0039;
        L_0x002c:
            r2 = r0.mWakeSumMs;
            r4 = r0.mWakeStartMs;
            r4 = r10 - r4;
            r2 = r2 + r4;
            r0.mWakeSumMs = r2;
            r2 = 0;
            r0.mWakeStartMs = r2;
        L_0x0039:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteStopWakeLocked(int, java.lang.String, int, long):void");
        }

        public void reportExcessiveWakeLocked(java.lang.String r3, long r4, long r6) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r2 = this;
            r0 = r2.getProcessStatsLocked(r3);
            if (r0 == 0) goto L_0x0009;
        L_0x0006:
            r0.addExcessiveWake(r4, r6);
        L_0x0009:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.reportExcessiveWakeLocked(java.lang.String, long, long):void");
        }

        public void reportExcessiveCpuLocked(java.lang.String r3, long r4, long r6) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r2 = this;
            r0 = r2.getProcessStatsLocked(r3);
            if (r0 == 0) goto L_0x0009;
        L_0x0006:
            r0.addExcessiveCpu(r4, r6);
        L_0x0009:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.reportExcessiveCpuLocked(java.lang.String, long, long):void");
        }

        public void noteStartSensor(int r3, long r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r2 = this;
            r1 = 1;
            r0 = r2.getSensorTimerLocked(r3, r1);
            if (r0 == 0) goto L_0x000a;
        L_0x0007:
            r0.startRunningLocked(r4);
        L_0x000a:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteStartSensor(int, long):void");
        }

        public void noteStopSensor(int r3, long r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r2 = this;
            r1 = 0;
            r0 = r2.getSensorTimerLocked(r3, r1);
            if (r0 == 0) goto L_0x000a;
        L_0x0007:
            r0.stopRunningLocked(r4);
        L_0x000a:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteStopSensor(int, long):void");
        }

        public void noteStartGps(long r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r3 = this;
            r1 = -10000; // 0xffffffffffffd8f0 float:NaN double:NaN;
            r2 = 1;
            r0 = r3.getSensorTimerLocked(r1, r2);
            if (r0 == 0) goto L_0x000c;
        L_0x0009:
            r0.startRunningLocked(r4);
        L_0x000c:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteStartGps(long):void");
        }

        public void noteStopGps(long r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r3 = this;
            r1 = -10000; // 0xffffffffffffd8f0 float:NaN double:NaN;
            r2 = 0;
            r0 = r3.getSensorTimerLocked(r1, r2);
            if (r0 == 0) goto L_0x000c;
        L_0x0009:
            r0.stopRunningLocked(r4);
        L_0x000c:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.noteStopGps(long):void");
        }

        public com.android.internal.os.BatteryStatsImpl getBatteryStats() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r1 = this;
            r0 = r1.this$0;
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.os.BatteryStatsImpl.Uid.getBatteryStats():com.android.internal.os.BatteryStatsImpl");
        }
    }

    public Map<String, ? extends Timer> getKernelWakelockStats() {
        return this.mKernelWakelockStats;
    }

    static {
        sKernelWakelockUpdateVersion = BATTERY_PLUGGED_NONE;
        PROC_WAKELOCKS_FORMAT = new int[]{5129, 8201, 9, 9, 9, 8201};
        WAKEUP_SOURCES_FORMAT = new int[]{4105, 8457, R.styleable.Theme_textColorSearchUrl, R.styleable.Theme_textColorSearchUrl, R.styleable.Theme_textColorSearchUrl, R.styleable.Theme_textColorSearchUrl, 8457};
        CREATOR = new AnonymousClass3();
    }

    public Map<String, ? extends Timer> getWakeupReasonStats() {
        return this.mWakeupReasonStats;
    }

    public BatteryStatsImpl() {
        this.mIsolatedUids = new SparseIntArray();
        this.mUidStats = new SparseArray();
        this.mPartialTimers = new ArrayList();
        this.mFullTimers = new ArrayList();
        this.mWindowTimers = new ArrayList();
        this.mSensorTimers = new SparseArray();
        this.mWifiRunningTimers = new ArrayList();
        this.mFullWifiLockTimers = new ArrayList();
        this.mWifiMulticastTimers = new ArrayList();
        this.mWifiScanTimers = new ArrayList();
        this.mWifiBatchedScanTimers = new SparseArray();
        this.mAudioTurnedOnTimers = new ArrayList();
        this.mVideoTurnedOnTimers = new ArrayList();
        this.mLastPartialTimers = new ArrayList();
        this.mOnBatteryTimeBase = new TimeBase();
        this.mOnBatteryScreenOffTimeBase = new TimeBase();
        this.mActiveEvents = new HistoryEventTracker();
        this.mHaveBatteryLevel = USE_OLD_HISTORY;
        this.mRecordingHistory = USE_OLD_HISTORY;
        this.mHistoryBuffer = Parcel.obtain();
        this.mHistoryLastWritten = new HistoryItem();
        this.mHistoryLastLastWritten = new HistoryItem();
        this.mHistoryReadTmp = new HistoryItem();
        this.mHistoryAddTmp = new HistoryItem();
        this.mHistoryTagPool = new HashMap();
        this.mNextHistoryTagIdx = BATTERY_PLUGGED_NONE;
        this.mNumHistoryTagChars = BATTERY_PLUGGED_NONE;
        this.mHistoryBufferLastPos = -1;
        this.mHistoryOverflow = USE_OLD_HISTORY;
        this.mLastHistoryElapsedRealtime = 0;
        this.mTrackRunningHistoryElapsedRealtime = 0;
        this.mTrackRunningHistoryUptime = 0;
        this.mHistoryCur = new HistoryItem();
        this.mScreenState = BATTERY_PLUGGED_NONE;
        this.mScreenBrightnessBin = -1;
        this.mScreenBrightnessTimer = new StopwatchTimer[5];
        this.mPhoneSignalStrengthBin = -1;
        this.mPhoneSignalStrengthBinRaw = -1;
        this.mPhoneSignalStrengthsTimer = new StopwatchTimer[5];
        this.mPhoneDataConnectionType = -1;
        this.mPhoneDataConnectionsTimer = new StopwatchTimer[17];
        this.mNetworkByteActivityCounters = new LongSamplingCounter[4];
        this.mNetworkPacketActivityCounters = new LongSamplingCounter[4];
        this.mWifiState = -1;
        this.mWifiStateTimer = new StopwatchTimer[8];
        this.mWifiSupplState = -1;
        this.mWifiSupplStateTimer = new StopwatchTimer[13];
        this.mWifiSignalStrengthBin = -1;
        this.mWifiSignalStrengthsTimer = new StopwatchTimer[5];
        this.mBluetoothState = -1;
        this.mBluetoothStateTimer = new StopwatchTimer[4];
        this.mMobileRadioPowerState = DataConnectionRealTimeInfo.DC_POWER_STATE_LOW;
        this.mInitStepMode = BATTERY_PLUGGED_NONE;
        this.mCurStepMode = BATTERY_PLUGGED_NONE;
        this.mModStepMode = BATTERY_PLUGGED_NONE;
        this.mDischargeStepDurations = new long[MAX_LEVEL_STEPS];
        this.mChargeStepDurations = new long[MAX_LEVEL_STEPS];
        this.mLastWriteTime = 0;
        this.mBluetoothPingStart = -1;
        this.mPhoneServiceState = -1;
        this.mPhoneServiceStateRaw = -1;
        this.mPhoneSimStateRaw = -1;
        this.mKernelWakelockStats = new HashMap();
        this.mLastWakeupReason = null;
        this.mLastWakeupUptimeMs = 0;
        this.mWakeupReasonStats = new HashMap();
        this.mProcWakelocksName = new String[STATE_BATTERY_PLUG_MASK];
        this.mProcWakelocksData = new long[STATE_BATTERY_PLUG_MASK];
        this.mProcWakelockFileStats = new HashMap();
        this.mNetworkStatsFactory = new NetworkStatsFactory();
        this.mCurMobileSnapshot = new NetworkStats(SystemClock.elapsedRealtime(), 50);
        this.mLastMobileSnapshot = new NetworkStats(SystemClock.elapsedRealtime(), 50);
        this.mCurWifiSnapshot = new NetworkStats(SystemClock.elapsedRealtime(), 50);
        this.mLastWifiSnapshot = new NetworkStats(SystemClock.elapsedRealtime(), 50);
        this.mTmpNetworkStatsEntry = new Entry();
        this.mMobileIfaces = new String[BATTERY_PLUGGED_NONE];
        this.mWifiIfaces = new String[BATTERY_PLUGGED_NONE];
        this.mChangedStates = BATTERY_PLUGGED_NONE;
        this.mChangedStates2 = BATTERY_PLUGGED_NONE;
        this.mInitialAcquireWakeUid = -1;
        this.mWifiFullLockNesting = BATTERY_PLUGGED_NONE;
        this.mWifiScanNesting = BATTERY_PLUGGED_NONE;
        this.mWifiMulticastNesting = BATTERY_PLUGGED_NONE;
        this.mPendingWrite = null;
        this.mWriteLock = new ReentrantLock();
        this.mFile = null;
        this.mCheckinFile = null;
        this.mHandler = null;
        clearHistoryLocked();
    }

    public SamplingTimer getWakeupReasonTimerLocked(String name) {
        SamplingTimer timer = (SamplingTimer) this.mWakeupReasonStats.get(name);
        if (timer != null) {
            return timer;
        }
        timer = new SamplingTimer(this.mOnBatteryTimeBase, true);
        this.mWakeupReasonStats.put(name, timer);
        return timer;
    }

    private final Map<String, KernelWakelockStats> readKernelWakelockStats() {
        FileInputStream is;
        byte[] buffer = new byte[DateUtils.FORMAT_UTC];
        boolean wakeup_sources = USE_OLD_HISTORY;
        try {
            is = new FileInputStream("/proc/wakelocks");
        } catch (FileNotFoundException e) {
            try {
                is = new FileInputStream("/d/wakeup_sources");
                wakeup_sources = true;
            } catch (FileNotFoundException e2) {
                return null;
            }
        }
        try {
            int len = is.read(buffer);
            is.close();
            if (len > 0) {
                for (int i = BATTERY_PLUGGED_NONE; i < len; i += NET_UPDATE_MOBILE) {
                    if (buffer[i] == null) {
                        len = i;
                        break;
                    }
                }
            }
            return parseProcWakelocks(buffer, len, wakeup_sources);
        } catch (IOException e3) {
            return null;
        }
    }

    private final Map<String, KernelWakelockStats> parseProcWakelocks(byte[] wlBuffer, int len, boolean wakeup_sources) {
        Map<String, KernelWakelockStats> m;
        int numUpdatedWlNames = BATTERY_PLUGGED_NONE;
        int i = BATTERY_PLUGGED_NONE;
        while (i < len && wlBuffer[i] != 10 && wlBuffer[i] != null) {
            i += NET_UPDATE_MOBILE;
        }
        int endIndex = i + NET_UPDATE_MOBILE;
        int startIndex = endIndex;
        synchronized (this) {
            m = this.mProcWakelockFileStats;
            sKernelWakelockUpdateVersion += NET_UPDATE_MOBILE;
            while (endIndex < len) {
                endIndex = startIndex;
                while (endIndex < len && wlBuffer[endIndex] != 10 && wlBuffer[endIndex] != null) {
                    endIndex += NET_UPDATE_MOBILE;
                }
                endIndex += NET_UPDATE_MOBILE;
                if (endIndex >= len - 1) {
                    break;
                }
                long totalTime;
                String[] nameStringArray = this.mProcWakelocksName;
                long[] wlData = this.mProcWakelocksData;
                for (int j = startIndex; j < endIndex; j += NET_UPDATE_MOBILE) {
                    if ((wlBuffer[j] & RILConstants.RIL_REQUEST_SET_DATA_PROFILE) != 0) {
                        wlBuffer[j] = (byte) 63;
                    }
                }
                boolean parsed = Process.parseProcLine(wlBuffer, startIndex, endIndex, wakeup_sources ? WAKEUP_SOURCES_FORMAT : PROC_WAKELOCKS_FORMAT, nameStringArray, wlData, null);
                String name = nameStringArray[BATTERY_PLUGGED_NONE];
                int count = (int) wlData[NET_UPDATE_MOBILE];
                if (wakeup_sources) {
                    totalTime = wlData[NET_UPDATE_WIFI] * 1000;
                } else {
                    totalTime = (wlData[NET_UPDATE_WIFI] + 500) / 1000;
                }
                if (parsed && name.length() > 0) {
                    if (m.containsKey(name)) {
                        KernelWakelockStats kwlStats = (KernelWakelockStats) m.get(name);
                        if (kwlStats.mVersion == sKernelWakelockUpdateVersion) {
                            kwlStats.mCount += count;
                            kwlStats.mTotalTime += totalTime;
                        } else {
                            kwlStats.mCount = count;
                            kwlStats.mTotalTime = totalTime;
                            kwlStats.mVersion = sKernelWakelockUpdateVersion;
                            numUpdatedWlNames += NET_UPDATE_MOBILE;
                        }
                    } else {
                        m.put(name, new KernelWakelockStats(this, count, totalTime, sKernelWakelockUpdateVersion));
                        numUpdatedWlNames += NET_UPDATE_MOBILE;
                    }
                }
                startIndex = endIndex;
            }
            if (m.size() != numUpdatedWlNames) {
                Iterator<KernelWakelockStats> itr = m.values().iterator();
                while (itr.hasNext()) {
                    if (((KernelWakelockStats) itr.next()).mVersion != sKernelWakelockUpdateVersion) {
                        itr.remove();
                    }
                }
            }
        }
        return m;
    }

    public SamplingTimer getKernelWakelockTimerLocked(String name) {
        SamplingTimer kwlt = (SamplingTimer) this.mKernelWakelockStats.get(name);
        if (kwlt != null) {
            return kwlt;
        }
        kwlt = new SamplingTimer(this.mOnBatteryScreenOffTimeBase, true);
        this.mKernelWakelockStats.put(name, kwlt);
        return kwlt;
    }

    private int getCurrentBluetoothPingCount() {
        if (this.mBtHeadset != null) {
            List<BluetoothDevice> deviceList = this.mBtHeadset.getConnectedDevices();
            if (deviceList.size() > 0) {
                return this.mBtHeadset.getBatteryUsageHint((BluetoothDevice) deviceList.get(BATTERY_PLUGGED_NONE));
            }
        }
        return -1;
    }

    public int getBluetoothPingCount() {
        if (this.mBluetoothPingStart == -1) {
            return this.mBluetoothPingCount;
        }
        if (this.mBtHeadset != null) {
            return getCurrentBluetoothPingCount() - this.mBluetoothPingStart;
        }
        return BATTERY_PLUGGED_NONE;
    }

    public void setBtHeadset(BluetoothHeadset headset) {
        if (headset != null && this.mBtHeadset == null && isOnBattery() && this.mBluetoothPingStart == -1) {
            this.mBluetoothPingStart = getCurrentBluetoothPingCount();
        }
        this.mBtHeadset = headset;
    }

    private int writeHistoryTag(HistoryTag tag) {
        Integer idxObj = (Integer) this.mHistoryTagPool.get(tag);
        if (idxObj != null) {
            return idxObj.intValue();
        }
        int idx = this.mNextHistoryTagIdx;
        HistoryTag key = new HistoryTag();
        key.setTo(tag);
        tag.poolIdx = idx;
        this.mHistoryTagPool.put(key, Integer.valueOf(idx));
        this.mNextHistoryTagIdx += NET_UPDATE_MOBILE;
        this.mNumHistoryTagChars += key.string.length() + NET_UPDATE_MOBILE;
        return idx;
    }

    private void readHistoryTag(int index, HistoryTag tag) {
        tag.string = this.mReadHistoryStrings[index];
        tag.uid = this.mReadHistoryUids[index];
        tag.poolIdx = index;
    }

    public void writeHistoryDelta(Parcel dest, HistoryItem cur, HistoryItem last) {
        if (last == null || cur.cmd != null) {
            dest.writeInt(DELTA_TIME_ABS);
            cur.writeToParcel(dest, BATTERY_PLUGGED_NONE);
            return;
        }
        int deltaTimeToken;
        long deltaTime = cur.time - last.time;
        int lastBatteryLevelInt = buildBatteryLevelInt(last);
        int lastStateInt = buildStateInt(last);
        if (deltaTime < 0 || deltaTime > 2147483647L) {
            deltaTimeToken = DELTA_TIME_MASK;
        } else if (deltaTime >= 524285) {
            deltaTimeToken = DELTA_TIME_INT;
        } else {
            deltaTimeToken = (int) deltaTime;
        }
        int firstToken = deltaTimeToken | (cur.states & DELTA_STATE_MASK);
        int batteryLevelInt = buildBatteryLevelInt(cur);
        boolean batteryLevelIntChanged = batteryLevelInt != lastBatteryLevelInt ? true : USE_OLD_HISTORY;
        if (batteryLevelIntChanged) {
            firstToken |= DELTA_BATTERY_LEVEL_FLAG;
        }
        int stateInt = buildStateInt(cur);
        boolean stateIntChanged = stateInt != lastStateInt ? true : USE_OLD_HISTORY;
        if (stateIntChanged) {
            firstToken |= DELTA_STATE_FLAG;
        }
        boolean state2IntChanged = cur.states2 != last.states2 ? true : USE_OLD_HISTORY;
        if (state2IntChanged) {
            firstToken |= DELTA_STATE2_FLAG;
        }
        if (!(cur.wakelockTag == null && cur.wakeReasonTag == null)) {
            firstToken |= DELTA_WAKELOCK_FLAG;
        }
        if (cur.eventCode != 0) {
            firstToken |= DELTA_EVENT_FLAG;
        }
        dest.writeInt(firstToken);
        if (deltaTimeToken >= DELTA_TIME_INT) {
            if (deltaTimeToken == DELTA_TIME_INT) {
                dest.writeInt((int) deltaTime);
            } else {
                dest.writeLong(deltaTime);
            }
        }
        if (batteryLevelIntChanged) {
            dest.writeInt(batteryLevelInt);
        }
        if (stateIntChanged) {
            dest.writeInt(stateInt);
        }
        if (state2IntChanged) {
            dest.writeInt(cur.states2);
        }
        if (!(cur.wakelockTag == null && cur.wakeReasonTag == null)) {
            int wakeLockIndex;
            int wakeReasonIndex;
            if (cur.wakelockTag != null) {
                wakeLockIndex = writeHistoryTag(cur.wakelockTag);
            } else {
                wakeLockIndex = NET_UPDATE_ALL;
            }
            if (cur.wakeReasonTag != null) {
                wakeReasonIndex = writeHistoryTag(cur.wakeReasonTag);
            } else {
                wakeReasonIndex = NET_UPDATE_ALL;
            }
            dest.writeInt((wakeReasonIndex << 16) | wakeLockIndex);
        }
        if (cur.eventCode != 0) {
            dest.writeInt((cur.eventCode & NET_UPDATE_ALL) | (writeHistoryTag(cur.eventTag) << 16));
        }
    }

    private int buildBatteryLevelInt(HistoryItem h) {
        return (((h.batteryLevel << 25) & -33554432) | ((h.batteryTemperature << 14) & 33538048)) | (h.batteryVoltage & 16383);
    }

    private int buildStateInt(HistoryItem h) {
        int plugType = BATTERY_PLUGGED_NONE;
        if ((h.batteryPlugType & NET_UPDATE_MOBILE) != 0) {
            plugType = NET_UPDATE_MOBILE;
        } else if ((h.batteryPlugType & NET_UPDATE_WIFI) != 0) {
            plugType = NET_UPDATE_WIFI;
        } else if ((h.batteryPlugType & 4) != 0) {
            plugType = STATE_BATTERY_PLUG_MASK;
        }
        return ((((h.batteryStatus & STATE_BATTERY_STATUS_MASK) << STATE_BATTERY_STATUS_SHIFT) | ((h.batteryHealth & STATE_BATTERY_STATUS_MASK) << STATE_BATTERY_HEALTH_SHIFT)) | ((plugType & STATE_BATTERY_PLUG_MASK) << STATE_BATTERY_PLUG_SHIFT)) | (h.states & AsyncService.CMD_ASYNC_SERVICE_ON_START_INTENT);
    }

    public void readHistoryDelta(Parcel src, HistoryItem cur) {
        int firstToken = src.readInt();
        int deltaTimeToken = firstToken & DELTA_TIME_MASK;
        cur.cmd = (byte) 0;
        cur.numReadInts = NET_UPDATE_MOBILE;
        if (deltaTimeToken < DELTA_TIME_ABS) {
            cur.time += (long) deltaTimeToken;
        } else if (deltaTimeToken == DELTA_TIME_ABS) {
            cur.time = src.readLong();
            cur.numReadInts += NET_UPDATE_WIFI;
            cur.readFromParcel(src);
            return;
        } else if (deltaTimeToken == DELTA_TIME_INT) {
            cur.time += (long) src.readInt();
            cur.numReadInts += NET_UPDATE_MOBILE;
        } else {
            cur.time += src.readLong();
            cur.numReadInts += NET_UPDATE_WIFI;
        }
        if ((DELTA_BATTERY_LEVEL_FLAG & firstToken) != 0) {
            int batteryLevelInt = src.readInt();
            cur.batteryLevel = (byte) ((batteryLevelInt >> 25) & RILConstants.RIL_REQUEST_SET_DC_RT_INFO_RATE);
            cur.batteryTemperature = (short) ((batteryLevelInt << STATE_BATTERY_STATUS_MASK) >> 21);
            cur.batteryVoltage = (char) (batteryLevelInt & 16383);
            cur.numReadInts += NET_UPDATE_MOBILE;
        }
        if ((DELTA_STATE_FLAG & firstToken) != 0) {
            int stateInt = src.readInt();
            cur.states = (DELTA_STATE_MASK & firstToken) | (AsyncService.CMD_ASYNC_SERVICE_ON_START_INTENT & stateInt);
            cur.batteryStatus = (byte) ((stateInt >> STATE_BATTERY_STATUS_SHIFT) & STATE_BATTERY_STATUS_MASK);
            cur.batteryHealth = (byte) ((stateInt >> STATE_BATTERY_HEALTH_SHIFT) & STATE_BATTERY_STATUS_MASK);
            cur.batteryPlugType = (byte) ((stateInt >> STATE_BATTERY_PLUG_SHIFT) & STATE_BATTERY_PLUG_MASK);
            switch (cur.batteryPlugType) {
                case NET_UPDATE_MOBILE /*1*/:
                    cur.batteryPlugType = (byte) 1;
                    break;
                case NET_UPDATE_WIFI /*2*/:
                    cur.batteryPlugType = (byte) 2;
                    break;
                case STATE_BATTERY_PLUG_MASK /*3*/:
                    cur.batteryPlugType = (byte) 4;
                    break;
            }
            cur.numReadInts += NET_UPDATE_MOBILE;
        } else {
            cur.states = (DELTA_STATE_MASK & firstToken) | (cur.states & AsyncService.CMD_ASYNC_SERVICE_ON_START_INTENT);
        }
        if ((DELTA_STATE2_FLAG & firstToken) != 0) {
            cur.states2 = src.readInt();
        }
        if ((DELTA_WAKELOCK_FLAG & firstToken) != 0) {
            int indexes = src.readInt();
            int wakeLockIndex = indexes & NET_UPDATE_ALL;
            int wakeReasonIndex = (indexes >> 16) & NET_UPDATE_ALL;
            if (wakeLockIndex != NET_UPDATE_ALL) {
                cur.wakelockTag = cur.localWakelockTag;
                readHistoryTag(wakeLockIndex, cur.wakelockTag);
            } else {
                cur.wakelockTag = null;
            }
            if (wakeReasonIndex != NET_UPDATE_ALL) {
                cur.wakeReasonTag = cur.localWakeReasonTag;
                readHistoryTag(wakeReasonIndex, cur.wakeReasonTag);
            } else {
                cur.wakeReasonTag = null;
            }
            cur.numReadInts += NET_UPDATE_MOBILE;
        } else {
            cur.wakelockTag = null;
            cur.wakeReasonTag = null;
        }
        if ((DELTA_EVENT_FLAG & firstToken) != 0) {
            cur.eventTag = cur.localEventTag;
            int codeAndIndex = src.readInt();
            cur.eventCode = NET_UPDATE_ALL & codeAndIndex;
            readHistoryTag((codeAndIndex >> 16) & NET_UPDATE_ALL, cur.eventTag);
            cur.numReadInts += NET_UPDATE_MOBILE;
            return;
        }
        cur.eventCode = BATTERY_PLUGGED_NONE;
    }

    public void commitCurrentHistoryBatchLocked() {
        this.mHistoryLastWritten.cmd = (byte) -1;
    }

    void addHistoryBufferLocked(long elapsedRealtimeMs, long uptimeMs, HistoryItem cur) {
        if (this.mHaveBatteryLevel && this.mRecordingHistory) {
            long timeDiff = (this.mHistoryBaseTime + elapsedRealtimeMs) - this.mHistoryLastWritten.time;
            int diffStates = this.mHistoryLastWritten.states ^ cur.states;
            int diffStates2 = this.mHistoryLastWritten.states2 ^ cur.states2;
            int lastDiffStates = this.mHistoryLastWritten.states ^ this.mHistoryLastLastWritten.states;
            int lastDiffStates2 = this.mHistoryLastWritten.states2 ^ this.mHistoryLastLastWritten.states2;
            if (this.mHistoryBufferLastPos >= 0 && this.mHistoryLastWritten.cmd == null && timeDiff < 1000 && (diffStates & lastDiffStates) == 0 && (diffStates2 & lastDiffStates2) == 0 && ((this.mHistoryLastWritten.wakelockTag == null || cur.wakelockTag == null) && ((this.mHistoryLastWritten.wakeReasonTag == null || cur.wakeReasonTag == null) && ((this.mHistoryLastWritten.eventCode == 0 || cur.eventCode == 0) && this.mHistoryLastWritten.batteryLevel == cur.batteryLevel && this.mHistoryLastWritten.batteryStatus == cur.batteryStatus && this.mHistoryLastWritten.batteryHealth == cur.batteryHealth && this.mHistoryLastWritten.batteryPlugType == cur.batteryPlugType && this.mHistoryLastWritten.batteryTemperature == cur.batteryTemperature && this.mHistoryLastWritten.batteryVoltage == cur.batteryVoltage)))) {
                this.mHistoryBuffer.setDataSize(this.mHistoryBufferLastPos);
                this.mHistoryBuffer.setDataPosition(this.mHistoryBufferLastPos);
                this.mHistoryBufferLastPos = -1;
                elapsedRealtimeMs = this.mHistoryLastWritten.time - this.mHistoryBaseTime;
                if (this.mHistoryLastWritten.wakelockTag != null) {
                    cur.wakelockTag = cur.localWakelockTag;
                    cur.wakelockTag.setTo(this.mHistoryLastWritten.wakelockTag);
                }
                if (this.mHistoryLastWritten.wakeReasonTag != null) {
                    cur.wakeReasonTag = cur.localWakeReasonTag;
                    cur.wakeReasonTag.setTo(this.mHistoryLastWritten.wakeReasonTag);
                }
                if (this.mHistoryLastWritten.eventCode != 0) {
                    cur.eventCode = this.mHistoryLastWritten.eventCode;
                    cur.eventTag = cur.localEventTag;
                    cur.eventTag.setTo(this.mHistoryLastWritten.eventTag);
                }
                this.mHistoryLastWritten.setTo(this.mHistoryLastLastWritten);
            }
            int dataSize = this.mHistoryBuffer.dataSize();
            if (dataSize < MAX_HISTORY_BUFFER) {
                if (dataSize == 0) {
                    cur.currentTime = System.currentTimeMillis();
                    this.mLastRecordedClockTime = cur.currentTime;
                    this.mLastRecordedClockRealtime = elapsedRealtimeMs;
                    addHistoryBufferLocked(elapsedRealtimeMs, uptimeMs, (byte) 7, cur);
                }
                addHistoryBufferLocked(elapsedRealtimeMs, uptimeMs, (byte) 0, cur);
            } else if (!this.mHistoryOverflow) {
                this.mHistoryOverflow = true;
                addHistoryBufferLocked(elapsedRealtimeMs, uptimeMs, (byte) 0, cur);
                addHistoryBufferLocked(elapsedRealtimeMs, uptimeMs, (byte) 6, cur);
            } else if (this.mHistoryLastWritten.batteryLevel != cur.batteryLevel || (dataSize < MAX_MAX_HISTORY_BUFFER && ((this.mHistoryLastWritten.states ^ cur.states) & 1900544) != 0 && ((this.mHistoryLastWritten.states2 ^ cur.states2) & -1879048192) != 0)) {
                addHistoryBufferLocked(elapsedRealtimeMs, uptimeMs, (byte) 0, cur);
            }
        }
    }

    private void addHistoryBufferLocked(long elapsedRealtimeMs, long uptimeMs, byte cmd, HistoryItem cur) {
        if (this.mIteratingHistory) {
            throw new IllegalStateException("Can't do this while iterating history!");
        }
        this.mHistoryBufferLastPos = this.mHistoryBuffer.dataPosition();
        this.mHistoryLastLastWritten.setTo(this.mHistoryLastWritten);
        this.mHistoryLastWritten.setTo(this.mHistoryBaseTime + elapsedRealtimeMs, cmd, cur);
        writeHistoryDelta(this.mHistoryBuffer, this.mHistoryLastWritten, this.mHistoryLastLastWritten);
        this.mLastHistoryElapsedRealtime = elapsedRealtimeMs;
        cur.wakelockTag = null;
        cur.wakeReasonTag = null;
        cur.eventCode = BATTERY_PLUGGED_NONE;
        cur.eventTag = null;
    }

    void addHistoryRecordLocked(long elapsedRealtimeMs, long uptimeMs) {
        HistoryItem historyItem;
        if (this.mTrackRunningHistoryElapsedRealtime != 0) {
            long diffElapsed = elapsedRealtimeMs - this.mTrackRunningHistoryElapsedRealtime;
            long diffUptime = uptimeMs - this.mTrackRunningHistoryUptime;
            if (diffUptime < diffElapsed - 20) {
                long wakeElapsedTime = elapsedRealtimeMs - (diffElapsed - diffUptime);
                this.mHistoryAddTmp.setTo(this.mHistoryLastWritten);
                this.mHistoryAddTmp.wakelockTag = null;
                this.mHistoryAddTmp.wakeReasonTag = null;
                this.mHistoryAddTmp.eventCode = BATTERY_PLUGGED_NONE;
                historyItem = this.mHistoryAddTmp;
                historyItem.states &= RILConstants.MAX_INT;
                addHistoryRecordInnerLocked(wakeElapsedTime, uptimeMs, this.mHistoryAddTmp);
            }
        }
        historyItem = this.mHistoryCur;
        historyItem.states |= ExploreByTouchHelper.INVALID_ID;
        this.mTrackRunningHistoryElapsedRealtime = elapsedRealtimeMs;
        this.mTrackRunningHistoryUptime = uptimeMs;
        addHistoryRecordInnerLocked(elapsedRealtimeMs, uptimeMs, this.mHistoryCur);
    }

    void addHistoryRecordInnerLocked(long elapsedRealtimeMs, long uptimeMs, HistoryItem cur) {
        addHistoryBufferLocked(elapsedRealtimeMs, uptimeMs, cur);
    }

    void addHistoryEventLocked(long elapsedRealtimeMs, long uptimeMs, int code, String name, int uid) {
        this.mHistoryCur.eventCode = code;
        this.mHistoryCur.eventTag = this.mHistoryCur.localEventTag;
        this.mHistoryCur.eventTag.string = name;
        this.mHistoryCur.eventTag.uid = uid;
        addHistoryRecordLocked(elapsedRealtimeMs, uptimeMs);
    }

    void addHistoryRecordLocked(long elapsedRealtimeMs, long uptimeMs, byte cmd, HistoryItem cur) {
        HistoryItem rec = this.mHistoryCache;
        if (rec != null) {
            this.mHistoryCache = rec.next;
        } else {
            rec = new HistoryItem();
        }
        rec.setTo(this.mHistoryBaseTime + elapsedRealtimeMs, cmd, cur);
        addHistoryRecordLocked(rec);
    }

    void addHistoryRecordLocked(HistoryItem rec) {
        this.mNumHistoryItems += NET_UPDATE_MOBILE;
        rec.next = null;
        this.mHistoryLastEnd = this.mHistoryEnd;
        if (this.mHistoryEnd != null) {
            this.mHistoryEnd.next = rec;
            this.mHistoryEnd = rec;
            return;
        }
        this.mHistoryEnd = rec;
        this.mHistory = rec;
    }

    void clearHistoryLocked() {
        this.mHistoryBaseTime = 0;
        this.mLastHistoryElapsedRealtime = 0;
        this.mTrackRunningHistoryElapsedRealtime = 0;
        this.mTrackRunningHistoryUptime = 0;
        this.mHistoryBuffer.setDataSize(BATTERY_PLUGGED_NONE);
        this.mHistoryBuffer.setDataPosition(BATTERY_PLUGGED_NONE);
        this.mHistoryBuffer.setDataCapacity(Protocol.BASE_WIFI);
        this.mHistoryLastLastWritten.clear();
        this.mHistoryLastWritten.clear();
        this.mHistoryTagPool.clear();
        this.mNextHistoryTagIdx = BATTERY_PLUGGED_NONE;
        this.mNumHistoryTagChars = BATTERY_PLUGGED_NONE;
        this.mHistoryBufferLastPos = -1;
        this.mHistoryOverflow = USE_OLD_HISTORY;
        this.mLastRecordedClockTime = 0;
        this.mLastRecordedClockRealtime = 0;
    }

    public void updateTimeBasesLocked(boolean unplugged, boolean screenOff, long uptime, long realtime) {
        boolean unpluggedScreenOff;
        if (this.mOnBatteryTimeBase.setRunning(unplugged, uptime, realtime)) {
            if (unplugged) {
                this.mBluetoothPingStart = getCurrentBluetoothPingCount();
                this.mBluetoothPingCount = BATTERY_PLUGGED_NONE;
            } else {
                this.mBluetoothPingCount = getBluetoothPingCount();
                this.mBluetoothPingStart = -1;
            }
        }
        if (unplugged && screenOff) {
            unpluggedScreenOff = true;
        } else {
            unpluggedScreenOff = USE_OLD_HISTORY;
        }
        if (unpluggedScreenOff != this.mOnBatteryScreenOffTimeBase.isRunning()) {
            updateKernelWakelocksLocked();
            requestWakelockCpuUpdate();
            if (!unpluggedScreenOff) {
                this.mDistributeWakelockCpu = true;
            }
            this.mOnBatteryScreenOffTimeBase.setRunning(unpluggedScreenOff, uptime, realtime);
        }
    }

    public void addIsolatedUidLocked(int isolatedUid, int appUid) {
        this.mIsolatedUids.put(isolatedUid, appUid);
    }

    public void removeIsolatedUidLocked(int isolatedUid, int appUid) {
        if (this.mIsolatedUids.get(isolatedUid, -1) == appUid) {
            this.mIsolatedUids.delete(isolatedUid);
        }
    }

    public int mapUid(int uid) {
        int isolated = this.mIsolatedUids.get(uid, -1);
        return isolated > 0 ? isolated : uid;
    }

    public void noteEventLocked(int code, String name, int uid) {
        uid = mapUid(uid);
        if (this.mActiveEvents.updateState(code, name, uid, BATTERY_PLUGGED_NONE)) {
            addHistoryEventLocked(SystemClock.elapsedRealtime(), SystemClock.uptimeMillis(), code, name, uid);
        }
    }

    public void noteCurrentTimeChangedLocked() {
        long currentTime = System.currentTimeMillis();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptime = SystemClock.uptimeMillis();
        if (isStartClockTimeValid() && this.mLastRecordedClockTime != 0) {
            long expectedClockTime = this.mLastRecordedClockTime + (elapsedRealtime - this.mLastRecordedClockRealtime);
            if (currentTime >= expectedClockTime - 500 && currentTime <= expectedClockTime + 500) {
                return;
            }
        }
        recordCurrentTimeChangeLocked(currentTime, elapsedRealtime, uptime);
        if (isStartClockTimeValid()) {
            this.mStartClockTime = currentTime;
        }
    }

    public void noteProcessStartLocked(String name, int uid) {
        uid = mapUid(uid);
        if (isOnBattery()) {
            getUidStatsLocked(uid).getProcessStatsLocked(name).incStartsLocked();
        }
        if (this.mActiveEvents.updateState(AudioMixingRule.RULE_EXCLUDE_ATTRIBUTE_USAGE, name, uid, BATTERY_PLUGGED_NONE) && this.mRecordAllHistory) {
            addHistoryEventLocked(SystemClock.elapsedRealtime(), SystemClock.uptimeMillis(), AudioMixingRule.RULE_EXCLUDE_ATTRIBUTE_USAGE, name, uid);
        }
    }

    public void noteProcessCrashLocked(String name, int uid) {
        uid = mapUid(uid);
        if (isOnBattery()) {
            getUidStatsLocked(uid).getProcessStatsLocked(name).incNumCrashesLocked();
        }
    }

    public void noteProcessAnrLocked(String name, int uid) {
        uid = mapUid(uid);
        if (isOnBattery()) {
            getUidStatsLocked(uid).getProcessStatsLocked(name).incNumAnrsLocked();
        }
    }

    public void noteProcessStateLocked(String name, int uid, int state) {
        uid = mapUid(uid);
        getUidStatsLocked(uid).updateProcessStateLocked(name, state, SystemClock.elapsedRealtime());
    }

    public void noteProcessFinishLocked(String name, int uid) {
        uid = mapUid(uid);
        if (this.mActiveEvents.updateState(GL10.GL_LIGHT1, name, uid, BATTERY_PLUGGED_NONE)) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long uptime = SystemClock.uptimeMillis();
            getUidStatsLocked(uid).updateProcessStateLocked(name, STATE_BATTERY_PLUG_MASK, elapsedRealtime);
            if (this.mRecordAllHistory) {
                addHistoryEventLocked(elapsedRealtime, uptime, GL10.GL_LIGHT1, name, uid);
            }
        }
    }

    public void noteSyncStartLocked(String name, int uid) {
        uid = mapUid(uid);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptime = SystemClock.uptimeMillis();
        getUidStatsLocked(uid).noteStartSyncLocked(name, elapsedRealtime);
        if (this.mActiveEvents.updateState(32772, name, uid, BATTERY_PLUGGED_NONE)) {
            addHistoryEventLocked(elapsedRealtime, uptime, 32772, name, uid);
        }
    }

    public void noteSyncFinishLocked(String name, int uid) {
        uid = mapUid(uid);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptime = SystemClock.uptimeMillis();
        getUidStatsLocked(uid).noteStopSyncLocked(name, elapsedRealtime);
        if (this.mActiveEvents.updateState(GL10.GL_LIGHT4, name, uid, BATTERY_PLUGGED_NONE)) {
            addHistoryEventLocked(elapsedRealtime, uptime, GL10.GL_LIGHT4, name, uid);
        }
    }

    public void noteJobStartLocked(String name, int uid) {
        uid = mapUid(uid);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptime = SystemClock.uptimeMillis();
        getUidStatsLocked(uid).noteStartJobLocked(name, elapsedRealtime);
        if (this.mActiveEvents.updateState(GL11ExtensionPack.GL_FUNC_ADD, name, uid, BATTERY_PLUGGED_NONE)) {
            addHistoryEventLocked(elapsedRealtime, uptime, GL11ExtensionPack.GL_FUNC_ADD, name, uid);
        }
    }

    public void noteJobFinishLocked(String name, int uid) {
        uid = mapUid(uid);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptime = SystemClock.uptimeMillis();
        getUidStatsLocked(uid).noteStopJobLocked(name, elapsedRealtime);
        if (this.mActiveEvents.updateState(GL10.GL_LIGHT6, name, uid, BATTERY_PLUGGED_NONE)) {
            addHistoryEventLocked(elapsedRealtime, uptime, GL10.GL_LIGHT6, name, uid);
        }
    }

    private void requestWakelockCpuUpdate() {
        if (!this.mHandler.hasMessages(NET_UPDATE_MOBILE)) {
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(NET_UPDATE_MOBILE), DELAY_UPDATE_WAKELOCKS);
        }
    }

    public void setRecordAllHistoryLocked(boolean enabled) {
        this.mRecordAllHistory = enabled;
        HashMap<String, SparseIntArray> active;
        long mSecRealtime;
        long mSecUptime;
        SparseIntArray uids;
        int j;
        if (enabled) {
            active = this.mActiveEvents.getStateForEvent(NET_UPDATE_MOBILE);
            if (active != null) {
                mSecRealtime = SystemClock.elapsedRealtime();
                mSecUptime = SystemClock.uptimeMillis();
                for (Map.Entry<String, SparseIntArray> ent : active.entrySet()) {
                    uids = (SparseIntArray) ent.getValue();
                    for (j = BATTERY_PLUGGED_NONE; j < uids.size(); j += NET_UPDATE_MOBILE) {
                        addHistoryEventLocked(mSecRealtime, mSecUptime, AudioMixingRule.RULE_EXCLUDE_ATTRIBUTE_USAGE, (String) ent.getKey(), uids.keyAt(j));
                    }
                }
                return;
            }
            return;
        }
        this.mActiveEvents.removeEvents(5);
        active = this.mActiveEvents.getStateForEvent(NET_UPDATE_MOBILE);
        if (active != null) {
            mSecRealtime = SystemClock.elapsedRealtime();
            mSecUptime = SystemClock.uptimeMillis();
            for (Map.Entry<String, SparseIntArray> ent2 : active.entrySet()) {
                uids = (SparseIntArray) ent2.getValue();
                for (j = BATTERY_PLUGGED_NONE; j < uids.size(); j += NET_UPDATE_MOBILE) {
                    addHistoryEventLocked(mSecRealtime, mSecUptime, GL10.GL_LIGHT1, (String) ent2.getKey(), uids.keyAt(j));
                }
            }
        }
    }

    public void setNoAutoReset(boolean enabled) {
        this.mNoAutoReset = enabled;
    }

    public void noteStartWakeLocked(int uid, int pid, String name, String historyName, int type, boolean unimportantForLogging, long elapsedRealtime, long uptime) {
        uid = mapUid(uid);
        if (type == 0) {
            aggregateLastWakeupUptimeLocked(uptime);
            if (historyName == null) {
                historyName = name;
            }
            if (this.mRecordAllHistory && this.mActiveEvents.updateState(32773, historyName, uid, BATTERY_PLUGGED_NONE)) {
                addHistoryEventLocked(elapsedRealtime, uptime, 32773, historyName, uid);
            }
            HistoryTag historyTag;
            if (this.mWakeLockNesting == 0) {
                HistoryItem historyItem = this.mHistoryCur;
                historyItem.states |= AudioSystem.DEVICE_OUT_DEFAULT;
                this.mHistoryCur.wakelockTag = this.mHistoryCur.localWakelockTag;
                historyTag = this.mHistoryCur.wakelockTag;
                this.mInitialAcquireWakeName = historyName;
                historyTag.string = historyName;
                historyTag = this.mHistoryCur.wakelockTag;
                this.mInitialAcquireWakeUid = uid;
                historyTag.uid = uid;
                this.mWakeLockImportant = !unimportantForLogging ? true : USE_OLD_HISTORY;
                addHistoryRecordLocked(elapsedRealtime, uptime);
            } else if (!(this.mWakeLockImportant || unimportantForLogging || this.mHistoryLastWritten.cmd != null)) {
                if (this.mHistoryLastWritten.wakelockTag != null) {
                    this.mHistoryLastWritten.wakelockTag = null;
                    this.mHistoryCur.wakelockTag = this.mHistoryCur.localWakelockTag;
                    historyTag = this.mHistoryCur.wakelockTag;
                    this.mInitialAcquireWakeName = historyName;
                    historyTag.string = historyName;
                    historyTag = this.mHistoryCur.wakelockTag;
                    this.mInitialAcquireWakeUid = uid;
                    historyTag.uid = uid;
                    addHistoryRecordLocked(elapsedRealtime, uptime);
                }
                this.mWakeLockImportant = true;
            }
            this.mWakeLockNesting += NET_UPDATE_MOBILE;
        }
        if (uid >= 0) {
            requestWakelockCpuUpdate();
            getUidStatsLocked(uid).noteStartWakeLocked(pid, name, type, elapsedRealtime);
        }
    }

    public void noteStopWakeLocked(int uid, int pid, String name, String historyName, int type, long elapsedRealtime, long uptime) {
        uid = mapUid(uid);
        if (type == 0) {
            this.mWakeLockNesting--;
            if (this.mRecordAllHistory) {
                if (historyName == null) {
                    historyName = name;
                }
                if (this.mActiveEvents.updateState(GL10.GL_LIGHT5, historyName, uid, BATTERY_PLUGGED_NONE)) {
                    addHistoryEventLocked(elapsedRealtime, uptime, GL10.GL_LIGHT5, historyName, uid);
                }
            }
            if (this.mWakeLockNesting == 0) {
                HistoryItem historyItem = this.mHistoryCur;
                historyItem.states &= -1073741825;
                this.mInitialAcquireWakeName = null;
                this.mInitialAcquireWakeUid = -1;
                addHistoryRecordLocked(elapsedRealtime, uptime);
            }
        }
        if (uid >= 0) {
            requestWakelockCpuUpdate();
            getUidStatsLocked(uid).noteStopWakeLocked(pid, name, type, elapsedRealtime);
        }
    }

    public void noteStartWakeFromSourceLocked(WorkSource ws, int pid, String name, String historyName, int type, boolean unimportantForLogging) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptime = SystemClock.uptimeMillis();
        int N = ws.size();
        for (int i = BATTERY_PLUGGED_NONE; i < N; i += NET_UPDATE_MOBILE) {
            noteStartWakeLocked(ws.get(i), pid, name, historyName, type, unimportantForLogging, elapsedRealtime, uptime);
        }
    }

    public void noteChangeWakelockFromSourceLocked(WorkSource ws, int pid, String name, String historyName, int type, WorkSource newWs, int newPid, String newName, String newHistoryName, int newType, boolean newUnimportantForLogging) {
        int i;
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptime = SystemClock.uptimeMillis();
        int NN = newWs.size();
        for (i = BATTERY_PLUGGED_NONE; i < NN; i += NET_UPDATE_MOBILE) {
            noteStartWakeLocked(newWs.get(i), newPid, newName, newHistoryName, newType, newUnimportantForLogging, elapsedRealtime, uptime);
        }
        int NO = ws.size();
        for (i = BATTERY_PLUGGED_NONE; i < NO; i += NET_UPDATE_MOBILE) {
            noteStopWakeLocked(ws.get(i), pid, name, historyName, type, elapsedRealtime, uptime);
        }
    }

    public void noteStopWakeFromSourceLocked(WorkSource ws, int pid, String name, String historyName, int type) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptime = SystemClock.uptimeMillis();
        int N = ws.size();
        for (int i = BATTERY_PLUGGED_NONE; i < N; i += NET_UPDATE_MOBILE) {
            noteStopWakeLocked(ws.get(i), pid, name, historyName, type, elapsedRealtime, uptime);
        }
    }

    void aggregateLastWakeupUptimeLocked(long uptimeMs) {
        if (this.mLastWakeupReason != null) {
            long deltaUptime = uptimeMs - this.mLastWakeupUptimeMs;
            SamplingTimer timer = getWakeupReasonTimerLocked(this.mLastWakeupReason);
            timer.addCurrentReportedCount(NET_UPDATE_MOBILE);
            timer.addCurrentReportedTotalTime(1000 * deltaUptime);
            this.mLastWakeupReason = null;
        }
    }

    public void noteWakeupReasonLocked(String reason) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptime = SystemClock.uptimeMillis();
        aggregateLastWakeupUptimeLocked(uptime);
        this.mHistoryCur.wakeReasonTag = this.mHistoryCur.localWakeReasonTag;
        this.mHistoryCur.wakeReasonTag.string = reason;
        this.mHistoryCur.wakeReasonTag.uid = BATTERY_PLUGGED_NONE;
        this.mLastWakeupReason = reason;
        this.mLastWakeupUptimeMs = uptime;
        addHistoryRecordLocked(elapsedRealtime, uptime);
    }

    public int startAddingCpuLocked() {
        this.mHandler.removeMessages(NET_UPDATE_MOBILE);
        int N = this.mPartialTimers.size();
        if (N == 0) {
            this.mLastPartialTimers.clear();
            this.mDistributeWakelockCpu = USE_OLD_HISTORY;
            return BATTERY_PLUGGED_NONE;
        } else if (!this.mOnBatteryScreenOffTimeBase.isRunning() && !this.mDistributeWakelockCpu) {
            return BATTERY_PLUGGED_NONE;
        } else {
            this.mDistributeWakelockCpu = USE_OLD_HISTORY;
            for (int i = BATTERY_PLUGGED_NONE; i < N; i += NET_UPDATE_MOBILE) {
                StopwatchTimer st = (StopwatchTimer) this.mPartialTimers.get(i);
                if (st.mInList) {
                    Uid uid = st.mUid;
                    if (!(uid == null || uid.mUid == RILConstants.RIL_UNSOL_RESPONSE_RADIO_STATE_CHANGED)) {
                        return 50;
                    }
                }
            }
            return BATTERY_PLUGGED_NONE;
        }
    }

    public void finishAddingCpuLocked(int perc, int utime, int stime, long[] cpuSpeedTimes) {
        int i;
        StopwatchTimer st;
        int N = this.mPartialTimers.size();
        if (perc != 0) {
            Uid uid;
            Proc proc;
            int num = BATTERY_PLUGGED_NONE;
            for (i = BATTERY_PLUGGED_NONE; i < N; i += NET_UPDATE_MOBILE) {
                st = (StopwatchTimer) this.mPartialTimers.get(i);
                if (st.mInList) {
                    uid = st.mUid;
                    if (!(uid == null || uid.mUid == RILConstants.RIL_UNSOL_RESPONSE_RADIO_STATE_CHANGED)) {
                        num += NET_UPDATE_MOBILE;
                    }
                }
            }
            if (num != 0) {
                for (i = BATTERY_PLUGGED_NONE; i < N; i += NET_UPDATE_MOBILE) {
                    st = (StopwatchTimer) this.mPartialTimers.get(i);
                    if (st.mInList) {
                        uid = st.mUid;
                        if (!(uid == null || uid.mUid == RILConstants.RIL_UNSOL_RESPONSE_RADIO_STATE_CHANGED)) {
                            int myUTime = utime / num;
                            int mySTime = stime / num;
                            utime -= myUTime;
                            stime -= mySTime;
                            num--;
                            proc = uid.getProcessStatsLocked("*wakelock*");
                            proc.addCpuTimeLocked(myUTime, mySTime);
                            proc.addSpeedStepTimes(cpuSpeedTimes);
                        }
                    }
                }
            }
            if (!(utime == 0 && stime == 0)) {
                uid = getUidStatsLocked(RILConstants.RIL_UNSOL_RESPONSE_RADIO_STATE_CHANGED);
                if (uid != null) {
                    proc = uid.getProcessStatsLocked("*lost*");
                    proc.addCpuTimeLocked(utime, stime);
                    proc.addSpeedStepTimes(cpuSpeedTimes);
                }
            }
        }
        int NL = this.mLastPartialTimers.size();
        boolean diff = N != NL ? true : USE_OLD_HISTORY;
        for (i = BATTERY_PLUGGED_NONE; i < NL && !diff; i += NET_UPDATE_MOBILE) {
            diff |= this.mPartialTimers.get(i) != this.mLastPartialTimers.get(i) ? NET_UPDATE_MOBILE : BATTERY_PLUGGED_NONE;
        }
        if (diff) {
            for (i = BATTERY_PLUGGED_NONE; i < NL; i += NET_UPDATE_MOBILE) {
                ((StopwatchTimer) this.mLastPartialTimers.get(i)).mInList = USE_OLD_HISTORY;
            }
            this.mLastPartialTimers.clear();
            for (i = BATTERY_PLUGGED_NONE; i < N; i += NET_UPDATE_MOBILE) {
                st = (StopwatchTimer) this.mPartialTimers.get(i);
                st.mInList = true;
                this.mLastPartialTimers.add(st);
            }
            return;
        }
        for (i = BATTERY_PLUGGED_NONE; i < NL; i += NET_UPDATE_MOBILE) {
            ((StopwatchTimer) this.mPartialTimers.get(i)).mInList = true;
        }
    }

    public void noteProcessDiedLocked(int uid, int pid) {
        Uid u = (Uid) this.mUidStats.get(mapUid(uid));
        if (u != null) {
            u.mPids.remove(pid);
        }
    }

    public long getProcessWakeTime(int uid, int pid, long realtime) {
        long j = 0;
        Uid u = (Uid) this.mUidStats.get(mapUid(uid));
        if (u == null) {
            return 0;
        }
        Pid p = (Pid) u.mPids.get(pid);
        if (p == null) {
            return 0;
        }
        long j2 = p.mWakeSumMs;
        if (p.mWakeNesting > 0) {
            j = realtime - p.mWakeStartMs;
        }
        return j + j2;
    }

    public void reportExcessiveWakeLocked(int uid, String proc, long overTime, long usedTime) {
        Uid u = (Uid) this.mUidStats.get(mapUid(uid));
        if (u != null) {
            u.reportExcessiveWakeLocked(proc, overTime, usedTime);
        }
    }

    public void reportExcessiveCpuLocked(int uid, String proc, long overTime, long usedTime) {
        Uid u = (Uid) this.mUidStats.get(mapUid(uid));
        if (u != null) {
            u.reportExcessiveCpuLocked(proc, overTime, usedTime);
        }
    }

    public void noteStartSensorLocked(int uid, int sensor) {
        uid = mapUid(uid);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptime = SystemClock.uptimeMillis();
        if (this.mSensorNesting == 0) {
            HistoryItem historyItem = this.mHistoryCur;
            historyItem.states |= DELTA_EVENT_FLAG;
            addHistoryRecordLocked(elapsedRealtime, uptime);
        }
        this.mSensorNesting += NET_UPDATE_MOBILE;
        getUidStatsLocked(uid).noteStartSensor(sensor, elapsedRealtime);
    }

    public void noteStopSensorLocked(int uid, int sensor) {
        uid = mapUid(uid);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptime = SystemClock.uptimeMillis();
        this.mSensorNesting--;
        if (this.mSensorNesting == 0) {
            HistoryItem historyItem = this.mHistoryCur;
            historyItem.states &= -8388609;
            addHistoryRecordLocked(elapsedRealtime, uptime);
        }
        getUidStatsLocked(uid).noteStopSensor(sensor, elapsedRealtime);
    }

    public void noteStartGpsLocked(int uid) {
        uid = mapUid(uid);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptime = SystemClock.uptimeMillis();
        if (this.mGpsNesting == 0) {
            HistoryItem historyItem = this.mHistoryCur;
            historyItem.states |= 536870912;
            addHistoryRecordLocked(elapsedRealtime, uptime);
        }
        this.mGpsNesting += NET_UPDATE_MOBILE;
        getUidStatsLocked(uid).noteStartGps(elapsedRealtime);
    }

    public void noteStopGpsLocked(int uid) {
        uid = mapUid(uid);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptime = SystemClock.uptimeMillis();
        this.mGpsNesting--;
        if (this.mGpsNesting == 0) {
            HistoryItem historyItem = this.mHistoryCur;
            historyItem.states &= -536870913;
            addHistoryRecordLocked(elapsedRealtime, uptime);
        }
        getUidStatsLocked(uid).noteStopGps(elapsedRealtime);
    }

    public void noteScreenStateLocked(int state) {
        if (this.mScreenState != state) {
            int oldState = this.mScreenState;
            this.mScreenState = state;
            if (state != 0) {
                int stepState = state - 1;
                if (stepState < 4) {
                    this.mModStepMode |= (this.mCurStepMode & STATE_BATTERY_PLUG_MASK) ^ stepState;
                    this.mCurStepMode = (this.mCurStepMode & -4) | stepState;
                } else {
                    Slog.wtf(TAG, "Unexpected screen state: " + state);
                }
            }
            long elapsedRealtime;
            long uptime;
            HistoryItem historyItem;
            if (state == NET_UPDATE_WIFI) {
                elapsedRealtime = SystemClock.elapsedRealtime();
                uptime = SystemClock.uptimeMillis();
                historyItem = this.mHistoryCur;
                historyItem.states |= DELTA_STATE_FLAG;
                addHistoryRecordLocked(elapsedRealtime, uptime);
                this.mScreenOnTimer.startRunningLocked(elapsedRealtime);
                if (this.mScreenBrightnessBin >= 0) {
                    this.mScreenBrightnessTimer[this.mScreenBrightnessBin].startRunningLocked(elapsedRealtime);
                }
                updateTimeBasesLocked(this.mOnBatteryTimeBase.isRunning(), USE_OLD_HISTORY, SystemClock.uptimeMillis() * 1000, 1000 * elapsedRealtime);
                noteStartWakeLocked(-1, -1, "screen", null, BATTERY_PLUGGED_NONE, USE_OLD_HISTORY, elapsedRealtime, uptime);
                if (this.mOnBatteryInternal) {
                    updateDischargeScreenLevelsLocked(USE_OLD_HISTORY, true);
                }
            } else if (oldState == NET_UPDATE_WIFI) {
                elapsedRealtime = SystemClock.elapsedRealtime();
                uptime = SystemClock.uptimeMillis();
                historyItem = this.mHistoryCur;
                historyItem.states &= -1048577;
                addHistoryRecordLocked(elapsedRealtime, uptime);
                this.mScreenOnTimer.stopRunningLocked(elapsedRealtime);
                if (this.mScreenBrightnessBin >= 0) {
                    this.mScreenBrightnessTimer[this.mScreenBrightnessBin].stopRunningLocked(elapsedRealtime);
                }
                noteStopWakeLocked(-1, -1, "screen", "screen", BATTERY_PLUGGED_NONE, elapsedRealtime, uptime);
                updateTimeBasesLocked(this.mOnBatteryTimeBase.isRunning(), true, SystemClock.uptimeMillis() * 1000, 1000 * elapsedRealtime);
                if (this.mOnBatteryInternal) {
                    updateDischargeScreenLevelsLocked(true, USE_OLD_HISTORY);
                }
            }
        }
    }

    public void noteScreenBrightnessLocked(int brightness) {
        int bin = brightness / 51;
        if (bin < 0) {
            bin = BATTERY_PLUGGED_NONE;
        } else if (bin >= 5) {
            bin = 4;
        }
        if (this.mScreenBrightnessBin != bin) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long uptime = SystemClock.uptimeMillis();
            this.mHistoryCur.states = (this.mHistoryCur.states & -8) | (bin << BATTERY_PLUGGED_NONE);
            addHistoryRecordLocked(elapsedRealtime, uptime);
            if (this.mScreenState == NET_UPDATE_WIFI) {
                if (this.mScreenBrightnessBin >= 0) {
                    this.mScreenBrightnessTimer[this.mScreenBrightnessBin].stopRunningLocked(elapsedRealtime);
                }
                this.mScreenBrightnessTimer[bin].startRunningLocked(elapsedRealtime);
            }
            this.mScreenBrightnessBin = bin;
        }
    }

    public void noteUserActivityLocked(int uid, int event) {
        if (this.mOnBatteryInternal) {
            getUidStatsLocked(mapUid(uid)).noteUserActivityLocked(event);
        }
    }

    public void noteInteractiveLocked(boolean interactive) {
        if (this.mInteractive != interactive) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            this.mInteractive = interactive;
            if (interactive) {
                this.mInteractiveTimer.startRunningLocked(elapsedRealtime);
            } else {
                this.mInteractiveTimer.stopRunningLocked(elapsedRealtime);
            }
        }
    }

    public void noteConnectivityChangedLocked(int type, String extra) {
        addHistoryEventLocked(SystemClock.elapsedRealtime(), SystemClock.uptimeMillis(), 9, extra, type);
        this.mNumConnectivityChange += NET_UPDATE_MOBILE;
    }

    public void noteMobileRadioPowerState(int powerState, long timestampNs) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptime = SystemClock.uptimeMillis();
        if (this.mMobileRadioPowerState != powerState) {
            long realElapsedRealtimeMs;
            boolean active = (powerState == DataConnectionRealTimeInfo.DC_POWER_STATE_MEDIUM || powerState == DataConnectionRealTimeInfo.DC_POWER_STATE_HIGH) ? true : USE_OLD_HISTORY;
            HistoryItem historyItem;
            if (active) {
                realElapsedRealtimeMs = elapsedRealtime;
                this.mMobileRadioActiveStartTime = elapsedRealtime;
                historyItem = this.mHistoryCur;
                historyItem.states |= 33554432;
            } else {
                realElapsedRealtimeMs = timestampNs / TimeUtils.NANOS_PER_MS;
                long lastUpdateTimeMs = this.mMobileRadioActiveStartTime;
                if (realElapsedRealtimeMs < lastUpdateTimeMs) {
                    Slog.wtf(TAG, "Data connection inactive timestamp " + realElapsedRealtimeMs + " is before start time " + lastUpdateTimeMs);
                    realElapsedRealtimeMs = elapsedRealtime;
                } else if (realElapsedRealtimeMs < elapsedRealtime) {
                    this.mMobileRadioActiveAdjustedTime.addCountLocked(elapsedRealtime - realElapsedRealtimeMs);
                }
                historyItem = this.mHistoryCur;
                historyItem.states &= -33554433;
            }
            addHistoryRecordLocked(elapsedRealtime, uptime);
            this.mMobileRadioPowerState = powerState;
            if (active) {
                this.mMobileRadioActiveTimer.startRunningLocked(elapsedRealtime);
                this.mMobileRadioActivePerAppTimer.startRunningLocked(elapsedRealtime);
                return;
            }
            this.mMobileRadioActiveTimer.stopRunningLocked(realElapsedRealtimeMs);
            updateNetworkActivityLocked(NET_UPDATE_MOBILE, realElapsedRealtimeMs);
            this.mMobileRadioActivePerAppTimer.stopRunningLocked(realElapsedRealtimeMs);
        }
    }

    public void noteLowPowerMode(boolean enabled) {
        if (this.mLowPowerModeEnabled != enabled) {
            int stepState = enabled ? 4 : BATTERY_PLUGGED_NONE;
            this.mModStepMode |= (this.mCurStepMode & 4) ^ stepState;
            this.mCurStepMode = (this.mCurStepMode & -5) | stepState;
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long uptime = SystemClock.uptimeMillis();
            this.mLowPowerModeEnabled = enabled;
            HistoryItem historyItem;
            if (enabled) {
                historyItem = this.mHistoryCur;
                historyItem.states2 |= ExploreByTouchHelper.INVALID_ID;
                this.mLowPowerModeEnabledTimer.startRunningLocked(elapsedRealtime);
            } else {
                historyItem = this.mHistoryCur;
                historyItem.states2 &= RILConstants.MAX_INT;
                this.mLowPowerModeEnabledTimer.stopRunningLocked(elapsedRealtime);
            }
            addHistoryRecordLocked(elapsedRealtime, uptime);
        }
    }

    public void notePhoneOnLocked() {
        if (!this.mPhoneOn) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long uptime = SystemClock.uptimeMillis();
            HistoryItem historyItem = this.mHistoryCur;
            historyItem.states |= MAX_HISTORY_BUFFER;
            addHistoryRecordLocked(elapsedRealtime, uptime);
            this.mPhoneOn = true;
            this.mPhoneOnTimer.startRunningLocked(elapsedRealtime);
        }
    }

    public void notePhoneOffLocked() {
        if (this.mPhoneOn) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long uptime = SystemClock.uptimeMillis();
            HistoryItem historyItem = this.mHistoryCur;
            historyItem.states &= -262145;
            addHistoryRecordLocked(elapsedRealtime, uptime);
            this.mPhoneOn = USE_OLD_HISTORY;
            this.mPhoneOnTimer.stopRunningLocked(elapsedRealtime);
        }
    }

    void stopAllPhoneSignalStrengthTimersLocked(int except) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        for (int i = BATTERY_PLUGGED_NONE; i < 5; i += NET_UPDATE_MOBILE) {
            if (i != except) {
                while (this.mPhoneSignalStrengthsTimer[i].isRunningLocked()) {
                    this.mPhoneSignalStrengthsTimer[i].stopRunningLocked(elapsedRealtime);
                }
            }
        }
    }

    private int fixPhoneServiceState(int state, int signalBin) {
        if (this.mPhoneSimStateRaw == NET_UPDATE_MOBILE && state == NET_UPDATE_MOBILE && signalBin > 0) {
            return BATTERY_PLUGGED_NONE;
        }
        return state;
    }

    private void updateAllPhoneStateLocked(int state, int simState, int strengthBin) {
        boolean scanning = USE_OLD_HISTORY;
        boolean newHistory = USE_OLD_HISTORY;
        this.mPhoneServiceStateRaw = state;
        this.mPhoneSimStateRaw = simState;
        this.mPhoneSignalStrengthBinRaw = strengthBin;
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptime = SystemClock.uptimeMillis();
        if (simState == NET_UPDATE_MOBILE && state == NET_UPDATE_MOBILE && strengthBin > 0) {
            state = BATTERY_PLUGGED_NONE;
        }
        if (state == STATE_BATTERY_PLUG_MASK) {
            strengthBin = -1;
        } else if (state != 0 && state == NET_UPDATE_MOBILE) {
            scanning = true;
            strengthBin = BATTERY_PLUGGED_NONE;
            if (!this.mPhoneSignalScanningTimer.isRunningLocked()) {
                HistoryItem historyItem = this.mHistoryCur;
                historyItem.states |= DELTA_STATE2_FLAG;
                newHistory = true;
                this.mPhoneSignalScanningTimer.startRunningLocked(elapsedRealtime);
            }
        }
        if (!scanning && this.mPhoneSignalScanningTimer.isRunningLocked()) {
            historyItem = this.mHistoryCur;
            historyItem.states &= -2097153;
            newHistory = true;
            this.mPhoneSignalScanningTimer.stopRunningLocked(elapsedRealtime);
        }
        if (this.mPhoneServiceState != state) {
            this.mHistoryCur.states = (this.mHistoryCur.states & -449) | (state << 6);
            newHistory = true;
            this.mPhoneServiceState = state;
        }
        if (this.mPhoneSignalStrengthBin != strengthBin) {
            if (this.mPhoneSignalStrengthBin >= 0) {
                this.mPhoneSignalStrengthsTimer[this.mPhoneSignalStrengthBin].stopRunningLocked(elapsedRealtime);
            }
            if (strengthBin >= 0) {
                if (!this.mPhoneSignalStrengthsTimer[strengthBin].isRunningLocked()) {
                    this.mPhoneSignalStrengthsTimer[strengthBin].startRunningLocked(elapsedRealtime);
                }
                this.mHistoryCur.states = (this.mHistoryCur.states & -57) | (strengthBin << STATE_BATTERY_PLUG_MASK);
                newHistory = true;
            } else {
                stopAllPhoneSignalStrengthTimersLocked(-1);
            }
            this.mPhoneSignalStrengthBin = strengthBin;
        }
        if (newHistory) {
            addHistoryRecordLocked(elapsedRealtime, uptime);
        }
    }

    public void notePhoneStateLocked(int state, int simState) {
        updateAllPhoneStateLocked(state, simState, this.mPhoneSignalStrengthBinRaw);
    }

    public void notePhoneSignalStrengthLocked(SignalStrength signalStrength) {
        updateAllPhoneStateLocked(this.mPhoneServiceStateRaw, this.mPhoneSimStateRaw, signalStrength.getLevel());
    }

    public void notePhoneDataConnectionStateLocked(int dataType, boolean hasData) {
        int bin = BATTERY_PLUGGED_NONE;
        if (hasData) {
            switch (dataType) {
                case NET_UPDATE_MOBILE /*1*/:
                    bin = NET_UPDATE_MOBILE;
                    break;
                case NET_UPDATE_WIFI /*2*/:
                    bin = NET_UPDATE_WIFI;
                    break;
                case STATE_BATTERY_PLUG_MASK /*3*/:
                    bin = STATE_BATTERY_PLUG_MASK;
                    break;
                case GL10.GL_TRIANGLES /*4*/:
                    bin = 4;
                    break;
                case GL10.GL_TRIANGLE_STRIP /*5*/:
                    bin = 5;
                    break;
                case GL10.GL_TRIANGLE_FAN /*6*/:
                    bin = 6;
                    break;
                case STATE_BATTERY_STATUS_MASK /*7*/:
                    bin = STATE_BATTERY_STATUS_MASK;
                    break;
                case RILConstants.RIL_REQUEST_ENTER_NETWORK_DEPERSONALIZATION /*8*/:
                    bin = 8;
                    break;
                case RILConstants.RIL_REQUEST_GET_CURRENT_CALLS /*9*/:
                    bin = 9;
                    break;
                case OnTriggerListener.CENTER_HANDLE /*10*/:
                    bin = 10;
                    break;
                case RILConstants.SIM_ABSENT /*11*/:
                    bin = 11;
                    break;
                case DnsServerRepository.NUM_SERVERS /*12*/:
                    bin = 12;
                    break;
                case RILConstants.RIL_REQUEST_HANGUP_WAITING_OR_BACKGROUND /*13*/:
                    bin = 13;
                    break;
                case RILConstants.RIL_REQUEST_HANGUP_FOREGROUND_RESUME_BACKGROUND /*14*/:
                    bin = 14;
                    break;
                case RILConstants.RIL_REQUEST_SWITCH_WAITING_OR_HOLDING_AND_ACTIVE /*15*/:
                    bin = 15;
                    break;
                default:
                    bin = 16;
                    break;
            }
        }
        if (this.mPhoneDataConnectionType != bin) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long uptime = SystemClock.uptimeMillis();
            this.mHistoryCur.states = (this.mHistoryCur.states & -15873) | (bin << 9);
            addHistoryRecordLocked(elapsedRealtime, uptime);
            if (this.mPhoneDataConnectionType >= 0) {
                this.mPhoneDataConnectionsTimer[this.mPhoneDataConnectionType].stopRunningLocked(elapsedRealtime);
            }
            this.mPhoneDataConnectionType = bin;
            this.mPhoneDataConnectionsTimer[bin].startRunningLocked(elapsedRealtime);
        }
    }

    public void noteWifiOnLocked() {
        if (!this.mWifiOn) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long uptime = SystemClock.uptimeMillis();
            HistoryItem historyItem = this.mHistoryCur;
            historyItem.states2 |= 268435456;
            addHistoryRecordLocked(elapsedRealtime, uptime);
            this.mWifiOn = true;
            this.mWifiOnTimer.startRunningLocked(elapsedRealtime);
        }
    }

    public void noteWifiOffLocked() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptime = SystemClock.uptimeMillis();
        if (this.mWifiOn) {
            HistoryItem historyItem = this.mHistoryCur;
            historyItem.states2 &= -268435457;
            addHistoryRecordLocked(elapsedRealtime, uptime);
            this.mWifiOn = USE_OLD_HISTORY;
            this.mWifiOnTimer.stopRunningLocked(elapsedRealtime);
        }
    }

    public void noteAudioOnLocked(int uid) {
        uid = mapUid(uid);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptime = SystemClock.uptimeMillis();
        if (this.mAudioOnNesting == 0) {
            HistoryItem historyItem = this.mHistoryCur;
            historyItem.states |= DELTA_WAKELOCK_FLAG;
            addHistoryRecordLocked(elapsedRealtime, uptime);
            this.mAudioOnTimer.startRunningLocked(elapsedRealtime);
        }
        this.mAudioOnNesting += NET_UPDATE_MOBILE;
        getUidStatsLocked(uid).noteAudioTurnedOnLocked(elapsedRealtime);
    }

    public void noteAudioOffLocked(int uid) {
        if (this.mAudioOnNesting != 0) {
            uid = mapUid(uid);
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long uptime = SystemClock.uptimeMillis();
            int i = this.mAudioOnNesting - 1;
            this.mAudioOnNesting = i;
            if (i == 0) {
                HistoryItem historyItem = this.mHistoryCur;
                historyItem.states &= -4194305;
                addHistoryRecordLocked(elapsedRealtime, uptime);
                this.mAudioOnTimer.stopRunningLocked(elapsedRealtime);
            }
            getUidStatsLocked(uid).noteAudioTurnedOffLocked(elapsedRealtime);
        }
    }

    public void noteVideoOnLocked(int uid) {
        uid = mapUid(uid);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptime = SystemClock.uptimeMillis();
        if (this.mVideoOnNesting == 0) {
            HistoryItem historyItem = this.mHistoryCur;
            historyItem.states2 |= AudioSystem.DEVICE_OUT_DEFAULT;
            addHistoryRecordLocked(elapsedRealtime, uptime);
            this.mVideoOnTimer.startRunningLocked(elapsedRealtime);
        }
        this.mVideoOnNesting += NET_UPDATE_MOBILE;
        getUidStatsLocked(uid).noteVideoTurnedOnLocked(elapsedRealtime);
    }

    public void noteVideoOffLocked(int uid) {
        if (this.mVideoOnNesting != 0) {
            uid = mapUid(uid);
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long uptime = SystemClock.uptimeMillis();
            int i = this.mVideoOnNesting - 1;
            this.mVideoOnNesting = i;
            if (i == 0) {
                HistoryItem historyItem = this.mHistoryCur;
                historyItem.states2 &= -1073741825;
                addHistoryRecordLocked(elapsedRealtime, uptime);
                this.mVideoOnTimer.stopRunningLocked(elapsedRealtime);
            }
            getUidStatsLocked(uid).noteVideoTurnedOffLocked(elapsedRealtime);
        }
    }

    public void noteResetAudioLocked() {
        if (this.mAudioOnNesting > 0) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long uptime = SystemClock.uptimeMillis();
            this.mAudioOnNesting = BATTERY_PLUGGED_NONE;
            HistoryItem historyItem = this.mHistoryCur;
            historyItem.states &= -4194305;
            addHistoryRecordLocked(elapsedRealtime, uptime);
            this.mAudioOnTimer.stopAllRunningLocked(elapsedRealtime);
            for (int i = BATTERY_PLUGGED_NONE; i < this.mUidStats.size(); i += NET_UPDATE_MOBILE) {
                ((Uid) this.mUidStats.valueAt(i)).noteResetAudioLocked(elapsedRealtime);
            }
        }
    }

    public void noteResetVideoLocked() {
        if (this.mVideoOnNesting > 0) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long uptime = SystemClock.uptimeMillis();
            this.mAudioOnNesting = BATTERY_PLUGGED_NONE;
            HistoryItem historyItem = this.mHistoryCur;
            historyItem.states2 &= -1073741825;
            addHistoryRecordLocked(elapsedRealtime, uptime);
            this.mVideoOnTimer.stopAllRunningLocked(elapsedRealtime);
            for (int i = BATTERY_PLUGGED_NONE; i < this.mUidStats.size(); i += NET_UPDATE_MOBILE) {
                ((Uid) this.mUidStats.valueAt(i)).noteResetVideoLocked(elapsedRealtime);
            }
        }
    }

    public void noteActivityResumedLocked(int uid) {
        getUidStatsLocked(mapUid(uid)).noteActivityResumedLocked(SystemClock.elapsedRealtime());
    }

    public void noteActivityPausedLocked(int uid) {
        getUidStatsLocked(mapUid(uid)).noteActivityPausedLocked(SystemClock.elapsedRealtime());
    }

    public void noteVibratorOnLocked(int uid, long durationMillis) {
        getUidStatsLocked(mapUid(uid)).noteVibratorOnLocked(durationMillis);
    }

    public void noteVibratorOffLocked(int uid) {
        getUidStatsLocked(mapUid(uid)).noteVibratorOffLocked();
    }

    public void noteFlashlightOnLocked() {
        if (!this.mFlashlightOn) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long uptime = SystemClock.uptimeMillis();
            HistoryItem historyItem = this.mHistoryCur;
            historyItem.states2 |= 134217728;
            addHistoryRecordLocked(elapsedRealtime, uptime);
            this.mFlashlightOn = true;
            this.mFlashlightOnTimer.startRunningLocked(elapsedRealtime);
        }
    }

    public void noteFlashlightOffLocked() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptime = SystemClock.uptimeMillis();
        if (this.mFlashlightOn) {
            HistoryItem historyItem = this.mHistoryCur;
            historyItem.states2 &= -134217729;
            addHistoryRecordLocked(elapsedRealtime, uptime);
            this.mFlashlightOn = USE_OLD_HISTORY;
            this.mFlashlightOnTimer.stopRunningLocked(elapsedRealtime);
        }
    }

    public void noteWifiRunningLocked(WorkSource ws) {
        if (this.mGlobalWifiRunning) {
            Log.w(TAG, "noteWifiRunningLocked -- called while WIFI running");
            return;
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptime = SystemClock.uptimeMillis();
        HistoryItem historyItem = this.mHistoryCur;
        historyItem.states2 |= 536870912;
        addHistoryRecordLocked(elapsedRealtime, uptime);
        this.mGlobalWifiRunning = true;
        this.mGlobalWifiRunningTimer.startRunningLocked(elapsedRealtime);
        int N = ws.size();
        for (int i = BATTERY_PLUGGED_NONE; i < N; i += NET_UPDATE_MOBILE) {
            getUidStatsLocked(mapUid(ws.get(i))).noteWifiRunningLocked(elapsedRealtime);
        }
    }

    public void noteWifiRunningChangedLocked(WorkSource oldWs, WorkSource newWs) {
        if (this.mGlobalWifiRunning) {
            int i;
            long elapsedRealtime = SystemClock.elapsedRealtime();
            int N = oldWs.size();
            for (i = BATTERY_PLUGGED_NONE; i < N; i += NET_UPDATE_MOBILE) {
                getUidStatsLocked(mapUid(oldWs.get(i))).noteWifiStoppedLocked(elapsedRealtime);
            }
            N = newWs.size();
            for (i = BATTERY_PLUGGED_NONE; i < N; i += NET_UPDATE_MOBILE) {
                getUidStatsLocked(mapUid(newWs.get(i))).noteWifiRunningLocked(elapsedRealtime);
            }
            return;
        }
        Log.w(TAG, "noteWifiRunningChangedLocked -- called while WIFI not running");
    }

    public void noteWifiStoppedLocked(WorkSource ws) {
        if (this.mGlobalWifiRunning) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long uptime = SystemClock.uptimeMillis();
            HistoryItem historyItem = this.mHistoryCur;
            historyItem.states2 &= -536870913;
            addHistoryRecordLocked(elapsedRealtime, uptime);
            this.mGlobalWifiRunning = USE_OLD_HISTORY;
            this.mGlobalWifiRunningTimer.stopRunningLocked(elapsedRealtime);
            int N = ws.size();
            for (int i = BATTERY_PLUGGED_NONE; i < N; i += NET_UPDATE_MOBILE) {
                getUidStatsLocked(mapUid(ws.get(i))).noteWifiStoppedLocked(elapsedRealtime);
            }
            return;
        }
        Log.w(TAG, "noteWifiStoppedLocked -- called while WIFI not running");
    }

    public void noteWifiStateLocked(int wifiState, String accessPoint) {
        if (this.mWifiState != wifiState) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            if (this.mWifiState >= 0) {
                this.mWifiStateTimer[this.mWifiState].stopRunningLocked(elapsedRealtime);
            }
            this.mWifiState = wifiState;
            this.mWifiStateTimer[wifiState].startRunningLocked(elapsedRealtime);
        }
    }

    public void noteWifiSupplicantStateChangedLocked(int supplState, boolean failedAuth) {
        if (this.mWifiSupplState != supplState) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long uptime = SystemClock.uptimeMillis();
            if (this.mWifiSupplState >= 0) {
                this.mWifiSupplStateTimer[this.mWifiSupplState].stopRunningLocked(elapsedRealtime);
            }
            this.mWifiSupplState = supplState;
            this.mWifiSupplStateTimer[supplState].startRunningLocked(elapsedRealtime);
            this.mHistoryCur.states2 = (this.mHistoryCur.states2 & -16) | (supplState << BATTERY_PLUGGED_NONE);
            addHistoryRecordLocked(elapsedRealtime, uptime);
        }
    }

    void stopAllWifiSignalStrengthTimersLocked(int except) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        for (int i = BATTERY_PLUGGED_NONE; i < 5; i += NET_UPDATE_MOBILE) {
            if (i != except) {
                while (this.mWifiSignalStrengthsTimer[i].isRunningLocked()) {
                    this.mWifiSignalStrengthsTimer[i].stopRunningLocked(elapsedRealtime);
                }
            }
        }
    }

    public void noteWifiRssiChangedLocked(int newRssi) {
        int strengthBin = WifiManager.calculateSignalLevel(newRssi, 5);
        if (this.mWifiSignalStrengthBin != strengthBin) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long uptime = SystemClock.uptimeMillis();
            if (this.mWifiSignalStrengthBin >= 0) {
                this.mWifiSignalStrengthsTimer[this.mWifiSignalStrengthBin].stopRunningLocked(elapsedRealtime);
            }
            if (strengthBin >= 0) {
                if (!this.mWifiSignalStrengthsTimer[strengthBin].isRunningLocked()) {
                    this.mWifiSignalStrengthsTimer[strengthBin].startRunningLocked(elapsedRealtime);
                }
                this.mHistoryCur.states2 = (this.mHistoryCur.states2 & -113) | (strengthBin << 4);
                addHistoryRecordLocked(elapsedRealtime, uptime);
            } else {
                stopAllWifiSignalStrengthTimersLocked(-1);
            }
            this.mWifiSignalStrengthBin = strengthBin;
        }
    }

    public void noteBluetoothOnLocked() {
        if (!this.mBluetoothOn) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long uptime = SystemClock.uptimeMillis();
            HistoryItem historyItem = this.mHistoryCur;
            historyItem.states |= Protocol.BASE_SYSTEM_RESERVED;
            addHistoryRecordLocked(elapsedRealtime, uptime);
            this.mBluetoothOn = true;
            this.mBluetoothOnTimer.startRunningLocked(elapsedRealtime);
        }
    }

    public void noteBluetoothOffLocked() {
        if (this.mBluetoothOn) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long uptime = SystemClock.uptimeMillis();
            HistoryItem historyItem = this.mHistoryCur;
            historyItem.states &= -65537;
            addHistoryRecordLocked(elapsedRealtime, uptime);
            this.mBluetoothOn = USE_OLD_HISTORY;
            this.mBluetoothOnTimer.stopRunningLocked(elapsedRealtime);
        }
    }

    public void noteBluetoothStateLocked(int bluetoothState) {
        if (this.mBluetoothState != bluetoothState) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            if (this.mBluetoothState >= 0) {
                this.mBluetoothStateTimer[this.mBluetoothState].stopRunningLocked(elapsedRealtime);
            }
            this.mBluetoothState = bluetoothState;
            this.mBluetoothStateTimer[bluetoothState].startRunningLocked(elapsedRealtime);
        }
    }

    public void noteFullWifiLockAcquiredLocked(int uid) {
        uid = mapUid(uid);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptime = SystemClock.uptimeMillis();
        if (this.mWifiFullLockNesting == 0) {
            HistoryItem historyItem = this.mHistoryCur;
            historyItem.states |= 268435456;
            addHistoryRecordLocked(elapsedRealtime, uptime);
        }
        this.mWifiFullLockNesting += NET_UPDATE_MOBILE;
        getUidStatsLocked(uid).noteFullWifiLockAcquiredLocked(elapsedRealtime);
    }

    public void noteFullWifiLockReleasedLocked(int uid) {
        uid = mapUid(uid);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptime = SystemClock.uptimeMillis();
        this.mWifiFullLockNesting--;
        if (this.mWifiFullLockNesting == 0) {
            HistoryItem historyItem = this.mHistoryCur;
            historyItem.states &= -268435457;
            addHistoryRecordLocked(elapsedRealtime, uptime);
        }
        getUidStatsLocked(uid).noteFullWifiLockReleasedLocked(elapsedRealtime);
    }

    public void noteWifiScanStartedLocked(int uid) {
        uid = mapUid(uid);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptime = SystemClock.uptimeMillis();
        if (this.mWifiScanNesting == 0) {
            HistoryItem historyItem = this.mHistoryCur;
            historyItem.states |= 134217728;
            addHistoryRecordLocked(elapsedRealtime, uptime);
        }
        this.mWifiScanNesting += NET_UPDATE_MOBILE;
        getUidStatsLocked(uid).noteWifiScanStartedLocked(elapsedRealtime);
    }

    public void noteWifiScanStoppedLocked(int uid) {
        uid = mapUid(uid);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptime = SystemClock.uptimeMillis();
        this.mWifiScanNesting--;
        if (this.mWifiScanNesting == 0) {
            HistoryItem historyItem = this.mHistoryCur;
            historyItem.states &= -134217729;
            addHistoryRecordLocked(elapsedRealtime, uptime);
        }
        getUidStatsLocked(uid).noteWifiScanStoppedLocked(elapsedRealtime);
    }

    public void noteWifiBatchedScanStartedLocked(int uid, int csph) {
        uid = mapUid(uid);
        getUidStatsLocked(uid).noteWifiBatchedScanStartedLocked(csph, SystemClock.elapsedRealtime());
    }

    public void noteWifiBatchedScanStoppedLocked(int uid) {
        uid = mapUid(uid);
        getUidStatsLocked(uid).noteWifiBatchedScanStoppedLocked(SystemClock.elapsedRealtime());
    }

    public void noteWifiMulticastEnabledLocked(int uid) {
        uid = mapUid(uid);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptime = SystemClock.uptimeMillis();
        if (this.mWifiMulticastNesting == 0) {
            HistoryItem historyItem = this.mHistoryCur;
            historyItem.states |= 67108864;
            addHistoryRecordLocked(elapsedRealtime, uptime);
        }
        this.mWifiMulticastNesting += NET_UPDATE_MOBILE;
        getUidStatsLocked(uid).noteWifiMulticastEnabledLocked(elapsedRealtime);
    }

    public void noteWifiMulticastDisabledLocked(int uid) {
        uid = mapUid(uid);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long uptime = SystemClock.uptimeMillis();
        this.mWifiMulticastNesting--;
        if (this.mWifiMulticastNesting == 0) {
            HistoryItem historyItem = this.mHistoryCur;
            historyItem.states &= -67108865;
            addHistoryRecordLocked(elapsedRealtime, uptime);
        }
        getUidStatsLocked(uid).noteWifiMulticastDisabledLocked(elapsedRealtime);
    }

    public void noteFullWifiLockAcquiredFromSourceLocked(WorkSource ws) {
        int N = ws.size();
        for (int i = BATTERY_PLUGGED_NONE; i < N; i += NET_UPDATE_MOBILE) {
            noteFullWifiLockAcquiredLocked(ws.get(i));
        }
    }

    public void noteFullWifiLockReleasedFromSourceLocked(WorkSource ws) {
        int N = ws.size();
        for (int i = BATTERY_PLUGGED_NONE; i < N; i += NET_UPDATE_MOBILE) {
            noteFullWifiLockReleasedLocked(ws.get(i));
        }
    }

    public void noteWifiScanStartedFromSourceLocked(WorkSource ws) {
        int N = ws.size();
        for (int i = BATTERY_PLUGGED_NONE; i < N; i += NET_UPDATE_MOBILE) {
            noteWifiScanStartedLocked(ws.get(i));
        }
    }

    public void noteWifiScanStoppedFromSourceLocked(WorkSource ws) {
        int N = ws.size();
        for (int i = BATTERY_PLUGGED_NONE; i < N; i += NET_UPDATE_MOBILE) {
            noteWifiScanStoppedLocked(ws.get(i));
        }
    }

    public void noteWifiBatchedScanStartedFromSourceLocked(WorkSource ws, int csph) {
        int N = ws.size();
        for (int i = BATTERY_PLUGGED_NONE; i < N; i += NET_UPDATE_MOBILE) {
            noteWifiBatchedScanStartedLocked(ws.get(i), csph);
        }
    }

    public void noteWifiBatchedScanStoppedFromSourceLocked(WorkSource ws) {
        int N = ws.size();
        for (int i = BATTERY_PLUGGED_NONE; i < N; i += NET_UPDATE_MOBILE) {
            noteWifiBatchedScanStoppedLocked(ws.get(i));
        }
    }

    public void noteWifiMulticastEnabledFromSourceLocked(WorkSource ws) {
        int N = ws.size();
        for (int i = BATTERY_PLUGGED_NONE; i < N; i += NET_UPDATE_MOBILE) {
            noteWifiMulticastEnabledLocked(ws.get(i));
        }
    }

    public void noteWifiMulticastDisabledFromSourceLocked(WorkSource ws) {
        int N = ws.size();
        for (int i = BATTERY_PLUGGED_NONE; i < N; i += NET_UPDATE_MOBILE) {
            noteWifiMulticastDisabledLocked(ws.get(i));
        }
    }

    private static String[] includeInStringArray(String[] array, String str) {
        if (ArrayUtils.indexOf(array, str) >= 0) {
            return array;
        }
        String[] newArray = new String[(array.length + NET_UPDATE_MOBILE)];
        System.arraycopy(array, BATTERY_PLUGGED_NONE, newArray, BATTERY_PLUGGED_NONE, array.length);
        newArray[array.length] = str;
        return newArray;
    }

    private static String[] excludeFromStringArray(String[] array, String str) {
        int index = ArrayUtils.indexOf(array, str);
        if (index < 0) {
            return array;
        }
        String[] newArray = new String[(array.length - 1)];
        if (index > 0) {
            System.arraycopy(array, BATTERY_PLUGGED_NONE, newArray, BATTERY_PLUGGED_NONE, index);
        }
        if (index >= array.length - 1) {
            return newArray;
        }
        System.arraycopy(array, index + NET_UPDATE_MOBILE, newArray, index, (array.length - index) - 1);
        return newArray;
    }

    public void noteNetworkInterfaceTypeLocked(String iface, int networkType) {
        if (!TextUtils.isEmpty(iface)) {
            if (ConnectivityManager.isNetworkTypeMobile(networkType)) {
                this.mMobileIfaces = includeInStringArray(this.mMobileIfaces, iface);
            } else {
                this.mMobileIfaces = excludeFromStringArray(this.mMobileIfaces, iface);
            }
            if (ConnectivityManager.isNetworkTypeWifi(networkType)) {
                this.mWifiIfaces = includeInStringArray(this.mWifiIfaces, iface);
            } else {
                this.mWifiIfaces = excludeFromStringArray(this.mWifiIfaces, iface);
            }
        }
    }

    public void noteNetworkStatsEnabledLocked() {
        updateNetworkActivityLocked(NET_UPDATE_ALL, SystemClock.elapsedRealtime());
    }

    public long getScreenOnTime(long elapsedRealtimeUs, int which) {
        return this.mScreenOnTimer.getTotalTimeLocked(elapsedRealtimeUs, which);
    }

    public int getScreenOnCount(int which) {
        return this.mScreenOnTimer.getCountLocked(which);
    }

    public long getScreenBrightnessTime(int brightnessBin, long elapsedRealtimeUs, int which) {
        return this.mScreenBrightnessTimer[brightnessBin].getTotalTimeLocked(elapsedRealtimeUs, which);
    }

    public long getInteractiveTime(long elapsedRealtimeUs, int which) {
        return this.mInteractiveTimer.getTotalTimeLocked(elapsedRealtimeUs, which);
    }

    public long getLowPowerModeEnabledTime(long elapsedRealtimeUs, int which) {
        return this.mLowPowerModeEnabledTimer.getTotalTimeLocked(elapsedRealtimeUs, which);
    }

    public int getLowPowerModeEnabledCount(int which) {
        return this.mLowPowerModeEnabledTimer.getCountLocked(which);
    }

    public int getNumConnectivityChange(int which) {
        int val = this.mNumConnectivityChange;
        if (which == NET_UPDATE_MOBILE) {
            return val - this.mLoadedNumConnectivityChange;
        }
        if (which == NET_UPDATE_WIFI) {
            return val - this.mUnpluggedNumConnectivityChange;
        }
        return val;
    }

    public long getPhoneOnTime(long elapsedRealtimeUs, int which) {
        return this.mPhoneOnTimer.getTotalTimeLocked(elapsedRealtimeUs, which);
    }

    public int getPhoneOnCount(int which) {
        return this.mPhoneOnTimer.getCountLocked(which);
    }

    public long getPhoneSignalStrengthTime(int strengthBin, long elapsedRealtimeUs, int which) {
        return this.mPhoneSignalStrengthsTimer[strengthBin].getTotalTimeLocked(elapsedRealtimeUs, which);
    }

    public long getPhoneSignalScanningTime(long elapsedRealtimeUs, int which) {
        return this.mPhoneSignalScanningTimer.getTotalTimeLocked(elapsedRealtimeUs, which);
    }

    public int getPhoneSignalStrengthCount(int strengthBin, int which) {
        return this.mPhoneSignalStrengthsTimer[strengthBin].getCountLocked(which);
    }

    public long getPhoneDataConnectionTime(int dataType, long elapsedRealtimeUs, int which) {
        return this.mPhoneDataConnectionsTimer[dataType].getTotalTimeLocked(elapsedRealtimeUs, which);
    }

    public int getPhoneDataConnectionCount(int dataType, int which) {
        return this.mPhoneDataConnectionsTimer[dataType].getCountLocked(which);
    }

    public long getMobileRadioActiveTime(long elapsedRealtimeUs, int which) {
        return this.mMobileRadioActiveTimer.getTotalTimeLocked(elapsedRealtimeUs, which);
    }

    public int getMobileRadioActiveCount(int which) {
        return this.mMobileRadioActiveTimer.getCountLocked(which);
    }

    public long getMobileRadioActiveAdjustedTime(int which) {
        return this.mMobileRadioActiveAdjustedTime.getCountLocked(which);
    }

    public long getMobileRadioActiveUnknownTime(int which) {
        return this.mMobileRadioActiveUnknownTime.getCountLocked(which);
    }

    public int getMobileRadioActiveUnknownCount(int which) {
        return (int) this.mMobileRadioActiveUnknownCount.getCountLocked(which);
    }

    public long getWifiOnTime(long elapsedRealtimeUs, int which) {
        return this.mWifiOnTimer.getTotalTimeLocked(elapsedRealtimeUs, which);
    }

    public long getGlobalWifiRunningTime(long elapsedRealtimeUs, int which) {
        return this.mGlobalWifiRunningTimer.getTotalTimeLocked(elapsedRealtimeUs, which);
    }

    public long getWifiStateTime(int wifiState, long elapsedRealtimeUs, int which) {
        return this.mWifiStateTimer[wifiState].getTotalTimeLocked(elapsedRealtimeUs, which);
    }

    public int getWifiStateCount(int wifiState, int which) {
        return this.mWifiStateTimer[wifiState].getCountLocked(which);
    }

    public long getWifiSupplStateTime(int state, long elapsedRealtimeUs, int which) {
        return this.mWifiSupplStateTimer[state].getTotalTimeLocked(elapsedRealtimeUs, which);
    }

    public int getWifiSupplStateCount(int state, int which) {
        return this.mWifiSupplStateTimer[state].getCountLocked(which);
    }

    public long getWifiSignalStrengthTime(int strengthBin, long elapsedRealtimeUs, int which) {
        return this.mWifiSignalStrengthsTimer[strengthBin].getTotalTimeLocked(elapsedRealtimeUs, which);
    }

    public int getWifiSignalStrengthCount(int strengthBin, int which) {
        return this.mWifiSignalStrengthsTimer[strengthBin].getCountLocked(which);
    }

    public long getBluetoothOnTime(long elapsedRealtimeUs, int which) {
        return this.mBluetoothOnTimer.getTotalTimeLocked(elapsedRealtimeUs, which);
    }

    public long getBluetoothStateTime(int bluetoothState, long elapsedRealtimeUs, int which) {
        return this.mBluetoothStateTimer[bluetoothState].getTotalTimeLocked(elapsedRealtimeUs, which);
    }

    public int getBluetoothStateCount(int bluetoothState, int which) {
        return this.mBluetoothStateTimer[bluetoothState].getCountLocked(which);
    }

    public long getFlashlightOnTime(long elapsedRealtimeUs, int which) {
        return this.mFlashlightOnTimer.getTotalTimeLocked(elapsedRealtimeUs, which);
    }

    public long getFlashlightOnCount(int which) {
        return (long) this.mFlashlightOnTimer.getCountLocked(which);
    }

    public long getNetworkActivityBytes(int type, int which) {
        if (type < 0 || type >= this.mNetworkByteActivityCounters.length) {
            return 0;
        }
        return this.mNetworkByteActivityCounters[type].getCountLocked(which);
    }

    public long getNetworkActivityPackets(int type, int which) {
        if (type < 0 || type >= this.mNetworkPacketActivityCounters.length) {
            return 0;
        }
        return this.mNetworkPacketActivityCounters[type].getCountLocked(which);
    }

    boolean isStartClockTimeValid() {
        return this.mStartClockTime > 31536000000L ? true : USE_OLD_HISTORY;
    }

    public long getStartClockTime() {
        if (!isStartClockTimeValid()) {
            this.mStartClockTime = System.currentTimeMillis();
            if (isStartClockTimeValid()) {
                recordCurrentTimeChangeLocked(this.mStartClockTime, SystemClock.elapsedRealtime(), SystemClock.uptimeMillis());
            }
        }
        return this.mStartClockTime;
    }

    public String getStartPlatformVersion() {
        return this.mStartPlatformVersion;
    }

    public String getEndPlatformVersion() {
        return this.mEndPlatformVersion;
    }

    public int getParcelVersion() {
        return VERSION;
    }

    public boolean getIsOnBattery() {
        return this.mOnBattery;
    }

    public SparseArray<? extends android.os.BatteryStats.Uid> getUidStats() {
        return this.mUidStats;
    }

    public BatteryStatsImpl(File systemDir, Handler handler) {
        int i;
        this.mIsolatedUids = new SparseIntArray();
        this.mUidStats = new SparseArray();
        this.mPartialTimers = new ArrayList();
        this.mFullTimers = new ArrayList();
        this.mWindowTimers = new ArrayList();
        this.mSensorTimers = new SparseArray();
        this.mWifiRunningTimers = new ArrayList();
        this.mFullWifiLockTimers = new ArrayList();
        this.mWifiMulticastTimers = new ArrayList();
        this.mWifiScanTimers = new ArrayList();
        this.mWifiBatchedScanTimers = new SparseArray();
        this.mAudioTurnedOnTimers = new ArrayList();
        this.mVideoTurnedOnTimers = new ArrayList();
        this.mLastPartialTimers = new ArrayList();
        this.mOnBatteryTimeBase = new TimeBase();
        this.mOnBatteryScreenOffTimeBase = new TimeBase();
        this.mActiveEvents = new HistoryEventTracker();
        this.mHaveBatteryLevel = USE_OLD_HISTORY;
        this.mRecordingHistory = USE_OLD_HISTORY;
        this.mHistoryBuffer = Parcel.obtain();
        this.mHistoryLastWritten = new HistoryItem();
        this.mHistoryLastLastWritten = new HistoryItem();
        this.mHistoryReadTmp = new HistoryItem();
        this.mHistoryAddTmp = new HistoryItem();
        this.mHistoryTagPool = new HashMap();
        this.mNextHistoryTagIdx = BATTERY_PLUGGED_NONE;
        this.mNumHistoryTagChars = BATTERY_PLUGGED_NONE;
        this.mHistoryBufferLastPos = -1;
        this.mHistoryOverflow = USE_OLD_HISTORY;
        this.mLastHistoryElapsedRealtime = 0;
        this.mTrackRunningHistoryElapsedRealtime = 0;
        this.mTrackRunningHistoryUptime = 0;
        this.mHistoryCur = new HistoryItem();
        this.mScreenState = BATTERY_PLUGGED_NONE;
        this.mScreenBrightnessBin = -1;
        this.mScreenBrightnessTimer = new StopwatchTimer[5];
        this.mPhoneSignalStrengthBin = -1;
        this.mPhoneSignalStrengthBinRaw = -1;
        this.mPhoneSignalStrengthsTimer = new StopwatchTimer[5];
        this.mPhoneDataConnectionType = -1;
        this.mPhoneDataConnectionsTimer = new StopwatchTimer[17];
        this.mNetworkByteActivityCounters = new LongSamplingCounter[4];
        this.mNetworkPacketActivityCounters = new LongSamplingCounter[4];
        this.mWifiState = -1;
        this.mWifiStateTimer = new StopwatchTimer[8];
        this.mWifiSupplState = -1;
        this.mWifiSupplStateTimer = new StopwatchTimer[13];
        this.mWifiSignalStrengthBin = -1;
        this.mWifiSignalStrengthsTimer = new StopwatchTimer[5];
        this.mBluetoothState = -1;
        this.mBluetoothStateTimer = new StopwatchTimer[4];
        this.mMobileRadioPowerState = DataConnectionRealTimeInfo.DC_POWER_STATE_LOW;
        this.mInitStepMode = BATTERY_PLUGGED_NONE;
        this.mCurStepMode = BATTERY_PLUGGED_NONE;
        this.mModStepMode = BATTERY_PLUGGED_NONE;
        this.mDischargeStepDurations = new long[MAX_LEVEL_STEPS];
        this.mChargeStepDurations = new long[MAX_LEVEL_STEPS];
        this.mLastWriteTime = 0;
        this.mBluetoothPingStart = -1;
        this.mPhoneServiceState = -1;
        this.mPhoneServiceStateRaw = -1;
        this.mPhoneSimStateRaw = -1;
        this.mKernelWakelockStats = new HashMap();
        this.mLastWakeupReason = null;
        this.mLastWakeupUptimeMs = 0;
        this.mWakeupReasonStats = new HashMap();
        this.mProcWakelocksName = new String[STATE_BATTERY_PLUG_MASK];
        this.mProcWakelocksData = new long[STATE_BATTERY_PLUG_MASK];
        this.mProcWakelockFileStats = new HashMap();
        this.mNetworkStatsFactory = new NetworkStatsFactory();
        this.mCurMobileSnapshot = new NetworkStats(SystemClock.elapsedRealtime(), 50);
        this.mLastMobileSnapshot = new NetworkStats(SystemClock.elapsedRealtime(), 50);
        this.mCurWifiSnapshot = new NetworkStats(SystemClock.elapsedRealtime(), 50);
        this.mLastWifiSnapshot = new NetworkStats(SystemClock.elapsedRealtime(), 50);
        this.mTmpNetworkStatsEntry = new Entry();
        this.mMobileIfaces = new String[BATTERY_PLUGGED_NONE];
        this.mWifiIfaces = new String[BATTERY_PLUGGED_NONE];
        this.mChangedStates = BATTERY_PLUGGED_NONE;
        this.mChangedStates2 = BATTERY_PLUGGED_NONE;
        this.mInitialAcquireWakeUid = -1;
        this.mWifiFullLockNesting = BATTERY_PLUGGED_NONE;
        this.mWifiScanNesting = BATTERY_PLUGGED_NONE;
        this.mWifiMulticastNesting = BATTERY_PLUGGED_NONE;
        this.mPendingWrite = null;
        this.mWriteLock = new ReentrantLock();
        if (systemDir != null) {
            this.mFile = new JournaledFile(new File(systemDir, "batterystats.bin"), new File(systemDir, "batterystats.bin.tmp"));
        } else {
            this.mFile = null;
        }
        this.mCheckinFile = new AtomicFile(new File(systemDir, "batterystats-checkin.bin"));
        this.mHandler = new MyHandler(this, handler.getLooper());
        this.mStartCount += NET_UPDATE_MOBILE;
        this.mScreenOnTimer = new StopwatchTimer(null, -1, null, this.mOnBatteryTimeBase);
        for (i = BATTERY_PLUGGED_NONE; i < 5; i += NET_UPDATE_MOBILE) {
            this.mScreenBrightnessTimer[i] = new StopwatchTimer(null, -100 - i, null, this.mOnBatteryTimeBase);
        }
        this.mInteractiveTimer = new StopwatchTimer(null, -9, null, this.mOnBatteryTimeBase);
        this.mLowPowerModeEnabledTimer = new StopwatchTimer(null, -2, null, this.mOnBatteryTimeBase);
        this.mPhoneOnTimer = new StopwatchTimer(null, -3, null, this.mOnBatteryTimeBase);
        for (i = BATTERY_PLUGGED_NONE; i < 5; i += NET_UPDATE_MOBILE) {
            this.mPhoneSignalStrengthsTimer[i] = new StopwatchTimer(null, -200 - i, null, this.mOnBatteryTimeBase);
        }
        this.mPhoneSignalScanningTimer = new StopwatchTimer(null, -199, null, this.mOnBatteryTimeBase);
        for (i = BATTERY_PLUGGED_NONE; i < 17; i += NET_UPDATE_MOBILE) {
            this.mPhoneDataConnectionsTimer[i] = new StopwatchTimer(null, -300 - i, null, this.mOnBatteryTimeBase);
        }
        for (i = BATTERY_PLUGGED_NONE; i < 4; i += NET_UPDATE_MOBILE) {
            this.mNetworkByteActivityCounters[i] = new LongSamplingCounter(this.mOnBatteryTimeBase);
            this.mNetworkPacketActivityCounters[i] = new LongSamplingCounter(this.mOnBatteryTimeBase);
        }
        this.mMobileRadioActiveTimer = new StopwatchTimer(null, -400, null, this.mOnBatteryTimeBase);
        this.mMobileRadioActivePerAppTimer = new StopwatchTimer(null, -401, null, this.mOnBatteryTimeBase);
        this.mMobileRadioActiveAdjustedTime = new LongSamplingCounter(this.mOnBatteryTimeBase);
        this.mMobileRadioActiveUnknownTime = new LongSamplingCounter(this.mOnBatteryTimeBase);
        this.mMobileRadioActiveUnknownCount = new LongSamplingCounter(this.mOnBatteryTimeBase);
        this.mWifiOnTimer = new StopwatchTimer(null, -4, null, this.mOnBatteryTimeBase);
        this.mGlobalWifiRunningTimer = new StopwatchTimer(null, -5, null, this.mOnBatteryTimeBase);
        for (i = BATTERY_PLUGGED_NONE; i < 8; i += NET_UPDATE_MOBILE) {
            this.mWifiStateTimer[i] = new StopwatchTimer(null, -600 - i, null, this.mOnBatteryTimeBase);
        }
        for (i = BATTERY_PLUGGED_NONE; i < 13; i += NET_UPDATE_MOBILE) {
            this.mWifiSupplStateTimer[i] = new StopwatchTimer(null, -700 - i, null, this.mOnBatteryTimeBase);
        }
        for (i = BATTERY_PLUGGED_NONE; i < 5; i += NET_UPDATE_MOBILE) {
            this.mWifiSignalStrengthsTimer[i] = new StopwatchTimer(null, -800 - i, null, this.mOnBatteryTimeBase);
        }
        this.mBluetoothOnTimer = new StopwatchTimer(null, -6, null, this.mOnBatteryTimeBase);
        for (i = BATTERY_PLUGGED_NONE; i < 4; i += NET_UPDATE_MOBILE) {
            this.mBluetoothStateTimer[i] = new StopwatchTimer(null, -500 - i, null, this.mOnBatteryTimeBase);
        }
        this.mAudioOnTimer = new StopwatchTimer(null, -7, null, this.mOnBatteryTimeBase);
        this.mVideoOnTimer = new StopwatchTimer(null, -8, null, this.mOnBatteryTimeBase);
        this.mFlashlightOnTimer = new StopwatchTimer(null, -9, null, this.mOnBatteryTimeBase);
        this.mOnBatteryInternal = USE_OLD_HISTORY;
        this.mOnBattery = USE_OLD_HISTORY;
        initTimes(SystemClock.uptimeMillis() * 1000, SystemClock.elapsedRealtime() * 1000);
        String str = Build.ID;
        this.mEndPlatformVersion = str;
        this.mStartPlatformVersion = str;
        this.mDischargeStartLevel = BATTERY_PLUGGED_NONE;
        this.mDischargeUnplugLevel = BATTERY_PLUGGED_NONE;
        this.mDischargePlugLevel = -1;
        this.mDischargeCurrentLevel = BATTERY_PLUGGED_NONE;
        this.mCurrentBatteryLevel = BATTERY_PLUGGED_NONE;
        initDischarge();
        clearHistoryLocked();
    }

    public BatteryStatsImpl(Parcel p) {
        this.mIsolatedUids = new SparseIntArray();
        this.mUidStats = new SparseArray();
        this.mPartialTimers = new ArrayList();
        this.mFullTimers = new ArrayList();
        this.mWindowTimers = new ArrayList();
        this.mSensorTimers = new SparseArray();
        this.mWifiRunningTimers = new ArrayList();
        this.mFullWifiLockTimers = new ArrayList();
        this.mWifiMulticastTimers = new ArrayList();
        this.mWifiScanTimers = new ArrayList();
        this.mWifiBatchedScanTimers = new SparseArray();
        this.mAudioTurnedOnTimers = new ArrayList();
        this.mVideoTurnedOnTimers = new ArrayList();
        this.mLastPartialTimers = new ArrayList();
        this.mOnBatteryTimeBase = new TimeBase();
        this.mOnBatteryScreenOffTimeBase = new TimeBase();
        this.mActiveEvents = new HistoryEventTracker();
        this.mHaveBatteryLevel = USE_OLD_HISTORY;
        this.mRecordingHistory = USE_OLD_HISTORY;
        this.mHistoryBuffer = Parcel.obtain();
        this.mHistoryLastWritten = new HistoryItem();
        this.mHistoryLastLastWritten = new HistoryItem();
        this.mHistoryReadTmp = new HistoryItem();
        this.mHistoryAddTmp = new HistoryItem();
        this.mHistoryTagPool = new HashMap();
        this.mNextHistoryTagIdx = BATTERY_PLUGGED_NONE;
        this.mNumHistoryTagChars = BATTERY_PLUGGED_NONE;
        this.mHistoryBufferLastPos = -1;
        this.mHistoryOverflow = USE_OLD_HISTORY;
        this.mLastHistoryElapsedRealtime = 0;
        this.mTrackRunningHistoryElapsedRealtime = 0;
        this.mTrackRunningHistoryUptime = 0;
        this.mHistoryCur = new HistoryItem();
        this.mScreenState = BATTERY_PLUGGED_NONE;
        this.mScreenBrightnessBin = -1;
        this.mScreenBrightnessTimer = new StopwatchTimer[5];
        this.mPhoneSignalStrengthBin = -1;
        this.mPhoneSignalStrengthBinRaw = -1;
        this.mPhoneSignalStrengthsTimer = new StopwatchTimer[5];
        this.mPhoneDataConnectionType = -1;
        this.mPhoneDataConnectionsTimer = new StopwatchTimer[17];
        this.mNetworkByteActivityCounters = new LongSamplingCounter[4];
        this.mNetworkPacketActivityCounters = new LongSamplingCounter[4];
        this.mWifiState = -1;
        this.mWifiStateTimer = new StopwatchTimer[8];
        this.mWifiSupplState = -1;
        this.mWifiSupplStateTimer = new StopwatchTimer[13];
        this.mWifiSignalStrengthBin = -1;
        this.mWifiSignalStrengthsTimer = new StopwatchTimer[5];
        this.mBluetoothState = -1;
        this.mBluetoothStateTimer = new StopwatchTimer[4];
        this.mMobileRadioPowerState = DataConnectionRealTimeInfo.DC_POWER_STATE_LOW;
        this.mInitStepMode = BATTERY_PLUGGED_NONE;
        this.mCurStepMode = BATTERY_PLUGGED_NONE;
        this.mModStepMode = BATTERY_PLUGGED_NONE;
        this.mDischargeStepDurations = new long[MAX_LEVEL_STEPS];
        this.mChargeStepDurations = new long[MAX_LEVEL_STEPS];
        this.mLastWriteTime = 0;
        this.mBluetoothPingStart = -1;
        this.mPhoneServiceState = -1;
        this.mPhoneServiceStateRaw = -1;
        this.mPhoneSimStateRaw = -1;
        this.mKernelWakelockStats = new HashMap();
        this.mLastWakeupReason = null;
        this.mLastWakeupUptimeMs = 0;
        this.mWakeupReasonStats = new HashMap();
        this.mProcWakelocksName = new String[STATE_BATTERY_PLUG_MASK];
        this.mProcWakelocksData = new long[STATE_BATTERY_PLUG_MASK];
        this.mProcWakelockFileStats = new HashMap();
        this.mNetworkStatsFactory = new NetworkStatsFactory();
        this.mCurMobileSnapshot = new NetworkStats(SystemClock.elapsedRealtime(), 50);
        this.mLastMobileSnapshot = new NetworkStats(SystemClock.elapsedRealtime(), 50);
        this.mCurWifiSnapshot = new NetworkStats(SystemClock.elapsedRealtime(), 50);
        this.mLastWifiSnapshot = new NetworkStats(SystemClock.elapsedRealtime(), 50);
        this.mTmpNetworkStatsEntry = new Entry();
        this.mMobileIfaces = new String[BATTERY_PLUGGED_NONE];
        this.mWifiIfaces = new String[BATTERY_PLUGGED_NONE];
        this.mChangedStates = BATTERY_PLUGGED_NONE;
        this.mChangedStates2 = BATTERY_PLUGGED_NONE;
        this.mInitialAcquireWakeUid = -1;
        this.mWifiFullLockNesting = BATTERY_PLUGGED_NONE;
        this.mWifiScanNesting = BATTERY_PLUGGED_NONE;
        this.mWifiMulticastNesting = BATTERY_PLUGGED_NONE;
        this.mPendingWrite = null;
        this.mWriteLock = new ReentrantLock();
        this.mFile = null;
        this.mCheckinFile = null;
        this.mHandler = null;
        clearHistoryLocked();
        readFromParcel(p);
    }

    public void setCallback(BatteryCallback cb) {
        this.mCallback = cb;
    }

    public void setNumSpeedSteps(int steps) {
        if (sNumSpeedSteps == 0) {
            sNumSpeedSteps = steps;
        }
    }

    public void setRadioScanningTimeout(long timeout) {
        if (this.mPhoneSignalScanningTimer != null) {
            this.mPhoneSignalScanningTimer.setTimeout(timeout);
        }
    }

    public boolean startIteratingOldHistoryLocked() {
        HistoryItem historyItem = this.mHistory;
        this.mHistoryIterator = historyItem;
        if (historyItem == null) {
            return USE_OLD_HISTORY;
        }
        this.mHistoryBuffer.setDataPosition(BATTERY_PLUGGED_NONE);
        this.mHistoryReadTmp.clear();
        this.mReadOverflow = USE_OLD_HISTORY;
        this.mIteratingHistory = true;
        return true;
    }

    public boolean getNextOldHistoryLocked(HistoryItem out) {
        boolean end;
        if (this.mHistoryBuffer.dataPosition() >= this.mHistoryBuffer.dataSize()) {
            end = true;
        } else {
            end = USE_OLD_HISTORY;
        }
        if (!end) {
            int i;
            readHistoryDelta(this.mHistoryBuffer, this.mHistoryReadTmp);
            boolean z = this.mReadOverflow;
            if (this.mHistoryReadTmp.cmd == 6) {
                i = NET_UPDATE_MOBILE;
            } else {
                i = BATTERY_PLUGGED_NONE;
            }
            this.mReadOverflow = i | z;
        }
        HistoryItem cur = this.mHistoryIterator;
        if (cur != null) {
            out.setTo(cur);
            this.mHistoryIterator = cur.next;
            if (!this.mReadOverflow) {
                if (end) {
                    Slog.w(TAG, "New history ends before old history!");
                } else if (!out.same(this.mHistoryReadTmp)) {
                    PrintWriter pw = new FastPrintWriter(new LogWriter(5, TAG));
                    pw.println("Histories differ!");
                    pw.println("Old history:");
                    new HistoryPrinter().printNextItem(pw, out, 0, USE_OLD_HISTORY, true);
                    pw.println("New history:");
                    new HistoryPrinter().printNextItem(pw, this.mHistoryReadTmp, 0, USE_OLD_HISTORY, true);
                    pw.flush();
                }
            }
            return true;
        } else if (this.mReadOverflow || end) {
            return USE_OLD_HISTORY;
        } else {
            Slog.w(TAG, "Old history ends before new history!");
            return USE_OLD_HISTORY;
        }
    }

    public void finishIteratingOldHistoryLocked() {
        this.mIteratingHistory = USE_OLD_HISTORY;
        this.mHistoryBuffer.setDataPosition(this.mHistoryBuffer.dataSize());
        this.mHistoryIterator = null;
    }

    public int getHistoryTotalSize() {
        return MAX_HISTORY_BUFFER;
    }

    public int getHistoryUsedSize() {
        return this.mHistoryBuffer.dataSize();
    }

    public boolean startIteratingHistoryLocked() {
        if (this.mHistoryBuffer.dataSize() <= 0) {
            return USE_OLD_HISTORY;
        }
        this.mHistoryBuffer.setDataPosition(BATTERY_PLUGGED_NONE);
        this.mReadOverflow = USE_OLD_HISTORY;
        this.mIteratingHistory = true;
        this.mReadHistoryStrings = new String[this.mHistoryTagPool.size()];
        this.mReadHistoryUids = new int[this.mHistoryTagPool.size()];
        this.mReadHistoryChars = BATTERY_PLUGGED_NONE;
        for (Map.Entry<HistoryTag, Integer> ent : this.mHistoryTagPool.entrySet()) {
            HistoryTag tag = (HistoryTag) ent.getKey();
            int idx = ((Integer) ent.getValue()).intValue();
            this.mReadHistoryStrings[idx] = tag.string;
            this.mReadHistoryUids[idx] = tag.uid;
            this.mReadHistoryChars += tag.string.length() + NET_UPDATE_MOBILE;
        }
        return true;
    }

    public int getHistoryStringPoolSize() {
        return this.mReadHistoryStrings.length;
    }

    public int getHistoryStringPoolBytes() {
        return (this.mReadHistoryStrings.length * 12) + (this.mReadHistoryChars * NET_UPDATE_WIFI);
    }

    public String getHistoryTagPoolString(int index) {
        return this.mReadHistoryStrings[index];
    }

    public int getHistoryTagPoolUid(int index) {
        return this.mReadHistoryUids[index];
    }

    public boolean getNextHistoryLocked(HistoryItem out) {
        boolean end;
        int pos = this.mHistoryBuffer.dataPosition();
        if (pos == 0) {
            out.clear();
        }
        if (pos >= this.mHistoryBuffer.dataSize()) {
            end = true;
        } else {
            end = USE_OLD_HISTORY;
        }
        if (end) {
            return USE_OLD_HISTORY;
        }
        long lastRealtime = out.time;
        long lastWalltime = out.currentTime;
        readHistoryDelta(this.mHistoryBuffer, out);
        if (!(out.cmd == 5 || out.cmd == STATE_BATTERY_STATUS_MASK || lastWalltime == 0)) {
            out.currentTime = (out.time - lastRealtime) + lastWalltime;
        }
        return true;
    }

    public void finishIteratingHistoryLocked() {
        this.mIteratingHistory = USE_OLD_HISTORY;
        this.mHistoryBuffer.setDataPosition(this.mHistoryBuffer.dataSize());
        this.mReadHistoryStrings = null;
    }

    public long getHistoryBaseTime() {
        return this.mHistoryBaseTime;
    }

    public int getStartCount() {
        return this.mStartCount;
    }

    public boolean isOnBattery() {
        return this.mOnBattery;
    }

    public boolean isScreenOn() {
        return this.mScreenState == NET_UPDATE_WIFI ? true : USE_OLD_HISTORY;
    }

    void initTimes(long uptime, long realtime) {
        this.mStartClockTime = System.currentTimeMillis();
        this.mOnBatteryTimeBase.init(uptime, realtime);
        this.mOnBatteryScreenOffTimeBase.init(uptime, realtime);
        this.mRealtime = 0;
        this.mUptime = 0;
        this.mRealtimeStart = realtime;
        this.mUptimeStart = uptime;
    }

    void initDischarge() {
        this.mLowDischargeAmountSinceCharge = BATTERY_PLUGGED_NONE;
        this.mHighDischargeAmountSinceCharge = BATTERY_PLUGGED_NONE;
        this.mDischargeAmountScreenOn = BATTERY_PLUGGED_NONE;
        this.mDischargeAmountScreenOnSinceCharge = BATTERY_PLUGGED_NONE;
        this.mDischargeAmountScreenOff = BATTERY_PLUGGED_NONE;
        this.mDischargeAmountScreenOffSinceCharge = BATTERY_PLUGGED_NONE;
        this.mLastDischargeStepTime = -1;
        this.mNumDischargeStepDurations = BATTERY_PLUGGED_NONE;
        this.mLastChargeStepTime = -1;
        this.mNumChargeStepDurations = BATTERY_PLUGGED_NONE;
    }

    public void resetAllStatsCmdLocked() {
        resetAllStatsLocked();
        long mSecUptime = SystemClock.uptimeMillis();
        long uptime = mSecUptime * 1000;
        long mSecRealtime = SystemClock.elapsedRealtime();
        long realtime = mSecRealtime * 1000;
        this.mDischargeStartLevel = this.mHistoryCur.batteryLevel;
        pullPendingStateUpdatesLocked();
        addHistoryRecordLocked(mSecRealtime, mSecUptime);
        byte b = this.mHistoryCur.batteryLevel;
        this.mCurrentBatteryLevel = b;
        this.mDischargePlugLevel = b;
        this.mDischargeUnplugLevel = b;
        this.mDischargeCurrentLevel = b;
        this.mOnBatteryTimeBase.reset(uptime, realtime);
        this.mOnBatteryScreenOffTimeBase.reset(uptime, realtime);
        if ((this.mHistoryCur.states & DELTA_BATTERY_LEVEL_FLAG) == 0) {
            if (this.mScreenState == NET_UPDATE_WIFI) {
                this.mDischargeScreenOnUnplugLevel = this.mHistoryCur.batteryLevel;
                this.mDischargeScreenOffUnplugLevel = BATTERY_PLUGGED_NONE;
            } else {
                this.mDischargeScreenOnUnplugLevel = BATTERY_PLUGGED_NONE;
                this.mDischargeScreenOffUnplugLevel = this.mHistoryCur.batteryLevel;
            }
            this.mDischargeAmountScreenOn = BATTERY_PLUGGED_NONE;
            this.mDischargeAmountScreenOff = BATTERY_PLUGGED_NONE;
        }
        initActiveHistoryEventsLocked(mSecRealtime, mSecUptime);
    }

    private void resetAllStatsLocked() {
        int i;
        this.mStartCount = BATTERY_PLUGGED_NONE;
        initTimes(SystemClock.uptimeMillis() * 1000, SystemClock.elapsedRealtime() * 1000);
        this.mScreenOnTimer.reset(USE_OLD_HISTORY);
        for (i = BATTERY_PLUGGED_NONE; i < 5; i += NET_UPDATE_MOBILE) {
            this.mScreenBrightnessTimer[i].reset(USE_OLD_HISTORY);
        }
        this.mInteractiveTimer.reset(USE_OLD_HISTORY);
        this.mLowPowerModeEnabledTimer.reset(USE_OLD_HISTORY);
        this.mPhoneOnTimer.reset(USE_OLD_HISTORY);
        this.mAudioOnTimer.reset(USE_OLD_HISTORY);
        this.mVideoOnTimer.reset(USE_OLD_HISTORY);
        this.mFlashlightOnTimer.reset(USE_OLD_HISTORY);
        for (i = BATTERY_PLUGGED_NONE; i < 5; i += NET_UPDATE_MOBILE) {
            this.mPhoneSignalStrengthsTimer[i].reset(USE_OLD_HISTORY);
        }
        this.mPhoneSignalScanningTimer.reset(USE_OLD_HISTORY);
        for (i = BATTERY_PLUGGED_NONE; i < 17; i += NET_UPDATE_MOBILE) {
            this.mPhoneDataConnectionsTimer[i].reset(USE_OLD_HISTORY);
        }
        for (i = BATTERY_PLUGGED_NONE; i < 4; i += NET_UPDATE_MOBILE) {
            this.mNetworkByteActivityCounters[i].reset(USE_OLD_HISTORY);
            this.mNetworkPacketActivityCounters[i].reset(USE_OLD_HISTORY);
        }
        this.mMobileRadioActiveTimer.reset(USE_OLD_HISTORY);
        this.mMobileRadioActivePerAppTimer.reset(USE_OLD_HISTORY);
        this.mMobileRadioActiveAdjustedTime.reset(USE_OLD_HISTORY);
        this.mMobileRadioActiveUnknownTime.reset(USE_OLD_HISTORY);
        this.mMobileRadioActiveUnknownCount.reset(USE_OLD_HISTORY);
        this.mWifiOnTimer.reset(USE_OLD_HISTORY);
        this.mGlobalWifiRunningTimer.reset(USE_OLD_HISTORY);
        for (i = BATTERY_PLUGGED_NONE; i < 8; i += NET_UPDATE_MOBILE) {
            this.mWifiStateTimer[i].reset(USE_OLD_HISTORY);
        }
        for (i = BATTERY_PLUGGED_NONE; i < 13; i += NET_UPDATE_MOBILE) {
            this.mWifiSupplStateTimer[i].reset(USE_OLD_HISTORY);
        }
        for (i = BATTERY_PLUGGED_NONE; i < 5; i += NET_UPDATE_MOBILE) {
            this.mWifiSignalStrengthsTimer[i].reset(USE_OLD_HISTORY);
        }
        this.mBluetoothOnTimer.reset(USE_OLD_HISTORY);
        for (i = BATTERY_PLUGGED_NONE; i < 4; i += NET_UPDATE_MOBILE) {
            this.mBluetoothStateTimer[i].reset(USE_OLD_HISTORY);
        }
        this.mUnpluggedNumConnectivityChange = BATTERY_PLUGGED_NONE;
        this.mLoadedNumConnectivityChange = BATTERY_PLUGGED_NONE;
        this.mNumConnectivityChange = BATTERY_PLUGGED_NONE;
        i = BATTERY_PLUGGED_NONE;
        while (i < this.mUidStats.size()) {
            if (((Uid) this.mUidStats.valueAt(i)).reset()) {
                this.mUidStats.remove(this.mUidStats.keyAt(i));
                i--;
            }
            i += NET_UPDATE_MOBILE;
        }
        if (this.mKernelWakelockStats.size() > 0) {
            for (SamplingTimer timer : this.mKernelWakelockStats.values()) {
                this.mOnBatteryScreenOffTimeBase.remove(timer);
            }
            this.mKernelWakelockStats.clear();
        }
        if (this.mWakeupReasonStats.size() > 0) {
            for (SamplingTimer timer2 : this.mWakeupReasonStats.values()) {
                this.mOnBatteryTimeBase.remove(timer2);
            }
            this.mWakeupReasonStats.clear();
        }
        initDischarge();
        clearHistoryLocked();
    }

    private void initActiveHistoryEventsLocked(long elapsedRealtimeMs, long uptimeMs) {
        int i = BATTERY_PLUGGED_NONE;
        while (i < 10) {
            if (this.mRecordAllHistory || i != NET_UPDATE_MOBILE) {
                HashMap<String, SparseIntArray> active = this.mActiveEvents.getStateForEvent(i);
                if (active != null) {
                    for (Map.Entry<String, SparseIntArray> ent : active.entrySet()) {
                        SparseIntArray uids = (SparseIntArray) ent.getValue();
                        for (int j = BATTERY_PLUGGED_NONE; j < uids.size(); j += NET_UPDATE_MOBILE) {
                            addHistoryEventLocked(elapsedRealtimeMs, uptimeMs, i, (String) ent.getKey(), uids.keyAt(j));
                        }
                    }
                }
            }
            i += NET_UPDATE_MOBILE;
        }
    }

    void updateDischargeScreenLevelsLocked(boolean oldScreenOn, boolean newScreenOn) {
        int diff;
        if (oldScreenOn) {
            diff = this.mDischargeScreenOnUnplugLevel - this.mDischargeCurrentLevel;
            if (diff > 0) {
                this.mDischargeAmountScreenOn += diff;
                this.mDischargeAmountScreenOnSinceCharge += diff;
            }
        } else {
            diff = this.mDischargeScreenOffUnplugLevel - this.mDischargeCurrentLevel;
            if (diff > 0) {
                this.mDischargeAmountScreenOff += diff;
                this.mDischargeAmountScreenOffSinceCharge += diff;
            }
        }
        if (newScreenOn) {
            this.mDischargeScreenOnUnplugLevel = this.mDischargeCurrentLevel;
            this.mDischargeScreenOffUnplugLevel = BATTERY_PLUGGED_NONE;
            return;
        }
        this.mDischargeScreenOnUnplugLevel = BATTERY_PLUGGED_NONE;
        this.mDischargeScreenOffUnplugLevel = this.mDischargeCurrentLevel;
    }

    public void pullPendingStateUpdatesLocked() {
        updateKernelWakelocksLocked();
        updateNetworkActivityLocked(NET_UPDATE_ALL, SystemClock.elapsedRealtime());
        if (this.mOnBatteryInternal) {
            boolean screenOn = this.mScreenState == NET_UPDATE_WIFI ? true : USE_OLD_HISTORY;
            updateDischargeScreenLevelsLocked(screenOn, screenOn);
        }
    }

    void setOnBatteryLocked(long mSecRealtime, long mSecUptime, boolean onBattery, int oldStatus, int level) {
        boolean doWrite = USE_OLD_HISTORY;
        Message m = this.mHandler.obtainMessage(NET_UPDATE_WIFI);
        m.arg1 = onBattery ? NET_UPDATE_MOBILE : BATTERY_PLUGGED_NONE;
        this.mHandler.sendMessage(m);
        long uptime = mSecUptime * 1000;
        long realtime = mSecRealtime * 1000;
        boolean screenOn = this.mScreenState == NET_UPDATE_WIFI ? true : USE_OLD_HISTORY;
        HistoryItem historyItem;
        if (onBattery) {
            boolean z;
            boolean reset = USE_OLD_HISTORY;
            if (!this.mNoAutoReset && (oldStatus == 5 || level >= 90 || ((this.mDischargeCurrentLevel < 20 && level >= 80) || (getHighDischargeAmountSinceCharge() >= MAX_LEVEL_STEPS && this.mHistoryBuffer.dataSize() >= MAX_HISTORY_BUFFER)))) {
                Slog.i(TAG, "Resetting battery stats: level=" + level + " status=" + oldStatus + " dischargeLevel=" + this.mDischargeCurrentLevel + " lowAmount=" + getLowDischargeAmountSinceCharge() + " highAmount=" + getHighDischargeAmountSinceCharge());
                if (getLowDischargeAmountSinceCharge() >= 20) {
                    Parcel parcel = Parcel.obtain();
                    writeSummaryToParcel(parcel, true);
                    BackgroundThread.getHandler().post(new AnonymousClass1(this, parcel));
                }
                doWrite = true;
                resetAllStatsLocked();
                this.mDischargeStartLevel = level;
                reset = true;
                this.mNumDischargeStepDurations = BATTERY_PLUGGED_NONE;
            }
            this.mOnBatteryInternal = onBattery;
            this.mOnBattery = onBattery;
            this.mLastDischargeStepLevel = level;
            this.mMinDischargeStepLevel = level;
            this.mLastDischargeStepTime = -1;
            this.mInitStepMode = this.mCurStepMode;
            this.mModStepMode = BATTERY_PLUGGED_NONE;
            pullPendingStateUpdatesLocked();
            this.mHistoryCur.batteryLevel = (byte) level;
            historyItem = this.mHistoryCur;
            historyItem.states &= -524289;
            if (reset) {
                this.mRecordingHistory = true;
                startRecordingHistory(mSecRealtime, mSecUptime, reset);
            }
            addHistoryRecordLocked(mSecRealtime, mSecUptime);
            this.mDischargeUnplugLevel = level;
            this.mDischargeCurrentLevel = level;
            if (screenOn) {
                this.mDischargeScreenOnUnplugLevel = level;
                this.mDischargeScreenOffUnplugLevel = BATTERY_PLUGGED_NONE;
            } else {
                this.mDischargeScreenOnUnplugLevel = BATTERY_PLUGGED_NONE;
                this.mDischargeScreenOffUnplugLevel = level;
            }
            this.mDischargeAmountScreenOn = BATTERY_PLUGGED_NONE;
            this.mDischargeAmountScreenOff = BATTERY_PLUGGED_NONE;
            if (screenOn) {
                z = USE_OLD_HISTORY;
            } else {
                z = true;
            }
            updateTimeBasesLocked(true, z, uptime, realtime);
        } else {
            this.mOnBatteryInternal = onBattery;
            this.mOnBattery = onBattery;
            pullPendingStateUpdatesLocked();
            this.mHistoryCur.batteryLevel = (byte) level;
            historyItem = this.mHistoryCur;
            historyItem.states |= DELTA_BATTERY_LEVEL_FLAG;
            addHistoryRecordLocked(mSecRealtime, mSecUptime);
            this.mDischargePlugLevel = level;
            this.mDischargeCurrentLevel = level;
            if (level < this.mDischargeUnplugLevel) {
                this.mLowDischargeAmountSinceCharge += (this.mDischargeUnplugLevel - level) - 1;
                this.mHighDischargeAmountSinceCharge += this.mDischargeUnplugLevel - level;
            }
            updateDischargeScreenLevelsLocked(screenOn, screenOn);
            updateTimeBasesLocked(USE_OLD_HISTORY, !screenOn ? true : USE_OLD_HISTORY, uptime, realtime);
            this.mNumChargeStepDurations = BATTERY_PLUGGED_NONE;
            this.mLastChargeStepLevel = level;
            this.mMaxChargeStepLevel = level;
            this.mLastChargeStepTime = -1;
            this.mInitStepMode = this.mCurStepMode;
            this.mModStepMode = BATTERY_PLUGGED_NONE;
        }
        if ((doWrite || this.mLastWriteTime + DateUtils.MINUTE_IN_MILLIS < mSecRealtime) && this.mFile != null) {
            writeAsyncLocked();
        }
    }

    private void startRecordingHistory(long elapsedRealtimeMs, long uptimeMs, boolean reset) {
        this.mRecordingHistory = true;
        this.mHistoryCur.currentTime = System.currentTimeMillis();
        this.mLastRecordedClockTime = this.mHistoryCur.currentTime;
        this.mLastRecordedClockRealtime = elapsedRealtimeMs;
        addHistoryBufferLocked(elapsedRealtimeMs, uptimeMs, reset ? (byte) 7 : (byte) 5, this.mHistoryCur);
        this.mHistoryCur.currentTime = 0;
        if (reset) {
            initActiveHistoryEventsLocked(elapsedRealtimeMs, uptimeMs);
        }
    }

    private void recordCurrentTimeChangeLocked(long currentTime, long elapsedRealtimeMs, long uptimeMs) {
        if (this.mRecordingHistory) {
            this.mHistoryCur.currentTime = currentTime;
            this.mLastRecordedClockTime = currentTime;
            this.mLastRecordedClockRealtime = elapsedRealtimeMs;
            addHistoryBufferLocked(elapsedRealtimeMs, uptimeMs, (byte) 5, this.mHistoryCur);
            this.mHistoryCur.currentTime = 0;
        }
    }

    private void recordShutdownLocked(long elapsedRealtimeMs, long uptimeMs) {
        if (this.mRecordingHistory) {
            this.mHistoryCur.currentTime = System.currentTimeMillis();
            this.mLastRecordedClockTime = this.mHistoryCur.currentTime;
            this.mLastRecordedClockRealtime = elapsedRealtimeMs;
            addHistoryBufferLocked(elapsedRealtimeMs, uptimeMs, (byte) 8, this.mHistoryCur);
            this.mHistoryCur.currentTime = 0;
        }
    }

    private static int addLevelSteps(long[] steps, int stepCount, long lastStepTime, int numStepLevels, long modeBits, long elapsedRealtime) {
        if (lastStepTime < 0 || numStepLevels <= 0) {
            return stepCount;
        }
        long duration = elapsedRealtime - lastStepTime;
        for (int i = BATTERY_PLUGGED_NONE; i < numStepLevels; i += NET_UPDATE_MOBILE) {
            System.arraycopy(steps, BATTERY_PLUGGED_NONE, steps, NET_UPDATE_MOBILE, steps.length - 1);
            long thisDuration = duration / ((long) (numStepLevels - i));
            duration -= thisDuration;
            if (thisDuration > 1099511627775L) {
                thisDuration = 1099511627775L;
            }
            steps[BATTERY_PLUGGED_NONE] = thisDuration | modeBits;
        }
        stepCount += numStepLevels;
        if (stepCount > steps.length) {
            return steps.length;
        }
        return stepCount;
    }

    public void setBatteryState(int status, int health, int plugType, int level, int temp, int volt) {
        synchronized (this) {
            boolean onBattery = plugType == 0 ? true : USE_OLD_HISTORY;
            long uptime = SystemClock.uptimeMillis();
            long elapsedRealtime = SystemClock.elapsedRealtime();
            int oldStatus = this.mHistoryCur.batteryStatus;
            if (!this.mHaveBatteryLevel) {
                this.mHaveBatteryLevel = true;
                if (onBattery == this.mOnBattery) {
                    HistoryItem historyItem;
                    if (onBattery) {
                        historyItem = this.mHistoryCur;
                        historyItem.states &= -524289;
                    } else {
                        historyItem = this.mHistoryCur;
                        historyItem.states |= DELTA_BATTERY_LEVEL_FLAG;
                    }
                }
                oldStatus = status;
            }
            if (onBattery) {
                this.mDischargeCurrentLevel = level;
                if (!this.mRecordingHistory) {
                    this.mRecordingHistory = true;
                    startRecordingHistory(elapsedRealtime, uptime, true);
                }
            } else if (level < 96) {
                if (!this.mRecordingHistory) {
                    this.mRecordingHistory = true;
                    startRecordingHistory(elapsedRealtime, uptime, true);
                }
            }
            this.mCurrentBatteryLevel = level;
            if (this.mDischargePlugLevel < 0) {
                this.mDischargePlugLevel = level;
            }
            if (onBattery != this.mOnBattery) {
                this.mHistoryCur.batteryLevel = (byte) level;
                this.mHistoryCur.batteryStatus = (byte) status;
                this.mHistoryCur.batteryHealth = (byte) health;
                this.mHistoryCur.batteryPlugType = (byte) plugType;
                this.mHistoryCur.batteryTemperature = (short) temp;
                this.mHistoryCur.batteryVoltage = (char) volt;
                setOnBatteryLocked(elapsedRealtime, uptime, onBattery, oldStatus, level);
            } else {
                boolean changed = USE_OLD_HISTORY;
                if (this.mHistoryCur.batteryLevel != level) {
                    this.mHistoryCur.batteryLevel = (byte) level;
                    changed = true;
                }
                if (this.mHistoryCur.batteryStatus != status) {
                    this.mHistoryCur.batteryStatus = (byte) status;
                    changed = true;
                }
                if (this.mHistoryCur.batteryHealth != health) {
                    this.mHistoryCur.batteryHealth = (byte) health;
                    changed = true;
                }
                if (this.mHistoryCur.batteryPlugType != plugType) {
                    this.mHistoryCur.batteryPlugType = (byte) plugType;
                    changed = true;
                }
                if (temp >= this.mHistoryCur.batteryTemperature + 10 || temp <= this.mHistoryCur.batteryTemperature - 10) {
                    this.mHistoryCur.batteryTemperature = (short) temp;
                    changed = true;
                }
                if (volt > this.mHistoryCur.batteryVoltage + 20 || volt < this.mHistoryCur.batteryVoltage - 20) {
                    this.mHistoryCur.batteryVoltage = (char) volt;
                    changed = true;
                }
                if (changed) {
                    addHistoryRecordLocked(elapsedRealtime, uptime);
                }
                long modeBits = ((((long) this.mInitStepMode) << 48) | (((long) this.mModStepMode) << 56)) | (((long) (level & R.styleable.Theme_windowSharedElementReturnTransition)) << 40);
                if (onBattery) {
                    if (this.mLastDischargeStepLevel != level && this.mMinDischargeStepLevel > level) {
                        this.mNumDischargeStepDurations = addLevelSteps(this.mDischargeStepDurations, this.mNumDischargeStepDurations, this.mLastDischargeStepTime, this.mLastDischargeStepLevel - level, modeBits, elapsedRealtime);
                        this.mLastDischargeStepLevel = level;
                        this.mMinDischargeStepLevel = level;
                        this.mLastDischargeStepTime = elapsedRealtime;
                        this.mInitStepMode = this.mCurStepMode;
                        this.mModStepMode = BATTERY_PLUGGED_NONE;
                    }
                } else if (this.mLastChargeStepLevel != level && this.mMaxChargeStepLevel < level) {
                    this.mNumChargeStepDurations = addLevelSteps(this.mChargeStepDurations, this.mNumChargeStepDurations, this.mLastChargeStepTime, level - this.mLastChargeStepLevel, modeBits, elapsedRealtime);
                    this.mLastChargeStepLevel = level;
                    this.mMaxChargeStepLevel = level;
                    this.mLastChargeStepTime = elapsedRealtime;
                    this.mInitStepMode = this.mCurStepMode;
                    this.mModStepMode = BATTERY_PLUGGED_NONE;
                }
            }
            if (!onBattery && status == 5) {
                this.mRecordingHistory = USE_OLD_HISTORY;
            }
        }
    }

    public void updateKernelWakelocksLocked() {
        Map<String, KernelWakelockStats> m = readKernelWakelockStats();
        if (m == null) {
            Slog.w(TAG, "Couldn't get kernel wake lock stats");
            return;
        }
        for (Map.Entry<String, KernelWakelockStats> ent : m.entrySet()) {
            String name = (String) ent.getKey();
            KernelWakelockStats kws = (KernelWakelockStats) ent.getValue();
            SamplingTimer kwlt = (SamplingTimer) this.mKernelWakelockStats.get(name);
            if (kwlt == null) {
                kwlt = new SamplingTimer(this.mOnBatteryScreenOffTimeBase, true);
                this.mKernelWakelockStats.put(name, kwlt);
            }
            kwlt.updateCurrentReportedCount(kws.mCount);
            kwlt.updateCurrentReportedTotalTime(kws.mTotalTime);
            kwlt.setUpdateVersion(sKernelWakelockUpdateVersion);
        }
        if (m.size() != this.mKernelWakelockStats.size()) {
            for (Map.Entry<String, SamplingTimer> ent2 : this.mKernelWakelockStats.entrySet()) {
                SamplingTimer st = (SamplingTimer) ent2.getValue();
                if (st.getUpdateVersion() != sKernelWakelockUpdateVersion) {
                    st.setStale();
                }
            }
        }
    }

    private void updateNetworkActivityLocked(int which, long elapsedRealtimeMs) {
        if (SystemProperties.getBoolean(NetworkManagementSocketTagger.PROP_QTAGUID_ENABLED, USE_OLD_HISTORY)) {
            NetworkStats last;
            NetworkStats snapshot;
            NetworkStats delta;
            int size;
            int i;
            Entry entry;
            Uid u;
            if ((which & NET_UPDATE_MOBILE) != 0 && this.mMobileIfaces.length > 0) {
                last = this.mCurMobileSnapshot;
                try {
                    snapshot = this.mNetworkStatsFactory.readNetworkStatsDetail(-1, this.mMobileIfaces, BATTERY_PLUGGED_NONE, this.mLastMobileSnapshot);
                    this.mCurMobileSnapshot = snapshot;
                    this.mLastMobileSnapshot = last;
                    if (this.mOnBatteryInternal) {
                        delta = NetworkStats.subtract(snapshot, last, null, null, this.mTmpNetworkStats);
                        this.mTmpNetworkStats = delta;
                        long radioTime = this.mMobileRadioActivePerAppTimer.checkpointRunningLocked(elapsedRealtimeMs);
                        long totalPackets = delta.getTotalPackets();
                        size = delta.size();
                        for (i = BATTERY_PLUGGED_NONE; i < size; i += NET_UPDATE_MOBILE) {
                            entry = delta.getValues(i, this.mTmpNetworkStatsEntry);
                            if (!(entry.rxBytes == 0 || entry.txBytes == 0)) {
                                u = getUidStatsLocked(mapUid(entry.uid));
                                u.noteNetworkActivityLocked(BATTERY_PLUGGED_NONE, entry.rxBytes, entry.rxPackets);
                                u.noteNetworkActivityLocked(NET_UPDATE_MOBILE, entry.txBytes, entry.txPackets);
                                if (radioTime > 0) {
                                    long appPackets = entry.rxPackets + entry.txPackets;
                                    long appRadioTime = (radioTime * appPackets) / totalPackets;
                                    u.noteMobileRadioActiveTimeLocked(appRadioTime);
                                    radioTime -= appRadioTime;
                                    totalPackets -= appPackets;
                                }
                                this.mNetworkByteActivityCounters[BATTERY_PLUGGED_NONE].addCountLocked(entry.rxBytes);
                                this.mNetworkByteActivityCounters[NET_UPDATE_MOBILE].addCountLocked(entry.txBytes);
                                this.mNetworkPacketActivityCounters[BATTERY_PLUGGED_NONE].addCountLocked(entry.rxPackets);
                                this.mNetworkPacketActivityCounters[NET_UPDATE_MOBILE].addCountLocked(entry.txPackets);
                            }
                        }
                        if (radioTime > 0) {
                            this.mMobileRadioActiveUnknownTime.addCountLocked(radioTime);
                            this.mMobileRadioActiveUnknownCount.addCountLocked(1);
                        }
                    }
                } catch (IOException e) {
                    Log.wtf(TAG, "Failed to read mobile network stats", e);
                    return;
                }
            }
            if ((which & NET_UPDATE_WIFI) != 0 && this.mWifiIfaces.length > 0) {
                last = this.mCurWifiSnapshot;
                try {
                    snapshot = this.mNetworkStatsFactory.readNetworkStatsDetail(-1, this.mWifiIfaces, BATTERY_PLUGGED_NONE, this.mLastWifiSnapshot);
                    this.mCurWifiSnapshot = snapshot;
                    this.mLastWifiSnapshot = last;
                    if (this.mOnBatteryInternal) {
                        delta = NetworkStats.subtract(snapshot, last, null, null, this.mTmpNetworkStats);
                        this.mTmpNetworkStats = delta;
                        size = delta.size();
                        for (i = BATTERY_PLUGGED_NONE; i < size; i += NET_UPDATE_MOBILE) {
                            entry = delta.getValues(i, this.mTmpNetworkStatsEntry);
                            if (!(entry.rxBytes == 0 || entry.txBytes == 0)) {
                                u = getUidStatsLocked(mapUid(entry.uid));
                                u.noteNetworkActivityLocked(NET_UPDATE_WIFI, entry.rxBytes, entry.rxPackets);
                                u.noteNetworkActivityLocked(STATE_BATTERY_PLUG_MASK, entry.txBytes, entry.txPackets);
                                this.mNetworkByteActivityCounters[NET_UPDATE_WIFI].addCountLocked(entry.rxBytes);
                                this.mNetworkByteActivityCounters[STATE_BATTERY_PLUG_MASK].addCountLocked(entry.txBytes);
                                this.mNetworkPacketActivityCounters[NET_UPDATE_WIFI].addCountLocked(entry.rxPackets);
                                this.mNetworkPacketActivityCounters[STATE_BATTERY_PLUG_MASK].addCountLocked(entry.txPackets);
                            }
                        }
                    }
                } catch (IOException e2) {
                    Log.wtf(TAG, "Failed to read wifi network stats", e2);
                }
            }
        }
    }

    public long getAwakeTimeBattery() {
        return computeBatteryUptime(getBatteryUptimeLocked(), NET_UPDATE_MOBILE);
    }

    public long getAwakeTimePlugged() {
        return (SystemClock.uptimeMillis() * 1000) - getAwakeTimeBattery();
    }

    public long computeUptime(long curTime, int which) {
        switch (which) {
            case BATTERY_PLUGGED_NONE /*0*/:
                return this.mUptime + (curTime - this.mUptimeStart);
            case NET_UPDATE_MOBILE /*1*/:
                return curTime - this.mUptimeStart;
            case NET_UPDATE_WIFI /*2*/:
                return curTime - this.mOnBatteryTimeBase.getUptimeStart();
            default:
                return 0;
        }
    }

    public long computeRealtime(long curTime, int which) {
        switch (which) {
            case BATTERY_PLUGGED_NONE /*0*/:
                return this.mRealtime + (curTime - this.mRealtimeStart);
            case NET_UPDATE_MOBILE /*1*/:
                return curTime - this.mRealtimeStart;
            case NET_UPDATE_WIFI /*2*/:
                return curTime - this.mOnBatteryTimeBase.getRealtimeStart();
            default:
                return 0;
        }
    }

    public long computeBatteryUptime(long curTime, int which) {
        return this.mOnBatteryTimeBase.computeUptime(curTime, which);
    }

    public long computeBatteryRealtime(long curTime, int which) {
        return this.mOnBatteryTimeBase.computeRealtime(curTime, which);
    }

    public long computeBatteryScreenOffUptime(long curTime, int which) {
        return this.mOnBatteryScreenOffTimeBase.computeUptime(curTime, which);
    }

    public long computeBatteryScreenOffRealtime(long curTime, int which) {
        return this.mOnBatteryScreenOffTimeBase.computeRealtime(curTime, which);
    }

    private long computeTimePerLevel(long[] steps, int numSteps) {
        if (numSteps <= 0) {
            return -1;
        }
        long total = 0;
        for (int i = BATTERY_PLUGGED_NONE; i < numSteps; i += NET_UPDATE_MOBILE) {
            total += steps[i] & 1099511627775L;
        }
        return total / ((long) numSteps);
    }

    public long computeBatteryTimeRemaining(long curTime) {
        if (!this.mOnBattery || this.mNumDischargeStepDurations < NET_UPDATE_MOBILE) {
            return -1;
        }
        long msPerLevel = computeTimePerLevel(this.mDischargeStepDurations, this.mNumDischargeStepDurations);
        if (msPerLevel > 0) {
            return (((long) this.mCurrentBatteryLevel) * msPerLevel) * 1000;
        }
        return -1;
    }

    public int getNumDischargeStepDurations() {
        return this.mNumDischargeStepDurations;
    }

    public long[] getDischargeStepDurationsArray() {
        return this.mDischargeStepDurations;
    }

    public long computeChargeTimeRemaining(long curTime) {
        if (this.mOnBattery || this.mNumChargeStepDurations < NET_UPDATE_MOBILE) {
            return -1;
        }
        long msPerLevel = computeTimePerLevel(this.mChargeStepDurations, this.mNumChargeStepDurations);
        if (msPerLevel > 0) {
            return (((long) (100 - this.mCurrentBatteryLevel)) * msPerLevel) * 1000;
        }
        return -1;
    }

    public int getNumChargeStepDurations() {
        return this.mNumChargeStepDurations;
    }

    public long[] getChargeStepDurationsArray() {
        return this.mChargeStepDurations;
    }

    long getBatteryUptimeLocked() {
        return this.mOnBatteryTimeBase.getUptime(SystemClock.uptimeMillis() * 1000);
    }

    public long getBatteryUptime(long curTime) {
        return this.mOnBatteryTimeBase.getUptime(curTime);
    }

    public long getBatteryRealtime(long curTime) {
        return this.mOnBatteryTimeBase.getRealtime(curTime);
    }

    public int getDischargeStartLevel() {
        int dischargeStartLevelLocked;
        synchronized (this) {
            dischargeStartLevelLocked = getDischargeStartLevelLocked();
        }
        return dischargeStartLevelLocked;
    }

    public int getDischargeStartLevelLocked() {
        return this.mDischargeUnplugLevel;
    }

    public int getDischargeCurrentLevel() {
        int dischargeCurrentLevelLocked;
        synchronized (this) {
            dischargeCurrentLevelLocked = getDischargeCurrentLevelLocked();
        }
        return dischargeCurrentLevelLocked;
    }

    public int getDischargeCurrentLevelLocked() {
        return this.mDischargeCurrentLevel;
    }

    public int getLowDischargeAmountSinceCharge() {
        int val;
        synchronized (this) {
            val = this.mLowDischargeAmountSinceCharge;
            if (this.mOnBattery && this.mDischargeCurrentLevel < this.mDischargeUnplugLevel) {
                val += (this.mDischargeUnplugLevel - this.mDischargeCurrentLevel) - 1;
            }
        }
        return val;
    }

    public int getHighDischargeAmountSinceCharge() {
        int val;
        synchronized (this) {
            val = this.mHighDischargeAmountSinceCharge;
            if (this.mOnBattery && this.mDischargeCurrentLevel < this.mDischargeUnplugLevel) {
                val += this.mDischargeUnplugLevel - this.mDischargeCurrentLevel;
            }
        }
        return val;
    }

    public int getDischargeAmount(int which) {
        int dischargeAmount = which == 0 ? getHighDischargeAmountSinceCharge() : getDischargeStartLevel() - getDischargeCurrentLevel();
        if (dischargeAmount < 0) {
            return BATTERY_PLUGGED_NONE;
        }
        return dischargeAmount;
    }

    public int getDischargeAmountScreenOn() {
        int val;
        synchronized (this) {
            val = this.mDischargeAmountScreenOn;
            if (this.mOnBattery && this.mScreenState == NET_UPDATE_WIFI && this.mDischargeCurrentLevel < this.mDischargeScreenOnUnplugLevel) {
                val += this.mDischargeScreenOnUnplugLevel - this.mDischargeCurrentLevel;
            }
        }
        return val;
    }

    public int getDischargeAmountScreenOnSinceCharge() {
        int val;
        synchronized (this) {
            val = this.mDischargeAmountScreenOnSinceCharge;
            if (this.mOnBattery && this.mScreenState == NET_UPDATE_WIFI && this.mDischargeCurrentLevel < this.mDischargeScreenOnUnplugLevel) {
                val += this.mDischargeScreenOnUnplugLevel - this.mDischargeCurrentLevel;
            }
        }
        return val;
    }

    public int getDischargeAmountScreenOff() {
        int val;
        synchronized (this) {
            val = this.mDischargeAmountScreenOff;
            if (this.mOnBattery && this.mScreenState != NET_UPDATE_WIFI && this.mDischargeCurrentLevel < this.mDischargeScreenOffUnplugLevel) {
                val += this.mDischargeScreenOffUnplugLevel - this.mDischargeCurrentLevel;
            }
        }
        return val;
    }

    public int getDischargeAmountScreenOffSinceCharge() {
        int val;
        synchronized (this) {
            val = this.mDischargeAmountScreenOffSinceCharge;
            if (this.mOnBattery && this.mScreenState != NET_UPDATE_WIFI && this.mDischargeCurrentLevel < this.mDischargeScreenOffUnplugLevel) {
                val += this.mDischargeScreenOffUnplugLevel - this.mDischargeCurrentLevel;
            }
        }
        return val;
    }

    public int getCpuSpeedSteps() {
        return sNumSpeedSteps;
    }

    public Uid getUidStatsLocked(int uid) {
        Uid u = (Uid) this.mUidStats.get(uid);
        if (u != null) {
            return u;
        }
        u = new Uid(this, uid);
        this.mUidStats.put(uid, u);
        return u;
    }

    public void removeUidStatsLocked(int uid) {
        this.mUidStats.remove(uid);
    }

    public Proc getProcessStatsLocked(int uid, String name) {
        return getUidStatsLocked(mapUid(uid)).getProcessStatsLocked(name);
    }

    public Pkg getPackageStatsLocked(int uid, String pkg) {
        return getUidStatsLocked(mapUid(uid)).getPackageStatsLocked(pkg);
    }

    public Serv getServiceStatsLocked(int uid, String pkg, String name) {
        return getUidStatsLocked(mapUid(uid)).getServiceStatsLocked(pkg, name);
    }

    public void distributeWorkLocked(int which) {
        Uid wifiUid = (Uid) this.mUidStats.get(RILConstants.RIL_UNSOL_DATA_CALL_LIST_CHANGED);
        if (wifiUid != null) {
            long uSecTime = computeBatteryRealtime(SystemClock.elapsedRealtime() * 1000, which);
            for (int ip = wifiUid.mProcessStats.size() - 1; ip >= 0; ip--) {
                Proc proc = (Proc) wifiUid.mProcessStats.valueAt(ip);
                long totalRunningTime = getGlobalWifiRunningTime(uSecTime, which);
                int i = BATTERY_PLUGGED_NONE;
                while (true) {
                    if (i >= this.mUidStats.size()) {
                        break;
                    }
                    Uid uid = (Uid) this.mUidStats.valueAt(i);
                    int i2 = uid.mUid;
                    if (r0 != 1010) {
                        long uidRunningTime = uid.getWifiRunningTime(uSecTime, which);
                        if (uidRunningTime > 0) {
                            Proc uidProc = uid.getProcessStatsLocked("*wifi*");
                            long time = (proc.getUserTime(which) * uidRunningTime) / totalRunningTime;
                            uidProc.mUserTime += time;
                            proc.mUserTime -= time;
                            time = (proc.getSystemTime(which) * uidRunningTime) / totalRunningTime;
                            uidProc.mSystemTime += time;
                            proc.mSystemTime -= time;
                            time = (proc.getForegroundTime(which) * uidRunningTime) / totalRunningTime;
                            uidProc.mForegroundTime += time;
                            proc.mForegroundTime -= time;
                            int sb = BATTERY_PLUGGED_NONE;
                            while (true) {
                                i2 = proc.mSpeedBins.length;
                                if (sb >= r0) {
                                    break;
                                }
                                SamplingCounter sc = proc.mSpeedBins[sb];
                                if (sc != null) {
                                    time = (((long) sc.getCountLocked(which)) * uidRunningTime) / totalRunningTime;
                                    SamplingCounter uidSc = uidProc.mSpeedBins[sb];
                                    if (uidSc == null) {
                                        SamplingCounter samplingCounter = new SamplingCounter(this.mOnBatteryTimeBase);
                                        uidProc.mSpeedBins[sb] = samplingCounter;
                                    }
                                    uidSc.mCount.addAndGet((int) time);
                                    sc.mCount.addAndGet((int) (-time));
                                }
                                sb += NET_UPDATE_MOBILE;
                            }
                            totalRunningTime -= uidRunningTime;
                        }
                    }
                    i += NET_UPDATE_MOBILE;
                }
            }
        }
    }

    public void shutdownLocked() {
        recordShutdownLocked(SystemClock.elapsedRealtime(), SystemClock.uptimeMillis());
        writeSyncLocked();
        this.mShuttingDown = true;
    }

    public void writeAsyncLocked() {
        writeLocked(USE_OLD_HISTORY);
    }

    public void writeSyncLocked() {
        writeLocked(true);
    }

    void writeLocked(boolean sync) {
        if (this.mFile == null) {
            Slog.w("BatteryStats", "writeLocked: no file associated with this instance");
        } else if (!this.mShuttingDown) {
            Parcel out = Parcel.obtain();
            writeSummaryToParcel(out, true);
            this.mLastWriteTime = SystemClock.elapsedRealtime();
            if (this.mPendingWrite != null) {
                this.mPendingWrite.recycle();
            }
            this.mPendingWrite = out;
            if (sync) {
                commitPendingDataToDisk();
            } else {
                BackgroundThread.getHandler().post(new AnonymousClass2(this));
            }
        }
    }

    public void commitPendingDataToDisk() {
        synchronized (this) {
            Parcel next = this.mPendingWrite;
            this.mPendingWrite = null;
            if (next == null) {
                return;
            }
            this.mWriteLock.lock();
            try {
                FileOutputStream stream = new FileOutputStream(this.mFile.chooseForWrite());
                stream.write(next.marshall());
                stream.flush();
                FileUtils.sync(stream);
                stream.close();
                this.mFile.commit();
            } catch (IOException e) {
                Slog.w("BatteryStats", "Error writing battery statistics", e);
                this.mFile.rollback();
            } finally {
                next.recycle();
                this.mWriteLock.unlock();
            }
        }
    }

    public void readLocked() {
        if (this.mFile == null) {
            Slog.w("BatteryStats", "readLocked: no file associated with this instance");
            return;
        }
        this.mUidStats.clear();
        try {
            File file = this.mFile.chooseForRead();
            if (file.exists()) {
                FileInputStream stream = new FileInputStream(file);
                byte[] raw = BatteryStatsHelper.readFully(stream);
                Parcel in = Parcel.obtain();
                in.unmarshall(raw, BATTERY_PLUGGED_NONE, raw.length);
                in.setDataPosition(BATTERY_PLUGGED_NONE);
                stream.close();
                readSummaryFromParcel(in);
                this.mEndPlatformVersion = Build.ID;
                if (this.mHistoryBuffer.dataPosition() > 0) {
                    this.mRecordingHistory = true;
                    long elapsedRealtime = SystemClock.elapsedRealtime();
                    long uptime = SystemClock.uptimeMillis();
                    addHistoryBufferLocked(elapsedRealtime, uptime, (byte) 4, this.mHistoryCur);
                    startRecordingHistory(elapsedRealtime, uptime, USE_OLD_HISTORY);
                }
            }
        } catch (Exception e) {
            Slog.e("BatteryStats", "Error reading battery statistics", e);
        }
    }

    public int describeContents() {
        return BATTERY_PLUGGED_NONE;
    }

    void readHistory(Parcel in, boolean andOldHistory) {
        long historyBaseTime = in.readLong();
        this.mHistoryBuffer.setDataSize(BATTERY_PLUGGED_NONE);
        this.mHistoryBuffer.setDataPosition(BATTERY_PLUGGED_NONE);
        this.mHistoryTagPool.clear();
        this.mNextHistoryTagIdx = BATTERY_PLUGGED_NONE;
        this.mNumHistoryTagChars = BATTERY_PLUGGED_NONE;
        int numTags = in.readInt();
        for (int i = BATTERY_PLUGGED_NONE; i < numTags; i += NET_UPDATE_MOBILE) {
            int idx = in.readInt();
            String str = in.readString();
            int uid = in.readInt();
            HistoryTag tag = new HistoryTag();
            tag.string = str;
            tag.uid = uid;
            tag.poolIdx = idx;
            this.mHistoryTagPool.put(tag, Integer.valueOf(idx));
            if (idx >= this.mNextHistoryTagIdx) {
                this.mNextHistoryTagIdx = idx + NET_UPDATE_MOBILE;
            }
            this.mNumHistoryTagChars += tag.string.length() + NET_UPDATE_MOBILE;
        }
        int bufSize = in.readInt();
        int curPos = in.dataPosition();
        if (bufSize >= 983040) {
            Slog.w(TAG, "File corrupt: history data buffer too large " + bufSize);
        } else if ((bufSize & -4) != bufSize) {
            Slog.w(TAG, "File corrupt: history data buffer not aligned " + bufSize);
        } else {
            this.mHistoryBuffer.appendFrom(in, curPos, bufSize);
            in.setDataPosition(curPos + bufSize);
        }
        if (andOldHistory) {
            readOldHistory(in);
        }
        this.mHistoryBaseTime = historyBaseTime;
        if (this.mHistoryBaseTime > 0) {
            this.mHistoryBaseTime = (this.mHistoryBaseTime - SystemClock.elapsedRealtime()) + 1;
        }
    }

    void readOldHistory(Parcel in) {
    }

    void writeHistory(Parcel out, boolean inclData, boolean andOldHistory) {
        out.writeLong(this.mHistoryBaseTime + this.mLastHistoryElapsedRealtime);
        if (inclData) {
            out.writeInt(this.mHistoryTagPool.size());
            for (Map.Entry<HistoryTag, Integer> ent : this.mHistoryTagPool.entrySet()) {
                HistoryTag tag = (HistoryTag) ent.getKey();
                out.writeInt(((Integer) ent.getValue()).intValue());
                out.writeString(tag.string);
                out.writeInt(tag.uid);
            }
            out.writeInt(this.mHistoryBuffer.dataSize());
            out.appendFrom(this.mHistoryBuffer, BATTERY_PLUGGED_NONE, this.mHistoryBuffer.dataSize());
            if (andOldHistory) {
                writeOldHistory(out);
                return;
            }
            return;
        }
        out.writeInt(BATTERY_PLUGGED_NONE);
        out.writeInt(BATTERY_PLUGGED_NONE);
    }

    void writeOldHistory(Parcel out) {
    }

    public void readSummaryFromParcel(Parcel in) {
        int version = in.readInt();
        if (version != VERSION) {
            Slog.w("BatteryStats", "readFromParcel: version got " + version + ", expected " + VERSION + "; erasing old stats");
            return;
        }
        int i;
        readHistory(in, true);
        this.mStartCount = in.readInt();
        this.mUptime = in.readLong();
        this.mRealtime = in.readLong();
        this.mStartClockTime = in.readLong();
        this.mStartPlatformVersion = in.readString();
        this.mEndPlatformVersion = in.readString();
        this.mOnBatteryTimeBase.readSummaryFromParcel(in);
        this.mOnBatteryScreenOffTimeBase.readSummaryFromParcel(in);
        this.mDischargeUnplugLevel = in.readInt();
        this.mDischargePlugLevel = in.readInt();
        this.mDischargeCurrentLevel = in.readInt();
        this.mCurrentBatteryLevel = in.readInt();
        this.mLowDischargeAmountSinceCharge = in.readInt();
        this.mHighDischargeAmountSinceCharge = in.readInt();
        this.mDischargeAmountScreenOnSinceCharge = in.readInt();
        this.mDischargeAmountScreenOffSinceCharge = in.readInt();
        this.mNumDischargeStepDurations = in.readInt();
        in.readLongArray(this.mDischargeStepDurations);
        this.mNumChargeStepDurations = in.readInt();
        in.readLongArray(this.mChargeStepDurations);
        this.mStartCount += NET_UPDATE_MOBILE;
        this.mScreenState = BATTERY_PLUGGED_NONE;
        this.mScreenOnTimer.readSummaryFromParcelLocked(in);
        for (i = BATTERY_PLUGGED_NONE; i < 5; i += NET_UPDATE_MOBILE) {
            this.mScreenBrightnessTimer[i].readSummaryFromParcelLocked(in);
        }
        this.mInteractive = USE_OLD_HISTORY;
        this.mInteractiveTimer.readSummaryFromParcelLocked(in);
        this.mPhoneOn = USE_OLD_HISTORY;
        this.mLowPowerModeEnabledTimer.readSummaryFromParcelLocked(in);
        this.mPhoneOnTimer.readSummaryFromParcelLocked(in);
        for (i = BATTERY_PLUGGED_NONE; i < 5; i += NET_UPDATE_MOBILE) {
            this.mPhoneSignalStrengthsTimer[i].readSummaryFromParcelLocked(in);
        }
        this.mPhoneSignalScanningTimer.readSummaryFromParcelLocked(in);
        for (i = BATTERY_PLUGGED_NONE; i < 17; i += NET_UPDATE_MOBILE) {
            this.mPhoneDataConnectionsTimer[i].readSummaryFromParcelLocked(in);
        }
        for (i = BATTERY_PLUGGED_NONE; i < 4; i += NET_UPDATE_MOBILE) {
            this.mNetworkByteActivityCounters[i].readSummaryFromParcelLocked(in);
            this.mNetworkPacketActivityCounters[i].readSummaryFromParcelLocked(in);
        }
        this.mMobileRadioPowerState = DataConnectionRealTimeInfo.DC_POWER_STATE_LOW;
        this.mMobileRadioActiveTimer.readSummaryFromParcelLocked(in);
        this.mMobileRadioActivePerAppTimer.readSummaryFromParcelLocked(in);
        this.mMobileRadioActiveAdjustedTime.readSummaryFromParcelLocked(in);
        this.mMobileRadioActiveUnknownTime.readSummaryFromParcelLocked(in);
        this.mMobileRadioActiveUnknownCount.readSummaryFromParcelLocked(in);
        this.mWifiOn = USE_OLD_HISTORY;
        this.mWifiOnTimer.readSummaryFromParcelLocked(in);
        this.mGlobalWifiRunning = USE_OLD_HISTORY;
        this.mGlobalWifiRunningTimer.readSummaryFromParcelLocked(in);
        for (i = BATTERY_PLUGGED_NONE; i < 8; i += NET_UPDATE_MOBILE) {
            this.mWifiStateTimer[i].readSummaryFromParcelLocked(in);
        }
        for (i = BATTERY_PLUGGED_NONE; i < 13; i += NET_UPDATE_MOBILE) {
            this.mWifiSupplStateTimer[i].readSummaryFromParcelLocked(in);
        }
        for (i = BATTERY_PLUGGED_NONE; i < 5; i += NET_UPDATE_MOBILE) {
            this.mWifiSignalStrengthsTimer[i].readSummaryFromParcelLocked(in);
        }
        this.mBluetoothOn = USE_OLD_HISTORY;
        this.mBluetoothOnTimer.readSummaryFromParcelLocked(in);
        for (i = BATTERY_PLUGGED_NONE; i < 4; i += NET_UPDATE_MOBILE) {
            this.mBluetoothStateTimer[i].readSummaryFromParcelLocked(in);
        }
        int readInt = in.readInt();
        this.mLoadedNumConnectivityChange = readInt;
        this.mNumConnectivityChange = readInt;
        this.mFlashlightOn = USE_OLD_HISTORY;
        this.mFlashlightOnTimer.readSummaryFromParcelLocked(in);
        int NKW = in.readInt();
        if (NKW > 10000) {
            Slog.w(TAG, "File corrupt: too many kernel wake locks " + NKW);
            return;
        }
        for (int ikw = BATTERY_PLUGGED_NONE; ikw < NKW; ikw += NET_UPDATE_MOBILE) {
            if (in.readInt() != 0) {
                getKernelWakelockTimerLocked(in.readString()).readSummaryFromParcelLocked(in);
            }
        }
        int NWR = in.readInt();
        if (NWR > 10000) {
            Slog.w(TAG, "File corrupt: too many wakeup reasons " + NWR);
            return;
        }
        for (int iwr = BATTERY_PLUGGED_NONE; iwr < NWR; iwr += NET_UPDATE_MOBILE) {
            if (in.readInt() != 0) {
                getWakeupReasonTimerLocked(in.readString()).readSummaryFromParcelLocked(in);
            }
        }
        sNumSpeedSteps = in.readInt();
        if (sNumSpeedSteps < 0 || sNumSpeedSteps > MAX_WAKELOCKS_PER_UID) {
            throw new BadParcelableException("Bad speed steps in data: " + sNumSpeedSteps);
        }
        int NU = in.readInt();
        if (NU > 10000) {
            Slog.w(TAG, "File corrupt: too many uids " + NU);
            return;
        }
        for (int iu = BATTERY_PLUGGED_NONE; iu < NU; iu += NET_UPDATE_MOBILE) {
            int uid = in.readInt();
            Uid uid2 = new Uid(this, uid);
            this.mUidStats.put(uid, uid2);
            uid2.mWifiRunning = USE_OLD_HISTORY;
            if (in.readInt() != 0) {
                uid2.mWifiRunningTimer.readSummaryFromParcelLocked(in);
            }
            uid2.mFullWifiLockOut = USE_OLD_HISTORY;
            if (in.readInt() != 0) {
                uid2.mFullWifiLockTimer.readSummaryFromParcelLocked(in);
            }
            uid2.mWifiScanStarted = USE_OLD_HISTORY;
            if (in.readInt() != 0) {
                uid2.mWifiScanTimer.readSummaryFromParcelLocked(in);
            }
            uid2.mWifiBatchedScanBinStarted = -1;
            for (i = BATTERY_PLUGGED_NONE; i < 5; i += NET_UPDATE_MOBILE) {
                if (in.readInt() != 0) {
                    uid2.makeWifiBatchedScanBin(i, null);
                    uid2.mWifiBatchedScanTimer[i].readSummaryFromParcelLocked(in);
                }
            }
            uid2.mWifiMulticastEnabled = USE_OLD_HISTORY;
            if (in.readInt() != 0) {
                uid2.mWifiMulticastTimer.readSummaryFromParcelLocked(in);
            }
            if (in.readInt() != 0) {
                uid2.createAudioTurnedOnTimerLocked().readSummaryFromParcelLocked(in);
            }
            if (in.readInt() != 0) {
                uid2.createVideoTurnedOnTimerLocked().readSummaryFromParcelLocked(in);
            }
            if (in.readInt() != 0) {
                uid2.createForegroundActivityTimerLocked().readSummaryFromParcelLocked(in);
            }
            uid2.mProcessState = STATE_BATTERY_PLUG_MASK;
            for (i = BATTERY_PLUGGED_NONE; i < STATE_BATTERY_PLUG_MASK; i += NET_UPDATE_MOBILE) {
                if (in.readInt() != 0) {
                    uid2.makeProcessState(i, null);
                    uid2.mProcessStateTimer[i].readSummaryFromParcelLocked(in);
                }
            }
            if (in.readInt() != 0) {
                uid2.createVibratorOnTimerLocked().readSummaryFromParcelLocked(in);
            }
            if (in.readInt() != 0) {
                if (uid2.mUserActivityCounters == null) {
                    uid2.initUserActivityLocked();
                }
                for (i = BATTERY_PLUGGED_NONE; i < STATE_BATTERY_PLUG_MASK; i += NET_UPDATE_MOBILE) {
                    uid2.mUserActivityCounters[i].readSummaryFromParcelLocked(in);
                }
            }
            if (in.readInt() != 0) {
                if (uid2.mNetworkByteActivityCounters == null) {
                    uid2.initNetworkActivityLocked();
                }
                for (i = BATTERY_PLUGGED_NONE; i < 4; i += NET_UPDATE_MOBILE) {
                    uid2.mNetworkByteActivityCounters[i].readSummaryFromParcelLocked(in);
                    uid2.mNetworkPacketActivityCounters[i].readSummaryFromParcelLocked(in);
                }
                uid2.mMobileRadioActiveTime.readSummaryFromParcelLocked(in);
                uid2.mMobileRadioActiveCount.readSummaryFromParcelLocked(in);
            }
            int NW = in.readInt();
            if (NW > MAX_WAKELOCKS_PER_UID) {
                Slog.w(TAG, "File corrupt: too many wake locks " + NW);
                return;
            }
            for (int iw = BATTERY_PLUGGED_NONE; iw < NW; iw += NET_UPDATE_MOBILE) {
                uid2.readWakeSummaryFromParcelLocked(in.readString(), in);
            }
            int NS = in.readInt();
            if (NS > MAX_WAKELOCKS_PER_UID) {
                Slog.w(TAG, "File corrupt: too many syncs " + NS);
                return;
            }
            int is;
            for (is = BATTERY_PLUGGED_NONE; is < NS; is += NET_UPDATE_MOBILE) {
                uid2.readSyncSummaryFromParcelLocked(in.readString(), in);
            }
            int NJ = in.readInt();
            if (NJ > MAX_WAKELOCKS_PER_UID) {
                Slog.w(TAG, "File corrupt: too many job timers " + NJ);
                return;
            }
            for (int ij = BATTERY_PLUGGED_NONE; ij < NJ; ij += NET_UPDATE_MOBILE) {
                uid2.readJobSummaryFromParcelLocked(in.readString(), in);
            }
            int NP = in.readInt();
            if (NP > 1000) {
                Slog.w(TAG, "File corrupt: too many sensors " + NP);
                return;
            }
            for (is = BATTERY_PLUGGED_NONE; is < NP; is += NET_UPDATE_MOBILE) {
                int seNumber = in.readInt();
                if (in.readInt() != 0) {
                    uid2.getSensorTimerLocked(seNumber, true).readSummaryFromParcelLocked(in);
                }
            }
            NP = in.readInt();
            if (NP > 1000) {
                Slog.w(TAG, "File corrupt: too many processes " + NP);
                return;
            }
            int ip = BATTERY_PLUGGED_NONE;
            while (ip < NP) {
                Proc p = uid2.getProcessStatsLocked(in.readString());
                long readLong = in.readLong();
                p.mLoadedUserTime = readLong;
                p.mUserTime = readLong;
                readLong = in.readLong();
                p.mLoadedSystemTime = readLong;
                p.mSystemTime = readLong;
                readLong = in.readLong();
                p.mLoadedForegroundTime = readLong;
                p.mForegroundTime = readLong;
                readInt = in.readInt();
                p.mLoadedStarts = readInt;
                p.mStarts = readInt;
                readInt = in.readInt();
                p.mLoadedNumCrashes = readInt;
                p.mNumCrashes = readInt;
                readInt = in.readInt();
                p.mLoadedNumAnrs = readInt;
                p.mNumAnrs = readInt;
                int NSB = in.readInt();
                if (NSB > MAX_WAKELOCKS_PER_UID) {
                    Slog.w(TAG, "File corrupt: too many speed bins " + NSB);
                    return;
                }
                p.mSpeedBins = new SamplingCounter[NSB];
                for (i = BATTERY_PLUGGED_NONE; i < NSB; i += NET_UPDATE_MOBILE) {
                    if (in.readInt() != 0) {
                        p.mSpeedBins[i] = new SamplingCounter(this.mOnBatteryTimeBase);
                        p.mSpeedBins[i].readSummaryFromParcelLocked(in);
                    }
                }
                if (p.readExcessivePowerFromParcelLocked(in)) {
                    ip += NET_UPDATE_MOBILE;
                } else {
                    return;
                }
            }
            NP = in.readInt();
            if (NP > 10000) {
                Slog.w(TAG, "File corrupt: too many packages " + NP);
                return;
            }
            for (ip = BATTERY_PLUGGED_NONE; ip < NP; ip += NET_UPDATE_MOBILE) {
                String pkgName = in.readString();
                Pkg p2 = uid2.getPackageStatsLocked(pkgName);
                readInt = in.readInt();
                p2.mLoadedWakeups = readInt;
                p2.mWakeups = readInt;
                NS = in.readInt();
                if (NS > 1000) {
                    Slog.w(TAG, "File corrupt: too many services " + NS);
                    return;
                }
                for (is = BATTERY_PLUGGED_NONE; is < NS; is += NET_UPDATE_MOBILE) {
                    Serv s = uid2.getServiceStatsLocked(pkgName, in.readString());
                    readLong = in.readLong();
                    s.mLoadedStartTime = readLong;
                    s.mStartTime = readLong;
                    readInt = in.readInt();
                    s.mLoadedStarts = readInt;
                    s.mStarts = readInt;
                    readInt = in.readInt();
                    s.mLoadedLaunches = readInt;
                    s.mLaunches = readInt;
                }
            }
        }
    }

    public void writeSummaryToParcel(Parcel out, boolean inclHistory) {
        int i;
        pullPendingStateUpdatesLocked();
        long startClockTime = getStartClockTime();
        long NOW_SYS = SystemClock.uptimeMillis() * 1000;
        long NOWREAL_SYS = SystemClock.elapsedRealtime() * 1000;
        out.writeInt(VERSION);
        writeHistory(out, inclHistory, true);
        out.writeInt(this.mStartCount);
        out.writeLong(computeUptime(NOW_SYS, BATTERY_PLUGGED_NONE));
        out.writeLong(computeRealtime(NOWREAL_SYS, BATTERY_PLUGGED_NONE));
        out.writeLong(startClockTime);
        out.writeString(this.mStartPlatformVersion);
        out.writeString(this.mEndPlatformVersion);
        this.mOnBatteryTimeBase.writeSummaryToParcel(out, NOW_SYS, NOWREAL_SYS);
        this.mOnBatteryScreenOffTimeBase.writeSummaryToParcel(out, NOW_SYS, NOWREAL_SYS);
        out.writeInt(this.mDischargeUnplugLevel);
        out.writeInt(this.mDischargePlugLevel);
        out.writeInt(this.mDischargeCurrentLevel);
        out.writeInt(this.mCurrentBatteryLevel);
        out.writeInt(getLowDischargeAmountSinceCharge());
        out.writeInt(getHighDischargeAmountSinceCharge());
        out.writeInt(getDischargeAmountScreenOnSinceCharge());
        out.writeInt(getDischargeAmountScreenOffSinceCharge());
        out.writeInt(this.mNumDischargeStepDurations);
        out.writeLongArray(this.mDischargeStepDurations);
        out.writeInt(this.mNumChargeStepDurations);
        out.writeLongArray(this.mChargeStepDurations);
        this.mScreenOnTimer.writeSummaryFromParcelLocked(out, NOWREAL_SYS);
        for (i = BATTERY_PLUGGED_NONE; i < 5; i += NET_UPDATE_MOBILE) {
            this.mScreenBrightnessTimer[i].writeSummaryFromParcelLocked(out, NOWREAL_SYS);
        }
        this.mInteractiveTimer.writeSummaryFromParcelLocked(out, NOWREAL_SYS);
        this.mLowPowerModeEnabledTimer.writeSummaryFromParcelLocked(out, NOWREAL_SYS);
        this.mPhoneOnTimer.writeSummaryFromParcelLocked(out, NOWREAL_SYS);
        for (i = BATTERY_PLUGGED_NONE; i < 5; i += NET_UPDATE_MOBILE) {
            this.mPhoneSignalStrengthsTimer[i].writeSummaryFromParcelLocked(out, NOWREAL_SYS);
        }
        this.mPhoneSignalScanningTimer.writeSummaryFromParcelLocked(out, NOWREAL_SYS);
        for (i = BATTERY_PLUGGED_NONE; i < 17; i += NET_UPDATE_MOBILE) {
            this.mPhoneDataConnectionsTimer[i].writeSummaryFromParcelLocked(out, NOWREAL_SYS);
        }
        for (i = BATTERY_PLUGGED_NONE; i < 4; i += NET_UPDATE_MOBILE) {
            this.mNetworkByteActivityCounters[i].writeSummaryFromParcelLocked(out);
            this.mNetworkPacketActivityCounters[i].writeSummaryFromParcelLocked(out);
        }
        this.mMobileRadioActiveTimer.writeSummaryFromParcelLocked(out, NOWREAL_SYS);
        this.mMobileRadioActivePerAppTimer.writeSummaryFromParcelLocked(out, NOWREAL_SYS);
        this.mMobileRadioActiveAdjustedTime.writeSummaryFromParcelLocked(out);
        this.mMobileRadioActiveUnknownTime.writeSummaryFromParcelLocked(out);
        this.mMobileRadioActiveUnknownCount.writeSummaryFromParcelLocked(out);
        this.mWifiOnTimer.writeSummaryFromParcelLocked(out, NOWREAL_SYS);
        this.mGlobalWifiRunningTimer.writeSummaryFromParcelLocked(out, NOWREAL_SYS);
        for (i = BATTERY_PLUGGED_NONE; i < 8; i += NET_UPDATE_MOBILE) {
            this.mWifiStateTimer[i].writeSummaryFromParcelLocked(out, NOWREAL_SYS);
        }
        for (i = BATTERY_PLUGGED_NONE; i < 13; i += NET_UPDATE_MOBILE) {
            this.mWifiSupplStateTimer[i].writeSummaryFromParcelLocked(out, NOWREAL_SYS);
        }
        for (i = BATTERY_PLUGGED_NONE; i < 5; i += NET_UPDATE_MOBILE) {
            this.mWifiSignalStrengthsTimer[i].writeSummaryFromParcelLocked(out, NOWREAL_SYS);
        }
        this.mBluetoothOnTimer.writeSummaryFromParcelLocked(out, NOWREAL_SYS);
        for (i = BATTERY_PLUGGED_NONE; i < 4; i += NET_UPDATE_MOBILE) {
            this.mBluetoothStateTimer[i].writeSummaryFromParcelLocked(out, NOWREAL_SYS);
        }
        out.writeInt(this.mNumConnectivityChange);
        this.mFlashlightOnTimer.writeSummaryFromParcelLocked(out, NOWREAL_SYS);
        out.writeInt(this.mKernelWakelockStats.size());
        for (Map.Entry<String, SamplingTimer> ent : this.mKernelWakelockStats.entrySet()) {
            Timer kwlt = (Timer) ent.getValue();
            if (kwlt != null) {
                out.writeInt(NET_UPDATE_MOBILE);
                out.writeString((String) ent.getKey());
                kwlt.writeSummaryFromParcelLocked(out, NOWREAL_SYS);
            } else {
                out.writeInt(BATTERY_PLUGGED_NONE);
            }
        }
        out.writeInt(this.mWakeupReasonStats.size());
        for (Map.Entry<String, SamplingTimer> ent2 : this.mWakeupReasonStats.entrySet()) {
            SamplingTimer timer = (SamplingTimer) ent2.getValue();
            if (timer != null) {
                out.writeInt(NET_UPDATE_MOBILE);
                out.writeString((String) ent2.getKey());
                timer.writeSummaryFromParcelLocked(out, NOWREAL_SYS);
            } else {
                out.writeInt(BATTERY_PLUGGED_NONE);
            }
        }
        out.writeInt(sNumSpeedSteps);
        int NU = this.mUidStats.size();
        out.writeInt(NU);
        for (int iu = BATTERY_PLUGGED_NONE; iu < NU; iu += NET_UPDATE_MOBILE) {
            out.writeInt(this.mUidStats.keyAt(iu));
            Uid u = (Uid) this.mUidStats.valueAt(iu);
            if (u.mWifiRunningTimer != null) {
                out.writeInt(NET_UPDATE_MOBILE);
                u.mWifiRunningTimer.writeSummaryFromParcelLocked(out, NOWREAL_SYS);
            } else {
                out.writeInt(BATTERY_PLUGGED_NONE);
            }
            if (u.mFullWifiLockTimer != null) {
                out.writeInt(NET_UPDATE_MOBILE);
                u.mFullWifiLockTimer.writeSummaryFromParcelLocked(out, NOWREAL_SYS);
            } else {
                out.writeInt(BATTERY_PLUGGED_NONE);
            }
            if (u.mWifiScanTimer != null) {
                out.writeInt(NET_UPDATE_MOBILE);
                u.mWifiScanTimer.writeSummaryFromParcelLocked(out, NOWREAL_SYS);
            } else {
                out.writeInt(BATTERY_PLUGGED_NONE);
            }
            for (i = BATTERY_PLUGGED_NONE; i < 5; i += NET_UPDATE_MOBILE) {
                if (u.mWifiBatchedScanTimer[i] != null) {
                    out.writeInt(NET_UPDATE_MOBILE);
                    u.mWifiBatchedScanTimer[i].writeSummaryFromParcelLocked(out, NOWREAL_SYS);
                } else {
                    out.writeInt(BATTERY_PLUGGED_NONE);
                }
            }
            if (u.mWifiMulticastTimer != null) {
                out.writeInt(NET_UPDATE_MOBILE);
                u.mWifiMulticastTimer.writeSummaryFromParcelLocked(out, NOWREAL_SYS);
            } else {
                out.writeInt(BATTERY_PLUGGED_NONE);
            }
            if (u.mAudioTurnedOnTimer != null) {
                out.writeInt(NET_UPDATE_MOBILE);
                u.mAudioTurnedOnTimer.writeSummaryFromParcelLocked(out, NOWREAL_SYS);
            } else {
                out.writeInt(BATTERY_PLUGGED_NONE);
            }
            if (u.mVideoTurnedOnTimer != null) {
                out.writeInt(NET_UPDATE_MOBILE);
                u.mVideoTurnedOnTimer.writeSummaryFromParcelLocked(out, NOWREAL_SYS);
            } else {
                out.writeInt(BATTERY_PLUGGED_NONE);
            }
            if (u.mForegroundActivityTimer != null) {
                out.writeInt(NET_UPDATE_MOBILE);
                u.mForegroundActivityTimer.writeSummaryFromParcelLocked(out, NOWREAL_SYS);
            } else {
                out.writeInt(BATTERY_PLUGGED_NONE);
            }
            for (i = BATTERY_PLUGGED_NONE; i < STATE_BATTERY_PLUG_MASK; i += NET_UPDATE_MOBILE) {
                if (u.mProcessStateTimer[i] != null) {
                    out.writeInt(NET_UPDATE_MOBILE);
                    u.mProcessStateTimer[i].writeSummaryFromParcelLocked(out, NOWREAL_SYS);
                } else {
                    out.writeInt(BATTERY_PLUGGED_NONE);
                }
            }
            if (u.mVibratorOnTimer != null) {
                out.writeInt(NET_UPDATE_MOBILE);
                u.mVibratorOnTimer.writeSummaryFromParcelLocked(out, NOWREAL_SYS);
            } else {
                out.writeInt(BATTERY_PLUGGED_NONE);
            }
            if (u.mUserActivityCounters == null) {
                out.writeInt(BATTERY_PLUGGED_NONE);
            } else {
                out.writeInt(NET_UPDATE_MOBILE);
                for (i = BATTERY_PLUGGED_NONE; i < STATE_BATTERY_PLUG_MASK; i += NET_UPDATE_MOBILE) {
                    u.mUserActivityCounters[i].writeSummaryFromParcelLocked(out);
                }
            }
            if (u.mNetworkByteActivityCounters == null) {
                out.writeInt(BATTERY_PLUGGED_NONE);
            } else {
                out.writeInt(NET_UPDATE_MOBILE);
                for (i = BATTERY_PLUGGED_NONE; i < 4; i += NET_UPDATE_MOBILE) {
                    u.mNetworkByteActivityCounters[i].writeSummaryFromParcelLocked(out);
                    u.mNetworkPacketActivityCounters[i].writeSummaryFromParcelLocked(out);
                }
                u.mMobileRadioActiveTime.writeSummaryFromParcelLocked(out);
                u.mMobileRadioActiveCount.writeSummaryFromParcelLocked(out);
            }
            ArrayMap<String, Wakelock> wakeStats = u.mWakelockStats.getMap();
            int NW = wakeStats.size();
            out.writeInt(NW);
            for (int iw = BATTERY_PLUGGED_NONE; iw < NW; iw += NET_UPDATE_MOBILE) {
                out.writeString((String) wakeStats.keyAt(iw));
                Wakelock wl = (Wakelock) wakeStats.valueAt(iw);
                if (wl.mTimerFull != null) {
                    out.writeInt(NET_UPDATE_MOBILE);
                    wl.mTimerFull.writeSummaryFromParcelLocked(out, NOWREAL_SYS);
                } else {
                    out.writeInt(BATTERY_PLUGGED_NONE);
                }
                if (wl.mTimerPartial != null) {
                    out.writeInt(NET_UPDATE_MOBILE);
                    wl.mTimerPartial.writeSummaryFromParcelLocked(out, NOWREAL_SYS);
                } else {
                    out.writeInt(BATTERY_PLUGGED_NONE);
                }
                if (wl.mTimerWindow != null) {
                    out.writeInt(NET_UPDATE_MOBILE);
                    wl.mTimerWindow.writeSummaryFromParcelLocked(out, NOWREAL_SYS);
                } else {
                    out.writeInt(BATTERY_PLUGGED_NONE);
                }
            }
            ArrayMap<String, StopwatchTimer> syncStats = u.mSyncStats.getMap();
            int NS = syncStats.size();
            out.writeInt(NS);
            for (int is = BATTERY_PLUGGED_NONE; is < NS; is += NET_UPDATE_MOBILE) {
                out.writeString((String) syncStats.keyAt(is));
                ((StopwatchTimer) syncStats.valueAt(is)).writeSummaryFromParcelLocked(out, NOWREAL_SYS);
            }
            ArrayMap<String, StopwatchTimer> jobStats = u.mJobStats.getMap();
            int NJ = jobStats.size();
            out.writeInt(NJ);
            for (int ij = BATTERY_PLUGGED_NONE; ij < NJ; ij += NET_UPDATE_MOBILE) {
                out.writeString((String) jobStats.keyAt(ij));
                ((StopwatchTimer) jobStats.valueAt(ij)).writeSummaryFromParcelLocked(out, NOWREAL_SYS);
            }
            int NSE = u.mSensorStats.size();
            out.writeInt(NSE);
            for (int ise = BATTERY_PLUGGED_NONE; ise < NSE; ise += NET_UPDATE_MOBILE) {
                out.writeInt(u.mSensorStats.keyAt(ise));
                Sensor se = (Sensor) u.mSensorStats.valueAt(ise);
                if (se.mTimer != null) {
                    out.writeInt(NET_UPDATE_MOBILE);
                    se.mTimer.writeSummaryFromParcelLocked(out, NOWREAL_SYS);
                } else {
                    out.writeInt(BATTERY_PLUGGED_NONE);
                }
            }
            int NP = u.mProcessStats.size();
            out.writeInt(NP);
            for (int ip = BATTERY_PLUGGED_NONE; ip < NP; ip += NET_UPDATE_MOBILE) {
                out.writeString((String) u.mProcessStats.keyAt(ip));
                Proc ps = (Proc) u.mProcessStats.valueAt(ip);
                out.writeLong(ps.mUserTime);
                out.writeLong(ps.mSystemTime);
                out.writeLong(ps.mForegroundTime);
                out.writeInt(ps.mStarts);
                out.writeInt(ps.mNumCrashes);
                out.writeInt(ps.mNumAnrs);
                int N = ps.mSpeedBins.length;
                out.writeInt(N);
                for (i = BATTERY_PLUGGED_NONE; i < N; i += NET_UPDATE_MOBILE) {
                    if (ps.mSpeedBins[i] != null) {
                        out.writeInt(NET_UPDATE_MOBILE);
                        ps.mSpeedBins[i].writeSummaryFromParcelLocked(out);
                    } else {
                        out.writeInt(BATTERY_PLUGGED_NONE);
                    }
                }
                ps.writeExcessivePowerToParcelLocked(out);
            }
            NP = u.mPackageStats.size();
            out.writeInt(NP);
            if (NP > 0) {
                for (Map.Entry<String, Pkg> ent3 : u.mPackageStats.entrySet()) {
                    out.writeString((String) ent3.getKey());
                    Pkg ps2 = (Pkg) ent3.getValue();
                    out.writeInt(ps2.mWakeups);
                    NS = ps2.mServiceStats.size();
                    out.writeInt(NS);
                    if (NS > 0) {
                        for (Map.Entry<String, Serv> sent : ps2.mServiceStats.entrySet()) {
                            out.writeString((String) sent.getKey());
                            Serv ss = (Serv) sent.getValue();
                            out.writeLong(ss.getStartTimeToNowLocked(this.mOnBatteryTimeBase.getUptime(NOW_SYS)));
                            out.writeInt(ss.mStarts);
                            out.writeInt(ss.mLaunches);
                        }
                    }
                }
            }
        }
    }

    public void readFromParcel(Parcel in) {
        readFromParcelLocked(in);
    }

    void readFromParcelLocked(Parcel in) {
        int magic = in.readInt();
        if (magic != MAGIC) {
            throw new ParcelFormatException("Bad magic number: #" + Integer.toHexString(magic));
        }
        int i;
        readHistory(in, USE_OLD_HISTORY);
        this.mStartCount = in.readInt();
        this.mStartClockTime = in.readLong();
        this.mStartPlatformVersion = in.readString();
        this.mEndPlatformVersion = in.readString();
        this.mUptime = in.readLong();
        this.mUptimeStart = in.readLong();
        this.mRealtime = in.readLong();
        this.mRealtimeStart = in.readLong();
        this.mOnBattery = in.readInt() != 0 ? true : USE_OLD_HISTORY;
        this.mOnBatteryInternal = USE_OLD_HISTORY;
        this.mOnBatteryTimeBase.readFromParcel(in);
        this.mOnBatteryScreenOffTimeBase.readFromParcel(in);
        this.mScreenState = BATTERY_PLUGGED_NONE;
        this.mScreenOnTimer = new StopwatchTimer(null, -1, null, this.mOnBatteryTimeBase, in);
        for (i = BATTERY_PLUGGED_NONE; i < 5; i += NET_UPDATE_MOBILE) {
            this.mScreenBrightnessTimer[i] = new StopwatchTimer(null, -100 - i, null, this.mOnBatteryTimeBase, in);
        }
        this.mInteractive = USE_OLD_HISTORY;
        this.mInteractiveTimer = new StopwatchTimer(null, -9, null, this.mOnBatteryTimeBase, in);
        this.mPhoneOn = USE_OLD_HISTORY;
        this.mLowPowerModeEnabledTimer = new StopwatchTimer(null, -2, null, this.mOnBatteryTimeBase, in);
        this.mPhoneOnTimer = new StopwatchTimer(null, -3, null, this.mOnBatteryTimeBase, in);
        for (i = BATTERY_PLUGGED_NONE; i < 5; i += NET_UPDATE_MOBILE) {
            this.mPhoneSignalStrengthsTimer[i] = new StopwatchTimer(null, -200 - i, null, this.mOnBatteryTimeBase, in);
        }
        this.mPhoneSignalScanningTimer = new StopwatchTimer(null, -199, null, this.mOnBatteryTimeBase, in);
        for (i = BATTERY_PLUGGED_NONE; i < 17; i += NET_UPDATE_MOBILE) {
            this.mPhoneDataConnectionsTimer[i] = new StopwatchTimer(null, -300 - i, null, this.mOnBatteryTimeBase, in);
        }
        for (i = BATTERY_PLUGGED_NONE; i < 4; i += NET_UPDATE_MOBILE) {
            this.mNetworkByteActivityCounters[i] = new LongSamplingCounter(this.mOnBatteryTimeBase, in);
            this.mNetworkPacketActivityCounters[i] = new LongSamplingCounter(this.mOnBatteryTimeBase, in);
        }
        this.mMobileRadioPowerState = DataConnectionRealTimeInfo.DC_POWER_STATE_LOW;
        this.mMobileRadioActiveTimer = new StopwatchTimer(null, -400, null, this.mOnBatteryTimeBase, in);
        this.mMobileRadioActivePerAppTimer = new StopwatchTimer(null, -401, null, this.mOnBatteryTimeBase, in);
        this.mMobileRadioActiveAdjustedTime = new LongSamplingCounter(this.mOnBatteryTimeBase, in);
        this.mMobileRadioActiveUnknownTime = new LongSamplingCounter(this.mOnBatteryTimeBase, in);
        this.mMobileRadioActiveUnknownCount = new LongSamplingCounter(this.mOnBatteryTimeBase, in);
        this.mWifiOn = USE_OLD_HISTORY;
        this.mWifiOnTimer = new StopwatchTimer(null, -4, null, this.mOnBatteryTimeBase, in);
        this.mGlobalWifiRunning = USE_OLD_HISTORY;
        this.mGlobalWifiRunningTimer = new StopwatchTimer(null, -5, null, this.mOnBatteryTimeBase, in);
        for (i = BATTERY_PLUGGED_NONE; i < 8; i += NET_UPDATE_MOBILE) {
            this.mWifiStateTimer[i] = new StopwatchTimer(null, -600 - i, null, this.mOnBatteryTimeBase, in);
        }
        for (i = BATTERY_PLUGGED_NONE; i < 13; i += NET_UPDATE_MOBILE) {
            this.mWifiSupplStateTimer[i] = new StopwatchTimer(null, -700 - i, null, this.mOnBatteryTimeBase, in);
        }
        for (i = BATTERY_PLUGGED_NONE; i < 5; i += NET_UPDATE_MOBILE) {
            this.mWifiSignalStrengthsTimer[i] = new StopwatchTimer(null, -800 - i, null, this.mOnBatteryTimeBase, in);
        }
        this.mBluetoothOn = USE_OLD_HISTORY;
        this.mBluetoothOnTimer = new StopwatchTimer(null, -6, null, this.mOnBatteryTimeBase, in);
        for (i = BATTERY_PLUGGED_NONE; i < 4; i += NET_UPDATE_MOBILE) {
            this.mBluetoothStateTimer[i] = new StopwatchTimer(null, -500 - i, null, this.mOnBatteryTimeBase, in);
        }
        this.mNumConnectivityChange = in.readInt();
        this.mLoadedNumConnectivityChange = in.readInt();
        this.mUnpluggedNumConnectivityChange = in.readInt();
        this.mAudioOnNesting = BATTERY_PLUGGED_NONE;
        this.mAudioOnTimer = new StopwatchTimer(null, -7, null, this.mOnBatteryTimeBase);
        this.mVideoOnNesting = BATTERY_PLUGGED_NONE;
        this.mVideoOnTimer = new StopwatchTimer(null, -8, null, this.mOnBatteryTimeBase);
        this.mFlashlightOn = USE_OLD_HISTORY;
        this.mFlashlightOnTimer = new StopwatchTimer(null, -9, null, this.mOnBatteryTimeBase, in);
        this.mDischargeUnplugLevel = in.readInt();
        this.mDischargePlugLevel = in.readInt();
        this.mDischargeCurrentLevel = in.readInt();
        this.mCurrentBatteryLevel = in.readInt();
        this.mLowDischargeAmountSinceCharge = in.readInt();
        this.mHighDischargeAmountSinceCharge = in.readInt();
        this.mDischargeAmountScreenOn = in.readInt();
        this.mDischargeAmountScreenOnSinceCharge = in.readInt();
        this.mDischargeAmountScreenOff = in.readInt();
        this.mDischargeAmountScreenOffSinceCharge = in.readInt();
        this.mNumDischargeStepDurations = in.readInt();
        in.readLongArray(this.mDischargeStepDurations);
        this.mNumChargeStepDurations = in.readInt();
        in.readLongArray(this.mChargeStepDurations);
        this.mLastWriteTime = in.readLong();
        this.mBluetoothPingCount = in.readInt();
        this.mBluetoothPingStart = -1;
        this.mKernelWakelockStats.clear();
        int NKW = in.readInt();
        for (int ikw = BATTERY_PLUGGED_NONE; ikw < NKW; ikw += NET_UPDATE_MOBILE) {
            if (in.readInt() != 0) {
                this.mKernelWakelockStats.put(in.readString(), new SamplingTimer(this.mOnBatteryScreenOffTimeBase, in));
            }
        }
        this.mWakeupReasonStats.clear();
        int NWR = in.readInt();
        for (int iwr = BATTERY_PLUGGED_NONE; iwr < NWR; iwr += NET_UPDATE_MOBILE) {
            if (in.readInt() != 0) {
                this.mWakeupReasonStats.put(in.readString(), new SamplingTimer(this.mOnBatteryTimeBase, in));
            }
        }
        this.mPartialTimers.clear();
        this.mFullTimers.clear();
        this.mWindowTimers.clear();
        this.mWifiRunningTimers.clear();
        this.mFullWifiLockTimers.clear();
        this.mWifiScanTimers.clear();
        this.mWifiBatchedScanTimers.clear();
        this.mWifiMulticastTimers.clear();
        this.mAudioTurnedOnTimers.clear();
        this.mVideoTurnedOnTimers.clear();
        sNumSpeedSteps = in.readInt();
        int numUids = in.readInt();
        this.mUidStats.clear();
        for (i = BATTERY_PLUGGED_NONE; i < numUids; i += NET_UPDATE_MOBILE) {
            int uid = in.readInt();
            Uid uid2 = new Uid(this, uid);
            uid2.readFromParcelLocked(this.mOnBatteryTimeBase, this.mOnBatteryScreenOffTimeBase, in);
            this.mUidStats.append(uid, uid2);
        }
    }

    public void writeToParcel(Parcel out, int flags) {
        writeToParcelLocked(out, true, flags);
    }

    public void writeToParcelWithoutUids(Parcel out, int flags) {
        writeToParcelLocked(out, USE_OLD_HISTORY, flags);
    }

    void writeToParcelLocked(Parcel out, boolean inclUids, int flags) {
        int i;
        pullPendingStateUpdatesLocked();
        long startClockTime = getStartClockTime();
        long uSecUptime = SystemClock.uptimeMillis() * 1000;
        long uSecRealtime = SystemClock.elapsedRealtime() * 1000;
        long batteryRealtime = this.mOnBatteryTimeBase.getRealtime(uSecRealtime);
        long batteryScreenOffRealtime = this.mOnBatteryScreenOffTimeBase.getRealtime(uSecRealtime);
        out.writeInt(MAGIC);
        writeHistory(out, true, USE_OLD_HISTORY);
        out.writeInt(this.mStartCount);
        out.writeLong(startClockTime);
        out.writeString(this.mStartPlatformVersion);
        out.writeString(this.mEndPlatformVersion);
        out.writeLong(this.mUptime);
        out.writeLong(this.mUptimeStart);
        out.writeLong(this.mRealtime);
        out.writeLong(this.mRealtimeStart);
        out.writeInt(this.mOnBattery ? NET_UPDATE_MOBILE : BATTERY_PLUGGED_NONE);
        this.mOnBatteryTimeBase.writeToParcel(out, uSecUptime, uSecRealtime);
        this.mOnBatteryScreenOffTimeBase.writeToParcel(out, uSecUptime, uSecRealtime);
        this.mScreenOnTimer.writeToParcel(out, uSecRealtime);
        for (i = BATTERY_PLUGGED_NONE; i < 5; i += NET_UPDATE_MOBILE) {
            this.mScreenBrightnessTimer[i].writeToParcel(out, uSecRealtime);
        }
        this.mInteractiveTimer.writeToParcel(out, uSecRealtime);
        this.mLowPowerModeEnabledTimer.writeToParcel(out, uSecRealtime);
        this.mPhoneOnTimer.writeToParcel(out, uSecRealtime);
        for (i = BATTERY_PLUGGED_NONE; i < 5; i += NET_UPDATE_MOBILE) {
            this.mPhoneSignalStrengthsTimer[i].writeToParcel(out, uSecRealtime);
        }
        this.mPhoneSignalScanningTimer.writeToParcel(out, uSecRealtime);
        for (i = BATTERY_PLUGGED_NONE; i < 17; i += NET_UPDATE_MOBILE) {
            this.mPhoneDataConnectionsTimer[i].writeToParcel(out, uSecRealtime);
        }
        for (i = BATTERY_PLUGGED_NONE; i < 4; i += NET_UPDATE_MOBILE) {
            this.mNetworkByteActivityCounters[i].writeToParcel(out);
            this.mNetworkPacketActivityCounters[i].writeToParcel(out);
        }
        this.mMobileRadioActiveTimer.writeToParcel(out, uSecRealtime);
        this.mMobileRadioActivePerAppTimer.writeToParcel(out, uSecRealtime);
        this.mMobileRadioActiveAdjustedTime.writeToParcel(out);
        this.mMobileRadioActiveUnknownTime.writeToParcel(out);
        this.mMobileRadioActiveUnknownCount.writeToParcel(out);
        this.mWifiOnTimer.writeToParcel(out, uSecRealtime);
        this.mGlobalWifiRunningTimer.writeToParcel(out, uSecRealtime);
        for (i = BATTERY_PLUGGED_NONE; i < 8; i += NET_UPDATE_MOBILE) {
            this.mWifiStateTimer[i].writeToParcel(out, uSecRealtime);
        }
        for (i = BATTERY_PLUGGED_NONE; i < 13; i += NET_UPDATE_MOBILE) {
            this.mWifiSupplStateTimer[i].writeToParcel(out, uSecRealtime);
        }
        for (i = BATTERY_PLUGGED_NONE; i < 5; i += NET_UPDATE_MOBILE) {
            this.mWifiSignalStrengthsTimer[i].writeToParcel(out, uSecRealtime);
        }
        this.mBluetoothOnTimer.writeToParcel(out, uSecRealtime);
        for (i = BATTERY_PLUGGED_NONE; i < 4; i += NET_UPDATE_MOBILE) {
            this.mBluetoothStateTimer[i].writeToParcel(out, uSecRealtime);
        }
        out.writeInt(this.mNumConnectivityChange);
        out.writeInt(this.mLoadedNumConnectivityChange);
        out.writeInt(this.mUnpluggedNumConnectivityChange);
        this.mFlashlightOnTimer.writeToParcel(out, uSecRealtime);
        out.writeInt(this.mDischargeUnplugLevel);
        out.writeInt(this.mDischargePlugLevel);
        out.writeInt(this.mDischargeCurrentLevel);
        out.writeInt(this.mCurrentBatteryLevel);
        out.writeInt(this.mLowDischargeAmountSinceCharge);
        out.writeInt(this.mHighDischargeAmountSinceCharge);
        out.writeInt(this.mDischargeAmountScreenOn);
        out.writeInt(this.mDischargeAmountScreenOnSinceCharge);
        out.writeInt(this.mDischargeAmountScreenOff);
        out.writeInt(this.mDischargeAmountScreenOffSinceCharge);
        out.writeInt(this.mNumDischargeStepDurations);
        out.writeLongArray(this.mDischargeStepDurations);
        out.writeInt(this.mNumChargeStepDurations);
        out.writeLongArray(this.mChargeStepDurations);
        out.writeLong(this.mLastWriteTime);
        out.writeInt(getBluetoothPingCount());
        if (inclUids) {
            out.writeInt(this.mKernelWakelockStats.size());
            for (Map.Entry<String, SamplingTimer> ent : this.mKernelWakelockStats.entrySet()) {
                SamplingTimer kwlt = (SamplingTimer) ent.getValue();
                if (kwlt != null) {
                    out.writeInt(NET_UPDATE_MOBILE);
                    out.writeString((String) ent.getKey());
                    kwlt.writeToParcel(out, uSecRealtime);
                } else {
                    out.writeInt(BATTERY_PLUGGED_NONE);
                }
            }
            out.writeInt(this.mWakeupReasonStats.size());
            for (Map.Entry<String, SamplingTimer> ent2 : this.mWakeupReasonStats.entrySet()) {
                SamplingTimer timer = (SamplingTimer) ent2.getValue();
                if (timer != null) {
                    out.writeInt(NET_UPDATE_MOBILE);
                    out.writeString((String) ent2.getKey());
                    timer.writeToParcel(out, uSecRealtime);
                } else {
                    out.writeInt(BATTERY_PLUGGED_NONE);
                }
            }
        } else {
            out.writeInt(BATTERY_PLUGGED_NONE);
        }
        out.writeInt(sNumSpeedSteps);
        if (inclUids) {
            int size = this.mUidStats.size();
            out.writeInt(size);
            for (i = BATTERY_PLUGGED_NONE; i < size; i += NET_UPDATE_MOBILE) {
                out.writeInt(this.mUidStats.keyAt(i));
                ((Uid) this.mUidStats.valueAt(i)).writeToParcelLocked(out, uSecRealtime);
            }
            return;
        }
        out.writeInt(BATTERY_PLUGGED_NONE);
    }

    public void prepareForDumpLocked() {
        pullPendingStateUpdatesLocked();
        getStartClockTime();
    }

    public void dumpLocked(Context context, PrintWriter pw, int flags, int reqUid, long histStart) {
        super.dumpLocked(context, pw, flags, reqUid, histStart);
    }
}
