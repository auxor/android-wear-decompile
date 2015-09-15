package com.android.internal.app;

import android.media.RemoteControlClient;
import android.media.session.PlaybackState;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import com.android.internal.R;
import com.android.internal.content.NativeLibraryHelper;
import com.android.internal.telephony.RILConstants;
import com.android.internal.util.GrowingArrayUtils;
import com.android.internal.util.Protocol;
import com.android.internal.widget.LockPatternUtils;
import dalvik.system.VMRuntime;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import javax.microedition.khronos.opengles.GL10;
import libcore.util.EmptyArray;

public final class ProcessStats implements Parcelable {
    public static final int ADJ_COUNT = 8;
    public static final int ADJ_MEM_FACTOR_COUNT = 4;
    public static final int ADJ_MEM_FACTOR_CRITICAL = 3;
    public static final int ADJ_MEM_FACTOR_LOW = 2;
    public static final int ADJ_MEM_FACTOR_MODERATE = 1;
    public static final int ADJ_MEM_FACTOR_NORMAL = 0;
    public static final String[] ADJ_MEM_NAMES_CSV = null;
    static final String[] ADJ_MEM_TAGS = null;
    public static final int ADJ_NOTHING = -1;
    public static final int ADJ_SCREEN_MOD = 4;
    public static final String[] ADJ_SCREEN_NAMES_CSV = null;
    public static final int ADJ_SCREEN_OFF = 0;
    public static final int ADJ_SCREEN_ON = 4;
    static final String[] ADJ_SCREEN_TAGS = null;
    public static final int[] ALL_MEM_ADJ = null;
    public static final int[] ALL_PROC_STATES = null;
    public static final int[] ALL_SCREEN_ADJ = null;
    public static final int[] BACKGROUND_PROC_STATES = null;
    static final int[] BAD_TABLE = null;
    public static long COMMIT_PERIOD = 0;
    public static long COMMIT_UPTIME_PERIOD = 0;
    public static final Creator<ProcessStats> CREATOR = null;
    static final String CSV_SEP = "\t";
    static final boolean DEBUG = false;
    static final boolean DEBUG_PARCEL = false;
    public static final int FLAG_COMPLETE = 1;
    public static final int FLAG_SHUTDOWN = 2;
    public static final int FLAG_SYSPROPS = 4;
    static final int LONGS_SIZE = 4096;
    private static final int MAGIC = 1347638355;
    public static final int[] NON_CACHED_PROC_STATES = null;
    static int OFFSET_ARRAY_MASK = 0;
    static int OFFSET_ARRAY_SHIFT = 0;
    static int OFFSET_INDEX_MASK = 0;
    static int OFFSET_INDEX_SHIFT = 0;
    static int OFFSET_TYPE_MASK = 0;
    static int OFFSET_TYPE_SHIFT = 0;
    private static final int PARCEL_VERSION = 18;
    static final int[] PROCESS_STATE_TO_STATE = null;
    public static final int PSS_AVERAGE = 2;
    public static final int PSS_COUNT = 7;
    public static final int PSS_MAXIMUM = 3;
    public static final int PSS_MINIMUM = 1;
    public static final int PSS_SAMPLE_COUNT = 0;
    public static final int PSS_USS_AVERAGE = 5;
    public static final int PSS_USS_MAXIMUM = 6;
    public static final int PSS_USS_MINIMUM = 4;
    public static final String SERVICE_NAME = "procstats";
    public static final int STATE_BACKUP = 4;
    public static final int STATE_CACHED_ACTIVITY = 11;
    public static final int STATE_CACHED_ACTIVITY_CLIENT = 12;
    public static final int STATE_CACHED_EMPTY = 13;
    public static final int STATE_COUNT = 14;
    public static final int STATE_HEAVY_WEIGHT = 5;
    public static final int STATE_HOME = 9;
    public static final int STATE_IMPORTANT_BACKGROUND = 3;
    public static final int STATE_IMPORTANT_FOREGROUND = 2;
    public static final int STATE_LAST_ACTIVITY = 10;
    static final String[] STATE_NAMES = null;
    public static final String[] STATE_NAMES_CSV = null;
    public static final int STATE_NOTHING = -1;
    public static final int STATE_PERSISTENT = 0;
    public static final int STATE_RECEIVER = 8;
    public static final int STATE_SERVICE = 6;
    public static final int STATE_SERVICE_RESTARTING = 7;
    static final String[] STATE_TAGS = null;
    public static final int STATE_TOP = 1;
    public static final int SYS_MEM_USAGE_CACHED_AVERAGE = 2;
    public static final int SYS_MEM_USAGE_CACHED_MAXIMUM = 3;
    public static final int SYS_MEM_USAGE_CACHED_MINIMUM = 1;
    public static final int SYS_MEM_USAGE_COUNT = 16;
    public static final int SYS_MEM_USAGE_FREE_AVERAGE = 5;
    public static final int SYS_MEM_USAGE_FREE_MAXIMUM = 6;
    public static final int SYS_MEM_USAGE_FREE_MINIMUM = 4;
    public static final int SYS_MEM_USAGE_KERNEL_AVERAGE = 11;
    public static final int SYS_MEM_USAGE_KERNEL_MAXIMUM = 12;
    public static final int SYS_MEM_USAGE_KERNEL_MINIMUM = 10;
    public static final int SYS_MEM_USAGE_NATIVE_AVERAGE = 14;
    public static final int SYS_MEM_USAGE_NATIVE_MAXIMUM = 15;
    public static final int SYS_MEM_USAGE_NATIVE_MINIMUM = 13;
    public static final int SYS_MEM_USAGE_SAMPLE_COUNT = 0;
    public static final int SYS_MEM_USAGE_ZRAM_AVERAGE = 8;
    public static final int SYS_MEM_USAGE_ZRAM_MAXIMUM = 9;
    public static final int SYS_MEM_USAGE_ZRAM_MINIMUM = 7;
    static final String TAG = "ProcessStats";
    int[] mAddLongTable;
    int mAddLongTableSize;
    ArrayMap<String, Integer> mCommonStringToIndex;
    public int mFlags;
    ArrayList<String> mIndexToCommonString;
    final ArrayList<long[]> mLongs;
    public int mMemFactor;
    public final long[] mMemFactorDurations;
    int mNextLong;
    public final ProcessMap<SparseArray<PackageState>> mPackages;
    public final ProcessMap<ProcessState> mProcesses;
    public String mReadError;
    boolean mRunning;
    String mRuntime;
    public long mStartTime;
    public final long[] mSysMemUsageArgs;
    public int[] mSysMemUsageTable;
    public int mSysMemUsageTableSize;
    public long mTimePeriodEndRealtime;
    public long mTimePeriodEndUptime;
    public long mTimePeriodStartClock;
    public String mTimePeriodStartClockStr;
    public long mTimePeriodStartRealtime;
    public long mTimePeriodStartUptime;

    /* renamed from: com.android.internal.app.ProcessStats.2 */
    class AnonymousClass2 implements Comparator<ProcessState> {
        final /* synthetic */ ProcessStats this$0;

        AnonymousClass2(com.android.internal.app.ProcessStats r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.app.ProcessStats.2.<init>(com.android.internal.app.ProcessStats):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.app.ProcessStats.2.<init>(com.android.internal.app.ProcessStats):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.app.ProcessStats.2.<init>(com.android.internal.app.ProcessStats):void");
        }

        public int compare(com.android.internal.app.ProcessStats.ProcessState r1, com.android.internal.app.ProcessStats.ProcessState r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.app.ProcessStats.2.compare(com.android.internal.app.ProcessStats$ProcessState, com.android.internal.app.ProcessStats$ProcessState):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.app.ProcessStats.2.compare(com.android.internal.app.ProcessStats$ProcessState, com.android.internal.app.ProcessStats$ProcessState):int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e4
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.app.ProcessStats.2.compare(com.android.internal.app.ProcessStats$ProcessState, com.android.internal.app.ProcessStats$ProcessState):int");
        }

        public /* bridge */ /* synthetic */ int compare(java.lang.Object r1, java.lang.Object r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.app.ProcessStats.2.compare(java.lang.Object, java.lang.Object):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.app.ProcessStats.2.compare(java.lang.Object, java.lang.Object):int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.app.ProcessStats.2.compare(java.lang.Object, java.lang.Object):int");
        }
    }

    public static class DurationsTable {
        public int[] mDurationsTable;
        public int mDurationsTableSize;
        public final String mName;
        public final ProcessStats mStats;

        public DurationsTable(ProcessStats stats, String name) {
            this.mStats = stats;
            this.mName = name;
        }

        void copyDurationsTo(DurationsTable other) {
            if (this.mDurationsTable != null) {
                this.mStats.mAddLongTable = new int[this.mDurationsTable.length];
                this.mStats.mAddLongTableSize = ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT;
                for (int i = ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT; i < this.mDurationsTableSize; i += ProcessStats.SYS_MEM_USAGE_CACHED_MINIMUM) {
                    int origEnt = this.mDurationsTable[i];
                    int type = (origEnt >> ProcessStats.OFFSET_TYPE_SHIFT) & ProcessStats.OFFSET_TYPE_MASK;
                    int newOff = this.mStats.addLongData(i, type, ProcessStats.SYS_MEM_USAGE_CACHED_MINIMUM);
                    this.mStats.mAddLongTable[i] = newOff | type;
                    this.mStats.setLong(newOff, ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT, this.mStats.getLong(origEnt, ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT));
                }
                other.mDurationsTable = this.mStats.mAddLongTable;
                other.mDurationsTableSize = this.mStats.mAddLongTableSize;
                return;
            }
            other.mDurationsTable = null;
            other.mDurationsTableSize = ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT;
        }

        void addDurations(DurationsTable other) {
            for (int i = ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT; i < other.mDurationsTableSize; i += ProcessStats.SYS_MEM_USAGE_CACHED_MINIMUM) {
                int ent = other.mDurationsTable[i];
                addDuration((ent >> ProcessStats.OFFSET_TYPE_SHIFT) & ProcessStats.OFFSET_TYPE_MASK, other.mStats.getLong(ent, ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT));
            }
        }

        void resetDurationsSafely() {
            this.mDurationsTable = null;
            this.mDurationsTableSize = ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT;
        }

        void writeDurationsToParcel(Parcel out) {
            out.writeInt(this.mDurationsTableSize);
            for (int i = ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT; i < this.mDurationsTableSize; i += ProcessStats.SYS_MEM_USAGE_CACHED_MINIMUM) {
                out.writeInt(this.mDurationsTable[i]);
            }
        }

        boolean readDurationsFromParcel(Parcel in) {
            int i = ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT;
            this.mDurationsTable = this.mStats.readTableFromParcel(in, this.mName, "durations");
            if (this.mDurationsTable == ProcessStats.BAD_TABLE) {
                return ProcessStats.DEBUG_PARCEL;
            }
            if (this.mDurationsTable != null) {
                i = this.mDurationsTable.length;
            }
            this.mDurationsTableSize = i;
            return true;
        }

        void addDuration(int state, long dur) {
            int off;
            int idx = ProcessStats.binarySearch(this.mDurationsTable, this.mDurationsTableSize, state);
            if (idx >= 0) {
                off = this.mDurationsTable[idx];
            } else {
                this.mStats.mAddLongTable = this.mDurationsTable;
                this.mStats.mAddLongTableSize = this.mDurationsTableSize;
                off = this.mStats.addLongData(idx ^ ProcessStats.STATE_NOTHING, state, ProcessStats.SYS_MEM_USAGE_CACHED_MINIMUM);
                this.mDurationsTable = this.mStats.mAddLongTable;
                this.mDurationsTableSize = this.mStats.mAddLongTableSize;
            }
            long[] longs = (long[]) this.mStats.mLongs.get((off >> ProcessStats.OFFSET_ARRAY_SHIFT) & ProcessStats.OFFSET_ARRAY_MASK);
            int i = (off >> ProcessStats.OFFSET_INDEX_SHIFT) & ProcessStats.OFFSET_INDEX_MASK;
            longs[i] = longs[i] + dur;
        }

        long getDuration(int state, long now) {
            int idx = ProcessStats.binarySearch(this.mDurationsTable, this.mDurationsTableSize, state);
            return idx >= 0 ? this.mStats.getLong(this.mDurationsTable[idx], ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT) : 0;
        }
    }

    public static final class PackageState {
        public final String mPackageName;
        public final ArrayMap<String, ProcessState> mProcesses;
        public final ArrayMap<String, ServiceState> mServices;
        public final int mUid;

        public PackageState(String packageName, int uid) {
            this.mProcesses = new ArrayMap();
            this.mServices = new ArrayMap();
            this.mUid = uid;
            this.mPackageName = packageName;
        }
    }

    public static final class ProcessDataCollection {
        public long avgPss;
        public long avgUss;
        public long maxPss;
        public long maxUss;
        final int[] memStates;
        public long minPss;
        public long minUss;
        public long numPss;
        final int[] procStates;
        final int[] screenStates;
        public long totalTime;

        public ProcessDataCollection(int[] r1, int[] r2, int[] r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.app.ProcessStats.ProcessDataCollection.<init>(int[], int[], int[]):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.app.ProcessStats.ProcessDataCollection.<init>(int[], int[], int[]):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.app.ProcessStats.ProcessDataCollection.<init>(int[], int[], int[]):void");
        }

        void print(java.io.PrintWriter r1, long r2, boolean r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.app.ProcessStats.ProcessDataCollection.print(java.io.PrintWriter, long, boolean):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.app.ProcessStats.ProcessDataCollection.print(java.io.PrintWriter, long, boolean):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e4
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.app.ProcessStats.ProcessDataCollection.print(java.io.PrintWriter, long, boolean):void");
        }
    }

    public static final class ProcessState extends DurationsTable {
        boolean mActive;
        long mAvgCachedKillPss;
        public ProcessState mCommonProcess;
        int mCurState;
        boolean mDead;
        int mLastPssState;
        long mLastPssTime;
        long mMaxCachedKillPss;
        long mMinCachedKillPss;
        boolean mMultiPackage;
        int mNumActiveServices;
        int mNumCachedKill;
        int mNumExcessiveCpu;
        int mNumExcessiveWake;
        int mNumStartedServices;
        public final String mPackage;
        int[] mPssTable;
        int mPssTableSize;
        long mStartTime;
        ProcessState mTmpFoundSubProc;
        int mTmpNumInUse;
        public long mTmpTotalTime;
        public final int mUid;
        public final int mVersion;

        public ProcessState(ProcessStats processStats, String pkg, int uid, int vers, String name) {
            super(processStats, name);
            this.mCurState = ProcessStats.STATE_NOTHING;
            this.mLastPssState = ProcessStats.STATE_NOTHING;
            this.mCommonProcess = this;
            this.mPackage = pkg;
            this.mUid = uid;
            this.mVersion = vers;
        }

        public ProcessState(ProcessState commonProcess, String pkg, int uid, int vers, String name, long now) {
            super(commonProcess.mStats, name);
            this.mCurState = ProcessStats.STATE_NOTHING;
            this.mLastPssState = ProcessStats.STATE_NOTHING;
            this.mCommonProcess = commonProcess;
            this.mPackage = pkg;
            this.mUid = uid;
            this.mVersion = vers;
            this.mCurState = commonProcess.mCurState;
            this.mStartTime = now;
        }

        ProcessState clone(String pkg, long now) {
            ProcessState pnew = new ProcessState(this, pkg, this.mUid, this.mVersion, this.mName, now);
            copyDurationsTo(pnew);
            if (this.mPssTable != null) {
                this.mStats.mAddLongTable = new int[this.mPssTable.length];
                this.mStats.mAddLongTableSize = ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT;
                for (int i = ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT; i < this.mPssTableSize; i += ProcessStats.SYS_MEM_USAGE_CACHED_MINIMUM) {
                    int origEnt = this.mPssTable[i];
                    int type = (origEnt >> ProcessStats.OFFSET_TYPE_SHIFT) & ProcessStats.OFFSET_TYPE_MASK;
                    int newOff = this.mStats.addLongData(i, type, ProcessStats.SYS_MEM_USAGE_ZRAM_MINIMUM);
                    this.mStats.mAddLongTable[i] = newOff | type;
                    for (int j = ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT; j < ProcessStats.SYS_MEM_USAGE_ZRAM_MINIMUM; j += ProcessStats.SYS_MEM_USAGE_CACHED_MINIMUM) {
                        this.mStats.setLong(newOff, j, this.mStats.getLong(origEnt, j));
                    }
                }
                pnew.mPssTable = this.mStats.mAddLongTable;
                pnew.mPssTableSize = this.mStats.mAddLongTableSize;
            }
            pnew.mNumExcessiveWake = this.mNumExcessiveWake;
            pnew.mNumExcessiveCpu = this.mNumExcessiveCpu;
            pnew.mNumCachedKill = this.mNumCachedKill;
            pnew.mMinCachedKillPss = this.mMinCachedKillPss;
            pnew.mAvgCachedKillPss = this.mAvgCachedKillPss;
            pnew.mMaxCachedKillPss = this.mMaxCachedKillPss;
            pnew.mActive = this.mActive;
            pnew.mNumActiveServices = this.mNumActiveServices;
            pnew.mNumStartedServices = this.mNumStartedServices;
            return pnew;
        }

        void add(ProcessState other) {
            addDurations(other);
            for (int i = ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT; i < other.mPssTableSize; i += ProcessStats.SYS_MEM_USAGE_CACHED_MINIMUM) {
                int ent = other.mPssTable[i];
                addPss((ent >> ProcessStats.OFFSET_TYPE_SHIFT) & ProcessStats.OFFSET_TYPE_MASK, (int) other.mStats.getLong(ent, ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT), other.mStats.getLong(ent, ProcessStats.SYS_MEM_USAGE_CACHED_MINIMUM), other.mStats.getLong(ent, ProcessStats.SYS_MEM_USAGE_CACHED_AVERAGE), other.mStats.getLong(ent, ProcessStats.SYS_MEM_USAGE_CACHED_MAXIMUM), other.mStats.getLong(ent, ProcessStats.SYS_MEM_USAGE_FREE_MINIMUM), other.mStats.getLong(ent, ProcessStats.SYS_MEM_USAGE_FREE_AVERAGE), other.mStats.getLong(ent, ProcessStats.SYS_MEM_USAGE_FREE_MAXIMUM));
            }
            this.mNumExcessiveWake += other.mNumExcessiveWake;
            this.mNumExcessiveCpu += other.mNumExcessiveCpu;
            if (other.mNumCachedKill > 0) {
                addCachedKill(other.mNumCachedKill, other.mMinCachedKillPss, other.mAvgCachedKillPss, other.mMaxCachedKillPss);
            }
        }

        void resetSafely(long now) {
            resetDurationsSafely();
            this.mStartTime = now;
            this.mLastPssState = ProcessStats.STATE_NOTHING;
            this.mLastPssTime = 0;
            this.mPssTable = null;
            this.mPssTableSize = ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT;
            this.mNumExcessiveWake = ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT;
            this.mNumExcessiveCpu = ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT;
            this.mNumCachedKill = ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT;
            this.mMaxCachedKillPss = 0;
            this.mAvgCachedKillPss = 0;
            this.mMinCachedKillPss = 0;
        }

        void makeDead() {
            this.mDead = true;
        }

        private void ensureNotDead() {
            if (this.mDead) {
                Slog.wtfStack(ProcessStats.TAG, "ProcessState dead: name=" + this.mName + " pkg=" + this.mPackage + " uid=" + this.mUid + " common.name=" + this.mCommonProcess.mName);
            }
        }

        void writeToParcel(Parcel out, long now) {
            out.writeInt(this.mMultiPackage ? ProcessStats.SYS_MEM_USAGE_CACHED_MINIMUM : ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT);
            writeDurationsToParcel(out);
            out.writeInt(this.mPssTableSize);
            for (int i = ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT; i < this.mPssTableSize; i += ProcessStats.SYS_MEM_USAGE_CACHED_MINIMUM) {
                out.writeInt(this.mPssTable[i]);
            }
            out.writeInt(this.mNumExcessiveWake);
            out.writeInt(this.mNumExcessiveCpu);
            out.writeInt(this.mNumCachedKill);
            if (this.mNumCachedKill > 0) {
                out.writeLong(this.mMinCachedKillPss);
                out.writeLong(this.mAvgCachedKillPss);
                out.writeLong(this.mMaxCachedKillPss);
            }
        }

        boolean readFromParcel(Parcel in, boolean fully) {
            boolean multiPackage;
            int i = ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT;
            if (in.readInt() != 0) {
                multiPackage = true;
            } else {
                multiPackage = ProcessStats.DEBUG_PARCEL;
            }
            if (fully) {
                this.mMultiPackage = multiPackage;
            }
            if (!readDurationsFromParcel(in)) {
                return ProcessStats.DEBUG_PARCEL;
            }
            this.mPssTable = this.mStats.readTableFromParcel(in, this.mName, "pss");
            if (this.mPssTable == ProcessStats.BAD_TABLE) {
                return ProcessStats.DEBUG_PARCEL;
            }
            if (this.mPssTable != null) {
                i = this.mPssTable.length;
            }
            this.mPssTableSize = i;
            this.mNumExcessiveWake = in.readInt();
            this.mNumExcessiveCpu = in.readInt();
            this.mNumCachedKill = in.readInt();
            if (this.mNumCachedKill > 0) {
                this.mMinCachedKillPss = in.readLong();
                this.mAvgCachedKillPss = in.readLong();
                this.mMaxCachedKillPss = in.readLong();
            } else {
                this.mMaxCachedKillPss = 0;
                this.mAvgCachedKillPss = 0;
                this.mMinCachedKillPss = 0;
            }
            return true;
        }

        public void makeActive() {
            ensureNotDead();
            this.mActive = true;
        }

        public void makeInactive() {
            this.mActive = ProcessStats.DEBUG_PARCEL;
        }

        public boolean isInUse() {
            return (this.mActive || this.mNumActiveServices > 0 || this.mNumStartedServices > 0 || this.mCurState != ProcessStats.STATE_NOTHING) ? true : ProcessStats.DEBUG_PARCEL;
        }

        public void setState(int state, int memFactor, long now, ArrayMap<String, ProcessStateHolder> pkgList) {
            state = state < 0 ? this.mNumStartedServices > 0 ? (memFactor * ProcessStats.SYS_MEM_USAGE_NATIVE_AVERAGE) + ProcessStats.SYS_MEM_USAGE_ZRAM_MINIMUM : ProcessStats.STATE_NOTHING : ProcessStats.PROCESS_STATE_TO_STATE[state] + (memFactor * ProcessStats.SYS_MEM_USAGE_NATIVE_AVERAGE);
            this.mCommonProcess.setState(state, now);
            if (this.mCommonProcess.mMultiPackage && pkgList != null) {
                for (int ip = pkgList.size() + ProcessStats.STATE_NOTHING; ip >= 0; ip += ProcessStats.STATE_NOTHING) {
                    pullFixedProc(pkgList, ip).setState(state, now);
                }
            }
        }

        void setState(int state, long now) {
            ensureNotDead();
            if (this.mCurState != state) {
                commitStateTime(now);
                this.mCurState = state;
            }
        }

        void commitStateTime(long now) {
            if (this.mCurState != ProcessStats.STATE_NOTHING) {
                long dur = now - this.mStartTime;
                if (dur > 0) {
                    addDuration(this.mCurState, dur);
                }
            }
            this.mStartTime = now;
        }

        void incActiveServices(String serviceName) {
            if (this.mCommonProcess != this) {
                this.mCommonProcess.incActiveServices(serviceName);
            }
            this.mNumActiveServices += ProcessStats.SYS_MEM_USAGE_CACHED_MINIMUM;
        }

        void decActiveServices(String serviceName) {
            if (this.mCommonProcess != this) {
                this.mCommonProcess.decActiveServices(serviceName);
            }
            this.mNumActiveServices += ProcessStats.STATE_NOTHING;
            if (this.mNumActiveServices < 0) {
                Slog.wtfStack(ProcessStats.TAG, "Proc active services underrun: pkg=" + this.mPackage + " uid=" + this.mUid + " proc=" + this.mName + " service=" + serviceName);
                this.mNumActiveServices = ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT;
            }
        }

        void incStartedServices(int memFactor, long now, String serviceName) {
            if (this.mCommonProcess != this) {
                this.mCommonProcess.incStartedServices(memFactor, now, serviceName);
            }
            this.mNumStartedServices += ProcessStats.SYS_MEM_USAGE_CACHED_MINIMUM;
            if (this.mNumStartedServices == ProcessStats.SYS_MEM_USAGE_CACHED_MINIMUM && this.mCurState == ProcessStats.STATE_NOTHING) {
                setState((memFactor * ProcessStats.SYS_MEM_USAGE_NATIVE_AVERAGE) + ProcessStats.SYS_MEM_USAGE_ZRAM_MINIMUM, now);
            }
        }

        void decStartedServices(int memFactor, long now, String serviceName) {
            if (this.mCommonProcess != this) {
                this.mCommonProcess.decStartedServices(memFactor, now, serviceName);
            }
            this.mNumStartedServices += ProcessStats.STATE_NOTHING;
            if (this.mNumStartedServices == 0 && this.mCurState % ProcessStats.SYS_MEM_USAGE_NATIVE_AVERAGE == ProcessStats.SYS_MEM_USAGE_ZRAM_MINIMUM) {
                setState(ProcessStats.STATE_NOTHING, now);
            } else if (this.mNumStartedServices < 0) {
                Slog.wtfStack(ProcessStats.TAG, "Proc started services underrun: pkg=" + this.mPackage + " uid=" + this.mUid + " name=" + this.mName);
                this.mNumStartedServices = ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT;
            }
        }

        public void addPss(long pss, long uss, boolean always, ArrayMap<String, ProcessStateHolder> pkgList) {
            ensureNotDead();
            if (always || this.mLastPssState != this.mCurState || SystemClock.uptimeMillis() >= this.mLastPssTime + LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS) {
                this.mLastPssState = this.mCurState;
                this.mLastPssTime = SystemClock.uptimeMillis();
                if (this.mCurState != ProcessStats.STATE_NOTHING) {
                    this.mCommonProcess.addPss(this.mCurState, ProcessStats.SYS_MEM_USAGE_CACHED_MINIMUM, pss, pss, pss, uss, uss, uss);
                    if (this.mCommonProcess.mMultiPackage && pkgList != null) {
                        for (int ip = pkgList.size() + ProcessStats.STATE_NOTHING; ip >= 0; ip += ProcessStats.STATE_NOTHING) {
                            pullFixedProc(pkgList, ip).addPss(this.mCurState, ProcessStats.SYS_MEM_USAGE_CACHED_MINIMUM, pss, pss, pss, uss, uss, uss);
                        }
                    }
                }
            }
        }

        void addPss(int state, int inCount, long minPss, long avgPss, long maxPss, long minUss, long avgUss, long maxUss) {
            int off;
            int idx = ProcessStats.binarySearch(this.mPssTable, this.mPssTableSize, state);
            if (idx >= 0) {
                off = this.mPssTable[idx];
            } else {
                this.mStats.mAddLongTable = this.mPssTable;
                this.mStats.mAddLongTableSize = this.mPssTableSize;
                off = this.mStats.addLongData(idx ^ ProcessStats.STATE_NOTHING, state, ProcessStats.SYS_MEM_USAGE_ZRAM_MINIMUM);
                this.mPssTable = this.mStats.mAddLongTable;
                this.mPssTableSize = this.mStats.mAddLongTableSize;
            }
            long[] longs = (long[]) this.mStats.mLongs.get((off >> ProcessStats.OFFSET_ARRAY_SHIFT) & ProcessStats.OFFSET_ARRAY_MASK);
            idx = (off >> ProcessStats.OFFSET_INDEX_SHIFT) & ProcessStats.OFFSET_INDEX_MASK;
            long count = longs[idx + ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT];
            if (count == 0) {
                longs[idx + ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT] = (long) inCount;
                longs[idx + ProcessStats.SYS_MEM_USAGE_CACHED_MINIMUM] = minPss;
                longs[idx + ProcessStats.SYS_MEM_USAGE_CACHED_AVERAGE] = avgPss;
                longs[idx + ProcessStats.SYS_MEM_USAGE_CACHED_MAXIMUM] = maxPss;
                longs[idx + ProcessStats.SYS_MEM_USAGE_FREE_MINIMUM] = minUss;
                longs[idx + ProcessStats.SYS_MEM_USAGE_FREE_AVERAGE] = avgUss;
                longs[idx + ProcessStats.SYS_MEM_USAGE_FREE_MAXIMUM] = maxUss;
                return;
            }
            longs[idx + ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT] = ((long) inCount) + count;
            if (longs[idx + ProcessStats.SYS_MEM_USAGE_CACHED_MINIMUM] > minPss) {
                longs[idx + ProcessStats.SYS_MEM_USAGE_CACHED_MINIMUM] = minPss;
            }
            longs[idx + ProcessStats.SYS_MEM_USAGE_CACHED_AVERAGE] = (long) (((((double) longs[idx + ProcessStats.SYS_MEM_USAGE_CACHED_AVERAGE]) * ((double) count)) + (((double) avgPss) * ((double) inCount))) / ((double) (((long) inCount) + count)));
            if (longs[idx + ProcessStats.SYS_MEM_USAGE_CACHED_MAXIMUM] < maxPss) {
                longs[idx + ProcessStats.SYS_MEM_USAGE_CACHED_MAXIMUM] = maxPss;
            }
            if (longs[idx + ProcessStats.SYS_MEM_USAGE_FREE_MINIMUM] > minUss) {
                longs[idx + ProcessStats.SYS_MEM_USAGE_FREE_MINIMUM] = minUss;
            }
            longs[idx + ProcessStats.SYS_MEM_USAGE_FREE_AVERAGE] = (long) (((((double) longs[idx + ProcessStats.SYS_MEM_USAGE_FREE_AVERAGE]) * ((double) count)) + (((double) avgUss) * ((double) inCount))) / ((double) (((long) inCount) + count)));
            if (longs[idx + ProcessStats.SYS_MEM_USAGE_FREE_MAXIMUM] < maxUss) {
                longs[idx + ProcessStats.SYS_MEM_USAGE_FREE_MAXIMUM] = maxUss;
            }
        }

        public void reportExcessiveWake(ArrayMap<String, ProcessStateHolder> pkgList) {
            ensureNotDead();
            ProcessState processState = this.mCommonProcess;
            processState.mNumExcessiveWake += ProcessStats.SYS_MEM_USAGE_CACHED_MINIMUM;
            if (this.mCommonProcess.mMultiPackage) {
                for (int ip = pkgList.size() + ProcessStats.STATE_NOTHING; ip >= 0; ip += ProcessStats.STATE_NOTHING) {
                    processState = pullFixedProc(pkgList, ip);
                    processState.mNumExcessiveWake += ProcessStats.SYS_MEM_USAGE_CACHED_MINIMUM;
                }
            }
        }

        public void reportExcessiveCpu(ArrayMap<String, ProcessStateHolder> pkgList) {
            ensureNotDead();
            ProcessState processState = this.mCommonProcess;
            processState.mNumExcessiveCpu += ProcessStats.SYS_MEM_USAGE_CACHED_MINIMUM;
            if (this.mCommonProcess.mMultiPackage) {
                for (int ip = pkgList.size() + ProcessStats.STATE_NOTHING; ip >= 0; ip += ProcessStats.STATE_NOTHING) {
                    processState = pullFixedProc(pkgList, ip);
                    processState.mNumExcessiveCpu += ProcessStats.SYS_MEM_USAGE_CACHED_MINIMUM;
                }
            }
        }

        private void addCachedKill(int num, long minPss, long avgPss, long maxPss) {
            if (this.mNumCachedKill <= 0) {
                this.mNumCachedKill = num;
                this.mMinCachedKillPss = minPss;
                this.mAvgCachedKillPss = avgPss;
                this.mMaxCachedKillPss = maxPss;
                return;
            }
            if (minPss < this.mMinCachedKillPss) {
                this.mMinCachedKillPss = minPss;
            }
            if (maxPss > this.mMaxCachedKillPss) {
                this.mMaxCachedKillPss = maxPss;
            }
            this.mAvgCachedKillPss = (long) (((((double) this.mAvgCachedKillPss) * ((double) this.mNumCachedKill)) + ((double) avgPss)) / ((double) (this.mNumCachedKill + num)));
            this.mNumCachedKill += num;
        }

        public void reportCachedKill(ArrayMap<String, ProcessStateHolder> pkgList, long pss) {
            ensureNotDead();
            this.mCommonProcess.addCachedKill(ProcessStats.SYS_MEM_USAGE_CACHED_MINIMUM, pss, pss, pss);
            if (this.mCommonProcess.mMultiPackage) {
                for (int ip = pkgList.size() + ProcessStats.STATE_NOTHING; ip >= 0; ip += ProcessStats.STATE_NOTHING) {
                    pullFixedProc(pkgList, ip).addCachedKill(ProcessStats.SYS_MEM_USAGE_CACHED_MINIMUM, pss, pss, pss);
                }
            }
        }

        ProcessState pullFixedProc(String pkgName) {
            if (!this.mMultiPackage) {
                return this;
            }
            SparseArray<PackageState> vpkg = (SparseArray) this.mStats.mPackages.get(pkgName, this.mUid);
            if (vpkg == null) {
                throw new IllegalStateException("Didn't find package " + pkgName + " / " + this.mUid);
            }
            PackageState pkg = (PackageState) vpkg.get(this.mVersion);
            if (pkg == null) {
                throw new IllegalStateException("Didn't find package " + pkgName + " / " + this.mUid + " vers " + this.mVersion);
            }
            ProcessState processState = (ProcessState) pkg.mProcesses.get(this.mName);
            if (processState != null) {
                return processState;
            }
            throw new IllegalStateException("Didn't create per-package process " + this.mName + " in pkg " + pkgName + " / " + this.mUid + " vers " + this.mVersion);
        }

        private ProcessState pullFixedProc(ArrayMap<String, ProcessStateHolder> pkgList, int index) {
            ProcessStateHolder holder = (ProcessStateHolder) pkgList.valueAt(index);
            ProcessState proc = holder.state;
            if (this.mDead && proc.mCommonProcess != proc) {
                Log.wtf(ProcessStats.TAG, "Pulling dead proc: name=" + this.mName + " pkg=" + this.mPackage + " uid=" + this.mUid + " common.name=" + this.mCommonProcess.mName);
                proc = this.mStats.getProcessStateLocked(proc.mPackage, proc.mUid, proc.mVersion, proc.mName);
            }
            if (proc.mMultiPackage) {
                SparseArray<PackageState> vpkg = (SparseArray) this.mStats.mPackages.get((String) pkgList.keyAt(index), proc.mUid);
                if (vpkg == null) {
                    throw new IllegalStateException("No existing package " + ((String) pkgList.keyAt(index)) + "/" + proc.mUid + " for multi-proc " + proc.mName);
                }
                PackageState pkg = (PackageState) vpkg.get(proc.mVersion);
                if (pkg == null) {
                    throw new IllegalStateException("No existing package " + ((String) pkgList.keyAt(index)) + "/" + proc.mUid + " for multi-proc " + proc.mName + " version " + proc.mVersion);
                }
                proc = (ProcessState) pkg.mProcesses.get(proc.mName);
                if (proc == null) {
                    throw new IllegalStateException("Didn't create per-package process " + proc.mName + " in pkg " + pkg.mPackageName + "/" + pkg.mUid);
                }
                holder.state = proc;
            }
            return proc;
        }

        long getDuration(int state, long now) {
            long time = super.getDuration(state, now);
            if (this.mCurState == state) {
                return time + (now - this.mStartTime);
            }
            return time;
        }

        long getPssSampleCount(int state) {
            int idx = ProcessStats.binarySearch(this.mPssTable, this.mPssTableSize, state);
            return idx >= 0 ? this.mStats.getLong(this.mPssTable[idx], ProcessStats.SYS_MEM_USAGE_SAMPLE_COUNT) : 0;
        }

        long getPssMinimum(int state) {
            int idx = ProcessStats.binarySearch(this.mPssTable, this.mPssTableSize, state);
            return idx >= 0 ? this.mStats.getLong(this.mPssTable[idx], ProcessStats.SYS_MEM_USAGE_CACHED_MINIMUM) : 0;
        }

        long getPssAverage(int state) {
            int idx = ProcessStats.binarySearch(this.mPssTable, this.mPssTableSize, state);
            return idx >= 0 ? this.mStats.getLong(this.mPssTable[idx], ProcessStats.SYS_MEM_USAGE_CACHED_AVERAGE) : 0;
        }

        long getPssMaximum(int state) {
            int idx = ProcessStats.binarySearch(this.mPssTable, this.mPssTableSize, state);
            return idx >= 0 ? this.mStats.getLong(this.mPssTable[idx], ProcessStats.SYS_MEM_USAGE_CACHED_MAXIMUM) : 0;
        }

        long getPssUssMinimum(int state) {
            int idx = ProcessStats.binarySearch(this.mPssTable, this.mPssTableSize, state);
            return idx >= 0 ? this.mStats.getLong(this.mPssTable[idx], ProcessStats.SYS_MEM_USAGE_FREE_MINIMUM) : 0;
        }

        long getPssUssAverage(int state) {
            int idx = ProcessStats.binarySearch(this.mPssTable, this.mPssTableSize, state);
            return idx >= 0 ? this.mStats.getLong(this.mPssTable[idx], ProcessStats.SYS_MEM_USAGE_FREE_AVERAGE) : 0;
        }

        long getPssUssMaximum(int state) {
            int idx = ProcessStats.binarySearch(this.mPssTable, this.mPssTableSize, state);
            return idx >= 0 ? this.mStats.getLong(this.mPssTable[idx], ProcessStats.SYS_MEM_USAGE_FREE_MAXIMUM) : 0;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(RILConstants.RIL_REQUEST_SET_DATA_PROFILE);
            sb.append("ProcessState{").append(Integer.toHexString(System.identityHashCode(this))).append(" ").append(this.mName).append("/").append(this.mUid).append(" pkg=").append(this.mPackage);
            if (this.mMultiPackage) {
                sb.append(" (multi)");
            }
            if (this.mCommonProcess != this) {
                sb.append(" (sub)");
            }
            sb.append("}");
            return sb.toString();
        }
    }

    public static final class ProcessStateHolder {
        public final int appVersion;
        public ProcessState state;

        public ProcessStateHolder(int _appVersion) {
            this.appVersion = _appVersion;
        }
    }

    static class PssAggr {
        long pss;
        long samples;

        PssAggr() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.app.ProcessStats.PssAggr.<init>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.app.ProcessStats.PssAggr.<init>():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e7
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.app.ProcessStats.PssAggr.<init>():void");
        }

        void add(long r1, long r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.app.ProcessStats.PssAggr.add(long, long):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.app.ProcessStats.PssAggr.add(long, long):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e4
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.app.ProcessStats.PssAggr.add(long, long):void");
        }
    }

    public static final class ServiceState extends DurationsTable {
        public static final int SERVICE_BOUND = 2;
        static final int SERVICE_COUNT = 4;
        public static final int SERVICE_EXEC = 3;
        public static final int SERVICE_RUN = 0;
        public static final int SERVICE_STARTED = 1;
        int mBoundCount;
        long mBoundStartTime;
        public int mBoundState;
        int mExecCount;
        long mExecStartTime;
        public int mExecState;
        Object mOwner;
        public final String mPackage;
        ProcessState mProc;
        public final String mProcessName;
        boolean mRestarting;
        int mRunCount;
        long mRunStartTime;
        public int mRunState;
        boolean mStarted;
        int mStartedCount;
        long mStartedStartTime;
        public int mStartedState;

        public ServiceState(ProcessStats processStats, String pkg, String name, String processName, ProcessState proc) {
            super(processStats, name);
            this.mRunState = ProcessStats.STATE_NOTHING;
            this.mStartedState = ProcessStats.STATE_NOTHING;
            this.mBoundState = ProcessStats.STATE_NOTHING;
            this.mExecState = ProcessStats.STATE_NOTHING;
            this.mPackage = pkg;
            this.mProcessName = processName;
            this.mProc = proc;
        }

        public void applyNewOwner(Object newOwner) {
            if (this.mOwner == newOwner) {
                return;
            }
            if (this.mOwner == null) {
                this.mOwner = newOwner;
                this.mProc.incActiveServices(this.mName);
                return;
            }
            this.mOwner = newOwner;
            if (this.mStarted || this.mBoundState != ProcessStats.STATE_NOTHING || this.mExecState != ProcessStats.STATE_NOTHING) {
                long now = SystemClock.uptimeMillis();
                if (this.mStarted) {
                    setStarted(ProcessStats.DEBUG_PARCEL, SERVICE_RUN, now);
                }
                if (this.mBoundState != ProcessStats.STATE_NOTHING) {
                    setBound(ProcessStats.DEBUG_PARCEL, SERVICE_RUN, now);
                }
                if (this.mExecState != ProcessStats.STATE_NOTHING) {
                    setExecuting(ProcessStats.DEBUG_PARCEL, SERVICE_RUN, now);
                }
            }
        }

        public void clearCurrentOwner(Object owner, boolean silently) {
            if (this.mOwner == owner) {
                this.mProc.decActiveServices(this.mName);
                if (!(!this.mStarted && this.mBoundState == ProcessStats.STATE_NOTHING && this.mExecState == ProcessStats.STATE_NOTHING)) {
                    long now = SystemClock.uptimeMillis();
                    if (this.mStarted) {
                        if (!silently) {
                            Slog.wtfStack(ProcessStats.TAG, "Service owner " + owner + " cleared while started: pkg=" + this.mPackage + " service=" + this.mName + " proc=" + this.mProc);
                        }
                        setStarted(ProcessStats.DEBUG_PARCEL, SERVICE_RUN, now);
                    }
                    if (this.mBoundState != ProcessStats.STATE_NOTHING) {
                        if (!silently) {
                            Slog.wtfStack(ProcessStats.TAG, "Service owner " + owner + " cleared while bound: pkg=" + this.mPackage + " service=" + this.mName + " proc=" + this.mProc);
                        }
                        setBound(ProcessStats.DEBUG_PARCEL, SERVICE_RUN, now);
                    }
                    if (this.mExecState != ProcessStats.STATE_NOTHING) {
                        if (!silently) {
                            Slog.wtfStack(ProcessStats.TAG, "Service owner " + owner + " cleared while exec: pkg=" + this.mPackage + " service=" + this.mName + " proc=" + this.mProc);
                        }
                        setExecuting(ProcessStats.DEBUG_PARCEL, SERVICE_RUN, now);
                    }
                }
                this.mOwner = null;
            }
        }

        public boolean isInUse() {
            return (this.mOwner != null || this.mRestarting) ? true : ProcessStats.DEBUG_PARCEL;
        }

        public boolean isRestarting() {
            return this.mRestarting;
        }

        void add(ServiceState other) {
            addDurations(other);
            this.mRunCount += other.mRunCount;
            this.mStartedCount += other.mStartedCount;
            this.mBoundCount += other.mBoundCount;
            this.mExecCount += other.mExecCount;
        }

        void resetSafely(long now) {
            int i;
            int i2 = SERVICE_STARTED;
            resetDurationsSafely();
            this.mRunCount = this.mRunState != ProcessStats.STATE_NOTHING ? SERVICE_STARTED : SERVICE_RUN;
            if (this.mStartedState != ProcessStats.STATE_NOTHING) {
                i = SERVICE_STARTED;
            } else {
                i = SERVICE_RUN;
            }
            this.mStartedCount = i;
            if (this.mBoundState != ProcessStats.STATE_NOTHING) {
                i = SERVICE_STARTED;
            } else {
                i = SERVICE_RUN;
            }
            this.mBoundCount = i;
            if (this.mExecState == ProcessStats.STATE_NOTHING) {
                i2 = SERVICE_RUN;
            }
            this.mExecCount = i2;
            this.mExecStartTime = now;
            this.mBoundStartTime = now;
            this.mStartedStartTime = now;
            this.mRunStartTime = now;
        }

        void writeToParcel(Parcel out, long now) {
            writeDurationsToParcel(out);
            out.writeInt(this.mRunCount);
            out.writeInt(this.mStartedCount);
            out.writeInt(this.mBoundCount);
            out.writeInt(this.mExecCount);
        }

        boolean readFromParcel(Parcel in) {
            if (!readDurationsFromParcel(in)) {
                return ProcessStats.DEBUG_PARCEL;
            }
            this.mRunCount = in.readInt();
            this.mStartedCount = in.readInt();
            this.mBoundCount = in.readInt();
            this.mExecCount = in.readInt();
            return true;
        }

        void commitStateTime(long now) {
            if (this.mRunState != ProcessStats.STATE_NOTHING) {
                addDuration((this.mRunState * SERVICE_COUNT) + SERVICE_RUN, now - this.mRunStartTime);
                this.mRunStartTime = now;
            }
            if (this.mStartedState != ProcessStats.STATE_NOTHING) {
                addDuration((this.mStartedState * SERVICE_COUNT) + SERVICE_STARTED, now - this.mStartedStartTime);
                this.mStartedStartTime = now;
            }
            if (this.mBoundState != ProcessStats.STATE_NOTHING) {
                addDuration((this.mBoundState * SERVICE_COUNT) + SERVICE_BOUND, now - this.mBoundStartTime);
                this.mBoundStartTime = now;
            }
            if (this.mExecState != ProcessStats.STATE_NOTHING) {
                addDuration((this.mExecState * SERVICE_COUNT) + SERVICE_EXEC, now - this.mExecStartTime);
                this.mExecStartTime = now;
            }
        }

        private void updateRunning(int memFactor, long now) {
            int state;
            if (this.mStartedState == ProcessStats.STATE_NOTHING && this.mBoundState == ProcessStats.STATE_NOTHING && this.mExecState == ProcessStats.STATE_NOTHING) {
                state = ProcessStats.STATE_NOTHING;
            } else {
                state = memFactor;
            }
            if (this.mRunState != state) {
                if (this.mRunState != ProcessStats.STATE_NOTHING) {
                    addDuration((this.mRunState * SERVICE_COUNT) + SERVICE_RUN, now - this.mRunStartTime);
                } else if (state != ProcessStats.STATE_NOTHING) {
                    this.mRunCount += SERVICE_STARTED;
                }
                this.mRunState = state;
                this.mRunStartTime = now;
            }
        }

        public void setStarted(boolean started, int memFactor, long now) {
            if (this.mOwner == null) {
                Slog.wtf(ProcessStats.TAG, "Starting service " + this + " without owner");
            }
            this.mStarted = started;
            updateStartedState(memFactor, now);
        }

        public void setRestarting(boolean restarting, int memFactor, long now) {
            this.mRestarting = restarting;
            updateStartedState(memFactor, now);
        }

        void updateStartedState(int memFactor, long now) {
            boolean wasStarted;
            boolean started;
            int state;
            if (this.mStartedState != ProcessStats.STATE_NOTHING) {
                wasStarted = true;
            } else {
                wasStarted = ProcessStats.DEBUG_PARCEL;
            }
            if (this.mStarted || this.mRestarting) {
                started = true;
            } else {
                started = ProcessStats.DEBUG_PARCEL;
            }
            if (started) {
                state = memFactor;
            } else {
                state = ProcessStats.STATE_NOTHING;
            }
            if (this.mStartedState != state) {
                if (this.mStartedState != ProcessStats.STATE_NOTHING) {
                    addDuration((this.mStartedState * SERVICE_COUNT) + SERVICE_STARTED, now - this.mStartedStartTime);
                } else if (started) {
                    this.mStartedCount += SERVICE_STARTED;
                }
                this.mStartedState = state;
                this.mStartedStartTime = now;
                this.mProc = this.mProc.pullFixedProc(this.mPackage);
                if (wasStarted != started) {
                    if (started) {
                        this.mProc.incStartedServices(memFactor, now, this.mName);
                    } else {
                        this.mProc.decStartedServices(memFactor, now, this.mName);
                    }
                }
                updateRunning(memFactor, now);
            }
        }

        public void setBound(boolean bound, int memFactor, long now) {
            int state;
            if (this.mOwner == null) {
                Slog.wtf(ProcessStats.TAG, "Binding service " + this + " without owner");
            }
            if (bound) {
                state = memFactor;
            } else {
                state = ProcessStats.STATE_NOTHING;
            }
            if (this.mBoundState != state) {
                if (this.mBoundState != ProcessStats.STATE_NOTHING) {
                    addDuration((this.mBoundState * SERVICE_COUNT) + SERVICE_BOUND, now - this.mBoundStartTime);
                } else if (bound) {
                    this.mBoundCount += SERVICE_STARTED;
                }
                this.mBoundState = state;
                this.mBoundStartTime = now;
                updateRunning(memFactor, now);
            }
        }

        public void setExecuting(boolean executing, int memFactor, long now) {
            int state;
            if (this.mOwner == null) {
                Slog.wtf(ProcessStats.TAG, "Executing service " + this + " without owner");
            }
            if (executing) {
                state = memFactor;
            } else {
                state = ProcessStats.STATE_NOTHING;
            }
            if (this.mExecState != state) {
                if (this.mExecState != ProcessStats.STATE_NOTHING) {
                    addDuration((this.mExecState * SERVICE_COUNT) + SERVICE_EXEC, now - this.mExecStartTime);
                } else if (executing) {
                    this.mExecCount += SERVICE_STARTED;
                }
                this.mExecState = state;
                this.mExecStartTime = now;
                updateRunning(memFactor, now);
            }
        }

        private long getDuration(int opType, int curState, long startTime, int memFactor, long now) {
            long time = getDuration(opType + (memFactor * SERVICE_COUNT), now);
            if (curState == memFactor) {
                return time + (now - startTime);
            }
            return time;
        }

        public String toString() {
            return "ServiceState{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.mName + " pkg=" + this.mPackage + " proc=" + Integer.toHexString(System.identityHashCode(this)) + "}";
        }
    }

    public static class TotalMemoryUseCollection {
        final int[] memStates;
        public long[] processStatePss;
        public int[] processStateSamples;
        public long[] processStateTime;
        public double[] processStateWeight;
        final int[] screenStates;
        public double sysMemCachedWeight;
        public double sysMemFreeWeight;
        public double sysMemKernelWeight;
        public double sysMemNativeWeight;
        public int sysMemSamples;
        public long[] sysMemUsage;
        public double sysMemZRamWeight;
        public long totalTime;

        public TotalMemoryUseCollection(int[] r1, int[] r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.app.ProcessStats.TotalMemoryUseCollection.<init>(int[], int[]):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.app.ProcessStats.TotalMemoryUseCollection.<init>(int[], int[]):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.app.ProcessStats.TotalMemoryUseCollection.<init>(int[], int[]):void");
        }
    }

    static {
        COMMIT_PERIOD = 10800000;
        COMMIT_UPTIME_PERIOD = DateUtils.HOUR_IN_MILLIS;
        ALL_MEM_ADJ = new int[]{SYS_MEM_USAGE_SAMPLE_COUNT, SYS_MEM_USAGE_CACHED_MINIMUM, SYS_MEM_USAGE_CACHED_AVERAGE, SYS_MEM_USAGE_CACHED_MAXIMUM};
        ALL_SCREEN_ADJ = new int[]{SYS_MEM_USAGE_SAMPLE_COUNT, SYS_MEM_USAGE_FREE_MINIMUM};
        NON_CACHED_PROC_STATES = new int[]{SYS_MEM_USAGE_SAMPLE_COUNT, SYS_MEM_USAGE_CACHED_MINIMUM, SYS_MEM_USAGE_CACHED_AVERAGE, SYS_MEM_USAGE_CACHED_MAXIMUM, SYS_MEM_USAGE_FREE_MINIMUM, SYS_MEM_USAGE_FREE_AVERAGE, SYS_MEM_USAGE_FREE_MAXIMUM, SYS_MEM_USAGE_ZRAM_MINIMUM, SYS_MEM_USAGE_ZRAM_AVERAGE};
        BACKGROUND_PROC_STATES = new int[]{SYS_MEM_USAGE_CACHED_AVERAGE, SYS_MEM_USAGE_CACHED_MAXIMUM, SYS_MEM_USAGE_FREE_MINIMUM, SYS_MEM_USAGE_FREE_AVERAGE, SYS_MEM_USAGE_FREE_MAXIMUM, SYS_MEM_USAGE_ZRAM_MINIMUM, SYS_MEM_USAGE_ZRAM_AVERAGE};
        PROCESS_STATE_TO_STATE = new int[]{SYS_MEM_USAGE_SAMPLE_COUNT, SYS_MEM_USAGE_SAMPLE_COUNT, SYS_MEM_USAGE_CACHED_MINIMUM, SYS_MEM_USAGE_CACHED_AVERAGE, SYS_MEM_USAGE_CACHED_MAXIMUM, SYS_MEM_USAGE_FREE_MINIMUM, SYS_MEM_USAGE_FREE_AVERAGE, SYS_MEM_USAGE_FREE_MAXIMUM, SYS_MEM_USAGE_ZRAM_AVERAGE, SYS_MEM_USAGE_ZRAM_MAXIMUM, SYS_MEM_USAGE_KERNEL_MINIMUM, SYS_MEM_USAGE_KERNEL_AVERAGE, SYS_MEM_USAGE_KERNEL_MAXIMUM, SYS_MEM_USAGE_NATIVE_MINIMUM};
        ALL_PROC_STATES = new int[]{SYS_MEM_USAGE_SAMPLE_COUNT, SYS_MEM_USAGE_CACHED_MINIMUM, SYS_MEM_USAGE_CACHED_AVERAGE, SYS_MEM_USAGE_CACHED_MAXIMUM, SYS_MEM_USAGE_FREE_MINIMUM, SYS_MEM_USAGE_FREE_AVERAGE, SYS_MEM_USAGE_FREE_MAXIMUM, SYS_MEM_USAGE_ZRAM_MINIMUM, SYS_MEM_USAGE_ZRAM_AVERAGE, SYS_MEM_USAGE_ZRAM_MAXIMUM, SYS_MEM_USAGE_KERNEL_MINIMUM, SYS_MEM_USAGE_KERNEL_AVERAGE, SYS_MEM_USAGE_KERNEL_MAXIMUM, SYS_MEM_USAGE_NATIVE_MINIMUM};
        String[] strArr = new String[SYS_MEM_USAGE_NATIVE_AVERAGE];
        strArr[SYS_MEM_USAGE_SAMPLE_COUNT] = "Persist";
        strArr[SYS_MEM_USAGE_CACHED_MINIMUM] = "Top    ";
        strArr[SYS_MEM_USAGE_CACHED_AVERAGE] = "ImpFg  ";
        strArr[SYS_MEM_USAGE_CACHED_MAXIMUM] = "ImpBg  ";
        strArr[SYS_MEM_USAGE_FREE_MINIMUM] = "Backup ";
        strArr[SYS_MEM_USAGE_FREE_AVERAGE] = "HeavyWt";
        strArr[SYS_MEM_USAGE_FREE_MAXIMUM] = "Service";
        strArr[SYS_MEM_USAGE_ZRAM_MINIMUM] = "ServRst";
        strArr[SYS_MEM_USAGE_ZRAM_AVERAGE] = "Receivr";
        strArr[SYS_MEM_USAGE_ZRAM_MAXIMUM] = "Home   ";
        strArr[SYS_MEM_USAGE_KERNEL_MINIMUM] = "LastAct";
        strArr[SYS_MEM_USAGE_KERNEL_AVERAGE] = "CchAct ";
        strArr[SYS_MEM_USAGE_KERNEL_MAXIMUM] = "CchCAct";
        strArr[SYS_MEM_USAGE_NATIVE_MINIMUM] = "CchEmty";
        STATE_NAMES = strArr;
        strArr = new String[SYS_MEM_USAGE_CACHED_AVERAGE];
        strArr[SYS_MEM_USAGE_SAMPLE_COUNT] = "off";
        strArr[SYS_MEM_USAGE_CACHED_MINIMUM] = "on";
        ADJ_SCREEN_NAMES_CSV = strArr;
        strArr = new String[SYS_MEM_USAGE_FREE_MINIMUM];
        strArr[SYS_MEM_USAGE_SAMPLE_COUNT] = "norm";
        strArr[SYS_MEM_USAGE_CACHED_MINIMUM] = "mod";
        strArr[SYS_MEM_USAGE_CACHED_AVERAGE] = "low";
        strArr[SYS_MEM_USAGE_CACHED_MAXIMUM] = "crit";
        ADJ_MEM_NAMES_CSV = strArr;
        strArr = new String[SYS_MEM_USAGE_NATIVE_AVERAGE];
        strArr[SYS_MEM_USAGE_SAMPLE_COUNT] = "pers";
        strArr[SYS_MEM_USAGE_CACHED_MINIMUM] = "top";
        strArr[SYS_MEM_USAGE_CACHED_AVERAGE] = "impfg";
        strArr[SYS_MEM_USAGE_CACHED_MAXIMUM] = "impbg";
        strArr[SYS_MEM_USAGE_FREE_MINIMUM] = "backup";
        strArr[SYS_MEM_USAGE_FREE_AVERAGE] = "heavy";
        strArr[SYS_MEM_USAGE_FREE_MAXIMUM] = "service";
        strArr[SYS_MEM_USAGE_ZRAM_MINIMUM] = "service-rs";
        strArr[SYS_MEM_USAGE_ZRAM_AVERAGE] = "receiver";
        strArr[SYS_MEM_USAGE_ZRAM_MAXIMUM] = "home";
        strArr[SYS_MEM_USAGE_KERNEL_MINIMUM] = "lastact";
        strArr[SYS_MEM_USAGE_KERNEL_AVERAGE] = "cch-activity";
        strArr[SYS_MEM_USAGE_KERNEL_MAXIMUM] = "cch-aclient";
        strArr[SYS_MEM_USAGE_NATIVE_MINIMUM] = "cch-empty";
        STATE_NAMES_CSV = strArr;
        strArr = new String[SYS_MEM_USAGE_CACHED_AVERAGE];
        strArr[SYS_MEM_USAGE_SAMPLE_COUNT] = "0";
        strArr[SYS_MEM_USAGE_CACHED_MINIMUM] = "1";
        ADJ_SCREEN_TAGS = strArr;
        strArr = new String[SYS_MEM_USAGE_FREE_MINIMUM];
        strArr[SYS_MEM_USAGE_SAMPLE_COUNT] = "n";
        strArr[SYS_MEM_USAGE_CACHED_MINIMUM] = "m";
        strArr[SYS_MEM_USAGE_CACHED_AVERAGE] = "l";
        strArr[SYS_MEM_USAGE_CACHED_MAXIMUM] = "c";
        ADJ_MEM_TAGS = strArr;
        strArr = new String[SYS_MEM_USAGE_NATIVE_AVERAGE];
        strArr[SYS_MEM_USAGE_SAMPLE_COUNT] = TtmlUtils.TAG_P;
        strArr[SYS_MEM_USAGE_CACHED_MINIMUM] = "t";
        strArr[SYS_MEM_USAGE_CACHED_AVERAGE] = "f";
        strArr[SYS_MEM_USAGE_CACHED_MAXIMUM] = "b";
        strArr[SYS_MEM_USAGE_FREE_MINIMUM] = "u";
        strArr[SYS_MEM_USAGE_FREE_AVERAGE] = "w";
        strArr[SYS_MEM_USAGE_FREE_MAXIMUM] = "s";
        strArr[SYS_MEM_USAGE_ZRAM_MINIMUM] = "x";
        strArr[SYS_MEM_USAGE_ZRAM_AVERAGE] = "r";
        strArr[SYS_MEM_USAGE_ZRAM_MAXIMUM] = "h";
        strArr[SYS_MEM_USAGE_KERNEL_MINIMUM] = "l";
        strArr[SYS_MEM_USAGE_KERNEL_AVERAGE] = "a";
        strArr[SYS_MEM_USAGE_KERNEL_MAXIMUM] = "c";
        strArr[SYS_MEM_USAGE_NATIVE_MINIMUM] = "e";
        STATE_TAGS = strArr;
        OFFSET_TYPE_SHIFT = SYS_MEM_USAGE_SAMPLE_COUNT;
        OFFSET_TYPE_MASK = R.styleable.Theme_windowSharedElementReturnTransition;
        OFFSET_ARRAY_SHIFT = SYS_MEM_USAGE_ZRAM_AVERAGE;
        OFFSET_ARRAY_MASK = R.styleable.Theme_windowSharedElementReturnTransition;
        OFFSET_INDEX_SHIFT = SYS_MEM_USAGE_COUNT;
        OFFSET_INDEX_MASK = Protocol.MAX_MESSAGE;
        CREATOR = new Creator<ProcessStats>() {
            public ProcessStats createFromParcel(Parcel in) {
                return new ProcessStats(in);
            }

            public ProcessStats[] newArray(int size) {
                return new ProcessStats[size];
            }
        };
        BAD_TABLE = new int[SYS_MEM_USAGE_SAMPLE_COUNT];
    }

    public ProcessStats(boolean running) {
        this.mPackages = new ProcessMap();
        this.mProcesses = new ProcessMap();
        this.mMemFactorDurations = new long[SYS_MEM_USAGE_ZRAM_AVERAGE];
        this.mMemFactor = STATE_NOTHING;
        this.mSysMemUsageTable = null;
        this.mSysMemUsageTableSize = SYS_MEM_USAGE_SAMPLE_COUNT;
        this.mSysMemUsageArgs = new long[SYS_MEM_USAGE_COUNT];
        this.mLongs = new ArrayList();
        this.mRunning = running;
        reset();
    }

    public ProcessStats(Parcel in) {
        this.mPackages = new ProcessMap();
        this.mProcesses = new ProcessMap();
        this.mMemFactorDurations = new long[SYS_MEM_USAGE_ZRAM_AVERAGE];
        this.mMemFactor = STATE_NOTHING;
        this.mSysMemUsageTable = null;
        this.mSysMemUsageTableSize = SYS_MEM_USAGE_SAMPLE_COUNT;
        this.mSysMemUsageArgs = new long[SYS_MEM_USAGE_COUNT];
        this.mLongs = new ArrayList();
        reset();
        readFromParcel(in);
    }

    public void add(ProcessStats other) {
        int ip;
        int iu;
        ProcessState thisProc;
        int i;
        ArrayMap<String, SparseArray<SparseArray<PackageState>>> pkgMap = other.mPackages.getMap();
        for (ip = SYS_MEM_USAGE_SAMPLE_COUNT; ip < pkgMap.size(); ip += SYS_MEM_USAGE_CACHED_MINIMUM) {
            String pkgName = (String) pkgMap.keyAt(ip);
            SparseArray<SparseArray<PackageState>> uids = (SparseArray) pkgMap.valueAt(ip);
            for (iu = SYS_MEM_USAGE_SAMPLE_COUNT; iu < uids.size(); iu += SYS_MEM_USAGE_CACHED_MINIMUM) {
                int uid = uids.keyAt(iu);
                SparseArray<PackageState> versions = (SparseArray) uids.valueAt(iu);
                for (int iv = SYS_MEM_USAGE_SAMPLE_COUNT; iv < versions.size(); iv += SYS_MEM_USAGE_CACHED_MINIMUM) {
                    int vers = versions.keyAt(iv);
                    PackageState otherState = (PackageState) versions.valueAt(iv);
                    int NPROCS = otherState.mProcesses.size();
                    int NSRVS = otherState.mServices.size();
                    for (int iproc = SYS_MEM_USAGE_SAMPLE_COUNT; iproc < NPROCS; iproc += SYS_MEM_USAGE_CACHED_MINIMUM) {
                        ProcessState otherProc = (ProcessState) otherState.mProcesses.valueAt(iproc);
                        if (otherProc.mCommonProcess != otherProc) {
                            thisProc = getProcessStateLocked(pkgName, uid, vers, otherProc.mName);
                            if (thisProc.mCommonProcess == thisProc) {
                                thisProc.mMultiPackage = true;
                                long now = SystemClock.uptimeMillis();
                                PackageState pkgState = getPackageStateLocked(pkgName, uid, vers);
                                thisProc = thisProc.clone(thisProc.mPackage, now);
                                pkgState.mProcesses.put(thisProc.mName, thisProc);
                            }
                            thisProc.add(otherProc);
                        }
                    }
                    for (int isvc = SYS_MEM_USAGE_SAMPLE_COUNT; isvc < NSRVS; isvc += SYS_MEM_USAGE_CACHED_MINIMUM) {
                        ServiceState otherSvc = (ServiceState) otherState.mServices.valueAt(isvc);
                        getServiceStateLocked(pkgName, uid, vers, otherSvc.mProcessName, otherSvc.mName).add(otherSvc);
                    }
                }
            }
        }
        ArrayMap<String, SparseArray<ProcessState>> procMap = other.mProcesses.getMap();
        for (ip = SYS_MEM_USAGE_SAMPLE_COUNT; ip < procMap.size(); ip += SYS_MEM_USAGE_CACHED_MINIMUM) {
            SparseArray<ProcessState> uids2 = (SparseArray) procMap.valueAt(ip);
            for (iu = SYS_MEM_USAGE_SAMPLE_COUNT; iu < uids2.size(); iu += SYS_MEM_USAGE_CACHED_MINIMUM) {
                uid = uids2.keyAt(iu);
                otherProc = (ProcessState) uids2.valueAt(iu);
                thisProc = (ProcessState) this.mProcesses.get(otherProc.mName, uid);
                if (thisProc == null) {
                    thisProc = new ProcessState(this, otherProc.mPackage, uid, otherProc.mVersion, otherProc.mName);
                    this.mProcesses.put(otherProc.mName, uid, thisProc);
                    PackageState thisState = getPackageStateLocked(otherProc.mPackage, uid, otherProc.mVersion);
                    if (!thisState.mProcesses.containsKey(otherProc.mName)) {
                        thisState.mProcesses.put(otherProc.mName, thisProc);
                    }
                }
                thisProc.add(otherProc);
            }
        }
        for (i = SYS_MEM_USAGE_SAMPLE_COUNT; i < SYS_MEM_USAGE_ZRAM_AVERAGE; i += SYS_MEM_USAGE_CACHED_MINIMUM) {
            long[] jArr = this.mMemFactorDurations;
            jArr[i] = jArr[i] + other.mMemFactorDurations[i];
        }
        for (i = SYS_MEM_USAGE_SAMPLE_COUNT; i < other.mSysMemUsageTableSize; i += SYS_MEM_USAGE_CACHED_MINIMUM) {
            int ent = other.mSysMemUsageTable[i];
            int i2 = (ent >> OFFSET_TYPE_SHIFT) & OFFSET_TYPE_MASK;
            long[] jArr2 = (long[]) other.mLongs.get((ent >> OFFSET_ARRAY_SHIFT) & OFFSET_ARRAY_MASK);
            addSysMemUsage(state, longs, (ent >> OFFSET_INDEX_SHIFT) & OFFSET_INDEX_MASK);
        }
        if (other.mTimePeriodStartClock < this.mTimePeriodStartClock) {
            this.mTimePeriodStartClock = other.mTimePeriodStartClock;
            this.mTimePeriodStartClockStr = other.mTimePeriodStartClockStr;
        }
        this.mTimePeriodEndRealtime += other.mTimePeriodEndRealtime - other.mTimePeriodStartRealtime;
        this.mTimePeriodEndUptime += other.mTimePeriodEndUptime - other.mTimePeriodStartUptime;
    }

    public void addSysMemUsage(long cachedMem, long freeMem, long zramMem, long kernelMem, long nativeMem) {
        if (this.mMemFactor != STATE_NOTHING) {
            int state = this.mMemFactor * SYS_MEM_USAGE_NATIVE_AVERAGE;
            this.mSysMemUsageArgs[SYS_MEM_USAGE_SAMPLE_COUNT] = 1;
            for (int i = SYS_MEM_USAGE_SAMPLE_COUNT; i < SYS_MEM_USAGE_CACHED_MAXIMUM; i += SYS_MEM_USAGE_CACHED_MINIMUM) {
                this.mSysMemUsageArgs[i + SYS_MEM_USAGE_CACHED_MINIMUM] = cachedMem;
                this.mSysMemUsageArgs[i + SYS_MEM_USAGE_FREE_MINIMUM] = freeMem;
                this.mSysMemUsageArgs[i + SYS_MEM_USAGE_ZRAM_MINIMUM] = zramMem;
                this.mSysMemUsageArgs[i + SYS_MEM_USAGE_KERNEL_MINIMUM] = kernelMem;
                this.mSysMemUsageArgs[i + SYS_MEM_USAGE_NATIVE_MINIMUM] = nativeMem;
            }
            addSysMemUsage(state, this.mSysMemUsageArgs, SYS_MEM_USAGE_SAMPLE_COUNT);
        }
    }

    void addSysMemUsage(int state, long[] data, int dataOff) {
        int off;
        int idx = binarySearch(this.mSysMemUsageTable, this.mSysMemUsageTableSize, state);
        if (idx >= 0) {
            off = this.mSysMemUsageTable[idx];
        } else {
            this.mAddLongTable = this.mSysMemUsageTable;
            this.mAddLongTableSize = this.mSysMemUsageTableSize;
            off = addLongData(idx ^ STATE_NOTHING, state, SYS_MEM_USAGE_COUNT);
            this.mSysMemUsageTable = this.mAddLongTable;
            this.mSysMemUsageTableSize = this.mAddLongTableSize;
        }
        addSysMemUsage((long[]) this.mLongs.get((off >> OFFSET_ARRAY_SHIFT) & OFFSET_ARRAY_MASK), (off >> OFFSET_INDEX_SHIFT) & OFFSET_INDEX_MASK, data, dataOff);
    }

    static void addSysMemUsage(long[] dstData, int dstOff, long[] addData, int addOff) {
        long dstCount = dstData[dstOff + SYS_MEM_USAGE_SAMPLE_COUNT];
        long addCount = addData[addOff + SYS_MEM_USAGE_SAMPLE_COUNT];
        int i;
        if (dstCount == 0) {
            dstData[dstOff + SYS_MEM_USAGE_SAMPLE_COUNT] = addCount;
            for (i = SYS_MEM_USAGE_CACHED_MINIMUM; i < SYS_MEM_USAGE_COUNT; i += SYS_MEM_USAGE_CACHED_MINIMUM) {
                dstData[dstOff + i] = addData[addOff + i];
            }
        } else if (addCount > 0) {
            dstData[dstOff + SYS_MEM_USAGE_SAMPLE_COUNT] = dstCount + addCount;
            for (i = SYS_MEM_USAGE_CACHED_MINIMUM; i < SYS_MEM_USAGE_COUNT; i += SYS_MEM_USAGE_CACHED_MAXIMUM) {
                if (dstData[dstOff + i] > addData[addOff + i]) {
                    dstData[dstOff + i] = addData[addOff + i];
                }
                dstData[(dstOff + i) + SYS_MEM_USAGE_CACHED_MINIMUM] = (long) (((((double) dstData[(dstOff + i) + SYS_MEM_USAGE_CACHED_MINIMUM]) * ((double) dstCount)) + (((double) addData[(addOff + i) + SYS_MEM_USAGE_CACHED_MINIMUM]) * ((double) addCount))) / ((double) (dstCount + addCount)));
                if (dstData[(dstOff + i) + SYS_MEM_USAGE_CACHED_AVERAGE] < addData[(addOff + i) + SYS_MEM_USAGE_CACHED_AVERAGE]) {
                    dstData[(dstOff + i) + SYS_MEM_USAGE_CACHED_AVERAGE] = addData[(addOff + i) + SYS_MEM_USAGE_CACHED_AVERAGE];
                }
            }
        }
    }

    private static void printScreenLabel(PrintWriter pw, int offset) {
        switch (offset) {
            case STATE_NOTHING /*-1*/:
                pw.print("     ");
            case SYS_MEM_USAGE_SAMPLE_COUNT /*0*/:
                pw.print("SOff/");
            case SYS_MEM_USAGE_FREE_MINIMUM /*4*/:
                pw.print("SOn /");
            default:
                pw.print("????/");
        }
    }

    public static void printScreenLabelCsv(PrintWriter pw, int offset) {
        switch (offset) {
            case STATE_NOTHING /*-1*/:
            case SYS_MEM_USAGE_SAMPLE_COUNT /*0*/:
                pw.print(ADJ_SCREEN_NAMES_CSV[SYS_MEM_USAGE_SAMPLE_COUNT]);
            case SYS_MEM_USAGE_FREE_MINIMUM /*4*/:
                pw.print(ADJ_SCREEN_NAMES_CSV[SYS_MEM_USAGE_CACHED_MINIMUM]);
            default:
                pw.print("???");
        }
    }

    private static void printMemLabel(PrintWriter pw, int offset, char sep) {
        switch (offset) {
            case STATE_NOTHING /*-1*/:
                pw.print("    ");
                if (sep != '\u0000') {
                    pw.print(' ');
                }
            case SYS_MEM_USAGE_SAMPLE_COUNT /*0*/:
                pw.print("Norm");
                if (sep != '\u0000') {
                    pw.print(sep);
                }
            case SYS_MEM_USAGE_CACHED_MINIMUM /*1*/:
                pw.print("Mod ");
                if (sep != '\u0000') {
                    pw.print(sep);
                }
            case SYS_MEM_USAGE_CACHED_AVERAGE /*2*/:
                pw.print("Low ");
                if (sep != '\u0000') {
                    pw.print(sep);
                }
            case SYS_MEM_USAGE_CACHED_MAXIMUM /*3*/:
                pw.print("Crit");
                if (sep != '\u0000') {
                    pw.print(sep);
                }
            default:
                pw.print("????");
                if (sep != '\u0000') {
                    pw.print(sep);
                }
        }
    }

    public static void printMemLabelCsv(PrintWriter pw, int offset) {
        if (offset < 0) {
            return;
        }
        if (offset <= SYS_MEM_USAGE_CACHED_MAXIMUM) {
            pw.print(ADJ_MEM_NAMES_CSV[offset]);
        } else {
            pw.print("???");
        }
    }

    public static long dumpSingleTime(PrintWriter pw, String prefix, long[] durations, int curState, long curStartTime, long now) {
        long totalTime = 0;
        int printedScreen = STATE_NOTHING;
        int iscreen = SYS_MEM_USAGE_SAMPLE_COUNT;
        while (iscreen < SYS_MEM_USAGE_ZRAM_AVERAGE) {
            int printedMem = STATE_NOTHING;
            int imem = SYS_MEM_USAGE_SAMPLE_COUNT;
            while (imem < SYS_MEM_USAGE_FREE_MINIMUM) {
                int state = imem + iscreen;
                long time = durations[state];
                String running = "";
                if (curState == state) {
                    time += now - curStartTime;
                    if (pw != null) {
                        running = " (running)";
                    }
                }
                if (time != 0) {
                    if (pw != null) {
                        pw.print(prefix);
                        printScreenLabel(pw, printedScreen != iscreen ? iscreen : STATE_NOTHING);
                        printedScreen = iscreen;
                        printMemLabel(pw, printedMem != imem ? imem : STATE_NOTHING, '\u0000');
                        printedMem = imem;
                        pw.print(": ");
                        TimeUtils.formatDuration(time, pw);
                        pw.println(running);
                    }
                    totalTime += time;
                }
                imem += SYS_MEM_USAGE_CACHED_MINIMUM;
            }
            iscreen += SYS_MEM_USAGE_FREE_MINIMUM;
        }
        if (!(totalTime == 0 || pw == null)) {
            pw.print(prefix);
            pw.print("    TOTAL: ");
            TimeUtils.formatDuration(totalTime, pw);
            pw.println();
        }
        return totalTime;
    }

    static void dumpAdjTimesCheckin(PrintWriter pw, String sep, long[] durations, int curState, long curStartTime, long now) {
        for (int iscreen = SYS_MEM_USAGE_SAMPLE_COUNT; iscreen < SYS_MEM_USAGE_ZRAM_AVERAGE; iscreen += SYS_MEM_USAGE_FREE_MINIMUM) {
            for (int imem = SYS_MEM_USAGE_SAMPLE_COUNT; imem < SYS_MEM_USAGE_FREE_MINIMUM; imem += SYS_MEM_USAGE_CACHED_MINIMUM) {
                int state = imem + iscreen;
                long time = durations[state];
                if (curState == state) {
                    time += now - curStartTime;
                }
                if (time != 0) {
                    printAdjTagAndValue(pw, state, time);
                }
            }
        }
    }

    static void dumpServiceTimeCheckin(PrintWriter pw, String label, String packageName, int uid, int vers, String serviceName, ServiceState svc, int serviceType, int opCount, int curState, long curStartTime, long now) {
        if (opCount > 0) {
            pw.print(label);
            pw.print(",");
            pw.print(packageName);
            pw.print(",");
            pw.print(uid);
            pw.print(",");
            pw.print(vers);
            pw.print(",");
            pw.print(serviceName);
            pw.print(",");
            pw.print(opCount);
            boolean didCurState = DEBUG_PARCEL;
            for (int i = SYS_MEM_USAGE_SAMPLE_COUNT; i < svc.mDurationsTableSize; i += SYS_MEM_USAGE_CACHED_MINIMUM) {
                int off = svc.mDurationsTable[i];
                int type = (off >> OFFSET_TYPE_SHIFT) & OFFSET_TYPE_MASK;
                int memFactor = type / SYS_MEM_USAGE_FREE_MINIMUM;
                if (type % SYS_MEM_USAGE_FREE_MINIMUM == serviceType) {
                    long time = svc.mStats.getLong(off, SYS_MEM_USAGE_SAMPLE_COUNT);
                    if (curState == memFactor) {
                        didCurState = true;
                        time += now - curStartTime;
                    }
                    printAdjTagAndValue(pw, memFactor, time);
                }
            }
            if (!(didCurState || curState == STATE_NOTHING)) {
                printAdjTagAndValue(pw, curState, now - curStartTime);
            }
            pw.println();
        }
    }

    public static void computeProcessData(ProcessState proc, ProcessDataCollection data, long now) {
        data.totalTime = 0;
        data.maxUss = 0;
        data.avgUss = 0;
        data.minUss = 0;
        data.maxPss = 0;
        data.avgPss = 0;
        data.minPss = 0;
        data.numPss = 0;
        int is = SYS_MEM_USAGE_SAMPLE_COUNT;
        while (true) {
            int length = data.screenStates.length;
            if (is < r0) {
                int im = SYS_MEM_USAGE_SAMPLE_COUNT;
                while (true) {
                    length = data.memStates.length;
                    if (im >= r0) {
                        break;
                    }
                    int ip = SYS_MEM_USAGE_SAMPLE_COUNT;
                    while (true) {
                        length = data.procStates.length;
                        if (ip >= r0) {
                            break;
                        }
                        int bucket = ((data.screenStates[is] + data.memStates[im]) * SYS_MEM_USAGE_NATIVE_AVERAGE) + data.procStates[ip];
                        data.totalTime += proc.getDuration(bucket, now);
                        long samples = proc.getPssSampleCount(bucket);
                        if (samples > 0) {
                            long minPss = proc.getPssMinimum(bucket);
                            long avgPss = proc.getPssAverage(bucket);
                            long maxPss = proc.getPssMaximum(bucket);
                            long minUss = proc.getPssUssMinimum(bucket);
                            long avgUss = proc.getPssUssAverage(bucket);
                            long maxUss = proc.getPssUssMaximum(bucket);
                            if (data.numPss == 0) {
                                data.minPss = minPss;
                                data.avgPss = avgPss;
                                data.maxPss = maxPss;
                                data.minUss = minUss;
                                data.avgUss = avgUss;
                                data.maxUss = maxUss;
                            } else {
                                if (minPss < data.minPss) {
                                    data.minPss = minPss;
                                }
                                double d = (double) data.avgPss;
                                double d2 = (double) data.numPss;
                                d2 = (double) avgPss;
                                double d3 = (double) samples;
                                data.avgPss = (long) (((r0 * r0) + (r0 * r0)) / ((double) (data.numPss + samples)));
                                if (maxPss > data.maxPss) {
                                    data.maxPss = maxPss;
                                }
                                if (minUss < data.minUss) {
                                    data.minUss = minUss;
                                }
                                d = (double) data.avgUss;
                                d2 = (double) data.numPss;
                                d2 = (double) avgUss;
                                d3 = (double) samples;
                                data.avgUss = (long) (((r0 * r0) + (r0 * r0)) / ((double) (data.numPss + samples)));
                                if (maxUss > data.maxUss) {
                                    data.maxUss = maxUss;
                                }
                            }
                            data.numPss += samples;
                        }
                        ip += SYS_MEM_USAGE_CACHED_MINIMUM;
                    }
                    im += SYS_MEM_USAGE_CACHED_MINIMUM;
                }
                is += SYS_MEM_USAGE_CACHED_MINIMUM;
            } else {
                return;
            }
        }
    }

    static long computeProcessTimeLocked(ProcessState proc, int[] screenStates, int[] memStates, int[] procStates, long now) {
        long totalTime = 0;
        for (int is = SYS_MEM_USAGE_SAMPLE_COUNT; is < screenStates.length; is += SYS_MEM_USAGE_CACHED_MINIMUM) {
            for (int im = SYS_MEM_USAGE_SAMPLE_COUNT; im < memStates.length; im += SYS_MEM_USAGE_CACHED_MINIMUM) {
                for (int ip = SYS_MEM_USAGE_SAMPLE_COUNT; ip < procStates.length; ip += SYS_MEM_USAGE_CACHED_MINIMUM) {
                    totalTime += proc.getDuration(((screenStates[is] + memStates[im]) * SYS_MEM_USAGE_NATIVE_AVERAGE) + procStates[ip], now);
                }
            }
        }
        proc.mTmpTotalTime = totalTime;
        return totalTime;
    }

    public void computeTotalMemoryUse(TotalMemoryUseCollection data, long now) {
        int i;
        data.totalTime = 0;
        for (i = SYS_MEM_USAGE_SAMPLE_COUNT; i < SYS_MEM_USAGE_NATIVE_AVERAGE; i += SYS_MEM_USAGE_CACHED_MINIMUM) {
            data.processStateWeight[i] = 0.0d;
            data.processStatePss[i] = 0;
            data.processStateTime[i] = 0;
            data.processStateSamples[i] = SYS_MEM_USAGE_SAMPLE_COUNT;
        }
        for (i = SYS_MEM_USAGE_SAMPLE_COUNT; i < SYS_MEM_USAGE_COUNT; i += SYS_MEM_USAGE_CACHED_MINIMUM) {
            data.sysMemUsage[i] = 0;
        }
        data.sysMemCachedWeight = 0.0d;
        data.sysMemFreeWeight = 0.0d;
        data.sysMemZRamWeight = 0.0d;
        data.sysMemKernelWeight = 0.0d;
        data.sysMemNativeWeight = 0.0d;
        data.sysMemSamples = SYS_MEM_USAGE_SAMPLE_COUNT;
        long[] totalMemUsage = new long[SYS_MEM_USAGE_COUNT];
        i = SYS_MEM_USAGE_SAMPLE_COUNT;
        while (true) {
            int i2 = this.mSysMemUsageTableSize;
            if (i >= r0) {
                break;
            }
            int ent = this.mSysMemUsageTable[i];
            long[] longs = (long[]) this.mLongs.get((ent >> OFFSET_ARRAY_SHIFT) & OFFSET_ARRAY_MASK);
            addSysMemUsage(totalMemUsage, SYS_MEM_USAGE_SAMPLE_COUNT, longs, (ent >> OFFSET_INDEX_SHIFT) & OFFSET_INDEX_MASK);
            i += SYS_MEM_USAGE_CACHED_MINIMUM;
        }
        for (int is = SYS_MEM_USAGE_SAMPLE_COUNT; is < data.screenStates.length; is += SYS_MEM_USAGE_CACHED_MINIMUM) {
            for (int im = SYS_MEM_USAGE_SAMPLE_COUNT; im < data.memStates.length; im += SYS_MEM_USAGE_CACHED_MINIMUM) {
                int memBucket = data.screenStates[is] + data.memStates[im];
                int stateBucket = memBucket * SYS_MEM_USAGE_NATIVE_AVERAGE;
                long memTime = this.mMemFactorDurations[memBucket];
                i2 = this.mMemFactor;
                if (r0 == memBucket) {
                    memTime += now - this.mStartTime;
                }
                data.totalTime += memTime;
                int sysIdx = binarySearch(this.mSysMemUsageTable, this.mSysMemUsageTableSize, stateBucket);
                longs = totalMemUsage;
                int idx = SYS_MEM_USAGE_SAMPLE_COUNT;
                if (sysIdx >= 0) {
                    ent = this.mSysMemUsageTable[sysIdx];
                    long[] tmpLongs = (long[]) this.mLongs.get((ent >> OFFSET_ARRAY_SHIFT) & OFFSET_ARRAY_MASK);
                    int tmpIdx = (ent >> OFFSET_INDEX_SHIFT) & OFFSET_INDEX_MASK;
                    if (tmpLongs[tmpIdx + SYS_MEM_USAGE_SAMPLE_COUNT] >= 3) {
                        addSysMemUsage(data.sysMemUsage, SYS_MEM_USAGE_SAMPLE_COUNT, longs, SYS_MEM_USAGE_SAMPLE_COUNT);
                        longs = tmpLongs;
                        idx = tmpIdx;
                    }
                }
                data.sysMemCachedWeight += ((double) longs[idx + SYS_MEM_USAGE_CACHED_AVERAGE]) * ((double) memTime);
                data.sysMemFreeWeight += ((double) longs[idx + SYS_MEM_USAGE_FREE_AVERAGE]) * ((double) memTime);
                data.sysMemZRamWeight += ((double) longs[idx + SYS_MEM_USAGE_ZRAM_AVERAGE]) * ((double) memTime);
                data.sysMemKernelWeight += ((double) longs[idx + SYS_MEM_USAGE_KERNEL_AVERAGE]) * ((double) memTime);
                data.sysMemNativeWeight += ((double) longs[idx + SYS_MEM_USAGE_NATIVE_AVERAGE]) * ((double) memTime);
                data.sysMemSamples = (int) (((long) data.sysMemSamples) + longs[idx + SYS_MEM_USAGE_SAMPLE_COUNT]);
            }
        }
        ArrayMap<String, SparseArray<ProcessState>> procMap = this.mProcesses.getMap();
        for (int iproc = SYS_MEM_USAGE_SAMPLE_COUNT; iproc < procMap.size(); iproc += SYS_MEM_USAGE_CACHED_MINIMUM) {
            SparseArray<ProcessState> uids = (SparseArray) procMap.valueAt(iproc);
            for (int iu = SYS_MEM_USAGE_SAMPLE_COUNT; iu < uids.size(); iu += SYS_MEM_USAGE_CACHED_MINIMUM) {
                long avg;
                ProcessState proc = (ProcessState) uids.valueAt(iu);
                PssAggr fgPss = new PssAggr();
                PssAggr bgPss = new PssAggr();
                PssAggr cachedPss = new PssAggr();
                boolean havePss = DEBUG_PARCEL;
                i = SYS_MEM_USAGE_SAMPLE_COUNT;
                while (true) {
                    i2 = proc.mDurationsTableSize;
                    if (i >= r0) {
                        break;
                    }
                    int type = (proc.mDurationsTable[i] >> OFFSET_TYPE_SHIFT) & OFFSET_TYPE_MASK;
                    int procState = type % SYS_MEM_USAGE_NATIVE_AVERAGE;
                    long samples = proc.getPssSampleCount(type);
                    if (samples > 0) {
                        avg = proc.getPssAverage(type);
                        havePss = true;
                        if (procState <= SYS_MEM_USAGE_CACHED_AVERAGE) {
                            fgPss.add(avg, samples);
                        } else if (procState <= SYS_MEM_USAGE_ZRAM_AVERAGE) {
                            bgPss.add(avg, samples);
                        } else {
                            cachedPss.add(avg, samples);
                        }
                    }
                    i += SYS_MEM_USAGE_CACHED_MINIMUM;
                }
                if (havePss) {
                    boolean fgHasBg = DEBUG_PARCEL;
                    boolean fgHasCached = DEBUG_PARCEL;
                    boolean bgHasCached = DEBUG_PARCEL;
                    if (fgPss.samples < 3) {
                        if (bgPss.samples > 0) {
                            fgHasBg = true;
                            fgPss.add(bgPss.pss, bgPss.samples);
                        }
                    }
                    if (fgPss.samples < 3) {
                        if (cachedPss.samples > 0) {
                            fgHasCached = true;
                            fgPss.add(cachedPss.pss, cachedPss.samples);
                        }
                    }
                    if (bgPss.samples < 3) {
                        if (cachedPss.samples > 0) {
                            bgHasCached = true;
                            bgPss.add(cachedPss.pss, cachedPss.samples);
                        }
                    }
                    if (bgPss.samples < 3 && !fgHasBg) {
                        if (fgPss.samples > 0) {
                            bgPss.add(fgPss.pss, fgPss.samples);
                        }
                    }
                    if (cachedPss.samples < 3 && !bgHasCached) {
                        if (bgPss.samples > 0) {
                            cachedPss.add(bgPss.pss, bgPss.samples);
                        }
                    }
                    if (cachedPss.samples < 3 && !fgHasCached) {
                        if (fgPss.samples > 0) {
                            cachedPss.add(fgPss.pss, fgPss.samples);
                        }
                    }
                    i = SYS_MEM_USAGE_SAMPLE_COUNT;
                    while (true) {
                        i2 = proc.mDurationsTableSize;
                        if (i >= r0) {
                            break;
                        }
                        int off = proc.mDurationsTable[i];
                        type = (off >> OFFSET_TYPE_SHIFT) & OFFSET_TYPE_MASK;
                        long time = getLong(off, SYS_MEM_USAGE_SAMPLE_COUNT);
                        i2 = proc.mCurState;
                        if (r0 == type) {
                            time += now - proc.mStartTime;
                        }
                        procState = type % SYS_MEM_USAGE_NATIVE_AVERAGE;
                        long[] jArr = data.processStateTime;
                        jArr[procState] = jArr[procState] + time;
                        samples = proc.getPssSampleCount(type);
                        if (samples > 0) {
                            avg = proc.getPssAverage(type);
                        } else if (procState <= SYS_MEM_USAGE_CACHED_AVERAGE) {
                            samples = fgPss.samples;
                            avg = fgPss.pss;
                        } else if (procState <= SYS_MEM_USAGE_ZRAM_AVERAGE) {
                            samples = bgPss.samples;
                            avg = bgPss.pss;
                        } else {
                            samples = cachedPss.samples;
                            avg = cachedPss.pss;
                        }
                        double d = (double) data.processStatePss[procState];
                        double d2 = (double) data.processStateSamples[procState];
                        d2 = (double) avg;
                        double d3 = (double) samples;
                        double newAvg = ((r0 * r0) + (r0 * r0)) / ((double) (((long) data.processStateSamples[procState]) + samples));
                        data.processStatePss[procState] = (long) newAvg;
                        int[] iArr = data.processStateSamples;
                        iArr[procState] = (int) (((long) iArr[procState]) + samples);
                        double[] dArr = data.processStateWeight;
                        dArr[procState] = dArr[procState] + (((double) avg) * ((double) time));
                        i += SYS_MEM_USAGE_CACHED_MINIMUM;
                    }
                }
            }
        }
    }

    static void dumpProcessState(PrintWriter pw, String prefix, ProcessState proc, int[] screenStates, int[] memStates, int[] procStates, long now) {
        long totalTime = 0;
        int printedScreen = STATE_NOTHING;
        for (int is = SYS_MEM_USAGE_SAMPLE_COUNT; is < screenStates.length; is += SYS_MEM_USAGE_CACHED_MINIMUM) {
            int printedMem = STATE_NOTHING;
            for (int im = SYS_MEM_USAGE_SAMPLE_COUNT; im < memStates.length; im += SYS_MEM_USAGE_CACHED_MINIMUM) {
                for (int ip = SYS_MEM_USAGE_SAMPLE_COUNT; ip < procStates.length; ip += SYS_MEM_USAGE_CACHED_MINIMUM) {
                    int iscreen = screenStates[is];
                    int imem = memStates[im];
                    int bucket = ((iscreen + imem) * SYS_MEM_USAGE_NATIVE_AVERAGE) + procStates[ip];
                    long time = proc.getDuration(bucket, now);
                    String running = "";
                    if (proc.mCurState == bucket) {
                        running = " (running)";
                    }
                    if (time != 0) {
                        pw.print(prefix);
                        if (screenStates.length > SYS_MEM_USAGE_CACHED_MINIMUM) {
                            printScreenLabel(pw, printedScreen != iscreen ? iscreen : STATE_NOTHING);
                            printedScreen = iscreen;
                        }
                        if (memStates.length > SYS_MEM_USAGE_CACHED_MINIMUM) {
                            printMemLabel(pw, printedMem != imem ? imem : STATE_NOTHING, '/');
                            printedMem = imem;
                        }
                        pw.print(STATE_NAMES[procStates[ip]]);
                        pw.print(": ");
                        TimeUtils.formatDuration(time, pw);
                        pw.println(running);
                        totalTime += time;
                    }
                }
            }
        }
        if (totalTime != 0) {
            pw.print(prefix);
            if (screenStates.length > SYS_MEM_USAGE_CACHED_MINIMUM) {
                printScreenLabel(pw, STATE_NOTHING);
            }
            if (memStates.length > SYS_MEM_USAGE_CACHED_MINIMUM) {
                printMemLabel(pw, STATE_NOTHING, '/');
            }
            pw.print("TOTAL  : ");
            TimeUtils.formatDuration(totalTime, pw);
            pw.println();
        }
    }

    static void dumpProcessPss(PrintWriter pw, String prefix, ProcessState proc, int[] screenStates, int[] memStates, int[] procStates) {
        boolean printedHeader = DEBUG_PARCEL;
        int printedScreen = STATE_NOTHING;
        for (int is = SYS_MEM_USAGE_SAMPLE_COUNT; is < screenStates.length; is += SYS_MEM_USAGE_CACHED_MINIMUM) {
            int printedMem = STATE_NOTHING;
            for (int im = SYS_MEM_USAGE_SAMPLE_COUNT; im < memStates.length; im += SYS_MEM_USAGE_CACHED_MINIMUM) {
                for (int ip = SYS_MEM_USAGE_SAMPLE_COUNT; ip < procStates.length; ip += SYS_MEM_USAGE_CACHED_MINIMUM) {
                    int iscreen = screenStates[is];
                    int imem = memStates[im];
                    int bucket = ((iscreen + imem) * SYS_MEM_USAGE_NATIVE_AVERAGE) + procStates[ip];
                    long count = proc.getPssSampleCount(bucket);
                    if (count > 0) {
                        if (!printedHeader) {
                            pw.print(prefix);
                            pw.print("PSS/USS (");
                            pw.print(proc.mPssTableSize);
                            pw.println(" entries):");
                            printedHeader = true;
                        }
                        pw.print(prefix);
                        pw.print("  ");
                        if (screenStates.length > SYS_MEM_USAGE_CACHED_MINIMUM) {
                            printScreenLabel(pw, printedScreen != iscreen ? iscreen : STATE_NOTHING);
                            printedScreen = iscreen;
                        }
                        if (memStates.length > SYS_MEM_USAGE_CACHED_MINIMUM) {
                            printMemLabel(pw, printedMem != imem ? imem : STATE_NOTHING, '/');
                            printedMem = imem;
                        }
                        pw.print(STATE_NAMES[procStates[ip]]);
                        pw.print(": ");
                        pw.print(count);
                        pw.print(" samples ");
                        printSizeValue(pw, proc.getPssMinimum(bucket) * PlaybackState.ACTION_PLAY_FROM_MEDIA_ID);
                        pw.print(" ");
                        printSizeValue(pw, proc.getPssAverage(bucket) * PlaybackState.ACTION_PLAY_FROM_MEDIA_ID);
                        pw.print(" ");
                        printSizeValue(pw, proc.getPssMaximum(bucket) * PlaybackState.ACTION_PLAY_FROM_MEDIA_ID);
                        pw.print(" / ");
                        printSizeValue(pw, proc.getPssUssMinimum(bucket) * PlaybackState.ACTION_PLAY_FROM_MEDIA_ID);
                        pw.print(" ");
                        printSizeValue(pw, proc.getPssUssAverage(bucket) * PlaybackState.ACTION_PLAY_FROM_MEDIA_ID);
                        pw.print(" ");
                        printSizeValue(pw, proc.getPssUssMaximum(bucket) * PlaybackState.ACTION_PLAY_FROM_MEDIA_ID);
                        pw.println();
                    }
                }
            }
        }
        if (proc.mNumExcessiveWake != 0) {
            pw.print(prefix);
            pw.print("Killed for excessive wake locks: ");
            pw.print(proc.mNumExcessiveWake);
            pw.println(" times");
        }
        if (proc.mNumExcessiveCpu != 0) {
            pw.print(prefix);
            pw.print("Killed for excessive CPU use: ");
            pw.print(proc.mNumExcessiveCpu);
            pw.println(" times");
        }
        if (proc.mNumCachedKill != 0) {
            pw.print(prefix);
            pw.print("Killed from cached state: ");
            pw.print(proc.mNumCachedKill);
            pw.print(" times from pss ");
            printSizeValue(pw, proc.mMinCachedKillPss * PlaybackState.ACTION_PLAY_FROM_MEDIA_ID);
            pw.print(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
            printSizeValue(pw, proc.mAvgCachedKillPss * PlaybackState.ACTION_PLAY_FROM_MEDIA_ID);
            pw.print(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
            printSizeValue(pw, proc.mMaxCachedKillPss * PlaybackState.ACTION_PLAY_FROM_MEDIA_ID);
            pw.println();
        }
    }

    long getSysMemUsageValue(int state, int index) {
        int idx = binarySearch(this.mSysMemUsageTable, this.mSysMemUsageTableSize, state);
        return idx >= 0 ? getLong(this.mSysMemUsageTable[idx], index) : 0;
    }

    void dumpSysMemUsageCategory(PrintWriter pw, String prefix, String label, int bucket, int index) {
        pw.print(prefix);
        pw.print(label);
        pw.print(": ");
        printSizeValue(pw, getSysMemUsageValue(bucket, index) * PlaybackState.ACTION_PLAY_FROM_MEDIA_ID);
        pw.print(" min, ");
        printSizeValue(pw, getSysMemUsageValue(bucket, index + SYS_MEM_USAGE_CACHED_MINIMUM) * PlaybackState.ACTION_PLAY_FROM_MEDIA_ID);
        pw.print(" avg, ");
        printSizeValue(pw, getSysMemUsageValue(bucket, index + SYS_MEM_USAGE_CACHED_AVERAGE) * PlaybackState.ACTION_PLAY_FROM_MEDIA_ID);
        pw.println(" max");
    }

    void dumpSysMemUsage(PrintWriter pw, String prefix, int[] screenStates, int[] memStates) {
        int printedScreen = STATE_NOTHING;
        for (int is = SYS_MEM_USAGE_SAMPLE_COUNT; is < screenStates.length; is += SYS_MEM_USAGE_CACHED_MINIMUM) {
            int printedMem = STATE_NOTHING;
            for (int im = SYS_MEM_USAGE_SAMPLE_COUNT; im < memStates.length; im += SYS_MEM_USAGE_CACHED_MINIMUM) {
                int iscreen = screenStates[is];
                int imem = memStates[im];
                int bucket = (iscreen + imem) * SYS_MEM_USAGE_NATIVE_AVERAGE;
                long count = getSysMemUsageValue(bucket, SYS_MEM_USAGE_SAMPLE_COUNT);
                if (count > 0) {
                    pw.print(prefix);
                    if (screenStates.length > SYS_MEM_USAGE_CACHED_MINIMUM) {
                        printScreenLabel(pw, printedScreen != iscreen ? iscreen : STATE_NOTHING);
                        printedScreen = iscreen;
                    }
                    if (memStates.length > SYS_MEM_USAGE_CACHED_MINIMUM) {
                        printMemLabel(pw, printedMem != imem ? imem : STATE_NOTHING, '\u0000');
                        printedMem = imem;
                    }
                    pw.print(": ");
                    pw.print(count);
                    pw.println(" samples:");
                    dumpSysMemUsageCategory(pw, prefix, "  Cached", bucket, SYS_MEM_USAGE_CACHED_MINIMUM);
                    dumpSysMemUsageCategory(pw, prefix, "  Free", bucket, SYS_MEM_USAGE_FREE_MINIMUM);
                    dumpSysMemUsageCategory(pw, prefix, "  ZRam", bucket, SYS_MEM_USAGE_ZRAM_MINIMUM);
                    dumpSysMemUsageCategory(pw, prefix, "  Kernel", bucket, SYS_MEM_USAGE_KERNEL_MINIMUM);
                    dumpSysMemUsageCategory(pw, prefix, "  Native", bucket, SYS_MEM_USAGE_NATIVE_MINIMUM);
                }
            }
        }
    }

    static void dumpStateHeadersCsv(PrintWriter pw, String sep, int[] screenStates, int[] memStates, int[] procStates) {
        int NS;
        int NM;
        int NP;
        if (screenStates != null) {
            NS = screenStates.length;
        } else {
            NS = SYS_MEM_USAGE_CACHED_MINIMUM;
        }
        if (memStates != null) {
            NM = memStates.length;
        } else {
            NM = SYS_MEM_USAGE_CACHED_MINIMUM;
        }
        if (procStates != null) {
            NP = procStates.length;
        } else {
            NP = SYS_MEM_USAGE_CACHED_MINIMUM;
        }
        for (int is = SYS_MEM_USAGE_SAMPLE_COUNT; is < NS; is += SYS_MEM_USAGE_CACHED_MINIMUM) {
            for (int im = SYS_MEM_USAGE_SAMPLE_COUNT; im < NM; im += SYS_MEM_USAGE_CACHED_MINIMUM) {
                for (int ip = SYS_MEM_USAGE_SAMPLE_COUNT; ip < NP; ip += SYS_MEM_USAGE_CACHED_MINIMUM) {
                    pw.print(sep);
                    boolean printed = DEBUG_PARCEL;
                    if (screenStates != null && screenStates.length > SYS_MEM_USAGE_CACHED_MINIMUM) {
                        printScreenLabelCsv(pw, screenStates[is]);
                        printed = true;
                    }
                    if (memStates != null && memStates.length > SYS_MEM_USAGE_CACHED_MINIMUM) {
                        if (printed) {
                            pw.print(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                        }
                        printMemLabelCsv(pw, memStates[im]);
                        printed = true;
                    }
                    if (procStates != null && procStates.length > SYS_MEM_USAGE_CACHED_MINIMUM) {
                        if (printed) {
                            pw.print(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                        }
                        pw.print(STATE_NAMES_CSV[procStates[ip]]);
                    }
                }
            }
        }
    }

    static void dumpProcessStateCsv(PrintWriter pw, ProcessState proc, boolean sepScreenStates, int[] screenStates, boolean sepMemStates, int[] memStates, boolean sepProcStates, int[] procStates, long now) {
        int NSS = sepScreenStates ? screenStates.length : SYS_MEM_USAGE_CACHED_MINIMUM;
        int NMS = sepMemStates ? memStates.length : SYS_MEM_USAGE_CACHED_MINIMUM;
        int NPS = sepProcStates ? procStates.length : SYS_MEM_USAGE_CACHED_MINIMUM;
        int iss = SYS_MEM_USAGE_SAMPLE_COUNT;
        while (iss < NSS) {
            int ims = SYS_MEM_USAGE_SAMPLE_COUNT;
            while (ims < NMS) {
                int ips = SYS_MEM_USAGE_SAMPLE_COUNT;
                while (ips < NPS) {
                    int vsscreen = sepScreenStates ? screenStates[iss] : SYS_MEM_USAGE_SAMPLE_COUNT;
                    int vsmem = sepMemStates ? memStates[ims] : SYS_MEM_USAGE_SAMPLE_COUNT;
                    int vsproc = sepProcStates ? procStates[ips] : SYS_MEM_USAGE_SAMPLE_COUNT;
                    int NSA = sepScreenStates ? SYS_MEM_USAGE_CACHED_MINIMUM : screenStates.length;
                    int NMA = sepMemStates ? SYS_MEM_USAGE_CACHED_MINIMUM : memStates.length;
                    int NPA = sepProcStates ? SYS_MEM_USAGE_CACHED_MINIMUM : procStates.length;
                    long totalTime = 0;
                    int isa = SYS_MEM_USAGE_SAMPLE_COUNT;
                    while (isa < NSA) {
                        int ima = SYS_MEM_USAGE_SAMPLE_COUNT;
                        while (ima < NMA) {
                            int ipa = SYS_MEM_USAGE_SAMPLE_COUNT;
                            while (ipa < NPA) {
                                int vascreen = sepScreenStates ? SYS_MEM_USAGE_SAMPLE_COUNT : screenStates[isa];
                                totalTime += proc.getDuration((((((vsscreen + vascreen) + vsmem) + (sepMemStates ? SYS_MEM_USAGE_SAMPLE_COUNT : memStates[ima])) * SYS_MEM_USAGE_NATIVE_AVERAGE) + vsproc) + (sepProcStates ? SYS_MEM_USAGE_SAMPLE_COUNT : procStates[ipa]), now);
                                ipa += SYS_MEM_USAGE_CACHED_MINIMUM;
                            }
                            ima += SYS_MEM_USAGE_CACHED_MINIMUM;
                        }
                        isa += SYS_MEM_USAGE_CACHED_MINIMUM;
                    }
                    pw.print(CSV_SEP);
                    pw.print(totalTime);
                    ips += SYS_MEM_USAGE_CACHED_MINIMUM;
                }
                ims += SYS_MEM_USAGE_CACHED_MINIMUM;
            }
            iss += SYS_MEM_USAGE_CACHED_MINIMUM;
        }
    }

    static void dumpProcessList(PrintWriter pw, String prefix, ArrayList<ProcessState> procs, int[] screenStates, int[] memStates, int[] procStates, long now) {
        String innerPrefix = prefix + "  ";
        for (int i = procs.size() + STATE_NOTHING; i >= 0; i += STATE_NOTHING) {
            ProcessState proc = (ProcessState) procs.get(i);
            pw.print(prefix);
            pw.print(proc.mName);
            pw.print(" / ");
            UserHandle.formatUid(pw, proc.mUid);
            pw.print(" (");
            pw.print(proc.mDurationsTableSize);
            pw.print(" entries)");
            pw.println(":");
            dumpProcessState(pw, innerPrefix, proc, screenStates, memStates, procStates, now);
            if (proc.mPssTableSize > 0) {
                dumpProcessPss(pw, innerPrefix, proc, screenStates, memStates, procStates);
            }
        }
    }

    static void dumpProcessSummaryDetails(PrintWriter pw, ProcessState proc, String prefix, String label, int[] screenStates, int[] memStates, int[] procStates, long now, long totalTime, boolean full) {
        ProcessDataCollection totals = new ProcessDataCollection(screenStates, memStates, procStates);
        computeProcessData(proc, totals, now);
        if ((((double) totals.totalTime) / ((double) totalTime)) * 100.0d >= 0.005d || totals.numPss != 0) {
            if (prefix != null) {
                pw.print(prefix);
            }
            if (label != null) {
                pw.print(label);
            }
            totals.print(pw, totalTime, full);
            if (prefix != null) {
                pw.println();
            }
        }
    }

    static void dumpProcessSummaryLocked(PrintWriter pw, String prefix, ArrayList<ProcessState> procs, int[] screenStates, int[] memStates, int[] procStates, boolean inclUidVers, long now, long totalTime) {
        for (int i = procs.size() + STATE_NOTHING; i >= 0; i += STATE_NOTHING) {
            ProcessState proc = (ProcessState) procs.get(i);
            pw.print(prefix);
            pw.print("* ");
            pw.print(proc.mName);
            pw.print(" / ");
            UserHandle.formatUid(pw, proc.mUid);
            pw.print(" / v");
            pw.print(proc.mVersion);
            pw.println(":");
            dumpProcessSummaryDetails(pw, proc, prefix, "         TOTAL: ", screenStates, memStates, procStates, now, totalTime, true);
            int[] iArr = new int[SYS_MEM_USAGE_CACHED_MINIMUM];
            iArr[SYS_MEM_USAGE_SAMPLE_COUNT] = SYS_MEM_USAGE_SAMPLE_COUNT;
            dumpProcessSummaryDetails(pw, proc, prefix, "    Persistent: ", screenStates, memStates, iArr, now, totalTime, true);
            iArr = new int[SYS_MEM_USAGE_CACHED_MINIMUM];
            iArr[SYS_MEM_USAGE_SAMPLE_COUNT] = SYS_MEM_USAGE_CACHED_MINIMUM;
            dumpProcessSummaryDetails(pw, proc, prefix, "           Top: ", screenStates, memStates, iArr, now, totalTime, true);
            iArr = new int[SYS_MEM_USAGE_CACHED_MINIMUM];
            iArr[SYS_MEM_USAGE_SAMPLE_COUNT] = SYS_MEM_USAGE_CACHED_AVERAGE;
            dumpProcessSummaryDetails(pw, proc, prefix, "        Imp Fg: ", screenStates, memStates, iArr, now, totalTime, true);
            iArr = new int[SYS_MEM_USAGE_CACHED_MINIMUM];
            iArr[SYS_MEM_USAGE_SAMPLE_COUNT] = SYS_MEM_USAGE_CACHED_MAXIMUM;
            dumpProcessSummaryDetails(pw, proc, prefix, "        Imp Bg: ", screenStates, memStates, iArr, now, totalTime, true);
            iArr = new int[SYS_MEM_USAGE_CACHED_MINIMUM];
            iArr[SYS_MEM_USAGE_SAMPLE_COUNT] = SYS_MEM_USAGE_FREE_MINIMUM;
            dumpProcessSummaryDetails(pw, proc, prefix, "        Backup: ", screenStates, memStates, iArr, now, totalTime, true);
            iArr = new int[SYS_MEM_USAGE_CACHED_MINIMUM];
            iArr[SYS_MEM_USAGE_SAMPLE_COUNT] = SYS_MEM_USAGE_FREE_AVERAGE;
            dumpProcessSummaryDetails(pw, proc, prefix, "     Heavy Wgt: ", screenStates, memStates, iArr, now, totalTime, true);
            iArr = new int[SYS_MEM_USAGE_CACHED_MINIMUM];
            iArr[SYS_MEM_USAGE_SAMPLE_COUNT] = SYS_MEM_USAGE_FREE_MAXIMUM;
            dumpProcessSummaryDetails(pw, proc, prefix, "       Service: ", screenStates, memStates, iArr, now, totalTime, true);
            iArr = new int[SYS_MEM_USAGE_CACHED_MINIMUM];
            iArr[SYS_MEM_USAGE_SAMPLE_COUNT] = SYS_MEM_USAGE_ZRAM_MINIMUM;
            dumpProcessSummaryDetails(pw, proc, prefix, "    Service Rs: ", screenStates, memStates, iArr, now, totalTime, true);
            iArr = new int[SYS_MEM_USAGE_CACHED_MINIMUM];
            iArr[SYS_MEM_USAGE_SAMPLE_COUNT] = SYS_MEM_USAGE_ZRAM_AVERAGE;
            dumpProcessSummaryDetails(pw, proc, prefix, "      Receiver: ", screenStates, memStates, iArr, now, totalTime, true);
            iArr = new int[SYS_MEM_USAGE_CACHED_MINIMUM];
            iArr[SYS_MEM_USAGE_SAMPLE_COUNT] = SYS_MEM_USAGE_ZRAM_MAXIMUM;
            dumpProcessSummaryDetails(pw, proc, prefix, "        (Home): ", screenStates, memStates, iArr, now, totalTime, true);
            iArr = new int[SYS_MEM_USAGE_CACHED_MINIMUM];
            iArr[SYS_MEM_USAGE_SAMPLE_COUNT] = SYS_MEM_USAGE_KERNEL_MINIMUM;
            dumpProcessSummaryDetails(pw, proc, prefix, "    (Last Act): ", screenStates, memStates, iArr, now, totalTime, true);
            dumpProcessSummaryDetails(pw, proc, prefix, "      (Cached): ", screenStates, memStates, new int[]{SYS_MEM_USAGE_KERNEL_AVERAGE, SYS_MEM_USAGE_KERNEL_MAXIMUM, SYS_MEM_USAGE_NATIVE_MINIMUM}, now, totalTime, true);
        }
    }

    static void printPercent(PrintWriter pw, double fraction) {
        fraction *= 100.0d;
        Object[] objArr;
        if (fraction < 1.0d) {
            objArr = new Object[SYS_MEM_USAGE_CACHED_MINIMUM];
            objArr[SYS_MEM_USAGE_SAMPLE_COUNT] = Double.valueOf(fraction);
            pw.print(String.format("%.2f", objArr));
        } else if (fraction < 10.0d) {
            objArr = new Object[SYS_MEM_USAGE_CACHED_MINIMUM];
            objArr[SYS_MEM_USAGE_SAMPLE_COUNT] = Double.valueOf(fraction);
            pw.print(String.format("%.1f", objArr));
        } else {
            objArr = new Object[SYS_MEM_USAGE_CACHED_MINIMUM];
            objArr[SYS_MEM_USAGE_SAMPLE_COUNT] = Double.valueOf(fraction);
            pw.print(String.format("%.0f", objArr));
        }
        pw.print("%");
    }

    static void printSizeValue(PrintWriter pw, long number) {
        String value;
        float result = (float) number;
        String suffix = "";
        if (result > 900.0f) {
            suffix = "KB";
            result /= 1024.0f;
        }
        if (result > 900.0f) {
            suffix = "MB";
            result /= 1024.0f;
        }
        if (result > 900.0f) {
            suffix = "GB";
            result /= 1024.0f;
        }
        if (result > 900.0f) {
            suffix = "TB";
            result /= 1024.0f;
        }
        if (result > 900.0f) {
            suffix = "PB";
            result /= 1024.0f;
        }
        Object[] objArr;
        if (result < RemoteControlClient.PLAYBACK_SPEED_1X) {
            objArr = new Object[SYS_MEM_USAGE_CACHED_MINIMUM];
            objArr[SYS_MEM_USAGE_SAMPLE_COUNT] = Float.valueOf(result);
            value = String.format("%.2f", objArr);
        } else if (result < 10.0f) {
            objArr = new Object[SYS_MEM_USAGE_CACHED_MINIMUM];
            objArr[SYS_MEM_USAGE_SAMPLE_COUNT] = Float.valueOf(result);
            value = String.format("%.1f", objArr);
        } else if (result < 100.0f) {
            objArr = new Object[SYS_MEM_USAGE_CACHED_MINIMUM];
            objArr[SYS_MEM_USAGE_SAMPLE_COUNT] = Float.valueOf(result);
            value = String.format("%.0f", objArr);
        } else {
            objArr = new Object[SYS_MEM_USAGE_CACHED_MINIMUM];
            objArr[SYS_MEM_USAGE_SAMPLE_COUNT] = Float.valueOf(result);
            value = String.format("%.0f", objArr);
        }
        pw.print(value);
        pw.print(suffix);
    }

    public static void dumpProcessListCsv(PrintWriter pw, ArrayList<ProcessState> procs, boolean sepScreenStates, int[] screenStates, boolean sepMemStates, int[] memStates, boolean sepProcStates, int[] procStates, long now) {
        int[] iArr;
        int[] iArr2;
        int[] iArr3;
        pw.print("process");
        pw.print(CSV_SEP);
        pw.print("uid");
        pw.print(CSV_SEP);
        pw.print("vers");
        String str = CSV_SEP;
        if (sepScreenStates) {
            iArr = screenStates;
        } else {
            iArr = null;
        }
        if (sepMemStates) {
            iArr2 = memStates;
        } else {
            iArr2 = null;
        }
        if (sepProcStates) {
            iArr3 = procStates;
        } else {
            iArr3 = null;
        }
        dumpStateHeadersCsv(pw, str, iArr, iArr2, iArr3);
        pw.println();
        for (int i = procs.size() + STATE_NOTHING; i >= 0; i += STATE_NOTHING) {
            ProcessState proc = (ProcessState) procs.get(i);
            pw.print(proc.mName);
            pw.print(CSV_SEP);
            UserHandle.formatUid(pw, proc.mUid);
            pw.print(CSV_SEP);
            pw.print(proc.mVersion);
            dumpProcessStateCsv(pw, proc, sepScreenStates, screenStates, sepMemStates, memStates, sepProcStates, procStates, now);
            pw.println();
        }
    }

    static int printArrayEntry(PrintWriter pw, String[] array, int value, int mod) {
        int index = value / mod;
        if (index < 0 || index >= array.length) {
            pw.print('?');
        } else {
            pw.print(array[index]);
        }
        return value - (index * mod);
    }

    static void printProcStateTag(PrintWriter pw, int state) {
        printArrayEntry(pw, STATE_TAGS, printArrayEntry(pw, ADJ_MEM_TAGS, printArrayEntry(pw, ADJ_SCREEN_TAGS, state, 56), SYS_MEM_USAGE_NATIVE_AVERAGE), SYS_MEM_USAGE_CACHED_MINIMUM);
    }

    static void printAdjTag(PrintWriter pw, int state) {
        printArrayEntry(pw, ADJ_MEM_TAGS, printArrayEntry(pw, ADJ_SCREEN_TAGS, state, SYS_MEM_USAGE_FREE_MINIMUM), SYS_MEM_USAGE_CACHED_MINIMUM);
    }

    static void printProcStateTagAndValue(PrintWriter pw, int state, long value) {
        pw.print(',');
        printProcStateTag(pw, state);
        pw.print(':');
        pw.print(value);
    }

    static void printAdjTagAndValue(PrintWriter pw, int state, long value) {
        pw.print(',');
        printAdjTag(pw, state);
        pw.print(':');
        pw.print(value);
    }

    static void dumpAllProcessStateCheckin(PrintWriter pw, ProcessState proc, long now) {
        boolean didCurState = DEBUG_PARCEL;
        for (int i = SYS_MEM_USAGE_SAMPLE_COUNT; i < proc.mDurationsTableSize; i += SYS_MEM_USAGE_CACHED_MINIMUM) {
            int off = proc.mDurationsTable[i];
            int type = (off >> OFFSET_TYPE_SHIFT) & OFFSET_TYPE_MASK;
            long time = proc.mStats.getLong(off, SYS_MEM_USAGE_SAMPLE_COUNT);
            if (proc.mCurState == type) {
                didCurState = true;
                time += now - proc.mStartTime;
            }
            printProcStateTagAndValue(pw, type, time);
        }
        if (!didCurState && proc.mCurState != STATE_NOTHING) {
            printProcStateTagAndValue(pw, proc.mCurState, now - proc.mStartTime);
        }
    }

    static void dumpAllProcessPssCheckin(PrintWriter pw, ProcessState proc) {
        for (int i = SYS_MEM_USAGE_SAMPLE_COUNT; i < proc.mPssTableSize; i += SYS_MEM_USAGE_CACHED_MINIMUM) {
            int off = proc.mPssTable[i];
            int type = (off >> OFFSET_TYPE_SHIFT) & OFFSET_TYPE_MASK;
            long count = proc.mStats.getLong(off, SYS_MEM_USAGE_SAMPLE_COUNT);
            long min = proc.mStats.getLong(off, SYS_MEM_USAGE_CACHED_MINIMUM);
            long avg = proc.mStats.getLong(off, SYS_MEM_USAGE_CACHED_AVERAGE);
            long max = proc.mStats.getLong(off, SYS_MEM_USAGE_CACHED_MAXIMUM);
            long umin = proc.mStats.getLong(off, SYS_MEM_USAGE_FREE_MINIMUM);
            long uavg = proc.mStats.getLong(off, SYS_MEM_USAGE_FREE_AVERAGE);
            long umax = proc.mStats.getLong(off, SYS_MEM_USAGE_FREE_MAXIMUM);
            pw.print(',');
            printProcStateTag(pw, type);
            pw.print(':');
            pw.print(count);
            pw.print(':');
            pw.print(min);
            pw.print(':');
            pw.print(avg);
            pw.print(':');
            pw.print(max);
            pw.print(':');
            pw.print(umin);
            pw.print(':');
            pw.print(uavg);
            pw.print(':');
            pw.print(umax);
        }
    }

    public void reset() {
        resetCommon();
        this.mPackages.getMap().clear();
        this.mProcesses.getMap().clear();
        this.mMemFactor = STATE_NOTHING;
        this.mStartTime = 0;
    }

    public void resetSafely() {
        int ip;
        int iu;
        resetCommon();
        long now = SystemClock.uptimeMillis();
        ArrayMap<String, SparseArray<ProcessState>> procMap = this.mProcesses.getMap();
        for (ip = procMap.size() + STATE_NOTHING; ip >= 0; ip += STATE_NOTHING) {
            SparseArray<ProcessState> uids = (SparseArray) procMap.valueAt(ip);
            for (iu = uids.size() + STATE_NOTHING; iu >= 0; iu += STATE_NOTHING) {
                ((ProcessState) uids.valueAt(iu)).mTmpNumInUse = SYS_MEM_USAGE_SAMPLE_COUNT;
            }
        }
        ArrayMap<String, SparseArray<SparseArray<PackageState>>> pkgMap = this.mPackages.getMap();
        for (ip = pkgMap.size() + STATE_NOTHING; ip >= 0; ip += STATE_NOTHING) {
            SparseArray<SparseArray<PackageState>> uids2 = (SparseArray) pkgMap.valueAt(ip);
            for (iu = uids2.size() + STATE_NOTHING; iu >= 0; iu += STATE_NOTHING) {
                SparseArray<PackageState> vpkgs = (SparseArray) uids2.valueAt(iu);
                for (int iv = vpkgs.size() + STATE_NOTHING; iv >= 0; iv += STATE_NOTHING) {
                    PackageState pkgState = (PackageState) vpkgs.valueAt(iv);
                    for (int iproc = pkgState.mProcesses.size() + STATE_NOTHING; iproc >= 0; iproc += STATE_NOTHING) {
                        ProcessState ps = (ProcessState) pkgState.mProcesses.valueAt(iproc);
                        if (ps.isInUse()) {
                            ps.resetSafely(now);
                            ProcessState processState = ps.mCommonProcess;
                            processState.mTmpNumInUse += SYS_MEM_USAGE_CACHED_MINIMUM;
                            ps.mCommonProcess.mTmpFoundSubProc = ps;
                        } else {
                            ((ProcessState) pkgState.mProcesses.valueAt(iproc)).makeDead();
                            pkgState.mProcesses.removeAt(iproc);
                        }
                    }
                    for (int isvc = pkgState.mServices.size() + STATE_NOTHING; isvc >= 0; isvc += STATE_NOTHING) {
                        ServiceState ss = (ServiceState) pkgState.mServices.valueAt(isvc);
                        if (ss.isInUse()) {
                            ss.resetSafely(now);
                        } else {
                            pkgState.mServices.removeAt(isvc);
                        }
                    }
                    if (pkgState.mProcesses.size() <= 0) {
                        if (pkgState.mServices.size() <= 0) {
                            vpkgs.removeAt(iv);
                        }
                    }
                }
                if (vpkgs.size() <= 0) {
                    uids2.removeAt(iu);
                }
            }
            if (uids2.size() <= 0) {
                pkgMap.removeAt(ip);
            }
        }
        for (ip = procMap.size() + STATE_NOTHING; ip >= 0; ip += STATE_NOTHING) {
            uids = (SparseArray) procMap.valueAt(ip);
            for (iu = uids.size() + STATE_NOTHING; iu >= 0; iu += STATE_NOTHING) {
                ps = (ProcessState) uids.valueAt(iu);
                if (ps.isInUse() || ps.mTmpNumInUse > 0) {
                    if (!ps.mActive && ps.mMultiPackage) {
                        int i = ps.mTmpNumInUse;
                        if (r0 == SYS_MEM_USAGE_CACHED_MINIMUM) {
                            ps = ps.mTmpFoundSubProc;
                            ps.mCommonProcess = ps;
                            uids.setValueAt(iu, ps);
                        }
                    }
                    ps.resetSafely(now);
                } else {
                    ps.makeDead();
                    uids.removeAt(iu);
                }
            }
            if (uids.size() <= 0) {
                procMap.removeAt(ip);
            }
        }
        this.mStartTime = now;
    }

    private void resetCommon() {
        this.mTimePeriodStartClock = System.currentTimeMillis();
        buildTimePeriodStartClockStr();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        this.mTimePeriodEndRealtime = elapsedRealtime;
        this.mTimePeriodStartRealtime = elapsedRealtime;
        elapsedRealtime = SystemClock.uptimeMillis();
        this.mTimePeriodEndUptime = elapsedRealtime;
        this.mTimePeriodStartUptime = elapsedRealtime;
        this.mLongs.clear();
        this.mLongs.add(new long[LONGS_SIZE]);
        this.mNextLong = SYS_MEM_USAGE_SAMPLE_COUNT;
        Arrays.fill(this.mMemFactorDurations, 0);
        this.mSysMemUsageTable = null;
        this.mSysMemUsageTableSize = SYS_MEM_USAGE_SAMPLE_COUNT;
        this.mStartTime = 0;
        this.mReadError = null;
        this.mFlags = SYS_MEM_USAGE_SAMPLE_COUNT;
        evaluateSystemProperties(true);
    }

    public boolean evaluateSystemProperties(boolean update) {
        boolean changed = DEBUG_PARCEL;
        String runtime = SystemProperties.get("persist.sys.dalvik.vm.lib.2", VMRuntime.getRuntime().vmLibrary());
        if (!Objects.equals(runtime, this.mRuntime)) {
            changed = true;
            if (update) {
                this.mRuntime = runtime;
            }
        }
        return changed;
    }

    private void buildTimePeriodStartClockStr() {
        this.mTimePeriodStartClockStr = DateFormat.format((CharSequence) "yyyy-MM-dd-HH-mm-ss", this.mTimePeriodStartClock).toString();
    }

    private int[] readTableFromParcel(Parcel in, String name, String what) {
        int size = in.readInt();
        if (size < 0) {
            Slog.w(TAG, "Ignoring existing stats; bad " + what + " table size: " + size);
            return BAD_TABLE;
        } else if (size == 0) {
            return null;
        } else {
            int[] table = new int[size];
            int i = SYS_MEM_USAGE_SAMPLE_COUNT;
            while (i < size) {
                table[i] = in.readInt();
                if (validateLongOffset(table[i])) {
                    i += SYS_MEM_USAGE_CACHED_MINIMUM;
                } else {
                    Slog.w(TAG, "Ignoring existing stats; bad " + what + " table entry: " + printLongOffset(table[i]));
                    return null;
                }
            }
            return table;
        }
    }

    private void writeCompactedLongArray(Parcel out, long[] array, int num) {
        for (int i = SYS_MEM_USAGE_SAMPLE_COUNT; i < num; i += SYS_MEM_USAGE_CACHED_MINIMUM) {
            long val = array[i];
            if (val < 0) {
                Slog.w(TAG, "Time val negative: " + val);
                val = 0;
            }
            if (val <= 2147483647L) {
                out.writeInt((int) val);
            } else {
                int bottom = (int) (268435455 & val);
                out.writeInt(((int) ((val >> 32) & 2147483647L)) ^ STATE_NOTHING);
                out.writeInt(bottom);
            }
        }
    }

    private void readCompactedLongArray(Parcel in, int version, long[] array, int num) {
        if (version <= SYS_MEM_USAGE_KERNEL_MINIMUM) {
            in.readLongArray(array);
            return;
        }
        int alen = array.length;
        if (num > alen) {
            throw new RuntimeException("bad array lengths: got " + num + " array is " + alen);
        }
        int i = SYS_MEM_USAGE_SAMPLE_COUNT;
        while (i < num) {
            int val = in.readInt();
            if (val >= 0) {
                array[i] = (long) val;
            } else {
                array[i] = (((long) (val ^ STATE_NOTHING)) << 32) | ((long) in.readInt());
            }
            i += SYS_MEM_USAGE_CACHED_MINIMUM;
        }
        while (i < alen) {
            array[i] = 0;
            i += SYS_MEM_USAGE_CACHED_MINIMUM;
        }
    }

    private void writeCommonString(Parcel out, String name) {
        Integer index = (Integer) this.mCommonStringToIndex.get(name);
        if (index != null) {
            out.writeInt(index.intValue());
            return;
        }
        index = Integer.valueOf(this.mCommonStringToIndex.size());
        this.mCommonStringToIndex.put(name, index);
        out.writeInt(index.intValue() ^ STATE_NOTHING);
        out.writeString(name);
    }

    private String readCommonString(Parcel in, int version) {
        if (version <= SYS_MEM_USAGE_ZRAM_MAXIMUM) {
            return in.readString();
        }
        int index = in.readInt();
        if (index >= 0) {
            return (String) this.mIndexToCommonString.get(index);
        }
        index ^= STATE_NOTHING;
        String name = in.readString();
        while (this.mIndexToCommonString.size() <= index) {
            this.mIndexToCommonString.add(null);
        }
        this.mIndexToCommonString.set(index, name);
        return name;
    }

    public int describeContents() {
        return SYS_MEM_USAGE_SAMPLE_COUNT;
    }

    public void writeToParcel(Parcel out, int flags) {
        writeToParcel(out, SystemClock.uptimeMillis(), flags);
    }

    public void writeToParcel(Parcel out, long now, int flags) {
        int ip;
        int iu;
        int isvc;
        out.writeInt(MAGIC);
        out.writeInt(PARCEL_VERSION);
        out.writeInt(SYS_MEM_USAGE_NATIVE_AVERAGE);
        out.writeInt(SYS_MEM_USAGE_ZRAM_AVERAGE);
        out.writeInt(SYS_MEM_USAGE_ZRAM_MINIMUM);
        out.writeInt(SYS_MEM_USAGE_COUNT);
        out.writeInt(LONGS_SIZE);
        this.mCommonStringToIndex = new ArrayMap(this.mProcesses.mMap.size());
        ArrayMap<String, SparseArray<ProcessState>> procMap = this.mProcesses.getMap();
        int NPROC = procMap.size();
        for (ip = SYS_MEM_USAGE_SAMPLE_COUNT; ip < NPROC; ip += SYS_MEM_USAGE_CACHED_MINIMUM) {
            SparseArray<ProcessState> uids = (SparseArray) procMap.valueAt(ip);
            int NUID = uids.size();
            for (iu = SYS_MEM_USAGE_SAMPLE_COUNT; iu < NUID; iu += SYS_MEM_USAGE_CACHED_MINIMUM) {
                ((ProcessState) uids.valueAt(iu)).commitStateTime(now);
            }
        }
        ArrayMap<String, SparseArray<SparseArray<PackageState>>> pkgMap = this.mPackages.getMap();
        int NPKG = pkgMap.size();
        for (ip = SYS_MEM_USAGE_SAMPLE_COUNT; ip < NPKG; ip += SYS_MEM_USAGE_CACHED_MINIMUM) {
            SparseArray<SparseArray<PackageState>> uids2 = (SparseArray) pkgMap.valueAt(ip);
            NUID = uids2.size();
            for (iu = SYS_MEM_USAGE_SAMPLE_COUNT; iu < NUID; iu += SYS_MEM_USAGE_CACHED_MINIMUM) {
                int iv;
                SparseArray<PackageState> vpkgs = (SparseArray) uids2.valueAt(iu);
                int NVERS = vpkgs.size();
                for (iv = SYS_MEM_USAGE_SAMPLE_COUNT; iv < NVERS; iv += SYS_MEM_USAGE_CACHED_MINIMUM) {
                    int iproc;
                    PackageState pkgState = (PackageState) vpkgs.valueAt(iv);
                    int NPROCS = pkgState.mProcesses.size();
                    for (iproc = SYS_MEM_USAGE_SAMPLE_COUNT; iproc < NPROCS; iproc += SYS_MEM_USAGE_CACHED_MINIMUM) {
                        ProcessState proc = (ProcessState) pkgState.mProcesses.valueAt(iproc);
                        ProcessState processState = proc.mCommonProcess;
                        if (r0 != proc) {
                            proc.commitStateTime(now);
                        }
                    }
                    int NSRVS = pkgState.mServices.size();
                    for (isvc = SYS_MEM_USAGE_SAMPLE_COUNT; isvc < NSRVS; isvc += SYS_MEM_USAGE_CACHED_MINIMUM) {
                        ((ServiceState) pkgState.mServices.valueAt(isvc)).commitStateTime(now);
                    }
                }
            }
        }
        out.writeLong(this.mTimePeriodStartClock);
        out.writeLong(this.mTimePeriodStartRealtime);
        out.writeLong(this.mTimePeriodEndRealtime);
        out.writeLong(this.mTimePeriodStartUptime);
        out.writeLong(this.mTimePeriodEndUptime);
        out.writeString(this.mRuntime);
        out.writeInt(this.mFlags);
        out.writeInt(this.mLongs.size());
        out.writeInt(this.mNextLong);
        int i = SYS_MEM_USAGE_SAMPLE_COUNT;
        while (true) {
            if (i >= this.mLongs.size() + STATE_NOTHING) {
                break;
            }
            long[] array = (long[]) this.mLongs.get(i);
            writeCompactedLongArray(out, array, array.length);
            i += SYS_MEM_USAGE_CACHED_MINIMUM;
        }
        writeCompactedLongArray(out, (long[]) this.mLongs.get(this.mLongs.size() + STATE_NOTHING), this.mNextLong);
        int i2 = this.mMemFactor;
        if (r0 != STATE_NOTHING) {
            long[] jArr = this.mMemFactorDurations;
            int i3 = this.mMemFactor;
            jArr[i3] = jArr[i3] + (now - this.mStartTime);
            this.mStartTime = now;
        }
        writeCompactedLongArray(out, this.mMemFactorDurations, this.mMemFactorDurations.length);
        out.writeInt(this.mSysMemUsageTableSize);
        i = SYS_MEM_USAGE_SAMPLE_COUNT;
        while (true) {
            i2 = this.mSysMemUsageTableSize;
            if (i >= r0) {
                break;
            }
            out.writeInt(this.mSysMemUsageTable[i]);
            i += SYS_MEM_USAGE_CACHED_MINIMUM;
        }
        out.writeInt(NPROC);
        for (ip = SYS_MEM_USAGE_SAMPLE_COUNT; ip < NPROC; ip += SYS_MEM_USAGE_CACHED_MINIMUM) {
            writeCommonString(out, (String) procMap.keyAt(ip));
            uids = (SparseArray) procMap.valueAt(ip);
            NUID = uids.size();
            out.writeInt(NUID);
            for (iu = SYS_MEM_USAGE_SAMPLE_COUNT; iu < NUID; iu += SYS_MEM_USAGE_CACHED_MINIMUM) {
                out.writeInt(uids.keyAt(iu));
                proc = (ProcessState) uids.valueAt(iu);
                writeCommonString(out, proc.mPackage);
                out.writeInt(proc.mVersion);
                proc.writeToParcel(out, now);
            }
        }
        out.writeInt(NPKG);
        for (ip = SYS_MEM_USAGE_SAMPLE_COUNT; ip < NPKG; ip += SYS_MEM_USAGE_CACHED_MINIMUM) {
            writeCommonString(out, (String) pkgMap.keyAt(ip));
            uids2 = (SparseArray) pkgMap.valueAt(ip);
            NUID = uids2.size();
            out.writeInt(NUID);
            for (iu = SYS_MEM_USAGE_SAMPLE_COUNT; iu < NUID; iu += SYS_MEM_USAGE_CACHED_MINIMUM) {
                out.writeInt(uids2.keyAt(iu));
                vpkgs = (SparseArray) uids2.valueAt(iu);
                NVERS = vpkgs.size();
                out.writeInt(NVERS);
                for (iv = SYS_MEM_USAGE_SAMPLE_COUNT; iv < NVERS; iv += SYS_MEM_USAGE_CACHED_MINIMUM) {
                    out.writeInt(vpkgs.keyAt(iv));
                    pkgState = (PackageState) vpkgs.valueAt(iv);
                    NPROCS = pkgState.mProcesses.size();
                    out.writeInt(NPROCS);
                    for (iproc = SYS_MEM_USAGE_SAMPLE_COUNT; iproc < NPROCS; iproc += SYS_MEM_USAGE_CACHED_MINIMUM) {
                        writeCommonString(out, (String) pkgState.mProcesses.keyAt(iproc));
                        proc = (ProcessState) pkgState.mProcesses.valueAt(iproc);
                        processState = proc.mCommonProcess;
                        if (r0 == proc) {
                            out.writeInt(SYS_MEM_USAGE_SAMPLE_COUNT);
                        } else {
                            out.writeInt(SYS_MEM_USAGE_CACHED_MINIMUM);
                            proc.writeToParcel(out, now);
                        }
                    }
                    NSRVS = pkgState.mServices.size();
                    out.writeInt(NSRVS);
                    for (isvc = SYS_MEM_USAGE_SAMPLE_COUNT; isvc < NSRVS; isvc += SYS_MEM_USAGE_CACHED_MINIMUM) {
                        out.writeString((String) pkgState.mServices.keyAt(isvc));
                        ServiceState svc = (ServiceState) pkgState.mServices.valueAt(isvc);
                        writeCommonString(out, svc.mProcessName);
                        svc.writeToParcel(out, now);
                    }
                }
            }
        }
        this.mCommonStringToIndex = null;
    }

    private boolean readCheckedInt(Parcel in, int val, String what) {
        int got = in.readInt();
        if (got == val) {
            return true;
        }
        this.mReadError = "bad " + what + ": " + got;
        return DEBUG_PARCEL;
    }

    static byte[] readFully(InputStream stream, int[] outLen) throws IOException {
        int pos = SYS_MEM_USAGE_SAMPLE_COUNT;
        int initialAvail = stream.available();
        byte[] data = new byte[(initialAvail > 0 ? initialAvail + SYS_MEM_USAGE_CACHED_MINIMUM : GL10.GL_LIGHT0)];
        while (true) {
            int amt = stream.read(data, pos, data.length - pos);
            if (amt < 0) {
                outLen[SYS_MEM_USAGE_SAMPLE_COUNT] = pos;
                return data;
            }
            pos += amt;
            if (pos >= data.length) {
                byte[] newData = new byte[(pos + GL10.GL_LIGHT0)];
                System.arraycopy(data, SYS_MEM_USAGE_SAMPLE_COUNT, newData, SYS_MEM_USAGE_SAMPLE_COUNT, pos);
                data = newData;
            }
        }
    }

    public void read(InputStream stream) {
        try {
            int[] len = new int[SYS_MEM_USAGE_CACHED_MINIMUM];
            byte[] raw = readFully(stream, len);
            Parcel in = Parcel.obtain();
            in.unmarshall(raw, SYS_MEM_USAGE_SAMPLE_COUNT, len[SYS_MEM_USAGE_SAMPLE_COUNT]);
            in.setDataPosition(SYS_MEM_USAGE_SAMPLE_COUNT);
            stream.close();
            readFromParcel(in);
        } catch (IOException e) {
            this.mReadError = "caught exception: " + e;
        }
    }

    public void readFromParcel(Parcel in) {
        boolean hadData = (this.mPackages.getMap().size() > 0 || this.mProcesses.getMap().size() > 0) ? true : DEBUG_PARCEL;
        if (hadData) {
            resetSafely();
        }
        if (readCheckedInt(in, MAGIC, "magic number")) {
            int version = in.readInt();
            if (version != PARCEL_VERSION) {
                this.mReadError = "bad version: " + version;
                return;
            }
            if (readCheckedInt(in, SYS_MEM_USAGE_NATIVE_AVERAGE, "state count")) {
                if (readCheckedInt(in, SYS_MEM_USAGE_ZRAM_AVERAGE, "adj count")) {
                    if (readCheckedInt(in, SYS_MEM_USAGE_ZRAM_MINIMUM, "pss count")) {
                        if (readCheckedInt(in, SYS_MEM_USAGE_COUNT, "sys mem usage count")) {
                            if (readCheckedInt(in, LONGS_SIZE, "longs size")) {
                                this.mIndexToCommonString = new ArrayList();
                                this.mTimePeriodStartClock = in.readLong();
                                buildTimePeriodStartClockStr();
                                this.mTimePeriodStartRealtime = in.readLong();
                                this.mTimePeriodEndRealtime = in.readLong();
                                this.mTimePeriodStartUptime = in.readLong();
                                this.mTimePeriodEndUptime = in.readLong();
                                this.mRuntime = in.readString();
                                this.mFlags = in.readInt();
                                int NLONGS = in.readInt();
                                int NEXTLONG = in.readInt();
                                this.mLongs.clear();
                                for (int i = SYS_MEM_USAGE_SAMPLE_COUNT; i < NLONGS + STATE_NOTHING; i += SYS_MEM_USAGE_CACHED_MINIMUM) {
                                    while (i >= this.mLongs.size()) {
                                        this.mLongs.add(new long[LONGS_SIZE]);
                                    }
                                    readCompactedLongArray(in, version, (long[]) this.mLongs.get(i), LONGS_SIZE);
                                }
                                Object longs = new long[LONGS_SIZE];
                                this.mNextLong = NEXTLONG;
                                readCompactedLongArray(in, version, longs, NEXTLONG);
                                this.mLongs.add(longs);
                                readCompactedLongArray(in, version, this.mMemFactorDurations, this.mMemFactorDurations.length);
                                this.mSysMemUsageTable = readTableFromParcel(in, TAG, "sys mem usage");
                                if (this.mSysMemUsageTable != BAD_TABLE) {
                                    String procName;
                                    int NUID;
                                    int uid;
                                    String pkgName;
                                    int vers;
                                    ProcessState proc;
                                    this.mSysMemUsageTableSize = this.mSysMemUsageTable != null ? this.mSysMemUsageTable.length : SYS_MEM_USAGE_SAMPLE_COUNT;
                                    int NPROC = in.readInt();
                                    if (NPROC < 0) {
                                        this.mReadError = "bad process count: " + NPROC;
                                        return;
                                    }
                                    while (NPROC > 0) {
                                        NPROC += STATE_NOTHING;
                                        procName = readCommonString(in, version);
                                        if (procName == null) {
                                            this.mReadError = "bad process name";
                                            return;
                                        }
                                        NUID = in.readInt();
                                        if (NUID < 0) {
                                            this.mReadError = "bad uid count: " + NUID;
                                            return;
                                        }
                                        while (NUID > 0) {
                                            NUID += STATE_NOTHING;
                                            uid = in.readInt();
                                            if (uid < 0) {
                                                this.mReadError = "bad uid: " + uid;
                                                return;
                                            }
                                            pkgName = readCommonString(in, version);
                                            if (pkgName == null) {
                                                this.mReadError = "bad process package name";
                                                return;
                                            }
                                            vers = in.readInt();
                                            proc = hadData ? (ProcessState) this.mProcesses.get(procName, uid) : null;
                                            if (proc == null) {
                                                proc = new ProcessState(this, pkgName, uid, vers, procName);
                                                if (!proc.readFromParcel(in, true)) {
                                                    return;
                                                }
                                            } else if (!proc.readFromParcel(in, DEBUG_PARCEL)) {
                                                return;
                                            }
                                            this.mProcesses.put(procName, uid, proc);
                                        }
                                    }
                                    int NPKG = in.readInt();
                                    if (NPKG < 0) {
                                        this.mReadError = "bad package count: " + NPKG;
                                        return;
                                    }
                                    while (NPKG > 0) {
                                        NPKG += STATE_NOTHING;
                                        pkgName = readCommonString(in, version);
                                        if (pkgName == null) {
                                            this.mReadError = "bad package name";
                                            return;
                                        }
                                        NUID = in.readInt();
                                        if (NUID < 0) {
                                            this.mReadError = "bad uid count: " + NUID;
                                            return;
                                        }
                                        while (NUID > 0) {
                                            NUID += STATE_NOTHING;
                                            uid = in.readInt();
                                            if (uid < 0) {
                                                this.mReadError = "bad uid: " + uid;
                                                return;
                                            }
                                            int NVERS = in.readInt();
                                            if (NVERS < 0) {
                                                this.mReadError = "bad versions count: " + NVERS;
                                                return;
                                            }
                                            while (NVERS > 0) {
                                                NVERS += STATE_NOTHING;
                                                vers = in.readInt();
                                                PackageState packageState = new PackageState(pkgName, uid);
                                                SparseArray<PackageState> vpkg = (SparseArray) this.mPackages.get(pkgName, uid);
                                                if (vpkg == null) {
                                                    vpkg = new SparseArray();
                                                    this.mPackages.put(pkgName, uid, vpkg);
                                                }
                                                vpkg.put(vers, packageState);
                                                int NPROCS = in.readInt();
                                                if (NPROCS < 0) {
                                                    this.mReadError = "bad package process count: " + NPROCS;
                                                    return;
                                                }
                                                while (NPROCS > 0) {
                                                    NPROCS += STATE_NOTHING;
                                                    procName = readCommonString(in, version);
                                                    if (procName == null) {
                                                        this.mReadError = "bad package process name";
                                                        return;
                                                    }
                                                    int hasProc = in.readInt();
                                                    ProcessState commonProc = (ProcessState) this.mProcesses.get(procName, uid);
                                                    if (commonProc == null) {
                                                        this.mReadError = "no common proc: " + procName;
                                                        return;
                                                    } else if (hasProc != 0) {
                                                        proc = hadData ? (ProcessState) packageState.mProcesses.get(procName) : null;
                                                        if (proc == null) {
                                                            proc = new ProcessState(commonProc, pkgName, uid, vers, procName, null);
                                                            if (!proc.readFromParcel(in, true)) {
                                                                return;
                                                            }
                                                        } else if (!proc.readFromParcel(in, DEBUG_PARCEL)) {
                                                            return;
                                                        }
                                                        packageState.mProcesses.put(procName, proc);
                                                    } else {
                                                        packageState.mProcesses.put(procName, commonProc);
                                                    }
                                                }
                                                int NSRVS = in.readInt();
                                                if (NSRVS < 0) {
                                                    this.mReadError = "bad package service count: " + NSRVS;
                                                    return;
                                                }
                                                while (NSRVS > 0) {
                                                    NSRVS += STATE_NOTHING;
                                                    String serviceName = in.readString();
                                                    if (serviceName == null) {
                                                        this.mReadError = "bad package service name";
                                                        return;
                                                    }
                                                    String processName = version > SYS_MEM_USAGE_ZRAM_MAXIMUM ? readCommonString(in, version) : null;
                                                    ServiceState serv = hadData ? (ServiceState) packageState.mServices.get(serviceName) : null;
                                                    if (serv == null) {
                                                        serv = new ServiceState(this, pkgName, serviceName, processName, null);
                                                    }
                                                    if (serv.readFromParcel(in)) {
                                                        packageState.mServices.put(serviceName, serv);
                                                    } else {
                                                        return;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    this.mIndexToCommonString = null;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    int addLongData(int index, int type, int num) {
        int off = allocLongData(num);
        this.mAddLongTable = GrowingArrayUtils.insert(this.mAddLongTable != null ? this.mAddLongTable : EmptyArray.INT, this.mAddLongTableSize, index, type | off);
        this.mAddLongTableSize += SYS_MEM_USAGE_CACHED_MINIMUM;
        return off;
    }

    int allocLongData(int num) {
        int whichLongs = this.mLongs.size() + STATE_NOTHING;
        if (this.mNextLong + num > ((long[]) this.mLongs.get(whichLongs)).length) {
            this.mLongs.add(new long[LONGS_SIZE]);
            whichLongs += SYS_MEM_USAGE_CACHED_MINIMUM;
            this.mNextLong = SYS_MEM_USAGE_SAMPLE_COUNT;
        }
        int off = (whichLongs << OFFSET_ARRAY_SHIFT) | (this.mNextLong << OFFSET_INDEX_SHIFT);
        this.mNextLong += num;
        return off;
    }

    boolean validateLongOffset(int off) {
        if (((off >> OFFSET_ARRAY_SHIFT) & OFFSET_ARRAY_MASK) < this.mLongs.size() && ((off >> OFFSET_INDEX_SHIFT) & OFFSET_INDEX_MASK) < LONGS_SIZE) {
            return true;
        }
        return DEBUG_PARCEL;
    }

    static String printLongOffset(int off) {
        StringBuilder sb = new StringBuilder(SYS_MEM_USAGE_COUNT);
        sb.append("a");
        sb.append((off >> OFFSET_ARRAY_SHIFT) & OFFSET_ARRAY_MASK);
        sb.append("i");
        sb.append((off >> OFFSET_INDEX_SHIFT) & OFFSET_INDEX_MASK);
        sb.append("t");
        sb.append((off >> OFFSET_TYPE_SHIFT) & OFFSET_TYPE_MASK);
        return sb.toString();
    }

    void setLong(int off, int index, long value) {
        ((long[]) this.mLongs.get((off >> OFFSET_ARRAY_SHIFT) & OFFSET_ARRAY_MASK))[((off >> OFFSET_INDEX_SHIFT) & OFFSET_INDEX_MASK) + index] = value;
    }

    long getLong(int off, int index) {
        return ((long[]) this.mLongs.get((off >> OFFSET_ARRAY_SHIFT) & OFFSET_ARRAY_MASK))[((off >> OFFSET_INDEX_SHIFT) & OFFSET_INDEX_MASK) + index];
    }

    static int binarySearch(int[] array, int size, int value) {
        int lo = SYS_MEM_USAGE_SAMPLE_COUNT;
        int hi = size + STATE_NOTHING;
        while (lo <= hi) {
            int i = (lo + hi) >>> SYS_MEM_USAGE_CACHED_MINIMUM;
            int midVal = (array[i] >> OFFSET_TYPE_SHIFT) & OFFSET_TYPE_MASK;
            if (midVal < value) {
                lo = i + SYS_MEM_USAGE_CACHED_MINIMUM;
            } else if (midVal <= value) {
                return i;
            } else {
                hi = i + STATE_NOTHING;
            }
        }
        return lo ^ STATE_NOTHING;
    }

    public PackageState getPackageStateLocked(String packageName, int uid, int vers) {
        SparseArray<PackageState> vpkg = (SparseArray) this.mPackages.get(packageName, uid);
        if (vpkg == null) {
            vpkg = new SparseArray();
            this.mPackages.put(packageName, uid, vpkg);
        }
        PackageState as = (PackageState) vpkg.get(vers);
        if (as != null) {
            return as;
        }
        as = new PackageState(packageName, uid);
        vpkg.put(vers, as);
        return as;
    }

    public ProcessState getProcessStateLocked(String packageName, int uid, int vers, String processName) {
        PackageState pkgState = getPackageStateLocked(packageName, uid, vers);
        ProcessState ps = (ProcessState) pkgState.mProcesses.get(processName);
        if (ps != null) {
            return ps;
        }
        ProcessState commonProc = (ProcessState) this.mProcesses.get(processName, uid);
        if (commonProc == null) {
            commonProc = new ProcessState(this, packageName, uid, vers, processName);
            this.mProcesses.put(processName, uid, commonProc);
        }
        if (commonProc.mMultiPackage) {
            ProcessState processState = new ProcessState(commonProc, packageName, uid, vers, processName, SystemClock.uptimeMillis());
        } else {
            if (packageName.equals(commonProc.mPackage) && vers == commonProc.mVersion) {
                ps = commonProc;
            } else {
                commonProc.mMultiPackage = true;
                long now = SystemClock.uptimeMillis();
                PackageState commonPkgState = getPackageStateLocked(commonProc.mPackage, uid, commonProc.mVersion);
                if (commonPkgState != null) {
                    ProcessState cloned = commonProc.clone(commonProc.mPackage, now);
                    commonPkgState.mProcesses.put(commonProc.mName, cloned);
                    for (int i = commonPkgState.mServices.size() + STATE_NOTHING; i >= 0; i += STATE_NOTHING) {
                        ServiceState ss = (ServiceState) commonPkgState.mServices.valueAt(i);
                        if (ss.mProc == commonProc) {
                            ss.mProc = cloned;
                        }
                    }
                } else {
                    Slog.w(TAG, "Cloning proc state: no package state " + commonProc.mPackage + "/" + uid + " for proc " + commonProc.mName);
                }
                ps = new ProcessState(commonProc, packageName, uid, vers, processName, now);
            }
        }
        pkgState.mProcesses.put(processName, ps);
        return ps;
    }

    public ServiceState getServiceStateLocked(String packageName, int uid, int vers, String processName, String className) {
        PackageState as = getPackageStateLocked(packageName, uid, vers);
        ServiceState ss = (ServiceState) as.mServices.get(className);
        if (ss != null) {
            return ss;
        }
        ss = new ServiceState(this, packageName, className, processName, processName != null ? getProcessStateLocked(packageName, uid, vers, processName) : null);
        as.mServices.put(className, ss);
        return ss;
    }

    private void dumpProcessInternalLocked(PrintWriter pw, String prefix, ProcessState proc, boolean dumpAll) {
        if (dumpAll) {
            pw.print(prefix);
            pw.print("myID=");
            pw.print(Integer.toHexString(System.identityHashCode(proc)));
            pw.print(" mCommonProcess=");
            pw.print(Integer.toHexString(System.identityHashCode(proc.mCommonProcess)));
            pw.print(" mPackage=");
            pw.println(proc.mPackage);
            if (proc.mMultiPackage) {
                pw.print(prefix);
                pw.print("mMultiPackage=");
                pw.println(proc.mMultiPackage);
            }
            if (proc != proc.mCommonProcess) {
                pw.print(prefix);
                pw.print("Common Proc: ");
                pw.print(proc.mCommonProcess.mName);
                pw.print("/");
                pw.print(proc.mCommonProcess.mUid);
                pw.print(" pkg=");
                pw.println(proc.mCommonProcess.mPackage);
            }
        }
        if (proc.mActive) {
            pw.print(prefix);
            pw.print("mActive=");
            pw.println(proc.mActive);
        }
        if (proc.mDead) {
            pw.print(prefix);
            pw.print("mDead=");
            pw.println(proc.mDead);
        }
        if (proc.mNumActiveServices != 0 || proc.mNumStartedServices != 0) {
            pw.print(prefix);
            pw.print("mNumActiveServices=");
            pw.print(proc.mNumActiveServices);
            pw.print(" mNumStartedServices=");
            pw.println(proc.mNumStartedServices);
        }
    }

    public void dumpLocked(PrintWriter pw, String reqPackage, long now, boolean dumpSummary, boolean dumpAll, boolean activeOnly) {
        int ip;
        long totalTime = dumpSingleTime(null, null, this.mMemFactorDurations, this.mMemFactor, this.mStartTime, now);
        boolean sepNeeded = DEBUG_PARCEL;
        if (this.mSysMemUsageTable != null) {
            pw.println("System memory usage:");
            dumpSysMemUsage(pw, "  ", ALL_SCREEN_ADJ, ALL_MEM_ADJ);
            sepNeeded = true;
        }
        ArrayMap<String, SparseArray<SparseArray<PackageState>>> pkgMap = this.mPackages.getMap();
        boolean printedHeader = DEBUG_PARCEL;
        for (ip = SYS_MEM_USAGE_SAMPLE_COUNT; ip < pkgMap.size(); ip += SYS_MEM_USAGE_CACHED_MINIMUM) {
            int iu;
            String pkgName = (String) pkgMap.keyAt(ip);
            SparseArray<SparseArray<PackageState>> uids = (SparseArray) pkgMap.valueAt(ip);
            for (iu = SYS_MEM_USAGE_SAMPLE_COUNT; iu < uids.size(); iu += SYS_MEM_USAGE_CACHED_MINIMUM) {
                int uid = uids.keyAt(iu);
                SparseArray<PackageState> vpkgs = (SparseArray) uids.valueAt(iu);
                for (int iv = SYS_MEM_USAGE_SAMPLE_COUNT; iv < vpkgs.size(); iv += SYS_MEM_USAGE_CACHED_MINIMUM) {
                    int iproc;
                    ProcessState proc;
                    int vers = vpkgs.keyAt(iv);
                    PackageState pkgState = (PackageState) vpkgs.valueAt(iv);
                    int NPROCS = pkgState.mProcesses.size();
                    int NSRVS = pkgState.mServices.size();
                    boolean pkgMatch = (reqPackage == null || reqPackage.equals(pkgName)) ? true : DEBUG_PARCEL;
                    if (!pkgMatch) {
                        boolean procMatch = DEBUG_PARCEL;
                        for (iproc = SYS_MEM_USAGE_SAMPLE_COUNT; iproc < NPROCS; iproc += SYS_MEM_USAGE_CACHED_MINIMUM) {
                            if (reqPackage.equals(((ProcessState) pkgState.mProcesses.valueAt(iproc)).mName)) {
                                procMatch = true;
                                break;
                            }
                        }
                        if (!procMatch) {
                        }
                    }
                    if (NPROCS > 0 || NSRVS > 0) {
                        if (!printedHeader) {
                            if (sepNeeded) {
                                pw.println();
                            }
                            pw.println("Per-Package Stats:");
                            printedHeader = true;
                            sepNeeded = true;
                        }
                        pw.print("  * ");
                        pw.print(pkgName);
                        pw.print(" / ");
                        UserHandle.formatUid(pw, uid);
                        pw.print(" / v");
                        pw.print(vers);
                        pw.println(":");
                    }
                    if (!dumpSummary || dumpAll) {
                        iproc = SYS_MEM_USAGE_SAMPLE_COUNT;
                        while (iproc < NPROCS) {
                            proc = (ProcessState) pkgState.mProcesses.valueAt(iproc);
                            if (!pkgMatch) {
                                if (!reqPackage.equals(proc.mName)) {
                                    iproc += SYS_MEM_USAGE_CACHED_MINIMUM;
                                }
                            }
                            if (!activeOnly || proc.isInUse()) {
                                pw.print("      Process ");
                                pw.print((String) pkgState.mProcesses.keyAt(iproc));
                                if (proc.mCommonProcess.mMultiPackage) {
                                    pw.print(" (multi, ");
                                } else {
                                    pw.print(" (unique, ");
                                }
                                pw.print(proc.mDurationsTableSize);
                                pw.print(" entries)");
                                pw.println(":");
                                dumpProcessState(pw, "        ", proc, ALL_SCREEN_ADJ, ALL_MEM_ADJ, ALL_PROC_STATES, now);
                                dumpProcessPss(pw, "        ", proc, ALL_SCREEN_ADJ, ALL_MEM_ADJ, ALL_PROC_STATES);
                                dumpProcessInternalLocked(pw, "        ", proc, dumpAll);
                                iproc += SYS_MEM_USAGE_CACHED_MINIMUM;
                            } else {
                                pw.print("      (Not active: ");
                                pw.print((String) pkgState.mProcesses.keyAt(iproc));
                                pw.println(")");
                                iproc += SYS_MEM_USAGE_CACHED_MINIMUM;
                            }
                        }
                    } else {
                        ArrayList<ProcessState> procs = new ArrayList();
                        iproc = SYS_MEM_USAGE_SAMPLE_COUNT;
                        while (iproc < NPROCS) {
                            proc = (ProcessState) pkgState.mProcesses.valueAt(iproc);
                            if (!pkgMatch) {
                                if (!reqPackage.equals(proc.mName)) {
                                    iproc += SYS_MEM_USAGE_CACHED_MINIMUM;
                                }
                            }
                            if (!activeOnly || proc.isInUse()) {
                                procs.add(proc);
                                iproc += SYS_MEM_USAGE_CACHED_MINIMUM;
                            } else {
                                iproc += SYS_MEM_USAGE_CACHED_MINIMUM;
                            }
                        }
                        dumpProcessSummaryLocked(pw, "      ", procs, ALL_SCREEN_ADJ, ALL_MEM_ADJ, NON_CACHED_PROC_STATES, DEBUG_PARCEL, now, totalTime);
                    }
                    int isvc = SYS_MEM_USAGE_SAMPLE_COUNT;
                    while (isvc < NSRVS) {
                        ServiceState svc = (ServiceState) pkgState.mServices.valueAt(isvc);
                        if (!pkgMatch) {
                            if (!reqPackage.equals(svc.mProcessName)) {
                                isvc += SYS_MEM_USAGE_CACHED_MINIMUM;
                            }
                        }
                        if (!activeOnly || svc.isInUse()) {
                            if (dumpAll) {
                                pw.print("      Service ");
                            } else {
                                pw.print("      * ");
                            }
                            pw.print((String) pkgState.mServices.keyAt(isvc));
                            pw.println(":");
                            pw.print("        Process: ");
                            pw.println(svc.mProcessName);
                            String str = "        ";
                            String str2 = "          ";
                            String str3 = "    ";
                            String str4 = "Running";
                            int i = svc.mRunCount;
                            int i2 = svc.mRunState;
                            long j = svc.mRunStartTime;
                            boolean z = (!dumpSummary || dumpAll) ? true : DEBUG_PARCEL;
                            dumpServiceStats(pw, str, str2, str3, str4, svc, i, SYS_MEM_USAGE_SAMPLE_COUNT, i2, j, now, totalTime, z);
                            str = "        ";
                            str2 = "          ";
                            str3 = "    ";
                            str4 = "Started";
                            i = svc.mStartedCount;
                            i2 = svc.mStartedState;
                            j = svc.mStartedStartTime;
                            z = (!dumpSummary || dumpAll) ? true : DEBUG_PARCEL;
                            dumpServiceStats(pw, str, str2, str3, str4, svc, i, SYS_MEM_USAGE_CACHED_MINIMUM, i2, j, now, totalTime, z);
                            str = "        ";
                            str2 = "          ";
                            str3 = "      ";
                            str4 = "Bound";
                            i = svc.mBoundCount;
                            i2 = svc.mBoundState;
                            j = svc.mBoundStartTime;
                            z = (!dumpSummary || dumpAll) ? true : DEBUG_PARCEL;
                            dumpServiceStats(pw, str, str2, str3, str4, svc, i, SYS_MEM_USAGE_CACHED_AVERAGE, i2, j, now, totalTime, z);
                            str = "        ";
                            str2 = "          ";
                            str3 = "  ";
                            str4 = "Executing";
                            i = svc.mExecCount;
                            i2 = svc.mExecState;
                            j = svc.mExecStartTime;
                            z = (!dumpSummary || dumpAll) ? true : DEBUG_PARCEL;
                            dumpServiceStats(pw, str, str2, str3, str4, svc, i, SYS_MEM_USAGE_CACHED_MAXIMUM, i2, j, now, totalTime, z);
                            if (dumpAll) {
                                if (svc.mOwner != null) {
                                    pw.print("        mOwner=");
                                    pw.println(svc.mOwner);
                                }
                                if (svc.mStarted || svc.mRestarting) {
                                    pw.print("        mStarted=");
                                    pw.print(svc.mStarted);
                                    pw.print(" mRestarting=");
                                    pw.println(svc.mRestarting);
                                }
                            }
                            isvc += SYS_MEM_USAGE_CACHED_MINIMUM;
                        } else {
                            pw.print("      (Not active: ");
                            pw.print((String) pkgState.mServices.keyAt(isvc));
                            pw.println(")");
                            isvc += SYS_MEM_USAGE_CACHED_MINIMUM;
                        }
                    }
                }
            }
        }
        ArrayMap<String, SparseArray<ProcessState>> procMap = this.mProcesses.getMap();
        printedHeader = DEBUG_PARCEL;
        int numShownProcs = SYS_MEM_USAGE_SAMPLE_COUNT;
        int numTotalProcs = SYS_MEM_USAGE_SAMPLE_COUNT;
        for (ip = SYS_MEM_USAGE_SAMPLE_COUNT; ip < procMap.size(); ip += SYS_MEM_USAGE_CACHED_MINIMUM) {
            String procName = (String) procMap.keyAt(ip);
            SparseArray<ProcessState> uids2 = (SparseArray) procMap.valueAt(ip);
            for (iu = SYS_MEM_USAGE_SAMPLE_COUNT; iu < uids2.size(); iu += SYS_MEM_USAGE_CACHED_MINIMUM) {
                uid = uids2.keyAt(iu);
                numTotalProcs += SYS_MEM_USAGE_CACHED_MINIMUM;
                proc = (ProcessState) uids2.valueAt(iu);
                if (!(proc.mDurationsTableSize == 0 && proc.mCurState == STATE_NOTHING && proc.mPssTableSize == 0) && proc.mMultiPackage) {
                    if (!(reqPackage == null || reqPackage.equals(procName))) {
                        if (!reqPackage.equals(proc.mPackage)) {
                        }
                    }
                    numShownProcs += SYS_MEM_USAGE_CACHED_MINIMUM;
                    if (sepNeeded) {
                        pw.println();
                    }
                    sepNeeded = true;
                    if (!printedHeader) {
                        pw.println("Multi-Package Common Processes:");
                        printedHeader = true;
                    }
                    if (!activeOnly || proc.isInUse()) {
                        pw.print("  * ");
                        pw.print(procName);
                        pw.print(" / ");
                        UserHandle.formatUid(pw, uid);
                        pw.print(" (");
                        pw.print(proc.mDurationsTableSize);
                        pw.print(" entries)");
                        pw.println(":");
                        dumpProcessState(pw, "        ", proc, ALL_SCREEN_ADJ, ALL_MEM_ADJ, ALL_PROC_STATES, now);
                        dumpProcessPss(pw, "        ", proc, ALL_SCREEN_ADJ, ALL_MEM_ADJ, ALL_PROC_STATES);
                        dumpProcessInternalLocked(pw, "        ", proc, dumpAll);
                    } else {
                        pw.print("      (Not active: ");
                        pw.print(procName);
                        pw.println(")");
                    }
                }
            }
        }
        if (dumpAll) {
            pw.println();
            pw.print("  Total procs: ");
            pw.print(numShownProcs);
            pw.print(" shown of ");
            pw.print(numTotalProcs);
            pw.println(" total");
        }
        if (sepNeeded) {
            pw.println();
        }
        if (dumpSummary) {
            pw.println("Summary:");
            dumpSummaryLocked(pw, reqPackage, now, activeOnly);
        } else {
            dumpTotalsLocked(pw, now);
        }
        if (dumpAll) {
            pw.println();
            pw.println("Internal state:");
            pw.print("  Num long arrays: ");
            pw.println(this.mLongs.size());
            pw.print("  Next long entry: ");
            pw.println(this.mNextLong);
            pw.print("  mRunning=");
            pw.println(this.mRunning);
        }
    }

    public static long dumpSingleServiceTime(PrintWriter pw, String prefix, ServiceState service, int serviceType, int curState, long curStartTime, long now) {
        long totalTime = 0;
        int printedScreen = STATE_NOTHING;
        int iscreen = SYS_MEM_USAGE_SAMPLE_COUNT;
        while (iscreen < SYS_MEM_USAGE_ZRAM_AVERAGE) {
            int printedMem = STATE_NOTHING;
            int imem = SYS_MEM_USAGE_SAMPLE_COUNT;
            while (imem < SYS_MEM_USAGE_FREE_MINIMUM) {
                int state = imem + iscreen;
                long time = service.getDuration(serviceType, curState, curStartTime, state, now);
                String running = "";
                if (curState == state && pw != null) {
                    running = " (running)";
                }
                if (time != 0) {
                    if (pw != null) {
                        pw.print(prefix);
                        printScreenLabel(pw, printedScreen != iscreen ? iscreen : STATE_NOTHING);
                        printedScreen = iscreen;
                        printMemLabel(pw, printedMem != imem ? imem : STATE_NOTHING, '\u0000');
                        printedMem = imem;
                        pw.print(": ");
                        TimeUtils.formatDuration(time, pw);
                        pw.println(running);
                    }
                    totalTime += time;
                }
                imem += SYS_MEM_USAGE_CACHED_MINIMUM;
            }
            iscreen += SYS_MEM_USAGE_FREE_MINIMUM;
        }
        if (!(totalTime == 0 || pw == null)) {
            pw.print(prefix);
            pw.print("    TOTAL: ");
            TimeUtils.formatDuration(totalTime, pw);
            pw.println();
        }
        return totalTime;
    }

    void dumpServiceStats(PrintWriter pw, String prefix, String prefixInner, String headerPrefix, String header, ServiceState service, int count, int serviceType, int state, long startTime, long now, long totalTime, boolean dumpAll) {
        if (count == 0) {
            return;
        }
        if (dumpAll) {
            pw.print(prefix);
            pw.print(header);
            pw.print(" op count ");
            pw.print(count);
            pw.println(":");
            dumpSingleServiceTime(pw, prefixInner, service, serviceType, state, startTime, now);
            return;
        }
        long myTime = dumpSingleServiceTime(null, null, service, serviceType, state, startTime, now);
        pw.print(prefix);
        pw.print(headerPrefix);
        pw.print(header);
        pw.print(" count ");
        pw.print(count);
        pw.print(" / time ");
        printPercent(pw, ((double) myTime) / ((double) totalTime));
        pw.println();
    }

    public void dumpSummaryLocked(PrintWriter pw, String reqPackage, long now, boolean activeOnly) {
        PrintWriter printWriter = pw;
        dumpFilteredSummaryLocked(printWriter, null, "  ", ALL_SCREEN_ADJ, ALL_MEM_ADJ, ALL_PROC_STATES, NON_CACHED_PROC_STATES, now, dumpSingleTime(null, null, this.mMemFactorDurations, this.mMemFactor, this.mStartTime, now), reqPackage, activeOnly);
        pw.println();
        dumpTotalsLocked(pw, now);
    }

    long printMemoryCategory(PrintWriter pw, String prefix, String label, double memWeight, long totalTime, long curTotalMem, int samples) {
        if (memWeight == 0.0d) {
            return curTotalMem;
        }
        long mem = (long) ((1024.0d * memWeight) / ((double) totalTime));
        pw.print(prefix);
        pw.print(label);
        pw.print(": ");
        printSizeValue(pw, mem);
        pw.print(" (");
        pw.print(samples);
        pw.print(" samples)");
        pw.println();
        return curTotalMem + mem;
    }

    void dumpTotalsLocked(PrintWriter pw, long now) {
        pw.println("Run time Stats:");
        dumpSingleTime(pw, "  ", this.mMemFactorDurations, this.mMemFactor, this.mStartTime, now);
        pw.println();
        pw.println("Memory usage:");
        TotalMemoryUseCollection totalMemoryUseCollection = new TotalMemoryUseCollection(ALL_SCREEN_ADJ, ALL_MEM_ADJ);
        computeTotalMemoryUse(totalMemoryUseCollection, now);
        long totalPss = printMemoryCategory(pw, "  ", "Native ", totalMemoryUseCollection.sysMemNativeWeight, totalMemoryUseCollection.totalTime, printMemoryCategory(pw, "  ", "Kernel ", totalMemoryUseCollection.sysMemKernelWeight, totalMemoryUseCollection.totalTime, 0, totalMemoryUseCollection.sysMemSamples), totalMemoryUseCollection.sysMemSamples);
        for (int i = SYS_MEM_USAGE_SAMPLE_COUNT; i < SYS_MEM_USAGE_NATIVE_AVERAGE; i += SYS_MEM_USAGE_CACHED_MINIMUM) {
            if (i != SYS_MEM_USAGE_ZRAM_MINIMUM) {
                totalPss = printMemoryCategory(pw, "  ", STATE_NAMES[i], totalMemoryUseCollection.processStateWeight[i], totalMemoryUseCollection.totalTime, totalPss, totalMemoryUseCollection.processStateSamples[i]);
            }
        }
        totalPss = printMemoryCategory(pw, "  ", "Z-Ram  ", totalMemoryUseCollection.sysMemZRamWeight, totalMemoryUseCollection.totalTime, printMemoryCategory(pw, "  ", "Free   ", totalMemoryUseCollection.sysMemFreeWeight, totalMemoryUseCollection.totalTime, printMemoryCategory(pw, "  ", "Cached ", totalMemoryUseCollection.sysMemCachedWeight, totalMemoryUseCollection.totalTime, totalPss, totalMemoryUseCollection.sysMemSamples), totalMemoryUseCollection.sysMemSamples), totalMemoryUseCollection.sysMemSamples);
        pw.print("  TOTAL  : ");
        printSizeValue(pw, totalPss);
        pw.println();
        printMemoryCategory(pw, "  ", STATE_NAMES[SYS_MEM_USAGE_ZRAM_MINIMUM], totalMemoryUseCollection.processStateWeight[SYS_MEM_USAGE_ZRAM_MINIMUM], totalMemoryUseCollection.totalTime, totalPss, totalMemoryUseCollection.processStateSamples[SYS_MEM_USAGE_ZRAM_MINIMUM]);
        pw.println();
        pw.print("          Start time: ");
        pw.print(DateFormat.format((CharSequence) "yyyy-MM-dd HH:mm:ss", this.mTimePeriodStartClock));
        pw.println();
        pw.print("  Total elapsed time: ");
        TimeUtils.formatDuration((this.mRunning ? SystemClock.elapsedRealtime() : this.mTimePeriodEndRealtime) - this.mTimePeriodStartRealtime, pw);
        boolean partial = true;
        if ((this.mFlags & SYS_MEM_USAGE_CACHED_AVERAGE) != 0) {
            pw.print(" (shutdown)");
            partial = DEBUG_PARCEL;
        }
        if ((this.mFlags & SYS_MEM_USAGE_FREE_MINIMUM) != 0) {
            pw.print(" (sysprops)");
            partial = DEBUG_PARCEL;
        }
        if ((this.mFlags & SYS_MEM_USAGE_CACHED_MINIMUM) != 0) {
            pw.print(" (complete)");
            partial = DEBUG_PARCEL;
        }
        if (partial) {
            pw.print(" (partial)");
        }
        pw.print(' ');
        pw.print(this.mRuntime);
        pw.println();
    }

    void dumpFilteredSummaryLocked(PrintWriter pw, String header, String prefix, int[] screenStates, int[] memStates, int[] procStates, int[] sortProcStates, long now, long totalTime, String reqPackage, boolean activeOnly) {
        ArrayList<ProcessState> procs = collectProcessesLocked(screenStates, memStates, procStates, sortProcStates, now, reqPackage, activeOnly);
        if (procs.size() > 0) {
            if (header != null) {
                pw.println();
                pw.println(header);
            }
            dumpProcessSummaryLocked(pw, prefix, procs, screenStates, memStates, sortProcStates, true, now, totalTime);
        }
    }

    public ArrayList<ProcessState> collectProcessesLocked(int[] screenStates, int[] memStates, int[] procStates, int[] sortProcStates, long now, String reqPackage, boolean activeOnly) {
        ArraySet<ProcessState> foundProcs = new ArraySet();
        ArrayMap<String, SparseArray<SparseArray<PackageState>>> pkgMap = this.mPackages.getMap();
        for (int ip = SYS_MEM_USAGE_SAMPLE_COUNT; ip < pkgMap.size(); ip += SYS_MEM_USAGE_CACHED_MINIMUM) {
            String pkgName = (String) pkgMap.keyAt(ip);
            SparseArray<SparseArray<PackageState>> procs = (SparseArray) pkgMap.valueAt(ip);
            for (int iu = SYS_MEM_USAGE_SAMPLE_COUNT; iu < procs.size(); iu += SYS_MEM_USAGE_CACHED_MINIMUM) {
                SparseArray<PackageState> vpkgs = (SparseArray) procs.valueAt(iu);
                int NVERS = vpkgs.size();
                for (int iv = SYS_MEM_USAGE_SAMPLE_COUNT; iv < NVERS; iv += SYS_MEM_USAGE_CACHED_MINIMUM) {
                    PackageState state = (PackageState) vpkgs.valueAt(iv);
                    int NPROCS = state.mProcesses.size();
                    boolean pkgMatch = (reqPackage == null || reqPackage.equals(pkgName)) ? true : DEBUG_PARCEL;
                    int iproc = SYS_MEM_USAGE_SAMPLE_COUNT;
                    while (iproc < NPROCS) {
                        ProcessState proc = (ProcessState) state.mProcesses.valueAt(iproc);
                        if (!pkgMatch) {
                            if (!reqPackage.equals(proc.mName)) {
                                iproc += SYS_MEM_USAGE_CACHED_MINIMUM;
                            }
                        }
                        if (!activeOnly || proc.isInUse()) {
                            foundProcs.add(proc.mCommonProcess);
                            iproc += SYS_MEM_USAGE_CACHED_MINIMUM;
                        } else {
                            iproc += SYS_MEM_USAGE_CACHED_MINIMUM;
                        }
                    }
                }
            }
        }
        ArrayList<ProcessState> arrayList = new ArrayList(foundProcs.size());
        for (int i = SYS_MEM_USAGE_SAMPLE_COUNT; i < foundProcs.size(); i += SYS_MEM_USAGE_CACHED_MINIMUM) {
            proc = (ProcessState) foundProcs.valueAt(i);
            if (computeProcessTimeLocked(proc, screenStates, memStates, procStates, now) > 0) {
                arrayList.add(proc);
                if (procStates != sortProcStates) {
                    computeProcessTimeLocked(proc, screenStates, memStates, sortProcStates, now);
                }
            }
        }
        Collections.sort(arrayList, new AnonymousClass2(this));
        return arrayList;
    }

    String collapseString(String pkgName, String itemName) {
        if (!itemName.startsWith(pkgName)) {
            return itemName;
        }
        int ITEMLEN = itemName.length();
        int PKGLEN = pkgName.length();
        if (ITEMLEN == PKGLEN) {
            return "";
        }
        if (ITEMLEN < PKGLEN || itemName.charAt(PKGLEN) != '.') {
            return itemName;
        }
        return itemName.substring(PKGLEN);
    }

    public void dumpCheckinLocked(PrintWriter pw, String reqPackage) {
        long elapsedRealtime;
        int ip;
        int iu;
        int i;
        long now = SystemClock.uptimeMillis();
        ArrayMap<String, SparseArray<SparseArray<PackageState>>> pkgMap = this.mPackages.getMap();
        pw.println("vers,5");
        pw.print("period,");
        pw.print(this.mTimePeriodStartClockStr);
        pw.print(",");
        pw.print(this.mTimePeriodStartRealtime);
        pw.print(",");
        if (this.mRunning) {
            elapsedRealtime = SystemClock.elapsedRealtime();
        } else {
            elapsedRealtime = this.mTimePeriodEndRealtime;
        }
        pw.print(elapsedRealtime);
        boolean partial = true;
        if ((this.mFlags & SYS_MEM_USAGE_CACHED_AVERAGE) != 0) {
            pw.print(",shutdown");
            partial = DEBUG_PARCEL;
        }
        if ((this.mFlags & SYS_MEM_USAGE_FREE_MINIMUM) != 0) {
            pw.print(",sysprops");
            partial = DEBUG_PARCEL;
        }
        if ((this.mFlags & SYS_MEM_USAGE_CACHED_MINIMUM) != 0) {
            pw.print(",complete");
            partial = DEBUG_PARCEL;
        }
        if (partial) {
            pw.print(",partial");
        }
        pw.println();
        pw.print("config,");
        pw.println(this.mRuntime);
        for (ip = SYS_MEM_USAGE_SAMPLE_COUNT; ip < pkgMap.size(); ip += SYS_MEM_USAGE_CACHED_MINIMUM) {
            int uid;
            String pkgName = (String) pkgMap.keyAt(ip);
            if (reqPackage == null || reqPackage.equals(pkgName)) {
                SparseArray<SparseArray<PackageState>> uids = (SparseArray) pkgMap.valueAt(ip);
                for (iu = SYS_MEM_USAGE_SAMPLE_COUNT; iu < uids.size(); iu += SYS_MEM_USAGE_CACHED_MINIMUM) {
                    uid = uids.keyAt(iu);
                    SparseArray<PackageState> vpkgs = (SparseArray) uids.valueAt(iu);
                    for (int iv = SYS_MEM_USAGE_SAMPLE_COUNT; iv < vpkgs.size(); iv += SYS_MEM_USAGE_CACHED_MINIMUM) {
                        int vers = vpkgs.keyAt(iv);
                        PackageState pkgState = (PackageState) vpkgs.valueAt(iv);
                        int NPROCS = pkgState.mProcesses.size();
                        int NSRVS = pkgState.mServices.size();
                        for (int iproc = SYS_MEM_USAGE_SAMPLE_COUNT; iproc < NPROCS; iproc += SYS_MEM_USAGE_CACHED_MINIMUM) {
                            ProcessState proc = (ProcessState) pkgState.mProcesses.valueAt(iproc);
                            pw.print("pkgproc,");
                            pw.print(pkgName);
                            pw.print(",");
                            pw.print(uid);
                            pw.print(",");
                            pw.print(vers);
                            pw.print(",");
                            pw.print(collapseString(pkgName, (String) pkgState.mProcesses.keyAt(iproc)));
                            dumpAllProcessStateCheckin(pw, proc, now);
                            pw.println();
                            if (proc.mPssTableSize > 0) {
                                pw.print("pkgpss,");
                                pw.print(pkgName);
                                pw.print(",");
                                pw.print(uid);
                                pw.print(",");
                                pw.print(vers);
                                pw.print(",");
                                pw.print(collapseString(pkgName, (String) pkgState.mProcesses.keyAt(iproc)));
                                dumpAllProcessPssCheckin(pw, proc);
                                pw.println();
                            }
                            if (proc.mNumExcessiveWake > 0 || proc.mNumExcessiveCpu > 0 || proc.mNumCachedKill > 0) {
                                pw.print("pkgkills,");
                                pw.print(pkgName);
                                pw.print(",");
                                pw.print(uid);
                                pw.print(",");
                                pw.print(vers);
                                pw.print(",");
                                pw.print(collapseString(pkgName, (String) pkgState.mProcesses.keyAt(iproc)));
                                pw.print(",");
                                pw.print(proc.mNumExcessiveWake);
                                pw.print(",");
                                pw.print(proc.mNumExcessiveCpu);
                                pw.print(",");
                                pw.print(proc.mNumCachedKill);
                                pw.print(",");
                                pw.print(proc.mMinCachedKillPss);
                                pw.print(":");
                                pw.print(proc.mAvgCachedKillPss);
                                pw.print(":");
                                pw.print(proc.mMaxCachedKillPss);
                                pw.println();
                            }
                        }
                        for (int isvc = SYS_MEM_USAGE_SAMPLE_COUNT; isvc < NSRVS; isvc += SYS_MEM_USAGE_CACHED_MINIMUM) {
                            String serviceName = collapseString(pkgName, (String) pkgState.mServices.keyAt(isvc));
                            ServiceState svc = (ServiceState) pkgState.mServices.valueAt(isvc);
                            dumpServiceTimeCheckin(pw, "pkgsvc-run", pkgName, uid, vers, serviceName, svc, SYS_MEM_USAGE_SAMPLE_COUNT, svc.mRunCount, svc.mRunState, svc.mRunStartTime, now);
                            dumpServiceTimeCheckin(pw, "pkgsvc-start", pkgName, uid, vers, serviceName, svc, SYS_MEM_USAGE_CACHED_MINIMUM, svc.mStartedCount, svc.mStartedState, svc.mStartedStartTime, now);
                            dumpServiceTimeCheckin(pw, "pkgsvc-bound", pkgName, uid, vers, serviceName, svc, SYS_MEM_USAGE_CACHED_AVERAGE, svc.mBoundCount, svc.mBoundState, svc.mBoundStartTime, now);
                            dumpServiceTimeCheckin(pw, "pkgsvc-exec", pkgName, uid, vers, serviceName, svc, SYS_MEM_USAGE_CACHED_MAXIMUM, svc.mExecCount, svc.mExecState, svc.mExecStartTime, now);
                        }
                    }
                }
            }
        }
        ArrayMap<String, SparseArray<ProcessState>> procMap = this.mProcesses.getMap();
        for (ip = SYS_MEM_USAGE_SAMPLE_COUNT; ip < procMap.size(); ip += SYS_MEM_USAGE_CACHED_MINIMUM) {
            String procName = (String) procMap.keyAt(ip);
            SparseArray<ProcessState> uids2 = (SparseArray) procMap.valueAt(ip);
            for (iu = SYS_MEM_USAGE_SAMPLE_COUNT; iu < uids2.size(); iu += SYS_MEM_USAGE_CACHED_MINIMUM) {
                uid = uids2.keyAt(iu);
                ProcessState procState = (ProcessState) uids2.valueAt(iu);
                if (procState.mDurationsTableSize > 0) {
                    pw.print("proc,");
                    pw.print(procName);
                    pw.print(",");
                    pw.print(uid);
                    dumpAllProcessStateCheckin(pw, procState, now);
                    pw.println();
                }
                if (procState.mPssTableSize > 0) {
                    pw.print("pss,");
                    pw.print(procName);
                    pw.print(",");
                    pw.print(uid);
                    dumpAllProcessPssCheckin(pw, procState);
                    pw.println();
                }
                if (procState.mNumExcessiveWake > 0 || procState.mNumExcessiveCpu > 0 || procState.mNumCachedKill > 0) {
                    pw.print("kills,");
                    pw.print(procName);
                    pw.print(",");
                    pw.print(uid);
                    pw.print(",");
                    pw.print(procState.mNumExcessiveWake);
                    pw.print(",");
                    pw.print(procState.mNumExcessiveCpu);
                    pw.print(",");
                    pw.print(procState.mNumCachedKill);
                    pw.print(",");
                    pw.print(procState.mMinCachedKillPss);
                    pw.print(":");
                    pw.print(procState.mAvgCachedKillPss);
                    pw.print(":");
                    pw.print(procState.mMaxCachedKillPss);
                    pw.println();
                }
            }
        }
        pw.print("total");
        PrintWriter printWriter = pw;
        dumpAdjTimesCheckin(printWriter, ",", this.mMemFactorDurations, this.mMemFactor, this.mStartTime, now);
        pw.println();
        if (this.mSysMemUsageTable != null) {
            pw.print("sysmemusage");
            for (i = SYS_MEM_USAGE_SAMPLE_COUNT; i < this.mSysMemUsageTableSize; i += SYS_MEM_USAGE_CACHED_MINIMUM) {
                int off = this.mSysMemUsageTable[i];
                int type = (off >> OFFSET_TYPE_SHIFT) & OFFSET_TYPE_MASK;
                pw.print(",");
                printProcStateTag(pw, type);
                for (int j = SYS_MEM_USAGE_SAMPLE_COUNT; j < SYS_MEM_USAGE_COUNT; j += SYS_MEM_USAGE_CACHED_MINIMUM) {
                    if (j > SYS_MEM_USAGE_CACHED_MINIMUM) {
                        pw.print(":");
                    }
                    pw.print(getLong(off, j));
                }
            }
        }
        pw.println();
        TotalMemoryUseCollection totalMemoryUseCollection = new TotalMemoryUseCollection(ALL_SCREEN_ADJ, ALL_MEM_ADJ);
        computeTotalMemoryUse(totalMemoryUseCollection, now);
        pw.print("weights,");
        pw.print(totalMemoryUseCollection.totalTime);
        pw.print(",");
        pw.print(totalMemoryUseCollection.sysMemCachedWeight);
        pw.print(":");
        pw.print(totalMemoryUseCollection.sysMemSamples);
        pw.print(",");
        pw.print(totalMemoryUseCollection.sysMemFreeWeight);
        pw.print(":");
        pw.print(totalMemoryUseCollection.sysMemSamples);
        pw.print(",");
        pw.print(totalMemoryUseCollection.sysMemZRamWeight);
        pw.print(":");
        pw.print(totalMemoryUseCollection.sysMemSamples);
        pw.print(",");
        pw.print(totalMemoryUseCollection.sysMemKernelWeight);
        pw.print(":");
        pw.print(totalMemoryUseCollection.sysMemSamples);
        pw.print(",");
        pw.print(totalMemoryUseCollection.sysMemNativeWeight);
        pw.print(":");
        pw.print(totalMemoryUseCollection.sysMemSamples);
        for (i = SYS_MEM_USAGE_SAMPLE_COUNT; i < SYS_MEM_USAGE_NATIVE_AVERAGE; i += SYS_MEM_USAGE_CACHED_MINIMUM) {
            pw.print(",");
            pw.print(totalMemoryUseCollection.processStateWeight[i]);
            pw.print(":");
            pw.print(totalMemoryUseCollection.processStateSamples[i]);
        }
        pw.println();
    }
}
