package android.view;

import android.util.Pools.SynchronizedPool;

class GLES20RecordingCanvas extends GLES20Canvas {
    private static final int POOL_LIMIT = 25;
    private static final SynchronizedPool<GLES20RecordingCanvas> sPool;
    RenderNode mNode;

    static {
        sPool = new SynchronizedPool(POOL_LIMIT);
    }

    private GLES20RecordingCanvas() {
    }

    static GLES20RecordingCanvas obtain(RenderNode node) {
        if (node == null) {
            throw new IllegalArgumentException("node cannot be null");
        }
        GLES20RecordingCanvas canvas = (GLES20RecordingCanvas) sPool.acquire();
        if (canvas == null) {
            canvas = new GLES20RecordingCanvas();
        }
        canvas.mNode = node;
        return canvas;
    }

    void recycle() {
        this.mNode = null;
        sPool.release(this);
    }

    long finishRecording() {
        return GLES20Canvas.nFinishRecording(this.mRenderer);
    }

    public boolean isRecordingFor(Object o) {
        return o == this.mNode;
    }
}
