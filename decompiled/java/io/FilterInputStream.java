package java.io;

public class FilterInputStream extends InputStream {
    protected volatile InputStream in;

    protected FilterInputStream(InputStream in) {
        this.in = in;
    }

    public int available() throws IOException {
        return this.in.available();
    }

    public void close() throws IOException {
        this.in.close();
    }

    public synchronized void mark(int readlimit) {
        this.in.mark(readlimit);
    }

    public boolean markSupported() {
        return this.in.markSupported();
    }

    public int read() throws IOException {
        return this.in.read();
    }

    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        return this.in.read(buffer, byteOffset, byteCount);
    }

    public synchronized void reset() throws IOException {
        this.in.reset();
    }

    public long skip(long byteCount) throws IOException {
        return this.in.skip(byteCount);
    }
}
