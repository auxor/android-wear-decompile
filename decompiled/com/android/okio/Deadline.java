package com.android.okio;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.TimeUnit;

public class Deadline {
    public static final Deadline NONE;
    private long deadlineNanos;

    static {
        NONE = new Deadline() {
            public Deadline start(long timeout, TimeUnit unit) {
                throw new UnsupportedOperationException();
            }

            public boolean reached() {
                return false;
            }
        };
    }

    public Deadline start(long timeout, TimeUnit unit) {
        this.deadlineNanos = System.nanoTime() + unit.toNanos(timeout);
        return this;
    }

    public boolean reached() {
        return System.nanoTime() - this.deadlineNanos >= 0;
    }

    public final void throwIfReached() throws IOException {
        if (reached()) {
            throw new IOException("Deadline reached");
        } else if (Thread.interrupted()) {
            throw new InterruptedIOException();
        }
    }
}
