package dalvik.system;

import android.system.ErrnoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import libcore.io.Libcore;

public final class DexFile {
    public static final byte DEXOPT_NEEDED = (byte) 2;
    public static final byte PATCHOAT_NEEDED = (byte) 1;
    public static final byte UP_TO_DATE = (byte) 0;
    private final CloseGuard guard;
    private long mCookie;
    private final String mFileName;

    private class DFEnum implements Enumeration<String> {
        private int mIndex;
        private String[] mNameList;

        DFEnum(DexFile df) {
            this.mIndex = 0;
            this.mNameList = DexFile.getClassNameList(DexFile.this.mCookie);
        }

        public boolean hasMoreElements() {
            return this.mIndex < this.mNameList.length;
        }

        public String nextElement() {
            String[] strArr = this.mNameList;
            int i = this.mIndex;
            this.mIndex = i + 1;
            return strArr[i];
        }
    }

    private static native void closeDexFile(long j);

    private static native Class defineClassNative(String str, ClassLoader classLoader, long j) throws ClassNotFoundException, NoClassDefFoundError;

    private static native String[] getClassNameList(long j);

    public static native boolean isDexOptNeeded(String str) throws FileNotFoundException, IOException;

    public static native byte isDexOptNeededInternal(String str, String str2, String str3, boolean z) throws FileNotFoundException, IOException;

    private static native long openDexFileNative(String str, String str2, int i);

    public DexFile(File file) throws IOException {
        this(file.getPath());
    }

    public DexFile(String fileName) throws IOException {
        this.guard = CloseGuard.get();
        this.mCookie = openDexFile(fileName, null, 0);
        this.mFileName = fileName;
        this.guard.open("close");
    }

    private DexFile(String sourceName, String outputName, int flags) throws IOException {
        this.guard = CloseGuard.get();
        if (outputName != null) {
            try {
                String parent = new File(outputName).getParent();
                if (Libcore.os.getuid() != Libcore.os.stat(parent).st_uid) {
                    throw new IllegalArgumentException("Optimized data directory " + parent + " is not owned by the current user. Shared storage cannot protect" + " your application from code injection attacks.");
                }
            } catch (ErrnoException e) {
            }
        }
        this.mCookie = openDexFile(sourceName, outputName, flags);
        this.mFileName = sourceName;
        this.guard.open("close");
    }

    public static DexFile loadDex(String sourcePathName, String outputPathName, int flags) throws IOException {
        return new DexFile(sourcePathName, outputPathName, flags);
    }

    public String getName() {
        return this.mFileName;
    }

    public String toString() {
        return getName();
    }

    public void close() throws IOException {
        if (this.mCookie != 0) {
            this.guard.close();
            closeDexFile(this.mCookie);
            this.mCookie = 0;
        }
    }

    public Class loadClass(String name, ClassLoader loader) {
        return loadClassBinaryName(name.replace('.', '/'), loader, null);
    }

    public Class loadClassBinaryName(String name, ClassLoader loader, List<Throwable> suppressed) {
        return defineClass(name, loader, this.mCookie, suppressed);
    }

    private static Class defineClass(String name, ClassLoader loader, long cookie, List<Throwable> suppressed) {
        Class result = null;
        try {
            result = defineClassNative(name, loader, cookie);
        } catch (NoClassDefFoundError e) {
            if (suppressed != null) {
                suppressed.add(e);
            }
        } catch (ClassNotFoundException e2) {
            if (suppressed != null) {
                suppressed.add(e2);
            }
        }
        return result;
    }

    public Enumeration<String> entries() {
        return new DFEnum(this);
    }

    protected void finalize() throws Throwable {
        try {
            if (this.guard != null) {
                this.guard.warnIfOpen();
            }
            close();
        } finally {
            super.finalize();
        }
    }

    private static long openDexFile(String sourceName, String outputName, int flags) throws IOException {
        return openDexFileNative(new File(sourceName).getAbsolutePath(), outputName == null ? null : new File(outputName).getAbsolutePath(), flags);
    }
}
