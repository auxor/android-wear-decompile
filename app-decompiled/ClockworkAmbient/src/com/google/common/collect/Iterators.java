package com.google.common.collect;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class Iterators {
    static final UnmodifiableListIterator<Object> EMPTY_LIST_ITERATOR;
    private static final Iterator<Object> EMPTY_MODIFIABLE_ITERATOR;

    /* renamed from: com.google.common.collect.Iterators.11 */
    static final class AnonymousClass11 extends AbstractIndexedListIterator<T> {
        final /* synthetic */ Object[] val$array;
        final /* synthetic */ int val$offset;

        AnonymousClass11(int i, int i2, Object[] objArr, int i3) {
            this.val$array = objArr;
            this.val$offset = i3;
            super(i, i2);
            throw new VerifyError("bad dex opcode");
        }

        protected T get(int i) {
            return this.val$array[this.val$offset + i];
        }
    }

    /* renamed from: com.google.common.collect.Iterators.12 */
    static final class AnonymousClass12 extends UnmodifiableIterator<T> {
        boolean done;
        final /* synthetic */ Object val$value;

        AnonymousClass12(Object obj) {
            this.val$value = obj;
            throw new VerifyError("bad dex opcode");
        }

        public boolean hasNext() {
            return !this.done;
        }

        public T next() {
            if (this.done) {
                throw new NoSuchElementException();
            }
            this.done = true;
            return this.val$value;
        }
    }

    /* renamed from: com.google.common.collect.Iterators.3 */
    static final class AnonymousClass3 extends UnmodifiableIterator<T> {
        final /* synthetic */ Iterator val$iterator;

        AnonymousClass3(Iterator it) {
            this.val$iterator = it;
            throw new VerifyError("bad dex opcode");
        }

        public boolean hasNext() {
            return this.val$iterator.hasNext();
        }

        public T next() {
            return this.val$iterator.next();
        }
    }

    /* renamed from: com.google.common.collect.Iterators.8 */
    static final class AnonymousClass8 extends TransformedIterator<F, T> {
        final /* synthetic */ Function val$function;

        AnonymousClass8(Iterator it, Function function) {
            this.val$function = function;
            super(it);
            throw new VerifyError("bad dex opcode");
        }

        T transform(F f) {
            return this.val$function.apply(f);
        }
    }

    private static class PeekingImpl<E> implements PeekingIterator<E> {
        private boolean hasPeeked;
        private final Iterator<? extends E> iterator;
        private E peekedElement;

        public PeekingImpl(Iterator<? extends E> it) {
            Iterator it2 = (Iterator) Preconditions.checkNotNull(it);
            throw new VerifyError("bad dex opcode");
        }

        public boolean hasNext() {
            return this.hasPeeked || this.iterator.hasNext();
        }

        public E next() {
            if (!this.hasPeeked) {
                return this.iterator.next();
            }
            E e = this.peekedElement;
            this.hasPeeked = false;
            this.peekedElement = null;
            return e;
        }

        public E peek() {
            if (!this.hasPeeked) {
                this.peekedElement = this.iterator.next();
                this.hasPeeked = true;
            }
            return this.peekedElement;
        }

        public void remove() {
            Preconditions.checkState(!this.hasPeeked, "Can't remove after you've peeked at next");
            this.iterator.remove();
        }
    }

    static {
        EMPTY_LIST_ITERATOR = new UnmodifiableListIterator<Object>() {
            public boolean hasNext() {
                return false;
            }

            public boolean hasPrevious() {
                return false;
            }

            public Object next() {
                throw new NoSuchElementException();
            }

            public int nextIndex() {
                return 0;
            }

            public Object previous() {
                throw new NoSuchElementException();
            }

            public int previousIndex() {
                return -1;
            }
        };
        EMPTY_MODIFIABLE_ITERATOR = new Iterator<Object>() {
            public boolean hasNext() {
                return false;
            }

            public Object next() {
                throw new NoSuchElementException();
            }

            public void remove() {
                CollectPreconditions.checkRemove(false);
            }
        };
    }

    public static <T> boolean addAll(Collection<T> collection, Iterator<? extends T> it) {
        Preconditions.checkNotNull(collection);
        Preconditions.checkNotNull(it);
        boolean z = false;
        while (it.hasNext()) {
            z |= collection.add(it.next());
        }
        return z;
    }

    public static <T> boolean any(Iterator<T> it, Predicate<? super T> predicate) {
        return indexOf(it, predicate) != -1;
    }

    static void clear(Iterator<?> it) {
        Preconditions.checkNotNull(it);
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
    }

    public static boolean contains(Iterator<?> it, Object obj) {
        return any(it, Predicates.equalTo(obj));
    }

    public static boolean elementsEqual(Iterator<?> it, Iterator<?> it2) {
        while (it.hasNext()) {
            if (!it2.hasNext()) {
                return false;
            }
            if (!Objects.equal(it.next(), it2.next())) {
                return false;
            }
        }
        return !it2.hasNext();
    }

    public static <T> UnmodifiableIterator<T> emptyIterator() {
        return emptyListIterator();
    }

    static <T> UnmodifiableListIterator<T> emptyListIterator() {
        return EMPTY_LIST_ITERATOR;
    }

    static <T> Iterator<T> emptyModifiableIterator() {
        return EMPTY_MODIFIABLE_ITERATOR;
    }

    public static <T> UnmodifiableIterator<T> forArray(T... tArr) {
        return forArray(tArr, 0, tArr.length, 0);
    }

    static <T> UnmodifiableListIterator<T> forArray(T[] tArr, int i, int i2, int i3) {
        Preconditions.checkArgument(i2 >= 0);
        Preconditions.checkPositionIndexes(i, i + i2, tArr.length);
        Preconditions.checkPositionIndex(i3, i2);
        return i2 == 0 ? emptyListIterator() : new AnonymousClass11(i2, i3, tArr, i);
    }

    public static <T> T getOnlyElement(Iterator<T> it) {
        T next = it.next();
        if (!it.hasNext()) {
            return next;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("expected one element but was: <" + next);
        for (int i = 0; i < 4 && it.hasNext(); i++) {
            stringBuilder.append(", " + it.next());
        }
        if (it.hasNext()) {
            stringBuilder.append(", ...");
        }
        stringBuilder.append('>');
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public static <T> int indexOf(Iterator<T> it, Predicate<? super T> predicate) {
        Preconditions.checkNotNull(predicate, "predicate");
        int i = 0;
        while (it.hasNext()) {
            if (predicate.apply(it.next())) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public static <T> PeekingIterator<T> peekingIterator(Iterator<? extends T> it) {
        return it instanceof PeekingImpl ? it : new PeekingImpl(it);
    }

    public static boolean removeAll(Iterator<?> it, Collection<?> collection) {
        return removeIf(it, Predicates.in(collection));
    }

    public static <T> boolean removeIf(Iterator<T> it, Predicate<? super T> predicate) {
        Preconditions.checkNotNull(predicate);
        boolean z = false;
        while (it.hasNext()) {
            if (predicate.apply(it.next())) {
                it.remove();
                z = true;
            }
        }
        return z;
    }

    public static <T> UnmodifiableIterator<T> singletonIterator(T t) {
        return new AnonymousClass12(t);
    }

    public static <F, T> Iterator<T> transform(Iterator<F> it, Function<? super F, ? extends T> function) {
        Preconditions.checkNotNull(function);
        return new AnonymousClass8(it, function);
    }

    public static <T> UnmodifiableIterator<T> unmodifiableIterator(Iterator<T> it) {
        Preconditions.checkNotNull(it);
        return it instanceof UnmodifiableIterator ? it : new AnonymousClass3(it);
    }
}
