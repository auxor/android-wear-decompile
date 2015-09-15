package java.lang;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadLocal<T> {
    private static AtomicInteger hashCounter;
    private final int hash;
    private final Reference<ThreadLocal<T>> reference;

    static class Values {
        private static final int INITIAL_SIZE = 16;
        private static final Object TOMBSTONE;
        private int clean;
        private int mask;
        private int maximumLoad;
        private int size;
        private Object[] table;
        private int tombstones;

        static {
            TOMBSTONE = new Object();
        }

        Values() {
            initializeTable(INITIAL_SIZE);
            this.size = 0;
            this.tombstones = 0;
        }

        Values(Values fromParent) {
            this.table = (Object[]) fromParent.table.clone();
            this.mask = fromParent.mask;
            this.size = fromParent.size;
            this.tombstones = fromParent.tombstones;
            this.maximumLoad = fromParent.maximumLoad;
            this.clean = fromParent.clean;
            inheritValues(fromParent);
        }

        private void inheritValues(Values fromParent) {
            Object[] table = this.table;
            for (int i = table.length - 2; i >= 0; i -= 2) {
                Reference<InheritableThreadLocal<?>> k = table[i];
                if (!(k == null || k == TOMBSTONE)) {
                    InheritableThreadLocal key = (InheritableThreadLocal) k.get();
                    if (key != null) {
                        table[i + 1] = key.childValue(fromParent.table[i + 1]);
                    } else {
                        table[i] = TOMBSTONE;
                        table[i + 1] = null;
                        fromParent.table[i] = TOMBSTONE;
                        fromParent.table[i + 1] = null;
                        this.tombstones++;
                        fromParent.tombstones++;
                        this.size--;
                        fromParent.size--;
                    }
                }
            }
        }

        private void initializeTable(int capacity) {
            this.table = new Object[(capacity * 2)];
            this.mask = this.table.length - 1;
            this.clean = 0;
            this.maximumLoad = (capacity * 2) / 3;
        }

        private void cleanUp() {
            if (!rehash() && this.size != 0) {
                int index = this.clean;
                Object[] table = this.table;
                int counter = table.length;
                while (counter > 0) {
                    Reference<ThreadLocal<?>> k = table[index];
                    if (!(k == TOMBSTONE || k == null || k.get() != null)) {
                        table[index] = TOMBSTONE;
                        table[index + 1] = null;
                        this.tombstones++;
                        this.size--;
                    }
                    counter >>= 1;
                    index = next(index);
                }
                this.clean = index;
            }
        }

        private boolean rehash() {
            if (this.tombstones + this.size < this.maximumLoad) {
                return false;
            }
            int capacity = this.table.length >> 1;
            int newCapacity = capacity;
            if (this.size > (capacity >> 1)) {
                newCapacity = capacity * 2;
            }
            Object[] oldTable = this.table;
            initializeTable(newCapacity);
            this.tombstones = 0;
            if (this.size == 0) {
                return true;
            }
            for (int i = oldTable.length - 2; i >= 0; i -= 2) {
                Reference<ThreadLocal<?>> k = oldTable[i];
                if (!(k == null || k == TOMBSTONE)) {
                    ThreadLocal<?> key = (ThreadLocal) k.get();
                    if (key != null) {
                        add(key, oldTable[i + 1]);
                    } else {
                        this.size--;
                    }
                }
            }
            return true;
        }

        void add(ThreadLocal<?> key, Object value) {
            int index = key.hash & this.mask;
            while (this.table[index] != null) {
                index = next(index);
            }
            this.table[index] = key.reference;
            this.table[index + 1] = value;
        }

        void put(ThreadLocal<?> key, Object value) {
            cleanUp();
            int firstTombstone = -1;
            int index = key.hash & this.mask;
            while (true) {
                Reference k = this.table[index];
                if (k == key.reference) {
                    this.table[index + 1] = value;
                    return;
                } else if (k == null) {
                    break;
                } else {
                    if (firstTombstone == -1 && k == TOMBSTONE) {
                        firstTombstone = index;
                    }
                    index = next(index);
                }
            }
            if (firstTombstone == -1) {
                this.table[index] = key.reference;
                this.table[index + 1] = value;
                this.size++;
                return;
            }
            this.table[firstTombstone] = key.reference;
            this.table[firstTombstone + 1] = value;
            this.tombstones--;
            this.size++;
        }

        Object getAfterMiss(ThreadLocal<?> key) {
            Object[] table = this.table;
            int index = key.hash & this.mask;
            Object value;
            if (table[index] == null) {
                value = key.initialValue();
                if (this.table == table && table[index] == null) {
                    table[index] = key.reference;
                    table[index + 1] = value;
                    this.size++;
                    cleanUp();
                    return value;
                }
                put(key, value);
                return value;
            }
            int firstTombstone = -1;
            index = next(index);
            while (true) {
                Reference reference = table[index];
                if (reference == key.reference) {
                    return table[index + 1];
                }
                if (reference == null) {
                    break;
                }
                if (firstTombstone == -1 && reference == TOMBSTONE) {
                    firstTombstone = index;
                }
                index = next(index);
            }
            value = key.initialValue();
            if (this.table == table) {
                if (firstTombstone > -1 && table[firstTombstone] == TOMBSTONE) {
                    table[firstTombstone] = key.reference;
                    table[firstTombstone + 1] = value;
                    this.tombstones--;
                    this.size++;
                    return value;
                } else if (table[index] == null) {
                    table[index] = key.reference;
                    table[index + 1] = value;
                    this.size++;
                    cleanUp();
                    return value;
                }
            }
            put(key, value);
            return value;
        }

        void remove(ThreadLocal<?> key) {
            cleanUp();
            int index = key.hash & this.mask;
            while (true) {
                Reference reference = this.table[index];
                if (reference == key.reference) {
                    this.table[index] = TOMBSTONE;
                    this.table[index + 1] = null;
                    this.tombstones++;
                    this.size--;
                    return;
                } else if (reference != null) {
                    index = next(index);
                } else {
                    return;
                }
            }
        }

        private int next(int index) {
            return (index + 2) & this.mask;
        }
    }

    public ThreadLocal() {
        this.reference = new WeakReference(this);
        this.hash = hashCounter.getAndAdd(-1013904242);
    }

    public T get() {
        Thread currentThread = Thread.currentThread();
        Values values = values(currentThread);
        if (values != null) {
            Object[] table = values.table;
            int index = this.hash & values.mask;
            if (this.reference == table[index]) {
                return table[index + 1];
            }
        }
        values = initializeValues(currentThread);
        return values.getAfterMiss(this);
    }

    protected T initialValue() {
        return null;
    }

    public void set(T value) {
        Thread currentThread = Thread.currentThread();
        Values values = values(currentThread);
        if (values == null) {
            values = initializeValues(currentThread);
        }
        values.put(this, value);
    }

    public void remove() {
        Values values = values(Thread.currentThread());
        if (values != null) {
            values.remove(this);
        }
    }

    Values initializeValues(Thread current) {
        Values values = new Values();
        current.localValues = values;
        return values;
    }

    Values values(Thread current) {
        return current.localValues;
    }

    static {
        hashCounter = new AtomicInteger(0);
    }
}
