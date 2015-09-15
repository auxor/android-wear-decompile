package java.util.jar;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Attributes implements Cloneable, Map<Object, Object> {
    protected Map<Object, Object> map;

    public static class Name {
        public static final Name CLASS_PATH;
        public static final Name CONTENT_TYPE;
        public static final Name EXTENSION_INSTALLATION;
        public static final Name EXTENSION_LIST;
        public static final Name EXTENSION_NAME;
        public static final Name IMPLEMENTATION_TITLE;
        public static final Name IMPLEMENTATION_URL;
        public static final Name IMPLEMENTATION_VENDOR;
        public static final Name IMPLEMENTATION_VENDOR_ID;
        public static final Name IMPLEMENTATION_VERSION;
        public static final Name MAIN_CLASS;
        public static final Name MANIFEST_VERSION;
        public static final Name NAME;
        public static final Name SEALED;
        public static final Name SIGNATURE_VERSION;
        public static final Name SPECIFICATION_TITLE;
        public static final Name SPECIFICATION_VENDOR;
        public static final Name SPECIFICATION_VERSION;
        private final String name;

        static {
            CLASS_PATH = new Name("Class-Path");
            MANIFEST_VERSION = new Name("Manifest-Version");
            MAIN_CLASS = new Name("Main-Class");
            SIGNATURE_VERSION = new Name("Signature-Version");
            CONTENT_TYPE = new Name("Content-Type");
            SEALED = new Name("Sealed");
            IMPLEMENTATION_TITLE = new Name("Implementation-Title");
            IMPLEMENTATION_VERSION = new Name("Implementation-Version");
            IMPLEMENTATION_VENDOR = new Name("Implementation-Vendor");
            SPECIFICATION_TITLE = new Name("Specification-Title");
            SPECIFICATION_VERSION = new Name("Specification-Version");
            SPECIFICATION_VENDOR = new Name("Specification-Vendor");
            EXTENSION_LIST = new Name("Extension-List");
            EXTENSION_NAME = new Name("Extension-Name");
            EXTENSION_INSTALLATION = new Name("Extension-Installation");
            IMPLEMENTATION_VENDOR_ID = new Name("Implementation-Vendor-Id");
            IMPLEMENTATION_URL = new Name("Implementation-URL");
            NAME = new Name("Name");
        }

        public Name(String name) {
            if (name.isEmpty() || name.length() > 70) {
                throw new IllegalArgumentException(name);
            }
            for (int i = 0; i < name.length(); i++) {
                char ch = name.charAt(i);
                if ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ch != '_' && ch != '-' && (ch < '0' || ch > '9'))) {
                    throw new IllegalArgumentException(name);
                }
            }
            this.name = name;
        }

        String getName() {
            return this.name;
        }

        public boolean equals(Object object) {
            return (object instanceof Name) && ((Name) object).name.equalsIgnoreCase(this.name);
        }

        public int hashCode() {
            return this.name.toLowerCase(Locale.US).hashCode();
        }

        public String toString() {
            return this.name;
        }
    }

    public Attributes() {
        this.map = new HashMap();
    }

    public Attributes(Attributes attrib) {
        this.map = (Map) ((HashMap) attrib.map).clone();
    }

    public Attributes(int size) {
        this.map = new HashMap(size);
    }

    public void clear() {
        this.map.clear();
    }

    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    public Set<Entry<Object, Object>> entrySet() {
        return this.map.entrySet();
    }

    public Object get(Object key) {
        return this.map.get(key);
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public Set<Object> keySet() {
        return this.map.keySet();
    }

    public Object put(Object key, Object value) {
        return this.map.put((Name) key, (String) value);
    }

    public void putAll(Map<?, ?> attrib) {
        if (attrib == null) {
            throw new NullPointerException("attrib == null");
        } else if (attrib instanceof Attributes) {
            this.map.putAll(attrib);
        } else {
            throw new ClassCastException(attrib.getClass().getName() + " not an Attributes");
        }
    }

    public Object remove(Object key) {
        return this.map.remove(key);
    }

    public int size() {
        return this.map.size();
    }

    public Collection<Object> values() {
        return this.map.values();
    }

    public Object clone() {
        try {
            Attributes clone = (Attributes) super.clone();
            clone.map = (Map) ((HashMap) this.map).clone();
            return clone;
        } catch (Object e) {
            throw new AssertionError(e);
        }
    }

    public int hashCode() {
        return this.map.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Attributes) {
            return this.map.equals(((Attributes) obj).map);
        }
        return false;
    }

    public String getValue(Name name) {
        return (String) this.map.get(name);
    }

    public String getValue(String name) {
        return getValue(new Name(name));
    }

    public String putValue(String name, String value) {
        return (String) this.map.put(new Name(name), value);
    }
}
