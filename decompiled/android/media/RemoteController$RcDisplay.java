package android.media;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.media.RemoteController.PlaybackInfo;
import android.os.Bundle;
import java.lang.ref.WeakReference;

class RemoteController$RcDisplay extends IRemoteControlDisplay$Stub {
    private final WeakReference<RemoteController> mController;

    RemoteController$RcDisplay(RemoteController rc) {
        this.mController = new WeakReference(rc);
    }

    public void setCurrentClientId(int genId, PendingIntent clientMediaIntent, boolean clearing) {
        RemoteController rc = (RemoteController) this.mController.get();
        if (rc != null) {
            boolean isNew = false;
            synchronized (RemoteController.access$400()) {
                if (RemoteController.access$500(rc) != genId) {
                    RemoteController.access$502(rc, genId);
                    isNew = true;
                }
            }
            if (clientMediaIntent != null) {
                RemoteController.access$700(RemoteController.access$600(rc), 0, 0, genId, 0, clientMediaIntent, 0);
            }
            if (isNew || clearing) {
                RemoteController.access$700(RemoteController.access$600(rc), 4, 0, genId, clearing ? 1 : 0, null, 0);
            }
        }
    }

    public void setEnabled(boolean enabled) {
        RemoteController rc = (RemoteController) this.mController.get();
        if (rc != null) {
            RemoteController.access$700(RemoteController.access$600(rc), 5, 0, enabled ? 1 : 0, 0, null, 0);
        }
    }

    public void setPlaybackState(int genId, int state, long stateChangeTimeMs, long currentPosMs, float speed) {
        RemoteController rc = (RemoteController) this.mController.get();
        if (rc != null) {
            synchronized (RemoteController.access$400()) {
                if (RemoteController.access$500(rc) != genId) {
                    return;
                }
                RemoteController.access$700(RemoteController.access$600(rc), 1, 0, genId, 0, new PlaybackInfo(state, stateChangeTimeMs, currentPosMs, speed), 0);
            }
        }
    }

    public void setTransportControlInfo(int genId, int transportControlFlags, int posCapabilities) {
        RemoteController rc = (RemoteController) this.mController.get();
        if (rc != null) {
            synchronized (RemoteController.access$400()) {
                if (RemoteController.access$500(rc) != genId) {
                    return;
                }
                RemoteController.access$700(RemoteController.access$600(rc), 2, 0, genId, transportControlFlags, null, 0);
            }
        }
    }

    public void setMetadata(int genId, Bundle metadata) {
        RemoteController rc = (RemoteController) this.mController.get();
        if (rc != null && metadata != null) {
            synchronized (RemoteController.access$400()) {
                if (RemoteController.access$500(rc) != genId) {
                    return;
                }
                RemoteController.access$700(RemoteController.access$600(rc), 3, 2, genId, 0, metadata, 0);
            }
        }
    }

    public void setArtwork(int genId, Bitmap artwork) {
        RemoteController rc = (RemoteController) this.mController.get();
        if (rc != null) {
            synchronized (RemoteController.access$400()) {
                if (RemoteController.access$500(rc) != genId) {
                    return;
                }
                Bundle metadata = new Bundle(1);
                metadata.putParcelable(String.valueOf(100), artwork);
                RemoteController.access$700(RemoteController.access$600(rc), 3, 2, genId, 0, metadata, 0);
            }
        }
    }

    public void setAllMetadata(int genId, Bundle metadata, Bitmap artwork) {
        RemoteController rc = (RemoteController) this.mController.get();
        if (rc != null) {
            if (metadata != null || artwork != null) {
                synchronized (RemoteController.access$400()) {
                    if (RemoteController.access$500(rc) != genId) {
                        return;
                    }
                    if (metadata == null) {
                        metadata = new Bundle(1);
                    }
                    if (artwork != null) {
                        metadata.putParcelable(String.valueOf(100), artwork);
                    }
                    RemoteController.access$700(RemoteController.access$600(rc), 3, 2, genId, 0, metadata, 0);
                }
            }
        }
    }
}
