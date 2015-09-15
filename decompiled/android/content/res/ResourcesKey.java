package android.content.res;

import android.os.IBinder;

public final class ResourcesKey {
    public final int mDisplayId;
    private final int mHash;
    public final Configuration mOverrideConfiguration;
    final String mResDir;
    final float mScale;
    private final IBinder mToken;

    public ResourcesKey(String resDir, int displayId, Configuration overrideConfiguration, float scale, IBinder token) {
        int i = 0;
        this.mOverrideConfiguration = new Configuration();
        this.mResDir = resDir;
        this.mDisplayId = displayId;
        if (overrideConfiguration != null) {
            this.mOverrideConfiguration.setTo(overrideConfiguration);
        }
        this.mScale = scale;
        this.mToken = token;
        int hashCode = ((((this.mResDir == null ? 0 : this.mResDir.hashCode()) + 527) * 31) + this.mDisplayId) * 31;
        if (this.mOverrideConfiguration != null) {
            i = this.mOverrideConfiguration.hashCode();
        }
        this.mHash = ((hashCode + i) * 31) + Float.floatToIntBits(this.mScale);
    }

    public boolean hasOverrideConfiguration() {
        return !Configuration.EMPTY.equals(this.mOverrideConfiguration);
    }

    public int hashCode() {
        return this.mHash;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ResourcesKey)) {
            return false;
        }
        ResourcesKey peer = (ResourcesKey) obj;
        if (this.mResDir == null && peer.mResDir != null) {
            return false;
        }
        if (this.mResDir != null && peer.mResDir == null) {
            return false;
        }
        if ((this.mResDir != null && peer.mResDir != null && !this.mResDir.equals(peer.mResDir)) || this.mDisplayId != peer.mDisplayId) {
            return false;
        }
        if ((this.mOverrideConfiguration == peer.mOverrideConfiguration || (this.mOverrideConfiguration != null && peer.mOverrideConfiguration != null && this.mOverrideConfiguration.equals(peer.mOverrideConfiguration))) && this.mScale == peer.mScale) {
            return true;
        }
        return false;
    }

    public String toString() {
        return Integer.toHexString(this.mHash);
    }
}
