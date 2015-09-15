package android.os;

import com.google.android.collect.Maps;
import java.util.HashMap;

public class SystemService {
    private static Object sPropertyLock;
    private static HashMap<String, State> sStates;

    public enum State {
        RUNNING("running"),
        STOPPING("stopping"),
        STOPPED("stopped"),
        RESTARTING("restarting");

        private State(String state) {
            SystemService.sStates.put(state, this);
        }
    }

    static {
        sStates = Maps.newHashMap();
        sPropertyLock = new Object();
        SystemProperties.addChangeCallback(new Runnable() {
            public void run() {
                synchronized (SystemService.sPropertyLock) {
                    SystemService.sPropertyLock.notifyAll();
                }
            }
        });
    }

    public static void start(String name) {
        SystemProperties.set("ctl.start", name);
    }

    public static void stop(String name) {
        SystemProperties.set("ctl.stop", name);
    }

    public static void restart(String name) {
        SystemProperties.set("ctl.restart", name);
    }

    public static State getState(String service) {
        State state = (State) sStates.get(SystemProperties.get("init.svc." + service));
        return state != null ? state : State.STOPPED;
    }

    public static boolean isStopped(String service) {
        return State.STOPPED.equals(getState(service));
    }

    public static boolean isRunning(String service) {
        return State.RUNNING.equals(getState(service));
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void waitForState(java.lang.String r8, android.os.SystemService.State r9, long r10) throws java.util.concurrent.TimeoutException {
        /*
        r4 = android.os.SystemClock.elapsedRealtime();
        r2 = r4 + r10;
    L_0x0006:
        r4 = sPropertyLock;
        monitor-enter(r4);
        r0 = getState(r8);	 Catch:{ all -> 0x0055 }
        r1 = r9.equals(r0);	 Catch:{ all -> 0x0055 }
        if (r1 == 0) goto L_0x0015;
    L_0x0013:
        monitor-exit(r4);	 Catch:{ all -> 0x0055 }
        return;
    L_0x0015:
        r6 = android.os.SystemClock.elapsedRealtime();	 Catch:{ all -> 0x0055 }
        r1 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1));
        if (r1 < 0) goto L_0x0058;
    L_0x001d:
        r1 = new java.util.concurrent.TimeoutException;	 Catch:{ all -> 0x0055 }
        r5 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0055 }
        r5.<init>();	 Catch:{ all -> 0x0055 }
        r6 = "Service ";
        r5 = r5.append(r6);	 Catch:{ all -> 0x0055 }
        r5 = r5.append(r8);	 Catch:{ all -> 0x0055 }
        r6 = " currently ";
        r5 = r5.append(r6);	 Catch:{ all -> 0x0055 }
        r5 = r5.append(r0);	 Catch:{ all -> 0x0055 }
        r6 = "; waited ";
        r5 = r5.append(r6);	 Catch:{ all -> 0x0055 }
        r5 = r5.append(r10);	 Catch:{ all -> 0x0055 }
        r6 = "ms for ";
        r5 = r5.append(r6);	 Catch:{ all -> 0x0055 }
        r5 = r5.append(r9);	 Catch:{ all -> 0x0055 }
        r5 = r5.toString();	 Catch:{ all -> 0x0055 }
        r1.<init>(r5);	 Catch:{ all -> 0x0055 }
        throw r1;	 Catch:{ all -> 0x0055 }
    L_0x0055:
        r1 = move-exception;
        monitor-exit(r4);	 Catch:{ all -> 0x0055 }
        throw r1;
    L_0x0058:
        r1 = sPropertyLock;	 Catch:{ InterruptedException -> 0x005f }
        r1.wait(r10);	 Catch:{ InterruptedException -> 0x005f }
    L_0x005d:
        monitor-exit(r4);	 Catch:{ all -> 0x0055 }
        goto L_0x0006;
    L_0x005f:
        r1 = move-exception;
        goto L_0x005d;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.os.SystemService.waitForState(java.lang.String, android.os.SystemService$State, long):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void waitForAnyStopped(java.lang.String... r7) {
        /*
    L_0x0000:
        r5 = sPropertyLock;
        monitor-enter(r5);
        r0 = r7;
        r2 = r0.length;	 Catch:{ all -> 0x0022 }
        r1 = 0;
    L_0x0006:
        if (r1 >= r2) goto L_0x001b;
    L_0x0008:
        r3 = r0[r1];	 Catch:{ all -> 0x0022 }
        r4 = android.os.SystemService.State.STOPPED;	 Catch:{ all -> 0x0022 }
        r6 = getState(r3);	 Catch:{ all -> 0x0022 }
        r4 = r4.equals(r6);	 Catch:{ all -> 0x0022 }
        if (r4 == 0) goto L_0x0018;
    L_0x0016:
        monitor-exit(r5);	 Catch:{ all -> 0x0022 }
        return;
    L_0x0018:
        r1 = r1 + 1;
        goto L_0x0006;
    L_0x001b:
        r4 = sPropertyLock;	 Catch:{ InterruptedException -> 0x0025 }
        r4.wait();	 Catch:{ InterruptedException -> 0x0025 }
    L_0x0020:
        monitor-exit(r5);	 Catch:{ all -> 0x0022 }
        goto L_0x0000;
    L_0x0022:
        r4 = move-exception;
        monitor-exit(r5);	 Catch:{ all -> 0x0022 }
        throw r4;
    L_0x0025:
        r4 = move-exception;
        goto L_0x0020;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.os.SystemService.waitForAnyStopped(java.lang.String[]):void");
    }
}
