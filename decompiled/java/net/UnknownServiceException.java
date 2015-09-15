package java.net;

import java.io.IOException;

public class UnknownServiceException extends IOException {
    private static final long serialVersionUID = -4169033248853639508L;

    public UnknownServiceException(String detailMessage) {
        super(detailMessage);
    }

    public UnknownServiceException(String detailMessage, Throwable cause) {
        super(detailMessage, cause);
    }
}
