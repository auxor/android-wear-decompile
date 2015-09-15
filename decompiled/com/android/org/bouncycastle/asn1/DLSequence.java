package com.android.org.bouncycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;

public class DLSequence extends ASN1Sequence {
    private int bodyLength;

    public DLSequence() {
        this.bodyLength = -1;
    }

    public DLSequence(ASN1Encodable obj) {
        super(obj);
        this.bodyLength = -1;
    }

    public DLSequence(ASN1EncodableVector v) {
        super(v);
        this.bodyLength = -1;
    }

    public DLSequence(ASN1Encodable[] array) {
        super(array);
        this.bodyLength = -1;
    }

    private int getBodyLength() throws IOException {
        if (this.bodyLength < 0) {
            int length = 0;
            Enumeration e = getObjects();
            while (e.hasMoreElements()) {
                length += ((ASN1Encodable) e.nextElement()).toASN1Primitive().toDLObject().encodedLength();
            }
            this.bodyLength = length;
        }
        return this.bodyLength;
    }

    int encodedLength() throws IOException {
        int length = getBodyLength();
        return (StreamUtil.calculateBodyLength(length) + 1) + length;
    }

    void encode(ASN1OutputStream out) throws IOException {
        ASN1OutputStream dOut = out.getDLSubStream();
        int length = getBodyLength();
        out.write(48);
        out.writeLength(length);
        Enumeration e = getObjects();
        while (e.hasMoreElements()) {
            dOut.writeObject((ASN1Encodable) e.nextElement());
        }
    }
}
