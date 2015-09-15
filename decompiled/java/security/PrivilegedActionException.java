package java.security;

public class PrivilegedActionException extends Exception {
    private static final long serialVersionUID = 4724086851538908602L;

    public PrivilegedActionException(Exception ex) {
        super((Throwable) ex);
    }

    public Exception getException() {
        return null;
    }
}
