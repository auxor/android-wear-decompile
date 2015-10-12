package com.google.android.clockwork.settings;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

public class AmbientConfig {
    private static AmbientConfig sInstance;
    private boolean mAmbientEnabled;
    private final Context mContext;
    private int mDeepAmbientTimeoutMin;
    private boolean mEnableDeepAmbient;
    private boolean mEnabledWhenPlugged;
    private boolean mForceWhenDocked;
    private int mGestureSensorId;
    private final Handler mHandler;
    private boolean mLowBitEnabled;
    private boolean mLowBitEnabledDev;
    private final ContentObserver mObserver;
    private int mPluggedTimeoutMin;

    /* renamed from: com.google.android.clockwork.settings.AmbientConfig.1 */
    class AnonymousClass1 extends ContentObserver {
        AnonymousClass1(Handler handler) {
            super(handler);
            throw new VerifyError("bad dex opcode");
        }

        public void onChange(boolean z) {
            AmbientConfig.this.readValues(AmbientConfig.this.mContext);
        }
    }

    private AmbientConfig(Context context) {
        this.mContext = context;
        this.mHandler = new Handler();
        readValues(context);
        this.mObserver = new AnonymousClass1(this.mHandler);
        throw new VerifyError("bad dex opcode");
    }

    private boolean getBoolean(Cursor cursor) {
        return cursor.getInt(1) == 1;
    }

    public static AmbientConfig getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AmbientConfig.class) {
                try {
                    if (sInstance == null) {
                        sInstance = new AmbientConfig(context.getApplicationContext());
                        sInstance.register();
                    }
                } catch (Throwable th) {
                    while (true) {
                        break;
                    }
                    Class cls = AmbientConfig.class;
                }
            }
        }
        return sInstance;
    }

    public static AmbientConfig newInstance(Context context) {
        return new AmbientConfig(context);
    }

    public int getPluggedTimeoutMin() {
        return this.mPluggedTimeoutMin;
    }

    public boolean isAmbientEnabled() {
        return this.mAmbientEnabled;
    }

    public boolean isLowBitEnabled() {
        return this.mLowBitEnabled || this.mLowBitEnabledDev;
    }

    public void readValues(Context context) {
        if (Log.isLoggable("AmbientConfig", 3)) {
            Log.d("AmbientConfig", "readValues");
        }
        Cursor query = context.getContentResolver().query(SettingsContract.AMBIENT_CONFIG_URI, null, null, null, null);
        if (query != null) {
            while (query.moveToNext()) {
                try {
                    String string = query.getString(0);
                    if (string.equals("ambient_enabled")) {
                        this.mAmbientEnabled = getBoolean(query);
                    } else if (string.equals("deep_ambient_timeout_min")) {
                        this.mDeepAmbientTimeoutMin = query.getInt(1);
                    } else if (string.equals("enable_deep_ambient")) {
                        this.mEnableDeepAmbient = getBoolean(query);
                    } else if (string.equals("ambient_enable_when_plugged")) {
                        this.mEnabledWhenPlugged = getBoolean(query);
                    } else if (string.equals("ambient_force_when_docked")) {
                        this.mForceWhenDocked = getBoolean(query);
                    } else if (string.equals("ambient_gesture_sensor_id")) {
                        this.mGestureSensorId = query.getInt(1);
                    } else if (string.equals("ambient_low_bit_enabled")) {
                        this.mLowBitEnabled = getBoolean(query);
                    } else if (string.equals("ambient_plugged_timeout_min")) {
                        this.mPluggedTimeoutMin = query.getInt(1);
                    } else if (string.equals("ambient_low_bit_enabled_dev")) {
                        this.mLowBitEnabledDev = getBoolean(query);
                    }
                } finally {
                    query.close();
                }
            }
        }
    }

    public void register() {
        this.mContext.getContentResolver().registerContentObserver(SettingsContract.AMBIENT_CONFIG_URI, false, this.mObserver);
    }

    public void release() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mObserver);
    }
}
