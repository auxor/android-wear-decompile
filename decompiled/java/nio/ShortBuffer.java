package java.nio;

import java.util.Arrays;

public abstract class ShortBuffer extends Buffer implements Comparable<ShortBuffer> {
    public abstract ShortBuffer asReadOnlyBuffer();

    public abstract ShortBuffer compact();

    public abstract ShortBuffer duplicate();

    public abstract short get();

    public abstract short get(int i);

    public abstract boolean isDirect();

    public abstract ByteOrder order();

    abstract short[] protectedArray();

    abstract int protectedArrayOffset();

    abstract boolean protectedHasArray();

    public abstract ShortBuffer put(int i, short s);

    public abstract ShortBuffer put(short s);

    public abstract ShortBuffer slice();

    public static ShortBuffer allocate(int capacity) {
        if (capacity >= 0) {
            return new ShortArrayBuffer(new short[capacity]);
        }
        throw new IllegalArgumentException("capacity < 0: " + capacity);
    }

    public static ShortBuffer wrap(short[] array) {
        return wrap(array, 0, array.length);
    }

    public static ShortBuffer wrap(short[] array, int start, int shortCount) {
        Arrays.checkOffsetAndCount(array.length, start, shortCount);
        ShortBuffer buf = new ShortArrayBuffer(array);
        buf.position = start;
        buf.limit = start + shortCount;
        return buf;
    }

    ShortBuffer(int capacity, long effectiveDirectAddress) {
        super(1, capacity, effectiveDirectAddress);
    }

    public final short[] array() {
        return protectedArray();
    }

    public final int arrayOffset() {
        return protectedArrayOffset();
    }

    public int compareTo(ShortBuffer otherBuffer) {
        int compareRemaining = remaining() < otherBuffer.remaining() ? remaining() : otherBuffer.remaining();
        int thisPos = this.position;
        int otherPos = otherBuffer.position;
        while (compareRemaining > 0) {
            short thisByte = get(thisPos);
            short otherByte = otherBuffer.get(otherPos);
            if (thisByte != otherByte) {
                return thisByte < otherByte ? -1 : 1;
            } else {
                thisPos++;
                otherPos++;
                compareRemaining--;
            }
        }
        return remaining() - otherBuffer.remaining();
    }

    public boolean equals(Object other) {
        if (!(other instanceof ShortBuffer)) {
            return false;
        }
        ShortBuffer otherBuffer = (ShortBuffer) other;
        if (remaining() != otherBuffer.remaining()) {
            return false;
        }
        int myPosition = this.position;
        boolean equalSoFar = true;
        int otherPosition = otherBuffer.position;
        int myPosition2 = myPosition;
        while (equalSoFar && myPosition2 < this.limit) {
            myPosition = myPosition2 + 1;
            int otherPosition2 = otherPosition + 1;
            equalSoFar = get(myPosition2) == otherBuffer.get(otherPosition);
            otherPosition = otherPosition2;
            myPosition2 = myPosition;
        }
        return equalSoFar;
    }

    public ShortBuffer get(short[] dst) {
        return get(dst, 0, dst.length);
    }

    public ShortBuffer get(short[] dst, int dstOffset, int shortCount) {
        Arrays.checkOffsetAndCount(dst.length, dstOffset, shortCount);
        if (shortCount > remaining()) {
            throw new BufferUnderflowException();
        }
        for (int i = dstOffset; i < dstOffset + shortCount; i++) {
            dst[i] = get();
        }
        return this;
    }

    public final boolean hasArray() {
        return protectedHasArray();
    }

    public int hashCode() {
        int hash = 0;
        for (int myPosition = this.position; myPosition < this.limit; myPosition++) {
            hash += get(myPosition);
        }
        return hash;
    }

    public final ShortBuffer put(short[] src) {
        return put(src, 0, src.length);
    }

    public ShortBuffer put(short[] src, int srcOffset, int shortCount) {
        Arrays.checkOffsetAndCount(src.length, srcOffset, shortCount);
        if (shortCount > remaining()) {
            throw new BufferOverflowException();
        }
        for (int i = srcOffset; i < srcOffset + shortCount; i++) {
            put(src[i]);
        }
        return this;
    }

    public ShortBuffer put(ShortBuffer src) {
        if (isReadOnly()) {
            throw new ReadOnlyBufferException();
        } else if (src == this) {
            throw new IllegalArgumentException("src == this");
        } else if (src.remaining() > remaining()) {
            throw new BufferOverflowException();
        } else {
            short[] contents = new short[src.remaining()];
            src.get(contents);
            put(contents);
            return this;
        }
    }
}
