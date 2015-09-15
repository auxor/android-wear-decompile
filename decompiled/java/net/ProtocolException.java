package java.net;

import java.io.IOException;

public class ProtocolException extends IOException {
    private static final long serialVersionUID = -6098449442062388080L;

    public ProtocolException(String detailMessage) {
        super(detailMessage);
    }

    public ProtocolException(String detailMessage, Throwable cause) {
        super(detailMessage, cause);
    }
}
