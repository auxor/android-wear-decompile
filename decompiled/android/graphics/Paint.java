package android.graphics;

import android.net.ProxyInfo;
import android.text.GraphicsOperations;
import android.text.SpannableString;
import android.text.SpannedString;
import android.text.TextUtils;
import android.view.WindowManager.LayoutParams;
import java.util.Locale;

public class Paint {
    public static final int ANTI_ALIAS_FLAG = 1;
    public static final int AUTO_HINTING_TEXT_FLAG = 2048;
    public static final int BIDI_DEFAULT_LTR = 2;
    public static final int BIDI_DEFAULT_RTL = 3;
    private static final int BIDI_FLAG_MASK = 7;
    public static final int BIDI_FORCE_LTR = 4;
    public static final int BIDI_FORCE_RTL = 5;
    public static final int BIDI_LTR = 0;
    private static final int BIDI_MAX_FLAG_VALUE = 5;
    public static final int BIDI_RTL = 1;
    public static final int CURSOR_AFTER = 0;
    public static final int CURSOR_AT = 4;
    public static final int CURSOR_AT_OR_AFTER = 1;
    public static final int CURSOR_AT_OR_BEFORE = 3;
    public static final int CURSOR_BEFORE = 2;
    private static final int CURSOR_OPT_MAX_VALUE = 4;
    static final int DEFAULT_PAINT_FLAGS = 1280;
    public static final int DEV_KERN_TEXT_FLAG = 256;
    public static final int DIRECTION_LTR = 0;
    public static final int DIRECTION_RTL = 1;
    public static final int DITHER_FLAG = 4;
    public static final int EMBEDDED_BITMAP_TEXT_FLAG = 1024;
    public static final int FAKE_BOLD_TEXT_FLAG = 32;
    public static final int FILTER_BITMAP_FLAG = 2;
    public static final int HINTING_OFF = 0;
    public static final int HINTING_ON = 1;
    public static final int LCD_RENDER_TEXT_FLAG = 512;
    public static final int LINEAR_TEXT_FLAG = 64;
    public static final int STRIKE_THRU_TEXT_FLAG = 16;
    public static final int SUBPIXEL_TEXT_FLAG = 128;
    public static final int UNDERLINE_TEXT_FLAG = 8;
    public static final int VERTICAL_TEXT_FLAG = 4096;
    static final Align[] sAlignArray;
    static final Cap[] sCapArray;
    static final Join[] sJoinArray;
    static final Style[] sStyleArray;
    public int mBidiFlags;
    private ColorFilter mColorFilter;
    private float mCompatScaling;
    private String mFontFeatureSettings;
    private boolean mHasCompatScaling;
    private float mInvCompatScaling;
    private Locale mLocale;
    private MaskFilter mMaskFilter;
    public long mNativePaint;
    public long mNativeTypeface;
    private PathEffect mPathEffect;
    private Rasterizer mRasterizer;
    private Shader mShader;
    private Typeface mTypeface;
    private Xfermode mXfermode;

    public enum Align {
        LEFT(Paint.HINTING_OFF),
        CENTER(Paint.HINTING_ON),
        RIGHT(Paint.FILTER_BITMAP_FLAG);
        
        final int nativeInt;

        private Align(int nativeInt) {
            this.nativeInt = nativeInt;
        }
    }

    public enum Cap {
        BUTT(Paint.HINTING_OFF),
        ROUND(Paint.HINTING_ON),
        SQUARE(Paint.FILTER_BITMAP_FLAG);
        
        final int nativeInt;

        private Cap(int nativeInt) {
            this.nativeInt = nativeInt;
        }
    }

    public static class FontMetrics {
        public float ascent;
        public float bottom;
        public float descent;
        public float leading;
        public float top;
    }

    public static class FontMetricsInt {
        public int ascent;
        public int bottom;
        public int descent;
        public int leading;
        public int top;

        public String toString() {
            return "FontMetricsInt: top=" + this.top + " ascent=" + this.ascent + " descent=" + this.descent + " bottom=" + this.bottom + " leading=" + this.leading;
        }
    }

    public enum Join {
        MITER(Paint.HINTING_OFF),
        ROUND(Paint.HINTING_ON),
        BEVEL(Paint.FILTER_BITMAP_FLAG);
        
        final int nativeInt;

        private Join(int nativeInt) {
            this.nativeInt = nativeInt;
        }
    }

    public enum Style {
        FILL(Paint.HINTING_OFF),
        STROKE(Paint.HINTING_ON),
        FILL_AND_STROKE(Paint.FILTER_BITMAP_FLAG);
        
        final int nativeInt;

        private Style(int nativeInt) {
            this.nativeInt = nativeInt;
        }
    }

    private static native void finalizer(long j);

    private static native void nativeGetCharArrayBounds(long j, long j2, char[] cArr, int i, int i2, int i3, Rect rect);

    private static native void nativeGetStringBounds(long j, long j2, String str, int i, int i2, int i3, Rect rect);

    private static native int native_breakText(long j, long j2, String str, boolean z, float f, int i, float[] fArr);

    private static native int native_breakText(long j, long j2, char[] cArr, int i, int i2, float f, int i3, float[] fArr);

    private static native boolean native_getFillPath(long j, long j2, long j3);

    private static native float native_getLetterSpacing(long j);

    private static native int native_getStrokeCap(long j);

    private static native int native_getStrokeJoin(long j);

    private static native int native_getStyle(long j);

    private static native int native_getTextAlign(long j);

    private static native int native_getTextGlyphs(long j, String str, int i, int i2, int i3, int i4, int i5, char[] cArr);

    private static native void native_getTextPath(long j, long j2, int i, String str, int i2, int i3, float f, float f2, long j3);

    private static native void native_getTextPath(long j, long j2, int i, char[] cArr, int i2, int i3, float f, float f2, long j3);

    private static native float native_getTextRunAdvances(long j, long j2, String str, int i, int i2, int i3, int i4, boolean z, float[] fArr, int i5);

    private static native float native_getTextRunAdvances(long j, long j2, char[] cArr, int i, int i2, int i3, int i4, boolean z, float[] fArr, int i5);

    private native int native_getTextRunCursor(long j, String str, int i, int i2, int i3, int i4, int i5);

    private native int native_getTextRunCursor(long j, char[] cArr, int i, int i2, int i3, int i4, int i5);

    private static native int native_getTextWidths(long j, long j2, String str, int i, int i2, int i3, float[] fArr);

    private static native int native_getTextWidths(long j, long j2, char[] cArr, int i, int i2, int i3, float[] fArr);

    private static native boolean native_hasShadowLayer(long j);

    private static native long native_init();

    private static native long native_initWithPaint(long j);

    private native float native_measureText(String str, int i);

    private native float native_measureText(String str, int i, int i2, int i3);

    private native float native_measureText(char[] cArr, int i, int i2, int i3);

    private static native void native_reset(long j);

    private static native void native_set(long j, long j2);

    private static native long native_setColorFilter(long j, long j2);

    private static native void native_setFontFeatureSettings(long j, String str);

    private static native void native_setLetterSpacing(long j, float f);

    private static native long native_setMaskFilter(long j, long j2);

    private static native long native_setPathEffect(long j, long j2);

    private static native long native_setRasterizer(long j, long j2);

    private static native long native_setShader(long j, long j2);

    private static native void native_setShadowLayer(long j, float f, float f2, float f3, int i);

    private static native void native_setStrokeCap(long j, int i);

    private static native void native_setStrokeJoin(long j, int i);

    private static native void native_setStyle(long j, int i);

    private static native void native_setTextAlign(long j, int i);

    private static native void native_setTextLocale(long j, String str);

    private static native long native_setTypeface(long j, long j2);

    private static native long native_setXfermode(long j, long j2);

    public native float ascent();

    public native float descent();

    public native int getAlpha();

    public native int getColor();

    public native int getFlags();

    public native float getFontMetrics(FontMetrics fontMetrics);

    public native int getFontMetricsInt(FontMetricsInt fontMetricsInt);

    public native int getHinting();

    public native float getStrokeMiter();

    public native float getStrokeWidth();

    public native float getTextScaleX();

    public native float getTextSize();

    public native float getTextSkewX();

    public native boolean isElegantTextHeight();

    public native void setAlpha(int i);

    public native void setAntiAlias(boolean z);

    public native void setColor(int i);

    public native void setDither(boolean z);

    public native void setElegantTextHeight(boolean z);

    public native void setFakeBoldText(boolean z);

    public native void setFilterBitmap(boolean z);

    public native void setFlags(int i);

    public native void setHinting(int i);

    public native void setLinearText(boolean z);

    public native void setStrikeThruText(boolean z);

    public native void setStrokeMiter(float f);

    public native void setStrokeWidth(float f);

    public native void setSubpixelText(boolean z);

    public native void setTextScaleX(float f);

    public native void setTextSize(float f);

    public native void setTextSkewX(float f);

    public native void setUnderlineText(boolean z);

    static {
        Style[] styleArr = new Style[CURSOR_AT_OR_BEFORE];
        styleArr[HINTING_OFF] = Style.FILL;
        styleArr[HINTING_ON] = Style.STROKE;
        styleArr[FILTER_BITMAP_FLAG] = Style.FILL_AND_STROKE;
        sStyleArray = styleArr;
        Cap[] capArr = new Cap[CURSOR_AT_OR_BEFORE];
        capArr[HINTING_OFF] = Cap.BUTT;
        capArr[HINTING_ON] = Cap.ROUND;
        capArr[FILTER_BITMAP_FLAG] = Cap.SQUARE;
        sCapArray = capArr;
        Join[] joinArr = new Join[CURSOR_AT_OR_BEFORE];
        joinArr[HINTING_OFF] = Join.MITER;
        joinArr[HINTING_ON] = Join.ROUND;
        joinArr[FILTER_BITMAP_FLAG] = Join.BEVEL;
        sJoinArray = joinArr;
        Align[] alignArr = new Align[CURSOR_AT_OR_BEFORE];
        alignArr[HINTING_OFF] = Align.LEFT;
        alignArr[HINTING_ON] = Align.CENTER;
        alignArr[FILTER_BITMAP_FLAG] = Align.RIGHT;
        sAlignArray = alignArr;
    }

    public Paint() {
        this((int) HINTING_OFF);
    }

    public Paint(int flags) {
        this.mBidiFlags = FILTER_BITMAP_FLAG;
        this.mNativePaint = native_init();
        setFlags(flags | DEFAULT_PAINT_FLAGS);
        this.mInvCompatScaling = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        this.mCompatScaling = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        setTextLocale(Locale.getDefault());
    }

    public Paint(Paint paint) {
        this.mBidiFlags = FILTER_BITMAP_FLAG;
        this.mNativePaint = native_initWithPaint(paint.mNativePaint);
        setClassVariablesFrom(paint);
    }

    public void reset() {
        native_reset(this.mNativePaint);
        setFlags(DEFAULT_PAINT_FLAGS);
        this.mColorFilter = null;
        this.mMaskFilter = null;
        this.mPathEffect = null;
        this.mRasterizer = null;
        this.mShader = null;
        this.mTypeface = null;
        this.mNativeTypeface = 0;
        this.mXfermode = null;
        this.mHasCompatScaling = false;
        this.mCompatScaling = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        this.mInvCompatScaling = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        this.mBidiFlags = FILTER_BITMAP_FLAG;
        setTextLocale(Locale.getDefault());
        setElegantTextHeight(false);
        this.mFontFeatureSettings = null;
    }

    public void set(Paint src) {
        if (this != src) {
            native_set(this.mNativePaint, src.mNativePaint);
            setClassVariablesFrom(src);
        }
    }

    private void setClassVariablesFrom(Paint paint) {
        this.mColorFilter = paint.mColorFilter;
        this.mMaskFilter = paint.mMaskFilter;
        this.mPathEffect = paint.mPathEffect;
        this.mRasterizer = paint.mRasterizer;
        if (paint.mShader != null) {
            this.mShader = paint.mShader.copy();
        } else {
            this.mShader = null;
        }
        this.mTypeface = paint.mTypeface;
        this.mNativeTypeface = paint.mNativeTypeface;
        this.mXfermode = paint.mXfermode;
        this.mHasCompatScaling = paint.mHasCompatScaling;
        this.mCompatScaling = paint.mCompatScaling;
        this.mInvCompatScaling = paint.mInvCompatScaling;
        this.mBidiFlags = paint.mBidiFlags;
        this.mLocale = paint.mLocale;
        this.mFontFeatureSettings = paint.mFontFeatureSettings;
    }

    public void setCompatibilityScaling(float factor) {
        if (((double) factor) == 1.0d) {
            this.mHasCompatScaling = false;
            this.mInvCompatScaling = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
            this.mCompatScaling = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
            return;
        }
        this.mHasCompatScaling = true;
        this.mCompatScaling = factor;
        this.mInvCompatScaling = LayoutParams.BRIGHTNESS_OVERRIDE_FULL / factor;
    }

    public int getBidiFlags() {
        return this.mBidiFlags;
    }

    public void setBidiFlags(int flags) {
        flags &= BIDI_FLAG_MASK;
        if (flags > BIDI_MAX_FLAG_VALUE) {
            throw new IllegalArgumentException("unknown bidi flag: " + flags);
        }
        this.mBidiFlags = flags;
    }

    public final boolean isAntiAlias() {
        return (getFlags() & HINTING_ON) != 0;
    }

    public final boolean isDither() {
        return (getFlags() & DITHER_FLAG) != 0;
    }

    public final boolean isLinearText() {
        return (getFlags() & LINEAR_TEXT_FLAG) != 0;
    }

    public final boolean isSubpixelText() {
        return (getFlags() & SUBPIXEL_TEXT_FLAG) != 0;
    }

    public final boolean isUnderlineText() {
        return (getFlags() & UNDERLINE_TEXT_FLAG) != 0;
    }

    public final boolean isStrikeThruText() {
        return (getFlags() & STRIKE_THRU_TEXT_FLAG) != 0;
    }

    public final boolean isFakeBoldText() {
        return (getFlags() & FAKE_BOLD_TEXT_FLAG) != 0;
    }

    public final boolean isFilterBitmap() {
        return (getFlags() & FILTER_BITMAP_FLAG) != 0;
    }

    public Style getStyle() {
        return sStyleArray[native_getStyle(this.mNativePaint)];
    }

    public void setStyle(Style style) {
        native_setStyle(this.mNativePaint, style.nativeInt);
    }

    public void setARGB(int a, int r, int g, int b) {
        setColor((((a << 24) | (r << STRIKE_THRU_TEXT_FLAG)) | (g << UNDERLINE_TEXT_FLAG)) | b);
    }

    public Cap getStrokeCap() {
        return sCapArray[native_getStrokeCap(this.mNativePaint)];
    }

    public void setStrokeCap(Cap cap) {
        native_setStrokeCap(this.mNativePaint, cap.nativeInt);
    }

    public Join getStrokeJoin() {
        return sJoinArray[native_getStrokeJoin(this.mNativePaint)];
    }

    public void setStrokeJoin(Join join) {
        native_setStrokeJoin(this.mNativePaint, join.nativeInt);
    }

    public boolean getFillPath(Path src, Path dst) {
        return native_getFillPath(this.mNativePaint, src.ni(), dst.ni());
    }

    public Shader getShader() {
        return this.mShader;
    }

    public Shader setShader(Shader shader) {
        long shaderNative = 0;
        if (shader != null) {
            shaderNative = shader.getNativeInstance();
        }
        native_setShader(this.mNativePaint, shaderNative);
        this.mShader = shader;
        return shader;
    }

    public ColorFilter getColorFilter() {
        return this.mColorFilter;
    }

    public ColorFilter setColorFilter(ColorFilter filter) {
        long filterNative = 0;
        if (filter != null) {
            filterNative = filter.native_instance;
        }
        native_setColorFilter(this.mNativePaint, filterNative);
        this.mColorFilter = filter;
        return filter;
    }

    public Xfermode getXfermode() {
        return this.mXfermode;
    }

    public Xfermode setXfermode(Xfermode xfermode) {
        long xfermodeNative = 0;
        if (xfermode != null) {
            xfermodeNative = xfermode.native_instance;
        }
        native_setXfermode(this.mNativePaint, xfermodeNative);
        this.mXfermode = xfermode;
        return xfermode;
    }

    public PathEffect getPathEffect() {
        return this.mPathEffect;
    }

    public PathEffect setPathEffect(PathEffect effect) {
        long effectNative = 0;
        if (effect != null) {
            effectNative = effect.native_instance;
        }
        native_setPathEffect(this.mNativePaint, effectNative);
        this.mPathEffect = effect;
        return effect;
    }

    public MaskFilter getMaskFilter() {
        return this.mMaskFilter;
    }

    public MaskFilter setMaskFilter(MaskFilter maskfilter) {
        long maskfilterNative = 0;
        if (maskfilter != null) {
            maskfilterNative = maskfilter.native_instance;
        }
        native_setMaskFilter(this.mNativePaint, maskfilterNative);
        this.mMaskFilter = maskfilter;
        return maskfilter;
    }

    public Typeface getTypeface() {
        return this.mTypeface;
    }

    public Typeface setTypeface(Typeface typeface) {
        long typefaceNative = 0;
        if (typeface != null) {
            typefaceNative = typeface.native_instance;
        }
        native_setTypeface(this.mNativePaint, typefaceNative);
        this.mTypeface = typeface;
        this.mNativeTypeface = typefaceNative;
        return typeface;
    }

    @Deprecated
    public Rasterizer getRasterizer() {
        return this.mRasterizer;
    }

    @Deprecated
    public Rasterizer setRasterizer(Rasterizer rasterizer) {
        long rasterizerNative = 0;
        if (rasterizer != null) {
            rasterizerNative = rasterizer.native_instance;
        }
        native_setRasterizer(this.mNativePaint, rasterizerNative);
        this.mRasterizer = rasterizer;
        return rasterizer;
    }

    public void setShadowLayer(float radius, float dx, float dy, int shadowColor) {
        native_setShadowLayer(this.mNativePaint, radius, dx, dy, shadowColor);
    }

    public void clearShadowLayer() {
        setShadowLayer(0.0f, 0.0f, 0.0f, HINTING_OFF);
    }

    public boolean hasShadowLayer() {
        return native_hasShadowLayer(this.mNativePaint);
    }

    public Align getTextAlign() {
        return sAlignArray[native_getTextAlign(this.mNativePaint)];
    }

    public void setTextAlign(Align align) {
        native_setTextAlign(this.mNativePaint, align.nativeInt);
    }

    public Locale getTextLocale() {
        return this.mLocale;
    }

    public void setTextLocale(Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException("locale cannot be null");
        } else if (!locale.equals(this.mLocale)) {
            this.mLocale = locale;
            native_setTextLocale(this.mNativePaint, locale.toString());
        }
    }

    public float getLetterSpacing() {
        return native_getLetterSpacing(this.mNativePaint);
    }

    public void setLetterSpacing(float letterSpacing) {
        native_setLetterSpacing(this.mNativePaint, letterSpacing);
    }

    public String getFontFeatureSettings() {
        return this.mFontFeatureSettings;
    }

    public void setFontFeatureSettings(String settings) {
        if (settings != null && settings.equals(ProxyInfo.LOCAL_EXCL_LIST)) {
            settings = null;
        }
        if (settings != null || this.mFontFeatureSettings != null) {
            if (settings == null || !settings.equals(this.mFontFeatureSettings)) {
                this.mFontFeatureSettings = settings;
                native_setFontFeatureSettings(this.mNativePaint, settings);
            }
        }
    }

    public FontMetrics getFontMetrics() {
        FontMetrics fm = new FontMetrics();
        getFontMetrics(fm);
        return fm;
    }

    public FontMetricsInt getFontMetricsInt() {
        FontMetricsInt fm = new FontMetricsInt();
        getFontMetricsInt(fm);
        return fm;
    }

    public float getFontSpacing() {
        return getFontMetrics(null);
    }

    public float measureText(char[] text, int index, int count) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        } else if ((index | count) < 0 || index + count > text.length) {
            throw new ArrayIndexOutOfBoundsException();
        } else if (text.length == 0 || count == 0) {
            return 0.0f;
        } else {
            if (!this.mHasCompatScaling) {
                return (float) Math.ceil((double) native_measureText(text, index, count, this.mBidiFlags));
            }
            float oldSize = getTextSize();
            setTextSize(this.mCompatScaling * oldSize);
            float w = native_measureText(text, index, count, this.mBidiFlags);
            setTextSize(oldSize);
            return (float) Math.ceil((double) (this.mInvCompatScaling * w));
        }
    }

    public float measureText(String text, int start, int end) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        } else if ((((start | end) | (end - start)) | (text.length() - end)) < 0) {
            throw new IndexOutOfBoundsException();
        } else if (text.length() == 0 || start == end) {
            return 0.0f;
        } else {
            if (!this.mHasCompatScaling) {
                return (float) Math.ceil((double) native_measureText(text, start, end, this.mBidiFlags));
            }
            float oldSize = getTextSize();
            setTextSize(this.mCompatScaling * oldSize);
            float w = native_measureText(text, start, end, this.mBidiFlags);
            setTextSize(oldSize);
            return (float) Math.ceil((double) (this.mInvCompatScaling * w));
        }
    }

    public float measureText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        } else if (text.length() == 0) {
            return 0.0f;
        } else {
            if (!this.mHasCompatScaling) {
                return (float) Math.ceil((double) native_measureText(text, this.mBidiFlags));
            }
            float oldSize = getTextSize();
            setTextSize(this.mCompatScaling * oldSize);
            float w = native_measureText(text, this.mBidiFlags);
            setTextSize(oldSize);
            return (float) Math.ceil((double) (this.mInvCompatScaling * w));
        }
    }

    public float measureText(CharSequence text, int start, int end) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        } else if ((((start | end) | (end - start)) | (text.length() - end)) < 0) {
            throw new IndexOutOfBoundsException();
        } else if (text.length() == 0 || start == end) {
            return 0.0f;
        } else {
            if (text instanceof String) {
                return measureText((String) text, start, end);
            }
            if ((text instanceof SpannedString) || (text instanceof SpannableString)) {
                return measureText(text.toString(), start, end);
            }
            if (text instanceof GraphicsOperations) {
                return ((GraphicsOperations) text).measureText(start, end, this);
            }
            char[] buf = TemporaryBuffer.obtain(end - start);
            TextUtils.getChars(text, start, end, buf, HINTING_OFF);
            float result = measureText(buf, (int) HINTING_OFF, end - start);
            TemporaryBuffer.recycle(buf);
            return result;
        }
    }

    public int breakText(char[] text, int index, int count, float maxWidth, float[] measuredWidth) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        } else if (index < 0 || text.length - index < Math.abs(count)) {
            throw new ArrayIndexOutOfBoundsException();
        } else if (text.length == 0 || count == 0) {
            return HINTING_OFF;
        } else {
            if (this.mHasCompatScaling) {
                float oldSize = getTextSize();
                setTextSize(this.mCompatScaling * oldSize);
                int res = native_breakText(this.mNativePaint, this.mNativeTypeface, text, index, count, maxWidth * this.mCompatScaling, this.mBidiFlags, measuredWidth);
                setTextSize(oldSize);
                if (measuredWidth == null) {
                    return res;
                }
                measuredWidth[HINTING_OFF] = measuredWidth[HINTING_OFF] * this.mInvCompatScaling;
                return res;
            }
            return native_breakText(this.mNativePaint, this.mNativeTypeface, text, index, count, maxWidth, this.mBidiFlags, measuredWidth);
        }
    }

    public int breakText(CharSequence text, int start, int end, boolean measureForwards, float maxWidth, float[] measuredWidth) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        } else if ((((start | end) | (end - start)) | (text.length() - end)) < 0) {
            throw new IndexOutOfBoundsException();
        } else if (text.length() == 0 || start == end) {
            return HINTING_OFF;
        } else {
            if (start == 0 && (text instanceof String) && end == text.length()) {
                return breakText((String) text, measureForwards, maxWidth, measuredWidth);
            }
            int result;
            char[] buf = TemporaryBuffer.obtain(end - start);
            TextUtils.getChars(text, start, end, buf, HINTING_OFF);
            if (measureForwards) {
                result = breakText(buf, HINTING_OFF, end - start, maxWidth, measuredWidth);
            } else {
                result = breakText(buf, HINTING_OFF, -(end - start), maxWidth, measuredWidth);
            }
            TemporaryBuffer.recycle(buf);
            return result;
        }
    }

    public int breakText(String text, boolean measureForwards, float maxWidth, float[] measuredWidth) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        } else if (text.length() == 0) {
            return HINTING_OFF;
        } else {
            if (this.mHasCompatScaling) {
                float oldSize = getTextSize();
                setTextSize(this.mCompatScaling * oldSize);
                int res = native_breakText(this.mNativePaint, this.mNativeTypeface, text, measureForwards, maxWidth * this.mCompatScaling, this.mBidiFlags, measuredWidth);
                setTextSize(oldSize);
                if (measuredWidth == null) {
                    return res;
                }
                measuredWidth[HINTING_OFF] = measuredWidth[HINTING_OFF] * this.mInvCompatScaling;
                return res;
            }
            return native_breakText(this.mNativePaint, this.mNativeTypeface, text, measureForwards, maxWidth, this.mBidiFlags, measuredWidth);
        }
    }

    public int getTextWidths(char[] text, int index, int count, float[] widths) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        } else if ((index | count) < 0 || index + count > text.length || count > widths.length) {
            throw new ArrayIndexOutOfBoundsException();
        } else if (text.length == 0 || count == 0) {
            return HINTING_OFF;
        } else {
            if (this.mHasCompatScaling) {
                float oldSize = getTextSize();
                setTextSize(this.mCompatScaling * oldSize);
                int res = native_getTextWidths(this.mNativePaint, this.mNativeTypeface, text, index, count, this.mBidiFlags, widths);
                setTextSize(oldSize);
                for (int i = HINTING_OFF; i < res; i += HINTING_ON) {
                    widths[i] = widths[i] * this.mInvCompatScaling;
                }
                return res;
            }
            return native_getTextWidths(this.mNativePaint, this.mNativeTypeface, text, index, count, this.mBidiFlags, widths);
        }
    }

    public int getTextWidths(CharSequence text, int start, int end, float[] widths) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        } else if ((((start | end) | (end - start)) | (text.length() - end)) < 0) {
            throw new IndexOutOfBoundsException();
        } else if (end - start > widths.length) {
            throw new ArrayIndexOutOfBoundsException();
        } else if (text.length() == 0 || start == end) {
            return HINTING_OFF;
        } else {
            if (text instanceof String) {
                return getTextWidths((String) text, start, end, widths);
            }
            if ((text instanceof SpannedString) || (text instanceof SpannableString)) {
                return getTextWidths(text.toString(), start, end, widths);
            }
            if (text instanceof GraphicsOperations) {
                return ((GraphicsOperations) text).getTextWidths(start, end, widths, this);
            }
            char[] buf = TemporaryBuffer.obtain(end - start);
            TextUtils.getChars(text, start, end, buf, HINTING_OFF);
            int result = getTextWidths(buf, (int) HINTING_OFF, end - start, widths);
            TemporaryBuffer.recycle(buf);
            return result;
        }
    }

    public int getTextWidths(String text, int start, int end, float[] widths) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        } else if ((((start | end) | (end - start)) | (text.length() - end)) < 0) {
            throw new IndexOutOfBoundsException();
        } else if (end - start > widths.length) {
            throw new ArrayIndexOutOfBoundsException();
        } else if (text.length() == 0 || start == end) {
            return HINTING_OFF;
        } else {
            if (this.mHasCompatScaling) {
                float oldSize = getTextSize();
                setTextSize(this.mCompatScaling * oldSize);
                int res = native_getTextWidths(this.mNativePaint, this.mNativeTypeface, text, start, end, this.mBidiFlags, widths);
                setTextSize(oldSize);
                for (int i = HINTING_OFF; i < res; i += HINTING_ON) {
                    widths[i] = widths[i] * this.mInvCompatScaling;
                }
                return res;
            }
            return native_getTextWidths(this.mNativePaint, this.mNativeTypeface, text, start, end, this.mBidiFlags, widths);
        }
    }

    public int getTextWidths(String text, float[] widths) {
        return getTextWidths(text, (int) HINTING_OFF, text.length(), widths);
    }

    public float getTextRunAdvances(char[] chars, int index, int count, int contextIndex, int contextCount, boolean isRtl, float[] advances, int advancesIndex) {
        if (chars == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if (((advances == null ? HINTING_OFF : advances.length - (advancesIndex + count)) | ((chars.length - (contextIndex + contextCount)) | (((((((index | count) | contextIndex) | contextCount) | advancesIndex) | (index - contextIndex)) | (contextCount - count)) | ((contextIndex + contextCount) - (index + count))))) < 0) {
            throw new IndexOutOfBoundsException();
        } else if (chars.length == 0 || count == 0) {
            return 0.0f;
        } else {
            if (!this.mHasCompatScaling) {
                return native_getTextRunAdvances(this.mNativePaint, this.mNativeTypeface, chars, index, count, contextIndex, contextCount, isRtl, advances, advancesIndex);
            }
            float oldSize = getTextSize();
            setTextSize(this.mCompatScaling * oldSize);
            float res = native_getTextRunAdvances(this.mNativePaint, this.mNativeTypeface, chars, index, count, contextIndex, contextCount, isRtl, advances, advancesIndex);
            setTextSize(oldSize);
            if (advances != null) {
                int i = advancesIndex;
                int e = i + count;
                while (i < e) {
                    advances[i] = advances[i] * this.mInvCompatScaling;
                    i += HINTING_ON;
                }
            }
            return this.mInvCompatScaling * res;
        }
    }

    public float getTextRunAdvances(CharSequence text, int start, int end, int contextStart, int contextEnd, boolean isRtl, float[] advances, int advancesIndex) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if (((advances == null ? HINTING_OFF : (advances.length - advancesIndex) - (end - start)) | ((text.length() - contextEnd) | (((((((start | end) | contextStart) | contextEnd) | advancesIndex) | (end - start)) | (start - contextStart)) | (contextEnd - end)))) < 0) {
            throw new IndexOutOfBoundsException();
        } else if (text instanceof String) {
            return getTextRunAdvances((String) text, start, end, contextStart, contextEnd, isRtl, advances, advancesIndex);
        } else {
            if ((text instanceof SpannedString) || (text instanceof SpannableString)) {
                return getTextRunAdvances(text.toString(), start, end, contextStart, contextEnd, isRtl, advances, advancesIndex);
            }
            if (text instanceof GraphicsOperations) {
                return ((GraphicsOperations) text).getTextRunAdvances(start, end, contextStart, contextEnd, isRtl, advances, advancesIndex, this);
            }
            if (text.length() == 0 || end == start) {
                return 0.0f;
            }
            int contextLen = contextEnd - contextStart;
            int len = end - start;
            char[] buf = TemporaryBuffer.obtain(contextLen);
            TextUtils.getChars(text, contextStart, contextEnd, buf, HINTING_OFF);
            float result = getTextRunAdvances(buf, start - contextStart, len, (int) HINTING_OFF, contextLen, isRtl, advances, advancesIndex);
            TemporaryBuffer.recycle(buf);
            return result;
        }
    }

    public float getTextRunAdvances(String text, int start, int end, int contextStart, int contextEnd, boolean isRtl, float[] advances, int advancesIndex) {
        if (text == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if (((advances == null ? HINTING_OFF : (advances.length - advancesIndex) - (end - start)) | ((text.length() - contextEnd) | (((((((start | end) | contextStart) | contextEnd) | advancesIndex) | (end - start)) | (start - contextStart)) | (contextEnd - end)))) < 0) {
            throw new IndexOutOfBoundsException();
        } else if (text.length() == 0 || start == end) {
            return 0.0f;
        } else {
            if (!this.mHasCompatScaling) {
                return native_getTextRunAdvances(this.mNativePaint, this.mNativeTypeface, text, start, end, contextStart, contextEnd, isRtl, advances, advancesIndex);
            }
            float oldSize = getTextSize();
            setTextSize(this.mCompatScaling * oldSize);
            float totalAdvance = native_getTextRunAdvances(this.mNativePaint, this.mNativeTypeface, text, start, end, contextStart, contextEnd, isRtl, advances, advancesIndex);
            setTextSize(oldSize);
            if (advances != null) {
                int i = advancesIndex;
                int e = i + (end - start);
                while (i < e) {
                    advances[i] = advances[i] * this.mInvCompatScaling;
                    i += HINTING_ON;
                }
            }
            return this.mInvCompatScaling * totalAdvance;
        }
    }

    public int getTextRunCursor(char[] text, int contextStart, int contextLength, int dir, int offset, int cursorOpt) {
        int contextEnd = contextStart + contextLength;
        if ((((((((contextStart | contextEnd) | offset) | (contextEnd - contextStart)) | (offset - contextStart)) | (contextEnd - offset)) | (text.length - contextEnd)) | cursorOpt) >= 0 && cursorOpt <= DITHER_FLAG) {
            return native_getTextRunCursor(this.mNativePaint, text, contextStart, contextLength, dir, offset, cursorOpt);
        }
        throw new IndexOutOfBoundsException();
    }

    public int getTextRunCursor(CharSequence text, int contextStart, int contextEnd, int dir, int offset, int cursorOpt) {
        if ((text instanceof String) || (text instanceof SpannedString) || (text instanceof SpannableString)) {
            return getTextRunCursor(text.toString(), contextStart, contextEnd, dir, offset, cursorOpt);
        }
        if (text instanceof GraphicsOperations) {
            return ((GraphicsOperations) text).getTextRunCursor(contextStart, contextEnd, dir, offset, cursorOpt, this);
        }
        int contextLen = contextEnd - contextStart;
        char[] buf = TemporaryBuffer.obtain(contextLen);
        TextUtils.getChars(text, contextStart, contextEnd, buf, HINTING_OFF);
        int result = getTextRunCursor(buf, (int) HINTING_OFF, contextLen, dir, offset - contextStart, cursorOpt);
        TemporaryBuffer.recycle(buf);
        return result;
    }

    public int getTextRunCursor(String text, int contextStart, int contextEnd, int dir, int offset, int cursorOpt) {
        if ((((((((contextStart | contextEnd) | offset) | (contextEnd - contextStart)) | (offset - contextStart)) | (contextEnd - offset)) | (text.length() - contextEnd)) | cursorOpt) >= 0 && cursorOpt <= DITHER_FLAG) {
            return native_getTextRunCursor(this.mNativePaint, text, contextStart, contextEnd, dir, offset, cursorOpt);
        }
        throw new IndexOutOfBoundsException();
    }

    public void getTextPath(char[] text, int index, int count, float x, float y, Path path) {
        if ((index | count) < 0 || index + count > text.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        native_getTextPath(this.mNativePaint, this.mNativeTypeface, this.mBidiFlags, text, index, count, x, y, path.ni());
    }

    public void getTextPath(String text, int start, int end, float x, float y, Path path) {
        if ((((start | end) | (end - start)) | (text.length() - end)) < 0) {
            throw new IndexOutOfBoundsException();
        }
        native_getTextPath(this.mNativePaint, this.mNativeTypeface, this.mBidiFlags, text, start, end, x, y, path.ni());
    }

    public void getTextBounds(String text, int start, int end, Rect bounds) {
        if ((((start | end) | (end - start)) | (text.length() - end)) < 0) {
            throw new IndexOutOfBoundsException();
        } else if (bounds == null) {
            throw new NullPointerException("need bounds Rect");
        } else {
            nativeGetStringBounds(this.mNativePaint, this.mNativeTypeface, text, start, end, this.mBidiFlags, bounds);
        }
    }

    public void getTextBounds(char[] text, int index, int count, Rect bounds) {
        if ((index | count) < 0 || index + count > text.length) {
            throw new ArrayIndexOutOfBoundsException();
        } else if (bounds == null) {
            throw new NullPointerException("need bounds Rect");
        } else {
            nativeGetCharArrayBounds(this.mNativePaint, this.mNativeTypeface, text, index, count, this.mBidiFlags, bounds);
        }
    }

    protected void finalize() throws Throwable {
        try {
            finalizer(this.mNativePaint);
        } finally {
            super.finalize();
        }
    }
}
