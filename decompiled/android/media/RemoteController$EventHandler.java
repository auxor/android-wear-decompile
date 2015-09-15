package android.media;

import android.app.PendingIntent;
import android.media.RemoteController.PlaybackInfo;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.SpellChecker;
import android.widget.Toast;

class RemoteController$EventHandler extends Handler {
    final /* synthetic */ RemoteController this$0;

    public RemoteController$EventHandler(RemoteController remoteController, RemoteController rc, Looper looper) {
        this.this$0 = remoteController;
        super(looper);
    }

    public void handleMessage(Message msg) {
        boolean z = true;
        RemoteController remoteController;
        switch (msg.what) {
            case Toast.LENGTH_SHORT /*0*/:
                RemoteController.access$1100(this.this$0, msg.arg1, (PendingIntent) msg.obj);
            case Toast.LENGTH_LONG /*1*/:
                RemoteController.access$1200(this.this$0, msg.arg1, (PlaybackInfo) msg.obj);
            case Action.MERGE_IGNORE /*2*/:
                RemoteController.access$1300(this.this$0, msg.arg1, msg.arg2);
            case SetDrawableParameters.TAG /*3*/:
                RemoteController.access$1400(this.this$0, msg.arg1, (Bundle) msg.obj);
            case ViewGroupAction.TAG /*4*/:
                remoteController = this.this$0;
                int i = msg.arg1;
                if (msg.arg2 != 1) {
                    z = false;
                }
                RemoteController.access$1500(remoteController, i, z);
            case ReflectionActionWithoutParams.TAG /*5*/:
                remoteController = this.this$0;
                if (msg.arg1 != 1) {
                    z = false;
                }
                RemoteController.access$1600(remoteController, z);
            case SetEmptyView.TAG /*6*/:
                RemoteController.access$800(this.this$0, (PlaybackState) msg.obj);
            case SpellChecker.AVERAGE_WORD_LENGTH /*7*/:
                RemoteController.access$900(this.this$0, (MediaMetadata) msg.obj);
            default:
                Log.e("RemoteController", "unknown event " + msg.what);
        }
    }
}
