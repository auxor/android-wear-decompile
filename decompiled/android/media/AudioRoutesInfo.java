package android.media;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;

public class AudioRoutesInfo implements Parcelable {
    public static final Creator<AudioRoutesInfo> CREATOR;
    static final int MAIN_DOCK_SPEAKERS = 4;
    static final int MAIN_HDMI = 8;
    static final int MAIN_HEADPHONES = 2;
    static final int MAIN_HEADSET = 1;
    static final int MAIN_SPEAKER = 0;
    CharSequence mBluetoothName;
    int mMainType;

    public AudioRoutesInfo() {
        this.mMainType = 0;
    }

    public AudioRoutesInfo(AudioRoutesInfo o) {
        this.mMainType = 0;
        this.mBluetoothName = o.mBluetoothName;
        this.mMainType = o.mMainType;
    }

    AudioRoutesInfo(Parcel src) {
        this.mMainType = 0;
        this.mBluetoothName = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(src);
        this.mMainType = src.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        TextUtils.writeToParcel(this.mBluetoothName, dest, flags);
        dest.writeInt(this.mMainType);
    }

    static {
        CREATOR = new Creator<AudioRoutesInfo>() {
            public AudioRoutesInfo createFromParcel(Parcel in) {
                return new AudioRoutesInfo(in);
            }

            public AudioRoutesInfo[] newArray(int size) {
                return new AudioRoutesInfo[size];
            }
        };
    }
}
