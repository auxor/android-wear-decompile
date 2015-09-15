package javax.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import org.apache.harmony.security.fortress.Engine;
import org.apache.harmony.security.fortress.Engine.SpiAndProvider;

public class KeyGenerator {
    private static final Engine ENGINE;
    private static final SecureRandom RANDOM;
    private final String algorithm;
    private final Provider provider;
    private final KeyGeneratorSpi spiImpl;

    static {
        ENGINE = new Engine("KeyGenerator");
        RANDOM = new SecureRandom();
    }

    protected KeyGenerator(KeyGeneratorSpi keyGenSpi, Provider provider, String algorithm) {
        this.provider = provider;
        this.algorithm = algorithm;
        this.spiImpl = keyGenSpi;
    }

    public final String getAlgorithm() {
        return this.algorithm;
    }

    public final Provider getProvider() {
        return this.provider;
    }

    public static final KeyGenerator getInstance(String algorithm) throws NoSuchAlgorithmException {
        if (algorithm == null) {
            throw new NullPointerException("algorithm == null");
        }
        SpiAndProvider sap = ENGINE.getInstance(algorithm, null);
        return new KeyGenerator((KeyGeneratorSpi) sap.spi, sap.provider, algorithm);
    }

    public static final KeyGenerator getInstance(String algorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
        if (provider == null || provider.isEmpty()) {
            throw new IllegalArgumentException("Provider is null or empty");
        }
        Provider impProvider = Security.getProvider(provider);
        if (impProvider != null) {
            return getInstance(algorithm, impProvider);
        }
        throw new NoSuchProviderException(provider);
    }

    public static final KeyGenerator getInstance(String algorithm, Provider provider) throws NoSuchAlgorithmException {
        if (provider == null) {
            throw new IllegalArgumentException("provider == null");
        } else if (algorithm != null) {
            return new KeyGenerator((KeyGeneratorSpi) ENGINE.getInstance(algorithm, provider, null), provider, algorithm);
        } else {
            throw new NullPointerException("algorithm == null");
        }
    }

    public final SecretKey generateKey() {
        return this.spiImpl.engineGenerateKey();
    }

    public final void init(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
        this.spiImpl.engineInit(params, RANDOM);
    }

    public final void init(AlgorithmParameterSpec params, SecureRandom random) throws InvalidAlgorithmParameterException {
        this.spiImpl.engineInit(params, random);
    }

    public final void init(int keysize) {
        this.spiImpl.engineInit(keysize, RANDOM);
    }

    public final void init(int keysize, SecureRandom random) {
        this.spiImpl.engineInit(keysize, random);
    }

    public final void init(SecureRandom random) {
        this.spiImpl.engineInit(random);
    }
}
