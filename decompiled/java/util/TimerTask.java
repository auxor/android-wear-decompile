package java.util;

public abstract class TimerTask implements Runnable {
    boolean cancelled;
    boolean fixedRate;
    final Object lock;
    long period;
    private long scheduledTime;
    long when;

    public abstract void run();

    long getWhen() {
        long j;
        synchronized (this.lock) {
            j = this.when;
        }
        return j;
    }

    void setScheduledTime(long time) {
        synchronized (this.lock) {
            this.scheduledTime = time;
        }
    }

    boolean isScheduled() {
        boolean z;
        synchronized (this.lock) {
            z = this.when > 0 || this.scheduledTime > 0;
        }
        return z;
    }

    protected TimerTask() {
        this.lock = new Object();
    }

    public boolean cancel() {
        boolean willRun = true;
        synchronized (this.lock) {
            if (this.cancelled || this.when <= 0) {
                willRun = false;
            }
            this.cancelled = true;
        }
        return willRun;
    }

    public long scheduledExecutionTime() {
        long j;
        synchronized (this.lock) {
            j = this.scheduledTime;
        }
        return j;
    }
}
