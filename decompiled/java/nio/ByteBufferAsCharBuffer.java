package java.nio;

final class ByteBufferAsCharBuffer extends CharBuffer {
    private final ByteBuffer byteBuffer;

    static CharBuffer asCharBuffer(ByteBuffer byteBuffer) {
        ByteBuffer slice = byteBuffer.slice();
        slice.order(byteBuffer.order());
        return new ByteBufferAsCharBuffer(slice);
    }

    private ByteBufferAsCharBuffer(ByteBuffer byteBuffer) {
        super(byteBuffer.capacity() / 2, byteBuffer.effectiveDirectAddress);
        this.byteBuffer = byteBuffer;
        this.byteBuffer.clear();
    }

    public CharBuffer asReadOnlyBuffer() {
        ByteBufferAsCharBuffer buf = new ByteBufferAsCharBuffer(this.byteBuffer.asReadOnlyBuffer());
        buf.limit = this.limit;
        buf.position = this.position;
        buf.mark = this.mark;
        buf.byteBuffer.order = this.byteBuffer.order;
        return buf;
    }

    public CharBuffer compact() {
        if (this.byteBuffer.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        this.byteBuffer.limit(this.limit * 2);
        this.byteBuffer.position(this.position * 2);
        this.byteBuffer.compact();
        this.byteBuffer.clear();
        this.position = this.limit - this.position;
        this.limit = this.capacity;
        this.mark = -1;
        return this;
    }

    public CharBuffer duplicate() {
        ByteBufferAsCharBuffer buf = new ByteBufferAsCharBuffer(this.byteBuffer.duplicate().order(this.byteBuffer.order()));
        buf.limit = this.limit;
        buf.position = this.position;
        buf.mark = this.mark;
        return buf;
    }

    public char get() {
        if (this.position == this.limit) {
            throw new BufferUnderflowException();
        }
        ByteBuffer byteBuffer = this.byteBuffer;
        int i = this.position;
        this.position = i + 1;
        return byteBuffer.getChar(i * 2);
    }

    public char get(int index) {
        checkIndex(index);
        return this.byteBuffer.getChar(index * 2);
    }

    public CharBuffer get(char[] dst, int dstOffset, int charCount) {
        this.byteBuffer.limit(this.limit * 2);
        this.byteBuffer.position(this.position * 2);
        if (this.byteBuffer instanceof DirectByteBuffer) {
            ((DirectByteBuffer) this.byteBuffer).get(dst, dstOffset, charCount);
        } else {
            ((ByteArrayBuffer) this.byteBuffer).get(dst, dstOffset, charCount);
        }
        this.position += charCount;
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

    char[] protectedArray() {
        throw new UnsupportedOperationException();
    }

    int protectedArrayOffset() {
        throw new UnsupportedOperationException();
    }

    boolean protectedHasArray() {
        return false;
    }

    public CharBuffer put(char c) {
        if (this.position == this.limit) {
            throw new BufferOverflowException();
        }
        ByteBuffer byteBuffer = this.byteBuffer;
        int i = this.position;
        this.position = i + 1;
        byteBuffer.putChar(i * 2, c);
        return this;
    }

    public CharBuffer put(int index, char c) {
        checkIndex(index);
        this.byteBuffer.putChar(index * 2, c);
        return this;
    }

    public CharBuffer put(char[] src, int srcOffset, int charCount) {
        this.byteBuffer.limit(this.limit * 2);
        this.byteBuffer.position(this.position * 2);
        if (this.byteBuffer instanceof DirectByteBuffer) {
            ((DirectByteBuffer) this.byteBuffer).put(src, srcOffset, charCount);
        } else {
            ((ByteArrayBuffer) this.byteBuffer).put(src, srcOffset, charCount);
        }
        this.position += charCount;
        return this;
    }

    public CharBuffer slice() {
        this.byteBuffer.limit(this.limit * 2);
        this.byteBuffer.position(this.position * 2);
        CharBuffer result = new ByteBufferAsCharBuffer(this.byteBuffer.slice().order(this.byteBuffer.order()));
        this.byteBuffer.clear();
        return result;
    }

    public CharBuffer subSequence(int start, int end) {
        checkStartEndRemaining(start, end);
        CharBuffer result = duplicate();
        result.limit(this.position + end);
        result.position(this.position + start);
        return result;
    }
}
