package java.net;

import com.android.dex.DexFormat;
import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import libcore.io.IoBridge;

public class ServerSocket implements Closeable {
    private static final int DEFAULT_BACKLOG = 50;
    static SocketImplFactory factory;
    private final SocketImpl impl;
    private boolean isBound;
    private boolean isClosed;
    private InetAddress localAddress;

    public SocketImpl getImpl$() {
        return this.impl;
    }

    public ServerSocket() throws IOException {
        this.impl = factory != null ? factory.createSocketImpl() : new PlainServerSocketImpl();
        this.impl.create(true);
    }

    public ServerSocket(int port) throws IOException {
        this(port, DEFAULT_BACKLOG, Inet4Address.ANY);
    }

    public ServerSocket(int port, int backlog) throws IOException {
        this(port, backlog, Inet4Address.ANY);
    }

    public ServerSocket(int port, int backlog, InetAddress localAddress) throws IOException {
        InetAddress addr;
        checkListen(port);
        this.impl = factory != null ? factory.createSocketImpl() : new PlainServerSocketImpl();
        if (localAddress == null) {
            addr = Inet4Address.ANY;
        } else {
            addr = localAddress;
        }
        synchronized (this) {
            this.impl.create(true);
            try {
                this.impl.bind(addr, port);
                readBackBindState();
                SocketImpl socketImpl = this.impl;
                if (backlog <= 0) {
                    backlog = DEFAULT_BACKLOG;
                }
                socketImpl.listen(backlog);
            } catch (IOException e) {
                close();
                throw e;
            }
        }
    }

    private void readBackBindState() throws SocketException {
        this.localAddress = IoBridge.getSocketLocalAddress(this.impl.fd);
        this.isBound = true;
    }

    public Socket accept() throws IOException {
        checkOpen();
        if (isBound()) {
            Socket aSocket = new Socket();
            try {
                implAccept(aSocket);
                return aSocket;
            } catch (IOException e) {
                aSocket.close();
                throw e;
            }
        }
        throw new SocketException("Socket is not bound");
    }

    private void checkListen(int aPort) {
        if (aPort < 0 || aPort > DexFormat.MAX_TYPE_IDX) {
            throw new IllegalArgumentException("Port out of range: " + aPort);
        }
    }

    public void close() throws IOException {
        this.isClosed = true;
        this.impl.close();
    }

    public InetAddress getInetAddress() {
        if (isBound()) {
            return this.localAddress;
        }
        return null;
    }

    public int getLocalPort() {
        if (isBound()) {
            return this.impl.getLocalPort();
        }
        return -1;
    }

    public synchronized int getSoTimeout() throws IOException {
        checkOpen();
        return ((Integer) this.impl.getOption(SocketOptions.SO_TIMEOUT)).intValue();
    }

    protected final void implAccept(Socket aSocket) throws IOException {
        synchronized (this) {
            this.impl.accept(aSocket.impl);
            aSocket.accepted();
        }
    }

    public static synchronized void setSocketFactory(SocketImplFactory aFactory) throws IOException {
        synchronized (ServerSocket.class) {
            if (factory != null) {
                throw new SocketException("Factory already set");
            }
            factory = aFactory;
        }
    }

    public synchronized void setSoTimeout(int timeout) throws SocketException {
        checkOpen();
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout < 0");
        }
        this.impl.setOption(SocketOptions.SO_TIMEOUT, Integer.valueOf(timeout));
    }

    public String toString() {
        StringBuilder result = new StringBuilder(64);
        result.append("ServerSocket[");
        if (isBound()) {
            return result.append("addr=").append(getInetAddress().getHostName()).append("/").append(getInetAddress().getHostAddress()).append(",port=0,localport=").append(getLocalPort()).append("]").toString();
        }
        return result.append("unbound]").toString();
    }

    public void bind(SocketAddress localAddr) throws IOException {
        bind(localAddr, DEFAULT_BACKLOG);
    }

    public void bind(SocketAddress localAddr, int backlog) throws IOException {
        checkOpen();
        if (isBound()) {
            throw new BindException("Socket is already bound");
        }
        InetAddress addr;
        int port;
        if (localAddr == null) {
            addr = Inet4Address.ANY;
            port = 0;
        } else if (localAddr instanceof InetSocketAddress) {
            InetSocketAddress inetAddr = (InetSocketAddress) localAddr;
            addr = inetAddr.getAddress();
            if (addr == null) {
                throw new SocketException("Host is unresolved: " + inetAddr.getHostName());
            }
            port = inetAddr.getPort();
        } else {
            throw new IllegalArgumentException("Local address not an InetSocketAddress: " + localAddr.getClass());
        }
        synchronized (this) {
            try {
                this.impl.bind(addr, port);
                readBackBindState();
                SocketImpl socketImpl = this.impl;
                if (backlog <= 0) {
                    backlog = DEFAULT_BACKLOG;
                }
                socketImpl.listen(backlog);
            } catch (IOException e) {
                close();
                throw e;
            }
        }
    }

    public SocketAddress getLocalSocketAddress() {
        if (isBound()) {
            return new InetSocketAddress(this.localAddress, getLocalPort());
        }
        return null;
    }

    public boolean isBound() {
        return this.isBound;
    }

    public boolean isClosed() {
        return this.isClosed;
    }

    private void checkOpen() throws SocketException {
        if (isClosed()) {
            throw new SocketException("Socket is closed");
        }
    }

    public void setReuseAddress(boolean reuse) throws SocketException {
        checkOpen();
        this.impl.setOption(4, Boolean.valueOf(reuse));
    }

    public boolean getReuseAddress() throws SocketException {
        checkOpen();
        return ((Boolean) this.impl.getOption(4)).booleanValue();
    }

    public void setReceiveBufferSize(int size) throws SocketException {
        checkOpen();
        if (size < 1) {
            throw new IllegalArgumentException("size < 1");
        }
        this.impl.setOption(SocketOptions.SO_RCVBUF, Integer.valueOf(size));
    }

    public int getReceiveBufferSize() throws SocketException {
        checkOpen();
        return ((Integer) this.impl.getOption(SocketOptions.SO_RCVBUF)).intValue();
    }

    public ServerSocketChannel getChannel() {
        return null;
    }

    public void setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
    }
}
