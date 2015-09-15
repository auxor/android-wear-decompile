package com.android.okio;

import java.io.IOException;
import java.io.InputStream;

public interface BufferedSource extends Source {
    OkBuffer buffer();

    boolean exhausted() throws IOException;

    long indexOf(byte b) throws IOException;

    InputStream inputStream();

    byte readByte() throws IOException;

    ByteString readByteString(long j) throws IOException;

    int readInt() throws IOException;

    int readIntLe() throws IOException;

    long readLong() throws IOException;

    long readLongLe() throws IOException;

    short readShort() throws IOException;

    short readShortLe() throws IOException;

    String readUtf8(long j) throws IOException;

    String readUtf8Line() throws IOException;

    String readUtf8LineStrict() throws IOException;

    void require(long j) throws IOException;

    void skip(long j) throws IOException;
}
