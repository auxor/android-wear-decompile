package java.sql;

public class SQLNonTransientConnectionException extends SQLNonTransientException {
    private static final long serialVersionUID = -5852318857474782892L;

    public SQLNonTransientConnectionException(String reason) {
        super(reason, null, 0);
    }

    public SQLNonTransientConnectionException(String reason, String sqlState) {
        super(reason, sqlState, 0);
    }

    public SQLNonTransientConnectionException(String reason, String sqlState, int vendorCode) {
        super(reason, sqlState, vendorCode);
    }

    public SQLNonTransientConnectionException(Throwable cause) {
        super(cause);
    }

    public SQLNonTransientConnectionException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public SQLNonTransientConnectionException(String reason, String sqlState, Throwable cause) {
        super(reason, sqlState, cause);
    }

    public SQLNonTransientConnectionException(String reason, String sqlState, int vendorCode, Throwable cause) {
        super(reason, sqlState, vendorCode, cause);
    }
}
