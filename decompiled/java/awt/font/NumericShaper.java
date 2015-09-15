package java.awt.font;

import com.android.dex.DexFormat;
import dalvik.bytecode.Opcodes;
import dalvik.system.VMDebug;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import javax.xml.datatype.DatatypeConstants;

public final class NumericShaper implements Serializable {
    public static final int ALL_RANGES = 524287;
    public static final int ARABIC = 2;
    public static final int BENGALI = 16;
    public static final int DEVANAGARI = 8;
    public static final int EASTERN_ARABIC = 4;
    public static final int ETHIOPIC = 65536;
    public static final int EUROPEAN = 1;
    public static final int GUJARATI = 64;
    public static final int GURMUKHI = 32;
    private static final int INDEX_ARABIC = 1;
    private static final int INDEX_BENGALI = 4;
    private static final int INDEX_DEVANAGARI = 3;
    private static final int INDEX_EASTERN_ARABIC = 2;
    private static final int INDEX_ETHIOPIC = 16;
    private static final int INDEX_EUROPEAN = 0;
    private static final int INDEX_GUJARATI = 6;
    private static final int INDEX_GURMUKHI = 5;
    private static final int INDEX_KANNADA = 10;
    private static final int INDEX_KHMER = 17;
    private static final int INDEX_LAO = 13;
    private static final int INDEX_MALAYALAM = 11;
    private static final int INDEX_MONGOLIAN = 18;
    private static final int INDEX_MYANMAR = 15;
    private static final int INDEX_ORIYA = 7;
    private static final int INDEX_TAMIL = 8;
    private static final int INDEX_TELUGU = 9;
    private static final int INDEX_THAI = 12;
    private static final int INDEX_TIBETAN = 14;
    public static final int KANNADA = 1024;
    public static final int KHMER = 131072;
    public static final int LAO = 8192;
    public static final int MALAYALAM = 2048;
    private static final int MAX_INDEX = 19;
    public static final int MONGOLIAN = 262144;
    public static final int MYANMAR = 32768;
    public static final int ORIYA = 128;
    private static final int[] STRONG_TEXT_FLAGS;
    public static final int TAMIL = 256;
    public static final int TELUGU = 512;
    public static final int THAI = 4096;
    public static final int TIBETAN = 16384;
    private static final long serialVersionUID = -8022764705923730308L;
    private final String[] contexts;
    private final int[] digitsLowRanges;
    private boolean fContextual;
    private int fDefaultContextIndex;
    private int fRanges;
    private int fSingleRangeIndex;
    private int key;
    private int mask;
    private final int[] scriptsRanges;

    private NumericShaper(int r1, int r2, boolean r3) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.awt.font.NumericShaper.<init>(int, int, boolean):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.awt.font.NumericShaper.<init>(int, int, boolean):void
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
        throw new UnsupportedOperationException("Method not decompiled: java.awt.font.NumericShaper.<init>(int, int, boolean):void");
    }

    private void contextualShape(char[] r1, int r2, int r3, int r4) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.awt.font.NumericShaper.contextualShape(char[], int, int, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.awt.font.NumericShaper.contextualShape(char[], int, int, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: java.awt.font.NumericShaper.contextualShape(char[], int, int, int):void");
    }

    private int getCharIndex(char r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.awt.font.NumericShaper.getCharIndex(char):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.awt.font.NumericShaper.getCharIndex(char):int
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
        throw new UnsupportedOperationException("Method not decompiled: java.awt.font.NumericShaper.getCharIndex(char):int");
    }

    private void nonContextualShape(char[] r1, int r2, int r3) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.awt.font.NumericShaper.nonContextualShape(char[], int, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.awt.font.NumericShaper.nonContextualShape(char[], int, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: java.awt.font.NumericShaper.nonContextualShape(char[], int, int):void");
    }

    private static java.lang.IllegalArgumentException rangeException(int r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.awt.font.NumericShaper.rangeException(int):java.lang.IllegalArgumentException
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.awt.font.NumericShaper.rangeException(int):java.lang.IllegalArgumentException
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
        throw new UnsupportedOperationException("Method not decompiled: java.awt.font.NumericShaper.rangeException(int):java.lang.IllegalArgumentException");
    }

    private void readObject(java.io.ObjectInputStream r1) throws java.io.IOException, java.lang.ClassNotFoundException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.awt.font.NumericShaper.readObject(java.io.ObjectInputStream):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.awt.font.NumericShaper.readObject(java.io.ObjectInputStream):void
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
        throw new UnsupportedOperationException("Method not decompiled: java.awt.font.NumericShaper.readObject(java.io.ObjectInputStream):void");
    }

    private void updateKeyMaskFields() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.awt.font.NumericShaper.updateKeyMaskFields():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.awt.font.NumericShaper.updateKeyMaskFields():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: java.awt.font.NumericShaper.updateKeyMaskFields():void");
    }

    private void updateRangesFields() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.awt.font.NumericShaper.updateRangesFields():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.awt.font.NumericShaper.updateRangesFields():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: java.awt.font.NumericShaper.updateRangesFields():void");
    }

    private void writeObject(java.io.ObjectOutputStream r1) throws java.io.IOException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.awt.font.NumericShaper.writeObject(java.io.ObjectOutputStream):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.awt.font.NumericShaper.writeObject(java.io.ObjectOutputStream):void
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
        throw new UnsupportedOperationException("Method not decompiled: java.awt.font.NumericShaper.writeObject(java.io.ObjectOutputStream):void");
    }

    public boolean equals(java.lang.Object r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.awt.font.NumericShaper.equals(java.lang.Object):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.awt.font.NumericShaper.equals(java.lang.Object):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: java.awt.font.NumericShaper.equals(java.lang.Object):boolean");
    }

    public int getRanges() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.awt.font.NumericShaper.getRanges():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.awt.font.NumericShaper.getRanges():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: java.awt.font.NumericShaper.getRanges():int");
    }

    public int hashCode() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.awt.font.NumericShaper.hashCode():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.awt.font.NumericShaper.hashCode():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: java.awt.font.NumericShaper.hashCode():int");
    }

    public void shape(char[] r1, int r2, int r3) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.awt.font.NumericShaper.shape(char[], int, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.awt.font.NumericShaper.shape(char[], int, int):void
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
        throw new UnsupportedOperationException("Method not decompiled: java.awt.font.NumericShaper.shape(char[], int, int):void");
    }

    public void shape(char[] r1, int r2, int r3, int r4) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.awt.font.NumericShaper.shape(char[], int, int, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.awt.font.NumericShaper.shape(char[], int, int, int):void
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
        throw new UnsupportedOperationException("Method not decompiled: java.awt.font.NumericShaper.shape(char[], int, int, int):void");
    }

    public java.lang.String toString() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.awt.font.NumericShaper.toString():java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.awt.font.NumericShaper.toString():java.lang.String
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
        throw new UnsupportedOperationException("Method not decompiled: java.awt.font.NumericShaper.toString():java.lang.String");
    }

    static {
        STRONG_TEXT_FLAGS = new int[]{INDEX_EUROPEAN, INDEX_EUROPEAN, 134217726, 134217726, INDEX_EUROPEAN, 69207040, -8388609, -8388609, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -65533, -1, -1, -100663297, 196611, 16415, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, 67108864, -10432, -5, -32769, -4194305, -1, -1, -1, -1, -1017, -1, -32769, 67108863, DexFormat.MAX_TYPE_IDX, -131072, -25165825, -2, Opcodes.OP_INSTANCE_OF_JUMBO, VMDebug.KIND_THREAD_EXT_FREED_OBJECTS, -65463, 2033663, -939513841, 134217726, Opcodes.OP_IGET_WIDE_JUMBO, -73728, -1, -1, 541065215, -67059616, -180225, DexFormat.MAX_TYPE_IDX, -8192, 16383, -1, 131135, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, -8, -469762049, -16703999, 537001971, -417812, -473563649, -1333765759, 133431235, -423960, -1016201729, 1577058305, 1900480, -278552, -470942209, 72193, 65475, -417812, 1676541439, -1333782143, 262083, -700594200, -1006647528, 8396230, 524224, -139282, 66059775, 30, 65475, -139284, -470811137, 1080036831, 65475, -139284, -1006633473, 8396225, 65475, -58720276, 805044223, -16547713, 1835008, -2, 917503, 268402815, INDEX_EUROPEAN, -17816170, 537783470, 872349791, INDEX_EUROPEAN, -50331649, -1050673153, -257, -2147481601, 3872, -1073741824, 237503, INDEX_EUROPEAN, -1, 16914171, 16777215, INDEX_EUROPEAN, INDEX_EUROPEAN, -1, -65473, 536870911, -1, -1, -2080374785, -1, -1, -249, -1, 67108863, -1, -1, 1031749119, -1, -49665, 2134769663, -8388803, -1, -12713985, -1, 134217727, 536870911, DexFormat.MAX_TYPE_IDX, -1, -1, 2097151, -2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 8388607, 134217726, -1, -1, 131071, 253951, 6553599, 262143, 122879, -1, -1065353217, 401605055, Double.MAX_EXPONENT, 67043328, -1, -1, 16777215, -1, Opcodes.OP_CHECK_CAST_JUMBO, INDEX_EUROPEAN, INDEX_EUROPEAN, 536870911, 33226872, -64, 2047999, -1, -64513, 67044351, INDEX_EUROPEAN, -830472193, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, -1, -1, -1, -1, -1, -1, INDEX_EUROPEAN, INDEX_EUROPEAN, -1, -1, -1, -1, 268435455, -1, -1, 67108863, 1061158911, -1, -1426112705, 1073741823, -1, 1608515583, 265232348, 534519807, 49152, 27648, INDEX_EUROPEAN, -2147352576, 2031616, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, 1043332228, -201605808, 992, -1, INDEX_MYANMAR, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, -4194304, -1, 134217727, Modifier.MIRANDA, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, -268435456, -1, -1, Double.MAX_EXPONENT, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, THAI, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, -1, -1, -1, -1, -1, -1, -1, -1, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, -1, -32769, Integer.MAX_VALUE, INDEX_EUROPEAN, -1, -1, -1, 31, -1, -65473, -1, 32831, 8388607, 2139062143, 2139062143, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, Opcodes.OP_SHL_INT_LIT8, 524157950, -2, -1, -528482305, -2, -1, -134217729, -32, -122881, -1, -1, -32769, 16777215, INDEX_EUROPEAN, -65536, 536870911, -1, INDEX_MYANMAR, -1879048193, -1, 131071, -61441, Integer.MAX_VALUE, -1, -1, -1, -125829121, -1, -1, 1073741823, Integer.MAX_VALUE, INDEX_ARABIC, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, Modifier.MIRANDA, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_ARABIC, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, 134217728, INDEX_EUROPEAN, INDEX_EUROPEAN, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, Opcodes.OP_SPUT_BYTE_JUMBO, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, -2117, Opcodes.OP_REM_LONG, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_ARABIC, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_TAMIL, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_ARABIC, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, DatatypeConstants.FIELD_UNDEFINED, INDEX_ARABIC, INDEX_EUROPEAN, INDEX_EUROPEAN, DatatypeConstants.FIELD_UNDEFINED, INDEX_ARABIC, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, DatatypeConstants.FIELD_UNDEFINED, INDEX_ARABIC, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, DatatypeConstants.FIELD_UNDEFINED, -1, -1, -1, -1, -1, -1, -1, -1, -1, -49153, -1, -63489, -1, -1, 67108863, INDEX_EUROPEAN, -1594359681, 1602223615, -37, -1, -1, 262143, -524288, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1073741823, -65536, -1, -196609, -1, Opcodes.OP_CONST_CLASS_JUMBO, 536805376, INDEX_EUROPEAN, INDEX_EUROPEAN, INDEX_EUROPEAN, -2162688, -1, -1, -1, 536870911, INDEX_EUROPEAN, 134217726, 134217726, -64, -1, Integer.MAX_VALUE, 486341884, INDEX_EUROPEAN};
    }

    private int getIndexFromRange(int range) {
        if (range == 0) {
            throw rangeException(range);
        }
        for (int index = INDEX_EUROPEAN; index < MAX_INDEX; index += INDEX_ARABIC) {
            if (range == (INDEX_ARABIC << index)) {
                return index;
            }
        }
        throw rangeException(range);
    }

    private int getRangeFromIndex(int index) {
        if (index >= 0 && index < MAX_INDEX) {
            return INDEX_ARABIC << index;
        }
        throw rangeException(index);
    }

    public static NumericShaper getContextualShaper(int ranges, int defaultContext) {
        return new NumericShaper(ranges & ALL_RANGES, defaultContext & ALL_RANGES, true);
    }

    public static NumericShaper getContextualShaper(int ranges) {
        return new NumericShaper(ranges & ALL_RANGES, INDEX_ARABIC, true);
    }

    public static NumericShaper getShaper(int singleRange) {
        return new NumericShaper(singleRange & ALL_RANGES, INDEX_ARABIC, false);
    }

    public boolean isContextual() {
        return this.fContextual;
    }

    private boolean isCharStrong(int chr) {
        return (STRONG_TEXT_FLAGS[chr >> INDEX_GURMUKHI] & (INDEX_ARABIC << (chr % GURMUKHI))) != 0;
    }
}
