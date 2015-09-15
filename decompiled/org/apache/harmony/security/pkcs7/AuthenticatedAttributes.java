package org.apache.harmony.security.pkcs7;

import java.util.List;
import org.apache.harmony.security.asn1.ASN1SetOf;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.x501.AttributeTypeAndValue;

final class AuthenticatedAttributes {
    public static final ASN1SetOf ASN1;
    private final List<AttributeTypeAndValue> authenticatedAttributes;
    private byte[] encoding;

    /* renamed from: org.apache.harmony.security.pkcs7.AuthenticatedAttributes.1 */
    static class AnonymousClass1 extends ASN1SetOf {
        AnonymousClass1(ASN1Type x0) {
            super(x0);
        }

        public Object getDecodedObject(BerInputStream in) {
            return new AuthenticatedAttributes((List) in.content, null);
        }
    }

    private AuthenticatedAttributes(byte[] encoding, List<AttributeTypeAndValue> authenticatedAttributes) {
        this.encoding = encoding;
        this.authenticatedAttributes = authenticatedAttributes;
    }

    public List<AttributeTypeAndValue> getAttributes() {
        return this.authenticatedAttributes;
    }

    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = ASN1.encode(this);
        }
        return this.encoding;
    }

    static {
        ASN1 = new AnonymousClass1(AttributeTypeAndValue.ASN1);
    }
}
