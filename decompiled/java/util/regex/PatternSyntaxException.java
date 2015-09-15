package java.util.regex;

import java.util.Arrays;

public class PatternSyntaxException extends IllegalArgumentException {
    private static final long serialVersionUID = -3864639126226059218L;
    private String desc;
    private int index;
    private String pattern;

    public PatternSyntaxException(String description, String pattern, int index) {
        this.index = -1;
        this.desc = description;
        this.pattern = pattern;
        this.index = index;
    }

    public String getPattern() {
        return this.pattern;
    }

    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        if (this.desc != null) {
            sb.append(this.desc);
        }
        if (this.index >= 0) {
            if (this.desc != null) {
                sb.append(' ');
            }
            sb.append("near index ");
            sb.append(this.index);
            sb.append(':');
        }
        if (this.pattern != null) {
            sb.append('\n');
            sb.append(this.pattern);
            if (this.index >= 0) {
                char[] spaces = new char[this.index];
                Arrays.fill(spaces, ' ');
                sb.append('\n');
                sb.append(spaces);
                sb.append('^');
            }
        }
        return sb.toString();
    }

    public String getDescription() {
        return this.desc;
    }

    public int getIndex() {
        return this.index;
    }
}
