package android.media;

import android.os.Handler;
import android.os.Looper;
import android.view.Surface;
import com.android.internal.telephony.RILConstants;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.PasswordEntryKeyboard;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import javax.microedition.khronos.opengles.GL10;

public class ImageReader implements AutoCloseable {
    private static final int ACQUIRE_MAX_IMAGES = 2;
    private static final int ACQUIRE_NO_BUFS = 1;
    private static final int ACQUIRE_SUCCESS = 0;
    private final int mFormat;
    private final int mHeight;
    private OnImageAvailableListener mListener;
    private ListenerHandler mListenerHandler;
    private final Object mListenerLock;
    private final int mMaxImages;
    private long mNativeContext;
    private final int mNumPlanes;
    private final Surface mSurface;
    private final int mWidth;

    private final class ListenerHandler extends Handler {
        final /* synthetic */ ImageReader this$0;

        public ListenerHandler(android.media.ImageReader r1, android.os.Looper r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.ImageReader.ListenerHandler.<init>(android.media.ImageReader, android.os.Looper):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.ImageReader.ListenerHandler.<init>(android.media.ImageReader, android.os.Looper):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.media.ImageReader.ListenerHandler.<init>(android.media.ImageReader, android.os.Looper):void");
        }

        public void handleMessage(android.os.Message r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.ImageReader.ListenerHandler.handleMessage(android.os.Message):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.ImageReader.ListenerHandler.handleMessage(android.os.Message):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.media.ImageReader.ListenerHandler.handleMessage(android.os.Message):void");
        }
    }

    public interface OnImageAvailableListener {
        void onImageAvailable(ImageReader imageReader);
    }

    private class SurfaceImage extends Image {
        private int mHeight;
        private boolean mIsImageValid;
        private long mLockedBuffer;
        private SurfacePlane[] mPlanes;
        private long mTimestamp;
        private int mWidth;
        final /* synthetic */ ImageReader this$0;

        private class SurfacePlane extends Image$Plane {
            private ByteBuffer mBuffer;
            private final int mIndex;
            private final int mPixelStride;
            private final int mRowStride;
            final /* synthetic */ SurfaceImage this$1;

            private SurfacePlane(android.media.ImageReader.SurfaceImage r1, int r2, int r3, int r4) {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.ImageReader.SurfaceImage.SurfacePlane.<init>(android.media.ImageReader$SurfaceImage, int, int, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.ImageReader.SurfaceImage.SurfacePlane.<init>(android.media.ImageReader$SurfaceImage, int, int, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.media.ImageReader.SurfaceImage.SurfacePlane.<init>(android.media.ImageReader$SurfaceImage, int, int, int):void");
            }

            private void clearBuffer() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.ImageReader.SurfaceImage.SurfacePlane.clearBuffer():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.ImageReader.SurfaceImage.SurfacePlane.clearBuffer():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.media.ImageReader.SurfaceImage.SurfacePlane.clearBuffer():void");
            }

            public java.nio.ByteBuffer getBuffer() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.ImageReader.SurfaceImage.SurfacePlane.getBuffer():java.nio.ByteBuffer
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.ImageReader.SurfaceImage.SurfacePlane.getBuffer():java.nio.ByteBuffer
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.media.ImageReader.SurfaceImage.SurfacePlane.getBuffer():java.nio.ByteBuffer");
            }

            public int getPixelStride() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.ImageReader.SurfaceImage.SurfacePlane.getPixelStride():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.ImageReader.SurfaceImage.SurfacePlane.getPixelStride():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.media.ImageReader.SurfaceImage.SurfacePlane.getPixelStride():int");
            }

            public int getRowStride() {
                /* JADX: method processing error */
/*
                Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.media.ImageReader.SurfaceImage.SurfacePlane.getRowStride():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.media.ImageReader.SurfaceImage.SurfacePlane.getRowStride():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
                /*
                // Can't load method instructions.
                */
                throw new UnsupportedOperationException("Method not decompiled: android.media.ImageReader.SurfaceImage.SurfacePlane.getRowStride():int");
            }
        }

        private native synchronized SurfacePlane nativeCreatePlane(int i, int i2);

        private native synchronized int nativeGetHeight();

        private native synchronized int nativeGetWidth();

        private native synchronized ByteBuffer nativeImageGetBuffer(int i, int i2);

        public SurfaceImage(ImageReader imageReader) {
            this.this$0 = imageReader;
            this.mHeight = -1;
            this.mWidth = -1;
            this.mIsImageValid = false;
        }

        public void close() {
            if (this.mIsImageValid) {
                this.this$0.releaseImage(this);
            }
        }

        public ImageReader getReader() {
            return this.this$0;
        }

        public int getFormat() {
            if (this.mIsImageValid) {
                return this.this$0.mFormat;
            }
            throw new IllegalStateException("Image is already released");
        }

        public int getWidth() {
            if (this.mIsImageValid) {
                if (this.mWidth == -1) {
                    this.mWidth = getFormat() == GL10.GL_DEPTH_BUFFER_BIT ? this.this$0.getWidth() : nativeGetWidth();
                }
                return this.mWidth;
            }
            throw new IllegalStateException("Image is already released");
        }

        public int getHeight() {
            if (this.mIsImageValid) {
                if (this.mHeight == -1) {
                    this.mHeight = getFormat() == GL10.GL_DEPTH_BUFFER_BIT ? this.this$0.getHeight() : nativeGetHeight();
                }
                return this.mHeight;
            }
            throw new IllegalStateException("Image is already released");
        }

        public long getTimestamp() {
            if (this.mIsImageValid) {
                return this.mTimestamp;
            }
            throw new IllegalStateException("Image is already released");
        }

        public Image$Plane[] getPlanes() {
            if (this.mIsImageValid) {
                return (Image$Plane[]) this.mPlanes.clone();
            }
            throw new IllegalStateException("Image is already released");
        }

        protected final void finalize() throws Throwable {
            try {
                close();
            } finally {
                super.finalize();
            }
        }

        private void setImageValid(boolean isValid) {
            this.mIsImageValid = isValid;
        }

        private boolean isImageValid() {
            return this.mIsImageValid;
        }

        private void clearSurfacePlanes() {
            if (this.mIsImageValid) {
                for (int i = 0; i < this.mPlanes.length; i += ImageReader.ACQUIRE_NO_BUFS) {
                    if (this.mPlanes[i] != null) {
                        this.mPlanes[i].clearBuffer();
                        this.mPlanes[i] = null;
                    }
                }
            }
        }

        private void createSurfacePlanes() {
            this.mPlanes = new SurfacePlane[this.this$0.mNumPlanes];
            for (int i = 0; i < this.this$0.mNumPlanes; i += ImageReader.ACQUIRE_NO_BUFS) {
                this.mPlanes[i] = nativeCreatePlane(i, this.this$0.mFormat);
            }
        }
    }

    private static native void nativeClassInit();

    private native synchronized void nativeClose();

    private native synchronized Surface nativeGetSurface();

    private native synchronized int nativeImageSetup(Image image);

    private native synchronized void nativeInit(Object obj, int i, int i2, int i3, int i4);

    private native synchronized void nativeReleaseImage(Image image);

    public static ImageReader newInstance(int width, int height, int format, int maxImages) {
        return new ImageReader(width, height, format, maxImages);
    }

    protected ImageReader(int width, int height, int format, int maxImages) {
        this.mListenerLock = new Object();
        this.mWidth = width;
        this.mHeight = height;
        this.mFormat = format;
        this.mMaxImages = maxImages;
        if (width < ACQUIRE_NO_BUFS || height < ACQUIRE_NO_BUFS) {
            throw new IllegalArgumentException("The image dimensions must be positive");
        } else if (this.mMaxImages < ACQUIRE_NO_BUFS) {
            throw new IllegalArgumentException("Maximum outstanding image count must be at least 1");
        } else if (format == 17) {
            throw new IllegalArgumentException("NV21 format is not supported");
        } else {
            this.mNumPlanes = getNumPlanesFromFormat();
            nativeInit(new WeakReference(this), width, height, format, maxImages);
            this.mSurface = nativeGetSurface();
        }
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public int getImageFormat() {
        return this.mFormat;
    }

    public int getMaxImages() {
        return this.mMaxImages;
    }

    public Surface getSurface() {
        return this.mSurface;
    }

    public Image acquireLatestImage() {
        Image image = acquireNextImage();
        if (image == null) {
            return null;
        }
        while (true) {
            Image next = acquireNextImageNoThrowISE();
            if (next == null) {
                break;
            }
            try {
                image.close();
                image = next;
            } catch (Throwable th) {
                if (image != null) {
                    image.close();
                }
            }
        }
        Image result = image;
        image = null;
        if (image == null) {
            return result;
        }
        image.close();
        return result;
    }

    public Image acquireNextImageNoThrowISE() {
        SurfaceImage si = new SurfaceImage(this);
        return acquireNextSurfaceImage(si) == 0 ? si : null;
    }

    private int acquireNextSurfaceImage(SurfaceImage si) {
        int status = nativeImageSetup(si);
        switch (status) {
            case GL10.GL_POINTS /*0*/:
                si.createSurfacePlanes();
                si.setImageValid(true);
                break;
            case ACQUIRE_NO_BUFS /*1*/:
            case ACQUIRE_MAX_IMAGES /*2*/:
                break;
            default:
                throw new AssertionError("Unknown nativeImageSetup return code " + status);
        }
        return status;
    }

    public Image acquireNextImage() {
        SurfaceImage si = new SurfaceImage(this);
        int status = acquireNextSurfaceImage(si);
        switch (status) {
            case GL10.GL_POINTS /*0*/:
                return si;
            case ACQUIRE_NO_BUFS /*1*/:
                return null;
            case ACQUIRE_MAX_IMAGES /*2*/:
                Object[] objArr = new Object[ACQUIRE_NO_BUFS];
                objArr[0] = Integer.valueOf(this.mMaxImages);
                throw new IllegalStateException(String.format("maxImages (%d) has already been acquired, call #close before acquiring more.", objArr));
            default:
                throw new AssertionError("Unknown nativeImageSetup return code " + status);
        }
    }

    private void releaseImage(Image i) {
        if (i instanceof SurfaceImage) {
            SurfaceImage si = (SurfaceImage) i;
            if (si.getReader() != this) {
                throw new IllegalArgumentException("This image was not produced by this ImageReader");
            }
            si.clearSurfacePlanes();
            nativeReleaseImage(i);
            si.setImageValid(false);
            return;
        }
        throw new IllegalArgumentException("This image was not produced by an ImageReader");
    }

    public void setOnImageAvailableListener(OnImageAvailableListener listener, Handler handler) {
        synchronized (this.mListenerLock) {
            if (listener != null) {
                Looper looper = handler != null ? handler.getLooper() : Looper.myLooper();
                if (looper == null) {
                    throw new IllegalArgumentException("handler is null but the current thread is not a looper");
                }
                if (this.mListenerHandler == null || this.mListenerHandler.getLooper() != looper) {
                    this.mListenerHandler = new ListenerHandler(this, looper);
                }
                this.mListener = listener;
            } else {
                this.mListener = null;
                this.mListenerHandler = null;
            }
        }
    }

    public void close() {
        setOnImageAvailableListener(null, null);
        nativeClose();
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    private int getNumPlanesFromFormat() {
        switch (this.mFormat) {
            case ACQUIRE_NO_BUFS /*1*/:
            case ACQUIRE_MAX_IMAGES /*2*/:
            case GL10.GL_LINE_STRIP /*3*/:
            case GL10.GL_TRIANGLES /*4*/:
            case LockPatternUtils.FAILED_ATTEMPTS_BEFORE_RESET /*20*/:
            case PasswordEntryKeyboard.KEYCODE_SPACE /*32*/:
            case RILConstants.RIL_REQUEST_SMS_ACKNOWLEDGE /*37*/:
            case GL10.GL_DEPTH_BUFFER_BIT /*256*/:
            case 538982489:
            case 540422489:
                return ACQUIRE_NO_BUFS;
            case RILConstants.RIL_RESTRICTED_STATE_PS_ALL /*16*/:
                return ACQUIRE_MAX_IMAGES;
            case RILConstants.RIL_REQUEST_UDUB /*17*/:
            case RILConstants.RIL_REQUEST_QUERY_CALL_WAITING /*35*/:
            case 842094169:
                return 3;
            default:
                Object[] objArr = new Object[ACQUIRE_NO_BUFS];
                objArr[0] = Integer.valueOf(this.mFormat);
                throw new UnsupportedOperationException(String.format("Invalid format specified %d", objArr));
        }
    }

    private static void postEventFromNative(Object selfRef) {
        ImageReader ir = (ImageReader) ((WeakReference) selfRef).get();
        if (ir != null) {
            Handler handler;
            synchronized (ir.mListenerLock) {
                handler = ir.mListenerHandler;
            }
            if (handler != null) {
                handler.sendEmptyMessage(0);
            }
        }
    }

    static {
        System.loadLibrary("media_jni");
        nativeClassInit();
    }
}
