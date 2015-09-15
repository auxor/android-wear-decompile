package java.lang;

public class ClassNotFoundException extends ReflectiveOperationException {
    private static final long serialVersionUID = 9176873029745254542L;
    private Throwable ex;

    public ClassNotFoundException() {
        super((Throwable) null);
    }

    public ClassNotFoundException(String detailMessage) {
        super(detailMessage, null);
    }

    public ClassNotFoundException(String detailMessage, Throwable exception) {
        super(detailMessage);
        this.ex = exception;
    }

    public Throwable getException() {
        return this.ex;
    }

    public Throwable getCause() {
        return this.ex;
    }
}
