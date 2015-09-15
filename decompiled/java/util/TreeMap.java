package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import libcore.util.Objects;
import org.w3c.dom.traversal.NodeFilter;
import org.xmlpull.v1.XmlPullParser;

public class TreeMap<K, V> extends AbstractMap<K, V> implements SortedMap<K, V>, NavigableMap<K, V>, Cloneable, Serializable {
    private static final Comparator<Comparable> NATURAL_ORDER = null;
    private static final long serialVersionUID = 919286545866124006L;
    Comparator<? super K> comparator;
    private EntrySet entrySet;
    private KeySet keySet;
    int modCount;
    Node<K, V> root;
    int size;

    /* renamed from: java.util.TreeMap.2 */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$java$util$TreeMap$Bound;
        static final /* synthetic */ int[] $SwitchMap$java$util$TreeMap$Relation;

        static {
            $SwitchMap$java$util$TreeMap$Bound = new int[Bound.values().length];
            try {
                $SwitchMap$java$util$TreeMap$Bound[Bound.NO_BOUND.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$util$TreeMap$Bound[Bound.INCLUSIVE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$util$TreeMap$Bound[Bound.EXCLUSIVE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            $SwitchMap$java$util$TreeMap$Relation = new int[Relation.values().length];
            try {
                $SwitchMap$java$util$TreeMap$Relation[Relation.LOWER.ordinal()] = 1;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$java$util$TreeMap$Relation[Relation.FLOOR.ordinal()] = 2;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$java$util$TreeMap$Relation[Relation.EQUAL.ordinal()] = 3;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$java$util$TreeMap$Relation[Relation.CEILING.ordinal()] = 4;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$java$util$TreeMap$Relation[Relation.HIGHER.ordinal()] = 5;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$java$util$TreeMap$Relation[Relation.CREATE.ordinal()] = 6;
            } catch (NoSuchFieldError e9) {
            }
        }
    }

    static abstract class NavigableSubMap<K, V> extends AbstractMap<K, V> implements Serializable {
        private static final long serialVersionUID = -2102997345730753016L;
        boolean fromStart;
        Object hi;
        boolean hiInclusive;
        Object lo;
        boolean loInclusive;
        TreeMap<K, V> m;
        boolean toEnd;

        NavigableSubMap(java.util.TreeMap<K, V> r1, K r2, java.util.TreeMap.Bound r3, K r4, java.util.TreeMap.Bound r5) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.TreeMap.NavigableSubMap.<init>(java.util.TreeMap, java.lang.Object, java.util.TreeMap$Bound, java.lang.Object, java.util.TreeMap$Bound):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.TreeMap.NavigableSubMap.<init>(java.util.TreeMap, java.lang.Object, java.util.TreeMap$Bound, java.lang.Object, java.util.TreeMap$Bound):void
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.TreeMap.NavigableSubMap.<init>(java.util.TreeMap, java.lang.Object, java.util.TreeMap$Bound, java.lang.Object, java.util.TreeMap$Bound):void");
        }

        protected java.lang.Object readResolve() throws java.io.ObjectStreamException {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.TreeMap.NavigableSubMap.readResolve():java.lang.Object
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.TreeMap.NavigableSubMap.readResolve():java.lang.Object
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.TreeMap.NavigableSubMap.readResolve():java.lang.Object");
        }

        public Set<Entry<K, V>> entrySet() {
            throw new UnsupportedOperationException();
        }
    }

    static class AscendingSubMap<K, V> extends NavigableSubMap<K, V> {
        private static final long serialVersionUID = 912986545866124060L;

        AscendingSubMap(TreeMap<K, V> delegate, K from, Bound fromBound, K to, Bound toBound) {
            super(delegate, from, fromBound, to, toBound);
        }
    }

    enum Bound {
        INCLUSIVE {
            public String leftCap(Object from) {
                return "[" + from;
            }

            public String rightCap(Object to) {
                return to + "]";
            }
        },
        EXCLUSIVE {
            public String leftCap(Object from) {
                return "(" + from;
            }

            public String rightCap(Object to) {
                return to + ")";
            }
        },
        NO_BOUND {
            public String leftCap(Object from) {
                return ".";
            }

            public String rightCap(Object to) {
                return ".";
            }
        };

        public abstract String leftCap(Object obj);

        public abstract String rightCap(Object obj);
    }

    abstract class MapIterator<T> implements Iterator<T> {
        protected int expectedModCount;
        protected Node<K, V> last;
        protected Node<K, V> next;

        MapIterator(Node<K, V> next) {
            this.expectedModCount = TreeMap.this.modCount;
            this.next = next;
        }

        public boolean hasNext() {
            return this.next != null;
        }

        protected Node<K, V> stepForward() {
            if (this.next == null) {
                throw new NoSuchElementException();
            } else if (TreeMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            } else {
                this.last = this.next;
                this.next = this.next.next();
                return this.last;
            }
        }

        protected Node<K, V> stepBackward() {
            if (this.next == null) {
                throw new NoSuchElementException();
            } else if (TreeMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            } else {
                this.last = this.next;
                this.next = this.next.prev();
                return this.last;
            }
        }

        public void remove() {
            if (this.last == null) {
                throw new IllegalStateException();
            }
            TreeMap.this.removeInternal(this.last);
            this.expectedModCount = TreeMap.this.modCount;
            this.last = null;
        }
    }

    final class BoundedMap extends AbstractMap<K, V> implements NavigableMap<K, V>, Serializable {
        private final transient boolean ascending;
        private transient java.util.TreeMap$BoundedMap.BoundedEntrySet entrySet;
        private final transient K from;
        private final transient Bound fromBound;
        private transient java.util.TreeMap$BoundedMap.BoundedKeySet keySet;
        final /* synthetic */ TreeMap this$0;
        private final transient K to;
        private final transient Bound toBound;

        abstract class BoundedIterator<T> extends MapIterator<T> {
            protected BoundedIterator(Node<K, V> next) {
                super(next);
            }

            protected Node<K, V> stepForward() {
                Node<K, V> result = super.stepForward();
                if (!(this.next == null || BoundedMap.this.isInBounds(this.next.key, Bound.NO_BOUND, BoundedMap.this.toBound))) {
                    this.next = null;
                }
                return result;
            }

            protected Node<K, V> stepBackward() {
                Node<K, V> result = super.stepBackward();
                if (!(this.next == null || BoundedMap.this.isInBounds(this.next.key, BoundedMap.this.fromBound, Bound.NO_BOUND))) {
                    this.next = null;
                }
                return result;
            }
        }

        final class BoundedEntrySet extends AbstractSet<Entry<K, V>> {

            /* renamed from: java.util.TreeMap.BoundedMap.BoundedEntrySet.1 */
            class AnonymousClass1 extends java.util.TreeMap$BoundedMap.BoundedIterator<Entry<K, V>> {
                AnonymousClass1(Node x0) {
                    super(x0);
                }

                public Entry<K, V> next() {
                    return BoundedMap.this.ascending ? stepForward() : stepBackward();
                }
            }

            BoundedEntrySet() {
            }

            public int size() {
                return BoundedMap.this.size();
            }

            public boolean isEmpty() {
                return BoundedMap.this.isEmpty();
            }

            public Iterator<Entry<K, V>> iterator() {
                return new AnonymousClass1(BoundedMap.this.endpoint(true));
            }

            public boolean contains(Object o) {
                if (!(o instanceof Entry)) {
                    return false;
                }
                Entry<?, ?> entry = (Entry) o;
                if (!BoundedMap.this.isInBounds(entry.getKey()) || BoundedMap.this.this$0.findByEntry(entry) == null) {
                    return false;
                }
                return true;
            }

            public boolean remove(Object o) {
                if (!(o instanceof Entry)) {
                    return false;
                }
                Entry<?, ?> entry = (Entry) o;
                if (BoundedMap.this.isInBounds(entry.getKey()) && BoundedMap.this.this$0.entrySet().remove(entry)) {
                    return true;
                }
                return false;
            }
        }

        final class BoundedKeySet extends AbstractSet<K> implements NavigableSet<K> {
            final /* synthetic */ BoundedMap this$1;

            /* renamed from: java.util.TreeMap.BoundedMap.BoundedKeySet.1 */
            class AnonymousClass1 extends java.util.TreeMap$BoundedMap.BoundedIterator<K> {
                AnonymousClass1(Node x0) {
                    super(x0);
                }

                public K next() {
                    return (BoundedKeySet.this.this$1.ascending ? stepForward() : stepBackward()).key;
                }
            }

            /* renamed from: java.util.TreeMap.BoundedMap.BoundedKeySet.2 */
            class AnonymousClass2 extends java.util.TreeMap$BoundedMap.BoundedIterator<K> {
                final /* synthetic */ BoundedKeySet this$2;

                AnonymousClass2(java.util.TreeMap.BoundedMap.BoundedKeySet r1, java.util.TreeMap.Node r2) {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.TreeMap.BoundedMap.BoundedKeySet.2.<init>(java.util.TreeMap$BoundedMap$BoundedKeySet, java.util.TreeMap$Node):void
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
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.TreeMap.BoundedMap.BoundedKeySet.2.<init>(java.util.TreeMap$BoundedMap$BoundedKeySet, java.util.TreeMap$Node):void
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
                    throw new UnsupportedOperationException("Method not decompiled: java.util.TreeMap.BoundedMap.BoundedKeySet.2.<init>(java.util.TreeMap$BoundedMap$BoundedKeySet, java.util.TreeMap$Node):void");
                }

                public K next() {
                    /* JADX: method processing error */
/*
                    Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.TreeMap.BoundedMap.BoundedKeySet.2.next():K
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
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.TreeMap.BoundedMap.BoundedKeySet.2.next():K
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
                    throw new UnsupportedOperationException("Method not decompiled: java.util.TreeMap.BoundedMap.BoundedKeySet.2.next():K");
                }
            }

            BoundedKeySet(BoundedMap boundedMap) {
                this.this$1 = boundedMap;
            }

            public int size() {
                return this.this$1.size();
            }

            public boolean isEmpty() {
                return this.this$1.isEmpty();
            }

            public Iterator<K> iterator() {
                return new AnonymousClass1(this.this$1.endpoint(true));
            }

            public Iterator<K> descendingIterator() {
                return new AnonymousClass2(this, this.this$1.endpoint(false));
            }

            public boolean contains(Object key) {
                return this.this$1.isInBounds(key) && this.this$1.this$0.findByObject(key) != null;
            }

            public boolean remove(Object key) {
                return this.this$1.isInBounds(key) && this.this$1.this$0.removeInternalByKey(key) != null;
            }

            public K first() {
                return this.this$1.firstKey();
            }

            public K pollFirst() {
                Entry<K, ?> entry = this.this$1.pollFirstEntry();
                return entry != null ? entry.getKey() : null;
            }

            public K last() {
                return this.this$1.lastKey();
            }

            public K pollLast() {
                Entry<K, ?> entry = this.this$1.pollLastEntry();
                return entry != null ? entry.getKey() : null;
            }

            public K lower(K key) {
                return this.this$1.lowerKey(key);
            }

            public K floor(K key) {
                return this.this$1.floorKey(key);
            }

            public K ceiling(K key) {
                return this.this$1.ceilingKey(key);
            }

            public K higher(K key) {
                return this.this$1.higherKey(key);
            }

            public Comparator<? super K> comparator() {
                return this.this$1.comparator();
            }

            public NavigableSet<K> subSet(K from, boolean fromInclusive, K to, boolean toInclusive) {
                return this.this$1.subMap((Object) from, fromInclusive, (Object) to, toInclusive).navigableKeySet();
            }

            public SortedSet<K> subSet(K fromInclusive, K toExclusive) {
                return this.this$1.subMap((Object) fromInclusive, (Object) toExclusive).navigableKeySet();
            }

            public NavigableSet<K> headSet(K to, boolean inclusive) {
                return this.this$1.headMap(to, inclusive).navigableKeySet();
            }

            public SortedSet<K> headSet(K toExclusive) {
                return this.this$1.headMap((Object) toExclusive).navigableKeySet();
            }

            public NavigableSet<K> tailSet(K from, boolean inclusive) {
                return this.this$1.tailMap(from, inclusive).navigableKeySet();
            }

            public SortedSet<K> tailSet(K fromInclusive) {
                return this.this$1.tailMap((Object) fromInclusive).navigableKeySet();
            }

            public NavigableSet<K> descendingSet() {
                return new BoundedMap(this.this$1.this$0, !this.this$1.ascending, this.this$1.from, this.this$1.fromBound, this.this$1.to, this.this$1.toBound).navigableKeySet();
            }
        }

        public /* bridge */ /* synthetic */ SortedMap m10headMap(Object x0) {
            return headMap(x0);
        }

        public /* bridge */ /* synthetic */ SortedMap m11subMap(Object x0, Object x1) {
            return subMap(x0, x1);
        }

        public /* bridge */ /* synthetic */ SortedMap m12tailMap(Object x0) {
            return tailMap(x0);
        }

        BoundedMap(TreeMap treeMap, boolean ascending, K from, Bound fromBound, K to, Bound toBound) {
            this.this$0 = treeMap;
            if (fromBound == Bound.NO_BOUND || toBound == Bound.NO_BOUND) {
                if (fromBound != Bound.NO_BOUND) {
                    treeMap.comparator.compare(from, from);
                } else if (toBound != Bound.NO_BOUND) {
                    treeMap.comparator.compare(to, to);
                }
            } else if (treeMap.comparator.compare(from, to) > 0) {
                throw new IllegalArgumentException(from + " > " + to);
            }
            this.ascending = ascending;
            this.from = from;
            this.fromBound = fromBound;
            this.to = to;
            this.toBound = toBound;
        }

        public int size() {
            return TreeMap.count(entrySet().iterator());
        }

        public boolean isEmpty() {
            return endpoint(true) == null;
        }

        public V get(Object key) {
            return isInBounds(key) ? this.this$0.get(key) : null;
        }

        public boolean containsKey(Object key) {
            return isInBounds(key) && this.this$0.containsKey(key);
        }

        public V put(K key, V value) {
            if (isInBounds(key)) {
                return this.this$0.putInternal(key, value);
            }
            throw outOfBounds(key, this.fromBound, this.toBound);
        }

        public V remove(Object key) {
            return isInBounds(key) ? this.this$0.remove(key) : null;
        }

        private boolean isInBounds(Object key) {
            return isInBounds(key, this.fromBound, this.toBound);
        }

        private boolean isInBounds(K key, Bound fromBound, Bound toBound) {
            if (fromBound == Bound.INCLUSIVE) {
                if (this.this$0.comparator.compare(key, this.from) < 0) {
                    return false;
                }
            } else if (fromBound == Bound.EXCLUSIVE && this.this$0.comparator.compare(key, this.from) <= 0) {
                return false;
            }
            if (toBound == Bound.INCLUSIVE) {
                if (this.this$0.comparator.compare(key, this.to) > 0) {
                    return false;
                }
            } else if (toBound == Bound.EXCLUSIVE && this.this$0.comparator.compare(key, this.to) >= 0) {
                return false;
            }
            return true;
        }

        private Node<K, V> bound(Node<K, V> node, Bound fromBound, Bound toBound) {
            return (node == null || !isInBounds(node.getKey(), fromBound, toBound)) ? null : node;
        }

        public Entry<K, V> firstEntry() {
            return this.this$0.immutableCopy(endpoint(true));
        }

        public Entry<K, V> pollFirstEntry() {
            Node<K, V> result = endpoint(true);
            if (result != null) {
                this.this$0.removeInternal(result);
            }
            return this.this$0.immutableCopy(result);
        }

        public K firstKey() {
            Entry<K, V> entry = endpoint(true);
            if (entry != null) {
                return entry.getKey();
            }
            throw new NoSuchElementException();
        }

        public Entry<K, V> lastEntry() {
            return this.this$0.immutableCopy(endpoint(false));
        }

        public Entry<K, V> pollLastEntry() {
            Node<K, V> result = endpoint(false);
            if (result != null) {
                this.this$0.removeInternal(result);
            }
            return this.this$0.immutableCopy(result);
        }

        public K lastKey() {
            Entry<K, V> entry = endpoint(false);
            if (entry != null) {
                return entry.getKey();
            }
            throw new NoSuchElementException();
        }

        private Node<K, V> endpoint(boolean first) {
            Node<K, V> node = null;
            if (this.ascending == first) {
                switch (AnonymousClass2.$SwitchMap$java$util$TreeMap$Bound[this.fromBound.ordinal()]) {
                    case NodeFilter.SHOW_ELEMENT /*1*/:
                        if (this.this$0.root != null) {
                            node = this.this$0.root.first();
                            break;
                        }
                        break;
                    case NodeFilter.SHOW_ATTRIBUTE /*2*/:
                        node = this.this$0.find(this.from, Relation.CEILING);
                        break;
                    case XmlPullParser.END_TAG /*3*/:
                        node = this.this$0.find(this.from, Relation.HIGHER);
                        break;
                    default:
                        throw new AssertionError();
                }
                return bound(node, Bound.NO_BOUND, this.toBound);
            }
            switch (AnonymousClass2.$SwitchMap$java$util$TreeMap$Bound[this.toBound.ordinal()]) {
                case NodeFilter.SHOW_ELEMENT /*1*/:
                    if (this.this$0.root != null) {
                        node = this.this$0.root.last();
                        break;
                    }
                    break;
                case NodeFilter.SHOW_ATTRIBUTE /*2*/:
                    node = this.this$0.find(this.to, Relation.FLOOR);
                    break;
                case XmlPullParser.END_TAG /*3*/:
                    node = this.this$0.find(this.to, Relation.LOWER);
                    break;
                default:
                    throw new AssertionError();
            }
            return bound(node, this.fromBound, Bound.NO_BOUND);
        }

        private Entry<K, V> findBounded(K key, Relation relation) {
            int comparison;
            relation = relation.forOrder(this.ascending);
            Bound fromBoundForCheck = this.fromBound;
            Bound toBoundForCheck = this.toBound;
            if (this.toBound != Bound.NO_BOUND && (relation == Relation.LOWER || relation == Relation.FLOOR)) {
                comparison = this.this$0.comparator.compare(this.to, key);
                if (comparison <= 0) {
                    key = this.to;
                    if (this.toBound == Bound.EXCLUSIVE) {
                        relation = Relation.LOWER;
                    } else if (comparison < 0) {
                        relation = Relation.FLOOR;
                    }
                }
                toBoundForCheck = Bound.NO_BOUND;
            }
            if (this.fromBound != Bound.NO_BOUND && (relation == Relation.CEILING || relation == Relation.HIGHER)) {
                comparison = this.this$0.comparator.compare(this.from, key);
                if (comparison >= 0) {
                    key = this.from;
                    if (this.fromBound == Bound.EXCLUSIVE) {
                        relation = Relation.HIGHER;
                    } else if (comparison > 0) {
                        relation = Relation.CEILING;
                    }
                }
                fromBoundForCheck = Bound.NO_BOUND;
            }
            return bound(this.this$0.find(key, relation), fromBoundForCheck, toBoundForCheck);
        }

        public Entry<K, V> lowerEntry(K key) {
            return this.this$0.immutableCopy(findBounded(key, Relation.LOWER));
        }

        public K lowerKey(K key) {
            Entry<K, V> entry = findBounded(key, Relation.LOWER);
            return entry != null ? entry.getKey() : null;
        }

        public Entry<K, V> floorEntry(K key) {
            return this.this$0.immutableCopy(findBounded(key, Relation.FLOOR));
        }

        public K floorKey(K key) {
            Entry<K, V> entry = findBounded(key, Relation.FLOOR);
            return entry != null ? entry.getKey() : null;
        }

        public Entry<K, V> ceilingEntry(K key) {
            return this.this$0.immutableCopy(findBounded(key, Relation.CEILING));
        }

        public K ceilingKey(K key) {
            Entry<K, V> entry = findBounded(key, Relation.CEILING);
            return entry != null ? entry.getKey() : null;
        }

        public Entry<K, V> higherEntry(K key) {
            return this.this$0.immutableCopy(findBounded(key, Relation.HIGHER));
        }

        public K higherKey(K key) {
            Entry<K, V> entry = findBounded(key, Relation.HIGHER);
            return entry != null ? entry.getKey() : null;
        }

        public Comparator<? super K> comparator() {
            Comparator<? super K> forward = this.this$0.comparator();
            return this.ascending ? forward : Collections.reverseOrder(forward);
        }

        public Set<Entry<K, V>> entrySet() {
            java.util.TreeMap$BoundedMap.BoundedEntrySet result = this.entrySet;
            if (result != null) {
                return result;
            }
            result = new BoundedEntrySet();
            this.entrySet = result;
            return result;
        }

        public Set<K> keySet() {
            return navigableKeySet();
        }

        public NavigableSet<K> navigableKeySet() {
            java.util.TreeMap$BoundedMap.BoundedKeySet result = this.keySet;
            if (result != null) {
                return result;
            }
            result = new BoundedKeySet(this);
            this.keySet = result;
            return result;
        }

        public NavigableMap<K, V> descendingMap() {
            return new BoundedMap(this.this$0, !this.ascending, this.from, this.fromBound, this.to, this.toBound);
        }

        public NavigableSet<K> descendingKeySet() {
            return new BoundedMap(this.this$0, !this.ascending, this.from, this.fromBound, this.to, this.toBound).navigableKeySet();
        }

        public NavigableMap<K, V> subMap(K from, boolean fromInclusive, K to, boolean toInclusive) {
            return subMap((Object) from, fromInclusive ? Bound.INCLUSIVE : Bound.EXCLUSIVE, (Object) to, toInclusive ? Bound.INCLUSIVE : Bound.EXCLUSIVE);
        }

        public NavigableMap<K, V> subMap(K fromInclusive, K toExclusive) {
            return subMap((Object) fromInclusive, Bound.INCLUSIVE, (Object) toExclusive, Bound.EXCLUSIVE);
        }

        public NavigableMap<K, V> headMap(K to, boolean inclusive) {
            return subMap(null, Bound.NO_BOUND, (Object) to, inclusive ? Bound.INCLUSIVE : Bound.EXCLUSIVE);
        }

        public NavigableMap<K, V> headMap(K toExclusive) {
            return subMap(null, Bound.NO_BOUND, (Object) toExclusive, Bound.EXCLUSIVE);
        }

        public NavigableMap<K, V> tailMap(K from, boolean inclusive) {
            return subMap((Object) from, inclusive ? Bound.INCLUSIVE : Bound.EXCLUSIVE, null, Bound.NO_BOUND);
        }

        public NavigableMap<K, V> tailMap(K fromInclusive) {
            return subMap((Object) fromInclusive, Bound.INCLUSIVE, null, Bound.NO_BOUND);
        }

        private NavigableMap<K, V> subMap(K from, Bound fromBound, K to, Bound toBound) {
            if (!this.ascending) {
                K fromTmp = from;
                Bound fromBoundTmp = fromBound;
                from = to;
                fromBound = toBound;
                to = fromTmp;
                toBound = fromBoundTmp;
            }
            if (fromBound == Bound.NO_BOUND) {
                from = this.from;
                fromBound = this.fromBound;
            } else {
                Bound fromBoundToCheck = fromBound == this.fromBound ? Bound.INCLUSIVE : this.fromBound;
                if (!isInBounds(from, fromBoundToCheck, this.toBound)) {
                    throw outOfBounds(to, fromBoundToCheck, this.toBound);
                }
            }
            if (toBound == Bound.NO_BOUND) {
                to = this.to;
                toBound = this.toBound;
            } else {
                Bound toBoundToCheck = toBound == this.toBound ? Bound.INCLUSIVE : this.toBound;
                if (!isInBounds(to, this.fromBound, toBoundToCheck)) {
                    throw outOfBounds(to, this.fromBound, toBoundToCheck);
                }
            }
            return new BoundedMap(this.this$0, this.ascending, from, fromBound, to, toBound);
        }

        private IllegalArgumentException outOfBounds(Object value, Bound fromBound, Bound toBound) {
            return new IllegalArgumentException(value + " not in range " + fromBound.leftCap(this.from) + ".." + toBound.rightCap(this.to));
        }

        Object writeReplace() throws ObjectStreamException {
            return this.ascending ? new AscendingSubMap(this.this$0, this.from, this.fromBound, this.to, this.toBound) : new DescendingSubMap(this.this$0, this.from, this.fromBound, this.to, this.toBound);
        }
    }

    static class DescendingSubMap<K, V> extends NavigableSubMap<K, V> {
        private static final long serialVersionUID = 912986545866120460L;
        Comparator<K> reverseComparator;

        DescendingSubMap(TreeMap<K, V> delegate, K from, Bound fromBound, K to, Bound toBound) {
            super(delegate, from, fromBound, to, toBound);
        }
    }

    class EntrySet extends AbstractSet<Entry<K, V>> {
        final /* synthetic */ TreeMap this$0;

        /* renamed from: java.util.TreeMap.EntrySet.1 */
        class AnonymousClass1 extends MapIterator<Entry<K, V>> {
            final /* synthetic */ EntrySet this$1;

            AnonymousClass1(EntrySet entrySet, Node x0) {
                this.this$1 = entrySet;
                super(x0);
            }

            public /* bridge */ /* synthetic */ Object next() {
                return next();
            }

            public Entry<K, V> m13next() {
                return stepForward();
            }
        }

        EntrySet(TreeMap treeMap) {
            this.this$0 = treeMap;
        }

        public int size() {
            return this.this$0.size;
        }

        public Iterator<Entry<K, V>> iterator() {
            return new AnonymousClass1(this, this.this$0.root == null ? null : this.this$0.root.first());
        }

        public boolean contains(Object o) {
            return (o instanceof Entry) && this.this$0.findByEntry((Entry) o) != null;
        }

        public boolean remove(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Node<K, V> node = this.this$0.findByEntry((Entry) o);
            if (node == null) {
                return false;
            }
            this.this$0.removeInternal(node);
            return true;
        }

        public void clear() {
            this.this$0.clear();
        }
    }

    class KeySet extends AbstractSet<K> implements NavigableSet<K> {
        final /* synthetic */ TreeMap this$0;

        /* renamed from: java.util.TreeMap.KeySet.1 */
        class AnonymousClass1 extends MapIterator<K> {
            final /* synthetic */ KeySet this$1;

            AnonymousClass1(KeySet keySet, Node x0) {
                this.this$1 = keySet;
                super(x0);
            }

            public K next() {
                return stepForward().key;
            }
        }

        /* renamed from: java.util.TreeMap.KeySet.2 */
        class AnonymousClass2 extends MapIterator<K> {
            final /* synthetic */ KeySet this$1;

            AnonymousClass2(java.util.TreeMap.KeySet r1, java.util.TreeMap.Node r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.TreeMap.KeySet.2.<init>(java.util.TreeMap$KeySet, java.util.TreeMap$Node):void
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
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.TreeMap.KeySet.2.<init>(java.util.TreeMap$KeySet, java.util.TreeMap$Node):void
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
                throw new UnsupportedOperationException("Method not decompiled: java.util.TreeMap.KeySet.2.<init>(java.util.TreeMap$KeySet, java.util.TreeMap$Node):void");
            }

            public K next() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.TreeMap.KeySet.2.next():K
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
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.TreeMap.KeySet.2.next():K
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
                throw new UnsupportedOperationException("Method not decompiled: java.util.TreeMap.KeySet.2.next():K");
            }
        }

        KeySet(TreeMap treeMap) {
            this.this$0 = treeMap;
        }

        public int size() {
            return this.this$0.size;
        }

        public Iterator<K> iterator() {
            return new AnonymousClass1(this, this.this$0.root == null ? null : this.this$0.root.first());
        }

        public Iterator<K> descendingIterator() {
            return new AnonymousClass2(this, this.this$0.root == null ? null : this.this$0.root.last());
        }

        public boolean contains(Object o) {
            return this.this$0.containsKey(o);
        }

        public boolean remove(Object key) {
            return this.this$0.removeInternalByKey(key) != null;
        }

        public void clear() {
            this.this$0.clear();
        }

        public Comparator<? super K> comparator() {
            return this.this$0.comparator();
        }

        public K first() {
            return this.this$0.firstKey();
        }

        public K last() {
            return this.this$0.lastKey();
        }

        public K lower(K key) {
            return this.this$0.lowerKey(key);
        }

        public K floor(K key) {
            return this.this$0.floorKey(key);
        }

        public K ceiling(K key) {
            return this.this$0.ceilingKey(key);
        }

        public K higher(K key) {
            return this.this$0.higherKey(key);
        }

        public K pollFirst() {
            Entry<K, V> entry = this.this$0.internalPollFirstEntry();
            return entry != null ? entry.getKey() : null;
        }

        public K pollLast() {
            Entry<K, V> entry = this.this$0.internalPollLastEntry();
            return entry != null ? entry.getKey() : null;
        }

        public NavigableSet<K> subSet(K from, boolean fromInclusive, K to, boolean toInclusive) {
            return this.this$0.subMap(from, fromInclusive, to, toInclusive).navigableKeySet();
        }

        public SortedSet<K> subSet(K fromInclusive, K toExclusive) {
            return this.this$0.subMap(fromInclusive, true, toExclusive, false).navigableKeySet();
        }

        public NavigableSet<K> headSet(K to, boolean inclusive) {
            return this.this$0.headMap(to, inclusive).navigableKeySet();
        }

        public SortedSet<K> headSet(K toExclusive) {
            return this.this$0.headMap(toExclusive, false).navigableKeySet();
        }

        public NavigableSet<K> tailSet(K from, boolean inclusive) {
            return this.this$0.tailMap(from, inclusive).navigableKeySet();
        }

        public SortedSet<K> tailSet(K fromInclusive) {
            return this.this$0.tailMap(fromInclusive, true).navigableKeySet();
        }

        public NavigableSet<K> descendingSet() {
            return new BoundedMap(this.this$0, false, null, Bound.NO_BOUND, null, Bound.NO_BOUND).navigableKeySet();
        }
    }

    static class Node<K, V> implements Entry<K, V> {
        int height;
        final K key;
        Node<K, V> left;
        Node<K, V> parent;
        Node<K, V> right;
        V value;

        Node(Node<K, V> parent, K key) {
            this.parent = parent;
            this.key = key;
            this.height = 1;
        }

        Node<K, V> copy(Node<K, V> parent) {
            Node<K, V> result = new Node(parent, this.key);
            if (this.left != null) {
                result.left = this.left.copy(result);
            }
            if (this.right != null) {
                result.right = this.right.copy(result);
            }
            result.value = this.value;
            result.height = this.height;
            return result;
        }

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            return this.value;
        }

        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry other = (Entry) o;
            if (this.key == null) {
                if (other.getKey() != null) {
                    return false;
                }
            } else if (!this.key.equals(other.getKey())) {
                return false;
            }
            if (this.value == null) {
                if (other.getValue() != null) {
                    return false;
                }
            } else if (!this.value.equals(other.getValue())) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            int i = 0;
            int hashCode = this.key == null ? 0 : this.key.hashCode();
            if (this.value != null) {
                i = this.value.hashCode();
            }
            return hashCode ^ i;
        }

        public String toString() {
            return this.key + "=" + this.value;
        }

        Node<K, V> next() {
            if (this.right != null) {
                return this.right.first();
            }
            Node<K, V> parent = this.parent;
            while (parent != null) {
                Node<K, V> node;
                if (parent.left == node) {
                    return parent;
                }
                node = parent;
                parent = node.parent;
            }
            return null;
        }

        public Node<K, V> prev() {
            if (this.left != null) {
                return this.left.last();
            }
            Node<K, V> parent = this.parent;
            while (parent != null) {
                Node<K, V> node;
                if (parent.right == node) {
                    return parent;
                }
                node = parent;
                parent = node.parent;
            }
            return null;
        }

        public Node<K, V> first() {
            Node<K, V> node;
            Node<K, V> child = this.left;
            while (child != null) {
                node = child;
                child = node.left;
            }
            return node;
        }

        public Node<K, V> last() {
            Node<K, V> node;
            Node<K, V> child = this.right;
            while (child != null) {
                node = child;
                child = node.right;
            }
            return node;
        }
    }

    enum Relation {
        LOWER,
        FLOOR,
        EQUAL,
        CREATE,
        CEILING,
        HIGHER;

        Relation forOrder(boolean ascending) {
            if (ascending) {
                return this;
            }
            switch (AnonymousClass2.$SwitchMap$java$util$TreeMap$Relation[ordinal()]) {
                case NodeFilter.SHOW_ELEMENT /*1*/:
                    return HIGHER;
                case NodeFilter.SHOW_ATTRIBUTE /*2*/:
                    return CEILING;
                case XmlPullParser.END_TAG /*3*/:
                    return EQUAL;
                case NodeFilter.SHOW_TEXT /*4*/:
                    return FLOOR;
                case XmlPullParser.CDSECT /*5*/:
                    return LOWER;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    class SubMap extends AbstractMap<K, V> implements Serializable {
        private static final long serialVersionUID = -6520786458950516097L;
        Object fromKey;
        boolean fromStart;
        final /* synthetic */ TreeMap this$0;
        boolean toEnd;
        Object toKey;

        SubMap(java.util.TreeMap r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.TreeMap.SubMap.<init>(java.util.TreeMap):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.TreeMap.SubMap.<init>(java.util.TreeMap):void
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.TreeMap.SubMap.<init>(java.util.TreeMap):void");
        }

        protected java.lang.Object readResolve() throws java.io.ObjectStreamException {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.TreeMap.SubMap.readResolve():java.lang.Object
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.TreeMap.SubMap.readResolve():java.lang.Object
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.TreeMap.SubMap.readResolve():java.lang.Object");
        }

        public Set<Entry<K, V>> entrySet() {
            throw new UnsupportedOperationException();
        }
    }

    static {
        NATURAL_ORDER = new Comparator<Comparable>() {
            public int compare(Comparable a, Comparable b) {
                return a.compareTo(b);
            }
        };
    }

    public TreeMap() {
        this.size = 0;
        this.modCount = 0;
        this.comparator = NATURAL_ORDER;
    }

    public TreeMap(Map<? extends K, ? extends V> copyFrom) {
        this();
        for (Entry<? extends K, ? extends V> entry : copyFrom.entrySet()) {
            putInternal(entry.getKey(), entry.getValue());
        }
    }

    public TreeMap(Comparator<? super K> comparator) {
        this.size = 0;
        this.modCount = 0;
        if (comparator != null) {
            this.comparator = comparator;
        } else {
            this.comparator = NATURAL_ORDER;
        }
    }

    public TreeMap(SortedMap<K, ? extends V> copyFrom) {
        this.size = 0;
        this.modCount = 0;
        Comparator<? super K> sourceComparator = copyFrom.comparator();
        if (sourceComparator != null) {
            this.comparator = sourceComparator;
        } else {
            this.comparator = NATURAL_ORDER;
        }
        for (Entry<K, ? extends V> entry : copyFrom.entrySet()) {
            putInternal(entry.getKey(), entry.getValue());
        }
    }

    public Object clone() {
        Node node = null;
        try {
            TreeMap<K, V> map = (TreeMap) super.clone();
            if (this.root != null) {
                node = this.root.copy(null);
            }
            map.root = node;
            map.entrySet = null;
            map.keySet = null;
            return map;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public V get(Object key) {
        Entry<K, V> entry = findByObject(key);
        return entry != null ? entry.getValue() : null;
    }

    public boolean containsKey(Object key) {
        return findByObject(key) != null;
    }

    public V put(K key, V value) {
        return putInternal(key, value);
    }

    public void clear() {
        this.root = null;
        this.size = 0;
        this.modCount++;
    }

    public V remove(Object key) {
        Node<K, V> node = removeInternalByKey(key);
        return node != null ? node.value : null;
    }

    V putInternal(K key, V value) {
        Node<K, V> created = find(key, Relation.CREATE);
        V result = created.value;
        created.value = value;
        return result;
    }

    Node<K, V> find(K key, Relation relation) {
        if (this.root != null) {
            Comparable<Object> comparableKey;
            if (this.comparator == NATURAL_ORDER) {
                comparableKey = (Comparable) key;
            } else {
                comparableKey = null;
            }
            Node<K, V> nearest = this.root;
            while (true) {
                int comparison = comparableKey != null ? comparableKey.compareTo(nearest.key) : this.comparator.compare(key, nearest.key);
                if (comparison == 0) {
                    switch (AnonymousClass2.$SwitchMap$java$util$TreeMap$Relation[relation.ordinal()]) {
                        case NodeFilter.SHOW_ELEMENT /*1*/:
                            return nearest.prev();
                        case NodeFilter.SHOW_ATTRIBUTE /*2*/:
                        case XmlPullParser.END_TAG /*3*/:
                        case NodeFilter.SHOW_TEXT /*4*/:
                        case XmlPullParser.ENTITY_REF /*6*/:
                            return nearest;
                        case XmlPullParser.CDSECT /*5*/:
                            return nearest.next();
                    }
                }
                Node<K, V> child = comparison < 0 ? nearest.left : nearest.right;
                if (child == null) {
                    Node<K, V> created;
                    if (comparison >= 0) {
                        switch (AnonymousClass2.$SwitchMap$java$util$TreeMap$Relation[relation.ordinal()]) {
                            case NodeFilter.SHOW_ELEMENT /*1*/:
                            case NodeFilter.SHOW_ATTRIBUTE /*2*/:
                                return nearest;
                            case XmlPullParser.END_TAG /*3*/:
                                return null;
                            case NodeFilter.SHOW_TEXT /*4*/:
                            case XmlPullParser.CDSECT /*5*/:
                                return nearest.next();
                            case XmlPullParser.ENTITY_REF /*6*/:
                                created = new Node(nearest, key);
                                nearest.right = created;
                                this.size++;
                                this.modCount++;
                                rebalance(nearest, true);
                                return created;
                            default:
                                break;
                        }
                    }
                    switch (AnonymousClass2.$SwitchMap$java$util$TreeMap$Relation[relation.ordinal()]) {
                        case NodeFilter.SHOW_ELEMENT /*1*/:
                        case NodeFilter.SHOW_ATTRIBUTE /*2*/:
                            return nearest.prev();
                        case XmlPullParser.END_TAG /*3*/:
                            return null;
                        case NodeFilter.SHOW_TEXT /*4*/:
                        case XmlPullParser.CDSECT /*5*/:
                            return nearest;
                        case XmlPullParser.ENTITY_REF /*6*/:
                            created = new Node(nearest, key);
                            nearest.left = created;
                            this.size++;
                            this.modCount++;
                            rebalance(nearest, true);
                            return created;
                        default:
                            break;
                    }
                }
                nearest = child;
            }
        } else if (this.comparator == NATURAL_ORDER && !(key instanceof Comparable)) {
            throw new ClassCastException(key.getClass().getName() + " is not Comparable");
        } else if (relation != Relation.CREATE) {
            return null;
        } else {
            this.root = new Node(null, key);
            this.size = 1;
            this.modCount++;
            return this.root;
        }
    }

    Node<K, V> findByObject(Object key) {
        return find(key, Relation.EQUAL);
    }

    Node<K, V> findByEntry(Entry<?, ?> entry) {
        Node<K, V> mine = findByObject(entry.getKey());
        boolean valuesEqual = mine != null && Objects.equal(mine.value, entry.getValue());
        return valuesEqual ? mine : null;
    }

    void removeInternal(Node<K, V> node) {
        Node<K, V> left = node.left;
        Node<K, V> right = node.right;
        Node<K, V> originalParent = node.parent;
        if (left == null || right == null) {
            if (left != null) {
                replaceInParent(node, left);
                node.left = null;
            } else if (right != null) {
                replaceInParent(node, right);
                node.right = null;
            } else {
                replaceInParent(node, null);
            }
            rebalance(originalParent, false);
            this.size--;
            this.modCount++;
            return;
        }
        Node<K, V> adjacent = left.height > right.height ? left.last() : right.first();
        removeInternal(adjacent);
        int leftHeight = 0;
        left = node.left;
        if (left != null) {
            leftHeight = left.height;
            adjacent.left = left;
            left.parent = adjacent;
            node.left = null;
        }
        int rightHeight = 0;
        right = node.right;
        if (right != null) {
            rightHeight = right.height;
            adjacent.right = right;
            right.parent = adjacent;
            node.right = null;
        }
        adjacent.height = Math.max(leftHeight, rightHeight) + 1;
        replaceInParent(node, adjacent);
    }

    Node<K, V> removeInternalByKey(Object key) {
        Node<K, V> node = findByObject(key);
        if (node != null) {
            removeInternal(node);
        }
        return node;
    }

    private void replaceInParent(Node<K, V> node, Node<K, V> replacement) {
        Node<K, V> parent = node.parent;
        node.parent = null;
        if (replacement != null) {
            replacement.parent = parent;
        }
        if (parent == null) {
            this.root = replacement;
        } else if (parent.left == node) {
            parent.left = replacement;
        } else {
            parent.right = replacement;
        }
    }

    private void rebalance(Node<K, V> unbalanced, boolean insert) {
        for (Node<K, V> node = unbalanced; node != null; node = node.parent) {
            Node<K, V> left = node.left;
            Node<K, V> right = node.right;
            int leftHeight = left != null ? left.height : 0;
            int rightHeight = right != null ? right.height : 0;
            int delta = leftHeight - rightHeight;
            if (delta == -2) {
                Node<K, V> rightLeft = right.left;
                Node<K, V> rightRight = right.right;
                int rightDelta = (rightLeft != null ? rightLeft.height : 0) - (rightRight != null ? rightRight.height : 0);
                if (rightDelta == -1 || (rightDelta == 0 && !insert)) {
                    rotateLeft(node);
                } else {
                    rotateRight(right);
                    rotateLeft(node);
                }
                if (insert) {
                    return;
                }
            } else if (delta == 2) {
                Node<K, V> leftLeft = left.left;
                Node<K, V> leftRight = left.right;
                int leftDelta = (leftLeft != null ? leftLeft.height : 0) - (leftRight != null ? leftRight.height : 0);
                if (leftDelta == 1 || (leftDelta == 0 && !insert)) {
                    rotateRight(node);
                } else {
                    rotateLeft(left);
                    rotateRight(node);
                }
                if (insert) {
                    return;
                }
            } else if (delta == 0) {
                node.height = leftHeight + 1;
                if (insert) {
                    return;
                }
            } else {
                node.height = Math.max(leftHeight, rightHeight) + 1;
                if (!insert) {
                    return;
                }
            }
        }
    }

    private void rotateLeft(Node<K, V> root) {
        int i;
        int i2 = 0;
        Node<K, V> left = root.left;
        Node<K, V> pivot = root.right;
        Node<K, V> pivotLeft = pivot.left;
        Node<K, V> pivotRight = pivot.right;
        root.right = pivotLeft;
        if (pivotLeft != null) {
            pivotLeft.parent = root;
        }
        replaceInParent(root, pivot);
        pivot.left = root;
        root.parent = pivot;
        if (left != null) {
            i = left.height;
        } else {
            i = 0;
        }
        root.height = Math.max(i, pivotLeft != null ? pivotLeft.height : 0) + 1;
        int i3 = root.height;
        if (pivotRight != null) {
            i2 = pivotRight.height;
        }
        pivot.height = Math.max(i3, i2) + 1;
    }

    private void rotateRight(Node<K, V> root) {
        int i;
        int i2 = 0;
        Node<K, V> pivot = root.left;
        Node<K, V> right = root.right;
        Node<K, V> pivotLeft = pivot.left;
        Node<K, V> pivotRight = pivot.right;
        root.left = pivotRight;
        if (pivotRight != null) {
            pivotRight.parent = root;
        }
        replaceInParent(root, pivot);
        pivot.right = root;
        root.parent = pivot;
        if (right != null) {
            i = right.height;
        } else {
            i = 0;
        }
        root.height = Math.max(i, pivotRight != null ? pivotRight.height : 0) + 1;
        int i3 = root.height;
        if (pivotLeft != null) {
            i2 = pivotLeft.height;
        }
        pivot.height = Math.max(i3, i2) + 1;
    }

    private SimpleImmutableEntry<K, V> immutableCopy(Entry<K, V> entry) {
        return entry == null ? null : new SimpleImmutableEntry(entry);
    }

    public Entry<K, V> firstEntry() {
        return immutableCopy(this.root == null ? null : this.root.first());
    }

    private Entry<K, V> internalPollFirstEntry() {
        if (this.root == null) {
            return null;
        }
        Entry<K, V> result = this.root.first();
        removeInternal(result);
        return result;
    }

    public Entry<K, V> pollFirstEntry() {
        return immutableCopy(internalPollFirstEntry());
    }

    public K firstKey() {
        if (this.root != null) {
            return this.root.first().getKey();
        }
        throw new NoSuchElementException();
    }

    public Entry<K, V> lastEntry() {
        return immutableCopy(this.root == null ? null : this.root.last());
    }

    private Entry<K, V> internalPollLastEntry() {
        if (this.root == null) {
            return null;
        }
        Entry<K, V> result = this.root.last();
        removeInternal(result);
        return result;
    }

    public Entry<K, V> pollLastEntry() {
        return immutableCopy(internalPollLastEntry());
    }

    public K lastKey() {
        if (this.root != null) {
            return this.root.last().getKey();
        }
        throw new NoSuchElementException();
    }

    public Entry<K, V> lowerEntry(K key) {
        return immutableCopy(find(key, Relation.LOWER));
    }

    public K lowerKey(K key) {
        Entry<K, V> entry = find(key, Relation.LOWER);
        return entry != null ? entry.getKey() : null;
    }

    public Entry<K, V> floorEntry(K key) {
        return immutableCopy(find(key, Relation.FLOOR));
    }

    public K floorKey(K key) {
        Entry<K, V> entry = find(key, Relation.FLOOR);
        return entry != null ? entry.getKey() : null;
    }

    public Entry<K, V> ceilingEntry(K key) {
        return immutableCopy(find(key, Relation.CEILING));
    }

    public K ceilingKey(K key) {
        Entry<K, V> entry = find(key, Relation.CEILING);
        return entry != null ? entry.getKey() : null;
    }

    public Entry<K, V> higherEntry(K key) {
        return immutableCopy(find(key, Relation.HIGHER));
    }

    public K higherKey(K key) {
        Entry<K, V> entry = find(key, Relation.HIGHER);
        return entry != null ? entry.getKey() : null;
    }

    public Comparator<? super K> comparator() {
        return this.comparator != NATURAL_ORDER ? this.comparator : null;
    }

    public Set<Entry<K, V>> entrySet() {
        EntrySet result = this.entrySet;
        if (result != null) {
            return result;
        }
        result = new EntrySet(this);
        this.entrySet = result;
        return result;
    }

    public Set<K> keySet() {
        KeySet result = this.keySet;
        if (result != null) {
            return result;
        }
        result = new KeySet(this);
        this.keySet = result;
        return result;
    }

    public NavigableSet<K> navigableKeySet() {
        KeySet result = this.keySet;
        if (result != null) {
            return result;
        }
        result = new KeySet(this);
        this.keySet = result;
        return result;
    }

    public NavigableMap<K, V> subMap(K from, boolean fromInclusive, K to, boolean toInclusive) {
        return new BoundedMap(this, true, from, fromInclusive ? Bound.INCLUSIVE : Bound.EXCLUSIVE, to, toInclusive ? Bound.INCLUSIVE : Bound.EXCLUSIVE);
    }

    public SortedMap<K, V> subMap(K fromInclusive, K toExclusive) {
        return new BoundedMap(this, true, fromInclusive, Bound.INCLUSIVE, toExclusive, Bound.EXCLUSIVE);
    }

    public NavigableMap<K, V> headMap(K to, boolean inclusive) {
        return new BoundedMap(this, true, null, Bound.NO_BOUND, to, inclusive ? Bound.INCLUSIVE : Bound.EXCLUSIVE);
    }

    public SortedMap<K, V> headMap(K toExclusive) {
        return new BoundedMap(this, true, null, Bound.NO_BOUND, toExclusive, Bound.EXCLUSIVE);
    }

    public NavigableMap<K, V> tailMap(K from, boolean inclusive) {
        return new BoundedMap(this, true, from, inclusive ? Bound.INCLUSIVE : Bound.EXCLUSIVE, null, Bound.NO_BOUND);
    }

    public SortedMap<K, V> tailMap(K fromInclusive) {
        return new BoundedMap(this, true, fromInclusive, Bound.INCLUSIVE, null, Bound.NO_BOUND);
    }

    public NavigableMap<K, V> descendingMap() {
        return new BoundedMap(this, false, null, Bound.NO_BOUND, null, Bound.NO_BOUND);
    }

    public NavigableSet<K> descendingKeySet() {
        return new BoundedMap(this, false, null, Bound.NO_BOUND, null, Bound.NO_BOUND).navigableKeySet();
    }

    static int count(Iterator<?> iterator) {
        int count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        return count;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.putFields().put("comparator", comparator());
        stream.writeFields();
        stream.writeInt(this.size);
        for (Entry<K, V> entry : entrySet()) {
            stream.writeObject(entry.getKey());
            stream.writeObject(entry.getValue());
        }
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        this.comparator = (Comparator) stream.readFields().get("comparator", null);
        if (this.comparator == null) {
            this.comparator = NATURAL_ORDER;
        }
        int size = stream.readInt();
        for (int i = 0; i < size; i++) {
            putInternal(stream.readObject(), stream.readObject());
        }
    }
}
