package java.nio;

import libcore.io.Memory;

final class ByteArrayBuffer extends ByteBuffer {
    final int arrayOffset;
    final byte[] backingArray;
    private final boolean isReadOnly;

    ByteArrayBuffer(byte[] backingArray) {
        this(backingArray.length, backingArray, 0, false);
    }

    private ByteArrayBuffer(int capacity, byte[] backingArray, int arrayOffset, boolean isReadOnly) {
        super(capacity, 0);
        this.backingArray = backingArray;
        this.arrayOffset = arrayOffset;
        this.isReadOnly = isReadOnly;
        if (arrayOffset + capacity > backingArray.length) {
            throw new IndexOutOfBoundsException("backingArray.length=" + backingArray.length + ", capacity=" + capacity + ", arrayOffset=" + arrayOffset);
        }
    }

    private static ByteArrayBuffer copy(ByteArrayBuffer other, int markOfOther, boolean isReadOnly) {
        ByteArrayBuffer buf = new ByteArrayBuffer(other.capacity(), other.backingArray, other.arrayOffset, isReadOnly);
        buf.limit = other.limit;
        buf.position = other.position();
        buf.mark = markOfOther;
        return buf;
    }

    public ByteBuffer asReadOnlyBuffer() {
        return copy(this, this.mark, true);
    }

    public ByteBuffer compact() {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        System.arraycopy(this.backingArray, this.position + this.arrayOffset, this.backingArray, this.arrayOffset, remaining());
        this.position = this.limit - this.position;
        this.limit = this.capacity;
        this.mark = -1;
        return this;
    }

    public ByteBuffer duplicate() {
        return copy(this, this.mark, this.isReadOnly);
    }

    public ByteBuffer slice() {
        return new ByteArrayBuffer(remaining(), this.backingArray, this.arrayOffset + this.position, this.isReadOnly);
    }

    public boolean isReadOnly() {
        return this.isReadOnly;
    }

    byte[] protectedArray() {
        if (!this.isReadOnly) {
            return this.backingArray;
        }
        throw new ReadOnlyBufferException();
    }

    int protectedArrayOffset() {
        if (!this.isReadOnly) {
            return this.arrayOffset;
        }
        throw new ReadOnlyBufferException();
    }

    boolean protectedHasArray() {
        if (this.isReadOnly) {
            return false;
        }
        return true;
    }

    public final ByteBuffer get(byte[] dst, int dstOffset, int byteCount) {
        checkGetBounds(1, dst.length, dstOffset, byteCount);
        System.arraycopy(this.backingArray, this.arrayOffset + this.position, dst, dstOffset, byteCount);
        this.position += byteCount;
        return this;
    }

    final void get(char[] dst, int dstOffset, int charCount) {
        int byteCount = checkGetBounds(2, dst.length, dstOffset, charCount);
        Memory.unsafeBulkGet(dst, dstOffset, byteCount, this.backingArray, this.arrayOffset + this.position, 2, this.order.needsSwap);
        this.position += byteCount;
    }

    final void get(double[] dst, int dstOffset, int doubleCount) {
        int byteCount = checkGetBounds(8, dst.length, dstOffset, doubleCount);
        Memory.unsafeBulkGet(dst, dstOffset, byteCount, this.backingArray, this.arrayOffset + this.position, 8, this.order.needsSwap);
        this.position += byteCount;
    }

    final void get(float[] dst, int dstOffset, int floatCount) {
        int byteCount = checkGetBounds(4, dst.length, dstOffset, floatCount);
        Memory.unsafeBulkGet(dst, dstOffset, byteCount, this.backingArray, this.arrayOffset + this.position, 4, this.order.needsSwap);
        this.position += byteCount;
    }

    final void get(int[] dst, int dstOffset, int intCount) {
        int byteCount = checkGetBounds(4, dst.length, dstOffset, intCount);
        Memory.unsafeBulkGet(dst, dstOffset, byteCount, this.backingArray, this.arrayOffset + this.position, 4, this.order.needsSwap);
        this.position += byteCount;
    }

    final void get(long[] dst, int dstOffset, int longCount) {
        int byteCount = checkGetBounds(8, dst.length, dstOffset, longCount);
        Memory.unsafeBulkGet(dst, dstOffset, byteCount, this.backingArray, this.arrayOffset + this.position, 8, this.order.needsSwap);
        this.position += byteCount;
    }

    final void get(short[] dst, int dstOffset, int shortCount) {
        int byteCount = checkGetBounds(2, dst.length, dstOffset, shortCount);
        Memory.unsafeBulkGet(dst, dstOffset, byteCount, this.backingArray, this.arrayOffset + this.position, 2, this.order.needsSwap);
        this.position += byteCount;
    }

    public final byte get() {
        if (this.position == this.limit) {
            throw new BufferUnderflowException();
        }
        byte[] bArr = this.backingArray;
        int i = this.arrayOffset;
        int i2 = this.position;
        this.position = i2 + 1;
        return bArr[i + i2];
    }

    public final byte get(int index) {
        checkIndex(index);
        return this.backingArray[this.arrayOffset + index];
    }

    public final char getChar() {
        int newPosition = this.position + 2;
        if (newPosition > this.limit) {
            throw new BufferUnderflowException();
        }
        char result = (char) Memory.peekShort(this.backingArray, this.arrayOffset + this.position, this.order);
        this.position = newPosition;
        return result;
    }

    public final char getChar(int index) {
        checkIndex(index, 2);
        return (char) Memory.peekShort(this.backingArray, this.arrayOffset + index, this.order);
    }

    public final double getDouble() {
        return Double.longBitsToDouble(getLong());
    }

    public final double getDouble(int index) {
        return Double.longBitsToDouble(getLong(index));
    }

    public final float getFloat() {
        return Float.intBitsToFloat(getInt());
    }

    public final float getFloat(int index) {
        return Float.intBitsToFloat(getInt(index));
    }

    public final int getInt() {
        int newPosition = this.position + 4;
        if (newPosition > this.limit) {
            throw new BufferUnderflowException();
        }
        int result = Memory.peekInt(this.backingArray, this.arrayOffset + this.position, this.order);
        this.position = newPosition;
        return result;
    }

    public final int getInt(int index) {
        checkIndex(index, 4);
        return Memory.peekInt(this.backingArray, this.arrayOffset + index, this.order);
    }

    public final long getLong() {
        int newPosition = this.position + 8;
        if (newPosition > this.limit) {
            throw new BufferUnderflowException();
        }
        long result = Memory.peekLong(this.backingArray, this.arrayOffset + this.position, this.order);
        this.position = newPosition;
        return result;
    }

    public final long getLong(int index) {
        checkIndex(index, 8);
        return Memory.peekLong(this.backingArray, this.arrayOffset + index, this.order);
    }

    public final short getShort() {
        int newPosition = this.position + 2;
        if (newPosition > this.limit) {
            throw new BufferUnderflowException();
        }
        short result = Memory.peekShort(this.backingArray, this.arrayOffset + this.position, this.order);
        this.position = newPosition;
        return result;
    }

    public final short getShort(int index) {
        checkIndex(index, 2);
        return Memory.peekShort(this.backingArray, this.arrayOffset + index, this.order);
    }

    public final boolean isDirect() {
        return false;
    }

    public ByteBuffer put(byte b) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        } else if (this.position == this.limit) {
            throw new BufferOverflowException();
        } else {
            byte[] bArr = this.backingArray;
            int i = this.arrayOffset;
            int i2 = this.position;
            this.position = i2 + 1;
            bArr[i + i2] = b;
            return this;
        }
    }

    public ByteBuffer put(int index, byte b) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkIndex(index);
        this.backingArray[this.arrayOffset + index] = b;
        return this;
    }

    public ByteBuffer put(byte[] src, int srcOffset, int byteCount) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkPutBounds(1, src.length, srcOffset, byteCount);
        System.arraycopy(src, srcOffset, this.backingArray, this.arrayOffset + this.position, byteCount);
        this.position += byteCount;
        return this;
    }

    final void put(char[] src, int srcOffset, int charCount) {
        int byteCount = checkPutBounds(2, src.length, srcOffset, charCount);
        Memory.unsafeBulkPut(this.backingArray, this.arrayOffset + this.position, byteCount, src, srcOffset, 2, this.order.needsSwap);
        this.position += byteCount;
    }

    final void put(double[] src, int srcOffset, int doubleCount) {
        int byteCount = checkPutBounds(8, src.length, srcOffset, doubleCount);
        Memory.unsafeBulkPut(this.backingArray, this.arrayOffset + this.position, byteCount, src, srcOffset, 8, this.order.needsSwap);
        this.position += byteCount;
    }

    final void put(float[] src, int srcOffset, int floatCount) {
        int byteCount = checkPutBounds(4, src.length, srcOffset, floatCount);
        Memory.unsafeBulkPut(this.backingArray, this.arrayOffset + this.position, byteCount, src, srcOffset, 4, this.order.needsSwap);
        this.position += byteCount;
    }

    final void put(int[] src, int srcOffset, int intCount) {
        int byteCount = checkPutBounds(4, src.length, srcOffset, intCount);
        Memory.unsafeBulkPut(this.backingArray, this.arrayOffset + this.position, byteCount, src, srcOffset, 4, this.order.needsSwap);
        this.position += byteCount;
    }

    final void put(long[] src, int srcOffset, int longCount) {
        int byteCount = checkPutBounds(8, src.length, srcOffset, longCount);
        Memory.unsafeBulkPut(this.backingArray, this.arrayOffset + this.position, byteCount, src, srcOffset, 8, this.order.needsSwap);
        this.position += byteCount;
    }

    final void put(short[] src, int srcOffset, int shortCount) {
        int byteCount = checkPutBounds(2, src.length, srcOffset, shortCount);
        Memory.unsafeBulkPut(this.backingArray, this.arrayOffset + this.position, byteCount, src, srcOffset, 2, this.order.needsSwap);
        this.position += byteCount;
    }

    public ByteBuffer putChar(int index, char value) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkIndex(index, 2);
        Memory.pokeShort(this.backingArray, this.arrayOffset + index, (short) value, this.order);
        return this;
    }

    public ByteBuffer putChar(char value) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        int newPosition = this.position + 2;
        if (newPosition > this.limit) {
            throw new BufferOverflowException();
        }
        Memory.pokeShort(this.backingArray, this.arrayOffset + this.position, (short) value, this.order);
        this.position = newPosition;
        return this;
    }

    public ByteBuffer putDouble(double value) {
        return putLong(Double.doubleToRawLongBits(value));
    }

    public ByteBuffer putDouble(int index, double value) {
        return putLong(index, Double.doubleToRawLongBits(value));
    }

    public ByteBuffer putFloat(float value) {
        return putInt(Float.floatToRawIntBits(value));
    }

    public ByteBuffer putFloat(int index, float value) {
        return putInt(index, Float.floatToRawIntBits(value));
    }

    public ByteBuffer putInt(int value) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        int newPosition = this.position + 4;
        if (newPosition > this.limit) {
            throw new BufferOverflowException();
        }
        Memory.pokeInt(this.backingArray, this.arrayOffset + this.position, value, this.order);
        this.position = newPosition;
        return this;
    }

    public ByteBuffer putInt(int index, int value) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkIndex(index, 4);
        Memory.pokeInt(this.backingArray, this.arrayOffset + index, value, this.order);
        return this;
    }

    public ByteBuffer putLong(int index, long value) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkIndex(index, 8);
        Memory.pokeLong(this.backingArray, this.arrayOffset + index, value, this.order);
        return this;
    }

    public ByteBuffer putLong(long value) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        int newPosition = this.position + 8;
        if (newPosition > this.limit) {
            throw new BufferOverflowException();
        }
        Memory.pokeLong(this.backingArray, this.arrayOffset + this.position, value, this.order);
        this.position = newPosition;
        return this;
    }

    public ByteBuffer putShort(int index, short value) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkIndex(index, 2);
        Memory.pokeShort(this.backingArray, this.arrayOffset + index, value, this.order);
        return this;
    }

    public ByteBuffer putShort(short value) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        int newPosition = this.position + 2;
        if (newPosition > this.limit) {
            throw new BufferOverflowException();
        }
        Memory.pokeShort(this.backingArray, this.arrayOffset + this.position, value, this.order);
        this.position = newPosition;
        return this;
    }

    public final CharBuffer asCharBuffer() {
        return ByteBufferAsCharBuffer.asCharBuffer(this);
    }

    public final DoubleBuffer asDoubleBuffer() {
        return ByteBufferAsDoubleBuffer.asDoubleBuffer(this);
    }

    public final FloatBuffer asFloatBuffer() {
        return ByteBufferAsFloatBuffer.asFloatBuffer(this);
    }

    public final IntBuffer asIntBuffer() {
        return ByteBufferAsIntBuffer.asIntBuffer(this);
    }

    public final LongBuffer asLongBuffer() {
        return ByteBufferAsLongBuffer.asLongBuffer(this);
    }

    public final ShortBuffer asShortBuffer() {
        return ByteBufferAsShortBuffer.asShortBuffer(this);
    }
}
