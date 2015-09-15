package java.lang;

import com.android.dex.Dex;
import com.android.dex.DexFormat;
import dalvik.system.VMStack;
import java.awt.font.NumericShaper;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ArtField;
import java.lang.reflect.ArtMethod;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.datatype.DatatypeConstants;
import libcore.icu.DateIntervalFormat;
import libcore.reflect.AnnotationAccess;
import libcore.reflect.GenericSignatureParser;
import libcore.reflect.InternalNames;
import libcore.reflect.Types;
import libcore.util.BasicLruCache;
import libcore.util.CollectionUtils;
import libcore.util.EmptyArray;
import libcore.util.SneakyThrow;
import org.w3c.dom.traversal.NodeFilter;
import org.xmlpull.v1.XmlPullParser;

public final class Class<T> implements Serializable, AnnotatedElement, GenericDeclaration, Type {
    private static final long serialVersionUID = 3206093459760846163L;
    private transient int accessFlags;
    private transient ClassLoader classLoader;
    private transient int classSize;
    private transient int clinitThreadId;
    private transient Class<?> componentType;
    private transient DexCache dexCache;
    private transient String[] dexCacheStrings;
    private transient int dexClassDefIndex;
    private volatile transient int dexTypeIndex;
    private transient ArtMethod[] directMethods;
    private transient ArtField[] iFields;
    private transient Object[] ifTable;
    private transient String name;
    private transient int numReferenceInstanceFields;
    private transient int numReferenceStaticFields;
    private transient int objectSize;
    private transient int primitiveType;
    private transient int referenceInstanceOffsets;
    private transient int referenceStaticOffsets;
    private transient ArtField[] sFields;
    private transient int status;
    private transient Class<? super T> superClass;
    private transient Class<?> verifyErrorClass;
    private transient ArtMethod[] virtualMethods;
    private transient ArtMethod[] vtable;

    private static class Caches {
        private static final BasicLruCache<Class, Type[]> genericInterfaces;

        private Caches() {
        }

        static {
            genericInterfaces = new BasicLruCache(8);
        }
    }

    static native Class<?> classForName(String str, boolean z, ClassLoader classLoader) throws ClassNotFoundException;

    private native String getNameNative();

    private native Class<?>[] getProxyInterfaces();

    private Class() {
    }

    public static Class<?> forName(String className) throws ClassNotFoundException {
        return forName(className, true, VMStack.getCallingClassLoader());
    }

    public static Class<?> forName(String className, boolean shouldInitialize, ClassLoader classLoader) throws ClassNotFoundException {
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        try {
            return classForName(className, shouldInitialize, classLoader);
        } catch (ClassNotFoundException e) {
            Throwable cause = e.getCause();
            if (cause instanceof LinkageError) {
                throw ((LinkageError) cause);
            }
            throw e;
        }
    }

    public Class<?>[] getClasses() {
        List<Class<?>> result = new ArrayList();
        for (Class<?> c = this; c != null; c = c.superClass) {
            for (Class<?> member : c.getDeclaredClasses()) {
                if (Modifier.isPublic(member.getModifiers())) {
                    result.add(member);
                }
            }
        }
        return (Class[]) result.toArray(new Class[result.size()]);
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return AnnotationAccess.getAnnotation(this, (Class) annotationType);
    }

    public Annotation[] getAnnotations() {
        return AnnotationAccess.getAnnotations(this);
    }

    public String getCanonicalName() {
        if (isLocalClass() || isAnonymousClass()) {
            return null;
        }
        String name;
        if (isArray()) {
            name = getComponentType().getCanonicalName();
            if (name != null) {
                return name + "[]";
            }
            return null;
        } else if (!isMemberClass()) {
            return getName();
        } else {
            name = getDeclaringClass().getCanonicalName();
            if (name != null) {
                return name + "." + getSimpleName();
            }
            return null;
        }
    }

    public ClassLoader getClassLoader() {
        if (isPrimitive()) {
            return null;
        }
        ClassLoader loader = getClassLoaderImpl();
        if (loader == null) {
            return BootClassLoader.getInstance();
        }
        return loader;
    }

    ClassLoader getClassLoaderImpl() {
        ClassLoader loader = this.classLoader;
        return loader == null ? BootClassLoader.getInstance() : loader;
    }

    public Class<?> getComponentType() {
        return this.componentType;
    }

    public Dex getDex() {
        if (this.dexCache == null) {
            return null;
        }
        return this.dexCache.getDex();
    }

    public String getDexCacheString(Dex dex, int dexStringIndex) {
        String s = this.dexCacheStrings[dexStringIndex];
        if (s != null) {
            return s;
        }
        s = ((String) dex.strings().get(dexStringIndex)).intern();
        this.dexCacheStrings[dexStringIndex] = s;
        return s;
    }

    public Class<?> getDexCacheType(Dex dex, int dexTypeIndex) {
        Class<?>[] dexCacheResolvedTypes = this.dexCache.resolvedTypes;
        Class<?> resolvedType = dexCacheResolvedTypes[dexTypeIndex];
        if (resolvedType != null) {
            return resolvedType;
        }
        resolvedType = InternalNames.getClass(getClassLoader(), getDexCacheString(dex, ((Integer) dex.typeIds().get(dexTypeIndex)).intValue()));
        dexCacheResolvedTypes[dexTypeIndex] = resolvedType;
        return resolvedType;
    }

    public Constructor<T> getConstructor(Class<?>... parameterTypes) throws NoSuchMethodException {
        return getConstructor(parameterTypes, true);
    }

    public Constructor<T> getDeclaredConstructor(Class<?>... parameterTypes) throws NoSuchMethodException {
        return getConstructor(parameterTypes, false);
    }

    private Constructor<T> getConstructor(Class<?>[] parameterTypes, boolean publicOnly) throws NoSuchMethodException {
        Object[] parameterTypes2;
        if (parameterTypes == null) {
            parameterTypes2 = EmptyArray.CLASS;
        }
        for (Class<?> c : parameterTypes2) {
            if (c == null) {
                throw new NoSuchMethodException("parameter type is null");
            }
        }
        Constructor<T> result = getDeclaredConstructorInternal(parameterTypes2);
        if (result != null && (!publicOnly || Modifier.isPublic(result.getAccessFlags()))) {
            return result;
        }
        throw new NoSuchMethodException("<init> " + Arrays.toString(parameterTypes2));
    }

    private Constructor<T> getDeclaredConstructorInternal(Class<?>[] args) {
        if (this.directMethods != null) {
            for (ArtMethod m : this.directMethods) {
                int modifiers = m.getAccessFlags();
                if (!Modifier.isStatic(modifiers) && Modifier.isConstructor(modifiers) && ArtMethod.equalConstructorParameters(m, args)) {
                    return new Constructor(m);
                }
            }
        }
        return null;
    }

    public Constructor<?>[] getConstructors() {
        ArrayList<Constructor<T>> constructors = new ArrayList();
        getDeclaredConstructors(true, constructors);
        return (Constructor[]) constructors.toArray(new Constructor[constructors.size()]);
    }

    public Constructor<?>[] getDeclaredConstructors() {
        ArrayList<Constructor<T>> constructors = new ArrayList();
        getDeclaredConstructors(false, constructors);
        return (Constructor[]) constructors.toArray(new Constructor[constructors.size()]);
    }

    private void getDeclaredConstructors(boolean publicOnly, List<Constructor<T>> constructors) {
        if (this.directMethods != null) {
            for (ArtMethod m : this.directMethods) {
                int modifiers = m.getAccessFlags();
                if ((!publicOnly || Modifier.isPublic(modifiers)) && !Modifier.isStatic(modifiers) && Modifier.isConstructor(modifiers)) {
                    constructors.add(new Constructor(m));
                }
            }
        }
    }

    public Method getDeclaredMethod(String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        return getMethod(name, parameterTypes, false);
    }

    public Method getMethod(String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        return getMethod(name, parameterTypes, true);
    }

    private Method getMethod(String name, Class<?>[] parameterTypes, boolean recursivePublicMethods) throws NoSuchMethodException {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        Object[] parameterTypes2;
        if (parameterTypes == null) {
            parameterTypes2 = EmptyArray.CLASS;
        }
        for (Class<?> c : parameterTypes2) {
            if (c == null) {
                throw new NoSuchMethodException("parameter type is null");
            }
        }
        Method result = recursivePublicMethods ? getPublicMethodRecursive(name, parameterTypes2) : getDeclaredMethodInternal(name, parameterTypes2);
        if (result != null && (!recursivePublicMethods || Modifier.isPublic(result.getAccessFlags()))) {
            return result;
        }
        throw new NoSuchMethodException(name + " " + Arrays.toString(parameterTypes2));
    }

    private Method getPublicMethodRecursive(String name, Class<?>[] parameterTypes) {
        for (Class<?> c = this; c != null; c = c.getSuperclass()) {
            Method result = c.getDeclaredMethodInternal(name, parameterTypes);
            if (result != null && Modifier.isPublic(result.getAccessFlags())) {
                return result;
            }
        }
        Object[] iftable = this.ifTable;
        if (iftable != null) {
            for (int i = 0; i < iftable.length; i += 2) {
                result = iftable[i].getPublicMethodRecursive(name, parameterTypes);
                if (result != null && Modifier.isPublic(result.getAccessFlags())) {
                    return result;
                }
            }
        }
        return null;
    }

    private Method getDeclaredMethodInternal(String name, Class<?>[] args) {
        int modifiers;
        ArtMethod artMethodResult = null;
        if (this.virtualMethods != null) {
            for (ArtMethod m : this.virtualMethods) {
                if (name.equals(ArtMethod.getMethodName(m)) && ArtMethod.equalMethodParameters(m, args)) {
                    modifiers = m.getAccessFlags();
                    if ((modifiers & 2101248) == 0) {
                        return new Method(m);
                    }
                    if ((Modifier.MIRANDA & modifiers) == 0) {
                        artMethodResult = m;
                    }
                }
            }
        }
        if (artMethodResult == null && this.directMethods != null) {
            for (ArtMethod m2 : this.directMethods) {
                modifiers = m2.getAccessFlags();
                if (!Modifier.isConstructor(modifiers) && name.equals(ArtMethod.getMethodName(m2)) && ArtMethod.equalMethodParameters(m2, args)) {
                    if ((modifiers & 2101248) == 0) {
                        return new Method(m2);
                    }
                    artMethodResult = m2;
                }
            }
        }
        if (artMethodResult == null) {
            return null;
        }
        return new Method(artMethodResult);
    }

    public Method[] getDeclaredMethods() {
        ArrayList<Method> methods = new ArrayList((this.virtualMethods == null ? 0 : this.virtualMethods.length) + (this.directMethods == null ? 0 : this.directMethods.length));
        getDeclaredMethodsUnchecked(false, methods);
        Method[] result = (Method[]) methods.toArray(new Method[methods.size()]);
        for (Method m : result) {
            m.getReturnType();
            m.getParameterTypes();
        }
        return result;
    }

    public void getDeclaredMethodsUnchecked(boolean publicOnly, List<Method> methods) {
        if (this.virtualMethods != null) {
            for (ArtMethod m : this.virtualMethods) {
                int modifiers = m.getAccessFlags();
                if ((!publicOnly || Modifier.isPublic(modifiers)) && (Modifier.MIRANDA & modifiers) == 0) {
                    methods.add(new Method(m));
                }
            }
        }
        if (this.directMethods != null) {
            for (ArtMethod m2 : this.directMethods) {
                modifiers = m2.getAccessFlags();
                if ((!publicOnly || Modifier.isPublic(modifiers)) && !Modifier.isConstructor(modifiers)) {
                    methods.add(new Method(m2));
                }
            }
        }
    }

    public Method[] getMethods() {
        List<Method> methods = new ArrayList();
        getPublicMethodsInternal(methods);
        CollectionUtils.removeDuplicates(methods, Method.ORDER_BY_SIGNATURE);
        return (Method[]) methods.toArray(new Method[methods.size()]);
    }

    private void getPublicMethodsInternal(List<Method> result) {
        getDeclaredMethodsUnchecked(true, result);
        if (!isInterface()) {
            for (Class<?> c = this.superClass; c != null; c = c.superClass) {
                c.getDeclaredMethodsUnchecked(true, result);
            }
        }
        Object[] iftable = this.ifTable;
        if (iftable != null) {
            for (int i = 0; i < iftable.length; i += 2) {
                iftable[i].getDeclaredMethodsUnchecked(true, result);
            }
        }
    }

    public Annotation[] getDeclaredAnnotations() {
        List<Annotation> result = AnnotationAccess.getDeclaredAnnotations(this);
        return (Annotation[]) result.toArray(new Annotation[result.size()]);
    }

    public Class<?>[] getDeclaredClasses() {
        return AnnotationAccess.getMemberClasses(this);
    }

    public Field getDeclaredField(String name) throws NoSuchFieldException {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        Field result = getDeclaredFieldInternal(name);
        if (result == null) {
            throw new NoSuchFieldException(name);
        }
        result.getType();
        return result;
    }

    public Field[] getDeclaredFields() {
        ArrayList<Field> fields = new ArrayList((this.sFields == null ? 0 : this.sFields.length) + (this.iFields == null ? 0 : this.iFields.length));
        getDeclaredFieldsUnchecked(false, fields);
        Field[] result = (Field[]) fields.toArray(new Field[fields.size()]);
        for (Field f : result) {
            f.getType();
        }
        return result;
    }

    public void getDeclaredFieldsUnchecked(boolean publicOnly, List<Field> fields) {
        if (this.iFields != null) {
            for (ArtField f : this.iFields) {
                if (!publicOnly || Modifier.isPublic(f.getAccessFlags())) {
                    fields.add(new Field(f));
                }
            }
        }
        if (this.sFields != null) {
            for (ArtField f2 : this.sFields) {
                if (!publicOnly || Modifier.isPublic(f2.getAccessFlags())) {
                    fields.add(new Field(f2));
                }
            }
        }
    }

    private Field getDeclaredFieldInternal(String name) {
        ArtField matched;
        if (this.iFields != null) {
            matched = findByName(name, this.iFields);
            if (matched != null) {
                return new Field(matched);
            }
        }
        if (this.sFields != null) {
            matched = findByName(name, this.sFields);
            if (matched != null) {
                return new Field(matched);
            }
        }
        return null;
    }

    private static ArtField findByName(String name, ArtField[] fields) {
        int low = 0;
        int high = fields.length - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            ArtField f = fields[mid];
            int result = f.getName().compareTo(name);
            if (result < 0) {
                low = mid + 1;
            } else if (result == 0) {
                return f;
            } else {
                high = mid - 1;
            }
        }
        return null;
    }

    public Class<?> getDeclaringClass() {
        if (AnnotationAccess.isAnonymousClass(this)) {
            return null;
        }
        return AnnotationAccess.getEnclosingClass(this);
    }

    public Class<?> getEnclosingClass() {
        Class<?> declaringClass = getDeclaringClass();
        if (declaringClass != null) {
            return declaringClass;
        }
        AccessibleObject member = AnnotationAccess.getEnclosingMethodOrConstructor(this);
        if (member != null) {
            return ((Member) member).getDeclaringClass();
        }
        return AnnotationAccess.getEnclosingClass(this);
    }

    public Constructor<?> getEnclosingConstructor() {
        if (classNameImpliesTopLevel()) {
            return null;
        }
        AccessibleObject result = AnnotationAccess.getEnclosingMethodOrConstructor(this);
        return result instanceof Constructor ? (Constructor) result : null;
    }

    public Method getEnclosingMethod() {
        if (classNameImpliesTopLevel()) {
            return null;
        }
        AccessibleObject result = AnnotationAccess.getEnclosingMethodOrConstructor(this);
        return result instanceof Method ? (Method) result : null;
    }

    private boolean classNameImpliesTopLevel() {
        return !getName().contains("$");
    }

    public T[] getEnumConstants() {
        if (isEnum()) {
            return (Object[]) Enum.getSharedConstants(this).clone();
        }
        return null;
    }

    public Field getField(String name) throws NoSuchFieldException {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        Field result = getPublicFieldRecursive(name);
        if (result == null) {
            throw new NoSuchFieldException(name);
        }
        result.getType();
        return result;
    }

    private Field getPublicFieldRecursive(String name) {
        for (Class<?> c = this; c != null; c = c.superClass) {
            Field result = c.getDeclaredFieldInternal(name);
            if (result != null && (result.getModifiers() & 1) != 0) {
                return result;
            }
        }
        if (this.ifTable != null) {
            for (int i = 0; i < this.ifTable.length; i += 2) {
                result = this.ifTable[i].getPublicFieldRecursive(name);
                if (result != null && (result.getModifiers() & 1) != 0) {
                    return result;
                }
            }
        }
        return null;
    }

    public Field[] getFields() {
        List<Field> fields = new ArrayList();
        getPublicFieldsRecursive(fields);
        Field[] result = (Field[]) fields.toArray(new Field[fields.size()]);
        for (Field f : result) {
            f.getType();
        }
        return result;
    }

    private void getPublicFieldsRecursive(List<Field> result) {
        for (Class<?> c = this; c != null; c = c.superClass) {
            c.getDeclaredFieldsUnchecked(true, result);
        }
        Object[] iftable = this.ifTable;
        if (iftable != null) {
            for (int i = 0; i < iftable.length; i += 2) {
                iftable[i].getDeclaredFieldsUnchecked(true, result);
            }
        }
    }

    public Type[] getGenericInterfaces() {
        Type[] result;
        synchronized (Caches.genericInterfaces) {
            result = (Type[]) Caches.genericInterfaces.get(this);
            if (result == null) {
                String annotationSignature = AnnotationAccess.getSignature(this);
                if (annotationSignature == null) {
                    result = getInterfaces();
                } else {
                    GenericSignatureParser parser = new GenericSignatureParser(getClassLoader());
                    parser.parseForClass(this, annotationSignature);
                    result = Types.getTypeArray(parser.interfaceTypes, false);
                }
                Caches.genericInterfaces.put(this, result);
            }
        }
        return result.length == 0 ? result : (Type[]) result.clone();
    }

    public Type getGenericSuperclass() {
        Type genericSuperclass = getSuperclass();
        if (genericSuperclass == null) {
            return null;
        }
        String annotationSignature = AnnotationAccess.getSignature(this);
        if (annotationSignature != null) {
            GenericSignatureParser parser = new GenericSignatureParser(getClassLoader());
            parser.parseForClass(this, annotationSignature);
            genericSuperclass = parser.superclassType;
        }
        return Types.getType(genericSuperclass);
    }

    public Class<?>[] getInterfaces() {
        if (isArray()) {
            return new Class[]{Cloneable.class, Serializable.class};
        } else if (isProxy()) {
            return getProxyInterfaces();
        } else {
            Dex dex = getDex();
            if (dex == null) {
                return EmptyArray.CLASS;
            }
            short[] interfaces = dex.interfaceTypeIndicesFromClassDefIndex(this.dexClassDefIndex);
            Class<?>[] result = new Class[interfaces.length];
            for (int i = 0; i < interfaces.length; i++) {
                result[i] = getDexCacheType(dex, interfaces[i]);
            }
            return result;
        }
    }

    public int getModifiers() {
        if (!isArray()) {
            return AnnotationAccess.getInnerClassFlags(this, this.accessFlags & DexFormat.MAX_TYPE_IDX) & DexFormat.MAX_TYPE_IDX;
        }
        int componentModifiers = getComponentType().getModifiers();
        if ((componentModifiers & NodeFilter.SHOW_DOCUMENT_TYPE) != 0) {
            componentModifiers &= -521;
        }
        return componentModifiers | 1040;
    }

    public String getName() {
        String str = this.name;
        if (str != null) {
            return str;
        }
        str = getNameNative();
        this.name = str;
        return str;
    }

    public String getSimpleName() {
        if (isArray()) {
            return getComponentType().getSimpleName() + "[]";
        }
        if (isAnonymousClass()) {
            return XmlPullParser.NO_NAMESPACE;
        }
        if (isMemberClass() || isLocalClass()) {
            return getInnerClassName();
        }
        String name = getName();
        int dot = name.lastIndexOf(46);
        if (dot != -1) {
            return name.substring(dot + 1);
        }
        return name;
    }

    private String getInnerClassName() {
        return AnnotationAccess.getInnerClassName(this);
    }

    public ProtectionDomain getProtectionDomain() {
        return null;
    }

    public URL getResource(String resourceName) {
        if (resourceName.startsWith("/")) {
            resourceName = resourceName.substring(1);
        } else {
            String pkg = getName();
            int dot = pkg.lastIndexOf(46);
            if (dot != -1) {
                pkg = pkg.substring(0, dot).replace('.', '/');
            } else {
                pkg = XmlPullParser.NO_NAMESPACE;
            }
            resourceName = pkg + "/" + resourceName;
        }
        ClassLoader loader = getClassLoader();
        if (loader != null) {
            return loader.getResource(resourceName);
        }
        return ClassLoader.getSystemResource(resourceName);
    }

    public InputStream getResourceAsStream(String resourceName) {
        if (resourceName.startsWith("/")) {
            resourceName = resourceName.substring(1);
        } else {
            String pkg = getName();
            int dot = pkg.lastIndexOf(46);
            if (dot != -1) {
                pkg = pkg.substring(0, dot).replace('.', '/');
            } else {
                pkg = XmlPullParser.NO_NAMESPACE;
            }
            resourceName = pkg + "/" + resourceName;
        }
        ClassLoader loader = getClassLoader();
        if (loader != null) {
            return loader.getResourceAsStream(resourceName);
        }
        return ClassLoader.getSystemResourceAsStream(resourceName);
    }

    public Object[] getSigners() {
        return null;
    }

    public Class<? super T> getSuperclass() {
        if (isInterface()) {
            return null;
        }
        return this.superClass;
    }

    public synchronized TypeVariable<Class<T>>[] getTypeParameters() {
        TypeVariable<Class<T>>[] typeVariableArr;
        String annotationSignature = AnnotationAccess.getSignature(this);
        if (annotationSignature == null) {
            typeVariableArr = EmptyArray.TYPE_VARIABLE;
        } else {
            GenericSignatureParser parser = new GenericSignatureParser(getClassLoader());
            parser.parseForClass(this, annotationSignature);
            typeVariableArr = parser.formalTypeParameters;
        }
        return typeVariableArr;
    }

    public boolean isAnnotation() {
        return (this.accessFlags & DateIntervalFormat.FORMAT_UTC) != 0;
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return AnnotationAccess.isAnnotationPresent(this, annotationType);
    }

    public boolean isAnonymousClass() {
        return AnnotationAccess.isAnonymousClass(this);
    }

    public boolean isArray() {
        return getComponentType() != null;
    }

    public boolean isProxy() {
        return (this.accessFlags & NumericShaper.MONGOLIAN) != 0;
    }

    public boolean isAssignableFrom(Class<?> c) {
        if (this == c) {
            return true;
        }
        if (this == Object.class) {
            if (c.isPrimitive()) {
                return false;
            }
            return true;
        } else if (isArray()) {
            if (c.isArray() && this.componentType.isAssignableFrom(c.componentType)) {
                return true;
            }
            return false;
        } else if (isInterface()) {
            Object[] iftable = c.ifTable;
            if (iftable != null) {
                for (int i = 0; i < iftable.length; i += 2) {
                    if (iftable[i] == this) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            if (!c.isInterface()) {
                for (c = c.superClass; c != null; c = c.superClass) {
                    if (c == this) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public boolean isEnum() {
        return getSuperclass() == Enum.class && (this.accessFlags & DateIntervalFormat.FORMAT_ABBREV_TIME) != 0;
    }

    public boolean isInstance(Object object) {
        if (object == null) {
            return false;
        }
        return isAssignableFrom(object.getClass());
    }

    public boolean isInterface() {
        return (this.accessFlags & NodeFilter.SHOW_DOCUMENT_TYPE) != 0;
    }

    public boolean isLocalClass() {
        return (classNameImpliesTopLevel() || AnnotationAccess.getEnclosingMethodOrConstructor(this) == null || isAnonymousClass()) ? false : true;
    }

    public boolean isMemberClass() {
        return getDeclaringClass() != null;
    }

    public boolean isPrimitive() {
        return this.primitiveType != 0;
    }

    public boolean isSynthetic() {
        return (this.accessFlags & Modifier.SYNTHETIC) != 0;
    }

    public boolean isFinalizable() {
        return (this.accessFlags & DatatypeConstants.FIELD_UNDEFINED) != 0;
    }

    public T newInstance() throws InstantiationException, IllegalAccessException {
        T t = null;
        if (isPrimitive() || isInterface() || isArray() || Modifier.isAbstract(this.accessFlags)) {
            throw new InstantiationException(this + " cannot be instantiated");
        }
        Object caller = VMStack.getStackClass1();
        if (caller.canAccess(this)) {
            try {
                Object init = getDeclaredConstructor(new Class[0]);
                if (caller.canAccessMember(this, init.getAccessFlags())) {
                    try {
                        t = init.newInstance(null, init.isAccessible());
                    } catch (InvocationTargetException e) {
                        SneakyThrow.sneakyThrow(e.getCause());
                    }
                    return t;
                }
                throw new IllegalAccessException(init + " is not accessible from " + caller);
            } catch (NoSuchMethodException e2) {
                InstantiationException t2 = new InstantiationException(this + " has no zero argument constructor");
                t2.initCause(e2);
                throw t2;
            }
        }
        throw new IllegalAccessException(this + " is not accessible from " + caller);
    }

    private boolean canAccess(Class<?> c) {
        if (Modifier.isPublic(c.accessFlags)) {
            return true;
        }
        return inSamePackage(c);
    }

    private boolean canAccessMember(Class<?> memberClass, int memberModifiers) {
        if (memberClass == this || Modifier.isPublic(memberModifiers)) {
            return true;
        }
        if (Modifier.isPrivate(memberModifiers)) {
            return false;
        }
        if (Modifier.isProtected(memberModifiers)) {
            for (Class<?> parent = this.superClass; parent != null; parent = parent.superClass) {
                if (parent == memberClass) {
                    return true;
                }
            }
        }
        return inSamePackage(memberClass);
    }

    private boolean inSamePackage(Class<?> c) {
        if (this.classLoader != c.classLoader) {
            return false;
        }
        String packageName1 = getPackageName$();
        String packageName2 = c.getPackageName$();
        if (packageName1 == null) {
            if (packageName2 == null) {
                return true;
            }
            return false;
        } else if (packageName2 != null) {
            return packageName1.equals(packageName2);
        } else {
            return false;
        }
    }

    public String toString() {
        if (isPrimitive()) {
            return getSimpleName();
        }
        return (isInterface() ? "interface " : "class ") + getName();
    }

    public Package getPackage() {
        ClassLoader loader = getClassLoader();
        if (loader == null) {
            return null;
        }
        String packageName = getPackageName$();
        if (packageName != null) {
            return loader.getPackage(packageName);
        }
        return null;
    }

    public String getPackageName$() {
        String name = getName();
        int last = name.lastIndexOf(46);
        return last == -1 ? null : name.substring(0, last);
    }

    public boolean desiredAssertionStatus() {
        return false;
    }

    public <U> Class<? extends U> asSubclass(Class<U> c) {
        if (c.isAssignableFrom(this)) {
            return this;
        }
        String actualClassName = getName();
        throw new ClassCastException(actualClassName + " cannot be cast to " + c.getName());
    }

    public T cast(Object obj) {
        if (obj == null) {
            return null;
        }
        if (isInstance(obj)) {
            return obj;
        }
        String actualClassName = obj.getClass().getName();
        throw new ClassCastException(actualClassName + " cannot be cast to " + getName());
    }

    public int getDexClassDefIndex() {
        return this.dexClassDefIndex == DexFormat.MAX_TYPE_IDX ? -1 : this.dexClassDefIndex;
    }

    public int getDexTypeIndex() {
        int typeIndex = this.dexTypeIndex;
        if (typeIndex != DexFormat.MAX_TYPE_IDX) {
            return typeIndex;
        }
        synchronized (this) {
            typeIndex = this.dexTypeIndex;
            if (typeIndex == DexFormat.MAX_TYPE_IDX) {
                if (this.dexClassDefIndex >= 0) {
                    typeIndex = getDex().typeIndexFromClassDefIndex(this.dexClassDefIndex);
                } else {
                    typeIndex = getDex().findTypeIndex(InternalNames.getInternalName(this));
                    if (typeIndex < 0) {
                        typeIndex = -1;
                    }
                }
                this.dexTypeIndex = typeIndex;
            }
        }
        return typeIndex;
    }

    public int getDexAnnotationDirectoryOffset() {
        Dex dex = getDex();
        if (dex == null) {
            return 0;
        }
        int classDefIndex = getDexClassDefIndex();
        if (classDefIndex >= 0) {
            return dex.annotationDirectoryOffsetFromClassDefIndex(classDefIndex);
        }
        return 0;
    }
}
