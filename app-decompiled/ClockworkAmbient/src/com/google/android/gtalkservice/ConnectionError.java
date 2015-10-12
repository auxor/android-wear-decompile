package com.google.android.gtalkservice;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class ConnectionError implements Parcelable {
    public static final Creator<ConnectionError> CREATOR;
    private int mError;

    static {
        CREATOR = new Creator<ConnectionError>() {
            public ConnectionError createFromParcel(Parcel parcel) {
                return new ConnectionError(parcel);
            }

            public ConnectionError[] newArray(int i) {
                return new ConnectionError[i];
            }
        };
    }

    public ConnectionError(Parcel parcel) {
        this.mError = parcel.readInt();
    }

    public static final String toString(int i) {
        switch (i) {
            case 1:
                return "NO NETWORK";
            case 2:
                return "CONNECTION FAILED";
            case 3:
                return "UNKNOWN HOST";
            case 4:
                return "AUTH FAILED";
            case 5:
                return "AUTH EXPIRED";
            case 6:
                return "HEARTBEAT TIMEOUT";
            case 7:
                return "SERVER FAILED";
            case 8:
                return "SERVER REJECT - RATE LIMIT";
            case 10:
                return "UNKNOWN";
            default:
                return "NO ERROR";
        }
    }

    public int describeContents() {
        return 0;
    }

    public final String toString() {
        return toString(this.mError);
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mError);
    }
}
