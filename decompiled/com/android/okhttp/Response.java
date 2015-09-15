package com.android.okhttp;

import com.android.okhttp.internal.Util;
import com.android.okhttp.internal.http.OkHeaders;
import com.android.okhttp.internal.http.StatusLine;
import com.android.okio.Okio;
import com.android.okio.Source;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Set;

public final class Response {
    private final Body body;
    private volatile CacheControl cacheControl;
    private Response cacheResponse;
    private final Handshake handshake;
    private final Headers headers;
    private Response networkResponse;
    private volatile ParsedHeaders parsedHeaders;
    private final Response priorResponse;
    private final Request request;
    private final StatusLine statusLine;

    public static abstract class Body implements Closeable {
        private Reader reader;
        private Source source;

        public abstract InputStream byteStream();

        public abstract long contentLength();

        public abstract MediaType contentType();

        public abstract boolean ready() throws IOException;

        public Source source() {
            Source s = this.source;
            if (s != null) {
                return s;
            }
            s = Okio.source(byteStream());
            this.source = s;
            return s;
        }

        public final byte[] bytes() throws IOException {
            long contentLength = contentLength();
            if (contentLength > 2147483647L) {
                throw new IOException("Cannot buffer entire body for content length: " + contentLength);
            } else if (contentLength != -1) {
                byte[] bArr = new byte[((int) contentLength)];
                InputStream in = byteStream();
                Util.readFully(in, bArr);
                if (in.read() == -1) {
                    return bArr;
                }
                throw new IOException("Content-Length and stream length disagree");
            } else {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Util.copy(byteStream(), out);
                return out.toByteArray();
            }
        }

        public final Reader charStream() {
            Reader r = this.reader;
            if (r != null) {
                return r;
            }
            r = new InputStreamReader(byteStream(), charset());
            this.reader = r;
            return r;
        }

        public final String string() throws IOException {
            return new String(bytes(), charset().name());
        }

        private Charset charset() {
            MediaType contentType = contentType();
            return contentType != null ? contentType.charset(Util.UTF_8) : Util.UTF_8;
        }

        public void close() throws IOException {
            byteStream().close();
        }
    }

    public static class Builder {
        private Body body;
        private Response cacheResponse;
        private Handshake handshake;
        private com.android.okhttp.Headers.Builder headers;
        private Response networkResponse;
        private Response priorResponse;
        private Request request;
        private StatusLine statusLine;

        public Builder() {
            this.headers = new com.android.okhttp.Headers.Builder();
        }

        private Builder(Response response) {
            this.request = response.request;
            this.statusLine = response.statusLine;
            this.handshake = response.handshake;
            this.headers = response.headers.newBuilder();
            this.body = response.body;
            this.networkResponse = response.networkResponse;
            this.cacheResponse = response.cacheResponse;
            this.priorResponse = response.priorResponse;
        }

        public Builder request(Request request) {
            this.request = request;
            return this;
        }

        public Builder statusLine(StatusLine statusLine) {
            if (statusLine == null) {
                throw new IllegalArgumentException("statusLine == null");
            }
            this.statusLine = statusLine;
            return this;
        }

        public Builder statusLine(String statusLine) {
            try {
                return statusLine(new StatusLine(statusLine));
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }

        public Builder handshake(Handshake handshake) {
            this.handshake = handshake;
            return this;
        }

        public Builder header(String name, String value) {
            this.headers.set(name, value);
            return this;
        }

        public Builder addHeader(String name, String value) {
            this.headers.add(name, value);
            return this;
        }

        public Builder removeHeader(String name) {
            this.headers.removeAll(name);
            return this;
        }

        public Builder headers(Headers headers) {
            this.headers = headers.newBuilder();
            return this;
        }

        public Builder body(Body body) {
            this.body = body;
            return this;
        }

        public Builder setResponseSource(ResponseSource responseSource) {
            return header(OkHeaders.RESPONSE_SOURCE, responseSource + " " + this.statusLine.code());
        }

        public Builder networkResponse(Response networkResponse) {
            if (networkResponse != null) {
                checkSupportResponse("networkResponse", networkResponse);
            }
            this.networkResponse = networkResponse;
            return this;
        }

        public Builder cacheResponse(Response cacheResponse) {
            if (cacheResponse != null) {
                checkSupportResponse("cacheResponse", cacheResponse);
            }
            this.cacheResponse = cacheResponse;
            return this;
        }

        private void checkSupportResponse(String name, Response response) {
            if (response.body != null) {
                throw new IllegalArgumentException(name + ".body != null");
            } else if (response.networkResponse != null) {
                throw new IllegalArgumentException(name + ".networkResponse != null");
            } else if (response.cacheResponse != null) {
                throw new IllegalArgumentException(name + ".cacheResponse != null");
            } else if (response.priorResponse != null) {
                throw new IllegalArgumentException(name + ".priorResponse != null");
            }
        }

        public Builder priorResponse(Response priorResponse) {
            this.priorResponse = priorResponse;
            return this;
        }

        public Response build() {
            if (this.request == null) {
                throw new IllegalStateException("request == null");
            } else if (this.statusLine != null) {
                return new Response();
            } else {
                throw new IllegalStateException("statusLine == null");
            }
        }
    }

    private static class ParsedHeaders {
        Date lastModified;
        private Set<String> varyFields;

        private ParsedHeaders(com.android.okhttp.Headers r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.okhttp.Response.ParsedHeaders.<init>(com.android.okhttp.Headers):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.okhttp.Response.ParsedHeaders.<init>(com.android.okhttp.Headers):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.okhttp.Response.ParsedHeaders.<init>(com.android.okhttp.Headers):void");
        }

        static /* synthetic */ java.util.Set access$900(com.android.okhttp.Response.ParsedHeaders r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.okhttp.Response.ParsedHeaders.access$900(com.android.okhttp.Response$ParsedHeaders):java.util.Set
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.okhttp.Response.ParsedHeaders.access$900(com.android.okhttp.Response$ParsedHeaders):java.util.Set
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.okhttp.Response.ParsedHeaders.access$900(com.android.okhttp.Response$ParsedHeaders):java.util.Set");
        }
    }

    public interface Receiver {
        void onFailure(Failure failure);

        boolean onResponse(Response response) throws IOException;
    }

    private Response(Builder builder) {
        this.request = builder.request;
        this.statusLine = builder.statusLine;
        this.handshake = builder.handshake;
        this.headers = builder.headers.build();
        this.body = builder.body;
        this.networkResponse = builder.networkResponse;
        this.cacheResponse = builder.cacheResponse;
        this.priorResponse = builder.priorResponse;
    }

    public Request request() {
        return this.request;
    }

    public String statusLine() {
        return this.statusLine.getStatusLine();
    }

    public int code() {
        return this.statusLine.code();
    }

    public String statusMessage() {
        return this.statusLine.message();
    }

    public int httpMinorVersion() {
        return this.statusLine.httpMinorVersion();
    }

    public Handshake handshake() {
        return this.handshake;
    }

    public List<String> headers(String name) {
        return this.headers.values(name);
    }

    public String header(String name) {
        return header(name, null);
    }

    public String header(String name, String defaultValue) {
        String result = this.headers.get(name);
        return result != null ? result : defaultValue;
    }

    public Headers headers() {
        return this.headers;
    }

    public Body body() {
        return this.body;
    }

    public Builder newBuilder() {
        return new Builder();
    }

    public Response priorResponse() {
        return this.priorResponse;
    }

    public Response networkResponse() {
        return this.networkResponse;
    }

    public Response cacheResponse() {
        return this.cacheResponse;
    }

    public Set<String> getVaryFields() {
        return ParsedHeaders.access$900(parsedHeaders());
    }

    public boolean hasVaryAll() {
        return ParsedHeaders.access$900(parsedHeaders()).contains("*");
    }

    public boolean varyMatches(Headers varyHeaders, Request newRequest) {
        for (String field : ParsedHeaders.access$900(parsedHeaders())) {
            if (!Util.equal(varyHeaders.values(field), newRequest.headers(field))) {
                return false;
            }
        }
        return true;
    }

    public boolean validate(Response network) {
        if (network.code() == 304) {
            return true;
        }
        ParsedHeaders networkHeaders = network.parsedHeaders();
        if (parsedHeaders().lastModified == null || networkHeaders.lastModified == null || networkHeaders.lastModified.getTime() >= parsedHeaders().lastModified.getTime()) {
            return false;
        }
        return true;
    }

    private ParsedHeaders parsedHeaders() {
        ParsedHeaders result = this.parsedHeaders;
        if (result != null) {
            return result;
        }
        result = new ParsedHeaders(null);
        this.parsedHeaders = result;
        return result;
    }

    public CacheControl cacheControl() {
        CacheControl result = this.cacheControl;
        if (result != null) {
            return result;
        }
        result = CacheControl.parse(this.headers);
        this.cacheControl = result;
        return result;
    }
}
