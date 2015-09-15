package android.net.wifi;

import android.net.ProxyInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.security.Credentials;
import android.text.TextUtils;
import android.widget.Toast;
import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Map.Entry;

public class WifiEnterpriseConfig implements Parcelable {
    public static final String ANON_IDENTITY_KEY = "anonymous_identity";
    public static final String CA_CERT_KEY = "ca_cert";
    public static final String CA_CERT_PREFIX = "keystore://CACERT_";
    public static final String CLIENT_CERT_KEY = "client_cert";
    public static final String CLIENT_CERT_PREFIX = "keystore://USRCERT_";
    public static final Creator<WifiEnterpriseConfig> CREATOR;
    public static final String EAP_KEY = "eap";
    public static final String EMPTY_VALUE = "NULL";
    public static final String ENGINE_DISABLE = "0";
    public static final String ENGINE_ENABLE = "1";
    public static final String ENGINE_ID_KEY = "engine_id";
    public static final String ENGINE_ID_KEYSTORE = "keystore";
    public static final String ENGINE_KEY = "engine";
    public static final String IDENTITY_KEY = "identity";
    public static final String KEYSTORE_URI = "keystore://";
    public static final String OPP_KEY_CACHING = "proactive_key_caching";
    public static final String PASSWORD_KEY = "password";
    public static final String PHASE2_KEY = "phase2";
    public static final String PRIVATE_KEY_ID_KEY = "key_id";
    public static final String SUBJECT_MATCH_KEY = "subject_match";
    private X509Certificate mCaCert;
    private X509Certificate mClientCertificate;
    private PrivateKey mClientPrivateKey;
    private HashMap<String, String> mFields;

    public static final class Eap {
        public static final int AKA = 5;
        public static final int NONE = -1;
        public static final int PEAP = 0;
        public static final int PWD = 3;
        public static final int SIM = 4;
        public static final int TLS = 1;
        public static final int TTLS = 2;
        public static final String[] strings;

        static {
            strings = new String[]{"PEAP", "TLS", "TTLS", "PWD", "SIM", "AKA"};
        }

        private Eap() {
        }
    }

    public static final class Phase2 {
        public static final int GTC = 4;
        public static final int MSCHAP = 2;
        public static final int MSCHAPV2 = 3;
        public static final int NONE = 0;
        public static final int PAP = 1;
        private static final String PREFIX = "auth=";
        public static final String[] strings;

        static {
            strings = new String[]{WifiEnterpriseConfig.EMPTY_VALUE, "PAP", "MSCHAP", "MSCHAPV2", "GTC"};
        }

        private Phase2() {
        }
    }

    public WifiEnterpriseConfig() {
        this.mFields = new HashMap();
    }

    public WifiEnterpriseConfig(WifiEnterpriseConfig source) {
        this.mFields = new HashMap();
        for (String key : source.mFields.keySet()) {
            this.mFields.put(key, source.mFields.get(key));
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mFields.size());
        for (Entry<String, String> entry : this.mFields.entrySet()) {
            dest.writeString((String) entry.getKey());
            dest.writeString((String) entry.getValue());
        }
        writeCertificate(dest, this.mCaCert);
        if (this.mClientPrivateKey != null) {
            String algorithm = this.mClientPrivateKey.getAlgorithm();
            byte[] userKeyBytes = this.mClientPrivateKey.getEncoded();
            dest.writeInt(userKeyBytes.length);
            dest.writeByteArray(userKeyBytes);
            dest.writeString(algorithm);
        } else {
            dest.writeInt(0);
        }
        writeCertificate(dest, this.mClientCertificate);
    }

    private void writeCertificate(Parcel dest, X509Certificate cert) {
        if (cert != null) {
            try {
                byte[] certBytes = cert.getEncoded();
                dest.writeInt(certBytes.length);
                dest.writeByteArray(certBytes);
                return;
            } catch (CertificateEncodingException e) {
                dest.writeInt(0);
                return;
            }
        }
        dest.writeInt(0);
    }

    static {
        CREATOR = new Creator<WifiEnterpriseConfig>() {
            public WifiEnterpriseConfig createFromParcel(Parcel in) {
                WifiEnterpriseConfig enterpriseConfig = new WifiEnterpriseConfig();
                int count = in.readInt();
                for (int i = 0; i < count; i++) {
                    enterpriseConfig.mFields.put(in.readString(), in.readString());
                }
                enterpriseConfig.mCaCert = readCertificate(in);
                PrivateKey userKey = null;
                int len = in.readInt();
                if (len > 0) {
                    try {
                        byte[] bytes = new byte[len];
                        in.readByteArray(bytes);
                        userKey = KeyFactory.getInstance(in.readString()).generatePrivate(new PKCS8EncodedKeySpec(bytes));
                    } catch (NoSuchAlgorithmException e) {
                        userKey = null;
                    } catch (InvalidKeySpecException e2) {
                        userKey = null;
                    }
                }
                enterpriseConfig.mClientPrivateKey = userKey;
                enterpriseConfig.mClientCertificate = readCertificate(in);
                return enterpriseConfig;
            }

            private X509Certificate readCertificate(Parcel in) {
                int len = in.readInt();
                if (len <= 0) {
                    return null;
                }
                try {
                    byte[] bytes = new byte[len];
                    in.readByteArray(bytes);
                    return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(bytes));
                } catch (CertificateException e) {
                    return null;
                }
            }

            public WifiEnterpriseConfig[] newArray(int size) {
                return new WifiEnterpriseConfig[size];
            }
        };
    }

    public HashMap<String, String> getFields() {
        return this.mFields;
    }

    public void setEapMethod(int eapMethod) {
        switch (eapMethod) {
            case Toast.LENGTH_SHORT /*0*/:
            case Action.MERGE_IGNORE /*2*/:
            case SetDrawableParameters.TAG /*3*/:
            case ViewGroupAction.TAG /*4*/:
            case ReflectionActionWithoutParams.TAG /*5*/:
                break;
            case Toast.LENGTH_LONG /*1*/:
                setPhase2Method(0);
                break;
            default:
                throw new IllegalArgumentException("Unknown EAP method");
        }
        this.mFields.put(EAP_KEY, Eap.strings[eapMethod]);
        this.mFields.put(OPP_KEY_CACHING, ENGINE_ENABLE);
    }

    public int getEapMethod() {
        return getStringIndex(Eap.strings, (String) this.mFields.get(EAP_KEY), -1);
    }

    public void setPhase2Method(int phase2Method) {
        switch (phase2Method) {
            case Toast.LENGTH_SHORT /*0*/:
                this.mFields.put(PHASE2_KEY, EMPTY_VALUE);
            case Toast.LENGTH_LONG /*1*/:
            case Action.MERGE_IGNORE /*2*/:
            case SetDrawableParameters.TAG /*3*/:
            case ViewGroupAction.TAG /*4*/:
                this.mFields.put(PHASE2_KEY, convertToQuotedString("auth=" + Phase2.strings[phase2Method]));
            default:
                throw new IllegalArgumentException("Unknown Phase 2 method");
        }
    }

    public int getPhase2Method() {
        String phase2Method = removeDoubleQuotes((String) this.mFields.get(PHASE2_KEY));
        if (phase2Method.startsWith("auth=")) {
            phase2Method = phase2Method.substring("auth=".length());
        }
        return getStringIndex(Phase2.strings, phase2Method, 0);
    }

    public void setIdentity(String identity) {
        setFieldValue(IDENTITY_KEY, identity, ProxyInfo.LOCAL_EXCL_LIST);
    }

    public String getIdentity() {
        return getFieldValue(IDENTITY_KEY, ProxyInfo.LOCAL_EXCL_LIST);
    }

    public void setAnonymousIdentity(String anonymousIdentity) {
        setFieldValue(ANON_IDENTITY_KEY, anonymousIdentity, ProxyInfo.LOCAL_EXCL_LIST);
    }

    public String getAnonymousIdentity() {
        return getFieldValue(ANON_IDENTITY_KEY, ProxyInfo.LOCAL_EXCL_LIST);
    }

    public void setPassword(String password) {
        setFieldValue(PASSWORD_KEY, password, ProxyInfo.LOCAL_EXCL_LIST);
    }

    public String getPassword() {
        return getFieldValue(PASSWORD_KEY, ProxyInfo.LOCAL_EXCL_LIST);
    }

    public void setCaCertificateAlias(String alias) {
        setFieldValue(CA_CERT_KEY, alias, CA_CERT_PREFIX);
    }

    public String getCaCertificateAlias() {
        return getFieldValue(CA_CERT_KEY, CA_CERT_PREFIX);
    }

    public void setCaCertificate(X509Certificate cert) {
        if (cert == null) {
            this.mCaCert = null;
        } else if (cert.getBasicConstraints() >= 0) {
            this.mCaCert = cert;
        } else {
            throw new IllegalArgumentException("Not a CA certificate");
        }
    }

    public X509Certificate getCaCertificate() {
        return this.mCaCert;
    }

    public void resetCaCertificate() {
        this.mCaCert = null;
    }

    public void setClientCertificateAlias(String alias) {
        setFieldValue(CLIENT_CERT_KEY, alias, CLIENT_CERT_PREFIX);
        setFieldValue(PRIVATE_KEY_ID_KEY, alias, Credentials.USER_PRIVATE_KEY);
        if (TextUtils.isEmpty(alias)) {
            this.mFields.put(ENGINE_KEY, ENGINE_DISABLE);
            this.mFields.put(ENGINE_ID_KEY, EMPTY_VALUE);
            return;
        }
        this.mFields.put(ENGINE_KEY, ENGINE_ENABLE);
        this.mFields.put(ENGINE_ID_KEY, convertToQuotedString(ENGINE_ID_KEYSTORE));
    }

    public String getClientCertificateAlias() {
        return getFieldValue(CLIENT_CERT_KEY, CLIENT_CERT_PREFIX);
    }

    public void setClientKeyEntry(PrivateKey privateKey, X509Certificate clientCertificate) {
        if (clientCertificate != null) {
            if (clientCertificate.getBasicConstraints() != -1) {
                throw new IllegalArgumentException("Cannot be a CA certificate");
            } else if (privateKey == null) {
                throw new IllegalArgumentException("Client cert without a private key");
            } else if (privateKey.getEncoded() == null) {
                throw new IllegalArgumentException("Private key cannot be encoded");
            }
        }
        this.mClientPrivateKey = privateKey;
        this.mClientCertificate = clientCertificate;
    }

    public X509Certificate getClientCertificate() {
        return this.mClientCertificate;
    }

    public void resetClientKeyEntry() {
        this.mClientPrivateKey = null;
        this.mClientCertificate = null;
    }

    public PrivateKey getClientPrivateKey() {
        return this.mClientPrivateKey;
    }

    public void setSubjectMatch(String subjectMatch) {
        setFieldValue(SUBJECT_MATCH_KEY, subjectMatch, ProxyInfo.LOCAL_EXCL_LIST);
    }

    public String getSubjectMatch() {
        return getFieldValue(SUBJECT_MATCH_KEY, ProxyInfo.LOCAL_EXCL_LIST);
    }

    String getKeyId(WifiEnterpriseConfig current) {
        String eap = (String) this.mFields.get(EAP_KEY);
        String phase2 = (String) this.mFields.get(PHASE2_KEY);
        if (TextUtils.isEmpty(eap)) {
            eap = (String) current.mFields.get(EAP_KEY);
        }
        if (TextUtils.isEmpty(phase2)) {
            phase2 = (String) current.mFields.get(PHASE2_KEY);
        }
        return eap + "_" + phase2;
    }

    private String removeDoubleQuotes(String string) {
        if (TextUtils.isEmpty(string)) {
            return ProxyInfo.LOCAL_EXCL_LIST;
        }
        int length = string.length();
        if (length > 1 && string.charAt(0) == '\"' && string.charAt(length - 1) == '\"') {
            return string.substring(1, length - 1);
        }
        return string;
    }

    private String convertToQuotedString(String string) {
        return "\"" + string + "\"";
    }

    private int getStringIndex(String[] arr, String toBeFound, int defaultIndex) {
        if (TextUtils.isEmpty(toBeFound)) {
            return defaultIndex;
        }
        for (int i = 0; i < arr.length; i++) {
            if (toBeFound.equals(arr[i])) {
                return i;
            }
        }
        return defaultIndex;
    }

    public String getFieldValue(String key, String prefix) {
        String value = (String) this.mFields.get(key);
        if (TextUtils.isEmpty(value) || EMPTY_VALUE.equals(value)) {
            return ProxyInfo.LOCAL_EXCL_LIST;
        }
        value = removeDoubleQuotes(value);
        return value.startsWith(prefix) ? value.substring(prefix.length()) : value;
    }

    public void setFieldValue(String key, String value, String prefix) {
        if (TextUtils.isEmpty(value)) {
            this.mFields.put(key, EMPTY_VALUE);
        } else {
            this.mFields.put(key, convertToQuotedString(prefix + value));
        }
    }

    public void setFieldValue(String key, String value) {
        if (TextUtils.isEmpty(value)) {
            this.mFields.put(key, EMPTY_VALUE);
        } else {
            this.mFields.put(key, convertToQuotedString(value));
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (String key : this.mFields.keySet()) {
            sb.append(key).append(" ").append((String) this.mFields.get(key)).append("\n");
        }
        return sb.toString();
    }
}
