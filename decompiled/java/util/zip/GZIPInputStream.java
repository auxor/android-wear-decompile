package java.util.zip;

import com.android.dex.DexFormat;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.ByteOrder;
import java.util.Arrays;
import libcore.io.Memory;
import libcore.io.Streams;
import org.w3c.dom.traversal.NodeFilter;

public class GZIPInputStream extends InflaterInputStream {
    private static final int FCOMMENT = 16;
    private static final int FEXTRA = 4;
    private static final int FHCRC = 2;
    private static final int FNAME = 8;
    public static final int GZIP_MAGIC = 35615;
    private static final int GZIP_TRAILER_SIZE = 8;
    protected CRC32 crc;
    protected boolean eos;

    public GZIPInputStream(InputStream is) throws IOException {
        this(is, NodeFilter.SHOW_DOCUMENT_TYPE);
    }

    public GZIPInputStream(InputStream is, int size) throws IOException {
        super(is, new Inflater(true), size);
        this.crc = new CRC32();
        this.eos = false;
        try {
            byte[] header = readHeader(is);
            if (Memory.peekShort(header, 0, ByteOrder.LITTLE_ENDIAN) != (short) -29921) {
                throw new IOException(String.format("unknown format (magic number %x)", Short.valueOf(Memory.peekShort(header, 0, ByteOrder.LITTLE_ENDIAN))));
            } else {
                parseGzipHeader(is, header, this.crc, this.buf);
            }
        } catch (IOException e) {
            close();
            throw e;
        }
    }

    public void close() throws IOException {
        this.eos = true;
        super.close();
    }

    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        if (this.closed) {
            throw new IOException("Stream is closed");
        } else if (this.eos) {
            return -1;
        } else {
            Arrays.checkOffsetAndCount(buffer.length, byteOffset, byteCount);
            try {
                int bytesRead = super.read(buffer, byteOffset, byteCount);
                if (bytesRead != -1) {
                    this.crc.update(buffer, byteOffset, bytesRead);
                }
                if (!this.eos) {
                    return bytesRead;
                }
                verifyCrc();
                this.eos = maybeReadNextMember();
                if (this.eos) {
                    return bytesRead;
                }
                this.crc.reset();
                this.inf.reset();
                this.eof = false;
                this.len = 0;
                return bytesRead;
            } finally {
                this.eos = this.eof;
            }
        }
    }

    private boolean maybeReadNextMember() throws IOException {
        int remaining = this.inf.getRemaining() - 8;
        if (remaining > 0) {
            if (!(this.in instanceof PushbackInputStream)) {
                this.in = new PushbackInputStream(this.in, this.buf.length);
            }
            ((PushbackInputStream) this.in).unread(this.buf, this.inf.getCurrentOffset() + GZIP_TRAILER_SIZE, remaining);
        }
        try {
            byte[] buffer = readHeader(this.in);
            if (Memory.peekShort(buffer, 0, ByteOrder.LITTLE_ENDIAN) != (short) -29921) {
                return true;
            }
            parseGzipHeader(this.in, buffer, this.crc, this.buf);
            return false;
        } catch (EOFException e) {
            return true;
        }
    }

    private static byte[] readHeader(InputStream in) throws IOException {
        byte[] header = new byte[10];
        Streams.readFully(in, header, 0, header.length);
        return header;
    }

    private static void parseGzipHeader(InputStream in, byte[] header, CRC32 crc, byte[] scratch) throws IOException {
        boolean hcrc;
        byte flags = header[3];
        if ((flags & FHCRC) != 0) {
            hcrc = true;
        } else {
            hcrc = false;
        }
        if (hcrc) {
            crc.update(header, 0, header.length);
        }
        if ((flags & FEXTRA) != 0) {
            Streams.readFully(in, header, 0, FHCRC);
            if (hcrc) {
                crc.update(header, 0, FHCRC);
            }
            int length = Memory.peekShort(scratch, 0, ByteOrder.LITTLE_ENDIAN) & DexFormat.MAX_TYPE_IDX;
            while (length > 0) {
                int max;
                if (length > scratch.length) {
                    max = scratch.length;
                } else {
                    max = length;
                }
                int result = in.read(scratch, 0, max);
                if (result == -1) {
                    throw new EOFException();
                }
                if (hcrc) {
                    crc.update(scratch, 0, result);
                }
                length -= result;
            }
        }
        if ((flags & GZIP_TRAILER_SIZE) != 0) {
            readZeroTerminated(in, crc, hcrc);
        }
        if ((flags & FCOMMENT) != 0) {
            readZeroTerminated(in, crc, hcrc);
        }
        if (hcrc) {
            Streams.readFully(in, header, 0, FHCRC);
            if (((short) ((int) crc.getValue())) != Memory.peekShort(scratch, 0, ByteOrder.LITTLE_ENDIAN)) {
                throw new IOException("CRC mismatch");
            }
            crc.reset();
        }
    }

    private void verifyCrc() throws IOException {
        int copySize = GZIP_TRAILER_SIZE;
        int size = this.inf.getRemaining();
        byte[] b = new byte[GZIP_TRAILER_SIZE];
        if (size <= GZIP_TRAILER_SIZE) {
            copySize = size;
        }
        System.arraycopy(this.buf, this.len - size, b, 0, copySize);
        Streams.readFully(this.in, b, copySize, 8 - copySize);
        if (Memory.peekInt(b, 0, ByteOrder.LITTLE_ENDIAN) != ((int) this.crc.getValue())) {
            throw new IOException("CRC mismatch");
        } else if (Memory.peekInt(b, FEXTRA, ByteOrder.LITTLE_ENDIAN) != this.inf.getTotalOut()) {
            throw new IOException("Size mismatch");
        }
    }

    private static void readZeroTerminated(InputStream in, CRC32 crc, boolean hcrc) throws IOException {
        while (true) {
            int result = in.read();
            if (result <= 0) {
                break;
            } else if (hcrc) {
                crc.update(result);
            }
        }
        if (result == -1) {
            throw new EOFException();
        } else if (hcrc) {
            crc.update(result);
        }
    }
}
