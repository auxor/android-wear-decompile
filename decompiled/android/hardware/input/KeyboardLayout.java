package android.hardware.input;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class KeyboardLayout implements Parcelable, Comparable<KeyboardLayout> {
    public static final Creator<KeyboardLayout> CREATOR;
    private final String mCollection;
    private final String mDescriptor;
    private final String mLabel;
    private final int mPriority;

    static {
        CREATOR = new Creator<KeyboardLayout>() {
            public KeyboardLayout createFromParcel(Parcel source) {
                return new KeyboardLayout(null);
            }

            public KeyboardLayout[] newArray(int size) {
                return new KeyboardLayout[size];
            }
        };
    }

    public KeyboardLayout(String descriptor, String label, String collection, int priority) {
        this.mDescriptor = descriptor;
        this.mLabel = label;
        this.mCollection = collection;
        this.mPriority = priority;
    }

    private KeyboardLayout(Parcel source) {
        this.mDescriptor = source.readString();
        this.mLabel = source.readString();
        this.mCollection = source.readString();
        this.mPriority = source.readInt();
    }

    public String getDescriptor() {
        return this.mDescriptor;
    }

    public String getLabel() {
        return this.mLabel;
    }

    public String getCollection() {
        return this.mCollection;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mDescriptor);
        dest.writeString(this.mLabel);
        dest.writeString(this.mCollection);
        dest.writeInt(this.mPriority);
    }

    public int compareTo(KeyboardLayout another) {
        int result = Integer.compare(another.mPriority, this.mPriority);
        if (result == 0) {
            result = this.mLabel.compareToIgnoreCase(another.mLabel);
        }
        if (result == 0) {
            return this.mCollection.compareToIgnoreCase(another.mCollection);
        }
        return result;
    }

    public String toString() {
        if (this.mCollection.isEmpty()) {
            return this.mLabel;
        }
        return this.mLabel + " - " + this.mCollection;
    }
}
