package com.android.internal.statusbar;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.UserHandle;

public class StatusBarIcon implements Parcelable {
    public static final Creator<StatusBarIcon> CREATOR;
    public CharSequence contentDescription;
    public int iconId;
    public int iconLevel;
    public String iconPackage;
    public int number;
    public UserHandle user;
    public boolean visible;

    public StatusBarIcon(String iconPackage, UserHandle user, int iconId, int iconLevel, int number, CharSequence contentDescription) {
        this.visible = true;
        this.iconPackage = iconPackage;
        this.user = user;
        this.iconId = iconId;
        this.iconLevel = iconLevel;
        this.number = number;
        this.contentDescription = contentDescription;
    }

    public String toString() {
        return "StatusBarIcon(pkg=" + this.iconPackage + "user=" + this.user.getIdentifier() + " id=0x" + Integer.toHexString(this.iconId) + " level=" + this.iconLevel + " visible=" + this.visible + " num=" + this.number + " )";
    }

    public StatusBarIcon clone() {
        StatusBarIcon that = new StatusBarIcon(this.iconPackage, this.user, this.iconId, this.iconLevel, this.number, this.contentDescription);
        that.visible = this.visible;
        return that;
    }

    public StatusBarIcon(Parcel in) {
        this.visible = true;
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.iconPackage = in.readString();
        this.user = (UserHandle) in.readParcelable(null);
        this.iconId = in.readInt();
        this.iconLevel = in.readInt();
        this.visible = in.readInt() != 0;
        this.number = in.readInt();
        this.contentDescription = in.readCharSequence();
    }

    public void writeToParcel(Parcel out, int flags) {
        int i = 0;
        out.writeString(this.iconPackage);
        out.writeParcelable(this.user, 0);
        out.writeInt(this.iconId);
        out.writeInt(this.iconLevel);
        if (this.visible) {
            i = 1;
        }
        out.writeInt(i);
        out.writeInt(this.number);
        out.writeCharSequence(this.contentDescription);
    }

    public int describeContents() {
        return 0;
    }

    static {
        CREATOR = new Creator<StatusBarIcon>() {
            public StatusBarIcon createFromParcel(Parcel parcel) {
                return new StatusBarIcon(parcel);
            }

            public StatusBarIcon[] newArray(int size) {
                return new StatusBarIcon[size];
            }
        };
    }
}
