package android.media;

import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes.Builder;
import android.media.IAudioFocusDispatcher.Stub;
import android.media.audiopolicy.AudioPolicy;
import android.media.session.MediaSessionLegacyHelper;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.provider.Settings.System;
import android.util.Log;
import android.view.KeyEvent;
import com.android.internal.R;
import com.android.internal.telephony.RILConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class AudioManager {
    public static final String ACTION_ANALOG_AUDIO_DOCK_PLUG = "android.media.action.ANALOG_AUDIO_DOCK_PLUG";
    public static final String ACTION_AUDIO_BECOMING_NOISY = "android.media.AUDIO_BECOMING_NOISY";
    public static final String ACTION_DIGITAL_AUDIO_DOCK_PLUG = "android.media.action.DIGITAL_AUDIO_DOCK_PLUG";
    public static final String ACTION_HDMI_AUDIO_PLUG = "android.media.action.HDMI_AUDIO_PLUG";
    public static final String ACTION_HEADSET_PLUG = "android.intent.action.HEADSET_PLUG";
    @Deprecated
    public static final String ACTION_SCO_AUDIO_STATE_CHANGED = "android.media.SCO_AUDIO_STATE_CHANGED";
    public static final String ACTION_SCO_AUDIO_STATE_UPDATED = "android.media.ACTION_SCO_AUDIO_STATE_UPDATED";
    public static final String ACTION_USB_AUDIO_ACCESSORY_PLUG = "android.media.action.USB_AUDIO_ACCESSORY_PLUG";
    public static final String ACTION_USB_AUDIO_DEVICE_PLUG = "android.media.action.USB_AUDIO_DEVICE_PLUG";
    public static final int ADJUST_LOWER = -1;
    public static final int ADJUST_RAISE = 1;
    public static final int ADJUST_SAME = 0;
    public static final int AUDIOFOCUS_FLAGS_APPS = 3;
    public static final int AUDIOFOCUS_FLAGS_SYSTEM = 7;
    public static final int AUDIOFOCUS_FLAG_DELAY_OK = 1;
    public static final int AUDIOFOCUS_FLAG_LOCK = 4;
    public static final int AUDIOFOCUS_FLAG_PAUSES_ON_DUCKABLE_LOSS = 2;
    public static final int AUDIOFOCUS_GAIN = 1;
    public static final int AUDIOFOCUS_GAIN_TRANSIENT = 2;
    public static final int AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE = 4;
    public static final int AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK = 3;
    public static final int AUDIOFOCUS_LOSS = -1;
    public static final int AUDIOFOCUS_LOSS_TRANSIENT = -2;
    public static final int AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK = -3;
    public static final int AUDIOFOCUS_NONE = 0;
    public static final int AUDIOFOCUS_REQUEST_DELAYED = 2;
    public static final int AUDIOFOCUS_REQUEST_FAILED = 0;
    public static final int AUDIOFOCUS_REQUEST_GRANTED = 1;
    static final int AUDIOPORT_GENERATION_INIT = 0;
    public static final int AUDIO_SESSION_ID_GENERATE = 0;
    public static final int DEVICE_IN_ANLG_DOCK_HEADSET = -2147483136;
    public static final int DEVICE_IN_BACK_MIC = -2147483520;
    public static final int DEVICE_IN_BLUETOOTH_SCO_HEADSET = -2147483640;
    public static final int DEVICE_IN_BUILTIN_MIC = -2147483644;
    public static final int DEVICE_IN_DGTL_DOCK_HEADSET = -2147482624;
    public static final int DEVICE_IN_FM_TUNER = -2147475456;
    public static final int DEVICE_IN_HDMI = -2147483616;
    public static final int DEVICE_IN_LINE = -2147450880;
    public static final int DEVICE_IN_LOOPBACK = -2147221504;
    public static final int DEVICE_IN_SPDIF = -2147418112;
    public static final int DEVICE_IN_TELEPHONY_RX = -2147483584;
    public static final int DEVICE_IN_TV_TUNER = -2147467264;
    public static final int DEVICE_IN_USB_ACCESSORY = -2147481600;
    public static final int DEVICE_IN_USB_DEVICE = -2147479552;
    public static final int DEVICE_IN_WIRED_HEADSET = -2147483632;
    public static final int DEVICE_NONE = 0;
    public static final int DEVICE_OUT_ANLG_DOCK_HEADSET = 2048;
    public static final int DEVICE_OUT_AUX_DIGITAL = 1024;
    public static final int DEVICE_OUT_BLUETOOTH_A2DP = 128;
    public static final int DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES = 256;
    public static final int DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER = 512;
    public static final int DEVICE_OUT_BLUETOOTH_SCO = 16;
    public static final int DEVICE_OUT_BLUETOOTH_SCO_CARKIT = 64;
    public static final int DEVICE_OUT_BLUETOOTH_SCO_HEADSET = 32;
    public static final int DEVICE_OUT_DEFAULT = 1073741824;
    public static final int DEVICE_OUT_DGTL_DOCK_HEADSET = 4096;
    public static final int DEVICE_OUT_EARPIECE = 1;
    public static final int DEVICE_OUT_FM = 1048576;
    public static final int DEVICE_OUT_HDMI = 1024;
    public static final int DEVICE_OUT_HDMI_ARC = 262144;
    public static final int DEVICE_OUT_LINE = 131072;
    public static final int DEVICE_OUT_REMOTE_SUBMIX = 32768;
    public static final int DEVICE_OUT_SPDIF = 524288;
    public static final int DEVICE_OUT_SPEAKER = 2;
    public static final int DEVICE_OUT_TELEPHONY_TX = 65536;
    public static final int DEVICE_OUT_USB_ACCESSORY = 8192;
    public static final int DEVICE_OUT_USB_DEVICE = 16384;
    public static final int DEVICE_OUT_WIRED_HEADPHONE = 8;
    public static final int DEVICE_OUT_WIRED_HEADSET = 4;
    public static final int ERROR = -1;
    public static final int ERROR_BAD_VALUE = -2;
    public static final int ERROR_DEAD_OBJECT = -6;
    public static final int ERROR_INVALID_OPERATION = -3;
    public static final int ERROR_NO_INIT = -5;
    public static final int ERROR_PERMISSION_DENIED = -4;
    public static final String EXTRA_AUDIO_PLUG_STATE = "android.media.extra.AUDIO_PLUG_STATE";
    public static final String EXTRA_ENCODINGS = "android.media.extra.ENCODINGS";
    public static final String EXTRA_MASTER_VOLUME_MUTED = "android.media.EXTRA_MASTER_VOLUME_MUTED";
    public static final String EXTRA_MASTER_VOLUME_VALUE = "android.media.EXTRA_MASTER_VOLUME_VALUE";
    public static final String EXTRA_MAX_CHANNEL_COUNT = "android.media.extra.MAX_CHANNEL_COUNT";
    public static final String EXTRA_PREV_MASTER_VOLUME_VALUE = "android.media.EXTRA_PREV_MASTER_VOLUME_VALUE";
    public static final String EXTRA_PREV_VOLUME_STREAM_VALUE = "android.media.EXTRA_PREV_VOLUME_STREAM_VALUE";
    public static final String EXTRA_RINGER_MODE = "android.media.EXTRA_RINGER_MODE";
    public static final String EXTRA_SCO_AUDIO_PREVIOUS_STATE = "android.media.extra.SCO_AUDIO_PREVIOUS_STATE";
    public static final String EXTRA_SCO_AUDIO_STATE = "android.media.extra.SCO_AUDIO_STATE";
    public static final String EXTRA_STREAM_VOLUME_MUTED = "android.media.EXTRA_STREAM_VOLUME_MUTED";
    public static final String EXTRA_VIBRATE_SETTING = "android.media.EXTRA_VIBRATE_SETTING";
    public static final String EXTRA_VIBRATE_TYPE = "android.media.EXTRA_VIBRATE_TYPE";
    public static final String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";
    public static final String EXTRA_VOLUME_STREAM_VALUE = "android.media.EXTRA_VOLUME_STREAM_VALUE";
    public static final int FLAG_ACTIVE_MEDIA_ONLY = 512;
    public static final int FLAG_ALLOW_RINGER_MODES = 2;
    public static final int FLAG_BLUETOOTH_ABS_VOLUME = 64;
    public static final int FLAG_FIXED_VOLUME = 32;
    public static final int FLAG_HDMI_SYSTEM_AUDIO_VOLUME = 256;
    private static final String[] FLAG_NAMES;
    public static final int FLAG_PLAY_SOUND = 4;
    public static final int FLAG_REMOVE_SOUND_AND_VIBRATE = 8;
    public static final int FLAG_SHOW_SILENT_HINT = 128;
    public static final int FLAG_SHOW_UI = 1;
    public static final int FLAG_SHOW_UI_WARNINGS = 1024;
    public static final int FLAG_SHOW_VIBRATE_HINT = 2048;
    public static final int FLAG_VIBRATE = 16;
    public static final int FX_FOCUS_NAVIGATION_DOWN = 2;
    public static final int FX_FOCUS_NAVIGATION_LEFT = 3;
    public static final int FX_FOCUS_NAVIGATION_RIGHT = 4;
    public static final int FX_FOCUS_NAVIGATION_UP = 1;
    public static final int FX_KEYPRESS_DELETE = 7;
    public static final int FX_KEYPRESS_INVALID = 9;
    public static final int FX_KEYPRESS_RETURN = 8;
    public static final int FX_KEYPRESS_SPACEBAR = 6;
    public static final int FX_KEYPRESS_STANDARD = 5;
    public static final int FX_KEY_CLICK = 0;
    public static final String INTERNAL_RINGER_MODE_CHANGED_ACTION = "android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION";
    public static final String MASTER_MUTE_CHANGED_ACTION = "android.media.MASTER_MUTE_CHANGED_ACTION";
    public static final String MASTER_VOLUME_CHANGED_ACTION = "android.media.MASTER_VOLUME_CHANGED_ACTION";
    public static final int MODE_CURRENT = -1;
    public static final int MODE_INVALID = -2;
    public static final int MODE_IN_CALL = 2;
    public static final int MODE_IN_COMMUNICATION = 3;
    public static final int MODE_NORMAL = 0;
    public static final int MODE_RINGTONE = 1;
    public static final int NUM_SOUND_EFFECTS = 10;
    @Deprecated
    public static final int NUM_STREAMS = 5;
    public static final String PROPERTY_OUTPUT_FRAMES_PER_BUFFER = "android.media.property.OUTPUT_FRAMES_PER_BUFFER";
    public static final String PROPERTY_OUTPUT_SAMPLE_RATE = "android.media.property.OUTPUT_SAMPLE_RATE";
    public static final String RINGER_MODE_CHANGED_ACTION = "android.media.RINGER_MODE_CHANGED";
    public static final int RINGER_MODE_MAX = 2;
    public static final int RINGER_MODE_NORMAL = 2;
    public static final int RINGER_MODE_SILENT = 0;
    public static final int RINGER_MODE_VIBRATE = 1;
    @Deprecated
    public static final int ROUTE_ALL = -1;
    @Deprecated
    public static final int ROUTE_BLUETOOTH = 4;
    @Deprecated
    public static final int ROUTE_BLUETOOTH_A2DP = 16;
    @Deprecated
    public static final int ROUTE_BLUETOOTH_SCO = 4;
    @Deprecated
    public static final int ROUTE_EARPIECE = 1;
    @Deprecated
    public static final int ROUTE_HEADSET = 8;
    @Deprecated
    public static final int ROUTE_SPEAKER = 2;
    public static final int SCO_AUDIO_STATE_CONNECTED = 1;
    public static final int SCO_AUDIO_STATE_CONNECTING = 2;
    public static final int SCO_AUDIO_STATE_DISCONNECTED = 0;
    public static final int SCO_AUDIO_STATE_ERROR = -1;
    public static final int STREAM_ALARM = 4;
    public static final int STREAM_BLUETOOTH_SCO = 6;
    public static final int STREAM_DTMF = 8;
    public static final int STREAM_MUSIC = 3;
    public static final String STREAM_MUTE_CHANGED_ACTION = "android.media.STREAM_MUTE_CHANGED_ACTION";
    public static final int STREAM_NOTIFICATION = 5;
    public static final int STREAM_RING = 2;
    public static final int STREAM_SYSTEM = 1;
    public static final int STREAM_SYSTEM_ENFORCED = 7;
    public static final int STREAM_TTS = 9;
    public static final int STREAM_VOICE_CALL = 0;
    public static final int SUCCESS = 0;
    private static String TAG = null;
    public static final int USE_DEFAULT_STREAM_TYPE = Integer.MIN_VALUE;
    public static final String VIBRATE_SETTING_CHANGED_ACTION = "android.media.VIBRATE_SETTING_CHANGED";
    public static final int VIBRATE_SETTING_OFF = 0;
    public static final int VIBRATE_SETTING_ON = 1;
    public static final int VIBRATE_SETTING_ONLY_SILENT = 2;
    public static final int VIBRATE_TYPE_NOTIFICATION = 1;
    public static final int VIBRATE_TYPE_RINGER = 0;
    public static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";
    static ArrayList<AudioPatch> sAudioPatchesCached;
    private static final AudioPortEventHandler sAudioPortEventHandler;
    static Integer sAudioPortGeneration;
    static ArrayList<AudioPort> sAudioPortsCached;
    private static IAudioService sService;
    private final IAudioFocusDispatcher mAudioFocusDispatcher;
    private final FocusEventHandlerDelegate mAudioFocusEventHandlerDelegate;
    private final HashMap<String, OnAudioFocusChangeListener> mAudioFocusIdListenerMap;
    private final Context mContext;
    private final Object mFocusListenerLock;
    private final IBinder mICallBack;
    private final Binder mToken;
    private final boolean mUseFixedVolume;
    private final boolean mUseMasterVolume;
    private final boolean mUseVolumeKeySounds;
    private long mVolumeKeyUpTime;

    private class FocusEventHandlerDelegate {
        private final Handler mHandler;

        /* renamed from: android.media.AudioManager.FocusEventHandlerDelegate.1 */
        class AnonymousClass1 extends Handler {
            final /* synthetic */ AudioManager val$this$0;

            AnonymousClass1(Looper x0, AudioManager audioManager) {
                this.val$this$0 = audioManager;
                super(x0);
            }

            public void handleMessage(Message msg) {
                synchronized (AudioManager.this.mFocusListenerLock) {
                    OnAudioFocusChangeListener listener = AudioManager.this.findFocusListener((String) msg.obj);
                }
                if (listener != null) {
                    Log.d(AudioManager.TAG, "AudioManager dispatching onAudioFocusChange(" + msg.what + ") for " + msg.obj);
                    listener.onAudioFocusChange(msg.what);
                }
            }
        }

        FocusEventHandlerDelegate() {
            Looper looper = Looper.myLooper();
            if (looper == null) {
                looper = Looper.getMainLooper();
            }
            if (looper != null) {
                this.mHandler = new AnonymousClass1(looper, AudioManager.this);
            } else {
                this.mHandler = null;
            }
        }

        Handler getHandler() {
            return this.mHandler;
        }
    }

    public interface OnAudioFocusChangeListener {
        void onAudioFocusChange(int i);
    }

    public interface OnAudioPortUpdateListener {
        void onAudioPatchListUpdate(AudioPatch[] audioPatchArr);

        void onAudioPortListUpdate(AudioPort[] audioPortArr);

        void onServiceDied();
    }

    static {
        TAG = "AudioManager";
        sAudioPortEventHandler = new AudioPortEventHandler();
        FLAG_NAMES = new String[]{"FLAG_SHOW_UI", "FLAG_ALLOW_RINGER_MODES", "FLAG_PLAY_SOUND", "FLAG_REMOVE_SOUND_AND_VIBRATE", "FLAG_VIBRATE", "FLAG_FIXED_VOLUME", "FLAG_BLUETOOTH_ABS_VOLUME", "FLAG_SHOW_SILENT_HINT", "FLAG_HDMI_SYSTEM_AUDIO_VOLUME", "FLAG_ACTIVE_MEDIA_ONLY", "FLAG_SHOW_UI_WARNINGS", "FLAG_SHOW_VIBRATE_HINT"};
        sAudioPortGeneration = new Integer(VIBRATE_TYPE_RINGER);
        sAudioPortsCached = new ArrayList();
        sAudioPatchesCached = new ArrayList();
    }

    public static String flagsToString(int flags) {
        StringBuilder sb = new StringBuilder();
        for (int i = VIBRATE_TYPE_RINGER; i < FLAG_NAMES.length; i += VIBRATE_TYPE_NOTIFICATION) {
            int flag = VIBRATE_TYPE_NOTIFICATION << i;
            if ((flags & flag) != 0) {
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append(FLAG_NAMES[i]);
                flags &= flag ^ SCO_AUDIO_STATE_ERROR;
            }
        }
        if (flags != 0) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(flags);
        }
        return sb.toString();
    }

    public AudioManager(Context context) {
        this.mToken = new Binder();
        this.mAudioFocusIdListenerMap = new HashMap();
        this.mFocusListenerLock = new Object();
        this.mAudioFocusEventHandlerDelegate = new FocusEventHandlerDelegate();
        this.mAudioFocusDispatcher = new Stub() {
            public void dispatchAudioFocusChange(int focusChange, String id) {
                AudioManager.this.mAudioFocusEventHandlerDelegate.getHandler().sendMessage(AudioManager.this.mAudioFocusEventHandlerDelegate.getHandler().obtainMessage(focusChange, id));
            }
        };
        this.mICallBack = new Binder();
        this.mContext = context;
        this.mUseMasterVolume = this.mContext.getResources().getBoolean(R.bool.config_useMasterVolume);
        this.mUseVolumeKeySounds = this.mContext.getResources().getBoolean(R.bool.config_useVolumeKeySounds);
        this.mUseFixedVolume = this.mContext.getResources().getBoolean(R.bool.config_useFixedVolume);
        sAudioPortEventHandler.init();
    }

    private static IAudioService getService() {
        if (sService != null) {
            return sService;
        }
        sService = IAudioService$Stub.asInterface(ServiceManager.getService("audio"));
        return sService;
    }

    public void dispatchMediaKeyEvent(KeyEvent keyEvent) {
        MediaSessionLegacyHelper.getHelper(this.mContext).sendMediaButtonEvent(keyEvent, false);
    }

    public void preDispatchKeyEvent(KeyEvent event, int stream) {
        int keyCode = event.getKeyCode();
        if (keyCode != 25 && keyCode != 24 && keyCode != R.styleable.Theme_borderlessButtonStyle && this.mVolumeKeyUpTime + 300 > SystemClock.uptimeMillis()) {
            if (this.mUseMasterVolume) {
                adjustMasterVolume(VIBRATE_TYPE_RINGER, STREAM_DTMF);
            } else {
                adjustSuggestedStreamVolume(VIBRATE_TYPE_RINGER, stream, STREAM_DTMF);
            }
        }
    }

    public void handleKeyDown(KeyEvent event, int stream) {
        int i = VIBRATE_TYPE_NOTIFICATION;
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case RILConstants.SS_MODIFIED_TO_DIAL /*24*/:
            case RILConstants.SS_MODIFIED_TO_USSD /*25*/:
                if (this.mUseMasterVolume) {
                    if (keyCode != 24) {
                        i = SCO_AUDIO_STATE_ERROR;
                    }
                    adjustMasterVolume(i, 17);
                    return;
                }
                if (keyCode != 24) {
                    i = SCO_AUDIO_STATE_ERROR;
                }
                adjustSuggestedStreamVolume(i, stream, 17);
            case R.styleable.Theme_borderlessButtonStyle /*164*/:
                if (event.getRepeatCount() == 0) {
                    MediaSessionLegacyHelper.getHelper(this.mContext).sendVolumeKeyEvent(event, false);
                }
            default:
        }
    }

    public void handleKeyUp(KeyEvent event, int stream) {
        switch (event.getKeyCode()) {
            case RILConstants.SS_MODIFIED_TO_DIAL /*24*/:
            case RILConstants.SS_MODIFIED_TO_USSD /*25*/:
                if (this.mUseVolumeKeySounds) {
                    if (this.mUseMasterVolume) {
                        adjustMasterVolume(VIBRATE_TYPE_RINGER, STREAM_ALARM);
                    } else {
                        adjustSuggestedStreamVolume(VIBRATE_TYPE_RINGER, stream, STREAM_ALARM);
                    }
                }
                this.mVolumeKeyUpTime = SystemClock.uptimeMillis();
            case R.styleable.Theme_borderlessButtonStyle /*164*/:
                MediaSessionLegacyHelper.getHelper(this.mContext).sendVolumeKeyEvent(event, false);
            default:
        }
    }

    public boolean isVolumeFixed() {
        return this.mUseFixedVolume;
    }

    public void adjustStreamVolume(int streamType, int direction, int flags) {
        IAudioService service = getService();
        try {
            if (this.mUseMasterVolume) {
                service.adjustMasterVolume(direction, flags, this.mContext.getOpPackageName());
            } else {
                service.adjustStreamVolume(streamType, direction, flags, this.mContext.getOpPackageName());
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in adjustStreamVolume", e);
        }
    }

    public void adjustVolume(int direction, int flags) {
        IAudioService service = getService();
        try {
            if (this.mUseMasterVolume) {
                service.adjustMasterVolume(direction, flags, this.mContext.getOpPackageName());
            } else {
                MediaSessionLegacyHelper.getHelper(this.mContext).sendAdjustVolumeBy(USE_DEFAULT_STREAM_TYPE, direction, flags);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in adjustVolume", e);
        }
    }

    public void adjustSuggestedStreamVolume(int direction, int suggestedStreamType, int flags) {
        IAudioService service = getService();
        try {
            if (this.mUseMasterVolume) {
                service.adjustMasterVolume(direction, flags, this.mContext.getOpPackageName());
            } else {
                MediaSessionLegacyHelper.getHelper(this.mContext).sendAdjustVolumeBy(suggestedStreamType, direction, flags);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in adjustSuggestedStreamVolume", e);
        }
    }

    public void adjustMasterVolume(int steps, int flags) {
        try {
            getService().adjustMasterVolume(steps, flags, this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in adjustMasterVolume", e);
        }
    }

    public int getRingerMode() {
        try {
            return getService().getRingerModeExternal();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getRingerMode", e);
            return VIBRATE_SETTING_ONLY_SILENT;
        }
    }

    public static boolean isValidRingerMode(int ringerMode) {
        boolean z = false;
        if (ringerMode >= 0 && ringerMode <= VIBRATE_SETTING_ONLY_SILENT) {
            try {
                z = getService().isValidRingerMode(ringerMode);
            } catch (RemoteException e) {
                Log.e(TAG, "Dead object in isValidRingerMode", e);
            }
        }
        return z;
    }

    public int getStreamMaxVolume(int streamType) {
        IAudioService service = getService();
        try {
            if (this.mUseMasterVolume) {
                return service.getMasterMaxVolume();
            }
            return service.getStreamMaxVolume(streamType);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getStreamMaxVolume", e);
            return VIBRATE_TYPE_RINGER;
        }
    }

    public int getStreamVolume(int streamType) {
        IAudioService service = getService();
        try {
            if (this.mUseMasterVolume) {
                return service.getMasterVolume();
            }
            return service.getStreamVolume(streamType);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getStreamVolume", e);
            return VIBRATE_TYPE_RINGER;
        }
    }

    public int getLastAudibleStreamVolume(int streamType) {
        IAudioService service = getService();
        try {
            if (this.mUseMasterVolume) {
                return service.getLastAudibleMasterVolume();
            }
            return service.getLastAudibleStreamVolume(streamType);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getLastAudibleStreamVolume", e);
            return VIBRATE_TYPE_RINGER;
        }
    }

    public int getMasterStreamType() {
        try {
            return getService().getMasterStreamType();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getMasterStreamType", e);
            return VIBRATE_SETTING_ONLY_SILENT;
        }
    }

    public void setRingerMode(int ringerMode) {
        if (isValidRingerMode(ringerMode)) {
            try {
                getService().setRingerModeExternal(ringerMode, this.mContext.getOpPackageName());
            } catch (RemoteException e) {
                Log.e(TAG, "Dead object in setRingerMode", e);
            }
        }
    }

    public void setStreamVolume(int streamType, int index, int flags) {
        IAudioService service = getService();
        try {
            if (this.mUseMasterVolume) {
                service.setMasterVolume(index, flags, this.mContext.getOpPackageName());
            } else {
                service.setStreamVolume(streamType, index, flags, this.mContext.getOpPackageName());
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setStreamVolume", e);
        }
    }

    public int getMasterMaxVolume() {
        try {
            return getService().getMasterMaxVolume();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getMasterMaxVolume", e);
            return VIBRATE_TYPE_RINGER;
        }
    }

    public int getMasterVolume() {
        try {
            return getService().getMasterVolume();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getMasterVolume", e);
            return VIBRATE_TYPE_RINGER;
        }
    }

    public int getLastAudibleMasterVolume() {
        try {
            return getService().getLastAudibleMasterVolume();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getLastAudibleMasterVolume", e);
            return VIBRATE_TYPE_RINGER;
        }
    }

    public void setMasterVolume(int index, int flags) {
        try {
            getService().setMasterVolume(index, flags, this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setMasterVolume", e);
        }
    }

    public void setStreamSolo(int streamType, boolean state) {
        try {
            getService().setStreamSolo(streamType, state, this.mICallBack);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setStreamSolo", e);
        }
    }

    public void setStreamMute(int streamType, boolean state) {
        try {
            getService().setStreamMute(streamType, state, this.mICallBack);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setStreamMute", e);
        }
    }

    public boolean isStreamMute(int streamType) {
        try {
            return getService().isStreamMute(streamType);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in isStreamMute", e);
            return false;
        }
    }

    public void setMasterMute(boolean state) {
        setMasterMute(state, VIBRATE_TYPE_NOTIFICATION);
    }

    public void setMasterMute(boolean state, int flags) {
        try {
            getService().setMasterMute(state, flags, this.mContext.getOpPackageName(), this.mICallBack);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setMasterMute", e);
        }
    }

    public boolean isMasterMute() {
        try {
            return getService().isMasterMute();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in isMasterMute", e);
            return false;
        }
    }

    public void forceVolumeControlStream(int streamType) {
        if (!this.mUseMasterVolume) {
            try {
                getService().forceVolumeControlStream(streamType, this.mICallBack);
            } catch (RemoteException e) {
                Log.e(TAG, "Dead object in forceVolumeControlStream", e);
            }
        }
    }

    public boolean shouldVibrate(int vibrateType) {
        try {
            return getService().shouldVibrate(vibrateType);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in shouldVibrate", e);
            return false;
        }
    }

    public int getVibrateSetting(int vibrateType) {
        try {
            return getService().getVibrateSetting(vibrateType);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getVibrateSetting", e);
            return VIBRATE_TYPE_RINGER;
        }
    }

    public void setVibrateSetting(int vibrateType, int vibrateSetting) {
        try {
            getService().setVibrateSetting(vibrateType, vibrateSetting);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setVibrateSetting", e);
        }
    }

    public void setSpeakerphoneOn(boolean on) {
        try {
            getService().setSpeakerphoneOn(on);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setSpeakerphoneOn", e);
        }
    }

    public boolean isSpeakerphoneOn() {
        try {
            return getService().isSpeakerphoneOn();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in isSpeakerphoneOn", e);
            return false;
        }
    }

    public boolean isBluetoothScoAvailableOffCall() {
        return this.mContext.getResources().getBoolean(R.bool.config_bluetooth_sco_off_call);
    }

    public void startBluetoothSco() {
        try {
            getService().startBluetoothSco(this.mICallBack, this.mContext.getApplicationInfo().targetSdkVersion);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in startBluetoothSco", e);
        }
    }

    public void startBluetoothScoVirtualCall() {
        try {
            getService().startBluetoothScoVirtualCall(this.mICallBack);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in startBluetoothScoVirtualCall", e);
        }
    }

    public void stopBluetoothSco() {
        try {
            getService().stopBluetoothSco(this.mICallBack);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in stopBluetoothSco", e);
        }
    }

    public void setBluetoothScoOn(boolean on) {
        try {
            getService().setBluetoothScoOn(on);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setBluetoothScoOn", e);
        }
    }

    public boolean isBluetoothScoOn() {
        try {
            return getService().isBluetoothScoOn();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in isBluetoothScoOn", e);
            return false;
        }
    }

    @Deprecated
    public void setBluetoothA2dpOn(boolean on) {
    }

    public boolean isBluetoothA2dpOn() {
        if (AudioSystem.getDeviceConnectionState(FLAG_SHOW_SILENT_HINT, "") == 0) {
            return false;
        }
        return true;
    }

    @Deprecated
    public void setWiredHeadsetOn(boolean on) {
    }

    public boolean isWiredHeadsetOn() {
        if (AudioSystem.getDeviceConnectionState(STREAM_ALARM, "") == 0 && AudioSystem.getDeviceConnectionState(STREAM_DTMF, "") == 0) {
            return false;
        }
        return true;
    }

    public void setMicrophoneMute(boolean on) {
        try {
            getService().setMicrophoneMute(on, this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setMicrophoneMute", e);
        }
    }

    public boolean isMicrophoneMute() {
        return AudioSystem.isMicrophoneMuted();
    }

    public void setMode(int mode) {
        try {
            getService().setMode(mode, this.mICallBack);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setMode", e);
        }
    }

    public int getMode() {
        try {
            return getService().getMode();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getMode", e);
            return MODE_INVALID;
        }
    }

    @Deprecated
    public void setRouting(int mode, int routes, int mask) {
    }

    @Deprecated
    public int getRouting(int mode) {
        return SCO_AUDIO_STATE_ERROR;
    }

    public boolean isMusicActive() {
        return AudioSystem.isStreamActive(STREAM_MUSIC, VIBRATE_TYPE_RINGER);
    }

    public boolean isMusicActiveRemotely() {
        return AudioSystem.isStreamActiveRemotely(STREAM_MUSIC, VIBRATE_TYPE_RINGER);
    }

    public boolean isAudioFocusExclusive() {
        try {
            if (getService().getCurrentAudioFocus() == STREAM_ALARM) {
                return true;
            }
            return false;
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in isAudioFocusExclusive()", e);
            return false;
        }
    }

    public int generateAudioSessionId() {
        int session = AudioSystem.newAudioSessionId();
        if (session > 0) {
            return session;
        }
        Log.e(TAG, "Failure to generate a new audio session ID");
        return SCO_AUDIO_STATE_ERROR;
    }

    @Deprecated
    public void setParameter(String key, String value) {
        setParameters(key + "=" + value);
    }

    public void setParameters(String keyValuePairs) {
        AudioSystem.setParameters(keyValuePairs);
    }

    public String getParameters(String keys) {
        return AudioSystem.getParameters(keys);
    }

    public void playSoundEffect(int effectType) {
        if (effectType >= 0 && effectType < NUM_SOUND_EFFECTS && querySoundEffectsEnabled(Process.myUserHandle().getIdentifier())) {
            try {
                getService().playSoundEffect(effectType);
            } catch (RemoteException e) {
                Log.e(TAG, "Dead object in playSoundEffect" + e);
            }
        }
    }

    public void playSoundEffect(int effectType, int userId) {
        if (effectType >= 0 && effectType < NUM_SOUND_EFFECTS && querySoundEffectsEnabled(userId)) {
            try {
                getService().playSoundEffect(effectType);
            } catch (RemoteException e) {
                Log.e(TAG, "Dead object in playSoundEffect" + e);
            }
        }
    }

    public void playSoundEffect(int effectType, float volume) {
        if (effectType >= 0 && effectType < NUM_SOUND_EFFECTS) {
            try {
                getService().playSoundEffectVolume(effectType, volume);
            } catch (RemoteException e) {
                Log.e(TAG, "Dead object in playSoundEffect" + e);
            }
        }
    }

    private boolean querySoundEffectsEnabled(int user) {
        return System.getIntForUser(this.mContext.getContentResolver(), "sound_effects_enabled", VIBRATE_TYPE_RINGER, user) != 0;
    }

    public void loadSoundEffects() {
        try {
            getService().loadSoundEffects();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in loadSoundEffects" + e);
        }
    }

    public void unloadSoundEffects() {
        try {
            getService().unloadSoundEffects();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in unloadSoundEffects" + e);
        }
    }

    private OnAudioFocusChangeListener findFocusListener(String id) {
        return (OnAudioFocusChangeListener) this.mAudioFocusIdListenerMap.get(id);
    }

    private String getIdForAudioFocusListener(OnAudioFocusChangeListener l) {
        if (l == null) {
            return new String(toString());
        }
        return new String(toString() + l.toString());
    }

    public void registerAudioFocusListener(OnAudioFocusChangeListener l) {
        synchronized (this.mFocusListenerLock) {
            if (this.mAudioFocusIdListenerMap.containsKey(getIdForAudioFocusListener(l))) {
                return;
            }
            this.mAudioFocusIdListenerMap.put(getIdForAudioFocusListener(l), l);
        }
    }

    public void unregisterAudioFocusListener(OnAudioFocusChangeListener l) {
        synchronized (this.mFocusListenerLock) {
            this.mAudioFocusIdListenerMap.remove(getIdForAudioFocusListener(l));
        }
    }

    public int requestAudioFocus(OnAudioFocusChangeListener l, int streamType, int durationHint) {
        int status = VIBRATE_TYPE_RINGER;
        try {
            status = requestAudioFocus(l, new Builder().setInternalLegacyStreamType(streamType).build(), durationHint, VIBRATE_TYPE_RINGER);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Audio focus request denied due to ", e);
        }
        return status;
    }

    public int requestAudioFocus(OnAudioFocusChangeListener l, AudioAttributes requestAttributes, int durationHint, int flags) throws IllegalArgumentException {
        if (flags != (flags & STREAM_MUSIC)) {
            throw new IllegalArgumentException("Invalid flags 0x" + Integer.toHexString(flags).toUpperCase());
        }
        return requestAudioFocus(l, requestAttributes, durationHint, flags & STREAM_MUSIC, null);
    }

    public int requestAudioFocus(OnAudioFocusChangeListener l, AudioAttributes requestAttributes, int durationHint, int flags, AudioPolicy ap) throws IllegalArgumentException {
        if (requestAttributes == null) {
            throw new IllegalArgumentException("Illegal null AudioAttributes argument");
        } else if (durationHint < VIBRATE_TYPE_NOTIFICATION || durationHint > STREAM_ALARM) {
            throw new IllegalArgumentException("Invalid duration hint");
        } else if (flags != (flags & STREAM_SYSTEM_ENFORCED)) {
            throw new IllegalArgumentException("Illegal flags 0x" + Integer.toHexString(flags).toUpperCase());
        } else if ((flags & VIBRATE_TYPE_NOTIFICATION) == VIBRATE_TYPE_NOTIFICATION && l == null) {
            throw new IllegalArgumentException("Illegal null focus listener when flagged as accepting delayed focus grant");
        } else if ((flags & STREAM_ALARM) == STREAM_ALARM && ap == null) {
            throw new IllegalArgumentException("Illegal null audio policy when locking audio focus");
        } else {
            int status = VIBRATE_TYPE_RINGER;
            registerAudioFocusListener(l);
            try {
                status = getService().requestAudioFocus(requestAttributes, durationHint, this.mICallBack, this.mAudioFocusDispatcher, getIdForAudioFocusListener(l), this.mContext.getOpPackageName(), flags, ap != null ? ap.cb() : null);
            } catch (RemoteException e) {
                Log.e(TAG, "Can't call requestAudioFocus() on AudioService:", e);
            }
            return status;
        }
    }

    public void requestAudioFocusForCall(int streamType, int durationHint) {
        IAudioService service = getService();
        try {
            int i = durationHint;
            service.requestAudioFocus(new Builder().setInternalLegacyStreamType(streamType).build(), i, this.mICallBack, null, "AudioFocus_For_Phone_Ring_And_Calls", this.mContext.getOpPackageName(), STREAM_ALARM, null);
        } catch (RemoteException e) {
            Log.e(TAG, "Can't call requestAudioFocusForCall() on AudioService:", e);
        }
    }

    public void abandonAudioFocusForCall() {
        try {
            getService().abandonAudioFocus(null, "AudioFocus_For_Phone_Ring_And_Calls", null);
        } catch (RemoteException e) {
            Log.e(TAG, "Can't call abandonAudioFocusForCall() on AudioService:", e);
        }
    }

    public int abandonAudioFocus(OnAudioFocusChangeListener l) {
        return abandonAudioFocus(l, null);
    }

    public int abandonAudioFocus(OnAudioFocusChangeListener l, AudioAttributes aa) {
        int status = VIBRATE_TYPE_RINGER;
        unregisterAudioFocusListener(l);
        try {
            status = getService().abandonAudioFocus(this.mAudioFocusDispatcher, getIdForAudioFocusListener(l), aa);
        } catch (RemoteException e) {
            Log.e(TAG, "Can't call abandonAudioFocus() on AudioService:", e);
        }
        return status;
    }

    @Deprecated
    public void registerMediaButtonEventReceiver(ComponentName eventReceiver) {
        if (eventReceiver != null) {
            if (eventReceiver.getPackageName().equals(this.mContext.getPackageName())) {
                Intent mediaButtonIntent = new Intent("android.intent.action.MEDIA_BUTTON");
                mediaButtonIntent.setComponent(eventReceiver);
                registerMediaButtonIntent(PendingIntent.getBroadcast(this.mContext, VIBRATE_TYPE_RINGER, mediaButtonIntent, VIBRATE_TYPE_RINGER), eventReceiver);
                return;
            }
            Log.e(TAG, "registerMediaButtonEventReceiver() error: receiver and context package names don't match");
        }
    }

    @Deprecated
    public void registerMediaButtonEventReceiver(PendingIntent eventReceiver) {
        if (eventReceiver != null) {
            registerMediaButtonIntent(eventReceiver, null);
        }
    }

    public void registerMediaButtonIntent(PendingIntent pi, ComponentName eventReceiver) {
        if (pi == null) {
            Log.e(TAG, "Cannot call registerMediaButtonIntent() with a null parameter");
        } else {
            MediaSessionLegacyHelper.getHelper(this.mContext).addMediaButtonListener(pi, eventReceiver, this.mContext);
        }
    }

    @Deprecated
    public void unregisterMediaButtonEventReceiver(ComponentName eventReceiver) {
        if (eventReceiver != null) {
            Intent mediaButtonIntent = new Intent("android.intent.action.MEDIA_BUTTON");
            mediaButtonIntent.setComponent(eventReceiver);
            unregisterMediaButtonIntent(PendingIntent.getBroadcast(this.mContext, VIBRATE_TYPE_RINGER, mediaButtonIntent, VIBRATE_TYPE_RINGER));
        }
    }

    @Deprecated
    public void unregisterMediaButtonEventReceiver(PendingIntent eventReceiver) {
        if (eventReceiver != null) {
            unregisterMediaButtonIntent(eventReceiver);
        }
    }

    public void unregisterMediaButtonIntent(PendingIntent pi) {
        MediaSessionLegacyHelper.getHelper(this.mContext).removeMediaButtonListener(pi);
    }

    @Deprecated
    public void registerRemoteControlClient(RemoteControlClient rcClient) {
        if (rcClient != null && rcClient.getRcMediaIntent() != null) {
            rcClient.registerWithSession(MediaSessionLegacyHelper.getHelper(this.mContext));
        }
    }

    @Deprecated
    public void unregisterRemoteControlClient(RemoteControlClient rcClient) {
        if (rcClient != null && rcClient.getRcMediaIntent() != null) {
            rcClient.unregisterWithSession(MediaSessionLegacyHelper.getHelper(this.mContext));
        }
    }

    @Deprecated
    public boolean registerRemoteController(RemoteController rctlr) {
        if (rctlr == null) {
            return false;
        }
        rctlr.startListeningToSessions();
        return true;
    }

    @Deprecated
    public void unregisterRemoteController(RemoteController rctlr) {
        if (rctlr != null) {
            rctlr.stopListeningToSessions();
        }
    }

    public void registerRemoteControlDisplay(IRemoteControlDisplay rcd) {
        registerRemoteControlDisplay(rcd, SCO_AUDIO_STATE_ERROR, SCO_AUDIO_STATE_ERROR);
    }

    public void registerRemoteControlDisplay(IRemoteControlDisplay rcd, int w, int h) {
        if (rcd != null) {
            try {
                getService().registerRemoteControlDisplay(rcd, w, h);
            } catch (RemoteException e) {
                Log.e(TAG, "Dead object in registerRemoteControlDisplay " + e);
            }
        }
    }

    public void unregisterRemoteControlDisplay(IRemoteControlDisplay rcd) {
        if (rcd != null) {
            try {
                getService().unregisterRemoteControlDisplay(rcd);
            } catch (RemoteException e) {
                Log.e(TAG, "Dead object in unregisterRemoteControlDisplay " + e);
            }
        }
    }

    public void remoteControlDisplayUsesBitmapSize(IRemoteControlDisplay rcd, int w, int h) {
        if (rcd != null) {
            try {
                getService().remoteControlDisplayUsesBitmapSize(rcd, w, h);
            } catch (RemoteException e) {
                Log.e(TAG, "Dead object in remoteControlDisplayUsesBitmapSize " + e);
            }
        }
    }

    public void remoteControlDisplayWantsPlaybackPositionSync(IRemoteControlDisplay rcd, boolean wantsSync) {
        if (rcd != null) {
            try {
                getService().remoteControlDisplayWantsPlaybackPositionSync(rcd, wantsSync);
            } catch (RemoteException e) {
                Log.e(TAG, "Dead object in remoteControlDisplayWantsPlaybackPositionSync " + e);
            }
        }
    }

    public int registerAudioPolicy(AudioPolicy policy) {
        if (policy == null) {
            throw new IllegalArgumentException("Illegal null AudioPolicy argument");
        }
        try {
            String regId = getService().registerAudioPolicy(policy.getConfig(), policy.cb(), policy.hasFocusListener());
            if (regId == null) {
                return SCO_AUDIO_STATE_ERROR;
            }
            policy.setRegistration(regId);
            return VIBRATE_TYPE_RINGER;
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in registerAudioPolicyAsync()", e);
            return SCO_AUDIO_STATE_ERROR;
        }
    }

    public void unregisterAudioPolicyAsync(AudioPolicy policy) {
        if (policy == null) {
            throw new IllegalArgumentException("Illegal null AudioPolicy argument");
        }
        try {
            getService().unregisterAudioPolicyAsync(policy.cb());
            policy.setRegistration(null);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in unregisterAudioPolicyAsync()", e);
        }
    }

    public void reloadAudioSettings() {
        try {
            getService().reloadAudioSettings();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in reloadAudioSettings" + e);
        }
    }

    public void avrcpSupportsAbsoluteVolume(String address, boolean support) {
        try {
            getService().avrcpSupportsAbsoluteVolume(address, support);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in avrcpSupportsAbsoluteVolume", e);
        }
    }

    public boolean isSilentMode() {
        int ringerMode = getRingerMode();
        if (ringerMode == 0 || ringerMode == VIBRATE_TYPE_NOTIFICATION) {
            return true;
        }
        return false;
    }

    public static boolean isOutputDevice(int device) {
        return (USE_DEFAULT_STREAM_TYPE & device) == 0;
    }

    public static boolean isInputDevice(int device) {
        return (device & USE_DEFAULT_STREAM_TYPE) == USE_DEFAULT_STREAM_TYPE;
    }

    public int getDevicesForStream(int streamType) {
        switch (streamType) {
            case VIBRATE_TYPE_RINGER /*0*/:
            case VIBRATE_TYPE_NOTIFICATION /*1*/:
            case VIBRATE_SETTING_ONLY_SILENT /*2*/:
            case STREAM_MUSIC /*3*/:
            case STREAM_ALARM /*4*/:
            case STREAM_NOTIFICATION /*5*/:
            case STREAM_DTMF /*8*/:
                return AudioSystem.getDevicesForStream(streamType);
            default:
                return VIBRATE_TYPE_RINGER;
        }
    }

    public void setWiredDeviceConnectionState(int device, int state, String name) {
        try {
            getService().setWiredDeviceConnectionState(device, state, name);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setWiredDeviceConnectionState " + e);
        }
    }

    public int setBluetoothA2dpDeviceConnectionState(BluetoothDevice device, int state, int profile) {
        IAudioService service = getService();
        int delay = VIBRATE_TYPE_RINGER;
        try {
            return service.setBluetoothA2dpDeviceConnectionState(device, state, profile);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setBluetoothA2dpDeviceConnectionState " + e);
            return delay;
        } catch (Throwable th) {
            return delay;
        }
    }

    public IRingtonePlayer getRingtonePlayer() {
        try {
            return getService().getRingtonePlayer();
        } catch (RemoteException e) {
            return null;
        }
    }

    public String getProperty(String key) {
        if (PROPERTY_OUTPUT_SAMPLE_RATE.equals(key)) {
            int outputSampleRate = AudioSystem.getPrimaryOutputSamplingRate();
            if (outputSampleRate > 0) {
                return Integer.toString(outputSampleRate);
            }
            return null;
        } else if (!PROPERTY_OUTPUT_FRAMES_PER_BUFFER.equals(key)) {
            return null;
        } else {
            int outputFramesPerBuffer = AudioSystem.getPrimaryOutputFrameCount();
            if (outputFramesPerBuffer > 0) {
                return Integer.toString(outputFramesPerBuffer);
            }
            return null;
        }
    }

    public int getOutputLatency(int streamType) {
        return AudioSystem.getOutputLatency(streamType);
    }

    public void setVolumeController(IVolumeController controller) {
        try {
            getService().setVolumeController(controller);
        } catch (RemoteException e) {
            Log.w(TAG, "Error setting volume controller", e);
        }
    }

    public void notifyVolumeControllerVisible(IVolumeController controller, boolean visible) {
        try {
            getService().notifyVolumeControllerVisible(controller, visible);
        } catch (RemoteException e) {
            Log.w(TAG, "Error notifying about volume controller visibility", e);
        }
    }

    public boolean isStreamAffectedByRingerMode(int streamType) {
        try {
            return getService().isStreamAffectedByRingerMode(streamType);
        } catch (RemoteException e) {
            Log.w(TAG, "Error calling isStreamAffectedByRingerMode", e);
            return false;
        }
    }

    public void disableSafeMediaVolume() {
        try {
            getService().disableSafeMediaVolume();
        } catch (RemoteException e) {
            Log.w(TAG, "Error disabling safe media volume", e);
        }
    }

    public void setRingerModeInternal(int ringerMode) {
        try {
            getService().setRingerModeInternal(ringerMode, this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            Log.w(TAG, "Error calling setRingerModeInternal", e);
        }
    }

    public int getRingerModeInternal() {
        try {
            return getService().getRingerModeInternal();
        } catch (RemoteException e) {
            Log.w(TAG, "Error calling getRingerModeInternal", e);
            return VIBRATE_SETTING_ONLY_SILENT;
        }
    }

    public int setHdmiSystemAudioSupported(boolean on) {
        try {
            return getService().setHdmiSystemAudioSupported(on);
        } catch (RemoteException e) {
            Log.w(TAG, "Error setting system audio mode", e);
            return VIBRATE_TYPE_RINGER;
        }
    }

    public boolean isHdmiSystemAudioSupported() {
        try {
            return getService().isHdmiSystemAudioSupported();
        } catch (RemoteException e) {
            Log.w(TAG, "Error querying system audio mode", e);
            return false;
        }
    }

    public int listAudioPorts(ArrayList<AudioPort> ports) {
        return updateAudioPortCache(ports, null);
    }

    public int listAudioDevicePorts(ArrayList<AudioPort> devices) {
        ArrayList<AudioPort> ports = new ArrayList();
        int status = updateAudioPortCache(ports, null);
        if (status == 0) {
            devices.clear();
            for (int i = VIBRATE_TYPE_RINGER; i < ports.size(); i += VIBRATE_TYPE_NOTIFICATION) {
                if (ports.get(i) instanceof AudioDevicePort) {
                    devices.add(ports.get(i));
                }
            }
        }
        return status;
    }

    public int createAudioPatch(AudioPatch[] patch, AudioPortConfig[] sources, AudioPortConfig[] sinks) {
        return AudioSystem.createAudioPatch(patch, sources, sinks);
    }

    public int releaseAudioPatch(AudioPatch patch) {
        return AudioSystem.releaseAudioPatch(patch);
    }

    public int listAudioPatches(ArrayList<AudioPatch> patches) {
        return updateAudioPortCache(null, patches);
    }

    public int setAudioPortGain(AudioPort port, AudioGainConfig gain) {
        if (port == null || gain == null) {
            return MODE_INVALID;
        }
        AudioPortConfig activeConfig = port.activeConfig();
        AudioPortConfig config = new AudioPortConfig(port, activeConfig.samplingRate(), activeConfig.channelMask(), activeConfig.format(), gain);
        config.mConfigMask = STREAM_DTMF;
        return AudioSystem.setAudioPortConfig(config);
    }

    public void registerAudioPortUpdateListener(OnAudioPortUpdateListener l) {
        sAudioPortEventHandler.registerListener(l);
    }

    public void unregisterAudioPortUpdateListener(OnAudioPortUpdateListener l) {
        sAudioPortEventHandler.unregisterListener(l);
    }

    static int resetAudioPortGeneration() {
        int generation;
        synchronized (sAudioPortGeneration) {
            generation = sAudioPortGeneration.intValue();
            sAudioPortGeneration = Integer.valueOf(VIBRATE_TYPE_RINGER);
        }
        return generation;
    }

    static int updateAudioPortCache(ArrayList<AudioPort> ports, ArrayList<AudioPatch> patches) {
        synchronized (sAudioPortGeneration) {
            if (sAudioPortGeneration.intValue() == 0) {
                int[] patchGeneration = new int[VIBRATE_TYPE_NOTIFICATION];
                int[] portGeneration = new int[VIBRATE_TYPE_NOTIFICATION];
                ArrayList<AudioPort> newPorts = new ArrayList();
                ArrayList<AudioPatch> newPatches = new ArrayList();
                do {
                    newPorts.clear();
                    int status = AudioSystem.listAudioPorts(newPorts, portGeneration);
                    if (status != 0) {
                        Log.w(TAG, "updateAudioPortCache: listAudioPorts failed");
                        return status;
                    }
                    newPatches.clear();
                    status = AudioSystem.listAudioPatches(newPatches, patchGeneration);
                    if (status != 0) {
                        Log.w(TAG, "updateAudioPortCache: listAudioPatches failed");
                        return status;
                    }
                } while (patchGeneration[VIBRATE_TYPE_RINGER] != portGeneration[VIBRATE_TYPE_RINGER]);
                for (int i = VIBRATE_TYPE_RINGER; i < newPatches.size(); i += VIBRATE_TYPE_NOTIFICATION) {
                    int j;
                    for (j = VIBRATE_TYPE_RINGER; j < ((AudioPatch) newPatches.get(i)).sources().length; j += VIBRATE_TYPE_NOTIFICATION) {
                        ((AudioPatch) newPatches.get(i)).sources()[j] = updatePortConfig(((AudioPatch) newPatches.get(i)).sources()[j], newPorts);
                    }
                    for (j = VIBRATE_TYPE_RINGER; j < ((AudioPatch) newPatches.get(i)).sinks().length; j += VIBRATE_TYPE_NOTIFICATION) {
                        ((AudioPatch) newPatches.get(i)).sinks()[j] = updatePortConfig(((AudioPatch) newPatches.get(i)).sinks()[j], newPorts);
                    }
                }
                Iterator<AudioPatch> i2 = newPatches.iterator();
                while (i2.hasNext()) {
                    int i$;
                    AudioPatch newPatch = (AudioPatch) i2.next();
                    boolean hasInvalidPort = false;
                    AudioPortConfig[] arr$ = newPatch.sources();
                    int len$ = arr$.length;
                    for (i$ = VIBRATE_TYPE_RINGER; i$ < len$; i$ += VIBRATE_TYPE_NOTIFICATION) {
                        if (arr$[i$] == null) {
                            hasInvalidPort = true;
                            break;
                        }
                    }
                    arr$ = newPatch.sinks();
                    len$ = arr$.length;
                    for (i$ = VIBRATE_TYPE_RINGER; i$ < len$; i$ += VIBRATE_TYPE_NOTIFICATION) {
                        if (arr$[i$] == null) {
                            hasInvalidPort = true;
                            break;
                        }
                    }
                    if (hasInvalidPort) {
                        i2.remove();
                    }
                }
                sAudioPortsCached = newPorts;
                sAudioPatchesCached = newPatches;
                sAudioPortGeneration = Integer.valueOf(portGeneration[VIBRATE_TYPE_RINGER]);
            }
            if (ports != null) {
                ports.clear();
                ports.addAll(sAudioPortsCached);
            }
            if (patches != null) {
                patches.clear();
                patches.addAll(sAudioPatchesCached);
            }
            return VIBRATE_TYPE_RINGER;
        }
    }

    static AudioPortConfig updatePortConfig(AudioPortConfig portCfg, ArrayList<AudioPort> ports) {
        AudioPort port = portCfg.port();
        int k = VIBRATE_TYPE_RINGER;
        while (k < ports.size()) {
            if (((AudioPort) ports.get(k)).handle().equals(port.handle())) {
                port = (AudioPort) ports.get(k);
                break;
            }
            k += VIBRATE_TYPE_NOTIFICATION;
        }
        if (k == ports.size()) {
            Log.e(TAG, "updatePortConfig port not found for handle: " + port.handle().id());
            return null;
        }
        AudioGainConfig gainCfg = portCfg.gain();
        if (gainCfg != null) {
            gainCfg = port.gain(gainCfg.index()).buildConfig(gainCfg.mode(), gainCfg.channelMask(), gainCfg.values(), gainCfg.rampDurationMs());
        }
        return port.buildConfig(portCfg.samplingRate(), portCfg.channelMask(), portCfg.format(), gainCfg);
    }
}
