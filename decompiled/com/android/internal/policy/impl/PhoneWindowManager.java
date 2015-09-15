package com.android.internal.policy.impl;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.IUiModeManager;
import android.app.IUiModeManager.Stub;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.UiModeManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.database.ContentObserver;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioAttributes.Builder;
import android.media.AudioManager;
import android.media.IAudioService;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.session.MediaSessionLegacyHelper;
import android.os.Build;
import android.os.Bundle;
import android.os.FactoryTest;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UEventObserver;
import android.os.UEventObserver.UEvent;
import android.os.UserHandle;
import android.os.Vibrator;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.service.dreams.DreamManagerInternal;
import android.service.dreams.IDreamManager;
import android.telecom.TelecomManager;
import android.util.EventLog;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.view.Display;
import android.view.IApplicationToken;
import android.view.IWindowManager;
import android.view.InputChannel;
import android.view.InputEventReceiver;
import android.view.InputEventReceiver.Factory;
import android.view.KeyCharacterMap;
import android.view.KeyCharacterMap.FallbackAction;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerInternal;
import android.view.WindowManagerPolicy;
import android.view.WindowManagerPolicy.FakeWindow;
import android.view.WindowManagerPolicy.OnKeyguardExitResult;
import android.view.WindowManagerPolicy.ScreenOnListener;
import android.view.WindowManagerPolicy.WindowManagerFuncs;
import android.view.WindowManagerPolicy.WindowState;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate;
import com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.ShowListener;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.widget.PointerLocationView;
import com.android.server.LocalServices;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;

public class PhoneWindowManager implements WindowManagerPolicy {
    static final int APPLICATION_MEDIA_OVERLAY_SUBLAYER = -1;
    static final int APPLICATION_MEDIA_SUBLAYER = -2;
    static final int APPLICATION_PANEL_SUBLAYER = 1;
    static final int APPLICATION_SUB_PANEL_SUBLAYER = 2;
    private static final int BRIGHTNESS_STEPS = 10;
    static final boolean DEBUG = false;
    static final boolean DEBUG_INPUT = false;
    static final boolean DEBUG_KEYGUARD = false;
    static final boolean DEBUG_LAYOUT = false;
    static final boolean DEBUG_STARTING_WINDOW = false;
    static final boolean DEBUG_WAKEUP = false;
    private static final int DISMISS_KEYGUARD_CONTINUE = 2;
    private static final int DISMISS_KEYGUARD_NONE = 0;
    private static final int DISMISS_KEYGUARD_START = 1;
    static final int DOUBLE_TAP_HOME_NOTHING = 0;
    static final int DOUBLE_TAP_HOME_RECENT_SYSTEM_UI = 1;
    static final boolean ENABLE_CAR_DOCK_HOME_CAPTURE = true;
    static final boolean ENABLE_DESK_DOCK_HOME_CAPTURE = false;
    private static final float KEYGUARD_SCREENSHOT_CHORD_DELAY_MULTIPLIER = 2.5f;
    static final int LONG_PRESS_HOME_ASSIST = 2;
    static final int LONG_PRESS_HOME_NOTHING = 0;
    static final int LONG_PRESS_HOME_RECENT_SYSTEM_UI = 1;
    static final int LONG_PRESS_POWER_GLOBAL_ACTIONS = 1;
    static final int LONG_PRESS_POWER_NOTHING = 0;
    static final int LONG_PRESS_POWER_SHUT_OFF = 2;
    static final int LONG_PRESS_POWER_SHUT_OFF_NO_CONFIRM = 3;
    private static final int MSG_DISABLE_POINTER_LOCATION = 2;
    private static final int MSG_DISPATCH_MEDIA_KEY_REPEAT_WITH_WAKE_LOCK = 4;
    private static final int MSG_DISPATCH_MEDIA_KEY_WITH_WAKE_LOCK = 3;
    private static final int MSG_DISPATCH_SHOW_GLOBAL_ACTIONS = 10;
    private static final int MSG_DISPATCH_SHOW_RECENTS = 9;
    private static final int MSG_ENABLE_POINTER_LOCATION = 1;
    private static final int MSG_HIDE_BOOT_MESSAGE = 11;
    private static final int MSG_KEYGUARD_DRAWN_COMPLETE = 5;
    private static final int MSG_KEYGUARD_DRAWN_TIMEOUT = 6;
    private static final int MSG_LAUNCH_VOICE_ASSIST_WITH_WAKE_LOCK = 12;
    private static final int MSG_POWER_DELAYED_PRESS = 13;
    private static final int MSG_POWER_LONG_PRESS = 14;
    private static final int MSG_WINDOW_MANAGER_DRAWN_COMPLETE = 7;
    static final int MULTI_PRESS_POWER_BRIGHTNESS_BOOST = 2;
    static final int MULTI_PRESS_POWER_NOTHING = 0;
    static final int MULTI_PRESS_POWER_THEATER_MODE = 1;
    static final boolean PRINT_ANIM = false;
    private static final long SCREENSHOT_CHORD_DEBOUNCE_DELAY_MILLIS = 150;
    static final int SHORT_PRESS_POWER_GO_HOME = 4;
    static final int SHORT_PRESS_POWER_GO_TO_SLEEP = 1;
    static final int SHORT_PRESS_POWER_NOTHING = 0;
    static final int SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP = 2;
    static final int SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME = 3;
    static final int SHORT_PRESS_SLEEP_GO_TO_SLEEP = 0;
    static final int SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME = 1;
    static final boolean SHOW_PROCESSES_ON_ALT_MENU = false;
    static final boolean SHOW_STARTING_ANIMATIONS = true;
    public static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";
    public static final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
    public static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    public static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    public static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    static final int SYSTEM_UI_CHANGING_LAYOUT = -1073709050;
    static final String TAG = "WindowManager";
    private static final AudioAttributes VIBRATION_ATTRIBUTES = null;
    static final int WAITING_FOR_DRAWN_TIMEOUT = 1000;
    private static final int[] WINDOW_TYPES_WHERE_HOME_DOESNT_WORK = null;
    static final boolean localLOGV = false;
    static final Rect mTmpContentFrame = null;
    static final Rect mTmpDecorFrame = null;
    static final Rect mTmpDisplayFrame = null;
    static final Rect mTmpNavigationFrame = null;
    static final Rect mTmpOverscanFrame = null;
    static final Rect mTmpParentFrame = null;
    static final Rect mTmpStableFrame = null;
    static final Rect mTmpVisibleFrame = null;
    static SparseArray<String> sApplicationLaunchKeyCategories;
    boolean mAccelerometerDefault;
    AccessibilityManager mAccessibilityManager;
    int mAllowAllRotations;
    boolean mAllowLockscreenWhenOn;
    private boolean mAllowTheaterModeWakeFromCameraLens;
    private boolean mAllowTheaterModeWakeFromKey;
    private boolean mAllowTheaterModeWakeFromLidSwitch;
    private boolean mAllowTheaterModeWakeFromMotion;
    private boolean mAllowTheaterModeWakeFromMotionWhenNotDreaming;
    private boolean mAllowTheaterModeWakeFromPowerKey;
    private boolean mAllowTheaterModeWakeFromWakeGesture;
    HashSet<IApplicationToken> mAppsThatDismissKeyguard;
    HashSet<IApplicationToken> mAppsToBeHidden;
    boolean mAssistKeyLongPressed;
    boolean mAwake;
    volatile boolean mBeganFromNonInteractive;
    boolean mBootMessageNeedsHiding;
    ProgressDialog mBootMsgDialog;
    WakeLock mBroadcastWakeLock;
    BurnInProtectionHelper mBurnInProtectionHelper;
    long[] mCalendarDateVibePattern;
    int mCameraLensCoverState;
    boolean mCanHideNavigationBar;
    boolean mCarDockEnablesAccelerometer;
    Intent mCarDockIntent;
    int mCarDockRotation;
    private final Runnable mClearHideNavigationFlag;
    long[] mClockTickVibePattern;
    boolean mConsumeSearchKeyUp;
    int mContentBottom;
    int mContentLeft;
    int mContentRight;
    int mContentTop;
    Context mContext;
    int mCurBottom;
    int mCurLeft;
    int mCurRight;
    int mCurTop;
    int mCurrentAppOrientation;
    private int mCurrentUserId;
    int mDemoHdmiRotation;
    boolean mDemoHdmiRotationLock;
    int mDemoRotation;
    boolean mDemoRotationLock;
    boolean mDeskDockEnablesAccelerometer;
    Intent mDeskDockIntent;
    int mDeskDockRotation;
    int mDismissKeyguard;
    Display mDisplay;
    int mDockBottom;
    int mDockLayer;
    int mDockLeft;
    int mDockMode;
    BroadcastReceiver mDockReceiver;
    int mDockRight;
    int mDockTop;
    int mDoublePressOnPowerBehavior;
    private int mDoubleTapOnHomeBehavior;
    DreamManagerInternal mDreamManagerInternal;
    BroadcastReceiver mDreamReceiver;
    boolean mDreamingLockscreen;
    boolean mEnableShiftMenuBugReports;
    volatile boolean mEndCallKeyHandled;
    private final Runnable mEndCallLongPress;
    int mEndcallBehavior;
    private final SparseArray<FallbackAction> mFallbackActions;
    IApplicationToken mFocusedApp;
    WindowState mFocusedWindow;
    int mForceClearedSystemUiFlags;
    private boolean mForceDefaultOrientation;
    boolean mForceStatusBar;
    boolean mForceStatusBarFromKeyguard;
    boolean mForcingShowNavBar;
    int mForcingShowNavBarLayer;
    GlobalActions mGlobalActions;
    private GlobalKeyManager mGlobalKeyManager;
    private boolean mGoToSleepOnButtonPressTheaterMode;
    private UEventObserver mHDMIObserver;
    Handler mHandler;
    boolean mHasNavigationBar;
    boolean mHasSoftInput;
    boolean mHaveBuiltInKeyboard;
    boolean mHavePendingMediaKeyRepeatWithWakeLock;
    boolean mHdmiPlugged;
    boolean mHideLockScreen;
    FakeWindow mHideNavFakeWindow;
    final Factory mHideNavInputEventReceiverFactory;
    boolean mHomeConsumed;
    boolean mHomeDoubleTapPending;
    private final Runnable mHomeDoubleTapTimeoutRunnable;
    Intent mHomeIntent;
    boolean mHomePressed;
    private ImmersiveModeConfirmation mImmersiveModeConfirmation;
    int mIncallPowerBehavior;
    long[] mKeyboardTapVibePattern;
    KeyguardServiceDelegate mKeyguardDelegate;
    final ShowListener mKeyguardDelegateCallback;
    boolean mKeyguardDrawComplete;
    private boolean mKeyguardDrawnOnce;
    private boolean mKeyguardHidden;
    volatile boolean mKeyguardOccluded;
    private WindowState mKeyguardScrim;
    boolean mKeyguardSecure;
    boolean mKeyguardSecureIncludingHidden;
    int mLandscapeRotation;
    boolean mLanguageSwitchKeyPressed;
    boolean mLastFocusNeedsMenu;
    WindowState mLastInputMethodTargetWindow;
    WindowState mLastInputMethodWindow;
    int mLastSystemUiFlags;
    boolean mLidControlsSleep;
    int mLidKeyboardAccessibility;
    int mLidNavigationAccessibility;
    int mLidOpenRotation;
    int mLidState;
    private final Object mLock;
    int mLockScreenTimeout;
    boolean mLockScreenTimerActive;
    private final LogDecelerateInterpolator mLogDecelerateInterpolator;
    private int mLongPressOnHomeBehavior;
    int mLongPressOnPowerBehavior;
    long[] mLongPressVibePattern;
    BroadcastReceiver mMultiuserReceiver;
    WindowState mNavigationBar;
    boolean mNavigationBarCanMove;
    private final BarController mNavigationBarController;
    int[] mNavigationBarHeightForRotation;
    boolean mNavigationBarOnBottom;
    int[] mNavigationBarWidthForRotation;
    MyOrientationListener mOrientationListener;
    boolean mOrientationSensorEnabled;
    int mOverscanBottom;
    int mOverscanLeft;
    int mOverscanRight;
    int mOverscanScreenHeight;
    int mOverscanScreenLeft;
    int mOverscanScreenTop;
    int mOverscanScreenWidth;
    int mOverscanTop;
    boolean mPendingMetaAction;
    int mPointerLocationMode;
    PointerLocationView mPointerLocationView;
    int mPortraitRotation;
    volatile boolean mPowerKeyHandled;
    volatile int mPowerKeyPressCounter;
    WakeLock mPowerKeyWakeLock;
    PowerManager mPowerManager;
    boolean mPreloadedRecentApps;
    int mRecentAppsHeldModifiers;
    boolean mRecentsVisible;
    private final Runnable mRequestTransientNav;
    int mResettingSystemUiFlags;
    int mRestrictedOverscanScreenHeight;
    int mRestrictedOverscanScreenLeft;
    int mRestrictedOverscanScreenTop;
    int mRestrictedOverscanScreenWidth;
    int mRestrictedScreenHeight;
    int mRestrictedScreenLeft;
    int mRestrictedScreenTop;
    int mRestrictedScreenWidth;
    boolean mSafeMode;
    long[] mSafeModeDisabledVibePattern;
    long[] mSafeModeEnabledVibePattern;
    ScreenLockTimeout mScreenLockTimeout;
    boolean mScreenOnEarly;
    boolean mScreenOnFully;
    ScreenOnListener mScreenOnListener;
    private boolean mScreenshotChordEnabled;
    private long mScreenshotChordPowerKeyTime;
    private boolean mScreenshotChordPowerKeyTriggered;
    private boolean mScreenshotChordVolumeDownKeyConsumed;
    private long mScreenshotChordVolumeDownKeyTime;
    private boolean mScreenshotChordVolumeDownKeyTriggered;
    private boolean mScreenshotChordVolumeUpKeyTriggered;
    ServiceConnection mScreenshotConnection;
    final Object mScreenshotLock;
    private final Runnable mScreenshotRunnable;
    final Runnable mScreenshotTimeout;
    boolean mSearchKeyShortcutPending;
    SearchManager mSearchManager;
    int mSeascapeRotation;
    final Object mServiceAquireLock;
    SettingsObserver mSettingsObserver;
    int mShortPressOnPowerBehavior;
    int mShortPressOnSleepBehavior;
    ShortcutManager mShortcutManager;
    boolean mShowingDream;
    boolean mShowingLockscreen;
    int mStableBottom;
    int mStableFullscreenBottom;
    int mStableFullscreenLeft;
    int mStableFullscreenRight;
    int mStableFullscreenTop;
    int mStableLeft;
    int mStableRight;
    int mStableTop;
    WindowState mStatusBar;
    private final BarController mStatusBarController;
    int mStatusBarHeight;
    int mStatusBarLayer;
    IStatusBarService mStatusBarService;
    boolean mSupportAutoRotation;
    private boolean mSupportLongPressPowerWhenNonInteractive;
    boolean mSystemBooted;
    int mSystemBottom;
    private SystemGesturesPointerEventListener mSystemGestures;
    int mSystemLeft;
    boolean mSystemReady;
    int mSystemRight;
    int mSystemTop;
    WindowState mTopFullscreenOpaqueWindowState;
    boolean mTopIsFullscreen;
    boolean mTranslucentDecorEnabled;
    int mTriplePressOnPowerBehavior;
    int mUiMode;
    IUiModeManager mUiModeManager;
    int mUndockedHdmiRotation;
    int mUnrestrictedScreenHeight;
    int mUnrestrictedScreenLeft;
    int mUnrestrictedScreenTop;
    int mUnrestrictedScreenWidth;
    int mUpsideDownRotation;
    int mUserRotation;
    int mUserRotationMode;
    Vibrator mVibrator;
    long[] mVirtualKeyVibePattern;
    int mVoiceContentBottom;
    int mVoiceContentLeft;
    int mVoiceContentRight;
    int mVoiceContentTop;
    boolean mWakeGestureEnabledSetting;
    MyWakeGestureListener mWakeGestureListener;
    private WindowState mWinDismissingKeyguard;
    private WindowState mWinShowWhenLocked;
    IWindowManager mWindowManager;
    final Runnable mWindowManagerDrawCallback;
    boolean mWindowManagerDrawComplete;
    WindowManagerFuncs mWindowManagerFuncs;
    WindowManagerInternal mWindowManagerInternal;

    /* renamed from: com.android.internal.policy.impl.PhoneWindowManager.11 */
    class AnonymousClass11 implements Runnable {
        final /* synthetic */ PhoneWindowManager this$0;

        AnonymousClass11(com.android.internal.policy.impl.PhoneWindowManager r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindowManager.11.<init>(com.android.internal.policy.impl.PhoneWindowManager):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindowManager.11.<init>(com.android.internal.policy.impl.PhoneWindowManager):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindowManager.11.<init>(com.android.internal.policy.impl.PhoneWindowManager):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindowManager.11.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindowManager.11.run():void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindowManager.11.run():void");
        }
    }

    /* renamed from: com.android.internal.policy.impl.PhoneWindowManager.12 */
    class AnonymousClass12 implements Runnable {
        final /* synthetic */ PhoneWindowManager this$0;

        AnonymousClass12(com.android.internal.policy.impl.PhoneWindowManager r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindowManager.12.<init>(com.android.internal.policy.impl.PhoneWindowManager):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindowManager.12.<init>(com.android.internal.policy.impl.PhoneWindowManager):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindowManager.12.<init>(com.android.internal.policy.impl.PhoneWindowManager):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindowManager.12.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindowManager.12.run():void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindowManager.12.run():void");
        }
    }

    /* renamed from: com.android.internal.policy.impl.PhoneWindowManager.13 */
    class AnonymousClass13 implements Runnable {
        final /* synthetic */ PhoneWindowManager this$0;

        AnonymousClass13(PhoneWindowManager phoneWindowManager) {
            this.this$0 = phoneWindowManager;
        }

        public void run() {
            synchronized (this.this$0.mScreenshotLock) {
                if (this.this$0.mScreenshotConnection != null) {
                    this.this$0.mContext.unbindService(this.this$0.mScreenshotConnection);
                    this.this$0.mScreenshotConnection = null;
                }
            }
        }
    }

    /* renamed from: com.android.internal.policy.impl.PhoneWindowManager.14 */
    class AnonymousClass14 implements ServiceConnection {
        final /* synthetic */ PhoneWindowManager this$0;

        /* renamed from: com.android.internal.policy.impl.PhoneWindowManager.14.1 */
        class AnonymousClass1 extends Handler {
            final /* synthetic */ AnonymousClass14 this$1;
            final /* synthetic */ ServiceConnection val$myConn;

            AnonymousClass1(com.android.internal.policy.impl.PhoneWindowManager.AnonymousClass14 r1, android.os.Looper r2, android.content.ServiceConnection r3) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindowManager.14.1.<init>(com.android.internal.policy.impl.PhoneWindowManager$14, android.os.Looper, android.content.ServiceConnection):void
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
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindowManager.14.1.<init>(com.android.internal.policy.impl.PhoneWindowManager$14, android.os.Looper, android.content.ServiceConnection):void
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
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindowManager.14.1.<init>(com.android.internal.policy.impl.PhoneWindowManager$14, android.os.Looper, android.content.ServiceConnection):void");
            }

            public void handleMessage(android.os.Message r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindowManager.14.1.handleMessage(android.os.Message):void
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
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindowManager.14.1.handleMessage(android.os.Message):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 9 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 10 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindowManager.14.1.handleMessage(android.os.Message):void");
            }
        }

        AnonymousClass14(com.android.internal.policy.impl.PhoneWindowManager r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindowManager.14.<init>(com.android.internal.policy.impl.PhoneWindowManager):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindowManager.14.<init>(com.android.internal.policy.impl.PhoneWindowManager):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindowManager.14.<init>(com.android.internal.policy.impl.PhoneWindowManager):void");
        }

        public void onServiceConnected(android.content.ComponentName r1, android.os.IBinder r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindowManager.14.onServiceConnected(android.content.ComponentName, android.os.IBinder):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindowManager.14.onServiceConnected(android.content.ComponentName, android.os.IBinder):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindowManager.14.onServiceConnected(android.content.ComponentName, android.os.IBinder):void");
        }

        public void onServiceDisconnected(ComponentName name) {
        }
    }

    /* renamed from: com.android.internal.policy.impl.PhoneWindowManager.15 */
    class AnonymousClass15 extends BroadcastReceiver {
        final /* synthetic */ PhoneWindowManager this$0;

        AnonymousClass15(PhoneWindowManager phoneWindowManager) {
            this.this$0 = phoneWindowManager;
        }

        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.DOCK_EVENT".equals(intent.getAction())) {
                this.this$0.mDockMode = intent.getIntExtra("android.intent.extra.DOCK_STATE", PhoneWindowManager.SHORT_PRESS_SLEEP_GO_TO_SLEEP);
            } else {
                try {
                    IUiModeManager uiModeService = Stub.asInterface(ServiceManager.getService("uimode"));
                    this.this$0.mUiMode = uiModeService.getCurrentModeType();
                } catch (RemoteException e) {
                }
            }
            this.this$0.updateRotation(PhoneWindowManager.SHOW_STARTING_ANIMATIONS);
            synchronized (this.this$0.mLock) {
                this.this$0.updateOrientationListenerLp();
            }
        }
    }

    /* renamed from: com.android.internal.policy.impl.PhoneWindowManager.16 */
    class AnonymousClass16 extends BroadcastReceiver {
        final /* synthetic */ PhoneWindowManager this$0;

        AnonymousClass16(PhoneWindowManager phoneWindowManager) {
            this.this$0 = phoneWindowManager;
        }

        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.DREAMING_STARTED".equals(intent.getAction())) {
                if (this.this$0.mKeyguardDelegate != null) {
                    this.this$0.mKeyguardDelegate.onDreamingStarted();
                }
            } else if ("android.intent.action.DREAMING_STOPPED".equals(intent.getAction()) && this.this$0.mKeyguardDelegate != null) {
                this.this$0.mKeyguardDelegate.onDreamingStopped();
            }
        }
    }

    /* renamed from: com.android.internal.policy.impl.PhoneWindowManager.17 */
    class AnonymousClass17 extends BroadcastReceiver {
        final /* synthetic */ PhoneWindowManager this$0;

        AnonymousClass17(PhoneWindowManager phoneWindowManager) {
            this.this$0 = phoneWindowManager;
        }

        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.USER_SWITCHED".equals(intent.getAction())) {
                this.this$0.mSettingsObserver.onChange(PhoneWindowManager.SHOW_PROCESSES_ON_ALT_MENU);
                synchronized (this.this$0.mWindowManagerFuncs.getWindowManagerLock()) {
                    this.this$0.mLastSystemUiFlags = PhoneWindowManager.SHORT_PRESS_SLEEP_GO_TO_SLEEP;
                    this.this$0.updateSystemUiVisibilityLw();
                }
            }
        }
    }

    /* renamed from: com.android.internal.policy.impl.PhoneWindowManager.18 */
    class AnonymousClass18 implements Runnable {
        final /* synthetic */ PhoneWindowManager this$0;

        AnonymousClass18(PhoneWindowManager phoneWindowManager) {
            this.this$0 = phoneWindowManager;
        }

        public void run() {
            this.this$0.requestTransientBars(this.this$0.mNavigationBar);
        }
    }

    /* renamed from: com.android.internal.policy.impl.PhoneWindowManager.19 */
    class AnonymousClass19 implements Runnable {
        final /* synthetic */ PhoneWindowManager this$0;

        AnonymousClass19(com.android.internal.policy.impl.PhoneWindowManager r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindowManager.19.<init>(com.android.internal.policy.impl.PhoneWindowManager):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindowManager.19.<init>(com.android.internal.policy.impl.PhoneWindowManager):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindowManager.19.<init>(com.android.internal.policy.impl.PhoneWindowManager):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindowManager.19.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindowManager.19.run():void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindowManager.19.run():void");
        }
    }

    /* renamed from: com.android.internal.policy.impl.PhoneWindowManager.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ PhoneWindowManager this$0;

        AnonymousClass1(PhoneWindowManager phoneWindowManager) {
            this.this$0 = phoneWindowManager;
        }

        public void run() {
            this.this$0.mHandler.sendEmptyMessage(PhoneWindowManager.MSG_WINDOW_MANAGER_DRAWN_COMPLETE);
        }
    }

    /* renamed from: com.android.internal.policy.impl.PhoneWindowManager.20 */
    class AnonymousClass20 implements Runnable {
        final /* synthetic */ PhoneWindowManager this$0;

        AnonymousClass20(com.android.internal.policy.impl.PhoneWindowManager r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindowManager.20.<init>(com.android.internal.policy.impl.PhoneWindowManager):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindowManager.20.<init>(com.android.internal.policy.impl.PhoneWindowManager):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindowManager.20.<init>(com.android.internal.policy.impl.PhoneWindowManager):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindowManager.20.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindowManager.20.run():void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindowManager.20.run():void");
        }
    }

    /* renamed from: com.android.internal.policy.impl.PhoneWindowManager.21 */
    class AnonymousClass21 implements Runnable {
        final /* synthetic */ PhoneWindowManager this$0;

        AnonymousClass21(PhoneWindowManager phoneWindowManager) {
            this.this$0 = phoneWindowManager;
        }

        public void run() {
            this.this$0.updateSettings();
        }
    }

    /* renamed from: com.android.internal.policy.impl.PhoneWindowManager.22 */
    class AnonymousClass22 implements Runnable {
        final /* synthetic */ PhoneWindowManager this$0;
        final /* synthetic */ CharSequence val$msg;

        /* renamed from: com.android.internal.policy.impl.PhoneWindowManager.22.1 */
        class AnonymousClass1 extends ProgressDialog {
            final /* synthetic */ AnonymousClass22 this$1;

            AnonymousClass1(AnonymousClass22 anonymousClass22, Context x0, int x1) {
                this.this$1 = anonymousClass22;
                super(x0, x1);
            }

            public boolean dispatchKeyEvent(KeyEvent event) {
                return PhoneWindowManager.SHOW_STARTING_ANIMATIONS;
            }

            public boolean dispatchKeyShortcutEvent(KeyEvent event) {
                return PhoneWindowManager.SHOW_STARTING_ANIMATIONS;
            }

            public boolean dispatchTouchEvent(MotionEvent ev) {
                return PhoneWindowManager.SHOW_STARTING_ANIMATIONS;
            }

            public boolean dispatchTrackballEvent(MotionEvent ev) {
                return PhoneWindowManager.SHOW_STARTING_ANIMATIONS;
            }

            public boolean dispatchGenericMotionEvent(MotionEvent ev) {
                return PhoneWindowManager.SHOW_STARTING_ANIMATIONS;
            }

            public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
                return PhoneWindowManager.SHOW_STARTING_ANIMATIONS;
            }
        }

        AnonymousClass22(PhoneWindowManager phoneWindowManager, CharSequence charSequence) {
            this.this$0 = phoneWindowManager;
            this.val$msg = charSequence;
        }

        public void run() {
            if (this.this$0.mBootMsgDialog == null) {
                int theme;
                if (this.this$0.mContext.getPackageManager().hasSystemFeature("android.hardware.type.watch")) {
                    theme = 16975039;
                } else if (this.this$0.mContext.getPackageManager().hasSystemFeature("android.hardware.type.television")) {
                    theme = 16975013;
                } else {
                    theme = PhoneWindowManager.SHORT_PRESS_SLEEP_GO_TO_SLEEP;
                }
                this.this$0.mBootMsgDialog = new AnonymousClass1(this, this.this$0.mContext, theme);
                if (this.this$0.mContext.getPackageManager().isUpgrade()) {
                    this.this$0.mBootMsgDialog.setTitle(17040540);
                } else {
                    this.this$0.mBootMsgDialog.setTitle(17040541);
                }
                this.this$0.mBootMsgDialog.setProgressStyle(PhoneWindowManager.SHORT_PRESS_SLEEP_GO_TO_SLEEP);
                this.this$0.mBootMsgDialog.setIndeterminate(PhoneWindowManager.SHOW_STARTING_ANIMATIONS);
                this.this$0.mBootMsgDialog.getWindow().setType(2021);
                this.this$0.mBootMsgDialog.getWindow().addFlags(258);
                this.this$0.mBootMsgDialog.getWindow().setDimAmount(1.0f);
                LayoutParams lp = this.this$0.mBootMsgDialog.getWindow().getAttributes();
                lp.screenOrientation = PhoneWindowManager.MSG_KEYGUARD_DRAWN_COMPLETE;
                this.this$0.mBootMsgDialog.getWindow().setAttributes(lp);
                this.this$0.mBootMsgDialog.setCancelable(PhoneWindowManager.SHOW_PROCESSES_ON_ALT_MENU);
                this.this$0.mBootMsgDialog.show();
            }
            this.this$0.mBootMsgDialog.setMessage(this.val$msg);
        }
    }

    /* renamed from: com.android.internal.policy.impl.PhoneWindowManager.23 */
    class AnonymousClass23 implements Runnable {
        final /* synthetic */ PhoneWindowManager this$0;
        final /* synthetic */ boolean val$needsMenu;
        final /* synthetic */ int val$visibility;
        final /* synthetic */ WindowState val$win;

        AnonymousClass23(PhoneWindowManager phoneWindowManager, int i, WindowState windowState, boolean z) {
            this.this$0 = phoneWindowManager;
            this.val$visibility = i;
            this.val$win = windowState;
            this.val$needsMenu = z;
        }

        public void run() {
            try {
                IStatusBarService statusbar = this.this$0.getStatusBarService();
                if (statusbar != null) {
                    statusbar.setSystemUiVisibility(this.val$visibility, PhoneWindowManager.APPLICATION_MEDIA_OVERLAY_SUBLAYER, this.val$win.toString());
                    statusbar.topAppWindowChanged(this.val$needsMenu);
                }
            } catch (RemoteException e) {
                this.this$0.mStatusBarService = null;
            }
        }
    }

    /* renamed from: com.android.internal.policy.impl.PhoneWindowManager.2 */
    class AnonymousClass2 implements ShowListener {
        final /* synthetic */ PhoneWindowManager this$0;

        AnonymousClass2(PhoneWindowManager phoneWindowManager) {
            this.this$0 = phoneWindowManager;
        }

        public void onShown(IBinder windowToken) {
            this.this$0.mHandler.sendEmptyMessage(PhoneWindowManager.MSG_KEYGUARD_DRAWN_COMPLETE);
        }
    }

    /* renamed from: com.android.internal.policy.impl.PhoneWindowManager.3 */
    class AnonymousClass3 extends UEventObserver {
        final /* synthetic */ PhoneWindowManager this$0;

        AnonymousClass3(PhoneWindowManager phoneWindowManager) {
            this.this$0 = phoneWindowManager;
        }

        public void onUEvent(UEvent event) {
            this.this$0.setHdmiPlugged("1".equals(event.get("SWITCH_STATE")));
        }
    }

    /* renamed from: com.android.internal.policy.impl.PhoneWindowManager.4 */
    class AnonymousClass4 implements Runnable {
        final /* synthetic */ PhoneWindowManager this$0;

        AnonymousClass4(PhoneWindowManager phoneWindowManager) {
            this.this$0 = phoneWindowManager;
        }

        public void run() {
            this.this$0.mEndCallKeyHandled = PhoneWindowManager.SHOW_STARTING_ANIMATIONS;
            if (!this.this$0.performHapticFeedbackLw(null, PhoneWindowManager.SHORT_PRESS_SLEEP_GO_TO_SLEEP, PhoneWindowManager.SHOW_PROCESSES_ON_ALT_MENU)) {
                this.this$0.performAuditoryFeedbackForAccessibilityIfNeed();
            }
            this.this$0.showGlobalActionsInternal();
        }
    }

    /* renamed from: com.android.internal.policy.impl.PhoneWindowManager.5 */
    class AnonymousClass5 implements Runnable {
        final /* synthetic */ PhoneWindowManager this$0;

        AnonymousClass5(PhoneWindowManager phoneWindowManager) {
            this.this$0 = phoneWindowManager;
        }

        public void run() {
            this.this$0.takeScreenshot();
        }
    }

    /* renamed from: com.android.internal.policy.impl.PhoneWindowManager.6 */
    class AnonymousClass6 implements Runnable {
        final /* synthetic */ PhoneWindowManager this$0;

        AnonymousClass6(PhoneWindowManager phoneWindowManager) {
            this.this$0 = phoneWindowManager;
        }

        public void run() {
            if (this.this$0.mHomeDoubleTapPending) {
                this.this$0.mHomeDoubleTapPending = PhoneWindowManager.SHOW_PROCESSES_ON_ALT_MENU;
                this.this$0.handleShortPressOnHome();
            }
        }
    }

    /* renamed from: com.android.internal.policy.impl.PhoneWindowManager.7 */
    class AnonymousClass7 implements Callbacks {
        final /* synthetic */ PhoneWindowManager this$0;

        AnonymousClass7(PhoneWindowManager phoneWindowManager) {
            this.this$0 = phoneWindowManager;
        }

        public void onSwipeFromTop() {
            if (this.this$0.mStatusBar != null) {
                this.this$0.requestTransientBars(this.this$0.mStatusBar);
            }
        }

        public void onSwipeFromBottom() {
            if (this.this$0.mNavigationBar != null && this.this$0.mNavigationBarOnBottom) {
                this.this$0.requestTransientBars(this.this$0.mNavigationBar);
            }
        }

        public void onSwipeFromRight() {
            if (this.this$0.mNavigationBar != null && !this.this$0.mNavigationBarOnBottom) {
                this.this$0.requestTransientBars(this.this$0.mNavigationBar);
            }
        }

        public void onDebug() {
        }
    }

    /* renamed from: com.android.internal.policy.impl.PhoneWindowManager.8 */
    class AnonymousClass8 implements OnKeyguardExitResult {
        final /* synthetic */ PhoneWindowManager this$0;
        final /* synthetic */ boolean val$awakenFromDreams;

        AnonymousClass8(com.android.internal.policy.impl.PhoneWindowManager r1, boolean r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindowManager.8.<init>(com.android.internal.policy.impl.PhoneWindowManager, boolean):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindowManager.8.<init>(com.android.internal.policy.impl.PhoneWindowManager, boolean):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindowManager.8.<init>(com.android.internal.policy.impl.PhoneWindowManager, boolean):void");
        }

        public void onKeyguardExitResult(boolean r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindowManager.8.onKeyguardExitResult(boolean):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindowManager.8.onKeyguardExitResult(boolean):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindowManager.8.onKeyguardExitResult(boolean):void");
        }
    }

    /* renamed from: com.android.internal.policy.impl.PhoneWindowManager.9 */
    class AnonymousClass9 implements Runnable {
        final /* synthetic */ PhoneWindowManager this$0;

        AnonymousClass9(PhoneWindowManager phoneWindowManager) {
            this.this$0 = phoneWindowManager;
        }

        public void run() {
            synchronized (this.this$0.mWindowManagerFuncs.getWindowManagerLock()) {
                PhoneWindowManager phoneWindowManager = this.this$0;
                phoneWindowManager.mForceClearedSystemUiFlags &= -3;
            }
            this.this$0.mWindowManagerFuncs.reevaluateStatusBarVisibility();
        }
    }

    final class HideNavInputEventReceiver extends InputEventReceiver {
        final /* synthetic */ PhoneWindowManager this$0;

        public HideNavInputEventReceiver(com.android.internal.policy.impl.PhoneWindowManager r1, android.view.InputChannel r2, android.os.Looper r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindowManager.HideNavInputEventReceiver.<init>(com.android.internal.policy.impl.PhoneWindowManager, android.view.InputChannel, android.os.Looper):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindowManager.HideNavInputEventReceiver.<init>(com.android.internal.policy.impl.PhoneWindowManager, android.view.InputChannel, android.os.Looper):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindowManager.HideNavInputEventReceiver.<init>(com.android.internal.policy.impl.PhoneWindowManager, android.view.InputChannel, android.os.Looper):void");
        }

        public void onInputEvent(android.view.InputEvent r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.PhoneWindowManager.HideNavInputEventReceiver.onInputEvent(android.view.InputEvent):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.PhoneWindowManager.HideNavInputEventReceiver.onInputEvent(android.view.InputEvent):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindowManager.HideNavInputEventReceiver.onInputEvent(android.view.InputEvent):void");
        }
    }

    class MyOrientationListener extends WindowOrientationListener {
        final /* synthetic */ PhoneWindowManager this$0;

        MyOrientationListener(PhoneWindowManager phoneWindowManager, Context context, Handler handler) {
            this.this$0 = phoneWindowManager;
            super(context, handler);
        }

        public void onProposedRotationChanged(int rotation) {
            this.this$0.updateRotation(PhoneWindowManager.SHOW_PROCESSES_ON_ALT_MENU);
        }
    }

    class MyWakeGestureListener extends WakeGestureListener {
        final /* synthetic */ PhoneWindowManager this$0;

        MyWakeGestureListener(PhoneWindowManager phoneWindowManager, Context context, Handler handler) {
            this.this$0 = phoneWindowManager;
            super(context, handler);
        }

        public void onWakeUp() {
            synchronized (this.this$0.mLock) {
                if (this.this$0.shouldEnableWakeGestureLp()) {
                    this.this$0.performHapticFeedbackLw(null, PhoneWindowManager.SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME, PhoneWindowManager.SHOW_PROCESSES_ON_ALT_MENU);
                    this.this$0.wakeUp(SystemClock.uptimeMillis(), this.this$0.mAllowTheaterModeWakeFromWakeGesture);
                }
            }
        }
    }

    private class PolicyHandler extends Handler {
        final /* synthetic */ PhoneWindowManager this$0;

        private PolicyHandler(PhoneWindowManager phoneWindowManager) {
            this.this$0 = phoneWindowManager;
        }

        /* synthetic */ PolicyHandler(PhoneWindowManager x0, AnonymousClass1 x1) {
            this(x0);
        }

        public void handleMessage(Message msg) {
            boolean z = PhoneWindowManager.SHOW_STARTING_ANIMATIONS;
            switch (msg.what) {
                case PhoneWindowManager.SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME /*1*/:
                    this.this$0.enablePointerLocation();
                case PhoneWindowManager.SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP /*2*/:
                    this.this$0.disablePointerLocation();
                case PhoneWindowManager.SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME /*3*/:
                    this.this$0.dispatchMediaKeyWithWakeLock((KeyEvent) msg.obj);
                case PhoneWindowManager.SHORT_PRESS_POWER_GO_HOME /*4*/:
                    this.this$0.dispatchMediaKeyRepeatWithWakeLock((KeyEvent) msg.obj);
                case PhoneWindowManager.MSG_KEYGUARD_DRAWN_COMPLETE /*5*/:
                    this.this$0.finishKeyguardDrawn();
                case PhoneWindowManager.MSG_KEYGUARD_DRAWN_TIMEOUT /*6*/:
                    Slog.w(PhoneWindowManager.TAG, "Keyguard drawn timeout. Setting mKeyguardDrawComplete");
                    this.this$0.finishKeyguardDrawn();
                case PhoneWindowManager.MSG_WINDOW_MANAGER_DRAWN_COMPLETE /*7*/:
                    this.this$0.finishWindowsDrawn();
                case PhoneWindowManager.MSG_DISPATCH_SHOW_RECENTS /*9*/:
                    this.this$0.showRecentApps(PhoneWindowManager.SHOW_PROCESSES_ON_ALT_MENU);
                case PhoneWindowManager.MSG_DISPATCH_SHOW_GLOBAL_ACTIONS /*10*/:
                    this.this$0.showGlobalActionsInternal();
                case PhoneWindowManager.MSG_HIDE_BOOT_MESSAGE /*11*/:
                    this.this$0.handleHideBootMessage();
                case PhoneWindowManager.MSG_LAUNCH_VOICE_ASSIST_WITH_WAKE_LOCK /*12*/:
                    this.this$0.launchVoiceAssistWithWakeLock(msg.arg1 != 0 ? PhoneWindowManager.SHOW_STARTING_ANIMATIONS : PhoneWindowManager.SHOW_PROCESSES_ON_ALT_MENU);
                case PhoneWindowManager.MSG_POWER_DELAYED_PRESS /*13*/:
                    PhoneWindowManager phoneWindowManager = this.this$0;
                    long longValue = ((Long) msg.obj).longValue();
                    if (msg.arg1 == 0) {
                        z = PhoneWindowManager.SHOW_PROCESSES_ON_ALT_MENU;
                    }
                    phoneWindowManager.powerPress(longValue, z, msg.arg2);
                    this.this$0.finishPowerKeyPress();
                case PhoneWindowManager.MSG_POWER_LONG_PRESS /*14*/:
                    this.this$0.powerLongPress();
                default:
            }
        }
    }

    class ScreenLockTimeout implements Runnable {
        Bundle options;
        final /* synthetic */ PhoneWindowManager this$0;

        ScreenLockTimeout(PhoneWindowManager phoneWindowManager) {
            this.this$0 = phoneWindowManager;
        }

        public void run() {
            synchronized (this) {
                if (this.this$0.mKeyguardDelegate != null) {
                    this.this$0.mKeyguardDelegate.doKeyguardTimeout(this.options);
                }
                this.this$0.mLockScreenTimerActive = PhoneWindowManager.SHOW_PROCESSES_ON_ALT_MENU;
                this.options = null;
            }
        }

        public void setLockOptions(Bundle options) {
            this.options = options;
        }
    }

    class SettingsObserver extends ContentObserver {
        final /* synthetic */ PhoneWindowManager this$0;

        SettingsObserver(PhoneWindowManager phoneWindowManager, Handler handler) {
            this.this$0 = phoneWindowManager;
            super(handler);
        }

        void observe() {
            ContentResolver resolver = this.this$0.mContext.getContentResolver();
            resolver.registerContentObserver(System.getUriFor("end_button_behavior"), PhoneWindowManager.SHOW_PROCESSES_ON_ALT_MENU, this, PhoneWindowManager.APPLICATION_MEDIA_OVERLAY_SUBLAYER);
            resolver.registerContentObserver(Secure.getUriFor("incall_power_button_behavior"), PhoneWindowManager.SHOW_PROCESSES_ON_ALT_MENU, this, PhoneWindowManager.APPLICATION_MEDIA_OVERLAY_SUBLAYER);
            resolver.registerContentObserver(Secure.getUriFor("wake_gesture_enabled"), PhoneWindowManager.SHOW_PROCESSES_ON_ALT_MENU, this, PhoneWindowManager.APPLICATION_MEDIA_OVERLAY_SUBLAYER);
            resolver.registerContentObserver(System.getUriFor("accelerometer_rotation"), PhoneWindowManager.SHOW_PROCESSES_ON_ALT_MENU, this, PhoneWindowManager.APPLICATION_MEDIA_OVERLAY_SUBLAYER);
            resolver.registerContentObserver(System.getUriFor("user_rotation"), PhoneWindowManager.SHOW_PROCESSES_ON_ALT_MENU, this, PhoneWindowManager.APPLICATION_MEDIA_OVERLAY_SUBLAYER);
            resolver.registerContentObserver(System.getUriFor("screen_off_timeout"), PhoneWindowManager.SHOW_PROCESSES_ON_ALT_MENU, this, PhoneWindowManager.APPLICATION_MEDIA_OVERLAY_SUBLAYER);
            resolver.registerContentObserver(System.getUriFor("pointer_location"), PhoneWindowManager.SHOW_PROCESSES_ON_ALT_MENU, this, PhoneWindowManager.APPLICATION_MEDIA_OVERLAY_SUBLAYER);
            resolver.registerContentObserver(Secure.getUriFor("default_input_method"), PhoneWindowManager.SHOW_PROCESSES_ON_ALT_MENU, this, PhoneWindowManager.APPLICATION_MEDIA_OVERLAY_SUBLAYER);
            resolver.registerContentObserver(Secure.getUriFor("immersive_mode_confirmations"), PhoneWindowManager.SHOW_PROCESSES_ON_ALT_MENU, this, PhoneWindowManager.APPLICATION_MEDIA_OVERLAY_SUBLAYER);
            resolver.registerContentObserver(Global.getUriFor("policy_control"), PhoneWindowManager.SHOW_PROCESSES_ON_ALT_MENU, this, PhoneWindowManager.APPLICATION_MEDIA_OVERLAY_SUBLAYER);
            this.this$0.updateSettings();
        }

        public void onChange(boolean selfChange) {
            this.this$0.updateSettings();
            this.this$0.updateRotation(PhoneWindowManager.SHOW_PROCESSES_ON_ALT_MENU);
        }
    }

    public PhoneWindowManager() {
        this.mLock = new Object();
        this.mServiceAquireLock = new Object();
        this.mEnableShiftMenuBugReports = SHOW_PROCESSES_ON_ALT_MENU;
        this.mStatusBar = null;
        this.mNavigationBar = null;
        this.mHasNavigationBar = SHOW_PROCESSES_ON_ALT_MENU;
        this.mCanHideNavigationBar = SHOW_PROCESSES_ON_ALT_MENU;
        this.mNavigationBarCanMove = SHOW_PROCESSES_ON_ALT_MENU;
        this.mNavigationBarOnBottom = SHOW_STARTING_ANIMATIONS;
        this.mNavigationBarHeightForRotation = new int[SHORT_PRESS_POWER_GO_HOME];
        this.mNavigationBarWidthForRotation = new int[SHORT_PRESS_POWER_GO_HOME];
        this.mWindowManagerDrawCallback = new AnonymousClass1(this);
        this.mKeyguardDelegateCallback = new AnonymousClass2(this);
        this.mLastInputMethodWindow = null;
        this.mLastInputMethodTargetWindow = null;
        this.mLidState = APPLICATION_MEDIA_OVERLAY_SUBLAYER;
        this.mCameraLensCoverState = APPLICATION_MEDIA_OVERLAY_SUBLAYER;
        this.mDockMode = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        this.mForceDefaultOrientation = SHOW_PROCESSES_ON_ALT_MENU;
        this.mUserRotationMode = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        this.mUserRotation = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        this.mAllowAllRotations = APPLICATION_MEDIA_OVERLAY_SUBLAYER;
        this.mOrientationSensorEnabled = SHOW_PROCESSES_ON_ALT_MENU;
        this.mCurrentAppOrientation = APPLICATION_MEDIA_OVERLAY_SUBLAYER;
        this.mHasSoftInput = SHOW_PROCESSES_ON_ALT_MENU;
        this.mTranslucentDecorEnabled = SHOW_STARTING_ANIMATIONS;
        this.mPointerLocationMode = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        this.mResettingSystemUiFlags = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        this.mForceClearedSystemUiFlags = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        this.mLastFocusNeedsMenu = SHOW_PROCESSES_ON_ALT_MENU;
        this.mHideNavFakeWindow = null;
        this.mAppsToBeHidden = new HashSet();
        this.mAppsThatDismissKeyguard = new HashSet();
        this.mDismissKeyguard = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        this.mLandscapeRotation = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        this.mSeascapeRotation = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        this.mPortraitRotation = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        this.mUpsideDownRotation = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        this.mOverscanLeft = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        this.mOverscanTop = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        this.mOverscanRight = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        this.mOverscanBottom = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        this.mFallbackActions = new SparseArray();
        this.mLogDecelerateInterpolator = new LogDecelerateInterpolator(100, SHORT_PRESS_SLEEP_GO_TO_SLEEP);
        this.mHDMIObserver = new AnonymousClass3(this);
        this.mStatusBarController = new BarController("StatusBar", 67108864, 268435456, 1073741824, SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME, 67108864);
        this.mNavigationBarController = new BarController("NavigationBar", 134217728, 536870912, Integer.MIN_VALUE, SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP, 134217728);
        this.mEndCallLongPress = new AnonymousClass4(this);
        this.mScreenshotRunnable = new AnonymousClass5(this);
        this.mHomeDoubleTapTimeoutRunnable = new AnonymousClass6(this);
        this.mClearHideNavigationFlag = new AnonymousClass9(this);
        this.mHideNavInputEventReceiverFactory = new Factory() {
            public InputEventReceiver createInputEventReceiver(InputChannel inputChannel, Looper looper) {
                return new HideNavInputEventReceiver(PhoneWindowManager.this, inputChannel, looper);
            }
        };
        this.mScreenshotLock = new Object();
        this.mScreenshotConnection = null;
        this.mScreenshotTimeout = new AnonymousClass13(this);
        this.mDockReceiver = new AnonymousClass15(this);
        this.mDreamReceiver = new AnonymousClass16(this);
        this.mMultiuserReceiver = new AnonymousClass17(this);
        this.mRequestTransientNav = new AnonymousClass18(this);
        this.mBootMsgDialog = null;
        this.mScreenLockTimeout = new ScreenLockTimeout(this);
    }

    static {
        VIBRATION_ATTRIBUTES = new Builder().setContentType(SHORT_PRESS_POWER_GO_HOME).setUsage(MSG_POWER_DELAYED_PRESS).build();
        sApplicationLaunchKeyCategories = new SparseArray();
        sApplicationLaunchKeyCategories.append(64, "android.intent.category.APP_BROWSER");
        sApplicationLaunchKeyCategories.append(65, "android.intent.category.APP_EMAIL");
        sApplicationLaunchKeyCategories.append(207, "android.intent.category.APP_CONTACTS");
        sApplicationLaunchKeyCategories.append(208, "android.intent.category.APP_CALENDAR");
        sApplicationLaunchKeyCategories.append(209, "android.intent.category.APP_MUSIC");
        sApplicationLaunchKeyCategories.append(210, "android.intent.category.APP_CALCULATOR");
        mTmpParentFrame = new Rect();
        mTmpDisplayFrame = new Rect();
        mTmpOverscanFrame = new Rect();
        mTmpContentFrame = new Rect();
        mTmpVisibleFrame = new Rect();
        mTmpDecorFrame = new Rect();
        mTmpStableFrame = new Rect();
        mTmpNavigationFrame = new Rect();
        WINDOW_TYPES_WHERE_HOME_DOESNT_WORK = new int[]{2003, 2010};
    }

    IStatusBarService getStatusBarService() {
        IStatusBarService iStatusBarService;
        synchronized (this.mServiceAquireLock) {
            if (this.mStatusBarService == null) {
                this.mStatusBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
            }
            iStatusBarService = this.mStatusBarService;
        }
        return iStatusBarService;
    }

    boolean needSensorRunningLp() {
        if (this.mSupportAutoRotation && (this.mCurrentAppOrientation == SHORT_PRESS_POWER_GO_HOME || this.mCurrentAppOrientation == MSG_DISPATCH_SHOW_GLOBAL_ACTIONS || this.mCurrentAppOrientation == MSG_WINDOW_MANAGER_DRAWN_COMPLETE || this.mCurrentAppOrientation == MSG_KEYGUARD_DRAWN_TIMEOUT)) {
            return SHOW_STARTING_ANIMATIONS;
        }
        if (this.mCarDockEnablesAccelerometer && this.mDockMode == SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP) {
            return SHOW_STARTING_ANIMATIONS;
        }
        if (this.mDeskDockEnablesAccelerometer && (this.mDockMode == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME || this.mDockMode == SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME || this.mDockMode == SHORT_PRESS_POWER_GO_HOME)) {
            return SHOW_STARTING_ANIMATIONS;
        }
        if (this.mUserRotationMode == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) {
            return SHOW_PROCESSES_ON_ALT_MENU;
        }
        return this.mSupportAutoRotation;
    }

    void updateOrientationListenerLp() {
        if (this.mOrientationListener.canDetectOrientation()) {
            boolean disable = SHOW_STARTING_ANIMATIONS;
            if (this.mScreenOnEarly && this.mAwake && needSensorRunningLp()) {
                disable = SHOW_PROCESSES_ON_ALT_MENU;
                if (!this.mOrientationSensorEnabled) {
                    this.mOrientationListener.enable();
                    this.mOrientationSensorEnabled = SHOW_STARTING_ANIMATIONS;
                }
            }
            if (disable && this.mOrientationSensorEnabled) {
                this.mOrientationListener.disable();
                this.mOrientationSensorEnabled = SHOW_PROCESSES_ON_ALT_MENU;
            }
        }
    }

    private void interceptPowerKeyDown(KeyEvent event, boolean interactive) {
        if (!this.mPowerKeyWakeLock.isHeld()) {
            this.mPowerKeyWakeLock.acquire();
        }
        if (this.mPowerKeyPressCounter != 0) {
            this.mHandler.removeMessages(MSG_POWER_DELAYED_PRESS);
        }
        if (this.mImmersiveModeConfirmation.onPowerKeyDown(interactive, event.getDownTime(), isImmersiveMode(this.mLastSystemUiFlags))) {
            this.mHandler.post(this.mRequestTransientNav);
        }
        if (interactive && !this.mScreenshotChordPowerKeyTriggered && (event.getFlags() & 1024) == 0) {
            this.mScreenshotChordPowerKeyTriggered = SHOW_STARTING_ANIMATIONS;
            this.mScreenshotChordPowerKeyTime = event.getDownTime();
            interceptScreenshotChord();
        }
        TelecomManager telecomManager = getTelecommService();
        boolean hungUp = SHOW_PROCESSES_ON_ALT_MENU;
        if (telecomManager != null) {
            if (telecomManager.isRinging()) {
                telecomManager.silenceRinger();
            } else if ((this.mIncallPowerBehavior & SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP) != 0 && telecomManager.isInCall() && interactive) {
                hungUp = telecomManager.endCall();
            }
        }
        boolean z = (hungUp || this.mScreenshotChordVolumeDownKeyTriggered || this.mScreenshotChordVolumeUpKeyTriggered) ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
        this.mPowerKeyHandled = z;
        if (!this.mPowerKeyHandled) {
            Message msg;
            if (!interactive) {
                wakeUpFromPowerKey(event.getDownTime());
                if (this.mSupportLongPressPowerWhenNonInteractive && hasLongPressOnPowerBehavior()) {
                    msg = this.mHandler.obtainMessage(MSG_POWER_LONG_PRESS);
                    msg.setAsynchronous(SHOW_STARTING_ANIMATIONS);
                    this.mHandler.sendMessageDelayed(msg, ViewConfiguration.get(this.mContext).getDeviceGlobalActionKeyTimeout());
                    this.mBeganFromNonInteractive = SHOW_STARTING_ANIMATIONS;
                } else if (getMaxMultiPressPowerCount() <= SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) {
                    this.mPowerKeyHandled = SHOW_STARTING_ANIMATIONS;
                } else {
                    this.mBeganFromNonInteractive = SHOW_STARTING_ANIMATIONS;
                }
            } else if (hasLongPressOnPowerBehavior()) {
                msg = this.mHandler.obtainMessage(MSG_POWER_LONG_PRESS);
                msg.setAsynchronous(SHOW_STARTING_ANIMATIONS);
                this.mHandler.sendMessageDelayed(msg, ViewConfiguration.get(this.mContext).getDeviceGlobalActionKeyTimeout());
            }
        }
    }

    private void interceptPowerKeyUp(KeyEvent event, boolean interactive, boolean canceled) {
        boolean handled;
        int i = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        if (canceled || this.mPowerKeyHandled) {
            handled = SHOW_STARTING_ANIMATIONS;
        } else {
            handled = SHOW_PROCESSES_ON_ALT_MENU;
        }
        this.mScreenshotChordPowerKeyTriggered = SHOW_PROCESSES_ON_ALT_MENU;
        cancelPendingScreenshotChordAction();
        cancelPendingPowerKeyAction();
        if (!handled) {
            this.mPowerKeyPressCounter += SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
            int maxCount = getMaxMultiPressPowerCount();
            long eventTime = event.getDownTime();
            if (this.mPowerKeyPressCounter < maxCount) {
                Handler handler = this.mHandler;
                if (interactive) {
                    i = SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
                }
                Message msg = handler.obtainMessage(MSG_POWER_DELAYED_PRESS, i, this.mPowerKeyPressCounter, Long.valueOf(eventTime));
                msg.setAsynchronous(SHOW_STARTING_ANIMATIONS);
                this.mHandler.sendMessageDelayed(msg, (long) ViewConfiguration.getDoubleTapTimeout());
                return;
            }
            powerPress(eventTime, interactive, this.mPowerKeyPressCounter);
        }
        finishPowerKeyPress();
    }

    private void finishPowerKeyPress() {
        this.mBeganFromNonInteractive = SHOW_PROCESSES_ON_ALT_MENU;
        this.mPowerKeyPressCounter = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        if (this.mPowerKeyWakeLock.isHeld()) {
            this.mPowerKeyWakeLock.release();
        }
    }

    private void cancelPendingPowerKeyAction() {
        if (!this.mPowerKeyHandled) {
            this.mPowerKeyHandled = SHOW_STARTING_ANIMATIONS;
            this.mHandler.removeMessages(MSG_POWER_LONG_PRESS);
        }
    }

    private void powerPress(long eventTime, boolean interactive, int count) {
        if (this.mScreenOnEarly && !this.mScreenOnFully) {
            Slog.i(TAG, "Suppressed redundant power key press while already in the process of turning the screen on.");
        } else if (count == SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP) {
            powerMultiPressAction(eventTime, interactive, this.mDoublePressOnPowerBehavior);
        } else if (count == SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME) {
            powerMultiPressAction(eventTime, interactive, this.mTriplePressOnPowerBehavior);
        } else if (interactive && !this.mBeganFromNonInteractive) {
            switch (this.mShortPressOnPowerBehavior) {
                case SHORT_PRESS_SLEEP_GO_TO_SLEEP /*0*/:
                case SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME /*1*/:
                    this.mPowerManager.goToSleep(eventTime, SHORT_PRESS_POWER_GO_HOME, SHORT_PRESS_SLEEP_GO_TO_SLEEP);
                case SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP /*2*/:
                    this.mPowerManager.goToSleep(eventTime, SHORT_PRESS_POWER_GO_HOME, SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME);
                case SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME /*3*/:
                    this.mPowerManager.goToSleep(eventTime, SHORT_PRESS_POWER_GO_HOME, SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME);
                    launchHomeFromHotKey();
                case SHORT_PRESS_POWER_GO_HOME /*4*/:
                    launchHomeFromHotKey(SHOW_STARTING_ANIMATIONS, SHOW_PROCESSES_ON_ALT_MENU);
                default:
            }
        }
    }

    private void powerMultiPressAction(long eventTime, boolean interactive, int behavior) {
        switch (behavior) {
            case SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME /*1*/:
                if (isTheaterModeEnabled()) {
                    Slog.i(TAG, "Toggling theater mode off.");
                    Global.putInt(this.mContext.getContentResolver(), "theater_mode_on", SHORT_PRESS_SLEEP_GO_TO_SLEEP);
                    if (!interactive) {
                        wakeUpFromPowerKey(eventTime);
                        return;
                    }
                    return;
                }
                Slog.i(TAG, "Toggling theater mode on.");
                Global.putInt(this.mContext.getContentResolver(), "theater_mode_on", SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME);
                if (this.mGoToSleepOnButtonPressTheaterMode && interactive) {
                    this.mPowerManager.goToSleep(eventTime, SHORT_PRESS_POWER_GO_HOME, SHORT_PRESS_SLEEP_GO_TO_SLEEP);
                }
            case SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP /*2*/:
                Slog.i(TAG, "Starting brightness boost.");
                if (!interactive) {
                    wakeUpFromPowerKey(eventTime);
                }
                this.mPowerManager.boostScreenBrightness(eventTime);
            default:
        }
    }

    private int getMaxMultiPressPowerCount() {
        if (this.mTriplePressOnPowerBehavior != 0) {
            return SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME;
        }
        if (this.mDoublePressOnPowerBehavior != 0) {
            return SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP;
        }
        return SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
    }

    private void powerLongPress() {
        boolean z = SHOW_STARTING_ANIMATIONS;
        int behavior = getResolvedLongPressOnPowerBehavior();
        switch (behavior) {
            case SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME /*1*/:
                this.mPowerKeyHandled = SHOW_STARTING_ANIMATIONS;
                if (!performHapticFeedbackLw(null, SHORT_PRESS_SLEEP_GO_TO_SLEEP, SHOW_PROCESSES_ON_ALT_MENU)) {
                    performAuditoryFeedbackForAccessibilityIfNeed();
                }
                showGlobalActionsInternal();
            case SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP /*2*/:
            case SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME /*3*/:
                this.mPowerKeyHandled = SHOW_STARTING_ANIMATIONS;
                performHapticFeedbackLw(null, SHORT_PRESS_SLEEP_GO_TO_SLEEP, SHOW_PROCESSES_ON_ALT_MENU);
                sendCloseSystemWindows(SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS);
                WindowManagerFuncs windowManagerFuncs = this.mWindowManagerFuncs;
                if (behavior != SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP) {
                    z = SHOW_PROCESSES_ON_ALT_MENU;
                }
                windowManagerFuncs.shutdown(z);
            default:
        }
    }

    private void sleepPress(KeyEvent event) {
        switch (this.mShortPressOnSleepBehavior) {
            case SHORT_PRESS_SLEEP_GO_TO_SLEEP /*0*/:
                this.mPowerManager.goToSleep(event.getEventTime(), MSG_KEYGUARD_DRAWN_TIMEOUT, SHORT_PRESS_SLEEP_GO_TO_SLEEP);
            case SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME /*1*/:
                launchHomeFromHotKey(SHOW_PROCESSES_ON_ALT_MENU, SHOW_STARTING_ANIMATIONS);
                this.mPowerManager.goToSleep(event.getEventTime(), MSG_KEYGUARD_DRAWN_TIMEOUT, SHORT_PRESS_SLEEP_GO_TO_SLEEP);
            default:
        }
    }

    private int getResolvedLongPressOnPowerBehavior() {
        if (FactoryTest.isLongPressOnPowerOffEnabled()) {
            return SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME;
        }
        return this.mLongPressOnPowerBehavior;
    }

    private boolean hasLongPressOnPowerBehavior() {
        return getResolvedLongPressOnPowerBehavior() != 0 ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
    }

    private void interceptScreenshotChord() {
        if (this.mScreenshotChordEnabled && this.mScreenshotChordVolumeDownKeyTriggered && this.mScreenshotChordPowerKeyTriggered && !this.mScreenshotChordVolumeUpKeyTriggered) {
            long now = SystemClock.uptimeMillis();
            if (now <= this.mScreenshotChordVolumeDownKeyTime + SCREENSHOT_CHORD_DEBOUNCE_DELAY_MILLIS && now <= this.mScreenshotChordPowerKeyTime + SCREENSHOT_CHORD_DEBOUNCE_DELAY_MILLIS) {
                this.mScreenshotChordVolumeDownKeyConsumed = SHOW_STARTING_ANIMATIONS;
                cancelPendingPowerKeyAction();
                this.mHandler.postDelayed(this.mScreenshotRunnable, getScreenshotChordLongPressDelay());
            }
        }
    }

    private long getScreenshotChordLongPressDelay() {
        if (this.mKeyguardDelegate.isShowing()) {
            return (long) (KEYGUARD_SCREENSHOT_CHORD_DELAY_MULTIPLIER * ((float) ViewConfiguration.get(this.mContext).getDeviceGlobalActionKeyTimeout()));
        }
        return ViewConfiguration.get(this.mContext).getDeviceGlobalActionKeyTimeout();
    }

    private void cancelPendingScreenshotChordAction() {
        this.mHandler.removeCallbacks(this.mScreenshotRunnable);
    }

    public void showGlobalActions() {
        this.mHandler.removeMessages(MSG_DISPATCH_SHOW_GLOBAL_ACTIONS);
        this.mHandler.sendEmptyMessage(MSG_DISPATCH_SHOW_GLOBAL_ACTIONS);
    }

    void showGlobalActionsInternal() {
        sendCloseSystemWindows(SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS);
        if (this.mGlobalActions == null) {
            this.mGlobalActions = new GlobalActions(this.mContext, this.mWindowManagerFuncs);
        }
        boolean keyguardShowing = isKeyguardShowingAndNotOccluded();
        this.mGlobalActions.showDialog(keyguardShowing, isDeviceProvisioned());
        if (keyguardShowing) {
            this.mPowerManager.userActivity(SystemClock.uptimeMillis(), SHOW_PROCESSES_ON_ALT_MENU);
        }
    }

    boolean isDeviceProvisioned() {
        return Global.getInt(this.mContext.getContentResolver(), "device_provisioned", SHORT_PRESS_SLEEP_GO_TO_SLEEP) != 0 ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
    }

    boolean isUserSetupComplete() {
        return Secure.getIntForUser(this.mContext.getContentResolver(), "user_setup_complete", SHORT_PRESS_SLEEP_GO_TO_SLEEP, APPLICATION_MEDIA_SUBLAYER) != 0 ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
    }

    private void handleShortPressOnHome() {
        if (this.mDreamManagerInternal == null || !this.mDreamManagerInternal.isDreaming()) {
            launchHomeFromHotKey();
        } else {
            this.mDreamManagerInternal.stopDream(SHOW_PROCESSES_ON_ALT_MENU);
        }
    }

    private void handleLongPressOnHome() {
        if (this.mLongPressOnHomeBehavior != 0) {
            this.mHomeConsumed = SHOW_STARTING_ANIMATIONS;
            performHapticFeedbackLw(null, SHORT_PRESS_SLEEP_GO_TO_SLEEP, SHOW_PROCESSES_ON_ALT_MENU);
            if (this.mLongPressOnHomeBehavior == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) {
                toggleRecentApps();
            } else if (this.mLongPressOnHomeBehavior == SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP) {
                launchAssistAction();
            }
        }
    }

    private void handleDoubleTapOnHome() {
        if (this.mDoubleTapOnHomeBehavior == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) {
            this.mHomeConsumed = SHOW_STARTING_ANIMATIONS;
            toggleRecentApps();
        }
    }

    private boolean isRoundWindow() {
        return (this.mContext.getResources().getBoolean(17956986) || (Build.HARDWARE.contains("goldfish") && SystemProperties.getBoolean("ro.emulator.circular", SHOW_PROCESSES_ON_ALT_MENU))) ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
    }

    public void init(Context context, IWindowManager windowManager, WindowManagerFuncs windowManagerFuncs) {
        this.mContext = context;
        this.mWindowManager = windowManager;
        this.mWindowManagerFuncs = windowManagerFuncs;
        this.mWindowManagerInternal = (WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class);
        this.mDreamManagerInternal = (DreamManagerInternal) LocalServices.getService(DreamManagerInternal.class);
        boolean burnInProtectionEnabled = context.getResources().getBoolean(17957011);
        boolean burnInProtectionDevMode = SystemProperties.getBoolean("persist.debug.force_burn_in", SHOW_PROCESSES_ON_ALT_MENU);
        if (burnInProtectionEnabled || burnInProtectionDevMode) {
            int minHorizontal;
            int maxHorizontal;
            int minVertical;
            int maxVertical;
            int maxRadius;
            if (burnInProtectionDevMode) {
                minHorizontal = -8;
                maxHorizontal = 8;
                minVertical = -8;
                maxVertical = -4;
                if (isRoundWindow()) {
                    maxRadius = MSG_KEYGUARD_DRAWN_TIMEOUT;
                } else {
                    maxRadius = APPLICATION_MEDIA_OVERLAY_SUBLAYER;
                }
            } else {
                Resources resources = context.getResources();
                minHorizontal = resources.getInteger(17694855);
                maxHorizontal = resources.getInteger(17694856);
                minVertical = resources.getInteger(17694857);
                maxVertical = resources.getInteger(17694858);
                maxRadius = resources.getInteger(17694854);
            }
            this.mBurnInProtectionHelper = new BurnInProtectionHelper(context, minHorizontal, maxHorizontal, minVertical, maxVertical, maxRadius);
        }
        this.mHandler = new PolicyHandler();
        this.mWakeGestureListener = new MyWakeGestureListener(this, this.mContext, this.mHandler);
        this.mOrientationListener = new MyOrientationListener(this, this.mContext, this.mHandler);
        try {
            this.mOrientationListener.setCurrentRotation(windowManager.getRotation());
        } catch (RemoteException e) {
        }
        this.mSettingsObserver = new SettingsObserver(this, this.mHandler);
        this.mSettingsObserver.observe();
        this.mShortcutManager = new ShortcutManager(context, this.mHandler);
        this.mShortcutManager.observe();
        this.mUiMode = context.getResources().getInteger(17694783);
        this.mHomeIntent = new Intent("android.intent.action.MAIN", null);
        this.mHomeIntent.addCategory("android.intent.category.HOME");
        this.mHomeIntent.addFlags(270532608);
        this.mCarDockIntent = new Intent("android.intent.action.MAIN", null);
        this.mCarDockIntent.addCategory("android.intent.category.CAR_DOCK");
        this.mCarDockIntent.addFlags(270532608);
        this.mDeskDockIntent = new Intent("android.intent.action.MAIN", null);
        this.mDeskDockIntent.addCategory("android.intent.category.DESK_DOCK");
        this.mDeskDockIntent.addFlags(270532608);
        this.mPowerManager = (PowerManager) context.getSystemService("power");
        this.mBroadcastWakeLock = this.mPowerManager.newWakeLock(SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME, "PhoneWindowManager.mBroadcastWakeLock");
        this.mPowerKeyWakeLock = this.mPowerManager.newWakeLock(SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME, "PhoneWindowManager.mPowerKeyWakeLock");
        this.mEnableShiftMenuBugReports = "1".equals(SystemProperties.get("ro.debuggable"));
        this.mSupportAutoRotation = this.mContext.getResources().getBoolean(17956912);
        this.mLidOpenRotation = readRotation(17694775);
        this.mCarDockRotation = readRotation(17694780);
        this.mDeskDockRotation = readRotation(17694778);
        this.mUndockedHdmiRotation = readRotation(17694782);
        this.mCarDockEnablesAccelerometer = this.mContext.getResources().getBoolean(17956917);
        this.mDeskDockEnablesAccelerometer = this.mContext.getResources().getBoolean(17956916);
        this.mLidKeyboardAccessibility = this.mContext.getResources().getInteger(17694776);
        this.mLidNavigationAccessibility = this.mContext.getResources().getInteger(17694777);
        this.mLidControlsSleep = this.mContext.getResources().getBoolean(17956915);
        this.mTranslucentDecorEnabled = this.mContext.getResources().getBoolean(17956926);
        this.mAllowTheaterModeWakeFromKey = this.mContext.getResources().getBoolean(17956904);
        boolean z = (this.mAllowTheaterModeWakeFromKey || this.mContext.getResources().getBoolean(17956903)) ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
        this.mAllowTheaterModeWakeFromPowerKey = z;
        this.mAllowTheaterModeWakeFromMotion = this.mContext.getResources().getBoolean(17956905);
        this.mAllowTheaterModeWakeFromMotionWhenNotDreaming = this.mContext.getResources().getBoolean(17956906);
        this.mAllowTheaterModeWakeFromCameraLens = this.mContext.getResources().getBoolean(17956902);
        this.mAllowTheaterModeWakeFromLidSwitch = this.mContext.getResources().getBoolean(17956907);
        this.mAllowTheaterModeWakeFromWakeGesture = this.mContext.getResources().getBoolean(17956901);
        this.mGoToSleepOnButtonPressTheaterMode = this.mContext.getResources().getBoolean(17956910);
        this.mSupportLongPressPowerWhenNonInteractive = this.mContext.getResources().getBoolean(17956911);
        this.mShortPressOnPowerBehavior = this.mContext.getResources().getInteger(17694785);
        this.mLongPressOnPowerBehavior = this.mContext.getResources().getInteger(17694784);
        this.mDoublePressOnPowerBehavior = this.mContext.getResources().getInteger(17694786);
        this.mTriplePressOnPowerBehavior = this.mContext.getResources().getInteger(17694787);
        this.mShortPressOnSleepBehavior = this.mContext.getResources().getInteger(17694788);
        readConfigurationDependentBehaviors();
        this.mAccessibilityManager = (AccessibilityManager) context.getSystemService("accessibility");
        IntentFilter filter = new IntentFilter();
        filter.addAction(UiModeManager.ACTION_ENTER_CAR_MODE);
        filter.addAction(UiModeManager.ACTION_EXIT_CAR_MODE);
        filter.addAction(UiModeManager.ACTION_ENTER_DESK_MODE);
        filter.addAction(UiModeManager.ACTION_EXIT_DESK_MODE);
        filter.addAction("android.intent.action.DOCK_EVENT");
        Intent intent = context.registerReceiver(this.mDockReceiver, filter);
        if (intent != null) {
            this.mDockMode = intent.getIntExtra("android.intent.extra.DOCK_STATE", SHORT_PRESS_SLEEP_GO_TO_SLEEP);
        }
        filter = new IntentFilter();
        filter.addAction("android.intent.action.DREAMING_STARTED");
        filter.addAction("android.intent.action.DREAMING_STOPPED");
        context.registerReceiver(this.mDreamReceiver, filter);
        context.registerReceiver(this.mMultiuserReceiver, new IntentFilter("android.intent.action.USER_SWITCHED"));
        this.mSystemGestures = new SystemGesturesPointerEventListener(context, new AnonymousClass7(this));
        this.mImmersiveModeConfirmation = new ImmersiveModeConfirmation(this.mContext);
        this.mWindowManagerFuncs.registerPointerEventListener(this.mSystemGestures);
        this.mVibrator = (Vibrator) context.getSystemService("vibrator");
        this.mLongPressVibePattern = getLongIntArray(this.mContext.getResources(), 17235996);
        this.mVirtualKeyVibePattern = getLongIntArray(this.mContext.getResources(), 17235997);
        this.mKeyboardTapVibePattern = getLongIntArray(this.mContext.getResources(), 17235998);
        this.mClockTickVibePattern = getLongIntArray(this.mContext.getResources(), 17235999);
        this.mCalendarDateVibePattern = getLongIntArray(this.mContext.getResources(), 17236000);
        this.mSafeModeDisabledVibePattern = getLongIntArray(this.mContext.getResources(), 17236001);
        this.mSafeModeEnabledVibePattern = getLongIntArray(this.mContext.getResources(), 17236002);
        this.mScreenshotChordEnabled = this.mContext.getResources().getBoolean(17956899);
        this.mGlobalKeyManager = new GlobalKeyManager(this.mContext);
        initializeHdmiState();
        if (!this.mPowerManager.isInteractive()) {
            goingToSleep(SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP);
        }
    }

    private void readConfigurationDependentBehaviors() {
        this.mLongPressOnHomeBehavior = this.mContext.getResources().getInteger(17694801);
        if (this.mLongPressOnHomeBehavior < 0 || this.mLongPressOnHomeBehavior > SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP) {
            this.mLongPressOnHomeBehavior = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        }
        this.mDoubleTapOnHomeBehavior = this.mContext.getResources().getInteger(17694802);
        if (this.mDoubleTapOnHomeBehavior < 0 || this.mDoubleTapOnHomeBehavior > SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) {
            this.mDoubleTapOnHomeBehavior = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        }
    }

    public void setInitialDisplaySize(Display display, int width, int height, int density) {
        if (this.mContext != null && display.getDisplayId() == 0) {
            int shortSize;
            int longSize;
            boolean z;
            this.mDisplay = display;
            Resources res = this.mContext.getResources();
            if (width > height) {
                shortSize = height;
                longSize = width;
                this.mLandscapeRotation = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
                this.mSeascapeRotation = SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP;
                if (res.getBoolean(17956914)) {
                    this.mPortraitRotation = SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
                    this.mUpsideDownRotation = SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME;
                } else {
                    this.mPortraitRotation = SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME;
                    this.mUpsideDownRotation = SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
                }
            } else {
                shortSize = width;
                longSize = height;
                this.mPortraitRotation = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
                this.mUpsideDownRotation = SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP;
                if (res.getBoolean(17956914)) {
                    this.mLandscapeRotation = SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME;
                    this.mSeascapeRotation = SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
                } else {
                    this.mLandscapeRotation = SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
                    this.mSeascapeRotation = SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME;
                }
            }
            this.mStatusBarHeight = res.getDimensionPixelSize(17104914);
            int[] iArr = this.mNavigationBarHeightForRotation;
            int i = this.mPortraitRotation;
            int[] iArr2 = this.mNavigationBarHeightForRotation;
            int i2 = this.mUpsideDownRotation;
            int dimensionPixelSize = res.getDimensionPixelSize(17104915);
            iArr2[i2] = dimensionPixelSize;
            iArr[i] = dimensionPixelSize;
            iArr = this.mNavigationBarHeightForRotation;
            i = this.mLandscapeRotation;
            iArr2 = this.mNavigationBarHeightForRotation;
            i2 = this.mSeascapeRotation;
            dimensionPixelSize = res.getDimensionPixelSize(17104916);
            iArr2[i2] = dimensionPixelSize;
            iArr[i] = dimensionPixelSize;
            iArr = this.mNavigationBarWidthForRotation;
            i = this.mPortraitRotation;
            iArr2 = this.mNavigationBarWidthForRotation;
            i2 = this.mUpsideDownRotation;
            int[] iArr3 = this.mNavigationBarWidthForRotation;
            int i3 = this.mLandscapeRotation;
            int[] iArr4 = this.mNavigationBarWidthForRotation;
            int i4 = this.mSeascapeRotation;
            int dimensionPixelSize2 = res.getDimensionPixelSize(17104917);
            iArr4[i4] = dimensionPixelSize2;
            iArr3[i3] = dimensionPixelSize2;
            iArr2[i2] = dimensionPixelSize2;
            iArr[i] = dimensionPixelSize2;
            int shortSizeDp = (shortSize * 160) / density;
            int longSizeDp = (longSize * 160) / density;
            this.mNavigationBarCanMove = shortSizeDp < 600 ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
            this.mHasNavigationBar = res.getBoolean(17956957);
            String navBarOverride = SystemProperties.get("qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                this.mHasNavigationBar = SHOW_PROCESSES_ON_ALT_MENU;
            } else if ("0".equals(navBarOverride)) {
                this.mHasNavigationBar = SHOW_STARTING_ANIMATIONS;
            }
            if ("portrait".equals(SystemProperties.get("persist.demo.hdmirotation"))) {
                this.mDemoHdmiRotation = this.mPortraitRotation;
            } else {
                this.mDemoHdmiRotation = this.mLandscapeRotation;
            }
            this.mDemoHdmiRotationLock = SystemProperties.getBoolean("persist.demo.hdmirotationlock", SHOW_PROCESSES_ON_ALT_MENU);
            if ("portrait".equals(SystemProperties.get("persist.demo.remoterotation"))) {
                this.mDemoRotation = this.mPortraitRotation;
            } else {
                this.mDemoRotation = this.mLandscapeRotation;
            }
            this.mDemoRotationLock = SystemProperties.getBoolean("persist.demo.rotationlock", SHOW_PROCESSES_ON_ALT_MENU);
            if (longSizeDp < 960 || shortSizeDp < 720 || !res.getBoolean(17956983) || "true".equals(SystemProperties.get("config.override_forced_orient"))) {
                z = SHOW_PROCESSES_ON_ALT_MENU;
            } else {
                z = SHOW_STARTING_ANIMATIONS;
            }
            this.mForceDefaultOrientation = z;
        }
    }

    private boolean canHideNavigationBar() {
        return (!this.mHasNavigationBar || this.mAccessibilityManager.isTouchExplorationEnabled()) ? SHOW_PROCESSES_ON_ALT_MENU : SHOW_STARTING_ANIMATIONS;
    }

    public boolean isDefaultOrientationForced() {
        return this.mForceDefaultOrientation;
    }

    public void setDisplayOverscan(Display display, int left, int top, int right, int bottom) {
        if (display.getDisplayId() == 0) {
            this.mOverscanLeft = left;
            this.mOverscanTop = top;
            this.mOverscanRight = right;
            this.mOverscanBottom = bottom;
        }
    }

    public void updateSettings() {
        int i = SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP;
        boolean hasSoftInput = SHOW_PROCESSES_ON_ALT_MENU;
        ContentResolver resolver = this.mContext.getContentResolver();
        boolean updateRotation = SHOW_PROCESSES_ON_ALT_MENU;
        synchronized (this.mLock) {
            boolean wakeGestureEnabledSetting;
            int userRotationMode;
            this.mEndcallBehavior = System.getIntForUser(resolver, "end_button_behavior", SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP, APPLICATION_MEDIA_SUBLAYER);
            this.mIncallPowerBehavior = Secure.getIntForUser(resolver, "incall_power_button_behavior", SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME, APPLICATION_MEDIA_SUBLAYER);
            if (Secure.getIntForUser(resolver, "wake_gesture_enabled", SHORT_PRESS_SLEEP_GO_TO_SLEEP, APPLICATION_MEDIA_SUBLAYER) != 0) {
                wakeGestureEnabledSetting = SHOW_STARTING_ANIMATIONS;
            } else {
                wakeGestureEnabledSetting = SHOW_PROCESSES_ON_ALT_MENU;
            }
            if (this.mWakeGestureEnabledSetting != wakeGestureEnabledSetting) {
                this.mWakeGestureEnabledSetting = wakeGestureEnabledSetting;
                updateWakeGestureListenerLp();
            }
            int userRotation = System.getIntForUser(resolver, "user_rotation", SHORT_PRESS_SLEEP_GO_TO_SLEEP, APPLICATION_MEDIA_SUBLAYER);
            if (this.mUserRotation != userRotation) {
                this.mUserRotation = userRotation;
                updateRotation = SHOW_STARTING_ANIMATIONS;
            }
            if (System.getIntForUser(resolver, "accelerometer_rotation", SHORT_PRESS_SLEEP_GO_TO_SLEEP, APPLICATION_MEDIA_SUBLAYER) != 0) {
                userRotationMode = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
            } else {
                userRotationMode = SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
            }
            if (this.mUserRotationMode != userRotationMode) {
                this.mUserRotationMode = userRotationMode;
                updateRotation = SHOW_STARTING_ANIMATIONS;
                updateOrientationListenerLp();
            }
            if (this.mSystemReady) {
                int pointerLocation = System.getIntForUser(resolver, "pointer_location", SHORT_PRESS_SLEEP_GO_TO_SLEEP, APPLICATION_MEDIA_SUBLAYER);
                if (this.mPointerLocationMode != pointerLocation) {
                    this.mPointerLocationMode = pointerLocation;
                    Handler handler = this.mHandler;
                    if (pointerLocation != 0) {
                        i = SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
                    }
                    handler.sendEmptyMessage(i);
                }
            }
            this.mLockScreenTimeout = System.getIntForUser(resolver, "screen_off_timeout", SHORT_PRESS_SLEEP_GO_TO_SLEEP, APPLICATION_MEDIA_SUBLAYER);
            String imId = Secure.getStringForUser(resolver, "default_input_method", APPLICATION_MEDIA_SUBLAYER);
            if (imId != null && imId.length() > 0) {
                hasSoftInput = SHOW_STARTING_ANIMATIONS;
            }
            if (this.mHasSoftInput != hasSoftInput) {
                this.mHasSoftInput = hasSoftInput;
                updateRotation = SHOW_STARTING_ANIMATIONS;
            }
            if (this.mImmersiveModeConfirmation != null) {
                this.mImmersiveModeConfirmation.loadSetting(this.mCurrentUserId);
            }
            PolicyControl.reloadFromSetting(this.mContext);
        }
        if (updateRotation) {
            updateRotation(SHOW_STARTING_ANIMATIONS);
        }
    }

    private void updateWakeGestureListenerLp() {
        if (shouldEnableWakeGestureLp()) {
            this.mWakeGestureListener.requestWakeUpTrigger();
        } else {
            this.mWakeGestureListener.cancelWakeUpTrigger();
        }
    }

    private boolean shouldEnableWakeGestureLp() {
        return (!this.mWakeGestureEnabledSetting || this.mAwake || ((this.mLidControlsSleep && this.mLidState == 0) || !this.mWakeGestureListener.isSupported())) ? SHOW_PROCESSES_ON_ALT_MENU : SHOW_STARTING_ANIMATIONS;
    }

    private void enablePointerLocation() {
        if (this.mPointerLocationView == null) {
            this.mPointerLocationView = new PointerLocationView(this.mContext);
            this.mPointerLocationView.setPrintCoords(SHOW_PROCESSES_ON_ALT_MENU);
            LayoutParams lp = new LayoutParams(APPLICATION_MEDIA_OVERLAY_SUBLAYER, APPLICATION_MEDIA_OVERLAY_SUBLAYER);
            lp.type = 2015;
            lp.flags = 1304;
            if (ActivityManager.isHighEndGfx()) {
                lp.flags |= 16777216;
                lp.privateFlags |= SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP;
            }
            lp.format = -3;
            lp.setTitle("PointerLocation");
            WindowManager wm = (WindowManager) this.mContext.getSystemService("window");
            lp.inputFeatures |= SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP;
            wm.addView(this.mPointerLocationView, lp);
            this.mWindowManagerFuncs.registerPointerEventListener(this.mPointerLocationView);
        }
    }

    private void disablePointerLocation() {
        if (this.mPointerLocationView != null) {
            this.mWindowManagerFuncs.unregisterPointerEventListener(this.mPointerLocationView);
            ((WindowManager) this.mContext.getSystemService("window")).removeView(this.mPointerLocationView);
            this.mPointerLocationView = null;
        }
    }

    private int readRotation(int resID) {
        try {
            switch (this.mContext.getResources().getInteger(resID)) {
                case SHORT_PRESS_SLEEP_GO_TO_SLEEP /*0*/:
                    return SHORT_PRESS_SLEEP_GO_TO_SLEEP;
                case 90:
                    return SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
                case 180:
                    return SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP;
                case 270:
                    return SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME;
            }
        } catch (NotFoundException e) {
        }
        return APPLICATION_MEDIA_OVERLAY_SUBLAYER;
    }

    public int checkAddPermission(LayoutParams attrs, int[] outAppOp) {
        int type = attrs.type;
        outAppOp[SHORT_PRESS_SLEEP_GO_TO_SLEEP] = APPLICATION_MEDIA_OVERLAY_SUBLAYER;
        if ((type < SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME || type > 99) && ((type < WAITING_FOR_DRAWN_TIMEOUT || type > 1999) && (type < 2000 || type > 2999))) {
            return -10;
        }
        if (type < 2000 || type > 2999) {
            return SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        }
        String permission = null;
        switch (type) {
            case 2002:
            case 2003:
            case 2006:
            case 2007:
            case 2010:
                permission = "android.permission.SYSTEM_ALERT_WINDOW";
                outAppOp[SHORT_PRESS_SLEEP_GO_TO_SLEEP] = 24;
                break;
            case 2005:
                outAppOp[SHORT_PRESS_SLEEP_GO_TO_SLEEP] = 45;
                break;
            case 2011:
            case 2013:
            case 2023:
            case 2030:
            case 2031:
            case 2032:
                break;
            default:
                permission = "android.permission.INTERNAL_SYSTEM_WINDOW";
                break;
        }
        if (permission == null || this.mContext.checkCallingOrSelfPermission(permission) == 0) {
            return SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        }
        return -8;
    }

    public boolean checkShowToOwnerOnly(LayoutParams attrs) {
        switch (attrs.type) {
            case SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME /*3*/:
            case 2000:
            case 2001:
            case 2002:
            case 2007:
            case 2008:
            case 2009:
            case 2014:
            case 2017:
            case 2018:
            case 2019:
            case 2020:
            case 2021:
            case 2022:
            case 2024:
            case 2025:
            case 2026:
            case 2027:
            case 2029:
            case 2030:
                break;
            default:
                if ((attrs.privateFlags & 16) == 0) {
                    return SHOW_STARTING_ANIMATIONS;
                }
                break;
        }
        return this.mContext.checkCallingOrSelfPermission("android.permission.INTERNAL_SYSTEM_WINDOW") == 0 ? SHOW_PROCESSES_ON_ALT_MENU : SHOW_STARTING_ANIMATIONS;
    }

    public void adjustWindowParamsLw(LayoutParams attrs) {
        switch (attrs.type) {
            case 2000:
                if (this.mKeyguardHidden) {
                    attrs.flags &= -1048577;
                    attrs.privateFlags &= -1025;
                    break;
                }
                break;
            case 2006:
            case 2015:
                attrs.flags |= 24;
                attrs.flags &= -262145;
                break;
        }
        if (attrs.type != 2000) {
            attrs.privateFlags &= -1025;
        }
        if (ActivityManager.isHighEndGfx() && (attrs.flags & Integer.MIN_VALUE) != 0) {
            attrs.subtreeSystemUiVisibility |= 1536;
        }
    }

    void readLidState() {
        this.mLidState = this.mWindowManagerFuncs.getLidState();
    }

    private void readCameraLensCoverState() {
        this.mCameraLensCoverState = this.mWindowManagerFuncs.getCameraLensCoverState();
    }

    private boolean isHidden(int accessibilityMode) {
        switch (accessibilityMode) {
            case SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME /*1*/:
                if (this.mLidState != 0) {
                    return SHOW_PROCESSES_ON_ALT_MENU;
                }
                return SHOW_STARTING_ANIMATIONS;
            case SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP /*2*/:
                if (this.mLidState != SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) {
                    return SHOW_PROCESSES_ON_ALT_MENU;
                }
                return SHOW_STARTING_ANIMATIONS;
            default:
                return SHOW_PROCESSES_ON_ALT_MENU;
        }
    }

    public void adjustConfigurationLw(Configuration config, int keyboardPresence, int navigationPresence) {
        this.mHaveBuiltInKeyboard = (keyboardPresence & SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) != 0 ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
        readConfigurationDependentBehaviors();
        readLidState();
        applyLidSwitchState();
        if (config.keyboard == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME || (keyboardPresence == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME && isHidden(this.mLidKeyboardAccessibility))) {
            config.hardKeyboardHidden = SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP;
            if (!this.mHasSoftInput) {
                config.keyboardHidden = SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP;
            }
        }
        if (config.navigation == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME || (navigationPresence == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME && isHidden(this.mLidNavigationAccessibility))) {
            config.navigationHidden = SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP;
        }
    }

    public int windowTypeToLayerLw(int type) {
        if (type >= SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME && type <= 99) {
            return SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP;
        }
        switch (type) {
            case 2000:
                return 15;
            case 2001:
                return SHORT_PRESS_POWER_GO_HOME;
            case 2002:
                return SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME;
            case 2003:
                return MSG_DISPATCH_SHOW_GLOBAL_ACTIONS;
            case 2005:
                return MSG_WINDOW_MANAGER_DRAWN_COMPLETE;
            case 2006:
                return 19;
            case 2007:
                return 8;
            case 2008:
                return MSG_KEYGUARD_DRAWN_TIMEOUT;
            case 2009:
                return 17;
            case 2010:
                return 22;
            case 2011:
                return MSG_HIDE_BOOT_MESSAGE;
            case 2012:
                return MSG_LAUNCH_VOICE_ASSIST_WITH_WAKE_LOCK;
            case 2013:
            case 2030:
                return SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP;
            case 2014:
                return 16;
            case 2015:
                return 27;
            case 2016:
                return 25;
            case 2017:
                return MSG_POWER_LONG_PRESS;
            case 2018:
                return 29;
            case 2019:
                return 20;
            case 2020:
                return 18;
            case 2021:
                return 28;
            case 2022:
                return 30;
            case 2023:
                return MSG_DISPATCH_SHOW_RECENTS;
            case 2024:
                return 21;
            case 2025:
                return SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
            case 2026:
                return 24;
            case 2027:
                return 23;
            case 2029:
                return MSG_POWER_DELAYED_PRESS;
            case 2031:
                return MSG_KEYGUARD_DRAWN_COMPLETE;
            case 2032:
                return 26;
            default:
                Log.e(TAG, "Unknown window type: " + type);
                return SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP;
        }
    }

    public int subWindowTypeToLayerLw(int type) {
        switch (type) {
            case WAITING_FOR_DRAWN_TIMEOUT /*1000*/:
            case 1003:
                return SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
            case 1001:
                return APPLICATION_MEDIA_SUBLAYER;
            case 1002:
                return SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP;
            case 1004:
                return APPLICATION_MEDIA_OVERLAY_SUBLAYER;
            default:
                Log.e(TAG, "Unknown sub-window type: " + type);
                return SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        }
    }

    public int getMaxWallpaperLayer() {
        return windowTypeToLayerLw(2000);
    }

    public int getAboveUniverseLayer() {
        return windowTypeToLayerLw(2010);
    }

    public int getNonDecorDisplayWidth(int fullWidth, int fullHeight, int rotation) {
        if (this.mHasNavigationBar && this.mNavigationBarCanMove && fullWidth > fullHeight) {
            return fullWidth - this.mNavigationBarWidthForRotation[rotation];
        }
        return fullWidth;
    }

    public int getNonDecorDisplayHeight(int fullWidth, int fullHeight, int rotation) {
        if (!this.mHasNavigationBar) {
            return fullHeight;
        }
        if (!this.mNavigationBarCanMove || fullWidth < fullHeight) {
            return fullHeight - this.mNavigationBarHeightForRotation[rotation];
        }
        return fullHeight;
    }

    public int getConfigDisplayWidth(int fullWidth, int fullHeight, int rotation) {
        return getNonDecorDisplayWidth(fullWidth, fullHeight, rotation);
    }

    public int getConfigDisplayHeight(int fullWidth, int fullHeight, int rotation) {
        return getNonDecorDisplayHeight(fullWidth, fullHeight, rotation) - this.mStatusBarHeight;
    }

    public boolean isForceHiding(LayoutParams attrs) {
        return ((attrs.privateFlags & 1024) != 0 || ((isKeyguardHostWindow(attrs) && this.mKeyguardDelegate != null && this.mKeyguardDelegate.isShowing()) || attrs.type == 2029)) ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
    }

    public boolean isKeyguardHostWindow(LayoutParams attrs) {
        return attrs.type == 2000 ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
    }

    public boolean canBeForceHidden(WindowState win, LayoutParams attrs) {
        switch (attrs.type) {
            case 2000:
            case 2013:
            case 2019:
            case 2023:
            case 2025:
            case 2029:
                return SHOW_PROCESSES_ON_ALT_MENU;
            default:
                return SHOW_STARTING_ANIMATIONS;
        }
    }

    public WindowState getWinShowWhenLockedLw() {
        return this.mWinShowWhenLocked;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View addStartingWindow(android.os.IBinder r14, java.lang.String r15, int r16, android.content.res.CompatibilityInfo r17, java.lang.CharSequence r18, int r19, int r20, int r21, int r22) {
        /*
        r13 = this;
        if (r15 != 0) goto L_0x0004;
    L_0x0002:
        r10 = 0;
    L_0x0003:
        return r10;
    L_0x0004:
        r9 = 0;
        r7 = 0;
        r2 = r13.mContext;	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r10 = r2.getThemeResId();	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r0 = r16;
        if (r0 != r10) goto L_0x0012;
    L_0x0010:
        if (r19 == 0) goto L_0x001c;
    L_0x0012:
        r10 = 0;
        r2 = r2.createPackageContext(r15, r10);	 Catch:{ NameNotFoundException -> 0x017f }
        r0 = r16;
        r2.setTheme(r0);	 Catch:{ NameNotFoundException -> 0x017f }
    L_0x001c:
        r8 = com.android.internal.policy.PolicyManager.makeNewWindow(r2);	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r6 = r8.getWindowStyle();	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r10 = 12;
        r11 = 0;
        r10 = r6.getBoolean(r10, r11);	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        if (r10 != 0) goto L_0x0036;
    L_0x002d:
        r10 = 14;
        r11 = 0;
        r10 = r6.getBoolean(r10, r11);	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        if (r10 == 0) goto L_0x004a;
    L_0x0036:
        r10 = 0;
        if (r7 == 0) goto L_0x0003;
    L_0x0039:
        r11 = r7.getParent();
        if (r11 != 0) goto L_0x0003;
    L_0x003f:
        r11 = "WindowManager";
        r12 = "view not successfully added to wm, removing view";
        android.util.Log.w(r11, r12);
        r9.removeViewImmediate(r7);
        goto L_0x0003;
    L_0x004a:
        r5 = r2.getResources();	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r0 = r19;
        r1 = r18;
        r10 = r5.getText(r0, r1);	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r8.setTitle(r10);	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r10 = 3;
        r8.setType(r10);	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r10 = r22 | 16;
        r10 = r10 | 8;
        r11 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r10 = r10 | r11;
        r11 = r22 | 16;
        r11 = r11 | 8;
        r12 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r11 = r11 | r12;
        r8.setFlags(r10, r11);	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r0 = r20;
        r8.setDefaultIcon(r0);	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r0 = r21;
        r8.setDefaultLogo(r0);	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r10 = -1;
        r11 = -1;
        r8.setLayout(r10, r11);	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r4 = r8.getAttributes();	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r4.token = r14;	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r4.packageName = r15;	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r10 = r8.getWindowStyle();	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r11 = 8;
        r12 = 0;
        r10 = r10.getResourceId(r11, r12);	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r4.windowAnimations = r10;	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r10 = r4.privateFlags;	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r10 = r10 | 1;
        r4.privateFlags = r10;	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r10 = r4.privateFlags;	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r10 = r10 | 16;
        r4.privateFlags = r10;	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r10 = r17.supportsScreen();	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        if (r10 != 0) goto L_0x00aa;
    L_0x00a4:
        r10 = r4.privateFlags;	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r10 = r10 | 128;
        r4.privateFlags = r10;	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
    L_0x00aa:
        r10 = new java.lang.StringBuilder;	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r10.<init>();	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r11 = "Starting ";
        r10 = r10.append(r11);	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r10 = r10.append(r15);	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r10 = r10.toString();	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r4.setTitle(r10);	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r10 = "window";
        r10 = r2.getSystemService(r10);	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r0 = r10;
        r0 = (android.view.WindowManager) r0;	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r9 = r0;
        r7 = r8.getDecorView();	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r10 = r8.isFloating();	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        if (r10 == 0) goto L_0x00e9;
    L_0x00d4:
        r10 = 0;
        if (r7 == 0) goto L_0x0003;
    L_0x00d7:
        r11 = r7.getParent();
        if (r11 != 0) goto L_0x0003;
    L_0x00dd:
        r11 = "WindowManager";
        r12 = "view not successfully added to wm, removing view";
        android.util.Log.w(r11, r12);
        r9.removeViewImmediate(r7);
        goto L_0x0003;
    L_0x00e9:
        r9.addView(r7, r4);	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        r10 = r7.getParent();	 Catch:{ BadTokenException -> 0x0109, RuntimeException -> 0x013f }
        if (r10 == 0) goto L_0x0107;
    L_0x00f2:
        r10 = r7;
    L_0x00f3:
        if (r7 == 0) goto L_0x0003;
    L_0x00f5:
        r11 = r7.getParent();
        if (r11 != 0) goto L_0x0003;
    L_0x00fb:
        r11 = "WindowManager";
        r12 = "view not successfully added to wm, removing view";
        android.util.Log.w(r11, r12);
        r9.removeViewImmediate(r7);
        goto L_0x0003;
    L_0x0107:
        r10 = 0;
        goto L_0x00f3;
    L_0x0109:
        r3 = move-exception;
        r10 = "WindowManager";
        r11 = new java.lang.StringBuilder;	 Catch:{ all -> 0x016b }
        r11.<init>();	 Catch:{ all -> 0x016b }
        r11 = r11.append(r14);	 Catch:{ all -> 0x016b }
        r12 = " already running, starting window not displayed. ";
        r11 = r11.append(r12);	 Catch:{ all -> 0x016b }
        r12 = r3.getMessage();	 Catch:{ all -> 0x016b }
        r11 = r11.append(r12);	 Catch:{ all -> 0x016b }
        r11 = r11.toString();	 Catch:{ all -> 0x016b }
        android.util.Log.w(r10, r11);	 Catch:{ all -> 0x016b }
        if (r7 == 0) goto L_0x013c;
    L_0x012c:
        r10 = r7.getParent();
        if (r10 != 0) goto L_0x013c;
    L_0x0132:
        r10 = "WindowManager";
        r11 = "view not successfully added to wm, removing view";
        android.util.Log.w(r10, r11);
        r9.removeViewImmediate(r7);
    L_0x013c:
        r10 = 0;
        goto L_0x0003;
    L_0x013f:
        r3 = move-exception;
        r10 = "WindowManager";
        r11 = new java.lang.StringBuilder;	 Catch:{ all -> 0x016b }
        r11.<init>();	 Catch:{ all -> 0x016b }
        r11 = r11.append(r14);	 Catch:{ all -> 0x016b }
        r12 = " failed creating starting window";
        r11 = r11.append(r12);	 Catch:{ all -> 0x016b }
        r11 = r11.toString();	 Catch:{ all -> 0x016b }
        android.util.Log.w(r10, r11, r3);	 Catch:{ all -> 0x016b }
        if (r7 == 0) goto L_0x013c;
    L_0x015a:
        r10 = r7.getParent();
        if (r10 != 0) goto L_0x013c;
    L_0x0160:
        r10 = "WindowManager";
        r11 = "view not successfully added to wm, removing view";
        android.util.Log.w(r10, r11);
        r9.removeViewImmediate(r7);
        goto L_0x013c;
    L_0x016b:
        r10 = move-exception;
        if (r7 == 0) goto L_0x017e;
    L_0x016e:
        r11 = r7.getParent();
        if (r11 != 0) goto L_0x017e;
    L_0x0174:
        r11 = "WindowManager";
        r12 = "view not successfully added to wm, removing view";
        android.util.Log.w(r11, r12);
        r9.removeViewImmediate(r7);
    L_0x017e:
        throw r10;
    L_0x017f:
        r10 = move-exception;
        goto L_0x001c;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.PhoneWindowManager.addStartingWindow(android.os.IBinder, java.lang.String, int, android.content.res.CompatibilityInfo, java.lang.CharSequence, int, int, int, int):android.view.View");
    }

    public void removeStartingWindow(IBinder appToken, View window) {
        if (window != null) {
            ((WindowManager) this.mContext.getSystemService("window")).removeView(window);
        }
    }

    public int prepareAddWindowLw(WindowState win, LayoutParams attrs) {
        switch (attrs.type) {
            case 2000:
                this.mContext.enforceCallingOrSelfPermission("android.permission.STATUS_BAR_SERVICE", "PhoneWindowManager");
                if (this.mStatusBar == null || !this.mStatusBar.isAlive()) {
                    this.mStatusBar = win;
                    this.mStatusBarController.setWindow(win);
                    break;
                }
                return -7;
                break;
            case 2014:
                this.mContext.enforceCallingOrSelfPermission("android.permission.STATUS_BAR_SERVICE", "PhoneWindowManager");
                break;
            case 2017:
                this.mContext.enforceCallingOrSelfPermission("android.permission.STATUS_BAR_SERVICE", "PhoneWindowManager");
                break;
            case 2019:
                this.mContext.enforceCallingOrSelfPermission("android.permission.STATUS_BAR_SERVICE", "PhoneWindowManager");
                if (this.mNavigationBar == null || !this.mNavigationBar.isAlive()) {
                    this.mNavigationBar = win;
                    this.mNavigationBarController.setWindow(win);
                    break;
                }
                return -7;
                break;
            case 2024:
                this.mContext.enforceCallingOrSelfPermission("android.permission.STATUS_BAR_SERVICE", "PhoneWindowManager");
                break;
            case 2029:
                if (this.mKeyguardScrim == null) {
                    this.mKeyguardScrim = win;
                    break;
                }
                return -7;
        }
        return SHORT_PRESS_SLEEP_GO_TO_SLEEP;
    }

    public void removeWindowLw(WindowState win) {
        if (this.mStatusBar == win) {
            this.mStatusBar = null;
            this.mStatusBarController.setWindow(null);
            this.mKeyguardDelegate.showScrim();
        } else if (this.mKeyguardScrim == win) {
            Log.v(TAG, "Removing keyguard scrim");
            this.mKeyguardScrim = null;
        }
        if (this.mNavigationBar == win) {
            this.mNavigationBar = null;
            this.mNavigationBarController.setWindow(null);
        }
    }

    public int selectAnimationLw(WindowState win, int transit) {
        if (win == this.mStatusBar) {
            boolean isKeyguard = (win.getAttrs().privateFlags & 1024) != 0 ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
            if (transit == SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP || transit == SHORT_PRESS_POWER_GO_HOME) {
                if (isKeyguard) {
                    return APPLICATION_MEDIA_OVERLAY_SUBLAYER;
                }
                return 17432605;
            } else if (transit == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME || transit == SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME) {
                if (isKeyguard) {
                    return APPLICATION_MEDIA_OVERLAY_SUBLAYER;
                }
                return 17432604;
            }
        } else if (win == this.mNavigationBar) {
            if (this.mNavigationBarOnBottom) {
                if (transit == SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP || transit == SHORT_PRESS_POWER_GO_HOME) {
                    return 17432599;
                }
                if (transit == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME || transit == SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME) {
                    return 17432598;
                }
            } else if (transit == SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP || transit == SHORT_PRESS_POWER_GO_HOME) {
                return 17432603;
            } else {
                if (transit == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME || transit == SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME) {
                    return 17432602;
                }
            }
        }
        if (transit == MSG_KEYGUARD_DRAWN_COMPLETE) {
            if (win.hasAppShownWindows()) {
                return 17432593;
            }
        } else if (win.getAttrs().type == 2023 && this.mDreamingLockscreen && transit == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) {
            return APPLICATION_MEDIA_OVERLAY_SUBLAYER;
        }
        return SHORT_PRESS_SLEEP_GO_TO_SLEEP;
    }

    public void selectRotationAnimationLw(int[] anim) {
        if (this.mTopFullscreenOpaqueWindowState == null || !this.mTopIsFullscreen) {
            anim[SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME] = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
            anim[SHORT_PRESS_SLEEP_GO_TO_SLEEP] = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
            return;
        }
        switch (this.mTopFullscreenOpaqueWindowState.getAttrs().rotationAnimation) {
            case SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME /*1*/:
                anim[SHORT_PRESS_SLEEP_GO_TO_SLEEP] = 17432645;
                anim[SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME] = 17432643;
            case SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP /*2*/:
                anim[SHORT_PRESS_SLEEP_GO_TO_SLEEP] = 17432644;
                anim[SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME] = 17432643;
            default:
                anim[SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME] = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
                anim[SHORT_PRESS_SLEEP_GO_TO_SLEEP] = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        }
    }

    public boolean validateRotationAnimationLw(int exitAnimId, int enterAnimId, boolean forceDefault) {
        switch (exitAnimId) {
            case 17432644:
            case 17432645:
                if (forceDefault) {
                    return SHOW_PROCESSES_ON_ALT_MENU;
                }
                int[] anim = new int[SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP];
                selectRotationAnimationLw(anim);
                return (exitAnimId == anim[SHORT_PRESS_SLEEP_GO_TO_SLEEP] && enterAnimId == anim[SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME]) ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
            default:
                return SHOW_STARTING_ANIMATIONS;
        }
    }

    public Animation createForceHideEnterAnimation(boolean onWallpaper, boolean goingToNotificationShade) {
        if (goingToNotificationShade) {
            return AnimationUtils.loadAnimation(this.mContext, 17432620);
        }
        AnimationSet set = (AnimationSet) AnimationUtils.loadAnimation(this.mContext, onWallpaper ? 17432621 : 17432619);
        List<Animation> animations = set.getAnimations();
        for (int i = animations.size() + APPLICATION_MEDIA_OVERLAY_SUBLAYER; i >= 0; i += APPLICATION_MEDIA_OVERLAY_SUBLAYER) {
            ((Animation) animations.get(i)).setInterpolator(this.mLogDecelerateInterpolator);
        }
        return set;
    }

    public Animation createForceHideWallpaperExitAnimation(boolean goingToNotificationShade) {
        if (goingToNotificationShade) {
            return null;
        }
        return AnimationUtils.loadAnimation(this.mContext, 17432624);
    }

    private static void awakenDreams() {
        IDreamManager dreamManager = getDreamManager();
        if (dreamManager != null) {
            try {
                dreamManager.awaken();
            } catch (RemoteException e) {
            }
        }
    }

    static IDreamManager getDreamManager() {
        return IDreamManager.Stub.asInterface(ServiceManager.checkService("dreams"));
    }

    TelecomManager getTelecommService() {
        return (TelecomManager) this.mContext.getSystemService("telecom");
    }

    static IAudioService getAudioService() {
        IAudioService audioService = IAudioService.Stub.asInterface(ServiceManager.checkService("audio"));
        if (audioService == null) {
            Log.w(TAG, "Unable to find IAudioService interface.");
        }
        return audioService;
    }

    boolean keyguardOn() {
        return (isKeyguardShowingAndNotOccluded() || inKeyguardRestrictedKeyInputMode()) ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
    }

    public long interceptKeyBeforeDispatching(WindowState win, KeyEvent event, int policyFlags) {
        boolean keyguardOn = keyguardOn();
        int keyCode = event.getKeyCode();
        int repeatCount = event.getRepeatCount();
        int metaState = event.getMetaState();
        int flags = event.getFlags();
        boolean down = event.getAction() == 0 ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
        boolean canceled = event.isCanceled();
        if (this.mScreenshotChordEnabled && (flags & 1024) == 0) {
            if (this.mScreenshotChordVolumeDownKeyTriggered && !this.mScreenshotChordPowerKeyTriggered) {
                long now = SystemClock.uptimeMillis();
                long timeoutTime = this.mScreenshotChordVolumeDownKeyTime + SCREENSHOT_CHORD_DEBOUNCE_DELAY_MILLIS;
                if (now < timeoutTime) {
                    return timeoutTime - now;
                }
            }
            if (keyCode == 25 && this.mScreenshotChordVolumeDownKeyConsumed) {
                if (!down) {
                    this.mScreenshotChordVolumeDownKeyConsumed = SHOW_PROCESSES_ON_ALT_MENU;
                }
                return -1;
            }
        }
        if (this.mPendingMetaAction && !KeyEvent.isMetaKey(keyCode)) {
            this.mPendingMetaAction = SHOW_PROCESSES_ON_ALT_MENU;
        }
        if (keyCode != SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME) {
            KeyCharacterMap kcm;
            Intent shortcutIntent;
            if (keyCode == 82) {
                if (down && repeatCount == 0 && this.mEnableShiftMenuBugReports && (metaState & SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) {
                    this.mContext.sendOrderedBroadcastAsUser(new Intent("android.intent.action.BUG_REPORT"), UserHandle.CURRENT, null, null, null, SHORT_PRESS_SLEEP_GO_TO_SLEEP, null, null);
                    return -1;
                }
            } else if (keyCode == 84) {
                if (!down) {
                    this.mSearchKeyShortcutPending = SHOW_PROCESSES_ON_ALT_MENU;
                    if (this.mConsumeSearchKeyUp) {
                        this.mConsumeSearchKeyUp = SHOW_PROCESSES_ON_ALT_MENU;
                        return -1;
                    }
                } else if (repeatCount == 0) {
                    this.mSearchKeyShortcutPending = SHOW_STARTING_ANIMATIONS;
                    this.mConsumeSearchKeyUp = SHOW_PROCESSES_ON_ALT_MENU;
                }
                return 0;
            } else if (keyCode == 187) {
                if (!keyguardOn) {
                    if (down && repeatCount == 0) {
                        preloadRecentApps();
                    } else if (!down) {
                        toggleRecentApps();
                    }
                }
                return -1;
            } else if (keyCode == 219) {
                if (down) {
                    if (repeatCount == 0) {
                        this.mAssistKeyLongPressed = SHOW_PROCESSES_ON_ALT_MENU;
                    } else if (repeatCount == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) {
                        this.mAssistKeyLongPressed = SHOW_STARTING_ANIMATIONS;
                        if (!keyguardOn) {
                            launchAssistLongPressAction();
                        }
                    }
                } else if (this.mAssistKeyLongPressed) {
                    this.mAssistKeyLongPressed = SHOW_PROCESSES_ON_ALT_MENU;
                } else if (!keyguardOn) {
                    launchAssistAction();
                }
                return -1;
            } else if (keyCode == 231) {
                if (!down) {
                    Intent intent;
                    if (keyguardOn) {
                        intent = new Intent("android.speech.action.VOICE_SEARCH_HANDS_FREE");
                        intent.putExtra("android.speech.extras.EXTRA_SECURE", SHOW_STARTING_ANIMATIONS);
                    } else {
                        intent = new Intent("android.speech.action.WEB_SEARCH");
                    }
                    startActivityAsUser(voiceIntent, UserHandle.CURRENT_OR_SELF);
                }
            } else if (keyCode == 120) {
                if (down && repeatCount == 0) {
                    this.mHandler.post(this.mScreenshotRunnable);
                }
                return -1;
            } else if (keyCode == 221 || keyCode == 220) {
                if (down) {
                    int direction = keyCode == 221 ? SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME : APPLICATION_MEDIA_OVERLAY_SUBLAYER;
                    if (System.getIntForUser(this.mContext.getContentResolver(), "screen_brightness_mode", SHORT_PRESS_SLEEP_GO_TO_SLEEP, -3) != 0) {
                        System.putIntForUser(this.mContext.getContentResolver(), "screen_brightness_mode", SHORT_PRESS_SLEEP_GO_TO_SLEEP, -3);
                    }
                    int min = this.mPowerManager.getMinimumScreenBrightnessSetting();
                    int max = this.mPowerManager.getMaximumScreenBrightnessSetting();
                    System.putIntForUser(this.mContext.getContentResolver(), "screen_brightness", Math.max(min, Math.min(max, System.getIntForUser(this.mContext.getContentResolver(), "screen_brightness", this.mPowerManager.getDefaultScreenBrightnessSetting(), -3) + (((((max - min) + MSG_DISPATCH_SHOW_GLOBAL_ACTIONS) + APPLICATION_MEDIA_OVERLAY_SUBLAYER) / MSG_DISPATCH_SHOW_GLOBAL_ACTIONS) * direction))), -3);
                    startActivityAsUser(new Intent("android.intent.action.SHOW_BRIGHTNESS_DIALOG"), UserHandle.CURRENT_OR_SELF);
                }
                return -1;
            } else if (KeyEvent.isMetaKey(keyCode)) {
                if (down) {
                    this.mPendingMetaAction = SHOW_STARTING_ANIMATIONS;
                } else if (this.mPendingMetaAction) {
                    launchAssistAction("android.intent.extra.ASSIST_INPUT_HINT_KEYBOARD");
                }
                return -1;
            }
            if (this.mSearchKeyShortcutPending) {
                kcm = event.getKeyCharacterMap();
                if (kcm.isPrintingKey(keyCode)) {
                    this.mConsumeSearchKeyUp = SHOW_STARTING_ANIMATIONS;
                    this.mSearchKeyShortcutPending = SHOW_PROCESSES_ON_ALT_MENU;
                    if (down && repeatCount == 0 && !keyguardOn) {
                        shortcutIntent = this.mShortcutManager.getIntent(kcm, keyCode, metaState);
                        if (shortcutIntent != null) {
                            shortcutIntent.addFlags(268435456);
                            try {
                                startActivityAsUser(shortcutIntent, UserHandle.CURRENT);
                            } catch (Throwable ex) {
                                Slog.w(TAG, "Dropping shortcut key combination because the activity to which it is registered was not found: SEARCH+" + KeyEvent.keyCodeToString(keyCode), ex);
                            }
                        } else {
                            Slog.i(TAG, "Dropping unregistered shortcut key combination: SEARCH+" + KeyEvent.keyCodeToString(keyCode));
                        }
                    }
                    return -1;
                }
            }
            if (down && repeatCount == 0 && !keyguardOn && (65536 & metaState) != 0) {
                kcm = event.getKeyCharacterMap();
                if (kcm.isPrintingKey(keyCode)) {
                    shortcutIntent = this.mShortcutManager.getIntent(kcm, keyCode, -458753 & metaState);
                    if (shortcutIntent != null) {
                        shortcutIntent.addFlags(268435456);
                        try {
                            startActivityAsUser(shortcutIntent, UserHandle.CURRENT);
                        } catch (Throwable ex2) {
                            Slog.w(TAG, "Dropping shortcut key combination because the activity to which it is registered was not found: META+" + KeyEvent.keyCodeToString(keyCode), ex2);
                        }
                        return -1;
                    }
                }
            }
            if (down && repeatCount == 0 && !keyguardOn) {
                String category = (String) sApplicationLaunchKeyCategories.get(keyCode);
                if (category != null) {
                    Intent intent2 = Intent.makeMainSelectorActivity("android.intent.action.MAIN", category);
                    intent2.setFlags(268435456);
                    try {
                        startActivityAsUser(intent2, UserHandle.CURRENT);
                    } catch (Throwable ex22) {
                        Slog.w(TAG, "Dropping application launch key because the activity to which it is registered was not found: keyCode=" + keyCode + ", category=" + category, ex22);
                    }
                    return -1;
                }
            }
            if (down && repeatCount == 0 && keyCode == 61) {
                if (this.mRecentAppsHeldModifiers == 0 && !keyguardOn) {
                    int shiftlessModifiers = event.getModifiers() & -194;
                    if (KeyEvent.metaStateHasModifiers(shiftlessModifiers, SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP)) {
                        this.mRecentAppsHeldModifiers = shiftlessModifiers;
                        showRecentApps(SHOW_STARTING_ANIMATIONS);
                        return -1;
                    }
                }
            } else if (!(down || this.mRecentAppsHeldModifiers == 0 || (this.mRecentAppsHeldModifiers & metaState) != 0)) {
                this.mRecentAppsHeldModifiers = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
                hideRecentApps(SHOW_STARTING_ANIMATIONS, SHOW_PROCESSES_ON_ALT_MENU);
            }
            if (down && repeatCount == 0 && (keyCode == 204 || (keyCode == 62 && (metaState & 28672) != 0))) {
                this.mWindowManagerFuncs.switchKeyboardLayout(event.getDeviceId(), (metaState & 193) != 0 ? APPLICATION_MEDIA_OVERLAY_SUBLAYER : SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME);
                return -1;
            } else if (this.mLanguageSwitchKeyPressed && !down && (keyCode == 204 || keyCode == 62)) {
                this.mLanguageSwitchKeyPressed = SHOW_PROCESSES_ON_ALT_MENU;
                return -1;
            } else if (isValidGlobalKey(keyCode) && this.mGlobalKeyManager.handleGlobalKey(this.mContext, keyCode, event)) {
                return -1;
            } else {
                if ((65536 & metaState) != 0) {
                    return -1;
                }
                return 0;
            }
        } else if (down) {
            LayoutParams attrs = win != null ? win.getAttrs() : null;
            if (attrs != null) {
                int type = attrs.type;
                if (type == 2029 || type == 2009 || (attrs.privateFlags & 1024) != 0) {
                    return 0;
                }
                int typeCount = WINDOW_TYPES_WHERE_HOME_DOESNT_WORK.length;
                for (int i = SHORT_PRESS_SLEEP_GO_TO_SLEEP; i < typeCount; i += SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) {
                    if (type == WINDOW_TYPES_WHERE_HOME_DOESNT_WORK[i]) {
                        return -1;
                    }
                }
            }
            if (repeatCount == 0) {
                this.mHomePressed = SHOW_STARTING_ANIMATIONS;
                if (this.mHomeDoubleTapPending) {
                    this.mHomeDoubleTapPending = SHOW_PROCESSES_ON_ALT_MENU;
                    this.mHandler.removeCallbacks(this.mHomeDoubleTapTimeoutRunnable);
                    handleDoubleTapOnHome();
                } else if (this.mLongPressOnHomeBehavior == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME || this.mDoubleTapOnHomeBehavior == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) {
                    preloadRecentApps();
                }
            } else if (!((event.getFlags() & 128) == 0 || keyguardOn)) {
                handleLongPressOnHome();
            }
            return -1;
        } else {
            cancelPreloadRecentApps();
            this.mHomePressed = SHOW_PROCESSES_ON_ALT_MENU;
            if (this.mHomeConsumed) {
                this.mHomeConsumed = SHOW_PROCESSES_ON_ALT_MENU;
                return -1;
            } else if (canceled) {
                Log.i(TAG, "Ignoring HOME; event canceled.");
                return -1;
            } else {
                TelecomManager telecomManager = getTelecommService();
                if (telecomManager != null && telecomManager.isRinging()) {
                    Log.i(TAG, "Ignoring HOME; there's a ringing incoming call.");
                    return -1;
                } else if (this.mDoubleTapOnHomeBehavior != 0) {
                    this.mHandler.removeCallbacks(this.mHomeDoubleTapTimeoutRunnable);
                    this.mHomeDoubleTapPending = SHOW_STARTING_ANIMATIONS;
                    this.mHandler.postDelayed(this.mHomeDoubleTapTimeoutRunnable, (long) ViewConfiguration.getDoubleTapTimeout());
                    return -1;
                } else {
                    handleShortPressOnHome();
                    return -1;
                }
            }
        }
    }

    public KeyEvent dispatchUnhandledKey(WindowState win, KeyEvent event, int policyFlags) {
        KeyEvent fallbackEvent = null;
        if ((event.getFlags() & 1024) == 0) {
            FallbackAction fallbackAction;
            KeyCharacterMap kcm = event.getKeyCharacterMap();
            int keyCode = event.getKeyCode();
            int metaState = event.getMetaState();
            boolean initialDown = (event.getAction() == 0 && event.getRepeatCount() == 0) ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
            if (initialDown) {
                fallbackAction = kcm.getFallbackAction(keyCode, metaState);
            } else {
                fallbackAction = (FallbackAction) this.mFallbackActions.get(keyCode);
            }
            if (fallbackAction != null) {
                fallbackEvent = KeyEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), fallbackAction.keyCode, event.getRepeatCount(), fallbackAction.metaState, event.getDeviceId(), event.getScanCode(), event.getFlags() | 1024, event.getSource(), null);
                if (!interceptFallback(win, fallbackEvent, policyFlags)) {
                    fallbackEvent.recycle();
                    fallbackEvent = null;
                }
                if (initialDown) {
                    this.mFallbackActions.put(keyCode, fallbackAction);
                } else if (event.getAction() == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) {
                    this.mFallbackActions.remove(keyCode);
                    fallbackAction.recycle();
                }
            }
        }
        return fallbackEvent;
    }

    private boolean interceptFallback(WindowState win, KeyEvent fallbackEvent, int policyFlags) {
        if ((interceptKeyBeforeQueueing(fallbackEvent, policyFlags) & SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) == 0 || interceptKeyBeforeDispatching(win, fallbackEvent, policyFlags) != 0) {
            return SHOW_PROCESSES_ON_ALT_MENU;
        }
        return SHOW_STARTING_ANIMATIONS;
    }

    private void launchAssistLongPressAction() {
        performHapticFeedbackLw(null, SHORT_PRESS_SLEEP_GO_TO_SLEEP, SHOW_PROCESSES_ON_ALT_MENU);
        sendCloseSystemWindows(SYSTEM_DIALOG_REASON_ASSIST);
        Intent intent = new Intent("android.intent.action.SEARCH_LONG_PRESS");
        intent.setFlags(268435456);
        try {
            SearchManager searchManager = getSearchManager();
            if (searchManager != null) {
                searchManager.stopSearch();
            }
            startActivityAsUser(intent, UserHandle.CURRENT);
        } catch (ActivityNotFoundException e) {
            Slog.w(TAG, "No activity to handle assist long press action.", e);
        }
    }

    private void launchAssistAction() {
        launchAssistAction(null);
    }

    private void launchAssistAction(String hint) {
        sendCloseSystemWindows(SYSTEM_DIALOG_REASON_ASSIST);
        Intent intent = ((SearchManager) this.mContext.getSystemService("search")).getAssistIntent(this.mContext, SHOW_STARTING_ANIMATIONS, APPLICATION_MEDIA_SUBLAYER);
        if (intent != null) {
            if (hint != null) {
                intent.putExtra(hint, SHOW_STARTING_ANIMATIONS);
            }
            intent.setFlags(872415232);
            try {
                startActivityAsUser(intent, UserHandle.CURRENT);
            } catch (ActivityNotFoundException e) {
                Slog.w(TAG, "No activity to handle assist action.", e);
            }
        }
    }

    private void startActivityAsUser(Intent intent, UserHandle handle) {
        if (isUserSetupComplete()) {
            this.mContext.startActivityAsUser(intent, handle);
        } else {
            Slog.i(TAG, "Not starting activity because user setup is in progress: " + intent);
        }
    }

    private SearchManager getSearchManager() {
        if (this.mSearchManager == null) {
            this.mSearchManager = (SearchManager) this.mContext.getSystemService("search");
        }
        return this.mSearchManager;
    }

    private void preloadRecentApps() {
        this.mPreloadedRecentApps = SHOW_STARTING_ANIMATIONS;
        try {
            IStatusBarService statusbar = getStatusBarService();
            if (statusbar != null) {
                statusbar.preloadRecentApps();
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "RemoteException when preloading recent apps", e);
            this.mStatusBarService = null;
        }
    }

    private void cancelPreloadRecentApps() {
        if (this.mPreloadedRecentApps) {
            this.mPreloadedRecentApps = SHOW_PROCESSES_ON_ALT_MENU;
            try {
                IStatusBarService statusbar = getStatusBarService();
                if (statusbar != null) {
                    statusbar.cancelPreloadRecentApps();
                }
            } catch (RemoteException e) {
                Slog.e(TAG, "RemoteException when cancelling recent apps preload", e);
                this.mStatusBarService = null;
            }
        }
    }

    private void toggleRecentApps() {
        this.mPreloadedRecentApps = SHOW_PROCESSES_ON_ALT_MENU;
        try {
            IStatusBarService statusbar = getStatusBarService();
            if (statusbar != null) {
                statusbar.toggleRecentApps();
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "RemoteException when toggling recent apps", e);
            this.mStatusBarService = null;
        }
    }

    public void showRecentApps() {
        this.mHandler.removeMessages(MSG_DISPATCH_SHOW_RECENTS);
        this.mHandler.sendEmptyMessage(MSG_DISPATCH_SHOW_RECENTS);
    }

    private void showRecentApps(boolean triggeredFromAltTab) {
        this.mPreloadedRecentApps = SHOW_PROCESSES_ON_ALT_MENU;
        try {
            IStatusBarService statusbar = getStatusBarService();
            if (statusbar != null) {
                statusbar.showRecentApps(triggeredFromAltTab);
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "RemoteException when showing recent apps", e);
            this.mStatusBarService = null;
        }
    }

    private void hideRecentApps(boolean triggeredFromAltTab, boolean triggeredFromHome) {
        this.mPreloadedRecentApps = SHOW_PROCESSES_ON_ALT_MENU;
        try {
            IStatusBarService statusbar = getStatusBarService();
            if (statusbar != null) {
                statusbar.hideRecentApps(triggeredFromAltTab, triggeredFromHome);
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "RemoteException when closing recent apps", e);
            this.mStatusBarService = null;
        }
    }

    void launchHomeFromHotKey() {
        launchHomeFromHotKey(SHOW_STARTING_ANIMATIONS, SHOW_STARTING_ANIMATIONS);
    }

    void launchHomeFromHotKey(boolean awakenFromDreams, boolean respectKeyguard) {
        if (respectKeyguard) {
            if (!isKeyguardShowingAndNotOccluded()) {
                if (!this.mHideLockScreen && this.mKeyguardDelegate.isInputRestricted()) {
                    this.mKeyguardDelegate.verifyUnlock(new AnonymousClass8(this, awakenFromDreams));
                    return;
                }
            }
            return;
        }
        try {
            ActivityManagerNative.getDefault().stopAppSwitches();
        } catch (RemoteException e) {
        }
        if (this.mRecentsVisible) {
            if (awakenFromDreams) {
                awakenDreams();
            }
            sendCloseSystemWindows(SYSTEM_DIALOG_REASON_HOME_KEY);
            hideRecentApps(SHOW_PROCESSES_ON_ALT_MENU, SHOW_STARTING_ANIMATIONS);
            return;
        }
        sendCloseSystemWindows(SYSTEM_DIALOG_REASON_HOME_KEY);
        startDockOrHome(SHOW_STARTING_ANIMATIONS, awakenFromDreams);
    }

    public int adjustSystemUiVisibilityLw(int visibility) {
        this.mStatusBarController.adjustSystemUiVisibilityLw(this.mLastSystemUiFlags, visibility);
        this.mNavigationBarController.adjustSystemUiVisibilityLw(this.mLastSystemUiFlags, visibility);
        this.mRecentsVisible = (visibility & 16384) > 0 ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
        this.mResettingSystemUiFlags &= visibility;
        return ((this.mResettingSystemUiFlags ^ APPLICATION_MEDIA_OVERLAY_SUBLAYER) & visibility) & (this.mForceClearedSystemUiFlags ^ APPLICATION_MEDIA_OVERLAY_SUBLAYER);
    }

    public void getInsetHintLw(LayoutParams attrs, Rect outContentInsets, Rect outStableInsets) {
        int fl = PolicyControl.getWindowFlags(null, attrs);
        int systemUiVisibility = PolicyControl.getSystemUiVisibility(null, attrs) | attrs.subtreeSystemUiVisibility;
        if ((fl & 65792) == 65792) {
            int availRight;
            int availBottom;
            if (!canHideNavigationBar() || (systemUiVisibility & 512) == 0) {
                availRight = this.mRestrictedScreenLeft + this.mRestrictedScreenWidth;
                availBottom = this.mRestrictedScreenTop + this.mRestrictedScreenHeight;
            } else {
                availRight = this.mUnrestrictedScreenLeft + this.mUnrestrictedScreenWidth;
                availBottom = this.mUnrestrictedScreenTop + this.mUnrestrictedScreenHeight;
            }
            if ((systemUiVisibility & 256) != 0) {
                if ((fl & 1024) != 0) {
                    outContentInsets.set(this.mStableFullscreenLeft, this.mStableFullscreenTop, availRight - this.mStableFullscreenRight, availBottom - this.mStableFullscreenBottom);
                } else {
                    outContentInsets.set(this.mStableLeft, this.mStableTop, availRight - this.mStableRight, availBottom - this.mStableBottom);
                }
            } else if ((fl & 1024) != 0 || (33554432 & fl) != 0) {
                outContentInsets.setEmpty();
            } else if ((systemUiVisibility & 1028) == 0) {
                outContentInsets.set(this.mCurLeft, this.mCurTop, availRight - this.mCurRight, availBottom - this.mCurBottom);
            } else {
                outContentInsets.set(this.mCurLeft, this.mCurTop, availRight - this.mCurRight, availBottom - this.mCurBottom);
            }
            outStableInsets.set(this.mStableLeft, this.mStableTop, availRight - this.mStableRight, availBottom - this.mStableBottom);
            return;
        }
        outContentInsets.setEmpty();
        outStableInsets.setEmpty();
    }

    public void beginLayoutLw(boolean isDefaultDisplay, int displayWidth, int displayHeight, int displayRotation) {
        int overscanLeft;
        int overscanTop;
        int overscanRight;
        int overscanBottom;
        if (isDefaultDisplay) {
            switch (displayRotation) {
                case SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME /*1*/:
                    overscanLeft = this.mOverscanTop;
                    overscanTop = this.mOverscanRight;
                    overscanRight = this.mOverscanBottom;
                    overscanBottom = this.mOverscanLeft;
                    break;
                case SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP /*2*/:
                    overscanLeft = this.mOverscanRight;
                    overscanTop = this.mOverscanBottom;
                    overscanRight = this.mOverscanLeft;
                    overscanBottom = this.mOverscanTop;
                    break;
                case SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME /*3*/:
                    overscanLeft = this.mOverscanBottom;
                    overscanTop = this.mOverscanLeft;
                    overscanRight = this.mOverscanTop;
                    overscanBottom = this.mOverscanRight;
                    break;
                default:
                    overscanLeft = this.mOverscanLeft;
                    overscanTop = this.mOverscanTop;
                    overscanRight = this.mOverscanRight;
                    overscanBottom = this.mOverscanBottom;
                    break;
            }
        }
        overscanLeft = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        overscanTop = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        overscanRight = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        overscanBottom = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        this.mRestrictedOverscanScreenLeft = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        this.mOverscanScreenLeft = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        this.mRestrictedOverscanScreenTop = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        this.mOverscanScreenTop = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        this.mRestrictedOverscanScreenWidth = displayWidth;
        this.mOverscanScreenWidth = displayWidth;
        this.mRestrictedOverscanScreenHeight = displayHeight;
        this.mOverscanScreenHeight = displayHeight;
        this.mSystemLeft = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        this.mSystemTop = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        this.mSystemRight = displayWidth;
        this.mSystemBottom = displayHeight;
        this.mUnrestrictedScreenLeft = overscanLeft;
        this.mUnrestrictedScreenTop = overscanTop;
        this.mUnrestrictedScreenWidth = (displayWidth - overscanLeft) - overscanRight;
        this.mUnrestrictedScreenHeight = (displayHeight - overscanTop) - overscanBottom;
        this.mRestrictedScreenLeft = this.mUnrestrictedScreenLeft;
        this.mRestrictedScreenTop = this.mUnrestrictedScreenTop;
        SystemGesturesPointerEventListener systemGesturesPointerEventListener = this.mSystemGestures;
        int i = this.mUnrestrictedScreenWidth;
        systemGesturesPointerEventListener.screenWidth = i;
        this.mRestrictedScreenWidth = i;
        systemGesturesPointerEventListener = this.mSystemGestures;
        i = this.mUnrestrictedScreenHeight;
        systemGesturesPointerEventListener.screenHeight = i;
        this.mRestrictedScreenHeight = i;
        int i2 = this.mUnrestrictedScreenLeft;
        this.mCurLeft = i2;
        this.mStableFullscreenLeft = i2;
        this.mStableLeft = i2;
        this.mVoiceContentLeft = i2;
        this.mContentLeft = i2;
        this.mDockLeft = i2;
        i2 = this.mUnrestrictedScreenTop;
        this.mCurTop = i2;
        this.mStableFullscreenTop = i2;
        this.mStableTop = i2;
        this.mVoiceContentTop = i2;
        this.mContentTop = i2;
        this.mDockTop = i2;
        i2 = displayWidth - overscanRight;
        this.mCurRight = i2;
        this.mStableFullscreenRight = i2;
        this.mStableRight = i2;
        this.mVoiceContentRight = i2;
        this.mContentRight = i2;
        this.mDockRight = i2;
        i2 = displayHeight - overscanBottom;
        this.mCurBottom = i2;
        this.mStableFullscreenBottom = i2;
        this.mStableBottom = i2;
        this.mVoiceContentBottom = i2;
        this.mContentBottom = i2;
        this.mDockBottom = i2;
        this.mDockLayer = 268435456;
        this.mStatusBarLayer = APPLICATION_MEDIA_OVERLAY_SUBLAYER;
        Rect pf = mTmpParentFrame;
        Rect df = mTmpDisplayFrame;
        Rect of = mTmpOverscanFrame;
        Rect vf = mTmpVisibleFrame;
        Rect dcf = mTmpDecorFrame;
        i2 = this.mDockLeft;
        vf.left = i2;
        of.left = i2;
        df.left = i2;
        pf.left = i2;
        i2 = this.mDockTop;
        vf.top = i2;
        of.top = i2;
        df.top = i2;
        pf.top = i2;
        i2 = this.mDockRight;
        vf.right = i2;
        of.right = i2;
        df.right = i2;
        pf.right = i2;
        i2 = this.mDockBottom;
        vf.bottom = i2;
        of.bottom = i2;
        df.bottom = i2;
        pf.bottom = i2;
        dcf.setEmpty();
        if (isDefaultDisplay) {
            int sysui = this.mLastSystemUiFlags;
            boolean navVisible = (sysui & SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP) == 0 ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
            boolean navTranslucent = (-2147450880 & sysui) != 0 ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
            boolean immersive = (sysui & 2048) != 0 ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
            boolean immersiveSticky = (sysui & 4096) != 0 ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
            boolean navAllowedHidden = (immersive || immersiveSticky) ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
            navTranslucent &= !immersiveSticky ? SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME : SHORT_PRESS_SLEEP_GO_TO_SLEEP;
            boolean isKeyguardShowing = (!isStatusBarKeyguard() || this.mHideLockScreen) ? SHOW_PROCESSES_ON_ALT_MENU : SHOW_STARTING_ANIMATIONS;
            if (!isKeyguardShowing) {
                navTranslucent &= areTranslucentBarsAllowed();
            }
            if (navVisible || navAllowedHidden) {
                if (this.mHideNavFakeWindow != null) {
                    this.mHideNavFakeWindow.dismiss();
                    this.mHideNavFakeWindow = null;
                }
            } else if (this.mHideNavFakeWindow == null) {
                this.mHideNavFakeWindow = this.mWindowManagerFuncs.addFakeWindow(this.mHandler.getLooper(), this.mHideNavInputEventReceiverFactory, "hidden nav", 2022, SHORT_PRESS_SLEEP_GO_TO_SLEEP, SHORT_PRESS_SLEEP_GO_TO_SLEEP, SHOW_PROCESSES_ON_ALT_MENU, SHOW_PROCESSES_ON_ALT_MENU, SHOW_STARTING_ANIMATIONS);
            }
            navVisible |= !canHideNavigationBar() ? SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME : SHORT_PRESS_SLEEP_GO_TO_SLEEP;
            boolean updateSysUiVisibility = SHOW_PROCESSES_ON_ALT_MENU;
            if (this.mNavigationBar != null) {
                boolean transientNavBarShowing = this.mNavigationBarController.isTransientShowing();
                boolean z = (!this.mNavigationBarCanMove || displayWidth < displayHeight) ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
                this.mNavigationBarOnBottom = z;
                if (this.mNavigationBarOnBottom) {
                    mTmpNavigationFrame.set(SHORT_PRESS_SLEEP_GO_TO_SLEEP, (displayHeight - overscanBottom) - this.mNavigationBarHeightForRotation[displayRotation], displayWidth, displayHeight - overscanBottom);
                    i2 = mTmpNavigationFrame.top;
                    this.mStableFullscreenBottom = i2;
                    this.mStableBottom = i2;
                    if (transientNavBarShowing) {
                        this.mNavigationBarController.setBarShowingLw(SHOW_STARTING_ANIMATIONS);
                    } else if (navVisible) {
                        this.mNavigationBarController.setBarShowingLw(SHOW_STARTING_ANIMATIONS);
                        this.mDockBottom = mTmpNavigationFrame.top;
                        this.mRestrictedScreenHeight = this.mDockBottom - this.mRestrictedScreenTop;
                        this.mRestrictedOverscanScreenHeight = this.mDockBottom - this.mRestrictedOverscanScreenTop;
                    } else {
                        this.mNavigationBarController.setBarShowingLw(SHOW_PROCESSES_ON_ALT_MENU);
                    }
                    if (!(!navVisible || navTranslucent || navAllowedHidden || this.mNavigationBar.isAnimatingLw() || this.mNavigationBarController.wasRecentlyTranslucent())) {
                        this.mSystemBottom = mTmpNavigationFrame.top;
                    }
                } else {
                    mTmpNavigationFrame.set((displayWidth - overscanRight) - this.mNavigationBarWidthForRotation[displayRotation], SHORT_PRESS_SLEEP_GO_TO_SLEEP, displayWidth - overscanRight, displayHeight);
                    i2 = mTmpNavigationFrame.left;
                    this.mStableFullscreenRight = i2;
                    this.mStableRight = i2;
                    if (transientNavBarShowing) {
                        this.mNavigationBarController.setBarShowingLw(SHOW_STARTING_ANIMATIONS);
                    } else if (navVisible) {
                        this.mNavigationBarController.setBarShowingLw(SHOW_STARTING_ANIMATIONS);
                        this.mDockRight = mTmpNavigationFrame.left;
                        this.mRestrictedScreenWidth = this.mDockRight - this.mRestrictedScreenLeft;
                        this.mRestrictedOverscanScreenWidth = this.mDockRight - this.mRestrictedOverscanScreenLeft;
                    } else {
                        this.mNavigationBarController.setBarShowingLw(SHOW_PROCESSES_ON_ALT_MENU);
                    }
                    if (!(!navVisible || navTranslucent || this.mNavigationBar.isAnimatingLw() || this.mNavigationBarController.wasRecentlyTranslucent())) {
                        this.mSystemRight = mTmpNavigationFrame.left;
                    }
                }
                i2 = this.mDockTop;
                this.mCurTop = i2;
                this.mVoiceContentTop = i2;
                this.mContentTop = i2;
                i2 = this.mDockBottom;
                this.mCurBottom = i2;
                this.mVoiceContentBottom = i2;
                this.mContentBottom = i2;
                i2 = this.mDockLeft;
                this.mCurLeft = i2;
                this.mVoiceContentLeft = i2;
                this.mContentLeft = i2;
                i2 = this.mDockRight;
                this.mCurRight = i2;
                this.mVoiceContentRight = i2;
                this.mContentRight = i2;
                this.mStatusBarLayer = this.mNavigationBar.getSurfaceLayer();
                this.mNavigationBar.computeFrameLw(mTmpNavigationFrame, mTmpNavigationFrame, mTmpNavigationFrame, mTmpNavigationFrame, mTmpNavigationFrame, dcf, mTmpNavigationFrame);
                if (this.mNavigationBarController.checkHiddenLw()) {
                    updateSysUiVisibility = SHOW_STARTING_ANIMATIONS;
                }
            }
            if (this.mStatusBar != null) {
                i2 = this.mUnrestrictedScreenLeft;
                of.left = i2;
                df.left = i2;
                pf.left = i2;
                i2 = this.mUnrestrictedScreenTop;
                of.top = i2;
                df.top = i2;
                pf.top = i2;
                i2 = this.mUnrestrictedScreenWidth + this.mUnrestrictedScreenLeft;
                of.right = i2;
                df.right = i2;
                pf.right = i2;
                i2 = this.mUnrestrictedScreenHeight + this.mUnrestrictedScreenTop;
                of.bottom = i2;
                df.bottom = i2;
                pf.bottom = i2;
                vf.left = this.mStableLeft;
                vf.top = this.mStableTop;
                vf.right = this.mStableRight;
                vf.bottom = this.mStableBottom;
                this.mStatusBarLayer = this.mStatusBar.getSurfaceLayer();
                this.mStatusBar.computeFrameLw(pf, df, vf, vf, vf, dcf, vf);
                this.mStableTop = this.mUnrestrictedScreenTop + this.mStatusBarHeight;
                boolean statusBarTransient = (67108864 & sysui) != 0 ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
                boolean statusBarTranslucent = (1073774592 & sysui) != 0 ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
                if (!isKeyguardShowing) {
                    statusBarTranslucent &= areTranslucentBarsAllowed();
                }
                if (this.mStatusBar.isVisibleLw() && !statusBarTransient) {
                    this.mDockTop = this.mUnrestrictedScreenTop + this.mStatusBarHeight;
                    i2 = this.mDockTop;
                    this.mCurTop = i2;
                    this.mVoiceContentTop = i2;
                    this.mContentTop = i2;
                    i2 = this.mDockBottom;
                    this.mCurBottom = i2;
                    this.mVoiceContentBottom = i2;
                    this.mContentBottom = i2;
                    i2 = this.mDockLeft;
                    this.mCurLeft = i2;
                    this.mVoiceContentLeft = i2;
                    this.mContentLeft = i2;
                    i2 = this.mDockRight;
                    this.mCurRight = i2;
                    this.mVoiceContentRight = i2;
                    this.mContentRight = i2;
                }
                if (!(!this.mStatusBar.isVisibleLw() || this.mStatusBar.isAnimatingLw() || statusBarTransient || statusBarTranslucent || this.mStatusBarController.wasRecentlyTranslucent())) {
                    this.mSystemTop = this.mUnrestrictedScreenTop + this.mStatusBarHeight;
                }
                if (this.mStatusBarController.checkHiddenLw()) {
                    updateSysUiVisibility = SHOW_STARTING_ANIMATIONS;
                }
            }
            if (updateSysUiVisibility) {
                updateSystemUiVisibilityLw();
            }
        }
    }

    public int getSystemDecorLayerLw() {
        if (this.mStatusBar != null && this.mStatusBar.isVisibleLw()) {
            return this.mStatusBar.getSurfaceLayer();
        }
        if (this.mNavigationBar == null || !this.mNavigationBar.isVisibleLw()) {
            return SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        }
        return this.mNavigationBar.getSurfaceLayer();
    }

    public void getContentRectLw(Rect r) {
        r.set(this.mContentLeft, this.mContentTop, this.mContentRight, this.mContentBottom);
    }

    void setAttachedWindowFrames(WindowState win, int fl, int adjust, WindowState attached, boolean insetDecors, Rect pf, Rect df, Rect of, Rect cf, Rect vf) {
        if (win.getSurfaceLayer() <= this.mDockLayer || attached.getSurfaceLayer() >= this.mDockLayer) {
            Rect contentFrameLw;
            if (adjust != 16) {
                if ((1073741824 & fl) != 0) {
                    contentFrameLw = attached.getContentFrameLw();
                } else {
                    contentFrameLw = attached.getOverscanFrameLw();
                }
                cf.set(contentFrameLw);
            } else {
                cf.set(attached.getContentFrameLw());
                if (attached.isVoiceInteraction()) {
                    if (cf.left < this.mVoiceContentLeft) {
                        cf.left = this.mVoiceContentLeft;
                    }
                    if (cf.top < this.mVoiceContentTop) {
                        cf.top = this.mVoiceContentTop;
                    }
                    if (cf.right > this.mVoiceContentRight) {
                        cf.right = this.mVoiceContentRight;
                    }
                    if (cf.bottom > this.mVoiceContentBottom) {
                        cf.bottom = this.mVoiceContentBottom;
                    }
                } else if (attached.getSurfaceLayer() < this.mDockLayer) {
                    if (cf.left < this.mContentLeft) {
                        cf.left = this.mContentLeft;
                    }
                    if (cf.top < this.mContentTop) {
                        cf.top = this.mContentTop;
                    }
                    if (cf.right > this.mContentRight) {
                        cf.right = this.mContentRight;
                    }
                    if (cf.bottom > this.mContentBottom) {
                        cf.bottom = this.mContentBottom;
                    }
                }
            }
            if (insetDecors) {
                contentFrameLw = attached.getDisplayFrameLw();
            } else {
                contentFrameLw = cf;
            }
            df.set(contentFrameLw);
            if (insetDecors) {
                cf = attached.getOverscanFrameLw();
            }
            of.set(cf);
            vf.set(attached.getVisibleFrameLw());
        } else {
            int i = this.mDockLeft;
            vf.left = i;
            cf.left = i;
            of.left = i;
            df.left = i;
            i = this.mDockTop;
            vf.top = i;
            cf.top = i;
            of.top = i;
            df.top = i;
            i = this.mDockRight;
            vf.right = i;
            cf.right = i;
            of.right = i;
            df.right = i;
            i = this.mDockBottom;
            vf.bottom = i;
            cf.bottom = i;
            of.bottom = i;
            df.bottom = i;
        }
        if ((fl & 256) == 0) {
            df = attached.getFrameLw();
        }
        pf.set(df);
    }

    private void applyStableConstraints(int sysui, int fl, Rect r) {
        if ((sysui & 256) == 0) {
            return;
        }
        if ((fl & 1024) != 0) {
            if (r.left < this.mStableFullscreenLeft) {
                r.left = this.mStableFullscreenLeft;
            }
            if (r.top < this.mStableFullscreenTop) {
                r.top = this.mStableFullscreenTop;
            }
            if (r.right > this.mStableFullscreenRight) {
                r.right = this.mStableFullscreenRight;
            }
            if (r.bottom > this.mStableFullscreenBottom) {
                r.bottom = this.mStableFullscreenBottom;
                return;
            }
            return;
        }
        if (r.left < this.mStableLeft) {
            r.left = this.mStableLeft;
        }
        if (r.top < this.mStableTop) {
            r.top = this.mStableTop;
        }
        if (r.right > this.mStableRight) {
            r.right = this.mStableRight;
        }
        if (r.bottom > this.mStableBottom) {
            r.bottom = this.mStableBottom;
        }
    }

    public void layoutWindowLw(WindowState win, WindowState attached) {
        LayoutParams attrs = win.getAttrs();
        if ((win != this.mStatusBar || (attrs.privateFlags & 1024) != 0) && win != this.mNavigationBar) {
            boolean isDefaultDisplay = win.isDefaultDisplay();
            boolean needsToOffsetInputMethodTarget = (isDefaultDisplay && win == this.mLastInputMethodTargetWindow && this.mLastInputMethodWindow != null) ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
            if (needsToOffsetInputMethodTarget) {
                offsetInputMethodWindowLw(this.mLastInputMethodWindow);
            }
            int fl = PolicyControl.getWindowFlags(win, attrs);
            int sim = attrs.softInputMode;
            int sysUiFl = PolicyControl.getSystemUiVisibility(win, null);
            Rect pf = mTmpParentFrame;
            Rect df = mTmpDisplayFrame;
            Rect of = mTmpOverscanFrame;
            Rect cf = mTmpContentFrame;
            Rect vf = mTmpVisibleFrame;
            Rect dcf = mTmpDecorFrame;
            Rect sf = mTmpStableFrame;
            dcf.setEmpty();
            boolean hasNavBar = (isDefaultDisplay && this.mHasNavigationBar && this.mNavigationBar != null && this.mNavigationBar.isVisibleLw()) ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
            int adjust = sim & 240;
            if (isDefaultDisplay) {
                sf.set(this.mStableLeft, this.mStableTop, this.mStableRight, this.mStableBottom);
            } else {
                sf.set(this.mOverscanLeft, this.mOverscanTop, this.mOverscanRight, this.mOverscanBottom);
            }
            int i;
            if (isDefaultDisplay) {
                if (attrs.type == 2011) {
                    i = this.mDockLeft;
                    vf.left = i;
                    cf.left = i;
                    of.left = i;
                    df.left = i;
                    pf.left = i;
                    i = this.mDockTop;
                    vf.top = i;
                    cf.top = i;
                    of.top = i;
                    df.top = i;
                    pf.top = i;
                    i = this.mDockRight;
                    vf.right = i;
                    cf.right = i;
                    of.right = i;
                    df.right = i;
                    pf.right = i;
                    i = this.mUnrestrictedScreenTop + this.mUnrestrictedScreenHeight;
                    of.bottom = i;
                    df.bottom = i;
                    pf.bottom = i;
                    i = this.mStableBottom;
                    vf.bottom = i;
                    cf.bottom = i;
                    attrs.gravity = 80;
                    this.mDockLayer = win.getSurfaceLayer();
                } else if (win != this.mStatusBar || (attrs.privateFlags & 1024) == 0) {
                    dcf.left = this.mSystemLeft;
                    dcf.top = this.mSystemTop;
                    dcf.right = this.mSystemRight;
                    dcf.bottom = this.mSystemBottom;
                    boolean inheritTranslucentDecor = (attrs.privateFlags & 512) != 0 ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
                    boolean isAppWindow = (attrs.type < SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME || attrs.type > 99) ? SHOW_PROCESSES_ON_ALT_MENU : SHOW_STARTING_ANIMATIONS;
                    boolean topAtRest = (win != this.mTopFullscreenOpaqueWindowState || win.isAnimatingLw()) ? SHOW_PROCESSES_ON_ALT_MENU : SHOW_STARTING_ANIMATIONS;
                    if (!(!isAppWindow || inheritTranslucentDecor || topAtRest)) {
                        if ((sysUiFl & SHORT_PRESS_POWER_GO_HOME) == 0 && (fl & 1024) == 0 && (67108864 & fl) == 0 && (Integer.MIN_VALUE & fl) == 0) {
                            dcf.top = this.mStableTop;
                        }
                        if ((134217728 & fl) == 0 && (sysUiFl & SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP) == 0 && (Integer.MIN_VALUE & fl) == 0) {
                            dcf.bottom = this.mStableBottom;
                            dcf.right = this.mStableRight;
                        }
                    }
                    if ((65792 & fl) == 65792) {
                        if (attached != null) {
                            setAttachedWindowFrames(win, fl, adjust, attached, SHOW_STARTING_ANIMATIONS, pf, df, of, cf, vf);
                        } else {
                            if (attrs.type == 2014 || attrs.type == 2017) {
                                i = hasNavBar ? this.mDockLeft : this.mUnrestrictedScreenLeft;
                                of.left = i;
                                df.left = i;
                                pf.left = i;
                                i = this.mUnrestrictedScreenTop;
                                of.top = i;
                                df.top = i;
                                pf.top = i;
                                i = hasNavBar ? this.mRestrictedScreenLeft + this.mRestrictedScreenWidth : this.mUnrestrictedScreenLeft + this.mUnrestrictedScreenWidth;
                                of.right = i;
                                df.right = i;
                                pf.right = i;
                                if (hasNavBar) {
                                    i = this.mRestrictedScreenTop + this.mRestrictedScreenHeight;
                                } else {
                                    i = this.mUnrestrictedScreenTop + this.mUnrestrictedScreenHeight;
                                }
                                of.bottom = i;
                                df.bottom = i;
                                pf.bottom = i;
                            } else if ((33554432 & fl) != 0 && attrs.type >= SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME && attrs.type <= 1999) {
                                i = this.mOverscanScreenLeft;
                                of.left = i;
                                df.left = i;
                                pf.left = i;
                                i = this.mOverscanScreenTop;
                                of.top = i;
                                df.top = i;
                                pf.top = i;
                                i = this.mOverscanScreenLeft + this.mOverscanScreenWidth;
                                of.right = i;
                                df.right = i;
                                pf.right = i;
                                i = this.mOverscanScreenTop + this.mOverscanScreenHeight;
                                of.bottom = i;
                                df.bottom = i;
                                pf.bottom = i;
                            } else if (!canHideNavigationBar() || (sysUiFl & 512) == 0 || attrs.type < SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME || attrs.type > 1999) {
                                i = this.mRestrictedOverscanScreenLeft;
                                df.left = i;
                                pf.left = i;
                                i = this.mRestrictedOverscanScreenTop;
                                df.top = i;
                                pf.top = i;
                                i = this.mRestrictedOverscanScreenLeft + this.mRestrictedOverscanScreenWidth;
                                df.right = i;
                                pf.right = i;
                                i = this.mRestrictedOverscanScreenTop + this.mRestrictedOverscanScreenHeight;
                                df.bottom = i;
                                pf.bottom = i;
                                of.left = this.mUnrestrictedScreenLeft;
                                of.top = this.mUnrestrictedScreenTop;
                                of.right = this.mUnrestrictedScreenLeft + this.mUnrestrictedScreenWidth;
                                of.bottom = this.mUnrestrictedScreenTop + this.mUnrestrictedScreenHeight;
                            } else {
                                i = this.mOverscanScreenLeft;
                                df.left = i;
                                pf.left = i;
                                i = this.mOverscanScreenTop;
                                df.top = i;
                                pf.top = i;
                                i = this.mOverscanScreenLeft + this.mOverscanScreenWidth;
                                df.right = i;
                                pf.right = i;
                                i = this.mOverscanScreenTop + this.mOverscanScreenHeight;
                                df.bottom = i;
                                pf.bottom = i;
                                of.left = this.mUnrestrictedScreenLeft;
                                of.top = this.mUnrestrictedScreenTop;
                                of.right = this.mUnrestrictedScreenLeft + this.mUnrestrictedScreenWidth;
                                of.bottom = this.mUnrestrictedScreenTop + this.mUnrestrictedScreenHeight;
                            }
                            if ((fl & 1024) != 0) {
                                cf.left = this.mRestrictedScreenLeft;
                                cf.top = this.mRestrictedScreenTop;
                                cf.right = this.mRestrictedScreenLeft + this.mRestrictedScreenWidth;
                                cf.bottom = this.mRestrictedScreenTop + this.mRestrictedScreenHeight;
                            } else if (win.isVoiceInteraction()) {
                                cf.left = this.mVoiceContentLeft;
                                cf.top = this.mVoiceContentTop;
                                cf.right = this.mVoiceContentRight;
                                cf.bottom = this.mVoiceContentBottom;
                            } else if (adjust != 16) {
                                cf.left = this.mDockLeft;
                                cf.top = this.mDockTop;
                                cf.right = this.mDockRight;
                                cf.bottom = this.mDockBottom;
                            } else {
                                cf.left = this.mContentLeft;
                                cf.top = this.mContentTop;
                                cf.right = this.mContentRight;
                                cf.bottom = this.mContentBottom;
                            }
                            applyStableConstraints(sysUiFl, fl, cf);
                            if (adjust != 48) {
                                vf.left = this.mCurLeft;
                                vf.top = this.mCurTop;
                                vf.right = this.mCurRight;
                                vf.bottom = this.mCurBottom;
                            } else {
                                vf.set(cf);
                            }
                        }
                    } else if ((fl & 256) != 0 || (sysUiFl & 1536) != 0) {
                        if (attrs.type == 2014 || attrs.type == 2017) {
                            i = hasNavBar ? this.mDockLeft : this.mUnrestrictedScreenLeft;
                            cf.left = i;
                            of.left = i;
                            df.left = i;
                            pf.left = i;
                            i = this.mUnrestrictedScreenTop;
                            cf.top = i;
                            of.top = i;
                            df.top = i;
                            pf.top = i;
                            i = hasNavBar ? this.mRestrictedScreenLeft + this.mRestrictedScreenWidth : this.mUnrestrictedScreenLeft + this.mUnrestrictedScreenWidth;
                            cf.right = i;
                            of.right = i;
                            df.right = i;
                            pf.right = i;
                            if (hasNavBar) {
                                i = this.mRestrictedScreenTop + this.mRestrictedScreenHeight;
                            } else {
                                i = this.mUnrestrictedScreenTop + this.mUnrestrictedScreenHeight;
                            }
                            cf.bottom = i;
                            of.bottom = i;
                            df.bottom = i;
                            pf.bottom = i;
                        } else if (attrs.type == 2019 || attrs.type == 2024) {
                            i = this.mUnrestrictedScreenLeft;
                            of.left = i;
                            df.left = i;
                            pf.left = i;
                            i = this.mUnrestrictedScreenTop;
                            of.top = i;
                            df.top = i;
                            pf.top = i;
                            i = this.mUnrestrictedScreenLeft + this.mUnrestrictedScreenWidth;
                            of.right = i;
                            df.right = i;
                            pf.right = i;
                            i = this.mUnrestrictedScreenTop + this.mUnrestrictedScreenHeight;
                            of.bottom = i;
                            df.bottom = i;
                            pf.bottom = i;
                        } else if ((attrs.type == 2015 || attrs.type == 2021) && (fl & 1024) != 0) {
                            i = this.mOverscanScreenLeft;
                            cf.left = i;
                            of.left = i;
                            df.left = i;
                            pf.left = i;
                            i = this.mOverscanScreenTop;
                            cf.top = i;
                            of.top = i;
                            df.top = i;
                            pf.top = i;
                            i = this.mOverscanScreenLeft + this.mOverscanScreenWidth;
                            cf.right = i;
                            of.right = i;
                            df.right = i;
                            pf.right = i;
                            i = this.mOverscanScreenTop + this.mOverscanScreenHeight;
                            cf.bottom = i;
                            of.bottom = i;
                            df.bottom = i;
                            pf.bottom = i;
                        } else if (attrs.type == 2021 || attrs.type == 2025) {
                            i = this.mOverscanScreenLeft;
                            cf.left = i;
                            of.left = i;
                            df.left = i;
                            pf.left = i;
                            i = this.mOverscanScreenTop;
                            cf.top = i;
                            of.top = i;
                            df.top = i;
                            pf.top = i;
                            i = this.mOverscanScreenLeft + this.mOverscanScreenWidth;
                            cf.right = i;
                            of.right = i;
                            df.right = i;
                            pf.right = i;
                            i = this.mOverscanScreenTop + this.mOverscanScreenHeight;
                            cf.bottom = i;
                            of.bottom = i;
                            df.bottom = i;
                            pf.bottom = i;
                        } else if (attrs.type == 2013) {
                            i = this.mOverscanScreenLeft;
                            df.left = i;
                            pf.left = i;
                            i = this.mOverscanScreenTop;
                            df.top = i;
                            pf.top = i;
                            i = this.mOverscanScreenLeft + this.mOverscanScreenWidth;
                            df.right = i;
                            pf.right = i;
                            i = this.mOverscanScreenTop + this.mOverscanScreenHeight;
                            df.bottom = i;
                            pf.bottom = i;
                            i = this.mUnrestrictedScreenLeft;
                            cf.left = i;
                            of.left = i;
                            i = this.mUnrestrictedScreenTop;
                            cf.top = i;
                            of.top = i;
                            i = this.mUnrestrictedScreenLeft + this.mUnrestrictedScreenWidth;
                            cf.right = i;
                            of.right = i;
                            i = this.mUnrestrictedScreenTop + this.mUnrestrictedScreenHeight;
                            cf.bottom = i;
                            of.bottom = i;
                        } else if ((33554432 & fl) != 0 && attrs.type >= SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME && attrs.type <= 1999) {
                            i = this.mOverscanScreenLeft;
                            cf.left = i;
                            of.left = i;
                            df.left = i;
                            pf.left = i;
                            i = this.mOverscanScreenTop;
                            cf.top = i;
                            of.top = i;
                            df.top = i;
                            pf.top = i;
                            i = this.mOverscanScreenLeft + this.mOverscanScreenWidth;
                            cf.right = i;
                            of.right = i;
                            df.right = i;
                            pf.right = i;
                            i = this.mOverscanScreenTop + this.mOverscanScreenHeight;
                            cf.bottom = i;
                            of.bottom = i;
                            df.bottom = i;
                            pf.bottom = i;
                        } else if (canHideNavigationBar() && (sysUiFl & 512) != 0 && (attrs.type == 2000 || attrs.type == 2005 || (attrs.type >= SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME && attrs.type <= 1999))) {
                            i = this.mUnrestrictedScreenLeft;
                            cf.left = i;
                            of.left = i;
                            df.left = i;
                            pf.left = i;
                            i = this.mUnrestrictedScreenTop;
                            cf.top = i;
                            of.top = i;
                            df.top = i;
                            pf.top = i;
                            i = this.mUnrestrictedScreenLeft + this.mUnrestrictedScreenWidth;
                            cf.right = i;
                            of.right = i;
                            df.right = i;
                            pf.right = i;
                            i = this.mUnrestrictedScreenTop + this.mUnrestrictedScreenHeight;
                            cf.bottom = i;
                            of.bottom = i;
                            df.bottom = i;
                            pf.bottom = i;
                        } else {
                            i = this.mRestrictedScreenLeft;
                            cf.left = i;
                            of.left = i;
                            df.left = i;
                            pf.left = i;
                            i = this.mRestrictedScreenTop;
                            cf.top = i;
                            of.top = i;
                            df.top = i;
                            pf.top = i;
                            i = this.mRestrictedScreenLeft + this.mRestrictedScreenWidth;
                            cf.right = i;
                            of.right = i;
                            df.right = i;
                            pf.right = i;
                            i = this.mRestrictedScreenTop + this.mRestrictedScreenHeight;
                            cf.bottom = i;
                            of.bottom = i;
                            df.bottom = i;
                            pf.bottom = i;
                        }
                        applyStableConstraints(sysUiFl, fl, cf);
                        if (adjust != 48) {
                            vf.left = this.mCurLeft;
                            vf.top = this.mCurTop;
                            vf.right = this.mCurRight;
                            vf.bottom = this.mCurBottom;
                        } else {
                            vf.set(cf);
                        }
                    } else if (attached != null) {
                        setAttachedWindowFrames(win, fl, adjust, attached, SHOW_PROCESSES_ON_ALT_MENU, pf, df, of, cf, vf);
                    } else if (attrs.type == 2014) {
                        i = this.mRestrictedScreenLeft;
                        cf.left = i;
                        of.left = i;
                        df.left = i;
                        pf.left = i;
                        i = this.mRestrictedScreenTop;
                        cf.top = i;
                        of.top = i;
                        df.top = i;
                        pf.top = i;
                        i = this.mRestrictedScreenLeft + this.mRestrictedScreenWidth;
                        cf.right = i;
                        of.right = i;
                        df.right = i;
                        pf.right = i;
                        i = this.mRestrictedScreenTop + this.mRestrictedScreenHeight;
                        cf.bottom = i;
                        of.bottom = i;
                        df.bottom = i;
                        pf.bottom = i;
                    } else if (attrs.type == 2005 || attrs.type == 2003 || attrs.type == 2020) {
                        i = this.mStableLeft;
                        cf.left = i;
                        of.left = i;
                        df.left = i;
                        pf.left = i;
                        i = this.mStableTop;
                        cf.top = i;
                        of.top = i;
                        df.top = i;
                        pf.top = i;
                        i = this.mStableRight;
                        cf.right = i;
                        of.right = i;
                        df.right = i;
                        pf.right = i;
                        i = this.mStableBottom;
                        cf.bottom = i;
                        of.bottom = i;
                        df.bottom = i;
                        pf.bottom = i;
                    } else {
                        pf.left = this.mContentLeft;
                        pf.top = this.mContentTop;
                        pf.right = this.mContentRight;
                        pf.bottom = this.mContentBottom;
                        if (win.isVoiceInteraction()) {
                            i = this.mVoiceContentLeft;
                            cf.left = i;
                            of.left = i;
                            df.left = i;
                            i = this.mVoiceContentTop;
                            cf.top = i;
                            of.top = i;
                            df.top = i;
                            i = this.mVoiceContentRight;
                            cf.right = i;
                            of.right = i;
                            df.right = i;
                            i = this.mVoiceContentBottom;
                            cf.bottom = i;
                            of.bottom = i;
                            df.bottom = i;
                        } else if (adjust != 16) {
                            i = this.mDockLeft;
                            cf.left = i;
                            of.left = i;
                            df.left = i;
                            i = this.mDockTop;
                            cf.top = i;
                            of.top = i;
                            df.top = i;
                            i = this.mDockRight;
                            cf.right = i;
                            of.right = i;
                            df.right = i;
                            i = this.mDockBottom;
                            cf.bottom = i;
                            of.bottom = i;
                            df.bottom = i;
                        } else {
                            i = this.mContentLeft;
                            cf.left = i;
                            of.left = i;
                            df.left = i;
                            i = this.mContentTop;
                            cf.top = i;
                            of.top = i;
                            df.top = i;
                            i = this.mContentRight;
                            cf.right = i;
                            of.right = i;
                            df.right = i;
                            i = this.mContentBottom;
                            cf.bottom = i;
                            of.bottom = i;
                            df.bottom = i;
                        }
                        if (adjust != 48) {
                            vf.left = this.mCurLeft;
                            vf.top = this.mCurTop;
                            vf.right = this.mCurRight;
                            vf.bottom = this.mCurBottom;
                        } else {
                            vf.set(cf);
                        }
                    }
                } else {
                    i = this.mUnrestrictedScreenLeft;
                    of.left = i;
                    df.left = i;
                    pf.left = i;
                    i = this.mUnrestrictedScreenTop;
                    of.top = i;
                    df.top = i;
                    pf.top = i;
                    i = this.mUnrestrictedScreenWidth + this.mUnrestrictedScreenLeft;
                    of.right = i;
                    df.right = i;
                    pf.right = i;
                    i = this.mUnrestrictedScreenHeight + this.mUnrestrictedScreenTop;
                    of.bottom = i;
                    df.bottom = i;
                    pf.bottom = i;
                    i = this.mStableLeft;
                    vf.left = i;
                    cf.left = i;
                    i = this.mStableTop;
                    vf.top = i;
                    cf.top = i;
                    i = this.mStableRight;
                    vf.right = i;
                    cf.right = i;
                    vf.bottom = this.mStableBottom;
                    cf.bottom = this.mContentBottom;
                }
            } else if (attached != null) {
                setAttachedWindowFrames(win, fl, adjust, attached, SHOW_STARTING_ANIMATIONS, pf, df, of, cf, vf);
            } else {
                i = this.mOverscanScreenLeft;
                cf.left = i;
                of.left = i;
                df.left = i;
                pf.left = i;
                i = this.mOverscanScreenTop;
                cf.top = i;
                of.top = i;
                df.top = i;
                pf.top = i;
                i = this.mOverscanScreenLeft + this.mOverscanScreenWidth;
                cf.right = i;
                of.right = i;
                df.right = i;
                pf.right = i;
                i = this.mOverscanScreenTop + this.mOverscanScreenHeight;
                cf.bottom = i;
                of.bottom = i;
                df.bottom = i;
                pf.bottom = i;
            }
            if (!((fl & 512) == 0 || attrs.type == 2010)) {
                df.top = -10000;
                df.left = -10000;
                df.bottom = 10000;
                df.right = 10000;
                if (attrs.type != 2013) {
                    vf.top = -10000;
                    vf.left = -10000;
                    cf.top = -10000;
                    cf.left = -10000;
                    of.top = -10000;
                    of.left = -10000;
                    vf.bottom = 10000;
                    vf.right = 10000;
                    cf.bottom = 10000;
                    cf.right = 10000;
                    of.bottom = 10000;
                    of.right = 10000;
                }
            }
            win.computeFrameLw(pf, df, of, cf, vf, dcf, sf);
            if (attrs.type == 2011 && win.isVisibleOrBehindKeyguardLw() && !win.getGivenInsetsPendingLw()) {
                setLastInputMethodWindowLw(null, null);
                offsetInputMethodWindowLw(win);
            }
            if (attrs.type == 2031 && win.isVisibleOrBehindKeyguardLw() && !win.getGivenInsetsPendingLw()) {
                offsetVoiceInputWindowLw(win);
            }
        }
    }

    private void offsetInputMethodWindowLw(WindowState win) {
        int top = win.getContentFrameLw().top + win.getGivenContentInsetsLw().top;
        if (this.mContentBottom > top) {
            this.mContentBottom = top;
        }
        if (this.mVoiceContentBottom > top) {
            this.mVoiceContentBottom = top;
        }
        top = win.getVisibleFrameLw().top + win.getGivenVisibleInsetsLw().top;
        if (this.mCurBottom > top) {
            this.mCurBottom = top;
        }
    }

    private void offsetVoiceInputWindowLw(WindowState win) {
        int gravity = win.getAttrs().gravity;
        switch (gravity & MSG_KEYGUARD_DRAWN_TIMEOUT) {
            case SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP /*2*/:
                int right = win.getContentFrameLw().right - win.getGivenContentInsetsLw().right;
                if (this.mVoiceContentLeft < right) {
                    this.mVoiceContentLeft = right;
                    break;
                }
                break;
            case SHORT_PRESS_POWER_GO_HOME /*4*/:
                int left = win.getContentFrameLw().left - win.getGivenContentInsetsLw().left;
                if (this.mVoiceContentRight < left) {
                    this.mVoiceContentRight = left;
                    break;
                }
                break;
        }
        switch (gravity & 96) {
            case 32:
                int bottom = win.getContentFrameLw().bottom - win.getGivenContentInsetsLw().bottom;
                if (this.mVoiceContentTop < bottom) {
                    this.mVoiceContentTop = bottom;
                }
            case 64:
                int top = win.getContentFrameLw().top - win.getGivenContentInsetsLw().top;
                if (this.mVoiceContentBottom < top) {
                    this.mVoiceContentBottom = top;
                }
            default:
        }
    }

    public void finishLayoutLw() {
    }

    public void beginPostLayoutPolicyLw(int displayWidth, int displayHeight) {
        boolean z = SHOW_PROCESSES_ON_ALT_MENU;
        this.mTopFullscreenOpaqueWindowState = null;
        this.mAppsToBeHidden.clear();
        this.mAppsThatDismissKeyguard.clear();
        this.mForceStatusBar = SHOW_PROCESSES_ON_ALT_MENU;
        this.mForceStatusBarFromKeyguard = SHOW_PROCESSES_ON_ALT_MENU;
        this.mForcingShowNavBar = SHOW_PROCESSES_ON_ALT_MENU;
        this.mForcingShowNavBarLayer = APPLICATION_MEDIA_OVERLAY_SUBLAYER;
        this.mHideLockScreen = SHOW_PROCESSES_ON_ALT_MENU;
        this.mAllowLockscreenWhenOn = SHOW_PROCESSES_ON_ALT_MENU;
        this.mDismissKeyguard = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        this.mShowingLockscreen = SHOW_PROCESSES_ON_ALT_MENU;
        this.mShowingDream = SHOW_PROCESSES_ON_ALT_MENU;
        this.mWinShowWhenLocked = null;
        this.mKeyguardSecure = isKeyguardSecure();
        if (this.mKeyguardSecure && this.mKeyguardDelegate != null && this.mKeyguardDelegate.isShowing()) {
            z = SHOW_STARTING_ANIMATIONS;
        }
        this.mKeyguardSecureIncludingHidden = z;
    }

    public void applyPostLayoutPolicyLw(WindowState win, LayoutParams attrs) {
        int fl = PolicyControl.getWindowFlags(win, attrs);
        if (this.mTopFullscreenOpaqueWindowState == null && win.isVisibleLw() && attrs.type == 2011) {
            this.mForcingShowNavBar = SHOW_STARTING_ANIMATIONS;
            this.mForcingShowNavBarLayer = win.getSurfaceLayer();
        }
        if (attrs.type == 2000 && (attrs.privateFlags & 1024) != 0) {
            this.mForceStatusBarFromKeyguard = SHOW_STARTING_ANIMATIONS;
        }
        if (this.mTopFullscreenOpaqueWindowState == null && win.isVisibleOrBehindKeyguardLw() && !win.isGoneForLayoutLw()) {
            boolean appWindow;
            boolean showWhenLocked;
            boolean dismissKeyguard;
            if ((fl & 2048) != 0) {
                if ((attrs.privateFlags & 1024) != 0) {
                    this.mForceStatusBarFromKeyguard = SHOW_STARTING_ANIMATIONS;
                } else {
                    this.mForceStatusBar = SHOW_STARTING_ANIMATIONS;
                }
            }
            if ((attrs.privateFlags & 1024) != 0) {
                this.mShowingLockscreen = SHOW_STARTING_ANIMATIONS;
            }
            if (attrs.type < SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME || attrs.type >= 2000) {
                appWindow = SHOW_PROCESSES_ON_ALT_MENU;
            } else {
                appWindow = SHOW_STARTING_ANIMATIONS;
            }
            if (attrs.type == 2023 && (!this.mDreamingLockscreen || (win.isVisibleLw() && win.hasDrawnLw()))) {
                this.mShowingDream = SHOW_STARTING_ANIMATIONS;
                appWindow = SHOW_STARTING_ANIMATIONS;
            }
            if ((524288 & fl) != 0) {
                showWhenLocked = SHOW_STARTING_ANIMATIONS;
            } else {
                showWhenLocked = SHOW_PROCESSES_ON_ALT_MENU;
            }
            if ((4194304 & fl) != 0) {
                dismissKeyguard = SHOW_STARTING_ANIMATIONS;
            } else {
                dismissKeyguard = SHOW_PROCESSES_ON_ALT_MENU;
            }
            if (appWindow) {
                IApplicationToken appToken = win.getAppToken();
                if (showWhenLocked) {
                    this.mAppsToBeHidden.remove(appToken);
                    this.mAppsThatDismissKeyguard.remove(appToken);
                    if (this.mAppsToBeHidden.isEmpty()) {
                        if (!dismissKeyguard || this.mKeyguardSecure) {
                            this.mWinShowWhenLocked = win;
                            this.mHideLockScreen = SHOW_STARTING_ANIMATIONS;
                            this.mForceStatusBarFromKeyguard = SHOW_PROCESSES_ON_ALT_MENU;
                        } else {
                            this.mAppsThatDismissKeyguard.add(appToken);
                        }
                    }
                } else if (dismissKeyguard) {
                    if (this.mKeyguardSecure) {
                        this.mAppsToBeHidden.add(appToken);
                    } else {
                        this.mAppsToBeHidden.remove(appToken);
                    }
                    this.mAppsThatDismissKeyguard.add(appToken);
                } else {
                    this.mAppsToBeHidden.add(appToken);
                }
                if (attrs.x == 0 && attrs.y == 0 && attrs.width == APPLICATION_MEDIA_OVERLAY_SUBLAYER && attrs.height == APPLICATION_MEDIA_OVERLAY_SUBLAYER) {
                    this.mTopFullscreenOpaqueWindowState = win;
                    if (!this.mAppsThatDismissKeyguard.isEmpty() && this.mDismissKeyguard == 0) {
                        boolean z;
                        this.mDismissKeyguard = this.mWinDismissingKeyguard == win ? SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP : SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
                        this.mWinDismissingKeyguard = win;
                        if (this.mShowingLockscreen && this.mKeyguardSecure) {
                            z = SHOW_STARTING_ANIMATIONS;
                        } else {
                            z = SHOW_PROCESSES_ON_ALT_MENU;
                        }
                        this.mForceStatusBarFromKeyguard = z;
                    } else if (this.mAppsToBeHidden.isEmpty() && showWhenLocked) {
                        this.mHideLockScreen = SHOW_STARTING_ANIMATIONS;
                        this.mForceStatusBarFromKeyguard = SHOW_PROCESSES_ON_ALT_MENU;
                    }
                    if ((fl & SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) != 0) {
                        this.mAllowLockscreenWhenOn = SHOW_STARTING_ANIMATIONS;
                    }
                }
                if (this.mWinShowWhenLocked != null && this.mWinShowWhenLocked.getAppToken() != win.getAppToken()) {
                    win.hideLw(SHOW_PROCESSES_ON_ALT_MENU);
                }
            }
        }
    }

    public int finishPostLayoutPolicyLw() {
        LayoutParams lp;
        if (!(this.mWinShowWhenLocked == null || this.mWinShowWhenLocked == this.mTopFullscreenOpaqueWindowState)) {
            LayoutParams attrs = this.mWinShowWhenLocked.getAttrs();
            attrs.flags |= 1048576;
            this.mTopFullscreenOpaqueWindowState.hideLw(SHOW_PROCESSES_ON_ALT_MENU);
            this.mTopFullscreenOpaqueWindowState = this.mWinShowWhenLocked;
        }
        int changes = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        boolean topIsFullscreen = SHOW_PROCESSES_ON_ALT_MENU;
        if (this.mTopFullscreenOpaqueWindowState != null) {
            lp = this.mTopFullscreenOpaqueWindowState.getAttrs();
        } else {
            lp = null;
        }
        if (!this.mShowingDream) {
            this.mDreamingLockscreen = this.mShowingLockscreen;
        }
        if (this.mStatusBar != null) {
            if (this.mForceStatusBar || this.mForceStatusBarFromKeyguard) {
                if (this.mStatusBarController.setBarShowingLw(SHOW_STARTING_ANIMATIONS)) {
                    changes = SHORT_PRESS_SLEEP_GO_TO_SLEEP | SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
                }
                if (this.mTopIsFullscreen && this.mStatusBar.isAnimatingLw()) {
                    topIsFullscreen = SHOW_STARTING_ANIMATIONS;
                } else {
                    topIsFullscreen = SHOW_PROCESSES_ON_ALT_MENU;
                }
                if (this.mForceStatusBarFromKeyguard && this.mStatusBarController.isTransientShowing()) {
                    this.mStatusBarController.updateVisibilityLw(SHOW_PROCESSES_ON_ALT_MENU, this.mLastSystemUiFlags, this.mLastSystemUiFlags);
                }
            } else if (this.mTopFullscreenOpaqueWindowState != null) {
                topIsFullscreen = ((PolicyControl.getWindowFlags(null, lp) & 1024) == 0 && (this.mLastSystemUiFlags & SHORT_PRESS_POWER_GO_HOME) == 0) ? SHOW_PROCESSES_ON_ALT_MENU : SHOW_STARTING_ANIMATIONS;
                if (this.mStatusBarController.isTransientShowing()) {
                    if (this.mStatusBarController.setBarShowingLw(SHOW_STARTING_ANIMATIONS)) {
                        changes = SHORT_PRESS_SLEEP_GO_TO_SLEEP | SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
                    }
                } else if (topIsFullscreen) {
                    if (this.mStatusBarController.setBarShowingLw(SHOW_PROCESSES_ON_ALT_MENU)) {
                        changes = SHORT_PRESS_SLEEP_GO_TO_SLEEP | SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
                    }
                } else if (this.mStatusBarController.setBarShowingLw(SHOW_STARTING_ANIMATIONS)) {
                    changes = SHORT_PRESS_SLEEP_GO_TO_SLEEP | SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
                }
            }
        }
        if (this.mTopIsFullscreen != topIsFullscreen) {
            if (!topIsFullscreen) {
                changes |= SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
            }
            this.mTopIsFullscreen = topIsFullscreen;
        }
        if (!(this.mKeyguardDelegate == null || this.mStatusBar == null)) {
            if (this.mDismissKeyguard != 0 && !this.mKeyguardSecure) {
                this.mKeyguardHidden = SHOW_STARTING_ANIMATIONS;
                if (setKeyguardOccludedLw(SHOW_STARTING_ANIMATIONS)) {
                    changes |= MSG_WINDOW_MANAGER_DRAWN_COMPLETE;
                }
                if (this.mKeyguardDelegate.isShowing()) {
                    this.mHandler.post(new AnonymousClass11(this));
                }
            } else if (this.mHideLockScreen) {
                this.mKeyguardHidden = SHOW_STARTING_ANIMATIONS;
                if (setKeyguardOccludedLw(SHOW_STARTING_ANIMATIONS)) {
                    changes |= MSG_WINDOW_MANAGER_DRAWN_COMPLETE;
                }
            } else if (this.mDismissKeyguard == 0) {
                this.mWinDismissingKeyguard = null;
                this.mKeyguardHidden = SHOW_PROCESSES_ON_ALT_MENU;
                if (setKeyguardOccludedLw(SHOW_PROCESSES_ON_ALT_MENU)) {
                    changes |= MSG_WINDOW_MANAGER_DRAWN_COMPLETE;
                }
            } else if (this.mDismissKeyguard == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) {
                this.mKeyguardHidden = SHOW_PROCESSES_ON_ALT_MENU;
                if (setKeyguardOccludedLw(SHOW_PROCESSES_ON_ALT_MENU)) {
                    changes |= MSG_WINDOW_MANAGER_DRAWN_COMPLETE;
                }
                this.mHandler.post(new AnonymousClass12(this));
            }
        }
        if ((updateSystemUiVisibilityLw() & SYSTEM_UI_CHANGING_LAYOUT) != 0) {
            changes |= SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
        }
        updateLockScreenTimeout();
        return changes;
    }

    private boolean setKeyguardOccludedLw(boolean isOccluded) {
        boolean wasOccluded = this.mKeyguardOccluded;
        boolean showing = this.mKeyguardDelegate.isShowing();
        LayoutParams attrs;
        if (wasOccluded && !isOccluded && showing) {
            this.mKeyguardOccluded = SHOW_PROCESSES_ON_ALT_MENU;
            this.mKeyguardDelegate.setOccluded(SHOW_PROCESSES_ON_ALT_MENU);
            attrs = this.mStatusBar.getAttrs();
            attrs.privateFlags |= 1024;
            attrs = this.mStatusBar.getAttrs();
            attrs.flags |= 1048576;
            return SHOW_STARTING_ANIMATIONS;
        } else if (wasOccluded || !isOccluded || !showing) {
            return SHOW_PROCESSES_ON_ALT_MENU;
        } else {
            this.mKeyguardOccluded = SHOW_STARTING_ANIMATIONS;
            this.mKeyguardDelegate.setOccluded(SHOW_STARTING_ANIMATIONS);
            attrs = this.mStatusBar.getAttrs();
            attrs.privateFlags &= -1025;
            attrs = this.mStatusBar.getAttrs();
            attrs.flags &= -1048577;
            return SHOW_STARTING_ANIMATIONS;
        }
    }

    private boolean isStatusBarKeyguard() {
        return (this.mStatusBar == null || (this.mStatusBar.getAttrs().privateFlags & 1024) == 0) ? SHOW_PROCESSES_ON_ALT_MENU : SHOW_STARTING_ANIMATIONS;
    }

    public boolean allowAppAnimationsLw() {
        if (isStatusBarKeyguard() || this.mShowingDream) {
            return SHOW_PROCESSES_ON_ALT_MENU;
        }
        return SHOW_STARTING_ANIMATIONS;
    }

    public int focusChangedLw(WindowState lastFocus, WindowState newFocus) {
        this.mFocusedWindow = newFocus;
        if ((updateSystemUiVisibilityLw() & SYSTEM_UI_CHANGING_LAYOUT) != 0) {
            return SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
        }
        return SHORT_PRESS_SLEEP_GO_TO_SLEEP;
    }

    public void notifyLidSwitchChanged(long whenNanos, boolean lidOpen) {
        int newLidState;
        if (lidOpen) {
            newLidState = SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
        } else {
            newLidState = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        }
        if (newLidState != this.mLidState) {
            this.mLidState = newLidState;
            applyLidSwitchState();
            updateRotation(SHOW_STARTING_ANIMATIONS);
            if (lidOpen) {
                wakeUp(SystemClock.uptimeMillis(), this.mAllowTheaterModeWakeFromLidSwitch);
            } else if (!this.mLidControlsSleep) {
                this.mPowerManager.userActivity(SystemClock.uptimeMillis(), SHOW_PROCESSES_ON_ALT_MENU);
            }
        }
    }

    public void notifyCameraLensCoverSwitchChanged(long whenNanos, boolean lensCovered) {
        int lensCoverState;
        if (lensCovered) {
            lensCoverState = SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
        } else {
            lensCoverState = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        }
        if (this.mCameraLensCoverState != lensCoverState) {
            if (this.mCameraLensCoverState == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME && lensCoverState == 0) {
                Intent intent;
                if (this.mKeyguardDelegate == null ? SHOW_PROCESSES_ON_ALT_MENU : this.mKeyguardDelegate.isShowing()) {
                    intent = new Intent("android.media.action.STILL_IMAGE_CAMERA_SECURE");
                } else {
                    intent = new Intent("android.media.action.STILL_IMAGE_CAMERA");
                }
                wakeUp(whenNanos / 1000000, this.mAllowTheaterModeWakeFromCameraLens);
                startActivityAsUser(intent, UserHandle.CURRENT_OR_SELF);
            }
            this.mCameraLensCoverState = lensCoverState;
        }
    }

    void setHdmiPlugged(boolean plugged) {
        if (this.mHdmiPlugged != plugged) {
            this.mHdmiPlugged = plugged;
            updateRotation(SHOW_STARTING_ANIMATIONS, SHOW_STARTING_ANIMATIONS);
            Intent intent = new Intent("android.intent.action.HDMI_PLUGGED");
            intent.addFlags(67108864);
            intent.putExtra("state", plugged);
            this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        }
    }

    void initializeHdmiState() {
        IOException ex;
        boolean z;
        Throwable th;
        NumberFormatException ex2;
        boolean z2 = SHOW_STARTING_ANIMATIONS;
        boolean plugged = SHOW_PROCESSES_ON_ALT_MENU;
        if (new File("/sys/devices/virtual/switch/hdmi/state").exists()) {
            this.mHDMIObserver.startObserving("DEVPATH=/devices/virtual/switch/hdmi");
            String filename = "/sys/class/switch/hdmi/state";
            FileReader reader = null;
            try {
                FileReader reader2 = new FileReader("/sys/class/switch/hdmi/state");
                try {
                    char[] buf = new char[15];
                    int n = reader2.read(buf);
                    if (n > SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) {
                        if (Integer.parseInt(new String(buf, SHORT_PRESS_SLEEP_GO_TO_SLEEP, n + APPLICATION_MEDIA_OVERLAY_SUBLAYER)) != 0) {
                            plugged = SHOW_STARTING_ANIMATIONS;
                        } else {
                            plugged = SHOW_PROCESSES_ON_ALT_MENU;
                        }
                    }
                    if (reader2 != null) {
                        try {
                            reader2.close();
                        } catch (IOException e) {
                        }
                    }
                } catch (IOException e2) {
                    ex = e2;
                    reader = reader2;
                    try {
                        Slog.w(TAG, "Couldn't read hdmi state from /sys/class/switch/hdmi/state: " + ex);
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e3) {
                            }
                        }
                        if (plugged) {
                            z = SHOW_STARTING_ANIMATIONS;
                        } else {
                            z = SHOW_PROCESSES_ON_ALT_MENU;
                        }
                        this.mHdmiPlugged = z;
                        if (this.mHdmiPlugged) {
                            z2 = SHOW_PROCESSES_ON_ALT_MENU;
                        }
                        setHdmiPlugged(z2);
                    } catch (Throwable th2) {
                        th = th2;
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e4) {
                            }
                        }
                        throw th;
                    }
                } catch (NumberFormatException e5) {
                    ex2 = e5;
                    reader = reader2;
                    Slog.w(TAG, "Couldn't read hdmi state from /sys/class/switch/hdmi/state: " + ex2);
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e6) {
                        }
                    }
                    if (plugged) {
                        z = SHOW_STARTING_ANIMATIONS;
                    } else {
                        z = SHOW_PROCESSES_ON_ALT_MENU;
                    }
                    this.mHdmiPlugged = z;
                    if (this.mHdmiPlugged) {
                        z2 = SHOW_PROCESSES_ON_ALT_MENU;
                    }
                    setHdmiPlugged(z2);
                } catch (Throwable th3) {
                    th = th3;
                    reader = reader2;
                    if (reader != null) {
                        reader.close();
                    }
                    throw th;
                }
            } catch (IOException e7) {
                ex = e7;
                Slog.w(TAG, "Couldn't read hdmi state from /sys/class/switch/hdmi/state: " + ex);
                if (reader != null) {
                    reader.close();
                }
                if (plugged) {
                    z = SHOW_PROCESSES_ON_ALT_MENU;
                } else {
                    z = SHOW_STARTING_ANIMATIONS;
                }
                this.mHdmiPlugged = z;
                if (this.mHdmiPlugged) {
                    z2 = SHOW_PROCESSES_ON_ALT_MENU;
                }
                setHdmiPlugged(z2);
            } catch (NumberFormatException e8) {
                ex2 = e8;
                Slog.w(TAG, "Couldn't read hdmi state from /sys/class/switch/hdmi/state: " + ex2);
                if (reader != null) {
                    reader.close();
                }
                if (plugged) {
                    z = SHOW_STARTING_ANIMATIONS;
                } else {
                    z = SHOW_PROCESSES_ON_ALT_MENU;
                }
                this.mHdmiPlugged = z;
                if (this.mHdmiPlugged) {
                    z2 = SHOW_PROCESSES_ON_ALT_MENU;
                }
                setHdmiPlugged(z2);
            }
        }
        if (plugged) {
            z = SHOW_STARTING_ANIMATIONS;
        } else {
            z = SHOW_PROCESSES_ON_ALT_MENU;
        }
        this.mHdmiPlugged = z;
        if (this.mHdmiPlugged) {
            z2 = SHOW_PROCESSES_ON_ALT_MENU;
        }
        setHdmiPlugged(z2);
    }

    private void takeScreenshot() {
        synchronized (this.mScreenshotLock) {
            if (this.mScreenshotConnection != null) {
                return;
            }
            ComponentName cn = new ComponentName("com.android.systemui", "com.android.systemui.screenshot.TakeScreenshotService");
            Intent intent = new Intent();
            intent.setComponent(cn);
            ServiceConnection conn = new AnonymousClass14(this);
            if (this.mContext.bindServiceAsUser(intent, conn, SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME, UserHandle.CURRENT)) {
                this.mScreenshotConnection = conn;
                this.mHandler.postDelayed(this.mScreenshotTimeout, 10000);
            }
        }
    }

    public int interceptKeyBeforeQueueing(KeyEvent event, int policyFlags) {
        if (!this.mSystemBooted) {
            return SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        }
        int result;
        boolean interactive = (536870912 & policyFlags) != 0 ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
        boolean down = event.getAction() == 0 ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
        boolean canceled = event.isCanceled();
        int keyCode = event.getKeyCode();
        boolean isInjected = (16777216 & policyFlags) != 0 ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
        boolean keyguardActive = this.mKeyguardDelegate == null ? SHOW_PROCESSES_ON_ALT_MENU : interactive ? isKeyguardShowingAndNotOccluded() : this.mKeyguardDelegate.isShowing();
        boolean isWakeKey = ((policyFlags & SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) != 0 || event.isWakeKey()) ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
        if (interactive || (isInjected && !isWakeKey)) {
            result = SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
            isWakeKey = SHOW_PROCESSES_ON_ALT_MENU;
        } else if (interactive || !shouldDispatchInputWhenNonInteractive()) {
            result = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
            if (isWakeKey && !(down && isWakeKeyWhenScreenOff(keyCode))) {
                isWakeKey = SHOW_PROCESSES_ON_ALT_MENU;
            }
        } else {
            result = SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
        }
        if (isValidGlobalKey(keyCode)) {
            if (this.mGlobalKeyManager.shouldHandleGlobalKey(keyCode, event)) {
                if (!isWakeKey) {
                    return result;
                }
                wakeUp(event.getEventTime(), this.mAllowTheaterModeWakeFromKey);
                return result;
            }
        }
        boolean useHapticFeedback = (down && (policyFlags & SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP) != 0 && event.getRepeatCount() == 0) ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
        TelecomManager telecomManager;
        Message msg;
        switch (keyCode) {
            case MSG_KEYGUARD_DRAWN_COMPLETE /*5*/:
                if (down) {
                    telecomManager = getTelecommService();
                    if (telecomManager != null && telecomManager.isRinging()) {
                        Log.i(TAG, "interceptKeyBeforeQueueing: CALL key-down while ringing: Answer the call!");
                        telecomManager.acceptRingingCall();
                        result &= APPLICATION_MEDIA_SUBLAYER;
                        break;
                    }
                }
                break;
            case MSG_KEYGUARD_DRAWN_TIMEOUT /*6*/:
                result &= APPLICATION_MEDIA_SUBLAYER;
                if (!down) {
                    if (!this.mEndCallKeyHandled) {
                        this.mHandler.removeCallbacks(this.mEndCallLongPress);
                        if (!canceled) {
                            if ((this.mEndcallBehavior & SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) == 0 || !goHome()) {
                                if ((this.mEndcallBehavior & SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP) != 0) {
                                    this.mPowerManager.goToSleep(event.getEventTime(), SHORT_PRESS_POWER_GO_HOME, SHORT_PRESS_SLEEP_GO_TO_SLEEP);
                                    isWakeKey = SHOW_PROCESSES_ON_ALT_MENU;
                                    break;
                                }
                            }
                        }
                    }
                }
                telecomManager = getTelecommService();
                boolean hungUp = SHOW_PROCESSES_ON_ALT_MENU;
                if (telecomManager != null) {
                    hungUp = telecomManager.endCall();
                }
                if (interactive && !hungUp) {
                    this.mEndCallKeyHandled = SHOW_PROCESSES_ON_ALT_MENU;
                    this.mHandler.postDelayed(this.mEndCallLongPress, ViewConfiguration.get(this.mContext).getDeviceGlobalActionKeyTimeout());
                    break;
                }
                this.mEndCallKeyHandled = SHOW_STARTING_ANIMATIONS;
                break;
                break;
            case 24:
            case 25:
            case 164:
                if (keyCode == 25) {
                    if (!down) {
                        this.mScreenshotChordVolumeDownKeyTriggered = SHOW_PROCESSES_ON_ALT_MENU;
                        cancelPendingScreenshotChordAction();
                    } else if (interactive && !this.mScreenshotChordVolumeDownKeyTriggered) {
                        if ((event.getFlags() & 1024) == 0) {
                            this.mScreenshotChordVolumeDownKeyTriggered = SHOW_STARTING_ANIMATIONS;
                            this.mScreenshotChordVolumeDownKeyTime = event.getDownTime();
                            this.mScreenshotChordVolumeDownKeyConsumed = SHOW_PROCESSES_ON_ALT_MENU;
                            cancelPendingPowerKeyAction();
                            interceptScreenshotChord();
                        }
                    }
                } else if (keyCode == 24) {
                    if (!down) {
                        this.mScreenshotChordVolumeUpKeyTriggered = SHOW_PROCESSES_ON_ALT_MENU;
                        cancelPendingScreenshotChordAction();
                    } else if (interactive && !this.mScreenshotChordVolumeUpKeyTriggered) {
                        if ((event.getFlags() & 1024) == 0) {
                            this.mScreenshotChordVolumeUpKeyTriggered = SHOW_STARTING_ANIMATIONS;
                            cancelPendingPowerKeyAction();
                            cancelPendingScreenshotChordAction();
                        }
                    }
                }
                if (down) {
                    telecomManager = getTelecommService();
                    if (telecomManager != null) {
                        if (!telecomManager.isRinging()) {
                            if (telecomManager.isInCall() && (result & SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) == 0) {
                                MediaSessionLegacyHelper.getHelper(this.mContext).sendVolumeKeyEvent(event, SHOW_PROCESSES_ON_ALT_MENU);
                                break;
                            }
                        }
                        Log.i(TAG, "interceptKeyBeforeQueueing: VOLUME key-down while ringing: Silence ringer!");
                        telecomManager.silenceRinger();
                        result &= APPLICATION_MEDIA_SUBLAYER;
                        break;
                    }
                    if ((result & SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) == 0) {
                        MediaSessionLegacyHelper.getHelper(this.mContext).sendVolumeKeyEvent(event, SHOW_STARTING_ANIMATIONS);
                        break;
                    }
                }
                break;
            case 26:
                result &= APPLICATION_MEDIA_SUBLAYER;
                isWakeKey = SHOW_PROCESSES_ON_ALT_MENU;
                if (!down) {
                    interceptPowerKeyUp(event, interactive, canceled);
                    break;
                }
                interceptPowerKeyDown(event, interactive);
                break;
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
            case 222:
                if (MediaSessionLegacyHelper.getHelper(this.mContext).isGlobalPriorityActive()) {
                    result &= APPLICATION_MEDIA_SUBLAYER;
                }
                if ((result & SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) == 0) {
                    this.mBroadcastWakeLock.acquire();
                    msg = this.mHandler.obtainMessage(SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME, new KeyEvent(event));
                    msg.setAsynchronous(SHOW_STARTING_ANIMATIONS);
                    msg.sendToTarget();
                    break;
                }
                break;
            case 223:
                result &= APPLICATION_MEDIA_SUBLAYER;
                isWakeKey = SHOW_PROCESSES_ON_ALT_MENU;
                if (!this.mPowerManager.isInteractive()) {
                    useHapticFeedback = SHOW_PROCESSES_ON_ALT_MENU;
                }
                sleepPress(event);
                break;
            case 224:
                result &= APPLICATION_MEDIA_SUBLAYER;
                isWakeKey = SHOW_STARTING_ANIMATIONS;
                break;
            case 231:
                if ((result & SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) == 0 && !down) {
                    this.mBroadcastWakeLock.acquire();
                    msg = this.mHandler.obtainMessage(MSG_LAUNCH_VOICE_ASSIST_WITH_WAKE_LOCK, keyguardActive ? SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME : SHORT_PRESS_SLEEP_GO_TO_SLEEP, SHORT_PRESS_SLEEP_GO_TO_SLEEP);
                    msg.setAsynchronous(SHOW_STARTING_ANIMATIONS);
                    msg.sendToTarget();
                    break;
                }
        }
        if (useHapticFeedback) {
            performHapticFeedbackLw(null, SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME, SHOW_PROCESSES_ON_ALT_MENU);
        }
        if (!isWakeKey) {
            return result;
        }
        wakeUp(event.getEventTime(), this.mAllowTheaterModeWakeFromKey);
        return result;
    }

    private static boolean isValidGlobalKey(int keyCode) {
        switch (keyCode) {
            case 26:
            case 223:
            case 224:
                return SHOW_PROCESSES_ON_ALT_MENU;
            default:
                return SHOW_STARTING_ANIMATIONS;
        }
    }

    private boolean isWakeKeyWhenScreenOff(int keyCode) {
        switch (keyCode) {
            case 24:
            case 25:
            case 164:
                if (this.mDockMode == 0) {
                    return SHOW_PROCESSES_ON_ALT_MENU;
                }
                return SHOW_STARTING_ANIMATIONS;
            case 27:
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
            case 222:
                return SHOW_PROCESSES_ON_ALT_MENU;
            default:
                return SHOW_STARTING_ANIMATIONS;
        }
    }

    public int interceptMotionBeforeQueueingNonInteractive(long whenNanos, int policyFlags) {
        if ((policyFlags & SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) != 0 && wakeUp(whenNanos / 1000000, this.mAllowTheaterModeWakeFromMotion)) {
            return SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        }
        if (shouldDispatchInputWhenNonInteractive()) {
            return SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
        }
        if (!isTheaterModeEnabled() || (policyFlags & SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) == 0) {
            return SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        }
        wakeUp(whenNanos / 1000000, this.mAllowTheaterModeWakeFromMotionWhenNotDreaming);
        return SHORT_PRESS_SLEEP_GO_TO_SLEEP;
    }

    private boolean shouldDispatchInputWhenNonInteractive() {
        if (isKeyguardShowingAndNotOccluded() && this.mDisplay != null && this.mDisplay.getState() != SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) {
            return SHOW_STARTING_ANIMATIONS;
        }
        IDreamManager dreamManager = getDreamManager();
        if (dreamManager != null) {
            try {
                if (dreamManager.isDreaming()) {
                    return SHOW_STARTING_ANIMATIONS;
                }
            } catch (RemoteException e) {
                Slog.e(TAG, "RemoteException when checking if dreaming", e);
            }
        }
        return SHOW_PROCESSES_ON_ALT_MENU;
    }

    void dispatchMediaKeyWithWakeLock(KeyEvent event) {
        if (this.mHavePendingMediaKeyRepeatWithWakeLock) {
            this.mHandler.removeMessages(SHORT_PRESS_POWER_GO_HOME);
            this.mHavePendingMediaKeyRepeatWithWakeLock = SHOW_PROCESSES_ON_ALT_MENU;
            this.mBroadcastWakeLock.release();
        }
        dispatchMediaKeyWithWakeLockToAudioService(event);
        if (event.getAction() == 0 && event.getRepeatCount() == 0) {
            this.mHavePendingMediaKeyRepeatWithWakeLock = SHOW_STARTING_ANIMATIONS;
            Message msg = this.mHandler.obtainMessage(SHORT_PRESS_POWER_GO_HOME, event);
            msg.setAsynchronous(SHOW_STARTING_ANIMATIONS);
            this.mHandler.sendMessageDelayed(msg, (long) ViewConfiguration.getKeyRepeatTimeout());
            return;
        }
        this.mBroadcastWakeLock.release();
    }

    void dispatchMediaKeyRepeatWithWakeLock(KeyEvent event) {
        this.mHavePendingMediaKeyRepeatWithWakeLock = SHOW_PROCESSES_ON_ALT_MENU;
        dispatchMediaKeyWithWakeLockToAudioService(KeyEvent.changeTimeRepeat(event, SystemClock.uptimeMillis(), SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME, event.getFlags() | 128));
        this.mBroadcastWakeLock.release();
    }

    void dispatchMediaKeyWithWakeLockToAudioService(KeyEvent event) {
        if (ActivityManagerNative.isSystemReady()) {
            MediaSessionLegacyHelper.getHelper(this.mContext).sendMediaButtonEvent(event, SHOW_STARTING_ANIMATIONS);
        }
    }

    void launchVoiceAssistWithWakeLock(boolean keyguardActive) {
        Intent voiceIntent = new Intent("android.speech.action.VOICE_SEARCH_HANDS_FREE");
        voiceIntent.putExtra("android.speech.extras.EXTRA_SECURE", keyguardActive);
        startActivityAsUser(voiceIntent, UserHandle.CURRENT_OR_SELF);
        this.mBroadcastWakeLock.release();
    }

    private void requestTransientBars(WindowState swipeTarget) {
        synchronized (this.mWindowManagerFuncs.getWindowManagerLock()) {
            if (isUserSetupComplete()) {
                boolean sb = this.mStatusBarController.checkShowTransientBarLw();
                boolean nb = this.mNavigationBarController.checkShowTransientBarLw();
                if (sb || nb) {
                    WindowState barTarget = sb ? this.mStatusBar : this.mNavigationBar;
                    if ((sb ^ nb) == 0 || barTarget == swipeTarget) {
                        if (sb) {
                            this.mStatusBarController.showTransient();
                        }
                        if (nb) {
                            this.mNavigationBarController.showTransient();
                        }
                        this.mImmersiveModeConfirmation.confirmCurrentPrompt();
                        updateSystemUiVisibilityLw();
                    } else {
                        return;
                    }
                }
                return;
            }
        }
    }

    public void goingToSleep(int why) {
        EventLog.writeEvent(70000, SHORT_PRESS_SLEEP_GO_TO_SLEEP);
        synchronized (this.mLock) {
            this.mAwake = SHOW_PROCESSES_ON_ALT_MENU;
            this.mKeyguardDrawComplete = SHOW_PROCESSES_ON_ALT_MENU;
            updateWakeGestureListenerLp();
            updateOrientationListenerLp();
            updateLockScreenTimeout();
        }
        if (this.mKeyguardDelegate != null) {
            this.mKeyguardDelegate.onScreenTurnedOff(why);
        }
    }

    private void wakeUpFromPowerKey(long eventTime) {
        wakeUp(eventTime, this.mAllowTheaterModeWakeFromPowerKey);
    }

    private boolean wakeUp(long wakeTime, boolean wakeInTheaterMode) {
        if (!wakeInTheaterMode && isTheaterModeEnabled()) {
            return SHOW_PROCESSES_ON_ALT_MENU;
        }
        this.mPowerManager.wakeUp(wakeTime);
        return SHOW_STARTING_ANIMATIONS;
    }

    public void wakingUp() {
        EventLog.writeEvent(70000, SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME);
        synchronized (this.mLock) {
            this.mAwake = SHOW_STARTING_ANIMATIONS;
            this.mKeyguardDrawComplete = SHOW_PROCESSES_ON_ALT_MENU;
            if (this.mKeyguardDelegate != null) {
                this.mHandler.removeMessages(MSG_KEYGUARD_DRAWN_TIMEOUT);
                this.mHandler.sendEmptyMessageDelayed(MSG_KEYGUARD_DRAWN_TIMEOUT, 1000);
            }
            updateWakeGestureListenerLp();
            updateOrientationListenerLp();
            updateLockScreenTimeout();
        }
        if (this.mKeyguardDelegate != null) {
            this.mKeyguardDelegate.onScreenTurnedOn(this.mKeyguardDelegateCallback);
        } else {
            finishKeyguardDrawn();
        }
    }

    private void finishKeyguardDrawn() {
        synchronized (this.mLock) {
            if (!this.mAwake || this.mKeyguardDrawComplete) {
                return;
            }
            this.mKeyguardDrawComplete = SHOW_STARTING_ANIMATIONS;
            if (this.mKeyguardDelegate != null) {
                this.mHandler.removeMessages(MSG_KEYGUARD_DRAWN_TIMEOUT);
            }
            finishScreenTurningOn();
        }
    }

    public void screenTurnedOff() {
        synchronized (this.mLock) {
            this.mScreenOnEarly = SHOW_PROCESSES_ON_ALT_MENU;
            this.mScreenOnFully = SHOW_PROCESSES_ON_ALT_MENU;
            this.mWindowManagerDrawComplete = SHOW_PROCESSES_ON_ALT_MENU;
            this.mScreenOnListener = null;
            updateOrientationListenerLp();
        }
    }

    public void screenTurningOn(ScreenOnListener screenOnListener) {
        synchronized (this.mLock) {
            this.mScreenOnEarly = SHOW_STARTING_ANIMATIONS;
            this.mScreenOnFully = SHOW_PROCESSES_ON_ALT_MENU;
            this.mWindowManagerDrawComplete = SHOW_PROCESSES_ON_ALT_MENU;
            this.mScreenOnListener = screenOnListener;
            updateOrientationListenerLp();
        }
        this.mWindowManagerInternal.waitForAllWindowsDrawn(this.mWindowManagerDrawCallback, 1000);
    }

    private void finishWindowsDrawn() {
        synchronized (this.mLock) {
            if (!this.mScreenOnEarly || this.mWindowManagerDrawComplete) {
                return;
            }
            this.mWindowManagerDrawComplete = SHOW_STARTING_ANIMATIONS;
            finishScreenTurningOn();
        }
    }

    private void finishScreenTurningOn() {
        synchronized (this.mLock) {
            if (this.mScreenOnFully || !this.mScreenOnEarly || !this.mWindowManagerDrawComplete || (this.mAwake && !this.mKeyguardDrawComplete)) {
                return;
            }
            boolean enableScreen;
            ScreenOnListener listener = this.mScreenOnListener;
            this.mScreenOnListener = null;
            this.mScreenOnFully = SHOW_STARTING_ANIMATIONS;
            if (this.mKeyguardDrawnOnce || !this.mAwake) {
                enableScreen = SHOW_PROCESSES_ON_ALT_MENU;
            } else {
                this.mKeyguardDrawnOnce = SHOW_STARTING_ANIMATIONS;
                enableScreen = SHOW_STARTING_ANIMATIONS;
                if (this.mBootMessageNeedsHiding) {
                    this.mBootMessageNeedsHiding = SHOW_PROCESSES_ON_ALT_MENU;
                    hideBootMessages();
                }
            }
            if (listener != null) {
                listener.onScreenOn();
            }
            if (enableScreen) {
                try {
                    this.mWindowManager.enableScreenIfNeeded();
                } catch (RemoteException e) {
                }
            }
        }
    }

    private void handleHideBootMessage() {
        synchronized (this.mLock) {
            if (this.mKeyguardDrawnOnce) {
                if (this.mBootMsgDialog != null) {
                    this.mBootMsgDialog.dismiss();
                    this.mBootMsgDialog = null;
                    return;
                }
                return;
            }
            this.mBootMessageNeedsHiding = SHOW_STARTING_ANIMATIONS;
        }
    }

    public boolean isScreenOn() {
        return this.mScreenOnFully;
    }

    public void enableKeyguard(boolean enabled) {
        if (this.mKeyguardDelegate != null) {
            this.mKeyguardDelegate.setKeyguardEnabled(enabled);
        }
    }

    public void exitKeyguardSecurely(OnKeyguardExitResult callback) {
        if (this.mKeyguardDelegate != null) {
            this.mKeyguardDelegate.verifyUnlock(callback);
        }
    }

    private boolean isKeyguardShowingAndNotOccluded() {
        if (this.mKeyguardDelegate == null || !this.mKeyguardDelegate.isShowing() || this.mKeyguardOccluded) {
            return SHOW_PROCESSES_ON_ALT_MENU;
        }
        return SHOW_STARTING_ANIMATIONS;
    }

    public boolean isKeyguardLocked() {
        return keyguardOn();
    }

    public boolean isKeyguardSecure() {
        if (this.mKeyguardDelegate == null) {
            return SHOW_PROCESSES_ON_ALT_MENU;
        }
        return this.mKeyguardDelegate.isSecure();
    }

    public boolean inKeyguardRestrictedKeyInputMode() {
        if (this.mKeyguardDelegate == null) {
            return SHOW_PROCESSES_ON_ALT_MENU;
        }
        return this.mKeyguardDelegate.isInputRestricted();
    }

    public void dismissKeyguardLw() {
        if (this.mKeyguardDelegate != null && this.mKeyguardDelegate.isShowing()) {
            this.mHandler.post(new AnonymousClass19(this));
        }
    }

    public void notifyActivityDrawnForKeyguardLw() {
        if (this.mKeyguardDelegate != null) {
            this.mHandler.post(new AnonymousClass20(this));
        }
    }

    public boolean isKeyguardDrawnLw() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mKeyguardDrawnOnce;
        }
        return z;
    }

    public void startKeyguardExitAnimation(long startTime, long fadeoutDuration) {
        if (this.mKeyguardDelegate != null) {
            this.mKeyguardDelegate.startKeyguardExitAnimation(startTime, fadeoutDuration);
        }
    }

    void sendCloseSystemWindows() {
        sendCloseSystemWindows(this.mContext, null);
    }

    void sendCloseSystemWindows(String reason) {
        sendCloseSystemWindows(this.mContext, reason);
    }

    static void sendCloseSystemWindows(Context context, String reason) {
        if (ActivityManagerNative.isSystemReady()) {
            try {
                ActivityManagerNative.getDefault().closeSystemDialogs(reason);
            } catch (RemoteException e) {
            }
        }
    }

    public int rotationForOrientationLw(int orientation, int lastRotation) {
        if (this.mForceDefaultOrientation) {
            return SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        }
        synchronized (this.mLock) {
            int preferredRotation;
            int sensorRotation = this.mOrientationListener.getProposedRotation();
            if (sensorRotation < 0) {
                sensorRotation = lastRotation;
            }
            if (this.mLidState == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME && this.mLidOpenRotation >= 0) {
                preferredRotation = this.mLidOpenRotation;
            } else if (this.mDockMode == SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP && (this.mCarDockEnablesAccelerometer || this.mCarDockRotation >= 0)) {
                preferredRotation = this.mCarDockEnablesAccelerometer ? sensorRotation : this.mCarDockRotation;
            } else if ((this.mDockMode == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME || this.mDockMode == SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME || this.mDockMode == SHORT_PRESS_POWER_GO_HOME) && (this.mDeskDockEnablesAccelerometer || this.mDeskDockRotation >= 0)) {
                preferredRotation = this.mDeskDockEnablesAccelerometer ? sensorRotation : this.mDeskDockRotation;
            } else if (this.mHdmiPlugged && this.mDemoHdmiRotationLock) {
                preferredRotation = this.mDemoHdmiRotation;
            } else if (this.mHdmiPlugged && this.mDockMode == 0 && this.mUndockedHdmiRotation >= 0) {
                preferredRotation = this.mUndockedHdmiRotation;
            } else if (this.mDemoRotationLock) {
                preferredRotation = this.mDemoRotation;
            } else if (orientation == MSG_POWER_LONG_PRESS) {
                preferredRotation = lastRotation;
            } else if (!this.mSupportAutoRotation) {
                preferredRotation = APPLICATION_MEDIA_OVERLAY_SUBLAYER;
            } else if ((this.mUserRotationMode == 0 && (orientation == SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP || orientation == APPLICATION_MEDIA_OVERLAY_SUBLAYER || orientation == MSG_HIDE_BOOT_MESSAGE || orientation == MSG_LAUNCH_VOICE_ASSIST_WITH_WAKE_LOCK || orientation == MSG_POWER_DELAYED_PRESS)) || orientation == SHORT_PRESS_POWER_GO_HOME || orientation == MSG_DISPATCH_SHOW_GLOBAL_ACTIONS || orientation == MSG_KEYGUARD_DRAWN_TIMEOUT || orientation == MSG_WINDOW_MANAGER_DRAWN_COMPLETE) {
                if (this.mAllowAllRotations < 0) {
                    int i;
                    if (this.mContext.getResources().getBoolean(17956913)) {
                        i = SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
                    } else {
                        i = SHORT_PRESS_SLEEP_GO_TO_SLEEP;
                    }
                    this.mAllowAllRotations = i;
                }
                if (sensorRotation != SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP || this.mAllowAllRotations == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME || orientation == MSG_DISPATCH_SHOW_GLOBAL_ACTIONS || orientation == MSG_POWER_DELAYED_PRESS) {
                    preferredRotation = sensorRotation;
                } else {
                    preferredRotation = lastRotation;
                }
            } else if (this.mUserRotationMode != SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME || orientation == MSG_KEYGUARD_DRAWN_COMPLETE) {
                preferredRotation = APPLICATION_MEDIA_OVERLAY_SUBLAYER;
            } else {
                preferredRotation = this.mUserRotation;
            }
            switch (orientation) {
                case SHORT_PRESS_SLEEP_GO_TO_SLEEP /*0*/:
                    if (isLandscapeOrSeascape(preferredRotation)) {
                        return preferredRotation;
                    }
                    preferredRotation = this.mLandscapeRotation;
                    return preferredRotation;
                case SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME /*1*/:
                    if (isAnyPortrait(preferredRotation)) {
                        return preferredRotation;
                    }
                    preferredRotation = this.mPortraitRotation;
                    return preferredRotation;
                case MSG_KEYGUARD_DRAWN_TIMEOUT /*6*/:
                case MSG_HIDE_BOOT_MESSAGE /*11*/:
                    if (isLandscapeOrSeascape(preferredRotation)) {
                        return preferredRotation;
                    } else if (isLandscapeOrSeascape(lastRotation)) {
                        return lastRotation;
                    } else {
                        preferredRotation = this.mLandscapeRotation;
                        return preferredRotation;
                    }
                case MSG_WINDOW_MANAGER_DRAWN_COMPLETE /*7*/:
                case MSG_LAUNCH_VOICE_ASSIST_WITH_WAKE_LOCK /*12*/:
                    if (isAnyPortrait(preferredRotation)) {
                        return preferredRotation;
                    } else if (isAnyPortrait(lastRotation)) {
                        return lastRotation;
                    } else {
                        preferredRotation = this.mPortraitRotation;
                        return preferredRotation;
                    }
                case 8:
                    if (isLandscapeOrSeascape(preferredRotation)) {
                        return preferredRotation;
                    }
                    preferredRotation = this.mSeascapeRotation;
                    return preferredRotation;
                case MSG_DISPATCH_SHOW_RECENTS /*9*/:
                    if (isAnyPortrait(preferredRotation)) {
                        return preferredRotation;
                    }
                    preferredRotation = this.mUpsideDownRotation;
                    return preferredRotation;
                default:
                    if (preferredRotation >= 0) {
                        return preferredRotation;
                    }
                    return SHORT_PRESS_SLEEP_GO_TO_SLEEP;
            }
        }
    }

    public boolean rotationHasCompatibleMetricsLw(int orientation, int rotation) {
        switch (orientation) {
            case SHORT_PRESS_SLEEP_GO_TO_SLEEP /*0*/:
            case MSG_KEYGUARD_DRAWN_TIMEOUT /*6*/:
            case 8:
                return isLandscapeOrSeascape(rotation);
            case SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME /*1*/:
            case MSG_WINDOW_MANAGER_DRAWN_COMPLETE /*7*/:
            case MSG_DISPATCH_SHOW_RECENTS /*9*/:
                return isAnyPortrait(rotation);
            default:
                return SHOW_STARTING_ANIMATIONS;
        }
    }

    public void setRotationLw(int rotation) {
        this.mOrientationListener.setCurrentRotation(rotation);
    }

    private boolean isLandscapeOrSeascape(int rotation) {
        return (rotation == this.mLandscapeRotation || rotation == this.mSeascapeRotation) ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
    }

    private boolean isAnyPortrait(int rotation) {
        return (rotation == this.mPortraitRotation || rotation == this.mUpsideDownRotation) ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
    }

    public int getUserRotationMode() {
        return System.getIntForUser(this.mContext.getContentResolver(), "accelerometer_rotation", SHORT_PRESS_SLEEP_GO_TO_SLEEP, APPLICATION_MEDIA_SUBLAYER) != 0 ? SHORT_PRESS_SLEEP_GO_TO_SLEEP : SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME;
    }

    public void setUserRotationMode(int mode, int rot) {
        ContentResolver res = this.mContext.getContentResolver();
        if (mode == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) {
            System.putIntForUser(res, "user_rotation", rot, APPLICATION_MEDIA_SUBLAYER);
            System.putIntForUser(res, "accelerometer_rotation", SHORT_PRESS_SLEEP_GO_TO_SLEEP, APPLICATION_MEDIA_SUBLAYER);
            return;
        }
        System.putIntForUser(res, "accelerometer_rotation", SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME, APPLICATION_MEDIA_SUBLAYER);
    }

    public void setSafeMode(boolean safeMode) {
        this.mSafeMode = safeMode;
        performHapticFeedbackLw(null, safeMode ? 10001 : 10000, SHOW_STARTING_ANIMATIONS);
    }

    static long[] getLongIntArray(Resources r, int resid) {
        int[] ar = r.getIntArray(resid);
        if (ar == null) {
            return null;
        }
        long[] out = new long[ar.length];
        for (int i = SHORT_PRESS_SLEEP_GO_TO_SLEEP; i < ar.length; i += SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) {
            out[i] = (long) ar[i];
        }
        return out;
    }

    public void systemReady() {
        this.mKeyguardDelegate = new KeyguardServiceDelegate(this.mContext);
        this.mKeyguardDelegate.onSystemReady();
        readCameraLensCoverState();
        updateUiMode();
        synchronized (this.mLock) {
            updateOrientationListenerLp();
            this.mSystemReady = SHOW_STARTING_ANIMATIONS;
            this.mHandler.post(new AnonymousClass21(this));
        }
    }

    public void systemBooted() {
        if (this.mKeyguardDelegate != null) {
            this.mKeyguardDelegate.bindService(this.mContext);
            this.mKeyguardDelegate.onBootCompleted();
        }
        synchronized (this.mLock) {
            this.mSystemBooted = SHOW_STARTING_ANIMATIONS;
        }
        wakingUp();
        screenTurningOn(null);
    }

    public void showBootMessage(CharSequence msg, boolean always) {
        this.mHandler.post(new AnonymousClass22(this, msg));
    }

    public void hideBootMessages() {
        this.mHandler.sendEmptyMessage(MSG_HIDE_BOOT_MESSAGE);
    }

    public void userActivity() {
        synchronized (this.mScreenLockTimeout) {
            if (this.mLockScreenTimerActive) {
                this.mHandler.removeCallbacks(this.mScreenLockTimeout);
                this.mHandler.postDelayed(this.mScreenLockTimeout, (long) this.mLockScreenTimeout);
            }
        }
    }

    public void lockNow(Bundle options) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", null);
        this.mHandler.removeCallbacks(this.mScreenLockTimeout);
        if (options != null) {
            this.mScreenLockTimeout.setLockOptions(options);
        }
        this.mHandler.post(this.mScreenLockTimeout);
    }

    private void updateLockScreenTimeout() {
        synchronized (this.mScreenLockTimeout) {
            boolean enable = (this.mAllowLockscreenWhenOn && this.mAwake && this.mKeyguardDelegate != null && this.mKeyguardDelegate.isSecure()) ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
            if (this.mLockScreenTimerActive != enable) {
                if (enable) {
                    this.mHandler.postDelayed(this.mScreenLockTimeout, (long) this.mLockScreenTimeout);
                } else {
                    this.mHandler.removeCallbacks(this.mScreenLockTimeout);
                }
                this.mLockScreenTimerActive = enable;
            }
        }
    }

    public void enableScreenAfterBoot() {
        readLidState();
        applyLidSwitchState();
        updateRotation(SHOW_STARTING_ANIMATIONS);
    }

    private void applyLidSwitchState() {
        if (this.mLidState == 0 && this.mLidControlsSleep) {
            this.mPowerManager.goToSleep(SystemClock.uptimeMillis(), SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME, SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME);
        }
        synchronized (this.mLock) {
            updateWakeGestureListenerLp();
        }
    }

    void updateUiMode() {
        if (this.mUiModeManager == null) {
            this.mUiModeManager = Stub.asInterface(ServiceManager.getService("uimode"));
        }
        try {
            this.mUiMode = this.mUiModeManager.getCurrentModeType();
        } catch (RemoteException e) {
        }
    }

    void updateRotation(boolean alwaysSendConfiguration) {
        try {
            this.mWindowManager.updateRotation(alwaysSendConfiguration, SHOW_PROCESSES_ON_ALT_MENU);
        } catch (RemoteException e) {
        }
    }

    void updateRotation(boolean alwaysSendConfiguration, boolean forceRelayout) {
        try {
            this.mWindowManager.updateRotation(alwaysSendConfiguration, forceRelayout);
        } catch (RemoteException e) {
        }
    }

    Intent createHomeDockIntent() {
        Intent intent;
        if (this.mUiMode == SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME) {
            intent = this.mCarDockIntent;
        } else if (this.mUiMode == SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP) {
            intent = null;
        } else if (this.mUiMode == MSG_KEYGUARD_DRAWN_TIMEOUT && (this.mDockMode == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME || this.mDockMode == SHORT_PRESS_POWER_GO_HOME || this.mDockMode == SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME)) {
            intent = this.mDeskDockIntent;
        } else {
            intent = null;
        }
        if (intent == null) {
            Intent intent2 = intent;
            return null;
        }
        ActivityInfo ai = null;
        ResolveInfo info = this.mContext.getPackageManager().resolveActivityAsUser(intent, 65664, this.mCurrentUserId);
        if (info != null) {
            ai = info.activityInfo;
        }
        if (ai == null || ai.metaData == null || !ai.metaData.getBoolean("android.dock_home")) {
            return null;
        }
        intent2 = new Intent(intent);
        intent2.setClassName(ai.packageName, ai.name);
        return intent2;
    }

    void startDockOrHome(boolean fromHomeKey, boolean awakenFromDreams) {
        Intent intent;
        if (awakenFromDreams) {
            awakenDreams();
        }
        Intent dock = createHomeDockIntent();
        if (dock != null) {
            if (fromHomeKey) {
                try {
                    dock.putExtra("android.intent.extra.FROM_HOME_KEY", fromHomeKey);
                } catch (ActivityNotFoundException e) {
                }
            }
            startActivityAsUser(dock, UserHandle.CURRENT);
            return;
        }
        if (fromHomeKey) {
            intent = new Intent(this.mHomeIntent);
            intent.putExtra("android.intent.extra.FROM_HOME_KEY", fromHomeKey);
        } else {
            intent = this.mHomeIntent;
        }
        startActivityAsUser(intent, UserHandle.CURRENT);
    }

    boolean goHome() {
        if (isUserSetupComplete()) {
            try {
                if (SystemProperties.getInt("persist.sys.uts-test-mode", SHORT_PRESS_SLEEP_GO_TO_SLEEP) == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) {
                    Log.d(TAG, "UTS-TEST-MODE");
                } else {
                    ActivityManagerNative.getDefault().stopAppSwitches();
                    sendCloseSystemWindows();
                    Intent dock = createHomeDockIntent();
                    if (dock != null && ActivityManagerNative.getDefault().startActivityAsUser(null, null, dock, dock.resolveTypeIfNeeded(this.mContext.getContentResolver()), null, null, SHORT_PRESS_SLEEP_GO_TO_SLEEP, SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME, null, null, APPLICATION_MEDIA_SUBLAYER) == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) {
                        return SHOW_PROCESSES_ON_ALT_MENU;
                    }
                }
                if (ActivityManagerNative.getDefault().startActivityAsUser(null, null, this.mHomeIntent, this.mHomeIntent.resolveTypeIfNeeded(this.mContext.getContentResolver()), null, null, SHORT_PRESS_SLEEP_GO_TO_SLEEP, SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME, null, null, APPLICATION_MEDIA_SUBLAYER) == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) {
                    return SHOW_PROCESSES_ON_ALT_MENU;
                }
            } catch (RemoteException e) {
            }
            return SHOW_STARTING_ANIMATIONS;
        }
        Slog.i(TAG, "Not going home because user setup is in progress.");
        return SHOW_PROCESSES_ON_ALT_MENU;
    }

    public void setCurrentOrientationLw(int newOrientation) {
        synchronized (this.mLock) {
            if (newOrientation != this.mCurrentAppOrientation) {
                this.mCurrentAppOrientation = newOrientation;
                updateOrientationListenerLp();
            }
        }
    }

    private void performAuditoryFeedbackForAccessibilityIfNeed() {
        if (isGlobalAccessibilityGestureEnabled() && !((AudioManager) this.mContext.getSystemService("audio")).isSilentMode()) {
            Ringtone ringTone = RingtoneManager.getRingtone(this.mContext, System.DEFAULT_NOTIFICATION_URI);
            ringTone.setStreamType(SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME);
            ringTone.play();
        }
    }

    private boolean isTheaterModeEnabled() {
        return Global.getInt(this.mContext.getContentResolver(), "theater_mode_on", SHORT_PRESS_SLEEP_GO_TO_SLEEP) == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
    }

    private boolean isGlobalAccessibilityGestureEnabled() {
        return Global.getInt(this.mContext.getContentResolver(), "enable_accessibility_global_gesture_enabled", SHORT_PRESS_SLEEP_GO_TO_SLEEP) == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
    }

    public boolean performHapticFeedbackLw(WindowState win, int effectId, boolean always) {
        if (!this.mVibrator.hasVibrator()) {
            return SHOW_PROCESSES_ON_ALT_MENU;
        }
        boolean hapticsDisabled;
        if (System.getIntForUser(this.mContext.getContentResolver(), "haptic_feedback_enabled", SHORT_PRESS_SLEEP_GO_TO_SLEEP, APPLICATION_MEDIA_SUBLAYER) == 0) {
            hapticsDisabled = SHOW_STARTING_ANIMATIONS;
        } else {
            hapticsDisabled = SHOW_PROCESSES_ON_ALT_MENU;
        }
        if (hapticsDisabled && !always) {
            return SHOW_PROCESSES_ON_ALT_MENU;
        }
        long[] pattern;
        int owningUid;
        String owningPackage;
        switch (effectId) {
            case SHORT_PRESS_SLEEP_GO_TO_SLEEP /*0*/:
                pattern = this.mLongPressVibePattern;
                break;
            case SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME /*1*/:
                pattern = this.mVirtualKeyVibePattern;
                break;
            case SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP_AND_GO_HOME /*3*/:
                pattern = this.mKeyboardTapVibePattern;
                break;
            case SHORT_PRESS_POWER_GO_HOME /*4*/:
                pattern = this.mClockTickVibePattern;
                break;
            case MSG_KEYGUARD_DRAWN_COMPLETE /*5*/:
                pattern = this.mCalendarDateVibePattern;
                break;
            case 10000:
                pattern = this.mSafeModeDisabledVibePattern;
                break;
            case 10001:
                pattern = this.mSafeModeEnabledVibePattern;
                break;
            default:
                return SHOW_PROCESSES_ON_ALT_MENU;
        }
        if (win != null) {
            owningUid = win.getOwningUid();
            owningPackage = win.getOwningPackage();
        } else {
            owningUid = Process.myUid();
            owningPackage = this.mContext.getOpPackageName();
        }
        if (pattern.length == SHORT_PRESS_SLEEP_GO_TO_SLEEP_AND_GO_HOME) {
            this.mVibrator.vibrate(owningUid, owningPackage, pattern[SHORT_PRESS_SLEEP_GO_TO_SLEEP], VIBRATION_ATTRIBUTES);
        } else {
            this.mVibrator.vibrate(owningUid, owningPackage, pattern, APPLICATION_MEDIA_OVERLAY_SUBLAYER, VIBRATION_ATTRIBUTES);
        }
        return SHOW_STARTING_ANIMATIONS;
    }

    public void keepScreenOnStartedLw() {
    }

    public void keepScreenOnStoppedLw() {
        if (isKeyguardShowingAndNotOccluded()) {
            this.mPowerManager.userActivity(SystemClock.uptimeMillis(), SHOW_PROCESSES_ON_ALT_MENU);
        }
    }

    private int updateSystemUiVisibilityLw() {
        WindowState win = this.mFocusedWindow != null ? this.mFocusedWindow : this.mTopFullscreenOpaqueWindowState;
        if (win == null) {
            return SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        }
        if ((win.getAttrs().privateFlags & 1024) != 0 && this.mHideLockScreen == SHOW_STARTING_ANIMATIONS) {
            return SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        }
        int tmpVisibility = (PolicyControl.getSystemUiVisibility(win, null) & (this.mResettingSystemUiFlags ^ APPLICATION_MEDIA_OVERLAY_SUBLAYER)) & (this.mForceClearedSystemUiFlags ^ APPLICATION_MEDIA_OVERLAY_SUBLAYER);
        if (this.mForcingShowNavBar && win.getSurfaceLayer() < this.mForcingShowNavBarLayer) {
            tmpVisibility &= PolicyControl.adjustClearableFlags(win, MSG_WINDOW_MANAGER_DRAWN_COMPLETE) ^ APPLICATION_MEDIA_OVERLAY_SUBLAYER;
        }
        int visibility = updateSystemBarsLw(win, this.mLastSystemUiFlags, tmpVisibility);
        int diff = visibility ^ this.mLastSystemUiFlags;
        boolean needsMenu = win.getNeedsMenuLw(this.mTopFullscreenOpaqueWindowState);
        if (diff == 0 && this.mLastFocusNeedsMenu == needsMenu && this.mFocusedApp == win.getAppToken()) {
            return SHORT_PRESS_SLEEP_GO_TO_SLEEP;
        }
        this.mLastSystemUiFlags = visibility;
        this.mLastFocusNeedsMenu = needsMenu;
        this.mFocusedApp = win.getAppToken();
        this.mHandler.post(new AnonymousClass23(this, visibility, win, needsMenu));
        return diff;
    }

    private int updateSystemBarsLw(WindowState win, int oldVis, int vis) {
        boolean hideStatusBarWM;
        boolean transientStatusBarAllowed;
        boolean transientNavBarAllowed;
        boolean oldImmersiveMode;
        boolean newImmersiveMode;
        WindowState transWin = (!isStatusBarKeyguard() || this.mHideLockScreen) ? this.mTopFullscreenOpaqueWindowState : this.mStatusBar;
        vis = this.mNavigationBarController.applyTranslucentFlagLw(transWin, this.mStatusBarController.applyTranslucentFlagLw(transWin, vis, oldVis), oldVis);
        int i = win.getAttrs().type;
        boolean statusBarHasFocus = r0 == 2000 ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
        if (statusBarHasFocus && !isStatusBarKeyguard()) {
            int flags = 6150;
            if (this.mHideLockScreen) {
                flags = 6150 | -1073741824;
            }
            vis = ((flags ^ APPLICATION_MEDIA_OVERLAY_SUBLAYER) & vis) | (oldVis & flags);
        }
        if (!areTranslucentBarsAllowed()) {
            WindowState windowState = this.mStatusBar;
            if (transWin != r0) {
                vis &= 1073709055;
            }
        }
        boolean immersiveSticky = (vis & 4096) != 0 ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
        if (this.mTopFullscreenOpaqueWindowState != null) {
            if ((PolicyControl.getWindowFlags(this.mTopFullscreenOpaqueWindowState, null) & 1024) != 0) {
                hideStatusBarWM = SHOW_STARTING_ANIMATIONS;
                boolean hideStatusBarSysui = (vis & SHORT_PRESS_POWER_GO_HOME) == 0 ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
                boolean hideNavBarSysui = (vis & SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP) == 0 ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
                transientStatusBarAllowed = (this.mStatusBar == null && (hideStatusBarWM || ((hideStatusBarSysui && immersiveSticky) || statusBarHasFocus))) ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
                transientNavBarAllowed = (this.mNavigationBar == null && hideNavBarSysui && immersiveSticky) ? SHOW_STARTING_ANIMATIONS : SHOW_PROCESSES_ON_ALT_MENU;
                boolean denyTransientStatus = (this.mStatusBarController.isTransientShowRequested() || transientStatusBarAllowed || !hideStatusBarSysui) ? SHOW_PROCESSES_ON_ALT_MENU : SHOW_STARTING_ANIMATIONS;
                boolean denyTransientNav = (this.mNavigationBarController.isTransientShowRequested() || transientNavBarAllowed) ? SHOW_PROCESSES_ON_ALT_MENU : SHOW_STARTING_ANIMATIONS;
                if (denyTransientStatus || denyTransientNav) {
                    clearClearableFlagsLw();
                }
                vis = this.mStatusBarController.updateVisibilityLw(transientStatusBarAllowed, oldVis, vis);
                oldImmersiveMode = isImmersiveMode(oldVis);
                newImmersiveMode = isImmersiveMode(vis);
                if (!(win == null || oldImmersiveMode == newImmersiveMode)) {
                    this.mImmersiveModeConfirmation.immersiveModeChanged(win.getOwningPackage(), newImmersiveMode, isUserSetupComplete());
                }
                return this.mNavigationBarController.updateVisibilityLw(transientNavBarAllowed, oldVis, vis);
            }
        }
        hideStatusBarWM = SHOW_PROCESSES_ON_ALT_MENU;
        if ((vis & SHORT_PRESS_POWER_GO_HOME) == 0) {
        }
        if ((vis & SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP) == 0) {
        }
        if (this.mStatusBar == null) {
        }
        if (this.mNavigationBar == null) {
        }
        if (this.mStatusBarController.isTransientShowRequested()) {
        }
        if (this.mNavigationBarController.isTransientShowRequested()) {
        }
        clearClearableFlagsLw();
        vis = this.mStatusBarController.updateVisibilityLw(transientStatusBarAllowed, oldVis, vis);
        oldImmersiveMode = isImmersiveMode(oldVis);
        newImmersiveMode = isImmersiveMode(vis);
        this.mImmersiveModeConfirmation.immersiveModeChanged(win.getOwningPackage(), newImmersiveMode, isUserSetupComplete());
        return this.mNavigationBarController.updateVisibilityLw(transientNavBarAllowed, oldVis, vis);
    }

    private void clearClearableFlagsLw() {
        int newVal = this.mResettingSystemUiFlags | MSG_WINDOW_MANAGER_DRAWN_COMPLETE;
        if (newVal != this.mResettingSystemUiFlags) {
            this.mResettingSystemUiFlags = newVal;
            this.mWindowManagerFuncs.reevaluateStatusBarVisibility();
        }
    }

    private boolean isImmersiveMode(int vis) {
        return (this.mNavigationBar == null || (vis & SHORT_PRESS_POWER_REALLY_GO_TO_SLEEP) == 0 || (vis & 6144) == 0 || !canHideNavigationBar()) ? SHOW_PROCESSES_ON_ALT_MENU : SHOW_STARTING_ANIMATIONS;
    }

    private boolean areTranslucentBarsAllowed() {
        return (!this.mTranslucentDecorEnabled || this.mAccessibilityManager.isTouchExplorationEnabled()) ? SHOW_PROCESSES_ON_ALT_MENU : SHOW_STARTING_ANIMATIONS;
    }

    public boolean hasNavigationBar() {
        return this.mHasNavigationBar;
    }

    public void setLastInputMethodWindowLw(WindowState ime, WindowState target) {
        this.mLastInputMethodWindow = ime;
        this.mLastInputMethodTargetWindow = target;
    }

    public int getInputMethodWindowVisibleHeightLw() {
        return this.mDockBottom - this.mCurBottom;
    }

    public void setCurrentUserLw(int newUserId) {
        this.mCurrentUserId = newUserId;
        if (this.mKeyguardDelegate != null) {
            this.mKeyguardDelegate.setCurrentUser(newUserId);
        }
        if (this.mStatusBarService != null) {
            try {
                this.mStatusBarService.setCurrentUser(newUserId);
            } catch (RemoteException e) {
            }
        }
        setLastInputMethodWindowLw(null, null);
    }

    public boolean canMagnifyWindow(int windowType) {
        switch (windowType) {
            case 2011:
            case 2012:
            case 2019:
            case 2027:
                return SHOW_PROCESSES_ON_ALT_MENU;
            default:
                return SHOW_STARTING_ANIMATIONS;
        }
    }

    public boolean isTopLevelWindow(int windowType) {
        if (windowType < WAITING_FOR_DRAWN_TIMEOUT || windowType > 1999 || windowType == 1003) {
            return SHOW_STARTING_ANIMATIONS;
        }
        return SHOW_PROCESSES_ON_ALT_MENU;
    }

    public void dump(String prefix, PrintWriter pw, String[] args) {
        pw.print(prefix);
        pw.print("mSafeMode=");
        pw.print(this.mSafeMode);
        pw.print(" mSystemReady=");
        pw.print(this.mSystemReady);
        pw.print(" mSystemBooted=");
        pw.println(this.mSystemBooted);
        pw.print(prefix);
        pw.print("mLidState=");
        pw.print(this.mLidState);
        pw.print(" mLidOpenRotation=");
        pw.print(this.mLidOpenRotation);
        pw.print(" mCameraLensCoverState=");
        pw.print(this.mCameraLensCoverState);
        pw.print(" mHdmiPlugged=");
        pw.println(this.mHdmiPlugged);
        if (!(this.mLastSystemUiFlags == 0 && this.mResettingSystemUiFlags == 0 && this.mForceClearedSystemUiFlags == 0)) {
            pw.print(prefix);
            pw.print("mLastSystemUiFlags=0x");
            pw.print(Integer.toHexString(this.mLastSystemUiFlags));
            pw.print(" mResettingSystemUiFlags=0x");
            pw.print(Integer.toHexString(this.mResettingSystemUiFlags));
            pw.print(" mForceClearedSystemUiFlags=0x");
            pw.println(Integer.toHexString(this.mForceClearedSystemUiFlags));
        }
        if (this.mLastFocusNeedsMenu) {
            pw.print(prefix);
            pw.print("mLastFocusNeedsMenu=");
            pw.println(this.mLastFocusNeedsMenu);
        }
        pw.print(prefix);
        pw.print("mWakeGestureEnabledSetting=");
        pw.println(this.mWakeGestureEnabledSetting);
        pw.print(prefix);
        pw.print("mSupportAutoRotation=");
        pw.println(this.mSupportAutoRotation);
        pw.print(prefix);
        pw.print("mUiMode=");
        pw.print(this.mUiMode);
        pw.print(" mDockMode=");
        pw.print(this.mDockMode);
        pw.print(" mCarDockRotation=");
        pw.print(this.mCarDockRotation);
        pw.print(" mDeskDockRotation=");
        pw.println(this.mDeskDockRotation);
        pw.print(prefix);
        pw.print("mUserRotationMode=");
        pw.print(this.mUserRotationMode);
        pw.print(" mUserRotation=");
        pw.print(this.mUserRotation);
        pw.print(" mAllowAllRotations=");
        pw.println(this.mAllowAllRotations);
        pw.print(prefix);
        pw.print("mCurrentAppOrientation=");
        pw.println(this.mCurrentAppOrientation);
        pw.print(prefix);
        pw.print("mCarDockEnablesAccelerometer=");
        pw.print(this.mCarDockEnablesAccelerometer);
        pw.print(" mDeskDockEnablesAccelerometer=");
        pw.println(this.mDeskDockEnablesAccelerometer);
        pw.print(prefix);
        pw.print("mLidKeyboardAccessibility=");
        pw.print(this.mLidKeyboardAccessibility);
        pw.print(" mLidNavigationAccessibility=");
        pw.print(this.mLidNavigationAccessibility);
        pw.print(" mLidControlsSleep=");
        pw.println(this.mLidControlsSleep);
        pw.print(prefix);
        pw.print("mShortPressOnPowerBehavior=");
        pw.print(this.mShortPressOnPowerBehavior);
        pw.print(" mLongPressOnPowerBehavior=");
        pw.println(this.mLongPressOnPowerBehavior);
        pw.print(prefix);
        pw.print("mDoublePressOnPowerBehavior=");
        pw.print(this.mDoublePressOnPowerBehavior);
        pw.print(" mTriplePressOnPowerBehavior=");
        pw.println(this.mTriplePressOnPowerBehavior);
        pw.print(prefix);
        pw.print("mHasSoftInput=");
        pw.println(this.mHasSoftInput);
        pw.print(prefix);
        pw.print("mAwake=");
        pw.println(this.mAwake);
        pw.print(prefix);
        pw.print("mScreenOnEarly=");
        pw.print(this.mScreenOnEarly);
        pw.print(" mScreenOnFully=");
        pw.println(this.mScreenOnFully);
        pw.print(prefix);
        pw.print("mKeyguardDrawComplete=");
        pw.print(this.mKeyguardDrawComplete);
        pw.print(" mWindowManagerDrawComplete=");
        pw.println(this.mWindowManagerDrawComplete);
        pw.print(prefix);
        pw.print("mOrientationSensorEnabled=");
        pw.println(this.mOrientationSensorEnabled);
        pw.print(prefix);
        pw.print("mOverscanScreen=(");
        pw.print(this.mOverscanScreenLeft);
        pw.print(",");
        pw.print(this.mOverscanScreenTop);
        pw.print(") ");
        pw.print(this.mOverscanScreenWidth);
        pw.print("x");
        pw.println(this.mOverscanScreenHeight);
        if (!(this.mOverscanLeft == 0 && this.mOverscanTop == 0 && this.mOverscanRight == 0 && this.mOverscanBottom == 0)) {
            pw.print(prefix);
            pw.print("mOverscan left=");
            pw.print(this.mOverscanLeft);
            pw.print(" top=");
            pw.print(this.mOverscanTop);
            pw.print(" right=");
            pw.print(this.mOverscanRight);
            pw.print(" bottom=");
            pw.println(this.mOverscanBottom);
        }
        pw.print(prefix);
        pw.print("mRestrictedOverscanScreen=(");
        pw.print(this.mRestrictedOverscanScreenLeft);
        pw.print(",");
        pw.print(this.mRestrictedOverscanScreenTop);
        pw.print(") ");
        pw.print(this.mRestrictedOverscanScreenWidth);
        pw.print("x");
        pw.println(this.mRestrictedOverscanScreenHeight);
        pw.print(prefix);
        pw.print("mUnrestrictedScreen=(");
        pw.print(this.mUnrestrictedScreenLeft);
        pw.print(",");
        pw.print(this.mUnrestrictedScreenTop);
        pw.print(") ");
        pw.print(this.mUnrestrictedScreenWidth);
        pw.print("x");
        pw.println(this.mUnrestrictedScreenHeight);
        pw.print(prefix);
        pw.print("mRestrictedScreen=(");
        pw.print(this.mRestrictedScreenLeft);
        pw.print(",");
        pw.print(this.mRestrictedScreenTop);
        pw.print(") ");
        pw.print(this.mRestrictedScreenWidth);
        pw.print("x");
        pw.println(this.mRestrictedScreenHeight);
        pw.print(prefix);
        pw.print("mStableFullscreen=(");
        pw.print(this.mStableFullscreenLeft);
        pw.print(",");
        pw.print(this.mStableFullscreenTop);
        pw.print(")-(");
        pw.print(this.mStableFullscreenRight);
        pw.print(",");
        pw.print(this.mStableFullscreenBottom);
        pw.println(")");
        pw.print(prefix);
        pw.print("mStable=(");
        pw.print(this.mStableLeft);
        pw.print(",");
        pw.print(this.mStableTop);
        pw.print(")-(");
        pw.print(this.mStableRight);
        pw.print(",");
        pw.print(this.mStableBottom);
        pw.println(")");
        pw.print(prefix);
        pw.print("mSystem=(");
        pw.print(this.mSystemLeft);
        pw.print(",");
        pw.print(this.mSystemTop);
        pw.print(")-(");
        pw.print(this.mSystemRight);
        pw.print(",");
        pw.print(this.mSystemBottom);
        pw.println(")");
        pw.print(prefix);
        pw.print("mCur=(");
        pw.print(this.mCurLeft);
        pw.print(",");
        pw.print(this.mCurTop);
        pw.print(")-(");
        pw.print(this.mCurRight);
        pw.print(",");
        pw.print(this.mCurBottom);
        pw.println(")");
        pw.print(prefix);
        pw.print("mContent=(");
        pw.print(this.mContentLeft);
        pw.print(",");
        pw.print(this.mContentTop);
        pw.print(")-(");
        pw.print(this.mContentRight);
        pw.print(",");
        pw.print(this.mContentBottom);
        pw.println(")");
        pw.print(prefix);
        pw.print("mVoiceContent=(");
        pw.print(this.mVoiceContentLeft);
        pw.print(",");
        pw.print(this.mVoiceContentTop);
        pw.print(")-(");
        pw.print(this.mVoiceContentRight);
        pw.print(",");
        pw.print(this.mVoiceContentBottom);
        pw.println(")");
        pw.print(prefix);
        pw.print("mDock=(");
        pw.print(this.mDockLeft);
        pw.print(",");
        pw.print(this.mDockTop);
        pw.print(")-(");
        pw.print(this.mDockRight);
        pw.print(",");
        pw.print(this.mDockBottom);
        pw.println(")");
        pw.print(prefix);
        pw.print("mDockLayer=");
        pw.print(this.mDockLayer);
        pw.print(" mStatusBarLayer=");
        pw.println(this.mStatusBarLayer);
        pw.print(prefix);
        pw.print("mShowingLockscreen=");
        pw.print(this.mShowingLockscreen);
        pw.print(" mShowingDream=");
        pw.print(this.mShowingDream);
        pw.print(" mDreamingLockscreen=");
        pw.println(this.mDreamingLockscreen);
        if (this.mLastInputMethodWindow != null) {
            pw.print(prefix);
            pw.print("mLastInputMethodWindow=");
            pw.println(this.mLastInputMethodWindow);
        }
        if (this.mLastInputMethodTargetWindow != null) {
            pw.print(prefix);
            pw.print("mLastInputMethodTargetWindow=");
            pw.println(this.mLastInputMethodTargetWindow);
        }
        if (this.mStatusBar != null) {
            pw.print(prefix);
            pw.print("mStatusBar=");
            pw.println(this.mStatusBar);
            pw.print(prefix);
            pw.print("isStatusBarKeyguard=");
            pw.print(isStatusBarKeyguard());
        }
        if (this.mNavigationBar != null) {
            pw.print(prefix);
            pw.print("mNavigationBar=");
            pw.println(this.mNavigationBar);
        }
        if (this.mFocusedWindow != null) {
            pw.print(prefix);
            pw.print("mFocusedWindow=");
            pw.println(this.mFocusedWindow);
        }
        if (this.mFocusedApp != null) {
            pw.print(prefix);
            pw.print("mFocusedApp=");
            pw.println(this.mFocusedApp);
        }
        if (this.mWinDismissingKeyguard != null) {
            pw.print(prefix);
            pw.print("mWinDismissingKeyguard=");
            pw.println(this.mWinDismissingKeyguard);
        }
        if (this.mTopFullscreenOpaqueWindowState != null) {
            pw.print(prefix);
            pw.print("mTopFullscreenOpaqueWindowState=");
            pw.println(this.mTopFullscreenOpaqueWindowState);
        }
        if (this.mForcingShowNavBar) {
            pw.print(prefix);
            pw.print("mForcingShowNavBar=");
            pw.println(this.mForcingShowNavBar);
            pw.print("mForcingShowNavBarLayer=");
            pw.println(this.mForcingShowNavBarLayer);
        }
        pw.print(prefix);
        pw.print("mTopIsFullscreen=");
        pw.print(this.mTopIsFullscreen);
        pw.print(" mHideLockScreen=");
        pw.println(this.mHideLockScreen);
        pw.print(prefix);
        pw.print("mForceStatusBar=");
        pw.print(this.mForceStatusBar);
        pw.print(" mForceStatusBarFromKeyguard=");
        pw.println(this.mForceStatusBarFromKeyguard);
        pw.print(prefix);
        pw.print("mDismissKeyguard=");
        pw.print(this.mDismissKeyguard);
        pw.print(" mWinDismissingKeyguard=");
        pw.print(this.mWinDismissingKeyguard);
        pw.print(" mHomePressed=");
        pw.println(this.mHomePressed);
        pw.print(prefix);
        pw.print("mAllowLockscreenWhenOn=");
        pw.print(this.mAllowLockscreenWhenOn);
        pw.print(" mLockScreenTimeout=");
        pw.print(this.mLockScreenTimeout);
        pw.print(" mLockScreenTimerActive=");
        pw.println(this.mLockScreenTimerActive);
        pw.print(prefix);
        pw.print("mEndcallBehavior=");
        pw.print(this.mEndcallBehavior);
        pw.print(" mIncallPowerBehavior=");
        pw.print(this.mIncallPowerBehavior);
        pw.print(" mLongPressOnHomeBehavior=");
        pw.println(this.mLongPressOnHomeBehavior);
        pw.print(prefix);
        pw.print("mLandscapeRotation=");
        pw.print(this.mLandscapeRotation);
        pw.print(" mSeascapeRotation=");
        pw.println(this.mSeascapeRotation);
        pw.print(prefix);
        pw.print("mPortraitRotation=");
        pw.print(this.mPortraitRotation);
        pw.print(" mUpsideDownRotation=");
        pw.println(this.mUpsideDownRotation);
        pw.print(prefix);
        pw.print("mDemoHdmiRotation=");
        pw.print(this.mDemoHdmiRotation);
        pw.print(" mDemoHdmiRotationLock=");
        pw.println(this.mDemoHdmiRotationLock);
        pw.print(prefix);
        pw.print("mUndockedHdmiRotation=");
        pw.println(this.mUndockedHdmiRotation);
        this.mGlobalKeyManager.dump(prefix, pw);
        this.mStatusBarController.dump(pw, prefix);
        this.mNavigationBarController.dump(pw, prefix);
        PolicyControl.dump(prefix, pw);
        if (this.mWakeGestureListener != null) {
            this.mWakeGestureListener.dump(pw, prefix);
        }
        if (this.mOrientationListener != null) {
            this.mOrientationListener.dump(pw, prefix);
        }
        if (this.mBurnInProtectionHelper != null) {
            this.mBurnInProtectionHelper.dump(prefix, pw);
        }
    }
}
