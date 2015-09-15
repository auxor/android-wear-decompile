package java.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import org.apache.harmony.security.fortress.Services;
import org.w3c.dom.traversal.NodeFilter;

public abstract class Provider extends Properties {
    private static final long serialVersionUID = -4298000515446427739L;
    private transient LinkedHashMap<String, Service> aliasTable;
    private transient LinkedHashMap<Object, Object> changedProperties;
    private String info;
    private transient String lastAlgorithm;
    private transient String lastServiceName;
    private transient Service lastServicesByType;
    private transient Set<Service> lastServicesSet;
    private transient String lastType;
    private String name;
    private transient LinkedHashMap<String, Service> propertyAliasTable;
    private transient LinkedHashMap<String, Service> propertyServiceTable;
    private transient int providerNumber;
    private transient Service returnedService;
    private transient LinkedHashMap<String, Service> serviceTable;
    private double version;
    private transient String versionString;

    public static class Service {
        private static final String ATTR_SUPPORTED_KEY_CLASSES = "SupportedKeyClasses";
        private static final String ATTR_SUPPORTED_KEY_FORMATS = "SupportedKeyFormats";
        private static final HashMap<String, Class<?>> constructorParameterClasses;
        private static final HashMap<String, Boolean> supportsParameterTypes;
        private String algorithm;
        private List<String> aliases;
        private Map<String, String> attributes;
        private String className;
        private Class<?> implementation;
        private Class<?>[] keyClasses;
        private String[] keyFormats;
        private String lastClassName;
        private Provider provider;
        private volatile boolean supportedKeysInitialized;
        private String type;

        static {
            supportsParameterTypes = new HashMap();
            supportsParameterTypes.put("AlgorithmParameterGenerator", Boolean.valueOf(false));
            supportsParameterTypes.put("AlgorithmParameters", Boolean.valueOf(false));
            supportsParameterTypes.put("CertificateFactory", Boolean.valueOf(false));
            supportsParameterTypes.put("CertPathBuilder", Boolean.valueOf(false));
            supportsParameterTypes.put("CertPathValidator", Boolean.valueOf(false));
            supportsParameterTypes.put("CertStore", Boolean.valueOf(false));
            supportsParameterTypes.put("KeyFactory", Boolean.valueOf(false));
            supportsParameterTypes.put("KeyGenerator", Boolean.valueOf(false));
            supportsParameterTypes.put("KeyManagerFactory", Boolean.valueOf(false));
            supportsParameterTypes.put("KeyPairGenerator", Boolean.valueOf(false));
            supportsParameterTypes.put("KeyStore", Boolean.valueOf(false));
            supportsParameterTypes.put("MessageDigest", Boolean.valueOf(false));
            supportsParameterTypes.put("SecretKeyFactory", Boolean.valueOf(false));
            supportsParameterTypes.put("SecureRandom", Boolean.valueOf(false));
            supportsParameterTypes.put("SSLContext", Boolean.valueOf(false));
            supportsParameterTypes.put("TrustManagerFactory", Boolean.valueOf(false));
            supportsParameterTypes.put("Cipher", Boolean.valueOf(true));
            supportsParameterTypes.put("KeyAgreement", Boolean.valueOf(true));
            supportsParameterTypes.put("Mac", Boolean.valueOf(true));
            supportsParameterTypes.put("Signature", Boolean.valueOf(true));
            constructorParameterClasses = new HashMap();
            constructorParameterClasses.put("CertStore", loadClassOrThrow("java.security.cert.CertStoreParameters"));
            constructorParameterClasses.put("AlgorithmParameterGenerator", null);
            constructorParameterClasses.put("AlgorithmParameters", null);
            constructorParameterClasses.put("CertificateFactory", null);
            constructorParameterClasses.put("CertPathBuilder", null);
            constructorParameterClasses.put("CertPathValidator", null);
            constructorParameterClasses.put("KeyFactory", null);
            constructorParameterClasses.put("KeyGenerator", null);
            constructorParameterClasses.put("KeyManagerFactory", null);
            constructorParameterClasses.put("KeyPairGenerator", null);
            constructorParameterClasses.put("KeyStore", null);
            constructorParameterClasses.put("MessageDigest", null);
            constructorParameterClasses.put("SecretKeyFactory", null);
            constructorParameterClasses.put("SecureRandom", null);
            constructorParameterClasses.put("SSLContext", null);
            constructorParameterClasses.put("TrustManagerFactory", null);
            constructorParameterClasses.put("Cipher", null);
            constructorParameterClasses.put("KeyAgreement", null);
            constructorParameterClasses.put("Mac", null);
            constructorParameterClasses.put("Signature", null);
        }

        private static Class<?> loadClassOrThrow(String className) {
            try {
                return Provider.class.getClassLoader().loadClass(className);
            } catch (Object e) {
                throw new AssertionError(e);
            }
        }

        public Service(Provider provider, String type, String algorithm, String className, List<String> aliases, Map<String, String> attributes) {
            if (provider == null) {
                throw new NullPointerException("provider == null");
            } else if (type == null) {
                throw new NullPointerException("type == null");
            } else if (algorithm == null) {
                throw new NullPointerException("algorithm == null");
            } else if (className == null) {
                throw new NullPointerException("className == null");
            } else {
                this.provider = provider;
                this.type = type;
                this.algorithm = algorithm;
                this.className = className;
                if (aliases != null && aliases.size() == 0) {
                    aliases = Collections.emptyList();
                }
                this.aliases = aliases;
                if (attributes != null && attributes.size() == 0) {
                    attributes = Collections.emptyMap();
                }
                this.attributes = attributes;
            }
        }

        void addAlias(String alias) {
            if (this.aliases == null || this.aliases.size() == 0) {
                this.aliases = new ArrayList();
            }
            this.aliases.add(alias);
        }

        void putAttribute(String name, String value) {
            if (this.attributes == null || this.attributes.size() == 0) {
                this.attributes = new HashMap();
            }
            this.attributes.put(name, value);
        }

        public final String getType() {
            return this.type;
        }

        public final String getAlgorithm() {
            return this.algorithm;
        }

        public final Provider getProvider() {
            return this.provider;
        }

        public final String getClassName() {
            return this.className;
        }

        public final String getAttribute(String name) {
            if (name == null) {
                throw new NullPointerException("name == null");
            } else if (this.attributes == null) {
                return null;
            } else {
                return (String) this.attributes.get(name);
            }
        }

        List<String> getAliases() {
            if (this.aliases == null) {
                this.aliases = new ArrayList(0);
            }
            return this.aliases;
        }

        public Object newInstance(Object constructorParameter) throws NoSuchAlgorithmException {
            if (this.implementation == null || !this.className.equals(this.lastClassName)) {
                ClassLoader cl = this.provider.getClass().getClassLoader();
                if (cl == null) {
                    cl = ClassLoader.getSystemClassLoader();
                }
                try {
                    this.implementation = Class.forName(this.className, true, cl);
                    this.lastClassName = this.className;
                } catch (Object e) {
                    throw new NoSuchAlgorithmException(this.type + " " + this.algorithm + " implementation not found: " + e);
                }
            }
            if (constructorParameterClasses.containsKey(this.type)) {
                if (constructorParameter == null) {
                    return newInstanceNoParameter();
                }
                Class<?> expectedClass = (Class) constructorParameterClasses.get(this.type);
                if (expectedClass == null) {
                    throw new IllegalArgumentException("Constructor parameter not supported for " + this.type);
                } else if (expectedClass.isAssignableFrom(constructorParameter.getClass())) {
                    return newInstanceWithParameter(constructorParameter, expectedClass);
                } else {
                    throw new IllegalArgumentException("Expecting constructor parameter of type " + expectedClass.getName() + " but was " + constructorParameter.getClass().getName());
                }
            } else if (constructorParameter == null) {
                return newInstanceNoParameter();
            } else {
                return newInstanceWithParameter(constructorParameter, constructorParameter.getClass());
            }
        }

        private Object newInstanceWithParameter(Object constructorParameter, Class<?> parameterClass) throws NoSuchAlgorithmException {
            try {
                return this.implementation.getConstructor(parameterClass).newInstance(constructorParameter);
            } catch (Exception e) {
                throw new NoSuchAlgorithmException(this.type + " " + this.algorithm + " implementation not found", e);
            }
        }

        private Object newInstanceNoParameter() throws NoSuchAlgorithmException {
            try {
                return this.implementation.newInstance();
            } catch (Exception e) {
                throw new NoSuchAlgorithmException(this.type + " " + this.algorithm + " implementation not found", e);
            }
        }

        public boolean supportsParameter(Object parameter) {
            Boolean supportsParameter = (Boolean) supportsParameterTypes.get(this.type);
            if (supportsParameter == null) {
                return true;
            }
            if (!supportsParameter.booleanValue()) {
                throw new InvalidParameterException("Cannot use a parameter with " + this.type);
            } else if (parameter == null || (parameter instanceof Key)) {
                ensureSupportedKeysInitialized();
                if (this.keyClasses == null && this.keyFormats == null) {
                    return true;
                }
                if (parameter == null) {
                    return false;
                }
                Key keyParam = (Key) parameter;
                if (this.keyClasses != null && isInArray(this.keyClasses, keyParam.getClass())) {
                    return true;
                }
                if (this.keyFormats == null || !isInArray(this.keyFormats, keyParam.getFormat())) {
                    return false;
                }
                return true;
            } else {
                throw new InvalidParameterException("Parameter should be of type Key");
            }
        }

        private void ensureSupportedKeysInitialized() {
            if (!this.supportedKeysInitialized) {
                String supportedClassesString = getAttribute(ATTR_SUPPORTED_KEY_CLASSES);
                if (supportedClassesString != null) {
                    String[] keyClassNames = supportedClassesString.split("\\|");
                    ArrayList<Class<?>> supportedClassList = new ArrayList(keyClassNames.length);
                    ClassLoader classLoader = getProvider().getClass().getClassLoader();
                    for (String keyClassName : keyClassNames) {
                        try {
                            Class<?> keyClass = classLoader.loadClass(keyClassName);
                            if (Key.class.isAssignableFrom(keyClass)) {
                                supportedClassList.add(keyClass);
                            }
                        } catch (ClassNotFoundException e) {
                        }
                    }
                    this.keyClasses = (Class[]) supportedClassList.toArray(new Class[supportedClassList.size()]);
                }
                String supportedFormatString = getAttribute(ATTR_SUPPORTED_KEY_FORMATS);
                if (supportedFormatString != null) {
                    this.keyFormats = supportedFormatString.split("\\|");
                }
                this.supportedKeysInitialized = true;
            }
        }

        private static <T> boolean isInArray(T[] itemList, T target) {
            if (target == null) {
                return false;
            }
            for (T item : itemList) {
                if (target.equals(item)) {
                    return true;
                }
            }
            return false;
        }

        private static boolean isInArray(Class<?>[] itemList, Class<?> target) {
            if (target == null) {
                return false;
            }
            for (Class<?> item : itemList) {
                if (item.isAssignableFrom(target)) {
                    return true;
                }
            }
            return false;
        }

        public String toString() {
            String result = "Provider " + this.provider.getName() + " Service " + this.type + "." + this.algorithm + " " + this.className;
            if (this.aliases != null) {
                result = result + "\nAliases " + this.aliases.toString();
            }
            if (this.attributes != null) {
                return result + "\nAttributes " + this.attributes.toString();
            }
            return result;
        }
    }

    protected Provider(String name, double version, String info) {
        this.providerNumber = -1;
        this.name = name;
        this.version = version;
        this.info = info;
        this.versionString = String.valueOf(version);
        putProviderInfo();
    }

    public String getName() {
        return this.name;
    }

    public double getVersion() {
        return this.version;
    }

    public String getInfo() {
        return this.info;
    }

    public String toString() {
        return this.name + " version " + this.version;
    }

    public synchronized void clear() {
        super.clear();
        if (this.serviceTable != null) {
            this.serviceTable.clear();
        }
        if (this.propertyServiceTable != null) {
            this.propertyServiceTable.clear();
        }
        if (this.aliasTable != null) {
            this.aliasTable.clear();
        }
        if (this.propertyAliasTable != null) {
            this.propertyAliasTable.clear();
        }
        this.changedProperties = null;
        putProviderInfo();
        if (this.providerNumber != -1) {
            Services.setNeedRefresh();
        }
        servicesChanged();
    }

    public synchronized void load(InputStream inStream) throws IOException {
        Properties tmp = new Properties();
        tmp.load(inStream);
        myPutAll(tmp);
    }

    public synchronized void putAll(Map<?, ?> t) {
        myPutAll(t);
    }

    private void myPutAll(Map<?, ?> t) {
        if (this.changedProperties == null) {
            this.changedProperties = new LinkedHashMap();
        }
        for (Entry<?, ?> entry : t.entrySet()) {
            Object key = entry.getKey();
            if (!(key instanceof String) || !((String) key).startsWith("Provider.")) {
                Object value = entry.getValue();
                super.put(key, value);
                if (this.changedProperties.remove(key) == null) {
                    removeFromPropertyServiceTable(key);
                }
                this.changedProperties.put(key, value);
            }
        }
        if (this.providerNumber != -1) {
            Services.setNeedRefresh();
        }
    }

    public synchronized Set<Entry<Object, Object>> entrySet() {
        return Collections.unmodifiableSet(super.entrySet());
    }

    public Set<Object> keySet() {
        return Collections.unmodifiableSet(super.keySet());
    }

    public Collection<Object> values() {
        return Collections.unmodifiableCollection(super.values());
    }

    public synchronized Object put(Object key, Object value) {
        Object obj;
        if (key instanceof String) {
            if (((String) key).startsWith("Provider.")) {
                obj = null;
            }
        }
        if (this.providerNumber != -1) {
            Services.setNeedRefresh();
        }
        if (this.changedProperties != null && this.changedProperties.remove(key) == null) {
            removeFromPropertyServiceTable(key);
        }
        if (this.changedProperties == null) {
            this.changedProperties = new LinkedHashMap();
        }
        this.changedProperties.put(key, value);
        obj = super.put(key, value);
        return obj;
    }

    public synchronized Object remove(Object key) {
        Object obj;
        if (key instanceof String) {
            if (((String) key).startsWith("Provider.")) {
                obj = null;
            }
        }
        if (this.providerNumber != -1) {
            Services.setNeedRefresh();
        }
        if (this.changedProperties != null && this.changedProperties.remove(key) == null) {
            removeFromPropertyServiceTable(key);
            if (this.changedProperties.size() == 0) {
                this.changedProperties = null;
            }
        }
        obj = super.remove(key);
        return obj;
    }

    boolean implementsAlg(String serv, String alg, String attribute, String val) {
        String servAlg = serv + "." + alg;
        String prop = getPropertyIgnoreCase(servAlg);
        if (prop == null) {
            alg = getPropertyIgnoreCase("Alg.Alias." + servAlg);
            if (alg != null) {
                servAlg = serv + "." + alg;
                prop = getPropertyIgnoreCase(servAlg);
            }
        }
        if (prop == null) {
            return false;
        }
        if (attribute == null) {
            return true;
        }
        return checkAttribute(servAlg, attribute, val);
    }

    private boolean checkAttribute(String servAlg, String attribute, String val) {
        String attributeValue = getPropertyIgnoreCase(servAlg + ' ' + attribute);
        if (attributeValue != null) {
            if (attribute.equalsIgnoreCase("KeySize")) {
                if (Integer.parseInt(attributeValue) >= Integer.parseInt(val)) {
                    return true;
                }
            } else if (attributeValue.equalsIgnoreCase(val)) {
                return true;
            }
        }
        return false;
    }

    void setProviderNumber(int n) {
        this.providerNumber = n;
    }

    int getProviderNumber() {
        return this.providerNumber;
    }

    synchronized Service getService(String type) {
        Service service;
        updatePropertyServiceTable();
        if (this.lastServicesByType == null || !type.equals(this.lastType)) {
            for (Service service2 : getServices()) {
                if (type.equals(service2.type)) {
                    this.lastType = type;
                    this.lastServicesByType = service2;
                    break;
                }
            }
            service2 = null;
        } else {
            service2 = this.lastServicesByType;
        }
        return service2;
    }

    public synchronized Service getService(String type, String algorithm) {
        Service service;
        if (type == null) {
            throw new NullPointerException("type == null");
        } else if (algorithm == null) {
            throw new NullPointerException("algorithm == null");
        } else if (type.equals(this.lastServiceName) && algorithm.equalsIgnoreCase(this.lastAlgorithm)) {
            service = this.returnedService;
        } else {
            Object obj;
            String key = key(type, algorithm);
            Object o = null;
            if (this.serviceTable != null) {
                o = this.serviceTable.get(key);
            }
            if (o != null || this.aliasTable == null) {
                obj = o;
            } else {
                obj = this.aliasTable.get(key);
            }
            if (obj == null) {
                updatePropertyServiceTable();
            }
            if (obj == null && this.propertyServiceTable != null) {
                obj = this.propertyServiceTable.get(key);
            }
            if (obj == null && this.propertyAliasTable != null) {
                obj = this.propertyAliasTable.get(key);
            }
            if (obj != null) {
                this.lastServiceName = type;
                this.lastAlgorithm = algorithm;
                this.returnedService = (Service) obj;
                service = this.returnedService;
            } else {
                service = null;
            }
        }
        return service;
    }

    public synchronized Set<Service> getServices() {
        Set<Service> set;
        updatePropertyServiceTable();
        if (this.lastServicesSet != null) {
            set = this.lastServicesSet;
        } else {
            if (this.serviceTable != null) {
                this.lastServicesSet = new LinkedHashSet(this.serviceTable.values());
            } else {
                this.lastServicesSet = new LinkedHashSet();
            }
            if (this.propertyServiceTable != null) {
                this.lastServicesSet.addAll(this.propertyServiceTable.values());
            }
            this.lastServicesSet = Collections.unmodifiableSet(this.lastServicesSet);
            set = this.lastServicesSet;
        }
        return set;
    }

    protected synchronized void putService(Service s) {
        if (s == null) {
            throw new NullPointerException("s == null");
        } else if (!"Provider".equals(s.getType())) {
            servicesChanged();
            if (this.serviceTable == null) {
                this.serviceTable = new LinkedHashMap((int) NodeFilter.SHOW_COMMENT);
            }
            this.serviceTable.put(key(s.type, s.algorithm), s);
            if (s.aliases != null) {
                if (this.aliasTable == null) {
                    this.aliasTable = new LinkedHashMap((int) NodeFilter.SHOW_DOCUMENT);
                }
                for (String alias : s.getAliases()) {
                    this.aliasTable.put(key(s.type, alias), s);
                }
            }
            serviceInfoToProperties(s);
        }
    }

    protected synchronized void removeService(Service s) {
        if (s == null) {
            throw new NullPointerException("s == null");
        }
        servicesChanged();
        if (this.serviceTable != null) {
            this.serviceTable.remove(key(s.type, s.algorithm));
        }
        if (!(this.aliasTable == null || s.aliases == null)) {
            for (String alias : s.getAliases()) {
                this.aliasTable.remove(key(s.type, alias));
            }
        }
        serviceInfoFromProperties(s);
    }

    private void serviceInfoToProperties(Service s) {
        super.put(s.type + "." + s.algorithm, s.className);
        if (s.aliases != null) {
            for (String str : s.aliases) {
                super.put("Alg.Alias." + s.type + "." + str, s.algorithm);
            }
        }
        if (s.attributes != null) {
            for (Entry<String, String> entry : s.attributes.entrySet()) {
                super.put(s.type + "." + s.algorithm + " " + ((String) entry.getKey()), entry.getValue());
            }
        }
        if (this.providerNumber != -1) {
            Services.setNeedRefresh();
        }
    }

    private void serviceInfoFromProperties(Service s) {
        super.remove(s.type + "." + s.algorithm);
        if (s.aliases != null) {
            for (String str : s.aliases) {
                super.remove("Alg.Alias." + s.type + "." + str);
            }
        }
        if (s.attributes != null) {
            for (Entry<String, String> entry : s.attributes.entrySet()) {
                super.remove(s.type + "." + s.algorithm + " " + ((String) entry.getKey()));
            }
        }
        if (this.providerNumber != -1) {
            Services.setNeedRefresh();
        }
    }

    private void removeFromPropertyServiceTable(Object key) {
        if (key != null && (key instanceof String)) {
            String k = (String) key;
            if (!k.startsWith("Provider.")) {
                int i;
                String serviceName;
                if (k.startsWith("Alg.Alias.")) {
                    String service_alias = k.substring(10);
                    i = service_alias.indexOf(46);
                    serviceName = service_alias.substring(0, i);
                    Object aliasName = service_alias.substring(i + 1);
                    if (this.propertyAliasTable != null) {
                        this.propertyAliasTable.remove(key(serviceName, aliasName));
                    }
                    if (this.propertyServiceTable != null) {
                        for (Service s : this.propertyServiceTable.values()) {
                            if (s.aliases.contains(aliasName)) {
                                s.aliases.remove(aliasName);
                                return;
                            }
                        }
                        return;
                    }
                    return;
                }
                int j = k.indexOf(46);
                if (j != -1) {
                    i = k.indexOf(32);
                    String algorithm;
                    if (i == -1) {
                        serviceName = k.substring(0, j);
                        algorithm = k.substring(j + 1);
                        if (this.propertyServiceTable != null) {
                            Service ser = (Service) this.propertyServiceTable.remove(key(serviceName, algorithm));
                            if (ser != null && this.propertyAliasTable != null && ser.aliases != null) {
                                for (String alias : ser.aliases) {
                                    this.propertyAliasTable.remove(key(serviceName, alias));
                                }
                                return;
                            }
                            return;
                        }
                        return;
                    }
                    String attribute = k.substring(i + 1);
                    serviceName = k.substring(0, j);
                    algorithm = k.substring(j + 1, i);
                    if (this.propertyServiceTable != null) {
                        Object o = this.propertyServiceTable.get(key(serviceName, algorithm));
                        if (o != null) {
                            ((Service) o).attributes.remove(attribute);
                        }
                    }
                }
            }
        }
    }

    private void updatePropertyServiceTable() {
        if (this.changedProperties != null && !this.changedProperties.isEmpty()) {
            for (Entry<Object, Object> entry : this.changedProperties.entrySet()) {
                String _key = entry.getKey();
                String _value = entry.getValue();
                if (_key != null && _value != null && (_key instanceof String) && (_value instanceof String)) {
                    String key = _key;
                    String value = _value;
                    if (!key.startsWith("Provider")) {
                        int i;
                        String serviceName;
                        String algorithm;
                        String propertyServiceTableKey;
                        String className;
                        if (key.startsWith("Alg.Alias.")) {
                            String service_alias = key.substring(10);
                            i = service_alias.indexOf(46);
                            serviceName = service_alias.substring(0, i);
                            String aliasName = service_alias.substring(i + 1);
                            algorithm = value;
                            propertyServiceTableKey = key(serviceName, algorithm);
                            Service o = null;
                            if (this.propertyServiceTable == null) {
                                this.propertyServiceTable = new LinkedHashMap((int) NodeFilter.SHOW_COMMENT);
                            } else {
                                o = this.propertyServiceTable.get(propertyServiceTableKey);
                            }
                            Service s;
                            if (o != null) {
                                s = o;
                                s.addAlias(aliasName);
                                if (this.propertyAliasTable == null) {
                                    this.propertyAliasTable = new LinkedHashMap((int) NodeFilter.SHOW_DOCUMENT);
                                }
                                this.propertyAliasTable.put(key(serviceName, aliasName), s);
                            } else {
                                className = (String) this.changedProperties.get(serviceName + "." + algorithm);
                                if (className != null) {
                                    List<String> l = new ArrayList();
                                    l.add(aliasName);
                                    s = new Service(this, serviceName, algorithm, className, l, new HashMap());
                                    this.propertyServiceTable.put(propertyServiceTableKey, s);
                                    if (this.propertyAliasTable == null) {
                                        this.propertyAliasTable = new LinkedHashMap((int) NodeFilter.SHOW_DOCUMENT);
                                    }
                                    this.propertyAliasTable.put(key(serviceName, aliasName), s);
                                }
                            }
                        } else {
                            int j = key.indexOf(46);
                            if (j != -1) {
                                i = key.indexOf(32);
                                Object obj;
                                if (i == -1) {
                                    serviceName = key.substring(0, j);
                                    algorithm = key.substring(j + 1);
                                    propertyServiceTableKey = key(serviceName, algorithm);
                                    obj = null;
                                    if (this.propertyServiceTable != null) {
                                        obj = this.propertyServiceTable.get(propertyServiceTableKey);
                                    }
                                    if (obj != null) {
                                        ((Service) obj).className = value;
                                    } else {
                                        Service service = new Service(this, serviceName, algorithm, value, Collections.emptyList(), Collections.emptyMap());
                                        if (this.propertyServiceTable == null) {
                                            this.propertyServiceTable = new LinkedHashMap((int) NodeFilter.SHOW_COMMENT);
                                        }
                                        this.propertyServiceTable.put(propertyServiceTableKey, service);
                                    }
                                } else {
                                    serviceName = key.substring(0, j);
                                    algorithm = key.substring(j + 1, i);
                                    String attribute = key.substring(i + 1);
                                    propertyServiceTableKey = key(serviceName, algorithm);
                                    obj = null;
                                    if (this.propertyServiceTable != null) {
                                        obj = this.propertyServiceTable.get(propertyServiceTableKey);
                                    }
                                    if (obj != null) {
                                        ((Service) obj).putAttribute(attribute, value);
                                    } else {
                                        className = (String) this.changedProperties.get(serviceName + "." + algorithm);
                                        if (className != null) {
                                            Map<String, String> m = new HashMap();
                                            m.put(attribute, value);
                                            Service service2 = new Service(this, serviceName, algorithm, className, new ArrayList(), m);
                                            if (this.propertyServiceTable == null) {
                                                this.propertyServiceTable = new LinkedHashMap((int) NodeFilter.SHOW_COMMENT);
                                            }
                                            this.propertyServiceTable.put(propertyServiceTableKey, service2);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            servicesChanged();
            this.changedProperties = null;
        }
    }

    private void servicesChanged() {
        this.lastServicesByType = null;
        this.lastServiceName = null;
        this.lastServicesSet = null;
    }

    private void putProviderInfo() {
        super.put("Provider.id name", this.name != null ? this.name : "null");
        super.put("Provider.id version", this.versionString);
        super.put("Provider.id info", this.info != null ? this.info : "null");
        super.put("Provider.id className", getClass().getName());
    }

    private String getPropertyIgnoreCase(String key) {
        String res = getProperty(key);
        if (res != null) {
            return res;
        }
        Enumeration<?> e = propertyNames();
        while (e.hasMoreElements()) {
            String propertyName = (String) e.nextElement();
            if (key.equalsIgnoreCase(propertyName)) {
                return getProperty(propertyName);
            }
        }
        return null;
    }

    private static String key(String type, String algorithm) {
        return type + '.' + algorithm.toUpperCase(Locale.US);
    }

    private void readObject(ObjectInputStream in) throws NotActiveException, IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.versionString = String.valueOf(this.version);
        this.providerNumber = -1;
    }
}
