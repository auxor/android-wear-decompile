package com.android.internal.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Log;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class VpnProfile implements Cloneable, Parcelable {
    public static final Creator<VpnProfile> CREATOR;
    private static final String TAG = "VpnProfile";
    public static final int TYPE_IPSEC_HYBRID_RSA = 5;
    public static final int TYPE_IPSEC_XAUTH_PSK = 3;
    public static final int TYPE_IPSEC_XAUTH_RSA = 4;
    public static final int TYPE_L2TP_IPSEC_PSK = 1;
    public static final int TYPE_L2TP_IPSEC_RSA = 2;
    public static final int TYPE_MAX = 5;
    public static final int TYPE_PPTP = 0;
    public String dnsServers;
    public String ipsecCaCert;
    public String ipsecIdentifier;
    public String ipsecSecret;
    public String ipsecServerCert;
    public String ipsecUserCert;
    public final String key;
    public String l2tpSecret;
    public boolean mppe;
    public String name;
    public String password;
    public String routes;
    public boolean saveLogin;
    public String searchDomains;
    public String server;
    public int type;
    public String username;

    public VpnProfile(String key) {
        this.name = "";
        this.type = 0;
        this.server = "";
        this.username = "";
        this.password = "";
        this.dnsServers = "";
        this.searchDomains = "";
        this.routes = "";
        this.mppe = true;
        this.l2tpSecret = "";
        this.ipsecIdentifier = "";
        this.ipsecSecret = "";
        this.ipsecUserCert = "";
        this.ipsecCaCert = "";
        this.ipsecServerCert = "";
        this.saveLogin = false;
        this.key = key;
    }

    public VpnProfile(Parcel in) {
        boolean z = true;
        this.name = "";
        this.type = 0;
        this.server = "";
        this.username = "";
        this.password = "";
        this.dnsServers = "";
        this.searchDomains = "";
        this.routes = "";
        this.mppe = true;
        this.l2tpSecret = "";
        this.ipsecIdentifier = "";
        this.ipsecSecret = "";
        this.ipsecUserCert = "";
        this.ipsecCaCert = "";
        this.ipsecServerCert = "";
        this.saveLogin = false;
        this.key = in.readString();
        this.name = in.readString();
        this.type = in.readInt();
        this.server = in.readString();
        this.username = in.readString();
        this.password = in.readString();
        this.dnsServers = in.readString();
        this.searchDomains = in.readString();
        this.routes = in.readString();
        this.mppe = in.readInt() != 0;
        this.l2tpSecret = in.readString();
        this.ipsecIdentifier = in.readString();
        this.ipsecSecret = in.readString();
        this.ipsecUserCert = in.readString();
        this.ipsecCaCert = in.readString();
        this.ipsecServerCert = in.readString();
        if (in.readInt() == 0) {
            z = false;
        }
        this.saveLogin = z;
    }

    public void writeToParcel(Parcel out, int flags) {
        int i = TYPE_L2TP_IPSEC_PSK;
        out.writeString(this.key);
        out.writeString(this.name);
        out.writeInt(this.type);
        out.writeString(this.server);
        out.writeString(this.username);
        out.writeString(this.password);
        out.writeString(this.dnsServers);
        out.writeString(this.searchDomains);
        out.writeString(this.routes);
        out.writeInt(this.mppe ? TYPE_L2TP_IPSEC_PSK : 0);
        out.writeString(this.l2tpSecret);
        out.writeString(this.ipsecIdentifier);
        out.writeString(this.ipsecSecret);
        out.writeString(this.ipsecUserCert);
        out.writeString(this.ipsecCaCert);
        out.writeString(this.ipsecServerCert);
        if (!this.saveLogin) {
            i = 0;
        }
        out.writeInt(i);
    }

    public static VpnProfile decode(String key, byte[] value) {
        boolean z = false;
        if (key == null) {
            return null;
        }
        try {
            String[] values = new String(value, StandardCharsets.UTF_8).split("\u0000", -1);
            if (values.length < 14 || values.length > 15) {
                return null;
            }
            VpnProfile profile = new VpnProfile(key);
            profile.name = values[0];
            profile.type = Integer.valueOf(values[TYPE_L2TP_IPSEC_PSK]).intValue();
            if (profile.type < 0 || profile.type > TYPE_MAX) {
                return null;
            }
            profile.server = values[TYPE_L2TP_IPSEC_RSA];
            profile.username = values[TYPE_IPSEC_XAUTH_PSK];
            profile.password = values[TYPE_IPSEC_XAUTH_RSA];
            profile.dnsServers = values[TYPE_MAX];
            profile.searchDomains = values[6];
            profile.routes = values[7];
            profile.mppe = Boolean.valueOf(values[8]).booleanValue();
            profile.l2tpSecret = values[9];
            profile.ipsecIdentifier = values[10];
            profile.ipsecSecret = values[11];
            profile.ipsecUserCert = values[12];
            profile.ipsecCaCert = values[13];
            profile.ipsecServerCert = values.length > 14 ? values[14] : "";
            if (!(profile.username.isEmpty() && profile.password.isEmpty())) {
                z = true;
            }
            profile.saveLogin = z;
            return profile;
        } catch (Exception e) {
            return null;
        }
    }

    public byte[] encode() {
        StringBuilder builder = new StringBuilder(this.name);
        builder.append('\u0000').append(this.type);
        builder.append('\u0000').append(this.server);
        builder.append('\u0000').append(this.saveLogin ? this.username : "");
        builder.append('\u0000').append(this.saveLogin ? this.password : "");
        builder.append('\u0000').append(this.dnsServers);
        builder.append('\u0000').append(this.searchDomains);
        builder.append('\u0000').append(this.routes);
        builder.append('\u0000').append(this.mppe);
        builder.append('\u0000').append(this.l2tpSecret);
        builder.append('\u0000').append(this.ipsecIdentifier);
        builder.append('\u0000').append(this.ipsecSecret);
        builder.append('\u0000').append(this.ipsecUserCert);
        builder.append('\u0000').append(this.ipsecCaCert);
        builder.append('\u0000').append(this.ipsecServerCert);
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    public boolean isValidLockdownProfile() {
        try {
            InetAddress.parseNumericAddress(this.server);
            String[] arr$ = this.dnsServers.split(" +");
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$ += TYPE_L2TP_IPSEC_PSK) {
                String dnsServer = arr$[i$];
                InetAddress.parseNumericAddress(this.dnsServers);
            }
            if (!TextUtils.isEmpty(this.dnsServers)) {
                return true;
            }
            Log.w(TAG, "DNS required");
            return false;
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Invalid address", e);
            return false;
        }
    }

    static {
        CREATOR = new Creator<VpnProfile>() {
            public VpnProfile createFromParcel(Parcel in) {
                return new VpnProfile(in);
            }

            public VpnProfile[] newArray(int size) {
                return new VpnProfile[size];
            }
        };
    }

    public int describeContents() {
        return 0;
    }
}
