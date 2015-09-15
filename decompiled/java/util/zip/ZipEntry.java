package java.util.zip;

import com.android.dex.DexFormat;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import libcore.io.BufferIterator;
import libcore.io.HeapBufferIterator;
import libcore.io.Streams;
import org.w3c.dom.traversal.NodeFilter;

public class ZipEntry implements ZipConstants, Cloneable {
    public static final int DEFLATED = 8;
    public static final int STORED = 0;
    String comment;
    long compressedSize;
    int compressionMethod;
    long crc;
    long dataOffset;
    byte[] extra;
    long localHeaderRelOffset;
    int modDate;
    String name;
    int nameLength;
    long size;
    int time;

    ZipEntry(String name, String comment, long crc, long compressedSize, long size, int compressionMethod, int time, int modDate, byte[] extra, int nameLength, long localHeaderRelOffset, long dataOffset) {
        this.crc = -1;
        this.compressedSize = -1;
        this.size = -1;
        this.compressionMethod = -1;
        this.time = -1;
        this.modDate = -1;
        this.nameLength = -1;
        this.localHeaderRelOffset = -1;
        this.dataOffset = -1;
        this.name = name;
        this.comment = comment;
        this.crc = crc;
        this.compressedSize = compressedSize;
        this.size = size;
        this.compressionMethod = compressionMethod;
        this.time = time;
        this.modDate = modDate;
        this.extra = extra;
        this.nameLength = nameLength;
        this.localHeaderRelOffset = localHeaderRelOffset;
        this.dataOffset = dataOffset;
    }

    public ZipEntry(String name) {
        this.crc = -1;
        this.compressedSize = -1;
        this.size = -1;
        this.compressionMethod = -1;
        this.time = -1;
        this.modDate = -1;
        this.nameLength = -1;
        this.localHeaderRelOffset = -1;
        this.dataOffset = -1;
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        validateStringLength("Name", name);
        this.name = name;
    }

    public String getComment() {
        return this.comment;
    }

    public long getCompressedSize() {
        return this.compressedSize;
    }

    public long getCrc() {
        return this.crc;
    }

    public byte[] getExtra() {
        return this.extra;
    }

    public int getMethod() {
        return this.compressionMethod;
    }

    public String getName() {
        return this.name;
    }

    public long getSize() {
        return this.size;
    }

    public long getTime() {
        if (this.time == -1) {
            return -1;
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(14, 0);
        cal.set(((this.modDate >> 9) & Float.MAX_EXPONENT) + 1980, ((this.modDate >> 5) & 15) - 1, this.modDate & 31, (this.time >> 11) & 31, (this.time >> 5) & 63, (this.time & 31) << 1);
        return cal.getTime().getTime();
    }

    public boolean isDirectory() {
        return this.name.charAt(this.name.length() + -1) == '/';
    }

    public void setComment(String comment) {
        if (comment == null) {
            this.comment = null;
            return;
        }
        validateStringLength("Comment", comment);
        this.comment = comment;
    }

    public void setCompressedSize(long value) {
        this.compressedSize = value;
    }

    public void setCrc(long value) {
        if (value < 0 || value > 4294967295L) {
            throw new IllegalArgumentException("Bad CRC32: " + value);
        }
        this.crc = value;
    }

    public void setExtra(byte[] data) {
        if (data == null || data.length <= DexFormat.MAX_TYPE_IDX) {
            this.extra = data;
            return;
        }
        throw new IllegalArgumentException("Extra data too long: " + data.length);
    }

    public void setMethod(int value) {
        if (value == 0 || value == DEFLATED) {
            this.compressionMethod = value;
            return;
        }
        throw new IllegalArgumentException("Bad method: " + value);
    }

    public void setSize(long value) {
        if (value < 0 || value > 4294967295L) {
            throw new IllegalArgumentException("Bad size: " + value);
        }
        this.size = value;
    }

    public void setTime(long value) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date(value));
        if (cal.get(1) < 1980) {
            this.modDate = 33;
            this.time = 0;
            return;
        }
        this.modDate = cal.get(5);
        this.modDate = ((cal.get(2) + 1) << 5) | this.modDate;
        this.modDate = ((cal.get(1) - 1980) << 9) | this.modDate;
        this.time = cal.get(13) >> 1;
        this.time = (cal.get(12) << 5) | this.time;
        this.time = (cal.get(11) << 11) | this.time;
    }

    public void setDataOffset(long value) {
        this.dataOffset = value;
    }

    public long getDataOffset() {
        return this.dataOffset;
    }

    public String toString() {
        return this.name;
    }

    public ZipEntry(ZipEntry ze) {
        this.crc = -1;
        this.compressedSize = -1;
        this.size = -1;
        this.compressionMethod = -1;
        this.time = -1;
        this.modDate = -1;
        this.nameLength = -1;
        this.localHeaderRelOffset = -1;
        this.dataOffset = -1;
        this.name = ze.name;
        this.comment = ze.comment;
        this.time = ze.time;
        this.size = ze.size;
        this.compressedSize = ze.compressedSize;
        this.crc = ze.crc;
        this.compressionMethod = ze.compressionMethod;
        this.modDate = ze.modDate;
        this.extra = ze.extra;
        this.nameLength = ze.nameLength;
        this.localHeaderRelOffset = ze.localHeaderRelOffset;
        this.dataOffset = ze.dataOffset;
    }

    public Object clone() {
        try {
            ZipEntry result = (ZipEntry) super.clone();
            result.extra = this.extra != null ? (byte[]) this.extra.clone() : null;
            return result;
        } catch (Object e) {
            throw new AssertionError(e);
        }
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    ZipEntry(byte[] cdeHdrBuf, InputStream cdStream, Charset defaultCharset) throws IOException {
        this.crc = -1;
        this.compressedSize = -1;
        this.size = -1;
        this.compressionMethod = -1;
        this.time = -1;
        this.modDate = -1;
        this.nameLength = -1;
        this.localHeaderRelOffset = -1;
        this.dataOffset = -1;
        Streams.readFully(cdStream, cdeHdrBuf, 0, cdeHdrBuf.length);
        BufferIterator it = HeapBufferIterator.iterator(cdeHdrBuf, 0, cdeHdrBuf.length, ByteOrder.LITTLE_ENDIAN);
        int sig = it.readInt();
        if (((long) sig) != ZipConstants.CENSIG) {
            ZipFile.throwZipException("Central Directory Entry", sig);
        }
        it.seek(DEFLATED);
        int gpbf = it.readShort() & DexFormat.MAX_TYPE_IDX;
        if ((gpbf & 1) != 0) {
            throw new ZipException("Invalid General Purpose Bit Flag: " + gpbf);
        }
        Charset charset = defaultCharset;
        if ((gpbf & NodeFilter.SHOW_NOTATION) != 0) {
            charset = StandardCharsets.UTF_8;
        }
        this.compressionMethod = it.readShort() & DexFormat.MAX_TYPE_IDX;
        this.time = it.readShort() & DexFormat.MAX_TYPE_IDX;
        this.modDate = it.readShort() & DexFormat.MAX_TYPE_IDX;
        this.crc = ((long) it.readInt()) & 4294967295L;
        this.compressedSize = ((long) it.readInt()) & 4294967295L;
        this.size = ((long) it.readInt()) & 4294967295L;
        this.nameLength = it.readShort() & DexFormat.MAX_TYPE_IDX;
        int extraLength = it.readShort() & DexFormat.MAX_TYPE_IDX;
        int commentByteCount = it.readShort() & DexFormat.MAX_TYPE_IDX;
        it.seek(42);
        this.localHeaderRelOffset = ((long) it.readInt()) & 4294967295L;
        byte[] nameBytes = new byte[this.nameLength];
        Streams.readFully(cdStream, nameBytes, 0, nameBytes.length);
        if (containsNulByte(nameBytes)) {
            throw new ZipException("Filename contains NUL byte: " + Arrays.toString(nameBytes));
        }
        this.name = new String(nameBytes, 0, nameBytes.length, charset);
        if (extraLength > 0) {
            this.extra = new byte[extraLength];
            Streams.readFully(cdStream, this.extra, 0, extraLength);
        }
        if (commentByteCount > 0) {
            byte[] commentBytes = new byte[commentByteCount];
            Streams.readFully(cdStream, commentBytes, 0, commentByteCount);
            this.comment = new String(commentBytes, 0, commentBytes.length, charset);
        }
    }

    private static boolean containsNulByte(byte[] bytes) {
        for (byte b : bytes) {
            if (b == null) {
                return true;
            }
        }
        return false;
    }

    private static void validateStringLength(String argument, String string) {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > DexFormat.MAX_TYPE_IDX) {
            throw new IllegalArgumentException(argument + " too long: " + bytes.length);
        }
    }
}
