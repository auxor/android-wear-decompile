package com.android.internal.telephony.cat;

public enum ResultCode {
    OK(0),
    PRFRMD_WITH_PARTIAL_COMPREHENSION(1),
    PRFRMD_WITH_MISSING_INFO(2),
    PRFRMD_WITH_ADDITIONAL_EFS_READ(3),
    PRFRMD_ICON_NOT_DISPLAYED(4),
    PRFRMD_MODIFIED_BY_NAA(5),
    PRFRMD_LIMITED_SERVICE(6),
    PRFRMD_WITH_MODIFICATION(7),
    PRFRMD_NAA_NOT_ACTIVE(8),
    PRFRMD_TONE_NOT_PLAYED(9),
    UICC_SESSION_TERM_BY_USER(16),
    BACKWARD_MOVE_BY_USER(17),
    NO_RESPONSE_FROM_USER(18),
    HELP_INFO_REQUIRED(19),
    USSD_SS_SESSION_TERM_BY_USER(20),
    TERMINAL_CRNTLY_UNABLE_TO_PROCESS(32),
    NETWORK_CRNTLY_UNABLE_TO_PROCESS(33),
    USER_NOT_ACCEPT(34),
    USER_CLEAR_DOWN_CALL(35),
    CONTRADICTION_WITH_TIMER(36),
    NAA_CALL_CONTROL_TEMPORARY(37),
    LAUNCH_BROWSER_ERROR(38),
    MMS_TEMPORARY(39),
    BEYOND_TERMINAL_CAPABILITY(48),
    CMD_TYPE_NOT_UNDERSTOOD(49),
    CMD_DATA_NOT_UNDERSTOOD(50),
    CMD_NUM_NOT_KNOWN(51),
    SS_RETURN_ERROR(52),
    SMS_RP_ERROR(53),
    REQUIRED_VALUES_MISSING(54),
    USSD_RETURN_ERROR(55),
    MULTI_CARDS_CMD_ERROR(56),
    USIM_CALL_CONTROL_PERMANENT(57),
    BIP_ERROR(58),
    ACCESS_TECH_UNABLE_TO_PROCESS(59),
    FRAMES_ERROR(60),
    MMS_ERROR(61);
    
    private int mCode;

    private ResultCode(int r3) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.telephony.cat.ResultCode.<init>(java.lang.String, int, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.telephony.cat.ResultCode.<init>(java.lang.String, int, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.internal.telephony.cat.ResultCode.<init>(java.lang.String, int, int):void");
    }

    public static com.android.internal.telephony.cat.ResultCode fromInt(int r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.telephony.cat.ResultCode.fromInt(int):com.android.internal.telephony.cat.ResultCode
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.telephony.cat.ResultCode.fromInt(int):com.android.internal.telephony.cat.ResultCode
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.internal.telephony.cat.ResultCode.fromInt(int):com.android.internal.telephony.cat.ResultCode");
    }

    public int value() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.telephony.cat.ResultCode.value():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.telephony.cat.ResultCode.value():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.internal.telephony.cat.ResultCode.value():int");
    }
}
