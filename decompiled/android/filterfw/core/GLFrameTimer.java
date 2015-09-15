package android.filterfw.core;

/* compiled from: GLFrame */
class GLFrameTimer {
    private static StopWatchMap mTimer;

    GLFrameTimer() {
    }

    static {
        mTimer = null;
    }

    public static StopWatchMap get() {
        if (mTimer == null) {
            mTimer = new StopWatchMap();
        }
        return mTimer;
    }
}
