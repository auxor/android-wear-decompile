package com.android.org.conscrypt;

import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;

public final class ChainStrengthAnalyzer {
    private static final int MIN_MODULUS = 1024;
    private static final String[] OID_BLACKLIST;

    static {
        OID_BLACKLIST = new String[]{"1.2.840.113549.1.1.4"};
    }

    public static final void check(X509Certificate[] chain) throws CertificateException {
        for (X509Certificate cert : chain) {
            checkCert(cert);
        }
    }

    private static final void checkCert(X509Certificate cert) throws CertificateException {
        checkModulusLength(cert);
        checkNotMD5(cert);
    }

    private static final void checkModulusLength(X509Certificate cert) throws CertificateException {
        PublicKey pubkey = cert.getPublicKey();
        if ((pubkey instanceof RSAPublicKey) && ((RSAPublicKey) pubkey).getModulus().bitLength() < MIN_MODULUS) {
            throw new CertificateException("Modulus is < 1024 bits");
        }
    }

    private static final void checkNotMD5(X509Certificate cert) throws CertificateException {
        String oid = cert.getSigAlgOID();
        for (String blacklisted : OID_BLACKLIST) {
            if (oid.equals(blacklisted)) {
                throw new CertificateException("Signature uses an insecure hash function");
            }
        }
    }
}
