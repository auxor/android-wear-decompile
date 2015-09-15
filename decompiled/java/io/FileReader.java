package java.io;

public class FileReader extends InputStreamReader {
    public FileReader(File file) throws FileNotFoundException {
        super(new FileInputStream(file));
    }

    public FileReader(FileDescriptor fd) {
        super(new FileInputStream(fd));
    }

    public FileReader(String filename) throws FileNotFoundException {
        super(new FileInputStream(filename));
    }
}
