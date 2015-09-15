package java.sql;

public class SQLDataException extends SQLNonTransientException {
    private static final long serialVersionUID = -6889123282670549800L;

    public SQLDataException(String reason) {
        super(reason, null, 0);
    }

    public SQLDataException(String reason, String sqlState) {
        super(reason, sqlState, 0);
    }

    public SQLDataException(String reason, String sqlState, int vendorCode) {
        super(reason, sqlState, vendorCode);
    }

    public SQLDataException(Throwable cause) {
        super(cause);
    }

    public SQLDataException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public SQLDataException(String reason, String sqlState, Throwable cause) {
        super(reason, sqlState, cause);
    }

    public SQLDataException(String reason, String sqlState, int vendorCode, Throwable cause) {
        super(reason, sqlState, vendorCode, cause);
    }
}
