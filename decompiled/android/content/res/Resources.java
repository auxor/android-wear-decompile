package android.content.res;

import android.R;
import android.animation.Animator;
import android.animation.StateListAnimator;
import android.content.pm.ActivityInfo;
import android.graphics.Movie;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.net.ProxyInfo;
import android.nfc.cardemulation.CardEmulation;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Trace;
import android.telephony.SubscriptionManager;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.Pools.SynchronizedPool;
import android.util.TypedValue;
import android.view.ViewDebug.ExportedProperty;
import android.view.Window;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AppSecurityPermissions;
import android.widget.Toast;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Locale;
import libcore.icu.NativePluralRules;
import org.xmlpull.v1.XmlPullParserException;

public class Resources {
    private static final boolean DEBUG_CONFIG = false;
    private static final boolean DEBUG_LOAD = false;
    private static final int ID_OTHER = 16777220;
    private static final int LAYOUT_DIR_CONFIG;
    static final String TAG = "Resources";
    private static final boolean TRACE_FOR_MISS_PRELOAD = false;
    private static final boolean TRACE_FOR_PRELOAD = false;
    static Resources mSystem;
    private static boolean sPreloaded;
    private static final LongSparseArray<ConstantState> sPreloadedColorDrawables;
    private static final LongSparseArray<ColorStateList> sPreloadedColorStateLists;
    private static int sPreloadedDensity;
    private static final LongSparseArray<ConstantState>[] sPreloadedDrawables;
    private static final Object sSync;
    private final Object mAccessLock;
    private final ConfigurationBoundResourceCache<Animator> mAnimatorCache;
    final AssetManager mAssets;
    private TypedArray mCachedStyledAttributes;
    private final int[] mCachedXmlBlockIds;
    private final XmlBlock[] mCachedXmlBlocks;
    private final ArrayMap<String, LongSparseArray<WeakReference<ConstantState>>> mColorDrawableCache;
    private final LongSparseArray<WeakReference<ColorStateList>> mColorStateListCache;
    private CompatibilityInfo mCompatibilityInfo;
    private final Configuration mConfiguration;
    private final ArrayMap<String, LongSparseArray<WeakReference<ConstantState>>> mDrawableCache;
    private int mLastCachedXmlBlockIndex;
    final DisplayMetrics mMetrics;
    private NativePluralRules mPluralRule;
    private boolean mPreloading;
    private final ConfigurationBoundResourceCache<StateListAnimator> mStateListAnimatorCache;
    private final Configuration mTmpConfig;
    private TypedValue mTmpValue;
    private WeakReference<IBinder> mToken;
    final SynchronizedPool<TypedArray> mTypedArrayPool;

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String name) {
            super(name);
        }
    }

    public final class Theme {
        private final AssetManager mAssets;
        private String mKey;
        private final long mTheme;
        private int mThemeResId;

        public void applyStyle(int resId, boolean force) {
            AssetManager.applyThemeStyle(this.mTheme, resId, force);
            this.mThemeResId = resId;
            this.mKey += Integer.toHexString(resId) + (force ? "! " : " ");
        }

        public void setTo(Theme other) {
            AssetManager.copyTheme(this.mTheme, other.mTheme);
            this.mThemeResId = other.mThemeResId;
            this.mKey = other.mKey;
        }

        public TypedArray obtainStyledAttributes(int[] attrs) {
            TypedArray array = TypedArray.obtain(Resources.this, attrs.length);
            array.mTheme = this;
            AssetManager.applyStyle(this.mTheme, Resources.LAYOUT_DIR_CONFIG, Resources.LAYOUT_DIR_CONFIG, 0, attrs, array.mData, array.mIndices);
            return array;
        }

        public TypedArray obtainStyledAttributes(int resid, int[] attrs) throws NotFoundException {
            TypedArray array = TypedArray.obtain(Resources.this, attrs.length);
            array.mTheme = this;
            AssetManager.applyStyle(this.mTheme, Resources.LAYOUT_DIR_CONFIG, resid, 0, attrs, array.mData, array.mIndices);
            return array;
        }

        public TypedArray obtainStyledAttributes(AttributeSet set, int[] attrs, int defStyleAttr, int defStyleRes) {
            TypedArray array = TypedArray.obtain(Resources.this, attrs.length);
            Parser parser = (Parser) set;
            AssetManager.applyStyle(this.mTheme, defStyleAttr, defStyleRes, parser != null ? parser.mParseState : 0, attrs, array.mData, array.mIndices);
            array.mTheme = this;
            array.mXml = parser;
            return array;
        }

        public TypedArray resolveAttributes(int[] values, int[] attrs) {
            int len = attrs.length;
            if (values == null || len != values.length) {
                throw new IllegalArgumentException("Base attribute values must the same length as attrs");
            }
            TypedArray array = TypedArray.obtain(Resources.this, len);
            AssetManager.resolveAttrs(this.mTheme, Resources.LAYOUT_DIR_CONFIG, Resources.LAYOUT_DIR_CONFIG, values, attrs, array.mData, array.mIndices);
            array.mTheme = this;
            array.mXml = null;
            return array;
        }

        public boolean resolveAttribute(int resid, TypedValue outValue, boolean resolveRefs) {
            return this.mAssets.getThemeValue(this.mTheme, resid, outValue, resolveRefs);
        }

        public int[] getAllAttributes() {
            return this.mAssets.getStyleAttributes(getAppliedStyleResId());
        }

        public Resources getResources() {
            return Resources.this;
        }

        public Drawable getDrawable(int id) throws NotFoundException {
            return Resources.this.getDrawable(id, this);
        }

        public void dump(int priority, String tag, String prefix) {
            AssetManager.dumpTheme(this.mTheme, priority, tag, prefix);
        }

        protected void finalize() throws Throwable {
            super.finalize();
            this.mAssets.releaseTheme(this.mTheme);
        }

        Theme() {
            this.mThemeResId = Resources.LAYOUT_DIR_CONFIG;
            this.mKey = ProxyInfo.LOCAL_EXCL_LIST;
            this.mAssets = Resources.this.mAssets;
            this.mTheme = this.mAssets.createTheme();
        }

        long getNativeTheme() {
            return this.mTheme;
        }

        int getAppliedStyleResId() {
            return this.mThemeResId;
        }

        String getKey() {
            return this.mKey;
        }

        private String getResourceNameFromHexString(String hexString) {
            return Resources.this.getResourceName(Integer.parseInt(hexString, 16));
        }

        @ExportedProperty(category = "theme", hasAdjacentMapping = true)
        public String[] getTheme() {
            String[] themeData = this.mKey.split(" ");
            String[] themes = new String[(themeData.length * 2)];
            int i = Resources.LAYOUT_DIR_CONFIG;
            int j = themeData.length - 1;
            while (i < themes.length) {
                String str;
                String theme = themeData[j];
                boolean forced = theme.endsWith("!");
                themes[i] = forced ? getResourceNameFromHexString(theme.substring(Resources.LAYOUT_DIR_CONFIG, theme.length() - 1)) : getResourceNameFromHexString(theme);
                int i2 = i + 1;
                if (forced) {
                    str = "forced";
                } else {
                    str = "not forced";
                }
                themes[i2] = str;
                i += 2;
                j--;
            }
            return themes;
        }
    }

    static {
        LAYOUT_DIR_CONFIG = ActivityInfo.activityInfoConfigToNative(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
        sSync = new Object();
        sPreloadedColorDrawables = new LongSparseArray();
        sPreloadedColorStateLists = new LongSparseArray();
        mSystem = null;
        sPreloadedDrawables = new LongSparseArray[2];
        sPreloadedDrawables[LAYOUT_DIR_CONFIG] = new LongSparseArray();
        sPreloadedDrawables[1] = new LongSparseArray();
    }

    public static int selectDefaultTheme(int curTheme, int targetSdkVersion) {
        return selectSystemTheme(curTheme, targetSdkVersion, R.style.Theme, R.style.Theme_Holo, R.style.Theme_DeviceDefault, R.style.Theme_DeviceDefault_Light_DarkActionBar);
    }

    public static int selectSystemTheme(int curTheme, int targetSdkVersion, int orig, int holo, int dark, int deviceDefault) {
        if (curTheme != 0) {
            return curTheme;
        }
        if (targetSdkVersion < 11) {
            return orig;
        }
        if (targetSdkVersion < 14) {
            return holo;
        }
        if (targetSdkVersion < Window.PROGRESS_END) {
            return dark;
        }
        return deviceDefault;
    }

    public ConfigurationBoundResourceCache<Animator> getAnimatorCache() {
        return this.mAnimatorCache;
    }

    public ConfigurationBoundResourceCache<StateListAnimator> getStateListAnimatorCache() {
        return this.mStateListAnimatorCache;
    }

    public Resources(AssetManager assets, DisplayMetrics metrics, Configuration config) {
        this(assets, metrics, config, CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO, null);
    }

    public Resources(AssetManager assets, DisplayMetrics metrics, Configuration config, CompatibilityInfo compatInfo, IBinder token) {
        this.mTypedArrayPool = new SynchronizedPool(5);
        this.mAccessLock = new Object();
        this.mTmpConfig = new Configuration();
        this.mDrawableCache = new ArrayMap();
        this.mColorDrawableCache = new ArrayMap();
        this.mColorStateListCache = new LongSparseArray();
        this.mAnimatorCache = new ConfigurationBoundResourceCache(this);
        this.mStateListAnimatorCache = new ConfigurationBoundResourceCache(this);
        this.mTmpValue = new TypedValue();
        this.mCachedStyledAttributes = null;
        this.mLastCachedXmlBlockIndex = -1;
        this.mCachedXmlBlockIds = new int[]{LAYOUT_DIR_CONFIG, LAYOUT_DIR_CONFIG, LAYOUT_DIR_CONFIG, LAYOUT_DIR_CONFIG};
        this.mCachedXmlBlocks = new XmlBlock[4];
        this.mMetrics = new DisplayMetrics();
        this.mConfiguration = new Configuration();
        this.mCompatibilityInfo = CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
        this.mAssets = assets;
        this.mMetrics.setToDefaults();
        if (compatInfo != null) {
            this.mCompatibilityInfo = compatInfo;
        }
        this.mToken = new WeakReference(token);
        updateConfiguration(config, metrics);
        assets.ensureStringBlocks();
    }

    public static Resources getSystem() {
        Resources ret;
        synchronized (sSync) {
            ret = mSystem;
            if (ret == null) {
                ret = new Resources();
                mSystem = ret;
            }
        }
        return ret;
    }

    public CharSequence getText(int id) throws NotFoundException {
        CharSequence res = this.mAssets.getResourceText(id);
        if (res != null) {
            return res;
        }
        throw new NotFoundException("String resource ID #0x" + Integer.toHexString(id));
    }

    public CharSequence getQuantityText(int id, int quantity) throws NotFoundException {
        NativePluralRules rule = getPluralRule();
        CharSequence res = this.mAssets.getResourceBagText(id, attrForQuantityCode(rule.quantityForInt(quantity)));
        if (res != null) {
            return res;
        }
        res = this.mAssets.getResourceBagText(id, ID_OTHER);
        if (res != null) {
            return res;
        }
        throw new NotFoundException("Plural resource ID #0x" + Integer.toHexString(id) + " quantity=" + quantity + " item=" + stringForQuantityCode(rule.quantityForInt(quantity)));
    }

    private NativePluralRules getPluralRule() {
        NativePluralRules nativePluralRules;
        synchronized (sSync) {
            if (this.mPluralRule == null) {
                this.mPluralRule = NativePluralRules.forLocale(this.mConfiguration.locale);
            }
            nativePluralRules = this.mPluralRule;
        }
        return nativePluralRules;
    }

    private static int attrForQuantityCode(int quantityCode) {
        switch (quantityCode) {
            case LAYOUT_DIR_CONFIG:
                return 16777221;
            case Toast.LENGTH_LONG /*1*/:
                return 16777222;
            case Action.MERGE_IGNORE /*2*/:
                return 16777223;
            case SetDrawableParameters.TAG /*3*/:
                return 16777224;
            case ViewGroupAction.TAG /*4*/:
                return 16777225;
            default:
                return ID_OTHER;
        }
    }

    private static String stringForQuantityCode(int quantityCode) {
        switch (quantityCode) {
            case LAYOUT_DIR_CONFIG:
                return "zero";
            case Toast.LENGTH_LONG /*1*/:
                return "one";
            case Action.MERGE_IGNORE /*2*/:
                return "two";
            case SetDrawableParameters.TAG /*3*/:
                return "few";
            case ViewGroupAction.TAG /*4*/:
                return "many";
            default:
                return CardEmulation.CATEGORY_OTHER;
        }
    }

    public String getString(int id) throws NotFoundException {
        CharSequence res = getText(id);
        if (res != null) {
            return res.toString();
        }
        throw new NotFoundException("String resource ID #0x" + Integer.toHexString(id));
    }

    public String getString(int id, Object... formatArgs) throws NotFoundException {
        return String.format(this.mConfiguration.locale, getString(id), formatArgs);
    }

    public String getQuantityString(int id, int quantity, Object... formatArgs) throws NotFoundException {
        return String.format(this.mConfiguration.locale, getQuantityText(id, quantity).toString(), formatArgs);
    }

    public String getQuantityString(int id, int quantity) throws NotFoundException {
        return getQuantityText(id, quantity).toString();
    }

    public CharSequence getText(int id, CharSequence def) {
        CharSequence res = id != 0 ? this.mAssets.getResourceText(id) : null;
        if (res != null) {
            return res;
        }
        return def;
    }

    public CharSequence[] getTextArray(int id) throws NotFoundException {
        CharSequence[] res = this.mAssets.getResourceTextArray(id);
        if (res != null) {
            return res;
        }
        throw new NotFoundException("Text array resource ID #0x" + Integer.toHexString(id));
    }

    public String[] getStringArray(int id) throws NotFoundException {
        String[] res = this.mAssets.getResourceStringArray(id);
        if (res != null) {
            return res;
        }
        throw new NotFoundException("String array resource ID #0x" + Integer.toHexString(id));
    }

    public int[] getIntArray(int id) throws NotFoundException {
        int[] res = this.mAssets.getArrayIntResource(id);
        if (res != null) {
            return res;
        }
        throw new NotFoundException("Int array resource ID #0x" + Integer.toHexString(id));
    }

    public TypedArray obtainTypedArray(int id) throws NotFoundException {
        int len = this.mAssets.getArraySize(id);
        if (len < 0) {
            throw new NotFoundException("Array resource ID #0x" + Integer.toHexString(id));
        }
        TypedArray array = TypedArray.obtain(this, len);
        array.mLength = this.mAssets.retrieveArray(id, array.mData);
        array.mIndices[LAYOUT_DIR_CONFIG] = LAYOUT_DIR_CONFIG;
        return array;
    }

    public float getDimension(int id) throws NotFoundException {
        float complexToDimension;
        synchronized (this.mAccessLock) {
            TypedValue value = this.mTmpValue;
            if (value == null) {
                value = new TypedValue();
                this.mTmpValue = value;
            }
            getValue(id, value, true);
            if (value.type == 5) {
                complexToDimension = TypedValue.complexToDimension(value.data, this.mMetrics);
            } else {
                throw new NotFoundException("Resource ID #0x" + Integer.toHexString(id) + " type #0x" + Integer.toHexString(value.type) + " is not valid");
            }
        }
        return complexToDimension;
    }

    public int getDimensionPixelOffset(int id) throws NotFoundException {
        int complexToDimensionPixelOffset;
        synchronized (this.mAccessLock) {
            TypedValue value = this.mTmpValue;
            if (value == null) {
                value = new TypedValue();
                this.mTmpValue = value;
            }
            getValue(id, value, true);
            if (value.type == 5) {
                complexToDimensionPixelOffset = TypedValue.complexToDimensionPixelOffset(value.data, this.mMetrics);
            } else {
                throw new NotFoundException("Resource ID #0x" + Integer.toHexString(id) + " type #0x" + Integer.toHexString(value.type) + " is not valid");
            }
        }
        return complexToDimensionPixelOffset;
    }

    public int getDimensionPixelSize(int id) throws NotFoundException {
        int complexToDimensionPixelSize;
        synchronized (this.mAccessLock) {
            TypedValue value = this.mTmpValue;
            if (value == null) {
                value = new TypedValue();
                this.mTmpValue = value;
            }
            getValue(id, value, true);
            if (value.type == 5) {
                complexToDimensionPixelSize = TypedValue.complexToDimensionPixelSize(value.data, this.mMetrics);
            } else {
                throw new NotFoundException("Resource ID #0x" + Integer.toHexString(id) + " type #0x" + Integer.toHexString(value.type) + " is not valid");
            }
        }
        return complexToDimensionPixelSize;
    }

    public float getFraction(int id, int base, int pbase) {
        float complexToFraction;
        synchronized (this.mAccessLock) {
            TypedValue value = this.mTmpValue;
            if (value == null) {
                value = new TypedValue();
                this.mTmpValue = value;
            }
            getValue(id, value, true);
            if (value.type == 6) {
                complexToFraction = TypedValue.complexToFraction(value.data, (float) base, (float) pbase);
            } else {
                throw new NotFoundException("Resource ID #0x" + Integer.toHexString(id) + " type #0x" + Integer.toHexString(value.type) + " is not valid");
            }
        }
        return complexToFraction;
    }

    @Deprecated
    public Drawable getDrawable(int id) throws NotFoundException {
        Drawable d = getDrawable(id, null);
        if (d != null && d.canApplyTheme()) {
            Log.w(TAG, "Drawable " + getResourceName(id) + " has unresolved theme " + "attributes! Consider using Resources.getDrawable(int, Theme) or " + "Context.getDrawable(int).", new RuntimeException());
        }
        return d;
    }

    public Drawable getDrawable(int id, Theme theme) throws NotFoundException {
        TypedValue value;
        synchronized (this.mAccessLock) {
            value = this.mTmpValue;
            if (value == null) {
                value = new TypedValue();
            } else {
                this.mTmpValue = null;
            }
            getValue(id, value, true);
        }
        Drawable res = loadDrawable(value, id, theme);
        synchronized (this.mAccessLock) {
            if (this.mTmpValue == null) {
                this.mTmpValue = value;
            }
        }
        return res;
    }

    @Deprecated
    public Drawable getDrawableForDensity(int id, int density) throws NotFoundException {
        return getDrawableForDensity(id, density, null);
    }

    public Drawable getDrawableForDensity(int id, int density, Theme theme) {
        TypedValue value;
        synchronized (this.mAccessLock) {
            value = this.mTmpValue;
            if (value == null) {
                value = new TypedValue();
            } else {
                this.mTmpValue = null;
            }
            getValueForDensity(id, density, value, true);
            if (value.density > 0 && value.density != AppSecurityPermissions.WHICH_ALL) {
                if (value.density == density) {
                    value.density = this.mMetrics.densityDpi;
                } else {
                    value.density = (value.density * this.mMetrics.densityDpi) / density;
                }
            }
        }
        Drawable res = loadDrawable(value, id, theme);
        synchronized (this.mAccessLock) {
            if (this.mTmpValue == null) {
                this.mTmpValue = value;
            }
        }
        return res;
    }

    public Movie getMovie(int id) throws NotFoundException {
        InputStream is = openRawResource(id);
        Movie movie = Movie.decodeStream(is);
        try {
            is.close();
        } catch (IOException e) {
        }
        return movie;
    }

    public int getColor(int id) throws NotFoundException {
        synchronized (this.mAccessLock) {
            TypedValue value = this.mTmpValue;
            if (value == null) {
                value = new TypedValue();
            }
            getValue(id, value, true);
            if (value.type >= 16 && value.type <= 31) {
                this.mTmpValue = value;
                int i = value.data;
                return i;
            } else if (value.type != 3) {
                throw new NotFoundException("Resource ID #0x" + Integer.toHexString(id) + " type #0x" + Integer.toHexString(value.type) + " is not valid");
            } else {
                this.mTmpValue = null;
                ColorStateList csl = loadColorStateList(value, id);
                synchronized (this.mAccessLock) {
                    if (this.mTmpValue == null) {
                        this.mTmpValue = value;
                    }
                }
                return csl.getDefaultColor();
            }
        }
    }

    public ColorStateList getColorStateList(int id) throws NotFoundException {
        TypedValue value;
        synchronized (this.mAccessLock) {
            value = this.mTmpValue;
            if (value == null) {
                value = new TypedValue();
            } else {
                this.mTmpValue = null;
            }
            getValue(id, value, true);
        }
        ColorStateList res = loadColorStateList(value, id);
        synchronized (this.mAccessLock) {
            if (this.mTmpValue == null) {
                this.mTmpValue = value;
            }
        }
        return res;
    }

    public boolean getBoolean(int id) throws NotFoundException {
        boolean z = true;
        synchronized (this.mAccessLock) {
            TypedValue value = this.mTmpValue;
            if (value == null) {
                value = new TypedValue();
                this.mTmpValue = value;
            }
            getValue(id, value, true);
            if (value.type < 16 || value.type > 31) {
                throw new NotFoundException("Resource ID #0x" + Integer.toHexString(id) + " type #0x" + Integer.toHexString(value.type) + " is not valid");
            }
            if (value.data == 0) {
                z = DEBUG_LOAD;
            }
        }
        return z;
    }

    public int getInteger(int id) throws NotFoundException {
        int i;
        synchronized (this.mAccessLock) {
            TypedValue value = this.mTmpValue;
            if (value == null) {
                value = new TypedValue();
                this.mTmpValue = value;
            }
            getValue(id, value, true);
            if (value.type < 16 || value.type > 31) {
                throw new NotFoundException("Resource ID #0x" + Integer.toHexString(id) + " type #0x" + Integer.toHexString(value.type) + " is not valid");
            }
            i = value.data;
        }
        return i;
    }

    public float getFloat(int id) {
        float f;
        synchronized (this.mAccessLock) {
            TypedValue value = this.mTmpValue;
            if (value == null) {
                value = new TypedValue();
                this.mTmpValue = value;
            }
            getValue(id, value, true);
            if (value.type == 4) {
                f = value.getFloat();
            } else {
                throw new NotFoundException("Resource ID #0x" + Integer.toHexString(id) + " type #0x" + Integer.toHexString(value.type) + " is not valid");
            }
        }
        return f;
    }

    public XmlResourceParser getLayout(int id) throws NotFoundException {
        return loadXmlResourceParser(id, "layout");
    }

    public XmlResourceParser getAnimation(int id) throws NotFoundException {
        return loadXmlResourceParser(id, "anim");
    }

    public XmlResourceParser getXml(int id) throws NotFoundException {
        return loadXmlResourceParser(id, "xml");
    }

    public InputStream openRawResource(int id) throws NotFoundException {
        TypedValue value;
        synchronized (this.mAccessLock) {
            value = this.mTmpValue;
            if (value == null) {
                value = new TypedValue();
            } else {
                this.mTmpValue = null;
            }
        }
        InputStream res = openRawResource(id, value);
        synchronized (this.mAccessLock) {
            if (this.mTmpValue == null) {
                this.mTmpValue = value;
            }
        }
        return res;
    }

    public InputStream openRawResource(int id, TypedValue value) throws NotFoundException {
        getValue(id, value, true);
        try {
            return this.mAssets.openNonAsset(value.assetCookie, value.string.toString(), 2);
        } catch (Exception e) {
            NotFoundException rnf = new NotFoundException("File " + value.string.toString() + " from drawable resource ID #0x" + Integer.toHexString(id));
            rnf.initCause(e);
            throw rnf;
        }
    }

    public AssetFileDescriptor openRawResourceFd(int id) throws NotFoundException {
        synchronized (this.mAccessLock) {
            TypedValue value = this.mTmpValue;
            if (value == null) {
                value = new TypedValue();
            } else {
                this.mTmpValue = null;
            }
            getValue(id, value, true);
        }
        try {
            AssetFileDescriptor openNonAssetFd = this.mAssets.openNonAssetFd(value.assetCookie, value.string.toString());
            synchronized (this.mAccessLock) {
                if (this.mTmpValue == null) {
                    this.mTmpValue = value;
                }
            }
            return openNonAssetFd;
        } catch (Exception e) {
            NotFoundException rnf = new NotFoundException("File " + value.string.toString() + " from drawable resource ID #0x" + Integer.toHexString(id));
            rnf.initCause(e);
            throw rnf;
        } catch (Throwable th) {
            synchronized (this.mAccessLock) {
            }
            if (this.mTmpValue == null) {
                this.mTmpValue = value;
            }
        }
    }

    public void getValue(int id, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
        if (!this.mAssets.getResourceValue(id, LAYOUT_DIR_CONFIG, outValue, resolveRefs)) {
            throw new NotFoundException("Resource ID #0x" + Integer.toHexString(id));
        }
    }

    public void getValueForDensity(int id, int density, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
        if (!this.mAssets.getResourceValue(id, density, outValue, resolveRefs)) {
            throw new NotFoundException("Resource ID #0x" + Integer.toHexString(id));
        }
    }

    public void getValue(String name, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
        int id = getIdentifier(name, "string", null);
        if (id != 0) {
            getValue(id, outValue, resolveRefs);
            return;
        }
        throw new NotFoundException("String resource name " + name);
    }

    public final Theme newTheme() {
        return new Theme();
    }

    public TypedArray obtainAttributes(AttributeSet set, int[] attrs) {
        TypedArray array = TypedArray.obtain(this, attrs.length);
        Parser parser = (Parser) set;
        this.mAssets.retrieveAttributes(parser.mParseState, attrs, array.mData, array.mIndices);
        array.mXml = parser;
        return array;
    }

    public void updateConfiguration(Configuration config, DisplayMetrics metrics) {
        updateConfiguration(config, metrics, null);
    }

    public void updateConfiguration(Configuration config, DisplayMetrics metrics, CompatibilityInfo compat) {
        synchronized (this.mAccessLock) {
            int width;
            int height;
            if (compat != null) {
                this.mCompatibilityInfo = compat;
            }
            if (metrics != null) {
                this.mMetrics.setTo(metrics);
            }
            this.mCompatibilityInfo.applyToDisplayMetrics(this.mMetrics);
            int configChanges = calcConfigChanges(config);
            if (this.mConfiguration.locale == null) {
                this.mConfiguration.locale = Locale.getDefault();
                this.mConfiguration.setLayoutDirection(this.mConfiguration.locale);
            }
            if (this.mConfiguration.densityDpi != 0) {
                this.mMetrics.densityDpi = this.mConfiguration.densityDpi;
                this.mMetrics.density = ((float) this.mConfiguration.densityDpi) * 0.00625f;
            }
            this.mMetrics.scaledDensity = this.mMetrics.density * this.mConfiguration.fontScale;
            String locale = null;
            if (this.mConfiguration.locale != null) {
                locale = adjustLanguageTag(this.mConfiguration.locale.toLanguageTag());
            }
            if (this.mMetrics.widthPixels >= this.mMetrics.heightPixels) {
                width = this.mMetrics.widthPixels;
                height = this.mMetrics.heightPixels;
            } else {
                width = this.mMetrics.heightPixels;
                height = this.mMetrics.widthPixels;
            }
            int keyboardHidden = this.mConfiguration.keyboardHidden;
            if (keyboardHidden == 1 && this.mConfiguration.hardKeyboardHidden == 2) {
                keyboardHidden = 3;
            }
            this.mAssets.setConfiguration(this.mConfiguration.mcc, this.mConfiguration.mnc, locale, this.mConfiguration.orientation, this.mConfiguration.touchscreen, this.mConfiguration.densityDpi, this.mConfiguration.keyboard, keyboardHidden, this.mConfiguration.navigation, width, height, this.mConfiguration.smallestScreenWidthDp, this.mConfiguration.screenWidthDp, this.mConfiguration.screenHeightDp, this.mConfiguration.screenLayout, this.mConfiguration.uiMode, VERSION.RESOURCES_SDK_INT);
            clearDrawableCachesLocked(this.mDrawableCache, configChanges);
            clearDrawableCachesLocked(this.mColorDrawableCache, configChanges);
            this.mAnimatorCache.onConfigurationChange(configChanges);
            this.mStateListAnimatorCache.onConfigurationChange(configChanges);
            this.mColorStateListCache.clear();
            flushLayoutCache();
        }
        synchronized (sSync) {
            if (this.mPluralRule != null) {
                this.mPluralRule = NativePluralRules.forLocale(config.locale);
            }
        }
    }

    private int calcConfigChanges(Configuration config) {
        if (config == null) {
            return 268435455;
        }
        this.mTmpConfig.setTo(config);
        int density = config.densityDpi;
        if (density == 0) {
            density = this.mMetrics.noncompatDensityDpi;
        }
        this.mCompatibilityInfo.applyToConfiguration(density, this.mTmpConfig);
        if (this.mTmpConfig.locale == null) {
            this.mTmpConfig.locale = Locale.getDefault();
            this.mTmpConfig.setLayoutDirection(this.mTmpConfig.locale);
        }
        return ActivityInfo.activityInfoConfigToNative(this.mConfiguration.updateFrom(this.mTmpConfig));
    }

    private void clearDrawableCachesLocked(ArrayMap<String, LongSparseArray<WeakReference<ConstantState>>> caches, int configChanges) {
        int N = caches.size();
        for (int i = LAYOUT_DIR_CONFIG; i < N; i++) {
            clearDrawableCacheLocked((LongSparseArray) caches.valueAt(i), configChanges);
        }
    }

    private void clearDrawableCacheLocked(LongSparseArray<WeakReference<ConstantState>> cache, int configChanges) {
        int N = cache.size();
        for (int i = LAYOUT_DIR_CONFIG; i < N; i++) {
            WeakReference<ConstantState> ref = (WeakReference) cache.valueAt(i);
            if (ref != null) {
                ConstantState cs = (ConstantState) ref.get();
                if (cs != null && Configuration.needNewResources(configChanges, cs.getChangingConfigurations())) {
                    cache.setValueAt(i, null);
                }
            }
        }
    }

    private static String adjustLanguageTag(String languageTag) {
        String language;
        String remainder;
        int separator = languageTag.indexOf(45);
        if (separator == -1) {
            language = languageTag;
            remainder = ProxyInfo.LOCAL_EXCL_LIST;
        } else {
            language = languageTag.substring(LAYOUT_DIR_CONFIG, separator);
            remainder = languageTag.substring(separator);
        }
        return Locale.adjustLanguageCode(language) + remainder;
    }

    public static void updateSystemConfiguration(Configuration config, DisplayMetrics metrics, CompatibilityInfo compat) {
        if (mSystem != null) {
            mSystem.updateConfiguration(config, metrics, compat);
        }
    }

    public DisplayMetrics getDisplayMetrics() {
        return this.mMetrics;
    }

    public Configuration getConfiguration() {
        return this.mConfiguration;
    }

    public CompatibilityInfo getCompatibilityInfo() {
        return this.mCompatibilityInfo;
    }

    public void setCompatibilityInfo(CompatibilityInfo ci) {
        if (ci != null) {
            this.mCompatibilityInfo = ci;
            updateConfiguration(this.mConfiguration, this.mMetrics);
        }
    }

    public int getIdentifier(String name, String defType, String defPackage) {
        if (name == null) {
            throw new NullPointerException("name is null");
        }
        try {
            return Integer.parseInt(name);
        } catch (Exception e) {
            return this.mAssets.getResourceIdentifier(name, defType, defPackage);
        }
    }

    public static boolean resourceHasPackage(int resid) {
        return (resid >>> 24) != 0 ? true : DEBUG_LOAD;
    }

    public String getResourceName(int resid) throws NotFoundException {
        String str = this.mAssets.getResourceName(resid);
        if (str != null) {
            return str;
        }
        throw new NotFoundException("Unable to find resource ID #0x" + Integer.toHexString(resid));
    }

    public String getResourcePackageName(int resid) throws NotFoundException {
        String str = this.mAssets.getResourcePackageName(resid);
        if (str != null) {
            return str;
        }
        throw new NotFoundException("Unable to find resource ID #0x" + Integer.toHexString(resid));
    }

    public String getResourceTypeName(int resid) throws NotFoundException {
        String str = this.mAssets.getResourceTypeName(resid);
        if (str != null) {
            return str;
        }
        throw new NotFoundException("Unable to find resource ID #0x" + Integer.toHexString(resid));
    }

    public String getResourceEntryName(int resid) throws NotFoundException {
        String str = this.mAssets.getResourceEntryName(resid);
        if (str != null) {
            return str;
        }
        throw new NotFoundException("Unable to find resource ID #0x" + Integer.toHexString(resid));
    }

    public void parseBundleExtras(XmlResourceParser parser, Bundle outBundle) throws XmlPullParserException, IOException {
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type == 1) {
                return;
            }
            if (type == 3 && parser.getDepth() <= outerDepth) {
                return;
            }
            if (!(type == 3 || type == 4)) {
                if (parser.getName().equals("extra")) {
                    parseBundleExtra("extra", parser, outBundle);
                    XmlUtils.skipCurrentTag(parser);
                } else {
                    XmlUtils.skipCurrentTag(parser);
                }
            }
        }
    }

    public void parseBundleExtra(String tagName, AttributeSet attrs, Bundle outBundle) throws XmlPullParserException {
        boolean z = true;
        TypedArray sa = obtainAttributes(attrs, com.android.internal.R.styleable.Extra);
        String name = sa.getString(LAYOUT_DIR_CONFIG);
        if (name == null) {
            sa.recycle();
            throw new XmlPullParserException("<" + tagName + "> requires an android:name attribute at " + attrs.getPositionDescription());
        }
        TypedValue v = sa.peekValue(1);
        if (v != null) {
            if (v.type == 3) {
                outBundle.putCharSequence(name, v.coerceToString());
            } else if (v.type == 18) {
                if (v.data == 0) {
                    z = DEBUG_LOAD;
                }
                outBundle.putBoolean(name, z);
            } else if (v.type >= 16 && v.type <= 31) {
                outBundle.putInt(name, v.data);
            } else if (v.type == 4) {
                outBundle.putFloat(name, v.getFloat());
            } else {
                sa.recycle();
                throw new XmlPullParserException("<" + tagName + "> only supports string, integer, float, color, and boolean at " + attrs.getPositionDescription());
            }
            sa.recycle();
            return;
        }
        sa.recycle();
        throw new XmlPullParserException("<" + tagName + "> requires an android:value or android:resource attribute at " + attrs.getPositionDescription());
    }

    public final AssetManager getAssets() {
        return this.mAssets;
    }

    public final void flushLayoutCache() {
        synchronized (this.mCachedXmlBlockIds) {
            int num = this.mCachedXmlBlockIds.length;
            for (int i = LAYOUT_DIR_CONFIG; i < num; i++) {
                this.mCachedXmlBlockIds[i] = LAYOUT_DIR_CONFIG;
                XmlBlock oldBlock = this.mCachedXmlBlocks[i];
                if (oldBlock != null) {
                    oldBlock.close();
                }
                this.mCachedXmlBlocks[i] = null;
            }
        }
    }

    public final void startPreloading() {
        synchronized (sSync) {
            if (sPreloaded) {
                throw new IllegalStateException("Resources already preloaded");
            }
            sPreloaded = true;
            this.mPreloading = true;
            sPreloadedDensity = DisplayMetrics.DENSITY_DEVICE;
            this.mConfiguration.densityDpi = sPreloadedDensity;
            updateConfiguration(null, null);
        }
    }

    public final void finishPreloading() {
        if (this.mPreloading) {
            this.mPreloading = DEBUG_LOAD;
            flushLayoutCache();
        }
    }

    public LongSparseArray<ConstantState> getPreloadedDrawables() {
        return sPreloadedDrawables[LAYOUT_DIR_CONFIG];
    }

    private boolean verifyPreloadConfig(int changingConfigurations, int allowVarying, int resourceId, String name) {
        if (((-1073745921 & changingConfigurations) & (allowVarying ^ -1)) == 0) {
            return true;
        }
        String resName;
        try {
            resName = getResourceName(resourceId);
        } catch (NotFoundException e) {
            resName = "?";
        }
        Log.w(TAG, "Preloaded " + name + " resource #0x" + Integer.toHexString(resourceId) + " (" + resName + ") that varies with configuration!!");
        return DEBUG_LOAD;
    }

    Drawable loadDrawable(TypedValue value, int id, Theme theme) throws NotFoundException {
        boolean isColorDrawable;
        ArrayMap<String, LongSparseArray<WeakReference<ConstantState>>> caches;
        long key;
        ConstantState cs;
        Drawable dr;
        if (value.type < 28 || value.type > 31) {
            isColorDrawable = DEBUG_LOAD;
            caches = this.mDrawableCache;
            key = (((long) value.assetCookie) << 32) | ((long) value.data);
        } else {
            isColorDrawable = true;
            caches = this.mColorDrawableCache;
            key = (long) value.data;
        }
        if (!this.mPreloading) {
            Drawable cachedDrawable = getCachedDrawable(caches, key, theme);
            if (cachedDrawable != null) {
                return cachedDrawable;
            }
        }
        if (isColorDrawable) {
            cs = (ConstantState) sPreloadedColorDrawables.get(key);
        } else {
            cs = (ConstantState) sPreloadedDrawables[this.mConfiguration.getLayoutDirection()].get(key);
        }
        if (cs != null) {
            Drawable clonedDr = cs.newDrawable(this);
            if (theme != null) {
                dr = clonedDr.mutate();
                dr.applyTheme(theme);
                dr.clearMutated();
            } else {
                dr = clonedDr;
            }
        } else if (isColorDrawable) {
            dr = new ColorDrawable(value.data);
        } else {
            dr = loadDrawableForCookie(value, id, theme);
        }
        if (dr != null) {
            dr.setChangingConfigurations(value.changingConfigurations);
            cacheDrawable(value, theme, isColorDrawable, caches, key, dr);
        }
        return dr;
    }

    private void cacheDrawable(TypedValue value, Theme theme, boolean isColorDrawable, ArrayMap<String, LongSparseArray<WeakReference<ConstantState>>> caches, long key, Drawable dr) {
        ConstantState cs = dr.getConstantState();
        if (cs != null) {
            if (this.mPreloading) {
                int changingConfigs = cs.getChangingConfigurations();
                if (isColorDrawable) {
                    if (verifyPreloadConfig(changingConfigs, LAYOUT_DIR_CONFIG, value.resourceId, "drawable")) {
                        sPreloadedColorDrawables.put(key, cs);
                        return;
                    }
                    return;
                } else if (!verifyPreloadConfig(changingConfigs, LAYOUT_DIR_CONFIG, value.resourceId, "drawable")) {
                    return;
                } else {
                    if ((LAYOUT_DIR_CONFIG & changingConfigs) == 0) {
                        sPreloadedDrawables[LAYOUT_DIR_CONFIG].put(key, cs);
                        sPreloadedDrawables[1].put(key, cs);
                        return;
                    }
                    sPreloadedDrawables[this.mConfiguration.getLayoutDirection()].put(key, cs);
                    return;
                }
            }
            synchronized (this.mAccessLock) {
                String themeKey = theme == null ? ProxyInfo.LOCAL_EXCL_LIST : theme.mKey;
                LongSparseArray<WeakReference<ConstantState>> themedCache = (LongSparseArray) caches.get(themeKey);
                if (themedCache == null) {
                    themedCache = new LongSparseArray(1);
                    caches.put(themeKey, themedCache);
                }
                themedCache.put(key, new WeakReference(cs));
            }
        }
    }

    private Drawable loadDrawableForCookie(TypedValue value, int id, Theme theme) {
        if (value.string == null) {
            throw new NotFoundException("Resource \"" + getResourceName(id) + "\" (" + Integer.toHexString(id) + ")  is not a Drawable (color or path): " + value);
        }
        String file = value.string.toString();
        Trace.traceBegin(Trace.TRACE_TAG_RESOURCES, file);
        try {
            Drawable dr;
            if (file.endsWith(".xml")) {
                XmlResourceParser rp = loadXmlResourceParser(file, id, value.assetCookie, "drawable");
                dr = Drawable.createFromXml(this, rp, theme);
                rp.close();
            } else {
                InputStream is = this.mAssets.openNonAsset(value.assetCookie, file, 2);
                dr = Drawable.createFromResourceStream(this, value, is, file, null);
                is.close();
            }
            Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
            return dr;
        } catch (Exception e) {
            Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
            NotFoundException rnf = new NotFoundException("File " + file + " from drawable resource ID #0x" + Integer.toHexString(id));
            rnf.initCause(e);
            throw rnf;
        }
    }

    private Drawable getCachedDrawable(ArrayMap<String, LongSparseArray<WeakReference<ConstantState>>> caches, long key, Theme theme) {
        Drawable themedDrawable;
        synchronized (this.mAccessLock) {
            LongSparseArray<WeakReference<ConstantState>> themedCache = (LongSparseArray) caches.get(theme != null ? theme.mKey : ProxyInfo.LOCAL_EXCL_LIST);
            if (themedCache != null) {
                themedDrawable = getCachedDrawableLocked(themedCache, key);
                if (themedDrawable != null) {
                }
            }
            themedDrawable = null;
        }
        return themedDrawable;
    }

    private ConstantState getConstantStateLocked(LongSparseArray<WeakReference<ConstantState>> drawableCache, long key) {
        WeakReference<ConstantState> wr = (WeakReference) drawableCache.get(key);
        if (wr != null) {
            ConstantState entry = (ConstantState) wr.get();
            if (entry != null) {
                return entry;
            }
            drawableCache.delete(key);
        }
        return null;
    }

    private Drawable getCachedDrawableLocked(LongSparseArray<WeakReference<ConstantState>> drawableCache, long key) {
        ConstantState entry = getConstantStateLocked(drawableCache, key);
        if (entry != null) {
            return entry.newDrawable(this);
        }
        return null;
    }

    ColorStateList loadColorStateList(TypedValue value, int id) throws NotFoundException {
        long key = (((long) value.assetCookie) << 32) | ((long) value.data);
        ColorStateList csl;
        if (value.type < 28 || value.type > 31) {
            csl = getCachedColorStateList(key);
            if (csl != null) {
                return csl;
            }
            csl = (ColorStateList) sPreloadedColorStateLists.get(key);
            if (csl != null) {
                return csl;
            }
            if (value.string == null) {
                throw new NotFoundException("Resource is not a ColorStateList (color or path): " + value);
            }
            String file = value.string.toString();
            if (file.endsWith(".xml")) {
                Trace.traceBegin(Trace.TRACE_TAG_RESOURCES, file);
                try {
                    XmlResourceParser rp = loadXmlResourceParser(file, id, value.assetCookie, "colorstatelist");
                    csl = ColorStateList.createFromXml(this, rp);
                    rp.close();
                    Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
                    if (csl != null) {
                        if (!this.mPreloading) {
                            synchronized (this.mAccessLock) {
                                this.mColorStateListCache.put(key, new WeakReference(csl));
                            }
                        } else if (verifyPreloadConfig(value.changingConfigurations, LAYOUT_DIR_CONFIG, value.resourceId, SubscriptionManager.COLOR)) {
                            sPreloadedColorStateLists.put(key, csl);
                        }
                    }
                    return csl;
                } catch (Exception e) {
                    Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
                    NotFoundException rnf = new NotFoundException("File " + file + " from color state list resource ID #0x" + Integer.toHexString(id));
                    rnf.initCause(e);
                    throw rnf;
                }
            }
            throw new NotFoundException("File " + file + " from drawable resource ID #0x" + Integer.toHexString(id) + ": .xml extension required");
        }
        csl = (ColorStateList) sPreloadedColorStateLists.get(key);
        if (csl != null) {
            return csl;
        }
        csl = ColorStateList.valueOf(value.data);
        if (this.mPreloading && verifyPreloadConfig(value.changingConfigurations, LAYOUT_DIR_CONFIG, value.resourceId, SubscriptionManager.COLOR)) {
            sPreloadedColorStateLists.put(key, csl);
        }
        return csl;
    }

    private ColorStateList getCachedColorStateList(long key) {
        synchronized (this.mAccessLock) {
            WeakReference<ColorStateList> wr = (WeakReference) this.mColorStateListCache.get(key);
            if (wr != null) {
                ColorStateList entry = (ColorStateList) wr.get();
                if (entry != null) {
                    return entry;
                }
                this.mColorStateListCache.delete(key);
            }
            return null;
        }
    }

    XmlResourceParser loadXmlResourceParser(int id, String type) throws NotFoundException {
        XmlResourceParser loadXmlResourceParser;
        synchronized (this.mAccessLock) {
            TypedValue value = this.mTmpValue;
            if (value == null) {
                value = new TypedValue();
                this.mTmpValue = value;
            }
            getValue(id, value, true);
            if (value.type == 3) {
                loadXmlResourceParser = loadXmlResourceParser(value.string.toString(), id, value.assetCookie, type);
            } else {
                throw new NotFoundException("Resource ID #0x" + Integer.toHexString(id) + " type #0x" + Integer.toHexString(value.type) + " is not valid");
            }
        }
        return loadXmlResourceParser;
    }

    XmlResourceParser loadXmlResourceParser(String file, int id, int assetCookie, String type) throws NotFoundException {
        if (id != 0) {
            try {
                synchronized (this.mCachedXmlBlockIds) {
                    XmlResourceParser newParser;
                    int num = this.mCachedXmlBlockIds.length;
                    for (int i = LAYOUT_DIR_CONFIG; i < num; i++) {
                        if (this.mCachedXmlBlockIds[i] == id) {
                            newParser = this.mCachedXmlBlocks[i].newParser();
                            break;
                        }
                    }
                    XmlBlock block = this.mAssets.openXmlBlockAsset(assetCookie, file);
                    if (block != null) {
                        int pos = this.mLastCachedXmlBlockIndex + 1;
                        if (pos >= num) {
                            pos = LAYOUT_DIR_CONFIG;
                        }
                        this.mLastCachedXmlBlockIndex = pos;
                        XmlBlock oldBlock = this.mCachedXmlBlocks[pos];
                        if (oldBlock != null) {
                            oldBlock.close();
                        }
                        this.mCachedXmlBlockIds[pos] = id;
                        this.mCachedXmlBlocks[pos] = block;
                        newParser = block.newParser();
                        return newParser;
                    }
                }
            } catch (Exception e) {
                NotFoundException rnf = new NotFoundException("File " + file + " from xml type " + type + " resource ID #0x" + Integer.toHexString(id));
                rnf.initCause(e);
                throw rnf;
            }
        }
        throw new NotFoundException("File " + file + " from xml type " + type + " resource ID #0x" + Integer.toHexString(id));
    }

    void recycleCachedStyledAttributes(TypedArray attrs) {
        synchronized (this.mAccessLock) {
            TypedArray cached = this.mCachedStyledAttributes;
            if (cached == null || cached.mData.length < attrs.mData.length) {
                this.mCachedStyledAttributes = attrs;
            }
        }
    }

    private Resources() {
        this.mTypedArrayPool = new SynchronizedPool(5);
        this.mAccessLock = new Object();
        this.mTmpConfig = new Configuration();
        this.mDrawableCache = new ArrayMap();
        this.mColorDrawableCache = new ArrayMap();
        this.mColorStateListCache = new LongSparseArray();
        this.mAnimatorCache = new ConfigurationBoundResourceCache(this);
        this.mStateListAnimatorCache = new ConfigurationBoundResourceCache(this);
        this.mTmpValue = new TypedValue();
        this.mCachedStyledAttributes = null;
        this.mLastCachedXmlBlockIndex = -1;
        this.mCachedXmlBlockIds = new int[]{LAYOUT_DIR_CONFIG, LAYOUT_DIR_CONFIG, LAYOUT_DIR_CONFIG, LAYOUT_DIR_CONFIG};
        this.mCachedXmlBlocks = new XmlBlock[4];
        this.mMetrics = new DisplayMetrics();
        this.mConfiguration = new Configuration();
        this.mCompatibilityInfo = CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
        this.mAssets = AssetManager.getSystem();
        this.mConfiguration.setToDefaults();
        this.mMetrics.setToDefaults();
        updateConfiguration(null, null);
        this.mAssets.ensureStringBlocks();
    }
}
