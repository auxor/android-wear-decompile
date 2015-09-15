package java.io;

public class FileWriter extends OutputStreamWriter {
    public FileWriter(File file) throws IOException {
        super(new FileOutputStream(file));
    }

    public FileWriter(File file, boolean append) throws IOException {
        super(new FileOutputStream(file, append));
    }

    public FileWriter(FileDescriptor fd) {
        super(new FileOutputStream(fd));
    }

    public FileWriter(String filename) throws IOException {
        super(new FileOutputStream(new File(filename)));
    }

    public FileWriter(String filename, boolean append) throws IOException {
        super(new FileOutputStream(filename, append));
    }
}
