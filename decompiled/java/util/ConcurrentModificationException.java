package java.util;

public class ConcurrentModificationException extends RuntimeException {
    private static final long serialVersionUID = -3666751008965953603L;

    public ConcurrentModificationException(String detailMessage) {
        super(detailMessage);
    }

    public ConcurrentModificationException(String detailMessage, Throwable cause) {
        super(detailMessage, cause);
    }

    public ConcurrentModificationException(Throwable cause) {
        super(cause);
    }
}
