package com.google.android.clockwork.settings;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;

public class BurnInConfig {
    private boolean mBurnInProtection;
    private final Context mContext;
    private final Handler mHandler;
    private final ContentObserver mObserver;

    /* renamed from: com.google.android.clockwork.settings.BurnInConfig.1 */
    class AnonymousClass1 extends ContentObserver {
        AnonymousClass1(Handler handler) {
            super(handler);
            throw new VerifyError("bad dex opcode");
        }

        public void onChange(boolean z) {
            BurnInConfig.this.readValues(BurnInConfig.this.mContext);
        }
    }

    private BurnInConfig(Context context) {
        this.mContext = context;
        this.mHandler = new Handler();
        readValues(context);
        this.mObserver = new AnonymousClass1(this.mHandler);
        throw new VerifyError("bad dex opcode");
    }

    public static BurnInConfig newInstance(Context context) {
        return new BurnInConfig(context);
    }

    public boolean isProtectionEnabled() {
        return this.mBurnInProtection;
    }

    public void readValues(Context context) {
        Cursor query = context.getContentResolver().query(SettingsContract.BURN_IN_CONFIG_URI, null, null, null, null);
        if (query != null) {
            while (query.moveToNext()) {
                try {
                    if (query.getString(0).equals("burn_in_protection")) {
                        this.mBurnInProtection = query.getInt(1) == 1;
                    }
                } finally {
                    query.close();
                }
            }
        }
    }

    public void register() {
        this.mContext.getContentResolver().registerContentObserver(SettingsContract.BURN_IN_CONFIG_URI, false, this.mObserver);
    }

    public void release() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mObserver);
    }

    public String toString() {
        return "BurnInConfig[" + this.mBurnInProtection + "]";
    }
}
