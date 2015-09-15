package javax.crypto;

import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Provider.Service;
import java.security.ProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import org.apache.harmony.crypto.internal.NullCipherSpi;
import org.apache.harmony.security.fortress.Engine;
import org.apache.harmony.security.fortress.Engine.SpiAndProvider;

public class Cipher {
    private static final String ATTRIBUTE_MODES = "SupportedModes";
    private static final String ATTRIBUTE_PADDINGS = "SupportedPaddings";
    public static final int DECRYPT_MODE = 2;
    public static final int ENCRYPT_MODE = 1;
    private static final Engine ENGINE;
    public static final int PRIVATE_KEY = 2;
    public static final int PUBLIC_KEY = 1;
    public static final int SECRET_KEY = 3;
    private static final String SERVICE = "Cipher";
    public static final int UNWRAP_MODE = 4;
    public static final int WRAP_MODE = 3;
    private static SecureRandom secureRandom;
    private final Object initLock;
    private int mode;
    private Provider provider;
    private final Provider specifiedProvider;
    private final CipherSpi specifiedSpi;
    private CipherSpi spiImpl;
    private final String[] transformParts;
    private final String transformation;

    private enum NeedToSet {
        NONE,
        MODE,
        PADDING,
        BOTH
    }

    static {
        ENGINE = new Engine(SERVICE);
    }

    protected Cipher(CipherSpi cipherSpi, Provider provider, String transformation) {
        this.initLock = new Object();
        if (cipherSpi == null) {
            throw new NullPointerException("cipherSpi == null");
        } else if ((cipherSpi instanceof NullCipherSpi) || provider != null) {
            this.specifiedProvider = provider;
            this.specifiedSpi = cipherSpi;
            this.transformation = transformation;
            this.transformParts = null;
        } else {
            throw new NullPointerException("provider == null");
        }
    }

    private Cipher(String transformation, String[] transformParts, Provider provider) {
        this.initLock = new Object();
        this.transformation = transformation;
        this.transformParts = transformParts;
        this.specifiedProvider = provider;
        this.specifiedSpi = null;
    }

    public static final Cipher getInstance(String transformation) throws NoSuchAlgorithmException, NoSuchPaddingException {
        return getCipher(transformation, null);
    }

    public static final Cipher getInstance(String transformation, String provider) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {
        if (provider == null) {
            throw new IllegalArgumentException("provider == null");
        }
        Provider p = Security.getProvider(provider);
        if (p != null) {
            return getInstance(transformation, p);
        }
        throw new NoSuchProviderException("Provider not available: " + provider);
    }

    public static final Cipher getInstance(String transformation, Provider provider) throws NoSuchAlgorithmException, NoSuchPaddingException {
        if (provider != null) {
            return getCipher(transformation, provider);
        }
        throw new IllegalArgumentException("provider == null");
    }

    private static NoSuchAlgorithmException invalidTransformation(String transformation) throws NoSuchAlgorithmException {
        throw new NoSuchAlgorithmException("Invalid transformation: " + transformation);
    }

    private static Cipher getCipher(String transformation, Provider provider) throws NoSuchAlgorithmException, NoSuchPaddingException {
        if (transformation == null || transformation.isEmpty()) {
            throw invalidTransformation(transformation);
        }
        String[] transformParts = checkTransformation(transformation);
        if (tryCombinations(null, provider, transformParts) != null) {
            return new Cipher(transformation, transformParts, provider);
        }
        if (provider == null) {
            throw new NoSuchAlgorithmException("No provider found for " + transformation);
        }
        throw new NoSuchAlgorithmException("Provider " + provider.getName() + " does not provide " + transformation);
    }

    private static String[] checkTransformation(String transformation) throws NoSuchAlgorithmException {
        if (transformation.startsWith("/")) {
            transformation = transformation.substring(PUBLIC_KEY);
        }
        String[] pieces = transformation.split("/");
        if (pieces.length > WRAP_MODE) {
            throw invalidTransformation(transformation);
        }
        String[] result = new String[WRAP_MODE];
        for (int i = 0; i < pieces.length; i += PUBLIC_KEY) {
            String piece = pieces[i].trim();
            if (!piece.isEmpty()) {
                result[i] = piece;
            }
        }
        if (result[0] == null) {
            throw invalidTransformation(transformation);
        } else if ((result[PUBLIC_KEY] == null && result[PRIVATE_KEY] == null) || (result[PUBLIC_KEY] != null && result[PRIVATE_KEY] != null)) {
            return result;
        } else {
            throw invalidTransformation(transformation);
        }
    }

    private CipherSpi getSpi(Key key) {
        if (this.specifiedSpi != null) {
            return this.specifiedSpi;
        }
        synchronized (this.initLock) {
            if (this.spiImpl == null || key != null) {
                SpiAndProvider sap = tryCombinations(key, this.specifiedProvider, this.transformParts);
                if (sap == null) {
                    throw new ProviderException("No provider for " + this.transformation);
                }
                this.spiImpl = (CipherSpi) sap.spi;
                this.provider = sap.provider;
                CipherSpi cipherSpi = this.spiImpl;
                return cipherSpi;
            }
            cipherSpi = this.spiImpl;
            return cipherSpi;
        }
    }

    private CipherSpi getSpi() {
        return getSpi(null);
    }

    private static SpiAndProvider tryCombinations(Key key, Provider provider, String[] transformParts) {
        SpiAndProvider sap;
        if (!(transformParts[PUBLIC_KEY] == null || transformParts[PRIVATE_KEY] == null)) {
            sap = tryTransform(key, provider, transformParts[0] + "/" + transformParts[PUBLIC_KEY] + "/" + transformParts[PRIVATE_KEY], transformParts, NeedToSet.NONE);
            if (sap != null) {
                return sap;
            }
        }
        if (transformParts[PUBLIC_KEY] != null) {
            sap = tryTransform(key, provider, transformParts[0] + "/" + transformParts[PUBLIC_KEY], transformParts, NeedToSet.PADDING);
            if (sap != null) {
                return sap;
            }
        }
        if (transformParts[PRIVATE_KEY] != null) {
            sap = tryTransform(key, provider, transformParts[0] + "//" + transformParts[PRIVATE_KEY], transformParts, NeedToSet.MODE);
            if (sap != null) {
                return sap;
            }
        }
        return tryTransform(key, provider, transformParts[0], transformParts, NeedToSet.BOTH);
    }

    private static SpiAndProvider tryTransform(Key key, Provider provider, String transform, String[] transformParts, NeedToSet type) {
        if (provider != null) {
            Service service = provider.getService(SERVICE, transform);
            if (service == null) {
                return null;
            }
            return tryTransformWithProvider(key, transformParts, type, service);
        }
        ArrayList<Service> services = ENGINE.getServices(transform);
        if (services == null) {
            return null;
        }
        Iterator i$ = services.iterator();
        while (i$.hasNext()) {
            SpiAndProvider sap = tryTransformWithProvider(key, transformParts, type, (Service) i$.next());
            if (sap != null) {
                return sap;
            }
        }
        return null;
    }

    private static SpiAndProvider tryTransformWithProvider(Key key, String[] transformParts, NeedToSet type, Service service) {
        if (key != null) {
            try {
                if (!service.supportsParameter(key)) {
                    return null;
                }
            } catch (NoSuchAlgorithmException e) {
                return null;
            } catch (NoSuchPaddingException e2) {
                return null;
            }
        }
        if (!matchAttribute(service, ATTRIBUTE_MODES, transformParts[PUBLIC_KEY]) || !matchAttribute(service, ATTRIBUTE_PADDINGS, transformParts[PRIVATE_KEY])) {
            return null;
        }
        SpiAndProvider sap = ENGINE.getInstance(service, null);
        if (sap.spi == null || sap.provider == null) {
            return null;
        }
        if (!(sap.spi instanceof CipherSpi)) {
            return null;
        }
        CipherSpi spi = sap.spi;
        if ((type == NeedToSet.MODE || type == NeedToSet.BOTH) && transformParts[PUBLIC_KEY] != null) {
            spi.engineSetMode(transformParts[PUBLIC_KEY]);
        }
        if ((type != NeedToSet.PADDING && type != NeedToSet.BOTH) || transformParts[PRIVATE_KEY] == null) {
            return sap;
        }
        spi.engineSetPadding(transformParts[PRIVATE_KEY]);
        return sap;
    }

    private static boolean matchAttribute(Service service, String attr, String value) {
        if (value == null) {
            return true;
        }
        String pattern = service.getAttribute(attr);
        if (pattern != null) {
            return value.toUpperCase(Locale.US).matches(pattern.toUpperCase(Locale.US));
        }
        return true;
    }

    public final Provider getProvider() {
        getSpi();
        return this.provider;
    }

    public final String getAlgorithm() {
        return this.transformation;
    }

    public final int getBlockSize() {
        return getSpi().engineGetBlockSize();
    }

    public final int getOutputSize(int inputLen) {
        if (this.mode != 0) {
            return getSpi().engineGetOutputSize(inputLen);
        }
        throw new IllegalStateException("Cipher has not yet been initialized");
    }

    public final byte[] getIV() {
        return getSpi().engineGetIV();
    }

    public final AlgorithmParameters getParameters() {
        return getSpi().engineGetParameters();
    }

    public final ExemptionMechanism getExemptionMechanism() {
        return null;
    }

    private void checkMode(int mode) {
        if (mode != PUBLIC_KEY && mode != PRIVATE_KEY && mode != UNWRAP_MODE && mode != WRAP_MODE) {
            throw new InvalidParameterException("Invalid mode: " + mode);
        }
    }

    public final void init(int opmode, Key key) throws InvalidKeyException {
        if (secureRandom == null) {
            secureRandom = new SecureRandom();
        }
        init(opmode, key, secureRandom);
    }

    public final void init(int opmode, Key key, SecureRandom random) throws InvalidKeyException {
        checkMode(opmode);
        getSpi(key).engineInit(opmode, key, random);
        this.mode = opmode;
    }

    public final void init(int opmode, Key key, AlgorithmParameterSpec params) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (secureRandom == null) {
            secureRandom = new SecureRandom();
        }
        init(opmode, key, params, secureRandom);
    }

    public final void init(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        checkMode(opmode);
        getSpi(key).engineInit(opmode, key, params, random);
        this.mode = opmode;
    }

    public final void init(int opmode, Key key, AlgorithmParameters params) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (secureRandom == null) {
            secureRandom = new SecureRandom();
        }
        init(opmode, key, params, secureRandom);
    }

    public final void init(int opmode, Key key, AlgorithmParameters params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        checkMode(opmode);
        getSpi(key).engineInit(opmode, key, params, random);
        this.mode = opmode;
    }

    public final void init(int opmode, Certificate certificate) throws InvalidKeyException {
        if (secureRandom == null) {
            secureRandom = new SecureRandom();
        }
        init(opmode, certificate, secureRandom);
    }

    public final void init(int opmode, Certificate certificate, SecureRandom random) throws InvalidKeyException {
        checkMode(opmode);
        if (certificate instanceof X509Certificate) {
            Set<String> ce = ((X509Certificate) certificate).getCriticalExtensionOIDs();
            boolean critical = false;
            if (!(ce == null || ce.isEmpty())) {
                for (String oid : ce) {
                    if (oid.equals("2.5.29.15")) {
                        critical = true;
                        break;
                    }
                }
                if (critical) {
                    boolean[] keyUsage = ((X509Certificate) certificate).getKeyUsage();
                    if (keyUsage != null) {
                        if (opmode == PUBLIC_KEY && !keyUsage[WRAP_MODE]) {
                            throw new InvalidKeyException("The public key in the certificate cannot be used for ENCRYPT_MODE");
                        } else if (opmode == WRAP_MODE && !keyUsage[PRIVATE_KEY]) {
                            throw new InvalidKeyException("The public key in the certificate cannot be used for WRAP_MODE");
                        }
                    }
                }
            }
        }
        Key key = certificate.getPublicKey();
        getSpi(key).engineInit(opmode, key, random);
        this.mode = opmode;
    }

    public final byte[] update(byte[] input) {
        if (this.mode != PUBLIC_KEY && this.mode != PRIVATE_KEY) {
            throw new IllegalStateException();
        } else if (input == null) {
            throw new IllegalArgumentException("input == null");
        } else if (input.length == 0) {
            return null;
        } else {
            return getSpi().engineUpdate(input, 0, input.length);
        }
    }

    public final byte[] update(byte[] input, int inputOffset, int inputLen) {
        if (this.mode != PUBLIC_KEY && this.mode != PRIVATE_KEY) {
            throw new IllegalStateException();
        } else if (input == null) {
            throw new IllegalArgumentException("input == null");
        } else {
            checkInputOffsetAndCount(input.length, inputOffset, inputLen);
            if (input.length == 0) {
                return null;
            }
            return getSpi().engineUpdate(input, inputOffset, inputLen);
        }
    }

    private static void checkInputOffsetAndCount(int inputArrayLength, int inputOffset, int inputLen) {
        if ((inputOffset | inputLen) < 0 || inputOffset > inputArrayLength || inputArrayLength - inputOffset < inputLen) {
            throw new IllegalArgumentException("input.length=" + inputArrayLength + "; inputOffset=" + inputOffset + "; inputLen=" + inputLen);
        }
    }

    public final int update(byte[] input, int inputOffset, int inputLen, byte[] output) throws ShortBufferException {
        return update(input, inputOffset, inputLen, output, 0);
    }

    public final int update(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws ShortBufferException {
        if (this.mode != PUBLIC_KEY && this.mode != PRIVATE_KEY) {
            throw new IllegalStateException();
        } else if (input == null) {
            throw new IllegalArgumentException("input == null");
        } else if (output == null) {
            throw new IllegalArgumentException("output == null");
        } else if (outputOffset < 0) {
            throw new IllegalArgumentException("outputOffset < 0. outputOffset=" + outputOffset);
        } else {
            checkInputOffsetAndCount(input.length, inputOffset, inputLen);
            if (input.length == 0) {
                return 0;
            }
            return getSpi().engineUpdate(input, inputOffset, inputLen, output, outputOffset);
        }
    }

    public final int update(ByteBuffer input, ByteBuffer output) throws ShortBufferException {
        if (this.mode != PUBLIC_KEY && this.mode != PRIVATE_KEY) {
            throw new IllegalStateException();
        } else if (input != output) {
            return getSpi().engineUpdate(input, output);
        } else {
            throw new IllegalArgumentException("input == output");
        }
    }

    public final void updateAAD(byte[] input) {
        if (input == null) {
            throw new IllegalArgumentException("input == null");
        } else if (this.mode != PUBLIC_KEY && this.mode != PRIVATE_KEY) {
            throw new IllegalStateException();
        } else if (input.length != 0) {
            getSpi().engineUpdateAAD(input, 0, input.length);
        }
    }

    public final void updateAAD(byte[] input, int inputOffset, int inputLen) {
        if (this.mode != PUBLIC_KEY && this.mode != PRIVATE_KEY) {
            throw new IllegalStateException();
        } else if (input == null) {
            throw new IllegalArgumentException("input == null");
        } else {
            checkInputOffsetAndCount(input.length, inputOffset, inputLen);
            if (input.length != 0) {
                getSpi().engineUpdateAAD(input, inputOffset, inputLen);
            }
        }
    }

    public final void updateAAD(ByteBuffer input) {
        if (this.mode != PUBLIC_KEY && this.mode != PRIVATE_KEY) {
            throw new IllegalStateException("Cipher is not initialized");
        } else if (input == null) {
            throw new IllegalArgumentException("input == null");
        } else {
            getSpi().engineUpdateAAD(input);
        }
    }

    public final byte[] doFinal() throws IllegalBlockSizeException, BadPaddingException {
        if (this.mode == PUBLIC_KEY || this.mode == PRIVATE_KEY) {
            return getSpi().engineDoFinal(null, 0, 0);
        }
        throw new IllegalStateException();
    }

    public final int doFinal(byte[] output, int outputOffset) throws IllegalBlockSizeException, ShortBufferException, BadPaddingException {
        if (this.mode != PUBLIC_KEY && this.mode != PRIVATE_KEY) {
            throw new IllegalStateException();
        } else if (outputOffset >= 0) {
            return getSpi().engineDoFinal(null, 0, 0, output, outputOffset);
        } else {
            throw new IllegalArgumentException("outputOffset < 0. outputOffset=" + outputOffset);
        }
    }

    public final byte[] doFinal(byte[] input) throws IllegalBlockSizeException, BadPaddingException {
        if (this.mode == PUBLIC_KEY || this.mode == PRIVATE_KEY) {
            return getSpi().engineDoFinal(input, 0, input.length);
        }
        throw new IllegalStateException();
    }

    public final byte[] doFinal(byte[] input, int inputOffset, int inputLen) throws IllegalBlockSizeException, BadPaddingException {
        if (this.mode == PUBLIC_KEY || this.mode == PRIVATE_KEY) {
            checkInputOffsetAndCount(input.length, inputOffset, inputLen);
            return getSpi().engineDoFinal(input, inputOffset, inputLen);
        }
        throw new IllegalStateException();
    }

    public final int doFinal(byte[] input, int inputOffset, int inputLen, byte[] output) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        return doFinal(input, inputOffset, inputLen, output, 0);
    }

    public final int doFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        if (this.mode == PUBLIC_KEY || this.mode == PRIVATE_KEY) {
            checkInputOffsetAndCount(input.length, inputOffset, inputLen);
            return getSpi().engineDoFinal(input, inputOffset, inputLen, output, outputOffset);
        }
        throw new IllegalStateException();
    }

    public final int doFinal(ByteBuffer input, ByteBuffer output) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        if (this.mode != PUBLIC_KEY && this.mode != PRIVATE_KEY) {
            throw new IllegalStateException();
        } else if (input != output) {
            return getSpi().engineDoFinal(input, output);
        } else {
            throw new IllegalArgumentException("input == output");
        }
    }

    public final byte[] wrap(Key key) throws IllegalBlockSizeException, InvalidKeyException {
        if (this.mode == WRAP_MODE) {
            return getSpi().engineWrap(key);
        }
        throw new IllegalStateException();
    }

    public final Key unwrap(byte[] wrappedKey, String wrappedKeyAlgorithm, int wrappedKeyType) throws InvalidKeyException, NoSuchAlgorithmException {
        if (this.mode == UNWRAP_MODE) {
            return getSpi().engineUnwrap(wrappedKey, wrappedKeyAlgorithm, wrappedKeyType);
        }
        throw new IllegalStateException();
    }

    public static final int getMaxAllowedKeyLength(String transformation) throws NoSuchAlgorithmException {
        if (transformation == null) {
            throw new NullPointerException("transformation == null");
        }
        checkTransformation(transformation);
        return Integer.MAX_VALUE;
    }

    public static final AlgorithmParameterSpec getMaxAllowedParameterSpec(String transformation) throws NoSuchAlgorithmException {
        if (transformation == null) {
            throw new NullPointerException("transformation == null");
        }
        checkTransformation(transformation);
        return null;
    }
}
