package libcore.util;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public final class EmptyArray {
    public static final boolean[] BOOLEAN;
    public static final byte[] BYTE;
    public static final char[] CHAR;
    public static final Class<?>[] CLASS;
    public static final double[] DOUBLE;
    public static final float[] FLOAT;
    public static final int[] INT;
    public static final long[] LONG;
    public static final Object[] OBJECT;
    public static final StackTraceElement[] STACK_TRACE_ELEMENT;
    public static final String[] STRING;
    public static final Throwable[] THROWABLE;
    public static final Type[] TYPE;
    public static final TypeVariable[] TYPE_VARIABLE;

    private EmptyArray() {
    }

    static {
        BOOLEAN = new boolean[0];
        BYTE = new byte[0];
        CHAR = new char[0];
        DOUBLE = new double[0];
        FLOAT = new float[0];
        INT = new int[0];
        LONG = new long[0];
        CLASS = new Class[0];
        OBJECT = new Object[0];
        STRING = new String[0];
        THROWABLE = new Throwable[0];
        STACK_TRACE_ELEMENT = new StackTraceElement[0];
        TYPE = new Type[0];
        TYPE_VARIABLE = new TypeVariable[0];
    }
}
