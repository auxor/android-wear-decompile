package android.app;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.AttributeSet;
import android.view.InputQueue;
import android.view.InputQueue.Callback;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback2;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.InputMethodManager;
import java.io.File;

public class NativeActivity extends Activity implements Callback2, Callback, OnGlobalLayoutListener {
    private static final String KEY_NATIVE_SAVED_STATE = "android:native_state";
    public static final String META_DATA_FUNC_NAME = "android.app.func_name";
    public static final String META_DATA_LIB_NAME = "android.app.lib_name";
    private InputQueue mCurInputQueue;
    private SurfaceHolder mCurSurfaceHolder;
    private boolean mDestroyed;
    private boolean mDispatchingUnhandledKey;
    private InputMethodManager mIMM;
    int mLastContentHeight;
    int mLastContentWidth;
    int mLastContentX;
    int mLastContentY;
    final int[] mLocation;
    private NativeContentView mNativeContentView;
    private long mNativeHandle;

    static class NativeContentView extends View {
        NativeActivity mActivity;

        public NativeContentView(Context context) {
            super(context);
        }

        public NativeContentView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
    }

    private native long loadNativeCode(String str, String str2, MessageQueue messageQueue, String str3, String str4, String str5, int i, AssetManager assetManager, byte[] bArr);

    private native void onConfigurationChangedNative(long j);

    private native void onContentRectChangedNative(long j, int i, int i2, int i3, int i4);

    private native void onInputQueueCreatedNative(long j, long j2);

    private native void onInputQueueDestroyedNative(long j, long j2);

    private native void onLowMemoryNative(long j);

    private native void onPauseNative(long j);

    private native void onResumeNative(long j);

    private native byte[] onSaveInstanceStateNative(long j);

    private native void onStartNative(long j);

    private native void onStopNative(long j);

    private native void onSurfaceChangedNative(long j, Surface surface, int i, int i2, int i3);

    private native void onSurfaceCreatedNative(long j, Surface surface);

    private native void onSurfaceDestroyedNative(long j);

    private native void onSurfaceRedrawNeededNative(long j, Surface surface);

    private native void onWindowFocusChangedNative(long j, boolean z);

    private native void unloadNativeCode(long j);

    public NativeActivity() {
        this.mLocation = new int[2];
    }

    protected void onCreate(Bundle savedInstanceState) {
        String libname = "main";
        String funcname = "ANativeActivity_onCreate";
        this.mIMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().takeSurface(this);
        getWindow().takeInputQueue(this);
        getWindow().setFormat(4);
        getWindow().setSoftInputMode(16);
        this.mNativeContentView = new NativeContentView(this);
        this.mNativeContentView.mActivity = this;
        setContentView(this.mNativeContentView);
        this.mNativeContentView.requestFocus();
        this.mNativeContentView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        try {
            ActivityInfo ai = getPackageManager().getActivityInfo(getIntent().getComponent(), AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS);
            if (ai.metaData != null) {
                String ln = ai.metaData.getString(META_DATA_LIB_NAME);
                if (ln != null) {
                    libname = ln;
                }
                ln = ai.metaData.getString(META_DATA_FUNC_NAME);
                if (ln != null) {
                    funcname = ln;
                }
            }
            String path = null;
            File libraryFile = new File(ai.applicationInfo.nativeLibraryDir, System.mapLibraryName(libname));
            if (libraryFile.exists()) {
                path = libraryFile.getPath();
            }
            if (path == null) {
                throw new IllegalArgumentException("Unable to find native library: " + libname);
            }
            byte[] nativeSavedState;
            if (savedInstanceState != null) {
                nativeSavedState = savedInstanceState.getByteArray(KEY_NATIVE_SAVED_STATE);
            } else {
                nativeSavedState = null;
            }
            this.mNativeHandle = loadNativeCode(path, funcname, Looper.myQueue(), getAbsolutePath(getFilesDir()), getAbsolutePath(getObbDir()), getAbsolutePath(getExternalFilesDir(null)), VERSION.SDK_INT, getAssets(), nativeSavedState);
            if (this.mNativeHandle == 0) {
                throw new IllegalArgumentException("Unable to load native library: " + path);
            }
            super.onCreate(savedInstanceState);
        } catch (NameNotFoundException e) {
            throw new RuntimeException("Error getting activity info", e);
        }
    }

    private static String getAbsolutePath(File file) {
        return file != null ? file.getAbsolutePath() : null;
    }

    protected void onDestroy() {
        this.mDestroyed = true;
        if (this.mCurSurfaceHolder != null) {
            onSurfaceDestroyedNative(this.mNativeHandle);
            this.mCurSurfaceHolder = null;
        }
        if (this.mCurInputQueue != null) {
            onInputQueueDestroyedNative(this.mNativeHandle, this.mCurInputQueue.getNativePtr());
            this.mCurInputQueue = null;
        }
        unloadNativeCode(this.mNativeHandle);
        super.onDestroy();
    }

    protected void onPause() {
        super.onPause();
        onPauseNative(this.mNativeHandle);
    }

    protected void onResume() {
        super.onResume();
        onResumeNative(this.mNativeHandle);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        byte[] state = onSaveInstanceStateNative(this.mNativeHandle);
        if (state != null) {
            outState.putByteArray(KEY_NATIVE_SAVED_STATE, state);
        }
    }

    protected void onStart() {
        super.onStart();
        onStartNative(this.mNativeHandle);
    }

    protected void onStop() {
        super.onStop();
        onStopNative(this.mNativeHandle);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!this.mDestroyed) {
            onConfigurationChangedNative(this.mNativeHandle);
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        if (!this.mDestroyed) {
            onLowMemoryNative(this.mNativeHandle);
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!this.mDestroyed) {
            onWindowFocusChangedNative(this.mNativeHandle, hasFocus);
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (!this.mDestroyed) {
            this.mCurSurfaceHolder = holder;
            onSurfaceCreatedNative(this.mNativeHandle, holder.getSurface());
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (!this.mDestroyed) {
            this.mCurSurfaceHolder = holder;
            onSurfaceChangedNative(this.mNativeHandle, holder.getSurface(), format, width, height);
        }
    }

    public void surfaceRedrawNeeded(SurfaceHolder holder) {
        if (!this.mDestroyed) {
            this.mCurSurfaceHolder = holder;
            onSurfaceRedrawNeededNative(this.mNativeHandle, holder.getSurface());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.mCurSurfaceHolder = null;
        if (!this.mDestroyed) {
            onSurfaceDestroyedNative(this.mNativeHandle);
        }
    }

    public void onInputQueueCreated(InputQueue queue) {
        if (!this.mDestroyed) {
            this.mCurInputQueue = queue;
            onInputQueueCreatedNative(this.mNativeHandle, queue.getNativePtr());
        }
    }

    public void onInputQueueDestroyed(InputQueue queue) {
        if (!this.mDestroyed) {
            onInputQueueDestroyedNative(this.mNativeHandle, queue.getNativePtr());
            this.mCurInputQueue = null;
        }
    }

    public void onGlobalLayout() {
        this.mNativeContentView.getLocationInWindow(this.mLocation);
        int w = this.mNativeContentView.getWidth();
        int h = this.mNativeContentView.getHeight();
        if (this.mLocation[0] != this.mLastContentX || this.mLocation[1] != this.mLastContentY || w != this.mLastContentWidth || h != this.mLastContentHeight) {
            this.mLastContentX = this.mLocation[0];
            this.mLastContentY = this.mLocation[1];
            this.mLastContentWidth = w;
            this.mLastContentHeight = h;
            if (!this.mDestroyed) {
                onContentRectChangedNative(this.mNativeHandle, this.mLastContentX, this.mLastContentY, this.mLastContentWidth, this.mLastContentHeight);
            }
        }
    }

    void setWindowFlags(int flags, int mask) {
        getWindow().setFlags(flags, mask);
    }

    void setWindowFormat(int format) {
        getWindow().setFormat(format);
    }

    void showIme(int mode) {
        this.mIMM.showSoftInput(this.mNativeContentView, mode);
    }

    void hideIme(int mode) {
        this.mIMM.hideSoftInputFromWindow(this.mNativeContentView.getWindowToken(), mode);
    }
}
