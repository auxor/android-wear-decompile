package com.android.org.bouncycastle.asn1.x509;

import com.android.org.bouncycastle.asn1.DERBitString;

public class ReasonFlags extends DERBitString {
    public static final int AA_COMPROMISE = 32768;
    public static final int AFFILIATION_CHANGED = 16;
    public static final int CA_COMPROMISE = 32;
    public static final int CERTIFICATE_HOLD = 2;
    public static final int CESSATION_OF_OPERATION = 4;
    public static final int KEY_COMPROMISE = 64;
    public static final int PRIVILEGE_WITHDRAWN = 1;
    public static final int SUPERSEDED = 8;
    public static final int UNUSED = 128;
    public static final int aACompromise = 32768;
    public static final int affiliationChanged = 16;
    public static final int cACompromise = 32;
    public static final int certificateHold = 2;
    public static final int cessationOfOperation = 4;
    public static final int keyCompromise = 64;
    public static final int privilegeWithdrawn = 1;
    public static final int superseded = 8;
    public static final int unused = 128;

    public ReasonFlags(com.android.org.bouncycastle.asn1.DERBitString r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.bouncycastle.asn1.x509.ReasonFlags.<init>(com.android.org.bouncycastle.asn1.DERBitString):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.bouncycastle.asn1.x509.ReasonFlags.<init>(com.android.org.bouncycastle.asn1.DERBitString):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.org.bouncycastle.asn1.x509.ReasonFlags.<init>(com.android.org.bouncycastle.asn1.DERBitString):void");
    }

    public ReasonFlags(int reasons) {
        super(DERBitString.getBytes(reasons), DERBitString.getPadBits(reasons));
    }
}
