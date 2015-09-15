package android.media;

import android.media.session.MediaController.Callback;
import android.media.session.PlaybackState;

class RemoteController$MediaControllerCallback extends Callback {
    final /* synthetic */ RemoteController this$0;

    private RemoteController$MediaControllerCallback(RemoteController remoteController) {
        this.this$0 = remoteController;
    }

    public void onPlaybackStateChanged(PlaybackState state) {
        RemoteController.access$800(this.this$0, state);
    }

    public void onMetadataChanged(MediaMetadata metadata) {
        RemoteController.access$900(this.this$0, metadata);
    }
}
