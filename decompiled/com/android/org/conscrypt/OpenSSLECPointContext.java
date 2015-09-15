package com.android.org.conscrypt;

import java.math.BigInteger;
import java.security.spec.ECPoint;

final class OpenSSLECPointContext {
    private final OpenSSLECGroupContext group;
    private final long pointCtx;

    OpenSSLECPointContext(OpenSSLECGroupContext group, long pointCtx) {
        this.group = group;
        this.pointCtx = pointCtx;
    }

    protected void finalize() throws Throwable {
        try {
            if (this.pointCtx != 0) {
                NativeCrypto.EC_POINT_clear_free(this.pointCtx);
            }
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
        }
    }

    public boolean equals(Object o) {
        if (!(o instanceof OpenSSLECPointContext)) {
            return false;
        }
        OpenSSLECPointContext other = (OpenSSLECPointContext) o;
        if (NativeCrypto.EC_GROUP_cmp(this.group.getContext(), other.group.getContext())) {
            return NativeCrypto.EC_POINT_cmp(this.group.getContext(), this.pointCtx, other.pointCtx);
        }
        return false;
    }

    public ECPoint getECPoint() {
        byte[][] generatorCoords = NativeCrypto.EC_POINT_get_affine_coordinates(this.group.getContext(), this.pointCtx);
        return new ECPoint(new BigInteger(generatorCoords[0]), new BigInteger(generatorCoords[1]));
    }

    public int hashCode() {
        return super.hashCode();
    }

    public long getContext() {
        return this.pointCtx;
    }

    public static OpenSSLECPointContext getInstance(int curveType, OpenSSLECGroupContext group, ECPoint javaPoint) {
        OpenSSLECPointContext point = new OpenSSLECPointContext(group, NativeCrypto.EC_POINT_new(group.getContext()));
        NativeCrypto.EC_POINT_set_affine_coordinates(group.getContext(), point.getContext(), javaPoint.getAffineX().toByteArray(), javaPoint.getAffineY().toByteArray());
        return point;
    }
}
