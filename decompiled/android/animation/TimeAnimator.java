package android.animation;

import android.view.animation.AnimationUtils;

public class TimeAnimator extends ValueAnimator {
    private TimeListener mListener;
    private long mPreviousTime;

    public interface TimeListener {
        void onTimeUpdate(TimeAnimator timeAnimator, long j, long j2);
    }

    public TimeAnimator() {
        this.mPreviousTime = -1;
    }

    public void start() {
        this.mPreviousTime = -1;
        super.start();
    }

    boolean animationFrame(long currentTime) {
        long deltaTime = 0;
        if (this.mListener != null) {
            long totalTime = currentTime - this.mStartTime;
            if (this.mPreviousTime >= 0) {
                deltaTime = currentTime - this.mPreviousTime;
            }
            this.mPreviousTime = currentTime;
            this.mListener.onTimeUpdate(this, totalTime, deltaTime);
        }
        return false;
    }

    public void setCurrentPlayTime(long playTime) {
        long currentTime = AnimationUtils.currentAnimationTimeMillis();
        this.mStartTime = Math.max(this.mStartTime, currentTime - playTime);
        animationFrame(currentTime);
    }

    public void setTimeListener(TimeListener listener) {
        this.mListener = listener;
    }

    void animateValue(float fraction) {
    }

    void initAnimation() {
    }
}
