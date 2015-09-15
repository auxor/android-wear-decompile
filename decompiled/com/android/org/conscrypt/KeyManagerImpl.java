package com.android.org.conscrypt;

import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;

public class KeyManagerImpl extends X509ExtendedKeyManager {
    private final Hashtable<String, PrivateKeyEntry> hash;

    public KeyManagerImpl(KeyStore keyStore, char[] pwd) {
        this.hash = new Hashtable();
        try {
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = (String) aliases.nextElement();
                try {
                    if (keyStore.entryInstanceOf(alias, PrivateKeyEntry.class)) {
                        this.hash.put(alias, (PrivateKeyEntry) keyStore.getEntry(alias, new PasswordProtection(pwd)));
                    }
                } catch (KeyStoreException e) {
                } catch (UnrecoverableEntryException e2) {
                } catch (NoSuchAlgorithmException e3) {
                }
            }
        } catch (KeyStoreException e4) {
        }
    }

    public String chooseClientAlias(String[] keyTypes, Principal[] issuers, Socket socket) {
        String[] al = chooseAlias(keyTypes, issuers);
        return al == null ? null : al[0];
    }

    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        String[] al = chooseAlias(new String[]{keyType}, issuers);
        return al == null ? null : al[0];
    }

    public X509Certificate[] getCertificateChain(String alias) {
        X509Certificate[] x509CertificateArr = null;
        if (alias != null && this.hash.containsKey(alias)) {
            Certificate[] certs = ((PrivateKeyEntry) this.hash.get(alias)).getCertificateChain();
            if (certs[0] instanceof X509Certificate) {
                x509CertificateArr = new X509Certificate[certs.length];
                for (int i = 0; i < certs.length; i++) {
                    x509CertificateArr[i] = (X509Certificate) certs[i];
                }
            }
        }
        return x509CertificateArr;
    }

    public String[] getClientAliases(String keyType, Principal[] issuers) {
        return chooseAlias(new String[]{keyType}, issuers);
    }

    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return chooseAlias(new String[]{keyType}, issuers);
    }

    public PrivateKey getPrivateKey(String alias) {
        if (alias != null && this.hash.containsKey(alias)) {
            return ((PrivateKeyEntry) this.hash.get(alias)).getPrivateKey();
        }
        return null;
    }

    public String chooseEngineClientAlias(String[] keyTypes, Principal[] issuers, SSLEngine engine) {
        String[] al = chooseAlias(keyTypes, issuers);
        return al == null ? null : al[0];
    }

    public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine engine) {
        String[] al = chooseAlias(new String[]{keyType}, issuers);
        return al == null ? null : al[0];
    }

    private String[] chooseAlias(String[] keyTypes, Principal[] issuers) {
        if (keyTypes == null || keyTypes.length == 0) {
            return null;
        }
        List<Principal> issuersList = issuers == null ? null : Arrays.asList(issuers);
        ArrayList<String> found = new ArrayList();
        Enumeration<String> aliases = this.hash.keys();
        while (aliases.hasMoreElements()) {
            String alias = (String) aliases.nextElement();
            Certificate[] chain = ((PrivateKeyEntry) this.hash.get(alias)).getCertificateChain();
            Certificate cert = chain[0];
            String certKeyAlg = cert.getPublicKey().getAlgorithm();
            String certSigAlg = cert instanceof X509Certificate ? ((X509Certificate) cert).getSigAlgName().toUpperCase(Locale.US) : null;
            for (String keyAlgorithm : keyTypes) {
                String keyAlgorithm2;
                if (keyAlgorithm2 != null) {
                    int index = keyAlgorithm2.indexOf(95);
                    String sigAlgorithm;
                    if (index == -1) {
                        sigAlgorithm = null;
                    } else {
                        sigAlgorithm = keyAlgorithm2.substring(index + 1);
                        keyAlgorithm2 = keyAlgorithm2.substring(0, index);
                    }
                    if (certKeyAlg.equals(keyAlgorithm2) && (sigAlgorithm == null || certSigAlg == null || certSigAlg.contains(sigAlgorithm))) {
                        if (issuers == null || issuers.length == 0) {
                            found.add(alias);
                        } else {
                            for (Certificate certFromChain : chain) {
                                if (certFromChain instanceof X509Certificate) {
                                    if (issuersList.contains(((X509Certificate) certFromChain).getIssuerX500Principal())) {
                                        found.add(alias);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (found.isEmpty()) {
            return null;
        }
        return (String[]) found.toArray(new String[found.size()]);
    }
}
