package com.google.android.gtalkservice;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class GroupChatInvitation implements Parcelable {
    public static final Creator<GroupChatInvitation> CREATOR;
    private long mGroupContactId;
    private String mInviter;
    private String mPassword;
    private String mReason;
    private String mRoomAddress;

    static {
        CREATOR = new Creator<GroupChatInvitation>() {
            public GroupChatInvitation createFromParcel(Parcel parcel) {
                return new GroupChatInvitation(parcel);
            }

            public GroupChatInvitation[] newArray(int i) {
                return new GroupChatInvitation[i];
            }
        };
    }

    public GroupChatInvitation(Parcel parcel) {
        this.mRoomAddress = parcel.readString();
        this.mInviter = parcel.readString();
        this.mReason = parcel.readString();
        this.mPassword = parcel.readString();
        this.mGroupContactId = parcel.readLong();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mRoomAddress);
        parcel.writeString(this.mInviter);
        parcel.writeString(this.mReason);
        parcel.writeString(this.mPassword);
        parcel.writeLong(this.mGroupContactId);
    }
}
