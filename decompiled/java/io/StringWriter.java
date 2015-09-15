package java.io;

import java.util.Arrays;

public class StringWriter extends Writer {
    private StringBuffer buf;

    public StringWriter() {
        this.buf = new StringBuffer(16);
        this.lock = this.buf;
    }

    public StringWriter(int initialSize) {
        if (initialSize < 0) {
            throw new IllegalArgumentException("initialSize < 0: " + initialSize);
        }
        this.buf = new StringBuffer(initialSize);
        this.lock = this.buf;
    }

    public void close() throws IOException {
    }

    public void flush() {
    }

    public StringBuffer getBuffer() {
        return this.buf;
    }

    public String toString() {
        return this.buf.toString();
    }

    public void write(char[] chars, int offset, int count) {
        Arrays.checkOffsetAndCount(chars.length, offset, count);
        if (count != 0) {
            this.buf.append(chars, offset, count);
        }
    }

    public void write(int oneChar) {
        this.buf.append((char) oneChar);
    }

    public void write(String str) {
        this.buf.append(str);
    }

    public void write(String str, int offset, int count) {
        this.buf.append(str.substring(offset, offset + count));
    }

    public StringWriter append(char c) {
        write((int) c);
        return this;
    }

    public StringWriter append(CharSequence csq) {
        if (csq == null) {
            csq = "null";
        }
        write(csq.toString());
        return this;
    }

    public StringWriter append(CharSequence csq, int start, int end) {
        if (csq == null) {
            csq = "null";
        }
        String output = csq.subSequence(start, end).toString();
        write(output, 0, output.length());
        return this;
    }
}
