package android.graphics.drawable;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.graphics.Canvas;
import android.graphics.CanvasProperty;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.MathUtils;
import android.view.HardwareCanvas;
import android.view.RenderNodeAnimator;
import android.view.animation.LinearInterpolator;
import java.util.ArrayList;

class Ripple {
    private static final TimeInterpolator DECEL_INTERPOLATOR;
    private static final float GLOBAL_SPEED = 1.0f;
    private static final TimeInterpolator LINEAR_INTERPOLATOR;
    private static final long RIPPLE_ENTER_DELAY = 80;
    private static final float WAVE_OPACITY_DECAY_VELOCITY = 3.0f;
    private static final float WAVE_TOUCH_DOWN_ACCELERATION = 1024.0f;
    private static final float WAVE_TOUCH_UP_ACCELERATION = 3400.0f;
    private ObjectAnimator mAnimOpacity;
    private ObjectAnimator mAnimRadius;
    private ObjectAnimator mAnimX;
    private ObjectAnimator mAnimY;
    private final AnimatorListenerAdapter mAnimationListener;
    private final Rect mBounds;
    private boolean mCanUseHardware;
    private boolean mCanceled;
    private float mClampedStartingX;
    private float mClampedStartingY;
    private float mDensity;
    private boolean mHardwareAnimating;
    private boolean mHasMaxRadius;
    private boolean mHasPendingHardwareExit;
    private float mOpacity;
    private float mOuterRadius;
    private float mOuterX;
    private float mOuterY;
    private final RippleDrawable mOwner;
    private int mPendingOpacityDuration;
    private int mPendingRadiusDuration;
    private CanvasProperty<Paint> mPropPaint;
    private CanvasProperty<Float> mPropRadius;
    private CanvasProperty<Float> mPropX;
    private CanvasProperty<Float> mPropY;
    private final ArrayList<RenderNodeAnimator> mRunningAnimations;
    private float mStartingX;
    private float mStartingY;
    private Paint mTempPaint;
    private float mTweenRadius;
    private float mTweenX;
    private float mTweenY;

    private static final class LogInterpolator implements TimeInterpolator {
        private LogInterpolator() {
        }

        public float getInterpolation(float input) {
            return Ripple.GLOBAL_SPEED - ((float) Math.pow(400.0d, ((double) (-input)) * 1.4d));
        }
    }

    static {
        LINEAR_INTERPOLATOR = new LinearInterpolator();
        DECEL_INTERPOLATOR = new LogInterpolator();
    }

    public Ripple(RippleDrawable owner, Rect bounds, float startingX, float startingY) {
        this.mRunningAnimations = new ArrayList();
        this.mOpacity = GLOBAL_SPEED;
        this.mTweenRadius = 0.0f;
        this.mTweenX = 0.0f;
        this.mTweenY = 0.0f;
        this.mAnimationListener = new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                Ripple.this.removeSelf();
            }
        };
        this.mOwner = owner;
        this.mBounds = bounds;
        this.mStartingX = startingX;
        this.mStartingY = startingY;
    }

    public void setup(int maxRadius, float density) {
        if (maxRadius != -1) {
            this.mHasMaxRadius = true;
            this.mOuterRadius = (float) maxRadius;
        } else {
            float halfWidth = ((float) this.mBounds.width()) / 2.0f;
            float halfHeight = ((float) this.mBounds.height()) / 2.0f;
            this.mOuterRadius = (float) Math.sqrt((double) ((halfWidth * halfWidth) + (halfHeight * halfHeight)));
        }
        this.mOuterX = 0.0f;
        this.mOuterY = 0.0f;
        this.mDensity = density;
        clampStartingPosition();
    }

    public boolean isHardwareAnimating() {
        return this.mHardwareAnimating;
    }

    private void clampStartingPosition() {
        float cX = this.mBounds.exactCenterX();
        float cY = this.mBounds.exactCenterY();
        float dX = this.mStartingX - cX;
        float dY = this.mStartingY - cY;
        float r = this.mOuterRadius;
        if ((dX * dX) + (dY * dY) > r * r) {
            double angle = Math.atan2((double) dY, (double) dX);
            this.mClampedStartingX = ((float) (Math.cos(angle) * ((double) r))) + cX;
            this.mClampedStartingY = ((float) (Math.sin(angle) * ((double) r))) + cY;
            return;
        }
        this.mClampedStartingX = this.mStartingX;
        this.mClampedStartingY = this.mStartingY;
    }

    public void onHotspotBoundsChanged() {
        if (!this.mHasMaxRadius) {
            float halfWidth = ((float) this.mBounds.width()) / 2.0f;
            float halfHeight = ((float) this.mBounds.height()) / 2.0f;
            this.mOuterRadius = (float) Math.sqrt((double) ((halfWidth * halfWidth) + (halfHeight * halfHeight)));
            clampStartingPosition();
        }
    }

    public void setOpacity(float a) {
        this.mOpacity = a;
        invalidateSelf();
    }

    public float getOpacity() {
        return this.mOpacity;
    }

    public void setRadiusGravity(float r) {
        this.mTweenRadius = r;
        invalidateSelf();
    }

    public float getRadiusGravity() {
        return this.mTweenRadius;
    }

    public void setXGravity(float x) {
        this.mTweenX = x;
        invalidateSelf();
    }

    public float getXGravity() {
        return this.mTweenX;
    }

    public void setYGravity(float y) {
        this.mTweenY = y;
        invalidateSelf();
    }

    public float getYGravity() {
        return this.mTweenY;
    }

    public boolean draw(Canvas c, Paint p) {
        boolean canUseHardware = c.isHardwareAccelerated();
        if (this.mCanUseHardware != canUseHardware && this.mCanUseHardware) {
            cancelHardwareAnimations(true);
        }
        this.mCanUseHardware = canUseHardware;
        if (canUseHardware && (this.mHardwareAnimating || this.mHasPendingHardwareExit)) {
            return drawHardware((HardwareCanvas) c, p);
        }
        return drawSoftware(c, p);
    }

    private boolean drawHardware(HardwareCanvas c, Paint p) {
        if (this.mHasPendingHardwareExit) {
            cancelHardwareAnimations(false);
            startPendingHardwareExit(c, p);
        }
        c.drawCircle(this.mPropX, this.mPropY, this.mPropRadius, this.mPropPaint);
        return true;
    }

    private boolean drawSoftware(Canvas c, Paint p) {
        int paintAlpha = p.getAlpha();
        int alpha = (int) ((((float) paintAlpha) * this.mOpacity) + 0.5f);
        float radius = MathUtils.lerp(0.0f, this.mOuterRadius, this.mTweenRadius);
        if (alpha <= 0 || radius <= 0.0f) {
            return false;
        }
        float x = MathUtils.lerp(this.mClampedStartingX - this.mBounds.exactCenterX(), this.mOuterX, this.mTweenX);
        float y = MathUtils.lerp(this.mClampedStartingY - this.mBounds.exactCenterY(), this.mOuterY, this.mTweenY);
        p.setAlpha(alpha);
        c.drawCircle(x, y, radius, p);
        p.setAlpha(paintAlpha);
        return true;
    }

    public void getBounds(Rect bounds) {
        int outerX = (int) this.mOuterX;
        int outerY = (int) this.mOuterY;
        int r = ((int) this.mOuterRadius) + 1;
        bounds.set(outerX - r, outerY - r, outerX + r, outerY + r);
    }

    public void move(float x, float y) {
        this.mStartingX = x;
        this.mStartingY = y;
        clampStartingPosition();
    }

    public void enter() {
        cancel();
        int radiusDuration = (int) ((1000.0d * Math.sqrt((double) ((this.mOuterRadius / WAVE_TOUCH_DOWN_ACCELERATION) * this.mDensity))) + 0.5d);
        ObjectAnimator radius = ObjectAnimator.ofFloat((Object) this, "radiusGravity", GLOBAL_SPEED);
        radius.setAutoCancel(true);
        radius.setDuration((long) radiusDuration);
        radius.setInterpolator(LINEAR_INTERPOLATOR);
        radius.setStartDelay(RIPPLE_ENTER_DELAY);
        ObjectAnimator cX = ObjectAnimator.ofFloat((Object) this, "xGravity", GLOBAL_SPEED);
        cX.setAutoCancel(true);
        cX.setDuration((long) radiusDuration);
        cX.setInterpolator(LINEAR_INTERPOLATOR);
        cX.setStartDelay(RIPPLE_ENTER_DELAY);
        ObjectAnimator cY = ObjectAnimator.ofFloat((Object) this, "yGravity", GLOBAL_SPEED);
        cY.setAutoCancel(true);
        cY.setDuration((long) radiusDuration);
        cY.setInterpolator(LINEAR_INTERPOLATOR);
        cY.setStartDelay(RIPPLE_ENTER_DELAY);
        this.mAnimRadius = radius;
        this.mAnimX = cX;
        this.mAnimY = cY;
        radius.start();
        cX.start();
        cY.start();
    }

    public void exit() {
        float remaining;
        float radius = MathUtils.lerp(0.0f, this.mOuterRadius, this.mTweenRadius);
        if (this.mAnimRadius == null || !this.mAnimRadius.isRunning()) {
            remaining = this.mOuterRadius;
        } else {
            remaining = this.mOuterRadius - radius;
        }
        cancel();
        int radiusDuration = (int) ((1000.0d * Math.sqrt((double) ((remaining / 4424.0f) * this.mDensity))) + 0.5d);
        int opacityDuration = (int) (((1000.0f * this.mOpacity) / WAVE_OPACITY_DECAY_VELOCITY) + 0.5f);
        if (this.mCanUseHardware) {
            createPendingHardwareExit(radiusDuration, opacityDuration);
        } else {
            exitSoftware(radiusDuration, opacityDuration);
        }
    }

    private void createPendingHardwareExit(int radiusDuration, int opacityDuration) {
        this.mHasPendingHardwareExit = true;
        this.mPendingRadiusDuration = radiusDuration;
        this.mPendingOpacityDuration = opacityDuration;
        invalidateSelf();
    }

    private void startPendingHardwareExit(HardwareCanvas c, Paint p) {
        this.mHasPendingHardwareExit = false;
        int radiusDuration = this.mPendingRadiusDuration;
        int opacityDuration = this.mPendingOpacityDuration;
        float startX = MathUtils.lerp(this.mClampedStartingX - this.mBounds.exactCenterX(), this.mOuterX, this.mTweenX);
        float startY = MathUtils.lerp(this.mClampedStartingY - this.mBounds.exactCenterY(), this.mOuterY, this.mTweenY);
        float startRadius = MathUtils.lerp(0.0f, this.mOuterRadius, this.mTweenRadius);
        Paint paint = getTempPaint(p);
        paint.setAlpha((int) ((((float) paint.getAlpha()) * this.mOpacity) + 0.5f));
        this.mPropPaint = CanvasProperty.createPaint(paint);
        this.mPropRadius = CanvasProperty.createFloat(startRadius);
        this.mPropX = CanvasProperty.createFloat(startX);
        this.mPropY = CanvasProperty.createFloat(startY);
        RenderNodeAnimator radiusAnim = new RenderNodeAnimator(this.mPropRadius, this.mOuterRadius);
        radiusAnim.setDuration((long) radiusDuration);
        radiusAnim.setInterpolator(DECEL_INTERPOLATOR);
        radiusAnim.setTarget((Canvas) c);
        radiusAnim.start();
        RenderNodeAnimator xAnim = new RenderNodeAnimator(this.mPropX, this.mOuterX);
        xAnim.setDuration((long) radiusDuration);
        xAnim.setInterpolator(DECEL_INTERPOLATOR);
        xAnim.setTarget((Canvas) c);
        xAnim.start();
        RenderNodeAnimator yAnim = new RenderNodeAnimator(this.mPropY, this.mOuterY);
        yAnim.setDuration((long) radiusDuration);
        yAnim.setInterpolator(DECEL_INTERPOLATOR);
        yAnim.setTarget((Canvas) c);
        yAnim.start();
        RenderNodeAnimator opacityAnim = new RenderNodeAnimator(this.mPropPaint, 1, 0.0f);
        opacityAnim.setDuration((long) opacityDuration);
        opacityAnim.setInterpolator(LINEAR_INTERPOLATOR);
        opacityAnim.addListener(this.mAnimationListener);
        opacityAnim.setTarget((Canvas) c);
        opacityAnim.start();
        this.mRunningAnimations.add(radiusAnim);
        this.mRunningAnimations.add(opacityAnim);
        this.mRunningAnimations.add(xAnim);
        this.mRunningAnimations.add(yAnim);
        this.mHardwareAnimating = true;
        this.mOpacity = 0.0f;
        this.mTweenX = GLOBAL_SPEED;
        this.mTweenY = GLOBAL_SPEED;
        this.mTweenRadius = GLOBAL_SPEED;
    }

    public void jump() {
        this.mCanceled = true;
        endSoftwareAnimations();
        cancelHardwareAnimations(true);
        this.mCanceled = false;
    }

    private void endSoftwareAnimations() {
        if (this.mAnimRadius != null) {
            this.mAnimRadius.end();
            this.mAnimRadius = null;
        }
        if (this.mAnimOpacity != null) {
            this.mAnimOpacity.end();
            this.mAnimOpacity = null;
        }
        if (this.mAnimX != null) {
            this.mAnimX.end();
            this.mAnimX = null;
        }
        if (this.mAnimY != null) {
            this.mAnimY.end();
            this.mAnimY = null;
        }
    }

    private Paint getTempPaint(Paint original) {
        if (this.mTempPaint == null) {
            this.mTempPaint = new Paint();
        }
        this.mTempPaint.set(original);
        return this.mTempPaint;
    }

    private void exitSoftware(int radiusDuration, int opacityDuration) {
        ObjectAnimator radiusAnim = ObjectAnimator.ofFloat((Object) this, "radiusGravity", GLOBAL_SPEED);
        radiusAnim.setAutoCancel(true);
        radiusAnim.setDuration((long) radiusDuration);
        radiusAnim.setInterpolator(DECEL_INTERPOLATOR);
        ObjectAnimator xAnim = ObjectAnimator.ofFloat((Object) this, "xGravity", GLOBAL_SPEED);
        xAnim.setAutoCancel(true);
        xAnim.setDuration((long) radiusDuration);
        xAnim.setInterpolator(DECEL_INTERPOLATOR);
        ObjectAnimator yAnim = ObjectAnimator.ofFloat((Object) this, "yGravity", GLOBAL_SPEED);
        yAnim.setAutoCancel(true);
        yAnim.setDuration((long) radiusDuration);
        yAnim.setInterpolator(DECEL_INTERPOLATOR);
        ObjectAnimator opacityAnim = ObjectAnimator.ofFloat((Object) this, "opacity", 0.0f);
        opacityAnim.setAutoCancel(true);
        opacityAnim.setDuration((long) opacityDuration);
        opacityAnim.setInterpolator(LINEAR_INTERPOLATOR);
        opacityAnim.addListener(this.mAnimationListener);
        this.mAnimRadius = radiusAnim;
        this.mAnimOpacity = opacityAnim;
        this.mAnimX = xAnim;
        this.mAnimY = yAnim;
        radiusAnim.start();
        opacityAnim.start();
        xAnim.start();
        yAnim.start();
    }

    public void cancel() {
        this.mCanceled = true;
        cancelSoftwareAnimations();
        cancelHardwareAnimations(false);
        this.mCanceled = false;
    }

    private void cancelSoftwareAnimations() {
        if (this.mAnimRadius != null) {
            this.mAnimRadius.cancel();
            this.mAnimRadius = null;
        }
        if (this.mAnimOpacity != null) {
            this.mAnimOpacity.cancel();
            this.mAnimOpacity = null;
        }
        if (this.mAnimX != null) {
            this.mAnimX.cancel();
            this.mAnimX = null;
        }
        if (this.mAnimY != null) {
            this.mAnimY.cancel();
            this.mAnimY = null;
        }
    }

    private void cancelHardwareAnimations(boolean jumpToEnd) {
        ArrayList<RenderNodeAnimator> runningAnimations = this.mRunningAnimations;
        int N = runningAnimations.size();
        for (int i = 0; i < N; i++) {
            if (jumpToEnd) {
                ((RenderNodeAnimator) runningAnimations.get(i)).end();
            } else {
                ((RenderNodeAnimator) runningAnimations.get(i)).cancel();
            }
        }
        runningAnimations.clear();
        if (this.mHasPendingHardwareExit) {
            this.mHasPendingHardwareExit = false;
            if (jumpToEnd) {
                this.mOpacity = 0.0f;
                this.mTweenX = GLOBAL_SPEED;
                this.mTweenY = GLOBAL_SPEED;
                this.mTweenRadius = GLOBAL_SPEED;
            }
        }
        this.mHardwareAnimating = false;
    }

    private void removeSelf() {
        if (!this.mCanceled) {
            this.mOwner.removeRipple(this);
        }
    }

    private void invalidateSelf() {
        this.mOwner.invalidateSelf();
    }
}
