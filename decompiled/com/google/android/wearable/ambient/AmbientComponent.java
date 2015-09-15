package com.google.android.wearable.ambient;

import android.content.ComponentName;
import android.os.Bundle;

public class AmbientComponent {
    private static final String COMPONENT = "component";
    private final Bundle mBundle;

    public AmbientComponent(ComponentName component) {
        this.mBundle = new Bundle();
        this.mBundle.putParcelable(COMPONENT, component);
    }

    public AmbientComponent(Bundle bundle) {
        this.mBundle = bundle;
    }

    public String toString() {
        return "AmbientComponentConfig[" + "component=" + this.mBundle.get(COMPONENT) + "]";
    }

    public ComponentName getComponent() {
        return (ComponentName) this.mBundle.getParcelable(COMPONENT);
    }

    public Bundle getBundle() {
        return this.mBundle;
    }
}
