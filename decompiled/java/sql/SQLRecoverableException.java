package java.sql;

public class SQLRecoverableException extends SQLException {
    private static final long serialVersionUID = -4144386502923131579L;

    public SQLRecoverableException(String reason) {
        super(reason, null, 0);
    }

    public SQLRecoverableException(String reason, String sqlState) {
        super(reason, sqlState, 0);
    }

    public SQLRecoverableException(String reason, String sqlState, int vendorCode) {
        super(reason, sqlState, vendorCode);
    }

    public SQLRecoverableException(Throwable cause) {
        super(cause);
    }

    public SQLRecoverableException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public SQLRecoverableException(String reason, String sqlState, Throwable cause) {
        super(reason, sqlState, cause);
    }

    public SQLRecoverableException(String reason, String sqlState, int vendorCode, Throwable cause) {
        super(reason, sqlState, vendorCode, cause);
    }
}
