package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public class PackageCleanItem {
    public static final Creator<PackageCleanItem> CREATOR;
    public final boolean andCode;
    public final String packageName;
    public final int userId;

    public PackageCleanItem(int userId, String packageName, boolean andCode) {
        this.userId = userId;
        this.packageName = packageName;
        this.andCode = andCode;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            try {
                PackageCleanItem other = (PackageCleanItem) obj;
                if (this.userId == other.userId && this.packageName.equals(other.packageName) && this.andCode == other.andCode) {
                    return true;
                }
                return false;
            } catch (ClassCastException e) {
            }
        }
        return false;
    }

    public int hashCode() {
        return ((((this.userId + 527) * 31) + this.packageName.hashCode()) * 31) + (this.andCode ? 1 : 0);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int parcelableFlags) {
        dest.writeInt(this.userId);
        dest.writeString(this.packageName);
        dest.writeInt(this.andCode ? 1 : 0);
    }

    static {
        CREATOR = new Creator<PackageCleanItem>() {
            public PackageCleanItem createFromParcel(Parcel source) {
                return new PackageCleanItem(null);
            }

            public PackageCleanItem[] newArray(int size) {
                return new PackageCleanItem[size];
            }
        };
    }

    private PackageCleanItem(Parcel source) {
        this.userId = source.readInt();
        this.packageName = source.readString();
        this.andCode = source.readInt() != 0;
    }
}
