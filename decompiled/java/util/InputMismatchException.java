package java.util;

import java.io.Serializable;

public class InputMismatchException extends NoSuchElementException implements Serializable {
    private static final long serialVersionUID = 8811230760997066428L;

    public InputMismatchException(String msg) {
        super(msg);
    }
}
