package android.media;

import android.media.audiopolicy.AudioMix;
import java.util.ArrayList;

public class AudioSystem {
    public static final int AUDIO_HW_SYNC_INVALID = 0;
    public static final int AUDIO_SESSION_ALLOCATE = 0;
    public static final int AUDIO_STATUS_ERROR = 1;
    public static final int AUDIO_STATUS_OK = 0;
    public static final int AUDIO_STATUS_SERVER_DIED = 100;
    public static final int BAD_VALUE = -2;
    public static final int DEAD_OBJECT = -6;
    public static final int DEVICE_ALL_HDMI_SYSTEM_AUDIO_AND_SPEAKER = 2883586;
    public static final int DEVICE_BIT_DEFAULT = 1073741824;
    public static final int DEVICE_BIT_IN = Integer.MIN_VALUE;
    public static final int DEVICE_IN_ALL = -1073217537;
    public static final int DEVICE_IN_ALL_SCO = -2147483640;
    public static final int DEVICE_IN_ALL_USB = -2147477504;
    public static final int DEVICE_IN_AMBIENT = -2147483646;
    public static final String DEVICE_IN_AMBIENT_NAME = "ambient";
    public static final int DEVICE_IN_ANLG_DOCK_HEADSET = -2147483136;
    public static final String DEVICE_IN_ANLG_DOCK_HEADSET_NAME = "analog_dock";
    public static final int DEVICE_IN_AUX_DIGITAL = -2147483616;
    public static final String DEVICE_IN_AUX_DIGITAL_NAME = "aux_digital";
    public static final int DEVICE_IN_BACK_MIC = -2147483520;
    public static final String DEVICE_IN_BACK_MIC_NAME = "back_mic";
    public static final int DEVICE_IN_BLUETOOTH_A2DP = -2147352576;
    public static final String DEVICE_IN_BLUETOOTH_A2DP_NAME = "bt_a2dp";
    public static final int DEVICE_IN_BLUETOOTH_SCO_HEADSET = -2147483640;
    public static final String DEVICE_IN_BLUETOOTH_SCO_HEADSET_NAME = "bt_sco_hs";
    public static final int DEVICE_IN_BUILTIN_MIC = -2147483644;
    public static final String DEVICE_IN_BUILTIN_MIC_NAME = "mic";
    public static final int DEVICE_IN_COMMUNICATION = -2147483647;
    public static final String DEVICE_IN_COMMUNICATION_NAME = "communication";
    public static final int DEVICE_IN_DEFAULT = -1073741824;
    public static final int DEVICE_IN_DGTL_DOCK_HEADSET = -2147482624;
    public static final String DEVICE_IN_DGTL_DOCK_HEADSET_NAME = "digital_dock";
    public static final int DEVICE_IN_FM_TUNER = -2147475456;
    public static final String DEVICE_IN_FM_TUNER_NAME = "fm_tuner";
    public static final int DEVICE_IN_HDMI = -2147483616;
    public static final int DEVICE_IN_LINE = -2147450880;
    public static final String DEVICE_IN_LINE_NAME = "line";
    public static final int DEVICE_IN_LOOPBACK = -2147221504;
    public static final String DEVICE_IN_LOOPBACK_NAME = "loopback";
    public static final int DEVICE_IN_REMOTE_SUBMIX = -2147483392;
    public static final String DEVICE_IN_REMOTE_SUBMIX_NAME = "remote_submix";
    public static final int DEVICE_IN_SPDIF = -2147418112;
    public static final String DEVICE_IN_SPDIF_NAME = "spdif";
    public static final int DEVICE_IN_TELEPHONY_RX = -2147483584;
    public static final String DEVICE_IN_TELEPHONY_RX_NAME = "telephony_rx";
    public static final int DEVICE_IN_TV_TUNER = -2147467264;
    public static final String DEVICE_IN_TV_TUNER_NAME = "tv_tuner";
    public static final int DEVICE_IN_USB_ACCESSORY = -2147481600;
    public static final String DEVICE_IN_USB_ACCESSORY_NAME = "usb_accessory";
    public static final int DEVICE_IN_USB_DEVICE = -2147479552;
    public static final String DEVICE_IN_USB_DEVICE_NAME = "usb_device";
    public static final int DEVICE_IN_VOICE_CALL = -2147483584;
    public static final int DEVICE_IN_WIRED_HEADSET = -2147483632;
    public static final String DEVICE_IN_WIRED_HEADSET_NAME = "headset";
    public static final int DEVICE_NONE = 0;
    public static final int DEVICE_OUT_ALL = 1082130431;
    public static final int DEVICE_OUT_ALL_A2DP = 896;
    public static final int DEVICE_OUT_ALL_HDMI_SYSTEM_AUDIO = 2883584;
    public static final int DEVICE_OUT_ALL_SCO = 112;
    public static final int DEVICE_OUT_ALL_USB = 24576;
    public static final int DEVICE_OUT_ANLG_DOCK_HEADSET = 2048;
    public static final String DEVICE_OUT_ANLG_DOCK_HEADSET_NAME = "analog_dock";
    public static final int DEVICE_OUT_AUX_DIGITAL = 1024;
    public static final String DEVICE_OUT_AUX_DIGITAL_NAME = "aux_digital";
    public static final int DEVICE_OUT_AUX_LINE = 2097152;
    public static final String DEVICE_OUT_AUX_LINE_NAME = "aux_line";
    public static final int DEVICE_OUT_BLUETOOTH_A2DP = 128;
    public static final int DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES = 256;
    public static final String DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES_NAME = "bt_a2dp_hp";
    public static final String DEVICE_OUT_BLUETOOTH_A2DP_NAME = "bt_a2dp";
    public static final int DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER = 512;
    public static final String DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER_NAME = "bt_a2dp_spk";
    public static final int DEVICE_OUT_BLUETOOTH_SCO = 16;
    public static final int DEVICE_OUT_BLUETOOTH_SCO_CARKIT = 64;
    public static final String DEVICE_OUT_BLUETOOTH_SCO_CARKIT_NAME = "bt_sco_carkit";
    public static final int DEVICE_OUT_BLUETOOTH_SCO_HEADSET = 32;
    public static final String DEVICE_OUT_BLUETOOTH_SCO_HEADSET_NAME = "bt_sco_hs";
    public static final String DEVICE_OUT_BLUETOOTH_SCO_NAME = "bt_sco";
    public static final int DEVICE_OUT_DEFAULT = 1073741824;
    public static final int DEVICE_OUT_DGTL_DOCK_HEADSET = 4096;
    public static final String DEVICE_OUT_DGTL_DOCK_HEADSET_NAME = "digital_dock";
    public static final int DEVICE_OUT_EARPIECE = 1;
    public static final String DEVICE_OUT_EARPIECE_NAME = "earpiece";
    public static final int DEVICE_OUT_FM = 1048576;
    public static final String DEVICE_OUT_FM_NAME = "fm_transmitter";
    public static final int DEVICE_OUT_HDMI = 1024;
    public static final int DEVICE_OUT_HDMI_ARC = 262144;
    public static final String DEVICE_OUT_HDMI_ARC_NAME = "hmdi_arc";
    public static final String DEVICE_OUT_HDMI_NAME = "hdmi";
    public static final int DEVICE_OUT_LINE = 131072;
    public static final String DEVICE_OUT_LINE_NAME = "line";
    public static final int DEVICE_OUT_REMOTE_SUBMIX = 32768;
    public static final String DEVICE_OUT_REMOTE_SUBMIX_NAME = "remote_submix";
    public static final int DEVICE_OUT_SPDIF = 524288;
    public static final String DEVICE_OUT_SPDIF_NAME = "spdif";
    public static final int DEVICE_OUT_SPEAKER = 2;
    public static final String DEVICE_OUT_SPEAKER_NAME = "speaker";
    public static final int DEVICE_OUT_SPEAKER_SAFE = 4194304;
    public static final String DEVICE_OUT_SPEAKER_SAFE_NAME = "speaker_safe";
    public static final int DEVICE_OUT_TELEPHONY_TX = 65536;
    public static final String DEVICE_OUT_TELEPHONY_TX_NAME = "telephony_tx";
    public static final int DEVICE_OUT_USB_ACCESSORY = 8192;
    public static final String DEVICE_OUT_USB_ACCESSORY_NAME = "usb_accessory";
    public static final int DEVICE_OUT_USB_DEVICE = 16384;
    public static final String DEVICE_OUT_USB_DEVICE_NAME = "usb_device";
    public static final int DEVICE_OUT_WIRED_HEADPHONE = 8;
    public static final String DEVICE_OUT_WIRED_HEADPHONE_NAME = "headphone";
    public static final int DEVICE_OUT_WIRED_HEADSET = 4;
    public static final String DEVICE_OUT_WIRED_HEADSET_NAME = "headset";
    public static final int DEVICE_STATE_AVAILABLE = 1;
    public static final int DEVICE_STATE_UNAVAILABLE = 0;
    public static final int ERROR = -1;
    public static final int FORCE_ANALOG_DOCK = 8;
    public static final int FORCE_BT_A2DP = 4;
    public static final int FORCE_BT_CAR_DOCK = 6;
    public static final int FORCE_BT_DESK_DOCK = 7;
    public static final int FORCE_BT_SCO = 3;
    public static final int FORCE_DEFAULT = 0;
    public static final int FORCE_DIGITAL_DOCK = 9;
    public static final int FORCE_HDMI_SYSTEM_AUDIO_ENFORCED = 12;
    public static final int FORCE_HEADPHONES = 2;
    public static final int FORCE_NONE = 0;
    public static final int FORCE_NO_BT_A2DP = 10;
    public static final int FORCE_SPEAKER = 1;
    public static final int FORCE_SYSTEM_ENFORCED = 11;
    public static final int FORCE_WIRED_ACCESSORY = 5;
    public static final int FOR_COMMUNICATION = 0;
    public static final int FOR_DOCK = 3;
    public static final int FOR_HDMI_SYSTEM_AUDIO = 5;
    public static final int FOR_MEDIA = 1;
    public static final int FOR_RECORD = 2;
    public static final int FOR_SYSTEM = 4;
    public static final int INVALID_OPERATION = -3;
    public static final int MODE_CURRENT = -1;
    public static final int MODE_INVALID = -2;
    public static final int MODE_IN_CALL = 2;
    public static final int MODE_IN_COMMUNICATION = 3;
    public static final int MODE_NORMAL = 0;
    public static final int MODE_RINGTONE = 1;
    public static final int NO_INIT = -5;
    private static final int NUM_DEVICE_STATES = 1;
    private static final int NUM_FORCE_CONFIG = 13;
    private static final int NUM_FORCE_USE = 6;
    public static final int NUM_MODES = 4;
    public static final int NUM_STREAMS = 5;
    private static final int NUM_STREAM_TYPES = 10;
    public static final int PERMISSION_DENIED = -4;
    public static final int PHONE_STATE_INCALL = 2;
    public static final int PHONE_STATE_OFFCALL = 0;
    public static final int PHONE_STATE_RINGING = 1;
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
    public static final int STREAM_ALARM = 4;
    public static final int STREAM_BLUETOOTH_SCO = 6;
    public static final int STREAM_DEFAULT = -1;
    public static final int STREAM_DTMF = 8;
    public static final int STREAM_MUSIC = 3;
    public static final int STREAM_NOTIFICATION = 5;
    public static final int STREAM_RING = 2;
    public static final int STREAM_SYSTEM = 1;
    public static final int STREAM_SYSTEM_ENFORCED = 7;
    public static final int STREAM_TTS = 9;
    public static final int STREAM_VOICE_CALL = 0;
    public static final int SUCCESS = 0;
    public static final int SYNC_EVENT_NONE = 0;
    public static final int SYNC_EVENT_PRESENTATION_COMPLETE = 1;
    private static ErrorCallback mErrorCallback;

    public interface ErrorCallback {
        void onError(int i);
    }

    public static native int checkAudioFlinger();

    public static native int createAudioPatch(AudioPatch[] audioPatchArr, AudioPortConfig[] audioPortConfigArr, AudioPortConfig[] audioPortConfigArr2);

    public static native int getAudioHwSyncForSession(int i);

    public static native int getDeviceConnectionState(int i, String str);

    public static native int getDevicesForStream(int i);

    public static native int getForceUse(int i);

    public static native boolean getMasterMute();

    public static native float getMasterVolume();

    public static native int getOutputLatency(int i);

    public static native String getParameters(String str);

    public static native int getPrimaryOutputFrameCount();

    public static native int getPrimaryOutputSamplingRate();

    public static native int getStreamVolumeIndex(int i, int i2);

    public static native int initStreamVolume(int i, int i2, int i3);

    public static native boolean isMicrophoneMuted();

    public static native boolean isSourceActive(int i);

    public static native boolean isStreamActive(int i, int i2);

    public static native boolean isStreamActiveRemotely(int i, int i2);

    public static native int listAudioPatches(ArrayList<AudioPatch> arrayList, int[] iArr);

    public static native int listAudioPorts(ArrayList<AudioPort> arrayList, int[] iArr);

    public static native int muteMicrophone(boolean z);

    public static native int newAudioSessionId();

    public static native int registerPolicyMixes(ArrayList<AudioMix> arrayList, boolean z);

    public static native int releaseAudioPatch(AudioPatch audioPatch);

    public static native int setAudioPortConfig(AudioPortConfig audioPortConfig);

    public static native int setDeviceConnectionState(int i, int i2, String str);

    public static native int setForceUse(int i, int i2);

    public static native int setLowRamDevice(boolean z);

    public static native int setMasterMute(boolean z);

    public static native int setMasterVolume(float f);

    public static native int setParameters(String str);

    public static native int setPhoneState(int i);

    public static native int setStreamVolumeIndex(int i, int i2, int i3);

    public static final int getNumStreamTypes() {
        return NUM_STREAM_TYPES;
    }

    public static void setErrorCallback(ErrorCallback cb) {
        synchronized (AudioSystem.class) {
            mErrorCallback = cb;
            if (cb != null) {
                cb.onError(checkAudioFlinger());
            }
        }
    }

    private static void errorCallbackFromNative(int error) {
        ErrorCallback errorCallback = null;
        synchronized (AudioSystem.class) {
            if (mErrorCallback != null) {
                errorCallback = mErrorCallback;
            }
        }
        if (errorCallback != null) {
            errorCallback.onError(error);
        }
    }

    public static String getOutputDeviceName(int device) {
        switch (device) {
            case SYNC_EVENT_PRESENTATION_COMPLETE /*1*/:
                return DEVICE_OUT_EARPIECE_NAME;
            case STREAM_RING /*2*/:
                return DEVICE_OUT_SPEAKER_NAME;
            case STREAM_ALARM /*4*/:
                return DEVICE_OUT_WIRED_HEADSET_NAME;
            case STREAM_DTMF /*8*/:
                return DEVICE_OUT_WIRED_HEADPHONE_NAME;
            case ROUTE_BLUETOOTH_A2DP /*16*/:
                return DEVICE_OUT_BLUETOOTH_SCO_NAME;
            case DEVICE_OUT_BLUETOOTH_SCO_HEADSET /*32*/:
                return DEVICE_OUT_BLUETOOTH_SCO_HEADSET_NAME;
            case DEVICE_OUT_BLUETOOTH_SCO_CARKIT /*64*/:
                return DEVICE_OUT_BLUETOOTH_SCO_CARKIT_NAME;
            case DEVICE_OUT_BLUETOOTH_A2DP /*128*/:
                return DEVICE_OUT_BLUETOOTH_A2DP_NAME;
            case DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES /*256*/:
                return DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES_NAME;
            case DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER /*512*/:
                return DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER_NAME;
            case DEVICE_OUT_HDMI /*1024*/:
                return DEVICE_OUT_HDMI_NAME;
            case DEVICE_OUT_ANLG_DOCK_HEADSET /*2048*/:
                return DEVICE_OUT_ANLG_DOCK_HEADSET_NAME;
            case DEVICE_OUT_DGTL_DOCK_HEADSET /*4096*/:
                return DEVICE_OUT_DGTL_DOCK_HEADSET_NAME;
            case DEVICE_OUT_USB_ACCESSORY /*8192*/:
                return DEVICE_OUT_USB_ACCESSORY_NAME;
            case DEVICE_OUT_USB_DEVICE /*16384*/:
                return DEVICE_OUT_USB_DEVICE_NAME;
            case DEVICE_OUT_REMOTE_SUBMIX /*32768*/:
                return DEVICE_OUT_REMOTE_SUBMIX_NAME;
            case DEVICE_OUT_TELEPHONY_TX /*65536*/:
                return DEVICE_OUT_TELEPHONY_TX_NAME;
            case DEVICE_OUT_LINE /*131072*/:
                return DEVICE_OUT_LINE_NAME;
            case DEVICE_OUT_HDMI_ARC /*262144*/:
                return DEVICE_OUT_HDMI_ARC_NAME;
            case DEVICE_OUT_SPDIF /*524288*/:
                return DEVICE_OUT_SPDIF_NAME;
            case DEVICE_OUT_FM /*1048576*/:
                return DEVICE_OUT_FM_NAME;
            case DEVICE_OUT_AUX_LINE /*2097152*/:
                return DEVICE_OUT_AUX_LINE_NAME;
            case DEVICE_OUT_SPEAKER_SAFE /*4194304*/:
                return DEVICE_OUT_SPEAKER_SAFE_NAME;
            default:
                return Integer.toString(device);
        }
    }

    public static String getInputDeviceName(int device) {
        switch (device) {
            case DEVICE_IN_COMMUNICATION /*-2147483647*/:
                return DEVICE_IN_COMMUNICATION_NAME;
            case DEVICE_IN_AMBIENT /*-2147483646*/:
                return DEVICE_IN_AMBIENT_NAME;
            case DEVICE_IN_BUILTIN_MIC /*-2147483644*/:
                return DEVICE_IN_BUILTIN_MIC_NAME;
            case DEVICE_IN_BLUETOOTH_SCO_HEADSET /*-2147483640*/:
                return DEVICE_OUT_BLUETOOTH_SCO_HEADSET_NAME;
            case DEVICE_IN_WIRED_HEADSET /*-2147483632*/:
                return DEVICE_OUT_WIRED_HEADSET_NAME;
            case DEVICE_IN_HDMI /*-2147483616*/:
                return DEVICE_OUT_AUX_DIGITAL_NAME;
            case DEVICE_IN_VOICE_CALL /*-2147483584*/:
                return DEVICE_IN_TELEPHONY_RX_NAME;
            case DEVICE_IN_BACK_MIC /*-2147483520*/:
                return DEVICE_IN_BACK_MIC_NAME;
            case DEVICE_IN_REMOTE_SUBMIX /*-2147483392*/:
                return DEVICE_OUT_REMOTE_SUBMIX_NAME;
            case DEVICE_IN_ANLG_DOCK_HEADSET /*-2147483136*/:
                return DEVICE_OUT_ANLG_DOCK_HEADSET_NAME;
            case DEVICE_IN_DGTL_DOCK_HEADSET /*-2147482624*/:
                return DEVICE_OUT_DGTL_DOCK_HEADSET_NAME;
            case DEVICE_IN_USB_ACCESSORY /*-2147481600*/:
                return DEVICE_OUT_USB_ACCESSORY_NAME;
            case DEVICE_IN_USB_DEVICE /*-2147479552*/:
                return DEVICE_OUT_USB_DEVICE_NAME;
            case DEVICE_IN_FM_TUNER /*-2147475456*/:
                return DEVICE_IN_FM_TUNER_NAME;
            case DEVICE_IN_TV_TUNER /*-2147467264*/:
                return DEVICE_IN_TV_TUNER_NAME;
            case DEVICE_IN_LINE /*-2147450880*/:
                return DEVICE_OUT_LINE_NAME;
            case DEVICE_IN_SPDIF /*-2147418112*/:
                return DEVICE_OUT_SPDIF_NAME;
            case DEVICE_IN_BLUETOOTH_A2DP /*-2147352576*/:
                return DEVICE_OUT_BLUETOOTH_A2DP_NAME;
            case DEVICE_IN_LOOPBACK /*-2147221504*/:
                return DEVICE_IN_LOOPBACK_NAME;
            default:
                return Integer.toString(device);
        }
    }
}
