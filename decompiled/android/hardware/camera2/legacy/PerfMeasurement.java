package android.hardware.camera2.legacy;

import android.os.SystemClock;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

class PerfMeasurement {
    public static final int DEFAULT_MAX_QUERIES = 3;
    private static final long FAILED_TIMING = -2;
    private static final long NO_DURATION_YET = -1;
    private static final String TAG = "PerfMeasurement";
    private ArrayList<Long> mCollectedCpuDurations;
    private ArrayList<Long> mCollectedGpuDurations;
    private ArrayList<Long> mCollectedTimestamps;
    private int mCompletedQueryCount;
    private Queue<Long> mCpuDurationsQueue;
    private final long mNativeContext;
    private long mStartTimeNs;
    private Queue<Long> mTimestampQueue;

    private static native long nativeCreateContext(int i);

    private static native void nativeDeleteContext(long j);

    protected static native long nativeGetNextGlDuration(long j);

    private static native boolean nativeQuerySupport();

    protected static native void nativeStartGlTimer(long j);

    protected static native void nativeStopGlTimer(long j);

    public PerfMeasurement() {
        this.mCompletedQueryCount = 0;
        this.mCollectedGpuDurations = new ArrayList();
        this.mCollectedCpuDurations = new ArrayList();
        this.mCollectedTimestamps = new ArrayList();
        this.mTimestampQueue = new LinkedList();
        this.mCpuDurationsQueue = new LinkedList();
        this.mNativeContext = nativeCreateContext(DEFAULT_MAX_QUERIES);
    }

    public PerfMeasurement(int maxQueries) {
        this.mCompletedQueryCount = 0;
        this.mCollectedGpuDurations = new ArrayList();
        this.mCollectedCpuDurations = new ArrayList();
        this.mCollectedTimestamps = new ArrayList();
        this.mTimestampQueue = new LinkedList();
        this.mCpuDurationsQueue = new LinkedList();
        if (maxQueries < 1) {
            throw new IllegalArgumentException("maxQueries is less than 1");
        }
        this.mNativeContext = nativeCreateContext(maxQueries);
    }

    public static boolean isGlTimingSupported() {
        return nativeQuerySupport();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dumpPerformanceData(java.lang.String r11) {
        /*
        r10 = this;
        r0 = new java.io.BufferedWriter;	 Catch:{ IOException -> 0x0060 }
        r4 = new java.io.FileWriter;	 Catch:{ IOException -> 0x0060 }
        r4.<init>(r11);	 Catch:{ IOException -> 0x0060 }
        r0.<init>(r4);	 Catch:{ IOException -> 0x0060 }
        r5 = 0;
        r4 = "timestamp gpu_duration cpu_duration\n";
        r0.write(r4);	 Catch:{ Throwable -> 0x0088, all -> 0x009f }
        r2 = 0;
    L_0x0012:
        r4 = r10.mCollectedGpuDurations;	 Catch:{ Throwable -> 0x0088, all -> 0x009f }
        r4 = r4.size();	 Catch:{ Throwable -> 0x0088, all -> 0x009f }
        if (r2 >= r4) goto L_0x0044;
    L_0x001a:
        r4 = "%d %d %d\n";
        r6 = 3;
        r6 = new java.lang.Object[r6];	 Catch:{ Throwable -> 0x0088, all -> 0x009f }
        r7 = 0;
        r8 = r10.mCollectedTimestamps;	 Catch:{ Throwable -> 0x0088, all -> 0x009f }
        r8 = r8.get(r2);	 Catch:{ Throwable -> 0x0088, all -> 0x009f }
        r6[r7] = r8;	 Catch:{ Throwable -> 0x0088, all -> 0x009f }
        r7 = 1;
        r8 = r10.mCollectedGpuDurations;	 Catch:{ Throwable -> 0x0088, all -> 0x009f }
        r8 = r8.get(r2);	 Catch:{ Throwable -> 0x0088, all -> 0x009f }
        r6[r7] = r8;	 Catch:{ Throwable -> 0x0088, all -> 0x009f }
        r7 = 2;
        r8 = r10.mCollectedCpuDurations;	 Catch:{ Throwable -> 0x0088, all -> 0x009f }
        r8 = r8.get(r2);	 Catch:{ Throwable -> 0x0088, all -> 0x009f }
        r6[r7] = r8;	 Catch:{ Throwable -> 0x0088, all -> 0x009f }
        r4 = java.lang.String.format(r4, r6);	 Catch:{ Throwable -> 0x0088, all -> 0x009f }
        r0.write(r4);	 Catch:{ Throwable -> 0x0088, all -> 0x009f }
        r2 = r2 + 1;
        goto L_0x0012;
    L_0x0044:
        r4 = r10.mCollectedTimestamps;	 Catch:{ Throwable -> 0x0088, all -> 0x009f }
        r4.clear();	 Catch:{ Throwable -> 0x0088, all -> 0x009f }
        r4 = r10.mCollectedGpuDurations;	 Catch:{ Throwable -> 0x0088, all -> 0x009f }
        r4.clear();	 Catch:{ Throwable -> 0x0088, all -> 0x009f }
        r4 = r10.mCollectedCpuDurations;	 Catch:{ Throwable -> 0x0088, all -> 0x009f }
        r4.clear();	 Catch:{ Throwable -> 0x0088, all -> 0x009f }
        if (r0 == 0) goto L_0x005a;
    L_0x0055:
        if (r5 == 0) goto L_0x0084;
    L_0x0057:
        r0.close();	 Catch:{ Throwable -> 0x005b }
    L_0x005a:
        return;
    L_0x005b:
        r3 = move-exception;
        r5.addSuppressed(r3);	 Catch:{ IOException -> 0x0060 }
        goto L_0x005a;
    L_0x0060:
        r1 = move-exception;
        r4 = "PerfMeasurement";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Error writing data dump to ";
        r5 = r5.append(r6);
        r5 = r5.append(r11);
        r6 = ":";
        r5 = r5.append(r6);
        r5 = r5.append(r1);
        r5 = r5.toString();
        android.util.Log.e(r4, r5);
        goto L_0x005a;
    L_0x0084:
        r0.close();	 Catch:{ IOException -> 0x0060 }
        goto L_0x005a;
    L_0x0088:
        r4 = move-exception;
        throw r4;	 Catch:{ all -> 0x008a }
    L_0x008a:
        r5 = move-exception;
        r9 = r5;
        r5 = r4;
        r4 = r9;
    L_0x008e:
        if (r0 == 0) goto L_0x0095;
    L_0x0090:
        if (r5 == 0) goto L_0x009b;
    L_0x0092:
        r0.close();	 Catch:{ Throwable -> 0x0096 }
    L_0x0095:
        throw r4;	 Catch:{ IOException -> 0x0060 }
    L_0x0096:
        r3 = move-exception;
        r5.addSuppressed(r3);	 Catch:{ IOException -> 0x0060 }
        goto L_0x0095;
    L_0x009b:
        r0.close();	 Catch:{ IOException -> 0x0060 }
        goto L_0x0095;
    L_0x009f:
        r4 = move-exception;
        goto L_0x008e;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.hardware.camera2.legacy.PerfMeasurement.dumpPerformanceData(java.lang.String):void");
    }

    public void startTimer() {
        nativeStartGlTimer(this.mNativeContext);
        this.mStartTimeNs = SystemClock.elapsedRealtimeNanos();
    }

    public void stopTimer() {
        long j = NO_DURATION_YET;
        this.mCpuDurationsQueue.add(Long.valueOf(SystemClock.elapsedRealtimeNanos() - this.mStartTimeNs));
        nativeStopGlTimer(this.mNativeContext);
        long duration = getNextGlDuration();
        if (duration > 0) {
            this.mCollectedGpuDurations.add(Long.valueOf(duration));
            this.mCollectedTimestamps.add(Long.valueOf(this.mTimestampQueue.isEmpty() ? NO_DURATION_YET : ((Long) this.mTimestampQueue.poll()).longValue()));
            ArrayList arrayList = this.mCollectedCpuDurations;
            if (!this.mCpuDurationsQueue.isEmpty()) {
                j = ((Long) this.mCpuDurationsQueue.poll()).longValue();
            }
            arrayList.add(Long.valueOf(j));
        }
        if (duration == FAILED_TIMING) {
            if (!this.mTimestampQueue.isEmpty()) {
                this.mTimestampQueue.poll();
            }
            if (!this.mCpuDurationsQueue.isEmpty()) {
                this.mCpuDurationsQueue.poll();
            }
        }
    }

    public void addTimestamp(long timestamp) {
        this.mTimestampQueue.add(Long.valueOf(timestamp));
    }

    private long getNextGlDuration() {
        long duration = nativeGetNextGlDuration(this.mNativeContext);
        if (duration > 0) {
            this.mCompletedQueryCount++;
        }
        return duration;
    }

    public int getCompletedQueryCount() {
        return this.mCompletedQueryCount;
    }

    protected void finalize() {
        nativeDeleteContext(this.mNativeContext);
    }
}
