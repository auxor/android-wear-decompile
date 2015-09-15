package android.mtp;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.IContentProvider;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaScanner;
import android.net.ProxyInfo;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.GLES11;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Playlists;
import android.provider.MediaStore.Audio.PlaylistsColumns;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Files.FileColumns;
import android.provider.MediaStore.MediaColumns;
import android.provider.OpenableColumns;
import android.provider.Settings.NameValueTable;
import android.provider.VoicemailContract.Voicemails;
import android.security.KeyChain;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.Display;
import android.view.InputDevice;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ExpandableListView;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public class MtpDatabase {
    static final int[] AUDIO_PROPERTIES = null;
    private static final int DEVICE_PROPERTIES_DATABASE_VERSION = 1;
    static final int[] FILE_PROPERTIES = null;
    private static final String FORMAT_PARENT_WHERE = "format=? AND parent=?";
    private static final String[] FORMAT_PROJECTION = null;
    private static final String FORMAT_WHERE = "format=?";
    private static final String[] ID_PROJECTION = null;
    private static final String ID_WHERE = "_id=?";
    static final int[] IMAGE_PROPERTIES = null;
    private static final String[] OBJECT_INFO_PROJECTION = null;
    private static final String PARENT_WHERE = "parent=?";
    private static final String[] PATH_FORMAT_PROJECTION = null;
    private static final String[] PATH_PROJECTION = null;
    private static final String PATH_WHERE = "_data=?";
    private static final String STORAGE_FORMAT_PARENT_WHERE = "storage_id=? AND format=? AND parent=?";
    private static final String STORAGE_FORMAT_WHERE = "storage_id=? AND format=?";
    private static final String STORAGE_PARENT_WHERE = "storage_id=? AND parent=?";
    private static final String STORAGE_WHERE = "storage_id=?";
    private static final String TAG = "MtpDatabase";
    static final int[] VIDEO_PROPERTIES = null;
    private int mBatteryLevel;
    private BroadcastReceiver mBatteryReceiver;
    private int mBatteryScale;
    private final Context mContext;
    private boolean mDatabaseModified;
    private SharedPreferences mDeviceProperties;
    private final IContentProvider mMediaProvider;
    private final MediaScanner mMediaScanner;
    private final String mMediaStoragePath;
    private long mNativeContext;
    private final Uri mObjectsUri;
    private final String mPackageName;
    private final HashMap<Integer, MtpPropertyGroup> mPropertyGroupsByFormat;
    private final HashMap<Integer, MtpPropertyGroup> mPropertyGroupsByProperty;
    private MtpServer mServer;
    private final HashMap<String, MtpStorage> mStorageMap;
    private final String[] mSubDirectories;
    private String mSubDirectoriesWhere;
    private String[] mSubDirectoriesWhereArgs;
    private final String mVolumeName;

    /* renamed from: android.mtp.MtpDatabase.1 */
    class AnonymousClass1 extends BroadcastReceiver {
        final /* synthetic */ MtpDatabase this$0;

        AnonymousClass1(android.mtp.MtpDatabase r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.mtp.MtpDatabase.1.<init>(android.mtp.MtpDatabase):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.mtp.MtpDatabase.1.<init>(android.mtp.MtpDatabase):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.mtp.MtpDatabase.1.<init>(android.mtp.MtpDatabase):void");
        }

        public void onReceive(android.content.Context r1, android.content.Intent r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.mtp.MtpDatabase.1.onReceive(android.content.Context, android.content.Intent):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.mtp.MtpDatabase.1.onReceive(android.content.Context, android.content.Intent):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.mtp.MtpDatabase.1.onReceive(android.content.Context, android.content.Intent):void");
        }
    }

    private int getObjectFormat(int r12) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find block by offset: 0x0043 in list []
	at jadx.core.utils.BlockUtils.getBlockByOffset(BlockUtils.java:42)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:58)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
        /*
        r11 = this;
        r10 = -1;
        r8 = 0;
        r0 = r11.mMediaProvider;	 Catch:{ RemoteException -> 0x0036, all -> 0x0045 }
        r1 = r11.mPackageName;	 Catch:{ RemoteException -> 0x0036, all -> 0x0045 }
        r2 = r11.mObjectsUri;	 Catch:{ RemoteException -> 0x0036, all -> 0x0045 }
        r3 = FORMAT_PROJECTION;	 Catch:{ RemoteException -> 0x0036, all -> 0x0045 }
        r4 = "_id=?";	 Catch:{ RemoteException -> 0x0036, all -> 0x0045 }
        r5 = 1;	 Catch:{ RemoteException -> 0x0036, all -> 0x0045 }
        r5 = new java.lang.String[r5];	 Catch:{ RemoteException -> 0x0036, all -> 0x0045 }
        r6 = 0;	 Catch:{ RemoteException -> 0x0036, all -> 0x0045 }
        r7 = java.lang.Integer.toString(r12);	 Catch:{ RemoteException -> 0x0036, all -> 0x0045 }
        r5[r6] = r7;	 Catch:{ RemoteException -> 0x0036, all -> 0x0045 }
        r6 = 0;	 Catch:{ RemoteException -> 0x0036, all -> 0x0045 }
        r7 = 0;	 Catch:{ RemoteException -> 0x0036, all -> 0x0045 }
        r8 = r0.query(r1, r2, r3, r4, r5, r6, r7);	 Catch:{ RemoteException -> 0x0036, all -> 0x0045 }
        if (r8 == 0) goto L_0x002f;	 Catch:{ RemoteException -> 0x0036, all -> 0x0045 }
    L_0x001e:
        r0 = r8.moveToNext();	 Catch:{ RemoteException -> 0x0036, all -> 0x0045 }
        if (r0 == 0) goto L_0x002f;	 Catch:{ RemoteException -> 0x0036, all -> 0x0045 }
    L_0x0024:
        r0 = 1;	 Catch:{ RemoteException -> 0x0036, all -> 0x0045 }
        r0 = r8.getInt(r0);	 Catch:{ RemoteException -> 0x0036, all -> 0x0045 }
        if (r8 == 0) goto L_0x002e;
    L_0x002b:
        r8.close();
    L_0x002e:
        return r0;
    L_0x002f:
        if (r8 == 0) goto L_0x0034;
    L_0x0031:
        r8.close();
    L_0x0034:
        r0 = r10;
        goto L_0x002e;
    L_0x0036:
        r9 = move-exception;
        r0 = "MtpDatabase";	 Catch:{ RemoteException -> 0x0036, all -> 0x0045 }
        r1 = "RemoteException in getObjectFilePath";	 Catch:{ RemoteException -> 0x0036, all -> 0x0045 }
        android.util.Log.e(r0, r1, r9);	 Catch:{ RemoteException -> 0x0036, all -> 0x0045 }
        if (r8 == 0) goto L_0x0043;
    L_0x0040:
        r8.close();
    L_0x0043:
        r0 = r10;
        goto L_0x002e;
    L_0x0045:
        r0 = move-exception;
        if (r8 == 0) goto L_0x004b;
    L_0x0048:
        r8.close();
    L_0x004b:
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.mtp.MtpDatabase.getObjectFormat(int):int");
    }

    private final native void native_finalize();

    private final native void native_setup();

    static {
        String[] strArr = new String[DEVICE_PROPERTIES_DATABASE_VERSION];
        strArr[0] = SubscriptionManager.UNIQUE_KEY_SUBSCRIPTION_ID;
        ID_PROJECTION = strArr;
        PATH_PROJECTION = new String[]{SubscriptionManager.UNIQUE_KEY_SUBSCRIPTION_ID, Voicemails._DATA};
        FORMAT_PROJECTION = new String[]{SubscriptionManager.UNIQUE_KEY_SUBSCRIPTION_ID, FileColumns.FORMAT};
        PATH_FORMAT_PROJECTION = new String[]{SubscriptionManager.UNIQUE_KEY_SUBSCRIPTION_ID, Voicemails._DATA, FileColumns.FORMAT};
        OBJECT_INFO_PROJECTION = new String[]{SubscriptionManager.UNIQUE_KEY_SUBSCRIPTION_ID, FileColumns.STORAGE_ID, FileColumns.FORMAT, FileColumns.PARENT, Voicemails._DATA, PlaylistsColumns.DATE_ADDED, PlaylistsColumns.DATE_MODIFIED};
        System.loadLibrary("media_jni");
        FILE_PROPERTIES = new int[]{MtpConstants.PROPERTY_STORAGE_ID, MtpConstants.PROPERTY_OBJECT_FORMAT, MtpConstants.PROPERTY_PROTECTION_STATUS, MtpConstants.PROPERTY_OBJECT_SIZE, MtpConstants.PROPERTY_OBJECT_FILE_NAME, MtpConstants.PROPERTY_DATE_MODIFIED, MtpConstants.PROPERTY_PARENT_OBJECT, MtpConstants.PROPERTY_PERSISTENT_UID, MtpConstants.PROPERTY_NAME, MtpConstants.PROPERTY_DISPLAY_NAME, MtpConstants.PROPERTY_DATE_ADDED};
        AUDIO_PROPERTIES = new int[]{MtpConstants.PROPERTY_STORAGE_ID, MtpConstants.PROPERTY_OBJECT_FORMAT, MtpConstants.PROPERTY_PROTECTION_STATUS, MtpConstants.PROPERTY_OBJECT_SIZE, MtpConstants.PROPERTY_OBJECT_FILE_NAME, MtpConstants.PROPERTY_DATE_MODIFIED, MtpConstants.PROPERTY_PARENT_OBJECT, MtpConstants.PROPERTY_PERSISTENT_UID, MtpConstants.PROPERTY_NAME, MtpConstants.PROPERTY_DISPLAY_NAME, MtpConstants.PROPERTY_DATE_ADDED, MtpConstants.PROPERTY_ARTIST, MtpConstants.PROPERTY_ALBUM_NAME, MtpConstants.PROPERTY_ALBUM_ARTIST, MtpConstants.PROPERTY_TRACK, MtpConstants.PROPERTY_ORIGINAL_RELEASE_DATE, MtpConstants.PROPERTY_DURATION, MtpConstants.PROPERTY_GENRE, MtpConstants.PROPERTY_COMPOSER, MtpConstants.PROPERTY_AUDIO_WAVE_CODEC, MtpConstants.PROPERTY_BITRATE_TYPE, MtpConstants.PROPERTY_AUDIO_BITRATE, MtpConstants.PROPERTY_NUMBER_OF_CHANNELS, MtpConstants.PROPERTY_SAMPLE_RATE};
        VIDEO_PROPERTIES = new int[]{MtpConstants.PROPERTY_STORAGE_ID, MtpConstants.PROPERTY_OBJECT_FORMAT, MtpConstants.PROPERTY_PROTECTION_STATUS, MtpConstants.PROPERTY_OBJECT_SIZE, MtpConstants.PROPERTY_OBJECT_FILE_NAME, MtpConstants.PROPERTY_DATE_MODIFIED, MtpConstants.PROPERTY_PARENT_OBJECT, MtpConstants.PROPERTY_PERSISTENT_UID, MtpConstants.PROPERTY_NAME, MtpConstants.PROPERTY_DISPLAY_NAME, MtpConstants.PROPERTY_DATE_ADDED, MtpConstants.PROPERTY_ARTIST, MtpConstants.PROPERTY_ALBUM_NAME, MtpConstants.PROPERTY_DURATION, MtpConstants.PROPERTY_DESCRIPTION};
        IMAGE_PROPERTIES = new int[]{MtpConstants.PROPERTY_STORAGE_ID, MtpConstants.PROPERTY_OBJECT_FORMAT, MtpConstants.PROPERTY_PROTECTION_STATUS, MtpConstants.PROPERTY_OBJECT_SIZE, MtpConstants.PROPERTY_OBJECT_FILE_NAME, MtpConstants.PROPERTY_DATE_MODIFIED, MtpConstants.PROPERTY_PARENT_OBJECT, MtpConstants.PROPERTY_PERSISTENT_UID, MtpConstants.PROPERTY_NAME, MtpConstants.PROPERTY_DISPLAY_NAME, MtpConstants.PROPERTY_DATE_ADDED, MtpConstants.PROPERTY_DESCRIPTION};
    }

    public MtpDatabase(Context context, String volumeName, String storagePath, String[] subDirectories) {
        this.mStorageMap = new HashMap();
        this.mPropertyGroupsByProperty = new HashMap();
        this.mPropertyGroupsByFormat = new HashMap();
        this.mBatteryReceiver = new AnonymousClass1(this);
        native_setup();
        this.mContext = context;
        this.mPackageName = context.getPackageName();
        this.mMediaProvider = context.getContentResolver().acquireProvider(MediaStore.AUTHORITY);
        this.mVolumeName = volumeName;
        this.mMediaStoragePath = storagePath;
        this.mObjectsUri = Files.getMtpObjectsUri(volumeName);
        this.mMediaScanner = new MediaScanner(context);
        this.mSubDirectories = subDirectories;
        if (subDirectories != null) {
            int i;
            StringBuilder builder = new StringBuilder();
            builder.append("(");
            int count = subDirectories.length;
            for (i = 0; i < count; i += DEVICE_PROPERTIES_DATABASE_VERSION) {
                builder.append("_data=? OR _data LIKE ?");
                if (i != count - 1) {
                    builder.append(" OR ");
                }
            }
            builder.append(")");
            this.mSubDirectoriesWhere = builder.toString();
            this.mSubDirectoriesWhereArgs = new String[(count * 2)];
            int j = 0;
            for (i = 0; i < count; i += DEVICE_PROPERTIES_DATABASE_VERSION) {
                String path = subDirectories[i];
                int i2 = j + DEVICE_PROPERTIES_DATABASE_VERSION;
                this.mSubDirectoriesWhereArgs[j] = path;
                j = i2 + DEVICE_PROPERTIES_DATABASE_VERSION;
                this.mSubDirectoriesWhereArgs[i2] = path + "/%";
            }
        }
        Locale locale = context.getResources().getConfiguration().locale;
        if (locale != null) {
            String language = locale.getLanguage();
            String country = locale.getCountry();
            if (language != null) {
                if (country != null) {
                    this.mMediaScanner.setLocale(language + "_" + country);
                } else {
                    this.mMediaScanner.setLocale(language);
                }
            }
        }
        initDeviceProperties(context);
    }

    public void setServer(MtpServer server) {
        this.mServer = server;
        try {
            this.mContext.unregisterReceiver(this.mBatteryReceiver);
        } catch (IllegalArgumentException e) {
        }
        if (server != null) {
            this.mContext.registerReceiver(this.mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }
    }

    protected void finalize() throws Throwable {
        try {
            native_finalize();
        } finally {
            super.finalize();
        }
    }

    public void addStorage(MtpStorage storage) {
        this.mStorageMap.put(storage.getPath(), storage);
    }

    public void removeStorage(MtpStorage storage) {
        this.mStorageMap.remove(storage.getPath());
    }

    private void initDeviceProperties(Context context) {
        String devicePropertiesName = "device-properties";
        this.mDeviceProperties = context.getSharedPreferences("device-properties", 0);
        if (context.getDatabasePath("device-properties").exists()) {
            SQLiteDatabase db = null;
            Cursor c = null;
            Editor e;
            try {
                db = context.openOrCreateDatabase("device-properties", 0, null);
                if (db != null) {
                    c = db.query("properties", new String[]{SubscriptionManager.UNIQUE_KEY_SUBSCRIPTION_ID, "code", NameValueTable.VALUE}, null, null, null, null, null);
                    if (c != null) {
                        e = this.mDeviceProperties.edit();
                        while (c.moveToNext()) {
                            e.putString(c.getString(DEVICE_PROPERTIES_DATABASE_VERSION), c.getString(2));
                        }
                        e.commit();
                    }
                }
                if (c != null) {
                    c.close();
                }
                if (db != null) {
                    db.close();
                }
            } catch (Editor e2) {
                Log.e(TAG, "failed to migrate device properties", e2);
                context.deleteDatabase("device-properties");
            } finally {
                if (c != null) {
                    c.close();
                }
                if (db != null) {
                    db.close();
                }
            }
            context.deleteDatabase("device-properties");
        }
    }

    private boolean inStorageSubDirectory(String path) {
        if (this.mSubDirectories == null) {
            return true;
        }
        if (path == null) {
            return false;
        }
        boolean allowed = false;
        int pathLength = path.length();
        for (int i = 0; i < this.mSubDirectories.length && !allowed; i += DEVICE_PROPERTIES_DATABASE_VERSION) {
            String subdir = this.mSubDirectories[i];
            int subdirLength = subdir.length();
            if (subdirLength < pathLength && path.charAt(subdirLength) == '/' && path.startsWith(subdir)) {
                allowed = true;
            }
        }
        return allowed;
    }

    private boolean isStorageSubDirectory(String path) {
        if (this.mSubDirectories == null) {
            return false;
        }
        for (int i = 0; i < this.mSubDirectories.length; i += DEVICE_PROPERTIES_DATABASE_VERSION) {
            if (path.equals(this.mSubDirectories[i])) {
                return true;
            }
        }
        return false;
    }

    private boolean inStorageRoot(String path) {
        try {
            String canonical = new File(path).getCanonicalPath();
            for (String root : this.mStorageMap.keySet()) {
                if (canonical.startsWith(root)) {
                    return true;
                }
            }
        } catch (IOException e) {
        }
        return false;
    }

    private int beginSendObject(String path, int format, int parent, int storageId, long size, long modified) {
        if (!inStorageRoot(path)) {
            Log.e(TAG, "attempt to put file outside of storage area: " + path);
            return -1;
        } else if (!inStorageSubDirectory(path)) {
            return -1;
        } else {
            if (path != null) {
                Cursor c = null;
                try {
                    IContentProvider iContentProvider = this.mMediaProvider;
                    String str = this.mPackageName;
                    Uri uri = this.mObjectsUri;
                    String[] strArr = ID_PROJECTION;
                    String str2 = PATH_WHERE;
                    String[] strArr2 = new String[DEVICE_PROPERTIES_DATABASE_VERSION];
                    strArr2[0] = path;
                    c = iContentProvider.query(str, uri, strArr, str2, strArr2, null, null);
                    if (c != null && c.getCount() > 0) {
                        Log.w(TAG, "file already exists in beginSendObject: " + path);
                        if (c == null) {
                            return -1;
                        }
                        c.close();
                        return -1;
                    } else if (c != null) {
                        c.close();
                    }
                } catch (RemoteException e) {
                    Log.e(TAG, "RemoteException in beginSendObject", e);
                    if (c != null) {
                        c.close();
                    }
                } catch (Throwable th) {
                    if (c != null) {
                        c.close();
                    }
                }
            }
            this.mDatabaseModified = true;
            ContentValues values = new ContentValues();
            values.put(Voicemails._DATA, path);
            values.put(FileColumns.FORMAT, Integer.valueOf(format));
            values.put(FileColumns.PARENT, Integer.valueOf(parent));
            values.put(FileColumns.STORAGE_ID, Integer.valueOf(storageId));
            values.put(OpenableColumns.SIZE, Long.valueOf(size));
            values.put(PlaylistsColumns.DATE_MODIFIED, Long.valueOf(modified));
            try {
                Uri uri2 = this.mMediaProvider.insert(this.mPackageName, this.mObjectsUri, values);
                if (uri2 != null) {
                    return Integer.parseInt((String) uri2.getPathSegments().get(2));
                }
                return -1;
            } catch (RemoteException e2) {
                Log.e(TAG, "RemoteException in beginSendObject", e2);
                return -1;
            }
        }
    }

    private void endSendObject(String path, int handle, int format, boolean succeeded) {
        if (!succeeded) {
            deleteFile(handle);
        } else if (format == MtpConstants.FORMAT_ABSTRACT_AV_PLAYLIST) {
            String name = path;
            int lastSlash = name.lastIndexOf(47);
            if (lastSlash >= 0) {
                name = name.substring(lastSlash + DEVICE_PROPERTIES_DATABASE_VERSION);
            }
            if (name.endsWith(".pla")) {
                name = name.substring(0, name.length() - 4);
            }
            ContentValues values = new ContentValues((int) DEVICE_PROPERTIES_DATABASE_VERSION);
            values.put(Voicemails._DATA, path);
            values.put(KeyChain.EXTRA_NAME, name);
            values.put(FileColumns.FORMAT, Integer.valueOf(format));
            values.put(PlaylistsColumns.DATE_MODIFIED, Long.valueOf(System.currentTimeMillis() / 1000));
            values.put(MediaColumns.MEDIA_SCANNER_NEW_OBJECT_ID, Integer.valueOf(handle));
            try {
                this.mMediaProvider.insert(this.mPackageName, Playlists.EXTERNAL_CONTENT_URI, values);
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException in endSendObject", e);
            }
        } else {
            this.mMediaScanner.scanMtpFile(path, this.mVolumeName, handle, format);
        }
    }

    private Cursor createObjectQuery(int storageID, int format, int parent) throws RemoteException {
        String where;
        String[] whereArgs;
        if (storageID == -1) {
            if (format == 0) {
                if (parent == 0) {
                    where = null;
                    whereArgs = null;
                } else {
                    if (parent == -1) {
                        parent = 0;
                    }
                    where = PARENT_WHERE;
                    whereArgs = new String[DEVICE_PROPERTIES_DATABASE_VERSION];
                    whereArgs[0] = Integer.toString(parent);
                }
            } else if (parent == 0) {
                where = FORMAT_WHERE;
                whereArgs = new String[DEVICE_PROPERTIES_DATABASE_VERSION];
                whereArgs[0] = Integer.toString(format);
            } else {
                if (parent == -1) {
                    parent = 0;
                }
                where = FORMAT_PARENT_WHERE;
                whereArgs = new String[]{Integer.toString(format), Integer.toString(parent)};
            }
        } else if (format == 0) {
            if (parent == 0) {
                where = STORAGE_WHERE;
                whereArgs = new String[DEVICE_PROPERTIES_DATABASE_VERSION];
                whereArgs[0] = Integer.toString(storageID);
            } else {
                if (parent == -1) {
                    parent = 0;
                }
                where = STORAGE_PARENT_WHERE;
                whereArgs = new String[]{Integer.toString(storageID), Integer.toString(parent)};
            }
        } else if (parent == 0) {
            where = STORAGE_FORMAT_WHERE;
            whereArgs = new String[]{Integer.toString(storageID), Integer.toString(format)};
        } else {
            if (parent == -1) {
                parent = 0;
            }
            where = STORAGE_FORMAT_PARENT_WHERE;
            whereArgs = new String[]{Integer.toString(storageID), Integer.toString(format), Integer.toString(parent)};
        }
        if (this.mSubDirectoriesWhere != null) {
            if (where == null) {
                where = this.mSubDirectoriesWhere;
                whereArgs = this.mSubDirectoriesWhereArgs;
            } else {
                where = where + " AND " + this.mSubDirectoriesWhere;
                String[] newWhereArgs = new String[(whereArgs.length + this.mSubDirectoriesWhereArgs.length)];
                int i = 0;
                while (i < whereArgs.length) {
                    newWhereArgs[i] = whereArgs[i];
                    i += DEVICE_PROPERTIES_DATABASE_VERSION;
                }
                for (int j = 0; j < this.mSubDirectoriesWhereArgs.length; j += DEVICE_PROPERTIES_DATABASE_VERSION) {
                    newWhereArgs[i] = this.mSubDirectoriesWhereArgs[j];
                    i += DEVICE_PROPERTIES_DATABASE_VERSION;
                }
                whereArgs = newWhereArgs;
            }
        }
        return this.mMediaProvider.query(this.mPackageName, this.mObjectsUri, ID_PROJECTION, where, whereArgs, null, null);
    }

    private int[] getObjectList(int storageID, int format, int parent) {
        Cursor c = null;
        try {
            c = createObjectQuery(storageID, format, parent);
            if (c == null) {
                if (c != null) {
                    c.close();
                }
                return null;
            }
            int count = c.getCount();
            if (count > 0) {
                int[] result = new int[count];
                for (int i = 0; i < count; i += DEVICE_PROPERTIES_DATABASE_VERSION) {
                    c.moveToNext();
                    result[i] = c.getInt(0);
                }
                if (c == null) {
                    return result;
                }
                c.close();
                return result;
            }
            if (c != null) {
                c.close();
            }
            return null;
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in getObjectList", e);
            if (c != null) {
                c.close();
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
        }
    }

    private int getNumObjects(int storageID, int format, int parent) {
        Cursor c = null;
        try {
            c = createObjectQuery(storageID, format, parent);
            if (c != null) {
                int count = c.getCount();
                if (c == null) {
                    return count;
                }
                c.close();
                return count;
            }
            if (c != null) {
                c.close();
            }
            return -1;
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in getNumObjects", e);
            if (c != null) {
                c.close();
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
        }
    }

    private int[] getSupportedPlaybackFormats() {
        return new int[]{GLES11.GL_CLIP_PLANE0, GLES11.GL_CLIP_PLANE1, GLES11.GL_CLIP_PLANE4, GLES11.GL_CLIP_PLANE5, EGL14.EGL_BAD_DISPLAY, EGL14.EGL_BAD_MATCH, EGL14.EGL_BAD_NATIVE_WINDOW, MtpConstants.FORMAT_EXIF_JPEG, MtpConstants.FORMAT_TIFF_EP, MtpConstants.FORMAT_BMP, MtpConstants.FORMAT_GIF, MtpConstants.FORMAT_JFIF, MtpConstants.FORMAT_PNG, MtpConstants.FORMAT_TIFF, MtpConstants.FORMAT_WMA, MtpConstants.FORMAT_OGG, MtpConstants.FORMAT_AAC, MtpConstants.FORMAT_MP4_CONTAINER, MtpConstants.FORMAT_MP2, MtpConstants.FORMAT_3GP_CONTAINER, MtpConstants.FORMAT_ABSTRACT_AV_PLAYLIST, MtpConstants.FORMAT_WPL_PLAYLIST, MtpConstants.FORMAT_M3U_PLAYLIST, MtpConstants.FORMAT_PLS_PLAYLIST, MtpConstants.FORMAT_XML_DOCUMENT, MtpConstants.FORMAT_FLAC};
    }

    private int[] getSupportedCaptureFormats() {
        return null;
    }

    private int[] getSupportedObjectProperties(int format) {
        switch (format) {
            case EGL14.EGL_BAD_DISPLAY /*12296*/:
            case EGL14.EGL_BAD_MATCH /*12297*/:
            case MtpConstants.FORMAT_WMA /*47361*/:
            case MtpConstants.FORMAT_OGG /*47362*/:
            case MtpConstants.FORMAT_AAC /*47363*/:
                return AUDIO_PROPERTIES;
            case EGL14.EGL_BAD_NATIVE_WINDOW /*12299*/:
            case MtpConstants.FORMAT_WMV /*47489*/:
            case MtpConstants.FORMAT_3GP_CONTAINER /*47492*/:
                return VIDEO_PROPERTIES;
            case MtpConstants.FORMAT_EXIF_JPEG /*14337*/:
            case MtpConstants.FORMAT_BMP /*14340*/:
            case MtpConstants.FORMAT_GIF /*14343*/:
            case MtpConstants.FORMAT_PNG /*14347*/:
                return IMAGE_PROPERTIES;
            default:
                return FILE_PROPERTIES;
        }
    }

    private int[] getSupportedDeviceProperties() {
        return new int[]{MtpConstants.DEVICE_PROPERTY_SYNCHRONIZATION_PARTNER, MtpConstants.DEVICE_PROPERTY_DEVICE_FRIENDLY_NAME, MtpConstants.DEVICE_PROPERTY_IMAGE_SIZE, MtpConstants.DEVICE_PROPERTY_BATTERY_LEVEL};
    }

    private MtpPropertyList getObjectPropertyList(long handle, int format, long property, int groupCode, int depth) {
        if (groupCode != 0) {
            return new MtpPropertyList(0, MtpConstants.RESPONSE_SPECIFICATION_BY_GROUP_UNSUPPORTED);
        }
        MtpPropertyGroup propertyGroup;
        if (property == ExpandableListView.PACKED_POSITION_VALUE_NULL) {
            if (format == 0 && handle > 0) {
                format = getObjectFormat((int) handle);
            }
            propertyGroup = (MtpPropertyGroup) this.mPropertyGroupsByFormat.get(Integer.valueOf(format));
            if (propertyGroup == null) {
                propertyGroup = new MtpPropertyGroup(this, this.mMediaProvider, this.mPackageName, this.mVolumeName, getSupportedObjectProperties(format));
                this.mPropertyGroupsByFormat.put(new Integer(format), propertyGroup);
            }
        } else {
            propertyGroup = (MtpPropertyGroup) this.mPropertyGroupsByProperty.get(Long.valueOf(property));
            if (propertyGroup == null) {
                int[] propertyList = new int[DEVICE_PROPERTIES_DATABASE_VERSION];
                propertyList[0] = (int) property;
                propertyGroup = new MtpPropertyGroup(this, this.mMediaProvider, this.mPackageName, this.mVolumeName, propertyList);
                this.mPropertyGroupsByProperty.put(new Integer((int) property), propertyGroup);
            }
        }
        return propertyGroup.getPropertyList((int) handle, format, depth);
    }

    private int renameFile(int handle, String newName) {
        Cursor c = null;
        String path = null;
        String[] whereArgs = new String[DEVICE_PROPERTIES_DATABASE_VERSION];
        whereArgs[0] = Integer.toString(handle);
        try {
            c = this.mMediaProvider.query(this.mPackageName, this.mObjectsUri, PATH_PROJECTION, ID_WHERE, whereArgs, null, null);
            if (c != null && c.moveToNext()) {
                path = c.getString(DEVICE_PROPERTIES_DATABASE_VERSION);
            }
            if (c != null) {
                c.close();
            }
            if (path == null) {
                return MtpConstants.RESPONSE_INVALID_OBJECT_HANDLE;
            }
            if (isStorageSubDirectory(path)) {
                return MtpConstants.RESPONSE_OBJECT_WRITE_PROTECTED;
            }
            File oldFile = new File(path);
            int lastSlash = path.lastIndexOf(47);
            if (lastSlash <= DEVICE_PROPERTIES_DATABASE_VERSION) {
                return InputDevice.SOURCE_MOUSE;
            }
            String newPath = path.substring(0, lastSlash + DEVICE_PROPERTIES_DATABASE_VERSION) + newName;
            File newFile = new File(newPath);
            if (oldFile.renameTo(newFile)) {
                ContentValues values = new ContentValues();
                values.put(Voicemails._DATA, newPath);
                int updated = 0;
                try {
                    updated = this.mMediaProvider.update(this.mPackageName, this.mObjectsUri, values, ID_WHERE, whereArgs);
                } catch (RemoteException e) {
                    Log.e(TAG, "RemoteException in mMediaProvider.update", e);
                }
                if (updated == 0) {
                    Log.e(TAG, "Unable to update path for " + path + " to " + newPath);
                    newFile.renameTo(oldFile);
                    return InputDevice.SOURCE_MOUSE;
                }
                if (newFile.isDirectory()) {
                    if (oldFile.getName().startsWith(".") && !newPath.startsWith(".")) {
                        try {
                            this.mMediaProvider.call(this.mPackageName, MediaStore.UNHIDE_CALL, newPath, null);
                        } catch (RemoteException e2) {
                            Log.e(TAG, "failed to unhide/rescan for " + newPath);
                        }
                    }
                } else if (oldFile.getName().toLowerCase(Locale.US).equals(MediaStore.MEDIA_IGNORE_FILENAME) && !newPath.toLowerCase(Locale.US).equals(MediaStore.MEDIA_IGNORE_FILENAME)) {
                    try {
                        this.mMediaProvider.call(this.mPackageName, MediaStore.UNHIDE_CALL, oldFile.getParent(), null);
                    } catch (RemoteException e3) {
                        Log.e(TAG, "failed to unhide/rescan for " + newPath);
                    }
                }
                return MtpConstants.RESPONSE_OK;
            }
            Log.w(TAG, "renaming " + path + " to " + newPath + " failed");
            return InputDevice.SOURCE_MOUSE;
        } catch (RemoteException e4) {
            Log.e(TAG, "RemoteException in getObjectFilePath", e4);
            if (c == null) {
                return InputDevice.SOURCE_MOUSE;
            }
            c.close();
            return InputDevice.SOURCE_MOUSE;
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
        }
    }

    private int setObjectProperty(int handle, int property, long intValue, String stringValue) {
        switch (property) {
            case MtpConstants.PROPERTY_OBJECT_FILE_NAME /*56327*/:
                return renameFile(handle, stringValue);
            default:
                return MtpConstants.RESPONSE_OBJECT_PROP_NOT_SUPPORTED;
        }
    }

    private int getDeviceProperty(int property, long[] outIntValue, char[] outStringValue) {
        switch (property) {
            case MtpConstants.DEVICE_PROPERTY_IMAGE_SIZE /*20483*/:
                Display display = ((WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                String imageSize = Integer.toString(display.getMaximumSizeDimension()) + "x" + Integer.toString(display.getMaximumSizeDimension());
                imageSize.getChars(0, imageSize.length(), outStringValue, 0);
                outStringValue[imageSize.length()] = '\u0000';
                return MtpConstants.RESPONSE_OK;
            case MtpConstants.DEVICE_PROPERTY_SYNCHRONIZATION_PARTNER /*54273*/:
            case MtpConstants.DEVICE_PROPERTY_DEVICE_FRIENDLY_NAME /*54274*/:
                String value = this.mDeviceProperties.getString(Integer.toString(property), ProxyInfo.LOCAL_EXCL_LIST);
                int length = value.length();
                if (length > EditorInfo.IME_MASK_ACTION) {
                    length = EditorInfo.IME_MASK_ACTION;
                }
                value.getChars(0, length, outStringValue, 0);
                outStringValue[length] = '\u0000';
                return MtpConstants.RESPONSE_OK;
            default:
                return MtpConstants.RESPONSE_DEVICE_PROP_NOT_SUPPORTED;
        }
    }

    private int setDeviceProperty(int property, long intValue, String stringValue) {
        switch (property) {
            case MtpConstants.DEVICE_PROPERTY_SYNCHRONIZATION_PARTNER /*54273*/:
            case MtpConstants.DEVICE_PROPERTY_DEVICE_FRIENDLY_NAME /*54274*/:
                Editor e = this.mDeviceProperties.edit();
                e.putString(Integer.toString(property), stringValue);
                return e.commit() ? MtpConstants.RESPONSE_OK : InputDevice.SOURCE_MOUSE;
            default:
                return MtpConstants.RESPONSE_DEVICE_PROP_NOT_SUPPORTED;
        }
    }

    private boolean getObjectInfo(int handle, int[] outStorageFormatParent, char[] outName, long[] outCreatedModified) {
        Cursor c = null;
        try {
            IContentProvider iContentProvider = this.mMediaProvider;
            String str = this.mPackageName;
            Uri uri = this.mObjectsUri;
            String[] strArr = OBJECT_INFO_PROJECTION;
            String str2 = ID_WHERE;
            String[] strArr2 = new String[DEVICE_PROPERTIES_DATABASE_VERSION];
            strArr2[0] = Integer.toString(handle);
            c = iContentProvider.query(str, uri, strArr, str2, strArr2, null, null);
            if (c == null || !c.moveToNext()) {
                if (c != null) {
                    c.close();
                }
                return false;
            }
            outStorageFormatParent[0] = c.getInt(DEVICE_PROPERTIES_DATABASE_VERSION);
            outStorageFormatParent[DEVICE_PROPERTIES_DATABASE_VERSION] = c.getInt(2);
            outStorageFormatParent[2] = c.getInt(3);
            String path = c.getString(4);
            int lastSlash = path.lastIndexOf(47);
            int start = lastSlash >= 0 ? lastSlash + DEVICE_PROPERTIES_DATABASE_VERSION : 0;
            int end = path.length();
            if (end - start > EditorInfo.IME_MASK_ACTION) {
                end = start + EditorInfo.IME_MASK_ACTION;
            }
            path.getChars(start, end, outName, 0);
            outName[end - start] = '\u0000';
            outCreatedModified[0] = c.getLong(5);
            outCreatedModified[DEVICE_PROPERTIES_DATABASE_VERSION] = c.getLong(6);
            if (outCreatedModified[0] == 0) {
                outCreatedModified[0] = outCreatedModified[DEVICE_PROPERTIES_DATABASE_VERSION];
            }
            if (c == null) {
                return true;
            }
            c.close();
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in getObjectInfo", e);
            if (c != null) {
                c.close();
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
        }
    }

    private int getObjectFilePath(int handle, char[] outFilePath, long[] outFileLengthFormat) {
        if (handle == 0) {
            this.mMediaStoragePath.getChars(0, this.mMediaStoragePath.length(), outFilePath, 0);
            outFilePath[this.mMediaStoragePath.length()] = '\u0000';
            outFileLengthFormat[0] = 0;
            outFileLengthFormat[DEVICE_PROPERTIES_DATABASE_VERSION] = 12289;
            return MtpConstants.RESPONSE_OK;
        }
        Cursor c = null;
        try {
            IContentProvider iContentProvider = this.mMediaProvider;
            String str = this.mPackageName;
            Uri uri = this.mObjectsUri;
            String[] strArr = PATH_FORMAT_PROJECTION;
            String str2 = ID_WHERE;
            String[] strArr2 = new String[DEVICE_PROPERTIES_DATABASE_VERSION];
            strArr2[0] = Integer.toString(handle);
            c = iContentProvider.query(str, uri, strArr, str2, strArr2, null, null);
            if (c != null && c.moveToNext()) {
                String path = c.getString(DEVICE_PROPERTIES_DATABASE_VERSION);
                path.getChars(0, path.length(), outFilePath, 0);
                outFilePath[path.length()] = '\u0000';
                outFileLengthFormat[0] = new File(path).length();
                outFileLengthFormat[DEVICE_PROPERTIES_DATABASE_VERSION] = c.getLong(2);
                if (c != null) {
                    c.close();
                }
                return MtpConstants.RESPONSE_OK;
            } else if (c == null) {
                return MtpConstants.RESPONSE_INVALID_OBJECT_HANDLE;
            } else {
                c.close();
                return MtpConstants.RESPONSE_INVALID_OBJECT_HANDLE;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in getObjectFilePath", e);
            if (c == null) {
                return InputDevice.SOURCE_MOUSE;
            }
            c.close();
            return InputDevice.SOURCE_MOUSE;
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
        }
    }

    private int deleteFile(int handle) {
        this.mDatabaseModified = true;
        Cursor c = null;
        int i;
        try {
            IContentProvider iContentProvider = this.mMediaProvider;
            String str = this.mPackageName;
            Uri uri = this.mObjectsUri;
            String[] strArr = PATH_FORMAT_PROJECTION;
            String str2 = ID_WHERE;
            String[] strArr2 = new String[DEVICE_PROPERTIES_DATABASE_VERSION];
            strArr2[0] = Integer.toString(handle);
            c = iContentProvider.query(str, uri, strArr, str2, strArr2, null, null);
            if (c == null || !c.moveToNext()) {
                i = MtpConstants.RESPONSE_INVALID_OBJECT_HANDLE;
                if (c != null) {
                    c.close();
                }
                return i;
            }
            String path = c.getString(DEVICE_PROPERTIES_DATABASE_VERSION);
            int format = c.getInt(2);
            if (path == null || format == 0) {
                i = InputDevice.SOURCE_MOUSE;
                if (c != null) {
                    c.close();
                }
                return i;
            }
            if (isStorageSubDirectory(path)) {
                i = MtpConstants.RESPONSE_OBJECT_WRITE_PROTECTED;
                if (c != null) {
                    c.close();
                }
            } else {
                if (format == GLES11.GL_CLIP_PLANE1) {
                    this.mMediaProvider.delete(this.mPackageName, Files.getMtpObjectsUri(this.mVolumeName), "_data LIKE ?1 AND lower(substr(_data,1,?2))=lower(?3)", new String[]{path + "/%", Integer.toString(path.length() + DEVICE_PROPERTIES_DATABASE_VERSION), path + "/"});
                }
                if (this.mMediaProvider.delete(this.mPackageName, Files.getMtpObjectsUri(this.mVolumeName, (long) handle), null, null) > 0) {
                    if (format != GLES11.GL_CLIP_PLANE1 && path.toLowerCase(Locale.US).endsWith("/.nomedia")) {
                        try {
                            this.mMediaProvider.call(this.mPackageName, MediaStore.UNHIDE_CALL, path.substring(0, path.lastIndexOf("/")), null);
                        } catch (RemoteException e) {
                            Log.e(TAG, "failed to unhide/rescan for " + path);
                        }
                    }
                    i = MtpConstants.RESPONSE_OK;
                    if (c != null) {
                        c.close();
                    }
                } else {
                    i = MtpConstants.RESPONSE_INVALID_OBJECT_HANDLE;
                    if (c != null) {
                        c.close();
                    }
                }
            }
            return i;
        } catch (RemoteException e2) {
            Log.e(TAG, "RemoteException in deleteFile", e2);
            i = InputDevice.SOURCE_MOUSE;
            if (c != null) {
                c.close();
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
        }
    }

    private int[] getObjectReferences(int handle) {
        Cursor c = null;
        try {
            c = this.mMediaProvider.query(this.mPackageName, Files.getMtpReferencesUri(this.mVolumeName, (long) handle), ID_PROJECTION, null, null, null, null);
            if (c == null) {
                if (c != null) {
                    c.close();
                }
                return null;
            }
            int count = c.getCount();
            if (count > 0) {
                int[] result = new int[count];
                for (int i = 0; i < count; i += DEVICE_PROPERTIES_DATABASE_VERSION) {
                    c.moveToNext();
                    result[i] = c.getInt(0);
                }
                if (c == null) {
                    return result;
                }
                c.close();
                return result;
            }
            if (c != null) {
                c.close();
            }
            return null;
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in getObjectList", e);
            if (c != null) {
                c.close();
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
        }
    }

    private int setObjectReferences(int handle, int[] references) {
        this.mDatabaseModified = true;
        Uri uri = Files.getMtpReferencesUri(this.mVolumeName, (long) handle);
        int count = references.length;
        ContentValues[] valuesList = new ContentValues[count];
        for (int i = 0; i < count; i += DEVICE_PROPERTIES_DATABASE_VERSION) {
            ContentValues values = new ContentValues();
            values.put(SubscriptionManager.UNIQUE_KEY_SUBSCRIPTION_ID, Integer.valueOf(references[i]));
            valuesList[i] = values;
        }
        try {
            if (this.mMediaProvider.bulkInsert(this.mPackageName, uri, valuesList) > 0) {
                return MtpConstants.RESPONSE_OK;
            }
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in setObjectReferences", e);
        }
        return InputDevice.SOURCE_MOUSE;
    }

    private void sessionStarted() {
        this.mDatabaseModified = false;
    }

    private void sessionEnded() {
        if (this.mDatabaseModified) {
            this.mContext.sendBroadcast(new Intent(MediaStore.ACTION_MTP_SESSION_END));
            this.mDatabaseModified = false;
        }
    }
}
