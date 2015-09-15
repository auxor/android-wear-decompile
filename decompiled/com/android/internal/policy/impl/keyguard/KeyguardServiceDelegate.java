package com.android.internal.policy.impl.keyguard;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.UserHandle;
import android.util.Log;
import android.util.Slog;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerPolicy.OnKeyguardExitResult;
import com.android.internal.policy.IKeyguardExitCallback.Stub;
import com.android.internal.policy.IKeyguardService;
import com.android.internal.policy.IKeyguardShowCallback;

public class KeyguardServiceDelegate {
    private static final boolean DEBUG = true;
    private static final String TAG = "KeyguardServiceDelegate";
    private final Context mContext;
    private final ServiceConnection mKeyguardConnection;
    protected KeyguardServiceWrapper mKeyguardService;
    private final KeyguardState mKeyguardState;
    private final View mScrim;
    private ShowListener mShowListenerWhenConnect;

    public interface ShowListener {
        void onShown(IBinder iBinder);
    }

    /* renamed from: com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.2 */
    class AnonymousClass2 implements Runnable {
        final /* synthetic */ KeyguardServiceDelegate this$0;

        AnonymousClass2(com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.2.<init>(com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.2.<init>(com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.2.<init>(com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.2.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.2.run():void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.2.run():void");
        }
    }

    /* renamed from: com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.3 */
    class AnonymousClass3 implements Runnable {
        final /* synthetic */ KeyguardServiceDelegate this$0;

        AnonymousClass3(com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.3.<init>(com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.3.<init>(com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.3.<init>(com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.3.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.3.run():void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.3.run():void");
        }
    }

    private final class KeyguardExitDelegate extends Stub {
        private OnKeyguardExitResult mOnKeyguardExitResult;
        final /* synthetic */ KeyguardServiceDelegate this$0;

        KeyguardExitDelegate(com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate r1, android.view.WindowManagerPolicy.OnKeyguardExitResult r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.KeyguardExitDelegate.<init>(com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate, android.view.WindowManagerPolicy$OnKeyguardExitResult):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.KeyguardExitDelegate.<init>(com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate, android.view.WindowManagerPolicy$OnKeyguardExitResult):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.KeyguardExitDelegate.<init>(com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate, android.view.WindowManagerPolicy$OnKeyguardExitResult):void");
        }

        public void onKeyguardExitResult(boolean r1) throws android.os.RemoteException {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.KeyguardExitDelegate.onKeyguardExitResult(boolean):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.KeyguardExitDelegate.onKeyguardExitResult(boolean):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.KeyguardExitDelegate.onKeyguardExitResult(boolean):void");
        }
    }

    private final class KeyguardShowDelegate extends IKeyguardShowCallback.Stub {
        private ShowListener mShowListener;
        final /* synthetic */ KeyguardServiceDelegate this$0;

        KeyguardShowDelegate(com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate r1, com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.ShowListener r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.KeyguardShowDelegate.<init>(com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate, com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate$ShowListener):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.KeyguardShowDelegate.<init>(com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate, com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate$ShowListener):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.KeyguardShowDelegate.<init>(com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate, com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate$ShowListener):void");
        }

        public void onShown(android.os.IBinder r1) throws android.os.RemoteException {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.KeyguardShowDelegate.onShown(android.os.IBinder):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.KeyguardShowDelegate.onShown(android.os.IBinder):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.keyguard.KeyguardServiceDelegate.KeyguardShowDelegate.onShown(android.os.IBinder):void");
        }
    }

    static final class KeyguardState {
        public boolean bootCompleted;
        public int currentUser;
        boolean deviceHasKeyguard;
        public boolean dismissable;
        boolean dreaming;
        public boolean enabled;
        boolean inputRestricted;
        boolean occluded;
        public int offReason;
        public boolean screenIsOn;
        boolean secure;
        boolean showing;
        boolean showingAndNotOccluded;
        boolean systemIsReady;

        KeyguardState() {
            this.showing = KeyguardServiceDelegate.DEBUG;
            this.showingAndNotOccluded = KeyguardServiceDelegate.DEBUG;
            this.secure = KeyguardServiceDelegate.DEBUG;
            this.deviceHasKeyguard = KeyguardServiceDelegate.DEBUG;
        }
    }

    public KeyguardServiceDelegate(Context context) {
        this.mKeyguardState = new KeyguardState();
        this.mKeyguardConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.v(KeyguardServiceDelegate.TAG, "*** Keyguard connected (yay!)");
                KeyguardServiceDelegate.this.mKeyguardService = new KeyguardServiceWrapper(KeyguardServiceDelegate.this.mContext, IKeyguardService.Stub.asInterface(service));
                if (KeyguardServiceDelegate.this.mKeyguardState.systemIsReady) {
                    KeyguardServiceDelegate.this.mKeyguardService.onSystemReady();
                    KeyguardServiceDelegate.this.mKeyguardService.onScreenTurnedOn(new KeyguardShowDelegate(KeyguardServiceDelegate.this, KeyguardServiceDelegate.this.mShowListenerWhenConnect));
                    KeyguardServiceDelegate.this.mShowListenerWhenConnect = null;
                }
                if (KeyguardServiceDelegate.this.mKeyguardState.bootCompleted) {
                    KeyguardServiceDelegate.this.mKeyguardService.onBootCompleted();
                }
            }

            public void onServiceDisconnected(ComponentName name) {
                Log.v(KeyguardServiceDelegate.TAG, "*** Keyguard disconnected (boo!)");
                KeyguardServiceDelegate.this.mKeyguardService = null;
            }
        };
        this.mContext = context;
        this.mScrim = createScrim(context);
    }

    public void bindService(Context context) {
        Intent intent = new Intent();
        ComponentName keyguardComponent = ComponentName.unflattenFromString(context.getApplicationContext().getResources().getString(17039434));
        intent.setComponent(keyguardComponent);
        if (context.bindServiceAsUser(intent, this.mKeyguardConnection, 1, UserHandle.OWNER)) {
            Log.v(TAG, "*** Keyguard started");
            return;
        }
        Log.v(TAG, "*** Keyguard: can't bind to " + keyguardComponent);
        this.mKeyguardState.showing = false;
        this.mKeyguardState.showingAndNotOccluded = false;
        this.mKeyguardState.secure = false;
        this.mKeyguardState.deviceHasKeyguard = false;
        hideScrim();
    }

    public boolean isShowing() {
        if (this.mKeyguardService != null) {
            this.mKeyguardState.showing = this.mKeyguardService.isShowing();
        }
        return this.mKeyguardState.showing;
    }

    public boolean isInputRestricted() {
        if (this.mKeyguardService != null) {
            this.mKeyguardState.inputRestricted = this.mKeyguardService.isInputRestricted();
        }
        return this.mKeyguardState.inputRestricted;
    }

    public void verifyUnlock(OnKeyguardExitResult onKeyguardExitResult) {
        if (this.mKeyguardService != null) {
            this.mKeyguardService.verifyUnlock(new KeyguardExitDelegate(this, onKeyguardExitResult));
        }
    }

    public void keyguardDone(boolean authenticated, boolean wakeup) {
        if (this.mKeyguardService != null) {
            this.mKeyguardService.keyguardDone(authenticated, wakeup);
        }
    }

    public void setOccluded(boolean isOccluded) {
        if (this.mKeyguardService != null) {
            this.mKeyguardService.setOccluded(isOccluded);
        }
        this.mKeyguardState.occluded = isOccluded;
    }

    public void dismiss() {
        if (this.mKeyguardService != null) {
            this.mKeyguardService.dismiss();
        }
    }

    public boolean isSecure() {
        if (this.mKeyguardService != null) {
            this.mKeyguardState.secure = this.mKeyguardService.isSecure();
        }
        return this.mKeyguardState.secure;
    }

    public void onDreamingStarted() {
        if (this.mKeyguardService != null) {
            this.mKeyguardService.onDreamingStarted();
        }
        this.mKeyguardState.dreaming = DEBUG;
    }

    public void onDreamingStopped() {
        if (this.mKeyguardService != null) {
            this.mKeyguardService.onDreamingStopped();
        }
        this.mKeyguardState.dreaming = false;
    }

    public void onScreenTurnedOn(ShowListener showListener) {
        if (this.mKeyguardService != null) {
            Log.v(TAG, "onScreenTurnedOn(showListener = " + showListener + ")");
            this.mKeyguardService.onScreenTurnedOn(new KeyguardShowDelegate(this, showListener));
        } else {
            Slog.w(TAG, "onScreenTurnedOn(): no keyguard service!");
            this.mShowListenerWhenConnect = showListener;
            showScrim();
        }
        this.mKeyguardState.screenIsOn = DEBUG;
    }

    public void onScreenTurnedOff(int why) {
        if (this.mKeyguardService != null) {
            this.mKeyguardService.onScreenTurnedOff(why);
        }
        this.mKeyguardState.offReason = why;
        this.mKeyguardState.screenIsOn = false;
    }

    public void setKeyguardEnabled(boolean enabled) {
        if (this.mKeyguardService != null) {
            this.mKeyguardService.setKeyguardEnabled(enabled);
        }
        this.mKeyguardState.enabled = enabled;
    }

    public void onSystemReady() {
        if (this.mKeyguardService != null) {
            this.mKeyguardService.onSystemReady();
        } else {
            this.mKeyguardState.systemIsReady = DEBUG;
        }
    }

    public void doKeyguardTimeout(Bundle options) {
        if (this.mKeyguardService != null) {
            this.mKeyguardService.doKeyguardTimeout(options);
        }
    }

    public void setCurrentUser(int newUserId) {
        if (this.mKeyguardService != null) {
            this.mKeyguardService.setCurrentUser(newUserId);
        }
        this.mKeyguardState.currentUser = newUserId;
    }

    public void startKeyguardExitAnimation(long startTime, long fadeoutDuration) {
        if (this.mKeyguardService != null) {
            this.mKeyguardService.startKeyguardExitAnimation(startTime, fadeoutDuration);
        }
    }

    private static final View createScrim(Context context) {
        View view = new View(context);
        LayoutParams lp = new LayoutParams(-1, -1, 2029, 1116416, -3);
        lp.softInputMode = 16;
        lp.screenOrientation = 5;
        lp.privateFlags |= 1;
        lp.setTitle("KeyguardScrim");
        ((WindowManager) context.getSystemService("window")).addView(view, lp);
        view.setSystemUiVisibility(56688640);
        return view;
    }

    public void showScrim() {
        if (this.mKeyguardState.deviceHasKeyguard) {
            this.mScrim.post(new AnonymousClass2(this));
        }
    }

    public void hideScrim() {
        this.mScrim.post(new AnonymousClass3(this));
    }

    public void onBootCompleted() {
        if (this.mKeyguardService != null) {
            this.mKeyguardService.onBootCompleted();
        }
        this.mKeyguardState.bootCompleted = DEBUG;
    }

    public void onActivityDrawn() {
        if (this.mKeyguardService != null) {
            this.mKeyguardService.onActivityDrawn();
        }
    }
}
