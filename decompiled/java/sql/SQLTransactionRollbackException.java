package java.sql;

public class SQLTransactionRollbackException extends SQLTransientException {
    private static final long serialVersionUID = 5246680841170837229L;

    public SQLTransactionRollbackException(String reason) {
        super(reason, null, 0);
    }

    public SQLTransactionRollbackException(String reason, String sqlState) {
        super(reason, sqlState, 0);
    }

    public SQLTransactionRollbackException(String reason, String sqlState, int vendorCode) {
        super(reason, sqlState, vendorCode);
    }

    public SQLTransactionRollbackException(Throwable cause) {
        super(cause);
    }

    public SQLTransactionRollbackException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public SQLTransactionRollbackException(String reason, String sqlState, Throwable cause) {
        super(reason, sqlState, cause);
    }

    public SQLTransactionRollbackException(String reason, String sqlState, int vendorCode, Throwable cause) {
        super(reason, sqlState, vendorCode, cause);
    }
}
