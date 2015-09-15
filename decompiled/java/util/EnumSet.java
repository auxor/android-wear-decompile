package java.util;

import java.io.Serializable;

public abstract class EnumSet<E extends Enum<E>> extends AbstractSet<E> implements Cloneable, Serializable {
    private static final long serialVersionUID = 1009687484059888093L;
    final Class<E> elementClass;

    private static class SerializationProxy<E extends Enum<E>> implements Serializable {
        private static final long serialVersionUID = 362491234563181265L;
        private Class<E> elementType;
        private E[] elements;

        static /* synthetic */ java.lang.Enum[] access$102(java.util.EnumSet.SerializationProxy r1, java.lang.Enum[] r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.EnumSet.SerializationProxy.access$102(java.util.EnumSet$SerializationProxy, java.lang.Enum[]):java.lang.Enum[]
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.EnumSet.SerializationProxy.access$102(java.util.EnumSet$SerializationProxy, java.lang.Enum[]):java.lang.Enum[]
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.EnumSet.SerializationProxy.access$102(java.util.EnumSet$SerializationProxy, java.lang.Enum[]):java.lang.Enum[]");
        }

        static /* synthetic */ java.lang.Class access$202(java.util.EnumSet.SerializationProxy r1, java.lang.Class r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.EnumSet.SerializationProxy.access$202(java.util.EnumSet$SerializationProxy, java.lang.Class):java.lang.Class
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.EnumSet.SerializationProxy.access$202(java.util.EnumSet$SerializationProxy, java.lang.Class):java.lang.Class
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.EnumSet.SerializationProxy.access$202(java.util.EnumSet$SerializationProxy, java.lang.Class):java.lang.Class");
        }

        private java.lang.Object readResolve() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.EnumSet.SerializationProxy.readResolve():java.lang.Object
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.EnumSet.SerializationProxy.readResolve():java.lang.Object
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
            throw new UnsupportedOperationException("Method not decompiled: java.util.EnumSet.SerializationProxy.readResolve():java.lang.Object");
        }

        private SerializationProxy() {
        }
    }

    abstract void complement();

    abstract void setRange(E e, E e2);

    EnumSet(Class<E> cls) {
        this.elementClass = cls;
    }

    public static <E extends Enum<E>> EnumSet<E> noneOf(Class<E> elementType) {
        if (elementType.isEnum()) {
            E[] enums = Enum.getSharedConstants(elementType);
            if (enums.length <= 64) {
                return new MiniEnumSet(elementType, enums);
            }
            return new HugeEnumSet(elementType, enums);
        }
        throw new ClassCastException(elementType.getClass().getName() + " is not an Enum");
    }

    public static <E extends Enum<E>> EnumSet<E> allOf(Class<E> elementType) {
        EnumSet<E> set = noneOf(elementType);
        set.complement();
        return set;
    }

    public static <E extends Enum<E>> EnumSet<E> copyOf(EnumSet<E> s) {
        EnumSet<E> set = noneOf(s.elementClass);
        set.addAll(s);
        return set;
    }

    public static <E extends Enum<E>> EnumSet<E> copyOf(Collection<E> c) {
        if (c instanceof EnumSet) {
            return copyOf((EnumSet) c);
        }
        if (c.isEmpty()) {
            throw new IllegalArgumentException("empty collection");
        }
        Iterator<E> iterator = c.iterator();
        Enum element = (Enum) iterator.next();
        EnumSet<E> set = noneOf(element.getDeclaringClass());
        set.add(element);
        while (iterator.hasNext()) {
            set.add(iterator.next());
        }
        return set;
    }

    public static <E extends Enum<E>> EnumSet<E> complementOf(EnumSet<E> s) {
        EnumSet<E> set = noneOf(s.elementClass);
        set.addAll(s);
        set.complement();
        return set;
    }

    public static <E extends Enum<E>> EnumSet<E> of(E e) {
        EnumSet<E> set = noneOf(e.getDeclaringClass());
        set.add(e);
        return set;
    }

    public static <E extends Enum<E>> EnumSet<E> of(E e1, E e2) {
        EnumSet<E> set = of(e1);
        set.add(e2);
        return set;
    }

    public static <E extends Enum<E>> EnumSet<E> of(E e1, E e2, E e3) {
        EnumSet<E> set = of((Enum) e1, (Enum) e2);
        set.add(e3);
        return set;
    }

    public static <E extends Enum<E>> EnumSet<E> of(E e1, E e2, E e3, E e4) {
        EnumSet<E> set = of(e1, e2, e3);
        set.add(e4);
        return set;
    }

    public static <E extends Enum<E>> EnumSet<E> of(E e1, E e2, E e3, E e4, E e5) {
        EnumSet<E> set = of(e1, e2, e3, e4);
        set.add(e5);
        return set;
    }

    @SafeVarargs
    public static <E extends Enum<E>> EnumSet<E> of(E start, E... others) {
        EnumSet<E> set = of(start);
        for (E e : others) {
            set.add(e);
        }
        return set;
    }

    public static <E extends Enum<E>> EnumSet<E> range(E start, E end) {
        if (start.compareTo((Enum) end) > 0) {
            throw new IllegalArgumentException("start is behind end");
        }
        EnumSet<E> set = noneOf(start.getDeclaringClass());
        set.setRange(start, end);
        return set;
    }

    public EnumSet<E> clone() {
        try {
            return (EnumSet) super.clone();
        } catch (Object e) {
            throw new AssertionError(e);
        }
    }

    boolean isValidType(Class<?> cls) {
        return cls == this.elementClass || cls.getSuperclass() == this.elementClass;
    }

    Object writeReplace() {
        SerializationProxy proxy = new SerializationProxy();
        SerializationProxy.access$102(proxy, (Enum[]) toArray(new Enum[0]));
        SerializationProxy.access$202(proxy, this.elementClass);
        return proxy;
    }
}
