package com.android.internal.policy.impl;

import android.content.Context;
import android.view.MotionEvent;
import android.view.WindowManagerPolicy.PointerEventListener;

public class SystemGesturesPointerEventListener implements PointerEventListener {
    private static final boolean DEBUG = false;
    private static final int MAX_TRACKED_POINTERS = 32;
    private static final int SWIPE_FROM_BOTTOM = 2;
    private static final int SWIPE_FROM_RIGHT = 3;
    private static final int SWIPE_FROM_TOP = 1;
    private static final int SWIPE_NONE = 0;
    private static final long SWIPE_TIMEOUT_MS = 500;
    private static final String TAG = "SystemGestures";
    private static final int UNTRACKED_POINTER = -1;
    private final Callbacks mCallbacks;
    private boolean mDebugFireable;
    private final int[] mDownPointerId;
    private int mDownPointers;
    private final long[] mDownTime;
    private final float[] mDownX;
    private final float[] mDownY;
    private final int mSwipeDistanceThreshold;
    private boolean mSwipeFireable;
    private final int mSwipeStartThreshold;
    int screenHeight;
    int screenWidth;

    interface Callbacks {
        void onDebug();

        void onSwipeFromBottom();

        void onSwipeFromRight();

        void onSwipeFromTop();
    }

    public SystemGesturesPointerEventListener(Context context, Callbacks callbacks) {
        this.mDownPointerId = new int[MAX_TRACKED_POINTERS];
        this.mDownX = new float[MAX_TRACKED_POINTERS];
        this.mDownY = new float[MAX_TRACKED_POINTERS];
        this.mDownTime = new long[MAX_TRACKED_POINTERS];
        this.mCallbacks = (Callbacks) checkNull("callbacks", callbacks);
        this.mSwipeStartThreshold = ((Context) checkNull("context", context)).getResources().getDimensionPixelSize(17104914);
        this.mSwipeDistanceThreshold = this.mSwipeStartThreshold;
    }

    private static <T> T checkNull(String name, T arg) {
        if (arg != null) {
            return arg;
        }
        throw new IllegalArgumentException(name + " must not be null");
    }

    public void onPointerEvent(MotionEvent event) {
        boolean z = true;
        boolean z2 = DEBUG;
        switch (event.getActionMasked()) {
            case SWIPE_NONE /*0*/:
                this.mSwipeFireable = true;
                this.mDebugFireable = true;
                this.mDownPointers = SWIPE_NONE;
                captureDown(event, SWIPE_NONE);
            case SWIPE_FROM_TOP /*1*/:
            case SWIPE_FROM_RIGHT /*3*/:
                this.mSwipeFireable = DEBUG;
                this.mDebugFireable = DEBUG;
            case SWIPE_FROM_BOTTOM /*2*/:
                if (this.mSwipeFireable) {
                    int swipe = detectSwipe(event);
                    if (swipe == 0) {
                        z2 = true;
                    }
                    this.mSwipeFireable = z2;
                    if (swipe == SWIPE_FROM_TOP) {
                        this.mCallbacks.onSwipeFromTop();
                    } else if (swipe == SWIPE_FROM_BOTTOM) {
                        this.mCallbacks.onSwipeFromBottom();
                    } else if (swipe == SWIPE_FROM_RIGHT) {
                        this.mCallbacks.onSwipeFromRight();
                    }
                }
            case 5:
                captureDown(event, event.getActionIndex());
                if (this.mDebugFireable) {
                    if (event.getPointerCount() >= 5) {
                        z = DEBUG;
                    }
                    this.mDebugFireable = z;
                    if (!this.mDebugFireable) {
                        this.mCallbacks.onDebug();
                    }
                }
            default:
        }
    }

    private void captureDown(MotionEvent event, int pointerIndex) {
        int i = findIndex(event.getPointerId(pointerIndex));
        if (i != UNTRACKED_POINTER) {
            this.mDownX[i] = event.getX(pointerIndex);
            this.mDownY[i] = event.getY(pointerIndex);
            this.mDownTime[i] = event.getEventTime();
        }
    }

    private int findIndex(int pointerId) {
        for (int i = SWIPE_NONE; i < this.mDownPointers; i += SWIPE_FROM_TOP) {
            if (this.mDownPointerId[i] == pointerId) {
                return i;
            }
        }
        if (this.mDownPointers == MAX_TRACKED_POINTERS || pointerId == UNTRACKED_POINTER) {
            return UNTRACKED_POINTER;
        }
        int[] iArr = this.mDownPointerId;
        int i2 = this.mDownPointers;
        this.mDownPointers = i2 + SWIPE_FROM_TOP;
        iArr[i2] = pointerId;
        return this.mDownPointers + UNTRACKED_POINTER;
    }

    private int detectSwipe(MotionEvent move) {
        int historySize = move.getHistorySize();
        int pointerCount = move.getPointerCount();
        for (int p = SWIPE_NONE; p < pointerCount; p += SWIPE_FROM_TOP) {
            int i = findIndex(move.getPointerId(p));
            if (i != UNTRACKED_POINTER) {
                int swipe;
                for (int h = SWIPE_NONE; h < historySize; h += SWIPE_FROM_TOP) {
                    swipe = detectSwipe(i, move.getHistoricalEventTime(h), move.getHistoricalX(p, h), move.getHistoricalY(p, h));
                    if (swipe != 0) {
                        return swipe;
                    }
                }
                swipe = detectSwipe(i, move.getEventTime(), move.getX(p), move.getY(p));
                if (swipe != 0) {
                    return swipe;
                }
            }
        }
        return SWIPE_NONE;
    }

    private int detectSwipe(int i, long time, float x, float y) {
        float fromX = this.mDownX[i];
        float fromY = this.mDownY[i];
        long elapsed = time - this.mDownTime[i];
        if (fromY <= ((float) this.mSwipeStartThreshold) && y > ((float) this.mSwipeDistanceThreshold) + fromY && elapsed < SWIPE_TIMEOUT_MS) {
            return SWIPE_FROM_TOP;
        }
        if (fromY >= ((float) (this.screenHeight - this.mSwipeStartThreshold)) && y < fromY - ((float) this.mSwipeDistanceThreshold) && elapsed < SWIPE_TIMEOUT_MS) {
            return SWIPE_FROM_BOTTOM;
        }
        if (fromX < ((float) (this.screenWidth - this.mSwipeStartThreshold)) || x >= fromX - ((float) this.mSwipeDistanceThreshold) || elapsed >= SWIPE_TIMEOUT_MS) {
            return SWIPE_NONE;
        }
        return SWIPE_FROM_RIGHT;
    }
}
