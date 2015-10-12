package android.support.v4.media;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class RatingCompat implements Parcelable {
    public static final Creator<RatingCompat> CREATOR;
    private final int mRatingStyle;
    private final float mRatingValue;

    static {
        CREATOR = new Creator<RatingCompat>() {
            public RatingCompat createFromParcel(Parcel parcel) {
                return new RatingCompat(parcel.readFloat(), null);
            }

            public RatingCompat[] newArray(int i) {
                return new RatingCompat[i];
            }
        };
    }

    private RatingCompat(int i, float f) {
        this.mRatingStyle = i;
        this.mRatingValue = f;
        throw new VerifyError("bad dex opcode");
    }

    public int describeContents() {
        return this.mRatingStyle;
    }

    public String toString() {
        return "Rating:style=" + this.mRatingStyle + " rating=" + (this.mRatingValue < 0.0f ? "unrated" : String.valueOf(this.mRatingValue));
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mRatingStyle);
        parcel.writeFloat(this.mRatingValue);
    }
}
