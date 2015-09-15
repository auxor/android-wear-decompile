package android.service.notification;

import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.provider.CalendarContract.Instances;
import android.provider.Contacts;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Slog;
import android.view.KeyEvent;
import android.view.WindowManager.LayoutParams;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class ZenModeConfig implements Parcelable {
    private static final String ALLOW_ATT_CALLS = "calls";
    private static final String ALLOW_ATT_EVENTS = "events";
    private static final String ALLOW_ATT_FROM = "from";
    private static final String ALLOW_ATT_MESSAGES = "messages";
    private static final String ALLOW_TAG = "allow";
    public static final int[] ALL_DAYS;
    private static final String CONDITION_ATT_COMPONENT = "component";
    private static final String CONDITION_ATT_FLAGS = "flags";
    private static final String CONDITION_ATT_ICON = "icon";
    private static final String CONDITION_ATT_ID = "id";
    private static final String CONDITION_ATT_LINE1 = "line1";
    private static final String CONDITION_ATT_LINE2 = "line2";
    private static final String CONDITION_ATT_STATE = "state";
    private static final String CONDITION_ATT_SUMMARY = "summary";
    private static final String CONDITION_TAG = "condition";
    public static final String COUNTDOWN_PATH = "countdown";
    public static final Creator<ZenModeConfig> CREATOR;
    private static final boolean DEFAULT_ALLOW_EVENTS = true;
    public static final String DOWNTIME_PATH = "downtime";
    private static final String EXIT_CONDITION_ATT_COMPONENT = "component";
    private static final String EXIT_CONDITION_TAG = "exitCondition";
    public static final int MAX_SOURCE = 2;
    private static final int MINUTES_MS = 60000;
    public static final int[] MINUTE_BUCKETS;
    public static final String NEXT_ALARM_PATH = "next_alarm";
    private static final int SECONDS_MS = 1000;
    private static final String SLEEP_ATT_END_HR = "endHour";
    private static final String SLEEP_ATT_END_MIN = "endMin";
    private static final String SLEEP_ATT_MODE = "mode";
    private static final String SLEEP_ATT_NONE = "none";
    private static final String SLEEP_ATT_START_HR = "startHour";
    private static final String SLEEP_ATT_START_MIN = "startMin";
    public static final String SLEEP_MODE_DAYS_PREFIX = "days:";
    public static final String SLEEP_MODE_NIGHTS = "nights";
    public static final String SLEEP_MODE_WEEKNIGHTS = "weeknights";
    private static final String SLEEP_TAG = "sleep";
    public static final int SOURCE_ANYONE = 0;
    public static final int SOURCE_CONTACT = 1;
    public static final int SOURCE_STAR = 2;
    public static final String SYSTEM_AUTHORITY = "android";
    private static String TAG = null;
    public static final int[] WEEKNIGHT_DAYS;
    private static final int XML_VERSION = 1;
    private static final String ZEN_ATT_VERSION = "version";
    private static final String ZEN_TAG = "zen";
    private static final int ZERO_VALUE_MS = 10000;
    public boolean allowCalls;
    public boolean allowEvents;
    public int allowFrom;
    public boolean allowMessages;
    public ComponentName[] conditionComponents;
    public Uri[] conditionIds;
    public Condition exitCondition;
    public ComponentName exitConditionComponent;
    public int sleepEndHour;
    public int sleepEndMinute;
    public String sleepMode;
    public boolean sleepNone;
    public int sleepStartHour;
    public int sleepStartMinute;

    public static class DowntimeInfo {
        public int endHour;
        public int endMinute;
        public String mode;
        public boolean none;
        public int startHour;
        public int startMinute;

        public boolean equals(java.lang.Object r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.service.notification.ZenModeConfig.DowntimeInfo.equals(java.lang.Object):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.service.notification.ZenModeConfig.DowntimeInfo.equals(java.lang.Object):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.service.notification.ZenModeConfig.DowntimeInfo.equals(java.lang.Object):boolean");
        }

        public int hashCode() {
            return ZenModeConfig.SOURCE_ANYONE;
        }
    }

    static {
        TAG = "ZenModeConfig";
        ALL_DAYS = new int[]{XML_VERSION, SOURCE_STAR, 3, 4, 5, 6, 7};
        WEEKNIGHT_DAYS = new int[]{XML_VERSION, SOURCE_STAR, 3, 4, 5};
        MINUTE_BUCKETS = new int[]{15, 30, 45, 60, KeyEvent.KEYCODE_SYSRQ, KeyEvent.KEYCODE_STB_INPUT, LayoutParams.SOFT_INPUT_MASK_ADJUST, 480};
        CREATOR = new Creator<ZenModeConfig>() {
            public ZenModeConfig createFromParcel(Parcel source) {
                return new ZenModeConfig(source);
            }

            public ZenModeConfig[] newArray(int size) {
                return new ZenModeConfig[size];
            }
        };
    }

    public ZenModeConfig() {
        this.allowEvents = DEFAULT_ALLOW_EVENTS;
        this.allowFrom = SOURCE_ANYONE;
    }

    public ZenModeConfig(Parcel source) {
        boolean z;
        boolean z2 = DEFAULT_ALLOW_EVENTS;
        this.allowEvents = DEFAULT_ALLOW_EVENTS;
        this.allowFrom = SOURCE_ANYONE;
        if (source.readInt() == XML_VERSION) {
            z = DEFAULT_ALLOW_EVENTS;
        } else {
            z = false;
        }
        this.allowCalls = z;
        if (source.readInt() == XML_VERSION) {
            z = DEFAULT_ALLOW_EVENTS;
        } else {
            z = false;
        }
        this.allowMessages = z;
        if (source.readInt() == XML_VERSION) {
            z = DEFAULT_ALLOW_EVENTS;
        } else {
            z = false;
        }
        this.allowEvents = z;
        if (source.readInt() == XML_VERSION) {
            this.sleepMode = source.readString();
        }
        this.sleepStartHour = source.readInt();
        this.sleepStartMinute = source.readInt();
        this.sleepEndHour = source.readInt();
        this.sleepEndMinute = source.readInt();
        if (source.readInt() != XML_VERSION) {
            z2 = false;
        }
        this.sleepNone = z2;
        int len = source.readInt();
        if (len > 0) {
            this.conditionComponents = new ComponentName[len];
            source.readTypedArray(this.conditionComponents, ComponentName.CREATOR);
        }
        len = source.readInt();
        if (len > 0) {
            this.conditionIds = new Uri[len];
            source.readTypedArray(this.conditionIds, Uri.CREATOR);
        }
        this.allowFrom = source.readInt();
        this.exitCondition = (Condition) source.readParcelable(null);
        this.exitConditionComponent = (ComponentName) source.readParcelable(null);
    }

    public void writeToParcel(Parcel dest, int flags) {
        int i;
        int i2 = XML_VERSION;
        dest.writeInt(this.allowCalls ? XML_VERSION : SOURCE_ANYONE);
        if (this.allowMessages) {
            i = XML_VERSION;
        } else {
            i = SOURCE_ANYONE;
        }
        dest.writeInt(i);
        if (this.allowEvents) {
            i = XML_VERSION;
        } else {
            i = SOURCE_ANYONE;
        }
        dest.writeInt(i);
        if (this.sleepMode != null) {
            dest.writeInt(XML_VERSION);
            dest.writeString(this.sleepMode);
        } else {
            dest.writeInt(SOURCE_ANYONE);
        }
        dest.writeInt(this.sleepStartHour);
        dest.writeInt(this.sleepStartMinute);
        dest.writeInt(this.sleepEndHour);
        dest.writeInt(this.sleepEndMinute);
        if (!this.sleepNone) {
            i2 = SOURCE_ANYONE;
        }
        dest.writeInt(i2);
        if (this.conditionComponents == null || this.conditionComponents.length <= 0) {
            dest.writeInt(SOURCE_ANYONE);
        } else {
            dest.writeInt(this.conditionComponents.length);
            dest.writeTypedArray(this.conditionComponents, SOURCE_ANYONE);
        }
        if (this.conditionIds == null || this.conditionIds.length <= 0) {
            dest.writeInt(SOURCE_ANYONE);
        } else {
            dest.writeInt(this.conditionIds.length);
            dest.writeTypedArray(this.conditionIds, SOURCE_ANYONE);
        }
        dest.writeInt(this.allowFrom);
        dest.writeParcelable(this.exitCondition, SOURCE_ANYONE);
        dest.writeParcelable(this.exitConditionComponent, SOURCE_ANYONE);
    }

    public String toString() {
        String str = null;
        StringBuilder append = new StringBuilder(ZenModeConfig.class.getSimpleName()).append('[').append("allowCalls=").append(this.allowCalls).append(",allowMessages=").append(this.allowMessages).append(",allowFrom=").append(sourceToString(this.allowFrom)).append(",allowEvents=").append(this.allowEvents).append(",sleepMode=").append(this.sleepMode).append(",sleepStart=").append(this.sleepStartHour).append('.').append(this.sleepStartMinute).append(",sleepEnd=").append(this.sleepEndHour).append('.').append(this.sleepEndMinute).append(",sleepNone=").append(this.sleepNone).append(",conditionComponents=").append(this.conditionComponents == null ? null : TextUtils.join(",", this.conditionComponents)).append(",conditionIds=");
        if (this.conditionIds != null) {
            str = TextUtils.join(",", this.conditionIds);
        }
        return append.append(str).append(",exitCondition=").append(this.exitCondition).append(",exitConditionComponent=").append(this.exitConditionComponent).append(']').toString();
    }

    public static String sourceToString(int source) {
        switch (source) {
            case SOURCE_ANYONE /*0*/:
                return "anyone";
            case XML_VERSION /*1*/:
                return Contacts.AUTHORITY;
            case SOURCE_STAR /*2*/:
                return "stars";
            default:
                return "UNKNOWN";
        }
    }

    public boolean equals(Object o) {
        if (!(o instanceof ZenModeConfig)) {
            return false;
        }
        if (o == this) {
            return DEFAULT_ALLOW_EVENTS;
        }
        ZenModeConfig other = (ZenModeConfig) o;
        if (other.allowCalls == this.allowCalls && other.allowMessages == this.allowMessages && other.allowFrom == this.allowFrom && other.allowEvents == this.allowEvents && Objects.equals(other.sleepMode, this.sleepMode) && other.sleepNone == this.sleepNone && other.sleepStartHour == this.sleepStartHour && other.sleepStartMinute == this.sleepStartMinute && other.sleepEndHour == this.sleepEndHour && other.sleepEndMinute == this.sleepEndMinute && Objects.deepEquals(other.conditionComponents, this.conditionComponents) && Objects.deepEquals(other.conditionIds, this.conditionIds) && Objects.equals(other.exitCondition, this.exitCondition) && Objects.equals(other.exitConditionComponent, this.exitConditionComponent)) {
            return DEFAULT_ALLOW_EVENTS;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{Boolean.valueOf(this.allowCalls), Boolean.valueOf(this.allowMessages), Integer.valueOf(this.allowFrom), Boolean.valueOf(this.allowEvents), this.sleepMode, Boolean.valueOf(this.sleepNone), Integer.valueOf(this.sleepStartHour), Integer.valueOf(this.sleepStartMinute), Integer.valueOf(this.sleepEndHour), Integer.valueOf(this.sleepEndMinute), Integer.valueOf(Arrays.hashCode(this.conditionComponents)), Integer.valueOf(Arrays.hashCode(this.conditionIds)), this.exitCondition, this.exitConditionComponent});
    }

    public boolean isValid() {
        return (isValidHour(this.sleepStartHour) && isValidMinute(this.sleepStartMinute) && isValidHour(this.sleepEndHour) && isValidMinute(this.sleepEndMinute) && isValidSleepMode(this.sleepMode)) ? DEFAULT_ALLOW_EVENTS : false;
    }

    public static boolean isValidSleepMode(String sleepMode) {
        return (sleepMode == null || sleepMode.equals(SLEEP_MODE_NIGHTS) || sleepMode.equals(SLEEP_MODE_WEEKNIGHTS) || tryParseDays(sleepMode) != null) ? DEFAULT_ALLOW_EVENTS : false;
    }

    public static int[] tryParseDays(String sleepMode) {
        if (sleepMode == null) {
            return null;
        }
        sleepMode = sleepMode.trim();
        if (SLEEP_MODE_NIGHTS.equals(sleepMode)) {
            return ALL_DAYS;
        }
        if (SLEEP_MODE_WEEKNIGHTS.equals(sleepMode)) {
            return WEEKNIGHT_DAYS;
        }
        if (!sleepMode.startsWith(SLEEP_MODE_DAYS_PREFIX)) {
            return null;
        }
        if (sleepMode.equals(SLEEP_MODE_DAYS_PREFIX)) {
            return null;
        }
        String[] tokens = sleepMode.substring(SLEEP_MODE_DAYS_PREFIX.length()).split(",");
        if (tokens.length == 0) {
            return null;
        }
        int[] rt = new int[tokens.length];
        for (int i = SOURCE_ANYONE; i < tokens.length; i += XML_VERSION) {
            int day = tryParseInt(tokens[i], -1);
            if (day == -1) {
                return null;
            }
            rt[i] = day;
        }
        return rt;
    }

    private static int tryParseInt(String value, int defValue) {
        if (!TextUtils.isEmpty(value)) {
            try {
                defValue = Integer.valueOf(value).intValue();
            } catch (NumberFormatException e) {
            }
        }
        return defValue;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.service.notification.ZenModeConfig readXml(org.xmlpull.v1.XmlPullParser r17) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        r12 = r17.getEventType();
        r14 = 2;
        if (r12 == r14) goto L_0x0009;
    L_0x0007:
        r8 = 0;
    L_0x0008:
        return r8;
    L_0x0009:
        r11 = r17.getName();
        r14 = "zen";
        r14 = r14.equals(r11);
        if (r14 != 0) goto L_0x0018;
    L_0x0016:
        r8 = 0;
        goto L_0x0008;
    L_0x0018:
        r8 = new android.service.notification.ZenModeConfig;
        r8.<init>();
        r14 = "version";
        r15 = 1;
        r0 = r17;
        r13 = safeInt(r0, r14, r15);
        r2 = new java.util.ArrayList;
        r2.<init>();
        r4 = new java.util.ArrayList;
        r4.<init>();
    L_0x0031:
        r12 = r17.next();
        r14 = 1;
        if (r12 == r14) goto L_0x0184;
    L_0x0038:
        r11 = r17.getName();
        r14 = 3;
        if (r12 != r14) goto L_0x006b;
    L_0x003f:
        r14 = "zen";
        r14 = r14.equals(r11);
        if (r14 == 0) goto L_0x006b;
    L_0x0048:
        r14 = r2.isEmpty();
        if (r14 != 0) goto L_0x0008;
    L_0x004e:
        r14 = r2.size();
        r14 = new android.content.ComponentName[r14];
        r14 = r2.toArray(r14);
        r14 = (android.content.ComponentName[]) r14;
        r8.conditionComponents = r14;
        r14 = r4.size();
        r14 = new android.net.Uri[r14];
        r14 = r4.toArray(r14);
        r14 = (android.net.Uri[]) r14;
        r8.conditionIds = r14;
        goto L_0x0008;
    L_0x006b:
        r14 = 2;
        if (r12 != r14) goto L_0x0031;
    L_0x006e:
        r14 = "allow";
        r14 = r14.equals(r11);
        if (r14 == 0) goto L_0x00c9;
    L_0x0076:
        r14 = "calls";
        r15 = 0;
        r0 = r17;
        r14 = safeBoolean(r0, r14, r15);
        r8.allowCalls = r14;
        r14 = "messages";
        r15 = 0;
        r0 = r17;
        r14 = safeBoolean(r0, r14, r15);
        r8.allowMessages = r14;
        r14 = "events";
        r15 = 1;
        r0 = r17;
        r14 = safeBoolean(r0, r14, r15);
        r8.allowEvents = r14;
        r14 = "from";
        r15 = 0;
        r0 = r17;
        r14 = safeInt(r0, r14, r15);
        r8.allowFrom = r14;
        r14 = r8.allowFrom;
        if (r14 < 0) goto L_0x00ac;
    L_0x00a7:
        r14 = r8.allowFrom;
        r15 = 2;
        if (r14 <= r15) goto L_0x0031;
    L_0x00ac:
        r14 = new java.lang.IndexOutOfBoundsException;
        r15 = new java.lang.StringBuilder;
        r15.<init>();
        r16 = "bad source in config:";
        r15 = r15.append(r16);
        r0 = r8.allowFrom;
        r16 = r0;
        r15 = r15.append(r16);
        r15 = r15.toString();
        r14.<init>(r15);
        throw r14;
    L_0x00c9:
        r14 = "sleep";
        r14 = r14.equals(r11);
        if (r14 == 0) goto L_0x0142;
    L_0x00d2:
        r14 = 0;
        r15 = "mode";
        r0 = r17;
        r7 = r0.getAttributeValue(r14, r15);
        r14 = isValidSleepMode(r7);
        if (r14 == 0) goto L_0x0138;
    L_0x00e2:
        r8.sleepMode = r7;
        r14 = "none";
        r15 = 0;
        r0 = r17;
        r14 = safeBoolean(r0, r14, r15);
        r8.sleepNone = r14;
        r14 = "startHour";
        r15 = 0;
        r0 = r17;
        r9 = safeInt(r0, r14, r15);
        r14 = "startMin";
        r15 = 0;
        r0 = r17;
        r10 = safeInt(r0, r14, r15);
        r14 = "endHour";
        r15 = 0;
        r0 = r17;
        r5 = safeInt(r0, r14, r15);
        r14 = "endMin";
        r15 = 0;
        r0 = r17;
        r6 = safeInt(r0, r14, r15);
        r14 = isValidHour(r9);
        if (r14 == 0) goto L_0x013a;
    L_0x011c:
        r8.sleepStartHour = r9;
        r14 = isValidMinute(r10);
        if (r14 == 0) goto L_0x013c;
    L_0x0124:
        r8.sleepStartMinute = r10;
        r14 = isValidHour(r5);
        if (r14 == 0) goto L_0x013e;
    L_0x012c:
        r8.sleepEndHour = r5;
        r14 = isValidMinute(r6);
        if (r14 == 0) goto L_0x0140;
    L_0x0134:
        r8.sleepEndMinute = r6;
        goto L_0x0031;
    L_0x0138:
        r7 = 0;
        goto L_0x00e2;
    L_0x013a:
        r9 = 0;
        goto L_0x011c;
    L_0x013c:
        r10 = 0;
        goto L_0x0124;
    L_0x013e:
        r5 = 0;
        goto L_0x012c;
    L_0x0140:
        r6 = 0;
        goto L_0x0134;
    L_0x0142:
        r14 = "condition";
        r14 = r14.equals(r11);
        if (r14 == 0) goto L_0x0166;
    L_0x014a:
        r14 = "component";
        r0 = r17;
        r1 = safeComponentName(r0, r14);
        r14 = "id";
        r0 = r17;
        r3 = safeUri(r0, r14);
        if (r1 == 0) goto L_0x0031;
    L_0x015c:
        if (r3 == 0) goto L_0x0031;
    L_0x015e:
        r2.add(r1);
        r4.add(r3);
        goto L_0x0031;
    L_0x0166:
        r14 = "exitCondition";
        r14 = r14.equals(r11);
        if (r14 == 0) goto L_0x0031;
    L_0x016e:
        r14 = readConditionXml(r17);
        r8.exitCondition = r14;
        r14 = r8.exitCondition;
        if (r14 == 0) goto L_0x0031;
    L_0x0178:
        r14 = "component";
        r0 = r17;
        r14 = safeComponentName(r0, r14);
        r8.exitConditionComponent = r14;
        goto L_0x0031;
    L_0x0184:
        r14 = new java.lang.IllegalStateException;
        r15 = "Failed to reach END_DOCUMENT";
        r14.<init>(r15);
        throw r14;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.service.notification.ZenModeConfig.readXml(org.xmlpull.v1.XmlPullParser):android.service.notification.ZenModeConfig");
    }

    public void writeXml(XmlSerializer out) throws IOException {
        out.startTag(null, ZEN_TAG);
        out.attribute(null, ZEN_ATT_VERSION, Integer.toString(XML_VERSION));
        out.startTag(null, ALLOW_TAG);
        out.attribute(null, ALLOW_ATT_CALLS, Boolean.toString(this.allowCalls));
        out.attribute(null, ALLOW_ATT_MESSAGES, Boolean.toString(this.allowMessages));
        out.attribute(null, ALLOW_ATT_EVENTS, Boolean.toString(this.allowEvents));
        out.attribute(null, ALLOW_ATT_FROM, Integer.toString(this.allowFrom));
        out.endTag(null, ALLOW_TAG);
        out.startTag(null, SLEEP_TAG);
        if (this.sleepMode != null) {
            out.attribute(null, SLEEP_ATT_MODE, this.sleepMode);
        }
        out.attribute(null, SLEEP_ATT_NONE, Boolean.toString(this.sleepNone));
        out.attribute(null, SLEEP_ATT_START_HR, Integer.toString(this.sleepStartHour));
        out.attribute(null, SLEEP_ATT_START_MIN, Integer.toString(this.sleepStartMinute));
        out.attribute(null, SLEEP_ATT_END_HR, Integer.toString(this.sleepEndHour));
        out.attribute(null, SLEEP_ATT_END_MIN, Integer.toString(this.sleepEndMinute));
        out.endTag(null, SLEEP_TAG);
        if (!(this.conditionComponents == null || this.conditionIds == null || this.conditionComponents.length != this.conditionIds.length)) {
            for (int i = SOURCE_ANYONE; i < this.conditionComponents.length; i += XML_VERSION) {
                out.startTag(null, CONDITION_TAG);
                out.attribute(null, EXIT_CONDITION_ATT_COMPONENT, this.conditionComponents[i].flattenToString());
                out.attribute(null, CONDITION_ATT_ID, this.conditionIds[i].toString());
                out.endTag(null, CONDITION_TAG);
            }
        }
        if (!(this.exitCondition == null || this.exitConditionComponent == null)) {
            out.startTag(null, EXIT_CONDITION_TAG);
            out.attribute(null, EXIT_CONDITION_ATT_COMPONENT, this.exitConditionComponent.flattenToString());
            writeConditionXml(this.exitCondition, out);
            out.endTag(null, EXIT_CONDITION_TAG);
        }
        out.endTag(null, ZEN_TAG);
    }

    public static Condition readConditionXml(XmlPullParser parser) {
        try {
            return new Condition(safeUri(parser, CONDITION_ATT_ID), parser.getAttributeValue(null, CONDITION_ATT_SUMMARY), parser.getAttributeValue(null, CONDITION_ATT_LINE1), parser.getAttributeValue(null, CONDITION_ATT_LINE2), safeInt(parser, CONDITION_ATT_ICON, -1), safeInt(parser, CONDITION_ATT_STATE, -1), safeInt(parser, CONDITION_ATT_FLAGS, -1));
        } catch (IllegalArgumentException e) {
            Slog.w(TAG, "Unable to read condition xml", e);
            return null;
        }
    }

    public static void writeConditionXml(Condition c, XmlSerializer out) throws IOException {
        out.attribute(null, CONDITION_ATT_ID, c.id.toString());
        out.attribute(null, CONDITION_ATT_SUMMARY, c.summary);
        out.attribute(null, CONDITION_ATT_LINE1, c.line1);
        out.attribute(null, CONDITION_ATT_LINE2, c.line2);
        out.attribute(null, CONDITION_ATT_ICON, Integer.toString(c.icon));
        out.attribute(null, CONDITION_ATT_STATE, Integer.toString(c.state));
        out.attribute(null, CONDITION_ATT_FLAGS, Integer.toString(c.flags));
    }

    public static boolean isValidHour(int val) {
        return (val < 0 || val >= 24) ? false : DEFAULT_ALLOW_EVENTS;
    }

    public static boolean isValidMinute(int val) {
        return (val < 0 || val >= 60) ? false : DEFAULT_ALLOW_EVENTS;
    }

    private static boolean safeBoolean(XmlPullParser parser, String att, boolean defValue) {
        String val = parser.getAttributeValue(null, att);
        return TextUtils.isEmpty(val) ? defValue : Boolean.valueOf(val).booleanValue();
    }

    private static int safeInt(XmlPullParser parser, String att, int defValue) {
        return tryParseInt(parser.getAttributeValue(null, att), defValue);
    }

    private static ComponentName safeComponentName(XmlPullParser parser, String att) {
        String val = parser.getAttributeValue(null, att);
        if (TextUtils.isEmpty(val)) {
            return null;
        }
        return ComponentName.unflattenFromString(val);
    }

    private static Uri safeUri(XmlPullParser parser, String att) {
        String val = parser.getAttributeValue(null, att);
        if (TextUtils.isEmpty(val)) {
            return null;
        }
        return Uri.parse(val);
    }

    public int describeContents() {
        return SOURCE_ANYONE;
    }

    public ZenModeConfig copy() {
        Parcel parcel = Parcel.obtain();
        try {
            writeToParcel(parcel, SOURCE_ANYONE);
            parcel.setDataPosition(SOURCE_ANYONE);
            ZenModeConfig zenModeConfig = new ZenModeConfig(parcel);
            return zenModeConfig;
        } finally {
            parcel.recycle();
        }
    }

    public DowntimeInfo toDowntimeInfo() {
        DowntimeInfo downtime = new DowntimeInfo();
        downtime.startHour = this.sleepStartHour;
        downtime.startMinute = this.sleepStartMinute;
        downtime.endHour = this.sleepEndHour;
        downtime.endMinute = this.sleepEndMinute;
        downtime.mode = this.sleepMode;
        downtime.none = this.sleepNone;
        return downtime;
    }

    public static Condition toTimeCondition(Context context, int minutesFromNow, int userHandle) {
        long now = System.currentTimeMillis();
        return toTimeCondition(context, now + (minutesFromNow == 0 ? 10000 : (long) (MINUTES_MS * minutesFromNow)), minutesFromNow, now, userHandle);
    }

    public static Condition toTimeCondition(Context context, long time, int minutes, long now, int userHandle) {
        int num;
        int summaryResId;
        int line1ResId;
        if (minutes < 60) {
            num = minutes;
            summaryResId = 18087961;
            line1ResId = 18087963;
        } else {
            num = Math.round(((float) minutes) / SensorManager.MAGNETIC_FIELD_EARTH_MAX);
            summaryResId = 18087962;
            line1ResId = 18087964;
        }
        CharSequence formattedTime = DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), DateFormat.is24HourFormat(context, userHandle) ? "Hm" : "hma"), time);
        Resources res = context.getResources();
        Object[] objArr = new Object[SOURCE_STAR];
        objArr[SOURCE_ANYONE] = Integer.valueOf(num);
        objArr[XML_VERSION] = formattedTime;
        String summary = res.getQuantityString(summaryResId, num, objArr);
        objArr = new Object[SOURCE_STAR];
        objArr[SOURCE_ANYONE] = Integer.valueOf(num);
        objArr[XML_VERSION] = formattedTime;
        String line1 = res.getQuantityString(line1ResId, num, objArr);
        Object[] objArr2 = new Object[XML_VERSION];
        objArr2[SOURCE_ANYONE] = formattedTime;
        return new Condition(toCountdownConditionId(time), summary, line1, res.getString(17041046, objArr2), SOURCE_ANYONE, XML_VERSION, XML_VERSION);
    }

    public static Uri toCountdownConditionId(long time) {
        return new Builder().scheme(CONDITION_TAG).authority(SYSTEM_AUTHORITY).appendPath(COUNTDOWN_PATH).appendPath(Long.toString(time)).build();
    }

    public static long tryParseCountdownConditionId(Uri conditionId) {
        long j = 0;
        if (Condition.isValidId(conditionId, SYSTEM_AUTHORITY) && conditionId.getPathSegments().size() == SOURCE_STAR && COUNTDOWN_PATH.equals(conditionId.getPathSegments().get(SOURCE_ANYONE))) {
            try {
                j = Long.parseLong((String) conditionId.getPathSegments().get(XML_VERSION));
            } catch (RuntimeException e) {
                Slog.w(TAG, "Error parsing countdown condition: " + conditionId, e);
            }
        }
        return j;
    }

    public static boolean isValidCountdownConditionId(Uri conditionId) {
        return tryParseCountdownConditionId(conditionId) != 0 ? DEFAULT_ALLOW_EVENTS : false;
    }

    public static Uri toDowntimeConditionId(DowntimeInfo downtime) {
        return new Builder().scheme(CONDITION_TAG).authority(SYSTEM_AUTHORITY).appendPath(DOWNTIME_PATH).appendQueryParameter("start", downtime.startHour + "." + downtime.startMinute).appendQueryParameter(Instances.END, downtime.endHour + "." + downtime.endMinute).appendQueryParameter(SLEEP_ATT_MODE, downtime.mode).appendQueryParameter(SLEEP_ATT_NONE, Boolean.toString(downtime.none)).build();
    }

    public static DowntimeInfo tryParseDowntimeConditionId(Uri conditionId) {
        if (!Condition.isValidId(conditionId, SYSTEM_AUTHORITY) || conditionId.getPathSegments().size() != XML_VERSION || !DOWNTIME_PATH.equals(conditionId.getPathSegments().get(SOURCE_ANYONE))) {
            return null;
        }
        int[] start = tryParseHourAndMinute(conditionId.getQueryParameter("start"));
        int[] end = tryParseHourAndMinute(conditionId.getQueryParameter(Instances.END));
        if (start == null || end == null) {
            return null;
        }
        DowntimeInfo downtime = new DowntimeInfo();
        downtime.startHour = start[SOURCE_ANYONE];
        downtime.startMinute = start[XML_VERSION];
        downtime.endHour = end[SOURCE_ANYONE];
        downtime.endMinute = end[XML_VERSION];
        downtime.mode = conditionId.getQueryParameter(SLEEP_ATT_MODE);
        downtime.none = Boolean.toString(DEFAULT_ALLOW_EVENTS).equals(conditionId.getQueryParameter(SLEEP_ATT_NONE));
        return downtime;
    }

    private static int[] tryParseHourAndMinute(String value) {
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        int i = value.indexOf(46);
        if (i < XML_VERSION || i >= value.length() - 1) {
            return null;
        }
        int hour = tryParseInt(value.substring(SOURCE_ANYONE, i), -1);
        int minute = tryParseInt(value.substring(i + XML_VERSION), -1);
        if (!isValidHour(hour) || !isValidMinute(minute)) {
            return null;
        }
        int[] iArr = new int[SOURCE_STAR];
        iArr[SOURCE_ANYONE] = hour;
        iArr[XML_VERSION] = minute;
        return iArr;
    }

    public static boolean isValidDowntimeConditionId(Uri conditionId) {
        return tryParseDowntimeConditionId(conditionId) != null ? DEFAULT_ALLOW_EVENTS : false;
    }
}
