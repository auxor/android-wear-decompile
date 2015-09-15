package com.android.org.conscrypt;

public abstract class OpenSSLNativeReference {
    final long context;

    public OpenSSLNativeReference(long ctx) {
        if (ctx == 0) {
            throw new NullPointerException("ctx == 0");
        }
        this.context = ctx;
    }
}
