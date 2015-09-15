package org.apache.harmony.security.asn1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class ASN1StringType extends ASN1Type {
    public static final ASN1StringType BMPSTRING;
    public static final ASN1StringType GENERALSTRING;
    public static final ASN1StringType IA5STRING;
    public static final ASN1StringType PRINTABLESTRING;
    public static final ASN1StringType TELETEXSTRING;
    public static final ASN1StringType UNIVERSALSTRING;
    public static final ASN1StringType UTF8STRING;

    /* renamed from: org.apache.harmony.security.asn1.ASN1StringType.1 */
    static class AnonymousClass1 extends ASN1StringType {
        AnonymousClass1(int x0) {
            super(x0);
        }
    }

    /* renamed from: org.apache.harmony.security.asn1.ASN1StringType.2 */
    static class AnonymousClass2 extends ASN1StringType {
        AnonymousClass2(int x0) {
            super(x0);
        }
    }

    /* renamed from: org.apache.harmony.security.asn1.ASN1StringType.3 */
    static class AnonymousClass3 extends ASN1StringType {
        AnonymousClass3(int x0) {
            super(x0);
        }
    }

    /* renamed from: org.apache.harmony.security.asn1.ASN1StringType.4 */
    static class AnonymousClass4 extends ASN1StringType {
        AnonymousClass4(int x0) {
            super(x0);
        }
    }

    private static class ASN1StringUTF8Type extends ASN1StringType {
        public ASN1StringUTF8Type(int tagNumber) {
            super(tagNumber);
        }

        public Object getDecodedObject(BerInputStream in) throws IOException {
            return new String(in.buffer, in.contentOffset, in.length, StandardCharsets.UTF_8);
        }

        public void setEncodingContent(BerOutputStream out) {
            byte[] bytes = ((String) out.content).getBytes(StandardCharsets.UTF_8);
            out.content = bytes;
            out.length = bytes.length;
        }
    }

    /* renamed from: org.apache.harmony.security.asn1.ASN1StringType.5 */
    static class AnonymousClass5 extends ASN1StringUTF8Type {
        AnonymousClass5(int x0) {
            super(x0);
        }
    }

    /* renamed from: org.apache.harmony.security.asn1.ASN1StringType.6 */
    static class AnonymousClass6 extends ASN1StringType {
        AnonymousClass6(int x0) {
            super(x0);
        }
    }

    /* renamed from: org.apache.harmony.security.asn1.ASN1StringType.7 */
    static class AnonymousClass7 extends ASN1StringUTF8Type {
        AnonymousClass7(int x0) {
            super(x0);
        }
    }

    static {
        BMPSTRING = new AnonymousClass1(30);
        IA5STRING = new AnonymousClass2(22);
        GENERALSTRING = new AnonymousClass3(27);
        PRINTABLESTRING = new AnonymousClass4(19);
        TELETEXSTRING = new AnonymousClass5(20);
        UNIVERSALSTRING = new AnonymousClass6(28);
        UTF8STRING = new AnonymousClass7(12);
    }

    public ASN1StringType(int tagNumber) {
        super(tagNumber);
    }

    public final boolean checkTag(int identifier) {
        return this.id == identifier || this.constrId == identifier;
    }

    public Object decode(BerInputStream in) throws IOException {
        in.readString(this);
        if (in.isVerify) {
            return null;
        }
        return getDecodedObject(in);
    }

    public Object getDecodedObject(BerInputStream in) throws IOException {
        return new String(in.buffer, in.contentOffset, in.length, StandardCharsets.ISO_8859_1);
    }

    public void encodeASN(BerOutputStream out) {
        out.encodeTag(this.id);
        encodeContent(out);
    }

    public void encodeContent(BerOutputStream out) {
        out.encodeString();
    }

    public void setEncodingContent(BerOutputStream out) {
        byte[] bytes = ((String) out.content).getBytes(StandardCharsets.UTF_8);
        out.content = bytes;
        out.length = bytes.length;
    }
}
