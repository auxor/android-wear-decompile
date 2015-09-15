package android.os;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class UEventObserver {
    private static final boolean DEBUG = false;
    private static final String TAG = "UEventObserver";
    private static UEventThread sThread;

    public static final class UEvent {
        private final HashMap<String, String> mMap;

        public UEvent(String message) {
            this.mMap = new HashMap();
            int offset = 0;
            int length = message.length();
            while (offset < length) {
                int equals = message.indexOf(61, offset);
                int at = message.indexOf(0, offset);
                if (at >= 0) {
                    if (equals > offset && equals < at) {
                        this.mMap.put(message.substring(offset, equals), message.substring(equals + 1, at));
                    }
                    offset = at + 1;
                } else {
                    return;
                }
            }
        }

        public String get(String key) {
            return (String) this.mMap.get(key);
        }

        public String get(String key, String defaultValue) {
            String result = (String) this.mMap.get(key);
            return result == null ? defaultValue : result;
        }

        public String toString() {
            return this.mMap.toString();
        }
    }

    private static final class UEventThread extends Thread {
        private final ArrayList<Object> mKeysAndObservers;
        private final ArrayList<UEventObserver> mTempObserversToSignal;

        public UEventThread() {
            super(UEventObserver.TAG);
            this.mKeysAndObservers = new ArrayList();
            this.mTempObserversToSignal = new ArrayList();
        }

        public void run() {
            UEventObserver.nativeSetup();
            while (true) {
                String message = UEventObserver.nativeWaitForNextEvent();
                if (message != null) {
                    sendEvent(message);
                }
            }
        }

        private void sendEvent(String message) {
            synchronized (this.mKeysAndObservers) {
                int i;
                int N = this.mKeysAndObservers.size();
                for (i = 0; i < N; i += 2) {
                    if (message.contains((String) this.mKeysAndObservers.get(i))) {
                        this.mTempObserversToSignal.add((UEventObserver) this.mKeysAndObservers.get(i + 1));
                    }
                }
            }
            if (!this.mTempObserversToSignal.isEmpty()) {
                UEvent event = new UEvent(message);
                N = this.mTempObserversToSignal.size();
                for (i = 0; i < N; i++) {
                    ((UEventObserver) this.mTempObserversToSignal.get(i)).onUEvent(event);
                }
                this.mTempObserversToSignal.clear();
            }
        }

        public void addObserver(String match, UEventObserver observer) {
            synchronized (this.mKeysAndObservers) {
                this.mKeysAndObservers.add(match);
                this.mKeysAndObservers.add(observer);
                UEventObserver.nativeAddMatch(match);
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void removeObserver(android.os.UEventObserver r6) {
            /*
            r5 = this;
            r3 = r5.mKeysAndObservers;
            monitor-enter(r3);
            r0 = 0;
        L_0x0004:
            r2 = r5.mKeysAndObservers;	 Catch:{ all -> 0x0029 }
            r2 = r2.size();	 Catch:{ all -> 0x0029 }
            if (r0 >= r2) goto L_0x002f;
        L_0x000c:
            r2 = r5.mKeysAndObservers;	 Catch:{ all -> 0x0029 }
            r4 = r0 + 1;
            r2 = r2.get(r4);	 Catch:{ all -> 0x0029 }
            if (r2 != r6) goto L_0x002c;
        L_0x0016:
            r2 = r5.mKeysAndObservers;	 Catch:{ all -> 0x0029 }
            r4 = r0 + 1;
            r2.remove(r4);	 Catch:{ all -> 0x0029 }
            r2 = r5.mKeysAndObservers;	 Catch:{ all -> 0x0029 }
            r1 = r2.remove(r0);	 Catch:{ all -> 0x0029 }
            r1 = (java.lang.String) r1;	 Catch:{ all -> 0x0029 }
            android.os.UEventObserver.nativeRemoveMatch(r1);	 Catch:{ all -> 0x0029 }
            goto L_0x0004;
        L_0x0029:
            r2 = move-exception;
            monitor-exit(r3);	 Catch:{ all -> 0x0029 }
            throw r2;
        L_0x002c:
            r0 = r0 + 2;
            goto L_0x0004;
        L_0x002f:
            monitor-exit(r3);	 Catch:{ all -> 0x0029 }
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.os.UEventObserver.UEventThread.removeObserver(android.os.UEventObserver):void");
        }
    }

    private static native void nativeAddMatch(String str);

    private static native void nativeRemoveMatch(String str);

    private static native void nativeSetup();

    private static native String nativeWaitForNextEvent();

    public abstract void onUEvent(UEvent uEvent);

    protected void finalize() throws Throwable {
        try {
            stopObserving();
        } finally {
            super.finalize();
        }
    }

    private static UEventThread getThread() {
        UEventThread uEventThread;
        synchronized (UEventObserver.class) {
            if (sThread == null) {
                sThread = new UEventThread();
                sThread.start();
            }
            uEventThread = sThread;
        }
        return uEventThread;
    }

    private static UEventThread peekThread() {
        UEventThread uEventThread;
        synchronized (UEventObserver.class) {
            uEventThread = sThread;
        }
        return uEventThread;
    }

    public final void startObserving(String match) {
        if (match == null || match.isEmpty()) {
            throw new IllegalArgumentException("match substring must be non-empty");
        }
        getThread().addObserver(match, this);
    }

    public final void stopObserving() {
        UEventThread t = getThread();
        if (t != null) {
            t.removeObserver(this);
        }
    }
}
