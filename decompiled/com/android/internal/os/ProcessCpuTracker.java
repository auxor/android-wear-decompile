package com.android.internal.os;

import android.os.FileUtils;
import android.os.Process;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Slog;
import com.android.internal.R;
import com.android.internal.os.BatteryStatsImpl.Uid.Proc;
import com.android.internal.telephony.RILConstants;
import com.android.internal.util.FastPrintWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;
import javax.microedition.khronos.opengles.GL10;
import libcore.io.IoUtils;

public class ProcessCpuTracker {
    private static final boolean DEBUG = false;
    private static final int[] LOAD_AVERAGE_FORMAT;
    private static final int[] PROCESS_FULL_STATS_FORMAT;
    static final int PROCESS_FULL_STAT_MAJOR_FAULTS = 2;
    static final int PROCESS_FULL_STAT_MINOR_FAULTS = 1;
    static final int PROCESS_FULL_STAT_STIME = 4;
    static final int PROCESS_FULL_STAT_UTIME = 3;
    static final int PROCESS_FULL_STAT_VSIZE = 5;
    private static final int[] PROCESS_STATS_FORMAT;
    static final int PROCESS_STAT_MAJOR_FAULTS = 1;
    static final int PROCESS_STAT_MINOR_FAULTS = 0;
    static final int PROCESS_STAT_STIME = 3;
    static final int PROCESS_STAT_UTIME = 2;
    private static final int[] SYSTEM_CPU_FORMAT;
    private static final String TAG = "ProcessCpuTracker";
    private static final boolean localLOGV = false;
    private static final Comparator<Stats> sLoadComparator;
    private long mBaseIdleTime;
    private long mBaseIoWaitTime;
    private long mBaseIrqTime;
    private long mBaseSoftIrqTime;
    private long mBaseSystemTime;
    private long mBaseUserTime;
    private byte[] mBuffer;
    private long[] mCpuSpeedTimes;
    private long[] mCpuSpeeds;
    private int[] mCurPids;
    private int[] mCurThreadPids;
    private long mCurrentSampleRealTime;
    private long mCurrentSampleTime;
    private boolean mFirst;
    private final boolean mIncludeThreads;
    private long mLastSampleRealTime;
    private long mLastSampleTime;
    private float mLoad1;
    private float mLoad15;
    private float mLoad5;
    private final float[] mLoadAverageData;
    private final ArrayList<Stats> mProcStats;
    private final long[] mProcessFullStatsData;
    private final String[] mProcessFullStatsStringData;
    private final long[] mProcessStatsData;
    private long[] mRelCpuSpeedTimes;
    private int mRelIdleTime;
    private int mRelIoWaitTime;
    private int mRelIrqTime;
    private int mRelSoftIrqTime;
    private int mRelSystemTime;
    private int mRelUserTime;
    private final long[] mSinglePidStatsData;
    private final long[] mSystemCpuData;
    private final ArrayList<Stats> mWorkingProcs;
    private boolean mWorkingProcsSorted;

    public static class Stats {
        public boolean active;
        public boolean added;
        public String baseName;
        public long base_majfaults;
        public long base_minfaults;
        public long base_stime;
        public long base_uptime;
        public long base_utime;
        public Proc batteryStats;
        final String cmdlineFile;
        public boolean interesting;
        public String name;
        public int nameWidth;
        public final int pid;
        public int rel_majfaults;
        public int rel_minfaults;
        public int rel_stime;
        public long rel_uptime;
        public int rel_utime;
        public boolean removed;
        final String statFile;
        final ArrayList<Stats> threadStats;
        final String threadsDir;
        public final int uid;
        public long vsize;
        public boolean working;
        final ArrayList<Stats> workingThreads;

        Stats(int _pid, int parentPid, boolean includeThreads) {
            this.pid = _pid;
            if (parentPid < 0) {
                File procDir = new File("/proc", Integer.toString(this.pid));
                this.statFile = new File(procDir, "stat").toString();
                this.cmdlineFile = new File(procDir, "cmdline").toString();
                this.threadsDir = new File(procDir, "task").toString();
                if (includeThreads) {
                    this.threadStats = new ArrayList();
                    this.workingThreads = new ArrayList();
                } else {
                    this.threadStats = null;
                    this.workingThreads = null;
                }
            } else {
                this.statFile = new File(new File(new File(new File("/proc", Integer.toString(parentPid)), "task"), Integer.toString(this.pid)), "stat").toString();
                this.cmdlineFile = null;
                this.threadsDir = null;
                this.threadStats = null;
                this.workingThreads = null;
            }
            this.uid = FileUtils.getUid(this.statFile.toString());
        }
    }

    static {
        PROCESS_STATS_FORMAT = new int[]{32, 544, 32, 32, 32, 32, 32, 32, 32, 8224, 32, 8224, 32, 8224, 8224};
        PROCESS_FULL_STATS_FORMAT = new int[]{32, 4640, 32, 32, 32, 32, 32, 32, 32, 8224, 32, 8224, 32, 8224, 8224, 32, 32, 32, 32, 32, 32, 32, 8224};
        SYSTEM_CPU_FORMAT = new int[]{R.styleable.Theme_actionModePopupWindowStyle, 8224, 8224, 8224, 8224, 8224, 8224, 8224};
        LOAD_AVERAGE_FORMAT = new int[]{16416, 16416, 16416};
        sLoadComparator = new Comparator<Stats>() {
            public final int compare(Stats sta, Stats stb) {
                int ta = sta.rel_utime + sta.rel_stime;
                int tb = stb.rel_utime + stb.rel_stime;
                if (ta != tb) {
                    if (ta > tb) {
                        return -1;
                    }
                    return ProcessCpuTracker.PROCESS_STAT_MAJOR_FAULTS;
                } else if (sta.added != stb.added) {
                    if (sta.added) {
                        return -1;
                    }
                    return ProcessCpuTracker.PROCESS_STAT_MAJOR_FAULTS;
                } else if (sta.removed == stb.removed) {
                    return ProcessCpuTracker.PROCESS_STAT_MINOR_FAULTS;
                } else {
                    if (sta.added) {
                        return -1;
                    }
                    return ProcessCpuTracker.PROCESS_STAT_MAJOR_FAULTS;
                }
            }
        };
    }

    public ProcessCpuTracker(boolean includeThreads) {
        this.mProcessStatsData = new long[PROCESS_FULL_STAT_STIME];
        this.mSinglePidStatsData = new long[PROCESS_FULL_STAT_STIME];
        this.mProcessFullStatsStringData = new String[6];
        this.mProcessFullStatsData = new long[6];
        this.mSystemCpuData = new long[7];
        this.mLoadAverageData = new float[PROCESS_STAT_STIME];
        this.mLoad1 = 0.0f;
        this.mLoad5 = 0.0f;
        this.mLoad15 = 0.0f;
        this.mProcStats = new ArrayList();
        this.mWorkingProcs = new ArrayList();
        this.mFirst = true;
        this.mBuffer = new byte[DateUtils.FORMAT_CAP_MIDNIGHT];
        this.mIncludeThreads = includeThreads;
    }

    public void onLoadChanged(float load1, float load5, float load15) {
    }

    public int onMeasureProcessName(String name) {
        return PROCESS_STAT_MINOR_FAULTS;
    }

    public void init() {
        this.mFirst = true;
        update();
    }

    public void update() {
        this.mLastSampleTime = this.mCurrentSampleTime;
        this.mCurrentSampleTime = SystemClock.uptimeMillis();
        this.mLastSampleRealTime = this.mCurrentSampleRealTime;
        this.mCurrentSampleRealTime = SystemClock.elapsedRealtime();
        long[] sysCpu = this.mSystemCpuData;
        if (Process.readProcFile("/proc/stat", SYSTEM_CPU_FORMAT, null, sysCpu, null)) {
            long usertime = sysCpu[PROCESS_STAT_MINOR_FAULTS] + sysCpu[PROCESS_STAT_MAJOR_FAULTS];
            long systemtime = sysCpu[PROCESS_STAT_UTIME];
            long idletime = sysCpu[PROCESS_STAT_STIME];
            long iowaittime = sysCpu[PROCESS_FULL_STAT_STIME];
            long irqtime = sysCpu[PROCESS_FULL_STAT_VSIZE];
            long softirqtime = sysCpu[6];
            this.mRelUserTime = (int) (usertime - this.mBaseUserTime);
            this.mRelSystemTime = (int) (systemtime - this.mBaseSystemTime);
            this.mRelIoWaitTime = (int) (iowaittime - this.mBaseIoWaitTime);
            this.mRelIrqTime = (int) (irqtime - this.mBaseIrqTime);
            this.mRelSoftIrqTime = (int) (softirqtime - this.mBaseSoftIrqTime);
            this.mRelIdleTime = (int) (idletime - this.mBaseIdleTime);
            this.mBaseUserTime = usertime;
            this.mBaseSystemTime = systemtime;
            this.mBaseIoWaitTime = iowaittime;
            this.mBaseIrqTime = irqtime;
            this.mBaseSoftIrqTime = softirqtime;
            this.mBaseIdleTime = idletime;
        }
        ThreadPolicy savedPolicy = StrictMode.allowThreadDiskReads();
        try {
            this.mCurPids = collectStats("/proc", -1, this.mFirst, this.mCurPids, this.mProcStats);
            float[] loadAverages = this.mLoadAverageData;
            if (Process.readProcFile("/proc/loadavg", LOAD_AVERAGE_FORMAT, null, null, loadAverages)) {
                float load1 = loadAverages[PROCESS_STAT_MINOR_FAULTS];
                float load5 = loadAverages[PROCESS_STAT_MAJOR_FAULTS];
                float load15 = loadAverages[PROCESS_STAT_UTIME];
                if (!(load1 == this.mLoad1 && load5 == this.mLoad5 && load15 == this.mLoad15)) {
                    this.mLoad1 = load1;
                    this.mLoad5 = load5;
                    this.mLoad15 = load15;
                    onLoadChanged(load1, load5, load15);
                }
            }
            this.mWorkingProcsSorted = DEBUG;
            this.mFirst = DEBUG;
        } finally {
            StrictMode.setThreadPolicy(savedPolicy);
        }
    }

    private int[] collectStats(String statsFile, int parentPid, boolean first, int[] curPids, ArrayList<Stats> allProcs) {
        int[] pids = Process.getPids(statsFile, curPids);
        int NP = pids == null ? PROCESS_STAT_MINOR_FAULTS : pids.length;
        int NS = allProcs.size();
        int curStatsIndex = PROCESS_STAT_MINOR_FAULTS;
        int i = PROCESS_STAT_MINOR_FAULTS;
        while (i < NP) {
            int pid = pids[i];
            if (pid < 0) {
                NP = pid;
                break;
            }
            Stats st = curStatsIndex < NS ? (Stats) allProcs.get(curStatsIndex) : null;
            long[] procStats;
            if (st != null && st.pid == pid) {
                st.added = DEBUG;
                st.working = DEBUG;
                curStatsIndex += PROCESS_STAT_MAJOR_FAULTS;
                if (st.interesting) {
                    long uptime = SystemClock.uptimeMillis();
                    procStats = this.mProcessStatsData;
                    if (Process.readProcFile(st.statFile.toString(), PROCESS_STATS_FORMAT, null, procStats, null)) {
                        long minfaults = procStats[PROCESS_STAT_MINOR_FAULTS];
                        long majfaults = procStats[PROCESS_STAT_MAJOR_FAULTS];
                        long utime = procStats[PROCESS_STAT_UTIME];
                        long stime = procStats[PROCESS_STAT_STIME];
                        if (utime == st.base_utime && stime == st.base_stime) {
                            st.rel_utime = PROCESS_STAT_MINOR_FAULTS;
                            st.rel_stime = PROCESS_STAT_MINOR_FAULTS;
                            st.rel_minfaults = PROCESS_STAT_MINOR_FAULTS;
                            st.rel_majfaults = PROCESS_STAT_MINOR_FAULTS;
                            if (st.active) {
                                st.active = DEBUG;
                            }
                        } else {
                            if (!st.active) {
                                st.active = true;
                            }
                            if (parentPid < 0) {
                                getName(st, st.cmdlineFile);
                                if (st.threadStats != null) {
                                    this.mCurThreadPids = collectStats(st.threadsDir, pid, DEBUG, this.mCurThreadPids, st.threadStats);
                                }
                            }
                            st.rel_uptime = uptime - st.base_uptime;
                            st.base_uptime = uptime;
                            st.rel_utime = (int) (utime - st.base_utime);
                            st.rel_stime = (int) (stime - st.base_stime);
                            st.base_utime = utime;
                            st.base_stime = stime;
                            st.rel_minfaults = (int) (minfaults - st.base_minfaults);
                            st.rel_majfaults = (int) (majfaults - st.base_majfaults);
                            st.base_minfaults = minfaults;
                            st.base_majfaults = majfaults;
                            st.working = true;
                        }
                    }
                }
            } else if (st == null || st.pid > pid) {
                Stats stats = new Stats(pid, parentPid, this.mIncludeThreads);
                allProcs.add(curStatsIndex, stats);
                curStatsIndex += PROCESS_STAT_MAJOR_FAULTS;
                NS += PROCESS_STAT_MAJOR_FAULTS;
                String[] procStatsString = this.mProcessFullStatsStringData;
                procStats = this.mProcessFullStatsData;
                stats.base_uptime = SystemClock.uptimeMillis();
                if (Process.readProcFile(stats.statFile.toString(), PROCESS_FULL_STATS_FORMAT, procStatsString, procStats, null)) {
                    stats.vsize = procStats[PROCESS_FULL_STAT_VSIZE];
                    stats.interesting = true;
                    stats.baseName = procStatsString[PROCESS_STAT_MINOR_FAULTS];
                    stats.base_minfaults = procStats[PROCESS_STAT_MAJOR_FAULTS];
                    stats.base_majfaults = procStats[PROCESS_STAT_UTIME];
                    stats.base_utime = procStats[PROCESS_STAT_STIME];
                    stats.base_stime = procStats[PROCESS_FULL_STAT_STIME];
                } else {
                    Slog.w(TAG, "Skipping unknown process pid " + pid);
                    stats.baseName = "<unknown>";
                    stats.base_stime = 0;
                    stats.base_utime = 0;
                    stats.base_majfaults = 0;
                    stats.base_minfaults = 0;
                }
                if (parentPid < 0) {
                    getName(stats, stats.cmdlineFile);
                    if (stats.threadStats != null) {
                        this.mCurThreadPids = collectStats(stats.threadsDir, pid, true, this.mCurThreadPids, stats.threadStats);
                    }
                } else if (stats.interesting) {
                    stats.name = stats.baseName;
                    stats.nameWidth = onMeasureProcessName(stats.name);
                }
                stats.rel_utime = PROCESS_STAT_MINOR_FAULTS;
                stats.rel_stime = PROCESS_STAT_MINOR_FAULTS;
                stats.rel_minfaults = PROCESS_STAT_MINOR_FAULTS;
                stats.rel_majfaults = PROCESS_STAT_MINOR_FAULTS;
                stats.added = true;
                if (!first && stats.interesting) {
                    stats.working = true;
                }
            } else {
                st.rel_utime = PROCESS_STAT_MINOR_FAULTS;
                st.rel_stime = PROCESS_STAT_MINOR_FAULTS;
                st.rel_minfaults = PROCESS_STAT_MINOR_FAULTS;
                st.rel_majfaults = PROCESS_STAT_MINOR_FAULTS;
                st.removed = true;
                st.working = true;
                allProcs.remove(curStatsIndex);
                NS--;
                i--;
            }
            i += PROCESS_STAT_MAJOR_FAULTS;
        }
        while (curStatsIndex < NS) {
            st = (Stats) allProcs.get(curStatsIndex);
            st.rel_utime = PROCESS_STAT_MINOR_FAULTS;
            st.rel_stime = PROCESS_STAT_MINOR_FAULTS;
            st.rel_minfaults = PROCESS_STAT_MINOR_FAULTS;
            st.rel_majfaults = PROCESS_STAT_MINOR_FAULTS;
            st.removed = true;
            st.working = true;
            allProcs.remove(curStatsIndex);
            NS--;
        }
        return pids;
    }

    public long getCpuTimeForPid(int pid) {
        long time;
        synchronized (this.mSinglePidStatsData) {
            String statFile = "/proc/" + pid + "/stat";
            long[] statsData = this.mSinglePidStatsData;
            if (Process.readProcFile(statFile, PROCESS_STATS_FORMAT, null, statsData, null)) {
                time = statsData[PROCESS_STAT_UTIME] + statsData[PROCESS_STAT_STIME];
            } else {
                time = 0;
            }
        }
        return time;
    }

    public long[] getLastCpuSpeedTimes() {
        int i;
        if (this.mCpuSpeedTimes == null) {
            this.mCpuSpeedTimes = getCpuSpeedTimes(null);
            this.mRelCpuSpeedTimes = new long[this.mCpuSpeedTimes.length];
            for (i = PROCESS_STAT_MINOR_FAULTS; i < this.mCpuSpeedTimes.length; i += PROCESS_STAT_MAJOR_FAULTS) {
                this.mRelCpuSpeedTimes[i] = 1;
            }
        } else {
            getCpuSpeedTimes(this.mRelCpuSpeedTimes);
            for (i = PROCESS_STAT_MINOR_FAULTS; i < this.mCpuSpeedTimes.length; i += PROCESS_STAT_MAJOR_FAULTS) {
                long temp = this.mRelCpuSpeedTimes[i];
                long[] jArr = this.mRelCpuSpeedTimes;
                jArr[i] = jArr[i] - this.mCpuSpeedTimes[i];
                this.mCpuSpeedTimes[i] = temp;
            }
        }
        return this.mRelCpuSpeedTimes;
    }

    private long[] getCpuSpeedTimes(long[] out) {
        long[] tempTimes = out;
        long[] tempSpeeds = this.mCpuSpeeds;
        if (out == null) {
            tempTimes = new long[60];
            tempSpeeds = new long[60];
        }
        int speed = PROCESS_STAT_MINOR_FAULTS;
        String file = readFile("/sys/devices/system/cpu/cpu0/cpufreq/stats/time_in_state", '\u0000');
        if (file != null) {
            StringTokenizer st = new StringTokenizer(file, "\n ");
            while (st.hasMoreElements()) {
                try {
                    tempSpeeds[speed] = Long.parseLong(st.nextToken());
                    tempTimes[speed] = Long.parseLong(st.nextToken());
                    speed += PROCESS_STAT_MAJOR_FAULTS;
                    if (speed == 60) {
                        break;
                    }
                } catch (NumberFormatException e) {
                    Slog.i(TAG, "Unable to parse time_in_state");
                }
            }
        }
        if (out != null) {
            return out;
        }
        out = new long[speed];
        this.mCpuSpeeds = new long[speed];
        System.arraycopy(tempSpeeds, PROCESS_STAT_MINOR_FAULTS, this.mCpuSpeeds, PROCESS_STAT_MINOR_FAULTS, speed);
        System.arraycopy(tempTimes, PROCESS_STAT_MINOR_FAULTS, out, PROCESS_STAT_MINOR_FAULTS, speed);
        return out;
    }

    public final int getLastUserTime() {
        return this.mRelUserTime;
    }

    public final int getLastSystemTime() {
        return this.mRelSystemTime;
    }

    public final int getLastIoWaitTime() {
        return this.mRelIoWaitTime;
    }

    public final int getLastIrqTime() {
        return this.mRelIrqTime;
    }

    public final int getLastSoftIrqTime() {
        return this.mRelSoftIrqTime;
    }

    public final int getLastIdleTime() {
        return this.mRelIdleTime;
    }

    public final float getTotalCpuPercent() {
        int denom = ((this.mRelUserTime + this.mRelSystemTime) + this.mRelIrqTime) + this.mRelIdleTime;
        if (denom <= 0) {
            return 0.0f;
        }
        return (((float) ((this.mRelUserTime + this.mRelSystemTime) + this.mRelIrqTime)) * 100.0f) / ((float) denom);
    }

    final void buildWorkingProcs() {
        if (!this.mWorkingProcsSorted) {
            this.mWorkingProcs.clear();
            int N = this.mProcStats.size();
            for (int i = PROCESS_STAT_MINOR_FAULTS; i < N; i += PROCESS_STAT_MAJOR_FAULTS) {
                Stats stats = (Stats) this.mProcStats.get(i);
                if (stats.working) {
                    this.mWorkingProcs.add(stats);
                    if (stats.threadStats != null && stats.threadStats.size() > PROCESS_STAT_MAJOR_FAULTS) {
                        stats.workingThreads.clear();
                        int M = stats.threadStats.size();
                        for (int j = PROCESS_STAT_MINOR_FAULTS; j < M; j += PROCESS_STAT_MAJOR_FAULTS) {
                            Stats tstats = (Stats) stats.threadStats.get(j);
                            if (tstats.working) {
                                stats.workingThreads.add(tstats);
                            }
                        }
                        Collections.sort(stats.workingThreads, sLoadComparator);
                    }
                }
            }
            Collections.sort(this.mWorkingProcs, sLoadComparator);
            this.mWorkingProcsSorted = true;
        }
    }

    public final int countStats() {
        return this.mProcStats.size();
    }

    public final Stats getStats(int index) {
        return (Stats) this.mProcStats.get(index);
    }

    public final int countWorkingStats() {
        buildWorkingProcs();
        return this.mWorkingProcs.size();
    }

    public final Stats getWorkingStats(int index) {
        return (Stats) this.mWorkingProcs.get(index);
    }

    public final String printCurrentLoad() {
        Writer sw = new StringWriter();
        PrintWriter pw = new FastPrintWriter(sw, (boolean) DEBUG, (int) RILConstants.RIL_REQUEST_SET_DATA_PROFILE);
        pw.print("Load: ");
        pw.print(this.mLoad1);
        pw.print(" / ");
        pw.print(this.mLoad5);
        pw.print(" / ");
        pw.println(this.mLoad15);
        pw.flush();
        return sw.toString();
    }

    public final String printCurrentState(long now) {
        buildWorkingProcs();
        Writer sw = new StringWriter();
        PrintWriter pw = new FastPrintWriter(sw, (boolean) DEBUG, (int) GL10.GL_STENCIL_BUFFER_BIT);
        pw.print("CPU usage from ");
        if (now > this.mLastSampleTime) {
            pw.print(now - this.mLastSampleTime);
            pw.print("ms to ");
            pw.print(now - this.mCurrentSampleTime);
            pw.print("ms ago");
        } else {
            pw.print(this.mLastSampleTime - now);
            pw.print("ms to ");
            pw.print(this.mCurrentSampleTime - now);
            pw.print("ms later");
        }
        long sampleRealTime = this.mCurrentSampleRealTime - this.mLastSampleRealTime;
        long percAwake = sampleRealTime > 0 ? (100 * (this.mCurrentSampleTime - this.mLastSampleTime)) / sampleRealTime : 0;
        if (percAwake != 100) {
            pw.print(" with ");
            pw.print(percAwake);
            pw.print("% awake");
        }
        pw.println(":");
        int totalTime = ((((this.mRelUserTime + this.mRelSystemTime) + this.mRelIoWaitTime) + this.mRelIrqTime) + this.mRelSoftIrqTime) + this.mRelIdleTime;
        int N = this.mWorkingProcs.size();
        for (int i = PROCESS_STAT_MINOR_FAULTS; i < N; i += PROCESS_STAT_MAJOR_FAULTS) {
            Stats st = (Stats) this.mWorkingProcs.get(i);
            String str = st.added ? " +" : st.removed ? " -" : "  ";
            printProcessCPU(pw, str, st.pid, st.name, ((int) (st.rel_uptime + 5)) / 10, st.rel_utime, st.rel_stime, PROCESS_STAT_MINOR_FAULTS, PROCESS_STAT_MINOR_FAULTS, PROCESS_STAT_MINOR_FAULTS, st.rel_minfaults, st.rel_majfaults);
            if (!(st.removed || st.workingThreads == null)) {
                int M = st.workingThreads.size();
                for (int j = PROCESS_STAT_MINOR_FAULTS; j < M; j += PROCESS_STAT_MAJOR_FAULTS) {
                    Stats tst = (Stats) st.workingThreads.get(j);
                    str = tst.added ? "   +" : tst.removed ? "   -" : "    ";
                    printProcessCPU(pw, str, tst.pid, tst.name, ((int) (st.rel_uptime + 5)) / 10, tst.rel_utime, tst.rel_stime, PROCESS_STAT_MINOR_FAULTS, PROCESS_STAT_MINOR_FAULTS, PROCESS_STAT_MINOR_FAULTS, PROCESS_STAT_MINOR_FAULTS, PROCESS_STAT_MINOR_FAULTS);
                }
            }
        }
        printProcessCPU(pw, "", -1, "TOTAL", totalTime, this.mRelUserTime, this.mRelSystemTime, this.mRelIoWaitTime, this.mRelIrqTime, this.mRelSoftIrqTime, PROCESS_STAT_MINOR_FAULTS, PROCESS_STAT_MINOR_FAULTS);
        pw.flush();
        return sw.toString();
    }

    private void printRatio(PrintWriter pw, long numerator, long denominator) {
        long thousands = (1000 * numerator) / denominator;
        long hundreds = thousands / 10;
        pw.print(hundreds);
        if (hundreds < 10) {
            long remainder = thousands - (hundreds * 10);
            if (remainder != 0) {
                pw.print('.');
                pw.print(remainder);
            }
        }
    }

    private void printProcessCPU(PrintWriter pw, String prefix, int pid, String label, int totalTime, int user, int system, int iowait, int irq, int softIrq, int minFaults, int majFaults) {
        pw.print(prefix);
        if (totalTime == 0) {
            totalTime = PROCESS_STAT_MAJOR_FAULTS;
        }
        printRatio(pw, (long) ((((user + system) + iowait) + irq) + softIrq), (long) totalTime);
        pw.print("% ");
        if (pid >= 0) {
            pw.print(pid);
            pw.print("/");
        }
        pw.print(label);
        pw.print(": ");
        printRatio(pw, (long) user, (long) totalTime);
        pw.print("% user + ");
        printRatio(pw, (long) system, (long) totalTime);
        pw.print("% kernel");
        if (iowait > 0) {
            pw.print(" + ");
            printRatio(pw, (long) iowait, (long) totalTime);
            pw.print("% iowait");
        }
        if (irq > 0) {
            pw.print(" + ");
            printRatio(pw, (long) irq, (long) totalTime);
            pw.print("% irq");
        }
        if (softIrq > 0) {
            pw.print(" + ");
            printRatio(pw, (long) softIrq, (long) totalTime);
            pw.print("% softirq");
        }
        if (minFaults > 0 || majFaults > 0) {
            pw.print(" / faults:");
            if (minFaults > 0) {
                pw.print(" ");
                pw.print(minFaults);
                pw.print(" minor");
            }
            if (majFaults > 0) {
                pw.print(" ");
                pw.print(majFaults);
                pw.print(" major");
            }
        }
        pw.println();
    }

    private String readFile(String file, char endChar) {
        Throwable th;
        ThreadPolicy savedPolicy = StrictMode.allowThreadDiskReads();
        FileInputStream is = null;
        try {
            FileInputStream is2 = new FileInputStream(file);
            try {
                int len = is2.read(this.mBuffer);
                is2.close();
                if (len > 0) {
                    int i = PROCESS_STAT_MINOR_FAULTS;
                    while (i < len && this.mBuffer[i] != endChar) {
                        i += PROCESS_STAT_MAJOR_FAULTS;
                    }
                    String str = new String(this.mBuffer, PROCESS_STAT_MINOR_FAULTS, i);
                    IoUtils.closeQuietly(is2);
                    StrictMode.setThreadPolicy(savedPolicy);
                    is = is2;
                    return str;
                }
                IoUtils.closeQuietly(is2);
                StrictMode.setThreadPolicy(savedPolicy);
                is = is2;
                return null;
            } catch (FileNotFoundException e) {
                is = is2;
                IoUtils.closeQuietly(is);
                StrictMode.setThreadPolicy(savedPolicy);
                return null;
            } catch (IOException e2) {
                is = is2;
                IoUtils.closeQuietly(is);
                StrictMode.setThreadPolicy(savedPolicy);
                return null;
            } catch (Throwable th2) {
                th = th2;
                is = is2;
                IoUtils.closeQuietly(is);
                StrictMode.setThreadPolicy(savedPolicy);
                throw th;
            }
        } catch (FileNotFoundException e3) {
            IoUtils.closeQuietly(is);
            StrictMode.setThreadPolicy(savedPolicy);
            return null;
        } catch (IOException e4) {
            IoUtils.closeQuietly(is);
            StrictMode.setThreadPolicy(savedPolicy);
            return null;
        } catch (Throwable th3) {
            th = th3;
            IoUtils.closeQuietly(is);
            StrictMode.setThreadPolicy(savedPolicy);
            throw th;
        }
    }

    private void getName(Stats st, String cmdlineFile) {
        String newName = st.name;
        if (st.name == null || st.name.equals("app_process") || st.name.equals("<pre-initialized>")) {
            String cmdName = readFile(cmdlineFile, '\u0000');
            if (cmdName != null && cmdName.length() > PROCESS_STAT_MAJOR_FAULTS) {
                newName = cmdName;
                int i = newName.lastIndexOf("/");
                if (i > 0 && i < newName.length() - 1) {
                    newName = newName.substring(i + PROCESS_STAT_MAJOR_FAULTS);
                }
            }
            if (newName == null) {
                newName = st.baseName;
            }
        }
        if (st.name == null || !newName.equals(st.name)) {
            st.name = newName;
            st.nameWidth = onMeasureProcessName(st.name);
        }
    }
}
