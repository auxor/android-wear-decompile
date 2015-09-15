package com.android.org.conscrypt;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public final class CryptoUpcalls {
    private static final String RSA_CRYPTO_ALGORITHM = "RSA/ECB/PKCS1Padding";

    private CryptoUpcalls() {
    }

    public static Provider getExternalProvider(String algorithm) {
        Provider selectedProvider = null;
        for (Provider p : Security.getProviders(algorithm)) {
            if (!p.getClass().getClassLoader().equals(CryptoUpcalls.class.getClassLoader())) {
                selectedProvider = p;
                break;
            }
        }
        if (selectedProvider == null) {
            System.err.println("Could not find external provider for algorithm: " + algorithm);
        }
        return selectedProvider;
    }

    public static byte[] rawSignDigestWithPrivateKey(PrivateKey javaKey, byte[] message) {
        String algorithm;
        byte[] bArr = null;
        if (javaKey instanceof RSAPrivateKey) {
            algorithm = "NONEwithRSA";
        } else if (javaKey instanceof DSAPrivateKey) {
            algorithm = "NONEwithDSA";
        } else if (javaKey instanceof ECPrivateKey) {
            algorithm = "NONEwithECDSA";
        } else {
            throw new RuntimeException("Unexpected key type: " + javaKey.toString());
        }
        Provider p = getExternalProvider("Signature." + algorithm);
        if (p != null) {
            Signature signature = null;
            try {
                signature = Signature.getInstance(algorithm, p);
            } catch (NoSuchAlgorithmException e) {
            }
            if (signature == null) {
                System.err.println("Unsupported private key algorithm: " + javaKey.getAlgorithm());
            } else {
                try {
                    signature.initSign(javaKey);
                    signature.update(message);
                    bArr = signature.sign();
                } catch (Exception e2) {
                    System.err.println("Exception while signing message with " + javaKey.getAlgorithm() + " private key:");
                    e2.printStackTrace();
                }
            }
        }
        return bArr;
    }

    public static byte[] rawCipherWithPrivateKey(PrivateKey javaKey, boolean encrypt, byte[] input) {
        byte[] bArr = null;
        if (javaKey instanceof RSAPrivateKey) {
            Provider p = getExternalProvider("Cipher.RSA/ECB/PKCS1Padding");
            if (p != null) {
                Cipher c = null;
                try {
                    c = Cipher.getInstance(RSA_CRYPTO_ALGORITHM, p);
                } catch (NoSuchAlgorithmException e) {
                } catch (NoSuchPaddingException e2) {
                }
                if (c == null) {
                    System.err.println("Unsupported private key algorithm: " + javaKey.getAlgorithm());
                }
                try {
                    c.init(encrypt ? 1 : 2, javaKey);
                    bArr = c.doFinal(input);
                } catch (Exception e3) {
                    System.err.println("Exception while ciphering message with " + javaKey.getAlgorithm() + " private key:");
                    e3.printStackTrace();
                }
            }
        } else {
            System.err.println("Unexpected key type: " + javaKey.toString());
        }
        return bArr;
    }
}
