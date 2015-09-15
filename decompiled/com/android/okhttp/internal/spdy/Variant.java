package com.android.okhttp.internal.spdy;

import com.android.okhttp.Protocol;
import com.android.okio.BufferedSink;
import com.android.okio.BufferedSource;

interface Variant {
    Protocol getProtocol();

    int maxFrameSize();

    FrameReader newReader(BufferedSource bufferedSource, boolean z);

    FrameWriter newWriter(BufferedSink bufferedSink, boolean z);
}
