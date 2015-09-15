package java.net;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class ResponseCache {
    private static ResponseCache defaultResponseCache;

    public abstract CacheResponse get(URI uri, String str, Map<String, List<String>> map) throws IOException;

    public abstract CacheRequest put(URI uri, URLConnection uRLConnection) throws IOException;

    static {
        defaultResponseCache = null;
    }

    public static ResponseCache getDefault() {
        return defaultResponseCache;
    }

    public static void setDefault(ResponseCache responseCache) {
        defaultResponseCache = responseCache;
    }
}
