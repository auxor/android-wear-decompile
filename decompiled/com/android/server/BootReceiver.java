package com.android.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.IPackageManager.Stub;
import android.os.DropBoxManager;
import android.os.FileObserver;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.Downloads;
import android.util.Slog;
import com.android.internal.util.Protocol;
import java.io.File;
import java.io.IOException;

public class BootReceiver extends BroadcastReceiver {
    private static final int LOG_SIZE;
    private static final String OLD_UPDATER_CLASS = "com.google.android.systemupdater.SystemUpdateReceiver";
    private static final String OLD_UPDATER_PACKAGE = "com.google.android.systemupdater";
    private static final String TAG = "BootReceiver";
    private static final File TOMBSTONE_DIR;
    private static FileObserver sTombstoneObserver;

    /* renamed from: com.android.server.BootReceiver.1 */
    class AnonymousClass1 extends Thread {
        final /* synthetic */ Context val$context;

        AnonymousClass1(Context context) {
            this.val$context = context;
        }

        public void run() {
            try {
                BootReceiver.this.logBootEvents(this.val$context);
            } catch (Exception e) {
                Slog.e(BootReceiver.TAG, "Can't log boot events", e);
            }
            boolean onlyCore = false;
            try {
                onlyCore = Stub.asInterface(ServiceManager.getService("package")).isOnlyCoreApps();
            } catch (RemoteException e2) {
            }
            if (!onlyCore) {
                try {
                    BootReceiver.this.removeOldUpdatePackages(this.val$context);
                } catch (Exception e3) {
                    Slog.e(BootReceiver.TAG, "Can't remove old update packages", e3);
                }
            }
        }
    }

    /* renamed from: com.android.server.BootReceiver.2 */
    class AnonymousClass2 extends FileObserver {
        final /* synthetic */ DropBoxManager val$db;
        final /* synthetic */ String val$headers;
        final /* synthetic */ SharedPreferences val$prefs;

        AnonymousClass2(String x0, int x1, DropBoxManager dropBoxManager, SharedPreferences sharedPreferences, String str) {
            this.val$db = dropBoxManager;
            this.val$prefs = sharedPreferences;
            this.val$headers = str;
            super(x0, x1);
        }

        public void onEvent(int event, String path) {
            try {
                File file = new File(BootReceiver.TOMBSTONE_DIR, path);
                if (file.isFile()) {
                    BootReceiver.addFileToDropBox(this.val$db, this.val$prefs, this.val$headers, file.getPath(), BootReceiver.LOG_SIZE, "SYSTEM_TOMBSTONE");
                }
            } catch (IOException e) {
                Slog.e(BootReceiver.TAG, "Can't log tombstone", e);
            }
        }
    }

    private static void addAuditErrorsToDropBox(android.os.DropBoxManager r1, android.content.SharedPreferences r2, java.lang.String r3, int r4, java.lang.String r5) throws java.io.IOException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.server.BootReceiver.addAuditErrorsToDropBox(android.os.DropBoxManager, android.content.SharedPreferences, java.lang.String, int, java.lang.String):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.server.BootReceiver.addAuditErrorsToDropBox(android.os.DropBoxManager, android.content.SharedPreferences, java.lang.String, int, java.lang.String):void
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BootReceiver.addAuditErrorsToDropBox(android.os.DropBoxManager, android.content.SharedPreferences, java.lang.String, int, java.lang.String):void");
    }

    private static void addFileWithFootersToDropBox(android.os.DropBoxManager r1, android.content.SharedPreferences r2, java.lang.String r3, java.lang.String r4, java.lang.String r5, int r6, java.lang.String r7) throws java.io.IOException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.server.BootReceiver.addFileWithFootersToDropBox(android.os.DropBoxManager, android.content.SharedPreferences, java.lang.String, java.lang.String, java.lang.String, int, java.lang.String):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.server.BootReceiver.addFileWithFootersToDropBox(android.os.DropBoxManager, android.content.SharedPreferences, java.lang.String, java.lang.String, java.lang.String, int, java.lang.String):void
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BootReceiver.addFileWithFootersToDropBox(android.os.DropBoxManager, android.content.SharedPreferences, java.lang.String, java.lang.String, java.lang.String, int, java.lang.String):void");
    }

    private static void addFsckErrorsToDropBox(android.os.DropBoxManager r1, android.content.SharedPreferences r2, java.lang.String r3, int r4, java.lang.String r5) throws java.io.IOException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.server.BootReceiver.addFsckErrorsToDropBox(android.os.DropBoxManager, android.content.SharedPreferences, java.lang.String, int, java.lang.String):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.server.BootReceiver.addFsckErrorsToDropBox(android.os.DropBoxManager, android.content.SharedPreferences, java.lang.String, int, java.lang.String):void
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BootReceiver.addFsckErrorsToDropBox(android.os.DropBoxManager, android.content.SharedPreferences, java.lang.String, int, java.lang.String):void");
    }

    private void logBootEvents(android.content.Context r1) throws java.io.IOException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.server.BootReceiver.logBootEvents(android.content.Context):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.server.BootReceiver.logBootEvents(android.content.Context):void
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BootReceiver.logBootEvents(android.content.Context):void");
    }

    public void onReceive(android.content.Context r1, android.content.Intent r2) {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.server.BootReceiver.onReceive(android.content.Context, android.content.Intent):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.server.BootReceiver.onReceive(android.content.Context, android.content.Intent):void
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.BootReceiver.onReceive(android.content.Context, android.content.Intent):void");
    }

    static {
        LOG_SIZE = SystemProperties.getInt("ro.debuggable", LOG_SIZE) == 1 ? Protocol.BASE_DATA_CONNECTION : Protocol.BASE_SYSTEM_RESERVED;
        TOMBSTONE_DIR = new File("/data/tombstones");
        sTombstoneObserver = null;
    }

    private void removeOldUpdatePackages(Context context) {
        Downloads.removeAllDownloadsByPackage(context, OLD_UPDATER_PACKAGE, OLD_UPDATER_CLASS);
    }

    private static void addFileToDropBox(DropBoxManager db, SharedPreferences prefs, String headers, String filename, int maxSize, String tag) throws IOException {
        addFileWithFootersToDropBox(db, prefs, headers, "", filename, maxSize, tag);
    }
}
