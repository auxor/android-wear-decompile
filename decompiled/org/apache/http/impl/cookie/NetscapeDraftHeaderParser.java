package org.apache.http.impl.cookie;

import org.apache.http.message.BasicHeaderValueParser;

@Deprecated
public class NetscapeDraftHeaderParser {
    public static final NetscapeDraftHeaderParser DEFAULT;
    private static final char[] DELIMITERS;
    private final BasicHeaderValueParser nvpParser;

    public NetscapeDraftHeaderParser() {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.http.impl.cookie.NetscapeDraftHeaderParser.<init>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.http.impl.cookie.NetscapeDraftHeaderParser.<init>():void
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.cookie.NetscapeDraftHeaderParser.<init>():void");
    }

    public org.apache.http.HeaderElement parseHeader(org.apache.http.util.CharArrayBuffer r1, org.apache.http.message.ParserCursor r2) throws org.apache.http.ParseException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.http.impl.cookie.NetscapeDraftHeaderParser.parseHeader(org.apache.http.util.CharArrayBuffer, org.apache.http.message.ParserCursor):org.apache.http.HeaderElement
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.http.impl.cookie.NetscapeDraftHeaderParser.parseHeader(org.apache.http.util.CharArrayBuffer, org.apache.http.message.ParserCursor):org.apache.http.HeaderElement
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.impl.cookie.NetscapeDraftHeaderParser.parseHeader(org.apache.http.util.CharArrayBuffer, org.apache.http.message.ParserCursor):org.apache.http.HeaderElement");
    }

    static {
        DEFAULT = new NetscapeDraftHeaderParser();
        DELIMITERS = new char[]{';'};
    }
}
