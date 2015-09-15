package com.google.android.wearable.ambient;

import android.os.Bundle;

public class AmbientConfiguration {
    private static final String AMBIENT_ENABLED = "ambient_enabled";
    private final Bundle mBundle;

    public AmbientConfiguration() {
        this.mBundle = new Bundle();
    }

    public static AmbientConfiguration create() {
        return new AmbientConfiguration();
    }

    public static AmbientConfiguration fromBundle(Bundle bundle) {
        return new AmbientConfiguration(bundle);
    }

    private AmbientConfiguration(Bundle bundle) {
        this.mBundle = new Bundle();
        this.mBundle.putBoolean(AMBIENT_ENABLED, bundle.getBoolean(AMBIENT_ENABLED));
    }

    public String toString() {
        return "AmbientConfiguration[" + ", ambientEnabled=" + this.mBundle.get(AMBIENT_ENABLED) + "]";
    }

    public void setAmbientEnabled(boolean enabled) {
        this.mBundle.putBoolean(AMBIENT_ENABLED, enabled);
    }

    public boolean isAmbientEnabled() {
        return this.mBundle.getBoolean(AMBIENT_ENABLED);
    }

    public Bundle getBundle() {
        return this.mBundle;
    }
}
