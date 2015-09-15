package java.util;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.Map.Entry;

public class Hashtable<K, V> extends Dictionary<K, V> implements Map<K, V>, Cloneable, Serializable {
    private static final int CHARS_PER_ENTRY = 15;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final Entry[] EMPTY_TABLE = null;
    private static final int MAXIMUM_CAPACITY = 1073741824;
    private static final int MINIMUM_CAPACITY = 4;
    private static final ObjectStreamField[] serialPersistentFields = null;
    private static final long serialVersionUID = 1421746759512286392L;
    private transient Set<Entry<K, V>> entrySet;
    private transient Set<K> keySet;
    private transient int modCount;
    private transient int size;
    private transient HashtableEntry<K, V>[] table;
    private transient int threshold;
    private transient Collection<V> values;

    private abstract class HashIterator {
        int expectedModCount;
        HashtableEntry<K, V> lastEntryReturned;
        HashtableEntry<K, V> nextEntry;
        int nextIndex;

        HashIterator() {
            this.expectedModCount = Hashtable.this.modCount;
            HashtableEntry<K, V>[] tab = Hashtable.this.table;
            HashtableEntry<K, V> next = null;
            while (next == null && this.nextIndex < tab.length) {
                int i = this.nextIndex;
                this.nextIndex = i + 1;
                next = tab[i];
            }
            this.nextEntry = next;
        }

        public boolean hasNext() {
            return this.nextEntry != null;
        }

        HashtableEntry<K, V> nextEntry() {
            if (Hashtable.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            } else if (this.nextEntry == null) {
                throw new NoSuchElementException();
            } else {
                HashtableEntry<K, V> entryToReturn = this.nextEntry;
                HashtableEntry<K, V>[] tab = Hashtable.this.table;
                HashtableEntry<K, V> next = entryToReturn.next;
                while (next == null && this.nextIndex < tab.length) {
                    int i = this.nextIndex;
                    this.nextIndex = i + 1;
                    next = tab[i];
                }
                this.nextEntry = next;
                this.lastEntryReturned = entryToReturn;
                return entryToReturn;
            }
        }

        HashtableEntry<K, V> nextEntryNotFailFast() {
            if (this.nextEntry == null) {
                throw new NoSuchElementException();
            }
            HashtableEntry<K, V> entryToReturn = this.nextEntry;
            HashtableEntry<K, V>[] tab = Hashtable.this.table;
            HashtableEntry<K, V> next = entryToReturn.next;
            while (next == null && this.nextIndex < tab.length) {
                int i = this.nextIndex;
                this.nextIndex = i + 1;
                next = tab[i];
            }
            this.nextEntry = next;
            this.lastEntryReturned = entryToReturn;
            return entryToReturn;
        }

        public void remove() {
            if (this.lastEntryReturned == null) {
                throw new IllegalStateException();
            } else if (Hashtable.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            } else {
                Hashtable.this.remove(this.lastEntryReturned.key);
                this.lastEntryReturned = null;
                this.expectedModCount = Hashtable.this.modCount;
            }
        }
    }

    private final class EntryIterator extends HashIterator implements Iterator<Entry<K, V>> {
        private EntryIterator() {
            super();
        }

        public Entry<K, V> next() {
            return nextEntry();
        }
    }

    private final class EntrySet extends AbstractSet<Entry<K, V>> {
        private EntrySet() {
        }

        public Iterator<Entry<K, V>> iterator() {
            return new EntryIterator(null);
        }

        public boolean contains(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry<?, ?> e = (Entry) o;
            return Hashtable.this.containsMapping(e.getKey(), e.getValue());
        }

        public boolean remove(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry<?, ?> e = (Entry) o;
            return Hashtable.this.removeMapping(e.getKey(), e.getValue());
        }

        public int size() {
            return Hashtable.this.size();
        }

        public void clear() {
            Hashtable.this.clear();
        }

        public boolean removeAll(Collection<?> collection) {
            boolean removeAll;
            synchronized (Hashtable.this) {
                removeAll = super.removeAll(collection);
            }
            return removeAll;
        }

        public boolean retainAll(Collection<?> collection) {
            boolean retainAll;
            synchronized (Hashtable.this) {
                retainAll = super.retainAll(collection);
            }
            return retainAll;
        }

        public boolean containsAll(Collection<?> collection) {
            boolean containsAll;
            synchronized (Hashtable.this) {
                containsAll = super.containsAll(collection);
            }
            return containsAll;
        }

        public boolean equals(Object object) {
            boolean equals;
            synchronized (Hashtable.this) {
                equals = super.equals(object);
            }
            return equals;
        }

        public int hashCode() {
            return Hashtable.this.hashCode();
        }

        public String toString() {
            String abstractSet;
            synchronized (Hashtable.this) {
                abstractSet = super.toString();
            }
            return abstractSet;
        }

        public Object[] toArray() {
            Object[] toArray;
            synchronized (Hashtable.this) {
                toArray = super.toArray();
            }
            return toArray;
        }

        public <T> T[] toArray(T[] a) {
            T[] toArray;
            synchronized (Hashtable.this) {
                toArray = super.toArray(a);
            }
            return toArray;
        }
    }

    private static class HashtableEntry<K, V> implements Entry<K, V> {
        final int hash;
        final K key;
        HashtableEntry<K, V> next;
        V value;

        HashtableEntry(K key, V value, int hash, HashtableEntry<K, V> next) {
            this.key = key;
            this.value = value;
            this.hash = hash;
            this.next = next;
        }

        public final K getKey() {
            return this.key;
        }

        public final V getValue() {
            return this.value;
        }

        public final V setValue(V value) {
            if (value == null) {
                throw new NullPointerException("value == null");
            }
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public final boolean equals(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry<?, ?> e = (Entry) o;
            if (this.key.equals(e.getKey()) && this.value.equals(e.getValue())) {
                return true;
            }
            return false;
        }

        public final int hashCode() {
            return this.key.hashCode() ^ this.value.hashCode();
        }

        public final String toString() {
            return this.key + "=" + this.value;
        }
    }

    private final class KeyEnumeration extends HashIterator implements Enumeration<K> {
        private KeyEnumeration() {
            super();
        }

        public boolean hasMoreElements() {
            return hasNext();
        }

        public K nextElement() {
            return nextEntryNotFailFast().key;
        }
    }

    private final class KeyIterator extends HashIterator implements Iterator<K> {
        final /* synthetic */ Hashtable this$0;

        private KeyIterator(java.util.Hashtable r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Hashtable.KeyIterator.<init>(java.util.Hashtable):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Hashtable.KeyIterator.<init>(java.util.Hashtable):void
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.Hashtable.KeyIterator.<init>(java.util.Hashtable):void");
        }

        /* synthetic */ KeyIterator(java.util.Hashtable r1, java.util.Hashtable.AnonymousClass1 r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Hashtable.KeyIterator.<init>(java.util.Hashtable, java.util.Hashtable$1):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Hashtable.KeyIterator.<init>(java.util.Hashtable, java.util.Hashtable$1):void
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.Hashtable.KeyIterator.<init>(java.util.Hashtable, java.util.Hashtable$1):void");
        }

        public K next() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Hashtable.KeyIterator.next():K
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Hashtable.KeyIterator.next():K
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.Hashtable.KeyIterator.next():K");
        }
    }

    private final class KeySet extends AbstractSet<K> {
        final /* synthetic */ Hashtable this$0;

        private KeySet(java.util.Hashtable r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Hashtable.KeySet.<init>(java.util.Hashtable):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Hashtable.KeySet.<init>(java.util.Hashtable):void
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.Hashtable.KeySet.<init>(java.util.Hashtable):void");
        }

        /* synthetic */ KeySet(java.util.Hashtable r1, java.util.Hashtable.AnonymousClass1 r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Hashtable.KeySet.<init>(java.util.Hashtable, java.util.Hashtable$1):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Hashtable.KeySet.<init>(java.util.Hashtable, java.util.Hashtable$1):void
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.Hashtable.KeySet.<init>(java.util.Hashtable, java.util.Hashtable$1):void");
        }

        public void clear() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Hashtable.KeySet.clear():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Hashtable.KeySet.clear():void
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.Hashtable.KeySet.clear():void");
        }

        public boolean contains(java.lang.Object r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Hashtable.KeySet.contains(java.lang.Object):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Hashtable.KeySet.contains(java.lang.Object):boolean
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.Hashtable.KeySet.contains(java.lang.Object):boolean");
        }

        public boolean containsAll(java.util.Collection<?> r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Hashtable.KeySet.containsAll(java.util.Collection):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Hashtable.KeySet.containsAll(java.util.Collection):boolean
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.Hashtable.KeySet.containsAll(java.util.Collection):boolean");
        }

        public boolean equals(java.lang.Object r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Hashtable.KeySet.equals(java.lang.Object):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Hashtable.KeySet.equals(java.lang.Object):boolean
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.Hashtable.KeySet.equals(java.lang.Object):boolean");
        }

        public int hashCode() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Hashtable.KeySet.hashCode():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Hashtable.KeySet.hashCode():int
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.Hashtable.KeySet.hashCode():int");
        }

        public java.util.Iterator<K> iterator() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Hashtable.KeySet.iterator():java.util.Iterator<K>
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Hashtable.KeySet.iterator():java.util.Iterator<K>
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.Hashtable.KeySet.iterator():java.util.Iterator<K>");
        }

        public boolean remove(java.lang.Object r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Hashtable.KeySet.remove(java.lang.Object):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Hashtable.KeySet.remove(java.lang.Object):boolean
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.Hashtable.KeySet.remove(java.lang.Object):boolean");
        }

        public boolean removeAll(java.util.Collection<?> r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Hashtable.KeySet.removeAll(java.util.Collection):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Hashtable.KeySet.removeAll(java.util.Collection):boolean
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.Hashtable.KeySet.removeAll(java.util.Collection):boolean");
        }

        public boolean retainAll(java.util.Collection<?> r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Hashtable.KeySet.retainAll(java.util.Collection):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Hashtable.KeySet.retainAll(java.util.Collection):boolean
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.Hashtable.KeySet.retainAll(java.util.Collection):boolean");
        }

        public int size() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Hashtable.KeySet.size():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Hashtable.KeySet.size():int
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.Hashtable.KeySet.size():int");
        }

        public java.lang.Object[] toArray() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Hashtable.KeySet.toArray():java.lang.Object[]
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Hashtable.KeySet.toArray():java.lang.Object[]
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.Hashtable.KeySet.toArray():java.lang.Object[]");
        }

        public <T> T[] toArray(T[] r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Hashtable.KeySet.toArray(java.lang.Object[]):T[]
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Hashtable.KeySet.toArray(java.lang.Object[]):T[]
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.Hashtable.KeySet.toArray(java.lang.Object[]):T[]");
        }

        public java.lang.String toString() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Hashtable.KeySet.toString():java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Hashtable.KeySet.toString():java.lang.String
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.Hashtable.KeySet.toString():java.lang.String");
        }
    }

    private final class ValueEnumeration extends HashIterator implements Enumeration<V> {
        final /* synthetic */ Hashtable this$0;

        private ValueEnumeration(java.util.Hashtable r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Hashtable.ValueEnumeration.<init>(java.util.Hashtable):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Hashtable.ValueEnumeration.<init>(java.util.Hashtable):void
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.Hashtable.ValueEnumeration.<init>(java.util.Hashtable):void");
        }

        /* synthetic */ ValueEnumeration(java.util.Hashtable r1, java.util.Hashtable.AnonymousClass1 r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Hashtable.ValueEnumeration.<init>(java.util.Hashtable, java.util.Hashtable$1):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Hashtable.ValueEnumeration.<init>(java.util.Hashtable, java.util.Hashtable$1):void
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.Hashtable.ValueEnumeration.<init>(java.util.Hashtable, java.util.Hashtable$1):void");
        }

        public boolean hasMoreElements() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Hashtable.ValueEnumeration.hasMoreElements():boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Hashtable.ValueEnumeration.hasMoreElements():boolean
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.Hashtable.ValueEnumeration.hasMoreElements():boolean");
        }

        public V nextElement() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Hashtable.ValueEnumeration.nextElement():V
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Hashtable.ValueEnumeration.nextElement():V
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.Hashtable.ValueEnumeration.nextElement():V");
        }
    }

    private final class ValueIterator extends HashIterator implements Iterator<V> {
        final /* synthetic */ Hashtable this$0;

        private ValueIterator(Hashtable hashtable) {
            this.this$0 = hashtable;
            super();
        }

        /* synthetic */ ValueIterator(Hashtable x0, AnonymousClass1 x1) {
            this(x0);
        }

        public V next() {
            return nextEntry().value;
        }
    }

    private final class Values extends AbstractCollection<V> {
        final /* synthetic */ Hashtable this$0;

        private Values(Hashtable hashtable) {
            this.this$0 = hashtable;
        }

        /* synthetic */ Values(Hashtable x0, AnonymousClass1 x1) {
            this(x0);
        }

        public Iterator<V> iterator() {
            return new ValueIterator(this.this$0, null);
        }

        public int size() {
            return this.this$0.size();
        }

        public boolean contains(Object o) {
            return this.this$0.containsValue(o);
        }

        public void clear() {
            this.this$0.clear();
        }

        public boolean containsAll(Collection<?> collection) {
            boolean containsAll;
            synchronized (this.this$0) {
                containsAll = super.containsAll(collection);
            }
            return containsAll;
        }

        public String toString() {
            String abstractCollection;
            synchronized (this.this$0) {
                abstractCollection = super.toString();
            }
            return abstractCollection;
        }

        public Object[] toArray() {
            Object[] toArray;
            synchronized (this.this$0) {
                toArray = super.toArray();
            }
            return toArray;
        }

        public <T> T[] toArray(T[] a) {
            T[] toArray;
            synchronized (this.this$0) {
                toArray = super.toArray(a);
            }
            return toArray;
        }
    }

    static {
        EMPTY_TABLE = new HashtableEntry[2];
        serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("threshold", Integer.TYPE), new ObjectStreamField("loadFactor", Float.TYPE)};
    }

    public Hashtable() {
        this.table = (HashtableEntry[]) EMPTY_TABLE;
        this.threshold = -1;
    }

    public Hashtable(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity: " + capacity);
        } else if (capacity == 0) {
            this.table = (HashtableEntry[]) ((HashtableEntry[]) EMPTY_TABLE);
            this.threshold = -1;
        } else {
            if (capacity < MINIMUM_CAPACITY) {
                capacity = MINIMUM_CAPACITY;
            } else if (capacity > MAXIMUM_CAPACITY) {
                capacity = MAXIMUM_CAPACITY;
            } else {
                capacity = Collections.roundUpToPowerOfTwo(capacity);
            }
            makeTable(capacity);
        }
    }

    public Hashtable(int capacity, float loadFactor) {
        this(capacity);
        if (loadFactor <= 0.0f || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Load factor: " + loadFactor);
        }
    }

    public Hashtable(Map<? extends K, ? extends V> map) {
        this(capacityForInitSize(map.size()));
        constructorPutAll(map);
    }

    private void constructorPutAll(Map<? extends K, ? extends V> map) {
        if (this.table == EMPTY_TABLE) {
            doubleCapacity();
        }
        for (Entry<? extends K, ? extends V> e : map.entrySet()) {
            constructorPut(e.getKey(), e.getValue());
        }
    }

    private static int capacityForInitSize(int size) {
        int result = (size >> 1) + size;
        return (-1073741824 & result) == 0 ? result : MAXIMUM_CAPACITY;
    }

    public synchronized Object clone() {
        Hashtable<K, V> result;
        try {
            result = (Hashtable) super.clone();
            result.makeTable(this.table.length);
            result.size = 0;
            result.keySet = null;
            result.entrySet = null;
            result.values = null;
            result.constructorPutAll(this);
        } catch (Object e) {
            throw new AssertionError(e);
        }
        return result;
    }

    public synchronized boolean isEmpty() {
        return this.size == 0;
    }

    public synchronized int size() {
        return this.size;
    }

    public synchronized V get(Object key) {
        V v;
        int hash = Collections.secondaryHash(key);
        HashtableEntry<K, V>[] tab = this.table;
        HashtableEntry<K, V> e = tab[(tab.length - 1) & hash];
        while (e != null) {
            K eKey = e.key;
            if (eKey == key || (e.hash == hash && key.equals(eKey))) {
                v = e.value;
                break;
            }
            e = e.next;
        }
        v = null;
        return v;
    }

    public synchronized boolean containsKey(Object key) {
        boolean z;
        int hash = Collections.secondaryHash(key);
        HashtableEntry<K, V>[] tab = this.table;
        HashtableEntry<K, V> e = tab[(tab.length - 1) & hash];
        while (e != null) {
            K eKey = e.key;
            if (eKey == key || (e.hash == hash && key.equals(eKey))) {
                z = true;
                break;
            }
            e = e.next;
        }
        z = false;
        return z;
    }

    public synchronized boolean containsValue(Object value) {
        boolean z;
        if (value == null) {
            throw new NullPointerException("value == null");
        }
        loop0:
        for (HashtableEntry e : this.table) {
            for (HashtableEntry e2 = tab[i]; e2 != null; e2 = e2.next) {
                if (value.equals(e2.value)) {
                    z = true;
                    break loop0;
                }
            }
        }
        z = false;
        return z;
    }

    public boolean contains(Object value) {
        return containsValue(value);
    }

    public synchronized V put(K key, V value) {
        V oldValue;
        if (key == null) {
            throw new NullPointerException("key == null");
        } else if (value == null) {
            throw new NullPointerException("value == null");
        } else {
            int hash = Collections.secondaryHash((Object) key);
            HashtableEntry<K, V>[] tab = this.table;
            int index = hash & (tab.length - 1);
            HashtableEntry<K, V> first = tab[index];
            HashtableEntry<K, V> e = first;
            while (e != null) {
                if (e.hash == hash && key.equals(e.key)) {
                    oldValue = e.value;
                    e.value = value;
                    break;
                }
                e = e.next;
            }
            this.modCount++;
            int i = this.size;
            this.size = i + 1;
            if (i > this.threshold) {
                rehash();
                tab = doubleCapacity();
                index = hash & (tab.length - 1);
                first = tab[index];
            }
            tab[index] = new HashtableEntry(key, value, hash, first);
            oldValue = null;
        }
        return oldValue;
    }

    private void constructorPut(K key, V value) {
        if (key == null) {
            throw new NullPointerException("key == null");
        } else if (value == null) {
            throw new NullPointerException("value == null");
        } else {
            int hash = Collections.secondaryHash((Object) key);
            HashtableEntry<K, V>[] tab = this.table;
            int index = hash & (tab.length - 1);
            HashtableEntry<K, V> first = tab[index];
            HashtableEntry<K, V> e = first;
            while (e != null) {
                if (e.hash == hash && key.equals(e.key)) {
                    e.value = value;
                    return;
                }
                e = e.next;
            }
            tab[index] = new HashtableEntry(key, value, hash, first);
            this.size++;
        }
    }

    public synchronized void putAll(Map<? extends K, ? extends V> map) {
        ensureCapacity(map.size());
        for (Entry<? extends K, ? extends V> e : map.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    private void ensureCapacity(int numMappings) {
        int newCapacity = Collections.roundUpToPowerOfTwo(capacityForInitSize(numMappings));
        if (newCapacity > oldCapacity) {
            rehash();
            if (newCapacity == oldCapacity * 2) {
                doubleCapacity();
                return;
            }
            HashtableEntry<K, V>[] newTable = makeTable(newCapacity);
            if (this.size != 0) {
                int newMask = newCapacity - 1;
                for (HashtableEntry<K, V> e : this.table) {
                    HashtableEntry<K, V> e2;
                    while (e2 != null) {
                        HashtableEntry<K, V> oldNext = e2.next;
                        int newIndex = e2.hash & newMask;
                        HashtableEntry<K, V> newNext = newTable[newIndex];
                        newTable[newIndex] = e2;
                        e2.next = newNext;
                        e2 = oldNext;
                    }
                }
            }
        }
    }

    protected void rehash() {
    }

    private HashtableEntry<K, V>[] makeTable(int newCapacity) {
        HashtableEntry[] newTable = (HashtableEntry[]) new HashtableEntry[newCapacity];
        this.table = newTable;
        this.threshold = (newCapacity >> 1) + (newCapacity >> 2);
        return newTable;
    }

    private HashtableEntry<K, V>[] doubleCapacity() {
        HashtableEntry<K, V>[] oldTable = this.table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY) {
            return oldTable;
        }
        HashtableEntry<K, V>[] newTable = makeTable(oldCapacity * 2);
        if (this.size == 0) {
            return newTable;
        }
        for (int j = 0; j < oldCapacity; j++) {
            HashtableEntry<K, V> e = oldTable[j];
            if (e != null) {
                int highBit = e.hash & oldCapacity;
                HashtableEntry<K, V> broken = null;
                newTable[j | highBit] = e;
                for (HashtableEntry<K, V> n = e.next; n != null; n = n.next) {
                    int nextHighBit = n.hash & oldCapacity;
                    if (nextHighBit != highBit) {
                        if (broken == null) {
                            newTable[j | nextHighBit] = n;
                        } else {
                            broken.next = n;
                        }
                        broken = e;
                        highBit = nextHighBit;
                    }
                    e = n;
                }
                if (broken != null) {
                    broken.next = null;
                }
            }
        }
        return newTable;
    }

    public synchronized V remove(Object key) {
        V v;
        int hash = Collections.secondaryHash(key);
        HashtableEntry<K, V>[] tab = this.table;
        int index = hash & (tab.length - 1);
        HashtableEntry<K, V> e = tab[index];
        HashtableEntry<K, V> prev = null;
        while (e != null) {
            if (e.hash == hash && key.equals(e.key)) {
                if (prev == null) {
                    tab[index] = e.next;
                } else {
                    prev.next = e.next;
                }
                this.modCount++;
                this.size--;
                v = e.value;
            } else {
                prev = e;
                e = e.next;
            }
        }
        v = null;
        return v;
    }

    public synchronized void clear() {
        if (this.size != 0) {
            Arrays.fill(this.table, null);
            this.modCount++;
            this.size = 0;
        }
    }

    public synchronized Set<K> keySet() {
        Set<K> ks;
        ks = this.keySet;
        if (ks == null) {
            ks = new KeySet();
            this.keySet = ks;
        }
        return ks;
    }

    public synchronized Collection<V> values() {
        Collection<V> vs;
        vs = this.values;
        if (vs == null) {
            vs = new Values();
            this.values = vs;
        }
        return vs;
    }

    public synchronized Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> es;
        es = this.entrySet;
        if (es == null) {
            es = new EntrySet();
            this.entrySet = es;
        }
        return es;
    }

    public synchronized Enumeration<K> keys() {
        return new KeyEnumeration();
    }

    public synchronized Enumeration<V> elements() {
        return new ValueEnumeration();
    }

    private synchronized boolean containsMapping(Object key, Object value) {
        boolean equals;
        int hash = Collections.secondaryHash(key);
        HashtableEntry<K, V>[] tab = this.table;
        HashtableEntry<K, V> e = tab[hash & (tab.length - 1)];
        while (e != null) {
            if (e.hash == hash && e.key.equals(key)) {
                equals = e.value.equals(value);
                break;
            }
            e = e.next;
        }
        equals = false;
        return equals;
    }

    private synchronized boolean removeMapping(Object key, Object value) {
        boolean z = false;
        synchronized (this) {
            int hash = Collections.secondaryHash(key);
            HashtableEntry<K, V>[] tab = this.table;
            int index = hash & (tab.length - 1);
            HashtableEntry<K, V> e = tab[index];
            HashtableEntry<K, V> prev = null;
            while (e != null) {
                if (e.hash == hash && e.key.equals(key)) {
                    if (e.value.equals(value)) {
                        if (prev == null) {
                            tab[index] = e.next;
                        } else {
                            prev.next = e.next;
                        }
                        this.modCount++;
                        this.size--;
                        z = true;
                    }
                } else {
                    prev = e;
                    e = e.next;
                }
            }
        }
        return z;
    }

    public synchronized boolean equals(Object object) {
        boolean z;
        z = (object instanceof Map) && entrySet().equals(((Map) object).entrySet());
        return z;
    }

    public synchronized int hashCode() {
        int result;
        result = 0;
        for (Entry<K, V> e : entrySet()) {
            K key = e.getKey();
            V value = e.getValue();
            if (!(key == this || value == this)) {
                int hashCode;
                if (key != null) {
                    hashCode = key.hashCode();
                } else {
                    hashCode = 0;
                }
                result += (value != null ? value.hashCode() : 0) ^ hashCode;
            }
        }
        return result;
    }

    public synchronized String toString() {
        StringBuilder result;
        result = new StringBuilder(this.size * CHARS_PER_ENTRY);
        result.append('{');
        Iterator<Entry<K, V>> i = entrySet().iterator();
        boolean hasMore = i.hasNext();
        while (hasMore) {
            Entry<K, V> entry = (Entry) i.next();
            K key = entry.getKey();
            result.append(key == this ? "(this Map)" : key.toString());
            result.append('=');
            V value = entry.getValue();
            result.append(value == this ? "(this Map)" : value.toString());
            hasMore = i.hasNext();
            if (hasMore) {
                result.append(", ");
            }
        }
        result.append('}');
        return result.toString();
    }

    private synchronized void writeObject(ObjectOutputStream stream) throws IOException {
        PutField fields = stream.putFields();
        fields.put("threshold", (int) (((float) this.table.length) * DEFAULT_LOAD_FACTOR));
        fields.put("loadFactor", (float) DEFAULT_LOAD_FACTOR);
        stream.writeFields();
        stream.writeInt(this.table.length);
        stream.writeInt(this.size);
        for (Entry<K, V> e : entrySet()) {
            stream.writeObject(e.getKey());
            stream.writeObject(e.getValue());
        }
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        int capacity = stream.readInt();
        if (capacity < 0) {
            throw new InvalidObjectException("Capacity: " + capacity);
        }
        if (capacity < MINIMUM_CAPACITY) {
            capacity = MINIMUM_CAPACITY;
        } else if (capacity > MAXIMUM_CAPACITY) {
            capacity = MAXIMUM_CAPACITY;
        } else {
            capacity = Collections.roundUpToPowerOfTwo(capacity);
        }
        makeTable(capacity);
        int size = stream.readInt();
        if (size < 0) {
            throw new InvalidObjectException("Size: " + size);
        }
        for (int i = 0; i < size; i++) {
            constructorPut(stream.readObject(), stream.readObject());
        }
    }
}
