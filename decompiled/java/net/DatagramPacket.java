package java.net;

import com.android.dex.DexFormat;

public final class DatagramPacket {
    private InetAddress address;
    private byte[] data;
    private int length;
    private int offset;
    private int port;
    private int userSuppliedLength;

    public DatagramPacket(byte[] data, int length) {
        this(data, 0, length);
    }

    public DatagramPacket(byte[] data, int offset, int length) {
        this.port = -1;
        this.offset = 0;
        setData(data, offset, length);
    }

    public DatagramPacket(byte[] data, int offset, int length, InetAddress host, int aPort) {
        this(data, offset, length);
        setPort(aPort);
        this.address = host;
    }

    public DatagramPacket(byte[] data, int length, InetAddress host, int port) {
        this(data, 0, length, host, port);
    }

    public synchronized InetAddress getAddress() {
        return this.address;
    }

    public synchronized byte[] getData() {
        return this.data;
    }

    public synchronized int getLength() {
        return this.length;
    }

    public synchronized int getOffset() {
        return this.offset;
    }

    public synchronized int getPort() {
        return this.port;
    }

    public synchronized void setAddress(InetAddress addr) {
        this.address = addr;
    }

    public synchronized void setData(byte[] data, int offset, int byteCount) {
        if ((offset | byteCount) >= 0) {
            if (offset <= data.length && byteCount <= data.length - offset) {
                this.data = data;
                this.offset = offset;
                this.length = byteCount;
                this.userSuppliedLength = byteCount;
            }
        }
        throw new IllegalArgumentException();
    }

    public synchronized void setData(byte[] buf) {
        this.length = buf.length;
        this.userSuppliedLength = this.length;
        this.data = buf;
        this.offset = 0;
    }

    public synchronized void setLength(int length) {
        if (length >= 0) {
            if (this.offset + length <= this.data.length) {
                this.length = length;
                this.userSuppliedLength = length;
            }
        }
        throw new IndexOutOfBoundsException("length=" + length + ", offset=" + this.offset + ", buffer size=" + this.data.length);
    }

    public void resetLengthForReceive() {
        this.length = this.userSuppliedLength;
    }

    public void setReceivedLength(int length) {
        this.length = length;
    }

    public synchronized void setPort(int aPort) {
        if (aPort < 0 || aPort > DexFormat.MAX_TYPE_IDX) {
            throw new IllegalArgumentException("Port out of range: " + aPort);
        }
        this.port = aPort;
    }

    public DatagramPacket(byte[] data, int length, SocketAddress sockAddr) throws SocketException {
        this(data, 0, length);
        setSocketAddress(sockAddr);
    }

    public DatagramPacket(byte[] data, int offset, int length, SocketAddress sockAddr) throws SocketException {
        this(data, offset, length);
        setSocketAddress(sockAddr);
    }

    public synchronized SocketAddress getSocketAddress() {
        return new InetSocketAddress(getAddress(), getPort());
    }

    public synchronized void setSocketAddress(SocketAddress sockAddr) {
        if (sockAddr instanceof InetSocketAddress) {
            InetSocketAddress inetAddr = (InetSocketAddress) sockAddr;
            if (inetAddr.isUnresolved()) {
                throw new IllegalArgumentException("Socket address unresolved: " + sockAddr);
            }
            this.port = inetAddr.getPort();
            this.address = inetAddr.getAddress();
        } else {
            throw new IllegalArgumentException("Socket address not an InetSocketAddress: " + (sockAddr == null ? null : sockAddr.getClass()));
        }
    }
}
