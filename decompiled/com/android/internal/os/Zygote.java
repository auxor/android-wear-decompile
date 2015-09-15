package com.android.internal.os;

import android.os.SystemClock;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Slog;
import dalvik.system.ZygoteHooks;

public final class Zygote {
    public static final int DEBUG_ENABLE_ASSERT = 4;
    public static final int DEBUG_ENABLE_CHECKJNI = 2;
    public static final int DEBUG_ENABLE_DEBUGGER = 1;
    public static final int DEBUG_ENABLE_JNI_LOGGING = 16;
    public static final int DEBUG_ENABLE_SAFEMODE = 8;
    public static final int MOUNT_EXTERNAL_MULTIUSER = 2;
    public static final int MOUNT_EXTERNAL_MULTIUSER_ALL = 3;
    public static final int MOUNT_EXTERNAL_NONE = 0;
    public static final int MOUNT_EXTERNAL_SINGLEUSER = 1;
    private static final String TAG = "Zygote";
    private static final ZygoteHooks VM_HOOKS;

    private static native int nativeForkAndSpecialize(int i, int i2, int[] iArr, int i3, int[][] iArr2, int i4, String str, String str2, int[] iArr3, String str3, String str4);

    private static native int nativeForkSystemServer(int i, int i2, int[] iArr, int i3, int[][] iArr2, long j, long j2);

    static {
        VM_HOOKS = new ZygoteHooks();
    }

    private Zygote() {
    }

    public static int forkAndSpecialize(int uid, int gid, int[] gids, int debugFlags, int[][] rlimits, int mountExternal, String seInfo, String niceName, int[] fdsToClose, String instructionSet, String appDataDir) {
        long startTime = SystemClock.elapsedRealtime();
        VM_HOOKS.preFork();
        checkTime(startTime, "Zygote.preFork");
        int pid = nativeForkAndSpecialize(uid, gid, gids, debugFlags, rlimits, mountExternal, seInfo, niceName, fdsToClose, instructionSet, appDataDir);
        checkTime(startTime, "Zygote.nativeForkAndSpecialize");
        VM_HOOKS.postForkCommon();
        checkTime(startTime, "Zygote.postForkCommon");
        return pid;
    }

    private static void checkTime(long startTime, String where) {
        long now = SystemClock.elapsedRealtime();
        if (now - startTime > 1000) {
            Slog.w(TAG, "Slow operation: " + (now - startTime) + "ms so far, now at " + where);
        }
    }

    public static int forkSystemServer(int uid, int gid, int[] gids, int debugFlags, int[][] rlimits, long permittedCapabilities, long effectiveCapabilities) {
        VM_HOOKS.preFork();
        int pid = nativeForkSystemServer(uid, gid, gids, debugFlags, rlimits, permittedCapabilities, effectiveCapabilities);
        VM_HOOKS.postForkCommon();
        return pid;
    }

    private static void callPostForkChildHooks(int debugFlags, String instructionSet) {
        long startTime = SystemClock.elapsedRealtime();
        VM_HOOKS.postForkChild(debugFlags, instructionSet);
        checkTime(startTime, "Zygote.callPostForkChildHooks");
    }

    public static void execShell(String command) {
        String[] args = new String[MOUNT_EXTERNAL_MULTIUSER_ALL];
        args[MOUNT_EXTERNAL_NONE] = "/system/bin/sh";
        args[MOUNT_EXTERNAL_SINGLEUSER] = "-c";
        args[MOUNT_EXTERNAL_MULTIUSER] = command;
        try {
            Os.execv(args[MOUNT_EXTERNAL_NONE], args);
        } catch (ErrnoException e) {
            throw new RuntimeException(e);
        }
    }

    public static void appendQuotedShellArgs(StringBuilder command, String[] args) {
        String[] arr$ = args;
        int len$ = arr$.length;
        for (int i$ = MOUNT_EXTERNAL_NONE; i$ < len$; i$ += MOUNT_EXTERNAL_SINGLEUSER) {
            command.append(" '").append(arr$[i$].replace("'", "'\\''")).append("'");
        }
    }
}
