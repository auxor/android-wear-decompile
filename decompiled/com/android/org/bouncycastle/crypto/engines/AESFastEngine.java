package com.android.org.bouncycastle.crypto.engines;

import com.android.org.bouncycastle.asn1.x509.ReasonFlags;
import com.android.org.bouncycastle.crypto.BlockCipher;

public class AESFastEngine implements BlockCipher {
    private static final int BLOCK_SIZE = 16;
    private static final byte[] S;
    private static final byte[] Si;
    private static final int[] T0;
    private static final int[] T1;
    private static final int[] T2;
    private static final int[] T3;
    private static final int[] Tinv0;
    private static final int[] Tinv1;
    private static final int[] Tinv2;
    private static final int[] Tinv3;
    private static final int m1 = -2139062144;
    private static final int m2 = 2139062143;
    private static final int m3 = 27;
    private static final int[] rcon;
    private int C0;
    private int C1;
    private int C2;
    private int C3;
    private int ROUNDS;
    private int[][] WorkingKey;
    private boolean forEncryption;

    public AESFastEngine() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.bouncycastle.crypto.engines.AESFastEngine.<init>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.bouncycastle.crypto.engines.AESFastEngine.<init>():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.crypto.engines.AESFastEngine.<init>():void");
    }

    private void decryptBlock(int[][] r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.bouncycastle.crypto.engines.AESFastEngine.decryptBlock(int[][]):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.bouncycastle.crypto.engines.AESFastEngine.decryptBlock(int[][]):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.crypto.engines.AESFastEngine.decryptBlock(int[][]):void");
    }

    private void encryptBlock(int[][] r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.bouncycastle.crypto.engines.AESFastEngine.encryptBlock(int[][]):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.bouncycastle.crypto.engines.AESFastEngine.encryptBlock(int[][]):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.crypto.engines.AESFastEngine.encryptBlock(int[][]):void");
    }

    private int[][] generateWorkingKey(byte[] r1, boolean r2) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.bouncycastle.crypto.engines.AESFastEngine.generateWorkingKey(byte[], boolean):int[][]
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.bouncycastle.crypto.engines.AESFastEngine.generateWorkingKey(byte[], boolean):int[][]
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.crypto.engines.AESFastEngine.generateWorkingKey(byte[], boolean):int[][]");
    }

    private void packBlock(byte[] r1, int r2) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.bouncycastle.crypto.engines.AESFastEngine.packBlock(byte[], int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.bouncycastle.crypto.engines.AESFastEngine.packBlock(byte[], int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.crypto.engines.AESFastEngine.packBlock(byte[], int):void");
    }

    private void unpackBlock(byte[] r1, int r2) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.bouncycastle.crypto.engines.AESFastEngine.unpackBlock(byte[], int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.bouncycastle.crypto.engines.AESFastEngine.unpackBlock(byte[], int):void
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.crypto.engines.AESFastEngine.unpackBlock(byte[], int):void");
    }

    public void init(boolean r1, com.android.org.bouncycastle.crypto.CipherParameters r2) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.bouncycastle.crypto.engines.AESFastEngine.init(boolean, com.android.org.bouncycastle.crypto.CipherParameters):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.bouncycastle.crypto.engines.AESFastEngine.init(boolean, com.android.org.bouncycastle.crypto.CipherParameters):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.crypto.engines.AESFastEngine.init(boolean, com.android.org.bouncycastle.crypto.CipherParameters):void");
    }

    public int processBlock(byte[] r1, int r2, byte[] r3, int r4) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.bouncycastle.crypto.engines.AESFastEngine.processBlock(byte[], int, byte[], int):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.bouncycastle.crypto.engines.AESFastEngine.processBlock(byte[], int, byte[], int):int
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.crypto.engines.AESFastEngine.processBlock(byte[], int, byte[], int):int");
    }

    static {
        S = new byte[]{(byte) 99, (byte) 124, (byte) 119, (byte) 123, (byte) -14, (byte) 107, (byte) 111, (byte) -59, (byte) 48, (byte) 1, (byte) 103, (byte) 43, (byte) -2, (byte) -41, (byte) -85, (byte) 118, (byte) -54, (byte) -126, (byte) -55, (byte) 125, (byte) -6, (byte) 89, (byte) 71, (byte) -16, (byte) -83, (byte) -44, (byte) -94, (byte) -81, (byte) -100, (byte) -92, (byte) 114, (byte) -64, (byte) -73, (byte) -3, (byte) -109, (byte) 38, (byte) 54, (byte) 63, (byte) -9, (byte) -52, (byte) 52, (byte) -91, (byte) -27, (byte) -15, (byte) 113, (byte) -40, (byte) 49, (byte) 21, (byte) 4, (byte) -57, (byte) 35, (byte) -61, (byte) 24, (byte) -106, (byte) 5, (byte) -102, (byte) 7, (byte) 18, Byte.MIN_VALUE, (byte) -30, (byte) -21, (byte) 39, (byte) -78, (byte) 117, (byte) 9, (byte) -125, (byte) 44, (byte) 26, (byte) 27, (byte) 110, (byte) 90, (byte) -96, (byte) 82, (byte) 59, (byte) -42, (byte) -77, (byte) 41, (byte) -29, (byte) 47, (byte) -124, (byte) 83, (byte) -47, (byte) 0, (byte) -19, (byte) 32, (byte) -4, (byte) -79, (byte) 91, (byte) 106, (byte) -53, (byte) -66, (byte) 57, (byte) 74, (byte) 76, (byte) 88, (byte) -49, (byte) -48, (byte) -17, (byte) -86, (byte) -5, (byte) 67, (byte) 77, (byte) 51, (byte) -123, (byte) 69, (byte) -7, (byte) 2, Byte.MAX_VALUE, (byte) 80, (byte) 60, (byte) -97, (byte) -88, (byte) 81, (byte) -93, (byte) 64, (byte) -113, (byte) -110, (byte) -99, (byte) 56, (byte) -11, (byte) -68, (byte) -74, (byte) -38, (byte) 33, Tnaf.POW_2_WIDTH, (byte) -1, (byte) -13, (byte) -46, (byte) -51, (byte) 12, (byte) 19, (byte) -20, (byte) 95, (byte) -105, (byte) 68, (byte) 23, (byte) -60, (byte) -89, (byte) 126, (byte) 61, (byte) 100, (byte) 93, (byte) 25, (byte) 115, (byte) 96, (byte) -127, (byte) 79, (byte) -36, (byte) 34, (byte) 42, (byte) -112, (byte) -120, (byte) 70, (byte) -18, (byte) -72, (byte) 20, (byte) -34, (byte) 94, (byte) 11, (byte) -37, (byte) -32, (byte) 50, (byte) 58, (byte) 10, (byte) 73, (byte) 6, (byte) 36, (byte) 92, (byte) -62, (byte) -45, (byte) -84, (byte) 98, (byte) -111, (byte) -107, (byte) -28, (byte) 121, (byte) -25, (byte) -56, (byte) 55, (byte) 109, (byte) -115, (byte) -43, (byte) 78, (byte) -87, (byte) 108, (byte) 86, (byte) -12, (byte) -22, (byte) 101, (byte) 122, (byte) -82, (byte) 8, (byte) -70, (byte) 120, (byte) 37, (byte) 46, (byte) 28, (byte) -90, (byte) -76, (byte) -58, (byte) -24, (byte) -35, (byte) 116, (byte) 31, (byte) 75, (byte) -67, (byte) -117, (byte) -118, (byte) 112, (byte) 62, (byte) -75, (byte) 102, (byte) 72, (byte) 3, (byte) -10, (byte) 14, (byte) 97, (byte) 53, (byte) 87, (byte) -71, (byte) -122, (byte) -63, (byte) 29, (byte) -98, (byte) -31, (byte) -8, (byte) -104, (byte) 17, (byte) 105, (byte) -39, (byte) -114, (byte) -108, (byte) -101, (byte) 30, (byte) -121, (byte) -23, (byte) -50, (byte) 85, (byte) 40, (byte) -33, (byte) -116, (byte) -95, (byte) -119, (byte) 13, (byte) -65, (byte) -26, (byte) 66, (byte) 104, (byte) 65, (byte) -103, (byte) 45, (byte) 15, (byte) -80, (byte) 84, (byte) -69, (byte) 22};
        Si = new byte[]{(byte) 82, (byte) 9, (byte) 106, (byte) -43, (byte) 48, (byte) 54, (byte) -91, (byte) 56, (byte) -65, (byte) 64, (byte) -93, (byte) -98, (byte) -127, (byte) -13, (byte) -41, (byte) -5, (byte) 124, (byte) -29, (byte) 57, (byte) -126, (byte) -101, (byte) 47, (byte) -1, (byte) -121, (byte) 52, (byte) -114, (byte) 67, (byte) 68, (byte) -60, (byte) -34, (byte) -23, (byte) -53, (byte) 84, (byte) 123, (byte) -108, (byte) 50, (byte) -90, (byte) -62, (byte) 35, (byte) 61, (byte) -18, (byte) 76, (byte) -107, (byte) 11, (byte) 66, (byte) -6, (byte) -61, (byte) 78, (byte) 8, (byte) 46, (byte) -95, (byte) 102, (byte) 40, (byte) -39, (byte) 36, (byte) -78, (byte) 118, (byte) 91, (byte) -94, (byte) 73, (byte) 109, (byte) -117, (byte) -47, (byte) 37, (byte) 114, (byte) -8, (byte) -10, (byte) 100, (byte) -122, (byte) 104, (byte) -104, (byte) 22, (byte) -44, (byte) -92, (byte) 92, (byte) -52, (byte) 93, (byte) 101, (byte) -74, (byte) -110, (byte) 108, (byte) 112, (byte) 72, (byte) 80, (byte) -3, (byte) -19, (byte) -71, (byte) -38, (byte) 94, (byte) 21, (byte) 70, (byte) 87, (byte) -89, (byte) -115, (byte) -99, (byte) -124, (byte) -112, (byte) -40, (byte) -85, (byte) 0, (byte) -116, (byte) -68, (byte) -45, (byte) 10, (byte) -9, (byte) -28, (byte) 88, (byte) 5, (byte) -72, (byte) -77, (byte) 69, (byte) 6, (byte) -48, (byte) 44, (byte) 30, (byte) -113, (byte) -54, (byte) 63, (byte) 15, (byte) 2, (byte) -63, (byte) -81, (byte) -67, (byte) 3, (byte) 1, (byte) 19, (byte) -118, (byte) 107, (byte) 58, (byte) -111, (byte) 17, (byte) 65, (byte) 79, (byte) 103, (byte) -36, (byte) -22, (byte) -105, (byte) -14, (byte) -49, (byte) -50, (byte) -16, (byte) -76, (byte) -26, (byte) 115, (byte) -106, (byte) -84, (byte) 116, (byte) 34, (byte) -25, (byte) -83, (byte) 53, (byte) -123, (byte) -30, (byte) -7, (byte) 55, (byte) -24, (byte) 28, (byte) 117, (byte) -33, (byte) 110, (byte) 71, (byte) -15, (byte) 26, (byte) 113, (byte) 29, (byte) 41, (byte) -59, (byte) -119, (byte) 111, (byte) -73, (byte) 98, (byte) 14, (byte) -86, (byte) 24, (byte) -66, (byte) 27, (byte) -4, (byte) 86, (byte) 62, (byte) 75, (byte) -58, (byte) -46, (byte) 121, (byte) 32, (byte) -102, (byte) -37, (byte) -64, (byte) -2, (byte) 120, (byte) -51, (byte) 90, (byte) -12, (byte) 31, (byte) -35, (byte) -88, (byte) 51, (byte) -120, (byte) 7, (byte) -57, (byte) 49, (byte) -79, (byte) 18, Tnaf.POW_2_WIDTH, (byte) 89, (byte) 39, Byte.MIN_VALUE, (byte) -20, (byte) 95, (byte) 96, (byte) 81, Byte.MAX_VALUE, (byte) -87, (byte) 25, (byte) -75, (byte) 74, (byte) 13, (byte) 45, (byte) -27, (byte) 122, (byte) -97, (byte) -109, (byte) -55, (byte) -100, (byte) -17, (byte) -96, (byte) -32, (byte) 59, (byte) 77, (byte) -82, (byte) 42, (byte) -11, (byte) -80, (byte) -56, (byte) -21, (byte) -69, (byte) 60, (byte) -125, (byte) 83, (byte) -103, (byte) 97, (byte) 23, (byte) 43, (byte) 4, (byte) 126, (byte) -70, (byte) 119, (byte) -42, (byte) 38, (byte) -31, (byte) 105, (byte) 20, (byte) 99, (byte) 85, (byte) 33, (byte) 12, (byte) 125};
        rcon = new int[]{1, 2, 4, 8, BLOCK_SIZE, 32, 64, ReasonFlags.unused, m3, 54, 108, 216, 171, 77, 154, 47, 94, 188, 99, 198, 151, 53, 106, 212, 179, 125, 250, 239, 197, 145};
        T0 = new int[]{-1520213050, -2072216328, -1720223762, -1921287178, 234025727, -1117033514, -1318096930, 1422247313, 1345335392, 50397442, -1452841010, 2099981142, 436141799, 1658312629, -424957107, -1703512340, 1170918031, -1652391393, 1086966153, -2021818886, 368769775, -346465870, -918075506, 200339707, -324162239, 1742001331, -39673249, -357585083, -1080255453, -140204973, -1770884380, 1539358875, -1028147339, 486407649, -1366060227, 1780885068, 1513502316, 1094664062, 49805301, 1338821763, 1546925160, -190470831, 887481809, 150073849, -1821281822, 1943591083, 1395732834, 1058346282, 201589768, 1388824469, 1696801606, 1589887901, 672667696, -1583966665, 251987210, -1248159185, 151455502, 907153956, -1686077413, 1038279391, 652995533, 1764173646, -843926913, -1619692054, 453576978, -1635548387, 1949051992, 773462580, 756751158, -1301385508, -296068428, -73359269, -162377052, 1295727478, 1641469623, -827083907, 2066295122, 1055122397, 1898917726, -1752923117, -179088474, 1758581177, 0, 753790401, 1612718144, 536673507, -927878791, -312779850, -1100322092, 1187761037, -641810841, 1262041458, -565556588, -733197160, -396863312, 1255133061, 1808847035, 720367557, -441800113, 385612781, -985447546, -682799718, 1429418854, -1803188975, -817543798, 284817897, 100794884, -2122350594, -263171936, 1144798328, -1163944155, -475486133, -212774494, -22830243, -1069531008, -1970303227, -1382903233, -1130521311, 1211644016, 83228145, -541279133, -1044990345, 1977277103, 1663115586, 806359072, 452984805, 250868733, 1842533055, 1288555905, 336333848, 890442534, 804056259, -513843266, -1567123659, -867941240, 957814574, 1472513171, -223893675, -2105639172, 1195195770, -1402706744, -413311558, 723065138, -1787595802, -1604296512, -1736343271, -783331426, 2145180835, 1713513028, 2116692564, -1416589253, -2088204277, -901364084, 703524551, -742868885, 1007948840, 2044649127, -497131844, 487262998, 1994120109, 1004593371, 1446130276, 1312438900, 503974420, -615954030, 168166924, 1814307912, -463709000, 1573044895, 1859376061, -273896381, -1503501628, -1466855111, -1533700815, 937747667, -1954973198, 854058965, 1137232011, 1496790894, -1217565222, -1936880383, 1691735473, -766620004, -525751991, -1267962664, -95005012, 133494003, 636152527, -1352309302, -1904575756, -374428089, 403179536, -709182865, -2005370640, 1864705354, 1915629148, 605822008, -240736681, -944458637, 1371981463, 602466507, 2094914977, -1670089496, 555687742, -582268010, -591544991, -2037675251, -2054518257, -1871679264, 1111375484, -994724495, -1436129588, -666351472, 84083462, 32962295, 302911004, -1553899070, 1597322602, -111716434, -793134743, -1853454825, 1489093017, 656219450, -1180787161, 954327513, 335083755, -1281845205, 856756514, -1150719534, 1893325225, -1987146233, -1483434957, -1231316179, 572399164, -1836611819, 552200649, 1238290055, -11184726, 2015897680, 2061492133, -1886614525, -123625127, -2138470135, 386731290, -624967835, 837215959, -968736124, -1201116976, -1019133566, -1332111063, 1999449434, 286199582, -877612933, -61582168, -692339859, 974525996};
        T1 = new int[]{1667483301, 2088564868, 2004348569, 2071721613, -218956019, 1802229437, 1869602481, -976907948, 808476752, 16843267, 1734856361, 724260477, -16849127, -673729182, -1414836762, 1987505306, -892694715, -2105401443, -909539008, 2105408135, -84218091, 1499050731, 1195871945, -252642549, -1381154324, -724257945, -1566416899, -1347467798, -1667488833, -1532734473, 1920132246, -1061119141, -1212713534, -33693412, -1819066962, 640044138, 909536346, 1061125697, -134744830, -859012273, 875849820, -1515892236, -437923532, -235800312, 1903288979, -656888973, 825320019, 353708607, 67373068, -943221422, 589514341, -1010590370, 404238376, -1768540255, 84216335, -1701171275, 117902857, 303178806, -2139087973, -488448195, -336868058, 656887401, -1296924723, 1970662047, 151589403, -2088559202, 741103732, 437924910, 454768173, 1852759218, 1515893998, -1600103429, 1381147894, 993752653, -690571423, -1280082482, 690573947, -471605954, 791633521, -2071719017, 1397991157, -774784664, 0, -303185620, 538984544, -50535649, -1313769016, 1532737261, 1785386174, -875852474, -1094817831, 960066123, 1246401758, 1280088276, 1482207464, -808483510, -791626901, -269499094, -1431679003, -67375850, 1128498885, 1296931543, 859006549, -2054876780, 1162185423, -101062384, 33686534, 2139094657, 1347461360, 1010595908, -1616960070, -1465365533, 1364304627, -1549574658, 1077969088, -1886452342, -1835909203, -1650646596, 943222856, -168431356, -1128504353, -1229555775, -623202443, 555827811, 269492272, -6886, -202113778, -757940371, -842170036, 202119188, 320022069, -320027857, 1600110305, -1751698014, 1145342156, 387395129, -993750185, -1482205710, 2122251394, 1027439175, 1684326572, 1566423783, 421081643, 1936975509, 1616953504, -2122245736, 1330618065, -589520001, 572671078, 707417214, -1869595733, -2004350077, 1179028682, -286341335, -1195873325, 336865340, -555833479, 1583267042, 185275933, -606360202, -522134725, 842163286, 976909390, 168432670, 1229558491, 101059594, 606357612, 1549580516, -1027432611, -741098130, -1397996561, 1650640038, -1852753496, -1785384540, -454765769, 2038035083, -404237006, -926381245, 926379609, 1835915959, -1920138868, -707415708, 1313774802, -1448523296, 1819072692, 1448520954, -185273593, -353710299, 1701169839, 2054878350, -1364310039, 134746136, -1162186795, 2021191816, 623200879, 774790258, 471611428, -1499047951, -1263242297, -960063663, -387396829, -572677764, 1953818780, 522141217, 1263245021, -1111662116, -1953821306, -1970663547, 1886445712, 1044282434, -1246400060, 1718013098, 1212715224, 50529797, -151587071, 235805714, 1633796771, 892693087, 1465364217, -1179031088, -2038032495, -1044276904, 488454695, -1633802311, -505292488, -117904621, -1734857805, 286335539, 1768542907, -640046736, -1903294583, -1802226777, -1684329034, 505297954, -2021190254, -370554592, -825325751, 1431677695, 673730680, -538991238, -1936981105, -1583261192, -1987507840, 218962455, -1077975590, -421079247, 1111655622, 1751699640, 1094812355, -1718015568, 757946999, 252648977, -1330611253, 1414834428, -1145344554, 370551866};
        T2 = new int[]{1673962851, 2096661628, 2012125559, 2079755643, -218165774, 1809235307, 1876865391, -980331323, 811618352, 16909057, 1741597031, 727088427, -18408962, -675978537, -1420958037, 1995217526, -896580150, -2111857278, -913751863, 2113570685, -84994566, 1504897881, 1200539975, -251982864, -1388188499, -726439980, -1570767454, -1354372433, -1675378788, -1538000988, 1927583346, -1063560256, -1217019209, -35578627, -1824674157, 642542118, 913070646, 1065238847, -134937865, -863809588, 879254580, -1521355611, -439274267, -235337487, 1910674289, -659852328, 828527409, 355090197, 67636228, -946515257, 591815971, -1013096765, 405809176, -1774739050, 84545285, -1708149350, 118360327, 304363026, -2145674368, -488686110, -338876693, 659450151, -1300247118, 1978310517, 152181513, -2095210877, 743994412, 439627290, 456535323, 1859957358, 1521806938, -1604584544, 1386542674, 997608763, -692624938, -1283600717, 693271337, -472039709, 794718511, -2079090812, 1403450707, -776378159, 0, -306107155, 541089824, -52224004, -1317418831, 1538714971, 1792327274, -879933749, -1100490306, 963791673, 1251270218, 1285084236, 1487988824, -813348145, -793023536, -272291089, -1437604438, -68348165, 1132905795, 1301993293, 862344499, -2062445435, 1166724933, -102166279, 33818114, 2147385727, 1352724560, 1014514748, -1624917345, -1471421528, 1369633617, -1554121053, 1082179648, -1895462257, -1841320558, -1658733411, 946882616, -168753931, -1134305348, -1233665610, -626035238, 557998881, 270544912, -1762561, -201519373, -759206446, -847164211, 202904588, 321271059, -322752532, 1606345055, -1758092649, 1149815876, 388905239, -996976700, -1487539545, 2130477694, 1031423805, 1690872932, 1572530013, 422718233, 1944491379, 1623236704, -2129028991, 1335808335, -593264676, 574907938, 710180394, -1875137648, -2012511352, 1183631942, -288937490, -1200893000, 338181140, -559449634, 1589437022, 185998603, -609388837, -522503200, 845436466, 980700730, 169090570, 1234361161, 101452294, 608726052, 1555620956, -1029743166, -742560045, -1404833876, 1657054818, -1858492271, -1791908715, -455919644, 2045938553, -405458201, -930397240, 929978679, 1843050349, -1929278323, -709794603, 1318900302, -1454776151, 1826141292, 1454176854, -185399308, -355523094, 1707781989, 2062847610, -1371018834, 135272456, -1167075910, 2029029496, 625635109, 777810478, 473441308, -1504185946, -1267480652, -963161658, -389340184, -576619299, 1961401460, 524165407, 1268178251, -1117659971, -1962047861, -1978694262, 1893765232, 1048330814, -1250835275, 1724688998, 1217452104, 50726147, -151584266, 236720654, 1640145761, 896163637, 1471084887, -1184247623, -2045275770, -1046914879, 490350365, -1641563746, -505857823, -118811656, -1741966440, 287453969, 1775418217, -643206951, -1912108658, -1808554092, -1691502949, 507257374, -2028629369, -372694807, -829994546, 1437269845, 676362280, -542803233, -1945923700, -1587939167, -1995865975, 219813645, -1083843905, -422104602, 1115997762, 1758509160, 1099088705, -1725321063, 760903469, 253628687, -1334064208, 1420360788, -1150429509, 371997206};
        T3 = new int[]{-962239645, -125535108, -291932297, -158499973, -15863054, -692229269, -558796945, -1856715323, 1615867952, 33751297, -827758745, 1451043627, -417726722, -1251813417, 1306962859, -325421450, -1891251510, 530416258, -1992242743, -91783811, -283772166, -1293199015, -1899411641, -83103504, 1106029997, -1285040940, 1610457762, 1173008303, 599760028, 1408738468, -459902350, -1688485696, 1975695287, -518193667, 1034851219, 1282024998, 1817851446, 2118205247, -184354825, -2091922228, 1750873140, 1374987685, -785062427, -116854287, -493653647, -1418471208, 1649619249, 708777237, 135005188, -1789737017, 1181033251, -1654733885, 807933976, 933336726, 168756485, 800430746, 235472647, 607523346, 463175808, -549592350, -853087253, 1315514151, 2144187058, -358648459, 303761673, 496927619, 1484008492, 875436570, 908925723, -592286098, -1259447718, 1543217312, -1527360942, 1984772923, -1218324778, 2110698419, 1383803177, -583080989, 1584475951, 328696964, -1493871789, -1184312879, 0, -1054020115, 1080041504, -484442884, 2043195825, -1225958565, -725718422, -1924740149, 1742323390, 1917532473, -1797371318, -1730917300, -1326950312, -2058694705, -1150562096, -987041809, 1340451498, -317260805, -2033892541, -1697166003, 1716859699, 294946181, -1966127803, -384763399, 67502594, -25067649, -1594863536, 2017737788, 632987551, 1273211048, -1561112239, 1576969123, -2134884288, 92966799, 1068339858, 566009245, 1883781176, -251333131, 1675607228, 2009183926, -1351230758, 1113792801, 540020752, -451215361, -49351693, -1083321646, -2125673011, 403966988, 641012499, -1020269332, -1092526241, 899848087, -1999879100, 775493399, -1822964540, 1441965991, -58556802, 2051489085, -928226204, -1159242403, 841685273, -426413197, -1063231392, 429425025, -1630449841, -1551901476, 1147544098, 1417554474, 1001099408, 193169544, -1932900794, -953553170, 1809037496, 675025940, -1485185314, -1126015394, 371002123, -1384719397, -616832800, 1683370546, 1951283770, 337512970, -1831122615, 201983494, 1215046692, -1192993700, -1621245246, -1116810285, 1139780780, -995728798, 967348625, 832869781, -751311644, -225740423, -718084121, -1958491960, 1851340599, -625513107, 25988493, -1318791723, -1663938994, 1239460265, -659264404, -1392880042, -217582348, -819598614, -894474907, -191989126, 1206496942, 270010376, 1876277946, -259491720, 1248797989, 1550986798, 941890588, 1475454630, 1942467764, -1756248378, -886839064, -1585652259, -392399756, 1042358047, -1763882165, 1641856445, 226921355, 260409994, -527404944, 2084716094, 1908716981, -861247898, -1864873912, 100991747, -150866186, 470945294, -1029480095, 1784624437, -1359390889, 1775286713, 395413126, -1722236479, 975641885, 666476190, -650583583, -351012616, 733190296, 573772049, -759469719, -1452221991, 126455438, 866620564, 766942107, 1008868894, 361924487, -920589847, -2025206066, -1426107051, 1350051880, -1518673953, 59739276, 1509466529, 159418761, 437718285, 1708834751, -684595482, -2067381694, -793221016, -2101132991, 699439513, 1517759789, 504434447, 2076946608, -1459858348, 1842789307, 742004246};
        Tinv0 = new int[]{1353184337, 1399144830, -1012656358, -1772214470, -882136261, -247096033, -1420232020, -1828461749, 1442459680, -160598355, -1854485368, 625738485, -52959921, -674551099, -2143013594, -1885117771, 1230680542, 1729870373, -1743852987, -507445667, 41234371, 317738113, -1550367091, -956705941, -413167869, -1784901099, -344298049, -631680363, 763608788, -752782248, 694804553, 1154009486, 1787413109, 2021232372, 1799248025, -579749593, -1236278850, 397248752, 1722556617, -1271214467, 407560035, -2110711067, 1613975959, 1165972322, -529046351, -2068943941, 480281086, -1809118983, 1483229296, 436028815, -2022908268, -1208452270, 601060267, -503166094, 1468997603, 715871590, 120122290, 63092015, -1703164538, -1526188077, -226023376, -1297760477, -1167457534, 1552029421, 723308426, -1833666137, -252573709, -1578997426, -839591323, -708967162, 526529745, -1963022652, -1655493068, -1604979806, 853641733, 1978398372, 971801355, -1427152832, 111112542, 1360031421, -108388034, 1023860118, -1375387939, 1186850381, -1249028975, 90031217, 1876166148, -15380384, 620468249, -1746289194, -868007799, 2006899047, -1119688528, -2004121337, 945494503, -605108103, 1191869601, -384875908, -920746760, 0, -2088337399, 1223502642, -1401941730, 1316117100, -67170563, 1446544655, 517320253, 658058550, 1691946762, 564550760, -783000677, 976107044, -1318647284, 266819475, -761860428, -1634624741, 1338359936, -1574904735, 1766553434, 370807324, 179999714, -450191168, 1138762300, 488053522, 185403662, -1379431438, -1180125651, -928440812, -2061897385, 1275557295, -1143105042, -44007517, -1624899081, -1124765092, -985962940, 880737115, 1982415755, -590994485, 1761406390, 1676797112, -891538985, 277177154, 1076008723, 538035844, 2099530373, -130171950, 288553390, 1839278535, 1261411869, -214912292, -330136051, -790380169, 1813426987, -1715900247, -95906799, 577038663, -997393240, 440397984, -668172970, -275762398, -951170681, -1043253031, -22885748, 906744984, -813566554, 685669029, 646887386, -1530942145, -459458004, 227702864, -1681105046, 1648787028, -1038905866, -390539120, 1593260334, -173030526, -1098883681, 2090061929, -1456614033, -1290656305, 999926984, -1484974064, 1852021992, 2075868123, 158869197, -199730834, 28809964, -1466282109, 1701746150, 2129067946, 147831841, -420997649, -644094022, -835293366, -737566742, -696471511, -1347247055, 824393514, 815048134, -1067015627, 935087732, -1496677636, -1328508704, 366520115, 1251476721, -136647615, 240176511, 804688151, -1915335306, 1303441219, 1414376140, -553347356, -474623586, 461924940, -1205916479, 2136040774, 82468509, 1563790337, 1937016826, 776014843, 1511876531, 1389550482, 861278441, 323475053, -1939744870, 2047648055, -1911228327, -1992551445, -299390514, 902390199, -303751967, 1018251130, 1507840668, 1064563285, 2043548696, -1086863501, -355600557, 1537932639, 342834655, -2032450440, -2114736182, 1053059257, 741614648, 1598071746, 1925389590, 203809468, -1958134744, 1100287487, 1895934009, -558691320, -1662733096, -1866377628, 1636092795, 1890988757, 1952214088, 1113045200};
        Tinv1 = new int[]{-1477160624, 1698790995, -1541989693, 1579629206, 1806384075, 1167925233, 1492823211, 65227667, -97509291, 1836494326, 1993115793, 1275262245, -672837636, -886389289, 1144333952, -1553812081, 1521606217, 465184103, 250234264, -1057071647, 1966064386, -263421678, -1756983901, -103584826, 1603208167, -1668147819, 2054012907, 1498584538, -2084645843, 561273043, 1776306473, -926314940, -1983744662, 2039411832, 1045993835, 1907959773, 1340194486, -1383534569, -1407137434, 986611124, 1256153880, 823846274, 860985184, 2136171077, 2003087840, -1368671356, -1602093540, 722008468, 1749577816, -45773031, 1826526343, -126135625, -747394269, 38499042, -1893735593, -1420466646, 686535175, -1028313341, 2076542618, 137876389, -2027409166, -1514200142, 1778582202, -2112426660, 483363371, -1267095662, -234359824, -496415071, -187013683, -1106966827, 1647628575, -22625142, 1395537053, 1442030240, -511048398, -336157579, -326956231, -278904662, -1619960314, 275692881, -1977532679, 115185213, 88006062, -1108980410, -1923837515, 1573155077, -737803153, 357589247, -73918172, -373434729, 1128303052, -1629919369, 1122545853, -1953953912, 1528424248, -288851493, 175939911, 256015593, 512030921, 0, -2038429309, -315936184, 1880170156, 1918528590, -15794693, 948244310, -710001378, 959264295, -653325724, -1503893471, 1415289809, 775300154, 1728711857, -413691121, -1762741038, -1852105826, -977239985, 551313826, 1266113129, 437394454, -1164713462, 715178213, -534627261, 387650077, 218697227, -947129683, -1464455751, -1457646392, 435246981, 125153100, -577114437, 1618977789, 637663135, -177054532, 996558021, 2130402100, 692292470, -970732580, -51530136, -236668829, -600713270, -2057092592, 580326208, 298222624, 608863613, 1035719416, 855223825, -1591097491, 798891339, 817028339, 1384517100, -473860144, 380840812, -1183798887, 1217663482, 1693009698, -1929598780, 1072734234, 746411736, -1875696913, 1313441735, -784803391, -1563783938, 198481974, -2114607409, -562387672, -1900553690, -1079165020, -1657131804, -1837608947, -866162021, 1182684258, 328070850, -1193766680, -147247522, -1346141451, -2141347906, -1815058052, 768962473, 304467891, -1716729797, 2098729127, 1671227502, -1153705093, 2015808777, 408514292, -1214583807, -1706064984, 1855317605, -419452290, -809754360, -401215514, -1679312167, 913263310, 161475284, 2091919830, -1297862225, 591342129, -1801075152, 1721906624, -1135709129, -897385306, -795811664, -660131051, -1744506550, -622050825, 1355644686, -158263505, -699566451, -1326496947, 1303039060, 76997855, -1244553501, -2006299621, 523026872, 1365591679, -362898172, 898367837, 1955068531, 1091304238, 493335386, -757362094, 1443948851, 1205234963, 1641519756, 211892090, 351820174, 1007938441, 665439982, -916342987, -451091987, -1320715716, -539845543, 1945261375, -837543815, 935818175, -839429142, -1426235557, 1866325780, -616269690, -206583167, -999769794, 874788908, 1084473951, -1021503886, 635616268, 1228679307, -1794244799, 27801969, -1291056930, -457910116, -1051302768, -2067039391, -1238182544, 1550600308, 1471729730};
        Tinv2 = new int[]{-195997529, 1098797925, 387629988, 658151006, -1422144661, -1658851003, -89347240, -481586429, 807425530, 1991112301, -863465098, 49620300, -447742761, 717608907, 891715652, 1656065955, -1310832294, -1171953893, -364537842, -27401792, 801309301, 1283527408, 1183687575, -747911431, -1895569569, -1844079204, 1841294202, 1385552473, -1093390973, 1951978273, -532076183, -913423160, -1032492407, -1896580999, 1486449470, -1188569743, -507595185, -1997531219, 550069932, -830622662, -547153846, 451248689, 1368875059, 1398949247, 1689378935, 1807451310, -2114052960, 150574123, 1215322216, 1167006205, -560691348, 2069018616, 1940595667, 1265820162, 534992783, 1432758955, -340654296, -1255210046, -981034373, 936617224, 674296455, -1088179547, 50510442, 384654466, -813028580, 2041025204, 133427442, 1766760930, -630862348, 84334014, 886120290, -1497068802, 775200083, -207445931, -1979370783, -156994069, -2096416276, 1614850799, 1901987487, 1857900816, 557775242, -577356538, 1054715397, -431143235, 1418835341, -999226019, 100954068, 1348534037, -1743182597, -1110009879, 1082772547, -647530594, -391070398, -1995994997, 434583643, -931537938, 2090944266, 1115482383, -2064070370, 0, -2146860154, 724715757, 287222896, 1517047410, 251526143, -2062592456, -1371726123, 758523705, 252339417, 1550328230, 1536938324, 908343854, 168604007, 1469255655, -290139498, -1692688751, -1065332795, -597581280, 2002413899, 303830554, -1813902662, -1597971158, 574374880, 454171927, 151915277, -1947030073, -1238517336, 504678569, -245922535, 1974422535, -1712407587, 2141453664, 33005350, 1918680309, 1715782971, -77908866, 1133213225, 600562886, -306812676, -457677839, 836225756, 1665273989, -1760346078, -964419567, 1250262308, -1143801795, -106032846, 700935585, -1642247377, -1294142672, -2045907886, -1049112349, -1288999914, 1890163129, -1810761144, -381214108, -56048500, -257942977, 2102843436, 857927568, 1233635150, 953795025, -896729438, -728222197, -173617279, 2057644254, -1210440050, -1388337985, 976020637, 2018512274, 1600822220, 2119459398, -1913208301, -661591880, 959340279, -1014827601, 1570750080, -798393197, -714102483, 634368786, -1396163687, 403744637, -1662488989, 1004239803, 650971512, 1500443672, -1695809097, 1334028442, -1780062866, -5603610, -1138685745, 368043752, -407184997, 1867173430, -1612000247, -1339435396, -1540247630, 1059729699, -1513738092, -1573535642, 1316239292, -2097371446, -1864322864, -1489824296, 82922136, -331221030, -847311280, -1860751370, 1299615190, -280801872, -1429449651, -1763385596, -778116171, 1783372680, 750893087, 1699118929, 1587348714, -1946067659, -2013629580, 201010753, 1739807261, -611167534, 283718486, -697494713, -677737375, -1590199796, -128348652, 334203196, -1446056409, 1639396809, 484568549, 1199193265, -761505313, -229294221, 337148366, -948715721, -145495347, -44082262, 1038029935, 1148749531, -1345682957, 1756970692, 607661108, -1547542720, 488010435, -490992603, 1009290057, 234832277, -1472630527, 201907891, -1260872476, 1449431233, -881106556, 852848822, 1816687708, -1194311081};
        Tinv3 = new int[]{1364240372, 2119394625, 449029143, 982933031, 1003187115, 535905693, -1398056710, 1267925987, 542505520, -1376359050, -2003732788, -182105086, 1341970405, -975713494, 645940277, -1248877726, -565617999, 627514298, 1167593194, 1575076094, -1023249105, -2129465268, -1918658746, 1808202195, 65494927, 362126482, -1075086739, -1780852398, -735214658, 1490231668, 1227450848, -1908094775, 1969916354, -193431154, -1721024936, 668823993, -1095348255, -266883704, -916018144, 2108963534, 1662536415, -444452582, -1755303087, 1648721747, -1310689436, -1148932501, -31678335, -107730168, 1884842056, -1894122171, -1803064098, 1387788411, -1423715469, 1927414347, -480800993, 1714072405, -1308153621, 788775605, -2036696123, -744159177, 821200680, 598910399, 45771267, -312704490, -1976886065, -1483557767, -202313209, 1319232105, 1707996378, 114671109, -786472396, -997523802, 882725678, -1566550541, 87220618, -1535775754, 188345475, 1084944224, 1577492337, -1118760850, 1056541217, -1774385443, -575797954, 1296481766, -1850372780, 1896177092, 74437638, 1627329872, 421854104, -694687299, -1983102144, 1735892697, -1329773848, 126389129, -415737063, 2044456648, -1589179780, 2095648578, -121037180, 0, 159614592, 843640107, 514617361, 1817080410, -33816818, 257308805, 1025430958, 908540205, 174381327, 1747035740, -1680780197, 607792694, 212952842, -1827674281, -1261267218, 463376795, -2142255680, 1638015196, 1516850039, 471210514, -502613357, -1058723168, 1011081250, 303896347, 235605257, -223492213, 767142070, 348694814, 1468340721, -1353971851, -289677927, -1543675777, -140564991, 1555887474, 1153776486, 1530167035, -1955190461, -874723805, -1234633491, -1201409564, -674571215, 1108378979, 322970263, -2078273082, -2055396278, -755483205, -1374604551, -949116631, 491466654, -588042062, 233591430, 2010178497, 728503987, -1449543312, 301615252, 1193436393, -1463513860, -1608892432, 1457007741, 586125363, -2016981431, -641609416, -1929469238, -1741288492, -1496350219, -1524048262, -635007305, 1067761581, 753179962, 1343066744, 1788595295, 1415726718, -155053171, -1863796520, 777975609, -2097827901, -1614905251, 1769771984, 1873358293, -810347995, -935618132, 279411992, -395418724, -612648133, -855017434, 1861490777, -335431782, -2086102449, -429560171, -1434523905, 554225596, -270079979, -1160143897, 1255028335, -355202657, 701922480, 833598116, 707863359, -969894747, 901801634, 1949809742, -56178046, -525283184, 857069735, -246769660, 1106762476, 2131644621, 389019281, 1989006925, 1129165039, -866890326, -455146346, -1629243951, 1276872810, -1044898004, 1182749029, -1660622242, 22885772, -93096825, -80854773, -1285939865, -1840065829, -382511600, 1829980118, -1702075945, 930745505, 1502483704, -343327725, -823253079, -1221211807, -504503012, 2050797895, -1671831598, 1430221810, 410635796, 1941911495, 1407897079, 1599843069, -552308931, 2022103876, -897453137, -1187068824, 942421028, -1033944925, 376619805, -1140054558, 680216892, -12479219, 963707304, 148812556, -660806476, 1687208278, 2069988555, -714033614, 1215585388, -800958536};
    }

    private static int shift(int r, int shift) {
        return (r >>> shift) | (r << (-shift));
    }

    private static int FFmulX(int x) {
        return ((m2 & x) << 1) ^ (((m1 & x) >>> 7) * m3);
    }

    private static int inv_mcol(int x) {
        int f2 = FFmulX(x);
        int f4 = FFmulX(f2);
        int f8 = FFmulX(f4);
        int f9 = x ^ f8;
        return ((((f2 ^ f4) ^ f8) ^ shift(f2 ^ f9, 8)) ^ shift(f4 ^ f9, BLOCK_SIZE)) ^ shift(f9, 24);
    }

    private static int subWord(int x) {
        return (((S[x & 255] & 255) | ((S[(x >> 8) & 255] & 255) << 8)) | ((S[(x >> BLOCK_SIZE) & 255] & 255) << BLOCK_SIZE)) | (S[(x >> 24) & 255] << 24);
    }

    public String getAlgorithmName() {
        return "AES";
    }

    public int getBlockSize() {
        return BLOCK_SIZE;
    }

    public void reset() {
    }
}
