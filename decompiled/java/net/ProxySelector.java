package java.net;

import java.io.IOException;
import java.util.List;

public abstract class ProxySelector {
    private static ProxySelector defaultSelector;

    public abstract void connectFailed(URI uri, SocketAddress socketAddress, IOException iOException);

    public abstract List<Proxy> select(URI uri);

    static {
        defaultSelector = new ProxySelectorImpl();
    }

    public static ProxySelector getDefault() {
        return defaultSelector;
    }

    public static void setDefault(ProxySelector selector) {
        defaultSelector = selector;
    }
}
