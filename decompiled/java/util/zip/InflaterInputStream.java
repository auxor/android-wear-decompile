package java.util.zip;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.zip.ZipFile.RAFStream;
import libcore.io.Streams;

public class InflaterInputStream extends FilterInputStream {
    static final int BUF_SIZE = 512;
    protected byte[] buf;
    boolean closed;
    boolean eof;
    protected Inflater inf;
    protected int len;
    int nativeEndBufSize;

    public InflaterInputStream(InputStream is) {
        this(is, new Inflater(), BUF_SIZE);
    }

    public InflaterInputStream(InputStream is, Inflater inflater) {
        this(is, inflater, BUF_SIZE);
    }

    public InflaterInputStream(InputStream is, Inflater inflater, int bufferSize) {
        super(is);
        this.nativeEndBufSize = 0;
        if (is == null) {
            throw new NullPointerException("is == null");
        } else if (inflater == null) {
            throw new NullPointerException("inflater == null");
        } else if (bufferSize <= 0) {
            throw new IllegalArgumentException("bufferSize <= 0: " + bufferSize);
        } else {
            this.inf = inflater;
            if (is instanceof RAFStream) {
                this.nativeEndBufSize = bufferSize;
            } else {
                this.buf = new byte[bufferSize];
            }
        }
    }

    public int read() throws IOException {
        return Streams.readSingleByte(this);
    }

    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        checkClosed();
        Arrays.checkOffsetAndCount(buffer.length, byteOffset, byteCount);
        if (byteCount == 0) {
            return 0;
        }
        if (this.eof) {
            return -1;
        }
        do {
            if (this.inf.needsInput()) {
                fill();
            }
            try {
                int result = this.inf.inflate(buffer, byteOffset, byteCount);
                this.eof = this.inf.finished();
                if (result > 0) {
                    return result;
                }
                if (this.eof) {
                    return -1;
                }
                if (this.inf.needsDictionary()) {
                    this.eof = true;
                    return -1;
                }
            } catch (DataFormatException e) {
                this.eof = true;
                if (this.len == -1) {
                    throw new EOFException();
                }
                throw ((IOException) new IOException().initCause(e));
            }
        } while (this.len != -1);
        this.eof = true;
        throw new EOFException();
    }

    protected void fill() throws IOException {
        checkClosed();
        if (this.nativeEndBufSize > 0) {
            this.len = this.in.fill(this.inf, this.nativeEndBufSize);
            return;
        }
        int read = this.in.read(this.buf);
        this.len = read;
        if (read > 0) {
            this.inf.setInput(this.buf, 0, this.len);
        }
    }

    public long skip(long byteCount) throws IOException {
        if (byteCount >= 0) {
            return Streams.skipByReading(this, byteCount);
        }
        throw new IllegalArgumentException("byteCount < 0");
    }

    public int available() throws IOException {
        checkClosed();
        if (this.eof) {
            return 0;
        }
        return 1;
    }

    public void close() throws IOException {
        if (!this.closed) {
            this.inf.end();
            this.closed = true;
            this.eof = true;
            super.close();
        }
    }

    public void mark(int readlimit) {
    }

    public void reset() throws IOException {
        throw new IOException();
    }

    public boolean markSupported() {
        return false;
    }

    private void checkClosed() throws IOException {
        if (this.closed) {
            throw new IOException("Stream is closed");
        }
    }
}
