package com.google.android.gtalkservice;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;

public final class Presence implements Parcelable {
    public static final Creator<Presence> CREATOR;
    public static final Presence OFFLINE;
    private boolean mAllowInvisibility;
    private boolean mAvailable;
    private int mCapabilities;
    private List<String> mDefaultStatusList;
    private List<String> mDndStatusList;
    private boolean mInvisible;
    private Show mShow;
    private String mStatus;
    private int mStatusListContentsMax;
    private int mStatusListMax;
    private int mStatusMax;

    public enum Show {
        NONE,
        AWAY,
        EXTENDED_AWAY,
        DND,
        AVAILABLE
    }

    static {
        OFFLINE = new Presence();
        CREATOR = new Creator<Presence>() {
            public Presence createFromParcel(Parcel parcel) {
                return new Presence(parcel);
            }

            public Presence[] newArray(int i) {
                return new Presence[i];
            }
        };
    }

    public Presence() {
        this(false, Show.NONE, null, 8);
    }

    public Presence(Parcel parcel) {
        throw new VerifyError("bad dex opcode");
    }

    public Presence(boolean z, Show show, String str, int i) {
        this.mAvailable = z;
        this.mShow = show;
        this.mStatus = str;
        this.mInvisible = false;
        this.mDefaultStatusList = new ArrayList();
        this.mDndStatusList = new ArrayList();
        this.mCapabilities = i;
    }

    public boolean allowInvisibility() {
        return this.mAllowInvisibility;
    }

    public int describeContents() {
        return 0;
    }

    public int getCapabilities() {
        return this.mCapabilities;
    }

    public int getStatusListContentsMax() {
        return this.mStatusListContentsMax;
    }

    public int getStatusListMax() {
        return this.mStatusListMax;
    }

    public int getStatusMax() {
        return this.mStatusMax;
    }

    public boolean isAvailable() {
        return this.mAvailable;
    }

    public boolean isInvisible() {
        return this.mInvisible;
    }

    public void setAllowInvisibility(boolean z) {
        this.mAllowInvisibility = z;
    }

    public void setAvailable(boolean z) {
        this.mAvailable = z;
    }

    public void setCapabilities(int i) {
        this.mCapabilities = i;
    }

    public boolean setInvisible(boolean z) {
        this.mInvisible = z;
        return !z || allowInvisibility();
    }

    public void setShow(Show show) {
        this.mShow = show;
    }

    public void setStatusListContentsMax(int i) {
        this.mStatusListContentsMax = i;
    }

    public void setStatusListMax(int i) {
        this.mStatusListMax = i;
    }

    public void setStatusMax(int i) {
        this.mStatusMax = i;
    }

    public String toString() {
        if (!isAvailable()) {
            return "UNAVAILABLE";
        }
        if (isInvisible()) {
            return "INVISIBLE";
        }
        StringBuilder stringBuilder = new StringBuilder(40);
        if (this.mShow == Show.NONE) {
            stringBuilder.append("AVAILABLE(x)");
        } else {
            stringBuilder.append(this.mShow.toString());
        }
        if ((this.mCapabilities & 8) != 0) {
            stringBuilder.append(" pmuc-v1");
        }
        if ((this.mCapabilities & 1) != 0) {
            stringBuilder.append(" voice-v1");
        }
        if ((this.mCapabilities & 2) != 0) {
            stringBuilder.append(" video-v1");
        }
        if ((this.mCapabilities & 4) != 0) {
            stringBuilder.append(" camera-v1");
        }
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        int i2 = 1;
        parcel.writeInt(getStatusMax());
        parcel.writeInt(getStatusListMax());
        parcel.writeInt(getStatusListContentsMax());
        parcel.writeInt(allowInvisibility() ? 1 : 0);
        parcel.writeInt(this.mAvailable ? 1 : 0);
        parcel.writeString(this.mShow.toString());
        parcel.writeString(this.mStatus);
        if (!this.mInvisible) {
            i2 = 0;
        }
        parcel.writeInt(i2);
        parcel.writeStringList(this.mDefaultStatusList);
        parcel.writeStringList(this.mDndStatusList);
        parcel.writeInt(getCapabilities());
    }
}
