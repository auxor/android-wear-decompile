package java.sql;

public class SQLNonTransientException extends SQLException {
    private static final long serialVersionUID = -9104382843534716547L;

    public SQLNonTransientException(String reason) {
        super(reason, null, 0);
    }

    public SQLNonTransientException(String reason, String sqlState) {
        super(reason, sqlState, 0);
    }

    public SQLNonTransientException(String reason, String sqlState, int vendorCode) {
        super(reason, sqlState, vendorCode);
    }

    public SQLNonTransientException(Throwable cause) {
        super(cause);
    }

    public SQLNonTransientException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public SQLNonTransientException(String reason, String sqlState, Throwable cause) {
        super(reason, sqlState, cause);
    }

    public SQLNonTransientException(String reason, String sqlState, int vendorCode, Throwable cause) {
        super(reason, sqlState, vendorCode, cause);
    }
}
