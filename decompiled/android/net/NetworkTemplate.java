package android.net;

import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import com.android.internal.util.ArrayUtils;
import java.util.Arrays;
import java.util.Objects;

public class NetworkTemplate implements Parcelable {
    public static final Creator<NetworkTemplate> CREATOR;
    private static final int[] DATA_USAGE_NETWORK_TYPES;
    public static final int MATCH_BLUETOOTH = 8;
    public static final int MATCH_ETHERNET = 5;
    @Deprecated
    public static final int MATCH_MOBILE_3G_LOWER = 2;
    @Deprecated
    public static final int MATCH_MOBILE_4G = 3;
    public static final int MATCH_MOBILE_ALL = 1;
    public static final int MATCH_MOBILE_WILDCARD = 6;
    public static final int MATCH_WIFI = 4;
    public static final int MATCH_WIFI_WILDCARD = 7;
    private static boolean sForceAllNetworkTypes;
    private final int mMatchRule;
    private final String[] mMatchSubscriberIds;
    private final String mNetworkId;
    private final String mSubscriberId;

    static {
        DATA_USAGE_NETWORK_TYPES = Resources.getSystem().getIntArray(17235984);
        sForceAllNetworkTypes = false;
        CREATOR = new Creator<NetworkTemplate>() {
            public NetworkTemplate createFromParcel(Parcel in) {
                return new NetworkTemplate(null);
            }

            public NetworkTemplate[] newArray(int size) {
                return new NetworkTemplate[size];
            }
        };
    }

    public static void forceAllNetworkTypes() {
        sForceAllNetworkTypes = true;
    }

    public static NetworkTemplate buildTemplateMobileAll(String subscriberId) {
        return new NetworkTemplate(MATCH_MOBILE_ALL, subscriberId, null);
    }

    @Deprecated
    public static NetworkTemplate buildTemplateMobile3gLower(String subscriberId) {
        return new NetworkTemplate(MATCH_MOBILE_3G_LOWER, subscriberId, null);
    }

    @Deprecated
    public static NetworkTemplate buildTemplateMobile4g(String subscriberId) {
        return new NetworkTemplate(MATCH_MOBILE_4G, subscriberId, null);
    }

    public static NetworkTemplate buildTemplateMobileWildcard() {
        return new NetworkTemplate(MATCH_MOBILE_WILDCARD, null, null);
    }

    public static NetworkTemplate buildTemplateWifiWildcard() {
        return new NetworkTemplate(MATCH_WIFI_WILDCARD, null, null);
    }

    @Deprecated
    public static NetworkTemplate buildTemplateWifi() {
        return buildTemplateWifiWildcard();
    }

    public static NetworkTemplate buildTemplateWifi(String networkId) {
        return new NetworkTemplate(MATCH_WIFI, null, networkId);
    }

    public static NetworkTemplate buildTemplateEthernet() {
        return new NetworkTemplate(MATCH_ETHERNET, null, null);
    }

    public static NetworkTemplate buildTemplateBluetooth() {
        return new NetworkTemplate(MATCH_BLUETOOTH, null, null);
    }

    public NetworkTemplate(int matchRule, String subscriberId, String networkId) {
        String[] strArr = new String[MATCH_MOBILE_ALL];
        strArr[0] = subscriberId;
        this(matchRule, subscriberId, strArr, networkId);
    }

    public NetworkTemplate(int matchRule, String subscriberId, String[] matchSubscriberIds, String networkId) {
        this.mMatchRule = matchRule;
        this.mSubscriberId = subscriberId;
        this.mMatchSubscriberIds = matchSubscriberIds;
        this.mNetworkId = networkId;
    }

    private NetworkTemplate(Parcel in) {
        this.mMatchRule = in.readInt();
        this.mSubscriberId = in.readString();
        this.mMatchSubscriberIds = in.createStringArray();
        this.mNetworkId = in.readString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mMatchRule);
        dest.writeString(this.mSubscriberId);
        dest.writeStringArray(this.mMatchSubscriberIds);
        dest.writeString(this.mNetworkId);
    }

    public int describeContents() {
        return 0;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("NetworkTemplate: ");
        builder.append("matchRule=").append(getMatchRuleName(this.mMatchRule));
        if (this.mSubscriberId != null) {
            builder.append(", subscriberId=").append(NetworkIdentity.scrubSubscriberId(this.mSubscriberId));
        }
        if (this.mMatchSubscriberIds != null) {
            builder.append(", matchSubscriberIds=").append(Arrays.toString(NetworkIdentity.scrubSubscriberId(this.mMatchSubscriberIds)));
        }
        if (this.mNetworkId != null) {
            builder.append(", networkId=").append(this.mNetworkId);
        }
        return builder.toString();
    }

    public int hashCode() {
        Object[] objArr = new Object[MATCH_MOBILE_4G];
        objArr[0] = Integer.valueOf(this.mMatchRule);
        objArr[MATCH_MOBILE_ALL] = this.mSubscriberId;
        objArr[MATCH_MOBILE_3G_LOWER] = this.mNetworkId;
        return Objects.hash(objArr);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof NetworkTemplate)) {
            return false;
        }
        NetworkTemplate other = (NetworkTemplate) obj;
        if (this.mMatchRule == other.mMatchRule && Objects.equals(this.mSubscriberId, other.mSubscriberId) && Objects.equals(this.mNetworkId, other.mNetworkId)) {
            return true;
        }
        return false;
    }

    public boolean isMatchRuleMobile() {
        switch (this.mMatchRule) {
            case MATCH_MOBILE_ALL /*1*/:
            case MATCH_MOBILE_3G_LOWER /*2*/:
            case MATCH_MOBILE_4G /*3*/:
            case MATCH_MOBILE_WILDCARD /*6*/:
                return true;
            default:
                return false;
        }
    }

    public int getMatchRule() {
        return this.mMatchRule;
    }

    public String getSubscriberId() {
        return this.mSubscriberId;
    }

    public String getNetworkId() {
        return this.mNetworkId;
    }

    public boolean matches(NetworkIdentity ident) {
        switch (this.mMatchRule) {
            case MATCH_MOBILE_ALL /*1*/:
                return matchesMobile(ident);
            case MATCH_MOBILE_3G_LOWER /*2*/:
                return matchesMobile3gLower(ident);
            case MATCH_MOBILE_4G /*3*/:
                return matchesMobile4g(ident);
            case MATCH_WIFI /*4*/:
                return matchesWifi(ident);
            case MATCH_ETHERNET /*5*/:
                return matchesEthernet(ident);
            case MATCH_MOBILE_WILDCARD /*6*/:
                return matchesMobileWildcard(ident);
            case MATCH_WIFI_WILDCARD /*7*/:
                return matchesWifiWildcard(ident);
            case MATCH_BLUETOOTH /*8*/:
                return matchesBluetooth(ident);
            default:
                throw new IllegalArgumentException("unknown network template");
        }
    }

    private boolean matchesMobile(NetworkIdentity ident) {
        if (ident.mType == MATCH_MOBILE_WILDCARD) {
            return true;
        }
        boolean matchesType;
        if (sForceAllNetworkTypes || ArrayUtils.contains(DATA_USAGE_NETWORK_TYPES, ident.mType)) {
            matchesType = true;
        } else {
            matchesType = false;
        }
        if (matchesType && ArrayUtils.contains(this.mMatchSubscriberIds, ident.mSubscriberId)) {
            return true;
        }
        return false;
    }

    @Deprecated
    private boolean matchesMobile3gLower(NetworkIdentity ident) {
        ensureSubtypeAvailable();
        if (ident.mType == MATCH_MOBILE_WILDCARD || !matchesMobile(ident)) {
            return false;
        }
        switch (TelephonyManager.getNetworkClass(ident.mSubType)) {
            case Toast.LENGTH_SHORT /*0*/:
            case MATCH_MOBILE_ALL /*1*/:
            case MATCH_MOBILE_3G_LOWER /*2*/:
                return true;
            default:
                return false;
        }
    }

    @Deprecated
    private boolean matchesMobile4g(NetworkIdentity ident) {
        ensureSubtypeAvailable();
        if (ident.mType == MATCH_MOBILE_WILDCARD) {
            return true;
        }
        if (matchesMobile(ident)) {
            switch (TelephonyManager.getNetworkClass(ident.mSubType)) {
                case MATCH_MOBILE_4G /*3*/:
                    return true;
            }
        }
        return false;
    }

    private boolean matchesWifi(NetworkIdentity ident) {
        switch (ident.mType) {
            case MATCH_MOBILE_ALL /*1*/:
                return Objects.equals(WifiInfo.removeDoubleQuotes(this.mNetworkId), WifiInfo.removeDoubleQuotes(ident.mNetworkId));
            default:
                return false;
        }
    }

    private boolean matchesEthernet(NetworkIdentity ident) {
        if (ident.mType == 9) {
            return true;
        }
        return false;
    }

    private boolean matchesMobileWildcard(NetworkIdentity ident) {
        if (ident.mType == MATCH_MOBILE_WILDCARD || sForceAllNetworkTypes || ArrayUtils.contains(DATA_USAGE_NETWORK_TYPES, ident.mType)) {
            return true;
        }
        return false;
    }

    private boolean matchesWifiWildcard(NetworkIdentity ident) {
        switch (ident.mType) {
            case MATCH_MOBILE_ALL /*1*/:
            case TextViewSizeAction.TAG /*13*/:
                return true;
            default:
                return false;
        }
    }

    private boolean matchesBluetooth(NetworkIdentity ident) {
        if (ident.mType == MATCH_WIFI_WILDCARD) {
            return true;
        }
        return false;
    }

    private static String getMatchRuleName(int matchRule) {
        switch (matchRule) {
            case MATCH_MOBILE_ALL /*1*/:
                return "MOBILE_ALL";
            case MATCH_MOBILE_3G_LOWER /*2*/:
                return "MOBILE_3G_LOWER";
            case MATCH_MOBILE_4G /*3*/:
                return "MOBILE_4G";
            case MATCH_WIFI /*4*/:
                return "WIFI";
            case MATCH_ETHERNET /*5*/:
                return "ETHERNET";
            case MATCH_MOBILE_WILDCARD /*6*/:
                return "MOBILE_WILDCARD";
            case MATCH_WIFI_WILDCARD /*7*/:
                return "WIFI_WILDCARD";
            case MATCH_BLUETOOTH /*8*/:
                return "BLUETOOTH";
            default:
                return "UNKNOWN";
        }
    }

    private static void ensureSubtypeAvailable() {
        throw new IllegalArgumentException("Unable to enforce 3G_LOWER template on combined data.");
    }

    public static NetworkTemplate normalize(NetworkTemplate template, String[] merged) {
        if (template.isMatchRuleMobile() && ArrayUtils.contains(merged, template.mSubscriberId)) {
            return new NetworkTemplate(template.mMatchRule, merged[0], merged, template.mNetworkId);
        }
        return template;
    }
}
