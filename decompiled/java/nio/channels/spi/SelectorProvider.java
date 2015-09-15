package java.nio.channels.spi;

import java.io.IOException;
import java.nio.SelectorProviderImpl;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Pipe;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.ServiceLoader;

public abstract class SelectorProvider {
    private static SelectorProvider provider;

    public abstract DatagramChannel openDatagramChannel() throws IOException;

    public abstract Pipe openPipe() throws IOException;

    public abstract AbstractSelector openSelector() throws IOException;

    public abstract ServerSocketChannel openServerSocketChannel() throws IOException;

    public abstract SocketChannel openSocketChannel() throws IOException;

    static {
        provider = null;
    }

    protected SelectorProvider() {
    }

    public static synchronized SelectorProvider provider() {
        SelectorProvider selectorProvider;
        synchronized (SelectorProvider.class) {
            if (provider == null) {
                provider = (SelectorProvider) ServiceLoader.loadFromSystemProperty(SelectorProvider.class);
                if (provider == null) {
                    provider = loadProviderByJar();
                }
                if (provider == null) {
                    provider = new SelectorProviderImpl();
                }
            }
            selectorProvider = provider;
        }
        return selectorProvider;
    }

    private static SelectorProvider loadProviderByJar() {
        Iterator i$ = ServiceLoader.load(SelectorProvider.class).iterator();
        if (i$.hasNext()) {
            return (SelectorProvider) i$.next();
        }
        return null;
    }

    public Channel inheritedChannel() throws IOException {
        return null;
    }
}
