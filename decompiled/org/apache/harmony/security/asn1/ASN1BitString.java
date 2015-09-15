package org.apache.harmony.security.asn1;

import java.io.IOException;
import libcore.util.EmptyArray;

public class ASN1BitString extends ASN1StringType {
    private static final ASN1BitString ASN1;

    public static class ASN1NamedBitList extends ASN1BitString {
        private static final int INDEFINITE_SIZE = -1;
        private static final byte[] SET_MASK;
        private static final BitString emptyString;
        private final int maxBits;
        private final int minBits;

        static {
            SET_MASK = new byte[]{Byte.MIN_VALUE, (byte) 64, (byte) 32, Character.FORMAT, (byte) 8, (byte) 4, (byte) 2, (byte) 1};
            emptyString = new BitString(EmptyArray.BYTE, 0);
        }

        public ASN1NamedBitList(int minBits) {
            this.minBits = minBits;
            this.maxBits = INDEFINITE_SIZE;
        }

        public Object getDecodedObject(BerInputStream in) throws IOException {
            boolean[] value;
            int unusedBits = in.buffer[in.contentOffset];
            int bitsNumber = ((in.length + INDEFINITE_SIZE) * 8) - unusedBits;
            if (this.maxBits == INDEFINITE_SIZE) {
                if (this.minBits == INDEFINITE_SIZE) {
                    value = new boolean[bitsNumber];
                } else if (bitsNumber > this.minBits) {
                    value = new boolean[bitsNumber];
                } else {
                    value = new boolean[this.minBits];
                }
            } else if (bitsNumber > this.maxBits) {
                throw new ASN1Exception("ASN.1 Named Bitstring: size constraints");
            } else {
                value = new boolean[this.maxBits];
            }
            if (bitsNumber != 0) {
                int k;
                boolean z;
                int i = 1;
                int j = 0;
                byte octet = in.buffer[in.contentOffset + 1];
                int size = in.length + INDEFINITE_SIZE;
                while (i < size) {
                    k = 0;
                    while (k < 8) {
                        if ((SET_MASK[k] & octet) != 0) {
                            z = true;
                        } else {
                            z = false;
                        }
                        value[j] = z;
                        k++;
                        j++;
                    }
                    i++;
                    octet = in.buffer[in.contentOffset + i];
                    i++;
                }
                k = 0;
                while (k < 8 - unusedBits) {
                    if ((SET_MASK[k] & octet) != 0) {
                        z = true;
                    } else {
                        z = false;
                    }
                    value[j] = z;
                    k++;
                    j++;
                }
            }
            return value;
        }

        public void setEncodingContent(BerOutputStream out) {
            boolean[] toEncode = (boolean[]) out.content;
            int index = toEncode.length + INDEFINITE_SIZE;
            while (index > INDEFINITE_SIZE && !toEncode[index]) {
                index += INDEFINITE_SIZE;
            }
            if (index == INDEFINITE_SIZE) {
                out.content = emptyString;
                out.length = 1;
                return;
            }
            int k;
            int unusedBits = 7 - (index % 8);
            byte[] bytes = new byte[((index / 8) + 1)];
            int j = 0;
            index = bytes.length + INDEFINITE_SIZE;
            for (int i = 0; i < index; i++) {
                k = 0;
                while (k < 8) {
                    if (toEncode[j]) {
                        bytes[i] = (byte) (bytes[i] | SET_MASK[k]);
                    }
                    k++;
                    j++;
                }
            }
            k = 0;
            while (k < 8 - unusedBits) {
                if (toEncode[j]) {
                    bytes[index] = (byte) (bytes[index] | SET_MASK[k]);
                }
                k++;
                j++;
            }
            out.content = new BitString(bytes, unusedBits);
            out.length = bytes.length + 1;
        }
    }

    static {
        ASN1 = new ASN1BitString();
    }

    public ASN1BitString() {
        super(3);
    }

    public static ASN1BitString getInstance() {
        return ASN1;
    }

    public Object decode(BerInputStream in) throws IOException {
        in.readBitString();
        if (in.isVerify) {
            return null;
        }
        return getDecodedObject(in);
    }

    public Object getDecodedObject(BerInputStream in) throws IOException {
        byte[] bytes = new byte[(in.length - 1)];
        System.arraycopy(in.buffer, in.contentOffset + 1, bytes, 0, in.length - 1);
        return new BitString(bytes, in.buffer[in.contentOffset]);
    }

    public void encodeContent(BerOutputStream out) {
        out.encodeBitString();
    }

    public void setEncodingContent(BerOutputStream out) {
        out.length = ((BitString) out.content).bytes.length + 1;
    }
}
