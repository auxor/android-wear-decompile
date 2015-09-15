package android.app.job;

import android.content.ComponentName;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.PersistableBundle;

public class JobInfo implements Parcelable {
    public static final int BACKOFF_POLICY_EXPONENTIAL = 1;
    public static final int BACKOFF_POLICY_LINEAR = 0;
    public static final Creator<JobInfo> CREATOR;
    public static final int DEFAULT_BACKOFF_POLICY = 1;
    public static final long DEFAULT_INITIAL_BACKOFF_MILLIS = 30000;
    public static final long MAX_BACKOFF_DELAY_MILLIS = 18000000;
    public static final int NETWORK_TYPE_ANY = 1;
    public static final int NETWORK_TYPE_NONE = 0;
    public static final int NETWORK_TYPE_UNMETERED = 2;
    private final int backoffPolicy;
    private final PersistableBundle extras;
    private final boolean hasEarlyConstraint;
    private final boolean hasLateConstraint;
    private final long initialBackoffMillis;
    private final long intervalMillis;
    private final boolean isPeriodic;
    private final boolean isPersisted;
    private final int jobId;
    private final long maxExecutionDelayMillis;
    private final long minLatencyMillis;
    private final int networkType;
    private final boolean requireCharging;
    private final boolean requireDeviceIdle;
    private final ComponentName service;

    public static final class Builder {
        private int mBackoffPolicy;
        private boolean mBackoffPolicySet;
        private PersistableBundle mExtras;
        private boolean mHasEarlyConstraint;
        private boolean mHasLateConstraint;
        private long mInitialBackoffMillis;
        private long mIntervalMillis;
        private boolean mIsPeriodic;
        private boolean mIsPersisted;
        private int mJobId;
        private ComponentName mJobService;
        private long mMaxExecutionDelayMillis;
        private long mMinLatencyMillis;
        private int mNetworkType;
        private boolean mRequiresCharging;
        private boolean mRequiresDeviceIdle;

        public Builder(int jobId, ComponentName jobService) {
            this.mExtras = PersistableBundle.EMPTY;
            this.mInitialBackoffMillis = JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS;
            this.mBackoffPolicy = JobInfo.NETWORK_TYPE_ANY;
            this.mBackoffPolicySet = false;
            this.mJobService = jobService;
            this.mJobId = jobId;
        }

        public Builder setExtras(PersistableBundle extras) {
            this.mExtras = extras;
            return this;
        }

        public Builder setRequiredNetworkType(int networkType) {
            this.mNetworkType = networkType;
            return this;
        }

        public Builder setRequiresCharging(boolean requiresCharging) {
            this.mRequiresCharging = requiresCharging;
            return this;
        }

        public Builder setRequiresDeviceIdle(boolean requiresDeviceIdle) {
            this.mRequiresDeviceIdle = requiresDeviceIdle;
            return this;
        }

        public Builder setPeriodic(long intervalMillis) {
            this.mIsPeriodic = true;
            this.mIntervalMillis = intervalMillis;
            this.mHasLateConstraint = true;
            this.mHasEarlyConstraint = true;
            return this;
        }

        public Builder setMinimumLatency(long minLatencyMillis) {
            this.mMinLatencyMillis = minLatencyMillis;
            this.mHasEarlyConstraint = true;
            return this;
        }

        public Builder setOverrideDeadline(long maxExecutionDelayMillis) {
            this.mMaxExecutionDelayMillis = maxExecutionDelayMillis;
            this.mHasLateConstraint = true;
            return this;
        }

        public Builder setBackoffCriteria(long initialBackoffMillis, int backoffPolicy) {
            this.mBackoffPolicySet = true;
            this.mInitialBackoffMillis = initialBackoffMillis;
            this.mBackoffPolicy = backoffPolicy;
            return this;
        }

        public Builder setPersisted(boolean isPersisted) {
            this.mIsPersisted = isPersisted;
            return this;
        }

        public JobInfo build() {
            if (this.mHasEarlyConstraint || this.mHasLateConstraint || this.mRequiresCharging || this.mRequiresDeviceIdle || this.mNetworkType != 0) {
                this.mExtras = new PersistableBundle(this.mExtras);
                if (this.mIsPeriodic && this.mMaxExecutionDelayMillis != 0) {
                    throw new IllegalArgumentException("Can't call setOverrideDeadline() on a periodic job.");
                } else if (this.mIsPeriodic && this.mMinLatencyMillis != 0) {
                    throw new IllegalArgumentException("Can't call setMinimumLatency() on a periodic job");
                } else if (!this.mBackoffPolicySet || !this.mRequiresDeviceIdle) {
                    return new JobInfo();
                } else {
                    throw new IllegalArgumentException("An idle mode job will not respect any back-off policy, so calling setBackoffCriteria with setRequiresDeviceIdle is an error.");
                }
            }
            throw new IllegalArgumentException("You're trying to build a job with no constraints, this is not allowed.");
        }
    }

    public int getId() {
        return this.jobId;
    }

    public PersistableBundle getExtras() {
        return this.extras;
    }

    public ComponentName getService() {
        return this.service;
    }

    public boolean isRequireCharging() {
        return this.requireCharging;
    }

    public boolean isRequireDeviceIdle() {
        return this.requireDeviceIdle;
    }

    public int getNetworkType() {
        return this.networkType;
    }

    public long getMinLatencyMillis() {
        return this.minLatencyMillis;
    }

    public long getMaxExecutionDelayMillis() {
        return this.maxExecutionDelayMillis;
    }

    public boolean isPeriodic() {
        return this.isPeriodic;
    }

    public boolean isPersisted() {
        return this.isPersisted;
    }

    public long getIntervalMillis() {
        return this.intervalMillis;
    }

    public long getInitialBackoffMillis() {
        return this.initialBackoffMillis;
    }

    public int getBackoffPolicy() {
        return this.backoffPolicy;
    }

    public boolean hasEarlyConstraint() {
        return this.hasEarlyConstraint;
    }

    public boolean hasLateConstraint() {
        return this.hasLateConstraint;
    }

    private JobInfo(Parcel in) {
        boolean z;
        boolean z2 = true;
        this.jobId = in.readInt();
        this.extras = in.readPersistableBundle();
        this.service = (ComponentName) in.readParcelable(null);
        this.requireCharging = in.readInt() == NETWORK_TYPE_ANY;
        if (in.readInt() == NETWORK_TYPE_ANY) {
            z = true;
        } else {
            z = false;
        }
        this.requireDeviceIdle = z;
        this.networkType = in.readInt();
        this.minLatencyMillis = in.readLong();
        this.maxExecutionDelayMillis = in.readLong();
        if (in.readInt() == NETWORK_TYPE_ANY) {
            z = true;
        } else {
            z = false;
        }
        this.isPeriodic = z;
        if (in.readInt() == NETWORK_TYPE_ANY) {
            z = true;
        } else {
            z = false;
        }
        this.isPersisted = z;
        this.intervalMillis = in.readLong();
        this.initialBackoffMillis = in.readLong();
        this.backoffPolicy = in.readInt();
        if (in.readInt() == NETWORK_TYPE_ANY) {
            z = true;
        } else {
            z = false;
        }
        this.hasEarlyConstraint = z;
        if (in.readInt() != NETWORK_TYPE_ANY) {
            z2 = false;
        }
        this.hasLateConstraint = z2;
    }

    private JobInfo(Builder b) {
        this.jobId = b.mJobId;
        this.extras = b.mExtras;
        this.service = b.mJobService;
        this.requireCharging = b.mRequiresCharging;
        this.requireDeviceIdle = b.mRequiresDeviceIdle;
        this.networkType = b.mNetworkType;
        this.minLatencyMillis = b.mMinLatencyMillis;
        this.maxExecutionDelayMillis = b.mMaxExecutionDelayMillis;
        this.isPeriodic = b.mIsPeriodic;
        this.isPersisted = b.mIsPersisted;
        this.intervalMillis = b.mIntervalMillis;
        this.initialBackoffMillis = b.mInitialBackoffMillis;
        this.backoffPolicy = b.mBackoffPolicy;
        this.hasEarlyConstraint = b.mHasEarlyConstraint;
        this.hasLateConstraint = b.mHasLateConstraint;
    }

    public int describeContents() {
        return NETWORK_TYPE_NONE;
    }

    public void writeToParcel(Parcel out, int flags) {
        int i;
        int i2 = NETWORK_TYPE_ANY;
        out.writeInt(this.jobId);
        out.writePersistableBundle(this.extras);
        out.writeParcelable(this.service, flags);
        out.writeInt(this.requireCharging ? NETWORK_TYPE_ANY : NETWORK_TYPE_NONE);
        if (this.requireDeviceIdle) {
            i = NETWORK_TYPE_ANY;
        } else {
            i = NETWORK_TYPE_NONE;
        }
        out.writeInt(i);
        out.writeInt(this.networkType);
        out.writeLong(this.minLatencyMillis);
        out.writeLong(this.maxExecutionDelayMillis);
        if (this.isPeriodic) {
            i = NETWORK_TYPE_ANY;
        } else {
            i = NETWORK_TYPE_NONE;
        }
        out.writeInt(i);
        if (this.isPersisted) {
            i = NETWORK_TYPE_ANY;
        } else {
            i = NETWORK_TYPE_NONE;
        }
        out.writeInt(i);
        out.writeLong(this.intervalMillis);
        out.writeLong(this.initialBackoffMillis);
        out.writeInt(this.backoffPolicy);
        if (this.hasEarlyConstraint) {
            i = NETWORK_TYPE_ANY;
        } else {
            i = NETWORK_TYPE_NONE;
        }
        out.writeInt(i);
        if (!this.hasLateConstraint) {
            i2 = NETWORK_TYPE_NONE;
        }
        out.writeInt(i2);
    }

    static {
        CREATOR = new Creator<JobInfo>() {
            public JobInfo createFromParcel(Parcel in) {
                return new JobInfo(null);
            }

            public JobInfo[] newArray(int size) {
                return new JobInfo[size];
            }
        };
    }

    public String toString() {
        return "(job:" + this.jobId + "/" + this.service.flattenToShortString() + ")";
    }
}
