package libcore.util;

import java.lang.ref.Reference;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public final class CollectionUtils {

    /* renamed from: libcore.util.CollectionUtils.1 */
    static class AnonymousClass1 implements Iterable<T> {
        final /* synthetic */ Iterable val$iterable;
        final /* synthetic */ boolean val$trim;

        AnonymousClass1(Iterable iterable, boolean z) {
            this.val$iterable = iterable;
            this.val$trim = z;
        }

        public Iterator<T> iterator() {
            return new Iterator<T>() {
                private final Iterator<? extends Reference<T>> delegate;
                private T next;
                private boolean removeIsOkay;

                {
                    this.delegate = AnonymousClass1.this.val$iterable.iterator();
                }

                private void computeNext() {
                    this.removeIsOkay = false;
                    while (this.next == null && this.delegate.hasNext()) {
                        this.next = ((Reference) this.delegate.next()).get();
                        if (AnonymousClass1.this.val$trim && this.next == null) {
                            this.delegate.remove();
                        }
                    }
                }

                public boolean hasNext() {
                    computeNext();
                    return this.next != null;
                }

                public T next() {
                    if (hasNext()) {
                        T result = this.next;
                        this.removeIsOkay = true;
                        this.next = null;
                        return result;
                    }
                    throw new IllegalStateException();
                }

                public void remove() {
                    if (this.removeIsOkay) {
                        this.delegate.remove();
                        return;
                    }
                    throw new IllegalStateException();
                }
            };
        }
    }

    private CollectionUtils() {
    }

    public static <T> Iterable<T> dereferenceIterable(Iterable<? extends Reference<T>> iterable, boolean trim) {
        return new AnonymousClass1(iterable, trim);
    }

    public static <T> void removeDuplicates(List<T> list, Comparator<? super T> comparator) {
        Collections.sort(list, comparator);
        int j = 1;
        for (int i = 1; i < list.size(); i++) {
            if (comparator.compare(list.get(j - 1), list.get(i)) != 0) {
                int j2 = j + 1;
                list.set(j, list.get(i));
                j = j2;
            }
        }
        if (j < list.size()) {
            list.subList(j, list.size()).clear();
        }
    }
}
