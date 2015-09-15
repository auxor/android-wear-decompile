package android.view;

import android.R;
import android.animation.StateListAnimator;
import android.app.admin.DevicePolicyManager;
import android.content.ClipData;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Canvas.EdgeType;
import android.graphics.Insets;
import android.graphics.Interpolator;
import android.graphics.Interpolator.Result;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Region.Op;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.hardware.display.DisplayManagerGlobal;
import android.net.ProxyInfo;
import android.nfc.tech.MifareClassic;
import android.opengl.GLES10;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.service.notification.ZenModeConfig;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatProperty;
import android.util.Log;
import android.util.LongSparseLongArray;
import android.util.Pools.SynchronizedPool;
import android.util.Property;
import android.util.SparseArray;
import android.util.SuperNotCalledException;
import android.util.TypedValue;
import android.view.AccessibilityIterators.TextSegmentIterator;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent.DispatcherState;
import android.view.ViewDebug.CapturedViewProperty;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.FlagToString;
import android.view.ViewDebug.IntToString;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.InternalInsetsInfo;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityEventSource;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListView;
import android.widget.ScrollBarDrawable;
import com.android.internal.util.Predicate;
import com.android.internal.view.menu.MenuBuilder;
import com.google.android.collect.Lists;
import com.google.android.collect.Maps;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class View implements Callback, KeyEvent.Callback, AccessibilityEventSource {
    public static final int ACCESSIBILITY_CURSOR_POSITION_UNDEFINED = -1;
    public static final int ACCESSIBILITY_LIVE_REGION_ASSERTIVE = 2;
    static final int ACCESSIBILITY_LIVE_REGION_DEFAULT = 0;
    public static final int ACCESSIBILITY_LIVE_REGION_NONE = 0;
    public static final int ACCESSIBILITY_LIVE_REGION_POLITE = 1;
    static final int ALL_RTL_PROPERTIES_RESOLVED = 1610678816;
    public static final Property<View, Float> ALPHA = null;
    static final int CLICKABLE = 16384;
    private static final boolean DBG = false;
    public static final String DEBUG_LAYOUT_PROPERTY = "debug.layout";
    static final int DISABLED = 32;
    public static final int DRAG_FLAG_GLOBAL = 1;
    static final int DRAG_MASK = 3;
    static final int DRAWING_CACHE_ENABLED = 32768;
    public static final int DRAWING_CACHE_QUALITY_AUTO = 0;
    private static final int[] DRAWING_CACHE_QUALITY_FLAGS = null;
    public static final int DRAWING_CACHE_QUALITY_HIGH = 1048576;
    public static final int DRAWING_CACHE_QUALITY_LOW = 524288;
    static final int DRAWING_CACHE_QUALITY_MASK = 1572864;
    static final int DRAW_MASK = 128;
    static final int DUPLICATE_PARENT_STATE = 4194304;
    protected static final int[] EMPTY_STATE_SET = null;
    static final int ENABLED = 0;
    protected static final int[] ENABLED_FOCUSED_SELECTED_STATE_SET = null;
    protected static final int[] ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = null;
    protected static final int[] ENABLED_FOCUSED_STATE_SET = null;
    protected static final int[] ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET = null;
    static final int ENABLED_MASK = 32;
    protected static final int[] ENABLED_SELECTED_STATE_SET = null;
    protected static final int[] ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET = null;
    protected static final int[] ENABLED_STATE_SET = null;
    protected static final int[] ENABLED_WINDOW_FOCUSED_STATE_SET = null;
    static final int FADING_EDGE_HORIZONTAL = 4096;
    static final int FADING_EDGE_MASK = 12288;
    static final int FADING_EDGE_NONE = 0;
    static final int FADING_EDGE_VERTICAL = 8192;
    static final int FILTER_TOUCHES_WHEN_OBSCURED = 1024;
    public static final int FIND_VIEWS_WITH_ACCESSIBILITY_NODE_PROVIDERS = 4;
    public static final int FIND_VIEWS_WITH_CONTENT_DESCRIPTION = 2;
    public static final int FIND_VIEWS_WITH_TEXT = 1;
    private static final int FITS_SYSTEM_WINDOWS = 2;
    private static final int FOCUSABLE = 1;
    public static final int FOCUSABLES_ALL = 0;
    public static final int FOCUSABLES_TOUCH_MODE = 1;
    static final int FOCUSABLE_IN_TOUCH_MODE = 262144;
    private static final int FOCUSABLE_MASK = 1;
    protected static final int[] FOCUSED_SELECTED_STATE_SET = null;
    protected static final int[] FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = null;
    protected static final int[] FOCUSED_STATE_SET = null;
    protected static final int[] FOCUSED_WINDOW_FOCUSED_STATE_SET = null;
    public static final int FOCUS_BACKWARD = 1;
    public static final int FOCUS_DOWN = 130;
    public static final int FOCUS_FORWARD = 2;
    public static final int FOCUS_LEFT = 17;
    public static final int FOCUS_RIGHT = 66;
    public static final int FOCUS_UP = 33;
    public static final int GONE = 8;
    public static final int HAPTIC_FEEDBACK_ENABLED = 268435456;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_AUTO = 0;
    static final int IMPORTANT_FOR_ACCESSIBILITY_DEFAULT = 0;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_NO = 2;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS = 4;
    public static final int IMPORTANT_FOR_ACCESSIBILITY_YES = 1;
    public static final int INVISIBLE = 4;
    public static final int KEEP_SCREEN_ON = 67108864;
    public static final int LAYER_TYPE_HARDWARE = 2;
    public static final int LAYER_TYPE_NONE = 0;
    public static final int LAYER_TYPE_SOFTWARE = 1;
    private static final int LAYOUT_DIRECTION_DEFAULT = 2;
    private static final int[] LAYOUT_DIRECTION_FLAGS = null;
    public static final int LAYOUT_DIRECTION_INHERIT = 2;
    public static final int LAYOUT_DIRECTION_LOCALE = 3;
    public static final int LAYOUT_DIRECTION_LTR = 0;
    static final int LAYOUT_DIRECTION_RESOLVED_DEFAULT = 0;
    public static final int LAYOUT_DIRECTION_RTL = 1;
    static final int LONG_CLICKABLE = 2097152;
    public static final int MEASURED_HEIGHT_STATE_SHIFT = 16;
    public static final int MEASURED_SIZE_MASK = 16777215;
    public static final int MEASURED_STATE_MASK = -16777216;
    public static final int MEASURED_STATE_TOO_SMALL = 16777216;
    public static final int NAVIGATION_BAR_TRANSIENT = 134217728;
    public static final int NAVIGATION_BAR_TRANSLUCENT = Integer.MIN_VALUE;
    public static final int NAVIGATION_BAR_UNHIDE = 536870912;
    private static final int NOT_FOCUSABLE = 0;
    public static final int NO_ID = -1;
    static final int OPTIONAL_FITS_SYSTEM_WINDOWS = 2048;
    public static final int OVER_SCROLL_ALWAYS = 0;
    public static final int OVER_SCROLL_IF_CONTENT_SCROLLS = 1;
    public static final int OVER_SCROLL_NEVER = 2;
    static final int PARENT_SAVE_DISABLED = 536870912;
    static final int PARENT_SAVE_DISABLED_MASK = 536870912;
    static final int PFLAG2_ACCESSIBILITY_FOCUSED = 67108864;
    static final int PFLAG2_ACCESSIBILITY_LIVE_REGION_MASK = 25165824;
    static final int PFLAG2_ACCESSIBILITY_LIVE_REGION_SHIFT = 23;
    static final int PFLAG2_DRAG_CAN_ACCEPT = 1;
    static final int PFLAG2_DRAG_HOVERED = 2;
    static final int PFLAG2_DRAWABLE_RESOLVED = 1073741824;
    static final int PFLAG2_HAS_TRANSIENT_STATE = Integer.MIN_VALUE;
    static final int PFLAG2_IMPORTANT_FOR_ACCESSIBILITY_MASK = 7340032;
    static final int PFLAG2_IMPORTANT_FOR_ACCESSIBILITY_SHIFT = 20;
    static final int PFLAG2_LAYOUT_DIRECTION_MASK = 12;
    static final int PFLAG2_LAYOUT_DIRECTION_MASK_SHIFT = 2;
    static final int PFLAG2_LAYOUT_DIRECTION_RESOLVED = 32;
    static final int PFLAG2_LAYOUT_DIRECTION_RESOLVED_MASK = 48;
    static final int PFLAG2_LAYOUT_DIRECTION_RESOLVED_RTL = 16;
    static final int PFLAG2_PADDING_RESOLVED = 536870912;
    static final int PFLAG2_SUBTREE_ACCESSIBILITY_STATE_CHANGED = 134217728;
    private static final int[] PFLAG2_TEXT_ALIGNMENT_FLAGS = null;
    static final int PFLAG2_TEXT_ALIGNMENT_MASK = 57344;
    static final int PFLAG2_TEXT_ALIGNMENT_MASK_SHIFT = 13;
    static final int PFLAG2_TEXT_ALIGNMENT_RESOLVED = 65536;
    private static final int PFLAG2_TEXT_ALIGNMENT_RESOLVED_DEFAULT = 131072;
    static final int PFLAG2_TEXT_ALIGNMENT_RESOLVED_MASK = 917504;
    static final int PFLAG2_TEXT_ALIGNMENT_RESOLVED_MASK_SHIFT = 17;
    private static final int[] PFLAG2_TEXT_DIRECTION_FLAGS = null;
    static final int PFLAG2_TEXT_DIRECTION_MASK = 448;
    static final int PFLAG2_TEXT_DIRECTION_MASK_SHIFT = 6;
    static final int PFLAG2_TEXT_DIRECTION_RESOLVED = 512;
    static final int PFLAG2_TEXT_DIRECTION_RESOLVED_DEFAULT = 1024;
    static final int PFLAG2_TEXT_DIRECTION_RESOLVED_MASK = 7168;
    static final int PFLAG2_TEXT_DIRECTION_RESOLVED_MASK_SHIFT = 10;
    static final int PFLAG2_VIEW_QUICK_REJECTED = 268435456;
    static final int PFLAG3_APPLYING_INSETS = 32;
    static final int PFLAG3_CALLED_SUPER = 16;
    static final int PFLAG3_FITTING_SYSTEM_WINDOWS = 64;
    static final int PFLAG3_IS_LAID_OUT = 4;
    static final int PFLAG3_MEASURE_NEEDED_BEFORE_LAYOUT = 8;
    static final int PFLAG3_NESTED_SCROLLING_ENABLED = 128;
    static final int PFLAG3_VIEW_IS_ANIMATING_ALPHA = 2;
    static final int PFLAG3_VIEW_IS_ANIMATING_TRANSFORM = 1;
    static final int PFLAG_ACTIVATED = 1073741824;
    static final int PFLAG_ALPHA_SET = 262144;
    static final int PFLAG_ANIMATION_STARTED = 65536;
    private static final int PFLAG_AWAKEN_SCROLL_BARS_ON_ATTACH = 134217728;
    static final int PFLAG_CANCEL_NEXT_UP_EVENT = 67108864;
    static final int PFLAG_DIRTY = 2097152;
    static final int PFLAG_DIRTY_MASK = 6291456;
    static final int PFLAG_DIRTY_OPAQUE = 4194304;
    private static final int PFLAG_DOES_NOTHING_REUSE_PLEASE = 536870912;
    static final int PFLAG_DRAWABLE_STATE_DIRTY = 1024;
    static final int PFLAG_DRAWING_CACHE_VALID = 32768;
    static final int PFLAG_DRAWN = 32;
    static final int PFLAG_DRAW_ANIMATION = 64;
    static final int PFLAG_FOCUSED = 2;
    static final int PFLAG_FORCE_LAYOUT = 4096;
    static final int PFLAG_HAS_BOUNDS = 16;
    private static final int PFLAG_HOVERED = 268435456;
    static final int PFLAG_INVALIDATED = Integer.MIN_VALUE;
    static final int PFLAG_IS_ROOT_NAMESPACE = 8;
    static final int PFLAG_LAYOUT_REQUIRED = 8192;
    static final int PFLAG_MEASURED_DIMENSION_SET = 2048;
    static final int PFLAG_ONLY_DRAWS_BACKGROUND = 256;
    static final int PFLAG_OPAQUE_BACKGROUND = 8388608;
    static final int PFLAG_OPAQUE_MASK = 25165824;
    static final int PFLAG_OPAQUE_SCROLLBARS = 16777216;
    private static final int PFLAG_PREPRESSED = 33554432;
    private static final int PFLAG_PRESSED = 16384;
    static final int PFLAG_REQUEST_TRANSPARENT_REGIONS = 512;
    private static final int PFLAG_SAVE_STATE_CALLED = 131072;
    static final int PFLAG_SCROLL_CONTAINER = 524288;
    static final int PFLAG_SCROLL_CONTAINER_ADDED = 1048576;
    static final int PFLAG_SELECTED = 4;
    static final int PFLAG_SKIP_DRAW = 128;
    static final int PFLAG_WANTS_FOCUS = 1;
    private static final int POPULATING_ACCESSIBILITY_EVENT_TYPES = 172479;
    protected static final int[] PRESSED_ENABLED_FOCUSED_SELECTED_STATE_SET = null;
    protected static final int[] PRESSED_ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = null;
    protected static final int[] PRESSED_ENABLED_FOCUSED_STATE_SET = null;
    protected static final int[] PRESSED_ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET = null;
    protected static final int[] PRESSED_ENABLED_SELECTED_STATE_SET = null;
    protected static final int[] PRESSED_ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET = null;
    protected static final int[] PRESSED_ENABLED_STATE_SET = null;
    protected static final int[] PRESSED_ENABLED_WINDOW_FOCUSED_STATE_SET = null;
    protected static final int[] PRESSED_FOCUSED_SELECTED_STATE_SET = null;
    protected static final int[] PRESSED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = null;
    protected static final int[] PRESSED_FOCUSED_STATE_SET = null;
    protected static final int[] PRESSED_FOCUSED_WINDOW_FOCUSED_STATE_SET = null;
    protected static final int[] PRESSED_SELECTED_STATE_SET = null;
    protected static final int[] PRESSED_SELECTED_WINDOW_FOCUSED_STATE_SET = null;
    protected static final int[] PRESSED_STATE_SET = null;
    protected static final int[] PRESSED_WINDOW_FOCUSED_STATE_SET = null;
    private static final int PROVIDER_BACKGROUND = 0;
    private static final int PROVIDER_BOUNDS = 2;
    private static final int PROVIDER_NONE = 1;
    private static final int PROVIDER_PADDED_BOUNDS = 3;
    public static final int PUBLIC_STATUS_BAR_VISIBILITY_MASK = 16383;
    public static final int RECENT_APPS_VISIBLE = 16384;
    public static final Property<View, Float> ROTATION = null;
    public static final Property<View, Float> ROTATION_X = null;
    public static final Property<View, Float> ROTATION_Y = null;
    static final int SAVE_DISABLED = 65536;
    static final int SAVE_DISABLED_MASK = 65536;
    public static final Property<View, Float> SCALE_X = null;
    public static final Property<View, Float> SCALE_Y = null;
    public static final int SCREEN_STATE_OFF = 0;
    public static final int SCREEN_STATE_ON = 1;
    static final int SCROLLBARS_HORIZONTAL = 256;
    static final int SCROLLBARS_INSET_MASK = 16777216;
    public static final int SCROLLBARS_INSIDE_INSET = 16777216;
    public static final int SCROLLBARS_INSIDE_OVERLAY = 0;
    static final int SCROLLBARS_MASK = 768;
    static final int SCROLLBARS_NONE = 0;
    public static final int SCROLLBARS_OUTSIDE_INSET = 50331648;
    static final int SCROLLBARS_OUTSIDE_MASK = 33554432;
    public static final int SCROLLBARS_OUTSIDE_OVERLAY = 33554432;
    static final int SCROLLBARS_STYLE_MASK = 50331648;
    static final int SCROLLBARS_VERTICAL = 512;
    public static final int SCROLLBAR_POSITION_DEFAULT = 0;
    public static final int SCROLLBAR_POSITION_LEFT = 1;
    public static final int SCROLLBAR_POSITION_RIGHT = 2;
    public static final int SCROLL_AXIS_HORIZONTAL = 1;
    public static final int SCROLL_AXIS_NONE = 0;
    public static final int SCROLL_AXIS_VERTICAL = 2;
    protected static final int[] SELECTED_STATE_SET = null;
    protected static final int[] SELECTED_WINDOW_FOCUSED_STATE_SET = null;
    public static final int SOUND_EFFECTS_ENABLED = 134217728;
    public static final int STATUS_BAR_DISABLE_BACK = 4194304;
    public static final int STATUS_BAR_DISABLE_CLOCK = 8388608;
    public static final int STATUS_BAR_DISABLE_EXPAND = 65536;
    public static final int STATUS_BAR_DISABLE_HOME = 2097152;
    public static final int STATUS_BAR_DISABLE_NOTIFICATION_ALERTS = 262144;
    public static final int STATUS_BAR_DISABLE_NOTIFICATION_ICONS = 131072;
    public static final int STATUS_BAR_DISABLE_NOTIFICATION_TICKER = 524288;
    public static final int STATUS_BAR_DISABLE_RECENT = 16777216;
    public static final int STATUS_BAR_DISABLE_SEARCH = 33554432;
    public static final int STATUS_BAR_DISABLE_SYSTEM_INFO = 1048576;
    public static final int STATUS_BAR_HIDDEN = 1;
    public static final int STATUS_BAR_TRANSIENT = 67108864;
    public static final int STATUS_BAR_TRANSLUCENT = 1073741824;
    public static final int STATUS_BAR_UNHIDE = 268435456;
    public static final int STATUS_BAR_VISIBLE = 0;
    public static final int SYSTEM_UI_CLEARABLE_FLAGS = 7;
    public static final int SYSTEM_UI_FLAG_FULLSCREEN = 4;
    public static final int SYSTEM_UI_FLAG_HIDE_NAVIGATION = 2;
    public static final int SYSTEM_UI_FLAG_IMMERSIVE = 2048;
    public static final int SYSTEM_UI_FLAG_IMMERSIVE_STICKY = 4096;
    public static final int SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN = 1024;
    public static final int SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION = 512;
    public static final int SYSTEM_UI_FLAG_LAYOUT_STABLE = 256;
    public static final int SYSTEM_UI_FLAG_LOW_PROFILE = 1;
    public static final int SYSTEM_UI_FLAG_VISIBLE = 0;
    public static final int SYSTEM_UI_LAYOUT_FLAGS = 1536;
    public static final int SYSTEM_UI_TRANSPARENT = 32768;
    public static final int TEXT_ALIGNMENT_CENTER = 4;
    private static final int TEXT_ALIGNMENT_DEFAULT = 1;
    public static final int TEXT_ALIGNMENT_GRAVITY = 1;
    public static final int TEXT_ALIGNMENT_INHERIT = 0;
    static final int TEXT_ALIGNMENT_RESOLVED_DEFAULT = 1;
    public static final int TEXT_ALIGNMENT_TEXT_END = 3;
    public static final int TEXT_ALIGNMENT_TEXT_START = 2;
    public static final int TEXT_ALIGNMENT_VIEW_END = 6;
    public static final int TEXT_ALIGNMENT_VIEW_START = 5;
    public static final int TEXT_DIRECTION_ANY_RTL = 2;
    private static final int TEXT_DIRECTION_DEFAULT = 0;
    public static final int TEXT_DIRECTION_FIRST_STRONG = 1;
    public static final int TEXT_DIRECTION_INHERIT = 0;
    public static final int TEXT_DIRECTION_LOCALE = 5;
    public static final int TEXT_DIRECTION_LTR = 3;
    static final int TEXT_DIRECTION_RESOLVED_DEFAULT = 1;
    public static final int TEXT_DIRECTION_RTL = 4;
    public static final Property<View, Float> TRANSLATION_X = null;
    public static final Property<View, Float> TRANSLATION_Y = null;
    public static final Property<View, Float> TRANSLATION_Z = null;
    private static final int UNDEFINED_PADDING = Integer.MIN_VALUE;
    protected static final String VIEW_LOG_TAG = "View";
    static final int VIEW_STATE_ACCELERATED = 64;
    static final int VIEW_STATE_ACTIVATED = 32;
    static final int VIEW_STATE_DRAG_CAN_ACCEPT = 256;
    static final int VIEW_STATE_DRAG_HOVERED = 512;
    static final int VIEW_STATE_ENABLED = 8;
    static final int VIEW_STATE_FOCUSED = 4;
    static final int VIEW_STATE_HOVERED = 128;
    static final int[] VIEW_STATE_IDS = null;
    static final int VIEW_STATE_PRESSED = 16;
    static final int VIEW_STATE_SELECTED = 2;
    private static final int[][] VIEW_STATE_SETS = null;
    static final int VIEW_STATE_WINDOW_FOCUSED = 1;
    private static final int[] VISIBILITY_FLAGS = null;
    static final int VISIBILITY_MASK = 12;
    public static final int VISIBLE = 0;
    static final int WILL_NOT_CACHE_DRAWING = 131072;
    static final int WILL_NOT_DRAW = 128;
    protected static final int[] WINDOW_FOCUSED_STATE_SET = null;
    public static final Property<View, Float> X = null;
    public static final Property<View, Float> Y = null;
    public static final Property<View, Float> Z = null;
    private static SparseArray<String> mAttributeMap;
    public static boolean mDebugViewAttributes;
    private static boolean sCompatibilityDone;
    private static boolean sIgnoreMeasureCache;
    private static int sNextAccessibilityViewId;
    private static final AtomicInteger sNextGeneratedId = null;
    static final ThreadLocal<Rect> sThreadLocal = null;
    private static boolean sUseBrokenMakeMeasureSpec;
    private int mAccessibilityCursorPosition;
    AccessibilityDelegate mAccessibilityDelegate;
    private int mAccessibilityTraversalAfterId;
    private int mAccessibilityTraversalBeforeId;
    int mAccessibilityViewId;
    private ViewPropertyAnimator mAnimator;
    AttachInfo mAttachInfo;
    @ExportedProperty(category = "attributes", hasAdjacentMapping = true)
    public String[] mAttributes;
    @ExportedProperty(deepExport = true, prefix = "bg_")
    private Drawable mBackground;
    private RenderNode mBackgroundRenderNode;
    private int mBackgroundResource;
    private boolean mBackgroundSizeChanged;
    private TintInfo mBackgroundTint;
    @ExportedProperty(category = "layout")
    protected int mBottom;
    public boolean mCachingFailed;
    Rect mClipBounds;
    private CharSequence mContentDescription;
    @ExportedProperty(deepExport = true)
    protected Context mContext;
    protected Animation mCurrentAnimation;
    private int[] mDrawableState;
    private Bitmap mDrawingCache;
    private int mDrawingCacheBackgroundColor;
    private ViewTreeObserver mFloatingTreeObserver;
    GhostView mGhostView;
    private boolean mHasPerformedLongPress;
    @ExportedProperty(resolveId = true)
    int mID;
    protected final InputEventConsistencyVerifier mInputEventConsistencyVerifier;
    private SparseArray<Object> mKeyedTags;
    private int mLabelForId;
    private boolean mLastIsOpaque;
    Paint mLayerPaint;
    @ExportedProperty(category = "drawing", mapping = {@IntToString(from = 0, to = "NONE"), @IntToString(from = 1, to = "SOFTWARE"), @IntToString(from = 2, to = "HARDWARE")})
    int mLayerType;
    private Insets mLayoutInsets;
    protected LayoutParams mLayoutParams;
    @ExportedProperty(category = "layout")
    protected int mLeft;
    private boolean mLeftPaddingDefined;
    ListenerInfo mListenerInfo;
    private MatchIdPredicate mMatchIdPredicate;
    private MatchLabelForPredicate mMatchLabelForPredicate;
    private LongSparseLongArray mMeasureCache;
    @ExportedProperty(category = "measurement")
    int mMeasuredHeight;
    @ExportedProperty(category = "measurement")
    int mMeasuredWidth;
    @ExportedProperty(category = "measurement")
    private int mMinHeight;
    @ExportedProperty(category = "measurement")
    private int mMinWidth;
    private ViewParent mNestedScrollingParent;
    private int mNextFocusDownId;
    int mNextFocusForwardId;
    private int mNextFocusLeftId;
    private int mNextFocusRightId;
    private int mNextFocusUpId;
    int mOldHeightMeasureSpec;
    int mOldWidthMeasureSpec;
    ViewOutlineProvider mOutlineProvider;
    private int mOverScrollMode;
    ViewOverlay mOverlay;
    @ExportedProperty(category = "padding")
    protected int mPaddingBottom;
    @ExportedProperty(category = "padding")
    protected int mPaddingLeft;
    @ExportedProperty(category = "padding")
    protected int mPaddingRight;
    @ExportedProperty(category = "padding")
    protected int mPaddingTop;
    protected ViewParent mParent;
    private CheckForLongPress mPendingCheckForLongPress;
    private CheckForTap mPendingCheckForTap;
    private PerformClick mPerformClick;
    @ExportedProperty(flagMapping = {@FlagToString(equals = 4096, mask = 4096, name = "FORCE_LAYOUT"), @FlagToString(equals = 8192, mask = 8192, name = "LAYOUT_REQUIRED"), @FlagToString(equals = 32768, mask = 32768, name = "DRAWING_CACHE_INVALID", outputIf = false), @FlagToString(equals = 32, mask = 32, name = "DRAWN", outputIf = true), @FlagToString(equals = 32, mask = 32, name = "NOT_DRAWN", outputIf = false), @FlagToString(equals = 4194304, mask = 6291456, name = "DIRTY_OPAQUE"), @FlagToString(equals = 2097152, mask = 6291456, name = "DIRTY")}, formatToHexString = true)
    int mPrivateFlags;
    int mPrivateFlags2;
    int mPrivateFlags3;
    boolean mRecreateDisplayList;
    final RenderNode mRenderNode;
    private final Resources mResources;
    @ExportedProperty(category = "layout")
    protected int mRight;
    private boolean mRightPaddingDefined;
    private ScrollabilityCache mScrollCache;
    @ExportedProperty(category = "scrolling")
    protected int mScrollX;
    @ExportedProperty(category = "scrolling")
    protected int mScrollY;
    private SendViewScrolledAccessibilityEvent mSendViewScrolledAccessibilityEvent;
    SendViewStateChangedAccessibilityEvent mSendViewStateChangedAccessibilityEvent;
    private boolean mSendingHoverAccessibilityEvents;
    private StateListAnimator mStateListAnimator;
    @ExportedProperty(flagMapping = {@FlagToString(equals = 1, mask = 1, name = "SYSTEM_UI_FLAG_LOW_PROFILE", outputIf = true), @FlagToString(equals = 2, mask = 2, name = "SYSTEM_UI_FLAG_HIDE_NAVIGATION", outputIf = true), @FlagToString(equals = 0, mask = 16383, name = "SYSTEM_UI_FLAG_VISIBLE", outputIf = true)}, formatToHexString = true)
    int mSystemUiVisibility;
    protected Object mTag;
    private int[] mTempNestedScrollConsumed;
    @ExportedProperty(category = "layout")
    protected int mTop;
    private TouchDelegate mTouchDelegate;
    private int mTouchSlop;
    TransformationInfo mTransformationInfo;
    int mTransientStateCount;
    private String mTransitionName;
    private Bitmap mUnscaledDrawingCache;
    private UnsetPressedState mUnsetPressedState;
    @ExportedProperty(category = "padding")
    protected int mUserPaddingBottom;
    @ExportedProperty(category = "padding")
    int mUserPaddingEnd;
    @ExportedProperty(category = "padding")
    protected int mUserPaddingLeft;
    int mUserPaddingLeftInitial;
    @ExportedProperty(category = "padding")
    protected int mUserPaddingRight;
    int mUserPaddingRightInitial;
    @ExportedProperty(category = "padding")
    int mUserPaddingStart;
    private float mVerticalScrollFactor;
    private int mVerticalScrollbarPosition;
    @ExportedProperty(formatToHexString = true)
    int mViewFlags;
    int mWindowAttachCount;

    public interface OnCreateContextMenuListener {
        void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenuInfo contextMenuInfo);
    }

    public interface OnClickListener {
        void onClick(View view);
    }

    public interface OnLayoutChangeListener {
        void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8);
    }

    public interface OnKeyListener {
        boolean onKey(View view, int i, KeyEvent keyEvent);
    }

    public interface OnAttachStateChangeListener {
        void onViewAttachedToWindow(View view);

        void onViewDetachedFromWindow(View view);
    }

    /* renamed from: android.view.View.10 */
    static class AnonymousClass10 extends FloatProperty<View> {
        AnonymousClass10(String x0) {
            super(x0);
        }

        public void setValue(View object, float value) {
            object.setRotation(value);
        }

        public Float get(View object) {
            return Float.valueOf(object.getRotation());
        }
    }

    /* renamed from: android.view.View.11 */
    static class AnonymousClass11 extends FloatProperty<View> {
        AnonymousClass11(String x0) {
            super(x0);
        }

        public void setValue(View object, float value) {
            object.setRotationX(value);
        }

        public Float get(View object) {
            return Float.valueOf(object.getRotationX());
        }
    }

    /* renamed from: android.view.View.12 */
    static class AnonymousClass12 extends FloatProperty<View> {
        AnonymousClass12(String x0) {
            super(x0);
        }

        public void setValue(View object, float value) {
            object.setRotationY(value);
        }

        public Float get(View object) {
            return Float.valueOf(object.getRotationY());
        }
    }

    /* renamed from: android.view.View.13 */
    static class AnonymousClass13 extends FloatProperty<View> {
        AnonymousClass13(String x0) {
            super(x0);
        }

        public void setValue(View object, float value) {
            object.setScaleX(value);
        }

        public Float get(View object) {
            return Float.valueOf(object.getScaleX());
        }
    }

    /* renamed from: android.view.View.14 */
    static class AnonymousClass14 extends FloatProperty<View> {
        AnonymousClass14(String x0) {
            super(x0);
        }

        public void setValue(View object, float value) {
            object.setScaleY(value);
        }

        public Float get(View object) {
            return Float.valueOf(object.getScaleY());
        }
    }

    /* renamed from: android.view.View.1 */
    class AnonymousClass1 implements OnClickListener {
        private Method mHandler;
        final /* synthetic */ View this$0;
        final /* synthetic */ String val$handlerName;

        AnonymousClass1(android.view.View r1, java.lang.String r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.1.<init>(android.view.View, java.lang.String):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.1.<init>(android.view.View, java.lang.String):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.1.<init>(android.view.View, java.lang.String):void");
        }

        public void onClick(android.view.View r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.1.onClick(android.view.View):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.1.onClick(android.view.View):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.1.onClick(android.view.View):void");
        }
    }

    /* renamed from: android.view.View.2 */
    class AnonymousClass2 implements Predicate<View> {
        final /* synthetic */ View this$0;
        final /* synthetic */ int val$id;

        AnonymousClass2(android.view.View r1, int r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.2.<init>(android.view.View, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.2.<init>(android.view.View, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.2.<init>(android.view.View, int):void");
        }

        public boolean apply(android.view.View r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.2.apply(android.view.View):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.2.apply(android.view.View):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.2.apply(android.view.View):boolean");
        }

        public /* bridge */ /* synthetic */ boolean apply(java.lang.Object r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.2.apply(java.lang.Object):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.2.apply(java.lang.Object):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.2.apply(java.lang.Object):boolean");
        }
    }

    /* renamed from: android.view.View.3 */
    static class AnonymousClass3 extends FloatProperty<View> {
        AnonymousClass3(String x0) {
            super(x0);
        }

        public /* bridge */ /* synthetic */ Object get(Object x0) {
            return get((View) x0);
        }

        public /* bridge */ /* synthetic */ void setValue(Object x0, float x1) {
            setValue((View) x0, x1);
        }

        public void setValue(View object, float value) {
            object.setAlpha(value);
        }

        public Float get(View object) {
            return Float.valueOf(object.getAlpha());
        }
    }

    /* renamed from: android.view.View.4 */
    static class AnonymousClass4 extends FloatProperty<View> {
        AnonymousClass4(String x0) {
            super(x0);
        }

        public /* bridge */ /* synthetic */ Object get(Object x0) {
            return get((View) x0);
        }

        public /* bridge */ /* synthetic */ void setValue(Object x0, float x1) {
            setValue((View) x0, x1);
        }

        public void setValue(View object, float value) {
            object.setTranslationX(value);
        }

        public Float get(View object) {
            return Float.valueOf(object.getTranslationX());
        }
    }

    /* renamed from: android.view.View.5 */
    static class AnonymousClass5 extends FloatProperty<View> {
        AnonymousClass5(String x0) {
            super(x0);
        }

        public /* bridge */ /* synthetic */ Object get(Object x0) {
            return get((View) x0);
        }

        public /* bridge */ /* synthetic */ void setValue(Object x0, float x1) {
            setValue((View) x0, x1);
        }

        public void setValue(View object, float value) {
            object.setTranslationY(value);
        }

        public Float get(View object) {
            return Float.valueOf(object.getTranslationY());
        }
    }

    /* renamed from: android.view.View.6 */
    static class AnonymousClass6 extends FloatProperty<View> {
        AnonymousClass6(String x0) {
            super(x0);
        }

        public /* bridge */ /* synthetic */ Object get(Object x0) {
            return get((View) x0);
        }

        public /* bridge */ /* synthetic */ void setValue(Object x0, float x1) {
            setValue((View) x0, x1);
        }

        public void setValue(View object, float value) {
            object.setTranslationZ(value);
        }

        public Float get(View object) {
            return Float.valueOf(object.getTranslationZ());
        }
    }

    /* renamed from: android.view.View.7 */
    static class AnonymousClass7 extends FloatProperty<View> {
        AnonymousClass7(String x0) {
            super(x0);
        }

        public /* bridge */ /* synthetic */ Object get(Object x0) {
            return get((View) x0);
        }

        public /* bridge */ /* synthetic */ void setValue(Object x0, float x1) {
            setValue((View) x0, x1);
        }

        public void setValue(View object, float value) {
            object.setX(value);
        }

        public Float get(View object) {
            return Float.valueOf(object.getX());
        }
    }

    /* renamed from: android.view.View.8 */
    static class AnonymousClass8 extends FloatProperty<View> {
        AnonymousClass8(String x0) {
            super(x0);
        }

        public /* bridge */ /* synthetic */ Object get(Object x0) {
            return get((View) x0);
        }

        public /* bridge */ /* synthetic */ void setValue(Object x0, float x1) {
            setValue((View) x0, x1);
        }

        public void setValue(View object, float value) {
            object.setY(value);
        }

        public Float get(View object) {
            return Float.valueOf(object.getY());
        }
    }

    /* renamed from: android.view.View.9 */
    static class AnonymousClass9 extends FloatProperty<View> {
        AnonymousClass9(String x0) {
            super(x0);
        }

        public /* bridge */ /* synthetic */ Object get(Object x0) {
            return get((View) x0);
        }

        public /* bridge */ /* synthetic */ void setValue(Object x0, float x1) {
            setValue((View) x0, x1);
        }

        public void setValue(View object, float value) {
            object.setZ(value);
        }

        public Float get(View object) {
            return Float.valueOf(object.getZ());
        }
    }

    public static class AccessibilityDelegate {
        public AccessibilityDelegate() {
        }

        public void sendAccessibilityEvent(View host, int eventType) {
            host.sendAccessibilityEventInternal(eventType);
        }

        public boolean performAccessibilityAction(View host, int action, Bundle args) {
            return host.performAccessibilityActionInternal(action, args);
        }

        public void sendAccessibilityEventUnchecked(View host, AccessibilityEvent event) {
            host.sendAccessibilityEventUncheckedInternal(event);
        }

        public boolean dispatchPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
            return host.dispatchPopulateAccessibilityEventInternal(event);
        }

        public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
            host.onPopulateAccessibilityEventInternal(event);
        }

        public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
            host.onInitializeAccessibilityEventInternal(event);
        }

        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
            host.onInitializeAccessibilityNodeInfoInternal(info);
        }

        public boolean onRequestSendAccessibilityEvent(ViewGroup host, View child, AccessibilityEvent event) {
            return host.onRequestSendAccessibilityEventInternal(child, event);
        }

        public AccessibilityNodeProvider getAccessibilityNodeProvider(View host) {
            return null;
        }

        public AccessibilityNodeInfo createAccessibilityNodeInfo(View host) {
            return host.createAccessibilityNodeInfoInternal();
        }
    }

    static final class AttachInfo {
        int mAccessibilityFetchFlags;
        Drawable mAccessibilityFocusDrawable;
        int mAccessibilityWindowId;
        float mApplicationScale;
        Canvas mCanvas;
        final Rect mContentInsets;
        boolean mDebugLayout;
        int mDisabledSystemUiVisibility;
        final Display mDisplay;
        int mDisplayState;
        long mDrawingTime;
        boolean mForceReportNewAttributes;
        final InternalInsetsInfo mGivenInternalInsets;
        int mGlobalSystemUiVisibility;
        final Handler mHandler;
        boolean mHardwareAccelerated;
        boolean mHardwareAccelerationRequested;
        HardwareRenderer mHardwareRenderer;
        boolean mHasNonEmptyGivenInternalInsets;
        boolean mHasSystemUiListeners;
        boolean mHasWindowFocus;
        boolean mHighContrastText;
        IWindowId mIWindowId;
        boolean mIgnoreDirtyState;
        boolean mInTouchMode;
        final int[] mInvalidateChildLocation;
        boolean mKeepScreenOn;
        final DispatcherState mKeyDispatchState;
        final Rect mOverscanInsets;
        boolean mOverscanRequested;
        IBinder mPanelParentWindowToken;
        List<RenderNode> mPendingAnimatingRenderNodes;
        final Point mPoint;
        boolean mRecomputeGlobalAttributes;
        final Callbacks mRootCallbacks;
        View mRootView;
        boolean mScalingRequired;
        final ArrayList<View> mScrollContainers;
        final IWindowSession mSession;
        boolean mSetIgnoreDirtyState;
        final Rect mStableInsets;
        int mSystemUiVisibility;
        final ArrayList<View> mTempArrayList;
        final Rect mTmpInvalRect;
        final int[] mTmpLocation;
        final Matrix mTmpMatrix;
        final Outline mTmpOutline;
        final List<RectF> mTmpRectList;
        final float[] mTmpTransformLocation;
        final RectF mTmpTransformRect;
        final RectF mTmpTransformRect1;
        final Transformation mTmpTransformation;
        final int[] mTransparentLocation;
        final ViewTreeObserver mTreeObserver;
        boolean mTurnOffWindowResizeAnim;
        boolean mUnbufferedDispatchRequested;
        boolean mUse32BitDrawingCache;
        View mViewRequestingLayout;
        final ViewRootImpl mViewRootImpl;
        boolean mViewScrollChanged;
        boolean mViewVisibilityChanged;
        final Rect mVisibleInsets;
        final IWindow mWindow;
        WindowId mWindowId;
        int mWindowLeft;
        final IBinder mWindowToken;
        int mWindowTop;
        int mWindowVisibility;

        interface Callbacks {
            boolean performHapticFeedback(int i, boolean z);

            void playSoundEffect(int i);
        }

        static class InvalidateInfo {
            private static final int POOL_LIMIT = 10;
            private static final SynchronizedPool<InvalidateInfo> sPool = null;
            int bottom;
            int left;
            int right;
            View target;
            int top;

            public static android.view.View.AttachInfo.InvalidateInfo obtain() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.AttachInfo.InvalidateInfo.obtain():android.view.View$AttachInfo$InvalidateInfo
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.AttachInfo.InvalidateInfo.obtain():android.view.View$AttachInfo$InvalidateInfo
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.view.View.AttachInfo.InvalidateInfo.obtain():android.view.View$AttachInfo$InvalidateInfo");
            }

            public void recycle() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.AttachInfo.InvalidateInfo.recycle():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.AttachInfo.InvalidateInfo.recycle():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.view.View.AttachInfo.InvalidateInfo.recycle():void");
            }

            InvalidateInfo() {
            }

            static {
                sPool = new SynchronizedPool(POOL_LIMIT);
            }
        }

        AttachInfo(IWindowSession session, IWindow window, Display display, ViewRootImpl viewRootImpl, Handler handler, Callbacks effectPlayer) {
            this.mDisplayState = View.VISIBLE;
            this.mOverscanInsets = new Rect();
            this.mContentInsets = new Rect();
            this.mVisibleInsets = new Rect();
            this.mStableInsets = new Rect();
            this.mGivenInternalInsets = new InternalInsetsInfo();
            this.mScrollContainers = new ArrayList();
            this.mKeyDispatchState = new DispatcherState();
            this.mSetIgnoreDirtyState = View.DBG;
            this.mTransparentLocation = new int[View.VIEW_STATE_SELECTED];
            this.mInvalidateChildLocation = new int[View.VIEW_STATE_SELECTED];
            this.mTmpLocation = new int[View.VIEW_STATE_SELECTED];
            this.mTmpTransformLocation = new float[View.VIEW_STATE_SELECTED];
            this.mTreeObserver = new ViewTreeObserver();
            this.mTmpInvalRect = new Rect();
            this.mTmpTransformRect = new RectF();
            this.mTmpTransformRect1 = new RectF();
            this.mTmpRectList = new ArrayList();
            this.mTmpMatrix = new Matrix();
            this.mTmpTransformation = new Transformation();
            this.mTmpOutline = new Outline();
            this.mTempArrayList = new ArrayList(24);
            this.mAccessibilityWindowId = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
            this.mDebugLayout = SystemProperties.getBoolean(View.DEBUG_LAYOUT_PROPERTY, View.DBG);
            this.mPoint = new Point();
            this.mSession = session;
            this.mWindow = window;
            this.mWindowToken = window.asBinder();
            this.mDisplay = display;
            this.mViewRootImpl = viewRootImpl;
            this.mHandler = handler;
            this.mRootCallbacks = effectPlayer;
        }
    }

    public static class BaseSavedState extends AbsSavedState {
        public static final Creator<BaseSavedState> CREATOR = null;

        /* renamed from: android.view.View.BaseSavedState.1 */
        static class AnonymousClass1 implements Creator<BaseSavedState> {
            AnonymousClass1() {
            }

            public /* bridge */ /* synthetic */ Object m14createFromParcel(Parcel x0) {
                return createFromParcel(x0);
            }

            public /* bridge */ /* synthetic */ Object[] m15newArray(int x0) {
                return newArray(x0);
            }

            public BaseSavedState createFromParcel(Parcel in) {
                return new BaseSavedState(in);
            }

            public BaseSavedState[] newArray(int size) {
                return new BaseSavedState[size];
            }
        }

        public BaseSavedState(Parcel source) {
            super(source);
        }

        public BaseSavedState(Parcelable superState) {
            super(superState);
        }

        static {
            CREATOR = new AnonymousClass1();
        }
    }

    private final class CheckForLongPress implements Runnable {
        private int mOriginalWindowAttachCount;
        final /* synthetic */ View this$0;

        private CheckForLongPress(android.view.View r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.CheckForLongPress.<init>(android.view.View):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.CheckForLongPress.<init>(android.view.View):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.CheckForLongPress.<init>(android.view.View):void");
        }

        /* synthetic */ CheckForLongPress(android.view.View r1, android.view.View.AnonymousClass1 r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.CheckForLongPress.<init>(android.view.View, android.view.View$1):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.CheckForLongPress.<init>(android.view.View, android.view.View$1):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 0073
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.CheckForLongPress.<init>(android.view.View, android.view.View$1):void");
        }

        public void rememberWindowAttachCount() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.CheckForLongPress.rememberWindowAttachCount():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.CheckForLongPress.rememberWindowAttachCount():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.CheckForLongPress.rememberWindowAttachCount():void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.CheckForLongPress.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.CheckForLongPress.run():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.CheckForLongPress.run():void");
        }
    }

    private final class CheckForTap implements Runnable {
        final /* synthetic */ View this$0;
        public float x;
        public float y;

        private CheckForTap(View view) {
            this.this$0 = view;
        }

        /* synthetic */ CheckForTap(View x0, AnonymousClass1 x1) {
            this(x0);
        }

        public void run() {
            View view = this.this$0;
            view.mPrivateFlags &= -33554433;
            this.this$0.setPressed(true, this.x, this.y);
            this.this$0.checkForLongClick(ViewConfiguration.getTapTimeout());
        }
    }

    public static class DragShadowBuilder {
        private final WeakReference<View> mView;

        public DragShadowBuilder() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.DragShadowBuilder.<init>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.DragShadowBuilder.<init>():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.DragShadowBuilder.<init>():void");
        }

        public DragShadowBuilder(android.view.View r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.DragShadowBuilder.<init>(android.view.View):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.DragShadowBuilder.<init>(android.view.View):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.DragShadowBuilder.<init>(android.view.View):void");
        }

        public final android.view.View getView() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.DragShadowBuilder.getView():android.view.View
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.DragShadowBuilder.getView():android.view.View
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.DragShadowBuilder.getView():android.view.View");
        }

        public void onDrawShadow(android.graphics.Canvas r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.DragShadowBuilder.onDrawShadow(android.graphics.Canvas):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.DragShadowBuilder.onDrawShadow(android.graphics.Canvas):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.DragShadowBuilder.onDrawShadow(android.graphics.Canvas):void");
        }

        public void onProvideShadowMetrics(android.graphics.Point r1, android.graphics.Point r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.DragShadowBuilder.onProvideShadowMetrics(android.graphics.Point, android.graphics.Point):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.DragShadowBuilder.onProvideShadowMetrics(android.graphics.Point, android.graphics.Point):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.DragShadowBuilder.onProvideShadowMetrics(android.graphics.Point, android.graphics.Point):void");
        }
    }

    static class ListenerInfo {
        OnApplyWindowInsetsListener mOnApplyWindowInsetsListener;
        private CopyOnWriteArrayList<OnAttachStateChangeListener> mOnAttachStateChangeListeners;
        public OnClickListener mOnClickListener;
        protected OnCreateContextMenuListener mOnCreateContextMenuListener;
        private OnDragListener mOnDragListener;
        protected OnFocusChangeListener mOnFocusChangeListener;
        private OnGenericMotionListener mOnGenericMotionListener;
        private OnHoverListener mOnHoverListener;
        private OnKeyListener mOnKeyListener;
        private ArrayList<OnLayoutChangeListener> mOnLayoutChangeListeners;
        protected OnLongClickListener mOnLongClickListener;
        protected OnScrollChangeListener mOnScrollChangeListener;
        private OnSystemUiVisibilityChangeListener mOnSystemUiVisibilityChangeListener;
        private OnTouchListener mOnTouchListener;

        ListenerInfo() {
        }
    }

    private class MatchIdPredicate implements Predicate<View> {
        public int mId;
        final /* synthetic */ View this$0;

        private MatchIdPredicate(android.view.View r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.MatchIdPredicate.<init>(android.view.View):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.MatchIdPredicate.<init>(android.view.View):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.MatchIdPredicate.<init>(android.view.View):void");
        }

        /* synthetic */ MatchIdPredicate(android.view.View r1, android.view.View.AnonymousClass1 r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.MatchIdPredicate.<init>(android.view.View, android.view.View$1):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.MatchIdPredicate.<init>(android.view.View, android.view.View$1):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 0073
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.MatchIdPredicate.<init>(android.view.View, android.view.View$1):void");
        }

        public boolean apply(android.view.View r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.MatchIdPredicate.apply(android.view.View):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.MatchIdPredicate.apply(android.view.View):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.MatchIdPredicate.apply(android.view.View):boolean");
        }

        public /* bridge */ /* synthetic */ boolean apply(java.lang.Object r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.MatchIdPredicate.apply(java.lang.Object):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.MatchIdPredicate.apply(java.lang.Object):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.MatchIdPredicate.apply(java.lang.Object):boolean");
        }
    }

    private class MatchLabelForPredicate implements Predicate<View> {
        private int mLabeledId;
        final /* synthetic */ View this$0;

        private MatchLabelForPredicate(android.view.View r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.MatchLabelForPredicate.<init>(android.view.View):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.MatchLabelForPredicate.<init>(android.view.View):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.MatchLabelForPredicate.<init>(android.view.View):void");
        }

        /* synthetic */ MatchLabelForPredicate(android.view.View r1, android.view.View.AnonymousClass1 r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.MatchLabelForPredicate.<init>(android.view.View, android.view.View$1):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.MatchLabelForPredicate.<init>(android.view.View, android.view.View$1):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 0073
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.MatchLabelForPredicate.<init>(android.view.View, android.view.View$1):void");
        }

        static /* synthetic */ int access$902(android.view.View.MatchLabelForPredicate r1, int r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.MatchLabelForPredicate.access$902(android.view.View$MatchLabelForPredicate, int):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.MatchLabelForPredicate.access$902(android.view.View$MatchLabelForPredicate, int):int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.MatchLabelForPredicate.access$902(android.view.View$MatchLabelForPredicate, int):int");
        }

        public boolean apply(android.view.View r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.MatchLabelForPredicate.apply(android.view.View):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.MatchLabelForPredicate.apply(android.view.View):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.MatchLabelForPredicate.apply(android.view.View):boolean");
        }

        public /* bridge */ /* synthetic */ boolean apply(java.lang.Object r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.MatchLabelForPredicate.apply(java.lang.Object):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.MatchLabelForPredicate.apply(java.lang.Object):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.MatchLabelForPredicate.apply(java.lang.Object):boolean");
        }
    }

    public static class MeasureSpec {
        public static final int AT_MOST = Integer.MIN_VALUE;
        public static final int EXACTLY = 1073741824;
        private static final int MODE_MASK = -1073741824;
        private static final int MODE_SHIFT = 30;
        public static final int UNSPECIFIED = 0;

        public MeasureSpec() {
        }

        public static int makeMeasureSpec(int size, int mode) {
            if (View.sUseBrokenMakeMeasureSpec) {
                return size + mode;
            }
            return (1073741823 & size) | (MODE_MASK & mode);
        }

        public static int getMode(int measureSpec) {
            return MODE_MASK & measureSpec;
        }

        public static int getSize(int measureSpec) {
            return 1073741823 & measureSpec;
        }

        static int adjust(int measureSpec, int delta) {
            int mode = getMode(measureSpec);
            if (mode == 0) {
                return makeMeasureSpec(View.VISIBLE, View.VISIBLE);
            }
            int size = getSize(measureSpec) + delta;
            if (size < 0) {
                Log.e(View.VIEW_LOG_TAG, "MeasureSpec.adjust: new size would be negative! (" + size + ") spec: " + toString(measureSpec) + " delta: " + delta);
                size = View.VISIBLE;
            }
            return makeMeasureSpec(size, mode);
        }

        public static String toString(int measureSpec) {
            int mode = getMode(measureSpec);
            int size = getSize(measureSpec);
            StringBuilder sb = new StringBuilder("MeasureSpec: ");
            if (mode == 0) {
                sb.append("UNSPECIFIED ");
            } else if (mode == EXACTLY) {
                sb.append("EXACTLY ");
            } else if (mode == AT_MOST) {
                sb.append("AT_MOST ");
            } else {
                sb.append(mode).append(" ");
            }
            sb.append(size);
            return sb.toString();
        }
    }

    public interface OnApplyWindowInsetsListener {
        WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets);
    }

    public interface OnDragListener {
        boolean onDrag(View view, DragEvent dragEvent);
    }

    public interface OnFocusChangeListener {
        void onFocusChange(View view, boolean z);
    }

    public interface OnGenericMotionListener {
        boolean onGenericMotion(View view, MotionEvent motionEvent);
    }

    public interface OnHoverListener {
        boolean onHover(View view, MotionEvent motionEvent);
    }

    public interface OnLongClickListener {
        boolean onLongClick(View view);
    }

    public interface OnScrollChangeListener {
        void onScrollChange(View view, int i, int i2, int i3, int i4);
    }

    public interface OnSystemUiVisibilityChangeListener {
        void onSystemUiVisibilityChange(int i);
    }

    public interface OnTouchListener {
        boolean onTouch(View view, MotionEvent motionEvent);
    }

    private final class PerformClick implements Runnable {
        final /* synthetic */ View this$0;

        private PerformClick(View view) {
            this.this$0 = view;
        }

        /* synthetic */ PerformClick(View x0, AnonymousClass1 x1) {
            this(x0);
        }

        public void run() {
            this.this$0.performClick();
        }
    }

    private static class ScrollabilityCache implements Runnable {
        public static final int FADING = 2;
        public static final int OFF = 0;
        public static final int ON = 1;
        private static final float[] OPAQUE = null;
        private static final float[] TRANSPARENT = null;
        public boolean fadeScrollBars;
        public long fadeStartTime;
        public int fadingEdgeLength;
        public View host;
        public float[] interpolatorValues;
        private int mLastColor;
        public final Matrix matrix;
        public final Paint paint;
        public ScrollBarDrawable scrollBar;
        public int scrollBarDefaultDelayBeforeFade;
        public int scrollBarFadeDuration;
        public final Interpolator scrollBarInterpolator;
        public int scrollBarSize;
        public Shader shader;
        public int state;

        static {
            float[] fArr = new float[ON];
            fArr[OFF] = 255.0f;
            OPAQUE = fArr;
            fArr = new float[ON];
            fArr[OFF] = 0.0f;
            TRANSPARENT = fArr;
        }

        public ScrollabilityCache(ViewConfiguration configuration, View host) {
            this.scrollBarInterpolator = new Interpolator(ON, FADING);
            this.state = OFF;
            this.fadingEdgeLength = configuration.getScaledFadingEdgeLength();
            this.scrollBarSize = configuration.getScaledScrollBarSize();
            this.scrollBarDefaultDelayBeforeFade = ViewConfiguration.getScrollDefaultDelay();
            this.scrollBarFadeDuration = ViewConfiguration.getScrollBarFadeDuration();
            this.paint = new Paint();
            this.matrix = new Matrix();
            this.shader = new LinearGradient(0.0f, 0.0f, 0.0f, (float) WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL, (int) View.MEASURED_STATE_MASK, (int) OFF, TileMode.CLAMP);
            this.paint.setShader(this.shader);
            this.paint.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
            this.host = host;
        }

        public void setFadeColor(int color) {
            if (color != this.mLastColor) {
                this.mLastColor = color;
                if (color != 0) {
                    this.shader = new LinearGradient(0.0f, 0.0f, 0.0f, (float) WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL, View.MEASURED_STATE_MASK | color, color & View.MEASURED_SIZE_MASK, TileMode.CLAMP);
                    this.paint.setShader(this.shader);
                    this.paint.setXfermode(null);
                    return;
                }
                this.shader = new LinearGradient(0.0f, 0.0f, 0.0f, (float) WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL, (int) View.MEASURED_STATE_MASK, (int) OFF, TileMode.CLAMP);
                this.paint.setShader(this.shader);
                this.paint.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
            }
        }

        public void run() {
            long now = AnimationUtils.currentAnimationTimeMillis();
            if (now >= this.fadeStartTime) {
                int nextFrame = (int) now;
                Interpolator interpolator = this.scrollBarInterpolator;
                int framesCount = OFF + ON;
                interpolator.setKeyFrame(OFF, nextFrame, OPAQUE);
                interpolator.setKeyFrame(framesCount, nextFrame + this.scrollBarFadeDuration, TRANSPARENT);
                this.state = FADING;
                this.host.invalidate(true);
            }
        }
    }

    private class SendViewScrolledAccessibilityEvent implements Runnable {
        public volatile boolean mIsPending;
        final /* synthetic */ View this$0;

        private SendViewScrolledAccessibilityEvent(android.view.View r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.SendViewScrolledAccessibilityEvent.<init>(android.view.View):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.SendViewScrolledAccessibilityEvent.<init>(android.view.View):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.SendViewScrolledAccessibilityEvent.<init>(android.view.View):void");
        }

        /* synthetic */ SendViewScrolledAccessibilityEvent(android.view.View r1, android.view.View.AnonymousClass1 r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.SendViewScrolledAccessibilityEvent.<init>(android.view.View, android.view.View$1):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.SendViewScrolledAccessibilityEvent.<init>(android.view.View, android.view.View$1):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 0073
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.SendViewScrolledAccessibilityEvent.<init>(android.view.View, android.view.View$1):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.SendViewScrolledAccessibilityEvent.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.SendViewScrolledAccessibilityEvent.run():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.SendViewScrolledAccessibilityEvent.run():void");
        }
    }

    private class SendViewStateChangedAccessibilityEvent implements Runnable {
        private int mChangeTypes;
        private long mLastEventTimeMillis;
        private boolean mPosted;
        private boolean mPostedWithDelay;
        final /* synthetic */ View this$0;

        private SendViewStateChangedAccessibilityEvent(android.view.View r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.SendViewStateChangedAccessibilityEvent.<init>(android.view.View):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.SendViewStateChangedAccessibilityEvent.<init>(android.view.View):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.SendViewStateChangedAccessibilityEvent.<init>(android.view.View):void");
        }

        /* synthetic */ SendViewStateChangedAccessibilityEvent(android.view.View r1, android.view.View.AnonymousClass1 r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.SendViewStateChangedAccessibilityEvent.<init>(android.view.View, android.view.View$1):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.SendViewStateChangedAccessibilityEvent.<init>(android.view.View, android.view.View$1):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 0073
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.SendViewStateChangedAccessibilityEvent.<init>(android.view.View, android.view.View$1):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.SendViewStateChangedAccessibilityEvent.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.SendViewStateChangedAccessibilityEvent.run():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.SendViewStateChangedAccessibilityEvent.run():void");
        }

        public void runOrPost(int r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.View.SendViewStateChangedAccessibilityEvent.runOrPost(int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.View.SendViewStateChangedAccessibilityEvent.runOrPost(int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.View.SendViewStateChangedAccessibilityEvent.runOrPost(int):void");
        }
    }

    private static class TintInfo {
        boolean mHasTintList;
        boolean mHasTintMode;
        ColorStateList mTintList;
        Mode mTintMode;

        private TintInfo() {
        }

        /* synthetic */ TintInfo(AnonymousClass1 x0) {
            this();
        }
    }

    static class TransformationInfo {
        @ExportedProperty
        float mAlpha;
        private Matrix mInverseMatrix;
        private final Matrix mMatrix;
        float mTransitionAlpha;

        TransformationInfo() {
            this.mMatrix = new Matrix();
            this.mAlpha = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
            this.mTransitionAlpha = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        }
    }

    private final class UnsetPressedState implements Runnable {
        final /* synthetic */ View this$0;

        private UnsetPressedState(View view) {
            this.this$0 = view;
        }

        /* synthetic */ UnsetPressedState(View x0, AnonymousClass1 x1) {
            this(x0);
        }

        public void run() {
            this.this$0.setPressed(View.DBG);
        }
    }

    static {
        mDebugViewAttributes = DBG;
        sCompatibilityDone = DBG;
        sUseBrokenMakeMeasureSpec = DBG;
        sIgnoreMeasureCache = DBG;
        VISIBILITY_FLAGS = new int[]{VISIBLE, VIEW_STATE_FOCUSED, VIEW_STATE_ENABLED};
        DRAWING_CACHE_QUALITY_FLAGS = new int[]{VISIBLE, STATUS_BAR_DISABLE_NOTIFICATION_TICKER, STATUS_BAR_DISABLE_SYSTEM_INFO};
        VIEW_STATE_IDS = new int[]{R.attr.state_window_focused, VIEW_STATE_WINDOW_FOCUSED, R.attr.state_selected, VIEW_STATE_SELECTED, R.attr.state_focused, VIEW_STATE_FOCUSED, R.attr.state_enabled, VIEW_STATE_ENABLED, R.attr.state_pressed, VIEW_STATE_PRESSED, R.attr.state_activated, VIEW_STATE_ACTIVATED, R.attr.state_accelerated, VIEW_STATE_ACCELERATED, R.attr.state_hovered, WILL_NOT_DRAW, R.attr.state_drag_can_accept, VIEW_STATE_DRAG_CAN_ACCEPT, R.attr.state_drag_hovered, VIEW_STATE_DRAG_HOVERED};
        if (VIEW_STATE_IDS.length / VIEW_STATE_SELECTED != com.android.internal.R.styleable.ViewDrawableStates.length) {
            throw new IllegalStateException("VIEW_STATE_IDs array length does not match ViewDrawableStates style array");
        }
        int i;
        int j;
        int[] orderedIds = new int[VIEW_STATE_IDS.length];
        for (i = VISIBLE; i < com.android.internal.R.styleable.ViewDrawableStates.length; i += VIEW_STATE_WINDOW_FOCUSED) {
            int viewState = com.android.internal.R.styleable.ViewDrawableStates[i];
            for (j = VISIBLE; j < VIEW_STATE_IDS.length; j += VIEW_STATE_SELECTED) {
                if (VIEW_STATE_IDS[j] == viewState) {
                    orderedIds[i * VIEW_STATE_SELECTED] = viewState;
                    orderedIds[(i * VIEW_STATE_SELECTED) + VIEW_STATE_WINDOW_FOCUSED] = VIEW_STATE_IDS[j + VIEW_STATE_WINDOW_FOCUSED];
                }
            }
        }
        VIEW_STATE_SETS = new int[(VIEW_STATE_WINDOW_FOCUSED << (VIEW_STATE_IDS.length / VIEW_STATE_SELECTED))][];
        for (i = VISIBLE; i < VIEW_STATE_SETS.length; i += VIEW_STATE_WINDOW_FOCUSED) {
            int[] set = new int[Integer.bitCount(i)];
            int pos = VISIBLE;
            for (j = VISIBLE; j < orderedIds.length; j += VIEW_STATE_SELECTED) {
                if ((orderedIds[j + VIEW_STATE_WINDOW_FOCUSED] & i) != 0) {
                    int pos2 = pos + VIEW_STATE_WINDOW_FOCUSED;
                    set[pos] = orderedIds[j];
                    pos = pos2;
                }
            }
            VIEW_STATE_SETS[i] = set;
        }
        EMPTY_STATE_SET = VIEW_STATE_SETS[VISIBLE];
        WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_WINDOW_FOCUSED];
        SELECTED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_SELECTED];
        SELECTED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[TEXT_DIRECTION_LTR];
        FOCUSED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_FOCUSED];
        FOCUSED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[TEXT_DIRECTION_LOCALE];
        FOCUSED_SELECTED_STATE_SET = VIEW_STATE_SETS[TEXT_ALIGNMENT_VIEW_END];
        FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[SYSTEM_UI_CLEARABLE_FLAGS];
        ENABLED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_ENABLED];
        ENABLED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[9];
        ENABLED_SELECTED_STATE_SET = VIEW_STATE_SETS[PFLAG2_TEXT_DIRECTION_RESOLVED_MASK_SHIFT];
        ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[11];
        ENABLED_FOCUSED_STATE_SET = VIEW_STATE_SETS[VISIBILITY_MASK];
        ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[PFLAG2_TEXT_ALIGNMENT_MASK_SHIFT];
        ENABLED_FOCUSED_SELECTED_STATE_SET = VIEW_STATE_SETS[14];
        ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[15];
        PRESSED_STATE_SET = VIEW_STATE_SETS[VIEW_STATE_PRESSED];
        PRESSED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[PFLAG2_TEXT_ALIGNMENT_RESOLVED_MASK_SHIFT];
        PRESSED_SELECTED_STATE_SET = VIEW_STATE_SETS[18];
        PRESSED_SELECTED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[19];
        PRESSED_FOCUSED_STATE_SET = VIEW_STATE_SETS[PFLAG2_IMPORTANT_FOR_ACCESSIBILITY_SHIFT];
        PRESSED_FOCUSED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[21];
        PRESSED_FOCUSED_SELECTED_STATE_SET = VIEW_STATE_SETS[22];
        PRESSED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[PFLAG2_ACCESSIBILITY_LIVE_REGION_SHIFT];
        PRESSED_ENABLED_STATE_SET = VIEW_STATE_SETS[24];
        PRESSED_ENABLED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[25];
        PRESSED_ENABLED_SELECTED_STATE_SET = VIEW_STATE_SETS[26];
        PRESSED_ENABLED_SELECTED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[27];
        PRESSED_ENABLED_FOCUSED_STATE_SET = VIEW_STATE_SETS[28];
        PRESSED_ENABLED_FOCUSED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[29];
        PRESSED_ENABLED_FOCUSED_SELECTED_STATE_SET = VIEW_STATE_SETS[30];
        PRESSED_ENABLED_FOCUSED_SELECTED_WINDOW_FOCUSED_STATE_SET = VIEW_STATE_SETS[31];
        sThreadLocal = new ThreadLocal();
        LAYOUT_DIRECTION_FLAGS = new int[]{VISIBLE, VIEW_STATE_WINDOW_FOCUSED, VIEW_STATE_SELECTED, TEXT_DIRECTION_LTR};
        PFLAG2_TEXT_DIRECTION_FLAGS = new int[]{VISIBLE, VIEW_STATE_ACCELERATED, WILL_NOT_DRAW, KeyEvent.KEYCODE_BUTTON_5, VIEW_STATE_DRAG_CAN_ACCEPT, MifareClassic.SIZE_MINI};
        PFLAG2_TEXT_ALIGNMENT_FLAGS = new int[]{VISIBLE, PFLAG_LAYOUT_REQUIRED, RECENT_APPS_VISIBLE, 24576, SYSTEM_UI_TRANSPARENT, 40960, 49152};
        sNextGeneratedId = new AtomicInteger(VIEW_STATE_WINDOW_FOCUSED);
        ALPHA = new AnonymousClass3("alpha");
        TRANSLATION_X = new AnonymousClass4("translationX");
        TRANSLATION_Y = new AnonymousClass5("translationY");
        TRANSLATION_Z = new AnonymousClass6("translationZ");
        X = new AnonymousClass7("x");
        Y = new AnonymousClass8("y");
        Z = new AnonymousClass9("z");
        ROTATION = new AnonymousClass10("rotation");
        ROTATION_X = new AnonymousClass11("rotationX");
        ROTATION_Y = new AnonymousClass12("rotationY");
        SCALE_X = new AnonymousClass13("scaleX");
        SCALE_Y = new AnonymousClass14("scaleY");
    }

    public View(Context context) {
        InputEventConsistencyVerifier inputEventConsistencyVerifier;
        Resources resources = null;
        boolean z = DBG;
        this.mCurrentAnimation = null;
        this.mRecreateDisplayList = DBG;
        this.mID = NO_ID;
        this.mAccessibilityViewId = NO_ID;
        this.mAccessibilityCursorPosition = NO_ID;
        this.mTag = null;
        this.mTransientStateCount = VISIBLE;
        this.mClipBounds = null;
        this.mPaddingLeft = VISIBLE;
        this.mPaddingRight = VISIBLE;
        this.mLabelForId = NO_ID;
        this.mAccessibilityTraversalBeforeId = NO_ID;
        this.mAccessibilityTraversalAfterId = NO_ID;
        this.mLeftPaddingDefined = DBG;
        this.mRightPaddingDefined = DBG;
        this.mOldWidthMeasureSpec = UNDEFINED_PADDING;
        this.mOldHeightMeasureSpec = UNDEFINED_PADDING;
        this.mDrawableState = null;
        this.mOutlineProvider = ViewOutlineProvider.BACKGROUND;
        this.mNextFocusLeftId = NO_ID;
        this.mNextFocusRightId = NO_ID;
        this.mNextFocusUpId = NO_ID;
        this.mNextFocusDownId = NO_ID;
        this.mNextFocusForwardId = NO_ID;
        this.mPendingCheckForTap = null;
        this.mTouchDelegate = null;
        this.mDrawingCacheBackgroundColor = VISIBLE;
        this.mAnimator = null;
        this.mLayerType = VISIBLE;
        if (InputEventConsistencyVerifier.isInstrumentationEnabled()) {
            inputEventConsistencyVerifier = new InputEventConsistencyVerifier(this, VISIBLE);
        } else {
            inputEventConsistencyVerifier = null;
        }
        this.mInputEventConsistencyVerifier = inputEventConsistencyVerifier;
        this.mContext = context;
        if (context != null) {
            resources = context.getResources();
        }
        this.mResources = resources;
        this.mViewFlags = 402653184;
        this.mPrivateFlags2 = 140296;
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setOverScrollMode(VIEW_STATE_WINDOW_FOCUSED);
        this.mUserPaddingStart = UNDEFINED_PADDING;
        this.mUserPaddingEnd = UNDEFINED_PADDING;
        this.mRenderNode = RenderNode.create(getClass().getName(), this);
        if (!sCompatibilityDone && context != null) {
            boolean z2;
            int targetSdkVersion = context.getApplicationInfo().targetSdkVersion;
            if (targetSdkVersion <= PFLAG2_TEXT_ALIGNMENT_RESOLVED_MASK_SHIFT) {
                z2 = true;
            } else {
                z2 = DBG;
            }
            sUseBrokenMakeMeasureSpec = z2;
            if (targetSdkVersion < 19) {
                z = true;
            }
            sIgnoreMeasureCache = z;
            sCompatibilityDone = true;
        }
    }

    public View(Context context, AttributeSet attrs) {
        this(context, attrs, VISIBLE);
    }

    public View(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, VISIBLE);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public View(android.content.Context r54, android.util.AttributeSet r55, int r56, int r57) {
        /*
        r53 = this;
        r53.<init>(r54);
        r50 = com.android.internal.R.styleable.View;
        r0 = r54;
        r1 = r55;
        r2 = r50;
        r3 = r56;
        r4 = r57;
        r6 = r0.obtainStyledAttributes(r1, r2, r3, r4);
        r50 = mDebugViewAttributes;
        if (r50 == 0) goto L_0x001e;
    L_0x0017:
        r0 = r53;
        r1 = r55;
        r0.saveAttributeData(r1, r6);
    L_0x001e:
        r8 = 0;
        r20 = -1;
        r39 = -1;
        r24 = -1;
        r9 = -1;
        r32 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r12 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r23 = -1;
        r46 = 0;
        r45 = 0;
        r31 = 0;
        r48 = 0;
        r49 = 0;
        r41 = 0;
        r42 = 0;
        r43 = 0;
        r11 = 0;
        r26 = 0;
        r27 = 0;
        r28 = 0;
        r34 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r35 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r40 = 0;
        r29 = 0;
        r0 = r53;
        r0 = r0.mOverScrollMode;
        r22 = r0;
        r18 = 0;
        r33 = 0;
        r13 = 0;
        r21 = 0;
        r25 = 0;
        r50 = r54.getApplicationInfo();
        r0 = r50;
        r0 = r0.targetSdkVersion;
        r36 = r0;
        r5 = r6.getIndexCount();
        r17 = 0;
    L_0x006a:
        r0 = r17;
        if (r0 >= r5) goto L_0x05d6;
    L_0x006e:
        r0 = r17;
        r7 = r6.getIndex(r0);
        switch(r7) {
            case 8: goto L_0x036b;
            case 9: goto L_0x01c3;
            case 10: goto L_0x01d3;
            case 11: goto L_0x00f4;
            case 12: goto L_0x00fe;
            case 13: goto L_0x007a;
            case 14: goto L_0x007f;
            case 15: goto L_0x0098;
            case 16: goto L_0x00a9;
            case 17: goto L_0x00b2;
            case 18: goto L_0x00c3;
            case 19: goto L_0x01ef;
            case 20: goto L_0x01ff;
            case 21: goto L_0x0269;
            case 22: goto L_0x01df;
            case 23: goto L_0x0334;
            case 24: goto L_0x034a;
            case 25: goto L_0x0077;
            case 26: goto L_0x03c4;
            case 27: goto L_0x03d4;
            case 28: goto L_0x03e4;
            case 29: goto L_0x03f4;
            case 30: goto L_0x0215;
            case 31: goto L_0x022d;
            case 32: goto L_0x0241;
            case 33: goto L_0x02b4;
            case 34: goto L_0x0255;
            case 35: goto L_0x0414;
            case 36: goto L_0x0424;
            case 37: goto L_0x030a;
            case 38: goto L_0x0398;
            case 39: goto L_0x0381;
            case 40: goto L_0x031f;
            case 41: goto L_0x0434;
            case 42: goto L_0x02ca;
            case 43: goto L_0x0077;
            case 44: goto L_0x0077;
            case 45: goto L_0x0077;
            case 46: goto L_0x045a;
            case 47: goto L_0x03ac;
            case 48: goto L_0x0108;
            case 49: goto L_0x0119;
            case 50: goto L_0x012f;
            case 51: goto L_0x0145;
            case 52: goto L_0x0156;
            case 53: goto L_0x01ab;
            case 54: goto L_0x01b7;
            case 55: goto L_0x0187;
            case 56: goto L_0x0193;
            case 57: goto L_0x019f;
            case 58: goto L_0x0464;
            case 59: goto L_0x0404;
            case 60: goto L_0x0474;
            case 61: goto L_0x0352;
            case 62: goto L_0x04ec;
            case 63: goto L_0x0489;
            case 64: goto L_0x04bf;
            case 65: goto L_0x027d;
            case 66: goto L_0x00cc;
            case 67: goto L_0x00e2;
            case 68: goto L_0x02f9;
            case 69: goto L_0x04fd;
            case 70: goto L_0x0167;
            case 71: goto L_0x050e;
            case 72: goto L_0x051b;
            case 73: goto L_0x0178;
            case 74: goto L_0x052c;
            case 75: goto L_0x0545;
            case 76: goto L_0x057e;
            case 77: goto L_0x05c1;
            case 78: goto L_0x02d7;
            case 79: goto L_0x02e8;
            default: goto L_0x0077;
        };
    L_0x0077:
        r17 = r17 + 1;
        goto L_0x006a;
    L_0x007a:
        r8 = r6.getDrawable(r7);
        goto L_0x0077;
    L_0x007f:
        r50 = -1;
        r0 = r50;
        r23 = r6.getDimensionPixelSize(r7, r0);
        r0 = r23;
        r1 = r53;
        r1.mUserPaddingLeftInitial = r0;
        r0 = r23;
        r1 = r53;
        r1.mUserPaddingRightInitial = r0;
        r21 = 1;
        r25 = 1;
        goto L_0x0077;
    L_0x0098:
        r50 = -1;
        r0 = r50;
        r20 = r6.getDimensionPixelSize(r7, r0);
        r0 = r20;
        r1 = r53;
        r1.mUserPaddingLeftInitial = r0;
        r21 = 1;
        goto L_0x0077;
    L_0x00a9:
        r50 = -1;
        r0 = r50;
        r39 = r6.getDimensionPixelSize(r7, r0);
        goto L_0x0077;
    L_0x00b2:
        r50 = -1;
        r0 = r50;
        r24 = r6.getDimensionPixelSize(r7, r0);
        r0 = r24;
        r1 = r53;
        r1.mUserPaddingRightInitial = r0;
        r25 = 1;
        goto L_0x0077;
    L_0x00c3:
        r50 = -1;
        r0 = r50;
        r9 = r6.getDimensionPixelSize(r7, r0);
        goto L_0x0077;
    L_0x00cc:
        r50 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r0 = r50;
        r32 = r6.getDimensionPixelSize(r7, r0);
        r50 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r0 = r32;
        r1 = r50;
        if (r0 == r1) goto L_0x00df;
    L_0x00dc:
        r33 = 1;
    L_0x00de:
        goto L_0x0077;
    L_0x00df:
        r33 = 0;
        goto L_0x00de;
    L_0x00e2:
        r50 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r0 = r50;
        r12 = r6.getDimensionPixelSize(r7, r0);
        r50 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r0 = r50;
        if (r12 == r0) goto L_0x00f2;
    L_0x00f0:
        r13 = 1;
    L_0x00f1:
        goto L_0x0077;
    L_0x00f2:
        r13 = 0;
        goto L_0x00f1;
    L_0x00f4:
        r50 = 0;
        r0 = r50;
        r48 = r6.getDimensionPixelOffset(r7, r0);
        goto L_0x0077;
    L_0x00fe:
        r50 = 0;
        r0 = r50;
        r49 = r6.getDimensionPixelOffset(r7, r0);
        goto L_0x0077;
    L_0x0108:
        r50 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r0 = r50;
        r50 = r6.getFloat(r7, r0);
        r0 = r53;
        r1 = r50;
        r0.setAlpha(r1);
        goto L_0x0077;
    L_0x0119:
        r50 = 0;
        r0 = r50;
        r50 = r6.getDimensionPixelOffset(r7, r0);
        r0 = r50;
        r0 = (float) r0;
        r50 = r0;
        r0 = r53;
        r1 = r50;
        r0.setPivotX(r1);
        goto L_0x0077;
    L_0x012f:
        r50 = 0;
        r0 = r50;
        r50 = r6.getDimensionPixelOffset(r7, r0);
        r0 = r50;
        r0 = (float) r0;
        r50 = r0;
        r0 = r53;
        r1 = r50;
        r0.setPivotY(r1);
        goto L_0x0077;
    L_0x0145:
        r50 = 0;
        r0 = r50;
        r50 = r6.getDimensionPixelOffset(r7, r0);
        r0 = r50;
        r0 = (float) r0;
        r41 = r0;
        r40 = 1;
        goto L_0x0077;
    L_0x0156:
        r50 = 0;
        r0 = r50;
        r50 = r6.getDimensionPixelOffset(r7, r0);
        r0 = r50;
        r0 = (float) r0;
        r42 = r0;
        r40 = 1;
        goto L_0x0077;
    L_0x0167:
        r50 = 0;
        r0 = r50;
        r50 = r6.getDimensionPixelOffset(r7, r0);
        r0 = r50;
        r0 = (float) r0;
        r43 = r0;
        r40 = 1;
        goto L_0x0077;
    L_0x0178:
        r50 = 0;
        r0 = r50;
        r50 = r6.getDimensionPixelOffset(r7, r0);
        r0 = r50;
        r11 = (float) r0;
        r40 = 1;
        goto L_0x0077;
    L_0x0187:
        r50 = 0;
        r0 = r50;
        r26 = r6.getFloat(r7, r0);
        r40 = 1;
        goto L_0x0077;
    L_0x0193:
        r50 = 0;
        r0 = r50;
        r27 = r6.getFloat(r7, r0);
        r40 = 1;
        goto L_0x0077;
    L_0x019f:
        r50 = 0;
        r0 = r50;
        r28 = r6.getFloat(r7, r0);
        r40 = 1;
        goto L_0x0077;
    L_0x01ab:
        r50 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r0 = r50;
        r34 = r6.getFloat(r7, r0);
        r40 = 1;
        goto L_0x0077;
    L_0x01b7:
        r50 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r0 = r50;
        r35 = r6.getFloat(r7, r0);
        r40 = 1;
        goto L_0x0077;
    L_0x01c3:
        r50 = -1;
        r0 = r50;
        r50 = r6.getResourceId(r7, r0);
        r0 = r50;
        r1 = r53;
        r1.mID = r0;
        goto L_0x0077;
    L_0x01d3:
        r50 = r6.getText(r7);
        r0 = r50;
        r1 = r53;
        r1.mTag = r0;
        goto L_0x0077;
    L_0x01df:
        r50 = 0;
        r0 = r50;
        r50 = r6.getBoolean(r7, r0);
        if (r50 == 0) goto L_0x0077;
    L_0x01e9:
        r46 = r46 | 2;
        r45 = r45 | 2;
        goto L_0x0077;
    L_0x01ef:
        r50 = 0;
        r0 = r50;
        r50 = r6.getBoolean(r7, r0);
        if (r50 == 0) goto L_0x0077;
    L_0x01f9:
        r46 = r46 | 1;
        r45 = r45 | 1;
        goto L_0x0077;
    L_0x01ff:
        r50 = 0;
        r0 = r50;
        r50 = r6.getBoolean(r7, r0);
        if (r50 == 0) goto L_0x0077;
    L_0x0209:
        r50 = 262145; // 0x40001 float:3.67343E-40 double:1.29517E-318;
        r46 = r46 | r50;
        r50 = 262145; // 0x40001 float:3.67343E-40 double:1.29517E-318;
        r45 = r45 | r50;
        goto L_0x0077;
    L_0x0215:
        r50 = 0;
        r0 = r50;
        r50 = r6.getBoolean(r7, r0);
        if (r50 == 0) goto L_0x0077;
    L_0x021f:
        r0 = r46;
        r0 = r0 | 16384;
        r46 = r0;
        r0 = r45;
        r0 = r0 | 16384;
        r45 = r0;
        goto L_0x0077;
    L_0x022d:
        r50 = 0;
        r0 = r50;
        r50 = r6.getBoolean(r7, r0);
        if (r50 == 0) goto L_0x0077;
    L_0x0237:
        r50 = 2097152; // 0x200000 float:2.938736E-39 double:1.0361308E-317;
        r46 = r46 | r50;
        r50 = 2097152; // 0x200000 float:2.938736E-39 double:1.0361308E-317;
        r45 = r45 | r50;
        goto L_0x0077;
    L_0x0241:
        r50 = 1;
        r0 = r50;
        r50 = r6.getBoolean(r7, r0);
        if (r50 != 0) goto L_0x0077;
    L_0x024b:
        r50 = 65536; // 0x10000 float:9.18355E-41 double:3.2379E-319;
        r46 = r46 | r50;
        r50 = 65536; // 0x10000 float:9.18355E-41 double:3.2379E-319;
        r45 = r45 | r50;
        goto L_0x0077;
    L_0x0255:
        r50 = 0;
        r0 = r50;
        r50 = r6.getBoolean(r7, r0);
        if (r50 == 0) goto L_0x0077;
    L_0x025f:
        r50 = 4194304; // 0x400000 float:5.877472E-39 double:2.0722615E-317;
        r46 = r46 | r50;
        r50 = 4194304; // 0x400000 float:5.877472E-39 double:2.0722615E-317;
        r45 = r45 | r50;
        goto L_0x0077;
    L_0x0269:
        r50 = 0;
        r0 = r50;
        r47 = r6.getInt(r7, r0);
        if (r47 == 0) goto L_0x0077;
    L_0x0273:
        r50 = VISIBILITY_FLAGS;
        r50 = r50[r47];
        r46 = r46 | r50;
        r45 = r45 | 12;
        goto L_0x0077;
    L_0x027d:
        r0 = r53;
        r0 = r0.mPrivateFlags2;
        r50 = r0;
        r50 = r50 & -61;
        r0 = r50;
        r1 = r53;
        r1.mPrivateFlags2 = r0;
        r50 = -1;
        r0 = r50;
        r19 = r6.getInt(r7, r0);
        r50 = -1;
        r0 = r19;
        r1 = r50;
        if (r0 == r1) goto L_0x02b1;
    L_0x029b:
        r50 = LAYOUT_DIRECTION_FLAGS;
        r44 = r50[r19];
    L_0x029f:
        r0 = r53;
        r0 = r0.mPrivateFlags2;
        r50 = r0;
        r51 = r44 << 2;
        r50 = r50 | r51;
        r0 = r50;
        r1 = r53;
        r1.mPrivateFlags2 = r0;
        goto L_0x0077;
    L_0x02b1:
        r44 = 2;
        goto L_0x029f;
    L_0x02b4:
        r50 = 0;
        r0 = r50;
        r10 = r6.getInt(r7, r0);
        if (r10 == 0) goto L_0x0077;
    L_0x02be:
        r50 = DRAWING_CACHE_QUALITY_FLAGS;
        r50 = r50[r10];
        r46 = r46 | r50;
        r50 = 1572864; // 0x180000 float:2.204052E-39 double:7.77098E-318;
        r45 = r45 | r50;
        goto L_0x0077;
    L_0x02ca:
        r50 = r6.getString(r7);
        r0 = r53;
        r1 = r50;
        r0.setContentDescription(r1);
        goto L_0x0077;
    L_0x02d7:
        r50 = -1;
        r0 = r50;
        r50 = r6.getResourceId(r7, r0);
        r0 = r53;
        r1 = r50;
        r0.setAccessibilityTraversalBefore(r1);
        goto L_0x0077;
    L_0x02e8:
        r50 = -1;
        r0 = r50;
        r50 = r6.getResourceId(r7, r0);
        r0 = r53;
        r1 = r50;
        r0.setAccessibilityTraversalAfter(r1);
        goto L_0x0077;
    L_0x02f9:
        r50 = -1;
        r0 = r50;
        r50 = r6.getResourceId(r7, r0);
        r0 = r53;
        r1 = r50;
        r0.setLabelFor(r1);
        goto L_0x0077;
    L_0x030a:
        r50 = 1;
        r0 = r50;
        r50 = r6.getBoolean(r7, r0);
        if (r50 != 0) goto L_0x0077;
    L_0x0314:
        r50 = -134217729; // 0xfffffffff7ffffff float:-1.0384593E34 double:NaN;
        r46 = r46 & r50;
        r50 = 134217728; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r45 = r45 | r50;
        goto L_0x0077;
    L_0x031f:
        r50 = 1;
        r0 = r50;
        r50 = r6.getBoolean(r7, r0);
        if (r50 != 0) goto L_0x0077;
    L_0x0329:
        r50 = -268435457; // 0xffffffffefffffff float:-1.5845632E29 double:NaN;
        r46 = r46 & r50;
        r50 = 268435456; // 0x10000000 float:2.5243549E-29 double:1.32624737E-315;
        r45 = r45 | r50;
        goto L_0x0077;
    L_0x0334:
        r50 = 0;
        r0 = r50;
        r30 = r6.getInt(r7, r0);
        if (r30 == 0) goto L_0x0077;
    L_0x033e:
        r46 = r46 | r30;
        r0 = r45;
        r0 = r0 | 768;
        r45 = r0;
        r18 = 1;
        goto L_0x0077;
    L_0x034a:
        r50 = 14;
        r0 = r36;
        r1 = r50;
        if (r0 >= r1) goto L_0x0077;
    L_0x0352:
        r50 = 0;
        r0 = r50;
        r14 = r6.getInt(r7, r0);
        if (r14 == 0) goto L_0x0077;
    L_0x035c:
        r46 = r46 | r14;
        r0 = r45;
        r0 = r0 | 12288;
        r45 = r0;
        r0 = r53;
        r0.initializeFadingEdgeInternal(r6);
        goto L_0x0077;
    L_0x036b:
        r50 = 0;
        r0 = r50;
        r29 = r6.getInt(r7, r0);
        if (r29 == 0) goto L_0x0077;
    L_0x0375:
        r50 = 50331648; // 0x3000000 float:3.761582E-37 double:2.4867138E-316;
        r50 = r50 & r29;
        r46 = r46 | r50;
        r50 = 50331648; // 0x3000000 float:3.761582E-37 double:2.4867138E-316;
        r45 = r45 | r50;
        goto L_0x0077;
    L_0x0381:
        r31 = 1;
        r50 = 0;
        r0 = r50;
        r50 = r6.getBoolean(r7, r0);
        if (r50 == 0) goto L_0x0077;
    L_0x038d:
        r50 = 1;
        r0 = r53;
        r1 = r50;
        r0.setScrollContainer(r1);
        goto L_0x0077;
    L_0x0398:
        r50 = 0;
        r0 = r50;
        r50 = r6.getBoolean(r7, r0);
        if (r50 == 0) goto L_0x0077;
    L_0x03a2:
        r50 = 67108864; // 0x4000000 float:1.5046328E-36 double:3.31561842E-316;
        r46 = r46 | r50;
        r50 = 67108864; // 0x4000000 float:1.5046328E-36 double:3.31561842E-316;
        r45 = r45 | r50;
        goto L_0x0077;
    L_0x03ac:
        r50 = 0;
        r0 = r50;
        r50 = r6.getBoolean(r7, r0);
        if (r50 == 0) goto L_0x0077;
    L_0x03b6:
        r0 = r46;
        r0 = r0 | 1024;
        r46 = r0;
        r0 = r45;
        r0 = r0 | 1024;
        r45 = r0;
        goto L_0x0077;
    L_0x03c4:
        r50 = -1;
        r0 = r50;
        r50 = r6.getResourceId(r7, r0);
        r0 = r50;
        r1 = r53;
        r1.mNextFocusLeftId = r0;
        goto L_0x0077;
    L_0x03d4:
        r50 = -1;
        r0 = r50;
        r50 = r6.getResourceId(r7, r0);
        r0 = r50;
        r1 = r53;
        r1.mNextFocusRightId = r0;
        goto L_0x0077;
    L_0x03e4:
        r50 = -1;
        r0 = r50;
        r50 = r6.getResourceId(r7, r0);
        r0 = r50;
        r1 = r53;
        r1.mNextFocusUpId = r0;
        goto L_0x0077;
    L_0x03f4:
        r50 = -1;
        r0 = r50;
        r50 = r6.getResourceId(r7, r0);
        r0 = r50;
        r1 = r53;
        r1.mNextFocusDownId = r0;
        goto L_0x0077;
    L_0x0404:
        r50 = -1;
        r0 = r50;
        r50 = r6.getResourceId(r7, r0);
        r0 = r50;
        r1 = r53;
        r1.mNextFocusForwardId = r0;
        goto L_0x0077;
    L_0x0414:
        r50 = 0;
        r0 = r50;
        r50 = r6.getDimensionPixelSize(r7, r0);
        r0 = r50;
        r1 = r53;
        r1.mMinWidth = r0;
        goto L_0x0077;
    L_0x0424:
        r50 = 0;
        r0 = r50;
        r50 = r6.getDimensionPixelSize(r7, r0);
        r0 = r50;
        r1 = r53;
        r1.mMinHeight = r0;
        goto L_0x0077;
    L_0x0434:
        r50 = r54.isRestricted();
        if (r50 == 0) goto L_0x0442;
    L_0x043a:
        r50 = new java.lang.IllegalStateException;
        r51 = "The android:onClick attribute cannot be used within a restricted context";
        r50.<init>(r51);
        throw r50;
    L_0x0442:
        r15 = r6.getString(r7);
        if (r15 == 0) goto L_0x0077;
    L_0x0448:
        r50 = new android.view.View$1;
        r0 = r50;
        r1 = r53;
        r0.<init>(r1, r15);
        r0 = r53;
        r1 = r50;
        r0.setOnClickListener(r1);
        goto L_0x0077;
    L_0x045a:
        r50 = 1;
        r0 = r50;
        r22 = r6.getInt(r7, r0);
        goto L_0x0077;
    L_0x0464:
        r50 = 0;
        r0 = r50;
        r50 = r6.getInt(r7, r0);
        r0 = r50;
        r1 = r53;
        r1.mVerticalScrollbarPosition = r0;
        goto L_0x0077;
    L_0x0474:
        r50 = 0;
        r0 = r50;
        r50 = r6.getInt(r7, r0);
        r51 = 0;
        r0 = r53;
        r1 = r50;
        r2 = r51;
        r0.setLayerType(r1, r2);
        goto L_0x0077;
    L_0x0489:
        r0 = r53;
        r0 = r0.mPrivateFlags2;
        r50 = r0;
        r0 = r50;
        r0 = r0 & -449;
        r50 = r0;
        r0 = r50;
        r1 = r53;
        r1.mPrivateFlags2 = r0;
        r50 = -1;
        r0 = r50;
        r38 = r6.getInt(r7, r0);
        r50 = -1;
        r0 = r38;
        r1 = r50;
        if (r0 == r1) goto L_0x0077;
    L_0x04ab:
        r0 = r53;
        r0 = r0.mPrivateFlags2;
        r50 = r0;
        r51 = PFLAG2_TEXT_DIRECTION_FLAGS;
        r51 = r51[r38];
        r50 = r50 | r51;
        r0 = r50;
        r1 = r53;
        r1.mPrivateFlags2 = r0;
        goto L_0x0077;
    L_0x04bf:
        r0 = r53;
        r0 = r0.mPrivateFlags2;
        r50 = r0;
        r51 = -57345; // 0xffffffffffff1fff float:NaN double:NaN;
        r50 = r50 & r51;
        r0 = r50;
        r1 = r53;
        r1.mPrivateFlags2 = r0;
        r50 = 1;
        r0 = r50;
        r37 = r6.getInt(r7, r0);
        r0 = r53;
        r0 = r0.mPrivateFlags2;
        r50 = r0;
        r51 = PFLAG2_TEXT_ALIGNMENT_FLAGS;
        r51 = r51[r37];
        r50 = r50 | r51;
        r0 = r50;
        r1 = r53;
        r1.mPrivateFlags2 = r0;
        goto L_0x0077;
    L_0x04ec:
        r50 = 0;
        r0 = r50;
        r50 = r6.getInt(r7, r0);
        r0 = r53;
        r1 = r50;
        r0.setImportantForAccessibility(r1);
        goto L_0x0077;
    L_0x04fd:
        r50 = 0;
        r0 = r50;
        r50 = r6.getInt(r7, r0);
        r0 = r53;
        r1 = r50;
        r0.setAccessibilityLiveRegion(r1);
        goto L_0x0077;
    L_0x050e:
        r50 = r6.getString(r7);
        r0 = r53;
        r1 = r50;
        r0.setTransitionName(r1);
        goto L_0x0077;
    L_0x051b:
        r50 = 0;
        r0 = r50;
        r50 = r6.getBoolean(r7, r0);
        r0 = r53;
        r1 = r50;
        r0.setNestedScrollingEnabled(r1);
        goto L_0x0077;
    L_0x052c:
        r50 = 0;
        r0 = r50;
        r50 = r6.getResourceId(r7, r0);
        r0 = r54;
        r1 = r50;
        r50 = android.animation.AnimatorInflater.loadStateListAnimator(r0, r1);
        r0 = r53;
        r1 = r50;
        r0.setStateListAnimator(r1);
        goto L_0x0077;
    L_0x0545:
        r0 = r53;
        r0 = r0.mBackgroundTint;
        r50 = r0;
        if (r50 != 0) goto L_0x055a;
    L_0x054d:
        r50 = new android.view.View$TintInfo;
        r51 = 0;
        r50.<init>(r51);
        r0 = r50;
        r1 = r53;
        r1.mBackgroundTint = r0;
    L_0x055a:
        r0 = r53;
        r0 = r0.mBackgroundTint;
        r50 = r0;
        r51 = 75;
        r0 = r51;
        r51 = r6.getColorStateList(r0);
        r0 = r51;
        r1 = r50;
        r1.mTintList = r0;
        r0 = r53;
        r0 = r0.mBackgroundTint;
        r50 = r0;
        r51 = 1;
        r0 = r51;
        r1 = r50;
        r1.mHasTintList = r0;
        goto L_0x0077;
    L_0x057e:
        r0 = r53;
        r0 = r0.mBackgroundTint;
        r50 = r0;
        if (r50 != 0) goto L_0x0593;
    L_0x0586:
        r50 = new android.view.View$TintInfo;
        r51 = 0;
        r50.<init>(r51);
        r0 = r50;
        r1 = r53;
        r1.mBackgroundTint = r0;
    L_0x0593:
        r0 = r53;
        r0 = r0.mBackgroundTint;
        r50 = r0;
        r51 = 76;
        r52 = -1;
        r0 = r51;
        r1 = r52;
        r51 = r6.getInt(r0, r1);
        r52 = 0;
        r51 = android.graphics.drawable.Drawable.parseTintMode(r51, r52);
        r0 = r51;
        r1 = r50;
        r1.mTintMode = r0;
        r0 = r53;
        r0 = r0.mBackgroundTint;
        r50 = r0;
        r51 = 1;
        r0 = r51;
        r1 = r50;
        r1.mHasTintMode = r0;
        goto L_0x0077;
    L_0x05c1:
        r50 = 77;
        r51 = 0;
        r0 = r50;
        r1 = r51;
        r50 = r6.getInt(r0, r1);
        r0 = r53;
        r1 = r50;
        r0.setOutlineProviderFromAttribute(r1);
        goto L_0x0077;
    L_0x05d6:
        r0 = r53;
        r1 = r22;
        r0.setOverScrollMode(r1);
        r0 = r32;
        r1 = r53;
        r1.mUserPaddingStart = r0;
        r0 = r53;
        r0.mUserPaddingEnd = r12;
        if (r8 == 0) goto L_0x05ee;
    L_0x05e9:
        r0 = r53;
        r0.setBackground(r8);
    L_0x05ee:
        r0 = r21;
        r1 = r53;
        r1.mLeftPaddingDefined = r0;
        r0 = r25;
        r1 = r53;
        r1.mRightPaddingDefined = r0;
        if (r23 < 0) goto L_0x0610;
    L_0x05fc:
        r20 = r23;
        r39 = r23;
        r24 = r23;
        r9 = r23;
        r0 = r23;
        r1 = r53;
        r1.mUserPaddingLeftInitial = r0;
        r0 = r23;
        r1 = r53;
        r1.mUserPaddingRightInitial = r0;
    L_0x0610:
        r50 = r53.isRtlCompatibilityMode();
        if (r50 == 0) goto L_0x06ea;
    L_0x0616:
        r0 = r53;
        r0 = r0.mLeftPaddingDefined;
        r50 = r0;
        if (r50 != 0) goto L_0x0622;
    L_0x061e:
        if (r33 == 0) goto L_0x0622;
    L_0x0620:
        r20 = r32;
    L_0x0622:
        if (r20 < 0) goto L_0x06da;
    L_0x0624:
        r50 = r20;
    L_0x0626:
        r0 = r50;
        r1 = r53;
        r1.mUserPaddingLeftInitial = r0;
        r0 = r53;
        r0 = r0.mRightPaddingDefined;
        r50 = r0;
        if (r50 != 0) goto L_0x0638;
    L_0x0634:
        if (r13 == 0) goto L_0x0638;
    L_0x0636:
        r24 = r12;
    L_0x0638:
        if (r24 < 0) goto L_0x06e2;
    L_0x063a:
        r50 = r24;
    L_0x063c:
        r0 = r50;
        r1 = r53;
        r1.mUserPaddingRightInitial = r0;
    L_0x0642:
        r0 = r53;
        r0 = r0.mUserPaddingLeftInitial;
        r50 = r0;
        if (r39 < 0) goto L_0x0715;
    L_0x064a:
        r0 = r53;
        r0 = r0.mUserPaddingRightInitial;
        r51 = r0;
        if (r9 < 0) goto L_0x071d;
    L_0x0652:
        r0 = r53;
        r1 = r50;
        r2 = r39;
        r3 = r51;
        r0.internalSetPadding(r1, r2, r3, r9);
        if (r45 == 0) goto L_0x0668;
    L_0x065f:
        r0 = r53;
        r1 = r46;
        r2 = r45;
        r0.setFlags(r1, r2);
    L_0x0668:
        if (r18 == 0) goto L_0x066f;
    L_0x066a:
        r0 = r53;
        r0.initializeScrollbarsInternal(r6);
    L_0x066f:
        r6.recycle();
        if (r29 == 0) goto L_0x0677;
    L_0x0674:
        r53.recomputePadding();
    L_0x0677:
        if (r48 != 0) goto L_0x067b;
    L_0x0679:
        if (r49 == 0) goto L_0x0684;
    L_0x067b:
        r0 = r53;
        r1 = r48;
        r2 = r49;
        r0.scrollTo(r1, r2);
    L_0x0684:
        if (r40 == 0) goto L_0x06c3;
    L_0x0686:
        r0 = r53;
        r1 = r41;
        r0.setTranslationX(r1);
        r0 = r53;
        r1 = r42;
        r0.setTranslationY(r1);
        r0 = r53;
        r1 = r43;
        r0.setTranslationZ(r1);
        r0 = r53;
        r0.setElevation(r11);
        r0 = r53;
        r1 = r26;
        r0.setRotation(r1);
        r0 = r53;
        r1 = r27;
        r0.setRotationX(r1);
        r0 = r53;
        r1 = r28;
        r0.setRotationY(r1);
        r0 = r53;
        r1 = r34;
        r0.setScaleX(r1);
        r0 = r53;
        r1 = r35;
        r0.setScaleY(r1);
    L_0x06c3:
        if (r31 != 0) goto L_0x06d6;
    L_0x06c5:
        r0 = r46;
        r0 = r0 & 512;
        r50 = r0;
        if (r50 == 0) goto L_0x06d6;
    L_0x06cd:
        r50 = 1;
        r0 = r53;
        r1 = r50;
        r0.setScrollContainer(r1);
    L_0x06d6:
        r53.computeOpaqueFlags();
        return;
    L_0x06da:
        r0 = r53;
        r0 = r0.mUserPaddingLeftInitial;
        r50 = r0;
        goto L_0x0626;
    L_0x06e2:
        r0 = r53;
        r0 = r0.mUserPaddingRightInitial;
        r50 = r0;
        goto L_0x063c;
    L_0x06ea:
        if (r33 != 0) goto L_0x06ee;
    L_0x06ec:
        if (r13 == 0) goto L_0x0712;
    L_0x06ee:
        r16 = 1;
    L_0x06f0:
        r0 = r53;
        r0 = r0.mLeftPaddingDefined;
        r50 = r0;
        if (r50 == 0) goto L_0x0700;
    L_0x06f8:
        if (r16 != 0) goto L_0x0700;
    L_0x06fa:
        r0 = r20;
        r1 = r53;
        r1.mUserPaddingLeftInitial = r0;
    L_0x0700:
        r0 = r53;
        r0 = r0.mRightPaddingDefined;
        r50 = r0;
        if (r50 == 0) goto L_0x0642;
    L_0x0708:
        if (r16 != 0) goto L_0x0642;
    L_0x070a:
        r0 = r24;
        r1 = r53;
        r1.mUserPaddingRightInitial = r0;
        goto L_0x0642;
    L_0x0712:
        r16 = 0;
        goto L_0x06f0;
    L_0x0715:
        r0 = r53;
        r0 = r0.mPaddingTop;
        r39 = r0;
        goto L_0x064a;
    L_0x071d:
        r0 = r53;
        r9 = r0.mPaddingBottom;
        goto L_0x0652;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.View.<init>(android.content.Context, android.util.AttributeSet, int, int):void");
    }

    View() {
        InputEventConsistencyVerifier inputEventConsistencyVerifier;
        this.mCurrentAnimation = null;
        this.mRecreateDisplayList = DBG;
        this.mID = NO_ID;
        this.mAccessibilityViewId = NO_ID;
        this.mAccessibilityCursorPosition = NO_ID;
        this.mTag = null;
        this.mTransientStateCount = VISIBLE;
        this.mClipBounds = null;
        this.mPaddingLeft = VISIBLE;
        this.mPaddingRight = VISIBLE;
        this.mLabelForId = NO_ID;
        this.mAccessibilityTraversalBeforeId = NO_ID;
        this.mAccessibilityTraversalAfterId = NO_ID;
        this.mLeftPaddingDefined = DBG;
        this.mRightPaddingDefined = DBG;
        this.mOldWidthMeasureSpec = UNDEFINED_PADDING;
        this.mOldHeightMeasureSpec = UNDEFINED_PADDING;
        this.mDrawableState = null;
        this.mOutlineProvider = ViewOutlineProvider.BACKGROUND;
        this.mNextFocusLeftId = NO_ID;
        this.mNextFocusRightId = NO_ID;
        this.mNextFocusUpId = NO_ID;
        this.mNextFocusDownId = NO_ID;
        this.mNextFocusForwardId = NO_ID;
        this.mPendingCheckForTap = null;
        this.mTouchDelegate = null;
        this.mDrawingCacheBackgroundColor = VISIBLE;
        this.mAnimator = null;
        this.mLayerType = VISIBLE;
        if (InputEventConsistencyVerifier.isInstrumentationEnabled()) {
            inputEventConsistencyVerifier = new InputEventConsistencyVerifier(this, VISIBLE);
        } else {
            inputEventConsistencyVerifier = null;
        }
        this.mInputEventConsistencyVerifier = inputEventConsistencyVerifier;
        this.mResources = null;
        this.mRenderNode = RenderNode.create(getClass().getName(), this);
    }

    private static SparseArray<String> getAttributeMap() {
        if (mAttributeMap == null) {
            mAttributeMap = new SparseArray();
        }
        return mAttributeMap;
    }

    private void saveAttributeData(AttributeSet attrs, TypedArray a) {
        int i = VISIBLE;
        if (attrs != null) {
            i = attrs.getAttributeCount();
        }
        this.mAttributes = new String[((i + a.getIndexCount()) * VIEW_STATE_SELECTED)];
        int i2 = VISIBLE;
        if (attrs != null) {
            i2 = VISIBLE;
            while (i2 < attrs.getAttributeCount()) {
                this.mAttributes[i2] = attrs.getAttributeName(i2);
                this.mAttributes[i2 + VIEW_STATE_WINDOW_FOCUSED] = attrs.getAttributeValue(i2);
                i2 += VIEW_STATE_SELECTED;
            }
        }
        SparseArray<String> attributeMap = getAttributeMap();
        for (int j = VISIBLE; j < a.length(); j += VIEW_STATE_WINDOW_FOCUSED) {
            if (a.hasValue(j)) {
                try {
                    int resourceId = a.getResourceId(j, VISIBLE);
                    if (resourceId != 0) {
                        String resourceName = (String) attributeMap.get(resourceId);
                        if (resourceName == null) {
                            resourceName = a.getResources().getResourceName(resourceId);
                            attributeMap.put(resourceId, resourceName);
                        }
                        this.mAttributes[i2] = resourceName;
                        this.mAttributes[i2 + VIEW_STATE_WINDOW_FOCUSED] = a.getText(j).toString();
                        i2 += VIEW_STATE_SELECTED;
                    }
                } catch (NotFoundException e) {
                }
            }
        }
    }

    public String toString() {
        char c;
        char c2 = 'F';
        char c3 = 'D';
        StringBuilder out = new StringBuilder(WILL_NOT_DRAW);
        out.append(getClass().getName());
        out.append('{');
        out.append(Integer.toHexString(System.identityHashCode(this)));
        out.append(' ');
        switch (this.mViewFlags & VISIBILITY_MASK) {
            case VISIBLE /*0*/:
                out.append('V');
                break;
            case VIEW_STATE_FOCUSED /*4*/:
                out.append('I');
                break;
            case VIEW_STATE_ENABLED /*8*/:
                out.append('G');
                break;
            default:
                out.append('.');
                break;
        }
        if ((this.mViewFlags & VIEW_STATE_WINDOW_FOCUSED) == VIEW_STATE_WINDOW_FOCUSED) {
            c = 'F';
        } else {
            c = '.';
        }
        out.append(c);
        if ((this.mViewFlags & VIEW_STATE_ACTIVATED) == 0) {
            c = 'E';
        } else {
            c = '.';
        }
        out.append(c);
        if ((this.mViewFlags & WILL_NOT_DRAW) == WILL_NOT_DRAW) {
            c = '.';
        } else {
            c = 'D';
        }
        out.append(c);
        if ((this.mViewFlags & VIEW_STATE_DRAG_CAN_ACCEPT) != 0) {
            c = 'H';
        } else {
            c = '.';
        }
        out.append(c);
        if ((this.mViewFlags & VIEW_STATE_DRAG_HOVERED) != 0) {
            c = 'V';
        } else {
            c = '.';
        }
        out.append(c);
        if ((this.mViewFlags & RECENT_APPS_VISIBLE) != 0) {
            c = 'C';
        } else {
            c = '.';
        }
        out.append(c);
        if ((this.mViewFlags & STATUS_BAR_DISABLE_HOME) != 0) {
            c = 'L';
        } else {
            c = '.';
        }
        out.append(c);
        out.append(' ');
        if ((this.mPrivateFlags & VIEW_STATE_ENABLED) != 0) {
            c = 'R';
        } else {
            c = '.';
        }
        out.append(c);
        if ((this.mPrivateFlags & VIEW_STATE_SELECTED) == 0) {
            c2 = '.';
        }
        out.append(c2);
        if ((this.mPrivateFlags & VIEW_STATE_FOCUSED) != 0) {
            c = 'S';
        } else {
            c = '.';
        }
        out.append(c);
        if ((this.mPrivateFlags & STATUS_BAR_DISABLE_SEARCH) != 0) {
            out.append('p');
        } else {
            out.append((this.mPrivateFlags & RECENT_APPS_VISIBLE) != 0 ? 'P' : '.');
        }
        if ((this.mPrivateFlags & STATUS_BAR_UNHIDE) != 0) {
            c = 'H';
        } else {
            c = '.';
        }
        out.append(c);
        if ((this.mPrivateFlags & STATUS_BAR_TRANSLUCENT) != 0) {
            c = 'A';
        } else {
            c = '.';
        }
        out.append(c);
        if ((this.mPrivateFlags & UNDEFINED_PADDING) != 0) {
            c = 'I';
        } else {
            c = '.';
        }
        out.append(c);
        if ((this.mPrivateFlags & PFLAG_DIRTY_MASK) == 0) {
            c3 = '.';
        }
        out.append(c3);
        out.append(' ');
        out.append(this.mLeft);
        out.append(PhoneNumberUtils.PAUSE);
        out.append(this.mTop);
        out.append('-');
        out.append(this.mRight);
        out.append(PhoneNumberUtils.PAUSE);
        out.append(this.mBottom);
        int id = getId();
        if (id != NO_ID) {
            out.append(" #");
            out.append(Integer.toHexString(id));
            Resources r = this.mResources;
            if (Resources.resourceHasPackage(id) && r != null) {
                String pkgname;
                switch (MEASURED_STATE_MASK & id) {
                    case STATUS_BAR_DISABLE_RECENT /*16777216*/:
                        pkgname = ZenModeConfig.SYSTEM_AUTHORITY;
                        break;
                    case 2130706432:
                        pkgname = "app";
                        break;
                    default:
                        try {
                            pkgname = r.getResourcePackageName(id);
                            break;
                        } catch (NotFoundException e) {
                            break;
                        }
                }
                String typename = r.getResourceTypeName(id);
                String entryname = r.getResourceEntryName(id);
                out.append(" ");
                out.append(pkgname);
                out.append(":");
                out.append(typename);
                out.append("/");
                out.append(entryname);
            }
        }
        out.append("}");
        return out.toString();
    }

    protected void initializeFadingEdge(TypedArray a) {
        TypedArray arr = this.mContext.obtainStyledAttributes(com.android.internal.R.styleable.View);
        initializeFadingEdgeInternal(arr);
        arr.recycle();
    }

    protected void initializeFadingEdgeInternal(TypedArray a) {
        initScrollCache();
        this.mScrollCache.fadingEdgeLength = a.getDimensionPixelSize(25, ViewConfiguration.get(this.mContext).getScaledFadingEdgeLength());
    }

    public int getVerticalFadingEdgeLength() {
        if (isVerticalFadingEdgeEnabled()) {
            ScrollabilityCache cache = this.mScrollCache;
            if (cache != null) {
                return cache.fadingEdgeLength;
            }
        }
        return VISIBLE;
    }

    public void setFadingEdgeLength(int length) {
        initScrollCache();
        this.mScrollCache.fadingEdgeLength = length;
    }

    public int getHorizontalFadingEdgeLength() {
        if (isHorizontalFadingEdgeEnabled()) {
            ScrollabilityCache cache = this.mScrollCache;
            if (cache != null) {
                return cache.fadingEdgeLength;
            }
        }
        return VISIBLE;
    }

    public int getVerticalScrollbarWidth() {
        ScrollabilityCache cache = this.mScrollCache;
        if (cache == null) {
            return VISIBLE;
        }
        ScrollBarDrawable scrollBar = cache.scrollBar;
        if (scrollBar == null) {
            return VISIBLE;
        }
        int size = scrollBar.getSize(true);
        if (size <= 0) {
            return cache.scrollBarSize;
        }
        return size;
    }

    protected int getHorizontalScrollbarHeight() {
        ScrollabilityCache cache = this.mScrollCache;
        if (cache == null) {
            return VISIBLE;
        }
        ScrollBarDrawable scrollBar = cache.scrollBar;
        if (scrollBar == null) {
            return VISIBLE;
        }
        int size = scrollBar.getSize(DBG);
        if (size <= 0) {
            return cache.scrollBarSize;
        }
        return size;
    }

    protected void initializeScrollbars(TypedArray a) {
        TypedArray arr = this.mContext.obtainStyledAttributes(com.android.internal.R.styleable.View);
        initializeScrollbarsInternal(arr);
        arr.recycle();
    }

    protected void initializeScrollbarsInternal(TypedArray a) {
        initScrollCache();
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        if (scrollabilityCache.scrollBar == null) {
            scrollabilityCache.scrollBar = new ScrollBarDrawable();
        }
        boolean fadeScrollbars = a.getBoolean(45, true);
        if (!fadeScrollbars) {
            scrollabilityCache.state = VIEW_STATE_WINDOW_FOCUSED;
        }
        scrollabilityCache.fadeScrollBars = fadeScrollbars;
        scrollabilityCache.scrollBarFadeDuration = a.getInt(43, ViewConfiguration.getScrollBarFadeDuration());
        scrollabilityCache.scrollBarDefaultDelayBeforeFade = a.getInt(44, ViewConfiguration.getScrollDefaultDelay());
        scrollabilityCache.scrollBarSize = a.getDimensionPixelSize(VIEW_STATE_WINDOW_FOCUSED, ViewConfiguration.get(this.mContext).getScaledScrollBarSize());
        scrollabilityCache.scrollBar.setHorizontalTrackDrawable(a.getDrawable(VIEW_STATE_FOCUSED));
        Drawable thumb = a.getDrawable(VIEW_STATE_SELECTED);
        if (thumb != null) {
            scrollabilityCache.scrollBar.setHorizontalThumbDrawable(thumb);
        }
        if (a.getBoolean(TEXT_ALIGNMENT_VIEW_END, DBG)) {
            scrollabilityCache.scrollBar.setAlwaysDrawHorizontalTrack(true);
        }
        Drawable track = a.getDrawable(TEXT_DIRECTION_LOCALE);
        scrollabilityCache.scrollBar.setVerticalTrackDrawable(track);
        thumb = a.getDrawable(TEXT_DIRECTION_LTR);
        if (thumb != null) {
            scrollabilityCache.scrollBar.setVerticalThumbDrawable(thumb);
        }
        if (a.getBoolean(SYSTEM_UI_CLEARABLE_FLAGS, DBG)) {
            scrollabilityCache.scrollBar.setAlwaysDrawVerticalTrack(true);
        }
        int layoutDirection = getLayoutDirection();
        if (track != null) {
            track.setLayoutDirection(layoutDirection);
        }
        if (thumb != null) {
            thumb.setLayoutDirection(layoutDirection);
        }
        resolvePadding();
    }

    private void initScrollCache() {
        if (this.mScrollCache == null) {
            this.mScrollCache = new ScrollabilityCache(ViewConfiguration.get(this.mContext), this);
        }
    }

    private ScrollabilityCache getScrollCache() {
        initScrollCache();
        return this.mScrollCache;
    }

    public void setVerticalScrollbarPosition(int position) {
        if (this.mVerticalScrollbarPosition != position) {
            this.mVerticalScrollbarPosition = position;
            computeOpaqueFlags();
            resolvePadding();
        }
    }

    public int getVerticalScrollbarPosition() {
        return this.mVerticalScrollbarPosition;
    }

    ListenerInfo getListenerInfo() {
        if (this.mListenerInfo != null) {
            return this.mListenerInfo;
        }
        this.mListenerInfo = new ListenerInfo();
        return this.mListenerInfo;
    }

    public void setOnScrollChangeListener(OnScrollChangeListener l) {
        getListenerInfo().mOnScrollChangeListener = l;
    }

    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        getListenerInfo().mOnFocusChangeListener = l;
    }

    public void addOnLayoutChangeListener(OnLayoutChangeListener listener) {
        ListenerInfo li = getListenerInfo();
        if (li.mOnLayoutChangeListeners == null) {
            li.mOnLayoutChangeListeners = new ArrayList();
        }
        if (!li.mOnLayoutChangeListeners.contains(listener)) {
            li.mOnLayoutChangeListeners.add(listener);
        }
    }

    public void removeOnLayoutChangeListener(OnLayoutChangeListener listener) {
        ListenerInfo li = this.mListenerInfo;
        if (li != null && li.mOnLayoutChangeListeners != null) {
            li.mOnLayoutChangeListeners.remove(listener);
        }
    }

    public void addOnAttachStateChangeListener(OnAttachStateChangeListener listener) {
        ListenerInfo li = getListenerInfo();
        if (li.mOnAttachStateChangeListeners == null) {
            li.mOnAttachStateChangeListeners = new CopyOnWriteArrayList();
        }
        li.mOnAttachStateChangeListeners.add(listener);
    }

    public void removeOnAttachStateChangeListener(OnAttachStateChangeListener listener) {
        ListenerInfo li = this.mListenerInfo;
        if (li != null && li.mOnAttachStateChangeListeners != null) {
            li.mOnAttachStateChangeListeners.remove(listener);
        }
    }

    public OnFocusChangeListener getOnFocusChangeListener() {
        ListenerInfo li = this.mListenerInfo;
        return li != null ? li.mOnFocusChangeListener : null;
    }

    public void setOnClickListener(OnClickListener l) {
        if (!isClickable()) {
            setClickable(true);
        }
        getListenerInfo().mOnClickListener = l;
    }

    public boolean hasOnClickListeners() {
        ListenerInfo li = this.mListenerInfo;
        return (li == null || li.mOnClickListener == null) ? DBG : true;
    }

    public void setOnLongClickListener(OnLongClickListener l) {
        if (!isLongClickable()) {
            setLongClickable(true);
        }
        getListenerInfo().mOnLongClickListener = l;
    }

    public void setOnCreateContextMenuListener(OnCreateContextMenuListener l) {
        if (!isLongClickable()) {
            setLongClickable(true);
        }
        getListenerInfo().mOnCreateContextMenuListener = l;
    }

    public boolean performClick() {
        boolean result;
        ListenerInfo li = this.mListenerInfo;
        if (li == null || li.mOnClickListener == null) {
            result = DBG;
        } else {
            playSoundEffect(VISIBLE);
            li.mOnClickListener.onClick(this);
            result = true;
        }
        sendAccessibilityEvent(VIEW_STATE_WINDOW_FOCUSED);
        return result;
    }

    public boolean callOnClick() {
        ListenerInfo li = this.mListenerInfo;
        if (li == null || li.mOnClickListener == null) {
            return DBG;
        }
        li.mOnClickListener.onClick(this);
        return true;
    }

    public boolean performLongClick() {
        sendAccessibilityEvent(VIEW_STATE_SELECTED);
        boolean handled = DBG;
        ListenerInfo li = this.mListenerInfo;
        if (!(li == null || li.mOnLongClickListener == null)) {
            handled = li.mOnLongClickListener.onLongClick(this);
        }
        if (!handled) {
            handled = showContextMenu();
        }
        if (handled) {
            performHapticFeedback(VISIBLE);
        }
        return handled;
    }

    protected boolean performButtonActionOnTouchDown(MotionEvent event) {
        if ((event.getButtonState() & VIEW_STATE_SELECTED) == 0 || !showContextMenu(event.getX(), event.getY(), event.getMetaState())) {
            return DBG;
        }
        return true;
    }

    public boolean showContextMenu() {
        return getParent().showContextMenuForChild(this);
    }

    public boolean showContextMenu(float x, float y, int metaState) {
        return showContextMenu();
    }

    public ActionMode startActionMode(ActionMode.Callback callback) {
        ViewParent parent = getParent();
        if (parent == null) {
            return null;
        }
        return parent.startActionModeForChild(this, callback);
    }

    public void setOnKeyListener(OnKeyListener l) {
        getListenerInfo().mOnKeyListener = l;
    }

    public void setOnTouchListener(OnTouchListener l) {
        getListenerInfo().mOnTouchListener = l;
    }

    public void setOnGenericMotionListener(OnGenericMotionListener l) {
        getListenerInfo().mOnGenericMotionListener = l;
    }

    public void setOnHoverListener(OnHoverListener l) {
        getListenerInfo().mOnHoverListener = l;
    }

    public void setOnDragListener(OnDragListener l) {
        getListenerInfo().mOnDragListener = l;
    }

    void handleFocusGainInternal(int direction, Rect previouslyFocusedRect) {
        if ((this.mPrivateFlags & VIEW_STATE_SELECTED) == 0) {
            this.mPrivateFlags |= VIEW_STATE_SELECTED;
            View oldFocus = this.mAttachInfo != null ? getRootView().findFocus() : null;
            if (this.mParent != null) {
                this.mParent.requestChildFocus(this, this);
            }
            if (this.mAttachInfo != null) {
                this.mAttachInfo.mTreeObserver.dispatchOnGlobalFocusChange(oldFocus, this);
            }
            onFocusChanged(true, direction, previouslyFocusedRect);
            refreshDrawableState();
        }
    }

    public void getHotspotBounds(Rect outRect) {
        Drawable background = getBackground();
        if (background != null) {
            background.getHotspotBounds(outRect);
        } else {
            getBoundsOnScreen(outRect);
        }
    }

    public boolean requestRectangleOnScreen(Rect rectangle) {
        return requestRectangleOnScreen(rectangle, DBG);
    }

    public boolean requestRectangleOnScreen(Rect rectangle, boolean immediate) {
        if (this.mParent == null) {
            return DBG;
        }
        View child = this;
        RectF position = this.mAttachInfo != null ? this.mAttachInfo.mTmpTransformRect : new RectF();
        position.set(rectangle);
        ViewParent parent = this.mParent;
        boolean scrolled = DBG;
        while (parent != null) {
            rectangle.set((int) position.left, (int) position.top, (int) position.right, (int) position.bottom);
            scrolled |= parent.requestChildRectangleOnScreen(child, rectangle, immediate);
            if (!child.hasIdentityMatrix()) {
                child.getMatrix().mapRect(position);
            }
            position.offset((float) child.mLeft, (float) child.mTop);
            if (!(parent instanceof View)) {
                return scrolled;
            }
            View parentView = (View) parent;
            position.offset((float) (-parentView.getScrollX()), (float) (-parentView.getScrollY()));
            child = parentView;
            parent = child.getParent();
        }
        return scrolled;
    }

    public void clearFocus() {
        clearFocusInternal(null, true, true);
    }

    void clearFocusInternal(View focused, boolean propagate, boolean refocus) {
        if ((this.mPrivateFlags & VIEW_STATE_SELECTED) != 0) {
            this.mPrivateFlags &= -3;
            if (propagate && this.mParent != null) {
                this.mParent.clearChildFocus(this);
            }
            onFocusChanged(DBG, VISIBLE, null);
            refreshDrawableState();
            if (!propagate) {
                return;
            }
            if (!refocus || !rootViewRequestFocus()) {
                notifyGlobalFocusCleared(this);
            }
        }
    }

    void notifyGlobalFocusCleared(View oldFocus) {
        if (oldFocus != null && this.mAttachInfo != null) {
            this.mAttachInfo.mTreeObserver.dispatchOnGlobalFocusChange(oldFocus, null);
        }
    }

    boolean rootViewRequestFocus() {
        View root = getRootView();
        return (root == null || !root.requestFocus()) ? DBG : true;
    }

    void unFocus(View focused) {
        clearFocusInternal(focused, DBG, DBG);
    }

    @ExportedProperty(category = "focus")
    public boolean hasFocus() {
        return (this.mPrivateFlags & VIEW_STATE_SELECTED) != 0 ? true : DBG;
    }

    public boolean hasFocusable() {
        if (!isFocusableInTouchMode()) {
            for (ViewParent p = this.mParent; p instanceof ViewGroup; p = p.getParent()) {
                if (((ViewGroup) p).shouldBlockFocusForTouchscreen()) {
                    return DBG;
                }
            }
        }
        if ((this.mViewFlags & VISIBILITY_MASK) == 0 && isFocusable()) {
            return true;
        }
        return DBG;
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if (gainFocus) {
            sendAccessibilityEvent(VIEW_STATE_ENABLED);
        } else {
            notifyViewAccessibilityStateChangedIfNeeded(VISIBLE);
        }
        InputMethodManager imm = InputMethodManager.peekInstance();
        if (!gainFocus) {
            if (isPressed()) {
                setPressed(DBG);
            }
            if (!(imm == null || this.mAttachInfo == null || !this.mAttachInfo.mHasWindowFocus)) {
                imm.focusOut(this);
            }
            onFocusLost();
        } else if (!(imm == null || this.mAttachInfo == null || !this.mAttachInfo.mHasWindowFocus)) {
            imm.focusIn(this);
        }
        invalidate(true);
        ListenerInfo li = this.mListenerInfo;
        if (!(li == null || li.mOnFocusChangeListener == null)) {
            li.mOnFocusChangeListener.onFocusChange(this, gainFocus);
        }
        if (this.mAttachInfo != null) {
            this.mAttachInfo.mKeyDispatchState.reset(this);
        }
    }

    public void sendAccessibilityEvent(int eventType) {
        if (this.mAccessibilityDelegate != null) {
            this.mAccessibilityDelegate.sendAccessibilityEvent(this, eventType);
        } else {
            sendAccessibilityEventInternal(eventType);
        }
    }

    public void announceForAccessibility(CharSequence text) {
        if (AccessibilityManager.getInstance(this.mContext).isEnabled() && this.mParent != null) {
            AccessibilityEvent event = AccessibilityEvent.obtain((int) RECENT_APPS_VISIBLE);
            onInitializeAccessibilityEvent(event);
            event.getText().add(text);
            event.setContentDescription(null);
            this.mParent.requestSendAccessibilityEvent(this, event);
        }
    }

    void sendAccessibilityEventInternal(int eventType) {
        if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            sendAccessibilityEventUnchecked(AccessibilityEvent.obtain(eventType));
        }
    }

    public void sendAccessibilityEventUnchecked(AccessibilityEvent event) {
        if (this.mAccessibilityDelegate != null) {
            this.mAccessibilityDelegate.sendAccessibilityEventUnchecked(this, event);
        } else {
            sendAccessibilityEventUncheckedInternal(event);
        }
    }

    void sendAccessibilityEventUncheckedInternal(AccessibilityEvent event) {
        if (isShown()) {
            onInitializeAccessibilityEvent(event);
            if ((event.getEventType() & POPULATING_ACCESSIBILITY_EVENT_TYPES) != 0) {
                dispatchPopulateAccessibilityEvent(event);
            }
            getParent().requestSendAccessibilityEvent(this, event);
        }
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        if (this.mAccessibilityDelegate != null) {
            return this.mAccessibilityDelegate.dispatchPopulateAccessibilityEvent(this, event);
        }
        return dispatchPopulateAccessibilityEventInternal(event);
    }

    boolean dispatchPopulateAccessibilityEventInternal(AccessibilityEvent event) {
        onPopulateAccessibilityEvent(event);
        return DBG;
    }

    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        if (this.mAccessibilityDelegate != null) {
            this.mAccessibilityDelegate.onPopulateAccessibilityEvent(this, event);
        } else {
            onPopulateAccessibilityEventInternal(event);
        }
    }

    void onPopulateAccessibilityEventInternal(AccessibilityEvent event) {
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        if (this.mAccessibilityDelegate != null) {
            this.mAccessibilityDelegate.onInitializeAccessibilityEvent(this, event);
        } else {
            onInitializeAccessibilityEventInternal(event);
        }
    }

    void onInitializeAccessibilityEventInternal(AccessibilityEvent event) {
        event.setSource(this);
        event.setClassName(View.class.getName());
        event.setPackageName(getContext().getPackageName());
        event.setEnabled(isEnabled());
        event.setContentDescription(this.mContentDescription);
        switch (event.getEventType()) {
            case VIEW_STATE_ENABLED /*8*/:
                ArrayList<View> focusablesTempList = this.mAttachInfo != null ? this.mAttachInfo.mTempArrayList : new ArrayList();
                getRootView().addFocusables(focusablesTempList, VIEW_STATE_SELECTED, VISIBLE);
                event.setItemCount(focusablesTempList.size());
                event.setCurrentItemIndex(focusablesTempList.indexOf(this));
                if (this.mAttachInfo != null) {
                    focusablesTempList.clear();
                }
            case PFLAG_LAYOUT_REQUIRED /*8192*/:
                CharSequence text = getIterableTextForAccessibility();
                if (text != null && text.length() > 0) {
                    event.setFromIndex(getAccessibilitySelectionStart());
                    event.setToIndex(getAccessibilitySelectionEnd());
                    event.setItemCount(text.length());
                }
            default:
        }
    }

    public AccessibilityNodeInfo createAccessibilityNodeInfo() {
        if (this.mAccessibilityDelegate != null) {
            return this.mAccessibilityDelegate.createAccessibilityNodeInfo(this);
        }
        return createAccessibilityNodeInfoInternal();
    }

    AccessibilityNodeInfo createAccessibilityNodeInfoInternal() {
        AccessibilityNodeProvider provider = getAccessibilityNodeProvider();
        if (provider != null) {
            return provider.createAccessibilityNodeInfo(NO_ID);
        }
        AccessibilityNodeInfo info = AccessibilityNodeInfo.obtain(this);
        onInitializeAccessibilityNodeInfo(info);
        return info;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        if (this.mAccessibilityDelegate != null) {
            this.mAccessibilityDelegate.onInitializeAccessibilityNodeInfo(this, info);
        } else {
            onInitializeAccessibilityNodeInfoInternal(info);
        }
    }

    public void getBoundsOnScreen(Rect outRect) {
        getBoundsOnScreen(outRect, DBG);
    }

    public void getBoundsOnScreen(Rect outRect, boolean clipToParent) {
        if (this.mAttachInfo != null) {
            RectF position = this.mAttachInfo.mTmpTransformRect;
            position.set(0.0f, 0.0f, (float) (this.mRight - this.mLeft), (float) (this.mBottom - this.mTop));
            if (!hasIdentityMatrix()) {
                getMatrix().mapRect(position);
            }
            position.offset((float) this.mLeft, (float) this.mTop);
            ViewParent parent = this.mParent;
            while (parent instanceof View) {
                View parentView = (View) parent;
                position.offset((float) (-parentView.mScrollX), (float) (-parentView.mScrollY));
                if (clipToParent) {
                    position.left = Math.max(position.left, 0.0f);
                    position.top = Math.max(position.top, 0.0f);
                    position.right = Math.min(position.right, (float) parentView.getWidth());
                    position.bottom = Math.min(position.bottom, (float) parentView.getHeight());
                }
                if (!parentView.hasIdentityMatrix()) {
                    parentView.getMatrix().mapRect(position);
                }
                position.offset((float) parentView.mLeft, (float) parentView.mTop);
                parent = parentView.mParent;
            }
            if (parent instanceof ViewRootImpl) {
                position.offset(0.0f, (float) (-((ViewRootImpl) parent).mCurScrollY));
            }
            position.offset((float) this.mAttachInfo.mWindowLeft, (float) this.mAttachInfo.mWindowTop);
            outRect.set((int) (position.left + 0.5f), (int) (position.top + 0.5f), (int) (position.right + 0.5f), (int) (position.bottom + 0.5f));
        }
    }

    void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo info) {
        View rootView;
        View next;
        Rect bounds = this.mAttachInfo.mTmpInvalRect;
        getDrawingRect(bounds);
        info.setBoundsInParent(bounds);
        getBoundsOnScreen(bounds, true);
        info.setBoundsInScreen(bounds);
        ViewParent parent = getParentForAccessibility();
        if (parent instanceof View) {
            info.setParent((View) parent);
        }
        if (this.mID != NO_ID) {
            rootView = getRootView();
            if (rootView == null) {
                rootView = this;
            }
            View label = rootView.findLabelForView(this, this.mID);
            if (label != null) {
                info.setLabeledBy(label);
            }
            if ((this.mAttachInfo.mAccessibilityFetchFlags & VIEW_STATE_PRESSED) != 0 && Resources.resourceHasPackage(this.mID)) {
                try {
                    info.setViewIdResourceName(getResources().getResourceName(this.mID));
                } catch (NotFoundException e) {
                }
            }
        }
        if (this.mLabelForId != NO_ID) {
            rootView = getRootView();
            if (rootView == null) {
                rootView = this;
            }
            View labeled = rootView.findViewInsideOutShouldExist(this, this.mLabelForId);
            if (labeled != null) {
                info.setLabelFor(labeled);
            }
        }
        if (this.mAccessibilityTraversalBeforeId != NO_ID) {
            rootView = getRootView();
            if (rootView == null) {
                rootView = this;
            }
            next = rootView.findViewInsideOutShouldExist(this, this.mAccessibilityTraversalBeforeId);
            if (next != null) {
                info.setTraversalBefore(next);
            }
        }
        if (this.mAccessibilityTraversalAfterId != NO_ID) {
            rootView = getRootView();
            if (rootView == null) {
                rootView = this;
            }
            next = rootView.findViewInsideOutShouldExist(this, this.mAccessibilityTraversalAfterId);
            if (next != null) {
                info.setTraversalAfter(next);
            }
        }
        info.setVisibleToUser(isVisibleToUser());
        info.setPackageName(this.mContext.getPackageName());
        info.setClassName(View.class.getName());
        info.setContentDescription(getContentDescription());
        info.setEnabled(isEnabled());
        info.setClickable(isClickable());
        info.setFocusable(isFocusable());
        info.setFocused(isFocused());
        info.setAccessibilityFocused(isAccessibilityFocused());
        info.setSelected(isSelected());
        info.setLongClickable(isLongClickable());
        info.setLiveRegion(getAccessibilityLiveRegion());
        info.addAction((int) VIEW_STATE_FOCUSED);
        info.addAction((int) VIEW_STATE_ENABLED);
        if (isFocusable()) {
            if (isFocused()) {
                info.addAction((int) VIEW_STATE_SELECTED);
            } else {
                info.addAction((int) VIEW_STATE_WINDOW_FOCUSED);
            }
        }
        if (isAccessibilityFocused()) {
            info.addAction((int) WILL_NOT_DRAW);
        } else {
            info.addAction((int) VIEW_STATE_ACCELERATED);
        }
        if (isClickable() && isEnabled()) {
            info.addAction((int) VIEW_STATE_PRESSED);
        }
        if (isLongClickable() && isEnabled()) {
            info.addAction((int) VIEW_STATE_ACTIVATED);
        }
        CharSequence text = getIterableTextForAccessibility();
        if (text != null && text.length() > 0) {
            info.setTextSelection(getAccessibilitySelectionStart(), getAccessibilitySelectionEnd());
            info.addAction((int) WILL_NOT_CACHE_DRAWING);
            info.addAction((int) VIEW_STATE_DRAG_CAN_ACCEPT);
            info.addAction((int) VIEW_STATE_DRAG_HOVERED);
            info.setMovementGranularities(11);
        }
    }

    private View findLabelForView(View view, int labeledId) {
        if (this.mMatchLabelForPredicate == null) {
            this.mMatchLabelForPredicate = new MatchLabelForPredicate();
        }
        MatchLabelForPredicate.access$902(this.mMatchLabelForPredicate, labeledId);
        return findViewByPredicateInsideOut(view, this.mMatchLabelForPredicate);
    }

    protected boolean isVisibleToUser() {
        return isVisibleToUser(null);
    }

    protected boolean isVisibleToUser(Rect boundInView) {
        if (this.mAttachInfo == null || this.mAttachInfo.mWindowVisibility != 0) {
            return DBG;
        }
        ViewParent viewParent = this;
        while (viewParent instanceof View) {
            View view = (View) viewParent;
            if (view.getAlpha() <= 0.0f || view.getTransitionAlpha() <= 0.0f || view.getVisibility() != 0) {
                return DBG;
            }
            viewParent = view.mParent;
        }
        Rect visibleRect = this.mAttachInfo.mTmpInvalRect;
        Point offset = this.mAttachInfo.mPoint;
        if (!getGlobalVisibleRect(visibleRect, offset)) {
            return DBG;
        }
        if (boundInView == null) {
            return true;
        }
        visibleRect.offset(-offset.x, -offset.y);
        return boundInView.intersect(visibleRect);
    }

    public AccessibilityDelegate getAccessibilityDelegate() {
        return this.mAccessibilityDelegate;
    }

    public void setAccessibilityDelegate(AccessibilityDelegate delegate) {
        this.mAccessibilityDelegate = delegate;
    }

    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        if (this.mAccessibilityDelegate != null) {
            return this.mAccessibilityDelegate.getAccessibilityNodeProvider(this);
        }
        return null;
    }

    public int getAccessibilityViewId() {
        if (this.mAccessibilityViewId == NO_ID) {
            int i = sNextAccessibilityViewId;
            sNextAccessibilityViewId = i + VIEW_STATE_WINDOW_FOCUSED;
            this.mAccessibilityViewId = i;
        }
        return this.mAccessibilityViewId;
    }

    public int getAccessibilityWindowId() {
        return this.mAttachInfo != null ? this.mAttachInfo.mAccessibilityWindowId : ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
    }

    @ExportedProperty(category = "accessibility")
    public CharSequence getContentDescription() {
        return this.mContentDescription;
    }

    @RemotableViewMethod
    public void setContentDescription(CharSequence contentDescription) {
        if (this.mContentDescription == null) {
            if (contentDescription == null) {
                return;
            }
        } else if (this.mContentDescription.equals(contentDescription)) {
            return;
        }
        this.mContentDescription = contentDescription;
        boolean nonEmptyDesc = (contentDescription == null || contentDescription.length() <= 0) ? DBG : true;
        if (nonEmptyDesc && getImportantForAccessibility() == 0) {
            setImportantForAccessibility(VIEW_STATE_WINDOW_FOCUSED);
            notifySubtreeAccessibilityStateChangedIfNeeded();
            return;
        }
        notifyViewAccessibilityStateChangedIfNeeded(VIEW_STATE_FOCUSED);
    }

    @RemotableViewMethod
    public void setAccessibilityTraversalBefore(int beforeId) {
        if (this.mAccessibilityTraversalBeforeId != beforeId) {
            this.mAccessibilityTraversalBeforeId = beforeId;
            notifyViewAccessibilityStateChangedIfNeeded(VISIBLE);
        }
    }

    public int getAccessibilityTraversalBefore() {
        return this.mAccessibilityTraversalBeforeId;
    }

    @RemotableViewMethod
    public void setAccessibilityTraversalAfter(int afterId) {
        if (this.mAccessibilityTraversalAfterId != afterId) {
            this.mAccessibilityTraversalAfterId = afterId;
            notifyViewAccessibilityStateChangedIfNeeded(VISIBLE);
        }
    }

    public int getAccessibilityTraversalAfter() {
        return this.mAccessibilityTraversalAfterId;
    }

    @ExportedProperty(category = "accessibility")
    public int getLabelFor() {
        return this.mLabelForId;
    }

    @RemotableViewMethod
    public void setLabelFor(int id) {
        if (this.mLabelForId != id) {
            this.mLabelForId = id;
            if (this.mLabelForId != NO_ID && this.mID == NO_ID) {
                this.mID = generateViewId();
            }
            notifyViewAccessibilityStateChangedIfNeeded(VISIBLE);
        }
    }

    protected void onFocusLost() {
        resetPressedState();
    }

    private void resetPressedState() {
        if ((this.mViewFlags & VIEW_STATE_ACTIVATED) != VIEW_STATE_ACTIVATED && isPressed()) {
            setPressed(DBG);
            if (!this.mHasPerformedLongPress) {
                removeLongPressCallback();
            }
        }
    }

    @ExportedProperty(category = "focus")
    public boolean isFocused() {
        return (this.mPrivateFlags & VIEW_STATE_SELECTED) != 0 ? true : DBG;
    }

    public View findFocus() {
        return (this.mPrivateFlags & VIEW_STATE_SELECTED) != 0 ? this : null;
    }

    public boolean isScrollContainer() {
        return (this.mPrivateFlags & STATUS_BAR_DISABLE_SYSTEM_INFO) != 0 ? true : DBG;
    }

    public void setScrollContainer(boolean isScrollContainer) {
        if (isScrollContainer) {
            if (this.mAttachInfo != null && (this.mPrivateFlags & STATUS_BAR_DISABLE_SYSTEM_INFO) == 0) {
                this.mAttachInfo.mScrollContainers.add(this);
                this.mPrivateFlags |= STATUS_BAR_DISABLE_SYSTEM_INFO;
            }
            this.mPrivateFlags |= STATUS_BAR_DISABLE_NOTIFICATION_TICKER;
            return;
        }
        if ((this.mPrivateFlags & STATUS_BAR_DISABLE_SYSTEM_INFO) != 0) {
            this.mAttachInfo.mScrollContainers.remove(this);
        }
        this.mPrivateFlags &= -1572865;
    }

    public int getDrawingCacheQuality() {
        return this.mViewFlags & DRAWING_CACHE_QUALITY_MASK;
    }

    public void setDrawingCacheQuality(int quality) {
        setFlags(quality, DRAWING_CACHE_QUALITY_MASK);
    }

    public boolean getKeepScreenOn() {
        return (this.mViewFlags & STATUS_BAR_TRANSIENT) != 0 ? true : DBG;
    }

    public void setKeepScreenOn(boolean keepScreenOn) {
        setFlags(keepScreenOn ? STATUS_BAR_TRANSIENT : VISIBLE, STATUS_BAR_TRANSIENT);
    }

    public int getNextFocusLeftId() {
        return this.mNextFocusLeftId;
    }

    public void setNextFocusLeftId(int nextFocusLeftId) {
        this.mNextFocusLeftId = nextFocusLeftId;
    }

    public int getNextFocusRightId() {
        return this.mNextFocusRightId;
    }

    public void setNextFocusRightId(int nextFocusRightId) {
        this.mNextFocusRightId = nextFocusRightId;
    }

    public int getNextFocusUpId() {
        return this.mNextFocusUpId;
    }

    public void setNextFocusUpId(int nextFocusUpId) {
        this.mNextFocusUpId = nextFocusUpId;
    }

    public int getNextFocusDownId() {
        return this.mNextFocusDownId;
    }

    public void setNextFocusDownId(int nextFocusDownId) {
        this.mNextFocusDownId = nextFocusDownId;
    }

    public int getNextFocusForwardId() {
        return this.mNextFocusForwardId;
    }

    public void setNextFocusForwardId(int nextFocusForwardId) {
        this.mNextFocusForwardId = nextFocusForwardId;
    }

    public boolean isShown() {
        View current = this;
        while ((current.mViewFlags & VISIBILITY_MASK) == 0) {
            ViewParent parent = current.mParent;
            if (parent == null) {
                return DBG;
            }
            if (!(parent instanceof View)) {
                return true;
            }
            current = (View) parent;
            if (current == null) {
                return DBG;
            }
        }
        return DBG;
    }

    protected boolean fitSystemWindows(Rect insets) {
        if ((this.mPrivateFlags3 & VIEW_STATE_ACTIVATED) != 0) {
            return fitSystemWindowsInt(insets);
        }
        if (insets == null) {
            return DBG;
        }
        try {
            this.mPrivateFlags3 |= VIEW_STATE_ACCELERATED;
            boolean isConsumed = dispatchApplyWindowInsets(new WindowInsets(insets)).isConsumed();
            return isConsumed;
        } finally {
            this.mPrivateFlags3 &= -65;
        }
    }

    private boolean fitSystemWindowsInt(Rect insets) {
        if ((this.mViewFlags & VIEW_STATE_SELECTED) != VIEW_STATE_SELECTED) {
            return DBG;
        }
        this.mUserPaddingStart = UNDEFINED_PADDING;
        this.mUserPaddingEnd = UNDEFINED_PADDING;
        Rect localInsets = (Rect) sThreadLocal.get();
        if (localInsets == null) {
            localInsets = new Rect();
            sThreadLocal.set(localInsets);
        }
        boolean res = computeFitSystemWindows(insets, localInsets);
        this.mUserPaddingLeftInitial = localInsets.left;
        this.mUserPaddingRightInitial = localInsets.right;
        internalSetPadding(localInsets.left, localInsets.top, localInsets.right, localInsets.bottom);
        return res;
    }

    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        if ((this.mPrivateFlags3 & VIEW_STATE_ACCELERATED) == 0) {
            if (fitSystemWindows(insets.getSystemWindowInsets())) {
                return insets.consumeSystemWindowInsets();
            }
            return insets;
        } else if (fitSystemWindowsInt(insets.getSystemWindowInsets())) {
            return insets.consumeSystemWindowInsets();
        } else {
            return insets;
        }
    }

    public void setOnApplyWindowInsetsListener(OnApplyWindowInsetsListener listener) {
        getListenerInfo().mOnApplyWindowInsetsListener = listener;
    }

    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        try {
            this.mPrivateFlags3 |= VIEW_STATE_ACTIVATED;
            WindowInsets onApplyWindowInsets;
            if (this.mListenerInfo == null || this.mListenerInfo.mOnApplyWindowInsetsListener == null) {
                onApplyWindowInsets = onApplyWindowInsets(insets);
                this.mPrivateFlags3 &= -33;
                return onApplyWindowInsets;
            }
            onApplyWindowInsets = this.mListenerInfo.mOnApplyWindowInsetsListener.onApplyWindowInsets(this, insets);
            return onApplyWindowInsets;
        } finally {
            this.mPrivateFlags3 &= -33;
        }
    }

    protected boolean computeFitSystemWindows(Rect inoutInsets, Rect outLocalInsets) {
        if ((this.mViewFlags & SYSTEM_UI_FLAG_IMMERSIVE) == 0 || this.mAttachInfo == null || ((this.mAttachInfo.mSystemUiVisibility & SYSTEM_UI_LAYOUT_FLAGS) == 0 && !this.mAttachInfo.mOverscanRequested)) {
            outLocalInsets.set(inoutInsets);
            inoutInsets.set(VISIBLE, VISIBLE, VISIBLE, VISIBLE);
            return true;
        }
        Rect overscan = this.mAttachInfo.mOverscanInsets;
        outLocalInsets.set(overscan);
        inoutInsets.left -= overscan.left;
        inoutInsets.top -= overscan.top;
        inoutInsets.right -= overscan.right;
        inoutInsets.bottom -= overscan.bottom;
        return DBG;
    }

    public WindowInsets computeSystemWindowInsets(WindowInsets in, Rect outLocalInsets) {
        if ((this.mViewFlags & SYSTEM_UI_FLAG_IMMERSIVE) == 0 || this.mAttachInfo == null || (this.mAttachInfo.mSystemUiVisibility & SYSTEM_UI_LAYOUT_FLAGS) == 0) {
            outLocalInsets.set(in.getSystemWindowInsets());
            return in.consumeSystemWindowInsets();
        }
        outLocalInsets.set(VISIBLE, VISIBLE, VISIBLE, VISIBLE);
        return in;
    }

    public void setFitsSystemWindows(boolean fitSystemWindows) {
        setFlags(fitSystemWindows ? VIEW_STATE_SELECTED : VISIBLE, VIEW_STATE_SELECTED);
    }

    @ExportedProperty
    public boolean getFitsSystemWindows() {
        return (this.mViewFlags & VIEW_STATE_SELECTED) == VIEW_STATE_SELECTED ? true : DBG;
    }

    public boolean fitsSystemWindows() {
        return getFitsSystemWindows();
    }

    public void requestFitSystemWindows() {
        if (this.mParent != null) {
            this.mParent.requestFitSystemWindows();
        }
    }

    public void requestApplyInsets() {
        requestFitSystemWindows();
    }

    public void makeOptionalFitsSystemWindows() {
        setFlags(SYSTEM_UI_FLAG_IMMERSIVE, SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @ExportedProperty(mapping = {@IntToString(from = 0, to = "VISIBLE"), @IntToString(from = 4, to = "INVISIBLE"), @IntToString(from = 8, to = "GONE")})
    public int getVisibility() {
        return this.mViewFlags & VISIBILITY_MASK;
    }

    @RemotableViewMethod
    public void setVisibility(int visibility) {
        setFlags(visibility, VISIBILITY_MASK);
        if (this.mBackground != null) {
            boolean z;
            Drawable drawable = this.mBackground;
            if (visibility == 0) {
                z = true;
            } else {
                z = DBG;
            }
            drawable.setVisible(z, DBG);
        }
    }

    @ExportedProperty
    public boolean isEnabled() {
        return (this.mViewFlags & VIEW_STATE_ACTIVATED) == 0 ? true : DBG;
    }

    @RemotableViewMethod
    public void setEnabled(boolean enabled) {
        if (enabled != isEnabled()) {
            setFlags(enabled ? VISIBLE : VIEW_STATE_ACTIVATED, VIEW_STATE_ACTIVATED);
            refreshDrawableState();
            invalidate(true);
            if (!enabled) {
                cancelPendingInputEvents();
            }
        }
    }

    public void setFocusable(boolean focusable) {
        int i = VISIBLE;
        if (!focusable) {
            setFlags(VISIBLE, STATUS_BAR_DISABLE_NOTIFICATION_ALERTS);
        }
        if (focusable) {
            i = VIEW_STATE_WINDOW_FOCUSED;
        }
        setFlags(i, VIEW_STATE_WINDOW_FOCUSED);
    }

    public void setFocusableInTouchMode(boolean focusableInTouchMode) {
        setFlags(focusableInTouchMode ? STATUS_BAR_DISABLE_NOTIFICATION_ALERTS : VISIBLE, STATUS_BAR_DISABLE_NOTIFICATION_ALERTS);
        if (focusableInTouchMode) {
            setFlags(VIEW_STATE_WINDOW_FOCUSED, VIEW_STATE_WINDOW_FOCUSED);
        }
    }

    public void setSoundEffectsEnabled(boolean soundEffectsEnabled) {
        setFlags(soundEffectsEnabled ? SOUND_EFFECTS_ENABLED : VISIBLE, SOUND_EFFECTS_ENABLED);
    }

    @ExportedProperty
    public boolean isSoundEffectsEnabled() {
        return SOUND_EFFECTS_ENABLED == (this.mViewFlags & SOUND_EFFECTS_ENABLED) ? true : DBG;
    }

    public void setHapticFeedbackEnabled(boolean hapticFeedbackEnabled) {
        setFlags(hapticFeedbackEnabled ? STATUS_BAR_UNHIDE : VISIBLE, STATUS_BAR_UNHIDE);
    }

    @ExportedProperty
    public boolean isHapticFeedbackEnabled() {
        return STATUS_BAR_UNHIDE == (this.mViewFlags & STATUS_BAR_UNHIDE) ? true : DBG;
    }

    @ExportedProperty(category = "layout", mapping = {@IntToString(from = 0, to = "LTR"), @IntToString(from = 1, to = "RTL"), @IntToString(from = 2, to = "INHERIT"), @IntToString(from = 3, to = "LOCALE")})
    public int getRawLayoutDirection() {
        return (this.mPrivateFlags2 & VISIBILITY_MASK) >> VIEW_STATE_SELECTED;
    }

    @RemotableViewMethod
    public void setLayoutDirection(int layoutDirection) {
        if (getRawLayoutDirection() != layoutDirection) {
            this.mPrivateFlags2 &= -13;
            resetRtlProperties();
            this.mPrivateFlags2 |= (layoutDirection << VIEW_STATE_SELECTED) & VISIBILITY_MASK;
            resolveRtlPropertiesIfNeeded();
            requestLayout();
            invalidate(true);
        }
    }

    @ExportedProperty(category = "layout", mapping = {@IntToString(from = 0, to = "RESOLVED_DIRECTION_LTR"), @IntToString(from = 1, to = "RESOLVED_DIRECTION_RTL")})
    public int getLayoutDirection() {
        if (getContext().getApplicationInfo().targetSdkVersion < PFLAG2_TEXT_ALIGNMENT_RESOLVED_MASK_SHIFT) {
            this.mPrivateFlags2 |= VIEW_STATE_ACTIVATED;
            return VISIBLE;
        } else if ((this.mPrivateFlags2 & VIEW_STATE_PRESSED) == VIEW_STATE_PRESSED) {
            return VIEW_STATE_WINDOW_FOCUSED;
        } else {
            return VISIBLE;
        }
    }

    @ExportedProperty(category = "layout")
    public boolean isLayoutRtl() {
        return getLayoutDirection() == VIEW_STATE_WINDOW_FOCUSED ? true : DBG;
    }

    @ExportedProperty(category = "layout")
    public boolean hasTransientState() {
        return (this.mPrivateFlags2 & UNDEFINED_PADDING) == UNDEFINED_PADDING ? true : DBG;
    }

    public void setHasTransientState(boolean hasTransientState) {
        this.mTransientStateCount = hasTransientState ? this.mTransientStateCount + VIEW_STATE_WINDOW_FOCUSED : this.mTransientStateCount + NO_ID;
        if (this.mTransientStateCount < 0) {
            this.mTransientStateCount = VISIBLE;
            Log.e(VIEW_LOG_TAG, "hasTransientState decremented below 0: unmatched pair of setHasTransientState calls");
        } else if ((hasTransientState && this.mTransientStateCount == VIEW_STATE_WINDOW_FOCUSED) || (!hasTransientState && this.mTransientStateCount == 0)) {
            int i;
            int i2 = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED & this.mPrivateFlags2;
            if (hasTransientState) {
                i = UNDEFINED_PADDING;
            } else {
                i = VISIBLE;
            }
            this.mPrivateFlags2 = i | i2;
            if (this.mParent != null) {
                try {
                    this.mParent.childHasTransientStateChanged(this, hasTransientState);
                } catch (AbstractMethodError e) {
                    Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                }
            }
        }
    }

    public boolean isAttachedToWindow() {
        return this.mAttachInfo != null ? true : DBG;
    }

    public boolean isLaidOut() {
        return (this.mPrivateFlags3 & VIEW_STATE_FOCUSED) == VIEW_STATE_FOCUSED ? true : DBG;
    }

    public void setWillNotDraw(boolean willNotDraw) {
        setFlags(willNotDraw ? WILL_NOT_DRAW : VISIBLE, WILL_NOT_DRAW);
    }

    @ExportedProperty(category = "drawing")
    public boolean willNotDraw() {
        return (this.mViewFlags & WILL_NOT_DRAW) == WILL_NOT_DRAW ? true : DBG;
    }

    public void setWillNotCacheDrawing(boolean willNotCacheDrawing) {
        setFlags(willNotCacheDrawing ? WILL_NOT_CACHE_DRAWING : VISIBLE, WILL_NOT_CACHE_DRAWING);
    }

    @ExportedProperty(category = "drawing")
    public boolean willNotCacheDrawing() {
        return (this.mViewFlags & WILL_NOT_CACHE_DRAWING) == WILL_NOT_CACHE_DRAWING ? true : DBG;
    }

    @ExportedProperty
    public boolean isClickable() {
        return (this.mViewFlags & RECENT_APPS_VISIBLE) == RECENT_APPS_VISIBLE ? true : DBG;
    }

    public void setClickable(boolean clickable) {
        setFlags(clickable ? RECENT_APPS_VISIBLE : VISIBLE, RECENT_APPS_VISIBLE);
    }

    public boolean isLongClickable() {
        return (this.mViewFlags & STATUS_BAR_DISABLE_HOME) == STATUS_BAR_DISABLE_HOME ? true : DBG;
    }

    public void setLongClickable(boolean longClickable) {
        setFlags(longClickable ? STATUS_BAR_DISABLE_HOME : VISIBLE, STATUS_BAR_DISABLE_HOME);
    }

    private void setPressed(boolean pressed, float x, float y) {
        if (pressed) {
            drawableHotspotChanged(x, y);
        }
        setPressed(pressed);
    }

    public void setPressed(boolean pressed) {
        boolean z;
        boolean needsRefresh = true;
        if ((this.mPrivateFlags & RECENT_APPS_VISIBLE) == RECENT_APPS_VISIBLE) {
            z = true;
        } else {
            z = VISIBLE;
        }
        if (pressed == z) {
            needsRefresh = DBG;
        }
        if (pressed) {
            this.mPrivateFlags |= RECENT_APPS_VISIBLE;
        } else {
            this.mPrivateFlags &= -16385;
        }
        if (needsRefresh) {
            refreshDrawableState();
        }
        dispatchSetPressed(pressed);
    }

    protected void dispatchSetPressed(boolean pressed) {
    }

    @ExportedProperty
    public boolean isPressed() {
        return (this.mPrivateFlags & RECENT_APPS_VISIBLE) == RECENT_APPS_VISIBLE ? true : DBG;
    }

    public boolean isSaveEnabled() {
        return (this.mViewFlags & STATUS_BAR_DISABLE_EXPAND) != STATUS_BAR_DISABLE_EXPAND ? true : DBG;
    }

    public void setSaveEnabled(boolean enabled) {
        int i;
        if (enabled) {
            i = VISIBLE;
        } else {
            i = STATUS_BAR_DISABLE_EXPAND;
        }
        setFlags(i, STATUS_BAR_DISABLE_EXPAND);
    }

    @ExportedProperty
    public boolean getFilterTouchesWhenObscured() {
        return (this.mViewFlags & SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) != 0 ? true : DBG;
    }

    public void setFilterTouchesWhenObscured(boolean enabled) {
        setFlags(enabled ? SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN : VISIBLE, SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public boolean isSaveFromParentEnabled() {
        return (this.mViewFlags & PFLAG_DOES_NOTHING_REUSE_PLEASE) != PFLAG_DOES_NOTHING_REUSE_PLEASE ? true : DBG;
    }

    public void setSaveFromParentEnabled(boolean enabled) {
        int i;
        if (enabled) {
            i = VISIBLE;
        } else {
            i = PFLAG_DOES_NOTHING_REUSE_PLEASE;
        }
        setFlags(i, PFLAG_DOES_NOTHING_REUSE_PLEASE);
    }

    @ExportedProperty(category = "focus")
    public final boolean isFocusable() {
        return VIEW_STATE_WINDOW_FOCUSED == (this.mViewFlags & VIEW_STATE_WINDOW_FOCUSED) ? true : DBG;
    }

    @ExportedProperty
    public final boolean isFocusableInTouchMode() {
        return STATUS_BAR_DISABLE_NOTIFICATION_ALERTS == (this.mViewFlags & STATUS_BAR_DISABLE_NOTIFICATION_ALERTS) ? true : DBG;
    }

    public View focusSearch(int direction) {
        if (this.mParent != null) {
            return this.mParent.focusSearch(this, direction);
        }
        return null;
    }

    public boolean dispatchUnhandledMove(View focused, int direction) {
        return DBG;
    }

    View findUserSetNextFocus(View root, int direction) {
        switch (direction) {
            case VIEW_STATE_WINDOW_FOCUSED /*1*/:
                return this.mID != NO_ID ? root.findViewByPredicateInsideOut(this, new AnonymousClass2(this, this.mID)) : null;
            case VIEW_STATE_SELECTED /*2*/:
                if (this.mNextFocusForwardId != NO_ID) {
                    return findViewInsideOutShouldExist(root, this.mNextFocusForwardId);
                }
                return null;
            case PFLAG2_TEXT_ALIGNMENT_RESOLVED_MASK_SHIFT /*17*/:
                if (this.mNextFocusLeftId != NO_ID) {
                    return findViewInsideOutShouldExist(root, this.mNextFocusLeftId);
                }
                return null;
            case FOCUS_UP /*33*/:
                if (this.mNextFocusUpId != NO_ID) {
                    return findViewInsideOutShouldExist(root, this.mNextFocusUpId);
                }
                return null;
            case FOCUS_RIGHT /*66*/:
                if (this.mNextFocusRightId != NO_ID) {
                    return findViewInsideOutShouldExist(root, this.mNextFocusRightId);
                }
                return null;
            case FOCUS_DOWN /*130*/:
                if (this.mNextFocusDownId != NO_ID) {
                    return findViewInsideOutShouldExist(root, this.mNextFocusDownId);
                }
                return null;
            default:
                return null;
        }
    }

    private View findViewInsideOutShouldExist(View root, int id) {
        if (this.mMatchIdPredicate == null) {
            this.mMatchIdPredicate = new MatchIdPredicate();
        }
        this.mMatchIdPredicate.mId = id;
        View result = root.findViewByPredicateInsideOut(this, this.mMatchIdPredicate);
        if (result == null) {
            Log.w(VIEW_LOG_TAG, "couldn't find view with id " + id);
        }
        return result;
    }

    public ArrayList<View> getFocusables(int direction) {
        ArrayList<View> result = new ArrayList(24);
        addFocusables(result, direction);
        return result;
    }

    public void addFocusables(ArrayList<View> views, int direction) {
        addFocusables(views, direction, VIEW_STATE_WINDOW_FOCUSED);
    }

    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        if (views == null || !isFocusable()) {
            return;
        }
        if ((focusableMode & VIEW_STATE_WINDOW_FOCUSED) != VIEW_STATE_WINDOW_FOCUSED || !isInTouchMode() || isFocusableInTouchMode()) {
            views.add(this);
        }
    }

    public void findViewsWithText(ArrayList<View> outViews, CharSequence searched, int flags) {
        if (getAccessibilityNodeProvider() != null) {
            if ((flags & VIEW_STATE_FOCUSED) != 0) {
                outViews.add(this);
            }
        } else if ((flags & VIEW_STATE_SELECTED) != 0 && searched != null && searched.length() > 0 && this.mContentDescription != null && this.mContentDescription.length() > 0) {
            if (this.mContentDescription.toString().toLowerCase().contains(searched.toString().toLowerCase())) {
                outViews.add(this);
            }
        }
    }

    public ArrayList<View> getTouchables() {
        ArrayList<View> result = new ArrayList();
        addTouchables(result);
        return result;
    }

    public void addTouchables(ArrayList<View> views) {
        int viewFlags = this.mViewFlags;
        if (((viewFlags & RECENT_APPS_VISIBLE) == RECENT_APPS_VISIBLE || (viewFlags & STATUS_BAR_DISABLE_HOME) == STATUS_BAR_DISABLE_HOME) && (viewFlags & VIEW_STATE_ACTIVATED) == 0) {
            views.add(this);
        }
    }

    public boolean isAccessibilityFocused() {
        return (this.mPrivateFlags2 & STATUS_BAR_TRANSIENT) != 0 ? true : DBG;
    }

    public boolean requestAccessibilityFocus() {
        AccessibilityManager manager = AccessibilityManager.getInstance(this.mContext);
        if (!manager.isEnabled() || !manager.isTouchExplorationEnabled() || (this.mViewFlags & VISIBILITY_MASK) != 0 || (this.mPrivateFlags2 & STATUS_BAR_TRANSIENT) != 0) {
            return DBG;
        }
        this.mPrivateFlags2 |= STATUS_BAR_TRANSIENT;
        ViewRootImpl viewRootImpl = getViewRootImpl();
        if (viewRootImpl != null) {
            viewRootImpl.setAccessibilityFocus(this, null);
        }
        invalidate();
        sendAccessibilityEvent(SYSTEM_UI_TRANSPARENT);
        return true;
    }

    public void clearAccessibilityFocus() {
        clearAccessibilityFocusNoCallbacks();
        ViewRootImpl viewRootImpl = getViewRootImpl();
        if (viewRootImpl != null) {
            View focusHost = viewRootImpl.getAccessibilityFocusedHost();
            if (focusHost != null && ViewRootImpl.isViewDescendantOf(focusHost, this)) {
                viewRootImpl.setAccessibilityFocus(null, null);
            }
        }
    }

    private void sendAccessibilityHoverEvent(int eventType) {
        View source = this;
        while (!source.includeForAccessibility()) {
            ViewParent parent = source.getParent();
            if (parent instanceof View) {
                source = (View) parent;
            } else {
                return;
            }
        }
        source.sendAccessibilityEvent(eventType);
    }

    void clearAccessibilityFocusNoCallbacks() {
        if ((this.mPrivateFlags2 & STATUS_BAR_TRANSIENT) != 0) {
            this.mPrivateFlags2 &= -67108865;
            invalidate();
            sendAccessibilityEvent(STATUS_BAR_DISABLE_EXPAND);
        }
    }

    public final boolean requestFocus() {
        return requestFocus(FOCUS_DOWN);
    }

    public final boolean requestFocus(int direction) {
        return requestFocus(direction, null);
    }

    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        return requestFocusNoSearch(direction, previouslyFocusedRect);
    }

    private boolean requestFocusNoSearch(int direction, Rect previouslyFocusedRect) {
        if ((this.mViewFlags & VIEW_STATE_WINDOW_FOCUSED) != VIEW_STATE_WINDOW_FOCUSED || (this.mViewFlags & VISIBILITY_MASK) != 0) {
            return DBG;
        }
        if ((isInTouchMode() && STATUS_BAR_DISABLE_NOTIFICATION_ALERTS != (this.mViewFlags & STATUS_BAR_DISABLE_NOTIFICATION_ALERTS)) || hasAncestorThatBlocksDescendantFocus()) {
            return DBG;
        }
        handleFocusGainInternal(direction, previouslyFocusedRect);
        return true;
    }

    public final boolean requestFocusFromTouch() {
        if (isInTouchMode()) {
            ViewRootImpl viewRoot = getViewRootImpl();
            if (viewRoot != null) {
                viewRoot.ensureTouchMode(DBG);
            }
        }
        return requestFocus(FOCUS_DOWN);
    }

    private boolean hasAncestorThatBlocksDescendantFocus() {
        boolean focusableInTouchMode = isFocusableInTouchMode();
        ViewParent ancestor = this.mParent;
        while (ancestor instanceof ViewGroup) {
            ViewGroup vgAncestor = (ViewGroup) ancestor;
            if (vgAncestor.getDescendantFocusability() == DevicePolicyManager.PASSWORD_QUALITY_COMPLEX || (!focusableInTouchMode && vgAncestor.shouldBlockFocusForTouchscreen())) {
                return true;
            }
            ancestor = vgAncestor.getParent();
        }
        return DBG;
    }

    @ExportedProperty(category = "accessibility", mapping = {@IntToString(from = 0, to = "auto"), @IntToString(from = 1, to = "yes"), @IntToString(from = 2, to = "no"), @IntToString(from = 4, to = "noHideDescendants")})
    public int getImportantForAccessibility() {
        return (this.mPrivateFlags2 & PFLAG2_IMPORTANT_FOR_ACCESSIBILITY_MASK) >> PFLAG2_IMPORTANT_FOR_ACCESSIBILITY_SHIFT;
    }

    public void setAccessibilityLiveRegion(int mode) {
        if (mode != getAccessibilityLiveRegion()) {
            this.mPrivateFlags2 &= -25165825;
            this.mPrivateFlags2 |= (mode << PFLAG2_ACCESSIBILITY_LIVE_REGION_SHIFT) & PFLAG_OPAQUE_MASK;
            notifyViewAccessibilityStateChangedIfNeeded(VISIBLE);
        }
    }

    public int getAccessibilityLiveRegion() {
        return (this.mPrivateFlags2 & PFLAG_OPAQUE_MASK) >> PFLAG2_ACCESSIBILITY_LIVE_REGION_SHIFT;
    }

    public void setImportantForAccessibility(int mode) {
        boolean oldIncludeForAccessibility = true;
        int oldMode = getImportantForAccessibility();
        if (mode != oldMode) {
            boolean maySkipNotify = (oldMode == 0 || mode == 0) ? true : DBG;
            if (!(maySkipNotify && includeForAccessibility())) {
                oldIncludeForAccessibility = DBG;
            }
            this.mPrivateFlags2 &= -7340033;
            this.mPrivateFlags2 |= (mode << PFLAG2_IMPORTANT_FOR_ACCESSIBILITY_SHIFT) & PFLAG2_IMPORTANT_FOR_ACCESSIBILITY_MASK;
            if (maySkipNotify && oldIncludeForAccessibility == includeForAccessibility()) {
                notifyViewAccessibilityStateChangedIfNeeded(VISIBLE);
            } else {
                notifySubtreeAccessibilityStateChangedIfNeeded();
            }
        }
    }

    public boolean isImportantForAccessibility() {
        int mode = (this.mPrivateFlags2 & PFLAG2_IMPORTANT_FOR_ACCESSIBILITY_MASK) >> PFLAG2_IMPORTANT_FOR_ACCESSIBILITY_SHIFT;
        if (mode == VIEW_STATE_SELECTED || mode == VIEW_STATE_FOCUSED) {
            return DBG;
        }
        for (ViewParent parent = this.mParent; parent instanceof View; parent = parent.getParent()) {
            if (((View) parent).getImportantForAccessibility() == VIEW_STATE_FOCUSED) {
                return DBG;
            }
        }
        boolean z = (mode == VIEW_STATE_WINDOW_FOCUSED || isActionableForAccessibility() || hasListenersForAccessibility() || getAccessibilityNodeProvider() != null || getAccessibilityLiveRegion() != 0) ? VIEW_STATE_WINDOW_FOCUSED : DBG;
        return z;
    }

    public ViewParent getParentForAccessibility() {
        if (!(this.mParent instanceof View)) {
            return null;
        }
        if (this.mParent.includeForAccessibility()) {
            return this.mParent;
        }
        return this.mParent.getParentForAccessibility();
    }

    public void addChildrenForAccessibility(ArrayList<View> arrayList) {
    }

    public boolean includeForAccessibility() {
        if (this.mAttachInfo == null) {
            return DBG;
        }
        if ((this.mAttachInfo.mAccessibilityFetchFlags & VIEW_STATE_ENABLED) != 0 || isImportantForAccessibility()) {
            return true;
        }
        return DBG;
    }

    public boolean isActionableForAccessibility() {
        return (isClickable() || isLongClickable() || isFocusable()) ? true : DBG;
    }

    private boolean hasListenersForAccessibility() {
        ListenerInfo info = getListenerInfo();
        return (this.mTouchDelegate == null && info.mOnKeyListener == null && info.mOnTouchListener == null && info.mOnGenericMotionListener == null && info.mOnHoverListener == null && info.mOnDragListener == null) ? DBG : true;
    }

    public void notifyViewAccessibilityStateChangedIfNeeded(int changeType) {
        if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            if (this.mSendViewStateChangedAccessibilityEvent == null) {
                this.mSendViewStateChangedAccessibilityEvent = new SendViewStateChangedAccessibilityEvent();
            }
            this.mSendViewStateChangedAccessibilityEvent.runOrPost(changeType);
        }
    }

    public void notifySubtreeAccessibilityStateChangedIfNeeded() {
        if (AccessibilityManager.getInstance(this.mContext).isEnabled() && (this.mPrivateFlags2 & SOUND_EFFECTS_ENABLED) == 0) {
            this.mPrivateFlags2 |= SOUND_EFFECTS_ENABLED;
            if (this.mParent != null) {
                try {
                    this.mParent.notifySubtreeAccessibilityStateChanged(this, this, VIEW_STATE_WINDOW_FOCUSED);
                } catch (AbstractMethodError e) {
                    Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                }
            }
        }
    }

    void resetSubtreeAccessibilityStateChanged() {
        this.mPrivateFlags2 &= -134217729;
    }

    public boolean dispatchNestedPrePerformAccessibilityAction(int action, Bundle arguments) {
        for (ViewParent p = getParent(); p != null; p = p.getParent()) {
            if (p.onNestedPrePerformAccessibilityAction(this, action, arguments)) {
                return true;
            }
        }
        return DBG;
    }

    public boolean performAccessibilityAction(int action, Bundle arguments) {
        if (this.mAccessibilityDelegate != null) {
            return this.mAccessibilityDelegate.performAccessibilityAction(this, action, arguments);
        }
        return performAccessibilityActionInternal(action, arguments);
    }

    public boolean performAccessibilityActionInternal(int action, Bundle arguments) {
        int end = NO_ID;
        if (isNestedScrollingEnabled() && ((action == PFLAG_LAYOUT_REQUIRED || action == SYSTEM_UI_FLAG_IMMERSIVE_STICKY) && dispatchNestedPrePerformAccessibilityAction(action, arguments))) {
            return true;
        }
        switch (action) {
            case VIEW_STATE_WINDOW_FOCUSED /*1*/:
                if (!hasFocus()) {
                    getViewRootImpl().ensureTouchMode(DBG);
                    return requestFocus();
                }
                break;
            case VIEW_STATE_SELECTED /*2*/:
                if (hasFocus()) {
                    clearFocus();
                    if (isFocused()) {
                        return DBG;
                    }
                    return true;
                }
                break;
            case VIEW_STATE_FOCUSED /*4*/:
                if (!isSelected()) {
                    setSelected(true);
                    return isSelected();
                }
                break;
            case VIEW_STATE_ENABLED /*8*/:
                if (isSelected()) {
                    setSelected(DBG);
                    if (isSelected()) {
                        return DBG;
                    }
                    return true;
                }
                break;
            case VIEW_STATE_PRESSED /*16*/:
                if (isClickable()) {
                    performClick();
                    return true;
                }
                break;
            case VIEW_STATE_ACTIVATED /*32*/:
                if (isLongClickable()) {
                    performLongClick();
                    return true;
                }
                break;
            case VIEW_STATE_ACCELERATED /*64*/:
                if (!isAccessibilityFocused()) {
                    return requestAccessibilityFocus();
                }
                break;
            case WILL_NOT_DRAW /*128*/:
                if (isAccessibilityFocused()) {
                    clearAccessibilityFocus();
                    return true;
                }
                break;
            case VIEW_STATE_DRAG_CAN_ACCEPT /*256*/:
                if (arguments != null) {
                    return traverseAtGranularity(arguments.getInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT), true, arguments.getBoolean(AccessibilityNodeInfo.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN));
                }
                break;
            case VIEW_STATE_DRAG_HOVERED /*512*/:
                if (arguments != null) {
                    return traverseAtGranularity(arguments.getInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT), DBG, arguments.getBoolean(AccessibilityNodeInfo.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN));
                }
                break;
            case WILL_NOT_CACHE_DRAWING /*131072*/:
                if (getIterableTextForAccessibility() == null) {
                    return DBG;
                }
                int start;
                if (arguments != null) {
                    start = arguments.getInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, NO_ID);
                } else {
                    start = NO_ID;
                }
                if (arguments != null) {
                    end = arguments.getInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, NO_ID);
                }
                if (!(getAccessibilitySelectionStart() == start && getAccessibilitySelectionEnd() == end) && start == end) {
                    setAccessibilitySelection(start, end);
                    notifyViewAccessibilityStateChangedIfNeeded(VISIBLE);
                    return true;
                }
        }
        return DBG;
    }

    private boolean traverseAtGranularity(int granularity, boolean forward, boolean extendSelection) {
        CharSequence text = getIterableTextForAccessibility();
        if (text == null || text.length() == 0) {
            return DBG;
        }
        TextSegmentIterator iterator = getIteratorForGranularity(granularity);
        if (iterator == null) {
            return DBG;
        }
        int current = getAccessibilitySelectionEnd();
        if (current == NO_ID) {
            current = forward ? VISIBLE : text.length();
        }
        int[] range = forward ? iterator.following(current) : iterator.preceding(current);
        if (range == null) {
            return DBG;
        }
        int selectionStart;
        int selectionEnd;
        int segmentStart = range[VISIBLE];
        int segmentEnd = range[VIEW_STATE_WINDOW_FOCUSED];
        if (extendSelection && isAccessibilitySelectionExtendable()) {
            selectionStart = getAccessibilitySelectionStart();
            if (selectionStart == NO_ID) {
                if (forward) {
                    selectionStart = segmentStart;
                } else {
                    selectionStart = segmentEnd;
                }
            }
            if (forward) {
                selectionEnd = segmentEnd;
            } else {
                selectionEnd = segmentStart;
            }
        } else {
            selectionEnd = forward ? segmentEnd : segmentStart;
            selectionStart = selectionEnd;
        }
        setAccessibilitySelection(selectionStart, selectionEnd);
        sendViewTextTraversedAtGranularityEvent(forward ? VIEW_STATE_DRAG_CAN_ACCEPT : VIEW_STATE_DRAG_HOVERED, granularity, segmentStart, segmentEnd);
        return true;
    }

    public CharSequence getIterableTextForAccessibility() {
        return getContentDescription();
    }

    public boolean isAccessibilitySelectionExtendable() {
        return DBG;
    }

    public int getAccessibilitySelectionStart() {
        return this.mAccessibilityCursorPosition;
    }

    public int getAccessibilitySelectionEnd() {
        return getAccessibilitySelectionStart();
    }

    public void setAccessibilitySelection(int start, int end) {
        if (start != end || end != this.mAccessibilityCursorPosition) {
            if (start < 0 || start != end || end > getIterableTextForAccessibility().length()) {
                this.mAccessibilityCursorPosition = NO_ID;
            } else {
                this.mAccessibilityCursorPosition = start;
            }
            sendAccessibilityEvent(PFLAG_LAYOUT_REQUIRED);
        }
    }

    private void sendViewTextTraversedAtGranularityEvent(int action, int granularity, int fromIndex, int toIndex) {
        if (this.mParent != null) {
            AccessibilityEvent event = AccessibilityEvent.obtain((int) WILL_NOT_CACHE_DRAWING);
            onInitializeAccessibilityEvent(event);
            onPopulateAccessibilityEvent(event);
            event.setFromIndex(fromIndex);
            event.setToIndex(toIndex);
            event.setAction(action);
            event.setMovementGranularity(granularity);
            this.mParent.requestSendAccessibilityEvent(this, event);
        }
    }

    public TextSegmentIterator getIteratorForGranularity(int granularity) {
        CharSequence text;
        TextSegmentIterator iterator;
        switch (granularity) {
            case VIEW_STATE_WINDOW_FOCUSED /*1*/:
                text = getIterableTextForAccessibility();
                if (text != null && text.length() > 0) {
                    iterator = CharacterTextSegmentIterator.getInstance(this.mContext.getResources().getConfiguration().locale);
                    iterator.initialize(text.toString());
                    return iterator;
                }
            case VIEW_STATE_SELECTED /*2*/:
                text = getIterableTextForAccessibility();
                if (text != null && text.length() > 0) {
                    iterator = WordTextSegmentIterator.getInstance(this.mContext.getResources().getConfiguration().locale);
                    iterator.initialize(text.toString());
                    return iterator;
                }
            case VIEW_STATE_ENABLED /*8*/:
                text = getIterableTextForAccessibility();
                if (text != null && text.length() > 0) {
                    iterator = ParagraphTextSegmentIterator.getInstance();
                    iterator.initialize(text.toString());
                    return iterator;
                }
        }
        return null;
    }

    public void dispatchStartTemporaryDetach() {
        onStartTemporaryDetach();
    }

    public void onStartTemporaryDetach() {
        removeUnsetPressCallback();
        this.mPrivateFlags |= STATUS_BAR_TRANSIENT;
    }

    public void dispatchFinishTemporaryDetach() {
        onFinishTemporaryDetach();
    }

    public void onFinishTemporaryDetach() {
    }

    public DispatcherState getKeyDispatcherState() {
        return this.mAttachInfo != null ? this.mAttachInfo.mKeyDispatchState : null;
    }

    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        return onKeyPreIme(event.getKeyCode(), event);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (this.mInputEventConsistencyVerifier != null) {
            this.mInputEventConsistencyVerifier.onKeyEvent(event, VISIBLE);
        }
        ListenerInfo li = this.mListenerInfo;
        if (li != null && li.mOnKeyListener != null && (this.mViewFlags & VIEW_STATE_ACTIVATED) == 0 && li.mOnKeyListener.onKey(this, event.getKeyCode(), event)) {
            return true;
        }
        if (event.dispatch(this, this.mAttachInfo != null ? this.mAttachInfo.mKeyDispatchState : null, this)) {
            return true;
        }
        if (this.mInputEventConsistencyVerifier != null) {
            this.mInputEventConsistencyVerifier.onUnhandledEvent(event, VISIBLE);
        }
        return DBG;
    }

    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        return onKeyShortcut(event.getKeyCode(), event);
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.isTargetAccessibilityFocus()) {
            if (!isAccessibilityFocusedViewOrHost()) {
                return DBG;
            }
            event.setTargetAccessibilityFocus(DBG);
        }
        boolean result = DBG;
        if (this.mInputEventConsistencyVerifier != null) {
            this.mInputEventConsistencyVerifier.onTouchEvent(event, VISIBLE);
        }
        int actionMasked = event.getActionMasked();
        if (actionMasked == 0) {
            stopNestedScroll();
        }
        if (onFilterTouchEventForSecurity(event)) {
            ListenerInfo li = this.mListenerInfo;
            if (li != null && li.mOnTouchListener != null && (this.mViewFlags & VIEW_STATE_ACTIVATED) == 0 && li.mOnTouchListener.onTouch(this, event)) {
                result = true;
            }
            if (!result && onTouchEvent(event)) {
                result = true;
            }
        }
        if (!(result || this.mInputEventConsistencyVerifier == null)) {
            this.mInputEventConsistencyVerifier.onUnhandledEvent(event, VISIBLE);
        }
        if (actionMasked != VIEW_STATE_WINDOW_FOCUSED && actionMasked != TEXT_DIRECTION_LTR && (actionMasked != 0 || result)) {
            return result;
        }
        stopNestedScroll();
        return result;
    }

    boolean isAccessibilityFocusedViewOrHost() {
        return (isAccessibilityFocused() || (getViewRootImpl() != null && getViewRootImpl().getAccessibilityFocusedHost() == this)) ? true : DBG;
    }

    public boolean onFilterTouchEventForSecurity(MotionEvent event) {
        if ((this.mViewFlags & SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) == 0 || (event.getFlags() & VIEW_STATE_WINDOW_FOCUSED) == 0) {
            return true;
        }
        return DBG;
    }

    public boolean dispatchTrackballEvent(MotionEvent event) {
        if (this.mInputEventConsistencyVerifier != null) {
            this.mInputEventConsistencyVerifier.onTrackballEvent(event, VISIBLE);
        }
        return onTrackballEvent(event);
    }

    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        if (this.mInputEventConsistencyVerifier != null) {
            this.mInputEventConsistencyVerifier.onGenericMotionEvent(event, VISIBLE);
        }
        if ((event.getSource() & VIEW_STATE_SELECTED) != 0) {
            int action = event.getAction();
            if (action == 9 || action == SYSTEM_UI_CLEARABLE_FLAGS || action == PFLAG2_TEXT_DIRECTION_RESOLVED_MASK_SHIFT) {
                if (dispatchHoverEvent(event)) {
                    return true;
                }
            } else if (dispatchGenericPointerEvent(event)) {
                return true;
            }
        } else if (dispatchGenericFocusedEvent(event)) {
            return true;
        }
        if (dispatchGenericMotionEventInternal(event)) {
            return true;
        }
        if (this.mInputEventConsistencyVerifier != null) {
            this.mInputEventConsistencyVerifier.onUnhandledEvent(event, VISIBLE);
        }
        return DBG;
    }

    private boolean dispatchGenericMotionEventInternal(MotionEvent event) {
        ListenerInfo li = this.mListenerInfo;
        if ((li != null && li.mOnGenericMotionListener != null && (this.mViewFlags & VIEW_STATE_ACTIVATED) == 0 && li.mOnGenericMotionListener.onGenericMotion(this, event)) || onGenericMotionEvent(event)) {
            return true;
        }
        if (this.mInputEventConsistencyVerifier != null) {
            this.mInputEventConsistencyVerifier.onUnhandledEvent(event, VISIBLE);
        }
        return DBG;
    }

    protected boolean dispatchHoverEvent(MotionEvent event) {
        ListenerInfo li = this.mListenerInfo;
        if (li == null || li.mOnHoverListener == null || (this.mViewFlags & VIEW_STATE_ACTIVATED) != 0 || !li.mOnHoverListener.onHover(this, event)) {
            return onHoverEvent(event);
        }
        return true;
    }

    protected boolean hasHoveredChild() {
        return DBG;
    }

    protected boolean dispatchGenericPointerEvent(MotionEvent event) {
        return DBG;
    }

    protected boolean dispatchGenericFocusedEvent(MotionEvent event) {
        return DBG;
    }

    public final boolean dispatchPointerEvent(MotionEvent event) {
        if (event.isTouchEvent()) {
            return dispatchTouchEvent(event);
        }
        return dispatchGenericMotionEvent(event);
    }

    public void dispatchWindowFocusChanged(boolean hasFocus) {
        onWindowFocusChanged(hasFocus);
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        InputMethodManager imm = InputMethodManager.peekInstance();
        if (!hasWindowFocus) {
            if (isPressed()) {
                setPressed(DBG);
            }
            if (!(imm == null || (this.mPrivateFlags & VIEW_STATE_SELECTED) == 0)) {
                imm.focusOut(this);
            }
            removeLongPressCallback();
            removeTapCallback();
            onFocusLost();
        } else if (!(imm == null || (this.mPrivateFlags & VIEW_STATE_SELECTED) == 0)) {
            imm.focusIn(this);
        }
        refreshDrawableState();
    }

    public boolean hasWindowFocus() {
        return (this.mAttachInfo == null || !this.mAttachInfo.mHasWindowFocus) ? DBG : true;
    }

    protected void dispatchVisibilityChanged(View changedView, int visibility) {
        onVisibilityChanged(changedView, visibility);
    }

    protected void onVisibilityChanged(View changedView, int visibility) {
        if (visibility != 0) {
            return;
        }
        if (this.mAttachInfo != null) {
            initialAwakenScrollBars();
        } else {
            this.mPrivateFlags |= SOUND_EFFECTS_ENABLED;
        }
    }

    public void dispatchDisplayHint(int hint) {
        onDisplayHint(hint);
    }

    protected void onDisplayHint(int hint) {
    }

    public void dispatchWindowVisibilityChanged(int visibility) {
        onWindowVisibilityChanged(visibility);
    }

    protected void onWindowVisibilityChanged(int visibility) {
        if (visibility == 0) {
            initialAwakenScrollBars();
        }
    }

    public int getWindowVisibility() {
        return this.mAttachInfo != null ? this.mAttachInfo.mWindowVisibility : VIEW_STATE_ENABLED;
    }

    public void getWindowVisibleDisplayFrame(Rect outRect) {
        if (this.mAttachInfo != null) {
            try {
                this.mAttachInfo.mSession.getDisplayFrame(this.mAttachInfo.mWindow, outRect);
                Rect insets = this.mAttachInfo.mVisibleInsets;
                outRect.left += insets.left;
                outRect.top += insets.top;
                outRect.right -= insets.right;
                outRect.bottom -= insets.bottom;
                return;
            } catch (RemoteException e) {
                return;
            }
        }
        DisplayManagerGlobal.getInstance().getRealDisplay(VISIBLE).getRectSize(outRect);
    }

    public void dispatchConfigurationChanged(Configuration newConfig) {
        onConfigurationChanged(newConfig);
    }

    protected void onConfigurationChanged(Configuration newConfig) {
    }

    void dispatchCollectViewAttributes(AttachInfo attachInfo, int visibility) {
        performCollectViewAttributes(attachInfo, visibility);
    }

    void performCollectViewAttributes(AttachInfo attachInfo, int visibility) {
        if ((visibility & VISIBILITY_MASK) == 0) {
            if ((this.mViewFlags & STATUS_BAR_TRANSIENT) == STATUS_BAR_TRANSIENT) {
                attachInfo.mKeepScreenOn = true;
            }
            attachInfo.mSystemUiVisibility |= this.mSystemUiVisibility;
            ListenerInfo li = this.mListenerInfo;
            if (li != null && li.mOnSystemUiVisibilityChangeListener != null) {
                attachInfo.mHasSystemUiListeners = true;
            }
        }
    }

    void needGlobalAttributesUpdate(boolean force) {
        AttachInfo ai = this.mAttachInfo;
        if (ai != null && !ai.mRecomputeGlobalAttributes) {
            if (force || ai.mKeepScreenOn || ai.mSystemUiVisibility != 0 || ai.mHasSystemUiListeners) {
                ai.mRecomputeGlobalAttributes = true;
            }
        }
    }

    @ExportedProperty
    public boolean isInTouchMode() {
        if (this.mAttachInfo != null) {
            return this.mAttachInfo.mInTouchMode;
        }
        return ViewRootImpl.isInTouchMode();
    }

    @CapturedViewProperty
    public final Context getContext() {
        return this.mContext;
    }

    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        return DBG;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!KeyEvent.isConfirmKey(keyCode)) {
            return DBG;
        }
        if ((this.mViewFlags & VIEW_STATE_ACTIVATED) == VIEW_STATE_ACTIVATED) {
            return true;
        }
        if (((this.mViewFlags & RECENT_APPS_VISIBLE) != RECENT_APPS_VISIBLE && (this.mViewFlags & STATUS_BAR_DISABLE_HOME) != STATUS_BAR_DISABLE_HOME) || event.getRepeatCount() != 0) {
            return DBG;
        }
        setPressed(true);
        checkForLongClick(VISIBLE);
        return true;
    }

    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return DBG;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (!KeyEvent.isConfirmKey(keyCode)) {
            return DBG;
        }
        if ((this.mViewFlags & VIEW_STATE_ACTIVATED) == VIEW_STATE_ACTIVATED) {
            return true;
        }
        if ((this.mViewFlags & RECENT_APPS_VISIBLE) != RECENT_APPS_VISIBLE || !isPressed()) {
            return DBG;
        }
        setPressed(DBG);
        if (this.mHasPerformedLongPress) {
            return DBG;
        }
        removeLongPressCallback();
        return performClick();
    }

    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return DBG;
    }

    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        return DBG;
    }

    public boolean onCheckIsTextEditor() {
        return DBG;
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return null;
    }

    public boolean checkInputConnectionProxy(View view) {
        return DBG;
    }

    public void createContextMenu(ContextMenu menu) {
        ContextMenuInfo menuInfo = getContextMenuInfo();
        ((MenuBuilder) menu).setCurrentMenuInfo(menuInfo);
        onCreateContextMenu(menu);
        ListenerInfo li = this.mListenerInfo;
        if (!(li == null || li.mOnCreateContextMenuListener == null)) {
            li.mOnCreateContextMenuListener.onCreateContextMenu(menu, this, menuInfo);
        }
        ((MenuBuilder) menu).setCurrentMenuInfo(null);
        if (this.mParent != null) {
            this.mParent.createContextMenu(menu);
        }
    }

    protected ContextMenuInfo getContextMenuInfo() {
        return null;
    }

    protected void onCreateContextMenu(ContextMenu menu) {
    }

    public boolean onTrackballEvent(MotionEvent event) {
        return DBG;
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        return DBG;
    }

    public boolean onHoverEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (this.mSendingHoverAccessibilityEvents) {
            if (action == PFLAG2_TEXT_DIRECTION_RESOLVED_MASK_SHIFT || (action == VIEW_STATE_SELECTED && !pointInView(event.getX(), event.getY()))) {
                this.mSendingHoverAccessibilityEvents = DBG;
                sendAccessibilityHoverEvent(VIEW_STATE_DRAG_CAN_ACCEPT);
            }
        } else if ((action == 9 || action == SYSTEM_UI_CLEARABLE_FLAGS) && !hasHoveredChild() && pointInView(event.getX(), event.getY())) {
            sendAccessibilityHoverEvent(WILL_NOT_DRAW);
            this.mSendingHoverAccessibilityEvents = true;
        }
        if (!isHoverable()) {
            return DBG;
        }
        switch (action) {
            case SetOnClickFillInIntent.TAG /*9*/:
                setHovered(true);
                break;
            case PFLAG2_TEXT_DIRECTION_RESOLVED_MASK_SHIFT /*10*/:
                setHovered(DBG);
                break;
        }
        dispatchGenericMotionEventInternal(event);
        return true;
    }

    private boolean isHoverable() {
        int viewFlags = this.mViewFlags;
        if ((viewFlags & VIEW_STATE_ACTIVATED) == VIEW_STATE_ACTIVATED) {
            return DBG;
        }
        if ((viewFlags & RECENT_APPS_VISIBLE) == RECENT_APPS_VISIBLE || (viewFlags & STATUS_BAR_DISABLE_HOME) == STATUS_BAR_DISABLE_HOME) {
            return true;
        }
        return DBG;
    }

    @ExportedProperty
    public boolean isHovered() {
        return (this.mPrivateFlags & STATUS_BAR_UNHIDE) != 0 ? true : DBG;
    }

    public void setHovered(boolean hovered) {
        if (hovered) {
            if ((this.mPrivateFlags & STATUS_BAR_UNHIDE) == 0) {
                this.mPrivateFlags |= STATUS_BAR_UNHIDE;
                refreshDrawableState();
                onHoverChanged(true);
            }
        } else if ((this.mPrivateFlags & STATUS_BAR_UNHIDE) != 0) {
            this.mPrivateFlags &= -268435457;
            refreshDrawableState();
            onHoverChanged(DBG);
        }
    }

    public void onHoverChanged(boolean hovered) {
    }

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int viewFlags = this.mViewFlags;
        if ((viewFlags & VIEW_STATE_ACTIVATED) == VIEW_STATE_ACTIVATED) {
            if (event.getAction() == VIEW_STATE_WINDOW_FOCUSED && (this.mPrivateFlags & RECENT_APPS_VISIBLE) != 0) {
                setPressed(DBG);
            }
            if ((viewFlags & RECENT_APPS_VISIBLE) == RECENT_APPS_VISIBLE || (viewFlags & STATUS_BAR_DISABLE_HOME) == STATUS_BAR_DISABLE_HOME) {
                return true;
            }
            return DBG;
        } else if (this.mTouchDelegate != null && this.mTouchDelegate.onTouchEvent(event)) {
            return true;
        } else {
            if ((viewFlags & RECENT_APPS_VISIBLE) != RECENT_APPS_VISIBLE && (viewFlags & STATUS_BAR_DISABLE_HOME) != STATUS_BAR_DISABLE_HOME) {
                return DBG;
            }
            switch (event.getAction()) {
                case VISIBLE /*0*/:
                    this.mHasPerformedLongPress = DBG;
                    if (!performButtonActionOnTouchDown(event)) {
                        if (!isInScrollingContainer()) {
                            setPressed(true, x, y);
                            checkForLongClick(VISIBLE);
                            break;
                        }
                        this.mPrivateFlags |= STATUS_BAR_DISABLE_SEARCH;
                        if (this.mPendingCheckForTap == null) {
                            this.mPendingCheckForTap = new CheckForTap();
                        }
                        this.mPendingCheckForTap.x = event.getX();
                        this.mPendingCheckForTap.y = event.getY();
                        postDelayed(this.mPendingCheckForTap, (long) ViewConfiguration.getTapTimeout());
                        break;
                    }
                    break;
                case VIEW_STATE_WINDOW_FOCUSED /*1*/:
                    boolean prepressed;
                    if ((this.mPrivateFlags & STATUS_BAR_DISABLE_SEARCH) != 0) {
                        prepressed = true;
                    } else {
                        prepressed = DBG;
                    }
                    if ((this.mPrivateFlags & RECENT_APPS_VISIBLE) != 0 || prepressed) {
                        boolean focusTaken = DBG;
                        if (isFocusable() && isFocusableInTouchMode() && !isFocused()) {
                            focusTaken = requestFocus();
                        }
                        if (prepressed) {
                            setPressed(true, x, y);
                        }
                        if (!this.mHasPerformedLongPress) {
                            removeLongPressCallback();
                            if (!focusTaken) {
                                if (this.mPerformClick == null) {
                                    this.mPerformClick = new PerformClick();
                                }
                                if (!post(this.mPerformClick)) {
                                    performClick();
                                }
                            }
                        }
                        if (this.mUnsetPressedState == null) {
                            this.mUnsetPressedState = new UnsetPressedState();
                        }
                        if (prepressed) {
                            postDelayed(this.mUnsetPressedState, (long) ViewConfiguration.getPressedStateDuration());
                        } else if (!post(this.mUnsetPressedState)) {
                            this.mUnsetPressedState.run();
                        }
                        removeTapCallback();
                        break;
                    }
                case VIEW_STATE_SELECTED /*2*/:
                    drawableHotspotChanged(x, y);
                    if (!pointInView(x, y, (float) this.mTouchSlop)) {
                        removeTapCallback();
                        if ((this.mPrivateFlags & RECENT_APPS_VISIBLE) != 0) {
                            removeLongPressCallback();
                            setPressed(DBG);
                            break;
                        }
                    }
                    break;
                case TEXT_DIRECTION_LTR /*3*/:
                    setPressed(DBG);
                    removeTapCallback();
                    removeLongPressCallback();
                    break;
            }
            return true;
        }
    }

    public boolean isInScrollingContainer() {
        ViewParent p = getParent();
        while (p != null && (p instanceof ViewGroup)) {
            if (((ViewGroup) p).shouldDelayChildPressedState()) {
                return true;
            }
            p = p.getParent();
        }
        return DBG;
    }

    private void removeLongPressCallback() {
        if (this.mPendingCheckForLongPress != null) {
            removeCallbacks(this.mPendingCheckForLongPress);
        }
    }

    private void removePerformClickCallback() {
        if (this.mPerformClick != null) {
            removeCallbacks(this.mPerformClick);
        }
    }

    private void removeUnsetPressCallback() {
        if ((this.mPrivateFlags & RECENT_APPS_VISIBLE) != 0 && this.mUnsetPressedState != null) {
            setPressed(DBG);
            removeCallbacks(this.mUnsetPressedState);
        }
    }

    private void removeTapCallback() {
        if (this.mPendingCheckForTap != null) {
            this.mPrivateFlags &= -33554433;
            removeCallbacks(this.mPendingCheckForTap);
        }
    }

    public void cancelLongPress() {
        removeLongPressCallback();
        removeTapCallback();
    }

    private void removeSendViewScrolledAccessibilityEventCallback() {
        if (this.mSendViewScrolledAccessibilityEvent != null) {
            removeCallbacks(this.mSendViewScrolledAccessibilityEvent);
            this.mSendViewScrolledAccessibilityEvent.mIsPending = DBG;
        }
    }

    public void setTouchDelegate(TouchDelegate delegate) {
        this.mTouchDelegate = delegate;
    }

    public TouchDelegate getTouchDelegate() {
        return this.mTouchDelegate;
    }

    public final void requestUnbufferedDispatch(MotionEvent event) {
        int action = event.getAction();
        if (this.mAttachInfo == null) {
            return;
        }
        if ((action == 0 || action == VIEW_STATE_SELECTED) && event.isTouchEvent()) {
            this.mAttachInfo.mUnbufferedDispatchRequested = true;
        }
    }

    void setFlags(int flags, int mask) {
        boolean oldIncludeForAccessibility;
        boolean accessibilityEnabled = AccessibilityManager.getInstance(this.mContext).isEnabled();
        if (accessibilityEnabled && includeForAccessibility()) {
            oldIncludeForAccessibility = true;
        } else {
            oldIncludeForAccessibility = DBG;
        }
        int old = this.mViewFlags;
        this.mViewFlags = (this.mViewFlags & (mask ^ NO_ID)) | (flags & mask);
        int changed = this.mViewFlags ^ old;
        if (changed != 0) {
            int privateFlags = this.mPrivateFlags;
            if (!((changed & VIEW_STATE_WINDOW_FOCUSED) == 0 || (privateFlags & VIEW_STATE_PRESSED) == 0)) {
                if ((old & VIEW_STATE_WINDOW_FOCUSED) == VIEW_STATE_WINDOW_FOCUSED && (privateFlags & VIEW_STATE_SELECTED) != 0) {
                    clearFocus();
                } else if ((old & VIEW_STATE_WINDOW_FOCUSED) == 0 && (privateFlags & VIEW_STATE_SELECTED) == 0 && this.mParent != null) {
                    this.mParent.focusableViewAvailable(this);
                }
            }
            int newVisibility = flags & VISIBILITY_MASK;
            if (newVisibility == 0 && (changed & VISIBILITY_MASK) != 0) {
                this.mPrivateFlags |= VIEW_STATE_ACTIVATED;
                invalidate(true);
                needGlobalAttributesUpdate(true);
                if (this.mParent != null && this.mBottom > this.mTop && this.mRight > this.mLeft) {
                    this.mParent.focusableViewAvailable(this);
                }
            }
            if ((changed & VIEW_STATE_ENABLED) != 0) {
                needGlobalAttributesUpdate(DBG);
                requestLayout();
                if ((this.mViewFlags & VISIBILITY_MASK) == VIEW_STATE_ENABLED) {
                    if (hasFocus()) {
                        clearFocus();
                    }
                    clearAccessibilityFocus();
                    destroyDrawingCache();
                    if (this.mParent instanceof View) {
                        ((View) this.mParent).invalidate(true);
                    }
                    this.mPrivateFlags |= VIEW_STATE_ACTIVATED;
                }
                if (this.mAttachInfo != null) {
                    this.mAttachInfo.mViewVisibilityChanged = true;
                }
            }
            if ((changed & VIEW_STATE_FOCUSED) != 0) {
                needGlobalAttributesUpdate(DBG);
                this.mPrivateFlags |= VIEW_STATE_ACTIVATED;
                if ((this.mViewFlags & VISIBILITY_MASK) == VIEW_STATE_FOCUSED && getRootView() != this) {
                    if (hasFocus()) {
                        clearFocus();
                    }
                    clearAccessibilityFocus();
                }
                if (this.mAttachInfo != null) {
                    this.mAttachInfo.mViewVisibilityChanged = true;
                }
            }
            if ((changed & VISIBILITY_MASK) != 0) {
                if (!(newVisibility == 0 || this.mAttachInfo == null)) {
                    cleanupDraw();
                }
                if (this.mParent instanceof ViewGroup) {
                    ((ViewGroup) this.mParent).onChildVisibilityChanged(this, changed & VISIBILITY_MASK, newVisibility);
                    ((View) this.mParent).invalidate(true);
                } else if (this.mParent != null) {
                    this.mParent.invalidateChild(this, null);
                }
                dispatchVisibilityChanged(this, newVisibility);
                notifySubtreeAccessibilityStateChangedIfNeeded();
            }
            if ((WILL_NOT_CACHE_DRAWING & changed) != 0) {
                destroyDrawingCache();
            }
            if ((SYSTEM_UI_TRANSPARENT & changed) != 0) {
                destroyDrawingCache();
                this.mPrivateFlags &= -32769;
                invalidateParentCaches();
            }
            if ((DRAWING_CACHE_QUALITY_MASK & changed) != 0) {
                destroyDrawingCache();
                this.mPrivateFlags &= -32769;
            }
            if ((changed & WILL_NOT_DRAW) != 0) {
                if ((this.mViewFlags & WILL_NOT_DRAW) == 0) {
                    this.mPrivateFlags &= -129;
                } else if (this.mBackground != null) {
                    this.mPrivateFlags &= -129;
                    this.mPrivateFlags |= VIEW_STATE_DRAG_CAN_ACCEPT;
                } else {
                    this.mPrivateFlags |= WILL_NOT_DRAW;
                }
                requestLayout();
                invalidate(true);
            }
            if (!((STATUS_BAR_TRANSIENT & changed) == 0 || this.mParent == null || this.mAttachInfo == null || this.mAttachInfo.mRecomputeGlobalAttributes)) {
                this.mParent.recomputeViewAttributes(this);
            }
            if (!accessibilityEnabled) {
                return;
            }
            if ((changed & VIEW_STATE_WINDOW_FOCUSED) == 0 && (changed & VISIBILITY_MASK) == 0 && (changed & RECENT_APPS_VISIBLE) == 0 && (STATUS_BAR_DISABLE_HOME & changed) == 0) {
                if ((changed & VIEW_STATE_ACTIVATED) != 0) {
                    notifyViewAccessibilityStateChangedIfNeeded(VISIBLE);
                }
            } else if (oldIncludeForAccessibility != includeForAccessibility()) {
                notifySubtreeAccessibilityStateChangedIfNeeded();
            } else {
                notifyViewAccessibilityStateChangedIfNeeded(VISIBLE);
            }
        }
    }

    public void bringToFront() {
        if (this.mParent != null) {
            this.mParent.bringChildToFront(this);
        }
    }

    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            postSendViewScrolledAccessibilityEventCallback();
        }
        this.mBackgroundSizeChanged = true;
        AttachInfo ai = this.mAttachInfo;
        if (ai != null) {
            ai.mViewScrollChanged = true;
        }
        if (this.mListenerInfo != null && this.mListenerInfo.mOnScrollChangeListener != null) {
            this.mListenerInfo.mOnScrollChangeListener.onScrollChange(this, l, t, oldl, oldt);
        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    }

    protected void dispatchDraw(Canvas canvas) {
    }

    public final ViewParent getParent() {
        return this.mParent;
    }

    public void setScrollX(int value) {
        scrollTo(value, this.mScrollY);
    }

    public void setScrollY(int value) {
        scrollTo(this.mScrollX, value);
    }

    public final int getScrollX() {
        return this.mScrollX;
    }

    public final int getScrollY() {
        return this.mScrollY;
    }

    @ExportedProperty(category = "layout")
    public final int getWidth() {
        return this.mRight - this.mLeft;
    }

    @ExportedProperty(category = "layout")
    public final int getHeight() {
        return this.mBottom - this.mTop;
    }

    public void getDrawingRect(Rect outRect) {
        outRect.left = this.mScrollX;
        outRect.top = this.mScrollY;
        outRect.right = this.mScrollX + (this.mRight - this.mLeft);
        outRect.bottom = this.mScrollY + (this.mBottom - this.mTop);
    }

    public final int getMeasuredWidth() {
        return this.mMeasuredWidth & MEASURED_SIZE_MASK;
    }

    public final int getMeasuredWidthAndState() {
        return this.mMeasuredWidth;
    }

    public final int getMeasuredHeight() {
        return this.mMeasuredHeight & MEASURED_SIZE_MASK;
    }

    public final int getMeasuredHeightAndState() {
        return this.mMeasuredHeight;
    }

    public final int getMeasuredState() {
        return (this.mMeasuredWidth & MEASURED_STATE_MASK) | ((this.mMeasuredHeight >> VIEW_STATE_PRESSED) & InputDevice.SOURCE_ANY);
    }

    public Matrix getMatrix() {
        ensureTransformationInfo();
        Matrix matrix = this.mTransformationInfo.mMatrix;
        this.mRenderNode.getMatrix(matrix);
        return matrix;
    }

    final boolean hasIdentityMatrix() {
        return this.mRenderNode.hasIdentityMatrix();
    }

    void ensureTransformationInfo() {
        if (this.mTransformationInfo == null) {
            this.mTransformationInfo = new TransformationInfo();
        }
    }

    public final Matrix getInverseMatrix() {
        ensureTransformationInfo();
        if (this.mTransformationInfo.mInverseMatrix == null) {
            this.mTransformationInfo.mInverseMatrix = new Matrix();
        }
        Matrix matrix = this.mTransformationInfo.mInverseMatrix;
        this.mRenderNode.getInverseMatrix(matrix);
        return matrix;
    }

    public float getCameraDistance() {
        return -(this.mRenderNode.getCameraDistance() * ((float) this.mResources.getDisplayMetrics().densityDpi));
    }

    public void setCameraDistance(float distance) {
        float dpi = (float) this.mResources.getDisplayMetrics().densityDpi;
        invalidateViewProperty(true, DBG);
        this.mRenderNode.setCameraDistance((-Math.abs(distance)) / dpi);
        invalidateViewProperty(DBG, DBG);
        invalidateParentIfNeededAndWasQuickRejected();
    }

    @ExportedProperty(category = "drawing")
    public float getRotation() {
        return this.mRenderNode.getRotation();
    }

    public void setRotation(float rotation) {
        if (rotation != getRotation()) {
            invalidateViewProperty(true, DBG);
            this.mRenderNode.setRotation(rotation);
            invalidateViewProperty(DBG, true);
            invalidateParentIfNeededAndWasQuickRejected();
            notifySubtreeAccessibilityStateChangedIfNeeded();
        }
    }

    @ExportedProperty(category = "drawing")
    public float getRotationY() {
        return this.mRenderNode.getRotationY();
    }

    public void setRotationY(float rotationY) {
        if (rotationY != getRotationY()) {
            invalidateViewProperty(true, DBG);
            this.mRenderNode.setRotationY(rotationY);
            invalidateViewProperty(DBG, true);
            invalidateParentIfNeededAndWasQuickRejected();
            notifySubtreeAccessibilityStateChangedIfNeeded();
        }
    }

    @ExportedProperty(category = "drawing")
    public float getRotationX() {
        return this.mRenderNode.getRotationX();
    }

    public void setRotationX(float rotationX) {
        if (rotationX != getRotationX()) {
            invalidateViewProperty(true, DBG);
            this.mRenderNode.setRotationX(rotationX);
            invalidateViewProperty(DBG, true);
            invalidateParentIfNeededAndWasQuickRejected();
            notifySubtreeAccessibilityStateChangedIfNeeded();
        }
    }

    @ExportedProperty(category = "drawing")
    public float getScaleX() {
        return this.mRenderNode.getScaleX();
    }

    public void setScaleX(float scaleX) {
        if (scaleX != getScaleX()) {
            invalidateViewProperty(true, DBG);
            this.mRenderNode.setScaleX(scaleX);
            invalidateViewProperty(DBG, true);
            invalidateParentIfNeededAndWasQuickRejected();
            notifySubtreeAccessibilityStateChangedIfNeeded();
        }
    }

    @ExportedProperty(category = "drawing")
    public float getScaleY() {
        return this.mRenderNode.getScaleY();
    }

    public void setScaleY(float scaleY) {
        if (scaleY != getScaleY()) {
            invalidateViewProperty(true, DBG);
            this.mRenderNode.setScaleY(scaleY);
            invalidateViewProperty(DBG, true);
            invalidateParentIfNeededAndWasQuickRejected();
            notifySubtreeAccessibilityStateChangedIfNeeded();
        }
    }

    @ExportedProperty(category = "drawing")
    public float getPivotX() {
        return this.mRenderNode.getPivotX();
    }

    public void setPivotX(float pivotX) {
        if (!this.mRenderNode.isPivotExplicitlySet() || pivotX != getPivotX()) {
            invalidateViewProperty(true, DBG);
            this.mRenderNode.setPivotX(pivotX);
            invalidateViewProperty(DBG, true);
            invalidateParentIfNeededAndWasQuickRejected();
        }
    }

    @ExportedProperty(category = "drawing")
    public float getPivotY() {
        return this.mRenderNode.getPivotY();
    }

    public void setPivotY(float pivotY) {
        if (!this.mRenderNode.isPivotExplicitlySet() || pivotY != getPivotY()) {
            invalidateViewProperty(true, DBG);
            this.mRenderNode.setPivotY(pivotY);
            invalidateViewProperty(DBG, true);
            invalidateParentIfNeededAndWasQuickRejected();
        }
    }

    @ExportedProperty(category = "drawing")
    public float getAlpha() {
        return this.mTransformationInfo != null ? this.mTransformationInfo.mAlpha : WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
    }

    @ExportedProperty(category = "drawing")
    public boolean hasOverlappingRendering() {
        return true;
    }

    public void setAlpha(float alpha) {
        ensureTransformationInfo();
        if (this.mTransformationInfo.mAlpha != alpha) {
            this.mTransformationInfo.mAlpha = alpha;
            if (onSetAlpha((int) (255.0f * alpha))) {
                this.mPrivateFlags |= STATUS_BAR_DISABLE_NOTIFICATION_ALERTS;
                invalidateParentCaches();
                invalidate(true);
                return;
            }
            this.mPrivateFlags &= -262145;
            invalidateViewProperty(true, DBG);
            this.mRenderNode.setAlpha(getFinalAlpha());
            notifyViewAccessibilityStateChangedIfNeeded(VISIBLE);
        }
    }

    boolean setAlphaNoInvalidation(float alpha) {
        ensureTransformationInfo();
        if (this.mTransformationInfo.mAlpha != alpha) {
            this.mTransformationInfo.mAlpha = alpha;
            if (onSetAlpha((int) (255.0f * alpha))) {
                this.mPrivateFlags |= STATUS_BAR_DISABLE_NOTIFICATION_ALERTS;
                return true;
            }
            this.mPrivateFlags &= -262145;
            this.mRenderNode.setAlpha(getFinalAlpha());
        }
        return DBG;
    }

    public void setTransitionAlpha(float alpha) {
        ensureTransformationInfo();
        if (this.mTransformationInfo.mTransitionAlpha != alpha) {
            this.mTransformationInfo.mTransitionAlpha = alpha;
            this.mPrivateFlags &= -262145;
            invalidateViewProperty(true, DBG);
            this.mRenderNode.setAlpha(getFinalAlpha());
        }
    }

    private float getFinalAlpha() {
        if (this.mTransformationInfo != null) {
            return this.mTransformationInfo.mAlpha * this.mTransformationInfo.mTransitionAlpha;
        }
        return WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
    }

    @ExportedProperty(category = "drawing")
    public float getTransitionAlpha() {
        return this.mTransformationInfo != null ? this.mTransformationInfo.mTransitionAlpha : WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
    }

    @CapturedViewProperty
    public final int getTop() {
        return this.mTop;
    }

    public final void setTop(int top) {
        if (top != this.mTop) {
            boolean matrixIsIdentity = hasIdentityMatrix();
            if (!matrixIsIdentity) {
                invalidate(true);
            } else if (this.mAttachInfo != null) {
                int minTop;
                int yLoc;
                if (top < this.mTop) {
                    minTop = top;
                    yLoc = top - this.mTop;
                } else {
                    minTop = this.mTop;
                    yLoc = VISIBLE;
                }
                invalidate(VISIBLE, yLoc, this.mRight - this.mLeft, this.mBottom - minTop);
            }
            int width = this.mRight - this.mLeft;
            int oldHeight = this.mBottom - this.mTop;
            this.mTop = top;
            this.mRenderNode.setTop(this.mTop);
            sizeChange(width, this.mBottom - this.mTop, width, oldHeight);
            if (!matrixIsIdentity) {
                this.mPrivateFlags |= VIEW_STATE_ACTIVATED;
                invalidate(true);
            }
            this.mBackgroundSizeChanged = true;
            invalidateParentIfNeeded();
            if ((this.mPrivateFlags2 & STATUS_BAR_UNHIDE) == STATUS_BAR_UNHIDE) {
                invalidateParentIfNeeded();
            }
        }
    }

    @CapturedViewProperty
    public final int getBottom() {
        return this.mBottom;
    }

    public boolean isDirty() {
        return (this.mPrivateFlags & PFLAG_DIRTY_MASK) != 0 ? true : DBG;
    }

    public final void setBottom(int bottom) {
        if (bottom != this.mBottom) {
            boolean matrixIsIdentity = hasIdentityMatrix();
            if (!matrixIsIdentity) {
                invalidate(true);
            } else if (this.mAttachInfo != null) {
                int maxBottom;
                if (bottom < this.mBottom) {
                    maxBottom = this.mBottom;
                } else {
                    maxBottom = bottom;
                }
                invalidate(VISIBLE, VISIBLE, this.mRight - this.mLeft, maxBottom - this.mTop);
            }
            int width = this.mRight - this.mLeft;
            int oldHeight = this.mBottom - this.mTop;
            this.mBottom = bottom;
            this.mRenderNode.setBottom(this.mBottom);
            sizeChange(width, this.mBottom - this.mTop, width, oldHeight);
            if (!matrixIsIdentity) {
                this.mPrivateFlags |= VIEW_STATE_ACTIVATED;
                invalidate(true);
            }
            this.mBackgroundSizeChanged = true;
            invalidateParentIfNeeded();
            if ((this.mPrivateFlags2 & STATUS_BAR_UNHIDE) == STATUS_BAR_UNHIDE) {
                invalidateParentIfNeeded();
            }
        }
    }

    @CapturedViewProperty
    public final int getLeft() {
        return this.mLeft;
    }

    public final void setLeft(int left) {
        if (left != this.mLeft) {
            boolean matrixIsIdentity = hasIdentityMatrix();
            if (!matrixIsIdentity) {
                invalidate(true);
            } else if (this.mAttachInfo != null) {
                int minLeft;
                int xLoc;
                if (left < this.mLeft) {
                    minLeft = left;
                    xLoc = left - this.mLeft;
                } else {
                    minLeft = this.mLeft;
                    xLoc = VISIBLE;
                }
                invalidate(xLoc, VISIBLE, this.mRight - minLeft, this.mBottom - this.mTop);
            }
            int oldWidth = this.mRight - this.mLeft;
            int height = this.mBottom - this.mTop;
            this.mLeft = left;
            this.mRenderNode.setLeft(left);
            sizeChange(this.mRight - this.mLeft, height, oldWidth, height);
            if (!matrixIsIdentity) {
                this.mPrivateFlags |= VIEW_STATE_ACTIVATED;
                invalidate(true);
            }
            this.mBackgroundSizeChanged = true;
            invalidateParentIfNeeded();
            if ((this.mPrivateFlags2 & STATUS_BAR_UNHIDE) == STATUS_BAR_UNHIDE) {
                invalidateParentIfNeeded();
            }
        }
    }

    @CapturedViewProperty
    public final int getRight() {
        return this.mRight;
    }

    public final void setRight(int right) {
        if (right != this.mRight) {
            boolean matrixIsIdentity = hasIdentityMatrix();
            if (!matrixIsIdentity) {
                invalidate(true);
            } else if (this.mAttachInfo != null) {
                int maxRight;
                if (right < this.mRight) {
                    maxRight = this.mRight;
                } else {
                    maxRight = right;
                }
                invalidate(VISIBLE, VISIBLE, maxRight - this.mLeft, this.mBottom - this.mTop);
            }
            int oldWidth = this.mRight - this.mLeft;
            int height = this.mBottom - this.mTop;
            this.mRight = right;
            this.mRenderNode.setRight(this.mRight);
            sizeChange(this.mRight - this.mLeft, height, oldWidth, height);
            if (!matrixIsIdentity) {
                this.mPrivateFlags |= VIEW_STATE_ACTIVATED;
                invalidate(true);
            }
            this.mBackgroundSizeChanged = true;
            invalidateParentIfNeeded();
            if ((this.mPrivateFlags2 & STATUS_BAR_UNHIDE) == STATUS_BAR_UNHIDE) {
                invalidateParentIfNeeded();
            }
        }
    }

    @ExportedProperty(category = "drawing")
    public float getX() {
        return ((float) this.mLeft) + getTranslationX();
    }

    public void setX(float x) {
        setTranslationX(x - ((float) this.mLeft));
    }

    @ExportedProperty(category = "drawing")
    public float getY() {
        return ((float) this.mTop) + getTranslationY();
    }

    public void setY(float y) {
        setTranslationY(y - ((float) this.mTop));
    }

    @ExportedProperty(category = "drawing")
    public float getZ() {
        return getElevation() + getTranslationZ();
    }

    public void setZ(float z) {
        setTranslationZ(z - getElevation());
    }

    @ExportedProperty(category = "drawing")
    public float getElevation() {
        return this.mRenderNode.getElevation();
    }

    public void setElevation(float elevation) {
        if (elevation != getElevation()) {
            invalidateViewProperty(true, DBG);
            this.mRenderNode.setElevation(elevation);
            invalidateViewProperty(DBG, true);
            invalidateParentIfNeededAndWasQuickRejected();
        }
    }

    @ExportedProperty(category = "drawing")
    public float getTranslationX() {
        return this.mRenderNode.getTranslationX();
    }

    public void setTranslationX(float translationX) {
        if (translationX != getTranslationX()) {
            invalidateViewProperty(true, DBG);
            this.mRenderNode.setTranslationX(translationX);
            invalidateViewProperty(DBG, true);
            invalidateParentIfNeededAndWasQuickRejected();
            notifySubtreeAccessibilityStateChangedIfNeeded();
        }
    }

    @ExportedProperty(category = "drawing")
    public float getTranslationY() {
        return this.mRenderNode.getTranslationY();
    }

    public void setTranslationY(float translationY) {
        if (translationY != getTranslationY()) {
            invalidateViewProperty(true, DBG);
            this.mRenderNode.setTranslationY(translationY);
            invalidateViewProperty(DBG, true);
            invalidateParentIfNeededAndWasQuickRejected();
        }
    }

    @ExportedProperty(category = "drawing")
    public float getTranslationZ() {
        return this.mRenderNode.getTranslationZ();
    }

    public void setTranslationZ(float translationZ) {
        if (translationZ != getTranslationZ()) {
            invalidateViewProperty(true, DBG);
            this.mRenderNode.setTranslationZ(translationZ);
            invalidateViewProperty(DBG, true);
            invalidateParentIfNeededAndWasQuickRejected();
        }
    }

    public void setAnimationMatrix(Matrix matrix) {
        invalidateViewProperty(true, DBG);
        this.mRenderNode.setAnimationMatrix(matrix);
        invalidateViewProperty(DBG, true);
        invalidateParentIfNeededAndWasQuickRejected();
    }

    public StateListAnimator getStateListAnimator() {
        return this.mStateListAnimator;
    }

    public void setStateListAnimator(StateListAnimator stateListAnimator) {
        if (this.mStateListAnimator != stateListAnimator) {
            if (this.mStateListAnimator != null) {
                this.mStateListAnimator.setTarget(null);
            }
            this.mStateListAnimator = stateListAnimator;
            if (stateListAnimator != null) {
                stateListAnimator.setTarget(this);
                if (isAttachedToWindow()) {
                    stateListAnimator.setState(getDrawableState());
                }
            }
        }
    }

    public final boolean getClipToOutline() {
        return this.mRenderNode.getClipToOutline();
    }

    public void setClipToOutline(boolean clipToOutline) {
        damageInParent();
        if (getClipToOutline() != clipToOutline) {
            this.mRenderNode.setClipToOutline(clipToOutline);
        }
    }

    private void setOutlineProviderFromAttribute(int providerInt) {
        switch (providerInt) {
            case VISIBLE /*0*/:
                setOutlineProvider(ViewOutlineProvider.BACKGROUND);
            case VIEW_STATE_WINDOW_FOCUSED /*1*/:
                setOutlineProvider(null);
            case VIEW_STATE_SELECTED /*2*/:
                setOutlineProvider(ViewOutlineProvider.BOUNDS);
            case TEXT_DIRECTION_LTR /*3*/:
                setOutlineProvider(ViewOutlineProvider.PADDED_BOUNDS);
            default:
        }
    }

    public void setOutlineProvider(ViewOutlineProvider provider) {
        this.mOutlineProvider = provider;
        invalidateOutline();
    }

    public ViewOutlineProvider getOutlineProvider() {
        return this.mOutlineProvider;
    }

    public void invalidateOutline() {
        rebuildOutline();
        notifySubtreeAccessibilityStateChangedIfNeeded();
        invalidateViewProperty(DBG, DBG);
    }

    private void rebuildOutline() {
        if (this.mAttachInfo != null) {
            if (this.mOutlineProvider == null) {
                this.mRenderNode.setOutline(null);
                return;
            }
            Outline outline = this.mAttachInfo.mTmpOutline;
            outline.setEmpty();
            outline.setAlpha(WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL);
            this.mOutlineProvider.getOutline(this, outline);
            this.mRenderNode.setOutline(outline);
        }
    }

    @ExportedProperty(category = "drawing")
    public boolean hasShadow() {
        return this.mRenderNode.hasShadow();
    }

    public void setRevealClip(boolean shouldClip, float x, float y, float radius) {
        this.mRenderNode.setRevealClip(shouldClip, x, y, radius);
        invalidateViewProperty(DBG, DBG);
    }

    public void getHitRect(Rect outRect) {
        if (hasIdentityMatrix() || this.mAttachInfo == null) {
            outRect.set(this.mLeft, this.mTop, this.mRight, this.mBottom);
            return;
        }
        RectF tmpRect = this.mAttachInfo.mTmpTransformRect;
        tmpRect.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
        getMatrix().mapRect(tmpRect);
        outRect.set(((int) tmpRect.left) + this.mLeft, ((int) tmpRect.top) + this.mTop, ((int) tmpRect.right) + this.mLeft, ((int) tmpRect.bottom) + this.mTop);
    }

    final boolean pointInView(float localX, float localY) {
        return (localX < 0.0f || localX >= ((float) (this.mRight - this.mLeft)) || localY < 0.0f || localY >= ((float) (this.mBottom - this.mTop))) ? DBG : true;
    }

    public boolean pointInView(float localX, float localY, float slop) {
        return (localX < (-slop) || localY < (-slop) || localX >= ((float) (this.mRight - this.mLeft)) + slop || localY >= ((float) (this.mBottom - this.mTop)) + slop) ? DBG : true;
    }

    public void getFocusedRect(Rect r) {
        getDrawingRect(r);
    }

    public boolean getGlobalVisibleRect(Rect r, Point globalOffset) {
        int width = this.mRight - this.mLeft;
        int height = this.mBottom - this.mTop;
        if (width <= 0 || height <= 0) {
            return DBG;
        }
        r.set(VISIBLE, VISIBLE, width, height);
        if (globalOffset != null) {
            globalOffset.set(-this.mScrollX, -this.mScrollY);
        }
        if (this.mParent == null || this.mParent.getChildVisibleRect(this, r, globalOffset)) {
            return true;
        }
        return DBG;
    }

    public final boolean getGlobalVisibleRect(Rect r) {
        return getGlobalVisibleRect(r, null);
    }

    public final boolean getLocalVisibleRect(Rect r) {
        Point offset = this.mAttachInfo != null ? this.mAttachInfo.mPoint : new Point();
        if (!getGlobalVisibleRect(r, offset)) {
            return DBG;
        }
        r.offset(-offset.x, -offset.y);
        return true;
    }

    public void offsetTopAndBottom(int offset) {
        if (offset != 0) {
            boolean matrixIsIdentity = hasIdentityMatrix();
            if (!matrixIsIdentity) {
                invalidateViewProperty(DBG, DBG);
            } else if (isHardwareAccelerated()) {
                invalidateViewProperty(DBG, DBG);
            } else {
                ViewParent p = this.mParent;
                if (!(p == null || this.mAttachInfo == null)) {
                    int minTop;
                    int maxBottom;
                    int yLoc;
                    Rect r = this.mAttachInfo.mTmpInvalRect;
                    if (offset < 0) {
                        minTop = this.mTop + offset;
                        maxBottom = this.mBottom;
                        yLoc = offset;
                    } else {
                        minTop = this.mTop;
                        maxBottom = this.mBottom + offset;
                        yLoc = VISIBLE;
                    }
                    r.set(VISIBLE, yLoc, this.mRight - this.mLeft, maxBottom - minTop);
                    p.invalidateChild(this, r);
                }
            }
            this.mTop += offset;
            this.mBottom += offset;
            this.mRenderNode.offsetTopAndBottom(offset);
            if (isHardwareAccelerated()) {
                invalidateViewProperty(DBG, DBG);
            } else {
                if (!matrixIsIdentity) {
                    invalidateViewProperty(DBG, true);
                }
                invalidateParentIfNeeded();
            }
            notifySubtreeAccessibilityStateChangedIfNeeded();
        }
    }

    public void offsetLeftAndRight(int offset) {
        if (offset != 0) {
            boolean matrixIsIdentity = hasIdentityMatrix();
            if (!matrixIsIdentity) {
                invalidateViewProperty(DBG, DBG);
            } else if (isHardwareAccelerated()) {
                invalidateViewProperty(DBG, DBG);
            } else {
                ViewParent p = this.mParent;
                if (!(p == null || this.mAttachInfo == null)) {
                    int minLeft;
                    int maxRight;
                    Rect r = this.mAttachInfo.mTmpInvalRect;
                    if (offset < 0) {
                        minLeft = this.mLeft + offset;
                        maxRight = this.mRight;
                    } else {
                        minLeft = this.mLeft;
                        maxRight = this.mRight + offset;
                    }
                    r.set(VISIBLE, VISIBLE, maxRight - minLeft, this.mBottom - this.mTop);
                    p.invalidateChild(this, r);
                }
            }
            this.mLeft += offset;
            this.mRight += offset;
            this.mRenderNode.offsetLeftAndRight(offset);
            if (isHardwareAccelerated()) {
                invalidateViewProperty(DBG, DBG);
            } else {
                if (!matrixIsIdentity) {
                    invalidateViewProperty(DBG, true);
                }
                invalidateParentIfNeeded();
            }
            notifySubtreeAccessibilityStateChangedIfNeeded();
        }
    }

    @ExportedProperty(deepExport = true, prefix = "layout_")
    public LayoutParams getLayoutParams() {
        return this.mLayoutParams;
    }

    public void setLayoutParams(LayoutParams params) {
        if (params == null) {
            throw new NullPointerException("Layout parameters cannot be null");
        }
        this.mLayoutParams = params;
        resolveLayoutParams();
        if (this.mParent instanceof ViewGroup) {
            ((ViewGroup) this.mParent).onSetLayoutParams(this, params);
        }
        requestLayout();
    }

    public void resolveLayoutParams() {
        if (this.mLayoutParams != null) {
            this.mLayoutParams.resolveLayoutDirection(getLayoutDirection());
        }
    }

    public void scrollTo(int x, int y) {
        if (this.mScrollX != x || this.mScrollY != y) {
            int oldX = this.mScrollX;
            int oldY = this.mScrollY;
            this.mScrollX = x;
            this.mScrollY = y;
            invalidateParentCaches();
            onScrollChanged(this.mScrollX, this.mScrollY, oldX, oldY);
            if (!awakenScrollBars()) {
                postInvalidateOnAnimation();
            }
        }
    }

    public void scrollBy(int x, int y) {
        scrollTo(this.mScrollX + x, this.mScrollY + y);
    }

    protected boolean awakenScrollBars() {
        return (this.mScrollCache == null || !awakenScrollBars(this.mScrollCache.scrollBarDefaultDelayBeforeFade, true)) ? DBG : true;
    }

    private boolean initialAwakenScrollBars() {
        return (this.mScrollCache == null || !awakenScrollBars(this.mScrollCache.scrollBarDefaultDelayBeforeFade * VIEW_STATE_FOCUSED, true)) ? DBG : true;
    }

    protected boolean awakenScrollBars(int startDelay) {
        return awakenScrollBars(startDelay, true);
    }

    protected boolean awakenScrollBars(int startDelay, boolean invalidate) {
        ScrollabilityCache scrollCache = this.mScrollCache;
        if (scrollCache == null || !scrollCache.fadeScrollBars) {
            return DBG;
        }
        if (scrollCache.scrollBar == null) {
            scrollCache.scrollBar = new ScrollBarDrawable();
        }
        if (!isHorizontalScrollBarEnabled() && !isVerticalScrollBarEnabled()) {
            return DBG;
        }
        if (invalidate) {
            postInvalidateOnAnimation();
        }
        if (scrollCache.state == 0) {
            startDelay = Math.max(750, startDelay);
        }
        long fadeStartTime = AnimationUtils.currentAnimationTimeMillis() + ((long) startDelay);
        scrollCache.fadeStartTime = fadeStartTime;
        scrollCache.state = VIEW_STATE_WINDOW_FOCUSED;
        if (this.mAttachInfo != null) {
            this.mAttachInfo.mHandler.removeCallbacks(scrollCache);
            this.mAttachInfo.mHandler.postAtTime(scrollCache, fadeStartTime);
        }
        return true;
    }

    private boolean skipInvalidate() {
        return ((this.mViewFlags & VISIBILITY_MASK) == 0 || this.mCurrentAnimation != null || ((this.mParent instanceof ViewGroup) && ((ViewGroup) this.mParent).isViewTransitioning(this))) ? DBG : true;
    }

    public void invalidate(Rect dirty) {
        int scrollX = this.mScrollX;
        int scrollY = this.mScrollY;
        invalidateInternal(dirty.left - scrollX, dirty.top - scrollY, dirty.right - scrollX, dirty.bottom - scrollY, true, DBG);
    }

    public void invalidate(int l, int t, int r, int b) {
        int scrollX = this.mScrollX;
        int scrollY = this.mScrollY;
        invalidateInternal(l - scrollX, t - scrollY, r - scrollX, b - scrollY, true, DBG);
    }

    public void invalidate() {
        invalidate(true);
    }

    void invalidate(boolean invalidateCache) {
        invalidateInternal(VISIBLE, VISIBLE, this.mRight - this.mLeft, this.mBottom - this.mTop, invalidateCache, true);
    }

    void invalidateInternal(int l, int t, int r, int b, boolean invalidateCache, boolean fullInvalidate) {
        if (this.mGhostView != null) {
            this.mGhostView.invalidate(true);
        } else if (!skipInvalidate()) {
            if ((this.mPrivateFlags & PFLAG2_LAYOUT_DIRECTION_RESOLVED_MASK) == PFLAG2_LAYOUT_DIRECTION_RESOLVED_MASK || ((invalidateCache && (this.mPrivateFlags & SYSTEM_UI_TRANSPARENT) == SYSTEM_UI_TRANSPARENT) || (this.mPrivateFlags & UNDEFINED_PADDING) != UNDEFINED_PADDING || (fullInvalidate && isOpaque() != this.mLastIsOpaque))) {
                if (fullInvalidate) {
                    this.mLastIsOpaque = isOpaque();
                    this.mPrivateFlags &= -33;
                }
                this.mPrivateFlags |= STATUS_BAR_DISABLE_HOME;
                if (invalidateCache) {
                    this.mPrivateFlags |= UNDEFINED_PADDING;
                    this.mPrivateFlags &= -32769;
                }
                AttachInfo ai = this.mAttachInfo;
                ViewParent p = this.mParent;
                if (p != null && ai != null && l < r && t < b) {
                    Rect damage = ai.mTmpInvalRect;
                    damage.set(l, t, r, b);
                    p.invalidateChild(this, damage);
                }
                if (this.mBackground != null && this.mBackground.isProjected()) {
                    View receiver = getProjectionReceiver();
                    if (receiver != null) {
                        receiver.damageInParent();
                    }
                }
                if (isHardwareAccelerated() && getZ() != 0.0f) {
                    damageShadowReceiver();
                }
            }
        }
    }

    private View getProjectionReceiver() {
        ViewParent p = getParent();
        while (p != null && (p instanceof View)) {
            View v = (View) p;
            if (v.isProjectionReceiver()) {
                return v;
            }
            p = p.getParent();
        }
        return null;
    }

    private boolean isProjectionReceiver() {
        return this.mBackground != null ? true : DBG;
    }

    private void damageShadowReceiver() {
        if (this.mAttachInfo != null) {
            ViewParent p = getParent();
            if (p != null && (p instanceof ViewGroup)) {
                ((ViewGroup) p).damageInParent();
            }
        }
    }

    void invalidateViewProperty(boolean invalidateParent, boolean forceRedraw) {
        if (isHardwareAccelerated() && this.mRenderNode.isValid() && (this.mPrivateFlags & VIEW_STATE_ACCELERATED) == 0) {
            damageInParent();
        } else {
            if (invalidateParent) {
                invalidateParentCaches();
            }
            if (forceRedraw) {
                this.mPrivateFlags |= VIEW_STATE_ACTIVATED;
            }
            invalidate((boolean) DBG);
        }
        if (isHardwareAccelerated() && invalidateParent && getZ() != 0.0f) {
            damageShadowReceiver();
        }
    }

    protected void damageInParent() {
        AttachInfo ai = this.mAttachInfo;
        if (this.mParent != null && ai != null) {
            Rect r = ai.mTmpInvalRect;
            r.set(VISIBLE, VISIBLE, this.mRight - this.mLeft, this.mBottom - this.mTop);
            if (this.mParent instanceof ViewGroup) {
                ((ViewGroup) this.mParent).damageChild(this, r);
            } else {
                this.mParent.invalidateChild(this, r);
            }
        }
    }

    void transformRect(Rect rect) {
        if (!getMatrix().isIdentity()) {
            RectF boundingRect = this.mAttachInfo.mTmpTransformRect;
            boundingRect.set(rect);
            getMatrix().mapRect(boundingRect);
            rect.set((int) Math.floor((double) boundingRect.left), (int) Math.floor((double) boundingRect.top), (int) Math.ceil((double) boundingRect.right), (int) Math.ceil((double) boundingRect.bottom));
        }
    }

    protected void invalidateParentCaches() {
        if (this.mParent instanceof View) {
            View view = (View) this.mParent;
            view.mPrivateFlags |= UNDEFINED_PADDING;
        }
    }

    protected void invalidateParentIfNeeded() {
        if (isHardwareAccelerated() && (this.mParent instanceof View)) {
            ((View) this.mParent).invalidate(true);
        }
    }

    protected void invalidateParentIfNeededAndWasQuickRejected() {
        if ((this.mPrivateFlags2 & STATUS_BAR_UNHIDE) != 0) {
            invalidateParentIfNeeded();
        }
    }

    @ExportedProperty(category = "drawing")
    public boolean isOpaque() {
        return ((this.mPrivateFlags & PFLAG_OPAQUE_MASK) != PFLAG_OPAQUE_MASK || getFinalAlpha() < WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL) ? DBG : true;
    }

    protected void computeOpaqueFlags() {
        if (this.mBackground == null || this.mBackground.getOpacity() != NO_ID) {
            this.mPrivateFlags &= -8388609;
        } else {
            this.mPrivateFlags |= STATUS_BAR_DISABLE_CLOCK;
        }
        int flags = this.mViewFlags;
        if (((flags & VIEW_STATE_DRAG_HOVERED) == 0 && (flags & VIEW_STATE_DRAG_CAN_ACCEPT) == 0) || (flags & SCROLLBARS_STYLE_MASK) == 0 || (flags & SCROLLBARS_STYLE_MASK) == STATUS_BAR_DISABLE_SEARCH) {
            this.mPrivateFlags |= STATUS_BAR_DISABLE_RECENT;
        } else {
            this.mPrivateFlags &= -16777217;
        }
    }

    protected boolean hasOpaqueScrollbars() {
        return (this.mPrivateFlags & STATUS_BAR_DISABLE_RECENT) == STATUS_BAR_DISABLE_RECENT ? true : DBG;
    }

    public Handler getHandler() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mHandler;
        }
        return null;
    }

    public ViewRootImpl getViewRootImpl() {
        if (this.mAttachInfo != null) {
            return this.mAttachInfo.mViewRootImpl;
        }
        return null;
    }

    public HardwareRenderer getHardwareRenderer() {
        return this.mAttachInfo != null ? this.mAttachInfo.mHardwareRenderer : null;
    }

    public boolean post(Runnable action) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mHandler.post(action);
        }
        ViewRootImpl.getRunQueue().post(action);
        return true;
    }

    public boolean postDelayed(Runnable action, long delayMillis) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            return attachInfo.mHandler.postDelayed(action, delayMillis);
        }
        ViewRootImpl.getRunQueue().postDelayed(action, delayMillis);
        return true;
    }

    public void postOnAnimation(Runnable action) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mViewRootImpl.mChoreographer.postCallback(VIEW_STATE_WINDOW_FOCUSED, action, null);
        } else {
            ViewRootImpl.getRunQueue().post(action);
        }
    }

    public void postOnAnimationDelayed(Runnable action, long delayMillis) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mViewRootImpl.mChoreographer.postCallbackDelayed(VIEW_STATE_WINDOW_FOCUSED, action, null, delayMillis);
        } else {
            ViewRootImpl.getRunQueue().postDelayed(action, delayMillis);
        }
    }

    public boolean removeCallbacks(Runnable action) {
        if (action != null) {
            AttachInfo attachInfo = this.mAttachInfo;
            if (attachInfo != null) {
                attachInfo.mHandler.removeCallbacks(action);
                attachInfo.mViewRootImpl.mChoreographer.removeCallbacks(VIEW_STATE_WINDOW_FOCUSED, action, null);
            }
            ViewRootImpl.getRunQueue().removeCallbacks(action);
        }
        return true;
    }

    public void postInvalidate() {
        postInvalidateDelayed(0);
    }

    public void postInvalidate(int left, int top, int right, int bottom) {
        postInvalidateDelayed(0, left, top, right, bottom);
    }

    public void postInvalidateDelayed(long delayMilliseconds) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mViewRootImpl.dispatchInvalidateDelayed(this, delayMilliseconds);
        }
    }

    public void postInvalidateDelayed(long delayMilliseconds, int left, int top, int right, int bottom) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            InvalidateInfo info = InvalidateInfo.obtain();
            info.target = this;
            info.left = left;
            info.top = top;
            info.right = right;
            info.bottom = bottom;
            attachInfo.mViewRootImpl.dispatchInvalidateRectDelayed(info, delayMilliseconds);
        }
    }

    public void postInvalidateOnAnimation() {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            attachInfo.mViewRootImpl.dispatchInvalidateOnAnimation(this);
        }
    }

    public void postInvalidateOnAnimation(int left, int top, int right, int bottom) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (attachInfo != null) {
            InvalidateInfo info = InvalidateInfo.obtain();
            info.target = this;
            info.left = left;
            info.top = top;
            info.right = right;
            info.bottom = bottom;
            attachInfo.mViewRootImpl.dispatchInvalidateRectOnAnimation(info);
        }
    }

    private void postSendViewScrolledAccessibilityEventCallback() {
        if (this.mSendViewScrolledAccessibilityEvent == null) {
            this.mSendViewScrolledAccessibilityEvent = new SendViewScrolledAccessibilityEvent();
        }
        if (!this.mSendViewScrolledAccessibilityEvent.mIsPending) {
            this.mSendViewScrolledAccessibilityEvent.mIsPending = true;
            postDelayed(this.mSendViewScrolledAccessibilityEvent, ViewConfiguration.getSendRecurringAccessibilityEventsInterval());
        }
    }

    public void computeScroll() {
    }

    public boolean isHorizontalFadingEdgeEnabled() {
        return (this.mViewFlags & SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == SYSTEM_UI_FLAG_IMMERSIVE_STICKY ? true : DBG;
    }

    public void setHorizontalFadingEdgeEnabled(boolean horizontalFadingEdgeEnabled) {
        if (isHorizontalFadingEdgeEnabled() != horizontalFadingEdgeEnabled) {
            if (horizontalFadingEdgeEnabled) {
                initScrollCache();
            }
            this.mViewFlags ^= SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
    }

    public boolean isVerticalFadingEdgeEnabled() {
        return (this.mViewFlags & PFLAG_LAYOUT_REQUIRED) == PFLAG_LAYOUT_REQUIRED ? true : DBG;
    }

    public void setVerticalFadingEdgeEnabled(boolean verticalFadingEdgeEnabled) {
        if (isVerticalFadingEdgeEnabled() != verticalFadingEdgeEnabled) {
            if (verticalFadingEdgeEnabled) {
                initScrollCache();
            }
            this.mViewFlags ^= PFLAG_LAYOUT_REQUIRED;
        }
    }

    protected float getTopFadingEdgeStrength() {
        return computeVerticalScrollOffset() > 0 ? WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL : 0.0f;
    }

    protected float getBottomFadingEdgeStrength() {
        return computeVerticalScrollOffset() + computeVerticalScrollExtent() < computeVerticalScrollRange() ? WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL : 0.0f;
    }

    protected float getLeftFadingEdgeStrength() {
        return computeHorizontalScrollOffset() > 0 ? WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL : 0.0f;
    }

    protected float getRightFadingEdgeStrength() {
        return computeHorizontalScrollOffset() + computeHorizontalScrollExtent() < computeHorizontalScrollRange() ? WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL : 0.0f;
    }

    public boolean isHorizontalScrollBarEnabled() {
        return (this.mViewFlags & VIEW_STATE_DRAG_CAN_ACCEPT) == VIEW_STATE_DRAG_CAN_ACCEPT ? true : DBG;
    }

    public void setHorizontalScrollBarEnabled(boolean horizontalScrollBarEnabled) {
        if (isHorizontalScrollBarEnabled() != horizontalScrollBarEnabled) {
            this.mViewFlags ^= VIEW_STATE_DRAG_CAN_ACCEPT;
            computeOpaqueFlags();
            resolvePadding();
        }
    }

    public boolean isVerticalScrollBarEnabled() {
        return (this.mViewFlags & VIEW_STATE_DRAG_HOVERED) == VIEW_STATE_DRAG_HOVERED ? true : DBG;
    }

    public void setVerticalScrollBarEnabled(boolean verticalScrollBarEnabled) {
        if (isVerticalScrollBarEnabled() != verticalScrollBarEnabled) {
            this.mViewFlags ^= VIEW_STATE_DRAG_HOVERED;
            computeOpaqueFlags();
            resolvePadding();
        }
    }

    protected void recomputePadding() {
        internalSetPadding(this.mUserPaddingLeft, this.mPaddingTop, this.mUserPaddingRight, this.mUserPaddingBottom);
    }

    public void setScrollbarFadingEnabled(boolean fadeScrollbars) {
        initScrollCache();
        ScrollabilityCache scrollabilityCache = this.mScrollCache;
        scrollabilityCache.fadeScrollBars = fadeScrollbars;
        if (fadeScrollbars) {
            scrollabilityCache.state = VISIBLE;
        } else {
            scrollabilityCache.state = VIEW_STATE_WINDOW_FOCUSED;
        }
    }

    public boolean isScrollbarFadingEnabled() {
        return (this.mScrollCache == null || !this.mScrollCache.fadeScrollBars) ? DBG : true;
    }

    public int getScrollBarDefaultDelayBeforeFade() {
        return this.mScrollCache == null ? ViewConfiguration.getScrollDefaultDelay() : this.mScrollCache.scrollBarDefaultDelayBeforeFade;
    }

    public void setScrollBarDefaultDelayBeforeFade(int scrollBarDefaultDelayBeforeFade) {
        getScrollCache().scrollBarDefaultDelayBeforeFade = scrollBarDefaultDelayBeforeFade;
    }

    public int getScrollBarFadeDuration() {
        return this.mScrollCache == null ? ViewConfiguration.getScrollBarFadeDuration() : this.mScrollCache.scrollBarFadeDuration;
    }

    public void setScrollBarFadeDuration(int scrollBarFadeDuration) {
        getScrollCache().scrollBarFadeDuration = scrollBarFadeDuration;
    }

    public int getScrollBarSize() {
        return this.mScrollCache == null ? ViewConfiguration.get(this.mContext).getScaledScrollBarSize() : this.mScrollCache.scrollBarSize;
    }

    public void setScrollBarSize(int scrollBarSize) {
        getScrollCache().scrollBarSize = scrollBarSize;
    }

    public void setScrollBarStyle(int style) {
        if (style != (this.mViewFlags & SCROLLBARS_STYLE_MASK)) {
            this.mViewFlags = (this.mViewFlags & -50331649) | (style & SCROLLBARS_STYLE_MASK);
            computeOpaqueFlags();
            resolvePadding();
        }
    }

    @ExportedProperty(mapping = {@IntToString(from = 0, to = "INSIDE_OVERLAY"), @IntToString(from = 16777216, to = "INSIDE_INSET"), @IntToString(from = 33554432, to = "OUTSIDE_OVERLAY"), @IntToString(from = 50331648, to = "OUTSIDE_INSET")})
    public int getScrollBarStyle() {
        return this.mViewFlags & SCROLLBARS_STYLE_MASK;
    }

    protected int computeHorizontalScrollRange() {
        return getWidth();
    }

    protected int computeHorizontalScrollOffset() {
        return this.mScrollX;
    }

    protected int computeHorizontalScrollExtent() {
        return getWidth();
    }

    protected int computeVerticalScrollRange() {
        return getHeight();
    }

    protected int computeVerticalScrollOffset() {
        return this.mScrollY;
    }

    protected int computeVerticalScrollExtent() {
        return getHeight();
    }

    public boolean canScrollHorizontally(int direction) {
        int offset = computeHorizontalScrollOffset();
        int range = computeHorizontalScrollRange() - computeHorizontalScrollExtent();
        if (range == 0) {
            return DBG;
        }
        if (direction < 0) {
            if (offset <= 0) {
                return DBG;
            }
            return true;
        } else if (offset >= range + NO_ID) {
            return DBG;
        } else {
            return true;
        }
    }

    public boolean canScrollVertically(int direction) {
        int offset = computeVerticalScrollOffset();
        int range = computeVerticalScrollRange() - computeVerticalScrollExtent();
        if (range == 0) {
            return DBG;
        }
        if (direction < 0) {
            if (offset <= 0) {
                return DBG;
            }
            return true;
        } else if (offset >= range + NO_ID) {
            return DBG;
        } else {
            return true;
        }
    }

    protected final void onDrawScrollBars(Canvas canvas) {
        ScrollabilityCache cache = this.mScrollCache;
        if (cache != null) {
            int state = cache.state;
            if (state != 0) {
                boolean invalidate = DBG;
                if (state == VIEW_STATE_SELECTED) {
                    if (cache.interpolatorValues == null) {
                        cache.interpolatorValues = new float[VIEW_STATE_WINDOW_FOCUSED];
                    }
                    float[] values = cache.interpolatorValues;
                    if (cache.scrollBarInterpolator.timeToValues(values) == Result.FREEZE_END) {
                        cache.state = VISIBLE;
                    } else {
                        cache.scrollBar.mutate().setAlpha(Math.round(values[VISIBLE]));
                    }
                    invalidate = true;
                } else {
                    cache.scrollBar.mutate().setAlpha(EditorInfo.IME_MASK_ACTION);
                }
                int viewFlags = this.mViewFlags;
                boolean drawHorizontalScrollBar = (viewFlags & VIEW_STATE_DRAG_CAN_ACCEPT) == VIEW_STATE_DRAG_CAN_ACCEPT ? true : DBG;
                boolean drawVerticalScrollBar = ((viewFlags & VIEW_STATE_DRAG_HOVERED) != VIEW_STATE_DRAG_HOVERED || isVerticalScrollBarHidden()) ? DBG : true;
                if (drawVerticalScrollBar || drawHorizontalScrollBar) {
                    int size;
                    int top;
                    int left;
                    int right;
                    int bottom;
                    int width = this.mRight - this.mLeft;
                    int height = this.mBottom - this.mTop;
                    ScrollBarDrawable scrollBar = cache.scrollBar;
                    int scrollX = this.mScrollX;
                    int scrollY = this.mScrollY;
                    int inside = (STATUS_BAR_DISABLE_SEARCH & viewFlags) == 0 ? NO_ID : VISIBLE;
                    if (drawHorizontalScrollBar) {
                        size = scrollBar.getSize(DBG);
                        if (size <= 0) {
                            size = cache.scrollBarSize;
                        }
                        scrollBar.setParameters(computeHorizontalScrollRange(), computeHorizontalScrollOffset(), computeHorizontalScrollExtent(), DBG);
                        top = ((scrollY + height) - size) - (this.mUserPaddingBottom & inside);
                        left = scrollX + (this.mPaddingLeft & inside);
                        right = ((scrollX + width) - (this.mUserPaddingRight & inside)) - (drawVerticalScrollBar ? getVerticalScrollbarWidth() : VISIBLE);
                        bottom = top + size;
                        onDrawHorizontalScrollBar(canvas, scrollBar, left, top, right, bottom);
                        if (invalidate) {
                            invalidate(left, top, right, bottom);
                        }
                    }
                    if (drawVerticalScrollBar) {
                        size = scrollBar.getSize(true);
                        if (size <= 0) {
                            size = cache.scrollBarSize;
                        }
                        scrollBar.setParameters(computeVerticalScrollRange(), computeVerticalScrollOffset(), computeVerticalScrollExtent(), true);
                        int verticalScrollbarPosition = this.mVerticalScrollbarPosition;
                        if (verticalScrollbarPosition == 0) {
                            verticalScrollbarPosition = isLayoutRtl() ? VIEW_STATE_WINDOW_FOCUSED : VIEW_STATE_SELECTED;
                        }
                        switch (verticalScrollbarPosition) {
                            case VIEW_STATE_WINDOW_FOCUSED /*1*/:
                                left = scrollX + (this.mUserPaddingLeft & inside);
                                break;
                            default:
                                left = ((scrollX + width) - size) - (this.mUserPaddingRight & inside);
                                break;
                        }
                        top = scrollY + (this.mPaddingTop & inside);
                        right = left + size;
                        bottom = (scrollY + height) - (this.mUserPaddingBottom & inside);
                        onDrawVerticalScrollBar(canvas, scrollBar, left, top, right, bottom);
                        if (invalidate) {
                            invalidate(left, top, right, bottom);
                        }
                    }
                }
            }
        }
    }

    protected boolean isVerticalScrollBarHidden() {
        return DBG;
    }

    protected void onDrawHorizontalScrollBar(Canvas canvas, Drawable scrollBar, int l, int t, int r, int b) {
        scrollBar.setBounds(l, t, r, b);
        scrollBar.draw(canvas);
    }

    protected void onDrawVerticalScrollBar(Canvas canvas, Drawable scrollBar, int l, int t, int r, int b) {
        scrollBar.setBounds(l, t, r, b);
        scrollBar.draw(canvas);
    }

    protected void onDraw(Canvas canvas) {
    }

    void assignParent(ViewParent parent) {
        if (this.mParent == null) {
            this.mParent = parent;
        } else if (parent == null) {
            this.mParent = null;
        } else {
            throw new RuntimeException("view " + this + " being added, but" + " it already has a parent");
        }
    }

    protected void onAttachedToWindow() {
        if ((this.mPrivateFlags & VIEW_STATE_DRAG_HOVERED) != 0) {
            this.mParent.requestTransparentRegion(this);
        }
        if ((this.mPrivateFlags & SOUND_EFFECTS_ENABLED) != 0) {
            initialAwakenScrollBars();
            this.mPrivateFlags &= -134217729;
        }
        this.mPrivateFlags3 &= -5;
        jumpDrawablesToCurrentState();
        resetSubtreeAccessibilityStateChanged();
        rebuildOutline();
        if (isFocused()) {
            InputMethodManager.peekInstance().focusIn(this);
        }
    }

    public boolean resolveRtlPropertiesIfNeeded() {
        if (!needRtlPropertiesResolution()) {
            return DBG;
        }
        if (!isLayoutDirectionResolved()) {
            resolveLayoutDirection();
            resolveLayoutParams();
        }
        if (!isTextDirectionResolved()) {
            resolveTextDirection();
        }
        if (!isTextAlignmentResolved()) {
            resolveTextAlignment();
        }
        if (!areDrawablesResolved()) {
            resolveDrawables();
        }
        if (!isPaddingResolved()) {
            resolvePadding();
        }
        onRtlPropertiesChanged(getLayoutDirection());
        return true;
    }

    public void resetRtlProperties() {
        resetResolvedLayoutDirection();
        resetResolvedTextDirection();
        resetResolvedTextAlignment();
        resetResolvedPadding();
        resetResolvedDrawables();
    }

    void dispatchScreenStateChanged(int screenState) {
        onScreenStateChanged(screenState);
    }

    public void onScreenStateChanged(int screenState) {
    }

    private boolean hasRtlSupport() {
        return this.mContext.getApplicationInfo().hasRtlSupport();
    }

    private boolean isRtlCompatibilityMode() {
        return (getContext().getApplicationInfo().targetSdkVersion < PFLAG2_TEXT_ALIGNMENT_RESOLVED_MASK_SHIFT || !hasRtlSupport()) ? true : DBG;
    }

    private boolean needRtlPropertiesResolution() {
        return (this.mPrivateFlags2 & ALL_RTL_PROPERTIES_RESOLVED) != ALL_RTL_PROPERTIES_RESOLVED ? true : DBG;
    }

    public void onRtlPropertiesChanged(int layoutDirection) {
    }

    public boolean resolveLayoutDirection() {
        this.mPrivateFlags2 &= -49;
        if (hasRtlSupport()) {
            switch ((this.mPrivateFlags2 & VISIBILITY_MASK) >> VIEW_STATE_SELECTED) {
                case VIEW_STATE_WINDOW_FOCUSED /*1*/:
                    this.mPrivateFlags2 |= VIEW_STATE_PRESSED;
                    break;
                case VIEW_STATE_SELECTED /*2*/:
                    if (canResolveLayoutDirection()) {
                        try {
                            if (this.mParent.isLayoutDirectionResolved()) {
                                if (this.mParent.getLayoutDirection() == VIEW_STATE_WINDOW_FOCUSED) {
                                    this.mPrivateFlags2 |= VIEW_STATE_PRESSED;
                                    break;
                                }
                            }
                            return DBG;
                        } catch (AbstractMethodError e) {
                            Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                            break;
                        }
                    }
                    return DBG;
                    break;
                case TEXT_DIRECTION_LTR /*3*/:
                    if (VIEW_STATE_WINDOW_FOCUSED == TextUtils.getLayoutDirectionFromLocale(Locale.getDefault())) {
                        this.mPrivateFlags2 |= VIEW_STATE_PRESSED;
                        break;
                    }
                    break;
            }
        }
        this.mPrivateFlags2 |= VIEW_STATE_ACTIVATED;
        return true;
    }

    public boolean canResolveLayoutDirection() {
        switch (getRawLayoutDirection()) {
            case VIEW_STATE_SELECTED /*2*/:
                if (this.mParent != null) {
                    try {
                        return this.mParent.canResolveLayoutDirection();
                    } catch (AbstractMethodError e) {
                        Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                    }
                }
                return DBG;
            default:
                return true;
        }
    }

    public void resetResolvedLayoutDirection() {
        this.mPrivateFlags2 &= -49;
    }

    public boolean isLayoutDirectionInherited() {
        return getRawLayoutDirection() == VIEW_STATE_SELECTED ? true : DBG;
    }

    public boolean isLayoutDirectionResolved() {
        return (this.mPrivateFlags2 & VIEW_STATE_ACTIVATED) == VIEW_STATE_ACTIVATED ? true : DBG;
    }

    boolean isPaddingResolved() {
        return (this.mPrivateFlags2 & PFLAG_DOES_NOTHING_REUSE_PLEASE) == PFLAG_DOES_NOTHING_REUSE_PLEASE ? true : DBG;
    }

    public void resolvePadding() {
        int resolvedLayoutDirection = getLayoutDirection();
        if (!isRtlCompatibilityMode()) {
            if (!(this.mBackground == null || (this.mLeftPaddingDefined && this.mRightPaddingDefined))) {
                Rect padding = (Rect) sThreadLocal.get();
                if (padding == null) {
                    padding = new Rect();
                    sThreadLocal.set(padding);
                }
                this.mBackground.getPadding(padding);
                if (!this.mLeftPaddingDefined) {
                    this.mUserPaddingLeftInitial = padding.left;
                }
                if (!this.mRightPaddingDefined) {
                    this.mUserPaddingRightInitial = padding.right;
                }
            }
            switch (resolvedLayoutDirection) {
                case VIEW_STATE_WINDOW_FOCUSED /*1*/:
                    if (this.mUserPaddingStart != UNDEFINED_PADDING) {
                        this.mUserPaddingRight = this.mUserPaddingStart;
                    } else {
                        this.mUserPaddingRight = this.mUserPaddingRightInitial;
                    }
                    if (this.mUserPaddingEnd == UNDEFINED_PADDING) {
                        this.mUserPaddingLeft = this.mUserPaddingLeftInitial;
                        break;
                    } else {
                        this.mUserPaddingLeft = this.mUserPaddingEnd;
                        break;
                    }
                default:
                    if (this.mUserPaddingStart != UNDEFINED_PADDING) {
                        this.mUserPaddingLeft = this.mUserPaddingStart;
                    } else {
                        this.mUserPaddingLeft = this.mUserPaddingLeftInitial;
                    }
                    if (this.mUserPaddingEnd == UNDEFINED_PADDING) {
                        this.mUserPaddingRight = this.mUserPaddingRightInitial;
                        break;
                    } else {
                        this.mUserPaddingRight = this.mUserPaddingEnd;
                        break;
                    }
            }
            this.mUserPaddingBottom = this.mUserPaddingBottom >= 0 ? this.mUserPaddingBottom : this.mPaddingBottom;
        }
        internalSetPadding(this.mUserPaddingLeft, this.mPaddingTop, this.mUserPaddingRight, this.mUserPaddingBottom);
        onRtlPropertiesChanged(resolvedLayoutDirection);
        this.mPrivateFlags2 |= PFLAG_DOES_NOTHING_REUSE_PLEASE;
    }

    public void resetResolvedPadding() {
        resetResolvedPaddingInternal();
    }

    void resetResolvedPaddingInternal() {
        this.mPrivateFlags2 &= -536870913;
    }

    protected void onDetachedFromWindow() {
    }

    protected void onDetachedFromWindowInternal() {
        this.mPrivateFlags &= -67108865;
        this.mPrivateFlags3 &= -5;
        removeUnsetPressCallback();
        removeLongPressCallback();
        removePerformClickCallback();
        removeSendViewScrolledAccessibilityEventCallback();
        stopNestedScroll();
        jumpDrawablesToCurrentState();
        destroyDrawingCache();
        cleanupDraw();
        this.mCurrentAnimation = null;
    }

    private void cleanupDraw() {
        resetDisplayList();
        if (this.mAttachInfo != null) {
            this.mAttachInfo.mViewRootImpl.cancelInvalidate(this);
        }
    }

    void invalidateInheritedLayoutMode(int layoutModeOfRoot) {
    }

    protected int getWindowAttachCount() {
        return this.mWindowAttachCount;
    }

    public IBinder getWindowToken() {
        return this.mAttachInfo != null ? this.mAttachInfo.mWindowToken : null;
    }

    public WindowId getWindowId() {
        if (this.mAttachInfo == null) {
            return null;
        }
        if (this.mAttachInfo.mWindowId == null) {
            try {
                this.mAttachInfo.mIWindowId = this.mAttachInfo.mSession.getWindowId(this.mAttachInfo.mWindowToken);
                this.mAttachInfo.mWindowId = new WindowId(this.mAttachInfo.mIWindowId);
            } catch (RemoteException e) {
            }
        }
        return this.mAttachInfo.mWindowId;
    }

    public IBinder getApplicationWindowToken() {
        AttachInfo ai = this.mAttachInfo;
        if (ai == null) {
            return null;
        }
        IBinder appWindowToken = ai.mPanelParentWindowToken;
        if (appWindowToken == null) {
            return ai.mWindowToken;
        }
        return appWindowToken;
    }

    public Display getDisplay() {
        return this.mAttachInfo != null ? this.mAttachInfo.mDisplay : null;
    }

    IWindowSession getWindowSession() {
        return this.mAttachInfo != null ? this.mAttachInfo.mSession : null;
    }

    void dispatchAttachedToWindow(AttachInfo info, int visibility) {
        CopyOnWriteArrayList<OnAttachStateChangeListener> listeners = null;
        this.mAttachInfo = info;
        if (this.mOverlay != null) {
            this.mOverlay.getOverlayView().dispatchAttachedToWindow(info, visibility);
        }
        this.mWindowAttachCount += VIEW_STATE_WINDOW_FOCUSED;
        this.mPrivateFlags |= SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if (this.mFloatingTreeObserver != null) {
            info.mTreeObserver.merge(this.mFloatingTreeObserver);
            this.mFloatingTreeObserver = null;
        }
        if ((this.mPrivateFlags & STATUS_BAR_DISABLE_NOTIFICATION_TICKER) != 0) {
            this.mAttachInfo.mScrollContainers.add(this);
            this.mPrivateFlags |= STATUS_BAR_DISABLE_SYSTEM_INFO;
        }
        performCollectViewAttributes(this.mAttachInfo, visibility);
        onAttachedToWindow();
        ListenerInfo li = this.mListenerInfo;
        if (li != null) {
            listeners = li.mOnAttachStateChangeListeners;
        }
        if (listeners != null && listeners.size() > 0) {
            Iterator i$ = listeners.iterator();
            while (i$.hasNext()) {
                ((OnAttachStateChangeListener) i$.next()).onViewAttachedToWindow(this);
            }
        }
        int vis = info.mWindowVisibility;
        if (vis != VIEW_STATE_ENABLED) {
            onWindowVisibilityChanged(vis);
        }
        if ((this.mPrivateFlags & SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) != 0) {
            refreshDrawableState();
        }
        needGlobalAttributesUpdate(DBG);
    }

    void dispatchDetachedFromWindow() {
        CopyOnWriteArrayList<OnAttachStateChangeListener> listeners;
        AttachInfo info = this.mAttachInfo;
        if (!(info == null || info.mWindowVisibility == VIEW_STATE_ENABLED)) {
            onWindowVisibilityChanged(VIEW_STATE_ENABLED);
        }
        onDetachedFromWindow();
        onDetachedFromWindowInternal();
        ListenerInfo li = this.mListenerInfo;
        if (li != null) {
            listeners = li.mOnAttachStateChangeListeners;
        } else {
            listeners = null;
        }
        if (listeners != null && listeners.size() > 0) {
            Iterator i$ = listeners.iterator();
            while (i$.hasNext()) {
                ((OnAttachStateChangeListener) i$.next()).onViewDetachedFromWindow(this);
            }
        }
        if ((this.mPrivateFlags & STATUS_BAR_DISABLE_SYSTEM_INFO) != 0) {
            this.mAttachInfo.mScrollContainers.remove(this);
            this.mPrivateFlags &= -1048577;
        }
        this.mAttachInfo = null;
        if (this.mOverlay != null) {
            this.mOverlay.getOverlayView().dispatchDetachedFromWindow();
        }
    }

    public final void cancelPendingInputEvents() {
        dispatchCancelPendingInputEvents();
    }

    void dispatchCancelPendingInputEvents() {
        this.mPrivateFlags3 &= -17;
        onCancelPendingInputEvents();
        if ((this.mPrivateFlags3 & VIEW_STATE_PRESSED) != VIEW_STATE_PRESSED) {
            throw new SuperNotCalledException("View " + getClass().getSimpleName() + " did not call through to super.onCancelPendingInputEvents()");
        }
    }

    public void onCancelPendingInputEvents() {
        removePerformClickCallback();
        cancelLongPress();
        this.mPrivateFlags3 |= VIEW_STATE_PRESSED;
    }

    public void saveHierarchyState(SparseArray<Parcelable> container) {
        dispatchSaveInstanceState(container);
    }

    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        if (this.mID != NO_ID && (this.mViewFlags & STATUS_BAR_DISABLE_EXPAND) == 0) {
            this.mPrivateFlags &= -131073;
            Parcelable state = onSaveInstanceState();
            if ((this.mPrivateFlags & WILL_NOT_CACHE_DRAWING) == 0) {
                throw new IllegalStateException("Derived class did not call super.onSaveInstanceState()");
            } else if (state != null) {
                container.put(this.mID, state);
            }
        }
    }

    protected Parcelable onSaveInstanceState() {
        this.mPrivateFlags |= WILL_NOT_CACHE_DRAWING;
        return BaseSavedState.EMPTY_STATE;
    }

    public void restoreHierarchyState(SparseArray<Parcelable> container) {
        dispatchRestoreInstanceState(container);
    }

    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        if (this.mID != NO_ID) {
            Parcelable state = (Parcelable) container.get(this.mID);
            if (state != null) {
                this.mPrivateFlags &= -131073;
                onRestoreInstanceState(state);
                if ((this.mPrivateFlags & WILL_NOT_CACHE_DRAWING) == 0) {
                    throw new IllegalStateException("Derived class did not call super.onRestoreInstanceState()");
                }
            }
        }
    }

    protected void onRestoreInstanceState(Parcelable state) {
        this.mPrivateFlags |= WILL_NOT_CACHE_DRAWING;
        if (state != BaseSavedState.EMPTY_STATE && state != null) {
            throw new IllegalArgumentException("Wrong state class, expecting View State but received " + state.getClass().toString() + " instead. This usually happens " + "when two views of different type have the same id in the same hierarchy. " + "This view's id is " + ViewDebug.resolveId(this.mContext, getId()) + ". Make sure " + "other views do not use the same id.");
        }
    }

    public long getDrawingTime() {
        return this.mAttachInfo != null ? this.mAttachInfo.mDrawingTime : 0;
    }

    public void setDuplicateParentStateEnabled(boolean enabled) {
        setFlags(enabled ? STATUS_BAR_DISABLE_BACK : VISIBLE, STATUS_BAR_DISABLE_BACK);
    }

    public boolean isDuplicateParentStateEnabled() {
        return (this.mViewFlags & STATUS_BAR_DISABLE_BACK) == STATUS_BAR_DISABLE_BACK ? true : DBG;
    }

    public void setLayerType(int layerType, Paint paint) {
        if (layerType < 0 || layerType > VIEW_STATE_SELECTED) {
            throw new IllegalArgumentException("Layer type can only be one of: LAYER_TYPE_NONE, LAYER_TYPE_SOFTWARE or LAYER_TYPE_HARDWARE");
        } else if (this.mRenderNode.setLayerType(layerType)) {
            if (this.mLayerType == VIEW_STATE_WINDOW_FOCUSED) {
                destroyDrawingCache();
            }
            this.mLayerType = layerType;
            if (this.mLayerType == 0 ? true : DBG) {
                paint = null;
            } else if (paint == null) {
                paint = new Paint();
            }
            this.mLayerPaint = paint;
            this.mRenderNode.setLayerPaint(this.mLayerPaint);
            invalidateParentCaches();
            invalidate(true);
        } else {
            setLayerPaint(paint);
        }
    }

    public void setLayerPaint(Paint paint) {
        int layerType = getLayerType();
        if (layerType != 0) {
            if (paint == null) {
                paint = new Paint();
            }
            this.mLayerPaint = paint;
            if (layerType != VIEW_STATE_SELECTED) {
                invalidate();
            } else if (this.mRenderNode.setLayerPaint(this.mLayerPaint)) {
                invalidateViewProperty(DBG, DBG);
            }
        }
    }

    boolean hasStaticLayer() {
        return true;
    }

    public int getLayerType() {
        return this.mLayerType;
    }

    public void buildLayer() {
        if (this.mLayerType != 0) {
            AttachInfo attachInfo = this.mAttachInfo;
            if (attachInfo == null) {
                throw new IllegalStateException("This view must be attached to a window first");
            } else if (getWidth() != 0 && getHeight() != 0) {
                switch (this.mLayerType) {
                    case VIEW_STATE_WINDOW_FOCUSED /*1*/:
                        buildDrawingCache(true);
                    case VIEW_STATE_SELECTED /*2*/:
                        updateDisplayListIfDirty();
                        if (attachInfo.mHardwareRenderer != null && this.mRenderNode.isValid()) {
                            attachInfo.mHardwareRenderer.buildLayer(this.mRenderNode);
                        }
                    default:
                }
            }
        }
    }

    HardwareLayer getHardwareLayer() {
        return null;
    }

    protected void destroyHardwareResources() {
        resetDisplayList();
    }

    public void setDrawingCacheEnabled(boolean enabled) {
        int i = VISIBLE;
        this.mCachingFailed = DBG;
        if (enabled) {
            i = SYSTEM_UI_TRANSPARENT;
        }
        setFlags(i, SYSTEM_UI_TRANSPARENT);
    }

    @ExportedProperty(category = "drawing")
    public boolean isDrawingCacheEnabled() {
        return (this.mViewFlags & SYSTEM_UI_TRANSPARENT) == SYSTEM_UI_TRANSPARENT ? true : DBG;
    }

    public void outputDirtyFlags(String indent, boolean clear, int clearMask) {
        Log.d(VIEW_LOG_TAG, indent + this + "             DIRTY(" + (this.mPrivateFlags & PFLAG_DIRTY_MASK) + ") DRAWN(" + (this.mPrivateFlags & VIEW_STATE_ACTIVATED) + ")" + " CACHE_VALID(" + (this.mPrivateFlags & SYSTEM_UI_TRANSPARENT) + ") INVALIDATED(" + (this.mPrivateFlags & UNDEFINED_PADDING) + ")");
        if (clear) {
            this.mPrivateFlags &= clearMask;
        }
        if (this instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) this;
            int count = parent.getChildCount();
            for (int i = VISIBLE; i < count; i += VIEW_STATE_WINDOW_FOCUSED) {
                parent.getChildAt(i).outputDirtyFlags(indent + "  ", clear, clearMask);
            }
        }
    }

    protected void dispatchGetDisplayList() {
    }

    public boolean canHaveDisplayList() {
        return (this.mAttachInfo == null || this.mAttachInfo.mHardwareRenderer == null) ? DBG : true;
    }

    private void updateDisplayListIfDirty() {
        RenderNode renderNode = this.mRenderNode;
        if (!canHaveDisplayList()) {
            return;
        }
        if ((this.mPrivateFlags & SYSTEM_UI_TRANSPARENT) != 0 && renderNode.isValid() && !this.mRecreateDisplayList) {
            this.mPrivateFlags |= 32800;
            this.mPrivateFlags &= -6291457;
        } else if (!renderNode.isValid() || this.mRecreateDisplayList) {
            this.mRecreateDisplayList = true;
            int width = this.mRight - this.mLeft;
            int height = this.mBottom - this.mTop;
            int layerType = getLayerType();
            HardwareCanvas canvas = renderNode.start(width, height);
            canvas.setHighContrastText(this.mAttachInfo.mHighContrastText);
            try {
                HardwareLayer layer = getHardwareLayer();
                if (layer != null && layer.isValid()) {
                    canvas.drawHardwareLayer(layer, 0.0f, 0.0f, this.mLayerPaint);
                } else if (layerType == VIEW_STATE_WINDOW_FOCUSED) {
                    buildDrawingCache(true);
                    Bitmap cache = getDrawingCache(true);
                    if (cache != null) {
                        canvas.drawBitmap(cache, 0.0f, 0.0f, this.mLayerPaint);
                    }
                } else {
                    computeScroll();
                    canvas.translate((float) (-this.mScrollX), (float) (-this.mScrollY));
                    this.mPrivateFlags |= 32800;
                    this.mPrivateFlags &= -6291457;
                    if ((this.mPrivateFlags & WILL_NOT_DRAW) == WILL_NOT_DRAW) {
                        dispatchDraw(canvas);
                        if (!(this.mOverlay == null || this.mOverlay.isEmpty())) {
                            this.mOverlay.getOverlayView().draw(canvas);
                        }
                    } else {
                        draw(canvas);
                    }
                }
                renderNode.end(canvas);
                setDisplayListProperties(renderNode);
            } catch (Throwable th) {
                renderNode.end(canvas);
                setDisplayListProperties(renderNode);
            }
        } else {
            this.mPrivateFlags |= 32800;
            this.mPrivateFlags &= -6291457;
            dispatchGetDisplayList();
        }
    }

    public RenderNode getDisplayList() {
        updateDisplayListIfDirty();
        return this.mRenderNode;
    }

    private void resetDisplayList() {
        if (this.mRenderNode.isValid()) {
            this.mRenderNode.destroyDisplayListData();
        }
        if (this.mBackgroundRenderNode != null && this.mBackgroundRenderNode.isValid()) {
            this.mBackgroundRenderNode.destroyDisplayListData();
        }
    }

    public Bitmap getDrawingCache() {
        return getDrawingCache(DBG);
    }

    public Bitmap getDrawingCache(boolean autoScale) {
        if ((this.mViewFlags & WILL_NOT_CACHE_DRAWING) == WILL_NOT_CACHE_DRAWING) {
            return null;
        }
        if ((this.mViewFlags & SYSTEM_UI_TRANSPARENT) == SYSTEM_UI_TRANSPARENT) {
            buildDrawingCache(autoScale);
        }
        return autoScale ? this.mDrawingCache : this.mUnscaledDrawingCache;
    }

    public void destroyDrawingCache() {
        if (this.mDrawingCache != null) {
            this.mDrawingCache.recycle();
            this.mDrawingCache = null;
        }
        if (this.mUnscaledDrawingCache != null) {
            this.mUnscaledDrawingCache.recycle();
            this.mUnscaledDrawingCache = null;
        }
    }

    public void setDrawingCacheBackgroundColor(int color) {
        if (color != this.mDrawingCacheBackgroundColor) {
            this.mDrawingCacheBackgroundColor = color;
            this.mPrivateFlags &= -32769;
        }
    }

    public int getDrawingCacheBackgroundColor() {
        return this.mDrawingCacheBackgroundColor;
    }

    public void buildDrawingCache() {
        buildDrawingCache(DBG);
    }

    public void buildDrawingCache(boolean autoScale) {
        if ((this.mPrivateFlags & SYSTEM_UI_TRANSPARENT) != 0) {
            if (autoScale) {
                if (this.mDrawingCache != null) {
                    return;
                }
            } else if (this.mUnscaledDrawingCache != null) {
                return;
            }
        }
        if (Trace.isTagEnabled(8)) {
            Trace.traceBegin(8, "buildDrawingCache/SW Layer for " + getClass().getSimpleName());
        }
        try {
            buildDrawingCacheImpl(autoScale);
        } finally {
            Trace.traceEnd(8);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void buildDrawingCacheImpl(boolean r24) {
        /*
        r23 = this;
        r20 = 0;
        r0 = r20;
        r1 = r23;
        r1.mCachingFailed = r0;
        r0 = r23;
        r0 = r0.mRight;
        r20 = r0;
        r0 = r23;
        r0 = r0.mLeft;
        r21 = r0;
        r19 = r20 - r21;
        r0 = r23;
        r0 = r0.mBottom;
        r20 = r0;
        r0 = r23;
        r0 = r0.mTop;
        r21 = r0;
        r10 = r20 - r21;
        r0 = r23;
        r2 = r0.mAttachInfo;
        if (r2 == 0) goto L_0x00d3;
    L_0x002a:
        r0 = r2.mScalingRequired;
        r20 = r0;
        if (r20 == 0) goto L_0x00d3;
    L_0x0030:
        r17 = 1;
    L_0x0032:
        if (r24 == 0) goto L_0x005a;
    L_0x0034:
        if (r17 == 0) goto L_0x005a;
    L_0x0036:
        r0 = r19;
        r0 = (float) r0;
        r20 = r0;
        r0 = r2.mApplicationScale;
        r21 = r0;
        r20 = r20 * r21;
        r21 = 1056964608; // 0x3f000000 float:0.5 double:5.222099017E-315;
        r20 = r20 + r21;
        r0 = r20;
        r0 = (int) r0;
        r19 = r0;
        r0 = (float) r10;
        r20 = r0;
        r0 = r2.mApplicationScale;
        r21 = r0;
        r20 = r20 * r21;
        r21 = 1056964608; // 0x3f000000 float:0.5 double:5.222099017E-315;
        r20 = r20 + r21;
        r0 = r20;
        r10 = (int) r0;
    L_0x005a:
        r0 = r23;
        r6 = r0.mDrawingCacheBackgroundColor;
        if (r6 != 0) goto L_0x0066;
    L_0x0060:
        r20 = r23.isOpaque();
        if (r20 == 0) goto L_0x00d7;
    L_0x0066:
        r11 = 1;
    L_0x0067:
        if (r2 == 0) goto L_0x00d9;
    L_0x0069:
        r0 = r2.mUse32BitDrawingCache;
        r20 = r0;
        if (r20 == 0) goto L_0x00d9;
    L_0x006f:
        r18 = 1;
    L_0x0071:
        r21 = r19 * r10;
        if (r11 == 0) goto L_0x00dc;
    L_0x0075:
        if (r18 != 0) goto L_0x00dc;
    L_0x0077:
        r20 = 2;
    L_0x0079:
        r20 = r20 * r21;
        r0 = r20;
        r12 = (long) r0;
        r0 = r23;
        r0 = r0.mContext;
        r20 = r0;
        r20 = android.view.ViewConfiguration.get(r20);
        r20 = r20.getScaledMaximumDrawingCacheSize();
        r0 = r20;
        r8 = (long) r0;
        if (r19 <= 0) goto L_0x0097;
    L_0x0091:
        if (r10 <= 0) goto L_0x0097;
    L_0x0093:
        r20 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1));
        if (r20 <= 0) goto L_0x00df;
    L_0x0097:
        if (r19 <= 0) goto L_0x00c7;
    L_0x0099:
        if (r10 <= 0) goto L_0x00c7;
    L_0x009b:
        r20 = "View";
        r21 = new java.lang.StringBuilder;
        r21.<init>();
        r22 = "View too large to fit into drawing cache, needs ";
        r21 = r21.append(r22);
        r0 = r21;
        r21 = r0.append(r12);
        r22 = " bytes, only ";
        r21 = r21.append(r22);
        r0 = r21;
        r21 = r0.append(r8);
        r22 = " available";
        r21 = r21.append(r22);
        r21 = r21.toString();
        android.util.Log.w(r20, r21);
    L_0x00c7:
        r23.destroyDrawingCache();
        r20 = 1;
        r0 = r20;
        r1 = r23;
        r1.mCachingFailed = r0;
    L_0x00d2:
        return;
    L_0x00d3:
        r17 = 0;
        goto L_0x0032;
    L_0x00d7:
        r11 = 0;
        goto L_0x0067;
    L_0x00d9:
        r18 = 0;
        goto L_0x0071;
    L_0x00dc:
        r20 = 4;
        goto L_0x0079;
    L_0x00df:
        r5 = 1;
        if (r24 == 0) goto L_0x0236;
    L_0x00e2:
        r0 = r23;
        r3 = r0.mDrawingCache;
    L_0x00e6:
        if (r3 == 0) goto L_0x00fa;
    L_0x00e8:
        r20 = r3.getWidth();
        r0 = r20;
        r1 = r19;
        if (r0 != r1) goto L_0x00fa;
    L_0x00f2:
        r20 = r3.getHeight();
        r0 = r20;
        if (r0 == r10) goto L_0x0146;
    L_0x00fa:
        if (r11 != 0) goto L_0x023c;
    L_0x00fc:
        r0 = r23;
        r0 = r0.mViewFlags;
        r20 = r0;
        r21 = 1572864; // 0x180000 float:2.204052E-39 double:7.77098E-318;
        r20 = r20 & r21;
        r14 = android.graphics.Bitmap.Config.ARGB_8888;
    L_0x0108:
        if (r3 == 0) goto L_0x010d;
    L_0x010a:
        r3.recycle();
    L_0x010d:
        r0 = r23;
        r0 = r0.mResources;	 Catch:{ OutOfMemoryError -> 0x024b }
        r20 = r0;
        r20 = r20.getDisplayMetrics();	 Catch:{ OutOfMemoryError -> 0x024b }
        r0 = r20;
        r1 = r19;
        r3 = android.graphics.Bitmap.createBitmap(r0, r1, r10, r14);	 Catch:{ OutOfMemoryError -> 0x024b }
        r20 = r23.getResources();	 Catch:{ OutOfMemoryError -> 0x024b }
        r20 = r20.getDisplayMetrics();	 Catch:{ OutOfMemoryError -> 0x024b }
        r0 = r20;
        r0 = r0.densityDpi;	 Catch:{ OutOfMemoryError -> 0x024b }
        r20 = r0;
        r0 = r20;
        r3.setDensity(r0);	 Catch:{ OutOfMemoryError -> 0x024b }
        if (r24 == 0) goto L_0x0245;
    L_0x0134:
        r0 = r23;
        r0.mDrawingCache = r3;	 Catch:{ OutOfMemoryError -> 0x024b }
    L_0x0138:
        if (r11 == 0) goto L_0x0143;
    L_0x013a:
        if (r18 == 0) goto L_0x0143;
    L_0x013c:
        r20 = 0;
        r0 = r20;
        r3.setHasAlpha(r0);	 Catch:{ OutOfMemoryError -> 0x024b }
    L_0x0143:
        if (r6 == 0) goto L_0x0269;
    L_0x0145:
        r5 = 1;
    L_0x0146:
        if (r2 == 0) goto L_0x026c;
    L_0x0148:
        r4 = r2.mCanvas;
        if (r4 != 0) goto L_0x0151;
    L_0x014c:
        r4 = new android.graphics.Canvas;
        r4.<init>();
    L_0x0151:
        r4.setBitmap(r3);
        r20 = 0;
        r0 = r20;
        r2.mCanvas = r0;
    L_0x015a:
        if (r5 == 0) goto L_0x015f;
    L_0x015c:
        r3.eraseColor(r6);
    L_0x015f:
        r23.computeScroll();
        r15 = r4.save();
        if (r24 == 0) goto L_0x0175;
    L_0x0168:
        if (r17 == 0) goto L_0x0175;
    L_0x016a:
        r0 = r2.mApplicationScale;
        r16 = r0;
        r0 = r16;
        r1 = r16;
        r4.scale(r0, r1);
    L_0x0175:
        r0 = r23;
        r0 = r0.mScrollX;
        r20 = r0;
        r0 = r20;
        r0 = -r0;
        r20 = r0;
        r0 = r20;
        r0 = (float) r0;
        r20 = r0;
        r0 = r23;
        r0 = r0.mScrollY;
        r21 = r0;
        r0 = r21;
        r0 = -r0;
        r21 = r0;
        r0 = r21;
        r0 = (float) r0;
        r21 = r0;
        r0 = r20;
        r1 = r21;
        r4.translate(r0, r1);
        r0 = r23;
        r0 = r0.mPrivateFlags;
        r20 = r0;
        r20 = r20 | 32;
        r0 = r20;
        r1 = r23;
        r1.mPrivateFlags = r0;
        r0 = r23;
        r0 = r0.mAttachInfo;
        r20 = r0;
        if (r20 == 0) goto L_0x01c8;
    L_0x01b2:
        r0 = r23;
        r0 = r0.mAttachInfo;
        r20 = r0;
        r0 = r20;
        r0 = r0.mHardwareAccelerated;
        r20 = r0;
        if (r20 == 0) goto L_0x01c8;
    L_0x01c0:
        r0 = r23;
        r0 = r0.mLayerType;
        r20 = r0;
        if (r20 == 0) goto L_0x01d9;
    L_0x01c8:
        r0 = r23;
        r0 = r0.mPrivateFlags;
        r20 = r0;
        r21 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
        r20 = r20 | r21;
        r0 = r20;
        r1 = r23;
        r1.mPrivateFlags = r0;
    L_0x01d9:
        r0 = r23;
        r0 = r0.mPrivateFlags;
        r20 = r0;
        r0 = r20;
        r0 = r0 & 128;
        r20 = r0;
        r21 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
        r0 = r20;
        r1 = r21;
        if (r0 != r1) goto L_0x0273;
    L_0x01ed:
        r0 = r23;
        r0 = r0.mPrivateFlags;
        r20 = r0;
        r21 = -6291457; // 0xffffffffff9fffff float:NaN double:NaN;
        r20 = r20 & r21;
        r0 = r20;
        r1 = r23;
        r1.mPrivateFlags = r0;
        r0 = r23;
        r0.dispatchDraw(r4);
        r0 = r23;
        r0 = r0.mOverlay;
        r20 = r0;
        if (r20 == 0) goto L_0x0226;
    L_0x020b:
        r0 = r23;
        r0 = r0.mOverlay;
        r20 = r0;
        r20 = r20.isEmpty();
        if (r20 != 0) goto L_0x0226;
    L_0x0217:
        r0 = r23;
        r0 = r0.mOverlay;
        r20 = r0;
        r20 = r20.getOverlayView();
        r0 = r20;
        r0.draw(r4);
    L_0x0226:
        r4.restoreToCount(r15);
        r20 = 0;
        r0 = r20;
        r4.setBitmap(r0);
        if (r2 == 0) goto L_0x00d2;
    L_0x0232:
        r2.mCanvas = r4;
        goto L_0x00d2;
    L_0x0236:
        r0 = r23;
        r3 = r0.mUnscaledDrawingCache;
        goto L_0x00e6;
    L_0x023c:
        if (r18 == 0) goto L_0x0242;
    L_0x023e:
        r14 = android.graphics.Bitmap.Config.ARGB_8888;
    L_0x0240:
        goto L_0x0108;
    L_0x0242:
        r14 = android.graphics.Bitmap.Config.RGB_565;
        goto L_0x0240;
    L_0x0245:
        r0 = r23;
        r0.mUnscaledDrawingCache = r3;	 Catch:{ OutOfMemoryError -> 0x024b }
        goto L_0x0138;
    L_0x024b:
        r7 = move-exception;
        if (r24 == 0) goto L_0x0260;
    L_0x024e:
        r20 = 0;
        r0 = r20;
        r1 = r23;
        r1.mDrawingCache = r0;
    L_0x0256:
        r20 = 1;
        r0 = r20;
        r1 = r23;
        r1.mCachingFailed = r0;
        goto L_0x00d2;
    L_0x0260:
        r20 = 0;
        r0 = r20;
        r1 = r23;
        r1.mUnscaledDrawingCache = r0;
        goto L_0x0256;
    L_0x0269:
        r5 = 0;
        goto L_0x0146;
    L_0x026c:
        r4 = new android.graphics.Canvas;
        r4.<init>(r3);
        goto L_0x015a;
    L_0x0273:
        r0 = r23;
        r0.draw(r4);
        goto L_0x0226;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.View.buildDrawingCacheImpl(boolean):void");
    }

    Bitmap createSnapshot(Config quality, int backgroundColor, boolean skipChildren) {
        int width = this.mRight - this.mLeft;
        int height = this.mBottom - this.mTop;
        AttachInfo attachInfo = this.mAttachInfo;
        float scale = attachInfo != null ? attachInfo.mApplicationScale : WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        width = (int) ((((float) width) * scale) + 0.5f);
        height = (int) ((((float) height) * scale) + 0.5f);
        DisplayMetrics displayMetrics = this.mResources.getDisplayMetrics();
        if (width <= 0) {
            width = VIEW_STATE_WINDOW_FOCUSED;
        }
        if (height <= 0) {
            height = VIEW_STATE_WINDOW_FOCUSED;
        }
        Bitmap bitmap = Bitmap.createBitmap(displayMetrics, width, height, quality);
        if (bitmap == null) {
            throw new OutOfMemoryError();
        }
        Canvas canvas;
        Resources resources = getResources();
        if (resources != null) {
            bitmap.setDensity(resources.getDisplayMetrics().densityDpi);
        }
        if (attachInfo != null) {
            canvas = attachInfo.mCanvas;
            if (canvas == null) {
                canvas = new Canvas();
            }
            canvas.setBitmap(bitmap);
            attachInfo.mCanvas = null;
        } else {
            canvas = new Canvas(bitmap);
        }
        if ((MEASURED_STATE_MASK & backgroundColor) != 0) {
            bitmap.eraseColor(backgroundColor);
        }
        computeScroll();
        int restoreCount = canvas.save();
        canvas.scale(scale, scale);
        canvas.translate((float) (-this.mScrollX), (float) (-this.mScrollY));
        int flags = this.mPrivateFlags;
        this.mPrivateFlags &= -6291457;
        if ((this.mPrivateFlags & WILL_NOT_DRAW) == WILL_NOT_DRAW) {
            dispatchDraw(canvas);
            if (!(this.mOverlay == null || this.mOverlay.isEmpty())) {
                this.mOverlay.getOverlayView().draw(canvas);
            }
        } else {
            draw(canvas);
        }
        this.mPrivateFlags = flags;
        canvas.restoreToCount(restoreCount);
        canvas.setBitmap(null);
        if (attachInfo != null) {
            attachInfo.mCanvas = canvas;
        }
        return bitmap;
    }

    public boolean isInEditMode() {
        return DBG;
    }

    protected boolean isPaddingOffsetRequired() {
        return DBG;
    }

    protected int getLeftPaddingOffset() {
        return VISIBLE;
    }

    protected int getRightPaddingOffset() {
        return VISIBLE;
    }

    protected int getTopPaddingOffset() {
        return VISIBLE;
    }

    protected int getBottomPaddingOffset() {
        return VISIBLE;
    }

    protected int getFadeTop(boolean offsetRequired) {
        int top = this.mPaddingTop;
        if (offsetRequired) {
            return top + getTopPaddingOffset();
        }
        return top;
    }

    protected int getFadeHeight(boolean offsetRequired) {
        int padding = this.mPaddingTop;
        if (offsetRequired) {
            padding += getTopPaddingOffset();
        }
        return ((this.mBottom - this.mTop) - this.mPaddingBottom) - padding;
    }

    @ExportedProperty(category = "drawing")
    public boolean isHardwareAccelerated() {
        return (this.mAttachInfo == null || !this.mAttachInfo.mHardwareAccelerated) ? DBG : true;
    }

    public void setClipBounds(Rect clipBounds) {
        if (clipBounds == this.mClipBounds) {
            return;
        }
        if (clipBounds == null || !clipBounds.equals(this.mClipBounds)) {
            if (clipBounds == null) {
                this.mClipBounds = null;
            } else if (this.mClipBounds == null) {
                this.mClipBounds = new Rect(clipBounds);
            } else {
                this.mClipBounds.set(clipBounds);
            }
            this.mRenderNode.setClipBounds(this.mClipBounds);
            invalidateViewProperty(DBG, DBG);
        }
    }

    public Rect getClipBounds() {
        return this.mClipBounds != null ? new Rect(this.mClipBounds) : null;
    }

    private boolean drawAnimation(ViewGroup parent, long drawingTime, Animation a, boolean scalingRequired) {
        Transformation invalidationTransform;
        int flags = parent.mGroupFlags;
        if (!a.isInitialized()) {
            a.initialize(this.mRight - this.mLeft, this.mBottom - this.mTop, parent.getWidth(), parent.getHeight());
            a.initializeInvalidateRegion(VISIBLE, VISIBLE, this.mRight - this.mLeft, this.mBottom - this.mTop);
            if (this.mAttachInfo != null) {
                a.setListenerHandler(this.mAttachInfo.mHandler);
            }
            onAnimationStart();
        }
        Transformation t = parent.getChildTransformation();
        boolean more = a.getTransformation(drawingTime, t, WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL);
        if (!scalingRequired || this.mAttachInfo.mApplicationScale == WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL) {
            invalidationTransform = t;
        } else {
            if (parent.mInvalidationTransformation == null) {
                parent.mInvalidationTransformation = new Transformation();
            }
            invalidationTransform = parent.mInvalidationTransformation;
            a.getTransformation(drawingTime, invalidationTransform, WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL);
        }
        if (more) {
            if (a.willChangeBounds()) {
                if (parent.mInvalidateRegion == null) {
                    parent.mInvalidateRegion = new RectF();
                }
                RectF region = parent.mInvalidateRegion;
                a.getInvalidateRegion(VISIBLE, VISIBLE, this.mRight - this.mLeft, this.mBottom - this.mTop, region, invalidationTransform);
                parent.mPrivateFlags |= VIEW_STATE_ACCELERATED;
                int left = this.mLeft + ((int) region.left);
                int top = this.mTop + ((int) region.top);
                parent.invalidate(left, top, ((int) (region.width() + 0.5f)) + left, ((int) (region.height() + 0.5f)) + top);
            } else if ((flags & KeyEvent.KEYCODE_NUMPAD_0) == WILL_NOT_DRAW) {
                parent.mGroupFlags |= VIEW_STATE_FOCUSED;
            } else if ((flags & VIEW_STATE_FOCUSED) == 0) {
                parent.mPrivateFlags |= VIEW_STATE_ACCELERATED;
                parent.invalidate(this.mLeft, this.mTop, this.mRight, this.mBottom);
            }
        }
        return more;
    }

    void setDisplayListProperties(RenderNode renderNode) {
        if (renderNode != null) {
            renderNode.setHasOverlappingRendering(hasOverlappingRendering());
            if (this.mParent instanceof ViewGroup) {
                renderNode.setClipToBounds((((ViewGroup) this.mParent).mGroupFlags & VIEW_STATE_WINDOW_FOCUSED) != 0 ? true : DBG);
            }
            float alpha = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
            if ((this.mParent instanceof ViewGroup) && (((ViewGroup) this.mParent).mGroupFlags & SYSTEM_UI_FLAG_IMMERSIVE) != 0) {
                ViewGroup parentVG = this.mParent;
                Transformation t = parentVG.getChildTransformation();
                if (parentVG.getChildStaticTransformation(this, t)) {
                    int transformType = t.getTransformationType();
                    if (transformType != 0) {
                        if ((transformType & VIEW_STATE_WINDOW_FOCUSED) != 0) {
                            alpha = t.getAlpha();
                        }
                        if ((transformType & VIEW_STATE_SELECTED) != 0) {
                            renderNode.setStaticMatrix(t.getMatrix());
                        }
                    }
                }
            }
            if (this.mTransformationInfo != null) {
                alpha *= getFinalAlpha();
                if (alpha < WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL && onSetAlpha((int) (255.0f * alpha))) {
                    alpha = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
                }
                renderNode.setAlpha(alpha);
            } else if (alpha < WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL) {
                renderNode.setAlpha(alpha);
            }
        }
    }

    boolean draw(Canvas canvas, ViewGroup parent, long drawingTime) {
        boolean caching;
        float alpha;
        int scrollX;
        int scrollY;
        boolean usingRenderNodeProperties = (this.mAttachInfo == null || !this.mAttachInfo.mHardwareAccelerated) ? DBG : true;
        boolean more = DBG;
        boolean childHasIdentityMatrix = hasIdentityMatrix();
        int flags = parent.mGroupFlags;
        if ((flags & VIEW_STATE_DRAG_CAN_ACCEPT) == VIEW_STATE_DRAG_CAN_ACCEPT) {
            parent.getChildTransformation().clear();
            parent.mGroupFlags &= -257;
        }
        Transformation transformToApply = null;
        boolean concatMatrix = DBG;
        boolean scalingRequired = DBG;
        int layerType = getLayerType();
        boolean hardwareAccelerated = canvas.isHardwareAccelerated();
        if ((SYSTEM_UI_TRANSPARENT & flags) == 0 && (flags & RECENT_APPS_VISIBLE) == 0) {
            caching = (layerType != 0 || hardwareAccelerated) ? true : DBG;
        } else {
            caching = true;
            if (this.mAttachInfo != null) {
                scalingRequired = this.mAttachInfo.mScalingRequired;
            }
        }
        Animation a = getAnimation();
        if (a != null) {
            more = drawAnimation(parent, drawingTime, a, scalingRequired);
            concatMatrix = a.willChangeTransformationMatrix();
            if (concatMatrix) {
                this.mPrivateFlags3 |= VIEW_STATE_WINDOW_FOCUSED;
            }
            transformToApply = parent.getChildTransformation();
        } else {
            if ((this.mPrivateFlags3 & VIEW_STATE_WINDOW_FOCUSED) != 0) {
                this.mRenderNode.setAnimationMatrix(null);
                this.mPrivateFlags3 &= -2;
            }
            if (!(usingRenderNodeProperties || (flags & SYSTEM_UI_FLAG_IMMERSIVE) == 0)) {
                Transformation t = parent.getChildTransformation();
                if (parent.getChildStaticTransformation(this, t)) {
                    int transformType = t.getTransformationType();
                    transformToApply = transformType != 0 ? t : null;
                    concatMatrix = (transformType & VIEW_STATE_SELECTED) != 0 ? true : DBG;
                }
            }
        }
        concatMatrix |= !childHasIdentityMatrix ? VIEW_STATE_WINDOW_FOCUSED : VISIBLE;
        this.mPrivateFlags |= VIEW_STATE_ACTIVATED;
        if (!concatMatrix && (flags & GLES10.GL_EXP2) == VIEW_STATE_WINDOW_FOCUSED) {
            if (canvas.quickReject((float) this.mLeft, (float) this.mTop, (float) this.mRight, (float) this.mBottom, EdgeType.BW) && (this.mPrivateFlags & VIEW_STATE_ACCELERATED) == 0) {
                this.mPrivateFlags2 |= STATUS_BAR_UNHIDE;
                return more;
            }
        }
        this.mPrivateFlags2 &= -268435457;
        if (hardwareAccelerated) {
            this.mRecreateDisplayList = (this.mPrivateFlags & UNDEFINED_PADDING) == UNDEFINED_PADDING ? true : DBG;
            this.mPrivateFlags &= ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        }
        RenderNode renderNode = null;
        Bitmap cache = null;
        boolean hasDisplayList = DBG;
        if (caching) {
            if (hardwareAccelerated) {
                switch (layerType) {
                    case VISIBLE /*0*/:
                        hasDisplayList = canHaveDisplayList();
                        break;
                    case VIEW_STATE_WINDOW_FOCUSED /*1*/:
                        if (!usingRenderNodeProperties) {
                            buildDrawingCache(true);
                            cache = getDrawingCache(true);
                            break;
                        }
                        hasDisplayList = canHaveDisplayList();
                        break;
                    case VIEW_STATE_SELECTED /*2*/:
                        if (usingRenderNodeProperties) {
                            hasDisplayList = canHaveDisplayList();
                            break;
                        }
                        break;
                    default:
                        break;
                }
            }
            if (layerType != 0) {
                layerType = VIEW_STATE_WINDOW_FOCUSED;
                buildDrawingCache(true);
            }
            cache = getDrawingCache(true);
        }
        usingRenderNodeProperties &= hasDisplayList;
        if (usingRenderNodeProperties) {
            renderNode = getDisplayList();
            if (!renderNode.isValid()) {
                renderNode = null;
                hasDisplayList = DBG;
                usingRenderNodeProperties = DBG;
            }
        }
        int sx = VISIBLE;
        int sy = VISIBLE;
        if (!hasDisplayList) {
            computeScroll();
            sx = this.mScrollX;
            sy = this.mScrollY;
        }
        boolean hasNoCache = (cache == null || hasDisplayList) ? true : DBG;
        boolean offsetForScroll = (cache != null || hasDisplayList || layerType == VIEW_STATE_SELECTED) ? DBG : true;
        int restoreTo = NO_ID;
        if (!(usingRenderNodeProperties && transformToApply == null)) {
            restoreTo = canvas.save();
        }
        if (offsetForScroll) {
            canvas.translate((float) (this.mLeft - sx), (float) (this.mTop - sy));
        } else {
            if (!usingRenderNodeProperties) {
                canvas.translate((float) this.mLeft, (float) this.mTop);
            }
            if (scalingRequired) {
                if (usingRenderNodeProperties) {
                    restoreTo = canvas.save();
                }
                float scale = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL / this.mAttachInfo.mApplicationScale;
                canvas.scale(scale, scale);
            }
        }
        if (usingRenderNodeProperties) {
            alpha = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        } else {
            alpha = getAlpha() * getTransitionAlpha();
        }
        if (transformToApply != null || alpha < WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL || !hasIdentityMatrix() || (this.mPrivateFlags3 & VIEW_STATE_SELECTED) == VIEW_STATE_SELECTED) {
            if (!(transformToApply == null && childHasIdentityMatrix)) {
                int transX = VISIBLE;
                int transY = VISIBLE;
                if (offsetForScroll) {
                    transX = -sx;
                    transY = -sy;
                }
                if (transformToApply != null) {
                    if (concatMatrix) {
                        if (usingRenderNodeProperties) {
                            renderNode.setAnimationMatrix(transformToApply.getMatrix());
                        } else {
                            canvas.translate((float) (-transX), (float) (-transY));
                            canvas.concat(transformToApply.getMatrix());
                            canvas.translate((float) transX, (float) transY);
                        }
                        parent.mGroupFlags |= VIEW_STATE_DRAG_CAN_ACCEPT;
                    }
                    float transformAlpha = transformToApply.getAlpha();
                    if (transformAlpha < WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL) {
                        alpha *= transformAlpha;
                        parent.mGroupFlags |= VIEW_STATE_DRAG_CAN_ACCEPT;
                    }
                }
                if (!(childHasIdentityMatrix || usingRenderNodeProperties)) {
                    canvas.translate((float) (-transX), (float) (-transY));
                    canvas.concat(getMatrix());
                    canvas.translate((float) transX, (float) transY);
                }
            }
            if (alpha < WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL || (this.mPrivateFlags3 & VIEW_STATE_SELECTED) == VIEW_STATE_SELECTED) {
                if (alpha < WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL) {
                    this.mPrivateFlags3 |= VIEW_STATE_SELECTED;
                } else {
                    this.mPrivateFlags3 &= -3;
                }
                parent.mGroupFlags |= VIEW_STATE_DRAG_CAN_ACCEPT;
                if (hasNoCache) {
                    int multipliedAlpha = (int) (255.0f * alpha);
                    if (onSetAlpha(multipliedAlpha)) {
                        this.mPrivateFlags |= STATUS_BAR_DISABLE_NOTIFICATION_ALERTS;
                    } else {
                        int layerFlags = VIEW_STATE_FOCUSED;
                        if (!((flags & VIEW_STATE_WINDOW_FOCUSED) == 0 && layerType == 0)) {
                            layerFlags = VIEW_STATE_FOCUSED | VIEW_STATE_PRESSED;
                        }
                        if (usingRenderNodeProperties) {
                            renderNode.setAlpha((getAlpha() * alpha) * getTransitionAlpha());
                        } else if (layerType == 0) {
                            if (hasDisplayList) {
                                scrollX = VISIBLE;
                            } else {
                                scrollX = sx;
                            }
                            if (hasDisplayList) {
                                scrollY = VISIBLE;
                            } else {
                                scrollY = sy;
                            }
                            canvas.saveLayerAlpha((float) scrollX, (float) scrollY, (float) ((this.mRight - this.mLeft) + scrollX), (float) ((this.mBottom - this.mTop) + scrollY), multipliedAlpha, layerFlags);
                        }
                    }
                }
            }
        } else if ((this.mPrivateFlags & STATUS_BAR_DISABLE_NOTIFICATION_ALERTS) == STATUS_BAR_DISABLE_NOTIFICATION_ALERTS) {
            onSetAlpha(EditorInfo.IME_MASK_ACTION);
            this.mPrivateFlags &= -262145;
        }
        if (!usingRenderNodeProperties) {
            if ((flags & VIEW_STATE_WINDOW_FOCUSED) == VIEW_STATE_WINDOW_FOCUSED && cache == null) {
                if (offsetForScroll) {
                    canvas.clipRect(sx, sy, (this.mRight - this.mLeft) + sx, (this.mBottom - this.mTop) + sy);
                } else if (!scalingRequired || cache == null) {
                    canvas.clipRect((int) VISIBLE, (int) VISIBLE, this.mRight - this.mLeft, this.mBottom - this.mTop);
                } else {
                    canvas.clipRect((int) VISIBLE, (int) VISIBLE, cache.getWidth(), cache.getHeight());
                }
            }
            if (this.mClipBounds != null) {
                canvas.clipRect(this.mClipBounds);
            }
        }
        if (!usingRenderNodeProperties && hasDisplayList) {
            renderNode = getDisplayList();
            if (!renderNode.isValid()) {
                renderNode = null;
                hasDisplayList = DBG;
            }
        }
        int restoreAlpha;
        if (hasNoCache) {
            boolean layerRendered = DBG;
            if (layerType == VIEW_STATE_SELECTED && !usingRenderNodeProperties) {
                HardwareLayer layer = getHardwareLayer();
                if (layer == null || !layer.isValid()) {
                    if (hasDisplayList) {
                        scrollX = VISIBLE;
                    } else {
                        scrollX = sx;
                    }
                    if (hasDisplayList) {
                        scrollY = VISIBLE;
                    } else {
                        scrollY = sy;
                    }
                    canvas.saveLayer((float) scrollX, (float) scrollY, (float) ((this.mRight + scrollX) - this.mLeft), (float) ((this.mBottom + scrollY) - this.mTop), this.mLayerPaint, PFLAG2_IMPORTANT_FOR_ACCESSIBILITY_SHIFT);
                } else {
                    restoreAlpha = this.mLayerPaint.getAlpha();
                    this.mLayerPaint.setAlpha((int) (255.0f * alpha));
                    ((HardwareCanvas) canvas).drawHardwareLayer(layer, 0.0f, 0.0f, this.mLayerPaint);
                    this.mLayerPaint.setAlpha(restoreAlpha);
                    layerRendered = true;
                }
            }
            if (!layerRendered) {
                if (hasDisplayList) {
                    this.mPrivateFlags &= -6291457;
                    ((HardwareCanvas) canvas).drawRenderNode(renderNode, null, flags);
                } else if ((this.mPrivateFlags & WILL_NOT_DRAW) == WILL_NOT_DRAW) {
                    this.mPrivateFlags &= -6291457;
                    dispatchDraw(canvas);
                } else {
                    draw(canvas);
                }
            }
        } else if (cache != null) {
            Paint cachePaint;
            this.mPrivateFlags &= -6291457;
            restoreAlpha = VISIBLE;
            if (layerType == 0) {
                cachePaint = parent.mCachePaint;
                if (cachePaint == null) {
                    cachePaint = new Paint();
                    cachePaint.setDither(DBG);
                    parent.mCachePaint = cachePaint;
                }
            } else {
                cachePaint = this.mLayerPaint;
                restoreAlpha = this.mLayerPaint.getAlpha();
            }
            cachePaint.setAlpha((int) (255.0f * alpha));
            canvas.drawBitmap(cache, 0.0f, 0.0f, cachePaint);
            cachePaint.setAlpha(restoreAlpha);
        }
        if (restoreTo >= 0) {
            canvas.restoreToCount(restoreTo);
        }
        if (!(a == null || more)) {
            if (!(hardwareAccelerated || a.getFillAfter())) {
                onSetAlpha(EditorInfo.IME_MASK_ACTION);
            }
            parent.finishAnimatingView(this, a);
        }
        if (more && hardwareAccelerated && a.hasAlpha() && (this.mPrivateFlags & STATUS_BAR_DISABLE_NOTIFICATION_ALERTS) == STATUS_BAR_DISABLE_NOTIFICATION_ALERTS) {
            invalidate(true);
        }
        this.mRecreateDisplayList = DBG;
        return more;
    }

    public void draw(Canvas canvas) {
        int privateFlags = this.mPrivateFlags;
        boolean dirtyOpaque = ((PFLAG_DIRTY_MASK & privateFlags) != STATUS_BAR_DISABLE_BACK || (this.mAttachInfo != null && this.mAttachInfo.mIgnoreDirtyState)) ? DBG : true;
        this.mPrivateFlags = (-6291457 & privateFlags) | VIEW_STATE_ACTIVATED;
        if (!dirtyOpaque) {
            drawBackground(canvas);
        }
        int viewFlags = this.mViewFlags;
        boolean horizontalEdges = (viewFlags & SYSTEM_UI_FLAG_IMMERSIVE_STICKY) != 0 ? true : DBG;
        boolean verticalEdges = (viewFlags & PFLAG_LAYOUT_REQUIRED) != 0 ? true : DBG;
        if (verticalEdges || horizontalEdges) {
            boolean drawTop = DBG;
            boolean drawBottom = DBG;
            boolean drawLeft = DBG;
            boolean drawRight = DBG;
            float topFadeStrength = 0.0f;
            float bottomFadeStrength = 0.0f;
            float leftFadeStrength = 0.0f;
            float rightFadeStrength = 0.0f;
            int paddingLeft = this.mPaddingLeft;
            boolean offsetRequired = isPaddingOffsetRequired();
            if (offsetRequired) {
                paddingLeft += getLeftPaddingOffset();
            }
            int left = this.mScrollX + paddingLeft;
            int right = (((this.mRight + left) - this.mLeft) - this.mPaddingRight) - paddingLeft;
            int top = this.mScrollY + getFadeTop(offsetRequired);
            int bottom = top + getFadeHeight(offsetRequired);
            if (offsetRequired) {
                right += getRightPaddingOffset();
                bottom += getBottomPaddingOffset();
            }
            ScrollabilityCache scrollabilityCache = this.mScrollCache;
            float fadeHeight = (float) scrollabilityCache.fadingEdgeLength;
            int length = (int) fadeHeight;
            if (verticalEdges && top + length > bottom - length) {
                length = (bottom - top) / VIEW_STATE_SELECTED;
            }
            if (horizontalEdges && left + length > right - length) {
                length = (right - left) / VIEW_STATE_SELECTED;
            }
            if (verticalEdges) {
                topFadeStrength = Math.max(0.0f, Math.min(WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL, getTopFadingEdgeStrength()));
                drawTop = topFadeStrength * fadeHeight > WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL ? true : DBG;
                bottomFadeStrength = Math.max(0.0f, Math.min(WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL, getBottomFadingEdgeStrength()));
                drawBottom = bottomFadeStrength * fadeHeight > WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL ? true : DBG;
            }
            if (horizontalEdges) {
                leftFadeStrength = Math.max(0.0f, Math.min(WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL, getLeftFadingEdgeStrength()));
                drawLeft = leftFadeStrength * fadeHeight > WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL ? true : DBG;
                rightFadeStrength = Math.max(0.0f, Math.min(WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL, getRightFadingEdgeStrength()));
                drawRight = rightFadeStrength * fadeHeight > WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL ? true : DBG;
            }
            int saveCount = canvas.getSaveCount();
            int solidColor = getSolidColor();
            if (solidColor == 0) {
                if (drawTop) {
                    canvas.saveLayer((float) left, (float) top, (float) right, (float) (top + length), null, VIEW_STATE_FOCUSED);
                }
                if (drawBottom) {
                    canvas.saveLayer((float) left, (float) (bottom - length), (float) right, (float) bottom, null, VIEW_STATE_FOCUSED);
                }
                if (drawLeft) {
                    canvas.saveLayer((float) left, (float) top, (float) (left + length), (float) bottom, null, VIEW_STATE_FOCUSED);
                }
                if (drawRight) {
                    canvas.saveLayer((float) (right - length), (float) top, (float) right, (float) bottom, null, VIEW_STATE_FOCUSED);
                }
            } else {
                scrollabilityCache.setFadeColor(solidColor);
            }
            if (!dirtyOpaque) {
                onDraw(canvas);
            }
            dispatchDraw(canvas);
            Paint p = scrollabilityCache.paint;
            Matrix matrix = scrollabilityCache.matrix;
            Shader fade = scrollabilityCache.shader;
            if (drawTop) {
                matrix.setScale(WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL, fadeHeight * topFadeStrength);
                matrix.postTranslate((float) left, (float) top);
                fade.setLocalMatrix(matrix);
                p.setShader(fade);
                canvas.drawRect((float) left, (float) top, (float) right, (float) (top + length), p);
            }
            if (drawBottom) {
                matrix.setScale(WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL, fadeHeight * bottomFadeStrength);
                matrix.postRotate(180.0f);
                matrix.postTranslate((float) left, (float) bottom);
                fade.setLocalMatrix(matrix);
                p.setShader(fade);
                canvas.drawRect((float) left, (float) (bottom - length), (float) right, (float) bottom, p);
            }
            if (drawLeft) {
                matrix.setScale(WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL, fadeHeight * leftFadeStrength);
                matrix.postRotate(-90.0f);
                matrix.postTranslate((float) left, (float) top);
                fade.setLocalMatrix(matrix);
                p.setShader(fade);
                canvas.drawRect((float) left, (float) top, (float) (left + length), (float) bottom, p);
            }
            if (drawRight) {
                matrix.setScale(WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL, fadeHeight * rightFadeStrength);
                matrix.postRotate(90.0f);
                matrix.postTranslate((float) right, (float) top);
                fade.setLocalMatrix(matrix);
                p.setShader(fade);
                canvas.drawRect((float) (right - length), (float) top, (float) right, (float) bottom, p);
            }
            canvas.restoreToCount(saveCount);
            onDrawScrollBars(canvas);
            if (this.mOverlay != null && !this.mOverlay.isEmpty()) {
                this.mOverlay.getOverlayView().dispatchDraw(canvas);
                return;
            }
            return;
        }
        if (!dirtyOpaque) {
            onDraw(canvas);
        }
        dispatchDraw(canvas);
        onDrawScrollBars(canvas);
        if (this.mOverlay != null && !this.mOverlay.isEmpty()) {
            this.mOverlay.getOverlayView().dispatchDraw(canvas);
        }
    }

    private void drawBackground(Canvas canvas) {
        Drawable background = this.mBackground;
        if (background != null) {
            if (this.mBackgroundSizeChanged) {
                background.setBounds(VISIBLE, VISIBLE, this.mRight - this.mLeft, this.mBottom - this.mTop);
                this.mBackgroundSizeChanged = DBG;
                rebuildOutline();
            }
            if (!(!canvas.isHardwareAccelerated() || this.mAttachInfo == null || this.mAttachInfo.mHardwareRenderer == null)) {
                this.mBackgroundRenderNode = getDrawableRenderNode(background, this.mBackgroundRenderNode);
                RenderNode renderNode = this.mBackgroundRenderNode;
                if (renderNode != null && renderNode.isValid()) {
                    setBackgroundRenderNodeProperties(renderNode);
                    ((HardwareCanvas) canvas).drawRenderNode(renderNode);
                    return;
                }
            }
            int scrollX = this.mScrollX;
            int scrollY = this.mScrollY;
            if ((scrollX | scrollY) == 0) {
                background.draw(canvas);
                return;
            }
            canvas.translate((float) scrollX, (float) scrollY);
            background.draw(canvas);
            canvas.translate((float) (-scrollX), (float) (-scrollY));
        }
    }

    private void setBackgroundRenderNodeProperties(RenderNode renderNode) {
        renderNode.setTranslationX((float) this.mScrollX);
        renderNode.setTranslationY((float) this.mScrollY);
    }

    private RenderNode getDrawableRenderNode(Drawable drawable, RenderNode renderNode) {
        if (renderNode == null) {
            renderNode = RenderNode.create(drawable.getClass().getName(), this);
        }
        Rect bounds = drawable.getBounds();
        HardwareCanvas canvas = renderNode.start(bounds.width(), bounds.height());
        canvas.translate((float) (-bounds.left), (float) (-bounds.top));
        try {
            drawable.draw(canvas);
            renderNode.setLeftTopRightBottom(bounds.left, bounds.top, bounds.right, bounds.bottom);
            renderNode.setProjectBackwards(drawable.isProjected());
            renderNode.setProjectionReceiver(true);
            renderNode.setClipToBounds(DBG);
            return renderNode;
        } finally {
            renderNode.end(canvas);
        }
    }

    public ViewOverlay getOverlay() {
        if (this.mOverlay == null) {
            this.mOverlay = new ViewOverlay(this.mContext, this);
        }
        return this.mOverlay;
    }

    @ExportedProperty(category = "drawing")
    public int getSolidColor() {
        return VISIBLE;
    }

    private static String printFlags(int flags) {
        String output = ProxyInfo.LOCAL_EXCL_LIST;
        int numFlags = VISIBLE;
        if ((flags & VIEW_STATE_WINDOW_FOCUSED) == VIEW_STATE_WINDOW_FOCUSED) {
            output = output + "TAKES_FOCUS";
            numFlags = VISIBLE + VIEW_STATE_WINDOW_FOCUSED;
        }
        switch (flags & VISIBILITY_MASK) {
            case VIEW_STATE_FOCUSED /*4*/:
                if (numFlags > 0) {
                    output = output + " ";
                }
                return output + "INVISIBLE";
            case VIEW_STATE_ENABLED /*8*/:
                if (numFlags > 0) {
                    output = output + " ";
                }
                return output + "GONE";
            default:
                return output;
        }
    }

    private static String printPrivateFlags(int privateFlags) {
        String output = ProxyInfo.LOCAL_EXCL_LIST;
        int numFlags = VISIBLE;
        if ((privateFlags & VIEW_STATE_WINDOW_FOCUSED) == VIEW_STATE_WINDOW_FOCUSED) {
            output = output + "WANTS_FOCUS";
            numFlags = VISIBLE + VIEW_STATE_WINDOW_FOCUSED;
        }
        if ((privateFlags & VIEW_STATE_SELECTED) == VIEW_STATE_SELECTED) {
            if (numFlags > 0) {
                output = output + " ";
            }
            output = output + "FOCUSED";
            numFlags += VIEW_STATE_WINDOW_FOCUSED;
        }
        if ((privateFlags & VIEW_STATE_FOCUSED) == VIEW_STATE_FOCUSED) {
            if (numFlags > 0) {
                output = output + " ";
            }
            output = output + "SELECTED";
            numFlags += VIEW_STATE_WINDOW_FOCUSED;
        }
        if ((privateFlags & VIEW_STATE_ENABLED) == VIEW_STATE_ENABLED) {
            if (numFlags > 0) {
                output = output + " ";
            }
            output = output + "IS_ROOT_NAMESPACE";
            numFlags += VIEW_STATE_WINDOW_FOCUSED;
        }
        if ((privateFlags & VIEW_STATE_PRESSED) == VIEW_STATE_PRESSED) {
            if (numFlags > 0) {
                output = output + " ";
            }
            output = output + "HAS_BOUNDS";
            numFlags += VIEW_STATE_WINDOW_FOCUSED;
        }
        if ((privateFlags & VIEW_STATE_ACTIVATED) != VIEW_STATE_ACTIVATED) {
            return output;
        }
        if (numFlags > 0) {
            output = output + " ";
        }
        return output + "DRAWN";
    }

    public boolean isLayoutRequested() {
        return (this.mPrivateFlags & SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == SYSTEM_UI_FLAG_IMMERSIVE_STICKY ? true : DBG;
    }

    public static boolean isLayoutModeOptical(Object o) {
        return ((o instanceof ViewGroup) && ((ViewGroup) o).isLayoutModeOptical()) ? true : DBG;
    }

    private boolean setOpticalFrame(int left, int top, int right, int bottom) {
        Insets parentInsets = this.mParent instanceof View ? ((View) this.mParent).getOpticalInsets() : Insets.NONE;
        Insets childInsets = getOpticalInsets();
        return setFrame((parentInsets.left + left) - childInsets.left, (parentInsets.top + top) - childInsets.top, (parentInsets.left + right) + childInsets.right, (parentInsets.top + bottom) + childInsets.bottom);
    }

    public void layout(int l, int t, int r, int b) {
        if ((this.mPrivateFlags3 & VIEW_STATE_ENABLED) != 0) {
            onMeasure(this.mOldWidthMeasureSpec, this.mOldHeightMeasureSpec);
            this.mPrivateFlags3 &= -9;
        }
        int oldL = this.mLeft;
        int oldT = this.mTop;
        int oldB = this.mBottom;
        int oldR = this.mRight;
        boolean changed = isLayoutModeOptical(this.mParent) ? setOpticalFrame(l, t, r, b) : setFrame(l, t, r, b);
        if (changed || (this.mPrivateFlags & PFLAG_LAYOUT_REQUIRED) == PFLAG_LAYOUT_REQUIRED) {
            onLayout(changed, l, t, r, b);
            this.mPrivateFlags &= -8193;
            ListenerInfo li = this.mListenerInfo;
            if (!(li == null || li.mOnLayoutChangeListeners == null)) {
                ArrayList<OnLayoutChangeListener> listenersCopy = (ArrayList) li.mOnLayoutChangeListeners.clone();
                int numListeners = listenersCopy.size();
                for (int i = VISIBLE; i < numListeners; i += VIEW_STATE_WINDOW_FOCUSED) {
                    ((OnLayoutChangeListener) listenersCopy.get(i)).onLayoutChange(this, l, t, r, b, oldL, oldT, oldR, oldB);
                }
            }
        }
        this.mPrivateFlags &= -4097;
        this.mPrivateFlags3 |= VIEW_STATE_FOCUSED;
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    }

    protected boolean setFrame(int left, int top, int right, int bottom) {
        boolean changed = DBG;
        if (!(this.mLeft == left && this.mRight == right && this.mTop == top && this.mBottom == bottom)) {
            changed = true;
            int drawn = this.mPrivateFlags & VIEW_STATE_ACTIVATED;
            int oldWidth = this.mRight - this.mLeft;
            int oldHeight = this.mBottom - this.mTop;
            int newWidth = right - left;
            int newHeight = bottom - top;
            boolean sizeChanged = (newWidth == oldWidth && newHeight == oldHeight) ? DBG : true;
            invalidate(sizeChanged);
            this.mLeft = left;
            this.mTop = top;
            this.mRight = right;
            this.mBottom = bottom;
            this.mRenderNode.setLeftTopRightBottom(this.mLeft, this.mTop, this.mRight, this.mBottom);
            this.mPrivateFlags |= VIEW_STATE_PRESSED;
            if (sizeChanged) {
                sizeChange(newWidth, newHeight, oldWidth, oldHeight);
            }
            if ((this.mViewFlags & VISIBILITY_MASK) == 0 || this.mGhostView != null) {
                this.mPrivateFlags |= VIEW_STATE_ACTIVATED;
                invalidate(sizeChanged);
                invalidateParentCaches();
            }
            this.mPrivateFlags |= drawn;
            this.mBackgroundSizeChanged = true;
            notifySubtreeAccessibilityStateChangedIfNeeded();
        }
        return changed;
    }

    public void setLeftTopRightBottom(int left, int top, int right, int bottom) {
        setFrame(left, top, right, bottom);
    }

    private void sizeChange(int newWidth, int newHeight, int oldWidth, int oldHeight) {
        onSizeChanged(newWidth, newHeight, oldWidth, oldHeight);
        if (this.mOverlay != null) {
            this.mOverlay.getOverlayView().setRight(newWidth);
            this.mOverlay.getOverlayView().setBottom(newHeight);
        }
        rebuildOutline();
    }

    protected void onFinishInflate() {
    }

    public Resources getResources() {
        return this.mResources;
    }

    public void invalidateDrawable(Drawable drawable) {
        if (verifyDrawable(drawable)) {
            Rect dirty = drawable.getDirtyBounds();
            int scrollX = this.mScrollX;
            int scrollY = this.mScrollY;
            invalidate(dirty.left + scrollX, dirty.top + scrollY, dirty.right + scrollX, dirty.bottom + scrollY);
            rebuildOutline();
        }
    }

    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        if (verifyDrawable(who) && what != null) {
            long delay = when - SystemClock.uptimeMillis();
            if (this.mAttachInfo != null) {
                this.mAttachInfo.mViewRootImpl.mChoreographer.postCallbackDelayed(VIEW_STATE_WINDOW_FOCUSED, what, who, Choreographer.subtractFrameDelay(delay));
                return;
            }
            ViewRootImpl.getRunQueue().postDelayed(what, delay);
        }
    }

    public void unscheduleDrawable(Drawable who, Runnable what) {
        if (verifyDrawable(who) && what != null) {
            if (this.mAttachInfo != null) {
                this.mAttachInfo.mViewRootImpl.mChoreographer.removeCallbacks(VIEW_STATE_WINDOW_FOCUSED, what, who);
            }
            ViewRootImpl.getRunQueue().removeCallbacks(what);
        }
    }

    public void unscheduleDrawable(Drawable who) {
        if (this.mAttachInfo != null && who != null) {
            this.mAttachInfo.mViewRootImpl.mChoreographer.removeCallbacks(VIEW_STATE_WINDOW_FOCUSED, null, who);
        }
    }

    protected void resolveDrawables() {
        if (isLayoutDirectionResolved() || getRawLayoutDirection() != VIEW_STATE_SELECTED) {
            int layoutDirection = isLayoutDirectionResolved() ? getLayoutDirection() : getRawLayoutDirection();
            if (this.mBackground != null) {
                this.mBackground.setLayoutDirection(layoutDirection);
            }
            this.mPrivateFlags2 |= STATUS_BAR_TRANSLUCENT;
            onResolveDrawables(layoutDirection);
        }
    }

    boolean areDrawablesResolved() {
        return (this.mPrivateFlags2 & STATUS_BAR_TRANSLUCENT) == STATUS_BAR_TRANSLUCENT ? true : DBG;
    }

    public void onResolveDrawables(int layoutDirection) {
    }

    protected void resetResolvedDrawables() {
        resetResolvedDrawablesInternal();
    }

    void resetResolvedDrawablesInternal() {
        this.mPrivateFlags2 &= -1073741825;
    }

    protected boolean verifyDrawable(Drawable who) {
        return who == this.mBackground ? true : DBG;
    }

    protected void drawableStateChanged() {
        Drawable d = this.mBackground;
        if (d != null && d.isStateful()) {
            d.setState(getDrawableState());
        }
        if (this.mStateListAnimator != null) {
            this.mStateListAnimator.setState(getDrawableState());
        }
    }

    public void drawableHotspotChanged(float x, float y) {
        if (this.mBackground != null) {
            this.mBackground.setHotspot(x, y);
        }
        dispatchDrawableHotspotChanged(x, y);
    }

    public void dispatchDrawableHotspotChanged(float x, float y) {
    }

    public void refreshDrawableState() {
        this.mPrivateFlags |= SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        drawableStateChanged();
        ViewParent parent = this.mParent;
        if (parent != null) {
            parent.childDrawableStateChanged(this);
        }
    }

    public final int[] getDrawableState() {
        if (this.mDrawableState != null && (this.mPrivateFlags & SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) == 0) {
            return this.mDrawableState;
        }
        this.mDrawableState = onCreateDrawableState(VISIBLE);
        this.mPrivateFlags &= -1025;
        return this.mDrawableState;
    }

    protected int[] onCreateDrawableState(int extraSpace) {
        if ((this.mViewFlags & STATUS_BAR_DISABLE_BACK) == STATUS_BAR_DISABLE_BACK && (this.mParent instanceof View)) {
            return ((View) this.mParent).onCreateDrawableState(extraSpace);
        }
        int privateFlags = this.mPrivateFlags;
        int viewStateIndex = VISIBLE;
        if ((privateFlags & RECENT_APPS_VISIBLE) != 0) {
            viewStateIndex = VISIBLE | VIEW_STATE_PRESSED;
        }
        if ((this.mViewFlags & VIEW_STATE_ACTIVATED) == 0) {
            viewStateIndex |= VIEW_STATE_ENABLED;
        }
        if (isFocused()) {
            viewStateIndex |= VIEW_STATE_FOCUSED;
        }
        if ((privateFlags & VIEW_STATE_FOCUSED) != 0) {
            viewStateIndex |= VIEW_STATE_SELECTED;
        }
        if (hasWindowFocus()) {
            viewStateIndex |= VIEW_STATE_WINDOW_FOCUSED;
        }
        if ((STATUS_BAR_TRANSLUCENT & privateFlags) != 0) {
            viewStateIndex |= VIEW_STATE_ACTIVATED;
        }
        if (this.mAttachInfo != null && this.mAttachInfo.mHardwareAccelerationRequested && HardwareRenderer.isAvailable()) {
            viewStateIndex |= VIEW_STATE_ACCELERATED;
        }
        if ((STATUS_BAR_UNHIDE & privateFlags) != 0) {
            viewStateIndex |= WILL_NOT_DRAW;
        }
        int privateFlags2 = this.mPrivateFlags2;
        if ((privateFlags2 & VIEW_STATE_WINDOW_FOCUSED) != 0) {
            viewStateIndex |= VIEW_STATE_DRAG_CAN_ACCEPT;
        }
        if ((privateFlags2 & VIEW_STATE_SELECTED) != 0) {
            viewStateIndex |= VIEW_STATE_DRAG_HOVERED;
        }
        int[] drawableState = VIEW_STATE_SETS[viewStateIndex];
        if (extraSpace == 0) {
            return drawableState;
        }
        int[] fullState;
        if (drawableState != null) {
            fullState = new int[(drawableState.length + extraSpace)];
            System.arraycopy(drawableState, VISIBLE, fullState, VISIBLE, drawableState.length);
        } else {
            fullState = new int[extraSpace];
        }
        return fullState;
    }

    protected static int[] mergeDrawableStates(int[] baseState, int[] additionalState) {
        int i = baseState.length + NO_ID;
        while (i >= 0 && baseState[i] == 0) {
            i += NO_ID;
        }
        System.arraycopy(additionalState, VISIBLE, baseState, i + VIEW_STATE_WINDOW_FOCUSED, additionalState.length);
        return baseState;
    }

    public void jumpDrawablesToCurrentState() {
        if (this.mBackground != null) {
            this.mBackground.jumpToCurrentState();
        }
        if (this.mStateListAnimator != null) {
            this.mStateListAnimator.jumpToCurrentState();
        }
    }

    @RemotableViewMethod
    public void setBackgroundColor(int color) {
        if (this.mBackground instanceof ColorDrawable) {
            ((ColorDrawable) this.mBackground.mutate()).setColor(color);
            computeOpaqueFlags();
            this.mBackgroundResource = VISIBLE;
            return;
        }
        setBackground(new ColorDrawable(color));
    }

    @RemotableViewMethod
    public void setBackgroundResource(int resid) {
        if (resid == 0 || resid != this.mBackgroundResource) {
            Drawable d = null;
            if (resid != 0) {
                d = this.mContext.getDrawable(resid);
            }
            setBackground(d);
            this.mBackgroundResource = resid;
        }
    }

    public void setBackground(Drawable background) {
        setBackgroundDrawable(background);
    }

    @Deprecated
    public void setBackgroundDrawable(Drawable background) {
        computeOpaqueFlags();
        if (background != this.mBackground) {
            boolean requestLayout = DBG;
            this.mBackgroundResource = VISIBLE;
            if (this.mBackground != null) {
                this.mBackground.setCallback(null);
                unscheduleDrawable(this.mBackground);
            }
            if (background != null) {
                Rect padding = (Rect) sThreadLocal.get();
                if (padding == null) {
                    padding = new Rect();
                    sThreadLocal.set(padding);
                }
                resetResolvedDrawablesInternal();
                background.setLayoutDirection(getLayoutDirection());
                if (background.getPadding(padding)) {
                    resetResolvedPaddingInternal();
                    switch (background.getLayoutDirection()) {
                        case VIEW_STATE_WINDOW_FOCUSED /*1*/:
                            this.mUserPaddingLeftInitial = padding.right;
                            this.mUserPaddingRightInitial = padding.left;
                            internalSetPadding(padding.right, padding.top, padding.left, padding.bottom);
                            break;
                        default:
                            this.mUserPaddingLeftInitial = padding.left;
                            this.mUserPaddingRightInitial = padding.right;
                            internalSetPadding(padding.left, padding.top, padding.right, padding.bottom);
                            break;
                    }
                    this.mLeftPaddingDefined = DBG;
                    this.mRightPaddingDefined = DBG;
                }
                if (!(this.mBackground != null && this.mBackground.getMinimumHeight() == background.getMinimumHeight() && this.mBackground.getMinimumWidth() == background.getMinimumWidth())) {
                    requestLayout = true;
                }
                background.setCallback(this);
                if (background.isStateful()) {
                    background.setState(getDrawableState());
                }
                background.setVisible(getVisibility() == 0 ? true : DBG, DBG);
                this.mBackground = background;
                applyBackgroundTint();
                if ((this.mPrivateFlags & WILL_NOT_DRAW) != 0) {
                    this.mPrivateFlags &= -129;
                    this.mPrivateFlags |= VIEW_STATE_DRAG_CAN_ACCEPT;
                    requestLayout = true;
                }
            } else {
                this.mBackground = null;
                if ((this.mPrivateFlags & VIEW_STATE_DRAG_CAN_ACCEPT) != 0) {
                    this.mPrivateFlags &= -257;
                    this.mPrivateFlags |= WILL_NOT_DRAW;
                }
                requestLayout = true;
            }
            computeOpaqueFlags();
            if (requestLayout) {
                requestLayout();
            }
            this.mBackgroundSizeChanged = true;
            invalidate(true);
        }
    }

    public Drawable getBackground() {
        return this.mBackground;
    }

    public void setBackgroundTintList(ColorStateList tint) {
        if (this.mBackgroundTint == null) {
            this.mBackgroundTint = new TintInfo();
        }
        this.mBackgroundTint.mTintList = tint;
        this.mBackgroundTint.mHasTintList = true;
        applyBackgroundTint();
    }

    public ColorStateList getBackgroundTintList() {
        return this.mBackgroundTint != null ? this.mBackgroundTint.mTintList : null;
    }

    public void setBackgroundTintMode(Mode tintMode) {
        if (this.mBackgroundTint == null) {
            this.mBackgroundTint = new TintInfo();
        }
        this.mBackgroundTint.mTintMode = tintMode;
        this.mBackgroundTint.mHasTintMode = true;
        applyBackgroundTint();
    }

    public Mode getBackgroundTintMode() {
        return this.mBackgroundTint != null ? this.mBackgroundTint.mTintMode : null;
    }

    private void applyBackgroundTint() {
        if (this.mBackground != null && this.mBackgroundTint != null) {
            TintInfo tintInfo = this.mBackgroundTint;
            if (tintInfo.mHasTintList || tintInfo.mHasTintMode) {
                this.mBackground = this.mBackground.mutate();
                if (tintInfo.mHasTintList) {
                    this.mBackground.setTintList(tintInfo.mTintList);
                }
                if (tintInfo.mHasTintMode) {
                    this.mBackground.setTintMode(tintInfo.mTintMode);
                }
                if (this.mBackground.isStateful()) {
                    this.mBackground.setState(getDrawableState());
                }
            }
        }
    }

    public void setPadding(int left, int top, int right, int bottom) {
        resetResolvedPaddingInternal();
        this.mUserPaddingStart = UNDEFINED_PADDING;
        this.mUserPaddingEnd = UNDEFINED_PADDING;
        this.mUserPaddingLeftInitial = left;
        this.mUserPaddingRightInitial = right;
        this.mLeftPaddingDefined = true;
        this.mRightPaddingDefined = true;
        internalSetPadding(left, top, right, bottom);
    }

    protected void internalSetPadding(int left, int top, int right, int bottom) {
        int i = VISIBLE;
        this.mUserPaddingLeft = left;
        this.mUserPaddingRight = right;
        this.mUserPaddingBottom = bottom;
        int viewFlags = this.mViewFlags;
        boolean changed = DBG;
        if ((viewFlags & SCROLLBARS_MASK) != 0) {
            if ((viewFlags & VIEW_STATE_DRAG_HOVERED) != 0) {
                int offset = (viewFlags & STATUS_BAR_DISABLE_RECENT) == 0 ? VISIBLE : getVerticalScrollbarWidth();
                switch (this.mVerticalScrollbarPosition) {
                    case VISIBLE /*0*/:
                        if (!isLayoutRtl()) {
                            right += offset;
                            break;
                        } else {
                            left += offset;
                            break;
                        }
                    case VIEW_STATE_WINDOW_FOCUSED /*1*/:
                        left += offset;
                        break;
                    case VIEW_STATE_SELECTED /*2*/:
                        right += offset;
                        break;
                }
            }
            if ((viewFlags & VIEW_STATE_DRAG_CAN_ACCEPT) != 0) {
                if ((viewFlags & STATUS_BAR_DISABLE_RECENT) != 0) {
                    i = getHorizontalScrollbarHeight();
                }
                bottom += i;
            }
        }
        if (this.mPaddingLeft != left) {
            changed = true;
            this.mPaddingLeft = left;
        }
        if (this.mPaddingTop != top) {
            changed = true;
            this.mPaddingTop = top;
        }
        if (this.mPaddingRight != right) {
            changed = true;
            this.mPaddingRight = right;
        }
        if (this.mPaddingBottom != bottom) {
            changed = true;
            this.mPaddingBottom = bottom;
        }
        if (changed) {
            requestLayout();
            invalidateOutline();
        }
    }

    public void setPaddingRelative(int start, int top, int end, int bottom) {
        resetResolvedPaddingInternal();
        this.mUserPaddingStart = start;
        this.mUserPaddingEnd = end;
        this.mLeftPaddingDefined = true;
        this.mRightPaddingDefined = true;
        switch (getLayoutDirection()) {
            case VIEW_STATE_WINDOW_FOCUSED /*1*/:
                this.mUserPaddingLeftInitial = end;
                this.mUserPaddingRightInitial = start;
                internalSetPadding(end, top, start, bottom);
            default:
                this.mUserPaddingLeftInitial = start;
                this.mUserPaddingRightInitial = end;
                internalSetPadding(start, top, end, bottom);
        }
    }

    public int getPaddingTop() {
        return this.mPaddingTop;
    }

    public int getPaddingBottom() {
        return this.mPaddingBottom;
    }

    public int getPaddingLeft() {
        if (!isPaddingResolved()) {
            resolvePadding();
        }
        return this.mPaddingLeft;
    }

    public int getPaddingStart() {
        if (!isPaddingResolved()) {
            resolvePadding();
        }
        return getLayoutDirection() == VIEW_STATE_WINDOW_FOCUSED ? this.mPaddingRight : this.mPaddingLeft;
    }

    public int getPaddingRight() {
        if (!isPaddingResolved()) {
            resolvePadding();
        }
        return this.mPaddingRight;
    }

    public int getPaddingEnd() {
        if (!isPaddingResolved()) {
            resolvePadding();
        }
        return getLayoutDirection() == VIEW_STATE_WINDOW_FOCUSED ? this.mPaddingLeft : this.mPaddingRight;
    }

    public boolean isPaddingRelative() {
        return (this.mUserPaddingStart == UNDEFINED_PADDING && this.mUserPaddingEnd == UNDEFINED_PADDING) ? DBG : true;
    }

    Insets computeOpticalInsets() {
        return this.mBackground == null ? Insets.NONE : this.mBackground.getOpticalInsets();
    }

    public void resetPaddingToInitialValues() {
        if (isRtlCompatibilityMode()) {
            this.mPaddingLeft = this.mUserPaddingLeftInitial;
            this.mPaddingRight = this.mUserPaddingRightInitial;
        } else if (isLayoutRtl()) {
            this.mPaddingLeft = this.mUserPaddingEnd >= 0 ? this.mUserPaddingEnd : this.mUserPaddingLeftInitial;
            this.mPaddingRight = this.mUserPaddingStart >= 0 ? this.mUserPaddingStart : this.mUserPaddingRightInitial;
        } else {
            this.mPaddingLeft = this.mUserPaddingStart >= 0 ? this.mUserPaddingStart : this.mUserPaddingLeftInitial;
            this.mPaddingRight = this.mUserPaddingEnd >= 0 ? this.mUserPaddingEnd : this.mUserPaddingRightInitial;
        }
    }

    public Insets getOpticalInsets() {
        if (this.mLayoutInsets == null) {
            this.mLayoutInsets = computeOpticalInsets();
        }
        return this.mLayoutInsets;
    }

    public void setOpticalInsets(Insets insets) {
        this.mLayoutInsets = insets;
    }

    public void setSelected(boolean selected) {
        if (((this.mPrivateFlags & VIEW_STATE_FOCUSED) != 0 ? true : VISIBLE) != selected) {
            int i;
            int i2 = this.mPrivateFlags & -5;
            if (selected) {
                i = VIEW_STATE_FOCUSED;
            } else {
                i = VISIBLE;
            }
            this.mPrivateFlags = i | i2;
            if (!selected) {
                resetPressedState();
            }
            invalidate(true);
            refreshDrawableState();
            dispatchSetSelected(selected);
            if (selected) {
                sendAccessibilityEvent(VIEW_STATE_FOCUSED);
            } else {
                notifyViewAccessibilityStateChangedIfNeeded(VISIBLE);
            }
        }
    }

    protected void dispatchSetSelected(boolean selected) {
    }

    @ExportedProperty
    public boolean isSelected() {
        return (this.mPrivateFlags & VIEW_STATE_FOCUSED) != 0 ? true : DBG;
    }

    public void setActivated(boolean activated) {
        int i = STATUS_BAR_TRANSLUCENT;
        if (((this.mPrivateFlags & STATUS_BAR_TRANSLUCENT) != 0 ? true : VISIBLE) != activated) {
            int i2 = this.mPrivateFlags & -1073741825;
            if (!activated) {
                i = VISIBLE;
            }
            this.mPrivateFlags = i | i2;
            invalidate(true);
            refreshDrawableState();
            dispatchSetActivated(activated);
        }
    }

    protected void dispatchSetActivated(boolean activated) {
    }

    @ExportedProperty
    public boolean isActivated() {
        return (this.mPrivateFlags & STATUS_BAR_TRANSLUCENT) != 0 ? true : DBG;
    }

    public ViewTreeObserver getViewTreeObserver() {
        if (this.mAttachInfo != null) {
            return this.mAttachInfo.mTreeObserver;
        }
        if (this.mFloatingTreeObserver == null) {
            this.mFloatingTreeObserver = new ViewTreeObserver();
        }
        return this.mFloatingTreeObserver;
    }

    public View getRootView() {
        if (this.mAttachInfo != null) {
            View v = this.mAttachInfo.mRootView;
            if (v != null) {
                return v;
            }
        }
        View parent = this;
        while (parent.mParent != null && (parent.mParent instanceof View)) {
            parent = parent.mParent;
        }
        return parent;
    }

    public boolean toGlobalMotionEvent(MotionEvent ev) {
        AttachInfo info = this.mAttachInfo;
        if (info == null) {
            return DBG;
        }
        Matrix m = info.mTmpMatrix;
        m.set(Matrix.IDENTITY_MATRIX);
        transformMatrixToGlobal(m);
        ev.transform(m);
        return true;
    }

    public boolean toLocalMotionEvent(MotionEvent ev) {
        AttachInfo info = this.mAttachInfo;
        if (info == null) {
            return DBG;
        }
        Matrix m = info.mTmpMatrix;
        m.set(Matrix.IDENTITY_MATRIX);
        transformMatrixToLocal(m);
        ev.transform(m);
        return true;
    }

    public void transformMatrixToGlobal(Matrix m) {
        ViewParent parent = this.mParent;
        if (parent instanceof View) {
            View vp = (View) parent;
            vp.transformMatrixToGlobal(m);
            m.preTranslate((float) (-vp.mScrollX), (float) (-vp.mScrollY));
        } else if (parent instanceof ViewRootImpl) {
            ViewRootImpl vr = (ViewRootImpl) parent;
            vr.transformMatrixToGlobal(m);
            m.preTranslate(0.0f, (float) (-vr.mCurScrollY));
        }
        m.preTranslate((float) this.mLeft, (float) this.mTop);
        if (!hasIdentityMatrix()) {
            m.preConcat(getMatrix());
        }
    }

    public void transformMatrixToLocal(Matrix m) {
        ViewParent parent = this.mParent;
        if (parent instanceof View) {
            View vp = (View) parent;
            vp.transformMatrixToLocal(m);
            m.postTranslate((float) vp.mScrollX, (float) vp.mScrollY);
        } else if (parent instanceof ViewRootImpl) {
            ViewRootImpl vr = (ViewRootImpl) parent;
            vr.transformMatrixToLocal(m);
            m.postTranslate(0.0f, (float) vr.mCurScrollY);
        }
        m.postTranslate((float) (-this.mLeft), (float) (-this.mTop));
        if (!hasIdentityMatrix()) {
            m.postConcat(getInverseMatrix());
        }
    }

    @ExportedProperty(category = "layout", indexMapping = {@IntToString(from = 0, to = "x"), @IntToString(from = 1, to = "y")})
    public int[] getLocationOnScreen() {
        int[] location = new int[VIEW_STATE_SELECTED];
        getLocationOnScreen(location);
        return location;
    }

    public void getLocationOnScreen(int[] location) {
        getLocationInWindow(location);
        AttachInfo info = this.mAttachInfo;
        if (info != null) {
            location[VISIBLE] = location[VISIBLE] + info.mWindowLeft;
            location[VIEW_STATE_WINDOW_FOCUSED] = location[VIEW_STATE_WINDOW_FOCUSED] + info.mWindowTop;
        }
    }

    public void getLocationInWindow(int[] location) {
        if (location == null || location.length < VIEW_STATE_SELECTED) {
            throw new IllegalArgumentException("location must be an array of two integers");
        } else if (this.mAttachInfo == null) {
            location[VIEW_STATE_WINDOW_FOCUSED] = VISIBLE;
            location[VISIBLE] = VISIBLE;
        } else {
            float[] position = this.mAttachInfo.mTmpTransformLocation;
            position[VIEW_STATE_WINDOW_FOCUSED] = 0.0f;
            position[VISIBLE] = 0.0f;
            if (!hasIdentityMatrix()) {
                getMatrix().mapPoints(position);
            }
            position[VISIBLE] = position[VISIBLE] + ((float) this.mLeft);
            position[VIEW_STATE_WINDOW_FOCUSED] = position[VIEW_STATE_WINDOW_FOCUSED] + ((float) this.mTop);
            ViewParent viewParent = this.mParent;
            while (viewParent instanceof View) {
                View view = (View) viewParent;
                position[VISIBLE] = position[VISIBLE] - ((float) view.mScrollX);
                position[VIEW_STATE_WINDOW_FOCUSED] = position[VIEW_STATE_WINDOW_FOCUSED] - ((float) view.mScrollY);
                if (!view.hasIdentityMatrix()) {
                    view.getMatrix().mapPoints(position);
                }
                position[VISIBLE] = position[VISIBLE] + ((float) view.mLeft);
                position[VIEW_STATE_WINDOW_FOCUSED] = position[VIEW_STATE_WINDOW_FOCUSED] + ((float) view.mTop);
                viewParent = view.mParent;
            }
            if (viewParent instanceof ViewRootImpl) {
                position[VIEW_STATE_WINDOW_FOCUSED] = position[VIEW_STATE_WINDOW_FOCUSED] - ((float) ((ViewRootImpl) viewParent).mCurScrollY);
            }
            location[VISIBLE] = (int) (position[VISIBLE] + 0.5f);
            location[VIEW_STATE_WINDOW_FOCUSED] = (int) (position[VIEW_STATE_WINDOW_FOCUSED] + 0.5f);
        }
    }

    protected View findViewTraversal(int id) {
        return id == this.mID ? this : null;
    }

    protected View findViewWithTagTraversal(Object tag) {
        return (tag == null || !tag.equals(this.mTag)) ? null : this;
    }

    protected View findViewByPredicateTraversal(Predicate<View> predicate, View childToSkip) {
        return predicate.apply(this) ? this : null;
    }

    public final View findViewById(int id) {
        if (id < 0) {
            return null;
        }
        return findViewTraversal(id);
    }

    final View findViewByAccessibilityId(int accessibilityId) {
        if (accessibilityId < 0) {
            return null;
        }
        return findViewByAccessibilityIdTraversal(accessibilityId);
    }

    public View findViewByAccessibilityIdTraversal(int accessibilityId) {
        return getAccessibilityViewId() == accessibilityId ? this : null;
    }

    public final View findViewWithTag(Object tag) {
        if (tag == null) {
            return null;
        }
        return findViewWithTagTraversal(tag);
    }

    public final View findViewByPredicate(Predicate<View> predicate) {
        return findViewByPredicateTraversal(predicate, null);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final android.view.View findViewByPredicateInsideOut(android.view.View r5, com.android.internal.util.Predicate<android.view.View> r6) {
        /*
        r4 = this;
        r0 = 0;
    L_0x0001:
        r2 = r5.findViewByPredicateTraversal(r6, r0);
        if (r2 != 0) goto L_0x0009;
    L_0x0007:
        if (r5 != r4) goto L_0x000a;
    L_0x0009:
        return r2;
    L_0x000a:
        r1 = r5.getParent();
        if (r1 == 0) goto L_0x0014;
    L_0x0010:
        r3 = r1 instanceof android.view.View;
        if (r3 != 0) goto L_0x0016;
    L_0x0014:
        r2 = 0;
        goto L_0x0009;
    L_0x0016:
        r0 = r5;
        r5 = r1;
        r5 = (android.view.View) r5;
        goto L_0x0001;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.View.findViewByPredicateInsideOut(android.view.View, com.android.internal.util.Predicate):android.view.View");
    }

    public void setId(int id) {
        this.mID = id;
        if (this.mID == NO_ID && this.mLabelForId != NO_ID) {
            this.mID = generateViewId();
        }
    }

    public void setIsRootNamespace(boolean isRoot) {
        if (isRoot) {
            this.mPrivateFlags |= VIEW_STATE_ENABLED;
        } else {
            this.mPrivateFlags &= -9;
        }
    }

    public boolean isRootNamespace() {
        return (this.mPrivateFlags & VIEW_STATE_ENABLED) != 0 ? true : DBG;
    }

    @CapturedViewProperty
    public int getId() {
        return this.mID;
    }

    @ExportedProperty
    public Object getTag() {
        return this.mTag;
    }

    public void setTag(Object tag) {
        this.mTag = tag;
    }

    public Object getTag(int key) {
        if (this.mKeyedTags != null) {
            return this.mKeyedTags.get(key);
        }
        return null;
    }

    public void setTag(int key, Object tag) {
        if ((key >>> 24) < VIEW_STATE_SELECTED) {
            throw new IllegalArgumentException("The key must be an application-specific resource id.");
        }
        setKeyedTag(key, tag);
    }

    public void setTagInternal(int key, Object tag) {
        if ((key >>> 24) != VIEW_STATE_WINDOW_FOCUSED) {
            throw new IllegalArgumentException("The key must be a framework-specific resource id.");
        }
        setKeyedTag(key, tag);
    }

    private void setKeyedTag(int key, Object tag) {
        if (this.mKeyedTags == null) {
            this.mKeyedTags = new SparseArray(VIEW_STATE_SELECTED);
        }
        this.mKeyedTags.put(key, tag);
    }

    public void debug() {
        debug(VISIBLE);
    }

    protected void debug(int depth) {
        String output = debugIndent(depth + NO_ID) + "+ " + this;
        int id = getId();
        if (id != NO_ID) {
            output = output + " (id=" + id + ")";
        }
        Object tag = getTag();
        if (tag != null) {
            output = output + " (tag=" + tag + ")";
        }
        Log.d(VIEW_LOG_TAG, output);
        if ((this.mPrivateFlags & VIEW_STATE_SELECTED) != 0) {
            Log.d(VIEW_LOG_TAG, debugIndent(depth) + " FOCUSED");
        }
        Log.d(VIEW_LOG_TAG, debugIndent(depth) + "frame={" + this.mLeft + ", " + this.mTop + ", " + this.mRight + ", " + this.mBottom + "} scroll={" + this.mScrollX + ", " + this.mScrollY + "} ");
        if (!(this.mPaddingLeft == 0 && this.mPaddingTop == 0 && this.mPaddingRight == 0 && this.mPaddingBottom == 0)) {
            Log.d(VIEW_LOG_TAG, debugIndent(depth) + "padding={" + this.mPaddingLeft + ", " + this.mPaddingTop + ", " + this.mPaddingRight + ", " + this.mPaddingBottom + "}");
        }
        Log.d(VIEW_LOG_TAG, debugIndent(depth) + "mMeasureWidth=" + this.mMeasuredWidth + " mMeasureHeight=" + this.mMeasuredHeight);
        output = debugIndent(depth);
        if (this.mLayoutParams == null) {
            output = output + "BAD! no layout params";
        } else {
            output = this.mLayoutParams.debug(output);
        }
        Log.d(VIEW_LOG_TAG, output);
        Log.d(VIEW_LOG_TAG, ((debugIndent(depth) + "flags={") + printFlags(this.mViewFlags)) + "}");
        Log.d(VIEW_LOG_TAG, ((debugIndent(depth) + "privateFlags={") + printPrivateFlags(this.mPrivateFlags)) + "}");
    }

    protected static String debugIndent(int depth) {
        StringBuilder spaces = new StringBuilder(((depth * VIEW_STATE_SELECTED) + TEXT_DIRECTION_LTR) * VIEW_STATE_SELECTED);
        for (int i = VISIBLE; i < (depth * VIEW_STATE_SELECTED) + TEXT_DIRECTION_LTR; i += VIEW_STATE_WINDOW_FOCUSED) {
            spaces.append(' ').append(' ');
        }
        return spaces.toString();
    }

    @ExportedProperty(category = "layout")
    public int getBaseline() {
        return NO_ID;
    }

    public boolean isInLayout() {
        ViewRootImpl viewRoot = getViewRootImpl();
        return (viewRoot == null || !viewRoot.isInLayout()) ? DBG : true;
    }

    public void requestLayout() {
        if (this.mMeasureCache != null) {
            this.mMeasureCache.clear();
        }
        if (this.mAttachInfo != null && this.mAttachInfo.mViewRequestingLayout == null) {
            ViewRootImpl viewRoot = getViewRootImpl();
            if (viewRoot == null || !viewRoot.isInLayout() || viewRoot.requestLayoutDuringLayout(this)) {
                this.mAttachInfo.mViewRequestingLayout = this;
            } else {
                return;
            }
        }
        this.mPrivateFlags |= SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        this.mPrivateFlags |= UNDEFINED_PADDING;
        if (!(this.mParent == null || this.mParent.isLayoutRequested())) {
            this.mParent.requestLayout();
        }
        if (this.mAttachInfo != null && this.mAttachInfo.mViewRequestingLayout == this) {
            this.mAttachInfo.mViewRequestingLayout = null;
        }
    }

    public void forceLayout() {
        if (this.mMeasureCache != null) {
            this.mMeasureCache.clear();
        }
        this.mPrivateFlags |= SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        this.mPrivateFlags |= UNDEFINED_PADDING;
    }

    public final void measure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean optical = isLayoutModeOptical(this);
        if (optical != isLayoutModeOptical(this.mParent)) {
            Insets insets = getOpticalInsets();
            int oWidth = insets.left + insets.right;
            int oHeight = insets.top + insets.bottom;
            if (optical) {
                oWidth = -oWidth;
            }
            widthMeasureSpec = MeasureSpec.adjust(widthMeasureSpec, oWidth);
            if (optical) {
                oHeight = -oHeight;
            }
            heightMeasureSpec = MeasureSpec.adjust(heightMeasureSpec, oHeight);
        }
        long key = (((long) widthMeasureSpec) << VIEW_STATE_ACTIVATED) | (((long) heightMeasureSpec) & ExpandableListView.PACKED_POSITION_VALUE_NULL);
        if (this.mMeasureCache == null) {
            this.mMeasureCache = new LongSparseLongArray(VIEW_STATE_SELECTED);
        }
        boolean forceLayout = (this.mPrivateFlags & SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == SYSTEM_UI_FLAG_IMMERSIVE_STICKY ? true : DBG;
        boolean isExactly = (MeasureSpec.getMode(widthMeasureSpec) == STATUS_BAR_TRANSLUCENT && MeasureSpec.getMode(heightMeasureSpec) == STATUS_BAR_TRANSLUCENT) ? true : DBG;
        boolean matchingSize = (isExactly && getMeasuredWidth() == MeasureSpec.getSize(widthMeasureSpec) && getMeasuredHeight() == MeasureSpec.getSize(heightMeasureSpec)) ? true : DBG;
        if (forceLayout || !(matchingSize || (widthMeasureSpec == this.mOldWidthMeasureSpec && heightMeasureSpec == this.mOldHeightMeasureSpec))) {
            this.mPrivateFlags &= -2049;
            resolveRtlPropertiesIfNeeded();
            int cacheIndex = forceLayout ? NO_ID : this.mMeasureCache.indexOfKey(key);
            if (cacheIndex < 0 || sIgnoreMeasureCache) {
                onMeasure(widthMeasureSpec, heightMeasureSpec);
                this.mPrivateFlags3 &= -9;
            } else {
                long value = this.mMeasureCache.valueAt(cacheIndex);
                setMeasuredDimensionRaw((int) (value >> VIEW_STATE_ACTIVATED), (int) value);
                this.mPrivateFlags3 |= VIEW_STATE_ENABLED;
            }
            if ((this.mPrivateFlags & SYSTEM_UI_FLAG_IMMERSIVE) != SYSTEM_UI_FLAG_IMMERSIVE) {
                throw new IllegalStateException("onMeasure() did not set the measured dimension by calling setMeasuredDimension()");
            }
            this.mPrivateFlags |= PFLAG_LAYOUT_REQUIRED;
        }
        this.mOldWidthMeasureSpec = widthMeasureSpec;
        this.mOldHeightMeasureSpec = heightMeasureSpec;
        this.mMeasureCache.put(key, (((long) this.mMeasuredWidth) << VIEW_STATE_ACTIVATED) | (((long) this.mMeasuredHeight) & ExpandableListView.PACKED_POSITION_VALUE_NULL));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec), getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    protected final void setMeasuredDimension(int measuredWidth, int measuredHeight) {
        boolean optical = isLayoutModeOptical(this);
        if (optical != isLayoutModeOptical(this.mParent)) {
            Insets insets = getOpticalInsets();
            int opticalWidth = insets.left + insets.right;
            int opticalHeight = insets.top + insets.bottom;
            if (!optical) {
                opticalWidth = -opticalWidth;
            }
            measuredWidth += opticalWidth;
            if (!optical) {
                opticalHeight = -opticalHeight;
            }
            measuredHeight += opticalHeight;
        }
        setMeasuredDimensionRaw(measuredWidth, measuredHeight);
    }

    private void setMeasuredDimensionRaw(int measuredWidth, int measuredHeight) {
        this.mMeasuredWidth = measuredWidth;
        this.mMeasuredHeight = measuredHeight;
        this.mPrivateFlags |= SYSTEM_UI_FLAG_IMMERSIVE;
    }

    public static int combineMeasuredStates(int curState, int newState) {
        return curState | newState;
    }

    public static int resolveSize(int size, int measureSpec) {
        return resolveSizeAndState(size, measureSpec, VISIBLE) & MEASURED_SIZE_MASK;
    }

    public static int resolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case UNDEFINED_PADDING /*-2147483648*/:
                if (specSize >= size) {
                    result = size;
                    break;
                }
                result = specSize | STATUS_BAR_DISABLE_RECENT;
                break;
            case VISIBLE /*0*/:
                result = size;
                break;
            case STATUS_BAR_TRANSLUCENT /*1073741824*/:
                result = specSize;
                break;
        }
        return (MEASURED_STATE_MASK & childMeasuredState) | result;
    }

    public static int getDefaultSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case UNDEFINED_PADDING /*-2147483648*/:
            case STATUS_BAR_TRANSLUCENT /*1073741824*/:
                return specSize;
            case VISIBLE /*0*/:
                return size;
            default:
                return result;
        }
    }

    protected int getSuggestedMinimumHeight() {
        return this.mBackground == null ? this.mMinHeight : Math.max(this.mMinHeight, this.mBackground.getMinimumHeight());
    }

    protected int getSuggestedMinimumWidth() {
        return this.mBackground == null ? this.mMinWidth : Math.max(this.mMinWidth, this.mBackground.getMinimumWidth());
    }

    public int getMinimumHeight() {
        return this.mMinHeight;
    }

    public void setMinimumHeight(int minHeight) {
        this.mMinHeight = minHeight;
        requestLayout();
    }

    public int getMinimumWidth() {
        return this.mMinWidth;
    }

    public void setMinimumWidth(int minWidth) {
        this.mMinWidth = minWidth;
        requestLayout();
    }

    public Animation getAnimation() {
        return this.mCurrentAnimation;
    }

    public void startAnimation(Animation animation) {
        animation.setStartTime(-1);
        setAnimation(animation);
        invalidateParentCaches();
        invalidate(true);
    }

    public void clearAnimation() {
        if (this.mCurrentAnimation != null) {
            this.mCurrentAnimation.detach();
        }
        this.mCurrentAnimation = null;
        invalidateParentIfNeeded();
    }

    public void setAnimation(Animation animation) {
        this.mCurrentAnimation = animation;
        if (animation != null) {
            if (this.mAttachInfo != null && this.mAttachInfo.mDisplayState == VIEW_STATE_WINDOW_FOCUSED && animation.getStartTime() == -1) {
                animation.setStartTime(AnimationUtils.currentAnimationTimeMillis());
            }
            animation.reset();
        }
    }

    protected void onAnimationStart() {
        this.mPrivateFlags |= STATUS_BAR_DISABLE_EXPAND;
    }

    protected void onAnimationEnd() {
        this.mPrivateFlags &= -65537;
    }

    protected boolean onSetAlpha(int alpha) {
        return DBG;
    }

    public boolean gatherTransparentRegion(Region region) {
        AttachInfo attachInfo = this.mAttachInfo;
        if (!(region == null || attachInfo == null)) {
            int pflags = this.mPrivateFlags;
            if ((pflags & WILL_NOT_DRAW) == 0) {
                int[] location = attachInfo.mTransparentLocation;
                getLocationInWindow(location);
                region.op(location[VISIBLE], location[VIEW_STATE_WINDOW_FOCUSED], (location[VISIBLE] + this.mRight) - this.mLeft, (location[VIEW_STATE_WINDOW_FOCUSED] + this.mBottom) - this.mTop, Op.DIFFERENCE);
            } else if (!((pflags & VIEW_STATE_DRAG_CAN_ACCEPT) == 0 || this.mBackground == null || this.mBackground.getOpacity() == -2)) {
                applyDrawableToTransparentRegion(this.mBackground, region);
            }
        }
        return true;
    }

    public void playSoundEffect(int soundConstant) {
        if (this.mAttachInfo != null && this.mAttachInfo.mRootCallbacks != null && isSoundEffectsEnabled()) {
            this.mAttachInfo.mRootCallbacks.playSoundEffect(soundConstant);
        }
    }

    public boolean performHapticFeedback(int feedbackConstant) {
        return performHapticFeedback(feedbackConstant, VISIBLE);
    }

    public boolean performHapticFeedback(int feedbackConstant, int flags) {
        boolean z = DBG;
        if (this.mAttachInfo == null) {
            return DBG;
        }
        if ((flags & VIEW_STATE_WINDOW_FOCUSED) == 0 && !isHapticFeedbackEnabled()) {
            return DBG;
        }
        Callbacks callbacks = this.mAttachInfo.mRootCallbacks;
        if ((flags & VIEW_STATE_SELECTED) != 0) {
            z = true;
        }
        return callbacks.performHapticFeedback(feedbackConstant, z);
    }

    public void setSystemUiVisibility(int visibility) {
        if (visibility != this.mSystemUiVisibility) {
            this.mSystemUiVisibility = visibility;
            if (this.mParent != null && this.mAttachInfo != null && !this.mAttachInfo.mRecomputeGlobalAttributes) {
                this.mParent.recomputeViewAttributes(this);
            }
        }
    }

    public int getSystemUiVisibility() {
        return this.mSystemUiVisibility;
    }

    public int getWindowSystemUiVisibility() {
        return this.mAttachInfo != null ? this.mAttachInfo.mSystemUiVisibility : VISIBLE;
    }

    public void onWindowSystemUiVisibilityChanged(int visible) {
    }

    public void dispatchWindowSystemUiVisiblityChanged(int visible) {
        onWindowSystemUiVisibilityChanged(visible);
    }

    public void setOnSystemUiVisibilityChangeListener(OnSystemUiVisibilityChangeListener l) {
        getListenerInfo().mOnSystemUiVisibilityChangeListener = l;
        if (this.mParent != null && this.mAttachInfo != null && !this.mAttachInfo.mRecomputeGlobalAttributes) {
            this.mParent.recomputeViewAttributes(this);
        }
    }

    public void dispatchSystemUiVisibilityChanged(int visibility) {
        ListenerInfo li = this.mListenerInfo;
        if (li != null && li.mOnSystemUiVisibilityChangeListener != null) {
            li.mOnSystemUiVisibilityChangeListener.onSystemUiVisibilityChange(visibility & PUBLIC_STATUS_BAR_VISIBILITY_MASK);
        }
    }

    boolean updateLocalSystemUiVisibility(int localValue, int localChanges) {
        int val = (this.mSystemUiVisibility & (localChanges ^ NO_ID)) | (localValue & localChanges);
        if (val == this.mSystemUiVisibility) {
            return DBG;
        }
        setSystemUiVisibility(val);
        return true;
    }

    public void setDisabledSystemUiVisibility(int flags) {
        if (this.mAttachInfo != null && this.mAttachInfo.mDisabledSystemUiVisibility != flags) {
            this.mAttachInfo.mDisabledSystemUiVisibility = flags;
            if (this.mParent != null) {
                this.mParent.recomputeViewAttributes(this);
            }
        }
    }

    public final boolean startDrag(ClipData data, DragShadowBuilder shadowBuilder, Object myLocalState, int flags) {
        Point shadowSize = new Point();
        Point shadowTouchPoint = new Point();
        shadowBuilder.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
        if (shadowSize.x < 0 || shadowSize.y < 0 || shadowTouchPoint.x < 0 || shadowTouchPoint.y < 0) {
            throw new IllegalStateException("Drag shadow dimensions must not be negative");
        }
        Surface surface = new Surface();
        Canvas canvas;
        try {
            IBinder token = this.mAttachInfo.mSession.prepareDrag(this.mAttachInfo.mWindow, flags, shadowSize.x, shadowSize.y, surface);
            if (token == null) {
                return DBG;
            }
            canvas = surface.lockCanvas(null);
            canvas.drawColor(VISIBLE, Mode.CLEAR);
            shadowBuilder.onDrawShadow(canvas);
            surface.unlockCanvasAndPost(canvas);
            ViewRootImpl root = getViewRootImpl();
            root.setLocalDragState(myLocalState);
            root.getLastTouchPoint(shadowSize);
            boolean okay = this.mAttachInfo.mSession.performDrag(this.mAttachInfo.mWindow, token, (float) shadowSize.x, (float) shadowSize.y, (float) shadowTouchPoint.x, (float) shadowTouchPoint.y, data);
            surface.release();
            return okay;
        } catch (Throwable e) {
            Log.e(VIEW_LOG_TAG, "Unable to initiate drag", e);
            surface.destroy();
            return DBG;
        } catch (Throwable th) {
            surface.unlockCanvasAndPost(canvas);
        }
    }

    public boolean onDragEvent(DragEvent event) {
        return DBG;
    }

    public boolean dispatchDragEvent(DragEvent event) {
        ListenerInfo li = this.mListenerInfo;
        if (li == null || li.mOnDragListener == null || (this.mViewFlags & VIEW_STATE_ACTIVATED) != 0 || !li.mOnDragListener.onDrag(this, event)) {
            return onDragEvent(event);
        }
        return true;
    }

    boolean canAcceptDrag() {
        return (this.mPrivateFlags2 & VIEW_STATE_WINDOW_FOCUSED) != 0 ? true : DBG;
    }

    public void onCloseSystemDialogs(String reason) {
    }

    public void applyDrawableToTransparentRegion(Drawable dr, Region region) {
        Region r = dr.getTransparentRegion();
        Rect db = dr.getBounds();
        AttachInfo attachInfo = this.mAttachInfo;
        if (r == null || attachInfo == null) {
            region.op(db, Op.DIFFERENCE);
            return;
        }
        int w = getRight() - getLeft();
        int h = getBottom() - getTop();
        if (db.left > 0) {
            r.op(VISIBLE, VISIBLE, db.left, h, Op.UNION);
        }
        if (db.right < w) {
            r.op(db.right, VISIBLE, w, h, Op.UNION);
        }
        if (db.top > 0) {
            r.op(VISIBLE, VISIBLE, w, db.top, Op.UNION);
        }
        if (db.bottom < h) {
            r.op(VISIBLE, db.bottom, w, h, Op.UNION);
        }
        int[] location = attachInfo.mTransparentLocation;
        getLocationInWindow(location);
        r.translate(location[VISIBLE], location[VIEW_STATE_WINDOW_FOCUSED]);
        region.op(r, Op.INTERSECT);
    }

    private void checkForLongClick(int delayOffset) {
        if ((this.mViewFlags & STATUS_BAR_DISABLE_HOME) == STATUS_BAR_DISABLE_HOME) {
            this.mHasPerformedLongPress = DBG;
            if (this.mPendingCheckForLongPress == null) {
                this.mPendingCheckForLongPress = new CheckForLongPress();
            }
            this.mPendingCheckForLongPress.rememberWindowAttachCount();
            postDelayed(this.mPendingCheckForLongPress, (long) (ViewConfiguration.getLongPressTimeout() - delayOffset));
        }
    }

    public static View inflate(Context context, int resource, ViewGroup root) {
        return LayoutInflater.from(context).inflate(resource, root);
    }

    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        int overScrollMode = this.mOverScrollMode;
        boolean canScrollHorizontal = computeHorizontalScrollRange() > computeHorizontalScrollExtent() ? true : DBG;
        boolean canScrollVertical = computeVerticalScrollRange() > computeVerticalScrollExtent() ? true : DBG;
        boolean overScrollHorizontal = (overScrollMode == 0 || (overScrollMode == VIEW_STATE_WINDOW_FOCUSED && canScrollHorizontal)) ? true : DBG;
        boolean overScrollVertical = (overScrollMode == 0 || (overScrollMode == VIEW_STATE_WINDOW_FOCUSED && canScrollVertical)) ? true : DBG;
        int newScrollX = scrollX + deltaX;
        if (!overScrollHorizontal) {
            maxOverScrollX = VISIBLE;
        }
        int newScrollY = scrollY + deltaY;
        if (!overScrollVertical) {
            maxOverScrollY = VISIBLE;
        }
        int left = -maxOverScrollX;
        int right = maxOverScrollX + scrollRangeX;
        int top = -maxOverScrollY;
        int bottom = maxOverScrollY + scrollRangeY;
        boolean clampedX = DBG;
        if (newScrollX > right) {
            newScrollX = right;
            clampedX = true;
        } else if (newScrollX < left) {
            newScrollX = left;
            clampedX = true;
        }
        boolean clampedY = DBG;
        if (newScrollY > bottom) {
            newScrollY = bottom;
            clampedY = true;
        } else if (newScrollY < top) {
            newScrollY = top;
            clampedY = true;
        }
        onOverScrolled(newScrollX, newScrollY, clampedX, clampedY);
        if (clampedX || clampedY) {
            return true;
        }
        return DBG;
    }

    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
    }

    public int getOverScrollMode() {
        return this.mOverScrollMode;
    }

    public void setOverScrollMode(int overScrollMode) {
        if (overScrollMode == 0 || overScrollMode == VIEW_STATE_WINDOW_FOCUSED || overScrollMode == VIEW_STATE_SELECTED) {
            this.mOverScrollMode = overScrollMode;
            return;
        }
        throw new IllegalArgumentException("Invalid overscroll mode " + overScrollMode);
    }

    public void setNestedScrollingEnabled(boolean enabled) {
        if (enabled) {
            this.mPrivateFlags3 |= WILL_NOT_DRAW;
            return;
        }
        stopNestedScroll();
        this.mPrivateFlags3 &= -129;
    }

    public boolean isNestedScrollingEnabled() {
        return (this.mPrivateFlags3 & WILL_NOT_DRAW) == WILL_NOT_DRAW ? true : DBG;
    }

    public boolean startNestedScroll(int axes) {
        if (hasNestedScrollingParent()) {
            return true;
        }
        if (isNestedScrollingEnabled()) {
            View child = this;
            for (ViewParent p = getParent(); p != null; p = p.getParent()) {
                try {
                    if (p.onStartNestedScroll(child, this, axes)) {
                        this.mNestedScrollingParent = p;
                        p.onNestedScrollAccepted(child, this, axes);
                        return true;
                    }
                } catch (AbstractMethodError e) {
                    Log.e(VIEW_LOG_TAG, "ViewParent " + p + " does not implement interface " + "method onStartNestedScroll", e);
                }
                if (p instanceof View) {
                    child = (View) p;
                }
            }
        }
        return DBG;
    }

    public void stopNestedScroll() {
        if (this.mNestedScrollingParent != null) {
            this.mNestedScrollingParent.onStopNestedScroll(this);
            this.mNestedScrollingParent = null;
        }
    }

    public boolean hasNestedScrollingParent() {
        return this.mNestedScrollingParent != null ? true : DBG;
    }

    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        if (isNestedScrollingEnabled() && this.mNestedScrollingParent != null) {
            if (dxConsumed != 0 || dyConsumed != 0 || dxUnconsumed != 0 || dyUnconsumed != 0) {
                int startX = VISIBLE;
                int startY = VISIBLE;
                if (offsetInWindow != null) {
                    getLocationInWindow(offsetInWindow);
                    startX = offsetInWindow[VISIBLE];
                    startY = offsetInWindow[VIEW_STATE_WINDOW_FOCUSED];
                }
                this.mNestedScrollingParent.onNestedScroll(this, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
                if (offsetInWindow != null) {
                    getLocationInWindow(offsetInWindow);
                    offsetInWindow[VISIBLE] = offsetInWindow[VISIBLE] - startX;
                    offsetInWindow[VIEW_STATE_WINDOW_FOCUSED] = offsetInWindow[VIEW_STATE_WINDOW_FOCUSED] - startY;
                }
                return true;
            } else if (offsetInWindow != null) {
                offsetInWindow[VISIBLE] = VISIBLE;
                offsetInWindow[VIEW_STATE_WINDOW_FOCUSED] = VISIBLE;
            }
        }
        return DBG;
    }

    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        if (!isNestedScrollingEnabled() || this.mNestedScrollingParent == null) {
            return DBG;
        }
        if (dx != 0 || dy != 0) {
            int startX = VISIBLE;
            int startY = VISIBLE;
            if (offsetInWindow != null) {
                getLocationInWindow(offsetInWindow);
                startX = offsetInWindow[VISIBLE];
                startY = offsetInWindow[VIEW_STATE_WINDOW_FOCUSED];
            }
            if (consumed == null) {
                if (this.mTempNestedScrollConsumed == null) {
                    this.mTempNestedScrollConsumed = new int[VIEW_STATE_SELECTED];
                }
                consumed = this.mTempNestedScrollConsumed;
            }
            consumed[VISIBLE] = VISIBLE;
            consumed[VIEW_STATE_WINDOW_FOCUSED] = VISIBLE;
            this.mNestedScrollingParent.onNestedPreScroll(this, dx, dy, consumed);
            if (offsetInWindow != null) {
                getLocationInWindow(offsetInWindow);
                offsetInWindow[VISIBLE] = offsetInWindow[VISIBLE] - startX;
                offsetInWindow[VIEW_STATE_WINDOW_FOCUSED] = offsetInWindow[VIEW_STATE_WINDOW_FOCUSED] - startY;
            }
            if (consumed[VISIBLE] == 0 && consumed[VIEW_STATE_WINDOW_FOCUSED] == 0) {
                return DBG;
            }
            return true;
        } else if (offsetInWindow == null) {
            return DBG;
        } else {
            offsetInWindow[VISIBLE] = VISIBLE;
            offsetInWindow[VIEW_STATE_WINDOW_FOCUSED] = VISIBLE;
            return DBG;
        }
    }

    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        if (!isNestedScrollingEnabled() || this.mNestedScrollingParent == null) {
            return DBG;
        }
        return this.mNestedScrollingParent.onNestedFling(this, velocityX, velocityY, consumed);
    }

    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        if (!isNestedScrollingEnabled() || this.mNestedScrollingParent == null) {
            return DBG;
        }
        return this.mNestedScrollingParent.onNestedPreFling(this, velocityX, velocityY);
    }

    protected float getVerticalScrollFactor() {
        if (this.mVerticalScrollFactor == 0.0f) {
            TypedValue outValue = new TypedValue();
            if (this.mContext.getTheme().resolveAttribute(R.attr.listPreferredItemHeight, outValue, true)) {
                this.mVerticalScrollFactor = outValue.getDimension(this.mContext.getResources().getDisplayMetrics());
            } else {
                throw new IllegalStateException("Expected theme to define listPreferredItemHeight.");
            }
        }
        return this.mVerticalScrollFactor;
    }

    protected float getHorizontalScrollFactor() {
        return getVerticalScrollFactor();
    }

    @ExportedProperty(category = "text", mapping = {@IntToString(from = 0, to = "INHERIT"), @IntToString(from = 1, to = "FIRST_STRONG"), @IntToString(from = 2, to = "ANY_RTL"), @IntToString(from = 3, to = "LTR"), @IntToString(from = 4, to = "RTL"), @IntToString(from = 5, to = "LOCALE")})
    public int getRawTextDirection() {
        return (this.mPrivateFlags2 & PFLAG2_TEXT_DIRECTION_MASK) >> TEXT_ALIGNMENT_VIEW_END;
    }

    public void setTextDirection(int textDirection) {
        if (getRawTextDirection() != textDirection) {
            this.mPrivateFlags2 &= -449;
            resetResolvedTextDirection();
            this.mPrivateFlags2 |= (textDirection << TEXT_ALIGNMENT_VIEW_END) & PFLAG2_TEXT_DIRECTION_MASK;
            resolveTextDirection();
            onRtlPropertiesChanged(getLayoutDirection());
            requestLayout();
            invalidate(true);
        }
    }

    @ExportedProperty(category = "text", mapping = {@IntToString(from = 0, to = "INHERIT"), @IntToString(from = 1, to = "FIRST_STRONG"), @IntToString(from = 2, to = "ANY_RTL"), @IntToString(from = 3, to = "LTR"), @IntToString(from = 4, to = "RTL"), @IntToString(from = 5, to = "LOCALE")})
    public int getTextDirection() {
        return (this.mPrivateFlags2 & PFLAG2_TEXT_DIRECTION_RESOLVED_MASK) >> PFLAG2_TEXT_DIRECTION_RESOLVED_MASK_SHIFT;
    }

    public boolean resolveTextDirection() {
        this.mPrivateFlags2 &= -7681;
        if (hasRtlSupport()) {
            int textDirection = getRawTextDirection();
            switch (textDirection) {
                case VISIBLE /*0*/:
                    if (canResolveTextDirection()) {
                        try {
                            if (this.mParent.isTextDirectionResolved()) {
                                int parentResolvedDirection;
                                try {
                                    parentResolvedDirection = this.mParent.getTextDirection();
                                } catch (AbstractMethodError e) {
                                    Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                                    parentResolvedDirection = TEXT_DIRECTION_LTR;
                                }
                                switch (parentResolvedDirection) {
                                    case VIEW_STATE_WINDOW_FOCUSED /*1*/:
                                    case VIEW_STATE_SELECTED /*2*/:
                                    case TEXT_DIRECTION_LTR /*3*/:
                                    case VIEW_STATE_FOCUSED /*4*/:
                                    case TEXT_DIRECTION_LOCALE /*5*/:
                                        this.mPrivateFlags2 |= parentResolvedDirection << PFLAG2_TEXT_DIRECTION_RESOLVED_MASK_SHIFT;
                                        break;
                                    default:
                                        this.mPrivateFlags2 |= SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                                        break;
                                }
                            }
                            this.mPrivateFlags2 |= SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                            return DBG;
                        } catch (AbstractMethodError e2) {
                            Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e2);
                            this.mPrivateFlags2 |= SYSTEM_UI_LAYOUT_FLAGS;
                            return true;
                        }
                    }
                    this.mPrivateFlags2 |= SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                    return DBG;
                case VIEW_STATE_WINDOW_FOCUSED /*1*/:
                case VIEW_STATE_SELECTED /*2*/:
                case TEXT_DIRECTION_LTR /*3*/:
                case VIEW_STATE_FOCUSED /*4*/:
                case TEXT_DIRECTION_LOCALE /*5*/:
                    this.mPrivateFlags2 |= textDirection << PFLAG2_TEXT_DIRECTION_RESOLVED_MASK_SHIFT;
                    break;
                default:
                    this.mPrivateFlags2 |= SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                    break;
            }
        }
        this.mPrivateFlags2 |= SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        this.mPrivateFlags2 |= VIEW_STATE_DRAG_HOVERED;
        return true;
    }

    public boolean canResolveTextDirection() {
        switch (getRawTextDirection()) {
            case VISIBLE /*0*/:
                if (this.mParent != null) {
                    try {
                        return this.mParent.canResolveTextDirection();
                    } catch (AbstractMethodError e) {
                        Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                    }
                }
                return DBG;
            default:
                return true;
        }
    }

    public void resetResolvedTextDirection() {
        this.mPrivateFlags2 &= -7681;
        this.mPrivateFlags2 |= SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
    }

    public boolean isTextDirectionInherited() {
        return getRawTextDirection() == 0 ? true : DBG;
    }

    public boolean isTextDirectionResolved() {
        return (this.mPrivateFlags2 & VIEW_STATE_DRAG_HOVERED) == VIEW_STATE_DRAG_HOVERED ? true : DBG;
    }

    @ExportedProperty(category = "text", mapping = {@IntToString(from = 0, to = "INHERIT"), @IntToString(from = 1, to = "GRAVITY"), @IntToString(from = 2, to = "TEXT_START"), @IntToString(from = 3, to = "TEXT_END"), @IntToString(from = 4, to = "CENTER"), @IntToString(from = 5, to = "VIEW_START"), @IntToString(from = 6, to = "VIEW_END")})
    public int getRawTextAlignment() {
        return (this.mPrivateFlags2 & PFLAG2_TEXT_ALIGNMENT_MASK) >> PFLAG2_TEXT_ALIGNMENT_MASK_SHIFT;
    }

    public void setTextAlignment(int textAlignment) {
        if (textAlignment != getRawTextAlignment()) {
            this.mPrivateFlags2 &= -57345;
            resetResolvedTextAlignment();
            this.mPrivateFlags2 |= (textAlignment << PFLAG2_TEXT_ALIGNMENT_MASK_SHIFT) & PFLAG2_TEXT_ALIGNMENT_MASK;
            resolveTextAlignment();
            onRtlPropertiesChanged(getLayoutDirection());
            requestLayout();
            invalidate(true);
        }
    }

    @ExportedProperty(category = "text", mapping = {@IntToString(from = 0, to = "INHERIT"), @IntToString(from = 1, to = "GRAVITY"), @IntToString(from = 2, to = "TEXT_START"), @IntToString(from = 3, to = "TEXT_END"), @IntToString(from = 4, to = "CENTER"), @IntToString(from = 5, to = "VIEW_START"), @IntToString(from = 6, to = "VIEW_END")})
    public int getTextAlignment() {
        return (this.mPrivateFlags2 & PFLAG2_TEXT_ALIGNMENT_RESOLVED_MASK) >> PFLAG2_TEXT_ALIGNMENT_RESOLVED_MASK_SHIFT;
    }

    public boolean resolveTextAlignment() {
        this.mPrivateFlags2 &= -983041;
        if (hasRtlSupport()) {
            int textAlignment = getRawTextAlignment();
            switch (textAlignment) {
                case VISIBLE /*0*/:
                    if (canResolveTextAlignment()) {
                        try {
                            if (this.mParent.isTextAlignmentResolved()) {
                                int parentResolvedTextAlignment;
                                try {
                                    parentResolvedTextAlignment = this.mParent.getTextAlignment();
                                } catch (AbstractMethodError e) {
                                    Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                                    parentResolvedTextAlignment = VIEW_STATE_WINDOW_FOCUSED;
                                }
                                switch (parentResolvedTextAlignment) {
                                    case VIEW_STATE_WINDOW_FOCUSED /*1*/:
                                    case VIEW_STATE_SELECTED /*2*/:
                                    case TEXT_DIRECTION_LTR /*3*/:
                                    case VIEW_STATE_FOCUSED /*4*/:
                                    case TEXT_DIRECTION_LOCALE /*5*/:
                                    case TEXT_ALIGNMENT_VIEW_END /*6*/:
                                        this.mPrivateFlags2 |= parentResolvedTextAlignment << PFLAG2_TEXT_ALIGNMENT_RESOLVED_MASK_SHIFT;
                                        break;
                                    default:
                                        this.mPrivateFlags2 |= WILL_NOT_CACHE_DRAWING;
                                        break;
                                }
                            }
                            this.mPrivateFlags2 |= WILL_NOT_CACHE_DRAWING;
                            return DBG;
                        } catch (AbstractMethodError e2) {
                            Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e2);
                            this.mPrivateFlags2 |= Menu.CATEGORY_SECONDARY;
                            return true;
                        }
                    }
                    this.mPrivateFlags2 |= WILL_NOT_CACHE_DRAWING;
                    return DBG;
                case VIEW_STATE_WINDOW_FOCUSED /*1*/:
                case VIEW_STATE_SELECTED /*2*/:
                case TEXT_DIRECTION_LTR /*3*/:
                case VIEW_STATE_FOCUSED /*4*/:
                case TEXT_DIRECTION_LOCALE /*5*/:
                case TEXT_ALIGNMENT_VIEW_END /*6*/:
                    this.mPrivateFlags2 |= textAlignment << PFLAG2_TEXT_ALIGNMENT_RESOLVED_MASK_SHIFT;
                    break;
                default:
                    this.mPrivateFlags2 |= WILL_NOT_CACHE_DRAWING;
                    break;
            }
        }
        this.mPrivateFlags2 |= WILL_NOT_CACHE_DRAWING;
        this.mPrivateFlags2 |= STATUS_BAR_DISABLE_EXPAND;
        return true;
    }

    public boolean canResolveTextAlignment() {
        switch (getRawTextAlignment()) {
            case VISIBLE /*0*/:
                if (this.mParent != null) {
                    try {
                        return this.mParent.canResolveTextAlignment();
                    } catch (AbstractMethodError e) {
                        Log.e(VIEW_LOG_TAG, this.mParent.getClass().getSimpleName() + " does not fully implement ViewParent", e);
                    }
                }
                return DBG;
            default:
                return true;
        }
    }

    public void resetResolvedTextAlignment() {
        this.mPrivateFlags2 &= -983041;
        this.mPrivateFlags2 |= WILL_NOT_CACHE_DRAWING;
    }

    public boolean isTextAlignmentInherited() {
        return getRawTextAlignment() == 0 ? true : DBG;
    }

    public boolean isTextAlignmentResolved() {
        return (this.mPrivateFlags2 & STATUS_BAR_DISABLE_EXPAND) == STATUS_BAR_DISABLE_EXPAND ? true : DBG;
    }

    public static int generateViewId() {
        int result;
        int newValue;
        do {
            result = sNextGeneratedId.get();
            newValue = result + VIEW_STATE_WINDOW_FOCUSED;
            if (newValue > MEASURED_SIZE_MASK) {
                newValue = VIEW_STATE_WINDOW_FOCUSED;
            }
        } while (!sNextGeneratedId.compareAndSet(result, newValue));
        return result;
    }

    public void captureTransitioningViews(List<View> transitioningViews) {
        if (getVisibility() == 0) {
            transitioningViews.add(this);
        }
    }

    public void findNamedViews(Map<String, View> namedElements) {
        if (getVisibility() == 0 || this.mGhostView != null) {
            String transitionName = getTransitionName();
            if (transitionName != null) {
                namedElements.put(transitionName, this);
            }
        }
    }

    public void hackTurnOffWindowResizeAnim(boolean off) {
        this.mAttachInfo.mTurnOffWindowResizeAnim = off;
    }

    public ViewPropertyAnimator animate() {
        if (this.mAnimator == null) {
            this.mAnimator = new ViewPropertyAnimator(this);
        }
        return this.mAnimator;
    }

    public final void setTransitionName(String transitionName) {
        this.mTransitionName = transitionName;
    }

    @ExportedProperty
    public String getTransitionName() {
        return this.mTransitionName;
    }

    private boolean inLiveRegion() {
        if (getAccessibilityLiveRegion() != 0) {
            return true;
        }
        for (ViewParent parent = getParent(); parent instanceof View; parent = parent.getParent()) {
            if (((View) parent).getAccessibilityLiveRegion() != 0) {
                return true;
            }
        }
        return DBG;
    }

    private static void dumpFlags() {
        HashMap<String, String> found = Maps.newHashMap();
        try {
            Field[] arr$ = View.class.getDeclaredFields();
            int len$ = arr$.length;
            for (int i$ = VISIBLE; i$ < len$; i$ += VIEW_STATE_WINDOW_FOCUSED) {
                Field field = arr$[i$];
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) {
                    if (field.getType().equals(Integer.TYPE)) {
                        dumpFlag(found, field.getName(), field.getInt(null));
                    } else if (field.getType().equals(int[].class)) {
                        int[] values = (int[]) field.get(null);
                        for (int i = VISIBLE; i < values.length; i += VIEW_STATE_WINDOW_FOCUSED) {
                            dumpFlag(found, field.getName() + "[" + i + "]", values[i]);
                        }
                    }
                }
            }
            ArrayList<String> keys = Lists.newArrayList();
            keys.addAll(found.keySet());
            Collections.sort(keys);
            Iterator i$2 = keys.iterator();
            while (i$2.hasNext()) {
                Log.d(VIEW_LOG_TAG, (String) found.get((String) i$2.next()));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void dumpFlag(HashMap<String, String> found, String name, int value) {
        String substring;
        Object[] objArr = new Object[VIEW_STATE_WINDOW_FOCUSED];
        objArr[VISIBLE] = Integer.toBinaryString(value);
        String bits = String.format("%32s", objArr).replace('0', ' ');
        int prefix = name.indexOf(95);
        StringBuilder stringBuilder = new StringBuilder();
        if (prefix > 0) {
            substring = name.substring(VISIBLE, prefix);
        } else {
            substring = name;
        }
        found.put(stringBuilder.append(substring).append(bits).append(name).toString(), bits + " " + name);
    }
}
