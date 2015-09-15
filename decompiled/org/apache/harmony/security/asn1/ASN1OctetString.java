package org.apache.harmony.security.asn1;

import java.io.IOException;
import java.util.Arrays;

public class ASN1OctetString extends ASN1StringType {
    private static final ASN1OctetString ASN1;

    static {
        ASN1 = new ASN1OctetString();
    }

    public ASN1OctetString() {
        super(4);
    }

    public static ASN1OctetString getInstance() {
        return ASN1;
    }

    public Object decode(BerInputStream in) throws IOException {
        in.readOctetString();
        if (in.isVerify) {
            return null;
        }
        return getDecodedObject(in);
    }

    public Object getDecodedObject(BerInputStream in) throws IOException {
        return Arrays.copyOfRange(in.buffer, in.contentOffset, in.contentOffset + in.length);
    }

    public void encodeContent(BerOutputStream out) {
        out.encodeOctetString();
    }

    public void setEncodingContent(BerOutputStream out) {
        out.length = ((byte[]) out.content).length;
    }
}
