package java.security;

import dalvik.bytecode.Opcodes;
import java.nio.ByteOrder;
import java.security.Provider.Service;
import java.util.Random;
import libcore.io.Memory;
import org.apache.harmony.security.fortress.Engine;
import org.apache.harmony.security.fortress.Engine.SpiAndProvider;
import org.apache.harmony.security.fortress.Services;
import org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl;

public class SecureRandom extends Random {
    private static final Engine ENGINE;
    private static final String SERVICE = "SecureRandom";
    private static volatile SecureRandom internalSecureRandom = null;
    private static final long serialVersionUID = 4940670005562187L;
    private final String algorithm;
    private final Provider provider;
    private final SecureRandomSpi secureRandomSpi;

    static {
        ENGINE = new Engine(SERVICE);
    }

    public SecureRandom() {
        super(0);
        Service service = Services.getSecureRandomService();
        if (service == null) {
            this.provider = null;
            this.secureRandomSpi = new SHA1PRNG_SecureRandomImpl();
            this.algorithm = "SHA1PRNG";
            return;
        }
        try {
            this.provider = service.getProvider();
            this.secureRandomSpi = (SecureRandomSpi) service.newInstance(null);
            this.algorithm = service.getAlgorithm();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public SecureRandom(byte[] seed) {
        this();
        setSeed(seed);
    }

    protected SecureRandom(SecureRandomSpi secureRandomSpi, Provider provider) {
        this(secureRandomSpi, provider, "unknown");
    }

    private SecureRandom(SecureRandomSpi secureRandomSpi, Provider provider, String algorithm) {
        super(0);
        this.provider = provider;
        this.algorithm = algorithm;
        this.secureRandomSpi = secureRandomSpi;
    }

    public static SecureRandom getInstance(String algorithm) throws NoSuchAlgorithmException {
        if (algorithm == null) {
            throw new NullPointerException("algorithm == null");
        }
        SpiAndProvider sap = ENGINE.getInstance(algorithm, null);
        return new SecureRandom((SecureRandomSpi) sap.spi, sap.provider, algorithm);
    }

    public static SecureRandom getInstance(String algorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
        if (provider == null || provider.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Provider p = Security.getProvider(provider);
        if (p != null) {
            return getInstance(algorithm, p);
        }
        throw new NoSuchProviderException(provider);
    }

    public static SecureRandom getInstance(String algorithm, Provider provider) throws NoSuchAlgorithmException {
        if (provider == null) {
            throw new IllegalArgumentException("provider == null");
        } else if (algorithm != null) {
            return new SecureRandom((SecureRandomSpi) ENGINE.getInstance(algorithm, provider, null), provider, algorithm);
        } else {
            throw new NullPointerException("algorithm == null");
        }
    }

    public final Provider getProvider() {
        return this.provider;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    public synchronized void setSeed(byte[] seed) {
        this.secureRandomSpi.engineSetSeed(seed);
    }

    public void setSeed(long seed) {
        if (seed != 0) {
            byte[] byteSeed = new byte[8];
            Memory.pokeLong(byteSeed, 0, seed, ByteOrder.BIG_ENDIAN);
            setSeed(byteSeed);
        }
    }

    public synchronized void nextBytes(byte[] bytes) {
        this.secureRandomSpi.engineNextBytes(bytes);
    }

    protected final int next(int numBits) {
        if (numBits < 0) {
            numBits = 0;
        } else if (numBits > 32) {
            numBits = 32;
        }
        int bytes = (numBits + 7) / 8;
        byte[] next = new byte[bytes];
        int ret = 0;
        nextBytes(next);
        for (int i = 0; i < bytes; i++) {
            ret = (next[i] & Opcodes.OP_CONST_CLASS_JUMBO) | (ret << 8);
        }
        return ret >>> ((bytes * 8) - numBits);
    }

    public static byte[] getSeed(int numBytes) {
        SecureRandom result = internalSecureRandom;
        if (result == null) {
            result = new SecureRandom();
            internalSecureRandom = result;
        }
        return result.generateSeed(numBytes);
    }

    public byte[] generateSeed(int numBytes) {
        return this.secureRandomSpi.engineGenerateSeed(numBytes);
    }
}
