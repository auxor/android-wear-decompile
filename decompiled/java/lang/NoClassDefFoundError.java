package java.lang;

public class NoClassDefFoundError extends LinkageError {
    private static final long serialVersionUID = 9095859863287012458L;

    public NoClassDefFoundError(String detailMessage) {
        super(detailMessage);
    }
}
