package java.security;

import java.nio.ByteBuffer;

public abstract class MessageDigestSpi {
    protected abstract byte[] engineDigest();

    protected abstract void engineReset();

    protected abstract void engineUpdate(byte b);

    protected abstract void engineUpdate(byte[] bArr, int i, int i2);

    protected int engineGetDigestLength() {
        return 0;
    }

    protected void engineUpdate(ByteBuffer input) {
        if (!input.hasRemaining()) {
            return;
        }
        if (input.hasArray()) {
            byte[] tmp = input.array();
            int offset = input.arrayOffset();
            int position = input.position();
            int limit = input.limit();
            engineUpdate(tmp, offset + position, limit - position);
            input.position(limit);
            return;
        }
        tmp = new byte[(input.limit() - input.position())];
        input.get(tmp);
        engineUpdate(tmp, 0, tmp.length);
    }

    protected int engineDigest(byte[] buf, int offset, int len) throws DigestException {
        if (len < engineGetDigestLength()) {
            engineReset();
            throw new DigestException("The value of len parameter is less than the actual digest length");
        } else if (offset < 0) {
            engineReset();
            throw new DigestException("offset < 0");
        } else if (offset + len > buf.length) {
            engineReset();
            throw new DigestException("offset + len > buf.length");
        } else {
            byte[] tmp = engineDigest();
            if (len < tmp.length) {
                throw new DigestException("The value of len parameter is less than the actual digest length");
            }
            System.arraycopy(tmp, 0, buf, offset, tmp.length);
            return tmp.length;
        }
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
