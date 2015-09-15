package com.android.internal.telephony.gsm;

public class SuppServiceNotification {
    public static final int MO_CODE_CALL_DEFLECTED = 8;
    public static final int MO_CODE_CALL_FORWARDED = 2;
    public static final int MO_CODE_CALL_IS_WAITING = 3;
    public static final int MO_CODE_CLIR_SUPPRESSION_REJECTED = 7;
    public static final int MO_CODE_CUG_CALL = 4;
    public static final int MO_CODE_INCOMING_CALLS_BARRED = 6;
    public static final int MO_CODE_OUTGOING_CALLS_BARRED = 5;
    public static final int MO_CODE_SOME_CF_ACTIVE = 1;
    public static final int MO_CODE_UNCONDITIONAL_CF_ACTIVE = 0;
    public static final int MT_CODE_ADDITIONAL_CALL_FORWARDED = 10;
    public static final int MT_CODE_CALL_CONNECTED_ECT = 8;
    public static final int MT_CODE_CALL_CONNECTING_ECT = 7;
    public static final int MT_CODE_CALL_ON_HOLD = 2;
    public static final int MT_CODE_CALL_RETRIEVED = 3;
    public static final int MT_CODE_CUG_CALL = 1;
    public static final int MT_CODE_DEFLECTED_CALL = 9;
    public static final int MT_CODE_FORWARDED_CALL = 0;
    public static final int MT_CODE_FORWARD_CHECK_RECEIVED = 6;
    public static final int MT_CODE_MULTI_PARTY_CALL = 4;
    public static final int MT_CODE_ON_HOLD_CALL_RELEASED = 5;
    public int code;
    public int index;
    public int notificationType;
    public String number;
    public int type;

    public java.lang.String toString() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.telephony.gsm.SuppServiceNotification.toString():java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.telephony.gsm.SuppServiceNotification.toString():java.lang.String
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.internal.telephony.gsm.SuppServiceNotification.toString():java.lang.String");
    }
}
