package android.media;

import android.os.Parcel;
import android.os.Parcelable.Creator;

class AudioAttributes$1 implements Creator<AudioAttributes> {
    AudioAttributes$1() {
    }

    public AudioAttributes createFromParcel(Parcel p) {
        return new AudioAttributes(p, null);
    }

    public AudioAttributes[] newArray(int size) {
        return new AudioAttributes[size];
    }
}
