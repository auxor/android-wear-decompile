package java.nio.channels.spi;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractSelector extends Selector {
    private final Set<SelectionKey> cancelledKeysSet;
    private final AtomicBoolean isOpen;
    private SelectorProvider provider;
    private final Runnable wakeupRunnable;

    protected abstract void implCloseSelector() throws IOException;

    protected abstract SelectionKey register(AbstractSelectableChannel abstractSelectableChannel, int i, Object obj);

    protected AbstractSelector(SelectorProvider selectorProvider) {
        this.isOpen = new AtomicBoolean(true);
        this.provider = null;
        this.cancelledKeysSet = new HashSet();
        this.wakeupRunnable = new Runnable() {
            public void run() {
                AbstractSelector.this.wakeup();
            }
        };
        this.provider = selectorProvider;
    }

    public final void close() throws IOException {
        if (this.isOpen.getAndSet(false)) {
            implCloseSelector();
        }
    }

    public final boolean isOpen() {
        return this.isOpen.get();
    }

    public final SelectorProvider provider() {
        return this.provider;
    }

    protected final Set<SelectionKey> cancelledKeys() {
        return this.cancelledKeysSet;
    }

    protected final void deregister(AbstractSelectionKey key) {
        ((AbstractSelectableChannel) key.channel()).deregister(key);
        key.isValid = false;
    }

    protected final void begin() {
        Thread.currentThread().pushInterruptAction$(this.wakeupRunnable);
    }

    protected final void end() {
        Thread.currentThread().popInterruptAction$(this.wakeupRunnable);
    }

    void cancel(SelectionKey key) {
        synchronized (this.cancelledKeysSet) {
            this.cancelledKeysSet.add(key);
        }
    }
}
