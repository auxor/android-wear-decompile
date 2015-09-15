package java.util;

public class StringTokenizer implements Enumeration<Object> {
    private String delimiters;
    private int position;
    private boolean returnDelimiters;
    private String string;

    public StringTokenizer(String string) {
        this(string, " \t\n\r\f", false);
    }

    public StringTokenizer(String string, String delimiters) {
        this(string, delimiters, false);
    }

    public StringTokenizer(String string, String delimiters, boolean returnDelimiters) {
        if (string == null) {
            throw new NullPointerException("string == null");
        }
        this.string = string;
        this.delimiters = delimiters;
        this.returnDelimiters = returnDelimiters;
        this.position = 0;
    }

    public int countTokens() {
        int count = 0;
        boolean inToken = false;
        int length = this.string.length();
        for (int i = this.position; i < length; i++) {
            if (this.delimiters.indexOf(this.string.charAt(i), 0) >= 0) {
                if (this.returnDelimiters) {
                    count++;
                }
                if (inToken) {
                    count++;
                    inToken = false;
                }
            } else {
                inToken = true;
            }
        }
        if (inToken) {
            return count + 1;
        }
        return count;
    }

    public boolean hasMoreElements() {
        return hasMoreTokens();
    }

    public boolean hasMoreTokens() {
        if (this.delimiters == null) {
            throw new NullPointerException("delimiters == null");
        }
        int length = this.string.length();
        if (this.position < length) {
            if (this.returnDelimiters) {
                return true;
            }
            for (int i = this.position; i < length; i++) {
                if (this.delimiters.indexOf(this.string.charAt(i), 0) == -1) {
                    return true;
                }
            }
        }
        return false;
    }

    public Object nextElement() {
        return nextToken();
    }

    public String nextToken() {
        if (this.delimiters == null) {
            throw new NullPointerException("delimiters == null");
        }
        int i = this.position;
        int length = this.string.length();
        if (i < length) {
            if (!this.returnDelimiters) {
                while (i < length && this.delimiters.indexOf(this.string.charAt(i), 0) >= 0) {
                    i++;
                }
                this.position = i;
                if (i < length) {
                    this.position++;
                    while (this.position < length) {
                        if (this.delimiters.indexOf(this.string.charAt(this.position), 0) >= 0) {
                            return this.string.substring(i, this.position);
                        }
                        this.position++;
                    }
                    return this.string.substring(i);
                }
            } else if (this.delimiters.indexOf(this.string.charAt(this.position), 0) >= 0) {
                String str = this.string;
                int i2 = this.position;
                this.position = i2 + 1;
                return String.valueOf(str.charAt(i2));
            } else {
                this.position++;
                while (this.position < length) {
                    if (this.delimiters.indexOf(this.string.charAt(this.position), 0) >= 0) {
                        return this.string.substring(i, this.position);
                    }
                    this.position++;
                }
                return this.string.substring(i);
            }
        }
        throw new NoSuchElementException();
    }

    public String nextToken(String delims) {
        this.delimiters = delims;
        return nextToken();
    }
}
