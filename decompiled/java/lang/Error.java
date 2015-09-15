package java.lang;

public class Error extends Throwable {
    private static final long serialVersionUID = 4980196508277280342L;

    public Error(String detailMessage) {
        super(detailMessage);
    }

    public Error(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public Error(Throwable throwable) {
        super(throwable);
    }
}
