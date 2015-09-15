package android.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Insets;
import android.graphics.Outline;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.Drawable.ConstantState;
import android.util.AttributeSet;
import android.widget.Toast;
import com.android.internal.R;
import java.io.IOException;
import java.util.Collection;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class InsetDrawable extends Drawable implements Callback {
    private boolean mMutated;
    private final InsetState mState;
    private final Rect mTmpRect;

    static final class InsetState extends ConstantState {
        private boolean mCanConstantState;
        int mChangingConfigurations;
        private boolean mCheckedConstantState;
        Drawable mDrawable;
        int mInsetBottom;
        int mInsetLeft;
        int mInsetRight;
        int mInsetTop;
        int[] mThemeAttrs;

        InsetState(InsetState orig, InsetDrawable owner, Resources res) {
            this.mInsetLeft = 0;
            this.mInsetTop = 0;
            this.mInsetRight = 0;
            this.mInsetBottom = 0;
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
                this.mInsetLeft = orig.mInsetLeft;
                this.mInsetTop = orig.mInsetTop;
                this.mInsetRight = orig.mInsetRight;
                this.mInsetBottom = orig.mInsetBottom;
                this.mCanConstantState = true;
                this.mCheckedConstantState = true;
            }
        }

        public boolean canApplyTheme() {
            return this.mThemeAttrs != null || ((this.mDrawable != null && this.mDrawable.canApplyTheme()) || super.canApplyTheme());
        }

        public Drawable newDrawable() {
            return new InsetDrawable(null, null);
        }

        public Drawable newDrawable(Resources res) {
            return new InsetDrawable(res, null);
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

    InsetDrawable() {
        this(null, null);
    }

    public InsetDrawable(Drawable drawable, int inset) {
        this(drawable, inset, inset, inset, inset);
    }

    public InsetDrawable(Drawable drawable, int insetLeft, int insetTop, int insetRight, int insetBottom) {
        this(null, null);
        this.mState.mDrawable = drawable;
        this.mState.mInsetLeft = insetLeft;
        this.mState.mInsetTop = insetTop;
        this.mState.mInsetRight = insetRight;
        this.mState.mInsetBottom = insetBottom;
        if (drawable != null) {
            drawable.setCallback(this);
        }
    }

    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Theme theme) throws XmlPullParserException, IOException {
        TypedArray a = Drawable.obtainAttributes(r, theme, attrs, R.styleable.InsetDrawable);
        super.inflateWithAttributes(r, parser, a, 0);
        this.mState.mDrawable = null;
        updateStateFromTypedArray(a);
        inflateChildElements(r, parser, attrs, theme);
        verifyRequiredAttributes(a);
        a.recycle();
    }

    private void inflateChildElements(Resources r, XmlPullParser parser, AttributeSet attrs, Theme theme) throws XmlPullParserException, IOException {
        if (this.mState.mDrawable == null) {
            int type;
            do {
                type = parser.next();
            } while (type == 4);
            if (type != 2) {
                throw new XmlPullParserException(parser.getPositionDescription() + ": <inset> tag requires a 'drawable' attribute or " + "child tag defining a drawable");
            }
            Drawable dr = Drawable.createFromXmlInner(r, parser, attrs, theme);
            this.mState.mDrawable = dr;
            dr.setCallback(this);
        }
    }

    private void verifyRequiredAttributes(TypedArray a) throws XmlPullParserException {
        if (this.mState.mDrawable != null) {
            return;
        }
        if (this.mState.mThemeAttrs == null || this.mState.mThemeAttrs[1] == 0) {
            throw new XmlPullParserException(a.getPositionDescription() + ": <inset> tag requires a 'drawable' attribute or " + "child tag defining a drawable");
        }
    }

    private void updateStateFromTypedArray(TypedArray a) throws XmlPullParserException {
        InsetState state = this.mState;
        state.mChangingConfigurations |= a.getChangingConfigurations();
        state.mThemeAttrs = a.extractThemeAttrs();
        int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case Toast.LENGTH_LONG /*1*/:
                    Drawable dr = a.getDrawable(attr);
                    if (dr == null) {
                        break;
                    }
                    state.mDrawable = dr;
                    dr.setCallback(this);
                    break;
                case Action.MERGE_IGNORE /*2*/:
                    state.mInsetLeft = a.getDimensionPixelOffset(attr, state.mInsetLeft);
                    break;
                case SetDrawableParameters.TAG /*3*/:
                    state.mInsetRight = a.getDimensionPixelOffset(attr, state.mInsetRight);
                    break;
                case ViewGroupAction.TAG /*4*/:
                    state.mInsetTop = a.getDimensionPixelOffset(attr, state.mInsetTop);
                    break;
                case ReflectionActionWithoutParams.TAG /*5*/:
                    state.mInsetBottom = a.getDimensionPixelOffset(attr, state.mInsetBottom);
                    break;
                case SetEmptyView.TAG /*6*/:
                    int inset = a.getDimensionPixelOffset(attr, RtlSpacingHelper.UNDEFINED);
                    if (inset == RtlSpacingHelper.UNDEFINED) {
                        break;
                    }
                    state.mInsetLeft = inset;
                    state.mInsetTop = inset;
                    state.mInsetRight = inset;
                    state.mInsetBottom = inset;
                    break;
                default:
                    break;
            }
        }
    }

    public void applyTheme(Theme t) {
        super.applyTheme(t);
        InsetState state = this.mState;
        if (state != null) {
            if (state.mThemeAttrs != null) {
                TypedArray a = t.resolveAttributes(state.mThemeAttrs, R.styleable.InsetDrawable);
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

    public void draw(Canvas canvas) {
        this.mState.mDrawable.draw(canvas);
    }

    public int getChangingConfigurations() {
        return (super.getChangingConfigurations() | this.mState.mChangingConfigurations) | this.mState.mDrawable.getChangingConfigurations();
    }

    public boolean getPadding(Rect padding) {
        boolean pad = this.mState.mDrawable.getPadding(padding);
        padding.left += this.mState.mInsetLeft;
        padding.right += this.mState.mInsetRight;
        padding.top += this.mState.mInsetTop;
        padding.bottom += this.mState.mInsetBottom;
        return pad || (((this.mState.mInsetLeft | this.mState.mInsetRight) | this.mState.mInsetTop) | this.mState.mInsetBottom) != 0;
    }

    public Insets getOpticalInsets() {
        Insets contentInsets = super.getOpticalInsets();
        return Insets.of(contentInsets.left + this.mState.mInsetLeft, contentInsets.top + this.mState.mInsetTop, contentInsets.right + this.mState.mInsetRight, contentInsets.bottom + this.mState.mInsetBottom);
    }

    public void setHotspot(float x, float y) {
        this.mState.mDrawable.setHotspot(x, y);
    }

    public void setHotspotBounds(int left, int top, int right, int bottom) {
        this.mState.mDrawable.setHotspotBounds(left, top, right, bottom);
    }

    public void getHotspotBounds(Rect outRect) {
        this.mState.mDrawable.getHotspotBounds(outRect);
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

    public void setLayoutDirection(int layoutDirection) {
        this.mState.mDrawable.setLayoutDirection(layoutDirection);
    }

    public int getOpacity() {
        InsetState state = this.mState;
        int opacity = state.mDrawable.getOpacity();
        if (opacity != -1) {
            return opacity;
        }
        if (state.mInsetLeft > 0 || state.mInsetTop > 0 || state.mInsetRight > 0 || state.mInsetBottom > 0) {
            return -3;
        }
        return opacity;
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
        return this.mState.mDrawable.setLevel(level);
    }

    protected void onBoundsChange(Rect bounds) {
        Rect r = this.mTmpRect;
        r.set(bounds);
        r.left += this.mState.mInsetLeft;
        r.top += this.mState.mInsetTop;
        r.right -= this.mState.mInsetRight;
        r.bottom -= this.mState.mInsetBottom;
        this.mState.mDrawable.setBounds(r.left, r.top, r.right, r.bottom);
    }

    public int getIntrinsicWidth() {
        return (this.mState.mDrawable.getIntrinsicWidth() + this.mState.mInsetLeft) + this.mState.mInsetRight;
    }

    public int getIntrinsicHeight() {
        return (this.mState.mDrawable.getIntrinsicHeight() + this.mState.mInsetTop) + this.mState.mInsetBottom;
    }

    public void getOutline(Outline outline) {
        this.mState.mDrawable.getOutline(outline);
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

    public Drawable getDrawable() {
        return this.mState.mDrawable;
    }

    private InsetDrawable(InsetState state, Resources res) {
        this.mTmpRect = new Rect();
        this.mState = new InsetState(state, this, res);
    }
}
