package java.nio;

import java.util.Arrays;

public abstract class IntBuffer extends Buffer implements Comparable<IntBuffer> {
    public abstract IntBuffer asReadOnlyBuffer();

    public abstract IntBuffer compact();

    public abstract IntBuffer duplicate();

    public abstract int get();

    public abstract int get(int i);

    public abstract boolean isDirect();

    public abstract ByteOrder order();

    abstract int[] protectedArray();

    abstract int protectedArrayOffset();

    abstract boolean protectedHasArray();

    public abstract IntBuffer put(int i);

    public abstract IntBuffer put(int i, int i2);

    public abstract IntBuffer slice();

    public static IntBuffer allocate(int capacity) {
        if (capacity >= 0) {
            return new IntArrayBuffer(new int[capacity]);
        }
        throw new IllegalArgumentException("capacity < 0: " + capacity);
    }

    public static IntBuffer wrap(int[] array) {
        return wrap(array, 0, array.length);
    }

    public static IntBuffer wrap(int[] array, int start, int intCount) {
        Arrays.checkOffsetAndCount(array.length, start, intCount);
        IntBuffer buf = new IntArrayBuffer(array);
        buf.position = start;
        buf.limit = start + intCount;
        return buf;
    }

    IntBuffer(int capacity, long effectiveDirectAddress) {
        super(2, capacity, effectiveDirectAddress);
    }

    public final int[] array() {
        return protectedArray();
    }

    public final int arrayOffset() {
        return protectedArrayOffset();
    }

    public int compareTo(IntBuffer otherBuffer) {
        int compareRemaining = remaining() < otherBuffer.remaining() ? remaining() : otherBuffer.remaining();
        int thisPos = this.position;
        int otherPos = otherBuffer.position;
        while (compareRemaining > 0) {
            int thisInt = get(thisPos);
            int otherInt = otherBuffer.get(otherPos);
            if (thisInt != otherInt) {
                return thisInt < otherInt ? -1 : 1;
            } else {
                thisPos++;
                otherPos++;
                compareRemaining--;
            }
        }
        return remaining() - otherBuffer.remaining();
    }

    public boolean equals(Object other) {
        if (!(other instanceof IntBuffer)) {
            return false;
        }
        IntBuffer otherBuffer = (IntBuffer) other;
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

    public IntBuffer get(int[] dst) {
        return get(dst, 0, dst.length);
    }

    public IntBuffer get(int[] dst, int dstOffset, int intCount) {
        Arrays.checkOffsetAndCount(dst.length, dstOffset, intCount);
        if (intCount > remaining()) {
            throw new BufferUnderflowException();
        }
        for (int i = dstOffset; i < dstOffset + intCount; i++) {
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

    public final IntBuffer put(int[] src) {
        return put(src, 0, src.length);
    }

    public IntBuffer put(int[] src, int srcOffset, int intCount) {
        if (isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        Arrays.checkOffsetAndCount(src.length, srcOffset, intCount);
        if (intCount > remaining()) {
            throw new BufferOverflowException();
        }
        for (int i = srcOffset; i < srcOffset + intCount; i++) {
            put(src[i]);
        }
        return this;
    }

    public IntBuffer put(IntBuffer src) {
        if (isReadOnly()) {
            throw new ReadOnlyBufferException();
        } else if (src == this) {
            throw new IllegalArgumentException("src == this");
        } else if (src.remaining() > remaining()) {
            throw new BufferOverflowException();
        } else {
            int[] contents = new int[src.remaining()];
            src.get(contents);
            put(contents);
            return this;
        }
    }
}
