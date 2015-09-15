package libcore.io;

import dalvik.bytecode.Opcodes;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import libcore.icu.DateIntervalFormat;
import org.w3c.dom.traversal.NodeFilter;

public final class Streams {
    private static AtomicReference<byte[]> skipBuffer;

    static {
        skipBuffer = new AtomicReference();
    }

    private Streams() {
    }

    public static int readSingleByte(InputStream in) throws IOException {
        byte[] buffer = new byte[1];
        if (in.read(buffer, 0, 1) != -1) {
            return buffer[0] & Opcodes.OP_CONST_CLASS_JUMBO;
        }
        return -1;
    }

    public static void writeSingleByte(OutputStream out, int b) throws IOException {
        out.write(new byte[]{(byte) (b & Opcodes.OP_CONST_CLASS_JUMBO)});
    }

    public static void readFully(InputStream in, byte[] dst) throws IOException {
        readFully(in, dst, 0, dst.length);
    }

    public static void readFully(InputStream in, byte[] dst, int offset, int byteCount) throws IOException {
        if (byteCount != 0) {
            if (in == null) {
                throw new NullPointerException("in == null");
            } else if (dst == null) {
                throw new NullPointerException("dst == null");
            } else {
                Arrays.checkOffsetAndCount(dst.length, offset, byteCount);
                while (byteCount > 0) {
                    int bytesRead = in.read(dst, offset, byteCount);
                    if (bytesRead < 0) {
                        throw new EOFException();
                    }
                    offset += bytesRead;
                    byteCount -= bytesRead;
                }
            }
        }
    }

    public static byte[] readFully(InputStream in) throws IOException {
        try {
            byte[] readFullyNoClose = readFullyNoClose(in);
            return readFullyNoClose;
        } finally {
            in.close();
        }
    }

    public static byte[] readFullyNoClose(InputStream in) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        byte[] buffer = new byte[NodeFilter.SHOW_DOCUMENT_FRAGMENT];
        while (true) {
            int count = in.read(buffer);
            if (count == -1) {
                return bytes.toByteArray();
            }
            bytes.write(buffer, 0, count);
        }
    }

    public static String readFully(Reader reader) throws IOException {
        try {
            StringWriter writer = new StringWriter();
            char[] buffer = new char[NodeFilter.SHOW_DOCUMENT_FRAGMENT];
            while (true) {
                int count = reader.read(buffer);
                if (count == -1) {
                    break;
                }
                writer.write(buffer, 0, count);
            }
            String stringWriter = writer.toString();
            return stringWriter;
        } finally {
            reader.close();
        }
    }

    public static void skipAll(InputStream in) throws IOException {
        do {
            in.skip(Long.MAX_VALUE);
        } while (in.read() != -1);
    }

    public static long skipByReading(InputStream in, long byteCount) throws IOException {
        byte[] buffer = (byte[]) skipBuffer.getAndSet(null);
        if (buffer == null) {
            buffer = new byte[Modifier.SYNTHETIC];
        }
        long skipped = 0;
        while (skipped < byteCount) {
            int toRead = (int) Math.min(byteCount - skipped, (long) buffer.length);
            int read = in.read(buffer, 0, toRead);
            if (read != -1) {
                skipped += (long) read;
                if (read < toRead) {
                    break;
                }
            }
            break;
        }
        skipBuffer.set(buffer);
        return skipped;
    }

    public static int copy(InputStream in, OutputStream out) throws IOException {
        int total = 0;
        byte[] buffer = new byte[DateIntervalFormat.FORMAT_UTC];
        while (true) {
            int c = in.read(buffer);
            if (c == -1) {
                return total;
            }
            total += c;
            out.write(buffer, 0, c);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String readAsciiLine(java.io.InputStream r5) throws java.io.IOException {
        /*
        r2 = new java.lang.StringBuilder;
        r3 = 80;
        r2.<init>(r3);
    L_0x0007:
        r0 = r5.read();
        r3 = -1;
        if (r0 != r3) goto L_0x0014;
    L_0x000e:
        r3 = new java.io.EOFException;
        r3.<init>();
        throw r3;
    L_0x0014:
        r3 = 10;
        if (r0 != r3) goto L_0x0032;
    L_0x0018:
        r1 = r2.length();
        if (r1 <= 0) goto L_0x002d;
    L_0x001e:
        r3 = r1 + -1;
        r3 = r2.charAt(r3);
        r4 = 13;
        if (r3 != r4) goto L_0x002d;
    L_0x0028:
        r3 = r1 + -1;
        r2.setLength(r3);
    L_0x002d:
        r3 = r2.toString();
        return r3;
    L_0x0032:
        r3 = (char) r0;
        r2.append(r3);
        goto L_0x0007;
        */
        throw new UnsupportedOperationException("Method not decompiled: libcore.io.Streams.readAsciiLine(java.io.InputStream):java.lang.String");
    }
}
