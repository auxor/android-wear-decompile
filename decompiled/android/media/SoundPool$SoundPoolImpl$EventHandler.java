package android.media;

import android.media.SoundPool.SoundPoolImpl;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

class SoundPool$SoundPoolImpl$EventHandler extends Handler {
    private SoundPool mSoundPool;
    final /* synthetic */ SoundPoolImpl this$0;

    public SoundPool$SoundPoolImpl$EventHandler(SoundPoolImpl soundPoolImpl, SoundPool soundPool, Looper looper) {
        this.this$0 = soundPoolImpl;
        super(looper);
        this.mSoundPool = soundPool;
    }

    public void handleMessage(Message msg) {
        switch (msg.what) {
            case Toast.LENGTH_LONG /*1*/:
                synchronized (SoundPoolImpl.access$100(this.this$0)) {
                    if (SoundPoolImpl.access$200(this.this$0) != null) {
                        SoundPoolImpl.access$200(this.this$0).onLoadComplete(this.mSoundPool, msg.arg1, msg.arg2);
                    }
                    break;
                }
            default:
                Log.e("SoundPool", "Unknown message type " + msg.what);
        }
    }
}
