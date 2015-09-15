package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.drawable.DrawableContainer.DrawableContainerState;
import android.os.SystemClock;
import android.util.AttributeSet;
import com.android.internal.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class AnimationDrawable extends DrawableContainer implements Runnable, Animatable {
    private boolean mAnimating;
    private AnimationState mAnimationState;
    private int mCurFrame;
    private boolean mMutated;
    private boolean mRunning;

    private static final class AnimationState extends DrawableContainerState {
        private int[] mDurations;
        private boolean mOneShot;

        AnimationState(AnimationState orig, AnimationDrawable owner, Resources res) {
            super(orig, owner, res);
            this.mOneShot = false;
            if (orig != null) {
                this.mDurations = orig.mDurations;
                this.mOneShot = orig.mOneShot;
                return;
            }
            this.mDurations = new int[getCapacity()];
            this.mOneShot = false;
        }

        private void mutate() {
            this.mDurations = (int[]) this.mDurations.clone();
        }

        public Drawable newDrawable() {
            return new AnimationDrawable(null, null);
        }

        public Drawable newDrawable(Resources res) {
            return new AnimationDrawable(res, null);
        }

        public void addFrame(Drawable dr, int dur) {
            this.mDurations[super.addChild(dr)] = dur;
        }

        public void growArray(int oldSize, int newSize) {
            super.growArray(oldSize, newSize);
            int[] newDurations = new int[newSize];
            System.arraycopy(this.mDurations, 0, newDurations, 0, oldSize);
            this.mDurations = newDurations;
        }
    }

    public AnimationDrawable() {
        this(null, null);
    }

    public boolean setVisible(boolean visible, boolean restart) {
        int i = 0;
        boolean changed = super.setVisible(visible, restart);
        if (!visible) {
            unscheduleSelf(this);
        } else if (restart || changed) {
            boolean startFromZero = restart || this.mCurFrame < 0 || this.mCurFrame >= this.mAnimationState.getChildCount();
            if (!startFromZero) {
                i = this.mCurFrame;
            }
            setFrame(i, true, this.mAnimating);
        }
        return changed;
    }

    public void start() {
        this.mAnimating = true;
        if (!isRunning()) {
            run();
        }
    }

    public void stop() {
        this.mAnimating = false;
        if (isRunning()) {
            unscheduleSelf(this);
        }
    }

    public boolean isRunning() {
        return this.mRunning;
    }

    public void run() {
        nextFrame(false);
    }

    public void unscheduleSelf(Runnable what) {
        this.mCurFrame = -1;
        this.mRunning = false;
        super.unscheduleSelf(what);
    }

    public int getNumberOfFrames() {
        return this.mAnimationState.getChildCount();
    }

    public Drawable getFrame(int index) {
        return this.mAnimationState.getChild(index);
    }

    public int getDuration(int i) {
        return this.mAnimationState.mDurations[i];
    }

    public boolean isOneShot() {
        return this.mAnimationState.mOneShot;
    }

    public void setOneShot(boolean oneShot) {
        this.mAnimationState.mOneShot = oneShot;
    }

    public void addFrame(Drawable frame, int duration) {
        this.mAnimationState.addFrame(frame, duration);
        if (this.mCurFrame < 0) {
            setFrame(0, true, false);
        }
    }

    private void nextFrame(boolean unschedule) {
        int next = this.mCurFrame + 1;
        int N = this.mAnimationState.getChildCount();
        if (next >= N) {
            next = 0;
        }
        boolean z = !this.mAnimationState.mOneShot || next < N - 1;
        setFrame(next, unschedule, z);
    }

    private void setFrame(int frame, boolean unschedule, boolean animate) {
        if (frame < this.mAnimationState.getChildCount()) {
            this.mAnimating = animate;
            this.mCurFrame = frame;
            selectDrawable(frame);
            if (unschedule || animate) {
                unscheduleSelf(this);
            }
            if (animate) {
                this.mCurFrame = frame;
                this.mRunning = true;
                scheduleSelf(this, SystemClock.uptimeMillis() + ((long) this.mAnimationState.mDurations[frame]));
            }
        }
    }

    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Theme theme) throws XmlPullParserException, IOException {
        TypedArray a = Drawable.obtainAttributes(r, theme, attrs, R.styleable.AnimationDrawable);
        super.inflateWithAttributes(r, parser, a, 0);
        updateStateFromTypedArray(a);
        a.recycle();
        inflateChildElements(r, parser, attrs, theme);
        setFrame(0, true, false);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void inflateChildElements(android.content.res.Resources r11, org.xmlpull.v1.XmlPullParser r12, android.util.AttributeSet r13, android.content.res.Resources.Theme r14) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        r10 = this;
        r9 = 2;
        r8 = 1;
        r6 = r12.getDepth();
        r4 = r6 + 1;
    L_0x0008:
        r5 = r12.next();
        if (r5 == r8) goto L_0x0097;
    L_0x000e:
        r1 = r12.getDepth();
        if (r1 >= r4) goto L_0x0017;
    L_0x0014:
        r6 = 3;
        if (r5 == r6) goto L_0x0097;
    L_0x0017:
        if (r5 != r9) goto L_0x0008;
    L_0x0019:
        if (r1 > r4) goto L_0x0008;
    L_0x001b:
        r6 = r12.getName();
        r7 = "item";
        r6 = r6.equals(r7);
        if (r6 == 0) goto L_0x0008;
    L_0x0027:
        r6 = com.android.internal.R.styleable.AnimationDrawableItem;
        r0 = android.graphics.drawable.Drawable.obtainAttributes(r11, r14, r13, r6);
        r6 = 0;
        r7 = -1;
        r3 = r0.getInt(r6, r7);
        if (r3 >= 0) goto L_0x0052;
    L_0x0035:
        r6 = new org.xmlpull.v1.XmlPullParserException;
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = r12.getPositionDescription();
        r7 = r7.append(r8);
        r8 = ": <item> tag requires a 'duration' attribute";
        r7 = r7.append(r8);
        r7 = r7.toString();
        r6.<init>(r7);
        throw r6;
    L_0x0052:
        r2 = r0.getDrawable(r8);
        r0.recycle();
        if (r2 != 0) goto L_0x008b;
    L_0x005b:
        r5 = r12.next();
        r6 = 4;
        if (r5 == r6) goto L_0x005b;
    L_0x0062:
        if (r5 == r9) goto L_0x0087;
    L_0x0064:
        r6 = new org.xmlpull.v1.XmlPullParserException;
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = r12.getPositionDescription();
        r7 = r7.append(r8);
        r8 = ": <item> tag requires a 'drawable' attribute or child tag";
        r7 = r7.append(r8);
        r8 = " defining a drawable";
        r7 = r7.append(r8);
        r7 = r7.toString();
        r6.<init>(r7);
        throw r6;
    L_0x0087:
        r2 = android.graphics.drawable.Drawable.createFromXmlInner(r11, r12, r13, r14);
    L_0x008b:
        r6 = r10.mAnimationState;
        r6.addFrame(r2, r3);
        if (r2 == 0) goto L_0x0008;
    L_0x0092:
        r2.setCallback(r10);
        goto L_0x0008;
    L_0x0097:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.graphics.drawable.AnimationDrawable.inflateChildElements(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.content.res.Resources$Theme):void");
    }

    private void updateStateFromTypedArray(TypedArray a) {
        this.mAnimationState.mVariablePadding = a.getBoolean(1, this.mAnimationState.mVariablePadding);
        this.mAnimationState.mOneShot = a.getBoolean(2, this.mAnimationState.mOneShot);
    }

    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mAnimationState.mutate();
            this.mMutated = true;
        }
        return this;
    }

    AnimationState cloneConstantState() {
        return new AnimationState(this.mAnimationState, this, null);
    }

    public void clearMutated() {
        super.clearMutated();
        this.mMutated = false;
    }

    protected void setConstantState(DrawableContainerState state) {
        super.setConstantState(state);
        if (state instanceof AnimationState) {
            this.mAnimationState = (AnimationState) state;
        }
    }

    private AnimationDrawable(AnimationState state, Resources res) {
        this.mCurFrame = -1;
        setConstantState(new AnimationState(state, this, res));
        if (state != null) {
            setFrame(0, true, false);
        }
    }
}
