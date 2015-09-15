package org.apache.harmony.security.asn1;

import java.io.IOException;

public class ASN1SequenceOf extends ASN1ValueCollection {
    public ASN1SequenceOf(ASN1Type type) {
        super(16, type);
    }

    public Object decode(BerInputStream in) throws IOException {
        in.readSequenceOf(this);
        if (in.isVerify) {
            return null;
        }
        return getDecodedObject(in);
    }

    public final void encodeContent(BerOutputStream out) {
        out.encodeSequenceOf(this);
    }

    public final void setEncodingContent(BerOutputStream out) {
        out.getSequenceOfLength(this);
    }
}
