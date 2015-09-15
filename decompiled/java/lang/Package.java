package java.lang;

import dalvik.system.VMStack;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.net.URL;

public class Package implements AnnotatedElement {
    private static final Annotation[] NO_ANNOTATIONS;
    private final String implTitle;
    private final String implVendor;
    private final String implVersion;
    private final String name;
    private final URL sealBase;
    private final String specTitle;
    private final String specVendor;
    private final String specVersion;

    static {
        NO_ANNOTATIONS = new Annotation[0];
    }

    Package(String name, String specTitle, String specVersion, String specVendor, String implTitle, String implVersion, String implVendor, URL sealBase) {
        this.name = name;
        this.specTitle = specTitle;
        this.specVersion = specVersion;
        this.specVendor = specVendor;
        this.implTitle = implTitle;
        this.implVersion = implVersion;
        this.implVendor = implVendor;
        this.sealBase = sealBase;
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        for (Annotation annotation : getAnnotations()) {
            if (annotationType.isInstance(annotation)) {
                return annotation;
            }
        }
        return null;
    }

    public Annotation[] getAnnotations() {
        try {
            return Class.forName(getName() + ".package-info").getAnnotations();
        } catch (Exception e) {
            return NO_ANNOTATIONS;
        }
    }

    public Annotation[] getDeclaredAnnotations() {
        return getAnnotations();
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return getAnnotation(annotationType) != null;
    }

    public String getImplementationTitle() {
        return this.implTitle;
    }

    public String getImplementationVendor() {
        return this.implVendor;
    }

    public String getImplementationVersion() {
        return this.implVersion;
    }

    public String getName() {
        return this.name;
    }

    public static Package getPackage(String packageName) {
        ClassLoader classloader = VMStack.getCallingClassLoader();
        if (classloader == null) {
            classloader = ClassLoader.getSystemClassLoader();
        }
        return classloader.getPackage(packageName);
    }

    public static Package[] getPackages() {
        ClassLoader classloader = VMStack.getCallingClassLoader();
        if (classloader == null) {
            classloader = ClassLoader.getSystemClassLoader();
        }
        return classloader.getPackages();
    }

    public String getSpecificationTitle() {
        return this.specTitle;
    }

    public String getSpecificationVendor() {
        return this.specVendor;
    }

    public String getSpecificationVersion() {
        return this.specVersion;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public boolean isCompatibleWith(String version) throws NumberFormatException {
        String[] requested = version.split("\\.");
        String[] provided = this.specVersion.split("\\.");
        for (int i = 0; i < Math.min(requested.length, provided.length); i++) {
            int reqNum = Integer.parseInt(requested[i]);
            int provNum = Integer.parseInt(provided[i]);
            if (reqNum > provNum) {
                return false;
            }
            if (reqNum < provNum) {
                return true;
            }
        }
        if (requested.length <= provided.length) {
            return true;
        }
        return false;
    }

    public boolean isSealed() {
        return this.sealBase != null;
    }

    public boolean isSealed(URL url) {
        return this.sealBase != null && this.sealBase.sameFile(url);
    }

    public String toString() {
        return "package " + this.name;
    }
}
