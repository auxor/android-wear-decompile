package java.sql;

public class SQLTransientConnectionException extends SQLTransientException {
    private static final long serialVersionUID = -2520155553543391200L;

    public SQLTransientConnectionException(String reason) {
        super(reason, null, 0);
    }

    public SQLTransientConnectionException(String reason, String sqlState) {
        super(reason, sqlState, 0);
    }

    public SQLTransientConnectionException(String reason, String sqlState, int vendorCode) {
        super(reason, sqlState, vendorCode);
    }

    public SQLTransientConnectionException(Throwable cause) {
        super(cause);
    }

    public SQLTransientConnectionException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public SQLTransientConnectionException(String reason, String sqlState, Throwable cause) {
        super(reason, sqlState, cause);
    }

    public SQLTransientConnectionException(String reason, String sqlState, int vendorCode, Throwable cause) {
        super(reason, sqlState, vendorCode, cause);
    }
}
