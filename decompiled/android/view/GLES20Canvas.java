package android.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Canvas.EdgeType;
import android.graphics.Canvas.VertexMode;
import android.graphics.CanvasProperty;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Region.Op;
import android.graphics.TemporaryBuffer;
import android.text.GraphicsOperations;
import android.text.SpannableString;
import android.text.SpannedString;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;

class GLES20Canvas extends HardwareCanvas {
    private static boolean sIsAvailable;
    private Rect mClipBounds;
    private DrawFilter mFilter;
    private CanvasFinalizer mFinalizer;
    private int mHeight;
    private float[] mLine;
    private final boolean mOpaque;
    private RectF mPathBounds;
    private float[] mPoint;
    protected long mRenderer;
    private int mWidth;

    private static final class CanvasFinalizer {
        private final long mRenderer;

        public CanvasFinalizer(long renderer) {
            this.mRenderer = renderer;
        }

        protected void finalize() throws Throwable {
            try {
                GLES20Canvas.nDestroyRenderer(this.mRenderer);
            } finally {
                super.finalize();
            }
        }
    }

    private static native int nCallDrawGLFunction(long j, long j2);

    private static native boolean nClipPath(long j, long j2, int i);

    private static native boolean nClipRect(long j, float f, float f2, float f3, float f4, int i);

    private static native boolean nClipRect(long j, int i, int i2, int i3, int i4, int i5);

    private static native boolean nClipRegion(long j, long j2, int i);

    private static native void nConcatMatrix(long j, long j2);

    private static native long nCreateDisplayListRenderer();

    private static native void nDestroyRenderer(long j);

    private static native void nDrawArc(long j, float f, float f2, float f3, float f4, float f5, float f6, boolean z, long j2);

    private static native void nDrawBitmap(long j, long j2, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, long j3);

    private static native void nDrawBitmap(long j, long j2, float f, float f2, long j3);

    private static native void nDrawBitmap(long j, long j2, long j3, long j4);

    private static native void nDrawBitmap(long j, int[] iArr, int i, int i2, float f, float f2, int i3, int i4, boolean z, long j2);

    private static native void nDrawBitmapMesh(long j, long j2, int i, int i2, float[] fArr, int i3, int[] iArr, int i4, long j3);

    private static native void nDrawCircle(long j, float f, float f2, float f3, long j2);

    private static native void nDrawCircle(long j, long j2, long j3, long j4, long j5);

    private static native void nDrawColor(long j, int i, int i2);

    private static native void nDrawLayer(long j, long j2, float f, float f2);

    private static native void nDrawLines(long j, float[] fArr, int i, int i2, long j2);

    private static native void nDrawOval(long j, float f, float f2, float f3, float f4, long j2);

    private static native void nDrawPatch(long j, long j2, long j3, float f, float f2, float f3, float f4, long j4);

    private static native void nDrawPath(long j, long j2, long j3);

    private static native void nDrawPoints(long j, float[] fArr, int i, int i2, long j2);

    private static native void nDrawRect(long j, float f, float f2, float f3, float f4, long j2);

    private static native void nDrawRects(long j, long j2, long j3);

    private static native int nDrawRenderNode(long j, long j2, Rect rect, int i);

    private static native void nDrawRoundRect(long j, float f, float f2, float f3, float f4, float f5, float f6, long j2);

    private static native void nDrawRoundRect(long j, long j2, long j3, long j4, long j5, long j6, long j7, long j8);

    private static native void nDrawText(long j, String str, int i, int i2, float f, float f2, int i3, long j2, long j3);

    private static native void nDrawText(long j, char[] cArr, int i, int i2, float f, float f2, int i3, long j2, long j3);

    private static native void nDrawTextOnPath(long j, String str, int i, int i2, long j2, float f, float f2, int i3, long j3, long j4);

    private static native void nDrawTextOnPath(long j, char[] cArr, int i, int i2, long j2, float f, float f2, int i3, long j3, long j4);

    private static native void nDrawTextRun(long j, String str, int i, int i2, int i3, int i4, float f, float f2, boolean z, long j2, long j3);

    private static native void nDrawTextRun(long j, char[] cArr, int i, int i2, int i3, int i4, float f, float f2, boolean z, long j2, long j3);

    private static native void nFinish(long j);

    protected static native long nFinishRecording(long j);

    private static native boolean nGetClipBounds(long j, Rect rect);

    private static native void nGetMatrix(long j, long j2);

    private static native int nGetMaximumTextureHeight();

    private static native int nGetMaximumTextureWidth();

    private static native int nGetSaveCount(long j);

    private static native void nInsertReorderBarrier(long j, boolean z);

    private static native boolean nIsAvailable();

    private static native int nPrepare(long j, boolean z);

    private static native int nPrepareDirty(long j, int i, int i2, int i3, int i4, boolean z);

    private static native boolean nQuickReject(long j, float f, float f2, float f3, float f4);

    private static native void nResetDisplayListRenderer(long j);

    private static native void nResetPaintFilter(long j);

    private static native void nRestore(long j);

    private static native void nRestoreToCount(long j, int i);

    private static native void nRotate(long j, float f);

    private static native int nSave(long j, int i);

    private static native int nSaveLayer(long j, float f, float f2, float f3, float f4, long j2, int i);

    private static native int nSaveLayer(long j, long j2, int i);

    private static native int nSaveLayerAlpha(long j, float f, float f2, float f3, float f4, int i, int i2);

    private static native int nSaveLayerAlpha(long j, int i, int i2);

    private static native void nScale(long j, float f, float f2);

    private static native void nSetHighContrastText(long j, boolean z);

    private static native void nSetMatrix(long j, long j2);

    private static native void nSetProperty(String str, String str2);

    private static native void nSetViewport(long j, int i, int i2);

    private static native void nSetupPaintFilter(long j, int i, int i2);

    private static native void nSkew(long j, float f, float f2);

    private static native void nTranslate(long j, float f, float f2);

    static {
        sIsAvailable = nIsAvailable();
    }

    static boolean isAvailable() {
        return sIsAvailable;
    }

    protected GLES20Canvas() {
        this.mOpaque = false;
        this.mRenderer = nCreateDisplayListRenderer();
        setupFinalizer();
    }

    private void setupFinalizer() {
        if (this.mRenderer == 0) {
            throw new IllegalStateException("Could not create GLES20Canvas renderer");
        }
        this.mFinalizer = new CanvasFinalizer(this.mRenderer);
    }

    public static void setProperty(String name, String value) {
        nSetProperty(name, value);
    }

    public boolean isOpaque() {
        return this.mOpaque;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public int getMaximumBitmapWidth() {
        return nGetMaximumTextureWidth();
    }

    public int getMaximumBitmapHeight() {
        return nGetMaximumTextureHeight();
    }

    long getRenderer() {
        return this.mRenderer;
    }

    public void setViewport(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
        nSetViewport(this.mRenderer, width, height);
    }

    public void setHighContrastText(boolean highContrastText) {
        nSetHighContrastText(this.mRenderer, highContrastText);
    }

    public void insertReorderBarrier() {
        nInsertReorderBarrier(this.mRenderer, true);
    }

    public void insertInorderBarrier() {
        nInsertReorderBarrier(this.mRenderer, false);
    }

    public int onPreDraw(Rect dirty) {
        if (dirty != null) {
            return nPrepareDirty(this.mRenderer, dirty.left, dirty.top, dirty.right, dirty.bottom, this.mOpaque);
        }
        return nPrepare(this.mRenderer, this.mOpaque);
    }

    public void onPostDraw() {
        nFinish(this.mRenderer);
    }

    public int callDrawGLFunction2(long drawGLFunction) {
        return nCallDrawGLFunction(this.mRenderer, drawGLFunction);
    }

    public int drawRenderNode(RenderNode renderNode, Rect dirty, int flags) {
        return nDrawRenderNode(this.mRenderer, renderNode.getNativeDisplayList(), dirty, flags);
    }

    void drawHardwareLayer(HardwareLayer layer, float x, float y, Paint paint) {
        layer.setLayerPaint(paint);
        nDrawLayer(this.mRenderer, layer.getLayerHandle(), x, y);
    }

    private Rect getInternalClipBounds() {
        if (this.mClipBounds == null) {
            this.mClipBounds = new Rect();
        }
        return this.mClipBounds;
    }

    private RectF getPathBounds() {
        if (this.mPathBounds == null) {
            this.mPathBounds = new RectF();
        }
        return this.mPathBounds;
    }

    private float[] getPointStorage() {
        if (this.mPoint == null) {
            this.mPoint = new float[2];
        }
        return this.mPoint;
    }

    private float[] getLineStorage() {
        if (this.mLine == null) {
            this.mLine = new float[4];
        }
        return this.mLine;
    }

    public boolean clipPath(Path path) {
        return nClipPath(this.mRenderer, path.mNativePath, Op.INTERSECT.nativeInt);
    }

    public boolean clipPath(Path path, Op op) {
        return nClipPath(this.mRenderer, path.mNativePath, op.nativeInt);
    }

    public boolean clipRect(float left, float top, float right, float bottom) {
        return nClipRect(this.mRenderer, left, top, right, bottom, Op.INTERSECT.nativeInt);
    }

    public boolean clipRect(float left, float top, float right, float bottom, Op op) {
        return nClipRect(this.mRenderer, left, top, right, bottom, op.nativeInt);
    }

    public boolean clipRect(int left, int top, int right, int bottom) {
        return nClipRect(this.mRenderer, left, top, right, bottom, Op.INTERSECT.nativeInt);
    }

    public boolean clipRect(Rect rect) {
        return nClipRect(this.mRenderer, rect.left, rect.top, rect.right, rect.bottom, Op.INTERSECT.nativeInt);
    }

    public boolean clipRect(Rect rect, Op op) {
        return nClipRect(this.mRenderer, rect.left, rect.top, rect.right, rect.bottom, op.nativeInt);
    }

    public boolean clipRect(RectF rect) {
        return nClipRect(this.mRenderer, rect.left, rect.top, rect.right, rect.bottom, Op.INTERSECT.nativeInt);
    }

    public boolean clipRect(RectF rect, Op op) {
        return nClipRect(this.mRenderer, rect.left, rect.top, rect.right, rect.bottom, op.nativeInt);
    }

    public boolean clipRegion(Region region) {
        return nClipRegion(this.mRenderer, region.mNativeRegion, Op.INTERSECT.nativeInt);
    }

    public boolean clipRegion(Region region, Op op) {
        return nClipRegion(this.mRenderer, region.mNativeRegion, op.nativeInt);
    }

    public boolean getClipBounds(Rect bounds) {
        return nGetClipBounds(this.mRenderer, bounds);
    }

    public boolean quickReject(float left, float top, float right, float bottom, EdgeType type) {
        return nQuickReject(this.mRenderer, left, top, right, bottom);
    }

    public boolean quickReject(Path path, EdgeType type) {
        RectF pathBounds = getPathBounds();
        path.computeBounds(pathBounds, true);
        return nQuickReject(this.mRenderer, pathBounds.left, pathBounds.top, pathBounds.right, pathBounds.bottom);
    }

    public boolean quickReject(RectF rect, EdgeType type) {
        return nQuickReject(this.mRenderer, rect.left, rect.top, rect.right, rect.bottom);
    }

    public void translate(float dx, float dy) {
        if (dx != 0.0f || dy != 0.0f) {
            nTranslate(this.mRenderer, dx, dy);
        }
    }

    public void skew(float sx, float sy) {
        nSkew(this.mRenderer, sx, sy);
    }

    public void rotate(float degrees) {
        nRotate(this.mRenderer, degrees);
    }

    public void scale(float sx, float sy) {
        nScale(this.mRenderer, sx, sy);
    }

    public void setMatrix(Matrix matrix) {
        nSetMatrix(this.mRenderer, matrix == null ? 0 : matrix.native_instance);
    }

    public void getMatrix(Matrix matrix) {
        nGetMatrix(this.mRenderer, matrix.native_instance);
    }

    public void concat(Matrix matrix) {
        if (matrix != null) {
            nConcatMatrix(this.mRenderer, matrix.native_instance);
        }
    }

    public int save() {
        return nSave(this.mRenderer, 3);
    }

    public int save(int saveFlags) {
        return nSave(this.mRenderer, saveFlags);
    }

    public int saveLayer(RectF bounds, Paint paint, int saveFlags) {
        if (bounds != null) {
            return saveLayer(bounds.left, bounds.top, bounds.right, bounds.bottom, paint, saveFlags);
        }
        return nSaveLayer(this.mRenderer, paint == null ? 0 : paint.mNativePaint, saveFlags);
    }

    public int saveLayer(float left, float top, float right, float bottom, Paint paint, int saveFlags) {
        if (left >= right || top >= bottom) {
            return save(saveFlags);
        }
        return nSaveLayer(this.mRenderer, left, top, right, bottom, paint == null ? 0 : paint.mNativePaint, saveFlags);
    }

    public int saveLayerAlpha(RectF bounds, int alpha, int saveFlags) {
        if (bounds != null) {
            return saveLayerAlpha(bounds.left, bounds.top, bounds.right, bounds.bottom, alpha, saveFlags);
        }
        return nSaveLayerAlpha(this.mRenderer, alpha, saveFlags);
    }

    public int saveLayerAlpha(float left, float top, float right, float bottom, int alpha, int saveFlags) {
        if (left >= right || top >= bottom) {
            return save(saveFlags);
        }
        return nSaveLayerAlpha(this.mRenderer, left, top, right, bottom, alpha, saveFlags);
    }

    public void restore() {
        nRestore(this.mRenderer);
    }

    public void restoreToCount(int saveCount) {
        nRestoreToCount(this.mRenderer, saveCount);
    }

    public int getSaveCount() {
        return nGetSaveCount(this.mRenderer);
    }

    public void setDrawFilter(DrawFilter filter) {
        this.mFilter = filter;
        if (filter == null) {
            nResetPaintFilter(this.mRenderer);
        } else if (filter instanceof PaintFlagsDrawFilter) {
            PaintFlagsDrawFilter flagsFilter = (PaintFlagsDrawFilter) filter;
            nSetupPaintFilter(this.mRenderer, flagsFilter.clearBits, flagsFilter.setBits);
        }
    }

    public DrawFilter getDrawFilter() {
        return this.mFilter;
    }

    public void drawArc(float left, float top, float right, float bottom, float startAngle, float sweepAngle, boolean useCenter, Paint paint) {
        nDrawArc(this.mRenderer, left, top, right, bottom, startAngle, sweepAngle, useCenter, paint.mNativePaint);
    }

    public void drawARGB(int a, int r, int g, int b) {
        drawColor(((((a & EditorInfo.IME_MASK_ACTION) << 24) | ((r & EditorInfo.IME_MASK_ACTION) << 16)) | ((g & EditorInfo.IME_MASK_ACTION) << 8)) | (b & EditorInfo.IME_MASK_ACTION));
    }

    public void drawPatch(NinePatch patch, Rect dst, Paint paint) {
        Bitmap bitmap = patch.getBitmap();
        Canvas.throwIfCannotDraw(bitmap);
        nDrawPatch(this.mRenderer, bitmap.mNativeBitmap, patch.mNativeChunk, (float) dst.left, (float) dst.top, (float) dst.right, (float) dst.bottom, paint == null ? 0 : paint.mNativePaint);
    }

    public void drawPatch(NinePatch patch, RectF dst, Paint paint) {
        Bitmap bitmap = patch.getBitmap();
        Canvas.throwIfCannotDraw(bitmap);
        nDrawPatch(this.mRenderer, bitmap.mNativeBitmap, patch.mNativeChunk, dst.left, dst.top, dst.right, dst.bottom, paint == null ? 0 : paint.mNativePaint);
    }

    public void drawBitmap(Bitmap bitmap, float left, float top, Paint paint) {
        Canvas.throwIfCannotDraw(bitmap);
        nDrawBitmap(this.mRenderer, bitmap.mNativeBitmap, left, top, paint == null ? 0 : paint.mNativePaint);
    }

    public void drawBitmap(Bitmap bitmap, Matrix matrix, Paint paint) {
        Canvas.throwIfCannotDraw(bitmap);
        nDrawBitmap(this.mRenderer, bitmap.mNativeBitmap, matrix.native_instance, paint == null ? 0 : paint.mNativePaint);
    }

    public void drawBitmap(Bitmap bitmap, Rect src, Rect dst, Paint paint) {
        int top;
        int left;
        int right;
        int bottom;
        Canvas.throwIfCannotDraw(bitmap);
        long nativePaint = paint == null ? 0 : paint.mNativePaint;
        if (src == null) {
            top = 0;
            left = 0;
            right = bitmap.getWidth();
            bottom = bitmap.getHeight();
        } else {
            left = src.left;
            right = src.right;
            top = src.top;
            bottom = src.bottom;
        }
        nDrawBitmap(this.mRenderer, bitmap.mNativeBitmap, (float) left, (float) top, (float) right, (float) bottom, (float) dst.left, (float) dst.top, (float) dst.right, (float) dst.bottom, nativePaint);
    }

    public void drawBitmap(Bitmap bitmap, Rect src, RectF dst, Paint paint) {
        float top;
        float left;
        float right;
        float bottom;
        Canvas.throwIfCannotDraw(bitmap);
        long nativePaint = paint == null ? 0 : paint.mNativePaint;
        if (src == null) {
            top = 0.0f;
            left = 0.0f;
            right = (float) bitmap.getWidth();
            bottom = (float) bitmap.getHeight();
        } else {
            left = (float) src.left;
            right = (float) src.right;
            top = (float) src.top;
            bottom = (float) src.bottom;
        }
        nDrawBitmap(this.mRenderer, bitmap.mNativeBitmap, left, top, right, bottom, dst.left, dst.top, dst.right, dst.bottom, nativePaint);
    }

    public void drawBitmap(int[] colors, int offset, int stride, float x, float y, int width, int height, boolean hasAlpha, Paint paint) {
        if (width < 0) {
            throw new IllegalArgumentException("width must be >= 0");
        } else if (height < 0) {
            throw new IllegalArgumentException("height must be >= 0");
        } else if (Math.abs(stride) < width) {
            throw new IllegalArgumentException("abs(stride) must be >= width");
        } else {
            int lastScanline = offset + ((height - 1) * stride);
            int length = colors.length;
            if (offset < 0 || offset + width > length || lastScanline < 0 || lastScanline + width > length) {
                throw new ArrayIndexOutOfBoundsException();
            }
            nDrawBitmap(this.mRenderer, colors, offset, stride, x, y, width, height, hasAlpha, paint == null ? 0 : paint.mNativePaint);
        }
    }

    public void drawBitmap(int[] colors, int offset, int stride, int x, int y, int width, int height, boolean hasAlpha, Paint paint) {
        drawBitmap(colors, offset, stride, (float) x, (float) y, width, height, hasAlpha, paint);
    }

    public void drawBitmapMesh(Bitmap bitmap, int meshWidth, int meshHeight, float[] verts, int vertOffset, int[] colors, int colorOffset, Paint paint) {
        Canvas.throwIfCannotDraw(bitmap);
        if (meshWidth < 0 || meshHeight < 0 || vertOffset < 0 || colorOffset < 0) {
            throw new ArrayIndexOutOfBoundsException();
        } else if (meshWidth != 0 && meshHeight != 0) {
            int count = (meshWidth + 1) * (meshHeight + 1);
            Canvas.checkRange(verts.length, vertOffset, count * 2);
            if (colors != null) {
                Canvas.checkRange(colors.length, colorOffset, count);
            }
            nDrawBitmapMesh(this.mRenderer, bitmap.mNativeBitmap, meshWidth, meshHeight, verts, vertOffset, colors, colorOffset, paint == null ? 0 : paint.mNativePaint);
        }
    }

    public void drawCircle(float cx, float cy, float radius, Paint paint) {
        nDrawCircle(this.mRenderer, cx, cy, radius, paint.mNativePaint);
    }

    public void drawCircle(CanvasProperty<Float> cx, CanvasProperty<Float> cy, CanvasProperty<Float> radius, CanvasProperty<Paint> paint) {
        nDrawCircle(this.mRenderer, cx.getNativeContainer(), cy.getNativeContainer(), radius.getNativeContainer(), paint.getNativeContainer());
    }

    public void drawRoundRect(CanvasProperty<Float> left, CanvasProperty<Float> top, CanvasProperty<Float> right, CanvasProperty<Float> bottom, CanvasProperty<Float> rx, CanvasProperty<Float> ry, CanvasProperty<Paint> paint) {
        nDrawRoundRect(this.mRenderer, left.getNativeContainer(), top.getNativeContainer(), right.getNativeContainer(), bottom.getNativeContainer(), rx.getNativeContainer(), ry.getNativeContainer(), paint.getNativeContainer());
    }

    public void drawColor(int color) {
        drawColor(color, Mode.SRC_OVER);
    }

    public void drawColor(int color, Mode mode) {
        nDrawColor(this.mRenderer, color, mode.nativeInt);
    }

    public void drawLine(float startX, float startY, float stopX, float stopY, Paint paint) {
        float[] line = getLineStorage();
        line[0] = startX;
        line[1] = startY;
        line[2] = stopX;
        line[3] = stopY;
        drawLines(line, 0, 4, paint);
    }

    public void drawLines(float[] pts, int offset, int count, Paint paint) {
        if (count >= 4) {
            if ((offset | count) < 0 || offset + count > pts.length) {
                throw new IllegalArgumentException("The lines array must contain 4 elements per line.");
            }
            nDrawLines(this.mRenderer, pts, offset, count, paint.mNativePaint);
        }
    }

    public void drawLines(float[] pts, Paint paint) {
        drawLines(pts, 0, pts.length, paint);
    }

    public void drawOval(float left, float top, float right, float bottom, Paint paint) {
        nDrawOval(this.mRenderer, left, top, right, bottom, paint.mNativePaint);
    }

    public void drawPaint(Paint paint) {
        Rect r = getInternalClipBounds();
        nGetClipBounds(this.mRenderer, r);
        drawRect((float) r.left, (float) r.top, (float) r.right, (float) r.bottom, paint);
    }

    public void drawPath(Path path, Paint paint) {
        if (!path.isSimplePath) {
            nDrawPath(this.mRenderer, path.mNativePath, paint.mNativePaint);
        } else if (path.rects != null) {
            nDrawRects(this.mRenderer, path.rects.mNativeRegion, paint.mNativePaint);
        }
    }

    public void drawPicture(Picture picture) {
        picture.endRecording();
    }

    public void drawPoint(float x, float y, Paint paint) {
        float[] point = getPointStorage();
        point[0] = x;
        point[1] = y;
        drawPoints(point, 0, 2, paint);
    }

    public void drawPoints(float[] pts, Paint paint) {
        drawPoints(pts, 0, pts.length, paint);
    }

    public void drawPoints(float[] pts, int offset, int count, Paint paint) {
        if (count >= 2) {
            nDrawPoints(this.mRenderer, pts, offset, count, paint.mNativePaint);
        }
    }

    public void drawRect(float left, float top, float right, float bottom, Paint paint) {
        if (left != right && top != bottom) {
            nDrawRect(this.mRenderer, left, top, right, bottom, paint.mNativePaint);
        }
    }

    public void drawRect(Rect r, Paint paint) {
        drawRect((float) r.left, (float) r.top, (float) r.right, (float) r.bottom, paint);
    }

    public void drawRect(RectF r, Paint paint) {
        drawRect(r.left, r.top, r.right, r.bottom, paint);
    }

    public void drawRGB(int r, int g, int b) {
        drawColor(((Color.BLACK | ((r & EditorInfo.IME_MASK_ACTION) << 16)) | ((g & EditorInfo.IME_MASK_ACTION) << 8)) | (b & EditorInfo.IME_MASK_ACTION));
    }

    public void drawRoundRect(float left, float top, float right, float bottom, float rx, float ry, Paint paint) {
        nDrawRoundRect(this.mRenderer, left, top, right, bottom, rx, ry, paint.mNativePaint);
    }

    public void drawText(char[] text, int index, int count, float x, float y, Paint paint) {
        if ((((index | count) | (index + count)) | ((text.length - index) - count)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        nDrawText(this.mRenderer, text, index, count, x, y, paint.mBidiFlags, paint.mNativePaint, paint.mNativeTypeface);
    }

    public void drawText(CharSequence text, int start, int end, float x, float y, Paint paint) {
        if ((((start | end) | (end - start)) | (text.length() - end)) < 0) {
            throw new IndexOutOfBoundsException();
        } else if ((text instanceof String) || (text instanceof SpannedString) || (text instanceof SpannableString)) {
            nDrawText(this.mRenderer, text.toString(), start, end, x, y, paint.mBidiFlags, paint.mNativePaint, paint.mNativeTypeface);
        } else if (text instanceof GraphicsOperations) {
            ((GraphicsOperations) text).drawText(this, start, end, x, y, paint);
        } else {
            char[] buf = TemporaryBuffer.obtain(end - start);
            TextUtils.getChars(text, start, end, buf, 0);
            nDrawText(this.mRenderer, buf, 0, end - start, x, y, paint.mBidiFlags, paint.mNativePaint, paint.mNativeTypeface);
            TemporaryBuffer.recycle(buf);
        }
    }

    public void drawText(String text, int start, int end, float x, float y, Paint paint) {
        if ((((start | end) | (end - start)) | (text.length() - end)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        nDrawText(this.mRenderer, text, start, end, x, y, paint.mBidiFlags, paint.mNativePaint, paint.mNativeTypeface);
    }

    public void drawText(String text, float x, float y, Paint paint) {
        nDrawText(this.mRenderer, text, 0, text.length(), x, y, paint.mBidiFlags, paint.mNativePaint, paint.mNativeTypeface);
    }

    public void drawTextOnPath(char[] text, int index, int count, Path path, float hOffset, float vOffset, Paint paint) {
        if (index < 0 || index + count > text.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        nDrawTextOnPath(this.mRenderer, text, index, count, path.mNativePath, hOffset, vOffset, paint.mBidiFlags, paint.mNativePaint, paint.mNativeTypeface);
    }

    public void drawTextOnPath(String text, Path path, float hOffset, float vOffset, Paint paint) {
        if (text.length() != 0) {
            nDrawTextOnPath(this.mRenderer, text, 0, text.length(), path.mNativePath, hOffset, vOffset, paint.mBidiFlags, paint.mNativePaint, paint.mNativeTypeface);
        }
    }

    public void drawTextRun(char[] text, int index, int count, int contextIndex, int contextCount, float x, float y, boolean isRtl, Paint paint) {
        if (((index | count) | ((text.length - index) - count)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        nDrawTextRun(this.mRenderer, text, index, count, contextIndex, contextCount, x, y, isRtl, paint.mNativePaint, paint.mNativeTypeface);
    }

    public void drawTextRun(CharSequence text, int start, int end, int contextStart, int contextEnd, float x, float y, boolean isRtl, Paint paint) {
        if ((((start | end) | (end - start)) | (text.length() - end)) < 0) {
            throw new IndexOutOfBoundsException();
        } else if ((text instanceof String) || (text instanceof SpannedString) || (text instanceof SpannableString)) {
            int i = start;
            r8 = end;
            int i2 = contextStart;
            r10 = contextEnd;
            r11 = x;
            r12 = y;
            r13 = isRtl;
            nDrawTextRun(this.mRenderer, text.toString(), i, r8, i2, r10, r11, r12, r13, paint.mNativePaint, paint.mNativeTypeface);
        } else if (text instanceof GraphicsOperations) {
            ((GraphicsOperations) text).drawTextRun(this, start, end, contextStart, contextEnd, x, y, isRtl, paint);
        } else {
            r10 = contextEnd - contextStart;
            r8 = end - start;
            char[] buf = TemporaryBuffer.obtain(r10);
            TextUtils.getChars(text, contextStart, contextEnd, buf, 0);
            r11 = x;
            r12 = y;
            r13 = isRtl;
            nDrawTextRun(this.mRenderer, buf, start - contextStart, r8, 0, r10, r11, r12, r13, paint.mNativePaint, paint.mNativeTypeface);
            TemporaryBuffer.recycle(buf);
        }
    }

    public void drawVertices(VertexMode mode, int vertexCount, float[] verts, int vertOffset, float[] texs, int texOffset, int[] colors, int colorOffset, short[] indices, int indexOffset, int indexCount, Paint paint) {
    }
}
