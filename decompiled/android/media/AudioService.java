package android.media;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.AppOpsManager;
import android.app.KeyguardManager;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.hardware.hdmi.HdmiControlManager;
import android.hardware.hdmi.HdmiPlaybackClient;
import android.hardware.hdmi.HdmiPlaybackClient.DisplayStatusCallback;
import android.hardware.hdmi.HdmiTvClient;
import android.media.AudioManagerInternal.RingerModeDelegate;
import android.media.AudioSystem.ErrorCallback;
import android.media.MediaPlayer.OnErrorListener;
import android.media.SoundPool.Builder;
import android.media.SoundPool.OnLoadCompleteListener;
import android.media.audiopolicy.AudioPolicyConfig;
import android.media.audiopolicy.IAudioPolicyCallback;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.Vibrator;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.MathUtils;
import android.util.Slog;
import android.util.TimedRemoteCaller;
import android.view.OrientationEventListener;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityManager.TouchExplorationStateChangeListener;
import com.android.internal.R;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.RILConstants;
import com.android.internal.util.Protocol;
import com.android.internal.widget.ExploreByTouchHelper;
import com.android.server.LocalServices;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javax.microedition.khronos.opengles.GL10;

public class AudioService extends IAudioService$Stub {
    private static final String ASSET_FILE_VERSION = "1.0";
    private static final String ATTR_ASSET_FILE = "file";
    private static final String ATTR_ASSET_ID = "id";
    private static final String ATTR_GROUP_NAME = "name";
    private static final String ATTR_VERSION = "version";
    private static final int BTA2DP_DOCK_TIMEOUT_MILLIS = 8000;
    private static final int BT_HEADSET_CNCT_TIMEOUT_MS = 3000;
    protected static final boolean DEBUG_AP = false;
    protected static final boolean DEBUG_MODE = false;
    private static final boolean DEBUG_SESSIONS = false;
    protected static final boolean DEBUG_VOL = false;
    private static int[] DEFAULT_STREAM_VOLUME = null;
    private static final int FLAG_ADJUST_VOLUME = 1;
    private static final String GROUP_TOUCH_SOUNDS = "touch_sounds";
    private static final int MAX_BATCH_VOLUME_ADJUST_STEPS = 4;
    private static final int MAX_MASTER_VOLUME = 100;
    private static int[] MAX_STREAM_VOLUME = null;
    private static final int MSG_BROADCAST_AUDIO_BECOMING_NOISY = 15;
    private static final int MSG_BROADCAST_BT_CONNECTION_STATE = 19;
    private static final int MSG_BTA2DP_DOCK_TIMEOUT = 6;
    private static final int MSG_BT_HEADSET_CNCT_FAILED = 9;
    private static final int MSG_CHECK_MUSIC_ACTIVE = 14;
    private static final int MSG_CONFIGURE_SAFE_MEDIA_VOLUME = 16;
    private static final int MSG_CONFIGURE_SAFE_MEDIA_VOLUME_FORCED = 17;
    private static final int MSG_LOAD_SOUND_EFFECTS = 7;
    private static final int MSG_MEDIA_SERVER_DIED = 4;
    private static final int MSG_PERSIST_MASTER_VOLUME = 2;
    private static final int MSG_PERSIST_MASTER_VOLUME_MUTE = 11;
    private static final int MSG_PERSIST_MICROPHONE_MUTE = 23;
    private static final int MSG_PERSIST_MUSIC_ACTIVE_MS = 22;
    private static final int MSG_PERSIST_RINGER_MODE = 3;
    private static final int MSG_PERSIST_SAFE_VOLUME_STATE = 18;
    private static final int MSG_PERSIST_VOLUME = 1;
    private static final int MSG_PLAY_SOUND_EFFECT = 5;
    private static final int MSG_REPORT_NEW_ROUTES = 12;
    private static final int MSG_SET_A2DP_SINK_CONNECTION_STATE = 102;
    private static final int MSG_SET_A2DP_SRC_CONNECTION_STATE = 101;
    private static final int MSG_SET_ALL_VOLUMES = 10;
    private static final int MSG_SET_DEVICE_VOLUME = 0;
    private static final int MSG_SET_FORCE_BT_A2DP_USE = 13;
    private static final int MSG_SET_FORCE_USE = 8;
    private static final int MSG_SET_WIRED_DEVICE_CONNECTION_STATE = 100;
    private static final int MSG_SYSTEM_READY = 21;
    private static final int MSG_UNLOAD_SOUND_EFFECTS = 20;
    private static final int MUSIC_ACTIVE_POLL_PERIOD_MS = 60000;
    private static final int NUM_SOUNDPOOL_CHANNELS = 4;
    private static final int PERSIST_DELAY = 500;
    private static final int PLATFORM_DEFAULT = 0;
    private static final int PLATFORM_TELEVISION = 2;
    private static final int PLATFORM_VOICE = 1;
    public static final int PLAY_SOUND_DELAY = 300;
    private static final boolean PREVENT_VOLUME_ADJUSTMENT_IF_SILENT = true;
    private static final String[] RINGER_MODE_NAMES = null;
    private static final int SAFE_MEDIA_VOLUME_ACTIVE = 3;
    private static final int SAFE_MEDIA_VOLUME_DISABLED = 1;
    private static final int SAFE_MEDIA_VOLUME_INACTIVE = 2;
    private static final int SAFE_MEDIA_VOLUME_NOT_CONFIGURED = 0;
    private static final int SAFE_VOLUME_CONFIGURE_TIMEOUT_MS = 30000;
    private static final int SCO_MODE_MAX = 2;
    private static final int SCO_MODE_RAW = 1;
    private static final int SCO_MODE_UNDEFINED = -1;
    private static final int SCO_MODE_VIRTUAL_CALL = 0;
    private static final int SCO_MODE_VR = 2;
    private static final int SCO_STATE_ACTIVATE_REQ = 1;
    private static final int SCO_STATE_ACTIVE_EXTERNAL = 2;
    private static final int SCO_STATE_ACTIVE_INTERNAL = 3;
    private static final int SCO_STATE_DEACTIVATE_EXT_REQ = 4;
    private static final int SCO_STATE_DEACTIVATE_REQ = 5;
    private static final int SCO_STATE_INACTIVE = 0;
    private static final int SENDMSG_NOOP = 1;
    private static final int SENDMSG_QUEUE = 2;
    private static final int SENDMSG_REPLACE = 0;
    private static final int SOUND_EFFECTS_LOAD_TIMEOUT_MS = 5000;
    private static final String SOUND_EFFECTS_PATH = "/media/audio/ui/";
    private static final List<String> SOUND_EFFECT_FILES = null;
    private static final int[] STEAM_VOLUME_OPS = null;
    private static final String[] STREAM_NAMES = null;
    private static final String TAG = "AudioService";
    private static final String TAG_ASSET = "asset";
    private static final String TAG_AUDIO_ASSETS = "audio_assets";
    private static final String TAG_GROUP = "group";
    private static final int UNSAFE_VOLUME_MUSIC_ACTIVE_MS_MAX = 72000000;
    private static final boolean VOLUME_SETS_RINGER_MODE_SILENT = false;
    private static Long mLastDeviceConnectMsgTime;
    private static int sSoundEffectVolumeDb;
    private final int[][] SOUND_EFFECT_FILES_MAP;
    private final int[] STREAM_VOLUME_ALIAS_DEFAULT;
    private final int[] STREAM_VOLUME_ALIAS_TELEVISION;
    private final int[] STREAM_VOLUME_ALIAS_VOICE;
    private BluetoothA2dp mA2dp;
    private final Object mA2dpAvrcpLock;
    private final AppOpsManager mAppOps;
    private WakeLock mAudioEventWakeLock;
    private AudioHandler mAudioHandler;
    private HashMap<IBinder, AudioPolicyProxy> mAudioPolicies;
    private int mAudioPolicyCounter;
    private final ErrorCallback mAudioSystemCallback;
    private AudioSystemThread mAudioSystemThread;
    private boolean mAvrcpAbsVolSupported;
    int mBecomingNoisyIntentDevices;
    private boolean mBluetoothA2dpEnabled;
    private final Object mBluetoothA2dpEnabledLock;
    private BluetoothHeadset mBluetoothHeadset;
    private BluetoothDevice mBluetoothHeadsetDevice;
    private ServiceListener mBluetoothProfileServiceListener;
    private Boolean mCameraSoundForced;
    private final HashMap<Integer, String> mConnectedDevices;
    private final ContentResolver mContentResolver;
    private final Context mContext;
    final AudioRoutesInfo mCurAudioRoutes;
    private int mDeviceOrientation;
    private int mDeviceRotation;
    private String mDockAddress;
    private boolean mDockAudioMediaEnabled;
    private int mDockState;
    int mFixedVolumeDevices;
    private ForceControlStreamClient mForceControlStreamClient;
    private final Object mForceControlStreamLock;
    private int mForcedUseForComm;
    int mFullVolumeDevices;
    private final boolean mHasVibrator;
    private boolean mHdmiCecSink;
    private MyDisplayStatusCallback mHdmiDisplayStatusCallback;
    private HdmiControlManager mHdmiManager;
    private HdmiPlaybackClient mHdmiPlaybackClient;
    private boolean mHdmiSystemAudioSupported;
    private HdmiTvClient mHdmiTvClient;
    private KeyguardManager mKeyguardManager;
    private final int[] mMasterVolumeRamp;
    private int mMcc;
    private final MediaFocusControl mMediaFocusControl;
    private int mMode;
    private final boolean mMonitorOrientation;
    private final boolean mMonitorRotation;
    private int mMusicActiveMs;
    private int mMuteAffectedStreams;
    private AudioOrientationEventListener mOrientationListener;
    private StreamVolumeCommand mPendingVolumeCommand;
    private final int mPlatformType;
    private int mPrevVolDirection;
    private final BroadcastReceiver mReceiver;
    private int mRingerMode;
    private int mRingerModeAffectedStreams;
    private RingerModeDelegate mRingerModeDelegate;
    private int mRingerModeExternal;
    private int mRingerModeMutedStreams;
    private volatile IRingtonePlayer mRingtonePlayer;
    private ArrayList<RmtSbmxFullVolDeathHandler> mRmtSbmxFullVolDeathHandlers;
    private int mRmtSbmxFullVolRefCount;
    final RemoteCallbackList<IAudioRoutesObserver> mRoutesObservers;
    private final int mSafeMediaVolumeDevices;
    private int mSafeMediaVolumeIndex;
    private Integer mSafeMediaVolumeState;
    private int mScoAudioMode;
    private int mScoAudioState;
    private final ArrayList<ScoClient> mScoClients;
    private int mScoConnectionState;
    private final ArrayList<SetModeDeathHandler> mSetModeDeathHandlers;
    private final Object mSettingsLock;
    private SettingsObserver mSettingsObserver;
    private final Object mSoundEffectsLock;
    private SoundPool mSoundPool;
    private SoundPoolCallback mSoundPoolCallBack;
    private SoundPoolListenerThread mSoundPoolListenerThread;
    private Looper mSoundPoolLooper;
    private VolumeStreamState[] mStreamStates;
    private int[] mStreamVolumeAlias;
    private boolean mSystemReady;
    private final boolean mUseFixedVolume;
    private final boolean mUseMasterVolume;
    private int mVibrateSetting;
    private int mVolumeControlStream;
    private final VolumeController mVolumeController;

    private class AudioHandler extends Handler {
        final /* synthetic */ AudioService this$0;

        /* renamed from: android.media.AudioService.AudioHandler.2 */
        class AnonymousClass2 implements OnErrorListener {
            final /* synthetic */ AudioHandler this$1;

            AnonymousClass2(android.media.AudioService.AudioHandler r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.AudioHandler.2.<init>(android.media.AudioService$AudioHandler):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.AudioHandler.2.<init>(android.media.AudioService$AudioHandler):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.AudioHandler.2.<init>(android.media.AudioService$AudioHandler):void");
            }

            public boolean onError(android.media.MediaPlayer r1, int r2, int r3) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.AudioHandler.2.onError(android.media.MediaPlayer, int, int):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.AudioHandler.2.onError(android.media.MediaPlayer, int, int):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.AudioHandler.2.onError(android.media.MediaPlayer, int, int):boolean");
            }
        }

        private AudioHandler(AudioService audioService) {
            this.this$0 = audioService;
        }

        /* synthetic */ AudioHandler(AudioService x0, AnonymousClass1 x1) {
            this(x0);
        }

        private void setDeviceVolume(VolumeStreamState streamState, int device) {
            synchronized (VolumeStreamState.class) {
                streamState.applyDeviceVolume_syncVSS(device);
                int streamType = AudioSystem.getNumStreamTypes() + AudioService.SCO_MODE_UNDEFINED;
                while (streamType >= 0) {
                    if (streamType != streamState.mStreamType && this.this$0.mStreamVolumeAlias[streamType] == streamState.mStreamType) {
                        int streamDevice = this.this$0.getDeviceForStream(streamType);
                        if (!(device == streamDevice || !this.this$0.mAvrcpAbsVolSupported || (device & AudioSystem.DEVICE_OUT_ALL_A2DP) == 0)) {
                            this.this$0.mStreamStates[streamType].applyDeviceVolume_syncVSS(device);
                        }
                        this.this$0.mStreamStates[streamType].applyDeviceVolume_syncVSS(streamDevice);
                    }
                    streamType += AudioService.SCO_MODE_UNDEFINED;
                }
            }
            AudioService.sendMsg(this.this$0.mAudioHandler, AudioService.SENDMSG_NOOP, AudioService.SENDMSG_QUEUE, device, AudioService.SENDMSG_REPLACE, streamState, AudioService.PERSIST_DELAY);
        }

        private void setAllVolumes(VolumeStreamState streamState) {
            streamState.applyAllVolumes();
            int streamType = AudioSystem.getNumStreamTypes() + AudioService.SCO_MODE_UNDEFINED;
            while (streamType >= 0) {
                if (streamType != streamState.mStreamType && this.this$0.mStreamVolumeAlias[streamType] == streamState.mStreamType) {
                    this.this$0.mStreamStates[streamType].applyAllVolumes();
                }
                streamType += AudioService.SCO_MODE_UNDEFINED;
            }
        }

        private void persistVolume(VolumeStreamState streamState, int device) {
            if (!this.this$0.mUseFixedVolume) {
                if (!this.this$0.isPlatformTelevision() || streamState.mStreamType == AudioService.SCO_STATE_ACTIVE_INTERNAL) {
                    System.putIntForUser(this.this$0.mContentResolver, streamState.getSettingNameForDevice(device), (streamState.getIndex(device) + AudioService.SCO_STATE_DEACTIVATE_REQ) / AudioService.MSG_SET_ALL_VOLUMES, -2);
                }
            }
        }

        private void persistRingerMode(int ringerMode) {
            if (!this.this$0.mUseFixedVolume) {
                Global.putInt(this.this$0.mContentResolver, "mode_ringer", ringerMode);
            }
        }

        private boolean onLoadSoundEffects() {
            synchronized (this.this$0.mSoundEffectsLock) {
                if (!this.this$0.mSystemReady) {
                    Log.w(AudioService.TAG, "onLoadSoundEffects() called before boot complete");
                    return AudioService.DEBUG_VOL;
                } else if (this.this$0.mSoundPool != null) {
                    return AudioService.PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
                } else {
                    int attempts;
                    this.this$0.loadTouchSoundAssets();
                    this.this$0.mSoundPool = new Builder().setMaxStreams(AudioService.SCO_STATE_DEACTIVATE_EXT_REQ).setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioService.MSG_SET_FORCE_BT_A2DP_USE).setContentType(AudioService.SCO_STATE_DEACTIVATE_EXT_REQ).build()).build();
                    this.this$0.mSoundPoolCallBack = null;
                    this.this$0.mSoundPoolListenerThread = new SoundPoolListenerThread(this.this$0);
                    this.this$0.mSoundPoolListenerThread.start();
                    int attempts2 = AudioService.SCO_STATE_ACTIVE_INTERNAL;
                    while (this.this$0.mSoundPoolCallBack == null) {
                        attempts = attempts2 + AudioService.SCO_MODE_UNDEFINED;
                        if (attempts2 <= 0) {
                            break;
                        }
                        try {
                            this.this$0.mSoundEffectsLock.wait(TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
                            attempts2 = attempts;
                        } catch (InterruptedException e) {
                            Log.w(AudioService.TAG, "Interrupted while waiting sound pool listener thread.");
                            attempts2 = attempts;
                        }
                    }
                    attempts = attempts2;
                    if (this.this$0.mSoundPoolCallBack == null) {
                        Log.w(AudioService.TAG, "onLoadSoundEffects() SoundPool listener or thread creation error");
                        if (this.this$0.mSoundPoolLooper != null) {
                            this.this$0.mSoundPoolLooper.quit();
                            this.this$0.mSoundPoolLooper = null;
                        }
                        this.this$0.mSoundPoolListenerThread = null;
                        this.this$0.mSoundPool.release();
                        this.this$0.mSoundPool = null;
                        return AudioService.DEBUG_VOL;
                    }
                    int effect;
                    int status;
                    int[] poolId = new int[AudioService.SOUND_EFFECT_FILES.size()];
                    for (int fileIdx = AudioService.SENDMSG_REPLACE; fileIdx < AudioService.SOUND_EFFECT_FILES.size(); fileIdx += AudioService.SENDMSG_NOOP) {
                        poolId[fileIdx] = AudioService.SCO_MODE_UNDEFINED;
                    }
                    int numSamples = AudioService.SENDMSG_REPLACE;
                    for (effect = AudioService.SENDMSG_REPLACE; effect < AudioService.MSG_SET_ALL_VOLUMES; effect += AudioService.SENDMSG_NOOP) {
                        if (this.this$0.SOUND_EFFECT_FILES_MAP[effect][AudioService.SENDMSG_NOOP] != 0) {
                            if (poolId[this.this$0.SOUND_EFFECT_FILES_MAP[effect][AudioService.SENDMSG_REPLACE]] == AudioService.SCO_MODE_UNDEFINED) {
                                String filePath = Environment.getRootDirectory() + AudioService.SOUND_EFFECTS_PATH + ((String) AudioService.SOUND_EFFECT_FILES.get(this.this$0.SOUND_EFFECT_FILES_MAP[effect][AudioService.SENDMSG_REPLACE]));
                                int sampleId = this.this$0.mSoundPool.load(filePath, (int) AudioService.SENDMSG_REPLACE);
                                if (sampleId <= 0) {
                                    Log.w(AudioService.TAG, "Soundpool could not load file: " + filePath);
                                } else {
                                    this.this$0.SOUND_EFFECT_FILES_MAP[effect][AudioService.SENDMSG_NOOP] = sampleId;
                                    poolId[this.this$0.SOUND_EFFECT_FILES_MAP[effect][AudioService.SENDMSG_REPLACE]] = sampleId;
                                    numSamples += AudioService.SENDMSG_NOOP;
                                }
                            } else {
                                this.this$0.SOUND_EFFECT_FILES_MAP[effect][AudioService.SENDMSG_NOOP] = poolId[this.this$0.SOUND_EFFECT_FILES_MAP[effect][AudioService.SENDMSG_REPLACE]];
                            }
                        }
                    }
                    if (numSamples > 0) {
                        this.this$0.mSoundPoolCallBack.setSamples(poolId);
                        status = AudioService.SENDMSG_NOOP;
                        attempts2 = AudioService.SCO_STATE_ACTIVE_INTERNAL;
                        while (status == AudioService.SENDMSG_NOOP) {
                            attempts = attempts2 + AudioService.SCO_MODE_UNDEFINED;
                            if (attempts2 <= 0) {
                                break;
                            }
                            try {
                                this.this$0.mSoundEffectsLock.wait(TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
                                status = this.this$0.mSoundPoolCallBack.status();
                                attempts2 = attempts;
                            } catch (InterruptedException e2) {
                                Log.w(AudioService.TAG, "Interrupted while waiting sound pool callback.");
                                attempts2 = attempts;
                            }
                        }
                    } else {
                        status = AudioService.SCO_MODE_UNDEFINED;
                    }
                    if (this.this$0.mSoundPoolLooper != null) {
                        this.this$0.mSoundPoolLooper.quit();
                        this.this$0.mSoundPoolLooper = null;
                    }
                    this.this$0.mSoundPoolListenerThread = null;
                    if (status != 0) {
                        Log.w(AudioService.TAG, "onLoadSoundEffects(), Error " + status + " while loading samples");
                        for (effect = AudioService.SENDMSG_REPLACE; effect < AudioService.MSG_SET_ALL_VOLUMES; effect += AudioService.SENDMSG_NOOP) {
                            if (this.this$0.SOUND_EFFECT_FILES_MAP[effect][AudioService.SENDMSG_NOOP] > 0) {
                                this.this$0.SOUND_EFFECT_FILES_MAP[effect][AudioService.SENDMSG_NOOP] = AudioService.SCO_MODE_UNDEFINED;
                            }
                        }
                        this.this$0.mSoundPool.release();
                        this.this$0.mSoundPool = null;
                    }
                    return status == 0 ? AudioService.PREVENT_VOLUME_ADJUSTMENT_IF_SILENT : AudioService.DEBUG_VOL;
                }
            }
        }

        private void onUnloadSoundEffects() {
            synchronized (this.this$0.mSoundEffectsLock) {
                if (this.this$0.mSoundPool == null) {
                    return;
                }
                int[] poolId = new int[AudioService.SOUND_EFFECT_FILES.size()];
                for (int fileIdx = AudioService.SENDMSG_REPLACE; fileIdx < AudioService.SOUND_EFFECT_FILES.size(); fileIdx += AudioService.SENDMSG_NOOP) {
                    poolId[fileIdx] = AudioService.SENDMSG_REPLACE;
                }
                int effect = AudioService.SENDMSG_REPLACE;
                while (effect < AudioService.MSG_SET_ALL_VOLUMES) {
                    if (this.this$0.SOUND_EFFECT_FILES_MAP[effect][AudioService.SENDMSG_NOOP] > 0 && poolId[this.this$0.SOUND_EFFECT_FILES_MAP[effect][AudioService.SENDMSG_REPLACE]] == 0) {
                        this.this$0.mSoundPool.unload(this.this$0.SOUND_EFFECT_FILES_MAP[effect][AudioService.SENDMSG_NOOP]);
                        this.this$0.SOUND_EFFECT_FILES_MAP[effect][AudioService.SENDMSG_NOOP] = AudioService.SCO_MODE_UNDEFINED;
                        poolId[this.this$0.SOUND_EFFECT_FILES_MAP[effect][AudioService.SENDMSG_REPLACE]] = AudioService.SCO_MODE_UNDEFINED;
                    }
                    effect += AudioService.SENDMSG_NOOP;
                }
                this.this$0.mSoundPool.release();
                this.this$0.mSoundPool = null;
            }
        }

        private void onPlaySoundEffect(int effectType, int volume) {
            synchronized (this.this$0.mSoundEffectsLock) {
                onLoadSoundEffects();
                if (this.this$0.mSoundPool == null) {
                    return;
                }
                float volFloat;
                if (volume < 0) {
                    volFloat = (float) Math.pow(10.0d, (double) (((float) AudioService.sSoundEffectVolumeDb) / 20.0f));
                } else {
                    volFloat = ((float) volume) / 1000.0f;
                }
                if (this.this$0.SOUND_EFFECT_FILES_MAP[effectType][AudioService.SENDMSG_NOOP] > 0) {
                    this.this$0.mSoundPool.play(this.this$0.SOUND_EFFECT_FILES_MAP[effectType][AudioService.SENDMSG_NOOP], volFloat, volFloat, AudioService.SENDMSG_REPLACE, AudioService.SENDMSG_REPLACE, RemoteControlClient.PLAYBACK_SPEED_1X);
                } else {
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(Environment.getRootDirectory() + AudioService.SOUND_EFFECTS_PATH + ((String) AudioService.SOUND_EFFECT_FILES.get(this.this$0.SOUND_EFFECT_FILES_MAP[effectType][AudioService.SENDMSG_REPLACE])));
                        mediaPlayer.setAudioStreamType(AudioService.SENDMSG_NOOP);
                        mediaPlayer.prepare();
                        mediaPlayer.setVolume(volFloat);
                        mediaPlayer.setOnCompletionListener(new 1(this));
                        mediaPlayer.setOnErrorListener(new AnonymousClass2(this));
                        mediaPlayer.start();
                    } catch (IOException ex) {
                        Log.w(AudioService.TAG, "MediaPlayer IOException: " + ex);
                    } catch (IllegalArgumentException ex2) {
                        Log.w(AudioService.TAG, "MediaPlayer IllegalArgumentException: " + ex2);
                    } catch (IllegalStateException ex3) {
                        Log.w(AudioService.TAG, "MediaPlayer IllegalStateException: " + ex3);
                    }
                }
            }
        }

        private void cleanupPlayer(MediaPlayer mp) {
            if (mp != null) {
                try {
                    mp.stop();
                    mp.release();
                } catch (IllegalStateException ex) {
                    Log.w(AudioService.TAG, "MediaPlayer IllegalStateException: " + ex);
                }
            }
        }

        private void setForceUse(int usage, int config) {
            AudioSystem.setForceUse(usage, config);
        }

        private void onPersistSafeVolumeState(int state) {
            Global.putInt(this.this$0.mContentResolver, "audio_safe_volume_state", state);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AudioService.SENDMSG_REPLACE /*0*/:
                    setDeviceVolume((VolumeStreamState) msg.obj, msg.arg1);
                case AudioService.SENDMSG_NOOP /*1*/:
                    persistVolume((VolumeStreamState) msg.obj, msg.arg1);
                case AudioService.SENDMSG_QUEUE /*2*/:
                    if (!this.this$0.mUseFixedVolume) {
                        System.putFloatForUser(this.this$0.mContentResolver, "volume_master", ((float) msg.arg1) / 1000.0f, -2);
                    }
                case AudioService.SCO_STATE_ACTIVE_INTERNAL /*3*/:
                    persistRingerMode(this.this$0.getRingerModeInternal());
                case AudioService.SCO_STATE_DEACTIVATE_EXT_REQ /*4*/:
                    if (this.this$0.mSystemReady && AudioSystem.checkAudioFlinger() == 0) {
                        Log.e(AudioService.TAG, "Media server started.");
                        AudioSystem.setParameters("restarting=true");
                        AudioService.readAndSetLowRamDevice();
                        synchronized (this.this$0.mConnectedDevices) {
                            for (Entry device : this.this$0.mConnectedDevices.entrySet()) {
                                AudioSystem.setDeviceConnectionState(((Integer) device.getKey()).intValue(), AudioService.SENDMSG_NOOP, (String) device.getValue());
                            }
                            break;
                        }
                        AudioSystem.setPhoneState(this.this$0.mMode);
                        AudioSystem.setForceUse(AudioService.SENDMSG_REPLACE, this.this$0.mForcedUseForComm);
                        AudioSystem.setForceUse(AudioService.SENDMSG_QUEUE, this.this$0.mForcedUseForComm);
                        AudioSystem.setForceUse(AudioService.SCO_STATE_DEACTIVATE_EXT_REQ, this.this$0.mCameraSoundForced.booleanValue() ? AudioService.MSG_PERSIST_MASTER_VOLUME_MUTE : AudioService.SENDMSG_REPLACE);
                        for (int streamType = AudioSystem.getNumStreamTypes() + AudioService.SCO_MODE_UNDEFINED; streamType >= 0; streamType += AudioService.SCO_MODE_UNDEFINED) {
                            VolumeStreamState streamState = this.this$0.mStreamStates[streamType];
                            AudioSystem.initStreamVolume(streamType, AudioService.SENDMSG_REPLACE, (streamState.mIndexMax + AudioService.SCO_STATE_DEACTIVATE_REQ) / AudioService.MSG_SET_ALL_VOLUMES);
                            streamState.applyAllVolumes();
                        }
                        this.this$0.setRingerModeInt(this.this$0.getRingerModeInternal(), AudioService.DEBUG_VOL);
                        this.this$0.restoreMasterVolume();
                        if (this.this$0.mMonitorOrientation) {
                            this.this$0.setOrientationForAudioSystem();
                        }
                        if (this.this$0.mMonitorRotation) {
                            this.this$0.setRotationForAudioSystem();
                        }
                        synchronized (this.this$0.mBluetoothA2dpEnabledLock) {
                            AudioSystem.setForceUse(AudioService.SENDMSG_NOOP, this.this$0.mBluetoothA2dpEnabled ? AudioService.SENDMSG_REPLACE : AudioService.MSG_SET_ALL_VOLUMES);
                            break;
                        }
                        synchronized (this.this$0.mSettingsLock) {
                            AudioSystem.setForceUse(AudioService.SCO_STATE_ACTIVE_INTERNAL, this.this$0.mDockAudioMediaEnabled ? AudioService.MSG_SET_FORCE_USE : AudioService.SENDMSG_REPLACE);
                            break;
                        }
                        if (this.this$0.mHdmiManager != null) {
                            synchronized (this.this$0.mHdmiManager) {
                                if (this.this$0.mHdmiTvClient != null) {
                                    this.this$0.setHdmiSystemAudioSupported(this.this$0.mHdmiSystemAudioSupported);
                                }
                                break;
                            }
                        }
                        synchronized (this.this$0.mAudioPolicies) {
                            for (AudioPolicyProxy policy : this.this$0.mAudioPolicies.values()) {
                                policy.connectMixes();
                            }
                            break;
                        }
                        AudioSystem.setParameters("restarting=false");
                        return;
                    }
                    Log.e(AudioService.TAG, "Media server died.");
                    AudioService.sendMsg(this.this$0.mAudioHandler, AudioService.SCO_STATE_DEACTIVATE_EXT_REQ, AudioService.SENDMSG_NOOP, AudioService.SENDMSG_REPLACE, AudioService.SENDMSG_REPLACE, null, AudioService.PERSIST_DELAY);
                case AudioService.SCO_STATE_DEACTIVATE_REQ /*5*/:
                    onPlaySoundEffect(msg.arg1, msg.arg2);
                case AudioService.MSG_BTA2DP_DOCK_TIMEOUT /*6*/:
                    synchronized (this.this$0.mConnectedDevices) {
                        this.this$0.makeA2dpDeviceUnavailableNow((String) msg.obj);
                        break;
                    }
                case AudioService.MSG_LOAD_SOUND_EFFECTS /*7*/:
                    boolean loaded = onLoadSoundEffects();
                    if (msg.obj != null) {
                        LoadSoundEffectReply reply = msg.obj;
                        synchronized (reply) {
                            reply.mStatus = loaded ? AudioService.SENDMSG_REPLACE : AudioService.SCO_MODE_UNDEFINED;
                            reply.notify();
                            break;
                        }
                    }
                case AudioService.MSG_SET_FORCE_USE /*8*/:
                case AudioService.MSG_SET_FORCE_BT_A2DP_USE /*13*/:
                    setForceUse(msg.arg1, msg.arg2);
                case AudioService.MSG_BT_HEADSET_CNCT_FAILED /*9*/:
                    this.this$0.resetBluetoothSco();
                case AudioService.MSG_SET_ALL_VOLUMES /*10*/:
                    setAllVolumes((VolumeStreamState) msg.obj);
                case AudioService.MSG_PERSIST_MASTER_VOLUME_MUTE /*11*/:
                    if (!this.this$0.mUseFixedVolume) {
                        System.putIntForUser(this.this$0.mContentResolver, "volume_master_mute", msg.arg1, msg.arg2);
                    }
                case AudioService.MSG_REPORT_NEW_ROUTES /*12*/:
                    int N = this.this$0.mRoutesObservers.beginBroadcast();
                    if (N > 0) {
                        AudioRoutesInfo audioRoutesInfo;
                        synchronized (this.this$0.mCurAudioRoutes) {
                            audioRoutesInfo = new AudioRoutesInfo(this.this$0.mCurAudioRoutes);
                            break;
                        }
                        while (N > 0) {
                            N += AudioService.SCO_MODE_UNDEFINED;
                            try {
                                ((IAudioRoutesObserver) this.this$0.mRoutesObservers.getBroadcastItem(N)).dispatchAudioRoutesChanged(audioRoutesInfo);
                            } catch (RemoteException e) {
                            }
                        }
                    }
                    this.this$0.mRoutesObservers.finishBroadcast();
                case AudioService.MSG_CHECK_MUSIC_ACTIVE /*14*/:
                    this.this$0.onCheckMusicActive();
                case AudioService.MSG_BROADCAST_AUDIO_BECOMING_NOISY /*15*/:
                    this.this$0.onSendBecomingNoisyIntent();
                case AudioService.MSG_CONFIGURE_SAFE_MEDIA_VOLUME /*16*/:
                case AudioService.MSG_CONFIGURE_SAFE_MEDIA_VOLUME_FORCED /*17*/:
                    this.this$0.onConfigureSafeVolume(msg.what == AudioService.MSG_CONFIGURE_SAFE_MEDIA_VOLUME_FORCED ? AudioService.PREVENT_VOLUME_ADJUSTMENT_IF_SILENT : AudioService.DEBUG_VOL);
                case AudioService.MSG_PERSIST_SAFE_VOLUME_STATE /*18*/:
                    onPersistSafeVolumeState(msg.arg1);
                case AudioService.MSG_BROADCAST_BT_CONNECTION_STATE /*19*/:
                    this.this$0.onBroadcastScoConnectionState(msg.arg1);
                case AudioService.MSG_UNLOAD_SOUND_EFFECTS /*20*/:
                    onUnloadSoundEffects();
                case AudioService.MSG_SYSTEM_READY /*21*/:
                    this.this$0.onSystemReady();
                case AudioService.MSG_PERSIST_MUSIC_ACTIVE_MS /*22*/:
                    Secure.putIntForUser(this.this$0.mContentResolver, "unsafe_volume_music_active_ms", msg.arg1, -2);
                case AudioService.MSG_PERSIST_MICROPHONE_MUTE /*23*/:
                    System.putIntForUser(this.this$0.mContentResolver, "microphone_mute", msg.arg1, msg.arg2);
                case AudioService.MSG_SET_WIRED_DEVICE_CONNECTION_STATE /*100*/:
                    this.this$0.onSetWiredDeviceConnectionState(msg.arg1, msg.arg2, (String) msg.obj);
                    this.this$0.mAudioEventWakeLock.release();
                case AudioService.MSG_SET_A2DP_SRC_CONNECTION_STATE /*101*/:
                    this.this$0.onSetA2dpSourceConnectionState((BluetoothDevice) msg.obj, msg.arg1);
                    this.this$0.mAudioEventWakeLock.release();
                case AudioService.MSG_SET_A2DP_SINK_CONNECTION_STATE /*102*/:
                    this.this$0.onSetA2dpSinkConnectionState((BluetoothDevice) msg.obj, msg.arg1);
                    this.this$0.mAudioEventWakeLock.release();
                default:
            }
        }
    }

    private class AudioOrientationEventListener extends OrientationEventListener {
        final /* synthetic */ AudioService this$0;

        public AudioOrientationEventListener(android.media.AudioService r1, android.content.Context r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.AudioOrientationEventListener.<init>(android.media.AudioService, android.content.Context):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.AudioOrientationEventListener.<init>(android.media.AudioService, android.content.Context):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.AudioOrientationEventListener.<init>(android.media.AudioService, android.content.Context):void");
        }

        public void onOrientationChanged(int r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.AudioOrientationEventListener.onOrientationChanged(int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.AudioOrientationEventListener.onOrientationChanged(int):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.AudioOrientationEventListener.onOrientationChanged(int):void");
        }
    }

    public class AudioPolicyProxy extends AudioPolicyConfig implements DeathRecipient {
        private static final String TAG = "AudioPolicyProxy";
        AudioPolicyConfig mConfig;
        int mFocusDuckBehavior;
        boolean mHasFocusListener;
        IAudioPolicyCallback mPolicyToken;
        final /* synthetic */ AudioService this$0;

        AudioPolicyProxy(android.media.AudioService r1, android.media.audiopolicy.AudioPolicyConfig r2, android.media.audiopolicy.IAudioPolicyCallback r3, boolean r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.AudioPolicyProxy.<init>(android.media.AudioService, android.media.audiopolicy.AudioPolicyConfig, android.media.audiopolicy.IAudioPolicyCallback, boolean):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.AudioPolicyProxy.<init>(android.media.AudioService, android.media.audiopolicy.AudioPolicyConfig, android.media.audiopolicy.IAudioPolicyCallback, boolean):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.AudioPolicyProxy.<init>(android.media.AudioService, android.media.audiopolicy.AudioPolicyConfig, android.media.audiopolicy.IAudioPolicyCallback, boolean):void");
        }

        public void binderDied() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.AudioPolicyProxy.binderDied():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.AudioPolicyProxy.binderDied():void
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.AudioPolicyProxy.binderDied():void");
        }

        void connectMixes() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.AudioPolicyProxy.connectMixes():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.AudioPolicyProxy.connectMixes():void
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.AudioPolicyProxy.connectMixes():void");
        }

        java.lang.String getRegistrationId() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.AudioPolicyProxy.getRegistrationId():java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.AudioPolicyProxy.getRegistrationId():java.lang.String
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.AudioPolicyProxy.getRegistrationId():java.lang.String");
        }

        void release() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.AudioPolicyProxy.release():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.AudioPolicyProxy.release():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.AudioPolicyProxy.release():void");
        }
    }

    private class AudioServiceBroadcastReceiver extends BroadcastReceiver {
        final /* synthetic */ AudioService this$0;

        private AudioServiceBroadcastReceiver(AudioService audioService) {
            this.this$0 = audioService;
        }

        /* synthetic */ AudioServiceBroadcastReceiver(AudioService x0, AnonymousClass1 x1) {
            this(x0);
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r33, android.content.Intent r34) {
            /*
            r32 = this;
            r10 = r34.getAction();
            r3 = "android.intent.action.DOCK_EVENT";
            r3 = r10.equals(r3);
            if (r3 == 0) goto L_0x0048;
        L_0x000c:
            r3 = "android.intent.extra.DOCK_STATE";
            r4 = 0;
            r0 = r34;
            r20 = r0.getIntExtra(r3, r4);
            switch(r20) {
                case 1: goto L_0x003c;
                case 2: goto L_0x003f;
                case 3: goto L_0x0042;
                case 4: goto L_0x0045;
                default: goto L_0x0018;
            };
        L_0x0018:
            r18 = 0;
        L_0x001a:
            r3 = 3;
            r0 = r20;
            if (r0 == r3) goto L_0x0032;
        L_0x001f:
            if (r20 != 0) goto L_0x002c;
        L_0x0021:
            r0 = r32;
            r3 = r0.this$0;
            r3 = r3.mDockState;
            r4 = 3;
            if (r3 == r4) goto L_0x0032;
        L_0x002c:
            r3 = 3;
            r0 = r18;
            android.media.AudioSystem.setForceUse(r3, r0);
        L_0x0032:
            r0 = r32;
            r3 = r0.this$0;
            r0 = r20;
            r3.mDockState = r0;
        L_0x003b:
            return;
        L_0x003c:
            r18 = 7;
            goto L_0x001a;
        L_0x003f:
            r18 = 6;
            goto L_0x001a;
        L_0x0042:
            r18 = 8;
            goto L_0x001a;
        L_0x0045:
            r18 = 9;
            goto L_0x001a;
        L_0x0048:
            r3 = "android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED";
            r3 = r10.equals(r3);
            if (r3 == 0) goto L_0x00e1;
        L_0x0050:
            r3 = "android.bluetooth.profile.extra.STATE";
            r4 = 0;
            r0 = r34;
            r30 = r0.getIntExtra(r3, r4);
            r27 = 16;
            r24 = -2147483640; // 0xffffffff80000008 float:-1.1E-44 double:NaN;
            r11 = 0;
            r3 = "android.bluetooth.device.extra.DEVICE";
            r0 = r34;
            r16 = r0.getParcelableExtra(r3);
            r16 = (android.bluetooth.BluetoothDevice) r16;
            if (r16 == 0) goto L_0x003b;
        L_0x006b:
            r11 = r16.getAddress();
            r15 = r16.getBluetoothClass();
            if (r15 == 0) goto L_0x007c;
        L_0x0075:
            r3 = r15.getDeviceClass();
            switch(r3) {
                case 1028: goto L_0x00c5;
                case 1032: goto L_0x00c5;
                case 1056: goto L_0x00c8;
                default: goto L_0x007c;
            };
        L_0x007c:
            r3 = android.bluetooth.BluetoothAdapter.checkBluetoothAddress(r11);
            if (r3 != 0) goto L_0x0084;
        L_0x0082:
            r11 = "";
        L_0x0084:
            r3 = 2;
            r0 = r30;
            if (r0 != r3) goto L_0x00cb;
        L_0x0089:
            r19 = 1;
        L_0x008b:
            r0 = r32;
            r3 = r0.this$0;
            r0 = r19;
            r1 = r27;
            r3 = r3.handleDeviceConnection(r0, r1, r11);
            if (r3 == 0) goto L_0x00ce;
        L_0x0099:
            r0 = r32;
            r3 = r0.this$0;
            r0 = r19;
            r1 = r24;
            r3 = r3.handleDeviceConnection(r0, r1, r11);
            if (r3 == 0) goto L_0x00ce;
        L_0x00a7:
            r31 = 1;
        L_0x00a9:
            if (r31 == 0) goto L_0x003b;
        L_0x00ab:
            r0 = r32;
            r3 = r0.this$0;
            r4 = r3.mScoClients;
            monitor-enter(r4);
            if (r19 == 0) goto L_0x00d1;
        L_0x00b6:
            r0 = r32;
            r3 = r0.this$0;	 Catch:{ all -> 0x00c2 }
            r0 = r16;
            r3.mBluetoothHeadsetDevice = r0;	 Catch:{ all -> 0x00c2 }
        L_0x00bf:
            monitor-exit(r4);	 Catch:{ all -> 0x00c2 }
            goto L_0x003b;
        L_0x00c2:
            r3 = move-exception;
            monitor-exit(r4);	 Catch:{ all -> 0x00c2 }
            throw r3;
        L_0x00c5:
            r27 = 32;
            goto L_0x007c;
        L_0x00c8:
            r27 = 64;
            goto L_0x007c;
        L_0x00cb:
            r19 = 0;
            goto L_0x008b;
        L_0x00ce:
            r31 = 0;
            goto L_0x00a9;
        L_0x00d1:
            r0 = r32;
            r3 = r0.this$0;	 Catch:{ all -> 0x00c2 }
            r5 = 0;
            r3.mBluetoothHeadsetDevice = r5;	 Catch:{ all -> 0x00c2 }
            r0 = r32;
            r3 = r0.this$0;	 Catch:{ all -> 0x00c2 }
            r3.resetBluetoothSco();	 Catch:{ all -> 0x00c2 }
            goto L_0x00bf;
        L_0x00e1:
            r3 = "android.media.action.USB_AUDIO_ACCESSORY_PLUG";
            r3 = r10.equals(r3);
            if (r3 == 0) goto L_0x013b;
        L_0x00e9:
            r3 = "state";
            r4 = 0;
            r0 = r34;
            r30 = r0.getIntExtra(r3, r4);
            r3 = "card";
            r4 = -1;
            r0 = r34;
            r12 = r0.getIntExtra(r3, r4);
            r3 = "device";
            r4 = -1;
            r0 = r34;
            r13 = r0.getIntExtra(r3, r4);
            r3 = -1;
            if (r12 != r3) goto L_0x011d;
        L_0x0107:
            r3 = -1;
            if (r13 != r3) goto L_0x011d;
        L_0x010a:
            r28 = "";
        L_0x010c:
            r27 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;
            r0 = r32;
            r3 = r0.this$0;
            r0 = r27;
            r1 = r30;
            r2 = r28;
            r3.setWiredDeviceConnectionState(r0, r1, r2);
            goto L_0x003b;
        L_0x011d:
            r3 = new java.lang.StringBuilder;
            r3.<init>();
            r4 = "card=";
            r3 = r3.append(r4);
            r3 = r3.append(r12);
            r4 = ";device=";
            r3 = r3.append(r4);
            r3 = r3.append(r13);
            r28 = r3.toString();
            goto L_0x010c;
        L_0x013b:
            r3 = "android.media.action.USB_AUDIO_DEVICE_PLUG";
            r3 = r10.equals(r3);
            if (r3 == 0) goto L_0x01d5;
        L_0x0143:
            r0 = r32;
            r3 = r0.this$0;
            r3 = r3.mContentResolver;
            r4 = "usb_audio_automatic_routing_disabled";
            r5 = 0;
            r25 = android.provider.Settings.Secure.getInt(r3, r4, r5);
            if (r25 != 0) goto L_0x003b;
        L_0x0154:
            r3 = "state";
            r4 = 0;
            r0 = r34;
            r30 = r0.getIntExtra(r3, r4);
            r3 = "card";
            r4 = -1;
            r0 = r34;
            r12 = r0.getIntExtra(r3, r4);
            r3 = "device";
            r4 = -1;
            r0 = r34;
            r13 = r0.getIntExtra(r3, r4);
            r3 = "hasPlayback";
            r4 = 0;
            r0 = r34;
            r23 = r0.getBooleanExtra(r3, r4);
            r3 = "hasCapture";
            r4 = 0;
            r0 = r34;
            r21 = r0.getBooleanExtra(r3, r4);
            r3 = "hasMIDI";
            r4 = 0;
            r0 = r34;
            r22 = r0.getBooleanExtra(r3, r4);
            r3 = -1;
            if (r12 != r3) goto L_0x01b7;
        L_0x018d:
            r3 = -1;
            if (r13 != r3) goto L_0x01b7;
        L_0x0190:
            r28 = "";
        L_0x0192:
            if (r23 == 0) goto L_0x01a3;
        L_0x0194:
            r27 = 16384; // 0x4000 float:2.2959E-41 double:8.0948E-320;
            r0 = r32;
            r3 = r0.this$0;
            r0 = r27;
            r1 = r30;
            r2 = r28;
            r3.setWiredDeviceConnectionState(r0, r1, r2);
        L_0x01a3:
            if (r21 == 0) goto L_0x003b;
        L_0x01a5:
            r24 = -2147479552; // 0xffffffff80001000 float:-5.74E-42 double:NaN;
            r0 = r32;
            r3 = r0.this$0;
            r0 = r24;
            r1 = r30;
            r2 = r28;
            r3.setWiredDeviceConnectionState(r0, r1, r2);
            goto L_0x003b;
        L_0x01b7:
            r3 = new java.lang.StringBuilder;
            r3.<init>();
            r4 = "card=";
            r3 = r3.append(r4);
            r3 = r3.append(r12);
            r4 = ";device=";
            r3 = r3.append(r4);
            r3 = r3.append(r13);
            r28 = r3.toString();
            goto L_0x0192;
        L_0x01d5:
            r3 = "android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED";
            r3 = r10.equals(r3);
            if (r3 == 0) goto L_0x02bd;
        L_0x01dd:
            r14 = 0;
            r29 = -1;
            r0 = r32;
            r3 = r0.this$0;
            r4 = r3.mScoClients;
            monitor-enter(r4);
            r3 = "android.bluetooth.profile.extra.STATE";
            r5 = -1;
            r0 = r34;
            r17 = r0.getIntExtra(r3, r5);	 Catch:{ all -> 0x027b }
            r0 = r32;
            r3 = r0.this$0;	 Catch:{ all -> 0x027b }
            r3 = r3.mScoClients;	 Catch:{ all -> 0x027b }
            r3 = r3.isEmpty();	 Catch:{ all -> 0x027b }
            if (r3 != 0) goto L_0x0222;
        L_0x0200:
            r0 = r32;
            r3 = r0.this$0;	 Catch:{ all -> 0x027b }
            r3 = r3.mScoAudioState;	 Catch:{ all -> 0x027b }
            r5 = 3;
            if (r3 == r5) goto L_0x0221;
        L_0x020b:
            r0 = r32;
            r3 = r0.this$0;	 Catch:{ all -> 0x027b }
            r3 = r3.mScoAudioState;	 Catch:{ all -> 0x027b }
            r5 = 1;
            if (r3 == r5) goto L_0x0221;
        L_0x0216:
            r0 = r32;
            r3 = r0.this$0;	 Catch:{ all -> 0x027b }
            r3 = r3.mScoAudioState;	 Catch:{ all -> 0x027b }
            r5 = 5;
            if (r3 != r5) goto L_0x0222;
        L_0x0221:
            r14 = 1;
        L_0x0222:
            switch(r17) {
                case 10: goto L_0x027e;
                case 11: goto L_0x0292;
                case 12: goto L_0x024f;
                default: goto L_0x0225;
            };	 Catch:{ all -> 0x027b }
        L_0x0225:
            r14 = 0;
        L_0x0226:
            monitor-exit(r4);	 Catch:{ all -> 0x027b }
            if (r14 == 0) goto L_0x003b;
        L_0x0229:
            r0 = r32;
            r3 = r0.this$0;
            r0 = r29;
            r3.broadcastScoConnectionState(r0);
            r26 = new android.content.Intent;
            r3 = "android.media.SCO_AUDIO_STATE_CHANGED";
            r0 = r26;
            r0.<init>(r3);
            r3 = "android.media.extra.SCO_AUDIO_STATE";
            r0 = r26;
            r1 = r29;
            r0.putExtra(r3, r1);
            r0 = r32;
            r3 = r0.this$0;
            r0 = r26;
            r3.sendStickyBroadcastToAll(r0);
            goto L_0x003b;
        L_0x024f:
            r29 = 1;
            r0 = r32;
            r3 = r0.this$0;	 Catch:{ all -> 0x027b }
            r3 = r3.mScoAudioState;	 Catch:{ all -> 0x027b }
            r5 = 3;
            if (r3 == r5) goto L_0x0226;
        L_0x025c:
            r0 = r32;
            r3 = r0.this$0;	 Catch:{ all -> 0x027b }
            r3 = r3.mScoAudioState;	 Catch:{ all -> 0x027b }
            r5 = 5;
            if (r3 == r5) goto L_0x0226;
        L_0x0267:
            r0 = r32;
            r3 = r0.this$0;	 Catch:{ all -> 0x027b }
            r3 = r3.mScoAudioState;	 Catch:{ all -> 0x027b }
            r5 = 4;
            if (r3 == r5) goto L_0x0226;
        L_0x0272:
            r0 = r32;
            r3 = r0.this$0;	 Catch:{ all -> 0x027b }
            r5 = 2;
            r3.mScoAudioState = r5;	 Catch:{ all -> 0x027b }
            goto L_0x0226;
        L_0x027b:
            r3 = move-exception;
            monitor-exit(r4);	 Catch:{ all -> 0x027b }
            throw r3;
        L_0x027e:
            r29 = 0;
            r0 = r32;
            r3 = r0.this$0;	 Catch:{ all -> 0x027b }
            r5 = 0;
            r3.mScoAudioState = r5;	 Catch:{ all -> 0x027b }
            r0 = r32;
            r3 = r0.this$0;	 Catch:{ all -> 0x027b }
            r5 = 0;
            r6 = 0;
            r3.clearAllScoClients(r5, r6);	 Catch:{ all -> 0x027b }
            goto L_0x0226;
        L_0x0292:
            r0 = r32;
            r3 = r0.this$0;	 Catch:{ all -> 0x027b }
            r3 = r3.mScoAudioState;	 Catch:{ all -> 0x027b }
            r5 = 3;
            if (r3 == r5) goto L_0x0225;
        L_0x029d:
            r0 = r32;
            r3 = r0.this$0;	 Catch:{ all -> 0x027b }
            r3 = r3.mScoAudioState;	 Catch:{ all -> 0x027b }
            r5 = 5;
            if (r3 == r5) goto L_0x0225;
        L_0x02a8:
            r0 = r32;
            r3 = r0.this$0;	 Catch:{ all -> 0x027b }
            r3 = r3.mScoAudioState;	 Catch:{ all -> 0x027b }
            r5 = 4;
            if (r3 == r5) goto L_0x0225;
        L_0x02b3:
            r0 = r32;
            r3 = r0.this$0;	 Catch:{ all -> 0x027b }
            r5 = 2;
            r3.mScoAudioState = r5;	 Catch:{ all -> 0x027b }
            goto L_0x0225;
        L_0x02bd:
            r3 = "android.intent.action.SCREEN_ON";
            r3 = r10.equals(r3);
            if (r3 == 0) goto L_0x02ed;
        L_0x02c5:
            r0 = r32;
            r3 = r0.this$0;
            r3 = r3.mMonitorRotation;
            if (r3 == 0) goto L_0x02e6;
        L_0x02cf:
            r0 = r32;
            r3 = r0.this$0;
            r3 = r3.mOrientationListener;
            r4 = 0;
            r3.onOrientationChanged(r4);
            r0 = r32;
            r3 = r0.this$0;
            r3 = r3.mOrientationListener;
            r3.enable();
        L_0x02e6:
            r3 = "screen_state=on";
            android.media.AudioSystem.setParameters(r3);
            goto L_0x003b;
        L_0x02ed:
            r3 = "android.intent.action.SCREEN_OFF";
            r3 = r10.equals(r3);
            if (r3 == 0) goto L_0x0311;
        L_0x02f5:
            r0 = r32;
            r3 = r0.this$0;
            r3 = r3.mMonitorRotation;
            if (r3 == 0) goto L_0x030a;
        L_0x02ff:
            r0 = r32;
            r3 = r0.this$0;
            r3 = r3.mOrientationListener;
            r3.disable();
        L_0x030a:
            r3 = "screen_state=off";
            android.media.AudioSystem.setParameters(r3);
            goto L_0x003b;
        L_0x0311:
            r3 = "android.intent.action.CONFIGURATION_CHANGED";
            r3 = r10.equals(r3);
            if (r3 == 0) goto L_0x0324;
        L_0x0319:
            r0 = r32;
            r3 = r0.this$0;
            r0 = r33;
            r3.handleConfigurationChanged(r0);
            goto L_0x003b;
        L_0x0324:
            r3 = "android.intent.action.USER_SWITCHED";
            r3 = r10.equals(r3);
            if (r3 == 0) goto L_0x003b;
        L_0x032c:
            r0 = r32;
            r3 = r0.this$0;
            r3 = r3.mAudioHandler;
            r4 = 15;
            r5 = 0;
            r6 = 0;
            r7 = 0;
            r8 = 0;
            r9 = 0;
            android.media.AudioService.sendMsg(r3, r4, r5, r6, r7, r8, r9);
            r0 = r32;
            r3 = r0.this$0;
            r3 = r3.mMediaFocusControl;
            r3.discardAudioFocusOwner();
            r0 = r32;
            r3 = r0.this$0;
            r4 = 1;
            r3.readAudioSettings(r4);
            r0 = r32;
            r3 = r0.this$0;
            r3 = r3.mAudioHandler;
            r4 = 10;
            r5 = 2;
            r6 = 0;
            r7 = 0;
            r0 = r32;
            r8 = r0.this$0;
            r8 = r8.mStreamStates;
            r9 = 3;
            r8 = r8[r9];
            r9 = 0;
            android.media.AudioService.sendMsg(r3, r4, r5, r6, r7, r8, r9);
            goto L_0x003b;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.AudioServiceBroadcastReceiver.onReceive(android.content.Context, android.content.Intent):void");
        }
    }

    final class AudioServiceInternal extends AudioManagerInternal {
        final /* synthetic */ AudioService this$0;

        AudioServiceInternal(AudioService audioService) {
            this.this$0 = audioService;
        }

        public void setRingerModeDelegate(RingerModeDelegate delegate) {
            this.this$0.mRingerModeDelegate = delegate;
            if (this.this$0.mRingerModeDelegate != null) {
                setRingerModeInternal(getRingerModeInternal(), "AudioService.setRingerModeDelegate");
            }
        }

        public void adjustSuggestedStreamVolumeForUid(int streamType, int direction, int flags, String callingPackage, int uid) {
            this.this$0.adjustSuggestedStreamVolume(direction, streamType, flags, callingPackage, uid);
        }

        public void adjustStreamVolumeForUid(int streamType, int direction, int flags, String callingPackage, int uid) {
            this.this$0.adjustStreamVolume(streamType, direction, flags, callingPackage, uid);
        }

        public void setStreamVolumeForUid(int streamType, int direction, int flags, String callingPackage, int uid) {
            this.this$0.setStreamVolume(streamType, direction, flags, callingPackage, uid);
        }

        public void adjustMasterVolumeForUid(int steps, int flags, String callingPackage, int uid) {
            this.this$0.adjustMasterVolume(steps, flags, callingPackage, uid);
        }

        public int getRingerModeInternal() {
            return this.this$0.getRingerModeInternal();
        }

        public void setRingerModeInternal(int ringerMode, String caller) {
            this.this$0.setRingerModeInternal(ringerMode, caller);
        }

        public void setMasterMuteForUid(boolean state, int flags, String callingPackage, IBinder cb, int uid) {
            this.this$0.setMasterMuteInternal(state, flags, callingPackage, cb, uid);
        }
    }

    private class AudioSystemThread extends Thread {
        final /* synthetic */ AudioService this$0;

        AudioSystemThread(AudioService audioService) {
            this.this$0 = audioService;
            super(AudioService.TAG);
        }

        public void run() {
            Looper.prepare();
            synchronized (this.this$0) {
                this.this$0.mAudioHandler = new AudioHandler(this.this$0, null);
                this.this$0.notify();
            }
            Looper.loop();
        }
    }

    private class ForceControlStreamClient implements DeathRecipient {
        private IBinder mCb;
        final /* synthetic */ AudioService this$0;

        ForceControlStreamClient(android.media.AudioService r1, android.os.IBinder r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.ForceControlStreamClient.<init>(android.media.AudioService, android.os.IBinder):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.ForceControlStreamClient.<init>(android.media.AudioService, android.os.IBinder):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.ForceControlStreamClient.<init>(android.media.AudioService, android.os.IBinder):void");
        }

        public void binderDied() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.ForceControlStreamClient.binderDied():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.ForceControlStreamClient.binderDied():void
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.ForceControlStreamClient.binderDied():void");
        }

        public void release() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.ForceControlStreamClient.release():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.ForceControlStreamClient.release():void
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.ForceControlStreamClient.release():void");
        }
    }

    private class MyDisplayStatusCallback implements DisplayStatusCallback {
        final /* synthetic */ AudioService this$0;

        private MyDisplayStatusCallback(AudioService audioService) {
            this.this$0 = audioService;
        }

        /* synthetic */ MyDisplayStatusCallback(AudioService x0, AnonymousClass1 x1) {
            this(x0);
        }

        public void onComplete(int status) {
            if (this.this$0.mHdmiManager != null) {
                synchronized (this.this$0.mHdmiManager) {
                    this.this$0.mHdmiCecSink = status != AudioService.SCO_MODE_UNDEFINED ? AudioService.PREVENT_VOLUME_ADJUSTMENT_IF_SILENT : AudioService.DEBUG_VOL;
                    if (this.this$0.isPlatformTelevision() && !this.this$0.mHdmiCecSink) {
                        AudioService audioService = this.this$0;
                        audioService.mFixedVolumeDevices &= -1025;
                    }
                    this.this$0.checkAllFixedVolumeDevices();
                }
            }
        }
    }

    private class RmtSbmxFullVolDeathHandler implements DeathRecipient {
        private IBinder mICallback;
        final /* synthetic */ AudioService this$0;

        RmtSbmxFullVolDeathHandler(android.media.AudioService r1, android.os.IBinder r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.RmtSbmxFullVolDeathHandler.<init>(android.media.AudioService, android.os.IBinder):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.RmtSbmxFullVolDeathHandler.<init>(android.media.AudioService, android.os.IBinder):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.RmtSbmxFullVolDeathHandler.<init>(android.media.AudioService, android.os.IBinder):void");
        }

        public void binderDied() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.RmtSbmxFullVolDeathHandler.binderDied():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.RmtSbmxFullVolDeathHandler.binderDied():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.RmtSbmxFullVolDeathHandler.binderDied():void");
        }

        void forget() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.RmtSbmxFullVolDeathHandler.forget():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.RmtSbmxFullVolDeathHandler.forget():void
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.RmtSbmxFullVolDeathHandler.forget():void");
        }

        boolean isHandlerFor(android.os.IBinder r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.RmtSbmxFullVolDeathHandler.isHandlerFor(android.os.IBinder):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.RmtSbmxFullVolDeathHandler.isHandlerFor(android.os.IBinder):boolean
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.RmtSbmxFullVolDeathHandler.isHandlerFor(android.os.IBinder):boolean");
        }
    }

    private class ScoClient implements DeathRecipient {
        private IBinder mCb;
        private int mCreatorPid;
        private int mStartcount;
        final /* synthetic */ AudioService this$0;

        ScoClient(android.media.AudioService r1, android.os.IBinder r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.ScoClient.<init>(android.media.AudioService, android.os.IBinder):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.ScoClient.<init>(android.media.AudioService, android.os.IBinder):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.ScoClient.<init>(android.media.AudioService, android.os.IBinder):void");
        }

        private void requestScoState(int r1, int r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.ScoClient.requestScoState(int, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.ScoClient.requestScoState(int, int):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.ScoClient.requestScoState(int, int):void");
        }

        public void binderDied() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.ScoClient.binderDied():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.ScoClient.binderDied():void
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.ScoClient.binderDied():void");
        }

        public void clearCount(boolean r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.ScoClient.clearCount(boolean):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.ScoClient.clearCount(boolean):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.ScoClient.clearCount(boolean):void");
        }

        public void decCount() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.ScoClient.decCount():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.ScoClient.decCount():void
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.ScoClient.decCount():void");
        }

        public android.os.IBinder getBinder() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.ScoClient.getBinder():android.os.IBinder
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.ScoClient.getBinder():android.os.IBinder
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.ScoClient.getBinder():android.os.IBinder");
        }

        public int getCount() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.ScoClient.getCount():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.ScoClient.getCount():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.ScoClient.getCount():int");
        }

        public int getPid() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.ScoClient.getPid():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.ScoClient.getPid():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.ScoClient.getPid():int");
        }

        public void incCount(int r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.ScoClient.incCount(int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.ScoClient.incCount(int):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.ScoClient.incCount(int):void");
        }

        public int totalCount() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.ScoClient.totalCount():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.ScoClient.totalCount():int
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.ScoClient.totalCount():int");
        }
    }

    private class SetModeDeathHandler implements DeathRecipient {
        private IBinder mCb;
        private int mMode;
        private int mPid;
        final /* synthetic */ AudioService this$0;

        SetModeDeathHandler(android.media.AudioService r1, android.os.IBinder r2, int r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.SetModeDeathHandler.<init>(android.media.AudioService, android.os.IBinder, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.SetModeDeathHandler.<init>(android.media.AudioService, android.os.IBinder, int):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.SetModeDeathHandler.<init>(android.media.AudioService, android.os.IBinder, int):void");
        }

        static /* synthetic */ int access$1600(android.media.AudioService.SetModeDeathHandler r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.SetModeDeathHandler.access$1600(android.media.AudioService$SetModeDeathHandler):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.SetModeDeathHandler.access$1600(android.media.AudioService$SetModeDeathHandler):int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.SetModeDeathHandler.access$1600(android.media.AudioService$SetModeDeathHandler):int");
        }

        public void binderDied() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.SetModeDeathHandler.binderDied():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.SetModeDeathHandler.binderDied():void
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.SetModeDeathHandler.binderDied():void");
        }

        public android.os.IBinder getBinder() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.SetModeDeathHandler.getBinder():android.os.IBinder
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.SetModeDeathHandler.getBinder():android.os.IBinder
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.SetModeDeathHandler.getBinder():android.os.IBinder");
        }

        public int getMode() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.SetModeDeathHandler.getMode():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.SetModeDeathHandler.getMode():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.SetModeDeathHandler.getMode():int");
        }

        public int getPid() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.SetModeDeathHandler.getPid():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.SetModeDeathHandler.getPid():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.SetModeDeathHandler.getPid():int");
        }

        public void setMode(int r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.SetModeDeathHandler.setMode(int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.SetModeDeathHandler.setMode(int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.SetModeDeathHandler.setMode(int):void");
        }
    }

    private class SettingsObserver extends ContentObserver {
        final /* synthetic */ AudioService this$0;

        SettingsObserver(AudioService audioService) {
            this.this$0 = audioService;
            super(new Handler());
            audioService.mContentResolver.registerContentObserver(System.getUriFor("mode_ringer_streams_affected"), AudioService.DEBUG_VOL, this);
            audioService.mContentResolver.registerContentObserver(Global.getUriFor("dock_audio_media_enabled"), AudioService.DEBUG_VOL, this);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            synchronized (this.this$0.mSettingsLock) {
                if (this.this$0.updateRingerModeAffectedStreams()) {
                    this.this$0.setRingerModeInt(this.this$0.getRingerModeInternal(), AudioService.DEBUG_VOL);
                }
                this.this$0.readDockAudioSettings(this.this$0.mContentResolver);
            }
        }
    }

    private final class SoundPoolCallback implements OnLoadCompleteListener {
        List<Integer> mSamples;
        int mStatus;
        final /* synthetic */ AudioService this$0;

        private SoundPoolCallback(AudioService audioService) {
            this.this$0 = audioService;
            this.mStatus = AudioService.SENDMSG_NOOP;
            this.mSamples = new ArrayList();
        }

        /* synthetic */ SoundPoolCallback(AudioService x0, AnonymousClass1 x1) {
            this(x0);
        }

        public int status() {
            return this.mStatus;
        }

        public void setSamples(int[] samples) {
            for (int i = AudioService.SENDMSG_REPLACE; i < samples.length; i += AudioService.SENDMSG_NOOP) {
                if (samples[i] > 0) {
                    this.mSamples.add(Integer.valueOf(samples[i]));
                }
            }
        }

        public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
            synchronized (this.this$0.mSoundEffectsLock) {
                int i = this.mSamples.indexOf(Integer.valueOf(sampleId));
                if (i >= 0) {
                    this.mSamples.remove(i);
                }
                if (status != 0 || this.mSamples.isEmpty()) {
                    this.mStatus = status;
                    this.this$0.mSoundEffectsLock.notify();
                }
            }
        }
    }

    class SoundPoolListenerThread extends Thread {
        final /* synthetic */ AudioService this$0;

        public SoundPoolListenerThread(AudioService audioService) {
            this.this$0 = audioService;
            super("SoundPoolListenerThread");
        }

        public void run() {
            Looper.prepare();
            this.this$0.mSoundPoolLooper = Looper.myLooper();
            synchronized (this.this$0.mSoundEffectsLock) {
                if (this.this$0.mSoundPool != null) {
                    this.this$0.mSoundPoolCallBack = new SoundPoolCallback(this.this$0, null);
                    this.this$0.mSoundPool.setOnLoadCompleteListener(this.this$0.mSoundPoolCallBack);
                }
                this.this$0.mSoundEffectsLock.notify();
            }
            Looper.loop();
        }
    }

    private static class StreamOverride implements TouchExplorationStateChangeListener {
        private static final int DEFAULT_STREAM_TYPE_OVERRIDE_DELAY_MS = 5000;
        private static final int TOUCH_EXPLORE_STREAM_TYPE_OVERRIDE_DELAY_MS = 1000;
        static int sDelayMs;

        private StreamOverride() {
        }

        static void init(Context ctxt) {
            AccessibilityManager accessibilityManager = (AccessibilityManager) ctxt.getSystemService("accessibility");
            updateDefaultStreamOverrideDelay(accessibilityManager.isTouchExplorationEnabled());
            accessibilityManager.addTouchExplorationStateChangeListener(new StreamOverride());
        }

        public void onTouchExplorationStateChanged(boolean enabled) {
            updateDefaultStreamOverrideDelay(enabled);
        }

        private static void updateDefaultStreamOverrideDelay(boolean touchExploreEnabled) {
            if (touchExploreEnabled) {
                sDelayMs = TOUCH_EXPLORE_STREAM_TYPE_OVERRIDE_DELAY_MS;
            } else {
                sDelayMs = DEFAULT_STREAM_TYPE_OVERRIDE_DELAY_MS;
            }
            if (AudioService.DEBUG_VOL) {
                Log.d(AudioService.TAG, "Touch exploration enabled=" + touchExploreEnabled + " stream override delay is now " + sDelayMs + " ms");
            }
        }
    }

    class StreamVolumeCommand {
        public final int mDevice;
        public final int mFlags;
        public final int mIndex;
        public final int mStreamType;
        final /* synthetic */ AudioService this$0;

        StreamVolumeCommand(android.media.AudioService r1, int r2, int r3, int r4, int r5) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.StreamVolumeCommand.<init>(android.media.AudioService, int, int, int, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.StreamVolumeCommand.<init>(android.media.AudioService, int, int, int, int):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.StreamVolumeCommand.<init>(android.media.AudioService, int, int, int, int):void");
        }

        public java.lang.String toString() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.StreamVolumeCommand.toString():java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.StreamVolumeCommand.toString():java.lang.String
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.StreamVolumeCommand.toString():java.lang.String");
        }
    }

    public static class VolumeController {
        private static final String TAG = "VolumeController";
        private IVolumeController mController;
        private int mLongPressTimeout;
        private long mNextLongPress;
        private boolean mVisible;

        public VolumeController() {
        }

        public void setController(IVolumeController controller) {
            this.mController = controller;
            this.mVisible = AudioService.DEBUG_VOL;
        }

        public void loadSettings(ContentResolver cr) {
            this.mLongPressTimeout = Secure.getIntForUser(cr, "long_press_timeout", AudioService.PERSIST_DELAY, -2);
        }

        public boolean suppressAdjustment(int resolvedStream, int flags) {
            if (resolvedStream != AudioService.SENDMSG_QUEUE || this.mController == null) {
                return AudioService.DEBUG_VOL;
            }
            long now = SystemClock.uptimeMillis();
            if ((flags & AudioService.SENDMSG_NOOP) != 0 && !this.mVisible) {
                if (this.mNextLongPress < now) {
                    this.mNextLongPress = ((long) this.mLongPressTimeout) + now;
                }
                return AudioService.PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
            } else if (this.mNextLongPress <= 0) {
                return AudioService.DEBUG_VOL;
            } else {
                if (now <= this.mNextLongPress) {
                    return AudioService.PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
                }
                this.mNextLongPress = 0;
                return AudioService.DEBUG_VOL;
            }
        }

        public void setVisible(boolean visible) {
            this.mVisible = visible;
        }

        public boolean isSameBinder(IVolumeController controller) {
            return Objects.equals(asBinder(), binder(controller));
        }

        public IBinder asBinder() {
            return binder(this.mController);
        }

        private static IBinder binder(IVolumeController controller) {
            return controller == null ? null : controller.asBinder();
        }

        public String toString() {
            return "VolumeController(" + asBinder() + ",mVisible=" + this.mVisible + ")";
        }

        public void postDisplaySafeVolumeWarning(int flags) {
            if (this.mController != null) {
                try {
                    this.mController.displaySafeVolumeWarning(flags);
                } catch (RemoteException e) {
                    Log.w(TAG, "Error calling displaySafeVolumeWarning", e);
                }
            }
        }

        public void postVolumeChanged(int streamType, int flags) {
            if (this.mController != null) {
                try {
                    this.mController.volumeChanged(streamType, flags);
                } catch (RemoteException e) {
                    Log.w(TAG, "Error calling volumeChanged", e);
                }
            }
        }

        public void postMasterVolumeChanged(int flags) {
            if (this.mController != null) {
                try {
                    this.mController.masterVolumeChanged(flags);
                } catch (RemoteException e) {
                    Log.w(TAG, "Error calling masterVolumeChanged", e);
                }
            }
        }

        public void postMasterMuteChanged(int flags) {
            if (this.mController != null) {
                try {
                    this.mController.masterMuteChanged(flags);
                } catch (RemoteException e) {
                    Log.w(TAG, "Error calling masterMuteChanged", e);
                }
            }
        }

        public void setLayoutDirection(int layoutDirection) {
            if (this.mController != null) {
                try {
                    this.mController.setLayoutDirection(layoutDirection);
                } catch (RemoteException e) {
                    Log.w(TAG, "Error calling setLayoutDirection", e);
                }
            }
        }

        public void postDismiss() {
            if (this.mController != null) {
                try {
                    this.mController.dismiss();
                } catch (RemoteException e) {
                    Log.w(TAG, "Error calling dismiss", e);
                }
            }
        }
    }

    public class VolumeStreamState {
        private ArrayList<VolumeDeathHandler> mDeathHandlers;
        private final ConcurrentHashMap<Integer, Integer> mIndex;
        private int mIndexMax;
        private final int mStreamType;
        private String mVolumeIndexSettingName;
        final /* synthetic */ AudioService this$0;

        private class VolumeDeathHandler implements DeathRecipient {
            private IBinder mICallback;
            private int mMuteCount;
            final /* synthetic */ VolumeStreamState this$1;

            VolumeDeathHandler(android.media.AudioService.VolumeStreamState r1, android.os.IBinder r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.VolumeStreamState.VolumeDeathHandler.<init>(android.media.AudioService$VolumeStreamState, android.os.IBinder):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.VolumeStreamState.VolumeDeathHandler.<init>(android.media.AudioService$VolumeStreamState, android.os.IBinder):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.VolumeStreamState.VolumeDeathHandler.<init>(android.media.AudioService$VolumeStreamState, android.os.IBinder):void");
            }

            static /* synthetic */ int access$2300(android.media.AudioService.VolumeStreamState.VolumeDeathHandler r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.VolumeStreamState.VolumeDeathHandler.access$2300(android.media.AudioService$VolumeStreamState$VolumeDeathHandler):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.VolumeStreamState.VolumeDeathHandler.access$2300(android.media.AudioService$VolumeStreamState$VolumeDeathHandler):int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.VolumeStreamState.VolumeDeathHandler.access$2300(android.media.AudioService$VolumeStreamState$VolumeDeathHandler):int");
            }

            static /* synthetic */ int access$2302(android.media.AudioService.VolumeStreamState.VolumeDeathHandler r1, int r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.VolumeStreamState.VolumeDeathHandler.access$2302(android.media.AudioService$VolumeStreamState$VolumeDeathHandler, int):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.VolumeStreamState.VolumeDeathHandler.access$2302(android.media.AudioService$VolumeStreamState$VolumeDeathHandler, int):int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.VolumeStreamState.VolumeDeathHandler.access$2302(android.media.AudioService$VolumeStreamState$VolumeDeathHandler, int):int");
            }

            static /* synthetic */ android.os.IBinder access$5100(android.media.AudioService.VolumeStreamState.VolumeDeathHandler r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.VolumeStreamState.VolumeDeathHandler.access$5100(android.media.AudioService$VolumeStreamState$VolumeDeathHandler):android.os.IBinder
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.VolumeStreamState.VolumeDeathHandler.access$5100(android.media.AudioService$VolumeStreamState$VolumeDeathHandler):android.os.IBinder
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.VolumeStreamState.VolumeDeathHandler.access$5100(android.media.AudioService$VolumeStreamState$VolumeDeathHandler):android.os.IBinder");
            }

            public void binderDied() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.VolumeStreamState.VolumeDeathHandler.binderDied():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.VolumeStreamState.VolumeDeathHandler.binderDied():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.VolumeStreamState.VolumeDeathHandler.binderDied():void");
            }

            public void mute_syncVSS(boolean r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.AudioService.VolumeStreamState.VolumeDeathHandler.mute_syncVSS(boolean):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.AudioService.VolumeStreamState.VolumeDeathHandler.mute_syncVSS(boolean):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.VolumeStreamState.VolumeDeathHandler.mute_syncVSS(boolean):void");
            }
        }

        /* synthetic */ VolumeStreamState(AudioService x0, String x1, int x2, AnonymousClass1 x3) {
            this(x0, x1, x2);
        }

        private VolumeStreamState(AudioService audioService, String settingName, int streamType) {
            this.this$0 = audioService;
            this.mIndex = new ConcurrentHashMap(AudioService.MSG_SET_FORCE_USE, 0.75f, AudioService.SCO_STATE_DEACTIVATE_EXT_REQ);
            this.mVolumeIndexSettingName = settingName;
            this.mStreamType = streamType;
            this.mIndexMax = AudioService.MAX_STREAM_VOLUME[streamType];
            AudioSystem.initStreamVolume(streamType, AudioService.SENDMSG_REPLACE, this.mIndexMax);
            this.mIndexMax *= AudioService.MSG_SET_ALL_VOLUMES;
            this.mDeathHandlers = new ArrayList();
            readSettings();
        }

        public String getSettingNameForDevice(int device) {
            String name = this.mVolumeIndexSettingName;
            String suffix = AudioSystem.getOutputDeviceName(device);
            return suffix.isEmpty() ? name : name + "_" + suffix;
        }

        public void readSettings() {
            synchronized (VolumeStreamState.class) {
                if (this.this$0.mUseFixedVolume || this.this$0.mUseMasterVolume) {
                    this.mIndex.put(Integer.valueOf(AudioSystem.DEVICE_OUT_DEFAULT), Integer.valueOf(this.mIndexMax));
                } else if (this.mStreamType == AudioService.SENDMSG_NOOP || this.mStreamType == AudioService.MSG_LOAD_SOUND_EFFECTS) {
                    index = AudioService.DEFAULT_STREAM_VOLUME[this.mStreamType] * AudioService.MSG_SET_ALL_VOLUMES;
                    synchronized (this.this$0.mCameraSoundForced) {
                        if (this.this$0.mCameraSoundForced.booleanValue()) {
                            index = this.mIndexMax;
                        }
                    }
                    this.mIndex.put(Integer.valueOf(AudioSystem.DEVICE_OUT_DEFAULT), Integer.valueOf(index));
                } else {
                    int remainingDevices = AudioSystem.DEVICE_OUT_ALL;
                    int i = AudioService.SENDMSG_REPLACE;
                    while (remainingDevices != 0) {
                        int device = AudioService.SENDMSG_NOOP << i;
                        if ((device & remainingDevices) != 0) {
                            int defaultIndex;
                            remainingDevices &= device ^ AudioService.SCO_MODE_UNDEFINED;
                            String name = getSettingNameForDevice(device);
                            if (device == AudioSystem.DEVICE_OUT_DEFAULT) {
                                defaultIndex = AudioService.DEFAULT_STREAM_VOLUME[this.mStreamType];
                            } else {
                                defaultIndex = AudioService.SCO_MODE_UNDEFINED;
                            }
                            index = System.getIntForUser(this.this$0.mContentResolver, name, defaultIndex, -2);
                            if (index != AudioService.SCO_MODE_UNDEFINED) {
                                this.mIndex.put(Integer.valueOf(device), Integer.valueOf(getValidIndex(index * AudioService.MSG_SET_ALL_VOLUMES)));
                            }
                        }
                        i += AudioService.SENDMSG_NOOP;
                    }
                }
            }
        }

        public void applyDeviceVolume_syncVSS(int device) {
            int index;
            if (isMuted_syncVSS()) {
                index = AudioService.SENDMSG_REPLACE;
            } else if (((device & AudioSystem.DEVICE_OUT_ALL_A2DP) == 0 || !this.this$0.mAvrcpAbsVolSupported) && (this.this$0.mFullVolumeDevices & device) == 0) {
                index = (getIndex(device) + AudioService.SCO_STATE_DEACTIVATE_REQ) / AudioService.MSG_SET_ALL_VOLUMES;
            } else {
                index = (this.mIndexMax + AudioService.SCO_STATE_DEACTIVATE_REQ) / AudioService.MSG_SET_ALL_VOLUMES;
            }
            AudioSystem.setStreamVolumeIndex(this.mStreamType, index, device);
        }

        public void applyAllVolumes() {
            synchronized (VolumeStreamState.class) {
                int index;
                if (isMuted_syncVSS()) {
                    index = AudioService.SENDMSG_REPLACE;
                } else {
                    index = (getIndex(AudioSystem.DEVICE_OUT_DEFAULT) + AudioService.SCO_STATE_DEACTIVATE_REQ) / AudioService.MSG_SET_ALL_VOLUMES;
                }
                AudioSystem.setStreamVolumeIndex(this.mStreamType, index, AudioSystem.DEVICE_OUT_DEFAULT);
                for (Entry entry : this.mIndex.entrySet()) {
                    int device = ((Integer) entry.getKey()).intValue();
                    if (device != AudioSystem.DEVICE_OUT_DEFAULT) {
                        if (isMuted_syncVSS()) {
                            index = AudioService.SENDMSG_REPLACE;
                        } else if (((device & AudioSystem.DEVICE_OUT_ALL_A2DP) == 0 || !this.this$0.mAvrcpAbsVolSupported) && (this.this$0.mFullVolumeDevices & device) == 0) {
                            index = (((Integer) entry.getValue()).intValue() + AudioService.SCO_STATE_DEACTIVATE_REQ) / AudioService.MSG_SET_ALL_VOLUMES;
                        } else {
                            index = (this.mIndexMax + AudioService.SCO_STATE_DEACTIVATE_REQ) / AudioService.MSG_SET_ALL_VOLUMES;
                        }
                        AudioSystem.setStreamVolumeIndex(this.mStreamType, index, device);
                    }
                }
            }
        }

        public boolean adjustIndex(int deltaIndex, int device) {
            return setIndex(getIndex(device) + deltaIndex, device);
        }

        public boolean setIndex(int index, int device) {
            synchronized (VolumeStreamState.class) {
                int oldIndex = getIndex(device);
                index = getValidIndex(index);
                synchronized (this.this$0.mCameraSoundForced) {
                    if (this.mStreamType == AudioService.MSG_LOAD_SOUND_EFFECTS && this.this$0.mCameraSoundForced.booleanValue()) {
                        index = this.mIndexMax;
                    }
                }
                this.mIndex.put(Integer.valueOf(device), Integer.valueOf(index));
                if (oldIndex != index) {
                    boolean currentDevice;
                    if (device == this.this$0.getDeviceForStream(this.mStreamType)) {
                        currentDevice = AudioService.PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
                    } else {
                        currentDevice = AudioService.DEBUG_VOL;
                    }
                    int streamType = AudioSystem.getNumStreamTypes() + AudioService.SCO_MODE_UNDEFINED;
                    while (streamType >= 0) {
                        if (streamType != this.mStreamType && this.this$0.mStreamVolumeAlias[streamType] == this.mStreamType) {
                            int scaledIndex = this.this$0.rescaleIndex(index, this.mStreamType, streamType);
                            this.this$0.mStreamStates[streamType].setIndex(scaledIndex, device);
                            if (currentDevice) {
                                this.this$0.mStreamStates[streamType].setIndex(scaledIndex, this.this$0.getDeviceForStream(streamType));
                            }
                        }
                        streamType += AudioService.SCO_MODE_UNDEFINED;
                    }
                    return AudioService.PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
                }
                return AudioService.DEBUG_VOL;
            }
        }

        public int getIndex(int device) {
            int intValue;
            synchronized (VolumeStreamState.class) {
                Integer index = (Integer) this.mIndex.get(Integer.valueOf(device));
                if (index == null) {
                    index = (Integer) this.mIndex.get(Integer.valueOf(AudioSystem.DEVICE_OUT_DEFAULT));
                }
                intValue = index.intValue();
            }
            return intValue;
        }

        public int getMaxIndex() {
            return this.mIndexMax;
        }

        public void setAllIndexes(VolumeStreamState srcStream) {
            synchronized (VolumeStreamState.class) {
                int srcStreamType = srcStream.getStreamType();
                int index = this.this$0.rescaleIndex(srcStream.getIndex(AudioSystem.DEVICE_OUT_DEFAULT), srcStreamType, this.mStreamType);
                for (Entry entry : this.mIndex.entrySet()) {
                    entry.setValue(Integer.valueOf(index));
                }
                for (Entry entry2 : srcStream.mIndex.entrySet()) {
                    setIndex(this.this$0.rescaleIndex(((Integer) entry2.getValue()).intValue(), srcStreamType, this.mStreamType), ((Integer) entry2.getKey()).intValue());
                }
            }
        }

        public void setAllIndexesToMax() {
            synchronized (VolumeStreamState.class) {
                for (Entry entry : this.mIndex.entrySet()) {
                    entry.setValue(Integer.valueOf(this.mIndexMax));
                }
            }
        }

        public void mute(IBinder cb, boolean state) {
            synchronized (VolumeStreamState.class) {
                VolumeDeathHandler handler = getDeathHandler_syncVSS(cb, state);
                if (handler == null) {
                    Log.e(AudioService.TAG, "Could not get client death handler for stream: " + this.mStreamType);
                    return;
                }
                handler.mute_syncVSS(state);
            }
        }

        public int getStreamType() {
            return this.mStreamType;
        }

        public void checkFixedVolumeDevices() {
            synchronized (VolumeStreamState.class) {
                if (this.this$0.mStreamVolumeAlias[this.mStreamType] == AudioService.SCO_STATE_ACTIVE_INTERNAL) {
                    for (Entry entry : this.mIndex.entrySet()) {
                        int device = ((Integer) entry.getKey()).intValue();
                        int index = ((Integer) entry.getValue()).intValue();
                        if (!((this.this$0.mFullVolumeDevices & device) == 0 && ((this.this$0.mFixedVolumeDevices & device) == 0 || index == 0))) {
                            entry.setValue(Integer.valueOf(this.mIndexMax));
                        }
                        applyDeviceVolume_syncVSS(device);
                    }
                }
            }
        }

        private int getValidIndex(int index) {
            if (index < 0) {
                return AudioService.SENDMSG_REPLACE;
            }
            if (this.this$0.mUseFixedVolume || this.this$0.mUseMasterVolume || index > this.mIndexMax) {
                return this.mIndexMax;
            }
            return index;
        }

        private int muteCount() {
            int count = AudioService.SENDMSG_REPLACE;
            int size = this.mDeathHandlers.size();
            for (int i = AudioService.SENDMSG_REPLACE; i < size; i += AudioService.SENDMSG_NOOP) {
                count += VolumeDeathHandler.access$2300((VolumeDeathHandler) this.mDeathHandlers.get(i));
            }
            return count;
        }

        private boolean isMuted_syncVSS() {
            return muteCount() != 0 ? AudioService.PREVENT_VOLUME_ADJUSTMENT_IF_SILENT : AudioService.DEBUG_VOL;
        }

        private VolumeDeathHandler getDeathHandler_syncVSS(IBinder cb, boolean state) {
            VolumeDeathHandler handler;
            int size = this.mDeathHandlers.size();
            for (int i = AudioService.SENDMSG_REPLACE; i < size; i += AudioService.SENDMSG_NOOP) {
                handler = (VolumeDeathHandler) this.mDeathHandlers.get(i);
                if (cb == VolumeDeathHandler.access$5100(handler)) {
                    return handler;
                }
            }
            if (state) {
                handler = new VolumeDeathHandler(this, cb);
            } else {
                Log.w(AudioService.TAG, "stream was not muted by this client");
                handler = null;
            }
            return handler;
        }

        private void dump(PrintWriter pw) {
            pw.print("   Mute count: ");
            pw.println(muteCount());
            pw.print("   Max: ");
            pw.println((this.mIndexMax + AudioService.SCO_STATE_DEACTIVATE_REQ) / AudioService.MSG_SET_ALL_VOLUMES);
            pw.print("   Current: ");
            Iterator i = this.mIndex.entrySet().iterator();
            while (i.hasNext()) {
                Entry entry = (Entry) i.next();
                int device = ((Integer) entry.getKey()).intValue();
                pw.print(Integer.toHexString(device));
                String deviceName = device == AudioSystem.DEVICE_OUT_DEFAULT ? PhoneConstants.APN_TYPE_DEFAULT : AudioSystem.getOutputDeviceName(device);
                if (!deviceName.isEmpty()) {
                    pw.print(" (");
                    pw.print(deviceName);
                    pw.print(")");
                }
                pw.print(": ");
                pw.print((((Integer) entry.getValue()).intValue() + AudioService.SCO_STATE_DEACTIVATE_REQ) / AudioService.MSG_SET_ALL_VOLUMES);
                if (i.hasNext()) {
                    pw.print(", ");
                }
            }
        }
    }

    static {
        DEBUG_MODE = Log.isLoggable("AudioService.MOD", SCO_STATE_ACTIVE_INTERNAL);
        DEBUG_AP = Log.isLoggable("AudioService.AP", SCO_STATE_ACTIVE_INTERNAL);
        DEBUG_VOL = Log.isLoggable("AudioService.VOL", SCO_STATE_ACTIVE_INTERNAL);
        DEBUG_SESSIONS = Log.isLoggable("AudioService.SESSIONS", SCO_STATE_ACTIVE_INTERNAL);
        SOUND_EFFECT_FILES = new ArrayList();
        MAX_STREAM_VOLUME = new int[]{SCO_STATE_DEACTIVATE_REQ, MSG_LOAD_SOUND_EFFECTS, MSG_LOAD_SOUND_EFFECTS, MSG_BROADCAST_AUDIO_BECOMING_NOISY, MSG_LOAD_SOUND_EFFECTS, MSG_LOAD_SOUND_EFFECTS, MSG_BROADCAST_AUDIO_BECOMING_NOISY, MSG_LOAD_SOUND_EFFECTS, MSG_BROADCAST_AUDIO_BECOMING_NOISY, MSG_BROADCAST_AUDIO_BECOMING_NOISY};
        DEFAULT_STREAM_VOLUME = new int[]{SCO_STATE_DEACTIVATE_EXT_REQ, MSG_LOAD_SOUND_EFFECTS, SCO_STATE_DEACTIVATE_REQ, MSG_PERSIST_MASTER_VOLUME_MUTE, MSG_BTA2DP_DOCK_TIMEOUT, SCO_STATE_DEACTIVATE_REQ, MSG_LOAD_SOUND_EFFECTS, MSG_LOAD_SOUND_EFFECTS, MSG_PERSIST_MASTER_VOLUME_MUTE, MSG_PERSIST_MASTER_VOLUME_MUTE};
        STEAM_VOLUME_OPS = new int[]{34, 36, 35, 36, 37, 38, 39, 36, 36, 36};
        String[] strArr = new String[MSG_SET_ALL_VOLUMES];
        strArr[SENDMSG_REPLACE] = "STREAM_VOICE_CALL";
        strArr[SENDMSG_NOOP] = "STREAM_SYSTEM";
        strArr[SENDMSG_QUEUE] = "STREAM_RING";
        strArr[SCO_STATE_ACTIVE_INTERNAL] = "STREAM_MUSIC";
        strArr[SCO_STATE_DEACTIVATE_EXT_REQ] = "STREAM_ALARM";
        strArr[SCO_STATE_DEACTIVATE_REQ] = "STREAM_NOTIFICATION";
        strArr[MSG_BTA2DP_DOCK_TIMEOUT] = "STREAM_BLUETOOTH_SCO";
        strArr[MSG_LOAD_SOUND_EFFECTS] = "STREAM_SYSTEM_ENFORCED";
        strArr[MSG_SET_FORCE_USE] = "STREAM_DTMF";
        strArr[MSG_BT_HEADSET_CNCT_FAILED] = "STREAM_TTS";
        STREAM_NAMES = strArr;
        mLastDeviceConnectMsgTime = new Long(0);
        strArr = new String[SCO_STATE_ACTIVE_INTERNAL];
        strArr[SENDMSG_REPLACE] = "SILENT";
        strArr[SENDMSG_NOOP] = "VIBRATE";
        strArr[SENDMSG_QUEUE] = "NORMAL";
        RINGER_MODE_NAMES = strArr;
    }

    private boolean isPlatformVoice() {
        return this.mPlatformType == SENDMSG_NOOP ? PREVENT_VOLUME_ADJUSTMENT_IF_SILENT : DEBUG_VOL;
    }

    private boolean isPlatformTelevision() {
        return this.mPlatformType == SENDMSG_QUEUE ? PREVENT_VOLUME_ADJUSTMENT_IF_SILENT : DEBUG_VOL;
    }

    public AudioService(Context context) {
        int i;
        this.mVolumeController = new VolumeController();
        this.mMode = SENDMSG_REPLACE;
        this.mSettingsLock = new Object();
        this.mSoundEffectsLock = new Object();
        this.SOUND_EFFECT_FILES_MAP = (int[][]) Array.newInstance(Integer.TYPE, new int[]{MSG_SET_ALL_VOLUMES, SENDMSG_QUEUE});
        this.STREAM_VOLUME_ALIAS_VOICE = new int[]{SENDMSG_REPLACE, SENDMSG_QUEUE, SENDMSG_QUEUE, SCO_STATE_ACTIVE_INTERNAL, SCO_STATE_DEACTIVATE_EXT_REQ, SENDMSG_QUEUE, MSG_BTA2DP_DOCK_TIMEOUT, SENDMSG_QUEUE, SENDMSG_QUEUE, SCO_STATE_ACTIVE_INTERNAL};
        this.STREAM_VOLUME_ALIAS_TELEVISION = new int[]{SCO_STATE_ACTIVE_INTERNAL, SCO_STATE_ACTIVE_INTERNAL, SCO_STATE_ACTIVE_INTERNAL, SCO_STATE_ACTIVE_INTERNAL, SCO_STATE_ACTIVE_INTERNAL, SCO_STATE_ACTIVE_INTERNAL, SCO_STATE_ACTIVE_INTERNAL, SCO_STATE_ACTIVE_INTERNAL, SCO_STATE_ACTIVE_INTERNAL, SCO_STATE_ACTIVE_INTERNAL};
        this.STREAM_VOLUME_ALIAS_DEFAULT = new int[]{SENDMSG_REPLACE, SENDMSG_QUEUE, SENDMSG_QUEUE, SCO_STATE_ACTIVE_INTERNAL, SCO_STATE_DEACTIVATE_EXT_REQ, SENDMSG_QUEUE, MSG_BTA2DP_DOCK_TIMEOUT, SENDMSG_QUEUE, SENDMSG_QUEUE, SCO_STATE_ACTIVE_INTERNAL};
        this.mAudioSystemCallback = new ErrorCallback() {
            public void onError(int error) {
                switch (error) {
                    case AudioService.MSG_SET_WIRED_DEVICE_CONNECTION_STATE /*100*/:
                        AudioService.sendMsg(AudioService.this.mAudioHandler, AudioService.SCO_STATE_DEACTIVATE_EXT_REQ, AudioService.SENDMSG_NOOP, AudioService.SENDMSG_REPLACE, AudioService.SENDMSG_REPLACE, null, AudioService.SENDMSG_REPLACE);
                    default:
                }
            }
        };
        this.mRingerModeExternal = SCO_MODE_UNDEFINED;
        this.mRingerModeAffectedStreams = SENDMSG_REPLACE;
        this.mReceiver = new AudioServiceBroadcastReceiver();
        this.mConnectedDevices = new HashMap();
        this.mSetModeDeathHandlers = new ArrayList();
        this.mScoClients = new ArrayList();
        this.mSoundPoolLooper = null;
        this.mPrevVolDirection = SENDMSG_REPLACE;
        this.mVolumeControlStream = SCO_MODE_UNDEFINED;
        this.mForceControlStreamLock = new Object();
        this.mForceControlStreamClient = null;
        this.mDeviceOrientation = SENDMSG_REPLACE;
        this.mDeviceRotation = SENDMSG_REPLACE;
        this.mBluetoothA2dpEnabledLock = new Object();
        this.mCurAudioRoutes = new AudioRoutesInfo();
        this.mRoutesObservers = new RemoteCallbackList();
        this.mFixedVolumeDevices = 2890752;
        this.mFullVolumeDevices = SENDMSG_REPLACE;
        this.mDockAudioMediaEnabled = PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
        this.mDockState = SENDMSG_REPLACE;
        this.mA2dpAvrcpLock = new Object();
        this.mAvrcpAbsVolSupported = DEBUG_VOL;
        this.mRmtSbmxFullVolRefCount = SENDMSG_REPLACE;
        this.mRmtSbmxFullVolDeathHandlers = new ArrayList();
        this.mBluetoothProfileServiceListener = new 2(this);
        this.mBecomingNoisyIntentDevices = 163724;
        this.mMcc = SENDMSG_REPLACE;
        this.mSafeMediaVolumeDevices = MSG_REPORT_NEW_ROUTES;
        this.mHdmiSystemAudioSupported = DEBUG_VOL;
        this.mHdmiDisplayStatusCallback = new MyDisplayStatusCallback();
        this.mAudioPolicies = new HashMap();
        this.mAudioPolicyCounter = SENDMSG_REPLACE;
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        this.mAppOps = (AppOpsManager) context.getSystemService("appops");
        if (this.mContext.getResources().getBoolean(R.bool.config_voice_capable)) {
            this.mPlatformType = SENDMSG_NOOP;
        } else if (context.getPackageManager().hasSystemFeature("android.software.leanback")) {
            this.mPlatformType = SENDMSG_QUEUE;
        } else {
            this.mPlatformType = SENDMSG_REPLACE;
        }
        this.mAudioEventWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(SENDMSG_NOOP, "handleAudioEvent");
        Vibrator vibrator = (Vibrator) context.getSystemService("vibrator");
        this.mHasVibrator = vibrator == null ? DEBUG_VOL : vibrator.hasVibrator();
        int maxVolume = SystemProperties.getInt("ro.config.vc_call_vol_steps", MAX_STREAM_VOLUME[SENDMSG_REPLACE]);
        if (maxVolume != MAX_STREAM_VOLUME[SENDMSG_REPLACE]) {
            MAX_STREAM_VOLUME[SENDMSG_REPLACE] = maxVolume;
            DEFAULT_STREAM_VOLUME[SENDMSG_REPLACE] = (maxVolume * SCO_STATE_ACTIVE_INTERNAL) / SCO_STATE_DEACTIVATE_EXT_REQ;
        }
        maxVolume = SystemProperties.getInt("ro.config.media_vol_steps", MAX_STREAM_VOLUME[SCO_STATE_ACTIVE_INTERNAL]);
        if (maxVolume != MAX_STREAM_VOLUME[SCO_STATE_ACTIVE_INTERNAL]) {
            MAX_STREAM_VOLUME[SCO_STATE_ACTIVE_INTERNAL] = maxVolume;
            DEFAULT_STREAM_VOLUME[SCO_STATE_ACTIVE_INTERNAL] = (maxVolume * SCO_STATE_ACTIVE_INTERNAL) / SCO_STATE_DEACTIVATE_EXT_REQ;
        }
        sSoundEffectVolumeDb = context.getResources().getInteger(R.integer.config_soundEffectVolumeDb);
        this.mForcedUseForComm = SENDMSG_REPLACE;
        createAudioSystemThread();
        this.mMediaFocusControl = new MediaFocusControl(this.mAudioHandler.getLooper(), this.mContext, this.mVolumeController, this);
        AudioSystem.setErrorCallback(this.mAudioSystemCallback);
        boolean cameraSoundForced = this.mContext.getResources().getBoolean(R.bool.config_camera_sound_forced);
        this.mCameraSoundForced = new Boolean(cameraSoundForced);
        Handler handler = this.mAudioHandler;
        if (cameraSoundForced) {
            i = MSG_PERSIST_MASTER_VOLUME_MUTE;
        } else {
            i = SENDMSG_REPLACE;
        }
        sendMsg(handler, MSG_SET_FORCE_USE, SENDMSG_QUEUE, SCO_STATE_DEACTIVATE_EXT_REQ, i, null, SENDMSG_REPLACE);
        this.mSafeMediaVolumeState = new Integer(Global.getInt(this.mContentResolver, "audio_safe_volume_state", SENDMSG_REPLACE));
        this.mSafeMediaVolumeIndex = this.mContext.getResources().getInteger(R.integer.config_safe_media_volume_index) * MSG_SET_ALL_VOLUMES;
        this.mUseFixedVolume = this.mContext.getResources().getBoolean(R.bool.config_useFixedVolume);
        this.mUseMasterVolume = context.getResources().getBoolean(R.bool.config_useMasterVolume);
        this.mMasterVolumeRamp = context.getResources().getIntArray(R.array.config_masterVolumeRamp);
        updateStreamVolumeAlias(DEBUG_VOL);
        readPersistedSettings();
        this.mSettingsObserver = new SettingsObserver(this);
        createStreamStates();
        readAndSetLowRamDevice();
        this.mRingerModeMutedStreams = SENDMSG_REPLACE;
        setRingerModeInt(getRingerModeInternal(), DEBUG_VOL);
        IntentFilter intentFilter = new IntentFilter("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED");
        intentFilter.addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
        intentFilter.addAction("android.intent.action.DOCK_EVENT");
        intentFilter.addAction(AudioManager.ACTION_USB_AUDIO_ACCESSORY_PLUG);
        intentFilter.addAction(AudioManager.ACTION_USB_AUDIO_DEVICE_PLUG);
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        intentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
        this.mMonitorOrientation = SystemProperties.getBoolean("ro.audio.monitorOrientation", DEBUG_VOL);
        if (this.mMonitorOrientation) {
            Log.v(TAG, "monitoring device orientation");
            setOrientationForAudioSystem();
        }
        this.mMonitorRotation = SystemProperties.getBoolean("ro.audio.monitorRotation", DEBUG_VOL);
        if (this.mMonitorRotation) {
            this.mDeviceRotation = ((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay().getRotation();
            Log.v(TAG, "monitoring device rotation, initial=" + this.mDeviceRotation);
            this.mOrientationListener = new AudioOrientationEventListener(this, this.mContext);
            this.mOrientationListener.enable();
            setRotationForAudioSystem();
        }
        context.registerReceiver(this.mReceiver, intentFilter);
        restoreMasterVolume();
        LocalServices.addService(AudioManagerInternal.class, new AudioServiceInternal(this));
    }

    public void systemReady() {
        sendMsg(this.mAudioHandler, MSG_SYSTEM_READY, SENDMSG_QUEUE, SENDMSG_REPLACE, SENDMSG_REPLACE, null, SENDMSG_REPLACE);
    }

    public void onSystemReady() {
        this.mSystemReady = PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
        sendMsg(this.mAudioHandler, MSG_LOAD_SOUND_EFFECTS, SENDMSG_QUEUE, SENDMSG_REPLACE, SENDMSG_REPLACE, null, SENDMSG_REPLACE);
        this.mKeyguardManager = (KeyguardManager) this.mContext.getSystemService("keyguard");
        this.mScoConnectionState = SCO_MODE_UNDEFINED;
        resetBluetoothSco();
        getBluetoothHeadset();
        Intent newIntent = new Intent(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED);
        newIntent.putExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, SENDMSG_REPLACE);
        sendStickyBroadcastToAll(newIntent);
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            adapter.getProfileProxy(this.mContext, this.mBluetoothProfileServiceListener, SENDMSG_QUEUE);
        }
        this.mHdmiManager = (HdmiControlManager) this.mContext.getSystemService("hdmi_control");
        if (this.mHdmiManager != null) {
            synchronized (this.mHdmiManager) {
                this.mHdmiTvClient = this.mHdmiManager.getTvClient();
                if (this.mHdmiTvClient != null) {
                    this.mFixedVolumeDevices &= -2883587;
                }
                this.mHdmiPlaybackClient = this.mHdmiManager.getPlaybackClient();
                this.mHdmiCecSink = DEBUG_VOL;
            }
        }
        sendMsg(this.mAudioHandler, MSG_CONFIGURE_SAFE_MEDIA_VOLUME_FORCED, SENDMSG_REPLACE, SENDMSG_REPLACE, SENDMSG_REPLACE, null, SAFE_VOLUME_CONFIGURE_TIMEOUT_MS);
        StreamOverride.init(this.mContext);
    }

    private void createAudioSystemThread() {
        this.mAudioSystemThread = new AudioSystemThread(this);
        this.mAudioSystemThread.start();
        waitForAudioHandlerCreation();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void waitForAudioHandlerCreation() {
        /*
        r3 = this;
        monitor-enter(r3);
    L_0x0001:
        r1 = r3.mAudioHandler;	 Catch:{ all -> 0x0012 }
        if (r1 != 0) goto L_0x0015;
    L_0x0005:
        r3.wait();	 Catch:{ InterruptedException -> 0x0009 }
        goto L_0x0001;
    L_0x0009:
        r0 = move-exception;
        r1 = "AudioService";
        r2 = "Interrupted while waiting on volume handler.";
        android.util.Log.e(r1, r2);	 Catch:{ all -> 0x0012 }
        goto L_0x0001;
    L_0x0012:
        r1 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x0012 }
        throw r1;
    L_0x0015:
        monitor-exit(r3);	 Catch:{ all -> 0x0012 }
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.waitForAudioHandlerCreation():void");
    }

    private void checkAllAliasStreamVolumes() {
        synchronized (VolumeStreamState.class) {
            int numStreamTypes = AudioSystem.getNumStreamTypes();
            for (int streamType = SENDMSG_REPLACE; streamType < numStreamTypes; streamType += SENDMSG_NOOP) {
                if (streamType != this.mStreamVolumeAlias[streamType]) {
                    this.mStreamStates[streamType].setAllIndexes(this.mStreamStates[this.mStreamVolumeAlias[streamType]]);
                }
                if (!this.mStreamStates[streamType].isMuted_syncVSS()) {
                    this.mStreamStates[streamType].applyAllVolumes();
                }
            }
        }
    }

    private void checkAllFixedVolumeDevices() {
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        for (int streamType = SENDMSG_REPLACE; streamType < numStreamTypes; streamType += SENDMSG_NOOP) {
            this.mStreamStates[streamType].checkFixedVolumeDevices();
        }
    }

    private void checkAllFixedVolumeDevices(int streamType) {
        this.mStreamStates[streamType].checkFixedVolumeDevices();
    }

    private void createStreamStates() {
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        VolumeStreamState[] streams = new VolumeStreamState[numStreamTypes];
        this.mStreamStates = streams;
        for (int i = SENDMSG_REPLACE; i < numStreamTypes; i += SENDMSG_NOOP) {
            streams[i] = new VolumeStreamState(this, System.VOLUME_SETTINGS[this.mStreamVolumeAlias[i]], i, null);
        }
        checkAllFixedVolumeDevices();
        checkAllAliasStreamVolumes();
    }

    private void dumpStreamStates(PrintWriter pw) {
        pw.println("\nStream volumes (device: index)");
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        for (int i = SENDMSG_REPLACE; i < numStreamTypes; i += SENDMSG_NOOP) {
            pw.println("- " + STREAM_NAMES[i] + ":");
            this.mStreamStates[i].dump(pw);
            pw.println("");
        }
        pw.print("\n- mute affected streams = 0x");
        pw.println(Integer.toHexString(this.mMuteAffectedStreams));
    }

    public static String streamToString(int stream) {
        if (stream >= 0 && stream < STREAM_NAMES.length) {
            return STREAM_NAMES[stream];
        }
        if (stream == ExploreByTouchHelper.INVALID_ID) {
            return "USE_DEFAULT_STREAM_TYPE";
        }
        return "UNKNOWN_STREAM_" + stream;
    }

    private void updateStreamVolumeAlias(boolean updateVolumes) {
        int dtmfStreamAlias;
        switch (this.mPlatformType) {
            case SENDMSG_NOOP /*1*/:
                this.mStreamVolumeAlias = this.STREAM_VOLUME_ALIAS_VOICE;
                dtmfStreamAlias = SENDMSG_QUEUE;
                break;
            case SENDMSG_QUEUE /*2*/:
                this.mStreamVolumeAlias = this.STREAM_VOLUME_ALIAS_TELEVISION;
                dtmfStreamAlias = SCO_STATE_ACTIVE_INTERNAL;
                break;
            default:
                this.mStreamVolumeAlias = this.STREAM_VOLUME_ALIAS_DEFAULT;
                dtmfStreamAlias = SCO_STATE_ACTIVE_INTERNAL;
                break;
        }
        if (isPlatformTelevision()) {
            this.mRingerModeAffectedStreams = SENDMSG_REPLACE;
        } else if (isInCommunication()) {
            dtmfStreamAlias = SENDMSG_REPLACE;
            this.mRingerModeAffectedStreams &= -257;
        } else {
            this.mRingerModeAffectedStreams |= GL10.GL_DEPTH_BUFFER_BIT;
        }
        this.mStreamVolumeAlias[MSG_SET_FORCE_USE] = dtmfStreamAlias;
        if (updateVolumes) {
            this.mStreamStates[MSG_SET_FORCE_USE].setAllIndexes(this.mStreamStates[dtmfStreamAlias]);
            setRingerModeInt(getRingerModeInternal(), DEBUG_VOL);
            sendMsg(this.mAudioHandler, MSG_SET_ALL_VOLUMES, SENDMSG_QUEUE, SENDMSG_REPLACE, SENDMSG_REPLACE, this.mStreamStates[MSG_SET_FORCE_USE], SENDMSG_REPLACE);
        }
    }

    private void readDockAudioSettings(ContentResolver cr) {
        int i;
        boolean z = PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
        if (Global.getInt(cr, "dock_audio_media_enabled", SENDMSG_REPLACE) != SENDMSG_NOOP) {
            z = DEBUG_VOL;
        }
        this.mDockAudioMediaEnabled = z;
        if (this.mDockAudioMediaEnabled) {
            this.mBecomingNoisyIntentDevices |= GL10.GL_EXP;
        } else {
            this.mBecomingNoisyIntentDevices &= -2049;
        }
        Handler handler = this.mAudioHandler;
        if (this.mDockAudioMediaEnabled) {
            i = MSG_SET_FORCE_USE;
        } else {
            i = SENDMSG_REPLACE;
        }
        sendMsg(handler, MSG_SET_FORCE_USE, SENDMSG_QUEUE, SCO_STATE_ACTIVE_INTERNAL, i, null, SENDMSG_REPLACE);
    }

    private void readPersistedSettings() {
        boolean masterMute;
        boolean microphoneMute;
        int i = SENDMSG_QUEUE;
        ContentResolver cr = this.mContentResolver;
        int ringerModeFromSettings = Global.getInt(cr, "mode_ringer", SENDMSG_QUEUE);
        int ringerMode = ringerModeFromSettings;
        if (!isValidRingerMode(ringerMode)) {
            ringerMode = SENDMSG_QUEUE;
        }
        if (ringerMode == SENDMSG_NOOP && !this.mHasVibrator) {
            ringerMode = SENDMSG_REPLACE;
        }
        if (ringerMode != ringerModeFromSettings) {
            Global.putInt(cr, "mode_ringer", ringerMode);
        }
        if (this.mUseFixedVolume || isPlatformTelevision()) {
            ringerMode = SENDMSG_QUEUE;
        }
        synchronized (this.mSettingsLock) {
            int i2;
            this.mRingerMode = ringerMode;
            if (this.mRingerModeExternal == SCO_MODE_UNDEFINED) {
                this.mRingerModeExternal = this.mRingerMode;
            }
            if (this.mHasVibrator) {
                i2 = SENDMSG_QUEUE;
            } else {
                i2 = SENDMSG_REPLACE;
            }
            this.mVibrateSetting = getValueForVibrateSetting(SENDMSG_REPLACE, SENDMSG_NOOP, i2);
            i2 = this.mVibrateSetting;
            if (!this.mHasVibrator) {
                i = SENDMSG_REPLACE;
            }
            this.mVibrateSetting = getValueForVibrateSetting(i2, SENDMSG_REPLACE, i);
            updateRingerModeAffectedStreams();
            readDockAudioSettings(cr);
        }
        this.mMuteAffectedStreams = System.getIntForUser(cr, "mute_streams_affected", MSG_CHECK_MUSIC_ACTIVE, -2);
        if (System.getIntForUser(cr, "volume_master_mute", SENDMSG_REPLACE, -2) == SENDMSG_NOOP) {
            masterMute = PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
        } else {
            masterMute = DEBUG_VOL;
        }
        if (this.mUseFixedVolume) {
            masterMute = DEBUG_VOL;
            AudioSystem.setMasterVolume(RemoteControlClient.PLAYBACK_SPEED_1X);
        }
        AudioSystem.setMasterMute(masterMute);
        broadcastMasterMuteStatus(masterMute);
        if (System.getIntForUser(cr, "microphone_mute", SENDMSG_REPLACE, -2) == SENDMSG_NOOP) {
            microphoneMute = PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
        } else {
            microphoneMute = DEBUG_VOL;
        }
        AudioSystem.muteMicrophone(microphoneMute);
        broadcastRingerMode(AudioManager.RINGER_MODE_CHANGED_ACTION, this.mRingerModeExternal);
        broadcastRingerMode(AudioManager.INTERNAL_RINGER_MODE_CHANGED_ACTION, this.mRingerMode);
        broadcastVibrateSetting(SENDMSG_REPLACE);
        broadcastVibrateSetting(SENDMSG_NOOP);
        this.mVolumeController.loadSettings(cr);
    }

    private int rescaleIndex(int index, int srcStream, int dstStream) {
        return ((this.mStreamStates[dstStream].getMaxIndex() * index) + (this.mStreamStates[srcStream].getMaxIndex() / SENDMSG_QUEUE)) / this.mStreamStates[srcStream].getMaxIndex();
    }

    public void adjustSuggestedStreamVolume(int direction, int suggestedStreamType, int flags, String callingPackage) {
        adjustSuggestedStreamVolume(direction, suggestedStreamType, flags, callingPackage, Binder.getCallingUid());
    }

    private void adjustSuggestedStreamVolume(int direction, int suggestedStreamType, int flags, String callingPackage, int uid) {
        int streamType;
        if (DEBUG_VOL) {
            Log.d(TAG, "adjustSuggestedStreamVolume() stream=" + suggestedStreamType + ", flags=" + flags);
        }
        if (this.mVolumeControlStream != SCO_MODE_UNDEFINED) {
            streamType = this.mVolumeControlStream;
        } else {
            streamType = getActiveStreamType(suggestedStreamType);
        }
        int resolvedStream = this.mStreamVolumeAlias[streamType];
        if (!((flags & SCO_STATE_DEACTIVATE_EXT_REQ) == 0 || resolvedStream == SENDMSG_QUEUE)) {
            flags &= -5;
        }
        if (this.mVolumeController.suppressAdjustment(resolvedStream, flags)) {
            direction = SENDMSG_REPLACE;
            flags = (flags & -5) & -17;
            if (DEBUG_VOL) {
                Log.d(TAG, "Volume controller suppressed adjustment");
            }
        }
        adjustStreamVolume(streamType, direction, flags, callingPackage, uid);
    }

    public void adjustStreamVolume(int streamType, int direction, int flags, String callingPackage) {
        adjustStreamVolume(streamType, direction, flags, callingPackage, Binder.getCallingUid());
    }

    private void adjustStreamVolume(int streamType, int direction, int flags, String callingPackage, int uid) {
        if (!this.mUseFixedVolume) {
            if (DEBUG_VOL) {
                Log.d(TAG, "adjustStreamVolume() stream=" + streamType + ", dir=" + direction + ", flags=" + flags);
            }
            ensureValidDirection(direction);
            ensureValidStreamType(streamType);
            int streamTypeAlias = this.mStreamVolumeAlias[streamType];
            VolumeStreamState streamState = this.mStreamStates[streamTypeAlias];
            int device = getDeviceForStream(streamTypeAlias);
            int aliasIndex = streamState.getIndex(device);
            boolean adjustVolume = PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
            if (((device & AudioSystem.DEVICE_OUT_ALL_A2DP) != 0 || (flags & 64) == 0) && this.mAppOps.noteOp(STEAM_VOLUME_OPS[streamTypeAlias], uid, callingPackage) == 0) {
                int step;
                synchronized (this.mSafeMediaVolumeState) {
                    this.mPendingVolumeCommand = null;
                }
                flags &= -33;
                if (streamTypeAlias != SCO_STATE_ACTIVE_INTERNAL || (this.mFixedVolumeDevices & device) == 0) {
                    step = rescaleIndex(MSG_SET_ALL_VOLUMES, streamType, streamTypeAlias);
                } else {
                    flags |= 32;
                    if (this.mSafeMediaVolumeState.intValue() != SCO_STATE_ACTIVE_INTERNAL || (device & MSG_REPORT_NEW_ROUTES) == 0) {
                        step = streamState.getMaxIndex();
                    } else {
                        step = this.mSafeMediaVolumeIndex;
                    }
                    if (aliasIndex != 0) {
                        aliasIndex = step;
                    }
                }
                if ((flags & SENDMSG_QUEUE) != 0 || streamTypeAlias == getMasterStreamType()) {
                    if (getRingerModeInternal() == SENDMSG_NOOP) {
                        flags &= -17;
                    }
                    int result = checkForRingerModeChange(aliasIndex, direction, step);
                    adjustVolume = (result & SENDMSG_NOOP) != 0 ? PREVENT_VOLUME_ADJUSTMENT_IF_SILENT : DEBUG_VOL;
                    if ((result & RILConstants.RIL_REQUEST_SET_DATA_PROFILE) != 0) {
                        flags |= RILConstants.RIL_REQUEST_SET_DATA_PROFILE;
                    }
                    if ((result & GL10.GL_EXP) != 0) {
                        flags |= GL10.GL_EXP;
                    }
                }
                int oldIndex = this.mStreamStates[streamType].getIndex(device);
                if (adjustVolume && direction != 0) {
                    int newIndex;
                    int keyCode;
                    if (streamTypeAlias == SCO_STATE_ACTIVE_INTERNAL && (device & AudioSystem.DEVICE_OUT_ALL_A2DP) != 0 && (flags & 64) == 0) {
                        synchronized (this.mA2dpAvrcpLock) {
                            if (this.mA2dp != null && this.mAvrcpAbsVolSupported) {
                                this.mA2dp.adjustAvrcpAbsoluteVolume(direction);
                            }
                        }
                    }
                    if (direction == SENDMSG_NOOP) {
                        if (!checkSafeMediaVolume(streamTypeAlias, aliasIndex + step, device)) {
                            Log.e(TAG, "adjustStreamVolume() safe volume index = " + oldIndex);
                            this.mVolumeController.postDisplaySafeVolumeWarning(flags);
                            newIndex = this.mStreamStates[streamType].getIndex(device);
                            if (streamTypeAlias == SCO_STATE_ACTIVE_INTERNAL) {
                                setSystemAudioVolume(oldIndex, newIndex, getStreamMaxVolume(streamType), flags);
                            }
                            if (this.mHdmiManager != null) {
                                synchronized (this.mHdmiManager) {
                                    if (this.mHdmiCecSink && streamTypeAlias == SCO_STATE_ACTIVE_INTERNAL && oldIndex != newIndex) {
                                        synchronized (this.mHdmiPlaybackClient) {
                                            keyCode = direction != SCO_MODE_UNDEFINED ? 25 : 24;
                                            this.mHdmiPlaybackClient.sendKeyEvent(keyCode, PREVENT_VOLUME_ADJUSTMENT_IF_SILENT);
                                            this.mHdmiPlaybackClient.sendKeyEvent(keyCode, DEBUG_VOL);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (streamState.adjustIndex(direction * step, device)) {
                        sendMsg(this.mAudioHandler, SENDMSG_REPLACE, SENDMSG_QUEUE, device, SENDMSG_REPLACE, streamState, SENDMSG_REPLACE);
                    }
                    newIndex = this.mStreamStates[streamType].getIndex(device);
                    if (streamTypeAlias == SCO_STATE_ACTIVE_INTERNAL) {
                        setSystemAudioVolume(oldIndex, newIndex, getStreamMaxVolume(streamType), flags);
                    }
                    if (this.mHdmiManager != null) {
                        synchronized (this.mHdmiManager) {
                            synchronized (this.mHdmiPlaybackClient) {
                                if (direction != SCO_MODE_UNDEFINED) {
                                }
                                this.mHdmiPlaybackClient.sendKeyEvent(keyCode, PREVENT_VOLUME_ADJUSTMENT_IF_SILENT);
                                this.mHdmiPlaybackClient.sendKeyEvent(keyCode, DEBUG_VOL);
                            }
                        }
                    }
                }
                sendVolumeUpdate(streamType, oldIndex, this.mStreamStates[streamType].getIndex(device), flags);
            }
        }
    }

    private void setSystemAudioVolume(int oldVolume, int newVolume, int maxVolume, int flags) {
        if (this.mHdmiManager != null && this.mHdmiTvClient != null && oldVolume != newVolume && (flags & GL10.GL_DEPTH_BUFFER_BIT) == 0) {
            synchronized (this.mHdmiManager) {
                if (this.mHdmiSystemAudioSupported) {
                    synchronized (this.mHdmiTvClient) {
                        long token = Binder.clearCallingIdentity();
                        try {
                            this.mHdmiTvClient.setSystemAudioVolume((oldVolume + SCO_STATE_DEACTIVATE_REQ) / MSG_SET_ALL_VOLUMES, (newVolume + SCO_STATE_DEACTIVATE_REQ) / MSG_SET_ALL_VOLUMES, maxVolume);
                            Binder.restoreCallingIdentity(token);
                        } catch (Throwable th) {
                            Binder.restoreCallingIdentity(token);
                        }
                    }
                    return;
                }
            }
        }
    }

    public void adjustMasterVolume(int steps, int flags, String callingPackage) {
        adjustMasterVolume(steps, flags, callingPackage, Binder.getCallingUid());
    }

    public void adjustMasterVolume(int steps, int flags, String callingPackage, int uid) {
        if (!this.mUseFixedVolume) {
            ensureValidSteps(steps);
            int volume = Math.round(AudioSystem.getMasterVolume() * 100.0f);
            int numSteps = Math.abs(steps);
            int direction = steps > 0 ? SENDMSG_NOOP : SCO_MODE_UNDEFINED;
            for (int i = SENDMSG_REPLACE; i < numSteps; i += SENDMSG_NOOP) {
                volume += findVolumeDelta(direction, volume);
            }
            setMasterVolume(volume, flags, callingPackage, uid);
        }
    }

    private void onSetStreamVolume(int streamType, int index, int flags, int device) {
        setStreamVolumeInt(this.mStreamVolumeAlias[streamType], index, device, DEBUG_VOL);
        if ((flags & SENDMSG_QUEUE) != 0 || this.mStreamVolumeAlias[streamType] == getMasterStreamType()) {
            int newRingerMode = index == 0 ? this.mHasVibrator ? SENDMSG_NOOP : SENDMSG_QUEUE : SENDMSG_QUEUE;
            setRingerMode(newRingerMode, "AudioService.onSetStreamVolume", DEBUG_VOL);
        }
    }

    public void setStreamVolume(int streamType, int index, int flags, String callingPackage) {
        setStreamVolume(streamType, index, flags, callingPackage, Binder.getCallingUid());
    }

    private void setStreamVolume(int streamType, int index, int flags, String callingPackage, int uid) {
        if (!this.mUseFixedVolume) {
            ensureValidStreamType(streamType);
            int streamTypeAlias = this.mStreamVolumeAlias[streamType];
            VolumeStreamState streamState = this.mStreamStates[streamTypeAlias];
            int device = getDeviceForStream(streamType);
            if (((device & AudioSystem.DEVICE_OUT_ALL_A2DP) != 0 || (flags & 64) == 0) && this.mAppOps.noteOp(STEAM_VOLUME_OPS[streamTypeAlias], uid, callingPackage) == 0) {
                int oldIndex;
                synchronized (this.mSafeMediaVolumeState) {
                    this.mPendingVolumeCommand = null;
                    oldIndex = streamState.getIndex(device);
                    index = rescaleIndex(index * MSG_SET_ALL_VOLUMES, streamType, streamTypeAlias);
                    if (streamTypeAlias == SCO_STATE_ACTIVE_INTERNAL && (device & AudioSystem.DEVICE_OUT_ALL_A2DP) != 0 && (flags & 64) == 0) {
                        synchronized (this.mA2dpAvrcpLock) {
                            if (this.mA2dp != null && this.mAvrcpAbsVolSupported) {
                                this.mA2dp.setAvrcpAbsoluteVolume(index / MSG_SET_ALL_VOLUMES);
                            }
                        }
                    }
                    if (streamTypeAlias == SCO_STATE_ACTIVE_INTERNAL) {
                        setSystemAudioVolume(oldIndex, index, getStreamMaxVolume(streamType), flags);
                    }
                    flags &= -33;
                    if (streamTypeAlias == SCO_STATE_ACTIVE_INTERNAL && (this.mFixedVolumeDevices & device) != 0) {
                        flags |= 32;
                        if (index != 0) {
                            index = (this.mSafeMediaVolumeState.intValue() != SCO_STATE_ACTIVE_INTERNAL || (device & MSG_REPORT_NEW_ROUTES) == 0) ? streamState.getMaxIndex() : this.mSafeMediaVolumeIndex;
                        }
                    }
                    if (checkSafeMediaVolume(streamTypeAlias, index, device)) {
                        onSetStreamVolume(streamType, index, flags, device);
                        index = this.mStreamStates[streamType].getIndex(device);
                    } else {
                        this.mVolumeController.postDisplaySafeVolumeWarning(flags);
                        this.mPendingVolumeCommand = new StreamVolumeCommand(this, streamType, index, flags, device);
                    }
                }
                sendVolumeUpdate(streamType, oldIndex, index, flags);
            }
        }
    }

    public void forceVolumeControlStream(int streamType, IBinder cb) {
        synchronized (this.mForceControlStreamLock) {
            this.mVolumeControlStream = streamType;
            if (this.mVolumeControlStream != SCO_MODE_UNDEFINED) {
                this.mForceControlStreamClient = new ForceControlStreamClient(this, cb);
            } else if (this.mForceControlStreamClient != null) {
                this.mForceControlStreamClient.release();
                this.mForceControlStreamClient = null;
            }
        }
    }

    private int findVolumeDelta(int direction, int volume) {
        int delta = SENDMSG_REPLACE;
        int i;
        if (direction == SENDMSG_NOOP) {
            if (volume == MSG_SET_WIRED_DEVICE_CONNECTION_STATE) {
                return SENDMSG_REPLACE;
            }
            delta = this.mMasterVolumeRamp[SENDMSG_NOOP];
            for (i = this.mMasterVolumeRamp.length + SCO_MODE_UNDEFINED; i > SENDMSG_NOOP; i -= 2) {
                if (volume >= this.mMasterVolumeRamp[i + SCO_MODE_UNDEFINED]) {
                    delta = this.mMasterVolumeRamp[i];
                    break;
                }
            }
        } else if (direction == SCO_MODE_UNDEFINED) {
            if (volume == 0) {
                return SENDMSG_REPLACE;
            }
            int length = this.mMasterVolumeRamp.length;
            delta = -this.mMasterVolumeRamp[length + SCO_MODE_UNDEFINED];
            for (i = SENDMSG_QUEUE; i < length; i += SENDMSG_QUEUE) {
                if (volume <= this.mMasterVolumeRamp[i]) {
                    delta = -this.mMasterVolumeRamp[i + SCO_MODE_UNDEFINED];
                    break;
                }
            }
        }
        return delta;
    }

    private void sendBroadcastToAll(Intent intent) {
        intent.addFlags(67108864);
        long ident = Binder.clearCallingIdentity();
        try {
            this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    private void sendStickyBroadcastToAll(Intent intent) {
        long ident = Binder.clearCallingIdentity();
        try {
            this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    private void sendVolumeUpdate(int streamType, int oldIndex, int index, int flags) {
        if (!isPlatformVoice() && streamType == SENDMSG_QUEUE) {
            streamType = SCO_STATE_DEACTIVATE_REQ;
        }
        if (streamType == SCO_STATE_ACTIVE_INTERNAL) {
            flags = updateFlagsForSystemAudio(flags);
        }
        this.mVolumeController.postVolumeChanged(streamType, flags);
        if ((flags & 32) == 0) {
            oldIndex = (oldIndex + SCO_STATE_DEACTIVATE_REQ) / MSG_SET_ALL_VOLUMES;
            index = (index + SCO_STATE_DEACTIVATE_REQ) / MSG_SET_ALL_VOLUMES;
            Intent intent = new Intent(AudioManager.VOLUME_CHANGED_ACTION);
            intent.putExtra(AudioManager.EXTRA_VOLUME_STREAM_TYPE, streamType);
            intent.putExtra(AudioManager.EXTRA_VOLUME_STREAM_VALUE, index);
            intent.putExtra(AudioManager.EXTRA_PREV_VOLUME_STREAM_VALUE, oldIndex);
            sendBroadcastToAll(intent);
        }
    }

    private int updateFlagsForSystemAudio(int flags) {
        if (this.mHdmiTvClient != null) {
            synchronized (this.mHdmiTvClient) {
                if (this.mHdmiSystemAudioSupported && (flags & GL10.GL_DEPTH_BUFFER_BIT) == 0) {
                    flags &= -2;
                }
            }
        }
        return flags;
    }

    private void sendMasterVolumeUpdate(int flags, int oldVolume, int newVolume) {
        this.mVolumeController.postMasterVolumeChanged(updateFlagsForSystemAudio(flags));
        Intent intent = new Intent(AudioManager.MASTER_VOLUME_CHANGED_ACTION);
        intent.putExtra(AudioManager.EXTRA_PREV_MASTER_VOLUME_VALUE, oldVolume);
        intent.putExtra(AudioManager.EXTRA_MASTER_VOLUME_VALUE, newVolume);
        sendBroadcastToAll(intent);
    }

    private void sendMasterMuteUpdate(boolean muted, int flags) {
        this.mVolumeController.postMasterMuteChanged(updateFlagsForSystemAudio(flags));
        broadcastMasterMuteStatus(muted);
    }

    private void broadcastMasterMuteStatus(boolean muted) {
        Intent intent = new Intent(AudioManager.MASTER_MUTE_CHANGED_ACTION);
        intent.putExtra(AudioManager.EXTRA_MASTER_VOLUME_MUTED, muted);
        intent.addFlags(603979776);
        sendStickyBroadcastToAll(intent);
    }

    private void setStreamVolumeInt(int streamType, int index, int device, boolean force) {
        VolumeStreamState streamState = this.mStreamStates[streamType];
        if (streamState.setIndex(index, device) || force) {
            sendMsg(this.mAudioHandler, SENDMSG_REPLACE, SENDMSG_QUEUE, device, SENDMSG_REPLACE, streamState, SENDMSG_REPLACE);
        }
    }

    public void setStreamSolo(int streamType, boolean state, IBinder cb) {
        if (!this.mUseFixedVolume) {
            int streamAlias = this.mStreamVolumeAlias[streamType];
            int stream = SENDMSG_REPLACE;
            while (stream < this.mStreamStates.length) {
                if (isStreamAffectedByMute(streamAlias) && streamAlias != this.mStreamVolumeAlias[stream]) {
                    this.mStreamStates[stream].mute(cb, state);
                }
                stream += SENDMSG_NOOP;
            }
        }
    }

    public void setStreamMute(int streamType, boolean state, IBinder cb) {
        if (!this.mUseFixedVolume) {
            if (streamType == ExploreByTouchHelper.INVALID_ID) {
                streamType = getActiveStreamType(streamType);
            }
            int streamAlias = this.mStreamVolumeAlias[streamType];
            if (isStreamAffectedByMute(streamAlias)) {
                if (streamAlias == SCO_STATE_ACTIVE_INTERNAL) {
                    setSystemAudioMute(state);
                }
                for (int stream = SENDMSG_REPLACE; stream < this.mStreamStates.length; stream += SENDMSG_NOOP) {
                    if (streamAlias == this.mStreamVolumeAlias[stream]) {
                        this.mStreamStates[stream].mute(cb, state);
                        Intent intent = new Intent(AudioManager.STREAM_MUTE_CHANGED_ACTION);
                        intent.putExtra(AudioManager.EXTRA_VOLUME_STREAM_TYPE, stream);
                        intent.putExtra(AudioManager.EXTRA_STREAM_VOLUME_MUTED, state);
                        sendBroadcastToAll(intent);
                    }
                }
            }
        }
    }

    private void setSystemAudioMute(boolean state) {
        if (this.mHdmiManager != null && this.mHdmiTvClient != null) {
            synchronized (this.mHdmiManager) {
                if (this.mHdmiSystemAudioSupported) {
                    synchronized (this.mHdmiTvClient) {
                        long token = Binder.clearCallingIdentity();
                        try {
                            this.mHdmiTvClient.setSystemAudioMute(state);
                            Binder.restoreCallingIdentity(token);
                        } catch (Throwable th) {
                            Binder.restoreCallingIdentity(token);
                        }
                    }
                    return;
                }
            }
        }
    }

    public boolean isStreamMute(int streamType) {
        boolean access$300;
        if (streamType == ExploreByTouchHelper.INVALID_ID) {
            streamType = getActiveStreamType(streamType);
        }
        synchronized (VolumeStreamState.class) {
            access$300 = this.mStreamStates[streamType].isMuted_syncVSS();
        }
        return access$300;
    }

    private boolean discardRmtSbmxFullVolDeathHandlerFor(IBinder cb) {
        Iterator<RmtSbmxFullVolDeathHandler> it = this.mRmtSbmxFullVolDeathHandlers.iterator();
        while (it.hasNext()) {
            RmtSbmxFullVolDeathHandler handler = (RmtSbmxFullVolDeathHandler) it.next();
            if (handler.isHandlerFor(cb)) {
                handler.forget();
                this.mRmtSbmxFullVolDeathHandlers.remove(handler);
                return PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
            }
        }
        return DEBUG_VOL;
    }

    private boolean hasRmtSbmxFullVolDeathHandlerFor(IBinder cb) {
        Iterator<RmtSbmxFullVolDeathHandler> it = this.mRmtSbmxFullVolDeathHandlers.iterator();
        while (it.hasNext()) {
            if (((RmtSbmxFullVolDeathHandler) it.next()).isHandlerFor(cb)) {
                return PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
            }
        }
        return DEBUG_VOL;
    }

    public void forceRemoteSubmixFullVolume(boolean startForcing, IBinder cb) {
        if (cb != null) {
            if (this.mContext.checkCallingOrSelfPermission("android.permission.CAPTURE_AUDIO_OUTPUT") != 0) {
                Log.w(TAG, "Trying to call forceRemoteSubmixFullVolume() without CAPTURE_AUDIO_OUTPUT");
                return;
            }
            synchronized (this.mRmtSbmxFullVolDeathHandlers) {
                boolean applyRequired = DEBUG_VOL;
                if (startForcing) {
                    if (!hasRmtSbmxFullVolDeathHandlerFor(cb)) {
                        this.mRmtSbmxFullVolDeathHandlers.add(new RmtSbmxFullVolDeathHandler(this, cb));
                        if (this.mRmtSbmxFullVolRefCount == 0) {
                            this.mFullVolumeDevices |= DateUtils.FORMAT_ABBREV_WEEKDAY;
                            this.mFixedVolumeDevices |= DateUtils.FORMAT_ABBREV_WEEKDAY;
                            applyRequired = PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
                        }
                        this.mRmtSbmxFullVolRefCount += SENDMSG_NOOP;
                    }
                } else if (discardRmtSbmxFullVolDeathHandlerFor(cb) && this.mRmtSbmxFullVolRefCount > 0) {
                    this.mRmtSbmxFullVolRefCount += SCO_MODE_UNDEFINED;
                    if (this.mRmtSbmxFullVolRefCount == 0) {
                        this.mFullVolumeDevices &= -32769;
                        this.mFixedVolumeDevices &= -32769;
                        applyRequired = PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
                    }
                }
                if (applyRequired) {
                    checkAllFixedVolumeDevices(SCO_STATE_ACTIVE_INTERNAL);
                    this.mStreamStates[SCO_STATE_ACTIVE_INTERNAL].applyAllVolumes();
                }
            }
        }
    }

    public void setMasterMute(boolean state, int flags, String callingPackage, IBinder cb) {
        setMasterMuteInternal(state, flags, callingPackage, cb, Binder.getCallingUid());
    }

    private void setMasterMuteInternal(boolean state, int flags, String callingPackage, IBinder cb, int uid) {
        if (!this.mUseFixedVolume && this.mAppOps.noteOp(33, uid, callingPackage) == 0 && state != AudioSystem.getMasterMute()) {
            int i;
            setSystemAudioMute(state);
            AudioSystem.setMasterMute(state);
            Handler handler = this.mAudioHandler;
            if (state) {
                i = SENDMSG_NOOP;
            } else {
                i = SENDMSG_REPLACE;
            }
            sendMsg(handler, MSG_PERSIST_MASTER_VOLUME_MUTE, SENDMSG_REPLACE, i, UserHandle.getCallingUserId(), null, PERSIST_DELAY);
            sendMasterMuteUpdate(state, flags);
            Intent intent = new Intent(AudioManager.MASTER_MUTE_CHANGED_ACTION);
            intent.putExtra(AudioManager.EXTRA_MASTER_VOLUME_MUTED, state);
            sendBroadcastToAll(intent);
        }
    }

    public boolean isMasterMute() {
        return AudioSystem.getMasterMute();
    }

    protected static int getMaxStreamVolume(int streamType) {
        return MAX_STREAM_VOLUME[streamType];
    }

    public static int getDefaultStreamVolume(int streamType) {
        return DEFAULT_STREAM_VOLUME[streamType];
    }

    public int getStreamVolume(int streamType) {
        int i;
        ensureValidStreamType(streamType);
        int device = getDeviceForStream(streamType);
        synchronized (VolumeStreamState.class) {
            int index = this.mStreamStates[streamType].getIndex(device);
            if (this.mStreamStates[streamType].isMuted_syncVSS()) {
                index = SENDMSG_REPLACE;
            }
            if (!(index == 0 || this.mStreamVolumeAlias[streamType] != SCO_STATE_ACTIVE_INTERNAL || (this.mFixedVolumeDevices & device) == 0)) {
                index = this.mStreamStates[streamType].getMaxIndex();
            }
            i = (index + SCO_STATE_DEACTIVATE_REQ) / MSG_SET_ALL_VOLUMES;
        }
        return i;
    }

    public int getMasterVolume() {
        if (isMasterMute()) {
            return SENDMSG_REPLACE;
        }
        return getLastAudibleMasterVolume();
    }

    public void setMasterVolume(int volume, int flags, String callingPackage) {
        setMasterVolume(volume, flags, callingPackage, Binder.getCallingUid());
    }

    public void setMasterVolume(int volume, int flags, String callingPackage, int uid) {
        if (!this.mUseFixedVolume && this.mAppOps.noteOp(33, uid, callingPackage) == 0) {
            if (volume < 0) {
                volume = SENDMSG_REPLACE;
            } else if (volume > MSG_SET_WIRED_DEVICE_CONNECTION_STATE) {
                volume = MSG_SET_WIRED_DEVICE_CONNECTION_STATE;
            }
            doSetMasterVolume(((float) volume) / 100.0f, flags);
        }
    }

    private void doSetMasterVolume(float volume, int flags) {
        if (!AudioSystem.getMasterMute()) {
            int oldVolume = getMasterVolume();
            AudioSystem.setMasterVolume(volume);
            int newVolume = getMasterVolume();
            if (newVolume != oldVolume) {
                sendMsg(this.mAudioHandler, SENDMSG_QUEUE, SENDMSG_REPLACE, Math.round(1000.0f * volume), SENDMSG_REPLACE, null, PERSIST_DELAY);
                setSystemAudioVolume(oldVolume, newVolume, getMasterMaxVolume(), flags);
            }
            sendMasterVolumeUpdate(flags, oldVolume, newVolume);
        }
    }

    public int getStreamMaxVolume(int streamType) {
        ensureValidStreamType(streamType);
        return (this.mStreamStates[streamType].getMaxIndex() + SCO_STATE_DEACTIVATE_REQ) / MSG_SET_ALL_VOLUMES;
    }

    public int getMasterMaxVolume() {
        return MSG_SET_WIRED_DEVICE_CONNECTION_STATE;
    }

    public int getLastAudibleStreamVolume(int streamType) {
        ensureValidStreamType(streamType);
        return (this.mStreamStates[streamType].getIndex(getDeviceForStream(streamType)) + SCO_STATE_DEACTIVATE_REQ) / MSG_SET_ALL_VOLUMES;
    }

    public int getLastAudibleMasterVolume() {
        return Math.round(AudioSystem.getMasterVolume() * 100.0f);
    }

    public int getMasterStreamType() {
        return this.mStreamVolumeAlias[SENDMSG_NOOP];
    }

    public void setMicrophoneMute(boolean on, String callingPackage) {
        if (this.mAppOps.noteOp(44, Binder.getCallingUid(), callingPackage) == 0 && checkAudioSettingsPermission("setMicrophoneMute()")) {
            AudioSystem.muteMicrophone(on);
            sendMsg(this.mAudioHandler, MSG_PERSIST_MICROPHONE_MUTE, SENDMSG_REPLACE, on ? SENDMSG_NOOP : SENDMSG_REPLACE, UserHandle.getCallingUserId(), null, PERSIST_DELAY);
        }
    }

    public int getRingerModeExternal() {
        int i;
        synchronized (this.mSettingsLock) {
            i = this.mRingerModeExternal;
        }
        return i;
    }

    public int getRingerModeInternal() {
        int i;
        synchronized (this.mSettingsLock) {
            i = this.mRingerMode;
        }
        return i;
    }

    private void ensureValidRingerMode(int ringerMode) {
        if (!isValidRingerMode(ringerMode)) {
            throw new IllegalArgumentException("Bad ringer mode " + ringerMode);
        }
    }

    public boolean isValidRingerMode(int ringerMode) {
        return (ringerMode < 0 || ringerMode > SENDMSG_QUEUE) ? DEBUG_VOL : PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
    }

    public void setRingerModeExternal(int ringerMode, String caller) {
        setRingerMode(ringerMode, caller, PREVENT_VOLUME_ADJUSTMENT_IF_SILENT);
    }

    public void setRingerModeInternal(int ringerMode, String caller) {
        enforceSelfOrSystemUI("setRingerModeInternal");
        setRingerMode(ringerMode, caller, DEBUG_VOL);
    }

    private void setRingerMode(int ringerMode, String caller, boolean external) {
        if (!this.mUseFixedVolume && !isPlatformTelevision()) {
            if (caller == null || caller.length() == 0) {
                throw new IllegalArgumentException("Bad caller: " + caller);
            }
            ensureValidRingerMode(ringerMode);
            if (ringerMode == SENDMSG_NOOP && !this.mHasVibrator) {
                ringerMode = SENDMSG_REPLACE;
            }
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (this.mSettingsLock) {
                    int ringerModeInternal = getRingerModeInternal();
                    int ringerModeExternal = getRingerModeExternal();
                    if (external) {
                        setRingerModeExt(ringerMode);
                        if (this.mRingerModeDelegate != null) {
                            ringerMode = this.mRingerModeDelegate.onSetRingerModeExternal(ringerModeExternal, ringerMode, caller, ringerModeInternal);
                        }
                        if (ringerMode != ringerModeInternal) {
                            setRingerModeInt(ringerMode, PREVENT_VOLUME_ADJUSTMENT_IF_SILENT);
                        }
                    } else {
                        if (ringerMode != ringerModeInternal) {
                            setRingerModeInt(ringerMode, PREVENT_VOLUME_ADJUSTMENT_IF_SILENT);
                        }
                        if (this.mRingerModeDelegate != null) {
                            ringerMode = this.mRingerModeDelegate.onSetRingerModeInternal(ringerModeInternal, ringerMode, caller, ringerModeExternal);
                        }
                        setRingerModeExt(ringerMode);
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(identity);
            }
        }
    }

    private void setRingerModeExt(int ringerMode) {
        synchronized (this.mSettingsLock) {
            if (ringerMode == this.mRingerModeExternal) {
                return;
            }
            this.mRingerModeExternal = ringerMode;
            broadcastRingerMode(AudioManager.RINGER_MODE_CHANGED_ACTION, ringerMode);
        }
    }

    private void setRingerModeInt(int ringerMode, boolean persist) {
        synchronized (this.mSettingsLock) {
            boolean change = this.mRingerMode != ringerMode ? PREVENT_VOLUME_ADJUSTMENT_IF_SILENT : DEBUG_VOL;
            this.mRingerMode = ringerMode;
        }
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        boolean ringerModeMute = (ringerMode == SENDMSG_NOOP || ringerMode == 0) ? PREVENT_VOLUME_ADJUSTMENT_IF_SILENT : DEBUG_VOL;
        int streamType = numStreamTypes + SCO_MODE_UNDEFINED;
        while (streamType >= 0) {
            boolean isMuted = isStreamMutedByRingerMode(streamType);
            boolean shouldMute = (ringerModeMute && isStreamAffectedByRingerMode(streamType)) ? PREVENT_VOLUME_ADJUSTMENT_IF_SILENT : DEBUG_VOL;
            if (isMuted != shouldMute) {
                if (shouldMute) {
                    this.mStreamStates[streamType].mute(null, PREVENT_VOLUME_ADJUSTMENT_IF_SILENT);
                    this.mRingerModeMutedStreams |= SENDMSG_NOOP << streamType;
                } else {
                    if ((isPlatformVoice() || this.mHasVibrator) && this.mStreamVolumeAlias[streamType] == SENDMSG_QUEUE) {
                        synchronized (VolumeStreamState.class) {
                            for (Entry entry : this.mStreamStates[streamType].mIndex.entrySet()) {
                                if (((Integer) entry.getValue()).intValue() == 0) {
                                    entry.setValue(Integer.valueOf(MSG_SET_ALL_VOLUMES));
                                }
                            }
                        }
                    }
                    this.mStreamStates[streamType].mute(null, DEBUG_VOL);
                    this.mRingerModeMutedStreams &= (SENDMSG_NOOP << streamType) ^ SCO_MODE_UNDEFINED;
                }
            }
            streamType += SCO_MODE_UNDEFINED;
        }
        if (persist) {
            sendMsg(this.mAudioHandler, SCO_STATE_ACTIVE_INTERNAL, SENDMSG_REPLACE, SENDMSG_REPLACE, SENDMSG_REPLACE, null, PERSIST_DELAY);
        }
        if (change) {
            broadcastRingerMode(AudioManager.INTERNAL_RINGER_MODE_CHANGED_ACTION, ringerMode);
        }
    }

    private void restoreMasterVolume() {
        if (this.mUseFixedVolume) {
            AudioSystem.setMasterVolume(RemoteControlClient.PLAYBACK_SPEED_1X);
        } else if (this.mUseMasterVolume) {
            float volume = System.getFloatForUser(this.mContentResolver, "volume_master", -1.0f, -2);
            if (volume >= 0.0f) {
                AudioSystem.setMasterVolume(volume);
            }
        }
    }

    public boolean shouldVibrate(int vibrateType) {
        boolean z = PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
        if (!this.mHasVibrator) {
            return DEBUG_VOL;
        }
        switch (getVibrateSetting(vibrateType)) {
            case SENDMSG_REPLACE /*0*/:
                return DEBUG_VOL;
            case SENDMSG_NOOP /*1*/:
                if (getRingerModeExternal() == 0) {
                    z = DEBUG_VOL;
                }
                return z;
            case SENDMSG_QUEUE /*2*/:
                if (getRingerModeExternal() != SENDMSG_NOOP) {
                    z = DEBUG_VOL;
                }
                return z;
            default:
                return DEBUG_VOL;
        }
    }

    public int getVibrateSetting(int vibrateType) {
        if (this.mHasVibrator) {
            return (this.mVibrateSetting >> (vibrateType * SENDMSG_QUEUE)) & SCO_STATE_ACTIVE_INTERNAL;
        }
        return SENDMSG_REPLACE;
    }

    public void setVibrateSetting(int vibrateType, int vibrateSetting) {
        if (this.mHasVibrator) {
            this.mVibrateSetting = getValueForVibrateSetting(this.mVibrateSetting, vibrateType, vibrateSetting);
            broadcastVibrateSetting(vibrateType);
        }
    }

    public static int getValueForVibrateSetting(int existingValue, int vibrateType, int vibrateSetting) {
        return (existingValue & ((SCO_STATE_ACTIVE_INTERNAL << (vibrateType * SENDMSG_QUEUE)) ^ SCO_MODE_UNDEFINED)) | ((vibrateSetting & SCO_STATE_ACTIVE_INTERNAL) << (vibrateType * SENDMSG_QUEUE));
    }

    public void setMode(int mode, IBinder cb) {
        if (DEBUG_MODE) {
            Log.v(TAG, "setMode(mode=" + mode + ")");
        }
        if (!checkAudioSettingsPermission("setMode()")) {
            return;
        }
        if (mode == SENDMSG_QUEUE && this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE") != 0) {
            Log.w(TAG, "MODIFY_PHONE_STATE Permission Denial: setMode(MODE_IN_CALL) from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
        } else if (mode >= SCO_MODE_UNDEFINED && mode < SCO_STATE_DEACTIVATE_EXT_REQ) {
            int newModeOwnerPid;
            synchronized (this.mSetModeDeathHandlers) {
                if (mode == SCO_MODE_UNDEFINED) {
                    mode = this.mMode;
                }
                newModeOwnerPid = setModeInt(mode, cb, Binder.getCallingPid());
            }
            if (newModeOwnerPid != 0) {
                disconnectBluetoothSco(newModeOwnerPid);
            }
        }
    }

    private int setModeInt(int mode, IBinder cb, int pid) {
        if (DEBUG_MODE) {
            Log.v(TAG, "setModeInt(mode=" + mode + ", pid=" + pid + ")");
        }
        int newModeOwnerPid = SENDMSG_REPLACE;
        if (cb == null) {
            Log.e(TAG, "setModeInt() called with null binder");
            return SENDMSG_REPLACE;
        }
        SetModeDeathHandler hdlr = null;
        Iterator iter = this.mSetModeDeathHandlers.iterator();
        while (iter.hasNext()) {
            SetModeDeathHandler h = (SetModeDeathHandler) iter.next();
            if (h.getPid() == pid) {
                hdlr = h;
                iter.remove();
                hdlr.getBinder().unlinkToDeath(hdlr, SENDMSG_REPLACE);
                break;
            }
        }
        do {
            int status;
            if (mode != 0) {
                if (hdlr == null) {
                    hdlr = new SetModeDeathHandler(this, cb, pid);
                }
                try {
                    cb.linkToDeath(hdlr, SENDMSG_REPLACE);
                } catch (RemoteException e) {
                    Log.w(TAG, "setMode() could not link to " + cb + " binder death");
                }
                this.mSetModeDeathHandlers.add(SENDMSG_REPLACE, hdlr);
                hdlr.setMode(mode);
            } else if (!this.mSetModeDeathHandlers.isEmpty()) {
                hdlr = (SetModeDeathHandler) this.mSetModeDeathHandlers.get(SENDMSG_REPLACE);
                cb = hdlr.getBinder();
                mode = hdlr.getMode();
                if (DEBUG_MODE) {
                    Log.w(TAG, " using mode=" + mode + " instead due to death hdlr at pid=" + SetModeDeathHandler.access$1600(hdlr));
                }
            }
            if (mode != this.mMode) {
                status = AudioSystem.setPhoneState(mode);
                if (status == 0) {
                    if (DEBUG_MODE) {
                        Log.v(TAG, " mode successfully set to " + mode);
                    }
                    this.mMode = mode;
                } else {
                    if (hdlr != null) {
                        this.mSetModeDeathHandlers.remove(hdlr);
                        cb.unlinkToDeath(hdlr, SENDMSG_REPLACE);
                    }
                    if (DEBUG_MODE) {
                        Log.w(TAG, " mode set to MODE_NORMAL after phoneState pb");
                    }
                    mode = SENDMSG_REPLACE;
                }
            } else {
                status = SENDMSG_REPLACE;
            }
            if (status == 0) {
                break;
            }
        } while (!this.mSetModeDeathHandlers.isEmpty());
        if (status == 0) {
            if (mode != 0) {
                if (this.mSetModeDeathHandlers.isEmpty()) {
                    Log.e(TAG, "setMode() different from MODE_NORMAL with empty mode client stack");
                } else {
                    newModeOwnerPid = ((SetModeDeathHandler) this.mSetModeDeathHandlers.get(SENDMSG_REPLACE)).getPid();
                }
            }
            int streamType = getActiveStreamType(ExploreByTouchHelper.INVALID_ID);
            int device = getDeviceForStream(streamType);
            setStreamVolumeInt(this.mStreamVolumeAlias[streamType], this.mStreamStates[this.mStreamVolumeAlias[streamType]].getIndex(device), device, PREVENT_VOLUME_ADJUSTMENT_IF_SILENT);
            updateStreamVolumeAlias(PREVENT_VOLUME_ADJUSTMENT_IF_SILENT);
        }
        return newModeOwnerPid;
    }

    public int getMode() {
        return this.mMode;
    }

    private void loadTouchSoundAssetDefaults() {
        SOUND_EFFECT_FILES.add("Effect_Tick.ogg");
        for (int i = SENDMSG_REPLACE; i < MSG_SET_ALL_VOLUMES; i += SENDMSG_NOOP) {
            this.SOUND_EFFECT_FILES_MAP[i][SENDMSG_REPLACE] = SENDMSG_REPLACE;
            this.SOUND_EFFECT_FILES_MAP[i][SENDMSG_NOOP] = SCO_MODE_UNDEFINED;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void loadTouchSoundAssets() {
        /*
        r14 = this;
        r9 = 0;
        r11 = SOUND_EFFECT_FILES;
        r11 = r11.isEmpty();
        if (r11 != 0) goto L_0x000a;
    L_0x0009:
        return;
    L_0x000a:
        r14.loadTouchSoundAssetDefaults();
        r11 = r14.mContext;	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        r11 = r11.getResources();	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        r12 = 17891329; // 0x1110001 float:2.6632297E-38 double:8.839491E-317;
        r9 = r11.getXml(r12);	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        r11 = "audio_assets";
        com.android.internal.util.XmlUtils.beginDocument(r9, r11);	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        r11 = 0;
        r12 = "version";
        r10 = r9.getAttributeValue(r11, r12);	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        r7 = 0;
        r11 = "1.0";
        r11 = r11.equals(r10);	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        if (r11 == 0) goto L_0x0043;
    L_0x002f:
        com.android.internal.util.XmlUtils.nextElement(r9);	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        r1 = r9.getName();	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        if (r1 != 0) goto L_0x0049;
    L_0x0038:
        if (r7 == 0) goto L_0x0043;
    L_0x003a:
        com.android.internal.util.XmlUtils.nextElement(r9);	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        r1 = r9.getName();	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        if (r1 != 0) goto L_0x0062;
    L_0x0043:
        if (r9 == 0) goto L_0x0009;
    L_0x0045:
        r9.close();
        goto L_0x0009;
    L_0x0049:
        r11 = "group";
        r11 = r1.equals(r11);	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        if (r11 == 0) goto L_0x002f;
    L_0x0051:
        r11 = 0;
        r12 = "name";
        r8 = r9.getAttributeValue(r11, r12);	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        r11 = "touch_sounds";
        r11 = r11.equals(r8);	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        if (r11 == 0) goto L_0x002f;
    L_0x0060:
        r7 = 1;
        goto L_0x0038;
    L_0x0062:
        r11 = "asset";
        r11 = r1.equals(r11);	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        if (r11 == 0) goto L_0x0043;
    L_0x006a:
        r11 = 0;
        r12 = "id";
        r6 = r9.getAttributeValue(r11, r12);	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        r11 = 0;
        r12 = "file";
        r3 = r9.getAttributeValue(r11, r12);	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        r11 = android.media.AudioManager.class;
        r2 = r11.getField(r6);	 Catch:{ Exception -> 0x00ae }
        r11 = 0;
        r4 = r2.getInt(r11);	 Catch:{ Exception -> 0x00ae }
        r11 = SOUND_EFFECT_FILES;	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        r5 = r11.indexOf(r3);	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        r11 = -1;
        if (r5 != r11) goto L_0x0097;
    L_0x008c:
        r11 = SOUND_EFFECT_FILES;	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        r5 = r11.size();	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        r11 = SOUND_EFFECT_FILES;	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        r11.add(r3);	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
    L_0x0097:
        r11 = r14.SOUND_EFFECT_FILES_MAP;	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        r11 = r11[r4];	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        r12 = 0;
        r11[r12] = r5;	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        goto L_0x0038;
    L_0x009f:
        r0 = move-exception;
        r11 = "AudioService";
        r12 = "audio assets file not found";
        android.util.Log.w(r11, r12, r0);	 Catch:{ all -> 0x00e7 }
        if (r9 == 0) goto L_0x0009;
    L_0x00a9:
        r9.close();
        goto L_0x0009;
    L_0x00ae:
        r0 = move-exception;
        r11 = "AudioService";
        r12 = new java.lang.StringBuilder;	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        r12.<init>();	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        r13 = "Invalid touch sound ID: ";
        r12 = r12.append(r13);	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        r12 = r12.append(r6);	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        r12 = r12.toString();	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        android.util.Log.w(r11, r12);	 Catch:{ NotFoundException -> 0x009f, XmlPullParserException -> 0x00c9, IOException -> 0x00d8 }
        goto L_0x0038;
    L_0x00c9:
        r0 = move-exception;
        r11 = "AudioService";
        r12 = "XML parser exception reading touch sound assets";
        android.util.Log.w(r11, r12, r0);	 Catch:{ all -> 0x00e7 }
        if (r9 == 0) goto L_0x0009;
    L_0x00d3:
        r9.close();
        goto L_0x0009;
    L_0x00d8:
        r0 = move-exception;
        r11 = "AudioService";
        r12 = "I/O exception reading touch sound assets";
        android.util.Log.w(r11, r12, r0);	 Catch:{ all -> 0x00e7 }
        if (r9 == 0) goto L_0x0009;
    L_0x00e2:
        r9.close();
        goto L_0x0009;
    L_0x00e7:
        r11 = move-exception;
        if (r9 == 0) goto L_0x00ed;
    L_0x00ea:
        r9.close();
    L_0x00ed:
        throw r11;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.loadTouchSoundAssets():void");
    }

    public void playSoundEffect(int effectType) {
        playSoundEffectVolume(effectType, -1.0f);
    }

    public void playSoundEffectVolume(int effectType, float volume) {
        if (effectType >= MSG_SET_ALL_VOLUMES || effectType < 0) {
            Log.w(TAG, "AudioService effectType value " + effectType + " out of range");
            return;
        }
        sendMsg(this.mAudioHandler, SCO_STATE_DEACTIVATE_REQ, SENDMSG_QUEUE, effectType, (int) (1000.0f * volume), null, SENDMSG_REPLACE);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean loadSoundEffects() {
        /*
        r12 = this;
        r10 = 1;
        r11 = 0;
        r7 = 3;
        r5 = new android.media.AudioService$LoadSoundEffectReply;
        r5.<init>(r12);
        monitor-enter(r5);
        r0 = r12.mAudioHandler;	 Catch:{ all -> 0x0035 }
        r1 = 7;
        r2 = 2;
        r3 = 0;
        r4 = 0;
        r6 = 0;
        sendMsg(r0, r1, r2, r3, r4, r5, r6);	 Catch:{ all -> 0x0035 }
        r8 = r7;
    L_0x0014:
        r0 = r5.mStatus;	 Catch:{ all -> 0x003a }
        if (r0 != r10) goto L_0x002d;
    L_0x0018:
        r7 = r8 + -1;
        if (r8 <= 0) goto L_0x002e;
    L_0x001c:
        r0 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
        r5.wait(r0);	 Catch:{ InterruptedException -> 0x0023 }
        r8 = r7;
        goto L_0x0014;
    L_0x0023:
        r9 = move-exception;
        r0 = "AudioService";
        r1 = "loadSoundEffects Interrupted while waiting sound pool loaded.";
        android.util.Log.w(r0, r1);	 Catch:{ all -> 0x0035 }
        r8 = r7;
        goto L_0x0014;
    L_0x002d:
        r7 = r8;
    L_0x002e:
        monitor-exit(r5);	 Catch:{ all -> 0x0035 }
        r0 = r5.mStatus;
        if (r0 != 0) goto L_0x0038;
    L_0x0033:
        r0 = r10;
    L_0x0034:
        return r0;
    L_0x0035:
        r0 = move-exception;
    L_0x0036:
        monitor-exit(r5);	 Catch:{ all -> 0x0035 }
        throw r0;
    L_0x0038:
        r0 = r11;
        goto L_0x0034;
    L_0x003a:
        r0 = move-exception;
        r7 = r8;
        goto L_0x0036;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.loadSoundEffects():boolean");
    }

    public void unloadSoundEffects() {
        sendMsg(this.mAudioHandler, MSG_UNLOAD_SOUND_EFFECTS, SENDMSG_QUEUE, SENDMSG_REPLACE, SENDMSG_REPLACE, null, SENDMSG_REPLACE);
    }

    public void reloadAudioSettings() {
        readAudioSettings(DEBUG_VOL);
    }

    private void readAudioSettings(boolean userSwitch) {
        readPersistedSettings();
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        int streamType = SENDMSG_REPLACE;
        while (streamType < numStreamTypes) {
            VolumeStreamState streamState = this.mStreamStates[streamType];
            if (!userSwitch || this.mStreamVolumeAlias[streamType] != SCO_STATE_ACTIVE_INTERNAL) {
                streamState.readSettings();
                synchronized (VolumeStreamState.class) {
                    if (streamState.isMuted_syncVSS() && (!(isStreamAffectedByMute(streamType) || isStreamMutedByRingerMode(streamType)) || this.mUseFixedVolume)) {
                        int size = streamState.mDeathHandlers.size();
                        for (int i = SENDMSG_REPLACE; i < size; i += SENDMSG_NOOP) {
                            VolumeDeathHandler.access$2302((VolumeDeathHandler) streamState.mDeathHandlers.get(i), SENDMSG_NOOP);
                            ((VolumeDeathHandler) streamState.mDeathHandlers.get(i)).mute_syncVSS(DEBUG_VOL);
                        }
                    }
                }
            }
            streamType += SENDMSG_NOOP;
        }
        setRingerModeInt(getRingerModeInternal(), DEBUG_VOL);
        checkAllFixedVolumeDevices();
        checkAllAliasStreamVolumes();
        synchronized (this.mSafeMediaVolumeState) {
            this.mMusicActiveMs = MathUtils.constrain(Secure.getIntForUser(this.mContentResolver, "unsafe_volume_music_active_ms", SENDMSG_REPLACE, -2), (int) SENDMSG_REPLACE, (int) UNSAFE_VOLUME_MUSIC_ACTIVE_MS_MAX);
            if (this.mSafeMediaVolumeState.intValue() == SCO_STATE_ACTIVE_INTERNAL) {
                enforceSafeMediaVolume();
            }
        }
    }

    public void setSpeakerphoneOn(boolean on) {
        if (checkAudioSettingsPermission("setSpeakerphoneOn()")) {
            if (on) {
                if (this.mForcedUseForComm == SCO_STATE_ACTIVE_INTERNAL) {
                    sendMsg(this.mAudioHandler, MSG_SET_FORCE_USE, SENDMSG_QUEUE, SENDMSG_QUEUE, SENDMSG_REPLACE, null, SENDMSG_REPLACE);
                }
                this.mForcedUseForComm = SENDMSG_NOOP;
            } else if (this.mForcedUseForComm == SENDMSG_NOOP) {
                this.mForcedUseForComm = SENDMSG_REPLACE;
            }
            sendMsg(this.mAudioHandler, MSG_SET_FORCE_USE, SENDMSG_QUEUE, SENDMSG_REPLACE, this.mForcedUseForComm, null, SENDMSG_REPLACE);
        }
    }

    public boolean isSpeakerphoneOn() {
        return this.mForcedUseForComm == SENDMSG_NOOP ? PREVENT_VOLUME_ADJUSTMENT_IF_SILENT : DEBUG_VOL;
    }

    public void setBluetoothScoOn(boolean on) {
        if (checkAudioSettingsPermission("setBluetoothScoOn()")) {
            if (on) {
                this.mForcedUseForComm = SCO_STATE_ACTIVE_INTERNAL;
            } else if (this.mForcedUseForComm == SCO_STATE_ACTIVE_INTERNAL) {
                this.mForcedUseForComm = SENDMSG_REPLACE;
            }
            sendMsg(this.mAudioHandler, MSG_SET_FORCE_USE, SENDMSG_QUEUE, SENDMSG_REPLACE, this.mForcedUseForComm, null, SENDMSG_REPLACE);
            sendMsg(this.mAudioHandler, MSG_SET_FORCE_USE, SENDMSG_QUEUE, SENDMSG_QUEUE, this.mForcedUseForComm, null, SENDMSG_REPLACE);
        }
    }

    public boolean isBluetoothScoOn() {
        return this.mForcedUseForComm == SCO_STATE_ACTIVE_INTERNAL ? PREVENT_VOLUME_ADJUSTMENT_IF_SILENT : DEBUG_VOL;
    }

    public void setBluetoothA2dpOn(boolean on) {
        int i = SENDMSG_REPLACE;
        synchronized (this.mBluetoothA2dpEnabledLock) {
            this.mBluetoothA2dpEnabled = on;
            Handler handler = this.mAudioHandler;
            if (!this.mBluetoothA2dpEnabled) {
                i = MSG_SET_ALL_VOLUMES;
            }
            sendMsg(handler, MSG_SET_FORCE_BT_A2DP_USE, SENDMSG_QUEUE, SENDMSG_NOOP, i, null, SENDMSG_REPLACE);
        }
    }

    public boolean isBluetoothA2dpOn() {
        boolean z;
        synchronized (this.mBluetoothA2dpEnabledLock) {
            z = this.mBluetoothA2dpEnabled;
        }
        return z;
    }

    public void startBluetoothSco(IBinder cb, int targetSdkVersion) {
        startBluetoothScoInt(cb, targetSdkVersion < MSG_PERSIST_SAFE_VOLUME_STATE ? SENDMSG_REPLACE : SCO_MODE_UNDEFINED);
    }

    public void startBluetoothScoVirtualCall(IBinder cb) {
        startBluetoothScoInt(cb, SENDMSG_REPLACE);
    }

    void startBluetoothScoInt(IBinder cb, int scoAudioMode) {
        if (checkAudioSettingsPermission("startBluetoothSco()") && this.mSystemReady) {
            ScoClient client = getScoClient(cb, PREVENT_VOLUME_ADJUSTMENT_IF_SILENT);
            long ident = Binder.clearCallingIdentity();
            client.incCount(scoAudioMode);
            Binder.restoreCallingIdentity(ident);
        }
    }

    public void stopBluetoothSco(IBinder cb) {
        if (checkAudioSettingsPermission("stopBluetoothSco()") && this.mSystemReady) {
            ScoClient client = getScoClient(cb, DEBUG_VOL);
            long ident = Binder.clearCallingIdentity();
            if (client != null) {
                client.decCount();
            }
            Binder.restoreCallingIdentity(ident);
        }
    }

    private void checkScoAudioState() {
        if (this.mBluetoothHeadset != null && this.mBluetoothHeadsetDevice != null && this.mScoAudioState == 0 && this.mBluetoothHeadset.getAudioState(this.mBluetoothHeadsetDevice) != MSG_SET_ALL_VOLUMES) {
            this.mScoAudioState = SENDMSG_QUEUE;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.media.AudioService.ScoClient getScoClient(android.os.IBinder r7, boolean r8) {
        /*
        r6 = this;
        r5 = r6.mScoClients;
        monitor-enter(r5);
        r0 = 0;
        r4 = r6.mScoClients;	 Catch:{ all -> 0x0032 }
        r3 = r4.size();	 Catch:{ all -> 0x0032 }
        r2 = 0;
        r1 = r0;
    L_0x000c:
        if (r2 >= r3) goto L_0x0023;
    L_0x000e:
        r4 = r6.mScoClients;	 Catch:{ all -> 0x0035 }
        r0 = r4.get(r2);	 Catch:{ all -> 0x0035 }
        r0 = (android.media.AudioService.ScoClient) r0;	 Catch:{ all -> 0x0035 }
        r4 = r0.getBinder();	 Catch:{ all -> 0x0032 }
        if (r4 != r7) goto L_0x001f;
    L_0x001c:
        monitor-exit(r5);	 Catch:{ all -> 0x0032 }
        r1 = r0;
    L_0x001e:
        return r1;
    L_0x001f:
        r2 = r2 + 1;
        r1 = r0;
        goto L_0x000c;
    L_0x0023:
        if (r8 == 0) goto L_0x0038;
    L_0x0025:
        r0 = new android.media.AudioService$ScoClient;	 Catch:{ all -> 0x0035 }
        r0.<init>(r6, r7);	 Catch:{ all -> 0x0035 }
        r4 = r6.mScoClients;	 Catch:{ all -> 0x0032 }
        r4.add(r0);	 Catch:{ all -> 0x0032 }
    L_0x002f:
        monitor-exit(r5);	 Catch:{ all -> 0x0032 }
        r1 = r0;
        goto L_0x001e;
    L_0x0032:
        r4 = move-exception;
    L_0x0033:
        monitor-exit(r5);	 Catch:{ all -> 0x0032 }
        throw r4;
    L_0x0035:
        r4 = move-exception;
        r0 = r1;
        goto L_0x0033;
    L_0x0038:
        r0 = r1;
        goto L_0x002f;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.getScoClient(android.os.IBinder, boolean):android.media.AudioService$ScoClient");
    }

    public void clearAllScoClients(int exceptPid, boolean stopSco) {
        synchronized (this.mScoClients) {
            ScoClient savedClient = null;
            int size = this.mScoClients.size();
            for (int i = SENDMSG_REPLACE; i < size; i += SENDMSG_NOOP) {
                ScoClient cl = (ScoClient) this.mScoClients.get(i);
                if (cl.getPid() != exceptPid) {
                    cl.clearCount(stopSco);
                } else {
                    savedClient = cl;
                }
            }
            this.mScoClients.clear();
            if (savedClient != null) {
                this.mScoClients.add(savedClient);
            }
        }
    }

    private boolean getBluetoothHeadset() {
        int i;
        boolean result = DEBUG_VOL;
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            result = adapter.getProfileProxy(this.mContext, this.mBluetoothProfileServiceListener, SENDMSG_NOOP);
        }
        Handler handler = this.mAudioHandler;
        if (result) {
            i = BT_HEADSET_CNCT_TIMEOUT_MS;
        } else {
            i = SENDMSG_REPLACE;
        }
        sendMsg(handler, MSG_BT_HEADSET_CNCT_FAILED, SENDMSG_REPLACE, SENDMSG_REPLACE, SENDMSG_REPLACE, null, i);
        return result;
    }

    private void disconnectBluetoothSco(int exceptPid) {
        synchronized (this.mScoClients) {
            checkScoAudioState();
            if (this.mScoAudioState != SENDMSG_QUEUE && this.mScoAudioState != SCO_STATE_DEACTIVATE_EXT_REQ) {
                clearAllScoClients(exceptPid, PREVENT_VOLUME_ADJUSTMENT_IF_SILENT);
            } else if (this.mBluetoothHeadsetDevice != null) {
                if (this.mBluetoothHeadset != null) {
                    if (!this.mBluetoothHeadset.stopVoiceRecognition(this.mBluetoothHeadsetDevice)) {
                        sendMsg(this.mAudioHandler, MSG_BT_HEADSET_CNCT_FAILED, SENDMSG_REPLACE, SENDMSG_REPLACE, SENDMSG_REPLACE, null, SENDMSG_REPLACE);
                    }
                } else if (this.mScoAudioState == SENDMSG_QUEUE && getBluetoothHeadset()) {
                    this.mScoAudioState = SCO_STATE_DEACTIVATE_EXT_REQ;
                }
            }
        }
    }

    private void resetBluetoothSco() {
        synchronized (this.mScoClients) {
            clearAllScoClients(SENDMSG_REPLACE, DEBUG_VOL);
            this.mScoAudioState = SENDMSG_REPLACE;
            broadcastScoConnectionState(SENDMSG_REPLACE);
        }
    }

    private void broadcastScoConnectionState(int state) {
        sendMsg(this.mAudioHandler, MSG_BROADCAST_BT_CONNECTION_STATE, SENDMSG_QUEUE, state, SENDMSG_REPLACE, null, SENDMSG_REPLACE);
    }

    private void onBroadcastScoConnectionState(int state) {
        if (state != this.mScoConnectionState) {
            Intent newIntent = new Intent(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
            newIntent.putExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, state);
            newIntent.putExtra(AudioManager.EXTRA_SCO_AUDIO_PREVIOUS_STATE, this.mScoConnectionState);
            sendStickyBroadcastToAll(newIntent);
            this.mScoConnectionState = state;
        }
    }

    private void onCheckMusicActive() {
        synchronized (this.mSafeMediaVolumeState) {
            if (this.mSafeMediaVolumeState.intValue() == SENDMSG_QUEUE) {
                int device = getDeviceForStream(SCO_STATE_ACTIVE_INTERNAL);
                if ((device & MSG_REPORT_NEW_ROUTES) != 0) {
                    sendMsg(this.mAudioHandler, MSG_CHECK_MUSIC_ACTIVE, SENDMSG_REPLACE, SENDMSG_REPLACE, SENDMSG_REPLACE, null, MUSIC_ACTIVE_POLL_PERIOD_MS);
                    int index = this.mStreamStates[SCO_STATE_ACTIVE_INTERNAL].getIndex(device);
                    if (AudioSystem.isStreamActive(SCO_STATE_ACTIVE_INTERNAL, SENDMSG_REPLACE) && index > this.mSafeMediaVolumeIndex) {
                        this.mMusicActiveMs += MUSIC_ACTIVE_POLL_PERIOD_MS;
                        if (this.mMusicActiveMs > UNSAFE_VOLUME_MUSIC_ACTIVE_MS_MAX) {
                            setSafeMediaVolumeEnabled(PREVENT_VOLUME_ADJUSTMENT_IF_SILENT);
                            this.mMusicActiveMs = SENDMSG_REPLACE;
                        }
                        saveMusicActiveMs();
                    }
                }
            }
        }
    }

    private void saveMusicActiveMs() {
        this.mAudioHandler.obtainMessage(MSG_PERSIST_MUSIC_ACTIVE_MS, this.mMusicActiveMs, SENDMSG_REPLACE).sendToTarget();
    }

    private void onConfigureSafeVolume(boolean force) {
        boolean safeMediaVolumeEnabled = DEBUG_VOL;
        synchronized (this.mSafeMediaVolumeState) {
            int mcc = this.mContext.getResources().getConfiguration().mcc;
            if (this.mMcc != mcc || (this.mMcc == 0 && force)) {
                int persistedState;
                this.mSafeMediaVolumeIndex = this.mContext.getResources().getInteger(R.integer.config_safe_media_volume_index) * MSG_SET_ALL_VOLUMES;
                if (SystemProperties.getBoolean("audio.safemedia.force", DEBUG_VOL) || this.mContext.getResources().getBoolean(R.bool.config_safe_media_volume_enabled)) {
                    safeMediaVolumeEnabled = PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
                }
                if (safeMediaVolumeEnabled) {
                    persistedState = SCO_STATE_ACTIVE_INTERNAL;
                    if (this.mSafeMediaVolumeState.intValue() != SENDMSG_QUEUE) {
                        if (this.mMusicActiveMs == 0) {
                            this.mSafeMediaVolumeState = Integer.valueOf(SCO_STATE_ACTIVE_INTERNAL);
                            enforceSafeMediaVolume();
                        } else {
                            this.mSafeMediaVolumeState = Integer.valueOf(SENDMSG_QUEUE);
                        }
                    }
                } else {
                    persistedState = SENDMSG_NOOP;
                    this.mSafeMediaVolumeState = Integer.valueOf(SENDMSG_NOOP);
                }
                this.mMcc = mcc;
                sendMsg(this.mAudioHandler, MSG_PERSIST_SAFE_VOLUME_STATE, SENDMSG_QUEUE, persistedState, SENDMSG_REPLACE, null, SENDMSG_REPLACE);
            }
        }
    }

    private int checkForRingerModeChange(int oldIndex, int direction, int step) {
        int result = SENDMSG_NOOP;
        int ringerMode = getRingerModeInternal();
        switch (ringerMode) {
            case SENDMSG_REPLACE /*0*/:
                if (direction == SENDMSG_NOOP) {
                    result = SENDMSG_NOOP | RILConstants.RIL_REQUEST_SET_DATA_PROFILE;
                }
                result &= -2;
                break;
            case SENDMSG_NOOP /*1*/:
                if (!this.mHasVibrator) {
                    Log.e(TAG, "checkForRingerModeChange() current ringer mode is vibratebut no vibrator is present");
                    break;
                }
                if (direction == SCO_MODE_UNDEFINED) {
                    if (this.mPrevVolDirection != SCO_MODE_UNDEFINED) {
                        result = SENDMSG_NOOP | GL10.GL_EXP;
                    }
                } else if (direction == SENDMSG_NOOP) {
                    ringerMode = SENDMSG_QUEUE;
                }
                result &= -2;
                break;
            case SENDMSG_QUEUE /*2*/:
                if (direction == SCO_MODE_UNDEFINED) {
                    if (!this.mHasVibrator) {
                        if (oldIndex < step) {
                            break;
                        }
                    } else if (step <= oldIndex && oldIndex < step * SENDMSG_QUEUE) {
                        ringerMode = SENDMSG_NOOP;
                        break;
                    }
                }
                break;
            default:
                Log.e(TAG, "checkForRingerModeChange() wrong ringer mode: " + ringerMode);
                break;
        }
        setRingerMode(ringerMode, "AudioService.checkForRingerModeChange", DEBUG_VOL);
        this.mPrevVolDirection = direction;
        return result;
    }

    public boolean isStreamAffectedByRingerMode(int streamType) {
        return (this.mRingerModeAffectedStreams & (SENDMSG_NOOP << streamType)) != 0 ? PREVENT_VOLUME_ADJUSTMENT_IF_SILENT : DEBUG_VOL;
    }

    private boolean isStreamMutedByRingerMode(int streamType) {
        return (this.mRingerModeMutedStreams & (SENDMSG_NOOP << streamType)) != 0 ? PREVENT_VOLUME_ADJUSTMENT_IF_SILENT : DEBUG_VOL;
    }

    boolean updateRingerModeAffectedStreams() {
        int ringerModeAffectedStreams = System.getIntForUser(this.mContentResolver, "mode_ringer_streams_affected", R.styleable.Theme_buttonBarStyle, -2) | 38;
        switch (this.mPlatformType) {
            case SENDMSG_QUEUE /*2*/:
                ringerModeAffectedStreams = SENDMSG_REPLACE;
                break;
            default:
                ringerModeAffectedStreams &= -9;
                break;
        }
        synchronized (this.mCameraSoundForced) {
            if (this.mCameraSoundForced.booleanValue()) {
                ringerModeAffectedStreams &= -129;
            } else {
                ringerModeAffectedStreams |= RILConstants.RIL_REQUEST_SET_DATA_PROFILE;
            }
        }
        if (this.mStreamVolumeAlias[MSG_SET_FORCE_USE] == SENDMSG_QUEUE) {
            ringerModeAffectedStreams |= GL10.GL_DEPTH_BUFFER_BIT;
        } else {
            ringerModeAffectedStreams &= -257;
        }
        if (ringerModeAffectedStreams == this.mRingerModeAffectedStreams) {
            return DEBUG_VOL;
        }
        System.putIntForUser(this.mContentResolver, "mode_ringer_streams_affected", ringerModeAffectedStreams, -2);
        this.mRingerModeAffectedStreams = ringerModeAffectedStreams;
        return PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
    }

    public boolean isStreamAffectedByMute(int streamType) {
        return (this.mMuteAffectedStreams & (SENDMSG_NOOP << streamType)) != 0 ? PREVENT_VOLUME_ADJUSTMENT_IF_SILENT : DEBUG_VOL;
    }

    private void ensureValidDirection(int direction) {
        if (direction < SCO_MODE_UNDEFINED || direction > SENDMSG_NOOP) {
            throw new IllegalArgumentException("Bad direction " + direction);
        }
    }

    private void ensureValidSteps(int steps) {
        if (Math.abs(steps) > SCO_STATE_DEACTIVATE_EXT_REQ) {
            throw new IllegalArgumentException("Bad volume adjust steps " + steps);
        }
    }

    private void ensureValidStreamType(int streamType) {
        if (streamType < 0 || streamType >= this.mStreamStates.length) {
            throw new IllegalArgumentException("Bad stream type " + streamType);
        }
    }

    private boolean isInCommunication() {
        TelecomManager telecomManager = (TelecomManager) this.mContext.getSystemService("telecom");
        long ident = Binder.clearCallingIdentity();
        boolean IsInCall = telecomManager.isInCall();
        Binder.restoreCallingIdentity(ident);
        return (IsInCall || getMode() == SCO_STATE_ACTIVE_INTERNAL) ? PREVENT_VOLUME_ADJUSTMENT_IF_SILENT : DEBUG_VOL;
    }

    private boolean isAfMusicActiveRecently(int delay_ms) {
        return (AudioSystem.isStreamActive(SCO_STATE_ACTIVE_INTERNAL, delay_ms) || AudioSystem.isStreamActiveRemotely(SCO_STATE_ACTIVE_INTERNAL, delay_ms)) ? PREVENT_VOLUME_ADJUSTMENT_IF_SILENT : DEBUG_VOL;
    }

    private int getActiveStreamType(int suggestedStreamType) {
        switch (this.mPlatformType) {
            case SENDMSG_NOOP /*1*/:
                if (isInCommunication()) {
                    return AudioSystem.getForceUse(SENDMSG_REPLACE) == SCO_STATE_ACTIVE_INTERNAL ? MSG_BTA2DP_DOCK_TIMEOUT : SENDMSG_REPLACE;
                } else {
                    if (suggestedStreamType == ExploreByTouchHelper.INVALID_ID) {
                        if (isAfMusicActiveRecently(StreamOverride.sDelayMs)) {
                            if (DEBUG_VOL) {
                                Log.v(TAG, "getActiveStreamType: Forcing STREAM_MUSIC stream active");
                            }
                            return SCO_STATE_ACTIVE_INTERNAL;
                        }
                        if (DEBUG_VOL) {
                            Log.v(TAG, "getActiveStreamType: Forcing STREAM_RING b/c default");
                        }
                        return SENDMSG_QUEUE;
                    } else if (isAfMusicActiveRecently(SENDMSG_REPLACE)) {
                        if (DEBUG_VOL) {
                            Log.v(TAG, "getActiveStreamType: Forcing STREAM_MUSIC stream active");
                        }
                        return SCO_STATE_ACTIVE_INTERNAL;
                    }
                }
                break;
            case SENDMSG_QUEUE /*2*/:
                if (suggestedStreamType == ExploreByTouchHelper.INVALID_ID) {
                    return SCO_STATE_ACTIVE_INTERNAL;
                }
                break;
            default:
                if (isInCommunication()) {
                    if (AudioSystem.getForceUse(SENDMSG_REPLACE) == SCO_STATE_ACTIVE_INTERNAL) {
                        if (DEBUG_VOL) {
                            Log.v(TAG, "getActiveStreamType: Forcing STREAM_BLUETOOTH_SCO");
                        }
                        return MSG_BTA2DP_DOCK_TIMEOUT;
                    }
                    if (DEBUG_VOL) {
                        Log.v(TAG, "getActiveStreamType: Forcing STREAM_VOICE_CALL");
                    }
                    return SENDMSG_REPLACE;
                } else if (AudioSystem.isStreamActive(SCO_STATE_DEACTIVATE_REQ, StreamOverride.sDelayMs) || AudioSystem.isStreamActive(SENDMSG_QUEUE, StreamOverride.sDelayMs)) {
                    if (DEBUG_VOL) {
                        Log.v(TAG, "getActiveStreamType: Forcing STREAM_NOTIFICATION");
                    }
                    return SCO_STATE_DEACTIVATE_REQ;
                } else if (suggestedStreamType == ExploreByTouchHelper.INVALID_ID) {
                    if (isAfMusicActiveRecently(StreamOverride.sDelayMs)) {
                        if (DEBUG_VOL) {
                            Log.v(TAG, "getActiveStreamType: forcing STREAM_MUSIC");
                        }
                        return SCO_STATE_ACTIVE_INTERNAL;
                    }
                    if (DEBUG_VOL) {
                        Log.v(TAG, "getActiveStreamType: using STREAM_NOTIFICATION as default");
                    }
                    return SCO_STATE_DEACTIVATE_REQ;
                }
                break;
        }
        if (!DEBUG_VOL) {
            return suggestedStreamType;
        }
        Log.v(TAG, "getActiveStreamType: Returning suggested type " + suggestedStreamType);
        return suggestedStreamType;
    }

    private void broadcastRingerMode(String action, int ringerMode) {
        Intent broadcast = new Intent(action);
        broadcast.putExtra(AudioManager.EXTRA_RINGER_MODE, ringerMode);
        broadcast.addFlags(603979776);
        sendStickyBroadcastToAll(broadcast);
    }

    private void broadcastVibrateSetting(int vibrateType) {
        if (ActivityManagerNative.isSystemReady()) {
            Intent broadcast = new Intent(AudioManager.VIBRATE_SETTING_CHANGED_ACTION);
            broadcast.putExtra(AudioManager.EXTRA_VIBRATE_TYPE, vibrateType);
            broadcast.putExtra(AudioManager.EXTRA_VIBRATE_SETTING, getVibrateSetting(vibrateType));
            sendBroadcastToAll(broadcast);
        }
    }

    private void queueMsgUnderWakeLock(Handler handler, int msg, int arg1, int arg2, Object obj, int delay) {
        long ident = Binder.clearCallingIdentity();
        this.mAudioEventWakeLock.acquire();
        Binder.restoreCallingIdentity(ident);
        sendMsg(handler, msg, SENDMSG_QUEUE, arg1, arg2, obj, delay);
    }

    private static void sendMsg(Handler handler, int msg, int existingMsgPolicy, int arg1, int arg2, Object obj, int delay) {
        if (existingMsgPolicy == 0) {
            handler.removeMessages(msg);
        } else if (existingMsgPolicy == SENDMSG_NOOP && handler.hasMessages(msg)) {
            return;
        }
        synchronized (mLastDeviceConnectMsgTime) {
            long time = SystemClock.uptimeMillis() + ((long) delay);
            handler.sendMessageAtTime(handler.obtainMessage(msg, arg1, arg2, obj), time);
            if (msg == MSG_SET_WIRED_DEVICE_CONNECTION_STATE || msg == MSG_SET_A2DP_SRC_CONNECTION_STATE || msg == MSG_SET_A2DP_SINK_CONNECTION_STATE) {
                mLastDeviceConnectMsgTime = Long.valueOf(time);
            }
        }
    }

    boolean checkAudioSettingsPermission(String method) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_AUDIO_SETTINGS") == 0) {
            return PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
        }
        Log.w(TAG, "Audio Settings Permission Denial: " + method + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
        return DEBUG_VOL;
    }

    private int getDeviceForStream(int stream) {
        int device = AudioSystem.getDevicesForStream(stream);
        if (((device + SCO_MODE_UNDEFINED) & device) == 0) {
            return device;
        }
        if ((device & SENDMSG_QUEUE) != 0) {
            return SENDMSG_QUEUE;
        }
        if ((Protocol.BASE_DATA_CONNECTION & device) != 0) {
            return Protocol.BASE_DATA_CONNECTION;
        }
        if ((Protocol.BASE_CONNECTIVITY_MANAGER & device) != 0) {
            return Protocol.BASE_CONNECTIVITY_MANAGER;
        }
        if ((AudioSystem.DEVICE_OUT_AUX_LINE & device) != 0) {
            return AudioSystem.DEVICE_OUT_AUX_LINE;
        }
        return device & AudioSystem.DEVICE_OUT_ALL_A2DP;
    }

    public void setWiredDeviceConnectionState(int device, int state, String name) {
        synchronized (this.mConnectedDevices) {
            queueMsgUnderWakeLock(this.mAudioHandler, MSG_SET_WIRED_DEVICE_CONNECTION_STATE, device, state, name, checkSendBecomingNoisyIntent(device, state));
        }
    }

    public int setBluetoothA2dpDeviceConnectionState(BluetoothDevice device, int state, int profile) {
        int i = SENDMSG_REPLACE;
        if (profile == SENDMSG_QUEUE || profile == MSG_SET_ALL_VOLUMES) {
            int delay;
            synchronized (this.mConnectedDevices) {
                if (profile == SENDMSG_QUEUE) {
                    if (state == SENDMSG_QUEUE) {
                        i = SENDMSG_NOOP;
                    }
                    delay = checkSendBecomingNoisyIntent(RILConstants.RIL_REQUEST_SET_DATA_PROFILE, i);
                } else {
                    delay = SENDMSG_REPLACE;
                }
                queueMsgUnderWakeLock(this.mAudioHandler, profile == SENDMSG_QUEUE ? MSG_SET_A2DP_SINK_CONNECTION_STATE : MSG_SET_A2DP_SRC_CONNECTION_STATE, state, SENDMSG_REPLACE, device, delay);
            }
            return delay;
        }
        throw new IllegalArgumentException("invalid profile " + profile);
    }

    private void makeA2dpDeviceAvailable(String address) {
        sendMsg(this.mAudioHandler, SENDMSG_REPLACE, SENDMSG_QUEUE, RILConstants.RIL_REQUEST_SET_DATA_PROFILE, SENDMSG_REPLACE, this.mStreamStates[SCO_STATE_ACTIVE_INTERNAL], SENDMSG_REPLACE);
        setBluetoothA2dpOnInt(PREVENT_VOLUME_ADJUSTMENT_IF_SILENT);
        AudioSystem.setDeviceConnectionState(RILConstants.RIL_REQUEST_SET_DATA_PROFILE, SENDMSG_NOOP, address);
        AudioSystem.setParameters("A2dpSuspended=false");
        this.mConnectedDevices.put(new Integer(RILConstants.RIL_REQUEST_SET_DATA_PROFILE), address);
    }

    private void onSendBecomingNoisyIntent() {
        sendBroadcastToAll(new Intent(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
    }

    private void makeA2dpDeviceUnavailableNow(String address) {
        synchronized (this.mA2dpAvrcpLock) {
            this.mAvrcpAbsVolSupported = DEBUG_VOL;
        }
        AudioSystem.setDeviceConnectionState(RILConstants.RIL_REQUEST_SET_DATA_PROFILE, SENDMSG_REPLACE, address);
        this.mConnectedDevices.remove(Integer.valueOf(RILConstants.RIL_REQUEST_SET_DATA_PROFILE));
        synchronized (this.mCurAudioRoutes) {
            if (this.mCurAudioRoutes.mBluetoothName != null) {
                this.mCurAudioRoutes.mBluetoothName = null;
                sendMsg(this.mAudioHandler, MSG_REPORT_NEW_ROUTES, SENDMSG_NOOP, SENDMSG_REPLACE, SENDMSG_REPLACE, null, SENDMSG_REPLACE);
            }
        }
    }

    private void makeA2dpDeviceUnavailableLater(String address) {
        AudioSystem.setParameters("A2dpSuspended=true");
        this.mConnectedDevices.remove(Integer.valueOf(RILConstants.RIL_REQUEST_SET_DATA_PROFILE));
        this.mAudioHandler.sendMessageDelayed(this.mAudioHandler.obtainMessage(MSG_BTA2DP_DOCK_TIMEOUT, address), 8000);
    }

    private void makeA2dpSrcAvailable(String address) {
        AudioSystem.setDeviceConnectionState(AudioSystem.DEVICE_IN_BLUETOOTH_A2DP, SENDMSG_NOOP, address);
        this.mConnectedDevices.put(new Integer(AudioSystem.DEVICE_IN_BLUETOOTH_A2DP), address);
    }

    private void makeA2dpSrcUnavailable(String address) {
        AudioSystem.setDeviceConnectionState(AudioSystem.DEVICE_IN_BLUETOOTH_A2DP, SENDMSG_REPLACE, address);
        this.mConnectedDevices.remove(Integer.valueOf(AudioSystem.DEVICE_IN_BLUETOOTH_A2DP));
    }

    private void cancelA2dpDeviceTimeout() {
        this.mAudioHandler.removeMessages(MSG_BTA2DP_DOCK_TIMEOUT);
    }

    private boolean hasScheduledA2dpDockTimeout() {
        return this.mAudioHandler.hasMessages(MSG_BTA2DP_DOCK_TIMEOUT);
    }

    private void onSetA2dpSinkConnectionState(BluetoothDevice btDevice, int state) {
        boolean isConnected = PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
        if (DEBUG_VOL) {
            Log.d(TAG, "onSetA2dpSinkConnectionState btDevice=" + btDevice + "state=" + state);
        }
        if (btDevice != null) {
            String address = btDevice.getAddress();
            if (!BluetoothAdapter.checkBluetoothAddress(address)) {
                address = "";
            }
            synchronized (this.mConnectedDevices) {
                if (!(this.mConnectedDevices.containsKey(Integer.valueOf(RILConstants.RIL_REQUEST_SET_DATA_PROFILE)) && ((String) this.mConnectedDevices.get(Integer.valueOf(RILConstants.RIL_REQUEST_SET_DATA_PROFILE))).equals(address))) {
                    isConnected = DEBUG_VOL;
                }
                if (isConnected && state != SENDMSG_QUEUE) {
                    if (!btDevice.isBluetoothDock()) {
                        makeA2dpDeviceUnavailableNow(address);
                    } else if (state == 0) {
                        makeA2dpDeviceUnavailableLater(address);
                    }
                    synchronized (this.mCurAudioRoutes) {
                        if (this.mCurAudioRoutes.mBluetoothName != null) {
                            this.mCurAudioRoutes.mBluetoothName = null;
                            sendMsg(this.mAudioHandler, MSG_REPORT_NEW_ROUTES, SENDMSG_NOOP, SENDMSG_REPLACE, SENDMSG_REPLACE, null, SENDMSG_REPLACE);
                        }
                    }
                } else if (!isConnected && state == SENDMSG_QUEUE) {
                    if (btDevice.isBluetoothDock()) {
                        cancelA2dpDeviceTimeout();
                        this.mDockAddress = address;
                    } else if (hasScheduledA2dpDockTimeout()) {
                        cancelA2dpDeviceTimeout();
                        makeA2dpDeviceUnavailableNow(this.mDockAddress);
                    }
                    makeA2dpDeviceAvailable(address);
                    synchronized (this.mCurAudioRoutes) {
                        String name = btDevice.getAliasName();
                        if (!TextUtils.equals(this.mCurAudioRoutes.mBluetoothName, name)) {
                            this.mCurAudioRoutes.mBluetoothName = name;
                            sendMsg(this.mAudioHandler, MSG_REPORT_NEW_ROUTES, SENDMSG_NOOP, SENDMSG_REPLACE, SENDMSG_REPLACE, null, SENDMSG_REPLACE);
                        }
                    }
                }
            }
        }
    }

    private void onSetA2dpSourceConnectionState(BluetoothDevice btDevice, int state) {
        if (DEBUG_VOL) {
            Log.d(TAG, "onSetA2dpSourceConnectionState btDevice=" + btDevice + " state=" + state);
        }
        if (btDevice != null) {
            String address = btDevice.getAddress();
            if (!BluetoothAdapter.checkBluetoothAddress(address)) {
                address = "";
            }
            synchronized (this.mConnectedDevices) {
                boolean isConnected = (this.mConnectedDevices.containsKey(Integer.valueOf(AudioSystem.DEVICE_IN_BLUETOOTH_A2DP)) && ((String) this.mConnectedDevices.get(Integer.valueOf(AudioSystem.DEVICE_IN_BLUETOOTH_A2DP))).equals(address)) ? PREVENT_VOLUME_ADJUSTMENT_IF_SILENT : DEBUG_VOL;
                if (isConnected && state != SENDMSG_QUEUE) {
                    makeA2dpSrcUnavailable(address);
                } else if (!isConnected && state == SENDMSG_QUEUE) {
                    makeA2dpSrcAvailable(address);
                }
            }
        }
    }

    public void avrcpSupportsAbsoluteVolume(String address, boolean support) {
        synchronized (this.mA2dpAvrcpLock) {
            this.mAvrcpAbsVolSupported = support;
            sendMsg(this.mAudioHandler, SENDMSG_REPLACE, SENDMSG_QUEUE, RILConstants.RIL_REQUEST_SET_DATA_PROFILE, SENDMSG_REPLACE, this.mStreamStates[SCO_STATE_ACTIVE_INTERNAL], SENDMSG_REPLACE);
            sendMsg(this.mAudioHandler, SENDMSG_REPLACE, SENDMSG_QUEUE, RILConstants.RIL_REQUEST_SET_DATA_PROFILE, SENDMSG_REPLACE, this.mStreamStates[SENDMSG_QUEUE], SENDMSG_REPLACE);
        }
    }

    private boolean handleDeviceConnection(boolean connected, int device, String params) {
        synchronized (this.mConnectedDevices) {
            boolean isConnected;
            if (this.mConnectedDevices.containsKey(Integer.valueOf(device)) && (params.isEmpty() || ((String) this.mConnectedDevices.get(Integer.valueOf(device))).equals(params))) {
                isConnected = PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
            } else {
                isConnected = DEBUG_VOL;
            }
            if (isConnected && !connected) {
                AudioSystem.setDeviceConnectionState(device, SENDMSG_REPLACE, (String) this.mConnectedDevices.get(Integer.valueOf(device)));
                this.mConnectedDevices.remove(Integer.valueOf(device));
                return PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
            } else if (isConnected || !connected) {
                return DEBUG_VOL;
            } else {
                AudioSystem.setDeviceConnectionState(device, SENDMSG_NOOP, params);
                this.mConnectedDevices.put(new Integer(device), params);
                return PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
            }
        }
    }

    private int checkSendBecomingNoisyIntent(int device, int state) {
        int delay = SENDMSG_REPLACE;
        if (state == 0 && (this.mBecomingNoisyIntentDevices & device) != 0) {
            int devices = SENDMSG_REPLACE;
            for (Integer intValue : this.mConnectedDevices.keySet()) {
                int dev = intValue.intValue();
                if ((ExploreByTouchHelper.INVALID_ID & dev) == 0 && (this.mBecomingNoisyIntentDevices & dev) != 0) {
                    devices |= dev;
                }
            }
            if (devices == device) {
                sendMsg(this.mAudioHandler, MSG_BROADCAST_AUDIO_BECOMING_NOISY, SENDMSG_REPLACE, SENDMSG_REPLACE, SENDMSG_REPLACE, null, SENDMSG_REPLACE);
                delay = RILConstants.RIL_UNSOL_RESPONSE_RADIO_STATE_CHANGED;
            }
        }
        if (this.mAudioHandler.hasMessages(MSG_SET_A2DP_SRC_CONNECTION_STATE) || this.mAudioHandler.hasMessages(MSG_SET_A2DP_SINK_CONNECTION_STATE) || this.mAudioHandler.hasMessages(MSG_SET_WIRED_DEVICE_CONNECTION_STATE)) {
            synchronized (mLastDeviceConnectMsgTime) {
                long time = SystemClock.uptimeMillis();
                if (mLastDeviceConnectMsgTime.longValue() > time) {
                    delay = ((int) (mLastDeviceConnectMsgTime.longValue() - time)) + 30;
                }
            }
        }
        return delay;
    }

    private void sendDeviceConnectionIntent(int device, int state, String name) {
        Intent intent = new Intent();
        intent.putExtra(PhoneConstants.STATE_KEY, state);
        intent.putExtra(ATTR_GROUP_NAME, name);
        intent.addFlags(AudioSystem.DEVICE_OUT_DEFAULT);
        int connType = SENDMSG_REPLACE;
        if (device == SCO_STATE_DEACTIVATE_EXT_REQ) {
            connType = SENDMSG_NOOP;
            intent.setAction(AudioManager.ACTION_HEADSET_PLUG);
            intent.putExtra("microphone", SENDMSG_NOOP);
        } else if (device == MSG_SET_FORCE_USE || device == 131072) {
            connType = SENDMSG_QUEUE;
            intent.setAction(AudioManager.ACTION_HEADSET_PLUG);
            intent.putExtra("microphone", SENDMSG_REPLACE);
        } else if (device == 2048) {
            connType = SCO_STATE_DEACTIVATE_EXT_REQ;
            intent.setAction(AudioManager.ACTION_ANALOG_AUDIO_DOCK_PLUG);
        } else if (device == 4096) {
            connType = SCO_STATE_DEACTIVATE_EXT_REQ;
            intent.setAction(AudioManager.ACTION_DIGITAL_AUDIO_DOCK_PLUG);
        } else if (device == 1024 || device == 262144) {
            connType = MSG_SET_FORCE_USE;
            configureHdmiPlugIntent(intent, state);
        }
        synchronized (this.mCurAudioRoutes) {
            if (connType != 0) {
                int newConn = this.mCurAudioRoutes.mMainType;
                if (state != 0) {
                    newConn |= connType;
                } else {
                    newConn &= connType ^ SCO_MODE_UNDEFINED;
                }
                if (newConn != this.mCurAudioRoutes.mMainType) {
                    this.mCurAudioRoutes.mMainType = newConn;
                    sendMsg(this.mAudioHandler, MSG_REPORT_NEW_ROUTES, SENDMSG_NOOP, SENDMSG_REPLACE, SENDMSG_REPLACE, null, SENDMSG_REPLACE);
                }
            }
        }
        long ident = Binder.clearCallingIdentity();
        try {
            ActivityManagerNative.broadcastStickyIntent(intent, null, SCO_MODE_UNDEFINED);
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    private void onSetWiredDeviceConnectionState(int device, int state, String name) {
        boolean z = DEBUG_VOL;
        synchronized (this.mConnectedDevices) {
            boolean isUsb;
            if (state == 0 && (device == SCO_STATE_DEACTIVATE_EXT_REQ || device == MSG_SET_FORCE_USE || device == Protocol.BASE_WIFI)) {
                setBluetoothA2dpOnInt(PREVENT_VOLUME_ADJUSTMENT_IF_SILENT);
            }
            if ((device & -24577) == 0 || ((ExploreByTouchHelper.INVALID_ID & device) != 0 && (2147477503 & device) == 0)) {
                isUsb = PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
            } else {
                isUsb = DEBUG_VOL;
            }
            if (state == SENDMSG_NOOP) {
                z = PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
            }
            handleDeviceConnection(z, device, isUsb ? name : "");
            if (state != 0) {
                if (device == SCO_STATE_DEACTIVATE_EXT_REQ || device == MSG_SET_FORCE_USE || device == Protocol.BASE_WIFI) {
                    setBluetoothA2dpOnInt(DEBUG_VOL);
                }
                if ((device & MSG_REPORT_NEW_ROUTES) != 0) {
                    sendMsg(this.mAudioHandler, MSG_CHECK_MUSIC_ACTIVE, SENDMSG_REPLACE, SENDMSG_REPLACE, SENDMSG_REPLACE, null, MUSIC_ACTIVE_POLL_PERIOD_MS);
                }
                if (isPlatformTelevision() && (device & GL10.GL_STENCIL_BUFFER_BIT) != 0) {
                    this.mFixedVolumeDevices |= GL10.GL_STENCIL_BUFFER_BIT;
                    checkAllFixedVolumeDevices();
                    if (this.mHdmiManager != null) {
                        synchronized (this.mHdmiManager) {
                            if (this.mHdmiPlaybackClient != null) {
                                this.mHdmiCecSink = DEBUG_VOL;
                                this.mHdmiPlaybackClient.queryDisplayStatus(this.mHdmiDisplayStatusCallback);
                            }
                        }
                    }
                }
            } else if (!(!isPlatformTelevision() || (device & GL10.GL_STENCIL_BUFFER_BIT) == 0 || this.mHdmiManager == null)) {
                synchronized (this.mHdmiManager) {
                    this.mHdmiCecSink = DEBUG_VOL;
                }
            }
            if (!(isUsb || device == AudioSystem.DEVICE_IN_WIRED_HEADSET)) {
                sendDeviceConnectionIntent(device, state, name);
            }
        }
    }

    private void configureHdmiPlugIntent(Intent intent, int state) {
        intent.setAction(AudioManager.ACTION_HDMI_AUDIO_PLUG);
        intent.putExtra(AudioManager.EXTRA_AUDIO_PLUG_STATE, state);
        if (state == SENDMSG_NOOP) {
            ArrayList<AudioPort> ports = new ArrayList();
            if (AudioSystem.listAudioPorts(ports, new int[SENDMSG_NOOP]) == 0) {
                Iterator it = ports.iterator();
                while (it.hasNext()) {
                    AudioPort port = (AudioPort) it.next();
                    if (port instanceof AudioDevicePort) {
                        AudioDevicePort devicePort = (AudioDevicePort) port;
                        if (devicePort.type() == 1024 || devicePort.type() == 262144) {
                            int[] arr$;
                            int len$;
                            int i$;
                            int[] formats = devicePort.formats();
                            if (formats.length > 0) {
                                ArrayList<Integer> encodingList = new ArrayList(SENDMSG_NOOP);
                                arr$ = formats;
                                len$ = arr$.length;
                                for (i$ = SENDMSG_REPLACE; i$ < len$; i$ += SENDMSG_NOOP) {
                                    int format = arr$[i$];
                                    if (format != 0) {
                                        encodingList.add(Integer.valueOf(format));
                                    }
                                }
                                int[] encodingArray = new int[encodingList.size()];
                                int i = SENDMSG_REPLACE;
                                while (true) {
                                    int length = encodingArray.length;
                                    if (i >= r0) {
                                        break;
                                    }
                                    encodingArray[i] = ((Integer) encodingList.get(i)).intValue();
                                    i += SENDMSG_NOOP;
                                }
                                intent.putExtra(AudioManager.EXTRA_ENCODINGS, encodingArray);
                            }
                            int maxChannels = SENDMSG_REPLACE;
                            arr$ = devicePort.channelMasks();
                            len$ = arr$.length;
                            for (i$ = SENDMSG_REPLACE; i$ < len$; i$ += SENDMSG_NOOP) {
                                int channelCount = AudioFormat.channelCountFromOutChannelMask(arr$[i$]);
                                if (channelCount > maxChannels) {
                                    maxChannels = channelCount;
                                }
                            }
                            intent.putExtra(AudioManager.EXTRA_MAX_CHANNEL_COUNT, maxChannels);
                        }
                    }
                }
            }
        }
    }

    public boolean registerRemoteController(IRemoteControlDisplay rcd, int w, int h, ComponentName listenerComp) {
        return this.mMediaFocusControl.registerRemoteController(rcd, w, h, listenerComp);
    }

    public boolean registerRemoteControlDisplay(IRemoteControlDisplay rcd, int w, int h) {
        return this.mMediaFocusControl.registerRemoteControlDisplay(rcd, w, h);
    }

    public void unregisterRemoteControlDisplay(IRemoteControlDisplay rcd) {
        this.mMediaFocusControl.unregisterRemoteControlDisplay(rcd);
    }

    public void remoteControlDisplayUsesBitmapSize(IRemoteControlDisplay rcd, int w, int h) {
        this.mMediaFocusControl.remoteControlDisplayUsesBitmapSize(rcd, w, h);
    }

    public void remoteControlDisplayWantsPlaybackPositionSync(IRemoteControlDisplay rcd, boolean wantsSync) {
        this.mMediaFocusControl.remoteControlDisplayWantsPlaybackPositionSync(rcd, wantsSync);
    }

    public void setRemoteStreamVolume(int index) {
        enforceSelfOrSystemUI("set the remote stream volume");
        this.mMediaFocusControl.setRemoteStreamVolume(index);
    }

    public int requestAudioFocus(AudioAttributes aa, int durationHint, IBinder cb, IAudioFocusDispatcher fd, String clientId, String callingPackageName, int flags, IAudioPolicyCallback pcb) {
        if ((flags & SCO_STATE_DEACTIVATE_EXT_REQ) == SCO_STATE_DEACTIVATE_EXT_REQ) {
            MediaFocusControl mediaFocusControl = this.mMediaFocusControl;
            if (!"AudioFocus_For_Phone_Ring_And_Calls".equals(clientId)) {
                synchronized (this.mAudioPolicies) {
                    if (this.mAudioPolicies.containsKey(pcb.asBinder())) {
                    } else {
                        Log.e(TAG, "Invalid unregistered AudioPolicy to (un)lock audio focus");
                        return SENDMSG_REPLACE;
                    }
                }
            } else if (this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE") != 0) {
                Log.e(TAG, "Invalid permission to (un)lock audio focus", new Exception());
                return SENDMSG_REPLACE;
            }
        }
        return this.mMediaFocusControl.requestAudioFocus(aa, durationHint, cb, fd, clientId, callingPackageName, flags);
    }

    public int abandonAudioFocus(IAudioFocusDispatcher fd, String clientId, AudioAttributes aa) {
        return this.mMediaFocusControl.abandonAudioFocus(fd, clientId, aa);
    }

    public void unregisterAudioFocusClient(String clientId) {
        this.mMediaFocusControl.unregisterAudioFocusClient(clientId);
    }

    public int getCurrentAudioFocus() {
        return this.mMediaFocusControl.getCurrentAudioFocus();
    }

    private void handleConfigurationChanged(Context context) {
        try {
            Configuration config = context.getResources().getConfiguration();
            if (this.mMonitorOrientation) {
                int newOrientation = config.orientation;
                if (newOrientation != this.mDeviceOrientation) {
                    this.mDeviceOrientation = newOrientation;
                    setOrientationForAudioSystem();
                }
            }
            sendMsg(this.mAudioHandler, MSG_CONFIGURE_SAFE_MEDIA_VOLUME, SENDMSG_REPLACE, SENDMSG_REPLACE, SENDMSG_REPLACE, null, SENDMSG_REPLACE);
            boolean cameraSoundForced = this.mContext.getResources().getBoolean(R.bool.config_camera_sound_forced);
            synchronized (this.mSettingsLock) {
                boolean cameraSoundForcedChanged = DEBUG_VOL;
                synchronized (this.mCameraSoundForced) {
                    if (cameraSoundForced != this.mCameraSoundForced.booleanValue()) {
                        this.mCameraSoundForced = Boolean.valueOf(cameraSoundForced);
                        cameraSoundForcedChanged = PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
                    }
                }
                if (cameraSoundForcedChanged) {
                    if (!isPlatformTelevision()) {
                        VolumeStreamState s = this.mStreamStates[MSG_LOAD_SOUND_EFFECTS];
                        if (cameraSoundForced) {
                            s.setAllIndexesToMax();
                            this.mRingerModeAffectedStreams &= -129;
                        } else {
                            s.setAllIndexes(this.mStreamStates[SENDMSG_NOOP]);
                            this.mRingerModeAffectedStreams |= RILConstants.RIL_REQUEST_SET_DATA_PROFILE;
                        }
                        setRingerModeInt(getRingerModeInternal(), DEBUG_VOL);
                    }
                    sendMsg(this.mAudioHandler, MSG_SET_FORCE_USE, SENDMSG_QUEUE, SCO_STATE_DEACTIVATE_EXT_REQ, cameraSoundForced ? MSG_PERSIST_MASTER_VOLUME_MUTE : SENDMSG_REPLACE, null, SENDMSG_REPLACE);
                    sendMsg(this.mAudioHandler, MSG_SET_ALL_VOLUMES, SENDMSG_QUEUE, SENDMSG_REPLACE, SENDMSG_REPLACE, this.mStreamStates[MSG_LOAD_SOUND_EFFECTS], SENDMSG_REPLACE);
                }
            }
            this.mVolumeController.setLayoutDirection(config.getLayoutDirection());
        } catch (Exception e) {
            Log.e(TAG, "Error handling configuration change: ", e);
        }
    }

    private void setOrientationForAudioSystem() {
        switch (this.mDeviceOrientation) {
            case SENDMSG_REPLACE /*0*/:
                AudioSystem.setParameters("orientation=undefined");
            case SENDMSG_NOOP /*1*/:
                AudioSystem.setParameters("orientation=portrait");
            case SENDMSG_QUEUE /*2*/:
                AudioSystem.setParameters("orientation=landscape");
            case SCO_STATE_ACTIVE_INTERNAL /*3*/:
                AudioSystem.setParameters("orientation=square");
            default:
                Log.e(TAG, "Unknown orientation");
        }
    }

    private void setRotationForAudioSystem() {
        switch (this.mDeviceRotation) {
            case SENDMSG_REPLACE /*0*/:
                AudioSystem.setParameters("rotation=0");
            case SENDMSG_NOOP /*1*/:
                AudioSystem.setParameters("rotation=90");
            case SENDMSG_QUEUE /*2*/:
                AudioSystem.setParameters("rotation=180");
            case SCO_STATE_ACTIVE_INTERNAL /*3*/:
                AudioSystem.setParameters("rotation=270");
            default:
                Log.e(TAG, "Unknown device rotation");
        }
    }

    public void setBluetoothA2dpOnInt(boolean on) {
        synchronized (this.mBluetoothA2dpEnabledLock) {
            this.mBluetoothA2dpEnabled = on;
            this.mAudioHandler.removeMessages(MSG_SET_FORCE_BT_A2DP_USE);
            AudioSystem.setForceUse(SENDMSG_NOOP, this.mBluetoothA2dpEnabled ? SENDMSG_REPLACE : MSG_SET_ALL_VOLUMES);
        }
    }

    public void setRingtonePlayer(IRingtonePlayer player) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.REMOTE_AUDIO_PLAYBACK", null);
        this.mRingtonePlayer = player;
    }

    public IRingtonePlayer getRingtonePlayer() {
        return this.mRingtonePlayer;
    }

    public AudioRoutesInfo startWatchingRoutes(IAudioRoutesObserver observer) {
        AudioRoutesInfo routes;
        synchronized (this.mCurAudioRoutes) {
            routes = new AudioRoutesInfo(this.mCurAudioRoutes);
            this.mRoutesObservers.register(observer);
        }
        return routes;
    }

    private void setSafeMediaVolumeEnabled(boolean on) {
        synchronized (this.mSafeMediaVolumeState) {
            if (!(this.mSafeMediaVolumeState.intValue() == 0 || this.mSafeMediaVolumeState.intValue() == SENDMSG_NOOP)) {
                if (on && this.mSafeMediaVolumeState.intValue() == SENDMSG_QUEUE) {
                    this.mSafeMediaVolumeState = Integer.valueOf(SCO_STATE_ACTIVE_INTERNAL);
                    enforceSafeMediaVolume();
                } else if (!on && this.mSafeMediaVolumeState.intValue() == SCO_STATE_ACTIVE_INTERNAL) {
                    this.mSafeMediaVolumeState = Integer.valueOf(SENDMSG_QUEUE);
                    this.mMusicActiveMs = SENDMSG_NOOP;
                    saveMusicActiveMs();
                    sendMsg(this.mAudioHandler, MSG_CHECK_MUSIC_ACTIVE, SENDMSG_REPLACE, SENDMSG_REPLACE, SENDMSG_REPLACE, null, MUSIC_ACTIVE_POLL_PERIOD_MS);
                }
            }
        }
    }

    private void enforceSafeMediaVolume() {
        VolumeStreamState streamState = this.mStreamStates[SCO_STATE_ACTIVE_INTERNAL];
        int devices = MSG_REPORT_NEW_ROUTES;
        int i = SENDMSG_REPLACE;
        while (devices != 0) {
            int i2 = i + SENDMSG_NOOP;
            int device = SENDMSG_NOOP << i;
            if ((device & devices) == 0) {
                i = i2;
            } else {
                if (streamState.getIndex(device) > this.mSafeMediaVolumeIndex) {
                    streamState.setIndex(this.mSafeMediaVolumeIndex, device);
                    sendMsg(this.mAudioHandler, SENDMSG_REPLACE, SENDMSG_QUEUE, device, SENDMSG_REPLACE, streamState, SENDMSG_REPLACE);
                }
                devices &= device ^ SCO_MODE_UNDEFINED;
                i = i2;
            }
        }
    }

    private boolean checkSafeMediaVolume(int streamType, int index, int device) {
        boolean z;
        synchronized (this.mSafeMediaVolumeState) {
            if (this.mSafeMediaVolumeState.intValue() != SCO_STATE_ACTIVE_INTERNAL || this.mStreamVolumeAlias[streamType] != SCO_STATE_ACTIVE_INTERNAL || (device & MSG_REPORT_NEW_ROUTES) == 0 || index <= this.mSafeMediaVolumeIndex) {
                z = PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
            } else {
                z = DEBUG_VOL;
            }
        }
        return z;
    }

    public void disableSafeMediaVolume() {
        enforceSelfOrSystemUI("disable the safe media volume");
        synchronized (this.mSafeMediaVolumeState) {
            setSafeMediaVolumeEnabled(DEBUG_VOL);
            if (this.mPendingVolumeCommand != null) {
                onSetStreamVolume(this.mPendingVolumeCommand.mStreamType, this.mPendingVolumeCommand.mIndex, this.mPendingVolumeCommand.mFlags, this.mPendingVolumeCommand.mDevice);
                this.mPendingVolumeCommand = null;
            }
        }
    }

    public int setHdmiSystemAudioSupported(boolean on) {
        int device = SENDMSG_REPLACE;
        if (this.mHdmiManager != null) {
            synchronized (this.mHdmiManager) {
                if (this.mHdmiTvClient == null) {
                    Log.w(TAG, "Only Hdmi-Cec enabled TV device supports system audio mode.");
                    return SENDMSG_REPLACE;
                }
                synchronized (this.mHdmiTvClient) {
                    if (this.mHdmiSystemAudioSupported != on) {
                        this.mHdmiSystemAudioSupported = on;
                        AudioSystem.setForceUse(SCO_STATE_DEACTIVATE_REQ, on ? MSG_REPORT_NEW_ROUTES : SENDMSG_REPLACE);
                    }
                    device = AudioSystem.getDevicesForStream(SCO_STATE_ACTIVE_INTERNAL);
                }
            }
        }
        return device;
    }

    public boolean isHdmiSystemAudioSupported() {
        return this.mHdmiSystemAudioSupported;
    }

    public boolean isCameraSoundForced() {
        boolean booleanValue;
        synchronized (this.mCameraSoundForced) {
            booleanValue = this.mCameraSoundForced.booleanValue();
        }
        return booleanValue;
    }

    private void dumpRingerMode(PrintWriter pw) {
        pw.println("\nRinger mode: ");
        pw.println("- mode (internal) = " + RINGER_MODE_NAMES[this.mRingerMode]);
        pw.println("- mode (external) = " + RINGER_MODE_NAMES[this.mRingerModeExternal]);
        pw.print("- ringer mode affected streams = 0x");
        pw.println(Integer.toHexString(this.mRingerModeAffectedStreams));
        pw.print("- ringer mode muted streams = 0x");
        pw.println(Integer.toHexString(this.mRingerModeMutedStreams));
        pw.print("- delegate = ");
        pw.println(this.mRingerModeDelegate);
    }

    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.DUMP", TAG);
        this.mMediaFocusControl.dump(pw);
        dumpStreamStates(pw);
        dumpRingerMode(pw);
        pw.println("\nAudio routes:");
        pw.print("  mMainType=0x");
        pw.println(Integer.toHexString(this.mCurAudioRoutes.mMainType));
        pw.print("  mBluetoothName=");
        pw.println(this.mCurAudioRoutes.mBluetoothName);
        pw.println("\nOther state:");
        pw.print("  mVolumeController=");
        pw.println(this.mVolumeController);
        pw.print("  mSafeMediaVolumeState=");
        pw.println(safeMediaVolumeStateToString(this.mSafeMediaVolumeState));
        pw.print("  mSafeMediaVolumeIndex=");
        pw.println(this.mSafeMediaVolumeIndex);
        pw.print("  mPendingVolumeCommand=");
        pw.println(this.mPendingVolumeCommand);
        pw.print("  mMusicActiveMs=");
        pw.println(this.mMusicActiveMs);
        pw.print("  mMcc=");
        pw.println(this.mMcc);
        pw.print("  mHasVibrator=");
        pw.println(this.mHasVibrator);
        dumpAudioPolicies(pw);
    }

    private static String safeMediaVolumeStateToString(Integer state) {
        switch (state.intValue()) {
            case SENDMSG_REPLACE /*0*/:
                return "SAFE_MEDIA_VOLUME_NOT_CONFIGURED";
            case SENDMSG_NOOP /*1*/:
                return "SAFE_MEDIA_VOLUME_DISABLED";
            case SENDMSG_QUEUE /*2*/:
                return "SAFE_MEDIA_VOLUME_INACTIVE";
            case SCO_STATE_ACTIVE_INTERNAL /*3*/:
                return "SAFE_MEDIA_VOLUME_ACTIVE";
            default:
                return null;
        }
    }

    private static void readAndSetLowRamDevice() {
        int status = AudioSystem.setLowRamDevice(ActivityManager.isLowRamDeviceStatic());
        if (status != 0) {
            Log.w(TAG, "AudioFlinger informed of device's low RAM attribute; status " + status);
        }
    }

    private void enforceSelfOrSystemUI(String action) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.STATUS_BAR_SERVICE", "Only SystemUI can " + action);
    }

    public void setVolumeController(IVolumeController controller) {
        enforceSelfOrSystemUI("set the volume controller");
        if (!this.mVolumeController.isSameBinder(controller)) {
            this.mVolumeController.postDismiss();
            if (controller != null) {
                try {
                    controller.asBinder().linkToDeath(new 3(this, controller), SENDMSG_REPLACE);
                } catch (RemoteException e) {
                }
            }
            this.mVolumeController.setController(controller);
            if (DEBUG_VOL) {
                Log.d(TAG, "Volume controller: " + this.mVolumeController);
            }
        }
    }

    public void notifyVolumeControllerVisible(IVolumeController controller, boolean visible) {
        enforceSelfOrSystemUI("notify about volume controller visibility");
        if (this.mVolumeController.isSameBinder(controller)) {
            this.mVolumeController.setVisible(visible);
            if (DEBUG_VOL) {
                Log.d(TAG, "Volume controller visible: " + visible);
            }
        }
    }

    public String registerAudioPolicy(AudioPolicyConfig policyConfig, IAudioPolicyCallback pcb, boolean hasFocusListener) {
        boolean hasPermissionForPolicy = DEBUG_VOL;
        if (DEBUG_AP) {
            Log.d(TAG, "registerAudioPolicy for " + pcb.asBinder() + " with config:" + policyConfig);
        }
        if (this.mContext.checkCallingPermission("android.permission.MODIFY_AUDIO_ROUTING") == 0) {
            hasPermissionForPolicy = PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
        }
        if (hasPermissionForPolicy) {
            synchronized (this.mAudioPolicies) {
                try {
                    if (this.mAudioPolicies.containsKey(pcb.asBinder())) {
                        Slog.e(TAG, "Cannot re-register policy");
                        return null;
                    }
                    AudioPolicyProxy app = new AudioPolicyProxy(this, policyConfig, pcb, hasFocusListener);
                    pcb.asBinder().linkToDeath(app, SENDMSG_REPLACE);
                    String regId = app.getRegistrationId();
                    this.mAudioPolicies.put(pcb.asBinder(), app);
                    return regId;
                } catch (RemoteException e) {
                    Slog.w(TAG, "Audio policy registration failed, could not link to " + pcb + " binder death", e);
                    return null;
                }
            }
        }
        Slog.w(TAG, "Can't register audio policy for pid " + Binder.getCallingPid() + " / uid " + Binder.getCallingUid() + ", need MODIFY_AUDIO_ROUTING");
        return null;
    }

    public void unregisterAudioPolicyAsync(IAudioPolicyCallback pcb) {
        if (DEBUG_AP) {
            Log.d(TAG, "unregisterAudioPolicyAsync for " + pcb.asBinder());
        }
        synchronized (this.mAudioPolicies) {
            AudioPolicyProxy app = (AudioPolicyProxy) this.mAudioPolicies.remove(pcb.asBinder());
            if (app == null) {
                Slog.w(TAG, "Trying to unregister unknown audio policy for pid " + Binder.getCallingPid() + " / uid " + Binder.getCallingUid());
                return;
            }
            pcb.asBinder().unlinkToDeath(app, SENDMSG_REPLACE);
            app.release();
        }
    }

    public int setFocusPropertiesForPolicy(int duckingBehavior, IAudioPolicyCallback pcb) {
        boolean hasPermissionForPolicy;
        if (DEBUG_AP) {
            Log.d(TAG, "setFocusPropertiesForPolicy() duck behavior=" + duckingBehavior + " policy " + pcb.asBinder());
        }
        if (this.mContext.checkCallingPermission("android.permission.MODIFY_AUDIO_ROUTING") == 0) {
            hasPermissionForPolicy = PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
        } else {
            hasPermissionForPolicy = DEBUG_VOL;
        }
        if (hasPermissionForPolicy) {
            synchronized (this.mAudioPolicies) {
                if (this.mAudioPolicies.containsKey(pcb.asBinder())) {
                    boolean z;
                    AudioPolicyProxy app = (AudioPolicyProxy) this.mAudioPolicies.get(pcb.asBinder());
                    if (duckingBehavior == SENDMSG_NOOP) {
                        for (AudioPolicyProxy policy : this.mAudioPolicies.values()) {
                            if (policy.mFocusDuckBehavior == SENDMSG_NOOP) {
                                Slog.e(TAG, "Cannot change audio policy ducking behavior, already handled");
                                return SCO_MODE_UNDEFINED;
                            }
                        }
                    }
                    app.mFocusDuckBehavior = duckingBehavior;
                    MediaFocusControl mediaFocusControl = this.mMediaFocusControl;
                    if (duckingBehavior == SENDMSG_NOOP) {
                        z = PREVENT_VOLUME_ADJUSTMENT_IF_SILENT;
                    } else {
                        z = DEBUG_VOL;
                    }
                    mediaFocusControl.setDuckingInExtPolicyAvailable(z);
                    return SENDMSG_REPLACE;
                }
                Slog.e(TAG, "Cannot change audio policy focus properties, unregistered policy");
                return SCO_MODE_UNDEFINED;
            }
        }
        Slog.w(TAG, "Cannot change audio policy ducking handling for pid " + Binder.getCallingPid() + " / uid " + Binder.getCallingUid() + ", need MODIFY_AUDIO_ROUTING");
        return SCO_MODE_UNDEFINED;
    }

    private void dumpAudioPolicies(PrintWriter pw) {
        pw.println("\nAudio policies:");
        synchronized (this.mAudioPolicies) {
            for (AudioPolicyProxy policy : this.mAudioPolicies.values()) {
                pw.println(policy.toLogFriendlyString());
            }
        }
    }
}
