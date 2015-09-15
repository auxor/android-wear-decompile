package java.io;

public abstract class Writer implements Appendable, Closeable, Flushable {
    protected Object lock;

    public abstract void close() throws IOException;

    public abstract void flush() throws IOException;

    public abstract void write(char[] cArr, int i, int i2) throws IOException;

    protected Writer() {
        this.lock = this;
    }

    protected Writer(Object lock) {
        if (lock == null) {
            throw new NullPointerException("lock == null");
        }
        this.lock = lock;
    }

    public void write(char[] buf) throws IOException {
        write(buf, 0, buf.length);
    }

    public void write(int oneChar) throws IOException {
        synchronized (this.lock) {
            write(new char[]{(char) oneChar});
        }
    }

    public void write(String str) throws IOException {
        write(str, 0, str.length());
    }

    public void write(String str, int offset, int count) throws IOException {
        if ((offset | count) < 0 || offset > str.length() - count) {
            throw new StringIndexOutOfBoundsException(str, offset, count);
        }
        char[] buf = new char[count];
        str.getChars(offset, offset + count, buf, 0);
        synchronized (this.lock) {
            write(buf, 0, buf.length);
        }
    }

    public Writer append(char c) throws IOException {
        write((int) c);
        return this;
    }

    public Writer append(CharSequence csq) throws IOException {
        if (csq == null) {
            csq = "null";
        }
        write(csq.toString());
        return this;
    }

    public Writer append(CharSequence csq, int start, int end) throws IOException {
        if (csq == null) {
            csq = "null";
        }
        write(csq.subSequence(start, end).toString());
        return this;
    }

    boolean checkError() {
        return false;
    }
}
