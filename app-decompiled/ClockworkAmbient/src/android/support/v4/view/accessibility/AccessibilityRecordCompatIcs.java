package android.support.v4.view.accessibility;

import android.view.accessibility.AccessibilityRecord;

class AccessibilityRecordCompatIcs {
    public static void setScrollX(Object obj, int i) {
        AccessibilityRecord accessibilityRecord = (AccessibilityRecord) obj;
        throw new VerifyError("bad dex opcode");
    }

    public static void setScrollY(Object obj, int i) {
        AccessibilityRecord accessibilityRecord = (AccessibilityRecord) obj;
        throw new VerifyError("bad dex opcode");
    }

    public static void setScrollable(Object obj, boolean z) {
        AccessibilityRecord accessibilityRecord = (AccessibilityRecord) obj;
        throw new VerifyError("bad dex opcode");
    }
}
