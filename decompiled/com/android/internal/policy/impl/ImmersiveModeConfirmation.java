package com.android.internal.policy.impl;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.util.Slog;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

public class ImmersiveModeConfirmation {
    private static final String CONFIRMED = "confirmed";
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_SHOW_EVERY_TIME = false;
    private static final String TAG = "ImmersiveModeConfirmation";
    private ClingWindowView mClingWindow;
    private final Runnable mConfirm;
    private boolean mConfirmed;
    private final Context mContext;
    private int mCurrentUserId;
    private final H mHandler;
    private final long mPanicThresholdMs;
    private long mPanicTime;
    private final long mShowDelayMs;
    private final SparseBooleanArray mUserPanicResets;
    private WindowManager mWindowManager;

    private class ClingWindowView extends FrameLayout {
        private static final int BGCOLOR = Integer.MIN_VALUE;
        private static final int OFFSET_DP = 48;
        private ViewGroup mClingLayout;
        private final ColorDrawable mColor;
        private ValueAnimator mColorAnim;
        private final Runnable mConfirm;
        private BroadcastReceiver mReceiver;
        private Runnable mUpdateLayoutRunnable;
        final /* synthetic */ ImmersiveModeConfirmation this$0;

        /* renamed from: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.1 */
        class AnonymousClass1 implements Runnable {
            final /* synthetic */ ClingWindowView this$1;

            AnonymousClass1(com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.1.<init>(com.android.internal.policy.impl.ImmersiveModeConfirmation$ClingWindowView):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.1.<init>(com.android.internal.policy.impl.ImmersiveModeConfirmation$ClingWindowView):void
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
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.1.<init>(com.android.internal.policy.impl.ImmersiveModeConfirmation$ClingWindowView):void");
            }

            public void run() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.1.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.1.run():void
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
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.1.run():void");
            }
        }

        /* renamed from: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.2 */
        class AnonymousClass2 extends BroadcastReceiver {
            final /* synthetic */ ClingWindowView this$1;

            AnonymousClass2(com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.2.<init>(com.android.internal.policy.impl.ImmersiveModeConfirmation$ClingWindowView):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.2.<init>(com.android.internal.policy.impl.ImmersiveModeConfirmation$ClingWindowView):void
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
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.2.<init>(com.android.internal.policy.impl.ImmersiveModeConfirmation$ClingWindowView):void");
            }

            public void onReceive(android.content.Context r1, android.content.Intent r2) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.2.onReceive(android.content.Context, android.content.Intent):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.2.onReceive(android.content.Context, android.content.Intent):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.2.onReceive(android.content.Context, android.content.Intent):void");
            }
        }

        /* renamed from: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.3 */
        class AnonymousClass3 implements OnClickListener {
            final /* synthetic */ ClingWindowView this$1;

            AnonymousClass3(com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.3.<init>(com.android.internal.policy.impl.ImmersiveModeConfirmation$ClingWindowView):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.3.<init>(com.android.internal.policy.impl.ImmersiveModeConfirmation$ClingWindowView):void
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
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.3.<init>(com.android.internal.policy.impl.ImmersiveModeConfirmation$ClingWindowView):void");
            }

            public void onClick(android.view.View r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.3.onClick(android.view.View):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.3.onClick(android.view.View):void
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
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.3.onClick(android.view.View):void");
            }
        }

        /* renamed from: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.4 */
        class AnonymousClass4 implements AnimatorUpdateListener {
            final /* synthetic */ ClingWindowView this$1;

            AnonymousClass4(com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.4.<init>(com.android.internal.policy.impl.ImmersiveModeConfirmation$ClingWindowView):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.4.<init>(com.android.internal.policy.impl.ImmersiveModeConfirmation$ClingWindowView):void
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
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.4.<init>(com.android.internal.policy.impl.ImmersiveModeConfirmation$ClingWindowView):void");
            }

            public void onAnimationUpdate(android.animation.ValueAnimator r1) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.4.onAnimationUpdate(android.animation.ValueAnimator):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.4.onAnimationUpdate(android.animation.ValueAnimator):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.4.onAnimationUpdate(android.animation.ValueAnimator):void");
            }
        }

        public ClingWindowView(com.android.internal.policy.impl.ImmersiveModeConfirmation r1, android.content.Context r2, java.lang.Runnable r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.<init>(com.android.internal.policy.impl.ImmersiveModeConfirmation, android.content.Context, java.lang.Runnable):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.<init>(com.android.internal.policy.impl.ImmersiveModeConfirmation, android.content.Context, java.lang.Runnable):void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.<init>(com.android.internal.policy.impl.ImmersiveModeConfirmation, android.content.Context, java.lang.Runnable):void");
        }

        static /* synthetic */ android.view.ViewGroup access$100(com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.access$100(com.android.internal.policy.impl.ImmersiveModeConfirmation$ClingWindowView):android.view.ViewGroup
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.access$100(com.android.internal.policy.impl.ImmersiveModeConfirmation$ClingWindowView):android.view.ViewGroup
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.access$100(com.android.internal.policy.impl.ImmersiveModeConfirmation$ClingWindowView):android.view.ViewGroup");
        }

        static /* synthetic */ java.lang.Runnable access$200(com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.access$200(com.android.internal.policy.impl.ImmersiveModeConfirmation$ClingWindowView):java.lang.Runnable
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.access$200(com.android.internal.policy.impl.ImmersiveModeConfirmation$ClingWindowView):java.lang.Runnable
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.access$200(com.android.internal.policy.impl.ImmersiveModeConfirmation$ClingWindowView):java.lang.Runnable");
        }

        static /* synthetic */ java.lang.Runnable access$400(com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.access$400(com.android.internal.policy.impl.ImmersiveModeConfirmation$ClingWindowView):java.lang.Runnable
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.access$400(com.android.internal.policy.impl.ImmersiveModeConfirmation$ClingWindowView):java.lang.Runnable
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.access$400(com.android.internal.policy.impl.ImmersiveModeConfirmation$ClingWindowView):java.lang.Runnable");
        }

        static /* synthetic */ android.graphics.drawable.ColorDrawable access$500(com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.access$500(com.android.internal.policy.impl.ImmersiveModeConfirmation$ClingWindowView):android.graphics.drawable.ColorDrawable
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.access$500(com.android.internal.policy.impl.ImmersiveModeConfirmation$ClingWindowView):android.graphics.drawable.ColorDrawable
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.access$500(com.android.internal.policy.impl.ImmersiveModeConfirmation$ClingWindowView):android.graphics.drawable.ColorDrawable");
        }

        public void onAttachedToWindow() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.onAttachedToWindow():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.onAttachedToWindow():void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.onAttachedToWindow():void");
        }

        public void onDetachedFromWindow() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.onDetachedFromWindow():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.onDetachedFromWindow():void
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.internal.policy.impl.ImmersiveModeConfirmation.ClingWindowView.onDetachedFromWindow():void");
        }

        public boolean onTouchEvent(MotionEvent motion) {
            return true;
        }
    }

    private final class H extends Handler {
        private static final int HIDE = 2;
        private static final int PANIC = 3;
        private static final int SHOW = 1;
        final /* synthetic */ ImmersiveModeConfirmation this$0;

        private H(ImmersiveModeConfirmation immersiveModeConfirmation) {
            this.this$0 = immersiveModeConfirmation;
        }

        /* synthetic */ H(ImmersiveModeConfirmation x0, AnonymousClass1 x1) {
            this(x0);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW /*1*/:
                    this.this$0.handleShow();
                case HIDE /*2*/:
                    this.this$0.handleHide();
                case PANIC /*3*/:
                    this.this$0.handlePanic();
                default:
            }
        }
    }

    public ImmersiveModeConfirmation(Context context) {
        this.mUserPanicResets = new SparseBooleanArray();
        this.mConfirm = new Runnable() {
            public void run() {
                if (!ImmersiveModeConfirmation.this.mConfirmed) {
                    ImmersiveModeConfirmation.this.mConfirmed = true;
                    ImmersiveModeConfirmation.this.saveSetting();
                }
                ImmersiveModeConfirmation.this.handleHide();
            }
        };
        this.mContext = context;
        this.mHandler = new H();
        this.mShowDelayMs = getNavBarExitDuration() * 3;
        this.mPanicThresholdMs = (long) context.getResources().getInteger(17694845);
        this.mWindowManager = (WindowManager) this.mContext.getSystemService("window");
    }

    private long getNavBarExitDuration() {
        Animation exit = AnimationUtils.loadAnimation(this.mContext, 17432599);
        return exit != null ? exit.getDuration() : 0;
    }

    public void loadSetting(int currentUserId) {
        this.mConfirmed = DEBUG_SHOW_EVERY_TIME;
        this.mCurrentUserId = currentUserId;
        String value = null;
        try {
            value = Secure.getStringForUser(this.mContext.getContentResolver(), "immersive_mode_confirmations", -2);
            this.mConfirmed = CONFIRMED.equals(value);
        } catch (Throwable t) {
            Slog.w(TAG, "Error loading confirmations, value=" + value, t);
        }
    }

    private void saveSetting() {
        try {
            Secure.putStringForUser(this.mContext.getContentResolver(), "immersive_mode_confirmations", this.mConfirmed ? CONFIRMED : null, -2);
        } catch (Throwable t) {
            Slog.w(TAG, "Error saving confirmations, mConfirmed=" + this.mConfirmed, t);
        }
    }

    public void immersiveModeChanged(String pkg, boolean isImmersiveMode, boolean userSetupComplete) {
        this.mHandler.removeMessages(1);
        if (!isImmersiveMode) {
            this.mHandler.sendEmptyMessage(2);
        } else if (!PolicyControl.disableImmersiveConfirmation(pkg) && !this.mConfirmed && userSetupComplete) {
            this.mHandler.sendEmptyMessageDelayed(1, this.mShowDelayMs);
        }
    }

    public boolean onPowerKeyDown(boolean isScreenOn, long time, boolean inImmersiveMode) {
        if (!isScreenOn && time - this.mPanicTime < this.mPanicThresholdMs) {
            this.mHandler.sendEmptyMessage(3);
            if (this.mClingWindow == null) {
                return true;
            }
            return DEBUG_SHOW_EVERY_TIME;
        } else if (isScreenOn && inImmersiveMode) {
            this.mPanicTime = time;
            return DEBUG_SHOW_EVERY_TIME;
        } else {
            this.mPanicTime = 0;
            return DEBUG_SHOW_EVERY_TIME;
        }
    }

    public void confirmCurrentPrompt() {
        if (this.mClingWindow != null) {
            this.mHandler.post(this.mConfirm);
        }
    }

    private void handlePanic() {
        if (!this.mUserPanicResets.get(this.mCurrentUserId, DEBUG_SHOW_EVERY_TIME)) {
            this.mUserPanicResets.put(this.mCurrentUserId, true);
            this.mConfirmed = DEBUG_SHOW_EVERY_TIME;
            saveSetting();
        }
    }

    private void handleHide() {
        if (this.mClingWindow != null) {
            this.mWindowManager.removeView(this.mClingWindow);
            this.mClingWindow = null;
        }
    }

    public LayoutParams getClingWindowLayoutParams() {
        LayoutParams lp = new LayoutParams(-1, -1, 2005, 16777480, -3);
        lp.privateFlags |= 16;
        lp.setTitle(TAG);
        lp.windowAnimations = 16974566;
        lp.gravity = 119;
        return lp;
    }

    public FrameLayout.LayoutParams getBubbleLayoutParams() {
        return new FrameLayout.LayoutParams(this.mContext.getResources().getDimensionPixelSize(17105054), -2, 49);
    }

    private void handleShow() {
        this.mClingWindow = new ClingWindowView(this, this.mContext, this.mConfirm);
        this.mClingWindow.setSystemUiVisibility(768);
        this.mWindowManager.addView(this.mClingWindow, getClingWindowLayoutParams());
    }
}
