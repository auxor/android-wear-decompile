package java.net;

public class URISyntaxException extends Exception {
    private static final long serialVersionUID = 2137979680897488891L;
    private int index;
    private String input;

    public URISyntaxException(String input, String reason, int index) {
        super(reason);
        if (input == null) {
            throw new NullPointerException("input == null");
        } else if (reason == null) {
            throw new NullPointerException("reason == null");
        } else if (index < -1) {
            throw new IllegalArgumentException("Bad index: " + index);
        } else {
            this.input = input;
            this.index = index;
        }
    }

    public URISyntaxException(String input, String reason) {
        super(reason);
        if (input == null) {
            throw new NullPointerException("input == null");
        } else if (reason == null) {
            throw new NullPointerException("reason == null");
        } else {
            this.input = input;
            this.index = -1;
        }
    }

    public int getIndex() {
        return this.index;
    }

    public String getReason() {
        return super.getMessage();
    }

    public String getInput() {
        return this.input;
    }

    public String getMessage() {
        String reason = super.getMessage();
        if (this.index != -1) {
            return reason + " at index " + this.index + ": " + this.input;
        }
        return reason + ": " + this.input;
    }
}
