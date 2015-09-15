package android.util;

@Deprecated
public class FloatMath {
    public static native float ceil(float f);

    public static native float cos(float f);

    public static native float exp(float f);

    public static native float floor(float f);

    public static native float hypot(float f, float f2);

    public static native float pow(float f, float f2);

    public static native float sin(float f);

    public static native float sqrt(float f);

    private FloatMath() {
    }
}
