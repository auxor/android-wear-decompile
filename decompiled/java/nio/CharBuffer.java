package java.nio;

import java.io.IOException;
import java.util.Arrays;

public abstract class CharBuffer extends Buffer implements Comparable<CharBuffer>, CharSequence, Appendable, Readable {
    public abstract CharBuffer asReadOnlyBuffer();

    public abstract CharBuffer compact();

    public abstract CharBuffer duplicate();

    public abstract char get();

    public abstract char get(int i);

    public abstract boolean isDirect();

    public abstract ByteOrder order();

    abstract char[] protectedArray();

    abstract int protectedArrayOffset();

    abstract boolean protectedHasArray();

    public abstract CharBuffer put(char c);

    public abstract CharBuffer put(int i, char c);

    public abstract CharBuffer slice();

    public abstract CharBuffer subSequence(int i, int i2);

    public static CharBuffer allocate(int capacity) {
        if (capacity >= 0) {
            return new CharArrayBuffer(new char[capacity]);
        }
        throw new IllegalArgumentException("capacity < 0: " + capacity);
    }

    public static CharBuffer wrap(char[] array) {
        return wrap(array, 0, array.length);
    }

    public static CharBuffer wrap(char[] array, int start, int charCount) {
        Arrays.checkOffsetAndCount(array.length, start, charCount);
        CharBuffer buf = new CharArrayBuffer(array);
        buf.position = start;
        buf.limit = start + charCount;
        return buf;
    }

    public static CharBuffer wrap(CharSequence chseq) {
        return new CharSequenceAdapter(chseq);
    }

    public static CharBuffer wrap(CharSequence cs, int start, int end) {
        if (start < 0 || end < start || end > cs.length()) {
            throw new IndexOutOfBoundsException("cs.length()=" + cs.length() + ", start=" + start + ", end=" + end);
        }
        CharBuffer result = new CharSequenceAdapter(cs);
        result.position = start;
        result.limit = end;
        return result;
    }

    CharBuffer(int capacity, long effectiveDirectAddress) {
        super(1, capacity, effectiveDirectAddress);
    }

    public final char[] array() {
        return protectedArray();
    }

    public final int arrayOffset() {
        return protectedArrayOffset();
    }

    public final char charAt(int index) {
        if (index >= 0 && index < remaining()) {
            return get(this.position + index);
        }
        throw new IndexOutOfBoundsException("index=" + index + ", remaining()=" + remaining());
    }

    public int compareTo(CharBuffer otherBuffer) {
        int compareRemaining = remaining() < otherBuffer.remaining() ? remaining() : otherBuffer.remaining();
        int thisPos = this.position;
        int otherPos = otherBuffer.position;
        while (compareRemaining > 0) {
            char thisByte = get(thisPos);
            char otherByte = otherBuffer.get(otherPos);
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
        if (!(other instanceof CharBuffer)) {
            return false;
        }
        CharBuffer otherBuffer = (CharBuffer) other;
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

    public CharBuffer get(char[] dst) {
        return get(dst, 0, dst.length);
    }

    public CharBuffer get(char[] dst, int dstOffset, int charCount) {
        Arrays.checkOffsetAndCount(dst.length, dstOffset, charCount);
        if (charCount > remaining()) {
            throw new BufferUnderflowException();
        }
        for (int i = dstOffset; i < dstOffset + charCount; i++) {
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

    public final int length() {
        return remaining();
    }

    public final CharBuffer put(char[] src) {
        return put(src, 0, src.length);
    }

    public CharBuffer put(char[] src, int srcOffset, int charCount) {
        Arrays.checkOffsetAndCount(src.length, srcOffset, charCount);
        if (charCount > remaining()) {
            throw new BufferOverflowException();
        }
        for (int i = srcOffset; i < srcOffset + charCount; i++) {
            put(src[i]);
        }
        return this;
    }

    public CharBuffer put(CharBuffer src) {
        if (isReadOnly()) {
            throw new ReadOnlyBufferException();
        } else if (src == this) {
            throw new IllegalArgumentException("src == this");
        } else if (src.remaining() > remaining()) {
            throw new BufferOverflowException();
        } else {
            char[] contents = new char[src.remaining()];
            src.get(contents);
            put(contents);
            return this;
        }
    }

    public final CharBuffer put(String str) {
        return put(str, 0, str.length());
    }

    public CharBuffer put(String str, int start, int end) {
        if (isReadOnly()) {
            throw new ReadOnlyBufferException();
        } else if (start < 0 || end < start || end > str.length()) {
            throw new IndexOutOfBoundsException("str.length()=" + str.length() + ", start=" + start + ", end=" + end);
        } else if (end - start > remaining()) {
            throw new BufferOverflowException();
        } else {
            for (int i = start; i < end; i++) {
                put(str.charAt(i));
            }
            return this;
        }
    }

    public String toString() {
        StringBuilder result = new StringBuilder(this.limit - this.position);
        for (int i = this.position; i < this.limit; i++) {
            result.append(get(i));
        }
        return result.toString();
    }

    public CharBuffer append(char c) {
        return put(c);
    }

    public CharBuffer append(CharSequence csq) {
        if (csq != null) {
            return put(csq.toString());
        }
        return put("null");
    }

    public CharBuffer append(CharSequence csq, int start, int end) {
        if (csq == null) {
            csq = "null";
        }
        CharSequence cs = csq.subSequence(start, end);
        if (cs.length() > 0) {
            return put(cs.toString());
        }
        return this;
    }

    public int read(CharBuffer target) throws IOException {
        int remaining = remaining();
        if (target == this) {
            if (remaining == 0) {
                return -1;
            }
            throw new IllegalArgumentException("target == this");
        } else if (remaining != 0) {
            remaining = Math.min(target.remaining(), remaining);
            if (remaining > 0) {
                char[] chars = new char[remaining];
                get(chars);
                target.put(chars);
            }
            return remaining;
        } else if (this.limit <= 0 || target.remaining() != 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
