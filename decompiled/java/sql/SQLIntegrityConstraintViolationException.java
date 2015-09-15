package java.sql;

public class SQLIntegrityConstraintViolationException extends SQLNonTransientException {
    private static final long serialVersionUID = 8033405298774849169L;

    public SQLIntegrityConstraintViolationException(String reason) {
        super(reason, null, 0);
    }

    public SQLIntegrityConstraintViolationException(String reason, String sqlState) {
        super(reason, sqlState, 0);
    }

    public SQLIntegrityConstraintViolationException(String reason, String sqlState, int vendorCode) {
        super(reason, sqlState, vendorCode);
    }

    public SQLIntegrityConstraintViolationException(Throwable cause) {
        super(cause);
    }

    public SQLIntegrityConstraintViolationException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public SQLIntegrityConstraintViolationException(String reason, String sqlState, Throwable cause) {
        super(reason, sqlState, cause);
    }

    public SQLIntegrityConstraintViolationException(String reason, String sqlState, int vendorCode, Throwable cause) {
        super(reason, sqlState, vendorCode, cause);
    }
}
