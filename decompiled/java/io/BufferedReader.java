package java.io;

import libcore.icu.DateIntervalFormat;

public class BufferedReader extends Reader {
    private char[] buf;
    private int end;
    private Reader in;
    private boolean lastWasCR;
    private int mark;
    private int markLimit;
    private boolean markedLastWasCR;
    private int pos;

    public BufferedReader(Reader in) {
        this(in, DateIntervalFormat.FORMAT_UTC);
    }

    public BufferedReader(Reader in, int size) {
        super(in);
        this.mark = -1;
        this.markLimit = -1;
        if (size <= 0) {
            throw new IllegalArgumentException("size <= 0");
        }
        this.in = in;
        this.buf = new char[size];
    }

    public void close() throws IOException {
        synchronized (this.lock) {
            if (!isClosed()) {
                this.in.close();
                this.buf = null;
            }
        }
    }

    private int fillBuf() throws IOException {
        if (this.mark == -1 || this.pos - this.mark >= this.markLimit) {
            int result = this.in.read(this.buf, 0, this.buf.length);
            if (result > 0) {
                this.mark = -1;
                this.pos = 0;
                this.end = result;
            }
            return result;
        }
        if (this.mark == 0 && this.markLimit > this.buf.length) {
            int newLength = this.buf.length * 2;
            if (newLength > this.markLimit) {
                newLength = this.markLimit;
            }
            char[] newbuf = new char[newLength];
            System.arraycopy(this.buf, 0, newbuf, 0, this.buf.length);
            this.buf = newbuf;
        } else if (this.mark > 0) {
            System.arraycopy(this.buf, this.mark, this.buf, 0, this.buf.length - this.mark);
            this.pos -= this.mark;
            this.end -= this.mark;
            this.mark = 0;
        }
        int count = this.in.read(this.buf, this.pos, this.buf.length - this.pos);
        if (count == -1) {
            return count;
        }
        this.end += count;
        return count;
    }

    private boolean isClosed() {
        return this.buf == null;
    }

    public void mark(int markLimit) throws IOException {
        if (markLimit < 0) {
            throw new IllegalArgumentException("markLimit < 0:" + markLimit);
        }
        synchronized (this.lock) {
            checkNotClosed();
            this.markLimit = markLimit;
            this.mark = this.pos;
            this.markedLastWasCR = this.lastWasCR;
        }
    }

    private void checkNotClosed() throws IOException {
        if (isClosed()) {
            throw new IOException("BufferedReader is closed");
        }
    }

    public boolean markSupported() {
        return true;
    }

    public int read() throws IOException {
        int ch;
        synchronized (this.lock) {
            checkNotClosed();
            ch = readChar();
            if (this.lastWasCR && ch == 10) {
                ch = readChar();
            }
            this.lastWasCR = false;
        }
        return ch;
    }

    private int readChar() throws IOException {
        if (this.pos >= this.end && fillBuf() == -1) {
            return -1;
        }
        char[] cArr = this.buf;
        int i = this.pos;
        this.pos = i + 1;
        return cArr[i];
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int read(char[] r8, int r9, int r10) throws java.io.IOException {
        /*
        r7 = this;
        r3 = -1;
        r4 = r7.lock;
        monitor-enter(r4);
        r7.checkNotClosed();	 Catch:{ all -> 0x0041 }
        r5 = r8.length;	 Catch:{ all -> 0x0041 }
        java.util.Arrays.checkOffsetAndCount(r5, r9, r10);	 Catch:{ all -> 0x0041 }
        if (r10 != 0) goto L_0x0010;
    L_0x000d:
        r1 = 0;
        monitor-exit(r4);	 Catch:{ all -> 0x0041 }
    L_0x000f:
        return r1;
    L_0x0010:
        r7.maybeSwallowLF();	 Catch:{ all -> 0x0041 }
        r2 = r10;
    L_0x0014:
        if (r2 <= 0) goto L_0x003b;
    L_0x0016:
        r5 = r7.end;	 Catch:{ all -> 0x0041 }
        r6 = r7.pos;	 Catch:{ all -> 0x0041 }
        r0 = r5 - r6;
        if (r0 <= 0) goto L_0x002f;
    L_0x001e:
        if (r0 < r2) goto L_0x0044;
    L_0x0020:
        r1 = r2;
    L_0x0021:
        r5 = r7.buf;	 Catch:{ all -> 0x0041 }
        r6 = r7.pos;	 Catch:{ all -> 0x0041 }
        java.lang.System.arraycopy(r5, r6, r8, r9, r1);	 Catch:{ all -> 0x0041 }
        r5 = r7.pos;	 Catch:{ all -> 0x0041 }
        r5 = r5 + r1;
        r7.pos = r5;	 Catch:{ all -> 0x0041 }
        r9 = r9 + r1;
        r2 = r2 - r1;
    L_0x002f:
        if (r2 == 0) goto L_0x003b;
    L_0x0031:
        if (r2 >= r10) goto L_0x0046;
    L_0x0033:
        r5 = r7.in;	 Catch:{ all -> 0x0041 }
        r5 = r5.ready();	 Catch:{ all -> 0x0041 }
        if (r5 != 0) goto L_0x0046;
    L_0x003b:
        r1 = r10 - r2;
        if (r1 <= 0) goto L_0x006c;
    L_0x003f:
        monitor-exit(r4);	 Catch:{ all -> 0x0041 }
        goto L_0x000f;
    L_0x0041:
        r3 = move-exception;
        monitor-exit(r4);	 Catch:{ all -> 0x0041 }
        throw r3;
    L_0x0044:
        r1 = r0;
        goto L_0x0021;
    L_0x0046:
        r5 = r7.mark;	 Catch:{ all -> 0x0041 }
        if (r5 == r3) goto L_0x0053;
    L_0x004a:
        r5 = r7.pos;	 Catch:{ all -> 0x0041 }
        r6 = r7.mark;	 Catch:{ all -> 0x0041 }
        r5 = r5 - r6;
        r6 = r7.markLimit;	 Catch:{ all -> 0x0041 }
        if (r5 < r6) goto L_0x0065;
    L_0x0053:
        r5 = r7.buf;	 Catch:{ all -> 0x0041 }
        r5 = r5.length;	 Catch:{ all -> 0x0041 }
        if (r2 < r5) goto L_0x0065;
    L_0x0058:
        r5 = r7.in;	 Catch:{ all -> 0x0041 }
        r1 = r5.read(r8, r9, r2);	 Catch:{ all -> 0x0041 }
        if (r1 <= 0) goto L_0x003b;
    L_0x0060:
        r2 = r2 - r1;
        r5 = -1;
        r7.mark = r5;	 Catch:{ all -> 0x0041 }
        goto L_0x003b;
    L_0x0065:
        r5 = r7.fillBuf();	 Catch:{ all -> 0x0041 }
        if (r5 != r3) goto L_0x0014;
    L_0x006b:
        goto L_0x003b;
    L_0x006c:
        monitor-exit(r4);	 Catch:{ all -> 0x0041 }
        r1 = r3;
        goto L_0x000f;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.io.BufferedReader.read(char[], int, int):int");
    }

    final void chompNewline() throws IOException {
        if (!(this.pos == this.end && fillBuf() == -1) && this.buf[this.pos] == '\n') {
            this.pos++;
        }
    }

    private void maybeSwallowLF() throws IOException {
        if (this.lastWasCR) {
            chompNewline();
            this.lastWasCR = false;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String readLine() throws java.io.IOException {
        /*
        r13 = this;
        r12 = 10;
        r4 = 1;
        r5 = 0;
        r11 = 13;
        r6 = r13.lock;
        monitor-enter(r6);
        r13.checkNotClosed();	 Catch:{ all -> 0x0068 }
        r13.maybeSwallowLF();	 Catch:{ all -> 0x0068 }
        r1 = r13.pos;	 Catch:{ all -> 0x0068 }
    L_0x0011:
        r7 = r13.end;	 Catch:{ all -> 0x0068 }
        if (r1 >= r7) goto L_0x0039;
    L_0x0015:
        r7 = r13.buf;	 Catch:{ all -> 0x0068 }
        r0 = r7[r1];	 Catch:{ all -> 0x0068 }
        if (r0 == r12) goto L_0x001d;
    L_0x001b:
        if (r0 != r11) goto L_0x0036;
    L_0x001d:
        r2 = new java.lang.String;	 Catch:{ all -> 0x0068 }
        r7 = r13.buf;	 Catch:{ all -> 0x0068 }
        r8 = r13.pos;	 Catch:{ all -> 0x0068 }
        r9 = r13.pos;	 Catch:{ all -> 0x0068 }
        r9 = r1 - r9;
        r2.<init>(r7, r8, r9);	 Catch:{ all -> 0x0068 }
        r7 = r1 + 1;
        r13.pos = r7;	 Catch:{ all -> 0x0068 }
        if (r0 != r11) goto L_0x0034;
    L_0x0030:
        r13.lastWasCR = r4;	 Catch:{ all -> 0x0068 }
        monitor-exit(r6);	 Catch:{ all -> 0x0068 }
    L_0x0033:
        return r2;
    L_0x0034:
        r4 = r5;
        goto L_0x0030;
    L_0x0036:
        r1 = r1 + 1;
        goto L_0x0011;
    L_0x0039:
        r3 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0068 }
        r7 = r13.end;	 Catch:{ all -> 0x0068 }
        r8 = r13.pos;	 Catch:{ all -> 0x0068 }
        r7 = r7 - r8;
        r7 = r7 + 80;
        r3.<init>(r7);	 Catch:{ all -> 0x0068 }
        r7 = r13.buf;	 Catch:{ all -> 0x0068 }
        r8 = r13.pos;	 Catch:{ all -> 0x0068 }
        r9 = r13.end;	 Catch:{ all -> 0x0068 }
        r10 = r13.pos;	 Catch:{ all -> 0x0068 }
        r9 = r9 - r10;
        r3.append(r7, r8, r9);	 Catch:{ all -> 0x0068 }
    L_0x0051:
        r7 = r13.end;	 Catch:{ all -> 0x0068 }
        r13.pos = r7;	 Catch:{ all -> 0x0068 }
        r7 = r13.fillBuf();	 Catch:{ all -> 0x0068 }
        r8 = -1;
        if (r7 != r8) goto L_0x006d;
    L_0x005c:
        r4 = r3.length();	 Catch:{ all -> 0x0068 }
        if (r4 <= 0) goto L_0x006b;
    L_0x0062:
        r2 = r3.toString();	 Catch:{ all -> 0x0068 }
    L_0x0066:
        monitor-exit(r6);	 Catch:{ all -> 0x0068 }
        goto L_0x0033;
    L_0x0068:
        r4 = move-exception;
        monitor-exit(r6);	 Catch:{ all -> 0x0068 }
        throw r4;
    L_0x006b:
        r2 = 0;
        goto L_0x0066;
    L_0x006d:
        r1 = r13.pos;	 Catch:{ all -> 0x0068 }
    L_0x006f:
        r7 = r13.end;	 Catch:{ all -> 0x0068 }
        if (r1 >= r7) goto L_0x0099;
    L_0x0073:
        r7 = r13.buf;	 Catch:{ all -> 0x0068 }
        r0 = r7[r1];	 Catch:{ all -> 0x0068 }
        if (r0 == r12) goto L_0x007b;
    L_0x0079:
        if (r0 != r11) goto L_0x0096;
    L_0x007b:
        r7 = r13.buf;	 Catch:{ all -> 0x0068 }
        r8 = r13.pos;	 Catch:{ all -> 0x0068 }
        r9 = r13.pos;	 Catch:{ all -> 0x0068 }
        r9 = r1 - r9;
        r3.append(r7, r8, r9);	 Catch:{ all -> 0x0068 }
        r7 = r1 + 1;
        r13.pos = r7;	 Catch:{ all -> 0x0068 }
        if (r0 != r11) goto L_0x0094;
    L_0x008c:
        r13.lastWasCR = r4;	 Catch:{ all -> 0x0068 }
        r2 = r3.toString();	 Catch:{ all -> 0x0068 }
        monitor-exit(r6);	 Catch:{ all -> 0x0068 }
        goto L_0x0033;
    L_0x0094:
        r4 = r5;
        goto L_0x008c;
    L_0x0096:
        r1 = r1 + 1;
        goto L_0x006f;
    L_0x0099:
        r7 = r13.buf;	 Catch:{ all -> 0x0068 }
        r8 = r13.pos;	 Catch:{ all -> 0x0068 }
        r9 = r13.end;	 Catch:{ all -> 0x0068 }
        r10 = r13.pos;	 Catch:{ all -> 0x0068 }
        r9 = r9 - r10;
        r3.append(r7, r8, r9);	 Catch:{ all -> 0x0068 }
        goto L_0x0051;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.io.BufferedReader.readLine():java.lang.String");
    }

    public boolean ready() throws IOException {
        boolean z;
        synchronized (this.lock) {
            checkNotClosed();
            z = this.end - this.pos > 0 || this.in.ready();
        }
        return z;
    }

    public void reset() throws IOException {
        synchronized (this.lock) {
            checkNotClosed();
            if (this.mark == -1) {
                throw new IOException("Invalid mark");
            }
            this.pos = this.mark;
            this.lastWasCR = this.markedLastWasCR;
        }
    }

    public long skip(long charCount) throws IOException {
        if (charCount < 0) {
            throw new IllegalArgumentException("charCount < 0: " + charCount);
        }
        synchronized (this.lock) {
            checkNotClosed();
            if (((long) (this.end - this.pos)) >= charCount) {
                this.pos = (int) (((long) this.pos) + charCount);
                return charCount;
            }
            long read = (long) (this.end - this.pos);
            this.pos = this.end;
            while (read < charCount) {
                if (fillBuf() == -1) {
                    return read;
                } else if (((long) (this.end - this.pos)) >= charCount - read) {
                    this.pos = (int) (((long) this.pos) + (charCount - read));
                    return charCount;
                } else {
                    read += (long) (this.end - this.pos);
                    this.pos = this.end;
                }
            }
            return charCount;
        }
    }
}
