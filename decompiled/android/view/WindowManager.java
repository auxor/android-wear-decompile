package android.view;

import android.graphics.Insets;
import android.graphics.Rect;
import android.net.ProxyInfo;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.FlagToString;
import android.view.ViewDebug.IntToString;

public interface WindowManager extends ViewManager {

    public static class BadTokenException extends RuntimeException {
        public BadTokenException(String name) {
            super(name);
        }
    }

    public static class InvalidDisplayException extends RuntimeException {
        public InvalidDisplayException(String name) {
            super(name);
        }
    }

    public static class LayoutParams extends android.view.ViewGroup.LayoutParams implements Parcelable {
        public static final int ALPHA_CHANGED = 128;
        public static final int ANIMATION_CHANGED = 16;
        public static final float BRIGHTNESS_OVERRIDE_FULL = 1.0f;
        public static final float BRIGHTNESS_OVERRIDE_NONE = -1.0f;
        public static final float BRIGHTNESS_OVERRIDE_OFF = 0.0f;
        public static final int BUTTON_BRIGHTNESS_CHANGED = 8192;
        public static final Creator<LayoutParams> CREATOR;
        public static final int DIM_AMOUNT_CHANGED = 32;
        public static final int EVERYTHING_CHANGED = -1;
        public static final int FIRST_APPLICATION_WINDOW = 1;
        public static final int FIRST_SUB_WINDOW = 1000;
        public static final int FIRST_SYSTEM_WINDOW = 2000;
        public static final int FLAGS_CHANGED = 4;
        public static final int FLAG_ALLOW_LOCK_WHILE_SCREEN_ON = 1;
        public static final int FLAG_ALT_FOCUSABLE_IM = 131072;
        @Deprecated
        public static final int FLAG_BLUR_BEHIND = 4;
        public static final int FLAG_DIM_BEHIND = 2;
        public static final int FLAG_DISMISS_KEYGUARD = 4194304;
        @Deprecated
        public static final int FLAG_DITHER = 4096;
        public static final int FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS = Integer.MIN_VALUE;
        public static final int FLAG_FORCE_NOT_FULLSCREEN = 2048;
        public static final int FLAG_FULLSCREEN = 1024;
        public static final int FLAG_HARDWARE_ACCELERATED = 16777216;
        public static final int FLAG_IGNORE_CHEEK_PRESSES = 32768;
        public static final int FLAG_KEEP_SCREEN_ON = 128;
        public static final int FLAG_LAYOUT_ATTACHED_IN_DECOR = 1073741824;
        public static final int FLAG_LAYOUT_INSET_DECOR = 65536;
        public static final int FLAG_LAYOUT_IN_OVERSCAN = 33554432;
        public static final int FLAG_LAYOUT_IN_SCREEN = 256;
        public static final int FLAG_LAYOUT_NO_LIMITS = 512;
        public static final int FLAG_LOCAL_FOCUS_MODE = 268435456;
        public static final int FLAG_NOT_FOCUSABLE = 8;
        public static final int FLAG_NOT_TOUCHABLE = 16;
        public static final int FLAG_NOT_TOUCH_MODAL = 32;
        public static final int FLAG_SCALED = 16384;
        public static final int FLAG_SECURE = 8192;
        public static final int FLAG_SHOW_WALLPAPER = 1048576;
        public static final int FLAG_SHOW_WHEN_LOCKED = 524288;
        public static final int FLAG_SLIPPERY = 536870912;
        public static final int FLAG_SPLIT_TOUCH = 8388608;
        @Deprecated
        public static final int FLAG_TOUCHABLE_WHEN_WAKING = 64;
        public static final int FLAG_TRANSLUCENT_NAVIGATION = 134217728;
        public static final int FLAG_TRANSLUCENT_STATUS = 67108864;
        public static final int FLAG_TURN_SCREEN_ON = 2097152;
        public static final int FLAG_WATCH_OUTSIDE_TOUCH = 262144;
        public static final int FORMAT_CHANGED = 8;
        public static final int INPUT_FEATURES_CHANGED = 65536;
        public static final int INPUT_FEATURE_DISABLE_POINTER_GESTURES = 1;
        public static final int INPUT_FEATURE_DISABLE_USER_ACTIVITY = 4;
        public static final int INPUT_FEATURE_NO_INPUT_CHANNEL = 2;
        public static final int LAST_APPLICATION_WINDOW = 99;
        public static final int LAST_SUB_WINDOW = 1999;
        public static final int LAST_SYSTEM_WINDOW = 2999;
        public static final int LAYOUT_CHANGED = 1;
        public static final int MEMORY_TYPE_CHANGED = 256;
        @Deprecated
        public static final int MEMORY_TYPE_GPU = 2;
        @Deprecated
        public static final int MEMORY_TYPE_HARDWARE = 1;
        @Deprecated
        public static final int MEMORY_TYPE_NORMAL = 0;
        @Deprecated
        public static final int MEMORY_TYPE_PUSH_BUFFERS = 3;
        public static final int NEEDS_MENU_KEY_CHANGED = 4194304;
        public static final int NEEDS_MENU_SET_FALSE = 2;
        public static final int NEEDS_MENU_SET_TRUE = 1;
        public static final int NEEDS_MENU_UNSET = 0;
        public static final int PREFERRED_REFRESH_RATE_CHANGED = 2097152;
        public static final int PRIVATE_FLAGS_CHANGED = 131072;
        public static final int PRIVATE_FLAG_COMPATIBLE_WINDOW = 128;
        public static final int PRIVATE_FLAG_DISABLE_WALLPAPER_TOUCH_EVENTS = 2048;
        public static final int PRIVATE_FLAG_FAKE_HARDWARE_ACCELERATED = 1;
        public static final int PRIVATE_FLAG_FORCE_HARDWARE_ACCELERATED = 2;
        public static final int PRIVATE_FLAG_INHERIT_TRANSLUCENT_DECOR = 512;
        public static final int PRIVATE_FLAG_KEYGUARD = 1024;
        public static final int PRIVATE_FLAG_NO_MOVE_ANIMATION = 64;
        public static final int PRIVATE_FLAG_SHOW_FOR_ALL_USERS = 16;
        public static final int PRIVATE_FLAG_SYSTEM_ERROR = 256;
        public static final int PRIVATE_FLAG_WANTS_OFFSET_NOTIFICATIONS = 4;
        public static final int ROTATION_ANIMATION_CHANGED = 4096;
        public static final int ROTATION_ANIMATION_CROSSFADE = 1;
        public static final int ROTATION_ANIMATION_JUMPCUT = 2;
        public static final int ROTATION_ANIMATION_ROTATE = 0;
        public static final int SCREEN_BRIGHTNESS_CHANGED = 2048;
        public static final int SCREEN_ORIENTATION_CHANGED = 1024;
        public static final int SOFT_INPUT_ADJUST_NOTHING = 48;
        public static final int SOFT_INPUT_ADJUST_PAN = 32;
        public static final int SOFT_INPUT_ADJUST_RESIZE = 16;
        public static final int SOFT_INPUT_ADJUST_UNSPECIFIED = 0;
        public static final int SOFT_INPUT_IS_FORWARD_NAVIGATION = 256;
        public static final int SOFT_INPUT_MASK_ADJUST = 240;
        public static final int SOFT_INPUT_MASK_STATE = 15;
        public static final int SOFT_INPUT_MODE_CHANGED = 512;
        public static final int SOFT_INPUT_STATE_ALWAYS_HIDDEN = 3;
        public static final int SOFT_INPUT_STATE_ALWAYS_VISIBLE = 5;
        public static final int SOFT_INPUT_STATE_HIDDEN = 2;
        public static final int SOFT_INPUT_STATE_UNCHANGED = 1;
        public static final int SOFT_INPUT_STATE_UNSPECIFIED = 0;
        public static final int SOFT_INPUT_STATE_VISIBLE = 4;
        public static final int SURFACE_INSETS_CHANGED = 1048576;
        public static final int SYSTEM_UI_LISTENER_CHANGED = 32768;
        public static final int SYSTEM_UI_VISIBILITY_CHANGED = 16384;
        public static final int TITLE_CHANGED = 64;
        public static final int TRANSLUCENT_FLAGS_CHANGED = 524288;
        public static final int TYPE_ACCESSIBILITY_OVERLAY = 2032;
        public static final int TYPE_APPLICATION = 2;
        public static final int TYPE_APPLICATION_ATTACHED_DIALOG = 1003;
        public static final int TYPE_APPLICATION_MEDIA = 1001;
        public static final int TYPE_APPLICATION_MEDIA_OVERLAY = 1004;
        public static final int TYPE_APPLICATION_PANEL = 1000;
        public static final int TYPE_APPLICATION_STARTING = 3;
        public static final int TYPE_APPLICATION_SUB_PANEL = 1002;
        public static final int TYPE_BASE_APPLICATION = 1;
        public static final int TYPE_BOOT_PROGRESS = 2021;
        public static final int TYPE_CHANGED = 2;
        public static final int TYPE_DISPLAY_OVERLAY = 2026;
        public static final int TYPE_DRAG = 2016;
        public static final int TYPE_DREAM = 2023;
        public static final int TYPE_HIDDEN_NAV_CONSUMER = 2022;
        public static final int TYPE_INPUT_METHOD = 2011;
        public static final int TYPE_INPUT_METHOD_DIALOG = 2012;
        public static final int TYPE_KEYGUARD = 2004;
        public static final int TYPE_KEYGUARD_DIALOG = 2009;
        public static final int TYPE_KEYGUARD_SCRIM = 2029;
        public static final int TYPE_MAGNIFICATION_OVERLAY = 2027;
        public static final int TYPE_NAVIGATION_BAR = 2019;
        public static final int TYPE_NAVIGATION_BAR_PANEL = 2024;
        public static final int TYPE_PHONE = 2002;
        public static final int TYPE_POINTER = 2018;
        public static final int TYPE_PRIORITY_PHONE = 2007;
        public static final int TYPE_PRIVATE_PRESENTATION = 2030;
        public static final int TYPE_SEARCH_BAR = 2001;
        public static final int TYPE_SECURE_SYSTEM_OVERLAY = 2015;
        public static final int TYPE_STATUS_BAR = 2000;
        public static final int TYPE_STATUS_BAR_PANEL = 2014;
        public static final int TYPE_STATUS_BAR_SUB_PANEL = 2017;
        public static final int TYPE_SYSTEM_ALERT = 2003;
        public static final int TYPE_SYSTEM_DIALOG = 2008;
        public static final int TYPE_SYSTEM_ERROR = 2010;
        public static final int TYPE_SYSTEM_OVERLAY = 2006;
        public static final int TYPE_TOAST = 2005;
        public static final int TYPE_UNIVERSE_BACKGROUND = 2025;
        public static final int TYPE_VOICE_INTERACTION = 2031;
        public static final int TYPE_VOLUME_OVERLAY = 2020;
        public static final int TYPE_WALLPAPER = 2013;
        public static final int USER_ACTIVITY_TIMEOUT_CHANGED = 262144;
        public float alpha;
        public float buttonBrightness;
        public float dimAmount;
        @ExportedProperty(flagMapping = {@FlagToString(equals = 1, mask = 1, name = "FLAG_ALLOW_LOCK_WHILE_SCREEN_ON"), @FlagToString(equals = 2, mask = 2, name = "FLAG_DIM_BEHIND"), @FlagToString(equals = 4, mask = 4, name = "FLAG_BLUR_BEHIND"), @FlagToString(equals = 8, mask = 8, name = "FLAG_NOT_FOCUSABLE"), @FlagToString(equals = 16, mask = 16, name = "FLAG_NOT_TOUCHABLE"), @FlagToString(equals = 32, mask = 32, name = "FLAG_NOT_TOUCH_MODAL"), @FlagToString(equals = 64, mask = 64, name = "FLAG_TOUCHABLE_WHEN_WAKING"), @FlagToString(equals = 128, mask = 128, name = "FLAG_KEEP_SCREEN_ON"), @FlagToString(equals = 256, mask = 256, name = "FLAG_LAYOUT_IN_SCREEN"), @FlagToString(equals = 512, mask = 512, name = "FLAG_LAYOUT_NO_LIMITS"), @FlagToString(equals = 1024, mask = 1024, name = "FLAG_FULLSCREEN"), @FlagToString(equals = 2048, mask = 2048, name = "FLAG_FORCE_NOT_FULLSCREEN"), @FlagToString(equals = 4096, mask = 4096, name = "FLAG_DITHER"), @FlagToString(equals = 8192, mask = 8192, name = "FLAG_SECURE"), @FlagToString(equals = 16384, mask = 16384, name = "FLAG_SCALED"), @FlagToString(equals = 32768, mask = 32768, name = "FLAG_IGNORE_CHEEK_PRESSES"), @FlagToString(equals = 65536, mask = 65536, name = "FLAG_LAYOUT_INSET_DECOR"), @FlagToString(equals = 131072, mask = 131072, name = "FLAG_ALT_FOCUSABLE_IM"), @FlagToString(equals = 262144, mask = 262144, name = "FLAG_WATCH_OUTSIDE_TOUCH"), @FlagToString(equals = 524288, mask = 524288, name = "FLAG_SHOW_WHEN_LOCKED"), @FlagToString(equals = 1048576, mask = 1048576, name = "FLAG_SHOW_WALLPAPER"), @FlagToString(equals = 2097152, mask = 2097152, name = "FLAG_TURN_SCREEN_ON"), @FlagToString(equals = 4194304, mask = 4194304, name = "FLAG_DISMISS_KEYGUARD"), @FlagToString(equals = 8388608, mask = 8388608, name = "FLAG_SPLIT_TOUCH"), @FlagToString(equals = 16777216, mask = 16777216, name = "FLAG_HARDWARE_ACCELERATED"), @FlagToString(equals = 268435456, mask = 268435456, name = "FLAG_LOCAL_FOCUS_MODE"), @FlagToString(equals = 67108864, mask = 67108864, name = "FLAG_TRANSLUCENT_STATUS"), @FlagToString(equals = 134217728, mask = 134217728, name = "FLAG_TRANSLUCENT_NAVIGATION"), @FlagToString(equals = Integer.MIN_VALUE, mask = Integer.MIN_VALUE, name = "FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS")}, formatToHexString = true)
        public int flags;
        public int format;
        public int gravity;
        public boolean hasSystemUiListeners;
        public float horizontalMargin;
        @ExportedProperty
        public float horizontalWeight;
        public int inputFeatures;
        private int[] mCompatibilityParamsBackup;
        private CharSequence mTitle;
        @Deprecated
        public int memoryType;
        public int needsMenuKey;
        public String packageName;
        public float preferredRefreshRate;
        public int privateFlags;
        public int rotationAnimation;
        public float screenBrightness;
        public int screenOrientation;
        public int softInputMode;
        public int subtreeSystemUiVisibility;
        public final Rect surfaceInsets;
        public int systemUiVisibility;
        public IBinder token;
        @ExportedProperty(mapping = {@IntToString(from = 1, to = "TYPE_BASE_APPLICATION"), @IntToString(from = 2, to = "TYPE_APPLICATION"), @IntToString(from = 3, to = "TYPE_APPLICATION_STARTING"), @IntToString(from = 1000, to = "TYPE_APPLICATION_PANEL"), @IntToString(from = 1001, to = "TYPE_APPLICATION_MEDIA"), @IntToString(from = 1002, to = "TYPE_APPLICATION_SUB_PANEL"), @IntToString(from = 1003, to = "TYPE_APPLICATION_ATTACHED_DIALOG"), @IntToString(from = 1004, to = "TYPE_APPLICATION_MEDIA_OVERLAY"), @IntToString(from = 2000, to = "TYPE_STATUS_BAR"), @IntToString(from = 2001, to = "TYPE_SEARCH_BAR"), @IntToString(from = 2002, to = "TYPE_PHONE"), @IntToString(from = 2003, to = "TYPE_SYSTEM_ALERT"), @IntToString(from = 2005, to = "TYPE_TOAST"), @IntToString(from = 2006, to = "TYPE_SYSTEM_OVERLAY"), @IntToString(from = 2007, to = "TYPE_PRIORITY_PHONE"), @IntToString(from = 2008, to = "TYPE_SYSTEM_DIALOG"), @IntToString(from = 2009, to = "TYPE_KEYGUARD_DIALOG"), @IntToString(from = 2010, to = "TYPE_SYSTEM_ERROR"), @IntToString(from = 2011, to = "TYPE_INPUT_METHOD"), @IntToString(from = 2012, to = "TYPE_INPUT_METHOD_DIALOG"), @IntToString(from = 2013, to = "TYPE_WALLPAPER"), @IntToString(from = 2014, to = "TYPE_STATUS_BAR_PANEL"), @IntToString(from = 2015, to = "TYPE_SECURE_SYSTEM_OVERLAY"), @IntToString(from = 2016, to = "TYPE_DRAG"), @IntToString(from = 2017, to = "TYPE_STATUS_BAR_SUB_PANEL"), @IntToString(from = 2018, to = "TYPE_POINTER"), @IntToString(from = 2019, to = "TYPE_NAVIGATION_BAR"), @IntToString(from = 2020, to = "TYPE_VOLUME_OVERLAY"), @IntToString(from = 2021, to = "TYPE_BOOT_PROGRESS"), @IntToString(from = 2022, to = "TYPE_HIDDEN_NAV_CONSUMER"), @IntToString(from = 2023, to = "TYPE_DREAM"), @IntToString(from = 2024, to = "TYPE_NAVIGATION_BAR_PANEL"), @IntToString(from = 2026, to = "TYPE_DISPLAY_OVERLAY"), @IntToString(from = 2027, to = "TYPE_MAGNIFICATION_OVERLAY"), @IntToString(from = 2030, to = "TYPE_PRIVATE_PRESENTATION"), @IntToString(from = 2031, to = "TYPE_VOICE_INTERACTION")})
        public int type;
        public long userActivityTimeout;
        public float verticalMargin;
        @ExportedProperty
        public float verticalWeight;
        public int windowAnimations;
        @ExportedProperty
        public int x;
        @ExportedProperty
        public int y;

        public static boolean mayUseInputMethod(int flags) {
            switch (131080 & flags) {
                case SOFT_INPUT_STATE_UNSPECIFIED /*0*/:
                case 131080:
                    return true;
                default:
                    return false;
            }
        }

        public LayoutParams() {
            super((int) EVERYTHING_CHANGED, (int) EVERYTHING_CHANGED);
            this.needsMenuKey = SOFT_INPUT_STATE_UNSPECIFIED;
            this.surfaceInsets = new Rect();
            this.alpha = BRIGHTNESS_OVERRIDE_FULL;
            this.dimAmount = BRIGHTNESS_OVERRIDE_FULL;
            this.screenBrightness = BRIGHTNESS_OVERRIDE_NONE;
            this.buttonBrightness = BRIGHTNESS_OVERRIDE_NONE;
            this.rotationAnimation = SOFT_INPUT_STATE_UNSPECIFIED;
            this.token = null;
            this.packageName = null;
            this.screenOrientation = EVERYTHING_CHANGED;
            this.userActivityTimeout = -1;
            this.mCompatibilityParamsBackup = null;
            this.mTitle = ProxyInfo.LOCAL_EXCL_LIST;
            this.type = TYPE_CHANGED;
            this.format = EVERYTHING_CHANGED;
        }

        public LayoutParams(int _type) {
            super((int) EVERYTHING_CHANGED, (int) EVERYTHING_CHANGED);
            this.needsMenuKey = SOFT_INPUT_STATE_UNSPECIFIED;
            this.surfaceInsets = new Rect();
            this.alpha = BRIGHTNESS_OVERRIDE_FULL;
            this.dimAmount = BRIGHTNESS_OVERRIDE_FULL;
            this.screenBrightness = BRIGHTNESS_OVERRIDE_NONE;
            this.buttonBrightness = BRIGHTNESS_OVERRIDE_NONE;
            this.rotationAnimation = SOFT_INPUT_STATE_UNSPECIFIED;
            this.token = null;
            this.packageName = null;
            this.screenOrientation = EVERYTHING_CHANGED;
            this.userActivityTimeout = -1;
            this.mCompatibilityParamsBackup = null;
            this.mTitle = ProxyInfo.LOCAL_EXCL_LIST;
            this.type = _type;
            this.format = EVERYTHING_CHANGED;
        }

        public LayoutParams(int _type, int _flags) {
            super((int) EVERYTHING_CHANGED, (int) EVERYTHING_CHANGED);
            this.needsMenuKey = SOFT_INPUT_STATE_UNSPECIFIED;
            this.surfaceInsets = new Rect();
            this.alpha = BRIGHTNESS_OVERRIDE_FULL;
            this.dimAmount = BRIGHTNESS_OVERRIDE_FULL;
            this.screenBrightness = BRIGHTNESS_OVERRIDE_NONE;
            this.buttonBrightness = BRIGHTNESS_OVERRIDE_NONE;
            this.rotationAnimation = SOFT_INPUT_STATE_UNSPECIFIED;
            this.token = null;
            this.packageName = null;
            this.screenOrientation = EVERYTHING_CHANGED;
            this.userActivityTimeout = -1;
            this.mCompatibilityParamsBackup = null;
            this.mTitle = ProxyInfo.LOCAL_EXCL_LIST;
            this.type = _type;
            this.flags = _flags;
            this.format = EVERYTHING_CHANGED;
        }

        public LayoutParams(int _type, int _flags, int _format) {
            super((int) EVERYTHING_CHANGED, (int) EVERYTHING_CHANGED);
            this.needsMenuKey = SOFT_INPUT_STATE_UNSPECIFIED;
            this.surfaceInsets = new Rect();
            this.alpha = BRIGHTNESS_OVERRIDE_FULL;
            this.dimAmount = BRIGHTNESS_OVERRIDE_FULL;
            this.screenBrightness = BRIGHTNESS_OVERRIDE_NONE;
            this.buttonBrightness = BRIGHTNESS_OVERRIDE_NONE;
            this.rotationAnimation = SOFT_INPUT_STATE_UNSPECIFIED;
            this.token = null;
            this.packageName = null;
            this.screenOrientation = EVERYTHING_CHANGED;
            this.userActivityTimeout = -1;
            this.mCompatibilityParamsBackup = null;
            this.mTitle = ProxyInfo.LOCAL_EXCL_LIST;
            this.type = _type;
            this.flags = _flags;
            this.format = _format;
        }

        public LayoutParams(int w, int h, int _type, int _flags, int _format) {
            super(w, h);
            this.needsMenuKey = SOFT_INPUT_STATE_UNSPECIFIED;
            this.surfaceInsets = new Rect();
            this.alpha = BRIGHTNESS_OVERRIDE_FULL;
            this.dimAmount = BRIGHTNESS_OVERRIDE_FULL;
            this.screenBrightness = BRIGHTNESS_OVERRIDE_NONE;
            this.buttonBrightness = BRIGHTNESS_OVERRIDE_NONE;
            this.rotationAnimation = SOFT_INPUT_STATE_UNSPECIFIED;
            this.token = null;
            this.packageName = null;
            this.screenOrientation = EVERYTHING_CHANGED;
            this.userActivityTimeout = -1;
            this.mCompatibilityParamsBackup = null;
            this.mTitle = ProxyInfo.LOCAL_EXCL_LIST;
            this.type = _type;
            this.flags = _flags;
            this.format = _format;
        }

        public LayoutParams(int w, int h, int xpos, int ypos, int _type, int _flags, int _format) {
            super(w, h);
            this.needsMenuKey = SOFT_INPUT_STATE_UNSPECIFIED;
            this.surfaceInsets = new Rect();
            this.alpha = BRIGHTNESS_OVERRIDE_FULL;
            this.dimAmount = BRIGHTNESS_OVERRIDE_FULL;
            this.screenBrightness = BRIGHTNESS_OVERRIDE_NONE;
            this.buttonBrightness = BRIGHTNESS_OVERRIDE_NONE;
            this.rotationAnimation = SOFT_INPUT_STATE_UNSPECIFIED;
            this.token = null;
            this.packageName = null;
            this.screenOrientation = EVERYTHING_CHANGED;
            this.userActivityTimeout = -1;
            this.mCompatibilityParamsBackup = null;
            this.mTitle = ProxyInfo.LOCAL_EXCL_LIST;
            this.x = xpos;
            this.y = ypos;
            this.type = _type;
            this.flags = _flags;
            this.format = _format;
        }

        public final void setTitle(CharSequence title) {
            if (title == null) {
                title = ProxyInfo.LOCAL_EXCL_LIST;
            }
            this.mTitle = TextUtils.stringOrSpannedString(title);
        }

        public final CharSequence getTitle() {
            return this.mTitle;
        }

        public final void setUserActivityTimeout(long timeout) {
            this.userActivityTimeout = timeout;
        }

        public final long getUserActivityTimeout() {
            return this.userActivityTimeout;
        }

        public int describeContents() {
            return SOFT_INPUT_STATE_UNSPECIFIED;
        }

        public void writeToParcel(Parcel out, int parcelableFlags) {
            out.writeInt(this.width);
            out.writeInt(this.height);
            out.writeInt(this.x);
            out.writeInt(this.y);
            out.writeInt(this.type);
            out.writeInt(this.flags);
            out.writeInt(this.privateFlags);
            out.writeInt(this.softInputMode);
            out.writeInt(this.gravity);
            out.writeFloat(this.horizontalMargin);
            out.writeFloat(this.verticalMargin);
            out.writeInt(this.format);
            out.writeInt(this.windowAnimations);
            out.writeFloat(this.alpha);
            out.writeFloat(this.dimAmount);
            out.writeFloat(this.screenBrightness);
            out.writeFloat(this.buttonBrightness);
            out.writeInt(this.rotationAnimation);
            out.writeStrongBinder(this.token);
            out.writeString(this.packageName);
            TextUtils.writeToParcel(this.mTitle, out, parcelableFlags);
            out.writeInt(this.screenOrientation);
            out.writeFloat(this.preferredRefreshRate);
            out.writeInt(this.systemUiVisibility);
            out.writeInt(this.subtreeSystemUiVisibility);
            out.writeInt(this.hasSystemUiListeners ? TYPE_BASE_APPLICATION : SOFT_INPUT_STATE_UNSPECIFIED);
            out.writeInt(this.inputFeatures);
            out.writeLong(this.userActivityTimeout);
            out.writeInt(this.surfaceInsets.left);
            out.writeInt(this.surfaceInsets.top);
            out.writeInt(this.surfaceInsets.right);
            out.writeInt(this.surfaceInsets.bottom);
            out.writeInt(this.needsMenuKey);
        }

        static {
            CREATOR = new Creator<LayoutParams>() {
                public LayoutParams createFromParcel(Parcel in) {
                    return new LayoutParams(in);
                }

                public LayoutParams[] newArray(int size) {
                    return new LayoutParams[size];
                }
            };
        }

        public LayoutParams(Parcel in) {
            this.needsMenuKey = SOFT_INPUT_STATE_UNSPECIFIED;
            this.surfaceInsets = new Rect();
            this.alpha = BRIGHTNESS_OVERRIDE_FULL;
            this.dimAmount = BRIGHTNESS_OVERRIDE_FULL;
            this.screenBrightness = BRIGHTNESS_OVERRIDE_NONE;
            this.buttonBrightness = BRIGHTNESS_OVERRIDE_NONE;
            this.rotationAnimation = SOFT_INPUT_STATE_UNSPECIFIED;
            this.token = null;
            this.packageName = null;
            this.screenOrientation = EVERYTHING_CHANGED;
            this.userActivityTimeout = -1;
            this.mCompatibilityParamsBackup = null;
            this.mTitle = ProxyInfo.LOCAL_EXCL_LIST;
            this.width = in.readInt();
            this.height = in.readInt();
            this.x = in.readInt();
            this.y = in.readInt();
            this.type = in.readInt();
            this.flags = in.readInt();
            this.privateFlags = in.readInt();
            this.softInputMode = in.readInt();
            this.gravity = in.readInt();
            this.horizontalMargin = in.readFloat();
            this.verticalMargin = in.readFloat();
            this.format = in.readInt();
            this.windowAnimations = in.readInt();
            this.alpha = in.readFloat();
            this.dimAmount = in.readFloat();
            this.screenBrightness = in.readFloat();
            this.buttonBrightness = in.readFloat();
            this.rotationAnimation = in.readInt();
            this.token = in.readStrongBinder();
            this.packageName = in.readString();
            this.mTitle = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
            this.screenOrientation = in.readInt();
            this.preferredRefreshRate = in.readFloat();
            this.systemUiVisibility = in.readInt();
            this.subtreeSystemUiVisibility = in.readInt();
            this.hasSystemUiListeners = in.readInt() != 0;
            this.inputFeatures = in.readInt();
            this.userActivityTimeout = in.readLong();
            this.surfaceInsets.left = in.readInt();
            this.surfaceInsets.top = in.readInt();
            this.surfaceInsets.right = in.readInt();
            this.surfaceInsets.bottom = in.readInt();
            this.needsMenuKey = in.readInt();
        }

        public final int copyFrom(LayoutParams o) {
            int changes = SOFT_INPUT_STATE_UNSPECIFIED;
            if (this.width != o.width) {
                this.width = o.width;
                changes = SOFT_INPUT_STATE_UNSPECIFIED | TYPE_BASE_APPLICATION;
            }
            if (this.height != o.height) {
                this.height = o.height;
                changes |= TYPE_BASE_APPLICATION;
            }
            if (this.x != o.x) {
                this.x = o.x;
                changes |= TYPE_BASE_APPLICATION;
            }
            if (this.y != o.y) {
                this.y = o.y;
                changes |= TYPE_BASE_APPLICATION;
            }
            if (this.horizontalWeight != o.horizontalWeight) {
                this.horizontalWeight = o.horizontalWeight;
                changes |= TYPE_BASE_APPLICATION;
            }
            if (this.verticalWeight != o.verticalWeight) {
                this.verticalWeight = o.verticalWeight;
                changes |= TYPE_BASE_APPLICATION;
            }
            if (this.horizontalMargin != o.horizontalMargin) {
                this.horizontalMargin = o.horizontalMargin;
                changes |= TYPE_BASE_APPLICATION;
            }
            if (this.verticalMargin != o.verticalMargin) {
                this.verticalMargin = o.verticalMargin;
                changes |= TYPE_BASE_APPLICATION;
            }
            if (this.type != o.type) {
                this.type = o.type;
                changes |= TYPE_CHANGED;
            }
            if (this.flags != o.flags) {
                if ((201326592 & (this.flags ^ o.flags)) != 0) {
                    changes |= TRANSLUCENT_FLAGS_CHANGED;
                }
                this.flags = o.flags;
                changes |= SOFT_INPUT_STATE_VISIBLE;
            }
            if (this.privateFlags != o.privateFlags) {
                this.privateFlags = o.privateFlags;
                changes |= PRIVATE_FLAGS_CHANGED;
            }
            if (this.softInputMode != o.softInputMode) {
                this.softInputMode = o.softInputMode;
                changes |= SOFT_INPUT_MODE_CHANGED;
            }
            if (this.gravity != o.gravity) {
                this.gravity = o.gravity;
                changes |= TYPE_BASE_APPLICATION;
            }
            if (this.format != o.format) {
                this.format = o.format;
                changes |= FORMAT_CHANGED;
            }
            if (this.windowAnimations != o.windowAnimations) {
                this.windowAnimations = o.windowAnimations;
                changes |= SOFT_INPUT_ADJUST_RESIZE;
            }
            if (this.token == null) {
                this.token = o.token;
            }
            if (this.packageName == null) {
                this.packageName = o.packageName;
            }
            if (!this.mTitle.equals(o.mTitle)) {
                this.mTitle = o.mTitle;
                changes |= TITLE_CHANGED;
            }
            if (this.alpha != o.alpha) {
                this.alpha = o.alpha;
                changes |= PRIVATE_FLAG_COMPATIBLE_WINDOW;
            }
            if (this.dimAmount != o.dimAmount) {
                this.dimAmount = o.dimAmount;
                changes |= SOFT_INPUT_ADJUST_PAN;
            }
            if (this.screenBrightness != o.screenBrightness) {
                this.screenBrightness = o.screenBrightness;
                changes |= SCREEN_BRIGHTNESS_CHANGED;
            }
            if (this.buttonBrightness != o.buttonBrightness) {
                this.buttonBrightness = o.buttonBrightness;
                changes |= FLAG_SECURE;
            }
            if (this.rotationAnimation != o.rotationAnimation) {
                this.rotationAnimation = o.rotationAnimation;
                changes |= ROTATION_ANIMATION_CHANGED;
            }
            if (this.screenOrientation != o.screenOrientation) {
                this.screenOrientation = o.screenOrientation;
                changes |= SCREEN_ORIENTATION_CHANGED;
            }
            if (this.preferredRefreshRate != o.preferredRefreshRate) {
                this.preferredRefreshRate = o.preferredRefreshRate;
                changes |= PREFERRED_REFRESH_RATE_CHANGED;
            }
            if (!(this.systemUiVisibility == o.systemUiVisibility && this.subtreeSystemUiVisibility == o.subtreeSystemUiVisibility)) {
                this.systemUiVisibility = o.systemUiVisibility;
                this.subtreeSystemUiVisibility = o.subtreeSystemUiVisibility;
                changes |= SYSTEM_UI_VISIBILITY_CHANGED;
            }
            if (this.hasSystemUiListeners != o.hasSystemUiListeners) {
                this.hasSystemUiListeners = o.hasSystemUiListeners;
                changes |= SYSTEM_UI_LISTENER_CHANGED;
            }
            if (this.inputFeatures != o.inputFeatures) {
                this.inputFeatures = o.inputFeatures;
                changes |= INPUT_FEATURES_CHANGED;
            }
            if (this.userActivityTimeout != o.userActivityTimeout) {
                this.userActivityTimeout = o.userActivityTimeout;
                changes |= USER_ACTIVITY_TIMEOUT_CHANGED;
            }
            if (!this.surfaceInsets.equals(o.surfaceInsets)) {
                this.surfaceInsets.set(o.surfaceInsets);
                changes |= SURFACE_INSETS_CHANGED;
            }
            if (this.needsMenuKey == o.needsMenuKey) {
                return changes;
            }
            this.needsMenuKey = o.needsMenuKey;
            return changes | NEEDS_MENU_KEY_CHANGED;
        }

        public String debug(String output) {
            Log.d("Debug", output + "Contents of " + this + ":");
            Log.d("Debug", super.debug(ProxyInfo.LOCAL_EXCL_LIST));
            Log.d("Debug", ProxyInfo.LOCAL_EXCL_LIST);
            Log.d("Debug", "WindowManager.LayoutParams={title=" + this.mTitle + "}");
            return ProxyInfo.LOCAL_EXCL_LIST;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(SOFT_INPUT_IS_FORWARD_NAVIGATION);
            sb.append("WM.LayoutParams{");
            sb.append("(");
            sb.append(this.x);
            sb.append(PhoneNumberUtils.PAUSE);
            sb.append(this.y);
            sb.append(")(");
            Object valueOf = this.width == EVERYTHING_CHANGED ? "fill" : this.width == -2 ? "wrap" : Integer.valueOf(this.width);
            sb.append(valueOf);
            sb.append('x');
            valueOf = this.height == EVERYTHING_CHANGED ? "fill" : this.height == -2 ? "wrap" : Integer.valueOf(this.height);
            sb.append(valueOf);
            sb.append(")");
            if (this.horizontalMargin != BRIGHTNESS_OVERRIDE_OFF) {
                sb.append(" hm=");
                sb.append(this.horizontalMargin);
            }
            if (this.verticalMargin != BRIGHTNESS_OVERRIDE_OFF) {
                sb.append(" vm=");
                sb.append(this.verticalMargin);
            }
            if (this.gravity != 0) {
                sb.append(" gr=#");
                sb.append(Integer.toHexString(this.gravity));
            }
            if (this.softInputMode != 0) {
                sb.append(" sim=#");
                sb.append(Integer.toHexString(this.softInputMode));
            }
            sb.append(" ty=");
            sb.append(this.type);
            sb.append(" fl=#");
            sb.append(Integer.toHexString(this.flags));
            if (this.privateFlags != 0) {
                if ((this.privateFlags & PRIVATE_FLAG_COMPATIBLE_WINDOW) != 0) {
                    sb.append(" compatible=true");
                }
                sb.append(" pfl=0x").append(Integer.toHexString(this.privateFlags));
            }
            if (this.format != EVERYTHING_CHANGED) {
                sb.append(" fmt=");
                sb.append(this.format);
            }
            if (this.windowAnimations != 0) {
                sb.append(" wanim=0x");
                sb.append(Integer.toHexString(this.windowAnimations));
            }
            if (this.screenOrientation != EVERYTHING_CHANGED) {
                sb.append(" or=");
                sb.append(this.screenOrientation);
            }
            if (this.alpha != BRIGHTNESS_OVERRIDE_FULL) {
                sb.append(" alpha=");
                sb.append(this.alpha);
            }
            if (this.screenBrightness != BRIGHTNESS_OVERRIDE_NONE) {
                sb.append(" sbrt=");
                sb.append(this.screenBrightness);
            }
            if (this.buttonBrightness != BRIGHTNESS_OVERRIDE_NONE) {
                sb.append(" bbrt=");
                sb.append(this.buttonBrightness);
            }
            if (this.rotationAnimation != 0) {
                sb.append(" rotAnim=");
                sb.append(this.rotationAnimation);
            }
            if (this.preferredRefreshRate != BRIGHTNESS_OVERRIDE_OFF) {
                sb.append(" preferredRefreshRate=");
                sb.append(this.preferredRefreshRate);
            }
            if (this.systemUiVisibility != 0) {
                sb.append(" sysui=0x");
                sb.append(Integer.toHexString(this.systemUiVisibility));
            }
            if (this.subtreeSystemUiVisibility != 0) {
                sb.append(" vsysui=0x");
                sb.append(Integer.toHexString(this.subtreeSystemUiVisibility));
            }
            if (this.hasSystemUiListeners) {
                sb.append(" sysuil=");
                sb.append(this.hasSystemUiListeners);
            }
            if (this.inputFeatures != 0) {
                sb.append(" if=0x").append(Integer.toHexString(this.inputFeatures));
            }
            if (this.userActivityTimeout >= 0) {
                sb.append(" userActivityTimeout=").append(this.userActivityTimeout);
            }
            if (!this.surfaceInsets.equals(Insets.NONE)) {
                sb.append(" surfaceInsets=").append(this.surfaceInsets);
            }
            if (this.needsMenuKey != 0) {
                sb.append(" needsMenuKey=");
                sb.append(this.needsMenuKey);
            }
            sb.append('}');
            return sb.toString();
        }

        public void scale(float scale) {
            this.x = (int) ((((float) this.x) * scale) + 0.5f);
            this.y = (int) ((((float) this.y) * scale) + 0.5f);
            if (this.width > 0) {
                this.width = (int) ((((float) this.width) * scale) + 0.5f);
            }
            if (this.height > 0) {
                this.height = (int) ((((float) this.height) * scale) + 0.5f);
            }
        }

        void backup() {
            int[] backup = this.mCompatibilityParamsBackup;
            if (backup == null) {
                backup = new int[SOFT_INPUT_STATE_VISIBLE];
                this.mCompatibilityParamsBackup = backup;
            }
            backup[SOFT_INPUT_STATE_UNSPECIFIED] = this.x;
            backup[TYPE_BASE_APPLICATION] = this.y;
            backup[TYPE_CHANGED] = this.width;
            backup[TYPE_APPLICATION_STARTING] = this.height;
        }

        void restore() {
            int[] backup = this.mCompatibilityParamsBackup;
            if (backup != null) {
                this.x = backup[SOFT_INPUT_STATE_UNSPECIFIED];
                this.y = backup[TYPE_BASE_APPLICATION];
                this.width = backup[TYPE_CHANGED];
                this.height = backup[TYPE_APPLICATION_STARTING];
            }
        }
    }

    Display getDefaultDisplay();

    void removeViewImmediate(View view);
}
