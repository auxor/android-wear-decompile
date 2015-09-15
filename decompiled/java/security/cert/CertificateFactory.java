package java.security.cert;

import java.io.InputStream;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.harmony.security.fortress.Engine;
import org.apache.harmony.security.fortress.Engine.SpiAndProvider;

public class CertificateFactory {
    private static final Engine ENGINE;
    private static final String SERVICE = "CertificateFactory";
    private final Provider provider;
    private final CertificateFactorySpi spiImpl;
    private final String type;

    static {
        ENGINE = new Engine(SERVICE);
    }

    protected CertificateFactory(CertificateFactorySpi certFacSpi, Provider provider, String type) {
        this.provider = provider;
        this.type = type;
        this.spiImpl = certFacSpi;
    }

    public static final CertificateFactory getInstance(String type) throws CertificateException {
        if (type == null) {
            throw new NullPointerException("type == null");
        }
        try {
            SpiAndProvider sap = ENGINE.getInstance(type, null);
            return new CertificateFactory((CertificateFactorySpi) sap.spi, sap.provider, type);
        } catch (Throwable e) {
            throw new CertificateException(e);
        }
    }

    public static final CertificateFactory getInstance(String type, String provider) throws CertificateException, NoSuchProviderException {
        if (provider == null || provider.isEmpty()) {
            throw new IllegalArgumentException("provider == null || provider.isEmpty()");
        }
        Provider impProvider = Security.getProvider(provider);
        if (impProvider != null) {
            return getInstance(type, impProvider);
        }
        throw new NoSuchProviderException(provider);
    }

    public static final CertificateFactory getInstance(String type, Provider provider) throws CertificateException {
        if (provider == null) {
            throw new IllegalArgumentException("provider == null");
        } else if (type == null) {
            throw new NullPointerException("type == null");
        } else {
            try {
                return new CertificateFactory((CertificateFactorySpi) ENGINE.getInstance(type, provider, null), provider, type);
            } catch (Throwable e) {
                throw new CertificateException(e);
            }
        }
    }

    public final Provider getProvider() {
        return this.provider;
    }

    public final String getType() {
        return this.type;
    }

    public final Certificate generateCertificate(InputStream inStream) throws CertificateException {
        return this.spiImpl.engineGenerateCertificate(inStream);
    }

    public final Iterator<String> getCertPathEncodings() {
        return this.spiImpl.engineGetCertPathEncodings();
    }

    public final CertPath generateCertPath(InputStream inStream) throws CertificateException {
        Iterator<String> it = getCertPathEncodings();
        if (it.hasNext()) {
            return this.spiImpl.engineGenerateCertPath(inStream, (String) it.next());
        }
        throw new CertificateException("There are no CertPath encodings");
    }

    public final CertPath generateCertPath(InputStream inputStream, String encoding) throws CertificateException {
        return this.spiImpl.engineGenerateCertPath(inputStream, encoding);
    }

    public final CertPath generateCertPath(List<? extends Certificate> certificates) throws CertificateException {
        return this.spiImpl.engineGenerateCertPath((List) certificates);
    }

    public final Collection<? extends Certificate> generateCertificates(InputStream inStream) throws CertificateException {
        return this.spiImpl.engineGenerateCertificates(inStream);
    }

    public final CRL generateCRL(InputStream inStream) throws CRLException {
        return this.spiImpl.engineGenerateCRL(inStream);
    }

    public final Collection<? extends CRL> generateCRLs(InputStream inStream) throws CRLException {
        return this.spiImpl.engineGenerateCRLs(inStream);
    }
}
