package android.view;

import android.content.res.CompatibilityInfo;
import android.os.IBinder;
import java.util.Objects;

public class DisplayAdjustments {
    public static final DisplayAdjustments DEFAULT_DISPLAY_ADJUSTMENTS;
    public static final boolean DEVELOPMENT_RESOURCES_DEPEND_ON_ACTIVITY_TOKEN = false;
    private volatile IBinder mActivityToken;
    private volatile CompatibilityInfo mCompatInfo;

    static {
        DEFAULT_DISPLAY_ADJUSTMENTS = new DisplayAdjustments();
    }

    public DisplayAdjustments() {
        this.mCompatInfo = CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
    }

    public DisplayAdjustments(IBinder token) {
        this.mCompatInfo = CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
        this.mActivityToken = token;
    }

    public DisplayAdjustments(DisplayAdjustments daj) {
        this(daj.getCompatibilityInfo(), daj.getActivityToken());
    }

    public DisplayAdjustments(CompatibilityInfo compatInfo, IBinder token) {
        this.mCompatInfo = CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
        setCompatibilityInfo(compatInfo);
        this.mActivityToken = token;
    }

    public void setCompatibilityInfo(CompatibilityInfo compatInfo) {
        if (this == DEFAULT_DISPLAY_ADJUSTMENTS) {
            throw new IllegalArgumentException("setCompatbilityInfo: Cannot modify DEFAULT_DISPLAY_ADJUSTMENTS");
        } else if (compatInfo == null || (!compatInfo.isScalingRequired() && compatInfo.supportsScreen())) {
            this.mCompatInfo = CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
        } else {
            this.mCompatInfo = compatInfo;
        }
    }

    public CompatibilityInfo getCompatibilityInfo() {
        return this.mCompatInfo;
    }

    public void setActivityToken(IBinder token) {
        if (this == DEFAULT_DISPLAY_ADJUSTMENTS) {
            throw new IllegalArgumentException("setActivityToken: Cannot modify DEFAULT_DISPLAY_ADJUSTMENTS");
        }
        this.mActivityToken = token;
    }

    public IBinder getActivityToken() {
        return this.mActivityToken;
    }

    public int hashCode() {
        return this.mCompatInfo.hashCode() + 527;
    }

    public boolean equals(Object o) {
        if (!(o instanceof DisplayAdjustments)) {
            return false;
        }
        DisplayAdjustments daj = (DisplayAdjustments) o;
        if (Objects.equals(daj.mCompatInfo, this.mCompatInfo) && Objects.equals(daj.mActivityToken, this.mActivityToken)) {
            return true;
        }
        return false;
    }
}
