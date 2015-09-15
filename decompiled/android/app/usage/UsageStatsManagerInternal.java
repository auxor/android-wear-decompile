package android.app.usage;

import android.content.ComponentName;
import android.content.res.Configuration;

public abstract class UsageStatsManagerInternal {
    public abstract void prepareShutdown();

    public abstract void reportConfigurationChange(Configuration configuration, int i);

    public abstract void reportEvent(ComponentName componentName, int i, int i2);
}
