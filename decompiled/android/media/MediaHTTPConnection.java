package android.media;

import android.net.NetworkUtils;
import android.os.IBinder;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.util.Log;
import com.android.internal.http.multipart.FilePart;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MediaHTTPConnection extends IMediaHTTPConnection$Stub {
    private static final int HTTP_TEMP_REDIRECT = 307;
    private static final int MAX_REDIRECTS = 20;
    private static final String TAG = "MediaHTTPConnection";
    private static final boolean VERBOSE = false;
    private boolean mAllowCrossDomainRedirect;
    private boolean mAllowCrossProtocolRedirect;
    private HttpURLConnection mConnection;
    private long mCurrentOffset;
    private Map<String, String> mHeaders;
    private InputStream mInputStream;
    private long mNativeContext;
    private long mTotalSize;
    private URL mURL;

    private final native void native_finalize();

    private final native IBinder native_getIMemory();

    private static final native void native_init();

    private final native int native_readAt(long j, int i);

    private final native void native_setup();

    public MediaHTTPConnection() {
        this.mCurrentOffset = -1;
        this.mURL = null;
        this.mHeaders = null;
        this.mConnection = null;
        this.mTotalSize = -1;
        this.mInputStream = null;
        this.mAllowCrossDomainRedirect = true;
        this.mAllowCrossProtocolRedirect = true;
        if (CookieHandler.getDefault() == null) {
            CookieHandler.setDefault(new CookieManager());
        }
        native_setup();
    }

    public IBinder connect(String uri, String headers) {
        try {
            disconnect();
            this.mAllowCrossDomainRedirect = true;
            this.mURL = new URL(uri);
            this.mHeaders = convertHeaderStringToMap(headers);
            return native_getIMemory();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    private boolean parseBoolean(String val) {
        boolean z = false;
        try {
            if (Long.parseLong(val) != 0) {
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            if ("true".equalsIgnoreCase(val) || "yes".equalsIgnoreCase(val)) {
                z = true;
            }
            return z;
        }
    }

    private boolean filterOutInternalHeaders(String key, String val) {
        if (!"android-allow-cross-domain-redirect".equalsIgnoreCase(key)) {
            return false;
        }
        this.mAllowCrossDomainRedirect = parseBoolean(val);
        this.mAllowCrossProtocolRedirect = this.mAllowCrossDomainRedirect;
        return true;
    }

    private Map<String, String> convertHeaderStringToMap(String headers) {
        HashMap<String, String> map = new HashMap();
        for (String pair : headers.split("\r\n")) {
            int colonPos = pair.indexOf(":");
            if (colonPos >= 0) {
                String key = pair.substring(0, colonPos);
                String val = pair.substring(colonPos + 1);
                if (!filterOutInternalHeaders(key, val)) {
                    map.put(key, val);
                }
            }
        }
        return map;
    }

    public void disconnect() {
        teardownConnection();
        this.mHeaders = null;
        this.mURL = null;
    }

    private void teardownConnection() {
        if (this.mConnection != null) {
            this.mInputStream = null;
            this.mConnection.disconnect();
            this.mConnection = null;
            this.mCurrentOffset = -1;
        }
    }

    private static final boolean isLocalHost(URL url) {
        if (url == null) {
            return false;
        }
        String host = url.getHost();
        if (host == null) {
            return false;
        }
        try {
            if (host.equalsIgnoreCase("localhost")) {
                return true;
            }
            if (NetworkUtils.numericToInetAddress(host).isLoopbackAddress()) {
                return true;
            }
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void seekTo(long r24) throws java.io.IOException {
        /*
        r23 = this;
        r23.teardownConnection();
        r12 = 0;
        r0 = r23;
        r0 = r0.mURL;	 Catch:{ IOException -> 0x006b }
        r17 = r0;
        r11 = isLocalHost(r17);	 Catch:{ IOException -> 0x006b }
    L_0x000e:
        if (r11 == 0) goto L_0x008d;
    L_0x0010:
        r18 = java.net.Proxy.NO_PROXY;	 Catch:{ IOException -> 0x006b }
        r18 = r17.openConnection(r18);	 Catch:{ IOException -> 0x006b }
        r18 = (java.net.HttpURLConnection) r18;	 Catch:{ IOException -> 0x006b }
        r0 = r18;
        r1 = r23;
        r1.mConnection = r0;	 Catch:{ IOException -> 0x006b }
    L_0x001e:
        r0 = r23;
        r0 = r0.mConnection;	 Catch:{ IOException -> 0x006b }
        r18 = r0;
        r0 = r23;
        r0 = r0.mAllowCrossDomainRedirect;	 Catch:{ IOException -> 0x006b }
        r19 = r0;
        r18.setInstanceFollowRedirects(r19);	 Catch:{ IOException -> 0x006b }
        r0 = r23;
        r0 = r0.mHeaders;	 Catch:{ IOException -> 0x006b }
        r18 = r0;
        if (r18 == 0) goto L_0x009a;
    L_0x0035:
        r0 = r23;
        r0 = r0.mHeaders;	 Catch:{ IOException -> 0x006b }
        r18 = r0;
        r18 = r18.entrySet();	 Catch:{ IOException -> 0x006b }
        r7 = r18.iterator();	 Catch:{ IOException -> 0x006b }
    L_0x0043:
        r18 = r7.hasNext();	 Catch:{ IOException -> 0x006b }
        if (r18 == 0) goto L_0x009a;
    L_0x0049:
        r6 = r7.next();	 Catch:{ IOException -> 0x006b }
        r6 = (java.util.Map.Entry) r6;	 Catch:{ IOException -> 0x006b }
        r0 = r23;
        r0 = r0.mConnection;	 Catch:{ IOException -> 0x006b }
        r20 = r0;
        r18 = r6.getKey();	 Catch:{ IOException -> 0x006b }
        r18 = (java.lang.String) r18;	 Catch:{ IOException -> 0x006b }
        r19 = r6.getValue();	 Catch:{ IOException -> 0x006b }
        r19 = (java.lang.String) r19;	 Catch:{ IOException -> 0x006b }
        r0 = r20;
        r1 = r18;
        r2 = r19;
        r0.setRequestProperty(r1, r2);	 Catch:{ IOException -> 0x006b }
        goto L_0x0043;
    L_0x006b:
        r5 = move-exception;
        r18 = -1;
        r0 = r18;
        r2 = r23;
        r2.mTotalSize = r0;
        r18 = 0;
        r0 = r18;
        r1 = r23;
        r1.mInputStream = r0;
        r18 = 0;
        r0 = r18;
        r1 = r23;
        r1.mConnection = r0;
        r18 = -1;
        r0 = r18;
        r2 = r23;
        r2.mCurrentOffset = r0;
        throw r5;
    L_0x008d:
        r18 = r17.openConnection();	 Catch:{ IOException -> 0x006b }
        r18 = (java.net.HttpURLConnection) r18;	 Catch:{ IOException -> 0x006b }
        r0 = r18;
        r1 = r23;
        r1.mConnection = r0;	 Catch:{ IOException -> 0x006b }
        goto L_0x001e;
    L_0x009a:
        r18 = 0;
        r18 = (r24 > r18 ? 1 : (r24 == r18 ? 0 : -1));
        if (r18 <= 0) goto L_0x00c8;
    L_0x00a0:
        r0 = r23;
        r0 = r0.mConnection;	 Catch:{ IOException -> 0x006b }
        r18 = r0;
        r19 = "Range";
        r20 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x006b }
        r20.<init>();	 Catch:{ IOException -> 0x006b }
        r21 = "bytes=";
        r20 = r20.append(r21);	 Catch:{ IOException -> 0x006b }
        r0 = r20;
        r1 = r24;
        r20 = r0.append(r1);	 Catch:{ IOException -> 0x006b }
        r21 = "-";
        r20 = r20.append(r21);	 Catch:{ IOException -> 0x006b }
        r20 = r20.toString();	 Catch:{ IOException -> 0x006b }
        r18.setRequestProperty(r19, r20);	 Catch:{ IOException -> 0x006b }
    L_0x00c8:
        r0 = r23;
        r0 = r0.mConnection;	 Catch:{ IOException -> 0x006b }
        r18 = r0;
        r13 = r18.getResponseCode();	 Catch:{ IOException -> 0x006b }
        r18 = 300; // 0x12c float:4.2E-43 double:1.48E-321;
        r0 = r18;
        if (r13 == r0) goto L_0x0152;
    L_0x00d8:
        r18 = 301; // 0x12d float:4.22E-43 double:1.487E-321;
        r0 = r18;
        if (r13 == r0) goto L_0x0152;
    L_0x00de:
        r18 = 302; // 0x12e float:4.23E-43 double:1.49E-321;
        r0 = r18;
        if (r13 == r0) goto L_0x0152;
    L_0x00e4:
        r18 = 303; // 0x12f float:4.25E-43 double:1.497E-321;
        r0 = r18;
        if (r13 == r0) goto L_0x0152;
    L_0x00ea:
        r18 = 307; // 0x133 float:4.3E-43 double:1.517E-321;
        r0 = r18;
        if (r13 == r0) goto L_0x0152;
    L_0x00f0:
        r0 = r23;
        r0 = r0.mAllowCrossDomainRedirect;	 Catch:{ IOException -> 0x006b }
        r18 = r0;
        if (r18 == 0) goto L_0x0108;
    L_0x00f8:
        r0 = r23;
        r0 = r0.mConnection;	 Catch:{ IOException -> 0x006b }
        r18 = r0;
        r18 = r18.getURL();	 Catch:{ IOException -> 0x006b }
        r0 = r18;
        r1 = r23;
        r1.mURL = r0;	 Catch:{ IOException -> 0x006b }
    L_0x0108:
        r18 = 206; // 0xce float:2.89E-43 double:1.02E-321;
        r0 = r18;
        if (r13 != r0) goto L_0x023c;
    L_0x010e:
        r0 = r23;
        r0 = r0.mConnection;	 Catch:{ IOException -> 0x006b }
        r18 = r0;
        r19 = "Content-Range";
        r4 = r18.getHeaderField(r19);	 Catch:{ IOException -> 0x006b }
        r18 = -1;
        r0 = r18;
        r2 = r23;
        r2.mTotalSize = r0;	 Catch:{ IOException -> 0x006b }
        if (r4 == 0) goto L_0x0140;
    L_0x0124:
        r18 = 47;
        r0 = r18;
        r8 = r4.lastIndexOf(r0);	 Catch:{ IOException -> 0x006b }
        if (r8 < 0) goto L_0x0140;
    L_0x012e:
        r18 = r8 + 1;
        r0 = r18;
        r16 = r4.substring(r0);	 Catch:{ IOException -> 0x006b }
        r18 = java.lang.Long.parseLong(r16);	 Catch:{ NumberFormatException -> 0x027b }
        r0 = r18;
        r2 = r23;
        r2.mTotalSize = r0;	 Catch:{ NumberFormatException -> 0x027b }
    L_0x0140:
        r18 = 0;
        r18 = (r24 > r18 ? 1 : (r24 == r18 ? 0 : -1));
        if (r18 <= 0) goto L_0x025f;
    L_0x0146:
        r18 = 206; // 0xce float:2.89E-43 double:1.02E-321;
        r0 = r18;
        if (r13 == r0) goto L_0x025f;
    L_0x014c:
        r18 = new java.net.ProtocolException;	 Catch:{ IOException -> 0x006b }
        r18.<init>();	 Catch:{ IOException -> 0x006b }
        throw r18;	 Catch:{ IOException -> 0x006b }
    L_0x0152:
        r12 = r12 + 1;
        r18 = 20;
        r0 = r18;
        if (r12 <= r0) goto L_0x0175;
    L_0x015a:
        r18 = new java.net.NoRouteToHostException;	 Catch:{ IOException -> 0x006b }
        r19 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x006b }
        r19.<init>();	 Catch:{ IOException -> 0x006b }
        r20 = "Too many redirects: ";
        r19 = r19.append(r20);	 Catch:{ IOException -> 0x006b }
        r0 = r19;
        r19 = r0.append(r12);	 Catch:{ IOException -> 0x006b }
        r19 = r19.toString();	 Catch:{ IOException -> 0x006b }
        r18.<init>(r19);	 Catch:{ IOException -> 0x006b }
        throw r18;	 Catch:{ IOException -> 0x006b }
    L_0x0175:
        r0 = r23;
        r0 = r0.mConnection;	 Catch:{ IOException -> 0x006b }
        r18 = r0;
        r10 = r18.getRequestMethod();	 Catch:{ IOException -> 0x006b }
        r18 = 307; // 0x133 float:4.3E-43 double:1.517E-321;
        r0 = r18;
        if (r13 != r0) goto L_0x01a1;
    L_0x0185:
        r18 = "GET";
        r0 = r18;
        r18 = r10.equals(r0);	 Catch:{ IOException -> 0x006b }
        if (r18 != 0) goto L_0x01a1;
    L_0x018f:
        r18 = "HEAD";
        r0 = r18;
        r18 = r10.equals(r0);	 Catch:{ IOException -> 0x006b }
        if (r18 != 0) goto L_0x01a1;
    L_0x0199:
        r18 = new java.net.NoRouteToHostException;	 Catch:{ IOException -> 0x006b }
        r19 = "Invalid redirect";
        r18.<init>(r19);	 Catch:{ IOException -> 0x006b }
        throw r18;	 Catch:{ IOException -> 0x006b }
    L_0x01a1:
        r0 = r23;
        r0 = r0.mConnection;	 Catch:{ IOException -> 0x006b }
        r18 = r0;
        r19 = "Location";
        r9 = r18.getHeaderField(r19);	 Catch:{ IOException -> 0x006b }
        if (r9 != 0) goto L_0x01b7;
    L_0x01af:
        r18 = new java.net.NoRouteToHostException;	 Catch:{ IOException -> 0x006b }
        r19 = "Invalid redirect";
        r18.<init>(r19);	 Catch:{ IOException -> 0x006b }
        throw r18;	 Catch:{ IOException -> 0x006b }
    L_0x01b7:
        r17 = new java.net.URL;	 Catch:{ IOException -> 0x006b }
        r0 = r23;
        r0 = r0.mURL;	 Catch:{ IOException -> 0x006b }
        r18 = r0;
        r0 = r17;
        r1 = r18;
        r0.<init>(r1, r9);	 Catch:{ IOException -> 0x006b }
        r18 = r17.getProtocol();	 Catch:{ IOException -> 0x006b }
        r19 = "https";
        r18 = r18.equals(r19);	 Catch:{ IOException -> 0x006b }
        if (r18 != 0) goto L_0x01e6;
    L_0x01d2:
        r18 = r17.getProtocol();	 Catch:{ IOException -> 0x006b }
        r19 = "http";
        r18 = r18.equals(r19);	 Catch:{ IOException -> 0x006b }
        if (r18 != 0) goto L_0x01e6;
    L_0x01de:
        r18 = new java.net.NoRouteToHostException;	 Catch:{ IOException -> 0x006b }
        r19 = "Unsupported protocol redirect";
        r18.<init>(r19);	 Catch:{ IOException -> 0x006b }
        throw r18;	 Catch:{ IOException -> 0x006b }
    L_0x01e6:
        r0 = r23;
        r0 = r0.mURL;	 Catch:{ IOException -> 0x006b }
        r18 = r0;
        r18 = r18.getProtocol();	 Catch:{ IOException -> 0x006b }
        r19 = r17.getProtocol();	 Catch:{ IOException -> 0x006b }
        r15 = r18.equals(r19);	 Catch:{ IOException -> 0x006b }
        r0 = r23;
        r0 = r0.mAllowCrossProtocolRedirect;	 Catch:{ IOException -> 0x006b }
        r18 = r0;
        if (r18 != 0) goto L_0x020a;
    L_0x0200:
        if (r15 != 0) goto L_0x020a;
    L_0x0202:
        r18 = new java.net.NoRouteToHostException;	 Catch:{ IOException -> 0x006b }
        r19 = "Cross-protocol redirects are disallowed";
        r18.<init>(r19);	 Catch:{ IOException -> 0x006b }
        throw r18;	 Catch:{ IOException -> 0x006b }
    L_0x020a:
        r0 = r23;
        r0 = r0.mURL;	 Catch:{ IOException -> 0x006b }
        r18 = r0;
        r18 = r18.getHost();	 Catch:{ IOException -> 0x006b }
        r19 = r17.getHost();	 Catch:{ IOException -> 0x006b }
        r14 = r18.equals(r19);	 Catch:{ IOException -> 0x006b }
        r0 = r23;
        r0 = r0.mAllowCrossDomainRedirect;	 Catch:{ IOException -> 0x006b }
        r18 = r0;
        if (r18 != 0) goto L_0x022e;
    L_0x0224:
        if (r14 != 0) goto L_0x022e;
    L_0x0226:
        r18 = new java.net.NoRouteToHostException;	 Catch:{ IOException -> 0x006b }
        r19 = "Cross-domain redirects are disallowed";
        r18.<init>(r19);	 Catch:{ IOException -> 0x006b }
        throw r18;	 Catch:{ IOException -> 0x006b }
    L_0x022e:
        r18 = 307; // 0x133 float:4.3E-43 double:1.517E-321;
        r0 = r18;
        if (r13 == r0) goto L_0x000e;
    L_0x0234:
        r0 = r17;
        r1 = r23;
        r1.mURL = r0;	 Catch:{ IOException -> 0x006b }
        goto L_0x000e;
    L_0x023c:
        r18 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r0 = r18;
        if (r13 == r0) goto L_0x0248;
    L_0x0242:
        r18 = new java.io.IOException;	 Catch:{ IOException -> 0x006b }
        r18.<init>();	 Catch:{ IOException -> 0x006b }
        throw r18;	 Catch:{ IOException -> 0x006b }
    L_0x0248:
        r0 = r23;
        r0 = r0.mConnection;	 Catch:{ IOException -> 0x006b }
        r18 = r0;
        r18 = r18.getContentLength();	 Catch:{ IOException -> 0x006b }
        r0 = r18;
        r0 = (long) r0;	 Catch:{ IOException -> 0x006b }
        r18 = r0;
        r0 = r18;
        r2 = r23;
        r2.mTotalSize = r0;	 Catch:{ IOException -> 0x006b }
        goto L_0x0140;
    L_0x025f:
        r18 = new java.io.BufferedInputStream;	 Catch:{ IOException -> 0x006b }
        r0 = r23;
        r0 = r0.mConnection;	 Catch:{ IOException -> 0x006b }
        r19 = r0;
        r19 = r19.getInputStream();	 Catch:{ IOException -> 0x006b }
        r18.<init>(r19);	 Catch:{ IOException -> 0x006b }
        r0 = r18;
        r1 = r23;
        r1.mInputStream = r0;	 Catch:{ IOException -> 0x006b }
        r0 = r24;
        r2 = r23;
        r2.mCurrentOffset = r0;	 Catch:{ IOException -> 0x006b }
        return;
    L_0x027b:
        r18 = move-exception;
        goto L_0x0140;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.MediaHTTPConnection.seekTo(long):void");
    }

    public int readAt(long offset, int size) {
        return native_readAt(offset, size);
    }

    private int readAt(long offset, byte[] data, int size) {
        StrictMode.setThreadPolicy(new Builder().permitAll().build());
        try {
            if (offset != this.mCurrentOffset) {
                seekTo(offset);
            }
            int n = this.mInputStream.read(data, 0, size);
            if (n == -1) {
                n = 0;
            }
            this.mCurrentOffset += (long) n;
            return n;
        } catch (ProtocolException e) {
            Log.w(TAG, "readAt " + offset + " / " + size + " => " + e);
            return MediaPlayer.MEDIA_ERROR_UNSUPPORTED;
        } catch (NoRouteToHostException e2) {
            Log.w(TAG, "readAt " + offset + " / " + size + " => " + e2);
            return MediaPlayer.MEDIA_ERROR_UNSUPPORTED;
        } catch (IOException e3) {
            return -1;
        } catch (Exception e4) {
            return -1;
        }
    }

    public long getSize() {
        if (this.mConnection == null) {
            try {
                seekTo(0);
            } catch (IOException e) {
                return -1;
            }
        }
        return this.mTotalSize;
    }

    public String getMIMEType() {
        if (this.mConnection == null) {
            try {
                seekTo(0);
            } catch (IOException e) {
                return FilePart.DEFAULT_CONTENT_TYPE;
            }
        }
        return this.mConnection.getContentType();
    }

    public String getUri() {
        return this.mURL.toString();
    }

    protected void finalize() {
        native_finalize();
    }

    static {
        System.loadLibrary("media_jni");
        native_init();
    }
}
