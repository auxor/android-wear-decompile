package android.media;

import android.graphics.Bitmap;
import android.os.Bundle;

public final class MediaMetadata$Builder {
    private final Bundle mBundle;

    public MediaMetadata$Builder() {
        this.mBundle = new Bundle();
    }

    public MediaMetadata$Builder(MediaMetadata source) {
        this.mBundle = new Bundle(MediaMetadata.access$100(source));
    }

    public MediaMetadata$Builder(MediaMetadata source, int maxBitmapSize) {
        this(source);
        for (String key : this.mBundle.keySet()) {
            Bitmap value = this.mBundle.get(key);
            if (value != null && (value instanceof Bitmap)) {
                Bitmap bmp = value;
                if (bmp.getHeight() > maxBitmapSize || bmp.getWidth() > maxBitmapSize) {
                    putBitmap(key, scaleBitmap(bmp, maxBitmapSize));
                }
            }
        }
    }

    public MediaMetadata$Builder putText(String key, CharSequence value) {
        if (!MediaMetadata.access$200().containsKey(key) || ((Integer) MediaMetadata.access$200().get(key)).intValue() == 1) {
            this.mBundle.putCharSequence(key, value);
            return this;
        }
        throw new IllegalArgumentException("The " + key + " key cannot be used to put a CharSequence");
    }

    public MediaMetadata$Builder putString(String key, String value) {
        if (!MediaMetadata.access$200().containsKey(key) || ((Integer) MediaMetadata.access$200().get(key)).intValue() == 1) {
            this.mBundle.putCharSequence(key, value);
            return this;
        }
        throw new IllegalArgumentException("The " + key + " key cannot be used to put a String");
    }

    public MediaMetadata$Builder putLong(String key, long value) {
        if (!MediaMetadata.access$200().containsKey(key) || ((Integer) MediaMetadata.access$200().get(key)).intValue() == 0) {
            this.mBundle.putLong(key, value);
            return this;
        }
        throw new IllegalArgumentException("The " + key + " key cannot be used to put a long");
    }

    public MediaMetadata$Builder putRating(String key, Rating value) {
        if (!MediaMetadata.access$200().containsKey(key) || ((Integer) MediaMetadata.access$200().get(key)).intValue() == 3) {
            this.mBundle.putParcelable(key, value);
            return this;
        }
        throw new IllegalArgumentException("The " + key + " key cannot be used to put a Rating");
    }

    public MediaMetadata$Builder putBitmap(String key, Bitmap value) {
        if (!MediaMetadata.access$200().containsKey(key) || ((Integer) MediaMetadata.access$200().get(key)).intValue() == 2) {
            this.mBundle.putParcelable(key, value);
            return this;
        }
        throw new IllegalArgumentException("The " + key + " key cannot be used to put a Bitmap");
    }

    public MediaMetadata build() {
        return new MediaMetadata(this.mBundle, null);
    }

    private Bitmap scaleBitmap(Bitmap bmp, int maxSize) {
        float maxSizeF = (float) maxSize;
        float scale = Math.min(maxSizeF / ((float) bmp.getWidth()), maxSizeF / ((float) bmp.getHeight()));
        return Bitmap.createScaledBitmap(bmp, (int) (((float) bmp.getWidth()) * scale), (int) (((float) bmp.getHeight()) * scale), true);
    }
}
