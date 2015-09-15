package java.security.cert;

import java.util.Collection;
import java.util.Set;

public abstract class PKIXCertPathChecker implements Cloneable {
    public abstract void check(Certificate certificate, Collection<String> collection) throws CertPathValidatorException;

    public abstract Set<String> getSupportedExtensions();

    public abstract void init(boolean z) throws CertPathValidatorException;

    public abstract boolean isForwardCheckingSupported();

    protected PKIXCertPathChecker() {
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (Object e) {
            throw new AssertionError(e);
        }
    }
}
