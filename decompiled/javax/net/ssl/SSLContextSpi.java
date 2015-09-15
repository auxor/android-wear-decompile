package javax.net.ssl;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.SecureRandom;

public abstract class SSLContextSpi {
    protected abstract SSLEngine engineCreateSSLEngine();

    protected abstract SSLEngine engineCreateSSLEngine(String str, int i);

    protected abstract SSLSessionContext engineGetClientSessionContext();

    protected abstract SSLSessionContext engineGetServerSessionContext();

    protected abstract SSLServerSocketFactory engineGetServerSocketFactory();

    protected abstract SSLSocketFactory engineGetSocketFactory();

    protected abstract void engineInit(KeyManager[] keyManagerArr, TrustManager[] trustManagerArr, SecureRandom secureRandom) throws KeyManagementException;

    protected SSLParameters engineGetDefaultSSLParameters() {
        return createSSLParameters(false);
    }

    protected SSLParameters engineGetSupportedSSLParameters() {
        return createSSLParameters(true);
    }

    private SSLParameters createSSLParameters(boolean supported) {
        try {
            String[] cipherSuites;
            String[] protocols;
            SSLSocket s = (SSLSocket) engineGetSocketFactory().createSocket();
            SSLParameters p = new SSLParameters();
            if (supported) {
                cipherSuites = s.getSupportedCipherSuites();
                protocols = s.getSupportedProtocols();
            } else {
                cipherSuites = s.getEnabledCipherSuites();
                protocols = s.getEnabledProtocols();
            }
            p.setCipherSuites(cipherSuites);
            p.setProtocols(protocols);
            p.setNeedClientAuth(s.getNeedClientAuth());
            p.setWantClientAuth(s.getWantClientAuth());
            return p;
        } catch (IOException e) {
            throw new UnsupportedOperationException("Could not access supported SSL parameters");
        }
    }
}
