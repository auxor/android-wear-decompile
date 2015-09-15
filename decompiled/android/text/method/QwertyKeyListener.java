package android.text.method;

import android.text.NoCopySpan;
import android.text.method.TextKeyListener.Capitalize;
import android.util.SparseArray;
import com.android.internal.telephony.RILConstants;

public class QwertyKeyListener extends BaseKeyListener {
    private static SparseArray<String> PICKER_SETS;
    private static QwertyKeyListener sFullKeyboardInstance;
    private static QwertyKeyListener[] sInstance;
    private Capitalize mAutoCap;
    private boolean mAutoText;
    private boolean mFullKeyboard;

    static class Replaced implements NoCopySpan {
        private char[] mText;

        public Replaced(char[] r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.text.method.QwertyKeyListener.Replaced.<init>(char[]):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.text.method.QwertyKeyListener.Replaced.<init>(char[]):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.text.method.QwertyKeyListener.Replaced.<init>(char[]):void");
        }

        static /* synthetic */ char[] access$000(android.text.method.QwertyKeyListener.Replaced r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.text.method.QwertyKeyListener.Replaced.access$000(android.text.method.QwertyKeyListener$Replaced):char[]
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.text.method.QwertyKeyListener.Replaced.access$000(android.text.method.QwertyKeyListener$Replaced):char[]
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.text.method.QwertyKeyListener.Replaced.access$000(android.text.method.QwertyKeyListener$Replaced):char[]");
        }
    }

    private QwertyKeyListener(android.text.method.TextKeyListener.Capitalize r1, boolean r2, boolean r3) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.text.method.QwertyKeyListener.<init>(android.text.method.TextKeyListener$Capitalize, boolean, boolean):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.text.method.QwertyKeyListener.<init>(android.text.method.TextKeyListener$Capitalize, boolean, boolean):void
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
        throw new UnsupportedOperationException("Method not decompiled: android.text.method.QwertyKeyListener.<init>(android.text.method.TextKeyListener$Capitalize, boolean, boolean):void");
    }

    public static android.text.method.QwertyKeyListener getInstance(boolean r1, android.text.method.TextKeyListener.Capitalize r2) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.text.method.QwertyKeyListener.getInstance(boolean, android.text.method.TextKeyListener$Capitalize):android.text.method.QwertyKeyListener
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.text.method.QwertyKeyListener.getInstance(boolean, android.text.method.TextKeyListener$Capitalize):android.text.method.QwertyKeyListener
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
        throw new UnsupportedOperationException("Method not decompiled: android.text.method.QwertyKeyListener.getInstance(boolean, android.text.method.TextKeyListener$Capitalize):android.text.method.QwertyKeyListener");
    }

    private java.lang.String getReplacement(java.lang.CharSequence r1, int r2, int r3, android.view.View r4) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.text.method.QwertyKeyListener.getReplacement(java.lang.CharSequence, int, int, android.view.View):java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.text.method.QwertyKeyListener.getReplacement(java.lang.CharSequence, int, int, android.view.View):java.lang.String
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
        throw new UnsupportedOperationException("Method not decompiled: android.text.method.QwertyKeyListener.getReplacement(java.lang.CharSequence, int, int, android.view.View):java.lang.String");
    }

    public static void markAsReplaced(android.text.Spannable r1, int r2, int r3, java.lang.String r4) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.text.method.QwertyKeyListener.markAsReplaced(android.text.Spannable, int, int, java.lang.String):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.text.method.QwertyKeyListener.markAsReplaced(android.text.Spannable, int, int, java.lang.String):void
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
        throw new UnsupportedOperationException("Method not decompiled: android.text.method.QwertyKeyListener.markAsReplaced(android.text.Spannable, int, int, java.lang.String):void");
    }

    private boolean showCharacterPicker(android.view.View r1, android.text.Editable r2, char r3, boolean r4, int r5) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.text.method.QwertyKeyListener.showCharacterPicker(android.view.View, android.text.Editable, char, boolean, int):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.text.method.QwertyKeyListener.showCharacterPicker(android.view.View, android.text.Editable, char, boolean, int):boolean
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
        throw new UnsupportedOperationException("Method not decompiled: android.text.method.QwertyKeyListener.showCharacterPicker(android.view.View, android.text.Editable, char, boolean, int):boolean");
    }

    private static java.lang.String toTitleCase(java.lang.String r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.text.method.QwertyKeyListener.toTitleCase(java.lang.String):java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.text.method.QwertyKeyListener.toTitleCase(java.lang.String):java.lang.String
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
        throw new UnsupportedOperationException("Method not decompiled: android.text.method.QwertyKeyListener.toTitleCase(java.lang.String):java.lang.String");
    }

    public int getInputType() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.text.method.QwertyKeyListener.getInputType():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.text.method.QwertyKeyListener.getInputType():int
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
        throw new UnsupportedOperationException("Method not decompiled: android.text.method.QwertyKeyListener.getInputType():int");
    }

    public boolean onKeyDown(android.view.View r1, android.text.Editable r2, int r3, android.view.KeyEvent r4) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.text.method.QwertyKeyListener.onKeyDown(android.view.View, android.text.Editable, int, android.view.KeyEvent):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.text.method.QwertyKeyListener.onKeyDown(android.view.View, android.text.Editable, int, android.view.KeyEvent):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00ea
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.method.QwertyKeyListener.onKeyDown(android.view.View, android.text.Editable, int, android.view.KeyEvent):boolean");
    }

    static {
        sInstance = new QwertyKeyListener[(Capitalize.values().length * 2)];
        PICKER_SETS = new SparseArray();
        PICKER_SETS.put(65, "\u00c0\u00c1\u00c2\u00c4\u00c6\u00c3\u00c5\u0104\u0100");
        PICKER_SETS.put(67, "\u00c7\u0106\u010c");
        PICKER_SETS.put(68, "\u010e");
        PICKER_SETS.put(69, "\u00c8\u00c9\u00ca\u00cb\u0118\u011a\u0112");
        PICKER_SETS.put(71, "\u011e");
        PICKER_SETS.put(76, "\u0141");
        PICKER_SETS.put(73, "\u00cc\u00cd\u00ce\u00cf\u012a\u0130");
        PICKER_SETS.put(78, "\u00d1\u0143\u0147");
        PICKER_SETS.put(79, "\u00d8\u0152\u00d5\u00d2\u00d3\u00d4\u00d6\u014c");
        PICKER_SETS.put(82, "\u0158");
        PICKER_SETS.put(83, "\u015a\u0160\u015e");
        PICKER_SETS.put(84, "\u0164");
        PICKER_SETS.put(85, "\u00d9\u00da\u00db\u00dc\u016e\u016a");
        PICKER_SETS.put(89, "\u00dd\u0178");
        PICKER_SETS.put(90, "\u0179\u017b\u017d");
        PICKER_SETS.put(97, "\u00e0\u00e1\u00e2\u00e4\u00e6\u00e3\u00e5\u0105\u0101");
        PICKER_SETS.put(99, "\u00e7\u0107\u010d");
        PICKER_SETS.put(100, "\u010f");
        PICKER_SETS.put(RILConstants.RIL_REQUEST_SET_SMSC_ADDRESS, "\u00e8\u00e9\u00ea\u00eb\u0119\u011b\u0113");
        PICKER_SETS.put(RILConstants.RIL_REQUEST_REPORT_STK_SERVICE_IS_RUNNING, "\u011f");
        PICKER_SETS.put(RILConstants.RIL_REQUEST_ISIM_AUTHENTICATION, "\u00ec\u00ed\u00ee\u00ef\u012b\u0131");
        PICKER_SETS.put(RILConstants.RIL_REQUEST_VOICE_RADIO_TECH, "\u0142");
        PICKER_SETS.put(RILConstants.RIL_REQUEST_SET_UNSOL_CELL_INFO_LIST_RATE, "\u00f1\u0144\u0148");
        PICKER_SETS.put(RILConstants.RIL_REQUEST_SET_INITIAL_ATTACH_APN, "\u00f8\u0153\u00f5\u00f2\u00f3\u00f4\u00f6\u014d");
        PICKER_SETS.put(RILConstants.RIL_REQUEST_SIM_TRANSMIT_APDU_BASIC, "\u0159");
        PICKER_SETS.put(RILConstants.RIL_REQUEST_SIM_OPEN_CHANNEL, "\u00a7\u00df\u015b\u0161\u015f");
        PICKER_SETS.put(RILConstants.RIL_REQUEST_SIM_CLOSE_CHANNEL, "\u0165");
        PICKER_SETS.put(RILConstants.RIL_REQUEST_SIM_TRANSMIT_APDU_CHANNEL, "\u00f9\u00fa\u00fb\u00fc\u016f\u016b");
        PICKER_SETS.put(RILConstants.RIL_REQUEST_NV_RESET_CONFIG, "\u00fd\u00ff");
        PICKER_SETS.put(RILConstants.RIL_REQUEST_SET_UICC_SUBSCRIPTION, "\u017a\u017c\u017e");
        PICKER_SETS.put(61185, "\u2026\u00a5\u2022\u00ae\u00a9\u00b1[]{}\\|");
        PICKER_SETS.put(47, "\\");
        PICKER_SETS.put(49, "\u00b9\u00bd\u2153\u00bc\u215b");
        PICKER_SETS.put(50, "\u00b2\u2154");
        PICKER_SETS.put(51, "\u00b3\u00be\u215c");
        PICKER_SETS.put(52, "\u2074");
        PICKER_SETS.put(53, "\u215d");
        PICKER_SETS.put(55, "\u215e");
        PICKER_SETS.put(48, "\u207f\u2205");
        PICKER_SETS.put(36, "\u00a2\u00a3\u20ac\u00a5\u20a3\u20a4\u20b1");
        PICKER_SETS.put(37, "\u2030");
        PICKER_SETS.put(42, "\u2020\u2021");
        PICKER_SETS.put(45, "\u2013\u2014");
        PICKER_SETS.put(43, "\u00b1");
        PICKER_SETS.put(40, "[{<");
        PICKER_SETS.put(41, "]}>");
        PICKER_SETS.put(33, "\u00a1");
        PICKER_SETS.put(34, "\u201c\u201d\u00ab\u00bb\u02dd");
        PICKER_SETS.put(63, "\u00bf");
        PICKER_SETS.put(44, "\u201a\u201e");
        PICKER_SETS.put(61, "\u2260\u2248\u221e");
        PICKER_SETS.put(60, "\u2264\u00ab\u2039");
        PICKER_SETS.put(62, "\u2265\u00bb\u203a");
    }

    public QwertyKeyListener(Capitalize cap, boolean autoText) {
        this(cap, autoText, false);
    }

    public static QwertyKeyListener getInstanceForFullKeyboard() {
        if (sFullKeyboardInstance == null) {
            sFullKeyboardInstance = new QwertyKeyListener(Capitalize.NONE, false, true);
        }
        return sFullKeyboardInstance;
    }
}
