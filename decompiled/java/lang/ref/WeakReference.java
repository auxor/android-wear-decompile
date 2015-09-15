package java.lang.ref;

public class WeakReference<T> extends Reference<T> {
    public WeakReference(T r) {
        super(r, null);
    }

    public WeakReference(T r, ReferenceQueue<? super T> q) {
        super(r, q);
    }
}
