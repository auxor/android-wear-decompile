package com.android.org.conscrypt;

import com.android.org.conscrypt.NativeCrypto.SSLHandshakeCallbacks;
import com.android.org.conscrypt.SSLParametersImpl.AliasChooser;
import com.android.org.conscrypt.SSLParametersImpl.PSKCallbacks;
import com.android.org.conscrypt.util.Arrays;
import dalvik.system.BlockGuard;
import dalvik.system.CloseGuard;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.crypto.SecretKey;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLProtocolException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;

public class OpenSSLSocketImpl extends SSLSocket implements SSLHandshakeCallbacks, AliasChooser, PSKCallbacks {
    private static final boolean DBG_STATE = false;
    private static final int STATE_CLOSED = 5;
    private static final int STATE_HANDSHAKE_COMPLETED = 2;
    private static final int STATE_HANDSHAKE_STARTED = 1;
    private static final int STATE_NEW = 0;
    private static final int STATE_READY = 4;
    private static final int STATE_READY_HANDSHAKE_CUT_THROUGH = 3;
    private final boolean autoClose;
    OpenSSLKey channelIdPrivateKey;
    private final CloseGuard guard;
    private OpenSSLSessionImpl handshakeSession;
    private int handshakeTimeoutMilliseconds;
    private SSLInputStream is;
    private ArrayList<HandshakeCompletedListener> listeners;
    private SSLOutputStream os;
    private String peerHostname;
    private final int peerPort;
    private int readTimeoutMilliseconds;
    private String resolvedHostname;
    private final Socket socket;
    private long sslNativePointer;
    private final SSLParametersImpl sslParameters;
    private OpenSSLSessionImpl sslSession;
    private int state;
    private final Object stateLock;
    private int writeTimeoutMilliseconds;

    private class SSLInputStream extends InputStream {
        private final Object readLock;

        SSLInputStream() {
            this.readLock = new Object();
        }

        public int read() throws IOException {
            byte[] buffer = new byte[OpenSSLSocketImpl.STATE_HANDSHAKE_STARTED];
            if (read(buffer, OpenSSLSocketImpl.STATE_NEW, OpenSSLSocketImpl.STATE_HANDSHAKE_STARTED) != -1) {
                return buffer[OpenSSLSocketImpl.STATE_NEW] & 255;
            }
            return -1;
        }

        public int read(byte[] buf, int offset, int byteCount) throws IOException {
            BlockGuard.getThreadPolicy().onNetwork();
            OpenSSLSocketImpl.this.checkOpen();
            Arrays.checkOffsetAndCount(buf.length, offset, byteCount);
            if (byteCount == 0) {
                return OpenSSLSocketImpl.STATE_NEW;
            }
            int SSL_read;
            synchronized (this.readLock) {
                synchronized (OpenSSLSocketImpl.this.stateLock) {
                    if (OpenSSLSocketImpl.this.state == OpenSSLSocketImpl.STATE_CLOSED) {
                        throw new SocketException("socket is closed");
                    }
                }
                SSL_read = NativeCrypto.SSL_read(OpenSSLSocketImpl.this.sslNativePointer, Platform.getFileDescriptor(OpenSSLSocketImpl.this.socket), OpenSSLSocketImpl.this, buf, offset, byteCount, OpenSSLSocketImpl.this.getSoTimeout());
            }
            return SSL_read;
        }

        public void awaitPendingOps() {
            synchronized (this.readLock) {
            }
        }
    }

    private class SSLOutputStream extends OutputStream {
        private final Object writeLock;

        SSLOutputStream() {
            this.writeLock = new Object();
        }

        public void write(int oneByte) throws IOException {
            byte[] buffer = new byte[OpenSSLSocketImpl.STATE_HANDSHAKE_STARTED];
            buffer[OpenSSLSocketImpl.STATE_NEW] = (byte) (oneByte & 255);
            write(buffer);
        }

        public void write(byte[] buf, int offset, int byteCount) throws IOException {
            BlockGuard.getThreadPolicy().onNetwork();
            OpenSSLSocketImpl.this.checkOpen();
            Arrays.checkOffsetAndCount(buf.length, offset, byteCount);
            if (byteCount != 0) {
                synchronized (this.writeLock) {
                    synchronized (OpenSSLSocketImpl.this.stateLock) {
                        if (OpenSSLSocketImpl.this.state == OpenSSLSocketImpl.STATE_CLOSED) {
                            throw new SocketException("socket is closed");
                        }
                    }
                    NativeCrypto.SSL_write(OpenSSLSocketImpl.this.sslNativePointer, Platform.getFileDescriptor(OpenSSLSocketImpl.this.socket), OpenSSLSocketImpl.this, buf, offset, byteCount, OpenSSLSocketImpl.this.writeTimeoutMilliseconds);
                }
            }
        }

        public void awaitPendingOps() {
            synchronized (this.writeLock) {
            }
        }
    }

    protected OpenSSLSocketImpl(SSLParametersImpl sslParameters) throws IOException {
        this.stateLock = new Object();
        this.state = STATE_NEW;
        this.guard = CloseGuard.get();
        this.readTimeoutMilliseconds = STATE_NEW;
        this.writeTimeoutMilliseconds = STATE_NEW;
        this.handshakeTimeoutMilliseconds = -1;
        this.socket = this;
        this.peerHostname = null;
        this.peerPort = -1;
        this.autoClose = DBG_STATE;
        this.sslParameters = sslParameters;
    }

    protected OpenSSLSocketImpl(String hostname, int port, SSLParametersImpl sslParameters) throws IOException {
        super(hostname, port);
        this.stateLock = new Object();
        this.state = STATE_NEW;
        this.guard = CloseGuard.get();
        this.readTimeoutMilliseconds = STATE_NEW;
        this.writeTimeoutMilliseconds = STATE_NEW;
        this.handshakeTimeoutMilliseconds = -1;
        this.socket = this;
        this.peerHostname = hostname;
        this.peerPort = port;
        this.autoClose = DBG_STATE;
        this.sslParameters = sslParameters;
    }

    protected OpenSSLSocketImpl(InetAddress address, int port, SSLParametersImpl sslParameters) throws IOException {
        super(address, port);
        this.stateLock = new Object();
        this.state = STATE_NEW;
        this.guard = CloseGuard.get();
        this.readTimeoutMilliseconds = STATE_NEW;
        this.writeTimeoutMilliseconds = STATE_NEW;
        this.handshakeTimeoutMilliseconds = -1;
        this.socket = this;
        this.peerHostname = null;
        this.peerPort = -1;
        this.autoClose = DBG_STATE;
        this.sslParameters = sslParameters;
    }

    protected OpenSSLSocketImpl(String hostname, int port, InetAddress clientAddress, int clientPort, SSLParametersImpl sslParameters) throws IOException {
        super(hostname, port, clientAddress, clientPort);
        this.stateLock = new Object();
        this.state = STATE_NEW;
        this.guard = CloseGuard.get();
        this.readTimeoutMilliseconds = STATE_NEW;
        this.writeTimeoutMilliseconds = STATE_NEW;
        this.handshakeTimeoutMilliseconds = -1;
        this.socket = this;
        this.peerHostname = hostname;
        this.peerPort = port;
        this.autoClose = DBG_STATE;
        this.sslParameters = sslParameters;
    }

    protected OpenSSLSocketImpl(InetAddress address, int port, InetAddress clientAddress, int clientPort, SSLParametersImpl sslParameters) throws IOException {
        super(address, port, clientAddress, clientPort);
        this.stateLock = new Object();
        this.state = STATE_NEW;
        this.guard = CloseGuard.get();
        this.readTimeoutMilliseconds = STATE_NEW;
        this.writeTimeoutMilliseconds = STATE_NEW;
        this.handshakeTimeoutMilliseconds = -1;
        this.socket = this;
        this.peerHostname = null;
        this.peerPort = -1;
        this.autoClose = DBG_STATE;
        this.sslParameters = sslParameters;
    }

    protected OpenSSLSocketImpl(Socket socket, String hostname, int port, boolean autoClose, SSLParametersImpl sslParameters) throws IOException {
        this.stateLock = new Object();
        this.state = STATE_NEW;
        this.guard = CloseGuard.get();
        this.readTimeoutMilliseconds = STATE_NEW;
        this.writeTimeoutMilliseconds = STATE_NEW;
        this.handshakeTimeoutMilliseconds = -1;
        this.socket = socket;
        this.peerHostname = hostname;
        this.peerPort = port;
        this.autoClose = autoClose;
        this.sslParameters = sslParameters;
    }

    private void checkOpen() throws SocketException {
        if (isClosed()) {
            throw new SocketException("Socket is closed");
        }
    }

    public void startHandshake() throws IOException {
        checkOpen();
        synchronized (this.stateLock) {
            if (this.state == 0) {
                this.state = STATE_HANDSHAKE_STARTED;
                SecureRandom secureRandom = this.sslParameters.getSecureRandomMember();
                if (secureRandom == null) {
                    NativeCrypto.RAND_load_file("/dev/urandom", 1024);
                } else {
                    NativeCrypto.RAND_seed(secureRandom.generateSeed(NativeCrypto.SSL_kSRP));
                }
                boolean client = this.sslParameters.getUseClientMode();
                this.sslNativePointer = 0;
                boolean releaseResources = true;
                try {
                    long sslCtxNativePointer = this.sslParameters.getSessionContext().sslCtxNativePointer;
                    this.sslNativePointer = NativeCrypto.SSL_new(sslCtxNativePointer);
                    this.guard.open("close");
                    boolean enableSessionCreation = getEnableSessionCreation();
                    if (!enableSessionCreation) {
                        NativeCrypto.SSL_set_session_creation_enabled(this.sslNativePointer, enableSessionCreation);
                    }
                    OpenSSLSessionImpl sessionToReuse = this.sslParameters.getSessionToReuse(this.sslNativePointer, getHostname(), getPort());
                    this.sslParameters.setSSLParameters(sslCtxNativePointer, this.sslNativePointer, this, this, this.peerHostname);
                    this.sslParameters.setCertificateValidation(this.sslNativePointer);
                    this.sslParameters.setTlsChannelId(this.sslNativePointer, this.channelIdPrivateKey);
                    int savedReadTimeoutMilliseconds = getSoTimeout();
                    int savedWriteTimeoutMilliseconds = getSoWriteTimeout();
                    if (this.handshakeTimeoutMilliseconds >= 0) {
                        setSoTimeout(this.handshakeTimeoutMilliseconds);
                        setSoWriteTimeout(this.handshakeTimeoutMilliseconds);
                    }
                    synchronized (this.stateLock) {
                        if (this.state == STATE_CLOSED) {
                            if (STATE_HANDSHAKE_STARTED != null) {
                                synchronized (this.stateLock) {
                                    this.state = STATE_CLOSED;
                                    this.stateLock.notifyAll();
                                }
                                try {
                                    shutdownAndFreeSslNative();
                                    return;
                                } catch (IOException e) {
                                    return;
                                }
                            }
                            return;
                        }
                        byte[] bArr;
                        long j = this.sslNativePointer;
                        FileDescriptor fileDescriptor = Platform.getFileDescriptor(this.socket);
                        int soTimeout = getSoTimeout();
                        byte[] bArr2 = this.sslParameters.npnProtocols;
                        if (client) {
                            bArr = null;
                        } else {
                            bArr = this.sslParameters.alpnProtocols;
                        }
                        long sslSessionNativePointer = NativeCrypto.SSL_do_handshake(j, fileDescriptor, this, soTimeout, client, bArr2, bArr);
                        boolean handshakeCompleted = DBG_STATE;
                        synchronized (this.stateLock) {
                            if (this.state == STATE_HANDSHAKE_COMPLETED) {
                                handshakeCompleted = true;
                            } else if (this.state == STATE_CLOSED) {
                                if (STATE_HANDSHAKE_STARTED != null) {
                                    synchronized (this.stateLock) {
                                        this.state = STATE_CLOSED;
                                        this.stateLock.notifyAll();
                                    }
                                    try {
                                        shutdownAndFreeSslNative();
                                        return;
                                    } catch (IOException e2) {
                                        return;
                                    }
                                }
                                return;
                            }
                            this.sslSession = this.sslParameters.setupSession(sslSessionNativePointer, this.sslNativePointer, sessionToReuse, getHostname(), getPort(), handshakeCompleted);
                            if (this.handshakeTimeoutMilliseconds >= 0) {
                                setSoTimeout(savedReadTimeoutMilliseconds);
                                setSoWriteTimeout(savedWriteTimeoutMilliseconds);
                            }
                            if (handshakeCompleted) {
                                notifyHandshakeCompletedListeners();
                            }
                            synchronized (this.stateLock) {
                                releaseResources = this.state == STATE_CLOSED ? true : DBG_STATE;
                                if (this.state == STATE_HANDSHAKE_STARTED) {
                                    this.state = STATE_READY_HANDSHAKE_CUT_THROUGH;
                                } else if (this.state == STATE_HANDSHAKE_COMPLETED) {
                                    this.state = STATE_READY;
                                }
                                if (!releaseResources) {
                                    this.stateLock.notifyAll();
                                }
                            }
                            if (releaseResources) {
                                synchronized (this.stateLock) {
                                    this.state = STATE_CLOSED;
                                    this.stateLock.notifyAll();
                                }
                                try {
                                    shutdownAndFreeSslNative();
                                } catch (IOException e3) {
                                }
                            }
                        }
                    }
                } catch (CertificateException e4) {
                    SSLHandshakeException sSLHandshakeException = new SSLHandshakeException(e4.getMessage());
                    sSLHandshakeException.initCause(e4);
                    throw sSLHandshakeException;
                } catch (SSLException e5) {
                    synchronized (this.stateLock) {
                    }
                    if (this.state != STATE_CLOSED) {
                        if (e5.getMessage().contains("unexpected CCS")) {
                            Object[] objArr = new Object[STATE_HANDSHAKE_STARTED];
                            objArr[STATE_NEW] = getHostname();
                            Platform.logEvent(String.format("ssl_unexpected_ccs: host=%s", objArr));
                        }
                        throw e5;
                    } else if (STATE_HANDSHAKE_STARTED != null) {
                        synchronized (this.stateLock) {
                        }
                        this.state = STATE_CLOSED;
                        this.stateLock.notifyAll();
                        try {
                            shutdownAndFreeSslNative();
                        } catch (IOException e6) {
                        }
                    }
                } catch (SSLProtocolException e7) {
                    try {
                        throw ((SSLHandshakeException) new SSLHandshakeException("Handshake failed").initCause(e7));
                    } catch (Throwable th) {
                        if (releaseResources) {
                            synchronized (this.stateLock) {
                            }
                            this.state = STATE_CLOSED;
                            this.stateLock.notifyAll();
                            try {
                                shutdownAndFreeSslNative();
                            } catch (IOException e8) {
                            }
                        }
                    }
                }
            }
        }
    }

    private String getHostname() {
        if (this.peerHostname != null) {
            return this.peerHostname;
        }
        if (this.resolvedHostname == null) {
            InetAddress inetAddress = super.getInetAddress();
            if (inetAddress != null) {
                this.resolvedHostname = inetAddress.getHostName();
            }
        }
        return this.resolvedHostname;
    }

    public int getPort() {
        return this.peerPort == -1 ? super.getPort() : this.peerPort;
    }

    public void clientCertificateRequested(byte[] keyTypeBytes, byte[][] asn1DerEncodedPrincipals) throws CertificateEncodingException, SSLException {
        this.sslParameters.chooseClientCertificate(keyTypeBytes, asn1DerEncodedPrincipals, this.sslNativePointer, this);
    }

    public int clientPSKKeyRequested(String identityHint, byte[] identity, byte[] key) {
        return this.sslParameters.clientPSKKeyRequested(identityHint, identity, key, this);
    }

    public int serverPSKKeyRequested(String identityHint, String identity, byte[] key) {
        return this.sslParameters.serverPSKKeyRequested(identityHint, identity, key, this);
    }

    public void onSSLStateChange(long sslSessionNativePtr, int type, int val) {
        if (type == 32) {
            synchronized (this.stateLock) {
                if (this.state == STATE_HANDSHAKE_STARTED) {
                    this.state = STATE_HANDSHAKE_COMPLETED;
                    return;
                }
                if (this.state != STATE_READY_HANDSHAKE_CUT_THROUGH) {
                    if (this.state == STATE_CLOSED) {
                        return;
                    }
                }
                this.sslSession.resetId();
                (this.sslParameters.getUseClientMode() ? this.sslParameters.getClientSessionContext() : this.sslParameters.getServerSessionContext()).putSession(this.sslSession);
                notifyHandshakeCompletedListeners();
                synchronized (this.stateLock) {
                    this.state = STATE_READY;
                    this.stateLock.notifyAll();
                }
            }
        }
    }

    private void notifyHandshakeCompletedListeners() {
        if (this.listeners != null && !this.listeners.isEmpty()) {
            HandshakeCompletedEvent event = new HandshakeCompletedEvent(this, this.sslSession);
            Iterator i$ = this.listeners.iterator();
            while (i$.hasNext()) {
                try {
                    ((HandshakeCompletedListener) i$.next()).handshakeCompleted(event);
                } catch (RuntimeException e) {
                    Thread thread = Thread.currentThread();
                    thread.getUncaughtExceptionHandler().uncaughtException(thread, e);
                }
            }
        }
    }

    public void verifyCertificateChain(long sslSessionNativePtr, long[] certRefs, String authMethod) throws CertificateException {
        try {
            X509TrustManager x509tm = this.sslParameters.getX509TrustManager();
            if (x509tm == null) {
                throw new CertificateException("No X.509 TrustManager");
            }
            if (certRefs != null) {
                if (certRefs.length != 0) {
                    OpenSSLX509Certificate[] peerCertChain = new OpenSSLX509Certificate[certRefs.length];
                    for (int i = STATE_NEW; i < certRefs.length; i += STATE_HANDSHAKE_STARTED) {
                        peerCertChain[i] = new OpenSSLX509Certificate(certRefs[i]);
                    }
                    this.handshakeSession = new OpenSSLSessionImpl(sslSessionNativePtr, null, peerCertChain, getHostname(), getPort(), null);
                    if (this.sslParameters.getUseClientMode()) {
                        Platform.checkServerTrusted(x509tm, peerCertChain, authMethod, getHostname());
                    } else {
                        x509tm.checkClientTrusted(peerCertChain, peerCertChain[STATE_NEW].getPublicKey().getAlgorithm());
                    }
                    this.handshakeSession = null;
                    return;
                }
            }
            throw new SSLException("Peer sent no certificate");
        } catch (CertificateException e) {
            throw e;
        } catch (Exception e2) {
            throw new CertificateException(e2);
        } catch (Throwable th) {
            this.handshakeSession = null;
        }
    }

    public InputStream getInputStream() throws IOException {
        InputStream returnVal;
        checkOpen();
        synchronized (this.stateLock) {
            if (this.state == STATE_CLOSED) {
                throw new SocketException("Socket is closed.");
            }
            if (this.is == null) {
                this.is = new SSLInputStream();
            }
            returnVal = this.is;
        }
        waitForHandshake();
        return returnVal;
    }

    public OutputStream getOutputStream() throws IOException {
        OutputStream returnVal;
        checkOpen();
        synchronized (this.stateLock) {
            if (this.state == STATE_CLOSED) {
                throw new SocketException("Socket is closed.");
            }
            if (this.os == null) {
                this.os = new SSLOutputStream();
            }
            returnVal = this.os;
        }
        waitForHandshake();
        return returnVal;
    }

    private void assertReadableOrWriteableState() {
        if (this.state != STATE_READY && this.state != STATE_READY_HANDSHAKE_CUT_THROUGH) {
            throw new AssertionError("Invalid state: " + this.state);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void waitForHandshake() throws java.io.IOException {
        /*
        r6 = this;
        r5 = 5;
        r6.startHandshake();
        r3 = r6.stateLock;
        monitor-enter(r3);
    L_0x0007:
        r2 = r6.state;	 Catch:{ all -> 0x002e }
        r4 = 4;
        if (r2 == r4) goto L_0x0031;
    L_0x000c:
        r2 = r6.state;	 Catch:{ all -> 0x002e }
        r4 = 3;
        if (r2 == r4) goto L_0x0031;
    L_0x0011:
        r2 = r6.state;	 Catch:{ all -> 0x002e }
        if (r2 == r5) goto L_0x0031;
    L_0x0015:
        r2 = r6.stateLock;	 Catch:{ InterruptedException -> 0x001b }
        r2.wait();	 Catch:{ InterruptedException -> 0x001b }
        goto L_0x0007;
    L_0x001b:
        r0 = move-exception;
        r2 = java.lang.Thread.currentThread();	 Catch:{ all -> 0x002e }
        r2.interrupt();	 Catch:{ all -> 0x002e }
        r1 = new java.io.IOException;	 Catch:{ all -> 0x002e }
        r2 = "Interrupted waiting for handshake";
        r1.<init>(r2);	 Catch:{ all -> 0x002e }
        r1.initCause(r0);	 Catch:{ all -> 0x002e }
        throw r1;	 Catch:{ all -> 0x002e }
    L_0x002e:
        r2 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x002e }
        throw r2;
    L_0x0031:
        r2 = r6.state;	 Catch:{ all -> 0x002e }
        if (r2 != r5) goto L_0x003d;
    L_0x0035:
        r2 = new java.net.SocketException;	 Catch:{ all -> 0x002e }
        r4 = "Socket is closed";
        r2.<init>(r4);	 Catch:{ all -> 0x002e }
        throw r2;	 Catch:{ all -> 0x002e }
    L_0x003d:
        monitor-exit(r3);	 Catch:{ all -> 0x002e }
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.org.conscrypt.OpenSSLSocketImpl.waitForHandshake():void");
    }

    public SSLSession getSession() {
        if (this.sslSession == null) {
            try {
                waitForHandshake();
            } catch (IOException e) {
                return SSLNullSession.getNullSession();
            }
        }
        return this.sslSession;
    }

    public void addHandshakeCompletedListener(HandshakeCompletedListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Provided listener is null");
        }
        if (this.listeners == null) {
            this.listeners = new ArrayList();
        }
        this.listeners.add(listener);
    }

    public void removeHandshakeCompletedListener(HandshakeCompletedListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Provided listener is null");
        } else if (this.listeners == null) {
            throw new IllegalArgumentException("Provided listener is not registered");
        } else if (!this.listeners.remove(listener)) {
            throw new IllegalArgumentException("Provided listener is not registered");
        }
    }

    public boolean getEnableSessionCreation() {
        return this.sslParameters.getEnableSessionCreation();
    }

    public void setEnableSessionCreation(boolean flag) {
        this.sslParameters.setEnableSessionCreation(flag);
    }

    public String[] getSupportedCipherSuites() {
        return NativeCrypto.getSupportedCipherSuites();
    }

    public String[] getEnabledCipherSuites() {
        return this.sslParameters.getEnabledCipherSuites();
    }

    public void setEnabledCipherSuites(String[] suites) {
        this.sslParameters.setEnabledCipherSuites(suites);
    }

    public String[] getSupportedProtocols() {
        return NativeCrypto.getSupportedProtocols();
    }

    public String[] getEnabledProtocols() {
        return this.sslParameters.getEnabledProtocols();
    }

    public void setEnabledProtocols(String[] protocols) {
        this.sslParameters.setEnabledProtocols(protocols);
    }

    public void setUseSessionTickets(boolean useSessionTickets) {
        this.sslParameters.useSessionTickets = useSessionTickets;
    }

    public void setHostname(String hostname) {
        this.sslParameters.setUseSni(hostname != null ? true : DBG_STATE);
        this.peerHostname = hostname;
    }

    public void setChannelIdEnabled(boolean enabled) {
        if (getUseClientMode()) {
            throw new IllegalStateException("Client mode");
        }
        synchronized (this.stateLock) {
            if (this.state != 0) {
                throw new IllegalStateException("Could not enable/disable Channel ID after the initial handshake has begun.");
            }
        }
        this.sslParameters.channelIdEnabled = enabled;
    }

    public byte[] getChannelId() throws SSLException {
        if (getUseClientMode()) {
            throw new IllegalStateException("Client mode");
        }
        synchronized (this.stateLock) {
            if (this.state != STATE_READY) {
                throw new IllegalStateException("Channel ID is only available after handshake completes");
            }
        }
        return NativeCrypto.SSL_get_tls_channel_id(this.sslNativePointer);
    }

    public void setChannelIdPrivateKey(PrivateKey privateKey) {
        if (getUseClientMode()) {
            synchronized (this.stateLock) {
                if (this.state != 0) {
                    throw new IllegalStateException("Could not change Channel ID private key after the initial handshake has begun.");
                }
            }
            if (privateKey == null) {
                this.sslParameters.channelIdEnabled = DBG_STATE;
                this.channelIdPrivateKey = null;
                return;
            }
            this.sslParameters.channelIdEnabled = true;
            try {
                this.channelIdPrivateKey = OpenSSLKey.fromPrivateKey(privateKey);
                return;
            } catch (InvalidKeyException e) {
                return;
            }
        }
        throw new IllegalStateException("Server mode");
    }

    public boolean getUseClientMode() {
        return this.sslParameters.getUseClientMode();
    }

    public void setUseClientMode(boolean mode) {
        synchronized (this.stateLock) {
            if (this.state != 0) {
                throw new IllegalArgumentException("Could not change the mode after the initial handshake has begun.");
            }
        }
        this.sslParameters.setUseClientMode(mode);
    }

    public boolean getWantClientAuth() {
        return this.sslParameters.getWantClientAuth();
    }

    public boolean getNeedClientAuth() {
        return this.sslParameters.getNeedClientAuth();
    }

    public void setNeedClientAuth(boolean need) {
        this.sslParameters.setNeedClientAuth(need);
    }

    public void setWantClientAuth(boolean want) {
        this.sslParameters.setWantClientAuth(want);
    }

    public void sendUrgentData(int data) throws IOException {
        throw new SocketException("Method sendUrgentData() is not supported.");
    }

    public void setOOBInline(boolean on) throws SocketException {
        throw new SocketException("Methods sendUrgentData, setOOBInline are not supported.");
    }

    public void setSoTimeout(int readTimeoutMilliseconds) throws SocketException {
        super.setSoTimeout(readTimeoutMilliseconds);
        this.readTimeoutMilliseconds = readTimeoutMilliseconds;
    }

    public int getSoTimeout() throws SocketException {
        return this.readTimeoutMilliseconds;
    }

    public void setSoWriteTimeout(int writeTimeoutMilliseconds) throws SocketException {
        this.writeTimeoutMilliseconds = writeTimeoutMilliseconds;
        Platform.setSocketWriteTimeout(this, (long) writeTimeoutMilliseconds);
    }

    public int getSoWriteTimeout() throws SocketException {
        return this.writeTimeoutMilliseconds;
    }

    public void setHandshakeTimeout(int handshakeTimeoutMilliseconds) throws SocketException {
        this.handshakeTimeoutMilliseconds = handshakeTimeoutMilliseconds;
    }

    public void close() throws IOException {
        synchronized (this.stateLock) {
            if (this.state == STATE_CLOSED) {
                return;
            }
            int oldState = this.state;
            this.state = STATE_CLOSED;
            if (oldState == 0) {
                closeUnderlyingSocket();
                this.stateLock.notifyAll();
            } else if (oldState == STATE_READY || oldState == STATE_READY_HANDSHAKE_CUT_THROUGH) {
                this.stateLock.notifyAll();
                SSLInputStream sslInputStream = this.is;
                SSLOutputStream sslOutputStream = this.os;
                if (!(sslInputStream == null && sslOutputStream == null)) {
                    NativeCrypto.SSL_interrupt(this.sslNativePointer);
                }
                if (sslInputStream != null) {
                    sslInputStream.awaitPendingOps();
                }
                if (sslOutputStream != null) {
                    sslOutputStream.awaitPendingOps();
                }
                shutdownAndFreeSslNative();
            } else {
                NativeCrypto.SSL_interrupt(this.sslNativePointer);
                this.stateLock.notifyAll();
            }
        }
    }

    private void shutdownAndFreeSslNative() throws IOException {
        try {
            BlockGuard.getThreadPolicy().onNetwork();
            NativeCrypto.SSL_shutdown(this.sslNativePointer, Platform.getFileDescriptor(this.socket), this);
        } catch (IOException e) {
        } finally {
            free();
            closeUnderlyingSocket();
        }
    }

    private void closeUnderlyingSocket() throws IOException {
        if (this.socket != this) {
            if (this.autoClose && !this.socket.isClosed()) {
                this.socket.close();
            }
        } else if (!super.isClosed()) {
            super.close();
        }
    }

    private void free() {
        if (this.sslNativePointer != 0) {
            NativeCrypto.SSL_free(this.sslNativePointer);
            this.sslNativePointer = 0;
            this.guard.close();
        }
    }

    protected void finalize() throws Throwable {
        try {
            if (this.guard != null) {
                this.guard.warnIfOpen();
            }
            free();
        } finally {
            super.finalize();
        }
    }

    public FileDescriptor getFileDescriptor$() {
        if (this.socket == this) {
            return Platform.getFileDescriptorFromSSLSocket(this);
        }
        return Platform.getFileDescriptor(this.socket);
    }

    public byte[] getNpnSelectedProtocol() {
        return NativeCrypto.SSL_get_npn_negotiated_protocol(this.sslNativePointer);
    }

    public byte[] getAlpnSelectedProtocol() {
        return NativeCrypto.SSL_get0_alpn_selected(this.sslNativePointer);
    }

    public void setNpnProtocols(byte[] npnProtocols) {
        if (npnProtocols == null || npnProtocols.length != 0) {
            this.sslParameters.npnProtocols = npnProtocols;
            return;
        }
        throw new IllegalArgumentException("npnProtocols.length == 0");
    }

    public void setAlpnProtocols(byte[] alpnProtocols) {
        if (alpnProtocols == null || alpnProtocols.length != 0) {
            this.sslParameters.alpnProtocols = alpnProtocols;
            return;
        }
        throw new IllegalArgumentException("alpnProtocols.length == 0");
    }

    public String chooseServerAlias(X509KeyManager keyManager, String keyType) {
        return keyManager.chooseServerAlias(keyType, null, this);
    }

    public String chooseClientAlias(X509KeyManager keyManager, X500Principal[] issuers, String[] keyTypes) {
        return keyManager.chooseClientAlias(keyTypes, null, this);
    }

    public String chooseServerPSKIdentityHint(PSKKeyManager keyManager) {
        return keyManager.chooseServerKeyIdentityHint((Socket) this);
    }

    public String chooseClientPSKIdentity(PSKKeyManager keyManager, String identityHint) {
        return keyManager.chooseClientKeyIdentity(identityHint, (Socket) this);
    }

    public SecretKey getPSKKey(PSKKeyManager keyManager, String identityHint, String identity) {
        return keyManager.getKey(identityHint, identity, (Socket) this);
    }
}
