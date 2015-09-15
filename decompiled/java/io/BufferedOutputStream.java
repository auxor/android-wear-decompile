package java.io;

import java.util.Arrays;
import libcore.icu.DateIntervalFormat;

public class BufferedOutputStream extends FilterOutputStream {
    protected byte[] buf;
    protected int count;

    public BufferedOutputStream(OutputStream out) {
        this(out, DateIntervalFormat.FORMAT_UTC);
    }

    public BufferedOutputStream(OutputStream out, int size) {
        super(out);
        if (size <= 0) {
            throw new IllegalArgumentException("size <= 0");
        }
        this.buf = new byte[size];
    }

    public synchronized void flush() throws IOException {
        checkNotClosed();
        flushInternal();
        this.out.flush();
    }

    private void checkNotClosed() throws IOException {
        if (this.buf == null) {
            throw new IOException("BufferedOutputStream is closed");
        }
    }

    public synchronized void write(byte[] buffer, int offset, int length) throws IOException {
        checkNotClosed();
        if (buffer == null) {
            throw new NullPointerException("buffer == null");
        }
        byte[] internalBuffer = this.buf;
        if (length >= internalBuffer.length) {
            flushInternal();
            this.out.write(buffer, offset, length);
        } else {
            Arrays.checkOffsetAndCount(buffer.length, offset, length);
            if (length > internalBuffer.length - this.count) {
                flushInternal();
            }
            System.arraycopy(buffer, offset, internalBuffer, this.count, length);
            this.count += length;
        }
    }

    public synchronized void close() throws IOException {
        byte[] bArr = this.buf;
        if (bArr != null) {
            try {
                super.close();
                this.buf = bArr;
            } finally {
                this.buf = null;
            }
        }
    }

    public synchronized void write(int oneByte) throws IOException {
        checkNotClosed();
        if (this.count == this.buf.length) {
            this.out.write(this.buf, 0, this.count);
            this.count = 0;
        }
        byte[] bArr = this.buf;
        int i = this.count;
        this.count = i + 1;
        bArr[i] = (byte) oneByte;
    }

    private void flushInternal() throws IOException {
        if (this.count > 0) {
            this.out.write(this.buf, 0, this.count);
            this.count = 0;
        }
    }
}
