package java.io;

public abstract class FilterReader extends Reader {
    protected Reader in;

    protected FilterReader(Reader in) {
        super(in);
        this.in = in;
    }

    public void close() throws IOException {
        synchronized (this.lock) {
            this.in.close();
        }
    }

    public synchronized void mark(int readlimit) throws IOException {
        synchronized (this.lock) {
            this.in.mark(readlimit);
        }
    }

    public boolean markSupported() {
        boolean markSupported;
        synchronized (this.lock) {
            markSupported = this.in.markSupported();
        }
        return markSupported;
    }

    public int read() throws IOException {
        int read;
        synchronized (this.lock) {
            read = this.in.read();
        }
        return read;
    }

    public int read(char[] buffer, int offset, int count) throws IOException {
        int read;
        synchronized (this.lock) {
            read = this.in.read(buffer, offset, count);
        }
        return read;
    }

    public boolean ready() throws IOException {
        boolean ready;
        synchronized (this.lock) {
            ready = this.in.ready();
        }
        return ready;
    }

    public void reset() throws IOException {
        synchronized (this.lock) {
            this.in.reset();
        }
    }

    public long skip(long charCount) throws IOException {
        long skip;
        synchronized (this.lock) {
            skip = this.in.skip(charCount);
        }
        return skip;
    }
}
