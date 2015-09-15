package org.apache.harmony.security.asn1;

import java.io.IOException;
import org.w3c.dom.traversal.NodeFilter;

public final class ASN1Implicit extends ASN1Type {
    private static final int TAGGING_CONSTRUCTED = 1;
    private static final int TAGGING_PRIMITIVE = 0;
    private static final int TAGGING_STRING = 2;
    private final int taggingType;
    private final ASN1Type type;

    public ASN1Implicit(int tagNumber, ASN1Type type) {
        super(NodeFilter.SHOW_COMMENT, tagNumber);
        if ((type instanceof ASN1Choice) || (type instanceof ASN1Any)) {
            throw new IllegalArgumentException("Implicit tagging can not be used for ASN.1 ANY or CHOICE type");
        }
        this.type = type;
        if (!type.checkTag(type.id)) {
            this.taggingType = TAGGING_CONSTRUCTED;
        } else if (type.checkTag(type.constrId)) {
            this.taggingType = TAGGING_STRING;
        } else {
            this.taggingType = TAGGING_PRIMITIVE;
        }
    }

    public final boolean checkTag(int identifier) {
        boolean z = false;
        switch (this.taggingType) {
            case TAGGING_PRIMITIVE /*0*/:
                if (this.id != identifier) {
                    return false;
                }
                return true;
            case TAGGING_CONSTRUCTED /*1*/:
                if (this.constrId != identifier) {
                    return false;
                }
                return true;
            default:
                if (this.id == identifier || this.constrId == identifier) {
                    z = true;
                }
                return z;
        }
    }

    public Object decode(BerInputStream in) throws IOException {
        if (checkTag(in.tag)) {
            if (this.id == in.tag) {
                in.tag = this.type.id;
            } else {
                in.tag = this.type.constrId;
            }
            in.content = this.type.decode(in);
            if (in.isVerify) {
                return null;
            }
            return getDecodedObject(in);
        }
        throw new ASN1Exception("ASN.1 implicitly tagged type expected at [" + in.tagOffset + "]. Expected tag: " + Integer.toHexString(this.id) + ", " + "but got " + Integer.toHexString(in.tag));
    }

    public void encodeASN(BerOutputStream out) {
        if (this.taggingType == TAGGING_CONSTRUCTED) {
            out.encodeTag(this.constrId);
        } else {
            out.encodeTag(this.id);
        }
        encodeContent(out);
    }

    public void encodeContent(BerOutputStream out) {
        this.type.encodeContent(out);
    }

    public void setEncodingContent(BerOutputStream out) {
        this.type.setEncodingContent(out);
    }
}
