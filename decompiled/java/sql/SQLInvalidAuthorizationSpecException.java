package java.sql;

public class SQLInvalidAuthorizationSpecException extends SQLNonTransientException {
    private static final long serialVersionUID = -64105250450891498L;

    public SQLInvalidAuthorizationSpecException(String reason) {
        super(reason, null, 0);
    }

    public SQLInvalidAuthorizationSpecException(String reason, String sqlState) {
        super(reason, sqlState, 0);
    }

    public SQLInvalidAuthorizationSpecException(String reason, String sqlState, int vendorCode) {
        super(reason, sqlState, vendorCode);
    }

    public SQLInvalidAuthorizationSpecException(Throwable cause) {
        super(cause);
    }

    public SQLInvalidAuthorizationSpecException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public SQLInvalidAuthorizationSpecException(String reason, String sqlState, Throwable cause) {
        super(reason, sqlState, cause);
    }

    public SQLInvalidAuthorizationSpecException(String reason, String sqlState, int vendorCode, Throwable cause) {
        super(reason, sqlState, vendorCode, cause);
    }
}
