package com.android.org.conscrypt;

import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.SignatureSpi;

public class OpenSSLSignature extends SignatureSpi {
    private OpenSSLDigestContext ctx;
    private final EngineType engineType;
    private final long evpAlgorithm;
    private OpenSSLKey key;
    private boolean signing;
    private final byte[] singleByte;

    /* renamed from: com.android.org.conscrypt.OpenSSLSignature.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$conscrypt$OpenSSLSignature$EngineType;

        static {
            $SwitchMap$org$conscrypt$OpenSSLSignature$EngineType = new int[EngineType.values().length];
            try {
                $SwitchMap$org$conscrypt$OpenSSLSignature$EngineType[EngineType.RSA.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$conscrypt$OpenSSLSignature$EngineType[EngineType.DSA.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$org$conscrypt$OpenSSLSignature$EngineType[EngineType.EC.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private enum EngineType {
        RSA,
        DSA,
        EC
    }

    public static final class MD5RSA extends OpenSSLSignature {
        private static final long EVP_MD;

        static {
            EVP_MD = NativeCrypto.EVP_get_digestbyname("RSA-MD5");
        }

        public MD5RSA() throws NoSuchAlgorithmException {
            super(EngineType.RSA, null);
        }
    }

    public static final class SHA1DSA extends OpenSSLSignature {
        private static final long EVP_MD;

        static {
            EVP_MD = NativeCrypto.EVP_get_digestbyname("DSA-SHA1");
        }

        public SHA1DSA() throws NoSuchAlgorithmException {
            super(EngineType.DSA, null);
        }
    }

    public static final class SHA1ECDSA extends OpenSSLSignature {
        private static final long EVP_MD;

        static {
            EVP_MD = NativeCrypto.EVP_get_digestbyname("SHA1");
        }

        public SHA1ECDSA() throws NoSuchAlgorithmException {
            super(EngineType.EC, null);
        }
    }

    public static final class SHA1RSA extends OpenSSLSignature {
        private static final long EVP_MD;

        static {
            EVP_MD = NativeCrypto.EVP_get_digestbyname("RSA-SHA1");
        }

        public SHA1RSA() throws NoSuchAlgorithmException {
            super(EngineType.RSA, null);
        }
    }

    public static final class SHA224ECDSA extends OpenSSLSignature {
        private static final long EVP_MD;

        static {
            EVP_MD = NativeCrypto.EVP_get_digestbyname("SHA224");
        }

        public SHA224ECDSA() throws NoSuchAlgorithmException {
            super(EngineType.EC, null);
        }
    }

    public static final class SHA224RSA extends OpenSSLSignature {
        private static final long EVP_MD;

        static {
            EVP_MD = NativeCrypto.EVP_get_digestbyname("RSA-SHA224");
        }

        public SHA224RSA() throws NoSuchAlgorithmException {
            super(EngineType.RSA, null);
        }
    }

    public static final class SHA256ECDSA extends OpenSSLSignature {
        private static final long EVP_MD;

        static {
            EVP_MD = NativeCrypto.EVP_get_digestbyname("SHA256");
        }

        public SHA256ECDSA() throws NoSuchAlgorithmException {
            super(EngineType.EC, null);
        }
    }

    public static final class SHA256RSA extends OpenSSLSignature {
        private static final long EVP_MD;

        static {
            EVP_MD = NativeCrypto.EVP_get_digestbyname("RSA-SHA256");
        }

        public SHA256RSA() throws NoSuchAlgorithmException {
            super(EngineType.RSA, null);
        }
    }

    public static final class SHA384ECDSA extends OpenSSLSignature {
        private static final long EVP_MD;

        static {
            EVP_MD = NativeCrypto.EVP_get_digestbyname("SHA384");
        }

        public SHA384ECDSA() throws NoSuchAlgorithmException {
            super(EngineType.EC, null);
        }
    }

    public static final class SHA384RSA extends OpenSSLSignature {
        private static final long EVP_MD;

        static {
            EVP_MD = NativeCrypto.EVP_get_digestbyname("RSA-SHA384");
        }

        public SHA384RSA() throws NoSuchAlgorithmException {
            super(EngineType.RSA, null);
        }
    }

    public static final class SHA512ECDSA extends OpenSSLSignature {
        private static final long EVP_MD;

        static {
            EVP_MD = NativeCrypto.EVP_get_digestbyname("SHA512");
        }

        public SHA512ECDSA() throws NoSuchAlgorithmException {
            super(EngineType.EC, null);
        }
    }

    public static final class SHA512RSA extends OpenSSLSignature {
        private static final long EVP_MD;

        static {
            EVP_MD = NativeCrypto.EVP_get_digestbyname("RSA-SHA512");
        }

        public SHA512RSA() throws NoSuchAlgorithmException {
            super(EngineType.RSA, null);
        }
    }

    protected boolean engineVerify(byte[] r11) throws java.security.SignatureException {
        /* JADX: method processing error */
/*
        Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.ssa.SSATransform.placePhi(SSATransform.java:82)
	at jadx.core.dex.visitors.ssa.SSATransform.process(SSATransform.java:50)
	at jadx.core.dex.visitors.ssa.SSATransform.visit(SSATransform.java:42)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
        /*
        r10 = this;
        r8 = 1;
        r9 = 0;
        r0 = r10.key;
        if (r0 != 0) goto L_0x000e;
    L_0x0006:
        r0 = new java.security.SignatureException;
        r1 = "Need DSA or RSA public key";
        r0.<init>(r1);
        throw r0;
    L_0x000e:
        r0 = r10.ctx;	 Catch:{ Exception -> 0x0026, all -> 0x002c }
        r2 = 0;	 Catch:{ Exception -> 0x0026, all -> 0x002c }
        r3 = r11.length;	 Catch:{ Exception -> 0x0026, all -> 0x002c }
        r1 = r10.key;	 Catch:{ Exception -> 0x0026, all -> 0x002c }
        r4 = r1.getPkeyContext();	 Catch:{ Exception -> 0x0026, all -> 0x002c }
        r1 = r11;	 Catch:{ Exception -> 0x0026, all -> 0x002c }
        r7 = com.android.org.conscrypt.NativeCrypto.EVP_VerifyFinal(r0, r1, r2, r3, r4);	 Catch:{ Exception -> 0x0026, all -> 0x002c }
        if (r7 != r8) goto L_0x0024;
    L_0x001f:
        r0 = r8;
    L_0x0020:
        r10.resetContext();
    L_0x0023:
        return r0;
    L_0x0024:
        r0 = r9;
        goto L_0x0020;
    L_0x0026:
        r6 = move-exception;
        r10.resetContext();
        r0 = r9;
        goto L_0x0023;
    L_0x002c:
        r0 = move-exception;
        r10.resetContext();
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.org.conscrypt.OpenSSLSignature.engineVerify(byte[]):boolean");
    }

    private OpenSSLSignature(long algorithm, EngineType engineType) throws NoSuchAlgorithmException {
        this.singleByte = new byte[1];
        this.engineType = engineType;
        this.evpAlgorithm = algorithm;
    }

    private final void resetContext() {
        OpenSSLDigestContext ctxLocal = new OpenSSLDigestContext(NativeCrypto.EVP_MD_CTX_create());
        NativeCrypto.EVP_MD_CTX_init(ctxLocal);
        if (this.signing) {
            enableDSASignatureNonceHardeningIfApplicable();
            NativeCrypto.EVP_SignInit(ctxLocal, this.evpAlgorithm);
        } else {
            NativeCrypto.EVP_VerifyInit(ctxLocal, this.evpAlgorithm);
        }
        this.ctx = ctxLocal;
    }

    protected void engineUpdate(byte input) {
        this.singleByte[0] = input;
        engineUpdate(this.singleByte, 0, 1);
    }

    protected void engineUpdate(byte[] input, int offset, int len) {
        OpenSSLDigestContext ctxLocal = this.ctx;
        if (this.signing) {
            NativeCrypto.EVP_SignUpdate(ctxLocal, input, offset, len);
        } else {
            NativeCrypto.EVP_VerifyUpdate(ctxLocal, input, offset, len);
        }
    }

    protected Object engineGetParameter(String param) throws InvalidParameterException {
        return null;
    }

    private void checkEngineType(OpenSSLKey pkey) throws InvalidKeyException {
        int pkeyType = NativeCrypto.EVP_PKEY_type(pkey.getPkeyContext());
        switch (AnonymousClass1.$SwitchMap$org$conscrypt$OpenSSLSignature$EngineType[this.engineType.ordinal()]) {
            case NativeCrypto.SSL_kRSA /*1*/:
                if (pkeyType != 6) {
                    throw new InvalidKeyException("Signature initialized as " + this.engineType + " (not RSA)");
                }
            case NativeCrypto.SSL_kDHr /*2*/:
                if (pkeyType != NativeCrypto.EVP_PKEY_DSA) {
                    throw new InvalidKeyException("Signature initialized as " + this.engineType + " (not DSA)");
                }
            case NativeCrypto.SSL_ST_OK /*3*/:
                if (pkeyType != NativeCrypto.EVP_PKEY_EC) {
                    throw new InvalidKeyException("Signature initialized as " + this.engineType + " (not EC)");
                }
            default:
                throw new InvalidKeyException("Key must be of type " + this.engineType);
        }
    }

    private void initInternal(OpenSSLKey newKey, boolean signing) throws InvalidKeyException {
        checkEngineType(newKey);
        this.key = newKey;
        this.signing = signing;
        resetContext();
    }

    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        initInternal(OpenSSLKey.fromPrivateKey(privateKey), true);
    }

    private void enableDSASignatureNonceHardeningIfApplicable() {
        OpenSSLKey key = this.key;
        switch (AnonymousClass1.$SwitchMap$org$conscrypt$OpenSSLSignature$EngineType[this.engineType.ordinal()]) {
            case NativeCrypto.SSL_kDHr /*2*/:
                NativeCrypto.set_DSA_flag_nonce_from_hash(key.getPkeyContext());
            case NativeCrypto.SSL_ST_OK /*3*/:
                NativeCrypto.EC_KEY_set_nonce_from_hash(key.getPkeyContext(), true);
            default:
        }
    }

    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        initInternal(OpenSSLKey.fromPublicKey(publicKey), false);
    }

    protected void engineSetParameter(String param, Object value) throws InvalidParameterException {
    }

    protected byte[] engineSign() throws SignatureException {
        if (this.key == null) {
            throw new SignatureException("Need DSA or RSA or EC private key");
        }
        try {
            byte[] buffer = new byte[NativeCrypto.EVP_PKEY_size(this.key.getPkeyContext())];
            int bytesWritten = NativeCrypto.EVP_SignFinal(this.ctx, buffer, 0, this.key.getPkeyContext());
            byte[] signature = new byte[bytesWritten];
            System.arraycopy(buffer, 0, signature, 0, bytesWritten);
            resetContext();
            return signature;
        } catch (Exception ex) {
            throw new SignatureException(ex);
        } catch (Throwable th) {
            resetContext();
        }
    }
}
