package java.lang;

import java.io.Serializable;
import java.util.jar.Pack200.Unpacker;

public final class Boolean implements Serializable, Comparable<Boolean> {
    public static final Boolean FALSE;
    public static final Boolean TRUE;
    public static final Class<Boolean> TYPE;
    private static final long serialVersionUID = -3665804199014368530L;
    private final boolean value;

    static {
        TYPE = boolean[].class.getComponentType();
        TRUE = new Boolean(true);
        FALSE = new Boolean(false);
    }

    public Boolean(String string) {
        this(parseBoolean(string));
    }

    public Boolean(boolean value) {
        this.value = value;
    }

    public boolean booleanValue() {
        return this.value;
    }

    @FindBugsSuppressWarnings({"RC_REF_COMPARISON_BAD_PRACTICE_BOOLEAN"})
    public boolean equals(Object o) {
        return o == this || ((o instanceof Boolean) && ((Boolean) o).value == this.value);
    }

    public int compareTo(Boolean that) {
        return compare(this.value, that.value);
    }

    public static int compare(boolean lhs, boolean rhs) {
        if (lhs == rhs) {
            return 0;
        }
        return lhs ? 1 : -1;
    }

    public int hashCode() {
        return this.value ? 1231 : 1237;
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    public static boolean getBoolean(String string) {
        if (string == null || string.length() == 0) {
            return false;
        }
        return parseBoolean(System.getProperty(string));
    }

    public static boolean parseBoolean(String s) {
        return Unpacker.TRUE.equalsIgnoreCase(s);
    }

    public static String toString(boolean value) {
        return String.valueOf(value);
    }

    public static Boolean valueOf(String string) {
        return parseBoolean(string) ? TRUE : FALSE;
    }

    public static Boolean valueOf(boolean b) {
        return b ? TRUE : FALSE;
    }
}
