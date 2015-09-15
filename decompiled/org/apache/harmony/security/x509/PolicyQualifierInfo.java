package org.apache.harmony.security.x509;

import org.apache.harmony.security.asn1.ASN1Any;
import org.apache.harmony.security.asn1.ASN1Oid;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1Type;

public final class PolicyQualifierInfo {
    public static final ASN1Sequence ASN1;

    /* renamed from: org.apache.harmony.security.x509.PolicyQualifierInfo.1 */
    static class AnonymousClass1 extends ASN1Sequence {
        AnonymousClass1(ASN1Type[] x0) {
            super(x0);
        }
    }

    static {
        ASN1 = new AnonymousClass1(new ASN1Type[]{ASN1Oid.getInstance(), ASN1Any.getInstance()});
    }
}
