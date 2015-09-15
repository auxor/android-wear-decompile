package java.net;

import com.android.dex.DexFormat;
import dalvik.bytecode.Opcodes;
import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy.Type;
import java.nio.channels.SocketChannel;
import libcore.io.IoBridge;
import org.w3c.dom.traversal.NodeFilter;

public class Socket implements Closeable {
    private static SocketImplFactory factory;
    private final Object connectLock;
    final SocketImpl impl;
    private boolean isBound;
    private boolean isClosed;
    private boolean isConnected;
    volatile boolean isCreated;
    private boolean isInputShutdown;
    private boolean isOutputShutdown;
    private InetAddress localAddress;
    private final Proxy proxy;

    public Socket() {
        this.isCreated = false;
        this.isBound = false;
        this.isConnected = false;
        this.isClosed = false;
        this.isInputShutdown = false;
        this.isOutputShutdown = false;
        this.localAddress = Inet4Address.ANY;
        this.connectLock = new Object();
        this.impl = factory != null ? factory.createSocketImpl() : new PlainSocketImpl();
        this.proxy = null;
    }

    public Socket(Proxy proxy) {
        this.isCreated = false;
        this.isBound = false;
        this.isConnected = false;
        this.isClosed = false;
        this.isInputShutdown = false;
        this.isOutputShutdown = false;
        this.localAddress = Inet4Address.ANY;
        this.connectLock = new Object();
        if (proxy == null || proxy.type() == Type.HTTP) {
            throw new IllegalArgumentException("Invalid proxy: " + proxy);
        }
        this.proxy = proxy;
        this.impl = factory != null ? factory.createSocketImpl() : new PlainSocketImpl(proxy);
    }

    private void tryAllAddresses(String dstName, int dstPort, InetAddress localAddress, int localPort, boolean streaming) throws IOException {
        InetAddress dstAddress;
        InetAddress[] dstAddresses = InetAddress.getAllByName(dstName);
        int i = 0;
        while (i < dstAddresses.length - 1) {
            dstAddress = dstAddresses[i];
            try {
                checkDestination(dstAddress, dstPort);
                startupSocket(dstAddress, dstPort, localAddress, localPort, streaming);
                return;
            } catch (IOException e) {
                i++;
            }
        }
        dstAddress = dstAddresses[dstAddresses.length - 1];
        checkDestination(dstAddress, dstPort);
        startupSocket(dstAddress, dstPort, localAddress, localPort, streaming);
    }

    public Socket(String dstName, int dstPort) throws UnknownHostException, IOException {
        this(dstName, dstPort, null, 0);
    }

    public Socket(String dstName, int dstPort, InetAddress localAddress, int localPort) throws IOException {
        this();
        tryAllAddresses(dstName, dstPort, localAddress, localPort, true);
    }

    @Deprecated
    public Socket(String hostName, int port, boolean streaming) throws IOException {
        this();
        tryAllAddresses(hostName, port, null, 0, streaming);
    }

    public Socket(InetAddress dstAddress, int dstPort) throws IOException {
        this();
        checkDestination(dstAddress, dstPort);
        startupSocket(dstAddress, dstPort, null, 0, true);
    }

    public Socket(InetAddress dstAddress, int dstPort, InetAddress localAddress, int localPort) throws IOException {
        this();
        checkDestination(dstAddress, dstPort);
        startupSocket(dstAddress, dstPort, localAddress, localPort, true);
    }

    @Deprecated
    public Socket(InetAddress addr, int port, boolean streaming) throws IOException {
        this();
        checkDestination(addr, port);
        startupSocket(addr, port, null, 0, streaming);
    }

    protected Socket(SocketImpl impl) throws SocketException {
        this.isCreated = false;
        this.isBound = false;
        this.isConnected = false;
        this.isClosed = false;
        this.isInputShutdown = false;
        this.isOutputShutdown = false;
        this.localAddress = Inet4Address.ANY;
        this.connectLock = new Object();
        this.impl = impl;
        this.proxy = null;
    }

    private void checkDestination(InetAddress destAddr, int dstPort) {
        if (dstPort < 0 || dstPort > DexFormat.MAX_TYPE_IDX) {
            throw new IllegalArgumentException("Port out of range: " + dstPort);
        }
    }

    public synchronized void close() throws IOException {
        this.isClosed = true;
        this.isConnected = false;
        this.localAddress = Inet4Address.ANY;
        this.impl.close();
    }

    public void onClose() {
        this.isClosed = true;
        this.isConnected = false;
        this.localAddress = Inet4Address.ANY;
        this.impl.onClose();
    }

    public InetAddress getInetAddress() {
        if (isConnected()) {
            return this.impl.getInetAddress();
        }
        return null;
    }

    public InputStream getInputStream() throws IOException {
        checkOpenAndCreate(false);
        if (!isInputShutdown()) {
            return this.impl.getInputStream();
        }
        throw new SocketException("Socket input is shutdown");
    }

    public boolean getKeepAlive() throws SocketException {
        checkOpenAndCreate(true);
        return ((Boolean) this.impl.getOption(8)).booleanValue();
    }

    public InetAddress getLocalAddress() {
        return this.localAddress;
    }

    public int getLocalPort() {
        if (isBound()) {
            return this.impl.getLocalPort();
        }
        return -1;
    }

    public OutputStream getOutputStream() throws IOException {
        checkOpenAndCreate(false);
        if (!isOutputShutdown()) {
            return this.impl.getOutputStream();
        }
        throw new SocketException("Socket output is shutdown");
    }

    public int getPort() {
        if (isConnected()) {
            return this.impl.getPort();
        }
        return 0;
    }

    public int getSoLinger() throws SocketException {
        checkOpenAndCreate(true);
        Object value = this.impl.getOption(NodeFilter.SHOW_COMMENT);
        if (value instanceof Integer) {
            return ((Integer) value).intValue();
        }
        return -1;
    }

    public synchronized int getReceiveBufferSize() throws SocketException {
        checkOpenAndCreate(true);
        return ((Integer) this.impl.getOption(SocketOptions.SO_RCVBUF)).intValue();
    }

    public synchronized int getSendBufferSize() throws SocketException {
        checkOpenAndCreate(true);
        return ((Integer) this.impl.getOption(SocketOptions.SO_SNDBUF)).intValue();
    }

    public synchronized int getSoTimeout() throws SocketException {
        checkOpenAndCreate(true);
        return ((Integer) this.impl.getOption(SocketOptions.SO_TIMEOUT)).intValue();
    }

    public boolean getTcpNoDelay() throws SocketException {
        checkOpenAndCreate(true);
        return ((Boolean) this.impl.getOption(1)).booleanValue();
    }

    public void setKeepAlive(boolean keepAlive) throws SocketException {
        if (this.impl != null) {
            checkOpenAndCreate(true);
            this.impl.setOption(8, Boolean.valueOf(keepAlive));
        }
    }

    public static synchronized void setSocketImplFactory(SocketImplFactory fac) throws IOException {
        synchronized (Socket.class) {
            if (factory != null) {
                throw new SocketException("Factory already set");
            }
            factory = fac;
        }
    }

    public synchronized void setSendBufferSize(int size) throws SocketException {
        checkOpenAndCreate(true);
        if (size < 1) {
            throw new IllegalArgumentException("size < 1");
        }
        this.impl.setOption(SocketOptions.SO_SNDBUF, Integer.valueOf(size));
    }

    public synchronized void setReceiveBufferSize(int size) throws SocketException {
        checkOpenAndCreate(true);
        if (size < 1) {
            throw new IllegalArgumentException("size < 1");
        }
        this.impl.setOption(SocketOptions.SO_RCVBUF, Integer.valueOf(size));
    }

    public void setSoLinger(boolean on, int timeout) throws SocketException {
        checkOpenAndCreate(true);
        if (on && timeout < 0) {
            throw new IllegalArgumentException("timeout < 0");
        } else if (on) {
            this.impl.setOption(NodeFilter.SHOW_COMMENT, Integer.valueOf(timeout));
        } else {
            this.impl.setOption(NodeFilter.SHOW_COMMENT, Boolean.FALSE);
        }
    }

    public synchronized void setSoTimeout(int timeout) throws SocketException {
        checkOpenAndCreate(true);
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout < 0");
        }
        this.impl.setOption(SocketOptions.SO_TIMEOUT, Integer.valueOf(timeout));
    }

    public void setTcpNoDelay(boolean on) throws SocketException {
        checkOpenAndCreate(true);
        this.impl.setOption(1, Boolean.valueOf(on));
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void startupSocket(java.net.InetAddress r6, int r7, java.net.InetAddress r8, int r9, boolean r10) throws java.io.IOException {
        /*
        r5 = this;
        if (r9 < 0) goto L_0x0007;
    L_0x0002:
        r2 = 65535; // 0xffff float:9.1834E-41 double:3.23786E-319;
        if (r9 <= r2) goto L_0x0020;
    L_0x0007:
        r2 = new java.lang.IllegalArgumentException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Local port out of range: ";
        r3 = r3.append(r4);
        r3 = r3.append(r9);
        r3 = r3.toString();
        r2.<init>(r3);
        throw r2;
    L_0x0020:
        if (r8 != 0) goto L_0x004d;
    L_0x0022:
        r0 = java.net.Inet4Address.ANY;
    L_0x0024:
        monitor-enter(r5);
        r2 = r5.impl;	 Catch:{ all -> 0x0056 }
        r2.create(r10);	 Catch:{ all -> 0x0056 }
        r2 = 1;
        r5.isCreated = r2;	 Catch:{ all -> 0x0056 }
        if (r10 == 0) goto L_0x0035;
    L_0x002f:
        r2 = r5.usingSocks();	 Catch:{ IOException -> 0x004f }
        if (r2 != 0) goto L_0x003a;
    L_0x0035:
        r2 = r5.impl;	 Catch:{ IOException -> 0x004f }
        r2.bind(r0, r9);	 Catch:{ IOException -> 0x004f }
    L_0x003a:
        r2 = 1;
        r5.isBound = r2;	 Catch:{ IOException -> 0x004f }
        r5.cacheLocalAddress();	 Catch:{ IOException -> 0x004f }
        r2 = r5.impl;	 Catch:{ IOException -> 0x004f }
        r2.connect(r6, r7);	 Catch:{ IOException -> 0x004f }
        r2 = 1;
        r5.isConnected = r2;	 Catch:{ IOException -> 0x004f }
        r5.cacheLocalAddress();	 Catch:{ IOException -> 0x004f }
        monitor-exit(r5);	 Catch:{ all -> 0x0056 }
        return;
    L_0x004d:
        r0 = r8;
        goto L_0x0024;
    L_0x004f:
        r1 = move-exception;
        r2 = r5.impl;	 Catch:{ all -> 0x0056 }
        r2.close();	 Catch:{ all -> 0x0056 }
        throw r1;	 Catch:{ all -> 0x0056 }
    L_0x0056:
        r2 = move-exception;
        monitor-exit(r5);	 Catch:{ all -> 0x0056 }
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.net.Socket.startupSocket(java.net.InetAddress, int, java.net.InetAddress, int, boolean):void");
    }

    private boolean usingSocks() {
        return this.proxy != null && this.proxy.type() == Type.SOCKS;
    }

    public String toString() {
        if (isConnected()) {
            return this.impl.toString();
        }
        return "Socket[unconnected]";
    }

    public void shutdownInput() throws IOException {
        if (isInputShutdown()) {
            throw new SocketException("Socket input is shutdown");
        }
        checkOpenAndCreate(false);
        this.impl.shutdownInput();
        this.isInputShutdown = true;
    }

    public void shutdownOutput() throws IOException {
        if (isOutputShutdown()) {
            throw new SocketException("Socket output is shutdown");
        }
        checkOpenAndCreate(false);
        this.impl.shutdownOutput();
        this.isOutputShutdown = true;
    }

    private void checkOpenAndCreate(boolean create) throws SocketException {
        if (isClosed()) {
            throw new SocketException("Socket is closed");
        } else if (create) {
            if (!this.isCreated) {
                synchronized (this) {
                    if (this.isCreated) {
                        return;
                    }
                    try {
                        this.impl.create(true);
                        this.isCreated = true;
                    } catch (SocketException e) {
                        throw e;
                    } catch (IOException e2) {
                        throw new SocketException(e2.toString());
                    }
                }
            }
        } else if (!isConnected()) {
            throw new SocketException("Socket is not connected");
        }
    }

    public SocketAddress getLocalSocketAddress() {
        if (isBound()) {
            return new InetSocketAddress(getLocalAddress(), getLocalPort());
        }
        return null;
    }

    public SocketAddress getRemoteSocketAddress() {
        if (isConnected()) {
            return new InetSocketAddress(getInetAddress(), getPort());
        }
        return null;
    }

    public boolean isBound() {
        return this.isBound;
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    public boolean isClosed() {
        return this.isClosed;
    }

    public void bind(SocketAddress localAddr) throws IOException {
        checkOpenAndCreate(true);
        if (isBound()) {
            throw new BindException("Socket is already bound");
        }
        int port;
        InetAddress addr;
        if (localAddr == null) {
            port = 0;
            addr = Inet4Address.ANY;
        } else if (localAddr instanceof InetSocketAddress) {
            InetSocketAddress inetAddr = (InetSocketAddress) localAddr;
            addr = inetAddr.getAddress();
            if (addr == null) {
                throw new UnknownHostException("Host is unresolved: " + inetAddr.getHostName());
            }
            port = inetAddr.getPort();
        } else {
            throw new IllegalArgumentException("Local address not an InetSocketAddress: " + localAddr.getClass());
        }
        synchronized (this) {
            try {
                this.impl.bind(addr, port);
                this.isBound = true;
                cacheLocalAddress();
            } catch (IOException e) {
                this.impl.close();
                throw e;
            }
        }
    }

    public void onBind(InetAddress localAddress, int localPort) {
        this.isBound = true;
        this.localAddress = localAddress;
        this.impl.onBind(localAddress, localPort);
    }

    public void connect(SocketAddress remoteAddr) throws IOException {
        connect(remoteAddr, 0);
    }

    public void connect(SocketAddress remoteAddr, int timeout) throws IOException {
        checkOpenAndCreate(true);
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout < 0");
        } else if (isConnected()) {
            throw new SocketException("Already connected");
        } else if (remoteAddr == null) {
            throw new IllegalArgumentException("remoteAddr == null");
        } else if (remoteAddr instanceof InetSocketAddress) {
            InetSocketAddress inetAddr = (InetSocketAddress) remoteAddr;
            InetAddress addr = inetAddr.getAddress();
            if (addr == null) {
                throw new UnknownHostException("Host is unresolved: " + inetAddr.getHostName());
            }
            checkDestination(addr, inetAddr.getPort());
            synchronized (this.connectLock) {
                try {
                    if (!isBound()) {
                        if (!usingSocks()) {
                            this.impl.bind(Inet4Address.ANY, 0);
                        }
                        this.isBound = true;
                    }
                    this.impl.connect(remoteAddr, timeout);
                    this.isConnected = true;
                    cacheLocalAddress();
                } catch (IOException e) {
                    this.impl.close();
                    throw e;
                }
            }
        } else {
            throw new IllegalArgumentException("Remote address not an InetSocketAddress: " + remoteAddr.getClass());
        }
    }

    public void onConnect(InetAddress remoteAddress, int remotePort) {
        this.isConnected = true;
        this.impl.onConnect(remoteAddress, remotePort);
    }

    public boolean isInputShutdown() {
        return this.isInputShutdown;
    }

    public boolean isOutputShutdown() {
        return this.isOutputShutdown;
    }

    public void setReuseAddress(boolean reuse) throws SocketException {
        checkOpenAndCreate(true);
        this.impl.setOption(4, Boolean.valueOf(reuse));
    }

    public boolean getReuseAddress() throws SocketException {
        checkOpenAndCreate(true);
        return ((Boolean) this.impl.getOption(4)).booleanValue();
    }

    public void setOOBInline(boolean oobinline) throws SocketException {
        checkOpenAndCreate(true);
        this.impl.setOption(SocketOptions.SO_OOBINLINE, Boolean.valueOf(oobinline));
    }

    public boolean getOOBInline() throws SocketException {
        checkOpenAndCreate(true);
        return ((Boolean) this.impl.getOption(SocketOptions.SO_OOBINLINE)).booleanValue();
    }

    public void setTrafficClass(int value) throws SocketException {
        checkOpenAndCreate(true);
        if (value < 0 || value > Opcodes.OP_CONST_CLASS_JUMBO) {
            throw new IllegalArgumentException("Value doesn't fit in an unsigned byte: " + value);
        }
        this.impl.setOption(3, Integer.valueOf(value));
    }

    public int getTrafficClass() throws SocketException {
        checkOpenAndCreate(true);
        return ((Integer) this.impl.getOption(3)).intValue();
    }

    public void sendUrgentData(int value) throws IOException {
        this.impl.sendUrgentData(value);
    }

    void accepted() throws SocketException {
        this.isConnected = true;
        this.isBound = true;
        this.isCreated = true;
        cacheLocalAddress();
    }

    private void cacheLocalAddress() throws SocketException {
        this.localAddress = IoBridge.getSocketLocalAddress(this.impl.fd);
    }

    public SocketChannel getChannel() {
        return null;
    }

    public FileDescriptor getFileDescriptor$() {
        return this.impl.fd;
    }

    public void setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
    }
}
