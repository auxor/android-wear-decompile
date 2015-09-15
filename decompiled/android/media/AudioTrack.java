package android.media;

import android.app.ActivityThread;
import android.media.AudioAttributes.Builder;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import com.android.internal.R;
import com.android.internal.app.IAppOpsService;
import com.android.internal.app.IAppOpsService.Stub;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.NioUtils;

public class AudioTrack {
    private static final int CHANNEL_COUNT_MAX = 8;
    public static final int ERROR = -1;
    public static final int ERROR_BAD_VALUE = -2;
    public static final int ERROR_INVALID_OPERATION = -3;
    private static final int ERROR_NATIVESETUP_AUDIOSYSTEM = -16;
    private static final int ERROR_NATIVESETUP_INVALIDCHANNELMASK = -17;
    private static final int ERROR_NATIVESETUP_INVALIDFORMAT = -18;
    private static final int ERROR_NATIVESETUP_INVALIDSTREAMTYPE = -19;
    private static final int ERROR_NATIVESETUP_NATIVEINITFAILED = -20;
    private static final float GAIN_MAX = 1.0f;
    private static final float GAIN_MIN = 0.0f;
    public static final int MODE_STATIC = 0;
    public static final int MODE_STREAM = 1;
    private static final int NATIVE_EVENT_MARKER = 3;
    private static final int NATIVE_EVENT_NEW_POS = 4;
    public static final int PLAYSTATE_PAUSED = 2;
    public static final int PLAYSTATE_PLAYING = 3;
    public static final int PLAYSTATE_STOPPED = 1;
    private static final int SAMPLE_RATE_HZ_MAX = 96000;
    private static final int SAMPLE_RATE_HZ_MIN = 4000;
    public static final int STATE_INITIALIZED = 1;
    public static final int STATE_NO_STATIC_DATA = 2;
    public static final int STATE_UNINITIALIZED = 0;
    public static final int SUCCESS = 0;
    private static final int SUPPORTED_OUT_CHANNELS = 7420;
    private static final String TAG = "android.media.AudioTrack";
    public static final int WRITE_BLOCKING = 0;
    public static final int WRITE_NON_BLOCKING = 1;
    private final IAppOpsService mAppOps;
    private final AudioAttributes mAttributes;
    private int mAudioFormat;
    private int mChannelConfiguration;
    private int mChannelCount;
    private int mChannels;
    private int mDataLoadMode;
    private NativeEventHandlerDelegate mEventHandlerDelegate;
    private final Looper mInitializationLooper;
    private long mJniData;
    private int mNativeBufferSizeInBytes;
    private int mNativeBufferSizeInFrames;
    private long mNativeTrackInJavaObj;
    private int mPlayState;
    private final Object mPlayStateLock;
    private int mSampleRate;
    private int mSessionId;
    private int mState;
    private int mStreamType;

    private class NativeEventHandlerDelegate {
        private final Handler mHandler;
        final /* synthetic */ AudioTrack this$0;

        NativeEventHandlerDelegate(android.media.AudioTrack r1, android.media.AudioTrack r2, android.media.AudioTrack.OnPlaybackPositionUpdateListener r3, android.os.Handler r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioTrack.NativeEventHandlerDelegate.<init>(android.media.AudioTrack, android.media.AudioTrack, android.media.AudioTrack$OnPlaybackPositionUpdateListener, android.os.Handler):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioTrack.NativeEventHandlerDelegate.<init>(android.media.AudioTrack, android.media.AudioTrack, android.media.AudioTrack$OnPlaybackPositionUpdateListener, android.os.Handler):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioTrack.NativeEventHandlerDelegate.<init>(android.media.AudioTrack, android.media.AudioTrack, android.media.AudioTrack$OnPlaybackPositionUpdateListener, android.os.Handler):void");
        }

        android.os.Handler getHandler() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioTrack.NativeEventHandlerDelegate.getHandler():android.os.Handler
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioTrack.NativeEventHandlerDelegate.getHandler():android.os.Handler
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioTrack.NativeEventHandlerDelegate.getHandler():android.os.Handler");
        }
    }

    private final native int native_attachAuxEffect(int i);

    private final native void native_finalize();

    private final native void native_flush();

    private final native int native_get_latency();

    private final native int native_get_marker_pos();

    private static final native int native_get_min_buff_size(int i, int i2, int i3);

    private final native int native_get_native_frame_count();

    private static final native int native_get_output_sample_rate(int i);

    private final native int native_get_playback_rate();

    private final native int native_get_pos_update_period();

    private final native int native_get_position();

    private final native int native_get_timestamp(long[] jArr);

    private final native void native_pause();

    private final native void native_release();

    private final native int native_reload_static();

    private final native int native_setAuxEffectSendLevel(float f);

    private final native void native_setVolume(float f, float f2);

    private final native int native_set_loop(int i, int i2, int i3);

    private final native int native_set_marker_pos(int i);

    private final native int native_set_playback_rate(int i);

    private final native int native_set_pos_update_period(int i);

    private final native int native_set_position(int i);

    private final native int native_setup(Object obj, Object obj2, int i, int i2, int i3, int i4, int i5, int[] iArr);

    private final native void native_start();

    private final native void native_stop();

    private final native int native_write_byte(byte[] bArr, int i, int i2, int i3, boolean z);

    private final native int native_write_float(float[] fArr, int i, int i2, int i3, boolean z);

    private final native int native_write_native_bytes(Object obj, int i, int i2, int i3, boolean z);

    private final native int native_write_short(short[] sArr, int i, int i2, int i3);

    public AudioTrack(int streamType, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes, int mode) throws IllegalArgumentException {
        this(streamType, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes, mode, WRITE_BLOCKING);
    }

    public AudioTrack(int streamType, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes, int mode, int sessionId) throws IllegalArgumentException {
        this(new Builder().setLegacyStreamType(streamType).build(), new AudioFormat.Builder().setChannelMask(channelConfig).setEncoding(audioFormat).setSampleRate(sampleRateInHz).build(), bufferSizeInBytes, mode, sessionId);
    }

    public AudioTrack(AudioAttributes attributes, AudioFormat format, int bufferSizeInBytes, int mode, int sessionId) throws IllegalArgumentException {
        this.mState = WRITE_BLOCKING;
        this.mPlayState = WRITE_NON_BLOCKING;
        this.mPlayStateLock = new Object();
        this.mNativeBufferSizeInBytes = WRITE_BLOCKING;
        this.mNativeBufferSizeInFrames = WRITE_BLOCKING;
        this.mChannelCount = WRITE_NON_BLOCKING;
        this.mChannels = NATIVE_EVENT_NEW_POS;
        this.mStreamType = PLAYSTATE_PLAYING;
        this.mDataLoadMode = WRITE_NON_BLOCKING;
        this.mChannelConfiguration = NATIVE_EVENT_NEW_POS;
        this.mAudioFormat = STATE_NO_STATIC_DATA;
        this.mSessionId = WRITE_BLOCKING;
        if (attributes == null) {
            throw new IllegalArgumentException("Illegal null AudioAttributes");
        } else if (format == null) {
            throw new IllegalArgumentException("Illegal null AudioFormat");
        } else {
            int rate;
            Looper looper = Looper.myLooper();
            if (looper == null) {
                looper = Looper.getMainLooper();
            }
            if ((format.getPropertySetMask() & STATE_NO_STATIC_DATA) != 0) {
                rate = format.getSampleRate();
            } else {
                rate = AudioSystem.getPrimaryOutputSamplingRate();
                if (rate <= 0) {
                    rate = 44100;
                }
            }
            int channelMask = 12;
            if ((format.getPropertySetMask() & NATIVE_EVENT_NEW_POS) != 0) {
                channelMask = format.getChannelMask();
            }
            int encoding = WRITE_NON_BLOCKING;
            if ((format.getPropertySetMask() & WRITE_NON_BLOCKING) != 0) {
                encoding = format.getEncoding();
            }
            audioParamCheck(rate, channelMask, encoding, mode);
            this.mStreamType = ERROR;
            audioBuffSizeCheck(bufferSizeInBytes);
            this.mInitializationLooper = looper;
            this.mAppOps = Stub.asInterface(ServiceManager.getService("appops"));
            this.mAttributes = new Builder(attributes).build();
            if (sessionId < 0) {
                throw new IllegalArgumentException("Invalid audio session ID: " + sessionId);
            }
            int[] session = new int[WRITE_NON_BLOCKING];
            session[WRITE_BLOCKING] = sessionId;
            int initResult = native_setup(new WeakReference(this), this.mAttributes, this.mSampleRate, this.mChannels, this.mAudioFormat, this.mNativeBufferSizeInBytes, this.mDataLoadMode, session);
            if (initResult != 0) {
                loge("Error code " + initResult + " when initializing AudioTrack.");
                return;
            }
            this.mSessionId = session[WRITE_BLOCKING];
            if (this.mDataLoadMode == 0) {
                this.mState = STATE_NO_STATIC_DATA;
            } else {
                this.mState = WRITE_NON_BLOCKING;
            }
        }
    }

    private void audioParamCheck(int sampleRateInHz, int channelConfig, int audioFormat, int mode) {
        if (sampleRateInHz < SAMPLE_RATE_HZ_MIN || sampleRateInHz > SAMPLE_RATE_HZ_MAX) {
            throw new IllegalArgumentException(sampleRateInHz + "Hz is not a supported sample rate.");
        }
        this.mSampleRate = sampleRateInHz;
        this.mChannelConfiguration = channelConfig;
        switch (channelConfig) {
            case WRITE_NON_BLOCKING /*1*/:
            case STATE_NO_STATIC_DATA /*2*/:
            case NATIVE_EVENT_NEW_POS /*4*/:
                this.mChannelCount = WRITE_NON_BLOCKING;
                this.mChannels = NATIVE_EVENT_NEW_POS;
                break;
            case PLAYSTATE_PLAYING /*3*/:
            case DnsServerRepository.NUM_SERVERS /*12*/:
                this.mChannelCount = STATE_NO_STATIC_DATA;
                this.mChannels = 12;
                break;
            default:
                if (isMultichannelConfigSupported(channelConfig)) {
                    this.mChannels = channelConfig;
                    this.mChannelCount = Integer.bitCount(channelConfig);
                    break;
                }
                throw new IllegalArgumentException("Unsupported channel configuration.");
        }
        if (audioFormat == WRITE_NON_BLOCKING) {
            audioFormat = STATE_NO_STATIC_DATA;
        }
        if (AudioFormat.isValidEncoding(audioFormat)) {
            this.mAudioFormat = audioFormat;
            if ((mode == WRITE_NON_BLOCKING || mode == 0) && (mode == WRITE_NON_BLOCKING || AudioFormat.isEncodingLinearPcm(this.mAudioFormat))) {
                this.mDataLoadMode = mode;
                return;
            }
            throw new IllegalArgumentException("Invalid mode.");
        }
        throw new IllegalArgumentException("Unsupported audio encoding.");
    }

    private static boolean isMultichannelConfigSupported(int channelConfig) {
        if ((channelConfig & SUPPORTED_OUT_CHANNELS) != channelConfig) {
            loge("Channel configuration features unsupported channels");
            return false;
        }
        int channelCount = Integer.bitCount(channelConfig);
        if (channelCount > CHANNEL_COUNT_MAX) {
            loge("Channel configuration contains too many channels " + channelCount + ">" + CHANNEL_COUNT_MAX);
            return false;
        } else if ((channelConfig & 12) != 12) {
            loge("Front channels must be present in multichannel configurations");
            return false;
        } else if ((channelConfig & R.styleable.Theme_actionModeSelectAllDrawable) != 0 && (channelConfig & R.styleable.Theme_actionModeSelectAllDrawable) != R.styleable.Theme_actionModeSelectAllDrawable) {
            loge("Rear channels can't be used independently");
            return false;
        } else if ((channelConfig & 6144) == 0 || (channelConfig & 6144) == 6144) {
            return true;
        } else {
            loge("Side channels can't be used independently");
            return false;
        }
    }

    private void audioBuffSizeCheck(int audioBufferSize) {
        int frameSizeInBytes;
        if (AudioFormat.isEncodingLinearPcm(this.mAudioFormat)) {
            frameSizeInBytes = this.mChannelCount * AudioFormat.getBytesPerSample(this.mAudioFormat);
        } else {
            frameSizeInBytes = WRITE_NON_BLOCKING;
        }
        if (audioBufferSize % frameSizeInBytes != 0 || audioBufferSize < WRITE_NON_BLOCKING) {
            throw new IllegalArgumentException("Invalid audio buffer size.");
        }
        this.mNativeBufferSizeInBytes = audioBufferSize;
        this.mNativeBufferSizeInFrames = audioBufferSize / frameSizeInBytes;
    }

    public void release() {
        try {
            stop();
        } catch (IllegalStateException e) {
        }
        native_release();
        this.mState = WRITE_BLOCKING;
    }

    protected void finalize() {
        native_finalize();
    }

    public static float getMinVolume() {
        return GAIN_MIN;
    }

    public static float getMaxVolume() {
        return GAIN_MAX;
    }

    public int getSampleRate() {
        return this.mSampleRate;
    }

    public int getPlaybackRate() {
        return native_get_playback_rate();
    }

    public int getAudioFormat() {
        return this.mAudioFormat;
    }

    public int getStreamType() {
        return this.mStreamType;
    }

    public int getChannelConfiguration() {
        return this.mChannelConfiguration;
    }

    public int getChannelCount() {
        return this.mChannelCount;
    }

    public int getState() {
        return this.mState;
    }

    public int getPlayState() {
        int i;
        synchronized (this.mPlayStateLock) {
            i = this.mPlayState;
        }
        return i;
    }

    @Deprecated
    protected int getNativeFrameCount() {
        return native_get_native_frame_count();
    }

    public int getNotificationMarkerPosition() {
        return native_get_marker_pos();
    }

    public int getPositionNotificationPeriod() {
        return native_get_pos_update_period();
    }

    public int getPlaybackHeadPosition() {
        return native_get_position();
    }

    public int getLatency() {
        return native_get_latency();
    }

    public static int getNativeOutputSampleRate(int streamType) {
        return native_get_output_sample_rate(streamType);
    }

    public static int getMinBufferSize(int sampleRateInHz, int channelConfig, int audioFormat) {
        int channelCount;
        switch (channelConfig) {
            case STATE_NO_STATIC_DATA /*2*/:
            case NATIVE_EVENT_NEW_POS /*4*/:
                channelCount = WRITE_NON_BLOCKING;
                break;
            case PLAYSTATE_PLAYING /*3*/:
            case DnsServerRepository.NUM_SERVERS /*12*/:
                channelCount = STATE_NO_STATIC_DATA;
                break;
            default:
                if ((channelConfig & SUPPORTED_OUT_CHANNELS) == channelConfig) {
                    channelCount = Integer.bitCount(channelConfig);
                    break;
                }
                loge("getMinBufferSize(): Invalid channel configuration.");
                return ERROR_BAD_VALUE;
        }
        if (!AudioFormat.isValidEncoding(audioFormat)) {
            loge("getMinBufferSize(): Invalid audio format.");
            return ERROR_BAD_VALUE;
        } else if (sampleRateInHz < SAMPLE_RATE_HZ_MIN || sampleRateInHz > SAMPLE_RATE_HZ_MAX) {
            loge("getMinBufferSize(): " + sampleRateInHz + " Hz is not a supported sample rate.");
            return ERROR_BAD_VALUE;
        } else {
            int size = native_get_min_buff_size(sampleRateInHz, channelCount, audioFormat);
            if (size > 0) {
                return size;
            }
            loge("getMinBufferSize(): error querying hardware");
            return ERROR;
        }
    }

    public int getAudioSessionId() {
        return this.mSessionId;
    }

    public boolean getTimestamp(AudioTimestamp timestamp) {
        if (timestamp == null) {
            throw new IllegalArgumentException();
        }
        long[] longArray = new long[STATE_NO_STATIC_DATA];
        if (native_get_timestamp(longArray) != 0) {
            return false;
        }
        timestamp.framePosition = longArray[WRITE_BLOCKING];
        timestamp.nanoTime = longArray[WRITE_NON_BLOCKING];
        return true;
    }

    public void setPlaybackPositionUpdateListener(OnPlaybackPositionUpdateListener listener) {
        setPlaybackPositionUpdateListener(listener, null);
    }

    public void setPlaybackPositionUpdateListener(OnPlaybackPositionUpdateListener listener, Handler handler) {
        if (listener != null) {
            this.mEventHandlerDelegate = new NativeEventHandlerDelegate(this, this, listener, handler);
        } else {
            this.mEventHandlerDelegate = null;
        }
    }

    private static float clampGainOrLevel(float gainOrLevel) {
        if (Float.isNaN(gainOrLevel)) {
            throw new IllegalArgumentException();
        } else if (gainOrLevel < GAIN_MIN) {
            return GAIN_MIN;
        } else {
            if (gainOrLevel > GAIN_MAX) {
                return GAIN_MAX;
            }
            return gainOrLevel;
        }
    }

    public int setStereoVolume(float leftGain, float rightGain) {
        if (isRestricted()) {
            return WRITE_BLOCKING;
        }
        if (this.mState == 0) {
            return ERROR_INVALID_OPERATION;
        }
        native_setVolume(clampGainOrLevel(leftGain), clampGainOrLevel(rightGain));
        return WRITE_BLOCKING;
    }

    public int setVolume(float gain) {
        return setStereoVolume(gain, gain);
    }

    public int setPlaybackRate(int sampleRateInHz) {
        if (this.mState != WRITE_NON_BLOCKING) {
            return ERROR_INVALID_OPERATION;
        }
        if (sampleRateInHz <= 0) {
            return ERROR_BAD_VALUE;
        }
        return native_set_playback_rate(sampleRateInHz);
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

    public int setPlaybackHeadPosition(int positionInFrames) {
        if (this.mDataLoadMode == WRITE_NON_BLOCKING || this.mState == 0 || getPlayState() == PLAYSTATE_PLAYING) {
            return ERROR_INVALID_OPERATION;
        }
        if (positionInFrames < 0 || positionInFrames > this.mNativeBufferSizeInFrames) {
            return ERROR_BAD_VALUE;
        }
        return native_set_position(positionInFrames);
    }

    public int setLoopPoints(int startInFrames, int endInFrames, int loopCount) {
        if (this.mDataLoadMode == WRITE_NON_BLOCKING || this.mState == 0 || getPlayState() == PLAYSTATE_PLAYING) {
            return ERROR_INVALID_OPERATION;
        }
        if (loopCount != 0 && (startInFrames < 0 || startInFrames >= this.mNativeBufferSizeInFrames || startInFrames >= endInFrames || endInFrames > this.mNativeBufferSizeInFrames)) {
            return ERROR_BAD_VALUE;
        }
        return native_set_loop(startInFrames, endInFrames, loopCount);
    }

    @Deprecated
    protected void setState(int state) {
        this.mState = state;
    }

    public void play() throws IllegalStateException {
        if (this.mState != WRITE_NON_BLOCKING) {
            throw new IllegalStateException("play() called on uninitialized AudioTrack.");
        }
        if (isRestricted()) {
            setVolume(GAIN_MIN);
        }
        synchronized (this.mPlayStateLock) {
            native_start();
            this.mPlayState = PLAYSTATE_PLAYING;
        }
    }

    private boolean isRestricted() {
        try {
            if (this.mAppOps.checkAudioOperation(28, AudioAttributes.usageForLegacyStreamType(this.mStreamType), Process.myUid(), ActivityThread.currentPackageName()) != 0) {
                return true;
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    public void stop() throws IllegalStateException {
        if (this.mState != WRITE_NON_BLOCKING) {
            throw new IllegalStateException("stop() called on uninitialized AudioTrack.");
        }
        synchronized (this.mPlayStateLock) {
            native_stop();
            this.mPlayState = WRITE_NON_BLOCKING;
        }
    }

    public void pause() throws IllegalStateException {
        if (this.mState != WRITE_NON_BLOCKING) {
            throw new IllegalStateException("pause() called on uninitialized AudioTrack.");
        }
        synchronized (this.mPlayStateLock) {
            native_pause();
            this.mPlayState = STATE_NO_STATIC_DATA;
        }
    }

    public void flush() {
        if (this.mState == WRITE_NON_BLOCKING) {
            native_flush();
        }
    }

    public int write(byte[] audioData, int offsetInBytes, int sizeInBytes) {
        if (this.mState == 0 || this.mAudioFormat == NATIVE_EVENT_NEW_POS) {
            return ERROR_INVALID_OPERATION;
        }
        if (audioData == null || offsetInBytes < 0 || sizeInBytes < 0 || offsetInBytes + sizeInBytes < 0 || offsetInBytes + sizeInBytes > audioData.length) {
            return ERROR_BAD_VALUE;
        }
        int ret = native_write_byte(audioData, offsetInBytes, sizeInBytes, this.mAudioFormat, true);
        if (this.mDataLoadMode != 0 || this.mState != STATE_NO_STATIC_DATA || ret <= 0) {
            return ret;
        }
        this.mState = WRITE_NON_BLOCKING;
        return ret;
    }

    public int write(short[] audioData, int offsetInShorts, int sizeInShorts) {
        if (this.mState == 0 || this.mAudioFormat == NATIVE_EVENT_NEW_POS) {
            return ERROR_INVALID_OPERATION;
        }
        if (audioData == null || offsetInShorts < 0 || sizeInShorts < 0 || offsetInShorts + sizeInShorts < 0 || offsetInShorts + sizeInShorts > audioData.length) {
            return ERROR_BAD_VALUE;
        }
        int ret = native_write_short(audioData, offsetInShorts, sizeInShorts, this.mAudioFormat);
        if (this.mDataLoadMode != 0 || this.mState != STATE_NO_STATIC_DATA || ret <= 0) {
            return ret;
        }
        this.mState = WRITE_NON_BLOCKING;
        return ret;
    }

    public int write(float[] audioData, int offsetInFloats, int sizeInFloats, int writeMode) {
        if (this.mState == 0) {
            Log.e(TAG, "AudioTrack.write() called in invalid state STATE_UNINITIALIZED");
            return ERROR_INVALID_OPERATION;
        } else if (this.mAudioFormat != NATIVE_EVENT_NEW_POS) {
            Log.e(TAG, "AudioTrack.write(float[] ...) requires format ENCODING_PCM_FLOAT");
            return ERROR_INVALID_OPERATION;
        } else if (writeMode != 0 && writeMode != WRITE_NON_BLOCKING) {
            Log.e(TAG, "AudioTrack.write() called with invalid blocking mode");
            return ERROR_BAD_VALUE;
        } else if (audioData == null || offsetInFloats < 0 || sizeInFloats < 0 || offsetInFloats + sizeInFloats < 0 || offsetInFloats + sizeInFloats > audioData.length) {
            Log.e(TAG, "AudioTrack.write() called with invalid array, offset, or size");
            return ERROR_BAD_VALUE;
        } else {
            int ret = native_write_float(audioData, offsetInFloats, sizeInFloats, this.mAudioFormat, writeMode == 0);
            if (this.mDataLoadMode != 0 || this.mState != STATE_NO_STATIC_DATA || ret <= 0) {
                return ret;
            }
            this.mState = WRITE_NON_BLOCKING;
            return ret;
        }
    }

    public int write(ByteBuffer audioData, int sizeInBytes, int writeMode) {
        boolean z = false;
        if (this.mState == 0) {
            Log.e(TAG, "AudioTrack.write() called in invalid state STATE_UNINITIALIZED");
            return ERROR_INVALID_OPERATION;
        } else if (writeMode != 0 && writeMode != WRITE_NON_BLOCKING) {
            Log.e(TAG, "AudioTrack.write() called with invalid blocking mode");
            return ERROR_BAD_VALUE;
        } else if (audioData == null || sizeInBytes < 0 || sizeInBytes > audioData.remaining()) {
            Log.e(TAG, "AudioTrack.write() called with invalid size (" + sizeInBytes + ") value");
            return ERROR_BAD_VALUE;
        } else {
            int ret;
            int position;
            int i;
            if (audioData.isDirect()) {
                position = audioData.position();
                i = this.mAudioFormat;
                if (writeMode == 0) {
                    z = true;
                }
                ret = native_write_native_bytes(audioData, position, sizeInBytes, i, z);
            } else {
                byte[] unsafeArray = NioUtils.unsafeArray(audioData);
                position = audioData.position() + NioUtils.unsafeArrayOffset(audioData);
                i = this.mAudioFormat;
                if (writeMode == 0) {
                    z = true;
                }
                ret = native_write_byte(unsafeArray, position, sizeInBytes, i, z);
            }
            if (this.mDataLoadMode == 0 && this.mState == STATE_NO_STATIC_DATA && ret > 0) {
                this.mState = WRITE_NON_BLOCKING;
            }
            if (ret <= 0) {
                return ret;
            }
            audioData.position(audioData.position() + ret);
            return ret;
        }
    }

    public int reloadStaticData() {
        if (this.mDataLoadMode == WRITE_NON_BLOCKING || this.mState != WRITE_NON_BLOCKING) {
            return ERROR_INVALID_OPERATION;
        }
        return native_reload_static();
    }

    public int attachAuxEffect(int effectId) {
        if (this.mState == 0) {
            return ERROR_INVALID_OPERATION;
        }
        return native_attachAuxEffect(effectId);
    }

    public int setAuxEffectSendLevel(float level) {
        if (isRestricted()) {
            return WRITE_BLOCKING;
        }
        if (this.mState == 0) {
            return ERROR_INVALID_OPERATION;
        }
        if (native_setAuxEffectSendLevel(clampGainOrLevel(level)) != 0) {
            return ERROR;
        }
        return WRITE_BLOCKING;
    }

    private static void postEventFromNative(Object audiotrack_ref, int what, int arg1, int arg2, Object obj) {
        AudioTrack track = (AudioTrack) ((WeakReference) audiotrack_ref).get();
        if (track != null) {
            NativeEventHandlerDelegate delegate = track.mEventHandlerDelegate;
            if (delegate != null) {
                Handler handler = delegate.getHandler();
                if (handler != null) {
                    handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
                }
            }
        }
    }

    private static void logd(String msg) {
        Log.d(TAG, msg);
    }

    private static void loge(String msg) {
        Log.e(TAG, msg);
    }
}
