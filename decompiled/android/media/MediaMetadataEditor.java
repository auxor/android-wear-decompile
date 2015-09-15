package android.media;

import android.graphics.Bitmap;
import android.media.MediaMetadata.Builder;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseIntArray;

@Deprecated
public abstract class MediaMetadataEditor {
    public static final int BITMAP_KEY_ARTWORK = 100;
    public static final int KEY_EDITABLE_MASK = 536870911;
    protected static final SparseIntArray METADATA_KEYS_TYPE;
    protected static final int METADATA_TYPE_BITMAP = 2;
    protected static final int METADATA_TYPE_INVALID = -1;
    protected static final int METADATA_TYPE_LONG = 0;
    protected static final int METADATA_TYPE_RATING = 3;
    protected static final int METADATA_TYPE_STRING = 1;
    public static final int RATING_KEY_BY_OTHERS = 101;
    public static final int RATING_KEY_BY_USER = 268435457;
    private static final String TAG = "MediaMetadataEditor";
    protected boolean mApplied;
    protected boolean mArtworkChanged;
    protected long mEditableKeys;
    protected Bitmap mEditorArtwork;
    protected Bundle mEditorMetadata;
    protected Builder mMetadataBuilder;
    protected boolean mMetadataChanged;

    public abstract void apply();

    protected MediaMetadataEditor() {
        this.mMetadataChanged = false;
        this.mApplied = false;
        this.mArtworkChanged = false;
    }

    public synchronized void clear() {
        if (this.mApplied) {
            Log.e(TAG, "Can't clear a previously applied MediaMetadataEditor");
        } else {
            this.mEditorMetadata.clear();
            this.mEditorArtwork = null;
            this.mMetadataBuilder = new Builder();
        }
    }

    public synchronized void addEditableKey(int key) {
        if (this.mApplied) {
            Log.e(TAG, "Can't change editable keys of a previously applied MetadataEditor");
        } else if (key == RATING_KEY_BY_USER) {
            this.mEditableKeys |= (long) (KEY_EDITABLE_MASK & key);
            this.mMetadataChanged = true;
        } else {
            Log.e(TAG, "Metadata key " + key + " cannot be edited");
        }
    }

    public synchronized void removeEditableKeys() {
        if (this.mApplied) {
            Log.e(TAG, "Can't remove all editable keys of a previously applied MetadataEditor");
        } else if (this.mEditableKeys != 0) {
            this.mEditableKeys = 0;
            this.mMetadataChanged = true;
        }
    }

    public synchronized int[] getEditableKeys() {
        int[] iArr;
        if (this.mEditableKeys == 268435457) {
            iArr = new int[METADATA_TYPE_STRING];
            iArr[METADATA_TYPE_LONG] = RATING_KEY_BY_USER;
        } else {
            iArr = null;
        }
        return iArr;
    }

    public synchronized MediaMetadataEditor putString(int key, String value) throws IllegalArgumentException {
        MediaMetadataEditor this;
        if (this.mApplied) {
            Log.e(TAG, "Can't edit a previously applied MediaMetadataEditor");
            this = this;
        } else if (METADATA_KEYS_TYPE.get(key, METADATA_TYPE_INVALID) != METADATA_TYPE_STRING) {
            throw new IllegalArgumentException("Invalid type 'String' for key " + key);
        } else {
            this.mEditorMetadata.putString(String.valueOf(key), value);
            this.mMetadataChanged = true;
            this = this;
        }
        return this;
    }

    public synchronized MediaMetadataEditor putLong(int key, long value) throws IllegalArgumentException {
        MediaMetadataEditor this;
        if (this.mApplied) {
            Log.e(TAG, "Can't edit a previously applied MediaMetadataEditor");
            this = this;
        } else if (METADATA_KEYS_TYPE.get(key, METADATA_TYPE_INVALID) != 0) {
            throw new IllegalArgumentException("Invalid type 'long' for key " + key);
        } else {
            this.mEditorMetadata.putLong(String.valueOf(key), value);
            this.mMetadataChanged = true;
            this = this;
        }
        return this;
    }

    public synchronized MediaMetadataEditor putBitmap(int key, Bitmap bitmap) throws IllegalArgumentException {
        MediaMetadataEditor this;
        if (this.mApplied) {
            Log.e(TAG, "Can't edit a previously applied MediaMetadataEditor");
            this = this;
        } else {
            if (key != BITMAP_KEY_ARTWORK) {
                throw new IllegalArgumentException("Invalid type 'Bitmap' for key " + key);
            }
            this.mEditorArtwork = bitmap;
            this.mArtworkChanged = true;
            this = this;
        }
        return this;
    }

    public synchronized MediaMetadataEditor putObject(int key, Object value) throws IllegalArgumentException {
        MediaMetadataEditor mediaMetadataEditor;
        if (this.mApplied) {
            Log.e(TAG, "Can't edit a previously applied MediaMetadataEditor");
            mediaMetadataEditor = this;
        } else {
            switch (METADATA_KEYS_TYPE.get(key, METADATA_TYPE_INVALID)) {
                case METADATA_TYPE_LONG /*0*/:
                    if (value instanceof Long) {
                        mediaMetadataEditor = putLong(key, ((Long) value).longValue());
                        break;
                    }
                    throw new IllegalArgumentException("Not a non-null Long for key " + key);
                    break;
                case METADATA_TYPE_STRING /*1*/:
                    if (value == null || (value instanceof String)) {
                        mediaMetadataEditor = putString(key, (String) value);
                        break;
                    }
                    throw new IllegalArgumentException("Not a String for key " + key);
                case METADATA_TYPE_BITMAP /*2*/:
                    if (value == null || (value instanceof Bitmap)) {
                        mediaMetadataEditor = putBitmap(key, (Bitmap) value);
                        break;
                    }
                    throw new IllegalArgumentException("Not a Bitmap for key " + key);
                case METADATA_TYPE_RATING /*3*/:
                    this.mEditorMetadata.putParcelable(String.valueOf(key), (Parcelable) value);
                    this.mMetadataChanged = true;
                    mediaMetadataEditor = this;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid key " + key);
            }
        }
        return mediaMetadataEditor;
    }

    public synchronized long getLong(int key, long defaultValue) throws IllegalArgumentException {
        if (METADATA_KEYS_TYPE.get(key, METADATA_TYPE_INVALID) != 0) {
            throw new IllegalArgumentException("Invalid type 'long' for key " + key);
        }
        return this.mEditorMetadata.getLong(String.valueOf(key), defaultValue);
    }

    public synchronized String getString(int key, String defaultValue) throws IllegalArgumentException {
        if (METADATA_KEYS_TYPE.get(key, METADATA_TYPE_INVALID) != METADATA_TYPE_STRING) {
            throw new IllegalArgumentException("Invalid type 'String' for key " + key);
        }
        return this.mEditorMetadata.getString(String.valueOf(key), defaultValue);
    }

    public synchronized Bitmap getBitmap(int key, Bitmap defaultValue) throws IllegalArgumentException {
        if (key != BITMAP_KEY_ARTWORK) {
            throw new IllegalArgumentException("Invalid type 'Bitmap' for key " + key);
        } else if (this.mEditorArtwork != null) {
            defaultValue = this.mEditorArtwork;
        }
        return defaultValue;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized java.lang.Object getObject(int r4, java.lang.Object r5) throws java.lang.IllegalArgumentException {
        /*
        r3 = this;
        monitor-enter(r3);
        r0 = METADATA_KEYS_TYPE;	 Catch:{ all -> 0x0024 }
        r1 = -1;
        r0 = r0.get(r4, r1);	 Catch:{ all -> 0x0024 }
        switch(r0) {
            case 0: goto L_0x0027;
            case 1: goto L_0x0043;
            case 2: goto L_0x0071;
            case 3: goto L_0x005a;
            default: goto L_0x000b;
        };	 Catch:{ all -> 0x0024 }
    L_0x000b:
        r0 = new java.lang.IllegalArgumentException;	 Catch:{ all -> 0x0024 }
        r1 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0024 }
        r1.<init>();	 Catch:{ all -> 0x0024 }
        r2 = "Invalid key ";
        r1 = r1.append(r2);	 Catch:{ all -> 0x0024 }
        r1 = r1.append(r4);	 Catch:{ all -> 0x0024 }
        r1 = r1.toString();	 Catch:{ all -> 0x0024 }
        r0.<init>(r1);	 Catch:{ all -> 0x0024 }
        throw r0;	 Catch:{ all -> 0x0024 }
    L_0x0024:
        r0 = move-exception;
        monitor-exit(r3);
        throw r0;
    L_0x0027:
        r0 = r3.mEditorMetadata;	 Catch:{ all -> 0x0024 }
        r1 = java.lang.String.valueOf(r4);	 Catch:{ all -> 0x0024 }
        r0 = r0.containsKey(r1);	 Catch:{ all -> 0x0024 }
        if (r0 == 0) goto L_0x0041;
    L_0x0033:
        r0 = r3.mEditorMetadata;	 Catch:{ all -> 0x0024 }
        r1 = java.lang.String.valueOf(r4);	 Catch:{ all -> 0x0024 }
        r0 = r0.getLong(r1);	 Catch:{ all -> 0x0024 }
        r5 = java.lang.Long.valueOf(r0);	 Catch:{ all -> 0x0024 }
    L_0x0041:
        monitor-exit(r3);
        return r5;
    L_0x0043:
        r0 = r3.mEditorMetadata;	 Catch:{ all -> 0x0024 }
        r1 = java.lang.String.valueOf(r4);	 Catch:{ all -> 0x0024 }
        r0 = r0.containsKey(r1);	 Catch:{ all -> 0x0024 }
        if (r0 == 0) goto L_0x0041;
    L_0x004f:
        r0 = r3.mEditorMetadata;	 Catch:{ all -> 0x0024 }
        r1 = java.lang.String.valueOf(r4);	 Catch:{ all -> 0x0024 }
        r5 = r0.getString(r1);	 Catch:{ all -> 0x0024 }
        goto L_0x0041;
    L_0x005a:
        r0 = r3.mEditorMetadata;	 Catch:{ all -> 0x0024 }
        r1 = java.lang.String.valueOf(r4);	 Catch:{ all -> 0x0024 }
        r0 = r0.containsKey(r1);	 Catch:{ all -> 0x0024 }
        if (r0 == 0) goto L_0x0041;
    L_0x0066:
        r0 = r3.mEditorMetadata;	 Catch:{ all -> 0x0024 }
        r1 = java.lang.String.valueOf(r4);	 Catch:{ all -> 0x0024 }
        r5 = r0.getParcelable(r1);	 Catch:{ all -> 0x0024 }
        goto L_0x0041;
    L_0x0071:
        r0 = 100;
        if (r4 != r0) goto L_0x000b;
    L_0x0075:
        r0 = r3.mEditorArtwork;	 Catch:{ all -> 0x0024 }
        if (r0 == 0) goto L_0x0041;
    L_0x0079:
        r5 = r3.mEditorArtwork;	 Catch:{ all -> 0x0024 }
        goto L_0x0041;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.MediaMetadataEditor.getObject(int, java.lang.Object):java.lang.Object");
    }

    static {
        METADATA_KEYS_TYPE = new SparseIntArray(17);
        METADATA_KEYS_TYPE.put(METADATA_TYPE_LONG, METADATA_TYPE_LONG);
        METADATA_KEYS_TYPE.put(14, METADATA_TYPE_LONG);
        METADATA_KEYS_TYPE.put(9, METADATA_TYPE_LONG);
        METADATA_KEYS_TYPE.put(8, METADATA_TYPE_LONG);
        METADATA_KEYS_TYPE.put(METADATA_TYPE_STRING, METADATA_TYPE_STRING);
        METADATA_KEYS_TYPE.put(13, METADATA_TYPE_STRING);
        METADATA_KEYS_TYPE.put(7, METADATA_TYPE_STRING);
        METADATA_KEYS_TYPE.put(METADATA_TYPE_BITMAP, METADATA_TYPE_STRING);
        METADATA_KEYS_TYPE.put(METADATA_TYPE_RATING, METADATA_TYPE_STRING);
        METADATA_KEYS_TYPE.put(15, METADATA_TYPE_STRING);
        METADATA_KEYS_TYPE.put(4, METADATA_TYPE_STRING);
        METADATA_KEYS_TYPE.put(5, METADATA_TYPE_STRING);
        METADATA_KEYS_TYPE.put(6, METADATA_TYPE_STRING);
        METADATA_KEYS_TYPE.put(11, METADATA_TYPE_STRING);
        METADATA_KEYS_TYPE.put(BITMAP_KEY_ARTWORK, METADATA_TYPE_BITMAP);
        METADATA_KEYS_TYPE.put(RATING_KEY_BY_OTHERS, METADATA_TYPE_RATING);
        METADATA_KEYS_TYPE.put(RATING_KEY_BY_USER, METADATA_TYPE_RATING);
    }
}
