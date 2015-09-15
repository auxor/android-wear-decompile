package org.apache.harmony.security.x509;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.apache.harmony.security.asn1.ASN1BitString;
import org.apache.harmony.security.asn1.ASN1Sequence;
import org.apache.harmony.security.asn1.ASN1Type;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.asn1.BitString;

public final class SubjectPublicKeyInfo {
    public static final ASN1Sequence ASN1;
    private AlgorithmIdentifier algorithmID;
    private byte[] encoding;
    private PublicKey publicKey;
    private byte[] subjectPublicKey;
    private int unusedBits;

    /* renamed from: org.apache.harmony.security.x509.SubjectPublicKeyInfo.1 */
    static class AnonymousClass1 extends ASN1Sequence {
        AnonymousClass1(ASN1Type[] x0) {
            super(x0);
        }

        protected Object getDecodedObject(BerInputStream in) {
            Object[] values = (Object[]) in.content;
            return new SubjectPublicKeyInfo(((BitString) values[1]).bytes, ((BitString) values[1]).unusedBits, in.getEncoded(), null);
        }

        protected void getValues(Object object, Object[] values) {
            SubjectPublicKeyInfo spki = (SubjectPublicKeyInfo) object;
            values[0] = spki.algorithmID;
            values[1] = new BitString(spki.subjectPublicKey, spki.unusedBits);
        }
    }

    public SubjectPublicKeyInfo(AlgorithmIdentifier algID, byte[] subjectPublicKey) {
        this(algID, subjectPublicKey, 0);
    }

    public SubjectPublicKeyInfo(AlgorithmIdentifier algID, byte[] subjectPublicKey, int unused) {
        this(algID, subjectPublicKey, 0, null);
    }

    private SubjectPublicKeyInfo(AlgorithmIdentifier algID, byte[] subjectPublicKey, int unused, byte[] encoding) {
        this.algorithmID = algID;
        this.subjectPublicKey = subjectPublicKey;
        this.unusedBits = unused;
        this.encoding = encoding;
    }

    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return this.algorithmID;
    }

    public byte[] getSubjectPublicKey() {
        return this.subjectPublicKey;
    }

    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = ASN1.encode(this);
        }
        return this.encoding;
    }

    public PublicKey getPublicKey() {
        if (this.publicKey == null) {
            byte[] encoded = getEncoded();
            KeySpec keySpec = new X509EncodedKeySpec(encoded);
            String algName = this.algorithmID.getAlgorithmName();
            this.publicKey = generateKeyForAlgorithm(keySpec, algName);
            String algOid = this.algorithmID.getAlgorithm();
            if (this.publicKey == null && !algOid.equals(algName)) {
                this.publicKey = generateKeyForAlgorithm(keySpec, algOid);
            }
            if (this.publicKey == null) {
                this.publicKey = new X509PublicKey(algOid, encoded, this.subjectPublicKey);
            }
        }
        return this.publicKey;
    }

    private static PublicKey generateKeyForAlgorithm(KeySpec keySpec, String algorithm) {
        try {
            return KeyFactory.getInstance(algorithm).generatePublic(keySpec);
        } catch (InvalidKeySpecException e) {
            return null;
        } catch (NoSuchAlgorithmException e2) {
            return null;
        }
    }

    static {
        ASN1 = new AnonymousClass1(new ASN1Type[]{AlgorithmIdentifier.ASN1, ASN1BitString.getInstance()});
    }
}
