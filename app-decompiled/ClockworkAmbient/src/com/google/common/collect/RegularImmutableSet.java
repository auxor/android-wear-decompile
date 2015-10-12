package com.google.common.collect;

final class RegularImmutableSet<E> extends ImmutableSet<E> {
    private final Object[] elements;
    private final transient int hashCode;
    private final transient int mask;
    final transient Object[] table;

    RegularImmutableSet(Object[] objArr, int i, Object[] objArr2, int i2) {
        this.elements = objArr;
        this.table = objArr2;
        this.mask = i2;
        this.hashCode = i;
        throw new VerifyError("bad dex opcode");
    }

    public boolean contains(Object obj) {
        if (obj != null) {
            int smear = Hashing.smear(obj.hashCode());
            while (true) {
                Object obj2 = this.table[this.mask & smear];
                if (obj2 == null) {
                    break;
                } else if (obj2.equals(obj)) {
                    return true;
                } else {
                    smear++;
                }
            }
        }
        return false;
    }

    int copyIntoArray(Object[] objArr, int i) {
        System.arraycopy(this.elements, 0, objArr, i, this.elements.length);
        return this.elements.length + i;
    }

    ImmutableList<E> createAsList() {
        return new RegularImmutableAsList((ImmutableCollection) this, this.elements);
    }

    public int hashCode() {
        return this.hashCode;
    }

    boolean isHashCodeFast() {
        return true;
    }

    boolean isPartialView() {
        return false;
    }

    public UnmodifiableIterator<E> iterator() {
        return Iterators.forArray(this.elements);
    }

    public int size() {
        return this.elements.length;
    }
}
