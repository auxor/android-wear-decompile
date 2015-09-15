package com.android.internal.policy.impl;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.provider.Settings.Bookmarks;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyCharacterMap;
import java.net.URISyntaxException;

class ShortcutManager extends ContentObserver {
    private static final int COLUMN_INTENT = 1;
    private static final int COLUMN_SHORTCUT = 0;
    private static final String TAG = "ShortcutManager";
    private static final String[] sProjection;
    private Context mContext;
    private Cursor mCursor;
    private SparseArray<Intent> mShortcutIntents;

    static {
        sProjection = new String[]{"shortcut", "intent"};
    }

    public ShortcutManager(Context context, Handler handler) {
        super(handler);
        this.mContext = context;
        this.mShortcutIntents = new SparseArray();
    }

    public void observe() {
        this.mCursor = this.mContext.getContentResolver().query(Bookmarks.CONTENT_URI, sProjection, null, null, null);
        this.mCursor.registerContentObserver(this);
        updateShortcuts();
    }

    public void onChange(boolean selfChange) {
        updateShortcuts();
    }

    private void updateShortcuts() {
        Cursor c = this.mCursor;
        if (c.requery()) {
            this.mShortcutIntents.clear();
            while (c.moveToNext()) {
                int shortcut = c.getInt(COLUMN_SHORTCUT);
                if (shortcut != 0) {
                    Intent intent = null;
                    try {
                        intent = Intent.getIntent(c.getString(COLUMN_INTENT));
                    } catch (URISyntaxException e) {
                        Log.w(TAG, "Intent URI for shortcut invalid.", e);
                    }
                    if (intent != null) {
                        this.mShortcutIntents.put(shortcut, intent);
                    }
                }
            }
            return;
        }
        Log.e(TAG, "ShortcutObserver could not re-query shortcuts.");
    }

    public Intent getIntent(KeyCharacterMap kcm, int keyCode, int metaState) {
        Intent intent = null;
        int shortcut = kcm.get(keyCode, metaState);
        if (shortcut != 0) {
            intent = (Intent) this.mShortcutIntents.get(shortcut);
        }
        if (intent != null) {
            return intent;
        }
        shortcut = Character.toLowerCase(kcm.getDisplayLabel(keyCode));
        if (shortcut != 0) {
            return (Intent) this.mShortcutIntents.get(shortcut);
        }
        return intent;
    }
}
