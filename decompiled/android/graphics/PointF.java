package android.graphics;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.FloatMath;

public class PointF implements Parcelable {
    public static final Creator<PointF> CREATOR;
    public float x;
    public float y;

    public PointF(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public PointF(Point p) {
        this.x = (float) p.x;
        this.y = (float) p.y;
    }

    public final void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public final void set(PointF p) {
        this.x = p.x;
        this.y = p.y;
    }

    public final void negate() {
        this.x = -this.x;
        this.y = -this.y;
    }

    public final void offset(float dx, float dy) {
        this.x += dx;
        this.y += dy;
    }

    public final boolean equals(float x, float y) {
        return this.x == x && this.y == y;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PointF pointF = (PointF) o;
        if (Float.compare(pointF.x, this.x) != 0) {
            return false;
        }
        if (Float.compare(pointF.y, this.y) != 0) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result;
        int i = 0;
        if (this.x != 0.0f) {
            result = Float.floatToIntBits(this.x);
        } else {
            result = 0;
        }
        int i2 = result * 31;
        if (this.y != 0.0f) {
            i = Float.floatToIntBits(this.y);
        }
        return i2 + i;
    }

    public String toString() {
        return "PointF(" + this.x + ", " + this.y + ")";
    }

    public final float length() {
        return length(this.x, this.y);
    }

    public static float length(float x, float y) {
        return FloatMath.sqrt((x * x) + (y * y));
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeFloat(this.x);
        out.writeFloat(this.y);
    }

    static {
        CREATOR = new Creator<PointF>() {
            public PointF createFromParcel(Parcel in) {
                PointF r = new PointF();
                r.readFromParcel(in);
                return r;
            }

            public PointF[] newArray(int size) {
                return new PointF[size];
            }
        };
    }

    public void readFromParcel(Parcel in) {
        this.x = in.readFloat();
        this.y = in.readFloat();
    }
}
