package dalvik.system;

import java.io.File;

public final class ZygoteHooks {
    private long token;

    private static native void nativePostForkChild(long j, int i, String str);

    private static native long nativePreFork();

    public void preFork() {
        Daemons.stop();
        waitUntilAllThreadsStopped();
        this.token = nativePreFork();
    }

    public void postForkChild(int debugFlags, String instructionSet) {
        nativePostForkChild(this.token, debugFlags, instructionSet);
    }

    public void postForkCommon() {
        Daemons.start();
    }

    private static void waitUntilAllThreadsStopped() {
        File tasks = new File("/proc/self/task");
        while (tasks.list().length > 1) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
    }
}
