package android.webkit;

import android.speech.tts.Voice;
import android.view.KeyEvent;
import java.io.InputStream;
import java.util.Map;

public class WebResourceResponse {
    private String mEncoding;
    private InputStream mInputStream;
    private String mMimeType;
    private String mReasonPhrase;
    private Map<String, String> mResponseHeaders;
    private int mStatusCode;

    public WebResourceResponse(String mimeType, String encoding, InputStream data) {
        this.mMimeType = mimeType;
        this.mEncoding = encoding;
        this.mInputStream = data;
    }

    public WebResourceResponse(String mimeType, String encoding, int statusCode, String reasonPhrase, Map<String, String> responseHeaders, InputStream data) {
        this(mimeType, encoding, data);
        setStatusCodeAndReasonPhrase(statusCode, reasonPhrase);
        setResponseHeaders(responseHeaders);
    }

    public void setMimeType(String mimeType) {
        this.mMimeType = mimeType;
    }

    public String getMimeType() {
        return this.mMimeType;
    }

    public void setEncoding(String encoding) {
        this.mEncoding = encoding;
    }

    public String getEncoding() {
        return this.mEncoding;
    }

    public void setStatusCodeAndReasonPhrase(int statusCode, String reasonPhrase) {
        if (statusCode < 100) {
            throw new IllegalArgumentException("statusCode can't be less than 100.");
        } else if (statusCode > 599) {
            throw new IllegalArgumentException("statusCode can't be greater than 599.");
        } else if (statusCode > 299 && statusCode < Voice.QUALITY_HIGH) {
            throw new IllegalArgumentException("statusCode can't be in the [300, 399] range.");
        } else if (reasonPhrase == null) {
            throw new IllegalArgumentException("reasonPhrase can't be null.");
        } else if (reasonPhrase.trim().isEmpty()) {
            throw new IllegalArgumentException("reasonPhrase can't be empty.");
        } else {
            for (int i = 0; i < reasonPhrase.length(); i++) {
                if (reasonPhrase.charAt(i) > KeyEvent.KEYCODE_MEDIA_PAUSE) {
                    throw new IllegalArgumentException("reasonPhrase can't contain non-ASCII characters.");
                }
            }
            this.mStatusCode = statusCode;
            this.mReasonPhrase = reasonPhrase;
        }
    }

    public int getStatusCode() {
        return this.mStatusCode;
    }

    public String getReasonPhrase() {
        return this.mReasonPhrase;
    }

    public void setResponseHeaders(Map<String, String> headers) {
        this.mResponseHeaders = headers;
    }

    public Map<String, String> getResponseHeaders() {
        return this.mResponseHeaders;
    }

    public void setData(InputStream data) {
        this.mInputStream = data;
    }

    public InputStream getData() {
        return this.mInputStream;
    }
}
