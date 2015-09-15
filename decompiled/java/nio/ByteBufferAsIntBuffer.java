package java.nio;

final class ByteBufferAsIntBuffer extends IntBuffer {
    private final ByteBuffer byteBuffer;

    static IntBuffer asIntBuffer(ByteBuffer byteBuffer) {
        ByteBuffer slice = byteBuffer.slice();
        slice.order(byteBuffer.order());
        return new ByteBufferAsIntBuffer(slice);
    }

    private ByteBufferAsIntBuffer(ByteBuffer byteBuffer) {
        super(byteBuffer.capacity() / 4, byteBuffer.effectiveDirectAddress);
        this.byteBuffer = byteBuffer;
        this.byteBuffer.clear();
    }

    public IntBuffer asReadOnlyBuffer() {
        ByteBufferAsIntBuffer buf = new ByteBufferAsIntBuffer(this.byteBuffer.asReadOnlyBuffer());
        buf.limit = this.limit;
        buf.position = this.position;
        buf.mark = this.mark;
        buf.byteBuffer.order = this.byteBuffer.order;
        return buf;
    }

    public IntBuffer compact() {
        if (this.byteBuffer.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        this.byteBuffer.limit(this.limit * 4);
        this.byteBuffer.position(this.position * 4);
        this.byteBuffer.compact();
        this.byteBuffer.clear();
        this.position = this.limit - this.position;
        this.limit = this.capacity;
        this.mark = -1;
        return this;
    }

    public IntBuffer duplicate() {
        ByteBufferAsIntBuffer buf = new ByteBufferAsIntBuffer(this.byteBuffer.duplicate().order(this.byteBuffer.order()));
        buf.limit = this.limit;
        buf.position = this.position;
        buf.mark = this.mark;
        return buf;
    }

    public int get() {
        if (this.position == this.limit) {
            throw new BufferUnderflowException();
        }
        ByteBuffer byteBuffer = this.byteBuffer;
        int i = this.position;
        this.position = i + 1;
        return byteBuffer.getInt(i * 4);
    }

    public int get(int index) {
        checkIndex(index);
        return this.byteBuffer.getInt(index * 4);
    }

    public IntBuffer get(int[] dst, int dstOffset, int intCount) {
        this.byteBuffer.limit(this.limit * 4);
        this.byteBuffer.position(this.position * 4);
        if (this.byteBuffer instanceof DirectByteBuffer) {
            ((DirectByteBuffer) this.byteBuffer).get(dst, dstOffset, intCount);
        } else {
            ((ByteArrayBuffer) this.byteBuffer).get(dst, dstOffset, intCount);
        }
        this.position += intCount;
        return this;
    }

    public boolean isDirect() {
        return this.byteBuffer.isDirect();
    }

    public boolean isReadOnly() {
        return this.byteBuffer.isReadOnly();
    }

    public ByteOrder order() {
        return this.byteBuffer.order();
    }

    int[] protectedArray() {
        throw new UnsupportedOperationException();
    }

    int protectedArrayOffset() {
        throw new UnsupportedOperationException();
    }

    boolean protectedHasArray() {
        return false;
    }

    public IntBuffer put(int c) {
        if (this.position == this.limit) {
            throw new BufferOverflowException();
        }
        ByteBuffer byteBuffer = this.byteBuffer;
        int i = this.position;
        this.position = i + 1;
        byteBuffer.putInt(i * 4, c);
        return this;
    }

    public IntBuffer put(int index, int c) {
        checkIndex(index);
        this.byteBuffer.putInt(index * 4, c);
        return this;
    }

    public IntBuffer put(int[] src, int srcOffset, int intCount) {
        this.byteBuffer.limit(this.limit * 4);
        this.byteBuffer.position(this.position * 4);
        if (this.byteBuffer instanceof DirectByteBuffer) {
            ((DirectByteBuffer) this.byteBuffer).put(src, srcOffset, intCount);
        } else {
            ((ByteArrayBuffer) this.byteBuffer).put(src, srcOffset, intCount);
        }
        this.position += intCount;
        return this;
    }

    public IntBuffer slice() {
        this.byteBuffer.limit(this.limit * 4);
        this.byteBuffer.position(this.position * 4);
        IntBuffer result = new ByteBufferAsIntBuffer(this.byteBuffer.slice().order(this.byteBuffer.order()));
        this.byteBuffer.clear();
        return result;
    }
}
