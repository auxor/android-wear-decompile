package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.List;
import libcore.reflect.AnnotationAccess;
import libcore.reflect.Types;

public final class Method extends AbstractMethod implements GenericDeclaration, Member {
    public static final Comparator<Method> ORDER_BY_SIGNATURE;

    private native Class<?>[] getExceptionTypesNative();

    private native Object invoke(Object obj, Object[] objArr, boolean z) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;

    static {
        ORDER_BY_SIGNATURE = new Comparator<Method>() {
            public int compare(Method a, Method b) {
                if (a == b) {
                    return 0;
                }
                int comparison = a.getName().compareTo(b.getName());
                if (comparison != 0) {
                    return comparison;
                }
                comparison = a.artMethod.findOverriddenMethodIfProxy().compareParameters(b.getParameterTypes());
                if (comparison != 0) {
                    return comparison;
                }
                Class<?> aReturnType = a.getReturnType();
                Class<?> bReturnType = b.getReturnType();
                if (aReturnType == bReturnType) {
                    return 0;
                }
                return aReturnType.getName().compareTo(bReturnType.getName());
            }
        };
    }

    public Method(ArtMethod artMethod) {
        super(artMethod);
    }

    ArtMethod getArtMethod() {
        return this.artMethod;
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

    public boolean isBridge() {
        return super.isBridge();
    }

    public boolean isSynthetic() {
        return super.isSynthetic();
    }

    public String getName() {
        return ArtMethod.getMethodName(this.artMethod);
    }

    public Class<?> getDeclaringClass() {
        return super.getDeclaringClass();
    }

    public Class<?>[] getExceptionTypes() {
        if (getDeclaringClass().isProxy()) {
            return getExceptionTypesNative();
        }
        return AnnotationAccess.getExceptions(this);
    }

    public Class<?>[] getParameterTypes() {
        return this.artMethod.findOverriddenMethodIfProxy().getParameterTypes();
    }

    public Class<?> getReturnType() {
        return this.artMethod.findOverriddenMethodIfProxy().getReturnType();
    }

    public int hashCode() {
        return getDeclaringClass().getName().hashCode() ^ getName().hashCode();
    }

    public boolean equals(Object other) {
        return super.equals(other);
    }

    boolean equalNameAndParameters(Method m) {
        return getName().equals(m.getName()) && ArtMethod.equalMethodParameters(this.artMethod, m.getParameterTypes());
    }

    public String toGenericString() {
        return super.toGenericString();
    }

    public TypeVariable<Method>[] getTypeParameters() {
        return (TypeVariable[]) getMethodOrConstructorGenericInfo().formalTypeParameters.clone();
    }

    public Type[] getGenericParameterTypes() {
        return Types.getTypeArray(getMethodOrConstructorGenericInfo().genericParameterTypes, false);
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        if (annotationType != null) {
            return AnnotationAccess.isDeclaredAnnotationPresent(this, annotationType);
        }
        throw new NullPointerException("annotationType == null");
    }

    public Type[] getGenericExceptionTypes() {
        return Types.getTypeArray(getMethodOrConstructorGenericInfo().genericExceptionTypes, false);
    }

    public Type getGenericReturnType() {
        return Types.getType(getMethodOrConstructorGenericInfo().genericReturnType);
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

    public Annotation[][] getParameterAnnotations() {
        return this.artMethod.findOverriddenMethodIfProxy().getParameterAnnotations();
    }

    public Object getDefaultValue() {
        return AnnotationAccess.getDefaultValue(this);
    }

    public Object invoke(Object receiver, Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return invoke(receiver, args, isAccessible());
    }

    public String toString() {
        StringBuilder result = new StringBuilder(Modifier.toString(getModifiers()));
        if (result.length() != 0) {
            result.append(' ');
        }
        result.append(getReturnType().getName());
        result.append(' ');
        result.append(getDeclaringClass().getName());
        result.append('.');
        result.append(getName());
        result.append("(");
        result.append(Types.toString(getParameterTypes()));
        result.append(")");
        Class<?>[] exceptionTypes = getExceptionTypes();
        if (exceptionTypes.length != 0) {
            result.append(" throws ");
            result.append(Types.toString(exceptionTypes));
        }
        return result.toString();
    }

    String getSignature() {
        StringBuilder result = new StringBuilder();
        result.append('(');
        for (Class<?> parameterType : getParameterTypes()) {
            result.append(Types.getSignature(parameterType));
        }
        result.append(')');
        result.append(Types.getSignature(getReturnType()));
        return result.toString();
    }
}
