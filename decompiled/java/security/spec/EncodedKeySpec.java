package java.security.spec;

public abstract class EncodedKeySpec implements KeySpec {
    private final byte[] encodedKey;

    public abstract String getFormat();

    public EncodedKeySpec(byte[] encodedKey) {
        this.encodedKey = new byte[encodedKey.length];
        System.arraycopy(encodedKey, 0, this.encodedKey, 0, this.encodedKey.length);
    }

    public byte[] getEncoded() {
        byte[] ret = new byte[this.encodedKey.length];
        System.arraycopy(this.encodedKey, 0, ret, 0, ret.length);
        return ret;
    }
}
