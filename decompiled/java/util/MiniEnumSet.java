package java.util;

final class MiniEnumSet<E extends Enum<E>> extends EnumSet<E> {
    private static final int MAX_ELEMENTS = 64;
    private long bits;
    private final E[] enums;
    private int size;

    private class MiniEnumSetIterator implements Iterator<E> {
        private long currentBits;
        private E last;
        private long mask;

        private MiniEnumSetIterator() {
            this.currentBits = MiniEnumSet.this.bits;
            this.mask = this.currentBits & (-this.currentBits);
        }

        public boolean hasNext() {
            return this.mask != 0;
        }

        public E next() {
            if (this.mask == 0) {
                throw new NoSuchElementException();
            }
            this.last = MiniEnumSet.this.enums[Long.numberOfTrailingZeros(this.mask)];
            this.currentBits &= this.mask ^ -1;
            this.mask = this.currentBits & (-this.currentBits);
            return this.last;
        }

        public void remove() {
            if (this.last == null) {
                throw new IllegalStateException();
            }
            MiniEnumSet.this.remove(this.last);
            this.last = null;
        }
    }

    MiniEnumSet(Class<E> elementType, E[] enums) {
        super(elementType);
        this.enums = enums;
    }

    public Iterator<E> iterator() {
        return new MiniEnumSetIterator();
    }

    public int size() {
        return this.size;
    }

    public void clear() {
        this.bits = 0;
        this.size = 0;
    }

    public boolean add(E element) {
        this.elementClass.cast(element);
        long oldBits = this.bits;
        long newBits = oldBits | (1 << element.ordinal());
        if (oldBits == newBits) {
            return false;
        }
        this.bits = newBits;
        this.size++;
        return true;
    }

    public boolean addAll(Collection<? extends E> collection) {
        if (collection.isEmpty()) {
            return false;
        }
        if (!(collection instanceof EnumSet)) {
            return super.addAll(collection);
        }
        EnumSet<?> set = (EnumSet) collection;
        set.elementClass.asSubclass(this.elementClass);
        MiniEnumSet<?> miniSet = (MiniEnumSet) set;
        long oldBits = this.bits;
        long newBits = oldBits | miniSet.bits;
        this.bits = newBits;
        this.size = Long.bitCount(newBits);
        if (oldBits != newBits) {
            return true;
        }
        return false;
    }

    public boolean contains(Object object) {
        if (object == null || !isValidType(object.getClass())) {
            return false;
        }
        if ((this.bits & (1 << ((Enum) object).ordinal())) != 0) {
            return true;
        }
        return false;
    }

    public boolean containsAll(Collection<?> collection) {
        if (collection.isEmpty()) {
            return true;
        }
        if (collection instanceof MiniEnumSet) {
            MiniEnumSet<?> set = (MiniEnumSet) collection;
            long setBits = set.bits;
            if (isValidType(set.elementClass) && (this.bits & setBits) == setBits) {
                return true;
            }
            return false;
        } else if ((collection instanceof EnumSet) || !super.containsAll(collection)) {
            return false;
        } else {
            return true;
        }
    }

    public boolean removeAll(Collection<?> collection) {
        if (collection.isEmpty()) {
            return false;
        }
        if (!(collection instanceof EnumSet)) {
            return super.removeAll(collection);
        }
        EnumSet<?> set = (EnumSet) collection;
        if (!isValidType(set.elementClass)) {
            return false;
        }
        MiniEnumSet<E> miniSet = (MiniEnumSet) set;
        long oldBits = this.bits;
        long newBits = oldBits & (miniSet.bits ^ -1);
        if (oldBits == newBits) {
            return false;
        }
        this.bits = newBits;
        this.size = Long.bitCount(newBits);
        return true;
    }

    public boolean retainAll(Collection<?> collection) {
        if (!(collection instanceof EnumSet)) {
            return super.retainAll(collection);
        }
        EnumSet<?> set = (EnumSet) collection;
        if (isValidType(set.elementClass)) {
            MiniEnumSet<E> miniSet = (MiniEnumSet) set;
            long oldBits = this.bits;
            long newBits = oldBits & miniSet.bits;
            if (oldBits == newBits) {
                return false;
            }
            this.bits = newBits;
            this.size = Long.bitCount(newBits);
            return true;
        } else if (this.size <= 0) {
            return false;
        } else {
            clear();
            return true;
        }
    }

    public boolean remove(Object object) {
        if (object == null || !isValidType(object.getClass())) {
            return false;
        }
        int ordinal = ((Enum) object).ordinal();
        long oldBits = this.bits;
        long newBits = oldBits & ((1 << ordinal) ^ -1);
        if (oldBits == newBits) {
            return false;
        }
        this.bits = newBits;
        this.size--;
        return true;
    }

    public boolean equals(Object object) {
        if (!(object instanceof EnumSet)) {
            return super.equals(object);
        }
        EnumSet<?> set = (EnumSet) object;
        if (isValidType(set.elementClass)) {
            if (this.bits != ((MiniEnumSet) set).bits) {
                return false;
            }
            return true;
        } else if (this.size == 0 && set.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    void complement() {
        if (this.enums.length != 0) {
            this.bits ^= -1;
            this.bits &= -1 >>> (64 - this.enums.length);
            this.size = this.enums.length - this.size;
        }
    }

    void setRange(E start, E end) {
        this.bits |= (-1 >>> (64 - ((end.ordinal() - start.ordinal()) + 1))) << start.ordinal();
        this.size = Long.bitCount(this.bits);
    }
}
