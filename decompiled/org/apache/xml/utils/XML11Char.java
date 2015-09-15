package org.apache.xml.utils;

import java.util.Arrays;
import org.apache.xpath.axes.WalkerFactory;

public class XML11Char {
    public static final int MASK_XML11_CONTENT = 32;
    public static final int MASK_XML11_CONTENT_INTERNAL = 48;
    public static final int MASK_XML11_CONTROL = 16;
    public static final int MASK_XML11_NAME = 8;
    public static final int MASK_XML11_NAME_START = 4;
    public static final int MASK_XML11_NCNAME = 128;
    public static final int MASK_XML11_NCNAME_START = 64;
    public static final int MASK_XML11_SPACE = 2;
    public static final int MASK_XML11_VALID = 1;
    private static final byte[] XML11CHARS;

    public static boolean isXML11ValidNCName(java.lang.String r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xml.utils.XML11Char.isXML11ValidNCName(java.lang.String):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xml.utils.XML11Char.isXML11ValidNCName(java.lang.String):boolean
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xml.utils.XML11Char.isXML11ValidNCName(java.lang.String):boolean");
    }

    public static boolean isXML11ValidName(java.lang.String r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xml.utils.XML11Char.isXML11ValidName(java.lang.String):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xml.utils.XML11Char.isXML11ValidName(java.lang.String):boolean
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xml.utils.XML11Char.isXML11ValidName(java.lang.String):boolean");
    }

    public static boolean isXML11ValidNmtoken(java.lang.String r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xml.utils.XML11Char.isXML11ValidNmtoken(java.lang.String):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xml.utils.XML11Char.isXML11ValidNmtoken(java.lang.String):boolean
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xml.utils.XML11Char.isXML11ValidNmtoken(java.lang.String):boolean");
    }

    public static boolean isXML11ValidQName(java.lang.String r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.xml.utils.XML11Char.isXML11ValidQName(java.lang.String):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.xml.utils.XML11Char.isXML11ValidQName(java.lang.String):boolean
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.xml.utils.XML11Char.isXML11ValidQName(java.lang.String):boolean");
    }

    static {
        XML11CHARS = new byte[WalkerFactory.BIT_CHILD];
        Arrays.fill(XML11CHARS, MASK_XML11_VALID, 9, (byte) 17);
        XML11CHARS[9] = (byte) 35;
        XML11CHARS[10] = (byte) 3;
        Arrays.fill(XML11CHARS, 11, 13, (byte) 17);
        XML11CHARS[13] = (byte) 3;
        Arrays.fill(XML11CHARS, 14, MASK_XML11_CONTENT, (byte) 17);
        XML11CHARS[MASK_XML11_CONTENT] = (byte) 35;
        Arrays.fill(XML11CHARS, 33, 38, (byte) 33);
        XML11CHARS[38] = (byte) 1;
        Arrays.fill(XML11CHARS, 39, 45, (byte) 33);
        Arrays.fill(XML11CHARS, 45, 47, (byte) -87);
        XML11CHARS[47] = (byte) 33;
        Arrays.fill(XML11CHARS, MASK_XML11_CONTENT_INTERNAL, 58, (byte) -87);
        XML11CHARS[58] = (byte) 45;
        XML11CHARS[59] = (byte) 33;
        XML11CHARS[60] = (byte) 1;
        Arrays.fill(XML11CHARS, 61, 65, (byte) 33);
        Arrays.fill(XML11CHARS, 65, 91, (byte) -19);
        Arrays.fill(XML11CHARS, 91, 93, (byte) 33);
        XML11CHARS[93] = (byte) 1;
        XML11CHARS[94] = (byte) 33;
        XML11CHARS[95] = (byte) -19;
        XML11CHARS[96] = (byte) 33;
        Arrays.fill(XML11CHARS, 97, 123, (byte) -19);
        Arrays.fill(XML11CHARS, 123, 127, (byte) 33);
        Arrays.fill(XML11CHARS, 127, 133, (byte) 17);
        XML11CHARS[133] = (byte) 35;
        Arrays.fill(XML11CHARS, 134, 160, (byte) 17);
        Arrays.fill(XML11CHARS, 160, 183, (byte) 33);
        XML11CHARS[183] = (byte) -87;
        Arrays.fill(XML11CHARS, 184, 192, (byte) 33);
        Arrays.fill(XML11CHARS, 192, 215, (byte) -19);
        XML11CHARS[215] = (byte) 33;
        Arrays.fill(XML11CHARS, 216, 247, (byte) -19);
        XML11CHARS[247] = (byte) 33;
        Arrays.fill(XML11CHARS, 248, 768, (byte) -19);
        Arrays.fill(XML11CHARS, 768, 880, (byte) -87);
        Arrays.fill(XML11CHARS, 880, 894, (byte) -19);
        XML11CHARS[894] = (byte) 33;
        Arrays.fill(XML11CHARS, 895, WalkerFactory.BIT_ANCESTOR, (byte) -19);
        Arrays.fill(XML11CHARS, WalkerFactory.BIT_ANCESTOR, 8204, (byte) 33);
        Arrays.fill(XML11CHARS, 8204, 8206, (byte) -19);
        Arrays.fill(XML11CHARS, 8206, 8232, (byte) 33);
        XML11CHARS[8232] = (byte) 35;
        Arrays.fill(XML11CHARS, 8233, 8255, (byte) 33);
        Arrays.fill(XML11CHARS, 8255, 8257, (byte) -87);
        Arrays.fill(XML11CHARS, 8257, 8304, (byte) 33);
        Arrays.fill(XML11CHARS, 8304, 8592, (byte) -19);
        Arrays.fill(XML11CHARS, 8592, 11264, (byte) 33);
        Arrays.fill(XML11CHARS, 11264, 12272, (byte) -19);
        Arrays.fill(XML11CHARS, 12272, 12289, (byte) 33);
        Arrays.fill(XML11CHARS, 12289, 55296, (byte) -19);
        Arrays.fill(XML11CHARS, 57344, 63744, (byte) 33);
        Arrays.fill(XML11CHARS, 63744, 64976, (byte) -19);
        Arrays.fill(XML11CHARS, 64976, 65008, (byte) 33);
        Arrays.fill(XML11CHARS, 65008, 65534, (byte) -19);
    }

    public static boolean isXML11Space(int c) {
        return c < WalkerFactory.BIT_CHILD && (XML11CHARS[c] & MASK_XML11_SPACE) != 0;
    }

    public static boolean isXML11Valid(int c) {
        return (c < WalkerFactory.BIT_CHILD && (XML11CHARS[c] & MASK_XML11_VALID) != 0) || (WalkerFactory.BIT_CHILD <= c && c <= 1114111);
    }

    public static boolean isXML11Invalid(int c) {
        return !isXML11Valid(c);
    }

    public static boolean isXML11ValidLiteral(int c) {
        return (c < WalkerFactory.BIT_CHILD && (XML11CHARS[c] & MASK_XML11_VALID) != 0 && (XML11CHARS[c] & MASK_XML11_CONTROL) == 0) || (WalkerFactory.BIT_CHILD <= c && c <= 1114111);
    }

    public static boolean isXML11Content(int c) {
        return (c < WalkerFactory.BIT_CHILD && (XML11CHARS[c] & MASK_XML11_CONTENT) != 0) || (WalkerFactory.BIT_CHILD <= c && c <= 1114111);
    }

    public static boolean isXML11InternalEntityContent(int c) {
        return (c < WalkerFactory.BIT_CHILD && (XML11CHARS[c] & MASK_XML11_CONTENT_INTERNAL) != 0) || (WalkerFactory.BIT_CHILD <= c && c <= 1114111);
    }

    public static boolean isXML11NameStart(int c) {
        return (c < WalkerFactory.BIT_CHILD && (XML11CHARS[c] & MASK_XML11_NAME_START) != 0) || (WalkerFactory.BIT_CHILD <= c && c < 983040);
    }

    public static boolean isXML11Name(int c) {
        return (c < WalkerFactory.BIT_CHILD && (XML11CHARS[c] & MASK_XML11_NAME) != 0) || (c >= WalkerFactory.BIT_CHILD && c < 983040);
    }

    public static boolean isXML11NCNameStart(int c) {
        return (c < WalkerFactory.BIT_CHILD && (XML11CHARS[c] & MASK_XML11_NCNAME_START) != 0) || (WalkerFactory.BIT_CHILD <= c && c < 983040);
    }

    public static boolean isXML11NCName(int c) {
        return (c < WalkerFactory.BIT_CHILD && (XML11CHARS[c] & MASK_XML11_NCNAME) != 0) || (WalkerFactory.BIT_CHILD <= c && c < 983040);
    }

    public static boolean isXML11NameHighSurrogate(int c) {
        return 55296 <= c && c <= 56191;
    }
}
