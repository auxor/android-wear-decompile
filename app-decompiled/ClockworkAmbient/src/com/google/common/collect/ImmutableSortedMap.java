package com.google.common.collect;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.SortedMap;

public abstract class ImmutableSortedMap<K, V> extends ImmutableSortedMapFauxverideShim<K, V> implements SortedMap<K, V> {
    private static final ImmutableSortedMap<Comparable, Object> NATURAL_EMPTY_MAP;
    private static final Comparator<Comparable> NATURAL_ORDER;
    private static final long serialVersionUID = 0;

    public static class Builder<K, V> extends com.google.common.collect.ImmutableMap.Builder<K, V> {
        private final Comparator<? super K> comparator;

        public Builder(Comparator<? super K> comparator) {
            Comparator comparator2 = (Comparator) Preconditions.checkNotNull(comparator);
            throw new VerifyError("bad dex opcode");
        }

        public ImmutableSortedMap<K, V> build() {
            return ImmutableSortedMap.fromEntries(this.comparator, false, this.size, this.entries);
        }

        public Builder<K, V> put(K k, V v) {
            super.put(k, v);
            return this;
        }
    }

    private static class SerializedForm extends SerializedForm {
        private static final long serialVersionUID = 0;
        private final Comparator<Object> comparator;

        SerializedForm(ImmutableSortedMap<?, ?> immutableSortedMap) {
            super(immutableSortedMap);
            this.comparator = immutableSortedMap.comparator();
            throw new VerifyError("bad dex opcode");
        }

        Object readResolve() {
            return createMap(new Builder(this.comparator));
        }
    }

    static {
        NATURAL_ORDER = Ordering.natural();
        NATURAL_EMPTY_MAP = new EmptyImmutableSortedMap(NATURAL_ORDER);
    }

    ImmutableSortedMap() {
    }

    static <K, V> ImmutableSortedMap<K, V> emptyMap(Comparator<? super K> comparator) {
        return Ordering.natural().equals(comparator) ? of() : new EmptyImmutableSortedMap(comparator);
    }

    static <K, V> ImmutableSortedMap<K, V> from(ImmutableSortedSet<K> immutableSortedSet, ImmutableList<V> immutableList) {
        throw new VerifyError("bad dex opcode");
    }

    static <K, V> ImmutableSortedMap<K, V> fromEntries(Comparator<? super K> comparator, boolean z, int i, Entry<K, V>... entryArr) {
        for (int i2 = 0; i2 < i; i2++) {
            Entry entry = entryArr[i2];
            entryArr[i2] = ImmutableMap.entryOf(entry.getKey(), entry.getValue());
        }
        if (!z) {
            sortEntries(comparator, i, entryArr);
            validateEntries(i, entryArr, comparator);
        }
        return fromSortedEntries(comparator, i, entryArr);
    }

    static <K, V> ImmutableSortedMap<K, V> fromSortedEntries(Comparator<? super K> comparator, int i, Entry<K, V>[] entryArr) {
        if (i == 0) {
            return emptyMap(comparator);
        }
        com.google.common.collect.ImmutableList.Builder builder = ImmutableList.builder();
        com.google.common.collect.ImmutableList.Builder builder2 = ImmutableList.builder();
        for (int i2 = 0; i2 < i; i2++) {
            Entry entry = entryArr[i2];
            builder.add(entry.getKey());
            builder2.add(entry.getValue());
        }
        return new RegularImmutableSortedMap(new RegularImmutableSortedSet(builder.build(), comparator), builder2.build());
    }

    public static <K, V> ImmutableSortedMap<K, V> of() {
        return NATURAL_EMPTY_MAP;
    }

    private static <K, V> void sortEntries(Comparator<? super K> comparator, int i, Entry<K, V>[] entryArr) {
        Arrays.sort(entryArr, 0, i, Ordering.from(comparator).onKeys());
    }

    private static <K, V> void validateEntries(int i, Entry<K, V>[] entryArr, Comparator<? super K> comparator) {
        for (int i2 = 1; i2 < i; i2++) {
            ImmutableMap.checkNoConflict(comparator.compare(entryArr[i2 + -1].getKey(), entryArr[i2].getKey()) != 0, "key", entryArr[i2 - 1], entryArr[i2]);
        }
    }

    public Comparator<? super K> comparator() {
        return keySet().comparator();
    }

    public boolean containsValue(Object obj) {
        return values().contains(obj);
    }

    public ImmutableSet<Entry<K, V>> entrySet() {
        return super.entrySet();
    }

    public K firstKey() {
        return keySet().first();
    }

    public ImmutableSortedMap<K, V> headMap(K k) {
        return headMap(k, false);
    }

    public abstract ImmutableSortedMap<K, V> headMap(K k, boolean z);

    boolean isPartialView() {
        return keySet().isPartialView() || values().isPartialView();
    }

    public abstract ImmutableSortedSet<K> keySet();

    public K lastKey() {
        return keySet().last();
    }

    public int size() {
        return values().size();
    }

    public ImmutableSortedMap<K, V> subMap(K k, K k2) {
        return subMap(k, true, k2, false);
    }

    public ImmutableSortedMap<K, V> subMap(K k, boolean z, K k2, boolean z2) {
        Preconditions.checkNotNull(k);
        Preconditions.checkNotNull(k2);
        throw new VerifyError("bad dex opcode");
    }

    public ImmutableSortedMap<K, V> tailMap(K k) {
        return tailMap(k, true);
    }

    public abstract ImmutableSortedMap<K, V> tailMap(K k, boolean z);

    public abstract ImmutableCollection<V> values();

    Object writeReplace() {
        return new SerializedForm(this);
    }
}
