package android.net;

import android.content.Context;
import android.text.TextUtils;
import android.widget.AppSecurityPermissions;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpHost;

public final class Proxy {
    private static final boolean DEBUG = false;
    private static final Pattern EXCLLIST_PATTERN;
    private static final String EXCLLIST_REGEXP = "^$|^[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*(\\.[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*)*(,[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*(\\.[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*)*)*$";
    private static final String EXCL_REGEX = "[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*(\\.[a-zA-Z0-9*]+(\\-[a-zA-Z0-9*]+)*)*";
    public static final String EXTRA_PROXY_INFO = "android.intent.extra.PROXY_INFO";
    private static final Pattern HOSTNAME_PATTERN;
    private static final String HOSTNAME_REGEXP = "^$|^[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*(\\.[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*)*$";
    private static final String NAME_IP_REGEX = "[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*(\\.[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*)*";
    public static final String PROXY_CHANGE_ACTION = "android.intent.action.PROXY_CHANGE";
    public static final int PROXY_EXCLLIST_INVALID = 5;
    public static final int PROXY_HOSTNAME_EMPTY = 1;
    public static final int PROXY_HOSTNAME_INVALID = 2;
    public static final int PROXY_PORT_EMPTY = 3;
    public static final int PROXY_PORT_INVALID = 4;
    public static final int PROXY_VALID = 0;
    private static final String TAG = "Proxy";
    private static ConnectivityManager sConnectivityManager;
    private static final ProxySelector sDefaultProxySelector;

    static {
        sConnectivityManager = null;
        HOSTNAME_PATTERN = Pattern.compile(HOSTNAME_REGEXP);
        EXCLLIST_PATTERN = Pattern.compile(EXCLLIST_REGEXP);
        sDefaultProxySelector = ProxySelector.getDefault();
    }

    public static final java.net.Proxy getProxy(Context ctx, String url) {
        String host = ProxyInfo.LOCAL_EXCL_LIST;
        if (!(url == null || isLocalHost(host))) {
            List<java.net.Proxy> proxyList = ProxySelector.getDefault().select(URI.create(url));
            if (proxyList.size() > 0) {
                return (java.net.Proxy) proxyList.get(PROXY_VALID);
            }
        }
        return java.net.Proxy.NO_PROXY;
    }

    public static final String getHost(Context ctx) {
        java.net.Proxy proxy = getProxy(ctx, null);
        if (proxy == java.net.Proxy.NO_PROXY) {
            return null;
        }
        try {
            return ((InetSocketAddress) proxy.address()).getHostName();
        } catch (Exception e) {
            return null;
        }
    }

    public static final int getPort(Context ctx) {
        java.net.Proxy proxy = getProxy(ctx, null);
        if (proxy == java.net.Proxy.NO_PROXY) {
            return -1;
        }
        try {
            return ((InetSocketAddress) proxy.address()).getPort();
        } catch (Exception e) {
            return -1;
        }
    }

    public static final String getDefaultHost() {
        String host = System.getProperty("http.proxyHost");
        if (TextUtils.isEmpty(host)) {
            return null;
        }
        return host;
    }

    public static final int getDefaultPort() {
        int i = -1;
        if (getDefaultHost() != null) {
            try {
                i = Integer.parseInt(System.getProperty("http.proxyPort"));
            } catch (NumberFormatException e) {
            }
        }
        return i;
    }

    public static final HttpHost getPreferredHttpHost(Context context, String url) {
        java.net.Proxy prefProxy = getProxy(context, url);
        if (prefProxy.equals(java.net.Proxy.NO_PROXY)) {
            return null;
        }
        InetSocketAddress sa = (InetSocketAddress) prefProxy.address();
        return new HttpHost(sa.getHostName(), sa.getPort(), "http");
    }

    private static final boolean isLocalHost(String host) {
        if (host == null || host == null) {
            return DEBUG;
        }
        try {
            if (host.equalsIgnoreCase(ProxyInfo.LOCAL_HOST)) {
                return true;
            }
            if (NetworkUtils.numericToInetAddress(host).isLoopbackAddress()) {
                return true;
            }
            return DEBUG;
        } catch (IllegalArgumentException e) {
            return DEBUG;
        }
    }

    public static int validate(String hostname, String port, String exclList) {
        Matcher match = HOSTNAME_PATTERN.matcher(hostname);
        Matcher listMatch = EXCLLIST_PATTERN.matcher(exclList);
        if (!match.matches()) {
            return PROXY_HOSTNAME_INVALID;
        }
        if (!listMatch.matches()) {
            return PROXY_EXCLLIST_INVALID;
        }
        if (hostname.length() > 0 && port.length() == 0) {
            return PROXY_PORT_EMPTY;
        }
        if (port.length() > 0) {
            if (hostname.length() == 0) {
                return PROXY_HOSTNAME_EMPTY;
            }
            try {
                int portVal = Integer.parseInt(port);
                if (portVal <= 0 || portVal > AppSecurityPermissions.WHICH_ALL) {
                    return PROXY_PORT_INVALID;
                }
            } catch (NumberFormatException e) {
                return PROXY_PORT_INVALID;
            }
        }
        return PROXY_VALID;
    }

    public static final void setHttpProxySystemProperty(ProxyInfo p) {
        String host = null;
        String port = null;
        String exclList = null;
        Uri pacFileUrl = Uri.EMPTY;
        if (p != null) {
            host = p.getHost();
            port = Integer.toString(p.getPort());
            exclList = p.getExclusionListAsString();
            pacFileUrl = p.getPacFileUrl();
        }
        setHttpProxySystemProperty(host, port, exclList, pacFileUrl);
    }

    public static final void setHttpProxySystemProperty(String host, String port, String exclList, Uri pacFileUrl) {
        if (exclList != null) {
            exclList = exclList.replace(",", "|");
        }
        if (host != null) {
            System.setProperty("http.proxyHost", host);
            System.setProperty("https.proxyHost", host);
        } else {
            System.clearProperty("http.proxyHost");
            System.clearProperty("https.proxyHost");
        }
        if (port != null) {
            System.setProperty("http.proxyPort", port);
            System.setProperty("https.proxyPort", port);
        } else {
            System.clearProperty("http.proxyPort");
            System.clearProperty("https.proxyPort");
        }
        if (exclList != null) {
            System.setProperty("http.nonProxyHosts", exclList);
            System.setProperty("https.nonProxyHosts", exclList);
        } else {
            System.clearProperty("http.nonProxyHosts");
            System.clearProperty("https.nonProxyHosts");
        }
        if (Uri.EMPTY.equals(pacFileUrl)) {
            ProxySelector.setDefault(sDefaultProxySelector);
        } else {
            ProxySelector.setDefault(new PacProxySelector());
        }
    }
}
