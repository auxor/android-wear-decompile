package android.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.Drawable.ConstantState;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.WindowManager.LayoutParams;
import com.android.internal.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class RotateDrawable extends Drawable implements Callback {
    private static final float MAX_LEVEL = 10000.0f;
    private boolean mMutated;
    private final RotateState mState;

    static final class RotateState extends ConstantState {
        private boolean mCanConstantState;
        int mChangingConfigurations;
        private boolean mCheckedConstantState;
        float mCurrentDegrees;
        Drawable mDrawable;
        float mFromDegrees;
        float mPivotX;
        boolean mPivotXRel;
        float mPivotY;
        boolean mPivotYRel;
        int[] mThemeAttrs;
        float mToDegrees;

        RotateState(RotateState orig, RotateDrawable owner, Resources res) {
            this.mPivotXRel = true;
            this.mPivotX = 0.5f;
            this.mPivotYRel = true;
            this.mPivotY = 0.5f;
            this.mFromDegrees = 0.0f;
            this.mToDegrees = 360.0f;
            this.mCurrentDegrees = 0.0f;
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
                this.mPivotXRel = orig.mPivotXRel;
                this.mPivotX = orig.mPivotX;
                this.mPivotYRel = orig.mPivotYRel;
                this.mPivotY = orig.mPivotY;
                this.mFromDegrees = orig.mFromDegrees;
                this.mToDegrees = orig.mToDegrees;
                this.mCurrentDegrees = orig.mCurrentDegrees;
                this.mCanConstantState = true;
                this.mCheckedConstantState = true;
            }
        }

        public boolean canApplyTheme() {
            return this.mThemeAttrs != null || ((this.mDrawable != null && this.mDrawable.canApplyTheme()) || super.canApplyTheme());
        }

        public Drawable newDrawable() {
            return new RotateDrawable(null, null);
        }

        public Drawable newDrawable(Resources res) {
            return new RotateDrawable(res, null);
        }

        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }

        public boolean canConstantState() {
            if (!this.mCheckedConstantState) {
                this.mCanConstantState = this.mDrawable.getConstantState() != null;
                this.mCheckedConstantState = true;
            }
            return this.mCanConstantState;
        }
    }

    public RotateDrawable() {
        this(null, null);
    }

    private RotateDrawable(RotateState rotateState, Resources res) {
        this.mState = new RotateState(rotateState, this, res);
    }

    public void draw(Canvas canvas) {
        RotateState st = this.mState;
        Drawable d = st.mDrawable;
        Rect bounds = d.getBounds();
        int h = bounds.bottom - bounds.top;
        float px = st.mPivotXRel ? ((float) (bounds.right - bounds.left)) * st.mPivotX : st.mPivotX;
        float py = st.mPivotYRel ? ((float) h) * st.mPivotY : st.mPivotY;
        int saveCount = canvas.save();
        canvas.rotate(st.mCurrentDegrees, ((float) bounds.left) + px, ((float) bounds.top) + py);
        d.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    public void setDrawable(Drawable drawable) {
        Drawable oldDrawable = this.mState.mDrawable;
        if (oldDrawable != drawable) {
            if (oldDrawable != null) {
                oldDrawable.setCallback(null);
            }
            this.mState.mDrawable = drawable;
            if (drawable != null) {
                drawable.setCallback(this);
            }
        }
    }

    public Drawable getDrawable() {
        return this.mState.mDrawable;
    }

    public int getChangingConfigurations() {
        return (super.getChangingConfigurations() | this.mState.mChangingConfigurations) | this.mState.mDrawable.getChangingConfigurations();
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

    public void setFromDegrees(float fromDegrees) {
        if (this.mState.mFromDegrees != fromDegrees) {
            this.mState.mFromDegrees = fromDegrees;
            invalidateSelf();
        }
    }

    public float getFromDegrees() {
        return this.mState.mFromDegrees;
    }

    public void setToDegrees(float toDegrees) {
        if (this.mState.mToDegrees != toDegrees) {
            this.mState.mToDegrees = toDegrees;
            invalidateSelf();
        }
    }

    public float getToDegrees() {
        return this.mState.mToDegrees;
    }

    public void setPivotX(float pivotX) {
        if (this.mState.mPivotX != pivotX) {
            this.mState.mPivotX = pivotX;
            invalidateSelf();
        }
    }

    public float getPivotX() {
        return this.mState.mPivotX;
    }

    public void setPivotXRelative(boolean relative) {
        if (this.mState.mPivotXRel != relative) {
            this.mState.mPivotXRel = relative;
            invalidateSelf();
        }
    }

    public boolean isPivotXRelative() {
        return this.mState.mPivotXRel;
    }

    public void setPivotY(float pivotY) {
        if (this.mState.mPivotY != pivotY) {
            this.mState.mPivotY = pivotY;
            invalidateSelf();
        }
    }

    public float getPivotY() {
        return this.mState.mPivotY;
    }

    public void setPivotYRelative(boolean relative) {
        if (this.mState.mPivotYRel != relative) {
            this.mState.mPivotYRel = relative;
            invalidateSelf();
        }
    }

    public boolean isPivotYRelative() {
        return this.mState.mPivotYRel;
    }

    public boolean canApplyTheme() {
        return (this.mState != null && this.mState.canApplyTheme()) || super.canApplyTheme();
    }

    public void invalidateDrawable(Drawable who) {
        Callback callback = getCallback();
        if (callback != null) {
            callback.invalidateDrawable(this);
        }
    }

    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        Callback callback = getCallback();
        if (callback != null) {
            callback.scheduleDrawable(this, what, when);
        }
    }

    public void unscheduleDrawable(Drawable who, Runnable what) {
        Callback callback = getCallback();
        if (callback != null) {
            callback.unscheduleDrawable(this, what);
        }
    }

    public boolean getPadding(Rect padding) {
        return this.mState.mDrawable.getPadding(padding);
    }

    public boolean setVisible(boolean visible, boolean restart) {
        this.mState.mDrawable.setVisible(visible, restart);
        return super.setVisible(visible, restart);
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
        this.mState.mCurrentDegrees = this.mState.mFromDegrees + ((this.mState.mToDegrees - this.mState.mFromDegrees) * (((float) level) / MAX_LEVEL));
        invalidateSelf();
        return true;
    }

    protected void onBoundsChange(Rect bounds) {
        this.mState.mDrawable.setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
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

    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Theme theme) throws XmlPullParserException, IOException {
        TypedArray a = Drawable.obtainAttributes(r, theme, attrs, R.styleable.RotateDrawable);
        super.inflateWithAttributes(r, parser, a, 0);
        this.mState.mDrawable = null;
        updateStateFromTypedArray(a);
        inflateChildElements(r, parser, attrs, theme);
        verifyRequiredAttributes(a);
        a.recycle();
    }

    public void applyTheme(Theme t) {
        super.applyTheme(t);
        RotateState state = this.mState;
        if (state != null) {
            if (state.mThemeAttrs != null) {
                TypedArray a = t.resolveAttributes(state.mThemeAttrs, R.styleable.RotateDrawable);
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
        throw new UnsupportedOperationException("Method not decompiled: android.graphics.drawable.RotateDrawable.inflateChildElements(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.content.res.Resources$Theme):void");
    }

    private void verifyRequiredAttributes(TypedArray a) throws XmlPullParserException {
        if (this.mState.mDrawable != null) {
            return;
        }
        if (this.mState.mThemeAttrs == null || this.mState.mThemeAttrs[0] == 0) {
            throw new XmlPullParserException(a.getPositionDescription() + ": <rotate> tag requires a 'drawable' attribute or " + "child tag defining a drawable");
        }
    }

    private void updateStateFromTypedArray(TypedArray a) {
        boolean z = false;
        RotateState state = this.mState;
        state.mChangingConfigurations |= a.getChangingConfigurations();
        state.mThemeAttrs = a.extractThemeAttrs();
        if (a.hasValue(4)) {
            TypedValue tv;
            boolean z2;
            tv = a.peekValue(4);
            if (tv.type == 6) {
                z2 = true;
            } else {
                z2 = false;
            }
            state.mPivotXRel = z2;
            state.mPivotX = state.mPivotXRel ? tv.getFraction(LayoutParams.BRIGHTNESS_OVERRIDE_FULL, LayoutParams.BRIGHTNESS_OVERRIDE_FULL) : tv.getFloat();
        }
        if (a.hasValue(5)) {
            tv = a.peekValue(5);
            if (tv.type == 6) {
                z = true;
            }
            state.mPivotYRel = z;
            state.mPivotY = state.mPivotYRel ? tv.getFraction(LayoutParams.BRIGHTNESS_OVERRIDE_FULL, LayoutParams.BRIGHTNESS_OVERRIDE_FULL) : tv.getFloat();
        }
        state.mFromDegrees = a.getFloat(2, state.mFromDegrees);
        state.mToDegrees = a.getFloat(3, state.mToDegrees);
        state.mCurrentDegrees = state.mFromDegrees;
        Drawable dr = a.getDrawable(1);
        if (dr != null) {
            state.mDrawable = dr;
            dr.setCallback(this);
        }
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
}
