package java.net;

import android.system.ErrnoException;
import android.system.OsConstants;
import dalvik.system.CloseGuard;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy.Type;
import java.nio.ByteOrder;
import java.util.Arrays;
import libcore.io.IoBridge;
import libcore.io.Libcore;
import libcore.io.Memory;
import libcore.io.Streams;

public class PlainSocketImpl extends SocketImpl {
    private static InetAddress lastConnectedAddress;
    private static int lastConnectedPort;
    private final CloseGuard guard;
    private Proxy proxy;
    private boolean shutdownInput;
    private boolean streaming;

    private static class PlainSocketInputStream extends InputStream {
        private final PlainSocketImpl socketImpl;

        public PlainSocketInputStream(PlainSocketImpl socketImpl) {
            this.socketImpl = socketImpl;
        }

        public int available() throws IOException {
            return this.socketImpl.available();
        }

        public void close() throws IOException {
            this.socketImpl.close();
        }

        public int read() throws IOException {
            return Streams.readSingleByte(this);
        }

        public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
            return this.socketImpl.read(buffer, byteOffset, byteCount);
        }
    }

    private static class PlainSocketOutputStream extends OutputStream {
        private final PlainSocketImpl socketImpl;

        public PlainSocketOutputStream(PlainSocketImpl socketImpl) {
            this.socketImpl = socketImpl;
        }

        public void close() throws IOException {
            this.socketImpl.close();
        }

        public void write(int oneByte) throws IOException {
            Streams.writeSingleByte(this, oneByte);
        }

        public void write(byte[] buffer, int offset, int byteCount) throws IOException {
            this.socketImpl.write(buffer, offset, byteCount);
        }
    }

    public PlainSocketImpl(FileDescriptor fd) {
        this.streaming = true;
        this.guard = CloseGuard.get();
        this.fd = fd;
        if (fd.valid()) {
            this.guard.open("close");
        }
    }

    public PlainSocketImpl(Proxy proxy) {
        this(new FileDescriptor());
        this.proxy = proxy;
    }

    public PlainSocketImpl() {
        this(new FileDescriptor());
    }

    public PlainSocketImpl(FileDescriptor fd, int localport, InetAddress addr, int port) {
        this.streaming = true;
        this.guard = CloseGuard.get();
        this.fd = fd;
        this.localport = localport;
        this.address = addr;
        this.port = port;
        if (fd.valid()) {
            this.guard.open("close");
        }
    }

    protected void accept(SocketImpl newImpl) throws IOException {
        if (usingSocks()) {
            ((PlainSocketImpl) newImpl).socksBind();
            ((PlainSocketImpl) newImpl).socksAccept();
            return;
        }
        try {
            InetSocketAddress peerAddress = new InetSocketAddress();
            newImpl.fd.setInt$(Libcore.os.accept(this.fd, peerAddress).getInt$());
            newImpl.address = peerAddress.getAddress();
            newImpl.port = peerAddress.getPort();
            newImpl.setOption(SocketOptions.SO_TIMEOUT, Integer.valueOf(0));
            newImpl.localport = IoBridge.getSocketLocalPort(newImpl.fd);
        } catch (Throwable errnoException) {
            if (errnoException.errno == OsConstants.EAGAIN) {
                throw new SocketTimeoutException(errnoException);
            }
            throw errnoException.rethrowAsSocketException();
        }
    }

    private boolean usingSocks() {
        return this.proxy != null && this.proxy.type() == Type.SOCKS;
    }

    private void checkNotClosed() throws IOException {
        if (!this.fd.valid()) {
            throw new SocketException("Socket is closed");
        }
    }

    protected synchronized int available() throws IOException {
        int i;
        checkNotClosed();
        if (this.shutdownInput) {
            i = 0;
        } else {
            i = IoBridge.available(this.fd);
        }
        return i;
    }

    protected void bind(InetAddress address, int port) throws IOException {
        IoBridge.bind(this.fd, address, port);
        if (port != 0) {
            this.localport = port;
        } else {
            this.localport = IoBridge.getSocketLocalPort(this.fd);
        }
    }

    public void onBind(InetAddress localAddress, int localPort) {
        this.localport = localPort;
    }

    protected synchronized void close() throws IOException {
        this.guard.close();
        IoBridge.closeAndSignalBlockedThreads(this.fd);
    }

    public void onClose() {
        this.guard.close();
    }

    protected void connect(String aHost, int aPort) throws IOException {
        connect(InetAddress.getByName(aHost), aPort);
    }

    protected void connect(InetAddress anAddr, int aPort) throws IOException {
        connect(anAddr, aPort, 0);
    }

    private void connect(InetAddress anAddr, int aPort, int timeout) throws IOException {
        InetAddress normalAddr;
        if (anAddr.isAnyLocalAddress()) {
            normalAddr = InetAddress.getLocalHost();
        } else {
            normalAddr = anAddr;
        }
        if (this.streaming && usingSocks()) {
            socksConnect(anAddr, aPort, 0);
        } else {
            IoBridge.connect(this.fd, normalAddr, aPort, timeout);
        }
        this.address = normalAddr;
        this.port = aPort;
    }

    public void onConnect(InetAddress remoteAddress, int remotePort) {
        this.address = remoteAddress;
        this.port = remotePort;
    }

    protected void create(boolean streaming) throws IOException {
        this.streaming = streaming;
        this.fd = IoBridge.socket(streaming);
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

    protected synchronized InputStream getInputStream() throws IOException {
        checkNotClosed();
        return new PlainSocketInputStream(this);
    }

    public Object getOption(int option) throws SocketException {
        return IoBridge.getSocketOption(this.fd, option);
    }

    protected synchronized OutputStream getOutputStream() throws IOException {
        checkNotClosed();
        return new PlainSocketOutputStream(this);
    }

    protected void listen(int backlog) throws IOException {
        if (!usingSocks()) {
            try {
                Libcore.os.listen(this.fd, backlog);
            } catch (ErrnoException errnoException) {
                throw errnoException.rethrowAsSocketException();
            }
        }
    }

    public void setOption(int option, Object value) throws SocketException {
        IoBridge.setSocketOption(this.fd, option, value);
    }

    private int socksGetServerPort() {
        return ((InetSocketAddress) this.proxy.address()).getPort();
    }

    private InetAddress socksGetServerAddress() throws UnknownHostException {
        InetSocketAddress addr = (InetSocketAddress) this.proxy.address();
        String proxyName = addr.getHostName();
        if (proxyName == null) {
            proxyName = addr.getAddress().getHostAddress();
        }
        return InetAddress.getByName(proxyName);
    }

    private void socksConnect(InetAddress applicationServerAddress, int applicationServerPort, int timeout) throws IOException {
        try {
            IoBridge.connect(this.fd, socksGetServerAddress(), socksGetServerPort(), timeout);
            socksRequestConnection(applicationServerAddress, applicationServerPort);
            lastConnectedAddress = applicationServerAddress;
            lastConnectedPort = applicationServerPort;
        } catch (Exception e) {
            throw new SocketException("SOCKS connection failed", e);
        }
    }

    private void socksRequestConnection(InetAddress applicationServerAddress, int applicationServerPort) throws IOException {
        socksSendRequest(1, applicationServerAddress, applicationServerPort);
        Socks4Message reply = socksReadReply();
        if (reply.getCommandOrResult() != 90) {
            throw new IOException(reply.getErrorString(reply.getCommandOrResult()));
        }
    }

    public void socksAccept() throws IOException {
        Socks4Message reply = socksReadReply();
        if (reply.getCommandOrResult() != 90) {
            throw new IOException(reply.getErrorString(reply.getCommandOrResult()));
        }
    }

    protected void shutdownInput() throws IOException {
        this.shutdownInput = true;
        try {
            Libcore.os.shutdown(this.fd, OsConstants.SHUT_RD);
        } catch (ErrnoException errnoException) {
            throw errnoException.rethrowAsSocketException();
        }
    }

    protected void shutdownOutput() throws IOException {
        try {
            Libcore.os.shutdown(this.fd, OsConstants.SHUT_WR);
        } catch (ErrnoException errnoException) {
            throw errnoException.rethrowAsSocketException();
        }
    }

    private void socksBind() throws IOException {
        try {
            IoBridge.connect(this.fd, socksGetServerAddress(), socksGetServerPort());
            if (lastConnectedAddress == null) {
                throw new SocketException("Invalid SOCKS client");
            }
            socksSendRequest(2, lastConnectedAddress, lastConnectedPort);
            Socks4Message reply = socksReadReply();
            if (reply.getCommandOrResult() != 90) {
                throw new IOException(reply.getErrorString(reply.getCommandOrResult()));
            }
            if (reply.getIP() == 0) {
                this.address = socksGetServerAddress();
            } else {
                byte[] replyBytes = new byte[4];
                Memory.pokeInt(replyBytes, 0, reply.getIP(), ByteOrder.BIG_ENDIAN);
                this.address = InetAddress.getByAddress(replyBytes);
            }
            this.localport = reply.getPort();
        } catch (Exception e) {
            throw new IOException("Unable to connect to SOCKS server", e);
        }
    }

    private void socksSendRequest(int command, InetAddress address, int port) throws IOException {
        Socks4Message request = new Socks4Message();
        request.setCommandOrResult(command);
        request.setPort(port);
        request.setIP(address.getAddress());
        request.setUserId("default");
        getOutputStream().write(request.getBytes(), 0, request.getLength());
    }

    private Socks4Message socksReadReply() throws IOException {
        Socks4Message reply = new Socks4Message();
        int bytesRead = 0;
        while (bytesRead < 8) {
            int count = getInputStream().read(reply.getBytes(), bytesRead, 8 - bytesRead);
            if (count == -1) {
                break;
            }
            bytesRead += count;
        }
        if (8 == bytesRead) {
            return reply;
        }
        throw new SocketException("Malformed reply from SOCKS server");
    }

    protected void connect(SocketAddress remoteAddr, int timeout) throws IOException {
        InetSocketAddress inetAddr = (InetSocketAddress) remoteAddr;
        connect(inetAddr.getAddress(), inetAddr.getPort(), timeout);
    }

    protected boolean supportsUrgentData() {
        return true;
    }

    protected void sendUrgentData(int value) throws IOException {
        try {
            Libcore.os.sendto(this.fd, new byte[]{(byte) value}, 0, 1, OsConstants.MSG_OOB, null, 0);
        } catch (ErrnoException errnoException) {
            throw errnoException.rethrowAsSocketException();
        }
    }

    private int read(byte[] buffer, int offset, int byteCount) throws IOException {
        if (byteCount == 0) {
            return 0;
        }
        Arrays.checkOffsetAndCount(buffer.length, offset, byteCount);
        if (this.shutdownInput) {
            return -1;
        }
        int readCount = IoBridge.recvfrom(true, this.fd, buffer, offset, byteCount, 0, null, false);
        if (readCount == 0) {
            throw new SocketTimeoutException();
        }
        if (readCount == -1) {
            this.shutdownInput = true;
        }
        return readCount;
    }

    private void write(byte[] buffer, int offset, int byteCount) throws IOException {
        Arrays.checkOffsetAndCount(buffer.length, offset, byteCount);
        if (this.streaming) {
            while (byteCount > 0) {
                int bytesWritten = IoBridge.sendto(this.fd, buffer, offset, byteCount, 0, null, 0);
                byteCount -= bytesWritten;
                offset += bytesWritten;
            }
            return;
        }
        IoBridge.sendto(this.fd, buffer, offset, byteCount, 0, this.address, this.port);
    }
}
