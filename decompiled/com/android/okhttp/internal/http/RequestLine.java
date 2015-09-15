package com.android.okhttp.internal.http;

import com.android.okhttp.Request;
import java.net.Proxy.Type;
import java.net.URL;

public final class RequestLine {
    private RequestLine() {
    }

    static String get(Request request, Type proxyType, int httpMinorVersion) {
        StringBuilder result = new StringBuilder();
        result.append(request.method());
        result.append(" ");
        if (includeAuthorityInRequestLine(request, proxyType)) {
            result.append(request.url());
        } else {
            result.append(requestPath(request.url()));
        }
        result.append(" ");
        result.append(version(httpMinorVersion));
        return result.toString();
    }

    private static boolean includeAuthorityInRequestLine(Request request, Type proxyType) {
        return !request.isHttps() && proxyType == Type.HTTP;
    }

    public static String requestPath(URL url) {
        String pathAndQuery = url.getFile();
        if (pathAndQuery == null) {
            return "/";
        }
        if (pathAndQuery.startsWith("/")) {
            return pathAndQuery;
        }
        return "/" + pathAndQuery;
    }

    public static String version(int httpMinorVersion) {
        return httpMinorVersion == 1 ? "HTTP/1.1" : "HTTP/1.0";
    }
}
