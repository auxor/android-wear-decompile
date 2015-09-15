package android.security;

import android.net.ProxyInfo;
import android.net.wifi.WifiEnterpriseConfig;
import android.util.Log;
import com.android.org.conscrypt.OpenSSLEngine;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore.Entry;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStore.TrustedCertificateEntry;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class AndroidKeyStore extends KeyStoreSpi {
    public static final String NAME = "AndroidKeyStore";
    private KeyStore mKeyStore;

    public Key engineGetKey(String alias, char[] password) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        if (!isKeyEntry(alias)) {
            return null;
        }
        try {
            return OpenSSLEngine.getInstance(WifiEnterpriseConfig.ENGINE_ID_KEYSTORE).getPrivateKeyById(Credentials.USER_PRIVATE_KEY + alias);
        } catch (InvalidKeyException e) {
            UnrecoverableKeyException t = new UnrecoverableKeyException("Can't get key");
            t.initCause(e);
            throw t;
        }
    }

    public Certificate[] engineGetCertificateChain(String alias) {
        if (alias == null) {
            throw new NullPointerException("alias == null");
        }
        X509Certificate leaf = (X509Certificate) engineGetCertificate(alias);
        if (leaf == null) {
            return null;
        }
        Certificate[] caList;
        byte[] caBytes = this.mKeyStore.get(Credentials.CA_CERTIFICATE + alias);
        if (caBytes != null) {
            Collection<X509Certificate> caChain = toCertificates(caBytes);
            caList = new Certificate[(caChain.size() + 1)];
            int i = 1;
            for (Certificate certificate : caChain) {
                int i2 = i + 1;
                caList[i] = certificate;
                i = i2;
            }
        } else {
            caList = new Certificate[1];
        }
        caList[0] = leaf;
        return caList;
    }

    public Certificate engineGetCertificate(String alias) {
        if (alias == null) {
            throw new NullPointerException("alias == null");
        }
        byte[] certificate = this.mKeyStore.get(Credentials.USER_CERTIFICATE + alias);
        if (certificate != null) {
            return toCertificate(certificate);
        }
        certificate = this.mKeyStore.get(Credentials.CA_CERTIFICATE + alias);
        if (certificate != null) {
            return toCertificate(certificate);
        }
        return null;
    }

    private static X509Certificate toCertificate(byte[] bytes) {
        try {
            return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(bytes));
        } catch (CertificateException e) {
            Log.w(NAME, "Couldn't parse certificate in keystore", e);
            return null;
        }
    }

    private static Collection<X509Certificate> toCertificates(byte[] bytes) {
        try {
            return CertificateFactory.getInstance("X.509").generateCertificates(new ByteArrayInputStream(bytes));
        } catch (CertificateException e) {
            Log.w(NAME, "Couldn't parse certificates in keystore", e);
            return new ArrayList();
        }
    }

    private Date getModificationDate(String alias) {
        long epochMillis = this.mKeyStore.getmtime(alias);
        if (epochMillis == -1) {
            return null;
        }
        return new Date(epochMillis);
    }

    public Date engineGetCreationDate(String alias) {
        if (alias == null) {
            throw new NullPointerException("alias == null");
        }
        Date d = getModificationDate(Credentials.USER_PRIVATE_KEY + alias);
        if (d != null) {
            return d;
        }
        d = getModificationDate(Credentials.USER_CERTIFICATE + alias);
        if (d != null) {
            return d;
        }
        return getModificationDate(Credentials.CA_CERTIFICATE + alias);
    }

    public void engineSetKeyEntry(String alias, Key key, char[] password, Certificate[] chain) throws KeyStoreException {
        if (password != null && password.length > 0) {
            throw new KeyStoreException("entries cannot be protected with passwords");
        } else if (key instanceof PrivateKey) {
            setPrivateKeyEntry(alias, (PrivateKey) key, chain, null);
        } else {
            throw new KeyStoreException("Only PrivateKeys are supported");
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setPrivateKeyEntry(java.lang.String r23, java.security.PrivateKey r24, java.security.cert.Certificate[] r25, android.security.KeyStoreParameter r26) throws java.security.KeyStoreException {
        /*
        r22 = this;
        r10 = 0;
        r0 = r24;
        r0 = r0 instanceof com.android.org.conscrypt.OpenSSLKeyHolder;
        r19 = r0;
        if (r19 == 0) goto L_0x005e;
    L_0x0009:
        r19 = r24;
        r19 = (com.android.org.conscrypt.OpenSSLKeyHolder) r19;
        r19 = r19.getOpenSSLKey();
        r14 = r19.getAlias();
    L_0x0015:
        if (r14 == 0) goto L_0x0072;
    L_0x0017:
        r19 = "USRPKEY_";
        r0 = r19;
        r19 = r14.startsWith(r0);
        if (r19 == 0) goto L_0x0072;
    L_0x0021:
        r19 = "USRPKEY_";
        r19 = r19.length();
        r0 = r19;
        r12 = r14.substring(r0);
        r0 = r23;
        r19 = r0.equals(r12);
        if (r19 != 0) goto L_0x0060;
    L_0x0035:
        r19 = new java.security.KeyStoreException;
        r20 = new java.lang.StringBuilder;
        r20.<init>();
        r21 = "Can only replace keys with same alias: ";
        r20 = r20.append(r21);
        r0 = r20;
        r1 = r23;
        r20 = r0.append(r1);
        r21 = " != ";
        r20 = r20.append(r21);
        r0 = r20;
        r20 = r0.append(r12);
        r20 = r20.toString();
        r19.<init>(r20);
        throw r19;
    L_0x005e:
        r14 = 0;
        goto L_0x0015;
    L_0x0060:
        r15 = 0;
    L_0x0061:
        if (r25 == 0) goto L_0x006a;
    L_0x0063:
        r0 = r25;
        r0 = r0.length;
        r19 = r0;
        if (r19 != 0) goto L_0x009a;
    L_0x006a:
        r19 = new java.security.KeyStoreException;
        r20 = "Must supply at least one Certificate with PrivateKey";
        r19.<init>(r20);
        throw r19;
    L_0x0072:
        r11 = r24.getFormat();
        if (r11 == 0) goto L_0x0082;
    L_0x0078:
        r19 = "PKCS#8";
        r0 = r19;
        r19 = r0.equals(r11);
        if (r19 != 0) goto L_0x008a;
    L_0x0082:
        r19 = new java.security.KeyStoreException;
        r20 = "Only PrivateKeys that can be encoded into PKCS#8 are supported";
        r19.<init>(r20);
        throw r19;
    L_0x008a:
        r10 = r24.getEncoded();
        if (r10 != 0) goto L_0x0098;
    L_0x0090:
        r19 = new java.security.KeyStoreException;
        r20 = "PrivateKey has no encoding";
        r19.<init>(r20);
        throw r19;
    L_0x0098:
        r15 = 1;
        goto L_0x0061;
    L_0x009a:
        r0 = r25;
        r0 = r0.length;
        r19 = r0;
        r0 = r19;
        r0 = new java.security.cert.X509Certificate[r0];
        r18 = r0;
        r9 = 0;
    L_0x00a6:
        r0 = r25;
        r0 = r0.length;
        r19 = r0;
        r0 = r19;
        if (r9 >= r0) goto L_0x0106;
    L_0x00af:
        r19 = "X.509";
        r20 = r25[r9];
        r20 = r20.getType();
        r19 = r19.equals(r20);
        if (r19 != 0) goto L_0x00d8;
    L_0x00bd:
        r19 = new java.security.KeyStoreException;
        r20 = new java.lang.StringBuilder;
        r20.<init>();
        r21 = "Certificates must be in X.509 format: invalid cert #";
        r20 = r20.append(r21);
        r0 = r20;
        r20 = r0.append(r9);
        r20 = r20.toString();
        r19.<init>(r20);
        throw r19;
    L_0x00d8:
        r19 = r25[r9];
        r0 = r19;
        r0 = r0 instanceof java.security.cert.X509Certificate;
        r19 = r0;
        if (r19 != 0) goto L_0x00fd;
    L_0x00e2:
        r19 = new java.security.KeyStoreException;
        r20 = new java.lang.StringBuilder;
        r20.<init>();
        r21 = "Certificates must be in X.509 format: invalid cert #";
        r20 = r20.append(r21);
        r0 = r20;
        r20 = r0.append(r9);
        r20 = r20.toString();
        r19.<init>(r20);
        throw r19;
    L_0x00fd:
        r19 = r25[r9];
        r19 = (java.security.cert.X509Certificate) r19;
        r18[r9] = r19;
        r9 = r9 + 1;
        goto L_0x00a6;
    L_0x0106:
        r19 = 0;
        r19 = r18[r19];	 Catch:{ CertificateEncodingException -> 0x0146 }
        r17 = r19.getEncoded();	 Catch:{ CertificateEncodingException -> 0x0146 }
        r0 = r25;
        r0 = r0.length;
        r19 = r0;
        r20 = 1;
        r0 = r19;
        r1 = r20;
        if (r0 <= r1) goto L_0x0198;
    L_0x011b:
        r0 = r18;
        r0 = r0.length;
        r19 = r0;
        r19 = r19 + -1;
        r0 = r19;
        r5 = new byte[r0][];
        r16 = 0;
        r9 = 0;
    L_0x0129:
        r0 = r5.length;
        r19 = r0;
        r0 = r19;
        if (r9 >= r0) goto L_0x0173;
    L_0x0130:
        r19 = r9 + 1;
        r19 = r18[r19];	 Catch:{ CertificateEncodingException -> 0x0153 }
        r19 = r19.getEncoded();	 Catch:{ CertificateEncodingException -> 0x0153 }
        r5[r9] = r19;	 Catch:{ CertificateEncodingException -> 0x0153 }
        r19 = r5[r9];	 Catch:{ CertificateEncodingException -> 0x0153 }
        r0 = r19;
        r0 = r0.length;	 Catch:{ CertificateEncodingException -> 0x0153 }
        r19 = r0;
        r16 = r16 + r19;
        r9 = r9 + 1;
        goto L_0x0129;
    L_0x0146:
        r7 = move-exception;
        r19 = new java.security.KeyStoreException;
        r20 = "Couldn't encode certificate #1";
        r0 = r19;
        r1 = r20;
        r0.<init>(r1, r7);
        throw r19;
    L_0x0153:
        r7 = move-exception;
        r19 = new java.security.KeyStoreException;
        r20 = new java.lang.StringBuilder;
        r20.<init>();
        r21 = "Can't encode Certificate #";
        r20 = r20.append(r21);
        r0 = r20;
        r20 = r0.append(r9);
        r20 = r20.toString();
        r0 = r19;
        r1 = r20;
        r0.<init>(r1, r7);
        throw r19;
    L_0x0173:
        r0 = r16;
        r6 = new byte[r0];
        r13 = 0;
        r9 = 0;
    L_0x0179:
        r0 = r5.length;
        r19 = r0;
        r0 = r19;
        if (r9 >= r0) goto L_0x0199;
    L_0x0180:
        r19 = r5[r9];
        r0 = r19;
        r4 = r0.length;
        r19 = r5[r9];
        r20 = 0;
        r0 = r19;
        r1 = r20;
        java.lang.System.arraycopy(r0, r1, r6, r13, r4);
        r13 = r13 + r4;
        r19 = 0;
        r5[r9] = r19;
        r9 = r9 + 1;
        goto L_0x0179;
    L_0x0198:
        r6 = 0;
    L_0x0199:
        if (r15 == 0) goto L_0x01ed;
    L_0x019b:
        r0 = r22;
        r0 = r0.mKeyStore;
        r19 = r0;
        r0 = r19;
        r1 = r23;
        android.security.Credentials.deleteAllTypesForAlias(r0, r1);
    L_0x01a8:
        if (r26 != 0) goto L_0x01fb;
    L_0x01aa:
        r8 = 0;
    L_0x01ab:
        if (r15 == 0) goto L_0x0200;
    L_0x01ad:
        r0 = r22;
        r0 = r0.mKeyStore;
        r19 = r0;
        r20 = new java.lang.StringBuilder;
        r20.<init>();
        r21 = "USRPKEY_";
        r20 = r20.append(r21);
        r0 = r20;
        r1 = r23;
        r20 = r0.append(r1);
        r20 = r20.toString();
        r21 = -1;
        r0 = r19;
        r1 = r20;
        r2 = r21;
        r19 = r0.importKey(r1, r10, r2, r8);
        if (r19 != 0) goto L_0x0200;
    L_0x01d8:
        r0 = r22;
        r0 = r0.mKeyStore;
        r19 = r0;
        r0 = r19;
        r1 = r23;
        android.security.Credentials.deleteAllTypesForAlias(r0, r1);
        r19 = new java.security.KeyStoreException;
        r20 = "Couldn't put private key in keystore";
        r19.<init>(r20);
        throw r19;
    L_0x01ed:
        r0 = r22;
        r0 = r0.mKeyStore;
        r19 = r0;
        r0 = r19;
        r1 = r23;
        android.security.Credentials.deleteCertificateTypesForAlias(r0, r1);
        goto L_0x01a8;
    L_0x01fb:
        r8 = r26.getFlags();
        goto L_0x01ab;
    L_0x0200:
        r0 = r22;
        r0 = r0.mKeyStore;
        r19 = r0;
        r20 = new java.lang.StringBuilder;
        r20.<init>();
        r21 = "USRCERT_";
        r20 = r20.append(r21);
        r0 = r20;
        r1 = r23;
        r20 = r0.append(r1);
        r20 = r20.toString();
        r21 = -1;
        r0 = r19;
        r1 = r20;
        r2 = r17;
        r3 = r21;
        r19 = r0.put(r1, r2, r3, r8);
        if (r19 != 0) goto L_0x0242;
    L_0x022d:
        r0 = r22;
        r0 = r0.mKeyStore;
        r19 = r0;
        r0 = r19;
        r1 = r23;
        android.security.Credentials.deleteAllTypesForAlias(r0, r1);
        r19 = new java.security.KeyStoreException;
        r20 = "Couldn't put certificate #1 in keystore";
        r19.<init>(r20);
        throw r19;
    L_0x0242:
        if (r6 == 0) goto L_0x0284;
    L_0x0244:
        r0 = r22;
        r0 = r0.mKeyStore;
        r19 = r0;
        r20 = new java.lang.StringBuilder;
        r20.<init>();
        r21 = "CACERT_";
        r20 = r20.append(r21);
        r0 = r20;
        r1 = r23;
        r20 = r0.append(r1);
        r20 = r20.toString();
        r21 = -1;
        r0 = r19;
        r1 = r20;
        r2 = r21;
        r19 = r0.put(r1, r6, r2, r8);
        if (r19 != 0) goto L_0x0284;
    L_0x026f:
        r0 = r22;
        r0 = r0.mKeyStore;
        r19 = r0;
        r0 = r19;
        r1 = r23;
        android.security.Credentials.deleteAllTypesForAlias(r0, r1);
        r19 = new java.security.KeyStoreException;
        r20 = "Couldn't put certificate chain in keystore";
        r19.<init>(r20);
        throw r19;
    L_0x0284:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.security.AndroidKeyStore.setPrivateKeyEntry(java.lang.String, java.security.PrivateKey, java.security.cert.Certificate[], android.security.KeyStoreParameter):void");
    }

    public void engineSetKeyEntry(String alias, byte[] userKey, Certificate[] chain) throws KeyStoreException {
        throw new KeyStoreException("Operation not supported because key encoding is unknown");
    }

    public void engineSetCertificateEntry(String alias, Certificate cert) throws KeyStoreException {
        if (isKeyEntry(alias)) {
            throw new KeyStoreException("Entry exists and is not a trusted certificate");
        } else if (cert == null) {
            throw new NullPointerException("cert == null");
        } else {
            try {
                if (!this.mKeyStore.put(Credentials.CA_CERTIFICATE + alias, cert.getEncoded(), -1, 0)) {
                    throw new KeyStoreException("Couldn't insert certificate; is KeyStore initialized?");
                }
            } catch (CertificateEncodingException e) {
                throw new KeyStoreException(e);
            }
        }
    }

    public void engineDeleteEntry(String alias) throws KeyStoreException {
        if ((isKeyEntry(alias) || isCertificateEntry(alias)) && !Credentials.deleteAllTypesForAlias(this.mKeyStore, alias)) {
            throw new KeyStoreException("No such entry " + alias);
        }
    }

    private Set<String> getUniqueAliases() {
        String[] rawAliases = this.mKeyStore.saw(ProxyInfo.LOCAL_EXCL_LIST);
        if (rawAliases == null) {
            return new HashSet();
        }
        Set<String> aliases = new HashSet(rawAliases.length);
        for (String alias : rawAliases) {
            int idx = alias.indexOf(95);
            if (idx == -1 || alias.length() <= idx) {
                Log.e(NAME, "invalid alias: " + alias);
            } else {
                aliases.add(new String(alias.substring(idx + 1)));
            }
        }
        return aliases;
    }

    public Enumeration<String> engineAliases() {
        return Collections.enumeration(getUniqueAliases());
    }

    public boolean engineContainsAlias(String alias) {
        if (alias != null) {
            return this.mKeyStore.contains(new StringBuilder().append(Credentials.USER_PRIVATE_KEY).append(alias).toString()) || this.mKeyStore.contains(Credentials.USER_CERTIFICATE + alias) || this.mKeyStore.contains(Credentials.CA_CERTIFICATE + alias);
        } else {
            throw new NullPointerException("alias == null");
        }
    }

    public int engineSize() {
        return getUniqueAliases().size();
    }

    public boolean engineIsKeyEntry(String alias) {
        return isKeyEntry(alias);
    }

    private boolean isKeyEntry(String alias) {
        if (alias != null) {
            return this.mKeyStore.contains(Credentials.USER_PRIVATE_KEY + alias);
        }
        throw new NullPointerException("alias == null");
    }

    private boolean isCertificateEntry(String alias) {
        if (alias != null) {
            return this.mKeyStore.contains(Credentials.CA_CERTIFICATE + alias);
        }
        throw new NullPointerException("alias == null");
    }

    public boolean engineIsCertificateEntry(String alias) {
        return !isKeyEntry(alias) && isCertificateEntry(alias);
    }

    public String engineGetCertificateAlias(Certificate cert) {
        if (cert == null) {
            return null;
        }
        Set<String> nonCaEntries = new HashSet();
        String[] certAliases = this.mKeyStore.saw(Credentials.USER_CERTIFICATE);
        if (certAliases != null) {
            for (String alias : certAliases) {
                byte[] certBytes = this.mKeyStore.get(Credentials.USER_CERTIFICATE + alias);
                if (certBytes != null) {
                    Certificate c = toCertificate(certBytes);
                    nonCaEntries.add(alias);
                    if (cert.equals(c)) {
                        return alias;
                    }
                }
            }
        }
        String[] caAliases = this.mKeyStore.saw(Credentials.CA_CERTIFICATE);
        if (certAliases != null) {
            for (String alias2 : caAliases) {
                if (!nonCaEntries.contains(alias2) && this.mKeyStore.get(Credentials.CA_CERTIFICATE + alias2) != null && cert.equals(toCertificate(this.mKeyStore.get(Credentials.CA_CERTIFICATE + alias2)))) {
                    return alias2;
                }
            }
        }
        return null;
    }

    public void engineStore(OutputStream stream, char[] password) throws IOException, NoSuchAlgorithmException, CertificateException {
        throw new UnsupportedOperationException("Can not serialize AndroidKeyStore to OutputStream");
    }

    public void engineLoad(InputStream stream, char[] password) throws IOException, NoSuchAlgorithmException, CertificateException {
        if (stream != null) {
            throw new IllegalArgumentException("InputStream not supported");
        } else if (password != null) {
            throw new IllegalArgumentException("password not supported");
        } else {
            this.mKeyStore = KeyStore.getInstance();
        }
    }

    public void engineSetEntry(String alias, Entry entry, ProtectionParameter param) throws KeyStoreException {
        if (entry == null) {
            throw new KeyStoreException("entry == null");
        }
        if (engineContainsAlias(alias)) {
            engineDeleteEntry(alias);
        }
        if (entry instanceof TrustedCertificateEntry) {
            engineSetCertificateEntry(alias, ((TrustedCertificateEntry) entry).getTrustedCertificate());
        } else if (param != null && !(param instanceof KeyStoreParameter)) {
            throw new KeyStoreException("protParam should be android.security.KeyStoreParameter; was: " + param.getClass().getName());
        } else if (entry instanceof PrivateKeyEntry) {
            PrivateKeyEntry prE = (PrivateKeyEntry) entry;
            setPrivateKeyEntry(alias, prE.getPrivateKey(), prE.getCertificateChain(), (KeyStoreParameter) param);
        } else {
            throw new KeyStoreException("Entry must be a PrivateKeyEntry or TrustedCertificateEntry; was " + entry);
        }
    }
}
