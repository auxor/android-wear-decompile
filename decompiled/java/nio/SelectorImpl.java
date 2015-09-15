package java.nio;

import android.system.ErrnoException;
import android.system.OsConstants;
import android.system.StructPollfd;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.IllegalSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.nio.channels.spi.AbstractSelectionKey;
import java.nio.channels.spi.AbstractSelector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UnsafeArrayList;
import libcore.io.IoBridge;
import libcore.io.IoUtils;
import libcore.io.Libcore;

final class SelectorImpl extends AbstractSelector {
    final Object keysLock;
    private final Set<SelectionKeyImpl> mutableKeys;
    private final Set<SelectionKey> mutableSelectedKeys;
    private final UnsafeArrayList<StructPollfd> pollFds;
    private final Set<SelectionKey> selectedKeys;
    private final Set<SelectionKey> unmodifiableKeys;
    private final FileDescriptor wakeupIn;
    private final FileDescriptor wakeupOut;

    private static class UnaddableSet<E> implements Set<E> {
        private final Set<E> set;

        UnaddableSet(Set<E> set) {
            this.set = set;
        }

        public boolean equals(Object object) {
            return this.set.equals(object);
        }

        public int hashCode() {
            return this.set.hashCode();
        }

        public boolean add(E e) {
            throw new UnsupportedOperationException();
        }

        public boolean addAll(Collection<? extends E> collection) {
            throw new UnsupportedOperationException();
        }

        public void clear() {
            this.set.clear();
        }

        public boolean contains(Object object) {
            return this.set.contains(object);
        }

        public boolean containsAll(Collection<?> c) {
            return this.set.containsAll(c);
        }

        public boolean isEmpty() {
            return this.set.isEmpty();
        }

        public Iterator<E> iterator() {
            return this.set.iterator();
        }

        public boolean remove(Object object) {
            return this.set.remove(object);
        }

        public boolean removeAll(Collection<?> c) {
            return this.set.removeAll(c);
        }

        public boolean retainAll(Collection<?> c) {
            return this.set.retainAll(c);
        }

        public int size() {
            return this.set.size();
        }

        public Object[] toArray() {
            return this.set.toArray();
        }

        public <T> T[] toArray(T[] a) {
            return this.set.toArray(a);
        }
    }

    public SelectorImpl(SelectorProvider selectorProvider) throws IOException {
        super(selectorProvider);
        this.keysLock = new Object();
        this.mutableKeys = new HashSet();
        this.unmodifiableKeys = Collections.unmodifiableSet(this.mutableKeys);
        this.mutableSelectedKeys = new HashSet();
        this.selectedKeys = new UnaddableSet(this.mutableSelectedKeys);
        this.pollFds = new UnsafeArrayList(StructPollfd.class, 8);
        try {
            FileDescriptor[] pipeFds = Libcore.os.pipe();
            this.wakeupIn = pipeFds[0];
            this.wakeupOut = pipeFds[1];
            IoUtils.setBlocking(this.wakeupIn, false);
            this.pollFds.add(new StructPollfd());
            setPollFd(0, this.wakeupIn, OsConstants.POLLIN, null);
        } catch (ErrnoException errnoException) {
            throw errnoException.rethrowAsIOException();
        }
    }

    protected void implCloseSelector() throws IOException {
        wakeup();
        synchronized (this) {
            synchronized (this.unmodifiableKeys) {
                synchronized (this.selectedKeys) {
                    IoUtils.close(this.wakeupIn);
                    IoUtils.close(this.wakeupOut);
                    doCancel();
                    for (SelectionKey sk : this.mutableKeys) {
                        deregister((AbstractSelectionKey) sk);
                    }
                }
            }
        }
    }

    protected SelectionKey register(AbstractSelectableChannel channel, int operations, Object attachment) {
        if (provider().equals(channel.provider())) {
            SelectionKeyImpl selectionKey;
            synchronized (this) {
                synchronized (this.unmodifiableKeys) {
                    selectionKey = new SelectionKeyImpl(channel, operations, attachment, this);
                    this.mutableKeys.add(selectionKey);
                    ensurePollFdsCapacity();
                }
            }
            return selectionKey;
        }
        throw new IllegalSelectorException();
    }

    public synchronized Set<SelectionKey> keys() {
        checkClosed();
        return this.unmodifiableKeys;
    }

    private void checkClosed() {
        if (!isOpen()) {
            throw new ClosedSelectorException();
        }
    }

    public int select() throws IOException {
        return selectInternal(-1);
    }

    public int select(long timeout) throws IOException {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout < 0: " + timeout);
        }
        if (timeout == 0) {
            timeout = -1;
        }
        return selectInternal(timeout);
    }

    public int selectNow() throws IOException {
        return selectInternal(0);
    }

    private int selectInternal(long timeout) throws IOException {
        int readyCount = 0;
        checkClosed();
        synchronized (this) {
            synchronized (this.unmodifiableKeys) {
                synchronized (this.selectedKeys) {
                    boolean isBlocking;
                    doCancel();
                    if (timeout != 0) {
                        isBlocking = true;
                    } else {
                        isBlocking = false;
                    }
                    synchronized (this.keysLock) {
                        preparePollFds();
                    }
                    int rc = -1;
                    if (isBlocking) {
                        try {
                            begin();
                        } catch (ErrnoException errnoException) {
                            if (errnoException.errno != OsConstants.EINTR) {
                                throw errnoException.rethrowAsIOException();
                            }
                        } catch (Throwable th) {
                            if (isBlocking) {
                                end();
                            }
                        }
                    }
                    rc = Libcore.os.poll((StructPollfd[]) this.pollFds.array(), (int) timeout);
                    if (isBlocking) {
                        end();
                    }
                    if (rc > 0) {
                        readyCount = processPollFds();
                    }
                    readyCount -= doCancel();
                }
            }
        }
        return readyCount;
    }

    private void setPollFd(int i, FileDescriptor fd, int events, Object object) {
        StructPollfd pollFd = (StructPollfd) this.pollFds.get(i);
        pollFd.fd = fd;
        pollFd.events = (short) events;
        pollFd.userData = object;
    }

    private void preparePollFds() {
        int i = 1;
        for (SelectionKeyImpl key : this.mutableKeys) {
            int interestOps = key.interestOpsNoCheck();
            short eventMask = (short) 0;
            if ((interestOps & 17) != 0) {
                eventMask = (short) (OsConstants.POLLIN | 0);
            }
            if ((interestOps & 12) != 0) {
                eventMask = (short) (OsConstants.POLLOUT | eventMask);
            }
            if (eventMask != (short) 0) {
                int i2 = i + 1;
                setPollFd(i, ((FileDescriptorChannel) key.channel()).getFD(), eventMask, key);
                i = i2;
            }
        }
    }

    private void ensurePollFdsCapacity() {
        while (this.pollFds.size() < this.mutableKeys.size() + 1) {
            this.pollFds.add(new StructPollfd());
        }
    }

    private int processPollFds() throws IOException {
        if (((StructPollfd) this.pollFds.get(0)).revents == OsConstants.POLLIN) {
            do {
            } while (IoBridge.read(this.wakeupIn, new byte[8], 0, 1) > 0);
        }
        int readyKeyCount = 0;
        for (int i = 1; i < this.pollFds.size(); i++) {
            StructPollfd pollFd = (StructPollfd) this.pollFds.get(i);
            if (pollFd.revents != (short) 0) {
                if (pollFd.fd == null) {
                    break;
                }
                SelectionKeyImpl key = pollFd.userData;
                pollFd.fd = null;
                pollFd.userData = null;
                int ops = key.interestOpsNoCheck();
                int selectedOps = 0;
                if (!((pollFd.revents & OsConstants.POLLHUP) == 0 && (pollFd.revents & OsConstants.POLLERR) == 0)) {
                    selectedOps = 0 | ops;
                }
                if ((pollFd.revents & OsConstants.POLLIN) != 0) {
                    selectedOps |= ops & 17;
                }
                if ((pollFd.revents & OsConstants.POLLOUT) != 0) {
                    if (key.isConnected()) {
                        selectedOps |= ops & 4;
                    } else {
                        selectedOps |= ops & 8;
                    }
                }
                if (selectedOps != 0) {
                    boolean wasSelected = this.mutableSelectedKeys.contains(key);
                    if (wasSelected && key.readyOps() != selectedOps) {
                        key.setReadyOps(key.readyOps() | selectedOps);
                        readyKeyCount++;
                    } else if (!wasSelected) {
                        key.setReadyOps(selectedOps);
                        this.mutableSelectedKeys.add(key);
                        readyKeyCount++;
                    }
                }
            }
        }
        return readyKeyCount;
    }

    public synchronized Set<SelectionKey> selectedKeys() {
        checkClosed();
        return this.selectedKeys;
    }

    private int doCancel() {
        int deselected = 0;
        Set<SelectionKey> cancelledKeys = cancelledKeys();
        synchronized (cancelledKeys) {
            if (cancelledKeys.size() > 0) {
                for (SelectionKey currentKey : cancelledKeys) {
                    this.mutableKeys.remove(currentKey);
                    deregister((AbstractSelectionKey) currentKey);
                    if (this.mutableSelectedKeys.remove(currentKey)) {
                        deselected++;
                    }
                }
                cancelledKeys.clear();
            }
        }
        return deselected;
    }

    public Selector wakeup() {
        try {
            Libcore.os.write(this.wakeupOut, new byte[]{(byte) 1}, 0, 1);
        } catch (ErrnoException e) {
        } catch (InterruptedIOException e2) {
        }
        return this;
    }
}
