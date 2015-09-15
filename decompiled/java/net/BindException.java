package java.net;

public class BindException extends SocketException {
    private static final long serialVersionUID = -5945005768251722951L;

    public BindException(String detailMessage) {
        super(detailMessage);
    }

    public BindException(String detailMessage, Throwable cause) {
        super(detailMessage, cause);
    }
}
