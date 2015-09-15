package java.io;

import dalvik.bytecode.Opcodes;
import java.util.Arrays;

public class ByteArrayOutputStream extends OutputStream {
    protected byte[] buf;
    protected int count;

    public ByteArrayOutputStream() {
        this.buf = new byte[32];
    }

    public ByteArrayOutputStream(int size) {
        if (size >= 0) {
            this.buf = new byte[size];
            return;
        }
        throw new IllegalArgumentException("size < 0");
    }

    public void close() throws IOException {
        super.close();
    }

    private void expand(int i) {
        if (this.count + i > this.buf.length) {
            byte[] newbuf = new byte[((this.count + i) * 2)];
            System.arraycopy(this.buf, 0, newbuf, 0, this.count);
            this.buf = newbuf;
        }
    }

    public synchronized void reset() {
        this.count = 0;
    }

    public int size() {
        return this.count;
    }

    public synchronized byte[] toByteArray() {
        byte[] newArray;
        newArray = new byte[this.count];
        System.arraycopy(this.buf, 0, newArray, 0, this.count);
        return newArray;
    }

    public String toString() {
        return new String(this.buf, 0, this.count);
    }

    @Deprecated
    public String toString(int hibyte) {
        char[] newBuf = new char[size()];
        for (int i = 0; i < newBuf.length; i++) {
            newBuf[i] = (char) (((hibyte & Opcodes.OP_CONST_CLASS_JUMBO) << 8) | (this.buf[i] & Opcodes.OP_CONST_CLASS_JUMBO));
        }
        return new String(newBuf);
    }

    public String toString(String charsetName) throws UnsupportedEncodingException {
        return new String(this.buf, 0, this.count, charsetName);
    }

    public synchronized void write(byte[] buffer, int offset, int len) {
        Arrays.checkOffsetAndCount(buffer.length, offset, len);
        if (len != 0) {
            expand(len);
            System.arraycopy(buffer, offset, this.buf, this.count, len);
            this.count += len;
        }
    }

    public synchronized void write(int oneByte) {
        if (this.count == this.buf.length) {
            expand(1);
        }
        byte[] bArr = this.buf;
        int i = this.count;
        this.count = i + 1;
        bArr[i] = (byte) oneByte;
    }

    public synchronized void writeTo(OutputStream out) throws IOException {
        out.write(this.buf, 0, this.count);
    }
}
