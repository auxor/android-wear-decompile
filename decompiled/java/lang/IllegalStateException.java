package java.lang;

public class IllegalStateException extends RuntimeException {
    private static final long serialVersionUID = -1848914673093119416L;

    public IllegalStateException(String detailMessage) {
        super(detailMessage);
    }

    public IllegalStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalStateException(Throwable cause) {
        super(cause == null ? null : cause.toString(), cause);
    }
}
