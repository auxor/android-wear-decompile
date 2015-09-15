package java.sql;

public class SQLTimeoutException extends SQLTransientException {
    private static final long serialVersionUID = -4487171280562520262L;

    public SQLTimeoutException(String reason) {
        super(reason, null, 0);
    }

    public SQLTimeoutException(String reason, String sqlState) {
        super(reason, sqlState, 0);
    }

    public SQLTimeoutException(String reason, String sqlState, int vendorCode) {
        super(reason, sqlState, vendorCode);
    }

    public SQLTimeoutException(Throwable cause) {
        super(cause);
    }

    public SQLTimeoutException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public SQLTimeoutException(String reason, String sqlState, Throwable cause) {
        super(reason, sqlState, cause);
    }

    public SQLTimeoutException(String reason, String sqlState, int vendorCode, Throwable cause) {
        super(reason, sqlState, vendorCode, cause);
    }
}
