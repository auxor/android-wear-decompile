package java.sql;

import java.io.Serializable;

public class SQLWarning extends SQLException implements Serializable {
    private static final long serialVersionUID = 3917336774604784856L;

    public SQLWarning(String theReason) {
        super(theReason);
    }

    public SQLWarning(String theReason, String theSQLState) {
        super(theReason, theSQLState);
    }

    public SQLWarning(String theReason, String theSQLState, int theErrorCode) {
        super(theReason, theSQLState, theErrorCode);
    }

    public SQLWarning(Throwable cause) {
        super(cause);
    }

    public SQLWarning(String reason, Throwable cause) {
        super(reason, cause);
    }

    public SQLWarning(String reason, String SQLState, Throwable cause) {
        super(reason, SQLState, cause);
    }

    public SQLWarning(String reason, String SQLState, int vendorCode, Throwable cause) {
        super(reason, SQLState, vendorCode, cause);
    }

    public SQLWarning getNextWarning() {
        SQLException next = super.getNextException();
        if (next == null) {
            return null;
        }
        if (next instanceof SQLWarning) {
            return next;
        }
        throw new Error("SQLWarning chain holds value that is not a SQLWarning");
    }

    public void setNextWarning(SQLWarning w) {
        super.setNextException(w);
    }
}
