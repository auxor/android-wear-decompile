package java.io;

import java.util.Arrays;
import libcore.util.SneakyThrow;

public class FilterOutputStream extends OutputStream {
    protected OutputStream out;

    public FilterOutputStream(OutputStream out) {
        this.out = out;
    }

    public void close() throws IOException {
        Throwable thrown = null;
        try {
            flush();
        } catch (Throwable e) {
            thrown = e;
        }
        try {
            this.out.close();
        } catch (Throwable e2) {
            if (thrown == null) {
                thrown = e2;
            }
        }
        if (thrown != null) {
            SneakyThrow.sneakyThrow(thrown);
        }
    }

    public void flush() throws IOException {
        this.out.flush();
    }

    public void write(byte[] buffer, int offset, int length) throws IOException {
        Arrays.checkOffsetAndCount(buffer.length, offset, length);
        for (int i = 0; i < length; i++) {
            write(buffer[offset + i]);
        }
    }

    public void write(int oneByte) throws IOException {
        this.out.write(oneByte);
    }
}
