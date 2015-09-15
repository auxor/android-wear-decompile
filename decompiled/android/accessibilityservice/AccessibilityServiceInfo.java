package android.accessibilityservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.SparseArray;
import android.util.TypedValue;
import android.util.Xml;
import android.view.accessibility.AccessibilityEvent;
import com.android.internal.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public class AccessibilityServiceInfo implements Parcelable {
    public static final int CAPABILITY_CAN_REQUEST_ENHANCED_WEB_ACCESSIBILITY = 4;
    public static final int CAPABILITY_CAN_REQUEST_FILTER_KEY_EVENTS = 8;
    public static final int CAPABILITY_CAN_REQUEST_TOUCH_EXPLORATION = 2;
    public static final int CAPABILITY_CAN_RETRIEVE_WINDOW_CONTENT = 1;
    public static final Creator<AccessibilityServiceInfo> CREATOR;
    public static final int DEFAULT = 1;
    public static final int FEEDBACK_ALL_MASK = -1;
    public static final int FEEDBACK_AUDIBLE = 4;
    public static final int FEEDBACK_BRAILLE = 32;
    public static final int FEEDBACK_GENERIC = 16;
    public static final int FEEDBACK_HAPTIC = 2;
    public static final int FEEDBACK_SPOKEN = 1;
    public static final int FEEDBACK_VISUAL = 8;
    public static final int FLAG_INCLUDE_NOT_IMPORTANT_VIEWS = 2;
    public static final int FLAG_REPORT_VIEW_IDS = 16;
    public static final int FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY = 8;
    public static final int FLAG_REQUEST_FILTER_KEY_EVENTS = 32;
    public static final int FLAG_REQUEST_TOUCH_EXPLORATION_MODE = 4;
    public static final int FLAG_RETRIEVE_INTERACTIVE_WINDOWS = 64;
    private static final String TAG_ACCESSIBILITY_SERVICE = "accessibility-service";
    private static final SparseArray<CapabilityInfo> sAvailableCapabilityInfos;
    public int eventTypes;
    public int feedbackType;
    public int flags;
    private int mCapabilities;
    private int mDescriptionResId;
    private String mId;
    private String mNonLocalizedDescription;
    private ResolveInfo mResolveInfo;
    private String mSettingsActivityName;
    public long notificationTimeout;
    public String[] packageNames;

    public static final class CapabilityInfo {
        public final int capability;
        public final int descResId;
        public final int titleResId;

        public CapabilityInfo(int r1, int r2, int r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.accessibilityservice.AccessibilityServiceInfo.CapabilityInfo.<init>(int, int, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.accessibilityservice.AccessibilityServiceInfo.CapabilityInfo.<init>(int, int, int):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.accessibilityservice.AccessibilityServiceInfo.CapabilityInfo.<init>(int, int, int):void");
        }
    }

    static {
        sAvailableCapabilityInfos = new SparseArray();
        sAvailableCapabilityInfos.put(FEEDBACK_SPOKEN, new CapabilityInfo(FEEDBACK_SPOKEN, 17039700, 17039701));
        sAvailableCapabilityInfos.put(FLAG_INCLUDE_NOT_IMPORTANT_VIEWS, new CapabilityInfo(FLAG_INCLUDE_NOT_IMPORTANT_VIEWS, 17039702, 17039703));
        sAvailableCapabilityInfos.put(FLAG_REQUEST_TOUCH_EXPLORATION_MODE, new CapabilityInfo(FLAG_REQUEST_TOUCH_EXPLORATION_MODE, 17039704, 17039705));
        sAvailableCapabilityInfos.put(FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY, new CapabilityInfo(FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY, 17039706, 17039707));
        CREATOR = new Creator<AccessibilityServiceInfo>() {
            public AccessibilityServiceInfo createFromParcel(Parcel parcel) {
                AccessibilityServiceInfo info = new AccessibilityServiceInfo();
                info.initFromParcel(parcel);
                return info;
            }

            public AccessibilityServiceInfo[] newArray(int size) {
                return new AccessibilityServiceInfo[size];
            }
        };
    }

    public AccessibilityServiceInfo(ResolveInfo resolveInfo, Context context) throws XmlPullParserException, IOException {
        ServiceInfo serviceInfo = resolveInfo.serviceInfo;
        this.mId = new ComponentName(serviceInfo.packageName, serviceInfo.name).flattenToShortString();
        this.mResolveInfo = resolveInfo;
        XmlResourceParser parser = null;
        try {
            PackageManager packageManager = context.getPackageManager();
            parser = serviceInfo.loadXmlMetaData(packageManager, AccessibilityService.SERVICE_META_DATA);
            if (parser != null) {
                int type = 0;
                while (type != FEEDBACK_SPOKEN && type != FLAG_INCLUDE_NOT_IMPORTANT_VIEWS) {
                    type = parser.next();
                }
                if (TAG_ACCESSIBILITY_SERVICE.equals(parser.getName())) {
                    TypedArray asAttributes = packageManager.getResourcesForApplication(serviceInfo.applicationInfo).obtainAttributes(Xml.asAttributeSet(parser), R.styleable.AccessibilityService);
                    this.eventTypes = asAttributes.getInt(FLAG_INCLUDE_NOT_IMPORTANT_VIEWS, 0);
                    String packageNamez = asAttributes.getString(3);
                    if (packageNamez != null) {
                        this.packageNames = packageNamez.split("(\\s)*,(\\s)*");
                    }
                    this.feedbackType = asAttributes.getInt(FLAG_REQUEST_TOUCH_EXPLORATION_MODE, 0);
                    this.notificationTimeout = (long) asAttributes.getInt(5, 0);
                    this.flags = asAttributes.getInt(6, 0);
                    this.mSettingsActivityName = asAttributes.getString(FEEDBACK_SPOKEN);
                    if (asAttributes.getBoolean(7, false)) {
                        this.mCapabilities |= FEEDBACK_SPOKEN;
                    }
                    if (asAttributes.getBoolean(FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY, false)) {
                        this.mCapabilities |= FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
                    }
                    if (asAttributes.getBoolean(9, false)) {
                        this.mCapabilities |= FLAG_REQUEST_TOUCH_EXPLORATION_MODE;
                    }
                    if (asAttributes.getBoolean(10, false)) {
                        this.mCapabilities |= FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY;
                    }
                    TypedValue peekedValue = asAttributes.peekValue(0);
                    if (peekedValue != null) {
                        this.mDescriptionResId = peekedValue.resourceId;
                        CharSequence nonLocalizedDescription = peekedValue.coerceToString();
                        if (nonLocalizedDescription != null) {
                            this.mNonLocalizedDescription = nonLocalizedDescription.toString().trim();
                        }
                    }
                    asAttributes.recycle();
                    if (parser != null) {
                        parser.close();
                        return;
                    }
                    return;
                }
                throw new XmlPullParserException("Meta-data does not start withaccessibility-service tag");
            } else if (parser != null) {
                parser.close();
            }
        } catch (NameNotFoundException e) {
            throw new XmlPullParserException("Unable to create context for: " + serviceInfo.packageName);
        } catch (Throwable th) {
            if (parser != null) {
                parser.close();
            }
        }
    }

    public void updateDynamicallyConfigurableProperties(AccessibilityServiceInfo other) {
        this.eventTypes = other.eventTypes;
        this.packageNames = other.packageNames;
        this.feedbackType = other.feedbackType;
        this.notificationTimeout = other.notificationTimeout;
        this.flags = other.flags;
    }

    public void setComponentName(ComponentName component) {
        this.mId = component.flattenToShortString();
    }

    public String getId() {
        return this.mId;
    }

    public ResolveInfo getResolveInfo() {
        return this.mResolveInfo;
    }

    public String getSettingsActivityName() {
        return this.mSettingsActivityName;
    }

    public boolean getCanRetrieveWindowContent() {
        return (this.mCapabilities & FEEDBACK_SPOKEN) != 0;
    }

    public int getCapabilities() {
        return this.mCapabilities;
    }

    public void setCapabilities(int capabilities) {
        this.mCapabilities = capabilities;
    }

    public String getDescription() {
        return this.mNonLocalizedDescription;
    }

    public String loadDescription(PackageManager packageManager) {
        if (this.mDescriptionResId == 0) {
            return this.mNonLocalizedDescription;
        }
        ServiceInfo serviceInfo = this.mResolveInfo.serviceInfo;
        CharSequence description = packageManager.getText(serviceInfo.packageName, this.mDescriptionResId, serviceInfo.applicationInfo);
        if (description != null) {
            return description.toString().trim();
        }
        return null;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flagz) {
        parcel.writeInt(this.eventTypes);
        parcel.writeStringArray(this.packageNames);
        parcel.writeInt(this.feedbackType);
        parcel.writeLong(this.notificationTimeout);
        parcel.writeInt(this.flags);
        parcel.writeString(this.mId);
        parcel.writeParcelable(this.mResolveInfo, 0);
        parcel.writeString(this.mSettingsActivityName);
        parcel.writeInt(this.mCapabilities);
        parcel.writeInt(this.mDescriptionResId);
        parcel.writeString(this.mNonLocalizedDescription);
    }

    private void initFromParcel(Parcel parcel) {
        this.eventTypes = parcel.readInt();
        this.packageNames = parcel.readStringArray();
        this.feedbackType = parcel.readInt();
        this.notificationTimeout = parcel.readLong();
        this.flags = parcel.readInt();
        this.mId = parcel.readString();
        this.mResolveInfo = (ResolveInfo) parcel.readParcelable(null);
        this.mSettingsActivityName = parcel.readString();
        this.mCapabilities = parcel.readInt();
        this.mDescriptionResId = parcel.readInt();
        this.mNonLocalizedDescription = parcel.readString();
    }

    public int hashCode() {
        return (this.mId == null ? 0 : this.mId.hashCode()) + 31;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AccessibilityServiceInfo other = (AccessibilityServiceInfo) obj;
        if (this.mId == null) {
            if (other.mId != null) {
                return false;
            }
            return true;
        } else if (this.mId.equals(other.mId)) {
            return true;
        } else {
            return false;
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        appendEventTypes(stringBuilder, this.eventTypes);
        stringBuilder.append(", ");
        appendPackageNames(stringBuilder, this.packageNames);
        stringBuilder.append(", ");
        appendFeedbackTypes(stringBuilder, this.feedbackType);
        stringBuilder.append(", ");
        stringBuilder.append("notificationTimeout: ").append(this.notificationTimeout);
        stringBuilder.append(", ");
        appendFlags(stringBuilder, this.flags);
        stringBuilder.append(", ");
        stringBuilder.append("id: ").append(this.mId);
        stringBuilder.append(", ");
        stringBuilder.append("resolveInfo: ").append(this.mResolveInfo);
        stringBuilder.append(", ");
        stringBuilder.append("settingsActivityName: ").append(this.mSettingsActivityName);
        stringBuilder.append(", ");
        appendCapabilities(stringBuilder, this.mCapabilities);
        return stringBuilder.toString();
    }

    private static void appendFeedbackTypes(StringBuilder stringBuilder, int feedbackTypes) {
        stringBuilder.append("feedbackTypes:");
        stringBuilder.append("[");
        while (feedbackTypes != 0) {
            int feedbackTypeBit = FEEDBACK_SPOKEN << Integer.numberOfTrailingZeros(feedbackTypes);
            stringBuilder.append(feedbackTypeToString(feedbackTypeBit));
            feedbackTypes &= feedbackTypeBit ^ FEEDBACK_ALL_MASK;
            if (feedbackTypes != 0) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("]");
    }

    private static void appendPackageNames(StringBuilder stringBuilder, String[] packageNames) {
        stringBuilder.append("packageNames:");
        stringBuilder.append("[");
        if (packageNames != null) {
            int packageNameCount = packageNames.length;
            for (int i = 0; i < packageNameCount; i += FEEDBACK_SPOKEN) {
                stringBuilder.append(packageNames[i]);
                if (i < packageNameCount + FEEDBACK_ALL_MASK) {
                    stringBuilder.append(", ");
                }
            }
        }
        stringBuilder.append("]");
    }

    private static void appendEventTypes(StringBuilder stringBuilder, int eventTypes) {
        stringBuilder.append("eventTypes:");
        stringBuilder.append("[");
        while (eventTypes != 0) {
            int eventTypeBit = FEEDBACK_SPOKEN << Integer.numberOfTrailingZeros(eventTypes);
            stringBuilder.append(AccessibilityEvent.eventTypeToString(eventTypeBit));
            eventTypes &= eventTypeBit ^ FEEDBACK_ALL_MASK;
            if (eventTypes != 0) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("]");
    }

    private static void appendFlags(StringBuilder stringBuilder, int flags) {
        stringBuilder.append("flags:");
        stringBuilder.append("[");
        while (flags != 0) {
            int flagBit = FEEDBACK_SPOKEN << Integer.numberOfTrailingZeros(flags);
            stringBuilder.append(flagToString(flagBit));
            flags &= flagBit ^ FEEDBACK_ALL_MASK;
            if (flags != 0) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("]");
    }

    private static void appendCapabilities(StringBuilder stringBuilder, int capabilities) {
        stringBuilder.append("capabilities:");
        stringBuilder.append("[");
        while (capabilities != 0) {
            int capabilityBit = FEEDBACK_SPOKEN << Integer.numberOfTrailingZeros(capabilities);
            stringBuilder.append(capabilityToString(capabilityBit));
            capabilities &= capabilityBit ^ FEEDBACK_ALL_MASK;
            if (capabilities != 0) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("]");
    }

    public static String feedbackTypeToString(int feedbackType) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        while (feedbackType != 0) {
            int feedbackTypeFlag = FEEDBACK_SPOKEN << Integer.numberOfTrailingZeros(feedbackType);
            feedbackType &= feedbackTypeFlag ^ FEEDBACK_ALL_MASK;
            switch (feedbackTypeFlag) {
                case FEEDBACK_SPOKEN /*1*/:
                    if (builder.length() > FEEDBACK_SPOKEN) {
                        builder.append(", ");
                    }
                    builder.append("FEEDBACK_SPOKEN");
                    break;
                case FLAG_INCLUDE_NOT_IMPORTANT_VIEWS /*2*/:
                    if (builder.length() > FEEDBACK_SPOKEN) {
                        builder.append(", ");
                    }
                    builder.append("FEEDBACK_HAPTIC");
                    break;
                case FLAG_REQUEST_TOUCH_EXPLORATION_MODE /*4*/:
                    if (builder.length() > FEEDBACK_SPOKEN) {
                        builder.append(", ");
                    }
                    builder.append("FEEDBACK_AUDIBLE");
                    break;
                case FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY /*8*/:
                    if (builder.length() > FEEDBACK_SPOKEN) {
                        builder.append(", ");
                    }
                    builder.append("FEEDBACK_VISUAL");
                    break;
                case FLAG_REPORT_VIEW_IDS /*16*/:
                    if (builder.length() > FEEDBACK_SPOKEN) {
                        builder.append(", ");
                    }
                    builder.append("FEEDBACK_GENERIC");
                    break;
                case FLAG_REQUEST_FILTER_KEY_EVENTS /*32*/:
                    if (builder.length() > FEEDBACK_SPOKEN) {
                        builder.append(", ");
                    }
                    builder.append("FEEDBACK_BRAILLE");
                    break;
                default:
                    break;
            }
        }
        builder.append("]");
        return builder.toString();
    }

    public static String flagToString(int flag) {
        switch (flag) {
            case FEEDBACK_SPOKEN /*1*/:
                return "DEFAULT";
            case FLAG_INCLUDE_NOT_IMPORTANT_VIEWS /*2*/:
                return "FLAG_INCLUDE_NOT_IMPORTANT_VIEWS";
            case FLAG_REQUEST_TOUCH_EXPLORATION_MODE /*4*/:
                return "FLAG_REQUEST_TOUCH_EXPLORATION_MODE";
            case FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY /*8*/:
                return "FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY";
            case FLAG_REPORT_VIEW_IDS /*16*/:
                return "FLAG_REPORT_VIEW_IDS";
            case FLAG_REQUEST_FILTER_KEY_EVENTS /*32*/:
                return "FLAG_REQUEST_FILTER_KEY_EVENTS";
            case FLAG_RETRIEVE_INTERACTIVE_WINDOWS /*64*/:
                return "FLAG_RETRIEVE_INTERACTIVE_WINDOWS";
            default:
                return null;
        }
    }

    public static String capabilityToString(int capability) {
        switch (capability) {
            case FEEDBACK_SPOKEN /*1*/:
                return "CAPABILITY_CAN_RETRIEVE_WINDOW_CONTENT";
            case FLAG_INCLUDE_NOT_IMPORTANT_VIEWS /*2*/:
                return "CAPABILITY_CAN_REQUEST_TOUCH_EXPLORATION";
            case FLAG_REQUEST_TOUCH_EXPLORATION_MODE /*4*/:
                return "CAPABILITY_CAN_REQUEST_ENHANCED_WEB_ACCESSIBILITY";
            case FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY /*8*/:
                return "CAPABILITY_CAN_FILTER_KEY_EVENTS";
            default:
                return "UNKNOWN";
        }
    }

    public List<CapabilityInfo> getCapabilityInfos() {
        if (this.mCapabilities == 0) {
            return Collections.emptyList();
        }
        int capabilities = this.mCapabilities;
        List<CapabilityInfo> capabilityInfos = new ArrayList();
        while (capabilities != 0) {
            int capabilityBit = FEEDBACK_SPOKEN << Integer.numberOfTrailingZeros(capabilities);
            capabilities &= capabilityBit ^ FEEDBACK_ALL_MASK;
            CapabilityInfo capabilityInfo = (CapabilityInfo) sAvailableCapabilityInfos.get(capabilityBit);
            if (capabilityInfo != null) {
                capabilityInfos.add(capabilityInfo);
            }
        }
        return capabilityInfos;
    }
}
