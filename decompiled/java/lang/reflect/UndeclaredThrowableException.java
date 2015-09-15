package java.lang.reflect;

public class UndeclaredThrowableException extends RuntimeException {
    private static final long serialVersionUID = 330127114055056639L;
    private Throwable undeclaredThrowable;

    public UndeclaredThrowableException(Throwable exception) {
        this.undeclaredThrowable = exception;
        initCause(exception);
    }

    public UndeclaredThrowableException(Throwable exception, String detailMessage) {
        super(detailMessage);
        this.undeclaredThrowable = exception;
        initCause(exception);
    }

    public Throwable getUndeclaredThrowable() {
        return this.undeclaredThrowable;
    }

    public Throwable getCause() {
        return this.undeclaredThrowable;
    }
}
