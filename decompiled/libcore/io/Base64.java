package libcore.io;

import dalvik.bytecode.Opcodes;
import java.io.ObjectStreamConstants;
import java.nio.charset.StandardCharsets;
import libcore.util.EmptyArray;
import org.w3c.dom.traversal.NodeFilter;

public final class Base64 {
    private static final byte[] map;

    private Base64() {
    }

    public static byte[] decode(byte[] in) {
        return decode(in, in.length);
    }

    public static byte[] decode(byte[] in, int len) {
        int length = (len / 4) * 3;
        if (length == 0) {
            return EmptyArray.BYTE;
        }
        int outIndex;
        byte[] result;
        byte[] out = new byte[length];
        int pad = 0;
        while (true) {
            byte chr = in[len - 1];
            if (!(chr == 10 || chr == 13 || chr == 32 || chr == 9)) {
                if (chr != 61) {
                    break;
                }
                pad++;
            }
            len--;
        }
        int inIndex = 0;
        int quantum = 0;
        int i = 0;
        int outIndex2 = 0;
        while (i < len) {
            chr = in[i];
            if (chr == 10 || chr == 13 || chr == 32) {
                outIndex = outIndex2;
            } else if (chr == 9) {
                outIndex = outIndex2;
            } else {
                int bits;
                if (chr >= 65 && chr <= 90) {
                    bits = chr - 65;
                } else if (chr >= 97 && chr <= 122) {
                    bits = chr - 71;
                } else if (chr >= 48 && chr <= 57) {
                    bits = chr + 4;
                } else if (chr == 43) {
                    bits = 62;
                } else if (chr != 47) {
                    return null;
                } else {
                    bits = 63;
                }
                quantum = (quantum << 6) | ((byte) bits);
                if (inIndex % 4 == 3) {
                    outIndex = outIndex2 + 1;
                    out[outIndex2] = (byte) (quantum >> 16);
                    outIndex2 = outIndex + 1;
                    out[outIndex] = (byte) (quantum >> 8);
                    outIndex = outIndex2 + 1;
                    out[outIndex2] = (byte) quantum;
                } else {
                    outIndex = outIndex2;
                }
                inIndex++;
            }
            i++;
            outIndex2 = outIndex;
        }
        if (pad > 0) {
            quantum <<= pad * 6;
            outIndex = outIndex2 + 1;
            out[outIndex2] = (byte) (quantum >> 16);
            if (pad == 1) {
                outIndex2 = outIndex + 1;
                out[outIndex] = (byte) (quantum >> 8);
            }
            result = new byte[outIndex];
            System.arraycopy(out, 0, result, 0, outIndex);
            return result;
        }
        outIndex = outIndex2;
        result = new byte[outIndex];
        System.arraycopy(out, 0, result, 0, outIndex);
        return result;
    }

    static {
        map = new byte[]{(byte) 65, (byte) 66, (byte) 67, (byte) 68, (byte) 69, (byte) 70, (byte) 71, (byte) 72, (byte) 73, (byte) 74, (byte) 75, (byte) 76, (byte) 77, (byte) 78, (byte) 79, (byte) 80, (byte) 81, (byte) 82, (byte) 83, (byte) 84, (byte) 85, (byte) 86, (byte) 87, (byte) 88, (byte) 89, (byte) 90, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102, (byte) 103, (byte) 104, (byte) 105, (byte) 106, (byte) 107, (byte) 108, (byte) 109, (byte) 110, (byte) 111, ObjectStreamConstants.TC_NULL, ObjectStreamConstants.TC_REFERENCE, ObjectStreamConstants.TC_CLASSDESC, ObjectStreamConstants.TC_OBJECT, ObjectStreamConstants.TC_STRING, ObjectStreamConstants.TC_ARRAY, ObjectStreamConstants.TC_CLASS, ObjectStreamConstants.TC_BLOCKDATA, ObjectStreamConstants.TC_ENDBLOCKDATA, ObjectStreamConstants.TC_RESET, ObjectStreamConstants.TC_BLOCKDATALONG, (byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 43, (byte) 47};
    }

    public static String encode(byte[] in) {
        int i;
        byte[] out = new byte[(((in.length + 2) * 4) / 3)];
        int end = in.length - (in.length % 3);
        int index = 0;
        for (int i2 = 0; i2 < end; i2 += 3) {
            i = index + 1;
            out[index] = map[(in[i2] & Opcodes.OP_CONST_CLASS_JUMBO) >> 2];
            index = i + 1;
            out[i] = map[((in[i2] & 3) << 4) | ((in[i2 + 1] & Opcodes.OP_CONST_CLASS_JUMBO) >> 4)];
            i = index + 1;
            out[index] = map[((in[i2 + 1] & 15) << 2) | ((in[i2 + 2] & Opcodes.OP_CONST_CLASS_JUMBO) >> 6)];
            index = i + 1;
            out[i] = map[in[i2 + 2] & 63];
        }
        switch (in.length % 3) {
            case NodeFilter.SHOW_ELEMENT /*1*/:
                i = index + 1;
                out[index] = map[(in[end] & Opcodes.OP_CONST_CLASS_JUMBO) >> 2];
                index = i + 1;
                out[i] = map[(in[end] & 3) << 4];
                i = index + 1;
                out[index] = (byte) 61;
                index = i + 1;
                out[i] = (byte) 61;
                i = index;
                break;
            case NodeFilter.SHOW_ATTRIBUTE /*2*/:
                i = index + 1;
                out[index] = map[(in[end] & Opcodes.OP_CONST_CLASS_JUMBO) >> 2];
                index = i + 1;
                out[i] = map[((in[end] & 3) << 4) | ((in[end + 1] & Opcodes.OP_CONST_CLASS_JUMBO) >> 4)];
                i = index + 1;
                out[index] = map[(in[end + 1] & 15) << 2];
                index = i + 1;
                out[i] = (byte) 61;
                break;
        }
        i = index;
        return new String(out, 0, i, StandardCharsets.US_ASCII);
    }
}
