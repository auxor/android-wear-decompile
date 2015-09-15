package android.app.backup;

import android.content.Context;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import java.io.File;

public class WallpaperBackupHelper extends FileBackupHelperBase implements BackupHelper {
    private static final boolean DEBUG = false;
    private static final double MAX_HEIGHT_RATIO = 1.35d;
    private static final double MIN_HEIGHT_RATIO = 0.0d;
    private static final boolean REJECT_OUTSIZED_RESTORE = true;
    private static final String STAGE_FILE;
    private static final String TAG = "WallpaperBackupHelper";
    public static final String WALLPAPER_IMAGE;
    public static final String WALLPAPER_IMAGE_KEY = "/data/data/com.android.settings/files/wallpaper";
    public static final String WALLPAPER_INFO;
    public static final String WALLPAPER_INFO_KEY = "/data/system/wallpaper_info.xml";
    Context mContext;
    double mDesiredMinHeight;
    double mDesiredMinWidth;
    String[] mFiles;
    String[] mKeys;

    public WallpaperBackupHelper(android.content.Context r1, java.lang.String[] r2, java.lang.String[] r3) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.backup.WallpaperBackupHelper.<init>(android.content.Context, java.lang.String[], java.lang.String[]):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.backup.WallpaperBackupHelper.<init>(android.content.Context, java.lang.String[], java.lang.String[]):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: android.app.backup.WallpaperBackupHelper.<init>(android.content.Context, java.lang.String[], java.lang.String[]):void");
    }

    public void performBackup(android.os.ParcelFileDescriptor r1, android.app.backup.BackupDataOutput r2, android.os.ParcelFileDescriptor r3) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.backup.WallpaperBackupHelper.performBackup(android.os.ParcelFileDescriptor, android.app.backup.BackupDataOutput, android.os.ParcelFileDescriptor):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.backup.WallpaperBackupHelper.performBackup(android.os.ParcelFileDescriptor, android.app.backup.BackupDataOutput, android.os.ParcelFileDescriptor):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: android.app.backup.WallpaperBackupHelper.performBackup(android.os.ParcelFileDescriptor, android.app.backup.BackupDataOutput, android.os.ParcelFileDescriptor):void");
    }

    public void restoreEntity(android.app.backup.BackupDataInputStream r1) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.backup.WallpaperBackupHelper.restoreEntity(android.app.backup.BackupDataInputStream):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.backup.WallpaperBackupHelper.restoreEntity(android.app.backup.BackupDataInputStream):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: android.app.backup.WallpaperBackupHelper.restoreEntity(android.app.backup.BackupDataInputStream):void");
    }

    public /* bridge */ /* synthetic */ void writeNewStateDescription(ParcelFileDescriptor x0) {
        super.writeNewStateDescription(x0);
    }

    static {
        WALLPAPER_IMAGE = new File(Environment.getUserSystemDirectory(0), Context.WALLPAPER_SERVICE).getAbsolutePath();
        WALLPAPER_INFO = new File(Environment.getUserSystemDirectory(0), "wallpaper_info.xml").getAbsolutePath();
        STAGE_FILE = new File(Environment.getUserSystemDirectory(0), "wallpaper-tmp").getAbsolutePath();
    }
}
