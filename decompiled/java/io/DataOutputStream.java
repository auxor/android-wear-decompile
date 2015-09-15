package java.io;

import java.nio.ByteOrder;
import java.nio.charset.ModifiedUtf8;
import libcore.io.Memory;

public class DataOutputStream extends FilterOutputStream implements DataOutput {
    private final byte[] scratch;
    protected int written;

    public DataOutputStream(OutputStream out) {
        super(out);
        this.scratch = new byte[8];
    }

    public void flush() throws IOException {
        super.flush();
    }

    public final int size() {
        if (this.written < 0) {
            this.written = Integer.MAX_VALUE;
        }
        return this.written;
    }

    public void write(byte[] buffer, int offset, int count) throws IOException {
        if (buffer == null) {
            throw new NullPointerException("buffer == null");
        }
        this.out.write(buffer, offset, count);
        this.written += count;
    }

    public void write(int oneByte) throws IOException {
        this.out.write(oneByte);
        this.written++;
    }

    public final void writeBoolean(boolean val) throws IOException {
        this.out.write(val ? 1 : 0);
        this.written++;
    }

    public final void writeByte(int val) throws IOException {
        this.out.write(val);
        this.written++;
    }

    public final void writeBytes(String str) throws IOException {
        if (str.length() != 0) {
            byte[] bytes = new byte[str.length()];
            for (int index = 0; index < str.length(); index++) {
                bytes[index] = (byte) str.charAt(index);
            }
            this.out.write(bytes);
            this.written += bytes.length;
        }
    }

    public final void writeChar(int val) throws IOException {
        writeShort(val);
    }

    public final void writeChars(String str) throws IOException {
        byte[] bytes = str.getBytes("UTF-16BE");
        this.out.write(bytes);
        this.written += bytes.length;
    }

    public final void writeDouble(double val) throws IOException {
        writeLong(Double.doubleToLongBits(val));
    }

    public final void writeFloat(float val) throws IOException {
        writeInt(Float.floatToIntBits(val));
    }

    public final void writeInt(int val) throws IOException {
        Memory.pokeInt(this.scratch, 0, val, ByteOrder.BIG_ENDIAN);
        this.out.write(this.scratch, 0, 4);
        this.written += 4;
    }

    public final void writeLong(long val) throws IOException {
        Memory.pokeLong(this.scratch, 0, val, ByteOrder.BIG_ENDIAN);
        this.out.write(this.scratch, 0, 8);
        this.written += 8;
    }

    public final void writeShort(int val) throws IOException {
        Memory.pokeShort(this.scratch, 0, (short) val, ByteOrder.BIG_ENDIAN);
        this.out.write(this.scratch, 0, 2);
        this.written += 2;
    }

    public final void writeUTF(String str) throws IOException {
        write(ModifiedUtf8.encode(str));
    }
}
