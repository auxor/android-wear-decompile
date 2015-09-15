package com.android.internal.telephony.cdma;

import java.util.HashMap;

public class SignalToneUtil {
    public static final int CDMA_INVALID_TONE = -1;
    public static final int IS95_CONST_IR_ALERT_HIGH = 1;
    public static final int IS95_CONST_IR_ALERT_LOW = 2;
    public static final int IS95_CONST_IR_ALERT_MED = 0;
    public static final int IS95_CONST_IR_SIGNAL_IS54B = 2;
    public static final int IS95_CONST_IR_SIGNAL_ISDN = 1;
    public static final int IS95_CONST_IR_SIGNAL_TONE = 0;
    public static final int IS95_CONST_IR_SIGNAL_USR_DEFD_ALERT = 4;
    public static final int IS95_CONST_IR_SIG_IS54B_L = 1;
    public static final int IS95_CONST_IR_SIG_IS54B_NO_TONE = 0;
    public static final int IS95_CONST_IR_SIG_IS54B_PBX_L = 7;
    public static final int IS95_CONST_IR_SIG_IS54B_PBX_SLS = 10;
    public static final int IS95_CONST_IR_SIG_IS54B_PBX_SS = 8;
    public static final int IS95_CONST_IR_SIG_IS54B_PBX_SSL = 9;
    public static final int IS95_CONST_IR_SIG_IS54B_PBX_S_X4 = 11;
    public static final int IS95_CONST_IR_SIG_IS54B_SLS = 5;
    public static final int IS95_CONST_IR_SIG_IS54B_SS = 2;
    public static final int IS95_CONST_IR_SIG_IS54B_SSL = 3;
    public static final int IS95_CONST_IR_SIG_IS54B_SS_2 = 4;
    public static final int IS95_CONST_IR_SIG_IS54B_S_X4 = 6;
    public static final int IS95_CONST_IR_SIG_ISDN_INTGRP = 1;
    public static final int IS95_CONST_IR_SIG_ISDN_NORMAL = 0;
    public static final int IS95_CONST_IR_SIG_ISDN_OFF = 15;
    public static final int IS95_CONST_IR_SIG_ISDN_PAT_3 = 3;
    public static final int IS95_CONST_IR_SIG_ISDN_PAT_5 = 5;
    public static final int IS95_CONST_IR_SIG_ISDN_PAT_6 = 6;
    public static final int IS95_CONST_IR_SIG_ISDN_PAT_7 = 7;
    public static final int IS95_CONST_IR_SIG_ISDN_PING = 4;
    public static final int IS95_CONST_IR_SIG_ISDN_SP_PRI = 2;
    public static final int IS95_CONST_IR_SIG_TONE_ABBR_ALRT = 0;
    public static final int IS95_CONST_IR_SIG_TONE_ABB_INT = 3;
    public static final int IS95_CONST_IR_SIG_TONE_ABB_RE = 5;
    public static final int IS95_CONST_IR_SIG_TONE_ANSWER = 8;
    public static final int IS95_CONST_IR_SIG_TONE_BUSY = 6;
    public static final int IS95_CONST_IR_SIG_TONE_CALL_W = 9;
    public static final int IS95_CONST_IR_SIG_TONE_CONFIRM = 7;
    public static final int IS95_CONST_IR_SIG_TONE_DIAL = 0;
    public static final int IS95_CONST_IR_SIG_TONE_INT = 2;
    public static final int IS95_CONST_IR_SIG_TONE_NO_TONE = 63;
    public static final int IS95_CONST_IR_SIG_TONE_PIP = 10;
    public static final int IS95_CONST_IR_SIG_TONE_REORDER = 4;
    public static final int IS95_CONST_IR_SIG_TONE_RING = 1;
    public static final int TAPIAMSSCDMA_SIGNAL_PITCH_UNKNOWN = 0;
    private static HashMap<Integer, Integer> mHm;

    public static int getAudioToneFromSignalInfo(int r1, int r2, int r3) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.telephony.cdma.SignalToneUtil.getAudioToneFromSignalInfo(int, int, int):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.telephony.cdma.SignalToneUtil.getAudioToneFromSignalInfo(int, int, int):int
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.internal.telephony.cdma.SignalToneUtil.getAudioToneFromSignalInfo(int, int, int):int");
    }

    static {
        mHm = new HashMap();
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_RING, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_DIAL), Integer.valueOf(45));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_RING, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_RING), Integer.valueOf(46));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_RING, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_INT), Integer.valueOf(47));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_RING, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_ABB_INT), Integer.valueOf(48));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_RING, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_REORDER), Integer.valueOf(49));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_RING, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_ABB_RE), Integer.valueOf(50));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_RING, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_BUSY), Integer.valueOf(51));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_RING, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_CONFIRM), Integer.valueOf(52));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_RING, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_ISDN_OFF), Integer.valueOf(98));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_DIAL), Integer.valueOf(34));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_RING), Integer.valueOf(35));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_INT), Integer.valueOf(29));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_ABB_INT), Integer.valueOf(30));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_REORDER), Integer.valueOf(38));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_ABB_RE), Integer.valueOf(39));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_BUSY), Integer.valueOf(40));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_CONFIRM), Integer.valueOf(32));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_ANSWER), Integer.valueOf(42));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_CALL_W), Integer.valueOf(43));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_PIP), Integer.valueOf(44));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_NO_TONE), Integer.valueOf(98));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_RING, IS95_CONST_IR_SIG_TONE_RING), Integer.valueOf(53));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_RING), Integer.valueOf(54));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_RING), Integer.valueOf(55));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_RING, IS95_CONST_IR_SIG_TONE_INT), Integer.valueOf(56));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_INT), Integer.valueOf(57));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_INT), Integer.valueOf(58));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_RING, IS95_CONST_IR_SIG_TONE_ABB_INT), Integer.valueOf(59));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_ABB_INT), Integer.valueOf(60));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_ABB_INT), Integer.valueOf(61));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_RING, IS95_CONST_IR_SIG_TONE_REORDER), Integer.valueOf(62));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_REORDER), Integer.valueOf(IS95_CONST_IR_SIG_TONE_NO_TONE));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_REORDER), Integer.valueOf(64));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_RING, IS95_CONST_IR_SIG_TONE_ABB_RE), Integer.valueOf(65));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_ABB_RE), Integer.valueOf(66));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_ABB_RE), Integer.valueOf(67));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_RING, IS95_CONST_IR_SIG_TONE_BUSY), Integer.valueOf(68));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_BUSY), Integer.valueOf(69));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_BUSY), Integer.valueOf(70));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_RING, IS95_CONST_IR_SIG_TONE_CONFIRM), Integer.valueOf(71));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_CONFIRM), Integer.valueOf(72));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_CONFIRM), Integer.valueOf(73));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_RING, IS95_CONST_IR_SIG_TONE_ANSWER), Integer.valueOf(74));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_ANSWER), Integer.valueOf(75));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_ANSWER), Integer.valueOf(76));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_RING, IS95_CONST_IR_SIG_TONE_CALL_W), Integer.valueOf(77));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_CALL_W), Integer.valueOf(78));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_CALL_W), Integer.valueOf(79));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_RING, IS95_CONST_IR_SIG_TONE_PIP), Integer.valueOf(80));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_PIP), Integer.valueOf(81));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_PIP), Integer.valueOf(82));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_RING, IS95_CONST_IR_SIG_IS54B_PBX_S_X4), Integer.valueOf(83));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_IS54B_PBX_S_X4), Integer.valueOf(84));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_IS54B_PBX_S_X4), Integer.valueOf(85));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_INT, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_DIAL), Integer.valueOf(98));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_REORDER, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_DIAL), Integer.valueOf(97));
        mHm.put(signalParamHash(IS95_CONST_IR_SIG_TONE_REORDER, IS95_CONST_IR_SIG_TONE_DIAL, IS95_CONST_IR_SIG_TONE_NO_TONE), Integer.valueOf(97));
    }

    private static Integer signalParamHash(int signalType, int alertPitch, int signal) {
        if (signalType < 0 || signalType > 256 || alertPitch > 256 || alertPitch < 0 || signal > 256 || signal < 0) {
            return new Integer(CDMA_INVALID_TONE);
        }
        if (signalType != IS95_CONST_IR_SIG_TONE_INT) {
            alertPitch = IS95_CONST_IR_SIG_TONE_DIAL;
        }
        return new Integer((((signalType * 256) * 256) + (alertPitch * 256)) + signal);
    }

    private SignalToneUtil() {
    }
}
