package java.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class WeakHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {
    private static final int DEFAULT_SIZE = 16;
    int elementCount;
    Entry<K, V>[] elementData;
    private final int loadFactor;
    volatile int modCount;
    private final ReferenceQueue<K> referenceQueue;
    private int threshold;

    /* renamed from: java.util.WeakHashMap.1 */
    class AnonymousClass1 extends AbstractSet<java.util.Map.Entry<K, V>> {
        final /* synthetic */ WeakHashMap this$0;

        /* renamed from: java.util.WeakHashMap.1.1 */
        class AnonymousClass1 implements Type<java.util.Map.Entry<K, V>, K, V> {
            final /* synthetic */ AnonymousClass1 this$1;

            AnonymousClass1(java.util.WeakHashMap.AnonymousClass1 r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.WeakHashMap.1.1.<init>(java.util.WeakHashMap$1):void
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
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.WeakHashMap.1.1.<init>(java.util.WeakHashMap$1):void
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
                throw new UnsupportedOperationException("Method not decompiled: java.util.WeakHashMap.1.1.<init>(java.util.WeakHashMap$1):void");
            }

            public /* bridge */ /* synthetic */ java.lang.Object get(java.util.Map.Entry r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.WeakHashMap.1.1.get(java.util.Map$Entry):java.lang.Object
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
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.WeakHashMap.1.1.get(java.util.Map$Entry):java.lang.Object
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
                throw new UnsupportedOperationException("Method not decompiled: java.util.WeakHashMap.1.1.get(java.util.Map$Entry):java.lang.Object");
            }

            public java.util.Map.Entry<K, V> m9get(java.util.Map.Entry<K, V> entry) {
                return entry;
            }
        }

        AnonymousClass1(java.util.WeakHashMap r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.WeakHashMap.1.<init>(java.util.WeakHashMap):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.WeakHashMap.1.<init>(java.util.WeakHashMap):void
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.WeakHashMap.1.<init>(java.util.WeakHashMap):void");
        }

        public void clear() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.WeakHashMap.1.clear():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.WeakHashMap.1.clear():void
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.WeakHashMap.1.clear():void");
        }

        public boolean contains(java.lang.Object r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.WeakHashMap.1.contains(java.lang.Object):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.WeakHashMap.1.contains(java.lang.Object):boolean
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.WeakHashMap.1.contains(java.lang.Object):boolean");
        }

        public java.util.Iterator<java.util.Map.Entry<K, V>> iterator() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.WeakHashMap.1.iterator():java.util.Iterator<java.util.Map$Entry<K, V>>
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.WeakHashMap.1.iterator():java.util.Iterator<java.util.Map$Entry<K, V>>
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.WeakHashMap.1.iterator():java.util.Iterator<java.util.Map$Entry<K, V>>");
        }

        public boolean remove(java.lang.Object r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.WeakHashMap.1.remove(java.lang.Object):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.WeakHashMap.1.remove(java.lang.Object):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: java.util.WeakHashMap.1.remove(java.lang.Object):boolean");
        }

        public int size() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.WeakHashMap.1.size():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.WeakHashMap.1.size():int
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.WeakHashMap.1.size():int");
        }
    }

    /* renamed from: java.util.WeakHashMap.2 */
    class AnonymousClass2 extends AbstractSet<K> {
        final /* synthetic */ WeakHashMap this$0;

        /* renamed from: java.util.WeakHashMap.2.1 */
        class AnonymousClass1 implements Type<K, K, V> {
            final /* synthetic */ AnonymousClass2 this$1;

            AnonymousClass1(AnonymousClass2 anonymousClass2) {
                this.this$1 = anonymousClass2;
            }

            public K get(java.util.Map.Entry<K, V> entry) {
                return entry.getKey();
            }
        }

        AnonymousClass2(WeakHashMap weakHashMap) {
            this.this$0 = weakHashMap;
        }

        public boolean contains(Object object) {
            return this.this$0.containsKey(object);
        }

        public int size() {
            return this.this$0.size();
        }

        public void clear() {
            this.this$0.clear();
        }

        public boolean remove(Object key) {
            if (!this.this$0.containsKey(key)) {
                return false;
            }
            this.this$0.remove(key);
            return true;
        }

        public Iterator<K> iterator() {
            return new HashIterator(this.this$0, new AnonymousClass1(this));
        }
    }

    /* renamed from: java.util.WeakHashMap.3 */
    class AnonymousClass3 extends AbstractCollection<V> {
        final /* synthetic */ WeakHashMap this$0;

        /* renamed from: java.util.WeakHashMap.3.1 */
        class AnonymousClass1 implements Type<V, K, V> {
            final /* synthetic */ AnonymousClass3 this$1;

            AnonymousClass1(java.util.WeakHashMap.AnonymousClass3 r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.WeakHashMap.3.1.<init>(java.util.WeakHashMap$3):void
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
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.WeakHashMap.3.1.<init>(java.util.WeakHashMap$3):void
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
                throw new UnsupportedOperationException("Method not decompiled: java.util.WeakHashMap.3.1.<init>(java.util.WeakHashMap$3):void");
            }

            public V get(java.util.Map.Entry<K, V> entry) {
                return entry.getValue();
            }
        }

        AnonymousClass3(java.util.WeakHashMap r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.WeakHashMap.3.<init>(java.util.WeakHashMap):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.WeakHashMap.3.<init>(java.util.WeakHashMap):void
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.WeakHashMap.3.<init>(java.util.WeakHashMap):void");
        }

        public void clear() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.WeakHashMap.3.clear():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.WeakHashMap.3.clear():void
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.WeakHashMap.3.clear():void");
        }

        public boolean contains(java.lang.Object r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.WeakHashMap.3.contains(java.lang.Object):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.WeakHashMap.3.contains(java.lang.Object):boolean
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.WeakHashMap.3.contains(java.lang.Object):boolean");
        }

        public java.util.Iterator<V> iterator() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.WeakHashMap.3.iterator():java.util.Iterator<V>
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.WeakHashMap.3.iterator():java.util.Iterator<V>
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.WeakHashMap.3.iterator():java.util.Iterator<V>");
        }

        public int size() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.WeakHashMap.3.size():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.WeakHashMap.3.size():int
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.WeakHashMap.3.size():int");
        }
    }

    private static final class Entry<K, V> extends WeakReference<K> implements java.util.Map.Entry<K, V> {
        final int hash;
        boolean isNull;
        Entry<K, V> next;
        V value;

        interface Type<R, K, V> {
            R get(java.util.Map.Entry<K, V> entry);
        }

        Entry(K key, V object, ReferenceQueue<K> queue) {
            boolean z;
            int i = 0;
            super(key, queue);
            if (key == null) {
                z = true;
            } else {
                z = false;
            }
            this.isNull = z;
            if (!this.isNull) {
                i = Collections.secondaryHash((Object) key);
            }
            this.hash = i;
            this.value = object;
        }

        public K getKey() {
            return super.get();
        }

        public V getValue() {
            return this.value;
        }

        public V setValue(V object) {
            V result = this.value;
            this.value = object;
            return result;
        }

        public boolean equals(Object other) {
            if (!(other instanceof java.util.Map.Entry)) {
                return false;
            }
            java.util.Map.Entry<?, ?> entry = (java.util.Map.Entry) other;
            Object key = super.get();
            if (key == null) {
                if (key != entry.getKey()) {
                    return false;
                }
            } else if (!key.equals(entry.getKey())) {
                return false;
            }
            if (this.value == null) {
                if (this.value != entry.getValue()) {
                    return false;
                }
            } else if (!this.value.equals(entry.getValue())) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return (this.value == null ? 0 : this.value.hashCode()) + this.hash;
        }

        public String toString() {
            return super.get() + "=" + this.value;
        }
    }

    class HashIterator<R> implements Iterator<R> {
        private Entry<K, V> currentEntry;
        private int expectedModCount;
        private Entry<K, V> nextEntry;
        private K nextKey;
        private int position;
        final /* synthetic */ WeakHashMap this$0;
        final Type<R, K, V> type;

        HashIterator(WeakHashMap weakHashMap, Type<R, K, V> type) {
            this.this$0 = weakHashMap;
            this.position = 0;
            this.type = type;
            this.expectedModCount = weakHashMap.modCount;
        }

        public boolean hasNext() {
            if (this.nextEntry != null && (this.nextKey != null || this.nextEntry.isNull)) {
                return true;
            }
            while (true) {
                if (this.nextEntry == null) {
                    while (this.position < this.this$0.elementData.length) {
                        Entry[] entryArr = this.this$0.elementData;
                        int i = this.position;
                        this.position = i + 1;
                        Entry entry = entryArr[i];
                        this.nextEntry = entry;
                        if (entry != null) {
                            break;
                        }
                    }
                    if (this.nextEntry == null) {
                        return false;
                    }
                }
                this.nextKey = this.nextEntry.get();
                if (this.nextKey != null) {
                    return true;
                }
                if (this.nextEntry.isNull) {
                    return true;
                }
                this.nextEntry = this.nextEntry.next;
            }
        }

        public R next() {
            if (this.expectedModCount != this.this$0.modCount) {
                throw new ConcurrentModificationException();
            } else if (hasNext()) {
                this.currentEntry = this.nextEntry;
                this.nextEntry = this.currentEntry.next;
                R result = this.type.get(this.currentEntry);
                this.nextKey = null;
                return result;
            } else {
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            if (this.expectedModCount != this.this$0.modCount) {
                throw new ConcurrentModificationException();
            } else if (this.currentEntry != null) {
                this.this$0.removeEntry(this.currentEntry);
                this.currentEntry = null;
                this.expectedModCount++;
            } else {
                throw new IllegalStateException();
            }
        }
    }

    private static <K, V> Entry<K, V>[] newEntryArray(int size) {
        return new Entry[size];
    }

    public WeakHashMap() {
        this((int) DEFAULT_SIZE);
    }

    public WeakHashMap(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity < 0: " + capacity);
        }
        this.elementCount = 0;
        if (capacity == 0) {
            capacity = 1;
        }
        this.elementData = newEntryArray(capacity);
        this.loadFactor = 7500;
        computeMaxSize();
        this.referenceQueue = new ReferenceQueue();
    }

    public WeakHashMap(int capacity, float loadFactor) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity < 0: " + capacity);
        } else if (loadFactor <= 0.0f) {
            throw new IllegalArgumentException("loadFactor <= 0: " + loadFactor);
        } else {
            this.elementCount = 0;
            if (capacity == 0) {
                capacity = 1;
            }
            this.elementData = newEntryArray(capacity);
            this.loadFactor = (int) (10000.0f * loadFactor);
            computeMaxSize();
            this.referenceQueue = new ReferenceQueue();
        }
    }

    public WeakHashMap(Map<? extends K, ? extends V> map) {
        this(map.size() < 6 ? 11 : map.size() * 2);
        putAllImpl(map);
    }

    public void clear() {
        if (this.elementCount > 0) {
            this.elementCount = 0;
            Arrays.fill(this.elementData, null);
            this.modCount++;
            do {
            } while (this.referenceQueue.poll() != null);
        }
    }

    private void computeMaxSize() {
        this.threshold = (int) ((((long) this.elementData.length) * ((long) this.loadFactor)) / 10000);
    }

    public boolean containsKey(Object key) {
        return getEntry(key) != null;
    }

    public Set<java.util.Map.Entry<K, V>> entrySet() {
        poll();
        return new AnonymousClass1(this);
    }

    public Set<K> keySet() {
        poll();
        if (this.keySet == null) {
            this.keySet = new AnonymousClass2(this);
        }
        return this.keySet;
    }

    public Collection<V> values() {
        poll();
        if (this.valuesCollection == null) {
            this.valuesCollection = new AnonymousClass3(this);
        }
        return this.valuesCollection;
    }

    public V get(Object key) {
        poll();
        Entry<K, V> entry;
        if (key != null) {
            for (entry = this.elementData[(Collections.secondaryHash(key) & Integer.MAX_VALUE) % this.elementData.length]; entry != null; entry = entry.next) {
                if (key.equals(entry.get())) {
                    return entry.value;
                }
            }
            return null;
        }
        for (entry = this.elementData[0]; entry != null; entry = entry.next) {
            if (entry.isNull) {
                return entry.value;
            }
        }
        return null;
    }

    Entry<K, V> getEntry(Object key) {
        poll();
        Entry<K, V> entry;
        if (key != null) {
            for (entry = this.elementData[(Collections.secondaryHash(key) & Integer.MAX_VALUE) % this.elementData.length]; entry != null; entry = entry.next) {
                if (key.equals(entry.get())) {
                    return entry;
                }
            }
            return null;
        }
        for (entry = this.elementData[0]; entry != null; entry = entry.next) {
            if (entry.isNull) {
                return entry;
            }
        }
        return null;
    }

    public boolean containsValue(Object value) {
        poll();
        int i;
        Entry<K, V> entry;
        if (value == null) {
            i = this.elementData.length;
            while (true) {
                i--;
                if (i < 0) {
                    break;
                }
                entry = this.elementData[i];
                while (entry != null) {
                    if ((entry.get() != null || entry.isNull) && entry.value == null) {
                        return true;
                    }
                    entry = entry.next;
                }
            }
        } else {
            i = this.elementData.length;
            while (true) {
                i--;
                if (i < 0) {
                    break;
                }
                entry = this.elementData[i];
                while (entry != null) {
                    if ((entry.get() != null || entry.isNull) && value.equals(entry.value)) {
                        return true;
                    }
                    entry = entry.next;
                }
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    void poll() {
        while (true) {
            Entry<K, V> toRemove = (Entry) this.referenceQueue.poll();
            if (toRemove != null) {
                removeEntry(toRemove);
            } else {
                return;
            }
        }
    }

    void removeEntry(Entry<K, V> toRemove) {
        Entry<K, V> last = null;
        int index = (toRemove.hash & Integer.MAX_VALUE) % this.elementData.length;
        for (Entry<K, V> entry = this.elementData[index]; entry != null; entry = entry.next) {
            if (toRemove == entry) {
                this.modCount++;
                if (last == null) {
                    this.elementData[index] = entry.next;
                } else {
                    last.next = entry.next;
                }
                this.elementCount--;
                return;
            }
            last = entry;
        }
    }

    public V put(K key, V value) {
        Entry<K, V> entry;
        poll();
        int index = 0;
        if (key != null) {
            index = (Collections.secondaryHash((Object) key) & Integer.MAX_VALUE) % this.elementData.length;
            entry = this.elementData[index];
            while (entry != null && !key.equals(entry.get())) {
                entry = entry.next;
            }
        } else {
            entry = this.elementData[0];
            while (entry != null && !entry.isNull) {
                entry = entry.next;
            }
        }
        if (entry == null) {
            this.modCount++;
            int i = this.elementCount + 1;
            this.elementCount = i;
            if (i > this.threshold) {
                rehash();
                index = key == null ? 0 : (Collections.secondaryHash((Object) key) & Integer.MAX_VALUE) % this.elementData.length;
            }
            entry = new Entry(key, value, this.referenceQueue);
            entry.next = this.elementData[index];
            this.elementData[index] = entry;
            return null;
        }
        V result = entry.value;
        entry.value = value;
        return result;
    }

    private void rehash() {
        int length = this.elementData.length * 2;
        if (length == 0) {
            length = 1;
        }
        Entry<K, V>[] newData = newEntryArray(length);
        for (Entry<K, V> entry : this.elementData) {
            Entry<K, V> entry2;
            while (entry2 != null) {
                int index = entry2.isNull ? 0 : (entry2.hash & Integer.MAX_VALUE) % length;
                Entry<K, V> next = entry2.next;
                entry2.next = newData[index];
                newData[index] = entry2;
                entry2 = next;
            }
        }
        this.elementData = newData;
        computeMaxSize();
    }

    public void putAll(Map<? extends K, ? extends V> map) {
        putAllImpl(map);
    }

    public V remove(Object key) {
        Entry<K, V> entry;
        poll();
        int index = 0;
        Entry<K, V> last = null;
        if (key != null) {
            index = (Collections.secondaryHash(key) & Integer.MAX_VALUE) % this.elementData.length;
            entry = this.elementData[index];
            while (entry != null && !key.equals(entry.get())) {
                last = entry;
                entry = entry.next;
            }
        } else {
            entry = this.elementData[0];
            while (entry != null && !entry.isNull) {
                last = entry;
                entry = entry.next;
            }
        }
        if (entry == null) {
            return null;
        }
        this.modCount++;
        if (last == null) {
            this.elementData[index] = entry.next;
        } else {
            last.next = entry.next;
        }
        this.elementCount--;
        return entry.value;
    }

    public int size() {
        poll();
        return this.elementCount;
    }

    private void putAllImpl(Map<? extends K, ? extends V> map) {
        if (map.entrySet() != null) {
            super.putAll(map);
        }
    }
}
