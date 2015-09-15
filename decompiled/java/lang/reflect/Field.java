package java.lang.reflect;

import com.android.dex.DexFormat;
import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.List;
import libcore.icu.DateIntervalFormat;
import libcore.reflect.AnnotationAccess;
import libcore.reflect.GenericSignatureParser;
import libcore.reflect.Types;

public final class Field extends AccessibleObject implements Member {
    public static final Comparator<Field> ORDER_BY_NAME_AND_DECLARING_CLASS;
    private final ArtField artField;

    private native Object get(Object obj, boolean z) throws IllegalAccessException, IllegalArgumentException;

    private native boolean getBoolean(Object obj, boolean z) throws IllegalAccessException, IllegalArgumentException;

    private native byte getByte(Object obj, boolean z) throws IllegalAccessException, IllegalArgumentException;

    private native char getChar(Object obj, boolean z) throws IllegalAccessException, IllegalArgumentException;

    private native double getDouble(Object obj, boolean z) throws IllegalAccessException, IllegalArgumentException;

    private native float getFloat(Object obj, boolean z) throws IllegalAccessException, IllegalArgumentException;

    private native int getInt(Object obj, boolean z) throws IllegalAccessException, IllegalArgumentException;

    private native long getLong(Object obj, boolean z) throws IllegalAccessException, IllegalArgumentException;

    private native short getShort(Object obj, boolean z) throws IllegalAccessException, IllegalArgumentException;

    private native void set(Object obj, Object obj2, boolean z) throws IllegalAccessException, IllegalArgumentException;

    private native void setBoolean(Object obj, boolean z, boolean z2) throws IllegalAccessException, IllegalArgumentException;

    private native void setByte(Object obj, byte b, boolean z) throws IllegalAccessException, IllegalArgumentException;

    private native void setChar(Object obj, char c, boolean z) throws IllegalAccessException, IllegalArgumentException;

    private native void setDouble(Object obj, double d, boolean z) throws IllegalAccessException, IllegalArgumentException;

    private native void setFloat(Object obj, float f, boolean z) throws IllegalAccessException, IllegalArgumentException;

    private native void setInt(Object obj, int i, boolean z) throws IllegalAccessException, IllegalArgumentException;

    private native void setLong(Object obj, long j, boolean z) throws IllegalAccessException, IllegalArgumentException;

    private native void setShort(Object obj, short s, boolean z) throws IllegalAccessException, IllegalArgumentException;

    static {
        ORDER_BY_NAME_AND_DECLARING_CLASS = new Comparator<Field>() {
            public int compare(Field a, Field b) {
                if (a == b) {
                    return 0;
                }
                int comparison = a.getName().compareTo(b.getName());
                if (comparison != 0) {
                    return comparison;
                }
                Class<?> aType = a.getDeclaringClass();
                Class<?> bType = b.getDeclaringClass();
                if (aType == bType) {
                    return 0;
                }
                return aType.getName().compareTo(bType.getName());
            }
        };
    }

    public Field(ArtField artField) {
        if (artField == null) {
            throw new NullPointerException("artField == null");
        }
        this.artField = artField;
    }

    public int getModifiers() {
        return this.artField.getAccessFlags() & DexFormat.MAX_TYPE_IDX;
    }

    public boolean isEnumConstant() {
        return (this.artField.getAccessFlags() & DateIntervalFormat.FORMAT_ABBREV_TIME) != 0;
    }

    public boolean isSynthetic() {
        return (this.artField.getAccessFlags() & Modifier.SYNTHETIC) != 0;
    }

    public String getName() {
        return this.artField.getName();
    }

    public Class<?> getDeclaringClass() {
        return this.artField.getDeclaringClass();
    }

    public Class<?> getType() {
        return this.artField.getType();
    }

    public int getDexFieldIndex() {
        return this.artField.getDexFieldIndex();
    }

    public int getOffset() {
        return this.artField.getOffset();
    }

    public int hashCode() {
        return getDeclaringClass().getName().hashCode() ^ getName().hashCode();
    }

    public boolean equals(Object other) {
        if ((other instanceof Field) && this.artField == ((Field) other).artField) {
            return true;
        }
        return false;
    }

    public String toGenericString() {
        StringBuilder sb = new StringBuilder(80);
        int modifier = getModifiers();
        if (modifier != 0) {
            sb.append(Modifier.toString(modifier)).append(' ');
        }
        Types.appendGenericType(sb, getGenericType());
        sb.append(' ');
        sb.append(getDeclaringClass().getName()).append('.').append(getName());
        return sb.toString();
    }

    public Type getGenericType() {
        String signatureAttribute = AnnotationAccess.getSignature(this);
        Class<?> declaringClass = getDeclaringClass();
        GenericSignatureParser parser = new GenericSignatureParser(declaringClass.getClassLoader());
        parser.parseForField(declaringClass, signatureAttribute);
        Type genericType = parser.fieldType;
        if (genericType == null) {
            return getType();
        }
        return genericType;
    }

    private String getSignature() {
        return Types.getSignature(getType());
    }

    public Annotation[] getDeclaredAnnotations() {
        List<Annotation> result = AnnotationAccess.getDeclaredAnnotations(this);
        return (Annotation[]) result.toArray(new Annotation[result.size()]);
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        if (annotationType != null) {
            return AnnotationAccess.getDeclaredAnnotation(this, annotationType);
        }
        throw new NullPointerException("annotationType == null");
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        if (annotationType != null) {
            return AnnotationAccess.isDeclaredAnnotationPresent(this, annotationType);
        }
        throw new NullPointerException("annotationType == null");
    }

    public Object get(Object object) throws IllegalAccessException, IllegalArgumentException {
        return get(object, isAccessible());
    }

    public boolean getBoolean(Object object) throws IllegalAccessException, IllegalArgumentException {
        return getBoolean(object, isAccessible());
    }

    public byte getByte(Object object) throws IllegalAccessException, IllegalArgumentException {
        return getByte(object, isAccessible());
    }

    public char getChar(Object object) throws IllegalAccessException, IllegalArgumentException {
        return getChar(object, isAccessible());
    }

    public double getDouble(Object object) throws IllegalAccessException, IllegalArgumentException {
        return getDouble(object, isAccessible());
    }

    public float getFloat(Object object) throws IllegalAccessException, IllegalArgumentException {
        return getFloat(object, isAccessible());
    }

    public int getInt(Object object) throws IllegalAccessException, IllegalArgumentException {
        return getInt(object, isAccessible());
    }

    public long getLong(Object object) throws IllegalAccessException, IllegalArgumentException {
        return getLong(object, isAccessible());
    }

    public short getShort(Object object) throws IllegalAccessException, IllegalArgumentException {
        return getShort(object, isAccessible());
    }

    public void set(Object object, Object value) throws IllegalAccessException, IllegalArgumentException {
        set(object, value, isAccessible());
    }

    public void setBoolean(Object object, boolean value) throws IllegalAccessException, IllegalArgumentException {
        setBoolean(object, value, isAccessible());
    }

    public void setByte(Object object, byte value) throws IllegalAccessException, IllegalArgumentException {
        setByte(object, value, isAccessible());
    }

    public void setChar(Object object, char value) throws IllegalAccessException, IllegalArgumentException {
        setChar(object, value, isAccessible());
    }

    public void setDouble(Object object, double value) throws IllegalAccessException, IllegalArgumentException {
        setDouble(object, value, isAccessible());
    }

    public void setFloat(Object object, float value) throws IllegalAccessException, IllegalArgumentException {
        setFloat(object, value, isAccessible());
    }

    public void setInt(Object object, int value) throws IllegalAccessException, IllegalArgumentException {
        setInt(object, value, isAccessible());
    }

    public void setLong(Object object, long value) throws IllegalAccessException, IllegalArgumentException {
        setLong(object, value, isAccessible());
    }

    public void setShort(Object object, short value) throws IllegalAccessException, IllegalArgumentException {
        setShort(object, value, isAccessible());
    }

    public String toString() {
        StringBuilder result = new StringBuilder(Modifier.toString(getModifiers()));
        if (result.length() != 0) {
            result.append(' ');
        }
        Types.appendTypeName(result, getType());
        result.append(' ');
        result.append(getDeclaringClass().getName());
        result.append('.');
        result.append(getName());
        return result.toString();
    }
}
