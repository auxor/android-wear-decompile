package com.android.okhttp.internal.http;

import com.android.okhttp.Connection;
import com.android.okhttp.Handshake;
import com.android.okhttp.Headers;
import com.android.okhttp.Headers.Builder;
import com.android.okhttp.OkHttpClient;
import com.android.okhttp.Protocol;
import com.android.okhttp.Request;
import com.android.okhttp.Response;
import com.android.okhttp.Route;
import com.android.okhttp.internal.Platform;
import com.android.okhttp.internal.Util;
import com.android.okio.BufferedSink;
import com.android.okio.ByteString;
import com.android.okio.Sink;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketPermission;
import java.net.URL;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpURLConnectionImpl extends HttpURLConnection {
    public static final int MAX_REDIRECTS = 20;
    final OkHttpClient client;
    private long fixedContentLength;
    Handshake handshake;
    protected HttpEngine httpEngine;
    protected IOException httpEngineFailure;
    private int redirectionCount;
    private Builder requestHeaders;
    private Route route;

    enum Retry {
        NONE,
        SAME_CONNECTION,
        DIFFERENT_CONNECTION
    }

    public HttpURLConnectionImpl(URL url, OkHttpClient client) {
        super(url);
        this.requestHeaders = new Builder();
        this.fixedContentLength = -1;
        this.client = client;
    }

    public final void connect() throws IOException {
        initHttpEngine();
        do {
        } while (!execute(false));
    }

    public final void disconnect() {
        if (this.httpEngine != null) {
            try {
                this.httpEngine.disconnect();
            } catch (IOException e) {
            }
        }
    }

    public final InputStream getErrorStream() {
        InputStream inputStream = null;
        try {
            HttpEngine response = getResponse();
            if (response.hasResponseBody() && response.getResponse().code() >= 400) {
                inputStream = response.getResponseBodyBytes();
            }
        } catch (IOException e) {
        }
        return inputStream;
    }

    public final String getHeaderField(int position) {
        try {
            return getResponse().getResponse().headers().value(position);
        } catch (IOException e) {
            return null;
        }
    }

    public final String getHeaderField(String fieldName) {
        try {
            Response response = getResponse().getResponse();
            return fieldName == null ? response.statusLine() : response.headers().get(fieldName);
        } catch (IOException e) {
            return null;
        }
    }

    public final String getHeaderFieldKey(int position) {
        try {
            return getResponse().getResponse().headers().name(position);
        } catch (IOException e) {
            return null;
        }
    }

    public final Map<String, List<String>> getHeaderFields() {
        try {
            Response response = getResponse().getResponse();
            return OkHeaders.toMultimap(response.headers(), response.statusLine());
        } catch (IOException e) {
            return Collections.emptyMap();
        }
    }

    public final Map<String, List<String>> getRequestProperties() {
        if (!this.connected) {
            return OkHeaders.toMultimap(this.requestHeaders.build(), null);
        }
        throw new IllegalStateException("Cannot access request header fields after connection is set");
    }

    public final InputStream getInputStream() throws IOException {
        if (this.doInput) {
            HttpEngine response = getResponse();
            if (getResponseCode() >= 400) {
                throw new FileNotFoundException(this.url.toString());
            }
            InputStream result = response.getResponseBodyBytes();
            if (result != null) {
                return result;
            }
            throw new ProtocolException("No response body exists; responseCode=" + getResponseCode());
        }
        throw new ProtocolException("This protocol does not support input");
    }

    public final OutputStream getOutputStream() throws IOException {
        connect();
        BufferedSink sink = this.httpEngine.getBufferedRequestBody();
        if (sink == null) {
            throw new ProtocolException("method does not support a request body: " + this.method);
        } else if (!this.httpEngine.hasResponse()) {
            return sink.outputStream();
        } else {
            throw new ProtocolException("cannot write request body after response has been read");
        }
    }

    public final Permission getPermission() throws IOException {
        String hostName = getURL().getHost();
        int hostPort = Util.getEffectivePort(getURL());
        if (usingProxy()) {
            InetSocketAddress proxyAddress = (InetSocketAddress) this.client.getProxy().address();
            hostName = proxyAddress.getHostName();
            hostPort = proxyAddress.getPort();
        }
        return new SocketPermission(hostName + ":" + hostPort, "connect, resolve");
    }

    public final String getRequestProperty(String field) {
        if (field == null) {
            return null;
        }
        return this.requestHeaders.get(field);
    }

    public void setConnectTimeout(int timeoutMillis) {
        this.client.setConnectTimeout((long) timeoutMillis, TimeUnit.MILLISECONDS);
    }

    public int getConnectTimeout() {
        return this.client.getConnectTimeout();
    }

    public void setReadTimeout(int timeoutMillis) {
        this.client.setReadTimeout((long) timeoutMillis, TimeUnit.MILLISECONDS);
    }

    public int getReadTimeout() {
        return this.client.getReadTimeout();
    }

    private void initHttpEngine() throws IOException {
        if (this.httpEngineFailure != null) {
            throw this.httpEngineFailure;
        } else if (this.httpEngine == null) {
            this.connected = true;
            try {
                if (this.doOutput) {
                    if (this.method.equals("GET")) {
                        this.method = "POST";
                    } else if (!HttpMethod.hasRequestBody(this.method)) {
                        throw new ProtocolException(this.method + " does not support writing");
                    }
                }
                this.httpEngine = newHttpEngine(this.method, null, null, null);
            } catch (IOException e) {
                this.httpEngineFailure = e;
                throw e;
            }
        }
    }

    private HttpEngine newHttpEngine(String method, Connection connection, RetryableSink requestBody, Response priorResponse) {
        Request.Builder builder = new Request.Builder().url(getURL()).method(method, null);
        Headers headers = this.requestHeaders.build();
        for (int i = 0; i < headers.size(); i++) {
            builder.addHeader(headers.name(i), headers.value(i));
        }
        boolean bufferRequestBody = false;
        if (HttpMethod.hasRequestBody(method)) {
            if (this.fixedContentLength != -1) {
                builder.header("Content-Length", Long.toString(this.fixedContentLength));
            } else if (this.chunkLength > 0) {
                builder.header("Transfer-Encoding", "chunked");
            } else {
                bufferRequestBody = true;
            }
        }
        Request request = builder.build();
        OkHttpClient engineClient = this.client;
        if (!(engineClient.getOkResponseCache() == null || getUseCaches())) {
            engineClient = this.client.clone().setOkResponseCache(null);
        }
        return new HttpEngine(engineClient, request, bufferRequestBody, connection, null, requestBody, priorResponse);
    }

    private HttpEngine getResponse() throws IOException {
        initHttpEngine();
        if (this.httpEngine.hasResponse()) {
            return this.httpEngine;
        }
        int responseCode;
        while (true) {
            if (execute(true)) {
                Response response = this.httpEngine.getResponse();
                Retry retry = processResponseHeaders();
                if (retry != Retry.NONE) {
                    String retryMethod = this.method;
                    Sink requestBody = this.httpEngine.getRequestBody();
                    responseCode = this.httpEngine.getResponse().code();
                    if (responseCode == 300 || responseCode == 301 || responseCode == 302 || responseCode == 303) {
                        retryMethod = "GET";
                        this.requestHeaders.removeAll("Content-Length");
                        requestBody = null;
                    }
                    if (requestBody != null && !(requestBody instanceof RetryableSink)) {
                        break;
                    }
                    if (retry == Retry.DIFFERENT_CONNECTION) {
                        this.httpEngine.releaseConnection();
                    }
                    this.httpEngine = newHttpEngine(retryMethod, this.httpEngine.close(), (RetryableSink) requestBody, response);
                } else {
                    this.httpEngine.releaseConnection();
                    return this.httpEngine;
                }
            }
        }
        throw new HttpRetryException("Cannot retry streamed HTTP body", responseCode);
    }

    private boolean execute(boolean readResponse) throws IOException {
        try {
            this.httpEngine.sendRequest();
            this.route = this.httpEngine.getRoute();
            this.handshake = this.httpEngine.getConnection() != null ? this.httpEngine.getConnection().getHandshake() : null;
            if (readResponse) {
                this.httpEngine.readResponse();
            }
            return true;
        } catch (IOException e) {
            HttpEngine retryEngine = this.httpEngine.recover(e);
            if (retryEngine != null) {
                this.httpEngine = retryEngine;
                return false;
            }
            this.httpEngineFailure = e;
            throw e;
        }
    }

    private Retry processResponseHeaders() throws IOException {
        Connection connection = this.httpEngine.getConnection();
        Proxy selectedProxy = connection != null ? connection.getRoute().getProxy() : this.client.getProxy();
        int responseCode = getResponseCode();
        switch (responseCode) {
            case 300:
            case 301:
            case 302:
            case 303:
            case StatusLine.HTTP_TEMP_REDIRECT /*307*/:
                if (!getInstanceFollowRedirects()) {
                    return Retry.NONE;
                }
                int i = this.redirectionCount + 1;
                this.redirectionCount = i;
                if (i > MAX_REDIRECTS) {
                    throw new ProtocolException("Too many redirects: " + this.redirectionCount);
                } else if (responseCode == StatusLine.HTTP_TEMP_REDIRECT && !this.method.equals("GET") && !this.method.equals("HEAD")) {
                    return Retry.NONE;
                } else {
                    String location = getHeaderField("Location");
                    if (location == null) {
                        return Retry.NONE;
                    }
                    URL previousUrl = this.url;
                    this.url = new URL(previousUrl, location);
                    if (!this.url.getProtocol().equals("https") && !this.url.getProtocol().equals("http")) {
                        return Retry.NONE;
                    }
                    boolean sameProtocol = previousUrl.getProtocol().equals(this.url.getProtocol());
                    if (!sameProtocol && !this.client.getFollowProtocolRedirects()) {
                        return Retry.NONE;
                    }
                    boolean sameHost = previousUrl.getHost().equals(this.url.getHost());
                    boolean samePort = Util.getEffectivePort(previousUrl) == Util.getEffectivePort(this.url);
                    if (sameHost && samePort && sameProtocol) {
                        return Retry.SAME_CONNECTION;
                    }
                    return Retry.DIFFERENT_CONNECTION;
                }
            case 401:
                break;
            case 407:
                if (selectedProxy.type() != Type.HTTP) {
                    throw new ProtocolException("Received HTTP_PROXY_AUTH (407) code while not using proxy");
                }
                break;
            default:
                return Retry.NONE;
        }
        Request successorRequest = HttpAuthenticator.processAuthHeader(this.client.getAuthenticator(), this.httpEngine.getResponse(), selectedProxy);
        if (successorRequest == null) {
            return Retry.NONE;
        }
        this.requestHeaders = successorRequest.getHeaders().newBuilder();
        return Retry.SAME_CONNECTION;
    }

    public final boolean usingProxy() {
        Proxy proxy = this.route != null ? this.route.getProxy() : this.client.getProxy();
        return (proxy == null || proxy.type() == Type.DIRECT) ? false : true;
    }

    public String getResponseMessage() throws IOException {
        return getResponse().getResponse().statusMessage();
    }

    public final int getResponseCode() throws IOException {
        return getResponse().getResponse().code();
    }

    public final void setRequestProperty(String field, String newValue) {
        if (this.connected) {
            throw new IllegalStateException("Cannot set request property after connection is made");
        } else if (field == null) {
            throw new NullPointerException("field == null");
        } else if (newValue == null) {
            Platform.get().logW("Ignoring header " + field + " because its value was null.");
        } else if ("X-Android-Transports".equals(field) || "X-Android-Protocols".equals(field)) {
            setProtocols(newValue, false);
        } else {
            this.requestHeaders.set(field, newValue);
        }
    }

    public void setIfModifiedSince(long newValue) {
        super.setIfModifiedSince(newValue);
        if (this.ifModifiedSince != 0) {
            this.requestHeaders.set("If-Modified-Since", HttpDate.format(new Date(this.ifModifiedSince)));
        } else {
            this.requestHeaders.removeAll("If-Modified-Since");
        }
    }

    public final void addRequestProperty(String field, String value) {
        if (this.connected) {
            throw new IllegalStateException("Cannot add request property after connection is made");
        } else if (field == null) {
            throw new NullPointerException("field == null");
        } else if (value == null) {
            Platform.get().logW("Ignoring header " + field + " because its value was null.");
        } else if ("X-Android-Transports".equals(field) || "X-Android-Protocols".equals(field)) {
            setProtocols(value, true);
        } else {
            this.requestHeaders.add(field, value);
        }
    }

    private void setProtocols(String protocolsString, boolean append) {
        List<Protocol> protocolsList = new ArrayList();
        if (append) {
            protocolsList.addAll(this.client.getProtocols());
        }
        String[] arr$ = protocolsString.split(",", -1);
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            try {
                protocolsList.add(Protocol.find(ByteString.encodeUtf8(arr$[i$])));
                i$++;
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        this.client.setProtocols(protocolsList);
    }

    public void setRequestMethod(String method) throws ProtocolException {
        if (HttpMethod.METHODS.contains(method)) {
            this.method = method;
            return;
        }
        throw new ProtocolException("Expected one of " + HttpMethod.METHODS + " but was " + method);
    }

    public void setFixedLengthStreamingMode(int contentLength) {
        setFixedLengthStreamingMode((long) contentLength);
    }

    public void setFixedLengthStreamingMode(long contentLength) {
        if (this.connected) {
            throw new IllegalStateException("Already connected");
        } else if (this.chunkLength > 0) {
            throw new IllegalStateException("Already in chunked mode");
        } else if (contentLength < 0) {
            throw new IllegalArgumentException("contentLength < 0");
        } else {
            this.fixedContentLength = contentLength;
            this.fixedContentLength = (int) Math.min(contentLength, 2147483647L);
        }
    }
}
