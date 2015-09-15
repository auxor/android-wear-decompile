package libcore.reflect;

import com.android.dex.Dex;
import com.android.dex.Dex.Section;
import com.android.dex.EncodedValueReader;
import com.android.dex.FieldId;
import com.android.dex.MethodId;
import com.android.dex.ProtoId;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import libcore.util.EmptyArray;

public final class AnnotationAccess {
    private static final Class<?>[] NO_ARGUMENTS;
    private static final byte VISIBILITY_BUILD = (byte) 0;
    private static final byte VISIBILITY_RUNTIME = (byte) 1;
    private static final byte VISIBILITY_SYSTEM = (byte) 2;

    private AnnotationAccess() {
    }

    static {
        NO_ARGUMENTS = null;
    }

    public static <A extends Annotation> A getAnnotation(Class<?> c, Class<A> annotationType) {
        if (annotationType == null) {
            throw new NullPointerException("annotationType == null");
        }
        A annotation = getDeclaredAnnotation(c, annotationType);
        if (annotation != null) {
            return annotation;
        }
        if (isInherited(annotationType)) {
            for (Class<?> sup = c.getSuperclass(); sup != null; sup = sup.getSuperclass()) {
                annotation = getDeclaredAnnotation(sup, annotationType);
                if (annotation != null) {
                    return annotation;
                }
            }
        }
        return null;
    }

    private static boolean isInherited(Class<? extends Annotation> annotationType) {
        return isDeclaredAnnotationPresent(annotationType, Inherited.class);
    }

    public static Annotation[] getAnnotations(Class<?> c) {
        HashMap<Class<?>, Annotation> map = new HashMap();
        for (Annotation declaredAnnotation : getDeclaredAnnotations(c)) {
            map.put(declaredAnnotation.annotationType(), declaredAnnotation);
        }
        for (Class<?> sup = c.getSuperclass(); sup != null; sup = sup.getSuperclass()) {
            for (Annotation declaredAnnotation2 : getDeclaredAnnotations(sup)) {
                Class<? extends Annotation> clazz = declaredAnnotation2.annotationType();
                if (!map.containsKey(clazz) && isInherited(clazz)) {
                    map.put(clazz, declaredAnnotation2);
                }
            }
        }
        Collection<Annotation> coll = map.values();
        return (Annotation[]) coll.toArray(new Annotation[coll.size()]);
    }

    public static boolean isAnnotationPresent(Class<?> c, Class<? extends Annotation> annotationType) {
        if (annotationType == null) {
            throw new NullPointerException("annotationType == null");
        } else if (isDeclaredAnnotationPresent(c, annotationType)) {
            return true;
        } else {
            if (isInherited(annotationType)) {
                for (Class<?> sup = c.getSuperclass(); sup != null; sup = sup.getSuperclass()) {
                    if (isDeclaredAnnotationPresent(sup, annotationType)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public static List<Annotation> getDeclaredAnnotations(AnnotatedElement element) {
        return annotationSetToAnnotations(getDexClass(element), getAnnotationSetOffset(element));
    }

    public static <A extends Annotation> A getDeclaredAnnotation(AnnotatedElement element, Class<A> annotationClass) {
        com.android.dex.Annotation a = getAnnotation(element, (Class) annotationClass);
        return a != null ? toAnnotationInstance(getDexClass(element), annotationClass, a) : null;
    }

    public static boolean isDeclaredAnnotationPresent(AnnotatedElement element, Class<? extends Annotation> annotationClass) {
        return getAnnotation(element, (Class) annotationClass) != null;
    }

    private static com.android.dex.Annotation getAnnotation(AnnotatedElement element, Class<? extends Annotation> annotationClass) {
        int annotationSetOffset = getAnnotationSetOffset(element);
        if (annotationSetOffset == 0) {
            return null;
        }
        Dex dex = getDexClass(element).getDex();
        Section setIn = dex.open(annotationSetOffset);
        String annotationInternalName = InternalNames.getInternalName(annotationClass);
        int size = setIn.readInt();
        for (int i = 0; i < size; i++) {
            com.android.dex.Annotation candidate = dex.open(setIn.readInt()).readAnnotation();
            if (((String) dex.typeNames().get(candidate.getTypeIndex())).equals(annotationInternalName)) {
                return candidate;
            }
        }
        return null;
    }

    private static int getAnnotationSetOffset(AnnotatedElement element) {
        Class<?> dexClass = getDexClass(element);
        int directoryOffset = dexClass.getDexAnnotationDirectoryOffset();
        if (directoryOffset == 0) {
            return 0;
        }
        Section directoryIn = dexClass.getDex().open(directoryOffset);
        int classSetOffset = directoryIn.readInt();
        if (element instanceof Class) {
            return classSetOffset;
        }
        int fieldsSize = directoryIn.readInt();
        int methodsSize = directoryIn.readInt();
        directoryIn.readInt();
        int i;
        int annotationSetOffset;
        if (element instanceof Field) {
            int fieldIndex = ((Field) element).getDexFieldIndex();
            for (i = 0; i < fieldsSize; i++) {
                int candidateFieldIndex = directoryIn.readInt();
                annotationSetOffset = directoryIn.readInt();
                if (candidateFieldIndex == fieldIndex) {
                    return annotationSetOffset;
                }
            }
            return 0;
        }
        directoryIn.skip(fieldsSize * 8);
        int methodIndex = element instanceof Method ? ((Method) element).getDexMethodIndex() : ((Constructor) element).getDexMethodIndex();
        for (i = 0; i < methodsSize; i++) {
            int candidateMethodIndex = directoryIn.readInt();
            annotationSetOffset = directoryIn.readInt();
            if (candidateMethodIndex == methodIndex) {
                return annotationSetOffset;
            }
        }
        return 0;
    }

    private static Class<?> getDexClass(AnnotatedElement element) {
        return element instanceof Class ? (Class) element : ((Member) element).getDeclaringClass();
    }

    public static Annotation[][] getParameterAnnotations(Class<?> declaringClass, int methodDexIndex) {
        Dex dex = declaringClass.getDex();
        int protoIndex = ((MethodId) dex.methodIds().get(methodDexIndex)).getProtoIndex();
        int typesCount = dex.readTypeList(((ProtoId) dex.protoIds().get(protoIndex)).getParametersOffset()).getTypes().length;
        int directoryOffset = declaringClass.getDexAnnotationDirectoryOffset();
        if (directoryOffset == 0) {
            return (Annotation[][]) Array.newInstance(Annotation.class, typesCount, 0);
        }
        int i;
        Section directoryIn = dex.open(directoryOffset);
        directoryIn.readInt();
        int fieldsSize = directoryIn.readInt();
        int methodsSize = directoryIn.readInt();
        int parametersSize = directoryIn.readInt();
        for (i = 0; i < fieldsSize; i++) {
            directoryIn.readInt();
            directoryIn.readInt();
        }
        for (i = 0; i < methodsSize; i++) {
            directoryIn.readInt();
            directoryIn.readInt();
        }
        i = 0;
        while (i < parametersSize) {
            int candidateMethodDexIndex = directoryIn.readInt();
            int annotationSetRefListOffset = directoryIn.readInt();
            if (candidateMethodDexIndex != methodDexIndex) {
                i++;
            } else {
                Section refList = dex.open(annotationSetRefListOffset);
                int parameterCount = refList.readInt();
                Annotation[][] result = new Annotation[parameterCount][];
                for (int p = 0; p < parameterCount; p++) {
                    List<Annotation> annotations = annotationSetToAnnotations(declaringClass, refList.readInt());
                    result[p] = (Annotation[]) annotations.toArray(new Annotation[annotations.size()]);
                }
                return result;
            }
        }
        return (Annotation[][]) Array.newInstance(Annotation.class, typesCount, 0);
    }

    public static Object getDefaultValue(Method method) {
        Class<?> annotationClass = method.getDeclaringClass();
        Dex dex = annotationClass.getDex();
        EncodedValueReader reader = getOnlyAnnotationValue(dex, annotationClass, "Ldalvik/annotation/AnnotationDefault;");
        if (reader == null) {
            return null;
        }
        int fieldCount = reader.readAnnotation();
        if (reader.getAnnotationType() != annotationClass.getDexTypeIndex()) {
            throw new AssertionError((Object) "annotation value type != annotation class");
        }
        int methodNameIndex = dex.findStringIndex(method.getName());
        for (int i = 0; i < fieldCount; i++) {
            if (reader.readAnnotationName() == methodNameIndex) {
                return decodeValue(annotationClass, method.getReturnType(), dex, reader);
            }
            reader.skipValue();
        }
        return null;
    }

    public static Class<?> getEnclosingClass(Class<?> c) {
        Dex dex = c.getDex();
        EncodedValueReader reader = getOnlyAnnotationValue(dex, c, "Ldalvik/annotation/EnclosingClass;");
        if (reader == null) {
            return null;
        }
        return c.getDexCacheType(dex, reader.readType());
    }

    public static AccessibleObject getEnclosingMethodOrConstructor(Class<?> c) {
        Dex dex = c.getDex();
        EncodedValueReader reader = getOnlyAnnotationValue(dex, c, "Ldalvik/annotation/EnclosingMethod;");
        if (reader == null) {
            return null;
        }
        return indexToMethod(c, dex, reader.readMethod());
    }

    public static Class<?>[] getMemberClasses(Class<?> c) {
        Dex dex = c.getDex();
        EncodedValueReader reader = getOnlyAnnotationValue(dex, c, "Ldalvik/annotation/MemberClasses;");
        if (reader == null) {
            return EmptyArray.CLASS;
        }
        return (Class[]) decodeValue(c, Class[].class, dex, reader);
    }

    public static String getSignature(AnnotatedElement element) {
        Class<?> dexClass = getDexClass(element);
        Dex dex = dexClass.getDex();
        EncodedValueReader reader = getOnlyAnnotationValue(dex, element, "Ldalvik/annotation/Signature;");
        if (reader == null) {
            return null;
        }
        String[] array = (String[]) decodeValue(dexClass, String[].class, dex, reader);
        StringBuilder result = new StringBuilder();
        for (String s : array) {
            result.append(s);
        }
        return result.toString();
    }

    public static Class<?>[] getExceptions(AnnotatedElement element) {
        Class<?> dexClass = getDexClass(element);
        Dex dex = dexClass.getDex();
        EncodedValueReader reader = getOnlyAnnotationValue(dex, element, "Ldalvik/annotation/Throws;");
        if (reader == null) {
            return EmptyArray.CLASS;
        }
        return (Class[]) decodeValue(dexClass, Class[].class, dex, reader);
    }

    public static int getInnerClassFlags(Class<?> c, int defaultValue) {
        EncodedValueReader reader = getAnnotationReader(c.getDex(), c, "Ldalvik/annotation/InnerClass;", 2);
        if (reader == null) {
            return defaultValue;
        }
        reader.readAnnotationName();
        return reader.readInt();
    }

    public static String getInnerClassName(Class<?> c) {
        Dex dex = c.getDex();
        EncodedValueReader reader = getAnnotationReader(dex, c, "Ldalvik/annotation/InnerClass;", 2);
        if (reader == null) {
            return null;
        }
        reader.readAnnotationName();
        reader.readInt();
        reader.readAnnotationName();
        if (reader.peek() != 30) {
            return (String) decodeValue(c, String.class, dex, reader);
        }
        return null;
    }

    public static boolean isAnonymousClass(Class<?> c) {
        EncodedValueReader reader = getAnnotationReader(c.getDex(), c, "Ldalvik/annotation/InnerClass;", 2);
        if (reader == null) {
            return false;
        }
        reader.readAnnotationName();
        reader.readInt();
        reader.readAnnotationName();
        if (reader.peek() == 30) {
            return true;
        }
        return false;
    }

    private static EncodedValueReader getAnnotationReader(Dex dex, AnnotatedElement element, String annotationName, int expectedFieldCount) {
        int annotationSetOffset = getAnnotationSetOffset(element);
        if (annotationSetOffset == 0) {
            return null;
        }
        Section setIn = dex.open(annotationSetOffset);
        com.android.dex.Annotation annotation = null;
        int size = setIn.readInt();
        for (int i = 0; i < size; i++) {
            com.android.dex.Annotation candidate = dex.open(setIn.readInt()).readAnnotation();
            if (annotationName.equals((String) dex.typeNames().get(candidate.getTypeIndex()))) {
                annotation = candidate;
                break;
            }
        }
        if (annotation == null) {
            return null;
        }
        EncodedValueReader reader = annotation.getReader();
        int fieldCount = reader.readAnnotation();
        if (!((String) dex.typeNames().get(reader.getAnnotationType())).equals(annotationName)) {
            throw new AssertionError();
        } else if (fieldCount != expectedFieldCount) {
            return null;
        } else {
            return reader;
        }
    }

    private static EncodedValueReader getOnlyAnnotationValue(Dex dex, AnnotatedElement element, String annotationName) {
        EncodedValueReader reader = getAnnotationReader(dex, element, annotationName, 1);
        if (reader == null) {
            return null;
        }
        reader.readAnnotationName();
        return reader;
    }

    private static Class<? extends Annotation> getAnnotationClass(Class<?> context, Dex dex, int typeIndex) {
        try {
            Class<? extends Annotation> dexCacheType = context.getDexCacheType(dex, typeIndex);
            if (dexCacheType.isAnnotation()) {
                return dexCacheType;
            }
            throw new IncompatibleClassChangeError("Expected annotation: " + dexCacheType.getName());
        } catch (NoClassDefFoundError e) {
            return null;
        }
    }

    private static AccessibleObject indexToMethod(Class<?> context, Dex dex, int methodIndex) {
        Class<?> declaringClass = context.getDexCacheType(dex, dex.declaringClassIndexFromMethodIndex(methodIndex));
        String name = context.getDexCacheString(dex, dex.nameIndexFromMethodIndex(methodIndex));
        short[] types = dex.parameterTypeIndicesFromMethodIndex(methodIndex);
        Object[] parametersArray = new Class[types.length];
        for (int i = 0; i < types.length; i++) {
            parametersArray[i] = context.getDexCacheType(dex, types[i]);
        }
        try {
            return name.equals("<init>") ? declaringClass.getDeclaredConstructor(parametersArray) : declaringClass.getDeclaredMethod(name, parametersArray);
        } catch (NoSuchMethodException e) {
            throw new IncompatibleClassChangeError("Couldn't find " + declaringClass.getName() + "." + name + Arrays.toString(parametersArray));
        }
    }

    private static List<Annotation> annotationSetToAnnotations(Class<?> context, int offset) {
        if (offset == 0) {
            return Collections.emptyList();
        }
        Dex dex = context.getDex();
        Section setIn = dex.open(offset);
        int size = setIn.readInt();
        List<Annotation> result = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            com.android.dex.Annotation annotation = dex.open(setIn.readInt()).readAnnotation();
            if (annotation.getVisibility() == 1) {
                Class<? extends Annotation> annotationClass = getAnnotationClass(context, dex, annotation.getTypeIndex());
                if (annotationClass != null) {
                    result.add(toAnnotationInstance(context, dex, annotationClass, annotation.getReader()));
                }
            }
        }
        return result;
    }

    private static <A extends Annotation> A toAnnotationInstance(Class<?> context, Class<A> annotationClass, com.android.dex.Annotation annotation) {
        return toAnnotationInstance(context, context.getDex(), annotationClass, annotation.getReader());
    }

    private static <A extends Annotation> A toAnnotationInstance(Class<?> context, Dex dex, Class<A> annotationClass, EncodedValueReader reader) {
        int fieldCount = reader.readAnnotation();
        if (annotationClass != context.getDexCacheType(dex, reader.getAnnotationType())) {
            throw new AssertionError((Object) "annotation value type != return type");
        }
        AnnotationMember[] members = new AnnotationMember[fieldCount];
        int i = 0;
        while (i < fieldCount) {
            String nameString = (String) dex.strings().get(reader.readAnnotationName());
            try {
                Method method = annotationClass.getMethod(nameString, NO_ARGUMENTS);
                Class<?> returnType = method.getReturnType();
                members[i] = new AnnotationMember(nameString, decodeValue(context, returnType, dex, reader), returnType, method);
                i++;
            } catch (NoSuchMethodException e) {
                throw new IncompatibleClassChangeError("Couldn't find " + annotationClass.getName() + "." + nameString);
            }
        }
        return AnnotationFactory.createAnnotation(annotationClass, members);
    }

    private static Object decodeValue(Class<?> context, Class<?> type, Dex dex, EncodedValueReader reader) {
        if (type.isArray()) {
            int size = reader.readArray();
            Class componentType = type.getComponentType();
            Object newInstance = Array.newInstance(componentType, size);
            for (int i = 0; i < size; i++) {
                Array.set(newInstance, i, decodeValue(context, componentType, dex, reader));
            }
            return newInstance;
        } else if (type.isEnum()) {
            int fieldIndex = reader.readEnum();
            try {
                return type.getDeclaredField((String) dex.strings().get(((FieldId) dex.fieldIds().get(fieldIndex)).getNameIndex())).get(null);
            } catch (NoSuchFieldException e) {
                NoSuchFieldError error = new NoSuchFieldError();
                error.initCause(e);
                throw error;
            } catch (IllegalAccessException e2) {
                IllegalAccessError error2 = new IllegalAccessError();
                error2.initCause(e2);
                throw error2;
            }
        } else if (type.isAnnotation()) {
            return toAnnotationInstance(context, dex, type, reader);
        } else if (type == String.class) {
            return context.getDexCacheString(dex, reader.readString());
        } else if (type == Class.class) {
            return context.getDexCacheType(dex, reader.readType());
        } else if (type == Byte.TYPE) {
            return Byte.valueOf(reader.readByte());
        } else {
            if (type == Short.TYPE) {
                return Short.valueOf(reader.readShort());
            }
            if (type == Integer.TYPE) {
                return Integer.valueOf(reader.readInt());
            }
            if (type == Long.TYPE) {
                return Long.valueOf(reader.readLong());
            }
            if (type == Float.TYPE) {
                return Float.valueOf(reader.readFloat());
            }
            if (type == Double.TYPE) {
                return Double.valueOf(reader.readDouble());
            }
            if (type == Character.TYPE) {
                return Character.valueOf(reader.readChar());
            }
            if (type == Boolean.TYPE) {
                return Boolean.valueOf(reader.readBoolean());
            }
            throw new AssertionError("Unexpected annotation value type: " + type);
        }
    }
}
