package android.app;

import android.app.IActivityContainerCallback.Stub;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.SurfaceTexture;
import android.os.IBinder;
import android.os.OperationCanceledException;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InputEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import dalvik.system.CloseGuard;
import java.lang.ref.WeakReference;

public class ActivityView extends ViewGroup {
    private static final boolean DEBUG = false;
    private static final String TAG = "ActivityView";
    private Activity mActivity;
    private ActivityContainerWrapper mActivityContainer;
    private ActivityViewCallback mActivityViewCallback;
    private int mHeight;
    private int mLastVisibility;
    DisplayMetrics mMetrics;
    private Surface mSurface;
    private final TextureView mTextureView;
    private int mWidth;

    private static class ActivityContainerCallback extends Stub {
        private final WeakReference<ActivityView> mActivityViewWeakReference;

        /* renamed from: android.app.ActivityView.ActivityContainerCallback.1 */
        class AnonymousClass1 implements Runnable {
            final /* synthetic */ ActivityContainerCallback this$0;
            final /* synthetic */ ActivityView val$activityView;
            final /* synthetic */ WeakReference val$callbackRef;

            AnonymousClass1(android.app.ActivityView.ActivityContainerCallback r1, java.lang.ref.WeakReference r2, android.app.ActivityView r3) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.ActivityView.ActivityContainerCallback.1.<init>(android.app.ActivityView$ActivityContainerCallback, java.lang.ref.WeakReference, android.app.ActivityView):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.ActivityView.ActivityContainerCallback.1.<init>(android.app.ActivityView$ActivityContainerCallback, java.lang.ref.WeakReference, android.app.ActivityView):void
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
                throw new UnsupportedOperationException("Method not decompiled: android.app.ActivityView.ActivityContainerCallback.1.<init>(android.app.ActivityView$ActivityContainerCallback, java.lang.ref.WeakReference, android.app.ActivityView):void");
            }

            public void run() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.app.ActivityView.ActivityContainerCallback.1.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.app.ActivityView.ActivityContainerCallback.1.run():void
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
                throw new UnsupportedOperationException("Method not decompiled: android.app.ActivityView.ActivityContainerCallback.1.run():void");
            }
        }

        ActivityContainerCallback(ActivityView activityView) {
            this.mActivityViewWeakReference = new WeakReference(activityView);
        }

        public void setVisible(IBinder container, boolean visible) {
        }

        public void onAllActivitiesComplete(IBinder container) {
            ActivityView activityView = (ActivityView) this.mActivityViewWeakReference.get();
            if (activityView != null) {
                ActivityViewCallback callback = activityView.mActivityViewCallback;
                if (callback != null) {
                    activityView.post(new AnonymousClass1(this, new WeakReference(callback), activityView));
                }
            }
        }
    }

    private static class ActivityContainerWrapper {
        private final CloseGuard mGuard;
        private final IActivityContainer mIActivityContainer;
        boolean mOpened;

        ActivityContainerWrapper(IActivityContainer container) {
            this.mGuard = CloseGuard.get();
            this.mIActivityContainer = container;
            this.mOpened = true;
            this.mGuard.open("release");
        }

        void attachToDisplay(int displayId) {
            try {
                this.mIActivityContainer.attachToDisplay(displayId);
            } catch (RemoteException e) {
            }
        }

        void setSurface(Surface surface, int width, int height, int density) throws RemoteException {
            this.mIActivityContainer.setSurface(surface, width, height, density);
        }

        int startActivity(Intent intent) {
            try {
                return this.mIActivityContainer.startActivity(intent);
            } catch (RemoteException e) {
                throw new RuntimeException("ActivityView: Unable to startActivity. " + e);
            }
        }

        int startActivityIntentSender(IIntentSender intentSender) {
            try {
                return this.mIActivityContainer.startActivityIntentSender(intentSender);
            } catch (RemoteException e) {
                throw new RuntimeException("ActivityView: Unable to startActivity from IntentSender. " + e);
            }
        }

        int getDisplayId() {
            try {
                return this.mIActivityContainer.getDisplayId();
            } catch (RemoteException e) {
                return -1;
            }
        }

        boolean injectEvent(InputEvent event) {
            try {
                return this.mIActivityContainer.injectEvent(event);
            } catch (RemoteException e) {
                return ActivityView.DEBUG;
            }
        }

        void release() {
            synchronized (this.mGuard) {
                if (this.mOpened) {
                    try {
                        this.mIActivityContainer.release();
                        this.mGuard.close();
                    } catch (RemoteException e) {
                    }
                    this.mOpened = ActivityView.DEBUG;
                }
            }
        }

        protected void finalize() throws Throwable {
            try {
                if (this.mGuard != null) {
                    this.mGuard.warnIfOpen();
                    release();
                }
                super.finalize();
            } catch (Throwable th) {
                super.finalize();
            }
        }
    }

    public static abstract class ActivityViewCallback {
        public abstract void onAllActivitiesComplete(ActivityView activityView);

        public abstract void onSurfaceAvailable(ActivityView activityView);

        public abstract void onSurfaceDestroyed(ActivityView activityView);

        public ActivityViewCallback() {
        }
    }

    private class ActivityViewSurfaceTextureListener implements SurfaceTextureListener {
        final /* synthetic */ ActivityView this$0;

        private ActivityViewSurfaceTextureListener(ActivityView activityView) {
            this.this$0 = activityView;
        }

        /* synthetic */ ActivityViewSurfaceTextureListener(ActivityView x0, AnonymousClass1 x1) {
            this(x0);
        }

        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            if (this.this$0.mActivityContainer != null) {
                this.this$0.mWidth = width;
                this.this$0.mHeight = height;
                this.this$0.attachToSurfaceWhenReady();
                if (this.this$0.mActivityViewCallback != null) {
                    this.this$0.mActivityViewCallback.onSurfaceAvailable(this.this$0);
                }
            }
        }

        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            if (this.this$0.mActivityContainer != null) {
            }
        }

        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            if (this.this$0.mActivityContainer != null) {
                this.this$0.mSurface.release();
                this.this$0.mSurface = null;
                try {
                    this.this$0.mActivityContainer.setSurface(null, this.this$0.mWidth, this.this$0.mHeight, this.this$0.mMetrics.densityDpi);
                    if (this.this$0.mActivityViewCallback != null) {
                        this.this$0.mActivityViewCallback.onSurfaceDestroyed(this.this$0);
                    }
                } catch (RemoteException e) {
                    throw new RuntimeException("ActivityView: Unable to set surface of ActivityContainer. " + e);
                }
            }
            return true;
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }
    }

    public ActivityView(Context context) {
        this(context, null);
    }

    public ActivityView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActivityView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                this.mActivity = (Activity) context;
                break;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        if (this.mActivity == null) {
            throw new IllegalStateException("The ActivityView's Context is not an Activity.");
        }
        try {
            this.mActivityContainer = new ActivityContainerWrapper(ActivityManagerNative.getDefault().createActivityContainer(this.mActivity.getActivityToken(), new ActivityContainerCallback(this)));
            this.mTextureView = new TextureView(context);
            this.mTextureView.setSurfaceTextureListener(new ActivityViewSurfaceTextureListener());
            addView(this.mTextureView);
            WindowManager wm = (WindowManager) this.mActivity.getSystemService(Context.WINDOW_SERVICE);
            this.mMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(this.mMetrics);
            this.mLastVisibility = getVisibility();
        } catch (RemoteException e) {
            throw new RuntimeException("ActivityView: Unable to create ActivityContainer. " + e);
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        this.mTextureView.layout(0, 0, r - l, b - t);
    }

    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (this.mSurface != null) {
            if (visibility == 8) {
                try {
                    this.mActivityContainer.setSurface(null, this.mWidth, this.mHeight, this.mMetrics.densityDpi);
                } catch (RemoteException e) {
                    throw new RuntimeException("ActivityView: Unable to set surface of ActivityContainer. " + e);
                }
            } else if (this.mLastVisibility == 8) {
                this.mActivityContainer.setSurface(this.mSurface, this.mWidth, this.mHeight, this.mMetrics.densityDpi);
            }
        }
        this.mLastVisibility = visibility;
    }

    private boolean injectInputEvent(InputEvent event) {
        return (this.mActivityContainer == null || !this.mActivityContainer.injectEvent(event)) ? DEBUG : true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        return (injectInputEvent(event) || super.onTouchEvent(event)) ? true : DEBUG;
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        if (event.isFromSource(2) && injectInputEvent(event)) {
            return true;
        }
        return super.onGenericMotionEvent(event);
    }

    public void onAttachedToWindow() {
    }

    public void onDetachedFromWindow() {
    }

    public boolean isAttachedToDisplay() {
        return this.mSurface != null ? true : DEBUG;
    }

    public void startActivity(Intent intent) {
        if (this.mActivityContainer == null) {
            throw new IllegalStateException("Attempt to call startActivity after release");
        } else if (this.mSurface == null) {
            throw new IllegalStateException("Surface not yet created.");
        } else if (this.mActivityContainer.startActivity(intent) == -6) {
            throw new OperationCanceledException();
        }
    }

    public void startActivity(IntentSender intentSender) {
        if (this.mActivityContainer == null) {
            throw new IllegalStateException("Attempt to call startActivity after release");
        } else if (this.mSurface == null) {
            throw new IllegalStateException("Surface not yet created.");
        } else {
            if (this.mActivityContainer.startActivityIntentSender(intentSender.getTarget()) == -6) {
                throw new OperationCanceledException();
            }
        }
    }

    public void startActivity(PendingIntent pendingIntent) {
        if (this.mActivityContainer == null) {
            throw new IllegalStateException("Attempt to call startActivity after release");
        } else if (this.mSurface == null) {
            throw new IllegalStateException("Surface not yet created.");
        } else {
            if (this.mActivityContainer.startActivityIntentSender(pendingIntent.getTarget()) == -6) {
                throw new OperationCanceledException();
            }
        }
    }

    public void release() {
        if (this.mActivityContainer == null) {
            Log.e(TAG, "Duplicate call to release");
            return;
        }
        this.mActivityContainer.release();
        this.mActivityContainer = null;
        if (this.mSurface != null) {
            this.mSurface.release();
            this.mSurface = null;
        }
        this.mTextureView.setSurfaceTextureListener(null);
    }

    private void attachToSurfaceWhenReady() {
        SurfaceTexture surfaceTexture = this.mTextureView.getSurfaceTexture();
        if (surfaceTexture != null && this.mSurface == null) {
            this.mSurface = new Surface(surfaceTexture);
            try {
                this.mActivityContainer.setSurface(this.mSurface, this.mWidth, this.mHeight, this.mMetrics.densityDpi);
            } catch (RemoteException e) {
                this.mSurface.release();
                this.mSurface = null;
                throw new RuntimeException("ActivityView: Unable to create ActivityContainer. " + e);
            }
        }
    }

    public void setCallback(ActivityViewCallback callback) {
        this.mActivityViewCallback = callback;
        if (this.mSurface != null) {
            this.mActivityViewCallback.onSurfaceAvailable(this);
        }
    }
}
