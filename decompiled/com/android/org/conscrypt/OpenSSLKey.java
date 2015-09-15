package com.android.org.conscrypt;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.SecretKey;

public class OpenSSLKey {
    private final String alias;
    private final long ctx;
    private final OpenSSLEngine engine;
    private final boolean wrapped;

    public OpenSSLKey(long ctx) {
        this(ctx, false);
    }

    public OpenSSLKey(long ctx, boolean wrapped) {
        this.ctx = ctx;
        this.engine = null;
        this.alias = null;
        this.wrapped = wrapped;
    }

    public OpenSSLKey(long ctx, OpenSSLEngine engine, String alias) {
        this.ctx = ctx;
        this.engine = engine;
        this.alias = alias;
        this.wrapped = false;
    }

    public long getPkeyContext() {
        return this.ctx;
    }

    OpenSSLEngine getEngine() {
        return this.engine;
    }

    boolean isEngineBased() {
        return this.engine != null;
    }

    public String getAlias() {
        return this.alias;
    }

    public boolean isWrapped() {
        return this.wrapped;
    }

    public static OpenSSLKey fromPrivateKey(PrivateKey key) throws InvalidKeyException {
        if (key instanceof OpenSSLKeyHolder) {
            return ((OpenSSLKeyHolder) key).getOpenSSLKey();
        }
        String keyFormat = key.getFormat();
        if (keyFormat == null) {
            return wrapPrivateKey(key);
        }
        if (!"PKCS#8".equals(key.getFormat())) {
            throw new InvalidKeyException("Unknown key format " + keyFormat);
        } else if (key.getEncoded() != null) {
            return new OpenSSLKey(NativeCrypto.d2i_PKCS8_PRIV_KEY_INFO(key.getEncoded()));
        } else {
            throw new InvalidKeyException("Key encoding is null");
        }
    }

    private static OpenSSLKey wrapPrivateKey(PrivateKey key) throws InvalidKeyException {
        if (key instanceof RSAPrivateKey) {
            return OpenSSLRSAPrivateKey.wrapPlatformKey((RSAPrivateKey) key);
        }
        if (key instanceof DSAPrivateKey) {
            return OpenSSLDSAPrivateKey.wrapPlatformKey((DSAPrivateKey) key);
        }
        if (key instanceof ECPrivateKey) {
            return OpenSSLECPrivateKey.wrapPlatformKey((ECPrivateKey) key);
        }
        throw new InvalidKeyException("Unknown key type: " + key.toString());
    }

    public static OpenSSLKey fromPublicKey(PublicKey key) throws InvalidKeyException {
        if (key instanceof OpenSSLKeyHolder) {
            return ((OpenSSLKeyHolder) key).getOpenSSLKey();
        }
        if (!"X.509".equals(key.getFormat())) {
            throw new InvalidKeyException("Unknown key format " + key.getFormat());
        } else if (key.getEncoded() != null) {
            return new OpenSSLKey(NativeCrypto.d2i_PUBKEY(key.getEncoded()));
        } else {
            throw new InvalidKeyException("Key encoding is null");
        }
    }

    public PublicKey getPublicKey() throws NoSuchAlgorithmException {
        switch (NativeCrypto.EVP_PKEY_type(this.ctx)) {
            case NativeCrypto.EVP_PKEY_RSA /*6*/:
                return new OpenSSLRSAPublicKey(this);
            case NativeCrypto.EVP_PKEY_DH /*28*/:
                return new OpenSSLDHPublicKey(this);
            case NativeCrypto.EVP_PKEY_DSA /*116*/:
                return new OpenSSLDSAPublicKey(this);
            case NativeCrypto.EVP_PKEY_EC /*408*/:
                return new OpenSSLECPublicKey(this);
            default:
                throw new NoSuchAlgorithmException("unknown PKEY type");
        }
    }

    static PublicKey getPublicKey(X509EncodedKeySpec keySpec, int type) throws InvalidKeySpecException {
        try {
            OpenSSLKey key = new OpenSSLKey(NativeCrypto.d2i_PUBKEY(keySpec.getEncoded()));
            if (NativeCrypto.EVP_PKEY_type(key.getPkeyContext()) != type) {
                throw new InvalidKeySpecException("Unexpected key type");
            }
            try {
                return key.getPublicKey();
            } catch (NoSuchAlgorithmException e) {
                throw new InvalidKeySpecException(e);
            }
        } catch (Exception e2) {
            throw new InvalidKeySpecException(e2);
        }
    }

    public PrivateKey getPrivateKey() throws NoSuchAlgorithmException {
        switch (NativeCrypto.EVP_PKEY_type(this.ctx)) {
            case NativeCrypto.EVP_PKEY_RSA /*6*/:
                return new OpenSSLRSAPrivateKey(this);
            case NativeCrypto.EVP_PKEY_DH /*28*/:
                return new OpenSSLDHPrivateKey(this);
            case NativeCrypto.EVP_PKEY_DSA /*116*/:
                return new OpenSSLDSAPrivateKey(this);
            case NativeCrypto.EVP_PKEY_EC /*408*/:
                return new OpenSSLECPrivateKey(this);
            default:
                throw new NoSuchAlgorithmException("unknown PKEY type");
        }
    }

    static PrivateKey getPrivateKey(PKCS8EncodedKeySpec keySpec, int type) throws InvalidKeySpecException {
        try {
            OpenSSLKey key = new OpenSSLKey(NativeCrypto.d2i_PKCS8_PRIV_KEY_INFO(keySpec.getEncoded()));
            if (NativeCrypto.EVP_PKEY_type(key.getPkeyContext()) != type) {
                throw new InvalidKeySpecException("Unexpected key type");
            }
            try {
                return key.getPrivateKey();
            } catch (NoSuchAlgorithmException e) {
                throw new InvalidKeySpecException(e);
            }
        } catch (Exception e2) {
            throw new InvalidKeySpecException(e2);
        }
    }

    public SecretKey getSecretKey(String algorithm) throws NoSuchAlgorithmException {
        switch (NativeCrypto.EVP_PKEY_type(this.ctx)) {
            case NativeCrypto.EVP_PKEY_HMAC /*855*/:
            case NativeCrypto.EVP_PKEY_CMAC /*894*/:
                return new OpenSSLSecretKey(algorithm, this);
            default:
                throw new NoSuchAlgorithmException("unknown PKEY type");
        }
    }

    protected void finalize() throws Throwable {
        try {
            if (this.ctx != 0) {
                NativeCrypto.EVP_PKEY_free(this.ctx);
            }
            super.finalize();
        } catch (Throwable th) {
            super.finalize();
        }
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (o == this) {
            return true;
        }
        if (!(o instanceof OpenSSLKey)) {
            return false;
        }
        OpenSSLKey other = (OpenSSLKey) o;
        if (this.ctx == other.getPkeyContext()) {
            return true;
        }
        if (this.engine == null) {
            if (other.getEngine() != null) {
                return false;
            }
        } else if (!this.engine.equals(other.getEngine())) {
            return false;
        } else {
            if (this.alias != null) {
                return this.alias.equals(other.getAlias());
            }
            if (other.getAlias() != null) {
                return false;
            }
        }
        if (NativeCrypto.EVP_PKEY_cmp(this.ctx, other.getPkeyContext()) != 1) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        return ((((int) this.ctx) + 17) * 31) + ((int) (this.engine == null ? 0 : this.engine.getEngineContext()));
    }
}
