package java.lang;

import android.system.OsConstants;
import dalvik.system.BaseDexClassLoader;
import dalvik.system.VMDebug;
import dalvik.system.VMStack;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.FinalizerReference;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import libcore.io.IoUtils;
import libcore.io.Libcore;
import libcore.util.EmptyArray;

public class Runtime {
    private static boolean finalizeOnExit;
    private static final Runtime mRuntime;
    private final String[] mLibPaths;
    private List<Thread> shutdownHooks;
    private boolean shuttingDown;
    private boolean tracingMethods;

    private static native void nativeExit(int i);

    private static native String nativeLoad(String str, ClassLoader classLoader, String str2);

    public native long freeMemory();

    public native void gc();

    public native long maxMemory();

    public native long totalMemory();

    static {
        mRuntime = new Runtime();
    }

    private static String[] initLibPaths() {
        String javaLibraryPath = System.getProperty("java.library.path");
        if (javaLibraryPath == null) {
            return EmptyArray.STRING;
        }
        String[] paths = javaLibraryPath.split(":");
        for (int i = 0; i < paths.length; i++) {
            if (!paths[i].endsWith("/")) {
                paths[i] = paths[i] + "/";
            }
        }
        return paths;
    }

    private Runtime() {
        this.mLibPaths = initLibPaths();
        this.shutdownHooks = new ArrayList();
    }

    public Process exec(String[] progArray) throws IOException {
        return exec(progArray, null, null);
    }

    public Process exec(String[] progArray, String[] envp) throws IOException {
        return exec(progArray, envp, null);
    }

    public Process exec(String[] progArray, String[] envp, File directory) throws IOException {
        return ProcessManager.getInstance().exec(progArray, envp, directory, false);
    }

    public Process exec(String prog) throws IOException {
        return exec(prog, null, null);
    }

    public Process exec(String prog, String[] envp) throws IOException {
        return exec(prog, envp, null);
    }

    public Process exec(String prog, String[] envp, File directory) throws IOException {
        if (prog == null) {
            throw new NullPointerException("prog == null");
        } else if (prog.isEmpty()) {
            throw new IllegalArgumentException("prog is empty");
        } else {
            StringTokenizer tokenizer = new StringTokenizer(prog);
            int length = tokenizer.countTokens();
            String[] progArray = new String[length];
            for (int i = 0; i < length; i++) {
                progArray[i] = tokenizer.nextToken();
            }
            return exec(progArray, envp, directory);
        }
    }

    public void exit(int code) {
        synchronized (this) {
            if (!this.shuttingDown) {
                this.shuttingDown = true;
                synchronized (this.shutdownHooks) {
                    Thread[] hooks = new Thread[this.shutdownHooks.size()];
                    this.shutdownHooks.toArray(hooks);
                }
                for (Thread hook : hooks) {
                    hook.start();
                }
                for (Thread hook2 : hooks) {
                    try {
                        hook2.join();
                    } catch (InterruptedException e) {
                    }
                }
                if (finalizeOnExit) {
                    runFinalization();
                }
                nativeExit(code);
            }
        }
    }

    public static Runtime getRuntime() {
        return mRuntime;
    }

    public void load(String absolutePath) {
        load(absolutePath, VMStack.getCallingClassLoader());
    }

    void load(String absolutePath, ClassLoader loader) {
        if (absolutePath == null) {
            throw new NullPointerException("absolutePath == null");
        }
        String error = doLoad(absolutePath, loader);
        if (error != null) {
            throw new UnsatisfiedLinkError(error);
        }
    }

    public void loadLibrary(String nickname) {
        loadLibrary(nickname, VMStack.getCallingClassLoader());
    }

    void loadLibrary(String libraryName, ClassLoader loader) {
        String filename;
        String error;
        if (loader != null) {
            filename = loader.findLibrary(libraryName);
            if (filename == null) {
                throw new UnsatisfiedLinkError(loader + " couldn't find \"" + System.mapLibraryName(libraryName) + "\"");
            }
            error = doLoad(filename, loader);
            if (error != null) {
                throw new UnsatisfiedLinkError(error);
            }
            return;
        }
        filename = System.mapLibraryName(libraryName);
        Object candidates = new ArrayList();
        String lastError = null;
        for (String directory : this.mLibPaths) {
            String candidate = directory + filename;
            candidates.add(candidate);
            if (IoUtils.canOpenReadOnly(candidate)) {
                error = doLoad(candidate, loader);
                if (error != null) {
                    lastError = error;
                } else {
                    return;
                }
            }
        }
        if (lastError != null) {
            throw new UnsatisfiedLinkError(lastError);
        }
        throw new UnsatisfiedLinkError("Library " + libraryName + " not found; tried " + candidates);
    }

    private String doLoad(String name, ClassLoader loader) {
        String nativeLoad;
        String ldLibraryPath = null;
        if (loader != null && (loader instanceof BaseDexClassLoader)) {
            ldLibraryPath = ((BaseDexClassLoader) loader).getLdLibraryPath();
        }
        synchronized (this) {
            nativeLoad = nativeLoad(name, loader, ldLibraryPath);
        }
        return nativeLoad;
    }

    public void runFinalization() {
        try {
            FinalizerReference.finalizeAllEnqueued();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Deprecated
    public static void runFinalizersOnExit(boolean run) {
        finalizeOnExit = run;
    }

    public void traceInstructions(boolean enable) {
    }

    public void traceMethodCalls(boolean enable) {
        if (enable != this.tracingMethods) {
            if (enable) {
                VMDebug.startMethodTracing();
            } else {
                VMDebug.stopMethodTracing();
            }
            this.tracingMethods = enable;
        }
    }

    @Deprecated
    public InputStream getLocalizedInputStream(InputStream stream) {
        String encoding = System.getProperty("file.encoding", "UTF-8");
        if (encoding.equals("UTF-8")) {
            return stream;
        }
        throw new UnsupportedOperationException("Cannot localize " + encoding);
    }

    @Deprecated
    public OutputStream getLocalizedOutputStream(OutputStream stream) {
        String encoding = System.getProperty("file.encoding", "UTF-8");
        if (encoding.equals("UTF-8")) {
            return stream;
        }
        throw new UnsupportedOperationException("Cannot localize " + encoding);
    }

    public void addShutdownHook(Thread hook) {
        if (hook == null) {
            throw new NullPointerException("hook == null");
        } else if (this.shuttingDown) {
            throw new IllegalStateException("VM already shutting down");
        } else if (hook.hasBeenStarted) {
            throw new IllegalArgumentException("Hook has already been started");
        } else {
            synchronized (this.shutdownHooks) {
                if (this.shutdownHooks.contains(hook)) {
                    throw new IllegalArgumentException("Hook already registered.");
                }
                this.shutdownHooks.add(hook);
            }
        }
    }

    public boolean removeShutdownHook(Thread hook) {
        if (hook == null) {
            throw new NullPointerException("hook == null");
        } else if (this.shuttingDown) {
            throw new IllegalStateException("VM already shutting down");
        } else {
            boolean remove;
            synchronized (this.shutdownHooks) {
                remove = this.shutdownHooks.remove((Object) hook);
            }
            return remove;
        }
    }

    public void halt(int code) {
        nativeExit(code);
    }

    public int availableProcessors() {
        return (int) Libcore.os.sysconf(OsConstants._SC_NPROCESSORS_CONF);
    }
}
