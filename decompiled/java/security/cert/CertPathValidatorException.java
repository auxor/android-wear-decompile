package java.security.cert;

import java.security.GeneralSecurityException;

public class CertPathValidatorException extends GeneralSecurityException {
    private static final long serialVersionUID = -3083180014971893139L;
    private CertPath certPath;
    private int index;

    public CertPathValidatorException(String msg, Throwable cause, CertPath certPath, int index) {
        super(msg, cause);
        this.index = -1;
        if (certPath == null && index != -1) {
            throw new IllegalArgumentException("Index should be -1 when CertPath is null");
        } else if (certPath == null || (index >= -1 && index < certPath.getCertificates().size())) {
            this.certPath = certPath;
            this.index = index;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public CertPathValidatorException(String msg, Throwable cause) {
        super(msg, cause);
        this.index = -1;
    }

    public CertPathValidatorException(Throwable cause) {
        super(cause);
        this.index = -1;
    }

    public CertPathValidatorException(String msg) {
        super(msg);
        this.index = -1;
    }

    public CertPathValidatorException() {
        this.index = -1;
    }

    public CertPath getCertPath() {
        return this.certPath;
    }

    public int getIndex() {
        return this.index;
    }
}
