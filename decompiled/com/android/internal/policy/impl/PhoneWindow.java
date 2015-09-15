package com.android.internal.policy.impl;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.session.MediaController;
import android.media.session.MediaSessionLegacyHelper;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.AndroidRuntimeException;
import android.util.DisplayMetrics;
import android.util.EventLog;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.IRotationWatcher.Stub;
import android.view.IWindowManager;
import android.view.InputEvent;
import android.view.InputQueue;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.KeyEvent.DispatcherState;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder.Callback2;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewManager;
import android.view.ViewParent;
import android.view.ViewRootImpl;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.internal.R;
import com.android.internal.view.RootViewSurfaceTaker;
import com.android.internal.view.StandaloneActionMode;
import com.android.internal.view.menu.ContextMenuBuilder;
import com.android.internal.view.menu.IconMenuPresenter;
import com.android.internal.view.menu.ListMenuPresenter;
import com.android.internal.view.menu.MenuBuilder;
import com.android.internal.view.menu.MenuBuilder.Callback;
import com.android.internal.view.menu.MenuDialogHelper;
import com.android.internal.view.menu.MenuPresenter;
import com.android.internal.view.menu.MenuView;
import com.android.internal.widget.ActionBarContextView;
import com.android.internal.widget.BackgroundFallback;
import com.android.internal.widget.DecorContentParent;
import com.android.internal.widget.SwipeDismissLayout;
import com.android.internal.widget.SwipeDismissLayout.OnDismissedListener;
import com.android.internal.widget.SwipeDismissLayout.OnSwipeProgressChangedListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class PhoneWindow extends Window implements Callback {
    private static final String ACTION_BAR_TAG = "android:ActionBar";
    private static final int CUSTOM_TITLE_COMPATIBLE_FEATURES = 13505;
    private static final int DEFAULT_BACKGROUND_FADE_DURATION_MS = 300;
    static final int FLAG_RESOURCE_SET_ICON = 1;
    static final int FLAG_RESOURCE_SET_ICON_FALLBACK = 4;
    static final int FLAG_RESOURCE_SET_LOGO = 2;
    private static final String FOCUSED_ID_TAG = "android:focusedViewId";
    private static final String PANELS_TAG = "android:Panels";
    private static final boolean SWEEP_OPEN_MENU = false;
    private static final String TAG = "PhoneWindow";
    private static final Transition USE_DEFAULT_TRANSITION = null;
    private static final String VIEWS_TAG = "android:views";
    static final RotationWatcher sRotationWatcher = null;
    private ActionMenuPresenterCallback mActionMenuPresenterCallback;
    private Boolean mAllowEnterTransitionOverlap;
    private Boolean mAllowReturnTransitionOverlap;
    private boolean mAlwaysReadCloseOnTouchAttr;
    private AudioManager mAudioManager;
    private Drawable mBackgroundDrawable;
    private long mBackgroundFadeDurationMillis;
    private int mBackgroundFallbackResource;
    private int mBackgroundResource;
    private ProgressBar mCircularProgressBar;
    private boolean mClipToOutline;
    private boolean mClosingActionMenu;
    private ViewGroup mContentParent;
    private ViewGroup mContentRoot;
    private Scene mContentScene;
    private ContextMenuBuilder mContextMenu;
    final DialogMenuCallback mContextMenuCallback;
    private MenuDialogHelper mContextMenuHelper;
    private DecorView mDecor;
    private DecorContentParent mDecorContentParent;
    private DrawableFeatureState[] mDrawables;
    private float mElevation;
    private Transition mEnterTransition;
    private Transition mExitTransition;
    TypedValue mFixedHeightMajor;
    TypedValue mFixedHeightMinor;
    TypedValue mFixedWidthMajor;
    TypedValue mFixedWidthMinor;
    private boolean mForcedNavigationBarColor;
    private boolean mForcedStatusBarColor;
    private int mFrameResource;
    private ProgressBar mHorizontalProgressBar;
    int mIconRes;
    private int mInvalidatePanelMenuFeatures;
    private boolean mInvalidatePanelMenuPosted;
    private final Runnable mInvalidatePanelMenuRunnable;
    private boolean mIsFloating;
    private KeyguardManager mKeyguardManager;
    private LayoutInflater mLayoutInflater;
    private ImageView mLeftIconView;
    int mLogoRes;
    private MediaController mMediaController;
    final TypedValue mMinWidthMajor;
    final TypedValue mMinWidthMinor;
    private int mNavigationBarColor;
    int mOutsetBottomPx;
    private int mPanelChordingKey;
    private PanelMenuPresenterCallback mPanelMenuPresenterCallback;
    private PanelFeatureState[] mPanels;
    private PanelFeatureState mPreparedPanel;
    private Transition mReenterTransition;
    int mResourcesSetFlags;
    private Transition mReturnTransition;
    private ImageView mRightIconView;
    private Transition mSharedElementEnterTransition;
    private Transition mSharedElementExitTransition;
    private Transition mSharedElementReenterTransition;
    private Transition mSharedElementReturnTransition;
    private Boolean mSharedElementsUseOverlay;
    private int mStatusBarColor;
    InputQueue.Callback mTakeInputQueueCallback;
    Callback2 mTakeSurfaceCallback;
    private Rect mTempRect;
    private int mTextColor;
    private CharSequence mTitle;
    private int mTitleColor;
    private TextView mTitleView;
    private TransitionManager mTransitionManager;
    private int mUiOptions;
    private int mVolumeControlStreamType;

    private final class ActionMenuPresenterCallback implements MenuPresenter.Callback {
        final /* synthetic */ PhoneWindow this$0;

        private ActionMenuPresenterCallback(com.android.internal.policy.impl.PhoneWindow r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindow.ActionMenuPresenterCallback.<init>(com.android.internal.policy.impl.PhoneWindow):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindow.ActionMenuPresenterCallback.<init>(com.android.internal.policy.impl.PhoneWindow):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindow.ActionMenuPresenterCallback.<init>(com.android.internal.policy.impl.PhoneWindow):void");
        }

        /* synthetic */ ActionMenuPresenterCallback(com.android.internal.policy.impl.PhoneWindow r1, com.android.internal.policy.impl.PhoneWindow.AnonymousClass1 r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindow.ActionMenuPresenterCallback.<init>(com.android.internal.policy.impl.PhoneWindow, com.android.internal.policy.impl.PhoneWindow$1):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindow.ActionMenuPresenterCallback.<init>(com.android.internal.policy.impl.PhoneWindow, com.android.internal.policy.impl.PhoneWindow$1):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindow.ActionMenuPresenterCallback.<init>(com.android.internal.policy.impl.PhoneWindow, com.android.internal.policy.impl.PhoneWindow$1):void");
        }

        public void onCloseMenu(com.android.internal.view.menu.MenuBuilder r1, boolean r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindow.ActionMenuPresenterCallback.onCloseMenu(com.android.internal.view.menu.MenuBuilder, boolean):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindow.ActionMenuPresenterCallback.onCloseMenu(com.android.internal.view.menu.MenuBuilder, boolean):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindow.ActionMenuPresenterCallback.onCloseMenu(com.android.internal.view.menu.MenuBuilder, boolean):void");
        }

        public boolean onOpenSubMenu(com.android.internal.view.menu.MenuBuilder r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindow.ActionMenuPresenterCallback.onOpenSubMenu(com.android.internal.view.menu.MenuBuilder):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindow.ActionMenuPresenterCallback.onOpenSubMenu(com.android.internal.view.menu.MenuBuilder):boolean
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindow.ActionMenuPresenterCallback.onOpenSubMenu(com.android.internal.view.menu.MenuBuilder):boolean");
        }
    }

    private static class ColorViewState {
        final int hideWindowFlag;
        final int id;
        final int systemUiHideFlag;
        int targetVisibility;
        final String transitionName;
        final int translucentFlag;
        final int verticalGravity;
        View view;

        ColorViewState(int r1, int r2, int r3, java.lang.String r4, int r5, int r6) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindow.ColorViewState.<init>(int, int, int, java.lang.String, int, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindow.ColorViewState.<init>(int, int, int, java.lang.String, int, int):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindow.ColorViewState.<init>(int, int, int, java.lang.String, int, int):void");
        }
    }

    private final class DecorView extends FrameLayout implements RootViewSurfaceTaker {
        private ActionMode mActionMode;
        private PopupWindow mActionModePopup;
        private ActionBarContextView mActionModeView;
        private final BackgroundFallback mBackgroundFallback;
        private final Rect mBackgroundPadding;
        private final int mBarEnterExitDuration;
        private boolean mChanging;
        int mDefaultOpacity;
        private int mDownY;
        private final Rect mDrawingBounds;
        private final int mFeatureId;
        private final Rect mFrameOffsets;
        private final Rect mFramePadding;
        private final Interpolator mHideInterpolator;
        private int mLastBottomInset;
        private boolean mLastHasBottomStableInset;
        private boolean mLastHasTopStableInset;
        private int mLastRightInset;
        private int mLastTopInset;
        private int mLastWindowFlags;
        private Drawable mMenuBackground;
        private final ColorViewState mNavigationColorViewState;
        private View mNavigationGuard;
        private int mRootScrollY;
        private Runnable mShowActionModePopup;
        private final Interpolator mShowInterpolator;
        private final ColorViewState mStatusColorViewState;
        private View mStatusGuard;
        private boolean mWatchingForMenu;
        final /* synthetic */ PhoneWindow this$0;

        /* renamed from: com.android.internal.policy.impl.PhoneWindow.DecorView.1 */
        class AnonymousClass1 implements Runnable {
            final /* synthetic */ DecorView this$1;

            AnonymousClass1(com.android.internal.policy.impl.PhoneWindow.DecorView r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindow.DecorView.1.<init>(com.android.internal.policy.impl.PhoneWindow$DecorView):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindow.DecorView.1.<init>(com.android.internal.policy.impl.PhoneWindow$DecorView):void
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
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindow.DecorView.1.<init>(com.android.internal.policy.impl.PhoneWindow$DecorView):void");
            }

            public void run() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindow.DecorView.1.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindow.DecorView.1.run():void
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
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindow.DecorView.1.run():void");
            }
        }

        /* renamed from: com.android.internal.policy.impl.PhoneWindow.DecorView.2 */
        class AnonymousClass2 implements Runnable {
            final /* synthetic */ DecorView this$1;
            final /* synthetic */ ColorViewState val$state;

            AnonymousClass2(com.android.internal.policy.impl.PhoneWindow.DecorView r1, com.android.internal.policy.impl.PhoneWindow.ColorViewState r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindow.DecorView.2.<init>(com.android.internal.policy.impl.PhoneWindow$DecorView, com.android.internal.policy.impl.PhoneWindow$ColorViewState):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindow.DecorView.2.<init>(com.android.internal.policy.impl.PhoneWindow$DecorView, com.android.internal.policy.impl.PhoneWindow$ColorViewState):void
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
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindow.DecorView.2.<init>(com.android.internal.policy.impl.PhoneWindow$DecorView, com.android.internal.policy.impl.PhoneWindow$ColorViewState):void");
            }

            public void run() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindow.DecorView.2.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindow.DecorView.2.run():void
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
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindow.DecorView.2.run():void");
            }
        }

        private class ActionModeCallbackWrapper implements ActionMode.Callback {
            private ActionMode.Callback mWrapped;
            final /* synthetic */ DecorView this$1;

            public ActionModeCallbackWrapper(com.android.internal.policy.impl.PhoneWindow.DecorView r1, android.view.ActionMode.Callback r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindow.DecorView.ActionModeCallbackWrapper.<init>(com.android.internal.policy.impl.PhoneWindow$DecorView, android.view.ActionMode$Callback):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindow.DecorView.ActionModeCallbackWrapper.<init>(com.android.internal.policy.impl.PhoneWindow$DecorView, android.view.ActionMode$Callback):void
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
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindow.DecorView.ActionModeCallbackWrapper.<init>(com.android.internal.policy.impl.PhoneWindow$DecorView, android.view.ActionMode$Callback):void");
            }

            public boolean onActionItemClicked(android.view.ActionMode r1, android.view.MenuItem r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindow.DecorView.ActionModeCallbackWrapper.onActionItemClicked(android.view.ActionMode, android.view.MenuItem):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindow.DecorView.ActionModeCallbackWrapper.onActionItemClicked(android.view.ActionMode, android.view.MenuItem):boolean
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
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindow.DecorView.ActionModeCallbackWrapper.onActionItemClicked(android.view.ActionMode, android.view.MenuItem):boolean");
            }

            public boolean onCreateActionMode(android.view.ActionMode r1, android.view.Menu r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindow.DecorView.ActionModeCallbackWrapper.onCreateActionMode(android.view.ActionMode, android.view.Menu):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindow.DecorView.ActionModeCallbackWrapper.onCreateActionMode(android.view.ActionMode, android.view.Menu):boolean
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
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindow.DecorView.ActionModeCallbackWrapper.onCreateActionMode(android.view.ActionMode, android.view.Menu):boolean");
            }

            public void onDestroyActionMode(android.view.ActionMode r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindow.DecorView.ActionModeCallbackWrapper.onDestroyActionMode(android.view.ActionMode):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindow.DecorView.ActionModeCallbackWrapper.onDestroyActionMode(android.view.ActionMode):void
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
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindow.DecorView.ActionModeCallbackWrapper.onDestroyActionMode(android.view.ActionMode):void");
            }

            public boolean onPrepareActionMode(android.view.ActionMode r1, android.view.Menu r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindow.DecorView.ActionModeCallbackWrapper.onPrepareActionMode(android.view.ActionMode, android.view.Menu):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindow.DecorView.ActionModeCallbackWrapper.onPrepareActionMode(android.view.ActionMode, android.view.Menu):boolean
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
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindow.DecorView.ActionModeCallbackWrapper.onPrepareActionMode(android.view.ActionMode, android.view.Menu):boolean");
            }
        }

        public DecorView(PhoneWindow phoneWindow, Context context, int featureId) {
            this.this$0 = phoneWindow;
            super(context);
            this.mDefaultOpacity = -1;
            this.mDrawingBounds = new Rect();
            this.mBackgroundPadding = new Rect();
            this.mFramePadding = new Rect();
            this.mFrameOffsets = new Rect();
            this.mStatusColorViewState = new ColorViewState(PhoneWindow.FLAG_RESOURCE_SET_ICON_FALLBACK, 67108864, 48, "android:status:background", 16908335, 1024);
            this.mNavigationColorViewState = new ColorViewState(PhoneWindow.FLAG_RESOURCE_SET_LOGO, 134217728, 80, "android:navigation:background", 16908336, 0);
            this.mBackgroundFallback = new BackgroundFallback();
            this.mLastTopInset = 0;
            this.mLastBottomInset = 0;
            this.mLastRightInset = 0;
            this.mLastHasTopStableInset = PhoneWindow.SWEEP_OPEN_MENU;
            this.mLastHasBottomStableInset = PhoneWindow.SWEEP_OPEN_MENU;
            this.mLastWindowFlags = 0;
            this.mRootScrollY = 0;
            this.mFeatureId = featureId;
            this.mShowInterpolator = AnimationUtils.loadInterpolator(context, 17563662);
            this.mHideInterpolator = AnimationUtils.loadInterpolator(context, 17563663);
            this.mBarEnterExitDuration = context.getResources().getInteger(17694888);
        }

        public void setBackgroundFallback(int resId) {
            this.mBackgroundFallback.setDrawable(resId != 0 ? getContext().getDrawable(resId) : null);
            boolean z = (getBackground() != null || this.mBackgroundFallback.hasFallback()) ? PhoneWindow.SWEEP_OPEN_MENU : true;
            setWillNotDraw(z);
        }

        public void onDraw(Canvas c) {
            super.onDraw(c);
            this.mBackgroundFallback.draw(this.this$0.mContentRoot, c, this.this$0.mContentParent);
        }

        public boolean dispatchKeyEvent(KeyEvent event) {
            boolean isDown;
            int keyCode = event.getKeyCode();
            if (event.getAction() == 0) {
                isDown = true;
            } else {
                isDown = PhoneWindow.SWEEP_OPEN_MENU;
            }
            if (isDown && event.getRepeatCount() == 0) {
                if (this.this$0.mPanelChordingKey > 0 && this.this$0.mPanelChordingKey != keyCode && dispatchKeyShortcutEvent(event)) {
                    return true;
                }
                if (this.this$0.mPreparedPanel != null && this.this$0.mPreparedPanel.isOpen && this.this$0.performPanelShortcut(this.this$0.mPreparedPanel, keyCode, event, 0)) {
                    return true;
                }
            }
            if (!this.this$0.isDestroyed()) {
                Window.Callback cb = this.this$0.getCallback();
                boolean handled = (cb == null || this.mFeatureId >= 0) ? super.dispatchKeyEvent(event) : cb.dispatchKeyEvent(event);
                if (handled) {
                    return true;
                }
            }
            if (isDown) {
                return this.this$0.onKeyDown(this.mFeatureId, event.getKeyCode(), event);
            }
            return this.this$0.onKeyUp(this.mFeatureId, event.getKeyCode(), event);
        }

        public boolean dispatchKeyShortcutEvent(KeyEvent ev) {
            if (this.this$0.mPreparedPanel == null || !this.this$0.performPanelShortcut(this.this$0.mPreparedPanel, ev.getKeyCode(), ev, (int) PhoneWindow.FLAG_RESOURCE_SET_ICON)) {
                Window.Callback cb = this.this$0.getCallback();
                boolean handled = (cb == null || this.this$0.isDestroyed() || this.mFeatureId >= 0) ? super.dispatchKeyShortcutEvent(ev) : cb.dispatchKeyShortcutEvent(ev);
                if (handled) {
                    return true;
                }
                PanelFeatureState st = this.this$0.getPanelState(0, PhoneWindow.SWEEP_OPEN_MENU);
                if (st != null && this.this$0.mPreparedPanel == null) {
                    this.this$0.preparePanel(st, ev);
                    handled = this.this$0.performPanelShortcut(st, ev.getKeyCode(), ev, (int) PhoneWindow.FLAG_RESOURCE_SET_ICON);
                    st.isPrepared = PhoneWindow.SWEEP_OPEN_MENU;
                    if (handled) {
                        return true;
                    }
                }
                return PhoneWindow.SWEEP_OPEN_MENU;
            } else if (this.this$0.mPreparedPanel == null) {
                return true;
            } else {
                this.this$0.mPreparedPanel.isHandled = true;
                return true;
            }
        }

        public boolean dispatchTouchEvent(MotionEvent ev) {
            Window.Callback cb = this.this$0.getCallback();
            return (cb == null || this.this$0.isDestroyed() || this.mFeatureId >= 0) ? super.dispatchTouchEvent(ev) : cb.dispatchTouchEvent(ev);
        }

        public boolean dispatchTrackballEvent(MotionEvent ev) {
            Window.Callback cb = this.this$0.getCallback();
            return (cb == null || this.this$0.isDestroyed() || this.mFeatureId >= 0) ? super.dispatchTrackballEvent(ev) : cb.dispatchTrackballEvent(ev);
        }

        public boolean dispatchGenericMotionEvent(MotionEvent ev) {
            Window.Callback cb = this.this$0.getCallback();
            return (cb == null || this.this$0.isDestroyed() || this.mFeatureId >= 0) ? super.dispatchGenericMotionEvent(ev) : cb.dispatchGenericMotionEvent(ev);
        }

        public boolean superDispatchKeyEvent(KeyEvent event) {
            if (event.getKeyCode() == PhoneWindow.FLAG_RESOURCE_SET_ICON_FALLBACK) {
                int action = event.getAction();
                if (this.mActionMode != null) {
                    if (action != PhoneWindow.FLAG_RESOURCE_SET_ICON) {
                        return true;
                    }
                    this.mActionMode.finish();
                    return true;
                }
            }
            return super.dispatchKeyEvent(event);
        }

        public boolean superDispatchKeyShortcutEvent(KeyEvent event) {
            return super.dispatchKeyShortcutEvent(event);
        }

        public boolean superDispatchTouchEvent(MotionEvent event) {
            return super.dispatchTouchEvent(event);
        }

        public boolean superDispatchTrackballEvent(MotionEvent event) {
            return super.dispatchTrackballEvent(event);
        }

        public boolean superDispatchGenericMotionEvent(MotionEvent event) {
            return super.dispatchGenericMotionEvent(event);
        }

        public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
            if (this.this$0.mOutsetBottomPx != 0) {
                return super.dispatchApplyWindowInsets(insets.replaceSystemWindowInsets(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(), insets.getSystemWindowInsetRight(), this.this$0.mOutsetBottomPx));
            }
            return super.dispatchApplyWindowInsets(insets);
        }

        public boolean onTouchEvent(MotionEvent event) {
            return onInterceptTouchEvent(event);
        }

        private boolean isOutOfBounds(int x, int y) {
            return (x < -5 || y < -5 || x > getWidth() + 5 || y > getHeight() + 5) ? true : PhoneWindow.SWEEP_OPEN_MENU;
        }

        public boolean onInterceptTouchEvent(MotionEvent event) {
            int action = event.getAction();
            if (this.mFeatureId < 0 || action != 0 || !isOutOfBounds((int) event.getX(), (int) event.getY())) {
                return PhoneWindow.SWEEP_OPEN_MENU;
            }
            this.this$0.closePanel(this.mFeatureId);
            return true;
        }

        public void sendAccessibilityEvent(int eventType) {
            if (!AccessibilityManager.getInstance(this.mContext).isEnabled()) {
                return;
            }
            if ((this.mFeatureId == 0 || this.mFeatureId == 6 || this.mFeatureId == PhoneWindow.FLAG_RESOURCE_SET_LOGO || this.mFeatureId == 5) && getChildCount() == PhoneWindow.FLAG_RESOURCE_SET_ICON) {
                getChildAt(0).sendAccessibilityEvent(eventType);
            } else {
                super.sendAccessibilityEvent(eventType);
            }
        }

        public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
            Window.Callback cb = this.this$0.getCallback();
            if (cb == null || this.this$0.isDestroyed() || !cb.dispatchPopulateAccessibilityEvent(event)) {
                return super.dispatchPopulateAccessibilityEvent(event);
            }
            return true;
        }

        protected boolean setFrame(int l, int t, int r, int b) {
            boolean changed = super.setFrame(l, t, r, b);
            if (changed) {
                Rect drawingBounds = this.mDrawingBounds;
                getDrawingRect(drawingBounds);
                Drawable fg = getForeground();
                if (fg != null) {
                    Rect frameOffsets = this.mFrameOffsets;
                    drawingBounds.left += frameOffsets.left;
                    drawingBounds.top += frameOffsets.top;
                    drawingBounds.right -= frameOffsets.right;
                    drawingBounds.bottom -= frameOffsets.bottom;
                    fg.setBounds(drawingBounds);
                    Rect framePadding = this.mFramePadding;
                    drawingBounds.left += framePadding.left - frameOffsets.left;
                    drawingBounds.top += framePadding.top - frameOffsets.top;
                    drawingBounds.right -= framePadding.right - frameOffsets.right;
                    drawingBounds.bottom -= framePadding.bottom - frameOffsets.bottom;
                }
                Drawable bg = getBackground();
                if (bg != null) {
                    bg.setBounds(drawingBounds);
                }
            }
            return changed;
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int i;
            DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
            boolean isPortrait = metrics.widthPixels < metrics.heightPixels ? true : PhoneWindow.SWEEP_OPEN_MENU;
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            boolean fixedWidth = PhoneWindow.SWEEP_OPEN_MENU;
            if (widthMode == Integer.MIN_VALUE) {
                TypedValue tvw = isPortrait ? this.this$0.mFixedWidthMinor : this.this$0.mFixedWidthMajor;
                if (!(tvw == null || tvw.type == 0)) {
                    int w;
                    i = tvw.type;
                    if (r0 == 5) {
                        w = (int) tvw.getDimension(metrics);
                    } else {
                        i = tvw.type;
                        if (r0 == 6) {
                            w = (int) tvw.getFraction((float) metrics.widthPixels, (float) metrics.widthPixels);
                        } else {
                            w = 0;
                        }
                    }
                    if (w > 0) {
                        widthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.min(w, MeasureSpec.getSize(widthMeasureSpec)), 1073741824);
                        fixedWidth = true;
                    }
                }
            }
            if (heightMode == Integer.MIN_VALUE) {
                TypedValue tvh = isPortrait ? this.this$0.mFixedHeightMajor : this.this$0.mFixedHeightMinor;
                if (!(tvh == null || tvh.type == 0)) {
                    int h;
                    i = tvh.type;
                    if (r0 == 5) {
                        h = (int) tvh.getDimension(metrics);
                    } else {
                        i = tvh.type;
                        if (r0 == 6) {
                            h = (int) tvh.getFraction((float) metrics.heightPixels, (float) metrics.heightPixels);
                        } else {
                            h = 0;
                        }
                    }
                    if (h > 0) {
                        heightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.min(h, MeasureSpec.getSize(heightMeasureSpec)), 1073741824);
                    }
                }
            }
            if (this.this$0.mOutsetBottomPx != 0) {
                int mode = MeasureSpec.getMode(heightMeasureSpec);
                if (mode != 0) {
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(this.this$0.mOutsetBottomPx + MeasureSpec.getSize(heightMeasureSpec), mode);
                }
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int width = getMeasuredWidth();
            boolean measure = PhoneWindow.SWEEP_OPEN_MENU;
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, 1073741824);
            if (!fixedWidth && widthMode == Integer.MIN_VALUE) {
                TypedValue tv = isPortrait ? this.this$0.mMinWidthMinor : this.this$0.mMinWidthMajor;
                if (tv.type != 0) {
                    int min;
                    i = tv.type;
                    if (r0 == 5) {
                        min = (int) tv.getDimension(metrics);
                    } else {
                        i = tv.type;
                        if (r0 == 6) {
                            min = (int) tv.getFraction((float) metrics.widthPixels, (float) metrics.widthPixels);
                        } else {
                            min = 0;
                        }
                    }
                    if (width < min) {
                        widthMeasureSpec = MeasureSpec.makeMeasureSpec(min, 1073741824);
                        measure = true;
                    }
                }
            }
            if (measure) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }

        public void draw(Canvas canvas) {
            super.draw(canvas);
            if (this.mMenuBackground != null) {
                this.mMenuBackground.draw(canvas);
            }
        }

        public boolean showContextMenuForChild(View originalView) {
            if (this.this$0.mContextMenu == null) {
                this.this$0.mContextMenu = new ContextMenuBuilder(getContext());
                this.this$0.mContextMenu.setCallback(this.this$0.mContextMenuCallback);
            } else {
                this.this$0.mContextMenu.clearAll();
            }
            MenuDialogHelper helper = this.this$0.mContextMenu.show(originalView, originalView.getWindowToken());
            if (helper != null) {
                helper.setPresenterCallback(this.this$0.mContextMenuCallback);
            } else if (this.this$0.mContextMenuHelper != null) {
                this.this$0.mContextMenuHelper.dismiss();
            }
            this.this$0.mContextMenuHelper = helper;
            if (helper != null) {
                return true;
            }
            return PhoneWindow.SWEEP_OPEN_MENU;
        }

        public ActionMode startActionModeForChild(View originalView, ActionMode.Callback callback) {
            return startActionMode(callback);
        }

        public ActionMode startActionMode(ActionMode.Callback callback) {
            if (this.mActionMode != null) {
                this.mActionMode.finish();
            }
            ActionMode.Callback wrappedCallback = new ActionModeCallbackWrapper(this, callback);
            ActionMode mode = null;
            if (!(this.this$0.getCallback() == null || this.this$0.isDestroyed())) {
                try {
                    mode = this.this$0.getCallback().onWindowStartingActionMode(wrappedCallback);
                } catch (AbstractMethodError e) {
                }
            }
            if (mode != null) {
                this.mActionMode = mode;
            } else {
                if (this.mActionModeView == null) {
                    if (this.this$0.isFloating()) {
                        Context actionBarContext;
                        TypedValue outValue = new TypedValue();
                        Theme baseTheme = this.mContext.getTheme();
                        baseTheme.resolveAttribute(16843825, outValue, true);
                        if (outValue.resourceId != 0) {
                            Theme actionBarTheme = this.mContext.getResources().newTheme();
                            actionBarTheme.setTo(baseTheme);
                            actionBarTheme.applyStyle(outValue.resourceId, true);
                            actionBarContext = new ContextThemeWrapper(this.mContext, 0);
                            actionBarContext.getTheme().setTo(actionBarTheme);
                        } else {
                            actionBarContext = this.mContext;
                        }
                        this.mActionModeView = new ActionBarContextView(actionBarContext);
                        this.mActionModePopup = new PopupWindow(actionBarContext, null, 18219033);
                        this.mActionModePopup.setWindowLayoutType(PhoneWindow.FLAG_RESOURCE_SET_LOGO);
                        this.mActionModePopup.setContentView(this.mActionModeView);
                        this.mActionModePopup.setWidth(-1);
                        actionBarContext.getTheme().resolveAttribute(16843499, outValue, true);
                        this.mActionModeView.setContentHeight(TypedValue.complexToDimensionPixelSize(outValue.data, actionBarContext.getResources().getDisplayMetrics()));
                        this.mActionModePopup.setHeight(-2);
                        this.mShowActionModePopup = new AnonymousClass1(this);
                    } else {
                        ViewStub stub = (ViewStub) findViewById(16909171);
                        if (stub != null) {
                            this.mActionModeView = (ActionBarContextView) stub.inflate();
                        }
                    }
                }
                if (this.mActionModeView != null) {
                    boolean z;
                    this.mActionModeView.killMode();
                    Context context = this.mActionModeView.getContext();
                    ActionBarContextView actionBarContextView = this.mActionModeView;
                    if (this.mActionModePopup == null) {
                        z = true;
                    } else {
                        z = PhoneWindow.SWEEP_OPEN_MENU;
                    }
                    mode = new StandaloneActionMode(context, actionBarContextView, wrappedCallback, z);
                    if (callback.onCreateActionMode(mode, mode.getMenu())) {
                        mode.invalidate();
                        this.mActionModeView.initForMode(mode);
                        this.mActionModeView.setVisibility(0);
                        this.mActionMode = mode;
                        if (this.mActionModePopup != null) {
                            post(this.mShowActionModePopup);
                        }
                        this.mActionModeView.sendAccessibilityEvent(32);
                    } else {
                        this.mActionMode = null;
                    }
                }
            }
            if (!(this.mActionMode == null || this.this$0.getCallback() == null || this.this$0.isDestroyed())) {
                try {
                    this.this$0.getCallback().onActionModeStarted(this.mActionMode);
                } catch (AbstractMethodError e2) {
                }
            }
            return this.mActionMode;
        }

        public void startChanging() {
            this.mChanging = true;
        }

        public void finishChanging() {
            this.mChanging = PhoneWindow.SWEEP_OPEN_MENU;
            drawableChanged();
        }

        public void setWindowBackground(Drawable drawable) {
            if (getBackground() != drawable) {
                setBackgroundDrawable(drawable);
                if (drawable != null) {
                    drawable.getPadding(this.mBackgroundPadding);
                } else {
                    this.mBackgroundPadding.setEmpty();
                }
                drawableChanged();
            }
        }

        public void setBackgroundDrawable(Drawable d) {
            super.setBackgroundDrawable(d);
            if (getWindowToken() != null) {
                updateWindowResizeState();
            }
        }

        public void setWindowFrame(Drawable drawable) {
            if (getForeground() != drawable) {
                setForeground(drawable);
                if (drawable != null) {
                    drawable.getPadding(this.mFramePadding);
                } else {
                    this.mFramePadding.setEmpty();
                }
                drawableChanged();
            }
        }

        public void onWindowSystemUiVisibilityChanged(int visible) {
            updateColorViews(null, true);
        }

        public WindowInsets onApplyWindowInsets(WindowInsets insets) {
            this.mFrameOffsets.set(insets.getSystemWindowInsets());
            insets = updateStatusGuard(updateColorViews(insets, true));
            updateNavigationGuard(insets);
            if (getForeground() != null) {
                drawableChanged();
            }
            return insets;
        }

        public boolean isTransitionGroup() {
            return PhoneWindow.SWEEP_OPEN_MENU;
        }

        private WindowInsets updateColorViews(WindowInsets insets, boolean animate) {
            LayoutParams attrs = this.this$0.getAttributes();
            int sysUiVisibility = attrs.systemUiVisibility | getWindowSystemUiVisibility();
            if (!this.this$0.mIsFloating && ActivityManager.isHighEndGfx()) {
                boolean disallowAnimate = (!isLaidOut() ? true : PhoneWindow.SWEEP_OPEN_MENU) | (((this.mLastWindowFlags ^ attrs.flags) & Integer.MIN_VALUE) != 0 ? PhoneWindow.FLAG_RESOURCE_SET_ICON : 0);
                this.mLastWindowFlags = attrs.flags;
                if (insets != null) {
                    this.mLastTopInset = Math.min(insets.getStableInsetTop(), insets.getSystemWindowInsetTop());
                    this.mLastBottomInset = Math.min(insets.getStableInsetBottom(), insets.getSystemWindowInsetBottom());
                    this.mLastRightInset = Math.min(insets.getStableInsetRight(), insets.getSystemWindowInsetRight());
                    boolean hasTopStableInset = insets.getStableInsetTop() != 0 ? true : PhoneWindow.SWEEP_OPEN_MENU;
                    disallowAnimate |= hasTopStableInset != this.mLastHasTopStableInset ? PhoneWindow.FLAG_RESOURCE_SET_ICON : 0;
                    this.mLastHasTopStableInset = hasTopStableInset;
                    boolean hasBottomStableInset = insets.getStableInsetBottom() != 0 ? true : PhoneWindow.SWEEP_OPEN_MENU;
                    disallowAnimate |= hasBottomStableInset != this.mLastHasBottomStableInset ? PhoneWindow.FLAG_RESOURCE_SET_ICON : 0;
                    this.mLastHasBottomStableInset = hasBottomStableInset;
                }
                ColorViewState colorViewState = this.mStatusColorViewState;
                int access$1700 = this.this$0.mStatusBarColor;
                int i = this.mLastTopInset;
                boolean z = (!animate || disallowAnimate) ? PhoneWindow.SWEEP_OPEN_MENU : true;
                updateColorViewInt(colorViewState, sysUiVisibility, access$1700, i, z);
                colorViewState = this.mNavigationColorViewState;
                access$1700 = this.this$0.mNavigationBarColor;
                i = this.mLastBottomInset;
                z = (!animate || disallowAnimate) ? PhoneWindow.SWEEP_OPEN_MENU : true;
                updateColorViewInt(colorViewState, sysUiVisibility, access$1700, i, z);
            }
            boolean consumingNavBar = ((attrs.flags & Integer.MIN_VALUE) != 0 && (sysUiVisibility & 512) == 0 && (sysUiVisibility & PhoneWindow.FLAG_RESOURCE_SET_LOGO) == 0) ? true : PhoneWindow.SWEEP_OPEN_MENU;
            int consumedRight = consumingNavBar ? this.mLastRightInset : 0;
            int consumedBottom = consumingNavBar ? this.mLastBottomInset : 0;
            if (this.this$0.mContentRoot != null && (this.this$0.mContentRoot.getLayoutParams() instanceof MarginLayoutParams)) {
                MarginLayoutParams lp = (MarginLayoutParams) this.this$0.mContentRoot.getLayoutParams();
                if (!(lp.rightMargin == consumedRight && lp.bottomMargin == consumedBottom)) {
                    lp.rightMargin = consumedRight;
                    lp.bottomMargin = consumedBottom;
                    this.this$0.mContentRoot.setLayoutParams(lp);
                    if (insets == null) {
                        requestApplyInsets();
                    }
                }
                if (insets != null) {
                    insets = insets.replaceSystemWindowInsets(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(), insets.getSystemWindowInsetRight() - consumedRight, insets.getSystemWindowInsetBottom() - consumedBottom);
                }
            }
            if (insets != null) {
                return insets.consumeStableInsets();
            }
            return insets;
        }

        private void updateColorViewInt(ColorViewState state, int sysUiVis, int color, int height, boolean animate) {
            boolean show = (height <= 0 || (state.systemUiHideFlag & sysUiVis) != 0 || (this.this$0.getAttributes().flags & state.hideWindowFlag) != 0 || (this.this$0.getAttributes().flags & state.translucentFlag) != 0 || (-16777216 & color) == 0 || (this.this$0.getAttributes().flags & Integer.MIN_VALUE) == 0) ? PhoneWindow.SWEEP_OPEN_MENU : true;
            boolean visibilityChanged = PhoneWindow.SWEEP_OPEN_MENU;
            View view = state.view;
            if (view != null) {
                int vis = show ? 0 : PhoneWindow.FLAG_RESOURCE_SET_ICON_FALLBACK;
                visibilityChanged = state.targetVisibility != vis ? true : PhoneWindow.SWEEP_OPEN_MENU;
                state.targetVisibility = vis;
                if (show) {
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.getLayoutParams();
                    if (lp.height != height) {
                        lp.height = height;
                        view.setLayoutParams(lp);
                    }
                    view.setBackgroundColor(color);
                }
            } else if (show) {
                view = new View(this.mContext);
                state.view = view;
                view.setBackgroundColor(color);
                view.setTransitionName(state.transitionName);
                view.setId(state.id);
                visibilityChanged = true;
                view.setVisibility(PhoneWindow.FLAG_RESOURCE_SET_ICON_FALLBACK);
                state.targetVisibility = 0;
                addView(view, new FrameLayout.LayoutParams(-1, height, 8388611 | state.verticalGravity));
                updateColorViewTranslations();
            }
            if (visibilityChanged) {
                view.animate().cancel();
                if (!animate) {
                    view.setAlpha(1.0f);
                    view.setVisibility(show ? 0 : PhoneWindow.FLAG_RESOURCE_SET_ICON_FALLBACK);
                } else if (show) {
                    if (view.getVisibility() != 0) {
                        view.setVisibility(0);
                        view.setAlpha(0.0f);
                    }
                    view.animate().alpha(1.0f).setInterpolator(this.mShowInterpolator).setDuration((long) this.mBarEnterExitDuration);
                } else {
                    view.animate().alpha(0.0f).setInterpolator(this.mHideInterpolator).setDuration((long) this.mBarEnterExitDuration).withEndAction(new AnonymousClass2(this, state));
                }
            }
        }

        private void updateColorViewTranslations() {
            float f = 0.0f;
            int rootScrollY = this.mRootScrollY;
            if (this.mStatusColorViewState.view != null) {
                this.mStatusColorViewState.view.setTranslationY(rootScrollY > 0 ? (float) rootScrollY : 0.0f);
            }
            if (this.mNavigationColorViewState.view != null) {
                View view = this.mNavigationColorViewState.view;
                if (rootScrollY < 0) {
                    f = (float) rootScrollY;
                }
                view.setTranslationY(f);
            }
        }

        private WindowInsets updateStatusGuard(WindowInsets insets) {
            boolean showStatusGuard = PhoneWindow.SWEEP_OPEN_MENU;
            if (this.mActionModeView != null && (this.mActionModeView.getLayoutParams() instanceof MarginLayoutParams)) {
                MarginLayoutParams mlp = (MarginLayoutParams) this.mActionModeView.getLayoutParams();
                boolean mlpChanged = PhoneWindow.SWEEP_OPEN_MENU;
                if (this.mActionModeView.isShown()) {
                    if (this.this$0.mTempRect == null) {
                        this.this$0.mTempRect = new Rect();
                    }
                    Rect rect = this.this$0.mTempRect;
                    this.this$0.mContentParent.computeSystemWindowInsets(insets, rect);
                    if (mlp.topMargin != (rect.top == 0 ? insets.getSystemWindowInsetTop() : 0)) {
                        mlpChanged = true;
                        mlp.topMargin = insets.getSystemWindowInsetTop();
                        if (this.mStatusGuard == null) {
                            this.mStatusGuard = new View(this.mContext);
                            this.mStatusGuard.setBackgroundColor(this.mContext.getResources().getColor(17170482));
                            addView(this.mStatusGuard, indexOfChild(this.mStatusColorViewState.view), new FrameLayout.LayoutParams(-1, mlp.topMargin, 8388659));
                        } else {
                            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) this.mStatusGuard.getLayoutParams();
                            if (lp.height != mlp.topMargin) {
                                lp.height = mlp.topMargin;
                                this.mStatusGuard.setLayoutParams(lp);
                            }
                        }
                    }
                    showStatusGuard = this.mStatusGuard != null ? true : PhoneWindow.SWEEP_OPEN_MENU;
                    boolean z = (((this.this$0.getLocalFeatures() & 1024) == 0 ? true : PhoneWindow.SWEEP_OPEN_MENU) && showStatusGuard) ? true : PhoneWindow.SWEEP_OPEN_MENU;
                    insets = insets.consumeSystemWindowInsets(PhoneWindow.SWEEP_OPEN_MENU, z, PhoneWindow.SWEEP_OPEN_MENU, PhoneWindow.SWEEP_OPEN_MENU);
                } else if (mlp.topMargin != 0) {
                    mlpChanged = true;
                    mlp.topMargin = 0;
                }
                if (mlpChanged) {
                    this.mActionModeView.setLayoutParams(mlp);
                }
            }
            if (this.mStatusGuard != null) {
                this.mStatusGuard.setVisibility(showStatusGuard ? 0 : 8);
            }
            return insets;
        }

        private void updateNavigationGuard(WindowInsets insets) {
            if (this.this$0.getAttributes().type == 2011) {
                if (this.this$0.mContentParent != null && (this.this$0.mContentParent.getLayoutParams() instanceof MarginLayoutParams)) {
                    MarginLayoutParams mlp = (MarginLayoutParams) this.this$0.mContentParent.getLayoutParams();
                    mlp.bottomMargin = insets.getSystemWindowInsetBottom();
                    this.this$0.mContentParent.setLayoutParams(mlp);
                }
                if (this.mNavigationGuard == null) {
                    this.mNavigationGuard = new View(this.mContext);
                    this.mNavigationGuard.setBackgroundColor(this.mContext.getResources().getColor(17170482));
                    addView(this.mNavigationGuard, indexOfChild(this.mNavigationColorViewState.view), new FrameLayout.LayoutParams(-1, insets.getSystemWindowInsetBottom(), 8388691));
                    return;
                }
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) this.mNavigationGuard.getLayoutParams();
                lp.height = insets.getSystemWindowInsetBottom();
                this.mNavigationGuard.setLayoutParams(lp);
            }
        }

        private void drawableChanged() {
            if (!this.mChanging) {
                setPadding(this.mFramePadding.left + this.mBackgroundPadding.left, this.mFramePadding.top + this.mBackgroundPadding.top, this.mFramePadding.right + this.mBackgroundPadding.right, this.mFramePadding.bottom + this.mBackgroundPadding.bottom);
                requestLayout();
                invalidate();
                int opacity = -1;
                Drawable bg = getBackground();
                Drawable fg = getForeground();
                if (bg != null) {
                    if (fg == null) {
                        opacity = bg.getOpacity();
                    } else if (this.mFramePadding.left > 0 || this.mFramePadding.top > 0 || this.mFramePadding.right > 0 || this.mFramePadding.bottom > 0) {
                        opacity = -3;
                    } else {
                        int fop = fg.getOpacity();
                        int bop = bg.getOpacity();
                        if (fop == -1 || bop == -1) {
                            opacity = -1;
                        } else if (fop == 0) {
                            opacity = bop;
                        } else if (bop == 0) {
                            opacity = fop;
                        } else {
                            opacity = Drawable.resolveOpacity(fop, bop);
                        }
                    }
                }
                this.mDefaultOpacity = opacity;
                if (this.mFeatureId < 0) {
                    this.this$0.setDefaultWindowFormat(opacity);
                }
            }
        }

        public void onWindowFocusChanged(boolean hasWindowFocus) {
            super.onWindowFocusChanged(hasWindowFocus);
            if (!(!this.this$0.hasFeature(0) || hasWindowFocus || this.this$0.mPanelChordingKey == 0)) {
                this.this$0.closePanel(0);
            }
            Window.Callback cb = this.this$0.getCallback();
            if (cb != null && !this.this$0.isDestroyed() && this.mFeatureId < 0) {
                cb.onWindowFocusChanged(hasWindowFocus);
            }
        }

        void updateWindowResizeState() {
            Drawable bg = getBackground();
            boolean z = (bg == null || bg.getOpacity() != -1) ? true : PhoneWindow.SWEEP_OPEN_MENU;
            hackTurnOffWindowResizeAnim(z);
        }

        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateWindowResizeState();
            Window.Callback cb = this.this$0.getCallback();
            if (!(cb == null || this.this$0.isDestroyed() || this.mFeatureId >= 0)) {
                cb.onAttachedToWindow();
            }
            if (this.mFeatureId == -1) {
                this.this$0.openPanelsAfterRestore();
            }
        }

        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            Window.Callback cb = this.this$0.getCallback();
            if (cb != null && this.mFeatureId < 0) {
                cb.onDetachedFromWindow();
            }
            if (this.this$0.mDecorContentParent != null) {
                this.this$0.mDecorContentParent.dismissPopups();
            }
            if (this.mActionModePopup != null) {
                removeCallbacks(this.mShowActionModePopup);
                if (this.mActionModePopup.isShowing()) {
                    this.mActionModePopup.dismiss();
                }
                this.mActionModePopup = null;
            }
            PanelFeatureState st = this.this$0.getPanelState(0, PhoneWindow.SWEEP_OPEN_MENU);
            if (st != null && st.menu != null && this.mFeatureId < 0) {
                st.menu.close();
            }
        }

        public void onCloseSystemDialogs(String reason) {
            if (this.mFeatureId >= 0) {
                this.this$0.closeAllPanels();
            }
        }

        public Callback2 willYouTakeTheSurface() {
            return this.mFeatureId < 0 ? this.this$0.mTakeSurfaceCallback : null;
        }

        public InputQueue.Callback willYouTakeTheInputQueue() {
            return this.mFeatureId < 0 ? this.this$0.mTakeInputQueueCallback : null;
        }

        public void setSurfaceType(int type) {
            this.this$0.setType(type);
        }

        public void setSurfaceFormat(int format) {
            this.this$0.setFormat(format);
        }

        public void setSurfaceKeepScreenOn(boolean keepOn) {
            if (keepOn) {
                this.this$0.addFlags(128);
            } else {
                this.this$0.clearFlags(128);
            }
        }

        public void onRootViewScrollYChanged(int rootScrollY) {
            this.mRootScrollY = rootScrollY;
            updateColorViewTranslations();
        }
    }

    private final class DialogMenuCallback implements Callback, MenuPresenter.Callback {
        private int mFeatureId;
        private MenuDialogHelper mSubMenuHelper;
        final /* synthetic */ PhoneWindow this$0;

        public DialogMenuCallback(PhoneWindow phoneWindow, int featureId) {
            this.this$0 = phoneWindow;
            this.mFeatureId = featureId;
        }

        public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
            if (menu.getRootMenu() != menu) {
                onCloseSubMenu(menu);
            }
            if (allMenusAreClosing) {
                Window.Callback callback = this.this$0.getCallback();
                if (!(callback == null || this.this$0.isDestroyed())) {
                    callback.onPanelClosed(this.mFeatureId, menu);
                }
                if (menu == this.this$0.mContextMenu) {
                    this.this$0.dismissContextMenu();
                }
                if (this.mSubMenuHelper != null) {
                    this.mSubMenuHelper.dismiss();
                    this.mSubMenuHelper = null;
                }
            }
        }

        public void onCloseSubMenu(MenuBuilder menu) {
            Window.Callback callback = this.this$0.getCallback();
            if (callback != null && !this.this$0.isDestroyed()) {
                callback.onPanelClosed(this.mFeatureId, menu.getRootMenu());
            }
        }

        public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
            Window.Callback callback = this.this$0.getCallback();
            return (callback == null || this.this$0.isDestroyed() || !callback.onMenuItemSelected(this.mFeatureId, item)) ? PhoneWindow.SWEEP_OPEN_MENU : true;
        }

        public void onMenuModeChange(MenuBuilder menu) {
        }

        public boolean onOpenSubMenu(MenuBuilder subMenu) {
            if (subMenu == null) {
                return PhoneWindow.SWEEP_OPEN_MENU;
            }
            subMenu.setCallback(this);
            this.mSubMenuHelper = new MenuDialogHelper(subMenu);
            this.mSubMenuHelper.show(null);
            return true;
        }
    }

    private static final class DrawableFeatureState {
        int alpha;
        Drawable child;
        Drawable cur;
        int curAlpha;
        Drawable def;
        final int featureId;
        Drawable local;
        int resid;
        Uri uri;

        DrawableFeatureState(int r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindow.DrawableFeatureState.<init>(int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindow.DrawableFeatureState.<init>(int):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindow.DrawableFeatureState.<init>(int):void");
        }
    }

    private static final class PanelFeatureState {
        int background;
        View createdPanelView;
        DecorView decorView;
        int featureId;
        Bundle frozenActionViewState;
        Bundle frozenMenuState;
        int fullBackground;
        int gravity;
        IconMenuPresenter iconMenuPresenter;
        boolean isCompact;
        boolean isHandled;
        boolean isInExpandedMode;
        boolean isOpen;
        boolean isPrepared;
        ListMenuPresenter listMenuPresenter;
        int listPresenterTheme;
        MenuBuilder menu;
        public boolean qwertyMode;
        boolean refreshDecorView;
        boolean refreshMenuContent;
        View shownPanelView;
        boolean wasLastExpanded;
        boolean wasLastOpen;
        int windowAnimations;
        int x;
        int y;

        private static class SavedState implements Parcelable {
            public static final Creator<SavedState> CREATOR = null;
            int featureId;
            boolean isInExpandedMode;
            boolean isOpen;
            Bundle menuState;

            /* renamed from: com.android.internal.policy.impl.PhoneWindow.PanelFeatureState.SavedState.1 */
            static class AnonymousClass1 implements Creator<SavedState> {
                AnonymousClass1() {
                }

                public /* bridge */ /* synthetic */ Object m1createFromParcel(Parcel x0) {
                    return createFromParcel(x0);
                }

                public /* bridge */ /* synthetic */ Object[] m2newArray(int x0) {
                    return newArray(x0);
                }

                public SavedState createFromParcel(Parcel in) {
                    return SavedState.readFromParcel(in);
                }

                public SavedState[] newArray(int size) {
                    return new SavedState[size];
                }
            }

            private SavedState() {
            }

            /* synthetic */ SavedState(AnonymousClass1 x0) {
                this();
            }

            public int describeContents() {
                return 0;
            }

            public void writeToParcel(Parcel dest, int flags) {
                int i = PhoneWindow.FLAG_RESOURCE_SET_ICON;
                dest.writeInt(this.featureId);
                dest.writeInt(this.isOpen ? PhoneWindow.FLAG_RESOURCE_SET_ICON : 0);
                if (!this.isInExpandedMode) {
                    i = 0;
                }
                dest.writeInt(i);
                if (this.isOpen) {
                    dest.writeBundle(this.menuState);
                }
            }

            private static SavedState readFromParcel(Parcel source) {
                boolean z = true;
                SavedState savedState = new SavedState();
                savedState.featureId = source.readInt();
                savedState.isOpen = source.readInt() == PhoneWindow.FLAG_RESOURCE_SET_ICON ? true : PhoneWindow.SWEEP_OPEN_MENU;
                if (source.readInt() != PhoneWindow.FLAG_RESOURCE_SET_ICON) {
                    z = PhoneWindow.SWEEP_OPEN_MENU;
                }
                savedState.isInExpandedMode = z;
                if (savedState.isOpen) {
                    savedState.menuState = source.readBundle();
                }
                return savedState;
            }

            static {
                CREATOR = new AnonymousClass1();
            }
        }

        PanelFeatureState(int featureId) {
            this.featureId = featureId;
            this.refreshDecorView = PhoneWindow.SWEEP_OPEN_MENU;
        }

        public boolean isInListMode() {
            return (this.isInExpandedMode || this.isCompact) ? true : PhoneWindow.SWEEP_OPEN_MENU;
        }

        public boolean hasPanelItems() {
            if (this.shownPanelView == null) {
                return PhoneWindow.SWEEP_OPEN_MENU;
            }
            if (this.createdPanelView != null) {
                return true;
            }
            if (this.isCompact || this.isInExpandedMode) {
                return this.listMenuPresenter.getAdapter().getCount() > 0;
            } else if (((ViewGroup) this.shownPanelView).getChildCount() <= 0) {
                return PhoneWindow.SWEEP_OPEN_MENU;
            } else {
                return true;
            }
        }

        public void clearMenuPresenters() {
            if (this.menu != null) {
                this.menu.removeMenuPresenter(this.iconMenuPresenter);
                this.menu.removeMenuPresenter(this.listMenuPresenter);
            }
            this.iconMenuPresenter = null;
            this.listMenuPresenter = null;
        }

        void setStyle(Context context) {
            TypedArray a = context.obtainStyledAttributes(R.styleable.Theme);
            this.background = a.getResourceId(46, 0);
            this.fullBackground = a.getResourceId(47, 0);
            this.windowAnimations = a.getResourceId(93, 0);
            this.isCompact = a.getBoolean(280, PhoneWindow.SWEEP_OPEN_MENU);
            this.listPresenterTheme = a.getResourceId(282, 16974978);
            a.recycle();
        }

        void setMenu(MenuBuilder menu) {
            if (menu != this.menu) {
                if (this.menu != null) {
                    this.menu.removeMenuPresenter(this.iconMenuPresenter);
                    this.menu.removeMenuPresenter(this.listMenuPresenter);
                }
                this.menu = menu;
                if (menu != null) {
                    if (this.iconMenuPresenter != null) {
                        menu.addMenuPresenter(this.iconMenuPresenter);
                    }
                    if (this.listMenuPresenter != null) {
                        menu.addMenuPresenter(this.listMenuPresenter);
                    }
                }
            }
        }

        MenuView getListMenuView(Context context, MenuPresenter.Callback cb) {
            if (this.menu == null) {
                return null;
            }
            if (!this.isCompact) {
                getIconMenuView(context, cb);
            }
            if (this.listMenuPresenter == null) {
                this.listMenuPresenter = new ListMenuPresenter(17367144, this.listPresenterTheme);
                this.listMenuPresenter.setCallback(cb);
                this.listMenuPresenter.setId(16908350);
                this.menu.addMenuPresenter(this.listMenuPresenter);
            }
            if (this.iconMenuPresenter != null) {
                this.listMenuPresenter.setItemIndexOffset(this.iconMenuPresenter.getNumActualItemsShown());
            }
            return this.listMenuPresenter.getMenuView(this.decorView);
        }

        MenuView getIconMenuView(Context context, MenuPresenter.Callback cb) {
            if (this.menu == null) {
                return null;
            }
            if (this.iconMenuPresenter == null) {
                this.iconMenuPresenter = new IconMenuPresenter(context);
                this.iconMenuPresenter.setCallback(cb);
                this.iconMenuPresenter.setId(16908349);
                this.menu.addMenuPresenter(this.iconMenuPresenter);
            }
            return this.iconMenuPresenter.getMenuView(this.decorView);
        }

        Parcelable onSaveInstanceState() {
            SavedState savedState = new SavedState();
            savedState.featureId = this.featureId;
            savedState.isOpen = this.isOpen;
            savedState.isInExpandedMode = this.isInExpandedMode;
            if (this.menu != null) {
                savedState.menuState = new Bundle();
                this.menu.savePresenterStates(savedState.menuState);
            }
            return savedState;
        }

        void onRestoreInstanceState(Parcelable state) {
            SavedState savedState = (SavedState) state;
            this.featureId = savedState.featureId;
            this.wasLastOpen = savedState.isOpen;
            this.wasLastExpanded = savedState.isInExpandedMode;
            this.frozenMenuState = savedState.menuState;
            this.createdPanelView = null;
            this.shownPanelView = null;
            this.decorView = null;
        }

        void applyFrozenState() {
            if (this.menu != null && this.frozenMenuState != null) {
                this.menu.restorePresenterStates(this.frozenMenuState);
                this.frozenMenuState = null;
            }
        }
    }

    private class PanelMenuPresenterCallback implements MenuPresenter.Callback {
        final /* synthetic */ PhoneWindow this$0;

        private PanelMenuPresenterCallback(com.android.internal.policy.impl.PhoneWindow r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindow.PanelMenuPresenterCallback.<init>(com.android.internal.policy.impl.PhoneWindow):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindow.PanelMenuPresenterCallback.<init>(com.android.internal.policy.impl.PhoneWindow):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindow.PanelMenuPresenterCallback.<init>(com.android.internal.policy.impl.PhoneWindow):void");
        }

        /* synthetic */ PanelMenuPresenterCallback(com.android.internal.policy.impl.PhoneWindow r1, com.android.internal.policy.impl.PhoneWindow.AnonymousClass1 r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindow.PanelMenuPresenterCallback.<init>(com.android.internal.policy.impl.PhoneWindow, com.android.internal.policy.impl.PhoneWindow$1):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindow.PanelMenuPresenterCallback.<init>(com.android.internal.policy.impl.PhoneWindow, com.android.internal.policy.impl.PhoneWindow$1):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindow.PanelMenuPresenterCallback.<init>(com.android.internal.policy.impl.PhoneWindow, com.android.internal.policy.impl.PhoneWindow$1):void");
        }

        public void onCloseMenu(com.android.internal.view.menu.MenuBuilder r1, boolean r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindow.PanelMenuPresenterCallback.onCloseMenu(com.android.internal.view.menu.MenuBuilder, boolean):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindow.PanelMenuPresenterCallback.onCloseMenu(com.android.internal.view.menu.MenuBuilder, boolean):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindow.PanelMenuPresenterCallback.onCloseMenu(com.android.internal.view.menu.MenuBuilder, boolean):void");
        }

        public boolean onOpenSubMenu(com.android.internal.view.menu.MenuBuilder r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindow.PanelMenuPresenterCallback.onOpenSubMenu(com.android.internal.view.menu.MenuBuilder):boolean
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindow.PanelMenuPresenterCallback.onOpenSubMenu(com.android.internal.view.menu.MenuBuilder):boolean
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindow.PanelMenuPresenterCallback.onOpenSubMenu(com.android.internal.view.menu.MenuBuilder):boolean");
        }
    }

    static class RotationWatcher extends Stub {
        private Handler mHandler;
        private boolean mIsWatching;
        private final Runnable mRotationChanged;
        private final ArrayList<WeakReference<PhoneWindow>> mWindows;

        /* renamed from: com.android.internal.policy.impl.PhoneWindow.RotationWatcher.1 */
        class AnonymousClass1 implements Runnable {
            final /* synthetic */ RotationWatcher this$0;

            AnonymousClass1(RotationWatcher rotationWatcher) {
                this.this$0 = rotationWatcher;
            }

            public void run() {
                this.this$0.dispatchRotationChanged();
            }
        }

        RotationWatcher() {
            this.mRotationChanged = new AnonymousClass1(this);
            this.mWindows = new ArrayList();
        }

        public void onRotationChanged(int rotation) throws RemoteException {
            this.mHandler.post(this.mRotationChanged);
        }

        public void addWindow(PhoneWindow phoneWindow) {
            synchronized (this.mWindows) {
                if (!this.mIsWatching) {
                    try {
                        WindowManagerHolder.sWindowManager.watchRotation(this);
                        this.mHandler = new Handler();
                        this.mIsWatching = true;
                    } catch (RemoteException ex) {
                        Log.e(PhoneWindow.TAG, "Couldn't start watching for device rotation", ex);
                    }
                }
                this.mWindows.add(new WeakReference(phoneWindow));
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void removeWindow(com.android.internal.policy.impl.PhoneWindow r6) {
            /*
            r5 = this;
            r4 = r5.mWindows;
            monitor-enter(r4);
            r0 = 0;
        L_0x0004:
            r3 = r5.mWindows;	 Catch:{ all -> 0x0024 }
            r3 = r3.size();	 Catch:{ all -> 0x0024 }
            if (r0 >= r3) goto L_0x002a;
        L_0x000c:
            r3 = r5.mWindows;	 Catch:{ all -> 0x0024 }
            r1 = r3.get(r0);	 Catch:{ all -> 0x0024 }
            r1 = (java.lang.ref.WeakReference) r1;	 Catch:{ all -> 0x0024 }
            r2 = r1.get();	 Catch:{ all -> 0x0024 }
            r2 = (com.android.internal.policy.impl.PhoneWindow) r2;	 Catch:{ all -> 0x0024 }
            if (r2 == 0) goto L_0x001e;
        L_0x001c:
            if (r2 != r6) goto L_0x0027;
        L_0x001e:
            r3 = r5.mWindows;	 Catch:{ all -> 0x0024 }
            r3.remove(r0);	 Catch:{ all -> 0x0024 }
            goto L_0x0004;
        L_0x0024:
            r3 = move-exception;
            monitor-exit(r4);	 Catch:{ all -> 0x0024 }
            throw r3;
        L_0x0027:
            r0 = r0 + 1;
            goto L_0x0004;
        L_0x002a:
            monitor-exit(r4);	 Catch:{ all -> 0x0024 }
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindow.RotationWatcher.removeWindow(com.android.internal.policy.impl.PhoneWindow):void");
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        void dispatchRotationChanged() {
            /*
            r5 = this;
            r4 = r5.mWindows;
            monitor-enter(r4);
            r0 = 0;
        L_0x0004:
            r3 = r5.mWindows;	 Catch:{ all -> 0x0028 }
            r3 = r3.size();	 Catch:{ all -> 0x0028 }
            if (r0 >= r3) goto L_0x002b;
        L_0x000c:
            r3 = r5.mWindows;	 Catch:{ all -> 0x0028 }
            r1 = r3.get(r0);	 Catch:{ all -> 0x0028 }
            r1 = (java.lang.ref.WeakReference) r1;	 Catch:{ all -> 0x0028 }
            r2 = r1.get();	 Catch:{ all -> 0x0028 }
            r2 = (com.android.internal.policy.impl.PhoneWindow) r2;	 Catch:{ all -> 0x0028 }
            if (r2 == 0) goto L_0x0022;
        L_0x001c:
            r2.onOptionsPanelRotationChanged();	 Catch:{ all -> 0x0028 }
            r0 = r0 + 1;
            goto L_0x0004;
        L_0x0022:
            r3 = r5.mWindows;	 Catch:{ all -> 0x0028 }
            r3.remove(r0);	 Catch:{ all -> 0x0028 }
            goto L_0x0004;
        L_0x0028:
            r3 = move-exception;
            monitor-exit(r4);	 Catch:{ all -> 0x0028 }
            throw r3;
        L_0x002b:
            monitor-exit(r4);	 Catch:{ all -> 0x0028 }
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindow.RotationWatcher.dispatchRotationChanged():void");
        }
    }

    static class WindowManagerHolder {
        static final IWindowManager sWindowManager = null;

        WindowManagerHolder() {
        }

        static {
            sWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        }
    }

    static {
        USE_DEFAULT_TRANSITION = new TransitionSet();
        sRotationWatcher = new RotationWatcher();
    }

    public PhoneWindow(Context context) {
        super(context);
        this.mContextMenuCallback = new DialogMenuCallback(this, 6);
        this.mMinWidthMajor = new TypedValue();
        this.mMinWidthMinor = new TypedValue();
        this.mBackgroundResource = 0;
        this.mBackgroundFallbackResource = 0;
        this.mFrameResource = 0;
        this.mTextColor = 0;
        this.mStatusBarColor = 0;
        this.mNavigationBarColor = 0;
        this.mForcedStatusBarColor = SWEEP_OPEN_MENU;
        this.mForcedNavigationBarColor = SWEEP_OPEN_MENU;
        this.mTitle = null;
        this.mTitleColor = 0;
        this.mAlwaysReadCloseOnTouchAttr = SWEEP_OPEN_MENU;
        this.mVolumeControlStreamType = Integer.MIN_VALUE;
        this.mUiOptions = 0;
        this.mInvalidatePanelMenuRunnable = new Runnable() {
            public void run() {
                for (int i = 0; i <= 13; i += PhoneWindow.FLAG_RESOURCE_SET_ICON) {
                    if ((PhoneWindow.this.mInvalidatePanelMenuFeatures & (PhoneWindow.FLAG_RESOURCE_SET_ICON << i)) != 0) {
                        PhoneWindow.this.doInvalidatePanelMenu(i);
                    }
                }
                PhoneWindow.this.mInvalidatePanelMenuPosted = PhoneWindow.SWEEP_OPEN_MENU;
                PhoneWindow.this.mInvalidatePanelMenuFeatures = 0;
            }
        };
        this.mEnterTransition = null;
        this.mReturnTransition = USE_DEFAULT_TRANSITION;
        this.mExitTransition = null;
        this.mReenterTransition = USE_DEFAULT_TRANSITION;
        this.mSharedElementEnterTransition = null;
        this.mSharedElementReturnTransition = USE_DEFAULT_TRANSITION;
        this.mSharedElementExitTransition = null;
        this.mSharedElementReenterTransition = USE_DEFAULT_TRANSITION;
        this.mBackgroundFadeDurationMillis = -1;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public final void setContainer(Window container) {
        super.setContainer(container);
    }

    public boolean requestFeature(int featureId) {
        if (this.mContentParent != null) {
            throw new AndroidRuntimeException("requestFeature() must be called before adding content");
        }
        int features = getFeatures();
        int newFeatures = features | (FLAG_RESOURCE_SET_ICON << featureId);
        if ((newFeatures & 128) != 0 && (newFeatures & -13506) != 0) {
            throw new AndroidRuntimeException("You cannot combine custom titles with other title features");
        } else if ((features & FLAG_RESOURCE_SET_LOGO) != 0 && featureId == 8) {
            return SWEEP_OPEN_MENU;
        } else {
            if ((features & 256) != 0 && featureId == FLAG_RESOURCE_SET_ICON) {
                removeFeature(8);
            }
            if ((features & 256) != 0 && featureId == 11) {
                throw new AndroidRuntimeException("You cannot combine swipe dismissal and the action bar.");
            } else if ((features & 2048) != 0 && featureId == 8) {
                throw new AndroidRuntimeException("You cannot combine swipe dismissal and the action bar.");
            } else if (featureId != 5 || !getContext().getPackageManager().hasSystemFeature("android.hardware.type.watch")) {
                return super.requestFeature(featureId);
            } else {
                throw new AndroidRuntimeException("You cannot use indeterminate progress on a watch.");
            }
        }
    }

    public void setUiOptions(int uiOptions) {
        this.mUiOptions = uiOptions;
    }

    public void setUiOptions(int uiOptions, int mask) {
        this.mUiOptions = (this.mUiOptions & (mask ^ -1)) | (uiOptions & mask);
    }

    public TransitionManager getTransitionManager() {
        return this.mTransitionManager;
    }

    public void setTransitionManager(TransitionManager tm) {
        this.mTransitionManager = tm;
    }

    public Scene getContentScene() {
        return this.mContentScene;
    }

    public void setContentView(int layoutResID) {
        if (this.mContentParent == null) {
            installDecor();
        } else if (!hasFeature(12)) {
            this.mContentParent.removeAllViews();
        }
        if (hasFeature(12)) {
            transitionTo(Scene.getSceneForLayout(this.mContentParent, layoutResID, getContext()));
        } else {
            this.mLayoutInflater.inflate(layoutResID, this.mContentParent);
        }
        Window.Callback cb = getCallback();
        if (cb != null && !isDestroyed()) {
            cb.onContentChanged();
        }
    }

    public void setContentView(View view) {
        setContentView(view, new ViewGroup.LayoutParams(-1, -1));
    }

    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (this.mContentParent == null) {
            installDecor();
        } else if (!hasFeature(12)) {
            this.mContentParent.removeAllViews();
        }
        if (hasFeature(12)) {
            view.setLayoutParams(params);
            transitionTo(new Scene(this.mContentParent, view));
        } else {
            this.mContentParent.addView(view, params);
        }
        Window.Callback cb = getCallback();
        if (cb != null && !isDestroyed()) {
            cb.onContentChanged();
        }
    }

    public void addContentView(View view, ViewGroup.LayoutParams params) {
        if (this.mContentParent == null) {
            installDecor();
        }
        if (hasFeature(12)) {
            Log.v(TAG, "addContentView does not support content transitions");
        }
        this.mContentParent.addView(view, params);
        Window.Callback cb = getCallback();
        if (cb != null && !isDestroyed()) {
            cb.onContentChanged();
        }
    }

    private void transitionTo(Scene scene) {
        if (this.mContentScene == null) {
            scene.enter();
        } else {
            this.mTransitionManager.transitionTo(scene);
        }
        this.mContentScene = scene;
    }

    public View getCurrentFocus() {
        return this.mDecor != null ? this.mDecor.findFocus() : null;
    }

    public void takeSurface(Callback2 callback) {
        this.mTakeSurfaceCallback = callback;
    }

    public void takeInputQueue(InputQueue.Callback callback) {
        this.mTakeInputQueueCallback = callback;
    }

    public boolean isFloating() {
        return this.mIsFloating;
    }

    public LayoutInflater getLayoutInflater() {
        return this.mLayoutInflater;
    }

    public void setTitle(CharSequence title) {
        if (this.mTitleView != null) {
            this.mTitleView.setText(title);
        } else if (this.mDecorContentParent != null) {
            this.mDecorContentParent.setWindowTitle(title);
        }
        this.mTitle = title;
    }

    @Deprecated
    public void setTitleColor(int textColor) {
        if (this.mTitleView != null) {
            this.mTitleView.setTextColor(textColor);
        }
        this.mTitleColor = textColor;
    }

    public final boolean preparePanel(PanelFeatureState st, KeyEvent event) {
        if (isDestroyed()) {
            return SWEEP_OPEN_MENU;
        }
        if (st.isPrepared) {
            return true;
        }
        boolean isActionBarMenu;
        if (!(this.mPreparedPanel == null || this.mPreparedPanel == st)) {
            closePanel(this.mPreparedPanel, SWEEP_OPEN_MENU);
        }
        Window.Callback cb = getCallback();
        if (cb != null) {
            st.createdPanelView = cb.onCreatePanelView(st.featureId);
        }
        if (st.featureId == 0 || st.featureId == 8) {
            isActionBarMenu = true;
        } else {
            isActionBarMenu = SWEEP_OPEN_MENU;
        }
        if (isActionBarMenu && this.mDecorContentParent != null) {
            this.mDecorContentParent.setMenuPrepared();
        }
        if (st.createdPanelView == null) {
            if (st.menu == null || st.refreshMenuContent) {
                if (st.menu == null && (!initializePanelMenu(st) || st.menu == null)) {
                    return SWEEP_OPEN_MENU;
                }
                if (isActionBarMenu && this.mDecorContentParent != null) {
                    if (this.mActionMenuPresenterCallback == null) {
                        this.mActionMenuPresenterCallback = new ActionMenuPresenterCallback();
                    }
                    this.mDecorContentParent.setMenu(st.menu, this.mActionMenuPresenterCallback);
                }
                st.menu.stopDispatchingItemsChanged();
                if (cb == null || !cb.onCreatePanelMenu(st.featureId, st.menu)) {
                    st.setMenu(null);
                    if (!isActionBarMenu || this.mDecorContentParent == null) {
                        return SWEEP_OPEN_MENU;
                    }
                    this.mDecorContentParent.setMenu(null, this.mActionMenuPresenterCallback);
                    return SWEEP_OPEN_MENU;
                }
                st.refreshMenuContent = SWEEP_OPEN_MENU;
            }
            st.menu.stopDispatchingItemsChanged();
            if (st.frozenActionViewState != null) {
                st.menu.restoreActionViewStates(st.frozenActionViewState);
                st.frozenActionViewState = null;
            }
            if (cb.onPreparePanel(st.featureId, st.createdPanelView, st.menu)) {
                boolean z;
                if (KeyCharacterMap.load(event != null ? event.getDeviceId() : -1).getKeyboardType() != FLAG_RESOURCE_SET_ICON) {
                    z = true;
                } else {
                    z = SWEEP_OPEN_MENU;
                }
                st.qwertyMode = z;
                st.menu.setQwertyMode(st.qwertyMode);
                st.menu.startDispatchingItemsChanged();
            } else {
                if (isActionBarMenu && this.mDecorContentParent != null) {
                    this.mDecorContentParent.setMenu(null, this.mActionMenuPresenterCallback);
                }
                st.menu.startDispatchingItemsChanged();
                return SWEEP_OPEN_MENU;
            }
        }
        st.isPrepared = true;
        st.isHandled = SWEEP_OPEN_MENU;
        this.mPreparedPanel = st;
        return true;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (this.mDecorContentParent == null) {
            PanelFeatureState st = getPanelState(0, SWEEP_OPEN_MENU);
            if (st != null && st.menu != null) {
                if (st.isOpen) {
                    Bundle state = new Bundle();
                    if (st.iconMenuPresenter != null) {
                        st.iconMenuPresenter.saveHierarchyState(state);
                    }
                    if (st.listMenuPresenter != null) {
                        st.listMenuPresenter.saveHierarchyState(state);
                    }
                    clearMenuViews(st);
                    reopenMenu(SWEEP_OPEN_MENU);
                    if (st.iconMenuPresenter != null) {
                        st.iconMenuPresenter.restoreHierarchyState(state);
                    }
                    if (st.listMenuPresenter != null) {
                        st.listMenuPresenter.restoreHierarchyState(state);
                        return;
                    }
                    return;
                }
                clearMenuViews(st);
            }
        }
    }

    private static void clearMenuViews(PanelFeatureState st) {
        st.createdPanelView = null;
        st.refreshDecorView = true;
        st.clearMenuPresenters();
    }

    public final void openPanel(int featureId, KeyEvent event) {
        if (featureId != 0 || this.mDecorContentParent == null || !this.mDecorContentParent.canShowOverflowMenu() || ViewConfiguration.get(getContext()).hasPermanentMenuKey()) {
            openPanel(getPanelState(featureId, true), event);
        } else {
            this.mDecorContentParent.showOverflowMenu();
        }
    }

    private void openPanel(PanelFeatureState st, KeyEvent event) {
        if (!st.isOpen && !isDestroyed()) {
            if (st.featureId == 0) {
                Context context = getContext();
                boolean isXLarge = (context.getResources().getConfiguration().screenLayout & 15) == FLAG_RESOURCE_SET_ICON_FALLBACK ? true : SWEEP_OPEN_MENU;
                boolean isHoneycombApp = context.getApplicationInfo().targetSdkVersion >= 11 ? true : SWEEP_OPEN_MENU;
                if (isXLarge && isHoneycombApp) {
                    return;
                }
            }
            Window.Callback cb = getCallback();
            if (cb == null || cb.onMenuOpened(st.featureId, st.menu)) {
                WindowManager wm = getWindowManager();
                if (wm != null && preparePanel(st, event)) {
                    LayoutParams lp;
                    int width = -2;
                    if (st.decorView == null || st.refreshDecorView) {
                        if (st.decorView == null) {
                            if (!initializePanelDecor(st) || st.decorView == null) {
                                return;
                            }
                        } else if (st.refreshDecorView && st.decorView.getChildCount() > 0) {
                            st.decorView.removeAllViews();
                        }
                        if (initializePanelContent(st) && st.hasPanelItems()) {
                            int backgroundResId;
                            ViewGroup.LayoutParams lp2 = st.shownPanelView.getLayoutParams();
                            if (lp2 == null) {
                                lp2 = new ViewGroup.LayoutParams(-2, -2);
                            }
                            if (lp2.width == -1) {
                                backgroundResId = st.fullBackground;
                                width = -1;
                            } else {
                                backgroundResId = st.background;
                            }
                            st.decorView.setWindowBackground(getContext().getDrawable(backgroundResId));
                            ViewParent shownPanelParent = st.shownPanelView.getParent();
                            if (shownPanelParent != null && (shownPanelParent instanceof ViewGroup)) {
                                ((ViewGroup) shownPanelParent).removeView(st.shownPanelView);
                            }
                            st.decorView.addView(st.shownPanelView, lp2);
                            if (!st.shownPanelView.hasFocus()) {
                                st.shownPanelView.requestFocus();
                            }
                        } else {
                            return;
                        }
                    } else if (!st.isInListMode()) {
                        width = -1;
                    } else if (st.createdPanelView != null) {
                        lp = st.createdPanelView.getLayoutParams();
                        if (lp != null && lp.width == -1) {
                            width = -1;
                        }
                    }
                    st.isHandled = SWEEP_OPEN_MENU;
                    lp = new LayoutParams(width, -2, st.x, st.y, 1003, 8519680, st.decorView.mDefaultOpacity);
                    if (st.isCompact) {
                        lp.gravity = getOptionsPanelGravity();
                        sRotationWatcher.addWindow(this);
                    } else {
                        lp.gravity = st.gravity;
                    }
                    lp.windowAnimations = st.windowAnimations;
                    wm.addView(st.decorView, lp);
                    st.isOpen = true;
                    return;
                }
                return;
            }
            closePanel(st, true);
        }
    }

    public final void closePanel(int featureId) {
        if (featureId == 0 && this.mDecorContentParent != null && this.mDecorContentParent.canShowOverflowMenu() && !ViewConfiguration.get(getContext()).hasPermanentMenuKey()) {
            this.mDecorContentParent.hideOverflowMenu();
        } else if (featureId == 6) {
            closeContextMenu();
        } else {
            closePanel(getPanelState(featureId, true), true);
        }
    }

    public final void closePanel(PanelFeatureState st, boolean doCallback) {
        if (doCallback && st.featureId == 0 && this.mDecorContentParent != null && this.mDecorContentParent.isOverflowMenuShowing()) {
            checkCloseActionMenu(st.menu);
            return;
        }
        ViewManager wm = getWindowManager();
        if (wm != null && st.isOpen) {
            if (st.decorView != null) {
                wm.removeView(st.decorView);
                if (st.isCompact) {
                    sRotationWatcher.removeWindow(this);
                }
            }
            if (doCallback) {
                callOnPanelClosed(st.featureId, st, null);
            }
        }
        st.isPrepared = SWEEP_OPEN_MENU;
        st.isHandled = SWEEP_OPEN_MENU;
        st.isOpen = SWEEP_OPEN_MENU;
        st.shownPanelView = null;
        if (st.isInExpandedMode) {
            st.refreshDecorView = true;
            st.isInExpandedMode = SWEEP_OPEN_MENU;
        }
        if (this.mPreparedPanel == st) {
            this.mPreparedPanel = null;
            this.mPanelChordingKey = 0;
        }
    }

    void checkCloseActionMenu(Menu menu) {
        if (!this.mClosingActionMenu) {
            this.mClosingActionMenu = true;
            this.mDecorContentParent.dismissPopups();
            Window.Callback cb = getCallback();
            if (!(cb == null || isDestroyed())) {
                cb.onPanelClosed(8, menu);
            }
            this.mClosingActionMenu = SWEEP_OPEN_MENU;
        }
    }

    public final void togglePanel(int featureId, KeyEvent event) {
        PanelFeatureState st = getPanelState(featureId, true);
        if (st.isOpen) {
            closePanel(st, true);
        } else {
            openPanel(st, event);
        }
    }

    public void invalidatePanelMenu(int featureId) {
        this.mInvalidatePanelMenuFeatures |= FLAG_RESOURCE_SET_ICON << featureId;
        if (!this.mInvalidatePanelMenuPosted && this.mDecor != null) {
            this.mDecor.postOnAnimation(this.mInvalidatePanelMenuRunnable);
            this.mInvalidatePanelMenuPosted = true;
        }
    }

    void doPendingInvalidatePanelMenu() {
        if (this.mInvalidatePanelMenuPosted) {
            this.mDecor.removeCallbacks(this.mInvalidatePanelMenuRunnable);
            this.mInvalidatePanelMenuRunnable.run();
        }
    }

    void doInvalidatePanelMenu(int featureId) {
        PanelFeatureState st = getPanelState(featureId, SWEEP_OPEN_MENU);
        if (st != null) {
            if (st.menu != null) {
                Bundle savedActionViewStates = new Bundle();
                st.menu.saveActionViewStates(savedActionViewStates);
                if (savedActionViewStates.size() > 0) {
                    st.frozenActionViewState = savedActionViewStates;
                }
                st.menu.stopDispatchingItemsChanged();
                st.menu.clear();
            }
            st.refreshMenuContent = true;
            st.refreshDecorView = true;
            if ((featureId == 8 || featureId == 0) && this.mDecorContentParent != null) {
                st = getPanelState(0, SWEEP_OPEN_MENU);
                if (st != null) {
                    st.isPrepared = SWEEP_OPEN_MENU;
                    preparePanel(st, null);
                }
            }
        }
    }

    public final boolean onKeyDownPanel(int featureId, KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getRepeatCount() != 0) {
            return SWEEP_OPEN_MENU;
        }
        this.mPanelChordingKey = keyCode;
        PanelFeatureState st = getPanelState(featureId, SWEEP_OPEN_MENU);
        if (st == null || st.isOpen) {
            return SWEEP_OPEN_MENU;
        }
        return preparePanel(st, event);
    }

    public final void onKeyUpPanel(int featureId, KeyEvent event) {
        if (this.mPanelChordingKey != 0) {
            this.mPanelChordingKey = 0;
            PanelFeatureState st = getPanelState(featureId, SWEEP_OPEN_MENU);
            if (!event.isCanceled()) {
                if ((this.mDecor == null || this.mDecor.mActionMode == null) && st != null) {
                    boolean playSoundEffect = SWEEP_OPEN_MENU;
                    if (featureId != 0 || this.mDecorContentParent == null || !this.mDecorContentParent.canShowOverflowMenu() || ViewConfiguration.get(getContext()).hasPermanentMenuKey()) {
                        if (st.isOpen || st.isHandled) {
                            playSoundEffect = st.isOpen;
                            closePanel(st, true);
                        } else if (st.isPrepared) {
                            boolean show = true;
                            if (st.refreshMenuContent) {
                                st.isPrepared = SWEEP_OPEN_MENU;
                                show = preparePanel(st, event);
                            }
                            if (show) {
                                EventLog.writeEvent(50001, 0);
                                openPanel(st, event);
                                playSoundEffect = true;
                            }
                        }
                    } else if (this.mDecorContentParent.isOverflowMenuShowing()) {
                        playSoundEffect = this.mDecorContentParent.hideOverflowMenu();
                    } else if (!isDestroyed() && preparePanel(st, event)) {
                        playSoundEffect = this.mDecorContentParent.showOverflowMenu();
                    }
                    if (playSoundEffect) {
                        AudioManager audioManager = (AudioManager) getContext().getSystemService("audio");
                        if (audioManager != null) {
                            audioManager.playSoundEffect(0);
                        } else {
                            Log.w(TAG, "Couldn't get audio manager");
                        }
                    }
                }
            }
        }
    }

    public final void closeAllPanels() {
        if (getWindowManager() != null) {
            PanelFeatureState[] panels = this.mPanels;
            int N = panels != null ? panels.length : 0;
            for (int i = 0; i < N; i += FLAG_RESOURCE_SET_ICON) {
                PanelFeatureState panel = panels[i];
                if (panel != null) {
                    closePanel(panel, true);
                }
            }
            closeContextMenu();
        }
    }

    private synchronized void closeContextMenu() {
        if (this.mContextMenu != null) {
            this.mContextMenu.close();
            dismissContextMenu();
        }
    }

    private synchronized void dismissContextMenu() {
        this.mContextMenu = null;
        if (this.mContextMenuHelper != null) {
            this.mContextMenuHelper.dismiss();
            this.mContextMenuHelper = null;
        }
    }

    public boolean performPanelShortcut(int featureId, int keyCode, KeyEvent event, int flags) {
        return performPanelShortcut(getPanelState(featureId, SWEEP_OPEN_MENU), keyCode, event, flags);
    }

    private boolean performPanelShortcut(PanelFeatureState st, int keyCode, KeyEvent event, int flags) {
        if (event.isSystem() || st == null) {
            return SWEEP_OPEN_MENU;
        }
        boolean handled = SWEEP_OPEN_MENU;
        if ((st.isPrepared || preparePanel(st, event)) && st.menu != null) {
            handled = st.menu.performShortcut(keyCode, event, flags);
        }
        if (!handled) {
            return handled;
        }
        st.isHandled = true;
        if ((flags & FLAG_RESOURCE_SET_ICON) != 0 || this.mDecorContentParent != null) {
            return handled;
        }
        closePanel(st, true);
        return handled;
    }

    public boolean performPanelIdentifierAction(int featureId, int id, int flags) {
        boolean z = SWEEP_OPEN_MENU;
        PanelFeatureState st = getPanelState(featureId, true);
        if (preparePanel(st, new KeyEvent(0, 82)) && st.menu != null) {
            z = st.menu.performIdentifierAction(id, flags);
            if (this.mDecorContentParent == null) {
                closePanel(st, true);
            }
        }
        return z;
    }

    public PanelFeatureState findMenuPanel(Menu menu) {
        PanelFeatureState[] panels = this.mPanels;
        int N = panels != null ? panels.length : 0;
        for (int i = 0; i < N; i += FLAG_RESOURCE_SET_ICON) {
            PanelFeatureState panel = panels[i];
            if (panel != null && panel.menu == menu) {
                return panel;
            }
        }
        return null;
    }

    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
        Window.Callback cb = getCallback();
        if (!(cb == null || isDestroyed())) {
            PanelFeatureState panel = findMenuPanel(menu.getRootMenu());
            if (panel != null) {
                return cb.onMenuItemSelected(panel.featureId, item);
            }
        }
        return SWEEP_OPEN_MENU;
    }

    public void onMenuModeChange(MenuBuilder menu) {
        reopenMenu(true);
    }

    private void reopenMenu(boolean toggleMenuMode) {
        PanelFeatureState st;
        if (this.mDecorContentParent == null || !this.mDecorContentParent.canShowOverflowMenu() || (ViewConfiguration.get(getContext()).hasPermanentMenuKey() && !this.mDecorContentParent.isOverflowMenuShowPending())) {
            st = getPanelState(0, SWEEP_OPEN_MENU);
            if (st != null) {
                boolean newExpandedMode = toggleMenuMode ? !st.isInExpandedMode ? true : SWEEP_OPEN_MENU : st.isInExpandedMode;
                st.refreshDecorView = true;
                closePanel(st, SWEEP_OPEN_MENU);
                st.isInExpandedMode = newExpandedMode;
                openPanel(st, null);
                return;
            }
            return;
        }
        Window.Callback cb = getCallback();
        if (this.mDecorContentParent.isOverflowMenuShowing() && toggleMenuMode) {
            this.mDecorContentParent.hideOverflowMenu();
            st = getPanelState(0, SWEEP_OPEN_MENU);
            if (st != null && cb != null && !isDestroyed()) {
                cb.onPanelClosed(8, st.menu);
            }
        } else if (cb != null && !isDestroyed()) {
            if (this.mInvalidatePanelMenuPosted && (this.mInvalidatePanelMenuFeatures & FLAG_RESOURCE_SET_ICON) != 0) {
                this.mDecor.removeCallbacks(this.mInvalidatePanelMenuRunnable);
                this.mInvalidatePanelMenuRunnable.run();
            }
            st = getPanelState(0, SWEEP_OPEN_MENU);
            if (st != null && st.menu != null && !st.refreshMenuContent && cb.onPreparePanel(0, st.createdPanelView, st.menu)) {
                cb.onMenuOpened(8, st.menu);
                this.mDecorContentParent.showOverflowMenu();
            }
        }
    }

    protected boolean initializePanelMenu(PanelFeatureState st) {
        Context context = getContext();
        if ((st.featureId == 0 || st.featureId == 8) && this.mDecorContentParent != null) {
            TypedValue outValue = new TypedValue();
            Theme baseTheme = context.getTheme();
            baseTheme.resolveAttribute(16843825, outValue, true);
            Theme widgetTheme = null;
            if (outValue.resourceId != 0) {
                widgetTheme = context.getResources().newTheme();
                widgetTheme.setTo(baseTheme);
                widgetTheme.applyStyle(outValue.resourceId, true);
                widgetTheme.resolveAttribute(16843671, outValue, true);
            } else {
                baseTheme.resolveAttribute(16843671, outValue, true);
            }
            if (outValue.resourceId != 0) {
                if (widgetTheme == null) {
                    widgetTheme = context.getResources().newTheme();
                    widgetTheme.setTo(baseTheme);
                }
                widgetTheme.applyStyle(outValue.resourceId, true);
            }
            if (widgetTheme != null) {
                Context context2 = new ContextThemeWrapper(context, 0);
                context2.getTheme().setTo(widgetTheme);
                context = context2;
            }
        }
        MenuBuilder menu = new MenuBuilder(context);
        menu.setCallback(this);
        st.setMenu(menu);
        return true;
    }

    protected boolean initializePanelDecor(PanelFeatureState st) {
        st.decorView = new DecorView(this, getContext(), st.featureId);
        st.gravity = 81;
        st.setStyle(getContext());
        TypedArray a = getContext().obtainStyledAttributes(null, R.styleable.Window, 0, st.listPresenterTheme);
        float elevation = a.getDimension(38, 0.0f);
        if (elevation != 0.0f) {
            st.decorView.setElevation(elevation);
        }
        a.recycle();
        return true;
    }

    private int getOptionsPanelGravity() {
        try {
            return WindowManagerHolder.sWindowManager.getPreferredOptionsPanelGravity();
        } catch (RemoteException ex) {
            Log.e(TAG, "Couldn't getOptionsPanelGravity; using default", ex);
            return 81;
        }
    }

    void onOptionsPanelRotationChanged() {
        PanelFeatureState st = getPanelState(0, SWEEP_OPEN_MENU);
        if (st != null) {
            LayoutParams lp = st.decorView != null ? (LayoutParams) st.decorView.getLayoutParams() : null;
            if (lp != null) {
                lp.gravity = getOptionsPanelGravity();
                ViewManager wm = getWindowManager();
                if (wm != null) {
                    wm.updateViewLayout(st.decorView, lp);
                }
            }
        }
    }

    protected boolean initializePanelContent(PanelFeatureState st) {
        if (st.createdPanelView != null) {
            st.shownPanelView = st.createdPanelView;
            return true;
        } else if (st.menu == null) {
            return SWEEP_OPEN_MENU;
        } else {
            if (this.mPanelMenuPresenterCallback == null) {
                this.mPanelMenuPresenterCallback = new PanelMenuPresenterCallback();
            }
            MenuView menuView = st.isInListMode() ? st.getListMenuView(getContext(), this.mPanelMenuPresenterCallback) : st.getIconMenuView(getContext(), this.mPanelMenuPresenterCallback);
            st.shownPanelView = (View) menuView;
            if (st.shownPanelView == null) {
                return SWEEP_OPEN_MENU;
            }
            int defaultAnimations = menuView.getWindowAnimations();
            if (defaultAnimations != 0) {
                st.windowAnimations = defaultAnimations;
            }
            return true;
        }
    }

    public boolean performContextMenuIdentifierAction(int id, int flags) {
        return this.mContextMenu != null ? this.mContextMenu.performIdentifierAction(id, flags) : SWEEP_OPEN_MENU;
    }

    public final void setElevation(float elevation) {
        this.mElevation = elevation;
        if (this.mDecor != null) {
            this.mDecor.setElevation(elevation);
        }
        dispatchWindowAttributesChanged(getAttributes());
    }

    public final void setClipToOutline(boolean clipToOutline) {
        this.mClipToOutline = clipToOutline;
        if (this.mDecor != null) {
            this.mDecor.setClipToOutline(clipToOutline);
        }
    }

    public final void setBackgroundDrawable(Drawable drawable) {
        int i = 0;
        if (drawable != this.mBackgroundDrawable || this.mBackgroundResource != 0) {
            this.mBackgroundResource = 0;
            this.mBackgroundDrawable = drawable;
            if (this.mDecor != null) {
                this.mDecor.setWindowBackground(drawable);
            }
            if (this.mBackgroundFallbackResource != 0) {
                DecorView decorView = this.mDecor;
                if (drawable == null) {
                    i = this.mBackgroundFallbackResource;
                }
                decorView.setBackgroundFallback(i);
            }
        }
    }

    public final void setFeatureDrawableResource(int featureId, int resId) {
        if (resId != 0) {
            DrawableFeatureState st = getDrawableState(featureId, true);
            if (st.resid != resId) {
                st.resid = resId;
                st.uri = null;
                st.local = getContext().getDrawable(resId);
                updateDrawable(featureId, st, SWEEP_OPEN_MENU);
                return;
            }
            return;
        }
        setFeatureDrawable(featureId, null);
    }

    public final void setFeatureDrawableUri(int featureId, Uri uri) {
        if (uri != null) {
            DrawableFeatureState st = getDrawableState(featureId, true);
            if (st.uri == null || !st.uri.equals(uri)) {
                st.resid = 0;
                st.uri = uri;
                st.local = loadImageURI(uri);
                updateDrawable(featureId, st, SWEEP_OPEN_MENU);
                return;
            }
            return;
        }
        setFeatureDrawable(featureId, null);
    }

    public final void setFeatureDrawable(int featureId, Drawable drawable) {
        DrawableFeatureState st = getDrawableState(featureId, true);
        st.resid = 0;
        st.uri = null;
        if (st.local != drawable) {
            st.local = drawable;
            updateDrawable(featureId, st, SWEEP_OPEN_MENU);
        }
    }

    public void setFeatureDrawableAlpha(int featureId, int alpha) {
        DrawableFeatureState st = getDrawableState(featureId, true);
        if (st.alpha != alpha) {
            st.alpha = alpha;
            updateDrawable(featureId, st, SWEEP_OPEN_MENU);
        }
    }

    protected final void setFeatureDefaultDrawable(int featureId, Drawable drawable) {
        DrawableFeatureState st = getDrawableState(featureId, true);
        if (st.def != drawable) {
            st.def = drawable;
            updateDrawable(featureId, st, SWEEP_OPEN_MENU);
        }
    }

    public final void setFeatureInt(int featureId, int value) {
        updateInt(featureId, value, SWEEP_OPEN_MENU);
    }

    protected final void updateDrawable(int featureId, boolean fromActive) {
        DrawableFeatureState st = getDrawableState(featureId, SWEEP_OPEN_MENU);
        if (st != null) {
            updateDrawable(featureId, st, fromActive);
        }
    }

    protected void onDrawableChanged(int featureId, Drawable drawable, int alpha) {
        ImageView view;
        if (featureId == 3) {
            view = getLeftIconView();
        } else if (featureId == FLAG_RESOURCE_SET_ICON_FALLBACK) {
            view = getRightIconView();
        } else {
            return;
        }
        if (drawable != null) {
            drawable.setAlpha(alpha);
            view.setImageDrawable(drawable);
            view.setVisibility(0);
            return;
        }
        view.setVisibility(8);
    }

    protected void onIntChanged(int featureId, int value) {
        if (featureId == FLAG_RESOURCE_SET_LOGO || featureId == 5) {
            updateProgressBars(value);
        } else if (featureId == 7) {
            FrameLayout titleContainer = (FrameLayout) findViewById(16908341);
            if (titleContainer != null) {
                this.mLayoutInflater.inflate(value, titleContainer);
            }
        }
    }

    private void updateProgressBars(int value) {
        ProgressBar circularProgressBar = getCircularProgressBar(true);
        ProgressBar horizontalProgressBar = getHorizontalProgressBar(true);
        int features = getLocalFeatures();
        if (value == -1) {
            if ((features & FLAG_RESOURCE_SET_ICON_FALLBACK) != 0) {
                if (horizontalProgressBar != null) {
                    int visibility = (horizontalProgressBar.isIndeterminate() || horizontalProgressBar.getProgress() < 10000) ? 0 : FLAG_RESOURCE_SET_ICON_FALLBACK;
                    horizontalProgressBar.setVisibility(visibility);
                } else {
                    Log.e(TAG, "Horizontal progress bar not located in current window decor");
                }
            }
            if ((features & 32) == 0) {
                return;
            }
            if (circularProgressBar != null) {
                circularProgressBar.setVisibility(0);
            } else {
                Log.e(TAG, "Circular progress bar not located in current window decor");
            }
        } else if (value == -2) {
            if ((features & FLAG_RESOURCE_SET_ICON_FALLBACK) != 0) {
                if (horizontalProgressBar != null) {
                    horizontalProgressBar.setVisibility(8);
                } else {
                    Log.e(TAG, "Horizontal progress bar not located in current window decor");
                }
            }
            if ((features & 32) == 0) {
                return;
            }
            if (circularProgressBar != null) {
                circularProgressBar.setVisibility(8);
            } else {
                Log.e(TAG, "Circular progress bar not located in current window decor");
            }
        } else if (value == -3) {
            if (horizontalProgressBar != null) {
                horizontalProgressBar.setIndeterminate(true);
            } else {
                Log.e(TAG, "Horizontal progress bar not located in current window decor");
            }
        } else if (value == -4) {
            if (horizontalProgressBar != null) {
                horizontalProgressBar.setIndeterminate(SWEEP_OPEN_MENU);
            } else {
                Log.e(TAG, "Horizontal progress bar not located in current window decor");
            }
        } else if (value >= 0 && value <= 10000) {
            if (horizontalProgressBar != null) {
                horizontalProgressBar.setProgress(value + 0);
            } else {
                Log.e(TAG, "Horizontal progress bar not located in current window decor");
            }
            if (value < 10000) {
                showProgressBars(horizontalProgressBar, circularProgressBar);
            } else {
                hideProgressBars(horizontalProgressBar, circularProgressBar);
            }
        } else if (20000 <= value && value <= 30000) {
            if (horizontalProgressBar != null) {
                horizontalProgressBar.setSecondaryProgress(value - 20000);
            } else {
                Log.e(TAG, "Horizontal progress bar not located in current window decor");
            }
            showProgressBars(horizontalProgressBar, circularProgressBar);
        }
    }

    private void showProgressBars(ProgressBar horizontalProgressBar, ProgressBar spinnyProgressBar) {
        int features = getLocalFeatures();
        if (!((features & 32) == 0 || spinnyProgressBar == null || spinnyProgressBar.getVisibility() != FLAG_RESOURCE_SET_ICON_FALLBACK)) {
            spinnyProgressBar.setVisibility(0);
        }
        if ((features & FLAG_RESOURCE_SET_ICON_FALLBACK) != 0 && horizontalProgressBar != null && horizontalProgressBar.getProgress() < 10000) {
            horizontalProgressBar.setVisibility(0);
        }
    }

    private void hideProgressBars(ProgressBar horizontalProgressBar, ProgressBar spinnyProgressBar) {
        int features = getLocalFeatures();
        Animation anim = AnimationUtils.loadAnimation(getContext(), 17432577);
        anim.setDuration(1000);
        if (!((features & 32) == 0 || spinnyProgressBar == null || spinnyProgressBar.getVisibility() != 0)) {
            spinnyProgressBar.startAnimation(anim);
            spinnyProgressBar.setVisibility(FLAG_RESOURCE_SET_ICON_FALLBACK);
        }
        if ((features & FLAG_RESOURCE_SET_ICON_FALLBACK) != 0 && horizontalProgressBar != null && horizontalProgressBar.getVisibility() == 0) {
            horizontalProgressBar.startAnimation(anim);
            horizontalProgressBar.setVisibility(FLAG_RESOURCE_SET_ICON_FALLBACK);
        }
    }

    public void setIcon(int resId) {
        this.mIconRes = resId;
        this.mResourcesSetFlags |= FLAG_RESOURCE_SET_ICON;
        this.mResourcesSetFlags &= -5;
        if (this.mDecorContentParent != null) {
            this.mDecorContentParent.setIcon(resId);
        }
    }

    public void setDefaultIcon(int resId) {
        if ((this.mResourcesSetFlags & FLAG_RESOURCE_SET_ICON) == 0) {
            this.mIconRes = resId;
            if (this.mDecorContentParent == null) {
                return;
            }
            if (!this.mDecorContentParent.hasIcon() || (this.mResourcesSetFlags & FLAG_RESOURCE_SET_ICON_FALLBACK) != 0) {
                if (resId != 0) {
                    this.mDecorContentParent.setIcon(resId);
                    this.mResourcesSetFlags &= -5;
                    return;
                }
                this.mDecorContentParent.setIcon(getContext().getPackageManager().getDefaultActivityIcon());
                this.mResourcesSetFlags |= FLAG_RESOURCE_SET_ICON_FALLBACK;
            }
        }
    }

    public void setLogo(int resId) {
        this.mLogoRes = resId;
        this.mResourcesSetFlags |= FLAG_RESOURCE_SET_LOGO;
        if (this.mDecorContentParent != null) {
            this.mDecorContentParent.setLogo(resId);
        }
    }

    public void setDefaultLogo(int resId) {
        if ((this.mResourcesSetFlags & FLAG_RESOURCE_SET_LOGO) == 0) {
            this.mLogoRes = resId;
            if (this.mDecorContentParent != null && !this.mDecorContentParent.hasLogo()) {
                this.mDecorContentParent.setLogo(resId);
            }
        }
    }

    public void setLocalFocus(boolean hasFocus, boolean inTouchMode) {
        getViewRootImpl().windowFocusChanged(hasFocus, inTouchMode);
    }

    public void injectInputEvent(InputEvent event) {
        getViewRootImpl().dispatchInputEvent(event);
    }

    private ViewRootImpl getViewRootImpl() {
        if (this.mDecor != null) {
            ViewRootImpl viewRootImpl = this.mDecor.getViewRootImpl();
            if (viewRootImpl != null) {
                return viewRootImpl;
            }
        }
        throw new IllegalStateException("view not added");
    }

    public void takeKeyEvents(boolean get) {
        this.mDecor.setFocusable(get);
    }

    public boolean superDispatchKeyEvent(KeyEvent event) {
        return this.mDecor.superDispatchKeyEvent(event);
    }

    public boolean superDispatchKeyShortcutEvent(KeyEvent event) {
        return this.mDecor.superDispatchKeyShortcutEvent(event);
    }

    public boolean superDispatchTouchEvent(MotionEvent event) {
        return this.mDecor.superDispatchTouchEvent(event);
    }

    public boolean superDispatchTrackballEvent(MotionEvent event) {
        return this.mDecor.superDispatchTrackballEvent(event);
    }

    public boolean superDispatchGenericMotionEvent(MotionEvent event) {
        return this.mDecor.superDispatchGenericMotionEvent(event);
    }

    protected boolean onKeyDown(int featureId, int keyCode, KeyEvent event) {
        DispatcherState dispatcher = this.mDecor != null ? this.mDecor.getKeyDispatcherState() : null;
        switch (keyCode) {
            case FLAG_RESOURCE_SET_ICON_FALLBACK /*4*/:
                if (event.getRepeatCount() <= 0 && featureId >= 0) {
                    if (dispatcher == null) {
                        return true;
                    }
                    dispatcher.startTracking(event, this);
                    return true;
                }
            case 24:
            case 25:
                int direction = keyCode == 24 ? FLAG_RESOURCE_SET_ICON : -1;
                if (this.mMediaController != null) {
                    this.mMediaController.adjustVolume(direction, FLAG_RESOURCE_SET_ICON);
                    return true;
                }
                MediaSessionLegacyHelper.getHelper(getContext()).sendAdjustVolumeBy(this.mVolumeControlStreamType, direction, 17);
                return true;
            case 79:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 126:
            case 127:
            case 130:
                if (this.mMediaController == null || !this.mMediaController.dispatchMediaButtonEvent(event)) {
                    return SWEEP_OPEN_MENU;
                }
                return true;
            case 82:
                if (featureId < 0) {
                    featureId = 0;
                }
                onKeyDownPanel(featureId, event);
                return true;
            case 164:
                getAudioManager().handleKeyDown(event, this.mVolumeControlStreamType);
                return true;
        }
        return SWEEP_OPEN_MENU;
    }

    private KeyguardManager getKeyguardManager() {
        if (this.mKeyguardManager == null) {
            this.mKeyguardManager = (KeyguardManager) getContext().getSystemService("keyguard");
        }
        return this.mKeyguardManager;
    }

    AudioManager getAudioManager() {
        if (this.mAudioManager == null) {
            this.mAudioManager = (AudioManager) getContext().getSystemService("audio");
        }
        return this.mAudioManager;
    }

    protected boolean onKeyUp(int featureId, int keyCode, KeyEvent event) {
        DispatcherState dispatcher = this.mDecor != null ? this.mDecor.getKeyDispatcherState() : null;
        if (dispatcher != null) {
            dispatcher.handleUpEvent(event);
        }
        switch (keyCode) {
            case FLAG_RESOURCE_SET_ICON_FALLBACK /*4*/:
                if (featureId < 0 || !event.isTracking() || event.isCanceled()) {
                    return SWEEP_OPEN_MENU;
                }
                if (featureId == 0) {
                    PanelFeatureState st = getPanelState(featureId, SWEEP_OPEN_MENU);
                    if (st != null && st.isInExpandedMode) {
                        reopenMenu(true);
                        return true;
                    }
                }
                closePanel(featureId);
                return true;
            case 24:
            case 25:
                if (this.mMediaController != null) {
                    this.mMediaController.adjustVolume(0, 20);
                } else {
                    MediaSessionLegacyHelper.getHelper(getContext()).sendAdjustVolumeBy(this.mVolumeControlStreamType, 0, 20);
                }
                return true;
            case 79:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 126:
            case 127:
            case 130:
                if (this.mMediaController == null || !this.mMediaController.dispatchMediaButtonEvent(event)) {
                    return SWEEP_OPEN_MENU;
                }
                return true;
            case 82:
                if (featureId < 0) {
                    featureId = 0;
                }
                onKeyUpPanel(featureId, event);
                return true;
            case 84:
                if (getKeyguardManager().inKeyguardRestrictedInputMode()) {
                    return SWEEP_OPEN_MENU;
                }
                if (event.isTracking() && !event.isCanceled()) {
                    launchDefaultSearch();
                }
                return true;
            case 164:
                getAudioManager().handleKeyUp(event, this.mVolumeControlStreamType);
                return true;
            default:
                return SWEEP_OPEN_MENU;
        }
    }

    protected void onActive() {
    }

    public final View getDecorView() {
        if (this.mDecor == null) {
            installDecor();
        }
        return this.mDecor;
    }

    public final View peekDecorView() {
        return this.mDecor;
    }

    public Bundle saveHierarchyState() {
        Bundle outState = new Bundle();
        if (this.mContentParent != null) {
            SparseArray<Parcelable> states = new SparseArray();
            this.mContentParent.saveHierarchyState(states);
            outState.putSparseParcelableArray(VIEWS_TAG, states);
            View focusedView = this.mContentParent.findFocus();
            if (!(focusedView == null || focusedView.getId() == -1)) {
                outState.putInt(FOCUSED_ID_TAG, focusedView.getId());
            }
            SparseArray<Parcelable> panelStates = new SparseArray();
            savePanelState(panelStates);
            if (panelStates.size() > 0) {
                outState.putSparseParcelableArray(PANELS_TAG, panelStates);
            }
            if (this.mDecorContentParent != null) {
                SparseArray<Parcelable> actionBarStates = new SparseArray();
                this.mDecorContentParent.saveToolbarHierarchyState(actionBarStates);
                outState.putSparseParcelableArray(ACTION_BAR_TAG, actionBarStates);
            }
        }
        return outState;
    }

    public void restoreHierarchyState(Bundle savedInstanceState) {
        if (this.mContentParent != null) {
            SparseArray<Parcelable> savedStates = savedInstanceState.getSparseParcelableArray(VIEWS_TAG);
            if (savedStates != null) {
                this.mContentParent.restoreHierarchyState(savedStates);
            }
            int focusedViewId = savedInstanceState.getInt(FOCUSED_ID_TAG, -1);
            if (focusedViewId != -1) {
                View needsFocus = this.mContentParent.findViewById(focusedViewId);
                if (needsFocus != null) {
                    needsFocus.requestFocus();
                } else {
                    Log.w(TAG, "Previously focused view reported id " + focusedViewId + " during save, but can't be found during restore.");
                }
            }
            SparseArray<Parcelable> panelStates = savedInstanceState.getSparseParcelableArray(PANELS_TAG);
            if (panelStates != null) {
                restorePanelState(panelStates);
            }
            if (this.mDecorContentParent != null) {
                SparseArray<Parcelable> actionBarStates = savedInstanceState.getSparseParcelableArray(ACTION_BAR_TAG);
                if (actionBarStates != null) {
                    doPendingInvalidatePanelMenu();
                    this.mDecorContentParent.restoreToolbarHierarchyState(actionBarStates);
                    return;
                }
                Log.w(TAG, "Missing saved instance states for action bar views! State will not be restored.");
            }
        }
    }

    private void savePanelState(SparseArray<Parcelable> icicles) {
        PanelFeatureState[] panels = this.mPanels;
        if (panels != null) {
            for (int curFeatureId = panels.length - 1; curFeatureId >= 0; curFeatureId--) {
                if (panels[curFeatureId] != null) {
                    icicles.put(curFeatureId, panels[curFeatureId].onSaveInstanceState());
                }
            }
        }
    }

    private void restorePanelState(SparseArray<Parcelable> icicles) {
        for (int i = icicles.size() - 1; i >= 0; i--) {
            int curFeatureId = icicles.keyAt(i);
            PanelFeatureState st = getPanelState(curFeatureId, SWEEP_OPEN_MENU);
            if (st != null) {
                st.onRestoreInstanceState((Parcelable) icicles.get(curFeatureId));
                invalidatePanelMenu(curFeatureId);
            }
        }
    }

    private void openPanelsAfterRestore() {
        PanelFeatureState[] panels = this.mPanels;
        if (panels != null) {
            for (int i = panels.length - 1; i >= 0; i--) {
                PanelFeatureState st = panels[i];
                if (st != null) {
                    st.applyFrozenState();
                    if (!st.isOpen && st.wasLastOpen) {
                        st.isInExpandedMode = st.wasLastExpanded;
                        openPanel(st, null);
                    }
                }
            }
        }
    }

    protected DecorView generateDecor() {
        return new DecorView(this, getContext(), -1);
    }

    protected void setFeatureFromAttrs(int featureId, TypedArray attrs, int drawableAttr, int alphaAttr) {
        Drawable d = attrs.getDrawable(drawableAttr);
        if (d != null) {
            requestFeature(featureId);
            setFeatureDefaultDrawable(featureId, d);
        }
        if ((getFeatures() & (FLAG_RESOURCE_SET_ICON << featureId)) != 0) {
            int alpha = attrs.getInt(alphaAttr, -1);
            if (alpha >= 0) {
                setFeatureDrawableAlpha(featureId, alpha);
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected android.view.ViewGroup generateLayout(com.android.internal.policy.impl.PhoneWindow.DecorView r29) {
        /*
        r28 = this;
        r4 = r28.getWindowStyle();
        r25 = 4;
        r26 = 0;
        r0 = r25;
        r1 = r26;
        r25 = r4.getBoolean(r0, r1);
        r0 = r25;
        r1 = r28;
        r1.mIsFloating = r0;
        r25 = 65792; // 0x10100 float:9.2194E-41 double:3.25056E-319;
        r26 = r28.getForcedWindowFlags();
        r26 = r26 ^ -1;
        r10 = r25 & r26;
        r0 = r28;
        r0 = r0.mIsFloating;
        r25 = r0;
        if (r25 == 0) goto L_0x0507;
    L_0x0029:
        r25 = -2;
        r26 = -2;
        r0 = r28;
        r1 = r25;
        r2 = r26;
        r0.setLayout(r1, r2);
        r25 = 0;
        r0 = r28;
        r1 = r25;
        r0.setFlags(r1, r10);
    L_0x003f:
        r25 = 3;
        r26 = 0;
        r0 = r25;
        r1 = r26;
        r25 = r4.getBoolean(r0, r1);
        if (r25 == 0) goto L_0x0513;
    L_0x004d:
        r25 = 1;
        r0 = r28;
        r1 = r25;
        r0.requestFeature(r1);
    L_0x0056:
        r25 = 17;
        r26 = 0;
        r0 = r25;
        r1 = r26;
        r25 = r4.getBoolean(r0, r1);
        if (r25 == 0) goto L_0x006d;
    L_0x0064:
        r25 = 9;
        r0 = r28;
        r1 = r25;
        r0.requestFeature(r1);
    L_0x006d:
        r25 = 16;
        r26 = 0;
        r0 = r25;
        r1 = r26;
        r25 = r4.getBoolean(r0, r1);
        if (r25 == 0) goto L_0x0084;
    L_0x007b:
        r25 = 10;
        r0 = r28;
        r1 = r25;
        r0.requestFeature(r1);
    L_0x0084:
        r25 = 25;
        r26 = 0;
        r0 = r25;
        r1 = r26;
        r25 = r4.getBoolean(r0, r1);
        if (r25 == 0) goto L_0x009b;
    L_0x0092:
        r25 = 11;
        r0 = r28;
        r1 = r25;
        r0.requestFeature(r1);
    L_0x009b:
        r25 = 9;
        r26 = 0;
        r0 = r25;
        r1 = r26;
        r25 = r4.getBoolean(r0, r1);
        if (r25 == 0) goto L_0x00c0;
    L_0x00a9:
        r25 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r26 = r28.getForcedWindowFlags();
        r26 = r26 ^ -1;
        r0 = r26;
        r0 = r0 & 1024;
        r26 = r0;
        r0 = r28;
        r1 = r25;
        r2 = r26;
        r0.setFlags(r1, r2);
    L_0x00c0:
        r25 = 23;
        r26 = 0;
        r0 = r25;
        r1 = r26;
        r25 = r4.getBoolean(r0, r1);
        if (r25 == 0) goto L_0x00e3;
    L_0x00ce:
        r25 = 67108864; // 0x4000000 float:1.5046328E-36 double:3.31561842E-316;
        r26 = 67108864; // 0x4000000 float:1.5046328E-36 double:3.31561842E-316;
        r27 = r28.getForcedWindowFlags();
        r27 = r27 ^ -1;
        r26 = r26 & r27;
        r0 = r28;
        r1 = r25;
        r2 = r26;
        r0.setFlags(r1, r2);
    L_0x00e3:
        r25 = 24;
        r26 = 0;
        r0 = r25;
        r1 = r26;
        r25 = r4.getBoolean(r0, r1);
        if (r25 == 0) goto L_0x0106;
    L_0x00f1:
        r25 = 134217728; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r26 = 134217728; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r27 = r28.getForcedWindowFlags();
        r27 = r27 ^ -1;
        r26 = r26 & r27;
        r0 = r28;
        r1 = r25;
        r2 = r26;
        r0.setFlags(r1, r2);
    L_0x0106:
        r25 = 22;
        r26 = 0;
        r0 = r25;
        r1 = r26;
        r25 = r4.getBoolean(r0, r1);
        if (r25 == 0) goto L_0x0129;
    L_0x0114:
        r25 = 33554432; // 0x2000000 float:9.403955E-38 double:1.6578092E-316;
        r26 = 33554432; // 0x2000000 float:9.403955E-38 double:1.6578092E-316;
        r27 = r28.getForcedWindowFlags();
        r27 = r27 ^ -1;
        r26 = r26 & r27;
        r0 = r28;
        r1 = r25;
        r2 = r26;
        r0.setFlags(r1, r2);
    L_0x0129:
        r25 = 14;
        r26 = 0;
        r0 = r25;
        r1 = r26;
        r25 = r4.getBoolean(r0, r1);
        if (r25 == 0) goto L_0x014c;
    L_0x0137:
        r25 = 1048576; // 0x100000 float:1.469368E-39 double:5.180654E-318;
        r26 = 1048576; // 0x100000 float:1.469368E-39 double:5.180654E-318;
        r27 = r28.getForcedWindowFlags();
        r27 = r27 ^ -1;
        r26 = r26 & r27;
        r0 = r28;
        r1 = r25;
        r2 = r26;
        r0.setFlags(r1, r2);
    L_0x014c:
        r26 = 18;
        r25 = r28.getContext();
        r25 = r25.getApplicationInfo();
        r0 = r25;
        r0 = r0.targetSdkVersion;
        r25 = r0;
        r27 = 11;
        r0 = r25;
        r1 = r27;
        if (r0 < r1) goto L_0x052c;
    L_0x0164:
        r25 = 1;
    L_0x0166:
        r0 = r26;
        r1 = r25;
        r25 = r4.getBoolean(r0, r1);
        if (r25 == 0) goto L_0x0185;
    L_0x0170:
        r25 = 8388608; // 0x800000 float:1.17549435E-38 double:4.144523E-317;
        r26 = 8388608; // 0x800000 float:1.17549435E-38 double:4.144523E-317;
        r27 = r28.getForcedWindowFlags();
        r27 = r27 ^ -1;
        r26 = r26 & r27;
        r0 = r28;
        r1 = r25;
        r2 = r26;
        r0.setFlags(r1, r2);
    L_0x0185:
        r25 = 19;
        r0 = r28;
        r0 = r0.mMinWidthMajor;
        r26 = r0;
        r0 = r25;
        r1 = r26;
        r4.getValue(r0, r1);
        r25 = 20;
        r0 = r28;
        r0 = r0.mMinWidthMinor;
        r26 = r0;
        r0 = r25;
        r1 = r26;
        r4.getValue(r0, r1);
        r25 = 48;
        r0 = r25;
        r25 = r4.hasValue(r0);
        if (r25 == 0) goto L_0x01cf;
    L_0x01ad:
        r0 = r28;
        r0 = r0.mFixedWidthMajor;
        r25 = r0;
        if (r25 != 0) goto L_0x01c0;
    L_0x01b5:
        r25 = new android.util.TypedValue;
        r25.<init>();
        r0 = r25;
        r1 = r28;
        r1.mFixedWidthMajor = r0;
    L_0x01c0:
        r25 = 48;
        r0 = r28;
        r0 = r0.mFixedWidthMajor;
        r26 = r0;
        r0 = r25;
        r1 = r26;
        r4.getValue(r0, r1);
    L_0x01cf:
        r25 = 50;
        r0 = r25;
        r25 = r4.hasValue(r0);
        if (r25 == 0) goto L_0x01fb;
    L_0x01d9:
        r0 = r28;
        r0 = r0.mFixedWidthMinor;
        r25 = r0;
        if (r25 != 0) goto L_0x01ec;
    L_0x01e1:
        r25 = new android.util.TypedValue;
        r25.<init>();
        r0 = r25;
        r1 = r28;
        r1.mFixedWidthMinor = r0;
    L_0x01ec:
        r25 = 50;
        r0 = r28;
        r0 = r0.mFixedWidthMinor;
        r26 = r0;
        r0 = r25;
        r1 = r26;
        r4.getValue(r0, r1);
    L_0x01fb:
        r25 = 51;
        r0 = r25;
        r25 = r4.hasValue(r0);
        if (r25 == 0) goto L_0x0227;
    L_0x0205:
        r0 = r28;
        r0 = r0.mFixedHeightMajor;
        r25 = r0;
        if (r25 != 0) goto L_0x0218;
    L_0x020d:
        r25 = new android.util.TypedValue;
        r25.<init>();
        r0 = r25;
        r1 = r28;
        r1.mFixedHeightMajor = r0;
    L_0x0218:
        r25 = 51;
        r0 = r28;
        r0 = r0.mFixedHeightMajor;
        r26 = r0;
        r0 = r25;
        r1 = r26;
        r4.getValue(r0, r1);
    L_0x0227:
        r25 = 49;
        r0 = r25;
        r25 = r4.hasValue(r0);
        if (r25 == 0) goto L_0x0253;
    L_0x0231:
        r0 = r28;
        r0 = r0.mFixedHeightMinor;
        r25 = r0;
        if (r25 != 0) goto L_0x0244;
    L_0x0239:
        r25 = new android.util.TypedValue;
        r25.<init>();
        r0 = r25;
        r1 = r28;
        r1.mFixedHeightMinor = r0;
    L_0x0244:
        r25 = 49;
        r0 = r28;
        r0 = r0.mFixedHeightMinor;
        r26 = r0;
        r0 = r25;
        r1 = r26;
        r4.getValue(r0, r1);
    L_0x0253:
        r25 = 26;
        r26 = 0;
        r0 = r25;
        r1 = r26;
        r25 = r4.getBoolean(r0, r1);
        if (r25 == 0) goto L_0x026a;
    L_0x0261:
        r25 = 12;
        r0 = r28;
        r1 = r25;
        r0.requestFeature(r1);
    L_0x026a:
        r25 = 45;
        r26 = 0;
        r0 = r25;
        r1 = r26;
        r25 = r4.getBoolean(r0, r1);
        if (r25 == 0) goto L_0x0281;
    L_0x0278:
        r25 = 13;
        r0 = r28;
        r1 = r25;
        r0.requestFeature(r1);
    L_0x0281:
        r25 = r28.getContext();
        r26 = "window";
        r24 = r25.getSystemService(r26);
        r24 = (android.view.WindowManager) r24;
        if (r24 == 0) goto L_0x02c1;
    L_0x028f:
        r8 = r24.getDefaultDisplay();
        r25 = r8.getDisplayId();
        if (r25 == 0) goto L_0x02a5;
    L_0x0299:
        r25 = r28.getForcedWindowFlags();
        r0 = r25;
        r0 = r0 & 1024;
        r25 = r0;
        if (r25 == 0) goto L_0x0530;
    L_0x02a5:
        r18 = 1;
    L_0x02a7:
        if (r18 == 0) goto L_0x02c1;
    L_0x02a9:
        r25 = r28.getContext();
        r25 = r25.getResources();
        r25 = r25.getDisplayMetrics();
        r0 = r25;
        r25 = com.android.internal.util.ScreenShapeHelper.getWindowOutsetBottomPx(r0, r4);
        r0 = r25;
        r1 = r28;
        r1.mOutsetBottomPx = r0;
    L_0x02c1:
        r7 = r28.getContext();
        r25 = r7.getApplicationInfo();
        r0 = r25;
        r0 = r0.targetSdkVersion;
        r23 = r0;
        r25 = 11;
        r0 = r23;
        r1 = r25;
        if (r0 >= r1) goto L_0x0534;
    L_0x02d7:
        r20 = 1;
    L_0x02d9:
        r25 = 14;
        r0 = r23;
        r1 = r25;
        if (r0 >= r1) goto L_0x0538;
    L_0x02e1:
        r21 = 1;
    L_0x02e3:
        r25 = 21;
        r0 = r23;
        r1 = r25;
        if (r0 >= r1) goto L_0x053c;
    L_0x02eb:
        r22 = 1;
    L_0x02ed:
        r25 = r7.getResources();
        r26 = 17956873; // 0x1120009 float:2.681599E-38 double:8.871874E-317;
        r19 = r25.getBoolean(r26);
        r25 = 8;
        r0 = r28;
        r1 = r25;
        r25 = r0.hasFeature(r1);
        if (r25 == 0) goto L_0x0310;
    L_0x0304:
        r25 = 1;
        r0 = r28;
        r1 = r25;
        r25 = r0.hasFeature(r1);
        if (r25 == 0) goto L_0x0540;
    L_0x0310:
        r14 = 1;
    L_0x0311:
        if (r20 != 0) goto L_0x0319;
    L_0x0313:
        if (r21 == 0) goto L_0x0543;
    L_0x0315:
        if (r19 == 0) goto L_0x0543;
    L_0x0317:
        if (r14 == 0) goto L_0x0543;
    L_0x0319:
        r25 = 1;
        r0 = r28;
        r1 = r25;
        r0.setNeedsMenuKey(r1);
    L_0x0322:
        r0 = r28;
        r0 = r0.mIsFloating;
        r25 = r0;
        if (r25 != 0) goto L_0x0355;
    L_0x032a:
        r25 = android.app.ActivityManager.isHighEndGfx();
        if (r25 == 0) goto L_0x0355;
    L_0x0330:
        if (r22 != 0) goto L_0x0355;
    L_0x0332:
        r25 = 34;
        r26 = 0;
        r0 = r25;
        r1 = r26;
        r25 = r4.getBoolean(r0, r1);
        if (r25 == 0) goto L_0x0355;
    L_0x0340:
        r25 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r26 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r27 = r28.getForcedWindowFlags();
        r27 = r27 ^ -1;
        r26 = r26 & r27;
        r0 = r28;
        r1 = r25;
        r2 = r26;
        r0.setFlags(r1, r2);
    L_0x0355:
        r0 = r28;
        r0 = r0.mForcedStatusBarColor;
        r25 = r0;
        if (r25 != 0) goto L_0x036f;
    L_0x035d:
        r25 = 35;
        r26 = -16777216; // 0xffffffffff000000 float:-1.7014118E38 double:NaN;
        r0 = r25;
        r1 = r26;
        r25 = r4.getColor(r0, r1);
        r0 = r25;
        r1 = r28;
        r1.mStatusBarColor = r0;
    L_0x036f:
        r0 = r28;
        r0 = r0.mForcedNavigationBarColor;
        r25 = r0;
        if (r25 != 0) goto L_0x0389;
    L_0x0377:
        r25 = 36;
        r26 = -16777216; // 0xffffffffff000000 float:-1.7014118E38 double:NaN;
        r0 = r25;
        r1 = r26;
        r25 = r4.getColor(r0, r1);
        r0 = r25;
        r1 = r28;
        r1.mNavigationBarColor = r0;
    L_0x0389:
        r0 = r28;
        r0 = r0.mAlwaysReadCloseOnTouchAttr;
        r25 = r0;
        if (r25 != 0) goto L_0x03a7;
    L_0x0391:
        r25 = r28.getContext();
        r25 = r25.getApplicationInfo();
        r0 = r25;
        r0 = r0.targetSdkVersion;
        r25 = r0;
        r26 = 11;
        r0 = r25;
        r1 = r26;
        if (r0 < r1) goto L_0x03be;
    L_0x03a7:
        r25 = 21;
        r26 = 0;
        r0 = r25;
        r1 = r26;
        r25 = r4.getBoolean(r0, r1);
        if (r25 == 0) goto L_0x03be;
    L_0x03b5:
        r25 = 1;
        r0 = r28;
        r1 = r25;
        r0.setCloseOnTouchOutsideIfNotSet(r1);
    L_0x03be:
        r15 = r28.getAttributes();
        r25 = r28.hasSoftInputMode();
        if (r25 != 0) goto L_0x03da;
    L_0x03c8:
        r25 = 13;
        r0 = r15.softInputMode;
        r26 = r0;
        r0 = r25;
        r1 = r26;
        r25 = r4.getInt(r0, r1);
        r0 = r25;
        r15.softInputMode = r0;
    L_0x03da:
        r25 = 11;
        r0 = r28;
        r0 = r0.mIsFloating;
        r26 = r0;
        r0 = r25;
        r1 = r26;
        r25 = r4.getBoolean(r0, r1);
        if (r25 == 0) goto L_0x0414;
    L_0x03ec:
        r25 = r28.getForcedWindowFlags();
        r25 = r25 & 2;
        if (r25 != 0) goto L_0x03fe;
    L_0x03f4:
        r0 = r15.flags;
        r25 = r0;
        r25 = r25 | 2;
        r0 = r25;
        r15.flags = r0;
    L_0x03fe:
        r25 = r28.haveDimAmount();
        if (r25 != 0) goto L_0x0414;
    L_0x0404:
        r25 = 0;
        r26 = 1056964608; // 0x3f000000 float:0.5 double:5.222099017E-315;
        r0 = r25;
        r1 = r26;
        r25 = r4.getFloat(r0, r1);
        r0 = r25;
        r15.dimAmount = r0;
    L_0x0414:
        r0 = r15.windowAnimations;
        r25 = r0;
        if (r25 != 0) goto L_0x042a;
    L_0x041a:
        r25 = 8;
        r26 = 0;
        r0 = r25;
        r1 = r26;
        r25 = r4.getResourceId(r0, r1);
        r0 = r25;
        r15.windowAnimations = r0;
    L_0x042a:
        r25 = r28.getContainer();
        if (r25 != 0) goto L_0x04b4;
    L_0x0430:
        r0 = r28;
        r0 = r0.mBackgroundDrawable;
        r25 = r0;
        if (r25 != 0) goto L_0x047e;
    L_0x0438:
        r0 = r28;
        r0 = r0.mBackgroundResource;
        r25 = r0;
        if (r25 != 0) goto L_0x0452;
    L_0x0440:
        r25 = 1;
        r26 = 0;
        r0 = r25;
        r1 = r26;
        r25 = r4.getResourceId(r0, r1);
        r0 = r25;
        r1 = r28;
        r1.mBackgroundResource = r0;
    L_0x0452:
        r0 = r28;
        r0 = r0.mFrameResource;
        r25 = r0;
        if (r25 != 0) goto L_0x046c;
    L_0x045a:
        r25 = 2;
        r26 = 0;
        r0 = r25;
        r1 = r26;
        r25 = r4.getResourceId(r0, r1);
        r0 = r25;
        r1 = r28;
        r1.mFrameResource = r0;
    L_0x046c:
        r25 = 46;
        r26 = 0;
        r0 = r25;
        r1 = r26;
        r25 = r4.getResourceId(r0, r1);
        r0 = r25;
        r1 = r28;
        r1.mBackgroundFallbackResource = r0;
    L_0x047e:
        r25 = 38;
        r26 = 0;
        r0 = r25;
        r1 = r26;
        r25 = r4.getDimension(r0, r1);
        r0 = r25;
        r1 = r28;
        r1.mElevation = r0;
        r25 = 39;
        r26 = 0;
        r0 = r25;
        r1 = r26;
        r25 = r4.getBoolean(r0, r1);
        r0 = r25;
        r1 = r28;
        r1.mClipToOutline = r0;
        r25 = 7;
        r26 = 0;
        r0 = r25;
        r1 = r26;
        r25 = r4.getColor(r0, r1);
        r0 = r25;
        r1 = r28;
        r1.mTextColor = r0;
    L_0x04b4:
        r9 = r28.getLocalFeatures();
        r0 = r9 & 2048;
        r25 = r0;
        if (r25 == 0) goto L_0x054e;
    L_0x04be:
        r13 = 17367225; // 0x10900b9 float:2.5163444E-38 double:8.580549E-317;
    L_0x04c1:
        r0 = r28;
        r0 = r0.mDecor;
        r25 = r0;
        r25.startChanging();
        r0 = r28;
        r0 = r0.mLayoutInflater;
        r25 = r0;
        r26 = 0;
        r0 = r25;
        r1 = r26;
        r12 = r0.inflate(r13, r1);
        r25 = new android.view.ViewGroup$LayoutParams;
        r26 = -1;
        r27 = -1;
        r25.<init>(r26, r27);
        r0 = r29;
        r1 = r25;
        r0.addView(r12, r1);
        r12 = (android.view.ViewGroup) r12;
        r0 = r28;
        r0.mContentRoot = r12;
        r25 = 16908290; // 0x1020002 float:2.3877235E-38 double:8.353805E-317;
        r0 = r28;
        r1 = r25;
        r6 = r0.findViewById(r1);
        r6 = (android.view.ViewGroup) r6;
        if (r6 != 0) goto L_0x0630;
    L_0x04ff:
        r25 = new java.lang.RuntimeException;
        r26 = "Window couldn't find content container view";
        r25.<init>(r26);
        throw r25;
    L_0x0507:
        r25 = 65792; // 0x10100 float:9.2194E-41 double:3.25056E-319;
        r0 = r28;
        r1 = r25;
        r0.setFlags(r1, r10);
        goto L_0x003f;
    L_0x0513:
        r25 = 15;
        r26 = 0;
        r0 = r25;
        r1 = r26;
        r25 = r4.getBoolean(r0, r1);
        if (r25 == 0) goto L_0x0056;
    L_0x0521:
        r25 = 8;
        r0 = r28;
        r1 = r25;
        r0.requestFeature(r1);
        goto L_0x0056;
    L_0x052c:
        r25 = 0;
        goto L_0x0166;
    L_0x0530:
        r18 = 0;
        goto L_0x02a7;
    L_0x0534:
        r20 = 0;
        goto L_0x02d9;
    L_0x0538:
        r21 = 0;
        goto L_0x02e3;
    L_0x053c:
        r22 = 0;
        goto L_0x02ed;
    L_0x0540:
        r14 = 0;
        goto L_0x0311;
    L_0x0543:
        r25 = 2;
        r0 = r28;
        r1 = r25;
        r0.setNeedsMenuKey(r1);
        goto L_0x0322;
    L_0x054e:
        r25 = r9 & 24;
        if (r25 == 0) goto L_0x058a;
    L_0x0552:
        r0 = r28;
        r0 = r0.mIsFloating;
        r25 = r0;
        if (r25 == 0) goto L_0x0586;
    L_0x055a:
        r17 = new android.util.TypedValue;
        r17.<init>();
        r25 = r28.getContext();
        r25 = r25.getTheme();
        r26 = 18219042; // 0x1160022 float:2.7550744E-38 double:9.0014028E-317;
        r27 = 1;
        r0 = r25;
        r1 = r26;
        r2 = r17;
        r3 = r27;
        r0.resolveAttribute(r1, r2, r3);
        r0 = r17;
        r13 = r0.resourceId;
    L_0x057b:
        r25 = 8;
        r0 = r28;
        r1 = r25;
        r0.removeFeature(r1);
        goto L_0x04c1;
    L_0x0586:
        r13 = 17367227; // 0x10900bb float:2.516345E-38 double:8.58055E-317;
        goto L_0x057b;
    L_0x058a:
        r25 = r9 & 36;
        if (r25 == 0) goto L_0x0599;
    L_0x058e:
        r0 = r9 & 256;
        r25 = r0;
        if (r25 != 0) goto L_0x0599;
    L_0x0594:
        r13 = 17367222; // 0x10900b6 float:2.5163436E-38 double:8.580548E-317;
        goto L_0x04c1;
    L_0x0599:
        r0 = r9 & 128;
        r25 = r0;
        if (r25 == 0) goto L_0x05d7;
    L_0x059f:
        r0 = r28;
        r0 = r0.mIsFloating;
        r25 = r0;
        if (r25 == 0) goto L_0x05d3;
    L_0x05a7:
        r17 = new android.util.TypedValue;
        r17.<init>();
        r25 = r28.getContext();
        r25 = r25.getTheme();
        r26 = 18219043; // 0x1160023 float:2.7550747E-38 double:9.001403E-317;
        r27 = 1;
        r0 = r25;
        r1 = r26;
        r2 = r17;
        r3 = r27;
        r0.resolveAttribute(r1, r2, r3);
        r0 = r17;
        r13 = r0.resourceId;
    L_0x05c8:
        r25 = 8;
        r0 = r28;
        r1 = r25;
        r0.removeFeature(r1);
        goto L_0x04c1;
    L_0x05d3:
        r13 = 17367221; // 0x10900b5 float:2.5163433E-38 double:8.5805473E-317;
        goto L_0x05c8;
    L_0x05d7:
        r25 = r9 & 2;
        if (r25 != 0) goto L_0x0620;
    L_0x05db:
        r0 = r28;
        r0 = r0.mIsFloating;
        r25 = r0;
        if (r25 == 0) goto L_0x0606;
    L_0x05e3:
        r17 = new android.util.TypedValue;
        r17.<init>();
        r25 = r28.getContext();
        r25 = r25.getTheme();
        r26 = 18219044; // 0x1160024 float:2.755075E-38 double:9.0014037E-317;
        r27 = 1;
        r0 = r25;
        r1 = r26;
        r2 = r17;
        r3 = r27;
        r0.resolveAttribute(r1, r2, r3);
        r0 = r17;
        r13 = r0.resourceId;
        goto L_0x04c1;
    L_0x0606:
        r0 = r9 & 256;
        r25 = r0;
        if (r25 == 0) goto L_0x061b;
    L_0x060c:
        r25 = 47;
        r26 = 17367220; // 0x10900b4 float:2.516343E-38 double:8.580547E-317;
        r0 = r25;
        r1 = r26;
        r13 = r4.getResourceId(r0, r1);
        goto L_0x04c1;
    L_0x061b:
        r13 = 17367226; // 0x10900ba float:2.5163447E-38 double:8.5805497E-317;
        goto L_0x04c1;
    L_0x0620:
        r0 = r9 & 1024;
        r25 = r0;
        if (r25 == 0) goto L_0x062b;
    L_0x0626:
        r13 = 17367224; // 0x10900b8 float:2.5163442E-38 double:8.5805487E-317;
        goto L_0x04c1;
    L_0x062b:
        r13 = 17367223; // 0x10900b7 float:2.516344E-38 double:8.5805482E-317;
        goto L_0x04c1;
    L_0x0630:
        r25 = r9 & 32;
        if (r25 == 0) goto L_0x0649;
    L_0x0634:
        r25 = 0;
        r0 = r28;
        r1 = r25;
        r16 = r0.getCircularProgressBar(r1);
        if (r16 == 0) goto L_0x0649;
    L_0x0640:
        r25 = 1;
        r0 = r16;
        r1 = r25;
        r0.setIndeterminate(r1);
    L_0x0649:
        r0 = r9 & 2048;
        r25 = r0;
        if (r25 == 0) goto L_0x0652;
    L_0x064f:
        r28.registerSwipeCallbacks();
    L_0x0652:
        r25 = r28.getContainer();
        if (r25 != 0) goto L_0x06ee;
    L_0x0658:
        r0 = r28;
        r0 = r0.mBackgroundResource;
        r25 = r0;
        if (r25 == 0) goto L_0x06f8;
    L_0x0660:
        r25 = r28.getContext();
        r0 = r28;
        r0 = r0.mBackgroundResource;
        r26 = r0;
        r5 = r25.getDrawable(r26);
    L_0x066e:
        r0 = r28;
        r0 = r0.mDecor;
        r25 = r0;
        r0 = r25;
        r0.setWindowBackground(r5);
        r0 = r28;
        r0 = r0.mFrameResource;
        r25 = r0;
        if (r25 == 0) goto L_0x06fe;
    L_0x0681:
        r25 = r28.getContext();
        r0 = r28;
        r0 = r0.mFrameResource;
        r26 = r0;
        r11 = r25.getDrawable(r26);
    L_0x068f:
        r0 = r28;
        r0 = r0.mDecor;
        r25 = r0;
        r0 = r25;
        r0.setWindowFrame(r11);
        r0 = r28;
        r0 = r0.mDecor;
        r25 = r0;
        r0 = r28;
        r0 = r0.mElevation;
        r26 = r0;
        r25.setElevation(r26);
        r0 = r28;
        r0 = r0.mDecor;
        r25 = r0;
        r0 = r28;
        r0 = r0.mClipToOutline;
        r26 = r0;
        r25.setClipToOutline(r26);
        r0 = r28;
        r0 = r0.mTitle;
        r25 = r0;
        if (r25 == 0) goto L_0x06cd;
    L_0x06c0:
        r0 = r28;
        r0 = r0.mTitle;
        r25 = r0;
        r0 = r28;
        r1 = r25;
        r0.setTitle(r1);
    L_0x06cd:
        r0 = r28;
        r0 = r0.mTitleColor;
        r25 = r0;
        if (r25 != 0) goto L_0x06e1;
    L_0x06d5:
        r0 = r28;
        r0 = r0.mTextColor;
        r25 = r0;
        r0 = r25;
        r1 = r28;
        r1.mTitleColor = r0;
    L_0x06e1:
        r0 = r28;
        r0 = r0.mTitleColor;
        r25 = r0;
        r0 = r28;
        r1 = r25;
        r0.setTitleColor(r1);
    L_0x06ee:
        r0 = r28;
        r0 = r0.mDecor;
        r25 = r0;
        r25.finishChanging();
        return r6;
    L_0x06f8:
        r0 = r28;
        r5 = r0.mBackgroundDrawable;
        goto L_0x066e;
    L_0x06fe:
        r11 = 0;
        goto L_0x068f;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindow.generateLayout(com.android.internal.policy.impl.PhoneWindow$DecorView):android.view.ViewGroup");
    }

    public void alwaysReadCloseOnTouchAttr() {
        this.mAlwaysReadCloseOnTouchAttr = true;
    }

    private void installDecor() {
        if (this.mDecor == null) {
            this.mDecor = generateDecor();
            this.mDecor.setDescendantFocusability(262144);
            this.mDecor.setIsRootNamespace(true);
            if (!(this.mInvalidatePanelMenuPosted || this.mInvalidatePanelMenuFeatures == 0)) {
                this.mDecor.postOnAnimation(this.mInvalidatePanelMenuRunnable);
            }
        }
        if (this.mContentParent == null) {
            this.mContentParent = generateLayout(this.mDecor);
            this.mDecor.makeOptionalFitsSystemWindows();
            DecorContentParent decorContentParent = (DecorContentParent) this.mDecor.findViewById(16909176);
            if (decorContentParent != null) {
                this.mDecorContentParent = decorContentParent;
                this.mDecorContentParent.setWindowCallback(getCallback());
                if (this.mDecorContentParent.getTitle() == null) {
                    this.mDecorContentParent.setWindowTitle(this.mTitle);
                }
                int localFeatures = getLocalFeatures();
                for (int i = 0; i < 13; i += FLAG_RESOURCE_SET_ICON) {
                    if (((FLAG_RESOURCE_SET_ICON << i) & localFeatures) != 0) {
                        this.mDecorContentParent.initFeature(i);
                    }
                }
                this.mDecorContentParent.setUiOptions(this.mUiOptions);
                if ((this.mResourcesSetFlags & FLAG_RESOURCE_SET_ICON) != 0 || (this.mIconRes != 0 && !this.mDecorContentParent.hasIcon())) {
                    this.mDecorContentParent.setIcon(this.mIconRes);
                } else if ((this.mResourcesSetFlags & FLAG_RESOURCE_SET_ICON) == 0 && this.mIconRes == 0 && !this.mDecorContentParent.hasIcon()) {
                    this.mDecorContentParent.setIcon(getContext().getPackageManager().getDefaultActivityIcon());
                    this.mResourcesSetFlags |= FLAG_RESOURCE_SET_ICON_FALLBACK;
                }
                if (!((this.mResourcesSetFlags & FLAG_RESOURCE_SET_LOGO) == 0 && (this.mLogoRes == 0 || this.mDecorContentParent.hasLogo()))) {
                    this.mDecorContentParent.setLogo(this.mLogoRes);
                }
                PanelFeatureState st = getPanelState(0, SWEEP_OPEN_MENU);
                if (!isDestroyed() && (st == null || st.menu == null)) {
                    invalidatePanelMenu(8);
                }
            } else {
                this.mTitleView = (TextView) findViewById(16908310);
                if (this.mTitleView != null) {
                    this.mTitleView.setLayoutDirection(this.mDecor.getLayoutDirection());
                    if ((getLocalFeatures() & FLAG_RESOURCE_SET_LOGO) != 0) {
                        View titleContainer = findViewById(16908341);
                        if (titleContainer != null) {
                            titleContainer.setVisibility(8);
                        } else {
                            this.mTitleView.setVisibility(8);
                        }
                        if (this.mContentParent instanceof FrameLayout) {
                            ((FrameLayout) this.mContentParent).setForeground(null);
                        }
                    } else {
                        this.mTitleView.setText(this.mTitle);
                    }
                }
            }
            if (this.mDecor.getBackground() == null && this.mBackgroundFallbackResource != 0) {
                this.mDecor.setBackgroundFallback(this.mBackgroundFallbackResource);
            }
            if (hasFeature(13)) {
                if (this.mTransitionManager == null) {
                    int transitionRes = getWindowStyle().getResourceId(27, 0);
                    if (transitionRes != 0) {
                        this.mTransitionManager = TransitionInflater.from(getContext()).inflateTransitionManager(transitionRes, this.mContentParent);
                    } else {
                        this.mTransitionManager = new TransitionManager();
                    }
                }
                this.mEnterTransition = getTransition(this.mEnterTransition, null, 28);
                this.mReturnTransition = getTransition(this.mReturnTransition, USE_DEFAULT_TRANSITION, 40);
                this.mExitTransition = getTransition(this.mExitTransition, null, 29);
                this.mReenterTransition = getTransition(this.mReenterTransition, USE_DEFAULT_TRANSITION, 41);
                this.mSharedElementEnterTransition = getTransition(this.mSharedElementEnterTransition, null, 30);
                this.mSharedElementReturnTransition = getTransition(this.mSharedElementReturnTransition, USE_DEFAULT_TRANSITION, 42);
                this.mSharedElementExitTransition = getTransition(this.mSharedElementExitTransition, null, 31);
                this.mSharedElementReenterTransition = getTransition(this.mSharedElementReenterTransition, USE_DEFAULT_TRANSITION, 43);
                if (this.mAllowEnterTransitionOverlap == null) {
                    this.mAllowEnterTransitionOverlap = Boolean.valueOf(getWindowStyle().getBoolean(33, true));
                }
                if (this.mAllowReturnTransitionOverlap == null) {
                    this.mAllowReturnTransitionOverlap = Boolean.valueOf(getWindowStyle().getBoolean(32, true));
                }
                if (this.mBackgroundFadeDurationMillis < 0) {
                    this.mBackgroundFadeDurationMillis = (long) getWindowStyle().getInteger(37, DEFAULT_BACKGROUND_FADE_DURATION_MS);
                }
                if (this.mSharedElementsUseOverlay == null) {
                    this.mSharedElementsUseOverlay = Boolean.valueOf(getWindowStyle().getBoolean(44, true));
                }
            }
        }
    }

    private Transition getTransition(Transition currentValue, Transition defaultValue, int id) {
        if (currentValue != defaultValue) {
            return currentValue;
        }
        int transitionId = getWindowStyle().getResourceId(id, -1);
        Transition transition = defaultValue;
        if (!(transitionId == -1 || transitionId == 17760256)) {
            transition = TransitionInflater.from(getContext()).inflateTransition(transitionId);
            if ((transition instanceof TransitionSet) && ((TransitionSet) transition).getTransitionCount() == 0) {
                transition = null;
            }
        }
        return transition;
    }

    private Drawable loadImageURI(Uri uri) {
        Drawable drawable = null;
        try {
            drawable = Drawable.createFromStream(getContext().getContentResolver().openInputStream(uri), null);
        } catch (Exception e) {
            Log.w(TAG, "Unable to open content: " + uri);
        }
        return drawable;
    }

    private DrawableFeatureState getDrawableState(int featureId, boolean required) {
        if ((getFeatures() & (FLAG_RESOURCE_SET_ICON << featureId)) != 0) {
            DrawableFeatureState[] ar = this.mDrawables;
            if (ar == null || ar.length <= featureId) {
                DrawableFeatureState[] nar = new DrawableFeatureState[(featureId + FLAG_RESOURCE_SET_ICON)];
                if (ar != null) {
                    System.arraycopy(ar, 0, nar, 0, ar.length);
                }
                ar = nar;
                this.mDrawables = nar;
            }
            DrawableFeatureState st = ar[featureId];
            if (st != null) {
                return st;
            }
            st = new DrawableFeatureState(featureId);
            ar[featureId] = st;
            return st;
        } else if (!required) {
            return null;
        } else {
            throw new RuntimeException("The feature has not been requested");
        }
    }

    private PanelFeatureState getPanelState(int featureId, boolean required) {
        return getPanelState(featureId, required, null);
    }

    private PanelFeatureState getPanelState(int featureId, boolean required, PanelFeatureState convertPanelState) {
        if ((getFeatures() & (FLAG_RESOURCE_SET_ICON << featureId)) != 0) {
            PanelFeatureState[] ar = this.mPanels;
            if (ar == null || ar.length <= featureId) {
                PanelFeatureState[] nar = new PanelFeatureState[(featureId + FLAG_RESOURCE_SET_ICON)];
                if (ar != null) {
                    System.arraycopy(ar, 0, nar, 0, ar.length);
                }
                ar = nar;
                this.mPanels = nar;
            }
            PanelFeatureState st = ar[featureId];
            if (st != null) {
                return st;
            }
            st = convertPanelState != null ? convertPanelState : new PanelFeatureState(featureId);
            ar[featureId] = st;
            return st;
        } else if (!required) {
            return null;
        } else {
            throw new RuntimeException("The feature has not been requested");
        }
    }

    public final void setChildDrawable(int featureId, Drawable drawable) {
        DrawableFeatureState st = getDrawableState(featureId, true);
        st.child = drawable;
        updateDrawable(featureId, st, SWEEP_OPEN_MENU);
    }

    public final void setChildInt(int featureId, int value) {
        updateInt(featureId, value, SWEEP_OPEN_MENU);
    }

    public boolean isShortcutKey(int keyCode, KeyEvent event) {
        PanelFeatureState st = getPanelState(0, SWEEP_OPEN_MENU);
        if (st == null || st.menu == null || !st.menu.isShortcutKey(keyCode, event)) {
            return SWEEP_OPEN_MENU;
        }
        return true;
    }

    private void updateDrawable(int featureId, DrawableFeatureState st, boolean fromResume) {
        if (this.mContentParent != null) {
            int featureMask = FLAG_RESOURCE_SET_ICON << featureId;
            if ((getFeatures() & featureMask) != 0 || fromResume) {
                Drawable drawable = null;
                if (st != null) {
                    drawable = st.child;
                    if (drawable == null) {
                        drawable = st.local;
                    }
                    if (drawable == null) {
                        drawable = st.def;
                    }
                }
                if ((getLocalFeatures() & featureMask) == 0) {
                    if (getContainer() == null) {
                        return;
                    }
                    if (isActive() || fromResume) {
                        getContainer().setChildDrawable(featureId, drawable);
                    }
                } else if (st == null) {
                } else {
                    if (st.cur != drawable || st.curAlpha != st.alpha) {
                        st.cur = drawable;
                        st.curAlpha = st.alpha;
                        onDrawableChanged(featureId, drawable, st.alpha);
                    }
                }
            }
        }
    }

    private void updateInt(int featureId, int value, boolean fromResume) {
        if (this.mContentParent != null) {
            int featureMask = FLAG_RESOURCE_SET_ICON << featureId;
            if ((getFeatures() & featureMask) == 0 && !fromResume) {
                return;
            }
            if ((getLocalFeatures() & featureMask) != 0) {
                onIntChanged(featureId, value);
            } else if (getContainer() != null) {
                getContainer().setChildInt(featureId, value);
            }
        }
    }

    private ImageView getLeftIconView() {
        if (this.mLeftIconView != null) {
            return this.mLeftIconView;
        }
        if (this.mContentParent == null) {
            installDecor();
        }
        ImageView imageView = (ImageView) findViewById(16908338);
        this.mLeftIconView = imageView;
        return imageView;
    }

    protected void dispatchWindowAttributesChanged(LayoutParams attrs) {
        super.dispatchWindowAttributesChanged(attrs);
        if (this.mDecor != null) {
            this.mDecor.updateColorViews(null, true);
        }
    }

    private ProgressBar getCircularProgressBar(boolean shouldInstallDecor) {
        if (this.mCircularProgressBar != null) {
            return this.mCircularProgressBar;
        }
        if (this.mContentParent == null && shouldInstallDecor) {
            installDecor();
        }
        this.mCircularProgressBar = (ProgressBar) findViewById(16909174);
        if (this.mCircularProgressBar != null) {
            this.mCircularProgressBar.setVisibility(FLAG_RESOURCE_SET_ICON_FALLBACK);
        }
        return this.mCircularProgressBar;
    }

    private ProgressBar getHorizontalProgressBar(boolean shouldInstallDecor) {
        if (this.mHorizontalProgressBar != null) {
            return this.mHorizontalProgressBar;
        }
        if (this.mContentParent == null && shouldInstallDecor) {
            installDecor();
        }
        this.mHorizontalProgressBar = (ProgressBar) findViewById(16909175);
        if (this.mHorizontalProgressBar != null) {
            this.mHorizontalProgressBar.setVisibility(FLAG_RESOURCE_SET_ICON_FALLBACK);
        }
        return this.mHorizontalProgressBar;
    }

    private ImageView getRightIconView() {
        if (this.mRightIconView != null) {
            return this.mRightIconView;
        }
        if (this.mContentParent == null) {
            installDecor();
        }
        ImageView imageView = (ImageView) findViewById(16908340);
        this.mRightIconView = imageView;
        return imageView;
    }

    private void registerSwipeCallbacks() {
        SwipeDismissLayout swipeDismiss = (SwipeDismissLayout) findViewById(16908290);
        swipeDismiss.setOnDismissedListener(new OnDismissedListener() {
            public void onDismissed(SwipeDismissLayout layout) {
                PhoneWindow.this.dispatchOnWindowDismissed();
            }
        });
        swipeDismiss.setOnSwipeProgressChangedListener(new OnSwipeProgressChangedListener() {
            private static final float ALPHA_DECREASE = 0.5f;
            private boolean mIsTranslucent;

            {
                this.mIsTranslucent = PhoneWindow.SWEEP_OPEN_MENU;
            }

            public void onSwipeProgressChanged(SwipeDismissLayout layout, float progress, float translate) {
                int flags;
                LayoutParams newParams = PhoneWindow.this.getAttributes();
                newParams.x = (int) translate;
                newParams.alpha = 1.0f - (ALPHA_DECREASE * progress);
                PhoneWindow.this.setAttributes(newParams);
                if (newParams.x == 0) {
                    flags = 1024;
                } else {
                    flags = 512;
                }
                PhoneWindow.this.setFlags(flags, 1536);
            }

            public void onSwipeCancelled(SwipeDismissLayout layout) {
                LayoutParams newParams = PhoneWindow.this.getAttributes();
                newParams.x = 0;
                newParams.alpha = 1.0f;
                PhoneWindow.this.setAttributes(newParams);
                PhoneWindow.this.setFlags(1024, 1536);
            }
        });
    }

    private void callOnPanelClosed(int featureId, PanelFeatureState panel, Menu menu) {
        Window.Callback cb = getCallback();
        if (cb != null) {
            if (menu == null) {
                if (panel == null && featureId >= 0 && featureId < this.mPanels.length) {
                    panel = this.mPanels[featureId];
                }
                if (panel != null) {
                    menu = panel.menu;
                }
            }
            if ((panel == null || panel.isOpen) && !isDestroyed()) {
                cb.onPanelClosed(featureId, menu);
            }
        }
    }

    private boolean launchDefaultSearch() {
        boolean result;
        Window.Callback cb = getCallback();
        if (cb == null || isDestroyed()) {
            result = SWEEP_OPEN_MENU;
        } else {
            sendCloseSystemWindows("search");
            result = cb.onSearchRequested();
        }
        if (result || (getContext().getResources().getConfiguration().uiMode & 15) != FLAG_RESOURCE_SET_ICON_FALLBACK) {
            return result;
        }
        return ((SearchManager) getContext().getSystemService("search")).launchAssistAction(0, null, UserHandle.myUserId());
    }

    public void setVolumeControlStream(int streamType) {
        this.mVolumeControlStreamType = streamType;
    }

    public int getVolumeControlStream() {
        return this.mVolumeControlStreamType;
    }

    public void setMediaController(MediaController controller) {
        this.mMediaController = controller;
    }

    public MediaController getMediaController() {
        return this.mMediaController;
    }

    public void setEnterTransition(Transition enterTransition) {
        this.mEnterTransition = enterTransition;
    }

    public void setReturnTransition(Transition transition) {
        this.mReturnTransition = transition;
    }

    public void setExitTransition(Transition exitTransition) {
        this.mExitTransition = exitTransition;
    }

    public void setReenterTransition(Transition transition) {
        this.mReenterTransition = transition;
    }

    public void setSharedElementEnterTransition(Transition sharedElementEnterTransition) {
        this.mSharedElementEnterTransition = sharedElementEnterTransition;
    }

    public void setSharedElementReturnTransition(Transition transition) {
        this.mSharedElementReturnTransition = transition;
    }

    public void setSharedElementExitTransition(Transition sharedElementExitTransition) {
        this.mSharedElementExitTransition = sharedElementExitTransition;
    }

    public void setSharedElementReenterTransition(Transition transition) {
        this.mSharedElementReenterTransition = transition;
    }

    public Transition getEnterTransition() {
        return this.mEnterTransition;
    }

    public Transition getReturnTransition() {
        return this.mReturnTransition == USE_DEFAULT_TRANSITION ? getEnterTransition() : this.mReturnTransition;
    }

    public Transition getExitTransition() {
        return this.mExitTransition;
    }

    public Transition getReenterTransition() {
        return this.mReenterTransition == USE_DEFAULT_TRANSITION ? getExitTransition() : this.mReenterTransition;
    }

    public Transition getSharedElementEnterTransition() {
        return this.mSharedElementEnterTransition;
    }

    public Transition getSharedElementReturnTransition() {
        return this.mSharedElementReturnTransition == USE_DEFAULT_TRANSITION ? getSharedElementEnterTransition() : this.mSharedElementReturnTransition;
    }

    public Transition getSharedElementExitTransition() {
        return this.mSharedElementExitTransition;
    }

    public Transition getSharedElementReenterTransition() {
        return this.mSharedElementReenterTransition == USE_DEFAULT_TRANSITION ? getSharedElementExitTransition() : this.mSharedElementReenterTransition;
    }

    public void setAllowEnterTransitionOverlap(boolean allow) {
        this.mAllowEnterTransitionOverlap = Boolean.valueOf(allow);
    }

    public boolean getAllowEnterTransitionOverlap() {
        return this.mAllowEnterTransitionOverlap == null ? true : this.mAllowEnterTransitionOverlap.booleanValue();
    }

    public void setAllowReturnTransitionOverlap(boolean allowExitTransitionOverlap) {
        this.mAllowReturnTransitionOverlap = Boolean.valueOf(allowExitTransitionOverlap);
    }

    public boolean getAllowReturnTransitionOverlap() {
        return this.mAllowReturnTransitionOverlap == null ? true : this.mAllowReturnTransitionOverlap.booleanValue();
    }

    public long getTransitionBackgroundFadeDuration() {
        return this.mBackgroundFadeDurationMillis < 0 ? 300 : this.mBackgroundFadeDurationMillis;
    }

    public void setTransitionBackgroundFadeDuration(long fadeDurationMillis) {
        if (fadeDurationMillis < 0) {
            throw new IllegalArgumentException("negative durations are not allowed");
        }
        this.mBackgroundFadeDurationMillis = fadeDurationMillis;
    }

    public void setSharedElementsUseOverlay(boolean sharedElementsUseOverlay) {
        this.mSharedElementsUseOverlay = Boolean.valueOf(sharedElementsUseOverlay);
    }

    public boolean getSharedElementsUseOverlay() {
        return this.mSharedElementsUseOverlay == null ? true : this.mSharedElementsUseOverlay.booleanValue();
    }

    void sendCloseSystemWindows() {
        PhoneWindowManager.sendCloseSystemWindows(getContext(), null);
    }

    void sendCloseSystemWindows(String reason) {
        PhoneWindowManager.sendCloseSystemWindows(getContext(), reason);
    }

    public int getStatusBarColor() {
        return this.mStatusBarColor;
    }

    public void setStatusBarColor(int color) {
        this.mStatusBarColor = color;
        this.mForcedStatusBarColor = true;
        if (this.mDecor != null) {
            this.mDecor.updateColorViews(null, SWEEP_OPEN_MENU);
        }
    }

    public int getNavigationBarColor() {
        return this.mNavigationBarColor;
    }

    public void setNavigationBarColor(int color) {
        this.mNavigationBarColor = color;
        this.mForcedNavigationBarColor = true;
        if (this.mDecor != null) {
            this.mDecor.updateColorViews(null, SWEEP_OPEN_MENU);
        }
    }
}
