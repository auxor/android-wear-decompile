package com.android.org.conscrypt;

import com.android.org.conscrypt.util.EmptyArray;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.crypto.SecretKey;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;

public class SSLParametersImpl implements Cloneable {
    private static final String KEY_TYPE_DH_DSA = "DH_DSA";
    private static final String KEY_TYPE_DH_RSA = "DH_RSA";
    private static final String KEY_TYPE_DSA = "DSA";
    private static final String KEY_TYPE_EC = "EC";
    private static final String KEY_TYPE_EC_EC = "EC_EC";
    private static final String KEY_TYPE_EC_RSA = "EC_RSA";
    private static final String KEY_TYPE_RSA = "RSA";
    private static volatile SSLParametersImpl defaultParameters;
    private static volatile SecureRandom defaultSecureRandom;
    private static volatile X509KeyManager defaultX509KeyManager;
    private static volatile X509TrustManager defaultX509TrustManager;
    byte[] alpnProtocols;
    boolean channelIdEnabled;
    private final ClientSessionContext clientSessionContext;
    private boolean client_mode;
    private boolean enable_session_creation;
    private String[] enabledCipherSuites;
    private String[] enabledProtocols;
    private String endpointIdentificationAlgorithm;
    private boolean need_client_auth;
    byte[] npnProtocols;
    private final PSKKeyManager pskKeyManager;
    private SecureRandom secureRandom;
    private final ServerSessionContext serverSessionContext;
    boolean useSessionTickets;
    private Boolean useSni;
    private boolean want_client_auth;
    private final X509KeyManager x509KeyManager;
    private final X509TrustManager x509TrustManager;

    public interface AliasChooser {
        String chooseClientAlias(X509KeyManager x509KeyManager, X500Principal[] x500PrincipalArr, String[] strArr);

        String chooseServerAlias(X509KeyManager x509KeyManager, String str);
    }

    public interface PSKCallbacks {
        String chooseClientPSKIdentity(PSKKeyManager pSKKeyManager, String str);

        String chooseServerPSKIdentityHint(PSKKeyManager pSKKeyManager);

        SecretKey getPSKKey(PSKKeyManager pSKKeyManager, String str, String str2);
    }

    protected SSLParametersImpl(KeyManager[] kms, TrustManager[] tms, SecureRandom sr, ClientSessionContext clientSessionContext, ServerSessionContext serverSessionContext) throws KeyManagementException {
        boolean pskCipherSuitesNeeded;
        this.client_mode = true;
        this.need_client_auth = false;
        this.want_client_auth = false;
        this.enable_session_creation = true;
        this.serverSessionContext = serverSessionContext;
        this.clientSessionContext = clientSessionContext;
        if (kms == null) {
            this.x509KeyManager = getDefaultX509KeyManager();
            this.pskKeyManager = null;
        } else {
            this.x509KeyManager = findFirstX509KeyManager(kms);
            this.pskKeyManager = findFirstPSKKeyManager(kms);
        }
        if (tms == null) {
            this.x509TrustManager = getDefaultX509TrustManager();
        } else {
            this.x509TrustManager = findFirstX509TrustManager(tms);
        }
        this.secureRandom = sr;
        this.enabledProtocols = getDefaultProtocols();
        boolean x509CipherSuitesNeeded = (this.x509KeyManager == null && this.x509TrustManager == null) ? false : true;
        if (this.pskKeyManager != null) {
            pskCipherSuitesNeeded = true;
        } else {
            pskCipherSuitesNeeded = false;
        }
        this.enabledCipherSuites = getDefaultCipherSuites(x509CipherSuitesNeeded, pskCipherSuitesNeeded);
    }

    protected static SSLParametersImpl getDefault() throws KeyManagementException {
        SSLParametersImpl result = defaultParameters;
        if (result == null) {
            result = new SSLParametersImpl(null, null, null, new ClientSessionContext(), new ServerSessionContext());
            defaultParameters = result;
        }
        return (SSLParametersImpl) result.clone();
    }

    public AbstractSessionContext getSessionContext() {
        return this.client_mode ? this.clientSessionContext : this.serverSessionContext;
    }

    protected ServerSessionContext getServerSessionContext() {
        return this.serverSessionContext;
    }

    protected ClientSessionContext getClientSessionContext() {
        return this.clientSessionContext;
    }

    protected X509KeyManager getX509KeyManager() {
        return this.x509KeyManager;
    }

    protected PSKKeyManager getPSKKeyManager() {
        return this.pskKeyManager;
    }

    protected X509TrustManager getX509TrustManager() {
        return this.x509TrustManager;
    }

    protected SecureRandom getSecureRandom() {
        if (this.secureRandom != null) {
            return this.secureRandom;
        }
        SecureRandom result = defaultSecureRandom;
        if (result == null) {
            result = new SecureRandom();
            defaultSecureRandom = result;
        }
        this.secureRandom = result;
        return this.secureRandom;
    }

    protected SecureRandom getSecureRandomMember() {
        return this.secureRandom;
    }

    protected String[] getEnabledCipherSuites() {
        return (String[]) this.enabledCipherSuites.clone();
    }

    protected void setEnabledCipherSuites(String[] cipherSuites) {
        this.enabledCipherSuites = (String[]) NativeCrypto.checkEnabledCipherSuites(cipherSuites).clone();
    }

    protected String[] getEnabledProtocols() {
        return (String[]) this.enabledProtocols.clone();
    }

    protected void setEnabledProtocols(String[] protocols) {
        this.enabledProtocols = (String[]) NativeCrypto.checkEnabledProtocols(protocols).clone();
    }

    protected void setUseClientMode(boolean mode) {
        this.client_mode = mode;
    }

    protected boolean getUseClientMode() {
        return this.client_mode;
    }

    protected void setNeedClientAuth(boolean need) {
        this.need_client_auth = need;
        this.want_client_auth = false;
    }

    protected boolean getNeedClientAuth() {
        return this.need_client_auth;
    }

    protected void setWantClientAuth(boolean want) {
        this.want_client_auth = want;
        this.need_client_auth = false;
    }

    protected boolean getWantClientAuth() {
        return this.want_client_auth;
    }

    protected void setEnableSessionCreation(boolean flag) {
        this.enable_session_creation = flag;
    }

    protected boolean getEnableSessionCreation() {
        return this.enable_session_creation;
    }

    protected void setUseSni(boolean flag) {
        this.useSni = Boolean.valueOf(flag);
    }

    protected boolean getUseSni() {
        return this.useSni != null ? this.useSni.booleanValue() : isSniEnabledByDefault();
    }

    static byte[][] encodeIssuerX509Principals(X509Certificate[] certificates) throws CertificateEncodingException {
        byte[][] principalBytes = new byte[certificates.length][];
        for (int i = 0; i < certificates.length; i++) {
            principalBytes[i] = certificates[i].getIssuerX500Principal().getEncoded();
        }
        return principalBytes;
    }

    private static OpenSSLX509Certificate[] createCertChain(long[] certificateRefs) throws IOException {
        if (certificateRefs == null) {
            return null;
        }
        OpenSSLX509Certificate[] certificates = new OpenSSLX509Certificate[certificateRefs.length];
        for (int i = 0; i < certificateRefs.length; i++) {
            certificates[i] = new OpenSSLX509Certificate(certificateRefs[i]);
        }
        return certificates;
    }

    OpenSSLSessionImpl getSessionToReuse(long sslNativePointer, String hostname, int port) throws SSLException {
        if (!this.client_mode) {
            return null;
        }
        OpenSSLSessionImpl sessionToReuse = getCachedClientSession(this.clientSessionContext, hostname, port);
        if (sessionToReuse == null) {
            return sessionToReuse;
        }
        NativeCrypto.SSL_set_session(sslNativePointer, sessionToReuse.sslSessionNativePointer);
        return sessionToReuse;
    }

    void setTlsChannelId(long sslNativePointer, OpenSSLKey channelIdPrivateKey) throws SSLHandshakeException, SSLException {
        if (!this.channelIdEnabled) {
            return;
        }
        if (!this.client_mode) {
            NativeCrypto.SSL_enable_tls_channel_id(sslNativePointer);
        } else if (channelIdPrivateKey == null) {
            throw new SSLHandshakeException("Invalid TLS channel ID key specified");
        } else {
            NativeCrypto.SSL_set1_tls_channel_id(sslNativePointer, channelIdPrivateKey.getPkeyContext());
        }
    }

    void setCertificate(long sslNativePointer, String alias) throws CertificateEncodingException, SSLException {
        if (alias != null) {
            X509KeyManager keyManager = getX509KeyManager();
            if (keyManager != null) {
                PrivateKey privateKey = keyManager.getPrivateKey(alias);
                if (privateKey != null) {
                    X509Certificate[] certificates = keyManager.getCertificateChain(alias);
                    if (certificates != null) {
                        OpenSSLX509Certificate[] openSslCerts = new OpenSSLX509Certificate[certificates.length];
                        long[] x509refs = new long[certificates.length];
                        for (int i = 0; i < certificates.length; i++) {
                            OpenSSLX509Certificate openSslCert = OpenSSLX509Certificate.fromCertificate(certificates[i]);
                            openSslCerts[i] = openSslCert;
                            x509refs[i] = openSslCert.getContext();
                        }
                        NativeCrypto.SSL_use_certificate(sslNativePointer, x509refs);
                        try {
                            OpenSSLKey key = OpenSSLKey.fromPrivateKey(privateKey);
                            NativeCrypto.SSL_use_PrivateKey(sslNativePointer, key.getPkeyContext());
                            if (!key.isWrapped()) {
                                NativeCrypto.SSL_check_private_key(sslNativePointer);
                            }
                        } catch (InvalidKeyException e) {
                            throw new SSLException(e);
                        }
                    }
                }
            }
        }
    }

    void setSSLParameters(long sslCtxNativePointer, long sslNativePointer, AliasChooser chooser, PSKCallbacks pskCallbacks, String sniHostname) throws SSLException, IOException {
        if (this.npnProtocols != null) {
            NativeCrypto.SSL_CTX_enable_npn(sslCtxNativePointer);
        }
        if (this.client_mode && this.alpnProtocols != null) {
            NativeCrypto.SSL_set_alpn_protos(sslNativePointer, this.alpnProtocols);
        }
        NativeCrypto.setEnabledProtocols(sslNativePointer, this.enabledProtocols);
        NativeCrypto.setEnabledCipherSuites(sslNativePointer, this.enabledCipherSuites);
        if (!this.client_mode) {
            String keyType;
            Set<String> keyTypes = new HashSet();
            for (long sslCipherNativePointer : NativeCrypto.SSL_get_ciphers(sslNativePointer)) {
                keyType = getServerX509KeyType(sslCipherNativePointer);
                if (keyType != null) {
                    keyTypes.add(keyType);
                }
            }
            if (getX509KeyManager() != null) {
                for (String keyType2 : keyTypes) {
                    try {
                        setCertificate(sslNativePointer, chooser.chooseServerAlias(this.x509KeyManager, keyType2));
                    } catch (CertificateEncodingException e) {
                        throw new IOException(e);
                    }
                }
            }
        }
        PSKKeyManager pskKeyManager = getPSKKeyManager();
        if (pskKeyManager != null) {
            boolean pskEnabled = false;
            for (String enabledCipherSuite : this.enabledCipherSuites) {
                if (enabledCipherSuite != null) {
                    if (enabledCipherSuite.contains("PSK")) {
                        pskEnabled = true;
                        break;
                    }
                }
            }
            if (pskEnabled) {
                if (this.client_mode) {
                    NativeCrypto.set_SSL_psk_client_callback_enabled(sslNativePointer, true);
                } else {
                    NativeCrypto.set_SSL_psk_server_callback_enabled(sslNativePointer, true);
                    NativeCrypto.SSL_use_psk_identity_hint(sslNativePointer, pskCallbacks.chooseServerPSKIdentityHint(pskKeyManager));
                }
            }
        }
        if (this.useSessionTickets) {
            NativeCrypto.SSL_clear_options(sslNativePointer, NativeCrypto.SSL_OP_NO_TICKET);
        }
        if (getUseSni() && AddressUtils.isValidSniHostname(sniHostname)) {
            NativeCrypto.SSL_set_tlsext_host_name(sslNativePointer, sniHostname);
        }
        NativeCrypto.SSL_set_mode(sslNativePointer, 256);
        boolean enableSessionCreation = getEnableSessionCreation();
        if (!enableSessionCreation) {
            NativeCrypto.SSL_set_session_creation_enabled(sslNativePointer, enableSessionCreation);
        }
    }

    private static boolean isValidSniHostname(String sniHostname) {
        if (sniHostname == null || sniHostname.indexOf(46) == -1 || Platform.isLiteralIpAddress(sniHostname)) {
            return false;
        }
        return true;
    }

    private boolean isSniEnabledByDefault() {
        String enableSNI = System.getProperty("jsse.enableSNIExtension", Platform.isSniEnabledByDefault() ? "true" : "false");
        if ("true".equalsIgnoreCase(enableSNI)) {
            return true;
        }
        if ("false".equalsIgnoreCase(enableSNI)) {
            return false;
        }
        throw new RuntimeException("Can only set \"jsse.enableSNIExtension\" to \"true\" or \"false\"");
    }

    void setCertificateValidation(long sslNativePointer) throws IOException {
        if (!this.client_mode) {
            boolean certRequested;
            if (getNeedClientAuth()) {
                NativeCrypto.SSL_set_verify(sslNativePointer, 3);
                certRequested = true;
            } else if (getWantClientAuth()) {
                NativeCrypto.SSL_set_verify(sslNativePointer, 1);
                certRequested = true;
            } else {
                NativeCrypto.SSL_set_verify(sslNativePointer, 0);
                certRequested = false;
            }
            if (certRequested) {
                X509Certificate[] issuers = getX509TrustManager().getAcceptedIssuers();
                if (issuers != null && issuers.length != 0) {
                    try {
                        NativeCrypto.SSL_set_client_CA_list(sslNativePointer, encodeIssuerX509Principals(issuers));
                    } catch (CertificateEncodingException e) {
                        throw new IOException("Problem encoding principals", e);
                    }
                }
            }
        }
    }

    OpenSSLSessionImpl setupSession(long sslSessionNativePointer, long sslNativePointer, OpenSSLSessionImpl sessionToReuse, String hostname, int port, boolean handshakeCompleted) throws IOException {
        byte[] sessionId = NativeCrypto.SSL_SESSION_session_id(sslSessionNativePointer);
        OpenSSLSessionImpl sslSession;
        if (sessionToReuse != null && Arrays.equals(sessionToReuse.getId(), sessionId)) {
            sslSession = sessionToReuse;
            sslSession.lastAccessedTime = System.currentTimeMillis();
            NativeCrypto.SSL_SESSION_free(sslSessionNativePointer);
            return sslSession;
        } else if (getEnableSessionCreation()) {
            sslSession = new OpenSSLSessionImpl(sslSessionNativePointer, createCertChain(NativeCrypto.SSL_get_certificate(sslNativePointer)), createCertChain(NativeCrypto.SSL_get_peer_cert_chain(sslNativePointer)), hostname, port, getSessionContext());
            if (!handshakeCompleted) {
                return sslSession;
            }
            getSessionContext().putSession(sslSession);
            return sslSession;
        } else {
            throw new IllegalStateException("SSL Session may not be created");
        }
    }

    void chooseClientCertificate(byte[] keyTypeBytes, byte[][] asn1DerEncodedPrincipals, long sslNativePointer, AliasChooser chooser) throws SSLException, CertificateEncodingException {
        int i;
        X500Principal[] issuers;
        String[] keyTypes = new String[keyTypeBytes.length];
        for (i = 0; i < keyTypeBytes.length; i++) {
            keyTypes[i] = getClientKeyType(keyTypeBytes[i]);
        }
        if (asn1DerEncodedPrincipals == null) {
            issuers = null;
        } else {
            issuers = new X500Principal[asn1DerEncodedPrincipals.length];
            for (i = 0; i < asn1DerEncodedPrincipals.length; i++) {
                issuers[i] = new X500Principal(asn1DerEncodedPrincipals[i]);
            }
        }
        X509KeyManager keyManager = getX509KeyManager();
        setCertificate(sslNativePointer, keyManager != null ? chooser.chooseClientAlias(keyManager, issuers, keyTypes) : null);
    }

    int clientPSKKeyRequested(String identityHint, byte[] identityBytesOut, byte[] key, PSKCallbacks pskCallbacks) {
        PSKKeyManager pskKeyManager = getPSKKeyManager();
        if (pskKeyManager == null) {
            return 0;
        }
        byte[] identityBytes;
        String identity = pskCallbacks.chooseClientPSKIdentity(pskKeyManager, identityHint);
        if (identity == null) {
            identity = "";
            identityBytes = EmptyArray.BYTE;
        } else if (identity.isEmpty()) {
            identityBytes = EmptyArray.BYTE;
        } else {
            try {
                identityBytes = identity.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("UTF-8 encoding not supported", e);
            }
        }
        if (identityBytes.length + 1 > identityBytesOut.length) {
            return 0;
        }
        if (identityBytes.length > 0) {
            System.arraycopy(identityBytes, 0, identityBytesOut, 0, identityBytes.length);
        }
        identityBytesOut[identityBytes.length] = (byte) 0;
        byte[] secretKeyBytes = pskCallbacks.getPSKKey(pskKeyManager, identityHint, identity).getEncoded();
        if (secretKeyBytes == null || secretKeyBytes.length > key.length) {
            return 0;
        }
        System.arraycopy(secretKeyBytes, 0, key, 0, secretKeyBytes.length);
        return secretKeyBytes.length;
    }

    int serverPSKKeyRequested(String identityHint, String identity, byte[] key, PSKCallbacks pskCallbacks) {
        PSKKeyManager pskKeyManager = getPSKKeyManager();
        if (pskKeyManager == null) {
            return 0;
        }
        byte[] secretKeyBytes = pskCallbacks.getPSKKey(pskKeyManager, identityHint, identity).getEncoded();
        if (secretKeyBytes == null || secretKeyBytes.length > key.length) {
            return 0;
        }
        System.arraycopy(secretKeyBytes, 0, key, 0, secretKeyBytes.length);
        return secretKeyBytes.length;
    }

    OpenSSLSessionImpl getCachedClientSession(ClientSessionContext sessionContext, String hostName, int port) {
        if (hostName == null) {
            return null;
        }
        OpenSSLSessionImpl session = (OpenSSLSessionImpl) sessionContext.getSession(hostName, port);
        if (session == null) {
            return null;
        }
        String protocol = session.getProtocol();
        boolean protocolFound = false;
        for (String enabledProtocol : this.enabledProtocols) {
            if (protocol.equals(enabledProtocol)) {
                protocolFound = true;
                break;
            }
        }
        if (!protocolFound) {
            return null;
        }
        String cipherSuite = session.getCipherSuite();
        boolean cipherSuiteFound = false;
        for (String enabledCipherSuite : this.enabledCipherSuites) {
            if (cipherSuite.equals(enabledCipherSuite)) {
                cipherSuiteFound = true;
                break;
            }
        }
        if (cipherSuiteFound) {
            return session;
        }
        return null;
    }

    protected Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    private static X509KeyManager getDefaultX509KeyManager() throws KeyManagementException {
        X509KeyManager result = defaultX509KeyManager;
        if (result != null) {
            return result;
        }
        result = createDefaultX509KeyManager();
        defaultX509KeyManager = result;
        return result;
    }

    private static X509KeyManager createDefaultX509KeyManager() throws KeyManagementException {
        try {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(null, null);
            KeyManager[] kms = kmf.getKeyManagers();
            X509KeyManager result = findFirstX509KeyManager(kms);
            if (result != null) {
                return result;
            }
            throw new KeyManagementException("No X509KeyManager among default KeyManagers: " + Arrays.toString(kms));
        } catch (NoSuchAlgorithmException e) {
            throw new KeyManagementException(e);
        } catch (KeyStoreException e2) {
            throw new KeyManagementException(e2);
        } catch (UnrecoverableKeyException e3) {
            throw new KeyManagementException(e3);
        }
    }

    private static X509KeyManager findFirstX509KeyManager(KeyManager[] kms) {
        for (KeyManager km : kms) {
            if (km instanceof X509KeyManager) {
                return (X509KeyManager) km;
            }
        }
        return null;
    }

    private static PSKKeyManager findFirstPSKKeyManager(KeyManager[] kms) {
        for (KeyManager km : kms) {
            if (km instanceof PSKKeyManager) {
                return (PSKKeyManager) km;
            }
        }
        return null;
    }

    public static X509TrustManager getDefaultX509TrustManager() throws KeyManagementException {
        X509TrustManager result = defaultX509TrustManager;
        if (result != null) {
            return result;
        }
        result = createDefaultX509TrustManager();
        defaultX509TrustManager = result;
        return result;
    }

    private static X509TrustManager createDefaultX509TrustManager() throws KeyManagementException {
        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore) null);
            TrustManager[] tms = tmf.getTrustManagers();
            X509TrustManager trustManager = findFirstX509TrustManager(tms);
            if (trustManager != null) {
                return trustManager;
            }
            throw new KeyManagementException("No X509TrustManager in among default TrustManagers: " + Arrays.toString(tms));
        } catch (NoSuchAlgorithmException e) {
            throw new KeyManagementException(e);
        } catch (KeyStoreException e2) {
            throw new KeyManagementException(e2);
        }
    }

    private static X509TrustManager findFirstX509TrustManager(TrustManager[] tms) {
        for (TrustManager tm : tms) {
            if (tm instanceof X509TrustManager) {
                return (X509TrustManager) tm;
            }
        }
        return null;
    }

    public String getEndpointIdentificationAlgorithm() {
        return this.endpointIdentificationAlgorithm;
    }

    public void setEndpointIdentificationAlgorithm(String endpointIdentificationAlgorithm) {
        this.endpointIdentificationAlgorithm = endpointIdentificationAlgorithm;
    }

    private static String getServerX509KeyType(long sslCipherNative) throws SSLException {
        int algorithm_mkey = NativeCrypto.get_SSL_CIPHER_algorithm_mkey(sslCipherNative);
        int algorithm_auth = NativeCrypto.get_SSL_CIPHER_algorithm_auth(sslCipherNative);
        switch (algorithm_mkey) {
            case NativeCrypto.SSL_kRSA /*1*/:
                return KEY_TYPE_RSA;
            case NativeCrypto.SSL_kEDH /*8*/:
                switch (algorithm_auth) {
                    case NativeCrypto.SSL_kRSA /*1*/:
                        return KEY_TYPE_RSA;
                    case NativeCrypto.SSL_kDHr /*2*/:
                        return KEY_TYPE_DSA;
                    case NativeCrypto.SSL_kDHd /*4*/:
                        return null;
                    default:
                        break;
                }
            case NativeCrypto.SSL_kECDHr /*32*/:
                return KEY_TYPE_EC_RSA;
            case NativeCrypto.SSL_kECDHe /*64*/:
                return KEY_TYPE_EC_EC;
            case PSKKeyManager.MAX_IDENTITY_LENGTH_BYTES /*128*/:
                switch (algorithm_auth) {
                    case NativeCrypto.SSL_kRSA /*1*/:
                        return KEY_TYPE_RSA;
                    case NativeCrypto.SSL_kDHd /*4*/:
                    case PSKKeyManager.MAX_IDENTITY_LENGTH_BYTES /*128*/:
                        return null;
                    case NativeCrypto.SSL_kECDHe /*64*/:
                        return KEY_TYPE_EC_EC;
                    default:
                        break;
                }
            case PSKKeyManager.MAX_KEY_LENGTH_BYTES /*256*/:
                return null;
        }
        throw new SSLException("Unsupported key exchange. mkey: 0x" + Long.toHexString(((long) algorithm_mkey) & 4294967295L) + ", auth: 0x" + Long.toHexString(((long) algorithm_auth) & 4294967295L));
    }

    public static String getClientKeyType(byte keyType) {
        switch (keyType) {
            case NativeCrypto.SSL_kRSA /*1*/:
                return KEY_TYPE_RSA;
            case NativeCrypto.SSL_kDHr /*2*/:
                return KEY_TYPE_DSA;
            case NativeCrypto.SSL_ST_OK /*3*/:
                return KEY_TYPE_DH_RSA;
            case NativeCrypto.SSL_kDHd /*4*/:
                return KEY_TYPE_DH_DSA;
            case NativeCrypto.SSL_kECDHe /*64*/:
                return KEY_TYPE_EC;
            case (byte) 65:
                return KEY_TYPE_EC_RSA;
            case (byte) 66:
                return KEY_TYPE_EC_EC;
            default:
                return null;
        }
    }

    private static String[] getDefaultCipherSuites(boolean x509CipherSuitesNeeded, boolean pskCipherSuitesNeeded) {
        String[][] strArr;
        if (x509CipherSuitesNeeded) {
            if (pskCipherSuitesNeeded) {
                strArr = new String[3][];
                strArr[0] = NativeCrypto.DEFAULT_PSK_CIPHER_SUITES;
                strArr[1] = NativeCrypto.DEFAULT_X509_CIPHER_SUITES;
                strArr[2] = new String[]{NativeCrypto.TLS_EMPTY_RENEGOTIATION_INFO_SCSV};
                return concat(strArr);
            }
            strArr = new String[2][];
            strArr[0] = NativeCrypto.DEFAULT_X509_CIPHER_SUITES;
            strArr[1] = new String[]{NativeCrypto.TLS_EMPTY_RENEGOTIATION_INFO_SCSV};
            return concat(strArr);
        } else if (pskCipherSuitesNeeded) {
            strArr = new String[2][];
            strArr[0] = NativeCrypto.DEFAULT_PSK_CIPHER_SUITES;
            strArr[1] = new String[]{NativeCrypto.TLS_EMPTY_RENEGOTIATION_INFO_SCSV};
            return concat(strArr);
        } else {
            return new String[]{NativeCrypto.TLS_EMPTY_RENEGOTIATION_INFO_SCSV};
        }
    }

    private static String[] getDefaultProtocols() {
        return (String[]) NativeCrypto.DEFAULT_PROTOCOLS.clone();
    }

    private static String[] concat(String[]... arrays) {
        int resultLength = 0;
        for (String[] array : arrays) {
            resultLength += array.length;
        }
        String[] result = new String[resultLength];
        int resultOffset = 0;
        for (String[] array2 : arrays) {
            System.arraycopy(array2, 0, result, resultOffset, array2.length);
            resultOffset += array2.length;
        }
        return result;
    }
}
