package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.R;
import java.util.regex.Pattern;

public class TableLayout extends LinearLayout {
    private SparseBooleanArray mCollapsedColumns;
    private boolean mInitialized;
    private int[] mMaxWidths;
    private PassThroughHierarchyChangeListener mPassThroughListener;
    private boolean mShrinkAllColumns;
    private SparseBooleanArray mShrinkableColumns;
    private boolean mStretchAllColumns;
    private SparseBooleanArray mStretchableColumns;

    public static class LayoutParams extends android.widget.LinearLayout.LayoutParams {
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int w, int h) {
            super(-1, h);
        }

        public LayoutParams(int w, int h, float initWeight) {
            super(-1, h, initWeight);
        }

        public LayoutParams() {
            super(-1, -2);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        protected void setBaseAttributes(TypedArray a, int widthAttr, int heightAttr) {
            this.width = -1;
            if (a.hasValue(heightAttr)) {
                this.height = a.getLayoutDimension(heightAttr, "layout_height");
            } else {
                this.height = -2;
            }
        }
    }

    private class PassThroughHierarchyChangeListener implements OnHierarchyChangeListener {
        private OnHierarchyChangeListener mOnHierarchyChangeListener;

        private PassThroughHierarchyChangeListener() {
        }

        public void onChildViewAdded(View parent, View child) {
            TableLayout.this.trackCollapsedColumns(child);
            if (this.mOnHierarchyChangeListener != null) {
                this.mOnHierarchyChangeListener.onChildViewAdded(parent, child);
            }
        }

        public void onChildViewRemoved(View parent, View child) {
            if (this.mOnHierarchyChangeListener != null) {
                this.mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
            }
        }
    }

    public TableLayout(Context context) {
        super(context);
        initTableLayout();
    }

    public TableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TableLayout);
        String stretchedColumns = a.getString(0);
        if (stretchedColumns != null) {
            if (stretchedColumns.charAt(0) == '*') {
                this.mStretchAllColumns = true;
            } else {
                this.mStretchableColumns = parseColumns(stretchedColumns);
            }
        }
        String shrinkedColumns = a.getString(1);
        if (shrinkedColumns != null) {
            if (shrinkedColumns.charAt(0) == '*') {
                this.mShrinkAllColumns = true;
            } else {
                this.mShrinkableColumns = parseColumns(shrinkedColumns);
            }
        }
        String collapsedColumns = a.getString(2);
        if (collapsedColumns != null) {
            this.mCollapsedColumns = parseColumns(collapsedColumns);
        }
        a.recycle();
        initTableLayout();
    }

    private static SparseBooleanArray parseColumns(String sequence) {
        SparseBooleanArray columns = new SparseBooleanArray();
        for (String columnIdentifier : Pattern.compile("\\s*,\\s*").split(sequence)) {
            try {
                int columnIndex = Integer.parseInt(columnIdentifier);
                if (columnIndex >= 0) {
                    columns.put(columnIndex, true);
                }
            } catch (NumberFormatException e) {
            }
        }
        return columns;
    }

    private void initTableLayout() {
        if (this.mCollapsedColumns == null) {
            this.mCollapsedColumns = new SparseBooleanArray();
        }
        if (this.mStretchableColumns == null) {
            this.mStretchableColumns = new SparseBooleanArray();
        }
        if (this.mShrinkableColumns == null) {
            this.mShrinkableColumns = new SparseBooleanArray();
        }
        setOrientation(1);
        this.mPassThroughListener = new PassThroughHierarchyChangeListener();
        super.setOnHierarchyChangeListener(this.mPassThroughListener);
        this.mInitialized = true;
    }

    public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
        this.mPassThroughListener.mOnHierarchyChangeListener = listener;
    }

    private void requestRowsLayout() {
        if (this.mInitialized) {
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                getChildAt(i).requestLayout();
            }
        }
    }

    public void requestLayout() {
        if (this.mInitialized) {
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                getChildAt(i).forceLayout();
            }
        }
        super.requestLayout();
    }

    public boolean isShrinkAllColumns() {
        return this.mShrinkAllColumns;
    }

    public void setShrinkAllColumns(boolean shrinkAllColumns) {
        this.mShrinkAllColumns = shrinkAllColumns;
    }

    public boolean isStretchAllColumns() {
        return this.mStretchAllColumns;
    }

    public void setStretchAllColumns(boolean stretchAllColumns) {
        this.mStretchAllColumns = stretchAllColumns;
    }

    public void setColumnCollapsed(int columnIndex, boolean isCollapsed) {
        this.mCollapsedColumns.put(columnIndex, isCollapsed);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (view instanceof TableRow) {
                ((TableRow) view).setColumnCollapsed(columnIndex, isCollapsed);
            }
        }
        requestRowsLayout();
    }

    public boolean isColumnCollapsed(int columnIndex) {
        return this.mCollapsedColumns.get(columnIndex);
    }

    public void setColumnStretchable(int columnIndex, boolean isStretchable) {
        this.mStretchableColumns.put(columnIndex, isStretchable);
        requestRowsLayout();
    }

    public boolean isColumnStretchable(int columnIndex) {
        return this.mStretchAllColumns || this.mStretchableColumns.get(columnIndex);
    }

    public void setColumnShrinkable(int columnIndex, boolean isShrinkable) {
        this.mShrinkableColumns.put(columnIndex, isShrinkable);
        requestRowsLayout();
    }

    public boolean isColumnShrinkable(int columnIndex) {
        return this.mShrinkAllColumns || this.mShrinkableColumns.get(columnIndex);
    }

    private void trackCollapsedColumns(View child) {
        if (child instanceof TableRow) {
            TableRow row = (TableRow) child;
            SparseBooleanArray collapsedColumns = this.mCollapsedColumns;
            int count = collapsedColumns.size();
            for (int i = 0; i < count; i++) {
                int columnIndex = collapsedColumns.keyAt(i);
                boolean isCollapsed = collapsedColumns.valueAt(i);
                if (isCollapsed) {
                    row.setColumnCollapsed(columnIndex, isCollapsed);
                }
            }
        }
    }

    public void addView(View child) {
        super.addView(child);
        requestRowsLayout();
    }

    public void addView(View child, int index) {
        super.addView(child, index);
        requestRowsLayout();
    }

    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        requestRowsLayout();
    }

    public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        requestRowsLayout();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureVertical(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutVertical(l, t, r, b);
    }

    void measureChildBeforeLayout(View child, int childIndex, int widthMeasureSpec, int totalWidth, int heightMeasureSpec, int totalHeight) {
        if (child instanceof TableRow) {
            ((TableRow) child).setColumnsWidthConstraints(this.mMaxWidths);
        }
        super.measureChildBeforeLayout(child, childIndex, widthMeasureSpec, totalWidth, heightMeasureSpec, totalHeight);
    }

    void measureVertical(int widthMeasureSpec, int heightMeasureSpec) {
        findLargestCells(widthMeasureSpec);
        shrinkAndStretchColumns(widthMeasureSpec);
        super.measureVertical(widthMeasureSpec, heightMeasureSpec);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void findLargestCells(int r22) {
        /*
        r21 = this;
        r7 = 1;
        r5 = r21.getChildCount();
        r8 = 0;
    L_0x0006:
        if (r8 >= r5) goto L_0x00d5;
    L_0x0008:
        r0 = r21;
        r4 = r0.getChildAt(r8);
        r17 = r4.getVisibility();
        r18 = 8;
        r0 = r17;
        r1 = r18;
        if (r0 != r1) goto L_0x001d;
    L_0x001a:
        r8 = r8 + 1;
        goto L_0x0006;
    L_0x001d:
        r0 = r4 instanceof android.widget.TableRow;
        r17 = r0;
        if (r17 == 0) goto L_0x001a;
    L_0x0023:
        r15 = r4;
        r15 = (android.widget.TableRow) r15;
        r10 = r15.getLayoutParams();
        r17 = -2;
        r0 = r17;
        r10.height = r0;
        r0 = r22;
        r16 = r15.getColumnsWidths(r0);
        r0 = r16;
        r13 = r0.length;
        if (r7 == 0) goto L_0x0073;
    L_0x003b:
        r0 = r21;
        r0 = r0.mMaxWidths;
        r17 = r0;
        if (r17 == 0) goto L_0x0052;
    L_0x0043:
        r0 = r21;
        r0 = r0.mMaxWidths;
        r17 = r0;
        r0 = r17;
        r0 = r0.length;
        r17 = r0;
        r0 = r17;
        if (r0 == r13) goto L_0x005c;
    L_0x0052:
        r0 = new int[r13];
        r17 = r0;
        r0 = r17;
        r1 = r21;
        r1.mMaxWidths = r0;
    L_0x005c:
        r17 = 0;
        r0 = r21;
        r0 = r0.mMaxWidths;
        r18 = r0;
        r19 = 0;
        r0 = r16;
        r1 = r17;
        r2 = r18;
        r3 = r19;
        java.lang.System.arraycopy(r0, r1, r2, r3, r13);
        r7 = 0;
        goto L_0x001a;
    L_0x0073:
        r0 = r21;
        r0 = r0.mMaxWidths;
        r17 = r0;
        r0 = r17;
        r11 = r0.length;
        r6 = r13 - r11;
        if (r6 <= 0) goto L_0x00bd;
    L_0x0080:
        r0 = r21;
        r14 = r0.mMaxWidths;
        r0 = new int[r13];
        r17 = r0;
        r0 = r17;
        r1 = r21;
        r1.mMaxWidths = r0;
        r17 = 0;
        r0 = r21;
        r0 = r0.mMaxWidths;
        r18 = r0;
        r19 = 0;
        r0 = r14.length;
        r20 = r0;
        r0 = r17;
        r1 = r18;
        r2 = r19;
        r3 = r20;
        java.lang.System.arraycopy(r14, r0, r1, r2, r3);
        r0 = r14.length;
        r17 = r0;
        r0 = r21;
        r0 = r0.mMaxWidths;
        r18 = r0;
        r0 = r14.length;
        r19 = r0;
        r0 = r16;
        r1 = r17;
        r2 = r18;
        r3 = r19;
        java.lang.System.arraycopy(r0, r1, r2, r3, r6);
    L_0x00bd:
        r0 = r21;
        r12 = r0.mMaxWidths;
        r11 = java.lang.Math.min(r11, r13);
        r9 = 0;
    L_0x00c6:
        if (r9 >= r11) goto L_0x001a;
    L_0x00c8:
        r17 = r12[r9];
        r18 = r16[r9];
        r17 = java.lang.Math.max(r17, r18);
        r12[r9] = r17;
        r9 = r9 + 1;
        goto L_0x00c6;
    L_0x00d5:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.TableLayout.findLargestCells(int):void");
    }

    private void shrinkAndStretchColumns(int widthMeasureSpec) {
        if (this.mMaxWidths != null) {
            int totalWidth = 0;
            for (int width : this.mMaxWidths) {
                totalWidth += width;
            }
            int size = (MeasureSpec.getSize(widthMeasureSpec) - this.mPaddingLeft) - this.mPaddingRight;
            if (totalWidth > size && (this.mShrinkAllColumns || this.mShrinkableColumns.size() > 0)) {
                mutateColumnsWidth(this.mShrinkableColumns, this.mShrinkAllColumns, size, totalWidth);
            } else if (totalWidth >= size) {
            } else {
                if (this.mStretchAllColumns || this.mStretchableColumns.size() > 0) {
                    mutateColumnsWidth(this.mStretchableColumns, this.mStretchAllColumns, size, totalWidth);
                }
            }
        }
    }

    private void mutateColumnsWidth(SparseBooleanArray columns, boolean allColumns, int size, int totalWidth) {
        int i;
        int skipped = 0;
        int[] maxWidths = this.mMaxWidths;
        int length = maxWidths.length;
        int count = allColumns ? length : columns.size();
        int extraSpace = (size - totalWidth) / count;
        int nbChildren = getChildCount();
        for (i = 0; i < nbChildren; i++) {
            View child = getChildAt(i);
            if (child instanceof TableRow) {
                child.forceLayout();
            }
        }
        if (allColumns) {
            for (i = 0; i < count; i++) {
                maxWidths[i] = maxWidths[i] + extraSpace;
            }
            return;
        }
        for (i = 0; i < count; i++) {
            int column = columns.keyAt(i);
            if (columns.valueAt(i)) {
                if (column < length) {
                    maxWidths[column] = maxWidths[column] + extraSpace;
                } else {
                    skipped++;
                }
            }
        }
        if (skipped > 0 && skipped < count) {
            extraSpace = (skipped * extraSpace) / (count - skipped);
            for (i = 0; i < count; i++) {
                column = columns.keyAt(i);
                if (columns.valueAt(i) && column < length) {
                    if (extraSpace > maxWidths[column]) {
                        maxWidths[column] = 0;
                    } else {
                        maxWidths[column] = maxWidths[column] + extraSpace;
                    }
                }
            }
        }
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    protected android.widget.LinearLayout.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    protected android.widget.LinearLayout.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(TableLayout.class.getName());
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(TableLayout.class.getName());
    }
}
