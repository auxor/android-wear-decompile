package javax.crypto.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

public class IvParameterSpec implements AlgorithmParameterSpec {
    private final byte[] iv;

    public IvParameterSpec(byte[] iv) {
        if (iv == null) {
            throw new NullPointerException("iv == null");
        }
        this.iv = new byte[iv.length];
        System.arraycopy(iv, 0, this.iv, 0, iv.length);
    }

    public IvParameterSpec(byte[] iv, int offset, int byteCount) {
        if (iv == null || iv.length - offset < byteCount) {
            throw new IllegalArgumentException();
        }
        Arrays.checkOffsetAndCount(iv.length, offset, byteCount);
        this.iv = new byte[byteCount];
        System.arraycopy(iv, offset, this.iv, 0, byteCount);
    }

    public byte[] getIV() {
        byte[] res = new byte[this.iv.length];
        System.arraycopy(this.iv, 0, res, 0, this.iv.length);
        return res;
    }
}
