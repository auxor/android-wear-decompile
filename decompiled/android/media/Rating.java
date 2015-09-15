package android.media;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;

public final class Rating implements Parcelable {
    public static final Creator<Rating> CREATOR;
    public static final int RATING_3_STARS = 3;
    public static final int RATING_4_STARS = 4;
    public static final int RATING_5_STARS = 5;
    public static final int RATING_HEART = 1;
    public static final int RATING_NONE = 0;
    private static final float RATING_NOT_RATED = -1.0f;
    public static final int RATING_PERCENTAGE = 6;
    public static final int RATING_THUMB_UP_DOWN = 2;
    private static final String TAG = "Rating";
    private final int mRatingStyle;
    private final float mRatingValue;

    private Rating(int ratingStyle, float rating) {
        this.mRatingStyle = ratingStyle;
        this.mRatingValue = rating;
    }

    public String toString() {
        return "Rating:style=" + this.mRatingStyle + " rating=" + (this.mRatingValue < 0.0f ? "unrated" : String.valueOf(this.mRatingValue));
    }

    public int describeContents() {
        return this.mRatingStyle;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mRatingStyle);
        dest.writeFloat(this.mRatingValue);
    }

    static {
        CREATOR = new 1();
    }

    public static Rating newUnratedRating(int ratingStyle) {
        switch (ratingStyle) {
            case RATING_HEART /*1*/:
            case RATING_THUMB_UP_DOWN /*2*/:
            case RATING_3_STARS /*3*/:
            case RATING_4_STARS /*4*/:
            case RATING_5_STARS /*5*/:
            case RATING_PERCENTAGE /*6*/:
                return new Rating(ratingStyle, RATING_NOT_RATED);
            default:
                return null;
        }
    }

    public static Rating newHeartRating(boolean hasHeart) {
        return new Rating(RATING_HEART, hasHeart ? RemoteControlClient.PLAYBACK_SPEED_1X : 0.0f);
    }

    public static Rating newThumbRating(boolean thumbIsUp) {
        return new Rating(RATING_THUMB_UP_DOWN, thumbIsUp ? RemoteControlClient.PLAYBACK_SPEED_1X : 0.0f);
    }

    public static Rating newStarRating(int starRatingStyle, float starRating) {
        float maxRating;
        switch (starRatingStyle) {
            case RATING_3_STARS /*3*/:
                maxRating = 3.0f;
                break;
            case RATING_4_STARS /*4*/:
                maxRating = 4.0f;
                break;
            case RATING_5_STARS /*5*/:
                maxRating = 5.0f;
                break;
            default:
                Log.e(TAG, "Invalid rating style (" + starRatingStyle + ") for a star rating");
                return null;
        }
        if (starRating >= 0.0f && starRating <= maxRating) {
            return new Rating(starRatingStyle, starRating);
        }
        Log.e(TAG, "Trying to set out of range star-based rating");
        return null;
    }

    public static Rating newPercentageRating(float percent) {
        if (percent >= 0.0f && percent <= 100.0f) {
            return new Rating(RATING_PERCENTAGE, percent);
        }
        Log.e(TAG, "Invalid percentage-based rating value");
        return null;
    }

    public boolean isRated() {
        return this.mRatingValue >= 0.0f;
    }

    public int getRatingStyle() {
        return this.mRatingStyle;
    }

    public boolean hasHeart() {
        boolean z = true;
        if (this.mRatingStyle != RATING_HEART) {
            return false;
        }
        if (this.mRatingValue != RemoteControlClient.PLAYBACK_SPEED_1X) {
            z = false;
        }
        return z;
    }

    public boolean isThumbUp() {
        if (this.mRatingStyle == RATING_THUMB_UP_DOWN && this.mRatingValue == RemoteControlClient.PLAYBACK_SPEED_1X) {
            return true;
        }
        return false;
    }

    public float getStarRating() {
        switch (this.mRatingStyle) {
            case RATING_3_STARS /*3*/:
            case RATING_4_STARS /*4*/:
            case RATING_5_STARS /*5*/:
                if (isRated()) {
                    return this.mRatingValue;
                }
                break;
        }
        return RATING_NOT_RATED;
    }

    public float getPercentRating() {
        if (this.mRatingStyle == RATING_PERCENTAGE && isRated()) {
            return this.mRatingValue;
        }
        return RATING_NOT_RATED;
    }
}
