package android.graphics.drawable;

import android.R;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable.ConstantState;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import java.io.IOException;
import java.util.Arrays;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class RippleDrawable extends LayerDrawable {
    private static final int MASK_CONTENT = 1;
    private static final int MASK_EXPLICIT = 2;
    private static final int MASK_NONE = 0;
    private static final int MASK_UNKNOWN = -1;
    private static final int MAX_RIPPLES = 10;
    public static final int RADIUS_AUTO = -1;
    private RippleBackground mBackground;
    private boolean mBackgroundActive;
    private float mDensity;
    private final Rect mDirtyBounds;
    private final Rect mDrawingBounds;
    private Ripple[] mExitingRipples;
    private int mExitingRipplesCount;
    private boolean mHasPending;
    private boolean mHasValidMask;
    private final Rect mHotspotBounds;
    private Drawable mMask;
    private Bitmap mMaskBuffer;
    private Canvas mMaskCanvas;
    private PorterDuffColorFilter mMaskColorFilter;
    private Matrix mMaskMatrix;
    private BitmapShader mMaskShader;
    private boolean mOverrideBounds;
    private float mPendingX;
    private float mPendingY;
    private Ripple mRipple;
    private boolean mRippleActive;
    private Paint mRipplePaint;
    private RippleState mState;
    private final Rect mTempRect;

    static class RippleState extends LayerState {
        ColorStateList mColor;
        int mMaxRadius;
        int[] mTouchThemeAttrs;

        public RippleState(LayerState orig, RippleDrawable owner, Resources res) {
            super(orig, owner, res);
            this.mColor = ColorStateList.valueOf(Color.MAGENTA);
            this.mMaxRadius = RippleDrawable.RADIUS_AUTO;
            if (orig != null && (orig instanceof RippleState)) {
                RippleState origs = (RippleState) orig;
                this.mTouchThemeAttrs = origs.mTouchThemeAttrs;
                this.mColor = origs.mColor;
                this.mMaxRadius = origs.mMaxRadius;
            }
        }

        public boolean canApplyTheme() {
            return this.mTouchThemeAttrs != null || super.canApplyTheme();
        }

        public Drawable newDrawable() {
            return new RippleDrawable(null, null);
        }

        public Drawable newDrawable(Resources res) {
            return new RippleDrawable(res, null);
        }
    }

    RippleDrawable() {
        this(new RippleState(null, null, null), null);
    }

    public RippleDrawable(ColorStateList color, Drawable content, Drawable mask) {
        this(new RippleState(null, null, null), null);
        if (color == null) {
            throw new IllegalArgumentException("RippleDrawable requires a non-null color");
        }
        if (content != null) {
            addLayer(content, null, MASK_NONE, MASK_NONE, MASK_NONE, MASK_NONE, MASK_NONE);
        }
        if (mask != null) {
            addLayer(mask, null, R.id.mask, MASK_NONE, MASK_NONE, MASK_NONE, MASK_NONE);
        }
        setColor(color);
        ensurePadding();
        initializeFromState();
    }

    public void jumpToCurrentState() {
        super.jumpToCurrentState();
        if (this.mRipple != null) {
            this.mRipple.jump();
        }
        if (this.mBackground != null) {
            this.mBackground.jump();
        }
        cancelExitingRipples();
        invalidateSelf();
    }

    private boolean cancelExitingRipples() {
        boolean needsDraw = false;
        int count = this.mExitingRipplesCount;
        Ripple[] ripples = this.mExitingRipples;
        for (int i = MASK_NONE; i < count; i += MASK_CONTENT) {
            needsDraw |= ripples[i].isHardwareAnimating();
            ripples[i].cancel();
        }
        if (ripples != null) {
            Arrays.fill(ripples, MASK_NONE, count, null);
        }
        this.mExitingRipplesCount = MASK_NONE;
        return needsDraw;
    }

    public void setAlpha(int alpha) {
        super.setAlpha(alpha);
    }

    public void setColorFilter(ColorFilter cf) {
        super.setColorFilter(cf);
    }

    public int getOpacity() {
        return -3;
    }

    protected boolean onStateChange(int[] stateSet) {
        boolean z;
        boolean z2 = false;
        boolean changed = super.onStateChange(stateSet);
        boolean enabled = false;
        boolean pressed = false;
        boolean focused = false;
        int[] arr$ = stateSet;
        int len$ = arr$.length;
        for (int i$ = MASK_NONE; i$ < len$; i$ += MASK_CONTENT) {
            int state = arr$[i$];
            if (state == R.attr.state_enabled) {
                enabled = true;
            }
            if (state == R.attr.state_focused) {
                focused = true;
            }
            if (state == R.attr.state_pressed) {
                pressed = true;
            }
        }
        if (enabled && pressed) {
            z = true;
        } else {
            z = false;
        }
        setRippleActive(z);
        if (focused || (enabled && pressed)) {
            z2 = true;
        }
        setBackgroundActive(z2, focused);
        return changed;
    }

    private void setRippleActive(boolean active) {
        if (this.mRippleActive != active) {
            this.mRippleActive = active;
            if (active) {
                tryRippleEnter();
            } else {
                tryRippleExit();
            }
        }
    }

    private void setBackgroundActive(boolean active, boolean focused) {
        if (this.mBackgroundActive != active) {
            this.mBackgroundActive = active;
            if (active) {
                tryBackgroundEnter(focused);
            } else {
                tryBackgroundExit();
            }
        }
    }

    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        if (!this.mOverrideBounds) {
            this.mHotspotBounds.set(bounds);
            onHotspotBoundsChanged();
        }
        invalidateSelf();
    }

    public boolean setVisible(boolean visible, boolean restart) {
        boolean changed = super.setVisible(visible, restart);
        if (!visible) {
            clearHotspots();
        } else if (changed) {
            if (this.mRippleActive) {
                tryRippleEnter();
            }
            if (this.mBackgroundActive) {
                tryBackgroundEnter(false);
            }
            jumpToCurrentState();
        }
        return changed;
    }

    public boolean isProjected() {
        return getNumberOfLayers() == 0;
    }

    public boolean isStateful() {
        return true;
    }

    public void setColor(ColorStateList color) {
        this.mState.mColor = color;
        invalidateSelf();
    }

    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Theme theme) throws XmlPullParserException, IOException {
        TypedArray a = Drawable.obtainAttributes(r, theme, attrs, com.android.internal.R.styleable.RippleDrawable);
        updateStateFromTypedArray(a);
        a.recycle();
        setPaddingMode(MASK_CONTENT);
        super.inflate(r, parser, attrs, theme);
        setTargetDensity(r.getDisplayMetrics());
        initializeFromState();
    }

    public boolean setDrawableByLayerId(int id, Drawable drawable) {
        if (!super.setDrawableByLayerId(id, drawable)) {
            return false;
        }
        if (id == R.id.mask) {
            this.mMask = drawable;
        }
        return true;
    }

    public void setPaddingMode(int mode) {
        super.setPaddingMode(mode);
    }

    private void updateStateFromTypedArray(TypedArray a) throws XmlPullParserException {
        RippleState state = this.mState;
        state.mChangingConfigurations |= a.getChangingConfigurations();
        state.mTouchThemeAttrs = a.extractThemeAttrs();
        ColorStateList color = a.getColorStateList(MASK_NONE);
        if (color != null) {
            this.mState.mColor = color;
        }
        verifyRequiredAttributes(a);
    }

    private void verifyRequiredAttributes(TypedArray a) throws XmlPullParserException {
        if (this.mState.mColor != null) {
            return;
        }
        if (this.mState.mTouchThemeAttrs == null || this.mState.mTouchThemeAttrs[MASK_NONE] == 0) {
            throw new XmlPullParserException(a.getPositionDescription() + ": <ripple> requires a valid color attribute");
        }
    }

    private void setTargetDensity(DisplayMetrics metrics) {
        if (this.mDensity != metrics.density) {
            this.mDensity = metrics.density;
            invalidateSelf();
        }
    }

    public void applyTheme(Theme t) {
        super.applyTheme(t);
        RippleState state = this.mState;
        if (state != null && state.mTouchThemeAttrs != null) {
            TypedArray a = t.resolveAttributes(state.mTouchThemeAttrs, com.android.internal.R.styleable.RippleDrawable);
            try {
                updateStateFromTypedArray(a);
                a.recycle();
                initializeFromState();
            } catch (XmlPullParserException e) {
                throw new RuntimeException(e);
            } catch (Throwable th) {
                a.recycle();
            }
        }
    }

    public boolean canApplyTheme() {
        return (this.mState != null && this.mState.canApplyTheme()) || super.canApplyTheme();
    }

    public void setHotspot(float x, float y) {
        if (this.mRipple == null || this.mBackground == null) {
            this.mPendingX = x;
            this.mPendingY = y;
            this.mHasPending = true;
        }
        if (this.mRipple != null) {
            this.mRipple.move(x, y);
        }
    }

    private void tryBackgroundEnter(boolean focused) {
        if (this.mBackground == null) {
            this.mBackground = new RippleBackground(this, this.mHotspotBounds);
        }
        this.mBackground.setup(this.mState.mMaxRadius, this.mDensity);
        this.mBackground.enter(focused);
    }

    private void tryBackgroundExit() {
        if (this.mBackground != null) {
            this.mBackground.exit();
        }
    }

    private void tryRippleEnter() {
        if (this.mExitingRipplesCount < MAX_RIPPLES) {
            if (this.mRipple == null) {
                float x;
                float y;
                if (this.mHasPending) {
                    this.mHasPending = false;
                    x = this.mPendingX;
                    y = this.mPendingY;
                } else {
                    x = this.mHotspotBounds.exactCenterX();
                    y = this.mHotspotBounds.exactCenterY();
                }
                this.mRipple = new Ripple(this, this.mHotspotBounds, x, y);
            }
            this.mRipple.setup(this.mState.mMaxRadius, this.mDensity);
            this.mRipple.enter();
        }
    }

    private void tryRippleExit() {
        if (this.mRipple != null) {
            if (this.mExitingRipples == null) {
                this.mExitingRipples = new Ripple[MAX_RIPPLES];
            }
            Ripple[] rippleArr = this.mExitingRipples;
            int i = this.mExitingRipplesCount;
            this.mExitingRipplesCount = i + MASK_CONTENT;
            rippleArr[i] = this.mRipple;
            this.mRipple.exit();
            this.mRipple = null;
        }
    }

    private void clearHotspots() {
        if (this.mRipple != null) {
            this.mRipple.cancel();
            this.mRipple = null;
            this.mRippleActive = false;
        }
        if (this.mBackground != null) {
            this.mBackground.cancel();
            this.mBackground = null;
            this.mBackgroundActive = false;
        }
        cancelExitingRipples();
        invalidateSelf();
    }

    public void setHotspotBounds(int left, int top, int right, int bottom) {
        this.mOverrideBounds = true;
        this.mHotspotBounds.set(left, top, right, bottom);
        onHotspotBoundsChanged();
    }

    public void getHotspotBounds(Rect outRect) {
        outRect.set(this.mHotspotBounds);
    }

    private void onHotspotBoundsChanged() {
        int count = this.mExitingRipplesCount;
        Ripple[] ripples = this.mExitingRipples;
        for (int i = MASK_NONE; i < count; i += MASK_CONTENT) {
            ripples[i].onHotspotBoundsChanged();
        }
        if (this.mRipple != null) {
            this.mRipple.onHotspotBoundsChanged();
        }
        if (this.mBackground != null) {
            this.mBackground.onHotspotBoundsChanged();
        }
    }

    public void getOutline(Outline outline) {
        LayerState state = this.mLayerState;
        ChildDrawable[] children = state.mChildren;
        int N = state.mNum;
        for (int i = MASK_NONE; i < N; i += MASK_CONTENT) {
            if (children[i].mId != R.id.mask) {
                children[i].mDrawable.getOutline(outline);
                if (!outline.isEmpty()) {
                    return;
                }
            }
        }
    }

    public void draw(Canvas canvas) {
        Rect bounds = getDirtyBounds();
        int saveCount = canvas.save(MASK_EXPLICIT);
        canvas.clipRect(bounds);
        drawContent(canvas);
        drawBackgroundAndRipples(canvas);
        canvas.restoreToCount(saveCount);
    }

    public void invalidateSelf() {
        super.invalidateSelf();
        this.mHasValidMask = false;
    }

    private void updateMaskShaderIfNeeded() {
        if (!this.mHasValidMask) {
            int maskType = getMaskType();
            if (maskType != RADIUS_AUTO) {
                this.mHasValidMask = true;
                Rect bounds = getBounds();
                if (maskType == 0 || bounds.isEmpty()) {
                    if (this.mMaskBuffer != null) {
                        this.mMaskBuffer.recycle();
                        this.mMaskBuffer = null;
                        this.mMaskShader = null;
                        this.mMaskCanvas = null;
                    }
                    this.mMaskMatrix = null;
                    this.mMaskColorFilter = null;
                    return;
                }
                if (this.mMaskBuffer != null && this.mMaskBuffer.getWidth() == bounds.width() && this.mMaskBuffer.getHeight() == bounds.height()) {
                    this.mMaskBuffer.eraseColor(MASK_NONE);
                } else {
                    if (this.mMaskBuffer != null) {
                        this.mMaskBuffer.recycle();
                    }
                    this.mMaskBuffer = Bitmap.createBitmap(bounds.width(), bounds.height(), Config.ALPHA_8);
                    this.mMaskShader = new BitmapShader(this.mMaskBuffer, TileMode.CLAMP, TileMode.CLAMP);
                    this.mMaskCanvas = new Canvas(this.mMaskBuffer);
                }
                if (this.mMaskMatrix == null) {
                    this.mMaskMatrix = new Matrix();
                } else {
                    this.mMaskMatrix.reset();
                }
                if (this.mMaskColorFilter == null) {
                    this.mMaskColorFilter = new PorterDuffColorFilter(MASK_NONE, Mode.SRC_IN);
                }
                if (maskType == MASK_EXPLICIT) {
                    drawMask(this.mMaskCanvas);
                } else if (maskType == MASK_CONTENT) {
                    drawContent(this.mMaskCanvas);
                }
            }
        }
    }

    private int getMaskType() {
        if (this.mRipple == null && this.mExitingRipplesCount <= 0 && (this.mBackground == null || !this.mBackground.shouldDraw())) {
            return RADIUS_AUTO;
        }
        if (this.mMask == null) {
            ChildDrawable[] array = this.mLayerState.mChildren;
            int count = this.mLayerState.mNum;
            for (int i = MASK_NONE; i < count; i += MASK_CONTENT) {
                if (array[i].mDrawable.getOpacity() != RADIUS_AUTO) {
                    return MASK_CONTENT;
                }
            }
            return MASK_NONE;
        } else if (this.mMask.getOpacity() == RADIUS_AUTO) {
            return MASK_NONE;
        } else {
            return MASK_EXPLICIT;
        }
    }

    void removeRipple(Ripple ripple) {
        Ripple[] ripples = this.mExitingRipples;
        int count = this.mExitingRipplesCount;
        int index = getRippleIndex(ripple);
        if (index >= 0) {
            System.arraycopy(ripples, index + MASK_CONTENT, ripples, index, count - (index + MASK_CONTENT));
            ripples[count + RADIUS_AUTO] = null;
            this.mExitingRipplesCount += RADIUS_AUTO;
            invalidateSelf();
        }
    }

    private int getRippleIndex(Ripple ripple) {
        Ripple[] ripples = this.mExitingRipples;
        int count = this.mExitingRipplesCount;
        for (int i = MASK_NONE; i < count; i += MASK_CONTENT) {
            if (ripples[i] == ripple) {
                return i;
            }
        }
        return RADIUS_AUTO;
    }

    private void drawContent(Canvas canvas) {
        ChildDrawable[] array = this.mLayerState.mChildren;
        int count = this.mLayerState.mNum;
        for (int i = MASK_NONE; i < count; i += MASK_CONTENT) {
            if (array[i].mId != R.id.mask) {
                array[i].mDrawable.draw(canvas);
            }
        }
    }

    private void drawBackgroundAndRipples(Canvas canvas) {
        Ripple active = this.mRipple;
        RippleBackground background = this.mBackground;
        int count = this.mExitingRipplesCount;
        if (active != null || count > 0 || (background != null && background.shouldDraw())) {
            float x = this.mHotspotBounds.exactCenterX();
            float y = this.mHotspotBounds.exactCenterY();
            canvas.translate(x, y);
            updateMaskShaderIfNeeded();
            if (this.mMaskShader != null) {
                this.mMaskMatrix.setTranslate(-x, -y);
                this.mMaskShader.setLocalMatrix(this.mMaskMatrix);
            }
            int color = this.mState.mColor.getColorForState(getState(), Color.BLACK);
            int halfAlpha = (Color.alpha(color) / MASK_EXPLICIT) << 24;
            Paint p = getRipplePaint();
            if (this.mMaskColorFilter != null) {
                this.mMaskColorFilter.setColor(color | Color.BLACK);
                p.setColor(halfAlpha);
                p.setColorFilter(this.mMaskColorFilter);
                p.setShader(this.mMaskShader);
            } else {
                p.setColor((View.MEASURED_SIZE_MASK & color) | halfAlpha);
                p.setColorFilter(null);
                p.setShader(null);
            }
            if (background != null && background.shouldDraw()) {
                background.draw(canvas, p);
            }
            if (count > 0) {
                Ripple[] ripples = this.mExitingRipples;
                for (int i = MASK_NONE; i < count; i += MASK_CONTENT) {
                    ripples[i].draw(canvas, p);
                }
            }
            if (active != null) {
                active.draw(canvas, p);
            }
            canvas.translate(-x, -y);
        }
    }

    private void drawMask(Canvas canvas) {
        this.mMask.draw(canvas);
    }

    private Paint getRipplePaint() {
        if (this.mRipplePaint == null) {
            this.mRipplePaint = new Paint();
            this.mRipplePaint.setAntiAlias(true);
            this.mRipplePaint.setStyle(Style.FILL);
        }
        return this.mRipplePaint;
    }

    public Rect getDirtyBounds() {
        if (!isProjected()) {
            return getBounds();
        }
        Rect drawingBounds = this.mDrawingBounds;
        Rect dirtyBounds = this.mDirtyBounds;
        dirtyBounds.set(drawingBounds);
        drawingBounds.setEmpty();
        int cX = (int) this.mHotspotBounds.exactCenterX();
        int cY = (int) this.mHotspotBounds.exactCenterY();
        Rect rippleBounds = this.mTempRect;
        Ripple[] activeRipples = this.mExitingRipples;
        int N = this.mExitingRipplesCount;
        for (int i = MASK_NONE; i < N; i += MASK_CONTENT) {
            activeRipples[i].getBounds(rippleBounds);
            rippleBounds.offset(cX, cY);
            drawingBounds.union(rippleBounds);
        }
        RippleBackground background = this.mBackground;
        if (background != null) {
            background.getBounds(rippleBounds);
            rippleBounds.offset(cX, cY);
            drawingBounds.union(rippleBounds);
        }
        dirtyBounds.union(drawingBounds);
        dirtyBounds.union(super.getDirtyBounds());
        return dirtyBounds;
    }

    public ConstantState getConstantState() {
        return this.mState;
    }

    public Drawable mutate() {
        super.mutate();
        this.mState = (RippleState) this.mLayerState;
        this.mMask = findDrawableByLayerId(R.id.mask);
        return this;
    }

    RippleState createConstantState(LayerState state, Resources res) {
        return new RippleState(state, this, res);
    }

    public void setMaxRadius(int maxRadius) {
        if (maxRadius == RADIUS_AUTO || maxRadius >= 0) {
            this.mState.mMaxRadius = maxRadius;
            return;
        }
        throw new IllegalArgumentException("maxRadius must be RADIUS_AUTO or >= 0");
    }

    public int getMaxRadius() {
        return this.mState.mMaxRadius;
    }

    private RippleDrawable(RippleState state, Resources res) {
        this.mTempRect = new Rect();
        this.mHotspotBounds = new Rect();
        this.mDrawingBounds = new Rect();
        this.mDirtyBounds = new Rect();
        this.mExitingRipplesCount = MASK_NONE;
        this.mDensity = LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        this.mState = new RippleState(state, this, res);
        this.mLayerState = this.mState;
        if (this.mState.mNum > 0) {
            ensurePadding();
        }
        if (res != null) {
            this.mDensity = res.getDisplayMetrics().density;
        }
        initializeFromState();
    }

    private void initializeFromState() {
        this.mMask = findDrawableByLayerId(R.id.mask);
    }
}
