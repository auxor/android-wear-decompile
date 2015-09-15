package java.io;

import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;
import libcore.io.Memory;
import libcore.util.EmptyArray;
import org.w3c.dom.traversal.NodeFilter;

public class ObjectStreamClass implements Serializable {
    static final Class<?> ARRAY_OF_FIELDS;
    static final Class<?> CLASSCLASS;
    private static final int CLASS_MODIFIERS_MASK = 1553;
    private static final int CLINIT_MODIFIERS = 8;
    private static final String CLINIT_NAME = "<clinit>";
    private static final String CLINIT_SIGNATURE = "()V";
    static final long CONSTRUCTOR_IS_NOT_RESOLVED = -1;
    private static final Class<Externalizable> EXTERNALIZABLE;
    private static final int FIELD_MODIFIERS_MASK = 223;
    private static final int METHOD_MODIFIERS_MASK = 3391;
    public static final ObjectStreamField[] NO_FIELDS;
    static final Class<ObjectStreamClass> OBJECTSTREAMCLASSCLASS;
    private static final Class<?>[] READ_PARAM_TYPES;
    private static final Class<Serializable> SERIALIZABLE;
    static final Class<String> STRINGCLASS;
    private static final String UID_FIELD_NAME = "serialVersionUID";
    private static final Class<?>[] WRITE_PARAM_TYPES;
    private static final long serialVersionUID = -6120832682080437368L;
    private static SoftReference<ThreadLocal<WeakHashMap<Class<?>, ObjectStreamClass>>> storage;
    private transient boolean arePropertiesResolved;
    private volatile transient List<ObjectStreamClass> cachedHierarchy;
    private transient String className;
    private transient long constructor;
    private transient ObjectStreamField[] fields;
    private transient byte flags;
    private transient boolean isEnum;
    private transient boolean isExternalizable;
    private transient boolean isProxy;
    private transient boolean isSerializable;
    private transient ObjectStreamField[] loadFields;
    private transient Method methodReadObject;
    private transient Method methodReadObjectNoData;
    private transient Method methodReadResolve;
    private transient Method methodWriteObject;
    private transient Method methodWriteReplace;
    private transient HashMap<ObjectStreamField, Field> reflectionFields;
    private transient Class<?> resolvedClass;
    private transient Class<?> resolvedConstructorClass;
    private transient long resolvedConstructorMethodId;
    private transient ObjectStreamClass superclass;
    private transient long svUID;

    private static native long getConstructorId(Class<?> cls);

    static native String getConstructorSignature(Constructor<?> constructor);

    private static native String getFieldSignature(Field field);

    static native String getMethodSignature(Method method);

    private static native boolean hasClinit(Class<?> cls);

    private static native Object newInstance(Class<?> cls, long j);

    static {
        READ_PARAM_TYPES = new Class[]{ObjectInputStream.class};
        WRITE_PARAM_TYPES = new Class[]{ObjectOutputStream.class};
        NO_FIELDS = new ObjectStreamField[0];
        try {
            ARRAY_OF_FIELDS = Class.forName("[Ljava.io.ObjectStreamField;");
            SERIALIZABLE = Serializable.class;
            EXTERNALIZABLE = Externalizable.class;
            STRINGCLASS = String.class;
            CLASSCLASS = Class.class;
            OBJECTSTREAMCLASSCLASS = ObjectStreamClass.class;
            storage = new SoftReference(null);
        } catch (Object e) {
            throw new AssertionError(e);
        }
    }

    void setConstructor(long newConstructor) {
        this.constructor = newConstructor;
    }

    long getConstructor() {
        return this.constructor;
    }

    Field checkAndGetReflectionField(ObjectStreamField osf) {
        Field field;
        synchronized (this.reflectionFields) {
            field = (Field) this.reflectionFields.get(osf);
            if (field != null || this.reflectionFields.containsKey(osf)) {
            } else {
                try {
                    field = forClass().getDeclaredField(osf.getName());
                    int modifiers = field.getModifiers();
                    if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
                        field = null;
                        synchronized (this.reflectionFields) {
                            this.reflectionFields.put(osf, field);
                        }
                    } else {
                        field.setAccessible(true);
                        synchronized (this.reflectionFields) {
                            this.reflectionFields.put(osf, field);
                        }
                    }
                } catch (NoSuchFieldException e) {
                    field = null;
                }
            }
        }
        return field;
    }

    ObjectStreamClass() {
        this.reflectionFields = new HashMap();
        this.constructor = CONSTRUCTOR_IS_NOT_RESOLVED;
    }

    private static ObjectStreamClass createClassDesc(Class<?> cl) {
        ObjectStreamClass result = new ObjectStreamClass();
        boolean isArray = cl.isArray();
        boolean serializable = isSerializable(cl);
        boolean externalizable = isExternalizable(cl);
        result.isSerializable = serializable;
        result.isExternalizable = externalizable;
        result.setName(cl.getName());
        result.setClass(cl);
        Class<?> superclass = cl.getSuperclass();
        if (superclass != null) {
            result.setSuperclass(lookup(superclass));
        }
        Field[] declaredFields = null;
        if (serializable || externalizable) {
            if (result.isEnum() || result.isProxy()) {
                result.setSerialVersionUID(0);
            } else {
                declaredFields = cl.getDeclaredFields();
                result.setSerialVersionUID(computeSerialVersionUID(cl, declaredFields));
            }
        }
        if (!serializable || isArray) {
            result.setFields(NO_FIELDS);
        } else {
            if (declaredFields == null) {
                declaredFields = cl.getDeclaredFields();
            }
            result.buildFieldDescriptors(declaredFields);
        }
        ObjectStreamField[] fields = result.getFields();
        if (fields != null) {
            ObjectStreamField[] loadFields = new ObjectStreamField[fields.length];
            for (int i = 0; i < fields.length; i++) {
                loadFields[i] = new ObjectStreamField(fields[i].getName(), fields[i].getType(), fields[i].isUnshared());
                loadFields[i].getTypeString();
            }
            result.setLoadFields(loadFields);
        }
        byte flags = (byte) 0;
        if (externalizable) {
            flags = (byte) (((byte) 4) | CLINIT_MODIFIERS);
        } else if (serializable) {
            flags = (byte) 2;
        }
        result.methodWriteReplace = findMethod(cl, "writeReplace");
        result.methodReadResolve = findMethod(cl, "readResolve");
        result.methodWriteObject = findPrivateMethod(cl, "writeObject", WRITE_PARAM_TYPES);
        result.methodReadObject = findPrivateMethod(cl, "readObject", READ_PARAM_TYPES);
        result.methodReadObjectNoData = findPrivateMethod(cl, "readObjectNoData", EmptyArray.CLASS);
        if (result.hasMethodWriteObject()) {
            flags = (byte) (flags | 1);
        }
        result.setFlags(flags);
        return result;
    }

    void buildFieldDescriptors(Field[] declaredFields) {
        Object[] _fields;
        Field f = fieldSerialPersistentFields(forClass());
        if (f == null) {
            List<ObjectStreamField> serializableFields = new ArrayList(declaredFields.length);
            for (Field declaredField : declaredFields) {
                int modifiers = declaredField.getModifiers();
                if (!(Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers))) {
                    serializableFields.add(new ObjectStreamField(declaredField.getName(), declaredField.getType()));
                }
            }
            if (serializableFields.size() == 0) {
                _fields = NO_FIELDS;
            } else {
                ObjectStreamField[] _fields2 = (ObjectStreamField[]) serializableFields.toArray(new ObjectStreamField[serializableFields.size()]);
            }
        } else {
            f.setAccessible(true);
            try {
                _fields = (ObjectStreamField[]) f.get(null);
            } catch (Object ex) {
                throw new AssertionError(ex);
            }
        }
        Arrays.sort(_fields);
        int primOffset = 0;
        int objectOffset = 0;
        int i = 0;
        while (true) {
            int length = _fields.length;
            if (i < r0) {
                Class<?> type = _fields[i].getType();
                if (type.isPrimitive()) {
                    _fields[i].offset = primOffset;
                    primOffset += primitiveSize(type);
                } else {
                    int objectOffset2 = objectOffset + 1;
                    _fields[i].offset = objectOffset;
                    objectOffset = objectOffset2;
                }
                i++;
            } else {
                this.fields = _fields;
                return;
            }
        }
    }

    private static long computeSerialVersionUID(Class<?> cl, Field[] fields) {
        int modifiers;
        int i = 0;
        while (true) {
            Field field;
            int length = fields.length;
            if (i < r0) {
                field = fields[i];
                if (field.getType() == Long.TYPE) {
                    modifiers = field.getModifiers();
                    if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) && UID_FIELD_NAME.equals(field.getName())) {
                        field.setAccessible(true);
                        try {
                            return field.getLong(null);
                        } catch (Object iae) {
                            throw new RuntimeException("Error fetching SUID: " + iae);
                        }
                    }
                }
                i++;
            } else {
                try {
                    break;
                } catch (Throwable e) {
                    throw new Error(e);
                }
            }
        }
        MessageDigest digest = MessageDigest.getInstance("SHA");
        ByteArrayOutputStream sha = new ByteArrayOutputStream();
        try {
            boolean skip;
            Constructor<?>[] constructors;
            Constructor<?> constructor;
            Method[] methods;
            Method method;
            DataOutputStream output = new DataOutputStream(sha);
            output.writeUTF(cl.getName());
            int classModifiers = cl.getModifiers() & CLASS_MODIFIERS_MASK;
            boolean isArray = cl.isArray();
            if (isArray) {
                classModifiers |= NodeFilter.SHOW_DOCUMENT_FRAGMENT;
            }
            if (cl.isInterface() && !Modifier.isPublic(classModifiers)) {
                classModifiers &= -1025;
            }
            output.writeInt(classModifiers);
            if (!isArray) {
                Class<?>[] interfaces = cl.getInterfaces();
                length = interfaces.length;
                if (r0 > 1) {
                    Arrays.sort(interfaces, new Comparator<Class<?>>() {
                        public int compare(java.lang.Class<?> r1, java.lang.Class<?> r2) {
                            /* JADX: method processing error */
/*
                            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.io.ObjectStreamClass.1.compare(java.lang.Class, java.lang.Class):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.io.ObjectStreamClass.1.compare(java.lang.Class, java.lang.Class):int
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
                            throw new UnsupportedOperationException("Method not decompiled: java.io.ObjectStreamClass.1.compare(java.lang.Class, java.lang.Class):int");
                        }
                    });
                }
                i = 0;
                while (true) {
                    length = interfaces.length;
                    if (i < r0) {
                        output.writeUTF(interfaces[i].getName());
                        i++;
                    }
                }
                length = fields.length;
                if (r0 > 1) {
                    Arrays.sort(fields, new Comparator<Field>() {
                        public int compare(java.lang.reflect.Field r1, java.lang.reflect.Field r2) {
                            /* JADX: method processing error */
/*
                            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.io.ObjectStreamClass.2.compare(java.lang.reflect.Field, java.lang.reflect.Field):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.io.ObjectStreamClass.2.compare(java.lang.reflect.Field, java.lang.reflect.Field):int
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
                            throw new UnsupportedOperationException("Method not decompiled: java.io.ObjectStreamClass.2.compare(java.lang.reflect.Field, java.lang.reflect.Field):int");
                        }
                    });
                }
                i = 0;
                while (true) {
                    length = fields.length;
                    if (i < r0) {
                        break;
                    }
                    field = fields[i];
                    modifiers = field.getModifiers() & FIELD_MODIFIERS_MASK;
                    skip = Modifier.isPrivate(modifiers) && (Modifier.isTransient(modifiers) || Modifier.isStatic(modifiers));
                    if (!skip) {
                        output.writeUTF(field.getName());
                        output.writeInt(modifiers);
                        output.writeUTF(descriptorForFieldSignature(getFieldSignature(field)));
                    }
                    i++;
                }
                if (hasClinit(cl)) {
                    output.writeUTF(CLINIT_NAME);
                    output.writeInt(CLINIT_MODIFIERS);
                    output.writeUTF(CLINIT_SIGNATURE);
                }
                constructors = cl.getDeclaredConstructors();
                length = constructors.length;
                if (r0 > 1) {
                    Arrays.sort(constructors, new Comparator<Constructor<?>>() {
                        public int compare(java.lang.reflect.Constructor<?> r1, java.lang.reflect.Constructor<?> r2) {
                            /* JADX: method processing error */
/*
                            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.io.ObjectStreamClass.3.compare(java.lang.reflect.Constructor, java.lang.reflect.Constructor):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.io.ObjectStreamClass.3.compare(java.lang.reflect.Constructor, java.lang.reflect.Constructor):int
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
                            throw new UnsupportedOperationException("Method not decompiled: java.io.ObjectStreamClass.3.compare(java.lang.reflect.Constructor, java.lang.reflect.Constructor):int");
                        }
                    });
                }
                i = 0;
                while (true) {
                    length = constructors.length;
                    if (i < r0) {
                        break;
                    }
                    constructor = constructors[i];
                    modifiers = constructor.getModifiers() & METHOD_MODIFIERS_MASK;
                    if (!Modifier.isPrivate(modifiers)) {
                        output.writeUTF("<init>");
                        output.writeInt(modifiers);
                        output.writeUTF(descriptorForSignature(getConstructorSignature(constructor)).replace('/', '.'));
                    }
                    i++;
                }
                methods = cl.getDeclaredMethods();
                length = methods.length;
                if (r0 > 1) {
                    Arrays.sort(methods, new Comparator<Method>() {
                        public int compare(java.lang.reflect.Method r1, java.lang.reflect.Method r2) {
                            /* JADX: method processing error */
/*
                            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.io.ObjectStreamClass.4.compare(java.lang.reflect.Method, java.lang.reflect.Method):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.io.ObjectStreamClass.4.compare(java.lang.reflect.Method, java.lang.reflect.Method):int
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
                            throw new UnsupportedOperationException("Method not decompiled: java.io.ObjectStreamClass.4.compare(java.lang.reflect.Method, java.lang.reflect.Method):int");
                        }
                    });
                }
                i = 0;
                while (true) {
                    length = methods.length;
                    if (i >= r0) {
                        method = methods[i];
                        modifiers = method.getModifiers() & METHOD_MODIFIERS_MASK;
                        if (!Modifier.isPrivate(modifiers)) {
                            output.writeUTF(method.getName());
                            output.writeInt(modifiers);
                            output.writeUTF(descriptorForSignature(getMethodSignature(method)).replace('/', '.'));
                        }
                        i++;
                    } else {
                        return Memory.peekLong(digest.digest(sha.toByteArray()), 0, ByteOrder.LITTLE_ENDIAN);
                    }
                }
            }
            break;
            length = fields.length;
            if (r0 > 1) {
                Arrays.sort(fields, new Comparator<Field>() {
                    public int compare(java.lang.reflect.Field r1, java.lang.reflect.Field r2) {
                        /* JADX: method processing error */
/*
                        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.io.ObjectStreamClass.2.compare(java.lang.reflect.Field, java.lang.reflect.Field):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.io.ObjectStreamClass.2.compare(java.lang.reflect.Field, java.lang.reflect.Field):int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                        /*
                        // Can't load method instructions.
                        */
                        throw new UnsupportedOperationException("Method not decompiled: java.io.ObjectStreamClass.2.compare(java.lang.reflect.Field, java.lang.reflect.Field):int");
                    }
                });
            }
            i = 0;
            while (true) {
                length = fields.length;
                if (i < r0) {
                    break;
                    if (hasClinit(cl)) {
                        output.writeUTF(CLINIT_NAME);
                        output.writeInt(CLINIT_MODIFIERS);
                        output.writeUTF(CLINIT_SIGNATURE);
                    }
                    constructors = cl.getDeclaredConstructors();
                    length = constructors.length;
                    if (r0 > 1) {
                        Arrays.sort(constructors, new Comparator<Constructor<?>>() {
                            public int compare(java.lang.reflect.Constructor<?> r1, java.lang.reflect.Constructor<?> r2) {
                                /* JADX: method processing error */
/*
                                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.io.ObjectStreamClass.3.compare(java.lang.reflect.Constructor, java.lang.reflect.Constructor):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.io.ObjectStreamClass.3.compare(java.lang.reflect.Constructor, java.lang.reflect.Constructor):int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                                /*
                                // Can't load method instructions.
                                */
                                throw new UnsupportedOperationException("Method not decompiled: java.io.ObjectStreamClass.3.compare(java.lang.reflect.Constructor, java.lang.reflect.Constructor):int");
                            }
                        });
                    }
                    i = 0;
                    while (true) {
                        length = constructors.length;
                        if (i < r0) {
                            break;
                            methods = cl.getDeclaredMethods();
                            length = methods.length;
                            if (r0 > 1) {
                                Arrays.sort(methods, new Comparator<Method>() {
                                    public int compare(java.lang.reflect.Method r1, java.lang.reflect.Method r2) {
                                        /* JADX: method processing error */
/*
                                        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.io.ObjectStreamClass.4.compare(java.lang.reflect.Method, java.lang.reflect.Method):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.io.ObjectStreamClass.4.compare(java.lang.reflect.Method, java.lang.reflect.Method):int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                                        /*
                                        // Can't load method instructions.
                                        */
                                        throw new UnsupportedOperationException("Method not decompiled: java.io.ObjectStreamClass.4.compare(java.lang.reflect.Method, java.lang.reflect.Method):int");
                                    }
                                });
                            }
                            i = 0;
                            while (true) {
                                length = methods.length;
                                if (i >= r0) {
                                    return Memory.peekLong(digest.digest(sha.toByteArray()), 0, ByteOrder.LITTLE_ENDIAN);
                                }
                                method = methods[i];
                                modifiers = method.getModifiers() & METHOD_MODIFIERS_MASK;
                                if (!Modifier.isPrivate(modifiers)) {
                                    output.writeUTF(method.getName());
                                    output.writeInt(modifiers);
                                    output.writeUTF(descriptorForSignature(getMethodSignature(method)).replace('/', '.'));
                                }
                                i++;
                            }
                        } else {
                            constructor = constructors[i];
                            modifiers = constructor.getModifiers() & METHOD_MODIFIERS_MASK;
                            if (!Modifier.isPrivate(modifiers)) {
                                output.writeUTF("<init>");
                                output.writeInt(modifiers);
                                output.writeUTF(descriptorForSignature(getConstructorSignature(constructor)).replace('/', '.'));
                            }
                            i++;
                        }
                    }
                } else {
                    field = fields[i];
                    modifiers = field.getModifiers() & FIELD_MODIFIERS_MASK;
                    if (!Modifier.isPrivate(modifiers)) {
                    }
                    if (!skip) {
                        output.writeUTF(field.getName());
                        output.writeInt(modifiers);
                        output.writeUTF(descriptorForFieldSignature(getFieldSignature(field)));
                    }
                    i++;
                }
            }
        } catch (Object e2) {
            throw new RuntimeException(e2 + " computing SHA-1/SUID");
        }
    }

    private static String descriptorForFieldSignature(String signature) {
        return signature.replace('.', '/');
    }

    private static String descriptorForSignature(String signature) {
        return signature.substring(signature.indexOf("("));
    }

    static Field fieldSerialPersistentFields(Class<?> cl) {
        try {
            Field f = cl.getDeclaredField("serialPersistentFields");
            int modifiers = f.getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isPrivate(modifiers) && Modifier.isFinal(modifiers) && f.getType() == ARRAY_OF_FIELDS) {
                return f;
            }
        } catch (NoSuchFieldException e) {
        }
        return null;
    }

    public Class<?> forClass() {
        return this.resolvedClass;
    }

    Object newInstance(Class<?> instantiationClass) throws InvalidClassException {
        resolveConstructorClass(instantiationClass);
        return newInstance(instantiationClass, this.resolvedConstructorMethodId);
    }

    private Class<?> resolveConstructorClass(Class<?> objectClass) throws InvalidClassException {
        boolean wasExternalizable = true;
        if (this.resolvedConstructorClass != null) {
            return this.resolvedConstructorClass;
        }
        boolean wasSerializable;
        Class<?> constructorClass = objectClass;
        if ((this.flags & 2) != 0) {
            wasSerializable = true;
        } else {
            wasSerializable = false;
        }
        if (wasSerializable) {
            while (constructorClass != null && isSerializable(constructorClass)) {
                constructorClass = constructorClass.getSuperclass();
            }
        }
        Constructor<?> constructor = null;
        if (constructorClass != null) {
            try {
                constructor = constructorClass.getDeclaredConstructor(EmptyArray.CLASS);
            } catch (NoSuchMethodException e) {
            }
        }
        if (constructor == null) {
            throw new InvalidClassException(constructorClass != null ? constructorClass.getName() : null, "IllegalAccessException");
        }
        int constructorModifiers = constructor.getModifiers();
        boolean isPublic = Modifier.isPublic(constructorModifiers);
        boolean isProtected = Modifier.isProtected(constructorModifiers);
        boolean isPrivate = Modifier.isPrivate(constructorModifiers);
        if ((this.flags & 4) == 0) {
            wasExternalizable = false;
        }
        if (isPrivate || (wasExternalizable && !isPublic)) {
            throw new InvalidClassException(constructorClass.getName(), "IllegalAccessException");
        } else if (isPublic || isProtected || inSamePackage(constructorClass, objectClass)) {
            this.resolvedConstructorClass = constructorClass;
            this.resolvedConstructorMethodId = getConstructorId(this.resolvedConstructorClass);
            return constructorClass;
        } else {
            throw new InvalidClassException(constructorClass.getName(), "IllegalAccessException");
        }
    }

    private boolean inSamePackage(Class<?> c1, Class<?> c2) {
        String nameC1 = c1.getName();
        String nameC2 = c2.getName();
        int indexDotC1 = nameC1.lastIndexOf(46);
        if (indexDotC1 != nameC2.lastIndexOf(46)) {
            return false;
        }
        if (indexDotC1 == -1) {
            return true;
        }
        return nameC1.regionMatches(0, nameC2, 0, indexDotC1);
    }

    public ObjectStreamField getField(String name) {
        ObjectStreamField[] allFields = getFields();
        for (ObjectStreamField f : allFields) {
            if (f.getName().equals(name)) {
                return f;
            }
        }
        return null;
    }

    ObjectStreamField[] fields() {
        if (this.fields == null) {
            Class<?> forCl = forClass();
            if (forCl == null || !isSerializable() || forCl.isArray()) {
                setFields(NO_FIELDS);
            } else {
                buildFieldDescriptors(forCl.getDeclaredFields());
            }
        }
        return this.fields;
    }

    public ObjectStreamField[] getFields() {
        copyFieldAttributes();
        return this.loadFields == null ? (ObjectStreamField[]) fields().clone() : (ObjectStreamField[]) this.loadFields.clone();
    }

    List<ObjectStreamClass> getHierarchy() {
        List<ObjectStreamClass> result = this.cachedHierarchy;
        if (result != null) {
            return result;
        }
        result = makeHierarchy();
        this.cachedHierarchy = result;
        return result;
    }

    private List<ObjectStreamClass> makeHierarchy() {
        ArrayList<ObjectStreamClass> result = new ArrayList();
        for (ObjectStreamClass osc = this; osc != null; osc = osc.getSuperclass()) {
            result.add(0, osc);
        }
        return result;
    }

    private void copyFieldAttributes() {
        if (this.loadFields != null && this.fields != null) {
            for (ObjectStreamField loadField : this.loadFields) {
                String name = loadField.getName();
                for (ObjectStreamField field : this.fields) {
                    if (name.equals(field.getName())) {
                        loadField.setUnshared(field.isUnshared());
                        loadField.setOffset(field.getOffset());
                        break;
                    }
                }
            }
        }
    }

    ObjectStreamField[] getLoadFields() {
        return this.loadFields;
    }

    byte getFlags() {
        return this.flags;
    }

    public String getName() {
        return this.className;
    }

    public long getSerialVersionUID() {
        return this.svUID;
    }

    ObjectStreamClass getSuperclass() {
        return this.superclass;
    }

    static boolean isExternalizable(Class<?> cl) {
        return EXTERNALIZABLE.isAssignableFrom(cl);
    }

    static boolean isPrimitiveType(char typecode) {
        return (typecode == '[' || typecode == 'L') ? false : true;
    }

    static boolean isSerializable(Class<?> cl) {
        return SERIALIZABLE.isAssignableFrom(cl);
    }

    private void resolveProperties() {
        if (!this.arePropertiesResolved) {
            Class<?> cl = forClass();
            this.isProxy = Proxy.isProxyClass(cl);
            this.isEnum = Enum.class.isAssignableFrom(cl);
            this.isSerializable = isSerializable(cl);
            this.isExternalizable = isExternalizable(cl);
            this.arePropertiesResolved = true;
        }
    }

    boolean isSerializable() {
        resolveProperties();
        return this.isSerializable;
    }

    boolean isExternalizable() {
        resolveProperties();
        return this.isExternalizable;
    }

    boolean isProxy() {
        resolveProperties();
        return this.isProxy;
    }

    boolean isEnum() {
        resolveProperties();
        return this.isEnum;
    }

    public static ObjectStreamClass lookup(Class<?> cl) {
        ObjectStreamClass osc = lookupStreamClass(cl);
        return (osc.isSerializable() || osc.isExternalizable()) ? osc : null;
    }

    public static ObjectStreamClass lookupAny(Class<?> cl) {
        return lookupStreamClass(cl);
    }

    static ObjectStreamClass lookupStreamClass(Class<?> cl) {
        WeakHashMap<Class<?>, ObjectStreamClass> tlc = getCache();
        ObjectStreamClass cachedValue = (ObjectStreamClass) tlc.get(cl);
        if (cachedValue != null) {
            return cachedValue;
        }
        cachedValue = createClassDesc(cl);
        tlc.put(cl, cachedValue);
        return cachedValue;
    }

    private static WeakHashMap<Class<?>, ObjectStreamClass> getCache() {
        ThreadLocal<WeakHashMap<Class<?>, ObjectStreamClass>> tls = (ThreadLocal) storage.get();
        if (tls == null) {
            tls = new ThreadLocal<WeakHashMap<Class<?>, ObjectStreamClass>>() {
                public WeakHashMap<Class<?>, ObjectStreamClass> initialValue() {
                    return new WeakHashMap();
                }
            };
            storage = new SoftReference(tls);
        }
        return (WeakHashMap) tls.get();
    }

    static Method findMethod(Class<?> cl, String methodName) {
        Class<?> search = cl;
        while (search != null) {
            try {
                Method method = search.getDeclaredMethod(methodName, (Class[]) null);
                if (search == cl || (method.getModifiers() & 2) == 0) {
                    method.setAccessible(true);
                    return method;
                }
                search = search.getSuperclass();
            } catch (NoSuchMethodException e) {
            }
        }
        return null;
    }

    static Method findPrivateMethod(Class<?> cl, String methodName, Class<?>[] param) {
        try {
            Method method = cl.getDeclaredMethod(methodName, param);
            if (Modifier.isPrivate(method.getModifiers()) && method.getReturnType() == Void.TYPE) {
                method.setAccessible(true);
                return method;
            }
        } catch (NoSuchMethodException e) {
        }
        return null;
    }

    boolean hasMethodWriteReplace() {
        return this.methodWriteReplace != null;
    }

    Method getMethodWriteReplace() {
        return this.methodWriteReplace;
    }

    boolean hasMethodReadResolve() {
        return this.methodReadResolve != null;
    }

    Method getMethodReadResolve() {
        return this.methodReadResolve;
    }

    boolean hasMethodWriteObject() {
        return this.methodWriteObject != null;
    }

    Method getMethodWriteObject() {
        return this.methodWriteObject;
    }

    boolean hasMethodReadObject() {
        return this.methodReadObject != null;
    }

    Method getMethodReadObject() {
        return this.methodReadObject;
    }

    boolean hasMethodReadObjectNoData() {
        return this.methodReadObjectNoData != null;
    }

    Method getMethodReadObjectNoData() {
        return this.methodReadObjectNoData;
    }

    void initPrivateFields(ObjectStreamClass desc) {
        this.methodWriteReplace = desc.methodWriteReplace;
        this.methodReadResolve = desc.methodReadResolve;
        this.methodWriteObject = desc.methodWriteObject;
        this.methodReadObject = desc.methodReadObject;
        this.methodReadObjectNoData = desc.methodReadObjectNoData;
    }

    void setClass(Class<?> c) {
        this.resolvedClass = c;
    }

    void setFields(ObjectStreamField[] f) {
        this.fields = f;
    }

    void setLoadFields(ObjectStreamField[] f) {
        this.loadFields = f;
    }

    void setFlags(byte b) {
        this.flags = b;
    }

    void setName(String newName) {
        this.className = newName;
    }

    void setSerialVersionUID(long l) {
        this.svUID = l;
    }

    void setSuperclass(ObjectStreamClass c) {
        this.superclass = c;
    }

    private int primitiveSize(Class<?> type) {
        if (type == Byte.TYPE || type == Boolean.TYPE) {
            return 1;
        }
        if (type == Short.TYPE || type == Character.TYPE) {
            return 2;
        }
        if (type == Integer.TYPE || type == Float.TYPE) {
            return 4;
        }
        if (type == Long.TYPE || type == Double.TYPE) {
            return CLINIT_MODIFIERS;
        }
        throw new AssertionError();
    }

    public String toString() {
        return getName() + ": static final long serialVersionUID =" + getSerialVersionUID() + "L;";
    }

    public Class<?> checkAndGetTcObjectClass() throws InvalidClassException {
        boolean wasSerializable;
        boolean wasExternalizable;
        if ((this.flags & 2) != 0) {
            wasSerializable = true;
        } else {
            wasSerializable = false;
        }
        if ((this.flags & 4) != 0) {
            wasExternalizable = true;
        } else {
            wasExternalizable = false;
        }
        if (wasSerializable == wasExternalizable) {
            throw new InvalidClassException(getName() + " stream data is corrupt: SC_SERIALIZABLE=" + wasSerializable + " SC_EXTERNALIZABLE=" + wasExternalizable + ", classDescFlags must have one or the other");
        } else if (isEnum()) {
            throw new InvalidClassException(getName() + " local class is incompatible: Local class is an enum, streamed" + " data is tagged with TC_OBJECT");
        } else if (!isSerializable()) {
            throw new InvalidClassException(getName() + " local class is incompatible: Not" + " Serializable");
        } else if (wasExternalizable == isExternalizable()) {
            return forClass();
        } else {
            throw new InvalidClassException(getName() + " local class is incompatible: Local class is Serializable, stream" + " data requires Externalizable");
        }
    }

    public Class<?> checkAndGetTcEnumClass() throws InvalidClassException {
        if (isEnum()) {
            return forClass();
        }
        throw new InvalidClassException(getName() + " local class is incompatible: Local class is not an enum," + " streamed data is tagged with TC_ENUM");
    }
}
