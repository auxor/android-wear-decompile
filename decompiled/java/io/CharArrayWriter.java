package java.io;

import java.util.Arrays;

public class CharArrayWriter extends Writer {
    protected char[] buf;
    protected int count;

    public CharArrayWriter() {
        this.buf = new char[32];
        this.lock = this.buf;
    }

    public CharArrayWriter(int initialSize) {
        if (initialSize < 0) {
            throw new IllegalArgumentException("size < 0");
        }
        this.buf = new char[initialSize];
        this.lock = this.buf;
    }

    public void close() {
    }

    private void expand(int i) {
        if (this.count + i > this.buf.length) {
            char[] newbuf = new char[Math.max(this.buf.length * 2, this.count + i)];
            System.arraycopy(this.buf, 0, newbuf, 0, this.count);
            this.buf = newbuf;
        }
    }

    public void flush() {
    }

    public void reset() {
        synchronized (this.lock) {
            this.count = 0;
        }
    }

    public int size() {
        int i;
        synchronized (this.lock) {
            i = this.count;
        }
        return i;
    }

    public char[] toCharArray() {
        char[] result;
        synchronized (this.lock) {
            result = new char[this.count];
            System.arraycopy(this.buf, 0, result, 0, this.count);
        }
        return result;
    }

    public String toString() {
        String str;
        synchronized (this.lock) {
            str = new String(this.buf, 0, this.count);
        }
        return str;
    }

    public void write(char[] buffer, int offset, int len) {
        Arrays.checkOffsetAndCount(buffer.length, offset, len);
        synchronized (this.lock) {
            expand(len);
            System.arraycopy(buffer, offset, this.buf, this.count, len);
            this.count += len;
        }
    }

    public void write(int oneChar) {
        synchronized (this.lock) {
            expand(1);
            char[] cArr = this.buf;
            int i = this.count;
            this.count = i + 1;
            cArr[i] = (char) oneChar;
        }
    }

    public void write(String str, int offset, int count) {
        if (str == null) {
            throw new NullPointerException("str == null");
        } else if ((offset | count) < 0 || offset > str.length() - count) {
            throw new StringIndexOutOfBoundsException(str, offset, count);
        } else {
            synchronized (this.lock) {
                expand(count);
                str.getChars(offset, offset + count, this.buf, this.count);
                this.count += count;
            }
        }
    }

    public void writeTo(Writer out) throws IOException {
        synchronized (this.lock) {
            out.write(this.buf, 0, this.count);
        }
    }

    public CharArrayWriter append(char c) {
        write(c);
        return this;
    }

    public CharArrayWriter append(CharSequence csq) {
        if (csq == null) {
            csq = "null";
        }
        append(csq, 0, csq.length());
        return this;
    }

    public CharArrayWriter append(CharSequence csq, int start, int end) {
        if (csq == null) {
            csq = "null";
        }
        String output = csq.subSequence(start, end).toString();
        write(output, 0, output.length());
        return this;
    }
}
