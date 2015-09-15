package android.security;

import android.net.wifi.WifiEnterpriseConfig;
import android.security.KeyStore.State;
import android.view.KeyEvent;
import com.android.org.bouncycastle.x509.X509V3CertificateGenerator;
import com.android.org.conscrypt.OpenSSLEngine;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGeneratorSpi;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;

public class AndroidKeyPairGenerator extends KeyPairGeneratorSpi {
    private KeyStore mKeyStore;
    private KeyPairGeneratorSpec mSpec;

    public KeyPair generateKeyPair() {
        if (this.mKeyStore == null || this.mSpec == null) {
            throw new IllegalStateException("Must call initialize with an android.security.KeyPairGeneratorSpec first");
        } else if ((this.mSpec.getFlags() & 1) == 0 || this.mKeyStore.state() == State.UNLOCKED) {
            String alias = this.mSpec.getKeystoreAlias();
            Credentials.deleteAllTypesForAlias(this.mKeyStore, alias);
            int keyType = KeyStore.getKeyTypeForAlgorithm(this.mSpec.getKeyType());
            byte[][] args = getArgsForKeyType(keyType, this.mSpec.getAlgorithmParameterSpec());
            String privateKeyAlias = Credentials.USER_PRIVATE_KEY + alias;
            if (this.mKeyStore.generate(privateKeyAlias, -1, keyType, this.mSpec.getKeySize(), this.mSpec.getFlags(), args)) {
                try {
                    PrivateKey privKey = OpenSSLEngine.getInstance(WifiEnterpriseConfig.ENGINE_ID_KEYSTORE).getPrivateKeyById(privateKeyAlias);
                    try {
                        PublicKey pubKey = KeyFactory.getInstance(this.mSpec.getKeyType()).generatePublic(new X509EncodedKeySpec(this.mKeyStore.getPubkey(privateKeyAlias)));
                        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
                        certGen.setPublicKey(pubKey);
                        certGen.setSerialNumber(this.mSpec.getSerialNumber());
                        certGen.setSubjectDN(this.mSpec.getSubjectDN());
                        certGen.setIssuerDN(this.mSpec.getSubjectDN());
                        certGen.setNotBefore(this.mSpec.getStartDate());
                        certGen.setNotAfter(this.mSpec.getEndDate());
                        certGen.setSignatureAlgorithm(getDefaultSignatureAlgorithmForKeyType(this.mSpec.getKeyType()));
                        try {
                            try {
                                if (this.mKeyStore.put(Credentials.USER_CERTIFICATE + alias, certGen.generate(privKey).getEncoded(), -1, this.mSpec.getFlags())) {
                                    return new KeyPair(pubKey, privKey);
                                }
                                Credentials.deleteAllTypesForAlias(this.mKeyStore, alias);
                                throw new IllegalStateException("Can't store certificate in AndroidKeyStore");
                            } catch (CertificateEncodingException e) {
                                Credentials.deleteAllTypesForAlias(this.mKeyStore, alias);
                                throw new IllegalStateException("Can't get encoding of certificate", e);
                            }
                        } catch (Exception e2) {
                            Credentials.deleteAllTypesForAlias(this.mKeyStore, alias);
                            throw new IllegalStateException("Can't generate certificate", e2);
                        }
                    } catch (NoSuchAlgorithmException e3) {
                        throw new IllegalStateException("Can't instantiate key generator", e3);
                    } catch (InvalidKeySpecException e4) {
                        throw new IllegalStateException("keystore returned invalid key encoding", e4);
                    }
                } catch (InvalidKeyException e5) {
                    throw new RuntimeException("Can't get key", e5);
                }
            }
            throw new IllegalStateException("could not generate key in keystore");
        } else {
            throw new IllegalStateException("Android keystore must be in initialized and unlocked state if encryption is required");
        }
    }

    private static String getDefaultSignatureAlgorithmForKeyType(String keyType) {
        if ("RSA".equalsIgnoreCase(keyType)) {
            return "sha256WithRSA";
        }
        if ("DSA".equalsIgnoreCase(keyType)) {
            return "sha1WithDSA";
        }
        if ("EC".equalsIgnoreCase(keyType)) {
            return "sha256WithECDSA";
        }
        throw new IllegalArgumentException("Unsupported key type " + keyType);
    }

    private static byte[][] getArgsForKeyType(int keyType, AlgorithmParameterSpec spec) {
        switch (keyType) {
            case SetEmptyView.TAG /*6*/:
                if (spec instanceof RSAKeyGenParameterSpec) {
                    return new byte[][]{((RSAKeyGenParameterSpec) spec).getPublicExponent().toByteArray()};
                }
                break;
            case KeyEvent.KEYCODE_SCROLL_LOCK /*116*/:
                if (spec instanceof DSAParameterSpec) {
                    DSAParameterSpec dsaSpec = (DSAParameterSpec) spec;
                    return new byte[][]{dsaSpec.getG().toByteArray(), dsaSpec.getP().toByteArray(), dsaSpec.getQ().toByteArray()};
                }
                break;
        }
        return (byte[][]) null;
    }

    public void initialize(int keysize, SecureRandom random) {
        throw new IllegalArgumentException("cannot specify keysize with AndroidKeyPairGenerator");
    }

    public void initialize(AlgorithmParameterSpec params, SecureRandom random) throws InvalidAlgorithmParameterException {
        if (params == null) {
            throw new InvalidAlgorithmParameterException("must supply params of type android.security.KeyPairGeneratorSpec");
        } else if (params instanceof KeyPairGeneratorSpec) {
            this.mSpec = (KeyPairGeneratorSpec) params;
            this.mKeyStore = KeyStore.getInstance();
        } else {
            throw new InvalidAlgorithmParameterException("params must be of type android.security.KeyPairGeneratorSpec");
        }
    }
}
