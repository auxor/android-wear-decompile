package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import libcore.net.url.UrlUtils;

public final class URL implements Serializable {
    private static final long serialVersionUID = -7627629688361524110L;
    private static URLStreamHandlerFactory streamHandlerFactory;
    private static final Hashtable<String, URLStreamHandler> streamHandlers;
    private String authority;
    private String file;
    private transient int hashCode;
    private String host;
    private transient String path;
    private int port;
    private String protocol;
    private transient String query;
    private String ref;
    transient URLStreamHandler streamHandler;
    private transient String userInfo;

    static {
        streamHandlers = new Hashtable();
    }

    public static synchronized void setURLStreamHandlerFactory(URLStreamHandlerFactory factory) {
        synchronized (URL.class) {
            if (streamHandlerFactory != null) {
                throw new Error("Factory already set");
            }
            streamHandlers.clear();
            streamHandlerFactory = factory;
        }
    }

    public URL(String spec) throws MalformedURLException {
        this((URL) null, spec, null);
    }

    public URL(URL context, String spec) throws MalformedURLException {
        this(context, spec, null);
    }

    public URL(URL context, String spec, URLStreamHandler handler) throws MalformedURLException {
        this.port = -1;
        if (spec == null) {
            throw new MalformedURLException();
        }
        if (handler != null) {
            this.streamHandler = handler;
        }
        spec = spec.trim();
        this.protocol = UrlUtils.getSchemePrefix(spec);
        int schemeSpecificPartStart = this.protocol != null ? this.protocol.length() + 1 : 0;
        if (!(this.protocol == null || context == null || this.protocol.equals(context.protocol))) {
            context = null;
        }
        if (context != null) {
            set(context.protocol, context.getHost(), context.getPort(), context.getAuthority(), context.getUserInfo(), context.getPath(), context.getQuery(), context.getRef());
            if (this.streamHandler == null) {
                this.streamHandler = context.streamHandler;
            }
        } else if (this.protocol == null) {
            throw new MalformedURLException("Protocol not found: " + spec);
        }
        if (this.streamHandler == null) {
            setupStreamHandler();
            if (this.streamHandler == null) {
                throw new MalformedURLException("Unknown protocol: " + this.protocol);
            }
        }
        try {
            this.streamHandler.parseURL(this, spec, schemeSpecificPartStart, spec.length());
        } catch (Exception e) {
            throw new MalformedURLException(e.toString());
        }
    }

    public URL(String protocol, String host, String file) throws MalformedURLException {
        this(protocol, host, -1, file, null);
    }

    public URL(String protocol, String host, int port, String file) throws MalformedURLException {
        this(protocol, host, port, file, null);
    }

    public URL(String protocol, String host, int port, String file, URLStreamHandler handler) throws MalformedURLException {
        this.port = -1;
        if (port < -1) {
            throw new MalformedURLException("port < -1: " + port);
        } else if (protocol == null) {
            throw new NullPointerException("protocol == null");
        } else {
            if (!(host == null || !host.contains(":") || host.charAt(0) == '[')) {
                host = "[" + host + "]";
            }
            this.protocol = protocol;
            this.host = host;
            this.port = port;
            file = UrlUtils.authoritySafePath(host, file);
            int hash = file.indexOf("#");
            if (hash != -1) {
                this.file = file.substring(0, hash);
                this.ref = file.substring(hash + 1);
            } else {
                this.file = file;
            }
            fixURL(false);
            if (handler == null) {
                setupStreamHandler();
                if (this.streamHandler == null) {
                    throw new MalformedURLException("Unknown protocol: " + protocol);
                }
                return;
            }
            this.streamHandler = handler;
        }
    }

    void fixURL(boolean fixHost) {
        int index;
        if (this.host != null && this.host.length() > 0) {
            this.authority = this.host;
            if (this.port != -1) {
                this.authority += ":" + this.port;
            }
        }
        if (fixHost) {
            if (this.host != null) {
                index = this.host.lastIndexOf(64);
                if (index > -1) {
                    this.userInfo = this.host.substring(0, index);
                    this.host = this.host.substring(index + 1);
                }
            }
            this.userInfo = null;
        }
        if (this.file != null) {
            index = this.file.indexOf(63);
            if (index > -1) {
                this.query = this.file.substring(index + 1);
                this.path = this.file.substring(0, index);
                return;
            }
        }
        this.query = null;
        this.path = this.file;
    }

    protected void set(String protocol, String host, int port, String file, String ref) {
        if (this.protocol == null) {
            this.protocol = protocol;
        }
        this.host = host;
        this.file = file;
        this.port = port;
        this.ref = ref;
        this.hashCode = 0;
        fixURL(true);
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (getClass() == o.getClass()) {
            return this.streamHandler.equals(this, (URL) o);
        }
        return false;
    }

    public boolean sameFile(URL otherURL) {
        return this.streamHandler.sameFile(this, otherURL);
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = this.streamHandler.hashCode(this);
        }
        return this.hashCode;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void setupStreamHandler() {
        /*
        r13 = this;
        r10 = streamHandlers;
        r11 = r13.protocol;
        r10 = r10.get(r11);
        r10 = (java.net.URLStreamHandler) r10;
        r13.streamHandler = r10;
        r10 = r13.streamHandler;
        if (r10 == 0) goto L_0x0011;
    L_0x0010:
        return;
    L_0x0011:
        r10 = streamHandlerFactory;
        if (r10 == 0) goto L_0x002d;
    L_0x0015:
        r10 = streamHandlerFactory;
        r11 = r13.protocol;
        r10 = r10.createURLStreamHandler(r11);
        r13.streamHandler = r10;
        r10 = r13.streamHandler;
        if (r10 == 0) goto L_0x002d;
    L_0x0023:
        r10 = streamHandlers;
        r11 = r13.protocol;
        r12 = r13.streamHandler;
        r10.put(r11, r12);
        goto L_0x0010;
    L_0x002d:
        r10 = "java.protocol.handler.pkgs";
        r8 = java.lang.System.getProperty(r10);
        r10 = java.lang.Thread.currentThread();
        r3 = r10.getContextClassLoader();
        if (r8 == 0) goto L_0x0088;
    L_0x003d:
        if (r3 == 0) goto L_0x0088;
    L_0x003f:
        r10 = "\\|";
        r0 = r8.split(r10);
        r6 = r0.length;
        r5 = 0;
    L_0x0047:
        if (r5 >= r6) goto L_0x0088;
    L_0x0049:
        r9 = r0[r5];
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r10 = r10.append(r9);
        r11 = ".";
        r10 = r10.append(r11);
        r11 = r13.protocol;
        r10 = r10.append(r11);
        r11 = ".Handler";
        r10 = r10.append(r11);
        r2 = r10.toString();
        r1 = r3.loadClass(r2);	 Catch:{ IllegalAccessException -> 0x0084, InstantiationException -> 0x010f, ClassNotFoundException -> 0x010c }
        r10 = r1.newInstance();	 Catch:{ IllegalAccessException -> 0x0084, InstantiationException -> 0x010f, ClassNotFoundException -> 0x010c }
        r10 = (java.net.URLStreamHandler) r10;	 Catch:{ IllegalAccessException -> 0x0084, InstantiationException -> 0x010f, ClassNotFoundException -> 0x010c }
        r13.streamHandler = r10;	 Catch:{ IllegalAccessException -> 0x0084, InstantiationException -> 0x010f, ClassNotFoundException -> 0x010c }
        r10 = r13.streamHandler;	 Catch:{ IllegalAccessException -> 0x0084, InstantiationException -> 0x010f, ClassNotFoundException -> 0x010c }
        if (r10 == 0) goto L_0x0010;
    L_0x007a:
        r10 = streamHandlers;	 Catch:{ IllegalAccessException -> 0x0084, InstantiationException -> 0x010f, ClassNotFoundException -> 0x010c }
        r11 = r13.protocol;	 Catch:{ IllegalAccessException -> 0x0084, InstantiationException -> 0x010f, ClassNotFoundException -> 0x010c }
        r12 = r13.streamHandler;	 Catch:{ IllegalAccessException -> 0x0084, InstantiationException -> 0x010f, ClassNotFoundException -> 0x010c }
        r10.put(r11, r12);	 Catch:{ IllegalAccessException -> 0x0084, InstantiationException -> 0x010f, ClassNotFoundException -> 0x010c }
        goto L_0x0010;
    L_0x0084:
        r10 = move-exception;
    L_0x0085:
        r5 = r5 + 1;
        goto L_0x0047;
    L_0x0088:
        r10 = r13.protocol;
        r11 = "file";
        r10 = r10.equals(r11);
        if (r10 == 0) goto L_0x00a8;
    L_0x0092:
        r10 = new libcore.net.url.FileHandler;
        r10.<init>();
        r13.streamHandler = r10;
    L_0x0099:
        r10 = r13.streamHandler;
        if (r10 == 0) goto L_0x0010;
    L_0x009d:
        r10 = streamHandlers;
        r11 = r13.protocol;
        r12 = r13.streamHandler;
        r10.put(r11, r12);
        goto L_0x0010;
    L_0x00a8:
        r10 = r13.protocol;
        r11 = "ftp";
        r10 = r10.equals(r11);
        if (r10 == 0) goto L_0x00ba;
    L_0x00b2:
        r10 = new libcore.net.url.FtpHandler;
        r10.<init>();
        r13.streamHandler = r10;
        goto L_0x0099;
    L_0x00ba:
        r10 = r13.protocol;
        r11 = "http";
        r10 = r10.equals(r11);
        if (r10 == 0) goto L_0x00da;
    L_0x00c4:
        r7 = "com.android.okhttp.HttpHandler";
        r10 = java.lang.Class.forName(r7);	 Catch:{ Exception -> 0x00d3 }
        r10 = r10.newInstance();	 Catch:{ Exception -> 0x00d3 }
        r10 = (java.net.URLStreamHandler) r10;	 Catch:{ Exception -> 0x00d3 }
        r13.streamHandler = r10;	 Catch:{ Exception -> 0x00d3 }
        goto L_0x0099;
    L_0x00d3:
        r4 = move-exception;
        r10 = new java.lang.AssertionError;
        r10.<init>(r4);
        throw r10;
    L_0x00da:
        r10 = r13.protocol;
        r11 = "https";
        r10 = r10.equals(r11);
        if (r10 == 0) goto L_0x00fa;
    L_0x00e4:
        r7 = "com.android.okhttp.HttpsHandler";
        r10 = java.lang.Class.forName(r7);	 Catch:{ Exception -> 0x00f3 }
        r10 = r10.newInstance();	 Catch:{ Exception -> 0x00f3 }
        r10 = (java.net.URLStreamHandler) r10;	 Catch:{ Exception -> 0x00f3 }
        r13.streamHandler = r10;	 Catch:{ Exception -> 0x00f3 }
        goto L_0x0099;
    L_0x00f3:
        r4 = move-exception;
        r10 = new java.lang.AssertionError;
        r10.<init>(r4);
        throw r10;
    L_0x00fa:
        r10 = r13.protocol;
        r11 = "jar";
        r10 = r10.equals(r11);
        if (r10 == 0) goto L_0x0099;
    L_0x0104:
        r10 = new libcore.net.url.JarHandler;
        r10.<init>();
        r13.streamHandler = r10;
        goto L_0x0099;
    L_0x010c:
        r10 = move-exception;
        goto L_0x0085;
    L_0x010f:
        r10 = move-exception;
        goto L_0x0085;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.net.URL.setupStreamHandler():void");
    }

    public final Object getContent() throws IOException {
        return openConnection().getContent();
    }

    public final Object getContent(Class[] types) throws IOException {
        return openConnection().getContent(types);
    }

    public final InputStream openStream() throws IOException {
        return openConnection().getInputStream();
    }

    public URLConnection openConnection() throws IOException {
        return this.streamHandler.openConnection(this);
    }

    public URLConnection openConnection(Proxy proxy) throws IOException {
        if (proxy != null) {
            return this.streamHandler.openConnection(this, proxy);
        }
        throw new IllegalArgumentException("proxy == null");
    }

    public URI toURI() throws URISyntaxException {
        return new URI(toExternalForm());
    }

    public URI toURILenient() throws URISyntaxException {
        if (this.streamHandler != null) {
            return new URI(this.streamHandler.toExternalForm(this, true));
        }
        throw new IllegalStateException(this.protocol);
    }

    public String toString() {
        return toExternalForm();
    }

    public String toExternalForm() {
        if (this.streamHandler == null) {
            return "unknown protocol(" + this.protocol + ")://" + this.host + this.file;
        }
        return this.streamHandler.toExternalForm(this);
    }

    private void readObject(ObjectInputStream stream) throws IOException {
        try {
            stream.defaultReadObject();
            if (this.host != null && this.authority == null) {
                fixURL(true);
            } else if (this.authority != null) {
                int index = this.authority.lastIndexOf(64);
                if (index > -1) {
                    this.userInfo = this.authority.substring(0, index);
                }
                if (this.file != null) {
                    index = this.file.indexOf(63);
                    if (index > -1) {
                        this.query = this.file.substring(index + 1);
                        this.path = this.file.substring(0, index);
                    }
                }
                this.path = this.file;
            }
            setupStreamHandler();
            if (this.streamHandler == null) {
                throw new IOException("Unknown protocol: " + this.protocol);
            }
            this.hashCode = 0;
        } catch (Throwable e) {
            throw new IOException(e);
        }
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
    }

    public int getEffectivePort() {
        return URI.getEffectivePort(this.protocol, this.port);
    }

    public String getProtocol() {
        return this.protocol;
    }

    public String getAuthority() {
        return this.authority;
    }

    public String getUserInfo() {
        return this.userInfo;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public int getDefaultPort() {
        return this.streamHandler.getDefaultPort();
    }

    public String getFile() {
        return this.file;
    }

    public String getPath() {
        return this.path;
    }

    public String getQuery() {
        return this.query;
    }

    public String getRef() {
        return this.ref;
    }

    protected void set(String protocol, String host, int port, String authority, String userInfo, String path, String query, String ref) {
        String file = path;
        if (!(query == null || query.isEmpty())) {
            file = file + "?" + query;
        }
        set(protocol, host, port, file, ref);
        this.authority = authority;
        this.userInfo = userInfo;
        this.path = path;
        this.query = query;
    }
}
