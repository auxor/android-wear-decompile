package java.lang;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import libcore.util.BasicLruCache;
import libcore.util.EmptyArray;

public abstract class Enum<E extends Enum<E>> implements Serializable, Comparable<E> {
    private static final long serialVersionUID = -4300926546619394005L;
    private static final BasicLruCache<Class<? extends Enum>, Object[]> sharedConstantsCache;
    private final String name;
    private final int ordinal;

    /* renamed from: java.lang.Enum.1 */
    static class AnonymousClass1 extends BasicLruCache<Class<? extends Enum>, Object[]> {
        AnonymousClass1(int x0) {
            super(x0);
        }

        protected Object[] create(Class<? extends Enum> enumType) {
            Object[] objArr = null;
            if (enumType.isEnum()) {
                try {
                    Method method = enumType.getDeclaredMethod("values", EmptyArray.CLASS);
                    method.setAccessible(true);
                    objArr = (Object[]) method.invoke((Object[]) null, new Object[0]);
                } catch (NoSuchMethodException impossible) {
                    throw new AssertionError("impossible", impossible);
                } catch (IllegalAccessException impossible2) {
                    throw new AssertionError("impossible", impossible2);
                } catch (InvocationTargetException impossible3) {
                    throw new AssertionError("impossible", impossible3);
                }
            }
            return objArr;
        }
    }

    static {
        sharedConstantsCache = new AnonymousClass1(64);
    }

    protected Enum(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
    }

    public final String name() {
        return this.name;
    }

    public final int ordinal() {
        return this.ordinal;
    }

    public String toString() {
        return this.name;
    }

    public final boolean equals(Object other) {
        return this == other;
    }

    public final int hashCode() {
        return (this.name == null ? 0 : this.name.hashCode()) + this.ordinal;
    }

    protected final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Enums may not be cloned");
    }

    public final int compareTo(E o) {
        return this.ordinal - o.ordinal;
    }

    public final Class<E> getDeclaringClass() {
        Class<?> myClass = getClass();
        Class<?> mySuperClass = myClass.getSuperclass();
        return Enum.class == mySuperClass ? myClass : mySuperClass;
    }

    public static <T extends Enum<T>> T valueOf(Class<T> enumType, String name) {
        if (enumType == null) {
            throw new NullPointerException("enumType == null");
        } else if (name == null) {
            throw new NullPointerException("name == null");
        } else {
            T[] values = getSharedConstants(enumType);
            if (values == null) {
                throw new IllegalArgumentException(enumType + " is not an enum type");
            }
            for (T value : values) {
                if (name.equals(value.name())) {
                    return value;
                }
            }
            throw new IllegalArgumentException(name + " is not a constant in " + enumType.getName());
        }
    }

    public static <T extends Enum<T>> T[] getSharedConstants(Class<T> enumType) {
        return (Enum[]) sharedConstantsCache.get(enumType);
    }

    protected final void finalize() {
    }
}
