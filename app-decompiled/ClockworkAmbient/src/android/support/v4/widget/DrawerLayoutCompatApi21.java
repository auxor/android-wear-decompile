package android.support.v4.widget;

import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowInsets;

class DrawerLayoutCompatApi21 {
    private static final int[] THEME_ATTRS;

    static {
        THEME_ATTRS = new int[]{16843828};
    }

    public static void applyMarginInsets(MarginLayoutParams marginLayoutParams, Object obj, int i) {
        WindowInsets windowInsets = (WindowInsets) obj;
        if (i == 3) {
            throw new VerifyError("bad dex opcode");
        } else if (i == 5) {
            throw new VerifyError("bad dex opcode");
        } else {
            throw new VerifyError("bad dex opcode");
        }
    }

    public static void dispatchChildInsets(View view, Object obj, int i) {
        WindowInsets windowInsets = (WindowInsets) obj;
        if (i == 3) {
            throw new VerifyError("bad dex opcode");
        } else if (i == 5) {
            throw new VerifyError("bad dex opcode");
        } else {
            throw new VerifyError("bad dex opcode");
        }
    }

    public static int getTopInset(Object obj) {
        if (obj == null) {
            return 0;
        }
        WindowInsets windowInsets = (WindowInsets) obj;
        throw new VerifyError("bad dex opcode");
    }
}
