package org.apache.http.impl.conn;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import org.apache.http.HttpHost;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

@Deprecated
public class DefaultClientConnectionOperator implements ClientConnectionOperator {
    private static final PlainSocketFactory staticPlainSocketFactory;
    protected SchemeRegistry schemeRegistry;

    static {
        staticPlainSocketFactory = new PlainSocketFactory();
    }

    public DefaultClientConnectionOperator(SchemeRegistry schemes) {
        if (schemes == null) {
            throw new IllegalArgumentException("Scheme registry must not be null.");
        }
        this.schemeRegistry = schemes;
    }

    public OperatedClientConnection createConnection() {
        return new DefaultClientConnection();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void openConnection(org.apache.http.conn.OperatedClientConnection r20, org.apache.http.HttpHost r21, java.net.InetAddress r22, org.apache.http.protocol.HttpContext r23, org.apache.http.params.HttpParams r24) throws java.io.IOException {
        /*
        r19 = this;
        if (r20 != 0) goto L_0x000a;
    L_0x0002:
        r5 = new java.lang.IllegalArgumentException;
        r6 = "Connection must not be null.";
        r5.<init>(r6);
        throw r5;
    L_0x000a:
        if (r21 != 0) goto L_0x0014;
    L_0x000c:
        r5 = new java.lang.IllegalArgumentException;
        r6 = "Target host must not be null.";
        r5.<init>(r6);
        throw r5;
    L_0x0014:
        if (r24 != 0) goto L_0x001e;
    L_0x0016:
        r5 = new java.lang.IllegalArgumentException;
        r6 = "Parameters must not be null.";
        r5.<init>(r6);
        throw r5;
    L_0x001e:
        r5 = r20.isOpen();
        if (r5 == 0) goto L_0x002c;
    L_0x0024:
        r5 = new java.lang.IllegalArgumentException;
        r6 = "Connection must not be open.";
        r5.<init>(r6);
        throw r5;
    L_0x002c:
        r0 = r19;
        r5 = r0.schemeRegistry;
        r6 = r21.getSchemeName();
        r17 = r5.getScheme(r6);
        r18 = r17.getSocketFactory();
        r0 = r18;
        r5 = r0 instanceof org.apache.http.conn.scheme.LayeredSocketFactory;
        if (r5 == 0) goto L_0x00bd;
    L_0x0042:
        r3 = staticPlainSocketFactory;
        r15 = r18;
        r15 = (org.apache.http.conn.scheme.LayeredSocketFactory) r15;
    L_0x0048:
        r5 = r21.getHostName();
        r10 = java.net.InetAddress.getAllByName(r5);
        r14 = 0;
    L_0x0051:
        r5 = r10.length;
        if (r14 >= r5) goto L_0x00bc;
    L_0x0054:
        r4 = r3.createSocket();
        r0 = r20;
        r1 = r21;
        r0.opening(r4, r1);
        r5 = r10[r14];	 Catch:{ SocketException -> 0x00cf, ConnectTimeoutException -> 0x00ee }
        r5 = r5.getHostAddress();	 Catch:{ SocketException -> 0x00cf, ConnectTimeoutException -> 0x00ee }
        r6 = r21.getPort();	 Catch:{ SocketException -> 0x00cf, ConnectTimeoutException -> 0x00ee }
        r0 = r17;
        r6 = r0.resolvePort(r6);	 Catch:{ SocketException -> 0x00cf, ConnectTimeoutException -> 0x00ee }
        r8 = 0;
        r7 = r22;
        r9 = r24;
        r12 = r3.connectSocket(r4, r5, r6, r7, r8, r9);	 Catch:{ SocketException -> 0x00cf, ConnectTimeoutException -> 0x00ee }
        if (r4 == r12) goto L_0x0082;
    L_0x007a:
        r4 = r12;
        r0 = r20;
        r1 = r21;
        r0.opening(r4, r1);	 Catch:{ SocketException -> 0x00cf, ConnectTimeoutException -> 0x00ee }
    L_0x0082:
        r0 = r19;
        r1 = r23;
        r2 = r24;
        r0.prepareSocket(r4, r1, r2);	 Catch:{ SocketException -> 0x00cf, ConnectTimeoutException -> 0x00ee }
        if (r15 == 0) goto L_0x00c1;
    L_0x008d:
        r5 = r21.getHostName();	 Catch:{ SocketException -> 0x00cf, ConnectTimeoutException -> 0x00ee }
        r6 = r21.getPort();	 Catch:{ SocketException -> 0x00cf, ConnectTimeoutException -> 0x00ee }
        r0 = r17;
        r6 = r0.resolvePort(r6);	 Catch:{ SocketException -> 0x00cf, ConnectTimeoutException -> 0x00ee }
        r7 = 1;
        r16 = r15.createSocket(r4, r5, r6, r7);	 Catch:{ SocketException -> 0x00cf, ConnectTimeoutException -> 0x00ee }
        r0 = r16;
        if (r0 == r4) goto L_0x00ad;
    L_0x00a4:
        r0 = r20;
        r1 = r16;
        r2 = r21;
        r0.opening(r1, r2);	 Catch:{ SocketException -> 0x00cf, ConnectTimeoutException -> 0x00ee }
    L_0x00ad:
        r0 = r18;
        r1 = r16;
        r5 = r0.isSecure(r1);	 Catch:{ SocketException -> 0x00cf, ConnectTimeoutException -> 0x00ee }
        r0 = r20;
        r1 = r24;
        r0.openCompleted(r5, r1);	 Catch:{ SocketException -> 0x00cf, ConnectTimeoutException -> 0x00ee }
    L_0x00bc:
        return;
    L_0x00bd:
        r3 = r18;
        r15 = 0;
        goto L_0x0048;
    L_0x00c1:
        r0 = r18;
        r5 = r0.isSecure(r4);	 Catch:{ SocketException -> 0x00cf, ConnectTimeoutException -> 0x00ee }
        r0 = r20;
        r1 = r24;
        r0.openCompleted(r5, r1);	 Catch:{ SocketException -> 0x00cf, ConnectTimeoutException -> 0x00ee }
        goto L_0x00bc;
    L_0x00cf:
        r13 = move-exception;
        r5 = r10.length;
        r5 = r5 + -1;
        if (r14 != r5) goto L_0x00f5;
    L_0x00d5:
        r5 = r13 instanceof java.net.ConnectException;
        if (r5 == 0) goto L_0x00e4;
    L_0x00d9:
        r13 = (java.net.ConnectException) r13;
        r11 = r13;
    L_0x00dc:
        r5 = new org.apache.http.conn.HttpHostConnectException;
        r0 = r21;
        r5.<init>(r0, r11);
        throw r5;
    L_0x00e4:
        r11 = new java.net.ConnectException;
        r5 = r13.getMessage();
        r11.<init>(r5, r13);
        goto L_0x00dc;
    L_0x00ee:
        r13 = move-exception;
        r5 = r10.length;
        r5 = r5 + -1;
        if (r14 != r5) goto L_0x00f5;
    L_0x00f4:
        throw r13;
    L_0x00f5:
        r14 = r14 + 1;
        goto L_0x0051;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.conn.DefaultClientConnectionOperator.openConnection(org.apache.http.conn.OperatedClientConnection, org.apache.http.HttpHost, java.net.InetAddress, org.apache.http.protocol.HttpContext, org.apache.http.params.HttpParams):void");
    }

    public void updateSecureConnection(OperatedClientConnection conn, HttpHost target, HttpContext context, HttpParams params) throws IOException {
        if (conn == null) {
            throw new IllegalArgumentException("Connection must not be null.");
        } else if (target == null) {
            throw new IllegalArgumentException("Target host must not be null.");
        } else if (params == null) {
            throw new IllegalArgumentException("Parameters must not be null.");
        } else if (conn.isOpen()) {
            Scheme schm = this.schemeRegistry.getScheme(target.getSchemeName());
            if (schm.getSocketFactory() instanceof LayeredSocketFactory) {
                LayeredSocketFactory lsf = (LayeredSocketFactory) schm.getSocketFactory();
                try {
                    Socket sock = lsf.createSocket(conn.getSocket(), target.getHostName(), schm.resolvePort(target.getPort()), true);
                    prepareSocket(sock, context, params);
                    conn.update(sock, target, lsf.isSecure(sock), params);
                    return;
                } catch (ConnectException ex) {
                    throw new HttpHostConnectException(target, ex);
                }
            }
            throw new IllegalArgumentException("Target scheme (" + schm.getName() + ") must have layered socket factory.");
        } else {
            throw new IllegalArgumentException("Connection must be open.");
        }
    }

    protected void prepareSocket(Socket sock, HttpContext context, HttpParams params) throws IOException {
        sock.setTcpNoDelay(HttpConnectionParams.getTcpNoDelay(params));
        sock.setSoTimeout(HttpConnectionParams.getSoTimeout(params));
        int linger = HttpConnectionParams.getLinger(params);
        if (linger >= 0) {
            sock.setSoLinger(linger > 0, linger);
        }
    }
}
