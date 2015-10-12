package com.google.common.collect;

public final class Iterables {
    public static <T> T getOnlyElement(Iterable<T> iterable) {
        return Iterators.getOnlyElement(iterable.iterator());
    }
}
