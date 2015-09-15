package com.android.internal.policy.impl;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.SystemProperties;
import android.util.FloatMath;
import android.util.Log;
import android.util.Slog;
import java.io.PrintWriter;

public abstract class WindowOrientationListener {
    private static final boolean LOG;
    private static final String TAG = "WindowOrientationListener";
    private static final boolean USE_GRAVITY_SENSOR = false;
    private int mCurrentRotation;
    private boolean mEnabled;
    private Handler mHandler;
    private final Object mLock;
    private int mRate;
    private Sensor mSensor;
    private SensorEventListenerImpl mSensorEventListener;
    private SensorManager mSensorManager;

    final class SensorEventListenerImpl implements SensorEventListener {
        private static final float ACCELERATION_TOLERANCE = 4.0f;
        private static final int ACCELEROMETER_DATA_X = 0;
        private static final int ACCELEROMETER_DATA_Y = 1;
        private static final int ACCELEROMETER_DATA_Z = 2;
        private static final int ADJACENT_ORIENTATION_ANGLE_GAP = 45;
        private static final float FILTER_TIME_CONSTANT_MS = 200.0f;
        private static final float FLAT_ANGLE = 75.0f;
        private static final long FLAT_TIME_NANOS = 1000000000;
        private static final float MAX_ACCELERATION_MAGNITUDE = 13.80665f;
        private static final long MAX_FILTER_DELTA_TIME_NANOS = 1000000000;
        private static final int MAX_TILT = 75;
        private static final float MIN_ACCELERATION_MAGNITUDE = 5.80665f;
        private static final long NANOS_PER_MS = 1000000;
        private static final float NEAR_ZERO_MAGNITUDE = 1.0f;
        private static final long PROPOSAL_MIN_TIME_SINCE_ACCELERATION_ENDED_NANOS = 500000000;
        private static final long PROPOSAL_MIN_TIME_SINCE_FLAT_ENDED_NANOS = 500000000;
        private static final long PROPOSAL_MIN_TIME_SINCE_SWING_ENDED_NANOS = 300000000;
        private static final long PROPOSAL_SETTLE_TIME_NANOS = 40000000;
        private static final float RADIANS_TO_DEGREES = 57.29578f;
        private static final float SWING_AWAY_ANGLE_DELTA = 20.0f;
        private static final long SWING_TIME_NANOS = 300000000;
        private static final int TILT_HISTORY_SIZE = 40;
        private final int TILT_OVERHEAD_ENTER;
        private final int TILT_OVERHEAD_EXIT;
        private final int[][] TILT_TOLERANCE;
        private boolean mAccelerating;
        private long mAccelerationTimestampNanos;
        private boolean mFlat;
        private long mFlatTimestampNanos;
        private long mLastFilteredTimestampNanos;
        private float mLastFilteredX;
        private float mLastFilteredY;
        private float mLastFilteredZ;
        private boolean mOverhead;
        private int mPredictedRotation;
        private long mPredictedRotationTimestampNanos;
        private int mProposedRotation;
        private long mSwingTimestampNanos;
        private boolean mSwinging;
        private float[] mTiltHistory;
        private int mTiltHistoryIndex;
        private long[] mTiltHistoryTimestampNanos;

        SensorEventListenerImpl() {
            this.TILT_TOLERANCE = new int[][]{new int[]{-25, 70}, new int[]{-25, 65}, new int[]{-25, 60}, new int[]{-25, 65}};
            this.TILT_OVERHEAD_ENTER = -40;
            this.TILT_OVERHEAD_EXIT = -15;
            this.mTiltHistory = new float[TILT_HISTORY_SIZE];
            this.mTiltHistoryTimestampNanos = new long[TILT_HISTORY_SIZE];
        }

        public int getProposedRotationLocked() {
            return this.mProposedRotation;
        }

        public void dumpLocked(PrintWriter pw, String prefix) {
            pw.println(prefix + "mProposedRotation=" + this.mProposedRotation);
            pw.println(prefix + "mPredictedRotation=" + this.mPredictedRotation);
            pw.println(prefix + "mLastFilteredX=" + this.mLastFilteredX);
            pw.println(prefix + "mLastFilteredY=" + this.mLastFilteredY);
            pw.println(prefix + "mLastFilteredZ=" + this.mLastFilteredZ);
            pw.println(prefix + "mTiltHistory={last: " + getLastTiltLocked() + "}");
            pw.println(prefix + "mFlat=" + this.mFlat);
            pw.println(prefix + "mSwinging=" + this.mSwinging);
            pw.println(prefix + "mAccelerating=" + this.mAccelerating);
            pw.println(prefix + "mOverhead=" + this.mOverhead);
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent event) {
            synchronized (WindowOrientationListener.this.mLock) {
                boolean skipSample;
                float x = event.values[ACCELEROMETER_DATA_X];
                float y = event.values[ACCELEROMETER_DATA_Y];
                float z = event.values[ACCELEROMETER_DATA_Z];
                if (WindowOrientationListener.LOG) {
                    Slog.v(WindowOrientationListener.TAG, "Raw acceleration vector: x=" + x + ", y=" + y + ", z=" + z + ", magnitude=" + FloatMath.sqrt(((x * x) + (y * y)) + (z * z)));
                }
                long now = event.timestamp;
                long then = this.mLastFilteredTimestampNanos;
                float timeDeltaMS = ((float) (now - then)) * 1.0E-6f;
                if (now < then || now > MAX_FILTER_DELTA_TIME_NANOS + then || (x == 0.0f && y == 0.0f && z == 0.0f)) {
                    if (WindowOrientationListener.LOG) {
                        Slog.v(WindowOrientationListener.TAG, "Resetting orientation listener.");
                    }
                    resetLocked();
                    skipSample = true;
                } else {
                    float alpha = timeDeltaMS / (FILTER_TIME_CONSTANT_MS + timeDeltaMS);
                    float f = this.mLastFilteredX;
                    x = ((x - r0) * alpha) + this.mLastFilteredX;
                    f = this.mLastFilteredY;
                    y = ((y - r0) * alpha) + this.mLastFilteredY;
                    f = this.mLastFilteredZ;
                    z = ((z - r0) * alpha) + this.mLastFilteredZ;
                    if (WindowOrientationListener.LOG) {
                        Slog.v(WindowOrientationListener.TAG, "Filtered acceleration vector: x=" + x + ", y=" + y + ", z=" + z + ", magnitude=" + FloatMath.sqrt(((x * x) + (y * y)) + (z * z)));
                    }
                    skipSample = WindowOrientationListener.LOG;
                }
                this.mLastFilteredTimestampNanos = now;
                this.mLastFilteredX = x;
                this.mLastFilteredY = y;
                this.mLastFilteredZ = z;
                boolean isAccelerating = WindowOrientationListener.LOG;
                boolean isFlat = WindowOrientationListener.LOG;
                boolean isSwinging = WindowOrientationListener.LOG;
                if (!skipSample) {
                    float magnitude = FloatMath.sqrt(((x * x) + (y * y)) + (z * z));
                    if (magnitude < NEAR_ZERO_MAGNITUDE) {
                        if (WindowOrientationListener.LOG) {
                            Slog.v(WindowOrientationListener.TAG, "Ignoring sensor data, magnitude too close to zero.");
                        }
                        clearPredictedRotationLocked();
                    } else {
                        if (isAcceleratingLocked(magnitude)) {
                            isAccelerating = true;
                            this.mAccelerationTimestampNanos = now;
                        }
                        int tiltAngle = (int) Math.round(Math.asin((double) (z / magnitude)) * 57.295780181884766d);
                        addTiltHistoryEntryLocked(now, (float) tiltAngle);
                        if (isFlatLocked(now)) {
                            isFlat = true;
                            this.mFlatTimestampNanos = now;
                        }
                        if (isSwingingLocked(now, (float) tiltAngle)) {
                            isSwinging = true;
                            this.mSwingTimestampNanos = now;
                        }
                        if (tiltAngle <= -40) {
                            this.mOverhead = true;
                        } else if (tiltAngle >= -15) {
                            this.mOverhead = WindowOrientationListener.LOG;
                        }
                        if (this.mOverhead) {
                            if (WindowOrientationListener.LOG) {
                                Slog.v(WindowOrientationListener.TAG, "Ignoring sensor data, device is overhead: tiltAngle=" + tiltAngle);
                            }
                            clearPredictedRotationLocked();
                        } else if (Math.abs(tiltAngle) > MAX_TILT) {
                            if (WindowOrientationListener.LOG) {
                                Slog.v(WindowOrientationListener.TAG, "Ignoring sensor data, tilt angle too high: tiltAngle=" + tiltAngle);
                            }
                            clearPredictedRotationLocked();
                        } else {
                            int orientationAngle = (int) Math.round((-Math.atan2((double) (-x), (double) y)) * 57.295780181884766d);
                            if (orientationAngle < 0) {
                                orientationAngle += 360;
                            }
                            int nearestRotation = (orientationAngle + ADJACENT_ORIENTATION_ANGLE_GAP) / 90;
                            if (nearestRotation == 4) {
                                nearestRotation = ACCELEROMETER_DATA_X;
                            }
                            if (isTiltAngleAcceptableLocked(nearestRotation, tiltAngle) && isOrientationAngleAcceptableLocked(nearestRotation, orientationAngle)) {
                                updatePredictedRotationLocked(now, nearestRotation);
                                if (WindowOrientationListener.LOG) {
                                    String str = WindowOrientationListener.TAG;
                                    StringBuilder append = new StringBuilder().append("Predicted: tiltAngle=").append(tiltAngle).append(", orientationAngle=");
                                    Slog.v(str, r25.append(orientationAngle).append(", predictedRotation=").append(this.mPredictedRotation).append(", predictedRotationAgeMS=").append(((float) (now - this.mPredictedRotationTimestampNanos)) * 1.0E-6f).toString());
                                }
                            } else {
                                if (WindowOrientationListener.LOG) {
                                    Slog.v(WindowOrientationListener.TAG, "Ignoring sensor data, no predicted rotation: tiltAngle=" + tiltAngle + ", orientationAngle=" + orientationAngle);
                                }
                                clearPredictedRotationLocked();
                            }
                        }
                    }
                }
                this.mFlat = isFlat;
                this.mSwinging = isSwinging;
                this.mAccelerating = isAccelerating;
                int oldProposedRotation = this.mProposedRotation;
                if (this.mPredictedRotation < 0 || isPredictedRotationAcceptableLocked(now)) {
                    this.mProposedRotation = this.mPredictedRotation;
                }
                int proposedRotation = this.mProposedRotation;
                if (WindowOrientationListener.LOG) {
                    str = WindowOrientationListener.TAG;
                    append = new StringBuilder().append("Result: currentRotation=").append(WindowOrientationListener.this.mCurrentRotation).append(", proposedRotation=");
                    append = r25.append(proposedRotation).append(", predictedRotation=").append(this.mPredictedRotation).append(", timeDeltaMS=").append(timeDeltaMS).append(", isAccelerating=").append(isAccelerating).append(", isFlat=").append(isFlat).append(", isSwinging=");
                    Slog.v(str, r25.append(isSwinging).append(", isOverhead=").append(this.mOverhead).append(", timeUntilSettledMS=").append(remainingMS(now, this.mPredictedRotationTimestampNanos + PROPOSAL_SETTLE_TIME_NANOS)).append(", timeUntilAccelerationDelayExpiredMS=").append(remainingMS(now, this.mAccelerationTimestampNanos + PROPOSAL_MIN_TIME_SINCE_FLAT_ENDED_NANOS)).append(", timeUntilFlatDelayExpiredMS=").append(remainingMS(now, this.mFlatTimestampNanos + PROPOSAL_MIN_TIME_SINCE_FLAT_ENDED_NANOS)).append(", timeUntilSwingDelayExpiredMS=").append(remainingMS(now, this.mSwingTimestampNanos + SWING_TIME_NANOS)).toString());
                }
            }
            if (proposedRotation != oldProposedRotation && proposedRotation >= 0) {
                if (WindowOrientationListener.LOG) {
                    Slog.v(WindowOrientationListener.TAG, "Proposed rotation changed!  proposedRotation=" + proposedRotation + ", oldProposedRotation=" + oldProposedRotation);
                }
                WindowOrientationListener.this.onProposedRotationChanged(proposedRotation);
            }
        }

        private boolean isTiltAngleAcceptableLocked(int rotation, int tiltAngle) {
            return (tiltAngle < this.TILT_TOLERANCE[rotation][ACCELEROMETER_DATA_X] || tiltAngle > this.TILT_TOLERANCE[rotation][ACCELEROMETER_DATA_Y]) ? WindowOrientationListener.LOG : true;
        }

        private boolean isOrientationAngleAcceptableLocked(int rotation, int orientationAngle) {
            int currentRotation = WindowOrientationListener.this.mCurrentRotation;
            if (currentRotation >= 0) {
                if (rotation == currentRotation || rotation == (currentRotation + ACCELEROMETER_DATA_Y) % 4) {
                    int lowerBound = ((rotation * 90) - 45) + 22;
                    if (rotation == 0) {
                        if (orientationAngle >= 315 && orientationAngle < lowerBound + 360) {
                            return WindowOrientationListener.LOG;
                        }
                    } else if (orientationAngle < lowerBound) {
                        return WindowOrientationListener.LOG;
                    }
                }
                if (rotation == currentRotation || rotation == (currentRotation + 3) % 4) {
                    int upperBound = ((rotation * 90) + ADJACENT_ORIENTATION_ANGLE_GAP) - 22;
                    if (rotation == 0) {
                        if (orientationAngle <= ADJACENT_ORIENTATION_ANGLE_GAP && orientationAngle > upperBound) {
                            return WindowOrientationListener.LOG;
                        }
                    } else if (orientationAngle > upperBound) {
                        return WindowOrientationListener.LOG;
                    }
                }
            }
            return true;
        }

        private boolean isPredictedRotationAcceptableLocked(long now) {
            if (now >= this.mPredictedRotationTimestampNanos + PROPOSAL_SETTLE_TIME_NANOS && now >= this.mFlatTimestampNanos + PROPOSAL_MIN_TIME_SINCE_FLAT_ENDED_NANOS && now >= this.mSwingTimestampNanos + SWING_TIME_NANOS && now >= this.mAccelerationTimestampNanos + PROPOSAL_MIN_TIME_SINCE_FLAT_ENDED_NANOS) {
                return true;
            }
            return WindowOrientationListener.LOG;
        }

        private void resetLocked() {
            this.mLastFilteredTimestampNanos = Long.MIN_VALUE;
            this.mProposedRotation = -1;
            this.mFlatTimestampNanos = Long.MIN_VALUE;
            this.mFlat = WindowOrientationListener.LOG;
            this.mSwingTimestampNanos = Long.MIN_VALUE;
            this.mSwinging = WindowOrientationListener.LOG;
            this.mAccelerationTimestampNanos = Long.MIN_VALUE;
            this.mAccelerating = WindowOrientationListener.LOG;
            this.mOverhead = WindowOrientationListener.LOG;
            clearPredictedRotationLocked();
            clearTiltHistoryLocked();
        }

        private void clearPredictedRotationLocked() {
            this.mPredictedRotation = -1;
            this.mPredictedRotationTimestampNanos = Long.MIN_VALUE;
        }

        private void updatePredictedRotationLocked(long now, int rotation) {
            if (this.mPredictedRotation != rotation) {
                this.mPredictedRotation = rotation;
                this.mPredictedRotationTimestampNanos = now;
            }
        }

        private boolean isAcceleratingLocked(float magnitude) {
            return (magnitude < MIN_ACCELERATION_MAGNITUDE || magnitude > MAX_ACCELERATION_MAGNITUDE) ? true : WindowOrientationListener.LOG;
        }

        private void clearTiltHistoryLocked() {
            this.mTiltHistoryTimestampNanos[ACCELEROMETER_DATA_X] = Long.MIN_VALUE;
            this.mTiltHistoryIndex = ACCELEROMETER_DATA_Y;
        }

        private void addTiltHistoryEntryLocked(long now, float tilt) {
            this.mTiltHistory[this.mTiltHistoryIndex] = tilt;
            this.mTiltHistoryTimestampNanos[this.mTiltHistoryIndex] = now;
            this.mTiltHistoryIndex = (this.mTiltHistoryIndex + ACCELEROMETER_DATA_Y) % TILT_HISTORY_SIZE;
            this.mTiltHistoryTimestampNanos[this.mTiltHistoryIndex] = Long.MIN_VALUE;
        }

        private boolean isFlatLocked(long now) {
            int i = this.mTiltHistoryIndex;
            do {
                i = nextTiltHistoryIndexLocked(i);
                if (i < 0 || this.mTiltHistory[i] < FLAT_ANGLE) {
                    return WindowOrientationListener.LOG;
                }
            } while (this.mTiltHistoryTimestampNanos[i] + MAX_FILTER_DELTA_TIME_NANOS > now);
            return true;
        }

        private boolean isSwingingLocked(long now, float tilt) {
            int i = this.mTiltHistoryIndex;
            do {
                i = nextTiltHistoryIndexLocked(i);
                if (i < 0 || this.mTiltHistoryTimestampNanos[i] + SWING_TIME_NANOS < now) {
                    return WindowOrientationListener.LOG;
                }
            } while (this.mTiltHistory[i] + SWING_AWAY_ANGLE_DELTA > tilt);
            return true;
        }

        private int nextTiltHistoryIndexLocked(int index) {
            if (index == 0) {
                index = TILT_HISTORY_SIZE;
            }
            index--;
            return this.mTiltHistoryTimestampNanos[index] != Long.MIN_VALUE ? index : -1;
        }

        private float getLastTiltLocked() {
            int index = nextTiltHistoryIndexLocked(this.mTiltHistoryIndex);
            return index >= 0 ? this.mTiltHistory[index] : Float.NaN;
        }

        private float remainingMS(long now, long until) {
            return now >= until ? 0.0f : ((float) (until - now)) * 1.0E-6f;
        }
    }

    public abstract void onProposedRotationChanged(int i);

    static {
        LOG = SystemProperties.getBoolean("debug.orientation.log", LOG);
    }

    public WindowOrientationListener(Context context, Handler handler) {
        this(context, handler, 2);
    }

    private WindowOrientationListener(Context context, Handler handler, int rate) {
        this.mCurrentRotation = -1;
        this.mLock = new Object();
        this.mHandler = handler;
        this.mSensorManager = (SensorManager) context.getSystemService("sensor");
        this.mRate = rate;
        this.mSensor = this.mSensorManager.getDefaultSensor(1);
        if (this.mSensor != null) {
            this.mSensorEventListener = new SensorEventListenerImpl();
        }
    }

    public void enable() {
        synchronized (this.mLock) {
            if (this.mSensor == null) {
                Log.w(TAG, "Cannot detect sensors. Not enabled");
                return;
            }
            if (!this.mEnabled) {
                if (LOG) {
                    Log.d(TAG, "WindowOrientationListener enabled");
                }
                this.mSensorEventListener.resetLocked();
                this.mSensorManager.registerListener(this.mSensorEventListener, this.mSensor, this.mRate, this.mHandler);
                this.mEnabled = true;
            }
        }
    }

    public void disable() {
        synchronized (this.mLock) {
            if (this.mSensor == null) {
                Log.w(TAG, "Cannot detect sensors. Invalid disable");
                return;
            }
            if (this.mEnabled) {
                if (LOG) {
                    Log.d(TAG, "WindowOrientationListener disabled");
                }
                this.mSensorManager.unregisterListener(this.mSensorEventListener);
                this.mEnabled = LOG;
            }
        }
    }

    public void setCurrentRotation(int rotation) {
        synchronized (this.mLock) {
            this.mCurrentRotation = rotation;
        }
    }

    public int getProposedRotation() {
        int proposedRotationLocked;
        synchronized (this.mLock) {
            if (this.mEnabled) {
                proposedRotationLocked = this.mSensorEventListener.getProposedRotationLocked();
            } else {
                proposedRotationLocked = -1;
            }
        }
        return proposedRotationLocked;
    }

    public boolean canDetectOrientation() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mSensor != null ? true : LOG;
        }
        return z;
    }

    public void dump(PrintWriter pw, String prefix) {
        synchronized (this.mLock) {
            pw.println(prefix + TAG);
            prefix = prefix + "  ";
            pw.println(prefix + "mEnabled=" + this.mEnabled);
            pw.println(prefix + "mCurrentRotation=" + this.mCurrentRotation);
            pw.println(prefix + "mSensor=" + this.mSensor);
            pw.println(prefix + "mRate=" + this.mRate);
            if (this.mSensorEventListener != null) {
                this.mSensorEventListener.dumpLocked(pw, prefix);
            }
        }
    }
}
