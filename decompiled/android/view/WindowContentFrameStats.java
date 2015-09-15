package android.view;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class WindowContentFrameStats extends FrameStats implements Parcelable {
    public static final Creator<WindowContentFrameStats> CREATOR;
    private long[] mFramesPostedTimeNano;
    private long[] mFramesReadyTimeNano;

    public void init(long refreshPeriodNano, long[] framesPostedTimeNano, long[] framesPresentedTimeNano, long[] framesReadyTimeNano) {
        this.mRefreshPeriodNano = refreshPeriodNano;
        this.mFramesPostedTimeNano = framesPostedTimeNano;
        this.mFramesPresentedTimeNano = framesPresentedTimeNano;
        this.mFramesReadyTimeNano = framesReadyTimeNano;
    }

    private WindowContentFrameStats(Parcel parcel) {
        this.mRefreshPeriodNano = parcel.readLong();
        this.mFramesPostedTimeNano = parcel.createLongArray();
        this.mFramesPresentedTimeNano = parcel.createLongArray();
        this.mFramesReadyTimeNano = parcel.createLongArray();
    }

    public long getFramePostedTimeNano(int index) {
        if (this.mFramesPostedTimeNano != null) {
            return this.mFramesPostedTimeNano[index];
        }
        throw new IndexOutOfBoundsException();
    }

    public long getFrameReadyTimeNano(int index) {
        if (this.mFramesReadyTimeNano != null) {
            return this.mFramesReadyTimeNano[index];
        }
        throw new IndexOutOfBoundsException();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(this.mRefreshPeriodNano);
        parcel.writeLongArray(this.mFramesPostedTimeNano);
        parcel.writeLongArray(this.mFramesPresentedTimeNano);
        parcel.writeLongArray(this.mFramesReadyTimeNano);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WindowContentFrameStats[");
        builder.append("frameCount:" + getFrameCount());
        builder.append(", fromTimeNano:" + getStartTimeNano());
        builder.append(", toTimeNano:" + getEndTimeNano());
        builder.append(']');
        return builder.toString();
    }

    static {
        CREATOR = new Creator<WindowContentFrameStats>() {
            public WindowContentFrameStats createFromParcel(Parcel parcel) {
                return new WindowContentFrameStats(null);
            }

            public WindowContentFrameStats[] newArray(int size) {
                return new WindowContentFrameStats[size];
            }
        };
    }
}
