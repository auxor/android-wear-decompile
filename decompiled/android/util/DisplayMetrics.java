package android.util;

import android.os.SystemProperties;

public class DisplayMetrics {
    public static final int DENSITY_280 = 280;
    public static final int DENSITY_400 = 400;
    public static final int DENSITY_560 = 560;
    public static final int DENSITY_DEFAULT = 160;
    public static final float DENSITY_DEFAULT_SCALE = 0.00625f;
    @Deprecated
    public static int DENSITY_DEVICE = 0;
    public static final int DENSITY_HIGH = 240;
    public static final int DENSITY_LOW = 120;
    public static final int DENSITY_MEDIUM = 160;
    public static final int DENSITY_TV = 213;
    public static final int DENSITY_XHIGH = 320;
    public static final int DENSITY_XXHIGH = 480;
    public static final int DENSITY_XXXHIGH = 640;
    public float density;
    public int densityDpi;
    public int heightPixels;
    public float noncompatDensity;
    public int noncompatDensityDpi;
    public int noncompatHeightPixels;
    public float noncompatScaledDensity;
    public int noncompatWidthPixels;
    public float noncompatXdpi;
    public float noncompatYdpi;
    public float scaledDensity;
    public int widthPixels;
    public float xdpi;
    public float ydpi;

    static {
        DENSITY_DEVICE = getDeviceDensity();
    }

    public void setTo(DisplayMetrics o) {
        this.widthPixels = o.widthPixels;
        this.heightPixels = o.heightPixels;
        this.density = o.density;
        this.densityDpi = o.densityDpi;
        this.scaledDensity = o.scaledDensity;
        this.xdpi = o.xdpi;
        this.ydpi = o.ydpi;
        this.noncompatWidthPixels = o.noncompatWidthPixels;
        this.noncompatHeightPixels = o.noncompatHeightPixels;
        this.noncompatDensity = o.noncompatDensity;
        this.noncompatDensityDpi = o.noncompatDensityDpi;
        this.noncompatScaledDensity = o.noncompatScaledDensity;
        this.noncompatXdpi = o.noncompatXdpi;
        this.noncompatYdpi = o.noncompatYdpi;
    }

    public void setToDefaults() {
        this.widthPixels = 0;
        this.heightPixels = 0;
        this.density = ((float) DENSITY_DEVICE) / 160.0f;
        this.densityDpi = DENSITY_DEVICE;
        this.scaledDensity = this.density;
        this.xdpi = (float) DENSITY_DEVICE;
        this.ydpi = (float) DENSITY_DEVICE;
        this.noncompatWidthPixels = this.widthPixels;
        this.noncompatHeightPixels = this.heightPixels;
        this.noncompatDensity = this.density;
        this.noncompatDensityDpi = this.densityDpi;
        this.noncompatScaledDensity = this.scaledDensity;
        this.noncompatXdpi = this.xdpi;
        this.noncompatYdpi = this.ydpi;
    }

    public boolean equals(Object o) {
        return (o instanceof DisplayMetrics) && equals((DisplayMetrics) o);
    }

    public boolean equals(DisplayMetrics other) {
        return equalsPhysical(other) && this.scaledDensity == other.scaledDensity && this.noncompatScaledDensity == other.noncompatScaledDensity;
    }

    public boolean equalsPhysical(DisplayMetrics other) {
        return other != null && this.widthPixels == other.widthPixels && this.heightPixels == other.heightPixels && this.density == other.density && this.densityDpi == other.densityDpi && this.xdpi == other.xdpi && this.ydpi == other.ydpi && this.noncompatWidthPixels == other.noncompatWidthPixels && this.noncompatHeightPixels == other.noncompatHeightPixels && this.noncompatDensity == other.noncompatDensity && this.noncompatDensityDpi == other.noncompatDensityDpi && this.noncompatXdpi == other.noncompatXdpi && this.noncompatYdpi == other.noncompatYdpi;
    }

    public int hashCode() {
        return (this.widthPixels * this.heightPixels) * this.densityDpi;
    }

    public String toString() {
        return "DisplayMetrics{density=" + this.density + ", width=" + this.widthPixels + ", height=" + this.heightPixels + ", scaledDensity=" + this.scaledDensity + ", xdpi=" + this.xdpi + ", ydpi=" + this.ydpi + "}";
    }

    private static int getDeviceDensity() {
        return SystemProperties.getInt("qemu.sf.lcd_density", SystemProperties.getInt("ro.sf.lcd_density", DENSITY_MEDIUM));
    }
}
