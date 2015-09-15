package java.security;

import java.nio.ByteBuffer;
import org.apache.harmony.security.fortress.Engine;
import org.apache.harmony.security.fortress.Engine.SpiAndProvider;

public abstract class MessageDigest extends MessageDigestSpi {
    private static final Engine ENGINE;
    private String algorithm;
    private Provider provider;

    private static class MessageDigestImpl extends MessageDigest {
        private MessageDigestSpi spiImpl;

        private MessageDigestImpl(MessageDigestSpi messageDigestSpi, Provider provider, String algorithm) {
            super(algorithm);
            this.provider = provider;
            this.spiImpl = messageDigestSpi;
        }

        protected void engineReset() {
            this.spiImpl.engineReset();
        }

        protected byte[] engineDigest() {
            return this.spiImpl.engineDigest();
        }

        protected int engineGetDigestLength() {
            return this.spiImpl.engineGetDigestLength();
        }

        protected void engineUpdate(byte arg0) {
            this.spiImpl.engineUpdate(arg0);
        }

        protected void engineUpdate(byte[] arg0, int arg1, int arg2) {
            this.spiImpl.engineUpdate(arg0, arg1, arg2);
        }

        public Object clone() throws CloneNotSupportedException {
            return new MessageDigestImpl((MessageDigestSpi) this.spiImpl.clone(), getProvider(), getAlgorithm());
        }
    }

    static {
        ENGINE = new Engine("MessageDigest");
    }

    protected MessageDigest(String algorithm) {
        this.algorithm = algorithm;
    }

    public static MessageDigest getInstance(String algorithm) throws NoSuchAlgorithmException {
        if (algorithm == null) {
            throw new NullPointerException("algorithm == null");
        }
        SpiAndProvider sap = ENGINE.getInstance(algorithm, null);
        MessageDigest spi = sap.spi;
        Provider provider = sap.provider;
        if (!(spi instanceof MessageDigest)) {
            return new MessageDigestImpl(sap.provider, algorithm, null);
        }
        MessageDigest result = spi;
        result.algorithm = algorithm;
        result.provider = provider;
        return result;
    }

    public static MessageDigest getInstance(String algorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
        if (provider == null || provider.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Provider p = Security.getProvider(provider);
        if (p != null) {
            return getInstance(algorithm, p);
        }
        throw new NoSuchProviderException(provider);
    }

    public static MessageDigest getInstance(String algorithm, Provider provider) throws NoSuchAlgorithmException {
        if (provider == null) {
            throw new IllegalArgumentException("provider == null");
        } else if (algorithm == null) {
            throw new NullPointerException("algorithm == null");
        } else {
            MessageDigest spi = ENGINE.getInstance(algorithm, provider, null);
            if (!(spi instanceof MessageDigest)) {
                return new MessageDigestImpl(provider, algorithm, null);
            }
            MessageDigest result = spi;
            result.algorithm = algorithm;
            result.provider = provider;
            return result;
        }
    }

    public void reset() {
        engineReset();
    }

    public void update(byte arg0) {
        engineUpdate(arg0);
    }

    public void update(byte[] input, int offset, int len) {
        if (input == null || ((long) offset) + ((long) len) > ((long) input.length)) {
            throw new IllegalArgumentException();
        }
        engineUpdate(input, offset, len);
    }

    public void update(byte[] input) {
        if (input == null) {
            throw new NullPointerException("input == null");
        }
        engineUpdate(input, 0, input.length);
    }

    public byte[] digest() {
        return engineDigest();
    }

    public int digest(byte[] buf, int offset, int len) throws DigestException {
        if (buf != null && ((long) offset) + ((long) len) <= ((long) buf.length)) {
            return engineDigest(buf, offset, len);
        }
        throw new IllegalArgumentException();
    }

    public byte[] digest(byte[] input) {
        update(input);
        return digest();
    }

    public String toString() {
        return "MESSAGE DIGEST " + this.algorithm;
    }

    public static boolean isEqual(byte[] digesta, byte[] digestb) {
        if (digesta.length != digestb.length) {
            return false;
        }
        int v = 0;
        for (int i = 0; i < digesta.length; i++) {
            v |= digesta[i] ^ digestb[i];
        }
        if (v == 0) {
            return true;
        }
        return false;
    }

    public final String getAlgorithm() {
        return this.algorithm;
    }

    public final Provider getProvider() {
        return this.provider;
    }

    public final int getDigestLength() {
        int l = engineGetDigestLength();
        if (l != 0) {
            return l;
        }
        if (!(this instanceof Cloneable)) {
            return 0;
        }
        try {
            return ((MessageDigest) clone()).digest().length;
        } catch (CloneNotSupportedException e) {
            return 0;
        }
    }

    public final void update(ByteBuffer input) {
        engineUpdate(input);
    }
}
