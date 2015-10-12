package com.google.android.gsf;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class LoginData implements Parcelable {
    public static final Creator<LoginData> CREATOR;
    public String mAuthtoken;
    public String mCaptchaAnswer;
    public byte[] mCaptchaData;
    public String mCaptchaMimeType;
    public String mCaptchaToken;
    public String mEncryptedPassword;
    public int mFlags;
    public String mJsonString;
    public String mOAuthAccessToken;
    public String mPassword;
    public String mService;
    public String mSid;
    public Status mStatus;
    public String mUsername;

    public enum Status {
        SUCCESS,
        ACCOUNT_DISABLED,
        BAD_USERNAME,
        BAD_REQUEST,
        LOGIN_FAIL,
        SERVER_ERROR,
        MISSING_APPS,
        NO_GMAIL,
        NETWORK_ERROR,
        CAPTCHA,
        CANCELLED,
        DELETED_GMAIL,
        OAUTH_MIGRATION_REQUIRED,
        DMAGENT
    }

    static {
        CREATOR = new Creator<LoginData>() {
            public LoginData createFromParcel(Parcel parcel) {
                return new LoginData(null);
            }

            public LoginData[] newArray(int i) {
                return new LoginData[i];
            }
        };
    }

    public LoginData() {
        this.mUsername = null;
        this.mEncryptedPassword = null;
        this.mPassword = null;
        this.mService = null;
        this.mCaptchaToken = null;
        this.mCaptchaData = null;
        this.mCaptchaMimeType = null;
        this.mCaptchaAnswer = null;
        this.mFlags = 0;
        this.mStatus = null;
        this.mJsonString = null;
        this.mSid = null;
        this.mAuthtoken = null;
        this.mOAuthAccessToken = null;
    }

    private LoginData(Parcel parcel) {
        this.mUsername = null;
        this.mEncryptedPassword = null;
        this.mPassword = null;
        this.mService = null;
        this.mCaptchaToken = null;
        this.mCaptchaData = null;
        this.mCaptchaMimeType = null;
        this.mCaptchaAnswer = null;
        this.mFlags = 0;
        this.mStatus = null;
        this.mJsonString = null;
        this.mSid = null;
        this.mAuthtoken = null;
        this.mOAuthAccessToken = null;
        readFromParcel(parcel);
    }

    public int describeContents() {
        return 0;
    }

    public void readFromParcel(Parcel parcel) {
        this.mUsername = parcel.readString();
        this.mEncryptedPassword = parcel.readString();
        this.mPassword = parcel.readString();
        this.mService = parcel.readString();
        this.mCaptchaToken = parcel.readString();
        int readInt = parcel.readInt();
        if (readInt == -1) {
            this.mCaptchaData = null;
        } else {
            this.mCaptchaData = new byte[readInt];
            parcel.readByteArray(this.mCaptchaData);
        }
        this.mCaptchaMimeType = parcel.readString();
        this.mCaptchaAnswer = parcel.readString();
        this.mFlags = parcel.readInt();
        String readString = parcel.readString();
        if (readString == null) {
            this.mStatus = null;
        } else {
            this.mStatus = Status.valueOf(readString);
        }
        this.mJsonString = parcel.readString();
        this.mSid = parcel.readString();
        this.mAuthtoken = parcel.readString();
        this.mOAuthAccessToken = parcel.readString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mUsername);
        parcel.writeString(this.mEncryptedPassword);
        parcel.writeString(this.mPassword);
        parcel.writeString(this.mService);
        parcel.writeString(this.mCaptchaToken);
        if (this.mCaptchaData == null) {
            parcel.writeInt(-1);
        } else {
            parcel.writeInt(this.mCaptchaData.length);
            parcel.writeByteArray(this.mCaptchaData);
        }
        parcel.writeString(this.mCaptchaMimeType);
        parcel.writeString(this.mCaptchaAnswer);
        parcel.writeInt(this.mFlags);
        if (this.mStatus == null) {
            parcel.writeString(null);
        } else {
            parcel.writeString(this.mStatus.name());
        }
        parcel.writeString(this.mJsonString);
        parcel.writeString(this.mSid);
        parcel.writeString(this.mAuthtoken);
        parcel.writeString(this.mOAuthAccessToken);
    }
}
