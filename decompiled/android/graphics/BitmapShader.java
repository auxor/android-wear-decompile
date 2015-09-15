package android.graphics;

import android.graphics.Shader.TileMode;

public class BitmapShader extends Shader {
    public final Bitmap mBitmap;
    private TileMode mTileX;
    private TileMode mTileY;

    private static native long nativeCreate(long j, int i, int i2);

    public BitmapShader(Bitmap bitmap, TileMode tileX, TileMode tileY) {
        this.mBitmap = bitmap;
        this.mTileX = tileX;
        this.mTileY = tileY;
        init(nativeCreate(bitmap.ni(), tileX.nativeInt, tileY.nativeInt));
    }

    protected Shader copy() {
        BitmapShader copy = new BitmapShader(this.mBitmap, this.mTileX, this.mTileY);
        copyLocalMatrix(copy);
        return copy;
    }
}
