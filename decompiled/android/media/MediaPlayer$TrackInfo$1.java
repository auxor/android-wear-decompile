package android.media;

import android.media.MediaPlayer.TrackInfo;
import android.os.Parcel;
import android.os.Parcelable.Creator;

class MediaPlayer$TrackInfo$1 implements Creator<TrackInfo> {
    MediaPlayer$TrackInfo$1() {
    }

    public TrackInfo createFromParcel(Parcel in) {
        return new TrackInfo(in);
    }

    public TrackInfo[] newArray(int size) {
        return new TrackInfo[size];
    }
}
