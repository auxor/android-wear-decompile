package android.os;

import android.app.backup.FullBackup;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.hardware.Camera.Parameters;
import android.hardware.SensorManager;
import android.net.ProxyInfo;
import android.net.wifi.WifiConfiguration.GroupCipher;
import android.net.wifi.WifiEnterpriseConfig;
import android.nfc.cardemulation.CardEmulation;
import android.provider.ContactsContract.Intents.Insert;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.telephony.PhoneNumberUtils;
import android.telephony.SignalStrength;
import android.text.format.DateFormat;
import android.util.Printer;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TimeUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceControl;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.EditorInfo;
import android.widget.AppSecurityPermissions;
import com.android.internal.os.BatterySipper;
import com.android.internal.os.BatterySipper.DrainType;
import com.android.internal.os.BatteryStatsHelper;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class BatteryStats implements Parcelable {
    private static final String APK_DATA = "apk";
    public static final int AUDIO_TURNED_ON = 15;
    private static final String BATTERY_DATA = "bt";
    private static final String BATTERY_DISCHARGE_DATA = "dc";
    private static final String BATTERY_LEVEL_DATA = "lv";
    private static final int BATTERY_STATS_CHECKIN_VERSION = 9;
    private static final String BLUETOOTH_STATE_COUNT_DATA = "bsc";
    public static final int BLUETOOTH_STATE_HIGH = 3;
    public static final int BLUETOOTH_STATE_INACTIVE = 0;
    public static final int BLUETOOTH_STATE_LOW = 1;
    public static final int BLUETOOTH_STATE_MEDIUM = 2;
    static final String[] BLUETOOTH_STATE_NAMES = null;
    private static final String BLUETOOTH_STATE_TIME_DATA = "bst";
    private static final long BYTES_PER_GB = 1073741824;
    private static final long BYTES_PER_KB = 1024;
    private static final long BYTES_PER_MB = 1048576;
    private static final String CHARGE_STEP_DATA = "csd";
    private static final String CHARGE_TIME_REMAIN_DATA = "ctr";
    public static final int DATA_CONNECTION_1xRTT = 7;
    public static final int DATA_CONNECTION_CDMA = 4;
    private static final String DATA_CONNECTION_COUNT_DATA = "dcc";
    public static final int DATA_CONNECTION_EDGE = 2;
    public static final int DATA_CONNECTION_EHRPD = 14;
    public static final int DATA_CONNECTION_EVDO_0 = 5;
    public static final int DATA_CONNECTION_EVDO_A = 6;
    public static final int DATA_CONNECTION_EVDO_B = 12;
    public static final int DATA_CONNECTION_GPRS = 1;
    public static final int DATA_CONNECTION_HSDPA = 8;
    public static final int DATA_CONNECTION_HSPA = 10;
    public static final int DATA_CONNECTION_HSPAP = 15;
    public static final int DATA_CONNECTION_HSUPA = 9;
    public static final int DATA_CONNECTION_IDEN = 11;
    public static final int DATA_CONNECTION_LTE = 13;
    static final String[] DATA_CONNECTION_NAMES = null;
    public static final int DATA_CONNECTION_NONE = 0;
    public static final int DATA_CONNECTION_OTHER = 16;
    private static final String DATA_CONNECTION_TIME_DATA = "dct";
    public static final int DATA_CONNECTION_UMTS = 3;
    private static final String DISCHARGE_STEP_DATA = "dsd";
    private static final String DISCHARGE_TIME_REMAIN_DATA = "dtr";
    public static final int DUMP_CHARGED_ONLY = 2;
    public static final int DUMP_DEVICE_WIFI_ONLY = 32;
    public static final int DUMP_HISTORY_ONLY = 4;
    public static final int DUMP_INCLUDE_HISTORY = 8;
    public static final int DUMP_UNPLUGGED_ONLY = 1;
    public static final int DUMP_VERBOSE = 16;
    public static final int FOREGROUND_ACTIVITY = 10;
    private static final String FOREGROUND_DATA = "fg";
    public static final int FULL_WIFI_LOCK = 5;
    private static final String GLOBAL_NETWORK_DATA = "gn";
    private static final String HISTORY_DATA = "h";
    public static final String[] HISTORY_EVENT_CHECKIN_NAMES = null;
    public static final String[] HISTORY_EVENT_NAMES = null;
    public static final BitDescription[] HISTORY_STATE2_DESCRIPTIONS = null;
    public static final BitDescription[] HISTORY_STATE_DESCRIPTIONS = null;
    private static final String HISTORY_STRING_POOL = "hsp";
    public static final int JOB = 14;
    private static final String JOB_DATA = "jb";
    private static final String KERNEL_WAKELOCK_DATA = "kwl";
    private static final boolean LOCAL_LOGV = false;
    private static final String MISC_DATA = "m";
    private static final String NETWORK_DATA = "nt";
    public static final int NETWORK_MOBILE_RX_DATA = 0;
    public static final int NETWORK_MOBILE_TX_DATA = 1;
    public static final int NETWORK_WIFI_RX_DATA = 2;
    public static final int NETWORK_WIFI_TX_DATA = 3;
    public static final int NUM_BLUETOOTH_STATES = 4;
    public static final int NUM_DATA_CONNECTION_TYPES = 17;
    public static final int NUM_NETWORK_ACTIVITY_TYPES = 4;
    public static final int NUM_SCREEN_BRIGHTNESS_BINS = 5;
    public static final int NUM_WIFI_SIGNAL_STRENGTH_BINS = 5;
    public static final int NUM_WIFI_STATES = 8;
    public static final int NUM_WIFI_SUPPL_STATES = 13;
    private static final String POWER_USE_ITEM_DATA = "pwi";
    private static final String POWER_USE_SUMMARY_DATA = "pws";
    private static final String PROCESS_DATA = "pr";
    public static final int PROCESS_STATE = 12;
    public static final int SCREEN_BRIGHTNESS_BRIGHT = 4;
    public static final int SCREEN_BRIGHTNESS_DARK = 0;
    private static final String SCREEN_BRIGHTNESS_DATA = "br";
    public static final int SCREEN_BRIGHTNESS_DIM = 1;
    public static final int SCREEN_BRIGHTNESS_LIGHT = 3;
    public static final int SCREEN_BRIGHTNESS_MEDIUM = 2;
    static final String[] SCREEN_BRIGHTNESS_NAMES = null;
    static final String[] SCREEN_BRIGHTNESS_SHORT_NAMES = null;
    public static final int SENSOR = 3;
    private static final String SENSOR_DATA = "sr";
    public static final String SERVICE_NAME = "batterystats";
    private static final String SIGNAL_SCANNING_TIME_DATA = "sst";
    private static final String SIGNAL_STRENGTH_COUNT_DATA = "sgc";
    private static final String SIGNAL_STRENGTH_TIME_DATA = "sgt";
    private static final String STATE_TIME_DATA = "st";
    public static final int STATS_CURRENT = 1;
    public static final int STATS_SINCE_CHARGED = 0;
    public static final int STATS_SINCE_UNPLUGGED = 2;
    private static final String[] STAT_NAMES = null;
    public static final long STEP_LEVEL_INITIAL_MODE_MASK = 71776119061217280L;
    public static final long STEP_LEVEL_INITIAL_MODE_SHIFT = 48;
    public static final long STEP_LEVEL_LEVEL_MASK = 280375465082880L;
    public static final long STEP_LEVEL_LEVEL_SHIFT = 40;
    public static final int STEP_LEVEL_MODE_POWER_SAVE = 4;
    public static final int STEP_LEVEL_MODE_SCREEN_STATE = 3;
    public static final long STEP_LEVEL_MODIFIED_MODE_MASK = -72057594037927936L;
    public static final long STEP_LEVEL_MODIFIED_MODE_SHIFT = 56;
    public static final long STEP_LEVEL_TIME_MASK = 1099511627775L;
    public static final int SYNC = 13;
    private static final String SYNC_DATA = "sy";
    private static final String UID_DATA = "uid";
    private static final String USER_ACTIVITY_DATA = "ua";
    private static final String VERSION_DATA = "vers";
    private static final String VIBRATOR_DATA = "vib";
    public static final int VIBRATOR_ON = 9;
    public static final int VIDEO_TURNED_ON = 8;
    private static final String WAKELOCK_DATA = "wl";
    private static final String WAKEUP_REASON_DATA = "wr";
    public static final int WAKE_TYPE_FULL = 1;
    public static final int WAKE_TYPE_PARTIAL = 0;
    public static final int WAKE_TYPE_WINDOW = 2;
    public static final int WIFI_BATCHED_SCAN = 11;
    private static final String WIFI_DATA = "wfl";
    public static final int WIFI_MULTICAST_ENABLED = 7;
    public static final int WIFI_RUNNING = 4;
    public static final int WIFI_SCAN = 6;
    private static final String WIFI_SIGNAL_STRENGTH_COUNT_DATA = "wsgc";
    private static final String WIFI_SIGNAL_STRENGTH_TIME_DATA = "wsgt";
    private static final String WIFI_STATE_COUNT_DATA = "wsc";
    static final String[] WIFI_STATE_NAMES = null;
    public static final int WIFI_STATE_OFF = 0;
    public static final int WIFI_STATE_OFF_SCANNING = 1;
    public static final int WIFI_STATE_ON_CONNECTED_P2P = 5;
    public static final int WIFI_STATE_ON_CONNECTED_STA = 4;
    public static final int WIFI_STATE_ON_CONNECTED_STA_P2P = 6;
    public static final int WIFI_STATE_ON_DISCONNECTED = 3;
    public static final int WIFI_STATE_ON_NO_NETWORKS = 2;
    public static final int WIFI_STATE_SOFT_AP = 7;
    private static final String WIFI_STATE_TIME_DATA = "wst";
    public static final int WIFI_SUPPL_STATE_ASSOCIATED = 7;
    public static final int WIFI_SUPPL_STATE_ASSOCIATING = 6;
    public static final int WIFI_SUPPL_STATE_AUTHENTICATING = 5;
    public static final int WIFI_SUPPL_STATE_COMPLETED = 10;
    private static final String WIFI_SUPPL_STATE_COUNT_DATA = "wssc";
    public static final int WIFI_SUPPL_STATE_DISCONNECTED = 1;
    public static final int WIFI_SUPPL_STATE_DORMANT = 11;
    public static final int WIFI_SUPPL_STATE_FOUR_WAY_HANDSHAKE = 8;
    public static final int WIFI_SUPPL_STATE_GROUP_HANDSHAKE = 9;
    public static final int WIFI_SUPPL_STATE_INACTIVE = 3;
    public static final int WIFI_SUPPL_STATE_INTERFACE_DISABLED = 2;
    public static final int WIFI_SUPPL_STATE_INVALID = 0;
    static final String[] WIFI_SUPPL_STATE_NAMES = null;
    public static final int WIFI_SUPPL_STATE_SCANNING = 4;
    static final String[] WIFI_SUPPL_STATE_SHORT_NAMES = null;
    private static final String WIFI_SUPPL_STATE_TIME_DATA = "wsst";
    public static final int WIFI_SUPPL_STATE_UNINITIALIZED = 12;
    private final StringBuilder mFormatBuilder;
    private final Formatter mFormatter;

    /* renamed from: android.os.BatteryStats.1 */
    class AnonymousClass1 implements Comparator<TimerEntry> {
        final /* synthetic */ BatteryStats this$0;

        AnonymousClass1(android.os.BatteryStats r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.os.BatteryStats.1.<init>(android.os.BatteryStats):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.os.BatteryStats.1.<init>(android.os.BatteryStats):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.os.BatteryStats.1.<init>(android.os.BatteryStats):void");
        }

        public int compare(android.os.BatteryStats.TimerEntry r1, android.os.BatteryStats.TimerEntry r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.os.BatteryStats.1.compare(android.os.BatteryStats$TimerEntry, android.os.BatteryStats$TimerEntry):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.os.BatteryStats.1.compare(android.os.BatteryStats$TimerEntry, android.os.BatteryStats$TimerEntry):int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e4
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.os.BatteryStats.1.compare(android.os.BatteryStats$TimerEntry, android.os.BatteryStats$TimerEntry):int");
        }

        public /* bridge */ /* synthetic */ int compare(java.lang.Object r1, java.lang.Object r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.os.BatteryStats.1.compare(java.lang.Object, java.lang.Object):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.os.BatteryStats.1.compare(java.lang.Object, java.lang.Object):int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.os.BatteryStats.1.compare(java.lang.Object, java.lang.Object):int");
        }
    }

    /* renamed from: android.os.BatteryStats.2 */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$android$internal$os$BatterySipper$DrainType = null;

        static {
            $SwitchMap$com$android$internal$os$BatterySipper$DrainType = new int[DrainType.values().length];
            try {
                $SwitchMap$com$android$internal$os$BatterySipper$DrainType[DrainType.IDLE.ordinal()] = BatteryStats.WIFI_SUPPL_STATE_DISCONNECTED;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$android$internal$os$BatterySipper$DrainType[DrainType.CELL.ordinal()] = BatteryStats.WIFI_SUPPL_STATE_INTERFACE_DISABLED;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$android$internal$os$BatterySipper$DrainType[DrainType.PHONE.ordinal()] = BatteryStats.WIFI_SUPPL_STATE_INACTIVE;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$android$internal$os$BatterySipper$DrainType[DrainType.WIFI.ordinal()] = BatteryStats.WIFI_SUPPL_STATE_SCANNING;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$android$internal$os$BatterySipper$DrainType[DrainType.BLUETOOTH.ordinal()] = BatteryStats.WIFI_SUPPL_STATE_AUTHENTICATING;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$android$internal$os$BatterySipper$DrainType[DrainType.SCREEN.ordinal()] = BatteryStats.WIFI_SUPPL_STATE_ASSOCIATING;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$android$internal$os$BatterySipper$DrainType[DrainType.FLASHLIGHT.ordinal()] = BatteryStats.WIFI_SUPPL_STATE_ASSOCIATED;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$android$internal$os$BatterySipper$DrainType[DrainType.APP.ordinal()] = BatteryStats.WIFI_SUPPL_STATE_FOUR_WAY_HANDSHAKE;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$android$internal$os$BatterySipper$DrainType[DrainType.USER.ordinal()] = BatteryStats.WIFI_SUPPL_STATE_GROUP_HANDSHAKE;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$android$internal$os$BatterySipper$DrainType[DrainType.UNACCOUNTED.ordinal()] = BatteryStats.WIFI_SUPPL_STATE_COMPLETED;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$android$internal$os$BatterySipper$DrainType[DrainType.OVERCOUNTED.ordinal()] = BatteryStats.WIFI_SUPPL_STATE_DORMANT;
            } catch (NoSuchFieldError e11) {
            }
        }
    }

    public static final class BitDescription {
        public final int mask;
        public final String name;
        public final int shift;
        public final String shortName;
        public final String[] shortValues;
        public final String[] values;

        public BitDescription(int mask, String name, String shortName) {
            this.mask = mask;
            this.shift = -1;
            this.name = name;
            this.shortName = shortName;
            this.values = null;
            this.shortValues = null;
        }

        public BitDescription(int mask, int shift, String name, String shortName, String[] values, String[] shortValues) {
            this.mask = mask;
            this.shift = shift;
            this.name = name;
            this.shortName = shortName;
            this.values = values;
            this.shortValues = shortValues;
        }
    }

    public static abstract class Counter {
        public abstract int getCountLocked(int i);

        public abstract void logState(Printer printer, String str);

        public Counter() {
        }
    }

    public static final class HistoryEventTracker {
        private final HashMap<String, SparseIntArray>[] mActiveEvents;

        public HistoryEventTracker() {
            this.mActiveEvents = new HashMap[BatteryStats.WIFI_SUPPL_STATE_COMPLETED];
        }

        public boolean updateState(int code, String name, int uid, int poolIdx) {
            int idx;
            HashMap<String, SparseIntArray> active;
            SparseIntArray uids;
            if ((AccessibilityNodeInfo.ACTION_PASTE & code) != 0) {
                idx = code & HistoryItem.EVENT_TYPE_MASK;
                active = this.mActiveEvents[idx];
                if (active == null) {
                    active = new HashMap();
                    this.mActiveEvents[idx] = active;
                }
                uids = (SparseIntArray) active.get(name);
                if (uids == null) {
                    uids = new SparseIntArray();
                    active.put(name, uids);
                }
                if (uids.indexOfKey(uid) >= 0) {
                    return BatteryStats.LOCAL_LOGV;
                }
                uids.put(uid, poolIdx);
            } else if ((code & AccessibilityNodeInfo.ACTION_COPY) != 0) {
                active = this.mActiveEvents[code & HistoryItem.EVENT_TYPE_MASK];
                if (active == null) {
                    return BatteryStats.LOCAL_LOGV;
                }
                uids = (SparseIntArray) active.get(name);
                if (uids == null) {
                    return BatteryStats.LOCAL_LOGV;
                }
                idx = uids.indexOfKey(uid);
                if (idx < 0) {
                    return BatteryStats.LOCAL_LOGV;
                }
                uids.removeAt(idx);
                if (uids.size() <= 0) {
                    active.remove(name);
                }
            }
            return true;
        }

        public void removeEvents(int code) {
            this.mActiveEvents[code & HistoryItem.EVENT_TYPE_MASK] = null;
        }

        public HashMap<String, SparseIntArray> getStateForEvent(int code) {
            return this.mActiveEvents[code];
        }
    }

    public static final class HistoryItem implements Parcelable {
        public static final byte CMD_CURRENT_TIME = (byte) 5;
        public static final byte CMD_NULL = (byte) -1;
        public static final byte CMD_OVERFLOW = (byte) 6;
        public static final byte CMD_RESET = (byte) 7;
        public static final byte CMD_SHUTDOWN = (byte) 8;
        public static final byte CMD_START = (byte) 4;
        public static final byte CMD_UPDATE = (byte) 0;
        public static final int EVENT_CONNECTIVITY_CHANGED = 9;
        public static final int EVENT_COUNT = 10;
        public static final int EVENT_FLAG_FINISH = 16384;
        public static final int EVENT_FLAG_START = 32768;
        public static final int EVENT_FOREGROUND = 2;
        public static final int EVENT_FOREGROUND_FINISH = 16386;
        public static final int EVENT_FOREGROUND_START = 32770;
        public static final int EVENT_JOB = 6;
        public static final int EVENT_JOB_FINISH = 16390;
        public static final int EVENT_JOB_START = 32774;
        public static final int EVENT_NONE = 0;
        public static final int EVENT_PROC = 1;
        public static final int EVENT_PROC_FINISH = 16385;
        public static final int EVENT_PROC_START = 32769;
        public static final int EVENT_SYNC = 4;
        public static final int EVENT_SYNC_FINISH = 16388;
        public static final int EVENT_SYNC_START = 32772;
        public static final int EVENT_TOP = 3;
        public static final int EVENT_TOP_FINISH = 16387;
        public static final int EVENT_TOP_START = 32771;
        public static final int EVENT_TYPE_MASK = -49153;
        public static final int EVENT_USER_FOREGROUND = 8;
        public static final int EVENT_USER_FOREGROUND_FINISH = 16392;
        public static final int EVENT_USER_FOREGROUND_START = 32776;
        public static final int EVENT_USER_RUNNING = 7;
        public static final int EVENT_USER_RUNNING_FINISH = 16391;
        public static final int EVENT_USER_RUNNING_START = 32775;
        public static final int EVENT_WAKE_LOCK = 5;
        public static final int EVENT_WAKE_LOCK_FINISH = 16389;
        public static final int EVENT_WAKE_LOCK_START = 32773;
        public static final int MOST_INTERESTING_STATES = 1900544;
        public static final int MOST_INTERESTING_STATES2 = -1879048192;
        public static final int STATE2_FLASHLIGHT_FLAG = 134217728;
        public static final int STATE2_LOW_POWER_FLAG = Integer.MIN_VALUE;
        public static final int STATE2_VIDEO_ON_FLAG = 1073741824;
        public static final int STATE2_WIFI_ON_FLAG = 268435456;
        public static final int STATE2_WIFI_RUNNING_FLAG = 536870912;
        public static final int STATE2_WIFI_SIGNAL_STRENGTH_MASK = 112;
        public static final int STATE2_WIFI_SIGNAL_STRENGTH_SHIFT = 4;
        public static final int STATE2_WIFI_SUPPL_STATE_MASK = 15;
        public static final int STATE2_WIFI_SUPPL_STATE_SHIFT = 0;
        public static final int STATE_AUDIO_ON_FLAG = 4194304;
        public static final int STATE_BATTERY_PLUGGED_FLAG = 524288;
        public static final int STATE_BLUETOOTH_ON_FLAG = 65536;
        public static final int STATE_BRIGHTNESS_MASK = 7;
        public static final int STATE_BRIGHTNESS_SHIFT = 0;
        public static final int STATE_CPU_RUNNING_FLAG = Integer.MIN_VALUE;
        public static final int STATE_DATA_CONNECTION_MASK = 15872;
        public static final int STATE_DATA_CONNECTION_SHIFT = 9;
        public static final int STATE_GPS_ON_FLAG = 536870912;
        public static final int STATE_MOBILE_RADIO_ACTIVE_FLAG = 33554432;
        public static final int STATE_PHONE_IN_CALL_FLAG = 262144;
        public static final int STATE_PHONE_SCANNING_FLAG = 2097152;
        public static final int STATE_PHONE_SIGNAL_STRENGTH_MASK = 56;
        public static final int STATE_PHONE_SIGNAL_STRENGTH_SHIFT = 3;
        public static final int STATE_PHONE_STATE_MASK = 448;
        public static final int STATE_PHONE_STATE_SHIFT = 6;
        public static final int STATE_SCREEN_ON_FLAG = 1048576;
        public static final int STATE_SENSOR_ON_FLAG = 8388608;
        public static final int STATE_WAKE_LOCK_FLAG = 1073741824;
        public static final int STATE_WIFI_FULL_LOCK_FLAG = 268435456;
        public static final int STATE_WIFI_MULTICAST_ON_FLAG = 67108864;
        public static final int STATE_WIFI_SCAN_FLAG = 134217728;
        public byte batteryHealth;
        public byte batteryLevel;
        public byte batteryPlugType;
        public byte batteryStatus;
        public short batteryTemperature;
        public char batteryVoltage;
        public byte cmd;
        public long currentTime;
        public int eventCode;
        public HistoryTag eventTag;
        public final HistoryTag localEventTag;
        public final HistoryTag localWakeReasonTag;
        public final HistoryTag localWakelockTag;
        public HistoryItem next;
        public int numReadInts;
        public int states;
        public int states2;
        public long time;
        public HistoryTag wakeReasonTag;
        public HistoryTag wakelockTag;

        public boolean isDeltaData() {
            return this.cmd == null ? true : BatteryStats.LOCAL_LOGV;
        }

        public HistoryItem() {
            this.cmd = CMD_NULL;
            this.localWakelockTag = new HistoryTag();
            this.localWakeReasonTag = new HistoryTag();
            this.localEventTag = new HistoryTag();
        }

        public HistoryItem(long time, Parcel src) {
            this.cmd = CMD_NULL;
            this.localWakelockTag = new HistoryTag();
            this.localWakeReasonTag = new HistoryTag();
            this.localEventTag = new HistoryTag();
            this.time = time;
            this.numReadInts = EVENT_FOREGROUND;
            readFromParcel(src);
        }

        public int describeContents() {
            return STATE_BRIGHTNESS_SHIFT;
        }

        public void writeToParcel(Parcel dest, int flags) {
            int i;
            int i2 = STATE_BRIGHTNESS_SHIFT;
            dest.writeLong(this.time);
            int i3 = ((this.batteryPlugType << 24) & 251658240) | ((((this.cmd & EditorInfo.IME_MASK_ACTION) | ((this.batteryLevel << EVENT_USER_FOREGROUND) & MotionEvent.ACTION_POINTER_INDEX_MASK)) | ((this.batteryStatus << BatteryStats.DUMP_VERBOSE) & SurfaceControl.FX_SURFACE_MASK)) | ((this.batteryHealth << 20) & 15728640));
            if (this.wakelockTag != null) {
                i = STATE_WIFI_FULL_LOCK_FLAG;
            } else {
                i = STATE_BRIGHTNESS_SHIFT;
            }
            i = (this.wakeReasonTag != null ? STATE_GPS_ON_FLAG : STATE_BRIGHTNESS_SHIFT) | (i3 | i);
            if (this.eventCode != 0) {
                i2 = STATE_WAKE_LOCK_FLAG;
            }
            dest.writeInt(i | i2);
            dest.writeInt((this.batteryTemperature & AppSecurityPermissions.WHICH_ALL) | ((this.batteryVoltage << BatteryStats.DUMP_VERBOSE) & Menu.CATEGORY_MASK));
            dest.writeInt(this.states);
            dest.writeInt(this.states2);
            if (this.wakelockTag != null) {
                this.wakelockTag.writeToParcel(dest, flags);
            }
            if (this.wakeReasonTag != null) {
                this.wakeReasonTag.writeToParcel(dest, flags);
            }
            if (this.eventCode != 0) {
                dest.writeInt(this.eventCode);
                this.eventTag.writeToParcel(dest, flags);
            }
            if (this.cmd == EVENT_WAKE_LOCK || this.cmd == STATE_BRIGHTNESS_MASK) {
                dest.writeLong(this.currentTime);
            }
        }

        public void readFromParcel(Parcel src) {
            int start = src.dataPosition();
            int bat = src.readInt();
            this.cmd = (byte) (bat & EditorInfo.IME_MASK_ACTION);
            this.batteryLevel = (byte) ((bat >> EVENT_USER_FOREGROUND) & EditorInfo.IME_MASK_ACTION);
            this.batteryStatus = (byte) ((bat >> BatteryStats.DUMP_VERBOSE) & STATE2_WIFI_SUPPL_STATE_MASK);
            this.batteryHealth = (byte) ((bat >> 20) & STATE2_WIFI_SUPPL_STATE_MASK);
            this.batteryPlugType = (byte) ((bat >> 24) & STATE2_WIFI_SUPPL_STATE_MASK);
            int bat2 = src.readInt();
            this.batteryTemperature = (short) (bat2 & AppSecurityPermissions.WHICH_ALL);
            this.batteryVoltage = (char) ((bat2 >> BatteryStats.DUMP_VERBOSE) & AppSecurityPermissions.WHICH_ALL);
            this.states = src.readInt();
            this.states2 = src.readInt();
            if ((STATE_WIFI_FULL_LOCK_FLAG & bat) != 0) {
                this.wakelockTag = this.localWakelockTag;
                this.wakelockTag.readFromParcel(src);
            } else {
                this.wakelockTag = null;
            }
            if ((STATE_GPS_ON_FLAG & bat) != 0) {
                this.wakeReasonTag = this.localWakeReasonTag;
                this.wakeReasonTag.readFromParcel(src);
            } else {
                this.wakeReasonTag = null;
            }
            if ((STATE_WAKE_LOCK_FLAG & bat) != 0) {
                this.eventCode = src.readInt();
                this.eventTag = this.localEventTag;
                this.eventTag.readFromParcel(src);
            } else {
                this.eventCode = STATE_BRIGHTNESS_SHIFT;
                this.eventTag = null;
            }
            if (this.cmd == EVENT_WAKE_LOCK || this.cmd == STATE_BRIGHTNESS_MASK) {
                this.currentTime = src.readLong();
            } else {
                this.currentTime = 0;
            }
            this.numReadInts += (src.dataPosition() - start) / STATE2_WIFI_SIGNAL_STRENGTH_SHIFT;
        }

        public void clear() {
            this.time = 0;
            this.cmd = CMD_NULL;
            this.batteryLevel = CMD_UPDATE;
            this.batteryStatus = CMD_UPDATE;
            this.batteryHealth = CMD_UPDATE;
            this.batteryPlugType = CMD_UPDATE;
            this.batteryTemperature = (short) 0;
            this.batteryVoltage = '\u0000';
            this.states = STATE_BRIGHTNESS_SHIFT;
            this.states2 = STATE_BRIGHTNESS_SHIFT;
            this.wakelockTag = null;
            this.wakeReasonTag = null;
            this.eventCode = STATE_BRIGHTNESS_SHIFT;
            this.eventTag = null;
        }

        public void setTo(HistoryItem o) {
            this.time = o.time;
            this.cmd = o.cmd;
            setToCommon(o);
        }

        public void setTo(long time, byte cmd, HistoryItem o) {
            this.time = time;
            this.cmd = cmd;
            setToCommon(o);
        }

        private void setToCommon(HistoryItem o) {
            this.batteryLevel = o.batteryLevel;
            this.batteryStatus = o.batteryStatus;
            this.batteryHealth = o.batteryHealth;
            this.batteryPlugType = o.batteryPlugType;
            this.batteryTemperature = o.batteryTemperature;
            this.batteryVoltage = o.batteryVoltage;
            this.states = o.states;
            this.states2 = o.states2;
            if (o.wakelockTag != null) {
                this.wakelockTag = this.localWakelockTag;
                this.wakelockTag.setTo(o.wakelockTag);
            } else {
                this.wakelockTag = null;
            }
            if (o.wakeReasonTag != null) {
                this.wakeReasonTag = this.localWakeReasonTag;
                this.wakeReasonTag.setTo(o.wakeReasonTag);
            } else {
                this.wakeReasonTag = null;
            }
            this.eventCode = o.eventCode;
            if (o.eventTag != null) {
                this.eventTag = this.localEventTag;
                this.eventTag.setTo(o.eventTag);
            } else {
                this.eventTag = null;
            }
            this.currentTime = o.currentTime;
        }

        public boolean sameNonEvent(HistoryItem o) {
            return (this.batteryLevel == o.batteryLevel && this.batteryStatus == o.batteryStatus && this.batteryHealth == o.batteryHealth && this.batteryPlugType == o.batteryPlugType && this.batteryTemperature == o.batteryTemperature && this.batteryVoltage == o.batteryVoltage && this.states == o.states && this.states2 == o.states2 && this.currentTime == o.currentTime) ? true : BatteryStats.LOCAL_LOGV;
        }

        public boolean same(HistoryItem o) {
            if (!sameNonEvent(o) || this.eventCode != o.eventCode) {
                return BatteryStats.LOCAL_LOGV;
            }
            if (this.wakelockTag != o.wakelockTag && (this.wakelockTag == null || o.wakelockTag == null || !this.wakelockTag.equals(o.wakelockTag))) {
                return BatteryStats.LOCAL_LOGV;
            }
            if (this.wakeReasonTag != o.wakeReasonTag && (this.wakeReasonTag == null || o.wakeReasonTag == null || !this.wakeReasonTag.equals(o.wakeReasonTag))) {
                return BatteryStats.LOCAL_LOGV;
            }
            if (this.eventTag == o.eventTag || (this.eventTag != null && o.eventTag != null && this.eventTag.equals(o.eventTag))) {
                return true;
            }
            return BatteryStats.LOCAL_LOGV;
        }
    }

    public static class HistoryPrinter {
        long lastTime;
        int oldHealth;
        int oldLevel;
        int oldPlug;
        int oldState;
        int oldState2;
        int oldStatus;
        int oldTemp;
        int oldVolt;

        public HistoryPrinter() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.os.BatteryStats.HistoryPrinter.<init>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.os.BatteryStats.HistoryPrinter.<init>():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.os.BatteryStats.HistoryPrinter.<init>():void");
        }

        public void printNextItem(java.io.PrintWriter r1, android.os.BatteryStats.HistoryItem r2, long r3, boolean r5, boolean r6) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.os.BatteryStats.HistoryPrinter.printNextItem(java.io.PrintWriter, android.os.BatteryStats$HistoryItem, long, boolean, boolean):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.os.BatteryStats.HistoryPrinter.printNextItem(java.io.PrintWriter, android.os.BatteryStats$HistoryItem, long, boolean, boolean):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.os.BatteryStats.HistoryPrinter.printNextItem(java.io.PrintWriter, android.os.BatteryStats$HistoryItem, long, boolean, boolean):void");
        }

        void reset() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.os.BatteryStats.HistoryPrinter.reset():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.os.BatteryStats.HistoryPrinter.reset():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.os.BatteryStats.HistoryPrinter.reset():void");
        }
    }

    public static final class HistoryTag {
        public int poolIdx;
        public String string;
        public int uid;

        public HistoryTag() {
        }

        public void setTo(HistoryTag o) {
            this.string = o.string;
            this.uid = o.uid;
            this.poolIdx = o.poolIdx;
        }

        public void setTo(String _string, int _uid) {
            this.string = _string;
            this.uid = _uid;
            this.poolIdx = -1;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.string);
            dest.writeInt(this.uid);
        }

        public void readFromParcel(Parcel src) {
            this.string = src.readString();
            this.uid = src.readInt();
            this.poolIdx = -1;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return BatteryStats.LOCAL_LOGV;
            }
            HistoryTag that = (HistoryTag) o;
            if (this.uid != that.uid) {
                return BatteryStats.LOCAL_LOGV;
            }
            if (this.string.equals(that.string)) {
                return true;
            }
            return BatteryStats.LOCAL_LOGV;
        }

        public int hashCode() {
            return (this.string.hashCode() * 31) + this.uid;
        }
    }

    public static abstract class LongCounter {
        public abstract long getCountLocked(int i);

        public abstract void logState(Printer printer, String str);

        public LongCounter() {
        }
    }

    public static abstract class Timer {
        public abstract int getCountLocked(int i);

        public abstract long getTotalTimeLocked(long j, int i);

        public abstract void logState(Printer printer, String str);

        public Timer() {
        }
    }

    static final class TimerEntry {
        final int mId;
        final String mName;
        final long mTime;
        final Timer mTimer;

        TimerEntry(java.lang.String r1, int r2, android.os.BatteryStats.Timer r3, long r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.os.BatteryStats.TimerEntry.<init>(java.lang.String, int, android.os.BatteryStats$Timer, long):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.os.BatteryStats.TimerEntry.<init>(java.lang.String, int, android.os.BatteryStats$Timer, long):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.os.BatteryStats.TimerEntry.<init>(java.lang.String, int, android.os.BatteryStats$Timer, long):void");
        }
    }

    public static abstract class Uid {
        public static final int NUM_PROCESS_STATE = 3;
        public static final int NUM_USER_ACTIVITY_TYPES = 3;
        public static final int NUM_WIFI_BATCHED_SCAN_BINS = 5;
        public static final int PROCESS_STATE_ACTIVE = 1;
        public static final int PROCESS_STATE_FOREGROUND = 0;
        static final String[] PROCESS_STATE_NAMES = null;
        public static final int PROCESS_STATE_RUNNING = 2;
        static final String[] USER_ACTIVITY_TYPES = null;

        public class Pid {
            public int mWakeNesting;
            public long mWakeStartMs;
            public long mWakeSumMs;
            final /* synthetic */ Uid this$0;

            public Pid() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r0 = this;
                r0.this$0 = r1;
                r0.<init>();
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: android.os.BatteryStats.Uid.Pid.<init>(android.os.BatteryStats$Uid):void");
            }
        }

        public static abstract class Pkg {

            public abstract class Serv {
                final /* synthetic */ Pkg this$0;

                public abstract int getLaunches(int i);

                public abstract long getStartTime(long j, int i);

                public abstract int getStarts(int i);

                public Serv(android.os.BatteryStats.Uid.Pkg r1) {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                    /*
                    r0 = this;
                    r0.this$0 = r1;
                    r0.<init>();
                    return;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: android.os.BatteryStats.Uid.Pkg.Serv.<init>(android.os.BatteryStats$Uid$Pkg):void");
                }
            }

            public abstract Map<String, ? extends Serv> getServiceStats();

            public abstract int getWakeups(int i);

            public Pkg() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r0 = this;
                r0.<init>();
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: android.os.BatteryStats.Uid.Pkg.<init>():void");
            }
        }

        public static abstract class Proc {

            public static class ExcessivePower {
                public static final int TYPE_CPU = 2;
                public static final int TYPE_WAKE = 1;
                public long overTime;
                public int type;
                public long usedTime;

                public ExcessivePower() {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                    /*
                    r0 = this;
                    r0.<init>();
                    return;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: android.os.BatteryStats.Uid.Proc.ExcessivePower.<init>():void");
                }
            }

            public abstract int countExcessivePowers();

            public abstract ExcessivePower getExcessivePower(int i);

            public abstract long getForegroundTime(int i);

            public abstract int getNumAnrs(int i);

            public abstract int getNumCrashes(int i);

            public abstract int getStarts(int i);

            public abstract long getSystemTime(int i);

            public abstract long getTimeAtCpuSpeedStep(int i, int i2);

            public abstract long getUserTime(int i);

            public abstract boolean isActive();

            public Proc() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r0 = this;
                r0.<init>();
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: android.os.BatteryStats.Uid.Proc.<init>():void");
            }
        }

        public static abstract class Sensor {
            public static final int GPS = -10000;

            public abstract int getHandle();

            public abstract Timer getSensorTime();

            public Sensor() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r0 = this;
                r0.<init>();
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: android.os.BatteryStats.Uid.Sensor.<init>():void");
            }
        }

        public static abstract class Wakelock {
            public abstract Timer getWakeTime(int i);

            public Wakelock() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
                /*
                r0 = this;
                r0.<init>();
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: android.os.BatteryStats.Uid.Wakelock.<init>():void");
            }
        }

        public abstract long getAudioTurnedOnTime(long j, int i);

        public abstract Timer getForegroundActivityTimer();

        public abstract long getFullWifiLockTime(long j, int i);

        public abstract Map<String, ? extends Timer> getJobStats();

        public abstract int getMobileRadioActiveCount(int i);

        public abstract long getMobileRadioActiveTime(int i);

        public abstract long getNetworkActivityBytes(int i, int i2);

        public abstract long getNetworkActivityPackets(int i, int i2);

        public abstract Map<String, ? extends Pkg> getPackageStats();

        public abstract SparseArray<? extends Pid> getPidStats();

        public abstract long getProcessStateTime(int i, long j, int i2);

        public abstract Map<String, ? extends Proc> getProcessStats();

        public abstract SparseArray<? extends Sensor> getSensorStats();

        public abstract Map<String, ? extends Timer> getSyncStats();

        public abstract int getUid();

        public abstract int getUserActivityCount(int i, int i2);

        public abstract Timer getVibratorOnTimer();

        public abstract long getVideoTurnedOnTime(long j, int i);

        public abstract Map<String, ? extends Wakelock> getWakelockStats();

        public abstract long getWifiBatchedScanTime(int i, long j, int i2);

        public abstract long getWifiMulticastTime(long j, int i);

        public abstract long getWifiRunningTime(long j, int i);

        public abstract long getWifiScanTime(long j, int i);

        public abstract boolean hasNetworkActivity();

        public abstract boolean hasUserActivity();

        public abstract void noteActivityPausedLocked(long j);

        public abstract void noteActivityResumedLocked(long j);

        public abstract void noteFullWifiLockAcquiredLocked(long j);

        public abstract void noteFullWifiLockReleasedLocked(long j);

        public abstract void noteUserActivityLocked(int i);

        public abstract void noteWifiBatchedScanStartedLocked(int i, long j);

        public abstract void noteWifiBatchedScanStoppedLocked(long j);

        public abstract void noteWifiMulticastDisabledLocked(long j);

        public abstract void noteWifiMulticastEnabledLocked(long j);

        public abstract void noteWifiRunningLocked(long j);

        public abstract void noteWifiScanStartedLocked(long j);

        public abstract void noteWifiScanStoppedLocked(long j);

        public abstract void noteWifiStoppedLocked(long j);

        public Uid() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r0 = this;
            r0.<init>();
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.os.BatteryStats.Uid.<init>():void");
        }

        static {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: SSA rename variables already executed
	at jadx.core.dex.visitors.ssa.SSATransform.renameVariables(SSATransform.java:119)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:52)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r5 = 3;
            r4 = 2;
            r3 = 1;
            r2 = 0;
            r0 = new java.lang.String[r5];
            r1 = "Foreground";
            r0[r2] = r1;
            r1 = "Active";
            r0[r3] = r1;
            r1 = "Running";
            r0[r4] = r1;
            PROCESS_STATE_NAMES = r0;
            r0 = new java.lang.String[r5];
            r1 = "other";
            r0[r2] = r1;
            r1 = "button";
            r0[r3] = r1;
            r1 = "touch";
            r0[r4] = r1;
            USER_ACTIVITY_TYPES = r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.os.BatteryStats.Uid.<clinit>():void");
        }
    }

    public abstract void commitCurrentHistoryBatchLocked();

    public abstract long computeBatteryRealtime(long j, int i);

    public abstract long computeBatteryScreenOffRealtime(long j, int i);

    public abstract long computeBatteryScreenOffUptime(long j, int i);

    public abstract long computeBatteryTimeRemaining(long j);

    public abstract long computeBatteryUptime(long j, int i);

    public abstract long computeChargeTimeRemaining(long j);

    public abstract long computeRealtime(long j, int i);

    public abstract long computeUptime(long j, int i);

    public abstract void finishIteratingHistoryLocked();

    public abstract void finishIteratingOldHistoryLocked();

    public abstract long getBatteryRealtime(long j);

    public abstract long getBatteryUptime(long j);

    public abstract long getBluetoothOnTime(long j, int i);

    public abstract int getBluetoothPingCount();

    public abstract int getBluetoothStateCount(int i, int i2);

    public abstract long getBluetoothStateTime(int i, long j, int i2);

    public abstract long[] getChargeStepDurationsArray();

    public abstract int getCpuSpeedSteps();

    public abstract int getDischargeAmount(int i);

    public abstract int getDischargeAmountScreenOff();

    public abstract int getDischargeAmountScreenOffSinceCharge();

    public abstract int getDischargeAmountScreenOn();

    public abstract int getDischargeAmountScreenOnSinceCharge();

    public abstract int getDischargeCurrentLevel();

    public abstract int getDischargeStartLevel();

    public abstract long[] getDischargeStepDurationsArray();

    public abstract String getEndPlatformVersion();

    public abstract long getFlashlightOnCount(int i);

    public abstract long getFlashlightOnTime(long j, int i);

    public abstract long getGlobalWifiRunningTime(long j, int i);

    public abstract int getHighDischargeAmountSinceCharge();

    public abstract long getHistoryBaseTime();

    public abstract int getHistoryStringPoolBytes();

    public abstract int getHistoryStringPoolSize();

    public abstract String getHistoryTagPoolString(int i);

    public abstract int getHistoryTagPoolUid(int i);

    public abstract int getHistoryTotalSize();

    public abstract int getHistoryUsedSize();

    public abstract long getInteractiveTime(long j, int i);

    public abstract boolean getIsOnBattery();

    public abstract Map<String, ? extends Timer> getKernelWakelockStats();

    public abstract int getLowDischargeAmountSinceCharge();

    public abstract int getLowPowerModeEnabledCount(int i);

    public abstract long getLowPowerModeEnabledTime(long j, int i);

    public abstract long getMobileRadioActiveAdjustedTime(int i);

    public abstract int getMobileRadioActiveCount(int i);

    public abstract long getMobileRadioActiveTime(long j, int i);

    public abstract int getMobileRadioActiveUnknownCount(int i);

    public abstract long getMobileRadioActiveUnknownTime(int i);

    public abstract long getNetworkActivityBytes(int i, int i2);

    public abstract long getNetworkActivityPackets(int i, int i2);

    public abstract boolean getNextHistoryLocked(HistoryItem historyItem);

    public abstract boolean getNextOldHistoryLocked(HistoryItem historyItem);

    public abstract int getNumChargeStepDurations();

    public abstract int getNumConnectivityChange(int i);

    public abstract int getNumDischargeStepDurations();

    public abstract int getParcelVersion();

    public abstract int getPhoneDataConnectionCount(int i, int i2);

    public abstract long getPhoneDataConnectionTime(int i, long j, int i2);

    public abstract int getPhoneOnCount(int i);

    public abstract long getPhoneOnTime(long j, int i);

    public abstract long getPhoneSignalScanningTime(long j, int i);

    public abstract int getPhoneSignalStrengthCount(int i, int i2);

    public abstract long getPhoneSignalStrengthTime(int i, long j, int i2);

    public abstract long getScreenBrightnessTime(int i, long j, int i2);

    public abstract int getScreenOnCount(int i);

    public abstract long getScreenOnTime(long j, int i);

    public abstract long getStartClockTime();

    public abstract int getStartCount();

    public abstract String getStartPlatformVersion();

    public abstract SparseArray<? extends Uid> getUidStats();

    public abstract Map<String, ? extends Timer> getWakeupReasonStats();

    public abstract long getWifiOnTime(long j, int i);

    public abstract int getWifiSignalStrengthCount(int i, int i2);

    public abstract long getWifiSignalStrengthTime(int i, long j, int i2);

    public abstract int getWifiStateCount(int i, int i2);

    public abstract long getWifiStateTime(int i, long j, int i2);

    public abstract int getWifiSupplStateCount(int i, int i2);

    public abstract long getWifiSupplStateTime(int i, long j, int i2);

    public abstract boolean startIteratingHistoryLocked();

    public abstract boolean startIteratingOldHistoryLocked();

    public abstract void writeToParcelWithoutUids(Parcel parcel, int i);

    public BatteryStats() {
        this.mFormatBuilder = new StringBuilder(DUMP_DEVICE_WIFI_ONLY);
        this.mFormatter = new Formatter(this.mFormatBuilder);
    }

    static {
        String[] strArr = new String[WIFI_SUPPL_STATE_INACTIVE];
        strArr[WIFI_SUPPL_STATE_INVALID] = "l";
        strArr[WIFI_SUPPL_STATE_DISCONNECTED] = FullBackup.CACHE_TREE_TOKEN;
        strArr[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = "u";
        STAT_NAMES = strArr;
        strArr = new String[WIFI_SUPPL_STATE_AUTHENTICATING];
        strArr[WIFI_SUPPL_STATE_INVALID] = "dark";
        strArr[WIFI_SUPPL_STATE_DISCONNECTED] = "dim";
        strArr[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = "medium";
        strArr[WIFI_SUPPL_STATE_INACTIVE] = "light";
        strArr[WIFI_SUPPL_STATE_SCANNING] = "bright";
        SCREEN_BRIGHTNESS_NAMES = strArr;
        strArr = new String[WIFI_SUPPL_STATE_AUTHENTICATING];
        strArr[WIFI_SUPPL_STATE_INVALID] = WifiEnterpriseConfig.ENGINE_DISABLE;
        strArr[WIFI_SUPPL_STATE_DISCONNECTED] = WifiEnterpriseConfig.ENGINE_ENABLE;
        strArr[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = "2";
        strArr[WIFI_SUPPL_STATE_INACTIVE] = "3";
        strArr[WIFI_SUPPL_STATE_SCANNING] = "4";
        SCREEN_BRIGHTNESS_SHORT_NAMES = strArr;
        strArr = new String[NUM_DATA_CONNECTION_TYPES];
        strArr[WIFI_SUPPL_STATE_INVALID] = Parameters.EFFECT_NONE;
        strArr[WIFI_SUPPL_STATE_DISCONNECTED] = "gprs";
        strArr[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = "edge";
        strArr[WIFI_SUPPL_STATE_INACTIVE] = "umts";
        strArr[WIFI_SUPPL_STATE_SCANNING] = "cdma";
        strArr[WIFI_SUPPL_STATE_AUTHENTICATING] = "evdo_0";
        strArr[WIFI_SUPPL_STATE_ASSOCIATING] = "evdo_A";
        strArr[WIFI_SUPPL_STATE_ASSOCIATED] = "1xrtt";
        strArr[WIFI_SUPPL_STATE_FOUR_WAY_HANDSHAKE] = "hsdpa";
        strArr[WIFI_SUPPL_STATE_GROUP_HANDSHAKE] = "hsupa";
        strArr[WIFI_SUPPL_STATE_COMPLETED] = "hspa";
        strArr[WIFI_SUPPL_STATE_DORMANT] = "iden";
        strArr[WIFI_SUPPL_STATE_UNINITIALIZED] = "evdo_b";
        strArr[SYNC] = "lte";
        strArr[JOB] = "ehrpd";
        strArr[DATA_CONNECTION_HSPAP] = "hspap";
        strArr[DUMP_VERBOSE] = CardEmulation.CATEGORY_OTHER;
        DATA_CONNECTION_NAMES = strArr;
        strArr = new String[SYNC];
        strArr[WIFI_SUPPL_STATE_INVALID] = "invalid";
        strArr[WIFI_SUPPL_STATE_DISCONNECTED] = "disconn";
        strArr[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = "disabled";
        strArr[WIFI_SUPPL_STATE_INACTIVE] = "inactive";
        strArr[WIFI_SUPPL_STATE_SCANNING] = "scanning";
        strArr[WIFI_SUPPL_STATE_AUTHENTICATING] = "authenticating";
        strArr[WIFI_SUPPL_STATE_ASSOCIATING] = "associating";
        strArr[WIFI_SUPPL_STATE_ASSOCIATED] = "associated";
        strArr[WIFI_SUPPL_STATE_FOUR_WAY_HANDSHAKE] = "4-way-handshake";
        strArr[WIFI_SUPPL_STATE_GROUP_HANDSHAKE] = "group-handshake";
        strArr[WIFI_SUPPL_STATE_COMPLETED] = "completed";
        strArr[WIFI_SUPPL_STATE_DORMANT] = "dormant";
        strArr[WIFI_SUPPL_STATE_UNINITIALIZED] = "uninit";
        WIFI_SUPPL_STATE_NAMES = strArr;
        strArr = new String[SYNC];
        strArr[WIFI_SUPPL_STATE_INVALID] = "inv";
        strArr[WIFI_SUPPL_STATE_DISCONNECTED] = "dsc";
        strArr[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = "dis";
        strArr[WIFI_SUPPL_STATE_INACTIVE] = "inact";
        strArr[WIFI_SUPPL_STATE_SCANNING] = "scan";
        strArr[WIFI_SUPPL_STATE_AUTHENTICATING] = "auth";
        strArr[WIFI_SUPPL_STATE_ASSOCIATING] = "ascing";
        strArr[WIFI_SUPPL_STATE_ASSOCIATED] = "asced";
        strArr[WIFI_SUPPL_STATE_FOUR_WAY_HANDSHAKE] = "4-way";
        strArr[WIFI_SUPPL_STATE_GROUP_HANDSHAKE] = GroupCipher.varName;
        strArr[WIFI_SUPPL_STATE_COMPLETED] = "compl";
        strArr[WIFI_SUPPL_STATE_DORMANT] = "dorm";
        strArr[WIFI_SUPPL_STATE_UNINITIALIZED] = "uninit";
        WIFI_SUPPL_STATE_SHORT_NAMES = strArr;
        r7 = new BitDescription[18];
        String[] strArr2 = new String[WIFI_SUPPL_STATE_SCANNING];
        strArr2[WIFI_SUPPL_STATE_INVALID] = "in";
        strArr2[WIFI_SUPPL_STATE_DISCONNECTED] = "out";
        strArr2[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = "emergency";
        strArr2[WIFI_SUPPL_STATE_INACTIVE] = Parameters.FLASH_MODE_OFF;
        String[] strArr3 = new String[WIFI_SUPPL_STATE_SCANNING];
        strArr3[WIFI_SUPPL_STATE_INVALID] = "in";
        strArr3[WIFI_SUPPL_STATE_DISCONNECTED] = "out";
        strArr3[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = "em";
        strArr3[WIFI_SUPPL_STATE_INACTIVE] = Parameters.FLASH_MODE_OFF;
        r7[DATA_CONNECTION_HSPAP] = new BitDescription(FileUtils.S_IRWXU, WIFI_SUPPL_STATE_ASSOCIATING, "phone_state", "Pst", strArr2, strArr3);
        strArr2 = SignalStrength.SIGNAL_STRENGTH_NAMES;
        strArr3 = new String[WIFI_SUPPL_STATE_AUTHENTICATING];
        strArr3[WIFI_SUPPL_STATE_INVALID] = WifiEnterpriseConfig.ENGINE_DISABLE;
        strArr3[WIFI_SUPPL_STATE_DISCONNECTED] = WifiEnterpriseConfig.ENGINE_ENABLE;
        strArr3[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = "2";
        strArr3[WIFI_SUPPL_STATE_INACTIVE] = "3";
        strArr3[WIFI_SUPPL_STATE_SCANNING] = "4";
        r7[DUMP_VERBOSE] = new BitDescription(56, WIFI_SUPPL_STATE_INACTIVE, "phone_signal_strength", "Pss", strArr2, strArr3);
        r7[NUM_DATA_CONNECTION_TYPES] = new BitDescription(WIFI_SUPPL_STATE_ASSOCIATED, WIFI_SUPPL_STATE_INVALID, "brightness", "Sb", SCREEN_BRIGHTNESS_NAMES, SCREEN_BRIGHTNESS_SHORT_NAMES);
        HISTORY_STATE_DESCRIPTIONS = r7;
        r7 = new BitDescription[WIFI_SUPPL_STATE_ASSOCIATED];
        r7[WIFI_SUPPL_STATE_INVALID] = new BitDescription(RtlSpacingHelper.UNDEFINED, Global.LOW_POWER_MODE, "lp");
        r7[WIFI_SUPPL_STATE_DISCONNECTED] = new BitDescription(EditorInfo.IME_FLAG_NO_ENTER_ACTION, "video", "v");
        r7[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = new BitDescription(EditorInfo.IME_FLAG_NO_ACCESSORY_ACTION, "wifi_running", "Wr");
        r7[WIFI_SUPPL_STATE_INACTIVE] = new BitDescription(EditorInfo.IME_FLAG_NO_EXTRACT_UI, System.RADIO_WIFI, "W");
        r7[WIFI_SUPPL_STATE_SCANNING] = new BitDescription(EditorInfo.IME_FLAG_NAVIGATE_NEXT, "flashlight", "fl");
        strArr2 = new String[WIFI_SUPPL_STATE_AUTHENTICATING];
        strArr2[WIFI_SUPPL_STATE_INVALID] = WifiEnterpriseConfig.ENGINE_DISABLE;
        strArr2[WIFI_SUPPL_STATE_DISCONNECTED] = WifiEnterpriseConfig.ENGINE_ENABLE;
        strArr2[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = "2";
        strArr2[WIFI_SUPPL_STATE_INACTIVE] = "3";
        strArr2[WIFI_SUPPL_STATE_SCANNING] = "4";
        strArr3 = new String[WIFI_SUPPL_STATE_AUTHENTICATING];
        strArr3[WIFI_SUPPL_STATE_INVALID] = WifiEnterpriseConfig.ENGINE_DISABLE;
        strArr3[WIFI_SUPPL_STATE_DISCONNECTED] = WifiEnterpriseConfig.ENGINE_ENABLE;
        strArr3[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = "2";
        strArr3[WIFI_SUPPL_STATE_INACTIVE] = "3";
        strArr3[WIFI_SUPPL_STATE_SCANNING] = "4";
        r7[WIFI_SUPPL_STATE_AUTHENTICATING] = new BitDescription(KeyEvent.KEYCODE_FORWARD_DEL, WIFI_SUPPL_STATE_SCANNING, "wifi_signal_strength", "Wss", strArr2, strArr3);
        r7[WIFI_SUPPL_STATE_ASSOCIATING] = new BitDescription(DATA_CONNECTION_HSPAP, WIFI_SUPPL_STATE_INVALID, "wifi_suppl", "Wsp", WIFI_SUPPL_STATE_NAMES, WIFI_SUPPL_STATE_SHORT_NAMES);
        HISTORY_STATE2_DESCRIPTIONS = r7;
        strArr = new String[WIFI_SUPPL_STATE_COMPLETED];
        strArr[WIFI_SUPPL_STATE_INVALID] = "null";
        strArr[WIFI_SUPPL_STATE_DISCONNECTED] = "proc";
        strArr[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = FOREGROUND_DATA;
        strArr[WIFI_SUPPL_STATE_INACTIVE] = "top";
        strArr[WIFI_SUPPL_STATE_SCANNING] = "sync";
        strArr[WIFI_SUPPL_STATE_AUTHENTICATING] = "wake_lock_in";
        strArr[WIFI_SUPPL_STATE_ASSOCIATING] = "job";
        strArr[WIFI_SUPPL_STATE_ASSOCIATED] = Context.USER_SERVICE;
        strArr[WIFI_SUPPL_STATE_FOUR_WAY_HANDSHAKE] = "userfg";
        strArr[WIFI_SUPPL_STATE_GROUP_HANDSHAKE] = "conn";
        HISTORY_EVENT_NAMES = strArr;
        strArr = new String[WIFI_SUPPL_STATE_COMPLETED];
        strArr[WIFI_SUPPL_STATE_INVALID] = "Enl";
        strArr[WIFI_SUPPL_STATE_DISCONNECTED] = "Epr";
        strArr[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = "Efg";
        strArr[WIFI_SUPPL_STATE_INACTIVE] = "Etp";
        strArr[WIFI_SUPPL_STATE_SCANNING] = "Esy";
        strArr[WIFI_SUPPL_STATE_AUTHENTICATING] = "Ewl";
        strArr[WIFI_SUPPL_STATE_ASSOCIATING] = "Ejb";
        strArr[WIFI_SUPPL_STATE_ASSOCIATED] = "Eur";
        strArr[WIFI_SUPPL_STATE_FOUR_WAY_HANDSHAKE] = "Euf";
        strArr[WIFI_SUPPL_STATE_GROUP_HANDSHAKE] = "Ecn";
        HISTORY_EVENT_CHECKIN_NAMES = strArr;
        strArr = new String[WIFI_SUPPL_STATE_FOUR_WAY_HANDSHAKE];
        strArr[WIFI_SUPPL_STATE_INVALID] = Parameters.FLASH_MODE_OFF;
        strArr[WIFI_SUPPL_STATE_DISCONNECTED] = "scanning";
        strArr[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = "no_net";
        strArr[WIFI_SUPPL_STATE_INACTIVE] = "disconn";
        strArr[WIFI_SUPPL_STATE_SCANNING] = "sta";
        strArr[WIFI_SUPPL_STATE_AUTHENTICATING] = "p2p";
        strArr[WIFI_SUPPL_STATE_ASSOCIATING] = "sta_p2p";
        strArr[WIFI_SUPPL_STATE_ASSOCIATED] = "soft_ap";
        WIFI_STATE_NAMES = strArr;
        strArr = new String[WIFI_SUPPL_STATE_SCANNING];
        strArr[WIFI_SUPPL_STATE_INVALID] = "inactive";
        strArr[WIFI_SUPPL_STATE_DISCONNECTED] = "low";
        strArr[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = "med";
        strArr[WIFI_SUPPL_STATE_INACTIVE] = "high";
        BLUETOOTH_STATE_NAMES = strArr;
    }

    private static final void formatTimeRaw(StringBuilder out, long seconds) {
        long days = seconds / 86400;
        if (days != 0) {
            out.append(days);
            out.append("d ");
        }
        long used = ((60 * days) * 60) * 24;
        long hours = (seconds - used) / 3600;
        if (!(hours == 0 && used == 0)) {
            out.append(hours);
            out.append("h ");
        }
        used += (60 * hours) * 60;
        long mins = (seconds - used) / 60;
        if (!(mins == 0 && used == 0)) {
            out.append(mins);
            out.append("m ");
        }
        used += 60 * mins;
        if (seconds != 0 || used != 0) {
            out.append(seconds - used);
            out.append("s ");
        }
    }

    public static final void formatTime(StringBuilder sb, long time) {
        long sec = time / 100;
        formatTimeRaw(sb, sec);
        sb.append((time - (100 * sec)) * 10);
        sb.append("ms ");
    }

    public static final void formatTimeMs(StringBuilder sb, long time) {
        long sec = time / 1000;
        formatTimeRaw(sb, sec);
        sb.append(time - (1000 * sec));
        sb.append("ms ");
    }

    public static final void formatTimeMsNoSpace(StringBuilder sb, long time) {
        long sec = time / 1000;
        formatTimeRaw(sb, sec);
        sb.append(time - (1000 * sec));
        sb.append("ms");
    }

    public final String formatRatioLocked(long num, long den) {
        if (den == 0) {
            return "--%";
        }
        float perc = (((float) num) / ((float) den)) * SensorManager.LIGHT_CLOUDY;
        this.mFormatBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
        Object[] objArr = new Object[WIFI_SUPPL_STATE_DISCONNECTED];
        objArr[WIFI_SUPPL_STATE_INVALID] = Float.valueOf(perc);
        this.mFormatter.format("%.1f%%", objArr);
        return this.mFormatBuilder.toString();
    }

    final String formatBytesLocked(long bytes) {
        this.mFormatBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
        if (bytes < BYTES_PER_KB) {
            return bytes + "B";
        }
        Object[] objArr;
        if (bytes < BYTES_PER_MB) {
            objArr = new Object[WIFI_SUPPL_STATE_DISCONNECTED];
            objArr[WIFI_SUPPL_STATE_INVALID] = Double.valueOf(((double) bytes) / 1024.0d);
            this.mFormatter.format("%.2fKB", objArr);
            return this.mFormatBuilder.toString();
        } else if (bytes < BYTES_PER_GB) {
            objArr = new Object[WIFI_SUPPL_STATE_DISCONNECTED];
            objArr[WIFI_SUPPL_STATE_INVALID] = Double.valueOf(((double) bytes) / 1048576.0d);
            this.mFormatter.format("%.2fMB", objArr);
            return this.mFormatBuilder.toString();
        } else {
            objArr = new Object[WIFI_SUPPL_STATE_DISCONNECTED];
            objArr[WIFI_SUPPL_STATE_INVALID] = Double.valueOf(((double) bytes) / 1.073741824E9d);
            this.mFormatter.format("%.2fGB", objArr);
            return this.mFormatBuilder.toString();
        }
    }

    private static long computeWakeLock(Timer timer, long elapsedRealtimeUs, int which) {
        if (timer != null) {
            return (500 + timer.getTotalTimeLocked(elapsedRealtimeUs, which)) / 1000;
        }
        return 0;
    }

    private static final String printWakeLock(StringBuilder sb, Timer timer, long elapsedRealtimeUs, String name, int which, String linePrefix) {
        if (timer == null) {
            return linePrefix;
        }
        long totalTimeMillis = computeWakeLock(timer, elapsedRealtimeUs, which);
        int count = timer.getCountLocked(which);
        if (totalTimeMillis == 0) {
            return linePrefix;
        }
        sb.append(linePrefix);
        formatTimeMs(sb, totalTimeMillis);
        if (name != null) {
            sb.append(name);
            sb.append(' ');
        }
        sb.append('(');
        sb.append(count);
        sb.append(" times)");
        return ", ";
    }

    private static final String printWakeLockCheckin(StringBuilder sb, Timer timer, long elapsedRealtimeUs, String name, int which, String linePrefix) {
        long totalTimeMicros = 0;
        int count = WIFI_SUPPL_STATE_INVALID;
        if (timer != null) {
            totalTimeMicros = timer.getTotalTimeLocked(elapsedRealtimeUs, which);
            count = timer.getCountLocked(which);
        }
        sb.append(linePrefix);
        sb.append((500 + totalTimeMicros) / 1000);
        sb.append(PhoneNumberUtils.PAUSE);
        sb.append(name != null ? name + "," : ProxyInfo.LOCAL_EXCL_LIST);
        sb.append(count);
        return ",";
    }

    private static final void dumpLine(PrintWriter pw, int uid, String category, String type, Object... args) {
        pw.print(WIFI_SUPPL_STATE_GROUP_HANDSHAKE);
        pw.print(PhoneNumberUtils.PAUSE);
        pw.print(uid);
        pw.print(PhoneNumberUtils.PAUSE);
        pw.print(category);
        pw.print(PhoneNumberUtils.PAUSE);
        pw.print(type);
        Object[] arr$ = args;
        int len$ = arr$.length;
        for (int i$ = WIFI_SUPPL_STATE_INVALID; i$ < len$; i$ += WIFI_SUPPL_STATE_DISCONNECTED) {
            Object arg = arr$[i$];
            pw.print(PhoneNumberUtils.PAUSE);
            pw.print(arg);
        }
        pw.println();
    }

    public final void dumpCheckinLocked(Context context, PrintWriter pw, int which, int reqUid) {
        dumpCheckinLocked(context, pw, which, reqUid, BatteryStatsHelper.checkWifiOnly(context));
    }

    public final void dumpCheckinLocked(Context context, PrintWriter pw, int which, int reqUid, boolean wifiOnly) {
        int iu;
        int i;
        int uid;
        long rawUptime = SystemClock.uptimeMillis() * 1000;
        long rawRealtime = SystemClock.elapsedRealtime() * 1000;
        long batteryUptime = getBatteryUptime(rawUptime);
        long whichBatteryUptime = computeBatteryUptime(rawUptime, which);
        long whichBatteryRealtime = computeBatteryRealtime(rawRealtime, which);
        long whichBatteryScreenOffUptime = computeBatteryScreenOffUptime(rawUptime, which);
        long whichBatteryScreenOffRealtime = computeBatteryScreenOffRealtime(rawRealtime, which);
        long totalRealtime = computeRealtime(rawRealtime, which);
        long totalUptime = computeUptime(rawUptime, which);
        long screenOnTime = getScreenOnTime(rawRealtime, which);
        long interactiveTime = getInteractiveTime(rawRealtime, which);
        long lowPowerModeEnabledTime = getLowPowerModeEnabledTime(rawRealtime, which);
        int connChanges = getNumConnectivityChange(which);
        long phoneOnTime = getPhoneOnTime(rawRealtime, which);
        long wifiOnTime = getWifiOnTime(rawRealtime, which);
        long wifiRunningTime = getGlobalWifiRunningTime(rawRealtime, which);
        long bluetoothOnTime = getBluetoothOnTime(rawRealtime, which);
        StringBuilder sb = new StringBuilder(AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS);
        SparseArray<? extends Uid> uidStats = getUidStats();
        int NU = uidStats.size();
        String category = STAT_NAMES[which];
        String str = BATTERY_DATA;
        Integer[] numArr = new Object[WIFI_SUPPL_STATE_FOUR_WAY_HANDSHAKE];
        numArr[WIFI_SUPPL_STATE_INVALID] = which == 0 ? Integer.valueOf(getStartCount()) : "N/A";
        numArr[WIFI_SUPPL_STATE_DISCONNECTED] = Long.valueOf(whichBatteryRealtime / 1000);
        numArr[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = Long.valueOf(whichBatteryUptime / 1000);
        numArr[WIFI_SUPPL_STATE_INACTIVE] = Long.valueOf(totalRealtime / 1000);
        numArr[WIFI_SUPPL_STATE_SCANNING] = Long.valueOf(totalUptime / 1000);
        numArr[WIFI_SUPPL_STATE_AUTHENTICATING] = Long.valueOf(getStartClockTime());
        numArr[WIFI_SUPPL_STATE_ASSOCIATING] = Long.valueOf(whichBatteryScreenOffRealtime / 1000);
        numArr[WIFI_SUPPL_STATE_ASSOCIATED] = Long.valueOf(whichBatteryScreenOffUptime / 1000);
        dumpLine(pw, WIFI_SUPPL_STATE_INVALID, category, str, numArr);
        long fullWakeLockTimeTotal = 0;
        long partialWakeLockTimeTotal = 0;
        for (iu = WIFI_SUPPL_STATE_INVALID; iu < NU; iu += WIFI_SUPPL_STATE_DISCONNECTED) {
            Map<String, ? extends Wakelock> wakelocks = ((Uid) uidStats.valueAt(iu)).getWakelockStats();
            if (wakelocks.size() > 0) {
                for (Entry<String, ? extends Wakelock> ent : wakelocks.entrySet()) {
                    Wakelock wl = (Wakelock) ent.getValue();
                    Timer fullWakeTimer = wl.getWakeTime(WIFI_SUPPL_STATE_DISCONNECTED);
                    if (fullWakeTimer != null) {
                        fullWakeLockTimeTotal += fullWakeTimer.getTotalTimeLocked(rawRealtime, which);
                    }
                    Timer partialWakeTimer = wl.getWakeTime(WIFI_SUPPL_STATE_INVALID);
                    if (partialWakeTimer != null) {
                        partialWakeLockTimeTotal += partialWakeTimer.getTotalTimeLocked(rawRealtime, which);
                    }
                }
            }
        }
        long mobileRxTotalBytes = getNetworkActivityBytes(WIFI_SUPPL_STATE_INVALID, which);
        long mobileTxTotalBytes = getNetworkActivityBytes(WIFI_SUPPL_STATE_DISCONNECTED, which);
        long wifiRxTotalBytes = getNetworkActivityBytes(WIFI_SUPPL_STATE_INTERFACE_DISABLED, which);
        long wifiTxTotalBytes = getNetworkActivityBytes(WIFI_SUPPL_STATE_INACTIVE, which);
        long mobileRxTotalPackets = getNetworkActivityPackets(WIFI_SUPPL_STATE_INVALID, which);
        long mobileTxTotalPackets = getNetworkActivityPackets(WIFI_SUPPL_STATE_DISCONNECTED, which);
        long wifiRxTotalPackets = getNetworkActivityPackets(WIFI_SUPPL_STATE_INTERFACE_DISABLED, which);
        long wifiTxTotalPackets = getNetworkActivityPackets(WIFI_SUPPL_STATE_INACTIVE, which);
        String str2 = GLOBAL_NETWORK_DATA;
        Object[] objArr = new Object[WIFI_SUPPL_STATE_FOUR_WAY_HANDSHAKE];
        objArr[WIFI_SUPPL_STATE_INVALID] = Long.valueOf(mobileRxTotalBytes);
        objArr[WIFI_SUPPL_STATE_DISCONNECTED] = Long.valueOf(mobileTxTotalBytes);
        objArr[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = Long.valueOf(wifiRxTotalBytes);
        objArr[WIFI_SUPPL_STATE_INACTIVE] = Long.valueOf(wifiTxTotalBytes);
        objArr[WIFI_SUPPL_STATE_SCANNING] = Long.valueOf(mobileRxTotalPackets);
        objArr[WIFI_SUPPL_STATE_AUTHENTICATING] = Long.valueOf(mobileTxTotalPackets);
        objArr[WIFI_SUPPL_STATE_ASSOCIATING] = Long.valueOf(wifiRxTotalPackets);
        objArr[WIFI_SUPPL_STATE_ASSOCIATED] = Long.valueOf(wifiTxTotalPackets);
        dumpLine(pw, WIFI_SUPPL_STATE_INVALID, category, str2, objArr);
        str2 = MISC_DATA;
        objArr = new Object[NUM_DATA_CONNECTION_TYPES];
        objArr[WIFI_SUPPL_STATE_INVALID] = Long.valueOf(screenOnTime / 1000);
        objArr[WIFI_SUPPL_STATE_DISCONNECTED] = Long.valueOf(phoneOnTime / 1000);
        objArr[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = Long.valueOf(wifiOnTime / 1000);
        objArr[WIFI_SUPPL_STATE_INACTIVE] = Long.valueOf(wifiRunningTime / 1000);
        objArr[WIFI_SUPPL_STATE_SCANNING] = Long.valueOf(bluetoothOnTime / 1000);
        objArr[WIFI_SUPPL_STATE_AUTHENTICATING] = Long.valueOf(mobileRxTotalBytes);
        objArr[WIFI_SUPPL_STATE_ASSOCIATING] = Long.valueOf(mobileTxTotalBytes);
        objArr[WIFI_SUPPL_STATE_ASSOCIATED] = Long.valueOf(wifiRxTotalBytes);
        objArr[WIFI_SUPPL_STATE_FOUR_WAY_HANDSHAKE] = Long.valueOf(wifiTxTotalBytes);
        objArr[WIFI_SUPPL_STATE_GROUP_HANDSHAKE] = Long.valueOf(fullWakeLockTimeTotal / 1000);
        objArr[WIFI_SUPPL_STATE_COMPLETED] = Long.valueOf(partialWakeLockTimeTotal / 1000);
        objArr[WIFI_SUPPL_STATE_DORMANT] = Integer.valueOf(WIFI_SUPPL_STATE_INVALID);
        objArr[WIFI_SUPPL_STATE_UNINITIALIZED] = Long.valueOf(getMobileRadioActiveTime(rawRealtime, which) / 1000);
        objArr[SYNC] = Long.valueOf(getMobileRadioActiveAdjustedTime(which) / 1000);
        objArr[JOB] = Long.valueOf(interactiveTime / 1000);
        objArr[DATA_CONNECTION_HSPAP] = Long.valueOf(lowPowerModeEnabledTime / 1000);
        objArr[DUMP_VERBOSE] = Integer.valueOf(connChanges);
        dumpLine(pw, WIFI_SUPPL_STATE_INVALID, category, str2, objArr);
        Object[] args = new Object[WIFI_SUPPL_STATE_AUTHENTICATING];
        for (i = WIFI_SUPPL_STATE_INVALID; i < WIFI_SUPPL_STATE_AUTHENTICATING; i += WIFI_SUPPL_STATE_DISCONNECTED) {
            args[i] = Long.valueOf(getScreenBrightnessTime(i, rawRealtime, which) / 1000);
        }
        dumpLine(pw, WIFI_SUPPL_STATE_INVALID, category, SCREEN_BRIGHTNESS_DATA, args);
        args = new Object[WIFI_SUPPL_STATE_AUTHENTICATING];
        for (i = WIFI_SUPPL_STATE_INVALID; i < WIFI_SUPPL_STATE_AUTHENTICATING; i += WIFI_SUPPL_STATE_DISCONNECTED) {
            args[i] = Long.valueOf(getPhoneSignalStrengthTime(i, rawRealtime, which) / 1000);
        }
        dumpLine(pw, WIFI_SUPPL_STATE_INVALID, category, SIGNAL_STRENGTH_TIME_DATA, args);
        str2 = SIGNAL_SCANNING_TIME_DATA;
        objArr = new Object[WIFI_SUPPL_STATE_DISCONNECTED];
        objArr[WIFI_SUPPL_STATE_INVALID] = Long.valueOf(getPhoneSignalScanningTime(rawRealtime, which) / 1000);
        dumpLine(pw, WIFI_SUPPL_STATE_INVALID, category, str2, objArr);
        for (i = WIFI_SUPPL_STATE_INVALID; i < WIFI_SUPPL_STATE_AUTHENTICATING; i += WIFI_SUPPL_STATE_DISCONNECTED) {
            args[i] = Integer.valueOf(getPhoneSignalStrengthCount(i, which));
        }
        dumpLine(pw, WIFI_SUPPL_STATE_INVALID, category, SIGNAL_STRENGTH_COUNT_DATA, args);
        args = new Object[NUM_DATA_CONNECTION_TYPES];
        for (i = WIFI_SUPPL_STATE_INVALID; i < NUM_DATA_CONNECTION_TYPES; i += WIFI_SUPPL_STATE_DISCONNECTED) {
            args[i] = Long.valueOf(getPhoneDataConnectionTime(i, rawRealtime, which) / 1000);
        }
        dumpLine(pw, WIFI_SUPPL_STATE_INVALID, category, DATA_CONNECTION_TIME_DATA, args);
        for (i = WIFI_SUPPL_STATE_INVALID; i < NUM_DATA_CONNECTION_TYPES; i += WIFI_SUPPL_STATE_DISCONNECTED) {
            args[i] = Integer.valueOf(getPhoneDataConnectionCount(i, which));
        }
        dumpLine(pw, WIFI_SUPPL_STATE_INVALID, category, DATA_CONNECTION_COUNT_DATA, args);
        args = new Object[WIFI_SUPPL_STATE_FOUR_WAY_HANDSHAKE];
        for (i = WIFI_SUPPL_STATE_INVALID; i < WIFI_SUPPL_STATE_FOUR_WAY_HANDSHAKE; i += WIFI_SUPPL_STATE_DISCONNECTED) {
            args[i] = Long.valueOf(getWifiStateTime(i, rawRealtime, which) / 1000);
        }
        dumpLine(pw, WIFI_SUPPL_STATE_INVALID, category, WIFI_STATE_TIME_DATA, args);
        for (i = WIFI_SUPPL_STATE_INVALID; i < WIFI_SUPPL_STATE_FOUR_WAY_HANDSHAKE; i += WIFI_SUPPL_STATE_DISCONNECTED) {
            args[i] = Integer.valueOf(getWifiStateCount(i, which));
        }
        dumpLine(pw, WIFI_SUPPL_STATE_INVALID, category, WIFI_STATE_COUNT_DATA, args);
        args = new Object[SYNC];
        for (i = WIFI_SUPPL_STATE_INVALID; i < SYNC; i += WIFI_SUPPL_STATE_DISCONNECTED) {
            args[i] = Long.valueOf(getWifiSupplStateTime(i, rawRealtime, which) / 1000);
        }
        dumpLine(pw, WIFI_SUPPL_STATE_INVALID, category, WIFI_SUPPL_STATE_TIME_DATA, args);
        for (i = WIFI_SUPPL_STATE_INVALID; i < SYNC; i += WIFI_SUPPL_STATE_DISCONNECTED) {
            args[i] = Integer.valueOf(getWifiSupplStateCount(i, which));
        }
        dumpLine(pw, WIFI_SUPPL_STATE_INVALID, category, WIFI_SUPPL_STATE_COUNT_DATA, args);
        args = new Object[WIFI_SUPPL_STATE_AUTHENTICATING];
        for (i = WIFI_SUPPL_STATE_INVALID; i < WIFI_SUPPL_STATE_AUTHENTICATING; i += WIFI_SUPPL_STATE_DISCONNECTED) {
            args[i] = Long.valueOf(getWifiSignalStrengthTime(i, rawRealtime, which) / 1000);
        }
        dumpLine(pw, WIFI_SUPPL_STATE_INVALID, category, WIFI_SIGNAL_STRENGTH_TIME_DATA, args);
        for (i = WIFI_SUPPL_STATE_INVALID; i < WIFI_SUPPL_STATE_AUTHENTICATING; i += WIFI_SUPPL_STATE_DISCONNECTED) {
            args[i] = Integer.valueOf(getWifiSignalStrengthCount(i, which));
        }
        dumpLine(pw, WIFI_SUPPL_STATE_INVALID, category, WIFI_SIGNAL_STRENGTH_COUNT_DATA, args);
        args = new Object[WIFI_SUPPL_STATE_SCANNING];
        for (i = WIFI_SUPPL_STATE_INVALID; i < WIFI_SUPPL_STATE_SCANNING; i += WIFI_SUPPL_STATE_DISCONNECTED) {
            args[i] = Long.valueOf(getBluetoothStateTime(i, rawRealtime, which) / 1000);
        }
        dumpLine(pw, WIFI_SUPPL_STATE_INVALID, category, BLUETOOTH_STATE_TIME_DATA, args);
        for (i = WIFI_SUPPL_STATE_INVALID; i < WIFI_SUPPL_STATE_SCANNING; i += WIFI_SUPPL_STATE_DISCONNECTED) {
            args[i] = Integer.valueOf(getBluetoothStateCount(i, which));
        }
        dumpLine(pw, WIFI_SUPPL_STATE_INVALID, category, BLUETOOTH_STATE_COUNT_DATA, args);
        if (which == WIFI_SUPPL_STATE_INTERFACE_DISABLED) {
            str2 = BATTERY_LEVEL_DATA;
            objArr = new Object[WIFI_SUPPL_STATE_INTERFACE_DISABLED];
            objArr[WIFI_SUPPL_STATE_INVALID] = Integer.valueOf(getDischargeStartLevel());
            objArr[WIFI_SUPPL_STATE_DISCONNECTED] = Integer.valueOf(getDischargeCurrentLevel());
            dumpLine(pw, WIFI_SUPPL_STATE_INVALID, category, str2, objArr);
        }
        if (which == WIFI_SUPPL_STATE_INTERFACE_DISABLED) {
            str2 = BATTERY_DISCHARGE_DATA;
            objArr = new Object[WIFI_SUPPL_STATE_SCANNING];
            objArr[WIFI_SUPPL_STATE_INVALID] = Integer.valueOf(getDischargeStartLevel() - getDischargeCurrentLevel());
            objArr[WIFI_SUPPL_STATE_DISCONNECTED] = Integer.valueOf(getDischargeStartLevel() - getDischargeCurrentLevel());
            objArr[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = Integer.valueOf(getDischargeAmountScreenOn());
            objArr[WIFI_SUPPL_STATE_INACTIVE] = Integer.valueOf(getDischargeAmountScreenOff());
            dumpLine(pw, WIFI_SUPPL_STATE_INVALID, category, str2, objArr);
        } else {
            str2 = BATTERY_DISCHARGE_DATA;
            objArr = new Object[WIFI_SUPPL_STATE_SCANNING];
            objArr[WIFI_SUPPL_STATE_INVALID] = Integer.valueOf(getLowDischargeAmountSinceCharge());
            objArr[WIFI_SUPPL_STATE_DISCONNECTED] = Integer.valueOf(getHighDischargeAmountSinceCharge());
            objArr[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = Integer.valueOf(getDischargeAmountScreenOnSinceCharge());
            objArr[WIFI_SUPPL_STATE_INACTIVE] = Integer.valueOf(getDischargeAmountScreenOffSinceCharge());
            dumpLine(pw, WIFI_SUPPL_STATE_INVALID, category, str2, objArr);
        }
        if (reqUid < 0) {
            Map<String, ? extends Timer> kernelWakelocks = getKernelWakelockStats();
            if (kernelWakelocks.size() > 0) {
                for (Entry<String, ? extends Timer> ent2 : kernelWakelocks.entrySet()) {
                    sb.setLength(WIFI_SUPPL_STATE_INVALID);
                    printWakeLockCheckin(sb, (Timer) ent2.getValue(), rawRealtime, null, which, ProxyInfo.LOCAL_EXCL_LIST);
                    str2 = KERNEL_WAKELOCK_DATA;
                    objArr = new Object[WIFI_SUPPL_STATE_INTERFACE_DISABLED];
                    objArr[WIFI_SUPPL_STATE_INVALID] = ent2.getKey();
                    objArr[WIFI_SUPPL_STATE_DISCONNECTED] = sb.toString();
                    dumpLine(pw, WIFI_SUPPL_STATE_INVALID, category, str2, objArr);
                }
            }
            Map<String, ? extends Timer> wakeupReasons = getWakeupReasonStats();
            if (wakeupReasons.size() > 0) {
                for (Entry<String, ? extends Timer> ent22 : wakeupReasons.entrySet()) {
                    long totalTimeMicros = ((Timer) ent22.getValue()).getTotalTimeLocked(rawRealtime, which);
                    int count = ((Timer) ent22.getValue()).getCountLocked(which);
                    str = WAKEUP_REASON_DATA;
                    String[] strArr = new Object[WIFI_SUPPL_STATE_INACTIVE];
                    String str3 = "\"";
                    strArr[WIFI_SUPPL_STATE_INVALID] = "\"" + ((String) ent22.getKey()) + r154;
                    strArr[WIFI_SUPPL_STATE_DISCONNECTED] = Long.valueOf((500 + totalTimeMicros) / 1000);
                    strArr[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = Integer.valueOf(count);
                    dumpLine(pw, WIFI_SUPPL_STATE_INVALID, category, str, strArr);
                }
            }
        }
        BatteryStatsHelper batteryStatsHelper = new BatteryStatsHelper(context, LOCAL_LOGV, wifiOnly);
        batteryStatsHelper.create(this);
        batteryStatsHelper.refreshStats(which, -1);
        List<BatterySipper> sippers = batteryStatsHelper.getUsageList();
        if (sippers != null && sippers.size() > 0) {
            str2 = POWER_USE_SUMMARY_DATA;
            objArr = new Object[WIFI_SUPPL_STATE_SCANNING];
            objArr[WIFI_SUPPL_STATE_INVALID] = BatteryStatsHelper.makemAh(batteryStatsHelper.getPowerProfile().getBatteryCapacity());
            objArr[WIFI_SUPPL_STATE_DISCONNECTED] = BatteryStatsHelper.makemAh(batteryStatsHelper.getComputedPower());
            objArr[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = BatteryStatsHelper.makemAh(batteryStatsHelper.getMinDrainedPower());
            objArr[WIFI_SUPPL_STATE_INACTIVE] = BatteryStatsHelper.makemAh(batteryStatsHelper.getMaxDrainedPower());
            dumpLine(pw, WIFI_SUPPL_STATE_INVALID, category, str2, objArr);
            for (i = WIFI_SUPPL_STATE_INVALID; i < sippers.size(); i += WIFI_SUPPL_STATE_DISCONNECTED) {
                String label;
                BatterySipper bs = (BatterySipper) sippers.get(i);
                uid = WIFI_SUPPL_STATE_INVALID;
                switch (AnonymousClass2.$SwitchMap$com$android$internal$os$BatterySipper$DrainType[bs.drainType.ordinal()]) {
                    case WIFI_SUPPL_STATE_DISCONNECTED /*1*/:
                        label = "idle";
                        break;
                    case WIFI_SUPPL_STATE_INTERFACE_DISABLED /*2*/:
                        label = System.RADIO_CELL;
                        break;
                    case WIFI_SUPPL_STATE_INACTIVE /*3*/:
                        label = Insert.PHONE;
                        break;
                    case WIFI_SUPPL_STATE_SCANNING /*4*/:
                        label = System.RADIO_WIFI;
                        break;
                    case WIFI_SUPPL_STATE_AUTHENTICATING /*5*/:
                        label = "blue";
                        break;
                    case WIFI_SUPPL_STATE_ASSOCIATING /*6*/:
                        label = "scrn";
                        break;
                    case WIFI_SUPPL_STATE_ASSOCIATED /*7*/:
                        label = "flashlight";
                        break;
                    case WIFI_SUPPL_STATE_FOUR_WAY_HANDSHAKE /*8*/:
                        uid = bs.uidObj.getUid();
                        label = UID_DATA;
                        break;
                    case WIFI_SUPPL_STATE_GROUP_HANDSHAKE /*9*/:
                        uid = UserHandle.getUid(bs.userId, WIFI_SUPPL_STATE_INVALID);
                        label = Context.USER_SERVICE;
                        break;
                    case WIFI_SUPPL_STATE_COMPLETED /*10*/:
                        label = "unacc";
                        break;
                    case WIFI_SUPPL_STATE_DORMANT /*11*/:
                        label = "over";
                        break;
                    default:
                        label = "???";
                        break;
                }
                String str4 = POWER_USE_ITEM_DATA;
                Object[] objArr2 = new Object[WIFI_SUPPL_STATE_INTERFACE_DISABLED];
                objArr2[WIFI_SUPPL_STATE_INVALID] = label;
                objArr2[WIFI_SUPPL_STATE_DISCONNECTED] = BatteryStatsHelper.makemAh(bs.value);
                dumpLine(pw, uid, category, str4, objArr2);
            }
        }
        for (iu = WIFI_SUPPL_STATE_INVALID; iu < NU; iu += WIFI_SUPPL_STATE_DISCONNECTED) {
            uid = uidStats.keyAt(iu);
            if (reqUid < 0 || uid == reqUid) {
                Timer timer;
                long totalTime;
                int starts;
                Uid u = (Uid) uidStats.valueAt(iu);
                long mobileBytesRx = u.getNetworkActivityBytes(WIFI_SUPPL_STATE_INVALID, which);
                long mobileBytesTx = u.getNetworkActivityBytes(WIFI_SUPPL_STATE_DISCONNECTED, which);
                long wifiBytesRx = u.getNetworkActivityBytes(WIFI_SUPPL_STATE_INTERFACE_DISABLED, which);
                long wifiBytesTx = u.getNetworkActivityBytes(WIFI_SUPPL_STATE_INACTIVE, which);
                long mobilePacketsRx = u.getNetworkActivityPackets(WIFI_SUPPL_STATE_INVALID, which);
                long mobilePacketsTx = u.getNetworkActivityPackets(WIFI_SUPPL_STATE_DISCONNECTED, which);
                long mobileActiveTime = u.getMobileRadioActiveTime(which);
                int mobileActiveCount = u.getMobileRadioActiveCount(which);
                long wifiPacketsRx = u.getNetworkActivityPackets(WIFI_SUPPL_STATE_INTERFACE_DISABLED, which);
                long wifiPacketsTx = u.getNetworkActivityPackets(WIFI_SUPPL_STATE_INACTIVE, which);
                long fullWifiLockOnTime = u.getFullWifiLockTime(rawRealtime, which);
                long wifiScanTime = u.getWifiScanTime(rawRealtime, which);
                long uidWifiRunningTime = u.getWifiRunningTime(rawRealtime, which);
                if (mobileBytesRx > 0 || mobileBytesTx > 0 || wifiBytesRx > 0 || wifiBytesTx > 0 || mobilePacketsRx > 0 || mobilePacketsTx > 0 || wifiPacketsRx > 0 || wifiPacketsTx > 0 || mobileActiveTime > 0 || mobileActiveCount > 0) {
                    str4 = NETWORK_DATA;
                    objArr2 = new Object[WIFI_SUPPL_STATE_COMPLETED];
                    objArr2[WIFI_SUPPL_STATE_INVALID] = Long.valueOf(mobileBytesRx);
                    objArr2[WIFI_SUPPL_STATE_DISCONNECTED] = Long.valueOf(mobileBytesTx);
                    objArr2[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = Long.valueOf(wifiBytesRx);
                    objArr2[WIFI_SUPPL_STATE_INACTIVE] = Long.valueOf(wifiBytesTx);
                    objArr2[WIFI_SUPPL_STATE_SCANNING] = Long.valueOf(mobilePacketsRx);
                    objArr2[WIFI_SUPPL_STATE_AUTHENTICATING] = Long.valueOf(mobilePacketsTx);
                    objArr2[WIFI_SUPPL_STATE_ASSOCIATING] = Long.valueOf(wifiPacketsRx);
                    objArr2[WIFI_SUPPL_STATE_ASSOCIATED] = Long.valueOf(wifiPacketsTx);
                    objArr2[WIFI_SUPPL_STATE_FOUR_WAY_HANDSHAKE] = Long.valueOf(mobileActiveTime);
                    objArr2[WIFI_SUPPL_STATE_GROUP_HANDSHAKE] = Integer.valueOf(mobileActiveCount);
                    dumpLine(pw, uid, category, str4, objArr2);
                }
                if (!(fullWifiLockOnTime == 0 && wifiScanTime == 0 && uidWifiRunningTime == 0)) {
                    str4 = WIFI_DATA;
                    objArr2 = new Object[WIFI_SUPPL_STATE_INACTIVE];
                    objArr2[WIFI_SUPPL_STATE_INVALID] = Long.valueOf(fullWifiLockOnTime);
                    objArr2[WIFI_SUPPL_STATE_DISCONNECTED] = Long.valueOf(wifiScanTime);
                    objArr2[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = Long.valueOf(uidWifiRunningTime);
                    dumpLine(pw, uid, category, str4, objArr2);
                }
                if (u.hasUserActivity()) {
                    args = new Object[WIFI_SUPPL_STATE_INACTIVE];
                    boolean hasData = LOCAL_LOGV;
                    for (i = WIFI_SUPPL_STATE_INVALID; i < WIFI_SUPPL_STATE_INACTIVE; i += WIFI_SUPPL_STATE_DISCONNECTED) {
                        int val = u.getUserActivityCount(i, which);
                        args[i] = Integer.valueOf(val);
                        if (val != 0) {
                            hasData = true;
                        }
                    }
                    if (hasData) {
                        dumpLine(pw, uid, category, USER_ACTIVITY_DATA, args);
                    }
                }
                wakelocks = u.getWakelockStats();
                if (wakelocks.size() > 0) {
                    for (Entry<String, ? extends Wakelock> ent3 : wakelocks.entrySet()) {
                        wl = (Wakelock) ent3.getValue();
                        String linePrefix = ProxyInfo.LOCAL_EXCL_LIST;
                        sb.setLength(WIFI_SUPPL_STATE_INVALID);
                        linePrefix = printWakeLockCheckin(sb, wl.getWakeTime(WIFI_SUPPL_STATE_INTERFACE_DISABLED), rawRealtime, "w", which, printWakeLockCheckin(sb, wl.getWakeTime(WIFI_SUPPL_STATE_INVALID), rawRealtime, "p", which, printWakeLockCheckin(sb, wl.getWakeTime(WIFI_SUPPL_STATE_DISCONNECTED), rawRealtime, FullBackup.DATA_TREE_TOKEN, which, linePrefix)));
                        if (sb.length() > 0) {
                            String name = (String) ent3.getKey();
                            if (name.indexOf(44) >= 0) {
                                name = name.replace(PhoneNumberUtils.PAUSE, '_');
                            }
                            str4 = WAKELOCK_DATA;
                            objArr2 = new Object[WIFI_SUPPL_STATE_INTERFACE_DISABLED];
                            objArr2[WIFI_SUPPL_STATE_INVALID] = name;
                            objArr2[WIFI_SUPPL_STATE_DISCONNECTED] = sb.toString();
                            dumpLine(pw, uid, category, str4, objArr2);
                        }
                    }
                }
                Map<String, ? extends Timer> syncs = u.getSyncStats();
                if (syncs.size() > 0) {
                    for (Entry<String, ? extends Timer> ent222 : syncs.entrySet()) {
                        timer = (Timer) ent222.getValue();
                        totalTime = (timer.getTotalTimeLocked(rawRealtime, which) + 500) / 1000;
                        count = timer.getCountLocked(which);
                        if (totalTime != 0) {
                            str4 = SYNC_DATA;
                            objArr2 = new Object[WIFI_SUPPL_STATE_INACTIVE];
                            objArr2[WIFI_SUPPL_STATE_INVALID] = ent222.getKey();
                            objArr2[WIFI_SUPPL_STATE_DISCONNECTED] = Long.valueOf(totalTime);
                            objArr2[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = Integer.valueOf(count);
                            dumpLine(pw, uid, category, str4, objArr2);
                        }
                    }
                }
                Map<String, ? extends Timer> jobs = u.getJobStats();
                if (jobs.size() > 0) {
                    for (Entry<String, ? extends Timer> ent2222 : jobs.entrySet()) {
                        timer = (Timer) ent2222.getValue();
                        totalTime = (timer.getTotalTimeLocked(rawRealtime, which) + 500) / 1000;
                        count = timer.getCountLocked(which);
                        if (totalTime != 0) {
                            str4 = JOB_DATA;
                            objArr2 = new Object[WIFI_SUPPL_STATE_INACTIVE];
                            objArr2[WIFI_SUPPL_STATE_INVALID] = ent2222.getKey();
                            objArr2[WIFI_SUPPL_STATE_DISCONNECTED] = Long.valueOf(totalTime);
                            objArr2[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = Integer.valueOf(count);
                            dumpLine(pw, uid, category, str4, objArr2);
                        }
                    }
                }
                SparseArray<? extends Sensor> sensors = u.getSensorStats();
                int NSE = sensors.size();
                for (int ise = WIFI_SUPPL_STATE_INVALID; ise < NSE; ise += WIFI_SUPPL_STATE_DISCONNECTED) {
                    Sensor se = (Sensor) sensors.valueAt(ise);
                    int sensorNumber = sensors.keyAt(ise);
                    timer = se.getSensorTime();
                    if (timer != null) {
                        totalTime = (timer.getTotalTimeLocked(rawRealtime, which) + 500) / 1000;
                        count = timer.getCountLocked(which);
                        if (totalTime != 0) {
                            str4 = SENSOR_DATA;
                            objArr2 = new Object[WIFI_SUPPL_STATE_INACTIVE];
                            objArr2[WIFI_SUPPL_STATE_INVALID] = Integer.valueOf(sensorNumber);
                            objArr2[WIFI_SUPPL_STATE_DISCONNECTED] = Long.valueOf(totalTime);
                            objArr2[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = Integer.valueOf(count);
                            dumpLine(pw, uid, category, str4, objArr2);
                        }
                    }
                }
                Timer vibTimer = u.getVibratorOnTimer();
                if (vibTimer != null) {
                    totalTime = (vibTimer.getTotalTimeLocked(rawRealtime, which) + 500) / 1000;
                    count = vibTimer.getCountLocked(which);
                    if (totalTime != 0) {
                        str4 = VIBRATOR_DATA;
                        objArr2 = new Object[WIFI_SUPPL_STATE_INTERFACE_DISABLED];
                        objArr2[WIFI_SUPPL_STATE_INVALID] = Long.valueOf(totalTime);
                        objArr2[WIFI_SUPPL_STATE_DISCONNECTED] = Integer.valueOf(count);
                        dumpLine(pw, uid, category, str4, objArr2);
                    }
                }
                Timer fgTimer = u.getForegroundActivityTimer();
                if (fgTimer != null) {
                    totalTime = (fgTimer.getTotalTimeLocked(rawRealtime, which) + 500) / 1000;
                    count = fgTimer.getCountLocked(which);
                    if (totalTime != 0) {
                        str4 = FOREGROUND_DATA;
                        objArr2 = new Object[WIFI_SUPPL_STATE_INTERFACE_DISABLED];
                        objArr2[WIFI_SUPPL_STATE_INVALID] = Long.valueOf(totalTime);
                        objArr2[WIFI_SUPPL_STATE_DISCONNECTED] = Integer.valueOf(count);
                        dumpLine(pw, uid, category, str4, objArr2);
                    }
                }
                Object[] stateTimes = new Object[WIFI_SUPPL_STATE_INACTIVE];
                long totalStateTime = 0;
                for (int ips = WIFI_SUPPL_STATE_INVALID; ips < WIFI_SUPPL_STATE_INACTIVE; ips += WIFI_SUPPL_STATE_DISCONNECTED) {
                    totalStateTime += u.getProcessStateTime(ips, rawRealtime, which);
                    stateTimes[ips] = Long.valueOf((500 + totalStateTime) / 1000);
                }
                if (totalStateTime > 0) {
                    dumpLine(pw, uid, category, STATE_TIME_DATA, stateTimes);
                }
                Map<String, ? extends Proc> processStats = u.getProcessStats();
                if (processStats.size() > 0) {
                    for (Entry<String, ? extends Proc> ent4 : processStats.entrySet()) {
                        Proc ps = (Proc) ent4.getValue();
                        long userMillis = ps.getUserTime(which) * 10;
                        long systemMillis = ps.getSystemTime(which) * 10;
                        long foregroundMillis = ps.getForegroundTime(which) * 10;
                        starts = ps.getStarts(which);
                        int numCrashes = ps.getNumCrashes(which);
                        int numAnrs = ps.getNumAnrs(which);
                        if (userMillis != 0 || systemMillis != 0 || foregroundMillis != 0 || starts != 0 || numAnrs != 0 || numCrashes != 0) {
                            str4 = PROCESS_DATA;
                            objArr2 = new Object[WIFI_SUPPL_STATE_ASSOCIATED];
                            objArr2[WIFI_SUPPL_STATE_INVALID] = ent4.getKey();
                            objArr2[WIFI_SUPPL_STATE_DISCONNECTED] = Long.valueOf(userMillis);
                            objArr2[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = Long.valueOf(systemMillis);
                            objArr2[WIFI_SUPPL_STATE_INACTIVE] = Long.valueOf(foregroundMillis);
                            objArr2[WIFI_SUPPL_STATE_SCANNING] = Integer.valueOf(starts);
                            objArr2[WIFI_SUPPL_STATE_AUTHENTICATING] = Integer.valueOf(numAnrs);
                            objArr2[WIFI_SUPPL_STATE_ASSOCIATING] = Integer.valueOf(numCrashes);
                            dumpLine(pw, uid, category, str4, objArr2);
                        }
                    }
                }
                Map<String, ? extends Pkg> packageStats = u.getPackageStats();
                if (packageStats.size() > 0) {
                    for (Entry<String, ? extends Pkg> ent5 : packageStats.entrySet()) {
                        Pkg ps2 = (Pkg) ent5.getValue();
                        int wakeups = ps2.getWakeups(which);
                        for (Entry<String, ? extends Serv> sent : ps2.getServiceStats().entrySet()) {
                            Serv ss = (Serv) sent.getValue();
                            long startTime = ss.getStartTime(batteryUptime, which);
                            starts = ss.getStarts(which);
                            int launches = ss.getLaunches(which);
                            if (startTime != 0 || starts != 0 || launches != 0) {
                                str4 = APK_DATA;
                                objArr2 = new Object[WIFI_SUPPL_STATE_ASSOCIATING];
                                objArr2[WIFI_SUPPL_STATE_INVALID] = Integer.valueOf(wakeups);
                                objArr2[WIFI_SUPPL_STATE_DISCONNECTED] = ent5.getKey();
                                objArr2[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = sent.getKey();
                                objArr2[WIFI_SUPPL_STATE_INACTIVE] = Long.valueOf(startTime / 1000);
                                objArr2[WIFI_SUPPL_STATE_SCANNING] = Integer.valueOf(starts);
                                objArr2[WIFI_SUPPL_STATE_AUTHENTICATING] = Integer.valueOf(launches);
                                dumpLine(pw, uid, category, str4, objArr2);
                            }
                        }
                    }
                }
            }
        }
    }

    private void printmAh(PrintWriter printer, double power) {
        printer.print(BatteryStatsHelper.makemAh(power));
    }

    public final void dumpLocked(Context context, PrintWriter pw, String prefix, int which, int reqUid) {
        dumpLocked(context, pw, prefix, which, reqUid, BatteryStatsHelper.checkWifiOnly(context));
    }

    public final void dumpLocked(Context context, PrintWriter pw, String prefix, int which, int reqUid, boolean wifiOnly) {
        int i;
        int iu;
        BatterySipper bs;
        long totalTime;
        Timer timer;
        long rawUptime = SystemClock.uptimeMillis() * 1000;
        long rawRealtime = SystemClock.elapsedRealtime() * 1000;
        long batteryUptime = getBatteryUptime(rawUptime);
        long whichBatteryUptime = computeBatteryUptime(rawUptime, which);
        long whichBatteryRealtime = computeBatteryRealtime(rawRealtime, which);
        long totalRealtime = computeRealtime(rawRealtime, which);
        long totalUptime = computeUptime(rawUptime, which);
        long whichBatteryScreenOffUptime = computeBatteryScreenOffUptime(rawUptime, which);
        long whichBatteryScreenOffRealtime = computeBatteryScreenOffRealtime(rawRealtime, which);
        long batteryTimeRemaining = computeBatteryTimeRemaining(rawRealtime);
        long chargeTimeRemaining = computeChargeTimeRemaining(rawRealtime);
        StringBuilder stringBuilder = new StringBuilder(AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS);
        SparseArray<? extends Uid> uidStats = getUidStats();
        int NU = uidStats.size();
        stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
        stringBuilder.append(prefix);
        stringBuilder.append("  Time on battery: ");
        formatTimeMs(stringBuilder, whichBatteryRealtime / 1000);
        stringBuilder.append("(");
        stringBuilder.append(formatRatioLocked(whichBatteryRealtime, totalRealtime));
        stringBuilder.append(") realtime, ");
        formatTimeMs(stringBuilder, whichBatteryUptime / 1000);
        stringBuilder.append("(");
        stringBuilder.append(formatRatioLocked(whichBatteryUptime, totalRealtime));
        stringBuilder.append(") uptime");
        pw.println(stringBuilder.toString());
        stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
        stringBuilder.append(prefix);
        stringBuilder.append("  Time on battery screen off: ");
        formatTimeMs(stringBuilder, whichBatteryScreenOffRealtime / 1000);
        stringBuilder.append("(");
        stringBuilder.append(formatRatioLocked(whichBatteryScreenOffRealtime, totalRealtime));
        stringBuilder.append(") realtime, ");
        formatTimeMs(stringBuilder, whichBatteryScreenOffUptime / 1000);
        stringBuilder.append("(");
        stringBuilder.append(formatRatioLocked(whichBatteryScreenOffUptime, totalRealtime));
        stringBuilder.append(") uptime");
        pw.println(stringBuilder.toString());
        stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
        stringBuilder.append(prefix);
        stringBuilder.append("  Total run time: ");
        formatTimeMs(stringBuilder, totalRealtime / 1000);
        stringBuilder.append("realtime, ");
        formatTimeMs(stringBuilder, totalUptime / 1000);
        stringBuilder.append("uptime");
        pw.println(stringBuilder.toString());
        if (batteryTimeRemaining >= 0) {
            stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
            stringBuilder.append(prefix);
            stringBuilder.append("  Battery time remaining: ");
            formatTimeMs(stringBuilder, batteryTimeRemaining / 1000);
            pw.println(stringBuilder.toString());
        }
        if (chargeTimeRemaining >= 0) {
            stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
            stringBuilder.append(prefix);
            stringBuilder.append("  Charge time remaining: ");
            formatTimeMs(stringBuilder, chargeTimeRemaining / 1000);
            pw.println(stringBuilder.toString());
        }
        pw.print("  Start clock time: ");
        pw.println(DateFormat.format("yyyy-MM-dd-HH-mm-ss", getStartClockTime()).toString());
        long screenOnTime = getScreenOnTime(rawRealtime, which);
        long interactiveTime = getInteractiveTime(rawRealtime, which);
        long lowPowerModeEnabledTime = getLowPowerModeEnabledTime(rawRealtime, which);
        long phoneOnTime = getPhoneOnTime(rawRealtime, which);
        long wifiRunningTime = getGlobalWifiRunningTime(rawRealtime, which);
        long wifiOnTime = getWifiOnTime(rawRealtime, which);
        long bluetoothOnTime = getBluetoothOnTime(rawRealtime, which);
        stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
        stringBuilder.append(prefix);
        stringBuilder.append("  Screen on: ");
        formatTimeMs(stringBuilder, screenOnTime / 1000);
        stringBuilder.append("(");
        stringBuilder.append(formatRatioLocked(screenOnTime, whichBatteryRealtime));
        stringBuilder.append(") ");
        stringBuilder.append(getScreenOnCount(which));
        stringBuilder.append("x, Interactive: ");
        formatTimeMs(stringBuilder, interactiveTime / 1000);
        stringBuilder.append("(");
        stringBuilder.append(formatRatioLocked(interactiveTime, whichBatteryRealtime));
        stringBuilder.append(")");
        pw.println(stringBuilder.toString());
        stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
        stringBuilder.append(prefix);
        stringBuilder.append("  Screen brightnesses:");
        boolean didOne = LOCAL_LOGV;
        for (i = WIFI_SUPPL_STATE_INVALID; i < WIFI_SUPPL_STATE_AUTHENTICATING; i += WIFI_SUPPL_STATE_DISCONNECTED) {
            long time = getScreenBrightnessTime(i, rawRealtime, which);
            if (time != 0) {
                stringBuilder.append("\n    ");
                stringBuilder.append(prefix);
                didOne = true;
                stringBuilder.append(SCREEN_BRIGHTNESS_NAMES[i]);
                stringBuilder.append(" ");
                formatTimeMs(stringBuilder, time / 1000);
                stringBuilder.append("(");
                stringBuilder.append(formatRatioLocked(time, screenOnTime));
                stringBuilder.append(")");
            }
        }
        if (!didOne) {
            stringBuilder.append(" (no activity)");
        }
        pw.println(stringBuilder.toString());
        if (lowPowerModeEnabledTime != 0) {
            stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
            stringBuilder.append(prefix);
            stringBuilder.append("  Low power mode enabled: ");
            formatTimeMs(stringBuilder, lowPowerModeEnabledTime / 1000);
            stringBuilder.append("(");
            stringBuilder.append(formatRatioLocked(lowPowerModeEnabledTime, whichBatteryRealtime));
            stringBuilder.append(")");
            pw.println(stringBuilder.toString());
        }
        if (phoneOnTime != 0) {
            stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
            stringBuilder.append(prefix);
            stringBuilder.append("  Active phone call: ");
            formatTimeMs(stringBuilder, phoneOnTime / 1000);
            stringBuilder.append("(");
            stringBuilder.append(formatRatioLocked(phoneOnTime, whichBatteryRealtime));
            stringBuilder.append(") ");
            stringBuilder.append(getPhoneOnCount(which));
        }
        int connChanges = getNumConnectivityChange(which);
        if (connChanges != 0) {
            pw.print(prefix);
            pw.print("  Connectivity changes: ");
            pw.println(connChanges);
        }
        long fullWakeLockTimeTotalMicros = 0;
        long partialWakeLockTimeTotalMicros = 0;
        ArrayList<TimerEntry> timers = new ArrayList();
        for (iu = WIFI_SUPPL_STATE_INVALID; iu < NU; iu += WIFI_SUPPL_STATE_DISCONNECTED) {
            Uid u = (Uid) uidStats.valueAt(iu);
            Map<String, ? extends Wakelock> wakelocks = u.getWakelockStats();
            if (wakelocks.size() > 0) {
                for (Entry<String, ? extends Wakelock> ent : wakelocks.entrySet()) {
                    Wakelock wl = (Wakelock) ent.getValue();
                    Timer fullWakeTimer = wl.getWakeTime(WIFI_SUPPL_STATE_DISCONNECTED);
                    if (fullWakeTimer != null) {
                        fullWakeLockTimeTotalMicros += fullWakeTimer.getTotalTimeLocked(rawRealtime, which);
                    }
                    Timer partialWakeTimer = wl.getWakeTime(WIFI_SUPPL_STATE_INVALID);
                    if (partialWakeTimer != null) {
                        long totalTimeMicros = partialWakeTimer.getTotalTimeLocked(rawRealtime, which);
                        if (totalTimeMicros > 0) {
                            if (reqUid < 0) {
                                timers.add(new TimerEntry((String) ent.getKey(), u.getUid(), partialWakeTimer, totalTimeMicros));
                            }
                            partialWakeLockTimeTotalMicros += totalTimeMicros;
                        }
                    }
                }
            }
        }
        long mobileRxTotalBytes = getNetworkActivityBytes(WIFI_SUPPL_STATE_INVALID, which);
        long mobileTxTotalBytes = getNetworkActivityBytes(WIFI_SUPPL_STATE_DISCONNECTED, which);
        long wifiRxTotalBytes = getNetworkActivityBytes(WIFI_SUPPL_STATE_INTERFACE_DISABLED, which);
        long wifiTxTotalBytes = getNetworkActivityBytes(WIFI_SUPPL_STATE_INACTIVE, which);
        long mobileRxTotalPackets = getNetworkActivityPackets(WIFI_SUPPL_STATE_INVALID, which);
        long mobileTxTotalPackets = getNetworkActivityPackets(WIFI_SUPPL_STATE_DISCONNECTED, which);
        long wifiRxTotalPackets = getNetworkActivityPackets(WIFI_SUPPL_STATE_INTERFACE_DISABLED, which);
        long wifiTxTotalPackets = getNetworkActivityPackets(WIFI_SUPPL_STATE_INACTIVE, which);
        if (fullWakeLockTimeTotalMicros != 0) {
            stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
            stringBuilder.append(prefix);
            stringBuilder.append("  Total full wakelock time: ");
            formatTimeMsNoSpace(stringBuilder, (500 + fullWakeLockTimeTotalMicros) / 1000);
            pw.println(stringBuilder.toString());
        }
        if (partialWakeLockTimeTotalMicros != 0) {
            stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
            stringBuilder.append(prefix);
            stringBuilder.append("  Total partial wakelock time: ");
            formatTimeMsNoSpace(stringBuilder, (500 + partialWakeLockTimeTotalMicros) / 1000);
            pw.println(stringBuilder.toString());
        }
        pw.print(prefix);
        pw.print("  Mobile total received: ");
        pw.print(formatBytesLocked(mobileRxTotalBytes));
        pw.print(", sent: ");
        pw.print(formatBytesLocked(mobileTxTotalBytes));
        pw.print(" (packets received ");
        pw.print(mobileRxTotalPackets);
        pw.print(", sent ");
        pw.print(mobileTxTotalPackets);
        pw.println(")");
        stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
        stringBuilder.append(prefix);
        stringBuilder.append("  Phone signal levels:");
        didOne = LOCAL_LOGV;
        for (i = WIFI_SUPPL_STATE_INVALID; i < WIFI_SUPPL_STATE_AUTHENTICATING; i += WIFI_SUPPL_STATE_DISCONNECTED) {
            time = getPhoneSignalStrengthTime(i, rawRealtime, which);
            if (time != 0) {
                stringBuilder.append("\n    ");
                stringBuilder.append(prefix);
                didOne = true;
                stringBuilder.append(SignalStrength.SIGNAL_STRENGTH_NAMES[i]);
                stringBuilder.append(" ");
                formatTimeMs(stringBuilder, time / 1000);
                stringBuilder.append("(");
                stringBuilder.append(formatRatioLocked(time, whichBatteryRealtime));
                stringBuilder.append(") ");
                stringBuilder.append(getPhoneSignalStrengthCount(i, which));
                stringBuilder.append("x");
            }
        }
        if (!didOne) {
            stringBuilder.append(" (no activity)");
        }
        pw.println(stringBuilder.toString());
        stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
        stringBuilder.append(prefix);
        stringBuilder.append("  Signal scanning time: ");
        formatTimeMsNoSpace(stringBuilder, getPhoneSignalScanningTime(rawRealtime, which) / 1000);
        pw.println(stringBuilder.toString());
        stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
        stringBuilder.append(prefix);
        stringBuilder.append("  Radio types:");
        didOne = LOCAL_LOGV;
        for (i = WIFI_SUPPL_STATE_INVALID; i < NUM_DATA_CONNECTION_TYPES; i += WIFI_SUPPL_STATE_DISCONNECTED) {
            time = getPhoneDataConnectionTime(i, rawRealtime, which);
            if (time != 0) {
                stringBuilder.append("\n    ");
                stringBuilder.append(prefix);
                didOne = true;
                stringBuilder.append(DATA_CONNECTION_NAMES[i]);
                stringBuilder.append(" ");
                formatTimeMs(stringBuilder, time / 1000);
                stringBuilder.append("(");
                stringBuilder.append(formatRatioLocked(time, whichBatteryRealtime));
                stringBuilder.append(") ");
                stringBuilder.append(getPhoneDataConnectionCount(i, which));
                stringBuilder.append("x");
            }
        }
        if (!didOne) {
            stringBuilder.append(" (no activity)");
        }
        pw.println(stringBuilder.toString());
        stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
        stringBuilder.append(prefix);
        stringBuilder.append("  Mobile radio active time: ");
        long mobileActiveTime = getMobileRadioActiveTime(rawRealtime, which);
        formatTimeMs(stringBuilder, mobileActiveTime / 1000);
        stringBuilder.append("(");
        stringBuilder.append(formatRatioLocked(mobileActiveTime, whichBatteryRealtime));
        stringBuilder.append(") ");
        stringBuilder.append(getMobileRadioActiveCount(which));
        stringBuilder.append("x");
        pw.println(stringBuilder.toString());
        long mobileActiveUnknownTime = getMobileRadioActiveUnknownTime(which);
        if (mobileActiveUnknownTime != 0) {
            stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
            stringBuilder.append(prefix);
            stringBuilder.append("  Mobile radio active unknown time: ");
            formatTimeMs(stringBuilder, mobileActiveUnknownTime / 1000);
            stringBuilder.append("(");
            stringBuilder.append(formatRatioLocked(mobileActiveUnknownTime, whichBatteryRealtime));
            stringBuilder.append(") ");
            stringBuilder.append(getMobileRadioActiveUnknownCount(which));
            stringBuilder.append("x");
            pw.println(stringBuilder.toString());
        }
        long mobileActiveAdjustedTime = getMobileRadioActiveAdjustedTime(which);
        if (mobileActiveAdjustedTime != 0) {
            stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
            stringBuilder.append(prefix);
            stringBuilder.append("  Mobile radio active adjusted time: ");
            formatTimeMs(stringBuilder, mobileActiveAdjustedTime / 1000);
            stringBuilder.append("(");
            stringBuilder.append(formatRatioLocked(mobileActiveAdjustedTime, whichBatteryRealtime));
            stringBuilder.append(")");
            pw.println(stringBuilder.toString());
        }
        pw.print(prefix);
        pw.print("  Wi-Fi total received: ");
        pw.print(formatBytesLocked(wifiRxTotalBytes));
        pw.print(", sent: ");
        pw.print(formatBytesLocked(wifiTxTotalBytes));
        pw.print(" (packets received ");
        pw.print(wifiRxTotalPackets);
        pw.print(", sent ");
        pw.print(wifiTxTotalPackets);
        pw.println(")");
        stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
        stringBuilder.append(prefix);
        stringBuilder.append("  Wifi on: ");
        formatTimeMs(stringBuilder, wifiOnTime / 1000);
        stringBuilder.append("(");
        stringBuilder.append(formatRatioLocked(wifiOnTime, whichBatteryRealtime));
        stringBuilder.append("), Wifi running: ");
        formatTimeMs(stringBuilder, wifiRunningTime / 1000);
        stringBuilder.append("(");
        stringBuilder.append(formatRatioLocked(wifiRunningTime, whichBatteryRealtime));
        stringBuilder.append(")");
        pw.println(stringBuilder.toString());
        stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
        stringBuilder.append(prefix);
        stringBuilder.append("  Wifi states:");
        didOne = LOCAL_LOGV;
        for (i = WIFI_SUPPL_STATE_INVALID; i < WIFI_SUPPL_STATE_FOUR_WAY_HANDSHAKE; i += WIFI_SUPPL_STATE_DISCONNECTED) {
            time = getWifiStateTime(i, rawRealtime, which);
            if (time != 0) {
                stringBuilder.append("\n    ");
                didOne = true;
                stringBuilder.append(WIFI_STATE_NAMES[i]);
                stringBuilder.append(" ");
                formatTimeMs(stringBuilder, time / 1000);
                stringBuilder.append("(");
                stringBuilder.append(formatRatioLocked(time, whichBatteryRealtime));
                stringBuilder.append(") ");
                stringBuilder.append(getWifiStateCount(i, which));
                stringBuilder.append("x");
            }
        }
        if (!didOne) {
            stringBuilder.append(" (no activity)");
        }
        pw.println(stringBuilder.toString());
        stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
        stringBuilder.append(prefix);
        stringBuilder.append("  Wifi supplicant states:");
        didOne = LOCAL_LOGV;
        for (i = WIFI_SUPPL_STATE_INVALID; i < SYNC; i += WIFI_SUPPL_STATE_DISCONNECTED) {
            time = getWifiSupplStateTime(i, rawRealtime, which);
            if (time != 0) {
                stringBuilder.append("\n    ");
                didOne = true;
                stringBuilder.append(WIFI_SUPPL_STATE_NAMES[i]);
                stringBuilder.append(" ");
                formatTimeMs(stringBuilder, time / 1000);
                stringBuilder.append("(");
                stringBuilder.append(formatRatioLocked(time, whichBatteryRealtime));
                stringBuilder.append(") ");
                stringBuilder.append(getWifiSupplStateCount(i, which));
                stringBuilder.append("x");
            }
        }
        if (!didOne) {
            stringBuilder.append(" (no activity)");
        }
        pw.println(stringBuilder.toString());
        stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
        stringBuilder.append(prefix);
        stringBuilder.append("  Wifi signal levels:");
        didOne = LOCAL_LOGV;
        for (i = WIFI_SUPPL_STATE_INVALID; i < WIFI_SUPPL_STATE_AUTHENTICATING; i += WIFI_SUPPL_STATE_DISCONNECTED) {
            time = getWifiSignalStrengthTime(i, rawRealtime, which);
            if (time != 0) {
                stringBuilder.append("\n    ");
                stringBuilder.append(prefix);
                didOne = true;
                stringBuilder.append("level(");
                stringBuilder.append(i);
                stringBuilder.append(") ");
                formatTimeMs(stringBuilder, time / 1000);
                stringBuilder.append("(");
                stringBuilder.append(formatRatioLocked(time, whichBatteryRealtime));
                stringBuilder.append(") ");
                stringBuilder.append(getWifiSignalStrengthCount(i, which));
                stringBuilder.append("x");
            }
        }
        if (!didOne) {
            stringBuilder.append(" (no activity)");
        }
        pw.println(stringBuilder.toString());
        stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
        stringBuilder.append(prefix);
        stringBuilder.append("  Bluetooth on: ");
        formatTimeMs(stringBuilder, bluetoothOnTime / 1000);
        stringBuilder.append("(");
        stringBuilder.append(formatRatioLocked(bluetoothOnTime, whichBatteryRealtime));
        stringBuilder.append(")");
        pw.println(stringBuilder.toString());
        stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
        stringBuilder.append(prefix);
        stringBuilder.append("  Bluetooth states:");
        didOne = LOCAL_LOGV;
        for (i = WIFI_SUPPL_STATE_INVALID; i < WIFI_SUPPL_STATE_SCANNING; i += WIFI_SUPPL_STATE_DISCONNECTED) {
            time = getBluetoothStateTime(i, rawRealtime, which);
            if (time != 0) {
                stringBuilder.append("\n    ");
                didOne = true;
                stringBuilder.append(BLUETOOTH_STATE_NAMES[i]);
                stringBuilder.append(" ");
                formatTimeMs(stringBuilder, time / 1000);
                stringBuilder.append("(");
                stringBuilder.append(formatRatioLocked(time, whichBatteryRealtime));
                stringBuilder.append(") ");
                stringBuilder.append(getPhoneDataConnectionCount(i, which));
                stringBuilder.append("x");
            }
        }
        if (!didOne) {
            stringBuilder.append(" (no activity)");
        }
        pw.println(stringBuilder.toString());
        pw.println();
        if (which == WIFI_SUPPL_STATE_INTERFACE_DISABLED) {
            if (getIsOnBattery()) {
                pw.print(prefix);
                pw.println("  Device is currently unplugged");
                pw.print(prefix);
                pw.print("    Discharge cycle start level: ");
                pw.println(getDischargeStartLevel());
                pw.print(prefix);
                pw.print("    Discharge cycle current level: ");
                pw.println(getDischargeCurrentLevel());
            } else {
                pw.print(prefix);
                pw.println("  Device is currently plugged into power");
                pw.print(prefix);
                pw.print("    Last discharge cycle start level: ");
                pw.println(getDischargeStartLevel());
                pw.print(prefix);
                pw.print("    Last discharge cycle end level: ");
                pw.println(getDischargeCurrentLevel());
            }
            pw.print(prefix);
            pw.print("    Amount discharged while screen on: ");
            pw.println(getDischargeAmountScreenOn());
            pw.print(prefix);
            pw.print("    Amount discharged while screen off: ");
            pw.println(getDischargeAmountScreenOff());
            pw.println(" ");
        } else {
            pw.print(prefix);
            pw.println("  Device battery use since last full charge");
            pw.print(prefix);
            pw.print("    Amount discharged (lower bound): ");
            pw.println(getLowDischargeAmountSinceCharge());
            pw.print(prefix);
            pw.print("    Amount discharged (upper bound): ");
            pw.println(getHighDischargeAmountSinceCharge());
            pw.print(prefix);
            pw.print("    Amount discharged while screen on: ");
            pw.println(getDischargeAmountScreenOnSinceCharge());
            pw.print(prefix);
            pw.print("    Amount discharged while screen off: ");
            pw.println(getDischargeAmountScreenOffSinceCharge());
            pw.println();
        }
        BatteryStatsHelper batteryStatsHelper = new BatteryStatsHelper(context, LOCAL_LOGV, wifiOnly);
        batteryStatsHelper.create(this);
        batteryStatsHelper.refreshStats(which, -1);
        List<BatterySipper> sippers = batteryStatsHelper.getUsageList();
        if (sippers != null && sippers.size() > 0) {
            pw.print(prefix);
            pw.println("  Estimated power use (mAh):");
            pw.print(prefix);
            pw.print("    Capacity: ");
            printmAh(pw, batteryStatsHelper.getPowerProfile().getBatteryCapacity());
            pw.print(", Computed drain: ");
            printmAh(pw, batteryStatsHelper.getComputedPower());
            pw.print(", actual drain: ");
            printmAh(pw, batteryStatsHelper.getMinDrainedPower());
            if (batteryStatsHelper.getMinDrainedPower() != batteryStatsHelper.getMaxDrainedPower()) {
                pw.print("-");
                printmAh(pw, batteryStatsHelper.getMaxDrainedPower());
            }
            pw.println();
            for (i = WIFI_SUPPL_STATE_INVALID; i < sippers.size(); i += WIFI_SUPPL_STATE_DISCONNECTED) {
                bs = (BatterySipper) sippers.get(i);
                switch (AnonymousClass2.$SwitchMap$com$android$internal$os$BatterySipper$DrainType[bs.drainType.ordinal()]) {
                    case WIFI_SUPPL_STATE_DISCONNECTED /*1*/:
                        pw.print(prefix);
                        pw.print("    Idle: ");
                        printmAh(pw, bs.value);
                        pw.println();
                        break;
                    case WIFI_SUPPL_STATE_INTERFACE_DISABLED /*2*/:
                        pw.print(prefix);
                        pw.print("    Cell standby: ");
                        printmAh(pw, bs.value);
                        pw.println();
                        break;
                    case WIFI_SUPPL_STATE_INACTIVE /*3*/:
                        pw.print(prefix);
                        pw.print("    Phone calls: ");
                        printmAh(pw, bs.value);
                        pw.println();
                        break;
                    case WIFI_SUPPL_STATE_SCANNING /*4*/:
                        pw.print(prefix);
                        pw.print("    Wifi: ");
                        printmAh(pw, bs.value);
                        pw.println();
                        break;
                    case WIFI_SUPPL_STATE_AUTHENTICATING /*5*/:
                        pw.print(prefix);
                        pw.print("    Bluetooth: ");
                        printmAh(pw, bs.value);
                        pw.println();
                        break;
                    case WIFI_SUPPL_STATE_ASSOCIATING /*6*/:
                        pw.print(prefix);
                        pw.print("    Screen: ");
                        printmAh(pw, bs.value);
                        pw.println();
                        break;
                    case WIFI_SUPPL_STATE_ASSOCIATED /*7*/:
                        pw.print(prefix);
                        pw.print("    Flashlight: ");
                        printmAh(pw, bs.value);
                        pw.println();
                        break;
                    case WIFI_SUPPL_STATE_FOUR_WAY_HANDSHAKE /*8*/:
                        pw.print(prefix);
                        pw.print("    Uid ");
                        UserHandle.formatUid(pw, bs.uidObj.getUid());
                        pw.print(": ");
                        printmAh(pw, bs.value);
                        pw.println();
                        break;
                    case WIFI_SUPPL_STATE_GROUP_HANDSHAKE /*9*/:
                        pw.print(prefix);
                        pw.print("    User ");
                        pw.print(bs.userId);
                        pw.print(": ");
                        printmAh(pw, bs.value);
                        pw.println();
                        break;
                    case WIFI_SUPPL_STATE_COMPLETED /*10*/:
                        pw.print(prefix);
                        pw.print("    Unaccounted: ");
                        printmAh(pw, bs.value);
                        pw.println();
                        break;
                    case WIFI_SUPPL_STATE_DORMANT /*11*/:
                        pw.print(prefix);
                        pw.print("    Over-counted: ");
                        printmAh(pw, bs.value);
                        pw.println();
                        break;
                    default:
                        break;
                }
            }
            pw.println();
        }
        sippers = batteryStatsHelper.getMobilemsppList();
        if (sippers != null && sippers.size() > 0) {
            pw.print(prefix);
            pw.println("  Per-app mobile ms per packet:");
            totalTime = 0;
            for (i = WIFI_SUPPL_STATE_INVALID; i < sippers.size(); i += WIFI_SUPPL_STATE_DISCONNECTED) {
                bs = (BatterySipper) sippers.get(i);
                stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
                stringBuilder.append(prefix);
                stringBuilder.append("    Uid ");
                UserHandle.formatUid(stringBuilder, bs.uidObj.getUid());
                stringBuilder.append(": ");
                stringBuilder.append(BatteryStatsHelper.makemAh(bs.mobilemspp));
                stringBuilder.append(" (");
                stringBuilder.append(bs.mobileRxPackets + bs.mobileTxPackets);
                stringBuilder.append(" packets over ");
                formatTimeMsNoSpace(stringBuilder, bs.mobileActive);
                stringBuilder.append(") ");
                stringBuilder.append(bs.mobileActiveCount);
                stringBuilder.append("x");
                pw.println(stringBuilder.toString());
                totalTime += bs.mobileActive;
            }
            stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
            stringBuilder.append(prefix);
            stringBuilder.append("    TOTAL TIME: ");
            formatTimeMs(stringBuilder, totalTime);
            stringBuilder.append("(");
            stringBuilder.append(formatRatioLocked(totalTime, whichBatteryRealtime));
            stringBuilder.append(")");
            pw.println(stringBuilder.toString());
            pw.println();
        }
        Comparator<TimerEntry> anonymousClass1 = new AnonymousClass1(this);
        if (reqUid < 0) {
            TimerEntry timer2;
            Map<String, ? extends Timer> kernelWakelocks = getKernelWakelockStats();
            if (kernelWakelocks.size() > 0) {
                ArrayList<TimerEntry> ktimers = new ArrayList();
                for (Entry<String, ? extends Timer> ent2 : kernelWakelocks.entrySet()) {
                    timer = (Timer) ent2.getValue();
                    long totalTimeMillis = computeWakeLock(timer, rawRealtime, which);
                    if (totalTimeMillis > 0) {
                        ktimers.add(new TimerEntry((String) ent2.getKey(), WIFI_SUPPL_STATE_INVALID, timer, totalTimeMillis));
                    }
                }
                if (ktimers.size() > 0) {
                    Collections.sort(ktimers, anonymousClass1);
                    pw.print(prefix);
                    pw.println("  All kernel wake locks:");
                    for (i = WIFI_SUPPL_STATE_INVALID; i < ktimers.size(); i += WIFI_SUPPL_STATE_DISCONNECTED) {
                        timer2 = (TimerEntry) ktimers.get(i);
                        stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
                        stringBuilder.append(prefix);
                        stringBuilder.append("  Kernel Wake lock ");
                        stringBuilder.append(timer2.mName);
                        if (!printWakeLock(stringBuilder, timer2.mTimer, rawRealtime, null, which, ": ").equals(": ")) {
                            stringBuilder.append(" realtime");
                            pw.println(stringBuilder.toString());
                        }
                    }
                    pw.println();
                }
            }
            if (timers.size() > 0) {
                Collections.sort(timers, anonymousClass1);
                pw.print(prefix);
                pw.println("  All partial wake locks:");
                for (i = WIFI_SUPPL_STATE_INVALID; i < timers.size(); i += WIFI_SUPPL_STATE_DISCONNECTED) {
                    timer2 = (TimerEntry) timers.get(i);
                    stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
                    stringBuilder.append("  Wake lock ");
                    UserHandle.formatUid(stringBuilder, timer2.mId);
                    stringBuilder.append(" ");
                    stringBuilder.append(timer2.mName);
                    printWakeLock(stringBuilder, timer2.mTimer, rawRealtime, null, which, ": ");
                    stringBuilder.append(" realtime");
                    pw.println(stringBuilder.toString());
                }
                timers.clear();
                pw.println();
            }
            Map<String, ? extends Timer> wakeupReasons = getWakeupReasonStats();
            if (wakeupReasons.size() > 0) {
                pw.print(prefix);
                pw.println("  All wakeup reasons:");
                ArrayList<TimerEntry> reasons = new ArrayList();
                for (Entry<String, ? extends Timer> ent22 : wakeupReasons.entrySet()) {
                    timer = (Timer) ent22.getValue();
                    reasons.add(new TimerEntry((String) ent22.getKey(), WIFI_SUPPL_STATE_INVALID, timer, (long) timer.getCountLocked(which)));
                }
                Collections.sort(reasons, anonymousClass1);
                for (i = WIFI_SUPPL_STATE_INVALID; i < reasons.size(); i += WIFI_SUPPL_STATE_DISCONNECTED) {
                    timer2 = (TimerEntry) reasons.get(i);
                    String str = ": ";
                    stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
                    stringBuilder.append(prefix);
                    stringBuilder.append("  Wakeup reason ");
                    stringBuilder.append(timer2.mName);
                    printWakeLock(stringBuilder, timer2.mTimer, rawRealtime, null, which, ": ");
                    stringBuilder.append(" realtime");
                    pw.println(stringBuilder.toString());
                }
                pw.println();
            }
        }
        for (iu = WIFI_SUPPL_STATE_INVALID; iu < NU; iu += WIFI_SUPPL_STATE_DISCONNECTED) {
            int uid = uidStats.keyAt(iu);
            if (reqUid < 0 || uid == reqUid || uid == 1000) {
                int count;
                int starts;
                u = (Uid) uidStats.valueAt(iu);
                pw.print(prefix);
                pw.print("  ");
                UserHandle.formatUid(pw, uid);
                pw.println(":");
                boolean uidActivity = LOCAL_LOGV;
                long mobileRxBytes = u.getNetworkActivityBytes(WIFI_SUPPL_STATE_INVALID, which);
                long mobileTxBytes = u.getNetworkActivityBytes(WIFI_SUPPL_STATE_DISCONNECTED, which);
                long wifiRxBytes = u.getNetworkActivityBytes(WIFI_SUPPL_STATE_INTERFACE_DISABLED, which);
                long wifiTxBytes = u.getNetworkActivityBytes(WIFI_SUPPL_STATE_INACTIVE, which);
                long mobileRxPackets = u.getNetworkActivityPackets(WIFI_SUPPL_STATE_INVALID, which);
                long mobileTxPackets = u.getNetworkActivityPackets(WIFI_SUPPL_STATE_DISCONNECTED, which);
                long uidMobileActiveTime = u.getMobileRadioActiveTime(which);
                int uidMobileActiveCount = u.getMobileRadioActiveCount(which);
                long wifiRxPackets = u.getNetworkActivityPackets(WIFI_SUPPL_STATE_INTERFACE_DISABLED, which);
                long wifiTxPackets = u.getNetworkActivityPackets(WIFI_SUPPL_STATE_INACTIVE, which);
                long fullWifiLockOnTime = u.getFullWifiLockTime(rawRealtime, which);
                long wifiScanTime = u.getWifiScanTime(rawRealtime, which);
                long uidWifiRunningTime = u.getWifiRunningTime(rawRealtime, which);
                if (mobileRxBytes > 0 || mobileTxBytes > 0 || mobileRxPackets > 0 || mobileTxPackets > 0) {
                    pw.print(prefix);
                    pw.print("    Mobile network: ");
                    pw.print(formatBytesLocked(mobileRxBytes));
                    pw.print(" received, ");
                    pw.print(formatBytesLocked(mobileTxBytes));
                    pw.print(" sent (packets ");
                    pw.print(mobileRxPackets);
                    pw.print(" received, ");
                    pw.print(mobileTxPackets);
                    pw.println(" sent)");
                }
                if (uidMobileActiveTime > 0 || uidMobileActiveCount > 0) {
                    stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
                    stringBuilder.append(prefix);
                    stringBuilder.append("    Mobile radio active: ");
                    formatTimeMs(stringBuilder, uidMobileActiveTime / 1000);
                    stringBuilder.append("(");
                    stringBuilder.append(formatRatioLocked(uidMobileActiveTime, mobileActiveTime));
                    stringBuilder.append(") ");
                    stringBuilder.append(uidMobileActiveCount);
                    stringBuilder.append("x");
                    long packets = mobileRxPackets + mobileTxPackets;
                    if (packets == 0) {
                        packets = 1;
                    }
                    stringBuilder.append(" @ ");
                    stringBuilder.append(BatteryStatsHelper.makemAh(((double) (uidMobileActiveTime / 1000)) / ((double) packets)));
                    stringBuilder.append(" mspp");
                    pw.println(stringBuilder.toString());
                }
                if (wifiRxBytes > 0 || wifiTxBytes > 0 || wifiRxPackets > 0 || wifiTxPackets > 0) {
                    pw.print(prefix);
                    pw.print("    Wi-Fi network: ");
                    pw.print(formatBytesLocked(wifiRxBytes));
                    pw.print(" received, ");
                    pw.print(formatBytesLocked(wifiTxBytes));
                    pw.print(" sent (packets ");
                    pw.print(wifiRxPackets);
                    pw.print(" received, ");
                    pw.print(wifiTxPackets);
                    pw.println(" sent)");
                }
                if (!(fullWifiLockOnTime == 0 && wifiScanTime == 0 && uidWifiRunningTime == 0)) {
                    stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
                    stringBuilder.append(prefix);
                    stringBuilder.append("    Wifi Running: ");
                    formatTimeMs(stringBuilder, uidWifiRunningTime / 1000);
                    stringBuilder.append("(");
                    stringBuilder.append(formatRatioLocked(uidWifiRunningTime, whichBatteryRealtime));
                    stringBuilder.append(")\n");
                    stringBuilder.append(prefix);
                    stringBuilder.append("    Full Wifi Lock: ");
                    formatTimeMs(stringBuilder, fullWifiLockOnTime / 1000);
                    stringBuilder.append("(");
                    stringBuilder.append(formatRatioLocked(fullWifiLockOnTime, whichBatteryRealtime));
                    stringBuilder.append(")\n");
                    stringBuilder.append(prefix);
                    stringBuilder.append("    Wifi Scan: ");
                    formatTimeMs(stringBuilder, wifiScanTime / 1000);
                    stringBuilder.append("(");
                    stringBuilder.append(formatRatioLocked(wifiScanTime, whichBatteryRealtime));
                    stringBuilder.append(")");
                    pw.println(stringBuilder.toString());
                }
                if (u.hasUserActivity()) {
                    boolean hasData = LOCAL_LOGV;
                    for (i = WIFI_SUPPL_STATE_INVALID; i < WIFI_SUPPL_STATE_INACTIVE; i += WIFI_SUPPL_STATE_DISCONNECTED) {
                        int val = u.getUserActivityCount(i, which);
                        if (val != 0) {
                            if (hasData) {
                                stringBuilder.append(", ");
                            } else {
                                stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
                                stringBuilder.append("    User activity: ");
                                hasData = true;
                            }
                            stringBuilder.append(val);
                            stringBuilder.append(" ");
                            stringBuilder.append(Uid.USER_ACTIVITY_TYPES[i]);
                        }
                    }
                    if (hasData) {
                        pw.println(stringBuilder.toString());
                    }
                }
                wakelocks = u.getWakelockStats();
                if (wakelocks.size() > 0) {
                    long totalFull = 0;
                    long totalPartial = 0;
                    long totalWindow = 0;
                    count = WIFI_SUPPL_STATE_INVALID;
                    for (Entry<String, ? extends Wakelock> ent3 : wakelocks.entrySet()) {
                        wl = (Wakelock) ent3.getValue();
                        stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
                        stringBuilder.append(prefix);
                        stringBuilder.append("    Wake lock ");
                        stringBuilder.append((String) ent3.getKey());
                        str = printWakeLock(stringBuilder, wl.getWakeTime(WIFI_SUPPL_STATE_INTERFACE_DISABLED), rawRealtime, Context.WINDOW_SERVICE, which, printWakeLock(stringBuilder, wl.getWakeTime(WIFI_SUPPL_STATE_INVALID), rawRealtime, "partial", which, printWakeLock(stringBuilder, wl.getWakeTime(WIFI_SUPPL_STATE_DISCONNECTED), rawRealtime, "full", which, ": ")));
                        stringBuilder.append(" realtime");
                        pw.println(stringBuilder.toString());
                        uidActivity = true;
                        count += WIFI_SUPPL_STATE_DISCONNECTED;
                        totalFull += computeWakeLock(wl.getWakeTime(WIFI_SUPPL_STATE_DISCONNECTED), rawRealtime, which);
                        totalPartial += computeWakeLock(wl.getWakeTime(WIFI_SUPPL_STATE_INVALID), rawRealtime, which);
                        totalWindow += computeWakeLock(wl.getWakeTime(WIFI_SUPPL_STATE_INTERFACE_DISABLED), rawRealtime, which);
                    }
                    if (count > WIFI_SUPPL_STATE_DISCONNECTED && !(totalFull == 0 && totalPartial == 0 && totalWindow == 0)) {
                        stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
                        stringBuilder.append(prefix);
                        stringBuilder.append("    TOTAL wake: ");
                        boolean needComma = LOCAL_LOGV;
                        if (totalFull != 0) {
                            needComma = true;
                            formatTimeMs(stringBuilder, totalFull);
                            stringBuilder.append("full");
                        }
                        if (totalPartial != 0) {
                            if (needComma) {
                                stringBuilder.append(", ");
                            }
                            needComma = true;
                            formatTimeMs(stringBuilder, totalPartial);
                            stringBuilder.append("partial");
                        }
                        if (totalWindow != 0) {
                            if (needComma) {
                                stringBuilder.append(", ");
                            }
                            formatTimeMs(stringBuilder, totalWindow);
                            stringBuilder.append(Context.WINDOW_SERVICE);
                        }
                        stringBuilder.append(" realtime");
                        pw.println(stringBuilder.toString());
                    }
                }
                Map<String, ? extends Timer> syncs = u.getSyncStats();
                if (syncs.size() > 0) {
                    for (Entry<String, ? extends Timer> ent222 : syncs.entrySet()) {
                        timer = (Timer) ent222.getValue();
                        totalTime = (timer.getTotalTimeLocked(rawRealtime, which) + 500) / 1000;
                        count = timer.getCountLocked(which);
                        stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
                        stringBuilder.append(prefix);
                        stringBuilder.append("    Sync ");
                        stringBuilder.append((String) ent222.getKey());
                        stringBuilder.append(": ");
                        if (totalTime != 0) {
                            formatTimeMs(stringBuilder, totalTime);
                            stringBuilder.append("realtime (");
                            stringBuilder.append(count);
                            stringBuilder.append(" times)");
                        } else {
                            stringBuilder.append("(not used)");
                        }
                        pw.println(stringBuilder.toString());
                        uidActivity = true;
                    }
                }
                Map<String, ? extends Timer> jobs = u.getJobStats();
                if (jobs.size() > 0) {
                    for (Entry<String, ? extends Timer> ent2222 : jobs.entrySet()) {
                        timer = (Timer) ent2222.getValue();
                        totalTime = (timer.getTotalTimeLocked(rawRealtime, which) + 500) / 1000;
                        count = timer.getCountLocked(which);
                        stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
                        stringBuilder.append(prefix);
                        stringBuilder.append("    Job ");
                        stringBuilder.append((String) ent2222.getKey());
                        stringBuilder.append(": ");
                        if (totalTime != 0) {
                            formatTimeMs(stringBuilder, totalTime);
                            stringBuilder.append("realtime (");
                            stringBuilder.append(count);
                            stringBuilder.append(" times)");
                        } else {
                            stringBuilder.append("(not used)");
                        }
                        pw.println(stringBuilder.toString());
                        uidActivity = true;
                    }
                }
                SparseArray<? extends Sensor> sensors = u.getSensorStats();
                int NSE = sensors.size();
                for (int ise = WIFI_SUPPL_STATE_INVALID; ise < NSE; ise += WIFI_SUPPL_STATE_DISCONNECTED) {
                    Sensor se = (Sensor) sensors.valueAt(ise);
                    int sensorNumber = sensors.keyAt(ise);
                    stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
                    stringBuilder.append(prefix);
                    stringBuilder.append("    Sensor ");
                    int handle = se.getHandle();
                    if (handle == -10000) {
                        stringBuilder.append("GPS");
                    } else {
                        stringBuilder.append(handle);
                    }
                    stringBuilder.append(": ");
                    timer = se.getSensorTime();
                    if (timer != null) {
                        totalTime = (timer.getTotalTimeLocked(rawRealtime, which) + 500) / 1000;
                        count = timer.getCountLocked(which);
                        if (totalTime != 0) {
                            formatTimeMs(stringBuilder, totalTime);
                            stringBuilder.append("realtime (");
                            stringBuilder.append(count);
                            stringBuilder.append(" times)");
                        } else {
                            stringBuilder.append("(not used)");
                        }
                    } else {
                        stringBuilder.append("(not used)");
                    }
                    pw.println(stringBuilder.toString());
                    uidActivity = true;
                }
                Timer vibTimer = u.getVibratorOnTimer();
                if (vibTimer != null) {
                    totalTime = (vibTimer.getTotalTimeLocked(rawRealtime, which) + 500) / 1000;
                    count = vibTimer.getCountLocked(which);
                    if (totalTime != 0) {
                        stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
                        stringBuilder.append(prefix);
                        stringBuilder.append("    Vibrator: ");
                        formatTimeMs(stringBuilder, totalTime);
                        stringBuilder.append("realtime (");
                        stringBuilder.append(count);
                        stringBuilder.append(" times)");
                        pw.println(stringBuilder.toString());
                        uidActivity = true;
                    }
                }
                Timer fgTimer = u.getForegroundActivityTimer();
                if (fgTimer != null) {
                    totalTime = (fgTimer.getTotalTimeLocked(rawRealtime, which) + 500) / 1000;
                    count = fgTimer.getCountLocked(which);
                    if (totalTime != 0) {
                        stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
                        stringBuilder.append(prefix);
                        stringBuilder.append("    Foreground activities: ");
                        formatTimeMs(stringBuilder, totalTime);
                        stringBuilder.append("realtime (");
                        stringBuilder.append(count);
                        stringBuilder.append(" times)");
                        pw.println(stringBuilder.toString());
                        uidActivity = true;
                    }
                }
                long totalStateTime = 0;
                for (int ips = WIFI_SUPPL_STATE_INVALID; ips < WIFI_SUPPL_STATE_INACTIVE; ips += WIFI_SUPPL_STATE_DISCONNECTED) {
                    time = u.getProcessStateTime(ips, rawRealtime, which);
                    if (time > 0) {
                        totalStateTime += time;
                        stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
                        stringBuilder.append(prefix);
                        stringBuilder.append("    ");
                        stringBuilder.append(Uid.PROCESS_STATE_NAMES[ips]);
                        stringBuilder.append(" for: ");
                        formatTimeMs(stringBuilder, (500 + totalStateTime) / 1000);
                        pw.println(stringBuilder.toString());
                        uidActivity = true;
                    }
                }
                Map<String, ? extends Proc> processStats = u.getProcessStats();
                if (processStats.size() > 0) {
                    for (Entry<String, ? extends Proc> ent4 : processStats.entrySet()) {
                        Proc ps = (Proc) ent4.getValue();
                        long userTime = ps.getUserTime(which);
                        long systemTime = ps.getSystemTime(which);
                        long foregroundTime = ps.getForegroundTime(which);
                        starts = ps.getStarts(which);
                        int numCrashes = ps.getNumCrashes(which);
                        int numAnrs = ps.getNumAnrs(which);
                        int numExcessive = which == 0 ? ps.countExcessivePowers() : WIFI_SUPPL_STATE_INVALID;
                        if (userTime != 0 || systemTime != 0 || foregroundTime != 0 || starts != 0 || numExcessive != 0 || numCrashes != 0 || numAnrs != 0) {
                            stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
                            stringBuilder.append(prefix);
                            stringBuilder.append("    Proc ");
                            stringBuilder.append((String) ent4.getKey());
                            stringBuilder.append(":\n");
                            stringBuilder.append(prefix);
                            stringBuilder.append("      CPU: ");
                            formatTime(stringBuilder, userTime);
                            stringBuilder.append("usr + ");
                            formatTime(stringBuilder, systemTime);
                            stringBuilder.append("krn ; ");
                            formatTime(stringBuilder, foregroundTime);
                            stringBuilder.append(FOREGROUND_DATA);
                            if (!(starts == 0 && numCrashes == 0 && numAnrs == 0)) {
                                stringBuilder.append("\n");
                                stringBuilder.append(prefix);
                                stringBuilder.append("      ");
                                boolean hasOne = LOCAL_LOGV;
                                if (starts != 0) {
                                    hasOne = true;
                                    stringBuilder.append(starts);
                                    stringBuilder.append(" starts");
                                }
                                if (numCrashes != 0) {
                                    if (hasOne) {
                                        stringBuilder.append(", ");
                                    }
                                    hasOne = true;
                                    stringBuilder.append(numCrashes);
                                    stringBuilder.append(" crashes");
                                }
                                if (numAnrs != 0) {
                                    if (hasOne) {
                                        stringBuilder.append(", ");
                                    }
                                    stringBuilder.append(numAnrs);
                                    stringBuilder.append(" anrs");
                                }
                            }
                            pw.println(stringBuilder.toString());
                            for (int e = WIFI_SUPPL_STATE_INVALID; e < numExcessive; e += WIFI_SUPPL_STATE_DISCONNECTED) {
                                ExcessivePower ew = ps.getExcessivePower(e);
                                if (ew != null) {
                                    pw.print(prefix);
                                    pw.print("      * Killed for ");
                                    if (ew.type == WIFI_SUPPL_STATE_DISCONNECTED) {
                                        pw.print("wake lock");
                                    } else if (ew.type == WIFI_SUPPL_STATE_INTERFACE_DISABLED) {
                                        pw.print("cpu");
                                    } else {
                                        pw.print(Environment.MEDIA_UNKNOWN);
                                    }
                                    pw.print(" use: ");
                                    TimeUtils.formatDuration(ew.usedTime, pw);
                                    pw.print(" over ");
                                    TimeUtils.formatDuration(ew.overTime, pw);
                                    if (ew.overTime != 0) {
                                        pw.print(" (");
                                        pw.print((ew.usedTime * 100) / ew.overTime);
                                        pw.println("%)");
                                    }
                                }
                            }
                            uidActivity = true;
                        }
                    }
                }
                Map<String, ? extends Pkg> packageStats = u.getPackageStats();
                if (packageStats.size() > 0) {
                    for (Entry<String, ? extends Pkg> ent5 : packageStats.entrySet()) {
                        pw.print(prefix);
                        pw.print("    Apk ");
                        pw.print((String) ent5.getKey());
                        pw.println(":");
                        boolean apkActivity = LOCAL_LOGV;
                        Pkg ps2 = (Pkg) ent5.getValue();
                        int wakeups = ps2.getWakeups(which);
                        if (wakeups != 0) {
                            pw.print(prefix);
                            pw.print("      ");
                            pw.print(wakeups);
                            pw.println(" wakeup alarms");
                            apkActivity = true;
                        }
                        Map<String, ? extends Serv> serviceStats = ps2.getServiceStats();
                        if (serviceStats.size() > 0) {
                            for (Entry<String, ? extends Serv> sent : serviceStats.entrySet()) {
                                Serv ss = (Serv) sent.getValue();
                                long startTime = ss.getStartTime(batteryUptime, which);
                                starts = ss.getStarts(which);
                                int launches = ss.getLaunches(which);
                                if (startTime != 0 || starts != 0 || launches != 0) {
                                    stringBuilder.setLength(WIFI_SUPPL_STATE_INVALID);
                                    stringBuilder.append(prefix);
                                    stringBuilder.append("      Service ");
                                    stringBuilder.append((String) sent.getKey());
                                    stringBuilder.append(":\n");
                                    stringBuilder.append(prefix);
                                    stringBuilder.append("        Created for: ");
                                    formatTimeMs(stringBuilder, startTime / 1000);
                                    stringBuilder.append("uptime\n");
                                    stringBuilder.append(prefix);
                                    stringBuilder.append("        Starts: ");
                                    stringBuilder.append(starts);
                                    stringBuilder.append(", launches: ");
                                    stringBuilder.append(launches);
                                    pw.println(stringBuilder.toString());
                                    apkActivity = true;
                                }
                            }
                        }
                        if (!apkActivity) {
                            pw.print(prefix);
                            pw.println("      (nothing executed)");
                        }
                        uidActivity = true;
                    }
                }
                if (!uidActivity) {
                    pw.print(prefix);
                    pw.println("    (nothing executed)");
                }
            }
        }
    }

    static void printBitDescriptions(PrintWriter pw, int oldval, int newval, HistoryTag wakelockTag, BitDescription[] descriptions, boolean longNames) {
        int diff = oldval ^ newval;
        if (diff != 0) {
            boolean didWake = LOCAL_LOGV;
            for (int i = WIFI_SUPPL_STATE_INVALID; i < descriptions.length; i += WIFI_SUPPL_STATE_DISCONNECTED) {
                BitDescription bd = descriptions[i];
                if ((bd.mask & diff) != 0) {
                    pw.print(longNames ? " " : ",");
                    if (bd.shift < 0) {
                        pw.print((bd.mask & newval) != 0 ? "+" : "-");
                        pw.print(longNames ? bd.name : bd.shortName);
                        if (bd.mask == EditorInfo.IME_FLAG_NO_ENTER_ACTION && wakelockTag != null) {
                            didWake = true;
                            pw.print("=");
                            if (longNames) {
                                UserHandle.formatUid(pw, wakelockTag.uid);
                                pw.print(":\"");
                                pw.print(wakelockTag.string);
                                pw.print("\"");
                            } else {
                                pw.print(wakelockTag.poolIdx);
                            }
                        }
                    } else {
                        pw.print(longNames ? bd.name : bd.shortName);
                        pw.print("=");
                        int val = (bd.mask & newval) >> bd.shift;
                        if (bd.values == null || val < 0 || val >= bd.values.length) {
                            pw.print(val);
                        } else {
                            pw.print(longNames ? bd.values[val] : bd.shortValues[val]);
                        }
                    }
                }
            }
            if (!didWake && wakelockTag != null) {
                pw.print(longNames ? " wake_lock=" : ",w=");
                if (longNames) {
                    UserHandle.formatUid(pw, wakelockTag.uid);
                    pw.print(":\"");
                    pw.print(wakelockTag.string);
                    pw.print("\"");
                    return;
                }
                pw.print(wakelockTag.poolIdx);
            }
        }
    }

    public void prepareForDumpLocked() {
    }

    private void printSizeValue(PrintWriter pw, long size) {
        float result = (float) size;
        String suffix = ProxyInfo.LOCAL_EXCL_LIST;
        if (result >= 10240.0f) {
            suffix = "KB";
            result /= 1024.0f;
        }
        if (result >= 10240.0f) {
            suffix = "MB";
            result /= 1024.0f;
        }
        if (result >= 10240.0f) {
            suffix = "GB";
            result /= 1024.0f;
        }
        if (result >= 10240.0f) {
            suffix = "TB";
            result /= 1024.0f;
        }
        if (result >= 10240.0f) {
            suffix = "PB";
            result /= 1024.0f;
        }
        pw.print((int) result);
        pw.print(suffix);
    }

    private static boolean dumpTimeEstimate(PrintWriter pw, String label, long[] steps, int count, long modesOfInterest, long modeValues) {
        if (count <= 0) {
            return LOCAL_LOGV;
        }
        long total = 0;
        int numOfInterest = WIFI_SUPPL_STATE_INVALID;
        for (int i = WIFI_SUPPL_STATE_INVALID; i < count; i += WIFI_SUPPL_STATE_DISCONNECTED) {
            long initMode = (steps[i] & STEP_LEVEL_INITIAL_MODE_MASK) >> 48;
            if ((((steps[i] & STEP_LEVEL_MODIFIED_MODE_MASK) >> 56) & modesOfInterest) == 0 && (initMode & modesOfInterest) == modeValues) {
                numOfInterest += WIFI_SUPPL_STATE_DISCONNECTED;
                total += steps[i] & STEP_LEVEL_TIME_MASK;
            }
        }
        if (numOfInterest <= 0) {
            return LOCAL_LOGV;
        }
        long estimatedTime = (total / ((long) numOfInterest)) * 100;
        pw.print(label);
        StringBuilder sb = new StringBuilder(64);
        formatTimeMs(sb, estimatedTime);
        pw.print(sb);
        pw.println();
        return true;
    }

    private static boolean dumpDurationSteps(PrintWriter pw, String header, long[] steps, int count, boolean checkin) {
        if (count <= 0) {
            return LOCAL_LOGV;
        }
        if (!checkin) {
            pw.println(header);
        }
        String[] lineArgs = new String[WIFI_SUPPL_STATE_SCANNING];
        for (int i = WIFI_SUPPL_STATE_INVALID; i < count; i += WIFI_SUPPL_STATE_DISCONNECTED) {
            long duration = steps[i] & STEP_LEVEL_TIME_MASK;
            int level = (int) ((steps[i] & STEP_LEVEL_LEVEL_MASK) >> 40);
            long initMode = (steps[i] & STEP_LEVEL_INITIAL_MODE_MASK) >> 48;
            long modMode = (steps[i] & STEP_LEVEL_MODIFIED_MODE_MASK) >> 56;
            if (checkin) {
                lineArgs[WIFI_SUPPL_STATE_INVALID] = Long.toString(duration);
                lineArgs[WIFI_SUPPL_STATE_DISCONNECTED] = Integer.toString(level);
                if ((3 & modMode) == 0) {
                    switch (((int) (3 & initMode)) + WIFI_SUPPL_STATE_DISCONNECTED) {
                        case WIFI_SUPPL_STATE_DISCONNECTED /*1*/:
                            lineArgs[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = "s-";
                            break;
                        case WIFI_SUPPL_STATE_INTERFACE_DISABLED /*2*/:
                            lineArgs[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = "s+";
                            break;
                        case WIFI_SUPPL_STATE_INACTIVE /*3*/:
                            lineArgs[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = "sd";
                            break;
                        case WIFI_SUPPL_STATE_SCANNING /*4*/:
                            lineArgs[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = "sds";
                            break;
                        default:
                            lineArgs[WIFI_SUPPL_STATE_DISCONNECTED] = "?";
                            break;
                    }
                }
                lineArgs[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = ProxyInfo.LOCAL_EXCL_LIST;
                if ((4 & modMode) == 0) {
                    lineArgs[WIFI_SUPPL_STATE_INACTIVE] = (4 & initMode) != 0 ? "p+" : "p-";
                } else {
                    lineArgs[WIFI_SUPPL_STATE_INACTIVE] = ProxyInfo.LOCAL_EXCL_LIST;
                }
                dumpLine(pw, WIFI_SUPPL_STATE_INVALID, "i", header, (Object[]) lineArgs);
            } else {
                pw.print("  #");
                pw.print(i);
                pw.print(": ");
                TimeUtils.formatDuration(duration, pw);
                pw.print(" to ");
                pw.print(level);
                boolean haveModes = LOCAL_LOGV;
                if ((3 & modMode) == 0) {
                    pw.print(" (");
                    switch (((int) (3 & initMode)) + WIFI_SUPPL_STATE_DISCONNECTED) {
                        case WIFI_SUPPL_STATE_DISCONNECTED /*1*/:
                            pw.print("screen-off");
                            break;
                        case WIFI_SUPPL_STATE_INTERFACE_DISABLED /*2*/:
                            pw.print("screen-on");
                            break;
                        case WIFI_SUPPL_STATE_INACTIVE /*3*/:
                            pw.print("screen-doze");
                            break;
                        case WIFI_SUPPL_STATE_SCANNING /*4*/:
                            pw.print("screen-doze-suspend");
                            break;
                        default:
                            lineArgs[WIFI_SUPPL_STATE_DISCONNECTED] = "screen-?";
                            break;
                    }
                    haveModes = true;
                }
                if ((4 & modMode) == 0) {
                    pw.print(haveModes ? ", " : " (");
                    pw.print((4 & initMode) != 0 ? "power-save-on" : "power-save-off");
                    haveModes = true;
                }
                if (haveModes) {
                    pw.print(")");
                }
                pw.println();
            }
        }
        return true;
    }

    private void dumpHistoryLocked(PrintWriter pw, int flags, long histStart, boolean checkin) {
        HistoryPrinter hprinter = new HistoryPrinter();
        HistoryItem rec = new HistoryItem();
        long lastTime = -1;
        long baseTime = -1;
        boolean printed = LOCAL_LOGV;
        HistoryEventTracker tracker = null;
        while (getNextHistoryLocked(rec)) {
            lastTime = rec.time;
            if (baseTime < 0) {
                baseTime = lastTime;
            }
            if (rec.time >= histStart) {
                if (histStart >= 0 && !printed) {
                    if (rec.cmd == WIFI_SUPPL_STATE_AUTHENTICATING || rec.cmd == WIFI_SUPPL_STATE_ASSOCIATED || rec.cmd == WIFI_SUPPL_STATE_SCANNING || rec.cmd == WIFI_SUPPL_STATE_FOUR_WAY_HANDSHAKE) {
                        printed = true;
                        hprinter.printNextItem(pw, rec, baseTime, checkin, (flags & DUMP_VERBOSE) != 0 ? true : LOCAL_LOGV);
                        rec.cmd = (byte) 0;
                    } else if (rec.currentTime != 0) {
                        printed = true;
                        byte cmd = rec.cmd;
                        rec.cmd = (byte) 5;
                        hprinter.printNextItem(pw, rec, baseTime, checkin, (flags & DUMP_VERBOSE) != 0 ? true : LOCAL_LOGV);
                        rec.cmd = cmd;
                    }
                    if (tracker != null) {
                        if (rec.cmd != null) {
                            hprinter.printNextItem(pw, rec, baseTime, checkin, (flags & DUMP_VERBOSE) != 0 ? true : LOCAL_LOGV);
                            rec.cmd = (byte) 0;
                        }
                        int oldEventCode = rec.eventCode;
                        HistoryTag oldEventTag = rec.eventTag;
                        rec.eventTag = new HistoryTag();
                        for (int i = WIFI_SUPPL_STATE_INVALID; i < WIFI_SUPPL_STATE_COMPLETED; i += WIFI_SUPPL_STATE_DISCONNECTED) {
                            HashMap<String, SparseIntArray> active = tracker.getStateForEvent(i);
                            if (active != null) {
                                for (Entry<String, SparseIntArray> ent : active.entrySet()) {
                                    SparseIntArray uids = (SparseIntArray) ent.getValue();
                                    for (int j = WIFI_SUPPL_STATE_INVALID; j < uids.size(); j += WIFI_SUPPL_STATE_DISCONNECTED) {
                                        rec.eventCode = i;
                                        rec.eventTag.string = (String) ent.getKey();
                                        rec.eventTag.uid = uids.keyAt(j);
                                        rec.eventTag.poolIdx = uids.valueAt(j);
                                        hprinter.printNextItem(pw, rec, baseTime, checkin, (flags & DUMP_VERBOSE) != 0 ? true : LOCAL_LOGV);
                                        rec.wakeReasonTag = null;
                                        rec.wakelockTag = null;
                                    }
                                }
                            }
                        }
                        rec.eventCode = oldEventCode;
                        rec.eventTag = oldEventTag;
                        tracker = null;
                    }
                }
                hprinter.printNextItem(pw, rec, baseTime, checkin, (flags & DUMP_VERBOSE) != 0 ? true : LOCAL_LOGV);
            }
        }
        if (histStart >= 0) {
            commitCurrentHistoryBatchLocked();
            pw.print(checkin ? "NEXT: " : "  NEXT: ");
            pw.println(1 + lastTime);
        }
    }

    public void dumpLocked(Context context, PrintWriter pw, int flags, int reqUid, long histStart) {
        prepareForDumpLocked();
        boolean filtering = (flags & WIFI_SUPPL_STATE_ASSOCIATED) != 0 ? true : LOCAL_LOGV;
        if (!((flags & WIFI_SUPPL_STATE_SCANNING) == 0 && filtering)) {
            long historyTotalSize = (long) getHistoryTotalSize();
            long historyUsedSize = (long) getHistoryUsedSize();
            if (startIteratingHistoryLocked()) {
                try {
                    pw.print("Battery History (");
                    pw.print((100 * historyUsedSize) / historyTotalSize);
                    pw.print("% used, ");
                    printSizeValue(pw, historyUsedSize);
                    pw.print(" used of ");
                    printSizeValue(pw, historyTotalSize);
                    pw.print(", ");
                    pw.print(getHistoryStringPoolSize());
                    pw.print(" strings using ");
                    printSizeValue(pw, (long) getHistoryStringPoolBytes());
                    pw.println("):");
                    dumpHistoryLocked(pw, flags, histStart, LOCAL_LOGV);
                    pw.println();
                } finally {
                    finishIteratingHistoryLocked();
                }
            }
            if (startIteratingOldHistoryLocked()) {
                try {
                    HistoryItem rec = new HistoryItem();
                    pw.println("Old battery History:");
                    HistoryPrinter hprinter = new HistoryPrinter();
                    long baseTime = -1;
                    while (getNextOldHistoryLocked(rec)) {
                        if (baseTime < 0) {
                            baseTime = rec.time;
                        }
                        hprinter.printNextItem(pw, rec, baseTime, LOCAL_LOGV, (flags & DUMP_VERBOSE) != 0 ? true : LOCAL_LOGV);
                    }
                    pw.println();
                } finally {
                    finishIteratingOldHistoryLocked();
                }
            }
        }
        if (!filtering || (flags & WIFI_SUPPL_STATE_INACTIVE) != 0) {
            if (!filtering) {
                SparseArray<? extends Uid> uidStats = getUidStats();
                int NU = uidStats.size();
                boolean didPid = LOCAL_LOGV;
                long nowRealtime = SystemClock.elapsedRealtime();
                for (int i = WIFI_SUPPL_STATE_INVALID; i < NU; i += WIFI_SUPPL_STATE_DISCONNECTED) {
                    SparseArray<? extends Pid> pids = ((Uid) uidStats.valueAt(i)).getPidStats();
                    if (pids != null) {
                        for (int j = WIFI_SUPPL_STATE_INVALID; j < pids.size(); j += WIFI_SUPPL_STATE_DISCONNECTED) {
                            Pid pid = (Pid) pids.valueAt(j);
                            if (!didPid) {
                                pw.println("Per-PID Stats:");
                                didPid = true;
                            }
                            long time = pid.mWakeSumMs + (pid.mWakeNesting > 0 ? nowRealtime - pid.mWakeStartMs : 0);
                            pw.print("  PID ");
                            pw.print(pids.keyAt(j));
                            pw.print(" wake time: ");
                            TimeUtils.formatDuration(time, pw);
                            pw.println(ProxyInfo.LOCAL_EXCL_LIST);
                        }
                    }
                }
                if (didPid) {
                    pw.println();
                }
            }
            if (!(filtering && (flags & WIFI_SUPPL_STATE_INTERFACE_DISABLED) == 0)) {
                long timeRemaining;
                if (dumpDurationSteps(pw, "Discharge step durations:", getDischargeStepDurationsArray(), getNumDischargeStepDurations(), LOCAL_LOGV)) {
                    timeRemaining = computeBatteryTimeRemaining(SystemClock.elapsedRealtime());
                    if (timeRemaining >= 0) {
                        pw.print("  Estimated discharge time remaining: ");
                        TimeUtils.formatDuration(timeRemaining / 1000, pw);
                        pw.println();
                    }
                    dumpTimeEstimate(pw, "  Estimated screen off time: ", getDischargeStepDurationsArray(), getNumDischargeStepDurations(), 7, 0);
                    dumpTimeEstimate(pw, "  Estimated screen off power save time: ", getDischargeStepDurationsArray(), getNumDischargeStepDurations(), 7, 4);
                    dumpTimeEstimate(pw, "  Estimated screen on time: ", getDischargeStepDurationsArray(), getNumDischargeStepDurations(), 7, 1);
                    dumpTimeEstimate(pw, "  Estimated screen on power save time: ", getDischargeStepDurationsArray(), getNumDischargeStepDurations(), 7, 5);
                    dumpTimeEstimate(pw, "  Estimated screen doze time: ", getDischargeStepDurationsArray(), getNumDischargeStepDurations(), 7, 2);
                    dumpTimeEstimate(pw, "  Estimated screen doze power save time: ", getDischargeStepDurationsArray(), getNumDischargeStepDurations(), 7, 6);
                    dumpTimeEstimate(pw, "  Estimated screen doze suspend time: ", getDischargeStepDurationsArray(), getNumDischargeStepDurations(), 7, 3);
                    dumpTimeEstimate(pw, "  Estimated screen doze suspend power save time: ", getDischargeStepDurationsArray(), getNumDischargeStepDurations(), 7, 7);
                    pw.println();
                }
                if (dumpDurationSteps(pw, "Charge step durations:", getChargeStepDurationsArray(), getNumChargeStepDurations(), LOCAL_LOGV)) {
                    timeRemaining = computeChargeTimeRemaining(SystemClock.elapsedRealtime());
                    if (timeRemaining >= 0) {
                        pw.print("  Estimated charge time remaining: ");
                        TimeUtils.formatDuration(timeRemaining / 1000, pw);
                        pw.println();
                    }
                    pw.println();
                }
                pw.println("Statistics since last charge:");
                pw.println("  System starts: " + getStartCount() + ", currently on battery: " + getIsOnBattery());
                dumpLocked(context, pw, ProxyInfo.LOCAL_EXCL_LIST, WIFI_SUPPL_STATE_INVALID, reqUid, (flags & DUMP_DEVICE_WIFI_ONLY) != 0 ? true : LOCAL_LOGV);
                pw.println();
            }
            if (!filtering || (flags & WIFI_SUPPL_STATE_DISCONNECTED) != 0) {
                boolean z;
                pw.println("Statistics since last unplugged:");
                String str = ProxyInfo.LOCAL_EXCL_LIST;
                if ((flags & DUMP_DEVICE_WIFI_ONLY) != 0) {
                    z = true;
                } else {
                    z = LOCAL_LOGV;
                }
                dumpLocked(context, pw, str, WIFI_SUPPL_STATE_INTERFACE_DISABLED, reqUid, z);
            }
        }
    }

    public void dumpCheckinLocked(Context context, PrintWriter pw, List<ApplicationInfo> apps, int flags, long histStart) {
        int i;
        prepareForDumpLocked();
        String str = VERSION_DATA;
        Object[] objArr = new Object[WIFI_SUPPL_STATE_SCANNING];
        objArr[WIFI_SUPPL_STATE_INVALID] = "12";
        objArr[WIFI_SUPPL_STATE_DISCONNECTED] = Integer.valueOf(getParcelVersion());
        objArr[WIFI_SUPPL_STATE_INTERFACE_DISABLED] = getStartPlatformVersion();
        objArr[WIFI_SUPPL_STATE_INACTIVE] = getEndPlatformVersion();
        dumpLine(pw, WIFI_SUPPL_STATE_INVALID, "i", str, objArr);
        long now = getHistoryBaseTime() + SystemClock.elapsedRealtime();
        boolean filtering = (flags & WIFI_SUPPL_STATE_ASSOCIATED) != 0 ? true : LOCAL_LOGV;
        if (!((flags & WIFI_SUPPL_STATE_FOUR_WAY_HANDSHAKE) == 0 && (flags & WIFI_SUPPL_STATE_SCANNING) == 0) && startIteratingHistoryLocked()) {
            i = WIFI_SUPPL_STATE_INVALID;
            while (i < getHistoryStringPoolSize()) {
                try {
                    pw.print(WIFI_SUPPL_STATE_GROUP_HANDSHAKE);
                    pw.print(PhoneNumberUtils.PAUSE);
                    pw.print(HISTORY_STRING_POOL);
                    pw.print(PhoneNumberUtils.PAUSE);
                    pw.print(i);
                    pw.print(",");
                    pw.print(getHistoryTagPoolUid(i));
                    pw.print(",\"");
                    pw.print(getHistoryTagPoolString(i).replace("\\", "\\\\").replace("\"", "\\\""));
                    pw.print("\"");
                    pw.println();
                    i += WIFI_SUPPL_STATE_DISCONNECTED;
                } finally {
                    finishIteratingHistoryLocked();
                }
            }
            dumpHistoryLocked(pw, flags, histStart, true);
        }
        if (!filtering || (flags & WIFI_SUPPL_STATE_INACTIVE) != 0) {
            String[] lineArgs;
            if (apps != null) {
                ArrayList<String> pkgs;
                SparseArray<ArrayList<String>> uids = new SparseArray();
                for (i = WIFI_SUPPL_STATE_INVALID; i < apps.size(); i += WIFI_SUPPL_STATE_DISCONNECTED) {
                    ApplicationInfo ai = (ApplicationInfo) apps.get(i);
                    pkgs = (ArrayList) uids.get(ai.uid);
                    if (pkgs == null) {
                        pkgs = new ArrayList();
                        uids.put(ai.uid, pkgs);
                    }
                    pkgs.add(ai.packageName);
                }
                SparseArray<? extends Uid> uidStats = getUidStats();
                int NU = uidStats.size();
                lineArgs = new String[WIFI_SUPPL_STATE_INTERFACE_DISABLED];
                for (i = WIFI_SUPPL_STATE_INVALID; i < NU; i += WIFI_SUPPL_STATE_DISCONNECTED) {
                    int uid = uidStats.keyAt(i);
                    pkgs = (ArrayList) uids.get(uid);
                    if (pkgs != null) {
                        for (int j = WIFI_SUPPL_STATE_INVALID; j < pkgs.size(); j += WIFI_SUPPL_STATE_DISCONNECTED) {
                            lineArgs[WIFI_SUPPL_STATE_INVALID] = Integer.toString(uid);
                            lineArgs[WIFI_SUPPL_STATE_DISCONNECTED] = (String) pkgs.get(j);
                            dumpLine(pw, WIFI_SUPPL_STATE_INVALID, "i", UID_DATA, (Object[]) lineArgs);
                        }
                    }
                }
            }
            if (!(filtering && (flags & WIFI_SUPPL_STATE_INTERFACE_DISABLED) == 0)) {
                dumpDurationSteps(pw, DISCHARGE_STEP_DATA, getDischargeStepDurationsArray(), getNumDischargeStepDurations(), true);
                lineArgs = new String[WIFI_SUPPL_STATE_DISCONNECTED];
                long timeRemaining = computeBatteryTimeRemaining(SystemClock.elapsedRealtime());
                if (timeRemaining >= 0) {
                    lineArgs[WIFI_SUPPL_STATE_INVALID] = Long.toString(timeRemaining);
                    dumpLine(pw, WIFI_SUPPL_STATE_INVALID, "i", DISCHARGE_TIME_REMAIN_DATA, (Object[]) lineArgs);
                }
                dumpDurationSteps(pw, CHARGE_STEP_DATA, getChargeStepDurationsArray(), getNumChargeStepDurations(), true);
                timeRemaining = computeChargeTimeRemaining(SystemClock.elapsedRealtime());
                if (timeRemaining >= 0) {
                    lineArgs[WIFI_SUPPL_STATE_INVALID] = Long.toString(timeRemaining);
                    dumpLine(pw, WIFI_SUPPL_STATE_INVALID, "i", CHARGE_TIME_REMAIN_DATA, (Object[]) lineArgs);
                }
                dumpCheckinLocked(context, pw, (int) WIFI_SUPPL_STATE_INVALID, -1, (flags & DUMP_DEVICE_WIFI_ONLY) != 0 ? true : LOCAL_LOGV);
            }
            if (!filtering || (flags & WIFI_SUPPL_STATE_DISCONNECTED) != 0) {
                dumpCheckinLocked(context, pw, (int) WIFI_SUPPL_STATE_INTERFACE_DISABLED, -1, (flags & DUMP_DEVICE_WIFI_ONLY) != 0 ? true : LOCAL_LOGV);
            }
        }
    }
}
