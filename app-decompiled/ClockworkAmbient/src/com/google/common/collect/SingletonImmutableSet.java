package com.google.common.collect;

import com.google.common.base.Preconditions;
import java.util.Set;

final class SingletonImmutableSet<E> extends ImmutableSet<E> {
    private transient int cachedHashCode;
    final transient E element;

    SingletonImmutableSet(E e) {
        this.element = Preconditions.checkNotNull(e);
        throw new VerifyError("bad dex opcode");
    }

    SingletonImmutableSet(E e, int i) {
        this.element = e;
        this.cachedHashCode = i;
        throw new VerifyError("bad dex opcode");
    }

    public boolean contains(Object obj) {
        return this.element.equals(obj);
    }

    int copyIntoArray(Object[] objArr, int i) {
        throw new VerifyError("bad dex opcode");
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Set)) {
            return false;
        }
        if (((Set) obj).size() != 1) {
            return false;
        }
        throw new VerifyError("bad dex opcode");
    }

    public final int hashCode() {
        int i = this.cachedHashCode;
        if (i != 0) {
            return i;
        }
        i = this.element.hashCode();
        this.cachedHashCode = i;
        return i;
    }

    public boolean isEmpty() {
        return false;
    }

    boolean isHashCodeFast() {
        return this.cachedHashCode != 0;
    }

    boolean isPartialView() {
        return false;
    }

    public UnmodifiableIterator<E> iterator() {
        return Iterators.singletonIterator(this.element);
    }

    public int size() {
        return 1;
    }

    public String toString() {
        String obj = this.element.toString();
        return new StringBuilder(obj.length() + 2).append('[').append(obj).append(']').toString();
    }
}
