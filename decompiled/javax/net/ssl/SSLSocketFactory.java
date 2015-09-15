package javax.net.ssl;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import javax.net.SocketFactory;
import org.apache.harmony.security.fortress.Services;

public abstract class SSLSocketFactory extends SocketFactory {
    private static SocketFactory defaultSocketFactory;
    private static int lastCacheVersion;

    public abstract Socket createSocket(Socket socket, String str, int i, boolean z) throws IOException;

    public abstract String[] getDefaultCipherSuites();

    public abstract String[] getSupportedCipherSuites();

    static {
        lastCacheVersion = -1;
    }

    public static synchronized SocketFactory getDefault() {
        SocketFactory socketFactory;
        synchronized (SSLSocketFactory.class) {
            int newCacheVersion = Services.getCacheVersion();
            if (defaultSocketFactory == null || lastCacheVersion != newCacheVersion) {
                lastCacheVersion = newCacheVersion;
                String newName = Security.getProperty("ssl.SocketFactory.provider");
                if (newName != null) {
                    if (defaultSocketFactory != null) {
                        if (newName.equals(defaultSocketFactory.getClass().getName())) {
                            socketFactory = defaultSocketFactory;
                        } else {
                            defaultSocketFactory = null;
                        }
                    }
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    if (cl == null) {
                        cl = ClassLoader.getSystemClassLoader();
                    }
                    try {
                        defaultSocketFactory = (SocketFactory) Class.forName(newName, true, cl).newInstance();
                    } catch (Exception e) {
                        System.logW("Could not create " + newName + " with ClassLoader " + cl.toString() + ": " + e.getMessage());
                    }
                } else {
                    defaultSocketFactory = null;
                }
                if (defaultSocketFactory == null) {
                    SSLContext context;
                    try {
                        context = SSLContext.getDefault();
                    } catch (NoSuchAlgorithmException e2) {
                        context = null;
                    }
                    if (context != null) {
                        defaultSocketFactory = context.getSocketFactory();
                    }
                }
                if (defaultSocketFactory == null) {
                    defaultSocketFactory = new DefaultSSLSocketFactory("No SSLSocketFactory installed");
                }
                socketFactory = defaultSocketFactory;
            } else {
                socketFactory = defaultSocketFactory;
            }
        }
        return socketFactory;
    }
}
