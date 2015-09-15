package com.android.org.bouncycastle.crypto.paddings;

import com.android.org.bouncycastle.crypto.InvalidCipherTextException;
import java.security.SecureRandom;

public class PKCS7Padding implements BlockCipherPadding {
    public void init(SecureRandom random) throws IllegalArgumentException {
    }

    public String getPaddingName() {
        return "PKCS7";
    }

    public int addPadding(byte[] in, int inOff) {
        byte code = (byte) (in.length - inOff);
        while (inOff < in.length) {
            in[inOff] = code;
            inOff++;
        }
        return code;
    }

    public int padCount(byte[] in) throws InvalidCipherTextException {
        byte count = in[in.length - 1] & 255;
        if (count > in.length || count == (byte) 0) {
            throw new InvalidCipherTextException("pad block corrupted");
        }
        for (byte i = (byte) 1; i <= count; i++) {
            if (in[in.length - i] != count) {
                throw new InvalidCipherTextException("pad block corrupted");
            }
        }
        return count;
    }
}
