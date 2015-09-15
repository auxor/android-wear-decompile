package android.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class GestureDetector {
    private static final int DOUBLE_TAP_MIN_TIME;
    private static final int DOUBLE_TAP_TIMEOUT;
    private static final int LONGPRESS_TIMEOUT;
    private static final int LONG_PRESS = 2;
    private static final int SHOW_PRESS = 1;
    private static final int TAP = 3;
    private static final int TAP_TIMEOUT;
    private boolean mAlwaysInBiggerTapRegion;
    private boolean mAlwaysInTapRegion;
    private MotionEvent mCurrentDownEvent;
    private boolean mDeferConfirmSingleTap;
    private OnDoubleTapListener mDoubleTapListener;
    private int mDoubleTapSlopSquare;
    private int mDoubleTapTouchSlopSquare;
    private float mDownFocusX;
    private float mDownFocusY;
    private final Handler mHandler;
    private boolean mInLongPress;
    private final InputEventConsistencyVerifier mInputEventConsistencyVerifier;
    private boolean mIsDoubleTapping;
    private boolean mIsLongpressEnabled;
    private float mLastFocusX;
    private float mLastFocusY;
    private final OnGestureListener mListener;
    private int mMaximumFlingVelocity;
    private int mMinimumFlingVelocity;
    private MotionEvent mPreviousUpEvent;
    private boolean mStillDown;
    private int mTouchSlopSquare;
    private VelocityTracker mVelocityTracker;

    public interface OnGestureListener {
        boolean onDown(MotionEvent motionEvent);

        boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2);

        void onLongPress(MotionEvent motionEvent);

        boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2);

        void onShowPress(MotionEvent motionEvent);

        boolean onSingleTapUp(MotionEvent motionEvent);
    }

    public interface OnDoubleTapListener {
        boolean onDoubleTap(MotionEvent motionEvent);

        boolean onDoubleTapEvent(MotionEvent motionEvent);

        boolean onSingleTapConfirmed(MotionEvent motionEvent);
    }

    public static class SimpleOnGestureListener implements OnGestureListener, OnDoubleTapListener {
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        public void onLongPress(MotionEvent e) {
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        public void onShowPress(MotionEvent e) {
        }

        public boolean onDown(MotionEvent e) {
            return false;
        }

        public boolean onDoubleTap(MotionEvent e) {
            return false;
        }

        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }

        public boolean onSingleTapConfirmed(MotionEvent e) {
            return false;
        }
    }

    private class GestureHandler extends Handler {
        GestureHandler() {
        }

        GestureHandler(Handler handler) {
            super(handler.getLooper());
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GestureDetector.SHOW_PRESS /*1*/:
                    GestureDetector.this.mListener.onShowPress(GestureDetector.this.mCurrentDownEvent);
                case GestureDetector.LONG_PRESS /*2*/:
                    GestureDetector.this.dispatchLongPress();
                case GestureDetector.TAP /*3*/:
                    if (GestureDetector.this.mDoubleTapListener == null) {
                        return;
                    }
                    if (GestureDetector.this.mStillDown) {
                        GestureDetector.this.mDeferConfirmSingleTap = true;
                    } else {
                        GestureDetector.this.mDoubleTapListener.onSingleTapConfirmed(GestureDetector.this.mCurrentDownEvent);
                    }
                default:
                    throw new RuntimeException("Unknown message " + msg);
            }
        }
    }

    static {
        LONGPRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();
        TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
        DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();
        DOUBLE_TAP_MIN_TIME = ViewConfiguration.getDoubleTapMinTime();
    }

    @Deprecated
    public GestureDetector(OnGestureListener listener, Handler handler) {
        this(null, listener, handler);
    }

    @Deprecated
    public GestureDetector(OnGestureListener listener) {
        this(null, listener, null);
    }

    public GestureDetector(Context context, OnGestureListener listener) {
        this(context, listener, null);
    }

    public GestureDetector(Context context, OnGestureListener listener, Handler handler) {
        InputEventConsistencyVerifier inputEventConsistencyVerifier;
        if (InputEventConsistencyVerifier.isInstrumentationEnabled()) {
            inputEventConsistencyVerifier = new InputEventConsistencyVerifier(this, LONGPRESS_TIMEOUT);
        } else {
            inputEventConsistencyVerifier = null;
        }
        this.mInputEventConsistencyVerifier = inputEventConsistencyVerifier;
        if (handler != null) {
            this.mHandler = new GestureHandler(handler);
        } else {
            this.mHandler = new GestureHandler();
        }
        this.mListener = listener;
        if (listener instanceof OnDoubleTapListener) {
            setOnDoubleTapListener((OnDoubleTapListener) listener);
        }
        init(context);
    }

    public GestureDetector(Context context, OnGestureListener listener, Handler handler, boolean unused) {
        this(context, listener, handler);
    }

    private void init(Context context) {
        if (this.mListener == null) {
            throw new NullPointerException("OnGestureListener must not be null");
        }
        int touchSlop;
        int doubleTapTouchSlop;
        int doubleTapSlop;
        this.mIsLongpressEnabled = true;
        if (context == null) {
            touchSlop = ViewConfiguration.getTouchSlop();
            doubleTapTouchSlop = touchSlop;
            doubleTapSlop = ViewConfiguration.getDoubleTapSlop();
            this.mMinimumFlingVelocity = ViewConfiguration.getMinimumFlingVelocity();
            this.mMaximumFlingVelocity = ViewConfiguration.getMaximumFlingVelocity();
        } else {
            ViewConfiguration configuration = ViewConfiguration.get(context);
            touchSlop = configuration.getScaledTouchSlop();
            doubleTapTouchSlop = configuration.getScaledDoubleTapTouchSlop();
            doubleTapSlop = configuration.getScaledDoubleTapSlop();
            this.mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
            this.mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();
        }
        this.mTouchSlopSquare = touchSlop * touchSlop;
        this.mDoubleTapTouchSlopSquare = doubleTapTouchSlop * doubleTapTouchSlop;
        this.mDoubleTapSlopSquare = doubleTapSlop * doubleTapSlop;
    }

    public void setOnDoubleTapListener(OnDoubleTapListener onDoubleTapListener) {
        this.mDoubleTapListener = onDoubleTapListener;
    }

    public void setIsLongpressEnabled(boolean isLongpressEnabled) {
        this.mIsLongpressEnabled = isLongpressEnabled;
    }

    public boolean isLongpressEnabled() {
        return this.mIsLongpressEnabled;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r43) {
        /*
        r42 = this;
        r0 = r42;
        r0 = r0.mInputEventConsistencyVerifier;
        r36 = r0;
        if (r36 == 0) goto L_0x0019;
    L_0x0008:
        r0 = r42;
        r0 = r0.mInputEventConsistencyVerifier;
        r36 = r0;
        r37 = 0;
        r0 = r36;
        r1 = r43;
        r2 = r37;
        r0.onTouchEvent(r1, r2);
    L_0x0019:
        r6 = r43.getAction();
        r0 = r42;
        r0 = r0.mVelocityTracker;
        r36 = r0;
        if (r36 != 0) goto L_0x002f;
    L_0x0025:
        r36 = android.view.VelocityTracker.obtain();
        r0 = r36;
        r1 = r42;
        r1.mVelocityTracker = r0;
    L_0x002f:
        r0 = r42;
        r0 = r0.mVelocityTracker;
        r36 = r0;
        r0 = r36;
        r1 = r43;
        r0.addMovement(r1);
        r0 = r6 & 255;
        r36 = r0;
        r37 = 6;
        r0 = r36;
        r1 = r37;
        if (r0 != r1) goto L_0x0067;
    L_0x0048:
        r22 = 1;
    L_0x004a:
        if (r22 == 0) goto L_0x006a;
    L_0x004c:
        r25 = r43.getActionIndex();
    L_0x0050:
        r26 = 0;
        r27 = 0;
        r7 = r43.getPointerCount();
        r18 = 0;
    L_0x005a:
        r0 = r18;
        if (r0 >= r7) goto L_0x0082;
    L_0x005e:
        r0 = r25;
        r1 = r18;
        if (r0 != r1) goto L_0x006d;
    L_0x0064:
        r18 = r18 + 1;
        goto L_0x005a;
    L_0x0067:
        r22 = 0;
        goto L_0x004a;
    L_0x006a:
        r25 = -1;
        goto L_0x0050;
    L_0x006d:
        r0 = r43;
        r1 = r18;
        r36 = r0.getX(r1);
        r26 = r26 + r36;
        r0 = r43;
        r1 = r18;
        r36 = r0.getY(r1);
        r27 = r27 + r36;
        goto L_0x0064;
    L_0x0082:
        if (r22 == 0) goto L_0x00b5;
    L_0x0084:
        r12 = r7 + -1;
    L_0x0086:
        r0 = (float) r12;
        r36 = r0;
        r14 = r26 / r36;
        r0 = (float) r12;
        r36 = r0;
        r15 = r27 / r36;
        r17 = 0;
        r0 = r6 & 255;
        r36 = r0;
        switch(r36) {
            case 0: goto L_0x0163;
            case 1: goto L_0x03a2;
            case 2: goto L_0x02ab;
            case 3: goto L_0x04e5;
            case 4: goto L_0x0099;
            case 5: goto L_0x00b7;
            case 6: goto L_0x00cb;
            default: goto L_0x0099;
        };
    L_0x0099:
        if (r17 != 0) goto L_0x00b4;
    L_0x009b:
        r0 = r42;
        r0 = r0.mInputEventConsistencyVerifier;
        r36 = r0;
        if (r36 == 0) goto L_0x00b4;
    L_0x00a3:
        r0 = r42;
        r0 = r0.mInputEventConsistencyVerifier;
        r36 = r0;
        r37 = 0;
        r0 = r36;
        r1 = r43;
        r2 = r37;
        r0.onUnhandledEvent(r1, r2);
    L_0x00b4:
        return r17;
    L_0x00b5:
        r12 = r7;
        goto L_0x0086;
    L_0x00b7:
        r0 = r42;
        r0.mLastFocusX = r14;
        r0 = r42;
        r0.mDownFocusX = r14;
        r0 = r42;
        r0.mLastFocusY = r15;
        r0 = r42;
        r0.mDownFocusY = r15;
        r42.cancelTaps();
        goto L_0x0099;
    L_0x00cb:
        r0 = r42;
        r0.mLastFocusX = r14;
        r0 = r42;
        r0.mDownFocusX = r14;
        r0 = r42;
        r0.mLastFocusY = r15;
        r0 = r42;
        r0.mDownFocusY = r15;
        r0 = r42;
        r0 = r0.mVelocityTracker;
        r36 = r0;
        r37 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r0 = r42;
        r0 = r0.mMaximumFlingVelocity;
        r38 = r0;
        r0 = r38;
        r0 = (float) r0;
        r38 = r0;
        r36.computeCurrentVelocity(r37, r38);
        r28 = r43.getActionIndex();
        r0 = r43;
        r1 = r28;
        r19 = r0.getPointerId(r1);
        r0 = r42;
        r0 = r0.mVelocityTracker;
        r36 = r0;
        r0 = r36;
        r1 = r19;
        r33 = r0.getXVelocity(r1);
        r0 = r42;
        r0 = r0.mVelocityTracker;
        r36 = r0;
        r0 = r36;
        r1 = r19;
        r35 = r0.getYVelocity(r1);
        r18 = 0;
    L_0x011b:
        r0 = r18;
        if (r0 >= r7) goto L_0x0099;
    L_0x011f:
        r0 = r18;
        r1 = r28;
        if (r0 != r1) goto L_0x0128;
    L_0x0125:
        r18 = r18 + 1;
        goto L_0x011b;
    L_0x0128:
        r0 = r43;
        r1 = r18;
        r20 = r0.getPointerId(r1);
        r0 = r42;
        r0 = r0.mVelocityTracker;
        r36 = r0;
        r0 = r36;
        r1 = r20;
        r36 = r0.getXVelocity(r1);
        r32 = r33 * r36;
        r0 = r42;
        r0 = r0.mVelocityTracker;
        r36 = r0;
        r0 = r36;
        r1 = r20;
        r36 = r0.getYVelocity(r1);
        r34 = r35 * r36;
        r13 = r32 + r34;
        r36 = 0;
        r36 = (r13 > r36 ? 1 : (r13 == r36 ? 0 : -1));
        if (r36 >= 0) goto L_0x0125;
    L_0x0158:
        r0 = r42;
        r0 = r0.mVelocityTracker;
        r36 = r0;
        r36.clear();
        goto L_0x0099;
    L_0x0163:
        r0 = r42;
        r0 = r0.mDoubleTapListener;
        r36 = r0;
        if (r36 == 0) goto L_0x01da;
    L_0x016b:
        r0 = r42;
        r0 = r0.mHandler;
        r36 = r0;
        r37 = 3;
        r16 = r36.hasMessages(r37);
        if (r16 == 0) goto L_0x0184;
    L_0x0179:
        r0 = r42;
        r0 = r0.mHandler;
        r36 = r0;
        r37 = 3;
        r36.removeMessages(r37);
    L_0x0184:
        r0 = r42;
        r0 = r0.mCurrentDownEvent;
        r36 = r0;
        if (r36 == 0) goto L_0x0297;
    L_0x018c:
        r0 = r42;
        r0 = r0.mPreviousUpEvent;
        r36 = r0;
        if (r36 == 0) goto L_0x0297;
    L_0x0194:
        if (r16 == 0) goto L_0x0297;
    L_0x0196:
        r0 = r42;
        r0 = r0.mCurrentDownEvent;
        r36 = r0;
        r0 = r42;
        r0 = r0.mPreviousUpEvent;
        r37 = r0;
        r0 = r42;
        r1 = r36;
        r2 = r37;
        r3 = r43;
        r36 = r0.isConsideredDoubleTap(r1, r2, r3);
        if (r36 == 0) goto L_0x0297;
    L_0x01b0:
        r36 = 1;
        r0 = r36;
        r1 = r42;
        r1.mIsDoubleTapping = r0;
        r0 = r42;
        r0 = r0.mDoubleTapListener;
        r36 = r0;
        r0 = r42;
        r0 = r0.mCurrentDownEvent;
        r37 = r0;
        r36 = r36.onDoubleTap(r37);
        r17 = r17 | r36;
        r0 = r42;
        r0 = r0.mDoubleTapListener;
        r36 = r0;
        r0 = r36;
        r1 = r43;
        r36 = r0.onDoubleTapEvent(r1);
        r17 = r17 | r36;
    L_0x01da:
        r0 = r42;
        r0.mLastFocusX = r14;
        r0 = r42;
        r0.mDownFocusX = r14;
        r0 = r42;
        r0.mLastFocusY = r15;
        r0 = r42;
        r0.mDownFocusY = r15;
        r0 = r42;
        r0 = r0.mCurrentDownEvent;
        r36 = r0;
        if (r36 == 0) goto L_0x01fb;
    L_0x01f2:
        r0 = r42;
        r0 = r0.mCurrentDownEvent;
        r36 = r0;
        r36.recycle();
    L_0x01fb:
        r36 = android.view.MotionEvent.obtain(r43);
        r0 = r36;
        r1 = r42;
        r1.mCurrentDownEvent = r0;
        r36 = 1;
        r0 = r36;
        r1 = r42;
        r1.mAlwaysInTapRegion = r0;
        r36 = 1;
        r0 = r36;
        r1 = r42;
        r1.mAlwaysInBiggerTapRegion = r0;
        r36 = 1;
        r0 = r36;
        r1 = r42;
        r1.mStillDown = r0;
        r36 = 0;
        r0 = r36;
        r1 = r42;
        r1.mInLongPress = r0;
        r36 = 0;
        r0 = r36;
        r1 = r42;
        r1.mDeferConfirmSingleTap = r0;
        r0 = r42;
        r0 = r0.mIsLongpressEnabled;
        r36 = r0;
        if (r36 == 0) goto L_0x0267;
    L_0x0235:
        r0 = r42;
        r0 = r0.mHandler;
        r36 = r0;
        r37 = 2;
        r36.removeMessages(r37);
        r0 = r42;
        r0 = r0.mHandler;
        r36 = r0;
        r37 = 2;
        r0 = r42;
        r0 = r0.mCurrentDownEvent;
        r38 = r0;
        r38 = r38.getDownTime();
        r40 = TAP_TIMEOUT;
        r0 = r40;
        r0 = (long) r0;
        r40 = r0;
        r38 = r38 + r40;
        r40 = LONGPRESS_TIMEOUT;
        r0 = r40;
        r0 = (long) r0;
        r40 = r0;
        r38 = r38 + r40;
        r36.sendEmptyMessageAtTime(r37, r38);
    L_0x0267:
        r0 = r42;
        r0 = r0.mHandler;
        r36 = r0;
        r37 = 1;
        r0 = r42;
        r0 = r0.mCurrentDownEvent;
        r38 = r0;
        r38 = r38.getDownTime();
        r40 = TAP_TIMEOUT;
        r0 = r40;
        r0 = (long) r0;
        r40 = r0;
        r38 = r38 + r40;
        r36.sendEmptyMessageAtTime(r37, r38);
        r0 = r42;
        r0 = r0.mListener;
        r36 = r0;
        r0 = r36;
        r1 = r43;
        r36 = r0.onDown(r1);
        r17 = r17 | r36;
        goto L_0x0099;
    L_0x0297:
        r0 = r42;
        r0 = r0.mHandler;
        r36 = r0;
        r37 = 3;
        r38 = DOUBLE_TAP_TIMEOUT;
        r0 = r38;
        r0 = (long) r0;
        r38 = r0;
        r36.sendEmptyMessageDelayed(r37, r38);
        goto L_0x01da;
    L_0x02ab:
        r0 = r42;
        r0 = r0.mInLongPress;
        r36 = r0;
        if (r36 != 0) goto L_0x0099;
    L_0x02b3:
        r0 = r42;
        r0 = r0.mLastFocusX;
        r36 = r0;
        r23 = r36 - r14;
        r0 = r42;
        r0 = r0.mLastFocusY;
        r36 = r0;
        r24 = r36 - r15;
        r0 = r42;
        r0 = r0.mIsDoubleTapping;
        r36 = r0;
        if (r36 == 0) goto L_0x02dd;
    L_0x02cb:
        r0 = r42;
        r0 = r0.mDoubleTapListener;
        r36 = r0;
        r0 = r36;
        r1 = r43;
        r36 = r0.onDoubleTapEvent(r1);
        r17 = r17 | r36;
        goto L_0x0099;
    L_0x02dd:
        r0 = r42;
        r0 = r0.mAlwaysInTapRegion;
        r36 = r0;
        if (r36 == 0) goto L_0x036a;
    L_0x02e5:
        r0 = r42;
        r0 = r0.mDownFocusX;
        r36 = r0;
        r36 = r14 - r36;
        r0 = r36;
        r9 = (int) r0;
        r0 = r42;
        r0 = r0.mDownFocusY;
        r36 = r0;
        r36 = r15 - r36;
        r0 = r36;
        r10 = (int) r0;
        r36 = r9 * r9;
        r37 = r10 * r10;
        r11 = r36 + r37;
        r0 = r42;
        r0 = r0.mTouchSlopSquare;
        r36 = r0;
        r0 = r36;
        if (r11 <= r0) goto L_0x0356;
    L_0x030b:
        r0 = r42;
        r0 = r0.mListener;
        r36 = r0;
        r0 = r42;
        r0 = r0.mCurrentDownEvent;
        r37 = r0;
        r0 = r36;
        r1 = r37;
        r2 = r43;
        r3 = r23;
        r4 = r24;
        r17 = r0.onScroll(r1, r2, r3, r4);
        r0 = r42;
        r0.mLastFocusX = r14;
        r0 = r42;
        r0.mLastFocusY = r15;
        r36 = 0;
        r0 = r36;
        r1 = r42;
        r1.mAlwaysInTapRegion = r0;
        r0 = r42;
        r0 = r0.mHandler;
        r36 = r0;
        r37 = 3;
        r36.removeMessages(r37);
        r0 = r42;
        r0 = r0.mHandler;
        r36 = r0;
        r37 = 1;
        r36.removeMessages(r37);
        r0 = r42;
        r0 = r0.mHandler;
        r36 = r0;
        r37 = 2;
        r36.removeMessages(r37);
    L_0x0356:
        r0 = r42;
        r0 = r0.mDoubleTapTouchSlopSquare;
        r36 = r0;
        r0 = r36;
        if (r11 <= r0) goto L_0x0099;
    L_0x0360:
        r36 = 0;
        r0 = r36;
        r1 = r42;
        r1.mAlwaysInBiggerTapRegion = r0;
        goto L_0x0099;
    L_0x036a:
        r36 = java.lang.Math.abs(r23);
        r37 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r36 = (r36 > r37 ? 1 : (r36 == r37 ? 0 : -1));
        if (r36 >= 0) goto L_0x037e;
    L_0x0374:
        r36 = java.lang.Math.abs(r24);
        r37 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r36 = (r36 > r37 ? 1 : (r36 == r37 ? 0 : -1));
        if (r36 < 0) goto L_0x0099;
    L_0x037e:
        r0 = r42;
        r0 = r0.mListener;
        r36 = r0;
        r0 = r42;
        r0 = r0.mCurrentDownEvent;
        r37 = r0;
        r0 = r36;
        r1 = r37;
        r2 = r43;
        r3 = r23;
        r4 = r24;
        r17 = r0.onScroll(r1, r2, r3, r4);
        r0 = r42;
        r0.mLastFocusX = r14;
        r0 = r42;
        r0.mLastFocusY = r15;
        goto L_0x0099;
    L_0x03a2:
        r36 = 0;
        r0 = r36;
        r1 = r42;
        r1.mStillDown = r0;
        r8 = android.view.MotionEvent.obtain(r43);
        r0 = r42;
        r0 = r0.mIsDoubleTapping;
        r36 = r0;
        if (r36 == 0) goto L_0x041c;
    L_0x03b6:
        r0 = r42;
        r0 = r0.mDoubleTapListener;
        r36 = r0;
        r0 = r36;
        r1 = r43;
        r36 = r0.onDoubleTapEvent(r1);
        r17 = r17 | r36;
    L_0x03c6:
        r0 = r42;
        r0 = r0.mPreviousUpEvent;
        r36 = r0;
        if (r36 == 0) goto L_0x03d7;
    L_0x03ce:
        r0 = r42;
        r0 = r0.mPreviousUpEvent;
        r36 = r0;
        r36.recycle();
    L_0x03d7:
        r0 = r42;
        r0.mPreviousUpEvent = r8;
        r0 = r42;
        r0 = r0.mVelocityTracker;
        r36 = r0;
        if (r36 == 0) goto L_0x03f4;
    L_0x03e3:
        r0 = r42;
        r0 = r0.mVelocityTracker;
        r36 = r0;
        r36.recycle();
        r36 = 0;
        r0 = r36;
        r1 = r42;
        r1.mVelocityTracker = r0;
    L_0x03f4:
        r36 = 0;
        r0 = r36;
        r1 = r42;
        r1.mIsDoubleTapping = r0;
        r36 = 0;
        r0 = r36;
        r1 = r42;
        r1.mDeferConfirmSingleTap = r0;
        r0 = r42;
        r0 = r0.mHandler;
        r36 = r0;
        r37 = 1;
        r36.removeMessages(r37);
        r0 = r42;
        r0 = r0.mHandler;
        r36 = r0;
        r37 = 2;
        r36.removeMessages(r37);
        goto L_0x0099;
    L_0x041c:
        r0 = r42;
        r0 = r0.mInLongPress;
        r36 = r0;
        if (r36 == 0) goto L_0x0438;
    L_0x0424:
        r0 = r42;
        r0 = r0.mHandler;
        r36 = r0;
        r37 = 3;
        r36.removeMessages(r37);
        r36 = 0;
        r0 = r36;
        r1 = r42;
        r1.mInLongPress = r0;
        goto L_0x03c6;
    L_0x0438:
        r0 = r42;
        r0 = r0.mAlwaysInTapRegion;
        r36 = r0;
        if (r36 == 0) goto L_0x046d;
    L_0x0440:
        r0 = r42;
        r0 = r0.mListener;
        r36 = r0;
        r0 = r36;
        r1 = r43;
        r17 = r0.onSingleTapUp(r1);
        r0 = r42;
        r0 = r0.mDeferConfirmSingleTap;
        r36 = r0;
        if (r36 == 0) goto L_0x03c6;
    L_0x0456:
        r0 = r42;
        r0 = r0.mDoubleTapListener;
        r36 = r0;
        if (r36 == 0) goto L_0x03c6;
    L_0x045e:
        r0 = r42;
        r0 = r0.mDoubleTapListener;
        r36 = r0;
        r0 = r36;
        r1 = r43;
        r0.onSingleTapConfirmed(r1);
        goto L_0x03c6;
    L_0x046d:
        r0 = r42;
        r0 = r0.mVelocityTracker;
        r29 = r0;
        r36 = 0;
        r0 = r43;
        r1 = r36;
        r21 = r0.getPointerId(r1);
        r36 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r0 = r42;
        r0 = r0.mMaximumFlingVelocity;
        r37 = r0;
        r0 = r37;
        r0 = (float) r0;
        r37 = r0;
        r0 = r29;
        r1 = r36;
        r2 = r37;
        r0.computeCurrentVelocity(r1, r2);
        r0 = r29;
        r1 = r21;
        r31 = r0.getYVelocity(r1);
        r0 = r29;
        r1 = r21;
        r30 = r0.getXVelocity(r1);
        r36 = java.lang.Math.abs(r31);
        r0 = r42;
        r0 = r0.mMinimumFlingVelocity;
        r37 = r0;
        r0 = r37;
        r0 = (float) r0;
        r37 = r0;
        r36 = (r36 > r37 ? 1 : (r36 == r37 ? 0 : -1));
        if (r36 > 0) goto L_0x04c9;
    L_0x04b6:
        r36 = java.lang.Math.abs(r30);
        r0 = r42;
        r0 = r0.mMinimumFlingVelocity;
        r37 = r0;
        r0 = r37;
        r0 = (float) r0;
        r37 = r0;
        r36 = (r36 > r37 ? 1 : (r36 == r37 ? 0 : -1));
        if (r36 <= 0) goto L_0x03c6;
    L_0x04c9:
        r0 = r42;
        r0 = r0.mListener;
        r36 = r0;
        r0 = r42;
        r0 = r0.mCurrentDownEvent;
        r37 = r0;
        r0 = r36;
        r1 = r37;
        r2 = r43;
        r3 = r30;
        r4 = r31;
        r17 = r0.onFling(r1, r2, r3, r4);
        goto L_0x03c6;
    L_0x04e5:
        r42.cancel();
        goto L_0x0099;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.GestureDetector.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void cancel() {
        this.mHandler.removeMessages(SHOW_PRESS);
        this.mHandler.removeMessages(LONG_PRESS);
        this.mHandler.removeMessages(TAP);
        this.mVelocityTracker.recycle();
        this.mVelocityTracker = null;
        this.mIsDoubleTapping = false;
        this.mStillDown = false;
        this.mAlwaysInTapRegion = false;
        this.mAlwaysInBiggerTapRegion = false;
        this.mDeferConfirmSingleTap = false;
        if (this.mInLongPress) {
            this.mInLongPress = false;
        }
    }

    private void cancelTaps() {
        this.mHandler.removeMessages(SHOW_PRESS);
        this.mHandler.removeMessages(LONG_PRESS);
        this.mHandler.removeMessages(TAP);
        this.mIsDoubleTapping = false;
        this.mAlwaysInTapRegion = false;
        this.mAlwaysInBiggerTapRegion = false;
        this.mDeferConfirmSingleTap = false;
        if (this.mInLongPress) {
            this.mInLongPress = false;
        }
    }

    private boolean isConsideredDoubleTap(MotionEvent firstDown, MotionEvent firstUp, MotionEvent secondDown) {
        if (!this.mAlwaysInBiggerTapRegion) {
            return false;
        }
        long deltaTime = secondDown.getEventTime() - firstUp.getEventTime();
        if (deltaTime > ((long) DOUBLE_TAP_TIMEOUT) || deltaTime < ((long) DOUBLE_TAP_MIN_TIME)) {
            return false;
        }
        int deltaX = ((int) firstDown.getX()) - ((int) secondDown.getX());
        int deltaY = ((int) firstDown.getY()) - ((int) secondDown.getY());
        if ((deltaX * deltaX) + (deltaY * deltaY) < this.mDoubleTapSlopSquare) {
            return true;
        }
        return false;
    }

    private void dispatchLongPress() {
        this.mHandler.removeMessages(TAP);
        this.mDeferConfirmSingleTap = false;
        this.mInLongPress = true;
        this.mListener.onLongPress(this.mCurrentDownEvent);
    }
}
