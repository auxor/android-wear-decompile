package com.google.android.clockwork.settings;

import android.net.Uri;
import android.net.Uri.Builder;

public class SettingsContract {
    public static final Uri ALL_SETTINGS_URI;
    public static final Uri AMBIENT_CONFIG_URI;
    public static final Uri AUTO_WIFI_URI;
    public static final Uri BLUETOOTH_MODE_URI;
    public static final Uri BUG_REPORT_URI;
    public static final Uri BURN_IN_CONFIG_URI;
    public static final Uri CAPABILITIES_URI;
    public static final Uri DEEP_AMBIENT_MODE_URI;
    public static final Uri DISABLE_AMBIENT_IN_THEATER_MODE_URI;
    public static final Uri DISPLAY_SHAPE_URI;
    public static final Uri ENABLE_UNREAD_WATCHFACE_URI;
    public static final Uri FITNESS_DISABLED_DURING_SETUP_URI;
    public static final Uri FORCE_SCREEN_TIMEOUT_URI;
    public static final Uri LICENSE_PATH_URI;
    public static final Uri PLAY_STORE_AVAILABILITY_URI;
    public static final Uri RETAIL_MODE_URI;
    public static final Uri SUNLIGHT_MODE_URI;
    public static final Uri SYSTEM_APPS_NOTIF_WHITELIST_URI;
    public static final Uri THEATER_MODE_URI;
    public static final Uri WIFI_POWER_SAVE_URI;

    static {
        RETAIL_MODE_URI = new Builder().scheme("content").authority("com.google.android.wearable.settings").path("retail").build();
        DISPLAY_SHAPE_URI = new Builder().scheme("content").authority("com.google.android.wearable.settings").path("shape").build();
        BUG_REPORT_URI = new Builder().scheme("content").authority("com.google.android.wearable.settings").path("bug_report").build();
        BLUETOOTH_MODE_URI = new Builder().scheme("content").authority("com.google.android.wearable.settings").path("bluetooth_mode").build();
        PLAY_STORE_AVAILABILITY_URI = new Builder().scheme("content").authority("com.google.android.wearable.settings").path("play_store_availability").build();
        THEATER_MODE_URI = new Builder().scheme("content").authority("com.google.android.wearable.settings").path("theater_mode").build();
        SUNLIGHT_MODE_URI = new Builder().scheme("content").authority("com.google.android.wearable.settings").path("sunlight_mode").build();
        DEEP_AMBIENT_MODE_URI = new Builder().scheme("content").authority("com.google.android.wearable.settings").path("deep_ambient_mode").build();
        DISABLE_AMBIENT_IN_THEATER_MODE_URI = new Builder().scheme("content").authority("com.google.android.wearable.settings").path("disable_ambient_in_theater_mode").build();
        BURN_IN_CONFIG_URI = new Builder().scheme("content").authority("com.google.android.wearable.settings").path("burn_in_config").build();
        AMBIENT_CONFIG_URI = new Builder().scheme("content").authority("com.google.android.wearable.settings").path("ambient_config").build();
        FORCE_SCREEN_TIMEOUT_URI = new Builder().scheme("content").authority("com.google.android.wearable.settings").path("force_screen_timeout").build();
        LICENSE_PATH_URI = new Builder().scheme("content").authority("com.google.android.wearable.settings").path("license_path").build();
        ENABLE_UNREAD_WATCHFACE_URI = new Builder().scheme("content").authority("com.google.android.wearable.settings").path("enable_unread_watchface").build();
        FITNESS_DISABLED_DURING_SETUP_URI = new Builder().scheme("content").authority("com.google.android.wearable.settings").path("fitness_disabled_during_setup").build();
        CAPABILITIES_URI = new Builder().scheme("content").authority("com.google.android.wearable.settings").path("capabilities").build();
        ALL_SETTINGS_URI = new Builder().scheme("content").authority("com.google.android.wearable.settings").path("settings").build();
        AUTO_WIFI_URI = new Builder().scheme("content").authority("com.google.android.wearable.settings").path("auto_wifi").build();
        WIFI_POWER_SAVE_URI = new Builder().scheme("content").authority("com.google.android.wearable.settings").path("wifi_power_save").build();
        SYSTEM_APPS_NOTIF_WHITELIST_URI = new Builder().scheme("content").authority("com.google.android.wearable.settings").path("system_apps_notif_whitelist").build();
    }
}
