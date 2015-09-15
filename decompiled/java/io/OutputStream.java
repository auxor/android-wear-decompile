package java.io;

import java.util.Arrays;

public abstract class OutputStream implements Closeable, Flushable {
    public abstract void write(int i) throws IOException;

    public void close() throws IOException {
    }

    public void flush() throws IOException {
    }

    public void write(byte[] buffer) throws IOException {
        write(buffer, 0, buffer.length);
    }

    public void write(byte[] buffer, int offset, int count) throws IOException {
        Arrays.checkOffsetAndCount(buffer.length, offset, count);
        for (int i = offset; i < offset + count; i++) {
            write(buffer[i]);
        }
    }

    boolean checkError() {
        return false;
    }
}
