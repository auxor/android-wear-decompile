package android.telephony;

class JapanesePhoneNumberFormatter {
    private static short[] FORMAT_MAP;

    public static void format(android.text.Editable r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.telephony.JapanesePhoneNumberFormatter.format(android.text.Editable):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.telephony.JapanesePhoneNumberFormatter.format(android.text.Editable):void
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
        throw new UnsupportedOperationException("Method not decompiled: android.telephony.JapanesePhoneNumberFormatter.format(android.text.Editable):void");
    }

    JapanesePhoneNumberFormatter() {
    }

    static {
        FORMAT_MAP = new short[]{(short) -100, (short) 10, (short) 220, (short) -15, (short) 410, (short) 530, (short) 1200, (short) 670, (short) 780, (short) 1060, (short) -100, (short) -25, (short) 20, (short) 40, (short) 70, (short) 100, (short) 150, (short) 190, (short) 200, (short) 210, (short) -36, (short) -100, (short) -100, (short) -35, (short) -35, (short) -35, (short) 30, (short) -100, (short) -100, (short) -100, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -45, (short) -35, (short) -35, (short) -100, (short) -100, (short) -100, (short) -35, (short) -35, (short) -35, (short) -35, (short) 50, (short) -35, (short) 60, (short) -35, (short) -35, (short) -45, (short) -35, (short) -45, (short) -35, (short) -35, (short) -45, (short) -35, (short) -35, (short) -35, (short) -35, (short) -45, (short) -35, (short) -35, (short) -35, (short) -35, (short) -45, (short) -45, (short) -35, (short) -100, (short) -100, (short) -35, (short) -35, (short) -35, (short) 80, (short) 90, (short) -100, (short) -100, (short) -100, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -45, (short) -45, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -45, (short) -35, (short) -35, (short) -35, (short) -25, (short) -25, (short) -35, (short) -35, (short) 110, (short) 120, (short) 130, (short) -35, (short) 140, (short) -25, (short) -35, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -45, (short) -25, (short) -35, (short) -35, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -45, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -45, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -45, (short) -45, (short) -35, (short) -35, (short) -100, (short) -100, (short) -35, (short) 160, (short) 170, (short) 180, (short) -35, (short) -35, (short) -100, (short) -100, (short) -35, (short) -35, (short) -45, (short) -35, (short) -45, (short) -45, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -45, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -45, (short) -45, (short) -45, (short) -35, (short) -45, (short) -35, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -35, (short) -35, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -35, (short) -35, (short) -25, (short) -100, (short) -100, (short) 230, (short) 250, (short) 260, (short) 270, (short) 320, (short) 340, (short) 360, (short) 390, (short) -35, (short) -25, (short) -25, (short) 240, (short) -35, (short) -35, (short) -35, (short) -25, (short) -35, (short) -35, (short) -25, (short) -35, (short) -35, (short) -35, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) -25, (short) -35, (short) -35, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -35, (short) -35, (short) -35, (short) -25, (short) -35, (short) -25, (short) -25, (short) -25, (short) -35, (short) 280, (short) 290, (short) 300, (short) 310, (short) -35, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -35, (short) -35, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -35, (short) 330, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -25, (short) -25, (short) -25, (short) -35, (short) -25, (short) -25, (short) -25, (short) -35, (short) -25, (short) -35, (short) -35, (short) 350, (short) -35, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -25, (short) -35, (short) -25, (short) -35, (short) 370, (short) -35, (short) -35, (short) -25, (short) -35, (short) -35, (short) 380, (short) -25, (short) -35, (short) -35, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -35, (short) -25, (short) -25, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -35, (short) -25, (short) 400, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -35, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -15, (short) -15, (short) 420, (short) 460, (short) -25, (short) -25, (short) 470, (short) 480, (short) 500, (short) 510, (short) -15, (short) -25, (short) 430, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) 440, (short) 450, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -25, (short) -35, (short) -35, (short) -25, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) -15, (short) -25, (short) -15, (short) -15, (short) -15, (short) -15, (short) -15, (short) -25, (short) -25, (short) -15, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -35, (short) -25, (short) -35, (short) -35, (short) -35, (short) -25, (short) -25, (short) -35, (short) -25, (short) -35, (short) -35, (short) -35, (short) -25, (short) -25, (short) 490, (short) -15, (short) -25, (short) -25, (short) -25, (short) -35, (short) -35, (short) -25, (short) -35, (short) -35, (short) -15, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -15, (short) -35, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) -25, (short) -25, (short) -25, (short) 520, (short) -100, (short) -100, (short) -45, (short) -100, (short) -45, (short) -100, (short) -45, (short) -100, (short) -45, (short) -100, (short) -26, (short) -100, (short) -25, (short) 540, (short) 580, (short) 590, (short) 600, (short) 610, (short) 630, (short) 640, (short) -25, (short) -35, (short) -35, (short) -35, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) 550, (short) -35, (short) -35, (short) -25, (short) -25, (short) -25, (short) -25, (short) 560, (short) 570, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -35, (short) -25, (short) -25, (short) -35, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -35, (short) -35, (short) -25, (short) -35, (short) -35, (short) -25, (short) -35, (short) -35, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -100, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -36, (short) -100, (short) -35, (short) -35, (short) -35, (short) -35, (short) 620, (short) -35, (short) -35, (short) -100, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -45, (short) -25, (short) -35, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) 650, (short) -35, (short) 660, (short) -35, (short) -35, (short) -35, (short) -35, (short) -45, (short) -35, (short) -35, (short) -35, (short) -35, (short) -45, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -26, (short) -100, (short) 680, (short) 690, (short) 700, (short) -25, (short) 720, (short) 730, (short) -25, (short) 740, (short) -25, (short) -35, (short) -25, (short) -25, (short) -25, (short) -35, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -100, (short) -35, (short) -35, (short) -35, (short) -35, (short) 710, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -45, (short) -35, (short) -25, (short) -35, (short) -25, (short) -35, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -35, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -25, (short) 750, (short) 760, (short) 770, (short) -35, (short) -35, (short) -35, (short) -25, (short) -35, (short) -25, (short) -25, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -25, (short) -35, (short) -35, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -25, (short) -35, (short) -35, (short) 790, (short) -100, (short) 800, (short) 850, (short) 900, (short) 920, (short) 940, (short) 1030, (short) 1040, (short) 1050, (short) -36, (short) -26, (short) -26, (short) -26, (short) -26, (short) -26, (short) -26, (short) -26, (short) -26, (short) -26, (short) -35, (short) -25, (short) -25, (short) -35, (short) 810, (short) -25, (short) -35, (short) -35, (short) -25, (short) 820, (short) -25, (short) -35, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -25, (short) -35, (short) 830, (short) -35, (short) 840, (short) -35, (short) -25, (short) -35, (short) -35, (short) -25, (short) -35, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -100, (short) -25, (short) -25, (short) -25, (short) -100, (short) -100, (short) -100, (short) -100, (short) -100, (short) -100, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) 860, (short) -35, (short) 870, (short) 880, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -45, (short) -45, (short) -35, (short) -100, (short) -100, (short) -100, (short) -100, (short) -100, (short) -100, (short) 890, (short) -100, (short) -100, (short) -100, (short) -25, (short) -45, (short) -45, (short) -25, (short) -45, (short) -45, (short) -25, (short) -45, (short) -45, (short) -45, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -35, (short) -35, (short) 910, (short) -35, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -45, (short) -35, (short) -35, (short) -100, (short) 930, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -100, (short) -100, (short) -45, (short) -100, (short) -45, (short) -100, (short) -100, (short) -100, (short) -100, (short) -100, (short) -25, (short) -25, (short) -25, (short) 950, (short) -25, (short) 970, (short) 990, (short) -35, (short) 1000, (short) 1010, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) 960, (short) -35, (short) -35, (short) -35, (short) -45, (short) -45, (short) -45, (short) -45, (short) -45, (short) -45, (short) -35, (short) -45, (short) -45, (short) -45, (short) -35, (short) -35, (short) -25, (short) -35, (short) -35, (short) 980, (short) -35, (short) -35, (short) -35, (short) -35, (short) -100, (short) -100, (short) -25, (short) -25, (short) -100, (short) -100, (short) -100, (short) -100, (short) -100, (short) -100, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) 1020, (short) -45, (short) -45, (short) -35, (short) -35, (short) -45, (short) -45, (short) -45, (short) -45, (short) -45, (short) -45, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -35, (short) -25, (short) -35, (short) -25, (short) -35, (short) -35, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) -25, (short) -35, (short) -25, (short) -35, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -26, (short) -100, (short) 1070, (short) 1080, (short) 1090, (short) 1110, (short) 1120, (short) 1130, (short) 1140, (short) 1160, (short) -35, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -35, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -25, (short) -35, (short) -100, (short) -35, (short) -35, (short) -35, (short) -100, (short) -35, (short) -35, (short) -35, (short) 1100, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -45, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -35, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -35, (short) -25, (short) -25, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) 1150, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -25, (short) -35, (short) -35, (short) -45, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) 1170, (short) -25, (short) -35, (short) 1180, (short) -35, (short) 1190, (short) -35, (short) -25, (short) -25, (short) -100, (short) -100, (short) -45, (short) -45, (short) -100, (short) -100, (short) -100, (short) -100, (short) -100, (short) -100, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -25, (short) -25, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -35, (short) -45, (short) -26, (short) -15, (short) -15, (short) -15, (short) -15, (short) -15, (short) -15, (short) -15, (short) -15, (short) -15};
    }
}
