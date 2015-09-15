package android.graphics;

import android.content.res.AssetManager.AssetInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Movie {
    private final long mNativeMovie;

    public static native Movie decodeByteArray(byte[] bArr, int i, int i2);

    private static native Movie nativeDecodeAsset(long j);

    private static native Movie nativeDecodeStream(InputStream inputStream);

    private static native void nativeDestructor(long j);

    public native void draw(Canvas canvas, float f, float f2, Paint paint);

    public native int duration();

    public native int height();

    public native boolean isOpaque();

    public native boolean setTime(int i);

    public native int width();

    private Movie(long nativeMovie) {
        if (nativeMovie == 0) {
            throw new RuntimeException("native movie creation failed");
        }
        this.mNativeMovie = nativeMovie;
    }

    public void draw(Canvas canvas, float x, float y) {
        draw(canvas, x, y, null);
    }

    public static Movie decodeStream(InputStream is) {
        if (is == null) {
            return null;
        }
        if (is instanceof AssetInputStream) {
            return nativeDecodeAsset(((AssetInputStream) is).getNativeAsset());
        }
        return nativeDecodeStream(is);
    }

    public static Movie decodeFile(String pathName) {
        try {
            return decodeTempStream(new FileInputStream(pathName));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    protected void finalize() throws Throwable {
        try {
            nativeDestructor(this.mNativeMovie);
        } finally {
            super.finalize();
        }
    }

    private static Movie decodeTempStream(InputStream is) {
        Movie moov = null;
        try {
            moov = decodeStream(is);
            is.close();
            return moov;
        } catch (IOException e) {
            return moov;
        }
    }
}
