package java.lang;

public class RuntimeException extends Exception {
    private static final long serialVersionUID = -7034897190745766939L;

    public RuntimeException(String detailMessage) {
        super(detailMessage);
    }

    public RuntimeException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public RuntimeException(Throwable throwable) {
        super(throwable);
    }
}
