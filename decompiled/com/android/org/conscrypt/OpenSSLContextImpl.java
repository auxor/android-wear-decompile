package com.android.org.conscrypt;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContextSpi;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class OpenSSLContextImpl extends SSLContextSpi {
    private static DefaultSSLContextImpl DEFAULT_SSL_CONTEXT_IMPL;
    private final ClientSessionContext clientSessionContext;
    private final ServerSessionContext serverSessionContext;
    protected SSLParametersImpl sslParameters;

    public OpenSSLContextImpl() {
        this.clientSessionContext = new ClientSessionContext();
        this.serverSessionContext = new ServerSessionContext();
    }

    protected OpenSSLContextImpl(DefaultSSLContextImpl dummy) throws GeneralSecurityException, IOException {
        synchronized (DefaultSSLContextImpl.class) {
            if (DEFAULT_SSL_CONTEXT_IMPL == null) {
                this.clientSessionContext = new ClientSessionContext();
                this.serverSessionContext = new ServerSessionContext();
                DEFAULT_SSL_CONTEXT_IMPL = (DefaultSSLContextImpl) this;
            } else {
                this.clientSessionContext = DEFAULT_SSL_CONTEXT_IMPL.engineGetClientSessionContext();
                this.serverSessionContext = DEFAULT_SSL_CONTEXT_IMPL.engineGetServerSessionContext();
            }
            this.sslParameters = new SSLParametersImpl(DEFAULT_SSL_CONTEXT_IMPL.getKeyManagers(), DEFAULT_SSL_CONTEXT_IMPL.getTrustManagers(), null, this.clientSessionContext, this.serverSessionContext);
        }
    }

    public void engineInit(KeyManager[] kms, TrustManager[] tms, SecureRandom sr) throws KeyManagementException {
        this.sslParameters = new SSLParametersImpl(kms, tms, sr, this.clientSessionContext, this.serverSessionContext);
    }

    public SSLSocketFactory engineGetSocketFactory() {
        if (this.sslParameters != null) {
            return new OpenSSLSocketFactoryImpl(this.sslParameters);
        }
        throw new IllegalStateException("SSLContext is not initialized.");
    }

    public SSLServerSocketFactory engineGetServerSocketFactory() {
        if (this.sslParameters != null) {
            return new OpenSSLServerSocketFactoryImpl(this.sslParameters);
        }
        throw new IllegalStateException("SSLContext is not initialized.");
    }

    public SSLEngine engineCreateSSLEngine(String host, int port) {
        if (this.sslParameters == null) {
            throw new IllegalStateException("SSLContext is not initialized.");
        }
        SSLParametersImpl p = (SSLParametersImpl) this.sslParameters.clone();
        p.setUseClientMode(false);
        return new OpenSSLEngineImpl(host, port, p);
    }

    public SSLEngine engineCreateSSLEngine() {
        if (this.sslParameters == null) {
            throw new IllegalStateException("SSLContext is not initialized.");
        }
        SSLParametersImpl p = (SSLParametersImpl) this.sslParameters.clone();
        p.setUseClientMode(false);
        return new OpenSSLEngineImpl(p);
    }

    public ServerSessionContext engineGetServerSessionContext() {
        return this.serverSessionContext;
    }

    public ClientSessionContext engineGetClientSessionContext() {
        return this.clientSessionContext;
    }
}
