package java.security.spec;

import java.math.BigInteger;

public class ECPoint {
    public static final ECPoint POINT_INFINITY;
    private final BigInteger affineX;
    private final BigInteger affineY;

    static {
        POINT_INFINITY = new ECPoint();
    }

    private ECPoint() {
        this.affineX = null;
        this.affineY = null;
    }

    public ECPoint(BigInteger affineX, BigInteger affineY) {
        this.affineX = affineX;
        if (this.affineX == null) {
            throw new NullPointerException("affineX == null");
        }
        this.affineY = affineY;
        if (this.affineY == null) {
            throw new NullPointerException("affineY == null");
        }
    }

    public BigInteger getAffineX() {
        return this.affineX;
    }

    public BigInteger getAffineY() {
        return this.affineY;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ECPoint)) {
            return false;
        }
        if (this.affineX != null) {
            ECPoint otherPoint = (ECPoint) other;
            if (this.affineX.equals(otherPoint.affineX) && this.affineY.equals(otherPoint.affineY)) {
                return true;
            }
            return false;
        } else if (other != POINT_INFINITY) {
            return false;
        } else {
            return true;
        }
    }

    public int hashCode() {
        if (this.affineX != null) {
            return (this.affineX.hashCode() * 31) + this.affineY.hashCode();
        }
        return 11;
    }
}
