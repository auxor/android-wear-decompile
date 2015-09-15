package java.lang;

public class LinkageError extends Error {
    private static final long serialVersionUID = 3579600108157160122L;

    public LinkageError(String detailMessage) {
        super(detailMessage);
    }

    public LinkageError(String detailMessage, Throwable cause) {
        super(detailMessage, cause);
    }
}
