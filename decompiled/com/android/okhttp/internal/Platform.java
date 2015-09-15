package com.android.okhttp.internal;

import com.android.okhttp.Protocol;
import com.android.okio.ByteString;
import dalvik.system.SocketTagger;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import javax.net.ssl.SSLSocket;

public final class Platform {
    private static final OptionalMethod<Socket> GET_ALPN_SELECTED_PROTOCOL;
    private static final OptionalMethod<Socket> GET_NPN_SELECTED_PROTOCOL;
    private static final Platform PLATFORM;
    private static final OptionalMethod<Socket> SET_ALPN_PROTOCOLS;
    private static final OptionalMethod<Socket> SET_HOSTNAME;
    private static final OptionalMethod<Socket> SET_NPN_PROTOCOLS;
    private static final OptionalMethod<Socket> SET_USE_SESSION_TICKETS;

    static {
        PLATFORM = new Platform();
        SET_USE_SESSION_TICKETS = new OptionalMethod(null, "setUseSessionTickets", Boolean.TYPE);
        SET_HOSTNAME = new OptionalMethod(null, "setHostname", String.class);
        GET_ALPN_SELECTED_PROTOCOL = new OptionalMethod(byte[].class, "getAlpnSelectedProtocol", new Class[0]);
        SET_ALPN_PROTOCOLS = new OptionalMethod(null, "setAlpnProtocols", byte[].class);
        GET_NPN_SELECTED_PROTOCOL = new OptionalMethod(byte[].class, "getNpnSelectedProtocol", new Class[0]);
        SET_NPN_PROTOCOLS = new OptionalMethod(null, "setNpnProtocols", byte[].class);
    }

    public static Platform get() {
        return PLATFORM;
    }

    public void logW(String warning) {
        System.logW(warning);
    }

    public void tagSocket(Socket socket) throws SocketException {
        SocketTagger.get().tag(socket);
    }

    public void untagSocket(Socket socket) throws SocketException {
        SocketTagger.get().untag(socket);
    }

    public URI toUriLenient(URL url) throws URISyntaxException {
        return url.toURILenient();
    }

    public void enableTlsExtensions(SSLSocket socket, String uriHost) {
        SET_USE_SESSION_TICKETS.invokeOptionalWithoutCheckedException(socket, Boolean.valueOf(true));
        SET_HOSTNAME.invokeOptionalWithoutCheckedException(socket, uriHost);
    }

    public void supportTlsIntolerantServer(SSLSocket socket) {
        String fallbackScsv = "TLS_FALLBACK_SCSV";
        boolean socketSupportsFallbackScsv = false;
        String[] supportedCipherSuites = socket.getSupportedCipherSuites();
        for (int i = supportedCipherSuites.length - 1; i >= 0; i--) {
            if ("TLS_FALLBACK_SCSV".equals(supportedCipherSuites[i])) {
                socketSupportsFallbackScsv = true;
                break;
            }
        }
        if (socketSupportsFallbackScsv) {
            String[] enabledCipherSuites = socket.getEnabledCipherSuites();
            String[] newEnabledCipherSuites = new String[(enabledCipherSuites.length + 1)];
            System.arraycopy(enabledCipherSuites, 0, newEnabledCipherSuites, 0, enabledCipherSuites.length);
            newEnabledCipherSuites[newEnabledCipherSuites.length - 1] = "TLS_FALLBACK_SCSV";
            socket.setEnabledCipherSuites(newEnabledCipherSuites);
        }
        socket.setEnabledProtocols(new String[]{"SSLv3"});
    }

    public ByteString getNpnSelectedProtocol(SSLSocket socket) {
        boolean alpnSupported = GET_ALPN_SELECTED_PROTOCOL.isSupported(socket);
        boolean npnSupported = GET_NPN_SELECTED_PROTOCOL.isSupported(socket);
        if (!alpnSupported && !npnSupported) {
            return null;
        }
        if (alpnSupported) {
            byte[] alpnResult = (byte[]) GET_ALPN_SELECTED_PROTOCOL.invokeWithoutCheckedException(socket, new Object[0]);
            if (alpnResult != null) {
                return ByteString.of(alpnResult);
            }
        }
        if (npnSupported) {
            byte[] npnResult = (byte[]) GET_NPN_SELECTED_PROTOCOL.invokeWithoutCheckedException(socket, new Object[0]);
            if (npnResult != null) {
                return ByteString.of(npnResult);
            }
        }
        return null;
    }

    public void setNpnProtocols(SSLSocket socket, List<Protocol> npnProtocols) {
        boolean alpnSupported = SET_ALPN_PROTOCOLS.isSupported(socket);
        boolean npnSupported = SET_NPN_PROTOCOLS.isSupported(socket);
        if (alpnSupported || npnSupported) {
            byte[] protocols = concatLengthPrefixed(npnProtocols);
            if (alpnSupported) {
                SET_ALPN_PROTOCOLS.invokeWithoutCheckedException(socket, protocols);
            }
            if (npnSupported) {
                SET_NPN_PROTOCOLS.invokeWithoutCheckedException(socket, protocols);
            }
        }
    }

    public OutputStream newDeflaterOutputStream(OutputStream out, Deflater deflater, boolean syncFlush) {
        return new DeflaterOutputStream(out, deflater, syncFlush);
    }

    public void connectSocket(Socket socket, InetSocketAddress address, int connectTimeout) throws IOException {
        socket.connect(address, connectTimeout);
    }

    public String getPrefix() {
        return "X-Android";
    }

    static byte[] concatLengthPrefixed(List<Protocol> protocols) {
        int size = 0;
        for (Protocol protocol : protocols) {
            size += protocol.name.size() + 1;
        }
        byte[] result = new byte[size];
        int pos = 0;
        for (Protocol protocol2 : protocols) {
            int nameSize = protocol2.name.size();
            int pos2 = pos + 1;
            result[pos] = (byte) nameSize;
            System.arraycopy(protocol2.name.toByteArray(), 0, result, pos2, nameSize);
            pos = pos2 + nameSize;
        }
        return result;
    }
}
