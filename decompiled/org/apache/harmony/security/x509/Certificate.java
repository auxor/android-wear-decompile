package org.apache.harmony.security.x509;

import org.apache.harmony.security.asn1.ASN1BitString;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.asn1.BitString;
import org.apache.harmony.security.utils.Array;
import org.xmlpull.v1.XmlPullParser;

public final class Certificate {
    public static final ASN1Sequence ASN1;
    private byte[] encoding;
    private final AlgorithmIdentifier signatureAlgorithm;
    private final byte[] signatureValue;
    private final TBSCertificate tbsCertificate;

    /* renamed from: org.apache.harmony.security.x509.Certificate.1 */
    static class AnonymousClass1 extends ASN1Sequence {
        AnonymousClass1(ASN1Type[] x0) {
            super(x0);
        }

        protected Object getDecodedObject(BerInputStream in) {
            Object[] values = (Object[]) in.content;
            return new Certificate((AlgorithmIdentifier) values[1], ((BitString) values[2]).bytes, in.getEncoded(), null);
        }

        protected void getValues(Object object, Object[] values) {
            Certificate cert = (Certificate) object;
            values[0] = cert.tbsCertificate;
            values[1] = cert.signatureAlgorithm;
            values[2] = new BitString(cert.signatureValue, 0);
        }
    }

    public Certificate(TBSCertificate tbsCertificate, AlgorithmIdentifier signatureAlgorithm, byte[] signatureValue) {
        this.tbsCertificate = tbsCertificate;
        this.signatureAlgorithm = signatureAlgorithm;
        this.signatureValue = new byte[signatureValue.length];
        System.arraycopy(signatureValue, 0, this.signatureValue, 0, signatureValue.length);
    }

    private Certificate(TBSCertificate tbsCertificate, AlgorithmIdentifier signatureAlgorithm, byte[] signatureValue, byte[] encoding) {
        this(tbsCertificate, signatureAlgorithm, signatureValue);
        this.encoding = encoding;
    }

    public TBSCertificate getTbsCertificate() {
        return this.tbsCertificate;
    }

    public byte[] getSignatureValue() {
        return (byte[]) this.signatureValue.clone();
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("X.509 Certificate:\n[\n");
        this.tbsCertificate.dumpValue(result);
        result.append("\n  Algorithm: [");
        this.signatureAlgorithm.dumpValue(result);
        result.append(']');
        result.append("\n  Signature Value:\n");
        result.append(Array.toString(this.signatureValue, XmlPullParser.NO_NAMESPACE));
        result.append(']');
        return result.toString();
    }

    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = ASN1.encode(this);
        }
        return this.encoding;
    }

    static {
        ASN1 = new AnonymousClass1(new ASN1Type[]{TBSCertificate.ASN1, AlgorithmIdentifier.ASN1, ASN1BitString.getInstance()});
    }
}
