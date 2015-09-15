package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.List;
import libcore.reflect.AnnotationAccess;
import libcore.reflect.Types;

public final class Constructor<T> extends AbstractMethod implements GenericDeclaration, Member {
    private static final Comparator<Method> ORDER_BY_SIGNATURE;

    public native T newInstance(Object[] objArr, boolean z) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;

    static {
        ORDER_BY_SIGNATURE = null;
    }

    public Constructor(ArtMethod artMethod) {
        super(artMethod);
    }

    public Annotation[] getAnnotations() {
        return super.getAnnotations();
    }

    public int getModifiers() {
        return super.getModifiers();
    }

    public boolean isVarArgs() {
        return super.isVarArgs();
    }

    public boolean isSynthetic() {
        return super.isSynthetic();
    }

    public String getName() {
        return getDeclaringClass().getName();
    }

    public Class<T> getDeclaringClass() {
        return super.getDeclaringClass();
    }

    public Class<?>[] getExceptionTypes() {
        return AnnotationAccess.getExceptions(this);
    }

    public Class<?>[] getParameterTypes() {
        return super.getParameterTypes();
    }

    public int hashCode() {
        return getDeclaringClass().getName().hashCode();
    }

    public boolean equals(Object other) {
        return super.equals(other);
    }

    public TypeVariable<Constructor<T>>[] getTypeParameters() {
        return (TypeVariable[]) getMethodOrConstructorGenericInfo().formalTypeParameters.clone();
    }

    public String toGenericString() {
        return super.toGenericString();
    }

    public Type[] getGenericParameterTypes() {
        return super.getGenericParameterTypes();
    }

    public Type[] getGenericExceptionTypes() {
        return super.getGenericExceptionTypes();
    }

    public Annotation[] getDeclaredAnnotations() {
        List<Annotation> result = AnnotationAccess.getDeclaredAnnotations(this);
        return (Annotation[]) result.toArray(new Annotation[result.size()]);
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        if (annotationType != null) {
            return AnnotationAccess.isDeclaredAnnotationPresent(this, annotationType);
        }
        throw new NullPointerException("annotationType == null");
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        if (annotationType != null) {
            return AnnotationAccess.getDeclaredAnnotation(this, annotationType);
        }
        throw new NullPointerException("annotationType == null");
    }

    public Annotation[][] getParameterAnnotations() {
        return this.artMethod.getParameterAnnotations();
    }

    String getSignature() {
        StringBuilder result = new StringBuilder();
        result.append('(');
        for (Class<?> parameterType : getParameterTypes()) {
            result.append(Types.getSignature(parameterType));
        }
        result.append(")V");
        return result.toString();
    }

    public T newInstance(Object... args) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return newInstance(args, isAccessible());
    }

    public String toString() {
        StringBuilder result = new StringBuilder(Modifier.toString(getModifiers()));
        if (result.length() != 0) {
            result.append(' ');
        }
        result.append(getDeclaringClass().getName());
        result.append("(");
        result.append(Types.toString(getParameterTypes()));
        result.append(")");
        Class<?>[] exceptionTypes = getExceptionTypes();
        if (exceptionTypes.length > 0) {
            result.append(" throws ");
            result.append(Types.toString(exceptionTypes));
        }
        return result.toString();
    }
}
