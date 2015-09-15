package com.android.okio;

import java.io.Closeable;
import java.io.IOException;

public interface Source extends Closeable {
    void close() throws IOException;

    Source deadline(Deadline deadline);

    long read(OkBuffer okBuffer, long j) throws IOException;
}
