package org.apache.harmony.security.x509;

import java.util.Date;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;

public final class Validity {
    public static final ASN1Sequence ASN1;
    private byte[] encoding;
    private final Date notAfter;
    private final Date notBefore;

    /* renamed from: org.apache.harmony.security.x509.Validity.1 */
    static class AnonymousClass1 extends ASN1Sequence {
        AnonymousClass1(ASN1Type[] x0) {
            super(x0);
        }

        protected Object getDecodedObject(BerInputStream in) {
            Object[] values = (Object[]) in.content;
            return new Validity((Date) values[0], (Date) values[1]);
        }

        protected void getValues(Object object, Object[] values) {
            Validity validity = (Validity) object;
            values[0] = validity.notBefore;
            values[1] = validity.notAfter;
        }
    }

    public Validity(Date notBefore, Date notAfter) {
        this.notBefore = notBefore;
        this.notAfter = notAfter;
    }

    public Date getNotBefore() {
        return this.notBefore;
    }

    public Date getNotAfter() {
        return this.notAfter;
    }

    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = ASN1.encode(this);
        }
        return this.encoding;
    }

    static {
        ASN1 = new AnonymousClass1(new ASN1Type[]{Time.ASN1, Time.ASN1});
    }
}
