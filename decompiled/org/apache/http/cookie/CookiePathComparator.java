package org.apache.http.cookie;

import java.io.Serializable;
import java.util.Comparator;

@Deprecated
public class CookiePathComparator implements Serializable, Comparator<Cookie> {
    private static final long serialVersionUID = 7523645369616405818L;

    private java.lang.String normalizePath(org.apache.http.cookie.Cookie r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.http.cookie.CookiePathComparator.normalizePath(org.apache.http.cookie.Cookie):java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.http.cookie.CookiePathComparator.normalizePath(org.apache.http.cookie.Cookie):java.lang.String
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.cookie.CookiePathComparator.normalizePath(org.apache.http.cookie.Cookie):java.lang.String");
    }

    public int compare(org.apache.http.cookie.Cookie r1, org.apache.http.cookie.Cookie r2) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: org.apache.http.cookie.CookiePathComparator.compare(org.apache.http.cookie.Cookie, org.apache.http.cookie.Cookie):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: org.apache.http.cookie.CookiePathComparator.compare(org.apache.http.cookie.Cookie, org.apache.http.cookie.Cookie):int
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
        throw new UnsupportedOperationException("Method not decompiled: org.apache.http.cookie.CookiePathComparator.compare(org.apache.http.cookie.Cookie, org.apache.http.cookie.Cookie):int");
    }
}
