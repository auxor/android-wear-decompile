package com.android.internal.os;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.LocalServerSocket;
import android.opengl.EGL14;
import android.os.Debug;
import android.os.Process;
import android.os.Process.ZygoteState;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.util.EventLog;
import android.util.Log;
import android.webkit.WebViewFactory;
import com.android.internal.R;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.RILConstants;
import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;
import dalvik.system.VMRuntime;
import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import javax.microedition.khronos.opengles.GL10;
import libcore.io.IoUtils;

public class ZygoteInit {
    private static final String ABI_LIST_ARG = "--abi-list=";
    private static final String ANDROID_SOCKET_PREFIX = "ANDROID_SOCKET_";
    static final int GC_LOOP_COUNT = 10;
    private static final int LOG_BOOT_PROGRESS_PRELOAD_END = 3030;
    private static final int LOG_BOOT_PROGRESS_PRELOAD_START = 3020;
    private static final String PRELOADED_CLASSES = "/system/etc/preloaded-classes";
    private static final int PRELOAD_GC_THRESHOLD = 50000;
    private static final boolean PRELOAD_RESOURCES = true;
    private static final String PROPERTY_DISABLE_OPENGL_PRELOADING = "ro.zygote.disable_gl_preload";
    private static final int ROOT_GID = 0;
    private static final int ROOT_UID = 0;
    private static final String SOCKET_NAME_ARG = "--socket-name=";
    private static final String TAG = "Zygote";
    private static final int UNPRIVILEGED_GID = 9999;
    private static final int UNPRIVILEGED_UID = 9999;
    private static Resources mResources;
    private static LocalServerSocket sServerSocket;

    public static class MethodAndArgsCaller extends Exception implements Runnable {
        private final String[] mArgs;
        private final Method mMethod;

        public MethodAndArgsCaller(Method method, String[] args) {
            this.mMethod = method;
            this.mArgs = args;
        }

        public void run() {
            try {
                this.mMethod.invoke(null, new Object[]{this.mArgs});
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            } catch (InvocationTargetException ex2) {
                Throwable cause = ex2.getCause();
                if (cause instanceof RuntimeException) {
                    throw ((RuntimeException) cause);
                } else if (cause instanceof Error) {
                    throw ((Error) cause);
                } else {
                    throw new RuntimeException(ex2);
                }
            }
        }
    }

    static native FileDescriptor createFileDescriptor(int i) throws IOException;

    static native int getpgid(int i) throws IOException;

    static native void reopenStdio(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3) throws IOException;

    static native int selectReadable(FileDescriptor[] fileDescriptorArr) throws IOException;

    static native void setCloseOnExec(FileDescriptor fileDescriptor, boolean z) throws IOException;

    static native int setpgid(int i, int i2);

    static native int setregid(int i, int i2);

    static native int setreuid(int i, int i2);

    static void invokeStaticMain(ClassLoader loader, String className, String[] argv) throws MethodAndArgsCaller {
        try {
            try {
                Method m = loader.loadClass(className).getMethod("main", new Class[]{String[].class});
                int modifiers = m.getModifiers();
                if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
                    throw new MethodAndArgsCaller(m, argv);
                }
                throw new RuntimeException("Main method is not public and static on " + className);
            } catch (NoSuchMethodException ex) {
                throw new RuntimeException("Missing static main on " + className, ex);
            } catch (SecurityException ex2) {
                throw new RuntimeException("Problem getting static main on " + className, ex2);
            }
        } catch (ClassNotFoundException ex3) {
            throw new RuntimeException("Missing class when invoking static main " + className, ex3);
        }
    }

    private static void registerZygoteSocket(String socketName) {
        if (sServerSocket == null) {
            String fullSocketName = ANDROID_SOCKET_PREFIX + socketName;
            try {
                int fileDesc = Integer.parseInt(System.getenv(fullSocketName));
                try {
                    sServerSocket = new LocalServerSocket(createFileDescriptor(fileDesc));
                } catch (IOException ex) {
                    throw new RuntimeException("Error binding to local socket '" + fileDesc + "'", ex);
                }
            } catch (RuntimeException ex2) {
                throw new RuntimeException(fullSocketName + " unset or invalid", ex2);
            }
        }
    }

    private static ZygoteConnection acceptCommandPeer(String abiList) {
        try {
            return new ZygoteConnection(sServerSocket.accept(), abiList);
        } catch (IOException ex) {
            throw new RuntimeException("IOException during accept()", ex);
        }
    }

    static void closeServerSocket() {
        try {
            if (sServerSocket != null) {
                FileDescriptor fd = sServerSocket.getFileDescriptor();
                sServerSocket.close();
                if (fd != null) {
                    Os.close(fd);
                }
            }
        } catch (IOException ex) {
            Log.e(TAG, "Zygote:  error closing sockets", ex);
        } catch (ErrnoException ex2) {
            Log.e(TAG, "Zygote:  error closing descriptor", ex2);
        }
        sServerSocket = null;
    }

    static FileDescriptor getServerSocketFileDescriptor() {
        return sServerSocket.getFileDescriptor();
    }

    private static void setEffectiveUser(int uid) {
        int errno = setreuid(ROOT_UID, uid);
        if (errno != 0) {
            Log.e(TAG, "setreuid() failed. errno: " + errno);
        }
    }

    private static void setEffectiveGroup(int gid) {
        int errno = setregid(ROOT_UID, gid);
        if (errno != 0) {
            Log.e(TAG, "setregid() failed. errno: " + errno);
        }
    }

    static void preload() {
        Log.d(TAG, "begin preload");
        preloadClasses();
        preloadResources();
        preloadOpenGL();
        preloadSharedLibraries();
        WebViewFactory.prepareWebViewInZygote();
        Log.d(TAG, "end preload");
    }

    private static void preloadSharedLibraries() {
        Log.i(TAG, "Preloading shared libraries...");
        System.loadLibrary("android");
        System.loadLibrary("compiler_rt");
        System.loadLibrary("jnigraphics");
    }

    private static void preloadOpenGL() {
        if (!SystemProperties.getBoolean(PROPERTY_DISABLE_OPENGL_PRELOADING, false)) {
            EGL14.eglGetDisplay(ROOT_UID);
        }
    }

    private static void preloadClasses() {
        VMRuntime runtime = VMRuntime.getRuntime();
        try {
            InputStream is = new FileInputStream(PRELOADED_CLASSES);
            Log.i(TAG, "Preloading classes...");
            long startTime = SystemClock.uptimeMillis();
            setEffectiveGroup(UNPRIVILEGED_UID);
            setEffectiveUser(UNPRIVILEGED_UID);
            float defaultUtilization = runtime.getTargetHeapUtilization();
            runtime.setTargetHeapUtilization(0.8f);
            System.gc();
            runtime.runFinalizationSync();
            Debug.startAllocCounting();
            BufferedReader br = new BufferedReader(new InputStreamReader(is), GL10.GL_DEPTH_BUFFER_BIT);
            int count = ROOT_UID;
            while (true) {
                String line = br.readLine();
                if (line != null) {
                    line = line.trim();
                    if (!(line.startsWith("#") || line.equals(""))) {
                        try {
                            Class.forName(line);
                            if (Debug.getGlobalAllocSize() > PRELOAD_GC_THRESHOLD) {
                                System.gc();
                                runtime.runFinalizationSync();
                                Debug.resetGlobalAllocSize();
                            }
                            count++;
                        } catch (ClassNotFoundException e) {
                            Log.w(TAG, "Class not found for preloading: " + line);
                        } catch (UnsatisfiedLinkError e2) {
                            Log.w(TAG, "Problem preloading " + line + ": " + e2);
                        } catch (IOException e3) {
                            Log.e(TAG, "Error reading /system/etc/preloaded-classes.", e3);
                            IoUtils.closeQuietly(is);
                            runtime.setTargetHeapUtilization(defaultUtilization);
                            runtime.preloadDexCaches();
                            Debug.stopAllocCounting();
                            setEffectiveUser(ROOT_UID);
                            setEffectiveGroup(ROOT_UID);
                            return;
                        } catch (Throwable th) {
                            IoUtils.closeQuietly(is);
                            runtime.setTargetHeapUtilization(defaultUtilization);
                            runtime.preloadDexCaches();
                            Debug.stopAllocCounting();
                            setEffectiveUser(ROOT_UID);
                            setEffectiveGroup(ROOT_UID);
                        }
                    }
                } else {
                    Log.i(TAG, "...preloaded " + count + " classes in " + (SystemClock.uptimeMillis() - startTime) + "ms.");
                    IoUtils.closeQuietly(is);
                    runtime.setTargetHeapUtilization(defaultUtilization);
                    runtime.preloadDexCaches();
                    Debug.stopAllocCounting();
                    setEffectiveUser(ROOT_UID);
                    setEffectiveGroup(ROOT_UID);
                    return;
                }
            }
        } catch (FileNotFoundException e4) {
            Log.e(TAG, "Couldn't find /system/etc/preloaded-classes.");
        }
    }

    private static void preloadResources() {
        VMRuntime runtime = VMRuntime.getRuntime();
        Debug.startAllocCounting();
        try {
            System.gc();
            runtime.runFinalizationSync();
            mResources = Resources.getSystem();
            mResources.startPreloading();
            Log.i(TAG, "Preloading resources...");
            long startTime = SystemClock.uptimeMillis();
            TypedArray ar = mResources.obtainTypedArray(R.array.preloaded_drawables);
            int N = preloadDrawables(runtime, ar);
            ar.recycle();
            Log.i(TAG, "...preloaded " + N + " resources in " + (SystemClock.uptimeMillis() - startTime) + "ms.");
            startTime = SystemClock.uptimeMillis();
            ar = mResources.obtainTypedArray(R.array.preloaded_color_state_lists);
            N = preloadColorStateLists(runtime, ar);
            ar.recycle();
            Log.i(TAG, "...preloaded " + N + " resources in " + (SystemClock.uptimeMillis() - startTime) + "ms.");
            mResources.finishPreloading();
        } catch (RuntimeException e) {
            Log.w(TAG, "Failure preloading resources", e);
        } finally {
            Debug.stopAllocCounting();
        }
    }

    private static int preloadColorStateLists(VMRuntime runtime, TypedArray ar) {
        int N = ar.length();
        int i = ROOT_UID;
        while (i < N) {
            if (Debug.getGlobalAllocSize() > PRELOAD_GC_THRESHOLD) {
                System.gc();
                runtime.runFinalizationSync();
                Debug.resetGlobalAllocSize();
            }
            int id = ar.getResourceId(i, ROOT_UID);
            if (id == 0 || mResources.getColorStateList(id) != null) {
                i++;
            } else {
                throw new IllegalArgumentException("Unable to find preloaded color resource #0x" + Integer.toHexString(id) + " (" + ar.getString(i) + ")");
            }
        }
        return N;
    }

    private static int preloadDrawables(VMRuntime runtime, TypedArray ar) {
        int N = ar.length();
        int i = ROOT_UID;
        while (i < N) {
            if (Debug.getGlobalAllocSize() > PRELOAD_GC_THRESHOLD) {
                System.gc();
                runtime.runFinalizationSync();
                Debug.resetGlobalAllocSize();
            }
            int id = ar.getResourceId(i, ROOT_UID);
            if (id == 0 || mResources.getDrawable(id, null) != null) {
                i++;
            } else {
                throw new IllegalArgumentException("Unable to find preloaded drawable resource #0x" + Integer.toHexString(id) + " (" + ar.getString(i) + ")");
            }
        }
        return N;
    }

    static void gc() {
        VMRuntime runtime = VMRuntime.getRuntime();
        System.gc();
        runtime.runFinalizationSync();
        System.gc();
        runtime.runFinalizationSync();
        System.gc();
        runtime.runFinalizationSync();
    }

    private static void handleSystemServerProcess(Arguments parsedArgs) throws MethodAndArgsCaller {
        closeServerSocket();
        Os.umask(OsConstants.S_IRWXG | OsConstants.S_IRWXO);
        if (parsedArgs.niceName != null) {
            Process.setArgV0(parsedArgs.niceName);
        }
        String systemServerClasspath = Os.getenv("SYSTEMSERVERCLASSPATH");
        if (systemServerClasspath != null) {
            performSystemServerDexOpt(systemServerClasspath);
        }
        if (parsedArgs.invokeWith != null) {
            String[] args = parsedArgs.remainingArgs;
            if (systemServerClasspath != null) {
                String[] amendedArgs = new String[(args.length + 2)];
                amendedArgs[ROOT_UID] = "-cp";
                amendedArgs[1] = systemServerClasspath;
                System.arraycopy(parsedArgs.remainingArgs, ROOT_UID, amendedArgs, 2, parsedArgs.remainingArgs.length);
            }
            WrapperInit.execApplication(parsedArgs.invokeWith, parsedArgs.niceName, parsedArgs.targetSdkVersion, null, args);
            return;
        }
        ClassLoader cl = null;
        if (systemServerClasspath != null) {
            cl = new PathClassLoader(systemServerClasspath, ClassLoader.getSystemClassLoader());
            Thread.currentThread().setContextClassLoader(cl);
        }
        RuntimeInit.zygoteInit(parsedArgs.targetSdkVersion, parsedArgs.remainingArgs, cl);
    }

    private static void performSystemServerDexOpt(String classPath) {
        String[] classPathElements = classPath.split(":");
        InstallerConnection installer = new InstallerConnection();
        String instructionSet = VMRuntime.getRuntime().vmInstructionSet();
        String[] arr$ = classPathElements;
        try {
            int len$ = arr$.length;
            for (int i$ = ROOT_UID; i$ < len$; i$++) {
                String classPathElement = arr$[i$];
                byte dexopt = DexFile.isDexOptNeededInternal(classPathElement, PhoneConstants.APN_TYPE_ALL, instructionSet, false);
                if (dexopt == 2) {
                    installer.dexopt(classPathElement, RILConstants.RIL_UNSOL_RESPONSE_RADIO_STATE_CHANGED, false, instructionSet);
                } else if (dexopt == 1) {
                    installer.patchoat(classPathElement, RILConstants.RIL_UNSOL_RESPONSE_RADIO_STATE_CHANGED, false, instructionSet);
                }
            }
            installer.disconnect();
        } catch (IOException ioe) {
            throw new RuntimeException("Error starting system_server", ioe);
        } catch (Throwable th) {
            installer.disconnect();
        }
    }

    private static boolean startSystemServer(String abiList, String socketName) throws MethodAndArgsCaller, RuntimeException {
        IllegalArgumentException ex;
        long capabilities = posixCapabilitiesAsBits(OsConstants.CAP_BLOCK_SUSPEND, OsConstants.CAP_KILL, OsConstants.CAP_NET_ADMIN, OsConstants.CAP_NET_BIND_SERVICE, OsConstants.CAP_NET_BROADCAST, OsConstants.CAP_NET_RAW, OsConstants.CAP_SYS_MODULE, OsConstants.CAP_SYS_NICE, OsConstants.CAP_SYS_RESOURCE, OsConstants.CAP_SYS_TIME, OsConstants.CAP_SYS_TTY_CONFIG);
        try {
            Arguments parsedArgs = new Arguments(new String[]{"--setuid=1000", "--setgid=1000", "--setgroups=1001,1002,1003,1004,1005,1006,1007,1008,1009,1010,1018,1032,3001,3002,3003,3006,3007", "--capabilities=" + capabilities + "," + capabilities, "--runtime-init", "--nice-name=system_server", "com.android.server.SystemServer"});
            try {
                ZygoteConnection.applyDebuggerSystemProperty(parsedArgs);
                ZygoteConnection.applyInvokeWithSystemProperty(parsedArgs);
                if (Zygote.forkSystemServer(parsedArgs.uid, parsedArgs.gid, parsedArgs.gids, parsedArgs.debugFlags, (int[][]) null, parsedArgs.permittedCapabilities, parsedArgs.effectiveCapabilities) == 0) {
                    if (hasSecondZygote(abiList)) {
                        waitForSecondaryZygote(socketName);
                    }
                    handleSystemServerProcess(parsedArgs);
                }
                return PRELOAD_RESOURCES;
            } catch (IllegalArgumentException e) {
                ex = e;
                Arguments arguments = parsedArgs;
                throw new RuntimeException(ex);
            }
        } catch (IllegalArgumentException e2) {
            ex = e2;
            throw new RuntimeException(ex);
        }
    }

    private static long posixCapabilitiesAsBits(int... capabilities) {
        long result = 0;
        int[] arr$ = capabilities;
        int len$ = arr$.length;
        for (int i$ = ROOT_UID; i$ < len$; i$++) {
            int capability = arr$[i$];
            if (capability < 0 || capability > OsConstants.CAP_LAST_CAP) {
                throw new IllegalArgumentException(String.valueOf(capability));
            }
            result |= 1 << capability;
        }
        return result;
    }

    public static void main(String[] argv) {
        try {
            SamplingProfilerIntegration.start();
            boolean startSystemServer = false;
            String socketName = "zygote";
            String abiList = null;
            for (int i = 1; i < argv.length; i++) {
                if ("start-system-server".equals(argv[i])) {
                    startSystemServer = PRELOAD_RESOURCES;
                } else if (argv[i].startsWith(ABI_LIST_ARG)) {
                    abiList = argv[i].substring(ABI_LIST_ARG.length());
                } else if (argv[i].startsWith(SOCKET_NAME_ARG)) {
                    socketName = argv[i].substring(SOCKET_NAME_ARG.length());
                } else {
                    throw new RuntimeException("Unknown command line argument: " + argv[i]);
                }
            }
            if (abiList == null) {
                throw new RuntimeException("No ABI list supplied.");
            }
            registerZygoteSocket(socketName);
            EventLog.writeEvent((int) LOG_BOOT_PROGRESS_PRELOAD_START, SystemClock.uptimeMillis());
            preload();
            EventLog.writeEvent((int) LOG_BOOT_PROGRESS_PRELOAD_END, SystemClock.uptimeMillis());
            SamplingProfilerIntegration.writeZygoteSnapshot();
            gc();
            Trace.setTracingEnabled(false);
            if (startSystemServer) {
                startSystemServer(abiList, socketName);
            }
            Log.i(TAG, "Accepting command socket connections");
            runSelectLoop(abiList);
            closeServerSocket();
        } catch (MethodAndArgsCaller caller) {
            caller.run();
        } catch (RuntimeException ex) {
            Log.e(TAG, "Zygote died with exception", ex);
            closeServerSocket();
            throw ex;
        }
    }

    private static boolean hasSecondZygote(String abiList) {
        return !SystemProperties.get("ro.product.cpu.abilist").equals(abiList) ? PRELOAD_RESOURCES : false;
    }

    private static void waitForSecondaryZygote(String socketName) {
        String otherZygoteName = "zygote".equals(socketName) ? "zygote_secondary" : "zygote";
        while (true) {
            try {
                ZygoteState.connect(otherZygoteName).close();
                break;
            } catch (IOException ioe) {
                Log.w(TAG, "Got error connecting to zygote, retrying. msg= " + ioe.getMessage());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private static void runSelectLoop(String abiList) throws MethodAndArgsCaller {
        ArrayList<FileDescriptor> fds = new ArrayList();
        ArrayList<ZygoteConnection> peers = new ArrayList();
        FileDescriptor[] fdArray = new FileDescriptor[4];
        fds.add(sServerSocket.getFileDescriptor());
        peers.add(null);
        int loopCount = GC_LOOP_COUNT;
        while (true) {
            if (loopCount <= 0) {
                gc();
                loopCount = GC_LOOP_COUNT;
            } else {
                loopCount--;
            }
            try {
                fdArray = (FileDescriptor[]) fds.toArray(fdArray);
                int index = selectReadable(fdArray);
                if (index < 0) {
                    break;
                } else if (index == 0) {
                    ZygoteConnection newPeer = acceptCommandPeer(abiList);
                    peers.add(newPeer);
                    fds.add(newPeer.getFileDescriptor());
                } else if (((ZygoteConnection) peers.get(index)).runOnce()) {
                    peers.remove(index);
                    fds.remove(index);
                }
            } catch (IOException ex) {
                throw new RuntimeException("Error in select()", ex);
            }
        }
        throw new RuntimeException("Error in select()");
    }

    private ZygoteInit() {
    }
}
