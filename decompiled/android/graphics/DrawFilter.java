package android.graphics;

public class DrawFilter {
    long mNativeInt;

    private static native void nativeDestructor(long j);

    protected void finalize() throws Throwable {
        try {
            nativeDestructor(this.mNativeInt);
        } finally {
            super.finalize();
        }
    }
}
