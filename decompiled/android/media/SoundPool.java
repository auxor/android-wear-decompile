package android.media;

import android.app.ActivityThread;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes.Builder;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.AndroidRuntimeException;
import android.util.Log;
import com.android.internal.app.IAppOpsService;
import com.android.internal.app.IAppOpsService.Stub;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class SoundPool {
    private final SoundPoolDelegate mImpl;

    interface SoundPoolDelegate {
        void autoPause();

        void autoResume();

        int load(Context context, int i, int i2);

        int load(AssetFileDescriptor assetFileDescriptor, int i);

        int load(FileDescriptor fileDescriptor, long j, long j2, int i);

        int load(String str, int i);

        void pause(int i);

        int play(int i, float f, float f2, int i2, int i3, float f3);

        void release();

        void resume(int i);

        void setLoop(int i, int i2);

        void setOnLoadCompleteListener(OnLoadCompleteListener onLoadCompleteListener);

        void setPriority(int i, int i2);

        void setRate(int i, float f);

        void setVolume(int i, float f);

        void setVolume(int i, float f, float f2);

        void stop(int i);

        boolean unload(int i);
    }

    static class SoundPoolImpl implements SoundPoolDelegate {
        private static final boolean DEBUG = false;
        private static final int SAMPLE_LOADED = 1;
        private static final String TAG = "SoundPool";
        private final IAppOpsService mAppOps;
        private final AudioAttributes mAttributes;
        private EventHandler mEventHandler;
        private final Object mLock;
        private long mNativeContext;
        private OnLoadCompleteListener mOnLoadCompleteListener;
        private SoundPool mProxy;

        private final native int _load(FileDescriptor fileDescriptor, long j, long j2, int i);

        private final native int _load(String str, int i);

        private final native void _setVolume(int i, float f, float f2);

        private final native int native_setup(Object obj, int i, Object obj2);

        public final native int _play(int i, float f, float f2, int i2, int i3, float f3);

        public final native void autoPause();

        public final native void autoResume();

        public final native void pause(int i);

        public final native void release();

        public final native void resume(int i);

        public final native void setLoop(int i, int i2);

        public final native void setPriority(int i, int i2);

        public final native void setRate(int i, float f);

        public final native void stop(int i);

        public final native boolean unload(int i);

        static {
            System.loadLibrary("soundpool");
        }

        public SoundPoolImpl(SoundPool proxy, int maxStreams, AudioAttributes attr) {
            if (native_setup(new WeakReference(this), maxStreams, attr) != 0) {
                throw new RuntimeException("Native setup failed");
            }
            this.mLock = new Object();
            this.mProxy = proxy;
            this.mAttributes = attr;
            this.mAppOps = Stub.asInterface(ServiceManager.getService("appops"));
        }

        public int load(String path, int priority) {
            if (path.startsWith("http:")) {
                return _load(path, priority);
            }
            try {
                File f = new File(path);
                ParcelFileDescriptor fd = ParcelFileDescriptor.open(f, 268435456);
                if (fd == null) {
                    return 0;
                }
                int id = _load(fd.getFileDescriptor(), 0, f.length(), priority);
                fd.close();
                return id;
            } catch (IOException e) {
                Log.e(TAG, "error loading " + path);
                return 0;
            }
        }

        public int load(Context context, int resId, int priority) {
            AssetFileDescriptor afd = context.getResources().openRawResourceFd(resId);
            int id = 0;
            if (afd != null) {
                id = _load(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength(), priority);
                try {
                    afd.close();
                } catch (IOException e) {
                }
            }
            return id;
        }

        public int load(AssetFileDescriptor afd, int priority) {
            if (afd == null) {
                return 0;
            }
            long len = afd.getLength();
            if (len >= 0) {
                return _load(afd.getFileDescriptor(), afd.getStartOffset(), len, priority);
            }
            throw new AndroidRuntimeException("no length for fd");
        }

        public int load(FileDescriptor fd, long offset, long length, int priority) {
            return _load(fd, offset, length, priority);
        }

        public final int play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate) {
            if (isRestricted()) {
                rightVolume = 0.0f;
                leftVolume = 0.0f;
            }
            return _play(soundID, leftVolume, rightVolume, priority, loop, rate);
        }

        private boolean isRestricted() {
            try {
                if (this.mAppOps.checkAudioOperation(28, this.mAttributes.getUsage(), Process.myUid(), ActivityThread.currentPackageName()) != 0) {
                    return true;
                }
                return DEBUG;
            } catch (RemoteException e) {
                return DEBUG;
            }
        }

        public final void setVolume(int streamID, float leftVolume, float rightVolume) {
            if (!isRestricted()) {
                _setVolume(streamID, leftVolume, rightVolume);
            }
        }

        public void setVolume(int streamID, float volume) {
            setVolume(streamID, volume, volume);
        }

        public void setOnLoadCompleteListener(OnLoadCompleteListener listener) {
            synchronized (this.mLock) {
                if (listener != null) {
                    Looper looper = Looper.myLooper();
                    if (looper != null) {
                        this.mEventHandler = new EventHandler(this, this.mProxy, looper);
                    } else {
                        looper = Looper.getMainLooper();
                        if (looper != null) {
                            this.mEventHandler = new EventHandler(this, this.mProxy, looper);
                        } else {
                            this.mEventHandler = null;
                        }
                    }
                } else {
                    this.mEventHandler = null;
                }
                this.mOnLoadCompleteListener = listener;
            }
        }

        private static void postEventFromNative(Object weakRef, int msg, int arg1, int arg2, Object obj) {
            SoundPoolImpl soundPoolImpl = (SoundPoolImpl) ((WeakReference) weakRef).get();
            if (soundPoolImpl != null && soundPoolImpl.mEventHandler != null) {
                soundPoolImpl.mEventHandler.sendMessage(soundPoolImpl.mEventHandler.obtainMessage(msg, arg1, arg2, obj));
            }
        }

        protected void finalize() {
            release();
        }
    }

    static class SoundPoolStub implements SoundPoolDelegate {
        public int load(String path, int priority) {
            return 0;
        }

        public int load(Context context, int resId, int priority) {
            return 0;
        }

        public int load(AssetFileDescriptor afd, int priority) {
            return 0;
        }

        public int load(FileDescriptor fd, long offset, long length, int priority) {
            return 0;
        }

        public final boolean unload(int soundID) {
            return true;
        }

        public final int play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate) {
            return 0;
        }

        public final void pause(int streamID) {
        }

        public final void resume(int streamID) {
        }

        public final void autoPause() {
        }

        public final void autoResume() {
        }

        public final void stop(int streamID) {
        }

        public final void setVolume(int streamID, float leftVolume, float rightVolume) {
        }

        public void setVolume(int streamID, float volume) {
        }

        public final void setPriority(int streamID, int priority) {
        }

        public final void setLoop(int streamID, int loop) {
        }

        public final void setRate(int streamID, float rate) {
        }

        public void setOnLoadCompleteListener(OnLoadCompleteListener listener) {
        }

        public final void release() {
        }
    }

    public SoundPool(int maxStreams, int streamType, int srcQuality) {
        this(maxStreams, new Builder().setInternalLegacyStreamType(streamType).build());
    }

    private SoundPool(int maxStreams, AudioAttributes attributes) {
        if (SystemProperties.getBoolean("config.disable_media", false)) {
            this.mImpl = new SoundPoolStub();
        } else {
            this.mImpl = new SoundPoolImpl(this, maxStreams, attributes);
        }
    }

    public int load(String path, int priority) {
        return this.mImpl.load(path, priority);
    }

    public int load(Context context, int resId, int priority) {
        return this.mImpl.load(context, resId, priority);
    }

    public int load(AssetFileDescriptor afd, int priority) {
        return this.mImpl.load(afd, priority);
    }

    public int load(FileDescriptor fd, long offset, long length, int priority) {
        return this.mImpl.load(fd, offset, length, priority);
    }

    public final boolean unload(int soundID) {
        return this.mImpl.unload(soundID);
    }

    public final int play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate) {
        return this.mImpl.play(soundID, leftVolume, rightVolume, priority, loop, rate);
    }

    public final void pause(int streamID) {
        this.mImpl.pause(streamID);
    }

    public final void resume(int streamID) {
        this.mImpl.resume(streamID);
    }

    public final void autoPause() {
        this.mImpl.autoPause();
    }

    public final void autoResume() {
        this.mImpl.autoResume();
    }

    public final void stop(int streamID) {
        this.mImpl.stop(streamID);
    }

    public final void setVolume(int streamID, float leftVolume, float rightVolume) {
        this.mImpl.setVolume(streamID, leftVolume, rightVolume);
    }

    public void setVolume(int streamID, float volume) {
        setVolume(streamID, volume, volume);
    }

    public final void setPriority(int streamID, int priority) {
        this.mImpl.setPriority(streamID, priority);
    }

    public final void setLoop(int streamID, int loop) {
        this.mImpl.setLoop(streamID, loop);
    }

    public final void setRate(int streamID, float rate) {
        this.mImpl.setRate(streamID, rate);
    }

    public void setOnLoadCompleteListener(OnLoadCompleteListener listener) {
        this.mImpl.setOnLoadCompleteListener(listener);
    }

    public final void release() {
        this.mImpl.release();
    }
}
