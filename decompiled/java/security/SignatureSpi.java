package java.security;

import java.nio.ByteBuffer;
import java.security.spec.AlgorithmParameterSpec;

public abstract class SignatureSpi {
    protected SecureRandom appRandom;

    @Deprecated
    protected abstract Object engineGetParameter(String str) throws InvalidParameterException;

    protected abstract void engineInitSign(PrivateKey privateKey) throws InvalidKeyException;

    protected abstract void engineInitVerify(PublicKey publicKey) throws InvalidKeyException;

    @Deprecated
    protected abstract void engineSetParameter(String str, Object obj) throws InvalidParameterException;

    protected abstract byte[] engineSign() throws SignatureException;

    protected abstract void engineUpdate(byte b) throws SignatureException;

    protected abstract void engineUpdate(byte[] bArr, int i, int i2) throws SignatureException;

    protected abstract boolean engineVerify(byte[] bArr) throws SignatureException;

    protected void engineInitSign(PrivateKey privateKey, SecureRandom random) throws InvalidKeyException {
        this.appRandom = random;
        engineInitSign(privateKey);
    }

    protected void engineUpdate(ByteBuffer input) {
        if (!input.hasRemaining()) {
            return;
        }
        byte[] tmp;
        if (input.hasArray()) {
            tmp = input.array();
            int offset = input.arrayOffset();
            int position = input.position();
            int limit = input.limit();
            try {
                engineUpdate(tmp, offset + position, limit - position);
                input.position(limit);
                return;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        tmp = new byte[(input.limit() - input.position())];
        input.get(tmp);
        try {
            engineUpdate(tmp, 0, tmp.length);
        } catch (Throwable e2) {
            throw new RuntimeException(e2);
        }
    }

    protected int engineSign(byte[] outbuf, int offset, int len) throws SignatureException {
        byte[] tmp = engineSign();
        if (tmp == null) {
            return 0;
        }
        if (len < tmp.length) {
            throw new SignatureException("The value of len parameter is less than the actual signature length");
        } else if (offset < 0) {
            throw new SignatureException("offset < 0");
        } else if (offset + len > outbuf.length) {
            throw new SignatureException("offset + len > outbuf.length");
        } else {
            System.arraycopy(tmp, 0, outbuf, offset, tmp.length);
            return tmp.length;
        }
    }

    protected boolean engineVerify(byte[] sigBytes, int offset, int length) throws SignatureException {
        byte[] tmp = new byte[length];
        System.arraycopy(sigBytes, offset, tmp, 0, length);
        return engineVerify(tmp);
    }

    protected void engineSetParameter(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
        throw new UnsupportedOperationException();
    }

    protected AlgorithmParameters engineGetParameters() {
        throw new UnsupportedOperationException();
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
