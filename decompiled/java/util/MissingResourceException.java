package java.util;

public class MissingResourceException extends RuntimeException {
    private static final long serialVersionUID = -4876345176062000401L;
    String className;
    String key;

    public MissingResourceException(String detailMessage, String className, String resourceName) {
        super(detailMessage);
        this.className = className;
        this.key = resourceName;
    }

    public String getClassName() {
        return this.className;
    }

    public String getKey() {
        return this.key;
    }
}
