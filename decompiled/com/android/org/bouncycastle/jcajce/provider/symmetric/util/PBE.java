package com.android.org.bouncycastle.jcajce.provider.symmetric.util;

import com.android.org.bouncycastle.crypto.PBEParametersGenerator;
import com.android.org.bouncycastle.crypto.digests.AndroidDigestFactory;
import com.android.org.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator;
import com.android.org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import com.android.org.bouncycastle.crypto.generators.PKCS5S1ParametersGenerator;
import com.android.org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;

public interface PBE {
    public static final int MD5 = 0;
    public static final int OPENSSL = 3;
    public static final int PKCS12 = 2;
    public static final int PKCS5S1 = 0;
    public static final int PKCS5S1_UTF8 = 4;
    public static final int PKCS5S2 = 1;
    public static final int PKCS5S2_UTF8 = 5;
    public static final int SHA1 = 1;
    public static final int SHA256 = 4;

    public static class Util {
        private static byte[] convertPassword(int r1, javax.crypto.spec.PBEKeySpec r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.bouncycastle.jcajce.provider.symmetric.util.PBE.Util.convertPassword(int, javax.crypto.spec.PBEKeySpec):byte[]
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.bouncycastle.jcajce.provider.symmetric.util.PBE.Util.convertPassword(int, javax.crypto.spec.PBEKeySpec):byte[]
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.jcajce.provider.symmetric.util.PBE.Util.convertPassword(int, javax.crypto.spec.PBEKeySpec):byte[]");
        }

        public static com.android.org.bouncycastle.crypto.CipherParameters makePBEMacParameters(com.android.org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey r1, java.security.spec.AlgorithmParameterSpec r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.bouncycastle.jcajce.provider.symmetric.util.PBE.Util.makePBEMacParameters(com.android.org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey, java.security.spec.AlgorithmParameterSpec):com.android.org.bouncycastle.crypto.CipherParameters
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.bouncycastle.jcajce.provider.symmetric.util.PBE.Util.makePBEMacParameters(com.android.org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey, java.security.spec.AlgorithmParameterSpec):com.android.org.bouncycastle.crypto.CipherParameters
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.jcajce.provider.symmetric.util.PBE.Util.makePBEMacParameters(com.android.org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey, java.security.spec.AlgorithmParameterSpec):com.android.org.bouncycastle.crypto.CipherParameters");
        }

        public static com.android.org.bouncycastle.crypto.CipherParameters makePBEMacParameters(javax.crypto.spec.PBEKeySpec r1, int r2, int r3, int r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.bouncycastle.jcajce.provider.symmetric.util.PBE.Util.makePBEMacParameters(javax.crypto.spec.PBEKeySpec, int, int, int):com.android.org.bouncycastle.crypto.CipherParameters
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.bouncycastle.jcajce.provider.symmetric.util.PBE.Util.makePBEMacParameters(javax.crypto.spec.PBEKeySpec, int, int, int):com.android.org.bouncycastle.crypto.CipherParameters
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.jcajce.provider.symmetric.util.PBE.Util.makePBEMacParameters(javax.crypto.spec.PBEKeySpec, int, int, int):com.android.org.bouncycastle.crypto.CipherParameters");
        }

        public static com.android.org.bouncycastle.crypto.CipherParameters makePBEParameters(com.android.org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey r1, java.security.spec.AlgorithmParameterSpec r2, java.lang.String r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.bouncycastle.jcajce.provider.symmetric.util.PBE.Util.makePBEParameters(com.android.org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey, java.security.spec.AlgorithmParameterSpec, java.lang.String):com.android.org.bouncycastle.crypto.CipherParameters
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.bouncycastle.jcajce.provider.symmetric.util.PBE.Util.makePBEParameters(com.android.org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey, java.security.spec.AlgorithmParameterSpec, java.lang.String):com.android.org.bouncycastle.crypto.CipherParameters
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.jcajce.provider.symmetric.util.PBE.Util.makePBEParameters(com.android.org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey, java.security.spec.AlgorithmParameterSpec, java.lang.String):com.android.org.bouncycastle.crypto.CipherParameters");
        }

        public static com.android.org.bouncycastle.crypto.CipherParameters makePBEParameters(javax.crypto.spec.PBEKeySpec r1, int r2, int r3, int r4, int r5) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.bouncycastle.jcajce.provider.symmetric.util.PBE.Util.makePBEParameters(javax.crypto.spec.PBEKeySpec, int, int, int, int):com.android.org.bouncycastle.crypto.CipherParameters
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.bouncycastle.jcajce.provider.symmetric.util.PBE.Util.makePBEParameters(javax.crypto.spec.PBEKeySpec, int, int, int, int):com.android.org.bouncycastle.crypto.CipherParameters
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.jcajce.provider.symmetric.util.PBE.Util.makePBEParameters(javax.crypto.spec.PBEKeySpec, int, int, int, int):com.android.org.bouncycastle.crypto.CipherParameters");
        }

        private static PBEParametersGenerator makePBEGenerator(int type, int hash) {
            if (type == 0 || type == PBE.SHA256) {
                switch (hash) {
                    case PBE.PKCS5S1 /*0*/:
                        return new PKCS5S1ParametersGenerator(AndroidDigestFactory.getMD5());
                    case PBE.SHA1 /*1*/:
                        return new PKCS5S1ParametersGenerator(AndroidDigestFactory.getSHA1());
                    default:
                        throw new IllegalStateException("PKCS5 scheme 1 only supports MD2, MD5 and SHA1.");
                }
            } else if (type == PBE.SHA1 || type == PBE.PKCS5S2_UTF8) {
                switch (hash) {
                    case PBE.PKCS5S1 /*0*/:
                        return new PKCS5S2ParametersGenerator(AndroidDigestFactory.getMD5());
                    case PBE.SHA1 /*1*/:
                        return new PKCS5S2ParametersGenerator(AndroidDigestFactory.getSHA1());
                    case PBE.SHA256 /*4*/:
                        return new PKCS5S2ParametersGenerator(AndroidDigestFactory.getSHA256());
                    default:
                        throw new IllegalStateException("unknown digest scheme for PBE PKCS5S2 encryption.");
                }
            } else if (type != PBE.PKCS12) {
                return new OpenSSLPBEParametersGenerator();
            } else {
                switch (hash) {
                    case PBE.PKCS5S1 /*0*/:
                        return new PKCS12ParametersGenerator(AndroidDigestFactory.getMD5());
                    case PBE.SHA1 /*1*/:
                        return new PKCS12ParametersGenerator(AndroidDigestFactory.getSHA1());
                    case PBE.SHA256 /*4*/:
                        return new PKCS12ParametersGenerator(AndroidDigestFactory.getSHA256());
                    default:
                        throw new IllegalStateException("unknown digest scheme for PBE encryption.");
                }
            }
        }
    }
}
