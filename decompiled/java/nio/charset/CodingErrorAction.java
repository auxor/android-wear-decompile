package java.nio.charset;

public class CodingErrorAction {
    public static final CodingErrorAction IGNORE;
    public static final CodingErrorAction REPLACE;
    public static final CodingErrorAction REPORT;
    private String action;

    static {
        IGNORE = new CodingErrorAction("IGNORE");
        REPLACE = new CodingErrorAction("REPLACE");
        REPORT = new CodingErrorAction("REPORT");
    }

    private CodingErrorAction(String action) {
        this.action = action;
    }

    public String toString() {
        return "Action: " + this.action;
    }
}
