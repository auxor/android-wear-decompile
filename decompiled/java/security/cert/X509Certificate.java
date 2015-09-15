package java.security.cert;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.Principal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.security.auth.x500.X500Principal;

public abstract class X509Certificate extends Certificate implements X509Extension {
    private static final long serialVersionUID = -2491127588187038216L;

    public abstract void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException;

    public abstract void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException;

    public abstract int getBasicConstraints();

    public abstract Principal getIssuerDN();

    public abstract boolean[] getIssuerUniqueID();

    public abstract boolean[] getKeyUsage();

    public abstract Date getNotAfter();

    public abstract Date getNotBefore();

    public abstract BigInteger getSerialNumber();

    public abstract String getSigAlgName();

    public abstract String getSigAlgOID();

    public abstract byte[] getSigAlgParams();

    public abstract byte[] getSignature();

    public abstract Principal getSubjectDN();

    public abstract boolean[] getSubjectUniqueID();

    public abstract byte[] getTBSCertificate() throws CertificateEncodingException;

    public abstract int getVersion();

    protected X509Certificate() {
        super("X.509");
    }

    public X500Principal getIssuerX500Principal() {
        try {
            return ((X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(getEncoded()))).getIssuerX500Principal();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get X500Principal issuer", e);
        }
    }

    public X500Principal getSubjectX500Principal() {
        try {
            return ((X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(getEncoded()))).getSubjectX500Principal();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get X500Principal subject", e);
        }
    }

    public List<String> getExtendedKeyUsage() throws CertificateParsingException {
        return null;
    }

    public Collection<List<?>> getSubjectAlternativeNames() throws CertificateParsingException {
        return null;
    }

    public Collection<List<?>> getIssuerAlternativeNames() throws CertificateParsingException {
        return null;
    }
}
