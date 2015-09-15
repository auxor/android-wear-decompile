package dalvik.system;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class BaseDexClassLoader extends ClassLoader {
    private final DexPathList pathList;

    public BaseDexClassLoader(String dexPath, File optimizedDirectory, String libraryPath, ClassLoader parent) {
        super(parent);
        this.pathList = new DexPathList(this, dexPath, libraryPath, optimizedDirectory);
    }

    protected Class<?> findClass(String name) throws ClassNotFoundException {
        List<Throwable> suppressedExceptions = new ArrayList();
        Class c = this.pathList.findClass(name, suppressedExceptions);
        if (c != null) {
            return c;
        }
        ClassNotFoundException cnfe = new ClassNotFoundException("Didn't find class \"" + name + "\" on path: " + this.pathList);
        for (Throwable t : suppressedExceptions) {
            cnfe.addSuppressed(t);
        }
        throw cnfe;
    }

    protected URL findResource(String name) {
        return this.pathList.findResource(name);
    }

    protected Enumeration<URL> findResources(String name) {
        return this.pathList.findResources(name);
    }

    public String findLibrary(String name) {
        return this.pathList.findLibrary(name);
    }

    protected synchronized Package getPackage(String name) {
        Package packageR = null;
        synchronized (this) {
            if (name != null) {
                if (!name.isEmpty()) {
                    packageR = super.getPackage(name);
                    if (packageR == null) {
                        packageR = definePackage(name, "Unknown", "0.0", "Unknown", "Unknown", "0.0", "Unknown", null);
                    }
                }
            }
        }
        return packageR;
    }

    public String getLdLibraryPath() {
        StringBuilder result = new StringBuilder();
        for (Object directory : this.pathList.getNativeLibraryDirectories()) {
            if (result.length() > 0) {
                result.append(':');
            }
            result.append(directory);
        }
        return result.toString();
    }

    public String toString() {
        return getClass().getName() + "[" + this.pathList + "]";
    }
}
