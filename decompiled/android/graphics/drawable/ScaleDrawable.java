package android.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.Drawable.ConstantState;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.Gravity;
import com.android.internal.R;
import java.io.IOException;
import java.util.Collection;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ScaleDrawable extends Drawable implements Callback {
    private boolean mMutated;
    private ScaleState mState;
    private final Rect mTmpRect;

    static final class ScaleState extends ConstantState {
        private static final float DO_NOT_SCALE = -1.0f;
        private boolean mCanConstantState;
        int mChangingConfigurations;
        private boolean mCheckedConstantState;
        Drawable mDrawable;
        int mGravity;
        float mScaleHeight;
        float mScaleWidth;
        int[] mThemeAttrs;
        boolean mUseIntrinsicSizeAsMin;

        ScaleState(ScaleState orig, ScaleDrawable owner, Resources res) {
            this.mScaleWidth = DO_NOT_SCALE;
            this.mScaleHeight = DO_NOT_SCALE;
            this.mGravity = 3;
            this.mUseIntrinsicSizeAsMin = false;
            if (orig != null) {
                this.mThemeAttrs = orig.mThemeAttrs;
                this.mChangingConfigurations = orig.mChangingConfigurations;
                if (res != null) {
                    this.mDrawable = orig.mDrawable.getConstantState().newDrawable(res);
                } else {
                    this.mDrawable = orig.mDrawable.getConstantState().newDrawable();
                }
                this.mDrawable.setCallback(owner);
                this.mDrawable.setLayoutDirection(orig.mDrawable.getLayoutDirection());
                this.mDrawable.setBounds(orig.mDrawable.getBounds());
                this.mDrawable.setLevel(orig.mDrawable.getLevel());
                this.mScaleWidth = orig.mScaleWidth;
                this.mScaleHeight = orig.mScaleHeight;
                this.mGravity = orig.mGravity;
                this.mUseIntrinsicSizeAsMin = orig.mUseIntrinsicSizeAsMin;
                this.mCanConstantState = true;
                this.mCheckedConstantState = true;
            }
        }

        public boolean canApplyTheme() {
            return this.mThemeAttrs != null || ((this.mDrawable != null && this.mDrawable.canApplyTheme()) || super.canApplyTheme());
        }

        public Drawable newDrawable() {
            return new ScaleDrawable(null, null);
        }

        public Drawable newDrawable(Resources res) {
            return new ScaleDrawable(res, null);
        }

        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }

        boolean canConstantState() {
            if (!this.mCheckedConstantState) {
                this.mCanConstantState = this.mDrawable.getConstantState() != null;
                this.mCheckedConstantState = true;
            }
            return this.mCanConstantState;
        }

        public int addAtlasableBitmaps(Collection<Bitmap> atlasList) {
            ConstantState state = this.mDrawable.getConstantState();
            if (state != null) {
                return state.addAtlasableBitmaps(atlasList);
            }
            return 0;
        }
    }

    ScaleDrawable() {
        this(null, null);
    }

    public ScaleDrawable(Drawable drawable, int gravity, float scaleWidth, float scaleHeight) {
        this(null, null);
        this.mState.mDrawable = drawable;
        this.mState.mGravity = gravity;
        this.mState.mScaleWidth = scaleWidth;
        this.mState.mScaleHeight = scaleHeight;
        if (drawable != null) {
            drawable.setCallback(this);
        }
    }

    public Drawable getDrawable() {
        return this.mState.mDrawable;
    }

    private static float getPercent(TypedArray a, int name, float defaultValue) {
        String s = a.getString(name);
        if (s == null || !s.endsWith("%")) {
            return defaultValue;
        }
        return Float.parseFloat(s.substring(0, s.length() - 1)) / SensorManager.LIGHT_CLOUDY;
    }

    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Theme theme) throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs, theme);
        TypedArray a = Drawable.obtainAttributes(r, theme, attrs, R.styleable.ScaleDrawable);
        this.mState.mDrawable = null;
        updateStateFromTypedArray(a);
        inflateChildElements(r, parser, attrs, theme);
        verifyRequiredAttributes(a);
        a.recycle();
    }

    public void applyTheme(Theme t) {
        super.applyTheme(t);
        ScaleState state = this.mState;
        if (state != null) {
            if (state.mThemeAttrs != null) {
                TypedArray a = t.resolveAttributes(state.mThemeAttrs, R.styleable.ScaleDrawable);
                try {
                    updateStateFromTypedArray(a);
                    verifyRequiredAttributes(a);
                    a.recycle();
                } catch (XmlPullParserException e) {
                    throw new RuntimeException(e);
                } catch (Throwable th) {
                    a.recycle();
                }
            }
            if (state.mDrawable != null && state.mDrawable.canApplyTheme()) {
                state.mDrawable.applyTheme(t);
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void inflateChildElements(android.content.res.Resources r5, org.xmlpull.v1.XmlPullParser r6, android.util.AttributeSet r7, android.content.res.Resources.Theme r8) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        r4 = this;
        r0 = 0;
        r1 = r6.getDepth();
    L_0x0005:
        r2 = r6.next();
        r3 = 1;
        if (r2 == r3) goto L_0x001d;
    L_0x000c:
        r3 = 3;
        if (r2 != r3) goto L_0x0015;
    L_0x000f:
        r3 = r6.getDepth();
        if (r3 <= r1) goto L_0x001d;
    L_0x0015:
        r3 = 2;
        if (r2 != r3) goto L_0x0005;
    L_0x0018:
        r0 = android.graphics.drawable.Drawable.createFromXmlInner(r5, r6, r7, r8);
        goto L_0x0005;
    L_0x001d:
        if (r0 == 0) goto L_0x0026;
    L_0x001f:
        r3 = r4.mState;
        r3.mDrawable = r0;
        r0.setCallback(r4);
    L_0x0026:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.graphics.drawable.ScaleDrawable.inflateChildElements(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.content.res.Resources$Theme):void");
    }

    private void verifyRequiredAttributes(TypedArray a) throws XmlPullParserException {
        if (this.mState.mDrawable != null) {
            return;
        }
        if (this.mState.mThemeAttrs == null || this.mState.mThemeAttrs[0] == 0) {
            throw new XmlPullParserException(a.getPositionDescription() + ": <scale> tag requires a 'drawable' attribute or " + "child tag defining a drawable");
        }
    }

    private void updateStateFromTypedArray(TypedArray a) {
        ScaleState state = this.mState;
        state.mChangingConfigurations |= a.getChangingConfigurations();
        state.mThemeAttrs = a.extractThemeAttrs();
        state.mScaleWidth = getPercent(a, 1, state.mScaleWidth);
        state.mScaleHeight = getPercent(a, 2, state.mScaleHeight);
        state.mGravity = a.getInt(3, state.mGravity);
        state.mUseIntrinsicSizeAsMin = a.getBoolean(4, state.mUseIntrinsicSizeAsMin);
        Drawable dr = a.getDrawable(0);
        if (dr != null) {
            state.mDrawable = dr;
            dr.setCallback(this);
        }
    }

    public boolean canApplyTheme() {
        return (this.mState != null && this.mState.canApplyTheme()) || super.canApplyTheme();
    }

    public void invalidateDrawable(Drawable who) {
        if (getCallback() != null) {
            getCallback().invalidateDrawable(this);
        }
    }

    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        if (getCallback() != null) {
            getCallback().scheduleDrawable(this, what, when);
        }
    }

    public void unscheduleDrawable(Drawable who, Runnable what) {
        if (getCallback() != null) {
            getCallback().unscheduleDrawable(this, what);
        }
    }

    public void draw(Canvas canvas) {
        if (this.mState.mDrawable.getLevel() != 0) {
            this.mState.mDrawable.draw(canvas);
        }
    }

    public int getChangingConfigurations() {
        return (super.getChangingConfigurations() | this.mState.mChangingConfigurations) | this.mState.mDrawable.getChangingConfigurations();
    }

    public boolean getPadding(Rect padding) {
        return this.mState.mDrawable.getPadding(padding);
    }

    public boolean setVisible(boolean visible, boolean restart) {
        this.mState.mDrawable.setVisible(visible, restart);
        return super.setVisible(visible, restart);
    }

    public void setAlpha(int alpha) {
        this.mState.mDrawable.setAlpha(alpha);
    }

    public int getAlpha() {
        return this.mState.mDrawable.getAlpha();
    }

    public void setColorFilter(ColorFilter cf) {
        this.mState.mDrawable.setColorFilter(cf);
    }

    public void setTintList(ColorStateList tint) {
        this.mState.mDrawable.setTintList(tint);
    }

    public void setTintMode(Mode tintMode) {
        this.mState.mDrawable.setTintMode(tintMode);
    }

    public int getOpacity() {
        return this.mState.mDrawable.getOpacity();
    }

    public boolean isStateful() {
        return this.mState.mDrawable.isStateful();
    }

    protected boolean onStateChange(int[] state) {
        boolean changed = this.mState.mDrawable.setState(state);
        onBoundsChange(getBounds());
        return changed;
    }

    protected boolean onLevelChange(int level) {
        this.mState.mDrawable.setLevel(level);
        onBoundsChange(getBounds());
        invalidateSelf();
        return true;
    }

    protected void onBoundsChange(Rect bounds) {
        Rect r = this.mTmpRect;
        boolean min = this.mState.mUseIntrinsicSizeAsMin;
        int level = getLevel();
        int w = bounds.width();
        if (this.mState.mScaleWidth > 0.0f) {
            int iw;
            if (min) {
                iw = this.mState.mDrawable.getIntrinsicWidth();
            } else {
                iw = 0;
            }
            w -= (int) ((((float) ((w - iw) * (10000 - level))) * this.mState.mScaleWidth) / SensorManager.LIGHT_OVERCAST);
        }
        int h = bounds.height();
        if (this.mState.mScaleHeight > 0.0f) {
            int ih;
            if (min) {
                ih = this.mState.mDrawable.getIntrinsicHeight();
            } else {
                ih = 0;
            }
            h -= (int) ((((float) ((h - ih) * (10000 - level))) * this.mState.mScaleHeight) / SensorManager.LIGHT_OVERCAST);
        }
        Gravity.apply(this.mState.mGravity, w, h, bounds, r, getLayoutDirection());
        if (w > 0 && h > 0) {
            this.mState.mDrawable.setBounds(r.left, r.top, r.right, r.bottom);
        }
    }

    public int getIntrinsicWidth() {
        return this.mState.mDrawable.getIntrinsicWidth();
    }

    public int getIntrinsicHeight() {
        return this.mState.mDrawable.getIntrinsicHeight();
    }

    public ConstantState getConstantState() {
        if (!this.mState.canConstantState()) {
            return null;
        }
        this.mState.mChangingConfigurations = getChangingConfigurations();
        return this.mState;
    }

    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mState.mDrawable.mutate();
            this.mMutated = true;
        }
        return this;
    }

    public void clearMutated() {
        super.clearMutated();
        this.mState.mDrawable.clearMutated();
        this.mMutated = false;
    }

    private ScaleDrawable(ScaleState state, Resources res) {
        this.mTmpRect = new Rect();
        this.mState = new ScaleState(state, this, res);
    }
}
