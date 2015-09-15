package android.media;

import android.os.Parcel;
import android.os.Parcelable.Creator;

class Rating$1 implements Creator<Rating> {
    Rating$1() {
    }

    public Rating createFromParcel(Parcel p) {
        return new Rating(p.readInt(), p.readFloat(), null);
    }

    public Rating[] newArray(int size) {
        return new Rating[size];
    }
}
