package java.nio;

public abstract class Buffer {
    static final int UNSET_MARK = -1;
    final int _elementSizeShift;
    final int capacity;
    final long effectiveDirectAddress;
    int limit;
    int mark;
    int position;

    public abstract Object array();

    public abstract int arrayOffset();

    public abstract boolean hasArray();

    public abstract boolean isDirect();

    public abstract boolean isReadOnly();

    Buffer(int elementSizeShift, int capacity, long effectiveDirectAddress) {
        this.mark = UNSET_MARK;
        this.position = 0;
        this._elementSizeShift = elementSizeShift;
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity < 0: " + capacity);
        }
        this.limit = capacity;
        this.capacity = capacity;
        this.effectiveDirectAddress = effectiveDirectAddress;
    }

    public final int capacity() {
        return this.capacity;
    }

    void checkIndex(int index) {
        if (index < 0 || index >= this.limit) {
            throw new IndexOutOfBoundsException("index=" + index + ", limit=" + this.limit);
        }
    }

    void checkIndex(int index, int sizeOfType) {
        if (index < 0 || index > this.limit - sizeOfType) {
            throw new IndexOutOfBoundsException("index=" + index + ", limit=" + this.limit + ", size of type=" + sizeOfType);
        }
    }

    int checkGetBounds(int bytesPerElement, int length, int offset, int count) {
        int byteCount = bytesPerElement * count;
        if ((offset | count) < 0 || offset > length || length - offset < count) {
            throw new IndexOutOfBoundsException("offset=" + offset + ", count=" + count + ", length=" + length);
        } else if (byteCount <= remaining()) {
            return byteCount;
        } else {
            throw new BufferUnderflowException();
        }
    }

    int checkPutBounds(int bytesPerElement, int length, int offset, int count) {
        int byteCount = bytesPerElement * count;
        if ((offset | count) < 0 || offset > length || length - offset < count) {
            throw new IndexOutOfBoundsException("offset=" + offset + ", count=" + count + ", length=" + length);
        } else if (byteCount > remaining()) {
            throw new BufferOverflowException();
        } else if (!isReadOnly()) {
            return byteCount;
        } else {
            throw new ReadOnlyBufferException();
        }
    }

    void checkStartEndRemaining(int start, int end) {
        if (end < start || start < 0 || end > remaining()) {
            throw new IndexOutOfBoundsException("start=" + start + ", end=" + end + ", remaining()=" + remaining());
        }
    }

    public final Buffer clear() {
        this.position = 0;
        this.mark = UNSET_MARK;
        this.limit = this.capacity;
        return this;
    }

    public final Buffer flip() {
        this.limit = this.position;
        this.position = 0;
        this.mark = UNSET_MARK;
        return this;
    }

    public final boolean hasRemaining() {
        return this.position < this.limit;
    }

    final void checkWritable() {
        if (isReadOnly()) {
            throw new IllegalArgumentException("Read-only buffer");
        }
    }

    public final int limit() {
        return this.limit;
    }

    public final Buffer limit(int newLimit) {
        if (newLimit < 0 || newLimit > this.capacity) {
            throw new IllegalArgumentException("Bad limit (capacity " + this.capacity + "): " + newLimit);
        }
        this.limit = newLimit;
        if (this.position > newLimit) {
            this.position = newLimit;
        }
        if (this.mark != UNSET_MARK && this.mark > newLimit) {
            this.mark = UNSET_MARK;
        }
        return this;
    }

    public final Buffer mark() {
        this.mark = this.position;
        return this;
    }

    public final int position() {
        return this.position;
    }

    public final Buffer position(int newPosition) {
        positionImpl(newPosition);
        return this;
    }

    void positionImpl(int newPosition) {
        if (newPosition < 0 || newPosition > this.limit) {
            throw new IllegalArgumentException("Bad position (limit " + this.limit + "): " + newPosition);
        }
        this.position = newPosition;
        if (this.mark != UNSET_MARK && this.mark > this.position) {
            this.mark = UNSET_MARK;
        }
    }

    public final int remaining() {
        return this.limit - this.position;
    }

    public final Buffer reset() {
        if (this.mark == UNSET_MARK) {
            throw new InvalidMarkException("Mark not set");
        }
        this.position = this.mark;
        return this;
    }

    public final Buffer rewind() {
        this.position = 0;
        this.mark = UNSET_MARK;
        return this;
    }

    public String toString() {
        return getClass().getName() + "[position=" + this.position + ",limit=" + this.limit + ",capacity=" + this.capacity + "]";
    }

    public final int getElementSizeShift() {
        return this._elementSizeShift;
    }
}
