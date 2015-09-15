package com.android.org.conscrypt;

import java.io.Serializable;
import java.security.SecureRandomSpi;

public class OpenSSLRandom extends SecureRandomSpi implements Serializable {
    private static final long serialVersionUID = 8506210602917522860L;
    private boolean mSeeded;

    protected void engineSetSeed(byte[] seed) {
        selfSeedIfNotSeeded();
        NativeCrypto.RAND_seed(seed);
    }

    protected void engineNextBytes(byte[] bytes) {
        selfSeedIfNotSeeded();
        NativeCrypto.RAND_bytes(bytes);
    }

    protected byte[] engineGenerateSeed(int numBytes) {
        selfSeedIfNotSeeded();
        byte[] output = new byte[numBytes];
        NativeCrypto.RAND_bytes(output);
        return output;
    }

    private void selfSeedIfNotSeeded() {
        if (!this.mSeeded) {
            seedOpenSSLPRNGFromLinuxRNG();
            this.mSeeded = true;
        }
    }

    public static void seedOpenSSLPRNGFromLinuxRNG() {
        int bytesRead = NativeCrypto.RAND_load_file("/dev/urandom", (long) 1024);
        if (bytesRead != NativeCrypto.SSL_kSRP) {
            throw new SecurityException("Failed to read sufficient bytes from /dev/urandom. Expected: " + NativeCrypto.SSL_kSRP + ", actual: " + bytesRead);
        }
    }
}
