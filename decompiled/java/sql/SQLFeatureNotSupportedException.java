package java.sql;

public class SQLFeatureNotSupportedException extends SQLNonTransientException {
    private static final long serialVersionUID = -1026510870282316051L;

    public SQLFeatureNotSupportedException(String reason) {
        super(reason, null, 0);
    }

    public SQLFeatureNotSupportedException(String reason, String sqlState) {
        super(reason, sqlState, 0);
    }

    public SQLFeatureNotSupportedException(String reason, String sqlState, int vendorCode) {
        super(reason, sqlState, vendorCode);
    }

    public SQLFeatureNotSupportedException(Throwable cause) {
        super(cause);
    }

    public SQLFeatureNotSupportedException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public SQLFeatureNotSupportedException(String reason, String sqlState, Throwable cause) {
        super(reason, sqlState, cause);
    }

    public SQLFeatureNotSupportedException(String reason, String sqlState, int vendorCode, Throwable cause) {
        super(reason, sqlState, vendorCode, cause);
    }
}
