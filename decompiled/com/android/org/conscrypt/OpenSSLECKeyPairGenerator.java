package com.android.org.conscrypt;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.util.HashMap;
import java.util.Map;

public final class OpenSSLECKeyPairGenerator extends KeyPairGenerator {
    private static final String ALGORITHM = "EC";
    private static final int DEFAULT_KEY_SIZE = 192;
    private static final Map<Integer, String> SIZE_TO_CURVE_NAME;
    private OpenSSLECGroupContext group;

    static {
        SIZE_TO_CURVE_NAME = new HashMap();
        SIZE_TO_CURVE_NAME.put(Integer.valueOf(DEFAULT_KEY_SIZE), "prime192v1");
        SIZE_TO_CURVE_NAME.put(Integer.valueOf(224), "secp224r1");
        SIZE_TO_CURVE_NAME.put(Integer.valueOf(PSKKeyManager.MAX_KEY_LENGTH_BYTES), "prime256v1");
        SIZE_TO_CURVE_NAME.put(Integer.valueOf(384), "secp384r1");
        SIZE_TO_CURVE_NAME.put(Integer.valueOf(521), "secp521r1");
    }

    public OpenSSLECKeyPairGenerator() {
        super(ALGORITHM);
    }

    public KeyPair generateKeyPair() {
        if (this.group == null) {
            this.group = OpenSSLECGroupContext.getCurveByName((String) SIZE_TO_CURVE_NAME.get(Integer.valueOf(DEFAULT_KEY_SIZE)));
        }
        OpenSSLKey key = new OpenSSLKey(NativeCrypto.EC_KEY_generate_key(this.group.getContext()));
        return new KeyPair(new OpenSSLECPublicKey(this.group, key), new OpenSSLECPrivateKey(this.group, key));
    }

    public void initialize(int keysize, SecureRandom random) {
        String name = (String) SIZE_TO_CURVE_NAME.get(Integer.valueOf(keysize));
        if (name == null) {
            throw new InvalidParameterException("unknown key size " + keysize);
        }
        OpenSSLECGroupContext possibleGroup = OpenSSLECGroupContext.getCurveByName(name);
        if (possibleGroup == null) {
            throw new InvalidParameterException("unknown curve " + name);
        }
        this.group = possibleGroup;
    }

    public void initialize(AlgorithmParameterSpec param, SecureRandom random) throws InvalidAlgorithmParameterException {
        if (param instanceof ECParameterSpec) {
            this.group = OpenSSLECGroupContext.getInstance((ECParameterSpec) param);
        } else if (param instanceof ECGenParameterSpec) {
            String curveName = ((ECGenParameterSpec) param).getName();
            OpenSSLECGroupContext possibleGroup = OpenSSLECGroupContext.getCurveByName(curveName);
            if (possibleGroup == null) {
                throw new InvalidAlgorithmParameterException("unknown curve name: " + curveName);
            }
            this.group = possibleGroup;
        } else {
            throw new InvalidAlgorithmParameterException("parameter must be ECParameterSpec or ECGenParameterSpec");
        }
    }
}
