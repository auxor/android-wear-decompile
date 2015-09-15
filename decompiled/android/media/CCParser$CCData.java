package android.media;

import android.media.CCParser.StyleCode;
import android.view.KeyEvent;
import android.view.MotionEvent;

/* compiled from: ClosedCaptionRenderer */
class CCParser$CCData {
    private static final String[] mCtrlCodeMap;
    private static final String[] mProtugueseCharMap;
    private static final String[] mSpanishCharMap;
    private static final String[] mSpecialCharMap;
    private final byte mData1;
    private final byte mData2;
    private final byte mType;

    CCParser$CCData(byte r1, byte r2, byte r3) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.CCParser$CCData.<init>(byte, byte, byte):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.CCParser$CCData.<init>(byte, byte, byte):void
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
        throw new UnsupportedOperationException("Method not decompiled: android.media.CCParser$CCData.<init>(byte, byte, byte):void");
    }

    private java.lang.String getBasicChars() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.CCParser$CCData.getBasicChars():java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.CCParser$CCData.getBasicChars():java.lang.String
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
        throw new UnsupportedOperationException("Method not decompiled: android.media.CCParser$CCData.getBasicChars():java.lang.String");
    }

    public java.lang.String toString() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.CCParser$CCData.toString():java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.CCParser$CCData.toString():java.lang.String
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
        throw new UnsupportedOperationException("Method not decompiled: android.media.CCParser$CCData.toString():java.lang.String");
    }

    static {
        mCtrlCodeMap = new String[]{"RCL", "BS", "AOF", "AON", "DER", "RU2", "RU3", "RU4", "FON", "RDC", "TR", "RTD", "EDM", "CR", "ENM", "EOC"};
        mSpecialCharMap = new String[]{"\u00ae", "\u00b0", "\u00bd", "\u00bf", "\u2122", "\u00a2", "\u00a3", "\u266a", "\u00e0", "\u00a0", "\u00e8", "\u00e2", "\u00ea", "\u00ee", "\u00f4", "\u00fb"};
        mSpanishCharMap = new String[]{"\u00c1", "\u00c9", "\u00d3", "\u00da", "\u00dc", "\u00fc", "\u2018", "\u00a1", "*", "'", "\u2014", "\u00a9", "\u2120", "\u2022", "\u201c", "\u201d", "\u00c0", "\u00c2", "\u00c7", "\u00c8", "\u00ca", "\u00cb", "\u00eb", "\u00ce", "\u00cf", "\u00ef", "\u00d4", "\u00d9", "\u00f9", "\u00db", "\u00ab", "\u00bb"};
        mProtugueseCharMap = new String[]{"\u00c3", "\u00e3", "\u00cd", "\u00cc", "\u00ec", "\u00d2", "\u00f2", "\u00d5", "\u00f5", "{", "}", "\\", "^", "_", "|", "~", "\u00c4", "\u00e4", "\u00d6", "\u00f6", "\u00df", "\u00a5", "\u00a4", "\u2502", "\u00c5", "\u00e5", "\u00d8", "\u00f8", "\u250c", "\u2510", "\u2514", "\u2518"};
    }

    static CCParser$CCData[] fromByteArray(byte[] data) {
        CCParser$CCData[] ccData = new CCParser$CCData[(data.length / 3)];
        for (int i = 0; i < ccData.length; i++) {
            ccData[i] = new CCParser$CCData(data[i * 3], data[(i * 3) + 1], data[(i * 3) + 2]);
        }
        return ccData;
    }

    int getCtrlCode() {
        if ((this.mData1 == 20 || this.mData1 == 28) && this.mData2 >= 32 && this.mData2 <= 47) {
            return this.mData2;
        }
        return -1;
    }

    StyleCode getMidRow() {
        if ((this.mData1 == 17 || this.mData1 == 25) && this.mData2 >= 32 && this.mData2 <= 47) {
            return StyleCode.fromByte(this.mData2);
        }
        return null;
    }

    CCParser$PAC getPAC() {
        if ((this.mData1 & KeyEvent.KEYCODE_FORWARD_DEL) == 16 && (this.mData2 & 64) == 64 && ((this.mData1 & 7) != 0 || (this.mData2 & 32) == 0)) {
            return CCParser$PAC.fromBytes(this.mData1, this.mData2);
        }
        return null;
    }

    int getTabOffset() {
        if ((this.mData1 == 23 || this.mData1 == 31) && this.mData2 >= 33 && this.mData2 <= 35) {
            return this.mData2 & 3;
        }
        return 0;
    }

    boolean isDisplayableChar() {
        return isBasicChar() || isSpecialChar() || isExtendedChar();
    }

    String getDisplayText() {
        String str = getBasicChars();
        if (str != null) {
            return str;
        }
        str = getSpecialChar();
        if (str == null) {
            return getExtendedChar();
        }
        return str;
    }

    private String ctrlCodeToString(int ctrlCode) {
        return mCtrlCodeMap[ctrlCode - 32];
    }

    private boolean isBasicChar() {
        return this.mData1 >= 32 && this.mData1 <= 127;
    }

    private boolean isSpecialChar() {
        return (this.mData1 == 17 || this.mData1 == 25) && this.mData2 >= 48 && this.mData2 <= 63;
    }

    private boolean isExtendedChar() {
        return (this.mData1 == 18 || this.mData1 == 26 || this.mData1 == 19 || this.mData1 == 27) && this.mData2 >= 32 && this.mData2 <= 63;
    }

    private char getBasicChar(byte data) {
        switch (data) {
            case MotionEvent.AXIS_GENERIC_11 /*42*/:
                return '\u00e1';
            case KeyEvent.KEYCODE_PAGE_UP /*92*/:
                return '\u00e9';
            case KeyEvent.KEYCODE_PICTSYMBOLS /*94*/:
                return '\u00ed';
            case KeyEvent.KEYCODE_SWITCH_CHARSET /*95*/:
                return '\u00f3';
            case KeyEvent.KEYCODE_BUTTON_A /*96*/:
                return '\u00fa';
            case KeyEvent.KEYCODE_MOVE_END /*123*/:
                return '\u00e7';
            case KeyEvent.KEYCODE_INSERT /*124*/:
                return '\u00f7';
            case KeyEvent.KEYCODE_FORWARD /*125*/:
                return '\u00d1';
            case KeyEvent.KEYCODE_MEDIA_PLAY /*126*/:
                return '\u00f1';
            case KeyEvent.KEYCODE_MEDIA_PAUSE /*127*/:
                return '\u2588';
            default:
                return (char) data;
        }
    }

    private String getSpecialChar() {
        if ((this.mData1 == 17 || this.mData1 == 25) && this.mData2 >= 48 && this.mData2 <= 63) {
            return mSpecialCharMap[this.mData2 - 48];
        }
        return null;
    }

    private String getExtendedChar() {
        if ((this.mData1 == 18 || this.mData1 == 26) && this.mData2 >= (byte) 32 && this.mData2 <= (byte) 63) {
            return mSpanishCharMap[this.mData2 - 32];
        }
        if ((this.mData1 == 19 || this.mData1 == 27) && this.mData2 >= (byte) 32 && this.mData2 <= (byte) 63) {
            return mProtugueseCharMap[this.mData2 - 32];
        }
        return null;
    }
}
