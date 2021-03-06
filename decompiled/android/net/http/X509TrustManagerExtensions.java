package android.net.http;

import com.android.org.conscrypt.TrustManagerImpl;

public class X509TrustManagerExtensions {
    TrustManagerImpl mDelegate;

    public X509TrustManagerExtensions(javax.net.ssl.X509TrustManager r1) throws java.lang.IllegalArgumentException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.net.http.X509TrustManagerExtensions.<init>(javax.net.ssl.X509TrustManager):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.net.http.X509TrustManagerExtensions.<init>(javax.net.ssl.X509TrustManager):void
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
        throw new UnsupportedOperationException("Method not decompiled: android.net.http.X509TrustManagerExtensions.<init>(javax.net.ssl.X509TrustManager):void");
    }

    public java.util.List<java.security.cert.X509Certificate> checkServerTrusted(java.security.cert.X509Certificate[] r1, java.lang.String r2, java.lang.String r3) throws java.security.cert.CertificateException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.net.http.X509TrustManagerExtensions.checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String, java.lang.String):java.util.List<java.security.cert.X509Certificate>
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.net.http.X509TrustManagerExtensions.checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String, java.lang.String):java.util.List<java.security.cert.X509Certificate>
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
        throw new UnsupportedOperationException("Method not decompiled: android.net.http.X509TrustManagerExtensions.checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String, java.lang.String):java.util.List<java.security.cert.X509Certificate>");
    }

    public boolean isUserAddedCertificate(java.security.cert.X509Certificate r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.net.http.X509TrustManagerExtensions.isUserAddedCertificate(java.security.cert.X509Certificate):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.net.http.X509TrustManagerExtensions.isUserAddedCertificate(java.security.cert.X509Certificate):boolean
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
        throw new UnsupportedOperationException("Method not decompiled: android.net.http.X509TrustManagerExtensions.isUserAddedCertificate(java.security.cert.X509Certificate):boolean");
    }
}
