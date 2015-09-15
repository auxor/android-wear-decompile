package java.net;

import com.android.dex.DexFormat;
import java.io.IOException;
import java.io.ObjectInputStream;

public class InetSocketAddress extends SocketAddress {
    private static final long serialVersionUID = 5076001401234631237L;
    private final InetAddress addr;
    private final String hostname;
    private final int port;

    public InetSocketAddress() {
        this.addr = null;
        this.hostname = null;
        this.port = -1;
    }

    public InetSocketAddress(int port) {
        this((InetAddress) null, port);
    }

    public InetSocketAddress(InetAddress address, int port) {
        if (port < 0 || port > DexFormat.MAX_TYPE_IDX) {
            throw new IllegalArgumentException("port=" + port);
        }
        if (address == null) {
            address = Inet4Address.ANY;
        }
        this.addr = address;
        this.hostname = null;
        this.port = port;
    }

    public InetSocketAddress(String host, int port) {
        this(host, port, true);
    }

    InetSocketAddress(String hostname, int port, boolean needResolved) {
        if (hostname == null || port < 0 || port > DexFormat.MAX_TYPE_IDX) {
            throw new IllegalArgumentException("host=" + hostname + ", port=" + port);
        }
        InetAddress addr = null;
        if (needResolved) {
            try {
                addr = InetAddress.getByName(hostname);
                hostname = null;
            } catch (UnknownHostException e) {
            }
        }
        this.addr = addr;
        this.hostname = hostname;
        this.port = port;
    }

    public static InetSocketAddress createUnresolved(String host, int port) {
        return new InetSocketAddress(host, port, false);
    }

    public final int getPort() {
        return this.port;
    }

    public final InetAddress getAddress() {
        return this.addr;
    }

    public final String getHostName() {
        return this.addr != null ? this.addr.getHostName() : this.hostname;
    }

    public final String getHostString() {
        return this.hostname != null ? this.hostname : this.addr.getHostAddress();
    }

    public final boolean isUnresolved() {
        return this.addr == null;
    }

    public String toString() {
        return (this.addr != null ? this.addr.toString() : this.hostname) + ":" + this.port;
    }

    public final boolean equals(Object socketAddr) {
        if (this == socketAddr) {
            return true;
        }
        if (!(socketAddr instanceof InetSocketAddress)) {
            return false;
        }
        InetSocketAddress iSockAddr = (InetSocketAddress) socketAddr;
        if (this.port != iSockAddr.port) {
            return false;
        }
        if (this.addr == null && iSockAddr.addr == null) {
            return this.hostname.equals(iSockAddr.hostname);
        }
        if (this.addr != null) {
            return this.addr.equals(iSockAddr.addr);
        }
        return false;
    }

    public final int hashCode() {
        if (this.addr == null) {
            return this.hostname.hashCode() + this.port;
        }
        return this.addr.hashCode() + this.port;
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
    }
}
