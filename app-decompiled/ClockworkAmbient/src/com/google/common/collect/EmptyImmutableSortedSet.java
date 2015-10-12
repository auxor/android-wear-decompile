package com.google.common.collect;

import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Set;

class EmptyImmutableSortedSet<E> extends ImmutableSortedSet<E> {
    EmptyImmutableSortedSet(Comparator<? super E> comparator) {
        super(comparator);
    }

    public ImmutableList<E> asList() {
        return ImmutableList.of();
    }

    public boolean contains(Object obj) {
        return false;
    }

    public boolean containsAll(Collection<?> collection) {
        return collection.isEmpty();
    }

    int copyIntoArray(Object[] objArr, int i) {
        return i;
    }

    public UnmodifiableIterator<E> descendingIterator() {
        return Iterators.emptyIterator();
    }

    public boolean equals(Object obj) {
        return obj instanceof Set ? ((Set) obj).isEmpty() : false;
    }

    public E first() {
        throw new NoSuchElementException();
    }

    public int hashCode() {
        return 0;
    }

    ImmutableSortedSet<E> headSetImpl(E e, boolean z) {
        return this;
    }

    int indexOf(Object obj) {
        return -1;
    }

    public boolean isEmpty() {
        return true;
    }

    boolean isPartialView() {
        return false;
    }

    public UnmodifiableIterator<E> iterator() {
        return Iterators.emptyIterator();
    }

    public E last() {
        throw new NoSuchElementException();
    }

    public int size() {
        return 0;
    }

    ImmutableSortedSet<E> subSetImpl(E e, boolean z, E e2, boolean z2) {
        return this;
    }

    ImmutableSortedSet<E> tailSetImpl(E e, boolean z) {
        return this;
    }

    public String toString() {
        return "[]";
    }
}
