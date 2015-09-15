package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;

public class EnumMap<K extends Enum<K>, V> extends AbstractMap<K, V> implements Serializable, Cloneable, Map<K, V> {
    private static final long serialVersionUID = 458661240069192865L;
    private transient EnumMapEntrySet<K, V> entrySet;
    transient int enumSize;
    transient boolean[] hasMapping;
    private Class<K> keyType;
    transient Enum[] keys;
    private transient int mappingsCount;
    transient Object[] values;

    private static class Entry<KT extends Enum<KT>, VT> extends MapEntry<KT, VT> {
        private final EnumMap<KT, VT> enumMap;
        private final int ordinal;

        Entry(KT theKey, VT theValue, EnumMap<KT, VT> em) {
            super(theKey, theValue);
            this.enumMap = em;
            this.ordinal = theKey.ordinal();
        }

        public boolean equals(Object object) {
            if (!this.enumMap.hasMapping[this.ordinal]) {
                return false;
            }
            boolean isEqual = false;
            if (object instanceof java.util.Map.Entry) {
                java.util.Map.Entry<KT, VT> entry = (java.util.Map.Entry) object;
                if (((Enum) this.key).equals(entry.getKey())) {
                    Object theValue = entry.getValue();
                    isEqual = this.enumMap.values[this.ordinal] == null ? theValue == null : this.enumMap.values[this.ordinal].equals(theValue);
                }
            }
            return isEqual;
        }

        public int hashCode() {
            int i = 0;
            int hashCode = this.enumMap.keys[this.ordinal] == null ? 0 : this.enumMap.keys[this.ordinal].hashCode();
            if (this.enumMap.values[this.ordinal] != null) {
                i = this.enumMap.values[this.ordinal].hashCode();
            }
            return hashCode ^ i;
        }

        public KT getKey() {
            checkEntryStatus();
            return this.enumMap.keys[this.ordinal];
        }

        public VT getValue() {
            checkEntryStatus();
            return this.enumMap.values[this.ordinal];
        }

        public VT setValue(VT value) {
            checkEntryStatus();
            return this.enumMap.put(this.enumMap.keys[this.ordinal], (Object) value);
        }

        public String toString() {
            StringBuilder result = new StringBuilder(this.enumMap.keys[this.ordinal].toString());
            result.append("=");
            result.append(this.enumMap.values[this.ordinal] == null ? "null" : this.enumMap.values[this.ordinal].toString());
            return result.toString();
        }

        private void checkEntryStatus() {
            if (!this.enumMap.hasMapping[this.ordinal]) {
                throw new IllegalStateException();
            }
        }
    }

    private static class EnumMapIterator<E, KT extends Enum<KT>, VT> implements Iterator<E> {
        final EnumMap<KT, VT> enumMap;
        int position;
        int prePosition;
        final Type<E, KT, VT> type;

        EnumMapIterator(Type<E, KT, VT> value, EnumMap<KT, VT> em) {
            this.position = 0;
            this.prePosition = -1;
            this.enumMap = em;
            this.type = value;
        }

        public boolean hasNext() {
            int length = this.enumMap.enumSize;
            while (this.position < length && !this.enumMap.hasMapping[this.position]) {
                this.position++;
            }
            if (this.position != length) {
                return true;
            }
            return false;
        }

        public E next() {
            if (hasNext()) {
                int i = this.position;
                this.position = i + 1;
                this.prePosition = i;
                return this.type.get(new MapEntry(this.enumMap.keys[this.prePosition], this.enumMap.values[this.prePosition]));
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            checkStatus();
            if (this.enumMap.hasMapping[this.prePosition]) {
                this.enumMap.remove(this.enumMap.keys[this.prePosition]);
            }
            this.prePosition = -1;
        }

        public String toString() {
            if (this.prePosition == -1) {
                return super.toString();
            }
            return this.type.get(new MapEntry(this.enumMap.keys[this.prePosition], this.enumMap.values[this.prePosition])).toString();
        }

        private void checkStatus() {
            if (this.prePosition == -1) {
                throw new IllegalStateException();
            }
        }
    }

    private static class EnumMapEntryIterator<E, KT extends Enum<KT>, VT> extends EnumMapIterator<E, KT, VT> {
        EnumMapEntryIterator(Type<E, KT, VT> value, EnumMap<KT, VT> em) {
            super(value, em);
        }

        public E next() {
            if (hasNext()) {
                int i = this.position;
                this.position = i + 1;
                this.prePosition = i;
                return this.type.get(new Entry(this.enumMap.keys[this.prePosition], this.enumMap.values[this.prePosition], this.enumMap));
            }
            throw new NoSuchElementException();
        }
    }

    private static class EnumMapEntrySet<KT extends Enum<KT>, VT> extends AbstractSet<java.util.Map.Entry<KT, VT>> {
        private final EnumMap<KT, VT> enumMap;

        EnumMapEntrySet(EnumMap<KT, VT> em) {
            this.enumMap = em;
        }

        public void clear() {
            this.enumMap.clear();
        }

        public boolean contains(Object object) {
            if (!(object instanceof java.util.Map.Entry)) {
                return false;
            }
            Object enumKey = ((java.util.Map.Entry) object).getKey();
            Object enumValue = ((java.util.Map.Entry) object).getValue();
            if (!this.enumMap.containsKey(enumKey)) {
                return false;
            }
            VT value = this.enumMap.get(enumKey);
            if (value == null) {
                return enumValue == null;
            } else {
                return value.equals(enumValue);
            }
        }

        public Iterator<java.util.Map.Entry<KT, VT>> iterator() {
            return new EnumMapEntryIterator(new Type<java.util.Map.Entry<KT, VT>, KT, VT>() {
                public java.util.Map.Entry<KT, VT> get(MapEntry<KT, VT> entry) {
                    return entry;
                }
            }, this.enumMap);
        }

        public boolean remove(Object object) {
            if (!contains(object)) {
                return false;
            }
            this.enumMap.remove(((java.util.Map.Entry) object).getKey());
            return true;
        }

        public int size() {
            return this.enumMap.size();
        }

        public Object[] toArray() {
            return toArray(new Object[this.enumMap.size()]);
        }

        public Object[] toArray(Object[] array) {
            int size = this.enumMap.size();
            int index = 0;
            Object[] entryArray = array;
            if (size > array.length) {
                entryArray = (Object[]) Array.newInstance(array.getClass().getComponentType(), size);
            }
            Iterator<java.util.Map.Entry<KT, VT>> iter = iterator();
            while (index < size) {
                java.util.Map.Entry<KT, VT> entry = (java.util.Map.Entry) iter.next();
                entryArray[index] = new MapEntry(entry.getKey(), entry.getValue());
                index++;
            }
            if (index < array.length) {
                entryArray[index] = null;
            }
            return entryArray;
        }
    }

    private static class EnumMapKeySet<KT extends Enum<KT>, VT> extends AbstractSet<KT> {
        private final EnumMap<KT, VT> enumMap;

        /* renamed from: java.util.EnumMap.EnumMapKeySet.1 */
        class AnonymousClass1 implements Type<KT, KT, VT> {
            final /* synthetic */ EnumMapKeySet this$0;

            AnonymousClass1(java.util.EnumMap.EnumMapKeySet r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.EnumMap.EnumMapKeySet.1.<init>(java.util.EnumMap$EnumMapKeySet):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.EnumMap.EnumMapKeySet.1.<init>(java.util.EnumMap$EnumMapKeySet):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.EnumMap.EnumMapKeySet.1.<init>(java.util.EnumMap$EnumMapKeySet):void");
            }

            public KT get(java.util.MapEntry<KT, VT> r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.EnumMap.EnumMapKeySet.1.get(java.util.MapEntry):KT
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.EnumMap.EnumMapKeySet.1.get(java.util.MapEntry):KT
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.EnumMap.EnumMapKeySet.1.get(java.util.MapEntry):KT");
            }

            public /* bridge */ /* synthetic */ java.lang.Object m16get(java.util.MapEntry r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.EnumMap.EnumMapKeySet.1.get(java.util.MapEntry):java.lang.Object
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.EnumMap.EnumMapKeySet.1.get(java.util.MapEntry):java.lang.Object
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: java.util.EnumMap.EnumMapKeySet.1.get(java.util.MapEntry):java.lang.Object");
            }
        }

        EnumMapKeySet(java.util.EnumMap<KT, VT> r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.EnumMap.EnumMapKeySet.<init>(java.util.EnumMap):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.EnumMap.EnumMapKeySet.<init>(java.util.EnumMap):void
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.EnumMap.EnumMapKeySet.<init>(java.util.EnumMap):void");
        }

        public void clear() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.EnumMap.EnumMapKeySet.clear():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.EnumMap.EnumMapKeySet.clear():void
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.EnumMap.EnumMapKeySet.clear():void");
        }

        public boolean contains(java.lang.Object r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.EnumMap.EnumMapKeySet.contains(java.lang.Object):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.EnumMap.EnumMapKeySet.contains(java.lang.Object):boolean
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.EnumMap.EnumMapKeySet.contains(java.lang.Object):boolean");
        }

        public java.util.Iterator iterator() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.EnumMap.EnumMapKeySet.iterator():java.util.Iterator
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.EnumMap.EnumMapKeySet.iterator():java.util.Iterator
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.EnumMap.EnumMapKeySet.iterator():java.util.Iterator");
        }

        public boolean remove(java.lang.Object r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.EnumMap.EnumMapKeySet.remove(java.lang.Object):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.EnumMap.EnumMapKeySet.remove(java.lang.Object):boolean
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.EnumMap.EnumMapKeySet.remove(java.lang.Object):boolean");
        }

        public int size() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.EnumMap.EnumMapKeySet.size():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.EnumMap.EnumMapKeySet.size():int
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.EnumMap.EnumMapKeySet.size():int");
        }
    }

    private static class EnumMapValueCollection<KT extends Enum<KT>, VT> extends AbstractCollection<VT> {
        private final EnumMap<KT, VT> enumMap;

        /* renamed from: java.util.EnumMap.EnumMapValueCollection.1 */
        class AnonymousClass1 implements Type<VT, KT, VT> {
            final /* synthetic */ EnumMapValueCollection this$0;

            AnonymousClass1(EnumMapValueCollection enumMapValueCollection) {
                this.this$0 = enumMapValueCollection;
            }

            public VT get(MapEntry<KT, VT> entry) {
                return entry.value;
            }
        }

        EnumMapValueCollection(EnumMap<KT, VT> em) {
            this.enumMap = em;
        }

        public void clear() {
            this.enumMap.clear();
        }

        public boolean contains(Object object) {
            return this.enumMap.containsValue(object);
        }

        public Iterator iterator() {
            return new EnumMapIterator(new AnonymousClass1(this), this.enumMap);
        }

        public boolean remove(Object object) {
            int i;
            if (object == null) {
                i = 0;
                while (i < this.enumMap.enumSize) {
                    if (this.enumMap.hasMapping[i] && this.enumMap.values[i] == null) {
                        this.enumMap.remove(this.enumMap.keys[i]);
                        return true;
                    }
                    i++;
                }
            } else {
                i = 0;
                while (i < this.enumMap.enumSize) {
                    if (this.enumMap.hasMapping[i] && object.equals(this.enumMap.values[i])) {
                        this.enumMap.remove(this.enumMap.keys[i]);
                        return true;
                    }
                    i++;
                }
            }
            return false;
        }

        public int size() {
            return this.enumMap.size();
        }
    }

    public /* bridge */ /* synthetic */ Object clone() throws CloneNotSupportedException {
        return clone();
    }

    public /* bridge */ /* synthetic */ Object put(Object x0, Object x1) {
        return put((Enum) x0, x1);
    }

    public EnumMap(Class<K> keyType) {
        this.entrySet = null;
        initialization((Class) keyType);
    }

    public EnumMap(EnumMap<K, ? extends V> map) {
        this.entrySet = null;
        initialization((EnumMap) map);
    }

    public EnumMap(Map<K, ? extends V> map) {
        this.entrySet = null;
        if (map instanceof EnumMap) {
            initialization((EnumMap) map);
        } else if (map.isEmpty()) {
            throw new IllegalArgumentException("map is empty");
        } else {
            Class clazz = ((Enum) map.keySet().iterator().next()).getClass();
            if (clazz.isEnum()) {
                initialization(clazz);
            } else {
                initialization(clazz.getSuperclass());
            }
            putAllImpl(map);
        }
    }

    public void clear() {
        Arrays.fill(this.values, null);
        Arrays.fill(this.hasMapping, false);
        this.mappingsCount = 0;
    }

    public EnumMap<K, V> m15clone() {
        try {
            EnumMap<K, V> enumMap = (EnumMap) super.clone();
            enumMap.initialization(this);
            return enumMap;
        } catch (Object e) {
            throw new AssertionError(e);
        }
    }

    public boolean containsKey(Object key) {
        if (!isValidKeyType(key)) {
            return false;
        }
        return this.hasMapping[((Enum) key).ordinal()];
    }

    public boolean containsValue(Object value) {
        int i;
        if (value == null) {
            i = 0;
            while (i < this.enumSize) {
                if (this.hasMapping[i] && this.values[i] == null) {
                    return true;
                }
                i++;
            }
        } else {
            i = 0;
            while (i < this.enumSize) {
                if (this.hasMapping[i] && value.equals(this.values[i])) {
                    return true;
                }
                i++;
            }
        }
        return false;
    }

    public Set<java.util.Map.Entry<K, V>> entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new EnumMapEntrySet(this);
        }
        return this.entrySet;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof EnumMap)) {
            return super.equals(object);
        }
        EnumMap<K, V> enumMap = (EnumMap) object;
        if (this.keyType != enumMap.keyType || size() != enumMap.size()) {
            return false;
        }
        if (Arrays.equals(this.hasMapping, enumMap.hasMapping) && Arrays.equals(this.values, enumMap.values)) {
            return true;
        }
        return false;
    }

    public V get(Object key) {
        if (!isValidKeyType(key)) {
            return null;
        }
        return this.values[((Enum) key).ordinal()];
    }

    public Set<K> keySet() {
        if (this.keySet == null) {
            this.keySet = new EnumMapKeySet(this);
        }
        return this.keySet;
    }

    public V put(K key, V value) {
        return putImpl(key, value);
    }

    public void putAll(Map<? extends K, ? extends V> map) {
        putAllImpl(map);
    }

    public V remove(Object key) {
        if (!isValidKeyType(key)) {
            return null;
        }
        int keyOrdinal = ((Enum) key).ordinal();
        if (this.hasMapping[keyOrdinal]) {
            this.hasMapping[keyOrdinal] = false;
            this.mappingsCount--;
        }
        V oldValue = this.values[keyOrdinal];
        this.values[keyOrdinal] = null;
        return oldValue;
    }

    public int size() {
        return this.mappingsCount;
    }

    public Collection<V> values() {
        if (this.valuesCollection == null) {
            this.valuesCollection = new EnumMapValueCollection(this);
        }
        return this.valuesCollection;
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        initialization(this.keyType);
        for (int i = stream.readInt(); i > 0; i--) {
            putImpl((Enum) stream.readObject(), stream.readObject());
        }
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeInt(this.mappingsCount);
        for (java.util.Map.Entry<K, V> entry : entrySet()) {
            stream.writeObject(entry.getKey());
            stream.writeObject(entry.getValue());
        }
    }

    private boolean isValidKeyType(Object key) {
        if (key == null || !this.keyType.isInstance(key)) {
            return false;
        }
        return true;
    }

    private void initialization(EnumMap enumMap) {
        this.keyType = enumMap.keyType;
        this.keys = enumMap.keys;
        this.enumSize = enumMap.enumSize;
        this.values = (Object[]) enumMap.values.clone();
        this.hasMapping = (boolean[]) enumMap.hasMapping.clone();
        this.mappingsCount = enumMap.mappingsCount;
    }

    private void initialization(Class<K> type) {
        this.keyType = type;
        this.keys = Enum.getSharedConstants(this.keyType);
        this.enumSize = this.keys.length;
        this.values = new Object[this.enumSize];
        this.hasMapping = new boolean[this.enumSize];
    }

    private void putAllImpl(Map map) {
        for (java.util.Map.Entry entry : map.entrySet()) {
            putImpl((Enum) entry.getKey(), entry.getValue());
        }
    }

    private V putImpl(K key, V value) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        this.keyType.cast(key);
        int keyOrdinal = key.ordinal();
        if (!this.hasMapping[keyOrdinal]) {
            this.hasMapping[keyOrdinal] = true;
            this.mappingsCount++;
        }
        V oldValue = this.values[keyOrdinal];
        this.values[keyOrdinal] = value;
        return oldValue;
    }
}
