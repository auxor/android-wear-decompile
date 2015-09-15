package android.media;

import android.app.PendingIntent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

class MediaFocusControl$MediaEventHandler extends Handler {
    final /* synthetic */ MediaFocusControl this$0;

    MediaFocusControl$MediaEventHandler(MediaFocusControl mediaFocusControl, Looper looper) {
        this.this$0 = mediaFocusControl;
        super(looper);
    }

    public void handleMessage(Message msg) {
        switch (msg.what) {
            case Toast.LENGTH_LONG /*1*/:
                MediaFocusControl.access$900(this.this$0);
            case Action.MERGE_IGNORE /*2*/:
                MediaFocusControl.access$1000(this.this$0, (PlayerRecord) msg.obj, msg.arg1);
            case SetDrawableParameters.TAG /*3*/:
                MediaFocusControl.access$1100(this.this$0);
            case ReflectionActionWithoutParams.TAG /*5*/:
                MediaFocusControl.access$1200(this.this$0, msg.arg1, (IRemoteVolumeObserver) msg.obj);
            case SetOnClickFillInIntent.TAG /*9*/:
                MediaFocusControl.access$1300(this.this$0, (IRemoteControlDisplay) msg.obj, msg.arg1, msg.arg2);
            case SetRemoteViewsAdapterIntent.TAG /*10*/:
                MediaFocusControl.access$1400(this.this$0);
            case TextViewDrawableAction.TAG /*11*/:
                this.this$0.unregisterMediaButtonIntent((PendingIntent) msg.obj);
            default:
        }
    }
}
