package java.io;

public class IOException extends Exception {
    private static final long serialVersionUID = 7818375828146090155L;

    public IOException(String detailMessage) {
        super(detailMessage);
    }

    public IOException(String message, Throwable cause) {
        super(message, cause);
    }

    public IOException(Throwable cause) {
        super(cause == null ? null : cause.toString(), cause);
    }
}
