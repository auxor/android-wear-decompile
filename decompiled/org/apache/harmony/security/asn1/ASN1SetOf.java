package org.apache.harmony.security.asn1;

import java.io.IOException;

public class ASN1SetOf extends ASN1ValueCollection {
    public ASN1SetOf(ASN1Type type) {
        super(17, type);
    }

    public Object decode(BerInputStream in) throws IOException {
        in.readSetOf(this);
        if (in.isVerify) {
            return null;
        }
        return getDecodedObject(in);
    }

    public final void encodeContent(BerOutputStream out) {
        out.encodeSetOf(this);
    }

    public final void setEncodingContent(BerOutputStream out) {
        out.getSetOfLength(this);
    }
}
