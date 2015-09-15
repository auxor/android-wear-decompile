package javax.net.ssl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import org.apache.harmony.security.fortress.Engine;
import org.apache.harmony.security.fortress.Engine.SpiAndProvider;

public class SSLContext {
    private static SSLContext DEFAULT = null;
    private static final Engine ENGINE;
    private static final String SERVICE = "SSLContext";
    private final String protocol;
    private final Provider provider;
    private final SSLContextSpi spiImpl;

    static {
        ENGINE = new Engine(SERVICE);
    }

    public static SSLContext getDefault() throws NoSuchAlgorithmException {
        SSLContext sSLContext;
        synchronized (ENGINE) {
            if (DEFAULT == null) {
                DEFAULT = getInstance("Default");
            }
            sSLContext = DEFAULT;
        }
        return sSLContext;
    }

    public static void setDefault(SSLContext sslContext) {
        if (sslContext == null) {
            throw new NullPointerException("sslContext == null");
        }
        synchronized (ENGINE) {
            DEFAULT = sslContext;
        }
    }

    public static SSLContext getInstance(String protocol) throws NoSuchAlgorithmException {
        if (protocol == null) {
            throw new NullPointerException("protocol == null");
        }
        SpiAndProvider sap = ENGINE.getInstance(protocol, null);
        return new SSLContext((SSLContextSpi) sap.spi, sap.provider, protocol);
    }

    public static SSLContext getInstance(String protocol, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
        if (provider == null) {
            throw new IllegalArgumentException("Provider is null");
        } else if (provider.length() == 0) {
            throw new IllegalArgumentException("Provider is empty");
        } else {
            Provider impProvider = Security.getProvider(provider);
            if (impProvider != null) {
                return getInstance(protocol, impProvider);
            }
            throw new NoSuchProviderException(provider);
        }
    }

    public static SSLContext getInstance(String protocol, Provider provider) throws NoSuchAlgorithmException {
        if (provider == null) {
            throw new IllegalArgumentException("provider is null");
        } else if (protocol != null) {
            return new SSLContext((SSLContextSpi) ENGINE.getInstance(protocol, provider, null), provider, protocol);
        } else {
            throw new NullPointerException("protocol == null");
        }
    }

    protected SSLContext(SSLContextSpi contextSpi, Provider provider, String protocol) {
        this.provider = provider;
        this.protocol = protocol;
        this.spiImpl = contextSpi;
    }

    public final String getProtocol() {
        return this.protocol;
    }

    public final Provider getProvider() {
        return this.provider;
    }

    public final void init(KeyManager[] km, TrustManager[] tm, SecureRandom sr) throws KeyManagementException {
        this.spiImpl.engineInit(km, tm, sr);
    }

    public final SSLSocketFactory getSocketFactory() {
        return this.spiImpl.engineGetSocketFactory();
    }

    public final SSLServerSocketFactory getServerSocketFactory() {
        return this.spiImpl.engineGetServerSocketFactory();
    }

    public final SSLEngine createSSLEngine() {
        return this.spiImpl.engineCreateSSLEngine();
    }

    public final SSLEngine createSSLEngine(String peerHost, int peerPort) {
        return this.spiImpl.engineCreateSSLEngine(peerHost, peerPort);
    }

    public final SSLSessionContext getServerSessionContext() {
        return this.spiImpl.engineGetServerSessionContext();
    }

    public final SSLSessionContext getClientSessionContext() {
        return this.spiImpl.engineGetClientSessionContext();
    }

    public final SSLParameters getDefaultSSLParameters() {
        return this.spiImpl.engineGetDefaultSSLParameters();
    }

    public final SSLParameters getSupportedSSLParameters() {
        return this.spiImpl.engineGetSupportedSSLParameters();
    }
}
