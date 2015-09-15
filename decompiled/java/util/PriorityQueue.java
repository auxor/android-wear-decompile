package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class PriorityQueue<E> extends AbstractQueue<E> implements Serializable {
    private static final int DEFAULT_CAPACITY = 11;
    private static final int DEFAULT_CAPACITY_RATIO = 2;
    private static final double DEFAULT_INIT_CAPACITY_RATIO = 1.1d;
    private static final long serialVersionUID = -7720805057305804111L;
    private Comparator<? super E> comparator;
    private transient E[] elements;
    private int size;

    private class PriorityIterator implements Iterator<E> {
        private boolean allowRemove;
        private int currentIndex;
        final /* synthetic */ PriorityQueue this$0;

        private PriorityIterator(java.util.PriorityQueue r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.PriorityQueue.PriorityIterator.<init>(java.util.PriorityQueue):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.PriorityQueue.PriorityIterator.<init>(java.util.PriorityQueue):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.PriorityQueue.PriorityIterator.<init>(java.util.PriorityQueue):void");
        }

        /* synthetic */ PriorityIterator(java.util.PriorityQueue r1, java.util.PriorityQueue.AnonymousClass1 r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.PriorityQueue.PriorityIterator.<init>(java.util.PriorityQueue, java.util.PriorityQueue$1):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.PriorityQueue.PriorityIterator.<init>(java.util.PriorityQueue, java.util.PriorityQueue$1):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 0073
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.PriorityQueue.PriorityIterator.<init>(java.util.PriorityQueue, java.util.PriorityQueue$1):void");
        }

        public boolean hasNext() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.PriorityQueue.PriorityIterator.hasNext():boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.PriorityQueue.PriorityIterator.hasNext():boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.PriorityQueue.PriorityIterator.hasNext():boolean");
        }

        public E next() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.PriorityQueue.PriorityIterator.next():E
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.PriorityQueue.PriorityIterator.next():E
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.PriorityQueue.PriorityIterator.next():E");
        }

        public void remove() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.PriorityQueue.PriorityIterator.remove():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.PriorityQueue.PriorityIterator.remove():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.PriorityQueue.PriorityIterator.remove():void");
        }
    }

    public PriorityQueue() {
        this((int) DEFAULT_CAPACITY);
    }

    public PriorityQueue(int initialCapacity) {
        this(initialCapacity, null);
    }

    public PriorityQueue(int initialCapacity, Comparator<? super E> comparator) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("initialCapacity < 1: " + initialCapacity);
        }
        this.elements = newElementArray(initialCapacity);
        this.comparator = comparator;
    }

    public PriorityQueue(Collection<? extends E> c) {
        if (c instanceof PriorityQueue) {
            getFromPriorityQueue((PriorityQueue) c);
        } else if (c instanceof SortedSet) {
            getFromSortedSet((SortedSet) c);
        } else {
            initSize(c);
            addAll(c);
        }
    }

    public PriorityQueue(PriorityQueue<? extends E> c) {
        getFromPriorityQueue(c);
    }

    public PriorityQueue(SortedSet<? extends E> c) {
        getFromSortedSet(c);
    }

    public Iterator<E> iterator() {
        return new PriorityIterator();
    }

    public int size() {
        return this.size;
    }

    public void clear() {
        Arrays.fill(this.elements, null);
        this.size = 0;
    }

    public boolean offer(E o) {
        if (o == null) {
            throw new NullPointerException("o == null");
        }
        growToSize(this.size + 1);
        this.elements[this.size] = o;
        int i = this.size;
        this.size = i + 1;
        siftUp(i);
        return true;
    }

    public E poll() {
        if (isEmpty()) {
            return null;
        }
        E result = this.elements[0];
        removeAt(0);
        return result;
    }

    public E peek() {
        if (isEmpty()) {
            return null;
        }
        return this.elements[0];
    }

    public Comparator<? super E> comparator() {
        return this.comparator;
    }

    public boolean remove(Object o) {
        if (o == null) {
            return false;
        }
        for (int targetIndex = 0; targetIndex < this.size; targetIndex++) {
            if (o.equals(this.elements[targetIndex])) {
                removeAt(targetIndex);
                return true;
            }
        }
        return false;
    }

    public boolean add(E o) {
        return offer(o);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.elements = newElementArray(in.readInt());
        for (int i = 0; i < this.size; i++) {
            this.elements[i] = in.readObject();
        }
    }

    private E[] newElementArray(int capacity) {
        return new Object[capacity];
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(this.elements.length);
        for (int i = 0; i < this.size; i++) {
            out.writeObject(this.elements[i]);
        }
    }

    private void getFromPriorityQueue(PriorityQueue<? extends E> c) {
        initSize(c);
        this.comparator = c.comparator();
        System.arraycopy(c.elements, 0, this.elements, 0, c.size());
        this.size = c.size();
    }

    private void getFromSortedSet(SortedSet<? extends E> c) {
        initSize(c);
        this.comparator = c.comparator();
        for (Object obj : c) {
            Object[] objArr = this.elements;
            int i = this.size;
            this.size = i + 1;
            objArr[i] = obj;
        }
    }

    private void removeAt(int index) {
        this.size--;
        E moved = this.elements[this.size];
        this.elements[index] = moved;
        siftDown(index);
        this.elements[this.size] = null;
        if (moved == this.elements[index]) {
            siftUp(index);
        }
    }

    private int compare(E o1, E o2) {
        if (this.comparator != null) {
            return this.comparator.compare(o1, o2);
        }
        return ((Comparable) o1).compareTo(o2);
    }

    private void siftUp(int childIndex) {
        E target = this.elements[childIndex];
        while (childIndex > 0) {
            int parentIndex = (childIndex - 1) / DEFAULT_CAPACITY_RATIO;
            E parent = this.elements[parentIndex];
            if (compare(parent, target) <= 0) {
                break;
            }
            this.elements[childIndex] = parent;
            childIndex = parentIndex;
        }
        this.elements[childIndex] = target;
    }

    private void siftDown(int rootIndex) {
        E target = this.elements[rootIndex];
        while (true) {
            int childIndex = (rootIndex * DEFAULT_CAPACITY_RATIO) + 1;
            if (childIndex >= this.size) {
                break;
            }
            if (childIndex + 1 < this.size && compare(this.elements[childIndex + 1], this.elements[childIndex]) < 0) {
                childIndex++;
            }
            if (compare(target, this.elements[childIndex]) <= 0) {
                break;
            }
            this.elements[rootIndex] = this.elements[childIndex];
            rootIndex = childIndex;
        }
        this.elements[rootIndex] = target;
    }

    private void initSize(Collection<? extends E> c) {
        if (c == null) {
            throw new NullPointerException("c == null");
        } else if (c.isEmpty()) {
            this.elements = newElementArray(1);
        } else {
            this.elements = newElementArray((int) Math.ceil(((double) c.size()) * DEFAULT_INIT_CAPACITY_RATIO));
        }
    }

    private void growToSize(int size) {
        if (size > this.elements.length) {
            Object newElements = newElementArray(size * DEFAULT_CAPACITY_RATIO);
            System.arraycopy(this.elements, 0, newElements, 0, this.elements.length);
            this.elements = newElements;
        }
    }
}
