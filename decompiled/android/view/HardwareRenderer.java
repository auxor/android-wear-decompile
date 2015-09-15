package android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.Surface.OutOfResourcesException;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public abstract class HardwareRenderer {
    private static final String CACHE_PATH_SHADERS = "com.android.opengl.shaders_cache";
    public static final String DEBUG_DIRTY_REGIONS_PROPERTY = "debug.hwui.show_dirty_regions";
    public static final String DEBUG_OVERDRAW_PROPERTY = "debug.hwui.overdraw";
    public static final String DEBUG_SHOW_LAYERS_UPDATES_PROPERTY = "debug.hwui.show_layers_updates";
    public static final String DEBUG_SHOW_NON_RECTANGULAR_CLIP_PROPERTY = "debug.hwui.show_non_rect_clip";
    static final String LOG_TAG = "HardwareRenderer";
    public static final String OVERDRAW_PROPERTY_SHOW = "show";
    static final String PRINT_CONFIG_PROPERTY = "debug.hwui.print_config";
    static final String PROFILE_MAXFRAMES_PROPERTY = "debug.hwui.profile.maxframes";
    public static final String PROFILE_PROPERTY = "debug.hwui.profile";
    public static final String PROFILE_PROPERTY_VISUALIZE_BARS = "visual_bars";
    static final String RENDER_DIRTY_REGIONS_PROPERTY = "debug.hwui.render_dirty_regions";
    public static boolean sRendererDisabled;
    public static boolean sSystemRendererDisabled;
    public static boolean sTrimForeground;
    private boolean mEnabled;
    private boolean mRequested;

    interface HardwareDrawCallbacks {
        void onHardwarePostDraw(HardwareCanvas hardwareCanvas);

        void onHardwarePreDraw(HardwareCanvas hardwareCanvas);
    }

    abstract void buildLayer(RenderNode renderNode);

    abstract boolean copyLayerInto(HardwareLayer hardwareLayer, Bitmap bitmap);

    abstract HardwareLayer createTextureLayer();

    abstract void destroy();

    abstract void destroyHardwareResources(View view);

    abstract void detachSurfaceTexture(long j);

    abstract void draw(View view, AttachInfo attachInfo, HardwareDrawCallbacks hardwareDrawCallbacks);

    abstract void dumpGfxInfo(PrintWriter printWriter, FileDescriptor fileDescriptor);

    abstract void fence();

    abstract int getHeight();

    abstract int getWidth();

    abstract boolean initialize(Surface surface) throws OutOfResourcesException;

    abstract void invalidate(Surface surface);

    abstract void invalidateRoot();

    abstract boolean loadSystemProperties();

    abstract void notifyFramePending();

    abstract void onLayerDestroyed(HardwareLayer hardwareLayer);

    abstract boolean pauseSurface(Surface surface);

    abstract void pushLayerUpdate(HardwareLayer hardwareLayer);

    abstract void registerAnimatingRenderNode(RenderNode renderNode);

    abstract void setName(String str);

    abstract void setOpaque(boolean z);

    abstract void setup(int i, int i2, Rect rect);

    abstract void stopDrawing();

    abstract void updateSurface(Surface surface) throws OutOfResourcesException;

    public HardwareRenderer() {
        this.mRequested = true;
    }

    static {
        sRendererDisabled = false;
        sSystemRendererDisabled = false;
        sTrimForeground = false;
    }

    public static void disable(boolean system) {
        sRendererDisabled = true;
        if (system) {
            sSystemRendererDisabled = true;
        }
    }

    public static void enableForegroundTrimming() {
        sTrimForeground = true;
    }

    public static boolean isAvailable() {
        return GLES20Canvas.isAvailable();
    }

    public static void setupDiskCache(File cacheDir) {
        ThreadedRenderer.setupShadersDiskCache(new File(cacheDir, CACHE_PATH_SHADERS).getAbsolutePath());
    }

    boolean initializeIfNeeded(int width, int height, Surface surface, Rect surfaceInsets) throws OutOfResourcesException {
        if (!isRequested() || isEnabled() || !initialize(surface)) {
            return false;
        }
        setup(width, height, surfaceInsets);
        return true;
    }

    static HardwareRenderer create(Context context, boolean translucent) {
        if (GLES20Canvas.isAvailable()) {
            return new ThreadedRenderer(context, translucent);
        }
        return null;
    }

    static void trimMemory(int level) {
        ThreadedRenderer.trimMemory(level);
    }

    boolean isEnabled() {
        return this.mEnabled;
    }

    void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }

    boolean isRequested() {
        return this.mRequested;
    }

    void setRequested(boolean requested) {
        this.mRequested = requested;
    }
}
