package java.net;

import java.io.InterruptedIOException;

public class SocketTimeoutException extends InterruptedIOException {
    private static final long serialVersionUID = -8846654841826352300L;

    public SocketTimeoutException(String detailMessage) {
        super(detailMessage);
    }

    public SocketTimeoutException(Throwable cause) {
        super(null, cause);
    }

    public SocketTimeoutException(String detailMessage, Throwable cause) {
        super(detailMessage, cause);
    }
}
