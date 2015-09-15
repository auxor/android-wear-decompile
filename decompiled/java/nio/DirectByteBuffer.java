package java.nio;

import java.nio.channels.FileChannel.MapMode;
import libcore.io.Memory;

class DirectByteBuffer extends MappedByteBuffer {
    private final boolean isReadOnly;
    protected final int offset;

    protected DirectByteBuffer(MemoryBlock block, int capacity, int offset, boolean isReadOnly, MapMode mapMode) {
        super(block, capacity, mapMode, block.toLong() + ((long) offset));
        long baseSize = block.getSize();
        if (baseSize < 0 || ((long) (capacity + offset)) <= baseSize) {
            this.offset = offset;
            this.isReadOnly = isReadOnly;
            return;
        }
        throw new IllegalArgumentException("capacity + offset > baseSize");
    }

    DirectByteBuffer(long address, int capacity) {
        this(MemoryBlock.wrapFromJni(address, (long) capacity), capacity, 0, false, null);
    }

    private static DirectByteBuffer copy(DirectByteBuffer other, int markOfOther, boolean isReadOnly) {
        other.checkNotFreed();
        DirectByteBuffer buf = new DirectByteBuffer(other.block, other.capacity(), other.offset, isReadOnly, other.mapMode);
        buf.limit = other.limit;
        buf.position = other.position();
        buf.mark = markOfOther;
        return buf;
    }

    public ByteBuffer asReadOnlyBuffer() {
        return copy(this, this.mark, true);
    }

    public ByteBuffer compact() {
        checkIsAccessible();
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        Memory.memmove(this, 0, this, this.position, (long) remaining());
        this.position = this.limit - this.position;
        this.limit = this.capacity;
        this.mark = -1;
        return this;
    }

    public ByteBuffer duplicate() {
        return copy(this, this.mark, this.isReadOnly);
    }

    public ByteBuffer slice() {
        checkNotFreed();
        return new DirectByteBuffer(this.block, remaining(), this.offset + this.position, this.isReadOnly, this.mapMode);
    }

    public boolean isReadOnly() {
        return this.isReadOnly;
    }

    byte[] protectedArray() {
        checkIsAccessible();
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        byte[] array = this.block.array();
        if (array != null) {
            return array;
        }
        throw new UnsupportedOperationException();
    }

    int protectedArrayOffset() {
        protectedArray();
        return this.offset;
    }

    boolean protectedHasArray() {
        return (this.isReadOnly || this.block.array() == null) ? false : true;
    }

    public final ByteBuffer get(byte[] dst, int dstOffset, int byteCount) {
        checkIsAccessible();
        checkGetBounds(1, dst.length, dstOffset, byteCount);
        this.block.peekByteArray(this.offset + this.position, dst, dstOffset, byteCount);
        this.position += byteCount;
        return this;
    }

    final void get(char[] dst, int dstOffset, int charCount) {
        checkIsAccessible();
        int byteCount = checkGetBounds(2, dst.length, dstOffset, charCount);
        this.block.peekCharArray(this.offset + this.position, dst, dstOffset, charCount, this.order.needsSwap);
        this.position += byteCount;
    }

    final void get(double[] dst, int dstOffset, int doubleCount) {
        checkIsAccessible();
        int byteCount = checkGetBounds(8, dst.length, dstOffset, doubleCount);
        this.block.peekDoubleArray(this.offset + this.position, dst, dstOffset, doubleCount, this.order.needsSwap);
        this.position += byteCount;
    }

    final void get(float[] dst, int dstOffset, int floatCount) {
        checkIsAccessible();
        int byteCount = checkGetBounds(4, dst.length, dstOffset, floatCount);
        this.block.peekFloatArray(this.offset + this.position, dst, dstOffset, floatCount, this.order.needsSwap);
        this.position += byteCount;
    }

    final void get(int[] dst, int dstOffset, int intCount) {
        checkIsAccessible();
        int byteCount = checkGetBounds(4, dst.length, dstOffset, intCount);
        this.block.peekIntArray(this.offset + this.position, dst, dstOffset, intCount, this.order.needsSwap);
        this.position += byteCount;
    }

    final void get(long[] dst, int dstOffset, int longCount) {
        checkIsAccessible();
        int byteCount = checkGetBounds(8, dst.length, dstOffset, longCount);
        this.block.peekLongArray(this.offset + this.position, dst, dstOffset, longCount, this.order.needsSwap);
        this.position += byteCount;
    }

    final void get(short[] dst, int dstOffset, int shortCount) {
        checkIsAccessible();
        int byteCount = checkGetBounds(2, dst.length, dstOffset, shortCount);
        this.block.peekShortArray(this.offset + this.position, dst, dstOffset, shortCount, this.order.needsSwap);
        this.position += byteCount;
    }

    public final byte get() {
        checkIsAccessible();
        if (this.position == this.limit) {
            throw new BufferUnderflowException();
        }
        MemoryBlock memoryBlock = this.block;
        int i = this.offset;
        int i2 = this.position;
        this.position = i2 + 1;
        return memoryBlock.peekByte(i + i2);
    }

    public final byte get(int index) {
        checkIsAccessible();
        checkIndex(index);
        return this.block.peekByte(this.offset + index);
    }

    public final char getChar() {
        checkIsAccessible();
        int newPosition = this.position + 2;
        if (newPosition > this.limit) {
            throw new BufferUnderflowException();
        }
        char result = (char) this.block.peekShort(this.offset + this.position, this.order);
        this.position = newPosition;
        return result;
    }

    public final char getChar(int index) {
        checkIsAccessible();
        checkIndex(index, 2);
        return (char) this.block.peekShort(this.offset + index, this.order);
    }

    public final double getDouble() {
        checkIsAccessible();
        int newPosition = this.position + 8;
        if (newPosition > this.limit) {
            throw new BufferUnderflowException();
        }
        double result = Double.longBitsToDouble(this.block.peekLong(this.offset + this.position, this.order));
        this.position = newPosition;
        return result;
    }

    public final double getDouble(int index) {
        checkIsAccessible();
        checkIndex(index, 8);
        return Double.longBitsToDouble(this.block.peekLong(this.offset + index, this.order));
    }

    public final float getFloat() {
        checkIsAccessible();
        int newPosition = this.position + 4;
        if (newPosition > this.limit) {
            throw new BufferUnderflowException();
        }
        float result = Float.intBitsToFloat(this.block.peekInt(this.offset + this.position, this.order));
        this.position = newPosition;
        return result;
    }

    public final float getFloat(int index) {
        checkIsAccessible();
        checkIndex(index, 4);
        return Float.intBitsToFloat(this.block.peekInt(this.offset + index, this.order));
    }

    public final int getInt() {
        checkIsAccessible();
        int newPosition = this.position + 4;
        if (newPosition > this.limit) {
            throw new BufferUnderflowException();
        }
        int result = this.block.peekInt(this.offset + this.position, this.order);
        this.position = newPosition;
        return result;
    }

    public final int getInt(int index) {
        checkIsAccessible();
        checkIndex(index, 4);
        return this.block.peekInt(this.offset + index, this.order);
    }

    public final long getLong() {
        checkIsAccessible();
        int newPosition = this.position + 8;
        if (newPosition > this.limit) {
            throw new BufferUnderflowException();
        }
        long result = this.block.peekLong(this.offset + this.position, this.order);
        this.position = newPosition;
        return result;
    }

    public final long getLong(int index) {
        checkIsAccessible();
        checkIndex(index, 8);
        return this.block.peekLong(this.offset + index, this.order);
    }

    public final short getShort() {
        checkIsAccessible();
        int newPosition = this.position + 2;
        if (newPosition > this.limit) {
            throw new BufferUnderflowException();
        }
        short result = this.block.peekShort(this.offset + this.position, this.order);
        this.position = newPosition;
        return result;
    }

    public final short getShort(int index) {
        checkIsAccessible();
        checkIndex(index, 2);
        return this.block.peekShort(this.offset + index, this.order);
    }

    public final boolean isDirect() {
        return true;
    }

    public final boolean isAccessible() {
        return this.block.isAccessible();
    }

    public void setAccessible(boolean accessible) {
        this.block.setAccessible(accessible);
    }

    public final void free() {
        this.block.free();
    }

    public final CharBuffer asCharBuffer() {
        checkNotFreed();
        return ByteBufferAsCharBuffer.asCharBuffer(this);
    }

    public final DoubleBuffer asDoubleBuffer() {
        checkNotFreed();
        return ByteBufferAsDoubleBuffer.asDoubleBuffer(this);
    }

    public final FloatBuffer asFloatBuffer() {
        checkNotFreed();
        return ByteBufferAsFloatBuffer.asFloatBuffer(this);
    }

    public final IntBuffer asIntBuffer() {
        checkNotFreed();
        return ByteBufferAsIntBuffer.asIntBuffer(this);
    }

    public final LongBuffer asLongBuffer() {
        checkNotFreed();
        return ByteBufferAsLongBuffer.asLongBuffer(this);
    }

    public final ShortBuffer asShortBuffer() {
        checkNotFreed();
        return ByteBufferAsShortBuffer.asShortBuffer(this);
    }

    public ByteBuffer put(byte value) {
        checkIsAccessible();
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        } else if (this.position == this.limit) {
            throw new BufferOverflowException();
        } else {
            MemoryBlock memoryBlock = this.block;
            int i = this.offset;
            int i2 = this.position;
            this.position = i2 + 1;
            memoryBlock.pokeByte(i + i2, value);
            return this;
        }
    }

    public ByteBuffer put(int index, byte value) {
        checkIsAccessible();
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkIndex(index);
        this.block.pokeByte(this.offset + index, value);
        return this;
    }

    public ByteBuffer put(byte[] src, int srcOffset, int byteCount) {
        checkIsAccessible();
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkPutBounds(1, src.length, srcOffset, byteCount);
        this.block.pokeByteArray(this.offset + this.position, src, srcOffset, byteCount);
        this.position += byteCount;
        return this;
    }

    final void put(char[] src, int srcOffset, int charCount) {
        checkIsAccessible();
        int byteCount = checkPutBounds(2, src.length, srcOffset, charCount);
        this.block.pokeCharArray(this.offset + this.position, src, srcOffset, charCount, this.order.needsSwap);
        this.position += byteCount;
    }

    final void put(double[] src, int srcOffset, int doubleCount) {
        checkIsAccessible();
        int byteCount = checkPutBounds(8, src.length, srcOffset, doubleCount);
        this.block.pokeDoubleArray(this.offset + this.position, src, srcOffset, doubleCount, this.order.needsSwap);
        this.position += byteCount;
    }

    final void put(float[] src, int srcOffset, int floatCount) {
        checkIsAccessible();
        int byteCount = checkPutBounds(4, src.length, srcOffset, floatCount);
        this.block.pokeFloatArray(this.offset + this.position, src, srcOffset, floatCount, this.order.needsSwap);
        this.position += byteCount;
    }

    final void put(int[] src, int srcOffset, int intCount) {
        checkIsAccessible();
        int byteCount = checkPutBounds(4, src.length, srcOffset, intCount);
        this.block.pokeIntArray(this.offset + this.position, src, srcOffset, intCount, this.order.needsSwap);
        this.position += byteCount;
    }

    final void put(long[] src, int srcOffset, int longCount) {
        checkIsAccessible();
        int byteCount = checkPutBounds(8, src.length, srcOffset, longCount);
        this.block.pokeLongArray(this.offset + this.position, src, srcOffset, longCount, this.order.needsSwap);
        this.position += byteCount;
    }

    final void put(short[] src, int srcOffset, int shortCount) {
        checkIsAccessible();
        int byteCount = checkPutBounds(2, src.length, srcOffset, shortCount);
        this.block.pokeShortArray(this.offset + this.position, src, srcOffset, shortCount, this.order.needsSwap);
        this.position += byteCount;
    }

    public ByteBuffer putChar(char value) {
        checkIsAccessible();
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        int newPosition = this.position + 2;
        if (newPosition > this.limit) {
            throw new BufferOverflowException();
        }
        this.block.pokeShort(this.offset + this.position, (short) value, this.order);
        this.position = newPosition;
        return this;
    }

    public ByteBuffer putChar(int index, char value) {
        checkIsAccessible();
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkIndex(index, 2);
        this.block.pokeShort(this.offset + index, (short) value, this.order);
        return this;
    }

    public ByteBuffer putDouble(double value) {
        checkIsAccessible();
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        int newPosition = this.position + 8;
        if (newPosition > this.limit) {
            throw new BufferOverflowException();
        }
        this.block.pokeLong(this.offset + this.position, Double.doubleToRawLongBits(value), this.order);
        this.position = newPosition;
        return this;
    }

    public ByteBuffer putDouble(int index, double value) {
        checkIsAccessible();
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkIndex(index, 8);
        this.block.pokeLong(this.offset + index, Double.doubleToRawLongBits(value), this.order);
        return this;
    }

    public ByteBuffer putFloat(float value) {
        checkIsAccessible();
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        int newPosition = this.position + 4;
        if (newPosition > this.limit) {
            throw new BufferOverflowException();
        }
        this.block.pokeInt(this.offset + this.position, Float.floatToRawIntBits(value), this.order);
        this.position = newPosition;
        return this;
    }

    public ByteBuffer putFloat(int index, float value) {
        checkIsAccessible();
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkIndex(index, 4);
        this.block.pokeInt(this.offset + index, Float.floatToRawIntBits(value), this.order);
        return this;
    }

    public ByteBuffer putInt(int value) {
        checkIsAccessible();
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        int newPosition = this.position + 4;
        if (newPosition > this.limit) {
            throw new BufferOverflowException();
        }
        this.block.pokeInt(this.offset + this.position, value, this.order);
        this.position = newPosition;
        return this;
    }

    public ByteBuffer putInt(int index, int value) {
        checkIsAccessible();
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkIndex(index, 4);
        this.block.pokeInt(this.offset + index, value, this.order);
        return this;
    }

    public ByteBuffer putLong(long value) {
        checkIsAccessible();
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        int newPosition = this.position + 8;
        if (newPosition > this.limit) {
            throw new BufferOverflowException();
        }
        this.block.pokeLong(this.offset + this.position, value, this.order);
        this.position = newPosition;
        return this;
    }

    public ByteBuffer putLong(int index, long value) {
        checkIsAccessible();
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkIndex(index, 8);
        this.block.pokeLong(this.offset + index, value, this.order);
        return this;
    }

    public ByteBuffer putShort(short value) {
        checkIsAccessible();
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        int newPosition = this.position + 2;
        if (newPosition > this.limit) {
            throw new BufferOverflowException();
        }
        this.block.pokeShort(this.offset + this.position, value, this.order);
        this.position = newPosition;
        return this;
    }

    public ByteBuffer putShort(int index, short value) {
        checkIsAccessible();
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkIndex(index, 2);
        this.block.pokeShort(this.offset + index, value, this.order);
        return this;
    }

    private void checkIsAccessible() {
        checkNotFreed();
        if (!this.block.isAccessible()) {
            throw new IllegalStateException("buffer is inaccessible");
        }
    }

    private void checkNotFreed() {
        if (this.block.isFreed()) {
            throw new IllegalStateException("buffer was freed");
        }
    }
}
