package android.content.res;

import android.hardware.Camera.Parameters;
import android.nfc.tech.MifareClassic;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.security.KeyChain;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.EditorInfo;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public final class Configuration implements Parcelable, Comparable<Configuration> {
    public static final Creator<Configuration> CREATOR;
    public static final int DENSITY_DPI_ANY = 65534;
    public static final int DENSITY_DPI_NONE = 65535;
    public static final int DENSITY_DPI_UNDEFINED = 0;
    public static final Configuration EMPTY;
    public static final int HARDKEYBOARDHIDDEN_NO = 1;
    public static final int HARDKEYBOARDHIDDEN_UNDEFINED = 0;
    public static final int HARDKEYBOARDHIDDEN_YES = 2;
    public static final int KEYBOARDHIDDEN_NO = 1;
    public static final int KEYBOARDHIDDEN_SOFT = 3;
    public static final int KEYBOARDHIDDEN_UNDEFINED = 0;
    public static final int KEYBOARDHIDDEN_YES = 2;
    public static final int KEYBOARD_12KEY = 3;
    public static final int KEYBOARD_NOKEYS = 1;
    public static final int KEYBOARD_QWERTY = 2;
    public static final int KEYBOARD_UNDEFINED = 0;
    public static final int MNC_ZERO = 65535;
    public static final int NATIVE_CONFIG_DENSITY = 256;
    public static final int NATIVE_CONFIG_KEYBOARD = 16;
    public static final int NATIVE_CONFIG_KEYBOARD_HIDDEN = 32;
    public static final int NATIVE_CONFIG_LAYOUTDIR = 16384;
    public static final int NATIVE_CONFIG_LOCALE = 4;
    public static final int NATIVE_CONFIG_MCC = 1;
    public static final int NATIVE_CONFIG_MNC = 2;
    public static final int NATIVE_CONFIG_NAVIGATION = 64;
    public static final int NATIVE_CONFIG_ORIENTATION = 128;
    public static final int NATIVE_CONFIG_SCREEN_LAYOUT = 2048;
    public static final int NATIVE_CONFIG_SCREEN_SIZE = 512;
    public static final int NATIVE_CONFIG_SMALLEST_SCREEN_SIZE = 8192;
    public static final int NATIVE_CONFIG_TOUCHSCREEN = 8;
    public static final int NATIVE_CONFIG_UI_MODE = 4096;
    public static final int NATIVE_CONFIG_VERSION = 1024;
    public static final int NAVIGATIONHIDDEN_NO = 1;
    public static final int NAVIGATIONHIDDEN_UNDEFINED = 0;
    public static final int NAVIGATIONHIDDEN_YES = 2;
    public static final int NAVIGATION_DPAD = 2;
    public static final int NAVIGATION_NONAV = 1;
    public static final int NAVIGATION_TRACKBALL = 3;
    public static final int NAVIGATION_UNDEFINED = 0;
    public static final int NAVIGATION_WHEEL = 4;
    public static final int ORIENTATION_LANDSCAPE = 2;
    public static final int ORIENTATION_PORTRAIT = 1;
    @Deprecated
    public static final int ORIENTATION_SQUARE = 3;
    public static final int ORIENTATION_UNDEFINED = 0;
    public static final int SCREENLAYOUT_COMPAT_NEEDED = 268435456;
    public static final int SCREENLAYOUT_LAYOUTDIR_LTR = 64;
    public static final int SCREENLAYOUT_LAYOUTDIR_MASK = 192;
    public static final int SCREENLAYOUT_LAYOUTDIR_RTL = 128;
    public static final int SCREENLAYOUT_LAYOUTDIR_SHIFT = 6;
    public static final int SCREENLAYOUT_LAYOUTDIR_UNDEFINED = 0;
    public static final int SCREENLAYOUT_LONG_MASK = 48;
    public static final int SCREENLAYOUT_LONG_NO = 16;
    public static final int SCREENLAYOUT_LONG_UNDEFINED = 0;
    public static final int SCREENLAYOUT_LONG_YES = 32;
    public static final int SCREENLAYOUT_SIZE_LARGE = 3;
    public static final int SCREENLAYOUT_SIZE_MASK = 15;
    public static final int SCREENLAYOUT_SIZE_NORMAL = 2;
    public static final int SCREENLAYOUT_SIZE_SMALL = 1;
    public static final int SCREENLAYOUT_SIZE_UNDEFINED = 0;
    public static final int SCREENLAYOUT_SIZE_XLARGE = 4;
    public static final int SCREENLAYOUT_UNDEFINED = 0;
    public static final int SCREEN_HEIGHT_DP_UNDEFINED = 0;
    public static final int SCREEN_WIDTH_DP_UNDEFINED = 0;
    public static final int SMALLEST_SCREEN_WIDTH_DP_UNDEFINED = 0;
    public static final int TOUCHSCREEN_FINGER = 3;
    public static final int TOUCHSCREEN_NOTOUCH = 1;
    @Deprecated
    public static final int TOUCHSCREEN_STYLUS = 2;
    public static final int TOUCHSCREEN_UNDEFINED = 0;
    public static final int UI_MODE_NIGHT_MASK = 48;
    public static final int UI_MODE_NIGHT_NO = 16;
    public static final int UI_MODE_NIGHT_UNDEFINED = 0;
    public static final int UI_MODE_NIGHT_YES = 32;
    public static final int UI_MODE_TYPE_APPLIANCE = 5;
    public static final int UI_MODE_TYPE_CAR = 3;
    public static final int UI_MODE_TYPE_DESK = 2;
    public static final int UI_MODE_TYPE_MASK = 15;
    public static final int UI_MODE_TYPE_NORMAL = 1;
    public static final int UI_MODE_TYPE_TELEVISION = 4;
    public static final int UI_MODE_TYPE_UNDEFINED = 0;
    public static final int UI_MODE_TYPE_WATCH = 6;
    private static final String XML_ATTR_DENSITY = "density";
    private static final String XML_ATTR_FONT_SCALE = "fs";
    private static final String XML_ATTR_HARD_KEYBOARD_HIDDEN = "hardKeyHid";
    private static final String XML_ATTR_KEYBOARD = "key";
    private static final String XML_ATTR_KEYBOARD_HIDDEN = "keyHid";
    private static final String XML_ATTR_LOCALE = "locale";
    private static final String XML_ATTR_MCC = "mcc";
    private static final String XML_ATTR_MNC = "mnc";
    private static final String XML_ATTR_NAVIGATION = "nav";
    private static final String XML_ATTR_NAVIGATION_HIDDEN = "navHid";
    private static final String XML_ATTR_ORIENTATION = "ori";
    private static final String XML_ATTR_SCREEN_HEIGHT = "height";
    private static final String XML_ATTR_SCREEN_LAYOUT = "scrLay";
    private static final String XML_ATTR_SCREEN_WIDTH = "width";
    private static final String XML_ATTR_SMALLEST_WIDTH = "sw";
    private static final String XML_ATTR_TOUCHSCREEN = "touch";
    private static final String XML_ATTR_UI_MODE = "ui";
    public int compatScreenHeightDp;
    public int compatScreenWidthDp;
    public int compatSmallestScreenWidthDp;
    public int densityDpi;
    public float fontScale;
    public int hardKeyboardHidden;
    public int keyboard;
    public int keyboardHidden;
    public Locale locale;
    public int mcc;
    public int mnc;
    public int navigation;
    public int navigationHidden;
    public int orientation;
    public int screenHeightDp;
    public int screenLayout;
    public int screenWidthDp;
    public int seq;
    public int smallestScreenWidthDp;
    public int touchscreen;
    public int uiMode;
    public boolean userSetLocale;

    static {
        EMPTY = new Configuration();
        CREATOR = new Creator<Configuration>() {
            public Configuration createFromParcel(Parcel source) {
                return new Configuration(null);
            }

            public Configuration[] newArray(int size) {
                return new Configuration[size];
            }
        };
    }

    public static int resetScreenLayout(int curLayout) {
        return (-268435520 & curLayout) | 36;
    }

    public static int reduceScreenLayout(int curLayout, int longSizeDp, int shortSizeDp) {
        int screenLayoutSize;
        boolean screenLayoutLong;
        boolean screenLayoutCompatNeeded;
        if (longSizeDp < 470) {
            screenLayoutSize = UI_MODE_TYPE_NORMAL;
            screenLayoutLong = false;
            screenLayoutCompatNeeded = false;
        } else {
            if (longSizeDp >= 960 && shortSizeDp >= 720) {
                screenLayoutSize = UI_MODE_TYPE_TELEVISION;
            } else if (longSizeDp < 640 || shortSizeDp < 480) {
                screenLayoutSize = UI_MODE_TYPE_DESK;
            } else {
                screenLayoutSize = UI_MODE_TYPE_CAR;
            }
            if (shortSizeDp > 321 || longSizeDp > 570) {
                screenLayoutCompatNeeded = true;
            } else {
                screenLayoutCompatNeeded = false;
            }
            if ((longSizeDp * UI_MODE_TYPE_CAR) / UI_MODE_TYPE_APPLIANCE >= shortSizeDp - 1) {
                screenLayoutLong = true;
            } else {
                screenLayoutLong = false;
            }
        }
        if (!screenLayoutLong) {
            curLayout = (curLayout & -49) | UI_MODE_NIGHT_NO;
        }
        if (screenLayoutCompatNeeded) {
            curLayout |= SCREENLAYOUT_COMPAT_NEEDED;
        }
        if (screenLayoutSize < (curLayout & UI_MODE_TYPE_MASK)) {
            return (curLayout & -16) | screenLayoutSize;
        }
        return curLayout;
    }

    public boolean isLayoutSizeAtLeast(int size) {
        int cur = this.screenLayout & UI_MODE_TYPE_MASK;
        if (cur != 0 && cur >= size) {
            return true;
        }
        return false;
    }

    public Configuration() {
        setToDefaults();
    }

    public Configuration(Configuration o) {
        setTo(o);
    }

    public void setTo(Configuration o) {
        this.fontScale = o.fontScale;
        this.mcc = o.mcc;
        this.mnc = o.mnc;
        if (o.locale != null) {
            this.locale = (Locale) o.locale.clone();
        }
        this.userSetLocale = o.userSetLocale;
        this.touchscreen = o.touchscreen;
        this.keyboard = o.keyboard;
        this.keyboardHidden = o.keyboardHidden;
        this.hardKeyboardHidden = o.hardKeyboardHidden;
        this.navigation = o.navigation;
        this.navigationHidden = o.navigationHidden;
        this.orientation = o.orientation;
        this.screenLayout = o.screenLayout;
        this.uiMode = o.uiMode;
        this.screenWidthDp = o.screenWidthDp;
        this.screenHeightDp = o.screenHeightDp;
        this.smallestScreenWidthDp = o.smallestScreenWidthDp;
        this.densityDpi = o.densityDpi;
        this.compatScreenWidthDp = o.compatScreenWidthDp;
        this.compatScreenHeightDp = o.compatScreenHeightDp;
        this.compatSmallestScreenWidthDp = o.compatSmallestScreenWidthDp;
        this.seq = o.seq;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(SCREENLAYOUT_LAYOUTDIR_RTL);
        sb.append("{");
        sb.append(this.fontScale);
        sb.append(" ");
        if (this.mcc != 0) {
            sb.append(this.mcc);
            sb.append(XML_ATTR_MCC);
        } else {
            sb.append("?mcc");
        }
        if (this.mnc != 0) {
            sb.append(this.mnc);
            sb.append(XML_ATTR_MNC);
        } else {
            sb.append("?mnc");
        }
        if (this.locale != null) {
            sb.append(" ");
            sb.append(this.locale);
        } else {
            sb.append(" ?locale");
        }
        int layoutDir = this.screenLayout & SCREENLAYOUT_LAYOUTDIR_MASK;
        switch (layoutDir) {
            case UI_MODE_TYPE_UNDEFINED /*0*/:
                sb.append(" ?layoutDir");
                break;
            case SCREENLAYOUT_LAYOUTDIR_LTR /*64*/:
                sb.append(" ldltr");
                break;
            case SCREENLAYOUT_LAYOUTDIR_RTL /*128*/:
                sb.append(" ldrtl");
                break;
            default:
                sb.append(" layoutDir=");
                sb.append(layoutDir >> UI_MODE_TYPE_WATCH);
                break;
        }
        if (this.smallestScreenWidthDp != 0) {
            sb.append(" sw");
            sb.append(this.smallestScreenWidthDp);
            sb.append("dp");
        } else {
            sb.append(" ?swdp");
        }
        if (this.screenWidthDp != 0) {
            sb.append(" w");
            sb.append(this.screenWidthDp);
            sb.append("dp");
        } else {
            sb.append(" ?wdp");
        }
        if (this.screenHeightDp != 0) {
            sb.append(" h");
            sb.append(this.screenHeightDp);
            sb.append("dp");
        } else {
            sb.append(" ?hdp");
        }
        if (this.densityDpi != 0) {
            sb.append(" ");
            sb.append(this.densityDpi);
            sb.append("dpi");
        } else {
            sb.append(" ?density");
        }
        switch (this.screenLayout & UI_MODE_TYPE_MASK) {
            case UI_MODE_TYPE_UNDEFINED /*0*/:
                sb.append(" ?lsize");
                break;
            case UI_MODE_TYPE_NORMAL /*1*/:
                sb.append(" smll");
                break;
            case UI_MODE_TYPE_DESK /*2*/:
                sb.append(" nrml");
                break;
            case UI_MODE_TYPE_CAR /*3*/:
                sb.append(" lrg");
                break;
            case UI_MODE_TYPE_TELEVISION /*4*/:
                sb.append(" xlrg");
                break;
            default:
                sb.append(" layoutSize=");
                sb.append(this.screenLayout & UI_MODE_TYPE_MASK);
                break;
        }
        switch (this.screenLayout & UI_MODE_NIGHT_MASK) {
            case UI_MODE_TYPE_UNDEFINED /*0*/:
                sb.append(" ?long");
                break;
            case UI_MODE_NIGHT_NO /*16*/:
                break;
            case UI_MODE_NIGHT_YES /*32*/:
                sb.append(" long");
                break;
            default:
                sb.append(" layoutLong=");
                sb.append(this.screenLayout & UI_MODE_NIGHT_MASK);
                break;
        }
        switch (this.orientation) {
            case UI_MODE_TYPE_UNDEFINED /*0*/:
                sb.append(" ?orien");
                break;
            case UI_MODE_TYPE_NORMAL /*1*/:
                sb.append(" port");
                break;
            case UI_MODE_TYPE_DESK /*2*/:
                sb.append(" land");
                break;
            default:
                sb.append(" orien=");
                sb.append(this.orientation);
                break;
        }
        switch (this.uiMode & UI_MODE_TYPE_MASK) {
            case UI_MODE_TYPE_UNDEFINED /*0*/:
                sb.append(" ?uimode");
                break;
            case UI_MODE_TYPE_NORMAL /*1*/:
                break;
            case UI_MODE_TYPE_DESK /*2*/:
                sb.append(" desk");
                break;
            case UI_MODE_TYPE_CAR /*3*/:
                sb.append(" car");
                break;
            case UI_MODE_TYPE_TELEVISION /*4*/:
                sb.append(" television");
                break;
            case UI_MODE_TYPE_APPLIANCE /*5*/:
                sb.append(" appliance");
                break;
            case UI_MODE_TYPE_WATCH /*6*/:
                sb.append(" watch");
                break;
            default:
                sb.append(" uimode=");
                sb.append(this.uiMode & UI_MODE_TYPE_MASK);
                break;
        }
        switch (this.uiMode & UI_MODE_NIGHT_MASK) {
            case UI_MODE_TYPE_UNDEFINED /*0*/:
                sb.append(" ?night");
                break;
            case UI_MODE_NIGHT_NO /*16*/:
                break;
            case UI_MODE_NIGHT_YES /*32*/:
                sb.append(" night");
                break;
            default:
                sb.append(" night=");
                sb.append(this.uiMode & UI_MODE_NIGHT_MASK);
                break;
        }
        switch (this.touchscreen) {
            case UI_MODE_TYPE_UNDEFINED /*0*/:
                sb.append(" ?touch");
                break;
            case UI_MODE_TYPE_NORMAL /*1*/:
                sb.append(" -touch");
                break;
            case UI_MODE_TYPE_DESK /*2*/:
                sb.append(" stylus");
                break;
            case UI_MODE_TYPE_CAR /*3*/:
                sb.append(" finger");
                break;
            default:
                sb.append(" touch=");
                sb.append(this.touchscreen);
                break;
        }
        switch (this.keyboard) {
            case UI_MODE_TYPE_UNDEFINED /*0*/:
                sb.append(" ?keyb");
                break;
            case UI_MODE_TYPE_NORMAL /*1*/:
                sb.append(" -keyb");
                break;
            case UI_MODE_TYPE_DESK /*2*/:
                sb.append(" qwerty");
                break;
            case UI_MODE_TYPE_CAR /*3*/:
                sb.append(" 12key");
                break;
            default:
                sb.append(" keys=");
                sb.append(this.keyboard);
                break;
        }
        switch (this.keyboardHidden) {
            case UI_MODE_TYPE_UNDEFINED /*0*/:
                sb.append("/?");
                break;
            case UI_MODE_TYPE_NORMAL /*1*/:
                sb.append("/v");
                break;
            case UI_MODE_TYPE_DESK /*2*/:
                sb.append("/h");
                break;
            case UI_MODE_TYPE_CAR /*3*/:
                sb.append("/s");
                break;
            default:
                sb.append("/");
                sb.append(this.keyboardHidden);
                break;
        }
        switch (this.hardKeyboardHidden) {
            case UI_MODE_TYPE_UNDEFINED /*0*/:
                sb.append("/?");
                break;
            case UI_MODE_TYPE_NORMAL /*1*/:
                sb.append("/v");
                break;
            case UI_MODE_TYPE_DESK /*2*/:
                sb.append("/h");
                break;
            default:
                sb.append("/");
                sb.append(this.hardKeyboardHidden);
                break;
        }
        switch (this.navigation) {
            case UI_MODE_TYPE_UNDEFINED /*0*/:
                sb.append(" ?nav");
                break;
            case UI_MODE_TYPE_NORMAL /*1*/:
                sb.append(" -nav");
                break;
            case UI_MODE_TYPE_DESK /*2*/:
                sb.append(" dpad");
                break;
            case UI_MODE_TYPE_CAR /*3*/:
                sb.append(" tball");
                break;
            case UI_MODE_TYPE_TELEVISION /*4*/:
                sb.append(" wheel");
                break;
            default:
                sb.append(" nav=");
                sb.append(this.navigation);
                break;
        }
        switch (this.navigationHidden) {
            case UI_MODE_TYPE_UNDEFINED /*0*/:
                sb.append("/?");
                break;
            case UI_MODE_TYPE_NORMAL /*1*/:
                sb.append("/v");
                break;
            case UI_MODE_TYPE_DESK /*2*/:
                sb.append("/h");
                break;
            default:
                sb.append("/");
                sb.append(this.navigationHidden);
                break;
        }
        if (this.seq != 0) {
            sb.append(" s.");
            sb.append(this.seq);
        }
        sb.append('}');
        return sb.toString();
    }

    public void setToDefaults() {
        this.fontScale = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        this.mnc = UI_MODE_TYPE_UNDEFINED;
        this.mcc = UI_MODE_TYPE_UNDEFINED;
        this.locale = null;
        this.userSetLocale = false;
        this.touchscreen = UI_MODE_TYPE_UNDEFINED;
        this.keyboard = UI_MODE_TYPE_UNDEFINED;
        this.keyboardHidden = UI_MODE_TYPE_UNDEFINED;
        this.hardKeyboardHidden = UI_MODE_TYPE_UNDEFINED;
        this.navigation = UI_MODE_TYPE_UNDEFINED;
        this.navigationHidden = UI_MODE_TYPE_UNDEFINED;
        this.orientation = UI_MODE_TYPE_UNDEFINED;
        this.screenLayout = UI_MODE_TYPE_UNDEFINED;
        this.uiMode = UI_MODE_TYPE_UNDEFINED;
        this.compatScreenWidthDp = UI_MODE_TYPE_UNDEFINED;
        this.screenWidthDp = UI_MODE_TYPE_UNDEFINED;
        this.compatScreenHeightDp = UI_MODE_TYPE_UNDEFINED;
        this.screenHeightDp = UI_MODE_TYPE_UNDEFINED;
        this.compatSmallestScreenWidthDp = UI_MODE_TYPE_UNDEFINED;
        this.smallestScreenWidthDp = UI_MODE_TYPE_UNDEFINED;
        this.densityDpi = UI_MODE_TYPE_UNDEFINED;
        this.seq = UI_MODE_TYPE_UNDEFINED;
    }

    @Deprecated
    public void makeDefault() {
        setToDefaults();
    }

    public int updateFrom(Configuration delta) {
        int changed = UI_MODE_TYPE_UNDEFINED;
        if (delta.fontScale > 0.0f && this.fontScale != delta.fontScale) {
            changed = UI_MODE_TYPE_UNDEFINED | EditorInfo.IME_FLAG_NO_ENTER_ACTION;
            this.fontScale = delta.fontScale;
        }
        if (!(delta.mcc == 0 || this.mcc == delta.mcc)) {
            changed |= UI_MODE_TYPE_NORMAL;
            this.mcc = delta.mcc;
        }
        if (!(delta.mnc == 0 || this.mnc == delta.mnc)) {
            changed |= UI_MODE_TYPE_DESK;
            this.mnc = delta.mnc;
        }
        if (delta.locale != null && (this.locale == null || !this.locale.equals(delta.locale))) {
            changed |= UI_MODE_TYPE_TELEVISION;
            this.locale = delta.locale != null ? (Locale) delta.locale.clone() : null;
            changed |= NATIVE_CONFIG_SMALLEST_SCREEN_SIZE;
            setLayoutDirection(this.locale);
        }
        int deltaScreenLayoutDir = delta.screenLayout & SCREENLAYOUT_LAYOUTDIR_MASK;
        if (!(deltaScreenLayoutDir == 0 || deltaScreenLayoutDir == (this.screenLayout & SCREENLAYOUT_LAYOUTDIR_MASK))) {
            this.screenLayout = (this.screenLayout & -193) | deltaScreenLayoutDir;
            changed |= NATIVE_CONFIG_SMALLEST_SCREEN_SIZE;
        }
        if (delta.userSetLocale && !(this.userSetLocale && (changed & UI_MODE_TYPE_TELEVISION) == 0)) {
            changed |= UI_MODE_TYPE_TELEVISION;
            this.userSetLocale = true;
        }
        if (!(delta.touchscreen == 0 || this.touchscreen == delta.touchscreen)) {
            changed |= NATIVE_CONFIG_TOUCHSCREEN;
            this.touchscreen = delta.touchscreen;
        }
        if (!(delta.keyboard == 0 || this.keyboard == delta.keyboard)) {
            changed |= UI_MODE_NIGHT_NO;
            this.keyboard = delta.keyboard;
        }
        if (!(delta.keyboardHidden == 0 || this.keyboardHidden == delta.keyboardHidden)) {
            changed |= UI_MODE_NIGHT_YES;
            this.keyboardHidden = delta.keyboardHidden;
        }
        if (!(delta.hardKeyboardHidden == 0 || this.hardKeyboardHidden == delta.hardKeyboardHidden)) {
            changed |= UI_MODE_NIGHT_YES;
            this.hardKeyboardHidden = delta.hardKeyboardHidden;
        }
        if (!(delta.navigation == 0 || this.navigation == delta.navigation)) {
            changed |= SCREENLAYOUT_LAYOUTDIR_LTR;
            this.navigation = delta.navigation;
        }
        if (!(delta.navigationHidden == 0 || this.navigationHidden == delta.navigationHidden)) {
            changed |= UI_MODE_NIGHT_YES;
            this.navigationHidden = delta.navigationHidden;
        }
        if (!(delta.orientation == 0 || this.orientation == delta.orientation)) {
            changed |= SCREENLAYOUT_LAYOUTDIR_RTL;
            this.orientation = delta.orientation;
        }
        if (!(getScreenLayoutNoDirection(delta.screenLayout) == 0 || getScreenLayoutNoDirection(this.screenLayout) == getScreenLayoutNoDirection(delta.screenLayout))) {
            changed |= NATIVE_CONFIG_DENSITY;
            if ((delta.screenLayout & SCREENLAYOUT_LAYOUTDIR_MASK) == 0) {
                this.screenLayout = (this.screenLayout & SCREENLAYOUT_LAYOUTDIR_MASK) | delta.screenLayout;
            } else {
                this.screenLayout = delta.screenLayout;
            }
        }
        if (!(delta.uiMode == 0 || this.uiMode == delta.uiMode)) {
            changed |= NATIVE_CONFIG_SCREEN_SIZE;
            if ((delta.uiMode & UI_MODE_TYPE_MASK) != 0) {
                this.uiMode = (this.uiMode & -16) | (delta.uiMode & UI_MODE_TYPE_MASK);
            }
            if ((delta.uiMode & UI_MODE_NIGHT_MASK) != 0) {
                this.uiMode = (this.uiMode & -49) | (delta.uiMode & UI_MODE_NIGHT_MASK);
            }
        }
        if (!(delta.screenWidthDp == 0 || this.screenWidthDp == delta.screenWidthDp)) {
            changed |= NATIVE_CONFIG_VERSION;
            this.screenWidthDp = delta.screenWidthDp;
        }
        if (!(delta.screenHeightDp == 0 || this.screenHeightDp == delta.screenHeightDp)) {
            changed |= NATIVE_CONFIG_VERSION;
            this.screenHeightDp = delta.screenHeightDp;
        }
        if (!(delta.smallestScreenWidthDp == 0 || this.smallestScreenWidthDp == delta.smallestScreenWidthDp)) {
            changed |= NATIVE_CONFIG_SCREEN_LAYOUT;
            this.smallestScreenWidthDp = delta.smallestScreenWidthDp;
        }
        if (!(delta.densityDpi == 0 || this.densityDpi == delta.densityDpi)) {
            changed |= NATIVE_CONFIG_UI_MODE;
            this.densityDpi = delta.densityDpi;
        }
        if (delta.compatScreenWidthDp != 0) {
            this.compatScreenWidthDp = delta.compatScreenWidthDp;
        }
        if (delta.compatScreenHeightDp != 0) {
            this.compatScreenHeightDp = delta.compatScreenHeightDp;
        }
        if (delta.compatSmallestScreenWidthDp != 0) {
            this.compatSmallestScreenWidthDp = delta.compatSmallestScreenWidthDp;
        }
        if (delta.seq != 0) {
            this.seq = delta.seq;
        }
        return changed;
    }

    public int diff(Configuration delta) {
        int changed = UI_MODE_TYPE_UNDEFINED;
        if (delta.fontScale > 0.0f && this.fontScale != delta.fontScale) {
            changed = UI_MODE_TYPE_UNDEFINED | EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        }
        if (!(delta.mcc == 0 || this.mcc == delta.mcc)) {
            changed |= UI_MODE_TYPE_NORMAL;
        }
        if (!(delta.mnc == 0 || this.mnc == delta.mnc)) {
            changed |= UI_MODE_TYPE_DESK;
        }
        if (delta.locale != null && (this.locale == null || !this.locale.equals(delta.locale))) {
            changed = (changed | UI_MODE_TYPE_TELEVISION) | NATIVE_CONFIG_SMALLEST_SCREEN_SIZE;
        }
        int deltaScreenLayoutDir = delta.screenLayout & SCREENLAYOUT_LAYOUTDIR_MASK;
        if (!(deltaScreenLayoutDir == 0 || deltaScreenLayoutDir == (this.screenLayout & SCREENLAYOUT_LAYOUTDIR_MASK))) {
            changed |= NATIVE_CONFIG_SMALLEST_SCREEN_SIZE;
        }
        if (!(delta.touchscreen == 0 || this.touchscreen == delta.touchscreen)) {
            changed |= NATIVE_CONFIG_TOUCHSCREEN;
        }
        if (!(delta.keyboard == 0 || this.keyboard == delta.keyboard)) {
            changed |= UI_MODE_NIGHT_NO;
        }
        if (!(delta.keyboardHidden == 0 || this.keyboardHidden == delta.keyboardHidden)) {
            changed |= UI_MODE_NIGHT_YES;
        }
        if (!(delta.hardKeyboardHidden == 0 || this.hardKeyboardHidden == delta.hardKeyboardHidden)) {
            changed |= UI_MODE_NIGHT_YES;
        }
        if (!(delta.navigation == 0 || this.navigation == delta.navigation)) {
            changed |= SCREENLAYOUT_LAYOUTDIR_LTR;
        }
        if (!(delta.navigationHidden == 0 || this.navigationHidden == delta.navigationHidden)) {
            changed |= UI_MODE_NIGHT_YES;
        }
        if (!(delta.orientation == 0 || this.orientation == delta.orientation)) {
            changed |= SCREENLAYOUT_LAYOUTDIR_RTL;
        }
        if (!(getScreenLayoutNoDirection(delta.screenLayout) == 0 || getScreenLayoutNoDirection(this.screenLayout) == getScreenLayoutNoDirection(delta.screenLayout))) {
            changed |= NATIVE_CONFIG_DENSITY;
        }
        if (!(delta.uiMode == 0 || this.uiMode == delta.uiMode)) {
            changed |= NATIVE_CONFIG_SCREEN_SIZE;
        }
        if (!(delta.screenWidthDp == 0 || this.screenWidthDp == delta.screenWidthDp)) {
            changed |= NATIVE_CONFIG_VERSION;
        }
        if (!(delta.screenHeightDp == 0 || this.screenHeightDp == delta.screenHeightDp)) {
            changed |= NATIVE_CONFIG_VERSION;
        }
        if (!(delta.smallestScreenWidthDp == 0 || this.smallestScreenWidthDp == delta.smallestScreenWidthDp)) {
            changed |= NATIVE_CONFIG_SCREEN_LAYOUT;
        }
        if (delta.densityDpi == 0 || this.densityDpi == delta.densityDpi) {
            return changed;
        }
        return changed | NATIVE_CONFIG_UI_MODE;
    }

    public static boolean needNewResources(int configChanges, int interestingChanges) {
        return ((EditorInfo.IME_FLAG_NO_ENTER_ACTION | interestingChanges) & configChanges) != 0;
    }

    public boolean isOtherSeqNewer(Configuration other) {
        if (other == null) {
            return false;
        }
        if (other.seq == 0 || this.seq == 0) {
            return true;
        }
        int diff = other.seq - this.seq;
        if (diff > AccessibilityNodeInfo.ACTION_CUT) {
            return false;
        }
        if (diff <= 0) {
            return false;
        }
        return true;
    }

    public int describeContents() {
        return UI_MODE_TYPE_UNDEFINED;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.fontScale);
        dest.writeInt(this.mcc);
        dest.writeInt(this.mnc);
        if (this.locale == null) {
            dest.writeInt(UI_MODE_TYPE_UNDEFINED);
        } else {
            dest.writeInt(UI_MODE_TYPE_NORMAL);
            dest.writeString(this.locale.getLanguage());
            dest.writeString(this.locale.getCountry());
            dest.writeString(this.locale.getVariant());
        }
        if (this.userSetLocale) {
            dest.writeInt(UI_MODE_TYPE_NORMAL);
        } else {
            dest.writeInt(UI_MODE_TYPE_UNDEFINED);
        }
        dest.writeInt(this.touchscreen);
        dest.writeInt(this.keyboard);
        dest.writeInt(this.keyboardHidden);
        dest.writeInt(this.hardKeyboardHidden);
        dest.writeInt(this.navigation);
        dest.writeInt(this.navigationHidden);
        dest.writeInt(this.orientation);
        dest.writeInt(this.screenLayout);
        dest.writeInt(this.uiMode);
        dest.writeInt(this.screenWidthDp);
        dest.writeInt(this.screenHeightDp);
        dest.writeInt(this.smallestScreenWidthDp);
        dest.writeInt(this.densityDpi);
        dest.writeInt(this.compatScreenWidthDp);
        dest.writeInt(this.compatScreenHeightDp);
        dest.writeInt(this.compatSmallestScreenWidthDp);
        dest.writeInt(this.seq);
    }

    public void readFromParcel(Parcel source) {
        boolean z = true;
        this.fontScale = source.readFloat();
        this.mcc = source.readInt();
        this.mnc = source.readInt();
        if (source.readInt() != 0) {
            this.locale = new Locale(source.readString(), source.readString(), source.readString());
        }
        if (source.readInt() != UI_MODE_TYPE_NORMAL) {
            z = false;
        }
        this.userSetLocale = z;
        this.touchscreen = source.readInt();
        this.keyboard = source.readInt();
        this.keyboardHidden = source.readInt();
        this.hardKeyboardHidden = source.readInt();
        this.navigation = source.readInt();
        this.navigationHidden = source.readInt();
        this.orientation = source.readInt();
        this.screenLayout = source.readInt();
        this.uiMode = source.readInt();
        this.screenWidthDp = source.readInt();
        this.screenHeightDp = source.readInt();
        this.smallestScreenWidthDp = source.readInt();
        this.densityDpi = source.readInt();
        this.compatScreenWidthDp = source.readInt();
        this.compatScreenHeightDp = source.readInt();
        this.compatSmallestScreenWidthDp = source.readInt();
        this.seq = source.readInt();
    }

    private Configuration(Parcel source) {
        readFromParcel(source);
    }

    public int compareTo(Configuration that) {
        float a = this.fontScale;
        float b = that.fontScale;
        if (a < b) {
            return -1;
        }
        if (a > b) {
            return UI_MODE_TYPE_NORMAL;
        }
        int n = this.mcc - that.mcc;
        if (n != 0) {
            return n;
        }
        n = this.mnc - that.mnc;
        if (n != 0) {
            return n;
        }
        if (this.locale == null) {
            if (that.locale != null) {
                return UI_MODE_TYPE_NORMAL;
            }
        } else if (that.locale == null) {
            return -1;
        } else {
            n = this.locale.getLanguage().compareTo(that.locale.getLanguage());
            if (n != 0) {
                return n;
            }
            n = this.locale.getCountry().compareTo(that.locale.getCountry());
            if (n != 0) {
                return n;
            }
            n = this.locale.getVariant().compareTo(that.locale.getVariant());
            if (n != 0) {
                return n;
            }
        }
        n = this.touchscreen - that.touchscreen;
        if (n != 0) {
            return n;
        }
        n = this.keyboard - that.keyboard;
        if (n != 0) {
            return n;
        }
        n = this.keyboardHidden - that.keyboardHidden;
        if (n != 0) {
            return n;
        }
        n = this.hardKeyboardHidden - that.hardKeyboardHidden;
        if (n != 0) {
            return n;
        }
        n = this.navigation - that.navigation;
        if (n != 0) {
            return n;
        }
        n = this.navigationHidden - that.navigationHidden;
        if (n != 0) {
            return n;
        }
        n = this.orientation - that.orientation;
        if (n != 0) {
            return n;
        }
        n = this.screenLayout - that.screenLayout;
        if (n != 0) {
            return n;
        }
        n = this.uiMode - that.uiMode;
        if (n != 0) {
            return n;
        }
        n = this.screenWidthDp - that.screenWidthDp;
        if (n != 0) {
            return n;
        }
        n = this.screenHeightDp - that.screenHeightDp;
        if (n != 0) {
            return n;
        }
        n = this.smallestScreenWidthDp - that.smallestScreenWidthDp;
        if (n == 0) {
            return this.densityDpi - that.densityDpi;
        }
        return n;
    }

    public boolean equals(Configuration that) {
        if (that == null) {
            return false;
        }
        if (that == this || compareTo(that) == 0) {
            return true;
        }
        return false;
    }

    public boolean equals(Object that) {
        try {
            return equals((Configuration) that);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public int hashCode() {
        return ((((((((((((((((((((((((((((((((Float.floatToIntBits(this.fontScale) + 527) * 31) + this.mcc) * 31) + this.mnc) * 31) + (this.locale != null ? this.locale.hashCode() : UI_MODE_TYPE_UNDEFINED)) * 31) + this.touchscreen) * 31) + this.keyboard) * 31) + this.keyboardHidden) * 31) + this.hardKeyboardHidden) * 31) + this.navigation) * 31) + this.navigationHidden) * 31) + this.orientation) * 31) + this.screenLayout) * 31) + this.uiMode) * 31) + this.screenWidthDp) * 31) + this.screenHeightDp) * 31) + this.smallestScreenWidthDp) * 31) + this.densityDpi;
    }

    public void setLocale(Locale loc) {
        this.locale = loc;
        this.userSetLocale = true;
        setLayoutDirection(this.locale);
    }

    public int getLayoutDirection() {
        return (this.screenLayout & SCREENLAYOUT_LAYOUTDIR_MASK) == SCREENLAYOUT_LAYOUTDIR_RTL ? UI_MODE_TYPE_NORMAL : UI_MODE_TYPE_UNDEFINED;
    }

    public void setLayoutDirection(Locale locale) {
        this.screenLayout = (this.screenLayout & -193) | ((TextUtils.getLayoutDirectionFromLocale(locale) + UI_MODE_TYPE_NORMAL) << UI_MODE_TYPE_WATCH);
    }

    private static int getScreenLayoutNoDirection(int screenLayout) {
        return screenLayout & -193;
    }

    public static String localeToResourceQualifier(Locale locale) {
        boolean l;
        boolean c;
        boolean s;
        boolean v;
        StringBuilder sb = new StringBuilder();
        if (locale.getLanguage().length() != 0) {
            l = true;
        } else {
            l = false;
        }
        if (locale.getCountry().length() != 0) {
            c = true;
        } else {
            c = false;
        }
        if (locale.getScript().length() != 0) {
            s = true;
        } else {
            s = false;
        }
        if (locale.getVariant().length() != 0) {
            v = true;
        } else {
            v = false;
        }
        if (l) {
            sb.append(locale.getLanguage());
            if (c) {
                sb.append("-r").append(locale.getCountry());
                if (s) {
                    sb.append("-s").append(locale.getScript());
                    if (v) {
                        sb.append("-v").append(locale.getVariant());
                    }
                }
            }
        }
        return sb.toString();
    }

    public static String resourceQualifierString(Configuration config) {
        ArrayList<String> parts = new ArrayList();
        if (config.mcc != 0) {
            parts.add(XML_ATTR_MCC + config.mcc);
            if (config.mnc != 0) {
                parts.add(XML_ATTR_MNC + config.mnc);
            }
        }
        if (!(config.locale == null || config.locale.getLanguage().isEmpty())) {
            parts.add(localeToResourceQualifier(config.locale));
        }
        switch (config.screenLayout & SCREENLAYOUT_LAYOUTDIR_MASK) {
            case SCREENLAYOUT_LAYOUTDIR_LTR /*64*/:
                parts.add("ldltr");
                break;
            case SCREENLAYOUT_LAYOUTDIR_RTL /*128*/:
                parts.add("ldrtl");
                break;
        }
        if (config.smallestScreenWidthDp != 0) {
            parts.add(XML_ATTR_SMALLEST_WIDTH + config.smallestScreenWidthDp + "dp");
        }
        if (config.screenWidthDp != 0) {
            parts.add("w" + config.screenWidthDp + "dp");
        }
        if (config.screenHeightDp != 0) {
            parts.add("h" + config.screenHeightDp + "dp");
        }
        switch (config.screenLayout & UI_MODE_TYPE_MASK) {
            case UI_MODE_TYPE_NORMAL /*1*/:
                parts.add("small");
                break;
            case UI_MODE_TYPE_DESK /*2*/:
                parts.add("normal");
                break;
            case UI_MODE_TYPE_CAR /*3*/:
                parts.add("large");
                break;
            case UI_MODE_TYPE_TELEVISION /*4*/:
                parts.add("xlarge");
                break;
        }
        switch (config.screenLayout & UI_MODE_NIGHT_MASK) {
            case UI_MODE_NIGHT_NO /*16*/:
                parts.add("notlong");
                break;
            case UI_MODE_NIGHT_YES /*32*/:
                parts.add("long");
                break;
        }
        switch (config.orientation) {
            case UI_MODE_TYPE_NORMAL /*1*/:
                parts.add(KeyChain.EXTRA_PORT);
                break;
            case UI_MODE_TYPE_DESK /*2*/:
                parts.add("land");
                break;
        }
        switch (config.uiMode & UI_MODE_TYPE_MASK) {
            case UI_MODE_TYPE_DESK /*2*/:
                parts.add("desk");
                break;
            case UI_MODE_TYPE_CAR /*3*/:
                parts.add("car");
                break;
            case UI_MODE_TYPE_TELEVISION /*4*/:
                parts.add("television");
                break;
            case UI_MODE_TYPE_APPLIANCE /*5*/:
                parts.add("appliance");
                break;
            case UI_MODE_TYPE_WATCH /*6*/:
                parts.add("watch");
                break;
        }
        switch (config.uiMode & UI_MODE_NIGHT_MASK) {
            case UI_MODE_NIGHT_NO /*16*/:
                parts.add("notnight");
                break;
            case UI_MODE_NIGHT_YES /*32*/:
                parts.add(Parameters.SCENE_MODE_NIGHT);
                break;
        }
        switch (config.densityDpi) {
            case UI_MODE_TYPE_UNDEFINED /*0*/:
                break;
            case KeyEvent.KEYCODE_SYSRQ /*120*/:
                parts.add("ldpi");
                break;
            case KeyEvent.KEYCODE_NUMPAD_ENTER /*160*/:
                parts.add("mdpi");
                break;
            case KeyEvent.KEYCODE_MUHENKAN /*213*/:
                parts.add("tvdpi");
                break;
            case LayoutParams.SOFT_INPUT_MASK_ADJUST /*240*/:
                parts.add("hdpi");
                break;
            case MifareClassic.SIZE_MINI /*320*/:
                parts.add("xhdpi");
                break;
            case 480:
                parts.add("xxhdpi");
                break;
            case 640:
                parts.add("xxxhdpi");
                break;
            case DENSITY_DPI_ANY /*65534*/:
                parts.add("anydpi");
                break;
            case MNC_ZERO /*65535*/:
                parts.add("nodpi");
                break;
        }
        parts.add(config.densityDpi + "dpi");
        switch (config.touchscreen) {
            case UI_MODE_TYPE_NORMAL /*1*/:
                parts.add("notouch");
                break;
            case UI_MODE_TYPE_CAR /*3*/:
                parts.add("finger");
                break;
        }
        switch (config.keyboardHidden) {
            case UI_MODE_TYPE_NORMAL /*1*/:
                parts.add("keysexposed");
                break;
            case UI_MODE_TYPE_DESK /*2*/:
                parts.add("keyshidden");
                break;
            case UI_MODE_TYPE_CAR /*3*/:
                parts.add("keyssoft");
                break;
        }
        switch (config.keyboard) {
            case UI_MODE_TYPE_NORMAL /*1*/:
                parts.add("nokeys");
                break;
            case UI_MODE_TYPE_DESK /*2*/:
                parts.add("qwerty");
                break;
            case UI_MODE_TYPE_CAR /*3*/:
                parts.add("12key");
                break;
        }
        switch (config.navigationHidden) {
            case UI_MODE_TYPE_NORMAL /*1*/:
                parts.add("navexposed");
                break;
            case UI_MODE_TYPE_DESK /*2*/:
                parts.add("navhidden");
                break;
        }
        switch (config.navigation) {
            case UI_MODE_TYPE_NORMAL /*1*/:
                parts.add("nonav");
                break;
            case UI_MODE_TYPE_DESK /*2*/:
                parts.add("dpad");
                break;
            case UI_MODE_TYPE_CAR /*3*/:
                parts.add("trackball");
                break;
            case UI_MODE_TYPE_TELEVISION /*4*/:
                parts.add("wheel");
                break;
        }
        parts.add("v" + VERSION.RESOURCES_SDK_INT);
        return TextUtils.join("-", parts);
    }

    public static Configuration generateDelta(Configuration base, Configuration change) {
        Configuration delta = new Configuration();
        if (base.fontScale != change.fontScale) {
            delta.fontScale = change.fontScale;
        }
        if (base.mcc != change.mcc) {
            delta.mcc = change.mcc;
        }
        if (base.mnc != change.mnc) {
            delta.mnc = change.mnc;
        }
        if ((base.locale == null && change.locale != null) || !(base.locale == null || base.locale.equals(change.locale))) {
            delta.locale = change.locale;
        }
        if (base.touchscreen != change.touchscreen) {
            delta.touchscreen = change.touchscreen;
        }
        if (base.keyboard != change.keyboard) {
            delta.keyboard = change.keyboard;
        }
        if (base.keyboardHidden != change.keyboardHidden) {
            delta.keyboardHidden = change.keyboardHidden;
        }
        if (base.navigation != change.navigation) {
            delta.navigation = change.navigation;
        }
        if (base.navigationHidden != change.navigationHidden) {
            delta.navigationHidden = change.navigationHidden;
        }
        if (base.orientation != change.orientation) {
            delta.orientation = change.orientation;
        }
        if ((base.screenLayout & UI_MODE_TYPE_MASK) != (change.screenLayout & UI_MODE_TYPE_MASK)) {
            delta.screenLayout |= change.screenLayout & UI_MODE_TYPE_MASK;
        }
        if ((base.screenLayout & SCREENLAYOUT_LAYOUTDIR_MASK) != (change.screenLayout & SCREENLAYOUT_LAYOUTDIR_MASK)) {
            delta.screenLayout |= change.screenLayout & SCREENLAYOUT_LAYOUTDIR_MASK;
        }
        if ((base.screenLayout & UI_MODE_NIGHT_MASK) != (change.screenLayout & UI_MODE_NIGHT_MASK)) {
            delta.screenLayout |= change.screenLayout & UI_MODE_NIGHT_MASK;
        }
        if ((base.uiMode & UI_MODE_TYPE_MASK) != (change.uiMode & UI_MODE_TYPE_MASK)) {
            delta.uiMode |= change.uiMode & UI_MODE_TYPE_MASK;
        }
        if ((base.uiMode & UI_MODE_NIGHT_MASK) != (change.uiMode & UI_MODE_NIGHT_MASK)) {
            delta.uiMode |= change.uiMode & UI_MODE_NIGHT_MASK;
        }
        if (base.screenWidthDp != change.screenWidthDp) {
            delta.screenWidthDp = change.screenWidthDp;
        }
        if (base.screenHeightDp != change.screenHeightDp) {
            delta.screenHeightDp = change.screenHeightDp;
        }
        if (base.smallestScreenWidthDp != change.smallestScreenWidthDp) {
            delta.smallestScreenWidthDp = change.smallestScreenWidthDp;
        }
        if (base.densityDpi != change.densityDpi) {
            delta.densityDpi = change.densityDpi;
        }
        return delta;
    }

    public static void readXmlAttrs(XmlPullParser parser, Configuration configOut) throws XmlPullParserException, IOException {
        configOut.fontScale = Float.intBitsToFloat(XmlUtils.readIntAttribute(parser, XML_ATTR_FONT_SCALE, UI_MODE_TYPE_UNDEFINED));
        configOut.mcc = XmlUtils.readIntAttribute(parser, XML_ATTR_MCC, UI_MODE_TYPE_UNDEFINED);
        configOut.mnc = XmlUtils.readIntAttribute(parser, XML_ATTR_MNC, UI_MODE_TYPE_UNDEFINED);
        String localeStr = XmlUtils.readStringAttribute(parser, XML_ATTR_LOCALE);
        if (localeStr != null) {
            configOut.locale = Locale.forLanguageTag(localeStr);
        }
        configOut.touchscreen = XmlUtils.readIntAttribute(parser, XML_ATTR_TOUCHSCREEN, UI_MODE_TYPE_UNDEFINED);
        configOut.keyboard = XmlUtils.readIntAttribute(parser, XML_ATTR_KEYBOARD, UI_MODE_TYPE_UNDEFINED);
        configOut.keyboardHidden = XmlUtils.readIntAttribute(parser, XML_ATTR_KEYBOARD_HIDDEN, UI_MODE_TYPE_UNDEFINED);
        configOut.hardKeyboardHidden = XmlUtils.readIntAttribute(parser, XML_ATTR_HARD_KEYBOARD_HIDDEN, UI_MODE_TYPE_UNDEFINED);
        configOut.navigation = XmlUtils.readIntAttribute(parser, XML_ATTR_NAVIGATION, UI_MODE_TYPE_UNDEFINED);
        configOut.navigationHidden = XmlUtils.readIntAttribute(parser, XML_ATTR_NAVIGATION_HIDDEN, UI_MODE_TYPE_UNDEFINED);
        configOut.orientation = XmlUtils.readIntAttribute(parser, XML_ATTR_ORIENTATION, UI_MODE_TYPE_UNDEFINED);
        configOut.screenLayout = XmlUtils.readIntAttribute(parser, XML_ATTR_SCREEN_LAYOUT, UI_MODE_TYPE_UNDEFINED);
        configOut.uiMode = XmlUtils.readIntAttribute(parser, XML_ATTR_UI_MODE, UI_MODE_TYPE_UNDEFINED);
        configOut.screenWidthDp = XmlUtils.readIntAttribute(parser, XML_ATTR_SCREEN_WIDTH, UI_MODE_TYPE_UNDEFINED);
        configOut.screenHeightDp = XmlUtils.readIntAttribute(parser, XML_ATTR_SCREEN_HEIGHT, UI_MODE_TYPE_UNDEFINED);
        configOut.smallestScreenWidthDp = XmlUtils.readIntAttribute(parser, XML_ATTR_SMALLEST_WIDTH, UI_MODE_TYPE_UNDEFINED);
        configOut.densityDpi = XmlUtils.readIntAttribute(parser, XML_ATTR_DENSITY, UI_MODE_TYPE_UNDEFINED);
    }

    public static void writeXmlAttrs(XmlSerializer xml, Configuration config) throws IOException {
        XmlUtils.writeIntAttribute(xml, XML_ATTR_FONT_SCALE, Float.floatToIntBits(config.fontScale));
        if (config.mcc != 0) {
            XmlUtils.writeIntAttribute(xml, XML_ATTR_MCC, config.mcc);
        }
        if (config.mnc != 0) {
            XmlUtils.writeIntAttribute(xml, XML_ATTR_MNC, config.mnc);
        }
        if (config.locale != null) {
            XmlUtils.writeStringAttribute(xml, XML_ATTR_LOCALE, config.locale.toLanguageTag());
        }
        if (config.touchscreen != 0) {
            XmlUtils.writeIntAttribute(xml, XML_ATTR_TOUCHSCREEN, config.touchscreen);
        }
        if (config.keyboard != 0) {
            XmlUtils.writeIntAttribute(xml, XML_ATTR_KEYBOARD, config.keyboard);
        }
        if (config.keyboardHidden != 0) {
            XmlUtils.writeIntAttribute(xml, XML_ATTR_KEYBOARD_HIDDEN, config.keyboardHidden);
        }
        if (config.hardKeyboardHidden != 0) {
            XmlUtils.writeIntAttribute(xml, XML_ATTR_HARD_KEYBOARD_HIDDEN, config.hardKeyboardHidden);
        }
        if (config.navigation != 0) {
            XmlUtils.writeIntAttribute(xml, XML_ATTR_NAVIGATION, config.navigation);
        }
        if (config.navigationHidden != 0) {
            XmlUtils.writeIntAttribute(xml, XML_ATTR_NAVIGATION_HIDDEN, config.navigationHidden);
        }
        if (config.orientation != 0) {
            XmlUtils.writeIntAttribute(xml, XML_ATTR_ORIENTATION, config.orientation);
        }
        if (config.screenLayout != 0) {
            XmlUtils.writeIntAttribute(xml, XML_ATTR_SCREEN_LAYOUT, config.screenLayout);
        }
        if (config.uiMode != 0) {
            XmlUtils.writeIntAttribute(xml, XML_ATTR_UI_MODE, config.uiMode);
        }
        if (config.screenWidthDp != 0) {
            XmlUtils.writeIntAttribute(xml, XML_ATTR_SCREEN_WIDTH, config.screenWidthDp);
        }
        if (config.screenHeightDp != 0) {
            XmlUtils.writeIntAttribute(xml, XML_ATTR_SCREEN_HEIGHT, config.screenHeightDp);
        }
        if (config.smallestScreenWidthDp != 0) {
            XmlUtils.writeIntAttribute(xml, XML_ATTR_SMALLEST_WIDTH, config.smallestScreenWidthDp);
        }
        if (config.densityDpi != 0) {
            XmlUtils.writeIntAttribute(xml, XML_ATTR_DENSITY, config.densityDpi);
        }
    }
}
