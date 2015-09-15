package android.view;

import android.graphics.Rect;
import android.graphics.Region;
import android.os.IBinder;
import java.util.List;

public abstract class WindowManagerInternal {

    public interface MagnificationCallbacks {
        void onMagnifedBoundsChanged(Region region);

        void onRectangleOnScreenRequested(int i, int i2, int i3, int i4);

        void onRotationChanged(int i);

        void onUserContextChanged();
    }

    public interface WindowsForAccessibilityCallback {
        void onWindowsForAccessibilityChanged(List<WindowInfo> list);
    }

    public abstract void addWindowToken(IBinder iBinder, int i);

    public abstract MagnificationSpec getCompatibleMagnificationSpecForWindow(IBinder iBinder);

    public abstract IBinder getFocusedWindowToken();

    public abstract void getWindowFrame(IBinder iBinder, Rect rect);

    public abstract boolean isKeyguardLocked();

    public abstract void removeWindowToken(IBinder iBinder, boolean z);

    public abstract void requestTraversalFromDisplayManager();

    public abstract void setInputFilter(IInputFilter iInputFilter);

    public abstract void setMagnificationCallbacks(MagnificationCallbacks magnificationCallbacks);

    public abstract void setMagnificationSpec(MagnificationSpec magnificationSpec);

    public abstract void setWindowsForAccessibilityCallback(WindowsForAccessibilityCallback windowsForAccessibilityCallback);

    public abstract void showGlobalActions();

    public abstract void waitForAllWindowsDrawn(Runnable runnable, long j);
}
