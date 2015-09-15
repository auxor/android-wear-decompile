package java.net;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class CookieHandler {
    private static CookieHandler systemWideCookieHandler;

    public abstract Map<String, List<String>> get(URI uri, Map<String, List<String>> map) throws IOException;

    public abstract void put(URI uri, Map<String, List<String>> map) throws IOException;

    public static CookieHandler getDefault() {
        return systemWideCookieHandler;
    }

    public static void setDefault(CookieHandler cHandler) {
        systemWideCookieHandler = cHandler;
    }
}
