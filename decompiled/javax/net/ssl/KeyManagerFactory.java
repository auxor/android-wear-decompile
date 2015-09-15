package javax.net.ssl;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import org.apache.harmony.security.fortress.Engine;
import org.apache.harmony.security.fortress.Engine.SpiAndProvider;

public class KeyManagerFactory {
    private static final String DEFAULT_PROPERTY = "PKIX";
    private static final Engine ENGINE;
    private static final String PROPERTY_NAME = "ssl.KeyManagerFactory.algorithm";
    private static final String SERVICE = "KeyManagerFactory";
    private final String algorithm;
    private final Provider provider;
    private final KeyManagerFactorySpi spiImpl;

    static {
        ENGINE = new Engine(SERVICE);
    }

    public static final String getDefaultAlgorithm() {
        String algorithm = Security.getProperty(PROPERTY_NAME);
        return algorithm != null ? algorithm : DEFAULT_PROPERTY;
    }

    public static final KeyManagerFactory getInstance(String algorithm) throws NoSuchAlgorithmException {
        if (algorithm == null) {
            throw new NullPointerException("algorithm == null");
        }
        SpiAndProvider sap = ENGINE.getInstance(algorithm, null);
        return new KeyManagerFactory((KeyManagerFactorySpi) sap.spi, sap.provider, algorithm);
    }

    public static final KeyManagerFactory getInstance(String algorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
        if (provider == null || provider.length() == 0) {
            throw new IllegalArgumentException("Provider is null or empty");
        }
        Provider impProvider = Security.getProvider(provider);
        if (impProvider != null) {
            return getInstance(algorithm, impProvider);
        }
        throw new NoSuchProviderException(provider);
    }

    public static final KeyManagerFactory getInstance(String algorithm, Provider provider) throws NoSuchAlgorithmException {
        if (provider == null) {
            throw new IllegalArgumentException("Provider is null");
        } else if (algorithm != null) {
            return new KeyManagerFactory((KeyManagerFactorySpi) ENGINE.getInstance(algorithm, provider, null), provider, algorithm);
        } else {
            throw new NullPointerException("algorithm == null");
        }
    }

    protected KeyManagerFactory(KeyManagerFactorySpi factorySpi, Provider provider, String algorithm) {
        this.provider = provider;
        this.algorithm = algorithm;
        this.spiImpl = factorySpi;
    }

    public final String getAlgorithm() {
        return this.algorithm;
    }

    public final Provider getProvider() {
        return this.provider;
    }

    public final void init(KeyStore ks, char[] password) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        this.spiImpl.engineInit(ks, password);
    }

    public final void init(ManagerFactoryParameters spec) throws InvalidAlgorithmParameterException {
        this.spiImpl.engineInit(spec);
    }

    public final KeyManager[] getKeyManagers() {
        return this.spiImpl.engineGetKeyManagers();
    }
}
