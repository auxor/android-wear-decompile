package com.android.okhttp;

import com.android.okhttp.Request.Builder;
import com.android.okhttp.internal.Util;
import java.io.IOException;
import java.net.URL;

public final class TunnelRequest {
    final String host;
    final int port;
    final String proxyAuthorization;
    final String userAgent;

    public TunnelRequest(String host, int port, String userAgent, String proxyAuthorization) {
        if (host == null) {
            throw new NullPointerException("host == null");
        } else if (userAgent == null) {
            throw new NullPointerException("userAgent == null");
        } else {
            this.host = host;
            this.port = port;
            this.userAgent = userAgent;
            this.proxyAuthorization = proxyAuthorization;
        }
    }

    String requestLine() {
        return "CONNECT " + this.host + ":" + this.port + " HTTP/1.1";
    }

    Request getRequest() throws IOException {
        Builder result = new Builder().url(new URL("https", this.host, this.port, "/"));
        result.header("Host", this.port == Util.getDefaultPort("https") ? this.host : this.host + ":" + this.port);
        result.header("User-Agent", this.userAgent);
        if (this.proxyAuthorization != null) {
            result.header("Proxy-Authorization", this.proxyAuthorization);
        }
        result.header("Proxy-Connection", "Keep-Alive");
        return result.build();
    }
}
