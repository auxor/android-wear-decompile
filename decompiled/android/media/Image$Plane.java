package android.media;

import java.nio.ByteBuffer;

public abstract class Image$Plane {
    public abstract ByteBuffer getBuffer();

    public abstract int getPixelStride();

    public abstract int getRowStride();

    protected Image$Plane() {
    }
}
