package org.apache.harmony.security.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.security.auth.x500.X500Principal;
import javax.xml.datatype.DatatypeConstants;
import org.apache.harmony.security.asn1.ASN1OctetString;
import org.apache.harmony.security.asn1.BerInputStream;
import org.apache.harmony.security.pkcs7.ContentInfo;
import org.apache.harmony.security.pkcs7.SignedData;
import org.apache.harmony.security.pkcs7.SignerInfo;
import org.apache.harmony.security.x501.AttributeTypeAndValue;

public class JarUtils {
    private static final int[] MESSAGE_DIGEST_OID;

    private static class VerbatimX509Certificate extends WrappedX509Certificate {
        private byte[] encodedVerbatim;

        public VerbatimX509Certificate(java.security.cert.X509Certificate r1, byte[] r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.harmony.security.utils.JarUtils.VerbatimX509Certificate.<init>(java.security.cert.X509Certificate, byte[]):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.harmony.security.utils.JarUtils.VerbatimX509Certificate.<init>(java.security.cert.X509Certificate, byte[]):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.security.utils.JarUtils.VerbatimX509Certificate.<init>(java.security.cert.X509Certificate, byte[]):void");
        }

        public byte[] getEncoded() throws java.security.cert.CertificateEncodingException {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.harmony.security.utils.JarUtils.VerbatimX509Certificate.getEncoded():byte[]
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.harmony.security.utils.JarUtils.VerbatimX509Certificate.getEncoded():byte[]
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.security.utils.JarUtils.VerbatimX509Certificate.getEncoded():byte[]");
        }
    }

    static {
        MESSAGE_DIGEST_OID = new int[]{1, 2, DatatypeConstants.MIN_TIMEZONE_OFFSET, 113549, 1, 9, 4};
    }

    public static Certificate[] verifySignature(InputStream signature, InputStream signatureBlock) throws IOException, GeneralSecurityException {
        BerInputStream bis = new BerInputStream(signatureBlock);
        SignedData signedData = ((ContentInfo) ContentInfo.ASN1.decode(bis)).getSignedData();
        if (signedData == null) {
            throw new IOException("No SignedData found");
        }
        Collection<org.apache.harmony.security.x509.Certificate> encCerts = signedData.getCertificates();
        if (encCerts.isEmpty()) {
            return null;
        }
        X509Certificate[] certs = new X509Certificate[encCerts.size()];
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        int i = 0;
        for (org.apache.harmony.security.x509.Certificate encCert : encCerts) {
            byte[] encoded = encCert.getEncoded();
            int i2 = i + 1;
            certs[i] = new VerbatimX509Certificate((X509Certificate) cf.generateCertificate(new ByteArrayInputStream(encoded)), encoded);
            i = i2;
        }
        List<SignerInfo> sigInfos = signedData.getSignerInfos();
        if (sigInfos.isEmpty()) {
            return null;
        }
        SignerInfo sigInfo = (SignerInfo) sigInfos.get(0);
        X500Principal issuer = sigInfo.getIssuer();
        BigInteger snum = sigInfo.getSerialNumber();
        int issuerSertIndex = 0;
        i = 0;
        while (i < certs.length) {
            if (issuer.equals(certs[i].getIssuerDN()) && snum.equals(certs[i].getSerialNumber())) {
                issuerSertIndex = i;
                break;
            }
            i++;
        }
        if (i == certs.length) {
            return null;
        }
        if (certs[issuerSertIndex].hasUnsupportedCriticalExtension()) {
            throw new SecurityException("Can not recognize a critical extension");
        }
        String daOid = sigInfo.getDigestAlgorithm();
        String daName = sigInfo.getDigestAlgorithmName();
        String deaOid = sigInfo.getDigestEncryptionAlgorithm();
        String deaName = sigInfo.getDigestEncryptionAlgorithmName();
        Signature sig = null;
        if (!(daOid == null || deaOid == null)) {
            try {
                sig = Signature.getInstance(daOid + "with" + deaOid);
            } catch (NoSuchAlgorithmException e) {
            }
            if (!(sig != null || daName == null || deaName == null)) {
                try {
                    sig = Signature.getInstance(daName + "with" + deaName);
                } catch (NoSuchAlgorithmException e2) {
                }
            }
        }
        if (sig == null && deaOid != null) {
            try {
                sig = Signature.getInstance(deaOid);
            } catch (NoSuchAlgorithmException e3) {
            }
            if (sig == null) {
                try {
                    sig = Signature.getInstance(deaName);
                } catch (NoSuchAlgorithmException e4) {
                }
            }
        }
        if (sig == null) {
            return null;
        }
        sig.initVerify(certs[issuerSertIndex]);
        List<AttributeTypeAndValue> atr = sigInfo.getAuthenticatedAttributes();
        byte[] sfBytes = new byte[signature.available()];
        signature.read(sfBytes);
        if (atr == null) {
            sig.update(sfBytes);
        } else {
            sig.update(sigInfo.getEncodedAuthenticatedAttributes());
            byte[] existingDigest = null;
            for (AttributeTypeAndValue a : atr) {
                if (Arrays.equals(a.getType().getOid(), MESSAGE_DIGEST_OID)) {
                    if (existingDigest != null) {
                        throw new SecurityException("Too many MessageDigest attributes");
                    }
                    Collection<?> entries = a.getValue().getValues(ASN1OctetString.getInstance());
                    if (entries.size() != 1) {
                        throw new SecurityException("Too many values for MessageDigest attribute");
                    }
                    existingDigest = (byte[]) entries.iterator().next();
                }
            }
            if (existingDigest == null) {
                throw new SecurityException("Missing MessageDigest in Authenticated Attributes");
            }
            MessageDigest md = null;
            if (daOid != null) {
                md = MessageDigest.getInstance(daOid);
            }
            if (md == null && daName != null) {
                md = MessageDigest.getInstance(daName);
            }
            if (md == null) {
                return null;
            }
            if (!Arrays.equals(existingDigest, md.digest(sfBytes))) {
                throw new SecurityException("Incorrect MD");
            }
        }
        if (sig.verify(sigInfo.getEncryptedDigest())) {
            return createChain(certs[issuerSertIndex], certs);
        }
        throw new SecurityException("Incorrect signature");
    }

    private static X509Certificate[] createChain(X509Certificate signer, X509Certificate[] candidates) {
        Principal issuer = signer.getIssuerDN();
        if (signer.getSubjectDN().equals(issuer)) {
            return new X509Certificate[]{signer};
        }
        ArrayList<X509Certificate> chain = new ArrayList(candidates.length + 1);
        chain.add(0, signer);
        int count = 1;
        X509Certificate issuerCert;
        do {
            issuerCert = findCert(issuer, candidates);
            if (issuerCert != null) {
                chain.add(issuerCert);
                count++;
                if (count > candidates.length) {
                    break;
                }
                issuer = issuerCert.getIssuerDN();
            } else {
                break;
            }
        } while (!issuerCert.getSubjectDN().equals(issuer));
        return (X509Certificate[]) chain.toArray(new X509Certificate[count]);
    }

    private static X509Certificate findCert(Principal issuer, X509Certificate[] candidates) {
        for (int i = 0; i < candidates.length; i++) {
            if (issuer.equals(candidates[i].getSubjectDN())) {
                return candidates[i];
            }
        }
        return null;
    }
}
