package com.android.internal.telephony.cat;

public class TextAttribute {
    public TextAlignment align;
    public boolean bold;
    public TextColor color;
    public boolean italic;
    public int length;
    public FontSize size;
    public int start;
    public boolean strikeThrough;
    public boolean underlined;

    public TextAttribute(int r1, int r2, com.android.internal.telephony.cat.TextAlignment r3, com.android.internal.telephony.cat.FontSize r4, boolean r5, boolean r6, boolean r7, boolean r8, com.android.internal.telephony.cat.TextColor r9) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.telephony.cat.TextAttribute.<init>(int, int, com.android.internal.telephony.cat.TextAlignment, com.android.internal.telephony.cat.FontSize, boolean, boolean, boolean, boolean, com.android.internal.telephony.cat.TextColor):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.telephony.cat.TextAttribute.<init>(int, int, com.android.internal.telephony.cat.TextAlignment, com.android.internal.telephony.cat.FontSize, boolean, boolean, boolean, boolean, com.android.internal.telephony.cat.TextColor):void
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.internal.telephony.cat.TextAttribute.<init>(int, int, com.android.internal.telephony.cat.TextAlignment, com.android.internal.telephony.cat.FontSize, boolean, boolean, boolean, boolean, com.android.internal.telephony.cat.TextColor):void");
    }
}
