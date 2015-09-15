package com.android.okhttp;

import java.io.IOException;
import java.net.CacheRequest;

public interface OkResponseCache {
    Response get(Request request) throws IOException;

    boolean maybeRemove(Request request) throws IOException;

    CacheRequest put(Response response) throws IOException;

    void trackConditionalCacheHit();

    void trackResponse(ResponseSource responseSource);

    void update(Response response, Response response2) throws IOException;
}
