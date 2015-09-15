package java.nio.charset;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.util.WeakHashMap;
import org.xmlpull.v1.XmlPullParser;

public class CoderResult {
    public static final CoderResult OVERFLOW;
    private static final int TYPE_MALFORMED_INPUT = 3;
    private static final int TYPE_OVERFLOW = 2;
    private static final int TYPE_UNDERFLOW = 1;
    private static final int TYPE_UNMAPPABLE_CHAR = 4;
    public static final CoderResult UNDERFLOW;
    private static WeakHashMap<Integer, CoderResult> _malformedErrors;
    private static WeakHashMap<Integer, CoderResult> _unmappableErrors;
    private final int length;
    private final int type;

    static {
        UNDERFLOW = new CoderResult(TYPE_UNDERFLOW, 0);
        OVERFLOW = new CoderResult(TYPE_OVERFLOW, 0);
        _malformedErrors = new WeakHashMap();
        _unmappableErrors = new WeakHashMap();
    }

    private CoderResult(int type, int length) {
        this.type = type;
        this.length = length;
    }

    public static synchronized CoderResult malformedForLength(int length) throws IllegalArgumentException {
        CoderResult r;
        synchronized (CoderResult.class) {
            if (length > 0) {
                Integer key = Integer.valueOf(length);
                synchronized (_malformedErrors) {
                    r = (CoderResult) _malformedErrors.get(key);
                    if (r == null) {
                        r = new CoderResult(TYPE_MALFORMED_INPUT, length);
                        _malformedErrors.put(key, r);
                    }
                }
            } else {
                throw new IllegalArgumentException("length <= 0: " + length);
            }
        }
        return r;
    }

    public static synchronized CoderResult unmappableForLength(int length) throws IllegalArgumentException {
        CoderResult r;
        synchronized (CoderResult.class) {
            if (length > 0) {
                Integer key = Integer.valueOf(length);
                synchronized (_unmappableErrors) {
                    r = (CoderResult) _unmappableErrors.get(key);
                    if (r == null) {
                        r = new CoderResult(TYPE_UNMAPPABLE_CHAR, length);
                        _unmappableErrors.put(key, r);
                    }
                }
            } else {
                throw new IllegalArgumentException("length <= 0: " + length);
            }
        }
        return r;
    }

    public boolean isUnderflow() {
        return this.type == TYPE_UNDERFLOW;
    }

    public boolean isError() {
        return this.type == TYPE_MALFORMED_INPUT || this.type == TYPE_UNMAPPABLE_CHAR;
    }

    public boolean isMalformed() {
        return this.type == TYPE_MALFORMED_INPUT;
    }

    public boolean isOverflow() {
        return this.type == TYPE_OVERFLOW;
    }

    public boolean isUnmappable() {
        return this.type == TYPE_UNMAPPABLE_CHAR;
    }

    public int length() throws UnsupportedOperationException {
        if (this.type == TYPE_MALFORMED_INPUT || this.type == TYPE_UNMAPPABLE_CHAR) {
            return this.length;
        }
        throw new UnsupportedOperationException("length meaningless for " + toString());
    }

    public void throwException() throws BufferUnderflowException, BufferOverflowException, UnmappableCharacterException, MalformedInputException, CharacterCodingException {
        switch (this.type) {
            case TYPE_UNDERFLOW /*1*/:
                throw new BufferUnderflowException();
            case TYPE_OVERFLOW /*2*/:
                throw new BufferOverflowException();
            case TYPE_MALFORMED_INPUT /*3*/:
                throw new MalformedInputException(this.length);
            case TYPE_UNMAPPABLE_CHAR /*4*/:
                throw new UnmappableCharacterException(this.length);
            default:
                throw new CharacterCodingException();
        }
    }

    public String toString() {
        String dsc;
        switch (this.type) {
            case TYPE_UNDERFLOW /*1*/:
                dsc = "UNDERFLOW error";
                break;
            case TYPE_OVERFLOW /*2*/:
                dsc = "OVERFLOW error";
                break;
            case TYPE_MALFORMED_INPUT /*3*/:
                dsc = "Malformed-input error with erroneous input length " + this.length;
                break;
            case TYPE_UNMAPPABLE_CHAR /*4*/:
                dsc = "Unmappable-character error with erroneous input length " + this.length;
                break;
            default:
                dsc = XmlPullParser.NO_NAMESPACE;
                break;
        }
        return getClass().getName() + "[" + dsc + "]";
    }
}
