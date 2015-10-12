package com.google.common.collect;

import java.util.Comparator;

final class ImmutableSortedAsList<E> extends RegularImmutableAsList<E> implements SortedIterable<E> {
    ImmutableSortedAsList(ImmutableSortedSet<E> immutableSortedSet, ImmutableList<E> immutableList) {
        super((ImmutableCollection) immutableSortedSet, (ImmutableList) immutableList);
    }

    public Comparator<? super E> comparator() {
        return delegateCollection().comparator();
    }

    public boolean contains(Object obj) {
        return indexOf(obj) >= 0;
    }

    ImmutableSortedSet<E> delegateCollection() {
        return (ImmutableSortedSet) super.delegateCollection();
    }

    public int indexOf(Object obj) {
        int indexOf = delegateCollection().indexOf(obj);
        return (indexOf < 0 || !get(indexOf).equals(obj)) ? -1 : indexOf;
    }

    public int lastIndexOf(Object obj) {
        return indexOf(obj);
    }

    ImmutableList<E> subListUnchecked(int i, int i2) {
        return new RegularImmutableSortedSet(super.subListUnchecked(i, i2), comparator()).asList();
    }
}
