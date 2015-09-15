package java.security;

import java.nio.ByteBuffer;
import java.security.Provider.Service;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import org.apache.harmony.security.fortress.Engine;
import org.apache.harmony.security.fortress.Engine.SpiAndProvider;
import org.xmlpull.v1.XmlPullParser;

public abstract class Signature extends SignatureSpi {
    private static final Engine ENGINE;
    private static final String SERVICE = "Signature";
    protected static final int SIGN = 2;
    protected static final int UNINITIALIZED = 0;
    protected static final int VERIFY = 3;
    final String algorithm;
    Provider provider;
    protected int state;

    private static class SignatureImpl extends Signature {
        private final Object initLock;
        private final Provider specifiedProvider;
        private SignatureSpi spiImpl;

        public SignatureImpl(String algorithm, Provider provider) {
            super(algorithm);
            this.initLock = new Object();
            this.specifiedProvider = provider;
        }

        private SignatureImpl(String algorithm, Provider provider, SignatureSpi spi) {
            this(algorithm, provider);
            this.spiImpl = spi;
        }

        void ensureProviderChosen() {
            getSpi(null);
        }

        protected byte[] engineSign() throws SignatureException {
            return getSpi().engineSign();
        }

        protected void engineUpdate(byte arg0) throws SignatureException {
            getSpi().engineUpdate(arg0);
        }

        protected boolean engineVerify(byte[] arg0) throws SignatureException {
            return getSpi().engineVerify(arg0);
        }

        protected void engineUpdate(byte[] arg0, int arg1, int arg2) throws SignatureException {
            getSpi().engineUpdate(arg0, arg1, arg2);
        }

        protected void engineInitSign(PrivateKey arg0) throws InvalidKeyException {
            getSpi(arg0).engineInitSign(arg0);
        }

        protected void engineInitVerify(PublicKey arg0) throws InvalidKeyException {
            getSpi(arg0).engineInitVerify(arg0);
        }

        protected Object engineGetParameter(String arg0) throws InvalidParameterException {
            return getSpi().engineGetParameter(arg0);
        }

        protected void engineSetParameter(String arg0, Object arg1) throws InvalidParameterException {
            getSpi().engineSetParameter(arg0, arg1);
        }

        protected void engineSetParameter(AlgorithmParameterSpec arg0) throws InvalidAlgorithmParameterException {
            getSpi().engineSetParameter(arg0);
        }

        public Object clone() throws CloneNotSupportedException {
            return new SignatureImpl(getAlgorithm(), getProvider(), this.spiImpl != null ? (SignatureSpi) this.spiImpl.clone() : null);
        }

        private SignatureSpi getSpi(Key key) {
            SignatureSpi signatureSpi;
            synchronized (this.initLock) {
                if (this.spiImpl == null || key != null) {
                    SpiAndProvider sap = Signature.tryAlgorithm(key, this.specifiedProvider, this.algorithm);
                    if (sap == null) {
                        throw new ProviderException("No provider for " + getAlgorithm());
                    }
                    this.spiImpl = (SignatureSpi) sap.spi;
                    this.provider = sap.provider;
                    signatureSpi = this.spiImpl;
                } else {
                    signatureSpi = this.spiImpl;
                }
            }
            return signatureSpi;
        }

        private SignatureSpi getSpi() {
            return getSpi(null);
        }
    }

    static {
        ENGINE = new Engine(SERVICE);
    }

    protected Signature(String algorithm) {
        this.state = UNINITIALIZED;
        this.algorithm = algorithm;
    }

    public static Signature getInstance(String algorithm) throws NoSuchAlgorithmException {
        if (algorithm != null) {
            return getSignature(algorithm, null);
        }
        throw new NullPointerException("algorithm == null");
    }

    public static Signature getInstance(String algorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
        if (algorithm == null) {
            throw new NullPointerException("algorithm == null");
        } else if (provider == null || provider.isEmpty()) {
            throw new IllegalArgumentException();
        } else {
            Provider p = Security.getProvider(provider);
            if (p != null) {
                return getSignature(algorithm, p);
            }
            throw new NoSuchProviderException(provider);
        }
    }

    public static Signature getInstance(String algorithm, Provider provider) throws NoSuchAlgorithmException {
        if (algorithm == null) {
            throw new NullPointerException("algorithm == null");
        } else if (provider != null) {
            return getSignature(algorithm, provider);
        } else {
            throw new IllegalArgumentException("provider == null");
        }
    }

    private static Signature getSignature(String algorithm, Provider provider) throws NoSuchAlgorithmException {
        if (algorithm == null || algorithm.isEmpty()) {
            throw new NoSuchAlgorithmException("Unknown algorithm: " + algorithm);
        }
        SpiAndProvider spiAndProvider = tryAlgorithm(null, provider, algorithm);
        if (spiAndProvider == null) {
            if (provider == null) {
                throw new NoSuchAlgorithmException("No provider found for " + algorithm);
            }
            throw new NoSuchAlgorithmException("Provider " + provider.getName() + " does not provide " + algorithm);
        } else if (spiAndProvider.spi instanceof Signature) {
            return (Signature) spiAndProvider.spi;
        } else {
            return new SignatureImpl(algorithm, provider);
        }
    }

    private static SpiAndProvider tryAlgorithm(Key key, Provider provider, String algorithm) {
        if (provider != null) {
            Service service = provider.getService(SERVICE, algorithm);
            if (service == null) {
                return null;
            }
            return tryAlgorithmWithProvider(key, service);
        }
        ArrayList<Service> services = ENGINE.getServices(algorithm);
        if (services == null) {
            return null;
        }
        Iterator i$ = services.iterator();
        while (i$.hasNext()) {
            SpiAndProvider sap = tryAlgorithmWithProvider(key, (Service) i$.next());
            if (sap != null) {
                return sap;
            }
        }
        return null;
    }

    private static SpiAndProvider tryAlgorithmWithProvider(Key key, Service service) {
        if (key != null) {
            try {
                if (!service.supportsParameter(key)) {
                    return null;
                }
            } catch (NoSuchAlgorithmException e) {
                return null;
            }
        }
        SpiAndProvider sap = ENGINE.getInstance(service, null);
        if (sap.spi == null || sap.provider == null) {
            return null;
        }
        if (sap.spi instanceof SignatureSpi) {
            return sap;
        }
        return null;
    }

    public final Provider getProvider() {
        ensureProviderChosen();
        return this.provider;
    }

    void ensureProviderChosen() {
    }

    public final String getAlgorithm() {
        return this.algorithm;
    }

    public final void initVerify(PublicKey publicKey) throws InvalidKeyException {
        engineInitVerify(publicKey);
        this.state = VERIFY;
    }

    public final void initVerify(Certificate certificate) throws InvalidKeyException {
        if (certificate instanceof X509Certificate) {
            Set<String> ce = ((X509Certificate) certificate).getCriticalExtensionOIDs();
            boolean critical = false;
            if (!(ce == null || ce.isEmpty())) {
                for (Object equals : ce) {
                    if ("2.5.29.15".equals(equals)) {
                        critical = true;
                        break;
                    }
                }
                if (critical) {
                    boolean[] keyUsage = ((X509Certificate) certificate).getKeyUsage();
                    if (!(keyUsage == null || keyUsage[UNINITIALIZED])) {
                        throw new InvalidKeyException("The public key in the certificate cannot be used for digital signature purposes");
                    }
                }
            }
        }
        engineInitVerify(certificate.getPublicKey());
        this.state = VERIFY;
    }

    public final void initSign(PrivateKey privateKey) throws InvalidKeyException {
        engineInitSign(privateKey);
        this.state = SIGN;
    }

    public final void initSign(PrivateKey privateKey, SecureRandom random) throws InvalidKeyException {
        engineInitSign(privateKey, random);
        this.state = SIGN;
    }

    public final byte[] sign() throws SignatureException {
        if (this.state == SIGN) {
            return engineSign();
        }
        throw new SignatureException("Signature object is not initialized properly");
    }

    public final int sign(byte[] outbuf, int offset, int len) throws SignatureException {
        if (outbuf == null || offset < 0 || len < 0 || offset + len > outbuf.length) {
            throw new IllegalArgumentException();
        } else if (this.state == SIGN) {
            return engineSign(outbuf, offset, len);
        } else {
            throw new SignatureException("Signature object is not initialized properly");
        }
    }

    public final boolean verify(byte[] signature) throws SignatureException {
        if (this.state == VERIFY) {
            return engineVerify(signature);
        }
        throw new SignatureException("Signature object is not initialized properly");
    }

    public final boolean verify(byte[] signature, int offset, int length) throws SignatureException {
        if (this.state != VERIFY) {
            throw new SignatureException("Signature object is not initialized properly");
        } else if (signature != null && offset >= 0 && length >= 0 && offset + length <= signature.length) {
            return engineVerify(signature, offset, length);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public final void update(byte b) throws SignatureException {
        if (this.state == 0) {
            throw new SignatureException("Signature object is not initialized properly");
        }
        engineUpdate(b);
    }

    public final void update(byte[] data) throws SignatureException {
        if (this.state == 0) {
            throw new SignatureException("Signature object is not initialized properly");
        }
        engineUpdate(data, UNINITIALIZED, data.length);
    }

    public final void update(byte[] data, int off, int len) throws SignatureException {
        if (this.state == 0) {
            throw new SignatureException("Signature object is not initialized properly");
        } else if (data == null || off < 0 || len < 0 || off + len > data.length) {
            throw new IllegalArgumentException();
        } else {
            engineUpdate(data, off, len);
        }
    }

    public final void update(ByteBuffer data) throws SignatureException {
        if (this.state == 0) {
            throw new SignatureException("Signature object is not initialized properly");
        }
        engineUpdate(data);
    }

    public String toString() {
        return "SIGNATURE " + this.algorithm + " state: " + stateToString(this.state);
    }

    private String stateToString(int state) {
        switch (state) {
            case UNINITIALIZED /*0*/:
                return "UNINITIALIZED";
            case SIGN /*2*/:
                return "SIGN";
            case VERIFY /*3*/:
                return "VERIFY";
            default:
                return XmlPullParser.NO_NAMESPACE;
        }
    }

    @Deprecated
    public final void setParameter(String param, Object value) throws InvalidParameterException {
        engineSetParameter(param, value);
    }

    public final void setParameter(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
        engineSetParameter(params);
    }

    public final AlgorithmParameters getParameters() {
        return engineGetParameters();
    }

    @Deprecated
    public final Object getParameter(String param) throws InvalidParameterException {
        return engineGetParameter(param);
    }
}
