package android.app;

public abstract class ActivityManagerInternal {
    public abstract void onWakefulnessChanged(int i);

    public abstract int startIsolatedProcess(String str, String[] strArr, String str2, String str3, int i, Runnable runnable);
}
