package android.app;

import android.os.Trace;
import android.util.ArrayMap;
import dalvik.system.PathClassLoader;

class ApplicationLoaders {
    private static final ApplicationLoaders gApplicationLoaders;
    private final ArrayMap<String, ClassLoader> mLoaders;

    ApplicationLoaders() {
        this.mLoaders = new ArrayMap();
    }

    public static ApplicationLoaders getDefault() {
        return gApplicationLoaders;
    }

    public ClassLoader getClassLoader(String zip, String libPath, ClassLoader parent) {
        ClassLoader baseParent = ClassLoader.getSystemClassLoader().getParent();
        synchronized (this.mLoaders) {
            if (parent == null) {
                parent = baseParent;
            }
            if (parent == baseParent) {
                ClassLoader loader = (ClassLoader) this.mLoaders.get(zip);
                if (loader != null) {
                    return loader;
                }
                Trace.traceBegin(64, zip);
                ClassLoader pathClassloader = new PathClassLoader(zip, libPath, parent);
                Trace.traceEnd(64);
                this.mLoaders.put(zip, pathClassloader);
                return pathClassloader;
            }
            Trace.traceBegin(64, zip);
            pathClassloader = new PathClassLoader(zip, parent);
            Trace.traceEnd(64);
            return pathClassloader;
        }
    }

    static {
        gApplicationLoaders = new ApplicationLoaders();
    }
}
