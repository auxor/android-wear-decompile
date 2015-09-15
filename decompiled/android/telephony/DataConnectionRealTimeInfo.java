package android.telephony;

import android.net.LinkQualityInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class DataConnectionRealTimeInfo implements Parcelable {
    public static final Creator<DataConnectionRealTimeInfo> CREATOR;
    public static int DC_POWER_STATE_HIGH;
    public static int DC_POWER_STATE_LOW;
    public static int DC_POWER_STATE_MEDIUM;
    public static int DC_POWER_STATE_UNKNOWN;
    private int mDcPowerState;
    private long mTime;

    static {
        DC_POWER_STATE_LOW = 1;
        DC_POWER_STATE_MEDIUM = 2;
        DC_POWER_STATE_HIGH = 3;
        DC_POWER_STATE_UNKNOWN = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        CREATOR = new Creator<DataConnectionRealTimeInfo>() {
            public DataConnectionRealTimeInfo createFromParcel(Parcel in) {
                return new DataConnectionRealTimeInfo(null);
            }

            public DataConnectionRealTimeInfo[] newArray(int size) {
                return new DataConnectionRealTimeInfo[size];
            }
        };
    }

    public DataConnectionRealTimeInfo(long time, int dcPowerState) {
        this.mTime = time;
        this.mDcPowerState = dcPowerState;
    }

    public DataConnectionRealTimeInfo() {
        this.mTime = LinkQualityInfo.UNKNOWN_LONG;
        this.mDcPowerState = DC_POWER_STATE_UNKNOWN;
    }

    private DataConnectionRealTimeInfo(Parcel in) {
        this.mTime = in.readLong();
        this.mDcPowerState = in.readInt();
    }

    public long getTime() {
        return this.mTime;
    }

    public int getDcPowerState() {
        return this.mDcPowerState;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(this.mTime);
        out.writeInt(this.mDcPowerState);
    }

    public int hashCode() {
        long result = (17 * 1) + this.mTime;
        return (int) (result + ((17 * result) + ((long) this.mDcPowerState)));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DataConnectionRealTimeInfo other = (DataConnectionRealTimeInfo) obj;
        if (this.mTime == other.mTime && this.mDcPowerState == other.mDcPowerState) {
            return true;
        }
        return false;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("mTime=").append(this.mTime);
        sb.append(" mDcPowerState=").append(this.mDcPowerState);
        return sb.toString();
    }
}
