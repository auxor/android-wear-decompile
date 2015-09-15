package com.android.okio;

import java.io.Closeable;
import java.io.IOException;

public interface Sink extends Closeable {
    void close() throws IOException;

    Sink deadline(Deadline deadline);

    void flush() throws IOException;

    void write(OkBuffer okBuffer, long j) throws IOException;
}
