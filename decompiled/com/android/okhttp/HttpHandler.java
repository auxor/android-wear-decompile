package com.android.okhttp;

import java.io.IOException;
import java.net.Proxy;
import java.net.ResponseCache;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class HttpHandler extends URLStreamHandler {
    private final ConfigAwareConnectionPool configAwareConnectionPool;

    public HttpHandler() {
        this.configAwareConnectionPool = ConfigAwareConnectionPool.getInstance();
    }

    protected URLConnection openConnection(URL url) throws IOException {
        return newOkHttpClient(null).open(url);
    }

    protected URLConnection openConnection(URL url, Proxy proxy) throws IOException {
        if (url != null && proxy != null) {
            return newOkHttpClient(proxy).open(url);
        }
        throw new IllegalArgumentException("url == null || proxy == null");
    }

    protected int getDefaultPort() {
        return 80;
    }

    protected OkHttpClient newOkHttpClient(Proxy proxy) {
        OkHttpClient okHttpClient = createHttpOkHttpClient(proxy);
        okHttpClient.setConnectionPool(this.configAwareConnectionPool.get());
        return okHttpClient;
    }

    public static OkHttpClient createHttpOkHttpClient(Proxy proxy) {
        OkHttpClient client = new OkHttpClient();
        client.setFollowProtocolRedirects(false);
        if (proxy != null) {
            client.setProxy(proxy);
        }
        ResponseCache responseCache = ResponseCache.getDefault();
        if (responseCache != null) {
            client.setResponseCache(responseCache);
        }
        return client;
    }
}
