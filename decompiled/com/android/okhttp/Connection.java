package com.android.okhttp;

import com.android.okhttp.internal.Platform;
import com.android.okhttp.internal.Util;
import com.android.okhttp.internal.http.HttpAuthenticator;
import com.android.okhttp.internal.http.HttpConnection;
import com.android.okhttp.internal.http.HttpEngine;
import com.android.okhttp.internal.http.HttpTransport;
import com.android.okhttp.internal.http.OkHeaders;
import com.android.okhttp.internal.http.SpdyTransport;
import com.android.okhttp.internal.spdy.SpdyConnection;
import com.android.okhttp.internal.spdy.SpdyConnection.Builder;
import com.android.okio.ByteString;
import java.io.Closeable;
import java.io.IOException;
import java.net.Proxy.Type;
import java.net.Socket;
import javax.net.ssl.SSLSocket;

public final class Connection implements Closeable {
    private boolean connected;
    private Handshake handshake;
    private HttpConnection httpConnection;
    private int httpMinorVersion;
    private long idleStartTimeNs;
    private Object owner;
    private final ConnectionPool pool;
    private int recycleCount;
    private final Route route;
    private Socket socket;
    private SpdyConnection spdyConnection;

    public Connection(ConnectionPool pool, Route route) {
        this.connected = false;
        this.httpMinorVersion = 1;
        this.pool = pool;
        this.route = route;
    }

    public Object getOwner() {
        Object obj;
        synchronized (this.pool) {
            obj = this.owner;
        }
        return obj;
    }

    public void setOwner(Object owner) {
        if (!isSpdy()) {
            synchronized (this.pool) {
                if (this.owner != null) {
                    throw new IllegalStateException("Connection already has an owner!");
                }
                this.owner = owner;
            }
        }
    }

    public boolean clearOwner() {
        boolean z;
        synchronized (this.pool) {
            if (this.owner == null) {
                z = false;
            } else {
                this.owner = null;
                z = true;
            }
        }
        return z;
    }

    public void closeIfOwnedBy(Object owner) throws IOException {
        if (isSpdy()) {
            throw new IllegalStateException();
        }
        synchronized (this.pool) {
            if (this.owner != owner) {
                return;
            }
            this.owner = null;
            this.socket.close();
        }
    }

    public void connect(int connectTimeout, int readTimeout, TunnelRequest tunnelRequest) throws IOException {
        if (this.connected) {
            throw new IllegalStateException("already connected");
        }
        if (this.route.proxy.type() == Type.DIRECT || this.route.proxy.type() == Type.HTTP) {
            this.socket = this.route.address.socketFactory.createSocket();
        } else {
            this.socket = new Socket(this.route.proxy);
        }
        this.socket.setSoTimeout(readTimeout);
        Platform.get().connectSocket(this.socket, this.route.inetSocketAddress, connectTimeout);
        if (this.route.address.sslSocketFactory != null) {
            upgradeToTls(tunnelRequest);
        } else {
            this.httpConnection = new HttpConnection(this.pool, this, this.socket);
        }
        this.connected = true;
    }

    private void upgradeToTls(TunnelRequest tunnelRequest) throws IOException {
        Platform platform = Platform.get();
        if (requiresTunnel()) {
            makeTunnel(tunnelRequest);
        }
        this.socket = this.route.address.sslSocketFactory.createSocket(this.socket, this.route.address.uriHost, this.route.address.uriPort, true);
        SSLSocket sslSocket = this.socket;
        if (this.route.modernTls) {
            platform.enableTlsExtensions(sslSocket, this.route.address.uriHost);
        } else {
            platform.supportTlsIntolerantServer(sslSocket);
        }
        boolean useNpn = false;
        if (this.route.modernTls) {
            boolean http2 = this.route.address.protocols.contains(Protocol.HTTP_2);
            boolean spdy3 = this.route.address.protocols.contains(Protocol.SPDY_3);
            if (http2 && spdy3) {
                platform.setNpnProtocols(sslSocket, Protocol.HTTP2_SPDY3_AND_HTTP);
                useNpn = true;
            } else if (http2) {
                platform.setNpnProtocols(sslSocket, Protocol.HTTP2_AND_HTTP_11);
                useNpn = true;
            } else if (spdy3) {
                platform.setNpnProtocols(sslSocket, Protocol.SPDY3_AND_HTTP11);
                useNpn = true;
            }
        }
        sslSocket.startHandshake();
        if (this.route.address.hostnameVerifier.verify(this.route.address.uriHost, sslSocket.getSession())) {
            this.handshake = Handshake.get(sslSocket.getSession());
            Protocol selectedProtocol = Protocol.HTTP_11;
            if (useNpn) {
                ByteString maybeProtocol = platform.getNpnSelectedProtocol(sslSocket);
                if (maybeProtocol != null) {
                    selectedProtocol = Protocol.find(maybeProtocol);
                }
            }
            if (selectedProtocol.spdyVariant) {
                sslSocket.setSoTimeout(0);
                this.spdyConnection = new Builder(this.route.address.getUriHost(), true, this.socket).protocol(selectedProtocol).build();
                this.spdyConnection.sendConnectionHeader();
                return;
            }
            this.httpConnection = new HttpConnection(this.pool, this, this.socket);
            return;
        }
        throw new IOException("Hostname '" + this.route.address.uriHost + "' was not verified");
    }

    public boolean isConnected() {
        return this.connected;
    }

    public void close() throws IOException {
        if (this.socket != null) {
            this.socket.close();
        }
    }

    public Route getRoute() {
        return this.route;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public boolean isAlive() {
        return (this.socket.isClosed() || this.socket.isInputShutdown() || this.socket.isOutputShutdown()) ? false : true;
    }

    public boolean isReadable() {
        if (this.httpConnection != null) {
            return this.httpConnection.isReadable();
        }
        return true;
    }

    public void resetIdleStartTime() {
        if (this.spdyConnection != null) {
            throw new IllegalStateException("spdyConnection != null");
        }
        this.idleStartTimeNs = System.nanoTime();
    }

    public boolean isIdle() {
        return this.spdyConnection == null || this.spdyConnection.isIdle();
    }

    public boolean isExpired(long keepAliveDurationNs) {
        return getIdleStartTimeNs() < System.nanoTime() - keepAliveDurationNs;
    }

    public long getIdleStartTimeNs() {
        return this.spdyConnection == null ? this.idleStartTimeNs : this.spdyConnection.getIdleStartTimeNs();
    }

    public Handshake getHandshake() {
        return this.handshake;
    }

    public Object newTransport(HttpEngine httpEngine) throws IOException {
        return this.spdyConnection != null ? new SpdyTransport(httpEngine, this.spdyConnection) : new HttpTransport(httpEngine, this.httpConnection);
    }

    public boolean isSpdy() {
        return this.spdyConnection != null;
    }

    public int getHttpMinorVersion() {
        return this.httpMinorVersion;
    }

    public void setHttpMinorVersion(int httpMinorVersion) {
        this.httpMinorVersion = httpMinorVersion;
    }

    public boolean requiresTunnel() {
        return this.route.address.sslSocketFactory != null && this.route.proxy.type() == Type.HTTP;
    }

    public void updateReadTimeout(int newTimeout) throws IOException {
        if (this.connected) {
            this.socket.setSoTimeout(newTimeout);
            return;
        }
        throw new IllegalStateException("updateReadTimeout - not connected");
    }

    public void incrementRecycleCount() {
        this.recycleCount++;
    }

    public int recycleCount() {
        return this.recycleCount;
    }

    private void makeTunnel(TunnelRequest tunnelRequest) throws IOException {
        HttpConnection tunnelConnection = new HttpConnection(this.pool, this, this.socket);
        Request request = tunnelRequest.getRequest();
        String requestLine = tunnelRequest.requestLine();
        do {
            tunnelConnection.writeRequest(request.headers(), requestLine);
            tunnelConnection.flush();
            Response response = tunnelConnection.readResponse().request(request).build();
            long contentLength = OkHeaders.contentLength(response);
            if (contentLength != -1) {
                Util.skipAll(tunnelConnection.newFixedLengthSource(null, contentLength), Integer.MAX_VALUE);
            } else {
                tunnelConnection.emptyResponseBody();
            }
            switch (response.code()) {
                case 200:
                    if (tunnelConnection.bufferSize() > 0) {
                        throw new IOException("TLS tunnel buffered too many bytes!");
                    }
                    return;
                case 407:
                    request = HttpAuthenticator.processAuthHeader(this.route.address.authenticator, response, this.route.proxy);
                    break;
                default:
                    throw new IOException("Unexpected response code for CONNECT: " + response.code());
            }
        } while (request != null);
        throw new IOException("Failed to authenticate with proxy");
    }
}
