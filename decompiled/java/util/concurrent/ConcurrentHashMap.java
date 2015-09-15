package java.util.concurrent;

import dalvik.system.VMDebug;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import sun.misc.Unsafe;

public class ConcurrentHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable {
    private static final long ABASE = 0;
    private static final int ASHIFT = 0;
    private static final long BASECOUNT = 0;
    private static final long CELLSBUSY = 0;
    private static final long CELLVALUE = 0;
    private static final int DEFAULT_CAPACITY = 16;
    private static final int DEFAULT_CONCURRENCY_LEVEL = 16;
    static final int HASH_BITS = Integer.MAX_VALUE;
    private static final float LOAD_FACTOR = 0.75f;
    private static final int MAXIMUM_CAPACITY = 1073741824;
    static final int MAX_ARRAY_SIZE = 2147483639;
    private static final int MIN_TRANSFER_STRIDE = 16;
    static final int MIN_TREEIFY_CAPACITY = 64;
    static final int MOVED = -1879048193;
    static final int NCPU = 0;
    static final int RESERVED = -2147483647;
    static final int SEED_INCREMENT = 1640531527;
    private static final long SIZECTL = 0;
    private static final long TRANSFERINDEX = 0;
    private static final long TRANSFERORIGIN = 0;
    static final int TREEBIN = Integer.MIN_VALUE;
    static final int TREEIFY_THRESHOLD = 8;
    private static final Unsafe U = null;
    static final int UNTREEIFY_THRESHOLD = 6;
    static final AtomicInteger counterHashCodeGenerator = null;
    private static final ObjectStreamField[] serialPersistentFields = null;
    private static final long serialVersionUID = 7249069246763182397L;
    static final ThreadLocal<CounterHashCode> threadCounterHashCode = null;
    private volatile transient long baseCount;
    private volatile transient int cellsBusy;
    private volatile transient CounterCell[] counterCells;
    private transient EntrySetView<K, V> entrySet;
    private transient KeySetView<K, V> keySet;
    private volatile transient Node<K, V>[] nextTable;
    private volatile transient int sizeCtl;
    volatile transient Node<K, V>[] table;
    private volatile transient int transferIndex;
    private volatile transient int transferOrigin;
    private transient ValuesView<K, V> values;

    static class Traverser<K, V> {
        int baseIndex;
        int baseLimit;
        final int baseSize;
        int index;
        Node<K, V> next;
        Node<K, V>[] tab;

        Traverser(Node<K, V>[] tab, int size, int index, int limit) {
            this.tab = tab;
            this.baseSize = size;
            this.index = index;
            this.baseIndex = index;
            this.baseLimit = limit;
            this.next = null;
        }

        final Node<K, V> advance() {
            Node<K, V> e = this.next;
            if (e != null) {
                e = e.next;
            }
            while (e == null) {
                if (this.baseIndex < this.baseLimit) {
                    Node<K, V>[] t = this.tab;
                    if (t != null) {
                        int n = t.length;
                        int i = this.index;
                        if (n > i && i >= 0) {
                            e = ConcurrentHashMap.tabAt(t, this.index);
                            if (e != null && e.hash < 0) {
                                if (e instanceof ForwardingNode) {
                                    this.tab = ((ForwardingNode) e).nextTable;
                                    e = null;
                                } else {
                                    e = e instanceof TreeBin ? ((TreeBin) e).first : null;
                                }
                            }
                            int i2 = this.index + this.baseSize;
                            this.index = i2;
                            if (i2 >= n) {
                                i2 = this.baseIndex + 1;
                                this.baseIndex = i2;
                                this.index = i2;
                            }
                        }
                    }
                }
                this.next = null;
                return null;
            }
            this.next = e;
            return e;
        }
    }

    static class BaseIterator<K, V> extends Traverser<K, V> {
        Node<K, V> lastReturned;
        final ConcurrentHashMap<K, V> map;

        BaseIterator(Node<K, V>[] tab, int size, int index, int limit, ConcurrentHashMap<K, V> map) {
            super(tab, size, index, limit);
            this.map = map;
            advance();
        }

        public final boolean hasNext() {
            return this.next != null;
        }

        public final boolean hasMoreElements() {
            return this.next != null;
        }

        public final void remove() {
            Node<K, V> p = this.lastReturned;
            if (p == null) {
                throw new IllegalStateException();
            }
            this.lastReturned = null;
            this.map.replaceNode(p.key, null, null);
        }
    }

    static abstract class CollectionView<K, V, E> implements Collection<E>, Serializable {
        private static final String oomeMsg = "Required array size too large";
        private static final long serialVersionUID = 7249069246763182397L;
        final ConcurrentHashMap<K, V> map;

        public abstract boolean contains(Object obj);

        public abstract Iterator<E> iterator();

        public abstract boolean remove(Object obj);

        CollectionView(ConcurrentHashMap<K, V> map) {
            this.map = map;
        }

        public ConcurrentHashMap<K, V> getMap() {
            return this.map;
        }

        public final void clear() {
            this.map.clear();
        }

        public final int size() {
            return this.map.size();
        }

        public final boolean isEmpty() {
            return this.map.isEmpty();
        }

        public final Object[] toArray() {
            long sz = this.map.mappingCount();
            if (sz > 2147483639) {
                throw new OutOfMemoryError(oomeMsg);
            }
            int n = (int) sz;
            Object[] r = new Object[n];
            int i = ConcurrentHashMap.NCPU;
            Iterator i$ = iterator();
            while (i$.hasNext()) {
                E e = i$.next();
                if (i == n) {
                    if (n >= ConcurrentHashMap.MAX_ARRAY_SIZE) {
                        throw new OutOfMemoryError(oomeMsg);
                    }
                    if (n >= 1073741819) {
                        n = ConcurrentHashMap.MAX_ARRAY_SIZE;
                    } else {
                        n += (n >>> 1) + 1;
                    }
                    r = Arrays.copyOf(r, n);
                }
                int i2 = i + 1;
                r[i] = e;
                i = i2;
            }
            return i == n ? r : Arrays.copyOf(r, i);
        }

        public final <T> T[] toArray(T[] a) {
            long sz = this.map.mappingCount();
            if (sz > 2147483639) {
                throw new OutOfMemoryError(oomeMsg);
            }
            int m = (int) sz;
            Object[] r = a.length >= m ? a : (Object[]) Array.newInstance(a.getClass().getComponentType(), m);
            int n = r.length;
            int i = ConcurrentHashMap.NCPU;
            Iterator i$ = iterator();
            while (i$.hasNext()) {
                E e = i$.next();
                if (i == n) {
                    if (n >= ConcurrentHashMap.MAX_ARRAY_SIZE) {
                        throw new OutOfMemoryError(oomeMsg);
                    }
                    if (n >= 1073741819) {
                        n = ConcurrentHashMap.MAX_ARRAY_SIZE;
                    } else {
                        n += (n >>> 1) + 1;
                    }
                    r = Arrays.copyOf(r, n);
                }
                int i2 = i + 1;
                r[i] = e;
                i = i2;
            }
            if (a != r || i >= n) {
                return i != n ? Arrays.copyOf(r, i) : r;
            } else {
                r[i] = null;
                return r;
            }
        }

        public final String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            Iterator<E> it = iterator();
            if (it.hasNext()) {
                while (true) {
                    Object e = it.next();
                    if (e == this) {
                        e = "(this Collection)";
                    }
                    sb.append(e);
                    if (!it.hasNext()) {
                        break;
                    }
                    sb.append(',').append(' ');
                }
            }
            return sb.append(']').toString();
        }

        public final boolean containsAll(Collection<?> c) {
            if (c != this) {
                for (Object e : c) {
                    if (e != null) {
                        if (!contains(e)) {
                        }
                    }
                    return false;
                }
            }
            return true;
        }

        public final boolean removeAll(Collection<?> c) {
            boolean modified = false;
            Iterator<E> it = iterator();
            while (it.hasNext()) {
                if (c.contains(it.next())) {
                    it.remove();
                    modified = true;
                }
            }
            return modified;
        }

        public final boolean retainAll(Collection<?> c) {
            boolean modified = false;
            Iterator<E> it = iterator();
            while (it.hasNext()) {
                if (!c.contains(it.next())) {
                    it.remove();
                    modified = true;
                }
            }
            return modified;
        }
    }

    static final class CounterCell {
        volatile long p0;
        volatile long p1;
        volatile long p2;
        volatile long p3;
        volatile long p4;
        volatile long p5;
        volatile long p6;
        volatile long q0;
        volatile long q1;
        volatile long q2;
        volatile long q3;
        volatile long q4;
        volatile long q5;
        volatile long q6;
        volatile long value;

        CounterCell(long x) {
            this.value = x;
        }
    }

    static final class CounterHashCode {
        int code;

        CounterHashCode() {
        }
    }

    static final class EntryIterator<K, V> extends BaseIterator<K, V> implements Iterator<Entry<K, V>> {
        EntryIterator(Node<K, V>[] tab, int index, int size, int limit, ConcurrentHashMap<K, V> map) {
            super(tab, index, size, limit, map);
        }

        public final Entry<K, V> next() {
            Node<K, V> p = this.next;
            if (p == null) {
                throw new NoSuchElementException();
            }
            K k = p.key;
            V v = p.val;
            this.lastReturned = p;
            advance();
            return new MapEntry(k, v, this.map);
        }
    }

    static final class EntrySetView<K, V> extends CollectionView<K, V, Entry<K, V>> implements Set<Entry<K, V>>, Serializable {
        private static final long serialVersionUID = 2249069246763182397L;

        EntrySetView(ConcurrentHashMap<K, V> map) {
            super(map);
        }

        public boolean contains(Object o) {
            if (o instanceof Entry) {
                Entry<?, ?> e = (Entry) o;
                Object k = e.getKey();
                if (k != null) {
                    Object r = this.map.get(k);
                    if (r != null) {
                        Object v = e.getValue();
                        if (v != null && (v == r || v.equals(r))) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        public boolean remove(Object o) {
            if (o instanceof Entry) {
                Entry<?, ?> e = (Entry) o;
                Object k = e.getKey();
                if (k != null) {
                    Object v = e.getValue();
                    if (v != null && this.map.remove(k, v)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public Iterator<Entry<K, V>> iterator() {
            ConcurrentHashMap<K, V> m = this.map;
            Node<K, V>[] t = m.table;
            int f = t == null ? ConcurrentHashMap.NCPU : t.length;
            return new EntryIterator(t, f, ConcurrentHashMap.NCPU, f, m);
        }

        public boolean add(Entry<K, V> e) {
            return this.map.putVal(e.getKey(), e.getValue(), false) == null;
        }

        public boolean addAll(Collection<? extends Entry<K, V>> c) {
            boolean added = false;
            for (Entry e : c) {
                if (add(e)) {
                    added = true;
                }
            }
            return added;
        }

        public final int hashCode() {
            int h = ConcurrentHashMap.NCPU;
            Node<K, V>[] t = this.map.table;
            if (t != null) {
                Traverser<K, V> it = new Traverser(t, t.length, ConcurrentHashMap.NCPU, t.length);
                while (true) {
                    Node<K, V> p = it.advance();
                    if (p == null) {
                        break;
                    }
                    h += p.hashCode();
                }
            }
            return h;
        }

        public final boolean equals(Object o) {
            if (o instanceof Set) {
                Object c = (Set) o;
                if (c == this || (containsAll(c) && c.containsAll(this))) {
                    return true;
                }
            }
            return false;
        }
    }

    static class Node<K, V> implements Entry<K, V> {
        final int hash;
        final K key;
        Node<K, V> next;
        volatile V val;

        Node(int hash, K key, V val, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.val = val;
            this.next = next;
        }

        public final K getKey() {
            return this.key;
        }

        public final V getValue() {
            return this.val;
        }

        public final int hashCode() {
            return this.key.hashCode() ^ this.val.hashCode();
        }

        public final String toString() {
            return this.key + "=" + this.val;
        }

        public final V setValue(V v) {
            throw new UnsupportedOperationException();
        }

        public final boolean equals(Object o) {
            if (o instanceof Entry) {
                Entry<?, ?> e = (Entry) o;
                Object k = e.getKey();
                if (k != null) {
                    Object v = e.getValue();
                    if (v != null && (k == this.key || k.equals(this.key))) {
                        Object u = this.val;
                        if (v == u || v.equals(u)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        Node<K, V> find(int h, Object k) {
            Node<K, V> e = this;
            if (k != null) {
                do {
                    if (e.hash == h) {
                        K ek = e.key;
                        if (ek == k || (ek != null && k.equals(ek))) {
                            return e;
                        }
                    }
                    e = e.next;
                } while (e != null);
            }
            return null;
        }
    }

    static final class ForwardingNode<K, V> extends Node<K, V> {
        final Node<K, V>[] nextTable;

        ForwardingNode(Node<K, V>[] tab) {
            super(ConcurrentHashMap.MOVED, null, null, null);
            this.nextTable = tab;
        }

        Node<K, V> find(int h, Object k) {
            Node<K, V>[] tab = this.nextTable;
            if (!(k == null || tab == null)) {
                int n = tab.length;
                if (n > 0) {
                    Node<K, V> e = ConcurrentHashMap.tabAt(tab, (n - 1) & h);
                    if (e != null) {
                        do {
                            int eh = e.hash;
                            if (eh == h) {
                                K ek = e.key;
                                if (ek == k) {
                                    return e;
                                }
                                if (ek != null && k.equals(ek)) {
                                    return e;
                                }
                            }
                            if (eh < 0) {
                                return e.find(h, k);
                            }
                            e = e.next;
                        } while (e != null);
                    }
                }
            }
            return null;
        }
    }

    static final class KeyIterator<K, V> extends BaseIterator<K, V> implements Iterator<K>, Enumeration<K> {
        KeyIterator(Node<K, V>[] tab, int index, int size, int limit, ConcurrentHashMap<K, V> map) {
            super(tab, index, size, limit, map);
        }

        public final K next() {
            Node<K, V> p = this.next;
            if (p == null) {
                throw new NoSuchElementException();
            }
            K k = p.key;
            this.lastReturned = p;
            advance();
            return k;
        }

        public final K nextElement() {
            return next();
        }
    }

    public static class KeySetView<K, V> extends CollectionView<K, V, K> implements Set<K>, Serializable {
        private static final long serialVersionUID = 7249069246763182397L;
        private final V value;

        public /* bridge */ /* synthetic */ ConcurrentHashMap getMap() {
            return super.getMap();
        }

        KeySetView(ConcurrentHashMap<K, V> map, V value) {
            super(map);
            this.value = value;
        }

        public V getMappedValue() {
            return this.value;
        }

        public boolean contains(Object o) {
            return this.map.containsKey(o);
        }

        public boolean remove(Object o) {
            return this.map.remove(o) != null;
        }

        public Iterator<K> iterator() {
            ConcurrentHashMap<K, V> m = this.map;
            Node<K, V>[] t = m.table;
            int f = t == null ? ConcurrentHashMap.NCPU : t.length;
            return new KeyIterator(t, f, ConcurrentHashMap.NCPU, f, m);
        }

        public boolean add(K e) {
            V v = this.value;
            if (v == null) {
                throw new UnsupportedOperationException();
            } else if (this.map.putVal(e, v, true) == null) {
                return true;
            } else {
                return false;
            }
        }

        public boolean addAll(Collection<? extends K> c) {
            boolean added = false;
            V v = this.value;
            if (v == null) {
                throw new UnsupportedOperationException();
            }
            for (K e : c) {
                if (this.map.putVal(e, v, true) == null) {
                    added = true;
                }
            }
            return added;
        }

        public int hashCode() {
            int h = ConcurrentHashMap.NCPU;
            Iterator i$ = iterator();
            while (i$.hasNext()) {
                h += i$.next().hashCode();
            }
            return h;
        }

        public boolean equals(Object o) {
            if (o instanceof Set) {
                Object c = (Set) o;
                if (c == this || (containsAll(c) && c.containsAll(this))) {
                    return true;
                }
            }
            return false;
        }
    }

    static final class MapEntry<K, V> implements Entry<K, V> {
        final K key;
        final ConcurrentHashMap<K, V> map;
        V val;

        MapEntry(K key, V val, ConcurrentHashMap<K, V> map) {
            this.key = key;
            this.val = val;
            this.map = map;
        }

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            return this.val;
        }

        public int hashCode() {
            return this.key.hashCode() ^ this.val.hashCode();
        }

        public String toString() {
            return this.key + "=" + this.val;
        }

        public boolean equals(Object o) {
            if (o instanceof Entry) {
                Entry<?, ?> e = (Entry) o;
                Object k = e.getKey();
                if (k != null) {
                    Object v = e.getValue();
                    if (v != null && ((k == this.key || k.equals(this.key)) && (v == this.val || v.equals(this.val)))) {
                        return true;
                    }
                }
            }
            return false;
        }

        public V setValue(V value) {
            if (value == null) {
                throw new NullPointerException();
            }
            V v = this.val;
            this.val = value;
            this.map.put(this.key, value);
            return v;
        }
    }

    static final class ReservationNode<K, V> extends Node<K, V> {
        ReservationNode() {
            super(ConcurrentHashMap.RESERVED, null, null, null);
        }

        Node<K, V> find(int h, Object k) {
            return null;
        }
    }

    static class Segment<K, V> extends ReentrantLock implements Serializable {
        private static final long serialVersionUID = 2249069246763182397L;
        final float loadFactor;

        Segment(float lf) {
            this.loadFactor = lf;
        }
    }

    static final class TreeBin<K, V> extends Node<K, V> {
        static final /* synthetic */ boolean $assertionsDisabled;
        private static final long LOCKSTATE;
        static final int READER = 4;
        private static final Unsafe U;
        static final int WAITER = 2;
        static final int WRITER = 1;
        volatile TreeNode<K, V> first;
        volatile int lockState;
        TreeNode<K, V> root;
        volatile Thread waiter;

        TreeBin(java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.ConcurrentHashMap.TreeBin.<init>(java.util.concurrent.ConcurrentHashMap$TreeNode):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.ConcurrentHashMap.TreeBin.<init>(java.util.concurrent.ConcurrentHashMap$TreeNode):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.TreeBin.<init>(java.util.concurrent.ConcurrentHashMap$TreeNode):void");
        }

        static <K, V> java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> balanceDeletion(java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> r1, java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.ConcurrentHashMap.TreeBin.balanceDeletion(java.util.concurrent.ConcurrentHashMap$TreeNode, java.util.concurrent.ConcurrentHashMap$TreeNode):java.util.concurrent.ConcurrentHashMap$TreeNode<K, V>
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.ConcurrentHashMap.TreeBin.balanceDeletion(java.util.concurrent.ConcurrentHashMap$TreeNode, java.util.concurrent.ConcurrentHashMap$TreeNode):java.util.concurrent.ConcurrentHashMap$TreeNode<K, V>
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.TreeBin.balanceDeletion(java.util.concurrent.ConcurrentHashMap$TreeNode, java.util.concurrent.ConcurrentHashMap$TreeNode):java.util.concurrent.ConcurrentHashMap$TreeNode<K, V>");
        }

        static <K, V> java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> balanceInsertion(java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> r1, java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.ConcurrentHashMap.TreeBin.balanceInsertion(java.util.concurrent.ConcurrentHashMap$TreeNode, java.util.concurrent.ConcurrentHashMap$TreeNode):java.util.concurrent.ConcurrentHashMap$TreeNode<K, V>
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.ConcurrentHashMap.TreeBin.balanceInsertion(java.util.concurrent.ConcurrentHashMap$TreeNode, java.util.concurrent.ConcurrentHashMap$TreeNode):java.util.concurrent.ConcurrentHashMap$TreeNode<K, V>
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.TreeBin.balanceInsertion(java.util.concurrent.ConcurrentHashMap$TreeNode, java.util.concurrent.ConcurrentHashMap$TreeNode):java.util.concurrent.ConcurrentHashMap$TreeNode<K, V>");
        }

        static <K, V> boolean checkInvariants(java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.ConcurrentHashMap.TreeBin.checkInvariants(java.util.concurrent.ConcurrentHashMap$TreeNode):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.ConcurrentHashMap.TreeBin.checkInvariants(java.util.concurrent.ConcurrentHashMap$TreeNode):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.TreeBin.checkInvariants(java.util.concurrent.ConcurrentHashMap$TreeNode):boolean");
        }

        private final void contendedLock() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.ConcurrentHashMap.TreeBin.contendedLock():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.ConcurrentHashMap.TreeBin.contendedLock():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00ea
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.TreeBin.contendedLock():void");
        }

        private final void lockRoot() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.ConcurrentHashMap.TreeBin.lockRoot():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.ConcurrentHashMap.TreeBin.lockRoot():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00ea
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.TreeBin.lockRoot():void");
        }

        static <K, V> java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> rotateLeft(java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> r1, java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.ConcurrentHashMap.TreeBin.rotateLeft(java.util.concurrent.ConcurrentHashMap$TreeNode, java.util.concurrent.ConcurrentHashMap$TreeNode):java.util.concurrent.ConcurrentHashMap$TreeNode<K, V>
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.ConcurrentHashMap.TreeBin.rotateLeft(java.util.concurrent.ConcurrentHashMap$TreeNode, java.util.concurrent.ConcurrentHashMap$TreeNode):java.util.concurrent.ConcurrentHashMap$TreeNode<K, V>
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.TreeBin.rotateLeft(java.util.concurrent.ConcurrentHashMap$TreeNode, java.util.concurrent.ConcurrentHashMap$TreeNode):java.util.concurrent.ConcurrentHashMap$TreeNode<K, V>");
        }

        static <K, V> java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> rotateRight(java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> r1, java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.ConcurrentHashMap.TreeBin.rotateRight(java.util.concurrent.ConcurrentHashMap$TreeNode, java.util.concurrent.ConcurrentHashMap$TreeNode):java.util.concurrent.ConcurrentHashMap$TreeNode<K, V>
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.ConcurrentHashMap.TreeBin.rotateRight(java.util.concurrent.ConcurrentHashMap$TreeNode, java.util.concurrent.ConcurrentHashMap$TreeNode):java.util.concurrent.ConcurrentHashMap$TreeNode<K, V>
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.TreeBin.rotateRight(java.util.concurrent.ConcurrentHashMap$TreeNode, java.util.concurrent.ConcurrentHashMap$TreeNode):java.util.concurrent.ConcurrentHashMap$TreeNode<K, V>");
        }

        final java.util.concurrent.ConcurrentHashMap.Node<K, V> find(int r1, java.lang.Object r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.ConcurrentHashMap.TreeBin.find(int, java.lang.Object):java.util.concurrent.ConcurrentHashMap$Node<K, V>
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.ConcurrentHashMap.TreeBin.find(int, java.lang.Object):java.util.concurrent.ConcurrentHashMap$Node<K, V>
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.TreeBin.find(int, java.lang.Object):java.util.concurrent.ConcurrentHashMap$Node<K, V>");
        }

        final java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> putTreeVal(int r1, K r2, V r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.ConcurrentHashMap.TreeBin.putTreeVal(int, java.lang.Object, java.lang.Object):java.util.concurrent.ConcurrentHashMap$TreeNode<K, V>
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.ConcurrentHashMap.TreeBin.putTreeVal(int, java.lang.Object, java.lang.Object):java.util.concurrent.ConcurrentHashMap$TreeNode<K, V>
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.TreeBin.putTreeVal(int, java.lang.Object, java.lang.Object):java.util.concurrent.ConcurrentHashMap$TreeNode<K, V>");
        }

        final boolean removeTreeNode(java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.ConcurrentHashMap.TreeBin.removeTreeNode(java.util.concurrent.ConcurrentHashMap$TreeNode):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.ConcurrentHashMap.TreeBin.removeTreeNode(java.util.concurrent.ConcurrentHashMap$TreeNode):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.TreeBin.removeTreeNode(java.util.concurrent.ConcurrentHashMap$TreeNode):boolean");
        }

        static {
            boolean z;
            if (ConcurrentHashMap.class.desiredAssertionStatus()) {
                z = $assertionsDisabled;
            } else {
                z = true;
            }
            $assertionsDisabled = z;
            try {
                U = Unsafe.getUnsafe();
                LOCKSTATE = U.objectFieldOffset(TreeBin.class.getDeclaredField("lockState"));
            } catch (Throwable e) {
                throw new Error(e);
            }
        }

        private final void unlockRoot() {
            this.lockState = ConcurrentHashMap.NCPU;
        }
    }

    static final class TreeNode<K, V> extends Node<K, V> {
        TreeNode<K, V> left;
        TreeNode<K, V> parent;
        TreeNode<K, V> prev;
        boolean red;
        TreeNode<K, V> right;

        TreeNode(int r1, K r2, V r3, java.util.concurrent.ConcurrentHashMap.Node<K, V> r4, java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> r5) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.ConcurrentHashMap.TreeNode.<init>(int, java.lang.Object, java.lang.Object, java.util.concurrent.ConcurrentHashMap$Node, java.util.concurrent.ConcurrentHashMap$TreeNode):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.ConcurrentHashMap.TreeNode.<init>(int, java.lang.Object, java.lang.Object, java.util.concurrent.ConcurrentHashMap$Node, java.util.concurrent.ConcurrentHashMap$TreeNode):void
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.TreeNode.<init>(int, java.lang.Object, java.lang.Object, java.util.concurrent.ConcurrentHashMap$Node, java.util.concurrent.ConcurrentHashMap$TreeNode):void");
        }

        java.util.concurrent.ConcurrentHashMap.Node<K, V> find(int r1, java.lang.Object r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.ConcurrentHashMap.TreeNode.find(int, java.lang.Object):java.util.concurrent.ConcurrentHashMap$Node<K, V>
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.ConcurrentHashMap.TreeNode.find(int, java.lang.Object):java.util.concurrent.ConcurrentHashMap$Node<K, V>
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.TreeNode.find(int, java.lang.Object):java.util.concurrent.ConcurrentHashMap$Node<K, V>");
        }

        final java.util.concurrent.ConcurrentHashMap.TreeNode<K, V> findTreeNode(int r1, java.lang.Object r2, java.lang.Class<?> r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.ConcurrentHashMap.TreeNode.findTreeNode(int, java.lang.Object, java.lang.Class):java.util.concurrent.ConcurrentHashMap$TreeNode<K, V>
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.ConcurrentHashMap.TreeNode.findTreeNode(int, java.lang.Object, java.lang.Class):java.util.concurrent.ConcurrentHashMap$TreeNode<K, V>
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.TreeNode.findTreeNode(int, java.lang.Object, java.lang.Class):java.util.concurrent.ConcurrentHashMap$TreeNode<K, V>");
        }
    }

    static final class ValueIterator<K, V> extends BaseIterator<K, V> implements Iterator<V>, Enumeration<V> {
        ValueIterator(Node<K, V>[] tab, int index, int size, int limit, ConcurrentHashMap<K, V> map) {
            super(tab, index, size, limit, map);
        }

        public final V next() {
            Node<K, V> p = this.next;
            if (p == null) {
                throw new NoSuchElementException();
            }
            V v = p.val;
            this.lastReturned = p;
            advance();
            return v;
        }

        public final V nextElement() {
            return next();
        }
    }

    static final class ValuesView<K, V> extends CollectionView<K, V, V> implements Collection<V>, Serializable {
        private static final long serialVersionUID = 2249069246763182397L;

        ValuesView(ConcurrentHashMap<K, V> map) {
            super(map);
        }

        public final boolean contains(Object o) {
            return this.map.containsValue(o);
        }

        public final boolean remove(Object o) {
            if (o != null) {
                Iterator<V> it = iterator();
                while (it.hasNext()) {
                    if (o.equals(it.next())) {
                        it.remove();
                        return true;
                    }
                }
            }
            return false;
        }

        public final Iterator<V> iterator() {
            ConcurrentHashMap<K, V> m = this.map;
            Node<K, V>[] t = m.table;
            int f = t == null ? ConcurrentHashMap.NCPU : t.length;
            return new ValueIterator(t, f, ConcurrentHashMap.NCPU, f, m);
        }

        public final boolean add(V v) {
            throw new UnsupportedOperationException();
        }

        public final boolean addAll(Collection<? extends V> collection) {
            throw new UnsupportedOperationException();
        }
    }

    static {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.concurrent.ConcurrentHashMap.<clinit>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.concurrent.ConcurrentHashMap.<clinit>():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.<clinit>():void");
    }

    static final int spread(int h) {
        return ((h >>> MIN_TRANSFER_STRIDE) ^ h) & HASH_BITS;
    }

    private static final int tableSizeFor(int c) {
        int n = c - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> TREEIFY_THRESHOLD;
        n |= n >>> MIN_TRANSFER_STRIDE;
        if (n < 0) {
            return 1;
        }
        if (n < MAXIMUM_CAPACITY) {
            return n + 1;
        }
        return MAXIMUM_CAPACITY;
    }

    static Class<?> comparableClassFor(Object x) {
        if (x instanceof Comparable) {
            Type c = x.getClass();
            if (c == String.class) {
                return c;
            }
            Type[] ts = c.getGenericInterfaces();
            if (ts != null) {
                for (int i = NCPU; i < ts.length; i++) {
                    Type t = ts[i];
                    if (t instanceof ParameterizedType) {
                        ParameterizedType p = (ParameterizedType) t;
                        if (p.getRawType() == Comparable.class) {
                            Type[] as = p.getActualTypeArguments();
                            if (as != null && as.length == 1 && as[NCPU] == c) {
                                return c;
                            }
                        }
                        continue;
                    }
                }
            }
        }
        return null;
    }

    static int compareComparables(Class<?> kc, Object k, Object x) {
        return (x == null || x.getClass() != kc) ? NCPU : ((Comparable) k).compareTo(x);
    }

    static final <K, V> Node<K, V> tabAt(Node<K, V>[] tab, int i) {
        return (Node) U.getObjectVolatile(tab, (((long) i) << ASHIFT) + ABASE);
    }

    static final <K, V> boolean casTabAt(Node<K, V>[] tab, int i, Node<K, V> c, Node<K, V> v) {
        return U.compareAndSwapObject(tab, (((long) i) << ASHIFT) + ABASE, c, v);
    }

    static final <K, V> void setTabAt(Node<K, V>[] tab, int i, Node<K, V> v) {
        U.putOrderedObject(tab, (((long) i) << ASHIFT) + ABASE, v);
    }

    public ConcurrentHashMap(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException();
        }
        this.sizeCtl = initialCapacity >= VMDebug.KIND_THREAD_EXT_ALLOCATED_BYTES ? MAXIMUM_CAPACITY : tableSizeFor(((initialCapacity >>> 1) + initialCapacity) + 1);
    }

    public ConcurrentHashMap(Map<? extends K, ? extends V> m) {
        this.sizeCtl = MIN_TRANSFER_STRIDE;
        putAll(m);
    }

    public ConcurrentHashMap(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, 1);
    }

    public ConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        if (loadFactor <= 0.0f || initialCapacity < 0 || concurrencyLevel <= 0) {
            throw new IllegalArgumentException();
        }
        if (initialCapacity < concurrencyLevel) {
            initialCapacity = concurrencyLevel;
        }
        long size = (long) (1.0d + ((double) (((float) ((long) initialCapacity)) / loadFactor)));
        this.sizeCtl = size >= 1073741824 ? MAXIMUM_CAPACITY : tableSizeFor((int) size);
    }

    public int size() {
        long n = sumCount();
        if (n < TRANSFERORIGIN) {
            return NCPU;
        }
        return n > 2147483647L ? HASH_BITS : (int) n;
    }

    public boolean isEmpty() {
        return sumCount() <= TRANSFERORIGIN;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public V get(java.lang.Object r10) {
        /*
        r9 = this;
        r7 = 0;
        r8 = r10.hashCode();
        r3 = spread(r8);
        r6 = r9.table;
        if (r6 == 0) goto L_0x002b;
    L_0x000d:
        r4 = r6.length;
        if (r4 <= 0) goto L_0x002b;
    L_0x0010:
        r8 = r4 + -1;
        r8 = r8 & r3;
        r0 = tabAt(r6, r8);
        if (r0 == 0) goto L_0x002b;
    L_0x0019:
        r1 = r0.hash;
        if (r1 != r3) goto L_0x002c;
    L_0x001d:
        r2 = r0.key;
        if (r2 == r10) goto L_0x0029;
    L_0x0021:
        if (r2 == 0) goto L_0x0037;
    L_0x0023:
        r8 = r10.equals(r2);
        if (r8 == 0) goto L_0x0037;
    L_0x0029:
        r7 = r0.val;
    L_0x002b:
        return r7;
    L_0x002c:
        if (r1 >= 0) goto L_0x0037;
    L_0x002e:
        r5 = r0.find(r3, r10);
        if (r5 == 0) goto L_0x002b;
    L_0x0034:
        r7 = r5.val;
        goto L_0x002b;
    L_0x0037:
        r0 = r0.next;
        if (r0 == 0) goto L_0x002b;
    L_0x003b:
        r8 = r0.hash;
        if (r8 != r3) goto L_0x0037;
    L_0x003f:
        r2 = r0.key;
        if (r2 == r10) goto L_0x004b;
    L_0x0043:
        if (r2 == 0) goto L_0x0037;
    L_0x0045:
        r8 = r10.equals(r2);
        if (r8 == 0) goto L_0x0037;
    L_0x004b:
        r7 = r0.val;
        goto L_0x002b;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.get(java.lang.Object):V");
    }

    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean containsValue(java.lang.Object r8) {
        /*
        r7 = this;
        r4 = 0;
        if (r8 != 0) goto L_0x0009;
    L_0x0003:
        r4 = new java.lang.NullPointerException;
        r4.<init>();
        throw r4;
    L_0x0009:
        r2 = r7.table;
        if (r2 == 0) goto L_0x0027;
    L_0x000d:
        r0 = new java.util.concurrent.ConcurrentHashMap$Traverser;
        r5 = r2.length;
        r6 = r2.length;
        r0.<init>(r2, r5, r4, r6);
    L_0x0014:
        r1 = r0.advance();
        if (r1 == 0) goto L_0x0027;
    L_0x001a:
        r3 = r1.val;
        if (r3 == r8) goto L_0x0026;
    L_0x001e:
        if (r3 == 0) goto L_0x0014;
    L_0x0020:
        r5 = r8.equals(r3);
        if (r5 == 0) goto L_0x0014;
    L_0x0026:
        r4 = 1;
    L_0x0027:
        return r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.containsValue(java.lang.Object):boolean");
    }

    public V put(K key, V value) {
        return putVal(key, value, false);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final V putVal(K r20, V r21, boolean r22) {
        /*
        r19 = this;
        if (r20 == 0) goto L_0x0004;
    L_0x0002:
        if (r21 != 0) goto L_0x000a;
    L_0x0004:
        r16 = new java.lang.NullPointerException;
        r16.<init>();
        throw r16;
    L_0x000a:
        r16 = r20.hashCode();
        r9 = spread(r16);
        r4 = 0;
        r0 = r19;
        r15 = r0.table;
    L_0x0017:
        if (r15 == 0) goto L_0x001c;
    L_0x0019:
        r11 = r15.length;
        if (r11 != 0) goto L_0x0021;
    L_0x001c:
        r15 = r19.initTable();
        goto L_0x0017;
    L_0x0021:
        r16 = r11 + -1;
        r10 = r16 & r9;
        r7 = tabAt(r15, r10);
        if (r7 != 0) goto L_0x0052;
    L_0x002b:
        r16 = 0;
        r17 = new java.util.concurrent.ConcurrentHashMap$Node;
        r18 = 0;
        r0 = r17;
        r1 = r20;
        r2 = r21;
        r3 = r18;
        r0.<init>(r9, r1, r2, r3);
        r0 = r16;
        r1 = r17;
        r16 = casTabAt(r15, r10, r0, r1);
        if (r16 == 0) goto L_0x0017;
    L_0x0046:
        r16 = 1;
        r0 = r19;
        r1 = r16;
        r0.addCount(r1, r4);
        r16 = 0;
    L_0x0051:
        return r16;
    L_0x0052:
        r8 = r7.hash;
        r16 = -1879048193; // 0xffffffff8fffffff float:-2.5243547E-29 double:NaN;
        r0 = r16;
        if (r8 != r0) goto L_0x0062;
    L_0x005b:
        r0 = r19;
        r15 = r0.helpTransfer(r15, r7);
        goto L_0x0017;
    L_0x0062:
        r12 = 0;
        monitor-enter(r7);
        r16 = tabAt(r15, r10);	 Catch:{ all -> 0x00bc }
        r0 = r16;
        if (r0 != r7) goto L_0x00e5;
    L_0x006c:
        if (r8 < 0) goto L_0x00c2;
    L_0x006e:
        r4 = 1;
        r5 = r7;
    L_0x0070:
        r0 = r5.hash;	 Catch:{ all -> 0x00bc }
        r16 = r0;
        r0 = r16;
        if (r0 != r9) goto L_0x00a3;
    L_0x0078:
        r6 = r5.key;	 Catch:{ all -> 0x00bc }
        r0 = r20;
        if (r6 == r0) goto L_0x0088;
    L_0x007e:
        if (r6 == 0) goto L_0x00a3;
    L_0x0080:
        r0 = r20;
        r16 = r0.equals(r6);	 Catch:{ all -> 0x00bc }
        if (r16 == 0) goto L_0x00a3;
    L_0x0088:
        r12 = r5.val;	 Catch:{ all -> 0x00bc }
        if (r22 != 0) goto L_0x0090;
    L_0x008c:
        r0 = r21;
        r5.val = r0;	 Catch:{ all -> 0x00bc }
    L_0x0090:
        r16 = r12;
    L_0x0092:
        monitor-exit(r7);	 Catch:{ all -> 0x00bc }
        if (r4 == 0) goto L_0x0017;
    L_0x0095:
        r17 = 8;
        r0 = r17;
        if (r4 < r0) goto L_0x00a0;
    L_0x009b:
        r0 = r19;
        r0.treeifyBin(r15, r10);
    L_0x00a0:
        if (r16 == 0) goto L_0x0046;
    L_0x00a2:
        goto L_0x0051;
    L_0x00a3:
        r14 = r5;
        r5 = r5.next;	 Catch:{ all -> 0x00bc }
        if (r5 != 0) goto L_0x00bf;
    L_0x00a8:
        r16 = new java.util.concurrent.ConcurrentHashMap$Node;	 Catch:{ all -> 0x00bc }
        r17 = 0;
        r0 = r16;
        r1 = r20;
        r2 = r21;
        r3 = r17;
        r0.<init>(r9, r1, r2, r3);	 Catch:{ all -> 0x00bc }
        r0 = r16;
        r14.next = r0;	 Catch:{ all -> 0x00bc }
        goto L_0x0090;
    L_0x00bc:
        r16 = move-exception;
        monitor-exit(r7);	 Catch:{ all -> 0x00bc }
        throw r16;
    L_0x00bf:
        r4 = r4 + 1;
        goto L_0x0070;
    L_0x00c2:
        r0 = r7 instanceof java.util.concurrent.ConcurrentHashMap.TreeBin;	 Catch:{ all -> 0x00bc }
        r16 = r0;
        if (r16 == 0) goto L_0x00e5;
    L_0x00c8:
        r4 = 2;
        r0 = r7;
        r0 = (java.util.concurrent.ConcurrentHashMap.TreeBin) r0;	 Catch:{ all -> 0x00bc }
        r16 = r0;
        r0 = r16;
        r1 = r20;
        r2 = r21;
        r13 = r0.putTreeVal(r9, r1, r2);	 Catch:{ all -> 0x00bc }
        if (r13 == 0) goto L_0x00e5;
    L_0x00da:
        r12 = r13.val;	 Catch:{ all -> 0x00bc }
        if (r22 != 0) goto L_0x00e2;
    L_0x00de:
        r0 = r21;
        r13.val = r0;	 Catch:{ all -> 0x00bc }
    L_0x00e2:
        r16 = r12;
        goto L_0x0092;
    L_0x00e5:
        r16 = r12;
        goto L_0x0092;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.putVal(java.lang.Object, java.lang.Object, boolean):V");
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        tryPresize(m.size());
        for (Entry<? extends K, ? extends V> e : m.entrySet()) {
            putVal(e.getKey(), e.getValue(), false);
        }
    }

    public V remove(Object key) {
        return replaceNode(key, null, null);
    }

    final V replaceNode(Object key, V value, Object cv) {
        int hash = spread(key.hashCode());
        Node<K, V>[] tab = this.table;
        while (tab != null) {
            int n = tab.length;
            if (n == 0) {
                break;
            }
            int i = (n - 1) & hash;
            Node<K, V> f = tabAt(tab, i);
            if (f == null) {
                break;
            }
            int fh = f.hash;
            if (fh == MOVED) {
                tab = helpTransfer(tab, f);
            } else {
                V v;
                V oldVal = null;
                boolean validated = false;
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        if (fh >= 0) {
                            validated = true;
                            Node<K, V> e = f;
                            Node<K, V> pred = null;
                            do {
                                int i2 = e.hash;
                                if (r0 == hash) {
                                    K ek = e.key;
                                    if (ek == key || (ek != null && key.equals(ek))) {
                                        V ev = e.val;
                                        if (cv == null || cv == ev || (ev != null && cv.equals(ev))) {
                                            oldVal = ev;
                                            if (value != null) {
                                                e.val = value;
                                            } else if (pred != null) {
                                                pred.next = e.next;
                                            } else {
                                                setTabAt(tab, i, e.next);
                                            }
                                        }
                                        v = oldVal;
                                    }
                                }
                                pred = e;
                                e = e.next;
                            } while (e != null);
                            v = oldVal;
                        } else if (f instanceof TreeBin) {
                            validated = true;
                            TreeBin<K, V> t = (TreeBin) f;
                            TreeNode<K, V> r = t.root;
                            if (r != null) {
                                TreeNode<K, V> p = r.findTreeNode(hash, key, null);
                                if (p != null) {
                                    V pv = p.val;
                                    if (cv == null || cv == pv || (pv != null && cv.equals(pv))) {
                                        oldVal = pv;
                                        if (value != null) {
                                            p.val = value;
                                            v = oldVal;
                                        } else {
                                            if (t.removeTreeNode(p)) {
                                                setTabAt(tab, i, untreeify(t.first));
                                            }
                                            v = oldVal;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    v = null;
                }
                if (validated) {
                    if (v != null) {
                        if (value != null) {
                            return v;
                        }
                        addCount(-1, -1);
                        return v;
                    }
                }
            }
        }
        return null;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void clear() {
        /*
        r14 = this;
        r11 = 0;
        r2 = 0;
        r6 = 0;
        r9 = r14.table;
        r7 = r6;
    L_0x0007:
        if (r9 == 0) goto L_0x004d;
    L_0x0009:
        r10 = r9.length;
        if (r7 >= r10) goto L_0x004d;
    L_0x000c:
        r4 = tabAt(r9, r7);
        if (r4 != 0) goto L_0x0016;
    L_0x0012:
        r6 = r7 + 1;
    L_0x0014:
        r7 = r6;
        goto L_0x0007;
    L_0x0016:
        r5 = r4.hash;
        r10 = -1879048193; // 0xffffffff8fffffff float:-2.5243547E-29 double:NaN;
        if (r5 != r10) goto L_0x0023;
    L_0x001d:
        r9 = r14.helpTransfer(r9, r4);
        r6 = 0;
        goto L_0x0014;
    L_0x0023:
        monitor-enter(r4);
        r10 = tabAt(r9, r7);	 Catch:{ all -> 0x0058 }
        if (r10 != r4) goto L_0x005b;
    L_0x002a:
        if (r5 < 0) goto L_0x0035;
    L_0x002c:
        r8 = r4;
    L_0x002d:
        if (r8 == 0) goto L_0x0042;
    L_0x002f:
        r12 = 1;
        r2 = r2 - r12;
        r8 = r8.next;	 Catch:{ all -> 0x0058 }
        goto L_0x002d;
    L_0x0035:
        r10 = r4 instanceof java.util.concurrent.ConcurrentHashMap.TreeBin;	 Catch:{ all -> 0x0058 }
        if (r10 == 0) goto L_0x0040;
    L_0x0039:
        r0 = r4;
        r0 = (java.util.concurrent.ConcurrentHashMap.TreeBin) r0;	 Catch:{ all -> 0x0058 }
        r10 = r0;
        r8 = r10.first;	 Catch:{ all -> 0x0058 }
        goto L_0x002d;
    L_0x0040:
        r8 = r11;
        goto L_0x002d;
    L_0x0042:
        r6 = r7 + 1;
        r10 = 0;
        setTabAt(r9, r7, r10);	 Catch:{ all -> 0x004a }
    L_0x0048:
        monitor-exit(r4);	 Catch:{ all -> 0x004a }
        goto L_0x0014;
    L_0x004a:
        r10 = move-exception;
    L_0x004b:
        monitor-exit(r4);	 Catch:{ all -> 0x004a }
        throw r10;
    L_0x004d:
        r10 = 0;
        r10 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1));
        if (r10 == 0) goto L_0x0057;
    L_0x0053:
        r10 = -1;
        r14.addCount(r2, r10);
    L_0x0057:
        return;
    L_0x0058:
        r10 = move-exception;
        r6 = r7;
        goto L_0x004b;
    L_0x005b:
        r6 = r7;
        goto L_0x0048;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.clear():void");
    }

    public Set<K> keySet() {
        KeySetView<K, V> ks = this.keySet;
        if (ks != null) {
            return ks;
        }
        ks = new KeySetView(this, null);
        this.keySet = ks;
        return ks;
    }

    public Collection<V> values() {
        ValuesView<K, V> vs = this.values;
        if (vs != null) {
            return vs;
        }
        vs = new ValuesView(this);
        this.values = vs;
        return vs;
    }

    public Set<Entry<K, V>> entrySet() {
        EntrySetView<K, V> es = this.entrySet;
        if (es != null) {
            return es;
        }
        es = new EntrySetView(this);
        this.entrySet = es;
        return es;
    }

    public int hashCode() {
        int h = NCPU;
        Node<K, V>[] t = this.table;
        if (t != null) {
            Traverser<K, V> it = new Traverser(t, t.length, NCPU, t.length);
            while (true) {
                Node<K, V> p = it.advance();
                if (p == null) {
                    break;
                }
                h += p.key.hashCode() ^ p.val.hashCode();
            }
        }
        return h;
    }

    public String toString() {
        Node<K, V>[] t = this.table;
        int f = t == null ? NCPU : t.length;
        Traverser<K, V> it = new Traverser(t, f, NCPU, f);
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        Node<K, V> p = it.advance();
        if (p != null) {
            while (true) {
                Object k = p.key;
                Object v = p.val;
                if (k == this) {
                    k = "(this Map)";
                }
                sb.append(k);
                sb.append('=');
                if (v == this) {
                    v = "(this Map)";
                }
                sb.append(v);
                p = it.advance();
                if (p == null) {
                    break;
                }
                sb.append(',').append(' ');
            }
        }
        return sb.append('}').toString();
    }

    public boolean equals(Object o) {
        if (o != this) {
            if (!(o instanceof Map)) {
                return false;
            }
            Map<?, ?> m = (Map) o;
            Node<K, V>[] t = this.table;
            int f = t == null ? NCPU : t.length;
            Traverser<K, V> it = new Traverser(t, f, NCPU, f);
            while (true) {
                Node<K, V> p = it.advance();
                if (p == null) {
                    break;
                }
                V val = p.val;
                V v = m.get(p.key);
                if (v == null) {
                    return false;
                }
                if (v != val && !v.equals(val)) {
                    return false;
                }
            }
            for (Entry<?, ?> e : m.entrySet()) {
                Object mk = e.getKey();
                if (mk == null) {
                    return false;
                }
                Object mv = e.getValue();
                if (mv == null) {
                    return false;
                }
                Object v2 = get(mk);
                if (v2 == null) {
                    return false;
                }
                if (mv != v2 && !mv.equals(v2)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        int sshift = NCPU;
        int ssize = 1;
        while (ssize < MIN_TRANSFER_STRIDE) {
            sshift++;
            ssize <<= 1;
        }
        int segmentShift = 32 - sshift;
        int segmentMask = ssize - 1;
        Object segments = (Segment[]) new Segment[MIN_TRANSFER_STRIDE];
        for (int i = NCPU; i < segments.length; i++) {
            segments[i] = new Segment(LOAD_FACTOR);
        }
        s.putFields().put("segments", segments);
        s.putFields().put("segmentShift", segmentShift);
        s.putFields().put("segmentMask", segmentMask);
        s.writeFields();
        Node<K, V>[] t = this.table;
        if (t != null) {
            Traverser<K, V> it = new Traverser(t, t.length, NCPU, t.length);
            while (true) {
                Node<K, V> p = it.advance();
                if (p == null) {
                    break;
                }
                s.writeObject(p.key);
                s.writeObject(p.val);
            }
        }
        s.writeObject(null);
        s.writeObject(null);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void readObject(java.io.ObjectInputStream r34) throws java.io.IOException, java.lang.ClassNotFoundException {
        /*
        r33 = this;
        r5 = -1;
        r0 = r33;
        r0.sizeCtl = r5;
        r34.defaultReadObject();
        r26 = 0;
        r22 = 0;
    L_0x000c:
        r18 = r34.readObject();
        r32 = r34.readObject();
        if (r18 == 0) goto L_0x0034;
    L_0x0016:
        if (r32 == 0) goto L_0x0034;
    L_0x0018:
        r23 = new java.util.concurrent.ConcurrentHashMap$Node;
        r5 = r18.hashCode();
        r5 = spread(r5);
        r0 = r23;
        r1 = r18;
        r2 = r32;
        r3 = r22;
        r0.<init>(r5, r1, r2, r3);
        r6 = 1;
        r26 = r26 + r6;
        r22 = r23;
        goto L_0x000c;
    L_0x0034:
        r6 = 0;
        r5 = (r26 > r6 ? 1 : (r26 == r6 ? 0 : -1));
        if (r5 != 0) goto L_0x0040;
    L_0x003a:
        r5 = 0;
        r0 = r33;
        r0.sizeCtl = r5;
    L_0x003f:
        return;
    L_0x0040:
        r6 = 536870912; // 0x20000000 float:1.0842022E-19 double:2.652494739E-315;
        r5 = (r26 > r6 ? 1 : (r26 == r6 ? 0 : -1));
        if (r5 < 0) goto L_0x0084;
    L_0x0047:
        r20 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
    L_0x0049:
        r0 = r20;
        r0 = new java.util.concurrent.ConcurrentHashMap.Node[r0];
        r30 = r0;
        r30 = (java.util.concurrent.ConcurrentHashMap.Node[]) r30;
        r19 = r20 + -1;
        r10 = 0;
    L_0x0055:
        if (r22 == 0) goto L_0x0132;
    L_0x0057:
        r0 = r22;
        r0 = r0.next;
        r21 = r0;
        r0 = r22;
        r14 = r0.hash;
        r17 = r14 & r19;
        r0 = r30;
        r1 = r17;
        r13 = tabAt(r0, r1);
        if (r13 != 0) goto L_0x0094;
    L_0x006d:
        r16 = 1;
    L_0x006f:
        if (r16 == 0) goto L_0x0081;
    L_0x0071:
        r6 = 1;
        r10 = r10 + r6;
        r0 = r22;
        r0.next = r13;
        r0 = r30;
        r1 = r17;
        r2 = r22;
        setTabAt(r0, r1, r2);
    L_0x0081:
        r22 = r21;
        goto L_0x0055;
    L_0x0084:
        r0 = r26;
        r0 = (int) r0;
        r28 = r0;
        r5 = r28 >>> 1;
        r5 = r5 + r28;
        r5 = r5 + 1;
        r20 = tableSizeFor(r5);
        goto L_0x0049;
    L_0x0094:
        r0 = r22;
        r0 = r0.key;
        r18 = r0;
        r5 = r13.hash;
        if (r5 >= 0) goto L_0x00b6;
    L_0x009e:
        r29 = r13;
        r29 = (java.util.concurrent.ConcurrentHashMap.TreeBin) r29;
        r0 = r22;
        r5 = r0.val;
        r0 = r29;
        r1 = r18;
        r5 = r0.putTreeVal(r14, r1, r5);
        if (r5 != 0) goto L_0x00b3;
    L_0x00b0:
        r6 = 1;
        r10 = r10 + r6;
    L_0x00b3:
        r16 = 0;
        goto L_0x006f;
    L_0x00b6:
        r12 = 0;
        r16 = 1;
        r24 = r13;
    L_0x00bb:
        if (r24 == 0) goto L_0x00dd;
    L_0x00bd:
        r0 = r24;
        r5 = r0.hash;
        if (r5 != r14) goto L_0x0116;
    L_0x00c3:
        r0 = r24;
        r0 = r0.key;
        r25 = r0;
        r0 = r25;
        r1 = r18;
        if (r0 == r1) goto L_0x00db;
    L_0x00cf:
        if (r25 == 0) goto L_0x0116;
    L_0x00d1:
        r0 = r18;
        r1 = r25;
        r5 = r0.equals(r1);
        if (r5 == 0) goto L_0x0116;
    L_0x00db:
        r16 = 0;
    L_0x00dd:
        if (r16 == 0) goto L_0x006f;
    L_0x00df:
        r5 = 8;
        if (r12 < r5) goto L_0x006f;
    L_0x00e3:
        r16 = 0;
        r6 = 1;
        r10 = r10 + r6;
        r0 = r22;
        r0.next = r13;
        r15 = 0;
        r31 = 0;
        r24 = r22;
    L_0x00f1:
        if (r24 == 0) goto L_0x0124;
    L_0x00f3:
        r4 = new java.util.concurrent.ConcurrentHashMap$TreeNode;
        r0 = r24;
        r5 = r0.hash;
        r0 = r24;
        r6 = r0.key;
        r0 = r24;
        r7 = r0.val;
        r8 = 0;
        r9 = 0;
        r4.<init>(r5, r6, r7, r8, r9);
        r0 = r31;
        r4.prev = r0;
        if (r31 != 0) goto L_0x011f;
    L_0x010c:
        r15 = r4;
    L_0x010d:
        r31 = r4;
        r0 = r24;
        r0 = r0.next;
        r24 = r0;
        goto L_0x00f1;
    L_0x0116:
        r12 = r12 + 1;
        r0 = r24;
        r0 = r0.next;
        r24 = r0;
        goto L_0x00bb;
    L_0x011f:
        r0 = r31;
        r0.next = r4;
        goto L_0x010d;
    L_0x0124:
        r5 = new java.util.concurrent.ConcurrentHashMap$TreeBin;
        r5.<init>(r15);
        r0 = r30;
        r1 = r17;
        setTabAt(r0, r1, r5);
        goto L_0x006f;
    L_0x0132:
        r0 = r30;
        r1 = r33;
        r1.table = r0;
        r5 = r20 >>> 2;
        r5 = r20 - r5;
        r0 = r33;
        r0.sizeCtl = r5;
        r0 = r33;
        r0.baseCount = r10;
        goto L_0x003f;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.readObject(java.io.ObjectInputStream):void");
    }

    public V putIfAbsent(K key, V value) {
        return putVal(key, value, true);
    }

    public boolean remove(Object key, Object value) {
        if (key != null) {
            return (value == null || replaceNode(key, null, value) == null) ? false : true;
        } else {
            throw new NullPointerException();
        }
    }

    public boolean replace(K key, V oldValue, V newValue) {
        if (key != null && oldValue != null && newValue != null) {
            return replaceNode(key, newValue, oldValue) != null;
        } else {
            throw new NullPointerException();
        }
    }

    public V replace(K key, V value) {
        if (key != null && value != null) {
            return replaceNode(key, value, null);
        }
        throw new NullPointerException();
    }

    public boolean contains(Object value) {
        return containsValue(value);
    }

    public Enumeration<K> keys() {
        Node<K, V>[] t = this.table;
        int f = t == null ? NCPU : t.length;
        return new KeyIterator(t, f, NCPU, f, this);
    }

    public Enumeration<V> elements() {
        Node<K, V>[] t = this.table;
        int f = t == null ? NCPU : t.length;
        return new ValueIterator(t, f, NCPU, f, this);
    }

    public long mappingCount() {
        long n = sumCount();
        return n < TRANSFERORIGIN ? TRANSFERORIGIN : n;
    }

    public static <K> KeySetView<K, Boolean> newKeySet() {
        return new KeySetView(new ConcurrentHashMap(), Boolean.TRUE);
    }

    public static <K> KeySetView<K, Boolean> newKeySet(int initialCapacity) {
        return new KeySetView(new ConcurrentHashMap(initialCapacity), Boolean.TRUE);
    }

    public Set<K> keySet(V mappedValue) {
        if (mappedValue != null) {
            return new KeySetView(this, mappedValue);
        }
        throw new NullPointerException();
    }

    private final Node<K, V>[] initTable() {
        Node<K, V>[] tab;
        int sc;
        while (true) {
            tab = this.table;
            if (tab != null && tab.length != 0) {
                break;
            }
            sc = this.sizeCtl;
            if (sc < 0) {
                Thread.yield();
            } else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
                try {
                    break;
                } catch (Throwable th) {
                    this.sizeCtl = sc;
                }
            }
        }
        tab = this.table;
        if (tab == null || tab.length == 0) {
            int n = sc > 0 ? sc : MIN_TRANSFER_STRIDE;
            Node<K, V>[] nt = (Node[]) new Node[n];
            tab = nt;
            this.table = nt;
            sc = n - (n >>> 2);
        }
        this.sizeCtl = sc;
        return tab;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void addCount(long r34, int r36) {
        /*
        r33 = this;
        r0 = r33;
        r0 = r0.counterCells;
        r26 = r0;
        if (r26 != 0) goto L_0x001a;
    L_0x0008:
        r6 = U;
        r8 = BASECOUNT;
        r0 = r33;
        r10 = r0.baseCount;
        r12 = r10 + r34;
        r7 = r33;
        r6 = r6.compareAndSwapLong(r7, r8, r10, r12);
        if (r6 != 0) goto L_0x005e;
    L_0x001a:
        r31 = 1;
        r6 = threadCounterHashCode;
        r27 = r6.get();
        r27 = (java.util.concurrent.ConcurrentHashMap.CounterHashCode) r27;
        if (r27 == 0) goto L_0x0049;
    L_0x0026:
        if (r26 == 0) goto L_0x0049;
    L_0x0028:
        r0 = r26;
        r6 = r0.length;
        r28 = r6 + -1;
        if (r28 < 0) goto L_0x0049;
    L_0x002f:
        r0 = r27;
        r6 = r0.code;
        r6 = r6 & r28;
        r15 = r26[r6];
        if (r15 == 0) goto L_0x0049;
    L_0x0039:
        r14 = U;
        r16 = CELLVALUE;
        r0 = r15.value;
        r18 = r0;
        r20 = r18 + r34;
        r31 = r14.compareAndSwapLong(r15, r16, r18, r20);
        if (r31 != 0) goto L_0x0055;
    L_0x0049:
        r0 = r33;
        r1 = r34;
        r3 = r27;
        r4 = r31;
        r0.fullAddCount(r1, r3, r4);
    L_0x0054:
        return;
    L_0x0055:
        r6 = 1;
        r0 = r36;
        if (r0 <= r6) goto L_0x0054;
    L_0x005a:
        r12 = r33.sumCount();
    L_0x005e:
        if (r36 < 0) goto L_0x0054;
    L_0x0060:
        r0 = r33;
        r0 = r0.sizeCtl;
        r24 = r0;
        r0 = r24;
        r6 = (long) r0;
        r6 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1));
        if (r6 < 0) goto L_0x0054;
    L_0x006d:
        r0 = r33;
        r0 = r0.table;
        r30 = r0;
        if (r30 == 0) goto L_0x0054;
    L_0x0075:
        r0 = r30;
        r6 = r0.length;
        r7 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        if (r6 >= r7) goto L_0x0054;
    L_0x007c:
        if (r24 >= 0) goto L_0x00b1;
    L_0x007e:
        r6 = -1;
        r0 = r24;
        if (r0 == r6) goto L_0x0054;
    L_0x0083:
        r0 = r33;
        r6 = r0.transferIndex;
        r0 = r33;
        r7 = r0.transferOrigin;
        if (r6 <= r7) goto L_0x0054;
    L_0x008d:
        r0 = r33;
        r0 = r0.nextTable;
        r29 = r0;
        if (r29 == 0) goto L_0x0054;
    L_0x0095:
        r20 = U;
        r22 = SIZECTL;
        r25 = r24 + -1;
        r21 = r33;
        r6 = r20.compareAndSwapInt(r21, r22, r24, r25);
        if (r6 == 0) goto L_0x00ac;
    L_0x00a3:
        r0 = r33;
        r1 = r30;
        r2 = r29;
        r0.transfer(r1, r2);
    L_0x00ac:
        r12 = r33.sumCount();
        goto L_0x0060;
    L_0x00b1:
        r20 = U;
        r22 = SIZECTL;
        r25 = -2;
        r21 = r33;
        r6 = r20.compareAndSwapInt(r21, r22, r24, r25);
        if (r6 == 0) goto L_0x00ac;
    L_0x00bf:
        r6 = 0;
        r0 = r33;
        r1 = r30;
        r0.transfer(r1, r6);
        goto L_0x00ac;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.addCount(long, int):void");
    }

    final Node<K, V>[] helpTransfer(Node<K, V>[] tab, Node<K, V> f) {
        if (f instanceof ForwardingNode) {
            Node<K, V>[] nextTab = ((ForwardingNode) f).nextTable;
            if (nextTab != null) {
                if (nextTab != this.nextTable || tab != this.table || this.transferIndex <= this.transferOrigin) {
                    return nextTab;
                }
                int sc = this.sizeCtl;
                if (sc >= -1 || !U.compareAndSwapInt(this, SIZECTL, sc, sc - 1)) {
                    return nextTab;
                }
                transfer(tab, nextTab);
                return nextTab;
            }
        }
        return this.table;
    }

    private final void tryPresize(int size) {
        int c = size >= VMDebug.KIND_THREAD_EXT_ALLOCATED_BYTES ? MAXIMUM_CAPACITY : tableSizeFor(((size >>> 1) + size) + 1);
        while (true) {
            int sc = this.sizeCtl;
            if (sc >= 0) {
                int n;
                Node<K, V>[] tab = this.table;
                if (tab != null) {
                    n = tab.length;
                    if (n != 0) {
                        if (c > sc && n < MAXIMUM_CAPACITY) {
                            if (tab == this.table && U.compareAndSwapInt(this, SIZECTL, sc, -2)) {
                                transfer(tab, null);
                            }
                        } else {
                            return;
                        }
                    }
                }
                if (sc > c) {
                    n = sc;
                } else {
                    n = c;
                }
                if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
                    try {
                        if (this.table == tab) {
                            this.table = (Node[]) new Node[n];
                            sc = n - (n >>> 2);
                        }
                        this.sizeCtl = sc;
                    } catch (Throwable th) {
                        this.sizeCtl = sc;
                    }
                } else {
                    continue;
                }
            } else {
                return;
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void transfer(java.util.concurrent.ConcurrentHashMap.Node<K, V>[] r59, java.util.concurrent.ConcurrentHashMap.Node<K, V>[] r60) {
        /*
        r58 = this;
        r0 = r59;
        r0 = r0.length;
        r46 = r0;
        r6 = NCPU;
        r7 = 1;
        if (r6 <= r7) goto L_0x0058;
    L_0x000a:
        r6 = r46 >>> 3;
        r7 = NCPU;
        r56 = r6 / r7;
    L_0x0010:
        r6 = 16;
        r0 = r56;
        if (r0 >= r6) goto L_0x0018;
    L_0x0016:
        r56 = 16;
    L_0x0018:
        if (r60 != 0) goto L_0x0082;
    L_0x001a:
        r6 = r46 << 1;
        r0 = new java.util.concurrent.ConcurrentHashMap.Node[r6];	 Catch:{ Throwable -> 0x005b }
        r49 = r0;
        r49 = (java.util.concurrent.ConcurrentHashMap.Node[]) r49;	 Catch:{ Throwable -> 0x005b }
        r60 = r49;
        r0 = r60;
        r1 = r58;
        r1.nextTable = r0;
        r0 = r46;
        r1 = r58;
        r1.transferOrigin = r0;
        r0 = r46;
        r1 = r58;
        r1.transferIndex = r0;
        r54 = new java.util.concurrent.ConcurrentHashMap$ForwardingNode;
        r0 = r54;
        r1 = r59;
        r0.<init>(r1);
        r38 = r46;
    L_0x0041:
        if (r38 <= 0) goto L_0x0082;
    L_0x0043:
        r0 = r38;
        r1 = r56;
        if (r0 <= r1) goto L_0x0064;
    L_0x0049:
        r47 = r38 - r56;
    L_0x004b:
        r45 = r47;
    L_0x004d:
        r0 = r45;
        r1 = r38;
        if (r0 >= r1) goto L_0x0067;
    L_0x0053:
        r60[r45] = r54;
        r45 = r45 + 1;
        goto L_0x004d;
    L_0x0058:
        r56 = r46;
        goto L_0x0010;
    L_0x005b:
        r28 = move-exception;
        r6 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r0 = r58;
        r0.sizeCtl = r6;
    L_0x0063:
        return;
    L_0x0064:
        r47 = 0;
        goto L_0x004b;
    L_0x0067:
        r45 = r46 + r47;
    L_0x0069:
        r6 = r46 + r38;
        r0 = r45;
        if (r0 >= r6) goto L_0x0074;
    L_0x006f:
        r60[r45] = r54;
        r45 = r45 + 1;
        goto L_0x0069;
    L_0x0074:
        r6 = U;
        r8 = TRANSFERORIGIN;
        r38 = r47;
        r0 = r58;
        r1 = r47;
        r6.putOrderedInt(r0, r8, r1);
        goto L_0x0041;
    L_0x0082:
        r0 = r60;
        r0 = r0.length;
        r48 = r0;
        r31 = new java.util.concurrent.ConcurrentHashMap$ForwardingNode;
        r0 = r31;
        r1 = r60;
        r0.<init>(r1);
        r24 = 1;
        r37 = 0;
        r26 = 0;
    L_0x0096:
        if (r24 == 0) goto L_0x00cd;
    L_0x0098:
        r37 = r37 + -1;
        r0 = r37;
        r1 = r26;
        if (r0 < r1) goto L_0x00a3;
    L_0x00a0:
        r24 = 0;
        goto L_0x0096;
    L_0x00a3:
        r0 = r58;
        r10 = r0.transferIndex;
        r0 = r58;
        r6 = r0.transferOrigin;
        if (r10 > r6) goto L_0x00b2;
    L_0x00ad:
        r37 = -1;
        r24 = 0;
        goto L_0x0096;
    L_0x00b2:
        r6 = U;
        r8 = TRANSFERINDEX;
        r0 = r56;
        if (r10 <= r0) goto L_0x00cb;
    L_0x00ba:
        r11 = r10 - r56;
    L_0x00bc:
        r7 = r58;
        r6 = r6.compareAndSwapInt(r7, r8, r10, r11);
        if (r6 == 0) goto L_0x0096;
    L_0x00c4:
        r26 = r11;
        r37 = r10 + -1;
        r24 = 0;
        goto L_0x0096;
    L_0x00cb:
        r11 = 0;
        goto L_0x00bc;
    L_0x00cd:
        if (r37 < 0) goto L_0x00db;
    L_0x00cf:
        r0 = r37;
        r1 = r46;
        if (r0 >= r1) goto L_0x00db;
    L_0x00d5:
        r6 = r37 + r46;
        r0 = r48;
        if (r6 < r0) goto L_0x010a;
    L_0x00db:
        r12 = U;
        r14 = SIZECTL;
        r0 = r58;
        r0 = r0.sizeCtl;
        r16 = r0;
        r17 = r16 + 1;
        r13 = r58;
        r6 = r12.compareAndSwapInt(r13, r14, r16, r17);
        if (r6 == 0) goto L_0x00db;
    L_0x00ef:
        r6 = -1;
        r0 = r17;
        if (r0 != r6) goto L_0x0063;
    L_0x00f4:
        r6 = 0;
        r0 = r58;
        r0.nextTable = r6;
        r0 = r60;
        r1 = r58;
        r1.table = r0;
        r6 = r46 << 1;
        r7 = r46 >>> 1;
        r6 = r6 - r7;
        r0 = r58;
        r0.sizeCtl = r6;
        goto L_0x0063;
    L_0x010a:
        r0 = r59;
        r1 = r37;
        r29 = tabAt(r0, r1);
        if (r29 != 0) goto L_0x0135;
    L_0x0114:
        r6 = 0;
        r0 = r59;
        r1 = r37;
        r2 = r31;
        r6 = casTabAt(r0, r1, r6, r2);
        if (r6 == 0) goto L_0x0096;
    L_0x0121:
        r6 = 0;
        r0 = r60;
        r1 = r37;
        setTabAt(r0, r1, r6);
        r6 = r37 + r46;
        r7 = 0;
        r0 = r60;
        setTabAt(r0, r6, r7);
        r24 = 1;
        goto L_0x0096;
    L_0x0135:
        r0 = r29;
        r0 = r0.hash;
        r30 = r0;
        r6 = -1879048193; // 0xffffffff8fffffff float:-2.5243547E-29 double:NaN;
        r0 = r30;
        if (r0 != r6) goto L_0x0146;
    L_0x0142:
        r24 = 1;
        goto L_0x0096;
    L_0x0146:
        monitor-enter(r29);
        r0 = r59;
        r1 = r37;
        r6 = tabAt(r0, r1);	 Catch:{ all -> 0x01f7 }
        r0 = r29;
        if (r6 != r0) goto L_0x01f4;
    L_0x0153:
        if (r30 < 0) goto L_0x01fa;
    L_0x0155:
        r55 = r30 & r46;
        r39 = r29;
        r0 = r29;
        r0 = r0.next;	 Catch:{ all -> 0x01f7 }
        r50 = r0;
    L_0x015f:
        if (r50 == 0) goto L_0x0178;
    L_0x0161:
        r0 = r50;
        r6 = r0.hash;	 Catch:{ all -> 0x01f7 }
        r25 = r6 & r46;
        r0 = r25;
        r1 = r55;
        if (r0 == r1) goto L_0x0171;
    L_0x016d:
        r55 = r25;
        r39 = r50;
    L_0x0171:
        r0 = r50;
        r0 = r0.next;	 Catch:{ all -> 0x01f7 }
        r50 = r0;
        goto L_0x015f;
    L_0x0178:
        if (r55 != 0) goto L_0x01bc;
    L_0x017a:
        r41 = r39;
        r35 = 0;
    L_0x017e:
        r50 = r29;
        r36 = r35;
        r42 = r41;
    L_0x0184:
        r0 = r50;
        r1 = r39;
        if (r0 == r1) goto L_0x01d3;
    L_0x018a:
        r0 = r50;
        r0 = r0.hash;	 Catch:{ all -> 0x01f7 }
        r51 = r0;
        r0 = r50;
        r0 = r0.key;	 Catch:{ all -> 0x01f7 }
        r52 = r0;
        r0 = r50;
        r0 = r0.val;	 Catch:{ all -> 0x01f7 }
        r53 = r0;
        r6 = r51 & r46;
        if (r6 != 0) goto L_0x01c1;
    L_0x01a0:
        r41 = new java.util.concurrent.ConcurrentHashMap$Node;	 Catch:{ all -> 0x01f7 }
        r0 = r41;
        r1 = r51;
        r2 = r52;
        r3 = r53;
        r4 = r42;
        r0.<init>(r1, r2, r3, r4);	 Catch:{ all -> 0x01f7 }
        r35 = r36;
    L_0x01b1:
        r0 = r50;
        r0 = r0.next;	 Catch:{ all -> 0x01f7 }
        r50 = r0;
        r36 = r35;
        r42 = r41;
        goto L_0x0184;
    L_0x01bc:
        r35 = r39;
        r41 = 0;
        goto L_0x017e;
    L_0x01c1:
        r35 = new java.util.concurrent.ConcurrentHashMap$Node;	 Catch:{ all -> 0x01f7 }
        r0 = r35;
        r1 = r51;
        r2 = r52;
        r3 = r53;
        r4 = r36;
        r0.<init>(r1, r2, r3, r4);	 Catch:{ all -> 0x01f7 }
        r41 = r42;
        goto L_0x01b1;
    L_0x01d3:
        r35 = r36;
        r41 = r42;
    L_0x01d7:
        r0 = r60;
        r1 = r37;
        r2 = r41;
        setTabAt(r0, r1, r2);	 Catch:{ all -> 0x01f7 }
        r6 = r37 + r46;
        r0 = r60;
        r1 = r35;
        setTabAt(r0, r6, r1);	 Catch:{ all -> 0x01f7 }
        r0 = r59;
        r1 = r37;
        r2 = r31;
        setTabAt(r0, r1, r2);	 Catch:{ all -> 0x01f7 }
        r24 = 1;
    L_0x01f4:
        monitor-exit(r29);	 Catch:{ all -> 0x01f7 }
        goto L_0x0096;
    L_0x01f7:
        r6 = move-exception;
        monitor-exit(r29);	 Catch:{ all -> 0x01f7 }
        throw r6;
    L_0x01fa:
        r0 = r29;
        r6 = r0 instanceof java.util.concurrent.ConcurrentHashMap.TreeBin;	 Catch:{ all -> 0x01f7 }
        if (r6 == 0) goto L_0x029d;
    L_0x0200:
        r0 = r29;
        r0 = (java.util.concurrent.ConcurrentHashMap.TreeBin) r0;	 Catch:{ all -> 0x01f7 }
        r57 = r0;
        r43 = 0;
        r44 = 0;
        r33 = 0;
        r34 = 0;
        r40 = 0;
        r32 = 0;
        r0 = r57;
        r0 = r0.first;	 Catch:{ all -> 0x01f7 }
        r27 = r0;
    L_0x0218:
        if (r27 == 0) goto L_0x026b;
    L_0x021a:
        r0 = r27;
        r0 = r0.hash;	 Catch:{ all -> 0x01f7 }
        r19 = r0;
        r18 = new java.util.concurrent.ConcurrentHashMap$TreeNode;	 Catch:{ all -> 0x01f7 }
        r0 = r27;
        r0 = r0.key;	 Catch:{ all -> 0x01f7 }
        r20 = r0;
        r0 = r27;
        r0 = r0.val;	 Catch:{ all -> 0x01f7 }
        r21 = r0;
        r22 = 0;
        r23 = 0;
        r18.<init>(r19, r20, r21, r22, r23);	 Catch:{ all -> 0x01f7 }
        r6 = r19 & r46;
        if (r6 != 0) goto L_0x0255;
    L_0x0239:
        r0 = r44;
        r1 = r18;
        r1.prev = r0;	 Catch:{ all -> 0x01f7 }
        if (r44 != 0) goto L_0x024e;
    L_0x0241:
        r43 = r18;
    L_0x0243:
        r44 = r18;
        r40 = r40 + 1;
    L_0x0247:
        r0 = r27;
        r0 = r0.next;	 Catch:{ all -> 0x01f7 }
        r27 = r0;
        goto L_0x0218;
    L_0x024e:
        r0 = r18;
        r1 = r44;
        r1.next = r0;	 Catch:{ all -> 0x01f7 }
        goto L_0x0243;
    L_0x0255:
        r0 = r34;
        r1 = r18;
        r1.prev = r0;	 Catch:{ all -> 0x01f7 }
        if (r34 != 0) goto L_0x0264;
    L_0x025d:
        r33 = r18;
    L_0x025f:
        r34 = r18;
        r32 = r32 + 1;
        goto L_0x0247;
    L_0x0264:
        r0 = r18;
        r1 = r34;
        r1.next = r0;	 Catch:{ all -> 0x01f7 }
        goto L_0x025f;
    L_0x026b:
        r6 = 6;
        r0 = r40;
        if (r0 > r6) goto L_0x027f;
    L_0x0270:
        r41 = untreeify(r43);	 Catch:{ all -> 0x01f7 }
    L_0x0274:
        r6 = 6;
        r0 = r32;
        if (r0 > r6) goto L_0x028e;
    L_0x0279:
        r35 = untreeify(r33);	 Catch:{ all -> 0x01f7 }
    L_0x027d:
        goto L_0x01d7;
    L_0x027f:
        if (r32 == 0) goto L_0x028b;
    L_0x0281:
        r41 = new java.util.concurrent.ConcurrentHashMap$TreeBin;	 Catch:{ all -> 0x01f7 }
        r0 = r41;
        r1 = r43;
        r0.<init>(r1);	 Catch:{ all -> 0x01f7 }
        goto L_0x0274;
    L_0x028b:
        r41 = r57;
        goto L_0x0274;
    L_0x028e:
        if (r40 == 0) goto L_0x029a;
    L_0x0290:
        r35 = new java.util.concurrent.ConcurrentHashMap$TreeBin;	 Catch:{ all -> 0x01f7 }
        r0 = r35;
        r1 = r33;
        r0.<init>(r1);	 Catch:{ all -> 0x01f7 }
        goto L_0x027d;
    L_0x029a:
        r35 = r57;
        goto L_0x027d;
    L_0x029d:
        r35 = 0;
        r41 = r35;
        goto L_0x01d7;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.concurrent.ConcurrentHashMap.transfer(java.util.concurrent.ConcurrentHashMap$Node[], java.util.concurrent.ConcurrentHashMap$Node[]):void");
    }

    private final void treeifyBin(Node<K, V>[] tab, int index) {
        if (tab != null) {
            int n = tab.length;
            if (r0 >= MIN_TREEIFY_CAPACITY) {
                Node<K, V> b = tabAt(tab, index);
                if (b != null) {
                    synchronized (b) {
                        if (tabAt(tab, index) == b) {
                            TreeNode<K, V> hd = null;
                            TreeNode<K, V> tl = null;
                            for (Node<K, V> e = b; e != null; e = e.next) {
                                TreeNode<K, V> p = new TreeNode(e.hash, e.key, e.val, null, null);
                                p.prev = tl;
                                if (tl == null) {
                                    hd = p;
                                } else {
                                    tl.next = p;
                                }
                                tl = p;
                            }
                            setTabAt(tab, index, new TreeBin(hd));
                        }
                    }
                }
            } else if (tab == this.table) {
                int sc = this.sizeCtl;
                if (sc >= 0 && U.compareAndSwapInt(this, SIZECTL, sc, -2)) {
                    transfer(tab, null);
                }
            }
        }
    }

    static <K, V> Node<K, V> untreeify(Node<K, V> b) {
        Node<K, V> hd = null;
        Node<K, V> tl = null;
        for (Node<K, V> q = b; q != null; q = q.next) {
            Node<K, V> p = new Node(q.hash, q.key, q.val, null);
            if (tl == null) {
                hd = p;
            } else {
                tl.next = p;
            }
            tl = p;
        }
        return hd;
    }

    final long sumCount() {
        CounterCell[] as = this.counterCells;
        long sum = this.baseCount;
        if (as != null) {
            for (int i = NCPU; i < as.length; i++) {
                CounterCell a = as[i];
                if (a != null) {
                    sum += a.value;
                }
            }
        }
        return sum;
    }

    private final void fullAddCount(long x, CounterHashCode hc, boolean wasUncontended) {
        int h;
        if (hc == null) {
            hc = new CounterHashCode();
            int s = counterHashCodeGenerator.addAndGet(SEED_INCREMENT);
            if (s == 0) {
                h = 1;
            } else {
                h = s;
            }
            hc.code = h;
            threadCounterHashCode.set(hc);
        } else {
            h = hc.code;
        }
        boolean collide = false;
        while (true) {
            long v;
            CounterCell[] rs;
            CounterCell[] as = this.counterCells;
            if (as != null) {
                int n = as.length;
                if (n > 0) {
                    CounterCell a = as[(n - 1) & h];
                    if (a != null) {
                        if (wasUncontended) {
                            Unsafe unsafe = U;
                            long j = CELLVALUE;
                            v = a.value;
                            if (unsafe.compareAndSwapLong(a, j, v, v + x)) {
                                break;
                            } else if (this.counterCells != as || n >= NCPU) {
                                collide = false;
                            } else if (!collide) {
                                collide = true;
                            } else if (this.cellsBusy == 0 && U.compareAndSwapInt(this, CELLSBUSY, NCPU, 1)) {
                                try {
                                    if (this.counterCells == as) {
                                        rs = new CounterCell[(n << 1)];
                                        for (int i = NCPU; i < n; i++) {
                                            rs[i] = as[i];
                                        }
                                        this.counterCells = rs;
                                    }
                                    this.cellsBusy = NCPU;
                                    collide = false;
                                } catch (Throwable th) {
                                    this.cellsBusy = NCPU;
                                }
                            }
                        } else {
                            wasUncontended = true;
                        }
                    } else {
                        if (this.cellsBusy == 0) {
                            CounterCell counterCell = new CounterCell(x);
                            if (this.cellsBusy == 0 && U.compareAndSwapInt(this, CELLSBUSY, NCPU, 1)) {
                                boolean created = false;
                                try {
                                    rs = this.counterCells;
                                    if (rs != null) {
                                        int m = rs.length;
                                        if (m > 0) {
                                            int j2 = (m - 1) & h;
                                            if (rs[j2] == null) {
                                                rs[j2] = counterCell;
                                                created = true;
                                            }
                                        }
                                    }
                                    this.cellsBusy = NCPU;
                                    if (created) {
                                        break;
                                    }
                                } catch (Throwable th2) {
                                    this.cellsBusy = NCPU;
                                }
                            }
                        }
                        collide = false;
                    }
                    h ^= h << 13;
                    h ^= h >>> 17;
                    h ^= h << 5;
                }
            }
            if (this.cellsBusy == 0 && this.counterCells == as && U.compareAndSwapInt(this, CELLSBUSY, NCPU, 1)) {
                boolean init = false;
                try {
                    if (this.counterCells == as) {
                        rs = new CounterCell[2];
                        rs[h & 1] = new CounterCell(x);
                        this.counterCells = rs;
                        init = true;
                    }
                    this.cellsBusy = NCPU;
                    if (init) {
                        break;
                    }
                } catch (Throwable th3) {
                    this.cellsBusy = NCPU;
                }
            } else {
                Unsafe unsafe2 = U;
                long j3 = BASECOUNT;
                v = this.baseCount;
                if (unsafe2.compareAndSwapLong(this, j3, v, v + x)) {
                    break;
                }
            }
        }
        hc.code = h;
    }
}
