package java.nio;

final class CharArrayBuffer extends CharBuffer {
    private final int arrayOffset;
    private final char[] backingArray;
    private final boolean isReadOnly;

    CharArrayBuffer(char[] array) {
        this(array.length, array, 0, false);
    }

    private CharArrayBuffer(int capacity, char[] backingArray, int arrayOffset, boolean isReadOnly) {
        super(capacity, 0);
        this.backingArray = backingArray;
        this.arrayOffset = arrayOffset;
        this.isReadOnly = isReadOnly;
    }

    private static CharArrayBuffer copy(CharArrayBuffer other, int markOfOther, boolean isReadOnly) {
        CharArrayBuffer buf = new CharArrayBuffer(other.capacity(), other.backingArray, other.arrayOffset, isReadOnly);
        buf.limit = other.limit;
        buf.position = other.position();
        buf.mark = markOfOther;
        return buf;
    }

    public CharBuffer asReadOnlyBuffer() {
        return copy(this, this.mark, true);
    }

    public CharBuffer compact() {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        System.arraycopy(this.backingArray, this.position + this.arrayOffset, this.backingArray, this.arrayOffset, remaining());
        this.position = this.limit - this.position;
        this.limit = this.capacity;
        this.mark = -1;
        return this;
    }

    public CharBuffer duplicate() {
        return copy(this, this.mark, this.isReadOnly);
    }

    public CharBuffer slice() {
        return new CharArrayBuffer(remaining(), this.backingArray, this.arrayOffset + this.position, this.isReadOnly);
    }

    public boolean isReadOnly() {
        return this.isReadOnly;
    }

    char[] protectedArray() {
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

    public final char get() {
        if (this.position == this.limit) {
            throw new BufferUnderflowException();
        }
        char[] cArr = this.backingArray;
        int i = this.arrayOffset;
        int i2 = this.position;
        this.position = i2 + 1;
        return cArr[i + i2];
    }

    public final char get(int index) {
        checkIndex(index);
        return this.backingArray[this.arrayOffset + index];
    }

    public final CharBuffer get(char[] dst, int srcOffset, int charCount) {
        if (charCount > remaining()) {
            throw new BufferUnderflowException();
        }
        System.arraycopy(this.backingArray, this.arrayOffset + this.position, dst, srcOffset, charCount);
        this.position += charCount;
        return this;
    }

    public final boolean isDirect() {
        return false;
    }

    public final ByteOrder order() {
        return ByteOrder.nativeOrder();
    }

    public final CharBuffer subSequence(int start, int end) {
        checkStartEndRemaining(start, end);
        CharBuffer result = duplicate();
        result.limit(this.position + end);
        result.position(this.position + start);
        return result;
    }

    public CharBuffer put(char c) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        } else if (this.position == this.limit) {
            throw new BufferOverflowException();
        } else {
            char[] cArr = this.backingArray;
            int i = this.arrayOffset;
            int i2 = this.position;
            this.position = i2 + 1;
            cArr[i + i2] = c;
            return this;
        }
    }

    public CharBuffer put(int index, char c) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        checkIndex(index);
        this.backingArray[this.arrayOffset + index] = c;
        return this;
    }

    public CharBuffer put(char[] src, int srcOffset, int charCount) {
        if (this.isReadOnly) {
            throw new ReadOnlyBufferException();
        } else if (charCount > remaining()) {
            throw new BufferOverflowException();
        } else {
            System.arraycopy(src, srcOffset, this.backingArray, this.arrayOffset + this.position, charCount);
            this.position += charCount;
            return this;
        }
    }

    public final String toString() {
        return String.copyValueOf(this.backingArray, this.arrayOffset + this.position, remaining());
    }
}
