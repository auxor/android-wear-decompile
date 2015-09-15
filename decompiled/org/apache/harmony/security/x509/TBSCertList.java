package org.apache.harmony.security.x509;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.security.auth.x500.X500Principal;
import org.apache.harmony.security.asn1.ASN1Explicit;
import org.apache.harmony.security.asn1.ASN1Integer;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1SequenceOf;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.x501.Name;

public final class TBSCertList {
    public static final ASN1Sequence ASN1;
    private final Extensions crlExtensions;
    private byte[] encoding;
    private final Name issuer;
    private final Date nextUpdate;
    private final List<RevokedCertificate> revokedCertificates;
    private final AlgorithmIdentifier signature;
    private final Date thisUpdate;
    private final int version;

    /* renamed from: org.apache.harmony.security.x509.TBSCertList.1 */
    static class AnonymousClass1 extends ASN1Sequence {
        AnonymousClass1(ASN1Type[] x0) {
            super(x0);
            setOptional(0);
            setOptional(4);
            setOptional(5);
            setOptional(6);
        }

        protected Object getDecodedObject(BerInputStream in) throws IOException {
            Object[] values = (Object[]) in.content;
            return new TBSCertList((AlgorithmIdentifier) values[1], (Name) values[2], (Date) values[3], (Date) values[4], (List) values[5], (Extensions) values[6], in.getEncoded(), null);
        }

        protected void getValues(Object object, Object[] values) {
            TBSCertList tbs = (TBSCertList) object;
            values[0] = tbs.version > 1 ? ASN1Integer.fromIntValue(tbs.version - 1) : null;
            values[1] = tbs.signature;
            values[2] = tbs.issuer;
            values[3] = tbs.thisUpdate;
            values[4] = tbs.nextUpdate;
            values[5] = tbs.revokedCertificates;
            values[6] = tbs.crlExtensions;
        }
    }

    public static class RevokedCertificate {
        public static final ASN1Sequence ASN1;
        private final Extensions crlEntryExtensions;
        private byte[] encoding;
        private X500Principal issuer;
        private boolean issuerRetrieved;
        private final Date revocationDate;
        private final BigInteger userCertificate;

        /* renamed from: org.apache.harmony.security.x509.TBSCertList.RevokedCertificate.1 */
        static class AnonymousClass1 extends ASN1Sequence {
            AnonymousClass1(ASN1Type[] x0) {
                super(x0);
                setOptional(2);
            }

            protected Object getDecodedObject(BerInputStream in) {
                Object[] values = (Object[]) in.content;
                return new RevokedCertificate(new BigInteger((byte[]) values[0]), (Date) values[1], (Extensions) values[2]);
            }

            protected void getValues(Object object, Object[] values) {
                RevokedCertificate rcert = (RevokedCertificate) object;
                values[0] = rcert.userCertificate.toByteArray();
                values[1] = rcert.revocationDate;
                values[2] = rcert.crlEntryExtensions;
            }
        }

        public RevokedCertificate(BigInteger userCertificate, Date revocationDate, Extensions crlEntryExtensions) {
            this.userCertificate = userCertificate;
            this.revocationDate = revocationDate;
            this.crlEntryExtensions = crlEntryExtensions;
        }

        public Extensions getCrlEntryExtensions() {
            return this.crlEntryExtensions;
        }

        public BigInteger getUserCertificate() {
            return this.userCertificate;
        }

        public Date getRevocationDate() {
            return this.revocationDate;
        }

        public X500Principal getIssuer() {
            if (this.crlEntryExtensions == null) {
                return null;
            }
            if (!this.issuerRetrieved) {
                try {
                    this.issuer = this.crlEntryExtensions.valueOfCertificateIssuerExtension();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.issuerRetrieved = true;
            }
            return this.issuer;
        }

        public byte[] getEncoded() {
            if (this.encoding == null) {
                this.encoding = ASN1.encode(this);
            }
            return this.encoding;
        }

        public boolean equals(Object rc) {
            if (!(rc instanceof RevokedCertificate)) {
                return false;
            }
            RevokedCertificate rcert = (RevokedCertificate) rc;
            if (!this.userCertificate.equals(rcert.userCertificate) || this.revocationDate.getTime() / 1000 != rcert.revocationDate.getTime() / 1000) {
                return false;
            }
            if (this.crlEntryExtensions == null) {
                if (rcert.crlEntryExtensions != null) {
                    return false;
                }
            } else if (!this.crlEntryExtensions.equals(rcert.crlEntryExtensions)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return (this.crlEntryExtensions == null ? 0 : this.crlEntryExtensions.hashCode()) + ((((int) this.revocationDate.getTime()) / Grego.MILLIS_PER_SECOND) + (this.userCertificate.hashCode() * 37));
        }

        public void dumpValue(StringBuilder sb, String prefix) {
            sb.append(prefix).append("Certificate Serial Number: ").append(this.userCertificate).append('\n');
            sb.append(prefix).append("Revocation Date: ").append(this.revocationDate);
            if (this.crlEntryExtensions != null) {
                sb.append('\n').append(prefix).append("CRL Entry Extensions: [");
                this.crlEntryExtensions.dumpValue(sb, prefix + "  ");
                sb.append(prefix).append(']');
            }
        }

        static {
            ASN1 = new AnonymousClass1(new ASN1Type[]{ASN1Integer.getInstance(), Time.ASN1, Extensions.ASN1});
        }
    }

    private TBSCertList(int version, AlgorithmIdentifier signature, Name issuer, Date thisUpdate, Date nextUpdate, List<RevokedCertificate> revokedCertificates, Extensions crlExtensions, byte[] encoding) {
        this.version = version;
        this.signature = signature;
        this.issuer = issuer;
        this.thisUpdate = thisUpdate;
        this.nextUpdate = nextUpdate;
        this.revokedCertificates = revokedCertificates;
        this.crlExtensions = crlExtensions;
        this.encoding = encoding;
    }

    public int getVersion() {
        return this.version;
    }

    public AlgorithmIdentifier getSignature() {
        return this.signature;
    }

    public Name getIssuer() {
        return this.issuer;
    }

    public Date getThisUpdate() {
        return this.thisUpdate;
    }

    public Date getNextUpdate() {
        return this.nextUpdate;
    }

    public List<RevokedCertificate> getRevokedCertificates() {
        return this.revokedCertificates;
    }

    public Extensions getCrlExtensions() {
        return this.crlExtensions;
    }

    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = ASN1.encode(this);
        }
        return this.encoding;
    }

    public boolean equals(Object other) {
        if (!(other instanceof TBSCertList)) {
            return false;
        }
        TBSCertList that = (TBSCertList) other;
        if (this.version != that.version || !this.signature.equals(that.signature) || !Arrays.equals(this.issuer.getEncoded(), that.issuer.getEncoded()) || this.thisUpdate.getTime() / 1000 != that.thisUpdate.getTime() / 1000) {
            return false;
        }
        if (this.nextUpdate == null) {
            if (that.nextUpdate != null) {
                return false;
            }
        } else if (this.nextUpdate.getTime() / 1000 != that.nextUpdate.getTime() / 1000) {
            return false;
        }
        if (((this.revokedCertificates != null && that.revokedCertificates != null) || this.revokedCertificates != that.revokedCertificates) && !this.revokedCertificates.equals(that.revokedCertificates)) {
            return false;
        }
        if (this.crlExtensions == null) {
            if (that.crlExtensions != null) {
                return false;
            }
        } else if (!this.crlExtensions.equals(that.crlExtensions)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (((((this.version * 37) + this.signature.hashCode()) * 37) + Arrays.hashCode(this.issuer.getEncoded())) * 37) + (((int) this.thisUpdate.getTime()) / Grego.MILLIS_PER_SECOND);
    }

    public void dumpValue(StringBuilder sb) {
        sb.append("X.509 CRL v").append(this.version);
        sb.append("\nSignature Algorithm: [");
        this.signature.dumpValue(sb);
        sb.append(']');
        sb.append("\nIssuer: ").append(this.issuer.getName(X500Principal.RFC2253));
        sb.append("\n\nThis Update: ").append(this.thisUpdate);
        sb.append("\nNext Update: ").append(this.nextUpdate).append('\n');
        if (this.revokedCertificates != null) {
            sb.append("\nRevoked Certificates: ").append(this.revokedCertificates.size()).append(" [");
            int number = 1;
            for (RevokedCertificate revokedCertificate : this.revokedCertificates) {
                int number2 = number + 1;
                sb.append("\n  [").append(number).append(']');
                revokedCertificate.dumpValue(sb, "  ");
                sb.append('\n');
                number = number2;
            }
            sb.append("]\n");
        }
        if (this.crlExtensions != null) {
            sb.append("\nCRL Extensions: ").append(this.crlExtensions.size()).append(" [");
            this.crlExtensions.dumpValue(sb, "  ");
            sb.append("]\n");
        }
    }

    static {
        ASN1 = new AnonymousClass1(new ASN1Type[]{ASN1Integer.getInstance(), AlgorithmIdentifier.ASN1, Name.ASN1, Time.ASN1, Time.ASN1, new ASN1SequenceOf(RevokedCertificate.ASN1), new ASN1Explicit(0, Extensions.ASN1)});
    }
}
