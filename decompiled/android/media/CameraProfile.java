package android.media;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import java.util.Arrays;
import java.util.HashMap;

public class CameraProfile {
    public static final int QUALITY_HIGH = 2;
    public static final int QUALITY_LOW = 0;
    public static final int QUALITY_MEDIUM = 1;
    private static final HashMap<Integer, int[]> sCache;

    private static final native int native_get_image_encoding_quality_level(int i, int i2);

    private static final native int native_get_num_image_encoding_quality_levels(int i);

    private static final native void native_init();

    static {
        sCache = new HashMap();
        System.loadLibrary("media_jni");
        native_init();
    }

    public static int getJpegEncodingQualityParameter(int quality) {
        int numberOfCameras = Camera.getNumberOfCameras();
        CameraInfo cameraInfo = new CameraInfo();
        for (int i = QUALITY_LOW; i < numberOfCameras; i += QUALITY_MEDIUM) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == 0) {
                return getJpegEncodingQualityParameter(i, quality);
            }
        }
        return QUALITY_LOW;
    }

    public static int getJpegEncodingQualityParameter(int cameraId, int quality) {
        if (quality < 0 || quality > QUALITY_HIGH) {
            throw new IllegalArgumentException("Unsupported quality level: " + quality);
        }
        int i;
        synchronized (sCache) {
            int[] levels = (int[]) sCache.get(Integer.valueOf(cameraId));
            if (levels == null) {
                levels = getImageEncodingQualityLevels(cameraId);
                sCache.put(Integer.valueOf(cameraId), levels);
            }
            i = levels[quality];
        }
        return i;
    }

    private static int[] getImageEncodingQualityLevels(int cameraId) {
        int nLevels = native_get_num_image_encoding_quality_levels(cameraId);
        if (nLevels != 3) {
            throw new RuntimeException("Unexpected Jpeg encoding quality levels " + nLevels);
        }
        int[] levels = new int[nLevels];
        for (int i = QUALITY_LOW; i < nLevels; i += QUALITY_MEDIUM) {
            levels[i] = native_get_image_encoding_quality_level(cameraId, i);
        }
        Arrays.sort(levels);
        return levels;
    }
}
