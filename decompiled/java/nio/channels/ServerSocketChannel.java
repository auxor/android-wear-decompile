package java.nio.channels;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.SelectorProvider;

public abstract class ServerSocketChannel extends AbstractSelectableChannel {
    public abstract SocketChannel accept() throws IOException;

    public abstract ServerSocket socket();

    protected ServerSocketChannel(SelectorProvider selectorProvider) {
        super(selectorProvider);
    }

    public static ServerSocketChannel open() throws IOException {
        return SelectorProvider.provider().openServerSocketChannel();
    }

    public final int validOps() {
        return 16;
    }
}
