package android.view;

import android.Manifest.permission;
import android.animation.LayoutTransition;
import android.app.ActivityManagerNative;
import android.bluetooth.BluetoothClass.Device.Major;
import android.content.ClipDescription;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.CompatibilityInfo;
import android.content.res.CompatibilityInfo.Translator;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.media.AudioManager;
import android.net.wifi.WifiEnterpriseConfig;
import android.opengl.GLES20;
import android.os.Binder;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.ParcelFileDescriptor.AutoCloseOutputStream;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.util.AndroidRuntimeException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Slog;
import android.util.TypedValue;
import android.view.Choreographer.FrameCallback;
import android.view.InputQueue.Callback;
import android.view.KeyCharacterMap.FallbackAction;
import android.view.Surface.OutOfResourcesException;
import android.view.SurfaceHolder.Callback2;
import android.view.View.MeasureSpec;
import android.view.ViewTreeObserver.InternalInsetsInfo;
import android.view.WindowManager.BadTokenException;
import android.view.WindowManager.InvalidDisplayException;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener;
import android.view.accessibility.AccessibilityManager.HighTextContrastChangeListener;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.accessibility.IAccessibilityInteractionConnection.Stub;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodManager.FinishedInputEventCallback;
import android.webkit.WebViewClient;
import android.widget.ListPopupWindow;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.Toast;
import com.android.internal.os.SomeArgs;
import com.android.internal.policy.PolicyManager;
import com.android.internal.util.ScreenShapeHelper;
import com.android.internal.view.BaseSurfaceHolder;
import com.android.internal.view.RootViewSurfaceTaker;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;

public final class ViewRootImpl implements ViewParent, Callbacks, HardwareDrawCallbacks {
    private static final boolean DBG = false;
    private static final boolean DEBUG_CONFIGURATION = false;
    private static final boolean DEBUG_DIALOG = false;
    private static final boolean DEBUG_DRAW = false;
    private static final boolean DEBUG_FPS = false;
    private static final boolean DEBUG_IMF = false;
    private static final boolean DEBUG_INPUT_RESIZE = false;
    private static final boolean DEBUG_INPUT_STAGES = false;
    private static final boolean DEBUG_LAYOUT = false;
    private static final boolean DEBUG_ORIENTATION = false;
    private static final boolean DEBUG_TRACKBALL = false;
    private static final boolean LOCAL_LOGV = false;
    private static final int MAX_QUEUED_INPUT_EVENT_POOL_SIZE = 10;
    static final int MAX_TRACKBALL_DELAY = 250;
    private static final int MSG_CHECK_FOCUS = 13;
    private static final int MSG_CLEAR_ACCESSIBILITY_FOCUS_HOST = 21;
    private static final int MSG_CLOSE_SYSTEM_DIALOGS = 14;
    private static final int MSG_DIE = 3;
    private static final int MSG_DISPATCH_APP_VISIBILITY = 8;
    private static final int MSG_DISPATCH_DONE_ANIMATING = 22;
    private static final int MSG_DISPATCH_DRAG_EVENT = 15;
    private static final int MSG_DISPATCH_DRAG_LOCATION_EVENT = 16;
    private static final int MSG_DISPATCH_GET_NEW_SURFACE = 9;
    private static final int MSG_DISPATCH_INPUT_EVENT = 7;
    private static final int MSG_DISPATCH_KEY_FROM_IME = 11;
    private static final int MSG_DISPATCH_SYSTEM_UI_VISIBILITY = 17;
    private static final int MSG_DISPATCH_WINDOW_SHOWN = 26;
    private static final int MSG_FINISH_INPUT_CONNECTION = 12;
    private static final int MSG_INVALIDATE = 1;
    private static final int MSG_INVALIDATE_RECT = 2;
    private static final int MSG_INVALIDATE_WORLD = 23;
    private static final int MSG_PROCESS_INPUT_EVENTS = 19;
    private static final int MSG_RESIZED = 4;
    private static final int MSG_RESIZED_REPORT = 5;
    private static final int MSG_SYNTHESIZE_INPUT_EVENT = 25;
    private static final int MSG_UPDATE_CONFIGURATION = 18;
    private static final int MSG_WINDOW_FOCUS_CHANGED = 6;
    private static final int MSG_WINDOW_MOVED = 24;
    public static final String PROPERTY_EMULATOR_CIRCULAR = "ro.emulator.circular";
    public static final String PROPERTY_EMULATOR_WIN_OUTSET_BOTTOM_PX = "ro.emu.win_outset_bottom_px";
    private static final String PROPERTY_MEDIA_DISABLED = "config.disable_media";
    private static final String PROPERTY_PROFILE_RENDERING = "viewroot.profile_rendering";
    private static final String TAG = "ViewRootImpl";
    static final Interpolator mResizeInterpolator = null;
    static final ArrayList<ComponentCallbacks> sConfigCallbacks = null;
    static boolean sFirstDrawComplete;
    static final ArrayList<Runnable> sFirstDrawHandlers = null;
    static final ThreadLocal<RunQueue> sRunQueues = null;
    View mAccessibilityFocusedHost;
    AccessibilityNodeInfo mAccessibilityFocusedVirtualView;
    AccessibilityInteractionConnectionManager mAccessibilityInteractionConnectionManager;
    AccessibilityInteractionController mAccessibilityInteractionController;
    final AccessibilityManager mAccessibilityManager;
    boolean mAdded;
    boolean mAddedTouchMode;
    boolean mAppVisible;
    boolean mApplyInsetsRequested;
    final AttachInfo mAttachInfo;
    AudioManager mAudioManager;
    final String mBasePackageName;
    boolean mBlockResizeBuffer;
    Choreographer mChoreographer;
    int mClientWindowLayoutFlags;
    final ConsumeBatchedInputImmediatelyRunnable mConsumeBatchedInputImmediatelyRunnable;
    boolean mConsumeBatchedInputImmediatelyScheduled;
    boolean mConsumeBatchedInputScheduled;
    final ConsumeBatchedInputRunnable mConsumedBatchedInputRunnable;
    final Context mContext;
    int mCurScrollY;
    View mCurrentDragView;
    private final int mDensity;
    Rect mDirty;
    final Rect mDispatchContentInsets;
    final Rect mDispatchStableInsets;
    final Display mDisplay;
    final DisplayAdjustments mDisplayAdjustments;
    private final DisplayListener mDisplayListener;
    final DisplayManager mDisplayManager;
    ClipDescription mDragDescription;
    final PointF mDragPoint;
    boolean mDrawDuringWindowsAnimating;
    boolean mDrawingAllowed;
    FallbackEventHandler mFallbackEventHandler;
    boolean mFirst;
    InputStage mFirstInputStage;
    InputStage mFirstPostImeInputStage;
    private int mFpsNumFrames;
    private long mFpsPrevTime;
    private long mFpsStartTime;
    boolean mFullRedrawNeeded;
    final ViewRootHandler mHandler;
    boolean mHandlingLayoutInLayoutRequest;
    int mHardwareXOffset;
    int mHardwareYOffset;
    boolean mHasHadWindowFocus;
    int mHeight;
    HighContrastTextManager mHighContrastTextManager;
    private boolean mInLayout;
    InputChannel mInputChannel;
    protected final InputEventConsistencyVerifier mInputEventConsistencyVerifier;
    WindowInputEventReceiver mInputEventReceiver;
    InputQueue mInputQueue;
    Callback mInputQueueCallback;
    final InvalidateOnAnimationRunnable mInvalidateOnAnimationRunnable;
    boolean mIsAnimating;
    boolean mIsCreating;
    boolean mIsDrawing;
    boolean mIsInTraversal;
    final Configuration mLastConfiguration;
    final InternalInsetsInfo mLastGivenInsets;
    boolean mLastInCompatMode;
    boolean mLastOverscanRequested;
    WeakReference<View> mLastScrolledFocus;
    int mLastSystemUiVisibility;
    final PointF mLastTouchPoint;
    boolean mLastWasImTarget;
    boolean mLayoutRequested;
    ArrayList<View> mLayoutRequesters;
    volatile Object mLocalDragState;
    final WindowLeaked mLocation;
    private boolean mMediaDisabled;
    boolean mNewSurfaceNeeded;
    private final int mNoncompatDensity;
    int mOrigWindowType;
    final Configuration mPendingConfiguration;
    final Rect mPendingContentInsets;
    int mPendingInputEventCount;
    QueuedInputEvent mPendingInputEventHead;
    String mPendingInputEventQueueLengthCounterName;
    QueuedInputEvent mPendingInputEventTail;
    final Rect mPendingOverscanInsets;
    final Rect mPendingStableInsets;
    private ArrayList<LayoutTransition> mPendingTransitions;
    final Rect mPendingVisibleInsets;
    final Region mPreviousTransparentRegion;
    boolean mProcessInputEventsScheduled;
    private boolean mProfile;
    private boolean mProfileRendering;
    private QueuedInputEvent mQueuedInputEventPool;
    private int mQueuedInputEventPoolSize;
    private boolean mRemoved;
    private FrameCallback mRenderProfiler;
    private boolean mRenderProfilingEnabled;
    boolean mReportNextDraw;
    int mResizeAlpha;
    HardwareLayer mResizeBuffer;
    int mResizeBufferDuration;
    long mResizeBufferStartTime;
    final Paint mResizePaint;
    boolean mScrollMayChange;
    int mScrollY;
    Scroller mScroller;
    SendWindowContentChangedAccessibilityEvent mSendWindowContentChangedAccessibilityEvent;
    int mSeq;
    int mSoftInputMode;
    boolean mStopped;
    final Surface mSurface;
    BaseSurfaceHolder mSurfaceHolder;
    Callback2 mSurfaceHolderCallback;
    InputStage mSyntheticInputStage;
    final int mTargetSdkVersion;
    HashSet<View> mTempHashSet;
    final Rect mTempRect;
    final Thread mThread;
    final int[] mTmpLocation;
    final TypedValue mTmpValue;
    Translator mTranslator;
    final Region mTransparentRegion;
    int mTraversalBarrier;
    final TraversalRunnable mTraversalRunnable;
    boolean mTraversalScheduled;
    boolean mUnbufferedInputDispatch;
    View mView;
    final ViewConfiguration mViewConfiguration;
    private int mViewLayoutDirectionInitial;
    int mViewVisibility;
    final Rect mVisRect;
    int mWidth;
    boolean mWillDrawSoon;
    final Rect mWinFrame;
    final W mWindow;
    final LayoutParams mWindowAttributes;
    boolean mWindowAttributesChanged;
    int mWindowAttributesChangesFlag;
    private final boolean mWindowIsRound;
    final IWindowSession mWindowSession;
    boolean mWindowsAnimating;

    /* renamed from: android.view.ViewRootImpl.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ ViewRootImpl this$0;
        final /* synthetic */ ArrayList val$finalRequesters;

        AnonymousClass2(android.view.ViewRootImpl r1, java.util.ArrayList r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.ViewRootImpl.2.<init>(android.view.ViewRootImpl, java.util.ArrayList):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.ViewRootImpl.2.<init>(android.view.ViewRootImpl, java.util.ArrayList):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.2.<init>(android.view.ViewRootImpl, java.util.ArrayList):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.ViewRootImpl.2.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.ViewRootImpl.2.run():void
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
            throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.2.run():void");
        }
    }

    /* renamed from: android.view.ViewRootImpl.3 */
    class AnonymousClass3 implements FrameCallback {
        final /* synthetic */ ViewRootImpl this$0;

        AnonymousClass3(android.view.ViewRootImpl r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.ViewRootImpl.3.<init>(android.view.ViewRootImpl):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.ViewRootImpl.3.<init>(android.view.ViewRootImpl):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.3.<init>(android.view.ViewRootImpl):void");
        }

        public void doFrame(long r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.ViewRootImpl.3.doFrame(long):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.ViewRootImpl.3.doFrame(long):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.3.doFrame(long):void");
        }
    }

    /* renamed from: android.view.ViewRootImpl.4 */
    class AnonymousClass4 implements Runnable {
        final /* synthetic */ ViewRootImpl this$0;

        AnonymousClass4(ViewRootImpl viewRootImpl) {
            this.this$0 = viewRootImpl;
        }

        public void run() {
            this.this$0.mProfileRendering = SystemProperties.getBoolean(ViewRootImpl.PROPERTY_PROFILE_RENDERING, ViewRootImpl.LOCAL_LOGV);
            this.this$0.profileRendering(this.this$0.mAttachInfo.mHasWindowFocus);
            this.this$0.mMediaDisabled = SystemProperties.getBoolean(ViewRootImpl.PROPERTY_MEDIA_DISABLED, ViewRootImpl.LOCAL_LOGV);
            if (this.this$0.mAttachInfo.mHardwareRenderer != null && this.this$0.mAttachInfo.mHardwareRenderer.loadSystemProperties()) {
                this.this$0.invalidate();
            }
            boolean layout = SystemProperties.getBoolean(View.DEBUG_LAYOUT_PROPERTY, ViewRootImpl.LOCAL_LOGV);
            if (layout != this.this$0.mAttachInfo.mDebugLayout) {
                this.this$0.mAttachInfo.mDebugLayout = layout;
                if (!this.this$0.mHandler.hasMessages(ViewRootImpl.MSG_INVALIDATE_WORLD)) {
                    this.this$0.mHandler.sendEmptyMessageDelayed(ViewRootImpl.MSG_INVALIDATE_WORLD, 200);
                }
            }
        }
    }

    static final class AccessibilityInteractionConnection extends Stub {
        private final WeakReference<ViewRootImpl> mViewRootImpl;

        AccessibilityInteractionConnection(android.view.ViewRootImpl r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.ViewRootImpl.AccessibilityInteractionConnection.<init>(android.view.ViewRootImpl):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.ViewRootImpl.AccessibilityInteractionConnection.<init>(android.view.ViewRootImpl):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.AccessibilityInteractionConnection.<init>(android.view.ViewRootImpl):void");
        }

        public void findAccessibilityNodeInfoByAccessibilityId(long r1, android.graphics.Region r3, int r4, android.view.accessibility.IAccessibilityInteractionConnectionCallback r5, int r6, int r7, long r8, android.view.MagnificationSpec r10) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.ViewRootImpl.AccessibilityInteractionConnection.findAccessibilityNodeInfoByAccessibilityId(long, android.graphics.Region, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, int, int, long, android.view.MagnificationSpec):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.ViewRootImpl.AccessibilityInteractionConnection.findAccessibilityNodeInfoByAccessibilityId(long, android.graphics.Region, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, int, int, long, android.view.MagnificationSpec):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.AccessibilityInteractionConnection.findAccessibilityNodeInfoByAccessibilityId(long, android.graphics.Region, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, int, int, long, android.view.MagnificationSpec):void");
        }

        public void findAccessibilityNodeInfosByText(long r1, java.lang.String r3, android.graphics.Region r4, int r5, android.view.accessibility.IAccessibilityInteractionConnectionCallback r6, int r7, int r8, long r9, android.view.MagnificationSpec r11) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.ViewRootImpl.AccessibilityInteractionConnection.findAccessibilityNodeInfosByText(long, java.lang.String, android.graphics.Region, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, int, int, long, android.view.MagnificationSpec):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.ViewRootImpl.AccessibilityInteractionConnection.findAccessibilityNodeInfosByText(long, java.lang.String, android.graphics.Region, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, int, int, long, android.view.MagnificationSpec):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.AccessibilityInteractionConnection.findAccessibilityNodeInfosByText(long, java.lang.String, android.graphics.Region, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, int, int, long, android.view.MagnificationSpec):void");
        }

        public void findAccessibilityNodeInfosByViewId(long r1, java.lang.String r3, android.graphics.Region r4, int r5, android.view.accessibility.IAccessibilityInteractionConnectionCallback r6, int r7, int r8, long r9, android.view.MagnificationSpec r11) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.ViewRootImpl.AccessibilityInteractionConnection.findAccessibilityNodeInfosByViewId(long, java.lang.String, android.graphics.Region, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, int, int, long, android.view.MagnificationSpec):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.ViewRootImpl.AccessibilityInteractionConnection.findAccessibilityNodeInfosByViewId(long, java.lang.String, android.graphics.Region, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, int, int, long, android.view.MagnificationSpec):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.AccessibilityInteractionConnection.findAccessibilityNodeInfosByViewId(long, java.lang.String, android.graphics.Region, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, int, int, long, android.view.MagnificationSpec):void");
        }

        public void findFocus(long r1, int r3, android.graphics.Region r4, int r5, android.view.accessibility.IAccessibilityInteractionConnectionCallback r6, int r7, int r8, long r9, android.view.MagnificationSpec r11) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.ViewRootImpl.AccessibilityInteractionConnection.findFocus(long, int, android.graphics.Region, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, int, int, long, android.view.MagnificationSpec):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.ViewRootImpl.AccessibilityInteractionConnection.findFocus(long, int, android.graphics.Region, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, int, int, long, android.view.MagnificationSpec):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.AccessibilityInteractionConnection.findFocus(long, int, android.graphics.Region, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, int, int, long, android.view.MagnificationSpec):void");
        }

        public void focusSearch(long r1, int r3, android.graphics.Region r4, int r5, android.view.accessibility.IAccessibilityInteractionConnectionCallback r6, int r7, int r8, long r9, android.view.MagnificationSpec r11) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.ViewRootImpl.AccessibilityInteractionConnection.focusSearch(long, int, android.graphics.Region, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, int, int, long, android.view.MagnificationSpec):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.ViewRootImpl.AccessibilityInteractionConnection.focusSearch(long, int, android.graphics.Region, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, int, int, long, android.view.MagnificationSpec):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.AccessibilityInteractionConnection.focusSearch(long, int, android.graphics.Region, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, int, int, long, android.view.MagnificationSpec):void");
        }

        public void performAccessibilityAction(long r1, int r3, android.os.Bundle r4, int r5, android.view.accessibility.IAccessibilityInteractionConnectionCallback r6, int r7, int r8, long r9) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.ViewRootImpl.AccessibilityInteractionConnection.performAccessibilityAction(long, int, android.os.Bundle, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, int, int, long):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.ViewRootImpl.AccessibilityInteractionConnection.performAccessibilityAction(long, int, android.os.Bundle, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, int, int, long):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.AccessibilityInteractionConnection.performAccessibilityAction(long, int, android.os.Bundle, int, android.view.accessibility.IAccessibilityInteractionConnectionCallback, int, int, long):void");
        }
    }

    final class AccessibilityInteractionConnectionManager implements AccessibilityStateChangeListener {
        final /* synthetic */ ViewRootImpl this$0;

        AccessibilityInteractionConnectionManager(ViewRootImpl viewRootImpl) {
            this.this$0 = viewRootImpl;
        }

        public void onAccessibilityStateChanged(boolean enabled) {
            if (enabled) {
                ensureConnection();
                if (this.this$0.mAttachInfo.mHasWindowFocus) {
                    this.this$0.mView.sendAccessibilityEvent(32);
                    View focusedView = this.this$0.mView.findFocus();
                    if (focusedView != null && focusedView != this.this$0.mView) {
                        focusedView.sendAccessibilityEvent(ViewRootImpl.MSG_DISPATCH_APP_VISIBILITY);
                        return;
                    }
                    return;
                }
                return;
            }
            ensureNoConnection();
            this.this$0.mHandler.obtainMessage(ViewRootImpl.MSG_CLEAR_ACCESSIBILITY_FOCUS_HOST).sendToTarget();
        }

        public void ensureConnection() {
            if (!(this.this$0.mAttachInfo.mAccessibilityWindowId != ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED ? true : ViewRootImpl.LOCAL_LOGV)) {
                this.this$0.mAttachInfo.mAccessibilityWindowId = this.this$0.mAccessibilityManager.addAccessibilityInteractionConnection(this.this$0.mWindow, new AccessibilityInteractionConnection(this.this$0));
            }
        }

        public void ensureNoConnection() {
            if (this.this$0.mAttachInfo.mAccessibilityWindowId != ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED ? true : ViewRootImpl.LOCAL_LOGV) {
                this.this$0.mAttachInfo.mAccessibilityWindowId = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
                this.this$0.mAccessibilityManager.removeAccessibilityInteractionConnection(this.this$0.mWindow);
            }
        }
    }

    abstract class InputStage {
        protected static final int FINISH_HANDLED = 1;
        protected static final int FINISH_NOT_HANDLED = 2;
        protected static final int FORWARD = 0;
        private final InputStage mNext;
        final /* synthetic */ ViewRootImpl this$0;

        public InputStage(ViewRootImpl viewRootImpl, InputStage next) {
            this.this$0 = viewRootImpl;
            this.mNext = next;
        }

        public final void deliver(QueuedInputEvent q) {
            if ((q.mFlags & ViewRootImpl.MSG_RESIZED) != 0) {
                forward(q);
            } else if (shouldDropInputEvent(q)) {
                finish(q, ViewRootImpl.LOCAL_LOGV);
            } else {
                apply(q, onProcess(q));
            }
        }

        protected void finish(QueuedInputEvent q, boolean handled) {
            q.mFlags |= ViewRootImpl.MSG_RESIZED;
            if (handled) {
                q.mFlags |= ViewRootImpl.MSG_DISPATCH_APP_VISIBILITY;
            }
            forward(q);
        }

        protected void forward(QueuedInputEvent q) {
            onDeliverToNext(q);
        }

        protected void apply(QueuedInputEvent q, int result) {
            if (result == 0) {
                forward(q);
            } else if (result == FINISH_HANDLED) {
                finish(q, true);
            } else if (result == FINISH_NOT_HANDLED) {
                finish(q, ViewRootImpl.LOCAL_LOGV);
            } else {
                throw new IllegalArgumentException("Invalid result: " + result);
            }
        }

        protected int onProcess(QueuedInputEvent q) {
            return 0;
        }

        protected void onDeliverToNext(QueuedInputEvent q) {
            if (this.mNext != null) {
                this.mNext.deliver(q);
            } else {
                this.this$0.finishInputEvent(q);
            }
        }

        protected boolean shouldDropInputEvent(QueuedInputEvent q) {
            if (this.this$0.mView == null || !this.this$0.mAdded) {
                Slog.w(ViewRootImpl.TAG, "Dropping event due to root view being removed: " + q.mEvent);
                return true;
            } else if ((this.this$0.mAttachInfo.mHasWindowFocus && !this.this$0.mStopped) || q.mEvent.isFromSource(FINISH_NOT_HANDLED)) {
                return ViewRootImpl.LOCAL_LOGV;
            } else {
                if (ViewRootImpl.isTerminalInputEvent(q.mEvent)) {
                    q.mEvent.cancel();
                    Slog.w(ViewRootImpl.TAG, "Cancelling event due to no window focus: " + q.mEvent);
                    return ViewRootImpl.LOCAL_LOGV;
                }
                Slog.w(ViewRootImpl.TAG, "Dropping event due to no window focus: " + q.mEvent);
                return true;
            }
        }

        void dump(String prefix, PrintWriter writer) {
            if (this.mNext != null) {
                this.mNext.dump(prefix, writer);
            }
        }
    }

    abstract class AsyncInputStage extends InputStage {
        protected static final int DEFER = 3;
        private QueuedInputEvent mQueueHead;
        private int mQueueLength;
        private QueuedInputEvent mQueueTail;
        private final String mTraceCounter;
        final /* synthetic */ ViewRootImpl this$0;

        public AsyncInputStage(ViewRootImpl viewRootImpl, InputStage next, String traceCounter) {
            this.this$0 = viewRootImpl;
            super(viewRootImpl, next);
            this.mTraceCounter = traceCounter;
        }

        protected void defer(QueuedInputEvent q) {
            q.mFlags |= ViewRootImpl.MSG_INVALIDATE_RECT;
            enqueue(q);
        }

        protected void forward(QueuedInputEvent q) {
            q.mFlags &= -3;
            QueuedInputEvent curr = this.mQueueHead;
            if (curr == null) {
                super.forward(q);
                return;
            }
            int deviceId = q.mEvent.getDeviceId();
            QueuedInputEvent prev = null;
            boolean blocked = ViewRootImpl.LOCAL_LOGV;
            while (curr != null && curr != q) {
                if (!blocked && deviceId == curr.mEvent.getDeviceId()) {
                    blocked = true;
                }
                prev = curr;
                curr = curr.mNext;
            }
            if (!blocked) {
                if (curr != null) {
                    curr = curr.mNext;
                    dequeue(q, prev);
                }
                super.forward(q);
                while (curr != null) {
                    if (deviceId != curr.mEvent.getDeviceId()) {
                        prev = curr;
                        curr = curr.mNext;
                    } else if ((curr.mFlags & ViewRootImpl.MSG_INVALIDATE_RECT) == 0) {
                        QueuedInputEvent next = curr.mNext;
                        dequeue(curr, prev);
                        super.forward(curr);
                        curr = next;
                    } else {
                        return;
                    }
                }
            } else if (curr == null) {
                enqueue(q);
            }
        }

        protected void apply(QueuedInputEvent q, int result) {
            if (result == DEFER) {
                defer(q);
            } else {
                super.apply(q, result);
            }
        }

        private void enqueue(QueuedInputEvent q) {
            if (this.mQueueTail == null) {
                this.mQueueHead = q;
                this.mQueueTail = q;
            } else {
                this.mQueueTail.mNext = q;
                this.mQueueTail = q;
            }
            this.mQueueLength += ViewRootImpl.MSG_INVALIDATE;
            Trace.traceCounter(4, this.mTraceCounter, this.mQueueLength);
        }

        private void dequeue(QueuedInputEvent q, QueuedInputEvent prev) {
            if (prev == null) {
                this.mQueueHead = q.mNext;
            } else {
                prev.mNext = q.mNext;
            }
            if (this.mQueueTail == q) {
                this.mQueueTail = prev;
            }
            q.mNext = null;
            this.mQueueLength--;
            Trace.traceCounter(4, this.mTraceCounter, this.mQueueLength);
        }

        void dump(String prefix, PrintWriter writer) {
            writer.print(prefix);
            writer.print(getClass().getName());
            writer.print(": mQueueLength=");
            writer.println(this.mQueueLength);
            super.dump(prefix, writer);
        }
    }

    public static final class CalledFromWrongThreadException extends AndroidRuntimeException {
        public CalledFromWrongThreadException(String msg) {
            super(msg);
        }
    }

    final class ConsumeBatchedInputImmediatelyRunnable implements Runnable {
        final /* synthetic */ ViewRootImpl this$0;

        ConsumeBatchedInputImmediatelyRunnable(ViewRootImpl viewRootImpl) {
            this.this$0 = viewRootImpl;
        }

        public void run() {
            this.this$0.doConsumeBatchedInput(-1);
        }
    }

    final class ConsumeBatchedInputRunnable implements Runnable {
        final /* synthetic */ ViewRootImpl this$0;

        ConsumeBatchedInputRunnable(ViewRootImpl viewRootImpl) {
            this.this$0 = viewRootImpl;
        }

        public void run() {
            this.this$0.doConsumeBatchedInput(this.this$0.mChoreographer.getFrameTimeNanos());
        }
    }

    final class EarlyPostImeInputStage extends InputStage {
        final /* synthetic */ ViewRootImpl this$0;

        public EarlyPostImeInputStage(ViewRootImpl viewRootImpl, InputStage next) {
            this.this$0 = viewRootImpl;
            super(viewRootImpl, next);
        }

        protected int onProcess(QueuedInputEvent q) {
            if (q.mEvent instanceof KeyEvent) {
                return processKeyEvent(q);
            }
            if ((q.mEvent.getSource() & ViewRootImpl.MSG_INVALIDATE_RECT) != 0) {
                return processPointerEvent(q);
            }
            return 0;
        }

        private int processKeyEvent(QueuedInputEvent q) {
            KeyEvent event = q.mEvent;
            if (this.this$0.checkForLeavingTouchModeAndConsume(event)) {
                return ViewRootImpl.MSG_INVALIDATE;
            }
            this.this$0.mFallbackEventHandler.preDispatchKeyEvent(event);
            return 0;
        }

        private int processPointerEvent(QueuedInputEvent q) {
            MotionEvent event = q.mEvent;
            if (this.this$0.mTranslator != null) {
                this.this$0.mTranslator.translateEventInScreenToAppWindow(event);
            }
            int action = event.getAction();
            if (action == 0 || action == ViewRootImpl.MSG_DISPATCH_APP_VISIBILITY) {
                this.this$0.ensureTouchMode(true);
            }
            if (this.this$0.mCurScrollY != 0) {
                event.offsetLocation(0.0f, (float) this.this$0.mCurScrollY);
            }
            if (event.isTouchEvent()) {
                this.this$0.mLastTouchPoint.x = event.getRawX();
                this.this$0.mLastTouchPoint.y = event.getRawY();
            }
            return 0;
        }
    }

    final class HighContrastTextManager implements HighTextContrastChangeListener {
        final /* synthetic */ ViewRootImpl this$0;

        HighContrastTextManager(ViewRootImpl viewRootImpl) {
            this.this$0 = viewRootImpl;
            viewRootImpl.mAttachInfo.mHighContrastText = viewRootImpl.mAccessibilityManager.isHighTextContrastEnabled();
        }

        public void onHighTextContrastStateChanged(boolean enabled) {
            this.this$0.mAttachInfo.mHighContrastText = enabled;
            this.this$0.destroyHardwareResources();
            this.this$0.invalidate();
        }
    }

    final class ImeInputStage extends AsyncInputStage implements FinishedInputEventCallback {
        final /* synthetic */ ViewRootImpl this$0;

        public ImeInputStage(ViewRootImpl viewRootImpl, InputStage next, String traceCounter) {
            this.this$0 = viewRootImpl;
            super(viewRootImpl, next, traceCounter);
        }

        protected int onProcess(QueuedInputEvent q) {
            if (this.this$0.mLastWasImTarget && !this.this$0.isInLocalFocusMode()) {
                InputMethodManager imm = InputMethodManager.peekInstance();
                if (imm != null) {
                    int result = imm.dispatchInputEvent(q.mEvent, q, this, this.this$0.mHandler);
                    if (result == ViewRootImpl.MSG_INVALIDATE) {
                        return ViewRootImpl.MSG_INVALIDATE;
                    }
                    if (result == 0) {
                        return 0;
                    }
                    return ViewRootImpl.MSG_DIE;
                }
            }
            return 0;
        }

        public void onFinishedInputEvent(Object token, boolean handled) {
            QueuedInputEvent q = (QueuedInputEvent) token;
            if (handled) {
                finish(q, true);
            } else {
                forward(q);
            }
        }
    }

    final class InvalidateOnAnimationRunnable implements Runnable {
        private boolean mPosted;
        private InvalidateInfo[] mTempViewRects;
        private View[] mTempViews;
        private final ArrayList<InvalidateInfo> mViewRects;
        private final ArrayList<View> mViews;
        final /* synthetic */ ViewRootImpl this$0;

        InvalidateOnAnimationRunnable(ViewRootImpl viewRootImpl) {
            this.this$0 = viewRootImpl;
            this.mViews = new ArrayList();
            this.mViewRects = new ArrayList();
        }

        public void addView(View view) {
            synchronized (this) {
                this.mViews.add(view);
                postIfNeededLocked();
            }
        }

        public void addViewRect(InvalidateInfo info) {
            synchronized (this) {
                this.mViewRects.add(info);
                postIfNeededLocked();
            }
        }

        public void removeView(View view) {
            synchronized (this) {
                this.mViews.remove(view);
                int i = this.mViewRects.size();
                while (true) {
                    int i2 = i - 1;
                    if (i <= 0) {
                        break;
                    }
                    InvalidateInfo info = (InvalidateInfo) this.mViewRects.get(i2);
                    if (info.target == view) {
                        this.mViewRects.remove(i2);
                        info.recycle();
                    }
                    i = i2;
                }
                if (this.mPosted && this.mViews.isEmpty() && this.mViewRects.isEmpty()) {
                    this.this$0.mChoreographer.removeCallbacks(ViewRootImpl.MSG_INVALIDATE, this, null);
                    this.mPosted = ViewRootImpl.LOCAL_LOGV;
                }
            }
        }

        public void run() {
            int i;
            synchronized (this) {
                this.mPosted = ViewRootImpl.LOCAL_LOGV;
                int viewCount = this.mViews.size();
                if (viewCount != 0) {
                    this.mTempViews = (View[]) this.mViews.toArray(this.mTempViews != null ? this.mTempViews : new View[viewCount]);
                    this.mViews.clear();
                }
                int viewRectCount = this.mViewRects.size();
                if (viewRectCount != 0) {
                    this.mTempViewRects = (InvalidateInfo[]) this.mViewRects.toArray(this.mTempViewRects != null ? this.mTempViewRects : new InvalidateInfo[viewRectCount]);
                    this.mViewRects.clear();
                }
            }
            for (i = 0; i < viewCount; i += ViewRootImpl.MSG_INVALIDATE) {
                this.mTempViews[i].invalidate();
                this.mTempViews[i] = null;
            }
            for (i = 0; i < viewRectCount; i += ViewRootImpl.MSG_INVALIDATE) {
                InvalidateInfo info = this.mTempViewRects[i];
                info.target.invalidate(info.left, info.top, info.right, info.bottom);
                info.recycle();
            }
        }

        private void postIfNeededLocked() {
            if (!this.mPosted) {
                this.this$0.mChoreographer.postCallback(ViewRootImpl.MSG_INVALIDATE, this, null);
                this.mPosted = true;
            }
        }
    }

    final class NativePostImeInputStage extends AsyncInputStage implements InputQueue.FinishedInputEventCallback {
        final /* synthetic */ ViewRootImpl this$0;

        public NativePostImeInputStage(ViewRootImpl viewRootImpl, InputStage next, String traceCounter) {
            this.this$0 = viewRootImpl;
            super(viewRootImpl, next, traceCounter);
        }

        protected int onProcess(QueuedInputEvent q) {
            if (this.this$0.mInputQueue == null) {
                return 0;
            }
            this.this$0.mInputQueue.sendInputEvent(q.mEvent, q, ViewRootImpl.LOCAL_LOGV, this);
            return ViewRootImpl.MSG_DIE;
        }

        public void onFinishedInputEvent(Object token, boolean handled) {
            QueuedInputEvent q = (QueuedInputEvent) token;
            if (handled) {
                finish(q, true);
            } else {
                forward(q);
            }
        }
    }

    final class NativePreImeInputStage extends AsyncInputStage implements InputQueue.FinishedInputEventCallback {
        final /* synthetic */ ViewRootImpl this$0;

        public NativePreImeInputStage(ViewRootImpl viewRootImpl, InputStage next, String traceCounter) {
            this.this$0 = viewRootImpl;
            super(viewRootImpl, next, traceCounter);
        }

        protected int onProcess(QueuedInputEvent q) {
            if (this.this$0.mInputQueue == null || !(q.mEvent instanceof KeyEvent)) {
                return 0;
            }
            this.this$0.mInputQueue.sendInputEvent(q.mEvent, q, true, this);
            return ViewRootImpl.MSG_DIE;
        }

        public void onFinishedInputEvent(Object token, boolean handled) {
            QueuedInputEvent q = (QueuedInputEvent) token;
            if (handled) {
                finish(q, true);
            } else {
                forward(q);
            }
        }
    }

    private static final class QueuedInputEvent {
        public static final int FLAG_DEFERRED = 2;
        public static final int FLAG_DELIVER_POST_IME = 1;
        public static final int FLAG_FINISHED = 4;
        public static final int FLAG_FINISHED_HANDLED = 8;
        public static final int FLAG_RESYNTHESIZED = 16;
        public static final int FLAG_UNHANDLED = 32;
        public InputEvent mEvent;
        public int mFlags;
        public QueuedInputEvent mNext;
        public InputEventReceiver mReceiver;

        private QueuedInputEvent() {
        }

        /* synthetic */ QueuedInputEvent(AnonymousClass1 x0) {
            this();
        }

        public boolean shouldSkipIme() {
            if ((this.mFlags & FLAG_DELIVER_POST_IME) != 0) {
                return true;
            }
            if ((this.mEvent instanceof MotionEvent) && this.mEvent.isFromSource(FLAG_DEFERRED)) {
                return true;
            }
            return ViewRootImpl.LOCAL_LOGV;
        }

        public boolean shouldSendToSynthesizer() {
            if ((this.mFlags & FLAG_UNHANDLED) != 0) {
                return true;
            }
            return ViewRootImpl.LOCAL_LOGV;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("QueuedInputEvent{flags=");
            if (!flagToString("UNHANDLED", FLAG_UNHANDLED, flagToString("RESYNTHESIZED", FLAG_RESYNTHESIZED, flagToString("FINISHED_HANDLED", FLAG_FINISHED_HANDLED, flagToString("FINISHED", FLAG_FINISHED, flagToString("DEFERRED", FLAG_DEFERRED, flagToString("DELIVER_POST_IME", FLAG_DELIVER_POST_IME, ViewRootImpl.LOCAL_LOGV, sb), sb), sb), sb), sb), sb)) {
                sb.append(WifiEnterpriseConfig.ENGINE_DISABLE);
            }
            sb.append(", hasNextQueuedEvent=" + (this.mEvent != null ? "true" : "false"));
            sb.append(", hasInputEventReceiver=" + (this.mReceiver != null ? "true" : "false"));
            sb.append(", mEvent=" + this.mEvent + "}");
            return sb.toString();
        }

        private boolean flagToString(String name, int flag, boolean hasPrevious, StringBuilder sb) {
            if ((this.mFlags & flag) == 0) {
                return hasPrevious;
            }
            if (hasPrevious) {
                sb.append("|");
            }
            sb.append(name);
            return true;
        }
    }

    static final class RunQueue {
        private final ArrayList<HandlerAction> mActions;

        private static class HandlerAction {
            Runnable action;
            long delay;

            private HandlerAction() {
            }

            /* synthetic */ HandlerAction(AnonymousClass1 x0) {
                this();
            }

            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return ViewRootImpl.LOCAL_LOGV;
                }
                HandlerAction that = (HandlerAction) o;
                if (this.action != null) {
                    if (this.action.equals(that.action)) {
                        return true;
                    }
                } else if (that.action == null) {
                    return true;
                }
                return ViewRootImpl.LOCAL_LOGV;
            }

            public int hashCode() {
                return ((this.action != null ? this.action.hashCode() : 0) * 31) + ((int) (this.delay ^ (this.delay >>> 32)));
            }
        }

        RunQueue() {
            this.mActions = new ArrayList();
        }

        void post(Runnable action) {
            postDelayed(action, 0);
        }

        void postDelayed(Runnable action, long delayMillis) {
            HandlerAction handlerAction = new HandlerAction();
            handlerAction.action = action;
            handlerAction.delay = delayMillis;
            synchronized (this.mActions) {
                this.mActions.add(handlerAction);
            }
        }

        void removeCallbacks(Runnable action) {
            HandlerAction handlerAction = new HandlerAction();
            handlerAction.action = action;
            synchronized (this.mActions) {
                do {
                } while (this.mActions.remove(handlerAction));
            }
        }

        void executeActions(Handler handler) {
            synchronized (this.mActions) {
                ArrayList<HandlerAction> actions = this.mActions;
                int count = actions.size();
                for (int i = 0; i < count; i += ViewRootImpl.MSG_INVALIDATE) {
                    HandlerAction handlerAction = (HandlerAction) actions.get(i);
                    handler.postDelayed(handlerAction.action, handlerAction.delay);
                }
                actions.clear();
            }
        }
    }

    private class SendWindowContentChangedAccessibilityEvent implements Runnable {
        private int mChangeTypes;
        public long mLastEventTimeMillis;
        public View mSource;
        final /* synthetic */ ViewRootImpl this$0;

        private SendWindowContentChangedAccessibilityEvent(android.view.ViewRootImpl r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.ViewRootImpl.SendWindowContentChangedAccessibilityEvent.<init>(android.view.ViewRootImpl):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.ViewRootImpl.SendWindowContentChangedAccessibilityEvent.<init>(android.view.ViewRootImpl):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.SendWindowContentChangedAccessibilityEvent.<init>(android.view.ViewRootImpl):void");
        }

        /* synthetic */ SendWindowContentChangedAccessibilityEvent(android.view.ViewRootImpl r1, android.view.ViewRootImpl.AnonymousClass1 r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.ViewRootImpl.SendWindowContentChangedAccessibilityEvent.<init>(android.view.ViewRootImpl, android.view.ViewRootImpl$1):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.ViewRootImpl.SendWindowContentChangedAccessibilityEvent.<init>(android.view.ViewRootImpl, android.view.ViewRootImpl$1):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.SendWindowContentChangedAccessibilityEvent.<init>(android.view.ViewRootImpl, android.view.ViewRootImpl$1):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.ViewRootImpl.SendWindowContentChangedAccessibilityEvent.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.ViewRootImpl.SendWindowContentChangedAccessibilityEvent.run():void
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
            throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.SendWindowContentChangedAccessibilityEvent.run():void");
        }

        public void runOrPost(android.view.View r1, int r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.ViewRootImpl.SendWindowContentChangedAccessibilityEvent.runOrPost(android.view.View, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.ViewRootImpl.SendWindowContentChangedAccessibilityEvent.runOrPost(android.view.View, int):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.SendWindowContentChangedAccessibilityEvent.runOrPost(android.view.View, int):void");
        }
    }

    final class SyntheticInputStage extends InputStage {
        private final SyntheticJoystickHandler mJoystick;
        private final SyntheticKeyboardHandler mKeyboard;
        private final SyntheticTouchNavigationHandler mTouchNavigation;
        private final SyntheticTrackballHandler mTrackball;
        final /* synthetic */ ViewRootImpl this$0;

        public SyntheticInputStage(ViewRootImpl viewRootImpl) {
            this.this$0 = viewRootImpl;
            super(viewRootImpl, null);
            this.mTrackball = new SyntheticTrackballHandler(this.this$0);
            this.mJoystick = new SyntheticJoystickHandler(this.this$0);
            this.mTouchNavigation = new SyntheticTouchNavigationHandler(this.this$0);
            this.mKeyboard = new SyntheticKeyboardHandler(this.this$0);
        }

        protected int onProcess(QueuedInputEvent q) {
            q.mFlags |= ViewRootImpl.MSG_DISPATCH_DRAG_LOCATION_EVENT;
            if (q.mEvent instanceof MotionEvent) {
                MotionEvent event = q.mEvent;
                int source = event.getSource();
                if ((source & ViewRootImpl.MSG_RESIZED) != 0) {
                    this.mTrackball.process(event);
                    return ViewRootImpl.MSG_INVALIDATE;
                } else if ((source & ViewRootImpl.MSG_DISPATCH_DRAG_LOCATION_EVENT) != 0) {
                    this.mJoystick.process(event);
                    return ViewRootImpl.MSG_INVALIDATE;
                } else if ((source & AccessibilityNodeInfo.ACTION_SET_TEXT) == AccessibilityNodeInfo.ACTION_SET_TEXT) {
                    this.mTouchNavigation.process(event);
                    return ViewRootImpl.MSG_INVALIDATE;
                }
            } else if ((q.mFlags & 32) != 0) {
                this.mKeyboard.process((KeyEvent) q.mEvent);
                return ViewRootImpl.MSG_INVALIDATE;
            }
            return 0;
        }

        protected void onDeliverToNext(QueuedInputEvent q) {
            if ((q.mFlags & ViewRootImpl.MSG_DISPATCH_DRAG_LOCATION_EVENT) == 0 && (q.mEvent instanceof MotionEvent)) {
                MotionEvent event = q.mEvent;
                int source = event.getSource();
                if ((source & ViewRootImpl.MSG_RESIZED) != 0) {
                    this.mTrackball.cancel(event);
                } else if ((source & ViewRootImpl.MSG_DISPATCH_DRAG_LOCATION_EVENT) != 0) {
                    this.mJoystick.cancel(event);
                } else if ((source & AccessibilityNodeInfo.ACTION_SET_TEXT) == AccessibilityNodeInfo.ACTION_SET_TEXT) {
                    this.mTouchNavigation.cancel(event);
                }
            }
            super.onDeliverToNext(q);
        }
    }

    final class SyntheticJoystickHandler extends Handler {
        private static final int MSG_ENQUEUE_X_AXIS_KEY_REPEAT = 1;
        private static final int MSG_ENQUEUE_Y_AXIS_KEY_REPEAT = 2;
        private static final String TAG = "SyntheticJoystickHandler";
        private int mLastXDirection;
        private int mLastXKeyCode;
        private int mLastYDirection;
        private int mLastYKeyCode;
        final /* synthetic */ ViewRootImpl this$0;

        public SyntheticJoystickHandler(ViewRootImpl viewRootImpl) {
            this.this$0 = viewRootImpl;
            super(true);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ENQUEUE_X_AXIS_KEY_REPEAT /*1*/:
                case MSG_ENQUEUE_Y_AXIS_KEY_REPEAT /*2*/:
                    KeyEvent oldEvent = msg.obj;
                    KeyEvent e = KeyEvent.changeTimeRepeat(oldEvent, SystemClock.uptimeMillis(), oldEvent.getRepeatCount() + MSG_ENQUEUE_X_AXIS_KEY_REPEAT);
                    if (this.this$0.mAttachInfo.mHasWindowFocus) {
                        this.this$0.enqueueInputEvent(e);
                        Message m = obtainMessage(msg.what, e);
                        m.setAsynchronous(true);
                        sendMessageDelayed(m, (long) ViewConfiguration.getKeyRepeatDelay());
                    }
                default:
            }
        }

        public void process(MotionEvent event) {
            switch (event.getActionMasked()) {
                case MSG_ENQUEUE_Y_AXIS_KEY_REPEAT /*2*/:
                    update(event, true);
                case ViewRootImpl.MSG_DIE /*3*/:
                    cancel(event);
                default:
                    Log.w(TAG, "Unexpected action: " + event.getActionMasked());
            }
        }

        private void cancel(MotionEvent event) {
            removeMessages(MSG_ENQUEUE_X_AXIS_KEY_REPEAT);
            removeMessages(MSG_ENQUEUE_Y_AXIS_KEY_REPEAT);
            update(event, ViewRootImpl.LOCAL_LOGV);
        }

        private void update(MotionEvent event, boolean synthesizeNewKeys) {
            long time = event.getEventTime();
            int metaState = event.getMetaState();
            int deviceId = event.getDeviceId();
            int source = event.getSource();
            int xDirection = joystickAxisValueToDirection(event.getAxisValue(ViewRootImpl.MSG_DISPATCH_DRAG_EVENT));
            if (xDirection == 0) {
                xDirection = joystickAxisValueToDirection(event.getX());
            }
            int yDirection = joystickAxisValueToDirection(event.getAxisValue(ViewRootImpl.MSG_DISPATCH_DRAG_LOCATION_EVENT));
            if (yDirection == 0) {
                yDirection = joystickAxisValueToDirection(event.getY());
            }
            if (xDirection != this.mLastXDirection) {
                if (this.mLastXKeyCode != 0) {
                    removeMessages(MSG_ENQUEUE_X_AXIS_KEY_REPEAT);
                    this.this$0.enqueueInputEvent(new KeyEvent(time, time, MSG_ENQUEUE_X_AXIS_KEY_REPEAT, this.mLastXKeyCode, 0, metaState, deviceId, 0, AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT, source));
                    this.mLastXKeyCode = 0;
                }
                this.mLastXDirection = xDirection;
                if (xDirection != 0 && synthesizeNewKeys) {
                    this.mLastXKeyCode = xDirection > 0 ? ViewRootImpl.MSG_DISPATCH_DONE_ANIMATING : ViewRootImpl.MSG_CLEAR_ACCESSIBILITY_FOCUS_HOST;
                    KeyEvent e = new KeyEvent(time, time, 0, this.mLastXKeyCode, 0, metaState, deviceId, 0, AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT, source);
                    this.this$0.enqueueInputEvent(e);
                    Message m = obtainMessage(MSG_ENQUEUE_X_AXIS_KEY_REPEAT, e);
                    m.setAsynchronous(true);
                    sendMessageDelayed(m, (long) ViewConfiguration.getKeyRepeatTimeout());
                }
            }
            if (yDirection != this.mLastYDirection) {
                if (this.mLastYKeyCode != 0) {
                    removeMessages(MSG_ENQUEUE_Y_AXIS_KEY_REPEAT);
                    this.this$0.enqueueInputEvent(new KeyEvent(time, time, MSG_ENQUEUE_X_AXIS_KEY_REPEAT, this.mLastYKeyCode, 0, metaState, deviceId, 0, AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT, source));
                    this.mLastYKeyCode = 0;
                }
                this.mLastYDirection = yDirection;
                if (yDirection != 0 && synthesizeNewKeys) {
                    this.mLastYKeyCode = yDirection > 0 ? 20 : ViewRootImpl.MSG_PROCESS_INPUT_EVENTS;
                    e = new KeyEvent(time, time, 0, this.mLastYKeyCode, 0, metaState, deviceId, 0, AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT, source);
                    this.this$0.enqueueInputEvent(e);
                    m = obtainMessage(MSG_ENQUEUE_Y_AXIS_KEY_REPEAT, e);
                    m.setAsynchronous(true);
                    sendMessageDelayed(m, (long) ViewConfiguration.getKeyRepeatTimeout());
                }
            }
        }

        private int joystickAxisValueToDirection(float value) {
            if (value >= 0.5f) {
                return MSG_ENQUEUE_X_AXIS_KEY_REPEAT;
            }
            if (value <= -0.5f) {
                return -1;
            }
            return 0;
        }
    }

    final class SyntheticKeyboardHandler {
        final /* synthetic */ ViewRootImpl this$0;

        SyntheticKeyboardHandler(ViewRootImpl viewRootImpl) {
            this.this$0 = viewRootImpl;
        }

        public void process(KeyEvent event) {
            if ((event.getFlags() & AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT) == 0) {
                FallbackAction fallbackAction = event.getKeyCharacterMap().getFallbackAction(event.getKeyCode(), event.getMetaState());
                if (fallbackAction != null) {
                    InputEvent fallbackEvent = KeyEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), fallbackAction.keyCode, event.getRepeatCount(), fallbackAction.metaState, event.getDeviceId(), event.getScanCode(), event.getFlags() | AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT, event.getSource(), null);
                    fallbackAction.recycle();
                    this.this$0.enqueueInputEvent(fallbackEvent);
                }
            }
        }
    }

    final class SyntheticTouchNavigationHandler extends Handler {
        private static final float DEFAULT_HEIGHT_MILLIMETERS = 48.0f;
        private static final float DEFAULT_WIDTH_MILLIMETERS = 48.0f;
        private static final float FLING_TICK_DECAY = 0.8f;
        private static final boolean LOCAL_DEBUG = false;
        private static final String LOCAL_TAG = "SyntheticTouchNavigationHandler";
        private static final float MAX_FLING_VELOCITY_TICKS_PER_SECOND = 20.0f;
        private static final float MIN_FLING_VELOCITY_TICKS_PER_SECOND = 6.0f;
        private static final int TICK_DISTANCE_MILLIMETERS = 12;
        private float mAccumulatedX;
        private float mAccumulatedY;
        private int mActivePointerId;
        private float mConfigMaxFlingVelocity;
        private float mConfigMinFlingVelocity;
        private float mConfigTickDistance;
        private boolean mConsumedMovement;
        private int mCurrentDeviceId;
        private boolean mCurrentDeviceSupported;
        private int mCurrentSource;
        private final Runnable mFlingRunnable;
        private float mFlingVelocity;
        private boolean mFlinging;
        private float mLastX;
        private float mLastY;
        private int mPendingKeyCode;
        private long mPendingKeyDownTime;
        private int mPendingKeyMetaState;
        private int mPendingKeyRepeatCount;
        private float mStartX;
        private float mStartY;
        private VelocityTracker mVelocityTracker;
        final /* synthetic */ ViewRootImpl this$0;

        /* renamed from: android.view.ViewRootImpl.SyntheticTouchNavigationHandler.1 */
        class AnonymousClass1 implements Runnable {
            final /* synthetic */ SyntheticTouchNavigationHandler this$1;

            AnonymousClass1(SyntheticTouchNavigationHandler syntheticTouchNavigationHandler) {
                this.this$1 = syntheticTouchNavigationHandler;
            }

            public void run() {
                long time = SystemClock.uptimeMillis();
                this.this$1.sendKeyDownOrRepeat(time, this.this$1.mPendingKeyCode, this.this$1.mPendingKeyMetaState);
                SyntheticTouchNavigationHandler.access$1332(this.this$1, SyntheticTouchNavigationHandler.FLING_TICK_DECAY);
                if (!this.this$1.postFling(time)) {
                    this.this$1.mFlinging = SyntheticTouchNavigationHandler.LOCAL_DEBUG;
                    this.this$1.finishKeys(time);
                }
            }
        }

        static /* synthetic */ float access$1332(SyntheticTouchNavigationHandler x0, float x1) {
            float f = x0.mFlingVelocity * x1;
            x0.mFlingVelocity = f;
            return f;
        }

        public SyntheticTouchNavigationHandler(ViewRootImpl viewRootImpl) {
            this.this$0 = viewRootImpl;
            super(true);
            this.mCurrentDeviceId = -1;
            this.mActivePointerId = -1;
            this.mPendingKeyCode = 0;
            this.mFlingRunnable = new AnonymousClass1(this);
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void process(android.view.MotionEvent r24) {
            /*
            r23 = this;
            r10 = r24.getEventTime();
            r5 = r24.getDeviceId();
            r9 = r24.getSource();
            r0 = r23;
            r0 = r0.mCurrentDeviceId;
            r20 = r0;
            r0 = r20;
            if (r0 != r5) goto L_0x0020;
        L_0x0016:
            r0 = r23;
            r0 = r0.mCurrentSource;
            r20 = r0;
            r0 = r20;
            if (r0 == r9) goto L_0x00b0;
        L_0x0020:
            r0 = r23;
            r0.finishKeys(r10);
            r0 = r23;
            r0.finishTracking(r10);
            r0 = r23;
            r0.mCurrentDeviceId = r5;
            r0 = r23;
            r0.mCurrentSource = r9;
            r20 = 0;
            r0 = r20;
            r1 = r23;
            r1.mCurrentDeviceSupported = r0;
            r4 = r24.getDevice();
            if (r4 == 0) goto L_0x00b0;
        L_0x0040:
            r20 = 0;
            r0 = r20;
            r15 = r4.getMotionRange(r0);
            r20 = 1;
            r0 = r20;
            r18 = r4.getMotionRange(r0);
            if (r15 == 0) goto L_0x00b0;
        L_0x0052:
            if (r18 == 0) goto L_0x00b0;
        L_0x0054:
            r20 = 1;
            r0 = r20;
            r1 = r23;
            r1.mCurrentDeviceSupported = r0;
            r16 = r15.getResolution();
            r20 = 0;
            r20 = (r16 > r20 ? 1 : (r16 == r20 ? 0 : -1));
            if (r20 > 0) goto L_0x006e;
        L_0x0066:
            r20 = r15.getRange();
            r21 = 1111490560; // 0x42400000 float:48.0 double:5.491493014E-315;
            r16 = r20 / r21;
        L_0x006e:
            r19 = r18.getResolution();
            r20 = 0;
            r20 = (r19 > r20 ? 1 : (r19 == r20 ? 0 : -1));
            if (r20 > 0) goto L_0x0080;
        L_0x0078:
            r20 = r18.getRange();
            r21 = 1111490560; // 0x42400000 float:48.0 double:5.491493014E-315;
            r19 = r20 / r21;
        L_0x0080:
            r20 = r16 + r19;
            r21 = 1056964608; // 0x3f000000 float:0.5 double:5.222099017E-315;
            r8 = r20 * r21;
            r20 = 1094713344; // 0x41400000 float:12.0 double:5.408602553E-315;
            r20 = r20 * r8;
            r0 = r20;
            r1 = r23;
            r1.mConfigTickDistance = r0;
            r20 = 1086324736; // 0x40c00000 float:6.0 double:5.367157323E-315;
            r0 = r23;
            r0 = r0.mConfigTickDistance;
            r21 = r0;
            r20 = r20 * r21;
            r0 = r20;
            r1 = r23;
            r1.mConfigMinFlingVelocity = r0;
            r20 = 1101004800; // 0x41a00000 float:20.0 double:5.439686476E-315;
            r0 = r23;
            r0 = r0.mConfigTickDistance;
            r21 = r0;
            r20 = r20 * r21;
            r0 = r20;
            r1 = r23;
            r1.mConfigMaxFlingVelocity = r0;
        L_0x00b0:
            r0 = r23;
            r0 = r0.mCurrentDeviceSupported;
            r20 = r0;
            if (r20 != 0) goto L_0x00b9;
        L_0x00b8:
            return;
        L_0x00b9:
            r2 = r24.getActionMasked();
            switch(r2) {
                case 0: goto L_0x00c1;
                case 1: goto L_0x0137;
                case 2: goto L_0x0137;
                case 3: goto L_0x020e;
                default: goto L_0x00c0;
            };
        L_0x00c0:
            goto L_0x00b8;
        L_0x00c1:
            r0 = r23;
            r3 = r0.mFlinging;
            r0 = r23;
            r0.finishKeys(r10);
            r0 = r23;
            r0.finishTracking(r10);
            r20 = 0;
            r0 = r24;
            r1 = r20;
            r20 = r0.getPointerId(r1);
            r0 = r20;
            r1 = r23;
            r1.mActivePointerId = r0;
            r20 = android.view.VelocityTracker.obtain();
            r0 = r20;
            r1 = r23;
            r1.mVelocityTracker = r0;
            r0 = r23;
            r0 = r0.mVelocityTracker;
            r20 = r0;
            r0 = r20;
            r1 = r24;
            r0.addMovement(r1);
            r20 = r24.getX();
            r0 = r20;
            r1 = r23;
            r1.mStartX = r0;
            r20 = r24.getY();
            r0 = r20;
            r1 = r23;
            r1.mStartY = r0;
            r0 = r23;
            r0 = r0.mStartX;
            r20 = r0;
            r0 = r20;
            r1 = r23;
            r1.mLastX = r0;
            r0 = r23;
            r0 = r0.mStartY;
            r20 = r0;
            r0 = r20;
            r1 = r23;
            r1.mLastY = r0;
            r20 = 0;
            r0 = r20;
            r1 = r23;
            r1.mAccumulatedX = r0;
            r20 = 0;
            r0 = r20;
            r1 = r23;
            r1.mAccumulatedY = r0;
            r0 = r23;
            r0.mConsumedMovement = r3;
            goto L_0x00b8;
        L_0x0137:
            r0 = r23;
            r0 = r0.mActivePointerId;
            r20 = r0;
            if (r20 < 0) goto L_0x00b8;
        L_0x013f:
            r0 = r23;
            r0 = r0.mActivePointerId;
            r20 = r0;
            r0 = r24;
            r1 = r20;
            r6 = r0.findPointerIndex(r1);
            if (r6 >= 0) goto L_0x015b;
        L_0x014f:
            r0 = r23;
            r0.finishKeys(r10);
            r0 = r23;
            r0.finishTracking(r10);
            goto L_0x00b8;
        L_0x015b:
            r0 = r23;
            r0 = r0.mVelocityTracker;
            r20 = r0;
            r0 = r20;
            r1 = r24;
            r0.addMovement(r1);
            r0 = r24;
            r14 = r0.getX(r6);
            r0 = r24;
            r17 = r0.getY(r6);
            r0 = r23;
            r0 = r0.mAccumulatedX;
            r20 = r0;
            r0 = r23;
            r0 = r0.mLastX;
            r21 = r0;
            r21 = r14 - r21;
            r20 = r20 + r21;
            r0 = r20;
            r1 = r23;
            r1.mAccumulatedX = r0;
            r0 = r23;
            r0 = r0.mAccumulatedY;
            r20 = r0;
            r0 = r23;
            r0 = r0.mLastY;
            r21 = r0;
            r21 = r17 - r21;
            r20 = r20 + r21;
            r0 = r20;
            r1 = r23;
            r1.mAccumulatedY = r0;
            r0 = r23;
            r0.mLastX = r14;
            r0 = r17;
            r1 = r23;
            r1.mLastY = r0;
            r7 = r24.getMetaState();
            r0 = r23;
            r0.consumeAccumulatedMovement(r10, r7);
            r20 = 1;
            r0 = r20;
            if (r2 != r0) goto L_0x00b8;
        L_0x01b9:
            r0 = r23;
            r0 = r0.mConsumedMovement;
            r20 = r0;
            if (r20 == 0) goto L_0x0207;
        L_0x01c1:
            r0 = r23;
            r0 = r0.mPendingKeyCode;
            r20 = r0;
            if (r20 == 0) goto L_0x0207;
        L_0x01c9:
            r0 = r23;
            r0 = r0.mVelocityTracker;
            r20 = r0;
            r21 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
            r0 = r23;
            r0 = r0.mConfigMaxFlingVelocity;
            r22 = r0;
            r20.computeCurrentVelocity(r21, r22);
            r0 = r23;
            r0 = r0.mVelocityTracker;
            r20 = r0;
            r0 = r23;
            r0 = r0.mActivePointerId;
            r21 = r0;
            r12 = r20.getXVelocity(r21);
            r0 = r23;
            r0 = r0.mVelocityTracker;
            r20 = r0;
            r0 = r23;
            r0 = r0.mActivePointerId;
            r21 = r0;
            r13 = r20.getYVelocity(r21);
            r0 = r23;
            r20 = r0.startFling(r10, r12, r13);
            if (r20 != 0) goto L_0x0207;
        L_0x0202:
            r0 = r23;
            r0.finishKeys(r10);
        L_0x0207:
            r0 = r23;
            r0.finishTracking(r10);
            goto L_0x00b8;
        L_0x020e:
            r0 = r23;
            r0.finishKeys(r10);
            r0 = r23;
            r0.finishTracking(r10);
            goto L_0x00b8;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.SyntheticTouchNavigationHandler.process(android.view.MotionEvent):void");
        }

        public void cancel(MotionEvent event) {
            if (this.mCurrentDeviceId == event.getDeviceId() && this.mCurrentSource == event.getSource()) {
                long time = event.getEventTime();
                finishKeys(time);
                finishTracking(time);
            }
        }

        private void finishKeys(long time) {
            cancelFling();
            sendKeyUp(time);
        }

        private void finishTracking(long time) {
            if (this.mActivePointerId >= 0) {
                this.mActivePointerId = -1;
                this.mVelocityTracker.recycle();
                this.mVelocityTracker = null;
            }
        }

        private void consumeAccumulatedMovement(long time, int metaState) {
            float absX = Math.abs(this.mAccumulatedX);
            float absY = Math.abs(this.mAccumulatedY);
            if (absX >= absY) {
                if (absX >= this.mConfigTickDistance) {
                    this.mAccumulatedX = consumeAccumulatedMovement(time, metaState, this.mAccumulatedX, ViewRootImpl.MSG_CLEAR_ACCESSIBILITY_FOCUS_HOST, ViewRootImpl.MSG_DISPATCH_DONE_ANIMATING);
                    this.mAccumulatedY = 0.0f;
                    this.mConsumedMovement = true;
                }
            } else if (absY >= this.mConfigTickDistance) {
                this.mAccumulatedY = consumeAccumulatedMovement(time, metaState, this.mAccumulatedY, ViewRootImpl.MSG_PROCESS_INPUT_EVENTS, 20);
                this.mAccumulatedX = 0.0f;
                this.mConsumedMovement = true;
            }
        }

        private float consumeAccumulatedMovement(long time, int metaState, float accumulator, int negativeKeyCode, int positiveKeyCode) {
            while (accumulator <= (-this.mConfigTickDistance)) {
                sendKeyDownOrRepeat(time, negativeKeyCode, metaState);
                accumulator += this.mConfigTickDistance;
            }
            while (accumulator >= this.mConfigTickDistance) {
                sendKeyDownOrRepeat(time, positiveKeyCode, metaState);
                accumulator -= this.mConfigTickDistance;
            }
            return accumulator;
        }

        private void sendKeyDownOrRepeat(long time, int keyCode, int metaState) {
            if (this.mPendingKeyCode != keyCode) {
                sendKeyUp(time);
                this.mPendingKeyDownTime = time;
                this.mPendingKeyCode = keyCode;
                this.mPendingKeyRepeatCount = 0;
            } else {
                this.mPendingKeyRepeatCount += ViewRootImpl.MSG_INVALIDATE;
            }
            this.mPendingKeyMetaState = metaState;
            this.this$0.enqueueInputEvent(new KeyEvent(this.mPendingKeyDownTime, time, 0, this.mPendingKeyCode, this.mPendingKeyRepeatCount, this.mPendingKeyMetaState, this.mCurrentDeviceId, AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT, this.mCurrentSource));
        }

        private void sendKeyUp(long time) {
            if (this.mPendingKeyCode != 0) {
                this.this$0.enqueueInputEvent(new KeyEvent(this.mPendingKeyDownTime, time, ViewRootImpl.MSG_INVALIDATE, this.mPendingKeyCode, 0, this.mPendingKeyMetaState, this.mCurrentDeviceId, 0, AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT, this.mCurrentSource));
                this.mPendingKeyCode = 0;
            }
        }

        private boolean startFling(long time, float vx, float vy) {
            switch (this.mPendingKeyCode) {
                case ViewRootImpl.MSG_PROCESS_INPUT_EVENTS /*19*/:
                    if ((-vy) >= this.mConfigMinFlingVelocity && Math.abs(vx) < this.mConfigMinFlingVelocity) {
                        this.mFlingVelocity = -vy;
                        break;
                    }
                    return LOCAL_DEBUG;
                case RelativeLayout.ALIGN_PARENT_START /*20*/:
                    if (vy >= this.mConfigMinFlingVelocity && Math.abs(vx) < this.mConfigMinFlingVelocity) {
                        this.mFlingVelocity = vy;
                        break;
                    }
                    return LOCAL_DEBUG;
                    break;
                case ViewRootImpl.MSG_CLEAR_ACCESSIBILITY_FOCUS_HOST /*21*/:
                    if ((-vx) >= this.mConfigMinFlingVelocity && Math.abs(vy) < this.mConfigMinFlingVelocity) {
                        this.mFlingVelocity = -vx;
                        break;
                    }
                    return LOCAL_DEBUG;
                case ViewRootImpl.MSG_DISPATCH_DONE_ANIMATING /*22*/:
                    if (vx >= this.mConfigMinFlingVelocity && Math.abs(vy) < this.mConfigMinFlingVelocity) {
                        this.mFlingVelocity = vx;
                        break;
                    }
                    return LOCAL_DEBUG;
            }
            this.mFlinging = postFling(time);
            return this.mFlinging;
        }

        private boolean postFling(long time) {
            if (this.mFlingVelocity < this.mConfigMinFlingVelocity) {
                return LOCAL_DEBUG;
            }
            postAtTime(this.mFlingRunnable, time + ((long) ((this.mConfigTickDistance / this.mFlingVelocity) * 1000.0f)));
            return true;
        }

        private void cancelFling() {
            if (this.mFlinging) {
                removeCallbacks(this.mFlingRunnable);
                this.mFlinging = LOCAL_DEBUG;
            }
        }
    }

    final class SyntheticTrackballHandler {
        private long mLastTime;
        private final TrackballAxis mX;
        private final TrackballAxis mY;
        final /* synthetic */ ViewRootImpl this$0;

        SyntheticTrackballHandler(ViewRootImpl viewRootImpl) {
            this.this$0 = viewRootImpl;
            this.mX = new TrackballAxis();
            this.mY = new TrackballAxis();
        }

        public void process(MotionEvent event) {
            long curTime = SystemClock.uptimeMillis();
            if (this.mLastTime + 250 < curTime) {
                this.mX.reset(0);
                this.mY.reset(0);
                this.mLastTime = curTime;
            }
            int action = event.getAction();
            int metaState = event.getMetaState();
            switch (action) {
                case Toast.LENGTH_SHORT /*0*/:
                    this.mX.reset(ViewRootImpl.MSG_INVALIDATE_RECT);
                    this.mY.reset(ViewRootImpl.MSG_INVALIDATE_RECT);
                    this.this$0.enqueueInputEvent(new KeyEvent(curTime, curTime, 0, ViewRootImpl.MSG_INVALIDATE_WORLD, 0, metaState, -1, 0, AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT, KeyEvent.KEYCODE_TV_MEDIA_CONTEXT_MENU));
                    break;
                case ViewRootImpl.MSG_INVALIDATE /*1*/:
                    this.mX.reset(ViewRootImpl.MSG_INVALIDATE_RECT);
                    this.mY.reset(ViewRootImpl.MSG_INVALIDATE_RECT);
                    this.this$0.enqueueInputEvent(new KeyEvent(curTime, curTime, ViewRootImpl.MSG_INVALIDATE, ViewRootImpl.MSG_INVALIDATE_WORLD, 0, metaState, -1, 0, AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT, KeyEvent.KEYCODE_TV_MEDIA_CONTEXT_MENU));
                    break;
            }
            float xOff = this.mX.collect(event.getX(), event.getEventTime(), "X");
            float yOff = this.mY.collect(event.getY(), event.getEventTime(), "Y");
            int keycode = 0;
            int movement = 0;
            float accel = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
            if (xOff > yOff) {
                movement = this.mX.generate();
                if (movement != 0) {
                    keycode = movement > 0 ? ViewRootImpl.MSG_DISPATCH_DONE_ANIMATING : ViewRootImpl.MSG_CLEAR_ACCESSIBILITY_FOCUS_HOST;
                    accel = this.mX.acceleration;
                    this.mY.reset(ViewRootImpl.MSG_INVALIDATE_RECT);
                }
            } else if (yOff > 0.0f) {
                movement = this.mY.generate();
                if (movement != 0) {
                    keycode = movement > 0 ? 20 : ViewRootImpl.MSG_PROCESS_INPUT_EVENTS;
                    accel = this.mY.acceleration;
                    this.mX.reset(ViewRootImpl.MSG_INVALIDATE_RECT);
                }
            }
            if (keycode != 0) {
                if (movement < 0) {
                    movement = -movement;
                }
                int accelMovement = (int) (((float) movement) * accel);
                if (accelMovement > movement) {
                    movement--;
                    this.this$0.enqueueInputEvent(new KeyEvent(curTime, curTime, ViewRootImpl.MSG_INVALIDATE_RECT, keycode, accelMovement - movement, metaState, -1, 0, AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT, KeyEvent.KEYCODE_TV_MEDIA_CONTEXT_MENU));
                }
                while (movement > 0) {
                    movement--;
                    curTime = SystemClock.uptimeMillis();
                    this.this$0.enqueueInputEvent(new KeyEvent(curTime, curTime, 0, keycode, 0, metaState, -1, 0, AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT, KeyEvent.KEYCODE_TV_MEDIA_CONTEXT_MENU));
                    this.this$0.enqueueInputEvent(new KeyEvent(curTime, curTime, ViewRootImpl.MSG_INVALIDATE, keycode, 0, metaState, -1, 0, AccessibilityNodeInfo.ACTION_NEXT_HTML_ELEMENT, KeyEvent.KEYCODE_TV_MEDIA_CONTEXT_MENU));
                }
                this.mLastTime = curTime;
            }
        }

        public void cancel(MotionEvent event) {
            this.mLastTime = -2147483648L;
            if (this.this$0.mView != null && this.this$0.mAdded) {
                this.this$0.ensureTouchMode(ViewRootImpl.LOCAL_LOGV);
            }
        }
    }

    static final class SystemUiVisibilityInfo {
        int globalVisibility;
        int localChanges;
        int localValue;
        int seq;

        SystemUiVisibilityInfo() {
        }
    }

    class TakenSurfaceHolder extends BaseSurfaceHolder {
        final /* synthetic */ ViewRootImpl this$0;

        TakenSurfaceHolder(android.view.ViewRootImpl r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.ViewRootImpl.TakenSurfaceHolder.<init>(android.view.ViewRootImpl):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.ViewRootImpl.TakenSurfaceHolder.<init>(android.view.ViewRootImpl):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.TakenSurfaceHolder.<init>(android.view.ViewRootImpl):void");
        }

        public boolean isCreating() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.ViewRootImpl.TakenSurfaceHolder.isCreating():boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.ViewRootImpl.TakenSurfaceHolder.isCreating():boolean
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
            throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.TakenSurfaceHolder.isCreating():boolean");
        }

        public boolean onAllowLockCanvas() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.ViewRootImpl.TakenSurfaceHolder.onAllowLockCanvas():boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.ViewRootImpl.TakenSurfaceHolder.onAllowLockCanvas():boolean
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
            throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.TakenSurfaceHolder.onAllowLockCanvas():boolean");
        }

        public void setFormat(int r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.ViewRootImpl.TakenSurfaceHolder.setFormat(int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.ViewRootImpl.TakenSurfaceHolder.setFormat(int):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.TakenSurfaceHolder.setFormat(int):void");
        }

        public void setKeepScreenOn(boolean r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.ViewRootImpl.TakenSurfaceHolder.setKeepScreenOn(boolean):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.ViewRootImpl.TakenSurfaceHolder.setKeepScreenOn(boolean):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.TakenSurfaceHolder.setKeepScreenOn(boolean):void");
        }

        public void setType(int r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.view.ViewRootImpl.TakenSurfaceHolder.setType(int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.view.ViewRootImpl.TakenSurfaceHolder.setType(int):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.view.ViewRootImpl.TakenSurfaceHolder.setType(int):void");
        }

        public void onRelayoutContainer() {
        }

        public void onUpdateSurface() {
            throw new IllegalStateException("Shouldn't be here");
        }

        public void setFixedSize(int width, int height) {
            throw new UnsupportedOperationException("Currently only support sizing from layout");
        }
    }

    static final class TrackballAxis {
        static final float ACCEL_MOVE_SCALING_FACTOR = 0.025f;
        static final long FAST_MOVE_TIME = 150;
        static final float FIRST_MOVEMENT_THRESHOLD = 0.5f;
        static final float MAX_ACCELERATION = 20.0f;
        static final float SECOND_CUMULATIVE_MOVEMENT_THRESHOLD = 2.0f;
        static final float SUBSEQUENT_INCREMENTAL_MOVEMENT_THRESHOLD = 1.0f;
        float acceleration;
        int dir;
        long lastMoveTime;
        int nonAccelMovement;
        float position;
        int step;

        TrackballAxis() {
            this.acceleration = SUBSEQUENT_INCREMENTAL_MOVEMENT_THRESHOLD;
            this.lastMoveTime = 0;
        }

        void reset(int _step) {
            this.position = 0.0f;
            this.acceleration = SUBSEQUENT_INCREMENTAL_MOVEMENT_THRESHOLD;
            this.lastMoveTime = 0;
            this.step = _step;
            this.dir = 0;
        }

        float collect(float off, long time, String axis) {
            long normTime;
            if (off > 0.0f) {
                normTime = (long) (150.0f * off);
                if (this.dir < 0) {
                    this.position = 0.0f;
                    this.step = 0;
                    this.acceleration = SUBSEQUENT_INCREMENTAL_MOVEMENT_THRESHOLD;
                    this.lastMoveTime = 0;
                }
                this.dir = ViewRootImpl.MSG_INVALIDATE;
            } else if (off < 0.0f) {
                normTime = (long) ((-off) * 150.0f);
                if (this.dir > 0) {
                    this.position = 0.0f;
                    this.step = 0;
                    this.acceleration = SUBSEQUENT_INCREMENTAL_MOVEMENT_THRESHOLD;
                    this.lastMoveTime = 0;
                }
                this.dir = -1;
            } else {
                normTime = 0;
            }
            if (normTime > 0) {
                long delta = time - this.lastMoveTime;
                this.lastMoveTime = time;
                float acc = this.acceleration;
                float scale;
                if (delta < normTime) {
                    scale = ((float) (normTime - delta)) * ACCEL_MOVE_SCALING_FACTOR;
                    if (scale > SUBSEQUENT_INCREMENTAL_MOVEMENT_THRESHOLD) {
                        acc *= scale;
                    }
                    if (acc >= MAX_ACCELERATION) {
                        acc = MAX_ACCELERATION;
                    }
                    this.acceleration = acc;
                } else {
                    scale = ((float) (delta - normTime)) * ACCEL_MOVE_SCALING_FACTOR;
                    if (scale > SUBSEQUENT_INCREMENTAL_MOVEMENT_THRESHOLD) {
                        acc /= scale;
                    }
                    if (acc <= SUBSEQUENT_INCREMENTAL_MOVEMENT_THRESHOLD) {
                        acc = SUBSEQUENT_INCREMENTAL_MOVEMENT_THRESHOLD;
                    }
                    this.acceleration = acc;
                }
            }
            this.position += off;
            return Math.abs(this.position);
        }

        int generate() {
            int movement = 0;
            this.nonAccelMovement = 0;
            while (true) {
                int dir = this.position >= 0.0f ? ViewRootImpl.MSG_INVALIDATE : -1;
                switch (this.step) {
                    case Toast.LENGTH_SHORT /*0*/:
                        if (Math.abs(this.position) < FIRST_MOVEMENT_THRESHOLD) {
                            break;
                        }
                        movement += dir;
                        this.nonAccelMovement += dir;
                        this.step = ViewRootImpl.MSG_INVALIDATE;
                        continue;
                    case ViewRootImpl.MSG_INVALIDATE /*1*/:
                        if (Math.abs(this.position) < SECOND_CUMULATIVE_MOVEMENT_THRESHOLD) {
                            break;
                        }
                        movement += dir;
                        this.nonAccelMovement += dir;
                        this.position -= ((float) dir) * SECOND_CUMULATIVE_MOVEMENT_THRESHOLD;
                        this.step = ViewRootImpl.MSG_INVALIDATE_RECT;
                        continue;
                    default:
                        if (Math.abs(this.position) < SUBSEQUENT_INCREMENTAL_MOVEMENT_THRESHOLD) {
                            break;
                        }
                        movement += dir;
                        this.position -= ((float) dir) * SUBSEQUENT_INCREMENTAL_MOVEMENT_THRESHOLD;
                        float acc = this.acceleration * 1.1f;
                        if (acc >= MAX_ACCELERATION) {
                            acc = this.acceleration;
                        }
                        this.acceleration = acc;
                        continue;
                }
                return movement;
            }
        }
    }

    final class TraversalRunnable implements Runnable {
        final /* synthetic */ ViewRootImpl this$0;

        TraversalRunnable(ViewRootImpl viewRootImpl) {
            this.this$0 = viewRootImpl;
        }

        public void run() {
            this.this$0.doTraversal();
        }
    }

    final class ViewPostImeInputStage extends InputStage {
        final /* synthetic */ ViewRootImpl this$0;

        public ViewPostImeInputStage(ViewRootImpl viewRootImpl, InputStage next) {
            this.this$0 = viewRootImpl;
            super(viewRootImpl, next);
        }

        protected int onProcess(QueuedInputEvent q) {
            if (q.mEvent instanceof KeyEvent) {
                return processKeyEvent(q);
            }
            this.this$0.handleDispatchDoneAnimating();
            int source = q.mEvent.getSource();
            if ((source & ViewRootImpl.MSG_INVALIDATE_RECT) != 0) {
                return processPointerEvent(q);
            }
            if ((source & ViewRootImpl.MSG_RESIZED) != 0) {
                return processTrackballEvent(q);
            }
            return processGenericMotionEvent(q);
        }

        protected void onDeliverToNext(QueuedInputEvent q) {
            if (this.this$0.mUnbufferedInputDispatch && (q.mEvent instanceof MotionEvent) && ((MotionEvent) q.mEvent).isTouchEvent() && ViewRootImpl.isTerminalInputEvent(q.mEvent)) {
                this.this$0.mUnbufferedInputDispatch = ViewRootImpl.LOCAL_LOGV;
                this.this$0.scheduleConsumeBatchedInput();
            }
            super.onDeliverToNext(q);
        }

        private int processKeyEvent(QueuedInputEvent q) {
            KeyEvent event = q.mEvent;
            if (event.getAction() != ViewRootImpl.MSG_INVALIDATE) {
                this.this$0.handleDispatchDoneAnimating();
            }
            if (this.this$0.mView.dispatchKeyEvent(event)) {
                return ViewRootImpl.MSG_INVALIDATE;
            }
            if (shouldDropInputEvent(q)) {
                return ViewRootImpl.MSG_INVALIDATE_RECT;
            }
            if (event.getAction() == 0 && event.isCtrlPressed() && event.getRepeatCount() == 0 && !KeyEvent.isModifierKey(event.getKeyCode())) {
                if (this.this$0.mView.dispatchKeyShortcutEvent(event)) {
                    return ViewRootImpl.MSG_INVALIDATE;
                }
                if (shouldDropInputEvent(q)) {
                    return ViewRootImpl.MSG_INVALIDATE_RECT;
                }
            }
            if (this.this$0.mFallbackEventHandler.dispatchKeyEvent(event)) {
                return ViewRootImpl.MSG_INVALIDATE;
            }
            if (shouldDropInputEvent(q)) {
                return ViewRootImpl.MSG_INVALIDATE_RECT;
            }
            if (event.getAction() == 0) {
                int direction = 0;
                switch (event.getKeyCode()) {
                    case ViewRootImpl.MSG_PROCESS_INPUT_EVENTS /*19*/:
                        if (event.hasNoModifiers()) {
                            direction = 33;
                            break;
                        }
                        break;
                    case RelativeLayout.ALIGN_PARENT_START /*20*/:
                        if (event.hasNoModifiers()) {
                            direction = KeyEvent.KEYCODE_MEDIA_RECORD;
                            break;
                        }
                        break;
                    case ViewRootImpl.MSG_CLEAR_ACCESSIBILITY_FOCUS_HOST /*21*/:
                        if (event.hasNoModifiers()) {
                            direction = ViewRootImpl.MSG_DISPATCH_SYSTEM_UI_VISIBILITY;
                            break;
                        }
                        break;
                    case ViewRootImpl.MSG_DISPATCH_DONE_ANIMATING /*22*/:
                        if (event.hasNoModifiers()) {
                            direction = 66;
                            break;
                        }
                        break;
                    case KeyEvent.KEYCODE_TAB /*61*/:
                        if (!event.hasNoModifiers()) {
                            if (event.hasModifiers(ViewRootImpl.MSG_INVALIDATE)) {
                                direction = ViewRootImpl.MSG_INVALIDATE;
                                break;
                            }
                        }
                        direction = ViewRootImpl.MSG_INVALIDATE_RECT;
                        break;
                        break;
                }
                if (direction != 0) {
                    View focused = this.this$0.mView.findFocus();
                    View v;
                    if (focused != null) {
                        v = focused.focusSearch(direction);
                        if (!(v == null || v == focused)) {
                            focused.getFocusedRect(this.this$0.mTempRect);
                            if (this.this$0.mView instanceof ViewGroup) {
                                ((ViewGroup) this.this$0.mView).offsetDescendantRectToMyCoords(focused, this.this$0.mTempRect);
                                ((ViewGroup) this.this$0.mView).offsetRectIntoDescendantCoords(v, this.this$0.mTempRect);
                            }
                            if (v.requestFocus(direction, this.this$0.mTempRect)) {
                                this.this$0.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
                                return ViewRootImpl.MSG_INVALIDATE;
                            }
                        }
                        if (this.this$0.mView.dispatchUnhandledMove(focused, direction)) {
                            return ViewRootImpl.MSG_INVALIDATE;
                        }
                    }
                    v = this.this$0.focusSearch(null, direction);
                    if (v != null && v.requestFocus(direction)) {
                        return ViewRootImpl.MSG_INVALIDATE;
                    }
                }
            }
            return 0;
        }

        private int processPointerEvent(QueuedInputEvent q) {
            MotionEvent event = q.mEvent;
            this.this$0.mAttachInfo.mUnbufferedDispatchRequested = ViewRootImpl.LOCAL_LOGV;
            boolean handled = this.this$0.mView.dispatchPointerEvent(event);
            if (this.this$0.mAttachInfo.mUnbufferedDispatchRequested && !this.this$0.mUnbufferedInputDispatch) {
                this.this$0.mUnbufferedInputDispatch = true;
                if (this.this$0.mConsumeBatchedInputScheduled) {
                    this.this$0.scheduleConsumeBatchedInputImmediately();
                }
            }
            if (handled) {
                return ViewRootImpl.MSG_INVALIDATE;
            }
            return 0;
        }

        private int processTrackballEvent(QueuedInputEvent q) {
            if (this.this$0.mView.dispatchTrackballEvent(q.mEvent)) {
                return ViewRootImpl.MSG_INVALIDATE;
            }
            return 0;
        }

        private int processGenericMotionEvent(QueuedInputEvent q) {
            if (this.this$0.mView.dispatchGenericMotionEvent(q.mEvent)) {
                return ViewRootImpl.MSG_INVALIDATE;
            }
            return 0;
        }
    }

    final class ViewPreImeInputStage extends InputStage {
        final /* synthetic */ ViewRootImpl this$0;

        public ViewPreImeInputStage(ViewRootImpl viewRootImpl, InputStage next) {
            this.this$0 = viewRootImpl;
            super(viewRootImpl, next);
        }

        protected int onProcess(QueuedInputEvent q) {
            if (q.mEvent instanceof KeyEvent) {
                return processKeyEvent(q);
            }
            return 0;
        }

        private int processKeyEvent(QueuedInputEvent q) {
            if (this.this$0.mView.dispatchKeyEventPreIme(q.mEvent)) {
                return ViewRootImpl.MSG_INVALIDATE;
            }
            return 0;
        }
    }

    final class ViewRootHandler extends Handler {
        final /* synthetic */ ViewRootImpl this$0;

        ViewRootHandler(ViewRootImpl viewRootImpl) {
            this.this$0 = viewRootImpl;
        }

        public String getMessageName(Message message) {
            switch (message.what) {
                case ViewRootImpl.MSG_INVALIDATE /*1*/:
                    return "MSG_INVALIDATE";
                case ViewRootImpl.MSG_INVALIDATE_RECT /*2*/:
                    return "MSG_INVALIDATE_RECT";
                case ViewRootImpl.MSG_DIE /*3*/:
                    return "MSG_DIE";
                case ViewRootImpl.MSG_RESIZED /*4*/:
                    return "MSG_RESIZED";
                case ViewRootImpl.MSG_RESIZED_REPORT /*5*/:
                    return "MSG_RESIZED_REPORT";
                case ViewRootImpl.MSG_WINDOW_FOCUS_CHANGED /*6*/:
                    return "MSG_WINDOW_FOCUS_CHANGED";
                case ViewRootImpl.MSG_DISPATCH_INPUT_EVENT /*7*/:
                    return "MSG_DISPATCH_INPUT_EVENT";
                case ViewRootImpl.MSG_DISPATCH_APP_VISIBILITY /*8*/:
                    return "MSG_DISPATCH_APP_VISIBILITY";
                case ViewRootImpl.MSG_DISPATCH_GET_NEW_SURFACE /*9*/:
                    return "MSG_DISPATCH_GET_NEW_SURFACE";
                case ViewRootImpl.MSG_DISPATCH_KEY_FROM_IME /*11*/:
                    return "MSG_DISPATCH_KEY_FROM_IME";
                case ViewRootImpl.MSG_FINISH_INPUT_CONNECTION /*12*/:
                    return "MSG_FINISH_INPUT_CONNECTION";
                case ViewRootImpl.MSG_CHECK_FOCUS /*13*/:
                    return "MSG_CHECK_FOCUS";
                case ViewRootImpl.MSG_CLOSE_SYSTEM_DIALOGS /*14*/:
                    return "MSG_CLOSE_SYSTEM_DIALOGS";
                case ViewRootImpl.MSG_DISPATCH_DRAG_EVENT /*15*/:
                    return "MSG_DISPATCH_DRAG_EVENT";
                case ViewRootImpl.MSG_DISPATCH_DRAG_LOCATION_EVENT /*16*/:
                    return "MSG_DISPATCH_DRAG_LOCATION_EVENT";
                case ViewRootImpl.MSG_DISPATCH_SYSTEM_UI_VISIBILITY /*17*/:
                    return "MSG_DISPATCH_SYSTEM_UI_VISIBILITY";
                case ViewRootImpl.MSG_UPDATE_CONFIGURATION /*18*/:
                    return "MSG_UPDATE_CONFIGURATION";
                case ViewRootImpl.MSG_PROCESS_INPUT_EVENTS /*19*/:
                    return "MSG_PROCESS_INPUT_EVENTS";
                case ViewRootImpl.MSG_CLEAR_ACCESSIBILITY_FOCUS_HOST /*21*/:
                    return "MSG_CLEAR_ACCESSIBILITY_FOCUS_HOST";
                case ViewRootImpl.MSG_DISPATCH_DONE_ANIMATING /*22*/:
                    return "MSG_DISPATCH_DONE_ANIMATING";
                case ViewRootImpl.MSG_WINDOW_MOVED /*24*/:
                    return "MSG_WINDOW_MOVED";
                case ViewRootImpl.MSG_SYNTHESIZE_INPUT_EVENT /*25*/:
                    return "MSG_SYNTHESIZE_INPUT_EVENT";
                case ViewRootImpl.MSG_DISPATCH_WINDOW_SHOWN /*26*/:
                    return "MSG_DISPATCH_WINDOW_SHOWN";
                default:
                    return super.getMessageName(message);
            }
        }

        public void handleMessage(Message msg) {
            SomeArgs args;
            Configuration config;
            InputMethodManager imm;
            switch (msg.what) {
                case ViewRootImpl.MSG_INVALIDATE /*1*/:
                    ((View) msg.obj).invalidate();
                    return;
                case ViewRootImpl.MSG_INVALIDATE_RECT /*2*/:
                    InvalidateInfo info = msg.obj;
                    info.target.invalidate(info.left, info.top, info.right, info.bottom);
                    info.recycle();
                    return;
                case ViewRootImpl.MSG_DIE /*3*/:
                    this.this$0.doDie();
                    return;
                case ViewRootImpl.MSG_RESIZED /*4*/:
                    args = msg.obj;
                    if (this.this$0.mWinFrame.equals(args.arg1) && this.this$0.mPendingOverscanInsets.equals(args.arg5) && this.this$0.mPendingContentInsets.equals(args.arg2) && this.this$0.mPendingStableInsets.equals(args.arg6) && this.this$0.mPendingVisibleInsets.equals(args.arg3) && args.arg4 == null) {
                        return;
                    }
                case ViewRootImpl.MSG_RESIZED_REPORT /*5*/:
                    break;
                case ViewRootImpl.MSG_WINDOW_FOCUS_CHANGED /*6*/:
                    if (this.this$0.mAdded) {
                        boolean hasWindowFocus = msg.arg1 != 0 ? true : ViewRootImpl.LOCAL_LOGV;
                        this.this$0.mAttachInfo.mHasWindowFocus = hasWindowFocus;
                        this.this$0.profileRendering(hasWindowFocus);
                        if (hasWindowFocus) {
                            this.this$0.ensureTouchModeLocally(msg.arg2 != 0 ? true : ViewRootImpl.LOCAL_LOGV);
                            if (this.this$0.mAttachInfo.mHardwareRenderer != null && this.this$0.mSurface.isValid()) {
                                this.this$0.mFullRedrawNeeded = true;
                                try {
                                    LayoutParams lp = this.this$0.mWindowAttributes;
                                    this.this$0.mAttachInfo.mHardwareRenderer.initializeIfNeeded(this.this$0.mWidth, this.this$0.mHeight, this.this$0.mSurface, lp != null ? lp.surfaceInsets : null);
                                } catch (OutOfResourcesException e) {
                                    Log.e(ViewRootImpl.TAG, "OutOfResourcesException locking surface", e);
                                    try {
                                        if (!this.this$0.mWindowSession.outOfMemory(this.this$0.mWindow)) {
                                            Slog.w(ViewRootImpl.TAG, "No processes killed for memory; killing self");
                                            Process.killProcess(Process.myPid());
                                        }
                                    } catch (RemoteException e2) {
                                    }
                                    sendMessageDelayed(obtainMessage(msg.what, msg.arg1, msg.arg2), 500);
                                    return;
                                }
                            }
                        }
                        this.this$0.mLastWasImTarget = LayoutParams.mayUseInputMethod(this.this$0.mWindowAttributes.flags);
                        imm = InputMethodManager.peekInstance();
                        if (this.this$0.mView != null) {
                            if (hasWindowFocus && imm != null && this.this$0.mLastWasImTarget && !this.this$0.isInLocalFocusMode()) {
                                imm.startGettingWindowFocus(this.this$0.mView);
                            }
                            this.this$0.mAttachInfo.mKeyDispatchState.reset();
                            this.this$0.mView.dispatchWindowFocusChanged(hasWindowFocus);
                            this.this$0.mAttachInfo.mTreeObserver.dispatchOnWindowFocusChange(hasWindowFocus);
                        }
                        if (hasWindowFocus) {
                            if (!(imm == null || !this.this$0.mLastWasImTarget || this.this$0.isInLocalFocusMode())) {
                                imm.onWindowFocus(this.this$0.mView, this.this$0.mView.findFocus(), this.this$0.mWindowAttributes.softInputMode, !this.this$0.mHasHadWindowFocus ? true : ViewRootImpl.LOCAL_LOGV, this.this$0.mWindowAttributes.flags);
                            }
                            LayoutParams layoutParams = this.this$0.mWindowAttributes;
                            layoutParams.softInputMode &= -257;
                            layoutParams = (LayoutParams) this.this$0.mView.getLayoutParams();
                            layoutParams.softInputMode &= -257;
                            this.this$0.mHasHadWindowFocus = true;
                        }
                        if (this.this$0.mView != null && this.this$0.mAccessibilityManager.isEnabled() && hasWindowFocus) {
                            this.this$0.mView.sendAccessibilityEvent(32);
                            return;
                        }
                        return;
                    }
                    return;
                case ViewRootImpl.MSG_DISPATCH_INPUT_EVENT /*7*/:
                    args = (SomeArgs) msg.obj;
                    this.this$0.enqueueInputEvent(args.arg1, args.arg2, 0, true);
                    args.recycle();
                    return;
                case ViewRootImpl.MSG_DISPATCH_APP_VISIBILITY /*8*/:
                    this.this$0.handleAppVisibility(msg.arg1 != 0 ? true : ViewRootImpl.LOCAL_LOGV);
                    return;
                case ViewRootImpl.MSG_DISPATCH_GET_NEW_SURFACE /*9*/:
                    this.this$0.handleGetNewSurface();
                    return;
                case ViewRootImpl.MSG_DISPATCH_KEY_FROM_IME /*11*/:
                    KeyEvent event = msg.obj;
                    if ((event.getFlags() & ViewRootImpl.MSG_DISPATCH_APP_VISIBILITY) != 0) {
                        event = KeyEvent.changeFlags(event, event.getFlags() & -9);
                    }
                    this.this$0.enqueueInputEvent(event, null, ViewRootImpl.MSG_INVALIDATE, true);
                    return;
                case ViewRootImpl.MSG_FINISH_INPUT_CONNECTION /*12*/:
                    imm = InputMethodManager.peekInstance();
                    if (imm != null) {
                        imm.reportFinishInputConnection((InputConnection) msg.obj);
                        return;
                    }
                    return;
                case ViewRootImpl.MSG_CHECK_FOCUS /*13*/:
                    imm = InputMethodManager.peekInstance();
                    if (imm != null) {
                        imm.checkFocus();
                        return;
                    }
                    return;
                case ViewRootImpl.MSG_CLOSE_SYSTEM_DIALOGS /*14*/:
                    if (this.this$0.mView != null) {
                        this.this$0.mView.onCloseSystemDialogs((String) msg.obj);
                        return;
                    }
                    return;
                case ViewRootImpl.MSG_DISPATCH_DRAG_EVENT /*15*/:
                case ViewRootImpl.MSG_DISPATCH_DRAG_LOCATION_EVENT /*16*/:
                    DragEvent event2 = msg.obj;
                    event2.mLocalState = this.this$0.mLocalDragState;
                    this.this$0.handleDragEvent(event2);
                    return;
                case ViewRootImpl.MSG_DISPATCH_SYSTEM_UI_VISIBILITY /*17*/:
                    this.this$0.handleDispatchSystemUiVisibilityChanged((SystemUiVisibilityInfo) msg.obj);
                    return;
                case ViewRootImpl.MSG_UPDATE_CONFIGURATION /*18*/:
                    config = (Configuration) msg.obj;
                    if (config.isOtherSeqNewer(this.this$0.mLastConfiguration)) {
                        config = this.this$0.mLastConfiguration;
                    }
                    this.this$0.updateConfiguration(config, ViewRootImpl.LOCAL_LOGV);
                    return;
                case ViewRootImpl.MSG_PROCESS_INPUT_EVENTS /*19*/:
                    this.this$0.mProcessInputEventsScheduled = ViewRootImpl.LOCAL_LOGV;
                    this.this$0.doProcessInputEvents();
                    return;
                case ViewRootImpl.MSG_CLEAR_ACCESSIBILITY_FOCUS_HOST /*21*/:
                    this.this$0.setAccessibilityFocus(null, null);
                    return;
                case ViewRootImpl.MSG_DISPATCH_DONE_ANIMATING /*22*/:
                    this.this$0.handleDispatchDoneAnimating();
                    return;
                case ViewRootImpl.MSG_INVALIDATE_WORLD /*23*/:
                    if (this.this$0.mView != null) {
                        this.this$0.invalidateWorld(this.this$0.mView);
                        return;
                    }
                    return;
                case ViewRootImpl.MSG_WINDOW_MOVED /*24*/:
                    if (this.this$0.mAdded) {
                        int w = this.this$0.mWinFrame.width();
                        int h = this.this$0.mWinFrame.height();
                        int l = msg.arg1;
                        int t = msg.arg2;
                        this.this$0.mWinFrame.left = l;
                        this.this$0.mWinFrame.right = l + w;
                        this.this$0.mWinFrame.top = t;
                        this.this$0.mWinFrame.bottom = t + h;
                        if (this.this$0.mView != null) {
                            ViewRootImpl.forceLayout(this.this$0.mView);
                        }
                        this.this$0.requestLayout();
                        return;
                    }
                    return;
                case ViewRootImpl.MSG_SYNTHESIZE_INPUT_EVENT /*25*/:
                    this.this$0.enqueueInputEvent((InputEvent) msg.obj, null, 32, true);
                    return;
                case ViewRootImpl.MSG_DISPATCH_WINDOW_SHOWN /*26*/:
                    this.this$0.handleDispatchWindowShown();
                    return;
                default:
                    return;
            }
            if (this.this$0.mAdded) {
                args = (SomeArgs) msg.obj;
                config = args.arg4;
                if (config != null) {
                    this.this$0.updateConfiguration(config, ViewRootImpl.LOCAL_LOGV);
                }
                this.this$0.mWinFrame.set((Rect) args.arg1);
                this.this$0.mPendingOverscanInsets.set((Rect) args.arg5);
                this.this$0.mPendingContentInsets.set((Rect) args.arg2);
                this.this$0.mPendingStableInsets.set((Rect) args.arg6);
                this.this$0.mPendingVisibleInsets.set((Rect) args.arg3);
                args.recycle();
                if (msg.what == ViewRootImpl.MSG_RESIZED_REPORT) {
                    this.this$0.mReportNextDraw = true;
                }
                if (this.this$0.mView != null) {
                    ViewRootImpl.forceLayout(this.this$0.mView);
                }
                this.this$0.requestLayout();
            }
        }
    }

    static class W extends IWindow.Stub {
        private final WeakReference<ViewRootImpl> mViewAncestor;
        private final IWindowSession mWindowSession;

        W(ViewRootImpl viewAncestor) {
            this.mViewAncestor = new WeakReference(viewAncestor);
            this.mWindowSession = viewAncestor.mWindowSession;
        }

        public void resized(Rect frame, Rect overscanInsets, Rect contentInsets, Rect visibleInsets, Rect stableInsets, boolean reportDraw, Configuration newConfig) {
            ViewRootImpl viewAncestor = (ViewRootImpl) this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchResized(frame, overscanInsets, contentInsets, visibleInsets, stableInsets, reportDraw, newConfig);
            }
        }

        public void moved(int newX, int newY) {
            ViewRootImpl viewAncestor = (ViewRootImpl) this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchMoved(newX, newY);
            }
        }

        public void dispatchAppVisibility(boolean visible) {
            ViewRootImpl viewAncestor = (ViewRootImpl) this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchAppVisibility(visible);
            }
        }

        public void dispatchGetNewSurface() {
            ViewRootImpl viewAncestor = (ViewRootImpl) this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchGetNewSurface();
            }
        }

        public void windowFocusChanged(boolean hasFocus, boolean inTouchMode) {
            ViewRootImpl viewAncestor = (ViewRootImpl) this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.windowFocusChanged(hasFocus, inTouchMode);
            }
        }

        private static int checkCallingPermission(String permission) {
            try {
                return ActivityManagerNative.getDefault().checkPermission(permission, Binder.getCallingPid(), Binder.getCallingUid());
            } catch (RemoteException e) {
                return -1;
            }
        }

        public void executeCommand(String command, String parameters, ParcelFileDescriptor out) {
            IOException e;
            Throwable th;
            ViewRootImpl viewAncestor = (ViewRootImpl) this.mViewAncestor.get();
            if (viewAncestor != null) {
                View view = viewAncestor.mView;
                if (view == null) {
                    return;
                }
                if (checkCallingPermission(permission.DUMP) != 0) {
                    throw new SecurityException("Insufficient permissions to invoke executeCommand() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
                }
                OutputStream clientStream = null;
                try {
                    OutputStream clientStream2 = new AutoCloseOutputStream(out);
                    try {
                        ViewDebug.dispatchCommand(view, command, parameters, clientStream2);
                        if (clientStream2 != null) {
                            try {
                                clientStream2.close();
                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }
                        }
                    } catch (IOException e3) {
                        e2 = e3;
                        clientStream = clientStream2;
                        try {
                            e2.printStackTrace();
                            if (clientStream != null) {
                                try {
                                    clientStream.close();
                                } catch (IOException e22) {
                                    e22.printStackTrace();
                                }
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            if (clientStream != null) {
                                try {
                                    clientStream.close();
                                } catch (IOException e222) {
                                    e222.printStackTrace();
                                }
                            }
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        clientStream = clientStream2;
                        if (clientStream != null) {
                            clientStream.close();
                        }
                        throw th;
                    }
                } catch (IOException e4) {
                    e222 = e4;
                    e222.printStackTrace();
                    if (clientStream != null) {
                        clientStream.close();
                    }
                }
            }
        }

        public void closeSystemDialogs(String reason) {
            ViewRootImpl viewAncestor = (ViewRootImpl) this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchCloseSystemDialogs(reason);
            }
        }

        public void dispatchWallpaperOffsets(float x, float y, float xStep, float yStep, boolean sync) {
            if (sync) {
                try {
                    this.mWindowSession.wallpaperOffsetsComplete(asBinder());
                } catch (RemoteException e) {
                }
            }
        }

        public void dispatchWallpaperCommand(String action, int x, int y, int z, Bundle extras, boolean sync) {
            if (sync) {
                try {
                    this.mWindowSession.wallpaperCommandComplete(asBinder(), null);
                } catch (RemoteException e) {
                }
            }
        }

        public void dispatchDragEvent(DragEvent event) {
            ViewRootImpl viewAncestor = (ViewRootImpl) this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchDragEvent(event);
            }
        }

        public void dispatchSystemUiVisibilityChanged(int seq, int globalVisibility, int localValue, int localChanges) {
            ViewRootImpl viewAncestor = (ViewRootImpl) this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchSystemUiVisibilityChanged(seq, globalVisibility, localValue, localChanges);
            }
        }

        public void doneAnimating() {
            ViewRootImpl viewAncestor = (ViewRootImpl) this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchDoneAnimating();
            }
        }

        public void dispatchWindowShown() {
            ViewRootImpl viewAncestor = (ViewRootImpl) this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchWindowShown();
            }
        }
    }

    final class WindowInputEventReceiver extends InputEventReceiver {
        final /* synthetic */ ViewRootImpl this$0;

        public WindowInputEventReceiver(ViewRootImpl viewRootImpl, InputChannel inputChannel, Looper looper) {
            this.this$0 = viewRootImpl;
            super(inputChannel, looper);
        }

        public void onInputEvent(InputEvent event) {
            this.this$0.enqueueInputEvent(event, this, 0, true);
        }

        public void onBatchedInputEventPending() {
            if (this.this$0.mUnbufferedInputDispatch) {
                super.onBatchedInputEventPending();
            } else {
                this.this$0.scheduleConsumeBatchedInput();
            }
        }

        public void dispose() {
            this.this$0.unscheduleConsumeBatchedInput();
            super.dispose();
        }
    }

    static {
        sRunQueues = new ThreadLocal();
        sFirstDrawHandlers = new ArrayList();
        sFirstDrawComplete = LOCAL_LOGV;
        sConfigCallbacks = new ArrayList();
        mResizeInterpolator = new AccelerateDecelerateInterpolator();
    }

    public ViewRootImpl(Context context, Display display) {
        InputEventConsistencyVerifier inputEventConsistencyVerifier;
        this.mTmpLocation = new int[MSG_INVALIDATE_RECT];
        this.mTmpValue = new TypedValue();
        this.mWindowAttributes = new LayoutParams();
        this.mAppVisible = true;
        this.mOrigWindowType = -1;
        this.mStopped = LOCAL_LOGV;
        this.mLastInCompatMode = LOCAL_LOGV;
        this.mPendingInputEventQueueLengthCounterName = "pq";
        this.mWindowAttributesChanged = LOCAL_LOGV;
        this.mWindowAttributesChangesFlag = 0;
        this.mSurface = new Surface();
        this.mPendingOverscanInsets = new Rect();
        this.mPendingVisibleInsets = new Rect();
        this.mPendingStableInsets = new Rect();
        this.mPendingContentInsets = new Rect();
        this.mLastGivenInsets = new InternalInsetsInfo();
        this.mDispatchContentInsets = new Rect();
        this.mDispatchStableInsets = new Rect();
        this.mLastConfiguration = new Configuration();
        this.mPendingConfiguration = new Configuration();
        this.mDragPoint = new PointF();
        this.mLastTouchPoint = new PointF();
        this.mFpsStartTime = -1;
        this.mFpsPrevTime = -1;
        this.mInLayout = LOCAL_LOGV;
        this.mLayoutRequesters = new ArrayList();
        this.mHandlingLayoutInLayoutRequest = LOCAL_LOGV;
        if (InputEventConsistencyVerifier.isInstrumentationEnabled()) {
            inputEventConsistencyVerifier = new InputEventConsistencyVerifier(this, 0);
        } else {
            inputEventConsistencyVerifier = null;
        }
        this.mInputEventConsistencyVerifier = inputEventConsistencyVerifier;
        this.mProfile = LOCAL_LOGV;
        this.mDisplayListener = new DisplayListener() {
            public void onDisplayChanged(int displayId) {
                if (ViewRootImpl.this.mView != null && ViewRootImpl.this.mDisplay.getDisplayId() == displayId) {
                    int oldDisplayState = ViewRootImpl.this.mAttachInfo.mDisplayState;
                    int newDisplayState = ViewRootImpl.this.mDisplay.getState();
                    if (oldDisplayState != newDisplayState) {
                        ViewRootImpl.this.mAttachInfo.mDisplayState = newDisplayState;
                        ViewRootImpl.this.pokeDrawLockIfNeeded();
                        if (oldDisplayState != 0) {
                            int oldScreenState = toViewScreenState(oldDisplayState);
                            int newScreenState = toViewScreenState(newDisplayState);
                            if (oldScreenState != newScreenState) {
                                ViewRootImpl.this.mView.dispatchScreenStateChanged(newScreenState);
                            }
                            if (oldDisplayState == ViewRootImpl.MSG_INVALIDATE) {
                                ViewRootImpl.this.mFullRedrawNeeded = true;
                                ViewRootImpl.this.scheduleTraversals();
                            }
                        }
                    }
                }
            }

            public void onDisplayRemoved(int displayId) {
            }

            public void onDisplayAdded(int displayId) {
            }

            private int toViewScreenState(int displayState) {
                return displayState == ViewRootImpl.MSG_INVALIDATE ? 0 : ViewRootImpl.MSG_INVALIDATE;
            }
        };
        this.mResizePaint = new Paint();
        this.mHandler = new ViewRootHandler(this);
        this.mTraversalRunnable = new TraversalRunnable(this);
        this.mConsumedBatchedInputRunnable = new ConsumeBatchedInputRunnable(this);
        this.mConsumeBatchedInputImmediatelyRunnable = new ConsumeBatchedInputImmediatelyRunnable(this);
        this.mInvalidateOnAnimationRunnable = new InvalidateOnAnimationRunnable(this);
        this.mContext = context;
        this.mWindowSession = WindowManagerGlobal.getWindowSession();
        this.mDisplay = display;
        this.mBasePackageName = context.getBasePackageName();
        this.mDisplayAdjustments = display.getDisplayAdjustments();
        this.mThread = Thread.currentThread();
        this.mLocation = new WindowLeaked(null);
        this.mLocation.fillInStackTrace();
        this.mWidth = -1;
        this.mHeight = -1;
        this.mDirty = new Rect();
        this.mTempRect = new Rect();
        this.mVisRect = new Rect();
        this.mWinFrame = new Rect();
        this.mWindow = new W(this);
        this.mTargetSdkVersion = context.getApplicationInfo().targetSdkVersion;
        this.mViewVisibility = MSG_DISPATCH_APP_VISIBILITY;
        this.mTransparentRegion = new Region();
        this.mPreviousTransparentRegion = new Region();
        this.mFirst = true;
        this.mAdded = LOCAL_LOGV;
        this.mAttachInfo = new AttachInfo(this.mWindowSession, this.mWindow, display, this, this.mHandler, this);
        this.mAccessibilityManager = AccessibilityManager.getInstance(context);
        this.mAccessibilityInteractionConnectionManager = new AccessibilityInteractionConnectionManager(this);
        this.mAccessibilityManager.addAccessibilityStateChangeListener(this.mAccessibilityInteractionConnectionManager);
        this.mHighContrastTextManager = new HighContrastTextManager(this);
        this.mAccessibilityManager.addHighTextContrastStateChangeListener(this.mHighContrastTextManager);
        this.mViewConfiguration = ViewConfiguration.get(context);
        this.mDensity = context.getResources().getDisplayMetrics().densityDpi;
        this.mNoncompatDensity = context.getResources().getDisplayMetrics().noncompatDensityDpi;
        this.mFallbackEventHandler = PolicyManager.makeNewFallbackEventHandler(context);
        this.mChoreographer = Choreographer.getInstance();
        this.mDisplayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        loadSystemProperties();
        this.mWindowIsRound = ScreenShapeHelper.getWindowIsRound(context.getResources());
    }

    public static void addFirstDrawHandler(Runnable callback) {
        synchronized (sFirstDrawHandlers) {
            if (!sFirstDrawComplete) {
                sFirstDrawHandlers.add(callback);
            }
        }
    }

    public static void addConfigCallback(ComponentCallbacks callback) {
        synchronized (sConfigCallbacks) {
            sConfigCallbacks.add(callback);
        }
    }

    public void profile() {
        this.mProfile = true;
    }

    static boolean isInTouchMode() {
        IWindowSession windowSession = WindowManagerGlobal.peekWindowSession();
        if (windowSession != null) {
            try {
                return windowSession.getInTouchMode();
            } catch (RemoteException e) {
            }
        }
        return LOCAL_LOGV;
    }

    public void setView(View view, LayoutParams attrs, View panelParentView) {
        synchronized (this) {
            if (this.mView == null) {
                float f;
                this.mView = view;
                this.mAttachInfo.mDisplayState = this.mDisplay.getState();
                this.mDisplayManager.registerDisplayListener(this.mDisplayListener, this.mHandler);
                this.mViewLayoutDirectionInitial = this.mView.getRawLayoutDirection();
                this.mFallbackEventHandler.setView(view);
                this.mWindowAttributes.copyFrom(attrs);
                if (this.mWindowAttributes.packageName == null) {
                    this.mWindowAttributes.packageName = this.mBasePackageName;
                }
                attrs = this.mWindowAttributes;
                this.mClientWindowLayoutFlags = attrs.flags;
                setAccessibilityFocus(null, null);
                if (view instanceof RootViewSurfaceTaker) {
                    this.mSurfaceHolderCallback = ((RootViewSurfaceTaker) view).willYouTakeTheSurface();
                    if (this.mSurfaceHolderCallback != null) {
                        this.mSurfaceHolder = new TakenSurfaceHolder(this);
                        this.mSurfaceHolder.setFormat(0);
                    }
                }
                int surfaceInset = (int) Math.ceil((double) (view.getZ() * 2.0f));
                attrs.surfaceInsets.set(surfaceInset, surfaceInset, surfaceInset, surfaceInset);
                CompatibilityInfo compatibilityInfo = this.mDisplayAdjustments.getCompatibilityInfo();
                this.mTranslator = compatibilityInfo.getTranslator();
                this.mDisplayAdjustments.setActivityToken(attrs.token);
                if (this.mSurfaceHolder == null) {
                    enableHardwareAcceleration(attrs);
                }
                boolean restore = LOCAL_LOGV;
                if (this.mTranslator != null) {
                    this.mSurface.setCompatibilityTranslator(this.mTranslator);
                    restore = true;
                    attrs.backup();
                    this.mTranslator.translateWindowLayout(attrs);
                }
                if (!compatibilityInfo.supportsScreen()) {
                    attrs.privateFlags |= AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS;
                    this.mLastInCompatMode = true;
                }
                this.mSoftInputMode = attrs.softInputMode;
                this.mWindowAttributesChanged = true;
                this.mWindowAttributesChangesFlag = -1;
                this.mAttachInfo.mRootView = view;
                this.mAttachInfo.mScalingRequired = this.mTranslator != null ? true : LOCAL_LOGV;
                AttachInfo attachInfo = this.mAttachInfo;
                if (this.mTranslator == null) {
                    f = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
                } else {
                    f = this.mTranslator.applicationScale;
                }
                attachInfo.mApplicationScale = f;
                if (panelParentView != null) {
                    this.mAttachInfo.mPanelParentWindowToken = panelParentView.getApplicationWindowToken();
                }
                this.mAdded = true;
                requestLayout();
                if ((this.mWindowAttributes.inputFeatures & MSG_INVALIDATE_RECT) == 0) {
                    this.mInputChannel = new InputChannel();
                }
                try {
                    this.mOrigWindowType = this.mWindowAttributes.type;
                    this.mAttachInfo.mRecomputeGlobalAttributes = true;
                    collectViewAttributes();
                    int res = this.mWindowSession.addToDisplay(this.mWindow, this.mSeq, this.mWindowAttributes, getHostVisibility(), this.mDisplay.getDisplayId(), this.mAttachInfo.mContentInsets, this.mAttachInfo.mStableInsets, this.mInputChannel);
                    if (restore) {
                        attrs.restore();
                    }
                    if (this.mTranslator != null) {
                        this.mTranslator.translateRectInScreenToAppWindow(this.mAttachInfo.mContentInsets);
                    }
                    this.mPendingOverscanInsets.set(0, 0, 0, 0);
                    this.mPendingContentInsets.set(this.mAttachInfo.mContentInsets);
                    this.mPendingStableInsets.set(this.mAttachInfo.mStableInsets);
                    this.mPendingVisibleInsets.set(0, 0, 0, 0);
                    if (res < 0) {
                        this.mAttachInfo.mRootView = null;
                        this.mAdded = LOCAL_LOGV;
                        this.mFallbackEventHandler.setView(null);
                        unscheduleTraversals();
                        setAccessibilityFocus(null, null);
                        switch (res) {
                            case WebViewClient.ERROR_UNSUPPORTED_SCHEME /*-10*/:
                                throw new InvalidDisplayException("Unable to add window " + this.mWindow + " -- the specified window type is not valid");
                            case WebViewClient.ERROR_REDIRECT_LOOP /*-9*/:
                                throw new InvalidDisplayException("Unable to add window " + this.mWindow + " -- the specified display can not be found");
                            case WebViewClient.ERROR_TIMEOUT /*-8*/:
                                throw new BadTokenException("Unable to add window " + this.mWindow + " -- permission denied for this window type");
                            case WebViewClient.ERROR_IO /*-7*/:
                                throw new BadTokenException("Unable to add window " + this.mWindow + " -- another window of this type already exists");
                            case WebViewClient.ERROR_CONNECT /*-6*/:
                                return;
                            case WebViewClient.ERROR_PROXY_AUTHENTICATION /*-5*/:
                                throw new BadTokenException("Unable to add window -- window " + this.mWindow + " has already been added");
                            case WebViewClient.ERROR_AUTHENTICATION /*-4*/:
                                throw new BadTokenException("Unable to add window -- app for token " + attrs.token + " is exiting");
                            case WebViewClient.ERROR_UNSUPPORTED_AUTH_SCHEME /*-3*/:
                                throw new BadTokenException("Unable to add window -- token " + attrs.token + " is not for an application");
                            case ListPopupWindow.WRAP_CONTENT /*-2*/:
                            case ListPopupWindow.MATCH_PARENT /*-1*/:
                                throw new BadTokenException("Unable to add window -- token " + attrs.token + " is not valid; is your activity running?");
                            default:
                                throw new RuntimeException("Unable to add window -- unknown error code " + res);
                        }
                    }
                    if (view instanceof RootViewSurfaceTaker) {
                        this.mInputQueueCallback = ((RootViewSurfaceTaker) view).willYouTakeTheInputQueue();
                    }
                    if (this.mInputChannel != null) {
                        if (this.mInputQueueCallback != null) {
                            this.mInputQueue = new InputQueue();
                            this.mInputQueueCallback.onInputQueueCreated(this.mInputQueue);
                        }
                        this.mInputEventReceiver = new WindowInputEventReceiver(this, this.mInputChannel, Looper.myLooper());
                    }
                    view.assignParent(this);
                    this.mAddedTouchMode = (res & MSG_INVALIDATE) != 0 ? true : LOCAL_LOGV;
                    this.mAppVisible = (res & MSG_INVALIDATE_RECT) != 0 ? true : LOCAL_LOGV;
                    if (this.mAccessibilityManager.isEnabled()) {
                        this.mAccessibilityInteractionConnectionManager.ensureConnection();
                    }
                    if (view.getImportantForAccessibility() == 0) {
                        view.setImportantForAccessibility(MSG_INVALIDATE);
                    }
                    CharSequence counterSuffix = attrs.getTitle();
                    this.mSyntheticInputStage = new SyntheticInputStage(this);
                    InputStage earlyPostImeInputStage = new EarlyPostImeInputStage(this, new NativePostImeInputStage(this, new ViewPostImeInputStage(this, this.mSyntheticInputStage), "aq:native-post-ime:" + counterSuffix));
                    this.mFirstInputStage = new NativePreImeInputStage(this, new ViewPreImeInputStage(this, new ImeInputStage(this, earlyPostImeInputStage, "aq:ime:" + counterSuffix)), "aq:native-pre-ime:" + counterSuffix);
                    this.mFirstPostImeInputStage = earlyPostImeInputStage;
                    this.mPendingInputEventQueueLengthCounterName = "aq:pending:" + counterSuffix;
                } catch (RemoteException e) {
                    this.mAdded = LOCAL_LOGV;
                    this.mView = null;
                    this.mAttachInfo.mRootView = null;
                    this.mInputChannel = null;
                    this.mFallbackEventHandler.setView(null);
                    unscheduleTraversals();
                    setAccessibilityFocus(null, null);
                    throw new RuntimeException("Adding window failed", e);
                } catch (Throwable th) {
                    if (restore) {
                        attrs.restore();
                    }
                }
            }
        }
    }

    private boolean isInLocalFocusMode() {
        return (this.mWindowAttributes.flags & EditorInfo.IME_FLAG_NO_EXTRACT_UI) != 0 ? true : LOCAL_LOGV;
    }

    void destroyHardwareResources() {
        if (this.mAttachInfo.mHardwareRenderer != null) {
            this.mAttachInfo.mHardwareRenderer.destroyHardwareResources(this.mView);
            this.mAttachInfo.mHardwareRenderer.destroy();
        }
    }

    public void detachFunctor(long functor) {
        this.mBlockResizeBuffer = true;
        if (this.mAttachInfo.mHardwareRenderer != null) {
            this.mAttachInfo.mHardwareRenderer.stopDrawing();
        }
    }

    public void invokeFunctor(long functor, boolean waitForCompletion) {
        ThreadedRenderer.invokeFunctor(functor, waitForCompletion);
    }

    public void registerAnimatingRenderNode(RenderNode animator) {
        if (this.mAttachInfo.mHardwareRenderer != null) {
            this.mAttachInfo.mHardwareRenderer.registerAnimatingRenderNode(animator);
            return;
        }
        if (this.mAttachInfo.mPendingAnimatingRenderNodes == null) {
            this.mAttachInfo.mPendingAnimatingRenderNodes = new ArrayList();
        }
        this.mAttachInfo.mPendingAnimatingRenderNodes.add(animator);
    }

    private void enableHardwareAcceleration(LayoutParams attrs) {
        this.mAttachInfo.mHardwareAccelerated = LOCAL_LOGV;
        this.mAttachInfo.mHardwareAccelerationRequested = LOCAL_LOGV;
        if (this.mTranslator == null) {
            boolean hardwareAccelerated;
            if ((attrs.flags & WindowManagerPolicy.FLAG_INJECTED) != 0) {
                hardwareAccelerated = true;
            } else {
                hardwareAccelerated = LOCAL_LOGV;
            }
            if (hardwareAccelerated && HardwareRenderer.isAvailable()) {
                boolean fakeHwAccelerated;
                if ((attrs.privateFlags & MSG_INVALIDATE) != 0) {
                    fakeHwAccelerated = true;
                } else {
                    fakeHwAccelerated = LOCAL_LOGV;
                }
                boolean forceHwAccelerated;
                if ((attrs.privateFlags & MSG_INVALIDATE_RECT) != 0) {
                    forceHwAccelerated = true;
                } else {
                    forceHwAccelerated = LOCAL_LOGV;
                }
                if (fakeHwAccelerated) {
                    this.mAttachInfo.mHardwareAccelerationRequested = true;
                } else if (!HardwareRenderer.sRendererDisabled || (HardwareRenderer.sSystemRendererDisabled && forceHwAccelerated)) {
                    boolean translucent;
                    if (this.mAttachInfo.mHardwareRenderer != null) {
                        this.mAttachInfo.mHardwareRenderer.destroy();
                    }
                    Rect insets = attrs.surfaceInsets;
                    boolean hasSurfaceInsets;
                    if (insets.left == 0 && insets.right == 0 && insets.top == 0 && insets.bottom == 0) {
                        hasSurfaceInsets = LOCAL_LOGV;
                    } else {
                        hasSurfaceInsets = true;
                    }
                    if (attrs.format != -1 || hasSurfaceInsets) {
                        translucent = true;
                    } else {
                        translucent = LOCAL_LOGV;
                    }
                    this.mAttachInfo.mHardwareRenderer = HardwareRenderer.create(this.mContext, translucent);
                    if (this.mAttachInfo.mHardwareRenderer != null) {
                        this.mAttachInfo.mHardwareRenderer.setName(attrs.getTitle().toString());
                        AttachInfo attachInfo = this.mAttachInfo;
                        this.mAttachInfo.mHardwareAccelerationRequested = true;
                        attachInfo.mHardwareAccelerated = true;
                    }
                }
            }
        }
    }

    public View getView() {
        return this.mView;
    }

    final WindowLeaked getLocation() {
        return this.mLocation;
    }

    void setLayoutParams(LayoutParams attrs, boolean newView) {
        synchronized (this) {
            int oldInsetLeft = this.mWindowAttributes.surfaceInsets.left;
            int oldInsetTop = this.mWindowAttributes.surfaceInsets.top;
            int oldInsetRight = this.mWindowAttributes.surfaceInsets.right;
            int oldInsetBottom = this.mWindowAttributes.surfaceInsets.bottom;
            int oldSoftInputMode = this.mWindowAttributes.softInputMode;
            this.mClientWindowLayoutFlags = attrs.flags;
            int compatibleWindowFlag = this.mWindowAttributes.privateFlags & AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS;
            attrs.systemUiVisibility = this.mWindowAttributes.systemUiVisibility;
            attrs.subtreeSystemUiVisibility = this.mWindowAttributes.subtreeSystemUiVisibility;
            this.mWindowAttributesChangesFlag = this.mWindowAttributes.copyFrom(attrs);
            if ((this.mWindowAttributesChangesFlag & AccessibilityNodeInfo.ACTION_COLLAPSE) != 0) {
                this.mAttachInfo.mRecomputeGlobalAttributes = true;
            }
            if (this.mWindowAttributes.packageName == null) {
                this.mWindowAttributes.packageName = this.mBasePackageName;
            }
            LayoutParams layoutParams = this.mWindowAttributes;
            layoutParams.privateFlags |= compatibleWindowFlag;
            this.mWindowAttributes.surfaceInsets.set(oldInsetLeft, oldInsetTop, oldInsetRight, oldInsetBottom);
            applyKeepScreenOnFlag(this.mWindowAttributes);
            if (newView) {
                this.mSoftInputMode = attrs.softInputMode;
                requestLayout();
            }
            if ((attrs.softInputMode & LayoutParams.SOFT_INPUT_MASK_ADJUST) == 0) {
                this.mWindowAttributes.softInputMode = (this.mWindowAttributes.softInputMode & -241) | (oldSoftInputMode & LayoutParams.SOFT_INPUT_MASK_ADJUST);
            }
            this.mWindowAttributesChanged = true;
            scheduleTraversals();
        }
    }

    void handleAppVisibility(boolean visible) {
        if (this.mAppVisible != visible) {
            this.mAppVisible = visible;
            scheduleTraversals();
            if (!this.mAppVisible) {
                WindowManagerGlobal.trimForeground();
            }
        }
    }

    void handleGetNewSurface() {
        this.mNewSurfaceNeeded = true;
        this.mFullRedrawNeeded = true;
        scheduleTraversals();
    }

    void pokeDrawLockIfNeeded() {
        int displayState = this.mAttachInfo.mDisplayState;
        if (this.mView == null || !this.mAdded || !this.mTraversalScheduled) {
            return;
        }
        if (displayState == MSG_DIE || displayState == MSG_RESIZED) {
            try {
                this.mWindowSession.pokeDrawLock(this.mWindow);
            } catch (RemoteException e) {
            }
        }
    }

    public void requestFitSystemWindows() {
        checkThread();
        this.mApplyInsetsRequested = true;
        scheduleTraversals();
    }

    public void requestLayout() {
        if (!this.mHandlingLayoutInLayoutRequest) {
            checkThread();
            this.mLayoutRequested = true;
            scheduleTraversals();
        }
    }

    public boolean isLayoutRequested() {
        return this.mLayoutRequested;
    }

    void invalidate() {
        this.mDirty.set(0, 0, this.mWidth, this.mHeight);
        if (!this.mWillDrawSoon) {
            scheduleTraversals();
        }
    }

    void invalidateWorld(View view) {
        view.invalidate();
        if (view instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) view;
            for (int i = 0; i < parent.getChildCount(); i += MSG_INVALIDATE) {
                invalidateWorld(parent.getChildAt(i));
            }
        }
    }

    public void invalidateChild(View child, Rect dirty) {
        invalidateChildInParent(null, dirty);
    }

    public ViewParent invalidateChildInParent(int[] location, Rect dirty) {
        checkThread();
        if (dirty == null) {
            invalidate();
        } else if (!dirty.isEmpty() || this.mIsAnimating) {
            if (!(this.mCurScrollY == 0 && this.mTranslator == null)) {
                this.mTempRect.set(dirty);
                dirty = this.mTempRect;
                if (this.mCurScrollY != 0) {
                    dirty.offset(0, -this.mCurScrollY);
                }
                if (this.mTranslator != null) {
                    this.mTranslator.translateRectInAppWindowToScreen(dirty);
                }
                if (this.mAttachInfo.mScalingRequired) {
                    dirty.inset(-1, -1);
                }
            }
            Rect localDirty = this.mDirty;
            if (!(localDirty.isEmpty() || localDirty.contains(dirty))) {
                this.mAttachInfo.mSetIgnoreDirtyState = true;
                this.mAttachInfo.mIgnoreDirtyState = true;
            }
            localDirty.union(dirty.left, dirty.top, dirty.right, dirty.bottom);
            float appScale = this.mAttachInfo.mApplicationScale;
            boolean intersected = localDirty.intersect(0, 0, (int) ((((float) this.mWidth) * appScale) + 0.5f), (int) ((((float) this.mHeight) * appScale) + 0.5f));
            if (!intersected) {
                localDirty.setEmpty();
            }
            if (!this.mWillDrawSoon && (intersected || this.mIsAnimating)) {
                scheduleTraversals();
            }
        }
        return null;
    }

    void setStopped(boolean stopped) {
        if (this.mStopped != stopped) {
            this.mStopped = stopped;
            if (!stopped) {
                scheduleTraversals();
            }
        }
    }

    public ViewParent getParent() {
        return null;
    }

    public boolean getChildVisibleRect(View child, Rect r, Point offset) {
        if (child == this.mView) {
            return r.intersect(0, 0, this.mWidth, this.mHeight);
        }
        throw new RuntimeException("child is not mine, honest!");
    }

    public void bringChildToFront(View child) {
    }

    int getHostVisibility() {
        return this.mAppVisible ? this.mView.getVisibility() : MSG_DISPATCH_APP_VISIBILITY;
    }

    void disposeResizeBuffer() {
        if (this.mResizeBuffer != null) {
            this.mResizeBuffer.destroy();
            this.mResizeBuffer = null;
        }
    }

    public void requestTransitionStart(LayoutTransition transition) {
        if (this.mPendingTransitions == null || !this.mPendingTransitions.contains(transition)) {
            if (this.mPendingTransitions == null) {
                this.mPendingTransitions = new ArrayList();
            }
            this.mPendingTransitions.add(transition);
        }
    }

    void notifyRendererOfFramePending() {
        if (this.mAttachInfo.mHardwareRenderer != null) {
            this.mAttachInfo.mHardwareRenderer.notifyFramePending();
        }
    }

    void scheduleTraversals() {
        if (!this.mTraversalScheduled) {
            this.mTraversalScheduled = true;
            this.mTraversalBarrier = this.mHandler.getLooper().postSyncBarrier();
            this.mChoreographer.postCallback(MSG_INVALIDATE_RECT, this.mTraversalRunnable, null);
            if (!this.mUnbufferedInputDispatch) {
                scheduleConsumeBatchedInput();
            }
            notifyRendererOfFramePending();
            pokeDrawLockIfNeeded();
        }
    }

    void unscheduleTraversals() {
        if (this.mTraversalScheduled) {
            this.mTraversalScheduled = LOCAL_LOGV;
            this.mHandler.getLooper().removeSyncBarrier(this.mTraversalBarrier);
            this.mChoreographer.removeCallbacks(MSG_INVALIDATE_RECT, this.mTraversalRunnable, null);
        }
    }

    void doTraversal() {
        if (this.mTraversalScheduled) {
            this.mTraversalScheduled = LOCAL_LOGV;
            this.mHandler.getLooper().removeSyncBarrier(this.mTraversalBarrier);
            if (this.mProfile) {
                Debug.startMethodTracing("ViewAncestor");
            }
            Trace.traceBegin(8, "performTraversals");
            try {
                performTraversals();
                if (this.mProfile) {
                    Debug.stopMethodTracing();
                    this.mProfile = LOCAL_LOGV;
                }
            } finally {
                Trace.traceEnd(8);
            }
        }
    }

    private void applyKeepScreenOnFlag(LayoutParams params) {
        if (this.mAttachInfo.mKeepScreenOn) {
            params.flags |= AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS;
        } else {
            params.flags = (params.flags & -129) | (this.mClientWindowLayoutFlags & AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS);
        }
    }

    private boolean collectViewAttributes() {
        if (!this.mAttachInfo.mRecomputeGlobalAttributes) {
            return LOCAL_LOGV;
        }
        this.mAttachInfo.mRecomputeGlobalAttributes = LOCAL_LOGV;
        boolean oldScreenOn = this.mAttachInfo.mKeepScreenOn;
        this.mAttachInfo.mKeepScreenOn = LOCAL_LOGV;
        this.mAttachInfo.mSystemUiVisibility = 0;
        this.mAttachInfo.mHasSystemUiListeners = LOCAL_LOGV;
        this.mView.dispatchCollectViewAttributes(this.mAttachInfo, 0);
        AttachInfo attachInfo = this.mAttachInfo;
        attachInfo.mSystemUiVisibility &= this.mAttachInfo.mDisabledSystemUiVisibility ^ -1;
        LayoutParams params = this.mWindowAttributes;
        attachInfo = this.mAttachInfo;
        attachInfo.mSystemUiVisibility |= getImpliedSystemUiVisibility(params);
        if (this.mAttachInfo.mKeepScreenOn == oldScreenOn && this.mAttachInfo.mSystemUiVisibility == params.subtreeSystemUiVisibility && this.mAttachInfo.mHasSystemUiListeners == params.hasSystemUiListeners) {
            return LOCAL_LOGV;
        }
        applyKeepScreenOnFlag(params);
        params.subtreeSystemUiVisibility = this.mAttachInfo.mSystemUiVisibility;
        params.hasSystemUiListeners = this.mAttachInfo.mHasSystemUiListeners;
        this.mView.dispatchWindowSystemUiVisiblityChanged(this.mAttachInfo.mSystemUiVisibility);
        return true;
    }

    private int getImpliedSystemUiVisibility(LayoutParams params) {
        int vis = 0;
        if ((params.flags & EditorInfo.IME_FLAG_NAVIGATE_PREVIOUS) != 0) {
            vis = 0 | GLES20.GL_INVALID_ENUM;
        }
        if ((params.flags & EditorInfo.IME_FLAG_NAVIGATE_NEXT) != 0) {
            return vis | GLES20.GL_SRC_COLOR;
        }
        return vis;
    }

    private boolean measureHierarchy(View host, LayoutParams lp, Resources res, int desiredWindowWidth, int desiredWindowHeight) {
        boolean goodMeasure = LOCAL_LOGV;
        if (lp.width == -2) {
            DisplayMetrics packageMetrics = res.getDisplayMetrics();
            res.getValue(17104905, this.mTmpValue, true);
            int baseSize = 0;
            if (this.mTmpValue.type == MSG_RESIZED_REPORT) {
                baseSize = (int) this.mTmpValue.getDimension(packageMetrics);
            }
            if (baseSize != 0 && desiredWindowWidth > baseSize) {
                int childWidthMeasureSpec = getRootMeasureSpec(baseSize, lp.width);
                int childHeightMeasureSpec = getRootMeasureSpec(desiredWindowHeight, lp.height);
                performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
                if ((host.getMeasuredWidthAndState() & WindowManagerPolicy.FLAG_INJECTED) == 0) {
                    goodMeasure = true;
                } else {
                    performMeasure(getRootMeasureSpec((baseSize + desiredWindowWidth) / MSG_INVALIDATE_RECT, lp.width), childHeightMeasureSpec);
                    if ((host.getMeasuredWidthAndState() & WindowManagerPolicy.FLAG_INJECTED) == 0) {
                        goodMeasure = true;
                    }
                }
            }
        }
        if (goodMeasure) {
            return LOCAL_LOGV;
        }
        performMeasure(getRootMeasureSpec(desiredWindowWidth, lp.width), getRootMeasureSpec(desiredWindowHeight, lp.height));
        if (this.mWidth == host.getMeasuredWidth() && this.mHeight == host.getMeasuredHeight()) {
            return LOCAL_LOGV;
        }
        return true;
    }

    void transformMatrixToGlobal(Matrix m) {
        m.preTranslate((float) this.mAttachInfo.mWindowLeft, (float) this.mAttachInfo.mWindowTop);
    }

    void transformMatrixToLocal(Matrix m) {
        m.postTranslate((float) (-this.mAttachInfo.mWindowLeft), (float) (-this.mAttachInfo.mWindowTop));
    }

    void dispatchApplyInsets(View host) {
        this.mDispatchContentInsets.set(this.mAttachInfo.mContentInsets);
        this.mDispatchStableInsets.set(this.mAttachInfo.mStableInsets);
        host.dispatchApplyWindowInsets(new WindowInsets(this.mDispatchContentInsets, null, this.mDispatchStableInsets, this.mWindowIsRound));
    }

    private void performTraversals() {
        View host = this.mView;
        if (host != null && this.mAdded) {
            Point size;
            int desiredWindowWidth;
            int desiredWindowHeight;
            DisplayMetrics packageMetrics;
            int i;
            this.mIsInTraversal = true;
            this.mWillDrawSoon = true;
            boolean windowSizeMayChange = LOCAL_LOGV;
            boolean newSurface = LOCAL_LOGV;
            boolean surfaceChanged = LOCAL_LOGV;
            LayoutParams lp = this.mWindowAttributes;
            int viewVisibility = getHostVisibility();
            boolean viewVisibilityChanged = (this.mViewVisibility != viewVisibility || this.mNewSurfaceNeeded) ? true : LOCAL_LOGV;
            LayoutParams params = null;
            if (this.mWindowAttributesChanged) {
                this.mWindowAttributesChanged = LOCAL_LOGV;
                surfaceChanged = true;
                params = lp;
            }
            if (this.mDisplayAdjustments.getCompatibilityInfo().supportsScreen() == this.mLastInCompatMode) {
                params = lp;
                this.mFullRedrawNeeded = true;
                this.mLayoutRequested = true;
                if (this.mLastInCompatMode) {
                    params.privateFlags &= -129;
                    this.mLastInCompatMode = LOCAL_LOGV;
                } else {
                    params.privateFlags |= AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS;
                    this.mLastInCompatMode = true;
                }
            }
            this.mWindowAttributesChangesFlag = 0;
            Rect frame = this.mWinFrame;
            if (this.mFirst) {
                this.mFullRedrawNeeded = true;
                this.mLayoutRequested = true;
                if (lp.type == LayoutParams.TYPE_STATUS_BAR_PANEL || lp.type == LayoutParams.TYPE_INPUT_METHOD) {
                    size = new Point();
                    this.mDisplay.getRealSize(size);
                    desiredWindowWidth = size.x;
                    desiredWindowHeight = size.y;
                } else {
                    packageMetrics = this.mView.getContext().getResources().getDisplayMetrics();
                    desiredWindowWidth = packageMetrics.widthPixels;
                    desiredWindowHeight = packageMetrics.heightPixels;
                }
                this.mAttachInfo.mUse32BitDrawingCache = true;
                this.mAttachInfo.mHasWindowFocus = LOCAL_LOGV;
                this.mAttachInfo.mWindowVisibility = viewVisibility;
                this.mAttachInfo.mRecomputeGlobalAttributes = LOCAL_LOGV;
                viewVisibilityChanged = LOCAL_LOGV;
                this.mLastConfiguration.setTo(host.getResources().getConfiguration());
                this.mLastSystemUiVisibility = this.mAttachInfo.mSystemUiVisibility;
                if (this.mViewLayoutDirectionInitial == MSG_INVALIDATE_RECT) {
                    host.setLayoutDirection(this.mLastConfiguration.getLayoutDirection());
                }
                host.dispatchAttachedToWindow(this.mAttachInfo, 0);
                this.mAttachInfo.mTreeObserver.dispatchOnWindowAttachedChange(true);
                dispatchApplyInsets(host);
            } else {
                desiredWindowWidth = frame.width();
                desiredWindowHeight = frame.height();
                if (!(desiredWindowWidth == this.mWidth && desiredWindowHeight == this.mHeight)) {
                    this.mFullRedrawNeeded = true;
                    this.mLayoutRequested = true;
                    windowSizeMayChange = true;
                }
            }
            if (viewVisibilityChanged) {
                this.mAttachInfo.mWindowVisibility = viewVisibility;
                host.dispatchWindowVisibilityChanged(viewVisibility);
                if (viewVisibility != 0 || this.mNewSurfaceNeeded) {
                    destroyHardwareResources();
                }
                if (viewVisibility == MSG_DISPATCH_APP_VISIBILITY) {
                    this.mHasHadWindowFocus = LOCAL_LOGV;
                }
            }
            getRunQueue().executeActions(this.mAttachInfo.mHandler);
            boolean insetsChanged = LOCAL_LOGV;
            boolean layoutRequested = (!this.mLayoutRequested || this.mStopped) ? LOCAL_LOGV : true;
            if (layoutRequested) {
                Resources res = this.mView.getContext().getResources();
                if (this.mFirst) {
                    this.mAttachInfo.mInTouchMode = !this.mAddedTouchMode ? true : LOCAL_LOGV;
                    ensureTouchModeLocally(this.mAddedTouchMode);
                } else {
                    if (!this.mPendingOverscanInsets.equals(this.mAttachInfo.mOverscanInsets)) {
                        insetsChanged = true;
                    }
                    if (!this.mPendingContentInsets.equals(this.mAttachInfo.mContentInsets)) {
                        insetsChanged = true;
                    }
                    if (!this.mPendingStableInsets.equals(this.mAttachInfo.mStableInsets)) {
                        insetsChanged = true;
                    }
                    if (!this.mPendingVisibleInsets.equals(this.mAttachInfo.mVisibleInsets)) {
                        this.mAttachInfo.mVisibleInsets.set(this.mPendingVisibleInsets);
                    }
                    if (lp.width == -2 || lp.height == -2) {
                        windowSizeMayChange = true;
                        if (lp.type == LayoutParams.TYPE_STATUS_BAR_PANEL || lp.type == LayoutParams.TYPE_INPUT_METHOD) {
                            size = new Point();
                            this.mDisplay.getRealSize(size);
                            desiredWindowWidth = size.x;
                            desiredWindowHeight = size.y;
                        } else {
                            packageMetrics = res.getDisplayMetrics();
                            desiredWindowWidth = packageMetrics.widthPixels;
                            desiredWindowHeight = packageMetrics.heightPixels;
                        }
                    }
                }
                windowSizeMayChange |= measureHierarchy(host, lp, res, desiredWindowWidth, desiredWindowHeight);
            }
            if (collectViewAttributes()) {
                params = lp;
            }
            if (this.mAttachInfo.mForceReportNewAttributes) {
                this.mAttachInfo.mForceReportNewAttributes = LOCAL_LOGV;
                params = lp;
            }
            if (this.mFirst || this.mAttachInfo.mViewVisibilityChanged) {
                this.mAttachInfo.mViewVisibilityChanged = LOCAL_LOGV;
                int resizeMode = this.mSoftInputMode & LayoutParams.SOFT_INPUT_MASK_ADJUST;
                if (resizeMode == 0) {
                    int N = this.mAttachInfo.mScrollContainers.size();
                    for (i = 0; i < N; i += MSG_INVALIDATE) {
                        if (((View) this.mAttachInfo.mScrollContainers.get(i)).isShown()) {
                            resizeMode = MSG_DISPATCH_DRAG_LOCATION_EVENT;
                        }
                    }
                    if (resizeMode == 0) {
                        resizeMode = 32;
                    }
                    if ((lp.softInputMode & LayoutParams.SOFT_INPUT_MASK_ADJUST) != resizeMode) {
                        lp.softInputMode = (lp.softInputMode & -241) | resizeMode;
                        params = lp;
                    }
                }
            }
            if (params != null) {
                if (!((host.mPrivateFlags & AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY) == 0 || PixelFormat.formatHasAlpha(params.format))) {
                    params.format = -3;
                }
                this.mAttachInfo.mOverscanRequested = (params.flags & EditorInfo.IME_FLAG_NO_FULLSCREEN) != 0 ? true : LOCAL_LOGV;
            }
            if (this.mApplyInsetsRequested) {
                this.mApplyInsetsRequested = LOCAL_LOGV;
                this.mLastOverscanRequested = this.mAttachInfo.mOverscanRequested;
                dispatchApplyInsets(host);
                if (this.mLayoutRequested) {
                    windowSizeMayChange |= measureHierarchy(host, lp, this.mView.getContext().getResources(), desiredWindowWidth, desiredWindowHeight);
                }
            }
            if (layoutRequested) {
                this.mLayoutRequested = LOCAL_LOGV;
            }
            boolean windowShouldResize = (layoutRequested && windowSizeMayChange && (this.mWidth != host.getMeasuredWidth() || this.mHeight != host.getMeasuredHeight() || ((lp.width == -2 && frame.width() < desiredWindowWidth && frame.width() != this.mWidth) || (lp.height == -2 && frame.height() < desiredWindowHeight && frame.height() != this.mHeight)))) ? true : LOCAL_LOGV;
            boolean computesInternalInsets = (this.mAttachInfo.mTreeObserver.hasComputeInternalInsetsListeners() || this.mAttachInfo.mHasNonEmptyGivenInternalInsets) ? true : LOCAL_LOGV;
            boolean insetsPending = LOCAL_LOGV;
            int relayoutResult = 0;
            if (this.mFirst || windowShouldResize || insetsChanged || viewVisibilityChanged || params != null) {
                if (viewVisibility == 0) {
                    insetsPending = (computesInternalInsets && (this.mFirst || viewVisibilityChanged)) ? true : LOCAL_LOGV;
                }
                if (this.mSurfaceHolder != null) {
                    this.mSurfaceHolder.mSurfaceLock.lock();
                    this.mDrawingAllowed = true;
                }
                boolean hwInitialized = LOCAL_LOGV;
                boolean z = LOCAL_LOGV;
                boolean hadSurface = this.mSurface.isValid();
                try {
                    if (this.mAttachInfo.mHardwareRenderer != null && this.mAttachInfo.mHardwareRenderer.pauseSurface(this.mSurface)) {
                        this.mDirty.set(0, 0, this.mWidth, this.mHeight);
                    }
                    int surfaceGenerationId = this.mSurface.getGenerationId();
                    relayoutResult = relayoutWindow(params, viewVisibility, insetsPending);
                    if (!(this.mDrawDuringWindowsAnimating || (relayoutResult & MSG_DISPATCH_APP_VISIBILITY) == 0)) {
                        this.mWindowsAnimating = true;
                    }
                    if (this.mPendingConfiguration.seq != 0) {
                        updateConfiguration(this.mPendingConfiguration, !this.mFirst ? true : LOCAL_LOGV);
                        this.mPendingConfiguration.seq = 0;
                    }
                    boolean overscanInsetsChanged = !this.mPendingOverscanInsets.equals(this.mAttachInfo.mOverscanInsets) ? true : LOCAL_LOGV;
                    z = !this.mPendingContentInsets.equals(this.mAttachInfo.mContentInsets) ? true : LOCAL_LOGV;
                    boolean visibleInsetsChanged = !this.mPendingVisibleInsets.equals(this.mAttachInfo.mVisibleInsets) ? true : LOCAL_LOGV;
                    boolean stableInsetsChanged = !this.mPendingStableInsets.equals(this.mAttachInfo.mStableInsets) ? true : LOCAL_LOGV;
                    if (z) {
                        if (!(this.mWidth <= 0 || this.mHeight <= 0 || lp == null || ((lp.systemUiVisibility | lp.subtreeSystemUiVisibility) & Major.IMAGING) != 0 || this.mSurface == null || !this.mSurface.isValid() || this.mAttachInfo.mTurnOffWindowResizeAnim || this.mAttachInfo.mHardwareRenderer == null || !this.mAttachInfo.mHardwareRenderer.isEnabled() || lp == null || PixelFormat.formatHasAlpha(lp.format) || this.mBlockResizeBuffer)) {
                            disposeResizeBuffer();
                        }
                        this.mAttachInfo.mContentInsets.set(this.mPendingContentInsets);
                    }
                    if (overscanInsetsChanged) {
                        this.mAttachInfo.mOverscanInsets.set(this.mPendingOverscanInsets);
                        z = true;
                    }
                    if (stableInsetsChanged) {
                        this.mAttachInfo.mStableInsets.set(this.mPendingStableInsets);
                        z = true;
                    }
                    if (z || this.mLastSystemUiVisibility != this.mAttachInfo.mSystemUiVisibility || this.mApplyInsetsRequested || this.mLastOverscanRequested != this.mAttachInfo.mOverscanRequested) {
                        this.mLastSystemUiVisibility = this.mAttachInfo.mSystemUiVisibility;
                        this.mLastOverscanRequested = this.mAttachInfo.mOverscanRequested;
                        this.mApplyInsetsRequested = LOCAL_LOGV;
                        dispatchApplyInsets(host);
                    }
                    if (visibleInsetsChanged) {
                        this.mAttachInfo.mVisibleInsets.set(this.mPendingVisibleInsets);
                    }
                    SurfaceHolder.Callback[] callbacks;
                    SurfaceHolder.Callback[] arr$;
                    int len$;
                    int i$;
                    int childWidthMeasureSpec;
                    int childHeightMeasureSpec;
                    int width;
                    int height;
                    boolean measureAgain;
                    if (hadSurface) {
                        if (!this.mSurface.isValid()) {
                            if (this.mLastScrolledFocus != null) {
                                this.mLastScrolledFocus.clear();
                            }
                            this.mCurScrollY = 0;
                            this.mScrollY = 0;
                            if (this.mView instanceof RootViewSurfaceTaker) {
                                ((RootViewSurfaceTaker) this.mView).onRootViewScrollYChanged(this.mCurScrollY);
                            }
                            if (this.mScroller != null) {
                                this.mScroller.abortAnimation();
                            }
                            disposeResizeBuffer();
                            if (this.mAttachInfo.mHardwareRenderer != null && this.mAttachInfo.mHardwareRenderer.isEnabled()) {
                                this.mAttachInfo.mHardwareRenderer.destroy();
                            }
                        } else if (!(surfaceGenerationId == this.mSurface.getGenerationId() || this.mSurfaceHolder != null || this.mAttachInfo.mHardwareRenderer == null)) {
                            this.mFullRedrawNeeded = true;
                            try {
                                this.mAttachInfo.mHardwareRenderer.updateSurface(this.mSurface);
                            } catch (OutOfResourcesException e) {
                                handleOutOfResourcesException(e);
                                return;
                            }
                        }
                        this.mAttachInfo.mWindowLeft = frame.left;
                        this.mAttachInfo.mWindowTop = frame.top;
                        this.mWidth = frame.width();
                        this.mHeight = frame.height();
                        if (this.mSurfaceHolder != null) {
                            if (this.mSurface.isValid()) {
                                this.mSurfaceHolder.mSurface = this.mSurface;
                            }
                            this.mSurfaceHolder.setSurfaceFrameSize(this.mWidth, this.mHeight);
                            this.mSurfaceHolder.mSurfaceLock.unlock();
                            if (this.mSurface.isValid()) {
                                if (!hadSurface) {
                                    this.mSurfaceHolder.ungetCallbacks();
                                    this.mIsCreating = true;
                                    this.mSurfaceHolderCallback.surfaceCreated(this.mSurfaceHolder);
                                    callbacks = this.mSurfaceHolder.getCallbacks();
                                    if (callbacks != null) {
                                        arr$ = callbacks;
                                        len$ = arr$.length;
                                        for (i$ = 0; i$ < len$; i$ += MSG_INVALIDATE) {
                                            arr$[i$].surfaceCreated(this.mSurfaceHolder);
                                        }
                                    }
                                    surfaceChanged = true;
                                }
                                if (surfaceChanged) {
                                    this.mSurfaceHolderCallback.surfaceChanged(this.mSurfaceHolder, lp.format, this.mWidth, this.mHeight);
                                    callbacks = this.mSurfaceHolder.getCallbacks();
                                    if (callbacks != null) {
                                        arr$ = callbacks;
                                        len$ = arr$.length;
                                        for (i$ = 0; i$ < len$; i$ += MSG_INVALIDATE) {
                                            arr$[i$].surfaceChanged(this.mSurfaceHolder, lp.format, this.mWidth, this.mHeight);
                                        }
                                    }
                                }
                                this.mIsCreating = LOCAL_LOGV;
                            } else if (hadSurface) {
                                this.mSurfaceHolder.ungetCallbacks();
                                callbacks = this.mSurfaceHolder.getCallbacks();
                                this.mSurfaceHolderCallback.surfaceDestroyed(this.mSurfaceHolder);
                                if (callbacks != null) {
                                    arr$ = callbacks;
                                    len$ = arr$.length;
                                    for (i$ = 0; i$ < len$; i$ += MSG_INVALIDATE) {
                                        arr$[i$].surfaceDestroyed(this.mSurfaceHolder);
                                    }
                                }
                                this.mSurfaceHolder.mSurfaceLock.lock();
                                this.mSurfaceHolder.mSurface = new Surface();
                            }
                        }
                        this.mAttachInfo.mHardwareRenderer.setup(this.mWidth, this.mHeight, this.mWindowAttributes.surfaceInsets);
                        if (!hwInitialized) {
                            this.mAttachInfo.mHardwareRenderer.invalidate(this.mSurface);
                            this.mFullRedrawNeeded = true;
                        }
                        if (!this.mStopped) {
                            if ((relayoutResult & MSG_INVALIDATE) == 0) {
                            }
                            childWidthMeasureSpec = getRootMeasureSpec(this.mWidth, lp.width);
                            childHeightMeasureSpec = getRootMeasureSpec(this.mHeight, lp.height);
                            performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
                            width = host.getMeasuredWidth();
                            height = host.getMeasuredHeight();
                            measureAgain = LOCAL_LOGV;
                            if (lp.horizontalWeight > 0.0f) {
                                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width + ((int) (((float) (this.mWidth - width)) * lp.horizontalWeight)), EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                                measureAgain = true;
                            }
                            if (lp.verticalWeight > 0.0f) {
                                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height + ((int) (((float) (this.mHeight - height)) * lp.verticalWeight)), EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                                measureAgain = true;
                            }
                            if (measureAgain) {
                                performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
                            }
                            layoutRequested = true;
                        }
                    } else {
                        if (this.mSurface.isValid()) {
                            newSurface = true;
                            this.mFullRedrawNeeded = true;
                            this.mPreviousTransparentRegion.setEmpty();
                            if (this.mAttachInfo.mHardwareRenderer != null) {
                                try {
                                    hwInitialized = this.mAttachInfo.mHardwareRenderer.initialize(this.mSurface);
                                } catch (OutOfResourcesException e2) {
                                    handleOutOfResourcesException(e2);
                                    return;
                                }
                            }
                        }
                        this.mAttachInfo.mWindowLeft = frame.left;
                        this.mAttachInfo.mWindowTop = frame.top;
                        if (!(this.mWidth == frame.width() && this.mHeight == frame.height())) {
                            this.mWidth = frame.width();
                            this.mHeight = frame.height();
                        }
                        if (this.mSurfaceHolder != null) {
                            if (this.mSurface.isValid()) {
                                this.mSurfaceHolder.mSurface = this.mSurface;
                            }
                            this.mSurfaceHolder.setSurfaceFrameSize(this.mWidth, this.mHeight);
                            this.mSurfaceHolder.mSurfaceLock.unlock();
                            if (this.mSurface.isValid()) {
                                if (hadSurface) {
                                    this.mSurfaceHolder.ungetCallbacks();
                                    this.mIsCreating = true;
                                    this.mSurfaceHolderCallback.surfaceCreated(this.mSurfaceHolder);
                                    callbacks = this.mSurfaceHolder.getCallbacks();
                                    if (callbacks != null) {
                                        arr$ = callbacks;
                                        len$ = arr$.length;
                                        for (i$ = 0; i$ < len$; i$ += MSG_INVALIDATE) {
                                            arr$[i$].surfaceCreated(this.mSurfaceHolder);
                                        }
                                    }
                                    surfaceChanged = true;
                                }
                                if (surfaceChanged) {
                                    this.mSurfaceHolderCallback.surfaceChanged(this.mSurfaceHolder, lp.format, this.mWidth, this.mHeight);
                                    callbacks = this.mSurfaceHolder.getCallbacks();
                                    if (callbacks != null) {
                                        arr$ = callbacks;
                                        len$ = arr$.length;
                                        for (i$ = 0; i$ < len$; i$ += MSG_INVALIDATE) {
                                            arr$[i$].surfaceChanged(this.mSurfaceHolder, lp.format, this.mWidth, this.mHeight);
                                        }
                                    }
                                }
                                this.mIsCreating = LOCAL_LOGV;
                            } else if (hadSurface) {
                                this.mSurfaceHolder.ungetCallbacks();
                                callbacks = this.mSurfaceHolder.getCallbacks();
                                this.mSurfaceHolderCallback.surfaceDestroyed(this.mSurfaceHolder);
                                if (callbacks != null) {
                                    arr$ = callbacks;
                                    len$ = arr$.length;
                                    for (i$ = 0; i$ < len$; i$ += MSG_INVALIDATE) {
                                        arr$[i$].surfaceDestroyed(this.mSurfaceHolder);
                                    }
                                }
                                this.mSurfaceHolder.mSurfaceLock.lock();
                                try {
                                    this.mSurfaceHolder.mSurface = new Surface();
                                } finally {
                                    this.mSurfaceHolder.mSurfaceLock.unlock();
                                }
                            }
                        }
                        if (!(this.mAttachInfo.mHardwareRenderer == null || !this.mAttachInfo.mHardwareRenderer.isEnabled() || (!hwInitialized && this.mWidth == this.mAttachInfo.mHardwareRenderer.getWidth() && this.mHeight == this.mAttachInfo.mHardwareRenderer.getHeight()))) {
                            this.mAttachInfo.mHardwareRenderer.setup(this.mWidth, this.mHeight, this.mWindowAttributes.surfaceInsets);
                            if (hwInitialized) {
                                this.mAttachInfo.mHardwareRenderer.invalidate(this.mSurface);
                                this.mFullRedrawNeeded = true;
                            }
                        }
                        if (this.mStopped) {
                            if (ensureTouchModeLocally((relayoutResult & MSG_INVALIDATE) == 0 ? true : LOCAL_LOGV) || this.mWidth != host.getMeasuredWidth() || this.mHeight != host.getMeasuredHeight() || contentInsetsChanged) {
                                childWidthMeasureSpec = getRootMeasureSpec(this.mWidth, lp.width);
                                childHeightMeasureSpec = getRootMeasureSpec(this.mHeight, lp.height);
                                performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
                                width = host.getMeasuredWidth();
                                height = host.getMeasuredHeight();
                                measureAgain = LOCAL_LOGV;
                                if (lp.horizontalWeight > 0.0f) {
                                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width + ((int) (((float) (this.mWidth - width)) * lp.horizontalWeight)), EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                                    measureAgain = true;
                                }
                                if (lp.verticalWeight > 0.0f) {
                                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height + ((int) (((float) (this.mHeight - height)) * lp.verticalWeight)), EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                                    measureAgain = true;
                                }
                                if (measureAgain) {
                                    performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
                                }
                                layoutRequested = true;
                            }
                        }
                    }
                } catch (RemoteException e3) {
                }
            } else {
                boolean windowMoved = (this.mAttachInfo.mWindowLeft == frame.left && this.mAttachInfo.mWindowTop == frame.top) ? LOCAL_LOGV : true;
                if (windowMoved) {
                    if (this.mTranslator != null) {
                        this.mTranslator.translateRectInScreenToAppWinFrame(frame);
                    }
                    this.mAttachInfo.mWindowLeft = frame.left;
                    this.mAttachInfo.mWindowTop = frame.top;
                }
            }
            boolean didLayout = (!layoutRequested || this.mStopped) ? LOCAL_LOGV : true;
            boolean triggerGlobalLayoutListener = (didLayout || this.mAttachInfo.mRecomputeGlobalAttributes) ? true : LOCAL_LOGV;
            if (didLayout) {
                performLayout(lp, desiredWindowWidth, desiredWindowHeight);
                if ((host.mPrivateFlags & AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY) != 0) {
                    host.getLocationInWindow(this.mTmpLocation);
                    this.mTransparentRegion.set(this.mTmpLocation[0], this.mTmpLocation[MSG_INVALIDATE], (this.mTmpLocation[0] + host.mRight) - host.mLeft, (this.mTmpLocation[MSG_INVALIDATE] + host.mBottom) - host.mTop);
                    host.gatherTransparentRegion(this.mTransparentRegion);
                    if (this.mTranslator != null) {
                        this.mTranslator.translateRegionInWindowToScreen(this.mTransparentRegion);
                    }
                    if (!this.mTransparentRegion.equals(this.mPreviousTransparentRegion)) {
                        this.mPreviousTransparentRegion.set(this.mTransparentRegion);
                        this.mFullRedrawNeeded = true;
                        try {
                            this.mWindowSession.setTransparentRegion(this.mWindow, this.mTransparentRegion);
                        } catch (RemoteException e4) {
                        }
                    }
                }
            }
            if (triggerGlobalLayoutListener) {
                this.mAttachInfo.mRecomputeGlobalAttributes = LOCAL_LOGV;
                this.mAttachInfo.mTreeObserver.dispatchOnGlobalLayout();
            }
            if (computesInternalInsets) {
                InternalInsetsInfo insets = this.mAttachInfo.mGivenInternalInsets;
                insets.reset();
                this.mAttachInfo.mTreeObserver.dispatchOnComputeInternalInsets(insets);
                this.mAttachInfo.mHasNonEmptyGivenInternalInsets = !insets.isEmpty() ? true : LOCAL_LOGV;
                if (insetsPending || !this.mLastGivenInsets.equals(insets)) {
                    Rect contentInsets;
                    Rect visibleInsets;
                    Region touchableRegion;
                    this.mLastGivenInsets.set(insets);
                    if (this.mTranslator != null) {
                        contentInsets = this.mTranslator.getTranslatedContentInsets(insets.contentInsets);
                        visibleInsets = this.mTranslator.getTranslatedVisibleInsets(insets.visibleInsets);
                        touchableRegion = this.mTranslator.getTranslatedTouchableArea(insets.touchableRegion);
                    } else {
                        contentInsets = insets.contentInsets;
                        visibleInsets = insets.visibleInsets;
                        touchableRegion = insets.touchableRegion;
                    }
                    try {
                        this.mWindowSession.setInsets(this.mWindow, insets.mTouchableInsets, contentInsets, visibleInsets, touchableRegion);
                    } catch (RemoteException e5) {
                    }
                }
            }
            boolean skipDraw = LOCAL_LOGV;
            if (this.mFirst) {
                if (!(this.mView == null || this.mView.hasFocus())) {
                    this.mView.requestFocus(MSG_INVALIDATE_RECT);
                }
                if ((relayoutResult & MSG_DISPATCH_APP_VISIBILITY) != 0) {
                    this.mWindowsAnimating = true;
                }
            } else if (this.mWindowsAnimating) {
                skipDraw = true;
            }
            this.mFirst = LOCAL_LOGV;
            this.mWillDrawSoon = LOCAL_LOGV;
            this.mNewSurfaceNeeded = LOCAL_LOGV;
            this.mViewVisibility = viewVisibility;
            if (this.mAttachInfo.mHasWindowFocus && !isInLocalFocusMode()) {
                boolean imTarget = LayoutParams.mayUseInputMethod(this.mWindowAttributes.flags);
                if (imTarget != this.mLastWasImTarget) {
                    this.mLastWasImTarget = imTarget;
                    InputMethodManager imm = InputMethodManager.peekInstance();
                    if (imm != null && imTarget) {
                        imm.startGettingWindowFocus(this.mView);
                        imm.onWindowFocus(this.mView, this.mView.findFocus(), this.mWindowAttributes.softInputMode, !this.mHasHadWindowFocus ? true : LOCAL_LOGV, this.mWindowAttributes.flags);
                    }
                }
            }
            if ((relayoutResult & MSG_INVALIDATE_RECT) != 0) {
                this.mReportNextDraw = true;
            }
            boolean cancelDraw = (this.mAttachInfo.mTreeObserver.dispatchOnPreDraw() || viewVisibility != 0) ? true : LOCAL_LOGV;
            if (cancelDraw || newSurface) {
                if (viewVisibility == 0) {
                    scheduleTraversals();
                } else if (this.mPendingTransitions != null && this.mPendingTransitions.size() > 0) {
                    for (i = 0; i < this.mPendingTransitions.size(); i += MSG_INVALIDATE) {
                        ((LayoutTransition) this.mPendingTransitions.get(i)).endChangingAnimations();
                    }
                    this.mPendingTransitions.clear();
                }
            } else if (!skipDraw || this.mReportNextDraw) {
                if (this.mPendingTransitions != null && this.mPendingTransitions.size() > 0) {
                    for (i = 0; i < this.mPendingTransitions.size(); i += MSG_INVALIDATE) {
                        ((LayoutTransition) this.mPendingTransitions.get(i)).startChangingAnimations();
                    }
                    this.mPendingTransitions.clear();
                }
                performDraw();
            }
            this.mIsInTraversal = LOCAL_LOGV;
        }
    }

    private void handleOutOfResourcesException(OutOfResourcesException e) {
        Log.e(TAG, "OutOfResourcesException initializing HW surface", e);
        try {
            if (!(this.mWindowSession.outOfMemory(this.mWindow) || Process.myUid() == LayoutParams.TYPE_APPLICATION_PANEL)) {
                Slog.w(TAG, "No processes killed for memory; killing self");
                Process.killProcess(Process.myPid());
            }
        } catch (RemoteException e2) {
        }
        this.mLayoutRequested = true;
    }

    private void performMeasure(int childWidthMeasureSpec, int childHeightMeasureSpec) {
        Trace.traceBegin(8, "measure");
        try {
            this.mView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        } finally {
            Trace.traceEnd(8);
        }
    }

    boolean isInLayout() {
        return this.mInLayout;
    }

    boolean requestLayoutDuringLayout(View view) {
        if (view.mParent == null || view.mAttachInfo == null) {
            return true;
        }
        if (!this.mLayoutRequesters.contains(view)) {
            this.mLayoutRequesters.add(view);
        }
        if (this.mHandlingLayoutInLayoutRequest) {
            return LOCAL_LOGV;
        }
        return true;
    }

    private void performLayout(LayoutParams lp, int desiredWindowWidth, int desiredWindowHeight) {
        this.mLayoutRequested = LOCAL_LOGV;
        this.mScrollMayChange = true;
        this.mInLayout = true;
        View host = this.mView;
        Trace.traceBegin(8, "layout");
        try {
            host.layout(0, 0, host.getMeasuredWidth(), host.getMeasuredHeight());
            this.mInLayout = LOCAL_LOGV;
            if (this.mLayoutRequesters.size() > 0) {
                ArrayList<View> validLayoutRequesters = getValidLayoutRequesters(this.mLayoutRequesters, LOCAL_LOGV);
                if (validLayoutRequesters != null) {
                    this.mHandlingLayoutInLayoutRequest = true;
                    int numValidRequests = validLayoutRequesters.size();
                    for (int i = 0; i < numValidRequests; i += MSG_INVALIDATE) {
                        View view = (View) validLayoutRequesters.get(i);
                        Log.w("View", "requestLayout() improperly called by " + view + " during layout: running second layout pass");
                        view.requestLayout();
                    }
                    measureHierarchy(host, lp, this.mView.getContext().getResources(), desiredWindowWidth, desiredWindowHeight);
                    this.mInLayout = true;
                    host.layout(0, 0, host.getMeasuredWidth(), host.getMeasuredHeight());
                    this.mHandlingLayoutInLayoutRequest = LOCAL_LOGV;
                    validLayoutRequesters = getValidLayoutRequesters(this.mLayoutRequesters, true);
                    if (validLayoutRequesters != null) {
                        getRunQueue().post(new AnonymousClass2(this, validLayoutRequesters));
                    }
                }
            }
            Trace.traceEnd(8);
            this.mInLayout = LOCAL_LOGV;
        } catch (Throwable th) {
            Trace.traceEnd(8);
        }
    }

    private ArrayList<View> getValidLayoutRequesters(ArrayList<View> layoutRequesters, boolean secondLayoutRequests) {
        int i;
        int numViewsRequestingLayout = layoutRequesters.size();
        ArrayList<View> validLayoutRequesters = null;
        for (i = 0; i < numViewsRequestingLayout; i += MSG_INVALIDATE) {
            View view = (View) layoutRequesters.get(i);
            if (!(view == null || view.mAttachInfo == null || view.mParent == null || (!secondLayoutRequests && (view.mPrivateFlags & AccessibilityNodeInfo.ACTION_SCROLL_FORWARD) != AccessibilityNodeInfo.ACTION_SCROLL_FORWARD))) {
                boolean gone = LOCAL_LOGV;
                View parent = view;
                while (parent != null) {
                    if ((parent.mViewFlags & MSG_FINISH_INPUT_CONNECTION) == MSG_DISPATCH_APP_VISIBILITY) {
                        gone = true;
                        break;
                    } else if (parent.mParent instanceof View) {
                        parent = parent.mParent;
                    } else {
                        parent = null;
                    }
                }
                if (!gone) {
                    if (validLayoutRequesters == null) {
                        validLayoutRequesters = new ArrayList();
                    }
                    validLayoutRequesters.add(view);
                }
            }
        }
        if (!secondLayoutRequests) {
            for (i = 0; i < numViewsRequestingLayout; i += MSG_INVALIDATE) {
                view = (View) layoutRequesters.get(i);
                while (view != null && (view.mPrivateFlags & AccessibilityNodeInfo.ACTION_SCROLL_FORWARD) != 0) {
                    view.mPrivateFlags &= -4097;
                    if (view.mParent instanceof View) {
                        view = view.mParent;
                    } else {
                        view = null;
                    }
                }
            }
        }
        layoutRequesters.clear();
        return validLayoutRequesters;
    }

    public void requestTransparentRegion(View child) {
        checkThread();
        if (this.mView == child) {
            View view = this.mView;
            view.mPrivateFlags |= AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY;
            this.mWindowAttributesChanged = true;
            this.mWindowAttributesChangesFlag = 0;
            requestLayout();
        }
    }

    private static int getRootMeasureSpec(int windowSize, int rootDimension) {
        switch (rootDimension) {
            case ListPopupWindow.WRAP_CONTENT /*-2*/:
                return MeasureSpec.makeMeasureSpec(windowSize, RtlSpacingHelper.UNDEFINED);
            case ListPopupWindow.MATCH_PARENT /*-1*/:
                return MeasureSpec.makeMeasureSpec(windowSize, EditorInfo.IME_FLAG_NO_ENTER_ACTION);
            default:
                return MeasureSpec.makeMeasureSpec(rootDimension, EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        }
    }

    public void onHardwarePreDraw(HardwareCanvas canvas) {
        canvas.translate((float) (-this.mHardwareXOffset), (float) (-this.mHardwareYOffset));
    }

    public void onHardwarePostDraw(HardwareCanvas canvas) {
        if (this.mResizeBuffer != null) {
            this.mResizePaint.setAlpha(this.mResizeAlpha);
            canvas.drawHardwareLayer(this.mResizeBuffer, (float) this.mHardwareXOffset, (float) this.mHardwareYOffset, this.mResizePaint);
        }
        drawAccessibilityFocusedDrawableIfNeeded(canvas);
    }

    void outputDisplayList(View view) {
        RenderNode renderNode = view.getDisplayList();
        if (renderNode != null) {
            renderNode.output();
        }
    }

    private void profileRendering(boolean enabled) {
        if (this.mProfileRendering) {
            this.mRenderProfilingEnabled = enabled;
            if (this.mRenderProfiler != null) {
                this.mChoreographer.removeFrameCallback(this.mRenderProfiler);
            }
            if (this.mRenderProfilingEnabled) {
                if (this.mRenderProfiler == null) {
                    this.mRenderProfiler = new AnonymousClass3(this);
                }
                this.mChoreographer.postFrameCallback(this.mRenderProfiler);
                return;
            }
            this.mRenderProfiler = null;
        }
    }

    private void trackFPS() {
        long nowTime = System.currentTimeMillis();
        if (this.mFpsStartTime < 0) {
            this.mFpsPrevTime = nowTime;
            this.mFpsStartTime = nowTime;
            this.mFpsNumFrames = 0;
            return;
        }
        this.mFpsNumFrames += MSG_INVALIDATE;
        String thisHash = Integer.toHexString(System.identityHashCode(this));
        long totalTime = nowTime - this.mFpsStartTime;
        Log.v(TAG, "0x" + thisHash + "\tFrame time:\t" + (nowTime - this.mFpsPrevTime));
        this.mFpsPrevTime = nowTime;
        if (totalTime > 1000) {
            Log.v(TAG, "0x" + thisHash + "\tFPS:\t" + ((((float) this.mFpsNumFrames) * 1000.0f) / ((float) totalTime)));
            this.mFpsStartTime = nowTime;
            this.mFpsNumFrames = 0;
        }
    }

    private void performDraw() {
        if (this.mAttachInfo.mDisplayState != MSG_INVALIDATE || this.mReportNextDraw) {
            boolean fullRedrawNeeded = this.mFullRedrawNeeded;
            this.mFullRedrawNeeded = LOCAL_LOGV;
            this.mIsDrawing = true;
            Trace.traceBegin(8, "draw");
            try {
                draw(fullRedrawNeeded);
                if (this.mAttachInfo.mPendingAnimatingRenderNodes != null) {
                    int count = this.mAttachInfo.mPendingAnimatingRenderNodes.size();
                    for (int i = 0; i < count; i += MSG_INVALIDATE) {
                        ((RenderNode) this.mAttachInfo.mPendingAnimatingRenderNodes.get(i)).endAllAnimators();
                    }
                    this.mAttachInfo.mPendingAnimatingRenderNodes.clear();
                }
                if (this.mReportNextDraw) {
                    this.mReportNextDraw = LOCAL_LOGV;
                    if (this.mAttachInfo.mHardwareRenderer != null) {
                        this.mAttachInfo.mHardwareRenderer.fence();
                    }
                    if (this.mSurfaceHolder != null && this.mSurface.isValid()) {
                        this.mSurfaceHolderCallback.surfaceRedrawNeeded(this.mSurfaceHolder);
                        SurfaceHolder.Callback[] callbacks = this.mSurfaceHolder.getCallbacks();
                        if (callbacks != null) {
                            SurfaceHolder.Callback[] arr$ = callbacks;
                            int len$ = arr$.length;
                            for (int i$ = 0; i$ < len$; i$ += MSG_INVALIDATE) {
                                SurfaceHolder.Callback c = arr$[i$];
                                if (c instanceof Callback2) {
                                    ((Callback2) c).surfaceRedrawNeeded(this.mSurfaceHolder);
                                }
                            }
                        }
                    }
                    try {
                        this.mWindowSession.finishDrawing(this.mWindow);
                    } catch (RemoteException e) {
                    }
                }
            } finally {
                this.mIsDrawing = LOCAL_LOGV;
                Trace.traceEnd(8);
            }
        }
    }

    private void draw(boolean fullRedrawNeeded) {
        Surface surface = this.mSurface;
        if (surface.isValid()) {
            int curScrollY;
            if (!sFirstDrawComplete) {
                synchronized (sFirstDrawHandlers) {
                    sFirstDrawComplete = true;
                    int count = sFirstDrawHandlers.size();
                    for (int i = 0; i < count; i += MSG_INVALIDATE) {
                        this.mHandler.post((Runnable) sFirstDrawHandlers.get(i));
                    }
                }
            }
            scrollToRectOrFocus(null, LOCAL_LOGV);
            if (this.mAttachInfo.mViewScrollChanged) {
                this.mAttachInfo.mViewScrollChanged = LOCAL_LOGV;
                this.mAttachInfo.mTreeObserver.dispatchOnScrollChanged();
            }
            boolean animating = (this.mScroller == null || !this.mScroller.computeScrollOffset()) ? LOCAL_LOGV : true;
            if (animating) {
                curScrollY = this.mScroller.getCurrY();
            } else {
                curScrollY = this.mScrollY;
            }
            if (this.mCurScrollY != curScrollY) {
                this.mCurScrollY = curScrollY;
                fullRedrawNeeded = true;
                if (this.mView instanceof RootViewSurfaceTaker) {
                    ((RootViewSurfaceTaker) this.mView).onRootViewScrollYChanged(this.mCurScrollY);
                }
            }
            float appScale = this.mAttachInfo.mApplicationScale;
            boolean scalingRequired = this.mAttachInfo.mScalingRequired;
            int resizeAlpha = 0;
            if (this.mResizeBuffer != null) {
                long deltaTime = SystemClock.uptimeMillis() - this.mResizeBufferStartTime;
                if (deltaTime < ((long) this.mResizeBufferDuration)) {
                    animating = true;
                    resizeAlpha = 255 - ((int) (255.0f * mResizeInterpolator.getInterpolation(((float) deltaTime) / ((float) this.mResizeBufferDuration))));
                } else {
                    disposeResizeBuffer();
                }
            }
            Rect dirty = this.mDirty;
            if (this.mSurfaceHolder != null) {
                dirty.setEmpty();
                if (animating) {
                    if (this.mScroller != null) {
                        this.mScroller.abortAnimation();
                    }
                    disposeResizeBuffer();
                    return;
                }
                return;
            }
            if (fullRedrawNeeded) {
                this.mAttachInfo.mIgnoreDirtyState = true;
                dirty.set(0, 0, (int) ((((float) this.mWidth) * appScale) + 0.5f), (int) ((((float) this.mHeight) * appScale) + 0.5f));
            }
            this.mAttachInfo.mTreeObserver.dispatchOnDraw();
            int xOffset = 0;
            int yOffset = curScrollY;
            LayoutParams params = this.mWindowAttributes;
            Rect surfaceInsets = params != null ? params.surfaceInsets : null;
            if (surfaceInsets != null) {
                xOffset = 0 - surfaceInsets.left;
                yOffset -= surfaceInsets.top;
                dirty.offset(surfaceInsets.left, surfaceInsets.right);
            }
            boolean accessibilityFocusDirty = LOCAL_LOGV;
            Drawable drawable = this.mAttachInfo.mAccessibilityFocusDrawable;
            if (drawable != null) {
                Rect bounds = this.mAttachInfo.mTmpInvalRect;
                if (!getAccessibilityFocusedRect(bounds)) {
                    bounds.setEmpty();
                }
                if (!bounds.equals(drawable.getBounds())) {
                    accessibilityFocusDirty = true;
                }
            }
            if (!dirty.isEmpty() || this.mIsAnimating || accessibilityFocusDirty) {
                if (this.mAttachInfo.mHardwareRenderer != null && this.mAttachInfo.mHardwareRenderer.isEnabled()) {
                    boolean invalidateRoot = accessibilityFocusDirty;
                    this.mIsAnimating = LOCAL_LOGV;
                    if (!(this.mHardwareYOffset == yOffset && this.mHardwareXOffset == xOffset)) {
                        this.mHardwareYOffset = yOffset;
                        this.mHardwareXOffset = xOffset;
                        invalidateRoot = true;
                    }
                    this.mResizeAlpha = resizeAlpha;
                    if (invalidateRoot) {
                        this.mAttachInfo.mHardwareRenderer.invalidateRoot();
                    }
                    dirty.setEmpty();
                    this.mBlockResizeBuffer = LOCAL_LOGV;
                    this.mAttachInfo.mHardwareRenderer.draw(this.mView, this.mAttachInfo, this);
                } else if (this.mAttachInfo.mHardwareRenderer != null && !this.mAttachInfo.mHardwareRenderer.isEnabled() && this.mAttachInfo.mHardwareRenderer.isRequested()) {
                    try {
                        this.mAttachInfo.mHardwareRenderer.initializeIfNeeded(this.mWidth, this.mHeight, this.mSurface, surfaceInsets);
                        this.mFullRedrawNeeded = true;
                        scheduleTraversals();
                        return;
                    } catch (OutOfResourcesException e) {
                        handleOutOfResourcesException(e);
                        return;
                    }
                } else if (!drawSoftware(surface, this.mAttachInfo, xOffset, yOffset, scalingRequired, dirty)) {
                    return;
                }
            }
            if (animating) {
                this.mFullRedrawNeeded = true;
                scheduleTraversals();
            }
        }
    }

    private boolean drawSoftware(Surface surface, AttachInfo attachInfo, int xoff, int yoff, boolean scalingRequired, Rect dirty) {
        try {
            int left = dirty.left;
            int top = dirty.top;
            int right = dirty.right;
            int bottom = dirty.bottom;
            Canvas canvas = this.mSurface.lockCanvas(dirty);
            if (!(left == dirty.left && top == dirty.top && right == dirty.right && bottom == dirty.bottom)) {
                attachInfo.mIgnoreDirtyState = true;
            }
            canvas.setDensity(this.mDensity);
            try {
                if (!(canvas.isOpaque() && yoff == 0 && xoff == 0)) {
                    canvas.drawColor(0, Mode.CLEAR);
                }
                dirty.setEmpty();
                this.mIsAnimating = LOCAL_LOGV;
                attachInfo.mDrawingTime = SystemClock.uptimeMillis();
                View view = this.mView;
                view.mPrivateFlags |= 32;
                canvas.translate((float) (-xoff), (float) (-yoff));
                if (this.mTranslator != null) {
                    this.mTranslator.translateCanvas(canvas);
                }
                canvas.setScreenDensity(scalingRequired ? this.mNoncompatDensity : 0);
                attachInfo.mSetIgnoreDirtyState = LOCAL_LOGV;
                this.mView.draw(canvas);
                drawAccessibilityFocusedDrawableIfNeeded(canvas);
                if (!attachInfo.mSetIgnoreDirtyState) {
                    attachInfo.mIgnoreDirtyState = LOCAL_LOGV;
                }
                try {
                    surface.unlockCanvasAndPost(canvas);
                    return true;
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Could not unlock surface", e);
                    this.mLayoutRequested = true;
                    return LOCAL_LOGV;
                }
            } catch (Throwable th) {
                try {
                    surface.unlockCanvasAndPost(canvas);
                } catch (IllegalArgumentException e2) {
                    Log.e(TAG, "Could not unlock surface", e2);
                    this.mLayoutRequested = true;
                    return LOCAL_LOGV;
                }
            }
        } catch (OutOfResourcesException e3) {
            handleOutOfResourcesException(e3);
            return LOCAL_LOGV;
        } catch (IllegalArgumentException e22) {
            Log.e(TAG, "Could not lock surface", e22);
            this.mLayoutRequested = true;
            return LOCAL_LOGV;
        }
    }

    private void drawAccessibilityFocusedDrawableIfNeeded(Canvas canvas) {
        Rect bounds = this.mAttachInfo.mTmpInvalRect;
        if (getAccessibilityFocusedRect(bounds)) {
            Drawable drawable = getAccessibilityFocusedDrawable();
            if (drawable != null) {
                drawable.setBounds(bounds);
                drawable.draw(canvas);
            }
        } else if (this.mAttachInfo.mAccessibilityFocusDrawable != null) {
            this.mAttachInfo.mAccessibilityFocusDrawable.setBounds(0, 0, 0, 0);
        }
    }

    private boolean getAccessibilityFocusedRect(Rect bounds) {
        boolean z = true;
        AccessibilityManager manager = AccessibilityManager.getInstance(this.mView.mContext);
        if (!manager.isEnabled() || !manager.isTouchExplorationEnabled()) {
            return LOCAL_LOGV;
        }
        View host = this.mAccessibilityFocusedHost;
        if (host == null || host.mAttachInfo == null) {
            return LOCAL_LOGV;
        }
        if (host.getAccessibilityNodeProvider() == null) {
            host.getBoundsOnScreen(bounds, true);
        } else if (this.mAccessibilityFocusedVirtualView == null) {
            return LOCAL_LOGV;
        } else {
            this.mAccessibilityFocusedVirtualView.getBoundsInScreen(bounds);
        }
        AttachInfo attachInfo = this.mAttachInfo;
        bounds.offset(-attachInfo.mWindowLeft, -attachInfo.mWindowTop);
        bounds.intersect(0, 0, attachInfo.mViewRootImpl.mWidth, attachInfo.mViewRootImpl.mHeight);
        if (bounds.isEmpty()) {
            z = LOCAL_LOGV;
        }
        return z;
    }

    private Drawable getAccessibilityFocusedDrawable() {
        if (this.mAttachInfo.mAccessibilityFocusDrawable == null) {
            TypedValue value = new TypedValue();
            if (this.mView.mContext.getTheme().resolveAttribute(18219049, value, true)) {
                this.mAttachInfo.mAccessibilityFocusDrawable = this.mView.mContext.getDrawable(value.resourceId);
            }
        }
        return this.mAttachInfo.mAccessibilityFocusDrawable;
    }

    public void setDrawDuringWindowsAnimating(boolean value) {
        this.mDrawDuringWindowsAnimating = value;
        if (value) {
            handleDispatchDoneAnimating();
        }
    }

    boolean scrollToRectOrFocus(Rect rectangle, boolean immediate) {
        Rect ci = this.mAttachInfo.mContentInsets;
        Rect vi = this.mAttachInfo.mVisibleInsets;
        int scrollY = 0;
        boolean handled = LOCAL_LOGV;
        if (vi.left > ci.left || vi.top > ci.top || vi.right > ci.right || vi.bottom > ci.bottom) {
            scrollY = this.mScrollY;
            View focus = this.mView.findFocus();
            if (focus == null) {
                return LOCAL_LOGV;
            }
            View lastScrolledFocus;
            if (this.mLastScrolledFocus != null) {
                lastScrolledFocus = (View) this.mLastScrolledFocus.get();
            } else {
                lastScrolledFocus = null;
            }
            if (focus != lastScrolledFocus) {
                rectangle = null;
            }
            if (!(focus == lastScrolledFocus && !this.mScrollMayChange && rectangle == null)) {
                this.mLastScrolledFocus = new WeakReference(focus);
                this.mScrollMayChange = LOCAL_LOGV;
                if (focus.getGlobalVisibleRect(this.mVisRect, null)) {
                    if (rectangle == null) {
                        focus.getFocusedRect(this.mTempRect);
                        if (this.mView instanceof ViewGroup) {
                            ((ViewGroup) this.mView).offsetDescendantRectToMyCoords(focus, this.mTempRect);
                        }
                    } else {
                        this.mTempRect.set(rectangle);
                    }
                    if (this.mTempRect.intersect(this.mVisRect)) {
                        if (this.mTempRect.height() <= (this.mView.getHeight() - vi.top) - vi.bottom) {
                            if (this.mTempRect.top - scrollY < vi.top) {
                                scrollY -= vi.top - (this.mTempRect.top - scrollY);
                            } else if (this.mTempRect.bottom - scrollY > this.mView.getHeight() - vi.bottom) {
                                scrollY += (this.mTempRect.bottom - scrollY) - (this.mView.getHeight() - vi.bottom);
                            }
                        }
                        handled = true;
                    }
                }
            }
        }
        if (scrollY != this.mScrollY) {
            if (!immediate && this.mResizeBuffer == null) {
                if (this.mScroller == null) {
                    this.mScroller = new Scroller(this.mView.getContext());
                }
                this.mScroller.startScroll(0, this.mScrollY, 0, scrollY - this.mScrollY);
            } else if (this.mScroller != null) {
                this.mScroller.abortAnimation();
            }
            this.mScrollY = scrollY;
        }
        return handled;
    }

    public View getAccessibilityFocusedHost() {
        return this.mAccessibilityFocusedHost;
    }

    public AccessibilityNodeInfo getAccessibilityFocusedVirtualView() {
        return this.mAccessibilityFocusedVirtualView;
    }

    void setAccessibilityFocus(View view, AccessibilityNodeInfo node) {
        if (this.mAccessibilityFocusedVirtualView != null) {
            AccessibilityNodeInfo focusNode = this.mAccessibilityFocusedVirtualView;
            View focusHost = this.mAccessibilityFocusedHost;
            this.mAccessibilityFocusedHost = null;
            this.mAccessibilityFocusedVirtualView = null;
            focusHost.clearAccessibilityFocusNoCallbacks();
            AccessibilityNodeProvider provider = focusHost.getAccessibilityNodeProvider();
            if (provider != null) {
                focusNode.getBoundsInParent(this.mTempRect);
                focusHost.invalidate(this.mTempRect);
                provider.performAction(AccessibilityNodeInfo.getVirtualDescendantId(focusNode.getSourceNodeId()), AccessibilityNodeInfo.ACTION_CLEAR_ACCESSIBILITY_FOCUS, null);
            }
            focusNode.recycle();
        }
        if (this.mAccessibilityFocusedHost != null) {
            this.mAccessibilityFocusedHost.clearAccessibilityFocusNoCallbacks();
        }
        this.mAccessibilityFocusedHost = view;
        this.mAccessibilityFocusedVirtualView = node;
        if (this.mAttachInfo.mHardwareRenderer != null) {
            this.mAttachInfo.mHardwareRenderer.invalidateRoot();
        }
    }

    public void requestChildFocus(View child, View focused) {
        checkThread();
        scheduleTraversals();
    }

    public void clearChildFocus(View child) {
        checkThread();
        scheduleTraversals();
    }

    public ViewParent getParentForAccessibility() {
        return null;
    }

    public void focusableViewAvailable(View v) {
        checkThread();
        if (this.mView == null) {
            return;
        }
        if (this.mView.hasFocus()) {
            View focused = this.mView.findFocus();
            if ((focused instanceof ViewGroup) && ((ViewGroup) focused).getDescendantFocusability() == AccessibilityNodeInfo.ACTION_EXPAND && isViewDescendantOf(v, focused)) {
                v.requestFocus();
                return;
            }
            return;
        }
        v.requestFocus();
    }

    public void recomputeViewAttributes(View child) {
        checkThread();
        if (this.mView == child) {
            this.mAttachInfo.mRecomputeGlobalAttributes = true;
            if (!this.mWillDrawSoon) {
                scheduleTraversals();
            }
        }
    }

    void dispatchDetachedFromWindow() {
        if (!(this.mView == null || this.mView.mAttachInfo == null)) {
            this.mAttachInfo.mTreeObserver.dispatchOnWindowAttachedChange(LOCAL_LOGV);
            this.mView.dispatchDetachedFromWindow();
        }
        this.mAccessibilityInteractionConnectionManager.ensureNoConnection();
        this.mAccessibilityManager.removeAccessibilityStateChangeListener(this.mAccessibilityInteractionConnectionManager);
        this.mAccessibilityManager.removeHighTextContrastStateChangeListener(this.mHighContrastTextManager);
        removeSendWindowContentChangedCallback();
        destroyHardwareRenderer();
        setAccessibilityFocus(null, null);
        this.mView.assignParent(null);
        this.mView = null;
        this.mAttachInfo.mRootView = null;
        this.mSurface.release();
        if (!(this.mInputQueueCallback == null || this.mInputQueue == null)) {
            this.mInputQueueCallback.onInputQueueDestroyed(this.mInputQueue);
            this.mInputQueue.dispose();
            this.mInputQueueCallback = null;
            this.mInputQueue = null;
        }
        if (this.mInputEventReceiver != null) {
            this.mInputEventReceiver.dispose();
            this.mInputEventReceiver = null;
        }
        try {
            this.mWindowSession.remove(this.mWindow);
        } catch (RemoteException e) {
        }
        if (this.mInputChannel != null) {
            this.mInputChannel.dispose();
            this.mInputChannel = null;
        }
        this.mDisplayManager.unregisterDisplayListener(this.mDisplayListener);
        unscheduleTraversals();
    }

    void updateConfiguration(Configuration config, boolean force) {
        CompatibilityInfo ci = this.mDisplayAdjustments.getCompatibilityInfo();
        if (!ci.equals(CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO)) {
            Configuration config2 = new Configuration(config);
            ci.applyToConfiguration(this.mNoncompatDensity, config2);
            config = config2;
        }
        synchronized (sConfigCallbacks) {
            for (int i = sConfigCallbacks.size() - 1; i >= 0; i--) {
                ((ComponentCallbacks) sConfigCallbacks.get(i)).onConfigurationChanged(config);
            }
        }
        if (this.mView != null) {
            config = this.mView.getResources().getConfiguration();
            if (force || this.mLastConfiguration.diff(config) != 0) {
                int lastLayoutDirection = this.mLastConfiguration.getLayoutDirection();
                int currentLayoutDirection = config.getLayoutDirection();
                this.mLastConfiguration.setTo(config);
                if (lastLayoutDirection != currentLayoutDirection && this.mViewLayoutDirectionInitial == MSG_INVALIDATE_RECT) {
                    this.mView.setLayoutDirection(currentLayoutDirection);
                }
                this.mView.dispatchConfigurationChanged(config);
            }
        }
    }

    public static boolean isViewDescendantOf(View child, View parent) {
        if (child == parent) {
            return true;
        }
        ViewParent theParent = child.getParent();
        if ((theParent instanceof ViewGroup) && isViewDescendantOf((View) theParent, parent)) {
            return true;
        }
        return LOCAL_LOGV;
    }

    private static void forceLayout(View view) {
        view.forceLayout();
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int count = group.getChildCount();
            for (int i = 0; i < count; i += MSG_INVALIDATE) {
                forceLayout(group.getChildAt(i));
            }
        }
    }

    boolean ensureTouchMode(boolean inTouchMode) {
        if (this.mAttachInfo.mInTouchMode == inTouchMode) {
            return LOCAL_LOGV;
        }
        try {
            if (!isInLocalFocusMode()) {
                this.mWindowSession.setInTouchMode(inTouchMode);
            }
            return ensureTouchModeLocally(inTouchMode);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean ensureTouchModeLocally(boolean inTouchMode) {
        if (this.mAttachInfo.mInTouchMode == inTouchMode) {
            return LOCAL_LOGV;
        }
        this.mAttachInfo.mInTouchMode = inTouchMode;
        this.mAttachInfo.mTreeObserver.dispatchOnTouchModeChanged(inTouchMode);
        return inTouchMode ? enterTouchMode() : leaveTouchMode();
    }

    private boolean enterTouchMode() {
        if (this.mView != null && this.mView.hasFocus()) {
            View focused = this.mView.findFocus();
            if (!(focused == null || focused.isFocusableInTouchMode())) {
                ViewGroup ancestorToTakeFocus = findAncestorToTakeFocusInTouchMode(focused);
                if (ancestorToTakeFocus != null) {
                    return ancestorToTakeFocus.requestFocus();
                }
                focused.clearFocusInternal(null, true, LOCAL_LOGV);
                return true;
            }
        }
        return LOCAL_LOGV;
    }

    private static ViewGroup findAncestorToTakeFocusInTouchMode(View focused) {
        ViewParent parent = focused.getParent();
        while (parent instanceof ViewGroup) {
            ViewGroup vgParent = (ViewGroup) parent;
            if (vgParent.getDescendantFocusability() == AccessibilityNodeInfo.ACTION_EXPAND && vgParent.isFocusableInTouchMode()) {
                return vgParent;
            }
            if (vgParent.isRootNamespace()) {
                return null;
            }
            parent = vgParent.getParent();
        }
        return null;
    }

    private boolean leaveTouchMode() {
        if (this.mView == null) {
            return LOCAL_LOGV;
        }
        if (this.mView.hasFocus()) {
            View focusedView = this.mView.findFocus();
            if (!((focusedView instanceof ViewGroup) && ((ViewGroup) focusedView).getDescendantFocusability() == AccessibilityNodeInfo.ACTION_EXPAND)) {
                return LOCAL_LOGV;
            }
        }
        View focused = focusSearch(null, KeyEvent.KEYCODE_MEDIA_RECORD);
        if (focused != null) {
            return focused.requestFocus(KeyEvent.KEYCODE_MEDIA_RECORD);
        }
        return LOCAL_LOGV;
    }

    private static boolean isNavigationKey(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case MSG_PROCESS_INPUT_EVENTS /*19*/:
            case RelativeLayout.ALIGN_PARENT_START /*20*/:
            case MSG_CLEAR_ACCESSIBILITY_FOCUS_HOST /*21*/:
            case MSG_DISPATCH_DONE_ANIMATING /*22*/:
            case MSG_INVALIDATE_WORLD /*23*/:
            case KeyEvent.KEYCODE_TAB /*61*/:
            case KeyEvent.KEYCODE_SPACE /*62*/:
            case KeyEvent.KEYCODE_ENTER /*66*/:
            case KeyEvent.KEYCODE_PAGE_UP /*92*/:
            case KeyEvent.KEYCODE_PAGE_DOWN /*93*/:
            case KeyEvent.KEYCODE_MOVE_HOME /*122*/:
            case KeyEvent.KEYCODE_MOVE_END /*123*/:
                return true;
            default:
                return LOCAL_LOGV;
        }
    }

    private static boolean isTypingKey(KeyEvent keyEvent) {
        return keyEvent.getUnicodeChar() > 0 ? true : LOCAL_LOGV;
    }

    private boolean checkForLeavingTouchModeAndConsume(KeyEvent event) {
        if (!this.mAttachInfo.mInTouchMode) {
            return LOCAL_LOGV;
        }
        int action = event.getAction();
        if ((action != 0 && action != MSG_INVALIDATE_RECT) || (event.getFlags() & MSG_RESIZED) != 0) {
            return LOCAL_LOGV;
        }
        if (isNavigationKey(event)) {
            return ensureTouchMode(LOCAL_LOGV);
        }
        if (!isTypingKey(event)) {
            return LOCAL_LOGV;
        }
        ensureTouchMode(LOCAL_LOGV);
        return LOCAL_LOGV;
    }

    void setLocalDragState(Object obj) {
        this.mLocalDragState = obj;
    }

    private void handleDragEvent(DragEvent event) {
        if (this.mView != null && this.mAdded) {
            int what = event.mAction;
            if (what == MSG_WINDOW_FOCUS_CHANGED) {
                this.mView.dispatchDragEvent(event);
            } else {
                if (what == MSG_INVALIDATE) {
                    this.mCurrentDragView = null;
                    this.mDragDescription = event.mClipDescription;
                } else {
                    event.mClipDescription = this.mDragDescription;
                }
                if (what == MSG_INVALIDATE_RECT || what == MSG_DIE) {
                    this.mDragPoint.set(event.mX, event.mY);
                    if (this.mTranslator != null) {
                        this.mTranslator.translatePointInScreenToAppWindow(this.mDragPoint);
                    }
                    if (this.mCurScrollY != 0) {
                        this.mDragPoint.offset(0.0f, (float) this.mCurScrollY);
                    }
                    event.mX = this.mDragPoint.x;
                    event.mY = this.mDragPoint.y;
                }
                View prevDragView = this.mCurrentDragView;
                boolean result = this.mView.dispatchDragEvent(event);
                if (prevDragView != this.mCurrentDragView) {
                    if (prevDragView != null) {
                        try {
                            this.mWindowSession.dragRecipientExited(this.mWindow);
                        } catch (RemoteException e) {
                            Slog.e(TAG, "Unable to note drag target change");
                        }
                    }
                    if (this.mCurrentDragView != null) {
                        this.mWindowSession.dragRecipientEntered(this.mWindow);
                    }
                }
                if (what == MSG_DIE) {
                    this.mDragDescription = null;
                    try {
                        Log.i(TAG, "Reporting drop result: " + result);
                        this.mWindowSession.reportDropResult(this.mWindow, result);
                    } catch (RemoteException e2) {
                        Log.e(TAG, "Unable to report drop result");
                    }
                }
                if (what == MSG_RESIZED) {
                    setLocalDragState(null);
                }
            }
        }
        event.recycle();
    }

    public void handleDispatchSystemUiVisibilityChanged(SystemUiVisibilityInfo args) {
        if (this.mSeq != args.seq) {
            this.mSeq = args.seq;
            this.mAttachInfo.mForceReportNewAttributes = true;
            scheduleTraversals();
        }
        if (this.mView != null) {
            if (args.localChanges != 0) {
                this.mView.updateLocalSystemUiVisibility(args.localValue, args.localChanges);
            }
            int visibility = args.globalVisibility & MSG_DISPATCH_INPUT_EVENT;
            if (visibility != this.mAttachInfo.mGlobalSystemUiVisibility) {
                this.mAttachInfo.mGlobalSystemUiVisibility = visibility;
                this.mView.dispatchSystemUiVisibilityChanged(visibility);
            }
        }
    }

    public void handleDispatchDoneAnimating() {
        if (this.mWindowsAnimating) {
            this.mWindowsAnimating = LOCAL_LOGV;
            if (!this.mDirty.isEmpty() || this.mIsAnimating || this.mFullRedrawNeeded) {
                scheduleTraversals();
            }
        }
    }

    public void handleDispatchWindowShown() {
        this.mAttachInfo.mTreeObserver.dispatchOnWindowShown();
    }

    public void getLastTouchPoint(Point outLocation) {
        outLocation.x = (int) this.mLastTouchPoint.x;
        outLocation.y = (int) this.mLastTouchPoint.y;
    }

    public void setDragFocus(View newDragTarget) {
        if (this.mCurrentDragView != newDragTarget) {
            this.mCurrentDragView = newDragTarget;
        }
    }

    private AudioManager getAudioManager() {
        if (this.mView == null) {
            throw new IllegalStateException("getAudioManager called when there is no mView");
        }
        if (this.mAudioManager == null) {
            this.mAudioManager = (AudioManager) this.mView.getContext().getSystemService(Context.AUDIO_SERVICE);
        }
        return this.mAudioManager;
    }

    public AccessibilityInteractionController getAccessibilityInteractionController() {
        if (this.mView == null) {
            throw new IllegalStateException("getAccessibilityInteractionController called when there is no mView");
        }
        if (this.mAccessibilityInteractionController == null) {
            this.mAccessibilityInteractionController = new AccessibilityInteractionController(this);
        }
        return this.mAccessibilityInteractionController;
    }

    private int relayoutWindow(LayoutParams params, int viewVisibility, boolean insetsPending) throws RemoteException {
        float appScale = this.mAttachInfo.mApplicationScale;
        boolean restore = LOCAL_LOGV;
        if (!(params == null || this.mTranslator == null)) {
            restore = true;
            params.backup();
            this.mTranslator.translateWindowLayout(params);
        }
        if (params != null) {
            this.mPendingConfiguration.seq = 0;
        } else {
            this.mPendingConfiguration.seq = 0;
        }
        if (!(params == null || this.mOrigWindowType == params.type || this.mTargetSdkVersion >= MSG_CLOSE_SYSTEM_DIALOGS)) {
            Slog.w(TAG, "Window type can not be changed after the window is added; ignoring change of " + this.mView);
            params.type = this.mOrigWindowType;
        }
        int relayoutResult = this.mWindowSession.relayout(this.mWindow, this.mSeq, params, (int) ((((float) this.mView.getMeasuredWidth()) * appScale) + 0.5f), (int) ((((float) this.mView.getMeasuredHeight()) * appScale) + 0.5f), viewVisibility, insetsPending ? MSG_INVALIDATE : 0, this.mWinFrame, this.mPendingOverscanInsets, this.mPendingContentInsets, this.mPendingVisibleInsets, this.mPendingStableInsets, this.mPendingConfiguration, this.mSurface);
        if (restore) {
            params.restore();
        }
        if (this.mTranslator != null) {
            this.mTranslator.translateRectInScreenToAppWinFrame(this.mWinFrame);
            this.mTranslator.translateRectInScreenToAppWindow(this.mPendingOverscanInsets);
            this.mTranslator.translateRectInScreenToAppWindow(this.mPendingContentInsets);
            this.mTranslator.translateRectInScreenToAppWindow(this.mPendingVisibleInsets);
            this.mTranslator.translateRectInScreenToAppWindow(this.mPendingStableInsets);
        }
        return relayoutResult;
    }

    public void playSoundEffect(int effectId) {
        checkThread();
        if (!this.mMediaDisabled) {
            try {
                AudioManager audioManager = getAudioManager();
                switch (effectId) {
                    case Toast.LENGTH_SHORT /*0*/:
                        audioManager.playSoundEffect(0);
                        return;
                    case MSG_INVALIDATE /*1*/:
                        audioManager.playSoundEffect(MSG_DIE);
                        return;
                    case MSG_INVALIDATE_RECT /*2*/:
                        audioManager.playSoundEffect(MSG_INVALIDATE);
                        return;
                    case MSG_DIE /*3*/:
                        audioManager.playSoundEffect(MSG_RESIZED);
                        return;
                    case MSG_RESIZED /*4*/:
                        audioManager.playSoundEffect(MSG_INVALIDATE_RECT);
                        return;
                    default:
                        throw new IllegalArgumentException("unknown effect id " + effectId + " not defined in " + SoundEffectConstants.class.getCanonicalName());
                }
            } catch (IllegalStateException e) {
                Log.e(TAG, "FATAL EXCEPTION when attempting to play sound effect: " + e);
                e.printStackTrace();
            }
            Log.e(TAG, "FATAL EXCEPTION when attempting to play sound effect: " + e);
            e.printStackTrace();
        }
    }

    public boolean performHapticFeedback(int effectId, boolean always) {
        try {
            return this.mWindowSession.performHapticFeedback(this.mWindow, effectId, always);
        } catch (RemoteException e) {
            return LOCAL_LOGV;
        }
    }

    public View focusSearch(View focused, int direction) {
        checkThread();
        if (this.mView instanceof ViewGroup) {
            return FocusFinder.getInstance().findNextFocus((ViewGroup) this.mView, focused, direction);
        }
        return null;
    }

    public void debug() {
        this.mView.debug();
    }

    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        String innerPrefix = prefix + "  ";
        writer.print(prefix);
        writer.println("ViewRoot:");
        writer.print(innerPrefix);
        writer.print("mAdded=");
        writer.print(this.mAdded);
        writer.print(" mRemoved=");
        writer.println(this.mRemoved);
        writer.print(innerPrefix);
        writer.print("mConsumeBatchedInputScheduled=");
        writer.println(this.mConsumeBatchedInputScheduled);
        writer.print(innerPrefix);
        writer.print("mConsumeBatchedInputImmediatelyScheduled=");
        writer.println(this.mConsumeBatchedInputImmediatelyScheduled);
        writer.print(innerPrefix);
        writer.print("mPendingInputEventCount=");
        writer.println(this.mPendingInputEventCount);
        writer.print(innerPrefix);
        writer.print("mProcessInputEventsScheduled=");
        writer.println(this.mProcessInputEventsScheduled);
        writer.print(innerPrefix);
        writer.print("mTraversalScheduled=");
        writer.print(this.mTraversalScheduled);
        if (this.mTraversalScheduled) {
            writer.print(" (barrier=");
            writer.print(this.mTraversalBarrier);
            writer.println(")");
        } else {
            writer.println();
        }
        this.mFirstInputStage.dump(innerPrefix, writer);
        this.mChoreographer.dump(prefix, writer);
        writer.print(prefix);
        writer.println("View Hierarchy:");
        dumpViewHierarchy(innerPrefix, writer, this.mView);
    }

    private void dumpViewHierarchy(String prefix, PrintWriter writer, View view) {
        writer.print(prefix);
        if (view == null) {
            writer.println("null");
            return;
        }
        writer.println(view.toString());
        if (view instanceof ViewGroup) {
            ViewGroup grp = (ViewGroup) view;
            int N = grp.getChildCount();
            if (N > 0) {
                prefix = prefix + "  ";
                for (int i = 0; i < N; i += MSG_INVALIDATE) {
                    dumpViewHierarchy(prefix, writer, grp.getChildAt(i));
                }
            }
        }
    }

    public void dumpGfxInfo(int[] info) {
        info[MSG_INVALIDATE] = 0;
        info[0] = 0;
        if (this.mView != null) {
            getGfxInfo(this.mView, info);
        }
    }

    private static void getGfxInfo(View view, int[] info) {
        RenderNode renderNode = view.mRenderNode;
        info[0] = info[0] + MSG_INVALIDATE;
        if (renderNode != null) {
            info[MSG_INVALIDATE] = info[MSG_INVALIDATE] + renderNode.getDebugSize();
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int count = group.getChildCount();
            for (int i = 0; i < count; i += MSG_INVALIDATE) {
                getGfxInfo(group.getChildAt(i), info);
            }
        }
    }

    boolean die(boolean immediate) {
        if (!immediate || this.mIsInTraversal) {
            if (this.mIsDrawing) {
                Log.e(TAG, "Attempting to destroy the window while drawing!\n  window=" + this + ", title=" + this.mWindowAttributes.getTitle());
            } else {
                destroyHardwareRenderer();
            }
            this.mHandler.sendEmptyMessage(MSG_DIE);
            return true;
        }
        doDie();
        return LOCAL_LOGV;
    }

    void doDie() {
        boolean viewVisibilityChanged = true;
        checkThread();
        synchronized (this) {
            if (this.mRemoved) {
                return;
            }
            this.mRemoved = true;
            if (this.mAdded) {
                dispatchDetachedFromWindow();
            }
            if (this.mAdded && !this.mFirst) {
                destroyHardwareRenderer();
                if (this.mView != null) {
                    int viewVisibility = this.mView.getVisibility();
                    if (this.mViewVisibility == viewVisibility) {
                        viewVisibilityChanged = LOCAL_LOGV;
                    }
                    if (this.mWindowAttributesChanged || viewVisibilityChanged) {
                        try {
                            if ((relayoutWindow(this.mWindowAttributes, viewVisibility, LOCAL_LOGV) & MSG_INVALIDATE_RECT) != 0) {
                                this.mWindowSession.finishDrawing(this.mWindow);
                            }
                        } catch (RemoteException e) {
                        }
                    }
                    this.mSurface.release();
                }
            }
            this.mAdded = LOCAL_LOGV;
            WindowManagerGlobal.getInstance().doRemoveView(this);
        }
    }

    public void requestUpdateConfiguration(Configuration config) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(MSG_UPDATE_CONFIGURATION, config));
    }

    public void loadSystemProperties() {
        this.mHandler.post(new AnonymousClass4(this));
    }

    private void destroyHardwareRenderer() {
        HardwareRenderer hardwareRenderer = this.mAttachInfo.mHardwareRenderer;
        if (hardwareRenderer != null) {
            if (this.mView != null) {
                hardwareRenderer.destroyHardwareResources(this.mView);
            }
            hardwareRenderer.destroy();
            hardwareRenderer.setRequested(LOCAL_LOGV);
            this.mAttachInfo.mHardwareRenderer = null;
            this.mAttachInfo.mHardwareAccelerated = LOCAL_LOGV;
        }
    }

    public void dispatchFinishInputConnection(InputConnection connection) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(MSG_FINISH_INPUT_CONNECTION, connection));
    }

    public void dispatchResized(Rect frame, Rect overscanInsets, Rect contentInsets, Rect visibleInsets, Rect stableInsets, boolean reportDraw, Configuration newConfig) {
        Message msg = this.mHandler.obtainMessage(reportDraw ? MSG_RESIZED_REPORT : MSG_RESIZED);
        if (this.mTranslator != null) {
            this.mTranslator.translateRectInScreenToAppWindow(frame);
            this.mTranslator.translateRectInScreenToAppWindow(overscanInsets);
            this.mTranslator.translateRectInScreenToAppWindow(contentInsets);
            this.mTranslator.translateRectInScreenToAppWindow(visibleInsets);
        }
        SomeArgs args = SomeArgs.obtain();
        boolean sameProcessCall = Binder.getCallingPid() == Process.myPid() ? true : LOCAL_LOGV;
        if (sameProcessCall) {
            frame = new Rect(frame);
        }
        args.arg1 = frame;
        if (sameProcessCall) {
            contentInsets = new Rect(contentInsets);
        }
        args.arg2 = contentInsets;
        if (sameProcessCall) {
            visibleInsets = new Rect(visibleInsets);
        }
        args.arg3 = visibleInsets;
        if (sameProcessCall && newConfig != null) {
            newConfig = new Configuration(newConfig);
        }
        args.arg4 = newConfig;
        if (sameProcessCall) {
            overscanInsets = new Rect(overscanInsets);
        }
        args.arg5 = overscanInsets;
        if (sameProcessCall) {
            stableInsets = new Rect(stableInsets);
        }
        args.arg6 = stableInsets;
        msg.obj = args;
        this.mHandler.sendMessage(msg);
    }

    public void dispatchMoved(int newX, int newY) {
        if (this.mTranslator != null) {
            PointF point = new PointF((float) newX, (float) newY);
            this.mTranslator.translatePointInScreenToAppWindow(point);
            newX = (int) (((double) point.x) + 0.5d);
            newY = (int) (((double) point.y) + 0.5d);
        }
        this.mHandler.sendMessage(this.mHandler.obtainMessage(MSG_WINDOW_MOVED, newX, newY));
    }

    private QueuedInputEvent obtainQueuedInputEvent(InputEvent event, InputEventReceiver receiver, int flags) {
        QueuedInputEvent q = this.mQueuedInputEventPool;
        if (q != null) {
            this.mQueuedInputEventPoolSize--;
            this.mQueuedInputEventPool = q.mNext;
            q.mNext = null;
        } else {
            q = new QueuedInputEvent();
        }
        q.mEvent = event;
        q.mReceiver = receiver;
        q.mFlags = flags;
        return q;
    }

    private void recycleQueuedInputEvent(QueuedInputEvent q) {
        q.mEvent = null;
        q.mReceiver = null;
        if (this.mQueuedInputEventPoolSize < MAX_QUEUED_INPUT_EVENT_POOL_SIZE) {
            this.mQueuedInputEventPoolSize += MSG_INVALIDATE;
            q.mNext = this.mQueuedInputEventPool;
            this.mQueuedInputEventPool = q;
        }
    }

    void enqueueInputEvent(InputEvent event) {
        enqueueInputEvent(event, null, 0, LOCAL_LOGV);
    }

    void enqueueInputEvent(InputEvent event, InputEventReceiver receiver, int flags, boolean processImmediately) {
        QueuedInputEvent q = obtainQueuedInputEvent(event, receiver, flags);
        QueuedInputEvent last = this.mPendingInputEventTail;
        if (last == null) {
            this.mPendingInputEventHead = q;
            this.mPendingInputEventTail = q;
        } else {
            last.mNext = q;
            this.mPendingInputEventTail = q;
        }
        this.mPendingInputEventCount += MSG_INVALIDATE;
        Trace.traceCounter(4, this.mPendingInputEventQueueLengthCounterName, this.mPendingInputEventCount);
        if (processImmediately) {
            doProcessInputEvents();
        } else {
            scheduleProcessInputEvents();
        }
    }

    private void scheduleProcessInputEvents() {
        if (!this.mProcessInputEventsScheduled) {
            this.mProcessInputEventsScheduled = true;
            Message msg = this.mHandler.obtainMessage(MSG_PROCESS_INPUT_EVENTS);
            msg.setAsynchronous(true);
            this.mHandler.sendMessage(msg);
        }
    }

    void doProcessInputEvents() {
        while (this.mPendingInputEventHead != null) {
            QueuedInputEvent q = this.mPendingInputEventHead;
            this.mPendingInputEventHead = q.mNext;
            if (this.mPendingInputEventHead == null) {
                this.mPendingInputEventTail = null;
            }
            q.mNext = null;
            this.mPendingInputEventCount--;
            Trace.traceCounter(4, this.mPendingInputEventQueueLengthCounterName, this.mPendingInputEventCount);
            deliverInputEvent(q);
        }
        if (this.mProcessInputEventsScheduled) {
            this.mProcessInputEventsScheduled = LOCAL_LOGV;
            this.mHandler.removeMessages(MSG_PROCESS_INPUT_EVENTS);
        }
    }

    private void deliverInputEvent(QueuedInputEvent q) {
        InputStage stage;
        Trace.asyncTraceBegin(8, "deliverInputEvent", q.mEvent.getSequenceNumber());
        if (this.mInputEventConsistencyVerifier != null) {
            this.mInputEventConsistencyVerifier.onInputEvent(q.mEvent, 0);
        }
        if (q.shouldSendToSynthesizer()) {
            stage = this.mSyntheticInputStage;
        } else {
            stage = q.shouldSkipIme() ? this.mFirstPostImeInputStage : this.mFirstInputStage;
        }
        if (stage != null) {
            stage.deliver(q);
        } else {
            finishInputEvent(q);
        }
    }

    private void finishInputEvent(QueuedInputEvent q) {
        Trace.asyncTraceEnd(8, "deliverInputEvent", q.mEvent.getSequenceNumber());
        if (q.mReceiver != null) {
            q.mReceiver.finishInputEvent(q.mEvent, (q.mFlags & MSG_DISPATCH_APP_VISIBILITY) != 0 ? true : LOCAL_LOGV);
        } else {
            q.mEvent.recycleIfNeededAfterDispatch();
        }
        recycleQueuedInputEvent(q);
    }

    static boolean isTerminalInputEvent(InputEvent event) {
        boolean z = LOCAL_LOGV;
        if (!(event instanceof KeyEvent)) {
            int action = ((MotionEvent) event).getAction();
            if (action == MSG_INVALIDATE || action == MSG_DIE || action == MAX_QUEUED_INPUT_EVENT_POOL_SIZE) {
                z = true;
            }
            return z;
        } else if (((KeyEvent) event).getAction() == MSG_INVALIDATE) {
            return true;
        } else {
            return LOCAL_LOGV;
        }
    }

    void scheduleConsumeBatchedInput() {
        if (!this.mConsumeBatchedInputScheduled) {
            this.mConsumeBatchedInputScheduled = true;
            this.mChoreographer.postCallback(0, this.mConsumedBatchedInputRunnable, null);
        }
    }

    void unscheduleConsumeBatchedInput() {
        if (this.mConsumeBatchedInputScheduled) {
            this.mConsumeBatchedInputScheduled = LOCAL_LOGV;
            this.mChoreographer.removeCallbacks(0, this.mConsumedBatchedInputRunnable, null);
        }
    }

    void scheduleConsumeBatchedInputImmediately() {
        if (!this.mConsumeBatchedInputImmediatelyScheduled) {
            unscheduleConsumeBatchedInput();
            this.mConsumeBatchedInputImmediatelyScheduled = true;
            this.mHandler.post(this.mConsumeBatchedInputImmediatelyRunnable);
        }
    }

    void doConsumeBatchedInput(long frameTimeNanos) {
        if (this.mConsumeBatchedInputScheduled) {
            this.mConsumeBatchedInputScheduled = LOCAL_LOGV;
            if (!(this.mInputEventReceiver == null || !this.mInputEventReceiver.consumeBatchedInputEvents(frameTimeNanos) || frameTimeNanos == -1)) {
                scheduleConsumeBatchedInput();
            }
            doProcessInputEvents();
        }
    }

    public void dispatchInvalidateDelayed(View view, long delayMilliseconds) {
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(MSG_INVALIDATE, view), delayMilliseconds);
    }

    public void dispatchInvalidateRectDelayed(InvalidateInfo info, long delayMilliseconds) {
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(MSG_INVALIDATE_RECT, info), delayMilliseconds);
    }

    public void dispatchInvalidateOnAnimation(View view) {
        this.mInvalidateOnAnimationRunnable.addView(view);
    }

    public void dispatchInvalidateRectOnAnimation(InvalidateInfo info) {
        this.mInvalidateOnAnimationRunnable.addViewRect(info);
    }

    public void cancelInvalidate(View view) {
        this.mHandler.removeMessages(MSG_INVALIDATE, view);
        this.mHandler.removeMessages(MSG_INVALIDATE_RECT, view);
        this.mInvalidateOnAnimationRunnable.removeView(view);
    }

    public void dispatchInputEvent(InputEvent event) {
        dispatchInputEvent(event, null);
    }

    public void dispatchInputEvent(InputEvent event, InputEventReceiver receiver) {
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = event;
        args.arg2 = receiver;
        Message msg = this.mHandler.obtainMessage(MSG_DISPATCH_INPUT_EVENT, args);
        msg.setAsynchronous(true);
        this.mHandler.sendMessage(msg);
    }

    public void synthesizeInputEvent(InputEvent event) {
        Message msg = this.mHandler.obtainMessage(MSG_SYNTHESIZE_INPUT_EVENT, event);
        msg.setAsynchronous(true);
        this.mHandler.sendMessage(msg);
    }

    public void dispatchKeyFromIme(KeyEvent event) {
        Message msg = this.mHandler.obtainMessage(MSG_DISPATCH_KEY_FROM_IME, event);
        msg.setAsynchronous(true);
        this.mHandler.sendMessage(msg);
    }

    public void dispatchUnhandledInputEvent(InputEvent event) {
        if (event instanceof MotionEvent) {
            event = MotionEvent.obtain((MotionEvent) event);
        }
        synthesizeInputEvent(event);
    }

    public void dispatchAppVisibility(boolean visible) {
        Message msg = this.mHandler.obtainMessage(MSG_DISPATCH_APP_VISIBILITY);
        msg.arg1 = visible ? MSG_INVALIDATE : 0;
        this.mHandler.sendMessage(msg);
    }

    public void dispatchGetNewSurface() {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(MSG_DISPATCH_GET_NEW_SURFACE));
    }

    public void windowFocusChanged(boolean hasFocus, boolean inTouchMode) {
        int i;
        int i2 = MSG_INVALIDATE;
        Message msg = Message.obtain();
        msg.what = MSG_WINDOW_FOCUS_CHANGED;
        if (hasFocus) {
            i = MSG_INVALIDATE;
        } else {
            i = 0;
        }
        msg.arg1 = i;
        if (!inTouchMode) {
            i2 = 0;
        }
        msg.arg2 = i2;
        this.mHandler.sendMessage(msg);
    }

    public void dispatchWindowShown() {
        this.mHandler.sendEmptyMessage(MSG_DISPATCH_WINDOW_SHOWN);
    }

    public void dispatchCloseSystemDialogs(String reason) {
        Message msg = Message.obtain();
        msg.what = MSG_CLOSE_SYSTEM_DIALOGS;
        msg.obj = reason;
        this.mHandler.sendMessage(msg);
    }

    public void dispatchDragEvent(DragEvent event) {
        int what;
        if (event.getAction() == MSG_INVALIDATE_RECT) {
            what = MSG_DISPATCH_DRAG_LOCATION_EVENT;
            this.mHandler.removeMessages(MSG_DISPATCH_DRAG_LOCATION_EVENT);
        } else {
            what = MSG_DISPATCH_DRAG_EVENT;
        }
        this.mHandler.sendMessage(this.mHandler.obtainMessage(what, event));
    }

    public void dispatchSystemUiVisibilityChanged(int seq, int globalVisibility, int localValue, int localChanges) {
        SystemUiVisibilityInfo args = new SystemUiVisibilityInfo();
        args.seq = seq;
        args.globalVisibility = globalVisibility;
        args.localValue = localValue;
        args.localChanges = localChanges;
        this.mHandler.sendMessage(this.mHandler.obtainMessage(MSG_DISPATCH_SYSTEM_UI_VISIBILITY, args));
    }

    public void dispatchDoneAnimating() {
        this.mHandler.sendEmptyMessage(MSG_DISPATCH_DONE_ANIMATING);
    }

    public void dispatchCheckFocus() {
        if (!this.mHandler.hasMessages(MSG_CHECK_FOCUS)) {
            this.mHandler.sendEmptyMessage(MSG_CHECK_FOCUS);
        }
    }

    private void postSendWindowContentChangedCallback(View source, int changeType) {
        if (this.mSendWindowContentChangedAccessibilityEvent == null) {
            this.mSendWindowContentChangedAccessibilityEvent = new SendWindowContentChangedAccessibilityEvent();
        }
        this.mSendWindowContentChangedAccessibilityEvent.runOrPost(source, changeType);
    }

    private void removeSendWindowContentChangedCallback() {
        if (this.mSendWindowContentChangedAccessibilityEvent != null) {
            this.mHandler.removeCallbacks(this.mSendWindowContentChangedAccessibilityEvent);
        }
    }

    public boolean showContextMenuForChild(View originalView) {
        return LOCAL_LOGV;
    }

    public ActionMode startActionModeForChild(View originalView, ActionMode.Callback callback) {
        return null;
    }

    public void createContextMenu(ContextMenu menu) {
    }

    public void childDrawableStateChanged(View child) {
    }

    public boolean requestSendAccessibilityEvent(View child, AccessibilityEvent event) {
        if (this.mView == null) {
            return LOCAL_LOGV;
        }
        AccessibilityNodeProvider provider;
        View source;
        switch (event.getEventType()) {
            case AccessibilityNodeInfo.ACTION_PREVIOUS_HTML_ELEMENT /*2048*/:
                if (!(this.mAccessibilityFocusedHost == null || this.mAccessibilityFocusedVirtualView == null || AccessibilityNodeInfo.getAccessibilityViewId(event.getSourceNodeId()) != this.mAccessibilityFocusedHost.getAccessibilityViewId())) {
                    int changes = event.getContentChangeTypes();
                    if ((changes & MSG_INVALIDATE) != 0 || changes == 0) {
                        provider = this.mAccessibilityFocusedHost.getAccessibilityNodeProvider();
                        if (provider != null) {
                            int virtualChildId = AccessibilityNodeInfo.getVirtualDescendantId(this.mAccessibilityFocusedVirtualView.getSourceNodeId());
                            if (virtualChildId != ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED) {
                                this.mAccessibilityFocusedVirtualView = provider.createAccessibilityNodeInfo(virtualChildId);
                                break;
                            }
                            this.mAccessibilityFocusedVirtualView = provider.createAccessibilityNodeInfo(-1);
                            break;
                        }
                    }
                }
                break;
            case AccessibilityNodeInfo.ACTION_PASTE /*32768*/:
                long sourceNodeId = event.getSourceNodeId();
                source = this.mView.findViewByAccessibilityId(AccessibilityNodeInfo.getAccessibilityViewId(sourceNodeId));
                if (source != null) {
                    provider = source.getAccessibilityNodeProvider();
                    if (provider != null) {
                        AccessibilityNodeInfo node;
                        int virtualNodeId = AccessibilityNodeInfo.getVirtualDescendantId(sourceNodeId);
                        if (virtualNodeId == ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED) {
                            node = provider.createAccessibilityNodeInfo(-1);
                        } else {
                            node = provider.createAccessibilityNodeInfo(virtualNodeId);
                        }
                        setAccessibilityFocus(source, node);
                        break;
                    }
                }
                break;
            case AccessibilityNodeInfo.ACTION_CUT /*65536*/:
                source = this.mView.findViewByAccessibilityId(AccessibilityNodeInfo.getAccessibilityViewId(event.getSourceNodeId()));
                if (!(source == null || source.getAccessibilityNodeProvider() == null)) {
                    setAccessibilityFocus(null, null);
                    break;
                }
        }
        this.mAccessibilityManager.sendAccessibilityEvent(event);
        return true;
    }

    public void notifySubtreeAccessibilityStateChanged(View child, View source, int changeType) {
        postSendWindowContentChangedCallback(source, changeType);
    }

    public boolean canResolveLayoutDirection() {
        return true;
    }

    public boolean isLayoutDirectionResolved() {
        return true;
    }

    public int getLayoutDirection() {
        return 0;
    }

    public boolean canResolveTextDirection() {
        return true;
    }

    public boolean isTextDirectionResolved() {
        return true;
    }

    public int getTextDirection() {
        return MSG_INVALIDATE;
    }

    public boolean canResolveTextAlignment() {
        return true;
    }

    public boolean isTextAlignmentResolved() {
        return true;
    }

    public int getTextAlignment() {
        return MSG_INVALIDATE;
    }

    private View getCommonPredecessor(View first, View second) {
        if (this.mTempHashSet == null) {
            this.mTempHashSet = new HashSet();
        }
        HashSet<View> seen = this.mTempHashSet;
        seen.clear();
        View firstCurrent = first;
        while (firstCurrent != null) {
            seen.add(firstCurrent);
            ViewParent firstCurrentParent = firstCurrent.mParent;
            if (firstCurrentParent instanceof View) {
                firstCurrent = (View) firstCurrentParent;
            } else {
                firstCurrent = null;
            }
        }
        View secondCurrent = second;
        while (secondCurrent != null) {
            if (seen.contains(secondCurrent)) {
                seen.clear();
                return secondCurrent;
            }
            ViewParent secondCurrentParent = secondCurrent.mParent;
            if (secondCurrentParent instanceof View) {
                secondCurrent = (View) secondCurrentParent;
            } else {
                secondCurrent = null;
            }
        }
        seen.clear();
        return null;
    }

    void checkThread() {
        if (this.mThread != Thread.currentThread()) {
            throw new CalledFromWrongThreadException("Only the original thread that created a view hierarchy can touch its views.");
        }
    }

    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
        boolean scrolled = scrollToRectOrFocus(rectangle, immediate);
        if (rectangle != null) {
            this.mTempRect.set(rectangle);
            this.mTempRect.offset(0, -this.mCurScrollY);
            this.mTempRect.offset(this.mAttachInfo.mWindowLeft, this.mAttachInfo.mWindowTop);
            try {
                this.mWindowSession.onRectangleOnScreenRequested(this.mWindow, this.mTempRect);
            } catch (RemoteException e) {
            }
        }
        return scrolled;
    }

    public void childHasTransientStateChanged(View child, boolean hasTransientState) {
    }

    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return LOCAL_LOGV;
    }

    public void onStopNestedScroll(View target) {
    }

    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
    }

    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
    }

    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
    }

    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return LOCAL_LOGV;
    }

    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return LOCAL_LOGV;
    }

    public boolean onNestedPrePerformAccessibilityAction(View target, int action, Bundle args) {
        return LOCAL_LOGV;
    }

    void changeCanvasOpacity(boolean opaque) {
        Log.d(TAG, "changeCanvasOpacity: opaque=" + opaque);
        if (this.mAttachInfo.mHardwareRenderer != null) {
            this.mAttachInfo.mHardwareRenderer.setOpaque(opaque);
        }
    }

    static RunQueue getRunQueue() {
        RunQueue rq = (RunQueue) sRunQueues.get();
        if (rq != null) {
            return rq;
        }
        rq = new RunQueue();
        sRunQueues.set(rq);
        return rq;
    }
}
