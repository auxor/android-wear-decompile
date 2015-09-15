package java.nio.channels.spi;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.IllegalSelectorException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSelectableChannel extends SelectableChannel {
    private final Object blockingLock;
    boolean isBlocking;
    private List<SelectionKey> keyList;
    private final SelectorProvider provider;

    protected abstract void implCloseSelectableChannel() throws IOException;

    protected abstract void implConfigureBlocking(boolean z) throws IOException;

    protected AbstractSelectableChannel(SelectorProvider selectorProvider) {
        this.keyList = new ArrayList();
        this.blockingLock = new Object();
        this.isBlocking = true;
        this.provider = selectorProvider;
    }

    public final SelectorProvider provider() {
        return this.provider;
    }

    public final synchronized boolean isRegistered() {
        return !this.keyList.isEmpty();
    }

    public final synchronized SelectionKey keyFor(Selector selector) {
        SelectionKey key;
        for (SelectionKey key2 : this.keyList) {
            if (key2 != null && key2.selector() == selector) {
                break;
            }
        }
        key2 = null;
        return key2;
    }

    public final SelectionKey register(Selector selector, int interestSet, Object attachment) throws ClosedChannelException {
        if (!isOpen()) {
            throw new ClosedChannelException();
        } else if (((validOps() ^ -1) & interestSet) != 0) {
            throw new IllegalArgumentException("no valid ops in interest set: " + interestSet);
        } else {
            SelectionKey key;
            synchronized (this.blockingLock) {
                if (this.isBlocking) {
                    throw new IllegalBlockingModeException();
                } else if (selector.isOpen()) {
                    key = keyFor(selector);
                    if (key == null) {
                        key = ((AbstractSelector) selector).register(this, interestSet, attachment);
                        this.keyList.add(key);
                    } else if (key.isValid()) {
                        key.interestOps(interestSet);
                        key.attach(attachment);
                    } else {
                        throw new CancelledKeyException();
                    }
                } else if (interestSet == 0) {
                    throw new IllegalSelectorException();
                } else {
                    throw new NullPointerException("selector not open");
                }
            }
            return key;
        }
    }

    protected final synchronized void implCloseChannel() throws IOException {
        implCloseSelectableChannel();
        for (SelectionKey key : this.keyList) {
            if (key != null) {
                key.cancel();
            }
        }
    }

    public final boolean isBlocking() {
        boolean z;
        synchronized (this.blockingLock) {
            z = this.isBlocking;
        }
        return z;
    }

    public final Object blockingLock() {
        return this.blockingLock;
    }

    public final SelectableChannel configureBlocking(boolean blockingMode) throws IOException {
        if (isOpen()) {
            synchronized (this.blockingLock) {
                if (this.isBlocking == blockingMode) {
                } else if (blockingMode && containsValidKeys()) {
                    throw new IllegalBlockingModeException();
                } else {
                    implConfigureBlocking(blockingMode);
                    this.isBlocking = blockingMode;
                }
            }
            return this;
        }
        throw new ClosedChannelException();
    }

    synchronized void deregister(SelectionKey k) {
        if (this.keyList != null) {
            this.keyList.remove((Object) k);
        }
    }

    private synchronized boolean containsValidKeys() {
        boolean z;
        for (SelectionKey key : this.keyList) {
            if (key != null && key.isValid()) {
                z = true;
                break;
            }
        }
        z = false;
        return z;
    }
}
