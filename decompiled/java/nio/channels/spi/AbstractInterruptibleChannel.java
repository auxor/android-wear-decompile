package java.nio.channels.spi;

import java.io.IOException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.Channel;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.InterruptibleChannel;

public abstract class AbstractInterruptibleChannel implements Channel, InterruptibleChannel {
    private volatile boolean closed;
    private final Runnable interruptAndCloseRunnable;
    volatile boolean interrupted;

    protected abstract void implCloseChannel() throws IOException;

    protected AbstractInterruptibleChannel() {
        this.closed = false;
        this.interrupted = false;
        this.interruptAndCloseRunnable = new Runnable() {
            public void run() {
                try {
                    AbstractInterruptibleChannel.this.interrupted = true;
                    AbstractInterruptibleChannel.this.close();
                } catch (IOException e) {
                }
            }
        };
    }

    public final synchronized boolean isOpen() {
        return !this.closed;
    }

    public final void close() throws IOException {
        if (!this.closed) {
            synchronized (this) {
                if (!this.closed) {
                    this.closed = true;
                    implCloseChannel();
                }
            }
        }
    }

    protected final void begin() {
        Thread.currentThread().pushInterruptAction$(this.interruptAndCloseRunnable);
    }

    protected final void end(boolean success) throws AsynchronousCloseException {
        Thread.currentThread().popInterruptAction$(this.interruptAndCloseRunnable);
        if (this.interrupted) {
            this.interrupted = false;
            throw new ClosedByInterruptException();
        } else if (!success && this.closed) {
            throw new AsynchronousCloseException();
        }
    }
}
