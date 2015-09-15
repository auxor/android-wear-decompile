package org.apache.harmony.security.x509;

import java.math.BigInteger;
import javax.security.auth.x500.X500Principal;
import org.apache.harmony.security.asn1.ASN1BitString;
import org.apache.harmony.security.asn1.ASN1Explicit;
import org.apache.harmony.security.asn1.ASN1Implicit;
import org.apache.harmony.security.asn1.ASN1Integer;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.asn1.BitString;
import org.apache.harmony.security.x501.Name;

public final class TBSCertificate {
    public static final ASN1Sequence ASN1;
    private byte[] encoding;
    private final Extensions extensions;
    private final Name issuer;
    private final boolean[] issuerUniqueID;
    private final BigInteger serialNumber;
    private final AlgorithmIdentifier signature;
    private final Name subject;
    private final SubjectPublicKeyInfo subjectPublicKeyInfo;
    private final boolean[] subjectUniqueID;
    private final Validity validity;
    private final int version;

    /* renamed from: org.apache.harmony.security.x509.TBSCertificate.1 */
    static class AnonymousClass1 extends ASN1Sequence {
        AnonymousClass1(ASN1Type[] x0) {
            super(x0);
            setDefault(new byte[]{null}, 0);
            setOptional(7);
            setOptional(8);
            setOptional(9);
        }

        protected Object getDecodedObject(BerInputStream in) {
            Object[] values = (Object[]) in.content;
            return new TBSCertificate(new BigInteger((byte[]) values[1]), (AlgorithmIdentifier) values[2], (Name) values[3], (Validity) values[4], (Name) values[5], (SubjectPublicKeyInfo) values[6], values[7] == null ? null : ((BitString) values[7]).toBooleanArray(), values[8] == null ? null : ((BitString) values[8]).toBooleanArray(), (Extensions) values[9], in.getEncoded(), null);
        }

        protected void getValues(Object object, Object[] values) {
            TBSCertificate tbs = (TBSCertificate) object;
            values[0] = ASN1Integer.fromIntValue(tbs.version);
            values[1] = tbs.serialNumber.toByteArray();
            values[2] = tbs.signature;
            values[3] = tbs.issuer;
            values[4] = tbs.validity;
            values[5] = tbs.subject;
            values[6] = tbs.subjectPublicKeyInfo;
            if (tbs.issuerUniqueID != null) {
                values[7] = new BitString(tbs.issuerUniqueID);
            }
            if (tbs.subjectUniqueID != null) {
                values[8] = new BitString(tbs.subjectUniqueID);
            }
            values[9] = tbs.extensions;
        }
    }

    public TBSCertificate(int version, BigInteger serialNumber, AlgorithmIdentifier signature, Name issuer, Validity validity, Name subject, SubjectPublicKeyInfo subjectPublicKeyInfo, boolean[] issuerUniqueID, boolean[] subjectUniqueID, Extensions extensions) {
        this.version = version;
        this.serialNumber = serialNumber;
        this.signature = signature;
        this.issuer = issuer;
        this.validity = validity;
        this.subject = subject;
        this.subjectPublicKeyInfo = subjectPublicKeyInfo;
        this.issuerUniqueID = issuerUniqueID;
        this.subjectUniqueID = subjectUniqueID;
        this.extensions = extensions;
    }

    private TBSCertificate(int version, BigInteger serialNumber, AlgorithmIdentifier signature, Name issuer, Validity validity, Name subject, SubjectPublicKeyInfo subjectPublicKeyInfo, boolean[] issuerUniqueID, boolean[] subjectUniqueID, Extensions extensions, byte[] encoding) {
        this(version, serialNumber, signature, issuer, validity, subject, subjectPublicKeyInfo, issuerUniqueID, subjectUniqueID, extensions);
        this.encoding = encoding;
    }

    public int getVersion() {
        return this.version;
    }

    public BigInteger getSerialNumber() {
        return this.serialNumber;
    }

    public AlgorithmIdentifier getSignature() {
        return this.signature;
    }

    public Name getIssuer() {
        return this.issuer;
    }

    public Validity getValidity() {
        return this.validity;
    }

    public Name getSubject() {
        return this.subject;
    }

    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.subjectPublicKeyInfo;
    }

    public boolean[] getIssuerUniqueID() {
        return this.issuerUniqueID;
    }

    public boolean[] getSubjectUniqueID() {
        return this.subjectUniqueID;
    }

    public Extensions getExtensions() {
        return this.extensions;
    }

    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = ASN1.encode(this);
        }
        return this.encoding;
    }

    public void dumpValue(StringBuilder sb) {
        char c;
        sb.append('[');
        sb.append("\n  Version: V").append(this.version + 1);
        sb.append("\n  Subject: ").append(this.subject.getName(X500Principal.RFC2253));
        sb.append("\n  Signature Algorithm: ");
        this.signature.dumpValue(sb);
        sb.append("\n  Key: ").append(this.subjectPublicKeyInfo.getPublicKey().toString());
        sb.append("\n  Validity: [From: ").append(this.validity.getNotBefore());
        sb.append("\n               To: ").append(this.validity.getNotAfter()).append(']');
        sb.append("\n  Issuer: ").append(this.issuer.getName(X500Principal.RFC2253));
        sb.append("\n  Serial Number: ").append(this.serialNumber);
        if (this.issuerUniqueID != null) {
            sb.append("\n  Issuer Id: ");
            for (boolean b : this.issuerUniqueID) {
                if (b) {
                    c = '1';
                } else {
                    c = '0';
                }
                sb.append(c);
            }
        }
        if (this.subjectUniqueID != null) {
            sb.append("\n  Subject Id: ");
            for (boolean b2 : this.subjectUniqueID) {
                if (b2) {
                    c = '1';
                } else {
                    c = '0';
                }
                sb.append(c);
            }
        }
        if (this.extensions != null) {
            sb.append("\n\n  Extensions: ");
            sb.append("[\n");
            this.extensions.dumpValue(sb, "    ");
            sb.append("  ]");
        }
        sb.append("\n]");
    }

    static {
        ASN1 = new AnonymousClass1(new ASN1Type[]{new ASN1Explicit(0, ASN1Integer.getInstance()), ASN1Integer.getInstance(), AlgorithmIdentifier.ASN1, Name.ASN1, Validity.ASN1, Name.ASN1, SubjectPublicKeyInfo.ASN1, new ASN1Implicit(1, ASN1BitString.getInstance()), new ASN1Implicit(2, ASN1BitString.getInstance()), new ASN1Explicit(3, Extensions.ASN1)});
    }
}
