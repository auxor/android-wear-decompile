package java.net;

import android.system.ErrnoException;
import android.system.OsConstants;
import com.android.dex.DexFormat;
import dalvik.bytecode.Opcodes;
import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.DatagramChannel;
import libcore.io.IoBridge;
import libcore.io.Libcore;

public class DatagramSocket implements Closeable {
    static DatagramSocketImplFactory factory;
    InetAddress address;
    DatagramSocketImpl impl;
    boolean isBound;
    private boolean isClosed;
    private boolean isConnected;
    private Object lock;
    private SocketException pendingConnectException;
    int port;

    public DatagramSocket() throws SocketException {
        this(0);
    }

    public DatagramSocket(int aPort) throws SocketException {
        this.port = -1;
        this.isBound = false;
        this.isConnected = false;
        this.isClosed = false;
        this.lock = new Object();
        checkPort(aPort);
        createSocket(aPort, Inet4Address.ANY);
    }

    public DatagramSocket(int aPort, InetAddress addr) throws SocketException {
        this.port = -1;
        this.isBound = false;
        this.isConnected = false;
        this.isClosed = false;
        this.lock = new Object();
        checkPort(aPort);
        if (addr == null) {
            addr = Inet4Address.ANY;
        }
        createSocket(aPort, addr);
    }

    private void checkPort(int aPort) {
        if (aPort < 0 || aPort > DexFormat.MAX_TYPE_IDX) {
            throw new IllegalArgumentException("Port out of range: " + aPort);
        }
    }

    public void close() {
        this.isClosed = true;
        this.impl.close();
    }

    public void onClose() {
        this.isClosed = true;
        this.impl.onClose();
    }

    public void disconnect() {
        if (!isClosed() && isConnected()) {
            this.impl.disconnect();
            this.address = null;
            this.port = -1;
            this.isConnected = false;
        }
    }

    public void onDisconnect() {
        this.address = null;
        this.port = -1;
        this.isConnected = false;
        this.impl.onDisconnect();
    }

    synchronized void createSocket(int aPort, InetAddress addr) throws SocketException {
        this.impl = factory != null ? factory.createDatagramSocketImpl() : new PlainDatagramSocketImpl();
        this.impl.create();
        try {
            this.impl.bind(aPort, addr);
            this.isBound = true;
        } catch (SocketException e) {
            close();
            throw e;
        }
    }

    public InetAddress getInetAddress() {
        return this.address;
    }

    public InetAddress getLocalAddress() {
        try {
            return IoBridge.getSocketLocalAddress(this.impl.fd);
        } catch (SocketException e) {
            return null;
        }
    }

    public int getLocalPort() {
        if (isClosed()) {
            return -1;
        }
        if (isBound()) {
            return this.impl.getLocalPort();
        }
        return 0;
    }

    public int getPort() {
        return this.port;
    }

    boolean isMulticastSocket() {
        return false;
    }

    public synchronized int getReceiveBufferSize() throws SocketException {
        checkOpen();
        return ((Integer) this.impl.getOption(SocketOptions.SO_RCVBUF)).intValue();
    }

    public synchronized int getSendBufferSize() throws SocketException {
        checkOpen();
        return ((Integer) this.impl.getOption(SocketOptions.SO_SNDBUF)).intValue();
    }

    public synchronized int getSoTimeout() throws SocketException {
        checkOpen();
        return ((Integer) this.impl.getOption(SocketOptions.SO_TIMEOUT)).intValue();
    }

    public synchronized void receive(DatagramPacket pack) throws IOException {
        checkOpen();
        ensureBound();
        if (pack == null) {
            throw new NullPointerException("pack == null");
        } else if (this.pendingConnectException != null) {
            throw new SocketException("Pending connect failure", this.pendingConnectException);
        } else {
            pack.resetLengthForReceive();
            this.impl.receive(pack);
        }
    }

    public void send(DatagramPacket pack) throws IOException {
        checkOpen();
        ensureBound();
        InetAddress packAddr = pack.getAddress();
        if (this.address != null) {
            if (packAddr == null) {
                pack.setAddress(this.address);
                pack.setPort(this.port);
            } else if (!(this.address.equals(packAddr) && this.port == pack.getPort())) {
                throw new IllegalArgumentException("Packet address mismatch with connected address");
            }
        } else if (packAddr == null) {
            throw new NullPointerException("Destination address is null");
        }
        this.impl.send(pack);
    }

    public void setNetworkInterface(NetworkInterface netInterface) throws SocketException {
        if (netInterface == null) {
            throw new NullPointerException("netInterface == null");
        }
        try {
            Libcore.os.setsockoptIfreq(this.impl.fd, OsConstants.SOL_SOCKET, OsConstants.SO_BINDTODEVICE, netInterface.getName());
        } catch (ErrnoException errnoException) {
            throw errnoException.rethrowAsSocketException();
        }
    }

    public synchronized void setSendBufferSize(int size) throws SocketException {
        if (size < 1) {
            throw new IllegalArgumentException("size < 1");
        }
        checkOpen();
        this.impl.setOption(SocketOptions.SO_SNDBUF, Integer.valueOf(size));
    }

    public synchronized void setReceiveBufferSize(int size) throws SocketException {
        if (size < 1) {
            throw new IllegalArgumentException("size < 1");
        }
        checkOpen();
        this.impl.setOption(SocketOptions.SO_RCVBUF, Integer.valueOf(size));
    }

    public synchronized void setSoTimeout(int timeout) throws SocketException {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout < 0");
        }
        checkOpen();
        this.impl.setOption(SocketOptions.SO_TIMEOUT, Integer.valueOf(timeout));
    }

    public static synchronized void setDatagramSocketImplFactory(DatagramSocketImplFactory fac) throws IOException {
        synchronized (DatagramSocket.class) {
            if (factory != null) {
                throw new SocketException("Factory already set");
            }
            factory = fac;
        }
    }

    protected DatagramSocket(DatagramSocketImpl socketImpl) {
        this.port = -1;
        this.isBound = false;
        this.isConnected = false;
        this.isClosed = false;
        this.lock = new Object();
        if (socketImpl == null) {
            throw new NullPointerException("socketImpl == null");
        }
        this.impl = socketImpl;
    }

    public DatagramSocket(SocketAddress localAddr) throws SocketException {
        this.port = -1;
        this.isBound = false;
        this.isConnected = false;
        this.isClosed = false;
        this.lock = new Object();
        if (localAddr != null) {
            if (localAddr instanceof InetSocketAddress) {
                checkPort(((InetSocketAddress) localAddr).getPort());
            } else {
                throw new IllegalArgumentException("Local address not an InetSocketAddress: " + localAddr.getClass());
            }
        }
        this.impl = factory != null ? factory.createDatagramSocketImpl() : new PlainDatagramSocketImpl();
        this.impl.create();
        if (localAddr != null) {
            try {
                bind(localAddr);
            } catch (SocketException e) {
                close();
                throw e;
            }
        }
        setBroadcast(true);
    }

    void checkOpen() throws SocketException {
        if (isClosed()) {
            throw new SocketException("Socket is closed");
        }
    }

    private void ensureBound() throws SocketException {
        if (!isBound()) {
            this.impl.bind(0, Inet4Address.ANY);
            this.isBound = true;
        }
    }

    public void bind(SocketAddress localAddr) throws SocketException {
        int localPort;
        InetAddress addr;
        checkOpen();
        if (localAddr == null) {
            localPort = 0;
            addr = Inet4Address.ANY;
        } else if (localAddr instanceof InetSocketAddress) {
            InetSocketAddress inetAddr = (InetSocketAddress) localAddr;
            addr = inetAddr.getAddress();
            if (addr == null) {
                throw new SocketException("Host is unresolved: " + inetAddr.getHostName());
            }
            localPort = inetAddr.getPort();
            checkPort(localPort);
        } else {
            throw new IllegalArgumentException("Local address not an InetSocketAddress: " + localAddr.getClass());
        }
        this.impl.bind(localPort, addr);
        this.isBound = true;
    }

    public void onBind(InetAddress localAddress, int localPort) {
        this.isBound = true;
        this.impl.onBind(localAddress, localPort);
    }

    public void connect(SocketAddress peer) throws SocketException {
        if (peer == null) {
            throw new IllegalArgumentException("peer == null");
        } else if (peer instanceof InetSocketAddress) {
            InetSocketAddress isa = (InetSocketAddress) peer;
            if (isa.getAddress() == null) {
                throw new SocketException("Host is unresolved: " + isa.getHostName());
            }
            synchronized (this.lock) {
                checkOpen();
                ensureBound();
                this.address = isa.getAddress();
                this.port = isa.getPort();
                this.isConnected = true;
                this.impl.connect(this.address, this.port);
            }
        } else {
            throw new IllegalArgumentException("peer not an InetSocketAddress: " + peer.getClass());
        }
    }

    public void onConnect(InetAddress remoteAddress, int remotePort) {
        this.isConnected = true;
        this.address = remoteAddress;
        this.port = remotePort;
        this.impl.onConnect(remoteAddress, remotePort);
    }

    public void connect(InetAddress address, int port) {
        if (address == null) {
            throw new IllegalArgumentException("address == null");
        }
        try {
            connect(new InetSocketAddress(address, port));
        } catch (SocketException connectException) {
            this.pendingConnectException = connectException;
        }
    }

    public boolean isBound() {
        return this.isBound;
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    public SocketAddress getRemoteSocketAddress() {
        if (isConnected()) {
            return new InetSocketAddress(getInetAddress(), getPort());
        }
        return null;
    }

    public SocketAddress getLocalSocketAddress() {
        if (isClosed() || !isBound()) {
            return null;
        }
        return new InetSocketAddress(getLocalAddress(), getLocalPort());
    }

    public void setReuseAddress(boolean reuse) throws SocketException {
        checkOpen();
        this.impl.setOption(4, Boolean.valueOf(reuse));
    }

    public boolean getReuseAddress() throws SocketException {
        checkOpen();
        return ((Boolean) this.impl.getOption(4)).booleanValue();
    }

    public void setBroadcast(boolean broadcast) throws SocketException {
        checkOpen();
        this.impl.setOption(32, Boolean.valueOf(broadcast));
    }

    public boolean getBroadcast() throws SocketException {
        checkOpen();
        return ((Boolean) this.impl.getOption(32)).booleanValue();
    }

    public void setTrafficClass(int value) throws SocketException {
        checkOpen();
        if (value < 0 || value > Opcodes.OP_CONST_CLASS_JUMBO) {
            throw new IllegalArgumentException("Value doesn't fit in an unsigned byte: " + value);
        }
        this.impl.setOption(3, Integer.valueOf(value));
    }

    public int getTrafficClass() throws SocketException {
        checkOpen();
        return ((Integer) this.impl.getOption(3)).intValue();
    }

    public boolean isClosed() {
        return this.isClosed;
    }

    public DatagramChannel getChannel() {
        return null;
    }

    public final FileDescriptor getFileDescriptor$() {
        return this.impl.fd;
    }
}
