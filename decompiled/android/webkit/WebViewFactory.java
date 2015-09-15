package android.webkit;

import android.app.ActivityManagerInternal;
import android.app.AppGlobals;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ProxyInfo;
import android.os.Build;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.SystemProperties;
import android.os.Trace;
import android.text.TextUtils;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.WindowManager.LayoutParams;
import android.webkit.IWebViewUpdateService.Stub;
import com.android.server.LocalServices;
import dalvik.system.VMRuntime;
import java.io.File;

public final class WebViewFactory {
    private static final long CHROMIUM_WEBVIEW_DEFAULT_VMSIZE_BYTES = 104857600;
    private static final String CHROMIUM_WEBVIEW_FACTORY = "com.android.webview.chromium.WebViewChromiumFactoryProvider";
    private static final String CHROMIUM_WEBVIEW_NATIVE_RELRO_32 = "/data/misc/shared_relro/libwebviewchromium32.relro";
    private static final String CHROMIUM_WEBVIEW_NATIVE_RELRO_64 = "/data/misc/shared_relro/libwebviewchromium64.relro";
    public static final String CHROMIUM_WEBVIEW_VMSIZE_SIZE_PROPERTY = "persist.sys.webview.vmsize";
    private static final boolean DEBUG = false;
    private static final String LOGTAG = "WebViewFactory";
    private static final String NULL_WEBVIEW_FACTORY = "com.android.webview.nullwebview.NullWebViewFactoryProvider";
    private static boolean sAddressSpaceReserved;
    private static PackageInfo sPackageInfo;
    private static WebViewFactoryProvider sProviderInstance;
    private static final Object sProviderLock;

    /* renamed from: android.webkit.WebViewFactory.1 */
    static class AnonymousClass1 implements Runnable {
        final /* synthetic */ String val$abi;
        final /* synthetic */ boolean val$is64Bit;

        AnonymousClass1(String str, boolean z) {
            this.val$abi = str;
            this.val$is64Bit = z;
        }

        public void run() {
            try {
                Log.e(WebViewFactory.LOGTAG, "relro file creator for " + this.val$abi + " crashed. Proceeding without");
                WebViewFactory.getUpdateService().notifyRelroCreationCompleted(this.val$is64Bit, WebViewFactory.DEBUG);
            } catch (RemoteException e) {
                Log.e(WebViewFactory.LOGTAG, "Cannot reach WebViewUpdateService. " + e.getMessage());
            }
        }
    }

    private static class RelroFileCreator {
        public static void main(java.lang.String[] r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.webkit.WebViewFactory.RelroFileCreator.main(java.lang.String[]):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.webkit.WebViewFactory.RelroFileCreator.main(java.lang.String[]):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.webkit.WebViewFactory.RelroFileCreator.main(java.lang.String[]):void");
        }

        private RelroFileCreator() {
        }
    }

    private static native boolean nativeCreateRelroFile(String str, String str2, String str3, String str4);

    private static native boolean nativeLoadWithRelroFile(String str, String str2, String str3, String str4);

    private static native boolean nativeReserveAddressSpace(long j);

    static {
        sProviderLock = new Object();
        sAddressSpaceReserved = DEBUG;
    }

    public static String getWebViewPackageName() {
        return AppGlobals.getInitialApplication().getString(17039432);
    }

    public static PackageInfo getLoadedPackageInfo() {
        return sPackageInfo;
    }

    static WebViewFactoryProvider getProvider() {
        WebViewFactoryProvider webViewFactoryProvider;
        synchronized (sProviderLock) {
            if (sProviderInstance != null) {
                webViewFactoryProvider = sProviderInstance;
            } else {
                int uid = Process.myUid();
                if (uid == 0 || uid == LayoutParams.TYPE_APPLICATION_PANEL) {
                    throw new UnsupportedOperationException("For security reasons, WebView is not allowed in privileged processes");
                }
                Trace.traceBegin(16, "WebViewFactory.getProvider()");
                long j;
                try {
                    Trace.traceBegin(16, "WebViewFactory.loadNativeLibrary()");
                    loadNativeLibrary();
                    Trace.traceEnd(16);
                    j = 16;
                    Trace.traceBegin(16, "WebViewFactory.getFactoryClass()");
                    try {
                        Class<WebViewFactoryProvider> providerClass = getFactoryClass();
                        Trace.traceEnd(j);
                        ThreadPolicy oldPolicy = StrictMode.allowThreadDiskReads();
                        Trace.traceBegin(16, "providerClass.newInstance()");
                        try {
                            sProviderInstance = (WebViewFactoryProvider) providerClass.getConstructor(new Class[]{WebViewDelegate.class}).newInstance(new Object[]{new WebViewDelegate()});
                        } catch (Exception e) {
                            sProviderInstance = (WebViewFactoryProvider) providerClass.newInstance();
                        }
                        try {
                            webViewFactoryProvider = sProviderInstance;
                            Trace.traceEnd(16);
                            StrictMode.setThreadPolicy(oldPolicy);
                            Trace.traceEnd(16);
                        } catch (Exception e2) {
                            Log.e(LOGTAG, "error instantiating provider", e2);
                            throw new AndroidRuntimeException(e2);
                        } catch (Throwable th) {
                            Trace.traceEnd(16);
                            StrictMode.setThreadPolicy(oldPolicy);
                        }
                    } catch (ClassNotFoundException e3) {
                        Log.e(LOGTAG, "error loading provider", e3);
                        throw new AndroidRuntimeException(e3);
                    } catch (Throwable th2) {
                        Trace.traceEnd(16);
                    }
                } finally {
                    j = 16;
                    Trace.traceEnd(16);
                }
            }
        }
        return webViewFactoryProvider;
    }

    private static Class<WebViewFactoryProvider> getFactoryClass() throws ClassNotFoundException {
        Class<WebViewFactoryProvider> cls;
        Application initialApplication = AppGlobals.getInitialApplication();
        try {
            String packageName = getWebViewPackageName();
            sPackageInfo = initialApplication.getPackageManager().getPackageInfo(packageName, 0);
            Log.i(LOGTAG, "Loading " + packageName + " version " + sPackageInfo.versionName + " (code " + sPackageInfo.versionCode + ")");
            Context webViewContext = initialApplication.createPackageContext(packageName, 3);
            initialApplication.getAssets().addAssetPath(webViewContext.getApplicationInfo().sourceDir);
            ClassLoader clazzLoader = webViewContext.getClassLoader();
            Trace.traceBegin(16, "Class.forName()");
            cls = Class.forName(CHROMIUM_WEBVIEW_FACTORY, true, clazzLoader);
            Trace.traceEnd(16);
        } catch (NameNotFoundException e) {
            try {
                cls = Class.forName(NULL_WEBVIEW_FACTORY);
            } catch (ClassNotFoundException e2) {
                Log.e(LOGTAG, "Chromium WebView package does not exist", e);
                throw new AndroidRuntimeException(e);
            }
        } catch (Throwable th) {
            Trace.traceEnd(16);
        }
        return cls;
    }

    public static void prepareWebViewInZygote() {
        try {
            System.loadLibrary("webviewchromium_loader");
            long addressSpaceToReserve = SystemProperties.getLong(CHROMIUM_WEBVIEW_VMSIZE_SIZE_PROPERTY, CHROMIUM_WEBVIEW_DEFAULT_VMSIZE_BYTES);
            sAddressSpaceReserved = nativeReserveAddressSpace(addressSpaceToReserve);
            if (!sAddressSpaceReserved) {
                Log.e(LOGTAG, "reserving " + addressSpaceToReserve + " bytes of address space failed");
            }
        } catch (Throwable t) {
            Log.e(LOGTAG, "error preparing native loader", t);
        }
    }

    public static void prepareWebViewInSystemServer() {
        String[] nativePaths = null;
        try {
            nativePaths = getWebViewNativeLibraryPaths();
        } catch (Throwable t) {
            Log.e(LOGTAG, "error preparing webview native library", t);
        }
        prepareWebViewInSystemServer(nativePaths);
    }

    private static void prepareWebViewInSystemServer(String[] nativeLibraryPaths) {
        if (Build.SUPPORTED_32_BIT_ABIS.length > 0) {
            createRelroFile(DEBUG, nativeLibraryPaths);
        }
        if (Build.SUPPORTED_64_BIT_ABIS.length > 0) {
            createRelroFile(true, nativeLibraryPaths);
        }
    }

    public static void onWebViewUpdateInstalled() {
        String[] nativeLibs = null;
        try {
            nativeLibs = getWebViewNativeLibraryPaths();
            if (nativeLibs != null) {
                long newVmSize = 0;
                for (String path : nativeLibs) {
                    if (path != null) {
                        File f = new File(path);
                        if (f.exists()) {
                            long length = f.length();
                            if (length > newVmSize) {
                                newVmSize = length;
                            }
                        }
                    }
                }
                newVmSize = Math.max(2 * newVmSize, CHROMIUM_WEBVIEW_DEFAULT_VMSIZE_BYTES);
                Log.d(LOGTAG, "Setting new address space to " + newVmSize);
                SystemProperties.set(CHROMIUM_WEBVIEW_VMSIZE_SIZE_PROPERTY, Long.toString(newVmSize));
            }
        } catch (Throwable t) {
            Log.e(LOGTAG, "error preparing webview native library", t);
        }
        prepareWebViewInSystemServer(nativeLibs);
    }

    private static String[] getWebViewNativeLibraryPaths() throws NameNotFoundException {
        String path64;
        String path32;
        String NATIVE_LIB_FILE_NAME = "libwebviewchromium.so";
        ApplicationInfo ai = AppGlobals.getInitialApplication().getPackageManager().getApplicationInfo(getWebViewPackageName(), 0);
        boolean primaryArchIs64bit = VMRuntime.is64BitAbi(ai.primaryCpuAbi);
        if (TextUtils.isEmpty(ai.secondaryCpuAbi)) {
            if (primaryArchIs64bit) {
                path64 = ai.nativeLibraryDir;
                path32 = ProxyInfo.LOCAL_EXCL_LIST;
            } else {
                path32 = ai.nativeLibraryDir;
                path64 = ProxyInfo.LOCAL_EXCL_LIST;
            }
        } else if (primaryArchIs64bit) {
            path64 = ai.nativeLibraryDir;
            path32 = ai.secondaryNativeLibraryDir;
        } else {
            path64 = ai.secondaryNativeLibraryDir;
            path32 = ai.nativeLibraryDir;
        }
        if (!TextUtils.isEmpty(path32)) {
            path32 = path32 + "/libwebviewchromium.so";
        }
        if (!TextUtils.isEmpty(path64)) {
            path64 = path64 + "/libwebviewchromium.so";
        }
        return new String[]{path32, path64};
    }

    private static void createRelroFile(boolean is64Bit, String[] nativeLibraryPaths) {
        String abi;
        if (is64Bit) {
            abi = Build.SUPPORTED_64_BIT_ABIS[0];
        } else {
            abi = Build.SUPPORTED_32_BIT_ABIS[0];
        }
        Runnable crashHandler = new AnonymousClass1(abi, is64Bit);
        if (nativeLibraryPaths != null) {
            try {
                if (!(nativeLibraryPaths[0] == null || nativeLibraryPaths[1] == null)) {
                    if (((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).startIsolatedProcess(RelroFileCreator.class.getName(), nativeLibraryPaths, "WebViewLoader-" + abi, abi, Process.SHARED_RELRO_UID, crashHandler) <= 0) {
                        throw new Exception("Failed to start the relro file creator process");
                    }
                    return;
                }
            } catch (Throwable t) {
                Log.e(LOGTAG, "error starting relro file creator for abi " + abi, t);
                crashHandler.run();
                return;
            }
        }
        throw new IllegalArgumentException("Native library paths to the WebView RelRo process must not be null!");
    }

    private static void loadNativeLibrary() {
        if (sAddressSpaceReserved) {
            try {
                getUpdateService().waitForRelroCreationCompleted(VMRuntime.getRuntime().is64Bit());
                try {
                    String[] args = getWebViewNativeLibraryPaths();
                    if (!nativeLoadWithRelroFile(args[0], args[1], CHROMIUM_WEBVIEW_NATIVE_RELRO_32, CHROMIUM_WEBVIEW_NATIVE_RELRO_64)) {
                        Log.w(LOGTAG, "failed to load with relro file, proceeding without");
                        return;
                    }
                    return;
                } catch (NameNotFoundException e) {
                    Log.e(LOGTAG, "Failed to list WebView package libraries for loadNativeLibrary", e);
                    return;
                }
            } catch (RemoteException e2) {
                Log.e(LOGTAG, "error waiting for relro creation, proceeding without", e2);
                return;
            }
        }
        Log.e(LOGTAG, "can't load with relro file; address space not reserved");
    }

    private static IWebViewUpdateService getUpdateService() {
        return Stub.asInterface(ServiceManager.getService("webviewupdate"));
    }
}
