package android.media;

import java.io.Closeable;

public interface DataSource extends Closeable {
    long getSize();

    int readAt(long j, byte[] bArr, int i);
}
