package java.io;

import dalvik.bytecode.Opcodes;
import dalvik.system.VMStack;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import libcore.util.EmptyArray;

public class ObjectInputStream extends InputStream implements ObjectInput, ObjectStreamConstants {
    private static final HashMap<String, Class<?>> PRIMITIVE_CLASSES;
    private static final Object UNSHARED_OBJ;
    private static final ClassLoader bootstrapLoader;
    private static final ClassLoader systemLoader;
    private HashMap<Class<?>, List<Class<?>>> cachedSuperclasses;
    private ClassLoader callerClassLoader;
    private ObjectStreamClass currentClass;
    private Object currentObject;
    private int descriptorHandle;
    private InputStream emptyStream;
    private boolean enableResolve;
    private boolean hasPushbackTC;
    private DataInputStream input;
    private boolean mustResolve;
    private int nestedLevels;
    private int nextHandle;
    private ArrayList<Object> objectsRead;
    private InputStream primitiveData;
    private DataInputStream primitiveTypes;
    private byte pushbackTC;
    private boolean subclassOverridingImplementation;
    private InputValidationDesc[] validations;

    public static abstract class GetField {
        public abstract boolean defaulted(String str) throws IOException, IllegalArgumentException;

        public abstract byte get(String str, byte b) throws IOException, IllegalArgumentException;

        public abstract char get(String str, char c) throws IOException, IllegalArgumentException;

        public abstract double get(String str, double d) throws IOException, IllegalArgumentException;

        public abstract float get(String str, float f) throws IOException, IllegalArgumentException;

        public abstract int get(String str, int i) throws IOException, IllegalArgumentException;

        public abstract long get(String str, long j) throws IOException, IllegalArgumentException;

        public abstract Object get(String str, Object obj) throws IOException, IllegalArgumentException;

        public abstract short get(String str, short s) throws IOException, IllegalArgumentException;

        public abstract boolean get(String str, boolean z) throws IOException, IllegalArgumentException;

        public abstract ObjectStreamClass getObjectStreamClass();
    }

    static class InputValidationDesc {
        int priority;
        ObjectInputValidation validator;

        InputValidationDesc() {
        }
    }

    static {
        UNSHARED_OBJ = new Object();
        PRIMITIVE_CLASSES = new HashMap();
        PRIMITIVE_CLASSES.put("boolean", Boolean.TYPE);
        PRIMITIVE_CLASSES.put("byte", Byte.TYPE);
        PRIMITIVE_CLASSES.put("char", Character.TYPE);
        PRIMITIVE_CLASSES.put("double", Double.TYPE);
        PRIMITIVE_CLASSES.put("float", Float.TYPE);
        PRIMITIVE_CLASSES.put("int", Integer.TYPE);
        PRIMITIVE_CLASSES.put("long", Long.TYPE);
        PRIMITIVE_CLASSES.put("short", Short.TYPE);
        PRIMITIVE_CLASSES.put("void", Void.TYPE);
        bootstrapLoader = Object.class.getClassLoader();
        systemLoader = ClassLoader.getSystemClassLoader();
    }

    protected ObjectInputStream() throws IOException {
        this.emptyStream = new ByteArrayInputStream(EmptyArray.BYTE);
        this.primitiveData = this.emptyStream;
        this.mustResolve = true;
        this.descriptorHandle = -1;
        this.cachedSuperclasses = new HashMap();
        this.subclassOverridingImplementation = true;
    }

    public ObjectInputStream(InputStream input) throws StreamCorruptedException, IOException {
        this.emptyStream = new ByteArrayInputStream(EmptyArray.BYTE);
        this.primitiveData = this.emptyStream;
        this.mustResolve = true;
        this.descriptorHandle = -1;
        this.cachedSuperclasses = new HashMap();
        this.input = input instanceof DataInputStream ? (DataInputStream) input : new DataInputStream(input);
        this.primitiveTypes = new DataInputStream(this);
        this.enableResolve = false;
        this.subclassOverridingImplementation = false;
        resetState();
        this.nestedLevels = 0;
        this.primitiveData = this.input;
        readStreamHeader();
        this.primitiveData = this.emptyStream;
    }

    public int available() throws IOException {
        checkReadPrimitiveTypes();
        return this.primitiveData.available();
    }

    private void checkReadPrimitiveTypes() throws IOException {
        if (this.primitiveData != this.input && this.primitiveData.available() <= 0) {
            while (true) {
                int next = 0;
                if (this.hasPushbackTC) {
                    this.hasPushbackTC = false;
                } else {
                    next = this.input.read();
                    this.pushbackTC = (byte) next;
                }
                switch (this.pushbackTC) {
                    case Opcodes.OP_INVOKE_STATIC_RANGE /*119*/:
                        this.primitiveData = new ByteArrayInputStream(readBlockData());
                        return;
                    case (byte) 121:
                        resetState();
                    case (byte) 122:
                        this.primitiveData = new ByteArrayInputStream(readBlockDataLong());
                        return;
                    default:
                        if (next != -1) {
                            pushbackTC();
                            return;
                        }
                        return;
                }
            }
        }
    }

    public void close() throws IOException {
        this.input.close();
    }

    public void defaultReadObject() throws IOException, ClassNotFoundException, NotActiveException {
        if (this.currentObject == null && this.mustResolve) {
            throw new NotActiveException();
        }
        readFieldValues(this.currentObject, this.currentClass);
    }

    protected boolean enableResolveObject(boolean enable) {
        boolean originalValue = this.enableResolve;
        this.enableResolve = enable;
        return originalValue;
    }

    private int nextHandle() {
        int i = this.nextHandle;
        this.nextHandle = i + 1;
        return i;
    }

    private byte nextTC() throws IOException {
        if (this.hasPushbackTC) {
            this.hasPushbackTC = false;
        } else {
            this.pushbackTC = this.input.readByte();
        }
        return this.pushbackTC;
    }

    private void pushbackTC() {
        this.hasPushbackTC = true;
    }

    public int read() throws IOException {
        checkReadPrimitiveTypes();
        return this.primitiveData.read();
    }

    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        Arrays.checkOffsetAndCount(buffer.length, byteOffset, byteCount);
        if (byteCount == 0) {
            return 0;
        }
        checkReadPrimitiveTypes();
        return this.primitiveData.read(buffer, byteOffset, byteCount);
    }

    private byte[] readBlockData() throws IOException {
        byte[] result = new byte[(this.input.readByte() & Opcodes.OP_CONST_CLASS_JUMBO)];
        this.input.readFully(result);
        return result;
    }

    private byte[] readBlockDataLong() throws IOException {
        byte[] result = new byte[this.input.readInt()];
        this.input.readFully(result);
        return result;
    }

    public boolean readBoolean() throws IOException {
        return this.primitiveTypes.readBoolean();
    }

    public byte readByte() throws IOException {
        return this.primitiveTypes.readByte();
    }

    public char readChar() throws IOException {
        return this.primitiveTypes.readChar();
    }

    private void discardData() throws ClassNotFoundException, IOException {
        this.primitiveData = this.emptyStream;
        boolean resolve = this.mustResolve;
        this.mustResolve = false;
        while (true) {
            byte tc = nextTC();
            if (tc == 120) {
                this.mustResolve = resolve;
                return;
            }
            readContent(tc);
        }
    }

    private ObjectStreamClass readClassDesc() throws ClassNotFoundException, IOException {
        byte tc = nextTC();
        switch (tc) {
            case Opcodes.OP_INVOKE_DIRECT /*112*/:
                return null;
            case Opcodes.OP_INVOKE_STATIC /*113*/:
                return (ObjectStreamClass) readCyclicReference();
            case Opcodes.OP_INVOKE_INTERFACE /*114*/:
                return readNewClassDesc(false);
            case Opcodes.OP_NEG_LONG /*125*/:
                ObjectStreamClass streamClass = ObjectStreamClass.lookup(readNewProxyClassDesc());
                streamClass.setLoadFields(ObjectStreamClass.NO_FIELDS);
                registerObjectRead(streamClass, nextHandle(), false);
                checkedSetSuperClassDesc(streamClass, readClassDesc());
                return streamClass;
            default:
                throw corruptStream(tc);
        }
    }

    private StreamCorruptedException corruptStream(byte tc) throws StreamCorruptedException {
        throw new StreamCorruptedException("Wrong format: " + Integer.toHexString(tc & Opcodes.OP_CONST_CLASS_JUMBO));
    }

    private Object readContent(byte tc) throws ClassNotFoundException, IOException {
        switch (tc) {
            case Opcodes.OP_INVOKE_DIRECT /*112*/:
                return null;
            case Opcodes.OP_INVOKE_STATIC /*113*/:
                return readCyclicReference();
            case Opcodes.OP_INVOKE_INTERFACE /*114*/:
                return readNewClassDesc(false);
            case (byte) 115:
                return readNewObject(false);
            case Opcodes.OP_INVOKE_VIRTUAL_RANGE /*116*/:
                return readNewString(false);
            case Opcodes.OP_INVOKE_SUPER_RANGE /*117*/:
                return readNewArray(false);
            case Opcodes.OP_INVOKE_DIRECT_RANGE /*118*/:
                return readNewClass(false);
            case Opcodes.OP_INVOKE_STATIC_RANGE /*119*/:
                return readBlockData();
            case (byte) 121:
                resetState();
                return null;
            case (byte) 122:
                return readBlockDataLong();
            case Opcodes.OP_NEG_INT /*123*/:
                throw new WriteAbortedException("Read an exception", readException());
            case Opcodes.OP_NOT_INT /*124*/:
                return readNewLongString(false);
            default:
                throw corruptStream(tc);
        }
    }

    private Object readNonPrimitiveContent(boolean unshared) throws ClassNotFoundException, IOException {
        checkReadPrimitiveTypes();
        if (this.primitiveData.available() > 0) {
            OptionalDataException e = new OptionalDataException();
            e.length = this.primitiveData.available();
            throw e;
        }
        while (true) {
            byte tc = nextTC();
            switch (tc) {
                case Opcodes.OP_INVOKE_DIRECT /*112*/:
                    return null;
                case Opcodes.OP_INVOKE_STATIC /*113*/:
                    if (!unshared) {
                        return readCyclicReference();
                    }
                    readNewHandle();
                    throw new InvalidObjectException("Unshared read of back reference");
                case Opcodes.OP_INVOKE_INTERFACE /*114*/:
                    return readNewClassDesc(unshared);
                case (byte) 115:
                    return readNewObject(unshared);
                case Opcodes.OP_INVOKE_VIRTUAL_RANGE /*116*/:
                    return readNewString(unshared);
                case Opcodes.OP_INVOKE_SUPER_RANGE /*117*/:
                    return readNewArray(unshared);
                case Opcodes.OP_INVOKE_DIRECT_RANGE /*118*/:
                    return readNewClass(unshared);
                case Opcodes.OP_INVOKE_INTERFACE_RANGE /*120*/:
                    pushbackTC();
                    e = new OptionalDataException();
                    e.eof = true;
                    throw e;
                case (byte) 121:
                    resetState();
                case Opcodes.OP_NEG_INT /*123*/:
                    throw new WriteAbortedException("Read an exception", readException());
                case Opcodes.OP_NOT_INT /*124*/:
                    return readNewLongString(unshared);
                case Opcodes.OP_NOT_LONG /*126*/:
                    return readEnum(unshared);
                default:
                    throw corruptStream(tc);
            }
        }
    }

    private Object readCyclicReference() throws InvalidObjectException, IOException {
        return registeredObjectRead(readNewHandle());
    }

    public double readDouble() throws IOException {
        return this.primitiveTypes.readDouble();
    }

    private Exception readException() throws WriteAbortedException, OptionalDataException, ClassNotFoundException, IOException {
        resetSeenObjects();
        Exception exc = (Exception) readObject();
        resetSeenObjects();
        return exc;
    }

    private void readFieldDescriptors(ObjectStreamClass cDesc) throws ClassNotFoundException, IOException {
        short numFields = this.input.readShort();
        ObjectStreamField[] fields = new ObjectStreamField[numFields];
        cDesc.setLoadFields(fields);
        for (short i = (short) 0; i < numFields; i = (short) (i + 1)) {
            String classSig;
            char typecode = (char) this.input.readByte();
            String fieldName = this.input.readUTF();
            if (ObjectStreamClass.isPrimitiveType(typecode)) {
                classSig = String.valueOf(typecode);
            } else {
                boolean old = this.enableResolve;
                try {
                    this.enableResolve = false;
                    classSig = (String) readObject();
                } finally {
                    this.enableResolve = old;
                }
            }
            classSig = formatClassSig(classSig);
            fields[i] = new ObjectStreamField(classSig, fieldName);
        }
    }

    private static String formatClassSig(String classSig) {
        int start = 0;
        int end = classSig.length();
        if (end <= 0) {
            return classSig;
        }
        while (classSig.startsWith("[L", start) && classSig.charAt(end - 1) == ';') {
            start += 2;
            end--;
        }
        if (start > 0) {
            return classSig.substring(start - 2, end + 1);
        }
        return classSig;
    }

    public GetField readFields() throws IOException, ClassNotFoundException, NotActiveException {
        if (this.currentObject == null) {
            throw new NotActiveException();
        }
        EmulatedFieldsForLoading result = new EmulatedFieldsForLoading(this.currentClass);
        readFieldValues(result);
        return result;
    }

    private void readFieldValues(EmulatedFieldsForLoading emulatedFields) throws OptionalDataException, InvalidClassException, IOException {
        for (ObjectSlot element : emulatedFields.emulatedFields().slots()) {
            element.defaulted = false;
            Class<?> type = element.field.getType();
            if (type == Integer.TYPE) {
                element.fieldValue = Integer.valueOf(this.input.readInt());
            } else if (type == Byte.TYPE) {
                element.fieldValue = Byte.valueOf(this.input.readByte());
            } else if (type == Character.TYPE) {
                element.fieldValue = Character.valueOf(this.input.readChar());
            } else if (type == Short.TYPE) {
                element.fieldValue = Short.valueOf(this.input.readShort());
            } else if (type == Boolean.TYPE) {
                element.fieldValue = Boolean.valueOf(this.input.readBoolean());
            } else if (type == Long.TYPE) {
                element.fieldValue = Long.valueOf(this.input.readLong());
            } else if (type == Float.TYPE) {
                element.fieldValue = Float.valueOf(this.input.readFloat());
            } else if (type == Double.TYPE) {
                element.fieldValue = Double.valueOf(this.input.readDouble());
            } else {
                try {
                    element.fieldValue = readObject();
                } catch (ClassNotFoundException cnf) {
                    throw new InvalidClassException(cnf.toString());
                }
            }
        }
    }

    private void readFieldValues(Object obj, ObjectStreamClass classDesc) throws OptionalDataException, ClassNotFoundException, IOException {
        ObjectStreamField[] fields = classDesc.getLoadFields();
        if (fields == null) {
            fields = ObjectStreamClass.NO_FIELDS;
        }
        if (classDesc.forClass() == null && this.mustResolve) {
            throw new ClassNotFoundException(classDesc.getName());
        }
        for (ObjectStreamField fieldDesc : fields) {
            Field field = classDesc.checkAndGetReflectionField(fieldDesc);
            try {
                Class<?> type = fieldDesc.getTypeInternal();
                if (type == Byte.TYPE) {
                    byte b = this.input.readByte();
                    if (field != null) {
                        field.setByte(obj, b);
                    }
                } else if (type == Character.TYPE) {
                    char c = this.input.readChar();
                    if (field != null) {
                        field.setChar(obj, c);
                    }
                } else if (type == Double.TYPE) {
                    double d = this.input.readDouble();
                    if (field != null) {
                        field.setDouble(obj, d);
                    }
                } else if (type == Float.TYPE) {
                    float f = this.input.readFloat();
                    if (field != null) {
                        field.setFloat(obj, f);
                    }
                } else if (type == Integer.TYPE) {
                    int i = this.input.readInt();
                    if (field != null) {
                        field.setInt(obj, i);
                    }
                } else if (type == Long.TYPE) {
                    long j = this.input.readLong();
                    if (field != null) {
                        field.setLong(obj, j);
                    }
                } else if (type == Short.TYPE) {
                    short s = this.input.readShort();
                    if (field != null) {
                        field.setShort(obj, s);
                    }
                } else if (type == Boolean.TYPE) {
                    boolean z = this.input.readBoolean();
                    if (field != null) {
                        field.setBoolean(obj, z);
                    }
                } else {
                    Object toSet = fieldDesc.isUnshared() ? readUnshared() : readObject();
                    if (toSet != null) {
                        String fieldName = fieldDesc.getName();
                        Object fieldType = classDesc.getField(fieldName).getTypeInternal();
                        Class<?> valueType = toSet.getClass();
                        if (!fieldType.isAssignableFrom(valueType)) {
                            throw new ClassCastException(classDesc.getName() + "." + fieldName + " - " + fieldType + " not compatible with " + valueType);
                        } else if (field != null) {
                            field.set(obj, toSet);
                        }
                    } else {
                        continue;
                    }
                }
            } catch (IllegalAccessException iae) {
                throw new AssertionError((Object) iae);
            } catch (NoSuchFieldError e) {
            }
        }
    }

    public float readFloat() throws IOException {
        return this.primitiveTypes.readFloat();
    }

    public void readFully(byte[] dst) throws IOException {
        this.primitiveTypes.readFully(dst);
    }

    public void readFully(byte[] dst, int offset, int byteCount) throws IOException {
        this.primitiveTypes.readFully(dst, offset, byteCount);
    }

    private void readHierarchy(Object object, ObjectStreamClass classDesc) throws IOException, ClassNotFoundException, NotActiveException {
        if (object == null && this.mustResolve) {
            throw new NotActiveException();
        }
        List<ObjectStreamClass> streamClassList = classDesc.getHierarchy();
        if (object == null) {
            for (ObjectStreamClass objectStreamClass : streamClassList) {
                readObjectForClass(null, objectStreamClass);
            }
            return;
        }
        List<Class<?>> superclasses = (List) this.cachedSuperclasses.get(object.getClass());
        if (superclasses == null) {
            superclasses = cacheSuperclassesFor(object.getClass());
        }
        int lastIndex = 0;
        int end = superclasses.size();
        for (int i = 0; i < end; i++) {
            Class<?> superclass = (Class) superclasses.get(i);
            int index = findStreamSuperclass(superclass, streamClassList, lastIndex);
            if (index == -1) {
                readObjectNoData(object, superclass, ObjectStreamClass.lookupStreamClass(superclass));
            } else {
                for (int j = lastIndex; j <= index; j++) {
                    readObjectForClass(object, (ObjectStreamClass) streamClassList.get(j));
                }
                lastIndex = index + 1;
            }
        }
    }

    private List<Class<?>> cacheSuperclassesFor(Class<?> c) {
        ArrayList<Class<?>> result = new ArrayList();
        Class<?> nextClass = c;
        while (nextClass != null) {
            Class<?> testClass = nextClass.getSuperclass();
            if (testClass != null) {
                result.add(0, nextClass);
            }
            nextClass = testClass;
        }
        this.cachedSuperclasses.put(c, result);
        return result;
    }

    private int findStreamSuperclass(Class<?> cl, List<ObjectStreamClass> classList, int lastIndex) {
        int end = classList.size();
        for (int i = lastIndex; i < end; i++) {
            ObjectStreamClass objCl = (ObjectStreamClass) classList.get(i);
            String forName = objCl.forClass().getName();
            if (objCl.getName().equals(forName)) {
                if (cl.getName().equals(objCl.getName())) {
                    return i;
                }
            } else if (cl.getName().equals(forName)) {
                return i;
            }
        }
        return -1;
    }

    private void readObjectNoData(Object object, Class<?> cls, ObjectStreamClass classDesc) throws ObjectStreamException {
        if (classDesc.isSerializable() && classDesc.hasMethodReadObjectNoData()) {
            try {
                classDesc.getMethodReadObjectNoData().invoke(object, new Object[0]);
            } catch (InvocationTargetException e) {
                Throwable ex = e.getTargetException();
                if (ex instanceof RuntimeException) {
                    throw ((RuntimeException) ex);
                } else if (ex instanceof Error) {
                    throw ((Error) ex);
                } else {
                    throw ((ObjectStreamException) ex);
                }
            } catch (IllegalAccessException e2) {
                throw new RuntimeException(e2.toString());
            }
        }
    }

    private void readObjectForClass(Object object, ObjectStreamClass classDesc) throws IOException, ClassNotFoundException, NotActiveException {
        Method readMethod;
        boolean hadWriteMethod = true;
        this.currentObject = object;
        this.currentClass = classDesc;
        if ((classDesc.getFlags() & 1) == 0) {
            hadWriteMethod = false;
        }
        if (classDesc.forClass() == null || !this.mustResolve) {
            readMethod = null;
        } else {
            readMethod = classDesc.getMethodReadObject();
        }
        if (readMethod != null) {
            try {
                readMethod.setAccessible(true);
                readMethod.invoke(object, this);
            } catch (InvocationTargetException e) {
                ex = e.getTargetException();
                Throwable ex;
                if (ex instanceof ClassNotFoundException) {
                    throw ((ClassNotFoundException) ex);
                } else if (ex instanceof RuntimeException) {
                    throw ((RuntimeException) ex);
                } else if (ex instanceof Error) {
                    throw ((Error) ex);
                } else {
                    throw ((IOException) ex);
                }
            } catch (IllegalAccessException e2) {
                throw new RuntimeException(e2.toString());
            } catch (Throwable th) {
                this.currentObject = null;
                this.currentClass = null;
            }
        } else {
            defaultReadObject();
        }
        if (hadWriteMethod) {
            discardData();
        }
        this.currentObject = null;
        this.currentClass = null;
    }

    public int readInt() throws IOException {
        return this.primitiveTypes.readInt();
    }

    @Deprecated
    public String readLine() throws IOException {
        return this.primitiveTypes.readLine();
    }

    public long readLong() throws IOException {
        return this.primitiveTypes.readLong();
    }

    private Object readNewArray(boolean unshared) throws OptionalDataException, ClassNotFoundException, IOException {
        ObjectStreamClass classDesc = readClassDesc();
        if (classDesc == null) {
            throw missingClassDescriptor();
        }
        int newHandle = nextHandle();
        int size = this.input.readInt();
        Class componentType = classDesc.forClass().getComponentType();
        Object result = Array.newInstance(componentType, size);
        registerObjectRead(result, newHandle, unshared);
        int i;
        if (!componentType.isPrimitive()) {
            Object[] objectArray = (Object[]) result;
            for (i = 0; i < size; i++) {
                objectArray[i] = readObject();
            }
        } else if (componentType == Integer.TYPE) {
            int[] intArray = (int[]) result;
            for (i = 0; i < size; i++) {
                intArray[i] = this.input.readInt();
            }
        } else if (componentType == Byte.TYPE) {
            byte[] byteArray = (byte[]) result;
            this.input.readFully(byteArray, 0, size);
        } else if (componentType == Character.TYPE) {
            char[] charArray = (char[]) result;
            for (i = 0; i < size; i++) {
                charArray[i] = this.input.readChar();
            }
        } else if (componentType == Short.TYPE) {
            short[] shortArray = (short[]) result;
            for (i = 0; i < size; i++) {
                shortArray[i] = this.input.readShort();
            }
        } else if (componentType == Boolean.TYPE) {
            boolean[] booleanArray = (boolean[]) result;
            for (i = 0; i < size; i++) {
                booleanArray[i] = this.input.readBoolean();
            }
        } else if (componentType == Long.TYPE) {
            long[] longArray = (long[]) result;
            for (i = 0; i < size; i++) {
                longArray[i] = this.input.readLong();
            }
        } else if (componentType == Float.TYPE) {
            float[] floatArray = (float[]) result;
            for (i = 0; i < size; i++) {
                floatArray[i] = this.input.readFloat();
            }
        } else if (componentType == Double.TYPE) {
            double[] doubleArray = (double[]) result;
            for (i = 0; i < size; i++) {
                doubleArray[i] = this.input.readDouble();
            }
        } else {
            throw new ClassNotFoundException("Wrong base type in " + classDesc.getName());
        }
        if (!this.enableResolve) {
            return result;
        }
        result = resolveObject(result);
        registerObjectRead(result, newHandle, false);
        return result;
    }

    private Class<?> readNewClass(boolean unshared) throws ClassNotFoundException, IOException {
        ObjectStreamClass classDesc = readClassDesc();
        if (classDesc == null) {
            throw missingClassDescriptor();
        }
        Class<?> localClass = classDesc.forClass();
        if (localClass != null) {
            registerObjectRead(localClass, nextHandle(), unshared);
        }
        return localClass;
    }

    private ObjectStreamClass readEnumDesc() throws IOException, ClassNotFoundException {
        byte tc = nextTC();
        switch (tc) {
            case Opcodes.OP_INVOKE_DIRECT /*112*/:
                return null;
            case Opcodes.OP_INVOKE_STATIC /*113*/:
                return (ObjectStreamClass) readCyclicReference();
            case Opcodes.OP_INVOKE_INTERFACE /*114*/:
                return readEnumDescInternal();
            default:
                throw corruptStream(tc);
        }
    }

    private ObjectStreamClass readEnumDescInternal() throws IOException, ClassNotFoundException {
        this.primitiveData = this.input;
        int oldHandle = this.descriptorHandle;
        this.descriptorHandle = nextHandle();
        ObjectStreamClass classDesc = readClassDescriptor();
        registerObjectRead(classDesc, this.descriptorHandle, false);
        this.descriptorHandle = oldHandle;
        this.primitiveData = this.emptyStream;
        classDesc.setClass(resolveClass(classDesc));
        discardData();
        Object superClass = readClassDesc();
        checkedSetSuperClassDesc(classDesc, superClass);
        if (0 == classDesc.getSerialVersionUID() && 0 == superClass.getSerialVersionUID()) {
            if (nextTC() == 120) {
                superClass.setSuperclass(readClassDesc());
            } else {
                pushbackTC();
            }
            return classDesc;
        }
        throw new InvalidClassException(superClass.getName(), "Incompatible class (SUID): " + superClass + " but expected " + superClass);
    }

    private Object readEnum(boolean unshared) throws OptionalDataException, ClassNotFoundException, IOException {
        String name;
        Class enumType = readEnumDesc().checkAndGetTcEnumClass();
        int newHandle = nextHandle();
        byte tc = nextTC();
        switch (tc) {
            case Opcodes.OP_INVOKE_STATIC /*113*/:
                if (!unshared) {
                    name = (String) readCyclicReference();
                    break;
                }
                readNewHandle();
                throw new InvalidObjectException("Unshared read of back reference");
            case Opcodes.OP_INVOKE_VIRTUAL_RANGE /*116*/:
                name = (String) readNewString(unshared);
                break;
            default:
                throw corruptStream(tc);
        }
        try {
            Enum<?> result = Enum.valueOf(enumType, name);
            registerObjectRead(result, newHandle, unshared);
            return result;
        } catch (IllegalArgumentException e) {
            InvalidObjectException ioe = new InvalidObjectException(e.getMessage());
            ioe.initCause(e);
            throw ioe;
        }
    }

    private ObjectStreamClass readNewClassDesc(boolean unshared) throws ClassNotFoundException, IOException {
        this.primitiveData = this.input;
        int oldHandle = this.descriptorHandle;
        this.descriptorHandle = nextHandle();
        ObjectStreamClass newClassDesc = readClassDescriptor();
        registerObjectRead(newClassDesc, this.descriptorHandle, unshared);
        this.descriptorHandle = oldHandle;
        this.primitiveData = this.emptyStream;
        try {
            newClassDesc.setClass(resolveClass(newClassDesc));
            verifyAndInit(newClassDesc);
        } catch (ClassNotFoundException e) {
            if (this.mustResolve) {
                throw e;
            }
        }
        ObjectStreamField[] fields = newClassDesc.getLoadFields();
        if (fields == null) {
            fields = ObjectStreamClass.NO_FIELDS;
        }
        ClassLoader loader = newClassDesc.forClass() == null ? this.callerClassLoader : newClassDesc.forClass().getClassLoader();
        for (ObjectStreamField element : fields) {
            element.resolve(loader);
        }
        discardData();
        checkedSetSuperClassDesc(newClassDesc, readClassDesc());
        return newClassDesc;
    }

    private Class<?> readNewProxyClassDesc() throws ClassNotFoundException, IOException {
        int count = this.input.readInt();
        String[] interfaceNames = new String[count];
        for (int i = 0; i < count; i++) {
            interfaceNames[i] = this.input.readUTF();
        }
        Class<?> proxy = resolveProxyClass(interfaceNames);
        discardData();
        return proxy;
    }

    protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
        ObjectStreamClass newClassDesc = new ObjectStreamClass();
        String name = this.input.readUTF();
        if (name.length() == 0) {
            throw new IOException("The stream is corrupted");
        }
        newClassDesc.setName(name);
        newClassDesc.setSerialVersionUID(this.input.readLong());
        newClassDesc.setFlags(this.input.readByte());
        if (this.descriptorHandle == -1) {
            this.descriptorHandle = nextHandle();
        }
        registerObjectRead(newClassDesc, this.descriptorHandle, false);
        readFieldDescriptors(newClassDesc);
        return newClassDesc;
    }

    protected Class<?> resolveProxyClass(String[] interfaceNames) throws IOException, ClassNotFoundException {
        ClassLoader loader = this.callerClassLoader;
        Class<?>[] interfaces = new Class[interfaceNames.length];
        for (int i = 0; i < interfaceNames.length; i++) {
            interfaces[i] = Class.forName(interfaceNames[i], false, loader);
        }
        try {
            return Proxy.getProxyClass(loader, interfaces);
        } catch (IllegalArgumentException e) {
            throw new ClassNotFoundException(e.toString(), e);
        }
    }

    private int readNewHandle() throws IOException {
        return this.input.readInt();
    }

    private Object readNewObject(boolean unshared) throws OptionalDataException, ClassNotFoundException, IOException {
        boolean blockData = true;
        ObjectStreamClass classDesc = readClassDesc();
        if (classDesc == null) {
            throw missingClassDescriptor();
        }
        Object result;
        Class<?> objectClass = classDesc.checkAndGetTcObjectClass();
        int newHandle = nextHandle();
        Object registeredResult = null;
        if (objectClass != null) {
            result = classDesc.newInstance(objectClass);
            registerObjectRead(result, newHandle, unshared);
            registeredResult = result;
        } else {
            result = null;
        }
        try {
            boolean wasExternalizable;
            this.currentObject = result;
            this.currentClass = classDesc;
            if ((classDesc.getFlags() & 4) != 0) {
                wasExternalizable = true;
            } else {
                wasExternalizable = false;
            }
            if (wasExternalizable) {
                if ((classDesc.getFlags() & 8) == 0) {
                    blockData = false;
                }
                if (!blockData) {
                    this.primitiveData = this.input;
                }
                if (this.mustResolve) {
                    ((Externalizable) result).readExternal(this);
                }
                if (blockData) {
                    discardData();
                } else {
                    this.primitiveData = this.emptyStream;
                }
            } else {
                readHierarchy(result, classDesc);
            }
            this.currentObject = null;
            this.currentClass = null;
            if (objectClass != null && classDesc.hasMethodReadResolve()) {
                try {
                    result = classDesc.getMethodReadResolve().invoke(result, (Object[]) null);
                } catch (IllegalAccessException e) {
                } catch (InvocationTargetException ite) {
                    Throwable target = ite.getTargetException();
                    if (target instanceof ObjectStreamException) {
                        throw ((ObjectStreamException) target);
                    } else if (target instanceof Error) {
                        throw ((Error) target);
                    } else {
                        throw ((RuntimeException) target);
                    }
                }
            }
            if (result != null && this.enableResolve) {
                result = resolveObject(result);
            }
            if (registeredResult != result) {
                registerObjectRead(result, newHandle, unshared);
            }
            return result;
        } catch (Throwable th) {
            this.currentObject = null;
            this.currentClass = null;
        }
    }

    private InvalidClassException missingClassDescriptor() throws InvalidClassException {
        throw new InvalidClassException("Read null attempting to read class descriptor for object");
    }

    private Object readNewString(boolean unshared) throws IOException {
        Object result = this.input.readUTF();
        if (this.enableResolve) {
            result = resolveObject(result);
        }
        registerObjectRead(result, nextHandle(), unshared);
        return result;
    }

    private Object readNewLongString(boolean unshared) throws IOException {
        Object result = this.input.decodeUTF((int) this.input.readLong());
        if (this.enableResolve) {
            result = resolveObject(result);
        }
        registerObjectRead(result, nextHandle(), unshared);
        return result;
    }

    public final Object readObject() throws OptionalDataException, ClassNotFoundException, IOException {
        return readObject(false);
    }

    public Object readUnshared() throws IOException, ClassNotFoundException {
        return readObject(true);
    }

    private Object readObject(boolean unshared) throws OptionalDataException, ClassNotFoundException, IOException {
        int i;
        boolean restoreInput = this.primitiveData == this.input;
        if (restoreInput) {
            this.primitiveData = this.emptyStream;
        }
        if (this.subclassOverridingImplementation && !unshared) {
            return readObjectOverride();
        }
        try {
            i = this.nestedLevels + 1;
            this.nestedLevels = i;
            if (i == 1) {
                this.callerClassLoader = VMStack.getClosestUserClassLoader(bootstrapLoader, systemLoader);
            }
            Object result = readNonPrimitiveContent(unshared);
            if (restoreInput) {
                this.primitiveData = this.input;
            }
            int i2 = this.nestedLevels - 1;
            this.nestedLevels = i2;
            if (i2 == 0) {
                this.callerClassLoader = null;
            }
            if (this.nestedLevels != 0 || this.validations == null) {
                return result;
            }
            try {
                for (InputValidationDesc element : this.validations) {
                    element.validator.validateObject();
                }
                return result;
            } finally {
                this.validations = null;
            }
        } catch (Throwable th) {
            i = this.nestedLevels - 1;
            this.nestedLevels = i;
            if (i == 0) {
                this.callerClassLoader = null;
            }
        }
    }

    protected Object readObjectOverride() throws OptionalDataException, ClassNotFoundException, IOException {
        if (this.input == null) {
            return null;
        }
        throw new IOException();
    }

    public short readShort() throws IOException {
        return this.primitiveTypes.readShort();
    }

    protected void readStreamHeader() throws IOException, StreamCorruptedException {
        if (this.input.readShort() != ObjectStreamConstants.STREAM_MAGIC || this.input.readShort() != (short) 5) {
            throw new StreamCorruptedException();
        }
    }

    public int readUnsignedByte() throws IOException {
        return this.primitiveTypes.readUnsignedByte();
    }

    public int readUnsignedShort() throws IOException {
        return this.primitiveTypes.readUnsignedShort();
    }

    public String readUTF() throws IOException {
        return this.primitiveTypes.readUTF();
    }

    private Object registeredObjectRead(int handle) throws InvalidObjectException {
        Object res = this.objectsRead.get(handle - ObjectStreamConstants.baseWireHandle);
        if (res != UNSHARED_OBJ) {
            return res;
        }
        throw new InvalidObjectException("Cannot read back reference to unshared object");
    }

    private void registerObjectRead(Object obj, int handle, boolean unshared) throws IOException {
        if (unshared) {
            obj = UNSHARED_OBJ;
        }
        int index = handle - ObjectStreamConstants.baseWireHandle;
        int size = this.objectsRead.size();
        while (index > size) {
            this.objectsRead.add(null);
            size++;
        }
        if (index == size) {
            this.objectsRead.add(obj);
        } else {
            this.objectsRead.set(index, obj);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void registerValidation(java.io.ObjectInputValidation r10, int r11) throws java.io.NotActiveException, java.io.InvalidObjectException {
        /*
        r9 = this;
        monitor-enter(r9);
        r3 = r9.currentObject;	 Catch:{ all -> 0x000f }
        if (r3 != 0) goto L_0x0012;
    L_0x0005:
        r6 = r9.nestedLevels;	 Catch:{ all -> 0x000f }
        if (r6 != 0) goto L_0x0012;
    L_0x0009:
        r6 = new java.io.NotActiveException;	 Catch:{ all -> 0x000f }
        r6.<init>();	 Catch:{ all -> 0x000f }
        throw r6;	 Catch:{ all -> 0x000f }
    L_0x000f:
        r6 = move-exception;
        monitor-exit(r9);
        throw r6;
    L_0x0012:
        if (r10 != 0) goto L_0x001c;
    L_0x0014:
        r6 = new java.io.InvalidObjectException;	 Catch:{ all -> 0x000f }
        r7 = "Callback object cannot be null";
        r6.<init>(r7);	 Catch:{ all -> 0x000f }
        throw r6;	 Catch:{ all -> 0x000f }
    L_0x001c:
        r1 = new java.io.ObjectInputStream$InputValidationDesc;	 Catch:{ all -> 0x000f }
        r1.<init>();	 Catch:{ all -> 0x000f }
        r1.validator = r10;	 Catch:{ all -> 0x000f }
        r1.priority = r11;	 Catch:{ all -> 0x000f }
        r6 = r9.validations;	 Catch:{ all -> 0x000f }
        if (r6 != 0) goto L_0x0035;
    L_0x0029:
        r6 = 1;
        r6 = new java.io.ObjectInputStream.InputValidationDesc[r6];	 Catch:{ all -> 0x000f }
        r9.validations = r6;	 Catch:{ all -> 0x000f }
        r6 = r9.validations;	 Catch:{ all -> 0x000f }
        r7 = 0;
        r6[r7] = r1;	 Catch:{ all -> 0x000f }
    L_0x0033:
        monitor-exit(r9);
        return;
    L_0x0035:
        r2 = 0;
    L_0x0036:
        r6 = r9.validations;	 Catch:{ all -> 0x000f }
        r6 = r6.length;	 Catch:{ all -> 0x000f }
        if (r2 >= r6) goto L_0x0043;
    L_0x003b:
        r6 = r9.validations;	 Catch:{ all -> 0x000f }
        r5 = r6[r2];	 Catch:{ all -> 0x000f }
        r6 = r5.priority;	 Catch:{ all -> 0x000f }
        if (r11 < r6) goto L_0x0061;
    L_0x0043:
        r4 = r9.validations;	 Catch:{ all -> 0x000f }
        r0 = r4.length;	 Catch:{ all -> 0x000f }
        r6 = r0 + 1;
        r6 = new java.io.ObjectInputStream.InputValidationDesc[r6];	 Catch:{ all -> 0x000f }
        r9.validations = r6;	 Catch:{ all -> 0x000f }
        r6 = 0;
        r7 = r9.validations;	 Catch:{ all -> 0x000f }
        r8 = 0;
        java.lang.System.arraycopy(r4, r6, r7, r8, r2);	 Catch:{ all -> 0x000f }
        r6 = r9.validations;	 Catch:{ all -> 0x000f }
        r7 = r2 + 1;
        r8 = r0 - r2;
        java.lang.System.arraycopy(r4, r2, r6, r7, r8);	 Catch:{ all -> 0x000f }
        r6 = r9.validations;	 Catch:{ all -> 0x000f }
        r6[r2] = r1;	 Catch:{ all -> 0x000f }
        goto L_0x0033;
    L_0x0061:
        r2 = r2 + 1;
        goto L_0x0036;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.io.ObjectInputStream.registerValidation(java.io.ObjectInputValidation, int):void");
    }

    private void resetSeenObjects() {
        this.objectsRead = new ArrayList();
        this.nextHandle = ObjectStreamConstants.baseWireHandle;
        this.primitiveData = this.emptyStream;
    }

    private void resetState() {
        resetSeenObjects();
        this.hasPushbackTC = false;
        this.pushbackTC = (byte) 0;
    }

    protected Class<?> resolveClass(ObjectStreamClass osClass) throws IOException, ClassNotFoundException {
        Class<?> cls = osClass.forClass();
        if (cls != null) {
            return cls;
        }
        String className = osClass.getName();
        cls = (Class) PRIMITIVE_CLASSES.get(className);
        if (cls == null) {
            return Class.forName(className, false, this.callerClassLoader);
        }
        return cls;
    }

    protected Object resolveObject(Object object) throws IOException {
        return object;
    }

    public int skipBytes(int length) throws IOException {
        if (this.input == null) {
            throw new NullPointerException("source stream is null");
        }
        int offset = 0;
        while (offset < length) {
            checkReadPrimitiveTypes();
            long skipped = this.primitiveData.skip((long) (length - offset));
            if (skipped == 0) {
                return offset;
            }
            offset += (int) skipped;
        }
        return length;
    }

    private void verifyAndInit(ObjectStreamClass loadedStreamClass) throws InvalidClassException {
        Object localStreamClass = ObjectStreamClass.lookupStreamClass(loadedStreamClass.forClass());
        if (loadedStreamClass.getSerialVersionUID() != localStreamClass.getSerialVersionUID()) {
            throw new InvalidClassException(loadedStreamClass.getName(), "Incompatible class (SUID): " + loadedStreamClass + " but expected " + localStreamClass);
        }
        if (getBaseName(loadedStreamClass.getName()).equals(getBaseName(localStreamClass.getName()))) {
            loadedStreamClass.initPrivateFields(localStreamClass);
            return;
        }
        throw new InvalidClassException(loadedStreamClass.getName(), String.format("Incompatible class (base name): %s but expected %s", loadedClassBaseName, localClassBaseName));
    }

    private static String getBaseName(String fullName) {
        int k = fullName.lastIndexOf(46);
        return (k == -1 || k == fullName.length() - 1) ? fullName : fullName.substring(k + 1);
    }

    private static void checkedSetSuperClassDesc(ObjectStreamClass desc, ObjectStreamClass superDesc) throws StreamCorruptedException {
        if (desc.equals(superDesc)) {
            throw new StreamCorruptedException();
        }
        desc.setSuperclass(superDesc);
    }
}
