package android.app.job;

import java.util.List;

public abstract class JobScheduler {
    public static final int RESULT_FAILURE = 0;
    public static final int RESULT_SUCCESS = 1;

    public abstract void cancel(int i);

    public abstract void cancelAll();

    public abstract List<JobInfo> getAllPendingJobs();

    public abstract int schedule(JobInfo jobInfo);
}
