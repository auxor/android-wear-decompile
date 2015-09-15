package android.view;

public class SoundEffectConstants {
    public static final int CLICK = 0;
    public static final int NAVIGATION_DOWN = 4;
    public static final int NAVIGATION_LEFT = 1;
    public static final int NAVIGATION_RIGHT = 3;
    public static final int NAVIGATION_UP = 2;

    private SoundEffectConstants() {
    }

    public static int getContantForFocusDirection(int direction) {
        switch (direction) {
            case NAVIGATION_LEFT /*1*/:
            case MotionEvent.AXIS_GENERIC_2 /*33*/:
                return NAVIGATION_UP;
            case NAVIGATION_UP /*2*/:
            case KeyEvent.KEYCODE_MEDIA_RECORD /*130*/:
                return NAVIGATION_DOWN;
            case TextViewDrawableColorFilterAction.TAG /*17*/:
                return NAVIGATION_LEFT;
            case KeyEvent.KEYCODE_ENTER /*66*/:
                return NAVIGATION_RIGHT;
            default:
                throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT, FOCUS_FORWARD, FOCUS_BACKWARD}.");
        }
    }
}
