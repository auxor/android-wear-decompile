package android.net.rtp;

import java.util.Arrays;

public class AudioCodec {
    public static final AudioCodec AMR;
    public static final AudioCodec GSM;
    public static final AudioCodec GSM_EFR;
    public static final AudioCodec PCMA;
    public static final AudioCodec PCMU;
    private static final AudioCodec[] sCodecs;
    public final String fmtp;
    public final String rtpmap;
    public final int type;

    private AudioCodec(int r1, java.lang.String r2, java.lang.String r3) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.net.rtp.AudioCodec.<init>(int, java.lang.String, java.lang.String):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.net.rtp.AudioCodec.<init>(int, java.lang.String, java.lang.String):void
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
        throw new UnsupportedOperationException("Method not decompiled: android.net.rtp.AudioCodec.<init>(int, java.lang.String, java.lang.String):void");
    }

    public static android.net.rtp.AudioCodec getCodec(int r1, java.lang.String r2, java.lang.String r3) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.net.rtp.AudioCodec.getCodec(int, java.lang.String, java.lang.String):android.net.rtp.AudioCodec
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.net.rtp.AudioCodec.getCodec(int, java.lang.String, java.lang.String):android.net.rtp.AudioCodec
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
        throw new UnsupportedOperationException("Method not decompiled: android.net.rtp.AudioCodec.getCodec(int, java.lang.String, java.lang.String):android.net.rtp.AudioCodec");
    }

    static {
        PCMU = new AudioCodec(0, "PCMU/8000", null);
        PCMA = new AudioCodec(8, "PCMA/8000", null);
        GSM = new AudioCodec(3, "GSM/8000", null);
        GSM_EFR = new AudioCodec(96, "GSM-EFR/8000", null);
        AMR = new AudioCodec(97, "AMR/8000", null);
        sCodecs = new AudioCodec[]{GSM_EFR, AMR, GSM, PCMU, PCMA};
    }

    public static AudioCodec[] getCodecs() {
        return (AudioCodec[]) Arrays.copyOf(sCodecs, sCodecs.length);
    }
}
