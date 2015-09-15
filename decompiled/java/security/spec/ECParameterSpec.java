package java.security.spec;

import java.math.BigInteger;

public class ECParameterSpec implements AlgorithmParameterSpec {
    private final int cofactor;
    private final EllipticCurve curve;
    private String curveName;
    private final ECPoint generator;
    private final BigInteger order;

    public ECParameterSpec(EllipticCurve curve, ECPoint generator, BigInteger order, int cofactor) {
        this.curve = curve;
        this.generator = generator;
        this.order = order;
        this.cofactor = cofactor;
        if (this.curve == null) {
            throw new NullPointerException("curve == null");
        } else if (this.generator == null) {
            throw new NullPointerException("generator == null");
        } else if (this.order == null) {
            throw new NullPointerException("order == null");
        } else if (this.order.compareTo(BigInteger.ZERO) <= 0) {
            throw new IllegalArgumentException("order <= 0");
        } else if (this.cofactor <= 0) {
            throw new IllegalArgumentException("cofactor <= 0");
        }
    }

    public int getCofactor() {
        return this.cofactor;
    }

    public EllipticCurve getCurve() {
        return this.curve;
    }

    public ECPoint getGenerator() {
        return this.generator;
    }

    public BigInteger getOrder() {
        return this.order;
    }

    public void setCurveName(String curveName) {
        this.curveName = curveName;
    }

    public String getCurveName() {
        return this.curveName;
    }
}
