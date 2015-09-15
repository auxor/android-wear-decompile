package org.apache.harmony.security.pkcs7;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import javax.security.auth.x500.X500Principal;
import org.apache.harmony.security.asn1.ASN1Implicit;
import org.apache.harmony.security.asn1.ASN1Integer;
import org.apache.harmony.security.asn1.ASN1OctetString;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1SetOf;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.x501.AttributeTypeAndValue;
import org.apache.harmony.security.x501.Name;
import org.apache.harmony.security.x509.AlgorithmIdentifier;

public final class SignerInfo {
    public static final ASN1Sequence ASN1;
    public static final ASN1Sequence ISSUER_AND_SERIAL_NUMBER;
    private final AuthenticatedAttributes authenticatedAttributes;
    private final AlgorithmIdentifier digestAlgorithm;
    private final AlgorithmIdentifier digestEncryptionAlgorithm;
    private final byte[] encryptedDigest;
    private final X500Principal issuer;
    private final BigInteger serialNumber;
    private final List<?> unauthenticatedAttributes;
    private final int version;

    /* renamed from: org.apache.harmony.security.pkcs7.SignerInfo.1 */
    static class AnonymousClass1 extends ASN1Sequence {
        AnonymousClass1(ASN1Type[] x0) {
            super(x0);
        }

        public void getValues(Object object, Object[] values) {
            Object[] issAndSerial = (Object[]) object;
            values[0] = issAndSerial[0];
            values[1] = issAndSerial[1];
        }
    }

    /* renamed from: org.apache.harmony.security.pkcs7.SignerInfo.2 */
    static class AnonymousClass2 extends ASN1Sequence {
        AnonymousClass2(ASN1Type[] x0) {
            super(x0);
            setOptional(3);
            setOptional(6);
        }

        protected void getValues(Object object, Object[] values) {
            SignerInfo si = (SignerInfo) object;
            values[0] = new byte[]{(byte) si.version};
            try {
                values[1] = new Object[]{new Name(si.issuer.getName()), si.serialNumber.toByteArray()};
                values[2] = si.digestAlgorithm;
                values[3] = si.authenticatedAttributes;
                values[4] = si.digestEncryptionAlgorithm;
                values[5] = si.encryptedDigest;
                values[6] = si.unauthenticatedAttributes;
            } catch (IOException e) {
                throw new RuntimeException("Failed to encode issuer name", e);
            }
        }

        protected Object getDecodedObject(BerInputStream in) {
            Object[] values = (Object[]) in.content;
            return new SignerInfo((Object[]) values[1], (AlgorithmIdentifier) values[2], (AuthenticatedAttributes) values[3], (AlgorithmIdentifier) values[4], (byte[]) values[5], (List) values[6], null);
        }
    }

    private SignerInfo(int version, Object[] issuerAndSerialNumber, AlgorithmIdentifier digestAlgorithm, AuthenticatedAttributes authenticatedAttributes, AlgorithmIdentifier digestEncryptionAlgorithm, byte[] encryptedDigest, List<?> unauthenticatedAttributes) {
        this.version = version;
        this.issuer = ((Name) issuerAndSerialNumber[0]).getX500Principal();
        this.serialNumber = ASN1Integer.toBigIntegerValue(issuerAndSerialNumber[1]);
        this.digestAlgorithm = digestAlgorithm;
        this.authenticatedAttributes = authenticatedAttributes;
        this.digestEncryptionAlgorithm = digestEncryptionAlgorithm;
        this.encryptedDigest = encryptedDigest;
        this.unauthenticatedAttributes = unauthenticatedAttributes;
    }

    public X500Principal getIssuer() {
        return this.issuer;
    }

    public BigInteger getSerialNumber() {
        return this.serialNumber;
    }

    public String getDigestAlgorithm() {
        return this.digestAlgorithm.getAlgorithm();
    }

    public String getDigestAlgorithmName() {
        return this.digestAlgorithm.getAlgorithmName();
    }

    public String getDigestEncryptionAlgorithm() {
        return this.digestEncryptionAlgorithm.getAlgorithm();
    }

    public String getDigestEncryptionAlgorithmName() {
        return this.digestEncryptionAlgorithm.getAlgorithmName();
    }

    public List<AttributeTypeAndValue> getAuthenticatedAttributes() {
        if (this.authenticatedAttributes == null) {
            return null;
        }
        return this.authenticatedAttributes.getAttributes();
    }

    public byte[] getEncodedAuthenticatedAttributes() {
        if (this.authenticatedAttributes == null) {
            return null;
        }
        return AuthenticatedAttributes.ASN1.encode(this.authenticatedAttributes.getAttributes());
    }

    public byte[] getEncryptedDigest() {
        return this.encryptedDigest;
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("-- SignerInfo:");
        res.append("\n version : ");
        res.append(this.version);
        res.append("\nissuerAndSerialNumber:  ");
        res.append(this.issuer);
        res.append("   ");
        res.append(this.serialNumber);
        res.append("\ndigestAlgorithm:  ");
        res.append(this.digestAlgorithm.toString());
        res.append("\nauthenticatedAttributes:  ");
        if (this.authenticatedAttributes != null) {
            res.append(this.authenticatedAttributes.toString());
        }
        res.append("\ndigestEncryptionAlgorithm: ");
        res.append(this.digestEncryptionAlgorithm.toString());
        res.append("\nunauthenticatedAttributes: ");
        if (this.unauthenticatedAttributes != null) {
            res.append(this.unauthenticatedAttributes.toString());
        }
        res.append("\n-- SignerInfo End\n");
        return res.toString();
    }

    static {
        ISSUER_AND_SERIAL_NUMBER = new AnonymousClass1(new ASN1Type[]{Name.ASN1, ASN1Integer.getInstance()});
        ASN1 = new AnonymousClass2(new ASN1Type[]{ASN1Integer.getInstance(), ISSUER_AND_SERIAL_NUMBER, AlgorithmIdentifier.ASN1, new ASN1Implicit(0, AuthenticatedAttributes.ASN1), AlgorithmIdentifier.ASN1, ASN1OctetString.getInstance(), new ASN1Implicit(1, new ASN1SetOf(AttributeTypeAndValue.ASN1))});
    }
}
