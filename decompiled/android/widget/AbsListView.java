package android.widget;

import android.content.Context;
import android.content.Intent;
import android.content.Intent.FilterComparison;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.hardware.SensorManager;
import android.net.ProxyInfo;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.StrictMode;
import android.os.StrictMode.Span;
import android.os.Trace;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.StateSet;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.KeyEvent.DispatcherState;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.View.BaseSavedState;
import android.view.ViewConfiguration;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnTouchModeChangeListener;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Filter.FilterListener;
import android.widget.RemoteViews.OnClickHandler;
import android.widget.RemoteViewsAdapter.RemoteAdapterConnectionCallback;
import com.android.internal.R;
import java.util.ArrayList;
import java.util.List;

public abstract class AbsListView extends AdapterView<ListAdapter> implements TextWatcher, OnGlobalLayoutListener, FilterListener, OnTouchModeChangeListener, RemoteAdapterConnectionCallback {
    private static final int CHECK_POSITION_SEARCH_DISTANCE = 20;
    public static final int CHOICE_MODE_MULTIPLE = 2;
    public static final int CHOICE_MODE_MULTIPLE_MODAL = 3;
    public static final int CHOICE_MODE_NONE = 0;
    public static final int CHOICE_MODE_SINGLE = 1;
    private static final int INVALID_POINTER = -1;
    static final int LAYOUT_FORCE_BOTTOM = 3;
    static final int LAYOUT_FORCE_TOP = 1;
    static final int LAYOUT_MOVE_SELECTION = 6;
    static final int LAYOUT_NORMAL = 0;
    static final int LAYOUT_SET_SELECTION = 2;
    static final int LAYOUT_SPECIFIC = 4;
    static final int LAYOUT_SYNC = 5;
    static final int OVERSCROLL_LIMIT_DIVISOR = 3;
    private static final boolean PROFILE_FLINGING = false;
    private static final boolean PROFILE_SCROLLING = false;
    private static final String TAG = "AbsListView";
    static final int TOUCH_MODE_DONE_WAITING = 2;
    static final int TOUCH_MODE_DOWN = 0;
    static final int TOUCH_MODE_FLING = 4;
    private static final int TOUCH_MODE_OFF = 1;
    private static final int TOUCH_MODE_ON = 0;
    static final int TOUCH_MODE_OVERFLING = 6;
    static final int TOUCH_MODE_OVERSCROLL = 5;
    static final int TOUCH_MODE_REST = -1;
    static final int TOUCH_MODE_SCROLL = 3;
    static final int TOUCH_MODE_TAP = 1;
    private static final int TOUCH_MODE_UNKNOWN = -1;
    public static final int TRANSCRIPT_MODE_ALWAYS_SCROLL = 2;
    public static final int TRANSCRIPT_MODE_DISABLED = 0;
    public static final int TRANSCRIPT_MODE_NORMAL = 1;
    static final Interpolator sLinearInterpolator = null;
    private ListItemAccessibilityDelegate mAccessibilityDelegate;
    private int mActivePointerId;
    ListAdapter mAdapter;
    boolean mAdapterHasStableIds;
    private int mCacheColorHint;
    boolean mCachingActive;
    boolean mCachingStarted;
    SparseBooleanArray mCheckStates;
    LongSparseArray<Integer> mCheckedIdStates;
    int mCheckedItemCount;
    ActionMode mChoiceActionMode;
    int mChoiceMode;
    private Runnable mClearScrollingCache;
    private ContextMenuInfo mContextMenuInfo;
    AdapterDataSetObserver mDataSetObserver;
    private InputConnection mDefInputConnection;
    private boolean mDeferNotifyDataSetChanged;
    private float mDensityScale;
    private int mDirection;
    boolean mDrawSelectorOnTop;
    private EdgeEffect mEdgeGlowBottom;
    private EdgeEffect mEdgeGlowTop;
    private FastScroller mFastScroll;
    boolean mFastScrollAlwaysVisible;
    boolean mFastScrollEnabled;
    private int mFastScrollStyle;
    private boolean mFiltered;
    private int mFirstPositionDistanceGuess;
    private boolean mFlingProfilingStarted;
    private FlingRunnable mFlingRunnable;
    private Span mFlingStrictSpan;
    private boolean mForceTranscriptScroll;
    private boolean mGlobalLayoutListenerAddedFilter;
    private int mGlowPaddingLeft;
    private int mGlowPaddingRight;
    private boolean mIsChildViewEnabled;
    private boolean mIsDetaching;
    final boolean[] mIsScrap;
    private int mLastAccessibilityScrollEventFromIndex;
    private int mLastAccessibilityScrollEventToIndex;
    private int mLastHandledItemCount;
    private int mLastPositionDistanceGuess;
    private int mLastScrollState;
    private int mLastTouchMode;
    int mLastY;
    int mLayoutMode;
    Rect mListPadding;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    int mMotionCorrection;
    int mMotionPosition;
    int mMotionViewNewTop;
    int mMotionViewOriginalTop;
    int mMotionX;
    int mMotionY;
    MultiChoiceModeWrapper mMultiChoiceModeCallback;
    private int mNestedYOffset;
    private OnScrollListener mOnScrollListener;
    int mOverflingDistance;
    int mOverscrollDistance;
    int mOverscrollMax;
    private final Thread mOwnerThread;
    private CheckForKeyLongPress mPendingCheckForKeyLongPress;
    private CheckForLongPress mPendingCheckForLongPress;
    private CheckForTap mPendingCheckForTap;
    private SavedState mPendingSync;
    private PerformClick mPerformClick;
    PopupWindow mPopup;
    private boolean mPopupHidden;
    Runnable mPositionScrollAfterLayout;
    AbsPositionScroller mPositionScroller;
    private InputConnectionWrapper mPublicInputConnection;
    final RecycleBin mRecycler;
    private RemoteViewsAdapter mRemoteAdapter;
    int mResurrectToPosition;
    private final int[] mScrollConsumed;
    View mScrollDown;
    private final int[] mScrollOffset;
    private boolean mScrollProfilingStarted;
    private Span mScrollStrictSpan;
    View mScrollUp;
    boolean mScrollingCacheEnabled;
    int mSelectedTop;
    int mSelectionBottomPadding;
    int mSelectionLeftPadding;
    int mSelectionRightPadding;
    int mSelectionTopPadding;
    Drawable mSelector;
    int mSelectorPosition;
    Rect mSelectorRect;
    private boolean mSmoothScrollbarEnabled;
    boolean mStackFromBottom;
    EditText mTextFilter;
    private boolean mTextFilterEnabled;
    private final float[] mTmpPoint;
    private Rect mTouchFrame;
    int mTouchMode;
    private Runnable mTouchModeReset;
    private int mTouchSlop;
    private int mTranscriptMode;
    private float mVelocityScale;
    private VelocityTracker mVelocityTracker;
    int mWidthMeasureSpec;

    /* renamed from: android.widget.AbsListView.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ AbsListView this$0;
        final /* synthetic */ boolean val$enabled;

        AnonymousClass1(android.widget.AbsListView r1, boolean r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.1.<init>(android.widget.AbsListView, boolean):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.1.<init>(android.widget.AbsListView, boolean):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.1.<init>(android.widget.AbsListView, boolean):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.1.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.1.run():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.1.run():void");
        }
    }

    /* renamed from: android.widget.AbsListView.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ AbsListView this$0;
        final /* synthetic */ boolean val$alwaysShow;

        AnonymousClass2(android.widget.AbsListView r1, boolean r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.2.<init>(android.widget.AbsListView, boolean):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.2.<init>(android.widget.AbsListView, boolean):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.2.<init>(android.widget.AbsListView, boolean):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.2.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.2.run():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.2.run():void");
        }
    }

    /* renamed from: android.widget.AbsListView.3 */
    class AnonymousClass3 implements Runnable {
        final /* synthetic */ AbsListView this$0;
        final /* synthetic */ View val$child;
        final /* synthetic */ PerformClick val$performClick;

        AnonymousClass3(android.widget.AbsListView r1, android.view.View r2, android.widget.AbsListView.PerformClick r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.3.<init>(android.widget.AbsListView, android.view.View, android.widget.AbsListView$PerformClick):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.3.<init>(android.widget.AbsListView, android.view.View, android.widget.AbsListView$PerformClick):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.3.<init>(android.widget.AbsListView, android.view.View, android.widget.AbsListView$PerformClick):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.3.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.3.run():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.3.run():void");
        }
    }

    /* renamed from: android.widget.AbsListView.4 */
    class AnonymousClass4 implements Runnable {
        final /* synthetic */ AbsListView this$0;

        AnonymousClass4(android.widget.AbsListView r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.4.<init>(android.widget.AbsListView):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.4.<init>(android.widget.AbsListView):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.4.<init>(android.widget.AbsListView):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.4.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.4.run():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.4.run():void");
        }
    }

    static abstract class AbsPositionScroller {
        public abstract void start(int i);

        public abstract void start(int i, int i2);

        public abstract void startWithOffset(int i, int i2);

        public abstract void startWithOffset(int i, int i2, int i3);

        public abstract void stop();

        AbsPositionScroller() {
        }
    }

    class AdapterDataSetObserver extends AdapterDataSetObserver {
        final /* synthetic */ AbsListView this$0;

        AdapterDataSetObserver(AbsListView absListView) {
            this.this$0 = absListView;
            super();
        }

        public void onChanged() {
            super.onChanged();
            if (this.this$0.mFastScroll != null) {
                this.this$0.mFastScroll.onSectionsChanged();
            }
        }

        public void onInvalidated() {
            super.onInvalidated();
            if (this.this$0.mFastScroll != null) {
                this.this$0.mFastScroll.onSectionsChanged();
            }
        }
    }

    private class WindowRunnnable {
        private int mOriginalAttachCount;
        final /* synthetic */ AbsListView this$0;

        private WindowRunnnable(android.widget.AbsListView r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.WindowRunnnable.<init>(android.widget.AbsListView):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.WindowRunnnable.<init>(android.widget.AbsListView):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.WindowRunnnable.<init>(android.widget.AbsListView):void");
        }

        /* synthetic */ WindowRunnnable(android.widget.AbsListView r1, android.widget.AbsListView.AnonymousClass1 r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.WindowRunnnable.<init>(android.widget.AbsListView, android.widget.AbsListView$1):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.WindowRunnnable.<init>(android.widget.AbsListView, android.widget.AbsListView$1):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 0073
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.WindowRunnnable.<init>(android.widget.AbsListView, android.widget.AbsListView$1):void");
        }

        public void rememberWindowAttachCount() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.WindowRunnnable.rememberWindowAttachCount():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.WindowRunnnable.rememberWindowAttachCount():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.WindowRunnnable.rememberWindowAttachCount():void");
        }

        public boolean sameWindow() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.WindowRunnnable.sameWindow():boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.WindowRunnnable.sameWindow():boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.WindowRunnnable.sameWindow():boolean");
        }
    }

    private class CheckForKeyLongPress extends WindowRunnnable implements Runnable {
        final /* synthetic */ AbsListView this$0;

        private CheckForKeyLongPress(android.widget.AbsListView r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.CheckForKeyLongPress.<init>(android.widget.AbsListView):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.CheckForKeyLongPress.<init>(android.widget.AbsListView):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.CheckForKeyLongPress.<init>(android.widget.AbsListView):void");
        }

        /* synthetic */ CheckForKeyLongPress(android.widget.AbsListView r1, android.widget.AbsListView.AnonymousClass1 r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.CheckForKeyLongPress.<init>(android.widget.AbsListView, android.widget.AbsListView$1):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.CheckForKeyLongPress.<init>(android.widget.AbsListView, android.widget.AbsListView$1):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 0073
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.CheckForKeyLongPress.<init>(android.widget.AbsListView, android.widget.AbsListView$1):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.CheckForKeyLongPress.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.CheckForKeyLongPress.run():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.CheckForKeyLongPress.run():void");
        }
    }

    private class CheckForLongPress extends WindowRunnnable implements Runnable {
        final /* synthetic */ AbsListView this$0;

        private CheckForLongPress(android.widget.AbsListView r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.CheckForLongPress.<init>(android.widget.AbsListView):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.CheckForLongPress.<init>(android.widget.AbsListView):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.CheckForLongPress.<init>(android.widget.AbsListView):void");
        }

        /* synthetic */ CheckForLongPress(android.widget.AbsListView r1, android.widget.AbsListView.AnonymousClass1 r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.CheckForLongPress.<init>(android.widget.AbsListView, android.widget.AbsListView$1):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.CheckForLongPress.<init>(android.widget.AbsListView, android.widget.AbsListView$1):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 0073
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.CheckForLongPress.<init>(android.widget.AbsListView, android.widget.AbsListView$1):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.CheckForLongPress.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.CheckForLongPress.run():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.CheckForLongPress.run():void");
        }
    }

    private final class CheckForTap implements Runnable {
        final /* synthetic */ AbsListView this$0;
        float x;
        float y;

        private CheckForTap(android.widget.AbsListView r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.CheckForTap.<init>(android.widget.AbsListView):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.CheckForTap.<init>(android.widget.AbsListView):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.CheckForTap.<init>(android.widget.AbsListView):void");
        }

        /* synthetic */ CheckForTap(android.widget.AbsListView r1, android.widget.AbsListView.AnonymousClass1 r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.CheckForTap.<init>(android.widget.AbsListView, android.widget.AbsListView$1):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.CheckForTap.<init>(android.widget.AbsListView, android.widget.AbsListView$1):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 0073
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.CheckForTap.<init>(android.widget.AbsListView, android.widget.AbsListView$1):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.CheckForTap.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.CheckForTap.run():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.CheckForTap.run():void");
        }
    }

    private class FlingRunnable implements Runnable {
        private static final int FLYWHEEL_TIMEOUT = 40;
        private final Runnable mCheckFlywheel;
        private int mLastFlingY;
        private final OverScroller mScroller;
        final /* synthetic */ AbsListView this$0;

        /* renamed from: android.widget.AbsListView.FlingRunnable.1 */
        class AnonymousClass1 implements Runnable {
            final /* synthetic */ FlingRunnable this$1;

            AnonymousClass1(android.widget.AbsListView.FlingRunnable r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.FlingRunnable.1.<init>(android.widget.AbsListView$FlingRunnable):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.FlingRunnable.1.<init>(android.widget.AbsListView$FlingRunnable):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.FlingRunnable.1.<init>(android.widget.AbsListView$FlingRunnable):void");
            }

            public void run() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.FlingRunnable.1.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.FlingRunnable.1.run():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.FlingRunnable.1.run():void");
            }
        }

        FlingRunnable(android.widget.AbsListView r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.FlingRunnable.<init>(android.widget.AbsListView):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.FlingRunnable.<init>(android.widget.AbsListView):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.FlingRunnable.<init>(android.widget.AbsListView):void");
        }

        static /* synthetic */ android.widget.OverScroller access$1600(android.widget.AbsListView.FlingRunnable r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.FlingRunnable.access$1600(android.widget.AbsListView$FlingRunnable):android.widget.OverScroller
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.FlingRunnable.access$1600(android.widget.AbsListView$FlingRunnable):android.widget.OverScroller
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.FlingRunnable.access$1600(android.widget.AbsListView$FlingRunnable):android.widget.OverScroller");
        }

        void edgeReached(int r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.FlingRunnable.edgeReached(int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.FlingRunnable.edgeReached(int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.FlingRunnable.edgeReached(int):void");
        }

        void endFling() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.FlingRunnable.endFling():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.FlingRunnable.endFling():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.FlingRunnable.endFling():void");
        }

        void flywheelTouch() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.FlingRunnable.flywheelTouch():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.FlingRunnable.flywheelTouch():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.FlingRunnable.flywheelTouch():void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.FlingRunnable.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.FlingRunnable.run():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.FlingRunnable.run():void");
        }

        void start(int r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.FlingRunnable.start(int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.FlingRunnable.start(int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.FlingRunnable.start(int):void");
        }

        void startOverfling(int r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.FlingRunnable.startOverfling(int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.FlingRunnable.startOverfling(int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.FlingRunnable.startOverfling(int):void");
        }

        void startScroll(int r1, int r2, boolean r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.FlingRunnable.startScroll(int, int, boolean):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.FlingRunnable.startScroll(int, int, boolean):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.FlingRunnable.startScroll(int, int, boolean):void");
        }

        void startSpringback() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.FlingRunnable.startSpringback():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.FlingRunnable.startSpringback():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.FlingRunnable.startSpringback():void");
        }
    }

    private class InputConnectionWrapper implements InputConnection {
        private final EditorInfo mOutAttrs;
        private InputConnection mTarget;
        final /* synthetic */ AbsListView this$0;

        public InputConnectionWrapper(android.widget.AbsListView r1, android.view.inputmethod.EditorInfo r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.InputConnectionWrapper.<init>(android.widget.AbsListView, android.view.inputmethod.EditorInfo):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.InputConnectionWrapper.<init>(android.widget.AbsListView, android.view.inputmethod.EditorInfo):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.InputConnectionWrapper.<init>(android.widget.AbsListView, android.view.inputmethod.EditorInfo):void");
        }

        private android.view.inputmethod.InputConnection getTarget() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.InputConnectionWrapper.getTarget():android.view.inputmethod.InputConnection
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.InputConnectionWrapper.getTarget():android.view.inputmethod.InputConnection
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.InputConnectionWrapper.getTarget():android.view.inputmethod.InputConnection");
        }

        public boolean finishComposingText() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.InputConnectionWrapper.finishComposingText():boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.InputConnectionWrapper.finishComposingText():boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.InputConnectionWrapper.finishComposingText():boolean");
        }

        public int getCursorCapsMode(int r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.InputConnectionWrapper.getCursorCapsMode(int):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.InputConnectionWrapper.getCursorCapsMode(int):int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.InputConnectionWrapper.getCursorCapsMode(int):int");
        }

        public java.lang.CharSequence getSelectedText(int r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.InputConnectionWrapper.getSelectedText(int):java.lang.CharSequence
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.InputConnectionWrapper.getSelectedText(int):java.lang.CharSequence
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.InputConnectionWrapper.getSelectedText(int):java.lang.CharSequence");
        }

        public java.lang.CharSequence getTextAfterCursor(int r1, int r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.InputConnectionWrapper.getTextAfterCursor(int, int):java.lang.CharSequence
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.InputConnectionWrapper.getTextAfterCursor(int, int):java.lang.CharSequence
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.InputConnectionWrapper.getTextAfterCursor(int, int):java.lang.CharSequence");
        }

        public java.lang.CharSequence getTextBeforeCursor(int r1, int r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.InputConnectionWrapper.getTextBeforeCursor(int, int):java.lang.CharSequence
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.InputConnectionWrapper.getTextBeforeCursor(int, int):java.lang.CharSequence
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.InputConnectionWrapper.getTextBeforeCursor(int, int):java.lang.CharSequence");
        }

        public boolean performEditorAction(int r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.InputConnectionWrapper.performEditorAction(int):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.InputConnectionWrapper.performEditorAction(int):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.InputConnectionWrapper.performEditorAction(int):boolean");
        }

        public boolean reportFullscreenMode(boolean r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.InputConnectionWrapper.reportFullscreenMode(boolean):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.InputConnectionWrapper.reportFullscreenMode(boolean):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.InputConnectionWrapper.reportFullscreenMode(boolean):boolean");
        }

        public boolean sendKeyEvent(android.view.KeyEvent r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.InputConnectionWrapper.sendKeyEvent(android.view.KeyEvent):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.InputConnectionWrapper.sendKeyEvent(android.view.KeyEvent):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.InputConnectionWrapper.sendKeyEvent(android.view.KeyEvent):boolean");
        }

        public ExtractedText getExtractedText(ExtractedTextRequest request, int flags) {
            return getTarget().getExtractedText(request, flags);
        }

        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            return getTarget().deleteSurroundingText(beforeLength, afterLength);
        }

        public boolean setComposingText(CharSequence text, int newCursorPosition) {
            return getTarget().setComposingText(text, newCursorPosition);
        }

        public boolean setComposingRegion(int start, int end) {
            return getTarget().setComposingRegion(start, end);
        }

        public boolean commitText(CharSequence text, int newCursorPosition) {
            return getTarget().commitText(text, newCursorPosition);
        }

        public boolean commitCompletion(CompletionInfo text) {
            return getTarget().commitCompletion(text);
        }

        public boolean commitCorrection(CorrectionInfo correctionInfo) {
            return getTarget().commitCorrection(correctionInfo);
        }

        public boolean setSelection(int start, int end) {
            return getTarget().setSelection(start, end);
        }

        public boolean performContextMenuAction(int id) {
            return getTarget().performContextMenuAction(id);
        }

        public boolean beginBatchEdit() {
            return getTarget().beginBatchEdit();
        }

        public boolean endBatchEdit() {
            return getTarget().endBatchEdit();
        }

        public boolean clearMetaKeyStates(int states) {
            return getTarget().clearMetaKeyStates(states);
        }

        public boolean performPrivateCommand(String action, Bundle data) {
            return getTarget().performPrivateCommand(action, data);
        }

        public boolean requestCursorUpdates(int cursorUpdateMode) {
            return getTarget().requestCursorUpdates(cursorUpdateMode);
        }
    }

    public static class LayoutParams extends android.view.ViewGroup.LayoutParams {
        @ExportedProperty(category = "list")
        boolean forceAdd;
        long itemId;
        @ExportedProperty(category = "list")
        boolean recycledHeaderFooter;
        int scrappedFromPosition;
        @ExportedProperty(category = "list", mapping = {@IntToString(from = -1, to = "ITEM_VIEW_TYPE_IGNORE"), @IntToString(from = -2, to = "ITEM_VIEW_TYPE_HEADER_OR_FOOTER")})
        int viewType;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.itemId = -1;
        }

        public LayoutParams(int w, int h) {
            super(w, h);
            this.itemId = -1;
        }

        public LayoutParams(int w, int h, int viewType) {
            super(w, h);
            this.itemId = -1;
            this.viewType = viewType;
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams source) {
            super(source);
            this.itemId = -1;
        }
    }

    class ListItemAccessibilityDelegate extends AccessibilityDelegate {
        final /* synthetic */ AbsListView this$0;

        ListItemAccessibilityDelegate(android.widget.AbsListView r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.ListItemAccessibilityDelegate.<init>(android.widget.AbsListView):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.ListItemAccessibilityDelegate.<init>(android.widget.AbsListView):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.ListItemAccessibilityDelegate.<init>(android.widget.AbsListView):void");
        }

        public android.view.accessibility.AccessibilityNodeInfo createAccessibilityNodeInfo(android.view.View r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.ListItemAccessibilityDelegate.createAccessibilityNodeInfo(android.view.View):android.view.accessibility.AccessibilityNodeInfo
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.ListItemAccessibilityDelegate.createAccessibilityNodeInfo(android.view.View):android.view.accessibility.AccessibilityNodeInfo
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.ListItemAccessibilityDelegate.createAccessibilityNodeInfo(android.view.View):android.view.accessibility.AccessibilityNodeInfo");
        }

        public void onInitializeAccessibilityNodeInfo(android.view.View r1, android.view.accessibility.AccessibilityNodeInfo r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.ListItemAccessibilityDelegate.onInitializeAccessibilityNodeInfo(android.view.View, android.view.accessibility.AccessibilityNodeInfo):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.ListItemAccessibilityDelegate.onInitializeAccessibilityNodeInfo(android.view.View, android.view.accessibility.AccessibilityNodeInfo):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.ListItemAccessibilityDelegate.onInitializeAccessibilityNodeInfo(android.view.View, android.view.accessibility.AccessibilityNodeInfo):void");
        }

        public boolean performAccessibilityAction(android.view.View r1, int r2, android.os.Bundle r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.ListItemAccessibilityDelegate.performAccessibilityAction(android.view.View, int, android.os.Bundle):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.ListItemAccessibilityDelegate.performAccessibilityAction(android.view.View, int, android.os.Bundle):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.ListItemAccessibilityDelegate.performAccessibilityAction(android.view.View, int, android.os.Bundle):boolean");
        }
    }

    public interface MultiChoiceModeListener extends Callback {
        void onItemCheckedStateChanged(ActionMode actionMode, int i, long j, boolean z);
    }

    class MultiChoiceModeWrapper implements MultiChoiceModeListener {
        private MultiChoiceModeListener mWrapped;
        final /* synthetic */ AbsListView this$0;

        MultiChoiceModeWrapper(android.widget.AbsListView r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.MultiChoiceModeWrapper.<init>(android.widget.AbsListView):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.MultiChoiceModeWrapper.<init>(android.widget.AbsListView):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.MultiChoiceModeWrapper.<init>(android.widget.AbsListView):void");
        }

        public boolean hasWrappedCallback() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.MultiChoiceModeWrapper.hasWrappedCallback():boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.MultiChoiceModeWrapper.hasWrappedCallback():boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.MultiChoiceModeWrapper.hasWrappedCallback():boolean");
        }

        public boolean onActionItemClicked(android.view.ActionMode r1, android.view.MenuItem r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.MultiChoiceModeWrapper.onActionItemClicked(android.view.ActionMode, android.view.MenuItem):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.MultiChoiceModeWrapper.onActionItemClicked(android.view.ActionMode, android.view.MenuItem):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.MultiChoiceModeWrapper.onActionItemClicked(android.view.ActionMode, android.view.MenuItem):boolean");
        }

        public boolean onCreateActionMode(android.view.ActionMode r1, android.view.Menu r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.MultiChoiceModeWrapper.onCreateActionMode(android.view.ActionMode, android.view.Menu):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.MultiChoiceModeWrapper.onCreateActionMode(android.view.ActionMode, android.view.Menu):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.MultiChoiceModeWrapper.onCreateActionMode(android.view.ActionMode, android.view.Menu):boolean");
        }

        public void onDestroyActionMode(android.view.ActionMode r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.MultiChoiceModeWrapper.onDestroyActionMode(android.view.ActionMode):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.MultiChoiceModeWrapper.onDestroyActionMode(android.view.ActionMode):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.MultiChoiceModeWrapper.onDestroyActionMode(android.view.ActionMode):void");
        }

        public void onItemCheckedStateChanged(android.view.ActionMode r1, int r2, long r3, boolean r5) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.MultiChoiceModeWrapper.onItemCheckedStateChanged(android.view.ActionMode, int, long, boolean):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.MultiChoiceModeWrapper.onItemCheckedStateChanged(android.view.ActionMode, int, long, boolean):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.MultiChoiceModeWrapper.onItemCheckedStateChanged(android.view.ActionMode, int, long, boolean):void");
        }

        public boolean onPrepareActionMode(android.view.ActionMode r1, android.view.Menu r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.MultiChoiceModeWrapper.onPrepareActionMode(android.view.ActionMode, android.view.Menu):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.MultiChoiceModeWrapper.onPrepareActionMode(android.view.ActionMode, android.view.Menu):boolean
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.MultiChoiceModeWrapper.onPrepareActionMode(android.view.ActionMode, android.view.Menu):boolean");
        }

        public void setWrapped(android.widget.AbsListView.MultiChoiceModeListener r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.MultiChoiceModeWrapper.setWrapped(android.widget.AbsListView$MultiChoiceModeListener):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.MultiChoiceModeWrapper.setWrapped(android.widget.AbsListView$MultiChoiceModeListener):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.MultiChoiceModeWrapper.setWrapped(android.widget.AbsListView$MultiChoiceModeListener):void");
        }
    }

    public interface OnScrollListener {
        public static final int SCROLL_STATE_FLING = 2;
        public static final int SCROLL_STATE_IDLE = 0;
        public static final int SCROLL_STATE_TOUCH_SCROLL = 1;

        void onScroll(AbsListView absListView, int i, int i2, int i3);

        void onScrollStateChanged(AbsListView absListView, int i);
    }

    private class PerformClick extends WindowRunnnable implements Runnable {
        int mClickMotionPosition;
        final /* synthetic */ AbsListView this$0;

        private PerformClick(android.widget.AbsListView r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.PerformClick.<init>(android.widget.AbsListView):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.PerformClick.<init>(android.widget.AbsListView):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.PerformClick.<init>(android.widget.AbsListView):void");
        }

        /* synthetic */ PerformClick(android.widget.AbsListView r1, android.widget.AbsListView.AnonymousClass1 r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.PerformClick.<init>(android.widget.AbsListView, android.widget.AbsListView$1):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.PerformClick.<init>(android.widget.AbsListView, android.widget.AbsListView$1):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 0073
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.PerformClick.<init>(android.widget.AbsListView, android.widget.AbsListView$1):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.PerformClick.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.PerformClick.run():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.PerformClick.run():void");
        }
    }

    class PositionScroller extends AbsPositionScroller implements Runnable {
        private static final int MOVE_DOWN_BOUND = 3;
        private static final int MOVE_DOWN_POS = 1;
        private static final int MOVE_OFFSET = 5;
        private static final int MOVE_UP_BOUND = 4;
        private static final int MOVE_UP_POS = 2;
        private static final int SCROLL_DURATION = 200;
        private int mBoundPos;
        private final int mExtraScroll;
        private int mLastSeenPos;
        private int mMode;
        private int mOffsetFromTop;
        private int mScrollDuration;
        private int mTargetPos;
        final /* synthetic */ AbsListView this$0;

        /* renamed from: android.widget.AbsListView.PositionScroller.1 */
        class AnonymousClass1 implements Runnable {
            final /* synthetic */ PositionScroller this$1;
            final /* synthetic */ int val$position;

            AnonymousClass1(android.widget.AbsListView.PositionScroller r1, int r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.PositionScroller.1.<init>(android.widget.AbsListView$PositionScroller, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.PositionScroller.1.<init>(android.widget.AbsListView$PositionScroller, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.PositionScroller.1.<init>(android.widget.AbsListView$PositionScroller, int):void");
            }

            public void run() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.PositionScroller.1.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.PositionScroller.1.run():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.PositionScroller.1.run():void");
            }
        }

        /* renamed from: android.widget.AbsListView.PositionScroller.2 */
        class AnonymousClass2 implements Runnable {
            final /* synthetic */ PositionScroller this$1;
            final /* synthetic */ int val$boundPosition;
            final /* synthetic */ int val$position;

            AnonymousClass2(android.widget.AbsListView.PositionScroller r1, int r2, int r3) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.PositionScroller.2.<init>(android.widget.AbsListView$PositionScroller, int, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.PositionScroller.2.<init>(android.widget.AbsListView$PositionScroller, int, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.PositionScroller.2.<init>(android.widget.AbsListView$PositionScroller, int, int):void");
            }

            public void run() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.PositionScroller.2.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.PositionScroller.2.run():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.PositionScroller.2.run():void");
            }
        }

        /* renamed from: android.widget.AbsListView.PositionScroller.3 */
        class AnonymousClass3 implements Runnable {
            final /* synthetic */ PositionScroller this$1;
            final /* synthetic */ int val$duration;
            final /* synthetic */ int val$position;
            final /* synthetic */ int val$postOffset;

            AnonymousClass3(android.widget.AbsListView.PositionScroller r1, int r2, int r3, int r4) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.PositionScroller.3.<init>(android.widget.AbsListView$PositionScroller, int, int, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.PositionScroller.3.<init>(android.widget.AbsListView$PositionScroller, int, int, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.PositionScroller.3.<init>(android.widget.AbsListView$PositionScroller, int, int, int):void");
            }

            public void run() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.PositionScroller.3.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.PositionScroller.3.run():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.PositionScroller.3.run():void");
            }
        }

        PositionScroller(android.widget.AbsListView r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.PositionScroller.<init>(android.widget.AbsListView):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.PositionScroller.<init>(android.widget.AbsListView):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.PositionScroller.<init>(android.widget.AbsListView):void");
        }

        private void scrollToVisible(int r1, int r2, int r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.PositionScroller.scrollToVisible(int, int, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.PositionScroller.scrollToVisible(int, int, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.PositionScroller.scrollToVisible(int, int, int):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.PositionScroller.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.PositionScroller.run():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.PositionScroller.run():void");
        }

        public void start(int r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.PositionScroller.start(int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.PositionScroller.start(int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.PositionScroller.start(int):void");
        }

        public void start(int r1, int r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.PositionScroller.start(int, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.PositionScroller.start(int, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.PositionScroller.start(int, int):void");
        }

        public void startWithOffset(int r1, int r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.PositionScroller.startWithOffset(int, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.PositionScroller.startWithOffset(int, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.PositionScroller.startWithOffset(int, int):void");
        }

        public void startWithOffset(int r1, int r2, int r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.PositionScroller.startWithOffset(int, int, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.PositionScroller.startWithOffset(int, int, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.PositionScroller.startWithOffset(int, int, int):void");
        }

        public void stop() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.AbsListView.PositionScroller.stop():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.AbsListView.PositionScroller.stop():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.PositionScroller.stop():void");
        }
    }

    class RecycleBin {
        private View[] mActiveViews;
        private ArrayList<View> mCurrentScrap;
        private int mFirstActivePosition;
        private RecyclerListener mRecyclerListener;
        private ArrayList<View>[] mScrapViews;
        private ArrayList<View> mSkippedScrap;
        private SparseArray<View> mTransientStateViews;
        private LongSparseArray<View> mTransientStateViewsById;
        private int mViewTypeCount;
        final /* synthetic */ AbsListView this$0;

        RecycleBin(AbsListView absListView) {
            this.this$0 = absListView;
            this.mActiveViews = new View[AbsListView.TRANSCRIPT_MODE_DISABLED];
        }

        public void setViewTypeCount(int viewTypeCount) {
            if (viewTypeCount < AbsListView.TRANSCRIPT_MODE_NORMAL) {
                throw new IllegalArgumentException("Can't have a viewTypeCount < 1");
            }
            ArrayList<View>[] scrapViews = new ArrayList[viewTypeCount];
            for (int i = AbsListView.TRANSCRIPT_MODE_DISABLED; i < viewTypeCount; i += AbsListView.TRANSCRIPT_MODE_NORMAL) {
                scrapViews[i] = new ArrayList();
            }
            this.mViewTypeCount = viewTypeCount;
            this.mCurrentScrap = scrapViews[AbsListView.TRANSCRIPT_MODE_DISABLED];
            this.mScrapViews = scrapViews;
        }

        public void markChildrenDirty() {
            int i;
            int count;
            ArrayList<View> scrap;
            int scrapCount;
            if (this.mViewTypeCount == AbsListView.TRANSCRIPT_MODE_NORMAL) {
                scrap = this.mCurrentScrap;
                scrapCount = scrap.size();
                for (i = AbsListView.TRANSCRIPT_MODE_DISABLED; i < scrapCount; i += AbsListView.TRANSCRIPT_MODE_NORMAL) {
                    ((View) scrap.get(i)).forceLayout();
                }
            } else {
                int typeCount = this.mViewTypeCount;
                for (i = AbsListView.TRANSCRIPT_MODE_DISABLED; i < typeCount; i += AbsListView.TRANSCRIPT_MODE_NORMAL) {
                    scrap = this.mScrapViews[i];
                    scrapCount = scrap.size();
                    for (int j = AbsListView.TRANSCRIPT_MODE_DISABLED; j < scrapCount; j += AbsListView.TRANSCRIPT_MODE_NORMAL) {
                        ((View) scrap.get(j)).forceLayout();
                    }
                }
            }
            if (this.mTransientStateViews != null) {
                count = this.mTransientStateViews.size();
                for (i = AbsListView.TRANSCRIPT_MODE_DISABLED; i < count; i += AbsListView.TRANSCRIPT_MODE_NORMAL) {
                    ((View) this.mTransientStateViews.valueAt(i)).forceLayout();
                }
            }
            if (this.mTransientStateViewsById != null) {
                count = this.mTransientStateViewsById.size();
                for (i = AbsListView.TRANSCRIPT_MODE_DISABLED; i < count; i += AbsListView.TRANSCRIPT_MODE_NORMAL) {
                    ((View) this.mTransientStateViewsById.valueAt(i)).forceLayout();
                }
            }
        }

        public boolean shouldRecycleViewType(int viewType) {
            return viewType >= 0 ? true : AbsListView.PROFILE_SCROLLING;
        }

        void clear() {
            if (this.mViewTypeCount == AbsListView.TRANSCRIPT_MODE_NORMAL) {
                clearScrap(this.mCurrentScrap);
            } else {
                int typeCount = this.mViewTypeCount;
                for (int i = AbsListView.TRANSCRIPT_MODE_DISABLED; i < typeCount; i += AbsListView.TRANSCRIPT_MODE_NORMAL) {
                    clearScrap(this.mScrapViews[i]);
                }
            }
            clearTransientStateViews();
        }

        void fillActiveViews(int childCount, int firstActivePosition) {
            if (this.mActiveViews.length < childCount) {
                this.mActiveViews = new View[childCount];
            }
            this.mFirstActivePosition = firstActivePosition;
            View[] activeViews = this.mActiveViews;
            for (int i = AbsListView.TRANSCRIPT_MODE_DISABLED; i < childCount; i += AbsListView.TRANSCRIPT_MODE_NORMAL) {
                View child = this.this$0.getChildAt(i);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (!(lp == null || lp.viewType == -2)) {
                    activeViews[i] = child;
                    lp.scrappedFromPosition = firstActivePosition + i;
                }
            }
        }

        View getActiveView(int position) {
            int index = position - this.mFirstActivePosition;
            View[] activeViews = this.mActiveViews;
            if (index < 0 || index >= activeViews.length) {
                return null;
            }
            View match = activeViews[index];
            activeViews[index] = null;
            return match;
        }

        View getTransientStateView(int position) {
            if (this.this$0.mAdapter == null || !this.this$0.mAdapterHasStableIds || this.mTransientStateViewsById == null) {
                if (this.mTransientStateViews != null) {
                    int index = this.mTransientStateViews.indexOfKey(position);
                    if (index >= 0) {
                        View result = (View) this.mTransientStateViews.valueAt(index);
                        this.mTransientStateViews.removeAt(index);
                        return result;
                    }
                }
                return null;
            }
            long id = this.this$0.mAdapter.getItemId(position);
            result = (View) this.mTransientStateViewsById.get(id);
            this.mTransientStateViewsById.remove(id);
            return result;
        }

        void clearTransientStateViews() {
            int N;
            int i;
            SparseArray<View> viewsByPos = this.mTransientStateViews;
            if (viewsByPos != null) {
                N = viewsByPos.size();
                for (i = AbsListView.TRANSCRIPT_MODE_DISABLED; i < N; i += AbsListView.TRANSCRIPT_MODE_NORMAL) {
                    removeDetachedView((View) viewsByPos.valueAt(i), AbsListView.PROFILE_SCROLLING);
                }
                viewsByPos.clear();
            }
            LongSparseArray<View> viewsById = this.mTransientStateViewsById;
            if (viewsById != null) {
                N = viewsById.size();
                for (i = AbsListView.TRANSCRIPT_MODE_DISABLED; i < N; i += AbsListView.TRANSCRIPT_MODE_NORMAL) {
                    removeDetachedView((View) viewsById.valueAt(i), AbsListView.PROFILE_SCROLLING);
                }
                viewsById.clear();
            }
        }

        View getScrapView(int position) {
            if (this.mViewTypeCount == AbsListView.TRANSCRIPT_MODE_NORMAL) {
                return retrieveFromScrap(this.mCurrentScrap, position);
            }
            int whichScrap = this.this$0.mAdapter.getItemViewType(position);
            if (whichScrap < 0 || whichScrap >= this.mScrapViews.length) {
                return null;
            }
            return retrieveFromScrap(this.mScrapViews[whichScrap], position);
        }

        void addScrapView(View scrap, int position) {
            LayoutParams lp = (LayoutParams) scrap.getLayoutParams();
            if (lp != null) {
                lp.scrappedFromPosition = position;
                int viewType = lp.viewType;
                if (shouldRecycleViewType(viewType)) {
                    scrap.dispatchStartTemporaryDetach();
                    this.this$0.notifyViewAccessibilityStateChangedIfNeeded(AbsListView.TRANSCRIPT_MODE_NORMAL);
                    if (!scrap.hasTransientState()) {
                        if (this.mViewTypeCount == AbsListView.TRANSCRIPT_MODE_NORMAL) {
                            this.mCurrentScrap.add(scrap);
                        } else {
                            this.mScrapViews[viewType].add(scrap);
                        }
                        if (this.mRecyclerListener != null) {
                            this.mRecyclerListener.onMovedToScrapHeap(scrap);
                        }
                    } else if (this.this$0.mAdapter != null && this.this$0.mAdapterHasStableIds) {
                        if (this.mTransientStateViewsById == null) {
                            this.mTransientStateViewsById = new LongSparseArray();
                        }
                        this.mTransientStateViewsById.put(lp.itemId, scrap);
                    } else if (this.this$0.mDataChanged) {
                        if (this.mSkippedScrap == null) {
                            this.mSkippedScrap = new ArrayList();
                        }
                        this.mSkippedScrap.add(scrap);
                    } else {
                        if (this.mTransientStateViews == null) {
                            this.mTransientStateViews = new SparseArray();
                        }
                        this.mTransientStateViews.put(position, scrap);
                    }
                }
            }
        }

        void removeSkippedScrap() {
            if (this.mSkippedScrap != null) {
                int count = this.mSkippedScrap.size();
                for (int i = AbsListView.TRANSCRIPT_MODE_DISABLED; i < count; i += AbsListView.TRANSCRIPT_MODE_NORMAL) {
                    removeDetachedView((View) this.mSkippedScrap.get(i), AbsListView.PROFILE_SCROLLING);
                }
                this.mSkippedScrap.clear();
            }
        }

        void scrapActiveViews() {
            boolean hasListener;
            boolean multipleScraps;
            View[] activeViews = this.mActiveViews;
            if (this.mRecyclerListener != null) {
                hasListener = true;
            } else {
                hasListener = AbsListView.PROFILE_SCROLLING;
            }
            if (this.mViewTypeCount > AbsListView.TRANSCRIPT_MODE_NORMAL) {
                multipleScraps = true;
            } else {
                multipleScraps = AbsListView.PROFILE_SCROLLING;
            }
            ArrayList<View> scrapViews = this.mCurrentScrap;
            for (int i = activeViews.length + AbsListView.TOUCH_MODE_UNKNOWN; i >= 0; i += AbsListView.TOUCH_MODE_UNKNOWN) {
                View victim = activeViews[i];
                if (victim != null) {
                    LayoutParams lp = (LayoutParams) victim.getLayoutParams();
                    int whichScrap = lp.viewType;
                    activeViews[i] = null;
                    if (victim.hasTransientState()) {
                        victim.dispatchStartTemporaryDetach();
                        if (this.this$0.mAdapter != null && this.this$0.mAdapterHasStableIds) {
                            if (this.mTransientStateViewsById == null) {
                                this.mTransientStateViewsById = new LongSparseArray();
                            }
                            this.mTransientStateViewsById.put(this.this$0.mAdapter.getItemId(this.mFirstActivePosition + i), victim);
                        } else if (!this.this$0.mDataChanged) {
                            if (this.mTransientStateViews == null) {
                                this.mTransientStateViews = new SparseArray();
                            }
                            this.mTransientStateViews.put(this.mFirstActivePosition + i, victim);
                        } else if (whichScrap != -2) {
                            removeDetachedView(victim, AbsListView.PROFILE_SCROLLING);
                        }
                    } else if (shouldRecycleViewType(whichScrap)) {
                        if (multipleScraps) {
                            scrapViews = this.mScrapViews[whichScrap];
                        }
                        victim.dispatchStartTemporaryDetach();
                        lp.scrappedFromPosition = this.mFirstActivePosition + i;
                        scrapViews.add(victim);
                        if (hasListener) {
                            this.mRecyclerListener.onMovedToScrapHeap(victim);
                        }
                    } else if (whichScrap != -2) {
                        removeDetachedView(victim, AbsListView.PROFILE_SCROLLING);
                    }
                }
            }
            pruneScrapViews();
        }

        private void pruneScrapViews() {
            int i;
            View v;
            int maxViews = this.mActiveViews.length;
            int viewTypeCount = this.mViewTypeCount;
            ArrayList<View>[] scrapViews = this.mScrapViews;
            for (i = AbsListView.TRANSCRIPT_MODE_DISABLED; i < viewTypeCount; i += AbsListView.TRANSCRIPT_MODE_NORMAL) {
                ArrayList<View> scrapPile = scrapViews[i];
                int size = scrapPile.size();
                int extras = size - maxViews;
                size += AbsListView.TOUCH_MODE_UNKNOWN;
                int j = AbsListView.TRANSCRIPT_MODE_DISABLED;
                int size2 = size;
                while (j < extras) {
                    size = size2 + AbsListView.TOUCH_MODE_UNKNOWN;
                    removeDetachedView((View) scrapPile.remove(size2), AbsListView.PROFILE_SCROLLING);
                    j += AbsListView.TRANSCRIPT_MODE_NORMAL;
                    size2 = size;
                }
            }
            SparseArray<View> transViewsByPos = this.mTransientStateViews;
            if (transViewsByPos != null) {
                i = AbsListView.TRANSCRIPT_MODE_DISABLED;
                while (i < transViewsByPos.size()) {
                    v = (View) transViewsByPos.valueAt(i);
                    if (!v.hasTransientState()) {
                        removeDetachedView(v, AbsListView.PROFILE_SCROLLING);
                        transViewsByPos.removeAt(i);
                        i += AbsListView.TOUCH_MODE_UNKNOWN;
                    }
                    i += AbsListView.TRANSCRIPT_MODE_NORMAL;
                }
            }
            LongSparseArray<View> transViewsById = this.mTransientStateViewsById;
            if (transViewsById != null) {
                i = AbsListView.TRANSCRIPT_MODE_DISABLED;
                while (i < transViewsById.size()) {
                    v = (View) transViewsById.valueAt(i);
                    if (!v.hasTransientState()) {
                        removeDetachedView(v, AbsListView.PROFILE_SCROLLING);
                        transViewsById.removeAt(i);
                        i += AbsListView.TOUCH_MODE_UNKNOWN;
                    }
                    i += AbsListView.TRANSCRIPT_MODE_NORMAL;
                }
            }
        }

        void reclaimScrapViews(List<View> views) {
            if (this.mViewTypeCount == AbsListView.TRANSCRIPT_MODE_NORMAL) {
                views.addAll(this.mCurrentScrap);
                return;
            }
            int viewTypeCount = this.mViewTypeCount;
            ArrayList<View>[] scrapViews = this.mScrapViews;
            for (int i = AbsListView.TRANSCRIPT_MODE_DISABLED; i < viewTypeCount; i += AbsListView.TRANSCRIPT_MODE_NORMAL) {
                views.addAll(scrapViews[i]);
            }
        }

        void setCacheColorHint(int color) {
            int i;
            ArrayList<View> scrap;
            int scrapCount;
            if (this.mViewTypeCount == AbsListView.TRANSCRIPT_MODE_NORMAL) {
                scrap = this.mCurrentScrap;
                scrapCount = scrap.size();
                for (i = AbsListView.TRANSCRIPT_MODE_DISABLED; i < scrapCount; i += AbsListView.TRANSCRIPT_MODE_NORMAL) {
                    ((View) scrap.get(i)).setDrawingCacheBackgroundColor(color);
                }
            } else {
                int typeCount = this.mViewTypeCount;
                for (i = AbsListView.TRANSCRIPT_MODE_DISABLED; i < typeCount; i += AbsListView.TRANSCRIPT_MODE_NORMAL) {
                    scrap = this.mScrapViews[i];
                    scrapCount = scrap.size();
                    for (int j = AbsListView.TRANSCRIPT_MODE_DISABLED; j < scrapCount; j += AbsListView.TRANSCRIPT_MODE_NORMAL) {
                        ((View) scrap.get(j)).setDrawingCacheBackgroundColor(color);
                    }
                }
            }
            View[] activeViews = this.mActiveViews;
            int count = activeViews.length;
            for (i = AbsListView.TRANSCRIPT_MODE_DISABLED; i < count; i += AbsListView.TRANSCRIPT_MODE_NORMAL) {
                View victim = activeViews[i];
                if (victim != null) {
                    victim.setDrawingCacheBackgroundColor(color);
                }
            }
        }

        private View retrieveFromScrap(ArrayList<View> scrapViews, int position) {
            int size = scrapViews.size();
            if (size <= 0) {
                return null;
            }
            View scrap;
            for (int i = AbsListView.TRANSCRIPT_MODE_DISABLED; i < size; i += AbsListView.TRANSCRIPT_MODE_NORMAL) {
                LayoutParams params = (LayoutParams) ((View) scrapViews.get(i)).getLayoutParams();
                if (this.this$0.mAdapterHasStableIds) {
                    if (this.this$0.mAdapter.getItemId(position) == params.itemId) {
                        return (View) scrapViews.remove(i);
                    }
                } else if (params.scrappedFromPosition == position) {
                    scrap = (View) scrapViews.remove(i);
                    clearAccessibilityFromScrap(scrap);
                    return scrap;
                }
            }
            scrap = (View) scrapViews.remove(size + AbsListView.TOUCH_MODE_UNKNOWN);
            clearAccessibilityFromScrap(scrap);
            return scrap;
        }

        private void clearScrap(ArrayList<View> scrap) {
            int scrapCount = scrap.size();
            for (int j = AbsListView.TRANSCRIPT_MODE_DISABLED; j < scrapCount; j += AbsListView.TRANSCRIPT_MODE_NORMAL) {
                removeDetachedView((View) scrap.remove((scrapCount + AbsListView.TOUCH_MODE_UNKNOWN) - j), AbsListView.PROFILE_SCROLLING);
            }
        }

        private void clearAccessibilityFromScrap(View view) {
            view.clearAccessibilityFocus();
            view.setAccessibilityDelegate(null);
        }

        private void removeDetachedView(View child, boolean animate) {
            child.setAccessibilityDelegate(null);
            this.this$0.removeDetachedView(child, animate);
        }
    }

    public interface RecyclerListener {
        void onMovedToScrapHeap(View view);
    }

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = null;
        LongSparseArray<Integer> checkIdState;
        SparseBooleanArray checkState;
        int checkedItemCount;
        String filter;
        long firstId;
        int height;
        boolean inActionMode;
        int position;
        long selectedId;
        int viewTop;

        /* renamed from: android.widget.AbsListView.SavedState.1 */
        static class AnonymousClass1 implements Creator<SavedState> {
            AnonymousClass1() {
            }

            public /* bridge */ /* synthetic */ Object m127createFromParcel(Parcel x0) {
                return createFromParcel(x0);
            }

            public /* bridge */ /* synthetic */ Object[] m128newArray(int x0) {
                return newArray(x0);
            }

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in, null);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        }

        /* synthetic */ SavedState(Parcel x0, AnonymousClass1 x1) {
            this(x0);
        }

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.selectedId = in.readLong();
            this.firstId = in.readLong();
            this.viewTop = in.readInt();
            this.position = in.readInt();
            this.height = in.readInt();
            this.filter = in.readString();
            this.inActionMode = in.readByte() != null ? true : AbsListView.PROFILE_SCROLLING;
            this.checkedItemCount = in.readInt();
            this.checkState = in.readSparseBooleanArray();
            int N = in.readInt();
            if (N > 0) {
                this.checkIdState = new LongSparseArray();
                for (int i = AbsListView.TRANSCRIPT_MODE_DISABLED; i < N; i += AbsListView.TRANSCRIPT_MODE_NORMAL) {
                    this.checkIdState.put(in.readLong(), Integer.valueOf(in.readInt()));
                }
            }
        }

        public void writeToParcel(Parcel out, int flags) {
            int i;
            int N;
            super.writeToParcel(out, flags);
            out.writeLong(this.selectedId);
            out.writeLong(this.firstId);
            out.writeInt(this.viewTop);
            out.writeInt(this.position);
            out.writeInt(this.height);
            out.writeString(this.filter);
            if (this.inActionMode) {
                i = AbsListView.TRANSCRIPT_MODE_NORMAL;
            } else {
                i = AbsListView.TRANSCRIPT_MODE_DISABLED;
            }
            out.writeByte((byte) i);
            out.writeInt(this.checkedItemCount);
            out.writeSparseBooleanArray(this.checkState);
            if (this.checkIdState != null) {
                N = this.checkIdState.size();
            } else {
                N = AbsListView.TRANSCRIPT_MODE_DISABLED;
            }
            out.writeInt(N);
            for (int i2 = AbsListView.TRANSCRIPT_MODE_DISABLED; i2 < N; i2 += AbsListView.TRANSCRIPT_MODE_NORMAL) {
                out.writeLong(this.checkIdState.keyAt(i2));
                out.writeInt(((Integer) this.checkIdState.valueAt(i2)).intValue());
            }
        }

        public String toString() {
            return "AbsListView.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " selectedId=" + this.selectedId + " firstId=" + this.firstId + " viewTop=" + this.viewTop + " position=" + this.position + " height=" + this.height + " filter=" + this.filter + " checkState=" + this.checkState + "}";
        }

        static {
            CREATOR = new AnonymousClass1();
        }
    }

    public interface SelectionBoundsAdjuster {
        void adjustListItemSelectionBounds(Rect rect);
    }

    abstract void fillGap(boolean z);

    abstract int findMotionRow(int i);

    abstract void setSelectionInt(int i);

    public /* bridge */ /* synthetic */ android.view.ViewGroup.LayoutParams generateLayoutParams(AttributeSet x0) {
        return generateLayoutParams(x0);
    }

    public /* bridge */ /* synthetic */ void setAdapter(Adapter x0) {
        setAdapter((ListAdapter) x0);
    }

    static {
        sLinearInterpolator = new LinearInterpolator();
    }

    public AbsListView(Context context) {
        super(context);
        this.mChoiceMode = TRANSCRIPT_MODE_DISABLED;
        this.mLayoutMode = TRANSCRIPT_MODE_DISABLED;
        this.mDeferNotifyDataSetChanged = PROFILE_SCROLLING;
        this.mDrawSelectorOnTop = PROFILE_SCROLLING;
        this.mSelectorPosition = TOUCH_MODE_UNKNOWN;
        this.mSelectorRect = new Rect();
        this.mRecycler = new RecycleBin(this);
        this.mSelectionLeftPadding = TRANSCRIPT_MODE_DISABLED;
        this.mSelectionTopPadding = TRANSCRIPT_MODE_DISABLED;
        this.mSelectionRightPadding = TRANSCRIPT_MODE_DISABLED;
        this.mSelectionBottomPadding = TRANSCRIPT_MODE_DISABLED;
        this.mListPadding = new Rect();
        this.mWidthMeasureSpec = TRANSCRIPT_MODE_DISABLED;
        this.mTouchMode = TOUCH_MODE_UNKNOWN;
        this.mSelectedTop = TRANSCRIPT_MODE_DISABLED;
        this.mSmoothScrollbarEnabled = true;
        this.mResurrectToPosition = TOUCH_MODE_UNKNOWN;
        this.mContextMenuInfo = null;
        this.mLastTouchMode = TOUCH_MODE_UNKNOWN;
        this.mScrollProfilingStarted = PROFILE_SCROLLING;
        this.mFlingProfilingStarted = PROFILE_SCROLLING;
        this.mScrollStrictSpan = null;
        this.mFlingStrictSpan = null;
        this.mLastScrollState = TRANSCRIPT_MODE_DISABLED;
        this.mVelocityScale = android.view.WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        this.mIsScrap = new boolean[TRANSCRIPT_MODE_NORMAL];
        this.mScrollOffset = new int[TRANSCRIPT_MODE_ALWAYS_SCROLL];
        this.mScrollConsumed = new int[TRANSCRIPT_MODE_ALWAYS_SCROLL];
        this.mTmpPoint = new float[TRANSCRIPT_MODE_ALWAYS_SCROLL];
        this.mNestedYOffset = TRANSCRIPT_MODE_DISABLED;
        this.mActivePointerId = TOUCH_MODE_UNKNOWN;
        this.mDirection = TRANSCRIPT_MODE_DISABLED;
        initAbsListView();
        this.mOwnerThread = Thread.currentThread();
        setVerticalScrollBarEnabled(true);
        TypedArray a = context.obtainStyledAttributes(R.styleable.View);
        initializeScrollbarsInternal(a);
        a.recycle();
    }

    public AbsListView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.absListViewStyle);
    }

    public AbsListView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, TRANSCRIPT_MODE_DISABLED);
    }

    public AbsListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mChoiceMode = TRANSCRIPT_MODE_DISABLED;
        this.mLayoutMode = TRANSCRIPT_MODE_DISABLED;
        this.mDeferNotifyDataSetChanged = PROFILE_SCROLLING;
        this.mDrawSelectorOnTop = PROFILE_SCROLLING;
        this.mSelectorPosition = TOUCH_MODE_UNKNOWN;
        this.mSelectorRect = new Rect();
        this.mRecycler = new RecycleBin(this);
        this.mSelectionLeftPadding = TRANSCRIPT_MODE_DISABLED;
        this.mSelectionTopPadding = TRANSCRIPT_MODE_DISABLED;
        this.mSelectionRightPadding = TRANSCRIPT_MODE_DISABLED;
        this.mSelectionBottomPadding = TRANSCRIPT_MODE_DISABLED;
        this.mListPadding = new Rect();
        this.mWidthMeasureSpec = TRANSCRIPT_MODE_DISABLED;
        this.mTouchMode = TOUCH_MODE_UNKNOWN;
        this.mSelectedTop = TRANSCRIPT_MODE_DISABLED;
        this.mSmoothScrollbarEnabled = true;
        this.mResurrectToPosition = TOUCH_MODE_UNKNOWN;
        this.mContextMenuInfo = null;
        this.mLastTouchMode = TOUCH_MODE_UNKNOWN;
        this.mScrollProfilingStarted = PROFILE_SCROLLING;
        this.mFlingProfilingStarted = PROFILE_SCROLLING;
        this.mScrollStrictSpan = null;
        this.mFlingStrictSpan = null;
        this.mLastScrollState = TRANSCRIPT_MODE_DISABLED;
        this.mVelocityScale = android.view.WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        this.mIsScrap = new boolean[TRANSCRIPT_MODE_NORMAL];
        this.mScrollOffset = new int[TRANSCRIPT_MODE_ALWAYS_SCROLL];
        this.mScrollConsumed = new int[TRANSCRIPT_MODE_ALWAYS_SCROLL];
        this.mTmpPoint = new float[TRANSCRIPT_MODE_ALWAYS_SCROLL];
        this.mNestedYOffset = TRANSCRIPT_MODE_DISABLED;
        this.mActivePointerId = TOUCH_MODE_UNKNOWN;
        this.mDirection = TRANSCRIPT_MODE_DISABLED;
        initAbsListView();
        this.mOwnerThread = Thread.currentThread();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AbsListView, defStyleAttr, defStyleRes);
        Drawable d = a.getDrawable(TRANSCRIPT_MODE_DISABLED);
        if (d != null) {
            setSelector(d);
        }
        this.mDrawSelectorOnTop = a.getBoolean(TRANSCRIPT_MODE_NORMAL, PROFILE_SCROLLING);
        setStackFromBottom(a.getBoolean(TRANSCRIPT_MODE_ALWAYS_SCROLL, PROFILE_SCROLLING));
        setScrollingCacheEnabled(a.getBoolean(TOUCH_MODE_SCROLL, true));
        setTextFilterEnabled(a.getBoolean(TOUCH_MODE_FLING, PROFILE_SCROLLING));
        setTranscriptMode(a.getInt(TOUCH_MODE_OVERSCROLL, TRANSCRIPT_MODE_DISABLED));
        setCacheColorHint(a.getColor(TOUCH_MODE_OVERFLING, TRANSCRIPT_MODE_DISABLED));
        setFastScrollEnabled(a.getBoolean(8, PROFILE_SCROLLING));
        setFastScrollStyle(a.getResourceId(11, TRANSCRIPT_MODE_DISABLED));
        setSmoothScrollbarEnabled(a.getBoolean(9, true));
        setChoiceMode(a.getInt(7, TRANSCRIPT_MODE_DISABLED));
        setFastScrollAlwaysVisible(a.getBoolean(10, PROFILE_SCROLLING));
        a.recycle();
    }

    private void initAbsListView() {
        setClickable(true);
        setFocusableInTouchMode(true);
        setWillNotDraw(PROFILE_SCROLLING);
        setAlwaysDrawnWithCacheEnabled(PROFILE_SCROLLING);
        setScrollingCacheEnabled(true);
        ViewConfiguration configuration = ViewConfiguration.get(this.mContext);
        this.mTouchSlop = configuration.getScaledTouchSlop();
        this.mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        this.mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        this.mOverscrollDistance = configuration.getScaledOverscrollDistance();
        this.mOverflingDistance = configuration.getScaledOverflingDistance();
        this.mDensityScale = getContext().getResources().getDisplayMetrics().density;
    }

    public void setOverScrollMode(int mode) {
        if (mode == TRANSCRIPT_MODE_ALWAYS_SCROLL) {
            this.mEdgeGlowTop = null;
            this.mEdgeGlowBottom = null;
        } else if (this.mEdgeGlowTop == null) {
            Context context = getContext();
            this.mEdgeGlowTop = new EdgeEffect(context);
            this.mEdgeGlowBottom = new EdgeEffect(context);
        }
        super.setOverScrollMode(mode);
    }

    public void setAdapter(ListAdapter adapter) {
        if (adapter != null) {
            this.mAdapterHasStableIds = this.mAdapter.hasStableIds();
            if (this.mChoiceMode != 0 && this.mAdapterHasStableIds && this.mCheckedIdStates == null) {
                this.mCheckedIdStates = new LongSparseArray();
            }
        }
        if (this.mCheckStates != null) {
            this.mCheckStates.clear();
        }
        if (this.mCheckedIdStates != null) {
            this.mCheckedIdStates.clear();
        }
    }

    public int getCheckedItemCount() {
        return this.mCheckedItemCount;
    }

    public boolean isItemChecked(int position) {
        if (this.mChoiceMode == 0 || this.mCheckStates == null) {
            return PROFILE_SCROLLING;
        }
        return this.mCheckStates.get(position);
    }

    public int getCheckedItemPosition() {
        if (this.mChoiceMode == TRANSCRIPT_MODE_NORMAL && this.mCheckStates != null && this.mCheckStates.size() == TRANSCRIPT_MODE_NORMAL) {
            return this.mCheckStates.keyAt(TRANSCRIPT_MODE_DISABLED);
        }
        return TOUCH_MODE_UNKNOWN;
    }

    public SparseBooleanArray getCheckedItemPositions() {
        if (this.mChoiceMode != 0) {
            return this.mCheckStates;
        }
        return null;
    }

    public long[] getCheckedItemIds() {
        if (this.mChoiceMode == 0 || this.mCheckedIdStates == null || this.mAdapter == null) {
            return new long[TRANSCRIPT_MODE_DISABLED];
        }
        LongSparseArray<Integer> idStates = this.mCheckedIdStates;
        int count = idStates.size();
        long[] ids = new long[count];
        for (int i = TRANSCRIPT_MODE_DISABLED; i < count; i += TRANSCRIPT_MODE_NORMAL) {
            ids[i] = idStates.keyAt(i);
        }
        return ids;
    }

    public void clearChoices() {
        if (this.mCheckStates != null) {
            this.mCheckStates.clear();
        }
        if (this.mCheckedIdStates != null) {
            this.mCheckedIdStates.clear();
        }
        this.mCheckedItemCount = TRANSCRIPT_MODE_DISABLED;
    }

    public void setItemChecked(int position, boolean value) {
        if (this.mChoiceMode != 0) {
            if (value && this.mChoiceMode == TOUCH_MODE_SCROLL && this.mChoiceActionMode == null) {
                if (this.mMultiChoiceModeCallback == null || !this.mMultiChoiceModeCallback.hasWrappedCallback()) {
                    throw new IllegalStateException("AbsListView: attempted to start selection mode for CHOICE_MODE_MULTIPLE_MODAL but no choice mode callback was supplied. Call setMultiChoiceModeListener to set a callback.");
                }
                this.mChoiceActionMode = startActionMode(this.mMultiChoiceModeCallback);
            }
            if (this.mChoiceMode == TRANSCRIPT_MODE_ALWAYS_SCROLL || this.mChoiceMode == TOUCH_MODE_SCROLL) {
                boolean oldValue = this.mCheckStates.get(position);
                this.mCheckStates.put(position, value);
                if (this.mCheckedIdStates != null && this.mAdapter.hasStableIds()) {
                    if (value) {
                        this.mCheckedIdStates.put(this.mAdapter.getItemId(position), Integer.valueOf(position));
                    } else {
                        this.mCheckedIdStates.delete(this.mAdapter.getItemId(position));
                    }
                }
                if (oldValue != value) {
                    if (value) {
                        this.mCheckedItemCount += TRANSCRIPT_MODE_NORMAL;
                    } else {
                        this.mCheckedItemCount += TOUCH_MODE_UNKNOWN;
                    }
                }
                if (this.mChoiceActionMode != null) {
                    this.mMultiChoiceModeCallback.onItemCheckedStateChanged(this.mChoiceActionMode, position, this.mAdapter.getItemId(position), value);
                }
            } else {
                boolean updateIds;
                if (this.mCheckedIdStates == null || !this.mAdapter.hasStableIds()) {
                    updateIds = PROFILE_SCROLLING;
                } else {
                    updateIds = true;
                }
                if (value || isItemChecked(position)) {
                    this.mCheckStates.clear();
                    if (updateIds) {
                        this.mCheckedIdStates.clear();
                    }
                }
                if (value) {
                    this.mCheckStates.put(position, true);
                    if (updateIds) {
                        this.mCheckedIdStates.put(this.mAdapter.getItemId(position), Integer.valueOf(position));
                    }
                    this.mCheckedItemCount = TRANSCRIPT_MODE_NORMAL;
                } else if (this.mCheckStates.size() == 0 || !this.mCheckStates.valueAt(TRANSCRIPT_MODE_DISABLED)) {
                    this.mCheckedItemCount = TRANSCRIPT_MODE_DISABLED;
                }
            }
            if (!this.mInLayout && !this.mBlockLayoutRequests) {
                this.mDataChanged = true;
                rememberSyncState();
                requestLayout();
            }
        }
    }

    public boolean performItemClick(View view, int position, long id) {
        boolean handled = PROFILE_SCROLLING;
        boolean dispatchItemClick = true;
        if (this.mChoiceMode != 0) {
            handled = true;
            boolean checkedStateChanged = PROFILE_SCROLLING;
            boolean checked;
            if (this.mChoiceMode == TRANSCRIPT_MODE_ALWAYS_SCROLL || (this.mChoiceMode == TOUCH_MODE_SCROLL && this.mChoiceActionMode != null)) {
                if (this.mCheckStates.get(position, PROFILE_SCROLLING)) {
                    checked = PROFILE_SCROLLING;
                } else {
                    checked = true;
                }
                this.mCheckStates.put(position, checked);
                if (this.mCheckedIdStates != null && this.mAdapter.hasStableIds()) {
                    if (checked) {
                        this.mCheckedIdStates.put(this.mAdapter.getItemId(position), Integer.valueOf(position));
                    } else {
                        this.mCheckedIdStates.delete(this.mAdapter.getItemId(position));
                    }
                }
                if (checked) {
                    this.mCheckedItemCount += TRANSCRIPT_MODE_NORMAL;
                } else {
                    this.mCheckedItemCount += TOUCH_MODE_UNKNOWN;
                }
                if (this.mChoiceActionMode != null) {
                    this.mMultiChoiceModeCallback.onItemCheckedStateChanged(this.mChoiceActionMode, position, id, checked);
                    dispatchItemClick = PROFILE_SCROLLING;
                }
                checkedStateChanged = true;
            } else if (this.mChoiceMode == TRANSCRIPT_MODE_NORMAL) {
                if (this.mCheckStates.get(position, PROFILE_SCROLLING)) {
                    checked = PROFILE_SCROLLING;
                } else {
                    checked = true;
                }
                if (checked) {
                    this.mCheckStates.clear();
                    this.mCheckStates.put(position, true);
                    if (this.mCheckedIdStates != null && this.mAdapter.hasStableIds()) {
                        this.mCheckedIdStates.clear();
                        this.mCheckedIdStates.put(this.mAdapter.getItemId(position), Integer.valueOf(position));
                    }
                    this.mCheckedItemCount = TRANSCRIPT_MODE_NORMAL;
                } else if (this.mCheckStates.size() == 0 || !this.mCheckStates.valueAt(TRANSCRIPT_MODE_DISABLED)) {
                    this.mCheckedItemCount = TRANSCRIPT_MODE_DISABLED;
                }
                checkedStateChanged = true;
            }
            if (checkedStateChanged) {
                updateOnScreenCheckedViews();
            }
        }
        if (dispatchItemClick) {
            return handled | super.performItemClick(view, position, id);
        }
        return handled;
    }

    private void updateOnScreenCheckedViews() {
        int firstPos = this.mFirstPosition;
        int count = getChildCount();
        boolean useActivated = getContext().getApplicationInfo().targetSdkVersion >= 11 ? true : PROFILE_SCROLLING;
        for (int i = TRANSCRIPT_MODE_DISABLED; i < count; i += TRANSCRIPT_MODE_NORMAL) {
            View child = getChildAt(i);
            int position = firstPos + i;
            if (child instanceof Checkable) {
                ((Checkable) child).setChecked(this.mCheckStates.get(position));
            } else if (useActivated) {
                child.setActivated(this.mCheckStates.get(position));
            }
        }
    }

    public int getChoiceMode() {
        return this.mChoiceMode;
    }

    public void setChoiceMode(int choiceMode) {
        this.mChoiceMode = choiceMode;
        if (this.mChoiceActionMode != null) {
            this.mChoiceActionMode.finish();
            this.mChoiceActionMode = null;
        }
        if (this.mChoiceMode != 0) {
            if (this.mCheckStates == null) {
                this.mCheckStates = new SparseBooleanArray(TRANSCRIPT_MODE_DISABLED);
            }
            if (this.mCheckedIdStates == null && this.mAdapter != null && this.mAdapter.hasStableIds()) {
                this.mCheckedIdStates = new LongSparseArray(TRANSCRIPT_MODE_DISABLED);
            }
            if (this.mChoiceMode == TOUCH_MODE_SCROLL) {
                clearChoices();
                setLongClickable(true);
            }
        }
    }

    public void setMultiChoiceModeListener(MultiChoiceModeListener listener) {
        if (this.mMultiChoiceModeCallback == null) {
            this.mMultiChoiceModeCallback = new MultiChoiceModeWrapper(this);
        }
        this.mMultiChoiceModeCallback.setWrapped(listener);
    }

    private boolean contentFits() {
        int childCount = getChildCount();
        if (childCount == 0) {
            return true;
        }
        if (childCount != this.mItemCount) {
            return PROFILE_SCROLLING;
        }
        if (getChildAt(TRANSCRIPT_MODE_DISABLED).getTop() < this.mListPadding.top || getChildAt(childCount + TOUCH_MODE_UNKNOWN).getBottom() > getHeight() - this.mListPadding.bottom) {
            return PROFILE_SCROLLING;
        }
        return true;
    }

    public void setFastScrollEnabled(boolean enabled) {
        if (this.mFastScrollEnabled != enabled) {
            this.mFastScrollEnabled = enabled;
            if (isOwnerThread()) {
                setFastScrollerEnabledUiThread(enabled);
            } else {
                post(new AnonymousClass1(this, enabled));
            }
        }
    }

    private void setFastScrollerEnabledUiThread(boolean enabled) {
        if (this.mFastScroll != null) {
            this.mFastScroll.setEnabled(enabled);
        } else if (enabled) {
            this.mFastScroll = new FastScroller(this, this.mFastScrollStyle);
            this.mFastScroll.setEnabled(true);
        }
        resolvePadding();
        if (this.mFastScroll != null) {
            this.mFastScroll.updateLayout();
        }
    }

    public void setFastScrollStyle(int styleResId) {
        if (this.mFastScroll == null) {
            this.mFastScrollStyle = styleResId;
        } else {
            this.mFastScroll.setStyle(styleResId);
        }
    }

    public void setFastScrollAlwaysVisible(boolean alwaysShow) {
        if (this.mFastScrollAlwaysVisible != alwaysShow) {
            if (alwaysShow && !this.mFastScrollEnabled) {
                setFastScrollEnabled(true);
            }
            this.mFastScrollAlwaysVisible = alwaysShow;
            if (isOwnerThread()) {
                setFastScrollerAlwaysVisibleUiThread(alwaysShow);
            } else {
                post(new AnonymousClass2(this, alwaysShow));
            }
        }
    }

    private void setFastScrollerAlwaysVisibleUiThread(boolean alwaysShow) {
        if (this.mFastScroll != null) {
            this.mFastScroll.setAlwaysShow(alwaysShow);
        }
    }

    private boolean isOwnerThread() {
        return this.mOwnerThread == Thread.currentThread() ? true : PROFILE_SCROLLING;
    }

    public boolean isFastScrollAlwaysVisible() {
        if (this.mFastScroll == null) {
            if (this.mFastScrollEnabled && this.mFastScrollAlwaysVisible) {
                return true;
            }
            return PROFILE_SCROLLING;
        } else if (this.mFastScroll.isEnabled() && this.mFastScroll.isAlwaysShowEnabled()) {
            return true;
        } else {
            return PROFILE_SCROLLING;
        }
    }

    public int getVerticalScrollbarWidth() {
        if (this.mFastScroll == null || !this.mFastScroll.isEnabled()) {
            return super.getVerticalScrollbarWidth();
        }
        return Math.max(super.getVerticalScrollbarWidth(), this.mFastScroll.getWidth());
    }

    @ExportedProperty
    public boolean isFastScrollEnabled() {
        if (this.mFastScroll == null) {
            return this.mFastScrollEnabled;
        }
        return this.mFastScroll.isEnabled();
    }

    public void setVerticalScrollbarPosition(int position) {
        super.setVerticalScrollbarPosition(position);
        if (this.mFastScroll != null) {
            this.mFastScroll.setScrollbarPosition(position);
        }
    }

    public void setScrollBarStyle(int style) {
        super.setScrollBarStyle(style);
        if (this.mFastScroll != null) {
            this.mFastScroll.setScrollBarStyle(style);
        }
    }

    protected boolean isVerticalScrollBarHidden() {
        return isFastScrollEnabled();
    }

    public void setSmoothScrollbarEnabled(boolean enabled) {
        this.mSmoothScrollbarEnabled = enabled;
    }

    @ExportedProperty
    public boolean isSmoothScrollbarEnabled() {
        return this.mSmoothScrollbarEnabled;
    }

    public void setOnScrollListener(OnScrollListener l) {
        this.mOnScrollListener = l;
        invokeOnItemScrollListener();
    }

    void invokeOnItemScrollListener() {
        if (this.mFastScroll != null) {
            this.mFastScroll.onScroll(this.mFirstPosition, getChildCount(), this.mItemCount);
        }
        if (this.mOnScrollListener != null) {
            this.mOnScrollListener.onScroll(this, this.mFirstPosition, getChildCount(), this.mItemCount);
        }
        onScrollChanged(TRANSCRIPT_MODE_DISABLED, TRANSCRIPT_MODE_DISABLED, TRANSCRIPT_MODE_DISABLED, TRANSCRIPT_MODE_DISABLED);
    }

    public void sendAccessibilityEvent(int eventType) {
        if (eventType == AccessibilityNodeInfo.ACTION_SCROLL_FORWARD) {
            int firstVisiblePosition = getFirstVisiblePosition();
            int lastVisiblePosition = getLastVisiblePosition();
            if (this.mLastAccessibilityScrollEventFromIndex != firstVisiblePosition || this.mLastAccessibilityScrollEventToIndex != lastVisiblePosition) {
                this.mLastAccessibilityScrollEventFromIndex = firstVisiblePosition;
                this.mLastAccessibilityScrollEventToIndex = lastVisiblePosition;
            } else {
                return;
            }
        }
        super.sendAccessibilityEvent(eventType);
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(AbsListView.class.getName());
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(AbsListView.class.getName());
        if (isEnabled()) {
            if (canScrollUp()) {
                info.addAction((int) AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
                info.setScrollable(true);
            }
            if (canScrollDown()) {
                info.addAction((int) AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                info.setScrollable(true);
            }
        }
    }

    int getSelectionModeForAccessibility() {
        switch (getChoiceMode()) {
            case TRANSCRIPT_MODE_NORMAL /*1*/:
                return TRANSCRIPT_MODE_NORMAL;
            case TRANSCRIPT_MODE_ALWAYS_SCROLL /*2*/:
            case TOUCH_MODE_SCROLL /*3*/:
                return TRANSCRIPT_MODE_ALWAYS_SCROLL;
            default:
                return TRANSCRIPT_MODE_DISABLED;
        }
    }

    public boolean performAccessibilityAction(int action, Bundle arguments) {
        if (super.performAccessibilityAction(action, arguments)) {
            return true;
        }
        switch (action) {
            case AccessibilityNodeInfo.ACTION_SCROLL_FORWARD /*4096*/:
                if (!isEnabled() || getLastVisiblePosition() >= getCount() + TOUCH_MODE_UNKNOWN) {
                    return PROFILE_SCROLLING;
                }
                smoothScrollBy((getHeight() - this.mListPadding.top) - this.mListPadding.bottom, KeyEvent.KEYCODE_BUTTON_13);
                return true;
            case AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD /*8192*/:
                if (!isEnabled() || this.mFirstPosition <= 0) {
                    return PROFILE_SCROLLING;
                }
                smoothScrollBy(-((getHeight() - this.mListPadding.top) - this.mListPadding.bottom), KeyEvent.KEYCODE_BUTTON_13);
                return true;
            default:
                return PROFILE_SCROLLING;
        }
    }

    public View findViewByAccessibilityIdTraversal(int accessibilityId) {
        if (accessibilityId == getAccessibilityViewId()) {
            return this;
        }
        if (this.mDataChanged) {
            return null;
        }
        return super.findViewByAccessibilityIdTraversal(accessibilityId);
    }

    @ExportedProperty
    public boolean isScrollingCacheEnabled() {
        return this.mScrollingCacheEnabled;
    }

    public void setScrollingCacheEnabled(boolean enabled) {
        if (this.mScrollingCacheEnabled && !enabled) {
            clearScrollingCache();
        }
        this.mScrollingCacheEnabled = enabled;
    }

    public void setTextFilterEnabled(boolean textFilterEnabled) {
        this.mTextFilterEnabled = textFilterEnabled;
    }

    @ExportedProperty
    public boolean isTextFilterEnabled() {
        return this.mTextFilterEnabled;
    }

    public void getFocusedRect(Rect r) {
        View view = getSelectedView();
        if (view == null || view.getParent() != this) {
            super.getFocusedRect(r);
            return;
        }
        view.getFocusedRect(r);
        offsetDescendantRectToMyCoords(view, r);
    }

    private void useDefaultSelector() {
        setSelector(getContext().getDrawable(android.R.drawable.list_selector_background));
    }

    @ExportedProperty
    public boolean isStackFromBottom() {
        return this.mStackFromBottom;
    }

    public void setStackFromBottom(boolean stackFromBottom) {
        if (this.mStackFromBottom != stackFromBottom) {
            this.mStackFromBottom = stackFromBottom;
            requestLayoutIfNecessary();
        }
    }

    void requestLayoutIfNecessary() {
        if (getChildCount() > 0) {
            resetList();
            requestLayout();
            invalidate();
        }
    }

    public Parcelable onSaveInstanceState() {
        dismissPopup();
        SavedState ss = new SavedState(super.onSaveInstanceState());
        if (this.mPendingSync != null) {
            ss.selectedId = this.mPendingSync.selectedId;
            ss.firstId = this.mPendingSync.firstId;
            ss.viewTop = this.mPendingSync.viewTop;
            ss.position = this.mPendingSync.position;
            ss.height = this.mPendingSync.height;
            ss.filter = this.mPendingSync.filter;
            ss.inActionMode = this.mPendingSync.inActionMode;
            ss.checkedItemCount = this.mPendingSync.checkedItemCount;
            ss.checkState = this.mPendingSync.checkState;
            ss.checkIdState = this.mPendingSync.checkIdState;
        } else {
            boolean haveChildren = (getChildCount() <= 0 || this.mItemCount <= 0) ? PROFILE_SCROLLING : true;
            long selectedId = getSelectedItemId();
            ss.selectedId = selectedId;
            ss.height = getHeight();
            if (selectedId >= 0) {
                ss.viewTop = this.mSelectedTop;
                ss.position = getSelectedItemPosition();
                ss.firstId = -1;
            } else if (!haveChildren || this.mFirstPosition <= 0) {
                ss.viewTop = TRANSCRIPT_MODE_DISABLED;
                ss.firstId = -1;
                ss.position = TRANSCRIPT_MODE_DISABLED;
            } else {
                ss.viewTop = getChildAt(TRANSCRIPT_MODE_DISABLED).getTop();
                int firstPos = this.mFirstPosition;
                if (firstPos >= this.mItemCount) {
                    firstPos = this.mItemCount + TOUCH_MODE_UNKNOWN;
                }
                ss.position = firstPos;
                ss.firstId = this.mAdapter.getItemId(firstPos);
            }
            ss.filter = null;
            if (this.mFiltered) {
                EditText textFilter = this.mTextFilter;
                if (textFilter != null) {
                    Editable filterText = textFilter.getText();
                    if (filterText != null) {
                        ss.filter = filterText.toString();
                    }
                }
            }
            boolean z = (this.mChoiceMode != TOUCH_MODE_SCROLL || this.mChoiceActionMode == null) ? PROFILE_SCROLLING : true;
            ss.inActionMode = z;
            if (this.mCheckStates != null) {
                ss.checkState = this.mCheckStates.clone();
            }
            if (this.mCheckedIdStates != null) {
                LongSparseArray<Integer> idState = new LongSparseArray();
                int count = this.mCheckedIdStates.size();
                for (int i = TRANSCRIPT_MODE_DISABLED; i < count; i += TRANSCRIPT_MODE_NORMAL) {
                    idState.put(this.mCheckedIdStates.keyAt(i), this.mCheckedIdStates.valueAt(i));
                }
                ss.checkIdState = idState;
            }
            ss.checkedItemCount = this.mCheckedItemCount;
            if (this.mRemoteAdapter != null) {
                this.mRemoteAdapter.saveRemoteViewsCache();
            }
        }
        return ss;
    }

    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.mDataChanged = true;
        this.mSyncHeight = (long) ss.height;
        if (ss.selectedId >= 0) {
            this.mNeedSync = true;
            this.mPendingSync = ss;
            this.mSyncRowId = ss.selectedId;
            this.mSyncPosition = ss.position;
            this.mSpecificTop = ss.viewTop;
            this.mSyncMode = TRANSCRIPT_MODE_DISABLED;
        } else if (ss.firstId >= 0) {
            setSelectedPositionInt(TOUCH_MODE_UNKNOWN);
            setNextSelectedPositionInt(TOUCH_MODE_UNKNOWN);
            this.mSelectorPosition = TOUCH_MODE_UNKNOWN;
            this.mNeedSync = true;
            this.mPendingSync = ss;
            this.mSyncRowId = ss.firstId;
            this.mSyncPosition = ss.position;
            this.mSpecificTop = ss.viewTop;
            this.mSyncMode = TRANSCRIPT_MODE_NORMAL;
        }
        setFilterText(ss.filter);
        if (ss.checkState != null) {
            this.mCheckStates = ss.checkState;
        }
        if (ss.checkIdState != null) {
            this.mCheckedIdStates = ss.checkIdState;
        }
        this.mCheckedItemCount = ss.checkedItemCount;
        if (ss.inActionMode && this.mChoiceMode == TOUCH_MODE_SCROLL && this.mMultiChoiceModeCallback != null) {
            this.mChoiceActionMode = startActionMode(this.mMultiChoiceModeCallback);
        }
        requestLayout();
    }

    private boolean acceptFilter() {
        return (this.mTextFilterEnabled && (getAdapter() instanceof Filterable) && ((Filterable) getAdapter()).getFilter() != null) ? true : PROFILE_SCROLLING;
    }

    public void setFilterText(String filterText) {
        if (this.mTextFilterEnabled && !TextUtils.isEmpty(filterText)) {
            createTextFilter(PROFILE_SCROLLING);
            this.mTextFilter.setText((CharSequence) filterText);
            this.mTextFilter.setSelection(filterText.length());
            if (this.mAdapter instanceof Filterable) {
                if (this.mPopup == null) {
                    ((Filterable) this.mAdapter).getFilter().filter(filterText);
                }
                this.mFiltered = true;
                this.mDataSetObserver.clearSavedState();
            }
        }
    }

    public CharSequence getTextFilter() {
        if (!this.mTextFilterEnabled || this.mTextFilter == null) {
            return null;
        }
        return this.mTextFilter.getText();
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus && this.mSelectedPosition < 0 && !isInTouchMode()) {
            if (!(isAttachedToWindow() || this.mAdapter == null)) {
                this.mDataChanged = true;
                this.mOldItemCount = this.mItemCount;
                this.mItemCount = this.mAdapter.getCount();
            }
            resurrectSelection();
        }
    }

    public void requestLayout() {
        if (!this.mBlockLayoutRequests && !this.mInLayout) {
            super.requestLayout();
        }
    }

    void resetList() {
        removeAllViewsInLayout();
        this.mFirstPosition = TRANSCRIPT_MODE_DISABLED;
        this.mDataChanged = PROFILE_SCROLLING;
        this.mPositionScrollAfterLayout = null;
        this.mNeedSync = PROFILE_SCROLLING;
        this.mPendingSync = null;
        this.mOldSelectedPosition = TOUCH_MODE_UNKNOWN;
        this.mOldSelectedRowId = Long.MIN_VALUE;
        setSelectedPositionInt(TOUCH_MODE_UNKNOWN);
        setNextSelectedPositionInt(TOUCH_MODE_UNKNOWN);
        this.mSelectedTop = TRANSCRIPT_MODE_DISABLED;
        this.mSelectorPosition = TOUCH_MODE_UNKNOWN;
        this.mSelectorRect.setEmpty();
        invalidate();
    }

    protected int computeVerticalScrollExtent() {
        int count = getChildCount();
        if (count <= 0) {
            return TRANSCRIPT_MODE_DISABLED;
        }
        if (!this.mSmoothScrollbarEnabled) {
            return TRANSCRIPT_MODE_NORMAL;
        }
        int extent = count * 100;
        View view = getChildAt(TRANSCRIPT_MODE_DISABLED);
        int top = view.getTop();
        int height = view.getHeight();
        if (height > 0) {
            extent += (top * 100) / height;
        }
        view = getChildAt(count + TOUCH_MODE_UNKNOWN);
        int bottom = view.getBottom();
        height = view.getHeight();
        if (height > 0) {
            return extent - (((bottom - getHeight()) * 100) / height);
        }
        return extent;
    }

    protected int computeVerticalScrollOffset() {
        int firstPosition = this.mFirstPosition;
        int childCount = getChildCount();
        if (firstPosition < 0 || childCount <= 0) {
            return TRANSCRIPT_MODE_DISABLED;
        }
        if (this.mSmoothScrollbarEnabled) {
            View view = getChildAt(TRANSCRIPT_MODE_DISABLED);
            int top = view.getTop();
            int height = view.getHeight();
            if (height > 0) {
                return Math.max(((firstPosition * 100) - ((top * 100) / height)) + ((int) (((((float) this.mScrollY) / ((float) getHeight())) * ((float) this.mItemCount)) * SensorManager.LIGHT_CLOUDY)), TRANSCRIPT_MODE_DISABLED);
            }
            return TRANSCRIPT_MODE_DISABLED;
        }
        int index;
        int count = this.mItemCount;
        if (firstPosition == 0) {
            index = TRANSCRIPT_MODE_DISABLED;
        } else if (firstPosition + childCount == count) {
            index = count;
        } else {
            index = firstPosition + (childCount / TRANSCRIPT_MODE_ALWAYS_SCROLL);
        }
        return (int) (((float) firstPosition) + (((float) childCount) * (((float) index) / ((float) count))));
    }

    protected int computeVerticalScrollRange() {
        if (!this.mSmoothScrollbarEnabled) {
            return this.mItemCount;
        }
        int result = Math.max(this.mItemCount * 100, TRANSCRIPT_MODE_DISABLED);
        if (this.mScrollY != 0) {
            return result + Math.abs((int) (((((float) this.mScrollY) / ((float) getHeight())) * ((float) this.mItemCount)) * SensorManager.LIGHT_CLOUDY));
        }
        return result;
    }

    protected float getTopFadingEdgeStrength() {
        int count = getChildCount();
        float fadeEdge = super.getTopFadingEdgeStrength();
        if (count == 0) {
            return fadeEdge;
        }
        if (this.mFirstPosition > 0) {
            return android.view.WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        }
        int top = getChildAt(TRANSCRIPT_MODE_DISABLED).getTop();
        return top < this.mPaddingTop ? ((float) (-(top - this.mPaddingTop))) / ((float) getVerticalFadingEdgeLength()) : fadeEdge;
    }

    protected float getBottomFadingEdgeStrength() {
        int count = getChildCount();
        float fadeEdge = super.getBottomFadingEdgeStrength();
        if (count == 0) {
            return fadeEdge;
        }
        if ((this.mFirstPosition + count) + TOUCH_MODE_UNKNOWN < this.mItemCount + TOUCH_MODE_UNKNOWN) {
            return android.view.WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        }
        int bottom = getChildAt(count + TOUCH_MODE_UNKNOWN).getBottom();
        int height = getHeight();
        return bottom > height - this.mPaddingBottom ? ((float) ((bottom - height) + this.mPaddingBottom)) / ((float) getVerticalFadingEdgeLength()) : fadeEdge;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean z = true;
        if (this.mSelector == null) {
            useDefaultSelector();
        }
        Rect listPadding = this.mListPadding;
        listPadding.left = this.mSelectionLeftPadding + this.mPaddingLeft;
        listPadding.top = this.mSelectionTopPadding + this.mPaddingTop;
        listPadding.right = this.mSelectionRightPadding + this.mPaddingRight;
        listPadding.bottom = this.mSelectionBottomPadding + this.mPaddingBottom;
        if (this.mTranscriptMode == TRANSCRIPT_MODE_NORMAL) {
            int childCount = getChildCount();
            int listBottom = getHeight() - getPaddingBottom();
            View lastChild = getChildAt(childCount + TOUCH_MODE_UNKNOWN);
            int lastBottom;
            if (lastChild != null) {
                lastBottom = lastChild.getBottom();
            } else {
                lastBottom = listBottom;
            }
            if (this.mFirstPosition + childCount < this.mLastHandledItemCount || lastBottom > listBottom) {
                z = PROFILE_SCROLLING;
            }
            this.mForceTranscriptScroll = z;
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.mInLayout = true;
        int childCount = getChildCount();
        if (changed) {
            for (int i = TRANSCRIPT_MODE_DISABLED; i < childCount; i += TRANSCRIPT_MODE_NORMAL) {
                getChildAt(i).forceLayout();
            }
            this.mRecycler.markChildrenDirty();
        }
        layoutChildren();
        this.mInLayout = PROFILE_SCROLLING;
        this.mOverscrollMax = (b - t) / TOUCH_MODE_SCROLL;
        if (this.mFastScroll != null) {
            this.mFastScroll.onItemCountChanged(getChildCount(), this.mItemCount);
        }
    }

    protected boolean setFrame(int left, int top, int right, int bottom) {
        boolean changed = super.setFrame(left, top, right, bottom);
        if (changed) {
            boolean visible = getWindowVisibility() == 0 ? true : PROFILE_SCROLLING;
            if (this.mFiltered && visible && this.mPopup != null && this.mPopup.isShowing()) {
                positionPopup();
            }
        }
        return changed;
    }

    protected void layoutChildren() {
    }

    View getAccessibilityFocusedChild(View focusedView) {
        View viewParent = focusedView.getParent();
        while ((viewParent instanceof View) && viewParent != this) {
            focusedView = viewParent;
            viewParent = viewParent.getParent();
        }
        if (viewParent instanceof View) {
            return focusedView;
        }
        return null;
    }

    void updateScrollIndicators() {
        int i = TRANSCRIPT_MODE_DISABLED;
        if (this.mScrollUp != null) {
            this.mScrollUp.setVisibility(canScrollUp() ? TRANSCRIPT_MODE_DISABLED : TOUCH_MODE_FLING);
        }
        if (this.mScrollDown != null) {
            View view = this.mScrollDown;
            if (!canScrollDown()) {
                i = TOUCH_MODE_FLING;
            }
            view.setVisibility(i);
        }
    }

    private boolean canScrollUp() {
        boolean canScrollUp;
        if (this.mFirstPosition > 0) {
            canScrollUp = true;
        } else {
            canScrollUp = PROFILE_SCROLLING;
        }
        if (canScrollUp || getChildCount() <= 0) {
            return canScrollUp;
        }
        if (getChildAt(TRANSCRIPT_MODE_DISABLED).getTop() < this.mListPadding.top) {
            return true;
        }
        return PROFILE_SCROLLING;
    }

    private boolean canScrollDown() {
        boolean canScrollDown;
        int count = getChildCount();
        if (this.mFirstPosition + count < this.mItemCount) {
            canScrollDown = true;
        } else {
            canScrollDown = PROFILE_SCROLLING;
        }
        if (canScrollDown || count <= 0) {
            return canScrollDown;
        }
        if (getChildAt(count + TOUCH_MODE_UNKNOWN).getBottom() > this.mBottom - this.mListPadding.bottom) {
            return true;
        }
        return PROFILE_SCROLLING;
    }

    @ExportedProperty
    public View getSelectedView() {
        if (this.mItemCount <= 0 || this.mSelectedPosition < 0) {
            return null;
        }
        return getChildAt(this.mSelectedPosition - this.mFirstPosition);
    }

    public int getListPaddingTop() {
        return this.mListPadding.top;
    }

    public int getListPaddingBottom() {
        return this.mListPadding.bottom;
    }

    public int getListPaddingLeft() {
        return this.mListPadding.left;
    }

    public int getListPaddingRight() {
        return this.mListPadding.right;
    }

    View obtainView(int position, boolean[] isScrap) {
        Trace.traceBegin(8, "obtainView");
        isScrap[TRANSCRIPT_MODE_DISABLED] = PROFILE_SCROLLING;
        View transientView = this.mRecycler.getTransientStateView(position);
        if (transientView != null) {
            if (((LayoutParams) transientView.getLayoutParams()).viewType == this.mAdapter.getItemViewType(position)) {
                View updatedView = this.mAdapter.getView(position, transientView, this);
                if (updatedView != transientView) {
                    setItemViewLayoutParams(updatedView, position);
                    this.mRecycler.addScrapView(updatedView, position);
                }
            }
            isScrap[TRANSCRIPT_MODE_DISABLED] = true;
            return transientView;
        }
        View scrapView = this.mRecycler.getScrapView(position);
        View child = this.mAdapter.getView(position, scrapView, this);
        if (scrapView != null) {
            if (child != scrapView) {
                this.mRecycler.addScrapView(scrapView, position);
            } else {
                isScrap[TRANSCRIPT_MODE_DISABLED] = true;
                child.dispatchFinishTemporaryDetach();
            }
        }
        if (this.mCacheColorHint != 0) {
            child.setDrawingCacheBackgroundColor(this.mCacheColorHint);
        }
        if (child.getImportantForAccessibility() == 0) {
            child.setImportantForAccessibility(TRANSCRIPT_MODE_NORMAL);
        }
        setItemViewLayoutParams(child, position);
        if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            if (this.mAccessibilityDelegate == null) {
                this.mAccessibilityDelegate = new ListItemAccessibilityDelegate(this);
            }
            if (child.getAccessibilityDelegate() == null) {
                child.setAccessibilityDelegate(this.mAccessibilityDelegate);
            }
        }
        Trace.traceEnd(8);
        return child;
    }

    private void setItemViewLayoutParams(View child, int position) {
        LayoutParams lp;
        android.view.ViewGroup.LayoutParams vlp = child.getLayoutParams();
        if (vlp == null) {
            lp = (LayoutParams) generateDefaultLayoutParams();
        } else if (checkLayoutParams(vlp)) {
            lp = (LayoutParams) vlp;
        } else {
            lp = (LayoutParams) generateLayoutParams(vlp);
        }
        if (this.mAdapterHasStableIds) {
            lp.itemId = this.mAdapter.getItemId(position);
        }
        lp.viewType = this.mAdapter.getItemViewType(position);
        child.setLayoutParams(lp);
    }

    public void onInitializeAccessibilityNodeInfoForItem(View view, int position, AccessibilityNodeInfo info) {
        ListAdapter adapter = (ListAdapter) getAdapter();
        if (position != TOUCH_MODE_UNKNOWN && adapter != null) {
            if (isEnabled() && adapter.isEnabled(position)) {
                if (position == getSelectedItemPosition()) {
                    info.setSelected(true);
                    info.addAction(8);
                } else {
                    info.addAction((int) TOUCH_MODE_FLING);
                }
                if (isClickable()) {
                    info.addAction(16);
                    info.setClickable(true);
                }
                if (isLongClickable()) {
                    info.addAction(32);
                    info.setLongClickable(true);
                    return;
                }
                return;
            }
            info.setEnabled(PROFILE_SCROLLING);
        }
    }

    void positionSelectorLikeTouch(int position, View sel, float x, float y) {
        positionSelector(position, sel, true, x, y);
    }

    void positionSelectorLikeFocus(int position, View sel) {
        if (this.mSelector == null || this.mSelectorPosition == position || position == TOUCH_MODE_UNKNOWN) {
            positionSelector(position, sel);
            return;
        }
        Rect bounds = this.mSelectorRect;
        positionSelector(position, sel, true, bounds.exactCenterX(), bounds.exactCenterY());
    }

    void positionSelector(int position, View sel) {
        positionSelector(position, sel, PROFILE_SCROLLING, android.view.WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE, android.view.WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE);
    }

    private void positionSelector(int position, View sel, boolean manageHotspot, float x, float y) {
        boolean positionChanged = position != this.mSelectorPosition ? true : PROFILE_SCROLLING;
        if (position != TOUCH_MODE_UNKNOWN) {
            this.mSelectorPosition = position;
        }
        Rect selectorRect = this.mSelectorRect;
        selectorRect.set(sel.getLeft(), sel.getTop(), sel.getRight(), sel.getBottom());
        if (sel instanceof SelectionBoundsAdjuster) {
            ((SelectionBoundsAdjuster) sel).adjustListItemSelectionBounds(selectorRect);
        }
        selectorRect.left -= this.mSelectionLeftPadding;
        selectorRect.top -= this.mSelectionTopPadding;
        selectorRect.right += this.mSelectionRightPadding;
        selectorRect.bottom += this.mSelectionBottomPadding;
        Drawable selector = this.mSelector;
        if (selector != null) {
            if (positionChanged) {
                selector.setVisible(PROFILE_SCROLLING, PROFILE_SCROLLING);
                selector.setState(StateSet.NOTHING);
            }
            selector.setBounds(selectorRect);
            if (positionChanged) {
                if (getVisibility() == 0) {
                    selector.setVisible(true, PROFILE_SCROLLING);
                }
                updateSelectorState();
            }
            if (manageHotspot) {
                selector.setHotspot(x, y);
            }
        }
        boolean isChildViewEnabled = this.mIsChildViewEnabled;
        if (sel.isEnabled() != isChildViewEnabled) {
            this.mIsChildViewEnabled = !isChildViewEnabled ? true : PROFILE_SCROLLING;
            if (getSelectedItemPosition() != TOUCH_MODE_UNKNOWN) {
                refreshDrawableState();
            }
        }
    }

    protected void dispatchDraw(Canvas canvas) {
        int saveCount = TRANSCRIPT_MODE_DISABLED;
        boolean clipToPadding = (this.mGroupFlags & 34) == 34 ? true : PROFILE_SCROLLING;
        if (clipToPadding) {
            saveCount = canvas.save();
            int scrollX = this.mScrollX;
            int scrollY = this.mScrollY;
            canvas.clipRect(this.mPaddingLeft + scrollX, this.mPaddingTop + scrollY, ((this.mRight + scrollX) - this.mLeft) - this.mPaddingRight, ((this.mBottom + scrollY) - this.mTop) - this.mPaddingBottom);
            this.mGroupFlags &= -35;
        }
        boolean drawSelectorOnTop = this.mDrawSelectorOnTop;
        if (!drawSelectorOnTop) {
            drawSelector(canvas);
        }
        super.dispatchDraw(canvas);
        if (drawSelectorOnTop) {
            drawSelector(canvas);
        }
        if (clipToPadding) {
            canvas.restoreToCount(saveCount);
            this.mGroupFlags |= 34;
        }
    }

    protected boolean isPaddingOffsetRequired() {
        return (this.mGroupFlags & 34) != 34 ? true : PROFILE_SCROLLING;
    }

    protected int getLeftPaddingOffset() {
        return (this.mGroupFlags & 34) == 34 ? TRANSCRIPT_MODE_DISABLED : -this.mPaddingLeft;
    }

    protected int getTopPaddingOffset() {
        return (this.mGroupFlags & 34) == 34 ? TRANSCRIPT_MODE_DISABLED : -this.mPaddingTop;
    }

    protected int getRightPaddingOffset() {
        return (this.mGroupFlags & 34) == 34 ? TRANSCRIPT_MODE_DISABLED : this.mPaddingRight;
    }

    protected int getBottomPaddingOffset() {
        return (this.mGroupFlags & 34) == 34 ? TRANSCRIPT_MODE_DISABLED : this.mPaddingBottom;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (getChildCount() > 0) {
            this.mDataChanged = true;
            rememberSyncState();
        }
        if (this.mFastScroll != null) {
            this.mFastScroll.onSizeChanged(w, h, oldw, oldh);
        }
    }

    boolean touchModeDrawsInPressedState() {
        switch (this.mTouchMode) {
            case TRANSCRIPT_MODE_NORMAL /*1*/:
            case TRANSCRIPT_MODE_ALWAYS_SCROLL /*2*/:
                return true;
            default:
                return PROFILE_SCROLLING;
        }
    }

    boolean shouldShowSelector() {
        return ((!isFocused() || isInTouchMode()) && !(touchModeDrawsInPressedState() && isPressed())) ? PROFILE_SCROLLING : true;
    }

    private void drawSelector(Canvas canvas) {
        if (!this.mSelectorRect.isEmpty()) {
            Drawable selector = this.mSelector;
            selector.setBounds(this.mSelectorRect);
            selector.draw(canvas);
        }
    }

    public void setDrawSelectorOnTop(boolean onTop) {
        this.mDrawSelectorOnTop = onTop;
    }

    public void setSelector(int resID) {
        setSelector(getContext().getDrawable(resID));
    }

    public void setSelector(Drawable sel) {
        if (this.mSelector != null) {
            this.mSelector.setCallback(null);
            unscheduleDrawable(this.mSelector);
        }
        this.mSelector = sel;
        Rect padding = new Rect();
        sel.getPadding(padding);
        this.mSelectionLeftPadding = padding.left;
        this.mSelectionTopPadding = padding.top;
        this.mSelectionRightPadding = padding.right;
        this.mSelectionBottomPadding = padding.bottom;
        sel.setCallback(this);
        updateSelectorState();
    }

    public Drawable getSelector() {
        return this.mSelector;
    }

    void keyPressed() {
        if (isEnabled() && isClickable()) {
            Drawable selector = this.mSelector;
            Rect selectorRect = this.mSelectorRect;
            if (selector == null) {
                return;
            }
            if ((isFocused() || touchModeDrawsInPressedState()) && !selectorRect.isEmpty()) {
                View v = getChildAt(this.mSelectedPosition - this.mFirstPosition);
                if (v != null) {
                    if (!v.hasFocusable()) {
                        v.setPressed(true);
                    } else {
                        return;
                    }
                }
                setPressed(true);
                boolean longClickable = isLongClickable();
                Drawable d = selector.getCurrent();
                if (d != null && (d instanceof TransitionDrawable)) {
                    if (longClickable) {
                        ((TransitionDrawable) d).startTransition(ViewConfiguration.getLongPressTimeout());
                    } else {
                        ((TransitionDrawable) d).resetTransition();
                    }
                }
                if (longClickable && !this.mDataChanged) {
                    if (this.mPendingCheckForKeyLongPress == null) {
                        this.mPendingCheckForKeyLongPress = new CheckForKeyLongPress();
                    }
                    this.mPendingCheckForKeyLongPress.rememberWindowAttachCount();
                    postDelayed(this.mPendingCheckForKeyLongPress, (long) ViewConfiguration.getLongPressTimeout());
                }
            }
        }
    }

    public void setScrollIndicators(View up, View down) {
        this.mScrollUp = up;
        this.mScrollDown = down;
    }

    void updateSelectorState() {
        if (this.mSelector == null) {
            return;
        }
        if (shouldShowSelector()) {
            this.mSelector.setState(getDrawableState());
        } else {
            this.mSelector.setState(StateSet.NOTHING);
        }
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        updateSelectorState();
    }

    protected int[] onCreateDrawableState(int extraSpace) {
        if (this.mIsChildViewEnabled) {
            return super.onCreateDrawableState(extraSpace);
        }
        int enabledState = ENABLED_STATE_SET[TRANSCRIPT_MODE_DISABLED];
        int[] state = super.onCreateDrawableState(extraSpace + TRANSCRIPT_MODE_NORMAL);
        int enabledPos = TOUCH_MODE_UNKNOWN;
        for (int i = state.length + TOUCH_MODE_UNKNOWN; i >= 0; i += TOUCH_MODE_UNKNOWN) {
            if (state[i] == enabledState) {
                enabledPos = i;
                break;
            }
        }
        if (enabledPos < 0) {
            return state;
        }
        System.arraycopy(state, enabledPos + TRANSCRIPT_MODE_NORMAL, state, enabledPos, (state.length - enabledPos) + TOUCH_MODE_UNKNOWN);
        return state;
    }

    public boolean verifyDrawable(Drawable dr) {
        return (this.mSelector == dr || super.verifyDrawable(dr)) ? true : PROFILE_SCROLLING;
    }

    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (this.mSelector != null) {
            this.mSelector.jumpToCurrentState();
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewTreeObserver treeObserver = getViewTreeObserver();
        treeObserver.addOnTouchModeChangeListener(this);
        if (!(!this.mTextFilterEnabled || this.mPopup == null || this.mGlobalLayoutListenerAddedFilter)) {
            treeObserver.addOnGlobalLayoutListener(this);
        }
        if (this.mAdapter != null && this.mDataSetObserver == null) {
            this.mDataSetObserver = new AdapterDataSetObserver(this);
            this.mAdapter.registerDataSetObserver(this.mDataSetObserver);
            this.mDataChanged = true;
            this.mOldItemCount = this.mItemCount;
            this.mItemCount = this.mAdapter.getCount();
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mIsDetaching = true;
        dismissPopup();
        this.mRecycler.clear();
        ViewTreeObserver treeObserver = getViewTreeObserver();
        treeObserver.removeOnTouchModeChangeListener(this);
        if (this.mTextFilterEnabled && this.mPopup != null) {
            treeObserver.removeOnGlobalLayoutListener(this);
            this.mGlobalLayoutListenerAddedFilter = PROFILE_SCROLLING;
        }
        if (!(this.mAdapter == null || this.mDataSetObserver == null)) {
            this.mAdapter.unregisterDataSetObserver(this.mDataSetObserver);
            this.mDataSetObserver = null;
        }
        if (this.mScrollStrictSpan != null) {
            this.mScrollStrictSpan.finish();
            this.mScrollStrictSpan = null;
        }
        if (this.mFlingStrictSpan != null) {
            this.mFlingStrictSpan.finish();
            this.mFlingStrictSpan = null;
        }
        if (this.mFlingRunnable != null) {
            removeCallbacks(this.mFlingRunnable);
        }
        if (this.mPositionScroller != null) {
            this.mPositionScroller.stop();
        }
        if (this.mClearScrollingCache != null) {
            removeCallbacks(this.mClearScrollingCache);
        }
        if (this.mPerformClick != null) {
            removeCallbacks(this.mPerformClick);
        }
        if (this.mTouchModeReset != null) {
            removeCallbacks(this.mTouchModeReset);
            this.mTouchModeReset.run();
        }
        this.mIsDetaching = PROFILE_SCROLLING;
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        int touchMode;
        super.onWindowFocusChanged(hasWindowFocus);
        if (isInTouchMode()) {
            touchMode = TRANSCRIPT_MODE_DISABLED;
        } else {
            touchMode = TRANSCRIPT_MODE_NORMAL;
        }
        if (hasWindowFocus) {
            if (this.mFiltered && !this.mPopupHidden) {
                showPopup();
            }
            if (!(touchMode == this.mLastTouchMode || this.mLastTouchMode == TOUCH_MODE_UNKNOWN)) {
                if (touchMode == TRANSCRIPT_MODE_NORMAL) {
                    resurrectSelection();
                } else {
                    hideSelector();
                    this.mLayoutMode = TRANSCRIPT_MODE_DISABLED;
                    layoutChildren();
                }
            }
        } else {
            setChildrenDrawingCacheEnabled(PROFILE_SCROLLING);
            if (this.mFlingRunnable != null) {
                removeCallbacks(this.mFlingRunnable);
                this.mFlingRunnable.endFling();
                if (this.mPositionScroller != null) {
                    this.mPositionScroller.stop();
                }
                if (this.mScrollY != 0) {
                    this.mScrollY = TRANSCRIPT_MODE_DISABLED;
                    invalidateParentCaches();
                    finishGlows();
                    invalidate();
                }
            }
            dismissPopup();
            if (touchMode == TRANSCRIPT_MODE_NORMAL) {
                this.mResurrectToPosition = this.mSelectedPosition;
            }
        }
        this.mLastTouchMode = touchMode;
    }

    public void onRtlPropertiesChanged(int layoutDirection) {
        super.onRtlPropertiesChanged(layoutDirection);
        if (this.mFastScroll != null) {
            this.mFastScroll.setScrollbarPosition(getVerticalScrollbarPosition());
        }
    }

    ContextMenuInfo createContextMenuInfo(View view, int position, long id) {
        return new AdapterContextMenuInfo(view, position, id);
    }

    public void onCancelPendingInputEvents() {
        super.onCancelPendingInputEvents();
        if (this.mPerformClick != null) {
            removeCallbacks(this.mPerformClick);
        }
        if (this.mPendingCheckForTap != null) {
            removeCallbacks(this.mPendingCheckForTap);
        }
        if (this.mPendingCheckForLongPress != null) {
            removeCallbacks(this.mPendingCheckForLongPress);
        }
        if (this.mPendingCheckForKeyLongPress != null) {
            removeCallbacks(this.mPendingCheckForKeyLongPress);
        }
    }

    boolean performLongPress(View child, int longPressPosition, long longPressId) {
        boolean z = true;
        if (this.mChoiceMode != TOUCH_MODE_SCROLL) {
            z = PROFILE_SCROLLING;
            if (this.mOnItemLongClickListener != null) {
                z = this.mOnItemLongClickListener.onItemLongClick(this, child, longPressPosition, longPressId);
            }
            if (!z) {
                this.mContextMenuInfo = createContextMenuInfo(child, longPressPosition, longPressId);
                z = super.showContextMenuForChild(this);
            }
            if (z) {
                performHapticFeedback(TRANSCRIPT_MODE_DISABLED);
            }
        } else if (this.mChoiceActionMode == null) {
            ActionMode startActionMode = startActionMode(this.mMultiChoiceModeCallback);
            this.mChoiceActionMode = startActionMode;
            if (startActionMode != null) {
                setItemChecked(longPressPosition, true);
                performHapticFeedback(TRANSCRIPT_MODE_DISABLED);
            }
        }
        return z;
    }

    protected ContextMenuInfo getContextMenuInfo() {
        return this.mContextMenuInfo;
    }

    public boolean showContextMenu(float x, float y, int metaState) {
        int position = pointToPosition((int) x, (int) y);
        if (position != TOUCH_MODE_UNKNOWN) {
            long id = this.mAdapter.getItemId(position);
            View child = getChildAt(position - this.mFirstPosition);
            if (child != null) {
                this.mContextMenuInfo = createContextMenuInfo(child, position, id);
                return super.showContextMenuForChild(this);
            }
        }
        return super.showContextMenu(x, y, metaState);
    }

    public boolean showContextMenuForChild(View originalView) {
        int longPressPosition = getPositionForView(originalView);
        if (longPressPosition < 0) {
            return PROFILE_SCROLLING;
        }
        long longPressId = this.mAdapter.getItemId(longPressPosition);
        boolean handled = PROFILE_SCROLLING;
        if (this.mOnItemLongClickListener != null) {
            handled = this.mOnItemLongClickListener.onItemLongClick(this, originalView, longPressPosition, longPressId);
        }
        if (handled) {
            return handled;
        }
        this.mContextMenuInfo = createContextMenuInfo(getChildAt(longPressPosition - this.mFirstPosition), longPressPosition, longPressId);
        return super.showContextMenuForChild(originalView);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return PROFILE_SCROLLING;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (KeyEvent.isConfirmKey(keyCode)) {
            if (!isEnabled()) {
                return true;
            }
            if (isClickable() && isPressed() && this.mSelectedPosition >= 0 && this.mAdapter != null && this.mSelectedPosition < this.mAdapter.getCount()) {
                View view = getChildAt(this.mSelectedPosition - this.mFirstPosition);
                if (view != null) {
                    performItemClick(view, this.mSelectedPosition, this.mSelectedRowId);
                    view.setPressed(PROFILE_SCROLLING);
                }
                setPressed(PROFILE_SCROLLING);
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    protected void dispatchSetPressed(boolean pressed) {
    }

    public void dispatchDrawableHotspotChanged(float x, float y) {
    }

    public int pointToPosition(int x, int y) {
        Rect frame = this.mTouchFrame;
        if (frame == null) {
            this.mTouchFrame = new Rect();
            frame = this.mTouchFrame;
        }
        for (int i = getChildCount() + TOUCH_MODE_UNKNOWN; i >= 0; i += TOUCH_MODE_UNKNOWN) {
            View child = getChildAt(i);
            if (child.getVisibility() == 0) {
                child.getHitRect(frame);
                if (frame.contains(x, y)) {
                    return this.mFirstPosition + i;
                }
            }
        }
        return TOUCH_MODE_UNKNOWN;
    }

    public long pointToRowId(int x, int y) {
        int position = pointToPosition(x, y);
        if (position >= 0) {
            return this.mAdapter.getItemId(position);
        }
        return Long.MIN_VALUE;
    }

    private boolean startScrollIfNeeded(int x, int y, MotionEvent vtev) {
        boolean overscroll;
        int deltaY = y - this.mMotionY;
        int distance = Math.abs(deltaY);
        if (this.mScrollY != 0) {
            overscroll = true;
        } else {
            overscroll = PROFILE_SCROLLING;
        }
        if ((!overscroll && distance <= this.mTouchSlop) || (getNestedScrollAxes() & TRANSCRIPT_MODE_ALWAYS_SCROLL) != 0) {
            return PROFILE_SCROLLING;
        }
        createScrollingCache();
        if (overscroll) {
            this.mTouchMode = TOUCH_MODE_OVERSCROLL;
            this.mMotionCorrection = TRANSCRIPT_MODE_DISABLED;
        } else {
            this.mTouchMode = TOUCH_MODE_SCROLL;
            this.mMotionCorrection = deltaY > 0 ? this.mTouchSlop : -this.mTouchSlop;
        }
        removeCallbacks(this.mPendingCheckForLongPress);
        setPressed(PROFILE_SCROLLING);
        View motionView = getChildAt(this.mMotionPosition - this.mFirstPosition);
        if (motionView != null) {
            motionView.setPressed(PROFILE_SCROLLING);
        }
        reportScrollStateChange(TRANSCRIPT_MODE_NORMAL);
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
        scrollIfNeeded(x, y, vtev);
        return true;
    }

    private void scrollIfNeeded(int x, int y, MotionEvent vtev) {
        int incrementalDeltaY;
        int rawDeltaY = y - this.mMotionY;
        int scrollOffsetCorrection = TRANSCRIPT_MODE_DISABLED;
        int scrollConsumedCorrection = TRANSCRIPT_MODE_DISABLED;
        if (this.mLastY == RtlSpacingHelper.UNDEFINED) {
            rawDeltaY -= this.mMotionCorrection;
        }
        if (dispatchNestedPreScroll(TRANSCRIPT_MODE_DISABLED, this.mLastY != RtlSpacingHelper.UNDEFINED ? this.mLastY - y : -rawDeltaY, this.mScrollConsumed, this.mScrollOffset)) {
            rawDeltaY += this.mScrollConsumed[TRANSCRIPT_MODE_NORMAL];
            scrollOffsetCorrection = -this.mScrollOffset[TRANSCRIPT_MODE_NORMAL];
            scrollConsumedCorrection = this.mScrollConsumed[TRANSCRIPT_MODE_NORMAL];
            if (vtev != null) {
                vtev.offsetLocation(0.0f, (float) this.mScrollOffset[TRANSCRIPT_MODE_NORMAL]);
                this.mNestedYOffset += this.mScrollOffset[TRANSCRIPT_MODE_NORMAL];
            }
        }
        int deltaY = rawDeltaY;
        if (this.mLastY != RtlSpacingHelper.UNDEFINED) {
            incrementalDeltaY = (y - this.mLastY) + scrollConsumedCorrection;
        } else {
            incrementalDeltaY = deltaY;
        }
        int lastYCorrection = TRANSCRIPT_MODE_DISABLED;
        View motionView;
        int overscrollMode;
        if (this.mTouchMode == TOUCH_MODE_SCROLL) {
            if (this.mScrollStrictSpan == null) {
                this.mScrollStrictSpan = StrictMode.enterCriticalSpan("AbsListView-scroll");
            }
            if (y != this.mLastY) {
                int motionIndex;
                if ((this.mGroupFlags & AccessibilityNodeInfo.ACTION_COLLAPSE) == 0 && Math.abs(rawDeltaY) > this.mTouchSlop) {
                    ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }
                if (this.mMotionPosition >= 0) {
                    motionIndex = this.mMotionPosition - this.mFirstPosition;
                } else {
                    motionIndex = getChildCount() / TRANSCRIPT_MODE_ALWAYS_SCROLL;
                }
                int motionViewPrevTop = TRANSCRIPT_MODE_DISABLED;
                motionView = getChildAt(motionIndex);
                if (motionView != null) {
                    motionViewPrevTop = motionView.getTop();
                }
                boolean atEdge = PROFILE_SCROLLING;
                if (incrementalDeltaY != 0) {
                    atEdge = trackMotionScroll(deltaY, incrementalDeltaY);
                }
                motionView = getChildAt(motionIndex);
                if (motionView != null) {
                    int motionViewRealTop = motionView.getTop();
                    if (atEdge) {
                        int overscroll = (-incrementalDeltaY) - (motionViewRealTop - motionViewPrevTop);
                        if (dispatchNestedScroll(TRANSCRIPT_MODE_DISABLED, overscroll - incrementalDeltaY, TRANSCRIPT_MODE_DISABLED, overscroll, this.mScrollOffset)) {
                            lastYCorrection = TRANSCRIPT_MODE_DISABLED - this.mScrollOffset[TRANSCRIPT_MODE_NORMAL];
                            if (vtev != null) {
                                vtev.offsetLocation(0.0f, (float) this.mScrollOffset[TRANSCRIPT_MODE_NORMAL]);
                                this.mNestedYOffset += this.mScrollOffset[TRANSCRIPT_MODE_NORMAL];
                            }
                        } else {
                            boolean atOverscrollEdge = overScrollBy(TRANSCRIPT_MODE_DISABLED, overscroll, TRANSCRIPT_MODE_DISABLED, this.mScrollY, TRANSCRIPT_MODE_DISABLED, TRANSCRIPT_MODE_DISABLED, TRANSCRIPT_MODE_DISABLED, this.mOverscrollDistance, true);
                            if (atOverscrollEdge && this.mVelocityTracker != null) {
                                this.mVelocityTracker.clear();
                            }
                            overscrollMode = getOverScrollMode();
                            if (overscrollMode == 0 || (overscrollMode == TRANSCRIPT_MODE_NORMAL && !contentFits())) {
                                if (!atOverscrollEdge) {
                                    this.mDirection = TRANSCRIPT_MODE_DISABLED;
                                    this.mTouchMode = TOUCH_MODE_OVERSCROLL;
                                }
                                if (incrementalDeltaY > 0) {
                                    this.mEdgeGlowTop.onPull(((float) (-overscroll)) / ((float) getHeight()), ((float) x) / ((float) getWidth()));
                                    if (!this.mEdgeGlowBottom.isFinished()) {
                                        this.mEdgeGlowBottom.onRelease();
                                    }
                                    invalidate(TRANSCRIPT_MODE_DISABLED, TRANSCRIPT_MODE_DISABLED, getWidth(), this.mEdgeGlowTop.getMaxHeight() + getPaddingTop());
                                } else if (incrementalDeltaY < 0) {
                                    this.mEdgeGlowBottom.onPull(((float) overscroll) / ((float) getHeight()), android.view.WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL - (((float) x) / ((float) getWidth())));
                                    if (!this.mEdgeGlowTop.isFinished()) {
                                        this.mEdgeGlowTop.onRelease();
                                    }
                                    invalidate(TRANSCRIPT_MODE_DISABLED, (getHeight() - getPaddingBottom()) - this.mEdgeGlowBottom.getMaxHeight(), getWidth(), getHeight());
                                }
                            }
                        }
                    }
                    this.mMotionY = (y + lastYCorrection) + scrollOffsetCorrection;
                }
                this.mLastY = (y + lastYCorrection) + scrollOffsetCorrection;
            }
        } else if (this.mTouchMode == TOUCH_MODE_OVERSCROLL && y != this.mLastY) {
            int oldScroll = this.mScrollY;
            int newScroll = oldScroll - incrementalDeltaY;
            int newDirection = y > this.mLastY ? TRANSCRIPT_MODE_NORMAL : TOUCH_MODE_UNKNOWN;
            if (this.mDirection == 0) {
                this.mDirection = newDirection;
            }
            int overScrollDistance = -incrementalDeltaY;
            if ((newScroll >= 0 || oldScroll < 0) && (newScroll <= 0 || oldScroll > 0)) {
                incrementalDeltaY = TRANSCRIPT_MODE_DISABLED;
            } else {
                overScrollDistance = -oldScroll;
                incrementalDeltaY += overScrollDistance;
            }
            if (overScrollDistance != 0) {
                overScrollBy(TRANSCRIPT_MODE_DISABLED, overScrollDistance, TRANSCRIPT_MODE_DISABLED, this.mScrollY, TRANSCRIPT_MODE_DISABLED, TRANSCRIPT_MODE_DISABLED, TRANSCRIPT_MODE_DISABLED, this.mOverscrollDistance, true);
                overscrollMode = getOverScrollMode();
                if (overscrollMode == 0 || (overscrollMode == TRANSCRIPT_MODE_NORMAL && !contentFits())) {
                    if (rawDeltaY > 0) {
                        this.mEdgeGlowTop.onPull(((float) overScrollDistance) / ((float) getHeight()), ((float) x) / ((float) getWidth()));
                        if (!this.mEdgeGlowBottom.isFinished()) {
                            this.mEdgeGlowBottom.onRelease();
                        }
                        invalidate(TRANSCRIPT_MODE_DISABLED, TRANSCRIPT_MODE_DISABLED, getWidth(), this.mEdgeGlowTop.getMaxHeight() + getPaddingTop());
                    } else if (rawDeltaY < 0) {
                        this.mEdgeGlowBottom.onPull(((float) overScrollDistance) / ((float) getHeight()), android.view.WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL - (((float) x) / ((float) getWidth())));
                        if (!this.mEdgeGlowTop.isFinished()) {
                            this.mEdgeGlowTop.onRelease();
                        }
                        invalidate(TRANSCRIPT_MODE_DISABLED, (getHeight() - getPaddingBottom()) - this.mEdgeGlowBottom.getMaxHeight(), getWidth(), getHeight());
                    }
                }
            }
            if (incrementalDeltaY != 0) {
                if (this.mScrollY != 0) {
                    this.mScrollY = TRANSCRIPT_MODE_DISABLED;
                    invalidateParentIfNeeded();
                }
                trackMotionScroll(incrementalDeltaY, incrementalDeltaY);
                this.mTouchMode = TOUCH_MODE_SCROLL;
                int motionPosition = findClosestMotionRow(y);
                this.mMotionCorrection = TRANSCRIPT_MODE_DISABLED;
                motionView = getChildAt(motionPosition - this.mFirstPosition);
                this.mMotionViewOriginalTop = motionView != null ? motionView.getTop() : TRANSCRIPT_MODE_DISABLED;
                this.mMotionY = y + scrollOffsetCorrection;
                this.mMotionPosition = motionPosition;
            }
            this.mLastY = (y + TRANSCRIPT_MODE_DISABLED) + scrollOffsetCorrection;
            this.mDirection = newDirection;
        }
    }

    public void onTouchModeChanged(boolean isInTouchMode) {
        if (isInTouchMode) {
            hideSelector();
            if (getHeight() > 0 && getChildCount() > 0) {
                layoutChildren();
            }
            updateSelectorState();
            return;
        }
        int touchMode = this.mTouchMode;
        if (touchMode == TOUCH_MODE_OVERSCROLL || touchMode == TOUCH_MODE_OVERFLING) {
            if (this.mFlingRunnable != null) {
                this.mFlingRunnable.endFling();
            }
            if (this.mPositionScroller != null) {
                this.mPositionScroller.stop();
            }
            if (this.mScrollY != 0) {
                this.mScrollY = TRANSCRIPT_MODE_DISABLED;
                invalidateParentCaches();
                finishGlows();
                invalidate();
            }
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (isEnabled()) {
            if (this.mPositionScroller != null) {
                this.mPositionScroller.stop();
            }
            if (this.mIsDetaching || !isAttachedToWindow()) {
                return PROFILE_SCROLLING;
            }
            startNestedScroll(TRANSCRIPT_MODE_ALWAYS_SCROLL);
            if (this.mFastScroll != null && this.mFastScroll.onTouchEvent(ev)) {
                return true;
            }
            initVelocityTrackerIfNotExists();
            MotionEvent vtev = MotionEvent.obtain(ev);
            int actionMasked = ev.getActionMasked();
            if (actionMasked == 0) {
                this.mNestedYOffset = TRANSCRIPT_MODE_DISABLED;
            }
            vtev.offsetLocation(0.0f, (float) this.mNestedYOffset);
            int x;
            int y;
            int motionPosition;
            switch (actionMasked) {
                case TRANSCRIPT_MODE_DISABLED /*0*/:
                    onTouchDown(ev);
                    break;
                case TRANSCRIPT_MODE_NORMAL /*1*/:
                    onTouchUp(ev);
                    break;
                case TRANSCRIPT_MODE_ALWAYS_SCROLL /*2*/:
                    onTouchMove(ev, vtev);
                    break;
                case TOUCH_MODE_SCROLL /*3*/:
                    onTouchCancel();
                    break;
                case TOUCH_MODE_OVERSCROLL /*5*/:
                    int index = ev.getActionIndex();
                    int id = ev.getPointerId(index);
                    x = (int) ev.getX(index);
                    y = (int) ev.getY(index);
                    this.mMotionCorrection = TRANSCRIPT_MODE_DISABLED;
                    this.mActivePointerId = id;
                    this.mMotionX = x;
                    this.mMotionY = y;
                    motionPosition = pointToPosition(x, y);
                    if (motionPosition >= 0) {
                        this.mMotionViewOriginalTop = getChildAt(motionPosition - this.mFirstPosition).getTop();
                        this.mMotionPosition = motionPosition;
                    }
                    this.mLastY = y;
                    break;
                case TOUCH_MODE_OVERFLING /*6*/:
                    onSecondaryPointerUp(ev);
                    x = this.mMotionX;
                    y = this.mMotionY;
                    motionPosition = pointToPosition(x, y);
                    if (motionPosition >= 0) {
                        this.mMotionViewOriginalTop = getChildAt(motionPosition - this.mFirstPosition).getTop();
                        this.mMotionPosition = motionPosition;
                    }
                    this.mLastY = y;
                    break;
            }
            if (this.mVelocityTracker != null) {
                this.mVelocityTracker.addMovement(vtev);
            }
            vtev.recycle();
            return true;
        } else if (isClickable() || isLongClickable()) {
            return true;
        } else {
            return PROFILE_SCROLLING;
        }
    }

    private void onTouchDown(MotionEvent ev) {
        this.mActivePointerId = ev.getPointerId(TRANSCRIPT_MODE_DISABLED);
        if (this.mTouchMode == TOUCH_MODE_OVERFLING) {
            this.mFlingRunnable.endFling();
            if (this.mPositionScroller != null) {
                this.mPositionScroller.stop();
            }
            this.mTouchMode = TOUCH_MODE_OVERSCROLL;
            this.mMotionX = (int) ev.getX();
            this.mMotionY = (int) ev.getY();
            this.mLastY = this.mMotionY;
            this.mMotionCorrection = TRANSCRIPT_MODE_DISABLED;
            this.mDirection = TRANSCRIPT_MODE_DISABLED;
        } else {
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            int motionPosition = pointToPosition(x, y);
            if (!this.mDataChanged) {
                if (this.mTouchMode == TOUCH_MODE_FLING) {
                    createScrollingCache();
                    this.mTouchMode = TOUCH_MODE_SCROLL;
                    this.mMotionCorrection = TRANSCRIPT_MODE_DISABLED;
                    motionPosition = findMotionRow(y);
                    this.mFlingRunnable.flywheelTouch();
                } else if (motionPosition >= 0 && ((ListAdapter) getAdapter()).isEnabled(motionPosition)) {
                    this.mTouchMode = TRANSCRIPT_MODE_DISABLED;
                    if (this.mPendingCheckForTap == null) {
                        this.mPendingCheckForTap = new CheckForTap();
                    }
                    this.mPendingCheckForTap.x = ev.getX();
                    this.mPendingCheckForTap.y = ev.getY();
                    postDelayed(this.mPendingCheckForTap, (long) ViewConfiguration.getTapTimeout());
                }
            }
            if (motionPosition >= 0) {
                this.mMotionViewOriginalTop = getChildAt(motionPosition - this.mFirstPosition).getTop();
            }
            this.mMotionX = x;
            this.mMotionY = y;
            this.mMotionPosition = motionPosition;
            this.mLastY = RtlSpacingHelper.UNDEFINED;
        }
        if (this.mTouchMode == 0 && this.mMotionPosition != TOUCH_MODE_UNKNOWN && performButtonActionOnTouchDown(ev)) {
            removeCallbacks(this.mPendingCheckForTap);
        }
    }

    private void onTouchMove(MotionEvent ev, MotionEvent vtev) {
        int pointerIndex = ev.findPointerIndex(this.mActivePointerId);
        if (pointerIndex == TOUCH_MODE_UNKNOWN) {
            pointerIndex = TRANSCRIPT_MODE_DISABLED;
            this.mActivePointerId = ev.getPointerId(TRANSCRIPT_MODE_DISABLED);
        }
        if (this.mDataChanged) {
            layoutChildren();
        }
        int y = (int) ev.getY(pointerIndex);
        switch (this.mTouchMode) {
            case TRANSCRIPT_MODE_DISABLED /*0*/:
            case TRANSCRIPT_MODE_NORMAL /*1*/:
            case TRANSCRIPT_MODE_ALWAYS_SCROLL /*2*/:
                if (!startScrollIfNeeded((int) ev.getX(pointerIndex), y, vtev)) {
                    View motionView = getChildAt(this.mMotionPosition - this.mFirstPosition);
                    float x = ev.getX(pointerIndex);
                    if (!pointInView(x, (float) y, (float) this.mTouchSlop)) {
                        setPressed(PROFILE_SCROLLING);
                        if (motionView != null) {
                            motionView.setPressed(PROFILE_SCROLLING);
                        }
                        removeCallbacks(this.mTouchMode == 0 ? this.mPendingCheckForTap : this.mPendingCheckForLongPress);
                        this.mTouchMode = TRANSCRIPT_MODE_ALWAYS_SCROLL;
                        updateSelectorState();
                    } else if (motionView != null) {
                        float[] point = this.mTmpPoint;
                        point[TRANSCRIPT_MODE_DISABLED] = x;
                        point[TRANSCRIPT_MODE_NORMAL] = (float) y;
                        transformPointToViewLocal(point, motionView);
                        motionView.drawableHotspotChanged(point[TRANSCRIPT_MODE_DISABLED], point[TRANSCRIPT_MODE_NORMAL]);
                    }
                }
            case TOUCH_MODE_SCROLL /*3*/:
            case TOUCH_MODE_OVERSCROLL /*5*/:
                scrollIfNeeded((int) ev.getX(pointerIndex), y, vtev);
            default:
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void onTouchUp(android.view.MotionEvent r23) {
        /*
        r22 = this;
        r0 = r22;
        r0 = r0.mTouchMode;
        r18 = r0;
        switch(r18) {
            case 0: goto L_0x0061;
            case 1: goto L_0x0061;
            case 2: goto L_0x0061;
            case 3: goto L_0x0217;
            case 4: goto L_0x0009;
            case 5: goto L_0x03f4;
            default: goto L_0x0009;
        };
    L_0x0009:
        r18 = 0;
        r0 = r22;
        r1 = r18;
        r0.setPressed(r1);
        r0 = r22;
        r0 = r0.mEdgeGlowTop;
        r18 = r0;
        if (r18 == 0) goto L_0x002c;
    L_0x001a:
        r0 = r22;
        r0 = r0.mEdgeGlowTop;
        r18 = r0;
        r18.onRelease();
        r0 = r22;
        r0 = r0.mEdgeGlowBottom;
        r18 = r0;
        r18.onRelease();
    L_0x002c:
        r22.invalidate();
        r0 = r22;
        r0 = r0.mPendingCheckForLongPress;
        r18 = r0;
        r0 = r22;
        r1 = r18;
        r0.removeCallbacks(r1);
        r22.recycleVelocityTracker();
        r18 = -1;
        r0 = r18;
        r1 = r22;
        r1.mActivePointerId = r0;
        r0 = r22;
        r0 = r0.mScrollStrictSpan;
        r18 = r0;
        if (r18 == 0) goto L_0x0060;
    L_0x004f:
        r0 = r22;
        r0 = r0.mScrollStrictSpan;
        r18 = r0;
        r18.finish();
        r18 = 0;
        r0 = r18;
        r1 = r22;
        r1.mScrollStrictSpan = r0;
    L_0x0060:
        return;
    L_0x0061:
        r0 = r22;
        r14 = r0.mMotionPosition;
        r0 = r22;
        r0 = r0.mFirstPosition;
        r18 = r0;
        r18 = r14 - r18;
        r0 = r22;
        r1 = r18;
        r4 = r0.getChildAt(r1);
        if (r4 == 0) goto L_0x020a;
    L_0x0077:
        r0 = r22;
        r0 = r0.mTouchMode;
        r18 = r0;
        if (r18 == 0) goto L_0x0086;
    L_0x007f:
        r18 = 0;
        r0 = r18;
        r4.setPressed(r0);
    L_0x0086:
        r17 = r23.getX();
        r0 = r22;
        r0 = r0.mListPadding;
        r18 = r0;
        r0 = r18;
        r0 = r0.left;
        r18 = r0;
        r0 = r18;
        r0 = (float) r0;
        r18 = r0;
        r18 = (r17 > r18 ? 1 : (r17 == r18 ? 0 : -1));
        if (r18 <= 0) goto L_0x01d9;
    L_0x009f:
        r18 = r22.getWidth();
        r0 = r22;
        r0 = r0.mListPadding;
        r19 = r0;
        r0 = r19;
        r0 = r0.right;
        r19 = r0;
        r18 = r18 - r19;
        r0 = r18;
        r0 = (float) r0;
        r18 = r0;
        r18 = (r17 > r18 ? 1 : (r17 == r18 ? 0 : -1));
        if (r18 >= 0) goto L_0x01d9;
    L_0x00ba:
        r11 = 1;
    L_0x00bb:
        if (r11 == 0) goto L_0x020a;
    L_0x00bd:
        r18 = r4.hasFocusable();
        if (r18 != 0) goto L_0x020a;
    L_0x00c3:
        r0 = r22;
        r0 = r0.mPerformClick;
        r18 = r0;
        if (r18 != 0) goto L_0x00de;
    L_0x00cb:
        r18 = new android.widget.AbsListView$PerformClick;
        r19 = 0;
        r0 = r18;
        r1 = r22;
        r2 = r19;
        r0.<init>(r1, r2);
        r0 = r18;
        r1 = r22;
        r1.mPerformClick = r0;
    L_0x00de:
        r0 = r22;
        r15 = r0.mPerformClick;
        r15.mClickMotionPosition = r14;
        r15.rememberWindowAttachCount();
        r0 = r22;
        r0.mResurrectToPosition = r14;
        r0 = r22;
        r0 = r0.mTouchMode;
        r18 = r0;
        if (r18 == 0) goto L_0x0101;
    L_0x00f3:
        r0 = r22;
        r0 = r0.mTouchMode;
        r18 = r0;
        r19 = 1;
        r0 = r18;
        r1 = r19;
        if (r0 != r1) goto L_0x01f1;
    L_0x0101:
        r0 = r22;
        r0 = r0.mTouchMode;
        r18 = r0;
        if (r18 != 0) goto L_0x01dc;
    L_0x0109:
        r0 = r22;
        r0 = r0.mPendingCheckForTap;
        r18 = r0;
    L_0x010f:
        r0 = r22;
        r1 = r18;
        r0.removeCallbacks(r1);
        r18 = 0;
        r0 = r18;
        r1 = r22;
        r1.mLayoutMode = r0;
        r0 = r22;
        r0 = r0.mDataChanged;
        r18 = r0;
        if (r18 != 0) goto L_0x01e4;
    L_0x0126:
        r0 = r22;
        r0 = r0.mAdapter;
        r18 = r0;
        r0 = r18;
        r18 = r0.isEnabled(r14);
        if (r18 == 0) goto L_0x01e4;
    L_0x0134:
        r18 = 1;
        r0 = r18;
        r1 = r22;
        r1.mTouchMode = r0;
        r0 = r22;
        r0 = r0.mMotionPosition;
        r18 = r0;
        r0 = r22;
        r1 = r18;
        r0.setSelectedPositionInt(r1);
        r22.layoutChildren();
        r18 = 1;
        r0 = r18;
        r4.setPressed(r0);
        r0 = r22;
        r0 = r0.mMotionPosition;
        r18 = r0;
        r0 = r22;
        r1 = r18;
        r0.positionSelector(r1, r4);
        r18 = 1;
        r0 = r22;
        r1 = r18;
        r0.setPressed(r1);
        r0 = r22;
        r0 = r0.mSelector;
        r18 = r0;
        if (r18 == 0) goto L_0x019b;
    L_0x0171:
        r0 = r22;
        r0 = r0.mSelector;
        r18 = r0;
        r8 = r18.getCurrent();
        if (r8 == 0) goto L_0x0188;
    L_0x017d:
        r0 = r8 instanceof android.graphics.drawable.TransitionDrawable;
        r18 = r0;
        if (r18 == 0) goto L_0x0188;
    L_0x0183:
        r8 = (android.graphics.drawable.TransitionDrawable) r8;
        r8.resetTransition();
    L_0x0188:
        r0 = r22;
        r0 = r0.mSelector;
        r18 = r0;
        r19 = r23.getY();
        r0 = r18;
        r1 = r17;
        r2 = r19;
        r0.setHotspot(r1, r2);
    L_0x019b:
        r0 = r22;
        r0 = r0.mTouchModeReset;
        r18 = r0;
        if (r18 == 0) goto L_0x01b0;
    L_0x01a3:
        r0 = r22;
        r0 = r0.mTouchModeReset;
        r18 = r0;
        r0 = r22;
        r1 = r18;
        r0.removeCallbacks(r1);
    L_0x01b0:
        r18 = new android.widget.AbsListView$3;
        r0 = r18;
        r1 = r22;
        r0.<init>(r1, r4, r15);
        r0 = r18;
        r1 = r22;
        r1.mTouchModeReset = r0;
        r0 = r22;
        r0 = r0.mTouchModeReset;
        r18 = r0;
        r19 = android.view.ViewConfiguration.getPressedStateDuration();
        r0 = r19;
        r0 = (long) r0;
        r20 = r0;
        r0 = r22;
        r1 = r18;
        r2 = r20;
        r0.postDelayed(r1, r2);
        goto L_0x0060;
    L_0x01d9:
        r11 = 0;
        goto L_0x00bb;
    L_0x01dc:
        r0 = r22;
        r0 = r0.mPendingCheckForLongPress;
        r18 = r0;
        goto L_0x010f;
    L_0x01e4:
        r18 = -1;
        r0 = r18;
        r1 = r22;
        r1.mTouchMode = r0;
        r22.updateSelectorState();
        goto L_0x0060;
    L_0x01f1:
        r0 = r22;
        r0 = r0.mDataChanged;
        r18 = r0;
        if (r18 != 0) goto L_0x020a;
    L_0x01f9:
        r0 = r22;
        r0 = r0.mAdapter;
        r18 = r0;
        r0 = r18;
        r18 = r0.isEnabled(r14);
        if (r18 == 0) goto L_0x020a;
    L_0x0207:
        r15.run();
    L_0x020a:
        r18 = -1;
        r0 = r18;
        r1 = r22;
        r1.mTouchMode = r0;
        r22.updateSelectorState();
        goto L_0x0009;
    L_0x0217:
        r5 = r22.getChildCount();
        if (r5 <= 0) goto L_0x03e1;
    L_0x021d:
        r18 = 0;
        r0 = r22;
        r1 = r18;
        r18 = r0.getChildAt(r1);
        r9 = r18.getTop();
        r18 = r5 + -1;
        r0 = r22;
        r1 = r18;
        r18 = r0.getChildAt(r1);
        r13 = r18.getBottom();
        r0 = r22;
        r0 = r0.mListPadding;
        r18 = r0;
        r0 = r18;
        r7 = r0.top;
        r18 = r22.getHeight();
        r0 = r22;
        r0 = r0.mListPadding;
        r19 = r0;
        r0 = r19;
        r0 = r0.bottom;
        r19 = r0;
        r6 = r18 - r19;
        r0 = r22;
        r0 = r0.mFirstPosition;
        r18 = r0;
        if (r18 != 0) goto L_0x0290;
    L_0x025d:
        if (r9 < r7) goto L_0x0290;
    L_0x025f:
        r0 = r22;
        r0 = r0.mFirstPosition;
        r18 = r0;
        r18 = r18 + r5;
        r0 = r22;
        r0 = r0.mItemCount;
        r19 = r0;
        r0 = r18;
        r1 = r19;
        if (r0 >= r1) goto L_0x0290;
    L_0x0273:
        r18 = r22.getHeight();
        r18 = r18 - r6;
        r0 = r18;
        if (r13 > r0) goto L_0x0290;
    L_0x027d:
        r18 = -1;
        r0 = r18;
        r1 = r22;
        r1.mTouchMode = r0;
        r18 = 0;
        r0 = r22;
        r1 = r18;
        r0.reportScrollStateChange(r1);
        goto L_0x0009;
    L_0x0290:
        r0 = r22;
        r0 = r0.mVelocityTracker;
        r16 = r0;
        r18 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r0 = r22;
        r0 = r0.mMaximumVelocity;
        r19 = r0;
        r0 = r19;
        r0 = (float) r0;
        r19 = r0;
        r0 = r16;
        r1 = r18;
        r2 = r19;
        r0.computeCurrentVelocity(r1, r2);
        r0 = r22;
        r0 = r0.mActivePointerId;
        r18 = r0;
        r0 = r16;
        r1 = r18;
        r18 = r0.getYVelocity(r1);
        r0 = r22;
        r0 = r0.mVelocityScale;
        r19 = r0;
        r18 = r18 * r19;
        r0 = r18;
        r12 = (int) r0;
        r18 = java.lang.Math.abs(r12);
        r0 = r22;
        r0 = r0.mMinimumVelocity;
        r19 = r0;
        r0 = r18;
        r1 = r19;
        if (r0 <= r1) goto L_0x0367;
    L_0x02d5:
        r10 = 1;
    L_0x02d6:
        if (r10 == 0) goto L_0x037d;
    L_0x02d8:
        r0 = r22;
        r0 = r0.mFirstPosition;
        r18 = r0;
        if (r18 != 0) goto L_0x02ec;
    L_0x02e0:
        r0 = r22;
        r0 = r0.mOverscrollDistance;
        r18 = r0;
        r18 = r7 - r18;
        r0 = r18;
        if (r9 == r0) goto L_0x037d;
    L_0x02ec:
        r0 = r22;
        r0 = r0.mFirstPosition;
        r18 = r0;
        r18 = r18 + r5;
        r0 = r22;
        r0 = r0.mItemCount;
        r19 = r0;
        r0 = r18;
        r1 = r19;
        if (r0 != r1) goto L_0x030c;
    L_0x0300:
        r0 = r22;
        r0 = r0.mOverscrollDistance;
        r18 = r0;
        r18 = r18 + r6;
        r0 = r18;
        if (r13 == r0) goto L_0x037d;
    L_0x030c:
        r18 = 0;
        r0 = -r12;
        r19 = r0;
        r0 = r19;
        r0 = (float) r0;
        r19 = r0;
        r0 = r22;
        r1 = r18;
        r2 = r19;
        r18 = r0.dispatchNestedPreFling(r1, r2);
        if (r18 != 0) goto L_0x036a;
    L_0x0322:
        r0 = r22;
        r0 = r0.mFlingRunnable;
        r18 = r0;
        if (r18 != 0) goto L_0x0339;
    L_0x032a:
        r18 = new android.widget.AbsListView$FlingRunnable;
        r0 = r18;
        r1 = r22;
        r0.<init>(r1);
        r0 = r18;
        r1 = r22;
        r1.mFlingRunnable = r0;
    L_0x0339:
        r18 = 2;
        r0 = r22;
        r1 = r18;
        r0.reportScrollStateChange(r1);
        r0 = r22;
        r0 = r0.mFlingRunnable;
        r18 = r0;
        r0 = -r12;
        r19 = r0;
        r18.start(r19);
        r18 = 0;
        r0 = -r12;
        r19 = r0;
        r0 = r19;
        r0 = (float) r0;
        r19 = r0;
        r20 = 1;
        r0 = r22;
        r1 = r18;
        r2 = r19;
        r3 = r20;
        r0.dispatchNestedFling(r1, r2, r3);
        goto L_0x0009;
    L_0x0367:
        r10 = 0;
        goto L_0x02d6;
    L_0x036a:
        r18 = -1;
        r0 = r18;
        r1 = r22;
        r1.mTouchMode = r0;
        r18 = 0;
        r0 = r22;
        r1 = r18;
        r0.reportScrollStateChange(r1);
        goto L_0x0009;
    L_0x037d:
        r18 = -1;
        r0 = r18;
        r1 = r22;
        r1.mTouchMode = r0;
        r18 = 0;
        r0 = r22;
        r1 = r18;
        r0.reportScrollStateChange(r1);
        r0 = r22;
        r0 = r0.mFlingRunnable;
        r18 = r0;
        if (r18 == 0) goto L_0x039f;
    L_0x0396:
        r0 = r22;
        r0 = r0.mFlingRunnable;
        r18 = r0;
        r18.endFling();
    L_0x039f:
        r0 = r22;
        r0 = r0.mPositionScroller;
        r18 = r0;
        if (r18 == 0) goto L_0x03b0;
    L_0x03a7:
        r0 = r22;
        r0 = r0.mPositionScroller;
        r18 = r0;
        r18.stop();
    L_0x03b0:
        if (r10 == 0) goto L_0x0009;
    L_0x03b2:
        r18 = 0;
        r0 = -r12;
        r19 = r0;
        r0 = r19;
        r0 = (float) r0;
        r19 = r0;
        r0 = r22;
        r1 = r18;
        r2 = r19;
        r18 = r0.dispatchNestedPreFling(r1, r2);
        if (r18 != 0) goto L_0x0009;
    L_0x03c8:
        r18 = 0;
        r0 = -r12;
        r19 = r0;
        r0 = r19;
        r0 = (float) r0;
        r19 = r0;
        r20 = 0;
        r0 = r22;
        r1 = r18;
        r2 = r19;
        r3 = r20;
        r0.dispatchNestedFling(r1, r2, r3);
        goto L_0x0009;
    L_0x03e1:
        r18 = -1;
        r0 = r18;
        r1 = r22;
        r1.mTouchMode = r0;
        r18 = 0;
        r0 = r22;
        r1 = r18;
        r0.reportScrollStateChange(r1);
        goto L_0x0009;
    L_0x03f4:
        r0 = r22;
        r0 = r0.mFlingRunnable;
        r18 = r0;
        if (r18 != 0) goto L_0x040b;
    L_0x03fc:
        r18 = new android.widget.AbsListView$FlingRunnable;
        r0 = r18;
        r1 = r22;
        r0.<init>(r1);
        r0 = r18;
        r1 = r22;
        r1.mFlingRunnable = r0;
    L_0x040b:
        r0 = r22;
        r0 = r0.mVelocityTracker;
        r16 = r0;
        r18 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r0 = r22;
        r0 = r0.mMaximumVelocity;
        r19 = r0;
        r0 = r19;
        r0 = (float) r0;
        r19 = r0;
        r0 = r16;
        r1 = r18;
        r2 = r19;
        r0.computeCurrentVelocity(r1, r2);
        r0 = r22;
        r0 = r0.mActivePointerId;
        r18 = r0;
        r0 = r16;
        r1 = r18;
        r18 = r0.getYVelocity(r1);
        r0 = r18;
        r12 = (int) r0;
        r18 = 2;
        r0 = r22;
        r1 = r18;
        r0.reportScrollStateChange(r1);
        r18 = java.lang.Math.abs(r12);
        r0 = r22;
        r0 = r0.mMinimumVelocity;
        r19 = r0;
        r0 = r18;
        r1 = r19;
        if (r0 <= r1) goto L_0x045f;
    L_0x0451:
        r0 = r22;
        r0 = r0.mFlingRunnable;
        r18 = r0;
        r0 = -r12;
        r19 = r0;
        r18.startOverfling(r19);
        goto L_0x0009;
    L_0x045f:
        r0 = r22;
        r0 = r0.mFlingRunnable;
        r18 = r0;
        r18.startSpringback();
        goto L_0x0009;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.AbsListView.onTouchUp(android.view.MotionEvent):void");
    }

    private void onTouchCancel() {
        switch (this.mTouchMode) {
            case TOUCH_MODE_OVERSCROLL /*5*/:
                if (this.mFlingRunnable == null) {
                    this.mFlingRunnable = new FlingRunnable(this);
                }
                this.mFlingRunnable.startSpringback();
                break;
            case TOUCH_MODE_OVERFLING /*6*/:
                break;
            default:
                this.mTouchMode = TOUCH_MODE_UNKNOWN;
                setPressed(PROFILE_SCROLLING);
                View motionView = getChildAt(this.mMotionPosition - this.mFirstPosition);
                if (motionView != null) {
                    motionView.setPressed(PROFILE_SCROLLING);
                }
                clearScrollingCache();
                removeCallbacks(this.mPendingCheckForLongPress);
                recycleVelocityTracker();
                break;
        }
        if (this.mEdgeGlowTop != null) {
            this.mEdgeGlowTop.onRelease();
            this.mEdgeGlowBottom.onRelease();
        }
        this.mActivePointerId = TOUCH_MODE_UNKNOWN;
    }

    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        if (this.mScrollY != scrollY) {
            onScrollChanged(this.mScrollX, scrollY, this.mScrollX, this.mScrollY);
            this.mScrollY = scrollY;
            invalidateParentIfNeeded();
            awakenScrollBars();
        }
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        if ((event.getSource() & TRANSCRIPT_MODE_ALWAYS_SCROLL) != 0) {
            switch (event.getAction()) {
                case SetPendingIntentTemplate.TAG /*8*/:
                    if (this.mTouchMode == TOUCH_MODE_UNKNOWN) {
                        float vscroll = event.getAxisValue(9);
                        if (vscroll != 0.0f) {
                            int delta = (int) (getVerticalScrollFactor() * vscroll);
                            if (!trackMotionScroll(delta, delta)) {
                                return true;
                            }
                        }
                    }
                    break;
            }
        }
        return super.onGenericMotionEvent(event);
    }

    public void fling(int velocityY) {
        if (this.mFlingRunnable == null) {
            this.mFlingRunnable = new FlingRunnable(this);
        }
        reportScrollStateChange(TRANSCRIPT_MODE_ALWAYS_SCROLL);
        this.mFlingRunnable.start(velocityY);
    }

    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & TRANSCRIPT_MODE_ALWAYS_SCROLL) != 0 ? true : PROFILE_SCROLLING;
    }

    public void onNestedScrollAccepted(View child, View target, int axes) {
        super.onNestedScrollAccepted(child, target, axes);
        startNestedScroll(TRANSCRIPT_MODE_ALWAYS_SCROLL);
    }

    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        int oldTop;
        View motionView = getChildAt(getChildCount() / TRANSCRIPT_MODE_ALWAYS_SCROLL);
        if (motionView != null) {
            oldTop = motionView.getTop();
        } else {
            oldTop = TRANSCRIPT_MODE_DISABLED;
        }
        if (motionView == null || trackMotionScroll(-dyUnconsumed, -dyUnconsumed)) {
            int myUnconsumed = dyUnconsumed;
            int myConsumed = TRANSCRIPT_MODE_DISABLED;
            if (motionView != null) {
                myConsumed = motionView.getTop() - oldTop;
                myUnconsumed -= myConsumed;
            }
            dispatchNestedScroll(TRANSCRIPT_MODE_DISABLED, myConsumed, TRANSCRIPT_MODE_DISABLED, myUnconsumed, null);
        }
    }

    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        int childCount = getChildCount();
        if (consumed || childCount <= 0 || !canScrollList((int) velocityY) || Math.abs(velocityY) <= ((float) this.mMinimumVelocity)) {
            return dispatchNestedFling(velocityX, velocityY, consumed);
        }
        reportScrollStateChange(TRANSCRIPT_MODE_ALWAYS_SCROLL);
        if (this.mFlingRunnable == null) {
            this.mFlingRunnable = new FlingRunnable(this);
        }
        if (!dispatchNestedPreFling(0.0f, velocityY)) {
            this.mFlingRunnable.start((int) velocityY);
        }
        return true;
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (this.mEdgeGlowTop != null) {
            int restoreCount;
            int width;
            int scrollY = this.mScrollY;
            if (!this.mEdgeGlowTop.isFinished()) {
                restoreCount = canvas.save();
                width = getWidth();
                canvas.translate(0.0f, (float) Math.min(TRANSCRIPT_MODE_DISABLED, this.mFirstPositionDistanceGuess + scrollY));
                this.mEdgeGlowTop.setSize(width, getHeight());
                if (this.mEdgeGlowTop.draw(canvas)) {
                    invalidate(TRANSCRIPT_MODE_DISABLED, TRANSCRIPT_MODE_DISABLED, getWidth(), this.mEdgeGlowTop.getMaxHeight() + getPaddingTop());
                }
                canvas.restoreToCount(restoreCount);
            }
            if (!this.mEdgeGlowBottom.isFinished()) {
                restoreCount = canvas.save();
                width = getWidth();
                int height = getHeight();
                canvas.translate((float) (-width), (float) Math.max(height, this.mLastPositionDistanceGuess + scrollY));
                canvas.rotate(180.0f, (float) width, 0.0f);
                this.mEdgeGlowBottom.setSize(width, height);
                if (this.mEdgeGlowBottom.draw(canvas)) {
                    invalidate(TRANSCRIPT_MODE_DISABLED, (getHeight() - getPaddingBottom()) - this.mEdgeGlowBottom.getMaxHeight(), getWidth(), getHeight());
                }
                canvas.restoreToCount(restoreCount);
            }
        }
    }

    public void setOverScrollEffectPadding(int leftPadding, int rightPadding) {
        this.mGlowPaddingLeft = leftPadding;
        this.mGlowPaddingRight = rightPadding;
    }

    private void initOrResetVelocityTracker() {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        } else {
            this.mVelocityTracker.clear();
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (disallowIntercept) {
            recycleVelocityTracker();
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    public boolean onInterceptHoverEvent(MotionEvent event) {
        if (this.mFastScroll == null || !this.mFastScroll.onInterceptHoverEvent(event)) {
            return super.onInterceptHoverEvent(event);
        }
        return true;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int actionMasked = ev.getActionMasked();
        if (this.mPositionScroller != null) {
            this.mPositionScroller.stop();
        }
        if (this.mIsDetaching || !isAttachedToWindow()) {
            return PROFILE_SCROLLING;
        }
        if (this.mFastScroll != null && this.mFastScroll.onInterceptTouchEvent(ev)) {
            return true;
        }
        int y;
        switch (actionMasked) {
            case TRANSCRIPT_MODE_DISABLED /*0*/:
                int touchMode = this.mTouchMode;
                if (touchMode == TOUCH_MODE_OVERFLING || touchMode == TOUCH_MODE_OVERSCROLL) {
                    this.mMotionCorrection = TRANSCRIPT_MODE_DISABLED;
                    return true;
                }
                int x = (int) ev.getX();
                y = (int) ev.getY();
                this.mActivePointerId = ev.getPointerId(TRANSCRIPT_MODE_DISABLED);
                int motionPosition = findMotionRow(y);
                if (touchMode != TOUCH_MODE_FLING && motionPosition >= 0) {
                    this.mMotionViewOriginalTop = getChildAt(motionPosition - this.mFirstPosition).getTop();
                    this.mMotionX = x;
                    this.mMotionY = y;
                    this.mMotionPosition = motionPosition;
                    this.mTouchMode = TRANSCRIPT_MODE_DISABLED;
                    clearScrollingCache();
                }
                this.mLastY = RtlSpacingHelper.UNDEFINED;
                initOrResetVelocityTracker();
                this.mVelocityTracker.addMovement(ev);
                this.mNestedYOffset = TRANSCRIPT_MODE_DISABLED;
                startNestedScroll(TRANSCRIPT_MODE_ALWAYS_SCROLL);
                if (touchMode == TOUCH_MODE_FLING) {
                    return true;
                }
                break;
            case TRANSCRIPT_MODE_NORMAL /*1*/:
            case TOUCH_MODE_SCROLL /*3*/:
                this.mTouchMode = TOUCH_MODE_UNKNOWN;
                this.mActivePointerId = TOUCH_MODE_UNKNOWN;
                recycleVelocityTracker();
                reportScrollStateChange(TRANSCRIPT_MODE_DISABLED);
                stopNestedScroll();
                break;
            case TRANSCRIPT_MODE_ALWAYS_SCROLL /*2*/:
                switch (this.mTouchMode) {
                    case TRANSCRIPT_MODE_DISABLED /*0*/:
                        int pointerIndex = ev.findPointerIndex(this.mActivePointerId);
                        if (pointerIndex == TOUCH_MODE_UNKNOWN) {
                            pointerIndex = TRANSCRIPT_MODE_DISABLED;
                            this.mActivePointerId = ev.getPointerId(TRANSCRIPT_MODE_DISABLED);
                        }
                        y = (int) ev.getY(pointerIndex);
                        initVelocityTrackerIfNotExists();
                        this.mVelocityTracker.addMovement(ev);
                        if (startScrollIfNeeded((int) ev.getX(pointerIndex), y, null)) {
                            return true;
                        }
                        break;
                    default:
                        break;
                }
            case TOUCH_MODE_OVERFLING /*6*/:
                onSecondaryPointerUp(ev);
                break;
        }
        return PROFILE_SCROLLING;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> 8;
        if (ev.getPointerId(pointerIndex) == this.mActivePointerId) {
            int newPointerIndex = pointerIndex == 0 ? TRANSCRIPT_MODE_NORMAL : TRANSCRIPT_MODE_DISABLED;
            this.mMotionX = (int) ev.getX(newPointerIndex);
            this.mMotionY = (int) ev.getY(newPointerIndex);
            this.mMotionCorrection = TRANSCRIPT_MODE_DISABLED;
            this.mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    public void addTouchables(ArrayList<View> views) {
        int count = getChildCount();
        int firstPosition = this.mFirstPosition;
        ListAdapter adapter = this.mAdapter;
        if (adapter != null) {
            for (int i = TRANSCRIPT_MODE_DISABLED; i < count; i += TRANSCRIPT_MODE_NORMAL) {
                View child = getChildAt(i);
                if (adapter.isEnabled(firstPosition + i)) {
                    views.add(child);
                }
                child.addTouchables(views);
            }
        }
    }

    void reportScrollStateChange(int newState) {
        if (newState != this.mLastScrollState && this.mOnScrollListener != null) {
            this.mLastScrollState = newState;
            this.mOnScrollListener.onScrollStateChanged(this, newState);
        }
    }

    public void setFriction(float friction) {
        if (this.mFlingRunnable == null) {
            this.mFlingRunnable = new FlingRunnable(this);
        }
        FlingRunnable.access$1600(this.mFlingRunnable).setFriction(friction);
    }

    public void setVelocityScale(float scale) {
        this.mVelocityScale = scale;
    }

    AbsPositionScroller createPositionScroller() {
        return new PositionScroller(this);
    }

    public void smoothScrollToPosition(int position) {
        if (this.mPositionScroller == null) {
            this.mPositionScroller = createPositionScroller();
        }
        this.mPositionScroller.start(position);
    }

    public void smoothScrollToPositionFromTop(int position, int offset, int duration) {
        if (this.mPositionScroller == null) {
            this.mPositionScroller = createPositionScroller();
        }
        this.mPositionScroller.startWithOffset(position, offset, duration);
    }

    public void smoothScrollToPositionFromTop(int position, int offset) {
        if (this.mPositionScroller == null) {
            this.mPositionScroller = createPositionScroller();
        }
        this.mPositionScroller.startWithOffset(position, offset);
    }

    public void smoothScrollToPosition(int position, int boundPosition) {
        if (this.mPositionScroller == null) {
            this.mPositionScroller = createPositionScroller();
        }
        this.mPositionScroller.start(position, boundPosition);
    }

    public void smoothScrollBy(int distance, int duration) {
        smoothScrollBy(distance, duration, PROFILE_SCROLLING);
    }

    void smoothScrollBy(int distance, int duration, boolean linear) {
        if (this.mFlingRunnable == null) {
            this.mFlingRunnable = new FlingRunnable(this);
        }
        int firstPos = this.mFirstPosition;
        int childCount = getChildCount();
        int lastPos = firstPos + childCount;
        int topLimit = getPaddingTop();
        int bottomLimit = getHeight() - getPaddingBottom();
        if (distance == 0 || this.mItemCount == 0 || childCount == 0 || ((firstPos == 0 && getChildAt(TRANSCRIPT_MODE_DISABLED).getTop() == topLimit && distance < 0) || (lastPos == this.mItemCount && getChildAt(childCount + TOUCH_MODE_UNKNOWN).getBottom() == bottomLimit && distance > 0))) {
            this.mFlingRunnable.endFling();
            if (this.mPositionScroller != null) {
                this.mPositionScroller.stop();
                return;
            }
            return;
        }
        reportScrollStateChange(TRANSCRIPT_MODE_ALWAYS_SCROLL);
        this.mFlingRunnable.startScroll(distance, duration, linear);
    }

    void smoothScrollByOffset(int position) {
        int index = TOUCH_MODE_UNKNOWN;
        if (position < 0) {
            index = getFirstVisiblePosition();
        } else if (position > 0) {
            index = getLastVisiblePosition();
        }
        if (index > TOUCH_MODE_UNKNOWN) {
            View child = getChildAt(index - getFirstVisiblePosition());
            if (child != null) {
                Rect visibleRect = new Rect();
                if (child.getGlobalVisibleRect(visibleRect)) {
                    float visibleArea = ((float) (visibleRect.width() * visibleRect.height())) / ((float) (child.getWidth() * child.getHeight()));
                    if (position < 0 && visibleArea < 0.75f) {
                        index += TRANSCRIPT_MODE_NORMAL;
                    } else if (position > 0 && visibleArea < 0.75f) {
                        index += TOUCH_MODE_UNKNOWN;
                    }
                }
                smoothScrollToPosition(Math.max(TRANSCRIPT_MODE_DISABLED, Math.min(getCount(), index + position)));
            }
        }
    }

    private void createScrollingCache() {
        if (this.mScrollingCacheEnabled && !this.mCachingStarted && !isHardwareAccelerated()) {
            setChildrenDrawnWithCacheEnabled(true);
            setChildrenDrawingCacheEnabled(true);
            this.mCachingActive = true;
            this.mCachingStarted = true;
        }
    }

    private void clearScrollingCache() {
        if (!isHardwareAccelerated()) {
            if (this.mClearScrollingCache == null) {
                this.mClearScrollingCache = new AnonymousClass4(this);
            }
            post(this.mClearScrollingCache);
        }
    }

    public void scrollListBy(int y) {
        trackMotionScroll(-y, -y);
    }

    public boolean canScrollList(int direction) {
        int childCount = getChildCount();
        if (childCount == 0) {
            return PROFILE_SCROLLING;
        }
        int firstPosition = this.mFirstPosition;
        Rect listPadding = this.mListPadding;
        if (direction > 0) {
            int lastBottom = getChildAt(childCount + TOUCH_MODE_UNKNOWN).getBottom();
            if (firstPosition + childCount < this.mItemCount || lastBottom > getHeight() - listPadding.bottom) {
                return true;
            }
            return PROFILE_SCROLLING;
        }
        int firstTop = getChildAt(TRANSCRIPT_MODE_DISABLED).getTop();
        if (firstPosition > 0 || firstTop < listPadding.top) {
            return true;
        }
        return PROFILE_SCROLLING;
    }

    boolean trackMotionScroll(int deltaY, int incrementalDeltaY) {
        int childCount = getChildCount();
        if (childCount == 0) {
            return true;
        }
        boolean cannotScrollUp;
        int firstTop = getChildAt(TRANSCRIPT_MODE_DISABLED).getTop();
        int lastBottom = getChildAt(childCount + TOUCH_MODE_UNKNOWN).getBottom();
        Rect listPadding = this.mListPadding;
        int effectivePaddingTop = TRANSCRIPT_MODE_DISABLED;
        int effectivePaddingBottom = TRANSCRIPT_MODE_DISABLED;
        if ((this.mGroupFlags & 34) == 34) {
            effectivePaddingTop = listPadding.top;
            effectivePaddingBottom = listPadding.bottom;
        }
        int spaceAbove = effectivePaddingTop - firstTop;
        int spaceBelow = lastBottom - (getHeight() - effectivePaddingBottom);
        int height = (getHeight() - this.mPaddingBottom) - this.mPaddingTop;
        if (deltaY < 0) {
            deltaY = Math.max(-(height + TOUCH_MODE_UNKNOWN), deltaY);
        } else {
            deltaY = Math.min(height + TOUCH_MODE_UNKNOWN, deltaY);
        }
        if (incrementalDeltaY < 0) {
            incrementalDeltaY = Math.max(-(height + TOUCH_MODE_UNKNOWN), incrementalDeltaY);
        } else {
            incrementalDeltaY = Math.min(height + TOUCH_MODE_UNKNOWN, incrementalDeltaY);
        }
        int firstPosition = this.mFirstPosition;
        if (firstPosition == 0) {
            this.mFirstPositionDistanceGuess = firstTop - listPadding.top;
        } else {
            this.mFirstPositionDistanceGuess += incrementalDeltaY;
        }
        if (firstPosition + childCount == this.mItemCount) {
            this.mLastPositionDistanceGuess = listPadding.bottom + lastBottom;
        } else {
            this.mLastPositionDistanceGuess += incrementalDeltaY;
        }
        boolean cannotScrollDown = (firstPosition != 0 || firstTop < listPadding.top || incrementalDeltaY < 0) ? PROFILE_SCROLLING : true;
        if (firstPosition + childCount == this.mItemCount) {
            if (lastBottom <= getHeight() - listPadding.bottom && incrementalDeltaY <= 0) {
                cannotScrollUp = true;
                if (cannotScrollDown && !cannotScrollUp) {
                    int i;
                    int childIndex;
                    boolean down = incrementalDeltaY < 0 ? true : PROFILE_SCROLLING;
                    boolean inTouchMode = isInTouchMode();
                    if (inTouchMode) {
                        hideSelector();
                    }
                    int headerViewsCount = getHeaderViewsCount();
                    int footerViewsStart = this.mItemCount - getFooterViewsCount();
                    int start = TRANSCRIPT_MODE_DISABLED;
                    int count = TRANSCRIPT_MODE_DISABLED;
                    int i2;
                    View child;
                    int position;
                    if (!down) {
                        int bottom = getHeight() - incrementalDeltaY;
                        if ((this.mGroupFlags & 34) == 34) {
                            bottom -= listPadding.bottom;
                        }
                        for (i2 = childCount + TOUCH_MODE_UNKNOWN; i2 >= 0; i2 += TOUCH_MODE_UNKNOWN) {
                            child = getChildAt(i2);
                            if (child.getTop() <= bottom) {
                                break;
                            }
                            start = i2;
                            count += TRANSCRIPT_MODE_NORMAL;
                            position = firstPosition + i2;
                            if (position >= headerViewsCount && position < footerViewsStart) {
                                child.clearAccessibilityFocus();
                                this.mRecycler.addScrapView(child, position);
                            }
                        }
                    } else {
                        int top = -incrementalDeltaY;
                        if ((this.mGroupFlags & 34) == 34) {
                            top += listPadding.top;
                        }
                        for (i2 = TRANSCRIPT_MODE_DISABLED; i2 < childCount; i2 += TRANSCRIPT_MODE_NORMAL) {
                            child = getChildAt(i2);
                            if (child.getBottom() >= top) {
                                break;
                            }
                            count += TRANSCRIPT_MODE_NORMAL;
                            position = firstPosition + i2;
                            if (position >= headerViewsCount && position < footerViewsStart) {
                                child.clearAccessibilityFocus();
                                this.mRecycler.addScrapView(child, position);
                            }
                        }
                    }
                    this.mMotionViewNewTop = this.mMotionViewOriginalTop + deltaY;
                    this.mBlockLayoutRequests = true;
                    if (count > 0) {
                        detachViewsFromParent(start, count);
                        this.mRecycler.removeSkippedScrap();
                    }
                    if (!awakenScrollBars()) {
                        invalidate();
                    }
                    offsetChildrenTopAndBottom(incrementalDeltaY);
                    if (down) {
                        this.mFirstPosition += count;
                    }
                    int absIncrementalDeltaY = Math.abs(incrementalDeltaY);
                    if (spaceAbove < absIncrementalDeltaY || spaceBelow < absIncrementalDeltaY) {
                        fillGap(down);
                    }
                    if (!inTouchMode) {
                        i = this.mSelectedPosition;
                        if (r0 != TOUCH_MODE_UNKNOWN) {
                            childIndex = this.mSelectedPosition - this.mFirstPosition;
                            if (childIndex >= 0 && childIndex < getChildCount()) {
                                positionSelector(this.mSelectedPosition, getChildAt(childIndex));
                            }
                            this.mBlockLayoutRequests = PROFILE_SCROLLING;
                            invokeOnItemScrollListener();
                            return PROFILE_SCROLLING;
                        }
                    }
                    i = this.mSelectorPosition;
                    if (r0 != TOUCH_MODE_UNKNOWN) {
                        childIndex = this.mSelectorPosition - this.mFirstPosition;
                        if (childIndex >= 0 && childIndex < getChildCount()) {
                            positionSelector(TOUCH_MODE_UNKNOWN, getChildAt(childIndex));
                        }
                    } else {
                        this.mSelectorRect.setEmpty();
                    }
                    this.mBlockLayoutRequests = PROFILE_SCROLLING;
                    invokeOnItemScrollListener();
                    return PROFILE_SCROLLING;
                } else if (incrementalDeltaY == 0) {
                    return true;
                } else {
                    return PROFILE_SCROLLING;
                }
            }
        }
        cannotScrollUp = PROFILE_SCROLLING;
        if (cannotScrollDown) {
        }
        if (incrementalDeltaY == 0) {
            return PROFILE_SCROLLING;
        }
        return true;
    }

    int getHeaderViewsCount() {
        return TRANSCRIPT_MODE_DISABLED;
    }

    int getFooterViewsCount() {
        return TRANSCRIPT_MODE_DISABLED;
    }

    void hideSelector() {
        if (this.mSelectedPosition != TOUCH_MODE_UNKNOWN) {
            if (this.mLayoutMode != TOUCH_MODE_FLING) {
                this.mResurrectToPosition = this.mSelectedPosition;
            }
            if (this.mNextSelectedPosition >= 0 && this.mNextSelectedPosition != this.mSelectedPosition) {
                this.mResurrectToPosition = this.mNextSelectedPosition;
            }
            setSelectedPositionInt(TOUCH_MODE_UNKNOWN);
            setNextSelectedPositionInt(TOUCH_MODE_UNKNOWN);
            this.mSelectedTop = TRANSCRIPT_MODE_DISABLED;
        }
    }

    int reconcileSelectedPosition() {
        int position = this.mSelectedPosition;
        if (position < 0) {
            position = this.mResurrectToPosition;
        }
        return Math.min(Math.max(TRANSCRIPT_MODE_DISABLED, position), this.mItemCount + TOUCH_MODE_UNKNOWN);
    }

    int findClosestMotionRow(int y) {
        int childCount = getChildCount();
        if (childCount == 0) {
            return TOUCH_MODE_UNKNOWN;
        }
        int motionRow = findMotionRow(y);
        return motionRow == TOUCH_MODE_UNKNOWN ? (this.mFirstPosition + childCount) + TOUCH_MODE_UNKNOWN : motionRow;
    }

    public void invalidateViews() {
        this.mDataChanged = true;
        rememberSyncState();
        requestLayout();
        invalidate();
    }

    boolean resurrectSelectionIfNeeded() {
        if (this.mSelectedPosition >= 0 || !resurrectSelection()) {
            return PROFILE_SCROLLING;
        }
        updateSelectorState();
        return true;
    }

    boolean resurrectSelection() {
        int childCount = getChildCount();
        if (childCount <= 0) {
            return PROFILE_SCROLLING;
        }
        int selectedPos;
        int selectedTop = TRANSCRIPT_MODE_DISABLED;
        int childrenTop = this.mListPadding.top;
        int i = this.mBottom;
        int i2 = this.mTop;
        int childrenBottom = (r0 - r0) - this.mListPadding.bottom;
        int firstPosition = this.mFirstPosition;
        int toPosition = this.mResurrectToPosition;
        boolean down = true;
        if (toPosition >= firstPosition && toPosition < firstPosition + childCount) {
            selectedPos = toPosition;
            View selected = getChildAt(selectedPos - this.mFirstPosition);
            selectedTop = selected.getTop();
            int selectedBottom = selected.getBottom();
            if (selectedTop < childrenTop) {
                selectedTop = childrenTop + getVerticalFadingEdgeLength();
            } else if (selectedBottom > childrenBottom) {
                selectedTop = (childrenBottom - selected.getMeasuredHeight()) - getVerticalFadingEdgeLength();
            }
        } else if (toPosition < firstPosition) {
            selectedPos = firstPosition;
            for (i = TRANSCRIPT_MODE_DISABLED; i < childCount; i += TRANSCRIPT_MODE_NORMAL) {
                top = getChildAt(i).getTop();
                if (i == 0) {
                    selectedTop = top;
                    if (firstPosition > 0 || top < childrenTop) {
                        childrenTop += getVerticalFadingEdgeLength();
                    }
                }
                if (top >= childrenTop) {
                    selectedPos = firstPosition + i;
                    selectedTop = top;
                    break;
                }
            }
        } else {
            int itemCount = this.mItemCount;
            down = PROFILE_SCROLLING;
            selectedPos = (firstPosition + childCount) + TOUCH_MODE_UNKNOWN;
            for (i = childCount + TOUCH_MODE_UNKNOWN; i >= 0; i += TOUCH_MODE_UNKNOWN) {
                View v = getChildAt(i);
                top = v.getTop();
                int bottom = v.getBottom();
                if (i == childCount + TOUCH_MODE_UNKNOWN) {
                    selectedTop = top;
                    if (firstPosition + childCount < itemCount || bottom > childrenBottom) {
                        childrenBottom -= getVerticalFadingEdgeLength();
                    }
                }
                if (bottom <= childrenBottom) {
                    selectedPos = firstPosition + i;
                    selectedTop = top;
                    break;
                }
            }
        }
        this.mResurrectToPosition = TOUCH_MODE_UNKNOWN;
        removeCallbacks(this.mFlingRunnable);
        if (this.mPositionScroller != null) {
            this.mPositionScroller.stop();
        }
        this.mTouchMode = TOUCH_MODE_UNKNOWN;
        clearScrollingCache();
        this.mSpecificTop = selectedTop;
        selectedPos = lookForSelectablePosition(selectedPos, down);
        if (selectedPos < firstPosition || selectedPos > getLastVisiblePosition()) {
            selectedPos = TOUCH_MODE_UNKNOWN;
        } else {
            this.mLayoutMode = TOUCH_MODE_FLING;
            updateSelectorState();
            setSelectionInt(selectedPos);
            invokeOnItemScrollListener();
        }
        reportScrollStateChange(TRANSCRIPT_MODE_DISABLED);
        if (selectedPos >= 0) {
            return true;
        }
        return PROFILE_SCROLLING;
    }

    void confirmCheckedPositionsById() {
        this.mCheckStates.clear();
        boolean checkedCountChanged = PROFILE_SCROLLING;
        int checkedIndex = TRANSCRIPT_MODE_DISABLED;
        while (checkedIndex < this.mCheckedIdStates.size()) {
            long id = this.mCheckedIdStates.keyAt(checkedIndex);
            int lastPos = ((Integer) this.mCheckedIdStates.valueAt(checkedIndex)).intValue();
            if (id != this.mAdapter.getItemId(lastPos)) {
                int start = Math.max(TRANSCRIPT_MODE_DISABLED, lastPos - 20);
                int end = Math.min(lastPos + CHECK_POSITION_SEARCH_DISTANCE, this.mItemCount);
                boolean found = PROFILE_SCROLLING;
                for (int searchPos = start; searchPos < end; searchPos += TRANSCRIPT_MODE_NORMAL) {
                    if (id == this.mAdapter.getItemId(searchPos)) {
                        found = true;
                        this.mCheckStates.put(searchPos, true);
                        this.mCheckedIdStates.setValueAt(checkedIndex, Integer.valueOf(searchPos));
                        break;
                    }
                }
                if (!found) {
                    this.mCheckedIdStates.delete(id);
                    checkedIndex += TOUCH_MODE_UNKNOWN;
                    this.mCheckedItemCount += TOUCH_MODE_UNKNOWN;
                    checkedCountChanged = true;
                    if (!(this.mChoiceActionMode == null || this.mMultiChoiceModeCallback == null)) {
                        this.mMultiChoiceModeCallback.onItemCheckedStateChanged(this.mChoiceActionMode, lastPos, id, PROFILE_SCROLLING);
                    }
                }
            } else {
                this.mCheckStates.put(lastPos, true);
            }
            checkedIndex += TRANSCRIPT_MODE_NORMAL;
        }
        if (checkedCountChanged && this.mChoiceActionMode != null) {
            this.mChoiceActionMode.invalidate();
        }
    }

    protected void handleDataChanged() {
        int i = TOUCH_MODE_SCROLL;
        int count = this.mItemCount;
        int lastHandledItemCount = this.mLastHandledItemCount;
        this.mLastHandledItemCount = this.mItemCount;
        if (!(this.mChoiceMode == 0 || this.mAdapter == null || !this.mAdapter.hasStableIds())) {
            confirmCheckedPositionsById();
        }
        this.mRecycler.clearTransientStateViews();
        if (count > 0) {
            int newPos;
            if (this.mNeedSync) {
                this.mNeedSync = PROFILE_SCROLLING;
                this.mPendingSync = null;
                if (this.mTranscriptMode == TRANSCRIPT_MODE_ALWAYS_SCROLL) {
                    this.mLayoutMode = TOUCH_MODE_SCROLL;
                    return;
                }
                if (this.mTranscriptMode == TRANSCRIPT_MODE_NORMAL) {
                    if (this.mForceTranscriptScroll) {
                        this.mForceTranscriptScroll = PROFILE_SCROLLING;
                        this.mLayoutMode = TOUCH_MODE_SCROLL;
                        return;
                    }
                    int childCount = getChildCount();
                    int listBottom = getHeight() - getPaddingBottom();
                    View lastChild = getChildAt(childCount + TOUCH_MODE_UNKNOWN);
                    int lastBottom;
                    if (lastChild != null) {
                        lastBottom = lastChild.getBottom();
                    } else {
                        lastBottom = listBottom;
                    }
                    if (this.mFirstPosition + childCount < lastHandledItemCount || lastBottom > listBottom) {
                        awakenScrollBars();
                    } else {
                        this.mLayoutMode = TOUCH_MODE_SCROLL;
                        return;
                    }
                }
                switch (this.mSyncMode) {
                    case TRANSCRIPT_MODE_DISABLED /*0*/:
                        if (isInTouchMode()) {
                            this.mLayoutMode = TOUCH_MODE_OVERSCROLL;
                            this.mSyncPosition = Math.min(Math.max(TRANSCRIPT_MODE_DISABLED, this.mSyncPosition), count + TOUCH_MODE_UNKNOWN);
                            return;
                        }
                        newPos = findSyncPosition();
                        if (newPos >= 0 && lookForSelectablePosition(newPos, true) == newPos) {
                            this.mSyncPosition = newPos;
                            if (this.mSyncHeight == ((long) getHeight())) {
                                this.mLayoutMode = TOUCH_MODE_OVERSCROLL;
                            } else {
                                this.mLayoutMode = TRANSCRIPT_MODE_ALWAYS_SCROLL;
                            }
                            setNextSelectedPositionInt(newPos);
                            return;
                        }
                    case TRANSCRIPT_MODE_NORMAL /*1*/:
                        this.mLayoutMode = TOUCH_MODE_OVERSCROLL;
                        this.mSyncPosition = Math.min(Math.max(TRANSCRIPT_MODE_DISABLED, this.mSyncPosition), count + TOUCH_MODE_UNKNOWN);
                        return;
                }
            }
            if (!isInTouchMode()) {
                newPos = getSelectedItemPosition();
                if (newPos >= count) {
                    newPos = count + TOUCH_MODE_UNKNOWN;
                }
                if (newPos < 0) {
                    newPos = TRANSCRIPT_MODE_DISABLED;
                }
                int selectablePos = lookForSelectablePosition(newPos, true);
                if (selectablePos >= 0) {
                    setNextSelectedPositionInt(selectablePos);
                    return;
                }
                selectablePos = lookForSelectablePosition(newPos, PROFILE_SCROLLING);
                if (selectablePos >= 0) {
                    setNextSelectedPositionInt(selectablePos);
                    return;
                }
            } else if (this.mResurrectToPosition >= 0) {
                return;
            }
        }
        if (!this.mStackFromBottom) {
            i = TRANSCRIPT_MODE_NORMAL;
        }
        this.mLayoutMode = i;
        this.mSelectedPosition = TOUCH_MODE_UNKNOWN;
        this.mSelectedRowId = Long.MIN_VALUE;
        this.mNextSelectedPosition = TOUCH_MODE_UNKNOWN;
        this.mNextSelectedRowId = Long.MIN_VALUE;
        this.mNeedSync = PROFILE_SCROLLING;
        this.mPendingSync = null;
        this.mSelectorPosition = TOUCH_MODE_UNKNOWN;
        checkSelectionChanged();
    }

    protected void onDisplayHint(int hint) {
        super.onDisplayHint(hint);
        switch (hint) {
            case TRANSCRIPT_MODE_DISABLED /*0*/:
                if (!(!this.mFiltered || this.mPopup == null || this.mPopup.isShowing())) {
                    showPopup();
                    break;
                }
            case TOUCH_MODE_FLING /*4*/:
                if (this.mPopup != null && this.mPopup.isShowing()) {
                    dismissPopup();
                    break;
                }
        }
        this.mPopupHidden = hint == TOUCH_MODE_FLING ? true : PROFILE_SCROLLING;
    }

    private void dismissPopup() {
        if (this.mPopup != null) {
            this.mPopup.dismiss();
        }
    }

    private void showPopup() {
        if (getWindowVisibility() == 0) {
            createTextFilter(true);
            positionPopup();
            checkFocus();
        }
    }

    private void positionPopup() {
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int[] xy = new int[TRANSCRIPT_MODE_ALWAYS_SCROLL];
        getLocationOnScreen(xy);
        int bottomGap = ((screenHeight - xy[TRANSCRIPT_MODE_NORMAL]) - getHeight()) + ((int) (this.mDensityScale * 20.0f));
        if (this.mPopup.isShowing()) {
            this.mPopup.update(xy[TRANSCRIPT_MODE_DISABLED], bottomGap, TOUCH_MODE_UNKNOWN, TOUCH_MODE_UNKNOWN);
        } else {
            this.mPopup.showAtLocation((View) this, 81, xy[TRANSCRIPT_MODE_DISABLED], bottomGap);
        }
    }

    static int getDistance(Rect source, Rect dest, int direction) {
        int sX;
        int sY;
        int dX;
        int dY;
        switch (direction) {
            case TRANSCRIPT_MODE_NORMAL /*1*/:
            case TRANSCRIPT_MODE_ALWAYS_SCROLL /*2*/:
                sX = source.right + (source.width() / TRANSCRIPT_MODE_ALWAYS_SCROLL);
                sY = source.top + (source.height() / TRANSCRIPT_MODE_ALWAYS_SCROLL);
                dX = dest.left + (dest.width() / TRANSCRIPT_MODE_ALWAYS_SCROLL);
                dY = dest.top + (dest.height() / TRANSCRIPT_MODE_ALWAYS_SCROLL);
                break;
            case TextViewDrawableColorFilterAction.TAG /*17*/:
                sX = source.left;
                sY = source.top + (source.height() / TRANSCRIPT_MODE_ALWAYS_SCROLL);
                dX = dest.right;
                dY = dest.top + (dest.height() / TRANSCRIPT_MODE_ALWAYS_SCROLL);
                break;
            case MotionEvent.AXIS_GENERIC_2 /*33*/:
                sX = source.left + (source.width() / TRANSCRIPT_MODE_ALWAYS_SCROLL);
                sY = source.top;
                dX = dest.left + (dest.width() / TRANSCRIPT_MODE_ALWAYS_SCROLL);
                dY = dest.bottom;
                break;
            case KeyEvent.KEYCODE_ENTER /*66*/:
                sX = source.right;
                sY = source.top + (source.height() / TRANSCRIPT_MODE_ALWAYS_SCROLL);
                dX = dest.left;
                dY = dest.top + (dest.height() / TRANSCRIPT_MODE_ALWAYS_SCROLL);
                break;
            case KeyEvent.KEYCODE_MEDIA_RECORD /*130*/:
                sX = source.left + (source.width() / TRANSCRIPT_MODE_ALWAYS_SCROLL);
                sY = source.bottom;
                dX = dest.left + (dest.width() / TRANSCRIPT_MODE_ALWAYS_SCROLL);
                dY = dest.top;
                break;
            default:
                throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT, FOCUS_FORWARD, FOCUS_BACKWARD}.");
        }
        int deltaX = dX - sX;
        int deltaY = dY - sY;
        return (deltaY * deltaY) + (deltaX * deltaX);
    }

    protected boolean isInFilterMode() {
        return this.mFiltered;
    }

    boolean sendToTextFilter(int keyCode, int count, KeyEvent event) {
        if (!acceptFilter()) {
            return PROFILE_SCROLLING;
        }
        boolean handled = PROFILE_SCROLLING;
        boolean okToSend = true;
        switch (keyCode) {
            case TOUCH_MODE_FLING /*4*/:
                if (this.mFiltered && this.mPopup != null && this.mPopup.isShowing()) {
                    if (event.getAction() == 0 && event.getRepeatCount() == 0) {
                        DispatcherState state = getKeyDispatcherState();
                        if (state != null) {
                            state.startTracking(event, this);
                        }
                        handled = true;
                    } else if (event.getAction() == TRANSCRIPT_MODE_NORMAL && event.isTracking() && !event.isCanceled()) {
                        handled = true;
                        this.mTextFilter.setText(ProxyInfo.LOCAL_EXCL_LIST);
                    }
                }
                okToSend = PROFILE_SCROLLING;
                break;
            case RelativeLayout.ALIGN_END /*19*/:
            case CHECK_POSITION_SEARCH_DISTANCE /*20*/:
            case RelativeLayout.ALIGN_PARENT_END /*21*/:
            case MotionEvent.AXIS_GAS /*22*/:
            case MotionEvent.AXIS_BRAKE /*23*/:
            case KeyEvent.KEYCODE_ENTER /*66*/:
                okToSend = PROFILE_SCROLLING;
                break;
            case KeyEvent.KEYCODE_SPACE /*62*/:
                okToSend = this.mFiltered;
                break;
        }
        if (!okToSend) {
            return handled;
        }
        createTextFilter(true);
        KeyEvent forwardEvent = event;
        if (forwardEvent.getRepeatCount() > 0) {
            forwardEvent = KeyEvent.changeTimeRepeat(event, event.getEventTime(), TRANSCRIPT_MODE_DISABLED);
        }
        switch (event.getAction()) {
            case TRANSCRIPT_MODE_DISABLED /*0*/:
                return this.mTextFilter.onKeyDown(keyCode, forwardEvent);
            case TRANSCRIPT_MODE_NORMAL /*1*/:
                return this.mTextFilter.onKeyUp(keyCode, forwardEvent);
            case TRANSCRIPT_MODE_ALWAYS_SCROLL /*2*/:
                return this.mTextFilter.onKeyMultiple(keyCode, count, event);
            default:
                return handled;
        }
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        if (!isTextFilterEnabled()) {
            return null;
        }
        if (this.mPublicInputConnection == null) {
            this.mDefInputConnection = new BaseInputConnection((View) this, (boolean) PROFILE_SCROLLING);
            this.mPublicInputConnection = new InputConnectionWrapper(this, outAttrs);
        }
        outAttrs.inputType = KeyEvent.KEYCODE_TV_POWER;
        outAttrs.imeOptions = TOUCH_MODE_OVERFLING;
        return this.mPublicInputConnection;
    }

    public boolean checkInputConnectionProxy(View view) {
        return view == this.mTextFilter ? true : PROFILE_SCROLLING;
    }

    private void createTextFilter(boolean animateEntrance) {
        if (this.mPopup == null) {
            PopupWindow p = new PopupWindow(getContext());
            p.setFocusable(PROFILE_SCROLLING);
            p.setTouchable(PROFILE_SCROLLING);
            p.setInputMethodMode(TRANSCRIPT_MODE_ALWAYS_SCROLL);
            p.setContentView(getTextFilterInput());
            p.setWidth(-2);
            p.setHeight(-2);
            p.setBackgroundDrawable(null);
            this.mPopup = p;
            getViewTreeObserver().addOnGlobalLayoutListener(this);
            this.mGlobalLayoutListenerAddedFilter = true;
        }
        if (animateEntrance) {
            this.mPopup.setAnimationStyle(16974556);
        } else {
            this.mPopup.setAnimationStyle(16974557);
        }
    }

    private EditText getTextFilterInput() {
        if (this.mTextFilter == null) {
            this.mTextFilter = (EditText) LayoutInflater.from(getContext()).inflate(17367270, null);
            this.mTextFilter.setRawInputType(KeyEvent.KEYCODE_TV_POWER);
            this.mTextFilter.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
            this.mTextFilter.addTextChangedListener(this);
        }
        return this.mTextFilter;
    }

    public void clearTextFilter() {
        if (this.mFiltered) {
            getTextFilterInput().setText(ProxyInfo.LOCAL_EXCL_LIST);
            this.mFiltered = PROFILE_SCROLLING;
            if (this.mPopup != null && this.mPopup.isShowing()) {
                dismissPopup();
            }
        }
    }

    public boolean hasTextFilter() {
        return this.mFiltered;
    }

    public void onGlobalLayout() {
        if (isShown()) {
            if (this.mFiltered && this.mPopup != null && !this.mPopup.isShowing() && !this.mPopupHidden) {
                showPopup();
            }
        } else if (this.mPopup != null && this.mPopup.isShowing()) {
            dismissPopup();
        }
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (isTextFilterEnabled()) {
            createTextFilter(true);
            int length = s.length();
            boolean showing = this.mPopup.isShowing();
            if (!showing && length > 0) {
                showPopup();
                this.mFiltered = true;
            } else if (showing && length == 0) {
                dismissPopup();
                this.mFiltered = PROFILE_SCROLLING;
            }
            if (this.mAdapter instanceof Filterable) {
                Filter f = ((Filterable) this.mAdapter).getFilter();
                if (f != null) {
                    f.filter(s, this);
                    return;
                }
                throw new IllegalStateException("You cannot call onTextChanged with a non filterable adapter");
            }
        }
    }

    public void afterTextChanged(Editable s) {
    }

    public void onFilterComplete(int count) {
        if (this.mSelectedPosition < 0 && count > 0) {
            this.mResurrectToPosition = TOUCH_MODE_UNKNOWN;
            resurrectSelection();
        }
    }

    protected android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(TOUCH_MODE_UNKNOWN, -2, TRANSCRIPT_MODE_DISABLED);
    }

    protected android.view.ViewGroup.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public LayoutParams m126generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public void setTranscriptMode(int mode) {
        this.mTranscriptMode = mode;
    }

    public int getTranscriptMode() {
        return this.mTranscriptMode;
    }

    public int getSolidColor() {
        return this.mCacheColorHint;
    }

    public void setCacheColorHint(int color) {
        if (color != this.mCacheColorHint) {
            this.mCacheColorHint = color;
            int count = getChildCount();
            for (int i = TRANSCRIPT_MODE_DISABLED; i < count; i += TRANSCRIPT_MODE_NORMAL) {
                getChildAt(i).setDrawingCacheBackgroundColor(color);
            }
            this.mRecycler.setCacheColorHint(color);
        }
    }

    @ExportedProperty(category = "drawing")
    public int getCacheColorHint() {
        return this.mCacheColorHint;
    }

    public void reclaimViews(List<View> views) {
        int childCount = getChildCount();
        RecyclerListener listener = this.mRecycler.mRecyclerListener;
        for (int i = TRANSCRIPT_MODE_DISABLED; i < childCount; i += TRANSCRIPT_MODE_NORMAL) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp != null && this.mRecycler.shouldRecycleViewType(lp.viewType)) {
                views.add(child);
                child.setAccessibilityDelegate(null);
                if (listener != null) {
                    listener.onMovedToScrapHeap(child);
                }
            }
        }
        this.mRecycler.reclaimScrapViews(views);
        removeAllViewsInLayout();
    }

    private void finishGlows() {
        if (this.mEdgeGlowTop != null) {
            this.mEdgeGlowTop.finish();
            this.mEdgeGlowBottom.finish();
        }
    }

    public void setRemoteViewsAdapter(Intent intent) {
        if (this.mRemoteAdapter == null || !new FilterComparison(intent).equals(new FilterComparison(this.mRemoteAdapter.getRemoteViewsServiceIntent()))) {
            this.mDeferNotifyDataSetChanged = PROFILE_SCROLLING;
            this.mRemoteAdapter = new RemoteViewsAdapter(getContext(), intent, this);
            if (this.mRemoteAdapter.isDataReady()) {
                setAdapter(this.mRemoteAdapter);
            }
        }
    }

    public void setRemoteViewsOnClickHandler(OnClickHandler handler) {
        if (this.mRemoteAdapter != null) {
            this.mRemoteAdapter.setRemoteViewsOnClickHandler(handler);
        }
    }

    public void deferNotifyDataSetChanged() {
        this.mDeferNotifyDataSetChanged = true;
    }

    public boolean onRemoteAdapterConnected() {
        if (this.mRemoteAdapter != this.mAdapter) {
            setAdapter(this.mRemoteAdapter);
            if (!this.mDeferNotifyDataSetChanged) {
                return PROFILE_SCROLLING;
            }
            this.mRemoteAdapter.notifyDataSetChanged();
            this.mDeferNotifyDataSetChanged = PROFILE_SCROLLING;
            return PROFILE_SCROLLING;
        } else if (this.mRemoteAdapter == null) {
            return PROFILE_SCROLLING;
        } else {
            this.mRemoteAdapter.superNotifyDataSetChanged();
            return true;
        }
    }

    public void onRemoteAdapterDisconnected() {
    }

    void setVisibleRangeHint(int start, int end) {
        if (this.mRemoteAdapter != null) {
            this.mRemoteAdapter.setVisibleRangeHint(start, end);
        }
    }

    public void setRecyclerListener(RecyclerListener listener) {
        this.mRecycler.mRecyclerListener = listener;
    }

    int getHeightForPosition(int position) {
        int firstVisiblePosition = getFirstVisiblePosition();
        int childCount = getChildCount();
        int index = position - firstVisiblePosition;
        if (index >= 0 && index < childCount) {
            return getChildAt(index).getHeight();
        }
        View view = obtainView(position, this.mIsScrap);
        view.measure(this.mWidthMeasureSpec, TRANSCRIPT_MODE_DISABLED);
        int height = view.getMeasuredHeight();
        this.mRecycler.addScrapView(view, position);
        return height;
    }

    public void setSelectionFromTop(int position, int y) {
        if (this.mAdapter != null) {
            if (isInTouchMode()) {
                this.mResurrectToPosition = position;
            } else {
                position = lookForSelectablePosition(position, true);
                if (position >= 0) {
                    setNextSelectedPositionInt(position);
                }
            }
            if (position >= 0) {
                this.mLayoutMode = TOUCH_MODE_FLING;
                this.mSpecificTop = this.mListPadding.top + y;
                if (this.mNeedSync) {
                    this.mSyncPosition = position;
                    this.mSyncRowId = this.mAdapter.getItemId(position);
                }
                if (this.mPositionScroller != null) {
                    this.mPositionScroller.stop();
                }
                requestLayout();
            }
        }
    }
}
