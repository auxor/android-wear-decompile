package com.android.dex;

import dalvik.bytecode.Opcodes;
import java.io.UTFDataFormatException;
import org.apache.harmony.security.asn1.ASN1Constants;
import org.w3c.dom.traversal.NodeFilter;

public final class Mutf8 {
    private Mutf8() {
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String decode(com.android.dex.util.ByteInput r8, char[] r9) throws java.io.UTFDataFormatException {
        /*
        r7 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
        r3 = 0;
    L_0x0003:
        r5 = r8.readByte();
        r5 = r5 & 255;
        r0 = (char) r5;
        if (r0 != 0) goto L_0x0013;
    L_0x000c:
        r5 = new java.lang.String;
        r6 = 0;
        r5.<init>(r9, r6, r3);
        return r5;
    L_0x0013:
        r9[r3] = r0;
        if (r0 >= r7) goto L_0x001a;
    L_0x0017:
        r3 = r3 + 1;
        goto L_0x0003;
    L_0x001a:
        r5 = r0 & 224;
        r6 = 192; // 0xc0 float:2.69E-43 double:9.5E-322;
        if (r5 != r6) goto L_0x0040;
    L_0x0020:
        r5 = r8.readByte();
        r1 = r5 & 255;
        r5 = r1 & 192;
        if (r5 == r7) goto L_0x0032;
    L_0x002a:
        r5 = new java.io.UTFDataFormatException;
        r6 = "bad second byte";
        r5.<init>(r6);
        throw r5;
    L_0x0032:
        r4 = r3 + 1;
        r5 = r0 & 31;
        r5 = r5 << 6;
        r6 = r1 & 63;
        r5 = r5 | r6;
        r5 = (char) r5;
        r9[r3] = r5;
        r3 = r4;
        goto L_0x0003;
    L_0x0040:
        r5 = r0 & 240;
        r6 = 224; // 0xe0 float:3.14E-43 double:1.107E-321;
        if (r5 != r6) goto L_0x0075;
    L_0x0046:
        r5 = r8.readByte();
        r1 = r5 & 255;
        r5 = r8.readByte();
        r2 = r5 & 255;
        r5 = r1 & 192;
        if (r5 != r7) goto L_0x005a;
    L_0x0056:
        r5 = r2 & 192;
        if (r5 == r7) goto L_0x0062;
    L_0x005a:
        r5 = new java.io.UTFDataFormatException;
        r6 = "bad second or third byte";
        r5.<init>(r6);
        throw r5;
    L_0x0062:
        r4 = r3 + 1;
        r5 = r0 & 15;
        r5 = r5 << 12;
        r6 = r1 & 63;
        r6 = r6 << 6;
        r5 = r5 | r6;
        r6 = r2 & 63;
        r5 = r5 | r6;
        r5 = (char) r5;
        r9[r3] = r5;
        r3 = r4;
        goto L_0x0003;
    L_0x0075:
        r5 = new java.io.UTFDataFormatException;
        r6 = "bad byte";
        r5.<init>(r6);
        throw r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.dex.Mutf8.decode(com.android.dex.util.ByteInput, char[]):java.lang.String");
    }

    private static long countBytes(String s, boolean shortLength) throws UTFDataFormatException {
        long result = 0;
        int length = s.length();
        int i = 0;
        while (i < length) {
            char ch = s.charAt(i);
            if (ch != '\u0000' && ch <= '\u007f') {
                result++;
            } else if (ch <= '\u07ff') {
                result += 2;
            } else {
                result += 3;
            }
            if (!shortLength || result <= 65535) {
                i++;
            } else {
                throw new UTFDataFormatException("String more than 65535 UTF bytes long");
            }
        }
        return result;
    }

    public static void encode(byte[] dst, int offset, String s) {
        int length = s.length();
        int i = 0;
        int offset2 = offset;
        while (i < length) {
            char ch = s.charAt(i);
            if (ch != '\u0000' && ch <= '\u007f') {
                offset = offset2 + 1;
                dst[offset2] = (byte) ch;
            } else if (ch <= '\u07ff') {
                offset = offset2 + 1;
                dst[offset2] = (byte) (((ch >> 6) & 31) | ASN1Constants.CLASS_PRIVATE);
                offset2 = offset + 1;
                dst[offset] = (byte) ((ch & 63) | NodeFilter.SHOW_COMMENT);
                offset = offset2;
            } else {
                offset = offset2 + 1;
                dst[offset2] = (byte) (((ch >> 12) & 15) | Opcodes.OP_SHL_INT_LIT8);
                offset2 = offset + 1;
                dst[offset] = (byte) (((ch >> 6) & 63) | NodeFilter.SHOW_COMMENT);
                offset = offset2 + 1;
                dst[offset2] = (byte) ((ch & 63) | NodeFilter.SHOW_COMMENT);
            }
            i++;
            offset2 = offset;
        }
    }

    public static byte[] encode(String s) throws UTFDataFormatException {
        byte[] result = new byte[((int) countBytes(s, true))];
        encode(result, 0, s);
        return result;
    }
}
