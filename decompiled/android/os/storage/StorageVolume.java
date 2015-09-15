package android.os.storage;

import android.content.Context;
import android.net.ProxyInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.UserHandle;
import com.android.internal.util.IndentingPrintWriter;
import java.io.CharArrayWriter;
import java.io.File;

public class StorageVolume implements Parcelable {
    public static final Creator<StorageVolume> CREATOR;
    public static final String EXTRA_STORAGE_VOLUME = "storage_volume";
    private final boolean mAllowMassStorage;
    private final int mDescriptionId;
    private final boolean mEmulated;
    private final long mMaxFileSize;
    private final int mMtpReserveSpace;
    private final UserHandle mOwner;
    private final File mPath;
    private final boolean mPrimary;
    private final boolean mRemovable;
    private String mState;
    private int mStorageId;
    private String mUserLabel;
    private String mUuid;

    public StorageVolume(File path, int descriptionId, boolean primary, boolean removable, boolean emulated, int mtpReserveSpace, boolean allowMassStorage, long maxFileSize, UserHandle owner) {
        this.mPath = path;
        this.mDescriptionId = descriptionId;
        this.mPrimary = primary;
        this.mRemovable = removable;
        this.mEmulated = emulated;
        this.mMtpReserveSpace = mtpReserveSpace;
        this.mAllowMassStorage = allowMassStorage;
        this.mMaxFileSize = maxFileSize;
        this.mOwner = owner;
    }

    private StorageVolume(Parcel in) {
        boolean z;
        boolean z2 = true;
        this.mStorageId = in.readInt();
        this.mPath = new File(in.readString());
        this.mDescriptionId = in.readInt();
        this.mPrimary = in.readInt() != 0;
        if (in.readInt() != 0) {
            z = true;
        } else {
            z = false;
        }
        this.mRemovable = z;
        if (in.readInt() != 0) {
            z = true;
        } else {
            z = false;
        }
        this.mEmulated = z;
        this.mMtpReserveSpace = in.readInt();
        if (in.readInt() == 0) {
            z2 = false;
        }
        this.mAllowMassStorage = z2;
        this.mMaxFileSize = in.readLong();
        this.mOwner = (UserHandle) in.readParcelable(null);
        this.mUuid = in.readString();
        this.mUserLabel = in.readString();
        this.mState = in.readString();
    }

    public static StorageVolume fromTemplate(StorageVolume template, File path, UserHandle owner) {
        return new StorageVolume(path, template.mDescriptionId, template.mPrimary, template.mRemovable, template.mEmulated, template.mMtpReserveSpace, template.mAllowMassStorage, template.mMaxFileSize, owner);
    }

    public String getPath() {
        return this.mPath.toString();
    }

    public File getPathFile() {
        return this.mPath;
    }

    public String getDescription(Context context) {
        return context.getResources().getString(this.mDescriptionId);
    }

    public int getDescriptionId() {
        return this.mDescriptionId;
    }

    public boolean isPrimary() {
        return this.mPrimary;
    }

    public boolean isRemovable() {
        return this.mRemovable;
    }

    public boolean isEmulated() {
        return this.mEmulated;
    }

    public int getStorageId() {
        return this.mStorageId;
    }

    public void setStorageId(int index) {
        this.mStorageId = ((index + 1) << 16) + 1;
    }

    public int getMtpReserveSpace() {
        return this.mMtpReserveSpace;
    }

    public boolean allowMassStorage() {
        return this.mAllowMassStorage;
    }

    public long getMaxFileSize() {
        return this.mMaxFileSize;
    }

    public UserHandle getOwner() {
        return this.mOwner;
    }

    public void setUuid(String uuid) {
        this.mUuid = uuid;
    }

    public String getUuid() {
        return this.mUuid;
    }

    public int getFatVolumeId() {
        if (this.mUuid == null || this.mUuid.length() != 9) {
            return -1;
        }
        try {
            return (int) Long.parseLong(this.mUuid.replace("-", ProxyInfo.LOCAL_EXCL_LIST), 16);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void setUserLabel(String userLabel) {
        this.mUserLabel = userLabel;
    }

    public String getUserLabel() {
        return this.mUserLabel;
    }

    public void setState(String state) {
        this.mState = state;
    }

    public String getState() {
        return this.mState;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof StorageVolume) || this.mPath == null) {
            return false;
        }
        return this.mPath.equals(((StorageVolume) obj).mPath);
    }

    public int hashCode() {
        return this.mPath.hashCode();
    }

    public String toString() {
        CharArrayWriter writer = new CharArrayWriter();
        dump(new IndentingPrintWriter(writer, "    ", 80));
        return writer.toString();
    }

    public void dump(IndentingPrintWriter pw) {
        pw.println("StorageVolume:");
        pw.increaseIndent();
        pw.printPair("mStorageId", Integer.valueOf(this.mStorageId));
        pw.printPair("mPath", this.mPath);
        pw.printPair("mDescriptionId", Integer.valueOf(this.mDescriptionId));
        pw.printPair("mPrimary", Boolean.valueOf(this.mPrimary));
        pw.printPair("mRemovable", Boolean.valueOf(this.mRemovable));
        pw.printPair("mEmulated", Boolean.valueOf(this.mEmulated));
        pw.printPair("mMtpReserveSpace", Integer.valueOf(this.mMtpReserveSpace));
        pw.printPair("mAllowMassStorage", Boolean.valueOf(this.mAllowMassStorage));
        pw.printPair("mMaxFileSize", Long.valueOf(this.mMaxFileSize));
        pw.printPair("mOwner", this.mOwner);
        pw.printPair("mUuid", this.mUuid);
        pw.printPair("mUserLabel", this.mUserLabel);
        pw.printPair("mState", this.mState);
        pw.decreaseIndent();
    }

    static {
        CREATOR = new Creator<StorageVolume>() {
            public StorageVolume createFromParcel(Parcel in) {
                return new StorageVolume(null);
            }

            public StorageVolume[] newArray(int size) {
                return new StorageVolume[size];
            }
        };
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        int i;
        int i2 = 1;
        parcel.writeInt(this.mStorageId);
        parcel.writeString(this.mPath.toString());
        parcel.writeInt(this.mDescriptionId);
        parcel.writeInt(this.mPrimary ? 1 : 0);
        if (this.mRemovable) {
            i = 1;
        } else {
            i = 0;
        }
        parcel.writeInt(i);
        if (this.mEmulated) {
            i = 1;
        } else {
            i = 0;
        }
        parcel.writeInt(i);
        parcel.writeInt(this.mMtpReserveSpace);
        if (!this.mAllowMassStorage) {
            i2 = 0;
        }
        parcel.writeInt(i2);
        parcel.writeLong(this.mMaxFileSize);
        parcel.writeParcelable(this.mOwner, flags);
        parcel.writeString(this.mUuid);
        parcel.writeString(this.mUserLabel);
        parcel.writeString(this.mState);
    }
}
