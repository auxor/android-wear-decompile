package java.io;

import dalvik.bytecode.Opcodes;
import java.util.Arrays;

public class ByteArrayInputStream extends InputStream {
    protected byte[] buf;
    protected int count;
    protected int mark;
    protected int pos;

    public ByteArrayInputStream(byte[] buf) {
        this.mark = 0;
        this.buf = buf;
        this.count = buf.length;
    }

    public ByteArrayInputStream(byte[] buf, int offset, int length) {
        this.buf = buf;
        this.pos = offset;
        this.mark = offset;
        this.count = offset + length > buf.length ? buf.length : offset + length;
    }

    public synchronized int available() {
        return this.count - this.pos;
    }

    public void close() throws IOException {
    }

    public synchronized void mark(int readlimit) {
        this.mark = this.pos;
    }

    public boolean markSupported() {
        return true;
    }

    public synchronized int read() {
        int i;
        if (this.pos < this.count) {
            byte[] bArr = this.buf;
            int i2 = this.pos;
            this.pos = i2 + 1;
            i = bArr[i2] & Opcodes.OP_CONST_CLASS_JUMBO;
        } else {
            i = -1;
        }
        return i;
    }

    public synchronized int read(byte[] buffer, int byteOffset, int byteCount) {
        int i;
        Arrays.checkOffsetAndCount(buffer.length, byteOffset, byteCount);
        if (this.pos >= this.count) {
            i = -1;
        } else if (byteCount == 0) {
            i = 0;
        } else {
            if (this.count - this.pos < byteCount) {
                i = this.count - this.pos;
            } else {
                i = byteCount;
            }
            System.arraycopy(this.buf, this.pos, buffer, byteOffset, i);
            this.pos += i;
        }
        return i;
    }

    public synchronized void reset() {
        this.pos = this.mark;
    }

    public synchronized long skip(long byteCount) {
        long j = 0;
        synchronized (this) {
            if (byteCount > 0) {
                int temp = this.pos;
                this.pos = ((long) (this.count - this.pos)) < byteCount ? this.count : (int) (((long) this.pos) + byteCount);
                j = (long) (this.pos - temp);
            }
        }
        return j;
    }
}
