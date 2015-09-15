package java.io;

import android.system.ErrnoException;
import android.system.OsConstants;
import android.system.StructStat;
import android.system.StructStatVfs;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import libcore.io.DeleteOnExit;
import libcore.io.Libcore;
import org.xmlpull.v1.XmlPullParser;

public class File implements Serializable, Comparable<File> {
    public static final String pathSeparator;
    public static final char pathSeparatorChar;
    public static final String separator;
    public static final char separatorChar;
    private static final long serialVersionUID = 301077366599181567L;
    private static final Random tempFileRandom;
    private String path;

    private static native String canonicalizePath(String str);

    private static native String[] listImpl(String str);

    private static native boolean setLastModifiedImpl(String str, long j);

    public boolean createNewFile() throws java.io.IOException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:16:? in {11, 12, 14, 15, 17, 18} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.rerun(BlockProcessor.java:44)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:57)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
        /*
        r6 = this;
        r1 = 0;
        r2 = libcore.io.Libcore.os;	 Catch:{ ErrnoException -> 0x0018, all -> 0x0029 }
        r3 = r6.path;	 Catch:{ ErrnoException -> 0x0018, all -> 0x0029 }
        r4 = android.system.OsConstants.O_RDWR;	 Catch:{ ErrnoException -> 0x0018, all -> 0x0029 }
        r5 = android.system.OsConstants.O_CREAT;	 Catch:{ ErrnoException -> 0x0018, all -> 0x0029 }
        r4 = r4 | r5;	 Catch:{ ErrnoException -> 0x0018, all -> 0x0029 }
        r5 = android.system.OsConstants.O_EXCL;	 Catch:{ ErrnoException -> 0x0018, all -> 0x0029 }
        r4 = r4 | r5;	 Catch:{ ErrnoException -> 0x0018, all -> 0x0029 }
        r5 = 384; // 0x180 float:5.38E-43 double:1.897E-321;	 Catch:{ ErrnoException -> 0x0018, all -> 0x0029 }
        r1 = r2.open(r3, r4, r5);	 Catch:{ ErrnoException -> 0x0018, all -> 0x0029 }
        r2 = 1;
        libcore.io.IoUtils.close(r1);
    L_0x0017:
        return r2;
    L_0x0018:
        r0 = move-exception;
        r2 = r0.errno;	 Catch:{ ErrnoException -> 0x0018, all -> 0x0029 }
        r3 = android.system.OsConstants.EEXIST;	 Catch:{ ErrnoException -> 0x0018, all -> 0x0029 }
        if (r2 != r3) goto L_0x0024;
    L_0x001f:
        r2 = 0;
        libcore.io.IoUtils.close(r1);
        goto L_0x0017;
    L_0x0024:
        r2 = r0.rethrowAsIOException();	 Catch:{ ErrnoException -> 0x0018, all -> 0x0029 }
        throw r2;	 Catch:{ ErrnoException -> 0x0018, all -> 0x0029 }
    L_0x0029:
        r2 = move-exception;
        libcore.io.IoUtils.close(r1);
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.io.File.createNewFile():boolean");
    }

    static {
        tempFileRandom = new Random();
        separatorChar = System.getProperty("file.separator", "/").charAt(0);
        pathSeparatorChar = System.getProperty("path.separator", ":").charAt(0);
        separator = String.valueOf(separatorChar);
        pathSeparator = String.valueOf(pathSeparatorChar);
    }

    public File(File dir, String name) {
        this(dir == null ? null : dir.getPath(), name);
    }

    public File(String path) {
        this.path = fixSlashes(path);
    }

    public File(String dirPath, String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        } else if (dirPath == null || dirPath.isEmpty()) {
            this.path = fixSlashes(name);
        } else if (name.isEmpty()) {
            this.path = fixSlashes(dirPath);
        } else {
            this.path = fixSlashes(join(dirPath, name));
        }
    }

    public File(URI uri) {
        checkURI(uri);
        this.path = fixSlashes(uri.getPath());
    }

    private static String fixSlashes(String origPath) {
        int newLength;
        boolean lastWasSlash = false;
        char[] newPath = origPath.toCharArray();
        int length = newPath.length;
        int i = 0;
        int newLength2 = 0;
        while (i < length) {
            char ch = newPath[i];
            if (ch != '/') {
                newLength = newLength2 + 1;
                newPath[newLength2] = ch;
                lastWasSlash = false;
            } else if (lastWasSlash) {
                newLength = newLength2;
            } else {
                newLength = newLength2 + 1;
                newPath[newLength2] = separatorChar;
                lastWasSlash = true;
            }
            i++;
            newLength2 = newLength;
        }
        if (!lastWasSlash || newLength2 <= 1) {
            newLength = newLength2;
        } else {
            newLength = newLength2 - 1;
        }
        return newLength != length ? new String(newPath, 0, newLength) : origPath;
    }

    private static String join(String prefix, String suffix) {
        boolean haveSlash;
        int prefixLength = prefix.length();
        if (prefixLength <= 0 || prefix.charAt(prefixLength - 1) != separatorChar) {
            haveSlash = false;
        } else {
            haveSlash = true;
        }
        if (!haveSlash) {
            if (suffix.length() <= 0 || suffix.charAt(0) != separatorChar) {
                haveSlash = false;
            } else {
                haveSlash = true;
            }
        }
        return haveSlash ? prefix + suffix : prefix + separatorChar + suffix;
    }

    private static void checkURI(URI uri) {
        if (!uri.isAbsolute()) {
            throw new IllegalArgumentException("URI is not absolute: " + uri);
        } else if (!uri.getRawSchemeSpecificPart().startsWith("/")) {
            throw new IllegalArgumentException("URI is not hierarchical: " + uri);
        } else if ("file".equals(uri.getScheme())) {
            String rawPath = uri.getRawPath();
            if (rawPath == null || rawPath.isEmpty()) {
                throw new IllegalArgumentException("Expected non-empty path in URI: " + uri);
            } else if (uri.getRawAuthority() != null) {
                throw new IllegalArgumentException("Found authority in URI: " + uri);
            } else if (uri.getRawQuery() != null) {
                throw new IllegalArgumentException("Found query in URI: " + uri);
            } else if (uri.getRawFragment() != null) {
                throw new IllegalArgumentException("Found fragment in URI: " + uri);
            }
        } else {
            throw new IllegalArgumentException("Expected file scheme in URI: " + uri);
        }
    }

    public static File[] listRoots() {
        return new File[]{new File("/")};
    }

    public boolean canExecute() {
        return doAccess(OsConstants.X_OK);
    }

    public boolean canRead() {
        return doAccess(OsConstants.R_OK);
    }

    public boolean canWrite() {
        return doAccess(OsConstants.W_OK);
    }

    private boolean doAccess(int mode) {
        try {
            return Libcore.os.access(this.path, mode);
        } catch (ErrnoException e) {
            return false;
        }
    }

    public int compareTo(File another) {
        return getPath().compareTo(another.getPath());
    }

    public boolean delete() {
        try {
            Libcore.os.remove(this.path);
            return true;
        } catch (ErrnoException e) {
            return false;
        }
    }

    public void deleteOnExit() {
        DeleteOnExit.getInstance().addFile(getAbsolutePath());
    }

    public boolean equals(Object obj) {
        if (obj instanceof File) {
            return this.path.equals(((File) obj).getPath());
        }
        return false;
    }

    public boolean exists() {
        return doAccess(OsConstants.F_OK);
    }

    public String getAbsolutePath() {
        if (isAbsolute()) {
            return this.path;
        }
        String userDir = System.getProperty("user.dir");
        return !this.path.isEmpty() ? join(userDir, this.path) : userDir;
    }

    public File getAbsoluteFile() {
        return new File(getAbsolutePath());
    }

    public String getCanonicalPath() throws IOException {
        return canonicalizePath(getAbsolutePath());
    }

    public File getCanonicalFile() throws IOException {
        return new File(getCanonicalPath());
    }

    public String getName() {
        int separatorIndex = this.path.lastIndexOf(separator);
        return separatorIndex < 0 ? this.path : this.path.substring(separatorIndex + 1, this.path.length());
    }

    public String getParent() {
        int length = this.path.length();
        int firstInPath = 0;
        if (separatorChar == '\\' && length > 2 && this.path.charAt(1) == ':') {
            firstInPath = 2;
        }
        int index = this.path.lastIndexOf(separatorChar);
        if (index == -1 && firstInPath > 0) {
            index = 2;
        }
        if (index == -1 || this.path.charAt(length - 1) == separatorChar) {
            return null;
        }
        if (this.path.indexOf(separatorChar) == index && this.path.charAt(firstInPath) == separatorChar) {
            return this.path.substring(0, index + 1);
        }
        return this.path.substring(0, index);
    }

    public File getParentFile() {
        String tempParent = getParent();
        if (tempParent == null) {
            return null;
        }
        return new File(tempParent);
    }

    public String getPath() {
        return this.path;
    }

    public int hashCode() {
        return getPath().hashCode() ^ 1234321;
    }

    public boolean isAbsolute() {
        return this.path.length() > 0 && this.path.charAt(0) == separatorChar;
    }

    public boolean isDirectory() {
        try {
            return OsConstants.S_ISDIR(Libcore.os.stat(this.path).st_mode);
        } catch (ErrnoException e) {
            return false;
        }
    }

    public boolean isFile() {
        try {
            return OsConstants.S_ISREG(Libcore.os.stat(this.path).st_mode);
        } catch (ErrnoException e) {
            return false;
        }
    }

    public boolean isHidden() {
        if (this.path.isEmpty()) {
            return false;
        }
        return getName().startsWith(".");
    }

    public long lastModified() {
        try {
            return Libcore.os.stat(this.path).st_mtime * 1000;
        } catch (ErrnoException e) {
            return 0;
        }
    }

    public boolean setLastModified(long time) {
        if (time >= 0) {
            return setLastModifiedImpl(this.path, time);
        }
        throw new IllegalArgumentException("time < 0");
    }

    public boolean setReadOnly() {
        return setWritable(false, false);
    }

    public boolean setExecutable(boolean executable, boolean ownerOnly) {
        return doChmod(ownerOnly ? OsConstants.S_IXUSR : (OsConstants.S_IXUSR | OsConstants.S_IXGRP) | OsConstants.S_IXOTH, executable);
    }

    public boolean setExecutable(boolean executable) {
        return setExecutable(executable, true);
    }

    public boolean setReadable(boolean readable, boolean ownerOnly) {
        return doChmod(ownerOnly ? OsConstants.S_IRUSR : (OsConstants.S_IRUSR | OsConstants.S_IRGRP) | OsConstants.S_IROTH, readable);
    }

    public boolean setReadable(boolean readable) {
        return setReadable(readable, true);
    }

    public boolean setWritable(boolean writable, boolean ownerOnly) {
        return doChmod(ownerOnly ? OsConstants.S_IWUSR : (OsConstants.S_IWUSR | OsConstants.S_IWGRP) | OsConstants.S_IWOTH, writable);
    }

    public boolean setWritable(boolean writable) {
        return setWritable(writable, true);
    }

    private boolean doChmod(int mask, boolean set) {
        try {
            StructStat sb = Libcore.os.stat(this.path);
            Libcore.os.chmod(this.path, set ? sb.st_mode | mask : sb.st_mode & (mask ^ -1));
            return true;
        } catch (ErrnoException e) {
            return false;
        }
    }

    public long length() {
        try {
            return Libcore.os.stat(this.path).st_size;
        } catch (ErrnoException e) {
            return 0;
        }
    }

    public String[] list() {
        return listImpl(this.path);
    }

    public String[] list(FilenameFilter filter) {
        String[] filenames = list();
        if (filter == null || filenames == null) {
            return filenames;
        }
        List<String> result = new ArrayList(filenames.length);
        for (String filename : filenames) {
            if (filter.accept(this, filename)) {
                result.add(filename);
            }
        }
        return (String[]) result.toArray(new String[result.size()]);
    }

    public File[] listFiles() {
        return filenamesToFiles(list());
    }

    public File[] listFiles(FilenameFilter filter) {
        return filenamesToFiles(list(filter));
    }

    public File[] listFiles(FileFilter filter) {
        File[] files = listFiles();
        if (filter == null || files == null) {
            return files;
        }
        List<File> result = new ArrayList(files.length);
        for (File file : files) {
            if (filter.accept(file)) {
                result.add(file);
            }
        }
        return (File[]) result.toArray(new File[result.size()]);
    }

    private File[] filenamesToFiles(String[] filenames) {
        if (filenames == null) {
            return null;
        }
        int count = filenames.length;
        File[] result = new File[count];
        for (int i = 0; i < count; i++) {
            result[i] = new File(this, filenames[i]);
        }
        return result;
    }

    public boolean mkdir() {
        try {
            mkdirErrno();
            return true;
        } catch (ErrnoException e) {
            return false;
        }
    }

    private void mkdirErrno() throws ErrnoException {
        Libcore.os.mkdir(this.path, OsConstants.S_IRWXU);
    }

    public boolean mkdirs() {
        return mkdirs(false);
    }

    private boolean mkdirs(boolean resultIfExists) {
        try {
            mkdirErrno();
            return true;
        } catch (ErrnoException errnoException) {
            if (errnoException.errno == OsConstants.ENOENT) {
                File parent = getParentFile();
                if (parent != null && parent.mkdirs(true) && mkdir()) {
                    return true;
                }
                return false;
            } else if (errnoException.errno == OsConstants.EEXIST) {
                return resultIfExists;
            } else {
                return false;
            }
        }
    }

    public static File createTempFile(String prefix, String suffix) throws IOException {
        return createTempFile(prefix, suffix, null);
    }

    public static File createTempFile(String prefix, String suffix, File directory) throws IOException {
        if (prefix.length() < 3) {
            throw new IllegalArgumentException("prefix must be at least 3 characters");
        }
        File result;
        if (suffix == null) {
            suffix = ".tmp";
        }
        File tmpDirFile = directory;
        if (tmpDirFile == null) {
            tmpDirFile = new File(System.getProperty("java.io.tmpdir", "."));
        }
        do {
            result = new File(tmpDirFile, prefix + tempFileRandom.nextInt() + suffix);
        } while (!result.createNewFile());
        return result;
    }

    public boolean renameTo(File newPath) {
        try {
            Libcore.os.rename(this.path, newPath.path);
            return true;
        } catch (ErrnoException e) {
            return false;
        }
    }

    public String toString() {
        return this.path;
    }

    public URI toURI() {
        String name = getAbsoluteName();
        try {
            if (!name.startsWith("/")) {
                return new URI("file", null, "/" + name, null, null);
            }
            if (name.startsWith("//")) {
                return new URI("file", XmlPullParser.NO_NAMESPACE, name, null);
            }
            return new URI("file", null, name, null, null);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    @Deprecated
    public URL toURL() throws MalformedURLException {
        String name = getAbsoluteName();
        if (!name.startsWith("/")) {
            return new URL("file", XmlPullParser.NO_NAMESPACE, -1, "/" + name, null);
        }
        if (name.startsWith("//")) {
            return new URL("file:" + name);
        }
        return new URL("file", XmlPullParser.NO_NAMESPACE, -1, name, null);
    }

    private String getAbsoluteName() {
        File f = getAbsoluteFile();
        String name = f.getPath();
        if (f.isDirectory() && name.charAt(name.length() - 1) != separatorChar) {
            name = name + "/";
        }
        if (separatorChar != '/') {
            return name.replace(separatorChar, '/');
        }
        return name;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeChar(separatorChar);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.path = fixSlashes(this.path.replace(stream.readChar(), separatorChar));
    }

    public long getTotalSpace() {
        try {
            StructStatVfs sb = Libcore.os.statvfs(this.path);
            return sb.f_blocks * sb.f_bsize;
        } catch (ErrnoException e) {
            return 0;
        }
    }

    public long getUsableSpace() {
        try {
            StructStatVfs sb = Libcore.os.statvfs(this.path);
            return sb.f_bavail * sb.f_bsize;
        } catch (ErrnoException e) {
            return 0;
        }
    }

    public long getFreeSpace() {
        try {
            StructStatVfs sb = Libcore.os.statvfs(this.path);
            return sb.f_bfree * sb.f_bsize;
        } catch (ErrnoException e) {
            return 0;
        }
    }
}
