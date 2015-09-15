package android.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable.ConstantState;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import com.android.internal.R;
import org.xmlpull.v1.XmlPullParser;

public class ShapeDrawable extends Drawable {
    private boolean mMutated;
    private ShapeState mShapeState;
    private PorterDuffColorFilter mTintFilter;

    public static abstract class ShaderFactory {
        public abstract Shader resize(int i, int i2);
    }

    static final class ShapeState extends ConstantState {
        int mAlpha;
        int mChangingConfigurations;
        int mIntrinsicHeight;
        int mIntrinsicWidth;
        Rect mPadding;
        Paint mPaint;
        ShaderFactory mShaderFactory;
        Shape mShape;
        int[] mThemeAttrs;
        ColorStateList mTint;
        Mode mTintMode;

        ShapeState(ShapeState orig) {
            this.mTint = null;
            this.mTintMode = Drawable.DEFAULT_TINT_MODE;
            this.mAlpha = EditorInfo.IME_MASK_ACTION;
            if (orig != null) {
                this.mThemeAttrs = orig.mThemeAttrs;
                this.mPaint = orig.mPaint;
                this.mShape = orig.mShape;
                this.mTint = orig.mTint;
                this.mTintMode = orig.mTintMode;
                this.mPadding = orig.mPadding;
                this.mIntrinsicWidth = orig.mIntrinsicWidth;
                this.mIntrinsicHeight = orig.mIntrinsicHeight;
                this.mAlpha = orig.mAlpha;
                this.mShaderFactory = orig.mShaderFactory;
                return;
            }
            this.mPaint = new Paint(1);
        }

        public boolean canApplyTheme() {
            return this.mThemeAttrs != null;
        }

        public Drawable newDrawable() {
            return new ShapeDrawable(null, null);
        }

        public Drawable newDrawable(Resources res) {
            return new ShapeDrawable(res, null);
        }

        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }
    }

    public ShapeDrawable() {
        this(new ShapeState(null), null);
    }

    public ShapeDrawable(Shape s) {
        this(new ShapeState(null), null);
        this.mShapeState.mShape = s;
    }

    public Shape getShape() {
        return this.mShapeState.mShape;
    }

    public void setShape(Shape s) {
        this.mShapeState.mShape = s;
        updateShape();
    }

    public void setShaderFactory(ShaderFactory fact) {
        this.mShapeState.mShaderFactory = fact;
    }

    public ShaderFactory getShaderFactory() {
        return this.mShapeState.mShaderFactory;
    }

    public Paint getPaint() {
        return this.mShapeState.mPaint;
    }

    public void setPadding(int left, int top, int right, int bottom) {
        if ((((left | top) | right) | bottom) == 0) {
            this.mShapeState.mPadding = null;
        } else {
            if (this.mShapeState.mPadding == null) {
                this.mShapeState.mPadding = new Rect();
            }
            this.mShapeState.mPadding.set(left, top, right, bottom);
        }
        invalidateSelf();
    }

    public void setPadding(Rect padding) {
        if (padding == null) {
            this.mShapeState.mPadding = null;
        } else {
            if (this.mShapeState.mPadding == null) {
                this.mShapeState.mPadding = new Rect();
            }
            this.mShapeState.mPadding.set(padding);
        }
        invalidateSelf();
    }

    public void setIntrinsicWidth(int width) {
        this.mShapeState.mIntrinsicWidth = width;
        invalidateSelf();
    }

    public void setIntrinsicHeight(int height) {
        this.mShapeState.mIntrinsicHeight = height;
        invalidateSelf();
    }

    public int getIntrinsicWidth() {
        return this.mShapeState.mIntrinsicWidth;
    }

    public int getIntrinsicHeight() {
        return this.mShapeState.mIntrinsicHeight;
    }

    public boolean getPadding(Rect padding) {
        if (this.mShapeState.mPadding == null) {
            return super.getPadding(padding);
        }
        padding.set(this.mShapeState.mPadding);
        return true;
    }

    private static int modulateAlpha(int paintAlpha, int alpha) {
        return (paintAlpha * (alpha + (alpha >>> 7))) >>> 8;
    }

    protected void onDraw(Shape shape, Canvas canvas, Paint paint) {
        shape.draw(canvas, paint);
    }

    public void draw(Canvas canvas) {
        Rect r = getBounds();
        ShapeState state = this.mShapeState;
        Paint paint = state.mPaint;
        int prevAlpha = paint.getAlpha();
        paint.setAlpha(modulateAlpha(prevAlpha, state.mAlpha));
        if (!(paint.getAlpha() == 0 && paint.getXfermode() == null && !paint.hasShadowLayer())) {
            boolean clearColorFilter;
            if (this.mTintFilter == null || paint.getColorFilter() != null) {
                clearColorFilter = false;
            } else {
                paint.setColorFilter(this.mTintFilter);
                clearColorFilter = true;
            }
            if (state.mShape != null) {
                int count = canvas.save();
                canvas.translate((float) r.left, (float) r.top);
                onDraw(state.mShape, canvas, paint);
                canvas.restoreToCount(count);
            } else {
                canvas.drawRect(r, paint);
            }
            if (clearColorFilter) {
                paint.setColorFilter(null);
            }
        }
        paint.setAlpha(prevAlpha);
    }

    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | this.mShapeState.mChangingConfigurations;
    }

    public void setAlpha(int alpha) {
        this.mShapeState.mAlpha = alpha;
        invalidateSelf();
    }

    public int getAlpha() {
        return this.mShapeState.mAlpha;
    }

    public void setTintList(ColorStateList tint) {
        this.mShapeState.mTint = tint;
        this.mTintFilter = updateTintFilter(this.mTintFilter, tint, this.mShapeState.mTintMode);
        invalidateSelf();
    }

    public void setTintMode(Mode tintMode) {
        this.mShapeState.mTintMode = tintMode;
        this.mTintFilter = updateTintFilter(this.mTintFilter, this.mShapeState.mTint, tintMode);
        invalidateSelf();
    }

    public void setColorFilter(ColorFilter cf) {
        this.mShapeState.mPaint.setColorFilter(cf);
        invalidateSelf();
    }

    public int getOpacity() {
        if (this.mShapeState.mShape == null) {
            Paint p = this.mShapeState.mPaint;
            if (p.getXfermode() == null) {
                int alpha = p.getAlpha();
                if (alpha == 0) {
                    return -2;
                }
                if (alpha == EditorInfo.IME_MASK_ACTION) {
                    return -1;
                }
            }
        }
        return -3;
    }

    public void setDither(boolean dither) {
        this.mShapeState.mPaint.setDither(dither);
        invalidateSelf();
    }

    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        updateShape();
    }

    protected boolean onStateChange(int[] stateSet) {
        ShapeState state = this.mShapeState;
        if (state.mTint == null || state.mTintMode == null) {
            return false;
        }
        this.mTintFilter = updateTintFilter(this.mTintFilter, state.mTint, state.mTintMode);
        return true;
    }

    public boolean isStateful() {
        ShapeState s = this.mShapeState;
        return super.isStateful() || (s.mTint != null && s.mTint.isStateful());
    }

    protected boolean inflateTag(String name, Resources r, XmlPullParser parser, AttributeSet attrs) {
        if (!"padding".equals(name)) {
            return false;
        }
        TypedArray a = r.obtainAttributes(attrs, R.styleable.ShapeDrawablePadding);
        setPadding(a.getDimensionPixelOffset(0, 0), a.getDimensionPixelOffset(1, 0), a.getDimensionPixelOffset(2, 0), a.getDimensionPixelOffset(3, 0));
        a.recycle();
        return true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void inflate(android.content.res.Resources r8, org.xmlpull.v1.XmlPullParser r9, android.util.AttributeSet r10, android.content.res.Resources.Theme r11) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        r7 = this;
        super.inflate(r8, r9, r10, r11);
        r4 = com.android.internal.R.styleable.ShapeDrawable;
        r0 = android.graphics.drawable.Drawable.obtainAttributes(r8, r11, r10, r4);
        r7.updateStateFromTypedArray(r0);
        r0.recycle();
        r2 = r9.getDepth();
    L_0x0013:
        r3 = r9.next();
        r4 = 1;
        if (r3 == r4) goto L_0x0053;
    L_0x001a:
        r4 = 3;
        if (r3 != r4) goto L_0x0023;
    L_0x001d:
        r4 = r9.getDepth();
        if (r4 <= r2) goto L_0x0053;
    L_0x0023:
        r4 = 2;
        if (r3 != r4) goto L_0x0013;
    L_0x0026:
        r1 = r9.getName();
        r4 = r7.inflateTag(r1, r8, r9, r10);
        if (r4 != 0) goto L_0x0013;
    L_0x0030:
        r4 = "drawable";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "Unknown element: ";
        r5 = r5.append(r6);
        r5 = r5.append(r1);
        r6 = " for ShapeDrawable ";
        r5 = r5.append(r6);
        r5 = r5.append(r7);
        r5 = r5.toString();
        android.util.Log.w(r4, r5);
        goto L_0x0013;
    L_0x0053:
        r4 = r7.mShapeState;
        r7.initializeWithState(r4, r8);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.graphics.drawable.ShapeDrawable.inflate(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.content.res.Resources$Theme):void");
    }

    public void applyTheme(Theme t) {
        super.applyTheme(t);
        ShapeState state = this.mShapeState;
        if (state != null && state.mThemeAttrs != null) {
            TypedArray a = t.resolveAttributes(state.mThemeAttrs, R.styleable.ShapeDrawable);
            updateStateFromTypedArray(a);
            a.recycle();
            initializeWithState(state, t.getResources());
        }
    }

    private void updateStateFromTypedArray(TypedArray a) {
        ShapeState state = this.mShapeState;
        Paint paint = state.mPaint;
        state.mChangingConfigurations |= a.getChangingConfigurations();
        state.mThemeAttrs = a.extractThemeAttrs();
        paint.setColor(a.getColor(4, paint.getColor()));
        paint.setDither(a.getBoolean(0, paint.isDither()));
        setIntrinsicWidth((int) a.getDimension(3, (float) state.mIntrinsicWidth));
        setIntrinsicHeight((int) a.getDimension(2, (float) state.mIntrinsicHeight));
        int tintMode = a.getInt(5, -1);
        if (tintMode != -1) {
            state.mTintMode = Drawable.parseTintMode(tintMode, Mode.SRC_IN);
        }
        ColorStateList tint = a.getColorStateList(1);
        if (tint != null) {
            state.mTint = tint;
        }
    }

    private void updateShape() {
        if (this.mShapeState.mShape != null) {
            Rect r = getBounds();
            int w = r.width();
            int h = r.height();
            this.mShapeState.mShape.resize((float) w, (float) h);
            if (this.mShapeState.mShaderFactory != null) {
                this.mShapeState.mPaint.setShader(this.mShapeState.mShaderFactory.resize(w, h));
            }
        }
        invalidateSelf();
    }

    public void getOutline(Outline outline) {
        if (this.mShapeState.mShape != null) {
            this.mShapeState.mShape.getOutline(outline);
            outline.setAlpha(((float) getAlpha()) / 255.0f);
        }
    }

    public ConstantState getConstantState() {
        this.mShapeState.mChangingConfigurations = getChangingConfigurations();
        return this.mShapeState;
    }

    public Drawable mutate() {
        if (this.mMutated || super.mutate() != this) {
            return this;
        }
        if (this.mShapeState.mPaint != null) {
            this.mShapeState.mPaint = new Paint(this.mShapeState.mPaint);
        } else {
            this.mShapeState.mPaint = new Paint(1);
        }
        if (this.mShapeState.mPadding != null) {
            this.mShapeState.mPadding = new Rect(this.mShapeState.mPadding);
        } else {
            this.mShapeState.mPadding = new Rect();
        }
        try {
            this.mShapeState.mShape = this.mShapeState.mShape.clone();
            this.mMutated = true;
            return this;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public void clearMutated() {
        super.clearMutated();
        this.mMutated = false;
    }

    private ShapeDrawable(ShapeState state, Resources res) {
        this.mShapeState = state;
        initializeWithState(state, res);
    }

    private void initializeWithState(ShapeState state, Resources res) {
        this.mTintFilter = updateTintFilter(this.mTintFilter, state.mTint, state.mTintMode);
    }
}
