package android.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseConfiguration;
import android.util.Log;
import java.io.File;

public final class DefaultDatabaseErrorHandler implements DatabaseErrorHandler {
    private static final String TAG = "DefaultDatabaseErrorHandler";

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCorruption(android.database.sqlite.SQLiteDatabase r7) {
        /*
        r6 = this;
        r3 = "DefaultDatabaseErrorHandler";
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "Corruption reported by sqlite on database: ";
        r4 = r4.append(r5);
        r5 = r7.getPath();
        r4 = r4.append(r5);
        r4 = r4.toString();
        android.util.Log.e(r3, r4);
        r3 = r7.isOpen();
        if (r3 != 0) goto L_0x002a;
    L_0x0022:
        r3 = r7.getPath();
        r6.deleteDatabaseFile(r3);
    L_0x0029:
        return;
    L_0x002a:
        r0 = 0;
        r0 = r7.getAttachedDbs();	 Catch:{ SQLiteException -> 0x0078, all -> 0x0054 }
    L_0x002f:
        r7.close();	 Catch:{ SQLiteException -> 0x007a, all -> 0x0054 }
    L_0x0032:
        if (r0 == 0) goto L_0x004c;
    L_0x0034:
        r1 = r0.iterator();
    L_0x0038:
        r3 = r1.hasNext();
        if (r3 == 0) goto L_0x0029;
    L_0x003e:
        r2 = r1.next();
        r2 = (android.util.Pair) r2;
        r3 = r2.second;
        r3 = (java.lang.String) r3;
        r6.deleteDatabaseFile(r3);
        goto L_0x0038;
    L_0x004c:
        r3 = r7.getPath();
        r6.deleteDatabaseFile(r3);
        goto L_0x0029;
    L_0x0054:
        r3 = move-exception;
        r4 = r3;
        if (r0 == 0) goto L_0x0070;
    L_0x0058:
        r1 = r0.iterator();
    L_0x005c:
        r3 = r1.hasNext();
        if (r3 == 0) goto L_0x0077;
    L_0x0062:
        r2 = r1.next();
        r2 = (android.util.Pair) r2;
        r3 = r2.second;
        r3 = (java.lang.String) r3;
        r6.deleteDatabaseFile(r3);
        goto L_0x005c;
    L_0x0070:
        r3 = r7.getPath();
        r6.deleteDatabaseFile(r3);
    L_0x0077:
        throw r4;
    L_0x0078:
        r3 = move-exception;
        goto L_0x002f;
    L_0x007a:
        r3 = move-exception;
        goto L_0x0032;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.database.DefaultDatabaseErrorHandler.onCorruption(android.database.sqlite.SQLiteDatabase):void");
    }

    private void deleteDatabaseFile(String fileName) {
        if (!fileName.equalsIgnoreCase(SQLiteDatabaseConfiguration.MEMORY_DB_PATH) && fileName.trim().length() != 0) {
            Log.e(TAG, "deleting the database file: " + fileName);
            try {
                SQLiteDatabase.deleteDatabase(new File(fileName));
            } catch (Exception e) {
                Log.w(TAG, "delete failed: " + e.getMessage());
            }
        }
    }
}
