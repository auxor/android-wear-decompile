package org.w3c.dom;

public class DOMException extends RuntimeException {
    public static final short DOMSTRING_SIZE_ERR = (short) 2;
    public static final short HIERARCHY_REQUEST_ERR = (short) 3;
    public static final short INDEX_SIZE_ERR = (short) 1;
    public static final short INUSE_ATTRIBUTE_ERR = (short) 10;
    public static final short INVALID_ACCESS_ERR = (short) 15;
    public static final short INVALID_CHARACTER_ERR = (short) 5;
    public static final short INVALID_MODIFICATION_ERR = (short) 13;
    public static final short INVALID_STATE_ERR = (short) 11;
    public static final short NAMESPACE_ERR = (short) 14;
    public static final short NOT_FOUND_ERR = (short) 8;
    public static final short NOT_SUPPORTED_ERR = (short) 9;
    public static final short NO_DATA_ALLOWED_ERR = (short) 6;
    public static final short NO_MODIFICATION_ALLOWED_ERR = (short) 7;
    public static final short SYNTAX_ERR = (short) 12;
    public static final short TYPE_MISMATCH_ERR = (short) 17;
    public static final short VALIDATION_ERR = (short) 16;
    public static final short WRONG_DOCUMENT_ERR = (short) 4;
    public short code;

    public DOMException(short r1, java.lang.String r2) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.w3c.dom.DOMException.<init>(short, java.lang.String):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.w3c.dom.DOMException.<init>(short, java.lang.String):void
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
        throw new UnsupportedOperationException("Method not decompiled: org.w3c.dom.DOMException.<init>(short, java.lang.String):void");
    }
}
