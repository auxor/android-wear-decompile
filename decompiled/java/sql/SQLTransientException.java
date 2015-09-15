package java.sql;

public class SQLTransientException extends SQLException {
    private static final long serialVersionUID = -9042733978262274539L;

    public SQLTransientException(String reason) {
        super(reason, null, 0);
    }

    public SQLTransientException(String reason, String sqlState) {
        super(reason, sqlState, 0);
    }

    public SQLTransientException(String reason, String sqlState, int vendorCode) {
        super(reason, sqlState, vendorCode);
    }

    public SQLTransientException(Throwable cause) {
        super(cause);
    }

    public SQLTransientException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public SQLTransientException(String reason, String sqlState, Throwable cause) {
        super(reason, sqlState, cause);
    }

    public SQLTransientException(String reason, String sqlState, int vendorCode, Throwable cause) {
        super(reason, sqlState, vendorCode, cause);
    }
}
