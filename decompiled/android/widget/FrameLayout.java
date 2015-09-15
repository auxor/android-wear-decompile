package android.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.EditorInfo;
import android.widget.RemoteViews.RemoteView;
import com.android.internal.R;
import java.util.ArrayList;

@RemoteView
public class FrameLayout extends ViewGroup {
    private static final int DEFAULT_CHILD_GRAVITY = 8388659;
    @ExportedProperty(category = "drawing")
    private Drawable mForeground;
    boolean mForegroundBoundsChanged;
    @ExportedProperty(category = "drawing")
    private int mForegroundGravity;
    @ExportedProperty(category = "drawing")
    protected boolean mForegroundInPadding;
    @ExportedProperty(category = "padding")
    private int mForegroundPaddingBottom;
    @ExportedProperty(category = "padding")
    private int mForegroundPaddingLeft;
    @ExportedProperty(category = "padding")
    private int mForegroundPaddingRight;
    @ExportedProperty(category = "padding")
    private int mForegroundPaddingTop;
    private ColorStateList mForegroundTintList;
    private Mode mForegroundTintMode;
    private boolean mHasForegroundTint;
    private boolean mHasForegroundTintMode;
    private final ArrayList<View> mMatchParentChildren;
    @ExportedProperty(category = "measurement")
    boolean mMeasureAllChildren;
    private final Rect mOverlayBounds;
    private final Rect mSelfBounds;

    public static class LayoutParams extends MarginLayoutParams {
        public int gravity;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.gravity = -1;
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.FrameLayout_Layout);
            this.gravity = a.getInt(0, -1);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.gravity = -1;
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height);
            this.gravity = -1;
            this.gravity = gravity;
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams source) {
            super(source);
            this.gravity = -1;
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
            this.gravity = -1;
        }

        public LayoutParams(LayoutParams source) {
            super((MarginLayoutParams) source);
            this.gravity = -1;
            this.gravity = source.gravity;
        }
    }

    public FrameLayout(Context context) {
        super(context);
        this.mMeasureAllChildren = false;
        this.mForegroundTintList = null;
        this.mForegroundTintMode = null;
        this.mHasForegroundTint = false;
        this.mHasForegroundTintMode = false;
        this.mForegroundPaddingLeft = 0;
        this.mForegroundPaddingTop = 0;
        this.mForegroundPaddingRight = 0;
        this.mForegroundPaddingBottom = 0;
        this.mSelfBounds = new Rect();
        this.mOverlayBounds = new Rect();
        this.mForegroundGravity = KeyEvent.KEYCODE_FUNCTION;
        this.mForegroundInPadding = true;
        this.mForegroundBoundsChanged = false;
        this.mMatchParentChildren = new ArrayList(1);
    }

    public FrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public FrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mMeasureAllChildren = false;
        this.mForegroundTintList = null;
        this.mForegroundTintMode = null;
        this.mHasForegroundTint = false;
        this.mHasForegroundTintMode = false;
        this.mForegroundPaddingLeft = 0;
        this.mForegroundPaddingTop = 0;
        this.mForegroundPaddingRight = 0;
        this.mForegroundPaddingBottom = 0;
        this.mSelfBounds = new Rect();
        this.mOverlayBounds = new Rect();
        this.mForegroundGravity = KeyEvent.KEYCODE_FUNCTION;
        this.mForegroundInPadding = true;
        this.mForegroundBoundsChanged = false;
        this.mMatchParentChildren = new ArrayList(1);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FrameLayout, defStyleAttr, defStyleRes);
        this.mForegroundGravity = a.getInt(2, this.mForegroundGravity);
        Drawable d = a.getDrawable(0);
        if (d != null) {
            setForeground(d);
        }
        if (a.getBoolean(1, false)) {
            setMeasureAllChildren(true);
        }
        if (a.hasValue(4)) {
            this.mForegroundTintMode = Drawable.parseTintMode(a.getInt(4, -1), this.mForegroundTintMode);
            this.mHasForegroundTintMode = true;
        }
        if (a.hasValue(3)) {
            this.mForegroundTintList = a.getColorStateList(3);
            this.mHasForegroundTint = true;
        }
        this.mForegroundInPadding = a.getBoolean(5, true);
        a.recycle();
        applyForegroundTint();
    }

    public int getForegroundGravity() {
        return this.mForegroundGravity;
    }

    @RemotableViewMethod
    public void setForegroundGravity(int foregroundGravity) {
        if (this.mForegroundGravity != foregroundGravity) {
            if ((Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK & foregroundGravity) == 0) {
                foregroundGravity |= Gravity.START;
            }
            if ((foregroundGravity & KeyEvent.KEYCODE_FORWARD_DEL) == 0) {
                foregroundGravity |= 48;
            }
            this.mForegroundGravity = foregroundGravity;
            if (this.mForegroundGravity != KeyEvent.KEYCODE_FUNCTION || this.mForeground == null) {
                this.mForegroundPaddingLeft = 0;
                this.mForegroundPaddingTop = 0;
                this.mForegroundPaddingRight = 0;
                this.mForegroundPaddingBottom = 0;
            } else {
                Rect padding = new Rect();
                if (this.mForeground.getPadding(padding)) {
                    this.mForegroundPaddingLeft = padding.left;
                    this.mForegroundPaddingTop = padding.top;
                    this.mForegroundPaddingRight = padding.right;
                    this.mForegroundPaddingBottom = padding.bottom;
                }
            }
            requestLayout();
        }
    }

    @RemotableViewMethod
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (this.mForeground != null) {
            boolean z;
            Drawable drawable = this.mForeground;
            if (visibility == 0) {
                z = true;
            } else {
                z = false;
            }
            drawable.setVisible(z, false);
        }
    }

    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == this.mForeground;
    }

    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (this.mForeground != null) {
            this.mForeground.jumpToCurrentState();
        }
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mForeground != null && this.mForeground.isStateful()) {
            this.mForeground.setState(getDrawableState());
        }
    }

    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
        if (this.mForeground != null) {
            this.mForeground.setHotspot(x, y);
        }
    }

    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-1, -1);
    }

    public void setForeground(Drawable d) {
        if (this.mForeground != d) {
            if (this.mForeground != null) {
                this.mForeground.setCallback(null);
                unscheduleDrawable(this.mForeground);
            }
            this.mForeground = d;
            this.mForegroundPaddingLeft = 0;
            this.mForegroundPaddingTop = 0;
            this.mForegroundPaddingRight = 0;
            this.mForegroundPaddingBottom = 0;
            if (d != null) {
                setWillNotDraw(false);
                d.setCallback(this);
                d.setLayoutDirection(getLayoutDirection());
                if (d.isStateful()) {
                    d.setState(getDrawableState());
                }
                applyForegroundTint();
                if (this.mForegroundGravity == KeyEvent.KEYCODE_FUNCTION) {
                    Rect padding = new Rect();
                    if (d.getPadding(padding)) {
                        this.mForegroundPaddingLeft = padding.left;
                        this.mForegroundPaddingTop = padding.top;
                        this.mForegroundPaddingRight = padding.right;
                        this.mForegroundPaddingBottom = padding.bottom;
                    }
                }
            } else {
                setWillNotDraw(true);
            }
            requestLayout();
            invalidate();
        }
    }

    public Drawable getForeground() {
        return this.mForeground;
    }

    public void setForegroundTintList(ColorStateList tint) {
        this.mForegroundTintList = tint;
        this.mHasForegroundTint = true;
        applyForegroundTint();
    }

    public ColorStateList getForegroundTintList() {
        return this.mForegroundTintList;
    }

    public void setForegroundTintMode(Mode tintMode) {
        this.mForegroundTintMode = tintMode;
        this.mHasForegroundTintMode = true;
        applyForegroundTint();
    }

    public Mode getForegroundTintMode() {
        return this.mForegroundTintMode;
    }

    private void applyForegroundTint() {
        if (this.mForeground == null) {
            return;
        }
        if (this.mHasForegroundTint || this.mHasForegroundTintMode) {
            this.mForeground = this.mForeground.mutate();
            if (this.mHasForegroundTint) {
                this.mForeground.setTintList(this.mForegroundTintList);
            }
            if (this.mHasForegroundTintMode) {
                this.mForeground.setTintMode(this.mForegroundTintMode);
            }
            if (this.mForeground.isStateful()) {
                this.mForeground.setState(getDrawableState());
            }
        }
    }

    int getPaddingLeftWithForeground() {
        return this.mForegroundInPadding ? Math.max(this.mPaddingLeft, this.mForegroundPaddingLeft) : this.mPaddingLeft + this.mForegroundPaddingLeft;
    }

    int getPaddingRightWithForeground() {
        return this.mForegroundInPadding ? Math.max(this.mPaddingRight, this.mForegroundPaddingRight) : this.mPaddingRight + this.mForegroundPaddingRight;
    }

    private int getPaddingTopWithForeground() {
        return this.mForegroundInPadding ? Math.max(this.mPaddingTop, this.mForegroundPaddingTop) : this.mPaddingTop + this.mForegroundPaddingTop;
    }

    private int getPaddingBottomWithForeground() {
        return this.mForegroundInPadding ? Math.max(this.mPaddingBottom, this.mForegroundPaddingBottom) : this.mPaddingBottom + this.mForegroundPaddingBottom;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i;
        int count = getChildCount();
        boolean measureMatchParentChildren = (MeasureSpec.getMode(widthMeasureSpec) == EditorInfo.IME_FLAG_NO_ENTER_ACTION && MeasureSpec.getMode(heightMeasureSpec) == EditorInfo.IME_FLAG_NO_ENTER_ACTION) ? false : true;
        this.mMatchParentChildren.clear();
        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;
        for (i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (this.mMeasureAllChildren || child.getVisibility() != 8) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                maxWidth = Math.max(maxWidth, (child.getMeasuredWidth() + lp.leftMargin) + lp.rightMargin);
                maxHeight = Math.max(maxHeight, (child.getMeasuredHeight() + lp.topMargin) + lp.bottomMargin);
                childState = View.combineMeasuredStates(childState, child.getMeasuredState());
                if (measureMatchParentChildren && (lp.width == -1 || lp.height == -1)) {
                    this.mMatchParentChildren.add(child);
                }
            }
        }
        maxWidth += getPaddingLeftWithForeground() + getPaddingRightWithForeground();
        maxHeight = Math.max(maxHeight + (getPaddingTopWithForeground() + getPaddingBottomWithForeground()), getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());
        Drawable drawable = getForeground();
        if (drawable != null) {
            maxHeight = Math.max(maxHeight, drawable.getMinimumHeight());
            maxWidth = Math.max(maxWidth, drawable.getMinimumWidth());
        }
        setMeasuredDimension(View.resolveSizeAndState(maxWidth, widthMeasureSpec, childState), View.resolveSizeAndState(maxHeight, heightMeasureSpec, childState << 16));
        count = this.mMatchParentChildren.size();
        if (count > 1) {
            for (i = 0; i < count; i++) {
                int childWidthMeasureSpec;
                int childHeightMeasureSpec;
                child = (View) this.mMatchParentChildren.get(i);
                MarginLayoutParams lp2 = (MarginLayoutParams) child.getLayoutParams();
                if (lp2.width == -1) {
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec((((getMeasuredWidth() - getPaddingLeftWithForeground()) - getPaddingRightWithForeground()) - lp2.leftMargin) - lp2.rightMargin, EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                } else {
                    childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, ((getPaddingLeftWithForeground() + getPaddingRightWithForeground()) + lp2.leftMargin) + lp2.rightMargin, lp2.width);
                }
                if (lp2.height == -1) {
                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec((((getMeasuredHeight() - getPaddingTopWithForeground()) - getPaddingBottomWithForeground()) - lp2.topMargin) - lp2.bottomMargin, EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                } else {
                    childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(heightMeasureSpec, ((getPaddingTopWithForeground() + getPaddingBottomWithForeground()) + lp2.topMargin) + lp2.bottomMargin, lp2.height);
                }
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        layoutChildren(left, top, right, bottom, false);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void layoutChildren(int r21, int r22, int r23, int r24, boolean r25) {
        /*
        r20 = this;
        r6 = r20.getChildCount();
        r13 = r20.getPaddingLeftWithForeground();
        r18 = r23 - r21;
        r19 = r20.getPaddingRightWithForeground();
        r14 = r18 - r19;
        r15 = r20.getPaddingTopWithForeground();
        r18 = r24 - r22;
        r19 = r20.getPaddingBottomWithForeground();
        r12 = r18 - r19;
        r18 = 1;
        r0 = r18;
        r1 = r20;
        r1.mForegroundBoundsChanged = r0;
        r9 = 0;
    L_0x0025:
        if (r9 >= r6) goto L_0x00c3;
    L_0x0027:
        r0 = r20;
        r3 = r0.getChildAt(r9);
        r18 = r3.getVisibility();
        r19 = 8;
        r0 = r18;
        r1 = r19;
        if (r0 == r1) goto L_0x007b;
    L_0x0039:
        r11 = r3.getLayoutParams();
        r11 = (android.widget.FrameLayout.LayoutParams) r11;
        r17 = r3.getMeasuredWidth();
        r8 = r3.getMeasuredHeight();
        r7 = r11.gravity;
        r18 = -1;
        r0 = r18;
        if (r7 != r0) goto L_0x0052;
    L_0x004f:
        r7 = 8388659; // 0x800033 float:1.1755015E-38 double:4.144548E-317;
    L_0x0052:
        r10 = r20.getLayoutDirection();
        r2 = android.view.Gravity.getAbsoluteGravity(r7, r10);
        r16 = r7 & 112;
        r18 = r2 & 7;
        switch(r18) {
            case 1: goto L_0x007e;
            case 5: goto L_0x0093;
            default: goto L_0x0061;
        };
    L_0x0061:
        r0 = r11.leftMargin;
        r18 = r0;
        r4 = r13 + r18;
    L_0x0067:
        switch(r16) {
            case 16: goto L_0x00a5;
            case 48: goto L_0x009e;
            case 80: goto L_0x00ba;
            default: goto L_0x006a;
        };
    L_0x006a:
        r0 = r11.topMargin;
        r18 = r0;
        r5 = r15 + r18;
    L_0x0070:
        r18 = r4 + r17;
        r19 = r5 + r8;
        r0 = r18;
        r1 = r19;
        r3.layout(r4, r5, r0, r1);
    L_0x007b:
        r9 = r9 + 1;
        goto L_0x0025;
    L_0x007e:
        r18 = r14 - r13;
        r18 = r18 - r17;
        r18 = r18 / 2;
        r18 = r18 + r13;
        r0 = r11.leftMargin;
        r19 = r0;
        r18 = r18 + r19;
        r0 = r11.rightMargin;
        r19 = r0;
        r4 = r18 - r19;
        goto L_0x0067;
    L_0x0093:
        if (r25 != 0) goto L_0x0061;
    L_0x0095:
        r18 = r14 - r17;
        r0 = r11.rightMargin;
        r19 = r0;
        r4 = r18 - r19;
        goto L_0x0067;
    L_0x009e:
        r0 = r11.topMargin;
        r18 = r0;
        r5 = r15 + r18;
        goto L_0x0070;
    L_0x00a5:
        r18 = r12 - r15;
        r18 = r18 - r8;
        r18 = r18 / 2;
        r18 = r18 + r15;
        r0 = r11.topMargin;
        r19 = r0;
        r18 = r18 + r19;
        r0 = r11.bottomMargin;
        r19 = r0;
        r5 = r18 - r19;
        goto L_0x0070;
    L_0x00ba:
        r18 = r12 - r8;
        r0 = r11.bottomMargin;
        r19 = r0;
        r5 = r18 - r19;
        goto L_0x0070;
    L_0x00c3:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.FrameLayout.layoutChildren(int, int, int, int, boolean):void");
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mForegroundBoundsChanged = true;
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (this.mForeground != null) {
            Drawable foreground = this.mForeground;
            if (this.mForegroundBoundsChanged) {
                this.mForegroundBoundsChanged = false;
                Rect selfBounds = this.mSelfBounds;
                Rect overlayBounds = this.mOverlayBounds;
                int w = this.mRight - this.mLeft;
                int h = this.mBottom - this.mTop;
                if (this.mForegroundInPadding) {
                    selfBounds.set(0, 0, w, h);
                } else {
                    selfBounds.set(this.mPaddingLeft, this.mPaddingTop, w - this.mPaddingRight, h - this.mPaddingBottom);
                }
                Gravity.apply(this.mForegroundGravity, foreground.getIntrinsicWidth(), foreground.getIntrinsicHeight(), selfBounds, overlayBounds, getLayoutDirection());
                foreground.setBounds(overlayBounds);
            }
            foreground.draw(canvas);
        }
    }

    public boolean gatherTransparentRegion(Region region) {
        boolean opaque = super.gatherTransparentRegion(region);
        if (!(region == null || this.mForeground == null)) {
            applyDrawableToTransparentRegion(this.mForeground, region);
        }
        return opaque;
    }

    @RemotableViewMethod
    public void setMeasureAllChildren(boolean measureAll) {
        this.mMeasureAllChildren = measureAll;
    }

    @Deprecated
    public boolean getConsiderGoneChildrenWhenMeasuring() {
        return getMeasureAllChildren();
    }

    public boolean getMeasureAllChildren() {
        return this.mMeasureAllChildren;
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public boolean shouldDelayChildPressedState() {
        return false;
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    protected android.view.ViewGroup.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(FrameLayout.class.getName());
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(FrameLayout.class.getName());
    }
}
