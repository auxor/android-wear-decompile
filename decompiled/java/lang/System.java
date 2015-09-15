package java.lang;

import android.system.StructUtsname;
import dalvik.system.VMRuntime;
import dalvik.system.VMStack;
import java.io.BufferedInputStream;
import java.io.Console;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.channels.Channel;
import java.nio.channels.spi.SelectorProvider;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import libcore.icu.ICU;
import libcore.io.Libcore;
import org.xmlpull.v1.XmlPullParser;

public final class System {
    private static final int ARRAYCOPY_SHORT_BOOLEAN_ARRAY_THRESHOLD = 32;
    private static final int ARRAYCOPY_SHORT_BYTE_ARRAY_THRESHOLD = 32;
    private static final int ARRAYCOPY_SHORT_CHAR_ARRAY_THRESHOLD = 32;
    private static final int ARRAYCOPY_SHORT_DOUBLE_ARRAY_THRESHOLD = 32;
    private static final int ARRAYCOPY_SHORT_FLOAT_ARRAY_THRESHOLD = 32;
    private static final int ARRAYCOPY_SHORT_INT_ARRAY_THRESHOLD = 32;
    private static final int ARRAYCOPY_SHORT_LONG_ARRAY_THRESHOLD = 32;
    private static final int ARRAYCOPY_SHORT_SHORT_ARRAY_THRESHOLD = 32;
    public static final PrintStream err;
    public static final InputStream in;
    private static boolean justRanFinalization;
    private static final String lineSeparator;
    private static final Object lock;
    public static final PrintStream out;
    private static boolean runGC;
    private static Properties systemProperties;
    private static final Properties unchangeableSystemProperties;

    static final class PropertiesWithNonOverrideableDefaults extends Properties {
        PropertiesWithNonOverrideableDefaults(Properties defaults) {
            super(defaults);
        }

        public Object put(Object key, Object value) {
            if (!this.defaults.containsKey(key)) {
                return super.put(key, value);
            }
            System.logE("Ignoring attempt to set property \"" + key + "\" to value \"" + value + "\".");
            return this.defaults.get(key);
        }

        public Object remove(Object key) {
            if (!this.defaults.containsKey(key)) {
                return super.remove(key);
            }
            System.logE("Ignoring attempt to remove property \"" + key + "\".");
            return null;
        }
    }

    static class SystemEnvironment extends AbstractMap<String, String> {
        private final Map<String, String> map;

        public SystemEnvironment(java.util.Map<java.lang.String, java.lang.String> r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.lang.System.SystemEnvironment.<init>(java.util.Map):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.lang.System.SystemEnvironment.<init>(java.util.Map):void
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
            throw new UnsupportedOperationException("Method not decompiled: java.lang.System.SystemEnvironment.<init>(java.util.Map):void");
        }

        public boolean containsKey(java.lang.Object r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.lang.System.SystemEnvironment.containsKey(java.lang.Object):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.lang.System.SystemEnvironment.containsKey(java.lang.Object):boolean
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
            throw new UnsupportedOperationException("Method not decompiled: java.lang.System.SystemEnvironment.containsKey(java.lang.Object):boolean");
        }

        public boolean containsValue(java.lang.Object r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.lang.System.SystemEnvironment.containsValue(java.lang.Object):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.lang.System.SystemEnvironment.containsValue(java.lang.Object):boolean
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
            throw new UnsupportedOperationException("Method not decompiled: java.lang.System.SystemEnvironment.containsValue(java.lang.Object):boolean");
        }

        public java.util.Set<java.util.Map.Entry<java.lang.String, java.lang.String>> entrySet() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.lang.System.SystemEnvironment.entrySet():java.util.Set<java.util.Map$Entry<java.lang.String, java.lang.String>>
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.lang.System.SystemEnvironment.entrySet():java.util.Set<java.util.Map$Entry<java.lang.String, java.lang.String>>
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
            throw new UnsupportedOperationException("Method not decompiled: java.lang.System.SystemEnvironment.entrySet():java.util.Set<java.util.Map$Entry<java.lang.String, java.lang.String>>");
        }

        public java.lang.String get(java.lang.Object r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.lang.System.SystemEnvironment.get(java.lang.Object):java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.lang.System.SystemEnvironment.get(java.lang.Object):java.lang.String
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
            throw new UnsupportedOperationException("Method not decompiled: java.lang.System.SystemEnvironment.get(java.lang.Object):java.lang.String");
        }

        private String toNonNullString(Object o) {
            if (o != null) {
                return (String) o;
            }
            throw new NullPointerException("o == null");
        }
    }

    public static native void arraycopy(Object obj, int i, Object obj2, int i2, int i3);

    private static native void arraycopyBooleanUnchecked(boolean[] zArr, int i, boolean[] zArr2, int i2, int i3);

    private static native void arraycopyByteUnchecked(byte[] bArr, int i, byte[] bArr2, int i2, int i3);

    private static native void arraycopyCharUnchecked(char[] cArr, int i, char[] cArr2, int i2, int i3);

    private static native void arraycopyDoubleUnchecked(double[] dArr, int i, double[] dArr2, int i2, int i3);

    private static native void arraycopyFloatUnchecked(float[] fArr, int i, float[] fArr2, int i2, int i3);

    private static native void arraycopyIntUnchecked(int[] iArr, int i, int[] iArr2, int i2, int i3);

    private static native void arraycopyLongUnchecked(long[] jArr, int i, long[] jArr2, int i2, int i3);

    private static native void arraycopyShortUnchecked(short[] sArr, int i, short[] sArr2, int i2, int i3);

    public static native long currentTimeMillis();

    public static native int identityHashCode(Object obj);

    private static native void log(char c, String str, Throwable th);

    public static native String mapLibraryName(String str);

    public static native long nanoTime();

    private static native void setFieldImpl(String str, String str2, Object obj);

    private static native String[] specialProperties();

    static {
        lock = new Object();
        err = new PrintStream(new FileOutputStream(FileDescriptor.err));
        out = new PrintStream(new FileOutputStream(FileDescriptor.out));
        in = new BufferedInputStream(new FileInputStream(FileDescriptor.in));
        unchangeableSystemProperties = initUnchangeableSystemProperties();
        systemProperties = createSystemProperties();
        lineSeparator = getProperty("line.separator");
    }

    public static void setIn(InputStream newIn) {
        setFieldImpl("in", "Ljava/io/InputStream;", newIn);
    }

    public static void setOut(PrintStream newOut) {
        setFieldImpl("out", "Ljava/io/PrintStream;", newOut);
    }

    public static void setErr(PrintStream newErr) {
        setFieldImpl("err", "Ljava/io/PrintStream;", newErr);
    }

    private System() {
    }

    public static void arraycopy(char[] src, int srcPos, char[] dst, int dstPos, int length) {
        if (src == null) {
            throw new NullPointerException("src == null");
        } else if (dst == null) {
            throw new NullPointerException("dst == null");
        } else if (srcPos < 0 || dstPos < 0 || length < 0 || srcPos > src.length - length || dstPos > dst.length - length) {
            throw new ArrayIndexOutOfBoundsException("src.length=" + src.length + " srcPos=" + srcPos + " dst.length=" + dst.length + " dstPos=" + dstPos + " length=" + length);
        } else if (length > ARRAYCOPY_SHORT_SHORT_ARRAY_THRESHOLD) {
            arraycopyCharUnchecked(src, srcPos, dst, dstPos, length);
        } else if (src != dst || srcPos >= dstPos || dstPos >= srcPos + length) {
            for (i = 0; i < length; i++) {
                dst[dstPos + i] = src[srcPos + i];
            }
        } else {
            for (i = length - 1; i >= 0; i--) {
                dst[dstPos + i] = src[srcPos + i];
            }
        }
    }

    public static void arraycopy(byte[] src, int srcPos, byte[] dst, int dstPos, int length) {
        if (src == null) {
            throw new NullPointerException("src == null");
        } else if (dst == null) {
            throw new NullPointerException("dst == null");
        } else if (srcPos < 0 || dstPos < 0 || length < 0 || srcPos > src.length - length || dstPos > dst.length - length) {
            throw new ArrayIndexOutOfBoundsException("src.length=" + src.length + " srcPos=" + srcPos + " dst.length=" + dst.length + " dstPos=" + dstPos + " length=" + length);
        } else if (length > ARRAYCOPY_SHORT_SHORT_ARRAY_THRESHOLD) {
            arraycopyByteUnchecked(src, srcPos, dst, dstPos, length);
        } else if (src != dst || srcPos >= dstPos || dstPos >= srcPos + length) {
            for (i = 0; i < length; i++) {
                dst[dstPos + i] = src[srcPos + i];
            }
        } else {
            for (i = length - 1; i >= 0; i--) {
                dst[dstPos + i] = src[srcPos + i];
            }
        }
    }

    public static void arraycopy(short[] src, int srcPos, short[] dst, int dstPos, int length) {
        if (src == null) {
            throw new NullPointerException("src == null");
        } else if (dst == null) {
            throw new NullPointerException("dst == null");
        } else if (srcPos < 0 || dstPos < 0 || length < 0 || srcPos > src.length - length || dstPos > dst.length - length) {
            throw new ArrayIndexOutOfBoundsException("src.length=" + src.length + " srcPos=" + srcPos + " dst.length=" + dst.length + " dstPos=" + dstPos + " length=" + length);
        } else if (length > ARRAYCOPY_SHORT_SHORT_ARRAY_THRESHOLD) {
            arraycopyShortUnchecked(src, srcPos, dst, dstPos, length);
        } else if (src != dst || srcPos >= dstPos || dstPos >= srcPos + length) {
            for (i = 0; i < length; i++) {
                dst[dstPos + i] = src[srcPos + i];
            }
        } else {
            for (i = length - 1; i >= 0; i--) {
                dst[dstPos + i] = src[srcPos + i];
            }
        }
    }

    public static void arraycopy(int[] src, int srcPos, int[] dst, int dstPos, int length) {
        if (src == null) {
            throw new NullPointerException("src == null");
        } else if (dst == null) {
            throw new NullPointerException("dst == null");
        } else if (srcPos < 0 || dstPos < 0 || length < 0 || srcPos > src.length - length || dstPos > dst.length - length) {
            throw new ArrayIndexOutOfBoundsException("src.length=" + src.length + " srcPos=" + srcPos + " dst.length=" + dst.length + " dstPos=" + dstPos + " length=" + length);
        } else if (length > ARRAYCOPY_SHORT_SHORT_ARRAY_THRESHOLD) {
            arraycopyIntUnchecked(src, srcPos, dst, dstPos, length);
        } else if (src != dst || srcPos >= dstPos || dstPos >= srcPos + length) {
            for (i = 0; i < length; i++) {
                dst[dstPos + i] = src[srcPos + i];
            }
        } else {
            for (i = length - 1; i >= 0; i--) {
                dst[dstPos + i] = src[srcPos + i];
            }
        }
    }

    public static void arraycopy(long[] src, int srcPos, long[] dst, int dstPos, int length) {
        if (src == null) {
            throw new NullPointerException("src == null");
        } else if (dst == null) {
            throw new NullPointerException("dst == null");
        } else if (srcPos < 0 || dstPos < 0 || length < 0 || srcPos > src.length - length || dstPos > dst.length - length) {
            throw new ArrayIndexOutOfBoundsException("src.length=" + src.length + " srcPos=" + srcPos + " dst.length=" + dst.length + " dstPos=" + dstPos + " length=" + length);
        } else if (length > ARRAYCOPY_SHORT_SHORT_ARRAY_THRESHOLD) {
            arraycopyLongUnchecked(src, srcPos, dst, dstPos, length);
        } else if (src != dst || srcPos >= dstPos || dstPos >= srcPos + length) {
            for (i = 0; i < length; i++) {
                dst[dstPos + i] = src[srcPos + i];
            }
        } else {
            for (i = length - 1; i >= 0; i--) {
                dst[dstPos + i] = src[srcPos + i];
            }
        }
    }

    public static void arraycopy(float[] src, int srcPos, float[] dst, int dstPos, int length) {
        if (src == null) {
            throw new NullPointerException("src == null");
        } else if (dst == null) {
            throw new NullPointerException("dst == null");
        } else if (srcPos < 0 || dstPos < 0 || length < 0 || srcPos > src.length - length || dstPos > dst.length - length) {
            throw new ArrayIndexOutOfBoundsException("src.length=" + src.length + " srcPos=" + srcPos + " dst.length=" + dst.length + " dstPos=" + dstPos + " length=" + length);
        } else if (length > ARRAYCOPY_SHORT_SHORT_ARRAY_THRESHOLD) {
            arraycopyFloatUnchecked(src, srcPos, dst, dstPos, length);
        } else if (src != dst || srcPos >= dstPos || dstPos >= srcPos + length) {
            for (i = 0; i < length; i++) {
                dst[dstPos + i] = src[srcPos + i];
            }
        } else {
            for (i = length - 1; i >= 0; i--) {
                dst[dstPos + i] = src[srcPos + i];
            }
        }
    }

    public static void arraycopy(double[] src, int srcPos, double[] dst, int dstPos, int length) {
        if (src == null) {
            throw new NullPointerException("src == null");
        } else if (dst == null) {
            throw new NullPointerException("dst == null");
        } else if (srcPos < 0 || dstPos < 0 || length < 0 || srcPos > src.length - length || dstPos > dst.length - length) {
            throw new ArrayIndexOutOfBoundsException("src.length=" + src.length + " srcPos=" + srcPos + " dst.length=" + dst.length + " dstPos=" + dstPos + " length=" + length);
        } else if (length > ARRAYCOPY_SHORT_SHORT_ARRAY_THRESHOLD) {
            arraycopyDoubleUnchecked(src, srcPos, dst, dstPos, length);
        } else if (src != dst || srcPos >= dstPos || dstPos >= srcPos + length) {
            for (i = 0; i < length; i++) {
                dst[dstPos + i] = src[srcPos + i];
            }
        } else {
            for (i = length - 1; i >= 0; i--) {
                dst[dstPos + i] = src[srcPos + i];
            }
        }
    }

    public static void arraycopy(boolean[] src, int srcPos, boolean[] dst, int dstPos, int length) {
        if (src == null) {
            throw new NullPointerException("src == null");
        } else if (dst == null) {
            throw new NullPointerException("dst == null");
        } else if (srcPos < 0 || dstPos < 0 || length < 0 || srcPos > src.length - length || dstPos > dst.length - length) {
            throw new ArrayIndexOutOfBoundsException("src.length=" + src.length + " srcPos=" + srcPos + " dst.length=" + dst.length + " dstPos=" + dstPos + " length=" + length);
        } else if (length > ARRAYCOPY_SHORT_SHORT_ARRAY_THRESHOLD) {
            arraycopyBooleanUnchecked(src, srcPos, dst, dstPos, length);
        } else if (src != dst || srcPos >= dstPos || dstPos >= srcPos + length) {
            for (i = 0; i < length; i++) {
                dst[dstPos + i] = src[srcPos + i];
            }
        } else {
            for (i = length - 1; i >= 0; i--) {
                dst[dstPos + i] = src[srcPos + i];
            }
        }
    }

    public static void exit(int code) {
        Runtime.getRuntime().exit(code);
    }

    public static void gc() {
        synchronized (lock) {
            boolean shouldRunGC = justRanFinalization;
            if (shouldRunGC) {
                justRanFinalization = false;
            } else {
                runGC = true;
            }
        }
        if (shouldRunGC) {
            Runtime.getRuntime().gc();
        }
    }

    public static String getenv(String name) {
        if (name != null) {
            return Libcore.os.getenv(name);
        }
        throw new NullPointerException("name == null");
    }

    public static Map<String, String> getenv() {
        Map<String, String> map = new HashMap();
        for (String entry : Libcore.os.environ()) {
            int index = entry.indexOf(61);
            if (index != -1) {
                map.put(entry.substring(0, index), entry.substring(index + 1));
            }
        }
        return new SystemEnvironment(map);
    }

    public static Channel inheritedChannel() throws IOException {
        return SelectorProvider.provider().inheritedChannel();
    }

    public static Properties getProperties() {
        return systemProperties;
    }

    private static Properties initUnchangeableSystemProperties() {
        VMRuntime runtime = VMRuntime.getRuntime();
        Properties p = new Properties();
        String projectUrl = "http://www.android.com/";
        String projectName = "The Android Project";
        p.put("java.boot.class.path", runtime.bootClassPath());
        p.put("java.class.path", runtime.classPath());
        p.put("java.class.version", "50.0");
        p.put("java.compiler", XmlPullParser.NO_NAMESPACE);
        p.put("java.ext.dirs", XmlPullParser.NO_NAMESPACE);
        p.put("java.version", "0");
        String javaHome = getenv("JAVA_HOME");
        if (javaHome == null) {
            javaHome = "/system";
        }
        p.put("java.home", javaHome);
        p.put("java.specification.name", "Dalvik Core Library");
        p.put("java.specification.vendor", projectName);
        p.put("java.specification.version", "0.9");
        p.put("java.vendor", projectName);
        p.put("java.vendor.url", projectUrl);
        p.put("java.vm.name", "Dalvik");
        p.put("java.vm.specification.name", "Dalvik Virtual Machine Specification");
        p.put("java.vm.specification.vendor", projectName);
        p.put("java.vm.specification.version", "0.9");
        p.put("java.vm.vendor", projectName);
        p.put("java.vm.version", runtime.vmVersion());
        p.put("file.separator", "/");
        p.put("line.separator", "\n");
        p.put("path.separator", ":");
        p.put("java.runtime.name", "Android Runtime");
        p.put("java.runtime.version", "0.9");
        p.put("java.vm.vendor.url", projectUrl);
        p.put("file.encoding", "UTF-8");
        p.put("user.language", "en");
        p.put("user.region", "US");
        try {
            p.put("user.name", Libcore.os.getpwuid(Libcore.os.getuid()).pw_name);
            StructUtsname info = Libcore.os.uname();
            p.put("os.arch", info.machine);
            p.put("os.name", info.sysname);
            p.put("os.version", info.release);
            p.put("android.icu.library.version", ICU.getIcuVersion());
            p.put("android.icu.unicode.version", ICU.getUnicodeVersion());
            p.put("android.icu.cldr.version", ICU.getCldrVersion());
            parsePropertyAssignments(p, specialProperties());
            parsePropertyAssignments(p, runtime.properties());
            return p;
        } catch (Object exception) {
            throw new AssertionError(exception);
        }
    }

    private static void initUnchangeableSystemProperty(String name, String value) {
        checkPropertyName(name);
        unchangeableSystemProperties.put(name, value);
    }

    private static void setDefaultChangeableProperties(Properties p) {
        p.put("java.io.tmpdir", "/tmp");
        p.put("user.home", XmlPullParser.NO_NAMESPACE);
    }

    private static Properties createSystemProperties() {
        Properties p = new PropertiesWithNonOverrideableDefaults(unchangeableSystemProperties);
        setDefaultChangeableProperties(p);
        return p;
    }

    private static void parsePropertyAssignments(Properties p, String[] assignments) {
        for (String assignment : assignments) {
            int split = assignment.indexOf(61);
            p.put(assignment.substring(0, split), assignment.substring(split + 1));
        }
    }

    public static String getProperty(String propertyName) {
        return getProperty(propertyName, null);
    }

    public static String getProperty(String name, String defaultValue) {
        checkPropertyName(name);
        return systemProperties.getProperty(name, defaultValue);
    }

    public static String setProperty(String name, String value) {
        checkPropertyName(name);
        return (String) systemProperties.setProperty(name, value);
    }

    public static String clearProperty(String name) {
        checkPropertyName(name);
        return (String) systemProperties.remove(name);
    }

    private static void checkPropertyName(String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        } else if (name.isEmpty()) {
            throw new IllegalArgumentException("name is empty");
        }
    }

    public static Console console() {
        return Console.getConsole();
    }

    public static SecurityManager getSecurityManager() {
        return null;
    }

    public static String lineSeparator() {
        return lineSeparator;
    }

    public static void load(String pathName) {
        Runtime.getRuntime().load(pathName, VMStack.getCallingClassLoader());
    }

    public static void loadLibrary(String libName) {
        Runtime.getRuntime().loadLibrary(libName, VMStack.getCallingClassLoader());
    }

    public static void logE(String message) {
        log('E', message, null);
    }

    public static void logE(String message, Throwable th) {
        log('E', message, th);
    }

    public static void logI(String message) {
        log('I', message, null);
    }

    public static void logI(String message, Throwable th) {
        log('I', message, th);
    }

    public static void logW(String message) {
        log('W', message, null);
    }

    public static void logW(String message, Throwable th) {
        log('W', message, th);
    }

    public static void runFinalization() {
        synchronized (lock) {
            boolean shouldRunGC = runGC;
            runGC = false;
        }
        if (shouldRunGC) {
            Runtime.getRuntime().gc();
        }
        Runtime.getRuntime().runFinalization();
        synchronized (lock) {
            justRanFinalization = true;
        }
    }

    @Deprecated
    public static void runFinalizersOnExit(boolean flag) {
        Runtime.runFinalizersOnExit(flag);
    }

    public static void setProperties(Properties p) {
        PropertiesWithNonOverrideableDefaults userProperties = new PropertiesWithNonOverrideableDefaults(unchangeableSystemProperties);
        if (p != null) {
            userProperties.putAll(p);
        } else {
            setDefaultChangeableProperties(userProperties);
        }
        systemProperties = userProperties;
    }

    public static void setSecurityManager(SecurityManager sm) {
        if (sm != null) {
            throw new SecurityException();
        }
    }
}
