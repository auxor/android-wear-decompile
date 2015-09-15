package com.android.okhttp;

import java.net.Proxy;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.DefaultHostnameVerifier;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

public final class HttpsHandler extends HttpHandler {
    private static final List<Protocol> ENABLED_PROTOCOLS;
    private final ConfigAwareConnectionPool configAwareConnectionPool;

    public HttpsHandler() {
        this.configAwareConnectionPool = ConfigAwareConnectionPool.getInstance();
    }

    static {
        ENABLED_PROTOCOLS = Arrays.asList(new Protocol[]{Protocol.HTTP_11});
    }

    protected int getDefaultPort() {
        return 443;
    }

    protected OkHttpClient newOkHttpClient(Proxy proxy) {
        OkHttpClient okHttpClient = createHttpsOkHttpClient(proxy);
        okHttpClient.setConnectionPool(this.configAwareConnectionPool.get());
        return okHttpClient;
    }

    public static OkHttpClient createHttpsOkHttpClient(Proxy proxy) {
        OkHttpClient client = HttpHandler.createHttpOkHttpClient(proxy);
        client.setProtocols(ENABLED_PROTOCOLS);
        HostnameVerifier verifier = HttpsURLConnection.getDefaultHostnameVerifier();
        if (!(verifier instanceof DefaultHostnameVerifier)) {
            client.setHostnameVerifier(verifier);
        }
        client.setSslSocketFactory(HttpsURLConnection.getDefaultSSLSocketFactory());
        return client;
    }
}
