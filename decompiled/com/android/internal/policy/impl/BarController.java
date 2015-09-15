package com.android.internal.policy.impl;

import android.app.StatusBarManager;
import android.os.Handler;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.view.WindowManagerPolicy.WindowState;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.IStatusBarService.Stub;
import java.io.PrintWriter;

public class BarController {
    private static final boolean DEBUG = false;
    private static final int TRANSIENT_BAR_HIDING = 3;
    private static final int TRANSIENT_BAR_NONE = 0;
    private static final int TRANSIENT_BAR_SHOWING = 2;
    private static final int TRANSIENT_BAR_SHOW_REQUESTED = 1;
    private static final int TRANSLUCENT_ANIMATION_DELAY_MS = 1000;
    private final Handler mHandler;
    private long mLastTranslucent;
    private boolean mPendingShow;
    private final Object mServiceAquireLock;
    private int mState;
    private final int mStatusBarManagerId;
    private IStatusBarService mStatusBarService;
    private final String mTag;
    private int mTransientBarState;
    private final int mTransientFlag;
    private final int mTranslucentFlag;
    private final int mTranslucentWmFlag;
    private final int mUnhideFlag;
    private WindowState mWin;

    /* renamed from: com.android.internal.policy.impl.BarController.1 */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ BarController this$0;
        final /* synthetic */ int val$state;

        AnonymousClass1(com.android.internal.policy.impl.BarController r1, int r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.BarController.1.<init>(com.android.internal.policy.impl.BarController, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.BarController.1.<init>(com.android.internal.policy.impl.BarController, int):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.BarController.1.<init>(com.android.internal.policy.impl.BarController, int):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.BarController.1.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.BarController.1.run():void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.BarController.1.run():void");
        }
    }

    public BarController(String tag, int transientFlag, int unhideFlag, int translucentFlag, int statusBarManagerId, int translucentWmFlag) {
        this.mServiceAquireLock = new Object();
        this.mState = TRANSIENT_BAR_NONE;
        this.mTag = "BarController." + tag;
        this.mTransientFlag = transientFlag;
        this.mUnhideFlag = unhideFlag;
        this.mTranslucentFlag = translucentFlag;
        this.mStatusBarManagerId = statusBarManagerId;
        this.mTranslucentWmFlag = translucentWmFlag;
        this.mHandler = new Handler();
    }

    public void setWindow(WindowState win) {
        this.mWin = win;
    }

    public void showTransient() {
        if (this.mWin != null) {
            setTransientBarState(TRANSIENT_BAR_SHOW_REQUESTED);
        }
    }

    public boolean isTransientShowing() {
        return this.mTransientBarState == TRANSIENT_BAR_SHOWING ? true : DEBUG;
    }

    public boolean isTransientShowRequested() {
        return this.mTransientBarState == TRANSIENT_BAR_SHOW_REQUESTED ? true : DEBUG;
    }

    public boolean wasRecentlyTranslucent() {
        return SystemClock.uptimeMillis() - this.mLastTranslucent < 1000 ? true : DEBUG;
    }

    public void adjustSystemUiVisibilityLw(int oldVis, int vis) {
        if (this.mWin != null && this.mTransientBarState == TRANSIENT_BAR_SHOWING && (this.mTransientFlag & vis) == 0) {
            setTransientBarState(TRANSIENT_BAR_HIDING);
            setBarShowingLw(DEBUG);
        } else if (this.mWin != null && (this.mUnhideFlag & oldVis) != 0 && (this.mUnhideFlag & vis) == 0) {
            setBarShowingLw(true);
        }
    }

    public int applyTranslucentFlagLw(WindowState win, int vis, int oldVis) {
        if (this.mWin == null) {
            return vis;
        }
        if (win == null || (win.getAttrs().privateFlags & 512) != 0) {
            return ((((this.mTranslucentFlag ^ -1) & vis) | (this.mTranslucentFlag & oldVis)) & -32769) | (oldVis & 32768);
        }
        int fl = PolicyControl.getWindowFlags(win, null);
        if ((this.mTranslucentWmFlag & fl) != 0) {
            vis |= this.mTranslucentFlag;
        } else {
            vis &= this.mTranslucentFlag ^ -1;
        }
        if ((Integer.MIN_VALUE & fl) != 0) {
            return vis | 32768;
        }
        return vis & -32769;
    }

    public boolean setBarShowingLw(boolean show) {
        if (this.mWin == null) {
            return DEBUG;
        }
        if (show && this.mTransientBarState == TRANSIENT_BAR_HIDING) {
            this.mPendingShow = true;
            return DEBUG;
        }
        boolean wasVis = this.mWin.isVisibleLw();
        boolean wasAnim = this.mWin.isAnimatingLw();
        boolean change = show ? this.mWin.showLw(true) : this.mWin.hideLw(true);
        boolean stateChanged = updateStateLw(computeStateLw(wasVis, wasAnim, this.mWin, change));
        if (change || stateChanged) {
            return true;
        }
        return DEBUG;
    }

    private int computeStateLw(boolean wasVis, boolean wasAnim, WindowState win, boolean change) {
        if (win.hasDrawnLw()) {
            boolean vis = win.isVisibleLw();
            boolean anim = win.isAnimatingLw();
            if (this.mState == TRANSIENT_BAR_SHOW_REQUESTED && !change && !vis) {
                return TRANSIENT_BAR_SHOWING;
            }
            if (this.mState == TRANSIENT_BAR_SHOWING && vis) {
                return TRANSIENT_BAR_NONE;
            }
            if (change) {
                if (wasVis && vis && !wasAnim && anim) {
                    return TRANSIENT_BAR_SHOW_REQUESTED;
                }
                return TRANSIENT_BAR_NONE;
            }
        }
        return this.mState;
    }

    private boolean updateStateLw(int state) {
        if (state == this.mState) {
            return DEBUG;
        }
        this.mState = state;
        this.mHandler.post(new AnonymousClass1(this, state));
        return true;
    }

    public boolean checkHiddenLw() {
        if (this.mWin != null && this.mWin.hasDrawnLw()) {
            if (!(this.mWin.isVisibleLw() || this.mWin.isAnimatingLw())) {
                updateStateLw(TRANSIENT_BAR_SHOWING);
            }
            if (this.mTransientBarState == TRANSIENT_BAR_HIDING && !this.mWin.isVisibleLw()) {
                setTransientBarState(TRANSIENT_BAR_NONE);
                if (!this.mPendingShow) {
                    return true;
                }
                setBarShowingLw(true);
                this.mPendingShow = DEBUG;
                return true;
            }
        }
        return DEBUG;
    }

    public boolean checkShowTransientBarLw() {
        if (this.mTransientBarState == TRANSIENT_BAR_SHOWING || this.mTransientBarState == TRANSIENT_BAR_SHOW_REQUESTED || this.mWin == null || this.mWin.isDisplayedLw()) {
            return DEBUG;
        }
        return true;
    }

    public int updateVisibilityLw(boolean transientAllowed, int oldVis, int vis) {
        if (this.mWin == null) {
            return vis;
        }
        if (isTransientShowing() || isTransientShowRequested()) {
            if (transientAllowed) {
                vis |= this.mTransientFlag;
                if ((this.mTransientFlag & oldVis) == 0) {
                    vis |= this.mUnhideFlag;
                }
                setTransientBarState(TRANSIENT_BAR_SHOWING);
            } else {
                setTransientBarState(TRANSIENT_BAR_NONE);
            }
        }
        if (this.mTransientBarState != 0) {
            vis = (vis | this.mTransientFlag) & -2;
        }
        if (!((this.mTranslucentFlag & vis) == 0 && (this.mTranslucentFlag & oldVis) == 0 && ((vis | oldVis) & 4) == 0)) {
            this.mLastTranslucent = SystemClock.uptimeMillis();
        }
        return vis;
    }

    private void setTransientBarState(int state) {
        if (this.mWin != null && state != this.mTransientBarState) {
            if (this.mTransientBarState == TRANSIENT_BAR_SHOWING || state == TRANSIENT_BAR_SHOWING) {
                this.mLastTranslucent = SystemClock.uptimeMillis();
            }
            this.mTransientBarState = state;
        }
    }

    private IStatusBarService getStatusBarService() {
        IStatusBarService iStatusBarService;
        synchronized (this.mServiceAquireLock) {
            if (this.mStatusBarService == null) {
                this.mStatusBarService = Stub.asInterface(ServiceManager.getService("statusbar"));
            }
            iStatusBarService = this.mStatusBarService;
        }
        return iStatusBarService;
    }

    private static String transientBarStateToString(int state) {
        if (state == TRANSIENT_BAR_HIDING) {
            return "TRANSIENT_BAR_HIDING";
        }
        if (state == TRANSIENT_BAR_SHOWING) {
            return "TRANSIENT_BAR_SHOWING";
        }
        if (state == TRANSIENT_BAR_SHOW_REQUESTED) {
            return "TRANSIENT_BAR_SHOW_REQUESTED";
        }
        if (state == 0) {
            return "TRANSIENT_BAR_NONE";
        }
        throw new IllegalArgumentException("Unknown state " + state);
    }

    public void dump(PrintWriter pw, String prefix) {
        if (this.mWin != null) {
            pw.print(prefix);
            pw.println(this.mTag);
            pw.print(prefix);
            pw.print("  ");
            pw.print("mState");
            pw.print('=');
            pw.println(StatusBarManager.windowStateToString(this.mState));
            pw.print(prefix);
            pw.print("  ");
            pw.print("mTransientBar");
            pw.print('=');
            pw.println(transientBarStateToString(this.mTransientBarState));
        }
    }
}
