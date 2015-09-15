package android.hardware.camera2;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.location.Location;
import android.media.Image;
import android.media.Image.Plane;
import android.os.SystemClock;
import android.util.Size;
import android.view.inputmethod.EditorInfo;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public final class DngCreator implements AutoCloseable {
    private static final int BYTES_PER_RGB_PIX = 3;
    private static final int DEFAULT_PIXEL_STRIDE = 2;
    private static final String GPS_DATE_FORMAT_STR = "yyyy:MM:dd";
    private static final String GPS_LAT_REF_NORTH = "N";
    private static final String GPS_LAT_REF_SOUTH = "S";
    private static final String GPS_LONG_REF_EAST = "E";
    private static final String GPS_LONG_REF_WEST = "W";
    public static final int MAX_THUMBNAIL_DIMENSION = 256;
    private static final String TAG = "DngCreator";
    private static final String TIFF_DATETIME_FORMAT = "yyyy:MM:dd kk:mm:ss";
    private static final DateFormat sDateTimeStampFormat;
    private static final DateFormat sExifGPSDateStamp;
    private final Calendar mGPSTimeStampCalendar;
    private long mNativeContext;

    private static native void nativeClassInit();

    private native synchronized void nativeDestroy();

    private native synchronized void nativeInit(CameraMetadataNative cameraMetadataNative, CameraMetadataNative cameraMetadataNative2, String str);

    private native synchronized void nativeSetDescription(String str);

    private native synchronized void nativeSetGpsTags(int[] iArr, String str, int[] iArr2, String str2, String str3, int[] iArr3);

    private native synchronized void nativeSetOrientation(int i);

    private native synchronized void nativeSetThumbnail(ByteBuffer byteBuffer, int i, int i2);

    private native synchronized void nativeWriteImage(OutputStream outputStream, int i, int i2, ByteBuffer byteBuffer, int i3, int i4, long j, boolean z) throws IOException;

    private native synchronized void nativeWriteInputStream(OutputStream outputStream, InputStream inputStream, int i, int i2, long j) throws IOException;

    public DngCreator(CameraCharacteristics characteristics, CaptureResult metadata) {
        this.mGPSTimeStampCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        if (characteristics == null || metadata == null) {
            throw new IllegalArgumentException("Null argument to DngCreator constructor");
        }
        long currentTime = System.currentTimeMillis();
        long bootTimeMillis = currentTime - SystemClock.elapsedRealtime();
        Long timestamp = (Long) metadata.get(CaptureResult.SENSOR_TIMESTAMP);
        long captureTime = currentTime;
        if (timestamp != null) {
            captureTime = (timestamp.longValue() / 1000000) + bootTimeMillis;
        }
        nativeInit(characteristics.getNativeCopy(), metadata.getNativeCopy(), sDateTimeStampFormat.format(Long.valueOf(captureTime)));
    }

    public DngCreator setOrientation(int orientation) {
        if (orientation < 0 || orientation > 8) {
            throw new IllegalArgumentException("Orientation " + orientation + " is not a valid EXIF orientation value");
        }
        nativeSetOrientation(orientation);
        return this;
    }

    public DngCreator setThumbnail(Bitmap pixels) {
        if (pixels == null) {
            throw new IllegalArgumentException("Null argument to setThumbnail");
        }
        int width = pixels.getWidth();
        int height = pixels.getHeight();
        if (width > MAX_THUMBNAIL_DIMENSION || height > MAX_THUMBNAIL_DIMENSION) {
            throw new IllegalArgumentException("Thumbnail dimensions width,height (" + width + "," + height + ") too large, dimensions must be smaller than " + MAX_THUMBNAIL_DIMENSION);
        }
        nativeSetThumbnail(convertToRGB(pixels), width, height);
        return this;
    }

    public DngCreator setThumbnail(Image pixels) {
        if (pixels == null) {
            throw new IllegalArgumentException("Null argument to setThumbnail");
        }
        int format = pixels.getFormat();
        if (format != 35) {
            throw new IllegalArgumentException("Unsupported Image format " + format);
        }
        int width = pixels.getWidth();
        int height = pixels.getHeight();
        if (width > MAX_THUMBNAIL_DIMENSION || height > MAX_THUMBNAIL_DIMENSION) {
            throw new IllegalArgumentException("Thumbnail dimensions width,height (" + width + "," + height + ") too large, dimensions must be smaller than " + MAX_THUMBNAIL_DIMENSION);
        }
        nativeSetThumbnail(convertToRGB(pixels), width, height);
        return this;
    }

    public DngCreator setLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Null location passed to setLocation");
        }
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        long time = location.getTime();
        int[] latTag = toExifLatLong(latitude);
        int[] longTag = toExifLatLong(longitude);
        String latRef = latitude >= 0.0d ? GPS_LAT_REF_NORTH : GPS_LAT_REF_SOUTH;
        String longRef = longitude >= 0.0d ? GPS_LONG_REF_EAST : GPS_LONG_REF_WEST;
        String dateTag = sExifGPSDateStamp.format(Long.valueOf(time));
        this.mGPSTimeStampCalendar.setTimeInMillis(time);
        nativeSetGpsTags(latTag, latRef, longTag, longRef, dateTag, new int[]{this.mGPSTimeStampCalendar.get(11), 1, this.mGPSTimeStampCalendar.get(12), 1, this.mGPSTimeStampCalendar.get(13), 1});
        return this;
    }

    public DngCreator setDescription(String description) {
        if (description == null) {
            throw new IllegalArgumentException("Null description passed to setDescription.");
        }
        nativeSetDescription(description);
        return this;
    }

    public void writeInputStream(OutputStream dngOutput, Size size, InputStream pixels, long offset) throws IOException {
        if (dngOutput == null) {
            throw new IllegalArgumentException("Null dngOutput passed to writeInputStream");
        } else if (size == null) {
            throw new IllegalArgumentException("Null size passed to writeInputStream");
        } else if (pixels == null) {
            throw new IllegalArgumentException("Null pixels passed to writeInputStream");
        } else if (offset < 0) {
            throw new IllegalArgumentException("Negative offset passed to writeInputStream");
        } else {
            int width = size.getWidth();
            int height = size.getHeight();
            if (width <= 0 || height <= 0) {
                throw new IllegalArgumentException("Size with invalid width, height: (" + width + "," + height + ") passed to writeInputStream");
            }
            nativeWriteInputStream(dngOutput, pixels, width, height, offset);
        }
    }

    public void writeByteBuffer(OutputStream dngOutput, Size size, ByteBuffer pixels, long offset) throws IOException {
        if (dngOutput == null) {
            throw new IllegalArgumentException("Null dngOutput passed to writeByteBuffer");
        } else if (size == null) {
            throw new IllegalArgumentException("Null size passed to writeByteBuffer");
        } else if (pixels == null) {
            throw new IllegalArgumentException("Null pixels passed to writeByteBuffer");
        } else if (offset < 0) {
            throw new IllegalArgumentException("Negative offset passed to writeByteBuffer");
        } else {
            int width = size.getWidth();
            writeByteBuffer(width, size.getHeight(), pixels, dngOutput, DEFAULT_PIXEL_STRIDE, width * DEFAULT_PIXEL_STRIDE, offset);
        }
    }

    public void writeImage(OutputStream dngOutput, Image pixels) throws IOException {
        if (dngOutput == null) {
            throw new IllegalArgumentException("Null dngOutput to writeImage");
        } else if (pixels == null) {
            throw new IllegalArgumentException("Null pixels to writeImage");
        } else {
            int format = pixels.getFormat();
            if (format != 32) {
                throw new IllegalArgumentException("Unsupported image format " + format);
            }
            Plane[] planes = pixels.getPlanes();
            if (planes == null || planes.length <= 0) {
                throw new IllegalArgumentException("Image with no planes passed to writeImage");
            }
            ByteBuffer buf = planes[0].getBuffer();
            writeByteBuffer(pixels.getWidth(), pixels.getHeight(), buf, dngOutput, planes[0].getPixelStride(), planes[0].getRowStride(), 0);
        }
    }

    public void close() {
        nativeDestroy();
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    static {
        sExifGPSDateStamp = new SimpleDateFormat(GPS_DATE_FORMAT_STR);
        sDateTimeStampFormat = new SimpleDateFormat(TIFF_DATETIME_FORMAT);
        sDateTimeStampFormat.setTimeZone(TimeZone.getDefault());
        sExifGPSDateStamp.setTimeZone(TimeZone.getTimeZone("UTC"));
        nativeClassInit();
    }

    private void writeByteBuffer(int width, int height, ByteBuffer pixels, OutputStream dngOutput, int pixelStride, int rowStride, long offset) throws IOException {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Image with invalid width, height: (" + width + "," + height + ") passed to write");
        }
        long capacity = (long) pixels.capacity();
        long totalSize = ((long) (rowStride * height)) + offset;
        if (capacity < totalSize) {
            throw new IllegalArgumentException("Image size " + capacity + " is too small (must be larger than " + totalSize + ")");
        }
        int minRowStride = pixelStride * width;
        if (minRowStride > rowStride) {
            throw new IllegalArgumentException("Invalid image pixel stride, row byte width " + minRowStride + " is too large, expecting " + rowStride);
        }
        pixels.clear();
        nativeWriteImage(dngOutput, width, height, pixels, rowStride, pixelStride, offset, pixels.isDirect());
        pixels.clear();
    }

    private static void yuvToRgb(byte[] yuvData, int outOffset, byte[] rgbOut) {
        float y = (float) (yuvData[0] & EditorInfo.IME_MASK_ACTION);
        float cb = (float) (yuvData[1] & EditorInfo.IME_MASK_ACTION);
        float cr = (float) (yuvData[DEFAULT_PIXEL_STRIDE] & EditorInfo.IME_MASK_ACTION);
        float g = (y - (0.34414f * (cb - 128.0f))) - (0.71414f * (cr - 128.0f));
        float b = y + (1.772f * (cb - 128.0f));
        rgbOut[outOffset] = (byte) ((int) Math.max(0.0f, Math.min(255.0f, y + (1.402f * (cr - 128.0f)))));
        rgbOut[outOffset + 1] = (byte) ((int) Math.max(0.0f, Math.min(255.0f, g)));
        rgbOut[outOffset + DEFAULT_PIXEL_STRIDE] = (byte) ((int) Math.max(0.0f, Math.min(255.0f, b)));
    }

    private static void colorToRgb(int color, int outOffset, byte[] rgbOut) {
        rgbOut[outOffset] = (byte) Color.red(color);
        rgbOut[outOffset + 1] = (byte) Color.green(color);
        rgbOut[outOffset + DEFAULT_PIXEL_STRIDE] = (byte) Color.blue(color);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.nio.ByteBuffer convertToRGB(android.media.Image r28) {
        /*
        r19 = r28.getWidth();
        r6 = r28.getHeight();
        r26 = r19 * 3;
        r26 = r26 * r6;
        r2 = java.nio.ByteBuffer.allocateDirect(r26);
        r26 = r28.getPlanes();
        r27 = 0;
        r23 = r26[r27];
        r26 = r28.getPlanes();
        r27 = 1;
        r12 = r26[r27];
        r26 = r28.getPlanes();
        r27 = 2;
        r17 = r26[r27];
        r20 = r23.getBuffer();
        r9 = r12.getBuffer();
        r14 = r17.getBuffer();
        r20.rewind();
        r9.rewind();
        r14.rewind();
        r24 = r23.getRowStride();
        r18 = r17.getRowStride();
        r13 = r12.getRowStride();
        r22 = r23.getPixelStride();
        r16 = r17.getPixelStride();
        r11 = r12.getPixelStride();
        r26 = 3;
        r0 = r26;
        r0 = new byte[r0];
        r25 = r0;
        r25 = {0, 0, 0};
        r26 = r19 + -1;
        r26 = r26 * r22;
        r26 = r26 + 1;
        r0 = r26;
        r0 = new byte[r0];
        r21 = r0;
        r26 = r19 / 2;
        r26 = r26 + -1;
        r26 = r26 * r11;
        r26 = r26 + 1;
        r0 = r26;
        r10 = new byte[r0];
        r26 = r19 / 2;
        r26 = r26 + -1;
        r26 = r26 * r16;
        r26 = r26 + 1;
        r0 = r26;
        r15 = new byte[r0];
        r26 = r19 * 3;
        r0 = r26;
        r3 = new byte[r0];
        r7 = 0;
    L_0x008b:
        if (r7 >= r6) goto L_0x00e0;
    L_0x008d:
        r4 = r7 / 2;
        r26 = r24 * r7;
        r0 = r20;
        r1 = r26;
        r0.position(r1);
        r20.get(r21);
        r26 = r13 * r4;
        r0 = r26;
        r9.position(r0);
        r9.get(r10);
        r26 = r18 * r4;
        r0 = r26;
        r14.position(r0);
        r14.get(r15);
        r8 = 0;
    L_0x00b0:
        r0 = r19;
        if (r8 >= r0) goto L_0x00da;
    L_0x00b4:
        r5 = r8 / 2;
        r26 = 0;
        r27 = r22 * r8;
        r27 = r21[r27];
        r25[r26] = r27;
        r26 = 1;
        r27 = r11 * r5;
        r27 = r10[r27];
        r25[r26] = r27;
        r26 = 2;
        r27 = r16 * r5;
        r27 = r15[r27];
        r25[r26] = r27;
        r26 = r8 * 3;
        r0 = r25;
        r1 = r26;
        yuvToRgb(r0, r1, r3);
        r8 = r8 + 1;
        goto L_0x00b0;
    L_0x00da:
        r2.put(r3);
        r7 = r7 + 1;
        goto L_0x008b;
    L_0x00e0:
        r20.rewind();
        r9.rewind();
        r14.rewind();
        r2.rewind();
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.hardware.camera2.DngCreator.convertToRGB(android.media.Image):java.nio.ByteBuffer");
    }

    private static ByteBuffer convertToRGB(Bitmap argbBitmap) {
        int width = argbBitmap.getWidth();
        int height = argbBitmap.getHeight();
        ByteBuffer buf = ByteBuffer.allocateDirect((width * BYTES_PER_RGB_PIX) * height);
        int[] pixelRow = new int[width];
        byte[] finalRow = new byte[(width * BYTES_PER_RGB_PIX)];
        for (int i = 0; i < height; i++) {
            argbBitmap.getPixels(pixelRow, 0, width, 0, i, width, 1);
            for (int j = 0; j < width; j++) {
                colorToRgb(pixelRow[j], j * BYTES_PER_RGB_PIX, finalRow);
            }
            buf.put(finalRow);
        }
        buf.rewind();
        return buf;
    }

    private static int[] toExifLatLong(double value) {
        value = Math.abs(value);
        value = (value - ((double) ((int) value))) * 60.0d;
        int seconds = (int) ((value - ((double) ((int) value))) * 6000.0d);
        return new int[]{degrees, 1, (int) value, 1, seconds, 100};
    }
}
