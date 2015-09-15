package java.lang.reflect;

import java.lang.annotation.Annotation;

public class AccessibleObject implements AnnotatedElement {
    private boolean flag;

    protected AccessibleObject() {
        this.flag = false;
    }

    public boolean isAccessible() {
        return this.flag;
    }

    public void setAccessible(boolean flag) {
        try {
            if (equals(Class.class.getDeclaredConstructor(new Class[0]))) {
                throw new SecurityException("Can't make class constructor accessible");
            }
            this.flag = flag;
        } catch (NoSuchMethodException e) {
            throw new AssertionError((Object) "Couldn't find class constructor");
        }
    }

    public static void setAccessible(AccessibleObject[] objects, boolean flag) {
        for (AccessibleObject object : objects) {
            object.flag = flag;
        }
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> cls) {
        throw new UnsupportedOperationException();
    }

    public Annotation[] getDeclaredAnnotations() {
        throw new UnsupportedOperationException();
    }

    public Annotation[] getAnnotations() {
        return getDeclaredAnnotations();
    }

    public <T extends Annotation> T getAnnotation(Class<T> cls) {
        throw new UnsupportedOperationException();
    }
}
