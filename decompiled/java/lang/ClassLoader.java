package java.lang;

import dalvik.system.PathClassLoader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ClassLoader {
    private Map<String, Package> packages;
    private ClassLoader parent;
    public final Map<List<Class<?>>, Class<?>> proxyCache;

    private static class SystemClassLoader {
        public static ClassLoader loader;

        private SystemClassLoader() {
        }

        static {
            loader = ClassLoader.createSystemClassLoader();
        }
    }

    private static ClassLoader createSystemClassLoader() {
        return new PathClassLoader(System.getProperty("java.class.path", "."), BootClassLoader.getInstance());
    }

    public static ClassLoader getSystemClassLoader() {
        return SystemClassLoader.loader;
    }

    public static URL getSystemResource(String resName) {
        return SystemClassLoader.loader.getResource(resName);
    }

    public static Enumeration<URL> getSystemResources(String resName) throws IOException {
        return SystemClassLoader.loader.getResources(resName);
    }

    public static InputStream getSystemResourceAsStream(String resName) {
        return SystemClassLoader.loader.getResourceAsStream(resName);
    }

    protected ClassLoader() {
        this(getSystemClassLoader(), false);
    }

    protected ClassLoader(ClassLoader parentLoader) {
        this(parentLoader, false);
    }

    ClassLoader(ClassLoader parentLoader, boolean nullAllowed) {
        this.packages = new HashMap();
        this.proxyCache = new HashMap();
        if (parentLoader != null || nullAllowed) {
            this.parent = parentLoader;
            return;
        }
        throw new NullPointerException("parentLoader == null && !nullAllowed");
    }

    @Deprecated
    protected final Class<?> defineClass(byte[] classRep, int offset, int length) throws ClassFormatError {
        throw new UnsupportedOperationException("can't load this type of class file");
    }

    protected final Class<?> defineClass(String className, byte[] classRep, int offset, int length) throws ClassFormatError {
        throw new UnsupportedOperationException("can't load this type of class file");
    }

    protected final Class<?> defineClass(String className, byte[] classRep, int offset, int length, ProtectionDomain protectionDomain) throws ClassFormatError {
        throw new UnsupportedOperationException("can't load this type of class file");
    }

    protected final Class<?> defineClass(String name, ByteBuffer b, ProtectionDomain protectionDomain) throws ClassFormatError {
        byte[] temp = new byte[b.remaining()];
        b.get(temp);
        return defineClass(name, temp, 0, temp.length, protectionDomain);
    }

    protected Class<?> findClass(String className) throws ClassNotFoundException {
        throw new ClassNotFoundException(className);
    }

    protected final Class<?> findLoadedClass(String className) {
        ClassLoader loader;
        if (this == BootClassLoader.getInstance()) {
            loader = null;
        } else {
            loader = this;
        }
        return VMClassLoader.findLoadedClass(loader, className);
    }

    protected final Class<?> findSystemClass(String className) throws ClassNotFoundException {
        return Class.forName(className, false, getSystemClassLoader());
    }

    public final ClassLoader getParent() {
        return this.parent;
    }

    public URL getResource(String resName) {
        URL resource = this.parent.getResource(resName);
        if (resource == null) {
            return findResource(resName);
        }
        return resource;
    }

    public Enumeration<URL> getResources(String resName) throws IOException {
        return new TwoEnumerationsInOne(this.parent.getResources(resName), findResources(resName));
    }

    public InputStream getResourceAsStream(String resName) {
        try {
            URL url = getResource(resName);
            if (url != null) {
                return url.openStream();
            }
        } catch (IOException e) {
        }
        return null;
    }

    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return loadClass(className, false);
    }

    protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = findLoadedClass(className);
        if (clazz == null) {
            ClassNotFoundException suppressed = null;
            try {
                clazz = this.parent.loadClass(className, false);
            } catch (ClassNotFoundException e) {
                suppressed = e;
            }
            if (clazz == null) {
                try {
                    clazz = findClass(className);
                } catch (ClassNotFoundException e2) {
                    e2.addSuppressed(suppressed);
                    throw e2;
                }
            }
        }
        return clazz;
    }

    protected final void resolveClass(Class<?> cls) {
    }

    protected URL findResource(String resName) {
        return null;
    }

    protected Enumeration<URL> findResources(String resName) throws IOException {
        return Collections.emptyEnumeration();
    }

    protected String findLibrary(String libName) {
        return null;
    }

    protected Package getPackage(String name) {
        Package packageR;
        synchronized (this.packages) {
            packageR = (Package) this.packages.get(name);
        }
        return packageR;
    }

    protected Package[] getPackages() {
        Package[] result;
        synchronized (this.packages) {
            Collection<Package> col = this.packages.values();
            result = new Package[col.size()];
            col.toArray(result);
        }
        return result;
    }

    protected Package definePackage(String name, String specTitle, String specVersion, String specVendor, String implTitle, String implVersion, String implVendor, URL sealBase) throws IllegalArgumentException {
        Package newPackage;
        synchronized (this.packages) {
            if (this.packages.containsKey(name)) {
                throw new IllegalArgumentException("Package " + name + " already defined");
            }
            newPackage = new Package(name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase);
            this.packages.put(name, newPackage);
        }
        return newPackage;
    }

    protected final void setSigners(Class<?> cls, Object[] signers) {
    }

    public void setClassAssertionStatus(String cname, boolean enable) {
    }

    public void setPackageAssertionStatus(String pname, boolean enable) {
    }

    public void setDefaultAssertionStatus(boolean enable) {
    }

    public void clearAssertionStatus() {
    }
}
