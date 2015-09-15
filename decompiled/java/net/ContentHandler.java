package java.net;

import java.io.IOException;

public abstract class ContentHandler {
    public abstract Object getContent(URLConnection uRLConnection) throws IOException;

    public Object getContent(URLConnection uConn, Class[] types) throws IOException {
        Object content = getContent(uConn);
        for (Class isInstance : types) {
            if (isInstance.isInstance(content)) {
                return content;
            }
        }
        return null;
    }
}
