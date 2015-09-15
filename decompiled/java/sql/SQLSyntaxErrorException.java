package java.sql;

public class SQLSyntaxErrorException extends SQLNonTransientException {
    private static final long serialVersionUID = -1843832610477496053L;

    public SQLSyntaxErrorException(String reason) {
        super(reason, null, 0);
    }

    public SQLSyntaxErrorException(String reason, String sqlState) {
        super(reason, sqlState, 0);
    }

    public SQLSyntaxErrorException(String reason, String sqlState, int vendorCode) {
        super(reason, sqlState, vendorCode);
    }

    public SQLSyntaxErrorException(Throwable cause) {
        super(cause);
    }

    public SQLSyntaxErrorException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public SQLSyntaxErrorException(String reason, String sqlState, Throwable cause) {
        super(reason, sqlState, cause);
    }

    public SQLSyntaxErrorException(String reason, String sqlState, int vendorCode, Throwable cause) {
        super(reason, sqlState, vendorCode, cause);
    }
}
