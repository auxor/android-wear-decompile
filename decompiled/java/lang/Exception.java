package java.lang;

public class Exception extends Throwable {
    private static final long serialVersionUID = -3387516993124229948L;

    public Exception(String detailMessage) {
        super(detailMessage);
    }

    public Exception(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public Exception(Throwable throwable) {
        super(throwable);
    }
}
