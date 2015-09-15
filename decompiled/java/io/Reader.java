package java.io;

import java.nio.CharBuffer;
import org.w3c.dom.traversal.NodeFilter;

public abstract class Reader implements Readable, Closeable {
    protected Object lock;

    public abstract void close() throws IOException;

    public abstract int read(char[] cArr, int i, int i2) throws IOException;

    protected Reader() {
        this.lock = this;
    }

    protected Reader(Object lock) {
        if (lock == null) {
            throw new NullPointerException("lock == null");
        }
        this.lock = lock;
    }

    public void mark(int readLimit) throws IOException {
        throw new IOException();
    }

    public boolean markSupported() {
        return false;
    }

    public int read() throws IOException {
        int i = -1;
        synchronized (this.lock) {
            char[] charArray = new char[1];
            if (read(charArray, 0, 1) != -1) {
                i = charArray[0];
            }
        }
        return i;
    }

    public int read(char[] buffer) throws IOException {
        return read(buffer, 0, buffer.length);
    }

    public boolean ready() throws IOException {
        return false;
    }

    public void reset() throws IOException {
        throw new IOException();
    }

    public long skip(long charCount) throws IOException {
        if (charCount < 0) {
            throw new IllegalArgumentException("charCount < 0: " + charCount);
        }
        synchronized (this.lock) {
            long skipped = 0;
            int toRead = charCount < 512 ? (int) charCount : NodeFilter.SHOW_DOCUMENT_TYPE;
            char[] charsSkipped = new char[toRead];
            while (skipped < charCount) {
                int read = read(charsSkipped, 0, toRead);
                if (read == -1) {
                    return skipped;
                }
                skipped += (long) read;
                if (read < toRead) {
                    return skipped;
                } else if (charCount - skipped < ((long) toRead)) {
                    toRead = (int) (charCount - skipped);
                }
            }
            return skipped;
        }
    }

    public int read(CharBuffer target) throws IOException {
        int length = target.length();
        char[] buf = new char[length];
        length = Math.min(length, read(buf));
        if (length > 0) {
            target.put(buf, 0, length);
        }
        return length;
    }
}
