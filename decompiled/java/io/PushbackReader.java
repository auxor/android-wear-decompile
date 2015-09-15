package java.io;

import java.util.Arrays;

public class PushbackReader extends FilterReader {
    char[] buf;
    int pos;

    public PushbackReader(Reader in) {
        super(in);
        this.buf = new char[1];
        this.pos = 1;
    }

    public PushbackReader(Reader in, int size) {
        super(in);
        if (size <= 0) {
            throw new IllegalArgumentException("size <= 0");
        }
        this.buf = new char[size];
        this.pos = size;
    }

    public void close() throws IOException {
        synchronized (this.lock) {
            this.buf = null;
            this.in.close();
        }
    }

    public void mark(int readAheadLimit) throws IOException {
        throw new IOException("mark/reset not supported");
    }

    public boolean markSupported() {
        return false;
    }

    public int read() throws IOException {
        int i;
        synchronized (this.lock) {
            checkNotClosed();
            if (this.pos < this.buf.length) {
                char[] cArr = this.buf;
                int i2 = this.pos;
                this.pos = i2 + 1;
                i = cArr[i2];
            } else {
                i = this.in.read();
            }
        }
        return i;
    }

    private void checkNotClosed() throws IOException {
        if (this.buf == null) {
            throw new IOException("PushbackReader is closed");
        }
    }

    public int read(char[] buffer, int offset, int count) throws IOException {
        synchronized (this.lock) {
            checkNotClosed();
            Arrays.checkOffsetAndCount(buffer.length, offset, count);
            int copiedChars = 0;
            int copyLength = 0;
            int newOffset = offset;
            if (this.pos < this.buf.length) {
                if (this.buf.length - this.pos >= count) {
                    copyLength = count;
                } else {
                    copyLength = this.buf.length - this.pos;
                }
                System.arraycopy(this.buf, this.pos, buffer, newOffset, copyLength);
                newOffset += copyLength;
                copiedChars = 0 + copyLength;
                this.pos += copyLength;
            }
            if (copyLength == count) {
                return count;
            }
            int inCopied = this.in.read(buffer, newOffset, count - copiedChars);
            if (inCopied > 0) {
                count = inCopied + copiedChars;
                return count;
            } else if (copiedChars == 0) {
                return inCopied;
            } else {
                return copiedChars;
            }
        }
    }

    public boolean ready() throws IOException {
        boolean z;
        synchronized (this.lock) {
            if (this.buf == null) {
                throw new IOException("Reader is closed");
            }
            z = this.buf.length - this.pos > 0 || this.in.ready();
        }
        return z;
    }

    public void reset() throws IOException {
        throw new IOException("mark/reset not supported");
    }

    public void unread(char[] buffer) throws IOException {
        unread(buffer, 0, buffer.length);
    }

    public void unread(char[] buffer, int offset, int length) throws IOException {
        synchronized (this.lock) {
            checkNotClosed();
            if (length > this.pos) {
                throw new IOException("Pushback buffer full");
            }
            Arrays.checkOffsetAndCount(buffer.length, offset, length);
            for (int i = (offset + length) - 1; i >= offset; i--) {
                unread(buffer[i]);
            }
        }
    }

    public void unread(int oneChar) throws IOException {
        synchronized (this.lock) {
            checkNotClosed();
            if (this.pos == 0) {
                throw new IOException("Pushback buffer full");
            }
            char[] cArr = this.buf;
            int i = this.pos - 1;
            this.pos = i;
            cArr[i] = (char) oneChar;
        }
    }

    public long skip(long charCount) throws IOException {
        if (charCount < 0) {
            throw new IllegalArgumentException("charCount < 0: " + charCount);
        }
        synchronized (this.lock) {
            checkNotClosed();
            if (charCount == 0) {
                return 0;
            }
            long inSkipped;
            int availableFromBuffer = this.buf.length - this.pos;
            if (availableFromBuffer > 0) {
                long requiredFromIn = charCount - ((long) availableFromBuffer);
                if (requiredFromIn <= 0) {
                    this.pos = (int) (((long) this.pos) + charCount);
                    return charCount;
                }
                this.pos += availableFromBuffer;
                inSkipped = this.in.skip(requiredFromIn);
            } else {
                inSkipped = this.in.skip(charCount);
            }
            charCount = inSkipped + ((long) availableFromBuffer);
            return charCount;
        }
    }
}
