package android.os;

import android.net.wifi.WifiEnterpriseConfig;
import android.os.Parcelable.Creator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ListPopupWindow;
import android.widget.RelativeLayout;
import com.android.internal.util.FastPrintWriter;
import com.android.internal.util.TypedProperties;
import dalvik.bytecode.OpcodeInfo;
import dalvik.system.VMDebug;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import org.apache.harmony.dalvik.ddmc.Chunk;
import org.apache.harmony.dalvik.ddmc.ChunkHandler;
import org.apache.harmony.dalvik.ddmc.DdmServer;

public final class Debug {
    private static final String DEFAULT_TRACE_BODY = "dmtrace";
    private static final String DEFAULT_TRACE_EXTENSION = ".trace";
    private static final String DEFAULT_TRACE_FILE_PATH;
    private static final String DEFAULT_TRACE_PATH_PREFIX;
    public static final int MEMINFO_BUFFERS = 2;
    public static final int MEMINFO_CACHED = 3;
    public static final int MEMINFO_COUNT = 13;
    public static final int MEMINFO_FREE = 1;
    public static final int MEMINFO_KERNEL_STACK = 12;
    public static final int MEMINFO_MAPPED = 9;
    public static final int MEMINFO_PAGE_TABLES = 11;
    public static final int MEMINFO_SHMEM = 4;
    public static final int MEMINFO_SLAB = 5;
    public static final int MEMINFO_SWAP_FREE = 7;
    public static final int MEMINFO_SWAP_TOTAL = 6;
    public static final int MEMINFO_TOTAL = 0;
    public static final int MEMINFO_VM_ALLOC_USED = 10;
    public static final int MEMINFO_ZRAM_TOTAL = 8;
    private static final int MIN_DEBUGGER_IDLE = 1300;
    public static final int SHOW_CLASSLOADER = 2;
    public static final int SHOW_FULL_DETAIL = 1;
    public static final int SHOW_INITIALIZED = 4;
    private static final int SPIN_DELAY = 200;
    private static final String SYSFS_QEMU_TRACE_STATE = "/sys/qemu_trace/state";
    private static final String TAG = "Debug";
    public static final int TRACE_COUNT_ALLOCS = 1;
    private static final TypedProperties debugProperties;
    private static volatile boolean mWaiting;

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DebugProperty {
    }

    public static class InstructionCount {
        private static final int NUM_INSTR;
        private int[] mCounts;

        public InstructionCount() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.os.Debug.InstructionCount.<init>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.os.Debug.InstructionCount.<init>():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.os.Debug.InstructionCount.<init>():void");
        }

        public boolean collect() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.os.Debug.InstructionCount.collect():boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.os.Debug.InstructionCount.collect():boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.os.Debug.InstructionCount.collect():boolean");
        }

        public int globalMethodInvocations() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.os.Debug.InstructionCount.globalMethodInvocations():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.os.Debug.InstructionCount.globalMethodInvocations():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.os.Debug.InstructionCount.globalMethodInvocations():int");
        }

        public int globalTotal() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.os.Debug.InstructionCount.globalTotal():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.os.Debug.InstructionCount.globalTotal():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.os.Debug.InstructionCount.globalTotal():int");
        }

        static {
            NUM_INSTR = OpcodeInfo.MAXIMUM_PACKED_VALUE + Debug.TRACE_COUNT_ALLOCS;
        }

        public boolean resetAndStart() {
            try {
                VMDebug.startInstructionCounting();
                VMDebug.resetInstructionCount();
                return true;
            } catch (UnsupportedOperationException e) {
                return false;
            }
        }
    }

    public static class MemoryInfo implements Parcelable {
        public static final Creator<MemoryInfo> CREATOR;
        public static final int NUM_CATEGORIES = 7;
        public static final int NUM_DVK_STATS = 8;
        public static final int NUM_OTHER_STATS = 17;
        public static final int offsetPrivateClean = 4;
        public static final int offsetPrivateDirty = 2;
        public static final int offsetPss = 0;
        public static final int offsetSharedClean = 5;
        public static final int offsetSharedDirty = 3;
        public static final int offsetSwappablePss = 1;
        public static final int offsetSwappedOut = 6;
        public int dalvikPrivateClean;
        public int dalvikPrivateDirty;
        public int dalvikPss;
        public int dalvikSharedClean;
        public int dalvikSharedDirty;
        public int dalvikSwappablePss;
        public int dalvikSwappedOut;
        public int nativePrivateClean;
        public int nativePrivateDirty;
        public int nativePss;
        public int nativeSharedClean;
        public int nativeSharedDirty;
        public int nativeSwappablePss;
        public int nativeSwappedOut;
        public int otherPrivateClean;
        public int otherPrivateDirty;
        public int otherPss;
        public int otherSharedClean;
        public int otherSharedDirty;
        private int[] otherStats;
        public int otherSwappablePss;
        public int otherSwappedOut;

        public MemoryInfo() {
            this.otherStats = new int[KeyEvent.KEYCODE_CAPTIONS];
        }

        public int getTotalPss() {
            return (this.dalvikPss + this.nativePss) + this.otherPss;
        }

        public int getTotalUss() {
            return ((((this.dalvikPrivateClean + this.dalvikPrivateDirty) + this.nativePrivateClean) + this.nativePrivateDirty) + this.otherPrivateClean) + this.otherPrivateDirty;
        }

        public int getTotalSwappablePss() {
            return (this.dalvikSwappablePss + this.nativeSwappablePss) + this.otherSwappablePss;
        }

        public int getTotalPrivateDirty() {
            return (this.dalvikPrivateDirty + this.nativePrivateDirty) + this.otherPrivateDirty;
        }

        public int getTotalSharedDirty() {
            return (this.dalvikSharedDirty + this.nativeSharedDirty) + this.otherSharedDirty;
        }

        public int getTotalPrivateClean() {
            return (this.dalvikPrivateClean + this.nativePrivateClean) + this.otherPrivateClean;
        }

        public int getTotalSharedClean() {
            return (this.dalvikSharedClean + this.nativeSharedClean) + this.otherSharedClean;
        }

        public int getTotalSwappedOut() {
            return (this.dalvikSwappedOut + this.nativeSwappedOut) + this.otherSwappedOut;
        }

        public int getOtherPss(int which) {
            return this.otherStats[(which * NUM_CATEGORIES) + offsetPss];
        }

        public int getOtherSwappablePss(int which) {
            return this.otherStats[(which * NUM_CATEGORIES) + offsetSwappablePss];
        }

        public int getOtherPrivateDirty(int which) {
            return this.otherStats[(which * NUM_CATEGORIES) + offsetPrivateDirty];
        }

        public int getOtherSharedDirty(int which) {
            return this.otherStats[(which * NUM_CATEGORIES) + offsetSharedDirty];
        }

        public int getOtherPrivateClean(int which) {
            return this.otherStats[(which * NUM_CATEGORIES) + offsetPrivateClean];
        }

        public int getOtherSharedClean(int which) {
            return this.otherStats[(which * NUM_CATEGORIES) + offsetSharedClean];
        }

        public int getOtherSwappedOut(int which) {
            return this.otherStats[(which * NUM_CATEGORIES) + offsetSwappedOut];
        }

        public static String getOtherLabel(int which) {
            switch (which) {
                case offsetPss /*0*/:
                    return "Dalvik Other";
                case offsetSwappablePss /*1*/:
                    return "Stack";
                case offsetPrivateDirty /*2*/:
                    return "Cursor";
                case offsetSharedDirty /*3*/:
                    return "Ashmem";
                case offsetPrivateClean /*4*/:
                    return "Gfx dev";
                case offsetSharedClean /*5*/:
                    return "Other dev";
                case offsetSwappedOut /*6*/:
                    return ".so mmap";
                case NUM_CATEGORIES /*7*/:
                    return ".jar mmap";
                case NUM_DVK_STATS /*8*/:
                    return ".apk mmap";
                case Debug.MEMINFO_MAPPED /*9*/:
                    return ".ttf mmap";
                case Debug.MEMINFO_VM_ALLOC_USED /*10*/:
                    return ".dex mmap";
                case Debug.MEMINFO_PAGE_TABLES /*11*/:
                    return ".oat mmap";
                case Debug.MEMINFO_KERNEL_STACK /*12*/:
                    return ".art mmap";
                case Debug.MEMINFO_COUNT /*13*/:
                    return "Other mmap";
                case ViewPaddingAction.TAG /*14*/:
                    return "EGL mtrack";
                case SetRemoteViewsAdapterList.TAG /*15*/:
                    return "GL mtrack";
                case RelativeLayout.START_OF /*16*/:
                    return "Other mtrack";
                case NUM_OTHER_STATS /*17*/:
                    return ".Heap";
                case RelativeLayout.ALIGN_START /*18*/:
                    return ".LOS";
                case RelativeLayout.ALIGN_END /*19*/:
                    return ".LinearAlloc";
                case RelativeLayout.ALIGN_PARENT_START /*20*/:
                    return ".GC";
                case RelativeLayout.ALIGN_PARENT_END /*21*/:
                    return ".JITCache";
                case MotionEvent.AXIS_GAS /*22*/:
                    return ".Zygote";
                case MotionEvent.AXIS_BRAKE /*23*/:
                    return ".NonMoving";
                case MotionEvent.AXIS_DISTANCE /*24*/:
                    return ".IndirectRef";
                default:
                    return "????";
            }
        }

        public int describeContents() {
            return offsetPss;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.dalvikPss);
            dest.writeInt(this.dalvikSwappablePss);
            dest.writeInt(this.dalvikPrivateDirty);
            dest.writeInt(this.dalvikSharedDirty);
            dest.writeInt(this.dalvikPrivateClean);
            dest.writeInt(this.dalvikSharedClean);
            dest.writeInt(this.dalvikSwappedOut);
            dest.writeInt(this.nativePss);
            dest.writeInt(this.nativeSwappablePss);
            dest.writeInt(this.nativePrivateDirty);
            dest.writeInt(this.nativeSharedDirty);
            dest.writeInt(this.nativePrivateClean);
            dest.writeInt(this.nativeSharedClean);
            dest.writeInt(this.nativeSwappedOut);
            dest.writeInt(this.otherPss);
            dest.writeInt(this.otherSwappablePss);
            dest.writeInt(this.otherPrivateDirty);
            dest.writeInt(this.otherSharedDirty);
            dest.writeInt(this.otherPrivateClean);
            dest.writeInt(this.otherSharedClean);
            dest.writeInt(this.otherSwappedOut);
            dest.writeIntArray(this.otherStats);
        }

        public void readFromParcel(Parcel source) {
            this.dalvikPss = source.readInt();
            this.dalvikSwappablePss = source.readInt();
            this.dalvikPrivateDirty = source.readInt();
            this.dalvikSharedDirty = source.readInt();
            this.dalvikPrivateClean = source.readInt();
            this.dalvikSharedClean = source.readInt();
            this.dalvikSwappedOut = source.readInt();
            this.nativePss = source.readInt();
            this.nativeSwappablePss = source.readInt();
            this.nativePrivateDirty = source.readInt();
            this.nativeSharedDirty = source.readInt();
            this.nativePrivateClean = source.readInt();
            this.nativeSharedClean = source.readInt();
            this.nativeSwappedOut = source.readInt();
            this.otherPss = source.readInt();
            this.otherSwappablePss = source.readInt();
            this.otherPrivateDirty = source.readInt();
            this.otherSharedDirty = source.readInt();
            this.otherPrivateClean = source.readInt();
            this.otherSharedClean = source.readInt();
            this.otherSwappedOut = source.readInt();
            this.otherStats = source.createIntArray();
        }

        static {
            CREATOR = new Creator<MemoryInfo>() {
                public MemoryInfo createFromParcel(Parcel source) {
                    return new MemoryInfo(null);
                }

                public MemoryInfo[] newArray(int size) {
                    return new MemoryInfo[size];
                }
            };
        }

        private MemoryInfo(Parcel source) {
            this.otherStats = new int[KeyEvent.KEYCODE_CAPTIONS];
            readFromParcel(source);
        }
    }

    public static native void dumpNativeBacktraceToFile(int i, String str);

    public static native void dumpNativeHeap(FileDescriptor fileDescriptor);

    public static final native int getBinderDeathObjectCount();

    public static final native int getBinderLocalObjectCount();

    public static final native int getBinderProxyObjectCount();

    public static native int getBinderReceivedTransactions();

    public static native int getBinderSentTransactions();

    public static native void getMemInfo(long[] jArr);

    public static native void getMemoryInfo(int i, MemoryInfo memoryInfo);

    public static native void getMemoryInfo(MemoryInfo memoryInfo);

    public static native long getNativeHeapAllocatedSize();

    public static native long getNativeHeapFreeSize();

    public static native long getNativeHeapSize();

    public static native long getPss();

    public static native long getPss(int i, long[] jArr, long[] jArr2);

    static {
        mWaiting = false;
        DEFAULT_TRACE_PATH_PREFIX = Environment.getLegacyExternalStorageDirectory().getPath() + "/";
        DEFAULT_TRACE_FILE_PATH = DEFAULT_TRACE_PATH_PREFIX + DEFAULT_TRACE_BODY + DEFAULT_TRACE_EXTENSION;
        debugProperties = null;
    }

    private Debug() {
    }

    public static void waitForDebugger() {
        if (VMDebug.isDebuggingEnabled() && !isDebuggerConnected()) {
            System.out.println("Sending WAIT chunk");
            byte[] data = new byte[TRACE_COUNT_ALLOCS];
            data[MEMINFO_TOTAL] = (byte) 0;
            DdmServer.sendChunk(new Chunk(ChunkHandler.type("WAIT"), data, MEMINFO_TOTAL, TRACE_COUNT_ALLOCS));
            mWaiting = true;
            while (!isDebuggerConnected()) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                }
            }
            mWaiting = false;
            System.out.println("Debugger has connected");
            while (true) {
                long delta = VMDebug.lastDebuggerActivity();
                if (delta < 0) {
                    System.out.println("debugger detached?");
                    return;
                } else if (delta < 1300) {
                    System.out.println("waiting for debugger to settle...");
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e2) {
                    }
                } else {
                    System.out.println("debugger has settled (" + delta + ")");
                    return;
                }
            }
        }
    }

    public static boolean waitingForDebugger() {
        return mWaiting;
    }

    public static boolean isDebuggerConnected() {
        return VMDebug.isDebuggerConnected();
    }

    public static String[] getVmFeatureList() {
        return VMDebug.getVmFeatureList();
    }

    @Deprecated
    public static void changeDebugPort(int port) {
    }

    public static void startNativeTracing() {
        Throwable th;
        PrintWriter outStream = null;
        try {
            PrintWriter outStream2 = new FastPrintWriter(new FileOutputStream(SYSFS_QEMU_TRACE_STATE));
            try {
                outStream2.println(WifiEnterpriseConfig.ENGINE_ENABLE);
                if (outStream2 != null) {
                    outStream2.close();
                    outStream = outStream2;
                }
            } catch (Exception e) {
                outStream = outStream2;
                if (outStream != null) {
                    outStream.close();
                }
                VMDebug.startEmulatorTracing();
            } catch (Throwable th2) {
                th = th2;
                outStream = outStream2;
                if (outStream != null) {
                    outStream.close();
                }
                throw th;
            }
        } catch (Exception e2) {
            if (outStream != null) {
                outStream.close();
            }
            VMDebug.startEmulatorTracing();
        } catch (Throwable th3) {
            th = th3;
            if (outStream != null) {
                outStream.close();
            }
            throw th;
        }
        VMDebug.startEmulatorTracing();
    }

    public static void stopNativeTracing() {
        Throwable th;
        VMDebug.stopEmulatorTracing();
        PrintWriter outStream = null;
        try {
            PrintWriter outStream2 = new FastPrintWriter(new FileOutputStream(SYSFS_QEMU_TRACE_STATE));
            try {
                outStream2.println(WifiEnterpriseConfig.ENGINE_DISABLE);
                if (outStream2 != null) {
                    outStream2.close();
                    outStream = outStream2;
                    return;
                }
            } catch (Exception e) {
                outStream = outStream2;
                if (outStream != null) {
                    outStream.close();
                }
            } catch (Throwable th2) {
                th = th2;
                outStream = outStream2;
                if (outStream != null) {
                    outStream.close();
                }
                throw th;
            }
        } catch (Exception e2) {
            if (outStream != null) {
                outStream.close();
            }
        } catch (Throwable th3) {
            th = th3;
            if (outStream != null) {
                outStream.close();
            }
            throw th;
        }
    }

    public static void enableEmulatorTraceOutput() {
        VMDebug.startEmulatorTracing();
    }

    public static void startMethodTracing() {
        VMDebug.startMethodTracing(DEFAULT_TRACE_FILE_PATH, MEMINFO_TOTAL, MEMINFO_TOTAL, false, MEMINFO_TOTAL);
    }

    public static void startMethodTracing(String traceName) {
        startMethodTracing(traceName, MEMINFO_TOTAL, MEMINFO_TOTAL);
    }

    public static void startMethodTracing(String traceName, int bufferSize) {
        startMethodTracing(traceName, bufferSize, MEMINFO_TOTAL);
    }

    public static void startMethodTracing(String traceName, int bufferSize, int flags) {
        VMDebug.startMethodTracing(fixTraceName(traceName), bufferSize, flags, false, MEMINFO_TOTAL);
    }

    public static void startMethodTracingSampling(String traceName, int bufferSize, int intervalUs) {
        VMDebug.startMethodTracing(fixTraceName(traceName), bufferSize, MEMINFO_TOTAL, true, intervalUs);
    }

    private static String fixTraceName(String traceName) {
        if (traceName == null) {
            traceName = DEFAULT_TRACE_FILE_PATH;
        }
        if (traceName.charAt(MEMINFO_TOTAL) != '/') {
            traceName = DEFAULT_TRACE_PATH_PREFIX + traceName;
        }
        if (traceName.endsWith(DEFAULT_TRACE_EXTENSION)) {
            return traceName;
        }
        return traceName + DEFAULT_TRACE_EXTENSION;
    }

    public static void startMethodTracing(String traceName, FileDescriptor fd, int bufferSize, int flags) {
        VMDebug.startMethodTracing(traceName, fd, bufferSize, flags, false, MEMINFO_TOTAL);
    }

    public static void startMethodTracingDdms(int bufferSize, int flags, boolean samplingEnabled, int intervalUs) {
        VMDebug.startMethodTracingDdms(bufferSize, flags, samplingEnabled, intervalUs);
    }

    public static int getMethodTracingMode() {
        return VMDebug.getMethodTracingMode();
    }

    public static void stopMethodTracing() {
        VMDebug.stopMethodTracing();
    }

    public static long threadCpuTimeNanos() {
        return VMDebug.threadCpuTimeNanos();
    }

    @Deprecated
    public static void startAllocCounting() {
        VMDebug.startAllocCounting();
    }

    @Deprecated
    public static void stopAllocCounting() {
        VMDebug.stopAllocCounting();
    }

    public static int getGlobalAllocCount() {
        return VMDebug.getAllocCount(TRACE_COUNT_ALLOCS);
    }

    public static void resetGlobalAllocCount() {
        VMDebug.resetAllocCount(TRACE_COUNT_ALLOCS);
    }

    public static int getGlobalAllocSize() {
        return VMDebug.getAllocCount(SHOW_CLASSLOADER);
    }

    public static void resetGlobalAllocSize() {
        VMDebug.resetAllocCount(SHOW_CLASSLOADER);
    }

    public static int getGlobalFreedCount() {
        return VMDebug.getAllocCount(SHOW_INITIALIZED);
    }

    public static void resetGlobalFreedCount() {
        VMDebug.resetAllocCount(SHOW_INITIALIZED);
    }

    public static int getGlobalFreedSize() {
        return VMDebug.getAllocCount(MEMINFO_ZRAM_TOTAL);
    }

    public static void resetGlobalFreedSize() {
        VMDebug.resetAllocCount(MEMINFO_ZRAM_TOTAL);
    }

    public static int getGlobalGcInvocationCount() {
        return VMDebug.getAllocCount(16);
    }

    public static void resetGlobalGcInvocationCount() {
        VMDebug.resetAllocCount(16);
    }

    public static int getGlobalClassInitCount() {
        return VMDebug.getAllocCount(32);
    }

    public static void resetGlobalClassInitCount() {
        VMDebug.resetAllocCount(32);
    }

    public static int getGlobalClassInitTime() {
        return VMDebug.getAllocCount(64);
    }

    public static void resetGlobalClassInitTime() {
        VMDebug.resetAllocCount(64);
    }

    @Deprecated
    public static int getGlobalExternalAllocCount() {
        return MEMINFO_TOTAL;
    }

    @Deprecated
    public static void resetGlobalExternalAllocSize() {
    }

    @Deprecated
    public static void resetGlobalExternalAllocCount() {
    }

    @Deprecated
    public static int getGlobalExternalAllocSize() {
        return MEMINFO_TOTAL;
    }

    @Deprecated
    public static int getGlobalExternalFreedCount() {
        return MEMINFO_TOTAL;
    }

    @Deprecated
    public static void resetGlobalExternalFreedCount() {
    }

    @Deprecated
    public static int getGlobalExternalFreedSize() {
        return MEMINFO_TOTAL;
    }

    @Deprecated
    public static void resetGlobalExternalFreedSize() {
    }

    public static int getThreadAllocCount() {
        return VMDebug.getAllocCount(AccessibilityNodeInfo.ACTION_CUT);
    }

    public static void resetThreadAllocCount() {
        VMDebug.resetAllocCount(AccessibilityNodeInfo.ACTION_CUT);
    }

    public static int getThreadAllocSize() {
        return VMDebug.getAllocCount(AccessibilityNodeInfo.ACTION_SET_SELECTION);
    }

    public static void resetThreadAllocSize() {
        VMDebug.resetAllocCount(AccessibilityNodeInfo.ACTION_SET_SELECTION);
    }

    @Deprecated
    public static int getThreadExternalAllocCount() {
        return MEMINFO_TOTAL;
    }

    @Deprecated
    public static void resetThreadExternalAllocCount() {
    }

    @Deprecated
    public static int getThreadExternalAllocSize() {
        return MEMINFO_TOTAL;
    }

    @Deprecated
    public static void resetThreadExternalAllocSize() {
    }

    public static int getThreadGcInvocationCount() {
        return VMDebug.getAllocCount(AccessibilityNodeInfo.ACTION_DISMISS);
    }

    public static void resetThreadGcInvocationCount() {
        VMDebug.resetAllocCount(AccessibilityNodeInfo.ACTION_DISMISS);
    }

    public static void resetAllCounts() {
        VMDebug.resetAllocCount(-1);
    }

    @Deprecated
    public static int setAllocationLimit(int limit) {
        return -1;
    }

    @Deprecated
    public static int setGlobalAllocationLimit(int limit) {
        return -1;
    }

    public static void printLoadedClasses(int flags) {
        VMDebug.printLoadedClasses(flags);
    }

    public static int getLoadedClassCount() {
        return VMDebug.getLoadedClassCount();
    }

    public static void dumpHprofData(String fileName) throws IOException {
        VMDebug.dumpHprofData(fileName);
    }

    public static void dumpHprofData(String fileName, FileDescriptor fd) throws IOException {
        VMDebug.dumpHprofData(fileName, fd);
    }

    public static void dumpHprofDataDdms() {
        VMDebug.dumpHprofDataDdms();
    }

    public static long countInstancesOfClass(Class cls) {
        return VMDebug.countInstancesOfClass(cls, true);
    }

    public static final boolean cacheRegisterMap(String classAndMethodDesc) {
        return VMDebug.cacheRegisterMap(classAndMethodDesc);
    }

    public static final void dumpReferenceTables() {
        VMDebug.dumpReferenceTables();
    }

    private static boolean fieldTypeMatches(Field field, Class<?> cl) {
        Class<?> fieldClass = field.getType();
        if (fieldClass == cl) {
            return true;
        }
        try {
            try {
                boolean z;
                if (fieldClass == ((Class) cl.getField("TYPE").get(null))) {
                    z = TRACE_COUNT_ALLOCS;
                } else {
                    z = false;
                }
                return z;
            } catch (IllegalAccessException e) {
                return false;
            }
        } catch (NoSuchFieldException e2) {
            return false;
        }
    }

    private static void modifyFieldIfSet(Field field, TypedProperties properties, String propertyName) {
        if (field.getType() == String.class) {
            int stringInfo = properties.getStringInfo(propertyName);
            switch (stringInfo) {
                case ListPopupWindow.WRAP_CONTENT /*-2*/:
                    throw new IllegalArgumentException("Type of " + propertyName + " " + " does not match field type (" + field.getType() + ")");
                case ListPopupWindow.MATCH_PARENT /*-1*/:
                    return;
                case MEMINFO_TOTAL /*0*/:
                    try {
                        field.set(null, null);
                        return;
                    } catch (IllegalAccessException ex) {
                        throw new IllegalArgumentException("Cannot set field for " + propertyName, ex);
                    }
                case TRACE_COUNT_ALLOCS /*1*/:
                    break;
                default:
                    throw new IllegalStateException("Unexpected getStringInfo(" + propertyName + ") return value " + stringInfo);
            }
        }
        Object value = properties.get(propertyName);
        if (value == null) {
            return;
        }
        if (fieldTypeMatches(field, value.getClass())) {
            try {
                field.set(null, value);
                return;
            } catch (IllegalAccessException ex2) {
                throw new IllegalArgumentException("Cannot set field for " + propertyName, ex2);
            }
        }
        throw new IllegalArgumentException("Type of " + propertyName + " (" + value.getClass() + ") " + " does not match field type (" + field.getType() + ")");
    }

    public static void setFieldsOn(Class<?> cl) {
        setFieldsOn(cl, false);
    }

    public static void setFieldsOn(Class<?> cl, boolean partial) {
        Log.wtf(TAG, "setFieldsOn(" + (cl == null ? "null" : cl.getName()) + ") called in non-DEBUG build");
    }

    public static boolean dumpService(String name, FileDescriptor fd, String[] args) {
        IBinder service = ServiceManager.getService(name);
        if (service == null) {
            Log.e(TAG, "Can't find service to dump: " + name);
            return false;
        }
        try {
            service.dump(fd, args);
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "Can't dump service: " + name, e);
            return false;
        }
    }

    private static String getCaller(StackTraceElement[] callStack, int depth) {
        if (depth + SHOW_INITIALIZED >= callStack.length) {
            return "<bottom of call stack>";
        }
        StackTraceElement caller = callStack[depth + SHOW_INITIALIZED];
        return caller.getClassName() + "." + caller.getMethodName() + ":" + caller.getLineNumber();
    }

    public static String getCallers(int depth) {
        StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
        StringBuffer sb = new StringBuffer();
        for (int i = MEMINFO_TOTAL; i < depth; i += TRACE_COUNT_ALLOCS) {
            sb.append(getCaller(callStack, i)).append(" ");
        }
        return sb.toString();
    }

    public static String getCallers(int start, int depth) {
        StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
        StringBuffer sb = new StringBuffer();
        depth += start;
        for (int i = start; i < depth; i += TRACE_COUNT_ALLOCS) {
            sb.append(getCaller(callStack, i)).append(" ");
        }
        return sb.toString();
    }

    public static String getCallers(int depth, String linePrefix) {
        StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
        StringBuffer sb = new StringBuffer();
        for (int i = MEMINFO_TOTAL; i < depth; i += TRACE_COUNT_ALLOCS) {
            sb.append(linePrefix).append(getCaller(callStack, i)).append("\n");
        }
        return sb.toString();
    }

    public static String getCaller() {
        return getCaller(Thread.currentThread().getStackTrace(), MEMINFO_TOTAL);
    }
}
