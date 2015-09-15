package com.android.org.bouncycastle.math.ec;

import com.android.org.bouncycastle.math.ec.ECPoint.F2m;
import java.math.BigInteger;

public class WTauNafMultiplier extends AbstractECMultiplier {
    private static com.android.org.bouncycastle.math.ec.ECPoint.F2m multiplyFromWTnaf(com.android.org.bouncycastle.math.ec.ECPoint.F2m r1, byte[] r2, com.android.org.bouncycastle.math.ec.PreCompInfo r3) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.bouncycastle.math.ec.WTauNafMultiplier.multiplyFromWTnaf(com.android.org.bouncycastle.math.ec.ECPoint$F2m, byte[], com.android.org.bouncycastle.math.ec.PreCompInfo):com.android.org.bouncycastle.math.ec.ECPoint$F2m
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.bouncycastle.math.ec.WTauNafMultiplier.multiplyFromWTnaf(com.android.org.bouncycastle.math.ec.ECPoint$F2m, byte[], com.android.org.bouncycastle.math.ec.PreCompInfo):com.android.org.bouncycastle.math.ec.ECPoint$F2m
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.math.ec.WTauNafMultiplier.multiplyFromWTnaf(com.android.org.bouncycastle.math.ec.ECPoint$F2m, byte[], com.android.org.bouncycastle.math.ec.PreCompInfo):com.android.org.bouncycastle.math.ec.ECPoint$F2m");
    }

    protected com.android.org.bouncycastle.math.ec.ECPoint multiplyPositive(com.android.org.bouncycastle.math.ec.ECPoint r1, java.math.BigInteger r2) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.bouncycastle.math.ec.WTauNafMultiplier.multiplyPositive(com.android.org.bouncycastle.math.ec.ECPoint, java.math.BigInteger):com.android.org.bouncycastle.math.ec.ECPoint
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.bouncycastle.math.ec.WTauNafMultiplier.multiplyPositive(com.android.org.bouncycastle.math.ec.ECPoint, java.math.BigInteger):com.android.org.bouncycastle.math.ec.ECPoint
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.math.ec.WTauNafMultiplier.multiplyPositive(com.android.org.bouncycastle.math.ec.ECPoint, java.math.BigInteger):com.android.org.bouncycastle.math.ec.ECPoint");
    }

    private F2m multiplyWTnaf(F2m p, ZTauElement lambda, PreCompInfo preCompInfo, byte a, byte mu) {
        ZTauElement[] alpha;
        if (a == null) {
            alpha = Tnaf.alpha0;
        } else {
            alpha = Tnaf.alpha1;
        }
        return multiplyFromWTnaf(p, Tnaf.tauAdicWNaf(mu, lambda, (byte) 4, BigInteger.valueOf(16), Tnaf.getTw(mu, 4), alpha), preCompInfo);
    }
}
