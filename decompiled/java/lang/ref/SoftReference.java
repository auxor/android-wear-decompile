package java.lang.ref;

public class SoftReference<T> extends Reference<T> {
    public SoftReference(T r) {
        super(r, null);
    }

    public SoftReference(T r, ReferenceQueue<? super T> q) {
        super(r, q);
    }
}
