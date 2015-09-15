package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;

public final class NetworkCapabilities implements Parcelable {
    public static final Creator<NetworkCapabilities> CREATOR;
    private static final int MAX_NET_CAPABILITY = 16;
    private static final int MAX_TRANSPORT = 4;
    private static final int MIN_NET_CAPABILITY = 0;
    private static final int MIN_TRANSPORT = 0;
    public static final int NET_CAPABILITY_CBS = 5;
    public static final int NET_CAPABILITY_DUN = 2;
    public static final int NET_CAPABILITY_EIMS = 10;
    public static final int NET_CAPABILITY_FOTA = 3;
    public static final int NET_CAPABILITY_IA = 7;
    public static final int NET_CAPABILITY_IMS = 4;
    public static final int NET_CAPABILITY_INTERNET = 12;
    public static final int NET_CAPABILITY_MMS = 0;
    public static final int NET_CAPABILITY_NOT_METERED = 11;
    public static final int NET_CAPABILITY_NOT_RESTRICTED = 13;
    public static final int NET_CAPABILITY_NOT_VPN = 15;
    public static final int NET_CAPABILITY_RCS = 8;
    public static final int NET_CAPABILITY_SUPL = 1;
    public static final int NET_CAPABILITY_TRUSTED = 14;
    public static final int NET_CAPABILITY_VALIDATED = 16;
    public static final int NET_CAPABILITY_WIFI_P2P = 6;
    public static final int NET_CAPABILITY_XCAP = 9;
    public static final int TRANSPORT_BLUETOOTH = 2;
    public static final int TRANSPORT_CELLULAR = 0;
    public static final int TRANSPORT_ETHERNET = 3;
    public static final int TRANSPORT_VPN = 4;
    public static final int TRANSPORT_WIFI = 1;
    private int mLinkDownBandwidthKbps;
    private int mLinkUpBandwidthKbps;
    private long mNetworkCapabilities;
    private String mNetworkSpecifier;
    private long mTransportTypes;

    public NetworkCapabilities() {
        this.mNetworkCapabilities = 57344;
    }

    public NetworkCapabilities(NetworkCapabilities nc) {
        this.mNetworkCapabilities = 57344;
        if (nc != null) {
            this.mNetworkCapabilities = nc.mNetworkCapabilities;
            this.mTransportTypes = nc.mTransportTypes;
            this.mLinkUpBandwidthKbps = nc.mLinkUpBandwidthKbps;
            this.mLinkDownBandwidthKbps = nc.mLinkDownBandwidthKbps;
            this.mNetworkSpecifier = nc.mNetworkSpecifier;
        }
    }

    public NetworkCapabilities addCapability(int capability) {
        if (capability < 0 || capability > NET_CAPABILITY_VALIDATED) {
            throw new IllegalArgumentException("NetworkCapability out of range");
        }
        this.mNetworkCapabilities |= (long) (TRANSPORT_WIFI << capability);
        return this;
    }

    public NetworkCapabilities removeCapability(int capability) {
        if (capability < 0 || capability > NET_CAPABILITY_VALIDATED) {
            throw new IllegalArgumentException("NetworkCapability out of range");
        }
        this.mNetworkCapabilities &= (long) ((TRANSPORT_WIFI << capability) ^ -1);
        return this;
    }

    public int[] getCapabilities() {
        return enumerateBits(this.mNetworkCapabilities);
    }

    public boolean hasCapability(int capability) {
        if (capability < 0 || capability > NET_CAPABILITY_VALIDATED) {
            return false;
        }
        if ((this.mNetworkCapabilities & ((long) (TRANSPORT_WIFI << capability))) == 0) {
            return false;
        }
        return true;
    }

    private int[] enumerateBits(long val) {
        int[] result = new int[Long.bitCount(val)];
        int resource = TRANSPORT_CELLULAR;
        int index = TRANSPORT_CELLULAR;
        while (val > 0) {
            int index2;
            if ((val & 1) == 1) {
                index2 = index + TRANSPORT_WIFI;
                result[index] = resource;
            } else {
                index2 = index;
            }
            val >>= TRANSPORT_WIFI;
            resource += TRANSPORT_WIFI;
            index = index2;
        }
        return result;
    }

    private void combineNetCapabilities(NetworkCapabilities nc) {
        this.mNetworkCapabilities |= nc.mNetworkCapabilities;
    }

    private boolean satisfiedByNetCapabilities(NetworkCapabilities nc) {
        return (nc.mNetworkCapabilities & this.mNetworkCapabilities) == this.mNetworkCapabilities;
    }

    public boolean equalsNetCapabilities(NetworkCapabilities nc) {
        return nc.mNetworkCapabilities == this.mNetworkCapabilities;
    }

    public NetworkCapabilities addTransportType(int transportType) {
        if (transportType < 0 || transportType > TRANSPORT_VPN) {
            throw new IllegalArgumentException("TransportType out of range");
        }
        this.mTransportTypes |= (long) (TRANSPORT_WIFI << transportType);
        setNetworkSpecifier(this.mNetworkSpecifier);
        return this;
    }

    public NetworkCapabilities removeTransportType(int transportType) {
        if (transportType < 0 || transportType > TRANSPORT_VPN) {
            throw new IllegalArgumentException("TransportType out of range");
        }
        this.mTransportTypes &= (long) ((TRANSPORT_WIFI << transportType) ^ -1);
        setNetworkSpecifier(this.mNetworkSpecifier);
        return this;
    }

    public int[] getTransportTypes() {
        return enumerateBits(this.mTransportTypes);
    }

    public boolean hasTransport(int transportType) {
        if (transportType < 0 || transportType > TRANSPORT_VPN) {
            return false;
        }
        if ((this.mTransportTypes & ((long) (TRANSPORT_WIFI << transportType))) == 0) {
            return false;
        }
        return true;
    }

    private void combineTransportTypes(NetworkCapabilities nc) {
        this.mTransportTypes |= nc.mTransportTypes;
    }

    private boolean satisfiedByTransportTypes(NetworkCapabilities nc) {
        return this.mTransportTypes == 0 || (this.mTransportTypes & nc.mTransportTypes) != 0;
    }

    public boolean equalsTransportTypes(NetworkCapabilities nc) {
        return nc.mTransportTypes == this.mTransportTypes;
    }

    public void setLinkUpstreamBandwidthKbps(int upKbps) {
        this.mLinkUpBandwidthKbps = upKbps;
    }

    public int getLinkUpstreamBandwidthKbps() {
        return this.mLinkUpBandwidthKbps;
    }

    public void setLinkDownstreamBandwidthKbps(int downKbps) {
        this.mLinkDownBandwidthKbps = downKbps;
    }

    public int getLinkDownstreamBandwidthKbps() {
        return this.mLinkDownBandwidthKbps;
    }

    private void combineLinkBandwidths(NetworkCapabilities nc) {
        this.mLinkUpBandwidthKbps = Math.max(this.mLinkUpBandwidthKbps, nc.mLinkUpBandwidthKbps);
        this.mLinkDownBandwidthKbps = Math.max(this.mLinkDownBandwidthKbps, nc.mLinkDownBandwidthKbps);
    }

    private boolean satisfiedByLinkBandwidths(NetworkCapabilities nc) {
        return this.mLinkUpBandwidthKbps <= nc.mLinkUpBandwidthKbps && this.mLinkDownBandwidthKbps <= nc.mLinkDownBandwidthKbps;
    }

    private boolean equalsLinkBandwidths(NetworkCapabilities nc) {
        return this.mLinkUpBandwidthKbps == nc.mLinkUpBandwidthKbps && this.mLinkDownBandwidthKbps == nc.mLinkDownBandwidthKbps;
    }

    public void setNetworkSpecifier(String networkSpecifier) {
        if (TextUtils.isEmpty(networkSpecifier) || Long.bitCount(this.mTransportTypes) == TRANSPORT_WIFI) {
            this.mNetworkSpecifier = networkSpecifier;
            return;
        }
        throw new IllegalStateException("Must have a single transport specified to use setNetworkSpecifier");
    }

    public String getNetworkSpecifier() {
        return this.mNetworkSpecifier;
    }

    private void combineSpecifiers(NetworkCapabilities nc) {
        String otherSpecifier = nc.getNetworkSpecifier();
        if (!TextUtils.isEmpty(otherSpecifier)) {
            if (TextUtils.isEmpty(this.mNetworkSpecifier)) {
                setNetworkSpecifier(otherSpecifier);
                return;
            }
            throw new IllegalStateException("Can't combine two networkSpecifiers");
        }
    }

    private boolean satisfiedBySpecifier(NetworkCapabilities nc) {
        return TextUtils.isEmpty(this.mNetworkSpecifier) || this.mNetworkSpecifier.equals(nc.mNetworkSpecifier);
    }

    private boolean equalsSpecifier(NetworkCapabilities nc) {
        if (TextUtils.isEmpty(this.mNetworkSpecifier)) {
            return TextUtils.isEmpty(nc.mNetworkSpecifier);
        }
        return this.mNetworkSpecifier.equals(nc.mNetworkSpecifier);
    }

    public void combineCapabilities(NetworkCapabilities nc) {
        combineNetCapabilities(nc);
        combineTransportTypes(nc);
        combineLinkBandwidths(nc);
        combineSpecifiers(nc);
    }

    public boolean satisfiedByNetworkCapabilities(NetworkCapabilities nc) {
        return nc != null && satisfiedByNetCapabilities(nc) && satisfiedByTransportTypes(nc) && satisfiedByLinkBandwidths(nc) && satisfiedBySpecifier(nc);
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof NetworkCapabilities)) {
            return false;
        }
        NetworkCapabilities that = (NetworkCapabilities) obj;
        if (equalsNetCapabilities(that) && equalsTransportTypes(that) && equalsLinkBandwidths(that) && equalsSpecifier(that)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (TextUtils.isEmpty(this.mNetworkSpecifier) ? TRANSPORT_CELLULAR : this.mNetworkSpecifier.hashCode() * 17) + ((this.mLinkDownBandwidthKbps * NET_CAPABILITY_NOT_RESTRICTED) + ((((((int) (this.mNetworkCapabilities & -1)) + (((int) (this.mNetworkCapabilities >> 32)) * TRANSPORT_ETHERNET)) + (((int) (this.mTransportTypes & -1)) * NET_CAPABILITY_CBS)) + (((int) (this.mTransportTypes >> 32)) * NET_CAPABILITY_IA)) + (this.mLinkUpBandwidthKbps * NET_CAPABILITY_NOT_METERED)));
    }

    public int describeContents() {
        return TRANSPORT_CELLULAR;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mNetworkCapabilities);
        dest.writeLong(this.mTransportTypes);
        dest.writeInt(this.mLinkUpBandwidthKbps);
        dest.writeInt(this.mLinkDownBandwidthKbps);
        dest.writeString(this.mNetworkSpecifier);
    }

    static {
        CREATOR = new Creator<NetworkCapabilities>() {
            public NetworkCapabilities createFromParcel(Parcel in) {
                NetworkCapabilities netCap = new NetworkCapabilities();
                netCap.mNetworkCapabilities = in.readLong();
                netCap.mTransportTypes = in.readLong();
                netCap.mLinkUpBandwidthKbps = in.readInt();
                netCap.mLinkDownBandwidthKbps = in.readInt();
                netCap.mNetworkSpecifier = in.readString();
                return netCap;
            }

            public NetworkCapabilities[] newArray(int size) {
                return new NetworkCapabilities[size];
            }
        };
    }

    public String toString() {
        String specifier;
        int[] types = getTransportTypes();
        String transports = types.length > 0 ? " Transports: " : ProxyInfo.LOCAL_EXCL_LIST;
        int i = TRANSPORT_CELLULAR;
        while (i < types.length) {
            switch (types[i]) {
                case TRANSPORT_CELLULAR /*0*/:
                    transports = transports + "CELLULAR";
                    break;
                case TRANSPORT_WIFI /*1*/:
                    transports = transports + "WIFI";
                    break;
                case TRANSPORT_BLUETOOTH /*2*/:
                    transports = transports + "BLUETOOTH";
                    break;
                case TRANSPORT_ETHERNET /*3*/:
                    transports = transports + "ETHERNET";
                    break;
                case TRANSPORT_VPN /*4*/:
                    transports = transports + "VPN";
                    break;
            }
            i += TRANSPORT_WIFI;
            if (i < types.length) {
                transports = transports + "|";
            }
        }
        types = getCapabilities();
        String capabilities = types.length > 0 ? " Capabilities: " : ProxyInfo.LOCAL_EXCL_LIST;
        i = TRANSPORT_CELLULAR;
        while (i < types.length) {
            switch (types[i]) {
                case TRANSPORT_CELLULAR /*0*/:
                    capabilities = capabilities + "MMS";
                    break;
                case TRANSPORT_WIFI /*1*/:
                    capabilities = capabilities + "SUPL";
                    break;
                case TRANSPORT_BLUETOOTH /*2*/:
                    capabilities = capabilities + "DUN";
                    break;
                case TRANSPORT_ETHERNET /*3*/:
                    capabilities = capabilities + "FOTA";
                    break;
                case TRANSPORT_VPN /*4*/:
                    capabilities = capabilities + "IMS";
                    break;
                case NET_CAPABILITY_CBS /*5*/:
                    capabilities = capabilities + "CBS";
                    break;
                case NET_CAPABILITY_WIFI_P2P /*6*/:
                    capabilities = capabilities + "WIFI_P2P";
                    break;
                case NET_CAPABILITY_IA /*7*/:
                    capabilities = capabilities + "IA";
                    break;
                case NET_CAPABILITY_RCS /*8*/:
                    capabilities = capabilities + "RCS";
                    break;
                case NET_CAPABILITY_XCAP /*9*/:
                    capabilities = capabilities + "XCAP";
                    break;
                case NET_CAPABILITY_EIMS /*10*/:
                    capabilities = capabilities + "EIMS";
                    break;
                case NET_CAPABILITY_NOT_METERED /*11*/:
                    capabilities = capabilities + "NOT_METERED";
                    break;
                case NET_CAPABILITY_INTERNET /*12*/:
                    capabilities = capabilities + "INTERNET";
                    break;
                case NET_CAPABILITY_NOT_RESTRICTED /*13*/:
                    capabilities = capabilities + "NOT_RESTRICTED";
                    break;
                case NET_CAPABILITY_TRUSTED /*14*/:
                    capabilities = capabilities + "TRUSTED";
                    break;
                case NET_CAPABILITY_NOT_VPN /*15*/:
                    capabilities = capabilities + "NOT_VPN";
                    break;
            }
            i += TRANSPORT_WIFI;
            if (i < types.length) {
                capabilities = capabilities + "&";
            }
        }
        String upBand = this.mLinkUpBandwidthKbps > 0 ? " LinkUpBandwidth>=" + this.mLinkUpBandwidthKbps + "Kbps" : ProxyInfo.LOCAL_EXCL_LIST;
        String dnBand = this.mLinkDownBandwidthKbps > 0 ? " LinkDnBandwidth>=" + this.mLinkDownBandwidthKbps + "Kbps" : ProxyInfo.LOCAL_EXCL_LIST;
        if (this.mNetworkSpecifier == null) {
            specifier = ProxyInfo.LOCAL_EXCL_LIST;
        } else {
            specifier = " Specifier: <" + this.mNetworkSpecifier + ">";
        }
        return "[" + transports + capabilities + upBand + dnBand + specifier + "]";
    }
}
