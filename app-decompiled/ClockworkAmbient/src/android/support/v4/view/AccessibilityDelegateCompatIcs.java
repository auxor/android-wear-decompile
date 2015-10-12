package android.support.v4.view;

import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

class AccessibilityDelegateCompatIcs {

    public interface AccessibilityDelegateBridge {
        boolean dispatchPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent);

        void onInitializeAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent);

        void onInitializeAccessibilityNodeInfo(View view, Object obj);

        void onPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent);

        boolean onRequestSendAccessibilityEvent(ViewGroup viewGroup, View view, AccessibilityEvent accessibilityEvent);

        void sendAccessibilityEvent(View view, int i);

        void sendAccessibilityEventUnchecked(View view, AccessibilityEvent accessibilityEvent);
    }

    /* renamed from: android.support.v4.view.AccessibilityDelegateCompatIcs.1 */
    static final class AnonymousClass1 extends AccessibilityDelegate {
        final /* synthetic */ AccessibilityDelegateBridge val$bridge;

        AnonymousClass1(AccessibilityDelegateBridge accessibilityDelegateBridge) {
            this.val$bridge = accessibilityDelegateBridge;
            throw new VerifyError("bad dex opcode");
        }

        public boolean dispatchPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            return this.val$bridge.dispatchPopulateAccessibilityEvent(view, accessibilityEvent);
        }

        public void onInitializeAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            this.val$bridge.onInitializeAccessibilityEvent(view, accessibilityEvent);
        }

        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfo accessibilityNodeInfo) {
            this.val$bridge.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
        }

        public void onPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            this.val$bridge.onPopulateAccessibilityEvent(view, accessibilityEvent);
        }

        public boolean onRequestSendAccessibilityEvent(ViewGroup viewGroup, View view, AccessibilityEvent accessibilityEvent) {
            return this.val$bridge.onRequestSendAccessibilityEvent(viewGroup, view, accessibilityEvent);
        }

        public void sendAccessibilityEvent(View view, int i) {
            this.val$bridge.sendAccessibilityEvent(view, i);
        }

        public void sendAccessibilityEventUnchecked(View view, AccessibilityEvent accessibilityEvent) {
            this.val$bridge.sendAccessibilityEventUnchecked(view, accessibilityEvent);
        }
    }

    public static boolean dispatchPopulateAccessibilityEvent(Object obj, View view, AccessibilityEvent accessibilityEvent) {
        AccessibilityDelegate accessibilityDelegate = (AccessibilityDelegate) obj;
        throw new VerifyError("bad dex opcode");
    }

    public static Object newAccessibilityDelegateBridge(AccessibilityDelegateBridge accessibilityDelegateBridge) {
        return new AnonymousClass1(accessibilityDelegateBridge);
    }

    public static Object newAccessibilityDelegateDefaultImpl() {
        return new AccessibilityDelegate();
    }

    public static void onInitializeAccessibilityEvent(Object obj, View view, AccessibilityEvent accessibilityEvent) {
        AccessibilityDelegate accessibilityDelegate = (AccessibilityDelegate) obj;
        throw new VerifyError("bad dex opcode");
    }

    public static void onInitializeAccessibilityNodeInfo(Object obj, View view, Object obj2) {
        AccessibilityDelegate accessibilityDelegate = (AccessibilityDelegate) obj;
        AccessibilityNodeInfo accessibilityNodeInfo = (AccessibilityNodeInfo) obj2;
        throw new VerifyError("bad dex opcode");
    }

    public static void onPopulateAccessibilityEvent(Object obj, View view, AccessibilityEvent accessibilityEvent) {
        AccessibilityDelegate accessibilityDelegate = (AccessibilityDelegate) obj;
        throw new VerifyError("bad dex opcode");
    }

    public static boolean onRequestSendAccessibilityEvent(Object obj, ViewGroup viewGroup, View view, AccessibilityEvent accessibilityEvent) {
        AccessibilityDelegate accessibilityDelegate = (AccessibilityDelegate) obj;
        throw new VerifyError("bad dex opcode");
    }

    public static void sendAccessibilityEvent(Object obj, View view, int i) {
        AccessibilityDelegate accessibilityDelegate = (AccessibilityDelegate) obj;
        throw new VerifyError("bad dex opcode");
    }

    public static void sendAccessibilityEventUnchecked(Object obj, View view, AccessibilityEvent accessibilityEvent) {
        AccessibilityDelegate accessibilityDelegate = (AccessibilityDelegate) obj;
        throw new VerifyError("bad dex opcode");
    }
}
