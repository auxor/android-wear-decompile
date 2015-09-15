package com.android.okhttp.internal.http;

import com.android.okhttp.Request;
import com.android.okhttp.Response.Builder;
import com.android.okio.Sink;
import com.android.okio.Source;
import java.io.IOException;
import java.net.CacheRequest;

interface Transport {
    public static final int DISCARD_STREAM_TIMEOUT_MILLIS = 100;

    boolean canReuseConnection();

    Sink createRequestBody(Request request) throws IOException;

    void disconnect(HttpEngine httpEngine) throws IOException;

    void emptyTransferStream() throws IOException;

    void flushRequest() throws IOException;

    Source getTransferStream(CacheRequest cacheRequest) throws IOException;

    Builder readResponseHeaders() throws IOException;

    void releaseConnectionOnIdle() throws IOException;

    void writeRequestBody(RetryableSink retryableSink) throws IOException;

    void writeRequestHeaders(Request request) throws IOException;
}
