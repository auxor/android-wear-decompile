package com.android.internal.policy;

import android.content.Context;
import android.view.FallbackEventHandler;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManagerPolicy;

public interface IPolicy {
    FallbackEventHandler makeNewFallbackEventHandler(Context context);

    LayoutInflater makeNewLayoutInflater(Context context);

    Window makeNewWindow(Context context);

    WindowManagerPolicy makeNewWindowManager();
}
