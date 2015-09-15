package com.android.okhttp.internal.http;

import com.android.okhttp.CacheControl;
import com.android.okhttp.MediaType;
import com.android.okhttp.Request;
import com.android.okhttp.Response;
import com.android.okhttp.Response.Body;
import com.android.okhttp.Response.Builder;
import com.android.okhttp.ResponseSource;
import com.android.okhttp.internal.Util;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class CacheStrategy {
    private static final Body EMPTY_BODY;
    private static final StatusLine GATEWAY_TIMEOUT_STATUS_LINE;
    public final Response cacheResponse;
    public final Request networkRequest;
    public final ResponseSource source;

    public static class Factory {
        private int ageSeconds;
        final Response cacheResponse;
        private String etag;
        private Date expires;
        private Date lastModified;
        private String lastModifiedString;
        final long nowMillis;
        private long receivedResponseMillis;
        final Request request;
        private long sentRequestMillis;
        private Date servedDate;
        private String servedDateString;

        public Factory(long nowMillis, Request request, Response cacheResponse) {
            this.ageSeconds = -1;
            this.nowMillis = nowMillis;
            this.request = request;
            this.cacheResponse = cacheResponse;
            if (cacheResponse != null) {
                for (int i = 0; i < cacheResponse.headers().size(); i++) {
                    String fieldName = cacheResponse.headers().name(i);
                    String value = cacheResponse.headers().value(i);
                    if ("Date".equalsIgnoreCase(fieldName)) {
                        this.servedDate = HttpDate.parse(value);
                        this.servedDateString = value;
                    } else if ("Expires".equalsIgnoreCase(fieldName)) {
                        this.expires = HttpDate.parse(value);
                    } else if ("Last-Modified".equalsIgnoreCase(fieldName)) {
                        this.lastModified = HttpDate.parse(value);
                        this.lastModifiedString = value;
                    } else if ("ETag".equalsIgnoreCase(fieldName)) {
                        this.etag = value;
                    } else if ("Age".equalsIgnoreCase(fieldName)) {
                        this.ageSeconds = HeaderParser.parseSeconds(value);
                    } else if (OkHeaders.SENT_MILLIS.equalsIgnoreCase(fieldName)) {
                        this.sentRequestMillis = Long.parseLong(value);
                    } else if (OkHeaders.RECEIVED_MILLIS.equalsIgnoreCase(fieldName)) {
                        this.receivedResponseMillis = Long.parseLong(value);
                    }
                }
            }
        }

        public CacheStrategy get() {
            CacheStrategy candidate = getCandidate();
            if (candidate.source == ResponseSource.CACHE || !this.request.cacheControl().onlyIfCached()) {
                return candidate;
            }
            return new CacheStrategy(new Builder().request(candidate.networkRequest).statusLine(CacheStrategy.GATEWAY_TIMEOUT_STATUS_LINE).setResponseSource(ResponseSource.NONE).body(CacheStrategy.EMPTY_BODY).build(), ResponseSource.NONE, null);
        }

        private CacheStrategy getCandidate() {
            if (this.cacheResponse == null) {
                return new CacheStrategy(null, ResponseSource.NETWORK, null);
            }
            if (this.request.isHttps()) {
                if (this.cacheResponse.handshake() == null) {
                    return new CacheStrategy(null, ResponseSource.NETWORK, null);
                }
            }
            if (CacheStrategy.isCacheable(this.cacheResponse, this.request)) {
                CacheControl requestCaching = this.request.cacheControl();
                if (!requestCaching.noCache()) {
                    if (!hasConditions(this.request)) {
                        long ageMillis = cacheResponseAge();
                        long freshMillis = computeFreshnessLifetime();
                        if (requestCaching.maxAgeSeconds() != -1) {
                            freshMillis = Math.min(freshMillis, TimeUnit.SECONDS.toMillis((long) requestCaching.maxAgeSeconds()));
                        }
                        long minFreshMillis = 0;
                        if (requestCaching.minFreshSeconds() != -1) {
                            minFreshMillis = TimeUnit.SECONDS.toMillis((long) requestCaching.minFreshSeconds());
                        }
                        long maxStaleMillis = 0;
                        CacheControl responseCaching = this.cacheResponse.cacheControl();
                        if (!(responseCaching.mustRevalidate() || requestCaching.maxStaleSeconds() == -1)) {
                            maxStaleMillis = TimeUnit.SECONDS.toMillis((long) requestCaching.maxStaleSeconds());
                        }
                        if (responseCaching.noCache() || ageMillis + minFreshMillis >= freshMillis + maxStaleMillis) {
                            String str;
                            Request.Builder conditionalRequestBuilder = this.request.newBuilder();
                            if (this.lastModified != null) {
                                str = "If-Modified-Since";
                                conditionalRequestBuilder.header(r19, this.lastModifiedString);
                            } else if (this.servedDate != null) {
                                str = "If-Modified-Since";
                                conditionalRequestBuilder.header(r19, this.servedDateString);
                            }
                            if (this.etag != null) {
                                str = "If-None-Match";
                                conditionalRequestBuilder.header(r19, this.etag);
                            }
                            Request conditionalRequest = conditionalRequestBuilder.build();
                            if (hasConditions(conditionalRequest)) {
                                return new CacheStrategy(this.cacheResponse, ResponseSource.CONDITIONAL_CACHE, null);
                            }
                            return new CacheStrategy(null, ResponseSource.NETWORK, null);
                        }
                        Builder builder = this.cacheResponse.newBuilder().setResponseSource(ResponseSource.CACHE);
                        if (ageMillis + minFreshMillis >= freshMillis) {
                            builder.addHeader("Warning", "110 HttpURLConnection \"Response is stale\"");
                        }
                        if (ageMillis > 86400000 && isFreshnessLifetimeHeuristic()) {
                            builder.addHeader("Warning", "113 HttpURLConnection \"Heuristic expiration\"");
                        }
                        return new CacheStrategy(builder.build(), ResponseSource.CACHE, null);
                    }
                }
                return new CacheStrategy(this.cacheResponse, ResponseSource.NETWORK, null);
            }
            return new CacheStrategy(null, ResponseSource.NETWORK, null);
        }

        private long computeFreshnessLifetime() {
            CacheControl responseCaching = this.cacheResponse.cacheControl();
            if (responseCaching.maxAgeSeconds() != -1) {
                return TimeUnit.SECONDS.toMillis((long) responseCaching.maxAgeSeconds());
            }
            long delta;
            if (this.expires != null) {
                delta = this.expires.getTime() - (this.servedDate != null ? this.servedDate.getTime() : this.receivedResponseMillis);
                if (delta <= 0) {
                    delta = 0;
                }
                return delta;
            } else if (this.lastModified == null || this.cacheResponse.request().url().getQuery() != null) {
                return 0;
            } else {
                delta = (this.servedDate != null ? this.servedDate.getTime() : this.sentRequestMillis) - this.lastModified.getTime();
                if (delta > 0) {
                    return delta / 10;
                }
                return 0;
            }
        }

        private long cacheResponseAge() {
            long receivedAge;
            long apparentReceivedAge = 0;
            if (this.servedDate != null) {
                apparentReceivedAge = Math.max(0, this.receivedResponseMillis - this.servedDate.getTime());
            }
            if (this.ageSeconds != -1) {
                receivedAge = Math.max(apparentReceivedAge, TimeUnit.SECONDS.toMillis((long) this.ageSeconds));
            } else {
                receivedAge = apparentReceivedAge;
            }
            return (receivedAge + (this.receivedResponseMillis - this.sentRequestMillis)) + (this.nowMillis - this.receivedResponseMillis);
        }

        private boolean isFreshnessLifetimeHeuristic() {
            return this.cacheResponse.cacheControl().maxAgeSeconds() == -1 && this.expires == null;
        }

        private static boolean hasConditions(Request request) {
            return (request.header("If-Modified-Since") == null && request.header("If-None-Match") == null) ? false : true;
        }
    }

    static {
        EMPTY_BODY = new Body() {
            public boolean ready() throws IOException {
                return true;
            }

            public MediaType contentType() {
                return null;
            }

            public long contentLength() {
                return 0;
            }

            public InputStream byteStream() {
                return Util.EMPTY_INPUT_STREAM;
            }
        };
        try {
            GATEWAY_TIMEOUT_STATUS_LINE = new StatusLine("HTTP/1.1 504 Gateway Timeout");
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    private CacheStrategy(Request networkRequest, Response cacheResponse, ResponseSource source) {
        this.networkRequest = networkRequest;
        this.cacheResponse = cacheResponse;
        this.source = source;
    }

    public static boolean isCacheable(Response response, Request request) {
        int responseCode = response.code();
        if (responseCode != 200 && responseCode != 203 && responseCode != 300 && responseCode != 301 && responseCode != 410) {
            return false;
        }
        CacheControl responseCaching = response.cacheControl();
        if ((request.header("Authorization") == null || responseCaching.isPublic() || responseCaching.mustRevalidate() || responseCaching.sMaxAgeSeconds() != -1) && !responseCaching.noStore()) {
            return true;
        }
        return false;
    }
}
