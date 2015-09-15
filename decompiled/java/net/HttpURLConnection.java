package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.security.Permission;
import java.util.Arrays;

public abstract class HttpURLConnection extends URLConnection {
    private static final int DEFAULT_CHUNK_LENGTH = 1024;
    public static final int HTTP_ACCEPTED = 202;
    public static final int HTTP_BAD_GATEWAY = 502;
    public static final int HTTP_BAD_METHOD = 405;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_CLIENT_TIMEOUT = 408;
    public static final int HTTP_CONFLICT = 409;
    public static final int HTTP_CREATED = 201;
    public static final int HTTP_ENTITY_TOO_LARGE = 413;
    public static final int HTTP_FORBIDDEN = 403;
    public static final int HTTP_GATEWAY_TIMEOUT = 504;
    public static final int HTTP_GONE = 410;
    public static final int HTTP_INTERNAL_ERROR = 500;
    public static final int HTTP_LENGTH_REQUIRED = 411;
    public static final int HTTP_MOVED_PERM = 301;
    public static final int HTTP_MOVED_TEMP = 302;
    public static final int HTTP_MULT_CHOICE = 300;
    public static final int HTTP_NOT_ACCEPTABLE = 406;
    public static final int HTTP_NOT_AUTHORITATIVE = 203;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_NOT_IMPLEMENTED = 501;
    public static final int HTTP_NOT_MODIFIED = 304;
    public static final int HTTP_NO_CONTENT = 204;
    public static final int HTTP_OK = 200;
    public static final int HTTP_PARTIAL = 206;
    public static final int HTTP_PAYMENT_REQUIRED = 402;
    public static final int HTTP_PRECON_FAILED = 412;
    public static final int HTTP_PROXY_AUTH = 407;
    public static final int HTTP_REQ_TOO_LONG = 414;
    public static final int HTTP_RESET = 205;
    public static final int HTTP_SEE_OTHER = 303;
    @Deprecated
    public static final int HTTP_SERVER_ERROR = 500;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int HTTP_UNAVAILABLE = 503;
    public static final int HTTP_UNSUPPORTED_TYPE = 415;
    public static final int HTTP_USE_PROXY = 305;
    public static final int HTTP_VERSION = 505;
    private static final String[] PERMITTED_USER_METHODS;
    private static boolean followRedirects;
    protected int chunkLength;
    protected int fixedContentLength;
    protected long fixedContentLengthLong;
    protected boolean instanceFollowRedirects;
    protected String method;
    protected int responseCode;
    protected String responseMessage;

    public abstract void disconnect();

    public abstract boolean usingProxy();

    static {
        PERMITTED_USER_METHODS = new String[]{"OPTIONS", "GET", "HEAD", "POST", "PUT", "DELETE", "TRACE"};
        followRedirects = true;
    }

    protected HttpURLConnection(URL url) {
        super(url);
        this.method = "GET";
        this.responseCode = -1;
        this.instanceFollowRedirects = followRedirects;
        this.chunkLength = -1;
        this.fixedContentLength = -1;
        this.fixedContentLengthLong = -1;
    }

    public InputStream getErrorStream() {
        return null;
    }

    public static boolean getFollowRedirects() {
        return followRedirects;
    }

    public Permission getPermission() throws IOException {
        int port = this.url.getPort();
        if (port < 0) {
            port = 80;
        }
        return new SocketPermission(this.url.getHost() + ":" + port, "connect, resolve");
    }

    public String getRequestMethod() {
        return this.method;
    }

    public int getResponseCode() throws IOException {
        getInputStream();
        String response = getHeaderField(0);
        if (response == null) {
            return -1;
        }
        response = response.trim();
        int mark = response.indexOf(" ") + 1;
        if (mark == 0) {
            return -1;
        }
        int last = mark + 3;
        if (last > response.length()) {
            last = response.length();
        }
        this.responseCode = Integer.parseInt(response.substring(mark, last));
        if (last + 1 <= response.length()) {
            this.responseMessage = response.substring(last + 1);
        }
        return this.responseCode;
    }

    public String getResponseMessage() throws IOException {
        if (this.responseMessage != null) {
            return this.responseMessage;
        }
        getResponseCode();
        return this.responseMessage;
    }

    public static void setFollowRedirects(boolean auto) {
        followRedirects = auto;
    }

    public void setRequestMethod(String method) throws ProtocolException {
        if (this.connected) {
            throw new ProtocolException("Connection already established");
        }
        for (String permittedUserMethod : PERMITTED_USER_METHODS) {
            if (permittedUserMethod.equals(method)) {
                this.method = permittedUserMethod;
                return;
            }
        }
        throw new ProtocolException("Unknown method '" + method + "'; must be one of " + Arrays.toString(PERMITTED_USER_METHODS));
    }

    public String getContentEncoding() {
        return super.getContentEncoding();
    }

    public boolean getInstanceFollowRedirects() {
        return this.instanceFollowRedirects;
    }

    public void setInstanceFollowRedirects(boolean followRedirects) {
        this.instanceFollowRedirects = followRedirects;
    }

    public long getHeaderFieldDate(String field, long defaultValue) {
        return super.getHeaderFieldDate(field, defaultValue);
    }

    public void setFixedLengthStreamingMode(long contentLength) {
        if (this.connected) {
            throw new IllegalStateException("Already connected");
        } else if (this.chunkLength > 0) {
            throw new IllegalStateException("Already in chunked mode");
        } else if (contentLength < 0) {
            throw new IllegalArgumentException("contentLength < 0");
        } else {
            this.fixedContentLength = (int) Math.min(contentLength, 2147483647L);
            this.fixedContentLengthLong = contentLength;
        }
    }

    public void setFixedLengthStreamingMode(int contentLength) {
        setFixedLengthStreamingMode((long) contentLength);
    }

    public void setChunkedStreamingMode(int chunkLength) {
        if (this.connected) {
            throw new IllegalStateException("Already connected");
        } else if (this.fixedContentLength >= 0) {
            throw new IllegalStateException("Already in fixed-length mode");
        } else if (chunkLength <= 0) {
            this.chunkLength = DEFAULT_CHUNK_LENGTH;
        } else {
            this.chunkLength = chunkLength;
        }
    }
}
