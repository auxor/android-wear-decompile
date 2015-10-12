package com.google.common.collect;

import com.google.common.base.Preconditions;
import java.util.List;

final class SingletonImmutableList<E> extends ImmutableList<E> {
    final transient E element;

    SingletonImmutableList(E e) {
        this.element = Preconditions.checkNotNull(e);
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
        if (!(obj instanceof List)) {
            return false;
        }
        if (((List) obj).size() != 1) {
            return false;
        }
        throw new VerifyError("bad dex opcode");
    }

    public E get(int i) {
        Preconditions.checkElementIndex(i, 1);
        return this.element;
    }

    public int hashCode() {
        return this.element.hashCode() + 31;
    }

    public int indexOf(Object obj) {
        return this.element.equals(obj) ? 0 : -1;
    }

    public boolean isEmpty() {
        return false;
    }

    boolean isPartialView() {
        return false;
    }

    public UnmodifiableIterator<E> iterator() {
        return Iterators.singletonIterator(this.element);
    }

    public int lastIndexOf(Object obj) {
        return indexOf(obj);
    }

    public ImmutableList<E> reverse() {
        return this;
    }

    public int size() {
        return 1;
    }

    public ImmutableList<E> subList(int i, int i2) {
        Preconditions.checkPositionIndexes(i, i2, 1);
        return i == i2 ? ImmutableList.of() : this;
    }

    public String toString() {
        String obj = this.element.toString();
        return new StringBuilder(obj.length() + 2).append('[').append(obj).append(']').toString();
    }
}
