package java.lang;

public class UnsupportedOperationException extends RuntimeException {
    private static final long serialVersionUID = -1242599979055084673L;

    public UnsupportedOperationException(String detailMessage) {
        super(detailMessage);
    }

    public UnsupportedOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedOperationException(Throwable cause) {
        super(cause == null ? null : cause.toString(), cause);
    }
}
