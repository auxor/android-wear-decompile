package java.net;

import java.io.IOException;
import java.net.Proxy.Type;
import java.util.Collections;
import java.util.List;

final class ProxySelectorImpl extends ProxySelector {
    ProxySelectorImpl() {
    }

    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        if (uri == null || sa == null || ioe == null) {
            throw new IllegalArgumentException();
        }
    }

    public List<Proxy> select(URI uri) {
        return Collections.singletonList(selectOneProxy(uri));
    }

    private Proxy selectOneProxy(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("uri == null");
        }
        String scheme = uri.getScheme();
        if (scheme == null) {
            throw new IllegalArgumentException("scheme == null");
        }
        int port = -1;
        Proxy proxy = null;
        String nonProxyHostsKey = null;
        boolean httpProxyOkay = true;
        if ("http".equalsIgnoreCase(scheme)) {
            port = 80;
            nonProxyHostsKey = "http.nonProxyHosts";
            proxy = lookupProxy("http.proxyHost", "http.proxyPort", Type.HTTP, 80);
        } else if ("https".equalsIgnoreCase(scheme)) {
            port = 443;
            nonProxyHostsKey = "https.nonProxyHosts";
            proxy = lookupProxy("https.proxyHost", "https.proxyPort", Type.HTTP, 443);
        } else if ("ftp".equalsIgnoreCase(scheme)) {
            port = 80;
            nonProxyHostsKey = "ftp.nonProxyHosts";
            proxy = lookupProxy("ftp.proxyHost", "ftp.proxyPort", Type.HTTP, 80);
        } else if (!"socket".equalsIgnoreCase(scheme)) {
            return Proxy.NO_PROXY;
        } else {
            httpProxyOkay = false;
        }
        if (nonProxyHostsKey != null && isNonProxyHost(uri.getHost(), System.getProperty(nonProxyHostsKey))) {
            return Proxy.NO_PROXY;
        }
        if (proxy != null) {
            return proxy;
        }
        if (httpProxyOkay) {
            proxy = lookupProxy("proxyHost", "proxyPort", Type.HTTP, port);
            if (proxy != null) {
                return proxy;
            }
        }
        proxy = lookupProxy("socksProxyHost", "socksProxyPort", Type.SOCKS, 1080);
        if (proxy != null) {
            return proxy;
        }
        return Proxy.NO_PROXY;
    }

    private Proxy lookupProxy(String hostKey, String portKey, Type type, int defaultPort) {
        String host = System.getProperty(hostKey);
        if (host == null || host.isEmpty()) {
            return null;
        }
        return new Proxy(type, InetSocketAddress.createUnresolved(host, getSystemPropertyInt(portKey, defaultPort)));
    }

    private int getSystemPropertyInt(String key, int defaultValue) {
        String string = System.getProperty(key);
        if (string != null) {
            try {
                defaultValue = Integer.parseInt(string);
            } catch (NumberFormatException e) {
            }
        }
        return defaultValue;
    }

    private boolean isNonProxyHost(String host, String nonProxyHosts) {
        if (host == null || nonProxyHosts == null) {
            return false;
        }
        StringBuilder patternBuilder = new StringBuilder();
        for (int i = 0; i < nonProxyHosts.length(); i++) {
            char c = nonProxyHosts.charAt(i);
            switch (c) {
                case ZipConstants.CENOFF /*42*/:
                    patternBuilder.append(".*");
                    break;
                case ZipConstants.CENHDR /*46*/:
                    patternBuilder.append("\\.");
                    break;
                default:
                    patternBuilder.append(c);
                    break;
            }
        }
        return host.matches(patternBuilder.toString());
    }
}
