package android.graphics;

public class PaintFlagsDrawFilter extends DrawFilter {
    public final int clearBits;
    public final int setBits;

    private static native long nativeConstructor(int i, int i2);

    public PaintFlagsDrawFilter(int clearBits, int setBits) {
        this.clearBits = clearBits;
        this.setBits = setBits;
        this.mNativeInt = nativeConstructor(clearBits, setBits);
    }
}
