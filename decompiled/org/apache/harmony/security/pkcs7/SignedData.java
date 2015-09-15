package org.apache.harmony.security.pkcs7;

import java.util.List;
import org.apache.harmony.security.asn1.ASN1Implicit;
import org.apache.harmony.security.asn1.ASN1Integer;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1SetOf;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.x509.AlgorithmIdentifier;
import org.apache.harmony.security.x509.Certificate;
import org.apache.harmony.security.x509.CertificateList;

public final class SignedData {
    public static final ASN1Sequence ASN1;
    private final List<Certificate> certificates;
    private final ContentInfo contentInfo;
    private final List<CertificateList> crls;
    private final List<?> digestAlgorithms;
    private final List<SignerInfo> signerInfos;
    private final int version;

    /* renamed from: org.apache.harmony.security.pkcs7.SignedData.1 */
    static class AnonymousClass1 extends ASN1Sequence {
        AnonymousClass1(ASN1Type[] x0) {
            super(x0);
            setOptional(3);
            setOptional(4);
        }

        protected void getValues(Object object, Object[] values) {
            SignedData sd = (SignedData) object;
            values[0] = new byte[]{(byte) sd.version};
            values[1] = sd.digestAlgorithms;
            values[2] = sd.contentInfo;
            values[3] = sd.certificates;
            values[4] = sd.crls;
            values[5] = sd.signerInfos;
        }

        protected Object getDecodedObject(BerInputStream in) {
            Object[] values = (Object[]) in.content;
            return new SignedData((List) values[1], (ContentInfo) values[2], (List) values[3], (List) values[4], (List) values[5], null);
        }
    }

    private SignedData(int version, List<?> digestAlgorithms, ContentInfo contentInfo, List<Certificate> certificates, List<CertificateList> crls, List<SignerInfo> signerInfos) {
        this.version = version;
        this.digestAlgorithms = digestAlgorithms;
        this.contentInfo = contentInfo;
        this.certificates = certificates;
        this.crls = crls;
        this.signerInfos = signerInfos;
    }

    public List<Certificate> getCertificates() {
        return this.certificates;
    }

    public List<CertificateList> getCRLs() {
        return this.crls;
    }

    public List<SignerInfo> getSignerInfos() {
        return this.signerInfos;
    }

    public int getVersion() {
        return this.version;
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("---- SignedData:");
        res.append("\nversion: ");
        res.append(this.version);
        res.append("\ndigestAlgorithms: ");
        res.append(this.digestAlgorithms.toString());
        res.append("\ncontentInfo: ");
        res.append(this.contentInfo.toString());
        res.append("\ncertificates: ");
        if (this.certificates != null) {
            res.append(this.certificates.toString());
        }
        res.append("\ncrls: ");
        if (this.crls != null) {
            res.append(this.crls.toString());
        }
        res.append("\nsignerInfos:\n");
        res.append(this.signerInfos.toString());
        res.append("\n---- SignedData End\n]");
        return res.toString();
    }

    static {
        ASN1 = new AnonymousClass1(new ASN1Type[]{ASN1Integer.getInstance(), new ASN1SetOf(AlgorithmIdentifier.ASN1), ContentInfo.ASN1, new ASN1Implicit(0, new ASN1SetOf(Certificate.ASN1)), new ASN1Implicit(1, new ASN1SetOf(CertificateList.ASN1)), new ASN1SetOf(SignerInfo.ASN1)});
    }
}
