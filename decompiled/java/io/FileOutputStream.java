package java.io;

import android.system.OsConstants;
import dalvik.system.CloseGuard;
import java.nio.NioUtils;
import java.nio.channels.FileChannel;
import libcore.io.IoBridge;

public class FileOutputStream extends OutputStream {
    private FileChannel channel;
    private FileDescriptor fd;
    private final CloseGuard guard;
    private final int mode;
    private final boolean shouldClose;

    public FileOutputStream(File file) throws FileNotFoundException {
        this(file, false);
    }

    public FileOutputStream(File file, boolean append) throws FileNotFoundException {
        this.guard = CloseGuard.get();
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        this.mode = (append ? OsConstants.O_APPEND : OsConstants.O_TRUNC) | (OsConstants.O_CREAT | OsConstants.O_WRONLY);
        this.fd = IoBridge.open(file.getPath(), this.mode);
        this.shouldClose = true;
        this.guard.open("close");
    }

    public FileOutputStream(FileDescriptor fd) {
        this.guard = CloseGuard.get();
        if (fd == null) {
            throw new NullPointerException("fd == null");
        }
        this.fd = fd;
        this.shouldClose = false;
        this.mode = OsConstants.O_WRONLY;
        this.channel = NioUtils.newFileChannel(this, fd, this.mode);
    }

    public FileOutputStream(String path) throws FileNotFoundException {
        this(path, false);
    }

    public FileOutputStream(String path, boolean append) throws FileNotFoundException {
        this(new File(path), append);
    }

    public void close() throws IOException {
        this.guard.close();
        synchronized (this) {
            if (this.channel != null) {
                this.channel.close();
            }
            if (this.shouldClose) {
                IoBridge.closeAndSignalBlockedThreads(this.fd);
            } else {
                this.fd = new FileDescriptor();
            }
        }
    }

    protected void finalize() throws IOException {
        AssertionError assertionError;
        try {
            if (this.guard != null) {
                this.guard.warnIfOpen();
            }
            close();
            try {
                super.finalize();
            } catch (Object t) {
                assertionError = new AssertionError(t);
            }
        } catch (Object t2) {
            assertionError = new AssertionError(t2);
        }
    }

    public FileChannel getChannel() {
        FileChannel fileChannel;
        synchronized (this) {
            if (this.channel == null) {
                this.channel = NioUtils.newFileChannel(this, this.fd, this.mode);
            }
            fileChannel = this.channel;
        }
        return fileChannel;
    }

    public final FileDescriptor getFD() throws IOException {
        return this.fd;
    }

    public void write(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        IoBridge.write(this.fd, buffer, byteOffset, byteCount);
    }

    public void write(int oneByte) throws IOException {
        write(new byte[]{(byte) oneByte}, 0, 1);
    }
}
