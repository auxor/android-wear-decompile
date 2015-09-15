package com.android.okhttp.internal.spdy;

import com.android.okio.BufferedSource;
import java.io.IOException;
import java.util.List;

public interface PushObserver {
    public static final PushObserver CANCEL;

    boolean onData(int i, BufferedSource bufferedSource, int i2, boolean z) throws IOException;

    boolean onHeaders(int i, List<Header> list, boolean z);

    boolean onRequest(int i, List<Header> list);

    void onReset(int i, ErrorCode errorCode);

    static {
        CANCEL = new PushObserver() {
            public boolean onRequest(int streamId, List<Header> list) {
                return true;
            }

            public boolean onHeaders(int streamId, List<Header> list, boolean last) {
                return true;
            }

            public boolean onData(int streamId, BufferedSource source, int byteCount, boolean last) throws IOException {
                source.skip((long) byteCount);
                return true;
            }

            public void onReset(int streamId, ErrorCode errorCode) {
            }
        };
    }
}
