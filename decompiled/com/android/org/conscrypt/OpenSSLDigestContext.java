package com.android.org.conscrypt;

public class OpenSSLDigestContext extends OpenSSLNativeReference {
    public OpenSSLDigestContext(long ctx) {
        super(ctx);
    }

    protected void finalize() throws Throwable {
        try {
            NativeCrypto.EVP_MD_CTX_destroy(this.context);
        } finally {
            super.finalize();
        }
    }
}
