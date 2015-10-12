package com.google.android.wearable.ambient;

import android.os.Bundle;
import com.google.android.clockwork.settings.AmbientConfig;
import com.google.android.clockwork.settings.BurnInConfig;

final class AmbientDetails {
    public static Bundle create(BurnInConfig burnInConfig, AmbientConfig ambientConfig) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("com.google.android.wearable.compat.extra.LOWBIT_AMBIENT", ambientConfig.isLowBitEnabled());
        bundle.putBoolean("com.google.android.wearable.compat.extra.BURN_IN_PROTECTION", burnInConfig.isProtectionEnabled());
        return bundle;
    }
}
