package org.apache.harmony.security.pkcs7;

import java.io.IOException;
import java.util.Arrays;
import javax.xml.datatype.DatatypeConstants;
import org.apache.harmony.security.asn1.ASN1Any;
import org.apache.harmony.security.asn1.ASN1Explicit;
import org.apache.harmony.security.asn1.ASN1OctetString;
import org.apache.harmony.security.asn1.ASN1Oid;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;

public final class ContentInfo {
    public static final ASN1Sequence ASN1;
    public static final int[] DATA;
    public static final int[] DIGESTED_DATA;
    public static final int[] ENCRYPTED_DATA;
    public static final int[] ENVELOPED_DATA;
    public static final int[] SIGNED_AND_ENVELOPED_DATA;
    public static final int[] SIGNED_DATA;
    private final Object content;
    private byte[] encoding;
    private final int[] oid;

    /* renamed from: org.apache.harmony.security.pkcs7.ContentInfo.1 */
    static class AnonymousClass1 extends ASN1Sequence {
        AnonymousClass1(ASN1Type[] x0) {
            super(x0);
            setOptional(1);
        }

        protected void getValues(Object object, Object[] values) {
            ContentInfo ci = (ContentInfo) object;
            values[0] = ci.oid;
            if (ci.content == null) {
                return;
            }
            if (Arrays.equals(ci.oid, ContentInfo.DATA)) {
                if (ci.content != null) {
                    values[1] = ASN1OctetString.getInstance().encode(ci.content);
                }
            } else if (ci.content instanceof SignedData) {
                values[1] = SignedData.ASN1.encode(ci.content);
            } else {
                values[1] = ci.content;
            }
        }

        protected Object getDecodedObject(BerInputStream in) throws IOException {
            Object[] values = (Object[]) in.content;
            int[] oid = (int[]) values[0];
            if (Arrays.equals(oid, ContentInfo.DATA)) {
                if (values[1] != null) {
                    return new ContentInfo(ASN1OctetString.getInstance().decode((byte[]) values[1]), in.getEncoded(), null);
                }
                return new ContentInfo(null, in.getEncoded(), null);
            } else if (Arrays.equals(oid, ContentInfo.SIGNED_DATA)) {
                return new ContentInfo(SignedData.ASN1.decode((byte[]) values[1]), in.getEncoded(), null);
            } else {
                return new ContentInfo(values[1], in.getEncoded(), null);
            }
        }
    }

    static {
        DATA = new int[]{1, 2, DatatypeConstants.MIN_TIMEZONE_OFFSET, 113549, 1, 7, 1};
        SIGNED_DATA = new int[]{1, 2, DatatypeConstants.MIN_TIMEZONE_OFFSET, 113549, 1, 7, 2};
        ENVELOPED_DATA = new int[]{1, 2, DatatypeConstants.MIN_TIMEZONE_OFFSET, 113549, 1, 7, 3};
        SIGNED_AND_ENVELOPED_DATA = new int[]{1, 2, DatatypeConstants.MIN_TIMEZONE_OFFSET, 113549, 1, 7, 4};
        DIGESTED_DATA = new int[]{1, 2, DatatypeConstants.MIN_TIMEZONE_OFFSET, 113549, 1, 7, 5};
        ENCRYPTED_DATA = new int[]{1, 2, DatatypeConstants.MIN_TIMEZONE_OFFSET, 113549, 1, 7, 6};
        ASN1 = new AnonymousClass1(new ASN1Type[]{ASN1Oid.getInstance(), new ASN1Explicit(0, ASN1Any.getInstance())});
    }

    private ContentInfo(int[] oid, Object content, byte[] encoding) {
        this.oid = oid;
        this.content = content;
        this.encoding = encoding;
    }

    public SignedData getSignedData() {
        if (Arrays.equals(this.oid, SIGNED_DATA)) {
            return (SignedData) this.content;
        }
        return null;
    }

    public Object getContent() {
        return this.content;
    }

    public int[] getContentType() {
        return this.oid;
    }

    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = ASN1.encode(this);
        }
        return this.encoding;
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("==== ContentInfo:");
        res.append("\n== ContentType (OID): ");
        for (int i : this.oid) {
            res.append(i);
            res.append(' ');
        }
        res.append("\n== Content: ");
        if (this.content != null) {
            res.append("\n");
            res.append(this.content.toString());
        }
        res.append("\n== Content End");
        res.append("\n==== ContentInfo End\n");
        return res.toString();
    }
}
