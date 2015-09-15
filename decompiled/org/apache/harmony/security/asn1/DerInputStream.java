package org.apache.harmony.security.asn1;

import java.io.IOException;
import java.io.InputStream;

public final class DerInputStream extends BerInputStream {
    private static final byte[] UNUSED_BITS_MASK;

    static {
        UNUSED_BITS_MASK = new byte[]{(byte) 1, (byte) 3, (byte) 7, Character.DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE, (byte) 31, (byte) 63, Byte.MAX_VALUE};
    }

    public DerInputStream(byte[] encoded) throws IOException {
        super(encoded, 0, encoded.length);
    }

    public DerInputStream(byte[] encoded, int offset, int encodingLen) throws IOException {
        super(encoded, offset, encodingLen);
    }

    public DerInputStream(InputStream in) throws IOException {
        super(in);
    }

    public int next() throws IOException {
        int tag = super.next();
        if (this.length != -1) {
            return tag;
        }
        throw new ASN1Exception("DER: only definite length encoding MUST be used");
    }

    public void readBitString() throws IOException {
        if (this.tag == 35) {
            throw new ASN1Exception("ASN.1 bitstring: constructed identifier at [" + this.tagOffset + "]. Not valid for DER.");
        }
        super.readBitString();
        if (this.length > 1 && this.buffer[this.contentOffset] != null && (this.buffer[this.offset - 1] & UNUSED_BITS_MASK[this.buffer[this.contentOffset] - 1]) != 0) {
            throw new ASN1Exception("ASN.1 bitstring: wrong content at [" + this.contentOffset + "]. DER requires zero unused bits in final octet.");
        }
    }

    public void readBoolean() throws IOException {
        super.readBoolean();
        if (this.buffer[this.contentOffset] != null && this.buffer[this.contentOffset] != -1) {
            throw new ASN1Exception("ASN.1 boolean: wrong content at [" + this.contentOffset + "]. DER allows only 0x00 or 0xFF values");
        }
    }

    public void readOctetString() throws IOException {
        if (this.tag == 36) {
            throw new ASN1Exception("ASN.1 octetstring: constructed identifier at [" + this.tagOffset + "]. Not valid for DER.");
        }
        super.readOctetString();
    }

    public void readSequence(ASN1Sequence sequence) throws IOException {
        super.readSequence(sequence);
    }

    public void readSetOf(ASN1SetOf setOf) throws IOException {
        super.readSetOf(setOf);
    }

    public void readString(ASN1StringType type) throws IOException {
        if (this.tag == type.constrId) {
            throw new ASN1Exception("ASN.1 string: constructed identifier at [" + this.tagOffset + "]. Not valid for DER.");
        }
        super.readString(type);
    }

    public void readUTCTime() throws IOException {
        if (this.tag == 55) {
            throw new ASN1Exception("ASN.1 UTCTime: constructed identifier at [" + this.tagOffset + "]. Not valid for DER.");
        } else if (this.length != 13) {
            throw new ASN1Exception("ASN.1 UTCTime: wrong format for DER, identifier at [" + this.tagOffset + "]");
        } else {
            super.readUTCTime();
        }
    }

    public void readGeneralizedTime() throws IOException {
        if (this.tag == 56) {
            throw new ASN1Exception("ASN.1 GeneralizedTime: constructed identifier at [" + this.tagOffset + "]. Not valid for DER.");
        }
        super.readGeneralizedTime();
    }
}
