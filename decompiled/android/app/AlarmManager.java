package android.app;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.WorkSource;

public class AlarmManager {
    public static final String ACTION_NEXT_ALARM_CLOCK_CHANGED = "android.app.action.NEXT_ALARM_CLOCK_CHANGED";
    public static final int ELAPSED_REALTIME = 3;
    public static final int ELAPSED_REALTIME_WAKEUP = 2;
    public static final long INTERVAL_DAY = 86400000;
    public static final long INTERVAL_FIFTEEN_MINUTES = 900000;
    public static final long INTERVAL_HALF_DAY = 43200000;
    public static final long INTERVAL_HALF_HOUR = 1800000;
    public static final long INTERVAL_HOUR = 3600000;
    public static final int RTC = 1;
    public static final int RTC_WAKEUP = 0;
    private static final String TAG = "AlarmManager";
    public static final long WINDOW_EXACT = 0;
    public static final long WINDOW_HEURISTIC = -1;
    private final boolean mAlwaysExact;
    private final IAlarmManager mService;

    public static final class AlarmClockInfo implements Parcelable {
        public static final Creator<AlarmClockInfo> CREATOR;
        private final PendingIntent mShowIntent;
        private final long mTriggerTime;

        public AlarmClockInfo(long triggerTime, PendingIntent showIntent) {
            this.mTriggerTime = triggerTime;
            this.mShowIntent = showIntent;
        }

        AlarmClockInfo(Parcel in) {
            this.mTriggerTime = in.readLong();
            this.mShowIntent = (PendingIntent) in.readParcelable(PendingIntent.class.getClassLoader());
        }

        public long getTriggerTime() {
            return this.mTriggerTime;
        }

        public PendingIntent getShowIntent() {
            return this.mShowIntent;
        }

        public int describeContents() {
            return AlarmManager.RTC_WAKEUP;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.mTriggerTime);
            dest.writeParcelable(this.mShowIntent, flags);
        }

        static {
            CREATOR = new Creator<AlarmClockInfo>() {
                public AlarmClockInfo createFromParcel(Parcel in) {
                    return new AlarmClockInfo(in);
                }

                public AlarmClockInfo[] newArray(int size) {
                    return new AlarmClockInfo[size];
                }
            };
        }
    }

    AlarmManager(IAlarmManager service, Context ctx) {
        this.mService = service;
        this.mAlwaysExact = ctx.getApplicationInfo().targetSdkVersion < 19;
    }

    private long legacyExactLength() {
        return this.mAlwaysExact ? WINDOW_EXACT : WINDOW_HEURISTIC;
    }

    public void set(int type, long triggerAtMillis, PendingIntent operation) {
        setImpl(type, triggerAtMillis, legacyExactLength(), WINDOW_EXACT, operation, null, null);
    }

    public void setRepeating(int type, long triggerAtMillis, long intervalMillis, PendingIntent operation) {
        setImpl(type, triggerAtMillis, legacyExactLength(), intervalMillis, operation, null, null);
    }

    public void setWindow(int type, long windowStartMillis, long windowLengthMillis, PendingIntent operation) {
        setImpl(type, windowStartMillis, windowLengthMillis, WINDOW_EXACT, operation, null, null);
    }

    public void setExact(int type, long triggerAtMillis, PendingIntent operation) {
        setImpl(type, triggerAtMillis, WINDOW_EXACT, WINDOW_EXACT, operation, null, null);
    }

    public void setAlarmClock(AlarmClockInfo info, PendingIntent operation) {
        setImpl(RTC_WAKEUP, info.getTriggerTime(), WINDOW_EXACT, WINDOW_EXACT, operation, null, info);
    }

    public void set(int type, long triggerAtMillis, long windowMillis, long intervalMillis, PendingIntent operation, WorkSource workSource) {
        setImpl(type, triggerAtMillis, windowMillis, intervalMillis, operation, workSource, null);
    }

    private void setImpl(int type, long triggerAtMillis, long windowMillis, long intervalMillis, PendingIntent operation, WorkSource workSource, AlarmClockInfo alarmClock) {
        if (triggerAtMillis < WINDOW_EXACT) {
            triggerAtMillis = WINDOW_EXACT;
        }
        try {
            this.mService.set(type, triggerAtMillis, windowMillis, intervalMillis, operation, workSource, alarmClock);
        } catch (RemoteException e) {
        }
    }

    public void setInexactRepeating(int type, long triggerAtMillis, long intervalMillis, PendingIntent operation) {
        setImpl(type, triggerAtMillis, WINDOW_HEURISTIC, intervalMillis, operation, null, null);
    }

    public void cancel(PendingIntent operation) {
        try {
            this.mService.remove(operation);
        } catch (RemoteException e) {
        }
    }

    public void setTime(long millis) {
        try {
            this.mService.setTime(millis);
        } catch (RemoteException e) {
        }
    }

    public void setTimeZone(String timeZone) {
        try {
            this.mService.setTimeZone(timeZone);
        } catch (RemoteException e) {
        }
    }

    public AlarmClockInfo getNextAlarmClock() {
        return getNextAlarmClock(UserHandle.myUserId());
    }

    public AlarmClockInfo getNextAlarmClock(int userId) {
        try {
            return this.mService.getNextAlarmClock(userId);
        } catch (RemoteException e) {
            return null;
        }
    }
}
