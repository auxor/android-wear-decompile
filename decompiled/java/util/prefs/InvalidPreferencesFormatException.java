package java.util.prefs;

public class InvalidPreferencesFormatException extends Exception {
    private static final long serialVersionUID = -791715184232119669L;

    public InvalidPreferencesFormatException(String s) {
        super(s);
    }

    public InvalidPreferencesFormatException(String s, Throwable t) {
        super(s, t);
    }

    public InvalidPreferencesFormatException(Throwable t) {
        super(t);
    }
}
