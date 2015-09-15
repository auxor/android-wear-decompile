package java.io;

import dalvik.bytecode.Opcodes;
import java.util.Arrays;

public class PushbackInputStream extends FilterInputStream {
    protected byte[] buf;
    protected int pos;

    public PushbackInputStream(InputStream in) {
        super(in);
        this.buf = in == null ? null : new byte[1];
        this.pos = 1;
    }

    public PushbackInputStream(InputStream in, int size) {
        super(in);
        if (size <= 0) {
            throw new IllegalArgumentException("size <= 0");
        }
        this.buf = in == null ? null : new byte[size];
        this.pos = size;
    }

    public int available() throws IOException {
        if (this.buf != null) {
            return (this.buf.length - this.pos) + this.in.available();
        }
        throw new IOException();
    }

    public void close() throws IOException {
        if (this.in != null) {
            this.in.close();
            this.in = null;
            this.buf = null;
        }
    }

    public boolean markSupported() {
        return false;
    }

    public int read() throws IOException {
        if (this.buf == null) {
            throw new IOException();
        } else if (this.pos >= this.buf.length) {
            return this.in.read();
        } else {
            byte[] bArr = this.buf;
            int i = this.pos;
            this.pos = i + 1;
            return bArr[i] & Opcodes.OP_CONST_CLASS_JUMBO;
        }
    }

    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        if (this.buf == null) {
            throw streamClosed();
        }
        Arrays.checkOffsetAndCount(buffer.length, byteOffset, byteCount);
        int copiedBytes = 0;
        int copyLength = 0;
        int newOffset = byteOffset;
        if (this.pos < this.buf.length) {
            if (this.buf.length - this.pos >= byteCount) {
                copyLength = byteCount;
            } else {
                copyLength = this.buf.length - this.pos;
            }
            System.arraycopy(this.buf, this.pos, buffer, newOffset, copyLength);
            newOffset += copyLength;
            copiedBytes = 0 + copyLength;
            this.pos += copyLength;
        }
        if (copyLength == byteCount) {
            return byteCount;
        }
        int inCopied = this.in.read(buffer, newOffset, byteCount - copiedBytes);
        if (inCopied > 0) {
            return inCopied + copiedBytes;
        }
        if (copiedBytes == 0) {
            return inCopied;
        }
        return copiedBytes;
    }

    private IOException streamClosed() throws IOException {
        throw new IOException("PushbackInputStream is closed");
    }

    public long skip(long byteCount) throws IOException {
        if (this.in == null) {
            throw streamClosed();
        } else if (byteCount <= 0) {
            return 0;
        } else {
            int numSkipped = 0;
            if (this.pos < this.buf.length) {
                numSkipped = (int) ((byteCount < ((long) (this.buf.length - this.pos)) ? byteCount : (long) (this.buf.length - this.pos)) + ((long) null));
                this.pos += numSkipped;
            }
            if (((long) numSkipped) < byteCount) {
                numSkipped = (int) (((long) numSkipped) + this.in.skip(byteCount - ((long) numSkipped)));
            }
            return (long) numSkipped;
        }
    }

    public void unread(byte[] buffer) throws IOException {
        unread(buffer, 0, buffer.length);
    }

    public void unread(byte[] buffer, int offset, int length) throws IOException {
        if (length > this.pos) {
            throw new IOException("Pushback buffer full");
        }
        Arrays.checkOffsetAndCount(buffer.length, offset, length);
        if (this.buf == null) {
            throw streamClosed();
        }
        System.arraycopy(buffer, offset, this.buf, this.pos - length, length);
        this.pos -= length;
    }

    public void unread(int oneByte) throws IOException {
        if (this.buf == null) {
            throw new IOException();
        } else if (this.pos == 0) {
            throw new IOException("Pushback buffer full");
        } else {
            byte[] bArr = this.buf;
            int i = this.pos - 1;
            this.pos = i;
            bArr[i] = (byte) oneByte;
        }
    }

    public void mark(int readlimit) {
    }

    public void reset() throws IOException {
        throw new IOException();
    }
}
