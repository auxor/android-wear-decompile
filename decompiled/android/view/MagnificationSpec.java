package android.view;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Pools.SynchronizedPool;
import android.view.WindowManager.LayoutParams;

public class MagnificationSpec implements Parcelable {
    public static final Creator<MagnificationSpec> CREATOR;
    private static final int MAX_POOL_SIZE = 20;
    private static final SynchronizedPool<MagnificationSpec> sPool;
    public float offsetX;
    public float offsetY;
    public float scale;

    static {
        sPool = new SynchronizedPool(MAX_POOL_SIZE);
        CREATOR = new Creator<MagnificationSpec>() {
            public MagnificationSpec[] newArray(int size) {
                return new MagnificationSpec[size];
            }

            public MagnificationSpec createFromParcel(Parcel parcel) {
                MagnificationSpec spec = MagnificationSpec.obtain();
                spec.initFromParcel(parcel);
                return spec;
            }
        };
    }

    private MagnificationSpec() {
        this.scale = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
    }

    public void initialize(float scale, float offsetX, float offsetY) {
        if (scale < LayoutParams.BRIGHTNESS_OVERRIDE_FULL) {
            throw new IllegalArgumentException("Scale must be greater than or equal to one!");
        }
        this.scale = scale;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public boolean isNop() {
        return this.scale == LayoutParams.BRIGHTNESS_OVERRIDE_FULL && this.offsetX == 0.0f && this.offsetY == 0.0f;
    }

    public static MagnificationSpec obtain(MagnificationSpec other) {
        MagnificationSpec info = obtain();
        info.scale = other.scale;
        info.offsetX = other.offsetX;
        info.offsetY = other.offsetY;
        return info;
    }

    public static MagnificationSpec obtain() {
        MagnificationSpec spec = (MagnificationSpec) sPool.acquire();
        return spec != null ? spec : new MagnificationSpec();
    }

    public void recycle() {
        clear();
        sPool.release(this);
    }

    public void clear() {
        this.scale = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        this.offsetX = 0.0f;
        this.offsetY = 0.0f;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeFloat(this.scale);
        parcel.writeFloat(this.offsetX);
        parcel.writeFloat(this.offsetY);
        recycle();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<scale:");
        builder.append(this.scale);
        builder.append(",offsetX:");
        builder.append(this.offsetX);
        builder.append(",offsetY:");
        builder.append(this.offsetY);
        builder.append(">");
        return builder.toString();
    }

    private void initFromParcel(Parcel parcel) {
        this.scale = parcel.readFloat();
        this.offsetX = parcel.readFloat();
        this.offsetY = parcel.readFloat();
    }
}
