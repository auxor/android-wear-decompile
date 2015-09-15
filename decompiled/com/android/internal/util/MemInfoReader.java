package com.android.internal.util;

import android.media.session.PlaybackState;
import android.os.Debug;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;

public final class MemInfoReader {
    final long[] mInfos;

    public MemInfoReader() {
        this.mInfos = new long[13];
    }

    public void readMemInfo() {
        ThreadPolicy savedPolicy = StrictMode.allowThreadDiskReads();
        try {
            Debug.getMemInfo(this.mInfos);
        } finally {
            StrictMode.setThreadPolicy(savedPolicy);
        }
    }

    public long getTotalSize() {
        return this.mInfos[0] * PlaybackState.ACTION_PLAY_FROM_MEDIA_ID;
    }

    public long getFreeSize() {
        return this.mInfos[1] * PlaybackState.ACTION_PLAY_FROM_MEDIA_ID;
    }

    public long getCachedSize() {
        return getCachedSizeKb() * PlaybackState.ACTION_PLAY_FROM_MEDIA_ID;
    }

    public long getKernelUsedSize() {
        return getKernelUsedSizeKb() * PlaybackState.ACTION_PLAY_FROM_MEDIA_ID;
    }

    public long getTotalSizeKb() {
        return this.mInfos[0];
    }

    public long getFreeSizeKb() {
        return this.mInfos[1];
    }

    public long getCachedSizeKb() {
        return (this.mInfos[2] + this.mInfos[3]) - this.mInfos[9];
    }

    public long getKernelUsedSizeKb() {
        return (((this.mInfos[4] + this.mInfos[5]) + this.mInfos[10]) + this.mInfos[11]) + this.mInfos[12];
    }

    public long getSwapTotalSizeKb() {
        return this.mInfos[6];
    }

    public long getSwapFreeSizeKb() {
        return this.mInfos[7];
    }

    public long getZramTotalSizeKb() {
        return this.mInfos[8];
    }

    public long[] getRawInfo() {
        return this.mInfos;
    }
}
