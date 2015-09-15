package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.FloatMath;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import com.android.internal.R;

public class EdgeEffect {
    private static final double ANGLE = 0.5235987755982988d;
    private static final float COS;
    private static final float EPSILON = 0.001f;
    private static final float MAX_ALPHA = 0.5f;
    private static final float MAX_GLOW_SCALE = 2.0f;
    private static final int MAX_VELOCITY = 10000;
    private static final int MIN_VELOCITY = 100;
    private static final int PULL_DECAY_TIME = 2000;
    private static final float PULL_DISTANCE_ALPHA_GLOW_FACTOR = 0.8f;
    private static final float PULL_GLOW_BEGIN = 0.0f;
    private static final int PULL_TIME = 167;
    private static final int RECEDE_TIME = 600;
    private static final float SIN;
    private static final int STATE_ABSORB = 2;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PULL = 1;
    private static final int STATE_PULL_DECAY = 4;
    private static final int STATE_RECEDE = 3;
    private static final String TAG = "EdgeEffect";
    private static final int VELOCITY_GLOW_FACTOR = 6;
    private float mBaseGlowScale;
    private final Rect mBounds;
    private float mDisplacement;
    private float mDuration;
    private float mGlowAlpha;
    private float mGlowAlphaFinish;
    private float mGlowAlphaStart;
    private float mGlowScaleY;
    private float mGlowScaleYFinish;
    private float mGlowScaleYStart;
    private final Interpolator mInterpolator;
    private final Paint mPaint;
    private float mPullDistance;
    private float mRadius;
    private long mStartTime;
    private int mState;
    private float mTargetDisplacement;

    static {
        SIN = (float) Math.sin(ANGLE);
        COS = (float) Math.cos(ANGLE);
    }

    public EdgeEffect(Context context) {
        this.mState = STATE_IDLE;
        this.mBounds = new Rect();
        this.mPaint = new Paint();
        this.mDisplacement = MAX_ALPHA;
        this.mTargetDisplacement = MAX_ALPHA;
        this.mPaint.setAntiAlias(true);
        TypedArray a = context.obtainStyledAttributes(R.styleable.EdgeEffect);
        int themeColor = a.getColor(STATE_IDLE, -10066330);
        a.recycle();
        this.mPaint.setColor((View.MEASURED_SIZE_MASK & themeColor) | 855638016);
        this.mPaint.setStyle(Style.FILL);
        this.mPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_ATOP));
        this.mInterpolator = new DecelerateInterpolator();
    }

    public void setSize(int width, int height) {
        float f = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        float r = (((float) width) * 0.75f) / SIN;
        float h = r - (COS * r);
        float or = (((float) height) * 0.75f) / SIN;
        float oh = or - (COS * or);
        this.mRadius = r;
        if (h > SIN) {
            f = Math.min(oh / h, LayoutParams.BRIGHTNESS_OVERRIDE_FULL);
        }
        this.mBaseGlowScale = f;
        this.mBounds.set(this.mBounds.left, this.mBounds.top, width, (int) Math.min((float) height, h));
    }

    public boolean isFinished() {
        return this.mState == 0;
    }

    public void finish() {
        this.mState = STATE_IDLE;
    }

    public void onPull(float deltaDistance) {
        onPull(deltaDistance, MAX_ALPHA);
    }

    public void onPull(float deltaDistance, float displacement) {
        long now = AnimationUtils.currentAnimationTimeMillis();
        this.mTargetDisplacement = displacement;
        if (this.mState != STATE_PULL_DECAY || ((float) (now - this.mStartTime)) >= this.mDuration) {
            if (this.mState != STATE_PULL) {
                this.mGlowScaleY = Math.max(SIN, this.mGlowScaleY);
            }
            this.mState = STATE_PULL;
            this.mStartTime = now;
            this.mDuration = 167.0f;
            this.mPullDistance += deltaDistance;
            float min = Math.min(MAX_ALPHA, this.mGlowAlpha + (PULL_DISTANCE_ALPHA_GLOW_FACTOR * Math.abs(deltaDistance)));
            this.mGlowAlphaStart = min;
            this.mGlowAlpha = min;
            if (this.mPullDistance == SIN) {
                this.mGlowScaleYStart = SIN;
                this.mGlowScaleY = SIN;
            } else {
                float scale = Math.max(SIN, (LayoutParams.BRIGHTNESS_OVERRIDE_FULL - (LayoutParams.BRIGHTNESS_OVERRIDE_FULL / FloatMath.sqrt(Math.abs(this.mPullDistance) * ((float) this.mBounds.height())))) - 0.3f) / 0.7f;
                this.mGlowScaleYStart = scale;
                this.mGlowScaleY = scale;
            }
            this.mGlowAlphaFinish = this.mGlowAlpha;
            this.mGlowScaleYFinish = this.mGlowScaleY;
        }
    }

    public void onRelease() {
        this.mPullDistance = SIN;
        if (this.mState == STATE_PULL || this.mState == STATE_PULL_DECAY) {
            this.mState = STATE_RECEDE;
            this.mGlowAlphaStart = this.mGlowAlpha;
            this.mGlowScaleYStart = this.mGlowScaleY;
            this.mGlowAlphaFinish = SIN;
            this.mGlowScaleYFinish = SIN;
            this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
            this.mDuration = 600.0f;
        }
    }

    public void onAbsorb(int velocity) {
        this.mState = STATE_ABSORB;
        velocity = Math.min(Math.max(MIN_VELOCITY, Math.abs(velocity)), MAX_VELOCITY);
        this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
        this.mDuration = 0.15f + (((float) velocity) * 0.02f);
        this.mGlowAlphaStart = 0.3f;
        this.mGlowScaleYStart = Math.max(this.mGlowScaleY, SIN);
        this.mGlowScaleYFinish = Math.min(0.025f + ((((float) ((velocity / MIN_VELOCITY) * velocity)) * 1.5E-4f) / MAX_GLOW_SCALE), LayoutParams.BRIGHTNESS_OVERRIDE_FULL);
        this.mGlowAlphaFinish = Math.max(this.mGlowAlphaStart, Math.min(((float) (velocity * VELOCITY_GLOW_FACTOR)) * 1.0E-5f, MAX_ALPHA));
        this.mTargetDisplacement = MAX_ALPHA;
    }

    public void setColor(int color) {
        this.mPaint.setColor(color);
    }

    public int getColor() {
        return this.mPaint.getColor();
    }

    public boolean draw(Canvas canvas) {
        update();
        int count = canvas.save();
        float centerX = (float) this.mBounds.centerX();
        float centerY = ((float) this.mBounds.height()) - this.mRadius;
        canvas.scale(LayoutParams.BRIGHTNESS_OVERRIDE_FULL, Math.min(this.mGlowScaleY, LayoutParams.BRIGHTNESS_OVERRIDE_FULL) * this.mBaseGlowScale, centerX, SIN);
        float translateX = (((float) this.mBounds.width()) * (Math.max(SIN, Math.min(this.mDisplacement, LayoutParams.BRIGHTNESS_OVERRIDE_FULL)) - MAX_ALPHA)) / MAX_GLOW_SCALE;
        canvas.clipRect(this.mBounds);
        canvas.translate(translateX, SIN);
        this.mPaint.setAlpha((int) (255.0f * this.mGlowAlpha));
        canvas.drawCircle(centerX, centerY, this.mRadius, this.mPaint);
        canvas.restoreToCount(count);
        boolean oneLastFrame = false;
        if (this.mState == STATE_RECEDE && this.mGlowScaleY == SIN) {
            this.mState = STATE_IDLE;
            oneLastFrame = true;
        }
        if (this.mState != 0 || oneLastFrame) {
            return true;
        }
        return false;
    }

    public int getMaxHeight() {
        return (int) ((((float) this.mBounds.height()) * MAX_GLOW_SCALE) + MAX_ALPHA);
    }

    private void update() {
        float t = Math.min(((float) (AnimationUtils.currentAnimationTimeMillis() - this.mStartTime)) / this.mDuration, LayoutParams.BRIGHTNESS_OVERRIDE_FULL);
        float interp = this.mInterpolator.getInterpolation(t);
        this.mGlowAlpha = this.mGlowAlphaStart + ((this.mGlowAlphaFinish - this.mGlowAlphaStart) * interp);
        this.mGlowScaleY = this.mGlowScaleYStart + ((this.mGlowScaleYFinish - this.mGlowScaleYStart) * interp);
        this.mDisplacement = (this.mDisplacement + this.mTargetDisplacement) / MAX_GLOW_SCALE;
        if (t >= 0.999f) {
            switch (this.mState) {
                case STATE_PULL /*1*/:
                    this.mState = STATE_PULL_DECAY;
                    this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
                    this.mDuration = 2000.0f;
                    this.mGlowAlphaStart = this.mGlowAlpha;
                    this.mGlowScaleYStart = this.mGlowScaleY;
                    this.mGlowAlphaFinish = SIN;
                    this.mGlowScaleYFinish = SIN;
                case STATE_ABSORB /*2*/:
                    this.mState = STATE_RECEDE;
                    this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
                    this.mDuration = 600.0f;
                    this.mGlowAlphaStart = this.mGlowAlpha;
                    this.mGlowScaleYStart = this.mGlowScaleY;
                    this.mGlowAlphaFinish = SIN;
                    this.mGlowScaleYFinish = SIN;
                case STATE_RECEDE /*3*/:
                    this.mState = STATE_IDLE;
                case STATE_PULL_DECAY /*4*/:
                    this.mState = STATE_RECEDE;
                default:
            }
        }
    }
}
