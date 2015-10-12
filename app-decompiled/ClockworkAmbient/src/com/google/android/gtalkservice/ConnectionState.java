package com.google.android.gtalkservice;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class ConnectionState implements Parcelable {
    public static final Creator<ConnectionState> CREATOR;
    private volatile int mState;

    static {
        CREATOR = new Creator<ConnectionState>() {
            public ConnectionState createFromParcel(Parcel parcel) {
                return new ConnectionState(parcel);
            }

            public ConnectionState[] newArray(int i) {
                return new ConnectionState[i];
            }
        };
    }

    public ConnectionState(Parcel parcel) {
        this.mState = parcel.readInt();
    }

    public static final String toString(int i) {
        switch (i) {
            case 1:
                return "RECONNECTION_SCHEDULED";
            case 2:
                return "CONNECTING";
            case 3:
                return "AUTHENTICATED";
            case 4:
                return "ONLINE";
            default:
                return "IDLE";
        }
    }

    public int describeContents() {
        return 0;
    }

    public final String toString() {
        return toString(this.mState);
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mState);
    }
}
