package android.animation;

import android.view.inputmethod.EditorInfo;

public class ArgbEvaluator implements TypeEvaluator {
    private static final ArgbEvaluator sInstance;

    static {
        sInstance = new ArgbEvaluator();
    }

    public static ArgbEvaluator getInstance() {
        return sInstance;
    }

    public Object evaluate(float fraction, Object startValue, Object endValue) {
        int startInt = ((Integer) startValue).intValue();
        int startA = (startInt >> 24) & EditorInfo.IME_MASK_ACTION;
        int startR = (startInt >> 16) & EditorInfo.IME_MASK_ACTION;
        int startG = (startInt >> 8) & EditorInfo.IME_MASK_ACTION;
        int startB = startInt & EditorInfo.IME_MASK_ACTION;
        int endInt = ((Integer) endValue).intValue();
        return Integer.valueOf(((((((int) (((float) (((endInt >> 24) & EditorInfo.IME_MASK_ACTION) - startA)) * fraction)) + startA) << 24) | ((((int) (((float) (((endInt >> 16) & EditorInfo.IME_MASK_ACTION) - startR)) * fraction)) + startR) << 16)) | ((((int) (((float) (((endInt >> 8) & EditorInfo.IME_MASK_ACTION) - startG)) * fraction)) + startG) << 8)) | (((int) (((float) ((endInt & EditorInfo.IME_MASK_ACTION) - startB)) * fraction)) + startB));
    }
}
