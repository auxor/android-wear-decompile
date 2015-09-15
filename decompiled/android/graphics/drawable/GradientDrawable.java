package android.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Path.FillType;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable.ConstantState;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.SpellChecker;
import com.android.internal.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class GradientDrawable extends Drawable {
    private static final float DEFAULT_INNER_RADIUS_RATIO = 3.0f;
    private static final float DEFAULT_THICKNESS_RATIO = 9.0f;
    public static final int LINE = 2;
    public static final int LINEAR_GRADIENT = 0;
    public static final int OVAL = 1;
    public static final int RADIAL_GRADIENT = 1;
    private static final int RADIUS_TYPE_FRACTION = 1;
    private static final int RADIUS_TYPE_FRACTION_PARENT = 2;
    private static final int RADIUS_TYPE_PIXELS = 0;
    public static final int RECTANGLE = 0;
    public static final int RING = 3;
    public static final int SWEEP_GRADIENT = 2;
    private int mAlpha;
    private ColorFilter mColorFilter;
    private final Paint mFillPaint;
    private boolean mGradientIsDirty;
    private float mGradientRadius;
    private GradientState mGradientState;
    private Paint mLayerPaint;
    private boolean mMutated;
    private Rect mPadding;
    private final Path mPath;
    private boolean mPathIsDirty;
    private final RectF mRect;
    private Path mRingPath;
    private Paint mStrokePaint;
    private PorterDuffColorFilter mTintFilter;

    /* renamed from: android.graphics.drawable.GradientDrawable.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation;

        static {
            $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation = new int[Orientation.values().length];
            try {
                $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation[Orientation.TOP_BOTTOM.ordinal()] = GradientDrawable.RADIUS_TYPE_FRACTION;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation[Orientation.TR_BL.ordinal()] = GradientDrawable.SWEEP_GRADIENT;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation[Orientation.RIGHT_LEFT.ordinal()] = GradientDrawable.RING;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation[Orientation.BR_TL.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation[Orientation.BOTTOM_TOP.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation[Orientation.BL_TR.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation[Orientation.LEFT_RIGHT.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    static final class GradientState extends ConstantState {
        public int mAngle;
        int[] mAttrCorners;
        int[] mAttrGradient;
        int[] mAttrPadding;
        int[] mAttrSize;
        int[] mAttrSolid;
        int[] mAttrStroke;
        float mCenterX;
        float mCenterY;
        public int mChangingConfigurations;
        public ColorStateList mColorStateList;
        public int[] mColors;
        public boolean mDither;
        public int mGradient;
        float mGradientRadius;
        int mGradientRadiusType;
        public int mHeight;
        public int mInnerRadius;
        public float mInnerRadiusRatio;
        boolean mOpaqueOverBounds;
        boolean mOpaqueOverShape;
        public Orientation mOrientation;
        public Rect mPadding;
        public float[] mPositions;
        public float mRadius;
        public float[] mRadiusArray;
        public int mShape;
        public ColorStateList mStrokeColorStateList;
        public float mStrokeDashGap;
        public float mStrokeDashWidth;
        public int mStrokeWidth;
        public int[] mTempColors;
        public float[] mTempPositions;
        int[] mThemeAttrs;
        public int mThickness;
        public float mThicknessRatio;
        ColorStateList mTint;
        Mode mTintMode;
        boolean mUseLevel;
        boolean mUseLevelForShape;
        public int mWidth;

        GradientState(Orientation orientation, int[] colors) {
            this.mShape = GradientDrawable.RECTANGLE;
            this.mGradient = GradientDrawable.RECTANGLE;
            this.mAngle = GradientDrawable.RECTANGLE;
            this.mStrokeWidth = -1;
            this.mStrokeDashWidth = 0.0f;
            this.mStrokeDashGap = 0.0f;
            this.mRadius = 0.0f;
            this.mRadiusArray = null;
            this.mPadding = null;
            this.mWidth = -1;
            this.mHeight = -1;
            this.mInnerRadiusRatio = GradientDrawable.DEFAULT_INNER_RADIUS_RATIO;
            this.mThicknessRatio = GradientDrawable.DEFAULT_THICKNESS_RATIO;
            this.mInnerRadius = -1;
            this.mThickness = -1;
            this.mDither = false;
            this.mCenterX = 0.5f;
            this.mCenterY = 0.5f;
            this.mGradientRadius = 0.5f;
            this.mGradientRadiusType = GradientDrawable.RECTANGLE;
            this.mUseLevel = false;
            this.mUseLevelForShape = true;
            this.mTint = null;
            this.mTintMode = Drawable.DEFAULT_TINT_MODE;
            this.mOrientation = orientation;
            setColors(colors);
        }

        public GradientState(GradientState state) {
            this.mShape = GradientDrawable.RECTANGLE;
            this.mGradient = GradientDrawable.RECTANGLE;
            this.mAngle = GradientDrawable.RECTANGLE;
            this.mStrokeWidth = -1;
            this.mStrokeDashWidth = 0.0f;
            this.mStrokeDashGap = 0.0f;
            this.mRadius = 0.0f;
            this.mRadiusArray = null;
            this.mPadding = null;
            this.mWidth = -1;
            this.mHeight = -1;
            this.mInnerRadiusRatio = GradientDrawable.DEFAULT_INNER_RADIUS_RATIO;
            this.mThicknessRatio = GradientDrawable.DEFAULT_THICKNESS_RATIO;
            this.mInnerRadius = -1;
            this.mThickness = -1;
            this.mDither = false;
            this.mCenterX = 0.5f;
            this.mCenterY = 0.5f;
            this.mGradientRadius = 0.5f;
            this.mGradientRadiusType = GradientDrawable.RECTANGLE;
            this.mUseLevel = false;
            this.mUseLevelForShape = true;
            this.mTint = null;
            this.mTintMode = Drawable.DEFAULT_TINT_MODE;
            this.mChangingConfigurations = state.mChangingConfigurations;
            this.mShape = state.mShape;
            this.mGradient = state.mGradient;
            this.mAngle = state.mAngle;
            this.mOrientation = state.mOrientation;
            this.mColorStateList = state.mColorStateList;
            if (state.mColors != null) {
                this.mColors = (int[]) state.mColors.clone();
            }
            if (state.mPositions != null) {
                this.mPositions = (float[]) state.mPositions.clone();
            }
            this.mStrokeColorStateList = state.mStrokeColorStateList;
            this.mStrokeWidth = state.mStrokeWidth;
            this.mStrokeDashWidth = state.mStrokeDashWidth;
            this.mStrokeDashGap = state.mStrokeDashGap;
            this.mRadius = state.mRadius;
            if (state.mRadiusArray != null) {
                this.mRadiusArray = (float[]) state.mRadiusArray.clone();
            }
            if (state.mPadding != null) {
                this.mPadding = new Rect(state.mPadding);
            }
            this.mWidth = state.mWidth;
            this.mHeight = state.mHeight;
            this.mInnerRadiusRatio = state.mInnerRadiusRatio;
            this.mThicknessRatio = state.mThicknessRatio;
            this.mInnerRadius = state.mInnerRadius;
            this.mThickness = state.mThickness;
            this.mDither = state.mDither;
            this.mCenterX = state.mCenterX;
            this.mCenterY = state.mCenterY;
            this.mGradientRadius = state.mGradientRadius;
            this.mGradientRadiusType = state.mGradientRadiusType;
            this.mUseLevel = state.mUseLevel;
            this.mUseLevelForShape = state.mUseLevelForShape;
            this.mOpaqueOverBounds = state.mOpaqueOverBounds;
            this.mOpaqueOverShape = state.mOpaqueOverShape;
            this.mTint = state.mTint;
            this.mTintMode = state.mTintMode;
            this.mThemeAttrs = state.mThemeAttrs;
            this.mAttrSize = state.mAttrSize;
            this.mAttrGradient = state.mAttrGradient;
            this.mAttrSolid = state.mAttrSolid;
            this.mAttrStroke = state.mAttrStroke;
            this.mAttrCorners = state.mAttrCorners;
            this.mAttrPadding = state.mAttrPadding;
        }

        public boolean canApplyTheme() {
            return (this.mThemeAttrs == null && this.mAttrSize == null && this.mAttrGradient == null && this.mAttrSolid == null && this.mAttrStroke == null && this.mAttrCorners == null && this.mAttrPadding == null && !super.canApplyTheme()) ? false : true;
        }

        public Drawable newDrawable() {
            return new GradientDrawable();
        }

        public Drawable newDrawable(Resources res) {
            return new GradientDrawable();
        }

        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }

        public void setShape(int shape) {
            this.mShape = shape;
            computeOpacity();
        }

        public void setGradientType(int gradient) {
            this.mGradient = gradient;
        }

        public void setGradientCenter(float x, float y) {
            this.mCenterX = x;
            this.mCenterY = y;
        }

        public void setColors(int[] colors) {
            this.mColors = colors;
            this.mColorStateList = null;
            computeOpacity();
        }

        public void setColorStateList(ColorStateList colorStateList) {
            this.mColors = null;
            this.mColorStateList = colorStateList;
            computeOpacity();
        }

        private void computeOpacity() {
            boolean z = true;
            this.mOpaqueOverBounds = false;
            this.mOpaqueOverShape = false;
            if (this.mColors != null) {
                int i = GradientDrawable.RECTANGLE;
                while (i < this.mColors.length) {
                    if (GradientDrawable.isOpaque(this.mColors[i])) {
                        i += GradientDrawable.RADIUS_TYPE_FRACTION;
                    } else {
                        return;
                    }
                }
            }
            if (this.mColors != null || this.mColorStateList != null) {
                this.mOpaqueOverShape = true;
                if (!(this.mShape == 0 && this.mRadius <= 0.0f && this.mRadiusArray == null)) {
                    z = false;
                }
                this.mOpaqueOverBounds = z;
            }
        }

        public void setStroke(int width, ColorStateList colorStateList, float dashWidth, float dashGap) {
            this.mStrokeWidth = width;
            this.mStrokeColorStateList = colorStateList;
            this.mStrokeDashWidth = dashWidth;
            this.mStrokeDashGap = dashGap;
            computeOpacity();
        }

        public void setCornerRadius(float radius) {
            if (radius < 0.0f) {
                radius = 0.0f;
            }
            this.mRadius = radius;
            this.mRadiusArray = null;
        }

        public void setCornerRadii(float[] radii) {
            this.mRadiusArray = radii;
            if (radii == null) {
                this.mRadius = 0.0f;
            }
        }

        public void setSize(int width, int height) {
            this.mWidth = width;
            this.mHeight = height;
        }

        public void setGradientRadius(float gradientRadius, int type) {
            this.mGradientRadius = gradientRadius;
            this.mGradientRadiusType = type;
        }
    }

    public enum Orientation {
        TOP_BOTTOM,
        TR_BL,
        RIGHT_LEFT,
        BR_TL,
        BOTTOM_TOP,
        BL_TR,
        LEFT_RIGHT,
        TL_BR
    }

    public GradientDrawable() {
        this(new GradientState(Orientation.TOP_BOTTOM, null));
    }

    public GradientDrawable(Orientation orientation, int[] colors) {
        this(new GradientState(orientation, colors));
    }

    public boolean getPadding(Rect padding) {
        if (this.mPadding == null) {
            return super.getPadding(padding);
        }
        padding.set(this.mPadding);
        return true;
    }

    public void setCornerRadii(float[] radii) {
        this.mGradientState.setCornerRadii(radii);
        this.mPathIsDirty = true;
        invalidateSelf();
    }

    public void setCornerRadius(float radius) {
        this.mGradientState.setCornerRadius(radius);
        this.mPathIsDirty = true;
        invalidateSelf();
    }

    public void setStroke(int width, int color) {
        setStroke(width, color, 0.0f, 0.0f);
    }

    public void setStroke(int width, ColorStateList colorStateList) {
        setStroke(width, colorStateList, 0.0f, 0.0f);
    }

    public void setStroke(int width, int color, float dashWidth, float dashGap) {
        this.mGradientState.setStroke(width, ColorStateList.valueOf(color), dashWidth, dashGap);
        setStrokeInternal(width, color, dashWidth, dashGap);
    }

    public void setStroke(int width, ColorStateList colorStateList, float dashWidth, float dashGap) {
        int color;
        this.mGradientState.setStroke(width, colorStateList, dashWidth, dashGap);
        if (colorStateList == null) {
            color = RECTANGLE;
        } else {
            color = colorStateList.getColorForState(getState(), RECTANGLE);
        }
        setStrokeInternal(width, color, dashWidth, dashGap);
    }

    private void setStrokeInternal(int width, int color, float dashWidth, float dashGap) {
        if (this.mStrokePaint == null) {
            this.mStrokePaint = new Paint((int) RADIUS_TYPE_FRACTION);
            this.mStrokePaint.setStyle(Style.STROKE);
        }
        this.mStrokePaint.setStrokeWidth((float) width);
        this.mStrokePaint.setColor(color);
        DashPathEffect e = null;
        if (dashWidth > 0.0f) {
            float[] fArr = new float[SWEEP_GRADIENT];
            fArr[RECTANGLE] = dashWidth;
            fArr[RADIUS_TYPE_FRACTION] = dashGap;
            e = new DashPathEffect(fArr, 0.0f);
        }
        this.mStrokePaint.setPathEffect(e);
        invalidateSelf();
    }

    public void setSize(int width, int height) {
        this.mGradientState.setSize(width, height);
        this.mPathIsDirty = true;
        invalidateSelf();
    }

    public void setShape(int shape) {
        this.mRingPath = null;
        this.mPathIsDirty = true;
        this.mGradientState.setShape(shape);
        invalidateSelf();
    }

    public void setGradientType(int gradient) {
        this.mGradientState.setGradientType(gradient);
        this.mGradientIsDirty = true;
        invalidateSelf();
    }

    public void setGradientCenter(float x, float y) {
        this.mGradientState.setGradientCenter(x, y);
        this.mGradientIsDirty = true;
        invalidateSelf();
    }

    public void setGradientRadius(float gradientRadius) {
        this.mGradientState.setGradientRadius(gradientRadius, RECTANGLE);
        this.mGradientIsDirty = true;
        invalidateSelf();
    }

    public float getGradientRadius() {
        if (this.mGradientState.mGradient != RADIUS_TYPE_FRACTION) {
            return 0.0f;
        }
        ensureValidRect();
        return this.mGradientRadius;
    }

    public void setUseLevel(boolean useLevel) {
        this.mGradientState.mUseLevel = useLevel;
        this.mGradientIsDirty = true;
        invalidateSelf();
    }

    private int modulateAlpha(int alpha) {
        return (alpha * (this.mAlpha + (this.mAlpha >> 7))) >> 8;
    }

    public Orientation getOrientation() {
        return this.mGradientState.mOrientation;
    }

    public void setOrientation(Orientation orientation) {
        this.mGradientState.mOrientation = orientation;
        this.mGradientIsDirty = true;
        invalidateSelf();
    }

    public void setColors(int[] colors) {
        this.mGradientState.setColors(colors);
        this.mGradientIsDirty = true;
        invalidateSelf();
    }

    public void draw(Canvas canvas) {
        if (ensureValidRect()) {
            float rad;
            int prevFillAlpha = this.mFillPaint.getAlpha();
            int prevStrokeAlpha = this.mStrokePaint != null ? this.mStrokePaint.getAlpha() : RECTANGLE;
            int currFillAlpha = modulateAlpha(prevFillAlpha);
            int currStrokeAlpha = modulateAlpha(prevStrokeAlpha);
            boolean haveStroke = currStrokeAlpha > 0 && this.mStrokePaint != null && this.mStrokePaint.getStrokeWidth() > 0.0f;
            boolean haveFill = currFillAlpha > 0;
            GradientState st = this.mGradientState;
            ColorFilter colorFilter = this.mColorFilter != null ? this.mColorFilter : this.mTintFilter;
            boolean useLayer = haveStroke && haveFill && st.mShape != SWEEP_GRADIENT && currStrokeAlpha < EditorInfo.IME_MASK_ACTION && (this.mAlpha < EditorInfo.IME_MASK_ACTION || colorFilter != null);
            if (useLayer) {
                if (this.mLayerPaint == null) {
                    this.mLayerPaint = new Paint();
                }
                this.mLayerPaint.setDither(st.mDither);
                this.mLayerPaint.setAlpha(this.mAlpha);
                this.mLayerPaint.setColorFilter(colorFilter);
                rad = this.mStrokePaint.getStrokeWidth();
                canvas.saveLayer(this.mRect.left - rad, this.mRect.top - rad, this.mRect.right + rad, this.mRect.bottom + rad, this.mLayerPaint, 4);
                this.mFillPaint.setColorFilter(null);
                this.mStrokePaint.setColorFilter(null);
            } else {
                this.mFillPaint.setAlpha(currFillAlpha);
                this.mFillPaint.setDither(st.mDither);
                this.mFillPaint.setColorFilter(colorFilter);
                if (colorFilter != null && st.mColorStateList == null) {
                    this.mFillPaint.setColor(this.mAlpha << 24);
                }
                if (haveStroke) {
                    this.mStrokePaint.setAlpha(currStrokeAlpha);
                    this.mStrokePaint.setDither(st.mDither);
                    this.mStrokePaint.setColorFilter(colorFilter);
                }
            }
            switch (st.mShape) {
                case RECTANGLE /*0*/:
                    if (st.mRadiusArray == null) {
                        if (st.mRadius <= 0.0f) {
                            if (!(this.mFillPaint.getColor() == 0 && colorFilter == null && this.mFillPaint.getShader() == null)) {
                                canvas.drawRect(this.mRect, this.mFillPaint);
                            }
                            if (haveStroke) {
                                canvas.drawRect(this.mRect, this.mStrokePaint);
                                break;
                            }
                        }
                        rad = Math.min(st.mRadius, Math.min(this.mRect.width(), this.mRect.height()) * 0.5f);
                        canvas.drawRoundRect(this.mRect, rad, rad, this.mFillPaint);
                        if (haveStroke) {
                            canvas.drawRoundRect(this.mRect, rad, rad, this.mStrokePaint);
                            break;
                        }
                    }
                    buildPathIfDirty();
                    canvas.drawPath(this.mPath, this.mFillPaint);
                    if (haveStroke) {
                        canvas.drawPath(this.mPath, this.mStrokePaint);
                        break;
                    }
                    break;
                case RADIUS_TYPE_FRACTION /*1*/:
                    canvas.drawOval(this.mRect, this.mFillPaint);
                    if (haveStroke) {
                        canvas.drawOval(this.mRect, this.mStrokePaint);
                        break;
                    }
                    break;
                case SWEEP_GRADIENT /*2*/:
                    RectF r = this.mRect;
                    float y = r.centerY();
                    if (haveStroke) {
                        canvas.drawLine(r.left, y, r.right, y, this.mStrokePaint);
                        break;
                    }
                    break;
                case RING /*3*/:
                    Path path = buildRing(st);
                    canvas.drawPath(path, this.mFillPaint);
                    if (haveStroke) {
                        canvas.drawPath(path, this.mStrokePaint);
                        break;
                    }
                    break;
            }
            if (useLayer) {
                canvas.restore();
                return;
            }
            this.mFillPaint.setAlpha(prevFillAlpha);
            if (haveStroke) {
                this.mStrokePaint.setAlpha(prevStrokeAlpha);
            }
        }
    }

    private void buildPathIfDirty() {
        GradientState st = this.mGradientState;
        if (this.mPathIsDirty) {
            ensureValidRect();
            this.mPath.reset();
            this.mPath.addRoundRect(this.mRect, st.mRadiusArray, Direction.CW);
            this.mPathIsDirty = false;
        }
    }

    private Path buildRing(GradientState st) {
        if (this.mRingPath != null && (!st.mUseLevelForShape || !this.mPathIsDirty)) {
            return this.mRingPath;
        }
        float sweep;
        float radius;
        this.mPathIsDirty = false;
        if (st.mUseLevelForShape) {
            sweep = (((float) getLevel()) * 360.0f) / SensorManager.LIGHT_OVERCAST;
        } else {
            sweep = 360.0f;
        }
        RectF bounds = new RectF(this.mRect);
        float x = bounds.width() / 2.0f;
        float y = bounds.height() / 2.0f;
        float thickness = st.mThickness != -1 ? (float) st.mThickness : bounds.width() / st.mThicknessRatio;
        if (st.mInnerRadius != -1) {
            radius = (float) st.mInnerRadius;
        } else {
            radius = bounds.width() / st.mInnerRadiusRatio;
        }
        RectF innerBounds = new RectF(bounds);
        innerBounds.inset(x - radius, y - radius);
        bounds = new RectF(innerBounds);
        bounds.inset(-thickness, -thickness);
        if (this.mRingPath == null) {
            this.mRingPath = new Path();
        } else {
            this.mRingPath.reset();
        }
        Path ringPath = this.mRingPath;
        if (sweep >= 360.0f || sweep <= -360.0f) {
            ringPath.addOval(bounds, Direction.CW);
            ringPath.addOval(innerBounds, Direction.CCW);
            return ringPath;
        }
        ringPath.setFillType(FillType.EVEN_ODD);
        ringPath.moveTo(x + radius, y);
        ringPath.lineTo((x + radius) + thickness, y);
        ringPath.arcTo(bounds, 0.0f, sweep, false);
        ringPath.arcTo(innerBounds, sweep, -sweep, false);
        ringPath.close();
        return ringPath;
    }

    public void setColor(int argb) {
        this.mGradientState.setColorStateList(ColorStateList.valueOf(argb));
        this.mFillPaint.setColor(argb);
        invalidateSelf();
    }

    public void setColor(ColorStateList colorStateList) {
        int color;
        this.mGradientState.setColorStateList(colorStateList);
        if (colorStateList == null) {
            color = RECTANGLE;
        } else {
            color = colorStateList.getColorForState(getState(), RECTANGLE);
        }
        this.mFillPaint.setColor(color);
        invalidateSelf();
    }

    protected boolean onStateChange(int[] stateSet) {
        boolean invalidateSelf = false;
        GradientState s = this.mGradientState;
        ColorStateList stateList = s.mColorStateList;
        if (stateList != null) {
            int newColor = stateList.getColorForState(stateSet, RECTANGLE);
            if (this.mFillPaint.getColor() != newColor) {
                this.mFillPaint.setColor(newColor);
                invalidateSelf = true;
            }
        }
        Paint strokePaint = this.mStrokePaint;
        if (strokePaint != null) {
            ColorStateList strokeStateList = s.mStrokeColorStateList;
            if (strokeStateList != null) {
                int newStrokeColor = strokeStateList.getColorForState(stateSet, RECTANGLE);
                if (strokePaint.getColor() != newStrokeColor) {
                    strokePaint.setColor(newStrokeColor);
                    invalidateSelf = true;
                }
            }
        }
        if (!(s.mTint == null || s.mTintMode == null)) {
            this.mTintFilter = updateTintFilter(this.mTintFilter, s.mTint, s.mTintMode);
            invalidateSelf = true;
        }
        if (!invalidateSelf) {
            return false;
        }
        invalidateSelf();
        return true;
    }

    public boolean isStateful() {
        GradientState s = this.mGradientState;
        return super.isStateful() || ((s.mColorStateList != null && s.mColorStateList.isStateful()) || ((s.mStrokeColorStateList != null && s.mStrokeColorStateList.isStateful()) || (s.mTint != null && s.mTint.isStateful())));
    }

    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mGradientState.mChangingConfigurations;
    }

    public void setAlpha(int alpha) {
        if (alpha != this.mAlpha) {
            this.mAlpha = alpha;
            invalidateSelf();
        }
    }

    public int getAlpha() {
        return this.mAlpha;
    }

    public void setDither(boolean dither) {
        if (dither != this.mGradientState.mDither) {
            this.mGradientState.mDither = dither;
            invalidateSelf();
        }
    }

    public ColorFilter getColorFilter() {
        return this.mColorFilter;
    }

    public void setColorFilter(ColorFilter cf) {
        if (cf != this.mColorFilter) {
            this.mColorFilter = cf;
            invalidateSelf();
        }
    }

    public void setTintList(ColorStateList tint) {
        this.mGradientState.mTint = tint;
        this.mTintFilter = updateTintFilter(this.mTintFilter, tint, this.mGradientState.mTintMode);
        invalidateSelf();
    }

    public void setTintMode(Mode tintMode) {
        this.mGradientState.mTintMode = tintMode;
        this.mTintFilter = updateTintFilter(this.mTintFilter, this.mGradientState.mTint, tintMode);
        invalidateSelf();
    }

    public int getOpacity() {
        return (this.mAlpha == EditorInfo.IME_MASK_ACTION && this.mGradientState.mOpaqueOverBounds && isOpaqueForState()) ? -1 : -3;
    }

    protected void onBoundsChange(Rect r) {
        super.onBoundsChange(r);
        this.mRingPath = null;
        this.mPathIsDirty = true;
        this.mGradientIsDirty = true;
    }

    protected boolean onLevelChange(int level) {
        super.onLevelChange(level);
        this.mGradientIsDirty = true;
        this.mPathIsDirty = true;
        invalidateSelf();
        return true;
    }

    private boolean ensureValidRect() {
        if (this.mGradientIsDirty) {
            this.mGradientIsDirty = false;
            Rect bounds = getBounds();
            float inset = 0.0f;
            if (this.mStrokePaint != null) {
                inset = this.mStrokePaint.getStrokeWidth() * 0.5f;
            }
            GradientState st = this.mGradientState;
            this.mRect.set(((float) bounds.left) + inset, ((float) bounds.top) + inset, ((float) bounds.right) - inset, ((float) bounds.bottom) - inset);
            int[] colors = st.mColors;
            if (colors != null) {
                RectF r = this.mRect;
                float level;
                float x0;
                float y0;
                if (st.mGradient == 0) {
                    float x1;
                    float y1;
                    level = st.mUseLevel ? ((float) getLevel()) / SensorManager.LIGHT_OVERCAST : LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
                    switch (AnonymousClass1.$SwitchMap$android$graphics$drawable$GradientDrawable$Orientation[st.mOrientation.ordinal()]) {
                        case RADIUS_TYPE_FRACTION /*1*/:
                            x0 = r.left;
                            y0 = r.top;
                            x1 = x0;
                            y1 = level * r.bottom;
                            break;
                        case SWEEP_GRADIENT /*2*/:
                            x0 = r.right;
                            y0 = r.top;
                            x1 = level * r.left;
                            y1 = level * r.bottom;
                            break;
                        case RING /*3*/:
                            x0 = r.right;
                            y0 = r.top;
                            x1 = level * r.left;
                            y1 = y0;
                            break;
                        case ViewGroupAction.TAG /*4*/:
                            x0 = r.right;
                            y0 = r.bottom;
                            x1 = level * r.left;
                            y1 = level * r.top;
                            break;
                        case ReflectionActionWithoutParams.TAG /*5*/:
                            x0 = r.left;
                            y0 = r.bottom;
                            x1 = x0;
                            y1 = level * r.top;
                            break;
                        case SetEmptyView.TAG /*6*/:
                            x0 = r.left;
                            y0 = r.bottom;
                            x1 = level * r.right;
                            y1 = level * r.top;
                            break;
                        case SpellChecker.AVERAGE_WORD_LENGTH /*7*/:
                            x0 = r.left;
                            y0 = r.top;
                            x1 = level * r.right;
                            y1 = y0;
                            break;
                        default:
                            x0 = r.left;
                            y0 = r.top;
                            x1 = level * r.right;
                            y1 = level * r.bottom;
                            break;
                    }
                    this.mFillPaint.setShader(new LinearGradient(x0, y0, x1, y1, colors, st.mPositions, TileMode.CLAMP));
                } else if (st.mGradient == RADIUS_TYPE_FRACTION) {
                    x0 = r.left + ((r.right - r.left) * st.mCenterX);
                    y0 = r.top + ((r.bottom - r.top) * st.mCenterY);
                    float radius = st.mGradientRadius;
                    if (st.mGradientRadiusType == RADIUS_TYPE_FRACTION) {
                        radius *= Math.min(st.mWidth >= 0 ? (float) st.mWidth : r.width(), st.mHeight >= 0 ? (float) st.mHeight : r.height());
                    } else if (st.mGradientRadiusType == SWEEP_GRADIENT) {
                        radius *= Math.min(r.width(), r.height());
                    }
                    if (st.mUseLevel) {
                        radius *= ((float) getLevel()) / SensorManager.LIGHT_OVERCAST;
                    }
                    this.mGradientRadius = radius;
                    if (radius <= 0.0f) {
                        radius = SensorManager.LIGHT_NO_MOON;
                    }
                    this.mFillPaint.setShader(new RadialGradient(x0, y0, radius, colors, null, TileMode.CLAMP));
                } else if (st.mGradient == SWEEP_GRADIENT) {
                    x0 = r.left + ((r.right - r.left) * st.mCenterX);
                    y0 = r.top + ((r.bottom - r.top) * st.mCenterY);
                    int[] tempColors = colors;
                    float[] tempPositions = null;
                    if (st.mUseLevel) {
                        tempColors = st.mTempColors;
                        int length = colors.length;
                        if (tempColors == null || tempColors.length != length + RADIUS_TYPE_FRACTION) {
                            tempColors = new int[(length + RADIUS_TYPE_FRACTION)];
                            st.mTempColors = tempColors;
                        }
                        System.arraycopy(colors, RECTANGLE, tempColors, RECTANGLE, length);
                        tempColors[length] = colors[length - 1];
                        tempPositions = st.mTempPositions;
                        float fraction = LayoutParams.BRIGHTNESS_OVERRIDE_FULL / ((float) (length - 1));
                        if (tempPositions == null || tempPositions.length != length + RADIUS_TYPE_FRACTION) {
                            tempPositions = new float[(length + RADIUS_TYPE_FRACTION)];
                            st.mTempPositions = tempPositions;
                        }
                        level = ((float) getLevel()) / SensorManager.LIGHT_OVERCAST;
                        for (int i = RECTANGLE; i < length; i += RADIUS_TYPE_FRACTION) {
                            tempPositions[i] = (((float) i) * fraction) * level;
                        }
                        tempPositions[length] = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
                    }
                    this.mFillPaint.setShader(new SweepGradient(x0, y0, tempColors, tempPositions));
                }
                if (st.mColorStateList == null) {
                    this.mFillPaint.setColor(Color.BLACK);
                }
            }
        }
        if (this.mRect.isEmpty()) {
            return false;
        }
        return true;
    }

    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Theme theme) throws XmlPullParserException, IOException {
        TypedArray a = Drawable.obtainAttributes(r, theme, attrs, R.styleable.GradientDrawable);
        super.inflateWithAttributes(r, parser, a, SWEEP_GRADIENT);
        updateStateFromTypedArray(a);
        a.recycle();
        inflateChildElements(r, parser, attrs, theme);
        this.mGradientState.computeOpacity();
    }

    public void applyTheme(Theme t) {
        super.applyTheme(t);
        GradientState state = this.mGradientState;
        if (state != null) {
            if (state.mThemeAttrs != null) {
                TypedArray a = t.resolveAttributes(state.mThemeAttrs, R.styleable.GradientDrawable);
                updateStateFromTypedArray(a);
                a.recycle();
            }
            applyThemeChildElements(t);
            state.computeOpacity();
        }
    }

    private void updateStateFromTypedArray(TypedArray a) {
        GradientState state = this.mGradientState;
        state.mChangingConfigurations |= a.getChangingConfigurations();
        state.mThemeAttrs = a.extractThemeAttrs();
        state.mShape = a.getInt(RING, state.mShape);
        state.mDither = a.getBoolean(RECTANGLE, state.mDither);
        if (state.mShape == RING) {
            state.mInnerRadius = a.getDimensionPixelSize(7, state.mInnerRadius);
            if (state.mInnerRadius == -1) {
                state.mInnerRadiusRatio = a.getFloat(4, state.mInnerRadiusRatio);
            }
            state.mThickness = a.getDimensionPixelSize(8, state.mThickness);
            if (state.mThickness == -1) {
                state.mThicknessRatio = a.getFloat(5, state.mThicknessRatio);
            }
            state.mUseLevelForShape = a.getBoolean(6, state.mUseLevelForShape);
        }
        int tintMode = a.getInt(9, -1);
        if (tintMode != -1) {
            state.mTintMode = Drawable.parseTintMode(tintMode, Mode.SRC_IN);
        }
        ColorStateList tint = a.getColorStateList(RADIUS_TYPE_FRACTION);
        if (tint != null) {
            state.mTint = tint;
        }
        this.mTintFilter = updateTintFilter(this.mTintFilter, state.mTint, state.mTintMode);
    }

    public boolean canApplyTheme() {
        return (this.mGradientState != null && this.mGradientState.canApplyTheme()) || super.canApplyTheme();
    }

    private void applyThemeChildElements(Theme t) {
        GradientState st = this.mGradientState;
        if (st.mAttrSize != null) {
            TypedArray a = t.resolveAttributes(st.mAttrSize, R.styleable.GradientDrawableSize);
            updateGradientDrawableSize(a);
            a.recycle();
        }
        if (st.mAttrGradient != null) {
            a = t.resolveAttributes(st.mAttrGradient, R.styleable.GradientDrawableGradient);
            try {
                updateGradientDrawableGradient(t.getResources(), a);
                a.recycle();
            } catch (XmlPullParserException e) {
                throw new RuntimeException(e);
            } catch (Throwable th) {
                a.recycle();
            }
        }
        if (st.mAttrSolid != null) {
            a = t.resolveAttributes(st.mAttrSolid, R.styleable.GradientDrawableSolid);
            updateGradientDrawableSolid(a);
            a.recycle();
        }
        if (st.mAttrStroke != null) {
            a = t.resolveAttributes(st.mAttrStroke, R.styleable.GradientDrawableStroke);
            updateGradientDrawableStroke(a);
            a.recycle();
        }
        if (st.mAttrCorners != null) {
            a = t.resolveAttributes(st.mAttrCorners, R.styleable.DrawableCorners);
            updateDrawableCorners(a);
            a.recycle();
        }
        if (st.mAttrPadding != null) {
            a = t.resolveAttributes(st.mAttrPadding, R.styleable.GradientDrawablePadding);
            updateGradientDrawablePadding(a);
            a.recycle();
        }
    }

    private void inflateChildElements(Resources r, XmlPullParser parser, AttributeSet attrs, Theme theme) throws XmlPullParserException, IOException {
        int innerDepth = parser.getDepth() + RADIUS_TYPE_FRACTION;
        while (true) {
            int type = parser.next();
            if (type != RADIUS_TYPE_FRACTION) {
                int depth = parser.getDepth();
                if (depth < innerDepth && type == RING) {
                    return;
                }
                if (type == SWEEP_GRADIENT && depth <= innerDepth) {
                    String name = parser.getName();
                    TypedArray a;
                    if (name.equals("size")) {
                        a = Drawable.obtainAttributes(r, theme, attrs, R.styleable.GradientDrawableSize);
                        updateGradientDrawableSize(a);
                        a.recycle();
                    } else if (name.equals("gradient")) {
                        a = Drawable.obtainAttributes(r, theme, attrs, R.styleable.GradientDrawableGradient);
                        updateGradientDrawableGradient(r, a);
                        a.recycle();
                    } else if (name.equals("solid")) {
                        a = Drawable.obtainAttributes(r, theme, attrs, R.styleable.GradientDrawableSolid);
                        updateGradientDrawableSolid(a);
                        a.recycle();
                    } else if (name.equals("stroke")) {
                        a = Drawable.obtainAttributes(r, theme, attrs, R.styleable.GradientDrawableStroke);
                        updateGradientDrawableStroke(a);
                        a.recycle();
                    } else if (name.equals("corners")) {
                        a = Drawable.obtainAttributes(r, theme, attrs, R.styleable.DrawableCorners);
                        updateDrawableCorners(a);
                        a.recycle();
                    } else if (name.equals("padding")) {
                        a = Drawable.obtainAttributes(r, theme, attrs, R.styleable.GradientDrawablePadding);
                        updateGradientDrawablePadding(a);
                        a.recycle();
                    } else {
                        Log.w("drawable", "Bad element under <shape>: " + name);
                    }
                }
            } else {
                return;
            }
        }
    }

    private void updateGradientDrawablePadding(TypedArray a) {
        GradientState st = this.mGradientState;
        st.mChangingConfigurations |= a.getChangingConfigurations();
        st.mAttrPadding = a.extractThemeAttrs();
        if (st.mPadding == null) {
            st.mPadding = new Rect();
        }
        Rect pad = st.mPadding;
        pad.set(a.getDimensionPixelOffset(RECTANGLE, pad.left), a.getDimensionPixelOffset(RADIUS_TYPE_FRACTION, pad.top), a.getDimensionPixelOffset(SWEEP_GRADIENT, pad.right), a.getDimensionPixelOffset(RING, pad.bottom));
        this.mPadding = pad;
    }

    private void updateDrawableCorners(TypedArray a) {
        GradientState st = this.mGradientState;
        st.mChangingConfigurations |= a.getChangingConfigurations();
        st.mAttrCorners = a.extractThemeAttrs();
        int radius = a.getDimensionPixelSize(RECTANGLE, (int) st.mRadius);
        setCornerRadius((float) radius);
        int topLeftRadius = a.getDimensionPixelSize(RADIUS_TYPE_FRACTION, radius);
        int topRightRadius = a.getDimensionPixelSize(SWEEP_GRADIENT, radius);
        int bottomLeftRadius = a.getDimensionPixelSize(RING, radius);
        int bottomRightRadius = a.getDimensionPixelSize(4, radius);
        if (topLeftRadius != radius || topRightRadius != radius || bottomLeftRadius != radius || bottomRightRadius != radius) {
            setCornerRadii(new float[]{(float) topLeftRadius, (float) topLeftRadius, (float) topRightRadius, (float) topRightRadius, (float) bottomRightRadius, (float) bottomRightRadius, (float) bottomLeftRadius, (float) bottomLeftRadius});
        }
    }

    private void updateGradientDrawableStroke(TypedArray a) {
        GradientState st = this.mGradientState;
        st.mChangingConfigurations |= a.getChangingConfigurations();
        st.mAttrStroke = a.extractThemeAttrs();
        int width = a.getDimensionPixelSize(RECTANGLE, Math.max(RECTANGLE, st.mStrokeWidth));
        float dashWidth = a.getDimension(SWEEP_GRADIENT, st.mStrokeDashWidth);
        ColorStateList colorStateList = a.getColorStateList(RADIUS_TYPE_FRACTION);
        if (colorStateList == null) {
            colorStateList = st.mStrokeColorStateList;
        }
        if (dashWidth != 0.0f) {
            setStroke(width, colorStateList, dashWidth, a.getDimension(RING, st.mStrokeDashGap));
        } else {
            setStroke(width, colorStateList);
        }
    }

    private void updateGradientDrawableSolid(TypedArray a) {
        GradientState st = this.mGradientState;
        st.mChangingConfigurations |= a.getChangingConfigurations();
        st.mAttrSolid = a.extractThemeAttrs();
        ColorStateList colorStateList = a.getColorStateList(RECTANGLE);
        if (colorStateList != null) {
            setColor(colorStateList);
        }
    }

    private void updateGradientDrawableGradient(Resources r, TypedArray a) throws XmlPullParserException {
        GradientState st = this.mGradientState;
        st.mChangingConfigurations |= a.getChangingConfigurations();
        st.mAttrGradient = a.extractThemeAttrs();
        st.mCenterX = getFloatOrFraction(a, 5, st.mCenterX);
        st.mCenterY = getFloatOrFraction(a, 6, st.mCenterY);
        st.mUseLevel = a.getBoolean(SWEEP_GRADIENT, st.mUseLevel);
        st.mGradient = a.getInt(4, st.mGradient);
        int startColor = a.getColor(RECTANGLE, RECTANGLE);
        boolean hasCenterColor = a.hasValue(8);
        int centerColor = a.getColor(8, RECTANGLE);
        int endColor = a.getColor(RADIUS_TYPE_FRACTION, RECTANGLE);
        if (hasCenterColor) {
            st.mColors = new int[RING];
            st.mColors[RECTANGLE] = startColor;
            st.mColors[RADIUS_TYPE_FRACTION] = centerColor;
            st.mColors[SWEEP_GRADIENT] = endColor;
            st.mPositions = new float[RING];
            st.mPositions[RECTANGLE] = 0.0f;
            st.mPositions[RADIUS_TYPE_FRACTION] = st.mCenterX != 0.5f ? st.mCenterX : st.mCenterY;
            st.mPositions[SWEEP_GRADIENT] = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        } else {
            st.mColors = new int[SWEEP_GRADIENT];
            st.mColors[RECTANGLE] = startColor;
            st.mColors[RADIUS_TYPE_FRACTION] = endColor;
        }
        if (st.mGradient == 0) {
            int angle = ((int) a.getFloat(RING, (float) st.mAngle)) % 360;
            if (angle % 45 != 0) {
                throw new XmlPullParserException(a.getPositionDescription() + "<gradient> tag requires 'angle' attribute to " + "be a multiple of 45");
            }
            st.mAngle = angle;
            switch (angle) {
                case RECTANGLE /*0*/:
                    st.mOrientation = Orientation.LEFT_RIGHT;
                    return;
                case MotionEvent.AXIS_GENERIC_14 /*45*/:
                    st.mOrientation = Orientation.BL_TR;
                    return;
                case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD /*90*/:
                    st.mOrientation = Orientation.BOTTOM_TOP;
                    return;
                case KeyEvent.KEYCODE_F5 /*135*/:
                    st.mOrientation = Orientation.BR_TL;
                    return;
                case KeyEvent.KEYCODE_STB_INPUT /*180*/:
                    st.mOrientation = Orientation.RIGHT_LEFT;
                    return;
                case KeyEvent.KEYCODE_PAIRING /*225*/:
                    st.mOrientation = Orientation.TR_BL;
                    return;
                case 270:
                    st.mOrientation = Orientation.TOP_BOTTOM;
                    return;
                case 315:
                    st.mOrientation = Orientation.TL_BR;
                    return;
                default:
                    return;
            }
        }
        TypedValue tv = a.peekValue(7);
        if (tv != null) {
            float radius;
            int radiusType;
            if (tv.type == 6) {
                radius = tv.getFraction(LayoutParams.BRIGHTNESS_OVERRIDE_FULL, LayoutParams.BRIGHTNESS_OVERRIDE_FULL);
                if (((tv.data >> RECTANGLE) & 15) == RADIUS_TYPE_FRACTION) {
                    radiusType = SWEEP_GRADIENT;
                } else {
                    radiusType = RADIUS_TYPE_FRACTION;
                }
            } else if (tv.type == 5) {
                radius = tv.getDimension(r.getDisplayMetrics());
                radiusType = RECTANGLE;
            } else {
                radius = tv.getFloat();
                radiusType = RECTANGLE;
            }
            st.mGradientRadius = radius;
            st.mGradientRadiusType = radiusType;
        } else if (st.mGradient == RADIUS_TYPE_FRACTION) {
            throw new XmlPullParserException(a.getPositionDescription() + "<gradient> tag requires 'gradientRadius' " + "attribute with radial type");
        }
    }

    private void updateGradientDrawableSize(TypedArray a) {
        GradientState st = this.mGradientState;
        st.mChangingConfigurations |= a.getChangingConfigurations();
        st.mAttrSize = a.extractThemeAttrs();
        st.mWidth = a.getDimensionPixelSize(RADIUS_TYPE_FRACTION, st.mWidth);
        st.mHeight = a.getDimensionPixelSize(RECTANGLE, st.mHeight);
    }

    private static float getFloatOrFraction(TypedArray a, int index, float defaultValue) {
        TypedValue tv = a.peekValue(index);
        float v = defaultValue;
        if (tv == null) {
            return v;
        }
        return tv.type == 6 ? tv.getFraction(LayoutParams.BRIGHTNESS_OVERRIDE_FULL, LayoutParams.BRIGHTNESS_OVERRIDE_FULL) : tv.getFloat();
    }

    public int getIntrinsicWidth() {
        return this.mGradientState.mWidth;
    }

    public int getIntrinsicHeight() {
        return this.mGradientState.mHeight;
    }

    public ConstantState getConstantState() {
        this.mGradientState.mChangingConfigurations = getChangingConfigurations();
        return this.mGradientState;
    }

    private boolean isOpaqueForState() {
        if ((this.mGradientState.mStrokeWidth < 0 || this.mStrokePaint == null || isOpaque(this.mStrokePaint.getColor())) && isOpaque(this.mFillPaint.getColor())) {
            return true;
        }
        return false;
    }

    public void getOutline(Outline outline) {
        GradientState st = this.mGradientState;
        Rect bounds = getBounds();
        float f = (st.mOpaqueOverShape && isOpaqueForState()) ? ((float) this.mAlpha) / 255.0f : 0.0f;
        outline.setAlpha(f);
        switch (st.mShape) {
            case RECTANGLE /*0*/:
                if (st.mRadiusArray != null) {
                    buildPathIfDirty();
                    outline.setConvexPath(this.mPath);
                    return;
                }
                float rad = 0.0f;
                if (st.mRadius > 0.0f) {
                    rad = Math.min(st.mRadius, ((float) Math.min(bounds.width(), bounds.height())) * 0.5f);
                }
                outline.setRoundRect(bounds, rad);
            case RADIUS_TYPE_FRACTION /*1*/:
                outline.setOval(bounds);
            case SWEEP_GRADIENT /*2*/:
                float halfStrokeWidth;
                if (this.mStrokePaint == null) {
                    halfStrokeWidth = 1.0E-4f;
                } else {
                    halfStrokeWidth = this.mStrokePaint.getStrokeWidth() * 0.5f;
                }
                float centerY = (float) bounds.centerY();
                outline.setRect(bounds.left, (int) Math.floor((double) (centerY - halfStrokeWidth)), bounds.right, (int) Math.ceil((double) (centerY + halfStrokeWidth)));
            default:
        }
    }

    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mGradientState = new GradientState(this.mGradientState);
            initializeWithState(this.mGradientState);
            this.mMutated = true;
        }
        return this;
    }

    public void clearMutated() {
        super.clearMutated();
        this.mMutated = false;
    }

    static boolean isOpaque(int color) {
        return ((color >> 24) & EditorInfo.IME_MASK_ACTION) == EditorInfo.IME_MASK_ACTION;
    }

    private GradientDrawable(GradientState state) {
        this.mFillPaint = new Paint((int) RADIUS_TYPE_FRACTION);
        this.mAlpha = EditorInfo.IME_MASK_ACTION;
        this.mPath = new Path();
        this.mRect = new RectF();
        this.mPathIsDirty = true;
        this.mGradientState = state;
        initializeWithState(this.mGradientState);
        this.mGradientIsDirty = true;
        this.mMutated = false;
    }

    private void initializeWithState(GradientState state) {
        if (state.mColorStateList != null) {
            this.mFillPaint.setColor(state.mColorStateList.getColorForState(getState(), RECTANGLE));
        } else if (state.mColors == null) {
            this.mFillPaint.setColor(RECTANGLE);
        } else {
            this.mFillPaint.setColor(Color.BLACK);
        }
        this.mPadding = state.mPadding;
        if (state.mStrokeWidth >= 0) {
            this.mStrokePaint = new Paint((int) RADIUS_TYPE_FRACTION);
            this.mStrokePaint.setStyle(Style.STROKE);
            this.mStrokePaint.setStrokeWidth((float) state.mStrokeWidth);
            if (state.mStrokeColorStateList != null) {
                this.mStrokePaint.setColor(state.mStrokeColorStateList.getColorForState(getState(), RECTANGLE));
            }
            if (state.mStrokeDashWidth != 0.0f) {
                float[] fArr = new float[SWEEP_GRADIENT];
                fArr[RECTANGLE] = state.mStrokeDashWidth;
                fArr[RADIUS_TYPE_FRACTION] = state.mStrokeDashGap;
                this.mStrokePaint.setPathEffect(new DashPathEffect(fArr, 0.0f));
            }
        }
        this.mTintFilter = updateTintFilter(this.mTintFilter, state.mTint, state.mTintMode);
    }
}
