package java.net;

public interface CookiePolicy {
    public static final CookiePolicy ACCEPT_ALL;
    public static final CookiePolicy ACCEPT_NONE;
    public static final CookiePolicy ACCEPT_ORIGINAL_SERVER;

    boolean shouldAccept(URI uri, HttpCookie httpCookie);

    static {
        ACCEPT_ALL = new CookiePolicy() {
            public boolean shouldAccept(URI uri, HttpCookie cookie) {
                return true;
            }
        };
        ACCEPT_NONE = new CookiePolicy() {
            public boolean shouldAccept(URI uri, HttpCookie cookie) {
                return false;
            }
        };
        ACCEPT_ORIGINAL_SERVER = new CookiePolicy() {
            public boolean shouldAccept(java.net.URI r1, java.net.HttpCookie r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.net.CookiePolicy.3.shouldAccept(java.net.URI, java.net.HttpCookie):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.net.CookiePolicy.3.shouldAccept(java.net.URI, java.net.HttpCookie):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.net.CookiePolicy.3.shouldAccept(java.net.URI, java.net.HttpCookie):boolean");
            }
        };
    }
}
