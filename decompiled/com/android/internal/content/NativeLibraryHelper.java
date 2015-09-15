package com.android.internal.content;

import android.content.pm.PackageParser;
import android.content.pm.PackageParser.Package;
import android.content.pm.PackageParser.PackageLite;
import android.content.pm.PackageParser.PackageParserException;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.SELinux;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.util.Slog;
import com.android.internal.widget.ExploreByTouchHelper;
import dalvik.system.CloseGuard;
import dalvik.system.VMRuntime;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class NativeLibraryHelper {
    private static final int BITCODE_PRESENT = 1;
    public static final String CLEAR_ABI_OVERRIDE = "-";
    private static final boolean DEBUG_NATIVE = false;
    public static final String LIB64_DIR_NAME = "lib64";
    public static final String LIB_DIR_NAME = "lib";
    private static final String TAG = "NativeHelper";

    public static class Handle implements Closeable {
        final long[] apkHandles;
        private volatile boolean mClosed;
        private final CloseGuard mGuard;
        final boolean multiArch;

        public static Handle create(File packageFile) throws IOException {
            try {
                return create(PackageParser.parsePackageLite(packageFile, 0));
            } catch (PackageParserException e) {
                throw new IOException("Failed to parse package: " + packageFile, e);
            }
        }

        public static Handle create(Package pkg) throws IOException {
            return create(pkg.getAllCodePaths(), (pkg.applicationInfo.flags & ExploreByTouchHelper.INVALID_ID) != 0 ? true : NativeLibraryHelper.DEBUG_NATIVE);
        }

        public static Handle create(PackageLite lite) throws IOException {
            return create(lite.getAllCodePaths(), lite.multiArch);
        }

        private static Handle create(List<String> codePaths, boolean multiArch) throws IOException {
            int size = codePaths.size();
            long[] apkHandles = new long[size];
            for (int i = 0; i < size; i += NativeLibraryHelper.BITCODE_PRESENT) {
                String path = (String) codePaths.get(i);
                apkHandles[i] = NativeLibraryHelper.nativeOpenApk(path);
                if (apkHandles[i] == 0) {
                    for (int j = 0; j < i; j += NativeLibraryHelper.BITCODE_PRESENT) {
                        NativeLibraryHelper.nativeClose(apkHandles[j]);
                    }
                    throw new IOException("Unable to open APK: " + path);
                }
            }
            return new Handle(apkHandles, multiArch);
        }

        Handle(long[] apkHandles, boolean multiArch) {
            this.mGuard = CloseGuard.get();
            this.apkHandles = apkHandles;
            this.multiArch = multiArch;
            this.mGuard.open("close");
        }

        public void close() {
            long[] arr$ = this.apkHandles;
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$ += NativeLibraryHelper.BITCODE_PRESENT) {
                NativeLibraryHelper.nativeClose(arr$[i$]);
            }
            this.mGuard.close();
            this.mClosed = true;
        }

        protected void finalize() throws Throwable {
            if (this.mGuard != null) {
                this.mGuard.warnIfOpen();
            }
            try {
                if (!this.mClosed) {
                    close();
                }
                super.finalize();
            } catch (Throwable th) {
                super.finalize();
            }
        }
    }

    private static native int hasRenderscriptBitcode(long j);

    private static native void nativeClose(long j);

    private static native int nativeCopyNativeBinaries(long j, String str, String str2);

    private static native int nativeFindSupportedAbi(long j, String[] strArr);

    private static native long nativeOpenApk(String str);

    private static native long nativeSumNativeBinaries(long j, String str);

    private static long sumNativeBinaries(Handle handle, String abi) {
        long sum = 0;
        long[] arr$ = handle.apkHandles;
        for (int i$ = 0; i$ < arr$.length; i$ += BITCODE_PRESENT) {
            sum += nativeSumNativeBinaries(arr$[i$], abi);
        }
        return sum;
    }

    public static int copyNativeBinaries(Handle handle, File sharedLibraryDir, String abi) {
        long[] arr$ = handle.apkHandles;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; i$ += BITCODE_PRESENT) {
            int res = nativeCopyNativeBinaries(arr$[i$], sharedLibraryDir.getPath(), abi);
            if (res != BITCODE_PRESENT) {
                return res;
            }
        }
        return BITCODE_PRESENT;
    }

    public static int findSupportedAbi(Handle handle, String[] supportedAbis) {
        int finalRes = -114;
        long[] arr$ = handle.apkHandles;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; i$ += BITCODE_PRESENT) {
            int nativeFindSupportedAbi = nativeFindSupportedAbi(arr$[i$], supportedAbis);
            if (nativeFindSupportedAbi != -114) {
                if (nativeFindSupportedAbi == -113) {
                    if (finalRes < 0) {
                        finalRes = -113;
                    }
                } else if (nativeFindSupportedAbi < 0) {
                    return nativeFindSupportedAbi;
                } else {
                    if (finalRes < 0 || nativeFindSupportedAbi < finalRes) {
                        finalRes = nativeFindSupportedAbi;
                    }
                }
            }
        }
        return finalRes;
    }

    public static void removeNativeBinariesLI(String nativeLibraryPath) {
        if (nativeLibraryPath != null) {
            removeNativeBinariesFromDirLI(new File(nativeLibraryPath), DEBUG_NATIVE);
        }
    }

    public static void removeNativeBinariesFromDirLI(File nativeLibraryRoot, boolean deleteRootDir) {
        if (nativeLibraryRoot.exists()) {
            File[] files = nativeLibraryRoot.listFiles();
            if (files != null) {
                for (int nn = 0; nn < files.length; nn += BITCODE_PRESENT) {
                    if (files[nn].isDirectory()) {
                        removeNativeBinariesFromDirLI(files[nn], true);
                    } else if (!files[nn].delete()) {
                        Slog.w(TAG, "Could not delete native binary: " + files[nn].getPath());
                    }
                }
            }
            if (deleteRootDir && !nativeLibraryRoot.delete()) {
                Slog.w(TAG, "Could not delete native binary directory: " + nativeLibraryRoot.getPath());
            }
        }
    }

    private static void createNativeLibrarySubdir(File path) throws IOException {
        if (!path.isDirectory()) {
            path.delete();
            if (path.mkdir()) {
                try {
                    Os.chmod(path.getPath(), (((OsConstants.S_IRWXU | OsConstants.S_IRGRP) | OsConstants.S_IXGRP) | OsConstants.S_IROTH) | OsConstants.S_IXOTH);
                    return;
                } catch (ErrnoException e) {
                    throw new IOException("Cannot chmod native library directory " + path.getPath(), e);
                }
            }
            throw new IOException("Cannot create " + path.getPath());
        } else if (!SELinux.restorecon(path)) {
            throw new IOException("Cannot set SELinux context for " + path.getPath());
        }
    }

    private static long sumNativeBinariesForSupportedAbi(Handle handle, String[] abiList) {
        int abi = findSupportedAbi(handle, abiList);
        if (abi >= 0) {
            return sumNativeBinaries(handle, abiList[abi]);
        }
        return 0;
    }

    public static int copyNativeBinariesForSupportedAbi(Handle handle, File libraryRoot, String[] abiList, boolean useIsaSubdir) throws IOException {
        createNativeLibrarySubdir(libraryRoot);
        int abi = findSupportedAbi(handle, abiList);
        if (abi >= 0) {
            File subDir;
            String instructionSet = VMRuntime.getInstructionSet(abiList[abi]);
            if (useIsaSubdir) {
                File isaSubdir = new File(libraryRoot, instructionSet);
                createNativeLibrarySubdir(isaSubdir);
                subDir = isaSubdir;
            } else {
                subDir = libraryRoot;
            }
            int copyRet = copyNativeBinaries(handle, subDir, abiList[abi]);
            if (copyRet != BITCODE_PRESENT) {
                return copyRet;
            }
        }
        return abi;
    }

    public static int copyNativeBinariesWithOverride(Handle handle, File libraryRoot, String abiOverride) {
        try {
            int copyRet;
            if (handle.multiArch) {
                if (!(abiOverride == null || CLEAR_ABI_OVERRIDE.equals(abiOverride))) {
                    Slog.w(TAG, "Ignoring abiOverride for multi arch application.");
                }
                if (Build.SUPPORTED_32_BIT_ABIS.length > 0) {
                    copyRet = copyNativeBinariesForSupportedAbi(handle, libraryRoot, Build.SUPPORTED_32_BIT_ABIS, true);
                    if (!(copyRet >= 0 || copyRet == -114 || copyRet == -113)) {
                        Slog.w(TAG, "Failure copying 32 bit native libraries; copyRet=" + copyRet);
                        return copyRet;
                    }
                }
                if (Build.SUPPORTED_64_BIT_ABIS.length > 0) {
                    copyRet = copyNativeBinariesForSupportedAbi(handle, libraryRoot, Build.SUPPORTED_64_BIT_ABIS, true);
                    if (!(copyRet >= 0 || copyRet == -114 || copyRet == -113)) {
                        Slog.w(TAG, "Failure copying 64 bit native libraries; copyRet=" + copyRet);
                        return copyRet;
                    }
                }
            }
            String[] abiList;
            String cpuAbiOverride = null;
            if (CLEAR_ABI_OVERRIDE.equals(abiOverride)) {
                cpuAbiOverride = null;
            } else if (abiOverride != null) {
                cpuAbiOverride = abiOverride;
            }
            if (cpuAbiOverride != null) {
                abiList = new String[BITCODE_PRESENT];
                abiList[0] = cpuAbiOverride;
            } else {
                abiList = Build.SUPPORTED_ABIS;
            }
            if (Build.SUPPORTED_64_BIT_ABIS.length > 0 && cpuAbiOverride == null && hasRenderscriptBitcode(handle)) {
                abiList = Build.SUPPORTED_32_BIT_ABIS;
            }
            copyRet = copyNativeBinariesForSupportedAbi(handle, libraryRoot, abiList, true);
            if (copyRet < 0 && copyRet != -114) {
                Slog.w(TAG, "Failure copying native libraries [errorCode=" + copyRet + "]");
                return copyRet;
            }
            return BITCODE_PRESENT;
        } catch (IOException e) {
            Slog.e(TAG, "Copying native libraries failed", e);
            return MediaPlayer.MEDIA_ERROR_TIMED_OUT;
        }
    }

    public static long sumNativeBinariesWithOverride(Handle handle, String abiOverride) throws IOException {
        long sum = 0;
        if (handle.multiArch) {
            if (!(abiOverride == null || CLEAR_ABI_OVERRIDE.equals(abiOverride))) {
                Slog.w(TAG, "Ignoring abiOverride for multi arch application.");
            }
            if (Build.SUPPORTED_32_BIT_ABIS.length > 0) {
                sum = 0 + sumNativeBinariesForSupportedAbi(handle, Build.SUPPORTED_32_BIT_ABIS);
            }
            return Build.SUPPORTED_64_BIT_ABIS.length > 0 ? sum + sumNativeBinariesForSupportedAbi(handle, Build.SUPPORTED_64_BIT_ABIS) : sum;
        } else {
            String[] abiList;
            String cpuAbiOverride = null;
            if (CLEAR_ABI_OVERRIDE.equals(abiOverride)) {
                cpuAbiOverride = null;
            } else if (abiOverride != null) {
                cpuAbiOverride = abiOverride;
            }
            if (cpuAbiOverride != null) {
                abiList = new String[BITCODE_PRESENT];
                abiList[0] = cpuAbiOverride;
            } else {
                abiList = Build.SUPPORTED_ABIS;
            }
            if (Build.SUPPORTED_64_BIT_ABIS.length > 0 && cpuAbiOverride == null && hasRenderscriptBitcode(handle)) {
                abiList = Build.SUPPORTED_32_BIT_ABIS;
            }
            return 0 + sumNativeBinariesForSupportedAbi(handle, abiList);
        }
    }

    public static boolean hasRenderscriptBitcode(Handle handle) throws IOException {
        long[] arr$ = handle.apkHandles;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            int res = hasRenderscriptBitcode(arr$[i$]);
            if (res < 0) {
                throw new IOException("Error scanning APK, code: " + res);
            } else if (res == BITCODE_PRESENT) {
                return true;
            } else {
                i$ += BITCODE_PRESENT;
            }
        }
        return DEBUG_NATIVE;
    }
}
