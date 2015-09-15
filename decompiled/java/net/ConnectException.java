package java.net;

public class ConnectException extends SocketException {
    private static final long serialVersionUID = 3831404271622369215L;

    public ConnectException(String detailMessage) {
        super(detailMessage);
    }

    public ConnectException(String detailMessage, Throwable cause) {
        super(detailMessage, cause);
    }
}
