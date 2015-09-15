package android.os;

import android.net.ProxyInfo;
import android.system.ErrnoException;
import android.system.Os;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

public class FileUtils {
    private static final Pattern SAFE_FILENAME_PATTERN;
    public static final int S_IRGRP = 32;
    public static final int S_IROTH = 4;
    public static final int S_IRUSR = 256;
    public static final int S_IRWXG = 56;
    public static final int S_IRWXO = 7;
    public static final int S_IRWXU = 448;
    public static final int S_IWGRP = 16;
    public static final int S_IWOTH = 2;
    public static final int S_IWUSR = 128;
    public static final int S_IXGRP = 8;
    public static final int S_IXOTH = 1;
    public static final int S_IXUSR = 64;
    private static final String TAG = "FileUtils";

    static {
        SAFE_FILENAME_PATTERN = Pattern.compile("[\\w%+,./=_-]+");
    }

    public static int setPermissions(File path, int mode, int uid, int gid) {
        return setPermissions(path.getAbsolutePath(), mode, uid, gid);
    }

    public static int setPermissions(String path, int mode, int uid, int gid) {
        try {
            Os.chmod(path, mode);
            if (uid >= 0 || gid >= 0) {
                try {
                    Os.chown(path, uid, gid);
                } catch (ErrnoException e) {
                    Slog.w(TAG, "Failed to chown(" + path + "): " + e);
                    return e.errno;
                }
            }
            return 0;
        } catch (ErrnoException e2) {
            Slog.w(TAG, "Failed to chmod(" + path + "): " + e2);
            return e2.errno;
        }
    }

    public static int setPermissions(FileDescriptor fd, int mode, int uid, int gid) {
        try {
            Os.fchmod(fd, mode);
            if (uid >= 0 || gid >= 0) {
                try {
                    Os.fchown(fd, uid, gid);
                } catch (ErrnoException e) {
                    Slog.w(TAG, "Failed to fchown(): " + e);
                    return e.errno;
                }
            }
            return 0;
        } catch (ErrnoException e2) {
            Slog.w(TAG, "Failed to fchmod(): " + e2);
            return e2.errno;
        }
    }

    public static int getUid(String path) {
        try {
            return Os.stat(path).st_uid;
        } catch (ErrnoException e) {
            return -1;
        }
    }

    public static boolean sync(FileOutputStream stream) {
        if (stream != null) {
            try {
                stream.getFD().sync();
            } catch (IOException e) {
                return false;
            }
        }
        return true;
    }

    public static boolean copyFile(File srcFile, File destFile) {
        InputStream in;
        try {
            in = new FileInputStream(srcFile);
            boolean result = copyToFile(in, destFile);
            in.close();
            return result;
        } catch (IOException e) {
            return false;
        } catch (Throwable th) {
            in.close();
        }
    }

    public static boolean copyToFile(InputStream inputStream, File destFile) {
        FileOutputStream out;
        try {
            if (destFile.exists()) {
                destFile.delete();
            }
            out = new FileOutputStream(destFile);
            byte[] buffer = new byte[AccessibilityNodeInfo.ACTION_SCROLL_FORWARD];
            while (true) {
                int bytesRead = inputStream.read(buffer);
                if (bytesRead < 0) {
                    break;
                }
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
            try {
                out.getFD().sync();
            } catch (IOException e) {
            }
            out.close();
            return true;
        } catch (IOException e2) {
            return false;
        } catch (Throwable th) {
            out.flush();
            try {
                out.getFD().sync();
            } catch (IOException e3) {
            }
            out.close();
        }
    }

    public static boolean isFilenameSafe(File file) {
        return SAFE_FILENAME_PATTERN.matcher(file.getPath()).matches();
    }

    public static String readTextFile(File file, int max, String ellipsis) throws IOException {
        InputStream input = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(input);
        try {
            long size = file.length();
            byte[] data;
            String str;
            if (max > 0 || (size > 0 && max == 0)) {
                if (size > 0 && (max == 0 || size < ((long) max))) {
                    max = (int) size;
                }
                data = new byte[(max + S_IXOTH)];
                int length = bis.read(data);
                if (length <= 0) {
                    str = ProxyInfo.LOCAL_EXCL_LIST;
                } else if (length <= max) {
                    str = new String(data, 0, length);
                    bis.close();
                    input.close();
                } else if (ellipsis == null) {
                    str = new String(data, 0, max);
                    bis.close();
                    input.close();
                } else {
                    str = new String(data, 0, max) + ellipsis;
                    bis.close();
                    input.close();
                }
                return str;
            } else if (max < 0) {
                boolean rolled = false;
                byte[] last = null;
                data = null;
                while (true) {
                    if (last != null) {
                        rolled = true;
                    }
                    byte[] tmp = last;
                    last = data;
                    data = tmp;
                    if (data == null) {
                        data = new byte[(-max)];
                    }
                    len = bis.read(data);
                    if (len != data.length) {
                        break;
                    }
                }
                if (last == null && len <= 0) {
                    str = ProxyInfo.LOCAL_EXCL_LIST;
                    bis.close();
                    input.close();
                    return str;
                } else if (last == null) {
                    str = new String(data, 0, len);
                    bis.close();
                    input.close();
                    return str;
                } else {
                    if (len > 0) {
                        rolled = true;
                        System.arraycopy(last, len, last, 0, last.length - len);
                        System.arraycopy(data, 0, last, last.length - len, len);
                    }
                    if (ellipsis == null || !rolled) {
                        str = new String(last);
                        bis.close();
                        input.close();
                        return str;
                    }
                    str = ellipsis + new String(last);
                    bis.close();
                    input.close();
                    return str;
                }
            } else {
                ByteArrayOutputStream contents = new ByteArrayOutputStream();
                data = new byte[AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT];
                while (true) {
                    len = bis.read(data);
                    if (len > 0) {
                        contents.write(data, 0, len);
                    }
                    if (len != data.length) {
                        break;
                    }
                }
                str = contents.toString();
                bis.close();
                input.close();
                return str;
            }
        } finally {
            bis.close();
            input.close();
        }
    }

    public static void stringToFile(String filename, String string) throws IOException {
        FileWriter out = new FileWriter(filename);
        try {
            out.write(string);
        } finally {
            out.close();
        }
    }

    public static long checksumCrc32(File file) throws FileNotFoundException, IOException {
        Throwable th;
        CRC32 checkSummer = new CRC32();
        CheckedInputStream cis = null;
        try {
            CheckedInputStream cis2 = new CheckedInputStream(new FileInputStream(file), checkSummer);
            try {
                do {
                } while (cis2.read(new byte[S_IWUSR]) >= 0);
                long value = checkSummer.getValue();
                if (cis2 != null) {
                    try {
                        cis2.close();
                    } catch (IOException e) {
                    }
                }
                return value;
            } catch (Throwable th2) {
                th = th2;
                cis = cis2;
                if (cis != null) {
                    try {
                        cis.close();
                    } catch (IOException e2) {
                    }
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            if (cis != null) {
                cis.close();
            }
            throw th;
        }
    }

    public static boolean deleteOlderFiles(File dir, int minCount, long minAge) {
        if (minCount < 0 || minAge < 0) {
            throw new IllegalArgumentException("Constraints must be positive or 0");
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return false;
        }
        Arrays.sort(files, new Comparator<File>() {
            public int compare(java.io.File r1, java.io.File r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.os.FileUtils.1.compare(java.io.File, java.io.File):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.os.FileUtils.1.compare(java.io.File, java.io.File):int
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
                throw new UnsupportedOperationException("Method not decompiled: android.os.FileUtils.1.compare(java.io.File, java.io.File):int");
            }
        });
        boolean deleted = false;
        for (int i = minCount; i < files.length; i += S_IXOTH) {
            File file = files[i];
            if (System.currentTimeMillis() - file.lastModified() > minAge && file.delete()) {
                Log.d(TAG, "Deleted old file " + file);
                deleted = true;
            }
        }
        return deleted;
    }

    public static boolean contains(File dir, File file) {
        if (file == null) {
            return false;
        }
        String dirPath = dir.getAbsolutePath();
        String filePath = file.getAbsolutePath();
        if (dirPath.equals(filePath)) {
            return true;
        }
        if (!dirPath.endsWith("/")) {
            dirPath = dirPath + "/";
        }
        return filePath.startsWith(dirPath);
    }

    public static boolean deleteContents(File dir) {
        File[] files = dir.listFiles();
        boolean success = true;
        if (files != null) {
            File[] arr$ = files;
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$ += S_IXOTH) {
                File file = arr$[i$];
                if (file.isDirectory()) {
                    success &= deleteContents(file);
                }
                if (!file.delete()) {
                    Log.w(TAG, "Failed to delete " + file);
                    success = false;
                }
            }
        }
        return success;
    }

    private static boolean isValidExtFilenameChar(char c) {
        switch (c) {
            case Toast.LENGTH_SHORT /*0*/:
            case MotionEvent.AXIS_GENERIC_16 /*47*/:
                return false;
            default:
                return true;
        }
    }

    public static boolean isValidExtFilename(String name) {
        return name != null && name.equals(buildValidExtFilename(name));
    }

    public static String buildValidExtFilename(String name) {
        if (TextUtils.isEmpty(name) || ".".equals(name) || "..".equals(name)) {
            return "(invalid)";
        }
        StringBuilder res = new StringBuilder(name.length());
        for (int i = 0; i < name.length(); i += S_IXOTH) {
            char c = name.charAt(i);
            if (isValidExtFilenameChar(c)) {
                res.append(c);
            } else {
                res.append('_');
            }
        }
        return res.toString();
    }

    private static boolean isValidFatFilenameChar(char c) {
        if (c >= '\u0000' && c <= '\u001f') {
            return false;
        }
        switch (c) {
            case MotionEvent.AXIS_GENERIC_3 /*34*/:
            case MotionEvent.AXIS_GENERIC_11 /*42*/:
            case MotionEvent.AXIS_GENERIC_16 /*47*/:
            case KeyEvent.KEYCODE_ALT_RIGHT /*58*/:
            case KeyEvent.KEYCODE_SHIFT_RIGHT /*60*/:
            case KeyEvent.KEYCODE_SPACE /*62*/:
            case KeyEvent.KEYCODE_SYM /*63*/:
            case KeyEvent.KEYCODE_PAGE_UP /*92*/:
            case KeyEvent.KEYCODE_INSERT /*124*/:
            case KeyEvent.KEYCODE_MEDIA_PAUSE /*127*/:
                return false;
            default:
                return true;
        }
    }

    public static boolean isValidFatFilename(String name) {
        return name != null && name.equals(buildValidFatFilename(name));
    }

    public static String buildValidFatFilename(String name) {
        if (TextUtils.isEmpty(name) || ".".equals(name) || "..".equals(name)) {
            return "(invalid)";
        }
        StringBuilder res = new StringBuilder(name.length());
        for (int i = 0; i < name.length(); i += S_IXOTH) {
            char c = name.charAt(i);
            if (isValidFatFilenameChar(c)) {
                res.append(c);
            } else {
                res.append('_');
            }
        }
        return res.toString();
    }

    public static String rewriteAfterRename(File beforeDir, File afterDir, String path) {
        if (path == null) {
            return null;
        }
        File result = rewriteAfterRename(beforeDir, afterDir, new File(path));
        if (result != null) {
            return result.getAbsolutePath();
        }
        return null;
    }

    public static String[] rewriteAfterRename(File beforeDir, File afterDir, String[] paths) {
        if (paths == null) {
            return null;
        }
        String[] result = new String[paths.length];
        for (int i = 0; i < paths.length; i += S_IXOTH) {
            result[i] = rewriteAfterRename(beforeDir, afterDir, paths[i]);
        }
        return result;
    }

    public static File rewriteAfterRename(File beforeDir, File afterDir, File file) {
        if (file != null && contains(beforeDir, file)) {
            return new File(afterDir, file.getAbsolutePath().substring(beforeDir.getAbsolutePath().length()));
        }
        return null;
    }
}
