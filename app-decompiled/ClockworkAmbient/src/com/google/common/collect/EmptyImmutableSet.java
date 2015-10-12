package com.google.common.collect;

import java.util.Collection;
import java.util.Set;

final class EmptyImmutableSet extends ImmutableSet<Object> {
    static final EmptyImmutableSet INSTANCE;
    private static final long serialVersionUID = 0;

    static {
        INSTANCE = new EmptyImmutableSet();
    }

    private EmptyImmutableSet() {
    }

    public ImmutableList<Object> asList() {
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

    public boolean equals(Object obj) {
        return obj instanceof Set ? ((Set) obj).isEmpty() : false;
    }

    public final int hashCode() {
        return 0;
    }

    public boolean isEmpty() {
        return true;
    }

    boolean isHashCodeFast() {
        return true;
    }

    boolean isPartialView() {
        return false;
    }

    public UnmodifiableIterator<Object> iterator() {
        return Iterators.emptyIterator();
    }

    Object readResolve() {
        return INSTANCE;
    }

    public int size() {
        return 0;
    }

    public String toString() {
        return "[]";
    }
}
