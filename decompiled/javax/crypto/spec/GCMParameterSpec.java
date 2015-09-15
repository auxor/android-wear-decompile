package javax.crypto.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

public class GCMParameterSpec implements AlgorithmParameterSpec {
    private final byte[] iv;
    private final int tagLen;

    public GCMParameterSpec(int tagLen, byte[] iv) {
        if (tagLen < 0) {
            throw new IllegalArgumentException("tag should be a non-negative integer");
        } else if (iv == null) {
            throw new IllegalArgumentException("iv == null");
        } else {
            this.tagLen = tagLen;
            this.iv = (byte[]) iv.clone();
        }
    }

    public GCMParameterSpec(int tagLen, byte[] iv, int offset, int byteCount) {
        if (tagLen < 0) {
            throw new IllegalArgumentException("tag should be a non-negative integer");
        } else if (iv == null) {
            throw new IllegalArgumentException("iv == null");
        } else {
            try {
                Arrays.checkOffsetAndCount(iv.length, offset, byteCount);
                this.tagLen = tagLen;
                this.iv = Arrays.copyOfRange(iv, offset, offset + byteCount);
            } catch (Throwable e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public int getTLen() {
        return this.tagLen;
    }

    public byte[] getIV() {
        return (byte[]) this.iv.clone();
    }
}
