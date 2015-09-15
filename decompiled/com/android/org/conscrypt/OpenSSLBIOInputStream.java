package com.android.org.conscrypt;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class OpenSSLBIOInputStream extends FilterInputStream {
    private long ctx;

    public OpenSSLBIOInputStream(InputStream is) {
        super(is);
        this.ctx = NativeCrypto.create_BIO_InputStream(this);
    }

    public long getBioContext() {
        return this.ctx;
    }

    public void release() {
        NativeCrypto.BIO_free_all(this.ctx);
    }

    public int gets(byte[] buffer) throws IOException {
        if (buffer == null || buffer.length == 0) {
            return 0;
        }
        int offset = 0;
        while (offset < buffer.length) {
            int inputByte = read();
            if (inputByte == -1) {
                return offset;
            }
            if (inputByte != 10) {
                int offset2 = offset + 1;
                buffer[offset] = (byte) inputByte;
                offset = offset2;
            } else if (offset != 0) {
                return offset;
            }
        }
        return offset;
    }
}
