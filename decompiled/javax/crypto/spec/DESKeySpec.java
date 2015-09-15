package javax.crypto.spec;

import java.security.InvalidKeyException;
import java.security.spec.KeySpec;

public class DESKeySpec implements KeySpec {
    public static final int DES_KEY_LEN = 8;
    private static final byte[][] SEMIWEAKS;
    private final byte[] key;

    public DESKeySpec(byte[] r1) throws java.security.InvalidKeyException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: javax.crypto.spec.DESKeySpec.<init>(byte[]):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: javax.crypto.spec.DESKeySpec.<init>(byte[]):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 0073
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: javax.crypto.spec.DESKeySpec.<init>(byte[]):void");
    }

    public DESKeySpec(byte[] r1, int r2) throws java.security.InvalidKeyException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: javax.crypto.spec.DESKeySpec.<init>(byte[], int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: javax.crypto.spec.DESKeySpec.<init>(byte[], int):void
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
        throw new UnsupportedOperationException("Method not decompiled: javax.crypto.spec.DESKeySpec.<init>(byte[], int):void");
    }

    public byte[] getKey() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: javax.crypto.spec.DESKeySpec.getKey():byte[]
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: javax.crypto.spec.DESKeySpec.getKey():byte[]
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
        throw new UnsupportedOperationException("Method not decompiled: javax.crypto.spec.DESKeySpec.getKey():byte[]");
    }

    static {
        SEMIWEAKS = new byte[][]{new byte[]{(byte) -32, (byte) 1, (byte) -32, (byte) 1, (byte) -15, (byte) 1, (byte) -15, (byte) 1}, new byte[]{(byte) 1, (byte) -32, (byte) 1, (byte) -32, (byte) 1, (byte) -15, (byte) 1, (byte) -15}, new byte[]{(byte) -2, (byte) 31, (byte) -2, (byte) 31, (byte) -2, Character.PARAGRAPH_SEPARATOR, (byte) -2, Character.PARAGRAPH_SEPARATOR}, new byte[]{(byte) 31, (byte) -2, (byte) 31, (byte) -2, Character.PARAGRAPH_SEPARATOR, (byte) -2, Character.PARAGRAPH_SEPARATOR, (byte) -2}, new byte[]{(byte) -32, (byte) 31, (byte) -32, (byte) 31, (byte) -15, Character.PARAGRAPH_SEPARATOR, (byte) -15, Character.PARAGRAPH_SEPARATOR}, new byte[]{(byte) 31, (byte) -32, (byte) 31, (byte) -32, Character.PARAGRAPH_SEPARATOR, (byte) -15, Character.PARAGRAPH_SEPARATOR, (byte) -15}, new byte[]{(byte) 1, (byte) -2, (byte) 1, (byte) -2, (byte) 1, (byte) -2, (byte) 1, (byte) -2}, new byte[]{(byte) -2, (byte) 1, (byte) -2, (byte) 1, (byte) -2, (byte) 1, (byte) -2, (byte) 1}, new byte[]{(byte) 1, (byte) 31, (byte) 1, (byte) 31, (byte) 1, Character.PARAGRAPH_SEPARATOR, (byte) 1, Character.PARAGRAPH_SEPARATOR}, new byte[]{(byte) 31, (byte) 1, (byte) 31, (byte) 1, Character.PARAGRAPH_SEPARATOR, (byte) 1, Character.PARAGRAPH_SEPARATOR, (byte) 1}, new byte[]{(byte) -32, (byte) -2, (byte) -32, (byte) -2, (byte) -15, (byte) -2, (byte) -15, (byte) -2}, new byte[]{(byte) -2, (byte) -32, (byte) -2, (byte) -32, (byte) -2, (byte) -15, (byte) -2, (byte) -15}, new byte[]{(byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1, (byte) 1}, new byte[]{(byte) -2, (byte) -2, (byte) -2, (byte) -2, (byte) -2, (byte) -2, (byte) -2, (byte) -2}, new byte[]{(byte) -32, (byte) -32, (byte) -32, (byte) -32, (byte) -15, (byte) -15, (byte) -15, (byte) -15}, new byte[]{(byte) 31, (byte) 31, (byte) 31, (byte) 31, Character.PARAGRAPH_SEPARATOR, Character.PARAGRAPH_SEPARATOR, Character.PARAGRAPH_SEPARATOR, Character.PARAGRAPH_SEPARATOR}};
    }

    public static boolean isParityAdjusted(byte[] key, int offset) throws InvalidKeyException {
        if (key == null) {
            throw new InvalidKeyException("key == null");
        } else if (key.length - offset < DES_KEY_LEN) {
            throw new InvalidKeyException("key too short");
        } else {
            for (int i = offset; i < DES_KEY_LEN; i++) {
                int byteKey = key[i];
                byteKey ^= byteKey >> 1;
                byteKey ^= byteKey >> 2;
                if (((byteKey ^ (byteKey >> 4)) & 1) == 0) {
                    return false;
                }
            }
            return true;
        }
    }

    public static boolean isWeak(byte[] key, int offset) throws InvalidKeyException {
        if (key == null) {
            throw new InvalidKeyException("key == null");
        } else if (key.length - offset < DES_KEY_LEN) {
            throw new InvalidKeyException("key too short");
        } else {
            int i = 0;
            while (i < SEMIWEAKS.length) {
                int j = 0;
                while (j < DES_KEY_LEN) {
                    if (SEMIWEAKS[i][j] != key[offset + j]) {
                        i++;
                    } else {
                        j++;
                    }
                }
                return true;
            }
            return false;
        }
    }
}
