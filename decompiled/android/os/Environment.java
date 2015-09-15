package android.os;

import android.content.Context;
import android.os.storage.IMountService.Stub;
import android.os.storage.StorageVolume;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Environment {
    private static final String CANONCIAL_EMULATED_STORAGE_TARGET;
    private static final File DATA_DIRECTORY;
    public static String DIRECTORY_ALARMS = null;
    @Deprecated
    public static final String DIRECTORY_ANDROID = "Android";
    public static String DIRECTORY_DCIM = null;
    public static String DIRECTORY_DOCUMENTS = null;
    public static String DIRECTORY_DOWNLOADS = null;
    public static String DIRECTORY_MOVIES = null;
    public static String DIRECTORY_MUSIC = null;
    public static String DIRECTORY_NOTIFICATIONS = null;
    public static String DIRECTORY_PICTURES = null;
    public static String DIRECTORY_PODCASTS = null;
    public static String DIRECTORY_RINGTONES = null;
    public static final String DIR_ANDROID = "Android";
    private static final File DIR_ANDROID_ROOT;
    private static final String DIR_CACHE = "cache";
    private static final String DIR_DATA = "data";
    private static final String DIR_FILES = "files";
    private static final String DIR_MEDIA = "media";
    private static final File DIR_MEDIA_STORAGE;
    private static final String DIR_OBB = "obb";
    private static final File DIR_OEM_ROOT;
    private static final File DIR_VENDOR_ROOT;
    private static final File DOWNLOAD_CACHE_DIRECTORY;
    private static final String ENV_ANDROID_ROOT = "ANDROID_ROOT";
    private static final String ENV_EMULATED_STORAGE_SOURCE = "EMULATED_STORAGE_SOURCE";
    private static final String ENV_EMULATED_STORAGE_TARGET = "EMULATED_STORAGE_TARGET";
    private static final String ENV_EXTERNAL_STORAGE = "EXTERNAL_STORAGE";
    private static final String ENV_MEDIA_STORAGE = "MEDIA_STORAGE";
    private static final String ENV_OEM_ROOT = "OEM_ROOT";
    private static final String ENV_SECONDARY_STORAGE = "SECONDARY_STORAGE";
    private static final String ENV_VENDOR_ROOT = "VENDOR_ROOT";
    public static final String MEDIA_BAD_REMOVAL = "bad_removal";
    public static final String MEDIA_CHECKING = "checking";
    public static final String MEDIA_MOUNTED = "mounted";
    public static final String MEDIA_MOUNTED_READ_ONLY = "mounted_ro";
    public static final String MEDIA_NOFS = "nofs";
    public static final String MEDIA_REMOVED = "removed";
    public static final String MEDIA_SHARED = "shared";
    public static final String MEDIA_UNKNOWN = "unknown";
    public static final String MEDIA_UNMOUNTABLE = "unmountable";
    public static final String MEDIA_UNMOUNTED = "unmounted";
    private static final File SECURE_DATA_DIRECTORY;
    private static final String SYSTEM_PROPERTY_EFS_ENABLED = "persist.security.efs.enabled";
    private static final String TAG = "Environment";
    private static UserEnvironment sCurrentUser;
    private static boolean sUserRequired;

    public static class UserEnvironment {
        private final File mEmulatedDirForDirect;
        private final File[] mExternalDirsForApp;
        private final File[] mExternalDirsForVold;

        public UserEnvironment(int userId) {
            String rawExternalStorage = System.getenv(Environment.ENV_EXTERNAL_STORAGE);
            String rawEmulatedSource = System.getenv(Environment.ENV_EMULATED_STORAGE_SOURCE);
            String rawEmulatedTarget = System.getenv(Environment.ENV_EMULATED_STORAGE_TARGET);
            String rawMediaStorage = System.getenv(Environment.ENV_MEDIA_STORAGE);
            if (TextUtils.isEmpty(rawMediaStorage)) {
                rawMediaStorage = "/data/media";
            }
            ArrayList<File> externalForVold = Lists.newArrayList();
            ArrayList<File> externalForApp = Lists.newArrayList();
            if (TextUtils.isEmpty(rawEmulatedTarget)) {
                if (TextUtils.isEmpty(rawExternalStorage)) {
                    Log.w(Environment.TAG, "EXTERNAL_STORAGE undefined; falling back to default");
                    rawExternalStorage = "/storage/sdcard0";
                }
                externalForVold.add(new File(rawExternalStorage));
                externalForApp.add(new File(rawExternalStorage));
                this.mEmulatedDirForDirect = new File(rawMediaStorage);
            } else {
                String rawUserId = Integer.toString(userId);
                File emulatedSourceBase = new File(rawEmulatedSource);
                File emulatedTargetBase = new File(rawEmulatedTarget);
                File mediaBase = new File(rawMediaStorage);
                externalForVold.add(Environment.buildPath(emulatedSourceBase, rawUserId));
                externalForApp.add(Environment.buildPath(emulatedTargetBase, rawUserId));
                this.mEmulatedDirForDirect = Environment.buildPath(mediaBase, rawUserId);
            }
            String rawSecondaryStorage = System.getenv(Environment.ENV_SECONDARY_STORAGE);
            if (!TextUtils.isEmpty(rawSecondaryStorage) && userId == 0) {
                for (String secondaryPath : rawSecondaryStorage.split(":")) {
                    externalForVold.add(new File(secondaryPath));
                    externalForApp.add(new File(secondaryPath));
                }
            }
            this.mExternalDirsForVold = (File[]) externalForVold.toArray(new File[externalForVold.size()]);
            this.mExternalDirsForApp = (File[]) externalForApp.toArray(new File[externalForApp.size()]);
        }

        @Deprecated
        public File getExternalStorageDirectory() {
            return this.mExternalDirsForApp[0];
        }

        @Deprecated
        public File getExternalStoragePublicDirectory(String type) {
            return buildExternalStoragePublicDirs(type)[0];
        }

        public File[] getExternalDirsForVold() {
            return this.mExternalDirsForVold;
        }

        public File[] getExternalDirsForApp() {
            return this.mExternalDirsForApp;
        }

        public File getMediaDir() {
            return this.mEmulatedDirForDirect;
        }

        public File[] buildExternalStoragePublicDirs(String type) {
            return Environment.buildPaths(this.mExternalDirsForApp, type);
        }

        public File[] buildExternalStorageAndroidDataDirs() {
            return Environment.buildPaths(this.mExternalDirsForApp, Environment.DIR_ANDROID, Environment.DIR_DATA);
        }

        public File[] buildExternalStorageAndroidObbDirs() {
            return Environment.buildPaths(this.mExternalDirsForApp, Environment.DIR_ANDROID, Environment.DIR_OBB);
        }

        public File[] buildExternalStorageAppDataDirs(String packageName) {
            return Environment.buildPaths(this.mExternalDirsForApp, Environment.DIR_ANDROID, Environment.DIR_DATA, packageName);
        }

        public File[] buildExternalStorageAppDataDirsForVold(String packageName) {
            return Environment.buildPaths(this.mExternalDirsForVold, Environment.DIR_ANDROID, Environment.DIR_DATA, packageName);
        }

        public File[] buildExternalStorageAppMediaDirs(String packageName) {
            return Environment.buildPaths(this.mExternalDirsForApp, Environment.DIR_ANDROID, Environment.DIR_MEDIA, packageName);
        }

        public File[] buildExternalStorageAppMediaDirsForVold(String packageName) {
            return Environment.buildPaths(this.mExternalDirsForVold, Environment.DIR_ANDROID, Environment.DIR_MEDIA, packageName);
        }

        public File[] buildExternalStorageAppObbDirs(String packageName) {
            return Environment.buildPaths(this.mExternalDirsForApp, Environment.DIR_ANDROID, Environment.DIR_OBB, packageName);
        }

        public File[] buildExternalStorageAppObbDirsForVold(String packageName) {
            return Environment.buildPaths(this.mExternalDirsForVold, Environment.DIR_ANDROID, Environment.DIR_OBB, packageName);
        }

        public File[] buildExternalStorageAppFilesDirs(String packageName) {
            return Environment.buildPaths(this.mExternalDirsForApp, Environment.DIR_ANDROID, Environment.DIR_DATA, packageName, Environment.DIR_FILES);
        }

        public File[] buildExternalStorageAppCacheDirs(String packageName) {
            return Environment.buildPaths(this.mExternalDirsForApp, Environment.DIR_ANDROID, Environment.DIR_DATA, packageName, Environment.DIR_CACHE);
        }
    }

    static {
        DIR_ANDROID_ROOT = getDirectory(ENV_ANDROID_ROOT, "/system");
        DIR_OEM_ROOT = getDirectory(ENV_OEM_ROOT, "/oem");
        DIR_VENDOR_ROOT = getDirectory(ENV_VENDOR_ROOT, "/vendor");
        DIR_MEDIA_STORAGE = getDirectory(ENV_MEDIA_STORAGE, "/data/media");
        CANONCIAL_EMULATED_STORAGE_TARGET = getCanonicalPathOrNull(ENV_EMULATED_STORAGE_TARGET);
        initForCurrentUser();
        DATA_DIRECTORY = getDirectory("ANDROID_DATA", "/data");
        SECURE_DATA_DIRECTORY = getDirectory("ANDROID_SECURE_DATA", "/data/secure");
        DOWNLOAD_CACHE_DIRECTORY = getDirectory("DOWNLOAD_CACHE", "/cache");
        DIRECTORY_MUSIC = "Music";
        DIRECTORY_PODCASTS = "Podcasts";
        DIRECTORY_RINGTONES = "Ringtones";
        DIRECTORY_ALARMS = "Alarms";
        DIRECTORY_NOTIFICATIONS = "Notifications";
        DIRECTORY_PICTURES = "Pictures";
        DIRECTORY_MOVIES = "Movies";
        DIRECTORY_DOWNLOADS = "Download";
        DIRECTORY_DCIM = "DCIM";
        DIRECTORY_DOCUMENTS = "Documents";
    }

    public static void initForCurrentUser() {
        sCurrentUser = new UserEnvironment(UserHandle.myUserId());
    }

    public static File getRootDirectory() {
        return DIR_ANDROID_ROOT;
    }

    public static File getOemDirectory() {
        return DIR_OEM_ROOT;
    }

    public static File getVendorDirectory() {
        return DIR_VENDOR_ROOT;
    }

    public static File getSystemSecureDirectory() {
        if (isEncryptedFilesystemEnabled()) {
            return new File(SECURE_DATA_DIRECTORY, "system");
        }
        return new File(DATA_DIRECTORY, "system");
    }

    public static File getSecureDataDirectory() {
        if (isEncryptedFilesystemEnabled()) {
            return SECURE_DATA_DIRECTORY;
        }
        return DATA_DIRECTORY;
    }

    public static File getMediaStorageDirectory() {
        throwIfUserRequired();
        return sCurrentUser.getMediaDir();
    }

    public static File getUserSystemDirectory(int userId) {
        return new File(new File(getSystemSecureDirectory(), "users"), Integer.toString(userId));
    }

    public static File getUserConfigDirectory(int userId) {
        return new File(new File(new File(getDataDirectory(), "misc"), Context.USER_SERVICE), Integer.toString(userId));
    }

    public static boolean isEncryptedFilesystemEnabled() {
        return SystemProperties.getBoolean(SYSTEM_PROPERTY_EFS_ENABLED, false);
    }

    public static File getDataDirectory() {
        return DATA_DIRECTORY;
    }

    public static File getExternalStorageDirectory() {
        throwIfUserRequired();
        return sCurrentUser.getExternalDirsForApp()[0];
    }

    public static File getLegacyExternalStorageDirectory() {
        return new File(System.getenv(ENV_EXTERNAL_STORAGE));
    }

    public static File getLegacyExternalStorageObbDirectory() {
        return buildPath(getLegacyExternalStorageDirectory(), DIR_ANDROID, DIR_OBB);
    }

    public static File getEmulatedStorageSource(int userId) {
        return new File(System.getenv(ENV_EMULATED_STORAGE_SOURCE), String.valueOf(userId));
    }

    public static File getEmulatedStorageObbSource() {
        return new File(System.getenv(ENV_EMULATED_STORAGE_SOURCE), DIR_OBB);
    }

    public static File getExternalStoragePublicDirectory(String type) {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStoragePublicDirs(type)[0];
    }

    public static File[] buildExternalStorageAndroidDataDirs() {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStorageAndroidDataDirs();
    }

    public static File[] buildExternalStorageAppDataDirs(String packageName) {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStorageAppDataDirs(packageName);
    }

    public static File[] buildExternalStorageAppMediaDirs(String packageName) {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStorageAppMediaDirs(packageName);
    }

    public static File[] buildExternalStorageAppObbDirs(String packageName) {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStorageAppObbDirs(packageName);
    }

    public static File[] buildExternalStorageAppFilesDirs(String packageName) {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStorageAppFilesDirs(packageName);
    }

    public static File[] buildExternalStorageAppCacheDirs(String packageName) {
        throwIfUserRequired();
        return sCurrentUser.buildExternalStorageAppCacheDirs(packageName);
    }

    public static File getDownloadCacheDirectory() {
        return DOWNLOAD_CACHE_DIRECTORY;
    }

    public static String getExternalStorageState() {
        return getExternalStorageState(sCurrentUser.getExternalDirsForApp()[0]);
    }

    @Deprecated
    public static String getStorageState(File path) {
        return getExternalStorageState(path);
    }

    public static String getExternalStorageState(File path) {
        StorageVolume volume = getStorageVolume(path);
        if (volume != null) {
            try {
                return Stub.asInterface(ServiceManager.getService("mount")).getVolumeState(volume.getPath());
            } catch (RemoteException e) {
            }
        }
        return MEDIA_UNKNOWN;
    }

    public static boolean isExternalStorageRemovable() {
        if (isStorageDisabled()) {
            return false;
        }
        return isExternalStorageRemovable(sCurrentUser.getExternalDirsForApp()[0]);
    }

    public static boolean isExternalStorageRemovable(File path) {
        StorageVolume volume = getStorageVolume(path);
        if (volume != null) {
            return volume.isRemovable();
        }
        throw new IllegalArgumentException("Failed to find storage device at " + path);
    }

    public static boolean isExternalStorageEmulated() {
        if (isStorageDisabled()) {
            return false;
        }
        return isExternalStorageEmulated(sCurrentUser.getExternalDirsForApp()[0]);
    }

    public static boolean isExternalStorageEmulated(File path) {
        StorageVolume volume = getStorageVolume(path);
        if (volume != null) {
            return volume.isEmulated();
        }
        throw new IllegalArgumentException("Failed to find storage device at " + path);
    }

    static File getDirectory(String variableName, String defaultPath) {
        String path = System.getenv(variableName);
        return path == null ? new File(defaultPath) : new File(path);
    }

    private static String getCanonicalPathOrNull(String variableName) {
        String str = null;
        String path = System.getenv(variableName);
        if (path != null) {
            try {
                str = new File(path).getCanonicalPath();
            } catch (IOException e) {
                Log.w(TAG, "Unable to resolve canonical path for " + path);
            }
        }
        return str;
    }

    public static void setUserRequired(boolean userRequired) {
        sUserRequired = userRequired;
    }

    private static void throwIfUserRequired() {
        if (sUserRequired) {
            Log.wtf(TAG, "Path requests must specify a user by using UserEnvironment", new Throwable());
        }
    }

    public static File[] buildPaths(File[] base, String... segments) {
        File[] result = new File[base.length];
        for (int i = 0; i < base.length; i++) {
            result[i] = buildPath(base[i], segments);
        }
        return result;
    }

    public static File buildPath(File base, String... segments) {
        File cur = base;
        String[] arr$ = segments;
        int len$ = arr$.length;
        int i$ = 0;
        File cur2 = cur;
        while (i$ < len$) {
            String segment = arr$[i$];
            if (cur2 == null) {
                cur = new File(segment);
            } else {
                cur = new File(cur2, segment);
            }
            i$++;
            cur2 = cur;
        }
        return cur2;
    }

    private static boolean isStorageDisabled() {
        return SystemProperties.getBoolean("config.disable_storage", false);
    }

    private static StorageVolume getStorageVolume(File path) {
        try {
            path = path.getCanonicalFile();
            try {
                for (StorageVolume volume : Stub.asInterface(ServiceManager.getService("mount")).getVolumeList()) {
                    if (FileUtils.contains(volume.getPathFile(), path)) {
                        return volume;
                    }
                }
            } catch (RemoteException e) {
            }
            return null;
        } catch (IOException e2) {
            return null;
        }
    }

    public static File maybeTranslateEmulatedPathToInternal(File path) {
        if (!isExternalStorageEmulated() || CANONCIAL_EMULATED_STORAGE_TARGET == null) {
            return path;
        }
        try {
            String rawPath = path.getCanonicalPath();
            if (!rawPath.startsWith(CANONCIAL_EMULATED_STORAGE_TARGET)) {
                return path;
            }
            File internalPath = new File(DIR_MEDIA_STORAGE, rawPath.substring(CANONCIAL_EMULATED_STORAGE_TARGET.length()));
            if (internalPath.exists()) {
                return internalPath;
            }
            return path;
        } catch (IOException e) {
            Log.w(TAG, "Failed to resolve canonical path for " + path);
            return path;
        }
    }
}
