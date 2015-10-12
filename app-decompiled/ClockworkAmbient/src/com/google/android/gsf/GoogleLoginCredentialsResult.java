package com.google.android.gsf;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class GoogleLoginCredentialsResult implements Parcelable {
    public static final Creator<GoogleLoginCredentialsResult> CREATOR;
    private String mAccount;
    private Intent mCredentialsIntent;
    private String mCredentialsString;

    static {
        CREATOR = new Creator<GoogleLoginCredentialsResult>() {
            public GoogleLoginCredentialsResult createFromParcel(Parcel parcel) {
                return new GoogleLoginCredentialsResult(null);
            }

            public GoogleLoginCredentialsResult[] newArray(int i) {
                return new GoogleLoginCredentialsResult[i];
            }
        };
    }

    public GoogleLoginCredentialsResult() {
        this.mCredentialsString = null;
        this.mCredentialsIntent = null;
        this.mAccount = null;
    }

    private GoogleLoginCredentialsResult(Parcel parcel) {
        readFromParcel(parcel);
    }

    public int describeContents() {
        return this.mCredentialsIntent != null ? this.mCredentialsIntent.describeContents() : 0;
    }

    public void readFromParcel(Parcel parcel) {
        this.mAccount = parcel.readString();
        this.mCredentialsString = parcel.readString();
        int readInt = parcel.readInt();
        this.mCredentialsIntent = null;
        if (readInt == 1) {
            this.mCredentialsIntent = new Intent();
            this.mCredentialsIntent.readFromParcel(parcel);
            this.mCredentialsIntent.setExtrasClassLoader(getClass().getClassLoader());
        }
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mAccount);
        parcel.writeString(this.mCredentialsString);
        if (this.mCredentialsIntent != null) {
            parcel.writeInt(1);
            this.mCredentialsIntent.writeToParcel(parcel, 0);
            return;
        }
        parcel.writeInt(0);
    }
}
