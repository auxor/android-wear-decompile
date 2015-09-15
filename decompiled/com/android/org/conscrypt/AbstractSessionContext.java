package com.android.org.conscrypt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;

abstract class AbstractSessionContext implements SSLSessionContext {
    private static final int DEFAULT_SESSION_TIMEOUT_SECONDS = 28800;
    static final int OPEN_SSL = 1;
    volatile int maximumSize;
    private final Map<ByteArray, SSLSession> sessions;
    final long sslCtxNativePointer;
    volatile int timeout;

    /* renamed from: com.android.org.conscrypt.AbstractSessionContext.2 */
    class AnonymousClass2 implements Enumeration<byte[]> {
        private SSLSession next;
        final /* synthetic */ AbstractSessionContext this$0;
        final /* synthetic */ Iterator val$i;

        AnonymousClass2(com.android.org.conscrypt.AbstractSessionContext r1, java.util.Iterator r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.conscrypt.AbstractSessionContext.2.<init>(com.android.org.conscrypt.AbstractSessionContext, java.util.Iterator):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.conscrypt.AbstractSessionContext.2.<init>(com.android.org.conscrypt.AbstractSessionContext, java.util.Iterator):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.org.conscrypt.AbstractSessionContext.2.<init>(com.android.org.conscrypt.AbstractSessionContext, java.util.Iterator):void");
        }

        public boolean hasMoreElements() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.conscrypt.AbstractSessionContext.2.hasMoreElements():boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.conscrypt.AbstractSessionContext.2.hasMoreElements():boolean
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.org.conscrypt.AbstractSessionContext.2.hasMoreElements():boolean");
        }

        public /* bridge */ /* synthetic */ java.lang.Object nextElement() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.conscrypt.AbstractSessionContext.2.nextElement():java.lang.Object
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.conscrypt.AbstractSessionContext.2.nextElement():java.lang.Object
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.org.conscrypt.AbstractSessionContext.2.nextElement():java.lang.Object");
        }

        public byte[] m0nextElement() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.org.conscrypt.AbstractSessionContext.2.nextElement():byte[]
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.org.conscrypt.AbstractSessionContext.2.nextElement():byte[]
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.org.conscrypt.AbstractSessionContext.2.nextElement():byte[]");
        }
    }

    protected abstract void sessionRemoved(SSLSession sSLSession);

    AbstractSessionContext(int maximumSize) {
        this.timeout = DEFAULT_SESSION_TIMEOUT_SECONDS;
        this.sslCtxNativePointer = NativeCrypto.SSL_CTX_new();
        this.sessions = new LinkedHashMap<ByteArray, SSLSession>() {
            protected boolean removeEldestEntry(Entry<ByteArray, SSLSession> eldest) {
                boolean remove;
                if (AbstractSessionContext.this.maximumSize <= 0 || size() <= AbstractSessionContext.this.maximumSize) {
                    remove = false;
                } else {
                    remove = true;
                }
                if (remove) {
                    remove(eldest.getKey());
                    AbstractSessionContext.this.sessionRemoved((SSLSession) eldest.getValue());
                }
                return false;
            }
        };
        this.maximumSize = maximumSize;
    }

    private Iterator<SSLSession> sessionIterator() {
        Iterator<SSLSession> it;
        synchronized (this.sessions) {
            it = Arrays.asList((SSLSession[]) this.sessions.values().toArray(new SSLSession[this.sessions.size()])).iterator();
        }
        return it;
    }

    public final Enumeration<byte[]> getIds() {
        return new AnonymousClass2(this, sessionIterator());
    }

    public final int getSessionCacheSize() {
        return this.maximumSize;
    }

    public final int getSessionTimeout() {
        return this.timeout;
    }

    protected void trimToSize() {
        synchronized (this.sessions) {
            int size = this.sessions.size();
            if (size > this.maximumSize) {
                int removals = size - this.maximumSize;
                Iterator<SSLSession> i = this.sessions.values().iterator();
                do {
                    SSLSession session = (SSLSession) i.next();
                    i.remove();
                    sessionRemoved(session);
                    removals--;
                } while (removals > 0);
            }
        }
    }

    public void setSessionTimeout(int seconds) throws IllegalArgumentException {
        if (seconds < 0) {
            throw new IllegalArgumentException("seconds < 0");
        }
        this.timeout = seconds;
        synchronized (this.sessions) {
            Iterator<SSLSession> i = this.sessions.values().iterator();
            while (i.hasNext()) {
                SSLSession session = (SSLSession) i.next();
                if (!session.isValid()) {
                    i.remove();
                    sessionRemoved(session);
                }
            }
        }
    }

    public final void setSessionCacheSize(int size) throws IllegalArgumentException {
        if (size < 0) {
            throw new IllegalArgumentException("size < 0");
        }
        int oldMaximum = this.maximumSize;
        this.maximumSize = size;
        if (size < oldMaximum) {
            trimToSize();
        }
    }

    byte[] toBytes(SSLSession session) {
        byte[] bArr = null;
        if (session instanceof OpenSSLSessionImpl) {
            OpenSSLSessionImpl sslSession = (OpenSSLSessionImpl) session;
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream daos = new DataOutputStream(baos);
                daos.writeInt(OPEN_SSL);
                byte[] data = sslSession.getEncoded();
                daos.writeInt(data.length);
                daos.write(data);
                Certificate[] certs = session.getPeerCertificates();
                daos.writeInt(certs.length);
                Certificate[] arr$ = certs;
                int len$ = arr$.length;
                for (int i$ = 0; i$ < len$; i$ += OPEN_SSL) {
                    data = arr$[i$].getEncoded();
                    daos.writeInt(data.length);
                    daos.write(data);
                }
                bArr = baos.toByteArray();
            } catch (IOException e) {
                log(e);
            } catch (CertificateEncodingException e2) {
                log(e2);
            }
        }
        return bArr;
    }

    SSLSession toSession(byte[] data, String host, int port) {
        DataInputStream dais = new DataInputStream(new ByteArrayInputStream(data));
        try {
            int type = dais.readInt();
            if (type != OPEN_SSL) {
                log(new AssertionError("Unexpected type ID: " + type));
                return null;
            }
            byte[] sessionData = new byte[dais.readInt()];
            dais.readFully(sessionData);
            int count = dais.readInt();
            X509Certificate[] certs = new X509Certificate[count];
            for (int i = 0; i < count; i += OPEN_SSL) {
                byte[] certData = new byte[dais.readInt()];
                dais.readFully(certData);
                certs[i] = OpenSSLX509Certificate.fromX509Der(certData);
            }
            return new OpenSSLSessionImpl(sessionData, host, port, certs, this);
        } catch (IOException e) {
            log(e);
            return null;
        }
    }

    public SSLSession getSession(byte[] sessionId) {
        if (sessionId == null) {
            throw new NullPointerException("sessionId == null");
        }
        SSLSession session;
        ByteArray key = new ByteArray(sessionId);
        synchronized (this.sessions) {
            session = (SSLSession) this.sessions.get(key);
        }
        return (session == null || !session.isValid()) ? null : session;
    }

    void putSession(SSLSession session) {
        byte[] id = session.getId();
        if (id.length != 0) {
            ByteArray key = new ByteArray(id);
            synchronized (this.sessions) {
                this.sessions.put(key, session);
            }
        }
    }

    static void log(Throwable t) {
        new Exception("Error converting session", t).printStackTrace();
    }

    protected void finalize() throws Throwable {
        try {
            NativeCrypto.SSL_CTX_free(this.sslCtxNativePointer);
        } finally {
            super.finalize();
        }
    }
}
