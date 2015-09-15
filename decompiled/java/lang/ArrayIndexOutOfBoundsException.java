package java.lang;

public class ArrayIndexOutOfBoundsException extends IndexOutOfBoundsException {
    private static final long serialVersionUID = -5116101128118950844L;

    public ArrayIndexOutOfBoundsException(int index) {
        super("index=" + index);
    }

    public ArrayIndexOutOfBoundsException(String detailMessage) {
        super(detailMessage);
    }

    public ArrayIndexOutOfBoundsException(int sourceLength, int index) {
        super("length=" + sourceLength + "; index=" + index);
    }

    public ArrayIndexOutOfBoundsException(int sourceLength, int offset, int count) {
        super("length=" + sourceLength + "; regionStart=" + offset + "; regionLength=" + count);
    }
}
