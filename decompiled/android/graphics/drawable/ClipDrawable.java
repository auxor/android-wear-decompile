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
import android.view.Gravity;
import android.view.Window;
import com.android.internal.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ClipDrawable extends Drawable implements Callback {
    public static final int HORIZONTAL = 1;
    public static final int VERTICAL = 2;
    private boolean mMutated;
    private ClipState mState;
    private final Rect mTmpRect;

    static final class ClipState extends ConstantState {
        private boolean mCanConstantState;
        int mChangingConfigurations;
        private boolean mCheckedConstantState;
        Drawable mDrawable;
        int mGravity;
        int mOrientation;
        int[] mThemeAttrs;

        ClipState(ClipState orig, ClipDrawable owner, Resources res) {
            this.mOrientation = ClipDrawable.HORIZONTAL;
            this.mGravity = 3;
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
                this.mOrientation = orig.mOrientation;
                this.mGravity = orig.mGravity;
                this.mCanConstantState = true;
                this.mCheckedConstantState = true;
            }
        }

        public boolean canApplyTheme() {
            return this.mThemeAttrs != null || ((this.mDrawable != null && this.mDrawable.canApplyTheme()) || super.canApplyTheme());
        }

        public Drawable newDrawable() {
            return new ClipDrawable(null, null);
        }

        public Drawable newDrawable(Resources res) {
            return new ClipDrawable(res, null);
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
    }

    ClipDrawable() {
        this(null, null);
    }

    public ClipDrawable(Drawable drawable, int gravity, int orientation) {
        this(null, null);
        this.mState.mDrawable = drawable;
        this.mState.mGravity = gravity;
        this.mState.mOrientation = orientation;
        if (drawable != null) {
            drawable.setCallback(this);
        }
    }

    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Theme theme) throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs, theme);
        TypedArray a = Drawable.obtainAttributes(r, theme, attrs, R.styleable.ClipDrawable);
        this.mState.mDrawable = null;
        updateStateFromTypedArray(a);
        inflateChildElements(r, parser, attrs, theme);
        verifyRequiredAttributes(a);
        a.recycle();
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
        throw new UnsupportedOperationException("Method not decompiled: android.graphics.drawable.ClipDrawable.inflateChildElements(android.content.res.Resources, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.content.res.Resources$Theme):void");
    }

    private void verifyRequiredAttributes(TypedArray a) throws XmlPullParserException {
        if (this.mState.mDrawable != null) {
            return;
        }
        if (this.mState.mThemeAttrs == null || this.mState.mThemeAttrs[HORIZONTAL] == 0) {
            throw new XmlPullParserException(a.getPositionDescription() + ": <clip> tag requires a 'drawable' attribute or " + "child tag defining a drawable");
        }
    }

    private void updateStateFromTypedArray(TypedArray a) {
        ClipState state = this.mState;
        state.mChangingConfigurations |= a.getChangingConfigurations();
        state.mThemeAttrs = a.extractThemeAttrs();
        state.mOrientation = a.getInt(VERTICAL, state.mOrientation);
        state.mGravity = a.getInt(0, state.mGravity);
        Drawable dr = a.getDrawable(HORIZONTAL);
        if (dr != null) {
            state.mDrawable = dr;
            dr.setCallback(this);
        }
    }

    public void applyTheme(Theme t) {
        super.applyTheme(t);
        ClipState state = this.mState;
        if (state != null && state.mThemeAttrs != null) {
            TypedArray a = t.resolveAttributes(state.mThemeAttrs, R.styleable.ClipDrawable);
            try {
                updateStateFromTypedArray(a);
                verifyRequiredAttributes(a);
                a.recycle();
                if (state.mDrawable != null && state.mDrawable.canApplyTheme()) {
                    state.mDrawable.applyTheme(t);
                }
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
        return this.mState.mDrawable.setState(state);
    }

    protected boolean onLevelChange(int level) {
        this.mState.mDrawable.setLevel(level);
        invalidateSelf();
        return true;
    }

    protected void onBoundsChange(Rect bounds) {
        this.mState.mDrawable.setBounds(bounds);
    }

    public void draw(Canvas canvas) {
        if (this.mState.mDrawable.getLevel() != 0) {
            Rect r = this.mTmpRect;
            Rect bounds = getBounds();
            int level = getLevel();
            int w = bounds.width();
            if ((this.mState.mOrientation & HORIZONTAL) != 0) {
                w -= ((w + 0) * (10000 - level)) / Window.PROGRESS_END;
            }
            int h = bounds.height();
            if ((this.mState.mOrientation & VERTICAL) != 0) {
                h -= ((h + 0) * (10000 - level)) / Window.PROGRESS_END;
            }
            Gravity.apply(this.mState.mGravity, w, h, bounds, r, getLayoutDirection());
            if (w > 0 && h > 0) {
                canvas.save();
                canvas.clipRect(r);
                this.mState.mDrawable.draw(canvas);
                canvas.restore();
            }
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

    public void setLayoutDirection(int layoutDirection) {
        this.mState.mDrawable.setLayoutDirection(layoutDirection);
        super.setLayoutDirection(layoutDirection);
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

    private ClipDrawable(ClipState state, Resources res) {
        this.mTmpRect = new Rect();
        this.mState = new ClipState(state, this, res);
    }
}
