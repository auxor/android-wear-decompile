package java.nio;

import android.system.ErrnoException;
import android.system.OsConstants;
import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import libcore.io.IoUtils;

final class ServerSocketChannelImpl extends ServerSocketChannel implements FileDescriptorChannel {
    private final Object acceptLock;
    private final ServerSocketAdapter socket;

    private static class ServerSocketAdapter extends ServerSocket {
        private final ServerSocketChannelImpl channelImpl;

        ServerSocketAdapter(ServerSocketChannelImpl aChannelImpl) throws IOException {
            this.channelImpl = aChannelImpl;
        }

        public Socket accept() throws IOException {
            if (isBound()) {
                SocketChannel sc = this.channelImpl.accept();
                if (sc != null) {
                    return sc.socket();
                }
                throw new IllegalBlockingModeException();
            }
            throw new IllegalBlockingModeException();
        }

        public Socket implAccept(SocketChannelImpl clientSocketChannel) throws IOException {
            Socket clientSocket = clientSocketChannel.socket();
            boolean connectOK = false;
            try {
                synchronized (this) {
                    super.implAccept(clientSocket);
                    clientSocketChannel.onAccept(new InetSocketAddress(clientSocket.getInetAddress(), clientSocket.getPort()), false);
                }
                connectOK = true;
                return clientSocket;
            } finally {
                if (!connectOK) {
                    clientSocket.close();
                }
            }
        }

        public ServerSocketChannel getChannel() {
            return this.channelImpl;
        }

        public void close() throws IOException {
            synchronized (this.channelImpl) {
                super.close();
                if (this.channelImpl.isOpen()) {
                    this.channelImpl.close();
                }
            }
        }

        private FileDescriptor getFD$() {
            return super.getImpl$().getFD$();
        }
    }

    public ServerSocketChannelImpl(SelectorProvider sp) throws IOException {
        super(sp);
        this.acceptLock = new Object();
        this.socket = new ServerSocketAdapter(this);
    }

    public ServerSocket socket() {
        return this.socket;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.nio.channels.SocketChannel accept() throws java.io.IOException {
        /*
        r4 = this;
        r2 = r4.isOpen();
        if (r2 != 0) goto L_0x000c;
    L_0x0006:
        r2 = new java.nio.channels.ClosedChannelException;
        r2.<init>();
        throw r2;
    L_0x000c:
        r2 = r4.socket;
        r2 = r2.isBound();
        if (r2 != 0) goto L_0x001a;
    L_0x0014:
        r2 = new java.nio.channels.NotYetBoundException;
        r2.<init>();
        throw r2;
    L_0x001a:
        r1 = new java.nio.SocketChannelImpl;
        r2 = r4.provider();
        r3 = 0;
        r1.<init>(r2, r3);
        r4.begin();	 Catch:{ all -> 0x0049 }
        r3 = r4.acceptLock;	 Catch:{ all -> 0x0049 }
        monitor-enter(r3);	 Catch:{ all -> 0x0049 }
        r2 = r4.socket;	 Catch:{ SocketTimeoutException -> 0x003e }
        r2.implAccept(r1);	 Catch:{ SocketTimeoutException -> 0x003e }
    L_0x002f:
        monitor-exit(r3);	 Catch:{ all -> 0x0046 }
        r2 = r1.isConnected();
        r4.end(r2);
        r2 = r1.isConnected();
        if (r2 == 0) goto L_0x0052;
    L_0x003d:
        return r1;
    L_0x003e:
        r0 = move-exception;
        r2 = r4.shouldThrowSocketTimeoutExceptionFromAccept(r0);	 Catch:{ all -> 0x0046 }
        if (r2 == 0) goto L_0x002f;
    L_0x0045:
        throw r0;	 Catch:{ all -> 0x0046 }
    L_0x0046:
        r2 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x0046 }
        throw r2;	 Catch:{ all -> 0x0049 }
    L_0x0049:
        r2 = move-exception;
        r3 = r1.isConnected();
        r4.end(r3);
        throw r2;
    L_0x0052:
        r1 = 0;
        goto L_0x003d;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.nio.ServerSocketChannelImpl.accept():java.nio.channels.SocketChannel");
    }

    private boolean shouldThrowSocketTimeoutExceptionFromAccept(SocketTimeoutException e) {
        if (isBlocking()) {
            return true;
        }
        Throwable cause = e.getCause();
        if ((cause instanceof ErrnoException) && ((ErrnoException) cause).errno == OsConstants.EAGAIN) {
            return false;
        }
        return true;
    }

    protected void implConfigureBlocking(boolean blocking) throws IOException {
        IoUtils.setBlocking(this.socket.getFD$(), blocking);
    }

    protected synchronized void implCloseSelectableChannel() throws IOException {
        if (!this.socket.isClosed()) {
            this.socket.close();
        }
    }

    public FileDescriptor getFD() {
        return this.socket.getFD$();
    }
}
