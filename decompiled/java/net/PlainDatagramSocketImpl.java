package java.net;

import android.system.OsConstants;
import android.system.StructGroupReq;
import dalvik.bytecode.Opcodes;
import dalvik.system.CloseGuard;
import java.io.FileDescriptor;
import java.io.IOException;
import libcore.io.IoBridge;
import libcore.io.Libcore;
import libcore.util.EmptyArray;

public class PlainDatagramSocketImpl extends DatagramSocketImpl {
    private InetAddress connectedAddress;
    private int connectedPort;
    private final CloseGuard guard;
    private volatile boolean isNativeConnected;

    public PlainDatagramSocketImpl(FileDescriptor fd, int localPort) {
        this.guard = CloseGuard.get();
        this.connectedPort = -1;
        this.fd = fd;
        this.localPort = localPort;
        if (fd.valid()) {
            this.guard.open("close");
        }
    }

    public PlainDatagramSocketImpl() {
        this.guard = CloseGuard.get();
        this.connectedPort = -1;
        this.fd = new FileDescriptor();
    }

    public void bind(int port, InetAddress address) throws SocketException {
        IoBridge.bind(this.fd, address, port);
        if (port != 0) {
            this.localPort = port;
        } else {
            this.localPort = IoBridge.getSocketLocalPort(this.fd);
        }
        try {
            setOption(32, Boolean.TRUE);
        } catch (IOException e) {
        }
    }

    protected void onBind(InetAddress localAddress, int localPort) {
        this.localPort = localPort;
    }

    public synchronized void close() {
        this.guard.close();
        try {
            IoBridge.closeAndSignalBlockedThreads(this.fd);
        } catch (IOException e) {
        }
    }

    protected void onClose() {
        this.guard.close();
    }

    public void create() throws SocketException {
        this.fd = IoBridge.socket(false);
    }

    protected void finalize() throws Throwable {
        try {
            if (this.guard != null) {
                this.guard.warnIfOpen();
            }
            close();
        } finally {
            super.finalize();
        }
    }

    public Object getOption(int option) throws SocketException {
        return IoBridge.getSocketOption(this.fd, option);
    }

    public int getTimeToLive() throws IOException {
        return ((Integer) getOption(17)).intValue();
    }

    public byte getTTL() throws IOException {
        return (byte) getTimeToLive();
    }

    private static StructGroupReq makeGroupReq(InetAddress gr_group, NetworkInterface networkInterface) {
        return new StructGroupReq(networkInterface != null ? networkInterface.getIndex() : 0, gr_group);
    }

    public void join(InetAddress addr) throws IOException {
        setOption(19, makeGroupReq(addr, null));
    }

    public void joinGroup(SocketAddress addr, NetworkInterface netInterface) throws IOException {
        if (addr instanceof InetSocketAddress) {
            setOption(19, makeGroupReq(((InetSocketAddress) addr).getAddress(), netInterface));
        }
    }

    public void leave(InetAddress addr) throws IOException {
        setOption(20, makeGroupReq(addr, null));
    }

    public void leaveGroup(SocketAddress addr, NetworkInterface netInterface) throws IOException {
        if (addr instanceof InetSocketAddress) {
            setOption(20, makeGroupReq(((InetSocketAddress) addr).getAddress(), netInterface));
        }
    }

    protected int peek(InetAddress sender) throws IOException {
        DatagramPacket packet = new DatagramPacket(EmptyArray.BYTE, 0);
        int result = peekData(packet);
        sender.ipaddress = packet.getAddress().getAddress();
        return result;
    }

    private void doRecv(DatagramPacket pack, int flags) throws IOException {
        IoBridge.recvfrom(false, this.fd, pack.getData(), pack.getOffset(), pack.getLength(), flags, pack, this.isNativeConnected);
        if (this.isNativeConnected) {
            updatePacketRecvAddress(pack);
        }
    }

    public void receive(DatagramPacket pack) throws IOException {
        doRecv(pack, 0);
    }

    public int peekData(DatagramPacket pack) throws IOException {
        doRecv(pack, OsConstants.MSG_PEEK);
        return pack.getPort();
    }

    public void send(DatagramPacket packet) throws IOException {
        IoBridge.sendto(this.fd, packet.getData(), packet.getOffset(), packet.getLength(), 0, this.isNativeConnected ? null : packet.getAddress(), this.isNativeConnected ? 0 : packet.getPort());
    }

    public void setOption(int option, Object value) throws SocketException {
        IoBridge.setSocketOption(this.fd, option, value);
    }

    public void setTimeToLive(int ttl) throws IOException {
        setOption(17, Integer.valueOf(ttl));
    }

    public void setTTL(byte ttl) throws IOException {
        setTimeToLive(ttl & Opcodes.OP_CONST_CLASS_JUMBO);
    }

    public void connect(InetAddress inetAddr, int port) throws SocketException {
        IoBridge.connect(this.fd, inetAddr, port);
        try {
            this.connectedAddress = InetAddress.getByAddress(inetAddr.getAddress());
            this.connectedPort = port;
            this.isNativeConnected = true;
        } catch (UnknownHostException e) {
            throw new SocketException("Host is unresolved: " + inetAddr.getHostName());
        }
    }

    protected void onConnect(InetAddress remoteAddress, int remotePort) {
        this.isNativeConnected = true;
        this.connectedAddress = remoteAddress;
        this.connectedPort = remotePort;
    }

    public void disconnect() {
        try {
            Libcore.os.connect(this.fd, InetAddress.UNSPECIFIED, 0);
        } catch (Object errnoException) {
            throw new AssertionError(errnoException);
        } catch (SocketException e) {
        }
        this.connectedPort = -1;
        this.connectedAddress = null;
        this.isNativeConnected = false;
    }

    protected void onDisconnect() {
        this.connectedPort = -1;
        this.connectedAddress = null;
        this.isNativeConnected = false;
    }

    private void updatePacketRecvAddress(DatagramPacket packet) {
        packet.setAddress(this.connectedAddress);
        packet.setPort(this.connectedPort);
    }
}
