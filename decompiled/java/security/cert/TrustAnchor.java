package java.security.cert;

import java.io.IOException;
import java.security.PublicKey;
import javax.security.auth.x500.X500Principal;
import org.apache.harmony.security.utils.Array;
import org.apache.harmony.security.x509.NameConstraints;

public class TrustAnchor {
    private final String caName;
    private final X500Principal caPrincipal;
    private final PublicKey caPublicKey;
    private final byte[] nameConstraints;
    private final X509Certificate trustedCert;

    public TrustAnchor(X509Certificate trustedCert, byte[] nameConstraints) {
        if (trustedCert == null) {
            throw new NullPointerException("trustedCert == null");
        }
        this.trustedCert = trustedCert;
        if (nameConstraints != null) {
            this.nameConstraints = new byte[nameConstraints.length];
            System.arraycopy(nameConstraints, 0, this.nameConstraints, 0, this.nameConstraints.length);
            processNameConstraints();
        } else {
            this.nameConstraints = null;
        }
        this.caName = null;
        this.caPrincipal = null;
        this.caPublicKey = null;
    }

    public TrustAnchor(String caName, PublicKey caPublicKey, byte[] nameConstraints) {
        if (caName == null) {
            throw new NullPointerException("caName == null");
        }
        this.caName = caName;
        if (caPublicKey == null) {
            throw new NullPointerException("caPublicKey == null");
        }
        this.caPublicKey = caPublicKey;
        if (nameConstraints != null) {
            this.nameConstraints = new byte[nameConstraints.length];
            System.arraycopy(nameConstraints, 0, this.nameConstraints, 0, this.nameConstraints.length);
            processNameConstraints();
        } else {
            this.nameConstraints = null;
        }
        this.trustedCert = null;
        if (caName.isEmpty()) {
            throw new IllegalArgumentException("caName.isEmpty()");
        }
        this.caPrincipal = new X500Principal(this.caName);
    }

    public TrustAnchor(X500Principal caPrincipal, PublicKey caPublicKey, byte[] nameConstraints) {
        if (caPrincipal == null) {
            throw new NullPointerException("caPrincipal == null");
        }
        this.caPrincipal = caPrincipal;
        if (caPublicKey == null) {
            throw new NullPointerException("caPublicKey == null");
        }
        this.caPublicKey = caPublicKey;
        if (nameConstraints != null) {
            this.nameConstraints = new byte[nameConstraints.length];
            System.arraycopy(nameConstraints, 0, this.nameConstraints, 0, this.nameConstraints.length);
            processNameConstraints();
        } else {
            this.nameConstraints = null;
        }
        this.trustedCert = null;
        this.caName = caPrincipal.getName();
    }

    public final byte[] getNameConstraints() {
        if (this.nameConstraints == null) {
            return null;
        }
        byte[] ret = new byte[this.nameConstraints.length];
        System.arraycopy(this.nameConstraints, 0, ret, 0, this.nameConstraints.length);
        return ret;
    }

    public final X509Certificate getTrustedCert() {
        return this.trustedCert;
    }

    public final X500Principal getCA() {
        return this.caPrincipal;
    }

    public final String getCAName() {
        return this.caName;
    }

    public final PublicKey getCAPublicKey() {
        return this.caPublicKey;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("TrustAnchor: [\n");
        if (this.trustedCert != null) {
            sb.append("Trusted CA certificate: ");
            sb.append(this.trustedCert);
            sb.append("\n");
        }
        if (this.caPrincipal != null) {
            sb.append("Trusted CA Name: ");
            sb.append(this.caPrincipal);
            sb.append("\n");
        }
        if (this.caPublicKey != null) {
            sb.append("Trusted CA Public Key: ");
            sb.append(this.caPublicKey);
            sb.append("\n");
        }
        if (this.nameConstraints != null) {
            sb.append("Name Constraints:\n");
            sb.append(Array.toString(this.nameConstraints, "    "));
        }
        sb.append("\n]");
        return sb.toString();
    }

    private void processNameConstraints() {
        try {
            NameConstraints.ASN1.decode(this.nameConstraints);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
