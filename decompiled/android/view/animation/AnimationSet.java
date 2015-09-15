package android.view.animation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.RectF;
import android.net.LinkQualityInfo;
import android.util.AttributeSet;
import android.view.WindowManager.LayoutParams;
import com.android.internal.R;
import java.util.ArrayList;
import java.util.List;

public class AnimationSet extends Animation {
    private static final int PROPERTY_CHANGE_BOUNDS_MASK = 128;
    private static final int PROPERTY_DURATION_MASK = 32;
    private static final int PROPERTY_FILL_AFTER_MASK = 1;
    private static final int PROPERTY_FILL_BEFORE_MASK = 2;
    private static final int PROPERTY_MORPH_MATRIX_MASK = 64;
    private static final int PROPERTY_REPEAT_MODE_MASK = 4;
    private static final int PROPERTY_SHARE_INTERPOLATOR_MASK = 16;
    private static final int PROPERTY_START_OFFSET_MASK = 8;
    private ArrayList<Animation> mAnimations;
    private boolean mDirty;
    private int mFlags;
    private boolean mHasAlpha;
    private long mLastEnd;
    private long[] mStoredOffsets;
    private Transformation mTempTransformation;

    public AnimationSet(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mFlags = 0;
        this.mAnimations = new ArrayList();
        this.mTempTransformation = new Transformation();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AnimationSet);
        setFlag(PROPERTY_SHARE_INTERPOLATOR_MASK, a.getBoolean(PROPERTY_FILL_AFTER_MASK, true));
        init();
        if (context.getApplicationInfo().targetSdkVersion >= 14) {
            if (a.hasValue(0)) {
                this.mFlags |= PROPERTY_DURATION_MASK;
            }
            if (a.hasValue(PROPERTY_FILL_BEFORE_MASK)) {
                this.mFlags |= PROPERTY_FILL_BEFORE_MASK;
            }
            if (a.hasValue(3)) {
                this.mFlags |= PROPERTY_FILL_AFTER_MASK;
            }
            if (a.hasValue(5)) {
                this.mFlags |= PROPERTY_REPEAT_MODE_MASK;
            }
            if (a.hasValue(PROPERTY_REPEAT_MODE_MASK)) {
                this.mFlags |= PROPERTY_START_OFFSET_MASK;
            }
        }
        a.recycle();
    }

    public AnimationSet(boolean shareInterpolator) {
        this.mFlags = 0;
        this.mAnimations = new ArrayList();
        this.mTempTransformation = new Transformation();
        setFlag(PROPERTY_SHARE_INTERPOLATOR_MASK, shareInterpolator);
        init();
    }

    protected AnimationSet clone() throws CloneNotSupportedException {
        AnimationSet animation = (AnimationSet) super.clone();
        animation.mTempTransformation = new Transformation();
        animation.mAnimations = new ArrayList();
        int count = this.mAnimations.size();
        ArrayList<Animation> animations = this.mAnimations;
        for (int i = 0; i < count; i += PROPERTY_FILL_AFTER_MASK) {
            animation.mAnimations.add(((Animation) animations.get(i)).clone());
        }
        return animation;
    }

    private void setFlag(int mask, boolean value) {
        if (value) {
            this.mFlags |= mask;
        } else {
            this.mFlags &= mask ^ -1;
        }
    }

    private void init() {
        this.mStartTime = 0;
    }

    public void setFillAfter(boolean fillAfter) {
        this.mFlags |= PROPERTY_FILL_AFTER_MASK;
        super.setFillAfter(fillAfter);
    }

    public void setFillBefore(boolean fillBefore) {
        this.mFlags |= PROPERTY_FILL_BEFORE_MASK;
        super.setFillBefore(fillBefore);
    }

    public void setRepeatMode(int repeatMode) {
        this.mFlags |= PROPERTY_REPEAT_MODE_MASK;
        super.setRepeatMode(repeatMode);
    }

    public void setStartOffset(long startOffset) {
        this.mFlags |= PROPERTY_START_OFFSET_MASK;
        super.setStartOffset(startOffset);
    }

    public boolean hasAlpha() {
        if (this.mDirty) {
            this.mHasAlpha = false;
            this.mDirty = false;
            int count = this.mAnimations.size();
            ArrayList<Animation> animations = this.mAnimations;
            for (int i = 0; i < count; i += PROPERTY_FILL_AFTER_MASK) {
                if (((Animation) animations.get(i)).hasAlpha()) {
                    this.mHasAlpha = true;
                    break;
                }
            }
        }
        return this.mHasAlpha;
    }

    public void setDuration(long durationMillis) {
        this.mFlags |= PROPERTY_DURATION_MASK;
        super.setDuration(durationMillis);
        this.mLastEnd = this.mStartOffset + this.mDuration;
    }

    public void addAnimation(Animation a) {
        boolean noMatrix;
        boolean changeBounds = false;
        this.mAnimations.add(a);
        if ((this.mFlags & PROPERTY_MORPH_MATRIX_MASK) == 0) {
            noMatrix = true;
        } else {
            noMatrix = false;
        }
        if (noMatrix && a.willChangeTransformationMatrix()) {
            this.mFlags |= PROPERTY_MORPH_MATRIX_MASK;
        }
        if ((this.mFlags & PROPERTY_CHANGE_BOUNDS_MASK) == 0) {
            changeBounds = true;
        }
        if (changeBounds && a.willChangeBounds()) {
            this.mFlags |= PROPERTY_CHANGE_BOUNDS_MASK;
        }
        if ((this.mFlags & PROPERTY_DURATION_MASK) == PROPERTY_DURATION_MASK) {
            this.mLastEnd = this.mStartOffset + this.mDuration;
        } else if (this.mAnimations.size() == PROPERTY_FILL_AFTER_MASK) {
            this.mDuration = a.getStartOffset() + a.getDuration();
            this.mLastEnd = this.mStartOffset + this.mDuration;
        } else {
            this.mLastEnd = Math.max(this.mLastEnd, a.getStartOffset() + a.getDuration());
            this.mDuration = this.mLastEnd - this.mStartOffset;
        }
        this.mDirty = true;
    }

    public void setStartTime(long startTimeMillis) {
        super.setStartTime(startTimeMillis);
        int count = this.mAnimations.size();
        ArrayList<Animation> animations = this.mAnimations;
        for (int i = 0; i < count; i += PROPERTY_FILL_AFTER_MASK) {
            ((Animation) animations.get(i)).setStartTime(startTimeMillis);
        }
    }

    public long getStartTime() {
        long startTime = LinkQualityInfo.UNKNOWN_LONG;
        int count = this.mAnimations.size();
        ArrayList<Animation> animations = this.mAnimations;
        for (int i = 0; i < count; i += PROPERTY_FILL_AFTER_MASK) {
            startTime = Math.min(startTime, ((Animation) animations.get(i)).getStartTime());
        }
        return startTime;
    }

    public void restrictDuration(long durationMillis) {
        super.restrictDuration(durationMillis);
        ArrayList<Animation> animations = this.mAnimations;
        int count = animations.size();
        for (int i = 0; i < count; i += PROPERTY_FILL_AFTER_MASK) {
            ((Animation) animations.get(i)).restrictDuration(durationMillis);
        }
    }

    public long getDuration() {
        ArrayList<Animation> animations = this.mAnimations;
        int count = animations.size();
        long duration = 0;
        if ((this.mFlags & PROPERTY_DURATION_MASK) == PROPERTY_DURATION_MASK) {
            return this.mDuration;
        }
        for (int i = 0; i < count; i += PROPERTY_FILL_AFTER_MASK) {
            duration = Math.max(duration, ((Animation) animations.get(i)).getDuration());
        }
        return duration;
    }

    public long computeDurationHint() {
        long duration = 0;
        int count = this.mAnimations.size();
        ArrayList<Animation> animations = this.mAnimations;
        for (int i = count - 1; i >= 0; i--) {
            long d = ((Animation) animations.get(i)).computeDurationHint();
            if (d > duration) {
                duration = d;
            }
        }
        return duration;
    }

    public void initializeInvalidateRegion(int left, int top, int right, int bottom) {
        RectF region = this.mPreviousRegion;
        region.set((float) left, (float) top, (float) right, (float) bottom);
        region.inset(LayoutParams.BRIGHTNESS_OVERRIDE_NONE, LayoutParams.BRIGHTNESS_OVERRIDE_NONE);
        if (this.mFillBefore) {
            int count = this.mAnimations.size();
            ArrayList<Animation> animations = this.mAnimations;
            Transformation temp = this.mTempTransformation;
            Transformation previousTransformation = this.mPreviousTransformation;
            for (int i = count - 1; i >= 0; i--) {
                Animation a = (Animation) animations.get(i);
                if (!a.isFillEnabled() || a.getFillBefore() || a.getStartOffset() == 0) {
                    temp.clear();
                    Interpolator interpolator = a.mInterpolator;
                    a.applyTransformation(interpolator != null ? interpolator.getInterpolation(0.0f) : 0.0f, temp);
                    previousTransformation.compose(temp);
                }
            }
        }
    }

    public boolean getTransformation(long currentTime, Transformation t) {
        int count = this.mAnimations.size();
        ArrayList<Animation> animations = this.mAnimations;
        Transformation temp = this.mTempTransformation;
        boolean more = false;
        boolean started = false;
        boolean ended = true;
        t.clear();
        for (int i = count - 1; i >= 0; i--) {
            Animation a = (Animation) animations.get(i);
            temp.clear();
            if (a.getTransformation(currentTime, temp, getScaleFactor()) || more) {
                more = true;
            } else {
                more = false;
            }
            t.compose(temp);
            if (started || a.hasStarted()) {
                started = true;
            } else {
                started = false;
            }
            if (a.hasEnded() && ended) {
                ended = true;
            } else {
                ended = false;
            }
        }
        if (started && !this.mStarted) {
            if (this.mListener != null) {
                this.mListener.onAnimationStart(this);
            }
            this.mStarted = true;
        }
        if (ended != this.mEnded) {
            if (this.mListener != null) {
                this.mListener.onAnimationEnd(this);
            }
            this.mEnded = ended;
        }
        return more;
    }

    public void scaleCurrentDuration(float scale) {
        ArrayList<Animation> animations = this.mAnimations;
        int count = animations.size();
        for (int i = 0; i < count; i += PROPERTY_FILL_AFTER_MASK) {
            ((Animation) animations.get(i)).scaleCurrentDuration(scale);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void initialize(int r29, int r30, int r31, int r32) {
        /*
        r28 = this;
        super.initialize(r29, r30, r31, r32);
        r0 = r28;
        r0 = r0.mFlags;
        r25 = r0;
        r25 = r25 & 32;
        r26 = 32;
        r0 = r25;
        r1 = r26;
        if (r0 != r1) goto L_0x00f3;
    L_0x0013:
        r7 = 1;
    L_0x0014:
        r0 = r28;
        r0 = r0.mFlags;
        r25 = r0;
        r25 = r25 & 1;
        r26 = 1;
        r0 = r25;
        r1 = r26;
        if (r0 != r1) goto L_0x00f6;
    L_0x0024:
        r11 = 1;
    L_0x0025:
        r0 = r28;
        r0 = r0.mFlags;
        r25 = r0;
        r25 = r25 & 2;
        r26 = 2;
        r0 = r25;
        r1 = r26;
        if (r0 != r1) goto L_0x00f9;
    L_0x0035:
        r13 = 1;
    L_0x0036:
        r0 = r28;
        r0 = r0.mFlags;
        r25 = r0;
        r25 = r25 & 4;
        r26 = 4;
        r0 = r25;
        r1 = r26;
        if (r0 != r1) goto L_0x00fc;
    L_0x0046:
        r19 = 1;
    L_0x0048:
        r0 = r28;
        r0 = r0.mFlags;
        r25 = r0;
        r25 = r25 & 16;
        r26 = 16;
        r0 = r25;
        r1 = r26;
        if (r0 != r1) goto L_0x0100;
    L_0x0058:
        r20 = 1;
    L_0x005a:
        r0 = r28;
        r0 = r0.mFlags;
        r25 = r0;
        r25 = r25 & 8;
        r26 = 8;
        r0 = r25;
        r1 = r26;
        if (r0 != r1) goto L_0x0104;
    L_0x006a:
        r21 = 1;
    L_0x006c:
        if (r20 == 0) goto L_0x0071;
    L_0x006e:
        r28.ensureInterpolator();
    L_0x0071:
        r0 = r28;
        r5 = r0.mAnimations;
        r6 = r5.size();
        r0 = r28;
        r8 = r0.mDuration;
        r0 = r28;
        r10 = r0.mFillAfter;
        r0 = r28;
        r12 = r0.mFillBefore;
        r0 = r28;
        r0 = r0.mRepeatMode;
        r18 = r0;
        r0 = r28;
        r15 = r0.mInterpolator;
        r0 = r28;
        r0 = r0.mStartOffset;
        r22 = r0;
        r0 = r28;
        r0 = r0.mStoredOffsets;
        r24 = r0;
        if (r21 == 0) goto L_0x0108;
    L_0x009d:
        if (r24 == 0) goto L_0x00a8;
    L_0x009f:
        r0 = r24;
        r0 = r0.length;
        r25 = r0;
        r0 = r25;
        if (r0 == r6) goto L_0x00b2;
    L_0x00a8:
        r0 = new long[r6];
        r24 = r0;
        r0 = r24;
        r1 = r28;
        r1.mStoredOffsets = r0;
    L_0x00b2:
        r14 = 0;
    L_0x00b3:
        if (r14 >= r6) goto L_0x0113;
    L_0x00b5:
        r4 = r5.get(r14);
        r4 = (android.view.animation.Animation) r4;
        if (r7 == 0) goto L_0x00c0;
    L_0x00bd:
        r4.setDuration(r8);
    L_0x00c0:
        if (r11 == 0) goto L_0x00c5;
    L_0x00c2:
        r4.setFillAfter(r10);
    L_0x00c5:
        if (r13 == 0) goto L_0x00ca;
    L_0x00c7:
        r4.setFillBefore(r12);
    L_0x00ca:
        if (r19 == 0) goto L_0x00d1;
    L_0x00cc:
        r0 = r18;
        r4.setRepeatMode(r0);
    L_0x00d1:
        if (r20 == 0) goto L_0x00d6;
    L_0x00d3:
        r4.setInterpolator(r15);
    L_0x00d6:
        if (r21 == 0) goto L_0x00e5;
    L_0x00d8:
        r16 = r4.getStartOffset();
        r26 = r16 + r22;
        r0 = r26;
        r4.setStartOffset(r0);
        r24[r14] = r16;
    L_0x00e5:
        r0 = r29;
        r1 = r30;
        r2 = r31;
        r3 = r32;
        r4.initialize(r0, r1, r2, r3);
        r14 = r14 + 1;
        goto L_0x00b3;
    L_0x00f3:
        r7 = 0;
        goto L_0x0014;
    L_0x00f6:
        r11 = 0;
        goto L_0x0025;
    L_0x00f9:
        r13 = 0;
        goto L_0x0036;
    L_0x00fc:
        r19 = 0;
        goto L_0x0048;
    L_0x0100:
        r20 = 0;
        goto L_0x005a;
    L_0x0104:
        r21 = 0;
        goto L_0x006c;
    L_0x0108:
        if (r24 == 0) goto L_0x00b2;
    L_0x010a:
        r24 = 0;
        r0 = r24;
        r1 = r28;
        r1.mStoredOffsets = r0;
        goto L_0x00b2;
    L_0x0113:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.view.animation.AnimationSet.initialize(int, int, int, int):void");
    }

    public void reset() {
        super.reset();
        restoreChildrenStartOffset();
    }

    void restoreChildrenStartOffset() {
        long[] offsets = this.mStoredOffsets;
        if (offsets != null) {
            ArrayList<Animation> children = this.mAnimations;
            int count = children.size();
            for (int i = 0; i < count; i += PROPERTY_FILL_AFTER_MASK) {
                ((Animation) children.get(i)).setStartOffset(offsets[i]);
            }
        }
    }

    public List<Animation> getAnimations() {
        return this.mAnimations;
    }

    public boolean willChangeTransformationMatrix() {
        return (this.mFlags & PROPERTY_MORPH_MATRIX_MASK) == PROPERTY_MORPH_MATRIX_MASK;
    }

    public boolean willChangeBounds() {
        return (this.mFlags & PROPERTY_CHANGE_BOUNDS_MASK) == PROPERTY_CHANGE_BOUNDS_MASK;
    }
}
