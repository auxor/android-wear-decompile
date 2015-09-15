package java.util;

public class IllformedLocaleException extends RuntimeException {
    private final int errorIndex;

    public IllformedLocaleException() {
        this(null, -1);
    }

    public IllformedLocaleException(String message) {
        this(message, -1);
    }

    public IllformedLocaleException(String message, int errorIndex) {
        super(message);
        this.errorIndex = errorIndex;
    }

    public int getErrorIndex() {
        return this.errorIndex;
    }
}
