package android.media;

import android.os.Parcel;
import android.os.Parcelable.Creator;

class MediaRouterClientState$1 implements Creator<MediaRouterClientState> {
    MediaRouterClientState$1() {
    }

    public MediaRouterClientState createFromParcel(Parcel in) {
        return new MediaRouterClientState(in);
    }

    public MediaRouterClientState[] newArray(int size) {
        return new MediaRouterClientState[size];
    }
}
