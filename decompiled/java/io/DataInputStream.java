package java.io;

import com.android.dex.DexFormat;
import java.nio.ByteOrder;
import java.nio.charset.ModifiedUtf8;
import libcore.io.Memory;
import libcore.io.Streams;
import org.apache.harmony.security.asn1.ASN1UTCTime;
import org.w3c.dom.traversal.NodeFilter;
import org.xmlpull.v1.XmlPullParser;

public class DataInputStream extends FilterInputStream implements DataInput {
    private final byte[] scratch;

    public DataInputStream(InputStream in) {
        super(in);
        this.scratch = new byte[8];
    }

    public final int read(byte[] buffer) throws IOException {
        return super.read(buffer);
    }

    public final int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        return this.in.read(buffer, byteOffset, byteCount);
    }

    public final boolean readBoolean() throws IOException {
        int temp = this.in.read();
        if (temp >= 0) {
            return temp != 0;
        } else {
            throw new EOFException();
        }
    }

    public final byte readByte() throws IOException {
        int temp = this.in.read();
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
        Streams.readFully(this.in, dst, offset, byteCount);
    }

    public final int readInt() throws IOException {
        Streams.readFully(this.in, this.scratch, 0, 4);
        return Memory.peekInt(this.scratch, 0, ByteOrder.BIG_ENDIAN);
    }

    @Deprecated
    public final String readLine() throws IOException {
        StringBuilder line = new StringBuilder(80);
        boolean foundTerminator = false;
        while (true) {
            int nextByte = this.in.read();
            switch (nextByte) {
                case NodeFilter.SHOW_ALL /*-1*/:
                    if (line.length() != 0 || foundTerminator) {
                        return line.toString();
                    }
                    return null;
                case XmlPullParser.DOCDECL /*10*/:
                    return line.toString();
                case ASN1UTCTime.UTC_HMS /*13*/:
                    if (!foundTerminator) {
                        foundTerminator = true;
                        if (this.in.getClass() == PushbackInputStream.class) {
                            break;
                        }
                        this.in = new PushbackInputStream(this.in);
                        break;
                    }
                    ((PushbackInputStream) this.in).unread(nextByte);
                    return line.toString();
                default:
                    if (!foundTerminator) {
                        line.append((char) nextByte);
                        break;
                    }
                    ((PushbackInputStream) this.in).unread(nextByte);
                    return line.toString();
            }
        }
    }

    public final long readLong() throws IOException {
        Streams.readFully(this.in, this.scratch, 0, 8);
        return Memory.peekLong(this.scratch, 0, ByteOrder.BIG_ENDIAN);
    }

    public final short readShort() throws IOException {
        Streams.readFully(this.in, this.scratch, 0, 2);
        return Memory.peekShort(this.scratch, 0, ByteOrder.BIG_ENDIAN);
    }

    public final int readUnsignedByte() throws IOException {
        int temp = this.in.read();
        if (temp >= 0) {
            return temp;
        }
        throw new EOFException();
    }

    public final int readUnsignedShort() throws IOException {
        return readShort() & DexFormat.MAX_TYPE_IDX;
    }

    public final String readUTF() throws IOException {
        return decodeUTF(readUnsignedShort());
    }

    String decodeUTF(int utfSize) throws IOException {
        return decodeUTF(utfSize, this);
    }

    private static String decodeUTF(int utfSize, DataInput in) throws IOException {
        byte[] buf = new byte[utfSize];
        in.readFully(buf, 0, utfSize);
        return ModifiedUtf8.decode(buf, new char[utfSize], 0, utfSize);
    }

    public static final String readUTF(DataInput in) throws IOException {
        return decodeUTF(in.readUnsignedShort(), in);
    }

    public final int skipBytes(int count) throws IOException {
        int skipped = 0;
        while (skipped < count) {
            long skip = this.in.skip((long) (count - skipped));
            if (skip == 0) {
                break;
            }
            skipped = (int) (((long) skipped) + skip);
        }
        return skipped;
    }
}
