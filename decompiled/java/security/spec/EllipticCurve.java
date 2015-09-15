package java.security.spec;

import java.math.BigInteger;
import java.util.Arrays;

public class EllipticCurve {
    private final BigInteger a;
    private final BigInteger b;
    private final ECField field;
    private volatile int hash;
    private final byte[] seed;

    public EllipticCurve(ECField field, BigInteger a, BigInteger b, byte[] seed) {
        this.field = field;
        if (this.field == null) {
            throw new NullPointerException("field == null");
        }
        this.a = a;
        if (this.a == null) {
            throw new NullPointerException("a == null");
        }
        this.b = b;
        if (this.b == null) {
            throw new NullPointerException("b == null");
        }
        if (seed == null) {
            this.seed = null;
        } else {
            this.seed = new byte[seed.length];
            System.arraycopy(seed, 0, this.seed, 0, this.seed.length);
        }
        if (this.field instanceof ECFieldFp) {
            BigInteger p = ((ECFieldFp) this.field).getP();
            if (this.a.signum() < 0 || this.a.compareTo(p) >= 0) {
                throw new IllegalArgumentException("the a is not in the field");
            } else if (this.b.signum() < 0 || this.b.compareTo(p) >= 0) {
                throw new IllegalArgumentException("the b is not in the field");
            }
        } else if (this.field instanceof ECFieldF2m) {
            int fieldSizeInBits = this.field.getFieldSize();
            if (this.a.bitLength() > fieldSizeInBits) {
                throw new IllegalArgumentException("the a is not in the field");
            } else if (this.b.bitLength() > fieldSizeInBits) {
                throw new IllegalArgumentException("the b is not in the field");
            }
        }
    }

    public EllipticCurve(ECField field, BigInteger a, BigInteger b) {
        this(field, a, b, null);
    }

    public BigInteger getA() {
        return this.a;
    }

    public BigInteger getB() {
        return this.b;
    }

    public ECField getField() {
        return this.field;
    }

    public byte[] getSeed() {
        if (this.seed == null) {
            return null;
        }
        byte[] ret = new byte[this.seed.length];
        System.arraycopy(this.seed, 0, ret, 0, ret.length);
        return ret;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EllipticCurve)) {
            return false;
        }
        EllipticCurve otherEc = (EllipticCurve) other;
        if (this.field.equals(otherEc.field) && this.a.equals(otherEc.a) && this.b.equals(otherEc.b) && Arrays.equals(this.seed, otherEc.seed)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        if (this.hash == 0) {
            int hash0 = ((((this.field.hashCode() + 341) * 31) + this.a.hashCode()) * 31) + this.b.hashCode();
            if (this.seed != null) {
                for (byte b : this.seed) {
                    hash0 = (hash0 * 31) + b;
                }
            } else {
                hash0 *= 31;
            }
            this.hash = hash0;
        }
        return this.hash;
    }
}
