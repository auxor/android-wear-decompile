package android.app.backup;

import android.os.ParcelFileDescriptor;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FullBackup {
    public static final String APK_TREE_TOKEN = "a";
    public static final String APPS_PREFIX = "apps/";
    public static final String CACHE_TREE_TOKEN = "c";
    public static final String CONF_TOKEN_INTENT_EXTRA = "conftoken";
    public static final String DATABASE_TREE_TOKEN = "db";
    public static final String DATA_TREE_TOKEN = "f";
    public static final String FULL_BACKUP_INTENT_ACTION = "fullback";
    public static final String FULL_RESTORE_INTENT_ACTION = "fullrest";
    public static final String MANAGED_EXTERNAL_TREE_TOKEN = "ef";
    public static final String NO_BACKUP_TREE_TOKEN = "nb";
    public static final String OBB_TREE_TOKEN = "obb";
    public static final String ROOT_TREE_TOKEN = "r";
    public static final String SHAREDPREFS_TREE_TOKEN = "sp";
    public static final String SHARED_PREFIX = "shared/";
    public static final String SHARED_STORAGE_TOKEN = "shared";
    static final String TAG = "FullBackup";

    public static native int backupToTar(String str, String str2, String str3, String str4, String str5, BackupDataOutput backupDataOutput);

    public static void restoreFile(ParcelFileDescriptor data, long size, int type, long mode, long mtime, File outFile) throws IOException {
        if (type != 2) {
            FileOutputStream fileOutputStream = null;
            if (outFile != null) {
                try {
                    File parent = outFile.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outFile);
                } catch (IOException e) {
                    Log.e(TAG, "Unable to create/open file " + outFile.getPath(), e);
                }
            }
            byte[] buffer = new byte[AccessibilityNodeInfo.ACTION_PASTE];
            long origSize = size;
            FileInputStream in = new FileInputStream(data.getFileDescriptor());
            while (size > 0) {
                int got = in.read(buffer, 0, size > ((long) buffer.length) ? buffer.length : (int) size);
                if (got <= 0) {
                    Log.w(TAG, "Incomplete read: expected " + size + " but got " + (origSize - size));
                    break;
                }
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.write(buffer, 0, got);
                    } catch (IOException e2) {
                        Log.e(TAG, "Unable to write to file " + outFile.getPath(), e2);
                        fileOutputStream.close();
                        fileOutputStream = null;
                        outFile.delete();
                    }
                }
                size -= (long) got;
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        } else if (outFile != null) {
            outFile.mkdirs();
        }
        if (mode >= 0 && outFile != null) {
            try {
                Os.chmod(outFile.getPath(), (int) (mode & 448));
            } catch (ErrnoException e3) {
                e3.rethrowAsIOException();
            }
            outFile.setLastModified(mtime);
        }
    }
}
