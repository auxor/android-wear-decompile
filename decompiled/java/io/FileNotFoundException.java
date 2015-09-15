package java.io;

public class FileNotFoundException extends IOException {
    private static final long serialVersionUID = -897856973823710492L;

    public FileNotFoundException(String detailMessage) {
        super(detailMessage);
    }
}
