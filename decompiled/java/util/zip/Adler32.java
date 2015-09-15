package java.util.zip;

import java.util.Arrays;

public class Adler32 implements Checksum {
    private long adler;

    private native long updateByteImpl(int i, long j);

    private native long updateImpl(byte[] bArr, int i, int i2, long j);

    public Adler32() {
        this.adler = 1;
    }

    public long getValue() {
        return this.adler;
    }

    public void reset() {
        this.adler = 1;
    }

    public void update(int i) {
        this.adler = updateByteImpl(i, this.adler);
    }

    public void update(byte[] buf) {
        update(buf, 0, buf.length);
    }

    public void update(byte[] buf, int offset, int byteCount) {
        Arrays.checkOffsetAndCount(buf.length, offset, byteCount);
        this.adler = updateImpl(buf, offset, byteCount, this.adler);
    }
}
