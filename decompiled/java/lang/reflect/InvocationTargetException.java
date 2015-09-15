package java.lang.reflect;

public class InvocationTargetException extends ReflectiveOperationException {
    private static final long serialVersionUID = 4085088731926701167L;
    private Throwable target;

    protected InvocationTargetException() {
        super((Throwable) null);
    }

    public InvocationTargetException(Throwable exception) {
        super(null, exception);
        this.target = exception;
    }

    public InvocationTargetException(Throwable exception, String detailMessage) {
        super(detailMessage, exception);
        this.target = exception;
    }

    public Throwable getTargetException() {
        return this.target;
    }

    public Throwable getCause() {
        return this.target;
    }
}
