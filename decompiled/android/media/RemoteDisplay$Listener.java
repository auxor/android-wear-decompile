package android.media;

import android.view.Surface;

public interface RemoteDisplay$Listener {
    void onDisplayConnected(Surface surface, int i, int i2, int i3, int i4);

    void onDisplayDisconnected();

    void onDisplayError(int i);
}
