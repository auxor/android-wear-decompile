package android.net;

import android.os.SystemClock;
import android.util.Slog;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class SamplingDataTracker {
    private static final boolean DBG = false;
    private static final String TAG = "SamplingDataTracker";
    private final int MINIMUM_SAMPLED_PACKETS;
    private final int MINIMUM_SAMPLING_INTERVAL;
    private SamplingSnapshot mBeginningSample;
    private SamplingSnapshot mEndingSample;
    private SamplingSnapshot mLastSample;
    public final Object mSamplingDataLock;

    public static class SamplingSnapshot {
        public long mRxByteCount;
        public long mRxPacketCount;
        public long mRxPacketErrorCount;
        public long mTimestamp;
        public long mTxByteCount;
        public long mTxPacketCount;
        public long mTxPacketErrorCount;
    }

    public SamplingDataTracker() {
        this.mSamplingDataLock = new Object();
        this.MINIMUM_SAMPLING_INTERVAL = 15000;
        this.MINIMUM_SAMPLED_PACKETS = 30;
    }

    public static void getSamplingSnapshots(Map<String, SamplingSnapshot> mapIfaceToSample) {
        Throwable th;
        BufferedReader reader = null;
        try {
            BufferedReader reader2 = new BufferedReader(new FileReader("/proc/net/dev"));
            try {
                reader2.readLine();
                reader2.readLine();
                while (true) {
                    String line = reader2.readLine();
                    if (line == null) {
                        break;
                    }
                    String[] tokens = line.trim().split("[ ]+");
                    if (tokens.length >= 17) {
                        String currentIface = tokens[0].split(":")[0];
                        if (mapIfaceToSample.containsKey(currentIface)) {
                            try {
                                SamplingSnapshot ss = new SamplingSnapshot();
                                ss.mTxByteCount = Long.parseLong(tokens[1]);
                                ss.mTxPacketCount = Long.parseLong(tokens[2]);
                                ss.mTxPacketErrorCount = Long.parseLong(tokens[3]);
                                ss.mRxByteCount = Long.parseLong(tokens[9]);
                                ss.mRxPacketCount = Long.parseLong(tokens[10]);
                                ss.mRxPacketErrorCount = Long.parseLong(tokens[11]);
                                ss.mTimestamp = SystemClock.elapsedRealtime();
                                mapIfaceToSample.put(currentIface, ss);
                            } catch (NumberFormatException e) {
                            }
                        }
                    }
                }
                if (reader2 != null) {
                    try {
                        reader2.close();
                    } catch (IOException e2) {
                        Slog.e(TAG, "could not close /proc/net/dev");
                        reader = reader2;
                        return;
                    }
                }
                reader = reader2;
            } catch (FileNotFoundException e3) {
                reader = reader2;
                try {
                    Slog.e(TAG, "could not find /proc/net/dev");
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e4) {
                            Slog.e(TAG, "could not close /proc/net/dev");
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e5) {
                            Slog.e(TAG, "could not close /proc/net/dev");
                        }
                    }
                    throw th;
                }
            } catch (IOException e6) {
                reader = reader2;
                Slog.e(TAG, "could not read /proc/net/dev");
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e7) {
                        Slog.e(TAG, "could not close /proc/net/dev");
                    }
                }
            } catch (Throwable th3) {
                th = th3;
                reader = reader2;
                if (reader != null) {
                    reader.close();
                }
                throw th;
            }
        } catch (FileNotFoundException e8) {
            Slog.e(TAG, "could not find /proc/net/dev");
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e9) {
            Slog.e(TAG, "could not read /proc/net/dev");
            if (reader != null) {
                reader.close();
            }
        }
    }

    public void startSampling(SamplingSnapshot s) {
        synchronized (this.mSamplingDataLock) {
            this.mLastSample = s;
        }
    }

    public void stopSampling(SamplingSnapshot s) {
        synchronized (this.mSamplingDataLock) {
            if (this.mLastSample != null && s.mTimestamp - this.mLastSample.mTimestamp > 15000 && getSampledPacketCount(this.mLastSample, s) > 30) {
                this.mBeginningSample = this.mLastSample;
                this.mEndingSample = s;
                this.mLastSample = null;
            }
        }
    }

    public void resetSamplingData() {
        synchronized (this.mSamplingDataLock) {
            this.mLastSample = null;
        }
    }

    public long getSampledTxByteCount() {
        long j;
        synchronized (this.mSamplingDataLock) {
            if (this.mBeginningSample == null || this.mEndingSample == null) {
                j = LinkQualityInfo.UNKNOWN_LONG;
            } else {
                j = this.mEndingSample.mTxByteCount - this.mBeginningSample.mTxByteCount;
            }
        }
        return j;
    }

    public long getSampledTxPacketCount() {
        long j;
        synchronized (this.mSamplingDataLock) {
            if (this.mBeginningSample == null || this.mEndingSample == null) {
                j = LinkQualityInfo.UNKNOWN_LONG;
            } else {
                j = this.mEndingSample.mTxPacketCount - this.mBeginningSample.mTxPacketCount;
            }
        }
        return j;
    }

    public long getSampledTxPacketErrorCount() {
        long j;
        synchronized (this.mSamplingDataLock) {
            if (this.mBeginningSample == null || this.mEndingSample == null) {
                j = LinkQualityInfo.UNKNOWN_LONG;
            } else {
                j = this.mEndingSample.mTxPacketErrorCount - this.mBeginningSample.mTxPacketErrorCount;
            }
        }
        return j;
    }

    public long getSampledRxByteCount() {
        long j;
        synchronized (this.mSamplingDataLock) {
            if (this.mBeginningSample == null || this.mEndingSample == null) {
                j = LinkQualityInfo.UNKNOWN_LONG;
            } else {
                j = this.mEndingSample.mRxByteCount - this.mBeginningSample.mRxByteCount;
            }
        }
        return j;
    }

    public long getSampledRxPacketCount() {
        long j;
        synchronized (this.mSamplingDataLock) {
            if (this.mBeginningSample == null || this.mEndingSample == null) {
                j = LinkQualityInfo.UNKNOWN_LONG;
            } else {
                j = this.mEndingSample.mRxPacketCount - this.mBeginningSample.mRxPacketCount;
            }
        }
        return j;
    }

    public long getSampledPacketCount() {
        return getSampledPacketCount(this.mBeginningSample, this.mEndingSample);
    }

    public long getSampledPacketCount(SamplingSnapshot begin, SamplingSnapshot end) {
        if (begin == null || end == null) {
            return LinkQualityInfo.UNKNOWN_LONG;
        }
        return (end.mRxPacketCount - begin.mRxPacketCount) + (end.mTxPacketCount - begin.mTxPacketCount);
    }

    public long getSampledPacketErrorCount() {
        if (this.mBeginningSample == null || this.mEndingSample == null) {
            return LinkQualityInfo.UNKNOWN_LONG;
        }
        return getSampledRxPacketErrorCount() + getSampledTxPacketErrorCount();
    }

    public long getSampledRxPacketErrorCount() {
        long j;
        synchronized (this.mSamplingDataLock) {
            if (this.mBeginningSample == null || this.mEndingSample == null) {
                j = LinkQualityInfo.UNKNOWN_LONG;
            } else {
                j = this.mEndingSample.mRxPacketErrorCount - this.mBeginningSample.mRxPacketErrorCount;
            }
        }
        return j;
    }

    public long getSampleTimestamp() {
        long j;
        synchronized (this.mSamplingDataLock) {
            if (this.mEndingSample != null) {
                j = this.mEndingSample.mTimestamp;
            } else {
                j = LinkQualityInfo.UNKNOWN_LONG;
            }
        }
        return j;
    }

    public int getSampleDuration() {
        int i;
        synchronized (this.mSamplingDataLock) {
            if (this.mBeginningSample == null || this.mEndingSample == null) {
                i = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
            } else {
                i = (int) (this.mEndingSample.mTimestamp - this.mBeginningSample.mTimestamp);
            }
        }
        return i;
    }

    public void setCommonLinkQualityInfoFields(LinkQualityInfo li) {
        synchronized (this.mSamplingDataLock) {
            li.setLastDataSampleTime(getSampleTimestamp());
            li.setDataSampleDuration(getSampleDuration());
            li.setPacketCount(getSampledPacketCount());
            li.setPacketErrorCount(getSampledPacketErrorCount());
        }
    }
}
