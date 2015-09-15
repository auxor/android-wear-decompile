package java.util;

public class ServiceConfigurationError extends Error {
    private static final long serialVersionUID = 74132770414881L;

    public ServiceConfigurationError(String message) {
        super(message);
    }

    public ServiceConfigurationError(String message, Throwable cause) {
        super(message, cause);
    }
}
