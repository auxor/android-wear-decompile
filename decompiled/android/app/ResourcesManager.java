package android.app;

import android.content.res.AssetManager;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.ResourcesKey;
import android.hardware.display.DisplayManagerGlobal;
import android.os.IBinder;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.Slog;
import android.view.Display;
import android.view.DisplayAdjustments;
import java.lang.ref.WeakReference;
import java.util.Locale;

public class ResourcesManager {
    static final boolean DEBUG_CACHE = false;
    static final boolean DEBUG_STATS = true;
    static final String TAG = "ResourcesManager";
    private static ResourcesManager sResourcesManager;
    final ArrayMap<ResourcesKey, WeakReference<Resources>> mActiveResources;
    final ArrayMap<DisplayAdjustments, DisplayMetrics> mDefaultDisplayMetrics;
    CompatibilityInfo mResCompatibilityInfo;
    Configuration mResConfiguration;
    final Configuration mTmpConfig;

    public ResourcesManager() {
        this.mActiveResources = new ArrayMap();
        this.mDefaultDisplayMetrics = new ArrayMap();
        this.mTmpConfig = new Configuration();
    }

    public static ResourcesManager getInstance() {
        ResourcesManager resourcesManager;
        synchronized (ResourcesManager.class) {
            if (sResourcesManager == null) {
                sResourcesManager = new ResourcesManager();
            }
            resourcesManager = sResourcesManager;
        }
        return resourcesManager;
    }

    public Configuration getConfiguration() {
        return this.mResConfiguration;
    }

    public void flushDisplayMetricsLocked() {
        this.mDefaultDisplayMetrics.clear();
    }

    public DisplayMetrics getDisplayMetricsLocked(int displayId) {
        return getDisplayMetricsLocked(displayId, DisplayAdjustments.DEFAULT_DISPLAY_ADJUSTMENTS);
    }

    public DisplayMetrics getDisplayMetricsLocked(int displayId, DisplayAdjustments daj) {
        boolean isDefaultDisplay = displayId == 0 ? DEBUG_STATS : DEBUG_CACHE;
        DisplayMetrics dm = isDefaultDisplay ? (DisplayMetrics) this.mDefaultDisplayMetrics.get(daj) : null;
        if (dm != null) {
            return dm;
        }
        dm = new DisplayMetrics();
        DisplayManagerGlobal displayManager = DisplayManagerGlobal.getInstance();
        if (displayManager == null) {
            dm.setToDefaults();
            return dm;
        }
        if (isDefaultDisplay) {
            this.mDefaultDisplayMetrics.put(daj, dm);
        }
        Display d = displayManager.getCompatibleDisplay(displayId, daj);
        if (d != null) {
            d.getMetrics(dm);
        } else {
            dm.setToDefaults();
        }
        return dm;
    }

    final void applyNonDefaultDisplayMetricsToConfigurationLocked(DisplayMetrics dm, Configuration config) {
        config.touchscreen = 1;
        config.densityDpi = dm.densityDpi;
        config.screenWidthDp = (int) (((float) dm.widthPixels) / dm.density);
        config.screenHeightDp = (int) (((float) dm.heightPixels) / dm.density);
        int sl = Configuration.resetScreenLayout(config.screenLayout);
        if (dm.widthPixels > dm.heightPixels) {
            config.orientation = 2;
            config.screenLayout = Configuration.reduceScreenLayout(sl, config.screenWidthDp, config.screenHeightDp);
        } else {
            config.orientation = 1;
            config.screenLayout = Configuration.reduceScreenLayout(sl, config.screenHeightDp, config.screenWidthDp);
        }
        config.smallestScreenWidthDp = config.screenWidthDp;
        config.compatScreenWidthDp = config.screenWidthDp;
        config.compatScreenHeightDp = config.screenHeightDp;
        config.compatSmallestScreenWidthDp = config.smallestScreenWidthDp;
    }

    public boolean applyCompatConfiguration(int displayDensity, Configuration compatConfiguration) {
        if (this.mResCompatibilityInfo == null || this.mResCompatibilityInfo.supportsScreen()) {
            return DEBUG_CACHE;
        }
        this.mResCompatibilityInfo.applyToConfiguration(displayDensity, compatConfiguration);
        return DEBUG_STATS;
    }

    public Resources getTopLevelResources(String resDir, String[] splitResDirs, String[] overlayDirs, String[] libDirs, int displayId, Configuration overrideConfiguration, CompatibilityInfo compatInfo, IBinder token) {
        ResourcesKey key = new ResourcesKey(resDir, displayId, overrideConfiguration, compatInfo.applicationScale, token);
        synchronized (this) {
            WeakReference<Resources> wr = (WeakReference) this.mActiveResources.get(key);
            Resources r = wr != null ? (Resources) wr.get() : null;
            if (r == null || !r.getAssets().isUpToDate()) {
                AssetManager assets = new AssetManager();
                if (resDir != null && assets.addAssetPath(resDir) == 0) {
                    return null;
                }
                Configuration config;
                if (splitResDirs != null) {
                    for (String addAssetPath : splitResDirs) {
                        if (assets.addAssetPath(addAssetPath) == 0) {
                            return null;
                        }
                    }
                }
                if (overlayDirs != null) {
                    for (String addAssetPath2 : overlayDirs) {
                        assets.addOverlayPath(addAssetPath2);
                    }
                }
                if (libDirs != null) {
                    for (String libDir : libDirs) {
                        if (assets.addAssetPath(libDir) == 0) {
                            Slog.w(TAG, "Asset path '" + libDir + "' does not exist or contains no resources.");
                        }
                    }
                }
                DisplayMetrics dm = getDisplayMetricsLocked(displayId);
                boolean isDefaultDisplay = displayId == 0 ? DEBUG_STATS : DEBUG_CACHE;
                boolean hasOverrideConfig = key.hasOverrideConfiguration();
                if (!isDefaultDisplay || hasOverrideConfig) {
                    config = new Configuration(getConfiguration());
                    if (!isDefaultDisplay) {
                        applyNonDefaultDisplayMetricsToConfigurationLocked(dm, config);
                    }
                    if (hasOverrideConfig) {
                        config.updateFrom(key.mOverrideConfiguration);
                    }
                } else {
                    config = getConfiguration();
                }
                r = new Resources(assets, dm, config, compatInfo, token);
                synchronized (this) {
                    wr = (WeakReference) this.mActiveResources.get(key);
                    Resources existing = wr != null ? (Resources) wr.get() : null;
                    if (existing == null || !existing.getAssets().isUpToDate()) {
                        this.mActiveResources.put(key, new WeakReference(r));
                        return r;
                    }
                    r.getAssets().close();
                    return existing;
                }
            }
            return r;
        }
    }

    public final boolean applyConfigurationToResourcesLocked(Configuration config, CompatibilityInfo compat) {
        boolean z = DEBUG_STATS;
        if (this.mResConfiguration == null) {
            this.mResConfiguration = new Configuration();
        }
        if (!this.mResConfiguration.isOtherSeqNewer(config) && compat == null) {
            return DEBUG_CACHE;
        }
        int changes = this.mResConfiguration.updateFrom(config);
        flushDisplayMetricsLocked();
        DisplayMetrics defaultDisplayMetrics = getDisplayMetricsLocked(0);
        if (compat != null && (this.mResCompatibilityInfo == null || !this.mResCompatibilityInfo.equals(compat))) {
            this.mResCompatibilityInfo = compat;
            changes |= 3328;
        }
        if (config.locale != null) {
            Locale.setDefault(config.locale);
        }
        Resources.updateSystemConfiguration(config, defaultDisplayMetrics, compat);
        ApplicationPackageManager.configurationChanged();
        Configuration tmpConfig = null;
        for (int i = this.mActiveResources.size() - 1; i >= 0; i--) {
            ResourcesKey key = (ResourcesKey) this.mActiveResources.keyAt(i);
            Resources r = (Resources) ((WeakReference) this.mActiveResources.valueAt(i)).get();
            if (r != null) {
                boolean isDefaultDisplay;
                int displayId = key.mDisplayId;
                if (displayId == 0) {
                    isDefaultDisplay = DEBUG_STATS;
                } else {
                    isDefaultDisplay = DEBUG_CACHE;
                }
                DisplayMetrics dm = defaultDisplayMetrics;
                boolean hasOverrideConfiguration = key.hasOverrideConfiguration();
                if (!isDefaultDisplay || hasOverrideConfiguration) {
                    if (tmpConfig == null) {
                        tmpConfig = new Configuration();
                    }
                    tmpConfig.setTo(config);
                    if (!isDefaultDisplay) {
                        dm = getDisplayMetricsLocked(displayId);
                        applyNonDefaultDisplayMetricsToConfigurationLocked(dm, tmpConfig);
                    }
                    if (hasOverrideConfiguration) {
                        tmpConfig.updateFrom(key.mOverrideConfiguration);
                    }
                    r.updateConfiguration(tmpConfig, dm, compat);
                } else {
                    r.updateConfiguration(config, dm, compat);
                }
            } else {
                this.mActiveResources.removeAt(i);
            }
        }
        if (changes == 0) {
            z = DEBUG_CACHE;
        }
        return z;
    }
}
