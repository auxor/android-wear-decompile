package java.lang;

public class AssertionError extends Error {
    private static final long serialVersionUID = -5013299493970297370L;

    public AssertionError(String detailMessage, Throwable cause) {
        super(detailMessage, cause);
    }

    public AssertionError(Object detailMessage) {
        super(String.valueOf(detailMessage));
        if (detailMessage instanceof Throwable) {
            initCause((Throwable) detailMessage);
        }
    }

    public AssertionError(boolean detailMessage) {
        this(String.valueOf(detailMessage));
    }

    public AssertionError(char detailMessage) {
        this(String.valueOf(detailMessage));
    }

    public AssertionError(int detailMessage) {
        this(Integer.toString(detailMessage));
    }

    public AssertionError(long detailMessage) {
        this(Long.toString(detailMessage));
    }

    public AssertionError(float detailMessage) {
        this(Float.toString(detailMessage));
    }

    public AssertionError(double detailMessage) {
        this(Double.toString(detailMessage));
    }
}
