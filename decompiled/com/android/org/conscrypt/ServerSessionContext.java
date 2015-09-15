package com.android.org.conscrypt;

import javax.net.ssl.SSLSession;

public class ServerSessionContext extends AbstractSessionContext {
    private SSLServerSessionCache persistentCache;

    public /* bridge */ /* synthetic */ void setSessionTimeout(int x0) throws IllegalArgumentException {
        super.setSessionTimeout(x0);
    }

    public ServerSessionContext() {
        super(100);
        NativeCrypto.SSL_CTX_set_session_id_context(this.sslCtxNativePointer, new byte[]{(byte) 32});
    }

    public void setPersistentCache(SSLServerSessionCache persistentCache) {
        this.persistentCache = persistentCache;
    }

    protected void sessionRemoved(SSLSession session) {
    }

    public SSLSession getSession(byte[] sessionId) {
        SSLSession session = super.getSession(sessionId);
        if (session != null) {
            return session;
        }
        if (this.persistentCache == null) {
            return null;
        }
        byte[] data = this.persistentCache.getSessionData(sessionId);
        if (data == null) {
            return null;
        }
        session = toSession(data, null, -1);
        if (session == null || !session.isValid()) {
            return null;
        }
        super.putSession(session);
        return session;
    }

    void putSession(SSLSession session) {
        super.putSession(session);
        if (this.persistentCache != null) {
            byte[] data = toBytes(session);
            if (data != null) {
                this.persistentCache.putSessionData(session, data);
            }
        }
    }
}
