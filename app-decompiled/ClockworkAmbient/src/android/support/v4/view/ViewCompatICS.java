package android.support.v4.view;

import android.view.View;
import android.view.View.AccessibilityDelegate;

class ViewCompatICS {
    public static boolean canScrollHorizontally(View view, int i) {
        return view.canScrollHorizontally(i);
    }

    public static boolean canScrollVertically(View view, int i) {
        return view.canScrollVertically(i);
    }

    public static void setAccessibilityDelegate(View view, Object obj) {
        AccessibilityDelegate accessibilityDelegate = (AccessibilityDelegate) obj;
        throw new VerifyError("bad dex opcode");
    }
}
