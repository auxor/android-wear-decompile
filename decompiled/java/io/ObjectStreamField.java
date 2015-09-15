package java.io;

import dalvik.bytecode.Opcodes;
import java.lang.ref.WeakReference;
import java.sql.Types;

public class ObjectStreamField implements Comparable<Object> {
    private boolean isDeserialized;
    private String name;
    int offset;
    private Object type;
    private String typeString;
    private boolean unshared;

    public ObjectStreamField(String name, Class<?> cl) {
        if (name == null) {
            throw new NullPointerException("name == null");
        } else if (cl == null) {
            throw new NullPointerException("cl == null");
        } else {
            this.name = name;
            this.type = new WeakReference(cl);
        }
    }

    public ObjectStreamField(String name, Class<?> cl, boolean unshared) {
        if (name == null) {
            throw new NullPointerException("name == null");
        } else if (cl == null) {
            throw new NullPointerException("cl == null");
        } else {
            this.name = name;
            if (cl.getClassLoader() != null) {
                Object cl2 = new WeakReference(cl);
            }
            this.type = cl;
            this.unshared = unshared;
        }
    }

    ObjectStreamField(String signature, String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        this.name = name;
        this.typeString = signature.replace('.', '/').intern();
        defaultResolve();
        this.isDeserialized = true;
    }

    public int compareTo(Object o) {
        ObjectStreamField f = (ObjectStreamField) o;
        boolean thisPrimitive = isPrimitive();
        if (thisPrimitive != f.isPrimitive()) {
            return thisPrimitive ? -1 : 1;
        } else {
            return getName().compareTo(f.getName());
        }
    }

    public String getName() {
        return this.name;
    }

    public int getOffset() {
        return this.offset;
    }

    Class<?> getTypeInternal() {
        if (this.type instanceof WeakReference) {
            return (Class) ((WeakReference) this.type).get();
        }
        return (Class) this.type;
    }

    public Class<?> getType() {
        Class<?> cl = getTypeInternal();
        if (!this.isDeserialized || cl.isPrimitive()) {
            return cl;
        }
        return Object.class;
    }

    public char getTypeCode() {
        return typeCodeOf(getTypeInternal());
    }

    private char typeCodeOf(Class<?> type) {
        if (type == Integer.TYPE) {
            return 'I';
        }
        if (type == Byte.TYPE) {
            return 'B';
        }
        if (type == Character.TYPE) {
            return 'C';
        }
        if (type == Short.TYPE) {
            return 'S';
        }
        if (type == Boolean.TYPE) {
            return 'Z';
        }
        if (type == Long.TYPE) {
            return 'J';
        }
        if (type == Float.TYPE) {
            return 'F';
        }
        if (type == Double.TYPE) {
            return 'D';
        }
        if (type.isArray()) {
            return '[';
        }
        return 'L';
    }

    public String getTypeString() {
        if (isPrimitive()) {
            return null;
        }
        if (this.typeString == null) {
            Class<?> t = getTypeInternal();
            String typeName = t.getName().replace('.', '/');
            this.typeString = (t.isArray() ? typeName : "L" + typeName + ';').intern();
        }
        return this.typeString;
    }

    public boolean isPrimitive() {
        Class<?> t = getTypeInternal();
        return t != null && t.isPrimitive();
    }

    boolean writeField(DataOutputStream out) throws IOException {
        Class<?> t = getTypeInternal();
        out.writeByte(typeCodeOf(t));
        out.writeUTF(this.name);
        return t != null && t.isPrimitive();
    }

    protected void setOffset(int newValue) {
        this.offset = newValue;
    }

    public String toString() {
        return getClass().getName() + '(' + getName() + ':' + getTypeInternal() + ')';
    }

    void resolve(ClassLoader loader) {
        if (this.typeString == null && isPrimitive()) {
            this.typeString = String.valueOf(getTypeCode());
        }
        if (this.typeString.length() != 1 || !defaultResolve()) {
            String className = this.typeString.replace('/', '.');
            if (className.charAt(0) == 'L') {
                className = className.substring(1, className.length() - 1);
            }
            try {
                Class<?> cl = Class.forName(className, false, loader);
                if (cl.getClassLoader() != null) {
                    Object cl2 = new WeakReference(cl);
                }
                this.type = cl;
            } catch (ClassNotFoundException e) {
            }
        }
    }

    public boolean isUnshared() {
        return this.unshared;
    }

    void setUnshared(boolean unshared) {
        this.unshared = unshared;
    }

    private boolean defaultResolve() {
        switch (this.typeString.charAt(0)) {
            case 'B':
                this.type = Byte.TYPE;
                return true;
            case 'C':
                this.type = Character.TYPE;
                return true;
            case Opcodes.OP_AGET /*68*/:
                this.type = Double.TYPE;
                return true;
            case Types.DATALINK /*70*/:
                this.type = Float.TYPE;
                return true;
            case Opcodes.OP_AGET_CHAR /*73*/:
                this.type = Integer.TYPE;
                return true;
            case Opcodes.OP_AGET_SHORT /*74*/:
                this.type = Long.TYPE;
                return true;
            case Opcodes.OP_IGET_WIDE /*83*/:
                this.type = Short.TYPE;
                return true;
            case Opcodes.OP_IPUT_WIDE /*90*/:
                this.type = Boolean.TYPE;
                return true;
            default:
                this.type = Object.class;
                return false;
        }
    }
}
