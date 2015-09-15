package java.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class SocketImpl implements SocketOptions {
    protected InetAddress address;
    protected FileDescriptor fd;
    protected int localport;
    protected int port;

    protected abstract void accept(SocketImpl socketImpl) throws IOException;

    protected abstract int available() throws IOException;

    protected abstract void bind(InetAddress inetAddress, int i) throws IOException;

    protected abstract void close() throws IOException;

    protected abstract void connect(String str, int i) throws IOException;

    protected abstract void connect(InetAddress inetAddress, int i) throws IOException;

    protected abstract void connect(SocketAddress socketAddress, int i) throws IOException;

    protected abstract void create(boolean z) throws IOException;

    protected abstract InputStream getInputStream() throws IOException;

    protected abstract OutputStream getOutputStream() throws IOException;

    protected abstract void listen(int i) throws IOException;

    protected abstract void sendUrgentData(int i) throws IOException;

    protected FileDescriptor getFileDescriptor() {
        return this.fd;
    }

    public FileDescriptor getFD$() {
        return this.fd;
    }

    protected InetAddress getInetAddress() {
        return this.address;
    }

    protected int getLocalPort() {
        return this.localport;
    }

    protected int getPort() {
        return this.port;
    }

    public String toString() {
        return "Socket[address=" + getInetAddress() + ",port=" + this.port + ",localPort=" + getLocalPort() + "]";
    }

    protected void shutdownInput() throws IOException {
        throw new IOException("Method has not been implemented");
    }

    protected void shutdownOutput() throws IOException {
        throw new IOException("Method has not been implemented");
    }

    protected boolean supportsUrgentData() {
        return false;
    }

    protected void setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
    }

    public void onBind(InetAddress localAddress, int localPort) {
    }

    public void onConnect(InetAddress remoteAddress, int remotePort) {
    }

    public void onClose() {
    }
}
