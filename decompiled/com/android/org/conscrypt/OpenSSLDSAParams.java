package com.android.org.conscrypt;

import java.math.BigInteger;
import java.security.interfaces.DSAParams;
import java.security.spec.AlgorithmParameterSpec;

public class OpenSSLDSAParams implements DSAParams, AlgorithmParameterSpec {
    private boolean fetchedParams;
    private BigInteger g;
    private OpenSSLKey key;
    private BigInteger p;
    private BigInteger q;
    private BigInteger x;
    private BigInteger y;

    OpenSSLDSAParams(OpenSSLKey key) {
        this.key = key;
    }

    OpenSSLKey getOpenSSLKey() {
        return this.key;
    }

    private final synchronized void ensureReadParams() {
        if (!this.fetchedParams) {
            byte[][] params = NativeCrypto.get_DSA_params(this.key.getPkeyContext());
            if (params[0] != null) {
                this.g = new BigInteger(params[0]);
            }
            if (params[1] != null) {
                this.p = new BigInteger(params[1]);
            }
            if (params[2] != null) {
                this.q = new BigInteger(params[2]);
            }
            if (params[3] != null) {
                this.y = new BigInteger(params[3]);
            }
            if (params[4] != null) {
                this.x = new BigInteger(params[4]);
            }
            this.fetchedParams = true;
        }
    }

    public BigInteger getG() {
        ensureReadParams();
        return this.g;
    }

    public BigInteger getP() {
        ensureReadParams();
        return this.p;
    }

    public BigInteger getQ() {
        ensureReadParams();
        return this.q;
    }

    boolean hasParams() {
        ensureReadParams();
        return (this.g == null || this.p == null || this.q == null) ? false : true;
    }

    BigInteger getY() {
        ensureReadParams();
        return this.y;
    }

    BigInteger getX() {
        ensureReadParams();
        return this.x;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof OpenSSLDSAParams) {
            if (this.key == ((OpenSSLDSAParams) o).getOpenSSLKey()) {
                return true;
            }
        }
        if (!(o instanceof DSAParams)) {
            return false;
        }
        ensureReadParams();
        DSAParams other = (DSAParams) o;
        if (this.g.equals(other.getG()) && this.p.equals(other.getP()) && this.q.equals(other.getQ())) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        ensureReadParams();
        return (this.g.hashCode() ^ this.p.hashCode()) ^ this.q.hashCode();
    }

    public String toString() {
        ensureReadParams();
        StringBuilder sb = new StringBuilder("OpenSSLDSAParams{");
        sb.append("G=");
        sb.append(this.g.toString(16));
        sb.append(",P=");
        sb.append(this.p.toString(16));
        sb.append(",Q=");
        sb.append(this.q.toString(16));
        sb.append('}');
        return sb.toString();
    }
}
