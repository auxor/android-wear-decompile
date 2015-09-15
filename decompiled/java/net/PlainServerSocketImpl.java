package java.net;

import java.io.FileDescriptor;
import java.io.IOException;

public class PlainServerSocketImpl extends PlainSocketImpl {
    public PlainServerSocketImpl(FileDescriptor fd) {
        super(fd);
    }

    protected void create(boolean isStreaming) throws IOException {
        super.create(isStreaming);
        if (isStreaming) {
            setOption(4, Boolean.TRUE);
        }
    }
}
