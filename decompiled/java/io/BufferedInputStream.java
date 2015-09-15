package java.io;

import dalvik.bytecode.Opcodes;

public class BufferedInputStream extends FilterInputStream {
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    protected volatile byte[] buf;
    protected int count;
    protected int marklimit;
    protected int markpos;
    protected int pos;

    public BufferedInputStream(InputStream in) {
        this(in, DEFAULT_BUFFER_SIZE);
    }

    public BufferedInputStream(InputStream in, int size) {
        super(in);
        this.markpos = -1;
        if (size <= 0) {
            throw new IllegalArgumentException("size <= 0");
        }
        this.buf = new byte[size];
    }

    public synchronized int available() throws IOException {
        InputStream localIn;
        localIn = this.in;
        if (this.buf == null || localIn == null) {
            throw streamClosed();
        }
        return (this.count - this.pos) + localIn.available();
    }

    private IOException streamClosed() throws IOException {
        throw new IOException("BufferedInputStream is closed");
    }

    public void close() throws IOException {
        this.buf = null;
        InputStream localIn = this.in;
        this.in = null;
        if (localIn != null) {
            localIn.close();
        }
    }

    private int fillbuf(InputStream localIn, byte[] localBuf) throws IOException {
        int i = 0;
        if (this.markpos == -1 || this.pos - this.markpos >= this.marklimit) {
            int result = localIn.read(localBuf);
            if (result > 0) {
                this.markpos = -1;
                this.pos = 0;
                if (result != -1) {
                    i = result;
                }
                this.count = i;
            }
            return result;
        }
        if (this.markpos == 0 && this.marklimit > localBuf.length) {
            int newLength = localBuf.length * 2;
            if (newLength > this.marklimit) {
                newLength = this.marklimit;
            }
            byte[] newbuf = new byte[newLength];
            System.arraycopy(localBuf, 0, newbuf, 0, localBuf.length);
            this.buf = newbuf;
            localBuf = newbuf;
        } else if (this.markpos > 0) {
            System.arraycopy(localBuf, this.markpos, localBuf, 0, localBuf.length - this.markpos);
        }
        this.pos -= this.markpos;
        this.markpos = 0;
        this.count = 0;
        int bytesread = localIn.read(localBuf, this.pos, localBuf.length - this.pos);
        if (bytesread <= 0) {
            i = this.pos;
        } else {
            i = this.pos + bytesread;
        }
        this.count = i;
        return bytesread;
    }

    public synchronized void mark(int readlimit) {
        this.marklimit = readlimit;
        this.markpos = this.pos;
    }

    public boolean markSupported() {
        return true;
    }

    public synchronized int read() throws IOException {
        int i = -1;
        synchronized (this) {
            byte[] localBuf = this.buf;
            InputStream localIn = this.in;
            if (localBuf == null || localIn == null) {
                throw streamClosed();
            }
            if (this.pos < this.count || fillbuf(localIn, localBuf) != -1) {
                if (localBuf != this.buf) {
                    localBuf = this.buf;
                    if (localBuf == null) {
                        throw streamClosed();
                    }
                }
                if (this.count - this.pos > 0) {
                    i = this.pos;
                    this.pos = i + 1;
                    i = localBuf[i] & Opcodes.OP_CONST_CLASS_JUMBO;
                }
            }
        }
        return i;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized int read(byte[] r9, int r10, int r11) throws java.io.IOException {
        /*
        r8 = this;
        r5 = -1;
        monitor-enter(r8);
        r1 = r8.buf;	 Catch:{ all -> 0x000b }
        if (r1 != 0) goto L_0x000e;
    L_0x0006:
        r5 = r8.streamClosed();	 Catch:{ all -> 0x000b }
        throw r5;	 Catch:{ all -> 0x000b }
    L_0x000b:
        r5 = move-exception;
        monitor-exit(r8);
        throw r5;
    L_0x000e:
        r6 = r9.length;	 Catch:{ all -> 0x000b }
        java.util.Arrays.checkOffsetAndCount(r6, r10, r11);	 Catch:{ all -> 0x000b }
        if (r11 != 0) goto L_0x0017;
    L_0x0014:
        r5 = 0;
    L_0x0015:
        monitor-exit(r8);
        return r5;
    L_0x0017:
        r2 = r8.in;	 Catch:{ all -> 0x000b }
        if (r2 != 0) goto L_0x0020;
    L_0x001b:
        r5 = r8.streamClosed();	 Catch:{ all -> 0x000b }
        throw r5;	 Catch:{ all -> 0x000b }
    L_0x0020:
        r6 = r8.pos;	 Catch:{ all -> 0x000b }
        r7 = r8.count;	 Catch:{ all -> 0x000b }
        if (r6 >= r7) goto L_0x005e;
    L_0x0026:
        r6 = r8.count;	 Catch:{ all -> 0x000b }
        r7 = r8.pos;	 Catch:{ all -> 0x000b }
        r6 = r6 - r7;
        if (r6 < r11) goto L_0x0042;
    L_0x002d:
        r0 = r11;
    L_0x002e:
        r6 = r8.pos;	 Catch:{ all -> 0x000b }
        java.lang.System.arraycopy(r1, r6, r9, r10, r0);	 Catch:{ all -> 0x000b }
        r6 = r8.pos;	 Catch:{ all -> 0x000b }
        r6 = r6 + r0;
        r8.pos = r6;	 Catch:{ all -> 0x000b }
        if (r0 == r11) goto L_0x0040;
    L_0x003a:
        r6 = r2.available();	 Catch:{ all -> 0x000b }
        if (r6 != 0) goto L_0x0049;
    L_0x0040:
        r5 = r0;
        goto L_0x0015;
    L_0x0042:
        r6 = r8.count;	 Catch:{ all -> 0x000b }
        r7 = r8.pos;	 Catch:{ all -> 0x000b }
        r0 = r6 - r7;
        goto L_0x002e;
    L_0x0049:
        r10 = r10 + r0;
        r4 = r11 - r0;
    L_0x004c:
        r6 = r8.markpos;	 Catch:{ all -> 0x000b }
        if (r6 != r5) goto L_0x0060;
    L_0x0050:
        r6 = r1.length;	 Catch:{ all -> 0x000b }
        if (r4 < r6) goto L_0x0060;
    L_0x0053:
        r3 = r2.read(r9, r10, r4);	 Catch:{ all -> 0x000b }
        if (r3 != r5) goto L_0x008a;
    L_0x0059:
        if (r4 == r11) goto L_0x0015;
    L_0x005b:
        r5 = r11 - r4;
        goto L_0x0015;
    L_0x005e:
        r4 = r11;
        goto L_0x004c;
    L_0x0060:
        r6 = r8.fillbuf(r2, r1);	 Catch:{ all -> 0x000b }
        if (r6 != r5) goto L_0x006b;
    L_0x0066:
        if (r4 == r11) goto L_0x0015;
    L_0x0068:
        r5 = r11 - r4;
        goto L_0x0015;
    L_0x006b:
        r6 = r8.buf;	 Catch:{ all -> 0x000b }
        if (r1 == r6) goto L_0x0078;
    L_0x006f:
        r1 = r8.buf;	 Catch:{ all -> 0x000b }
        if (r1 != 0) goto L_0x0078;
    L_0x0073:
        r5 = r8.streamClosed();	 Catch:{ all -> 0x000b }
        throw r5;	 Catch:{ all -> 0x000b }
    L_0x0078:
        r6 = r8.count;	 Catch:{ all -> 0x000b }
        r7 = r8.pos;	 Catch:{ all -> 0x000b }
        r6 = r6 - r7;
        if (r6 < r4) goto L_0x008f;
    L_0x007f:
        r3 = r4;
    L_0x0080:
        r6 = r8.pos;	 Catch:{ all -> 0x000b }
        java.lang.System.arraycopy(r1, r6, r9, r10, r3);	 Catch:{ all -> 0x000b }
        r6 = r8.pos;	 Catch:{ all -> 0x000b }
        r6 = r6 + r3;
        r8.pos = r6;	 Catch:{ all -> 0x000b }
    L_0x008a:
        r4 = r4 - r3;
        if (r4 != 0) goto L_0x0096;
    L_0x008d:
        r5 = r11;
        goto L_0x0015;
    L_0x008f:
        r6 = r8.count;	 Catch:{ all -> 0x000b }
        r7 = r8.pos;	 Catch:{ all -> 0x000b }
        r3 = r6 - r7;
        goto L_0x0080;
    L_0x0096:
        r6 = r2.available();	 Catch:{ all -> 0x000b }
        if (r6 != 0) goto L_0x00a0;
    L_0x009c:
        r5 = r11 - r4;
        goto L_0x0015;
    L_0x00a0:
        r10 = r10 + r3;
        goto L_0x004c;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.io.BufferedInputStream.read(byte[], int, int):int");
    }

    public synchronized void reset() throws IOException {
        if (this.buf == null) {
            throw new IOException("Stream is closed");
        } else if (this.markpos == -1) {
            throw new IOException("Mark has been invalidated.");
        } else {
            this.pos = this.markpos;
        }
    }

    public synchronized long skip(long byteCount) throws IOException {
        byte[] localBuf = this.buf;
        InputStream localIn = this.in;
        if (localBuf == null) {
            throw streamClosed();
        } else if (byteCount < 1) {
            byteCount = 0;
        } else if (localIn == null) {
            throw streamClosed();
        } else if (((long) (this.count - this.pos)) >= byteCount) {
            this.pos = (int) (((long) this.pos) + byteCount);
        } else {
            long read = (long) (this.count - this.pos);
            this.pos = this.count;
            if (this.markpos == -1 || byteCount > ((long) this.marklimit)) {
                byteCount = read + localIn.skip(byteCount - read);
            } else if (fillbuf(localIn, localBuf) == -1) {
                byteCount = read;
            } else if (((long) (this.count - this.pos)) >= byteCount - read) {
                this.pos = (int) (((long) this.pos) + (byteCount - read));
            } else {
                read += (long) (this.count - this.pos);
                this.pos = this.count;
                byteCount = read;
            }
        }
        return byteCount;
    }
}
