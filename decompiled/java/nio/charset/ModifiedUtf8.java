package java.nio.charset;

import dalvik.bytecode.Opcodes;
import java.io.UTFDataFormatException;
import java.nio.ByteOrder;
import libcore.io.Memory;
import org.apache.harmony.security.asn1.ASN1Constants;
import org.w3c.dom.traversal.NodeFilter;

public class ModifiedUtf8 {
    public static String decode(byte[] in, char[] out, int offset, int utfSize) throws UTFDataFormatException {
        int s = 0;
        int count = 0;
        while (count < utfSize) {
            int count2 = count + 1;
            char c = (char) in[offset + count];
            out[s] = c;
            if (c < '\u0080') {
                s++;
                count = count2;
            } else {
                int a = out[s];
                int b;
                int s2;
                if ((a & Opcodes.OP_SHL_INT_LIT8) == ASN1Constants.CLASS_PRIVATE) {
                    if (count2 >= utfSize) {
                        throw new UTFDataFormatException("bad second byte at " + count2);
                    }
                    count = count2 + 1;
                    b = in[offset + count2];
                    if ((b & ASN1Constants.CLASS_PRIVATE) != NodeFilter.SHOW_COMMENT) {
                        throw new UTFDataFormatException("bad second byte at " + (count - 1));
                    }
                    s2 = s + 1;
                    out[s] = (char) (((a & 31) << 6) | (b & 63));
                    s = s2;
                } else if ((a & Opcodes.OP_INVOKE_DIRECT_EMPTY) != Opcodes.OP_SHL_INT_LIT8) {
                    throw new UTFDataFormatException("bad byte at " + (count2 - 1));
                } else if (count2 + 1 >= utfSize) {
                    throw new UTFDataFormatException("bad third byte at " + (count2 + 1));
                } else {
                    count = count2 + 1;
                    b = in[offset + count2];
                    count2 = count + 1;
                    int c2 = in[offset + count];
                    if ((b & ASN1Constants.CLASS_PRIVATE) == NodeFilter.SHOW_COMMENT && (c2 & ASN1Constants.CLASS_PRIVATE) == NodeFilter.SHOW_COMMENT) {
                        s2 = s + 1;
                        out[s] = (char) ((((a & 15) << 12) | ((b & 63) << 6)) | (c2 & 63));
                        s = s2;
                        count = count2;
                    } else {
                        throw new UTFDataFormatException("bad second or third byte at " + (count2 - 2));
                    }
                }
            }
        }
        return new String(out, 0, s);
    }

    public static long countBytes(String s, boolean shortLength) throws UTFDataFormatException {
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
        int utfCount = (int) countBytes(s, true);
        byte[] result = new byte[(utfCount + 2)];
        Memory.pokeShort(result, 0, (short) utfCount, ByteOrder.BIG_ENDIAN);
        encode(result, 2, s);
        return result;
    }

    private ModifiedUtf8() {
    }
}
