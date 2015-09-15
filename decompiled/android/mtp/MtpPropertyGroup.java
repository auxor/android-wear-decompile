package android.mtp;

import android.content.IContentProvider;
import android.database.Cursor;
import android.net.ProxyInfo;
import android.net.Uri;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.Audio.Genres;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.Audio.PlaylistsColumns;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Files.FileColumns;
import android.provider.MediaStore.Video.VideoColumns;
import android.provider.OpenableColumns;
import android.provider.Settings.Bookmarks;
import android.provider.VoicemailContract.Voicemails;
import android.security.KeyChain;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.AppSecurityPermissions;
import java.util.ArrayList;

class MtpPropertyGroup {
    private static final String FORMAT_WHERE = "format=?";
    private static final String ID_FORMAT_WHERE = "_id=? AND format=?";
    private static final String ID_WHERE = "_id=?";
    private static final String PARENT_FORMAT_WHERE = "parent=? AND format=?";
    private static final String PARENT_WHERE = "parent=?";
    private static final String TAG = "MtpPropertyGroup";
    private String[] mColumns;
    private final MtpDatabase mDatabase;
    private final String mPackageName;
    private final Property[] mProperties;
    private final IContentProvider mProvider;
    private final Uri mUri;
    private final String mVolumeName;

    private class Property {
        int code;
        int column;
        final /* synthetic */ MtpPropertyGroup this$0;
        int type;

        Property(android.mtp.MtpPropertyGroup r1, int r2, int r3, int r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.mtp.MtpPropertyGroup.Property.<init>(android.mtp.MtpPropertyGroup, int, int, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.mtp.MtpPropertyGroup.Property.<init>(android.mtp.MtpPropertyGroup, int, int, int):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.mtp.MtpPropertyGroup.Property.<init>(android.mtp.MtpPropertyGroup, int, int, int):void");
        }
    }

    private native String format_date_time(long j);

    public MtpPropertyGroup(MtpDatabase database, IContentProvider provider, String packageName, String volume, int[] properties) {
        int i;
        this.mDatabase = database;
        this.mProvider = provider;
        this.mPackageName = packageName;
        this.mVolumeName = volume;
        this.mUri = Files.getMtpObjectsUri(volume);
        int count = properties.length;
        ArrayList<String> columns = new ArrayList(count);
        columns.add(SubscriptionManager.UNIQUE_KEY_SUBSCRIPTION_ID);
        this.mProperties = new Property[count];
        for (i = 0; i < count; i++) {
            this.mProperties[i] = createProperty(properties[i], columns);
        }
        count = columns.size();
        this.mColumns = new String[count];
        for (i = 0; i < count; i++) {
            this.mColumns[i] = (String) columns.get(i);
        }
    }

    private Property createProperty(int code, ArrayList<String> columns) {
        int type;
        String column = null;
        switch (code) {
            case MtpConstants.PROPERTY_STORAGE_ID /*56321*/:
                column = FileColumns.STORAGE_ID;
                type = 6;
                break;
            case MtpConstants.PROPERTY_OBJECT_FORMAT /*56322*/:
                column = FileColumns.FORMAT;
                type = 4;
                break;
            case MtpConstants.PROPERTY_PROTECTION_STATUS /*56323*/:
                type = 4;
                break;
            case MtpConstants.PROPERTY_OBJECT_SIZE /*56324*/:
                column = OpenableColumns.SIZE;
                type = 8;
                break;
            case MtpConstants.PROPERTY_OBJECT_FILE_NAME /*56327*/:
                column = Voicemails._DATA;
                type = AppSecurityPermissions.WHICH_ALL;
                break;
            case MtpConstants.PROPERTY_DATE_MODIFIED /*56329*/:
                column = PlaylistsColumns.DATE_MODIFIED;
                type = AppSecurityPermissions.WHICH_ALL;
                break;
            case MtpConstants.PROPERTY_PARENT_OBJECT /*56331*/:
                column = FileColumns.PARENT;
                type = 6;
                break;
            case MtpConstants.PROPERTY_PERSISTENT_UID /*56385*/:
                column = FileColumns.STORAGE_ID;
                type = 10;
                break;
            case MtpConstants.PROPERTY_NAME /*56388*/:
                column = Bookmarks.TITLE;
                type = AppSecurityPermissions.WHICH_ALL;
                break;
            case MtpConstants.PROPERTY_ARTIST /*56390*/:
                type = AppSecurityPermissions.WHICH_ALL;
                break;
            case MtpConstants.PROPERTY_DESCRIPTION /*56392*/:
                column = VideoColumns.DESCRIPTION;
                type = AppSecurityPermissions.WHICH_ALL;
                break;
            case MtpConstants.PROPERTY_DATE_ADDED /*56398*/:
                column = PlaylistsColumns.DATE_ADDED;
                type = AppSecurityPermissions.WHICH_ALL;
                break;
            case MtpConstants.PROPERTY_DURATION /*56457*/:
                column = Voicemails.DURATION;
                type = 6;
                break;
            case MtpConstants.PROPERTY_TRACK /*56459*/:
                column = AudioColumns.TRACK;
                type = 4;
                break;
            case MtpConstants.PROPERTY_GENRE /*56460*/:
                type = AppSecurityPermissions.WHICH_ALL;
                break;
            case MtpConstants.PROPERTY_COMPOSER /*56470*/:
                column = AudioColumns.COMPOSER;
                type = AppSecurityPermissions.WHICH_ALL;
                break;
            case MtpConstants.PROPERTY_ORIGINAL_RELEASE_DATE /*56473*/:
                column = AudioColumns.YEAR;
                type = AppSecurityPermissions.WHICH_ALL;
                break;
            case MtpConstants.PROPERTY_ALBUM_NAME /*56474*/:
                type = AppSecurityPermissions.WHICH_ALL;
                break;
            case MtpConstants.PROPERTY_ALBUM_ARTIST /*56475*/:
                column = AudioColumns.ALBUM_ARTIST;
                type = AppSecurityPermissions.WHICH_ALL;
                break;
            case MtpConstants.PROPERTY_DISPLAY_NAME /*56544*/:
                column = OpenableColumns.DISPLAY_NAME;
                type = AppSecurityPermissions.WHICH_ALL;
                break;
            case MtpConstants.PROPERTY_BITRATE_TYPE /*56978*/:
            case MtpConstants.PROPERTY_NUMBER_OF_CHANNELS /*56980*/:
                type = 4;
                break;
            case MtpConstants.PROPERTY_SAMPLE_RATE /*56979*/:
            case MtpConstants.PROPERTY_AUDIO_WAVE_CODEC /*56985*/:
            case MtpConstants.PROPERTY_AUDIO_BITRATE /*56986*/:
                type = 6;
                break;
            default:
                type = 0;
                Log.e(TAG, "unsupported property " + code);
                break;
        }
        if (column == null) {
            return new Property(this, code, type, -1);
        }
        columns.add(column);
        return new Property(this, code, type, columns.size() - 1);
    }

    private String queryString(int id, String column) {
        Cursor c = null;
        try {
            c = this.mProvider.query(this.mPackageName, this.mUri, new String[]{SubscriptionManager.UNIQUE_KEY_SUBSCRIPTION_ID, column}, ID_WHERE, new String[]{Integer.toString(id)}, null, null);
            String str;
            if (c == null || !c.moveToNext()) {
                str = ProxyInfo.LOCAL_EXCL_LIST;
                if (c == null) {
                    return str;
                }
                c.close();
                return str;
            }
            str = c.getString(1);
            if (c == null) {
                return str;
            }
            c.close();
            return str;
        } catch (Exception e) {
            if (c != null) {
                c.close();
            }
            return null;
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
        }
    }

    private String queryAudio(int id, String column) {
        Cursor c = null;
        try {
            c = this.mProvider.query(this.mPackageName, Media.getContentUri(this.mVolumeName), new String[]{SubscriptionManager.UNIQUE_KEY_SUBSCRIPTION_ID, column}, ID_WHERE, new String[]{Integer.toString(id)}, null, null);
            String str;
            if (c == null || !c.moveToNext()) {
                str = ProxyInfo.LOCAL_EXCL_LIST;
                if (c == null) {
                    return str;
                }
                c.close();
                return str;
            }
            str = c.getString(1);
            if (c == null) {
                return str;
            }
            c.close();
            return str;
        } catch (Exception e) {
            if (c != null) {
                c.close();
            }
            return null;
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
        }
    }

    private String queryGenre(int id) {
        Cursor c = null;
        try {
            Uri uri = Genres.getContentUriForAudioId(this.mVolumeName, id);
            c = this.mProvider.query(this.mPackageName, uri, new String[]{SubscriptionManager.UNIQUE_KEY_SUBSCRIPTION_ID, KeyChain.EXTRA_NAME}, null, null, null, null);
            String str;
            if (c == null || !c.moveToNext()) {
                str = ProxyInfo.LOCAL_EXCL_LIST;
                if (c == null) {
                    return str;
                }
                c.close();
                return str;
            }
            str = c.getString(1);
            if (c == null) {
                return str;
            }
            c.close();
            return str;
        } catch (Exception e) {
            Log.e(TAG, "queryGenre exception", e);
            return null;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private Long queryLong(int id, String column) {
        Cursor c = null;
        try {
            c = this.mProvider.query(this.mPackageName, this.mUri, new String[]{SubscriptionManager.UNIQUE_KEY_SUBSCRIPTION_ID, column}, ID_WHERE, new String[]{Integer.toString(id)}, null, null);
            if (c == null || !c.moveToNext()) {
                if (c != null) {
                    c.close();
                }
                return null;
            }
            Long l = new Long(c.getLong(1));
            if (c == null) {
                return l;
            }
            c.close();
            return l;
        } catch (Exception e) {
            if (c != null) {
                c.close();
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
        }
    }

    private static String nameFromPath(String path) {
        int start = 0;
        int lastSlash = path.lastIndexOf(47);
        if (lastSlash >= 0) {
            start = lastSlash + 1;
        }
        int end = path.length();
        if (end - start > EditorInfo.IME_MASK_ACTION) {
            end = start + EditorInfo.IME_MASK_ACTION;
        }
        return path.substring(start, end);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    android.mtp.MtpPropertyList getPropertyList(int r33, int r34, int r35) {
        /*
        r32 = this;
        r2 = 1;
        r0 = r35;
        if (r0 <= r2) goto L_0x000f;
    L_0x0005:
        r8 = new android.mtp.MtpPropertyList;
        r2 = 0;
        r3 = 43016; // 0xa808 float:6.0278E-41 double:2.12527E-319;
        r8.<init>(r2, r3);
    L_0x000e:
        return r8;
    L_0x000f:
        if (r34 != 0) goto L_0x0065;
    L_0x0011:
        r2 = -1;
        r0 = r33;
        if (r0 != r2) goto L_0x004f;
    L_0x0016:
        r6 = 0;
        r7 = 0;
    L_0x0018:
        r20 = 0;
        if (r35 > 0) goto L_0x0029;
    L_0x001c:
        r2 = -1;
        r0 = r33;
        if (r0 == r2) goto L_0x0029;
    L_0x0021:
        r0 = r32;
        r2 = r0.mColumns;	 Catch:{ RemoteException -> 0x00fe }
        r2 = r2.length;	 Catch:{ RemoteException -> 0x00fe }
        r3 = 1;
        if (r2 <= r3) goto L_0x0094;
    L_0x0029:
        r0 = r32;
        r2 = r0.mProvider;	 Catch:{ RemoteException -> 0x00fe }
        r0 = r32;
        r3 = r0.mPackageName;	 Catch:{ RemoteException -> 0x00fe }
        r0 = r32;
        r4 = r0.mUri;	 Catch:{ RemoteException -> 0x00fe }
        r0 = r32;
        r5 = r0.mColumns;	 Catch:{ RemoteException -> 0x00fe }
        r8 = 0;
        r9 = 0;
        r20 = r2.query(r3, r4, r5, r6, r7, r8, r9);	 Catch:{ RemoteException -> 0x00fe }
        if (r20 != 0) goto L_0x0094;
    L_0x0041:
        r8 = new android.mtp.MtpPropertyList;	 Catch:{ RemoteException -> 0x00fe }
        r2 = 0;
        r3 = 8201; // 0x2009 float:1.1492E-41 double:4.052E-320;
        r8.<init>(r2, r3);	 Catch:{ RemoteException -> 0x00fe }
        if (r20 == 0) goto L_0x000e;
    L_0x004b:
        r20.close();
        goto L_0x000e;
    L_0x004f:
        r2 = 1;
        r7 = new java.lang.String[r2];
        r2 = 0;
        r3 = java.lang.Integer.toString(r33);
        r7[r2] = r3;
        r2 = 1;
        r0 = r35;
        if (r0 != r2) goto L_0x0062;
    L_0x005e:
        r6 = "parent=?";
        goto L_0x0018;
    L_0x0062:
        r6 = "_id=?";
        goto L_0x0018;
    L_0x0065:
        r2 = -1;
        r0 = r33;
        if (r0 != r2) goto L_0x0077;
    L_0x006a:
        r6 = "format=?";
        r2 = 1;
        r7 = new java.lang.String[r2];
        r2 = 0;
        r3 = java.lang.Integer.toString(r34);
        r7[r2] = r3;
        goto L_0x0018;
    L_0x0077:
        r2 = 2;
        r7 = new java.lang.String[r2];
        r2 = 0;
        r3 = java.lang.Integer.toString(r33);
        r7[r2] = r3;
        r2 = 1;
        r3 = java.lang.Integer.toString(r34);
        r7[r2] = r3;
        r2 = 1;
        r0 = r35;
        if (r0 != r2) goto L_0x0091;
    L_0x008d:
        r6 = "parent=? AND format=?";
        goto L_0x0018;
    L_0x0091:
        r6 = "_id=? AND format=?";
        goto L_0x0018;
    L_0x0094:
        if (r20 != 0) goto L_0x00f0;
    L_0x0096:
        r22 = 1;
    L_0x0098:
        r8 = new android.mtp.MtpPropertyList;	 Catch:{ RemoteException -> 0x00fe }
        r0 = r32;
        r2 = r0.mProperties;	 Catch:{ RemoteException -> 0x00fe }
        r2 = r2.length;	 Catch:{ RemoteException -> 0x00fe }
        r2 = r2 * r22;
        r3 = 8193; // 0x2001 float:1.1481E-41 double:4.048E-320;
        r8.<init>(r2, r3);	 Catch:{ RemoteException -> 0x00fe }
        r27 = 0;
    L_0x00a8:
        r0 = r27;
        r1 = r22;
        if (r0 >= r1) goto L_0x0241;
    L_0x00ae:
        if (r20 == 0) goto L_0x00bd;
    L_0x00b0:
        r20.moveToNext();	 Catch:{ RemoteException -> 0x00fe }
        r2 = 0;
        r0 = r20;
        r2 = r0.getLong(r2);	 Catch:{ RemoteException -> 0x00fe }
        r0 = (int) r2;	 Catch:{ RemoteException -> 0x00fe }
        r33 = r0;
    L_0x00bd:
        r29 = 0;
    L_0x00bf:
        r0 = r32;
        r2 = r0.mProperties;	 Catch:{ RemoteException -> 0x00fe }
        r2 = r2.length;	 Catch:{ RemoteException -> 0x00fe }
        r0 = r29;
        if (r0 >= r2) goto L_0x023d;
    L_0x00c8:
        r0 = r32;
        r2 = r0.mProperties;	 Catch:{ RemoteException -> 0x00fe }
        r28 = r2[r29];	 Catch:{ RemoteException -> 0x00fe }
        r0 = r28;
        r10 = r0.code;	 Catch:{ RemoteException -> 0x00fe }
        r0 = r28;
        r0 = r0.column;	 Catch:{ RemoteException -> 0x00fe }
        r21 = r0;
        switch(r10) {
            case 56323: goto L_0x00f5;
            case 56327: goto L_0x010e;
            case 56329: goto L_0x015e;
            case 56385: goto L_0x0194;
            case 56388: goto L_0x012b;
            case 56390: goto L_0x01bd;
            case 56398: goto L_0x015e;
            case 56459: goto L_0x01a8;
            case 56460: goto L_0x01df;
            case 56473: goto L_0x0170;
            case 56474: goto L_0x01ce;
            case 56978: goto L_0x0203;
            case 56979: goto L_0x01f5;
            case 56980: goto L_0x0203;
            case 56985: goto L_0x01f5;
            case 56986: goto L_0x01f5;
            default: goto L_0x00db;
        };	 Catch:{ RemoteException -> 0x00fe }
    L_0x00db:
        r0 = r28;
        r2 = r0.type;	 Catch:{ RemoteException -> 0x00fe }
        r3 = 65535; // 0xffff float:9.1834E-41 double:3.23786E-319;
        if (r2 != r3) goto L_0x0211;
    L_0x00e4:
        r2 = r20.getString(r21);	 Catch:{ RemoteException -> 0x00fe }
        r0 = r33;
        r8.append(r0, r10, r2);	 Catch:{ RemoteException -> 0x00fe }
    L_0x00ed:
        r29 = r29 + 1;
        goto L_0x00bf;
    L_0x00f0:
        r22 = r20.getCount();	 Catch:{ RemoteException -> 0x00fe }
        goto L_0x0098;
    L_0x00f5:
        r11 = 4;
        r12 = 0;
        r9 = r33;
        r8.append(r9, r10, r11, r12);	 Catch:{ RemoteException -> 0x00fe }
        goto L_0x00ed;
    L_0x00fe:
        r24 = move-exception;
        r8 = new android.mtp.MtpPropertyList;	 Catch:{ all -> 0x011e }
        r2 = 0;
        r3 = 8194; // 0x2002 float:1.1482E-41 double:4.0484E-320;
        r8.<init>(r2, r3);	 Catch:{ all -> 0x011e }
        if (r20 == 0) goto L_0x000e;
    L_0x0109:
        r20.close();
        goto L_0x000e;
    L_0x010e:
        r30 = r20.getString(r21);	 Catch:{ RemoteException -> 0x00fe }
        if (r30 == 0) goto L_0x0125;
    L_0x0114:
        r2 = nameFromPath(r30);	 Catch:{ RemoteException -> 0x00fe }
        r0 = r33;
        r8.append(r0, r10, r2);	 Catch:{ RemoteException -> 0x00fe }
        goto L_0x00ed;
    L_0x011e:
        r2 = move-exception;
        if (r20 == 0) goto L_0x0124;
    L_0x0121:
        r20.close();
    L_0x0124:
        throw r2;
    L_0x0125:
        r2 = 8201; // 0x2009 float:1.1492E-41 double:4.052E-320;
        r8.setResult(r2);	 Catch:{ RemoteException -> 0x00fe }
        goto L_0x00ed;
    L_0x012b:
        r26 = r20.getString(r21);	 Catch:{ RemoteException -> 0x00fe }
        if (r26 != 0) goto L_0x013c;
    L_0x0131:
        r2 = "name";
        r0 = r32;
        r1 = r33;
        r26 = r0.queryString(r1, r2);	 Catch:{ RemoteException -> 0x00fe }
    L_0x013c:
        if (r26 != 0) goto L_0x014e;
    L_0x013e:
        r2 = "_data";
        r0 = r32;
        r1 = r33;
        r26 = r0.queryString(r1, r2);	 Catch:{ RemoteException -> 0x00fe }
        if (r26 == 0) goto L_0x014e;
    L_0x014a:
        r26 = nameFromPath(r26);	 Catch:{ RemoteException -> 0x00fe }
    L_0x014e:
        if (r26 == 0) goto L_0x0158;
    L_0x0150:
        r0 = r33;
        r1 = r26;
        r8.append(r0, r10, r1);	 Catch:{ RemoteException -> 0x00fe }
        goto L_0x00ed;
    L_0x0158:
        r2 = 8201; // 0x2009 float:1.1492E-41 double:4.052E-320;
        r8.setResult(r2);	 Catch:{ RemoteException -> 0x00fe }
        goto L_0x00ed;
    L_0x015e:
        r2 = r20.getInt(r21);	 Catch:{ RemoteException -> 0x00fe }
        r2 = (long) r2;	 Catch:{ RemoteException -> 0x00fe }
        r0 = r32;
        r2 = r0.format_date_time(r2);	 Catch:{ RemoteException -> 0x00fe }
        r0 = r33;
        r8.append(r0, r10, r2);	 Catch:{ RemoteException -> 0x00fe }
        goto L_0x00ed;
    L_0x0170:
        r31 = r20.getInt(r21);	 Catch:{ RemoteException -> 0x00fe }
        r2 = new java.lang.StringBuilder;	 Catch:{ RemoteException -> 0x00fe }
        r2.<init>();	 Catch:{ RemoteException -> 0x00fe }
        r3 = java.lang.Integer.toString(r31);	 Catch:{ RemoteException -> 0x00fe }
        r2 = r2.append(r3);	 Catch:{ RemoteException -> 0x00fe }
        r3 = "0101T000000";
        r2 = r2.append(r3);	 Catch:{ RemoteException -> 0x00fe }
        r23 = r2.toString();	 Catch:{ RemoteException -> 0x00fe }
        r0 = r33;
        r1 = r23;
        r8.append(r0, r10, r1);	 Catch:{ RemoteException -> 0x00fe }
        goto L_0x00ed;
    L_0x0194:
        r12 = r20.getLong(r21);	 Catch:{ RemoteException -> 0x00fe }
        r2 = 32;
        r12 = r12 << r2;
        r0 = r33;
        r2 = (long) r0;	 Catch:{ RemoteException -> 0x00fe }
        r12 = r12 + r2;
        r11 = 10;
        r9 = r33;
        r8.append(r9, r10, r11, r12);	 Catch:{ RemoteException -> 0x00fe }
        goto L_0x00ed;
    L_0x01a8:
        r17 = 4;
        r2 = r20.getInt(r21);	 Catch:{ RemoteException -> 0x00fe }
        r2 = r2 % 1000;
        r0 = (long) r2;	 Catch:{ RemoteException -> 0x00fe }
        r18 = r0;
        r14 = r8;
        r15 = r33;
        r16 = r10;
        r14.append(r15, r16, r17, r18);	 Catch:{ RemoteException -> 0x00fe }
        goto L_0x00ed;
    L_0x01bd:
        r2 = "artist";
        r0 = r32;
        r1 = r33;
        r2 = r0.queryAudio(r1, r2);	 Catch:{ RemoteException -> 0x00fe }
        r0 = r33;
        r8.append(r0, r10, r2);	 Catch:{ RemoteException -> 0x00fe }
        goto L_0x00ed;
    L_0x01ce:
        r2 = "album";
        r0 = r32;
        r1 = r33;
        r2 = r0.queryAudio(r1, r2);	 Catch:{ RemoteException -> 0x00fe }
        r0 = r33;
        r8.append(r0, r10, r2);	 Catch:{ RemoteException -> 0x00fe }
        goto L_0x00ed;
    L_0x01df:
        r25 = r32.queryGenre(r33);	 Catch:{ RemoteException -> 0x00fe }
        if (r25 == 0) goto L_0x01ee;
    L_0x01e5:
        r0 = r33;
        r1 = r25;
        r8.append(r0, r10, r1);	 Catch:{ RemoteException -> 0x00fe }
        goto L_0x00ed;
    L_0x01ee:
        r2 = 8201; // 0x2009 float:1.1492E-41 double:4.052E-320;
        r8.setResult(r2);	 Catch:{ RemoteException -> 0x00fe }
        goto L_0x00ed;
    L_0x01f5:
        r17 = 6;
        r18 = 0;
        r14 = r8;
        r15 = r33;
        r16 = r10;
        r14.append(r15, r16, r17, r18);	 Catch:{ RemoteException -> 0x00fe }
        goto L_0x00ed;
    L_0x0203:
        r17 = 4;
        r18 = 0;
        r14 = r8;
        r15 = r33;
        r16 = r10;
        r14.append(r15, r16, r17, r18);	 Catch:{ RemoteException -> 0x00fe }
        goto L_0x00ed;
    L_0x0211:
        r0 = r28;
        r2 = r0.type;	 Catch:{ RemoteException -> 0x00fe }
        if (r2 != 0) goto L_0x0229;
    L_0x0217:
        r0 = r28;
        r0 = r0.type;	 Catch:{ RemoteException -> 0x00fe }
        r17 = r0;
        r18 = 0;
        r14 = r8;
        r15 = r33;
        r16 = r10;
        r14.append(r15, r16, r17, r18);	 Catch:{ RemoteException -> 0x00fe }
        goto L_0x00ed;
    L_0x0229:
        r0 = r28;
        r0 = r0.type;	 Catch:{ RemoteException -> 0x00fe }
        r17 = r0;
        r18 = r20.getLong(r21);	 Catch:{ RemoteException -> 0x00fe }
        r14 = r8;
        r15 = r33;
        r16 = r10;
        r14.append(r15, r16, r17, r18);	 Catch:{ RemoteException -> 0x00fe }
        goto L_0x00ed;
    L_0x023d:
        r27 = r27 + 1;
        goto L_0x00a8;
    L_0x0241:
        if (r20 == 0) goto L_0x000e;
    L_0x0243:
        r20.close();
        goto L_0x000e;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.mtp.MtpPropertyGroup.getPropertyList(int, int, int):android.mtp.MtpPropertyList");
    }
}
