package com.android.org.conscrypt;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;

public class OpenSSLDSAPublicKey implements DSAPublicKey, OpenSSLKeyHolder {
    private static final long serialVersionUID = 5238609500353792232L;
    private transient OpenSSLKey key;
    private transient OpenSSLDSAParams params;

    OpenSSLDSAPublicKey(OpenSSLKey key) {
        this.key = key;
    }

    public OpenSSLKey getOpenSSLKey() {
        return this.key;
    }

    OpenSSLDSAPublicKey(DSAPublicKeySpec dsaKeySpec) throws InvalidKeySpecException {
        try {
            this.key = new OpenSSLKey(NativeCrypto.EVP_PKEY_new_DSA(dsaKeySpec.getP().toByteArray(), dsaKeySpec.getQ().toByteArray(), dsaKeySpec.getG().toByteArray(), dsaKeySpec.getY().toByteArray(), null));
        } catch (Exception e) {
            throw new InvalidKeySpecException(e);
        }
    }

    private void ensureReadParams() {
        if (this.params == null) {
            this.params = new OpenSSLDSAParams(this.key);
        }
    }

    static OpenSSLKey getInstance(DSAPublicKey dsaPublicKey) throws InvalidKeyException {
        try {
            DSAParams dsaParams = dsaPublicKey.getParams();
            return new OpenSSLKey(NativeCrypto.EVP_PKEY_new_DSA(dsaParams.getP().toByteArray(), dsaParams.getQ().toByteArray(), dsaParams.getG().toByteArray(), dsaPublicKey.getY().toByteArray(), null));
        } catch (Exception e) {
            throw new InvalidKeyException(e);
        }
    }

    public DSAParams getParams() {
        ensureReadParams();
        if (this.params.hasParams()) {
            return this.params;
        }
        return null;
    }

    public String getAlgorithm() {
        return "DSA";
    }

    public String getFormat() {
        return "X.509";
    }

    public byte[] getEncoded() {
        return NativeCrypto.i2d_PUBKEY(this.key.getPkeyContext());
    }

    public BigInteger getY() {
        ensureReadParams();
        return this.params.getY();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof OpenSSLDSAPublicKey) {
            if (this.key.equals(((OpenSSLDSAPublicKey) o).getOpenSSLKey())) {
                return true;
            }
        }
        if (!(o instanceof DSAPublicKey)) {
            return false;
        }
        ensureReadParams();
        DSAPublicKey other = (DSAPublicKey) o;
        if (this.params.getY().equals(other.getY()) && this.params.equals(other.getParams())) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        ensureReadParams();
        return this.params.getY().hashCode() ^ this.params.hashCode();
    }

    public String toString() {
        ensureReadParams();
        StringBuilder sb = new StringBuilder("OpenSSLDSAPublicKey{");
        sb.append("Y=");
        sb.append(this.params.getY().toString(16));
        sb.append(',');
        sb.append("params=");
        sb.append(this.params.toString());
        sb.append('}');
        return sb.toString();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        BigInteger p = (BigInteger) stream.readObject();
        BigInteger q = (BigInteger) stream.readObject();
        this.key = new OpenSSLKey(NativeCrypto.EVP_PKEY_new_DSA(p.toByteArray(), q.toByteArray(), ((BigInteger) stream.readObject()).toByteArray(), ((BigInteger) stream.readObject()).toByteArray(), null));
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        if (getOpenSSLKey().isEngineBased()) {
            throw new NotSerializableException("engine-based keys can not be serialized");
        }
        stream.defaultWriteObject();
        ensureReadParams();
        stream.writeObject(this.params.getG());
        stream.writeObject(this.params.getP());
        stream.writeObject(this.params.getQ());
        stream.writeObject(this.params.getY());
    }
}
