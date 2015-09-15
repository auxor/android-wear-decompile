package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Map.Entry;

public class Collections {
    private static final Enumeration<?> EMPTY_ENUMERATION = null;
    private static final Iterator<?> EMPTY_ITERATOR = null;
    public static final List EMPTY_LIST = null;
    public static final Map EMPTY_MAP = null;
    public static final Set EMPTY_SET = null;

    /* renamed from: java.util.Collections.3 */
    static class AnonymousClass3 implements Enumeration<T> {
        Iterator<T> it;
        final /* synthetic */ Collection val$c;

        AnonymousClass3(Collection collection) {
            this.val$c = collection;
            this.it = this.val$c.iterator();
        }

        public boolean hasMoreElements() {
            return this.it.hasNext();
        }

        public T nextElement() {
            return this.it.next();
        }
    }

    private static class AsLIFOQueue<E> extends AbstractQueue<E> implements Serializable {
        private static final long serialVersionUID = 1802017725587941708L;
        private final Deque<E> q;

        AsLIFOQueue(Deque<E> deque) {
            this.q = deque;
        }

        public Iterator<E> iterator() {
            return this.q.iterator();
        }

        public int size() {
            return this.q.size();
        }

        public boolean offer(E o) {
            return this.q.offerFirst(o);
        }

        public E peek() {
            return this.q.peekFirst();
        }

        public E poll() {
            return this.q.pollFirst();
        }

        public boolean add(E o) {
            this.q.push(o);
            return true;
        }

        public void clear() {
            this.q.clear();
        }

        public E element() {
            return this.q.getFirst();
        }

        public E remove() {
            return this.q.pop();
        }

        public boolean contains(Object object) {
            return this.q.contains(object);
        }

        public boolean containsAll(Collection<?> collection) {
            return this.q.containsAll(collection);
        }

        public boolean isEmpty() {
            return this.q.isEmpty();
        }

        public boolean remove(Object object) {
            return this.q.remove(object);
        }

        public boolean removeAll(Collection<?> collection) {
            return this.q.removeAll(collection);
        }

        public boolean retainAll(Collection<?> collection) {
            return this.q.retainAll(collection);
        }

        public Object[] toArray() {
            return this.q.toArray();
        }

        public <T> T[] toArray(T[] contents) {
            return this.q.toArray(contents);
        }

        public String toString() {
            return this.q.toString();
        }
    }

    private static class CheckedCollection<E> implements Collection<E>, Serializable {
        private static final long serialVersionUID = 1578914078182001775L;
        final Collection<E> c;
        final Class<E> type;

        public CheckedCollection(Collection<E> c, Class<E> type) {
            if (c == null) {
                throw new NullPointerException("c == null");
            } else if (type == null) {
                throw new NullPointerException("type == null");
            } else {
                this.c = c;
                this.type = type;
            }
        }

        public int size() {
            return this.c.size();
        }

        public boolean isEmpty() {
            return this.c.isEmpty();
        }

        public boolean contains(Object obj) {
            return this.c.contains(obj);
        }

        public Iterator<E> iterator() {
            Iterator<E> i = this.c.iterator();
            if (i instanceof ListIterator) {
                return new CheckedListIterator((ListIterator) i, this.type);
            }
            return i;
        }

        public Object[] toArray() {
            return this.c.toArray();
        }

        public <T> T[] toArray(T[] arr) {
            return this.c.toArray(arr);
        }

        public boolean add(E obj) {
            return this.c.add(Collections.checkType(obj, this.type));
        }

        public boolean remove(Object obj) {
            return this.c.remove(obj);
        }

        public boolean containsAll(Collection<?> c1) {
            return this.c.containsAll(c1);
        }

        public boolean addAll(Collection<? extends E> c1) {
            Object[] array = c1.toArray();
            for (Object o : array) {
                Collections.checkType(o, this.type);
            }
            return this.c.addAll(Arrays.asList(array));
        }

        public boolean removeAll(Collection<?> c1) {
            return this.c.removeAll(c1);
        }

        public boolean retainAll(Collection<?> c1) {
            return this.c.retainAll(c1);
        }

        public void clear() {
            this.c.clear();
        }

        public String toString() {
            return this.c.toString();
        }
    }

    private static class CheckedList<E> extends CheckedCollection<E> implements List<E> {
        private static final long serialVersionUID = 65247728283967356L;
        final List<E> l;

        public CheckedList(List<E> l, Class<E> type) {
            super(l, type);
            this.l = l;
        }

        public boolean addAll(int index, Collection<? extends E> c1) {
            Object[] array = c1.toArray();
            for (Object o : array) {
                Collections.checkType(o, this.type);
            }
            return this.l.addAll(index, Arrays.asList(array));
        }

        public E get(int index) {
            return this.l.get(index);
        }

        public E set(int index, E obj) {
            return this.l.set(index, Collections.checkType(obj, this.type));
        }

        public void add(int index, E obj) {
            this.l.add(index, Collections.checkType(obj, this.type));
        }

        public E remove(int index) {
            return this.l.remove(index);
        }

        public int indexOf(Object obj) {
            return this.l.indexOf(obj);
        }

        public int lastIndexOf(Object obj) {
            return this.l.lastIndexOf(obj);
        }

        public ListIterator<E> listIterator() {
            return new CheckedListIterator(this.l.listIterator(), this.type);
        }

        public ListIterator<E> listIterator(int index) {
            return new CheckedListIterator(this.l.listIterator(index), this.type);
        }

        public List<E> subList(int fromIndex, int toIndex) {
            return Collections.checkedList(this.l.subList(fromIndex, toIndex), this.type);
        }

        public boolean equals(Object obj) {
            return this.l.equals(obj);
        }

        public int hashCode() {
            return this.l.hashCode();
        }
    }

    private static class CheckedListIterator<E> implements ListIterator<E> {
        private final ListIterator<E> i;
        private final Class<E> type;

        public CheckedListIterator(java.util.ListIterator<E> r1, java.lang.Class<E> r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedListIterator.<init>(java.util.ListIterator, java.lang.Class):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedListIterator.<init>(java.util.ListIterator, java.lang.Class):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedListIterator.<init>(java.util.ListIterator, java.lang.Class):void");
        }

        public void add(E r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedListIterator.add(java.lang.Object):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedListIterator.add(java.lang.Object):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedListIterator.add(java.lang.Object):void");
        }

        public boolean hasNext() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedListIterator.hasNext():boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedListIterator.hasNext():boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedListIterator.hasNext():boolean");
        }

        public boolean hasPrevious() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedListIterator.hasPrevious():boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedListIterator.hasPrevious():boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedListIterator.hasPrevious():boolean");
        }

        public E next() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedListIterator.next():E
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedListIterator.next():E
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedListIterator.next():E");
        }

        public int nextIndex() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedListIterator.nextIndex():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedListIterator.nextIndex():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedListIterator.nextIndex():int");
        }

        public E previous() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedListIterator.previous():E
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedListIterator.previous():E
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedListIterator.previous():E");
        }

        public int previousIndex() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedListIterator.previousIndex():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedListIterator.previousIndex():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedListIterator.previousIndex():int");
        }

        public void remove() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedListIterator.remove():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedListIterator.remove():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedListIterator.remove():void");
        }

        public void set(E r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedListIterator.set(java.lang.Object):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedListIterator.set(java.lang.Object):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedListIterator.set(java.lang.Object):void");
        }
    }

    private static class CheckedMap<K, V> implements Map<K, V>, Serializable {
        private static final long serialVersionUID = 5742860141034234728L;
        final Class<K> keyType;
        final Map<K, V> m;
        final Class<V> valueType;

        private static class CheckedEntry<K, V> implements Entry<K, V> {
            final Entry<K, V> e;
            final Class<V> valueType;

            public CheckedEntry(java.util.Map.Entry<K, V> r1, java.lang.Class<V> r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedMap.CheckedEntry.<init>(java.util.Map$Entry, java.lang.Class):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedMap.CheckedEntry.<init>(java.util.Map$Entry, java.lang.Class):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedMap.CheckedEntry.<init>(java.util.Map$Entry, java.lang.Class):void");
            }

            public boolean equals(java.lang.Object r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedMap.CheckedEntry.equals(java.lang.Object):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedMap.CheckedEntry.equals(java.lang.Object):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedMap.CheckedEntry.equals(java.lang.Object):boolean");
            }

            public K getKey() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedMap.CheckedEntry.getKey():K
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedMap.CheckedEntry.getKey():K
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedMap.CheckedEntry.getKey():K");
            }

            public V getValue() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedMap.CheckedEntry.getValue():V
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedMap.CheckedEntry.getValue():V
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedMap.CheckedEntry.getValue():V");
            }

            public int hashCode() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedMap.CheckedEntry.hashCode():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedMap.CheckedEntry.hashCode():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedMap.CheckedEntry.hashCode():int");
            }

            public V setValue(V r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedMap.CheckedEntry.setValue(java.lang.Object):V
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedMap.CheckedEntry.setValue(java.lang.Object):V
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedMap.CheckedEntry.setValue(java.lang.Object):V");
            }
        }

        private static class CheckedEntrySet<K, V> implements Set<Entry<K, V>> {
            final Set<Entry<K, V>> s;
            final Class<V> valueType;

            private static class CheckedEntryIterator<K, V> implements Iterator<Entry<K, V>> {
                Iterator<Entry<K, V>> i;
                Class<V> valueType;

                public CheckedEntryIterator(java.util.Iterator<java.util.Map.Entry<K, V>> r1, java.lang.Class<V> r2) {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedMap.CheckedEntrySet.CheckedEntryIterator.<init>(java.util.Iterator, java.lang.Class):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedMap.CheckedEntrySet.CheckedEntryIterator.<init>(java.util.Iterator, java.lang.Class):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 10 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 11 more
*/
                    /*
                    // Can't load method instructions.
                    */
                    throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedMap.CheckedEntrySet.CheckedEntryIterator.<init>(java.util.Iterator, java.lang.Class):void");
                }

                public boolean hasNext() {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedMap.CheckedEntrySet.CheckedEntryIterator.hasNext():boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedMap.CheckedEntrySet.CheckedEntryIterator.hasNext():boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 10 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 11 more
*/
                    /*
                    // Can't load method instructions.
                    */
                    throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedMap.CheckedEntrySet.CheckedEntryIterator.hasNext():boolean");
                }

                public java.util.Map.Entry<K, V> next() {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedMap.CheckedEntrySet.CheckedEntryIterator.next():java.util.Map$Entry<K, V>
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedMap.CheckedEntrySet.CheckedEntryIterator.next():java.util.Map$Entry<K, V>
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 10 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 11 more
*/
                    /*
                    // Can't load method instructions.
                    */
                    throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedMap.CheckedEntrySet.CheckedEntryIterator.next():java.util.Map$Entry<K, V>");
                }

                public void remove() {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedMap.CheckedEntrySet.CheckedEntryIterator.remove():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedMap.CheckedEntrySet.CheckedEntryIterator.remove():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 10 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 11 more
*/
                    /*
                    // Can't load method instructions.
                    */
                    throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedMap.CheckedEntrySet.CheckedEntryIterator.remove():void");
                }
            }

            public CheckedEntrySet(java.util.Set<java.util.Map.Entry<K, V>> r1, java.lang.Class<V> r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedMap.CheckedEntrySet.<init>(java.util.Set, java.lang.Class):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedMap.CheckedEntrySet.<init>(java.util.Set, java.lang.Class):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedMap.CheckedEntrySet.<init>(java.util.Set, java.lang.Class):void");
            }

            public void clear() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedMap.CheckedEntrySet.clear():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedMap.CheckedEntrySet.clear():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedMap.CheckedEntrySet.clear():void");
            }

            public boolean contains(java.lang.Object r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedMap.CheckedEntrySet.contains(java.lang.Object):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedMap.CheckedEntrySet.contains(java.lang.Object):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedMap.CheckedEntrySet.contains(java.lang.Object):boolean");
            }

            public boolean containsAll(java.util.Collection<?> r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedMap.CheckedEntrySet.containsAll(java.util.Collection):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedMap.CheckedEntrySet.containsAll(java.util.Collection):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedMap.CheckedEntrySet.containsAll(java.util.Collection):boolean");
            }

            public boolean equals(java.lang.Object r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedMap.CheckedEntrySet.equals(java.lang.Object):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedMap.CheckedEntrySet.equals(java.lang.Object):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedMap.CheckedEntrySet.equals(java.lang.Object):boolean");
            }

            public int hashCode() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedMap.CheckedEntrySet.hashCode():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedMap.CheckedEntrySet.hashCode():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedMap.CheckedEntrySet.hashCode():int");
            }

            public boolean isEmpty() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedMap.CheckedEntrySet.isEmpty():boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedMap.CheckedEntrySet.isEmpty():boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedMap.CheckedEntrySet.isEmpty():boolean");
            }

            public java.util.Iterator<java.util.Map.Entry<K, V>> iterator() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedMap.CheckedEntrySet.iterator():java.util.Iterator<java.util.Map$Entry<K, V>>
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedMap.CheckedEntrySet.iterator():java.util.Iterator<java.util.Map$Entry<K, V>>
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedMap.CheckedEntrySet.iterator():java.util.Iterator<java.util.Map$Entry<K, V>>");
            }

            public boolean remove(java.lang.Object r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedMap.CheckedEntrySet.remove(java.lang.Object):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedMap.CheckedEntrySet.remove(java.lang.Object):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedMap.CheckedEntrySet.remove(java.lang.Object):boolean");
            }

            public boolean removeAll(java.util.Collection<?> r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedMap.CheckedEntrySet.removeAll(java.util.Collection):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedMap.CheckedEntrySet.removeAll(java.util.Collection):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedMap.CheckedEntrySet.removeAll(java.util.Collection):boolean");
            }

            public boolean retainAll(java.util.Collection<?> r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedMap.CheckedEntrySet.retainAll(java.util.Collection):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedMap.CheckedEntrySet.retainAll(java.util.Collection):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedMap.CheckedEntrySet.retainAll(java.util.Collection):boolean");
            }

            public int size() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedMap.CheckedEntrySet.size():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedMap.CheckedEntrySet.size():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedMap.CheckedEntrySet.size():int");
            }

            public java.lang.Object[] toArray() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedMap.CheckedEntrySet.toArray():java.lang.Object[]
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedMap.CheckedEntrySet.toArray():java.lang.Object[]
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedMap.CheckedEntrySet.toArray():java.lang.Object[]");
            }

            public <T> T[] toArray(T[] r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.CheckedMap.CheckedEntrySet.toArray(java.lang.Object[]):T[]
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.CheckedMap.CheckedEntrySet.toArray(java.lang.Object[]):T[]
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.CheckedMap.CheckedEntrySet.toArray(java.lang.Object[]):T[]");
            }

            public boolean addAll(Collection<? extends Entry<K, V>> collection) {
                throw new UnsupportedOperationException();
            }

            public boolean add(Entry<K, V> entry) {
                throw new UnsupportedOperationException();
            }
        }

        private CheckedMap(Map<K, V> m, Class<K> keyType, Class<V> valueType) {
            if (m == null) {
                throw new NullPointerException("m == null");
            } else if (keyType == null) {
                throw new NullPointerException("keyType == null");
            } else if (valueType == null) {
                throw new NullPointerException("valueType == null");
            } else {
                this.m = m;
                this.keyType = keyType;
                this.valueType = valueType;
            }
        }

        public int size() {
            return this.m.size();
        }

        public boolean isEmpty() {
            return this.m.isEmpty();
        }

        public boolean containsKey(Object key) {
            return this.m.containsKey(key);
        }

        public boolean containsValue(Object value) {
            return this.m.containsValue(value);
        }

        public V get(Object key) {
            return this.m.get(key);
        }

        public V put(K key, V value) {
            return this.m.put(Collections.checkType(key, this.keyType), Collections.checkType(value, this.valueType));
        }

        public V remove(Object key) {
            return this.m.remove(key);
        }

        public void putAll(Map<? extends K, ? extends V> map) {
            int size = map.size();
            if (size != 0) {
                int i;
                Entry<? extends K, ? extends V>[] entries = new Entry[size];
                Iterator<? extends Entry<? extends K, ? extends V>> it = map.entrySet().iterator();
                for (i = 0; i < size; i++) {
                    Entry<? extends K, ? extends V> e = (Entry) it.next();
                    Collections.checkType(e.getKey(), this.keyType);
                    Collections.checkType(e.getValue(), this.valueType);
                    entries[i] = e;
                }
                for (i = 0; i < size; i++) {
                    this.m.put(entries[i].getKey(), entries[i].getValue());
                }
            }
        }

        public void clear() {
            this.m.clear();
        }

        public Set<K> keySet() {
            return this.m.keySet();
        }

        public Collection<V> values() {
            return this.m.values();
        }

        public Set<Entry<K, V>> entrySet() {
            return new CheckedEntrySet(this.m.entrySet(), this.valueType);
        }

        public boolean equals(Object obj) {
            return this.m.equals(obj);
        }

        public int hashCode() {
            return this.m.hashCode();
        }

        public String toString() {
            return this.m.toString();
        }
    }

    private static class CheckedRandomAccessList<E> extends CheckedList<E> implements RandomAccess {
        private static final long serialVersionUID = 1638200125423088369L;

        public CheckedRandomAccessList(List<E> l, Class<E> type) {
            super(l, type);
        }
    }

    private static class CheckedSet<E> extends CheckedCollection<E> implements Set<E> {
        private static final long serialVersionUID = 4694047833775013803L;

        public CheckedSet(Set<E> s, Class<E> type) {
            super(s, type);
        }

        public boolean equals(Object obj) {
            return this.c.equals(obj);
        }

        public int hashCode() {
            return this.c.hashCode();
        }
    }

    private static class CheckedSortedMap<K, V> extends CheckedMap<K, V> implements SortedMap<K, V> {
        private static final long serialVersionUID = 1599671320688067438L;
        final SortedMap<K, V> sm;

        CheckedSortedMap(SortedMap<K, V> m, Class<K> keyType, Class<V> valueType) {
            super(keyType, valueType, null);
            this.sm = m;
        }

        public Comparator<? super K> comparator() {
            return this.sm.comparator();
        }

        public SortedMap<K, V> subMap(K fromKey, K toKey) {
            return new CheckedSortedMap(this.sm.subMap(fromKey, toKey), this.keyType, this.valueType);
        }

        public SortedMap<K, V> headMap(K toKey) {
            return new CheckedSortedMap(this.sm.headMap(toKey), this.keyType, this.valueType);
        }

        public SortedMap<K, V> tailMap(K fromKey) {
            return new CheckedSortedMap(this.sm.tailMap(fromKey), this.keyType, this.valueType);
        }

        public K firstKey() {
            return this.sm.firstKey();
        }

        public K lastKey() {
            return this.sm.lastKey();
        }
    }

    private static class CheckedSortedSet<E> extends CheckedSet<E> implements SortedSet<E> {
        private static final long serialVersionUID = 1599911165492914959L;
        private final SortedSet<E> ss;

        public CheckedSortedSet(SortedSet<E> s, Class<E> type) {
            super(s, type);
            this.ss = s;
        }

        public Comparator<? super E> comparator() {
            return this.ss.comparator();
        }

        public SortedSet<E> subSet(E fromElement, E toElement) {
            return new CheckedSortedSet(this.ss.subSet(fromElement, toElement), this.type);
        }

        public SortedSet<E> headSet(E toElement) {
            return new CheckedSortedSet(this.ss.headSet(toElement), this.type);
        }

        public SortedSet<E> tailSet(E fromElement) {
            return new CheckedSortedSet(this.ss.tailSet(fromElement), this.type);
        }

        public E first() {
            return this.ss.first();
        }

        public E last() {
            return this.ss.last();
        }
    }

    private static final class CopiesList<E> extends AbstractList<E> implements Serializable {
        private static final long serialVersionUID = 2739099268398711800L;
        private final E element;
        private final int n;

        CopiesList(int length, E object) {
            if (length < 0) {
                throw new IllegalArgumentException("length < 0: " + length);
            }
            this.n = length;
            this.element = object;
        }

        public boolean contains(Object object) {
            if (this.element == null) {
                return object == null;
            } else {
                return this.element.equals(object);
            }
        }

        public int size() {
            return this.n;
        }

        public E get(int location) {
            if (location >= 0 && location < this.n) {
                return this.element;
            }
            throw new IndexOutOfBoundsException();
        }
    }

    private static final class EmptyList extends AbstractList implements RandomAccess, Serializable {
        private static final long serialVersionUID = 8842843931221139166L;

        private EmptyList() {
        }

        public boolean contains(Object object) {
            return false;
        }

        public int size() {
            return 0;
        }

        public Object get(int location) {
            throw new IndexOutOfBoundsException();
        }

        private Object readResolve() {
            return Collections.EMPTY_LIST;
        }
    }

    private static final class EmptyMap extends AbstractMap implements Serializable {
        private static final long serialVersionUID = 6428348081105594320L;

        private EmptyMap() {
        }

        public boolean containsKey(Object key) {
            return false;
        }

        public boolean containsValue(Object value) {
            return false;
        }

        public Set entrySet() {
            return Collections.EMPTY_SET;
        }

        public Object get(Object key) {
            return null;
        }

        public Set keySet() {
            return Collections.EMPTY_SET;
        }

        public Collection values() {
            return Collections.EMPTY_LIST;
        }

        private Object readResolve() {
            return Collections.EMPTY_MAP;
        }
    }

    private static final class EmptySet extends AbstractSet implements Serializable {
        private static final long serialVersionUID = 1582296315990362920L;

        private EmptySet() {
        }

        public boolean contains(Object object) {
            return false;
        }

        public int size() {
            return 0;
        }

        public Iterator iterator() {
            return Collections.EMPTY_ITERATOR;
        }

        private Object readResolve() {
            return Collections.EMPTY_SET;
        }
    }

    private static final class ReverseComparator2<T> implements Comparator<T>, Serializable {
        private static final long serialVersionUID = 4374092139857L;
        private final Comparator<T> cmp;

        ReverseComparator2(Comparator<T> comparator) {
            this.cmp = comparator;
        }

        public int compare(T o1, T o2) {
            return this.cmp.compare(o2, o1);
        }

        public boolean equals(Object o) {
            return (o instanceof ReverseComparator2) && ((ReverseComparator2) o).cmp.equals(this.cmp);
        }

        public int hashCode() {
            return this.cmp.hashCode() ^ -1;
        }
    }

    private static final class ReverseComparator<T> implements Comparator<T>, Serializable {
        private static final ReverseComparator<Object> INSTANCE;
        private static final long serialVersionUID = 7207038068494060240L;

        private ReverseComparator() {
        }

        static {
            INSTANCE = new ReverseComparator();
        }

        public int compare(T o1, T o2) {
            return ((Comparable) o2).compareTo(o1);
        }

        private Object readResolve() throws ObjectStreamException {
            return INSTANCE;
        }
    }

    private static class SetFromMap<E> extends AbstractSet<E> implements Serializable {
        private static final long serialVersionUID = 2454657854757543876L;
        private transient Set<E> backingSet;
        private final Map<E, Boolean> m;

        SetFromMap(Map<E, Boolean> map) {
            this.m = map;
            this.backingSet = map.keySet();
        }

        public boolean equals(Object object) {
            return this.backingSet.equals(object);
        }

        public int hashCode() {
            return this.backingSet.hashCode();
        }

        public boolean add(E object) {
            return this.m.put(object, Boolean.TRUE) == null;
        }

        public void clear() {
            this.m.clear();
        }

        public String toString() {
            return this.backingSet.toString();
        }

        public boolean contains(Object object) {
            return this.backingSet.contains(object);
        }

        public boolean containsAll(Collection<?> collection) {
            return this.backingSet.containsAll(collection);
        }

        public boolean isEmpty() {
            return this.m.isEmpty();
        }

        public boolean remove(Object object) {
            return this.m.remove(object) != null;
        }

        public boolean retainAll(Collection<?> collection) {
            return this.backingSet.retainAll(collection);
        }

        public Object[] toArray() {
            return this.backingSet.toArray();
        }

        public <T> T[] toArray(T[] contents) {
            return this.backingSet.toArray(contents);
        }

        public Iterator<E> iterator() {
            return this.backingSet.iterator();
        }

        public int size() {
            return this.m.size();
        }

        private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
            stream.defaultReadObject();
            this.backingSet = this.m.keySet();
        }
    }

    private static final class SingletonList<E> extends AbstractList<E> implements Serializable {
        private static final long serialVersionUID = 3093736618740652951L;
        final E element;

        SingletonList(E object) {
            this.element = object;
        }

        public boolean contains(Object object) {
            if (this.element == null) {
                return object == null;
            } else {
                return this.element.equals(object);
            }
        }

        public E get(int location) {
            if (location == 0) {
                return this.element;
            }
            throw new IndexOutOfBoundsException();
        }

        public int size() {
            return 1;
        }
    }

    private static final class SingletonMap<K, V> extends AbstractMap<K, V> implements Serializable {
        private static final long serialVersionUID = -6979724477215052911L;
        final K k;
        final V v;

        /* renamed from: java.util.Collections.SingletonMap.1 */
        class AnonymousClass1 extends AbstractSet<Entry<K, V>> {
            final /* synthetic */ SingletonMap this$0;

            /* renamed from: java.util.Collections.SingletonMap.1.1 */
            class AnonymousClass1 implements Iterator<Entry<K, V>> {
                boolean hasNext;
                final /* synthetic */ AnonymousClass1 this$1;

                /* renamed from: java.util.Collections.SingletonMap.1.1.1 */
                class AnonymousClass1 extends MapEntry<K, V> {
                    final /* synthetic */ AnonymousClass1 this$2;

                    AnonymousClass1(java.util.Collections.SingletonMap.1.AnonymousClass1 r1, java.lang.Object r2, java.lang.Object r3) {
                        /* JADX: method processing error */
/*
                        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.SingletonMap.1.1.1.<init>(java.util.Collections$SingletonMap$1$1, java.lang.Object, java.lang.Object):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.SingletonMap.1.1.1.<init>(java.util.Collections$SingletonMap$1$1, java.lang.Object, java.lang.Object):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 11 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 12 more
*/
                        /*
                        // Can't load method instructions.
                        */
                        throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.SingletonMap.1.1.1.<init>(java.util.Collections$SingletonMap$1$1, java.lang.Object, java.lang.Object):void");
                    }

                    public V setValue(V v) {
                        throw new UnsupportedOperationException();
                    }
                }

                AnonymousClass1(java.util.Collections.SingletonMap.AnonymousClass1 r1) {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.SingletonMap.1.1.<init>(java.util.Collections$SingletonMap$1):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.SingletonMap.1.1.<init>(java.util.Collections$SingletonMap$1):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 10 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 11 more
*/
                    /*
                    // Can't load method instructions.
                    */
                    throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.SingletonMap.1.1.<init>(java.util.Collections$SingletonMap$1):void");
                }

                public /* bridge */ /* synthetic */ java.lang.Object next() {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.SingletonMap.1.1.next():java.lang.Object
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.SingletonMap.1.1.next():java.lang.Object
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 10 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 11 more
*/
                    /*
                    // Can't load method instructions.
                    */
                    throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.SingletonMap.1.1.next():java.lang.Object");
                }

                public java.util.Map.Entry<K, V> m0next() {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.SingletonMap.1.1.next():java.util.Map$Entry<K, V>
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.SingletonMap.1.1.next():java.util.Map$Entry<K, V>
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 10 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 11 more
*/
                    /*
                    // Can't load method instructions.
                    */
                    throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.SingletonMap.1.1.next():java.util.Map$Entry<K, V>");
                }

                public boolean hasNext() {
                    return this.hasNext;
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }
            }

            AnonymousClass1(java.util.Collections.SingletonMap r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.SingletonMap.1.<init>(java.util.Collections$SingletonMap):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.SingletonMap.1.<init>(java.util.Collections$SingletonMap):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.SingletonMap.1.<init>(java.util.Collections$SingletonMap):void");
            }

            public boolean contains(java.lang.Object r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.SingletonMap.1.contains(java.lang.Object):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.SingletonMap.1.contains(java.lang.Object):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.SingletonMap.1.contains(java.lang.Object):boolean");
            }

            public int size() {
                return 1;
            }

            public Iterator<Entry<K, V>> iterator() {
                return new AnonymousClass1(this);
            }
        }

        SingletonMap(K key, V value) {
            this.k = key;
            this.v = value;
        }

        public boolean containsKey(Object key) {
            if (this.k == null) {
                return key == null;
            } else {
                return this.k.equals(key);
            }
        }

        public boolean containsValue(Object value) {
            if (this.v == null) {
                return value == null;
            } else {
                return this.v.equals(value);
            }
        }

        public V get(Object key) {
            if (containsKey(key)) {
                return this.v;
            }
            return null;
        }

        public int size() {
            return 1;
        }

        public Set<Entry<K, V>> entrySet() {
            return new AnonymousClass1(this);
        }
    }

    private static final class SingletonSet<E> extends AbstractSet<E> implements Serializable {
        private static final long serialVersionUID = 3193687207550431679L;
        final E element;

        /* renamed from: java.util.Collections.SingletonSet.1 */
        class AnonymousClass1 implements Iterator<E> {
            boolean hasNext;
            final /* synthetic */ SingletonSet this$0;

            AnonymousClass1(java.util.Collections.SingletonSet r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.SingletonSet.1.<init>(java.util.Collections$SingletonSet):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.SingletonSet.1.<init>(java.util.Collections$SingletonSet):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.SingletonSet.1.<init>(java.util.Collections$SingletonSet):void");
            }

            public E next() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.SingletonSet.1.next():E
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.SingletonSet.1.next():E
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.SingletonSet.1.next():E");
            }

            public boolean hasNext() {
                return this.hasNext;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        }

        SingletonSet(E object) {
            this.element = object;
        }

        public boolean contains(Object object) {
            if (this.element == null) {
                return object == null;
            } else {
                return this.element.equals(object);
            }
        }

        public int size() {
            return 1;
        }

        public Iterator<E> iterator() {
            return new AnonymousClass1(this);
        }
    }

    static class SynchronizedCollection<E> implements Collection<E>, Serializable {
        private static final long serialVersionUID = 3053995032091335093L;
        final Collection<E> c;
        final Object mutex;

        SynchronizedCollection(Collection<E> collection) {
            this.c = collection;
            this.mutex = this;
        }

        SynchronizedCollection(Collection<E> collection, Object mutex) {
            this.c = collection;
            this.mutex = mutex;
        }

        public boolean add(E object) {
            boolean add;
            synchronized (this.mutex) {
                add = this.c.add(object);
            }
            return add;
        }

        public boolean addAll(Collection<? extends E> collection) {
            boolean addAll;
            synchronized (this.mutex) {
                addAll = this.c.addAll(collection);
            }
            return addAll;
        }

        public void clear() {
            synchronized (this.mutex) {
                this.c.clear();
            }
        }

        public boolean contains(Object object) {
            boolean contains;
            synchronized (this.mutex) {
                contains = this.c.contains(object);
            }
            return contains;
        }

        public boolean containsAll(Collection<?> collection) {
            boolean containsAll;
            synchronized (this.mutex) {
                containsAll = this.c.containsAll(collection);
            }
            return containsAll;
        }

        public boolean isEmpty() {
            boolean isEmpty;
            synchronized (this.mutex) {
                isEmpty = this.c.isEmpty();
            }
            return isEmpty;
        }

        public Iterator<E> iterator() {
            Iterator<E> it;
            synchronized (this.mutex) {
                it = this.c.iterator();
            }
            return it;
        }

        public boolean remove(Object object) {
            boolean remove;
            synchronized (this.mutex) {
                remove = this.c.remove(object);
            }
            return remove;
        }

        public boolean removeAll(Collection<?> collection) {
            boolean removeAll;
            synchronized (this.mutex) {
                removeAll = this.c.removeAll(collection);
            }
            return removeAll;
        }

        public boolean retainAll(Collection<?> collection) {
            boolean retainAll;
            synchronized (this.mutex) {
                retainAll = this.c.retainAll(collection);
            }
            return retainAll;
        }

        public int size() {
            int size;
            synchronized (this.mutex) {
                size = this.c.size();
            }
            return size;
        }

        public Object[] toArray() {
            Object[] toArray;
            synchronized (this.mutex) {
                toArray = this.c.toArray();
            }
            return toArray;
        }

        public String toString() {
            String obj;
            synchronized (this.mutex) {
                obj = this.c.toString();
            }
            return obj;
        }

        public <T> T[] toArray(T[] array) {
            T[] toArray;
            synchronized (this.mutex) {
                toArray = this.c.toArray(array);
            }
            return toArray;
        }

        private void writeObject(ObjectOutputStream stream) throws IOException {
            synchronized (this.mutex) {
                stream.defaultWriteObject();
            }
        }
    }

    static class SynchronizedList<E> extends SynchronizedCollection<E> implements List<E> {
        private static final long serialVersionUID = -7754090372962971524L;
        final List<E> list;

        SynchronizedList(List<E> l) {
            super(l);
            this.list = l;
        }

        SynchronizedList(List<E> l, Object mutex) {
            super(l, mutex);
            this.list = l;
        }

        public void add(int location, E object) {
            synchronized (this.mutex) {
                this.list.add(location, object);
            }
        }

        public boolean addAll(int location, Collection<? extends E> collection) {
            boolean addAll;
            synchronized (this.mutex) {
                addAll = this.list.addAll(location, collection);
            }
            return addAll;
        }

        public boolean equals(Object object) {
            boolean equals;
            synchronized (this.mutex) {
                equals = this.list.equals(object);
            }
            return equals;
        }

        public E get(int location) {
            E e;
            synchronized (this.mutex) {
                e = this.list.get(location);
            }
            return e;
        }

        public int hashCode() {
            int hashCode;
            synchronized (this.mutex) {
                hashCode = this.list.hashCode();
            }
            return hashCode;
        }

        public int indexOf(Object object) {
            synchronized (this.mutex) {
                int size = this.list.size();
                Object[] array = new Object[size];
                this.list.toArray(array);
            }
            int i;
            if (object != null) {
                for (i = 0; i < size; i++) {
                    if (object.equals(array[i])) {
                        return i;
                    }
                }
            } else {
                for (i = 0; i < size; i++) {
                    if (array[i] == null) {
                        return i;
                    }
                }
            }
            return -1;
        }

        public int lastIndexOf(Object object) {
            synchronized (this.mutex) {
                int size = this.list.size();
                Object[] array = new Object[size];
                this.list.toArray(array);
            }
            int i;
            if (object != null) {
                for (i = size - 1; i >= 0; i--) {
                    if (object.equals(array[i])) {
                        return i;
                    }
                }
            } else {
                for (i = size - 1; i >= 0; i--) {
                    if (array[i] == null) {
                        return i;
                    }
                }
            }
            return -1;
        }

        public ListIterator<E> listIterator() {
            ListIterator<E> listIterator;
            synchronized (this.mutex) {
                listIterator = this.list.listIterator();
            }
            return listIterator;
        }

        public ListIterator<E> listIterator(int location) {
            ListIterator<E> listIterator;
            synchronized (this.mutex) {
                listIterator = this.list.listIterator(location);
            }
            return listIterator;
        }

        public E remove(int location) {
            E remove;
            synchronized (this.mutex) {
                remove = this.list.remove(location);
            }
            return remove;
        }

        public E set(int location, E object) {
            E e;
            synchronized (this.mutex) {
                e = this.list.set(location, object);
            }
            return e;
        }

        public List<E> subList(int start, int end) {
            List synchronizedList;
            synchronized (this.mutex) {
                synchronizedList = new SynchronizedList(this.list.subList(start, end), this.mutex);
            }
            return synchronizedList;
        }

        private void writeObject(ObjectOutputStream stream) throws IOException {
            synchronized (this.mutex) {
                stream.defaultWriteObject();
            }
        }

        private Object readResolve() {
            if (this.list instanceof RandomAccess) {
                return new SynchronizedRandomAccessList(this.list, this.mutex);
            }
            return this;
        }
    }

    static class SynchronizedMap<K, V> implements Map<K, V>, Serializable {
        private static final long serialVersionUID = 1978198479659022715L;
        private final Map<K, V> m;
        final Object mutex;

        SynchronizedMap(Map<K, V> map) {
            this.m = map;
            this.mutex = this;
        }

        SynchronizedMap(Map<K, V> map, Object mutex) {
            this.m = map;
            this.mutex = mutex;
        }

        public void clear() {
            synchronized (this.mutex) {
                this.m.clear();
            }
        }

        public boolean containsKey(Object key) {
            boolean containsKey;
            synchronized (this.mutex) {
                containsKey = this.m.containsKey(key);
            }
            return containsKey;
        }

        public boolean containsValue(Object value) {
            boolean containsValue;
            synchronized (this.mutex) {
                containsValue = this.m.containsValue(value);
            }
            return containsValue;
        }

        public Set<Entry<K, V>> entrySet() {
            Set synchronizedSet;
            synchronized (this.mutex) {
                synchronizedSet = new SynchronizedSet(this.m.entrySet(), this.mutex);
            }
            return synchronizedSet;
        }

        public boolean equals(Object object) {
            boolean equals;
            synchronized (this.mutex) {
                equals = this.m.equals(object);
            }
            return equals;
        }

        public V get(Object key) {
            V v;
            synchronized (this.mutex) {
                v = this.m.get(key);
            }
            return v;
        }

        public int hashCode() {
            int hashCode;
            synchronized (this.mutex) {
                hashCode = this.m.hashCode();
            }
            return hashCode;
        }

        public boolean isEmpty() {
            boolean isEmpty;
            synchronized (this.mutex) {
                isEmpty = this.m.isEmpty();
            }
            return isEmpty;
        }

        public Set<K> keySet() {
            Set synchronizedSet;
            synchronized (this.mutex) {
                synchronizedSet = new SynchronizedSet(this.m.keySet(), this.mutex);
            }
            return synchronizedSet;
        }

        public V put(K key, V value) {
            V put;
            synchronized (this.mutex) {
                put = this.m.put(key, value);
            }
            return put;
        }

        public void putAll(Map<? extends K, ? extends V> map) {
            synchronized (this.mutex) {
                this.m.putAll(map);
            }
        }

        public V remove(Object key) {
            V remove;
            synchronized (this.mutex) {
                remove = this.m.remove(key);
            }
            return remove;
        }

        public int size() {
            int size;
            synchronized (this.mutex) {
                size = this.m.size();
            }
            return size;
        }

        public Collection<V> values() {
            Collection synchronizedCollection;
            synchronized (this.mutex) {
                synchronizedCollection = new SynchronizedCollection(this.m.values(), this.mutex);
            }
            return synchronizedCollection;
        }

        public String toString() {
            String obj;
            synchronized (this.mutex) {
                obj = this.m.toString();
            }
            return obj;
        }

        private void writeObject(ObjectOutputStream stream) throws IOException {
            synchronized (this.mutex) {
                stream.defaultWriteObject();
            }
        }
    }

    static class SynchronizedRandomAccessList<E> extends SynchronizedList<E> implements RandomAccess {
        private static final long serialVersionUID = 1530674583602358482L;

        SynchronizedRandomAccessList(List<E> l) {
            super(l);
        }

        SynchronizedRandomAccessList(List<E> l, Object mutex) {
            super(l, mutex);
        }

        public List<E> subList(int start, int end) {
            List synchronizedRandomAccessList;
            synchronized (this.mutex) {
                synchronizedRandomAccessList = new SynchronizedRandomAccessList(this.list.subList(start, end), this.mutex);
            }
            return synchronizedRandomAccessList;
        }

        private Object writeReplace() {
            return new SynchronizedList(this.list);
        }
    }

    static class SynchronizedSet<E> extends SynchronizedCollection<E> implements Set<E> {
        private static final long serialVersionUID = 487447009682186044L;

        SynchronizedSet(Set<E> set) {
            super(set);
        }

        SynchronizedSet(Set<E> set, Object mutex) {
            super(set, mutex);
        }

        public boolean equals(Object object) {
            boolean equals;
            synchronized (this.mutex) {
                equals = this.c.equals(object);
            }
            return equals;
        }

        public int hashCode() {
            int hashCode;
            synchronized (this.mutex) {
                hashCode = this.c.hashCode();
            }
            return hashCode;
        }

        private void writeObject(ObjectOutputStream stream) throws IOException {
            synchronized (this.mutex) {
                stream.defaultWriteObject();
            }
        }
    }

    static class SynchronizedSortedMap<K, V> extends SynchronizedMap<K, V> implements SortedMap<K, V> {
        private static final long serialVersionUID = -8798146769416483793L;
        private final SortedMap<K, V> sm;

        SynchronizedSortedMap(SortedMap<K, V> map) {
            super(map);
            this.sm = map;
        }

        SynchronizedSortedMap(SortedMap<K, V> map, Object mutex) {
            super(map, mutex);
            this.sm = map;
        }

        public Comparator<? super K> comparator() {
            Comparator<? super K> comparator;
            synchronized (this.mutex) {
                comparator = this.sm.comparator();
            }
            return comparator;
        }

        public K firstKey() {
            K firstKey;
            synchronized (this.mutex) {
                firstKey = this.sm.firstKey();
            }
            return firstKey;
        }

        public SortedMap<K, V> headMap(K endKey) {
            SortedMap synchronizedSortedMap;
            synchronized (this.mutex) {
                synchronizedSortedMap = new SynchronizedSortedMap(this.sm.headMap(endKey), this.mutex);
            }
            return synchronizedSortedMap;
        }

        public K lastKey() {
            K lastKey;
            synchronized (this.mutex) {
                lastKey = this.sm.lastKey();
            }
            return lastKey;
        }

        public SortedMap<K, V> subMap(K startKey, K endKey) {
            SortedMap synchronizedSortedMap;
            synchronized (this.mutex) {
                synchronizedSortedMap = new SynchronizedSortedMap(this.sm.subMap(startKey, endKey), this.mutex);
            }
            return synchronizedSortedMap;
        }

        public SortedMap<K, V> tailMap(K startKey) {
            SortedMap synchronizedSortedMap;
            synchronized (this.mutex) {
                synchronizedSortedMap = new SynchronizedSortedMap(this.sm.tailMap(startKey), this.mutex);
            }
            return synchronizedSortedMap;
        }

        private void writeObject(ObjectOutputStream stream) throws IOException {
            synchronized (this.mutex) {
                stream.defaultWriteObject();
            }
        }
    }

    static class SynchronizedSortedSet<E> extends SynchronizedSet<E> implements SortedSet<E> {
        private static final long serialVersionUID = 8695801310862127406L;
        private final SortedSet<E> ss;

        SynchronizedSortedSet(SortedSet<E> set) {
            super(set);
            this.ss = set;
        }

        SynchronizedSortedSet(SortedSet<E> set, Object mutex) {
            super(set, mutex);
            this.ss = set;
        }

        public Comparator<? super E> comparator() {
            Comparator<? super E> comparator;
            synchronized (this.mutex) {
                comparator = this.ss.comparator();
            }
            return comparator;
        }

        public E first() {
            E first;
            synchronized (this.mutex) {
                first = this.ss.first();
            }
            return first;
        }

        public SortedSet<E> headSet(E end) {
            SortedSet synchronizedSortedSet;
            synchronized (this.mutex) {
                synchronizedSortedSet = new SynchronizedSortedSet(this.ss.headSet(end), this.mutex);
            }
            return synchronizedSortedSet;
        }

        public E last() {
            E last;
            synchronized (this.mutex) {
                last = this.ss.last();
            }
            return last;
        }

        public SortedSet<E> subSet(E start, E end) {
            SortedSet synchronizedSortedSet;
            synchronized (this.mutex) {
                synchronizedSortedSet = new SynchronizedSortedSet(this.ss.subSet(start, end), this.mutex);
            }
            return synchronizedSortedSet;
        }

        public SortedSet<E> tailSet(E start) {
            SortedSet synchronizedSortedSet;
            synchronized (this.mutex) {
                synchronizedSortedSet = new SynchronizedSortedSet(this.ss.tailSet(start), this.mutex);
            }
            return synchronizedSortedSet;
        }

        private void writeObject(ObjectOutputStream stream) throws IOException {
            synchronized (this.mutex) {
                stream.defaultWriteObject();
            }
        }
    }

    private static class UnmodifiableCollection<E> implements Collection<E>, Serializable {
        private static final long serialVersionUID = 1820017752578914078L;
        final Collection<E> c;

        /* renamed from: java.util.Collections.UnmodifiableCollection.1 */
        class AnonymousClass1 implements Iterator<E> {
            Iterator<E> iterator;
            final /* synthetic */ UnmodifiableCollection this$0;

            AnonymousClass1(UnmodifiableCollection unmodifiableCollection) {
                this.this$0 = unmodifiableCollection;
                this.iterator = this.this$0.c.iterator();
            }

            public boolean hasNext() {
                return this.iterator.hasNext();
            }

            public E next() {
                return this.iterator.next();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        }

        UnmodifiableCollection(Collection<E> collection) {
            this.c = collection;
        }

        public boolean add(E e) {
            throw new UnsupportedOperationException();
        }

        public boolean addAll(Collection<? extends E> collection) {
            throw new UnsupportedOperationException();
        }

        public void clear() {
            throw new UnsupportedOperationException();
        }

        public boolean contains(Object object) {
            return this.c.contains(object);
        }

        public boolean containsAll(Collection<?> collection) {
            return this.c.containsAll(collection);
        }

        public boolean isEmpty() {
            return this.c.isEmpty();
        }

        public Iterator<E> iterator() {
            return new AnonymousClass1(this);
        }

        public boolean remove(Object object) {
            throw new UnsupportedOperationException();
        }

        public boolean removeAll(Collection<?> collection) {
            throw new UnsupportedOperationException();
        }

        public boolean retainAll(Collection<?> collection) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return this.c.size();
        }

        public Object[] toArray() {
            return this.c.toArray();
        }

        public <T> T[] toArray(T[] array) {
            return this.c.toArray(array);
        }

        public String toString() {
            return this.c.toString();
        }
    }

    private static class UnmodifiableList<E> extends UnmodifiableCollection<E> implements List<E> {
        private static final long serialVersionUID = -283967356065247728L;
        final List<E> list;

        /* renamed from: java.util.Collections.UnmodifiableList.1 */
        class AnonymousClass1 implements ListIterator<E> {
            ListIterator<E> iterator;
            final /* synthetic */ UnmodifiableList this$0;
            final /* synthetic */ int val$location;

            AnonymousClass1(java.util.Collections.UnmodifiableList r1, int r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.UnmodifiableList.1.<init>(java.util.Collections$UnmodifiableList, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.UnmodifiableList.1.<init>(java.util.Collections$UnmodifiableList, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.UnmodifiableList.1.<init>(java.util.Collections$UnmodifiableList, int):void");
            }

            public boolean hasNext() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.UnmodifiableList.1.hasNext():boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.UnmodifiableList.1.hasNext():boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.UnmodifiableList.1.hasNext():boolean");
            }

            public boolean hasPrevious() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.UnmodifiableList.1.hasPrevious():boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.UnmodifiableList.1.hasPrevious():boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.UnmodifiableList.1.hasPrevious():boolean");
            }

            public E next() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.UnmodifiableList.1.next():E
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.UnmodifiableList.1.next():E
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.UnmodifiableList.1.next():E");
            }

            public int nextIndex() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.UnmodifiableList.1.nextIndex():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.UnmodifiableList.1.nextIndex():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.UnmodifiableList.1.nextIndex():int");
            }

            public E previous() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.UnmodifiableList.1.previous():E
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.UnmodifiableList.1.previous():E
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.UnmodifiableList.1.previous():E");
            }

            public int previousIndex() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Collections.UnmodifiableList.1.previousIndex():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Collections.UnmodifiableList.1.previousIndex():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.Collections.UnmodifiableList.1.previousIndex():int");
            }

            public void add(E e) {
                throw new UnsupportedOperationException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

            public void set(E e) {
                throw new UnsupportedOperationException();
            }
        }

        UnmodifiableList(List<E> l) {
            super(l);
            this.list = l;
        }

        public void add(int location, E e) {
            throw new UnsupportedOperationException();
        }

        public boolean addAll(int location, Collection<? extends E> collection) {
            throw new UnsupportedOperationException();
        }

        public boolean equals(Object object) {
            return this.list.equals(object);
        }

        public E get(int location) {
            return this.list.get(location);
        }

        public int hashCode() {
            return this.list.hashCode();
        }

        public int indexOf(Object object) {
            return this.list.indexOf(object);
        }

        public int lastIndexOf(Object object) {
            return this.list.lastIndexOf(object);
        }

        public ListIterator<E> listIterator() {
            return listIterator(0);
        }

        public ListIterator<E> listIterator(int location) {
            return new AnonymousClass1(this, location);
        }

        public E remove(int location) {
            throw new UnsupportedOperationException();
        }

        public E set(int location, E e) {
            throw new UnsupportedOperationException();
        }

        public List<E> subList(int start, int end) {
            return new UnmodifiableList(this.list.subList(start, end));
        }

        private Object readResolve() {
            if (this.list instanceof RandomAccess) {
                return new UnmodifiableRandomAccessList(this.list);
            }
            return this;
        }
    }

    private static class UnmodifiableSet<E> extends UnmodifiableCollection<E> implements Set<E> {
        private static final long serialVersionUID = -9215047833775013803L;

        UnmodifiableSet(Set<E> set) {
            super(set);
        }

        public boolean equals(Object object) {
            return this.c.equals(object);
        }

        public int hashCode() {
            return this.c.hashCode();
        }
    }

    private static class UnmodifiableMap<K, V> implements Map<K, V>, Serializable {
        private static final long serialVersionUID = -1034234728574286014L;
        private final Map<K, V> m;

        private static class UnmodifiableEntrySet<K, V> extends UnmodifiableSet<Entry<K, V>> {
            private static final long serialVersionUID = 7854390611657943733L;

            /* renamed from: java.util.Collections.UnmodifiableMap.UnmodifiableEntrySet.1 */
            class AnonymousClass1 implements Iterator<Entry<K, V>> {
                Iterator<Entry<K, V>> iterator;
                final /* synthetic */ UnmodifiableEntrySet this$0;

                AnonymousClass1(UnmodifiableEntrySet unmodifiableEntrySet) {
                    this.this$0 = unmodifiableEntrySet;
                    this.iterator = this.this$0.c.iterator();
                }

                public /* bridge */ /* synthetic */ Object next() {
                    return next();
                }

                public boolean hasNext() {
                    return this.iterator.hasNext();
                }

                public Entry<K, V> m1next() {
                    return new UnmodifiableMapEntry((Entry) this.iterator.next());
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }
            }

            private static class UnmodifiableMapEntry<K, V> implements Entry<K, V> {
                Entry<K, V> mapEntry;

                UnmodifiableMapEntry(Entry<K, V> entry) {
                    this.mapEntry = entry;
                }

                public boolean equals(Object object) {
                    return this.mapEntry.equals(object);
                }

                public K getKey() {
                    return this.mapEntry.getKey();
                }

                public V getValue() {
                    return this.mapEntry.getValue();
                }

                public int hashCode() {
                    return this.mapEntry.hashCode();
                }

                public V setValue(V v) {
                    throw new UnsupportedOperationException();
                }

                public String toString() {
                    return this.mapEntry.toString();
                }
            }

            UnmodifiableEntrySet(Set<Entry<K, V>> set) {
                super(set);
            }

            public Iterator<Entry<K, V>> iterator() {
                return new AnonymousClass1(this);
            }

            public Object[] toArray() {
                int length = this.c.size();
                Object[] result = new Object[length];
                Iterator<?> it = iterator();
                for (int i = 0; i < length; i++) {
                    result[i] = it.next();
                }
                return result;
            }

            public <T> T[] toArray(T[] contents) {
                int index;
                int size = this.c.size();
                Iterator<Entry<K, V>> it = iterator();
                if (size > contents.length) {
                    contents = (Object[]) ((Object[]) Array.newInstance(contents.getClass().getComponentType(), size));
                    index = 0;
                } else {
                    index = 0;
                }
                while (index < size) {
                    int index2 = index + 1;
                    contents[index] = it.next();
                    index = index2;
                }
                if (index < contents.length) {
                    contents[index] = null;
                }
                return contents;
            }
        }

        UnmodifiableMap(Map<K, V> map) {
            this.m = map;
        }

        public void clear() {
            throw new UnsupportedOperationException();
        }

        public boolean containsKey(Object key) {
            return this.m.containsKey(key);
        }

        public boolean containsValue(Object value) {
            return this.m.containsValue(value);
        }

        public Set<Entry<K, V>> entrySet() {
            return new UnmodifiableEntrySet(this.m.entrySet());
        }

        public boolean equals(Object object) {
            return this.m.equals(object);
        }

        public V get(Object key) {
            return this.m.get(key);
        }

        public int hashCode() {
            return this.m.hashCode();
        }

        public boolean isEmpty() {
            return this.m.isEmpty();
        }

        public Set<K> keySet() {
            return new UnmodifiableSet(this.m.keySet());
        }

        public V put(K k, V v) {
            throw new UnsupportedOperationException();
        }

        public void putAll(Map<? extends K, ? extends V> map) {
            throw new UnsupportedOperationException();
        }

        public V remove(Object key) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return this.m.size();
        }

        public Collection<V> values() {
            return new UnmodifiableCollection(this.m.values());
        }

        public String toString() {
            return this.m.toString();
        }
    }

    private static class UnmodifiableRandomAccessList<E> extends UnmodifiableList<E> implements RandomAccess {
        private static final long serialVersionUID = -2542308836966382001L;

        UnmodifiableRandomAccessList(List<E> l) {
            super(l);
        }

        public List<E> subList(int start, int end) {
            return new UnmodifiableRandomAccessList(this.list.subList(start, end));
        }

        private Object writeReplace() {
            return new UnmodifiableList(this.list);
        }
    }

    private static class UnmodifiableSortedMap<K, V> extends UnmodifiableMap<K, V> implements SortedMap<K, V> {
        private static final long serialVersionUID = -8806743815996713206L;
        private final SortedMap<K, V> sm;

        UnmodifiableSortedMap(SortedMap<K, V> map) {
            super(map);
            this.sm = map;
        }

        public Comparator<? super K> comparator() {
            return this.sm.comparator();
        }

        public K firstKey() {
            return this.sm.firstKey();
        }

        public SortedMap<K, V> headMap(K before) {
            return new UnmodifiableSortedMap(this.sm.headMap(before));
        }

        public K lastKey() {
            return this.sm.lastKey();
        }

        public SortedMap<K, V> subMap(K start, K end) {
            return new UnmodifiableSortedMap(this.sm.subMap(start, end));
        }

        public SortedMap<K, V> tailMap(K after) {
            return new UnmodifiableSortedMap(this.sm.tailMap(after));
        }
    }

    private static class UnmodifiableSortedSet<E> extends UnmodifiableSet<E> implements SortedSet<E> {
        private static final long serialVersionUID = -4929149591599911165L;
        private final SortedSet<E> ss;

        UnmodifiableSortedSet(SortedSet<E> set) {
            super(set);
            this.ss = set;
        }

        public Comparator<? super E> comparator() {
            return this.ss.comparator();
        }

        public E first() {
            return this.ss.first();
        }

        public SortedSet<E> headSet(E before) {
            return new UnmodifiableSortedSet(this.ss.headSet(before));
        }

        public E last() {
            return this.ss.last();
        }

        public SortedSet<E> subSet(E start, E end) {
            return new UnmodifiableSortedSet(this.ss.subSet(start, end));
        }

        public SortedSet<E> tailSet(E after) {
            return new UnmodifiableSortedSet(this.ss.tailSet(after));
        }
    }

    static {
        EMPTY_ITERATOR = new Iterator<Object>() {
            public boolean hasNext() {
                return false;
            }

            public Object next() {
                throw new NoSuchElementException();
            }

            public void remove() {
                throw new IllegalStateException();
            }
        };
        EMPTY_ENUMERATION = new Enumeration<Object>() {
            public boolean hasMoreElements() {
                return false;
            }

            public Object nextElement() {
                throw new NoSuchElementException();
            }
        };
        EMPTY_LIST = new EmptyList();
        EMPTY_SET = new EmptySet();
        EMPTY_MAP = new EmptyMap();
    }

    private Collections() {
    }

    public static <T> int binarySearch(List<? extends Comparable<? super T>> list, T object) {
        if (list == null) {
            throw new NullPointerException("list == null");
        } else if (list.isEmpty()) {
            return -1;
        } else {
            int result;
            if (list instanceof RandomAccess) {
                int low = 0;
                int mid = list.size();
                int high = mid - 1;
                result = -1;
                while (low <= high) {
                    mid = (low + high) >>> 1;
                    result = -((Comparable) list.get(mid)).compareTo(object);
                    if (result > 0) {
                        low = mid + 1;
                    } else if (result == 0) {
                        return mid;
                    } else {
                        high = mid - 1;
                    }
                }
                return (-mid) - (result < 0 ? 1 : 2);
            }
            ListIterator<? extends Comparable<? super T>> it = list.listIterator();
            while (it.hasNext()) {
                result = -((Comparable) it.next()).compareTo(object);
                if (result <= 0) {
                    if (result == 0) {
                        return it.previousIndex();
                    }
                    return (-it.previousIndex()) - 1;
                }
            }
            return (-list.size()) - 1;
        }
    }

    public static <T> int binarySearch(List<? extends T> list, T object, Comparator<? super T> comparator) {
        if (comparator == null) {
            return binarySearch(list, object);
        }
        int result;
        if (list instanceof RandomAccess) {
            int low = 0;
            int mid = list.size();
            int high = mid - 1;
            result = -1;
            while (low <= high) {
                mid = (low + high) >>> 1;
                result = -comparator.compare(list.get(mid), object);
                if (result > 0) {
                    low = mid + 1;
                } else if (result == 0) {
                    return mid;
                } else {
                    high = mid - 1;
                }
            }
            return (-mid) - (result < 0 ? 1 : 2);
        }
        ListIterator<? extends T> it = list.listIterator();
        while (it.hasNext()) {
            result = -comparator.compare(it.next(), object);
            if (result <= 0) {
                if (result == 0) {
                    return it.previousIndex();
                }
                return (-it.previousIndex()) - 1;
            }
        }
        return (-list.size()) - 1;
    }

    public static <T> void copy(List<? super T> destination, List<? extends T> source) {
        if (destination.size() < source.size()) {
            throw new IndexOutOfBoundsException("destination.size() < source.size(): " + destination.size() + " < " + source.size());
        }
        ListIterator<? super T> destIt = destination.listIterator();
        for (Object obj : source) {
            try {
                destIt.next();
                destIt.set(obj);
            } catch (NoSuchElementException e) {
                throw new IndexOutOfBoundsException("Source size " + source.size() + " does not fit into destination");
            }
        }
    }

    public static <T> Enumeration<T> enumeration(Collection<T> collection) {
        return new AnonymousClass3(collection);
    }

    public static <T> void fill(List<? super T> list, T object) {
        ListIterator<? super T> it = list.listIterator();
        while (it.hasNext()) {
            it.next();
            it.set(object);
        }
    }

    public static <T extends Comparable<? super T>> T max(Collection<? extends T> collection) {
        Iterator<? extends T> it = collection.iterator();
        T max = it.next();
        while (it.hasNext()) {
            T next = it.next();
            if (((Comparable) max).compareTo(next) < 0) {
                max = next;
            }
        }
        return max;
    }

    public static <T> T max(Collection<? extends T> collection, Comparator<? super T> comparator) {
        if (comparator == null) {
            return max(collection);
        }
        Iterator<? extends T> it = collection.iterator();
        T max = it.next();
        while (it.hasNext()) {
            T next = it.next();
            if (comparator.compare(max, next) < 0) {
                max = next;
            }
        }
        return max;
    }

    public static <T extends Comparable<? super T>> T min(Collection<? extends T> collection) {
        Iterator<? extends T> it = collection.iterator();
        T min = it.next();
        while (it.hasNext()) {
            T next = it.next();
            if (((Comparable) min).compareTo(next) > 0) {
                min = next;
            }
        }
        return min;
    }

    public static <T> T min(Collection<? extends T> collection, Comparator<? super T> comparator) {
        if (comparator == null) {
            return min(collection);
        }
        Iterator<? extends T> it = collection.iterator();
        T min = it.next();
        while (it.hasNext()) {
            T next = it.next();
            if (comparator.compare(min, next) > 0) {
                min = next;
            }
        }
        return min;
    }

    public static <T> List<T> nCopies(int length, T object) {
        return new CopiesList(length, object);
    }

    public static void reverse(List<?> list) {
        int size = list.size();
        ListIterator<Object> front = list.listIterator();
        ListIterator<Object> back = list.listIterator(size);
        for (int i = 0; i < size / 2; i++) {
            Object frontNext = front.next();
            front.set(back.previous());
            back.set(frontNext);
        }
    }

    public static <T> Comparator<T> reverseOrder() {
        return ReverseComparator.INSTANCE;
    }

    public static <T> Comparator<T> reverseOrder(Comparator<T> c) {
        if (c == null) {
            return reverseOrder();
        }
        if (c instanceof ReverseComparator2) {
            return ((ReverseComparator2) c).cmp;
        }
        return new ReverseComparator2(c);
    }

    public static void shuffle(List<?> list) {
        shuffle(list, new Random());
    }

    public static void shuffle(List<?> list, Random random) {
        List<Object> objectList = list;
        int i;
        if (list instanceof RandomAccess) {
            for (i = objectList.size() - 1; i > 0; i--) {
                int index = random.nextInt(i + 1);
                objectList.set(index, objectList.set(i, objectList.get(index)));
            }
            return;
        }
        Object[] array = objectList.toArray();
        for (i = array.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            Object temp = array[i];
            array[i] = array[index];
            array[index] = temp;
        }
        i = 0;
        ListIterator<Object> it = objectList.listIterator();
        while (it.hasNext()) {
            it.next();
            int i2 = i + 1;
            it.set(array[i]);
            i = i2;
        }
    }

    public static <E> Set<E> singleton(E object) {
        return new SingletonSet(object);
    }

    public static <E> List<E> singletonList(E object) {
        return new SingletonList(object);
    }

    public static <K, V> Map<K, V> singletonMap(K key, V value) {
        return new SingletonMap(key, value);
    }

    public static <T extends Comparable<? super T>> void sort(List<T> list) {
        Object[] array = list.toArray();
        Arrays.sort(array);
        int i = 0;
        ListIterator<T> it = list.listIterator();
        while (it.hasNext()) {
            it.next();
            int i2 = i + 1;
            it.set((Comparable) array[i]);
            i = i2;
        }
    }

    public static <T> void sort(List<T> list, Comparator<? super T> comparator) {
        T[] array = list.toArray(new Object[list.size()]);
        Arrays.sort(array, comparator);
        int i = 0;
        ListIterator<T> it = list.listIterator();
        while (it.hasNext()) {
            it.next();
            int i2 = i + 1;
            it.set(array[i]);
            i = i2;
        }
    }

    public static void swap(List<?> list, int index1, int index2) {
        if (list == null) {
            throw new NullPointerException("list == null");
        }
        int size = list.size();
        if (index1 < 0 || index1 >= size || index2 < 0 || index2 >= size) {
            throw new IndexOutOfBoundsException();
        } else if (index1 != index2) {
            List<Object> rawList = list;
            rawList.set(index2, rawList.set(index1, rawList.get(index2)));
        }
    }

    public static <T> boolean replaceAll(List<T> list, T obj, T obj2) {
        boolean found = false;
        while (true) {
            int index = list.indexOf(obj);
            if (index <= -1) {
                return found;
            }
            found = true;
            list.set(index, obj2);
        }
    }

    public static void rotate(List<?> lst, int dist) {
        List<Object> list = lst;
        int size = list.size();
        if (size != 0) {
            int normdist;
            if (dist > 0) {
                normdist = dist % size;
            } else {
                normdist = size - ((dist % size) * -1);
            }
            if (normdist != 0 && normdist != size) {
                if (list instanceof RandomAccess) {
                    Object temp = list.get(0);
                    int index = 0;
                    int beginIndex = 0;
                    for (int i = 0; i < size; i++) {
                        index = (index + normdist) % size;
                        temp = list.set(index, temp);
                        if (index == beginIndex) {
                            beginIndex++;
                            index = beginIndex;
                            temp = list.get(beginIndex);
                        }
                    }
                    return;
                }
                int divideIndex = (size - normdist) % size;
                List<Object> sublist1 = list.subList(0, divideIndex);
                List<Object> sublist2 = list.subList(divideIndex, size);
                reverse(sublist1);
                reverse(sublist2);
                reverse(list);
            }
        }
    }

    public static int indexOfSubList(List<?> list, List<?> sublist) {
        int size = list.size();
        int sublistSize = sublist.size();
        if (sublistSize > size) {
            return -1;
        }
        if (sublistSize == 0) {
            return 0;
        }
        Object firstObj = sublist.get(0);
        int index = list.indexOf(firstObj);
        if (index == -1) {
            return -1;
        }
        while (index < size && size - index >= sublistSize) {
            ListIterator<?> listIt = list.listIterator(index);
            ListIterator<?> sublistIt;
            boolean difFound;
            Object element;
            if (firstObj == null) {
                if (listIt.next() != null) {
                    continue;
                }
                sublistIt = sublist.listIterator(1);
                difFound = false;
                while (sublistIt.hasNext()) {
                    element = sublistIt.next();
                    if (listIt.hasNext()) {
                        if (element != null) {
                            if (listIt.next() == null) {
                            }
                        } else if (element.equals(listIt.next())) {
                        }
                        difFound = true;
                        break;
                    }
                    return -1;
                }
                if (difFound) {
                    return index;
                }
            } else if (firstObj.equals(listIt.next())) {
                sublistIt = sublist.listIterator(1);
                difFound = false;
                while (sublistIt.hasNext()) {
                    element = sublistIt.next();
                    if (listIt.hasNext()) {
                        return -1;
                    }
                    if (element != null) {
                        if (element.equals(listIt.next())) {
                        }
                    } else if (listIt.next() == null) {
                    }
                    difFound = true;
                    break;
                    if (difFound) {
                        return index;
                    }
                }
                if (difFound) {
                    return index;
                }
            } else {
                continue;
            }
            index++;
        }
        return -1;
    }

    public static int lastIndexOfSubList(List<?> list, List<?> sublist) {
        int sublistSize = sublist.size();
        int size = list.size();
        if (sublistSize > size) {
            return -1;
        }
        if (sublistSize == 0) {
            return size;
        }
        Object lastObj = sublist.get(sublistSize - 1);
        int index = list.lastIndexOf(lastObj);
        while (index > -1 && index + 1 >= sublistSize) {
            ListIterator<?> listIt = list.listIterator(index + 1);
            if (lastObj == null) {
                if (listIt.previous() != null) {
                    continue;
                    index--;
                }
            } else if (!lastObj.equals(listIt.previous())) {
                continue;
                index--;
            }
            ListIterator<?> sublistIt = sublist.listIterator(sublistSize - 1);
            boolean difFound = false;
            while (sublistIt.hasPrevious()) {
                Object element = sublistIt.previous();
                if (listIt.hasPrevious()) {
                    if (element == null) {
                        if (listIt.previous() != null) {
                        }
                    } else if (element.equals(listIt.previous())) {
                    }
                    difFound = true;
                    break;
                }
                return -1;
            }
            if (!difFound) {
                return listIt.nextIndex();
            }
            index--;
        }
        return -1;
    }

    public static <T> ArrayList<T> list(Enumeration<T> enumeration) {
        ArrayList<T> list = new ArrayList();
        while (enumeration.hasMoreElements()) {
            list.add(enumeration.nextElement());
        }
        return list;
    }

    public static <T> Collection<T> synchronizedCollection(Collection<T> collection) {
        if (collection != null) {
            return new SynchronizedCollection(collection);
        }
        throw new NullPointerException("collection == null");
    }

    public static <T> List<T> synchronizedList(List<T> list) {
        if (list == null) {
            throw new NullPointerException("list == null");
        } else if (list instanceof RandomAccess) {
            return new SynchronizedRandomAccessList(list);
        } else {
            return new SynchronizedList(list);
        }
    }

    public static <K, V> Map<K, V> synchronizedMap(Map<K, V> map) {
        if (map != null) {
            return new SynchronizedMap(map);
        }
        throw new NullPointerException("map == null");
    }

    public static <E> Set<E> synchronizedSet(Set<E> set) {
        if (set != null) {
            return new SynchronizedSet(set);
        }
        throw new NullPointerException("set == null");
    }

    public static <K, V> SortedMap<K, V> synchronizedSortedMap(SortedMap<K, V> map) {
        if (map != null) {
            return new SynchronizedSortedMap(map);
        }
        throw new NullPointerException("map == null");
    }

    public static <E> SortedSet<E> synchronizedSortedSet(SortedSet<E> set) {
        if (set != null) {
            return new SynchronizedSortedSet(set);
        }
        throw new NullPointerException("set == null");
    }

    public static <E> Collection<E> unmodifiableCollection(Collection<? extends E> collection) {
        if (collection != null) {
            return new UnmodifiableCollection(collection);
        }
        throw new NullPointerException("collection == null");
    }

    public static <E> List<E> unmodifiableList(List<? extends E> list) {
        if (list == null) {
            throw new NullPointerException("list == null");
        } else if (list instanceof RandomAccess) {
            return new UnmodifiableRandomAccessList(list);
        } else {
            return new UnmodifiableList(list);
        }
    }

    public static <K, V> Map<K, V> unmodifiableMap(Map<? extends K, ? extends V> map) {
        if (map != null) {
            return new UnmodifiableMap(map);
        }
        throw new NullPointerException("map == null");
    }

    public static <E> Set<E> unmodifiableSet(Set<? extends E> set) {
        if (set != null) {
            return new UnmodifiableSet(set);
        }
        throw new NullPointerException("set == null");
    }

    public static <K, V> SortedMap<K, V> unmodifiableSortedMap(SortedMap<K, ? extends V> map) {
        if (map != null) {
            return new UnmodifiableSortedMap(map);
        }
        throw new NullPointerException("map == null");
    }

    public static <E> SortedSet<E> unmodifiableSortedSet(SortedSet<E> set) {
        if (set != null) {
            return new UnmodifiableSortedSet(set);
        }
        throw new NullPointerException("set == null");
    }

    public static int frequency(Collection<?> c, Object o) {
        if (c == null) {
            throw new NullPointerException("c == null");
        } else if (c.isEmpty()) {
            return 0;
        } else {
            int result = 0;
            for (Object e : c) {
                if (o == null) {
                    if (e != null) {
                    }
                } else if (o.equals(e)) {
                }
                result++;
            }
            return result;
        }
    }

    public static final <T> List<T> emptyList() {
        return EMPTY_LIST;
    }

    public static final <T> Set<T> emptySet() {
        return EMPTY_SET;
    }

    public static final <K, V> Map<K, V> emptyMap() {
        return EMPTY_MAP;
    }

    public static <T> Enumeration<T> emptyEnumeration() {
        return EMPTY_ENUMERATION;
    }

    public static <T> Iterator<T> emptyIterator() {
        return EMPTY_ITERATOR;
    }

    public static <T> ListIterator<T> emptyListIterator() {
        return emptyList().listIterator();
    }

    public static <E> Collection<E> checkedCollection(Collection<E> c, Class<E> type) {
        return new CheckedCollection(c, type);
    }

    public static <K, V> Map<K, V> checkedMap(Map<K, V> m, Class<K> keyType, Class<V> valueType) {
        return new CheckedMap(keyType, valueType, null);
    }

    public static <E> List<E> checkedList(List<E> list, Class<E> type) {
        if (list instanceof RandomAccess) {
            return new CheckedRandomAccessList(list, type);
        }
        return new CheckedList(list, type);
    }

    public static <E> Set<E> checkedSet(Set<E> s, Class<E> type) {
        return new CheckedSet(s, type);
    }

    public static <K, V> SortedMap<K, V> checkedSortedMap(SortedMap<K, V> m, Class<K> keyType, Class<V> valueType) {
        return new CheckedSortedMap(m, keyType, valueType);
    }

    public static <E> SortedSet<E> checkedSortedSet(SortedSet<E> s, Class<E> type) {
        return new CheckedSortedSet(s, type);
    }

    @SafeVarargs
    public static <T> boolean addAll(Collection<? super T> c, T... a) {
        boolean modified = false;
        for (Object add : a) {
            modified |= c.add(add);
        }
        return modified;
    }

    public static boolean disjoint(Collection<?> c1, Collection<?> c2) {
        if (((c1 instanceof Set) && !(c2 instanceof Set)) || c2.size() > c1.size()) {
            Collection<?> tmp = c1;
            c1 = c2;
            c2 = tmp;
        }
        for (Object contains : c1) {
            if (c2.contains(contains)) {
                return false;
            }
        }
        return true;
    }

    static <E> E checkType(E obj, Class<? extends E> type) {
        if (obj == null || type.isInstance(obj)) {
            return obj;
        }
        throw new ClassCastException("Attempt to insert element of type " + obj.getClass() + " into collection of type " + type);
    }

    public static <E> Set<E> newSetFromMap(Map<E, Boolean> map) {
        if (map.isEmpty()) {
            return new SetFromMap(map);
        }
        throw new IllegalArgumentException("map not empty");
    }

    public static <T> Queue<T> asLifoQueue(Deque<T> deque) {
        return new AsLIFOQueue(deque);
    }

    public static int secondaryHash(Object key) {
        return secondaryHash(key.hashCode());
    }

    public static int secondaryIdentityHash(Object key) {
        return secondaryHash(System.identityHashCode(key));
    }

    private static int secondaryHash(int h) {
        h += (h << 15) ^ -12931;
        h ^= h >>> 10;
        h += h << 3;
        h ^= h >>> 6;
        h += (h << 2) + (h << 14);
        return (h >>> 16) ^ h;
    }

    public static int roundUpToPowerOfTwo(int i) {
        i--;
        i |= i >>> 1;
        i |= i >>> 2;
        i |= i >>> 4;
        i |= i >>> 8;
        return (i | (i >>> 16)) + 1;
    }
}
