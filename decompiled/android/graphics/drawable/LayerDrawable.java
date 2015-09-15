package android.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.Drawable.ConstantState;
import android.util.AttributeSet;
import com.android.internal.R;
import java.io.IOException;
import java.util.Collection;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class LayerDrawable extends Drawable implements Callback {
    public static final int PADDING_MODE_NEST = 0;
    public static final int PADDING_MODE_STACK = 1;
    private Rect mHotspotBounds;
    LayerState mLayerState;
    private boolean mMutated;
    private int mOpacityOverride;
    private int[] mPaddingB;
    private int[] mPaddingL;
    private int[] mPaddingR;
    private int[] mPaddingT;
    private final Rect mTmpRect;

    static class ChildDrawable {
        public Drawable mDrawable;
        public int mId;
        public int mInsetB;
        public int mInsetL;
        public int mInsetR;
        public int mInsetT;
        public int[] mThemeAttrs;

        ChildDrawable() {
            this.mId = -1;
        }

        ChildDrawable(ChildDrawable orig, LayerDrawable owner, Resources res) {
            this.mId = -1;
            if (res != null) {
                this.mDrawable = orig.mDrawable.getConstantState().newDrawable(res);
            } else {
                this.mDrawable = orig.mDrawable.getConstantState().newDrawable();
            }
            this.mDrawable.setCallback(owner);
            this.mDrawable.setLayoutDirection(orig.mDrawable.getLayoutDirection());
            this.mDrawable.setBounds(orig.mDrawable.getBounds());
            this.mDrawable.setLevel(orig.mDrawable.getLevel());
            this.mThemeAttrs = orig.mThemeAttrs;
            this.mInsetL = orig.mInsetL;
            this.mInsetT = orig.mInsetT;
            this.mInsetR = orig.mInsetR;
            this.mInsetB = orig.mInsetB;
            this.mId = orig.mId;
        }
    }

    static class LayerState extends ConstantState {
        private boolean mAutoMirrored;
        int mChangingConfigurations;
        ChildDrawable[] mChildren;
        int mChildrenChangingConfigurations;
        private boolean mHaveIsStateful;
        private boolean mHaveOpacity;
        private boolean mIsStateful;
        int mNum;
        private int mOpacity;
        private int mPaddingMode;
        int[] mThemeAttrs;

        LayerState(LayerState orig, LayerDrawable owner, Resources res) {
            this.mAutoMirrored = false;
            this.mPaddingMode = LayerDrawable.PADDING_MODE_NEST;
            if (orig != null) {
                ChildDrawable[] origChildDrawable = orig.mChildren;
                int N = orig.mNum;
                this.mNum = N;
                this.mChildren = new ChildDrawable[N];
                this.mChangingConfigurations = orig.mChangingConfigurations;
                this.mChildrenChangingConfigurations = orig.mChildrenChangingConfigurations;
                for (int i = LayerDrawable.PADDING_MODE_NEST; i < N; i += LayerDrawable.PADDING_MODE_STACK) {
                    this.mChildren[i] = new ChildDrawable(origChildDrawable[i], owner, res);
                }
                this.mHaveOpacity = orig.mHaveOpacity;
                this.mOpacity = orig.mOpacity;
                this.mHaveIsStateful = orig.mHaveIsStateful;
                this.mIsStateful = orig.mIsStateful;
                this.mAutoMirrored = orig.mAutoMirrored;
                this.mPaddingMode = orig.mPaddingMode;
                this.mThemeAttrs = orig.mThemeAttrs;
                return;
            }
            this.mNum = LayerDrawable.PADDING_MODE_NEST;
            this.mChildren = null;
        }

        public boolean canApplyTheme() {
            if (this.mThemeAttrs != null || super.canApplyTheme()) {
                return true;
            }
            ChildDrawable[] array = this.mChildren;
            int N = this.mNum;
            for (int i = LayerDrawable.PADDING_MODE_NEST; i < N; i += LayerDrawable.PADDING_MODE_STACK) {
                ChildDrawable layer = array[i];
                if (layer.mThemeAttrs != null || layer.mDrawable.canApplyTheme()) {
                    return true;
                }
            }
            return false;
        }

        public Drawable newDrawable() {
            return new LayerDrawable(this, null);
        }

        public Drawable newDrawable(Resources res) {
            return new LayerDrawable(this, res);
        }

        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }

        public final int getOpacity() {
            if (this.mHaveOpacity) {
                return this.mOpacity;
            }
            ChildDrawable[] array = this.mChildren;
            int N = this.mNum;
            int op = N > 0 ? array[LayerDrawable.PADDING_MODE_NEST].mDrawable.getOpacity() : -2;
            for (int i = LayerDrawable.PADDING_MODE_STACK; i < N; i += LayerDrawable.PADDING_MODE_STACK) {
                op = Drawable.resolveOpacity(op, array[i].mDrawable.getOpacity());
            }
            this.mOpacity = op;
            this.mHaveOpacity = true;
            return op;
        }

        public final boolean isStateful() {
            if (this.mHaveIsStateful) {
                return this.mIsStateful;
            }
            ChildDrawable[] array = this.mChildren;
            int N = this.mNum;
            boolean isStateful = false;
            for (int i = LayerDrawable.PADDING_MODE_NEST; i < N; i += LayerDrawable.PADDING_MODE_STACK) {
                if (array[i].mDrawable.isStateful()) {
                    isStateful = true;
                    break;
                }
            }
            this.mIsStateful = isStateful;
            this.mHaveIsStateful = true;
            return isStateful;
        }

        public final boolean canConstantState() {
            ChildDrawable[] array = this.mChildren;
            int N = this.mNum;
            for (int i = LayerDrawable.PADDING_MODE_NEST; i < N; i += LayerDrawable.PADDING_MODE_STACK) {
                if (array[i].mDrawable.getConstantState() == null) {
                    return false;
                }
            }
            return true;
        }

        public void invalidateCache() {
            this.mHaveOpacity = false;
            this.mHaveIsStateful = false;
        }

        public int addAtlasableBitmaps(Collection<Bitmap> atlasList) {
            ChildDrawable[] array = this.mChildren;
            int N = this.mNum;
            int pixelCount = LayerDrawable.PADDING_MODE_NEST;
            for (int i = LayerDrawable.PADDING_MODE_NEST; i < N; i += LayerDrawable.PADDING_MODE_STACK) {
                ConstantState state = array[i].mDrawable.getConstantState();
                if (state != null) {
                    pixelCount += state.addAtlasableBitmaps(atlasList);
                }
            }
            return pixelCount;
        }
    }

    public LayerDrawable(Drawable[] layers) {
        this(layers, null);
    }

    LayerDrawable(Drawable[] layers, LayerState state) {
        this(state, null);
        int length = layers.length;
        ChildDrawable[] r = new ChildDrawable[length];
        for (int i = PADDING_MODE_NEST; i < length; i += PADDING_MODE_STACK) {
            r[i] = new ChildDrawable();
            r[i].mDrawable = layers[i];
            layers[i].setCallback(this);
            LayerState layerState = this.mLayerState;
            layerState.mChildrenChangingConfigurations |= layers[i].getChangingConfigurations();
        }
        this.mLayerState.mNum = length;
        this.mLayerState.mChildren = r;
        ensurePadding();
    }

    LayerDrawable() {
        this((LayerState) null, null);
    }

    LayerDrawable(LayerState state, Resources res) {
        this.mOpacityOverride = PADDING_MODE_NEST;
        this.mTmpRect = new Rect();
        this.mLayerState = createConstantState(state, res);
        if (this.mLayerState.mNum > 0) {
            ensurePadding();
        }
    }

    LayerState createConstantState(LayerState state, Resources res) {
        return new LayerState(state, this, res);
    }

    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Theme theme) throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs, theme);
        TypedArray a = Drawable.obtainAttributes(r, theme, attrs, R.styleable.LayerDrawable);
        updateStateFromTypedArray(a);
        a.recycle();
        inflateLayers(r, parser, attrs, theme);
        ensurePadding();
        onStateChange(getState());
    }

    private void updateStateFromTypedArray(TypedArray a) {
        LayerState state = this.mLayerState;
        state.mChangingConfigurations |= a.getChangingConfigurations();
        state.mThemeAttrs = a.extractThemeAttrs();
        this.mOpacityOverride = a.getInt(PADDING_MODE_NEST, this.mOpacityOverride);
        state.mAutoMirrored = a.getBoolean(PADDING_MODE_STACK, state.mAutoMirrored);
        state.mPaddingMode = a.getInteger(2, state.mPaddingMode);
    }

    private void inflateLayers(Resources r, XmlPullParser parser, AttributeSet attrs, Theme theme) throws XmlPullParserException, IOException {
        LayerState state = this.mLayerState;
        int innerDepth = parser.getDepth() + PADDING_MODE_STACK;
        while (true) {
            int type = parser.next();
            if (type != PADDING_MODE_STACK) {
                int depth = parser.getDepth();
                if (depth < innerDepth && type == 3) {
                    return;
                }
                if (type == 2 && depth <= innerDepth && parser.getName().equals("item")) {
                    ChildDrawable layer = new ChildDrawable();
                    TypedArray a = Drawable.obtainAttributes(r, theme, attrs, R.styleable.LayerDrawableItem);
                    updateLayerFromTypedArray(layer, a);
                    a.recycle();
                    if (layer.mDrawable == null) {
                        do {
                            type = parser.next();
                        } while (type == 4);
                        if (type != 2) {
                            break;
                        }
                        layer.mDrawable = Drawable.createFromXmlInner(r, parser, attrs, theme);
                    }
                    if (layer.mDrawable != null) {
                        state.mChildrenChangingConfigurations |= layer.mDrawable.getChangingConfigurations();
                        layer.mDrawable.setCallback(this);
                    }
                    addLayer(layer);
                }
            } else {
                return;
            }
        }
        throw new XmlPullParserException(parser.getPositionDescription() + ": <item> tag requires a 'drawable' attribute or " + "child tag defining a drawable");
    }

    private void updateLayerFromTypedArray(ChildDrawable layer, TypedArray a) {
        LayerState state = this.mLayerState;
        state.mChildrenChangingConfigurations |= a.getChangingConfigurations();
        layer.mThemeAttrs = a.extractThemeAttrs();
        layer.mInsetL = a.getDimensionPixelOffset(2, layer.mInsetL);
        layer.mInsetT = a.getDimensionPixelOffset(3, layer.mInsetT);
        layer.mInsetR = a.getDimensionPixelOffset(4, layer.mInsetR);
        layer.mInsetB = a.getDimensionPixelOffset(5, layer.mInsetB);
        layer.mId = a.getResourceId(PADDING_MODE_NEST, layer.mId);
        Drawable dr = a.getDrawable(PADDING_MODE_STACK);
        if (dr != null) {
            layer.mDrawable = dr;
        }
    }

    public void applyTheme(Theme t) {
        super.applyTheme(t);
        LayerState state = this.mLayerState;
        if (state != null) {
            TypedArray a;
            if (state.mThemeAttrs != null) {
                a = t.resolveAttributes(state.mThemeAttrs, R.styleable.LayerDrawable);
                updateStateFromTypedArray(a);
                a.recycle();
            }
            ChildDrawable[] array = state.mChildren;
            int N = state.mNum;
            for (int i = PADDING_MODE_NEST; i < N; i += PADDING_MODE_STACK) {
                ChildDrawable layer = array[i];
                if (layer.mThemeAttrs != null) {
                    a = t.resolveAttributes(layer.mThemeAttrs, R.styleable.LayerDrawableItem);
                    updateLayerFromTypedArray(layer, a);
                    a.recycle();
                }
                Drawable d = layer.mDrawable;
                if (d.canApplyTheme()) {
                    d.applyTheme(t);
                }
            }
            ensurePadding();
            onStateChange(getState());
        }
    }

    public boolean canApplyTheme() {
        return (this.mLayerState != null && this.mLayerState.canApplyTheme()) || super.canApplyTheme();
    }

    public boolean isProjected() {
        if (super.isProjected()) {
            return true;
        }
        ChildDrawable[] layers = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = PADDING_MODE_NEST; i < N; i += PADDING_MODE_STACK) {
            if (layers[i].mDrawable.isProjected()) {
                return true;
            }
        }
        return false;
    }

    void addLayer(ChildDrawable layer) {
        int N;
        LayerState st = this.mLayerState;
        if (st.mChildren != null) {
            N = st.mChildren.length;
        } else {
            N = PADDING_MODE_NEST;
        }
        int i = st.mNum;
        if (i >= N) {
            ChildDrawable[] nu = new ChildDrawable[(N + 10)];
            if (i > 0) {
                System.arraycopy(st.mChildren, PADDING_MODE_NEST, nu, PADDING_MODE_NEST, i);
            }
            st.mChildren = nu;
        }
        st.mChildren[i] = layer;
        st.mNum += PADDING_MODE_STACK;
        st.invalidateCache();
    }

    ChildDrawable addLayer(Drawable layer, int[] themeAttrs, int id, int left, int top, int right, int bottom) {
        ChildDrawable childDrawable = new ChildDrawable();
        childDrawable.mId = id;
        childDrawable.mThemeAttrs = themeAttrs;
        childDrawable.mDrawable = layer;
        childDrawable.mDrawable.setAutoMirrored(isAutoMirrored());
        childDrawable.mInsetL = left;
        childDrawable.mInsetT = top;
        childDrawable.mInsetR = right;
        childDrawable.mInsetB = bottom;
        addLayer(childDrawable);
        LayerState layerState = this.mLayerState;
        layerState.mChildrenChangingConfigurations |= layer.getChangingConfigurations();
        layer.setCallback(this);
        return childDrawable;
    }

    public Drawable findDrawableByLayerId(int id) {
        ChildDrawable[] layers = this.mLayerState.mChildren;
        for (int i = this.mLayerState.mNum - 1; i >= 0; i--) {
            if (layers[i].mId == id) {
                return layers[i].mDrawable;
            }
        }
        return null;
    }

    public void setId(int index, int id) {
        this.mLayerState.mChildren[index].mId = id;
    }

    public int getNumberOfLayers() {
        return this.mLayerState.mNum;
    }

    public Drawable getDrawable(int index) {
        return this.mLayerState.mChildren[index].mDrawable;
    }

    public int getId(int index) {
        return this.mLayerState.mChildren[index].mId;
    }

    public boolean setDrawableByLayerId(int id, Drawable drawable) {
        ChildDrawable[] layers = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = PADDING_MODE_NEST; i < N; i += PADDING_MODE_STACK) {
            ChildDrawable childDrawable = layers[i];
            if (childDrawable.mId == id) {
                if (childDrawable.mDrawable != null) {
                    if (drawable != null) {
                        drawable.setBounds(childDrawable.mDrawable.getBounds());
                    }
                    childDrawable.mDrawable.setCallback(null);
                }
                if (drawable != null) {
                    drawable.setCallback(this);
                }
                childDrawable.mDrawable = drawable;
                this.mLayerState.invalidateCache();
                return true;
            }
        }
        return false;
    }

    public void setLayerInset(int index, int l, int t, int r, int b) {
        ChildDrawable childDrawable = this.mLayerState.mChildren[index];
        childDrawable.mInsetL = l;
        childDrawable.mInsetT = t;
        childDrawable.mInsetR = r;
        childDrawable.mInsetB = b;
    }

    public void setPaddingMode(int mode) {
        if (this.mLayerState.mPaddingMode != mode) {
            this.mLayerState.mPaddingMode = mode;
        }
    }

    public int getPaddingMode() {
        return this.mLayerState.mPaddingMode;
    }

    public void invalidateDrawable(Drawable who) {
        invalidateSelf();
    }

    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        scheduleSelf(what, when);
    }

    public void unscheduleDrawable(Drawable who, Runnable what) {
        unscheduleSelf(what);
    }

    public void draw(Canvas canvas) {
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = PADDING_MODE_NEST; i < N; i += PADDING_MODE_STACK) {
            array[i].mDrawable.draw(canvas);
        }
    }

    public int getChangingConfigurations() {
        return (super.getChangingConfigurations() | this.mLayerState.mChangingConfigurations) | this.mLayerState.mChildrenChangingConfigurations;
    }

    public boolean getPadding(Rect padding) {
        if (this.mLayerState.mPaddingMode == 0) {
            computeNestedPadding(padding);
        } else {
            computeStackedPadding(padding);
        }
        return (padding.left == 0 && padding.top == 0 && padding.right == 0 && padding.bottom == 0) ? false : true;
    }

    private void computeNestedPadding(Rect padding) {
        padding.left = PADDING_MODE_NEST;
        padding.top = PADDING_MODE_NEST;
        padding.right = PADDING_MODE_NEST;
        padding.bottom = PADDING_MODE_NEST;
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = PADDING_MODE_NEST; i < N; i += PADDING_MODE_STACK) {
            refreshChildPadding(i, array[i]);
            padding.left += this.mPaddingL[i];
            padding.top += this.mPaddingT[i];
            padding.right += this.mPaddingR[i];
            padding.bottom += this.mPaddingB[i];
        }
    }

    private void computeStackedPadding(Rect padding) {
        padding.left = PADDING_MODE_NEST;
        padding.top = PADDING_MODE_NEST;
        padding.right = PADDING_MODE_NEST;
        padding.bottom = PADDING_MODE_NEST;
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = PADDING_MODE_NEST; i < N; i += PADDING_MODE_STACK) {
            refreshChildPadding(i, array[i]);
            padding.left = Math.max(padding.left, this.mPaddingL[i]);
            padding.top = Math.max(padding.top, this.mPaddingT[i]);
            padding.right = Math.max(padding.right, this.mPaddingR[i]);
            padding.bottom = Math.max(padding.bottom, this.mPaddingB[i]);
        }
    }

    public void getOutline(Outline outline) {
        LayerState state = this.mLayerState;
        ChildDrawable[] children = state.mChildren;
        int N = state.mNum;
        int i = PADDING_MODE_NEST;
        while (i < N) {
            children[i].mDrawable.getOutline(outline);
            if (outline.isEmpty()) {
                i += PADDING_MODE_STACK;
            } else {
                return;
            }
        }
    }

    public void setHotspot(float x, float y) {
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = PADDING_MODE_NEST; i < N; i += PADDING_MODE_STACK) {
            array[i].mDrawable.setHotspot(x, y);
        }
    }

    public void setHotspotBounds(int left, int top, int right, int bottom) {
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = PADDING_MODE_NEST; i < N; i += PADDING_MODE_STACK) {
            array[i].mDrawable.setHotspotBounds(left, top, right, bottom);
        }
        if (this.mHotspotBounds == null) {
            this.mHotspotBounds = new Rect(left, top, right, bottom);
        } else {
            this.mHotspotBounds.set(left, top, right, bottom);
        }
    }

    public void getHotspotBounds(Rect outRect) {
        if (this.mHotspotBounds != null) {
            outRect.set(this.mHotspotBounds);
        } else {
            super.getHotspotBounds(outRect);
        }
    }

    public boolean setVisible(boolean visible, boolean restart) {
        boolean changed = super.setVisible(visible, restart);
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = PADDING_MODE_NEST; i < N; i += PADDING_MODE_STACK) {
            array[i].mDrawable.setVisible(visible, restart);
        }
        return changed;
    }

    public void setDither(boolean dither) {
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = PADDING_MODE_NEST; i < N; i += PADDING_MODE_STACK) {
            array[i].mDrawable.setDither(dither);
        }
    }

    public void setAlpha(int alpha) {
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = PADDING_MODE_NEST; i < N; i += PADDING_MODE_STACK) {
            array[i].mDrawable.setAlpha(alpha);
        }
    }

    public int getAlpha() {
        ChildDrawable[] array = this.mLayerState.mChildren;
        if (this.mLayerState.mNum > 0) {
            return array[PADDING_MODE_NEST].mDrawable.getAlpha();
        }
        return super.getAlpha();
    }

    public void setColorFilter(ColorFilter cf) {
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = PADDING_MODE_NEST; i < N; i += PADDING_MODE_STACK) {
            array[i].mDrawable.setColorFilter(cf);
        }
    }

    public void setTintList(ColorStateList tint) {
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = PADDING_MODE_NEST; i < N; i += PADDING_MODE_STACK) {
            array[i].mDrawable.setTintList(tint);
        }
    }

    public void setTintMode(Mode tintMode) {
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = PADDING_MODE_NEST; i < N; i += PADDING_MODE_STACK) {
            array[i].mDrawable.setTintMode(tintMode);
        }
    }

    public void setOpacity(int opacity) {
        this.mOpacityOverride = opacity;
    }

    public int getOpacity() {
        if (this.mOpacityOverride != 0) {
            return this.mOpacityOverride;
        }
        return this.mLayerState.getOpacity();
    }

    public void setAutoMirrored(boolean mirrored) {
        this.mLayerState.mAutoMirrored = mirrored;
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = PADDING_MODE_NEST; i < N; i += PADDING_MODE_STACK) {
            array[i].mDrawable.setAutoMirrored(mirrored);
        }
    }

    public boolean isAutoMirrored() {
        return this.mLayerState.mAutoMirrored;
    }

    public boolean isStateful() {
        return this.mLayerState.isStateful();
    }

    protected boolean onStateChange(int[] state) {
        boolean paddingChanged = false;
        boolean changed = false;
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = PADDING_MODE_NEST; i < N; i += PADDING_MODE_STACK) {
            ChildDrawable r = array[i];
            if (r.mDrawable.isStateful() && r.mDrawable.setState(state)) {
                changed = true;
            }
            if (refreshChildPadding(i, r)) {
                paddingChanged = true;
            }
        }
        if (paddingChanged) {
            onBoundsChange(getBounds());
        }
        return changed;
    }

    protected boolean onLevelChange(int level) {
        boolean paddingChanged = false;
        boolean changed = false;
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = PADDING_MODE_NEST; i < N; i += PADDING_MODE_STACK) {
            ChildDrawable r = array[i];
            if (r.mDrawable.setLevel(level)) {
                changed = true;
            }
            if (refreshChildPadding(i, r)) {
                paddingChanged = true;
            }
        }
        if (paddingChanged) {
            onBoundsChange(getBounds());
        }
        return changed;
    }

    protected void onBoundsChange(Rect bounds) {
        int padL = PADDING_MODE_NEST;
        int padT = PADDING_MODE_NEST;
        int padR = PADDING_MODE_NEST;
        int padB = PADDING_MODE_NEST;
        boolean nest = this.mLayerState.mPaddingMode == 0;
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = PADDING_MODE_NEST; i < N; i += PADDING_MODE_STACK) {
            ChildDrawable r = array[i];
            r.mDrawable.setBounds((bounds.left + r.mInsetL) + padL, (bounds.top + r.mInsetT) + padT, (bounds.right - r.mInsetR) - padR, (bounds.bottom - r.mInsetB) - padB);
            if (nest) {
                padL += this.mPaddingL[i];
                padR += this.mPaddingR[i];
                padT += this.mPaddingT[i];
                padB += this.mPaddingB[i];
            }
        }
    }

    public int getIntrinsicWidth() {
        int width = -1;
        int padL = PADDING_MODE_NEST;
        int padR = PADDING_MODE_NEST;
        boolean nest = this.mLayerState.mPaddingMode == 0;
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = PADDING_MODE_NEST; i < N; i += PADDING_MODE_STACK) {
            ChildDrawable r = array[i];
            int w = (((r.mDrawable.getIntrinsicWidth() + r.mInsetL) + r.mInsetR) + padL) + padR;
            if (w > width) {
                width = w;
            }
            if (nest) {
                padL += this.mPaddingL[i];
                padR += this.mPaddingR[i];
            }
        }
        return width;
    }

    public int getIntrinsicHeight() {
        int height = -1;
        int padT = PADDING_MODE_NEST;
        int padB = PADDING_MODE_NEST;
        boolean nest = this.mLayerState.mPaddingMode == 0;
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = PADDING_MODE_NEST; i < N; i += PADDING_MODE_STACK) {
            ChildDrawable r = array[i];
            int h = (((r.mDrawable.getIntrinsicHeight() + r.mInsetT) + r.mInsetB) + padT) + padB;
            if (h > height) {
                height = h;
            }
            if (nest) {
                padT += this.mPaddingT[i];
                padB += this.mPaddingB[i];
            }
        }
        return height;
    }

    private boolean refreshChildPadding(int i, ChildDrawable r) {
        Rect rect = this.mTmpRect;
        r.mDrawable.getPadding(rect);
        if (rect.left == this.mPaddingL[i] && rect.top == this.mPaddingT[i] && rect.right == this.mPaddingR[i] && rect.bottom == this.mPaddingB[i]) {
            return false;
        }
        this.mPaddingL[i] = rect.left;
        this.mPaddingT[i] = rect.top;
        this.mPaddingR[i] = rect.right;
        this.mPaddingB[i] = rect.bottom;
        return true;
    }

    void ensurePadding() {
        int N = this.mLayerState.mNum;
        if (this.mPaddingL == null || this.mPaddingL.length < N) {
            this.mPaddingL = new int[N];
            this.mPaddingT = new int[N];
            this.mPaddingR = new int[N];
            this.mPaddingB = new int[N];
        }
    }

    public ConstantState getConstantState() {
        if (!this.mLayerState.canConstantState()) {
            return null;
        }
        this.mLayerState.mChangingConfigurations = getChangingConfigurations();
        return this.mLayerState;
    }

    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mLayerState = createConstantState(this.mLayerState, null);
            ChildDrawable[] array = this.mLayerState.mChildren;
            int N = this.mLayerState.mNum;
            for (int i = PADDING_MODE_NEST; i < N; i += PADDING_MODE_STACK) {
                array[i].mDrawable.mutate();
            }
            this.mMutated = true;
        }
        return this;
    }

    public void clearMutated() {
        super.clearMutated();
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = PADDING_MODE_NEST; i < N; i += PADDING_MODE_STACK) {
            array[i].mDrawable.clearMutated();
        }
        this.mMutated = false;
    }

    public void setLayoutDirection(int layoutDirection) {
        ChildDrawable[] array = this.mLayerState.mChildren;
        int N = this.mLayerState.mNum;
        for (int i = PADDING_MODE_NEST; i < N; i += PADDING_MODE_STACK) {
            array[i].mDrawable.setLayoutDirection(layoutDirection);
        }
        super.setLayoutDirection(layoutDirection);
    }
}
