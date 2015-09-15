package java.io;

import android.system.ErrnoException;
import android.system.OsConstants;
import com.android.dex.DexFormat;
import dalvik.bytecode.Opcodes;
import dalvik.system.CloseGuard;
import java.nio.ByteOrder;
import java.nio.NioUtils;
import java.nio.channels.FileChannel;
import java.nio.charset.ModifiedUtf8;
import java.util.Arrays;
import libcore.io.IoBridge;
import libcore.io.Libcore;
import libcore.io.Memory;
import org.apache.harmony.security.asn1.ASN1UTCTime;
import org.w3c.dom.traversal.NodeFilter;
import org.xmlpull.v1.XmlPullParser;

public class RandomAccessFile implements DataInput, DataOutput, Closeable {
    private FileChannel channel;
    private FileDescriptor fd;
    private final CloseGuard guard;
    private int mode;
    private final byte[] scratch;
    private boolean syncMetadata;

    public RandomAccessFile(File file, String mode) throws FileNotFoundException {
        int flags;
        this.syncMetadata = false;
        this.guard = CloseGuard.get();
        this.scratch = new byte[8];
        if (mode.equals("r")) {
            flags = OsConstants.O_RDONLY;
        } else if (mode.equals("rw") || mode.equals("rws") || mode.equals("rwd")) {
            flags = OsConstants.O_RDWR | OsConstants.O_CREAT;
            if (mode.equals("rws")) {
                this.syncMetadata = true;
            } else if (mode.equals("rwd")) {
                flags |= OsConstants.O_SYNC;
            }
        } else {
            throw new IllegalArgumentException("Invalid mode: " + mode);
        }
        this.mode = flags;
        this.fd = IoBridge.open(file.getPath(), flags);
        if (this.syncMetadata) {
            try {
                this.fd.sync();
            } catch (IOException e) {
            }
        }
        this.guard.open("close");
    }

    public RandomAccessFile(String fileName, String mode) throws FileNotFoundException {
        this(new File(fileName), mode);
    }

    public void close() throws IOException {
        this.guard.close();
        synchronized (this) {
            if (this.channel != null && this.channel.isOpen()) {
                this.channel.close();
                this.channel = null;
            }
            IoBridge.closeAndSignalBlockedThreads(this.fd);
        }
    }

    protected void finalize() throws Throwable {
        try {
            if (this.guard != null) {
                this.guard.warnIfOpen();
            }
            close();
        } finally {
            super.finalize();
        }
    }

    public final synchronized FileChannel getChannel() {
        if (this.channel == null) {
            this.channel = NioUtils.newFileChannel(this, this.fd, this.mode);
        }
        return this.channel;
    }

    public final FileDescriptor getFD() throws IOException {
        return this.fd;
    }

    public long getFilePointer() throws IOException {
        try {
            return Libcore.os.lseek(this.fd, 0, OsConstants.SEEK_CUR);
        } catch (ErrnoException errnoException) {
            throw errnoException.rethrowAsIOException();
        }
    }

    public long length() throws IOException {
        try {
            return Libcore.os.fstat(this.fd).st_size;
        } catch (ErrnoException errnoException) {
            throw errnoException.rethrowAsIOException();
        }
    }

    public int read() throws IOException {
        return read(this.scratch, 0, 1) != -1 ? this.scratch[0] & Opcodes.OP_CONST_CLASS_JUMBO : -1;
    }

    public int read(byte[] buffer) throws IOException {
        return read(buffer, 0, buffer.length);
    }

    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        return IoBridge.read(this.fd, buffer, byteOffset, byteCount);
    }

    public final boolean readBoolean() throws IOException {
        int temp = read();
        if (temp >= 0) {
            return temp != 0;
        } else {
            throw new EOFException();
        }
    }

    public final byte readByte() throws IOException {
        int temp = read();
        if (temp >= 0) {
            return (byte) temp;
        }
        throw new EOFException();
    }

    public final char readChar() throws IOException {
        return (char) readShort();
    }

    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    public final void readFully(byte[] dst) throws IOException {
        readFully(dst, 0, dst.length);
    }

    public final void readFully(byte[] dst, int offset, int byteCount) throws IOException {
        Arrays.checkOffsetAndCount(dst.length, offset, byteCount);
        while (byteCount > 0) {
            int result = read(dst, offset, byteCount);
            if (result < 0) {
                throw new EOFException();
            }
            offset += result;
            byteCount -= result;
        }
    }

    public final int readInt() throws IOException {
        readFully(this.scratch, 0, 4);
        return Memory.peekInt(this.scratch, 0, ByteOrder.BIG_ENDIAN);
    }

    public final String readLine() throws IOException {
        StringBuilder line = new StringBuilder(80);
        boolean foundTerminator = false;
        long unreadPosition = 0;
        while (true) {
            int nextByte = read();
            switch (nextByte) {
                case NodeFilter.SHOW_ALL /*-1*/:
                    return line.length() != 0 ? line.toString() : null;
                case XmlPullParser.DOCDECL /*10*/:
                    return line.toString();
                case ASN1UTCTime.UTC_HMS /*13*/:
                    if (!foundTerminator) {
                        foundTerminator = true;
                        unreadPosition = getFilePointer();
                        break;
                    }
                    seek(unreadPosition);
                    return line.toString();
                default:
                    if (!foundTerminator) {
                        line.append((char) nextByte);
                        break;
                    }
                    seek(unreadPosition);
                    return line.toString();
            }
        }
    }

    public final long readLong() throws IOException {
        readFully(this.scratch, 0, 8);
        return Memory.peekLong(this.scratch, 0, ByteOrder.BIG_ENDIAN);
    }

    public final short readShort() throws IOException {
        readFully(this.scratch, 0, 2);
        return Memory.peekShort(this.scratch, 0, ByteOrder.BIG_ENDIAN);
    }

    public final int readUnsignedByte() throws IOException {
        int temp = read();
        if (temp >= 0) {
            return temp;
        }
        throw new EOFException();
    }

    public final int readUnsignedShort() throws IOException {
        return readShort() & DexFormat.MAX_TYPE_IDX;
    }

    public final String readUTF() throws IOException {
        int utfSize = readUnsignedShort();
        if (utfSize == 0) {
            return XmlPullParser.NO_NAMESPACE;
        }
        byte[] buf = new byte[utfSize];
        if (read(buf, 0, buf.length) == buf.length) {
            return ModifiedUtf8.decode(buf, new char[utfSize], 0, utfSize);
        }
        throw new EOFException();
    }

    public void seek(long offset) throws IOException {
        if (offset < 0) {
            throw new IOException("offset < 0: " + offset);
        }
        try {
            Libcore.os.lseek(this.fd, offset, OsConstants.SEEK_SET);
        } catch (ErrnoException errnoException) {
            throw errnoException.rethrowAsIOException();
        }
    }

    public void setLength(long newLength) throws IOException {
        if (newLength < 0) {
            throw new IllegalArgumentException("newLength < 0");
        }
        try {
            Libcore.os.ftruncate(this.fd, newLength);
            if (getFilePointer() > newLength) {
                seek(newLength);
            }
            if (this.syncMetadata) {
                this.fd.sync();
            }
        } catch (ErrnoException errnoException) {
            throw errnoException.rethrowAsIOException();
        }
    }

    public int skipBytes(int count) throws IOException {
        if (count <= 0) {
            return 0;
        }
        long currentPos = getFilePointer();
        long eof = length();
        int newCount = (int) (((long) count) + currentPos > eof ? eof - currentPos : (long) count);
        seek(((long) newCount) + currentPos);
        return newCount;
    }

    public void write(byte[] buffer) throws IOException {
        write(buffer, 0, buffer.length);
    }

    public void write(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        IoBridge.write(this.fd, buffer, byteOffset, byteCount);
        if (this.syncMetadata) {
            this.fd.sync();
        }
    }

    public void write(int oneByte) throws IOException {
        this.scratch[0] = (byte) (oneByte & Opcodes.OP_CONST_CLASS_JUMBO);
        write(this.scratch, 0, 1);
    }

    public final void writeBoolean(boolean val) throws IOException {
        write(val ? 1 : 0);
    }

    public final void writeByte(int val) throws IOException {
        write(val & Opcodes.OP_CONST_CLASS_JUMBO);
    }

    public final void writeBytes(String str) throws IOException {
        byte[] bytes = new byte[str.length()];
        for (int index = 0; index < str.length(); index++) {
            bytes[index] = (byte) (str.charAt(index) & Opcodes.OP_CONST_CLASS_JUMBO);
        }
        write(bytes);
    }

    public final void writeChar(int val) throws IOException {
        writeShort(val);
    }

    public final void writeChars(String str) throws IOException {
        write(str.getBytes("UTF-16BE"));
    }

    public final void writeDouble(double val) throws IOException {
        writeLong(Double.doubleToLongBits(val));
    }

    public final void writeFloat(float val) throws IOException {
        writeInt(Float.floatToIntBits(val));
    }

    public final void writeInt(int val) throws IOException {
        Memory.pokeInt(this.scratch, 0, val, ByteOrder.BIG_ENDIAN);
        write(this.scratch, 0, 4);
    }

    public final void writeLong(long val) throws IOException {
        Memory.pokeLong(this.scratch, 0, val, ByteOrder.BIG_ENDIAN);
        write(this.scratch, 0, 8);
    }

    public final void writeShort(int val) throws IOException {
        Memory.pokeShort(this.scratch, 0, (short) val, ByteOrder.BIG_ENDIAN);
        write(this.scratch, 0, 2);
    }

    public final void writeUTF(String str) throws IOException {
        write(ModifiedUtf8.encode(str));
    }
}
