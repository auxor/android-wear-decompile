package android.media;

import android.content.Context;
import android.media.AudioAttributes.Builder;
import android.media.IAudioService.Stub;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.WindowManager.LayoutParams;
import android.widget.RelativeLayout;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

public class AudioRecord {
    private static final int AUDIORECORD_ERROR_SETUP_INVALIDCHANNELMASK = -17;
    private static final int AUDIORECORD_ERROR_SETUP_INVALIDFORMAT = -18;
    private static final int AUDIORECORD_ERROR_SETUP_INVALIDSOURCE = -19;
    private static final int AUDIORECORD_ERROR_SETUP_NATIVEINITFAILED = -20;
    private static final int AUDIORECORD_ERROR_SETUP_ZEROFRAMECOUNT = -16;
    public static final int ERROR = -1;
    public static final int ERROR_BAD_VALUE = -2;
    public static final int ERROR_INVALID_OPERATION = -3;
    private static final int NATIVE_EVENT_MARKER = 2;
    private static final int NATIVE_EVENT_NEW_POS = 3;
    public static final int RECORDSTATE_RECORDING = 3;
    public static final int RECORDSTATE_STOPPED = 1;
    public static final int STATE_INITIALIZED = 1;
    public static final int STATE_UNINITIALIZED = 0;
    public static final String SUBMIX_FIXED_VOLUME = "fixedVolume";
    public static final int SUCCESS = 0;
    private static final String TAG = "android.media.AudioRecord";
    private AudioAttributes mAudioAttributes;
    private int mAudioFormat;
    private int mChannelCount;
    private int mChannelMask;
    private NativeEventHandler mEventHandler;
    private final IBinder mICallBack;
    private Looper mInitializationLooper;
    private boolean mIsSubmixFullVolume;
    private int mNativeBufferSizeInBytes;
    private long mNativeCallbackCookie;
    private long mNativeRecorderInJavaObj;
    private OnRecordPositionUpdateListener mPositionListener;
    private final Object mPositionListenerLock;
    private int mRecordSource;
    private int mRecordingState;
    private final Object mRecordingStateLock;
    private int mSampleRate;
    private int mSessionId;
    private int mState;

    private final native void native_finalize();

    private final native int native_get_marker_pos();

    private static final native int native_get_min_buff_size(int i, int i2, int i3);

    private final native int native_get_pos_update_period();

    private final native int native_read_in_byte_array(byte[] bArr, int i, int i2);

    private final native int native_read_in_direct_buffer(Object obj, int i);

    private final native int native_read_in_short_array(short[] sArr, int i, int i2);

    private final native void native_release();

    private final native int native_set_marker_pos(int i);

    private final native int native_set_pos_update_period(int i);

    private final native int native_setup(Object obj, Object obj2, int i, int i2, int i3, int i4, int[] iArr);

    private final native int native_start(int i, int i2);

    private final native void native_stop();

    public AudioRecord(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes) throws IllegalArgumentException {
        this(new Builder().setInternalCapturePreset(audioSource).build(), new AudioFormat.Builder().setChannelMask(getChannelMaskFromLegacyConfig(channelConfig, true)).setEncoding(audioFormat).setSampleRate(sampleRateInHz).build(), bufferSizeInBytes, SUCCESS);
    }

    public AudioRecord(AudioAttributes attributes, AudioFormat format, int bufferSizeInBytes, int sessionId) throws IllegalArgumentException {
        this.mState = SUCCESS;
        this.mRecordingState = STATE_INITIALIZED;
        this.mRecordingStateLock = new Object();
        this.mPositionListener = null;
        this.mPositionListenerLock = new Object();
        this.mEventHandler = null;
        this.mInitializationLooper = null;
        this.mNativeBufferSizeInBytes = SUCCESS;
        this.mSessionId = SUCCESS;
        this.mIsSubmixFullVolume = false;
        this.mICallBack = new Binder();
        this.mRecordingState = STATE_INITIALIZED;
        if (attributes == null) {
            throw new IllegalArgumentException("Illegal null AudioAttributes");
        } else if (format == null) {
            throw new IllegalArgumentException("Illegal null AudioFormat");
        } else {
            int rate;
            Looper myLooper = Looper.myLooper();
            this.mInitializationLooper = myLooper;
            if (myLooper == null) {
                this.mInitializationLooper = Looper.getMainLooper();
            }
            if (attributes.getCapturePreset() == 8) {
                Builder filteredAttr = new Builder();
                for (String tag : attributes.getTags()) {
                    if (tag.equalsIgnoreCase(SUBMIX_FIXED_VOLUME)) {
                        this.mIsSubmixFullVolume = true;
                        Log.v(TAG, "Will record from REMOTE_SUBMIX at full fixed volume");
                    } else {
                        filteredAttr.addTag(tag);
                    }
                }
                filteredAttr.setInternalCapturePreset(attributes.getCapturePreset());
                this.mAudioAttributes = filteredAttr.build();
            } else {
                this.mAudioAttributes = attributes;
            }
            if ((format.getPropertySetMask() & NATIVE_EVENT_MARKER) != 0) {
                rate = format.getSampleRate();
            } else {
                rate = AudioSystem.getPrimaryOutputSamplingRate();
                if (rate <= 0) {
                    rate = 44100;
                }
            }
            int encoding = STATE_INITIALIZED;
            if ((format.getPropertySetMask() & STATE_INITIALIZED) != 0) {
                encoding = format.getEncoding();
            }
            audioParamCheck(attributes.getCapturePreset(), rate, encoding);
            this.mChannelCount = AudioFormat.channelCountFromInChannelMask(format.getChannelMask());
            this.mChannelMask = getChannelMaskFromLegacyConfig(format.getChannelMask(), false);
            audioBuffSizeCheck(bufferSizeInBytes);
            int[] session = new int[STATE_INITIALIZED];
            session[SUCCESS] = sessionId;
            int initResult = native_setup(new WeakReference(this), this.mAudioAttributes, this.mSampleRate, this.mChannelMask, this.mAudioFormat, this.mNativeBufferSizeInBytes, session);
            if (initResult != 0) {
                loge("Error code " + initResult + " when initializing native AudioRecord object.");
                return;
            }
            this.mSessionId = session[SUCCESS];
            this.mState = STATE_INITIALIZED;
        }
    }

    private static int getChannelMaskFromLegacyConfig(int inChannelConfig, boolean allowLegacyConfig) {
        int mask;
        switch (inChannelConfig) {
            case STATE_INITIALIZED /*1*/:
            case NATIVE_EVENT_MARKER /*2*/:
            case RelativeLayout.START_OF /*16*/:
                mask = 16;
                break;
            case RECORDSTATE_RECORDING /*3*/:
            case BitmapReflectionAction.TAG /*12*/:
                mask = 12;
                break;
            case LayoutParams.SOFT_INPUT_ADJUST_NOTHING /*48*/:
                mask = inChannelConfig;
                break;
            default:
                throw new IllegalArgumentException("Unsupported channel configuration.");
        }
        if (allowLegacyConfig || (inChannelConfig != NATIVE_EVENT_MARKER && inChannelConfig != RECORDSTATE_RECORDING)) {
            return mask;
        }
        throw new IllegalArgumentException("Unsupported deprecated configuration.");
    }

    private void audioParamCheck(int audioSource, int sampleRateInHz, int audioFormat) throws IllegalArgumentException {
        if (audioSource < 0 || !(audioSource <= MediaRecorder.getAudioSourceMax() || audioSource == 1998 || audioSource == LayoutParams.LAST_SUB_WINDOW)) {
            throw new IllegalArgumentException("Invalid audio source.");
        }
        this.mRecordSource = audioSource;
        if (sampleRateInHz < 4000 || sampleRateInHz > 48000) {
            throw new IllegalArgumentException(sampleRateInHz + "Hz is not a supported sample rate.");
        }
        this.mSampleRate = sampleRateInHz;
        switch (audioFormat) {
            case STATE_INITIALIZED /*1*/:
                this.mAudioFormat = NATIVE_EVENT_MARKER;
            case NATIVE_EVENT_MARKER /*2*/:
            case RECORDSTATE_RECORDING /*3*/:
                this.mAudioFormat = audioFormat;
            default:
                throw new IllegalArgumentException("Unsupported sample encoding. Should be ENCODING_PCM_8BIT or ENCODING_PCM_16BIT.");
        }
    }

    private void audioBuffSizeCheck(int audioBufferSize) throws IllegalArgumentException {
        if (audioBufferSize % (this.mChannelCount * AudioFormat.getBytesPerSample(this.mAudioFormat)) != 0 || audioBufferSize < STATE_INITIALIZED) {
            throw new IllegalArgumentException("Invalid audio buffer size.");
        }
        this.mNativeBufferSizeInBytes = audioBufferSize;
    }

    public void release() {
        try {
            stop();
        } catch (IllegalStateException e) {
        }
        native_release();
        this.mState = SUCCESS;
    }

    protected void finalize() {
        release();
    }

    public int getSampleRate() {
        return this.mSampleRate;
    }

    public int getAudioSource() {
        return this.mRecordSource;
    }

    public int getAudioFormat() {
        return this.mAudioFormat;
    }

    public int getChannelConfiguration() {
        return this.mChannelMask;
    }

    public int getChannelCount() {
        return this.mChannelCount;
    }

    public int getState() {
        return this.mState;
    }

    public int getRecordingState() {
        int i;
        synchronized (this.mRecordingStateLock) {
            i = this.mRecordingState;
        }
        return i;
    }

    public int getNotificationMarkerPosition() {
        return native_get_marker_pos();
    }

    public int getPositionNotificationPeriod() {
        return native_get_pos_update_period();
    }

    public static int getMinBufferSize(int sampleRateInHz, int channelConfig, int audioFormat) {
        int channelCount;
        switch (channelConfig) {
            case STATE_INITIALIZED /*1*/:
            case NATIVE_EVENT_MARKER /*2*/:
            case RelativeLayout.START_OF /*16*/:
                channelCount = STATE_INITIALIZED;
                break;
            case RECORDSTATE_RECORDING /*3*/:
            case BitmapReflectionAction.TAG /*12*/:
            case LayoutParams.SOFT_INPUT_ADJUST_NOTHING /*48*/:
                channelCount = NATIVE_EVENT_MARKER;
                break;
            default:
                loge("getMinBufferSize(): Invalid channel configuration.");
                return ERROR_BAD_VALUE;
        }
        if (audioFormat != NATIVE_EVENT_MARKER) {
            loge("getMinBufferSize(): Invalid audio format.");
            return ERROR_BAD_VALUE;
        }
        int size = native_get_min_buff_size(sampleRateInHz, channelCount, audioFormat);
        if (size == 0) {
            return ERROR_BAD_VALUE;
        }
        if (size == ERROR) {
            return ERROR;
        }
        return size;
    }

    public int getAudioSessionId() {
        return this.mSessionId;
    }

    public void startRecording() throws IllegalStateException {
        if (this.mState != STATE_INITIALIZED) {
            throw new IllegalStateException("startRecording() called on an uninitialized AudioRecord.");
        }
        synchronized (this.mRecordingStateLock) {
            if (native_start(SUCCESS, SUCCESS) == 0) {
                handleFullVolumeRec(true);
                this.mRecordingState = RECORDSTATE_RECORDING;
            }
        }
    }

    public void startRecording(MediaSyncEvent syncEvent) throws IllegalStateException {
        if (this.mState != STATE_INITIALIZED) {
            throw new IllegalStateException("startRecording() called on an uninitialized AudioRecord.");
        }
        synchronized (this.mRecordingStateLock) {
            if (native_start(syncEvent.getType(), syncEvent.getAudioSessionId()) == 0) {
                handleFullVolumeRec(true);
                this.mRecordingState = RECORDSTATE_RECORDING;
            }
        }
    }

    public void stop() throws IllegalStateException {
        if (this.mState != STATE_INITIALIZED) {
            throw new IllegalStateException("stop() called on an uninitialized AudioRecord.");
        }
        synchronized (this.mRecordingStateLock) {
            handleFullVolumeRec(false);
            native_stop();
            this.mRecordingState = STATE_INITIALIZED;
        }
    }

    private void handleFullVolumeRec(boolean starting) {
        if (this.mIsSubmixFullVolume) {
            try {
                Stub.asInterface(ServiceManager.getService(Context.AUDIO_SERVICE)).forceRemoteSubmixFullVolume(starting, this.mICallBack);
            } catch (RemoteException e) {
                Log.e(TAG, "Error talking to AudioService when handling full submix volume", e);
            }
        }
    }

    public int read(byte[] audioData, int offsetInBytes, int sizeInBytes) {
        if (this.mState != STATE_INITIALIZED) {
            return ERROR_INVALID_OPERATION;
        }
        if (audioData == null || offsetInBytes < 0 || sizeInBytes < 0 || offsetInBytes + sizeInBytes < 0 || offsetInBytes + sizeInBytes > audioData.length) {
            return ERROR_BAD_VALUE;
        }
        return native_read_in_byte_array(audioData, offsetInBytes, sizeInBytes);
    }

    public int read(short[] audioData, int offsetInShorts, int sizeInShorts) {
        if (this.mState != STATE_INITIALIZED) {
            return ERROR_INVALID_OPERATION;
        }
        if (audioData == null || offsetInShorts < 0 || sizeInShorts < 0 || offsetInShorts + sizeInShorts < 0 || offsetInShorts + sizeInShorts > audioData.length) {
            return ERROR_BAD_VALUE;
        }
        return native_read_in_short_array(audioData, offsetInShorts, sizeInShorts);
    }

    public int read(ByteBuffer audioBuffer, int sizeInBytes) {
        if (this.mState != STATE_INITIALIZED) {
            return ERROR_INVALID_OPERATION;
        }
        if (audioBuffer == null || sizeInBytes < 0) {
            return ERROR_BAD_VALUE;
        }
        return native_read_in_direct_buffer(audioBuffer, sizeInBytes);
    }

    public void setRecordPositionUpdateListener(OnRecordPositionUpdateListener listener) {
        setRecordPositionUpdateListener(listener, null);
    }

    public void setRecordPositionUpdateListener(OnRecordPositionUpdateListener listener, Handler handler) {
        synchronized (this.mPositionListenerLock) {
            this.mPositionListener = listener;
            if (listener == null) {
                this.mEventHandler = null;
            } else if (handler != null) {
                this.mEventHandler = new NativeEventHandler(this, this, handler.getLooper());
            } else {
                this.mEventHandler = new NativeEventHandler(this, this, this.mInitializationLooper);
            }
        }
    }

    public int setNotificationMarkerPosition(int markerInFrames) {
        if (this.mState == 0) {
            return ERROR_INVALID_OPERATION;
        }
        return native_set_marker_pos(markerInFrames);
    }

    public int setPositionNotificationPeriod(int periodInFrames) {
        if (this.mState == 0) {
            return ERROR_INVALID_OPERATION;
        }
        return native_set_pos_update_period(periodInFrames);
    }

    private static void postEventFromNative(Object audiorecord_ref, int what, int arg1, int arg2, Object obj) {
        AudioRecord recorder = (AudioRecord) ((WeakReference) audiorecord_ref).get();
        if (recorder != null && recorder.mEventHandler != null) {
            recorder.mEventHandler.sendMessage(recorder.mEventHandler.obtainMessage(what, arg1, arg2, obj));
        }
    }

    private static void logd(String msg) {
        Log.d(TAG, msg);
    }

    private static void loge(String msg) {
        Log.e(TAG, msg);
    }
}
