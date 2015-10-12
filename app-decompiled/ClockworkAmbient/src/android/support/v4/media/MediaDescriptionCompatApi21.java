package android.support.v4.media;

import android.graphics.Bitmap;
import android.media.MediaDescription;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;

public class MediaDescriptionCompatApi21 {

    public static class Builder {
        public static Object build(Object obj) {
            android.media.MediaDescription.Builder builder = (android.media.MediaDescription.Builder) obj;
            throw new VerifyError("bad dex opcode");
        }

        public static Object newInstance() {
            return new android.media.MediaDescription.Builder();
        }

        public static void setDescription(Object obj, CharSequence charSequence) {
            android.media.MediaDescription.Builder builder = (android.media.MediaDescription.Builder) obj;
            throw new VerifyError("bad dex opcode");
        }

        public static void setExtras(Object obj, Bundle bundle) {
            android.media.MediaDescription.Builder builder = (android.media.MediaDescription.Builder) obj;
            throw new VerifyError("bad dex opcode");
        }

        public static void setIconBitmap(Object obj, Bitmap bitmap) {
            android.media.MediaDescription.Builder builder = (android.media.MediaDescription.Builder) obj;
            throw new VerifyError("bad dex opcode");
        }

        public static void setIconUri(Object obj, Uri uri) {
            android.media.MediaDescription.Builder builder = (android.media.MediaDescription.Builder) obj;
            throw new VerifyError("bad dex opcode");
        }

        public static void setMediaId(Object obj, String str) {
            android.media.MediaDescription.Builder builder = (android.media.MediaDescription.Builder) obj;
            throw new VerifyError("bad dex opcode");
        }

        public static void setSubtitle(Object obj, CharSequence charSequence) {
            android.media.MediaDescription.Builder builder = (android.media.MediaDescription.Builder) obj;
            throw new VerifyError("bad dex opcode");
        }

        public static void setTitle(Object obj, CharSequence charSequence) {
            android.media.MediaDescription.Builder builder = (android.media.MediaDescription.Builder) obj;
            throw new VerifyError("bad dex opcode");
        }
    }

    public static Object fromParcel(Parcel parcel) {
        return MediaDescription.CREATOR.createFromParcel(parcel);
    }

    public static CharSequence getDescription(Object obj) {
        MediaDescription mediaDescription = (MediaDescription) obj;
        throw new VerifyError("bad dex opcode");
    }

    public static Bundle getExtras(Object obj) {
        MediaDescription mediaDescription = (MediaDescription) obj;
        throw new VerifyError("bad dex opcode");
    }

    public static Bitmap getIconBitmap(Object obj) {
        MediaDescription mediaDescription = (MediaDescription) obj;
        throw new VerifyError("bad dex opcode");
    }

    public static Uri getIconUri(Object obj) {
        MediaDescription mediaDescription = (MediaDescription) obj;
        throw new VerifyError("bad dex opcode");
    }

    public static String getMediaId(Object obj) {
        MediaDescription mediaDescription = (MediaDescription) obj;
        throw new VerifyError("bad dex opcode");
    }

    public static CharSequence getSubtitle(Object obj) {
        MediaDescription mediaDescription = (MediaDescription) obj;
        throw new VerifyError("bad dex opcode");
    }

    public static CharSequence getTitle(Object obj) {
        MediaDescription mediaDescription = (MediaDescription) obj;
        throw new VerifyError("bad dex opcode");
    }

    public static void writeToParcel(Object obj, Parcel parcel, int i) {
        MediaDescription mediaDescription = (MediaDescription) obj;
        throw new VerifyError("bad dex opcode");
    }
}
