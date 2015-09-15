package org.apache.harmony.dalvik.ddmc;

import java.util.HashMap;

public class DdmServer {
    public static final int CLIENT_PROTOCOL_VERSION = 1;
    private static final int CONNECTED = 1;
    private static final int DISCONNECTED = 2;
    private static HashMap<Integer, ChunkHandler> mHandlerMap;
    private static volatile boolean mRegistrationComplete;
    private static boolean mRegistrationTimedOut;

    private static native void nativeSendChunk(int i, byte[] bArr, int i2, int i3);

    static {
        mHandlerMap = new HashMap();
        mRegistrationComplete = false;
        mRegistrationTimedOut = false;
    }

    private DdmServer() {
    }

    public static void registerHandler(int type, ChunkHandler handler) {
        if (handler == null) {
            throw new NullPointerException("handler == null");
        }
        synchronized (mHandlerMap) {
            if (mHandlerMap.get(Integer.valueOf(type)) != null) {
                throw new RuntimeException("type " + Integer.toHexString(type) + " already registered");
            }
            mHandlerMap.put(Integer.valueOf(type), handler);
        }
    }

    public static ChunkHandler unregisterHandler(int type) {
        ChunkHandler chunkHandler;
        synchronized (mHandlerMap) {
            chunkHandler = (ChunkHandler) mHandlerMap.remove(Integer.valueOf(type));
        }
        return chunkHandler;
    }

    public static void registrationComplete() {
        synchronized (mHandlerMap) {
            mRegistrationComplete = true;
            mHandlerMap.notifyAll();
        }
    }

    public static void sendChunk(Chunk chunk) {
        nativeSendChunk(chunk.type, chunk.data, chunk.offset, chunk.length);
    }

    private static void broadcast(int event) {
        synchronized (mHandlerMap) {
            for (ChunkHandler handler : mHandlerMap.values()) {
                switch (event) {
                    case CONNECTED /*1*/:
                        handler.connected();
                        continue;
                    case DISCONNECTED /*2*/:
                        handler.disconnected();
                        continue;
                    default:
                        throw new UnsupportedOperationException();
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static org.apache.harmony.dalvik.ddmc.Chunk dispatch(int r8, byte[] r9, int r10, int r11) {
        /*
        r4 = mHandlerMap;
        monitor-enter(r4);
    L_0x0003:
        r3 = mRegistrationComplete;	 Catch:{ all -> 0x001a }
        if (r3 != 0) goto L_0x001f;
    L_0x0007:
        r3 = mRegistrationTimedOut;	 Catch:{ all -> 0x001a }
        if (r3 != 0) goto L_0x001f;
    L_0x000b:
        r3 = mHandlerMap;	 Catch:{ InterruptedException -> 0x001d }
        r6 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r3.wait(r6);	 Catch:{ InterruptedException -> 0x001d }
        r3 = mRegistrationComplete;	 Catch:{ all -> 0x001a }
        if (r3 != 0) goto L_0x0003;
    L_0x0016:
        r3 = 1;
        mRegistrationTimedOut = r3;	 Catch:{ all -> 0x001a }
        goto L_0x0003;
    L_0x001a:
        r3 = move-exception;
        monitor-exit(r4);	 Catch:{ all -> 0x001a }
        throw r3;
    L_0x001d:
        r2 = move-exception;
        goto L_0x0003;
    L_0x001f:
        r3 = mHandlerMap;	 Catch:{ all -> 0x001a }
        r5 = java.lang.Integer.valueOf(r8);	 Catch:{ all -> 0x001a }
        r1 = r3.get(r5);	 Catch:{ all -> 0x001a }
        r1 = (org.apache.harmony.dalvik.ddmc.ChunkHandler) r1;	 Catch:{ all -> 0x001a }
        monitor-exit(r4);	 Catch:{ all -> 0x001a }
        if (r1 != 0) goto L_0x0030;
    L_0x002e:
        r3 = 0;
    L_0x002f:
        return r3;
    L_0x0030:
        r0 = new org.apache.harmony.dalvik.ddmc.Chunk;
        r0.<init>(r8, r9, r10, r11);
        r3 = r1.handleChunk(r0);
        goto L_0x002f;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.harmony.dalvik.ddmc.DdmServer.dispatch(int, byte[], int, int):org.apache.harmony.dalvik.ddmc.Chunk");
    }
}
