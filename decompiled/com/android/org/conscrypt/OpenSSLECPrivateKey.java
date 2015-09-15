package com.android.org.conscrypt;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

public final class OpenSSLECPrivateKey implements ECPrivateKey, OpenSSLKeyHolder {
    private static final String ALGORITHM = "EC";
    private static final long serialVersionUID = -4036633595001083922L;
    protected transient OpenSSLECGroupContext group;
    protected transient OpenSSLKey key;

    public OpenSSLECPrivateKey(OpenSSLECGroupContext group, OpenSSLKey key) {
        this.group = group;
        this.key = key;
    }

    public OpenSSLECPrivateKey(OpenSSLKey key) {
        this.group = new OpenSSLECGroupContext(NativeCrypto.EC_GROUP_dup(NativeCrypto.EC_KEY_get0_group(key.getPkeyContext())));
        this.key = key;
    }

    public OpenSSLECPrivateKey(ECPrivateKeySpec ecKeySpec) throws InvalidKeySpecException {
        try {
            this.group = OpenSSLECGroupContext.getInstance(ecKeySpec.getParams());
            this.key = new OpenSSLKey(NativeCrypto.EVP_PKEY_new_EC_KEY(this.group.getContext(), 0, ecKeySpec.getS().toByteArray()));
        } catch (Exception e) {
            throw new InvalidKeySpecException(e);
        }
    }

    public static OpenSSLKey wrapPlatformKey(ECPrivateKey ecPrivateKey) throws InvalidKeyException {
        try {
            return wrapPlatformKey(ecPrivateKey, OpenSSLECGroupContext.getInstance(ecPrivateKey.getParams()));
        } catch (InvalidAlgorithmParameterException e) {
            throw new InvalidKeyException("Unknown group parameters", e);
        }
    }

    private static OpenSSLKey wrapPlatformKey(ECPrivateKey ecPrivateKey, OpenSSLECGroupContext group) throws InvalidKeyException {
        return new OpenSSLKey(NativeCrypto.getECPrivateKeyWrapper(ecPrivateKey, group.getContext()), true);
    }

    public static OpenSSLKey getInstance(ECPrivateKey ecPrivateKey) throws InvalidKeyException {
        try {
            OpenSSLECGroupContext group = OpenSSLECGroupContext.getInstance(ecPrivateKey.getParams());
            if (ecPrivateKey.getFormat() == null) {
                return wrapPlatformKey(ecPrivateKey, group);
            }
            return new OpenSSLKey(NativeCrypto.EVP_PKEY_new_EC_KEY(group.getContext(), 0, ecPrivateKey.getS().toByteArray()));
        } catch (Exception e) {
            throw new InvalidKeyException(e);
        }
    }

    public String getAlgorithm() {
        return ALGORITHM;
    }

    public String getFormat() {
        if (this.key.isEngineBased()) {
            return null;
        }
        return "PKCS#8";
    }

    public byte[] getEncoded() {
        if (this.key.isEngineBased()) {
            return null;
        }
        return NativeCrypto.i2d_PKCS8_PRIV_KEY_INFO(this.key.getPkeyContext());
    }

    public ECParameterSpec getParams() {
        return this.group.getECParameterSpec();
    }

    public BigInteger getS() {
        if (!this.key.isEngineBased()) {
            return getPrivateKey();
        }
        throw new UnsupportedOperationException("private key value S cannot be extracted");
    }

    private BigInteger getPrivateKey() {
        return new BigInteger(NativeCrypto.EC_KEY_get_private_key(this.key.getPkeyContext()));
    }

    public OpenSSLKey getOpenSSLKey() {
        return this.key;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof OpenSSLECPrivateKey) {
            return this.key.equals(((OpenSSLECPrivateKey) o).key);
        } else if (!(o instanceof ECPrivateKey)) {
            return false;
        } else {
            ECPrivateKey other = (ECPrivateKey) o;
            if (!getPrivateKey().equals(other.getS())) {
                return false;
            }
            ECParameterSpec spec = getParams();
            ECParameterSpec otherSpec = other.getParams();
            if (spec.getCurve().equals(otherSpec.getCurve()) && spec.getGenerator().equals(otherSpec.getGenerator()) && spec.getOrder().equals(otherSpec.getOrder()) && spec.getCofactor() == otherSpec.getCofactor()) {
                return true;
            }
            return false;
        }
    }

    public int hashCode() {
        return Arrays.hashCode(NativeCrypto.i2d_PKCS8_PRIV_KEY_INFO(this.key.getPkeyContext()));
    }

    public String toString() {
        return NativeCrypto.EVP_PKEY_print_private(this.key.getPkeyContext());
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.key = new OpenSSLKey(NativeCrypto.d2i_PKCS8_PRIV_KEY_INFO((byte[]) stream.readObject()));
        this.group = new OpenSSLECGroupContext(NativeCrypto.EC_GROUP_dup(NativeCrypto.EC_KEY_get0_group(this.key.getPkeyContext())));
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        if (this.key.isEngineBased()) {
            throw new NotSerializableException("engine-based keys can not be serialized");
        }
        stream.defaultWriteObject();
        stream.writeObject(getEncoded());
    }
}
