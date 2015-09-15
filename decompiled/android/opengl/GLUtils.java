package android.opengl;

import android.graphics.Bitmap;

public final class GLUtils {
    private static native void nativeClassInit();

    private static native int native_getInternalFormat(Bitmap bitmap);

    private static native int native_getType(Bitmap bitmap);

    private static native int native_texImage2D(int i, int i2, int i3, Bitmap bitmap, int i4, int i5);

    private static native int native_texSubImage2D(int i, int i2, int i3, int i4, Bitmap bitmap, int i5, int i6);

    public static native void setTracingLevel(int i);

    static {
        nativeClassInit();
    }

    private GLUtils() {
    }

    public static int getInternalFormat(Bitmap bitmap) {
        if (bitmap == null) {
            throw new NullPointerException("getInternalFormat can't be used with a null Bitmap");
        } else if (bitmap.isRecycled()) {
            throw new IllegalArgumentException("bitmap is recycled");
        } else {
            int result = native_getInternalFormat(bitmap);
            if (result >= 0) {
                return result;
            }
            throw new IllegalArgumentException("Unknown internalformat");
        }
    }

    public static int getType(Bitmap bitmap) {
        if (bitmap == null) {
            throw new NullPointerException("getType can't be used with a null Bitmap");
        } else if (bitmap.isRecycled()) {
            throw new IllegalArgumentException("bitmap is recycled");
        } else {
            int result = native_getType(bitmap);
            if (result >= 0) {
                return result;
            }
            throw new IllegalArgumentException("Unknown type");
        }
    }

    public static void texImage2D(int target, int level, int internalformat, Bitmap bitmap, int border) {
        if (bitmap == null) {
            throw new NullPointerException("texImage2D can't be used with a null Bitmap");
        } else if (bitmap.isRecycled()) {
            throw new IllegalArgumentException("bitmap is recycled");
        } else if (native_texImage2D(target, level, internalformat, bitmap, -1, border) != 0) {
            throw new IllegalArgumentException("invalid Bitmap format");
        }
    }

    public static void texImage2D(int target, int level, int internalformat, Bitmap bitmap, int type, int border) {
        if (bitmap == null) {
            throw new NullPointerException("texImage2D can't be used with a null Bitmap");
        } else if (bitmap.isRecycled()) {
            throw new IllegalArgumentException("bitmap is recycled");
        } else if (native_texImage2D(target, level, internalformat, bitmap, type, border) != 0) {
            throw new IllegalArgumentException("invalid Bitmap format");
        }
    }

    public static void texImage2D(int target, int level, Bitmap bitmap, int border) {
        if (bitmap == null) {
            throw new NullPointerException("texImage2D can't be used with a null Bitmap");
        } else if (bitmap.isRecycled()) {
            throw new IllegalArgumentException("bitmap is recycled");
        } else if (native_texImage2D(target, level, -1, bitmap, -1, border) != 0) {
            throw new IllegalArgumentException("invalid Bitmap format");
        }
    }

    public static void texSubImage2D(int target, int level, int xoffset, int yoffset, Bitmap bitmap) {
        if (bitmap == null) {
            throw new NullPointerException("texSubImage2D can't be used with a null Bitmap");
        } else if (bitmap.isRecycled()) {
            throw new IllegalArgumentException("bitmap is recycled");
        } else {
            if (native_texSubImage2D(target, level, xoffset, yoffset, bitmap, -1, getType(bitmap)) != 0) {
                throw new IllegalArgumentException("invalid Bitmap format");
            }
        }
    }

    public static void texSubImage2D(int target, int level, int xoffset, int yoffset, Bitmap bitmap, int format, int type) {
        if (bitmap == null) {
            throw new NullPointerException("texSubImage2D can't be used with a null Bitmap");
        } else if (bitmap.isRecycled()) {
            throw new IllegalArgumentException("bitmap is recycled");
        } else if (native_texSubImage2D(target, level, xoffset, yoffset, bitmap, format, type) != 0) {
            throw new IllegalArgumentException("invalid Bitmap format");
        }
    }

    public static String getEGLErrorString(int error) {
        switch (error) {
            case GLES11.GL_CLIP_PLANE0 /*12288*/:
                return "EGL_SUCCESS";
            case GLES11.GL_CLIP_PLANE1 /*12289*/:
                return "EGL_NOT_INITIALIZED";
            case GLES11.GL_CLIP_PLANE2 /*12290*/:
                return "EGL_BAD_ACCESS";
            case GLES11.GL_CLIP_PLANE3 /*12291*/:
                return "EGL_BAD_ALLOC";
            case GLES11.GL_CLIP_PLANE4 /*12292*/:
                return "EGL_BAD_ATTRIBUTE";
            case GLES11.GL_CLIP_PLANE5 /*12293*/:
                return "EGL_BAD_CONFIG";
            case EGL14.EGL_BAD_CONTEXT /*12294*/:
                return "EGL_BAD_CONTEXT";
            case EGL14.EGL_BAD_CURRENT_SURFACE /*12295*/:
                return "EGL_BAD_CURRENT_SURFACE";
            case EGL14.EGL_BAD_DISPLAY /*12296*/:
                return "EGL_BAD_DISPLAY";
            case EGL14.EGL_BAD_MATCH /*12297*/:
                return "EGL_BAD_MATCH";
            case EGL14.EGL_BAD_NATIVE_PIXMAP /*12298*/:
                return "EGL_BAD_NATIVE_PIXMAP";
            case EGL14.EGL_BAD_NATIVE_WINDOW /*12299*/:
                return "EGL_BAD_NATIVE_WINDOW";
            case EGL14.EGL_BAD_PARAMETER /*12300*/:
                return "EGL_BAD_PARAMETER";
            case EGL14.EGL_BAD_SURFACE /*12301*/:
                return "EGL_BAD_SURFACE";
            case EGL14.EGL_CONTEXT_LOST /*12302*/:
                return "EGL_CONTEXT_LOST";
            default:
                return "0x" + Integer.toHexString(error);
        }
    }
}
