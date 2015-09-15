package android.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Insets;
import android.graphics.NinePatch;
import android.graphics.Outline;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Xfermode;
import android.os.BatteryManager;
import android.os.Trace;
import android.telephony.SubscriptionManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.StateSet;
import android.util.TypedValue;
import android.util.Xml;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.SpellChecker;
import android.widget.Toast;
import com.android.internal.R;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public abstract class Drawable {
    static final Mode DEFAULT_TINT_MODE;
    private static final Rect ZERO_BOUNDS_RECT;
    private Rect mBounds;
    private WeakReference<Callback> mCallback;
    private int mChangingConfigurations;
    private int mLayoutDirection;
    private int mLevel;
    private int[] mStateSet;
    private boolean mVisible;

    public interface Callback {
        void invalidateDrawable(Drawable drawable);

        void scheduleDrawable(Drawable drawable, Runnable runnable, long j);

        void unscheduleDrawable(Drawable drawable, Runnable runnable);
    }

    public static abstract class ConstantState {
        public abstract int getChangingConfigurations();

        public abstract Drawable newDrawable();

        public Drawable newDrawable(Resources res) {
            return newDrawable();
        }

        public Drawable newDrawable(Resources res, Theme theme) {
            return newDrawable(null);
        }

        public int addAtlasableBitmaps(Collection<Bitmap> collection) {
            return 0;
        }

        protected final boolean isAtlasable(Bitmap bitmap) {
            return bitmap != null && bitmap.getConfig() == Config.ARGB_8888;
        }

        public boolean canApplyTheme() {
            return false;
        }
    }

    public abstract void draw(Canvas canvas);

    public abstract int getOpacity();

    public abstract void setAlpha(int i);

    public abstract void setColorFilter(ColorFilter colorFilter);

    public Drawable() {
        this.mStateSet = StateSet.WILD_CARD;
        this.mLevel = 0;
        this.mChangingConfigurations = 0;
        this.mBounds = ZERO_BOUNDS_RECT;
        this.mCallback = null;
        this.mVisible = true;
    }

    static {
        ZERO_BOUNDS_RECT = new Rect();
        DEFAULT_TINT_MODE = Mode.SRC_IN;
    }

    public void setBounds(int left, int top, int right, int bottom) {
        Rect oldBounds = this.mBounds;
        if (oldBounds == ZERO_BOUNDS_RECT) {
            oldBounds = new Rect();
            this.mBounds = oldBounds;
        }
        if (oldBounds.left != left || oldBounds.top != top || oldBounds.right != right || oldBounds.bottom != bottom) {
            if (!oldBounds.isEmpty()) {
                invalidateSelf();
            }
            this.mBounds.set(left, top, right, bottom);
            onBoundsChange(this.mBounds);
        }
    }

    public void setBounds(Rect bounds) {
        setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
    }

    public final void copyBounds(Rect bounds) {
        bounds.set(this.mBounds);
    }

    public final Rect copyBounds() {
        return new Rect(this.mBounds);
    }

    public final Rect getBounds() {
        if (this.mBounds == ZERO_BOUNDS_RECT) {
            this.mBounds = new Rect();
        }
        return this.mBounds;
    }

    public Rect getDirtyBounds() {
        return getBounds();
    }

    public void setChangingConfigurations(int configs) {
        this.mChangingConfigurations = configs;
    }

    public int getChangingConfigurations() {
        return this.mChangingConfigurations;
    }

    public void setDither(boolean dither) {
    }

    public void setFilterBitmap(boolean filter) {
    }

    public final void setCallback(Callback cb) {
        this.mCallback = new WeakReference(cb);
    }

    public Callback getCallback() {
        if (this.mCallback != null) {
            return (Callback) this.mCallback.get();
        }
        return null;
    }

    public void invalidateSelf() {
        Callback callback = getCallback();
        if (callback != null) {
            callback.invalidateDrawable(this);
        }
    }

    public void scheduleSelf(Runnable what, long when) {
        Callback callback = getCallback();
        if (callback != null) {
            callback.scheduleDrawable(this, what, when);
        }
    }

    public void unscheduleSelf(Runnable what) {
        Callback callback = getCallback();
        if (callback != null) {
            callback.unscheduleDrawable(this, what);
        }
    }

    public int getLayoutDirection() {
        return this.mLayoutDirection;
    }

    public void setLayoutDirection(int layoutDirection) {
        if (getLayoutDirection() != layoutDirection) {
            this.mLayoutDirection = layoutDirection;
        }
    }

    public int getAlpha() {
        return EditorInfo.IME_MASK_ACTION;
    }

    public void setXfermode(Xfermode mode) {
    }

    public void setColorFilter(int color, Mode mode) {
        setColorFilter(new PorterDuffColorFilter(color, mode));
    }

    public void setTint(int tint) {
        setTintList(ColorStateList.valueOf(tint));
    }

    public void setTintList(ColorStateList tint) {
    }

    public void setTintMode(Mode tintMode) {
    }

    public ColorFilter getColorFilter() {
        return null;
    }

    public void clearColorFilter() {
        setColorFilter(null);
    }

    public void setHotspot(float x, float y) {
    }

    public void setHotspotBounds(int left, int top, int right, int bottom) {
    }

    public void getHotspotBounds(Rect outRect) {
        outRect.set(getBounds());
    }

    public boolean isProjected() {
        return false;
    }

    public boolean isStateful() {
        return false;
    }

    public boolean setState(int[] stateSet) {
        if (Arrays.equals(this.mStateSet, stateSet)) {
            return false;
        }
        this.mStateSet = stateSet;
        return onStateChange(stateSet);
    }

    public int[] getState() {
        return this.mStateSet;
    }

    public void jumpToCurrentState() {
    }

    public Drawable getCurrent() {
        return this;
    }

    public final boolean setLevel(int level) {
        if (this.mLevel == level) {
            return false;
        }
        this.mLevel = level;
        return onLevelChange(level);
    }

    public final int getLevel() {
        return this.mLevel;
    }

    public boolean setVisible(boolean visible, boolean restart) {
        boolean changed = this.mVisible != visible;
        if (changed) {
            this.mVisible = visible;
            invalidateSelf();
        }
        return changed;
    }

    public final boolean isVisible() {
        return this.mVisible;
    }

    public void setAutoMirrored(boolean mirrored) {
    }

    public boolean isAutoMirrored() {
        return false;
    }

    public void applyTheme(Theme t) {
    }

    public boolean canApplyTheme() {
        return false;
    }

    public static int resolveOpacity(int op1, int op2) {
        if (op1 == op2) {
            return op1;
        }
        if (op1 == 0 || op2 == 0) {
            return 0;
        }
        if (op1 == -3 || op2 == -3) {
            return -3;
        }
        if (op1 == -2 || op2 == -2) {
            return -2;
        }
        return -1;
    }

    public Region getTransparentRegion() {
        return null;
    }

    protected boolean onStateChange(int[] state) {
        return false;
    }

    protected boolean onLevelChange(int level) {
        return false;
    }

    protected void onBoundsChange(Rect bounds) {
    }

    public int getIntrinsicWidth() {
        return -1;
    }

    public int getIntrinsicHeight() {
        return -1;
    }

    public int getMinimumWidth() {
        int intrinsicWidth = getIntrinsicWidth();
        return intrinsicWidth > 0 ? intrinsicWidth : 0;
    }

    public int getMinimumHeight() {
        int intrinsicHeight = getIntrinsicHeight();
        return intrinsicHeight > 0 ? intrinsicHeight : 0;
    }

    public boolean getPadding(Rect padding) {
        padding.set(0, 0, 0, 0);
        return false;
    }

    public Insets getOpticalInsets() {
        return Insets.NONE;
    }

    public void getOutline(Outline outline) {
        outline.setRect(getBounds());
        outline.setAlpha(0.0f);
    }

    public Drawable mutate() {
        return this;
    }

    public void clearMutated() {
    }

    public static Drawable createFromStream(InputStream is, String srcName) {
        Trace.traceBegin(Trace.TRACE_TAG_RESOURCES, srcName != null ? srcName : "Unknown drawable");
        try {
            Drawable createFromResourceStream = createFromResourceStream(null, null, is, srcName);
            return createFromResourceStream;
        } finally {
            Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
        }
    }

    public static Drawable createFromResourceStream(Resources res, TypedValue value, InputStream is, String srcName) {
        Trace.traceBegin(Trace.TRACE_TAG_RESOURCES, srcName != null ? srcName : "Unknown drawable");
        try {
            Drawable createFromResourceStream = createFromResourceStream(res, value, is, srcName, null);
            return createFromResourceStream;
        } finally {
            Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
        }
    }

    public static Drawable createFromResourceStream(Resources res, TypedValue value, InputStream is, String srcName, Options opts) {
        if (is == null) {
            return null;
        }
        Rect pad = new Rect();
        if (opts == null) {
            opts = new Options();
        }
        opts.inScreenDensity = res != null ? res.getDisplayMetrics().noncompatDensityDpi : DisplayMetrics.DENSITY_DEVICE;
        Bitmap bm = BitmapFactory.decodeResourceStream(res, value, is, pad, opts);
        if (bm == null) {
            return null;
        }
        byte[] np = bm.getNinePatchChunk();
        if (np == null || !NinePatch.isNinePatchChunk(np)) {
            np = null;
            pad = null;
        }
        Rect opticalInsets = new Rect();
        bm.getOpticalInsets(opticalInsets);
        return drawableFromBitmap(res, bm, np, pad, opticalInsets, srcName);
    }

    public static Drawable createFromXml(Resources r, XmlPullParser parser) throws XmlPullParserException, IOException {
        return createFromXml(r, parser, null);
    }

    public static Drawable createFromXml(Resources r, XmlPullParser parser, Theme theme) throws XmlPullParserException, IOException {
        int type;
        AttributeSet attrs = Xml.asAttributeSet(parser);
        do {
            type = parser.next();
            if (type == 2) {
                break;
            }
        } while (type != 1);
        if (type != 2) {
            throw new XmlPullParserException("No start tag found");
        }
        Drawable drawable = createFromXmlInner(r, parser, attrs, theme);
        if (drawable != null) {
            return drawable;
        }
        throw new RuntimeException("Unknown initial tag: " + parser.getName());
    }

    public static Drawable createFromXmlInner(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        return createFromXmlInner(r, parser, attrs, null);
    }

    public static Drawable createFromXmlInner(Resources r, XmlPullParser parser, AttributeSet attrs, Theme theme) throws XmlPullParserException, IOException {
        Drawable drawable;
        String name = parser.getName();
        Object obj = -1;
        switch (name.hashCode()) {
            case -1724158635:
                if (name.equals("transition")) {
                    obj = 4;
                    break;
                }
                break;
            case -1671889043:
                if (name.equals("nine-patch")) {
                    obj = 17;
                    break;
                }
                break;
            case -1493546681:
                if (name.equals("animation-list")) {
                    obj = 14;
                    break;
                }
                break;
            case -1388777169:
                if (name.equals("bitmap")) {
                    obj = 16;
                    break;
                }
                break;
            case -930826704:
                if (name.equals("ripple")) {
                    obj = 5;
                    break;
                }
                break;
            case -925180581:
                if (name.equals("rotate")) {
                    obj = 12;
                    break;
                }
                break;
            case -820387517:
                if (name.equals("vector")) {
                    obj = 8;
                    break;
                }
                break;
            case -510364471:
                if (name.equals("animated-selector")) {
                    obj = 1;
                    break;
                }
                break;
            case -94197862:
                if (name.equals("layer-list")) {
                    obj = 3;
                    break;
                }
                break;
            case 3056464:
                if (name.equals("clip")) {
                    obj = 11;
                    break;
                }
                break;
            case 94842723:
                if (name.equals(SubscriptionManager.COLOR)) {
                    obj = 6;
                    break;
                }
                break;
            case 100360477:
                if (name.equals("inset")) {
                    obj = 15;
                    break;
                }
                break;
            case 109250890:
                if (name.equals(BatteryManager.EXTRA_SCALE)) {
                    obj = 10;
                    break;
                }
                break;
            case 109399969:
                if (name.equals("shape")) {
                    obj = 7;
                    break;
                }
                break;
            case 160680263:
                if (name.equals("level-list")) {
                    obj = 2;
                    break;
                }
                break;
            case 1191572447:
                if (name.equals("selector")) {
                    obj = null;
                    break;
                }
                break;
            case 2013827269:
                if (name.equals("animated-rotate")) {
                    obj = 13;
                    break;
                }
                break;
            case 2118620333:
                if (name.equals("animated-vector")) {
                    obj = 9;
                    break;
                }
                break;
        }
        switch (obj) {
            case Toast.LENGTH_SHORT /*0*/:
                drawable = new StateListDrawable();
                break;
            case Toast.LENGTH_LONG /*1*/:
                drawable = new AnimatedStateListDrawable();
                break;
            case Action.MERGE_IGNORE /*2*/:
                drawable = new LevelListDrawable();
                break;
            case SetDrawableParameters.TAG /*3*/:
                drawable = new LayerDrawable();
                break;
            case ViewGroupAction.TAG /*4*/:
                drawable = new TransitionDrawable();
                break;
            case ReflectionActionWithoutParams.TAG /*5*/:
                drawable = new RippleDrawable();
                break;
            case SetEmptyView.TAG /*6*/:
                drawable = new ColorDrawable();
                break;
            case SpellChecker.AVERAGE_WORD_LENGTH /*7*/:
                drawable = new GradientDrawable();
                break;
            case SetPendingIntentTemplate.TAG /*8*/:
                drawable = new VectorDrawable();
                break;
            case SetOnClickFillInIntent.TAG /*9*/:
                drawable = new AnimatedVectorDrawable();
                break;
            case SetRemoteViewsAdapterIntent.TAG /*10*/:
                drawable = new ScaleDrawable();
                break;
            case TextViewDrawableAction.TAG /*11*/:
                drawable = new ClipDrawable();
                break;
            case BitmapReflectionAction.TAG /*12*/:
                drawable = new RotateDrawable();
                break;
            case TextViewSizeAction.TAG /*13*/:
                drawable = new AnimatedRotateDrawable();
                break;
            case ViewPaddingAction.TAG /*14*/:
                drawable = new AnimationDrawable();
                break;
            case SetRemoteViewsAdapterList.TAG /*15*/:
                drawable = new InsetDrawable();
                break;
            case RelativeLayout.START_OF /*16*/:
                drawable = new BitmapDrawable(r);
                if (r != null) {
                    ((BitmapDrawable) drawable).setTargetDensity(r.getDisplayMetrics());
                    break;
                }
                break;
            case TextViewDrawableColorFilterAction.TAG /*17*/:
                drawable = new NinePatchDrawable();
                if (r != null) {
                    ((NinePatchDrawable) drawable).setTargetDensity(r.getDisplayMetrics());
                    break;
                }
                break;
            default:
                throw new XmlPullParserException(parser.getPositionDescription() + ": invalid drawable tag " + name);
        }
        drawable.inflate(r, parser, attrs, theme);
        return drawable;
    }

    public static Drawable createFromPath(String pathName) {
        Drawable drawable = null;
        if (pathName != null) {
            Trace.traceBegin(Trace.TRACE_TAG_RESOURCES, pathName);
            try {
                Bitmap bm = BitmapFactory.decodeFile(pathName);
                if (bm != null) {
                    drawable = drawableFromBitmap(null, bm, null, null, null, pathName);
                } else {
                    Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
                }
            } finally {
                Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
            }
        }
        return drawable;
    }

    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        inflate(r, parser, attrs, null);
    }

    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Theme theme) throws XmlPullParserException, IOException {
        TypedArray a;
        if (theme != null) {
            a = theme.obtainStyledAttributes(attrs, R.styleable.Drawable, 0, 0);
        } else {
            a = r.obtainAttributes(attrs, R.styleable.Drawable);
        }
        inflateWithAttributes(r, parser, a, 0);
        a.recycle();
    }

    void inflateWithAttributes(Resources r, XmlPullParser parser, TypedArray attrs, int visibleAttr) throws XmlPullParserException, IOException {
        this.mVisible = attrs.getBoolean(visibleAttr, this.mVisible);
    }

    public ConstantState getConstantState() {
        return null;
    }

    private static Drawable drawableFromBitmap(Resources res, Bitmap bm, byte[] np, Rect pad, Rect layoutBounds, String srcName) {
        if (np != null) {
            return new NinePatchDrawable(res, bm, np, pad, layoutBounds, srcName);
        }
        return new BitmapDrawable(res, bm);
    }

    PorterDuffColorFilter updateTintFilter(PorterDuffColorFilter tintFilter, ColorStateList tint, Mode tintMode) {
        if (tint == null || tintMode == null) {
            return null;
        }
        int color = tint.getColorForState(getState(), 0);
        if (tintFilter == null) {
            return new PorterDuffColorFilter(color, tintMode);
        }
        tintFilter.setColor(color);
        tintFilter.setMode(tintMode);
        return tintFilter;
    }

    static TypedArray obtainAttributes(Resources res, Theme theme, AttributeSet set, int[] attrs) {
        if (theme == null) {
            return res.obtainAttributes(set, attrs);
        }
        return theme.obtainStyledAttributes(set, attrs, 0, 0);
    }

    public static Mode parseTintMode(int value, Mode defaultMode) {
        switch (value) {
            case SetDrawableParameters.TAG /*3*/:
                return Mode.SRC_OVER;
            case ReflectionActionWithoutParams.TAG /*5*/:
                return Mode.SRC_IN;
            case SetOnClickFillInIntent.TAG /*9*/:
                return Mode.SRC_ATOP;
            case ViewPaddingAction.TAG /*14*/:
                return Mode.MULTIPLY;
            case SetRemoteViewsAdapterList.TAG /*15*/:
                return Mode.SCREEN;
            case RelativeLayout.START_OF /*16*/:
                return Mode.ADD;
            default:
                return defaultMode;
        }
    }
}
