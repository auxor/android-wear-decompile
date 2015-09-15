package com.android.okio;

import java.io.IOException;
import java.io.OutputStream;

public interface BufferedSink extends Sink {
    OkBuffer buffer();

    BufferedSink emitCompleteSegments() throws IOException;

    OutputStream outputStream();

    BufferedSink write(ByteString byteString) throws IOException;

    BufferedSink write(byte[] bArr) throws IOException;

    BufferedSink write(byte[] bArr, int i, int i2) throws IOException;

    BufferedSink writeByte(int i) throws IOException;

    BufferedSink writeInt(int i) throws IOException;

    BufferedSink writeIntLe(int i) throws IOException;

    BufferedSink writeLong(long j) throws IOException;

    BufferedSink writeLongLe(long j) throws IOException;

    BufferedSink writeShort(int i) throws IOException;

    BufferedSink writeShortLe(int i) throws IOException;

    BufferedSink writeUtf8(String str) throws IOException;
}
