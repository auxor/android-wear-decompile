package com.android.org.bouncycastle.crypto.engines;

import com.android.org.bouncycastle.asn1.x509.ReasonFlags;
import com.android.org.bouncycastle.crypto.BlockCipher;

public class RC2Engine implements BlockCipher {
    private static final int BLOCK_SIZE = 8;
    private static byte[] piTable;
    private boolean encrypting;
    private int[] workingKey;

    private void decryptBlock(byte[] r1, int r2, byte[] r3, int r4) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.bouncycastle.crypto.engines.RC2Engine.decryptBlock(byte[], int, byte[], int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.bouncycastle.crypto.engines.RC2Engine.decryptBlock(byte[], int, byte[], int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.crypto.engines.RC2Engine.decryptBlock(byte[], int, byte[], int):void");
    }

    private void encryptBlock(byte[] r1, int r2, byte[] r3, int r4) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.bouncycastle.crypto.engines.RC2Engine.encryptBlock(byte[], int, byte[], int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.bouncycastle.crypto.engines.RC2Engine.encryptBlock(byte[], int, byte[], int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.crypto.engines.RC2Engine.encryptBlock(byte[], int, byte[], int):void");
    }

    public void init(boolean r1, com.android.org.bouncycastle.crypto.CipherParameters r2) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.bouncycastle.crypto.engines.RC2Engine.init(boolean, com.android.org.bouncycastle.crypto.CipherParameters):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.bouncycastle.crypto.engines.RC2Engine.init(boolean, com.android.org.bouncycastle.crypto.CipherParameters):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.crypto.engines.RC2Engine.init(boolean, com.android.org.bouncycastle.crypto.CipherParameters):void");
    }

    public final int processBlock(byte[] r1, int r2, byte[] r3, int r4) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.bouncycastle.crypto.engines.RC2Engine.processBlock(byte[], int, byte[], int):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.bouncycastle.crypto.engines.RC2Engine.processBlock(byte[], int, byte[], int):int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.crypto.engines.RC2Engine.processBlock(byte[], int, byte[], int):int");
    }

    static {
        piTable = new byte[]{(byte) -39, (byte) 120, (byte) -7, (byte) -60, (byte) 25, (byte) -35, (byte) -75, (byte) -19, (byte) 40, (byte) -23, (byte) -3, (byte) 121, (byte) 74, (byte) -96, (byte) -40, (byte) -99, (byte) -58, (byte) 126, (byte) 55, (byte) -125, (byte) 43, (byte) 118, (byte) 83, (byte) -114, (byte) 98, (byte) 76, (byte) 100, (byte) -120, (byte) 68, (byte) -117, (byte) -5, (byte) -94, (byte) 23, (byte) -102, (byte) 89, (byte) -11, (byte) -121, (byte) -77, (byte) 79, (byte) 19, (byte) 97, (byte) 69, (byte) 109, (byte) -115, (byte) 9, (byte) -127, (byte) 125, (byte) 50, (byte) -67, (byte) -113, (byte) 64, (byte) -21, (byte) -122, (byte) -73, (byte) 123, (byte) 11, (byte) -16, (byte) -107, (byte) 33, (byte) 34, (byte) 92, (byte) 107, (byte) 78, (byte) -126, (byte) 84, (byte) -42, (byte) 101, (byte) -109, (byte) -50, (byte) 96, (byte) -78, (byte) 28, (byte) 115, (byte) 86, (byte) -64, (byte) 20, (byte) -89, (byte) -116, (byte) -15, (byte) -36, (byte) 18, (byte) 117, (byte) -54, (byte) 31, (byte) 59, (byte) -66, (byte) -28, (byte) -47, (byte) 66, (byte) 61, (byte) -44, (byte) 48, (byte) -93, (byte) 60, (byte) -74, (byte) 38, (byte) 111, (byte) -65, (byte) 14, (byte) -38, (byte) 70, (byte) 105, (byte) 7, (byte) 87, (byte) 39, (byte) -14, (byte) 29, (byte) -101, (byte) -68, (byte) -108, (byte) 67, (byte) 3, (byte) -8, (byte) 17, (byte) -57, (byte) -10, (byte) -112, (byte) -17, (byte) 62, (byte) -25, (byte) 6, (byte) -61, (byte) -43, (byte) 47, (byte) -56, (byte) 102, (byte) 30, (byte) -41, (byte) 8, (byte) -24, (byte) -22, (byte) -34, Byte.MIN_VALUE, (byte) 82, (byte) -18, (byte) -9, (byte) -124, (byte) -86, (byte) 114, (byte) -84, (byte) 53, (byte) 77, (byte) 106, (byte) 42, (byte) -106, (byte) 26, (byte) -46, (byte) 113, (byte) 90, (byte) 21, (byte) 73, (byte) 116, (byte) 75, (byte) -97, (byte) -48, (byte) 94, (byte) 4, (byte) 24, (byte) -92, (byte) -20, (byte) -62, (byte) -32, (byte) 65, (byte) 110, (byte) 15, (byte) 81, (byte) -53, (byte) -52, (byte) 36, (byte) -111, (byte) -81, (byte) 80, (byte) -95, (byte) -12, (byte) 112, (byte) 57, (byte) -103, (byte) 124, (byte) 58, (byte) -123, (byte) 35, (byte) -72, (byte) -76, (byte) 122, (byte) -4, (byte) 2, (byte) 54, (byte) 91, (byte) 37, (byte) 85, (byte) -105, (byte) 49, (byte) 45, (byte) 93, (byte) -6, (byte) -104, (byte) -29, (byte) -118, (byte) -110, (byte) -82, (byte) 5, (byte) -33, (byte) 41, Tnaf.POW_2_WIDTH, (byte) 103, (byte) 108, (byte) -70, (byte) -55, (byte) -45, (byte) 0, (byte) -26, (byte) -49, (byte) -31, (byte) -98, (byte) -88, (byte) 44, (byte) 99, (byte) 22, (byte) 1, (byte) 63, (byte) 88, (byte) -30, (byte) -119, (byte) -87, (byte) 13, (byte) 56, (byte) 52, (byte) 27, (byte) -85, (byte) 51, (byte) -1, (byte) -80, (byte) -69, (byte) 72, (byte) 12, (byte) 95, (byte) -71, (byte) -79, (byte) -51, (byte) 46, (byte) -59, (byte) -13, (byte) -37, (byte) 71, (byte) -27, (byte) -91, (byte) -100, (byte) 119, (byte) 10, (byte) -90, (byte) 32, (byte) 104, (byte) -2, Byte.MAX_VALUE, (byte) -63, (byte) -83};
    }

    private int[] generateWorkingKey(byte[] key, int bits) {
        int i;
        int x;
        int[] xKey = new int[ReasonFlags.unused];
        for (i = 0; i != key.length; i++) {
            xKey[i] = key[i] & 255;
        }
        int len = key.length;
        if (len < ReasonFlags.unused) {
            int len2;
            int index = 0;
            x = xKey[len - 1];
            while (true) {
                int index2 = index + 1;
                x = piTable[(xKey[index] + x) & 255] & 255;
                len2 = len + 1;
                xKey[len] = x;
                if (len2 >= ReasonFlags.unused) {
                    break;
                }
                index = index2;
                len = len2;
            }
            len = len2;
        }
        len = (bits + 7) >> 3;
        x = piTable[xKey[128 - len] & (255 >> ((-bits) & 7))] & 255;
        xKey[128 - len] = x;
        for (i = (128 - len) - 1; i >= 0; i--) {
            x = piTable[xKey[i + len] ^ x] & 255;
            xKey[i] = x;
        }
        int[] newKey = new int[64];
        for (i = 0; i != newKey.length; i++) {
            newKey[i] = xKey[i * 2] + (xKey[(i * 2) + 1] << BLOCK_SIZE);
        }
        return newKey;
    }

    public void reset() {
    }

    public String getAlgorithmName() {
        return "RC2";
    }

    public int getBlockSize() {
        return BLOCK_SIZE;
    }

    private int rotateWordLeft(int x, int y) {
        x &= 65535;
        return (x << y) | (x >> (16 - y));
    }
}
