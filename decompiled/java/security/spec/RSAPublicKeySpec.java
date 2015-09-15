package java.security.spec;

import java.math.BigInteger;

public class RSAPublicKeySpec implements KeySpec {
    private final BigInteger modulus;
    private final BigInteger publicExponent;

    public RSAPublicKeySpec(BigInteger modulus, BigInteger publicExponent) {
        this.modulus = modulus;
        this.publicExponent = publicExponent;
    }

    public BigInteger getModulus() {
        return this.modulus;
    }

    public BigInteger getPublicExponent() {
        return this.publicExponent;
    }
}
