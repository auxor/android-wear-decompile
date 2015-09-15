package java.io;

import java.util.Arrays;
import libcore.icu.DateIntervalFormat;
import libcore.util.SneakyThrow;

public class BufferedWriter extends Writer {
    private char[] buf;
    private Writer out;
    private int pos;

    public BufferedWriter(Writer out) {
        this(out, DateIntervalFormat.FORMAT_UTC);
    }

    public BufferedWriter(Writer out, int size) {
        super(out);
        if (size <= 0) {
            throw new IllegalArgumentException("size <= 0");
        }
        this.out = out;
        this.buf = new char[size];
    }

    public void close() throws IOException {
        synchronized (this.lock) {
            if (isClosed()) {
                return;
            }
            Throwable thrown = null;
            try {
                flushInternal();
            } catch (Throwable e) {
                thrown = e;
            }
            this.buf = null;
            try {
                this.out.close();
            } catch (Throwable e2) {
                if (thrown == null) {
                    thrown = e2;
                }
            }
            this.out = null;
            if (thrown != null) {
                SneakyThrow.sneakyThrow(thrown);
            }
        }
    }

    public void flush() throws IOException {
        synchronized (this.lock) {
            checkNotClosed();
            flushInternal();
            this.out.flush();
        }
    }

    private void checkNotClosed() throws IOException {
        if (isClosed()) {
            throw new IOException("BufferedWriter is closed");
        }
    }

    private void flushInternal() throws IOException {
        if (this.pos > 0) {
            this.out.write(this.buf, 0, this.pos);
        }
        this.pos = 0;
    }

    private boolean isClosed() {
        return this.out == null;
    }

    public void newLine() throws IOException {
        write(System.lineSeparator());
    }

    public void write(char[] buffer, int offset, int count) throws IOException {
        synchronized (this.lock) {
            checkNotClosed();
            if (buffer == null) {
                throw new NullPointerException("buffer == null");
            }
            Arrays.checkOffsetAndCount(buffer.length, offset, count);
            if (this.pos != 0 || count < this.buf.length) {
                int available = this.buf.length - this.pos;
                if (count < available) {
                    available = count;
                }
                if (available > 0) {
                    System.arraycopy(buffer, offset, this.buf, this.pos, available);
                    this.pos += available;
                }
                if (this.pos == this.buf.length) {
                    this.out.write(this.buf, 0, this.buf.length);
                    this.pos = 0;
                    if (count > available) {
                        offset += available;
                        available = count - available;
                        if (available >= this.buf.length) {
                            this.out.write(buffer, offset, available);
                            return;
                        }
                        System.arraycopy(buffer, offset, this.buf, this.pos, available);
                        this.pos += available;
                    }
                }
                return;
            }
            this.out.write(buffer, offset, count);
        }
    }

    public void write(int oneChar) throws IOException {
        synchronized (this.lock) {
            checkNotClosed();
            if (this.pos >= this.buf.length) {
                this.out.write(this.buf, 0, this.buf.length);
                this.pos = 0;
            }
            char[] cArr = this.buf;
            int i = this.pos;
            this.pos = i + 1;
            cArr[i] = (char) oneChar;
        }
    }

    public void write(String str, int offset, int count) throws IOException {
        synchronized (this.lock) {
            checkNotClosed();
            if (count <= 0) {
            } else if (offset < 0 || offset > str.length() - count) {
                throw new StringIndexOutOfBoundsException(str, offset, count);
            } else if (this.pos != 0 || count < this.buf.length) {
                int available = this.buf.length - this.pos;
                if (count < available) {
                    available = count;
                }
                if (available > 0) {
                    str.getChars(offset, offset + available, this.buf, this.pos);
                    this.pos += available;
                }
                if (this.pos == this.buf.length) {
                    this.out.write(this.buf, 0, this.buf.length);
                    this.pos = 0;
                    if (count > available) {
                        offset += available;
                        available = count - available;
                        if (available >= this.buf.length) {
                            chars = new char[count];
                            str.getChars(offset, offset + available, chars, 0);
                            this.out.write(chars, 0, available);
                            return;
                        }
                        str.getChars(offset, offset + available, this.buf, this.pos);
                        this.pos += available;
                    }
                }
            } else {
                chars = new char[count];
                str.getChars(offset, offset + count, chars, 0);
                this.out.write(chars, 0, count);
            }
        }
    }
}
