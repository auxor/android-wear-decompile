package android.app.usage;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class UsageStats implements Parcelable {
    public static final Creator<UsageStats> CREATOR;
    public long mBeginTimeStamp;
    public long mEndTimeStamp;
    public int mLastEvent;
    public long mLastTimeUsed;
    public int mLaunchCount;
    public String mPackageName;
    public long mTotalTimeInForeground;

    public UsageStats(UsageStats stats) {
        this.mPackageName = stats.mPackageName;
        this.mBeginTimeStamp = stats.mBeginTimeStamp;
        this.mEndTimeStamp = stats.mEndTimeStamp;
        this.mLastTimeUsed = stats.mLastTimeUsed;
        this.mTotalTimeInForeground = stats.mTotalTimeInForeground;
        this.mLaunchCount = stats.mLaunchCount;
        this.mLastEvent = stats.mLastEvent;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public long getFirstTimeStamp() {
        return this.mBeginTimeStamp;
    }

    public long getLastTimeStamp() {
        return this.mEndTimeStamp;
    }

    public long getLastTimeUsed() {
        return this.mLastTimeUsed;
    }

    public long getTotalTimeInForeground() {
        return this.mTotalTimeInForeground;
    }

    public void add(UsageStats right) {
        if (this.mPackageName.equals(right.mPackageName)) {
            if (right.mEndTimeStamp > this.mEndTimeStamp) {
                this.mLastEvent = right.mLastEvent;
                this.mEndTimeStamp = right.mEndTimeStamp;
                this.mLastTimeUsed = right.mLastTimeUsed;
            }
            this.mBeginTimeStamp = Math.min(this.mBeginTimeStamp, right.mBeginTimeStamp);
            this.mTotalTimeInForeground += right.mTotalTimeInForeground;
            this.mLaunchCount += right.mLaunchCount;
            return;
        }
        throw new IllegalArgumentException("Can't merge UsageStats for package '" + this.mPackageName + "' with UsageStats for package '" + right.mPackageName + "'.");
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mPackageName);
        dest.writeLong(this.mBeginTimeStamp);
        dest.writeLong(this.mEndTimeStamp);
        dest.writeLong(this.mLastTimeUsed);
        dest.writeLong(this.mTotalTimeInForeground);
        dest.writeInt(this.mLaunchCount);
        dest.writeInt(this.mLastEvent);
    }

    static {
        CREATOR = new Creator<UsageStats>() {
            public UsageStats createFromParcel(Parcel in) {
                UsageStats stats = new UsageStats();
                stats.mPackageName = in.readString();
                stats.mBeginTimeStamp = in.readLong();
                stats.mEndTimeStamp = in.readLong();
                stats.mLastTimeUsed = in.readLong();
                stats.mTotalTimeInForeground = in.readLong();
                stats.mLaunchCount = in.readInt();
                stats.mLastEvent = in.readInt();
                return stats;
            }

            public UsageStats[] newArray(int size) {
                return new UsageStats[size];
            }
        };
    }
}
