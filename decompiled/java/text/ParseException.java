package java.text;

public class ParseException extends Exception {
    private static final long serialVersionUID = 2703218443322787634L;
    private int errorOffset;

    public ParseException(String detailMessage, int location) {
        super(detailMessage + " (at offset " + location + ")");
        this.errorOffset = location;
    }

    public int getErrorOffset() {
        return this.errorOffset;
    }
}
