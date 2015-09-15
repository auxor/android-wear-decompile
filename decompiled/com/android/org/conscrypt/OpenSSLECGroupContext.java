package com.android.org.conscrypt;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.spec.ECField;
import java.security.spec.ECFieldF2m;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;

public final class OpenSSLECGroupContext {
    private final long groupCtx;

    public OpenSSLECGroupContext(long groupCtx) {
        this.groupCtx = groupCtx;
    }

    public static OpenSSLECGroupContext getCurveByName(String curveName) {
        if ("secp256r1".equals(curveName)) {
            curveName = "prime256v1";
        } else if ("secp192r1".equals(curveName)) {
            curveName = "prime192v1";
        }
        long ctx = NativeCrypto.EC_GROUP_new_by_curve_name(curveName);
        if (ctx == 0) {
            return null;
        }
        NativeCrypto.EC_GROUP_set_point_conversion_form(ctx, 4);
        NativeCrypto.EC_GROUP_set_asn1_flag(ctx, 1);
        return new OpenSSLECGroupContext(ctx);
    }

    public static OpenSSLECGroupContext getInstance(int type, BigInteger p, BigInteger a, BigInteger b, BigInteger x, BigInteger y, BigInteger n, BigInteger h) {
        long ctx = NativeCrypto.EC_GROUP_new_curve(type, p.toByteArray(), a.toByteArray(), b.toByteArray());
        if (ctx == 0) {
            return null;
        }
        NativeCrypto.EC_GROUP_set_point_conversion_form(ctx, 4);
        OpenSSLECGroupContext group = new OpenSSLECGroupContext(ctx);
        OpenSSLECPointContext generator = new OpenSSLECPointContext(group, NativeCrypto.EC_POINT_new(ctx));
        NativeCrypto.EC_POINT_set_affine_coordinates(ctx, generator.getContext(), x.toByteArray(), y.toByteArray());
        NativeCrypto.EC_GROUP_set_generator(ctx, generator.getContext(), n.toByteArray(), h.toByteArray());
        return group;
    }

    protected void finalize() throws Throwable {
        try {
            if (this.groupCtx != 0) {
                NativeCrypto.EC_GROUP_clear_free(this.groupCtx);
            }
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
        }
    }

    public boolean equals(Object o) {
        if (!(o instanceof OpenSSLECGroupContext)) {
            return false;
        }
        return NativeCrypto.EC_GROUP_cmp(this.groupCtx, ((OpenSSLECGroupContext) o).groupCtx);
    }

    public int hashCode() {
        return super.hashCode();
    }

    public long getContext() {
        return this.groupCtx;
    }

    public static OpenSSLECGroupContext getInstance(ECParameterSpec params) throws InvalidAlgorithmParameterException {
        String curveName = Platform.getCurveName(params);
        if (curveName != null) {
            return getCurveByName(curveName);
        }
        int type;
        BigInteger p;
        EllipticCurve curve = params.getCurve();
        ECField field = curve.getField();
        if (field instanceof ECFieldFp) {
            type = 1;
            p = ((ECFieldFp) field).getP();
        } else if (field instanceof ECFieldF2m) {
            type = 2;
            p = ((ECFieldF2m) field).getReductionPolynomial();
        } else {
            throw new InvalidParameterException("unhandled field class " + field.getClass().getName());
        }
        ECPoint generator = params.getGenerator();
        return getInstance(type, p, curve.getA(), curve.getB(), generator.getAffineX(), generator.getAffineY(), params.getOrder(), BigInteger.valueOf((long) params.getCofactor()));
    }

    public ECParameterSpec getECParameterSpec() {
        ECField field;
        String curveName = NativeCrypto.EC_GROUP_get_curve_name(this.groupCtx);
        byte[][] curveParams = NativeCrypto.EC_GROUP_get_curve(this.groupCtx);
        BigInteger p = new BigInteger(curveParams[0]);
        BigInteger a = new BigInteger(curveParams[1]);
        BigInteger b = new BigInteger(curveParams[2]);
        int type = NativeCrypto.get_EC_GROUP_type(this.groupCtx);
        if (type == 1) {
            field = new ECFieldFp(p);
        } else if (type == 2) {
            field = new ECFieldF2m(p.bitLength() - 1, p);
        } else {
            throw new RuntimeException("unknown curve type " + type);
        }
        long EC_GROUP_get_generator = NativeCrypto.EC_GROUP_get_generator(this.groupCtx);
        byte[] EC_GROUP_get_order = NativeCrypto.EC_GROUP_get_order(this.groupCtx);
        ECParameterSpec spec = new ECParameterSpec(new EllipticCurve(field, a, b), new OpenSSLECPointContext(this, r18).getECPoint(), new BigInteger(r17), new BigInteger(NativeCrypto.EC_GROUP_get_cofactor(this.groupCtx)).intValue());
        Platform.setCurveName(spec, curveName);
        return spec;
    }
}
