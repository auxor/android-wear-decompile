package com.android.org.conscrypt;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

public final class OpenSSLECPublicKey implements ECPublicKey, OpenSSLKeyHolder {
    private static final String ALGORITHM = "EC";
    private static final long serialVersionUID = 3215842926808298020L;
    protected transient OpenSSLECGroupContext group;
    protected transient OpenSSLKey key;

    public OpenSSLECPublicKey(OpenSSLECGroupContext group, OpenSSLKey key) {
        this.group = group;
        this.key = key;
    }

    public OpenSSLECPublicKey(OpenSSLKey key) {
        this.group = new OpenSSLECGroupContext(NativeCrypto.EC_GROUP_dup(NativeCrypto.EC_KEY_get0_group(key.getPkeyContext())));
        this.key = key;
    }

    public OpenSSLECPublicKey(ECPublicKeySpec ecKeySpec) throws InvalidKeySpecException {
        try {
            this.group = OpenSSLECGroupContext.getInstance(ecKeySpec.getParams());
            this.key = new OpenSSLKey(NativeCrypto.EVP_PKEY_new_EC_KEY(this.group.getContext(), OpenSSLECPointContext.getInstance(NativeCrypto.get_EC_GROUP_type(this.group.getContext()), this.group, ecKeySpec.getW()).getContext(), null));
        } catch (Exception e) {
            throw new InvalidKeySpecException(e);
        }
    }

    public static OpenSSLKey getInstance(ECPublicKey ecPublicKey) throws InvalidKeyException {
        try {
            OpenSSLECGroupContext group = OpenSSLECGroupContext.getInstance(ecPublicKey.getParams());
            return new OpenSSLKey(NativeCrypto.EVP_PKEY_new_EC_KEY(group.getContext(), OpenSSLECPointContext.getInstance(NativeCrypto.get_EC_GROUP_type(group.getContext()), group, ecPublicKey.getW()).getContext(), null));
        } catch (Exception e) {
            throw new InvalidKeyException(e);
        }
    }

    public String getAlgorithm() {
        return ALGORITHM;
    }

    public String getFormat() {
        return "X.509";
    }

    public byte[] getEncoded() {
        return NativeCrypto.i2d_PUBKEY(this.key.getPkeyContext());
    }

    public ECParameterSpec getParams() {
        return this.group.getECParameterSpec();
    }

    private ECPoint getPublicKey() {
        return new OpenSSLECPointContext(this.group, NativeCrypto.EC_KEY_get_public_key(this.key.getPkeyContext())).getECPoint();
    }

    public ECPoint getW() {
        return getPublicKey();
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
        } else if (!(o instanceof ECPublicKey)) {
            return false;
        } else {
            ECPublicKey other = (ECPublicKey) o;
            if (!getPublicKey().equals(other.getW())) {
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
        return Arrays.hashCode(NativeCrypto.i2d_PUBKEY(this.key.getPkeyContext()));
    }

    public String toString() {
        return NativeCrypto.EVP_PKEY_print_public(this.key.getPkeyContext());
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.key = new OpenSSLKey(NativeCrypto.d2i_PUBKEY((byte[]) stream.readObject()));
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
