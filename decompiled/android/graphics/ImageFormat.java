package android.graphics;

public class ImageFormat {
    public static final int JPEG = 256;
    public static final int NV16 = 16;
    public static final int NV21 = 17;
    public static final int RAW10 = 37;
    public static final int RAW_SENSOR = 32;
    public static final int RGB_565 = 4;
    public static final int UNKNOWN = 0;
    public static final int Y16 = 540422489;
    public static final int Y8 = 538982489;
    public static final int YUV_420_888 = 35;
    public static final int YUY2 = 20;
    public static final int YV12 = 842094169;

    public static int getBitsPerPixel(int format) {
        switch (format) {
            case RGB_565 /*4*/:
            case NV16 /*16*/:
            case YUY2 /*20*/:
            case RAW_SENSOR /*32*/:
            case Y16 /*540422489*/:
                return NV16;
            case NV21 /*17*/:
                return 12;
            case YUV_420_888 /*35*/:
                return 12;
            case RAW10 /*37*/:
                return 10;
            case Y8 /*538982489*/:
                return 8;
            case YV12 /*842094169*/:
                return 12;
            default:
                return -1;
        }
    }

    public static boolean isPublicFormat(int format) {
        switch (format) {
            case RGB_565 /*4*/:
            case NV16 /*16*/:
            case NV21 /*17*/:
            case YUY2 /*20*/:
            case RAW_SENSOR /*32*/:
            case YUV_420_888 /*35*/:
            case RAW10 /*37*/:
            case JPEG /*256*/:
            case YV12 /*842094169*/:
                return true;
            default:
                return false;
        }
    }
}
