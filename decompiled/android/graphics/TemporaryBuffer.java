package android.graphics;

import android.view.WindowManager.LayoutParams;
import com.android.internal.util.ArrayUtils;

public class TemporaryBuffer {
    private static char[] sTemp;

    public static char[] obtain(int len) {
        synchronized (TemporaryBuffer.class) {
            char[] buf = sTemp;
            sTemp = null;
        }
        if (buf == null || buf.length < len) {
            return ArrayUtils.newUnpaddedCharArray(len);
        }
        return buf;
    }

    public static void recycle(char[] temp) {
        if (temp.length <= LayoutParams.TYPE_APPLICATION_PANEL) {
            synchronized (TemporaryBuffer.class) {
                sTemp = temp;
            }
        }
    }

    static {
        sTemp = null;
    }
}
