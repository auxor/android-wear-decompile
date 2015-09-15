package android.widget;

import android.R;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.MathUtils;
import android.util.SparseBooleanArray;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.RemotableViewMethod;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.CollectionInfo;
import android.view.accessibility.AccessibilityNodeInfo.CollectionItemInfo;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView.LayoutParams;
import android.widget.RemoteViews.RemoteView;
import com.android.internal.util.Predicate;
import com.google.android.collect.Lists;
import java.util.ArrayList;

@RemoteView
public class ListView extends AbsListView {
    private static final float MAX_SCROLL_FACTOR = 0.33f;
    private static final int MIN_SCROLL_PREVIEW_PIXELS = 2;
    static final int NO_POSITION = -1;
    private boolean mAreAllItemsSelectable;
    private final ArrowScrollFocusResult mArrowScrollFocusResult;
    Drawable mDivider;
    int mDividerHeight;
    private boolean mDividerIsOpaque;
    private Paint mDividerPaint;
    private FocusSelector mFocusSelector;
    private boolean mFooterDividersEnabled;
    private ArrayList<FixedViewInfo> mFooterViewInfos;
    private boolean mHeaderDividersEnabled;
    private ArrayList<FixedViewInfo> mHeaderViewInfos;
    private boolean mIsCacheColorOpaque;
    private boolean mItemsCanFocus;
    Drawable mOverScrollFooter;
    Drawable mOverScrollHeader;
    private final Rect mTempRect;

    private static class ArrowScrollFocusResult {
        private int mAmountToScroll;
        private int mSelectedPosition;

        private ArrowScrollFocusResult() {
        }

        void populate(int selectedPosition, int amountToScroll) {
            this.mSelectedPosition = selectedPosition;
            this.mAmountToScroll = amountToScroll;
        }

        public int getSelectedPosition() {
            return this.mSelectedPosition;
        }

        public int getAmountToScroll() {
            return this.mAmountToScroll;
        }
    }

    public class FixedViewInfo {
        public Object data;
        public boolean isSelectable;
        public View view;
    }

    private class FocusSelector implements Runnable {
        private int mPosition;
        private int mPositionTop;
        final /* synthetic */ ListView this$0;

        private FocusSelector(android.widget.ListView r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.ListView.FocusSelector.<init>(android.widget.ListView):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.ListView.FocusSelector.<init>(android.widget.ListView):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.ListView.FocusSelector.<init>(android.widget.ListView):void");
        }

        /* synthetic */ FocusSelector(android.widget.ListView r1, android.widget.ListView.AnonymousClass1 r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.ListView.FocusSelector.<init>(android.widget.ListView, android.widget.ListView$1):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.ListView.FocusSelector.<init>(android.widget.ListView, android.widget.ListView$1):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 0073
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.ListView.FocusSelector.<init>(android.widget.ListView, android.widget.ListView$1):void");
        }

        public void run() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.ListView.FocusSelector.run():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.ListView.FocusSelector.run():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.ListView.FocusSelector.run():void");
        }

        public android.widget.ListView.FocusSelector setup(int r1, int r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.ListView.FocusSelector.setup(int, int):android.widget.ListView$FocusSelector
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.ListView.FocusSelector.setup(int, int):android.widget.ListView$FocusSelector
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e6
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.ListView.FocusSelector.setup(int, int):android.widget.ListView$FocusSelector");
        }
    }

    protected void layoutChildren() {
        /* JADX: method processing error */
/*
        Error: java.lang.OutOfMemoryError: Java heap space
	at java.util.Arrays.copyOf(Arrays.java:2219)
	at java.util.ArrayList.grow(ArrayList.java:242)
	at java.util.ArrayList.ensureExplicitCapacity(ArrayList.java:216)
	at java.util.ArrayList.ensureCapacityInternal(ArrayList.java:208)
	at java.util.ArrayList.add(ArrayList.java:440)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:447)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:448)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:448)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:448)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:448)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:448)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:448)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:448)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:448)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:448)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:448)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:448)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:448)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:448)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:448)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:448)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:448)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:448)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:448)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:448)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:448)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:448)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:448)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:448)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:448)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:448)
	at jadx.core.utils.BlockUtils.collectWhileDominates(BlockUtils.java:448)
*/
        /*
        r40 = this;
        r0 = r40;
        r13 = r0.mBlockLayoutRequests;
        if (r13 == 0) goto L_0x0007;
    L_0x0006:
        return;
    L_0x0007:
        r4 = 1;
        r0 = r40;
        r0.mBlockLayoutRequests = r4;
        super.layoutChildren();	 Catch:{ all -> 0x0127 }
        r40.invalidate();	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mAdapter;	 Catch:{ all -> 0x0127 }
        if (r4 != 0) goto L_0x0026;	 Catch:{ all -> 0x0127 }
    L_0x0018:
        r40.resetList();	 Catch:{ all -> 0x0127 }
        r40.invokeOnItemScrollListener();	 Catch:{ all -> 0x0127 }
        if (r13 != 0) goto L_0x0006;
    L_0x0020:
        r4 = 0;
        r0 = r40;
        r0.mBlockLayoutRequests = r4;
        goto L_0x0006;
    L_0x0026:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mListPadding;	 Catch:{ all -> 0x0127 }
        r8 = r4.top;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mBottom;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0 = r0.mTop;	 Catch:{ all -> 0x0127 }
        r37 = r0;	 Catch:{ all -> 0x0127 }
        r4 = r4 - r37;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0 = r0.mListPadding;	 Catch:{ all -> 0x0127 }
        r37 = r0;	 Catch:{ all -> 0x0127 }
        r0 = r37;	 Catch:{ all -> 0x0127 }
        r0 = r0.bottom;	 Catch:{ all -> 0x0127 }
        r37 = r0;	 Catch:{ all -> 0x0127 }
        r9 = r4 - r37;	 Catch:{ all -> 0x0127 }
        r15 = r40.getChildCount();	 Catch:{ all -> 0x0127 }
        r27 = 0;	 Catch:{ all -> 0x0127 }
        r7 = 0;	 Catch:{ all -> 0x0127 }
        r5 = 0;	 Catch:{ all -> 0x0127 }
        r29 = 0;	 Catch:{ all -> 0x0127 }
        r6 = 0;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mLayoutMode;	 Catch:{ all -> 0x0127 }
        switch(r4) {
            case 1: goto L_0x0093;
            case 2: goto L_0x00b3;
            case 3: goto L_0x0093;
            case 4: goto L_0x0093;
            case 5: goto L_0x0093;
            default: goto L_0x0058;
        };	 Catch:{ all -> 0x0127 }
    L_0x0058:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mSelectedPosition;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0 = r0.mFirstPosition;	 Catch:{ all -> 0x0127 }
        r37 = r0;	 Catch:{ all -> 0x0127 }
        r27 = r4 - r37;	 Catch:{ all -> 0x0127 }
        if (r27 < 0) goto L_0x0072;	 Catch:{ all -> 0x0127 }
    L_0x0066:
        r0 = r27;	 Catch:{ all -> 0x0127 }
        if (r0 >= r15) goto L_0x0072;	 Catch:{ all -> 0x0127 }
    L_0x006a:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r1 = r27;	 Catch:{ all -> 0x0127 }
        r5 = r0.getChildAt(r1);	 Catch:{ all -> 0x0127 }
    L_0x0072:
        r4 = 0;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r29 = r0.getChildAt(r4);	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mNextSelectedPosition;	 Catch:{ all -> 0x0127 }
        if (r4 < 0) goto L_0x008b;	 Catch:{ all -> 0x0127 }
    L_0x007f:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mNextSelectedPosition;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0 = r0.mSelectedPosition;	 Catch:{ all -> 0x0127 }
        r37 = r0;	 Catch:{ all -> 0x0127 }
        r7 = r4 - r37;	 Catch:{ all -> 0x0127 }
    L_0x008b:
        r4 = r27 + r7;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r6 = r0.getChildAt(r4);	 Catch:{ all -> 0x0127 }
    L_0x0093:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0 = r0.mDataChanged;	 Catch:{ all -> 0x0127 }
        r16 = r0;	 Catch:{ all -> 0x0127 }
        if (r16 == 0) goto L_0x009e;	 Catch:{ all -> 0x0127 }
    L_0x009b:
        r40.handleDataChanged();	 Catch:{ all -> 0x0127 }
    L_0x009e:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mItemCount;	 Catch:{ all -> 0x0127 }
        if (r4 != 0) goto L_0x00ce;	 Catch:{ all -> 0x0127 }
    L_0x00a4:
        r40.resetList();	 Catch:{ all -> 0x0127 }
        r40.invokeOnItemScrollListener();	 Catch:{ all -> 0x0127 }
        if (r13 != 0) goto L_0x0006;
    L_0x00ac:
        r4 = 0;
        r0 = r40;
        r0.mBlockLayoutRequests = r4;
        goto L_0x0006;
    L_0x00b3:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mNextSelectedPosition;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0 = r0.mFirstPosition;	 Catch:{ all -> 0x0127 }
        r37 = r0;	 Catch:{ all -> 0x0127 }
        r27 = r4 - r37;	 Catch:{ all -> 0x0127 }
        if (r27 < 0) goto L_0x0093;	 Catch:{ all -> 0x0127 }
    L_0x00c1:
        r0 = r27;	 Catch:{ all -> 0x0127 }
        if (r0 >= r15) goto L_0x0093;	 Catch:{ all -> 0x0127 }
    L_0x00c5:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r1 = r27;	 Catch:{ all -> 0x0127 }
        r6 = r0.getChildAt(r1);	 Catch:{ all -> 0x0127 }
        goto L_0x0093;	 Catch:{ all -> 0x0127 }
    L_0x00ce:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mItemCount;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0 = r0.mAdapter;	 Catch:{ all -> 0x0127 }
        r37 = r0;	 Catch:{ all -> 0x0127 }
        r37 = r37.getCount();	 Catch:{ all -> 0x0127 }
        r0 = r37;	 Catch:{ all -> 0x0127 }
        if (r4 == r0) goto L_0x0133;	 Catch:{ all -> 0x0127 }
    L_0x00e0:
        r4 = new java.lang.IllegalStateException;	 Catch:{ all -> 0x0127 }
        r37 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0127 }
        r37.<init>();	 Catch:{ all -> 0x0127 }
        r38 = "The content of the adapter has changed but ListView did not receive a notification. Make sure the content of your adapter is not modified from a background thread, but only from the UI thread. Make sure your adapter calls notifyDataSetChanged() when its content changes. [in ListView(";	 Catch:{ all -> 0x0127 }
        r37 = r37.append(r38);	 Catch:{ all -> 0x0127 }
        r38 = r40.getId();	 Catch:{ all -> 0x0127 }
        r37 = r37.append(r38);	 Catch:{ all -> 0x0127 }
        r38 = ", ";	 Catch:{ all -> 0x0127 }
        r37 = r37.append(r38);	 Catch:{ all -> 0x0127 }
        r38 = r40.getClass();	 Catch:{ all -> 0x0127 }
        r37 = r37.append(r38);	 Catch:{ all -> 0x0127 }
        r38 = ") with Adapter(";	 Catch:{ all -> 0x0127 }
        r37 = r37.append(r38);	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0 = r0.mAdapter;	 Catch:{ all -> 0x0127 }
        r38 = r0;	 Catch:{ all -> 0x0127 }
        r38 = r38.getClass();	 Catch:{ all -> 0x0127 }
        r37 = r37.append(r38);	 Catch:{ all -> 0x0127 }
        r38 = ")]";	 Catch:{ all -> 0x0127 }
        r37 = r37.append(r38);	 Catch:{ all -> 0x0127 }
        r37 = r37.toString();	 Catch:{ all -> 0x0127 }
        r0 = r37;	 Catch:{ all -> 0x0127 }
        r4.<init>(r0);	 Catch:{ all -> 0x0127 }
        throw r4;	 Catch:{ all -> 0x0127 }
    L_0x0127:
        r4 = move-exception;
        if (r13 != 0) goto L_0x0132;
    L_0x012a:
        r37 = 0;
        r0 = r37;
        r1 = r40;
        r1.mBlockLayoutRequests = r0;
    L_0x0132:
        throw r4;
    L_0x0133:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mNextSelectedPosition;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0.setSelectedPositionInt(r4);	 Catch:{ all -> 0x0127 }
        r10 = 0;	 Catch:{ all -> 0x0127 }
        r11 = 0;	 Catch:{ all -> 0x0127 }
        r12 = -1;	 Catch:{ all -> 0x0127 }
        r35 = r40.getViewRootImpl();	 Catch:{ all -> 0x0127 }
        if (r35 == 0) goto L_0x017b;	 Catch:{ all -> 0x0127 }
    L_0x0145:
        r19 = r35.getAccessibilityFocusedHost();	 Catch:{ all -> 0x0127 }
        if (r19 == 0) goto L_0x017b;	 Catch:{ all -> 0x0127 }
    L_0x014b:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r1 = r19;	 Catch:{ all -> 0x0127 }
        r18 = r0.getAccessibilityFocusedChild(r1);	 Catch:{ all -> 0x0127 }
        if (r18 == 0) goto L_0x017b;	 Catch:{ all -> 0x0127 }
    L_0x0155:
        if (r16 == 0) goto L_0x016d;	 Catch:{ all -> 0x0127 }
    L_0x0157:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r1 = r18;	 Catch:{ all -> 0x0127 }
        r4 = r0.isDirectChildHeaderOrFooter(r1);	 Catch:{ all -> 0x0127 }
        if (r4 != 0) goto L_0x016d;	 Catch:{ all -> 0x0127 }
    L_0x0161:
        r4 = r18.hasTransientState();	 Catch:{ all -> 0x0127 }
        if (r4 != 0) goto L_0x016d;	 Catch:{ all -> 0x0127 }
    L_0x0167:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mAdapterHasStableIds;	 Catch:{ all -> 0x0127 }
        if (r4 == 0) goto L_0x0173;	 Catch:{ all -> 0x0127 }
    L_0x016d:
        r11 = r19;	 Catch:{ all -> 0x0127 }
        r10 = r35.getAccessibilityFocusedVirtualView();	 Catch:{ all -> 0x0127 }
    L_0x0173:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r1 = r18;	 Catch:{ all -> 0x0127 }
        r12 = r0.getPositionForView(r1);	 Catch:{ all -> 0x0127 }
    L_0x017b:
        r20 = 0;	 Catch:{ all -> 0x0127 }
        r21 = 0;	 Catch:{ all -> 0x0127 }
        r24 = r40.getFocusedChild();	 Catch:{ all -> 0x0127 }
        if (r24 == 0) goto L_0x019f;	 Catch:{ all -> 0x0127 }
    L_0x0185:
        if (r16 == 0) goto L_0x0191;	 Catch:{ all -> 0x0127 }
    L_0x0187:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r1 = r24;	 Catch:{ all -> 0x0127 }
        r4 = r0.isDirectChildHeaderOrFooter(r1);	 Catch:{ all -> 0x0127 }
        if (r4 == 0) goto L_0x019c;	 Catch:{ all -> 0x0127 }
    L_0x0191:
        r20 = r24;	 Catch:{ all -> 0x0127 }
        r21 = r40.findFocus();	 Catch:{ all -> 0x0127 }
        if (r21 == 0) goto L_0x019c;	 Catch:{ all -> 0x0127 }
    L_0x0199:
        r21.onStartTemporaryDetach();	 Catch:{ all -> 0x0127 }
    L_0x019c:
        r40.requestFocus();	 Catch:{ all -> 0x0127 }
    L_0x019f:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0 = r0.mFirstPosition;	 Catch:{ all -> 0x0127 }
        r17 = r0;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0 = r0.mRecycler;	 Catch:{ all -> 0x0127 }
        r32 = r0;	 Catch:{ all -> 0x0127 }
        if (r16 == 0) goto L_0x01c7;	 Catch:{ all -> 0x0127 }
    L_0x01ad:
        r25 = 0;	 Catch:{ all -> 0x0127 }
    L_0x01af:
        r0 = r25;	 Catch:{ all -> 0x0127 }
        if (r0 >= r15) goto L_0x01ce;	 Catch:{ all -> 0x0127 }
    L_0x01b3:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r1 = r25;	 Catch:{ all -> 0x0127 }
        r4 = r0.getChildAt(r1);	 Catch:{ all -> 0x0127 }
        r37 = r17 + r25;	 Catch:{ all -> 0x0127 }
        r0 = r32;	 Catch:{ all -> 0x0127 }
        r1 = r37;	 Catch:{ all -> 0x0127 }
        r0.addScrapView(r4, r1);	 Catch:{ all -> 0x0127 }
        r25 = r25 + 1;	 Catch:{ all -> 0x0127 }
        goto L_0x01af;	 Catch:{ all -> 0x0127 }
    L_0x01c7:
        r0 = r32;	 Catch:{ all -> 0x0127 }
        r1 = r17;	 Catch:{ all -> 0x0127 }
        r0.fillActiveViews(r15, r1);	 Catch:{ all -> 0x0127 }
    L_0x01ce:
        r40.detachAllViewsFromParent();	 Catch:{ all -> 0x0127 }
        r32.removeSkippedScrap();	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mLayoutMode;	 Catch:{ all -> 0x0127 }
        switch(r4) {
            case 1: goto L_0x02fa;
            case 2: goto L_0x02bf;
            case 3: goto L_0x02e9;
            case 4: goto L_0x030a;
            case 5: goto L_0x02d5;
            case 6: goto L_0x031e;
            default: goto L_0x01db;
        };	 Catch:{ all -> 0x0127 }
    L_0x01db:
        if (r15 != 0) goto L_0x034b;	 Catch:{ all -> 0x0127 }
    L_0x01dd:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mStackFromBottom;	 Catch:{ all -> 0x0127 }
        if (r4 != 0) goto L_0x0326;	 Catch:{ all -> 0x0127 }
    L_0x01e3:
        r4 = 0;	 Catch:{ all -> 0x0127 }
        r37 = 1;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r1 = r37;	 Catch:{ all -> 0x0127 }
        r30 = r0.lookForSelectablePosition(r4, r1);	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r1 = r30;	 Catch:{ all -> 0x0127 }
        r0.setSelectedPositionInt(r1);	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r34 = r0.fillFromTop(r8);	 Catch:{ all -> 0x0127 }
    L_0x01fb:
        r32.scrapActiveViews();	 Catch:{ all -> 0x0127 }
        if (r34 == 0) goto L_0x03b9;	 Catch:{ all -> 0x0127 }
    L_0x0200:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mItemsCanFocus;	 Catch:{ all -> 0x0127 }
        if (r4 == 0) goto L_0x03af;	 Catch:{ all -> 0x0127 }
    L_0x0206:
        r4 = r40.hasFocus();	 Catch:{ all -> 0x0127 }
        if (r4 == 0) goto L_0x03af;	 Catch:{ all -> 0x0127 }
    L_0x020c:
        r4 = r34.hasFocus();	 Catch:{ all -> 0x0127 }
        if (r4 != 0) goto L_0x03af;	 Catch:{ all -> 0x0127 }
    L_0x0212:
        r0 = r34;	 Catch:{ all -> 0x0127 }
        r1 = r20;	 Catch:{ all -> 0x0127 }
        if (r0 != r1) goto L_0x0220;	 Catch:{ all -> 0x0127 }
    L_0x0218:
        if (r21 == 0) goto L_0x0220;	 Catch:{ all -> 0x0127 }
    L_0x021a:
        r4 = r21.requestFocus();	 Catch:{ all -> 0x0127 }
        if (r4 != 0) goto L_0x0226;	 Catch:{ all -> 0x0127 }
    L_0x0220:
        r4 = r34.requestFocus();	 Catch:{ all -> 0x0127 }
        if (r4 == 0) goto L_0x039c;	 Catch:{ all -> 0x0127 }
    L_0x0226:
        r22 = 1;	 Catch:{ all -> 0x0127 }
    L_0x0228:
        if (r22 != 0) goto L_0x03a0;	 Catch:{ all -> 0x0127 }
    L_0x022a:
        r23 = r40.getFocusedChild();	 Catch:{ all -> 0x0127 }
        if (r23 == 0) goto L_0x0233;	 Catch:{ all -> 0x0127 }
    L_0x0230:
        r23.clearFocus();	 Catch:{ all -> 0x0127 }
    L_0x0233:
        r4 = -1;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r1 = r34;	 Catch:{ all -> 0x0127 }
        r0.positionSelector(r4, r1);	 Catch:{ all -> 0x0127 }
    L_0x023b:
        r4 = r34.getTop();	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0.mSelectedTop = r4;	 Catch:{ all -> 0x0127 }
    L_0x0243:
        if (r35 == 0) goto L_0x0270;	 Catch:{ all -> 0x0127 }
    L_0x0245:
        r28 = r35.getAccessibilityFocusedHost();	 Catch:{ all -> 0x0127 }
        if (r28 != 0) goto L_0x0270;	 Catch:{ all -> 0x0127 }
    L_0x024b:
        if (r11 == 0) goto L_0x0438;	 Catch:{ all -> 0x0127 }
    L_0x024d:
        r4 = r11.isAttachedToWindow();	 Catch:{ all -> 0x0127 }
        if (r4 == 0) goto L_0x0438;	 Catch:{ all -> 0x0127 }
    L_0x0253:
        r31 = r11.getAccessibilityNodeProvider();	 Catch:{ all -> 0x0127 }
        if (r10 == 0) goto L_0x0433;	 Catch:{ all -> 0x0127 }
    L_0x0259:
        if (r31 == 0) goto L_0x0433;	 Catch:{ all -> 0x0127 }
    L_0x025b:
        r38 = r10.getSourceNodeId();	 Catch:{ all -> 0x0127 }
        r36 = android.view.accessibility.AccessibilityNodeInfo.getVirtualDescendantId(r38);	 Catch:{ all -> 0x0127 }
        r4 = 64;	 Catch:{ all -> 0x0127 }
        r37 = 0;	 Catch:{ all -> 0x0127 }
        r0 = r31;	 Catch:{ all -> 0x0127 }
        r1 = r36;	 Catch:{ all -> 0x0127 }
        r2 = r37;	 Catch:{ all -> 0x0127 }
        r0.performAction(r1, r4, r2);	 Catch:{ all -> 0x0127 }
    L_0x0270:
        if (r21 == 0) goto L_0x027b;	 Catch:{ all -> 0x0127 }
    L_0x0272:
        r4 = r21.getWindowToken();	 Catch:{ all -> 0x0127 }
        if (r4 == 0) goto L_0x027b;	 Catch:{ all -> 0x0127 }
    L_0x0278:
        r21.onFinishTemporaryDetach();	 Catch:{ all -> 0x0127 }
    L_0x027b:
        r4 = 0;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0.mLayoutMode = r4;	 Catch:{ all -> 0x0127 }
        r4 = 0;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0.mDataChanged = r4;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mPositionScrollAfterLayout;	 Catch:{ all -> 0x0127 }
        if (r4 == 0) goto L_0x0299;	 Catch:{ all -> 0x0127 }
    L_0x028b:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mPositionScrollAfterLayout;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0.post(r4);	 Catch:{ all -> 0x0127 }
        r4 = 0;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0.mPositionScrollAfterLayout = r4;	 Catch:{ all -> 0x0127 }
    L_0x0299:
        r4 = 0;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0.mNeedSync = r4;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mSelectedPosition;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0.setNextSelectedPositionInt(r4);	 Catch:{ all -> 0x0127 }
        r40.updateScrollIndicators();	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mItemCount;	 Catch:{ all -> 0x0127 }
        if (r4 <= 0) goto L_0x02b3;	 Catch:{ all -> 0x0127 }
    L_0x02b0:
        r40.checkSelectionChanged();	 Catch:{ all -> 0x0127 }
    L_0x02b3:
        r40.invokeOnItemScrollListener();	 Catch:{ all -> 0x0127 }
        if (r13 != 0) goto L_0x0006;
    L_0x02b8:
        r4 = 0;
        r0 = r40;
        r0.mBlockLayoutRequests = r4;
        goto L_0x0006;
    L_0x02bf:
        if (r6 == 0) goto L_0x02cd;
    L_0x02c1:
        r4 = r6.getTop();	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r34 = r0.fillFromSelection(r4, r8, r9);	 Catch:{ all -> 0x0127 }
        goto L_0x01fb;	 Catch:{ all -> 0x0127 }
    L_0x02cd:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r34 = r0.fillFromMiddle(r8, r9);	 Catch:{ all -> 0x0127 }
        goto L_0x01fb;	 Catch:{ all -> 0x0127 }
    L_0x02d5:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mSyncPosition;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0 = r0.mSpecificTop;	 Catch:{ all -> 0x0127 }
        r37 = r0;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r1 = r37;	 Catch:{ all -> 0x0127 }
        r34 = r0.fillSpecific(r4, r1);	 Catch:{ all -> 0x0127 }
        goto L_0x01fb;	 Catch:{ all -> 0x0127 }
    L_0x02e9:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mItemCount;	 Catch:{ all -> 0x0127 }
        r4 = r4 + -1;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r34 = r0.fillUp(r4, r9);	 Catch:{ all -> 0x0127 }
        r40.adjustViewsUpOrDown();	 Catch:{ all -> 0x0127 }
        goto L_0x01fb;	 Catch:{ all -> 0x0127 }
    L_0x02fa:
        r4 = 0;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0.mFirstPosition = r4;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r34 = r0.fillFromTop(r8);	 Catch:{ all -> 0x0127 }
        r40.adjustViewsUpOrDown();	 Catch:{ all -> 0x0127 }
        goto L_0x01fb;	 Catch:{ all -> 0x0127 }
    L_0x030a:
        r4 = r40.reconcileSelectedPosition();	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0 = r0.mSpecificTop;	 Catch:{ all -> 0x0127 }
        r37 = r0;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r1 = r37;	 Catch:{ all -> 0x0127 }
        r34 = r0.fillSpecific(r4, r1);	 Catch:{ all -> 0x0127 }
        goto L_0x01fb;	 Catch:{ all -> 0x0127 }
    L_0x031e:
        r4 = r40;	 Catch:{ all -> 0x0127 }
        r34 = r4.moveSelection(r5, r6, r7, r8, r9);	 Catch:{ all -> 0x0127 }
        goto L_0x01fb;	 Catch:{ all -> 0x0127 }
    L_0x0326:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mItemCount;	 Catch:{ all -> 0x0127 }
        r4 = r4 + -1;	 Catch:{ all -> 0x0127 }
        r37 = 0;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r1 = r37;	 Catch:{ all -> 0x0127 }
        r30 = r0.lookForSelectablePosition(r4, r1);	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r1 = r30;	 Catch:{ all -> 0x0127 }
        r0.setSelectedPositionInt(r1);	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mItemCount;	 Catch:{ all -> 0x0127 }
        r4 = r4 + -1;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r34 = r0.fillUp(r4, r9);	 Catch:{ all -> 0x0127 }
        goto L_0x01fb;	 Catch:{ all -> 0x0127 }
    L_0x034b:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mSelectedPosition;	 Catch:{ all -> 0x0127 }
        if (r4 < 0) goto L_0x0372;	 Catch:{ all -> 0x0127 }
    L_0x0351:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mSelectedPosition;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0 = r0.mItemCount;	 Catch:{ all -> 0x0127 }
        r37 = r0;	 Catch:{ all -> 0x0127 }
        r0 = r37;	 Catch:{ all -> 0x0127 }
        if (r4 >= r0) goto L_0x0372;	 Catch:{ all -> 0x0127 }
    L_0x035f:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mSelectedPosition;	 Catch:{ all -> 0x0127 }
        if (r5 != 0) goto L_0x036d;	 Catch:{ all -> 0x0127 }
    L_0x0365:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r34 = r0.fillSpecific(r4, r8);	 Catch:{ all -> 0x0127 }
        goto L_0x01fb;	 Catch:{ all -> 0x0127 }
    L_0x036d:
        r8 = r5.getTop();	 Catch:{ all -> 0x0127 }
        goto L_0x0365;	 Catch:{ all -> 0x0127 }
    L_0x0372:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mFirstPosition;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0 = r0.mItemCount;	 Catch:{ all -> 0x0127 }
        r37 = r0;	 Catch:{ all -> 0x0127 }
        r0 = r37;	 Catch:{ all -> 0x0127 }
        if (r4 >= r0) goto L_0x0393;	 Catch:{ all -> 0x0127 }
    L_0x0380:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mFirstPosition;	 Catch:{ all -> 0x0127 }
        if (r29 != 0) goto L_0x038e;	 Catch:{ all -> 0x0127 }
    L_0x0386:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r34 = r0.fillSpecific(r4, r8);	 Catch:{ all -> 0x0127 }
        goto L_0x01fb;	 Catch:{ all -> 0x0127 }
    L_0x038e:
        r8 = r29.getTop();	 Catch:{ all -> 0x0127 }
        goto L_0x0386;	 Catch:{ all -> 0x0127 }
    L_0x0393:
        r4 = 0;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r34 = r0.fillSpecific(r4, r8);	 Catch:{ all -> 0x0127 }
        goto L_0x01fb;	 Catch:{ all -> 0x0127 }
    L_0x039c:
        r22 = 0;	 Catch:{ all -> 0x0127 }
        goto L_0x0228;	 Catch:{ all -> 0x0127 }
    L_0x03a0:
        r4 = 0;	 Catch:{ all -> 0x0127 }
        r0 = r34;	 Catch:{ all -> 0x0127 }
        r0.setSelected(r4);	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mSelectorRect;	 Catch:{ all -> 0x0127 }
        r4.setEmpty();	 Catch:{ all -> 0x0127 }
        goto L_0x023b;	 Catch:{ all -> 0x0127 }
    L_0x03af:
        r4 = -1;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r1 = r34;	 Catch:{ all -> 0x0127 }
        r0.positionSelector(r4, r1);	 Catch:{ all -> 0x0127 }
        goto L_0x023b;	 Catch:{ all -> 0x0127 }
    L_0x03b9:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mTouchMode;	 Catch:{ all -> 0x0127 }
        r37 = 1;	 Catch:{ all -> 0x0127 }
        r0 = r37;	 Catch:{ all -> 0x0127 }
        if (r4 == r0) goto L_0x03cd;	 Catch:{ all -> 0x0127 }
    L_0x03c3:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mTouchMode;	 Catch:{ all -> 0x0127 }
        r37 = 2;	 Catch:{ all -> 0x0127 }
        r0 = r37;	 Catch:{ all -> 0x0127 }
        if (r4 != r0) goto L_0x03fb;	 Catch:{ all -> 0x0127 }
    L_0x03cd:
        r26 = 1;	 Catch:{ all -> 0x0127 }
    L_0x03cf:
        if (r26 == 0) goto L_0x03fe;	 Catch:{ all -> 0x0127 }
    L_0x03d1:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mMotionPosition;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0 = r0.mFirstPosition;	 Catch:{ all -> 0x0127 }
        r37 = r0;	 Catch:{ all -> 0x0127 }
        r4 = r4 - r37;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r14 = r0.getChildAt(r4);	 Catch:{ all -> 0x0127 }
        if (r14 == 0) goto L_0x03ee;	 Catch:{ all -> 0x0127 }
    L_0x03e5:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mMotionPosition;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0.positionSelector(r4, r14);	 Catch:{ all -> 0x0127 }
    L_0x03ee:
        r4 = r40.hasFocus();	 Catch:{ all -> 0x0127 }
        if (r4 == 0) goto L_0x0243;	 Catch:{ all -> 0x0127 }
    L_0x03f4:
        if (r21 == 0) goto L_0x0243;	 Catch:{ all -> 0x0127 }
    L_0x03f6:
        r21.requestFocus();	 Catch:{ all -> 0x0127 }
        goto L_0x0243;	 Catch:{ all -> 0x0127 }
    L_0x03fb:
        r26 = 0;	 Catch:{ all -> 0x0127 }
        goto L_0x03cf;	 Catch:{ all -> 0x0127 }
    L_0x03fe:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mSelectorPosition;	 Catch:{ all -> 0x0127 }
        r37 = -1;	 Catch:{ all -> 0x0127 }
        r0 = r37;	 Catch:{ all -> 0x0127 }
        if (r4 == r0) goto L_0x0426;	 Catch:{ all -> 0x0127 }
    L_0x0408:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mSelectorPosition;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0 = r0.mFirstPosition;	 Catch:{ all -> 0x0127 }
        r37 = r0;	 Catch:{ all -> 0x0127 }
        r4 = r4 - r37;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r14 = r0.getChildAt(r4);	 Catch:{ all -> 0x0127 }
        if (r14 == 0) goto L_0x03ee;	 Catch:{ all -> 0x0127 }
    L_0x041c:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mSelectorPosition;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0.positionSelector(r4, r14);	 Catch:{ all -> 0x0127 }
        goto L_0x03ee;	 Catch:{ all -> 0x0127 }
    L_0x0426:
        r4 = 0;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r0.mSelectedTop = r4;	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mSelectorRect;	 Catch:{ all -> 0x0127 }
        r4.setEmpty();	 Catch:{ all -> 0x0127 }
        goto L_0x03ee;	 Catch:{ all -> 0x0127 }
    L_0x0433:
        r11.requestAccessibilityFocus();	 Catch:{ all -> 0x0127 }
        goto L_0x0270;	 Catch:{ all -> 0x0127 }
    L_0x0438:
        r4 = -1;	 Catch:{ all -> 0x0127 }
        if (r12 == r4) goto L_0x0270;	 Catch:{ all -> 0x0127 }
    L_0x043b:
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r4 = r0.mFirstPosition;	 Catch:{ all -> 0x0127 }
        r4 = r12 - r4;	 Catch:{ all -> 0x0127 }
        r37 = 0;	 Catch:{ all -> 0x0127 }
        r38 = r40.getChildCount();	 Catch:{ all -> 0x0127 }
        r38 = r38 + -1;	 Catch:{ all -> 0x0127 }
        r0 = r37;	 Catch:{ all -> 0x0127 }
        r1 = r38;	 Catch:{ all -> 0x0127 }
        r30 = android.util.MathUtils.constrain(r4, r0, r1);	 Catch:{ all -> 0x0127 }
        r0 = r40;	 Catch:{ all -> 0x0127 }
        r1 = r30;	 Catch:{ all -> 0x0127 }
        r33 = r0.getChildAt(r1);	 Catch:{ all -> 0x0127 }
        if (r33 == 0) goto L_0x0270;	 Catch:{ all -> 0x0127 }
    L_0x045b:
        r33.requestAccessibilityFocus();	 Catch:{ all -> 0x0127 }
        goto L_0x0270;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.ListView.layoutChildren():void");
    }

    public /* bridge */ /* synthetic */ Adapter getAdapter() {
        return getAdapter();
    }

    public /* bridge */ /* synthetic */ void setAdapter(Adapter x0) {
        setAdapter((ListAdapter) x0);
    }

    public ListView(Context context) {
        this(context, null);
    }

    public ListView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.listViewStyle);
    }

    public ListView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mHeaderViewInfos = Lists.newArrayList();
        this.mFooterViewInfos = Lists.newArrayList();
        this.mAreAllItemsSelectable = true;
        this.mItemsCanFocus = false;
        this.mTempRect = new Rect();
        this.mArrowScrollFocusResult = new ArrowScrollFocusResult();
        TypedArray a = context.obtainStyledAttributes(attrs, com.android.internal.R.styleable.ListView, defStyleAttr, defStyleRes);
        Object[] entries = a.getTextArray(0);
        if (entries != null) {
            setAdapter(new ArrayAdapter(context, (int) R.layout.simple_list_item_1, entries));
        }
        Drawable d = a.getDrawable(1);
        if (d != null) {
            setDivider(d);
        }
        Drawable osHeader = a.getDrawable(5);
        if (osHeader != null) {
            setOverscrollHeader(osHeader);
        }
        Drawable osFooter = a.getDrawable(6);
        if (osFooter != null) {
            setOverscrollFooter(osFooter);
        }
        int dividerHeight = a.getDimensionPixelSize(MIN_SCROLL_PREVIEW_PIXELS, 0);
        if (dividerHeight != 0) {
            setDividerHeight(dividerHeight);
        }
        this.mHeaderDividersEnabled = a.getBoolean(3, true);
        this.mFooterDividersEnabled = a.getBoolean(4, true);
        a.recycle();
    }

    public int getMaxScrollAmount() {
        return (int) (MAX_SCROLL_FACTOR * ((float) (this.mBottom - this.mTop)));
    }

    private void adjustViewsUpOrDown() {
        int childCount = getChildCount();
        if (childCount > 0) {
            int delta;
            if (this.mStackFromBottom) {
                delta = getChildAt(childCount + NO_POSITION).getBottom() - (getHeight() - this.mListPadding.bottom);
                if (this.mFirstPosition + childCount < this.mItemCount) {
                    delta += this.mDividerHeight;
                }
                if (delta > 0) {
                    delta = 0;
                }
            } else {
                delta = getChildAt(0).getTop() - this.mListPadding.top;
                if (this.mFirstPosition != 0) {
                    delta -= this.mDividerHeight;
                }
                if (delta < 0) {
                    delta = 0;
                }
            }
            if (delta != 0) {
                offsetChildrenTopAndBottom(-delta);
            }
        }
    }

    public void addHeaderView(View v, Object data, boolean isSelectable) {
        FixedViewInfo info = new FixedViewInfo();
        info.view = v;
        info.data = data;
        info.isSelectable = isSelectable;
        this.mHeaderViewInfos.add(info);
        this.mAreAllItemsSelectable &= isSelectable;
        if (this.mAdapter != null) {
            if (!(this.mAdapter instanceof HeaderViewListAdapter)) {
                this.mAdapter = new HeaderViewListAdapter(this.mHeaderViewInfos, this.mFooterViewInfos, this.mAdapter);
            }
            if (this.mDataSetObserver != null) {
                this.mDataSetObserver.onChanged();
            }
        }
    }

    public void addHeaderView(View v) {
        addHeaderView(v, null, true);
    }

    public int getHeaderViewsCount() {
        return this.mHeaderViewInfos.size();
    }

    public boolean removeHeaderView(View v) {
        if (this.mHeaderViewInfos.size() <= 0) {
            return false;
        }
        boolean result = false;
        if (this.mAdapter != null && ((HeaderViewListAdapter) this.mAdapter).removeHeader(v)) {
            if (this.mDataSetObserver != null) {
                this.mDataSetObserver.onChanged();
            }
            result = true;
        }
        removeFixedViewInfo(v, this.mHeaderViewInfos);
        return result;
    }

    private void removeFixedViewInfo(View v, ArrayList<FixedViewInfo> where) {
        int len = where.size();
        for (int i = 0; i < len; i++) {
            if (((FixedViewInfo) where.get(i)).view == v) {
                where.remove(i);
                return;
            }
        }
    }

    public void addFooterView(View v, Object data, boolean isSelectable) {
        FixedViewInfo info = new FixedViewInfo();
        info.view = v;
        info.data = data;
        info.isSelectable = isSelectable;
        this.mFooterViewInfos.add(info);
        this.mAreAllItemsSelectable &= isSelectable;
        if (this.mAdapter != null) {
            if (!(this.mAdapter instanceof HeaderViewListAdapter)) {
                this.mAdapter = new HeaderViewListAdapter(this.mHeaderViewInfos, this.mFooterViewInfos, this.mAdapter);
            }
            if (this.mDataSetObserver != null) {
                this.mDataSetObserver.onChanged();
            }
        }
    }

    public void addFooterView(View v) {
        addFooterView(v, null, true);
    }

    public int getFooterViewsCount() {
        return this.mFooterViewInfos.size();
    }

    public boolean removeFooterView(View v) {
        if (this.mFooterViewInfos.size() <= 0) {
            return false;
        }
        boolean result = false;
        if (this.mAdapter != null && ((HeaderViewListAdapter) this.mAdapter).removeFooter(v)) {
            if (this.mDataSetObserver != null) {
                this.mDataSetObserver.onChanged();
            }
            result = true;
        }
        removeFixedViewInfo(v, this.mFooterViewInfos);
        return result;
    }

    public ListAdapter m56getAdapter() {
        return this.mAdapter;
    }

    @RemotableViewMethod
    public void setRemoteViewsAdapter(Intent intent) {
        super.setRemoteViewsAdapter(intent);
    }

    public void setAdapter(ListAdapter adapter) {
        if (!(this.mAdapter == null || this.mDataSetObserver == null)) {
            this.mAdapter.unregisterDataSetObserver(this.mDataSetObserver);
        }
        resetList();
        this.mRecycler.clear();
        if (this.mHeaderViewInfos.size() > 0 || this.mFooterViewInfos.size() > 0) {
            this.mAdapter = new HeaderViewListAdapter(this.mHeaderViewInfos, this.mFooterViewInfos, adapter);
        } else {
            this.mAdapter = adapter;
        }
        this.mOldSelectedPosition = NO_POSITION;
        this.mOldSelectedRowId = Long.MIN_VALUE;
        super.setAdapter(adapter);
        if (this.mAdapter != null) {
            int position;
            this.mAreAllItemsSelectable = this.mAdapter.areAllItemsEnabled();
            this.mOldItemCount = this.mItemCount;
            this.mItemCount = this.mAdapter.getCount();
            checkFocus();
            this.mDataSetObserver = new AdapterDataSetObserver(this);
            this.mAdapter.registerDataSetObserver(this.mDataSetObserver);
            this.mRecycler.setViewTypeCount(this.mAdapter.getViewTypeCount());
            if (this.mStackFromBottom) {
                position = lookForSelectablePosition(this.mItemCount + NO_POSITION, false);
            } else {
                position = lookForSelectablePosition(0, true);
            }
            setSelectedPositionInt(position);
            setNextSelectedPositionInt(position);
            if (this.mItemCount == 0) {
                checkSelectionChanged();
            }
        } else {
            this.mAreAllItemsSelectable = true;
            checkFocus();
            checkSelectionChanged();
        }
        requestLayout();
    }

    void resetList() {
        clearRecycledState(this.mHeaderViewInfos);
        clearRecycledState(this.mFooterViewInfos);
        super.resetList();
        this.mLayoutMode = 0;
    }

    private void clearRecycledState(ArrayList<FixedViewInfo> infos) {
        if (infos != null) {
            int count = infos.size();
            for (int i = 0; i < count; i++) {
                LayoutParams p = (LayoutParams) ((FixedViewInfo) infos.get(i)).view.getLayoutParams();
                if (p != null) {
                    p.recycledHeaderFooter = false;
                }
            }
        }
    }

    private boolean showingTopFadingEdge() {
        int listTop = this.mScrollY + this.mListPadding.top;
        if (this.mFirstPosition > 0 || getChildAt(0).getTop() > listTop) {
            return true;
        }
        return false;
    }

    private boolean showingBottomFadingEdge() {
        int childCount = getChildCount();
        return (this.mFirstPosition + childCount) + NO_POSITION < this.mItemCount + NO_POSITION || getChildAt(childCount + NO_POSITION).getBottom() < (this.mScrollY + getHeight()) - this.mListPadding.bottom;
    }

    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
        int rectTopWithinChild = rect.top;
        rect.offset(child.getLeft(), child.getTop());
        rect.offset(-child.getScrollX(), -child.getScrollY());
        int height = getHeight();
        int listUnfadedTop = getScrollY();
        int listUnfadedBottom = listUnfadedTop + height;
        int fadingEdge = getVerticalFadingEdgeLength();
        if (showingTopFadingEdge() && (this.mSelectedPosition > 0 || rectTopWithinChild > fadingEdge)) {
            listUnfadedTop += fadingEdge;
        }
        int bottomOfBottomChild = getChildAt(getChildCount() + NO_POSITION).getBottom();
        if (showingBottomFadingEdge() && (this.mSelectedPosition < this.mItemCount + NO_POSITION || rect.bottom < bottomOfBottomChild - fadingEdge)) {
            listUnfadedBottom -= fadingEdge;
        }
        int scrollYDelta = 0;
        if (rect.bottom > listUnfadedBottom && rect.top > listUnfadedTop) {
            if (rect.height() > height) {
                scrollYDelta = 0 + (rect.top - listUnfadedTop);
            } else {
                scrollYDelta = 0 + (rect.bottom - listUnfadedBottom);
            }
            scrollYDelta = Math.min(scrollYDelta, bottomOfBottomChild - listUnfadedBottom);
        } else if (rect.top < listUnfadedTop && rect.bottom < listUnfadedBottom) {
            if (rect.height() > height) {
                scrollYDelta = 0 - (listUnfadedBottom - rect.bottom);
            } else {
                scrollYDelta = 0 - (listUnfadedTop - rect.top);
            }
            scrollYDelta = Math.max(scrollYDelta, getChildAt(0).getTop() - listUnfadedTop);
        }
        boolean scroll = scrollYDelta != 0;
        if (scroll) {
            scrollListItemsBy(-scrollYDelta);
            positionSelector(NO_POSITION, child);
            this.mSelectedTop = child.getTop();
            invalidate();
        }
        return scroll;
    }

    void fillGap(boolean down) {
        int count = getChildCount();
        if (down) {
            int startOffset;
            int paddingTop = 0;
            if ((this.mGroupFlags & 34) == 34) {
                paddingTop = getListPaddingTop();
            }
            if (count > 0) {
                startOffset = getChildAt(count + NO_POSITION).getBottom() + this.mDividerHeight;
            } else {
                startOffset = paddingTop;
            }
            fillDown(this.mFirstPosition + count, startOffset);
            correctTooHigh(getChildCount());
            return;
        }
        int paddingBottom = 0;
        if ((this.mGroupFlags & 34) == 34) {
            paddingBottom = getListPaddingBottom();
        }
        fillUp(this.mFirstPosition + NO_POSITION, count > 0 ? getChildAt(0).getTop() - this.mDividerHeight : getHeight() - paddingBottom);
        correctTooLow(getChildCount());
    }

    private View fillDown(int pos, int nextTop) {
        View selectedView = null;
        int end = this.mBottom - this.mTop;
        if ((this.mGroupFlags & 34) == 34) {
            end -= this.mListPadding.bottom;
        }
        while (nextTop < end && pos < this.mItemCount) {
            boolean selected = pos == this.mSelectedPosition;
            View child = makeAndAddView(pos, nextTop, true, this.mListPadding.left, selected);
            nextTop = child.getBottom() + this.mDividerHeight;
            if (selected) {
                selectedView = child;
            }
            pos++;
        }
        setVisibleRangeHint(this.mFirstPosition, (this.mFirstPosition + getChildCount()) + NO_POSITION);
        return selectedView;
    }

    private View fillUp(int pos, int nextBottom) {
        View selectedView = null;
        int end = 0;
        if ((this.mGroupFlags & 34) == 34) {
            end = this.mListPadding.top;
        }
        while (nextBottom > end && pos >= 0) {
            boolean selected;
            if (pos == this.mSelectedPosition) {
                selected = true;
            } else {
                selected = false;
            }
            View child = makeAndAddView(pos, nextBottom, false, this.mListPadding.left, selected);
            nextBottom = child.getTop() - this.mDividerHeight;
            if (selected) {
                selectedView = child;
            }
            pos += NO_POSITION;
        }
        this.mFirstPosition = pos + 1;
        setVisibleRangeHint(this.mFirstPosition, (this.mFirstPosition + getChildCount()) + NO_POSITION);
        return selectedView;
    }

    private View fillFromTop(int nextTop) {
        this.mFirstPosition = Math.min(this.mFirstPosition, this.mSelectedPosition);
        this.mFirstPosition = Math.min(this.mFirstPosition, this.mItemCount + NO_POSITION);
        if (this.mFirstPosition < 0) {
            this.mFirstPosition = 0;
        }
        return fillDown(this.mFirstPosition, nextTop);
    }

    private View fillFromMiddle(int childrenTop, int childrenBottom) {
        int height = childrenBottom - childrenTop;
        int position = reconcileSelectedPosition();
        View sel = makeAndAddView(position, childrenTop, true, this.mListPadding.left, true);
        this.mFirstPosition = position;
        int selHeight = sel.getMeasuredHeight();
        if (selHeight <= height) {
            sel.offsetTopAndBottom((height - selHeight) / MIN_SCROLL_PREVIEW_PIXELS);
        }
        fillAboveAndBelow(sel, position);
        if (this.mStackFromBottom) {
            correctTooLow(getChildCount());
        } else {
            correctTooHigh(getChildCount());
        }
        return sel;
    }

    private void fillAboveAndBelow(View sel, int position) {
        int dividerHeight = this.mDividerHeight;
        if (this.mStackFromBottom) {
            fillDown(position + 1, sel.getBottom() + dividerHeight);
            adjustViewsUpOrDown();
            fillUp(position + NO_POSITION, sel.getTop() - dividerHeight);
            return;
        }
        fillUp(position + NO_POSITION, sel.getTop() - dividerHeight);
        adjustViewsUpOrDown();
        fillDown(position + 1, sel.getBottom() + dividerHeight);
    }

    private View fillFromSelection(int selectedTop, int childrenTop, int childrenBottom) {
        int fadingEdgeLength = getVerticalFadingEdgeLength();
        int selectedPosition = this.mSelectedPosition;
        int topSelectionPixel = getTopSelectionPixel(childrenTop, fadingEdgeLength, selectedPosition);
        int bottomSelectionPixel = getBottomSelectionPixel(childrenBottom, fadingEdgeLength, selectedPosition);
        View sel = makeAndAddView(selectedPosition, selectedTop, true, this.mListPadding.left, true);
        if (sel.getBottom() > bottomSelectionPixel) {
            sel.offsetTopAndBottom(-Math.min(sel.getTop() - topSelectionPixel, sel.getBottom() - bottomSelectionPixel));
        } else if (sel.getTop() < topSelectionPixel) {
            sel.offsetTopAndBottom(Math.min(topSelectionPixel - sel.getTop(), bottomSelectionPixel - sel.getBottom()));
        }
        fillAboveAndBelow(sel, selectedPosition);
        if (this.mStackFromBottom) {
            correctTooLow(getChildCount());
        } else {
            correctTooHigh(getChildCount());
        }
        return sel;
    }

    private int getBottomSelectionPixel(int childrenBottom, int fadingEdgeLength, int selectedPosition) {
        int bottomSelectionPixel = childrenBottom;
        if (selectedPosition != this.mItemCount + NO_POSITION) {
            return bottomSelectionPixel - fadingEdgeLength;
        }
        return bottomSelectionPixel;
    }

    private int getTopSelectionPixel(int childrenTop, int fadingEdgeLength, int selectedPosition) {
        int topSelectionPixel = childrenTop;
        if (selectedPosition > 0) {
            return topSelectionPixel + fadingEdgeLength;
        }
        return topSelectionPixel;
    }

    @RemotableViewMethod
    public void smoothScrollToPosition(int position) {
        super.smoothScrollToPosition(position);
    }

    @RemotableViewMethod
    public void smoothScrollByOffset(int offset) {
        super.smoothScrollByOffset(offset);
    }

    private View moveSelection(View oldSel, View newSel, int delta, int childrenTop, int childrenBottom) {
        View sel;
        int fadingEdgeLength = getVerticalFadingEdgeLength();
        int selectedPosition = this.mSelectedPosition;
        int topSelectionPixel = getTopSelectionPixel(childrenTop, fadingEdgeLength, selectedPosition);
        int bottomSelectionPixel = getBottomSelectionPixel(childrenTop, fadingEdgeLength, selectedPosition);
        int halfVerticalSpace;
        if (delta > 0) {
            oldSel = makeAndAddView(selectedPosition + NO_POSITION, oldSel.getTop(), true, this.mListPadding.left, false);
            int dividerHeight = this.mDividerHeight;
            sel = makeAndAddView(selectedPosition, oldSel.getBottom() + dividerHeight, true, this.mListPadding.left, true);
            if (sel.getBottom() > bottomSelectionPixel) {
                halfVerticalSpace = (childrenBottom - childrenTop) / MIN_SCROLL_PREVIEW_PIXELS;
                int offset = Math.min(Math.min(sel.getTop() - topSelectionPixel, sel.getBottom() - bottomSelectionPixel), halfVerticalSpace);
                oldSel.offsetTopAndBottom(-offset);
                sel.offsetTopAndBottom(-offset);
            }
            if (this.mStackFromBottom) {
                fillDown(this.mSelectedPosition + 1, sel.getBottom() + dividerHeight);
                adjustViewsUpOrDown();
                fillUp(this.mSelectedPosition - 2, sel.getTop() - dividerHeight);
            } else {
                fillUp(this.mSelectedPosition - 2, sel.getTop() - dividerHeight);
                adjustViewsUpOrDown();
                fillDown(this.mSelectedPosition + 1, sel.getBottom() + dividerHeight);
            }
        } else if (delta < 0) {
            if (newSel != null) {
                sel = makeAndAddView(selectedPosition, newSel.getTop(), true, this.mListPadding.left, true);
            } else {
                sel = makeAndAddView(selectedPosition, oldSel.getTop(), false, this.mListPadding.left, true);
            }
            if (sel.getTop() < topSelectionPixel) {
                halfVerticalSpace = (childrenBottom - childrenTop) / MIN_SCROLL_PREVIEW_PIXELS;
                sel.offsetTopAndBottom(Math.min(Math.min(topSelectionPixel - sel.getTop(), bottomSelectionPixel - sel.getBottom()), halfVerticalSpace));
            }
            fillAboveAndBelow(sel, selectedPosition);
        } else {
            int oldTop = oldSel.getTop();
            sel = makeAndAddView(selectedPosition, oldTop, true, this.mListPadding.left, true);
            if (oldTop < childrenTop && sel.getBottom() < childrenTop + 20) {
                sel.offsetTopAndBottom(childrenTop - sel.getTop());
            }
            fillAboveAndBelow(sel, selectedPosition);
        }
        return sel;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (getChildCount() > 0) {
            View focusedChild = getFocusedChild();
            if (focusedChild != null) {
                int childPosition = this.mFirstPosition + indexOfChild(focusedChild);
                int top = focusedChild.getTop() - Math.max(0, focusedChild.getBottom() - (h - this.mPaddingTop));
                if (this.mFocusSelector == null) {
                    this.mFocusSelector = new FocusSelector();
                }
                post(this.mFocusSelector.setup(childPosition, top));
            }
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int childWidth = 0;
        int childHeight = 0;
        int childState = 0;
        if (this.mAdapter == null) {
            i = 0;
        } else {
            i = this.mAdapter.getCount();
        }
        this.mItemCount = i;
        if (this.mItemCount > 0 && (widthMode == 0 || heightMode == 0)) {
            View child = obtainView(0, this.mIsScrap);
            measureScrapChild(child, 0, widthMeasureSpec);
            childWidth = child.getMeasuredWidth();
            childHeight = child.getMeasuredHeight();
            childState = View.combineMeasuredStates(0, child.getMeasuredState());
            if (recycleOnMeasure() && this.mRecycler.shouldRecycleViewType(((LayoutParams) child.getLayoutParams()).viewType)) {
                this.mRecycler.addScrapView(child, 0);
            }
        }
        if (widthMode == 0) {
            widthSize = ((this.mListPadding.left + this.mListPadding.right) + childWidth) + getVerticalScrollbarWidth();
        } else {
            widthSize |= Color.BLACK & childState;
        }
        if (heightMode == 0) {
            heightSize = ((this.mListPadding.top + this.mListPadding.bottom) + childHeight) + (getVerticalFadingEdgeLength() * MIN_SCROLL_PREVIEW_PIXELS);
        }
        if (heightMode == RtlSpacingHelper.UNDEFINED) {
            heightSize = measureHeightOfChildren(widthMeasureSpec, 0, NO_POSITION, heightSize, NO_POSITION);
        }
        setMeasuredDimension(widthSize, heightSize);
        this.mWidthMeasureSpec = widthMeasureSpec;
    }

    private void measureScrapChild(View child, int position, int widthMeasureSpec) {
        int childHeightSpec;
        LayoutParams p = (LayoutParams) child.getLayoutParams();
        if (p == null) {
            p = (LayoutParams) generateDefaultLayoutParams();
            child.setLayoutParams(p);
        }
        p.viewType = this.mAdapter.getItemViewType(position);
        p.forceAdd = true;
        int childWidthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, this.mListPadding.left + this.mListPadding.right, p.width);
        int lpHeight = p.height;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, 0);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    @ExportedProperty(category = "list")
    protected boolean recycleOnMeasure() {
        return true;
    }

    final int measureHeightOfChildren(int widthMeasureSpec, int startPosition, int endPosition, int maxHeight, int disallowPartialChildPosition) {
        ListAdapter adapter = this.mAdapter;
        if (adapter == null) {
            return this.mListPadding.top + this.mListPadding.bottom;
        }
        int returnedHeight = this.mListPadding.top + this.mListPadding.bottom;
        int dividerHeight = (this.mDividerHeight <= 0 || this.mDivider == null) ? 0 : this.mDividerHeight;
        int prevHeightWithoutPartialChild = 0;
        if (endPosition == NO_POSITION) {
            endPosition = adapter.getCount() + NO_POSITION;
        }
        RecycleBin recycleBin = this.mRecycler;
        boolean recyle = recycleOnMeasure();
        boolean[] isScrap = this.mIsScrap;
        int i = startPosition;
        while (i <= endPosition) {
            View child = obtainView(i, isScrap);
            measureScrapChild(child, i, widthMeasureSpec);
            if (i > 0) {
                returnedHeight += dividerHeight;
            }
            if (recyle && recycleBin.shouldRecycleViewType(((LayoutParams) child.getLayoutParams()).viewType)) {
                recycleBin.addScrapView(child, NO_POSITION);
            }
            returnedHeight += child.getMeasuredHeight();
            if (returnedHeight < maxHeight) {
                if (disallowPartialChildPosition >= 0 && i >= disallowPartialChildPosition) {
                    prevHeightWithoutPartialChild = returnedHeight;
                }
                i++;
            } else if (disallowPartialChildPosition < 0 || i <= disallowPartialChildPosition || prevHeightWithoutPartialChild <= 0 || returnedHeight == maxHeight) {
                return maxHeight;
            } else {
                return prevHeightWithoutPartialChild;
            }
        }
        return returnedHeight;
    }

    int findMotionRow(int y) {
        int childCount = getChildCount();
        if (childCount > 0) {
            int i;
            if (this.mStackFromBottom) {
                for (i = childCount + NO_POSITION; i >= 0; i += NO_POSITION) {
                    if (y >= getChildAt(i).getTop()) {
                        return this.mFirstPosition + i;
                    }
                }
            } else {
                for (i = 0; i < childCount; i++) {
                    if (y <= getChildAt(i).getBottom()) {
                        return this.mFirstPosition + i;
                    }
                }
            }
        }
        return NO_POSITION;
    }

    private View fillSpecific(int position, int top) {
        View below;
        View above;
        boolean tempIsSelected = position == this.mSelectedPosition;
        View temp = makeAndAddView(position, top, true, this.mListPadding.left, tempIsSelected);
        this.mFirstPosition = position;
        int dividerHeight = this.mDividerHeight;
        int childCount;
        if (this.mStackFromBottom) {
            below = fillDown(position + 1, temp.getBottom() + dividerHeight);
            adjustViewsUpOrDown();
            above = fillUp(position + NO_POSITION, temp.getTop() - dividerHeight);
            childCount = getChildCount();
            if (childCount > 0) {
                correctTooLow(childCount);
            }
        } else {
            above = fillUp(position + NO_POSITION, temp.getTop() - dividerHeight);
            adjustViewsUpOrDown();
            below = fillDown(position + 1, temp.getBottom() + dividerHeight);
            childCount = getChildCount();
            if (childCount > 0) {
                correctTooHigh(childCount);
            }
        }
        if (tempIsSelected) {
            return temp;
        }
        if (above != null) {
            return above;
        }
        return below;
    }

    private void correctTooHigh(int childCount) {
        if ((this.mFirstPosition + childCount) + NO_POSITION == this.mItemCount + NO_POSITION && childCount > 0) {
            int bottomOffset = ((this.mBottom - this.mTop) - this.mListPadding.bottom) - getChildAt(childCount + NO_POSITION).getBottom();
            View firstChild = getChildAt(0);
            int firstTop = firstChild.getTop();
            if (bottomOffset <= 0) {
                return;
            }
            if (this.mFirstPosition > 0 || firstTop < this.mListPadding.top) {
                if (this.mFirstPosition == 0) {
                    bottomOffset = Math.min(bottomOffset, this.mListPadding.top - firstTop);
                }
                offsetChildrenTopAndBottom(bottomOffset);
                if (this.mFirstPosition > 0) {
                    fillUp(this.mFirstPosition + NO_POSITION, firstChild.getTop() - this.mDividerHeight);
                    adjustViewsUpOrDown();
                }
            }
        }
    }

    private void correctTooLow(int childCount) {
        if (this.mFirstPosition == 0 && childCount > 0) {
            int end = (this.mBottom - this.mTop) - this.mListPadding.bottom;
            int topOffset = getChildAt(0).getTop() - this.mListPadding.top;
            View lastChild = getChildAt(childCount + NO_POSITION);
            int lastBottom = lastChild.getBottom();
            int lastPosition = (this.mFirstPosition + childCount) + NO_POSITION;
            if (topOffset <= 0) {
                return;
            }
            if (lastPosition < this.mItemCount + NO_POSITION || lastBottom > end) {
                if (lastPosition == this.mItemCount + NO_POSITION) {
                    topOffset = Math.min(topOffset, lastBottom - end);
                }
                offsetChildrenTopAndBottom(-topOffset);
                if (lastPosition < this.mItemCount + NO_POSITION) {
                    fillDown(lastPosition + 1, lastChild.getBottom() + this.mDividerHeight);
                    adjustViewsUpOrDown();
                }
            } else if (lastPosition == this.mItemCount + NO_POSITION) {
                adjustViewsUpOrDown();
            }
        }
    }

    private boolean isDirectChildHeaderOrFooter(View child) {
        int i;
        ArrayList<FixedViewInfo> headers = this.mHeaderViewInfos;
        int numHeaders = headers.size();
        for (i = 0; i < numHeaders; i++) {
            if (child == ((FixedViewInfo) headers.get(i)).view) {
                return true;
            }
        }
        ArrayList<FixedViewInfo> footers = this.mFooterViewInfos;
        int numFooters = footers.size();
        for (i = 0; i < numFooters; i++) {
            if (child == ((FixedViewInfo) footers.get(i)).view) {
                return true;
            }
        }
        return false;
    }

    private View makeAndAddView(int position, int y, boolean flow, int childrenLeft, boolean selected) {
        View child;
        if (!this.mDataChanged) {
            child = this.mRecycler.getActiveView(position);
            if (child != null) {
                setupChild(child, position, y, flow, childrenLeft, selected, true);
                return child;
            }
        }
        child = obtainView(position, this.mIsScrap);
        setupChild(child, position, y, flow, childrenLeft, selected, this.mIsScrap[0]);
        return child;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setupChild(android.view.View r23, int r24, int r25, boolean r26, int r27, boolean r28, boolean r29) {
        /*
        r22 = this;
        r20 = 8;
        r19 = "setupListItem";
        r0 = r20;
        r2 = r19;
        android.os.Trace.traceBegin(r0, r2);
        if (r28 == 0) goto L_0x0157;
    L_0x000e:
        r19 = r22.shouldShowSelector();
        if (r19 == 0) goto L_0x0157;
    L_0x0014:
        r11 = 1;
    L_0x0015:
        r19 = r23.isSelected();
        r0 = r19;
        if (r11 == r0) goto L_0x015a;
    L_0x001d:
        r17 = 1;
    L_0x001f:
        r0 = r22;
        r13 = r0.mTouchMode;
        if (r13 <= 0) goto L_0x015e;
    L_0x0025:
        r19 = 3;
        r0 = r19;
        if (r13 >= r0) goto L_0x015e;
    L_0x002b:
        r0 = r22;
        r0 = r0.mMotionPosition;
        r19 = r0;
        r0 = r19;
        r1 = r24;
        if (r0 != r1) goto L_0x015e;
    L_0x0037:
        r10 = 1;
    L_0x0038:
        r19 = r23.isPressed();
        r0 = r19;
        if (r10 == r0) goto L_0x0161;
    L_0x0040:
        r16 = 1;
    L_0x0042:
        if (r29 == 0) goto L_0x004c;
    L_0x0044:
        if (r17 != 0) goto L_0x004c;
    L_0x0046:
        r19 = r23.isLayoutRequested();
        if (r19 == 0) goto L_0x0165;
    L_0x004c:
        r14 = 1;
    L_0x004d:
        r15 = r23.getLayoutParams();
        r15 = (android.widget.AbsListView.LayoutParams) r15;
        if (r15 != 0) goto L_0x005b;
    L_0x0055:
        r15 = r22.generateDefaultLayoutParams();
        r15 = (android.widget.AbsListView.LayoutParams) r15;
    L_0x005b:
        r0 = r22;
        r0 = r0.mAdapter;
        r19 = r0;
        r0 = r19;
        r1 = r24;
        r19 = r0.getItemViewType(r1);
        r0 = r19;
        r15.viewType = r0;
        if (r29 == 0) goto L_0x0075;
    L_0x006f:
        r0 = r15.forceAdd;
        r19 = r0;
        if (r19 == 0) goto L_0x0087;
    L_0x0075:
        r0 = r15.recycledHeaderFooter;
        r19 = r0;
        if (r19 == 0) goto L_0x016c;
    L_0x007b:
        r0 = r15.viewType;
        r19 = r0;
        r20 = -2;
        r0 = r19;
        r1 = r20;
        if (r0 != r1) goto L_0x016c;
    L_0x0087:
        if (r26 == 0) goto L_0x0168;
    L_0x0089:
        r19 = -1;
    L_0x008b:
        r0 = r22;
        r1 = r23;
        r2 = r19;
        r0.attachViewToParent(r1, r2, r15);
    L_0x0094:
        if (r17 == 0) goto L_0x009b;
    L_0x0096:
        r0 = r23;
        r0.setSelected(r11);
    L_0x009b:
        if (r16 == 0) goto L_0x00a2;
    L_0x009d:
        r0 = r23;
        r0.setPressed(r10);
    L_0x00a2:
        r0 = r22;
        r0 = r0.mChoiceMode;
        r19 = r0;
        if (r19 == 0) goto L_0x00cf;
    L_0x00aa:
        r0 = r22;
        r0 = r0.mCheckStates;
        r19 = r0;
        if (r19 == 0) goto L_0x00cf;
    L_0x00b2:
        r0 = r23;
        r0 = r0 instanceof android.widget.Checkable;
        r19 = r0;
        if (r19 == 0) goto L_0x019a;
    L_0x00ba:
        r19 = r23;
        r19 = (android.widget.Checkable) r19;
        r0 = r22;
        r0 = r0.mCheckStates;
        r20 = r0;
        r0 = r20;
        r1 = r24;
        r20 = r0.get(r1);
        r19.setChecked(r20);
    L_0x00cf:
        if (r14 == 0) goto L_0x01d1;
    L_0x00d1:
        r0 = r22;
        r0 = r0.mWidthMeasureSpec;
        r19 = r0;
        r0 = r22;
        r0 = r0.mListPadding;
        r20 = r0;
        r0 = r20;
        r0 = r0.left;
        r20 = r0;
        r0 = r22;
        r0 = r0.mListPadding;
        r21 = r0;
        r0 = r21;
        r0 = r0.right;
        r21 = r0;
        r20 = r20 + r21;
        r0 = r15.width;
        r21 = r0;
        r8 = android.view.ViewGroup.getChildMeasureSpec(r19, r20, r21);
        r12 = r15.height;
        if (r12 <= 0) goto L_0x01c7;
    L_0x00fd:
        r19 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        r0 = r19;
        r5 = android.view.View.MeasureSpec.makeMeasureSpec(r12, r0);
    L_0x0105:
        r0 = r23;
        r0.measure(r8, r5);
    L_0x010a:
        r18 = r23.getMeasuredWidth();
        r9 = r23.getMeasuredHeight();
        if (r26 == 0) goto L_0x01d6;
    L_0x0114:
        r7 = r25;
    L_0x0116:
        if (r14 == 0) goto L_0x01da;
    L_0x0118:
        r6 = r27 + r18;
        r4 = r7 + r9;
        r0 = r23;
        r1 = r27;
        r0.layout(r1, r7, r6, r4);
    L_0x0123:
        r0 = r22;
        r0 = r0.mCachingStarted;
        r19 = r0;
        if (r19 == 0) goto L_0x013a;
    L_0x012b:
        r19 = r23.isDrawingCacheEnabled();
        if (r19 != 0) goto L_0x013a;
    L_0x0131:
        r19 = 1;
        r0 = r23;
        r1 = r19;
        r0.setDrawingCacheEnabled(r1);
    L_0x013a:
        if (r29 == 0) goto L_0x0151;
    L_0x013c:
        r19 = r23.getLayoutParams();
        r19 = (android.widget.AbsListView.LayoutParams) r19;
        r0 = r19;
        r0 = r0.scrappedFromPosition;
        r19 = r0;
        r0 = r19;
        r1 = r24;
        if (r0 == r1) goto L_0x0151;
    L_0x014e:
        r23.jumpDrawablesToCurrentState();
    L_0x0151:
        r20 = 8;
        android.os.Trace.traceEnd(r20);
        return;
    L_0x0157:
        r11 = 0;
        goto L_0x0015;
    L_0x015a:
        r17 = 0;
        goto L_0x001f;
    L_0x015e:
        r10 = 0;
        goto L_0x0038;
    L_0x0161:
        r16 = 0;
        goto L_0x0042;
    L_0x0165:
        r14 = 0;
        goto L_0x004d;
    L_0x0168:
        r19 = 0;
        goto L_0x008b;
    L_0x016c:
        r19 = 0;
        r0 = r19;
        r15.forceAdd = r0;
        r0 = r15.viewType;
        r19 = r0;
        r20 = -2;
        r0 = r19;
        r1 = r20;
        if (r0 != r1) goto L_0x0184;
    L_0x017e:
        r19 = 1;
        r0 = r19;
        r15.recycledHeaderFooter = r0;
    L_0x0184:
        if (r26 == 0) goto L_0x0197;
    L_0x0186:
        r19 = -1;
    L_0x0188:
        r20 = 1;
        r0 = r22;
        r1 = r23;
        r2 = r19;
        r3 = r20;
        r0.addViewInLayout(r1, r2, r15, r3);
        goto L_0x0094;
    L_0x0197:
        r19 = 0;
        goto L_0x0188;
    L_0x019a:
        r19 = r22.getContext();
        r19 = r19.getApplicationInfo();
        r0 = r19;
        r0 = r0.targetSdkVersion;
        r19 = r0;
        r20 = 11;
        r0 = r19;
        r1 = r20;
        if (r0 < r1) goto L_0x00cf;
    L_0x01b0:
        r0 = r22;
        r0 = r0.mCheckStates;
        r19 = r0;
        r0 = r19;
        r1 = r24;
        r19 = r0.get(r1);
        r0 = r23;
        r1 = r19;
        r0.setActivated(r1);
        goto L_0x00cf;
    L_0x01c7:
        r19 = 0;
        r20 = 0;
        r5 = android.view.View.MeasureSpec.makeMeasureSpec(r19, r20);
        goto L_0x0105;
    L_0x01d1:
        r22.cleanupLayoutState(r23);
        goto L_0x010a;
    L_0x01d6:
        r7 = r25 - r9;
        goto L_0x0116;
    L_0x01da:
        r19 = r23.getLeft();
        r19 = r27 - r19;
        r0 = r23;
        r1 = r19;
        r0.offsetLeftAndRight(r1);
        r19 = r23.getTop();
        r19 = r7 - r19;
        r0 = r23;
        r1 = r19;
        r0.offsetTopAndBottom(r1);
        goto L_0x0123;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.ListView.setupChild(android.view.View, int, int, boolean, int, boolean, boolean):void");
    }

    protected boolean canAnimate() {
        return super.canAnimate() && this.mItemCount > 0;
    }

    public void setSelection(int position) {
        setSelectionFromTop(position, 0);
    }

    void setSelectionInt(int position) {
        setNextSelectedPositionInt(position);
        boolean awakeScrollbars = false;
        int selectedPosition = this.mSelectedPosition;
        if (selectedPosition >= 0) {
            if (position == selectedPosition + NO_POSITION) {
                awakeScrollbars = true;
            } else if (position == selectedPosition + 1) {
                awakeScrollbars = true;
            }
        }
        if (this.mPositionScroller != null) {
            this.mPositionScroller.stop();
        }
        layoutChildren();
        if (awakeScrollbars) {
            awakenScrollBars();
        }
    }

    int lookForSelectablePosition(int position, boolean lookDown) {
        ListAdapter adapter = this.mAdapter;
        if (adapter == null || isInTouchMode()) {
            return NO_POSITION;
        }
        int count = adapter.getCount();
        if (!this.mAreAllItemsSelectable) {
            if (lookDown) {
                position = Math.max(0, position);
                while (position < count && !adapter.isEnabled(position)) {
                    position++;
                }
            } else {
                position = Math.min(position, count + NO_POSITION);
                while (position >= 0 && !adapter.isEnabled(position)) {
                    position += NO_POSITION;
                }
            }
        }
        if (position < 0 || position >= count) {
            return NO_POSITION;
        }
        return position;
    }

    int lookForSelectablePositionAfter(int current, int position, boolean lookDown) {
        ListAdapter adapter = this.mAdapter;
        if (adapter == null || isInTouchMode()) {
            return NO_POSITION;
        }
        int after = lookForSelectablePosition(position, lookDown);
        if (after != NO_POSITION) {
            return after;
        }
        int count = adapter.getCount();
        current = MathUtils.constrain(current, NO_POSITION, count + NO_POSITION);
        if (lookDown) {
            position = Math.min(position + NO_POSITION, count + NO_POSITION);
            while (position > current && !adapter.isEnabled(position)) {
                position += NO_POSITION;
            }
            if (position <= current) {
                return NO_POSITION;
            }
        }
        position = Math.max(0, position + 1);
        while (position < current && !adapter.isEnabled(position)) {
            position++;
        }
        if (position >= current) {
            return NO_POSITION;
        }
        return position;
    }

    public void setSelectionAfterHeaderView() {
        int count = this.mHeaderViewInfos.size();
        if (count > 0) {
            this.mNextSelectedPosition = 0;
        } else if (this.mAdapter != null) {
            setSelection(count);
        } else {
            this.mNextSelectedPosition = count;
            this.mLayoutMode = MIN_SCROLL_PREVIEW_PIXELS;
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean handled = super.dispatchKeyEvent(event);
        if (handled || getFocusedChild() == null || event.getAction() != 0) {
            return handled;
        }
        return onKeyDown(event.getKeyCode(), event);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return commonKey(keyCode, 1, event);
    }

    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return commonKey(keyCode, repeatCount, event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return commonKey(keyCode, 1, event);
    }

    private boolean commonKey(int keyCode, int count, KeyEvent event) {
        if (this.mAdapter == null || !isAttachedToWindow()) {
            return false;
        }
        if (this.mDataChanged) {
            layoutChildren();
        }
        boolean handled = false;
        int action = event.getAction();
        if (action != 1) {
            int count2;
            switch (keyCode) {
                case RelativeLayout.ALIGN_END /*19*/:
                    if (!event.hasNoModifiers()) {
                        if (event.hasModifiers(MIN_SCROLL_PREVIEW_PIXELS)) {
                            handled = resurrectSelectionIfNeeded() || fullScroll(33);
                            break;
                        }
                    }
                    handled = resurrectSelectionIfNeeded();
                    if (!handled) {
                        count2 = count;
                        while (true) {
                            count = count2 + NO_POSITION;
                            if (count2 > 0 && arrowScroll(33)) {
                                handled = true;
                                count2 = count;
                            }
                        }
                    }
                    break;
                case RelativeLayout.ALIGN_PARENT_START /*20*/:
                    if (!event.hasNoModifiers()) {
                        if (event.hasModifiers(MIN_SCROLL_PREVIEW_PIXELS)) {
                            handled = resurrectSelectionIfNeeded() || fullScroll(KeyEvent.KEYCODE_MEDIA_RECORD);
                            break;
                        }
                    }
                    handled = resurrectSelectionIfNeeded();
                    if (!handled) {
                        count2 = count;
                        while (true) {
                            count = count2 + NO_POSITION;
                            if (count2 > 0 && arrowScroll(KeyEvent.KEYCODE_MEDIA_RECORD)) {
                                handled = true;
                                count2 = count;
                            }
                        }
                    }
                    break;
                case RelativeLayout.ALIGN_PARENT_END /*21*/:
                    if (event.hasNoModifiers()) {
                        handled = handleHorizontalFocusWithinListItem(17);
                        break;
                    }
                    break;
                case MotionEvent.AXIS_GAS /*22*/:
                    if (event.hasNoModifiers()) {
                        handled = handleHorizontalFocusWithinListItem(66);
                        break;
                    }
                    break;
                case MotionEvent.AXIS_BRAKE /*23*/:
                case KeyEvent.KEYCODE_ENTER /*66*/:
                    if (event.hasNoModifiers()) {
                        handled = resurrectSelectionIfNeeded();
                        if (!handled && event.getRepeatCount() == 0 && getChildCount() > 0) {
                            keyPressed();
                            handled = true;
                            break;
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_SPACE /*62*/:
                    if (this.mPopup == null || !this.mPopup.isShowing()) {
                        if (event.hasNoModifiers()) {
                            if (resurrectSelectionIfNeeded() || pageScroll(KeyEvent.KEYCODE_MEDIA_RECORD)) {
                                handled = true;
                            } else {
                                handled = false;
                            }
                        } else if (event.hasModifiers(1)) {
                            if (resurrectSelectionIfNeeded() || pageScroll(33)) {
                                handled = true;
                            } else {
                                handled = false;
                            }
                        }
                        handled = true;
                        break;
                    }
                    break;
                case KeyEvent.KEYCODE_PAGE_UP /*92*/:
                    if (!event.hasNoModifiers()) {
                        if (event.hasModifiers(MIN_SCROLL_PREVIEW_PIXELS)) {
                            handled = resurrectSelectionIfNeeded() || fullScroll(33);
                            break;
                        }
                    }
                    handled = resurrectSelectionIfNeeded() || pageScroll(33);
                    break;
                    break;
                case KeyEvent.KEYCODE_PAGE_DOWN /*93*/:
                    if (!event.hasNoModifiers()) {
                        if (event.hasModifiers(MIN_SCROLL_PREVIEW_PIXELS)) {
                            handled = resurrectSelectionIfNeeded() || fullScroll(KeyEvent.KEYCODE_MEDIA_RECORD);
                            break;
                        }
                    }
                    handled = resurrectSelectionIfNeeded() || pageScroll(KeyEvent.KEYCODE_MEDIA_RECORD);
                    break;
                    break;
                case KeyEvent.KEYCODE_MOVE_HOME /*122*/:
                    if (event.hasNoModifiers()) {
                        handled = resurrectSelectionIfNeeded() || fullScroll(33);
                        break;
                    }
                    break;
                case KeyEvent.KEYCODE_MOVE_END /*123*/:
                    if (event.hasNoModifiers()) {
                        handled = resurrectSelectionIfNeeded() || fullScroll(KeyEvent.KEYCODE_MEDIA_RECORD);
                        break;
                    }
                    break;
            }
        }
        if (handled || sendToTextFilter(keyCode, count, event)) {
            return true;
        }
        switch (action) {
            case Toast.LENGTH_SHORT /*0*/:
                return super.onKeyDown(keyCode, event);
            case Toast.LENGTH_LONG /*1*/:
                return super.onKeyUp(keyCode, event);
            case MIN_SCROLL_PREVIEW_PIXELS /*2*/:
                return super.onKeyMultiple(keyCode, count, event);
            default:
                return false;
        }
    }

    boolean pageScroll(int direction) {
        int nextPage;
        boolean down;
        if (direction == 33) {
            nextPage = Math.max(0, (this.mSelectedPosition - getChildCount()) + NO_POSITION);
            down = false;
        } else if (direction != KeyEvent.KEYCODE_MEDIA_RECORD) {
            return false;
        } else {
            nextPage = Math.min(this.mItemCount + NO_POSITION, (this.mSelectedPosition + getChildCount()) + NO_POSITION);
            down = true;
        }
        if (nextPage < 0) {
            return false;
        }
        int position = lookForSelectablePositionAfter(this.mSelectedPosition, nextPage, down);
        if (position < 0) {
            return false;
        }
        this.mLayoutMode = 4;
        this.mSpecificTop = this.mPaddingTop + getVerticalFadingEdgeLength();
        if (down && position > this.mItemCount - getChildCount()) {
            this.mLayoutMode = 3;
        }
        if (!down && position < getChildCount()) {
            this.mLayoutMode = 1;
        }
        setSelectionInt(position);
        invokeOnItemScrollListener();
        if (!awakenScrollBars()) {
            invalidate();
        }
        return true;
    }

    boolean fullScroll(int direction) {
        boolean moved = false;
        int position;
        if (direction == 33) {
            if (this.mSelectedPosition != 0) {
                position = lookForSelectablePositionAfter(this.mSelectedPosition, 0, true);
                if (position >= 0) {
                    this.mLayoutMode = 1;
                    setSelectionInt(position);
                    invokeOnItemScrollListener();
                }
                moved = true;
            }
        } else if (direction == KeyEvent.KEYCODE_MEDIA_RECORD) {
            int lastItem = this.mItemCount + NO_POSITION;
            if (this.mSelectedPosition < lastItem) {
                position = lookForSelectablePositionAfter(this.mSelectedPosition, lastItem, false);
                if (position >= 0) {
                    this.mLayoutMode = 3;
                    setSelectionInt(position);
                    invokeOnItemScrollListener();
                }
                moved = true;
            }
        }
        if (moved && !awakenScrollBars()) {
            awakenScrollBars();
            invalidate();
        }
        return moved;
    }

    private boolean handleHorizontalFocusWithinListItem(int direction) {
        if (direction == 17 || direction == 66) {
            int numChildren = getChildCount();
            if (this.mItemsCanFocus && numChildren > 0 && this.mSelectedPosition != NO_POSITION) {
                View selectedView = getSelectedView();
                if (selectedView != null && selectedView.hasFocus() && (selectedView instanceof ViewGroup)) {
                    View currentFocus = selectedView.findFocus();
                    View nextFocus = FocusFinder.getInstance().findNextFocus((ViewGroup) selectedView, currentFocus, direction);
                    if (nextFocus != null) {
                        currentFocus.getFocusedRect(this.mTempRect);
                        offsetDescendantRectToMyCoords(currentFocus, this.mTempRect);
                        offsetRectIntoDescendantCoords(nextFocus, this.mTempRect);
                        if (nextFocus.requestFocus(direction, this.mTempRect)) {
                            return true;
                        }
                    }
                    View globalNextFocus = FocusFinder.getInstance().findNextFocus((ViewGroup) getRootView(), currentFocus, direction);
                    if (globalNextFocus != null) {
                        return isViewAncestorOf(globalNextFocus, this);
                    }
                }
            }
            return false;
        }
        throw new IllegalArgumentException("direction must be one of {View.FOCUS_LEFT, View.FOCUS_RIGHT}");
    }

    boolean arrowScroll(int direction) {
        try {
            this.mInLayout = true;
            boolean handled = arrowScrollImpl(direction);
            if (handled) {
                playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
            }
            this.mInLayout = false;
            return handled;
        } catch (Throwable th) {
            this.mInLayout = false;
        }
    }

    private final int nextSelectedPositionForDirection(View selectedView, int selectedPos, int direction) {
        int nextSelected;
        if (direction == KeyEvent.KEYCODE_MEDIA_RECORD) {
            int listBottom = getHeight() - this.mListPadding.bottom;
            if (selectedView == null || selectedView.getBottom() > listBottom) {
                return NO_POSITION;
            }
            if (selectedPos == NO_POSITION || selectedPos < this.mFirstPosition) {
                nextSelected = this.mFirstPosition;
            } else {
                nextSelected = selectedPos + 1;
            }
        } else {
            int listTop = this.mListPadding.top;
            if (selectedView == null || selectedView.getTop() < listTop) {
                return NO_POSITION;
            }
            int lastPos = (this.mFirstPosition + getChildCount()) + NO_POSITION;
            if (selectedPos == NO_POSITION || selectedPos > lastPos) {
                nextSelected = lastPos;
            } else {
                nextSelected = selectedPos + NO_POSITION;
            }
        }
        if (nextSelected < 0 || nextSelected >= this.mAdapter.getCount()) {
            return NO_POSITION;
        }
        return lookForSelectablePosition(nextSelected, direction == KeyEvent.KEYCODE_MEDIA_RECORD);
    }

    private boolean arrowScrollImpl(int direction) {
        if (getChildCount() <= 0) {
            return false;
        }
        boolean needToRedraw;
        View focused;
        View selectedView = getSelectedView();
        int selectedPos = this.mSelectedPosition;
        int nextSelectedPosition = nextSelectedPositionForDirection(selectedView, selectedPos, direction);
        int amountToScroll = amountToScroll(direction, nextSelectedPosition);
        ArrowScrollFocusResult focusResult = this.mItemsCanFocus ? arrowScrollFocused(direction) : null;
        if (focusResult != null) {
            nextSelectedPosition = focusResult.getSelectedPosition();
            amountToScroll = focusResult.getAmountToScroll();
        }
        if (focusResult != null) {
            needToRedraw = true;
        } else {
            needToRedraw = false;
        }
        if (nextSelectedPosition != NO_POSITION) {
            boolean z;
            if (focusResult != null) {
                z = true;
            } else {
                z = false;
            }
            handleNewSelectionChange(selectedView, direction, nextSelectedPosition, z);
            setSelectedPositionInt(nextSelectedPosition);
            setNextSelectedPositionInt(nextSelectedPosition);
            selectedView = getSelectedView();
            selectedPos = nextSelectedPosition;
            if (this.mItemsCanFocus && focusResult == null) {
                focused = getFocusedChild();
                if (focused != null) {
                    focused.clearFocus();
                }
            }
            needToRedraw = true;
            checkSelectionChanged();
        }
        if (amountToScroll > 0) {
            if (direction != 33) {
                amountToScroll = -amountToScroll;
            }
            scrollListItemsBy(amountToScroll);
            needToRedraw = true;
        }
        if (this.mItemsCanFocus && focusResult == null && selectedView != null && selectedView.hasFocus()) {
            focused = selectedView.findFocus();
            if (!isViewAncestorOf(focused, this) || distanceToView(focused) > 0) {
                focused.clearFocus();
            }
        }
        if (!(nextSelectedPosition != NO_POSITION || selectedView == null || isViewAncestorOf(selectedView, this))) {
            selectedView = null;
            hideSelector();
            this.mResurrectToPosition = NO_POSITION;
        }
        if (!needToRedraw) {
            return false;
        }
        if (selectedView != null) {
            positionSelectorLikeFocus(selectedPos, selectedView);
            this.mSelectedTop = selectedView.getTop();
        }
        if (!awakenScrollBars()) {
            invalidate();
        }
        invokeOnItemScrollListener();
        return true;
    }

    private void handleNewSelectionChange(View selectedView, int direction, int newSelectedPosition, boolean newFocusAssigned) {
        if (newSelectedPosition == NO_POSITION) {
            throw new IllegalArgumentException("newSelectedPosition needs to be valid");
        }
        int topViewIndex;
        int bottomViewIndex;
        View topView;
        View bottomView;
        boolean topSelected = false;
        int selectedIndex = this.mSelectedPosition - this.mFirstPosition;
        int nextSelectedIndex = newSelectedPosition - this.mFirstPosition;
        if (direction == 33) {
            topViewIndex = nextSelectedIndex;
            bottomViewIndex = selectedIndex;
            topView = getChildAt(topViewIndex);
            bottomView = selectedView;
            topSelected = true;
        } else {
            topViewIndex = selectedIndex;
            bottomViewIndex = nextSelectedIndex;
            topView = selectedView;
            bottomView = getChildAt(bottomViewIndex);
        }
        int numChildren = getChildCount();
        if (topView != null) {
            boolean z = !newFocusAssigned && topSelected;
            topView.setSelected(z);
            measureAndAdjustDown(topView, topViewIndex, numChildren);
        }
        if (bottomView != null) {
            z = (newFocusAssigned || topSelected) ? false : true;
            bottomView.setSelected(z);
            measureAndAdjustDown(bottomView, bottomViewIndex, numChildren);
        }
    }

    private void measureAndAdjustDown(View child, int childIndex, int numChildren) {
        int oldHeight = child.getHeight();
        measureItem(child);
        if (child.getMeasuredHeight() != oldHeight) {
            relayoutMeasuredItem(child);
            int heightDelta = child.getMeasuredHeight() - oldHeight;
            for (int i = childIndex + 1; i < numChildren; i++) {
                getChildAt(i).offsetTopAndBottom(heightDelta);
            }
        }
    }

    private void measureItem(View child) {
        int childHeightSpec;
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams((int) NO_POSITION, -2);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(this.mWidthMeasureSpec, this.mListPadding.left + this.mListPadding.right, p.width);
        int lpHeight = p.height;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, 0);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    private void relayoutMeasuredItem(View child) {
        int w = child.getMeasuredWidth();
        int h = child.getMeasuredHeight();
        int childLeft = this.mListPadding.left;
        int childRight = childLeft + w;
        int childTop = child.getTop();
        child.layout(childLeft, childTop, childRight, childTop + h);
    }

    private int getArrowScrollPreviewLength() {
        return Math.max(MIN_SCROLL_PREVIEW_PIXELS, getVerticalFadingEdgeLength());
    }

    private int amountToScroll(int direction, int nextSelectedPosition) {
        int listBottom = getHeight() - this.mListPadding.bottom;
        int listTop = this.mListPadding.top;
        int numChildren = getChildCount();
        int indexToMakeVisible;
        int positionToMakeVisible;
        View viewToMakeVisible;
        int amountToScroll;
        if (direction == KeyEvent.KEYCODE_MEDIA_RECORD) {
            indexToMakeVisible = numChildren + NO_POSITION;
            if (nextSelectedPosition != NO_POSITION) {
                indexToMakeVisible = nextSelectedPosition - this.mFirstPosition;
            }
            while (numChildren <= indexToMakeVisible) {
                addViewBelow(getChildAt(numChildren + NO_POSITION), (this.mFirstPosition + numChildren) + NO_POSITION);
                numChildren++;
            }
            positionToMakeVisible = this.mFirstPosition + indexToMakeVisible;
            viewToMakeVisible = getChildAt(indexToMakeVisible);
            int goalBottom = listBottom;
            if (positionToMakeVisible < this.mItemCount + NO_POSITION) {
                goalBottom -= getArrowScrollPreviewLength();
            }
            if (viewToMakeVisible.getBottom() <= goalBottom) {
                return 0;
            }
            if (nextSelectedPosition != NO_POSITION && goalBottom - viewToMakeVisible.getTop() >= getMaxScrollAmount()) {
                return 0;
            }
            amountToScroll = viewToMakeVisible.getBottom() - goalBottom;
            if (this.mFirstPosition + numChildren == this.mItemCount) {
                amountToScroll = Math.min(amountToScroll, getChildAt(numChildren + NO_POSITION).getBottom() - listBottom);
            }
            return Math.min(amountToScroll, getMaxScrollAmount());
        }
        indexToMakeVisible = 0;
        if (nextSelectedPosition != NO_POSITION) {
            indexToMakeVisible = nextSelectedPosition - this.mFirstPosition;
        }
        while (indexToMakeVisible < 0) {
            addViewAbove(getChildAt(0), this.mFirstPosition);
            this.mFirstPosition += NO_POSITION;
            indexToMakeVisible = nextSelectedPosition - this.mFirstPosition;
        }
        positionToMakeVisible = this.mFirstPosition + indexToMakeVisible;
        viewToMakeVisible = getChildAt(indexToMakeVisible);
        int goalTop = listTop;
        if (positionToMakeVisible > 0) {
            goalTop += getArrowScrollPreviewLength();
        }
        if (viewToMakeVisible.getTop() >= goalTop) {
            return 0;
        }
        if (nextSelectedPosition != NO_POSITION && viewToMakeVisible.getBottom() - goalTop >= getMaxScrollAmount()) {
            return 0;
        }
        amountToScroll = goalTop - viewToMakeVisible.getTop();
        if (this.mFirstPosition == 0) {
            amountToScroll = Math.min(amountToScroll, listTop - getChildAt(0).getTop());
        }
        return Math.min(amountToScroll, getMaxScrollAmount());
    }

    private int lookForSelectablePositionOnScreen(int direction) {
        int firstPosition = this.mFirstPosition;
        int startPos;
        ListAdapter adapter;
        int pos;
        if (direction == KeyEvent.KEYCODE_MEDIA_RECORD) {
            if (this.mSelectedPosition != NO_POSITION) {
                startPos = this.mSelectedPosition + 1;
            } else {
                startPos = firstPosition;
            }
            if (startPos >= this.mAdapter.getCount()) {
                return NO_POSITION;
            }
            if (startPos < firstPosition) {
                startPos = firstPosition;
            }
            int lastVisiblePos = getLastVisiblePosition();
            adapter = getAdapter();
            pos = startPos;
            while (pos <= lastVisiblePos) {
                if (adapter.isEnabled(pos) && getChildAt(pos - firstPosition).getVisibility() == 0) {
                    return pos;
                }
                pos++;
            }
        } else {
            int last = (getChildCount() + firstPosition) + NO_POSITION;
            startPos = this.mSelectedPosition != NO_POSITION ? this.mSelectedPosition + NO_POSITION : (getChildCount() + firstPosition) + NO_POSITION;
            if (startPos < 0 || startPos >= this.mAdapter.getCount()) {
                return NO_POSITION;
            }
            if (startPos > last) {
                startPos = last;
            }
            adapter = getAdapter();
            pos = startPos;
            while (pos >= firstPosition) {
                if (adapter.isEnabled(pos) && getChildAt(pos - firstPosition).getVisibility() == 0) {
                    return pos;
                }
                pos += NO_POSITION;
            }
        }
        return NO_POSITION;
    }

    private ArrowScrollFocusResult arrowScrollFocused(int direction) {
        View newFocus;
        View selectedView = getSelectedView();
        if (selectedView == null || !selectedView.hasFocus()) {
            int ySearchPoint;
            if (direction == 130) {
                int listTop = this.mListPadding.top + (this.mFirstPosition > 0 ? getArrowScrollPreviewLength() : 0);
                if (selectedView == null || selectedView.getTop() <= listTop) {
                    ySearchPoint = listTop;
                } else {
                    ySearchPoint = selectedView.getTop();
                }
                this.mTempRect.set(0, ySearchPoint, 0, ySearchPoint);
            } else {
                int listBottom = (getHeight() - this.mListPadding.bottom) - ((this.mFirstPosition + getChildCount()) + NO_POSITION < this.mItemCount ? getArrowScrollPreviewLength() : 0);
                if (selectedView == null || selectedView.getBottom() >= listBottom) {
                    ySearchPoint = listBottom;
                } else {
                    ySearchPoint = selectedView.getBottom();
                }
                this.mTempRect.set(0, ySearchPoint, 0, ySearchPoint);
            }
            newFocus = FocusFinder.getInstance().findNextFocusFromRect(this, this.mTempRect, direction);
        } else {
            newFocus = FocusFinder.getInstance().findNextFocus(this, selectedView.findFocus(), direction);
        }
        if (newFocus != null) {
            int positionOfNewFocus = positionOfNewFocus(newFocus);
            if (!(this.mSelectedPosition == NO_POSITION || positionOfNewFocus == this.mSelectedPosition)) {
                int selectablePosition = lookForSelectablePositionOnScreen(direction);
                if (selectablePosition != NO_POSITION && ((direction == 130 && selectablePosition < positionOfNewFocus) || (direction == 33 && selectablePosition > positionOfNewFocus))) {
                    return null;
                }
            }
            int focusScroll = amountToScrollToNewFocus(direction, newFocus, positionOfNewFocus);
            int maxScrollAmount = getMaxScrollAmount();
            if (focusScroll < maxScrollAmount) {
                newFocus.requestFocus(direction);
                this.mArrowScrollFocusResult.populate(positionOfNewFocus, focusScroll);
                return this.mArrowScrollFocusResult;
            } else if (distanceToView(newFocus) < maxScrollAmount) {
                newFocus.requestFocus(direction);
                this.mArrowScrollFocusResult.populate(positionOfNewFocus, maxScrollAmount);
                return this.mArrowScrollFocusResult;
            }
        }
        return null;
    }

    private int positionOfNewFocus(View newFocus) {
        int numChildren = getChildCount();
        for (int i = 0; i < numChildren; i++) {
            if (isViewAncestorOf(newFocus, getChildAt(i))) {
                return this.mFirstPosition + i;
            }
        }
        throw new IllegalArgumentException("newFocus is not a child of any of the children of the list!");
    }

    private boolean isViewAncestorOf(View child, View parent) {
        if (child == parent) {
            return true;
        }
        ViewParent theParent = child.getParent();
        if ((theParent instanceof ViewGroup) && isViewAncestorOf((View) theParent, parent)) {
            return true;
        }
        return false;
    }

    private int amountToScrollToNewFocus(int direction, View newFocus, int positionOfNewFocus) {
        newFocus.getDrawingRect(this.mTempRect);
        offsetDescendantRectToMyCoords(newFocus, this.mTempRect);
        int amountToScroll;
        if (direction != 33) {
            int listBottom = getHeight() - this.mListPadding.bottom;
            if (this.mTempRect.bottom <= listBottom) {
                return 0;
            }
            amountToScroll = this.mTempRect.bottom - listBottom;
            if (positionOfNewFocus < this.mItemCount + NO_POSITION) {
                return amountToScroll + getArrowScrollPreviewLength();
            }
            return amountToScroll;
        } else if (this.mTempRect.top >= this.mListPadding.top) {
            return 0;
        } else {
            amountToScroll = this.mListPadding.top - this.mTempRect.top;
            return positionOfNewFocus > 0 ? amountToScroll + getArrowScrollPreviewLength() : amountToScroll;
        }
    }

    private int distanceToView(View descendant) {
        descendant.getDrawingRect(this.mTempRect);
        offsetDescendantRectToMyCoords(descendant, this.mTempRect);
        int listBottom = (this.mBottom - this.mTop) - this.mListPadding.bottom;
        if (this.mTempRect.bottom < this.mListPadding.top) {
            return this.mListPadding.top - this.mTempRect.bottom;
        }
        if (this.mTempRect.top > listBottom) {
            return this.mTempRect.top - listBottom;
        }
        return 0;
    }

    private void scrollListItemsBy(int amount) {
        offsetChildrenTopAndBottom(amount);
        int listBottom = getHeight() - this.mListPadding.bottom;
        int listTop = this.mListPadding.top;
        RecycleBin recycleBin = this.mRecycler;
        View last;
        View first;
        if (amount < 0) {
            int numChildren = getChildCount();
            last = getChildAt(numChildren + NO_POSITION);
            while (last.getBottom() < listBottom) {
                int lastVisiblePosition = (this.mFirstPosition + numChildren) + NO_POSITION;
                if (lastVisiblePosition >= this.mItemCount + NO_POSITION) {
                    break;
                }
                last = addViewBelow(last, lastVisiblePosition);
                numChildren++;
            }
            if (last.getBottom() < listBottom) {
                offsetChildrenTopAndBottom(listBottom - last.getBottom());
            }
            first = getChildAt(0);
            while (first.getBottom() < listTop) {
                if (recycleBin.shouldRecycleViewType(((LayoutParams) first.getLayoutParams()).viewType)) {
                    recycleBin.addScrapView(first, this.mFirstPosition);
                }
                detachViewFromParent(first);
                first = getChildAt(0);
                this.mFirstPosition++;
            }
            return;
        }
        first = getChildAt(0);
        while (first.getTop() > listTop && this.mFirstPosition > 0) {
            first = addViewAbove(first, this.mFirstPosition);
            this.mFirstPosition += NO_POSITION;
        }
        if (first.getTop() > listTop) {
            offsetChildrenTopAndBottom(listTop - first.getTop());
        }
        int lastIndex = getChildCount() + NO_POSITION;
        last = getChildAt(lastIndex);
        while (last.getTop() > listBottom) {
            if (recycleBin.shouldRecycleViewType(((LayoutParams) last.getLayoutParams()).viewType)) {
                recycleBin.addScrapView(last, this.mFirstPosition + lastIndex);
            }
            detachViewFromParent(last);
            lastIndex += NO_POSITION;
            last = getChildAt(lastIndex);
        }
    }

    private View addViewAbove(View theView, int position) {
        int abovePosition = position + NO_POSITION;
        View view = obtainView(abovePosition, this.mIsScrap);
        setupChild(view, abovePosition, theView.getTop() - this.mDividerHeight, false, this.mListPadding.left, false, this.mIsScrap[0]);
        return view;
    }

    private View addViewBelow(View theView, int position) {
        int belowPosition = position + 1;
        View view = obtainView(belowPosition, this.mIsScrap);
        setupChild(view, belowPosition, theView.getBottom() + this.mDividerHeight, true, this.mListPadding.left, false, this.mIsScrap[0]);
        return view;
    }

    public void setItemsCanFocus(boolean itemsCanFocus) {
        this.mItemsCanFocus = itemsCanFocus;
        if (!itemsCanFocus) {
            setDescendantFocusability(DevicePolicyManager.PASSWORD_QUALITY_COMPLEX);
        }
    }

    public boolean getItemsCanFocus() {
        return this.mItemsCanFocus;
    }

    public boolean isOpaque() {
        boolean retValue = (this.mCachingActive && this.mIsCacheColorOpaque && this.mDividerIsOpaque && hasOpaqueScrollbars()) || super.isOpaque();
        if (!retValue) {
            return retValue;
        }
        int listTop = this.mListPadding != null ? this.mListPadding.top : this.mPaddingTop;
        View first = getChildAt(0);
        if (first == null || first.getTop() > listTop) {
            return false;
        }
        int listBottom = getHeight() - (this.mListPadding != null ? this.mListPadding.bottom : this.mPaddingBottom);
        View last = getChildAt(getChildCount() + NO_POSITION);
        if (last == null || last.getBottom() < listBottom) {
            return false;
        }
        return retValue;
    }

    public void setCacheColorHint(int color) {
        boolean opaque = (color >>> 24) == EditorInfo.IME_MASK_ACTION;
        this.mIsCacheColorOpaque = opaque;
        if (opaque) {
            if (this.mDividerPaint == null) {
                this.mDividerPaint = new Paint();
            }
            this.mDividerPaint.setColor(color);
        }
        super.setCacheColorHint(color);
    }

    void drawOverscrollHeader(Canvas canvas, Drawable drawable, Rect bounds) {
        int height = drawable.getMinimumHeight();
        canvas.save();
        canvas.clipRect(bounds);
        if (bounds.bottom - bounds.top < height) {
            bounds.top = bounds.bottom - height;
        }
        drawable.setBounds(bounds);
        drawable.draw(canvas);
        canvas.restore();
    }

    void drawOverscrollFooter(Canvas canvas, Drawable drawable, Rect bounds) {
        int height = drawable.getMinimumHeight();
        canvas.save();
        canvas.clipRect(bounds);
        if (bounds.bottom - bounds.top < height) {
            bounds.bottom = bounds.top + height;
        }
        drawable.setBounds(bounds);
        drawable.draw(canvas);
        canvas.restore();
    }

    protected void dispatchDraw(Canvas canvas) {
        if (this.mCachingStarted) {
            this.mCachingActive = true;
        }
        int dividerHeight = this.mDividerHeight;
        Drawable overscrollHeader = this.mOverScrollHeader;
        Drawable overscrollFooter = this.mOverScrollFooter;
        boolean drawOverscrollHeader = overscrollHeader != null;
        boolean drawOverscrollFooter = overscrollFooter != null;
        boolean drawDividers = dividerHeight > 0 && this.mDivider != null;
        if (drawDividers || drawOverscrollHeader || drawOverscrollFooter) {
            Rect bounds = this.mTempRect;
            bounds.left = this.mPaddingLeft;
            bounds.right = (this.mRight - this.mLeft) - this.mPaddingRight;
            int count = getChildCount();
            int headerCount = this.mHeaderViewInfos.size();
            int itemCount = this.mItemCount;
            int footerLimit = itemCount - this.mFooterViewInfos.size();
            boolean headerDividers = this.mHeaderDividersEnabled;
            boolean footerDividers = this.mFooterDividersEnabled;
            int first = this.mFirstPosition;
            boolean areAllItemsSelectable = this.mAreAllItemsSelectable;
            ListAdapter adapter = this.mAdapter;
            boolean fillForMissingDividers = isOpaque() && !super.isOpaque();
            if (fillForMissingDividers && this.mDividerPaint == null && this.mIsCacheColorOpaque) {
                this.mDividerPaint = new Paint();
                this.mDividerPaint.setColor(getCacheColorHint());
            }
            Paint paint = this.mDividerPaint;
            int effectivePaddingTop = 0;
            int effectivePaddingBottom = 0;
            if ((this.mGroupFlags & 34) == 34) {
                effectivePaddingTop = this.mListPadding.top;
                effectivePaddingBottom = this.mListPadding.bottom;
            }
            int listBottom = ((this.mBottom - this.mTop) - effectivePaddingBottom) + this.mScrollY;
            int scrollY;
            int i;
            int itemIndex;
            boolean isHeader;
            boolean isFooter;
            if (this.mStackFromBottom) {
                scrollY = this.mScrollY;
                if (count > 0 && drawOverscrollHeader) {
                    bounds.top = scrollY;
                    bounds.bottom = getChildAt(0).getTop();
                    drawOverscrollHeader(canvas, overscrollHeader, bounds);
                }
                int start = drawOverscrollHeader ? 1 : 0;
                i = start;
                while (i < count) {
                    itemIndex = first + i;
                    isHeader = itemIndex < headerCount;
                    isFooter = itemIndex >= footerLimit;
                    if ((headerDividers || !isHeader) && (footerDividers || !isFooter)) {
                        int top = getChildAt(i).getTop();
                        if (drawDividers && top > effectivePaddingTop) {
                            boolean isFirstItem = i == start;
                            int previousIndex = itemIndex + NO_POSITION;
                            if (adapter.isEnabled(itemIndex) && ((headerDividers || (!isHeader && previousIndex >= headerCount)) && (isFirstItem || (adapter.isEnabled(previousIndex) && (footerDividers || (!isFooter && previousIndex < footerLimit)))))) {
                                bounds.top = top - dividerHeight;
                                bounds.bottom = top;
                                drawDivider(canvas, bounds, i + NO_POSITION);
                            } else if (fillForMissingDividers) {
                                bounds.top = top - dividerHeight;
                                bounds.bottom = top;
                                canvas.drawRect(bounds, paint);
                            }
                        }
                    }
                    i++;
                }
                if (count > 0 && scrollY > 0) {
                    if (drawOverscrollFooter) {
                        int absListBottom = this.mBottom;
                        bounds.top = absListBottom;
                        bounds.bottom = absListBottom + scrollY;
                        drawOverscrollFooter(canvas, overscrollFooter, bounds);
                    } else if (drawDividers) {
                        bounds.top = listBottom;
                        bounds.bottom = listBottom + dividerHeight;
                        drawDivider(canvas, bounds, NO_POSITION);
                    }
                }
            } else {
                int bottom = 0;
                scrollY = this.mScrollY;
                if (count > 0 && scrollY < 0) {
                    if (drawOverscrollHeader) {
                        bounds.bottom = 0;
                        bounds.top = scrollY;
                        drawOverscrollHeader(canvas, overscrollHeader, bounds);
                    } else if (drawDividers) {
                        bounds.bottom = 0;
                        bounds.top = -dividerHeight;
                        drawDivider(canvas, bounds, NO_POSITION);
                    }
                }
                i = 0;
                while (i < count) {
                    itemIndex = first + i;
                    isHeader = itemIndex < headerCount;
                    isFooter = itemIndex >= footerLimit;
                    if ((headerDividers || !isHeader) && (footerDividers || !isFooter)) {
                        bottom = getChildAt(i).getBottom();
                        boolean isLastItem = i == count + NO_POSITION;
                        if (drawDividers && bottom < listBottom && !(drawOverscrollFooter && isLastItem)) {
                            int nextIndex = itemIndex + 1;
                            if (adapter.isEnabled(itemIndex) && ((headerDividers || (!isHeader && nextIndex >= headerCount)) && (isLastItem || (adapter.isEnabled(nextIndex) && (footerDividers || (!isFooter && nextIndex < footerLimit)))))) {
                                bounds.top = bottom;
                                bounds.bottom = bottom + dividerHeight;
                                drawDivider(canvas, bounds, i);
                            } else if (fillForMissingDividers) {
                                bounds.top = bottom;
                                bounds.bottom = bottom + dividerHeight;
                                canvas.drawRect(bounds, paint);
                            }
                        }
                    }
                    i++;
                }
                int overFooterBottom = this.mBottom + this.mScrollY;
                if (drawOverscrollFooter && first + count == itemCount && overFooterBottom > bottom) {
                    bounds.top = bottom;
                    bounds.bottom = overFooterBottom;
                    drawOverscrollFooter(canvas, overscrollFooter, bounds);
                }
            }
        }
        super.dispatchDraw(canvas);
    }

    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean more = super.drawChild(canvas, child, drawingTime);
        if (this.mCachingActive && child.mCachingFailed) {
            this.mCachingActive = false;
        }
        return more;
    }

    void drawDivider(Canvas canvas, Rect bounds, int childIndex) {
        Drawable divider = this.mDivider;
        divider.setBounds(bounds);
        divider.draw(canvas);
    }

    public Drawable getDivider() {
        return this.mDivider;
    }

    public void setDivider(Drawable divider) {
        boolean z = false;
        if (divider != null) {
            this.mDividerHeight = divider.getIntrinsicHeight();
        } else {
            this.mDividerHeight = 0;
        }
        this.mDivider = divider;
        if (divider == null || divider.getOpacity() == NO_POSITION) {
            z = true;
        }
        this.mDividerIsOpaque = z;
        requestLayout();
        invalidate();
    }

    public int getDividerHeight() {
        return this.mDividerHeight;
    }

    public void setDividerHeight(int height) {
        this.mDividerHeight = height;
        requestLayout();
        invalidate();
    }

    public void setHeaderDividersEnabled(boolean headerDividersEnabled) {
        this.mHeaderDividersEnabled = headerDividersEnabled;
        invalidate();
    }

    public boolean areHeaderDividersEnabled() {
        return this.mHeaderDividersEnabled;
    }

    public void setFooterDividersEnabled(boolean footerDividersEnabled) {
        this.mFooterDividersEnabled = footerDividersEnabled;
        invalidate();
    }

    public boolean areFooterDividersEnabled() {
        return this.mFooterDividersEnabled;
    }

    public void setOverscrollHeader(Drawable header) {
        this.mOverScrollHeader = header;
        if (this.mScrollY < 0) {
            invalidate();
        }
    }

    public Drawable getOverscrollHeader() {
        return this.mOverScrollHeader;
    }

    public void setOverscrollFooter(Drawable footer) {
        this.mOverScrollFooter = footer;
        invalidate();
    }

    public Drawable getOverscrollFooter() {
        return this.mOverScrollFooter;
    }

    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        ListAdapter adapter = this.mAdapter;
        int closetChildIndex = NO_POSITION;
        int closestChildTop = 0;
        if (!(adapter == null || !gainFocus || previouslyFocusedRect == null)) {
            previouslyFocusedRect.offset(this.mScrollX, this.mScrollY);
            if (adapter.getCount() < getChildCount() + this.mFirstPosition) {
                this.mLayoutMode = 0;
                layoutChildren();
            }
            Rect otherRect = this.mTempRect;
            int minDistance = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
            int childCount = getChildCount();
            int firstPosition = this.mFirstPosition;
            for (int i = 0; i < childCount; i++) {
                if (adapter.isEnabled(firstPosition + i)) {
                    View other = getChildAt(i);
                    other.getDrawingRect(otherRect);
                    offsetDescendantRectToMyCoords(other, otherRect);
                    int distance = AbsListView.getDistance(previouslyFocusedRect, otherRect, direction);
                    if (distance < minDistance) {
                        minDistance = distance;
                        closetChildIndex = i;
                        closestChildTop = other.getTop();
                    }
                }
            }
        }
        if (closetChildIndex >= 0) {
            setSelectionFromTop(this.mFirstPosition + closetChildIndex, closestChildTop);
        } else {
            requestLayout();
        }
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        int count = getChildCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                addHeaderView(getChildAt(i));
            }
            removeAllViews();
        }
    }

    protected View findViewTraversal(int id) {
        View v = super.findViewTraversal(id);
        if (v == null) {
            v = findViewInHeadersOrFooters(this.mHeaderViewInfos, id);
            if (v != null) {
                return v;
            }
            v = findViewInHeadersOrFooters(this.mFooterViewInfos, id);
            if (v != null) {
                return v;
            }
        }
        return v;
    }

    View findViewInHeadersOrFooters(ArrayList<FixedViewInfo> where, int id) {
        if (where != null) {
            int len = where.size();
            for (int i = 0; i < len; i++) {
                View v = ((FixedViewInfo) where.get(i)).view;
                if (!v.isRootNamespace()) {
                    v = v.findViewById(id);
                    if (v != null) {
                        return v;
                    }
                }
            }
        }
        return null;
    }

    protected View findViewWithTagTraversal(Object tag) {
        View v = super.findViewWithTagTraversal(tag);
        if (v == null) {
            v = findViewWithTagInHeadersOrFooters(this.mHeaderViewInfos, tag);
            if (v != null) {
                return v;
            }
            v = findViewWithTagInHeadersOrFooters(this.mFooterViewInfos, tag);
            if (v != null) {
                return v;
            }
        }
        return v;
    }

    View findViewWithTagInHeadersOrFooters(ArrayList<FixedViewInfo> where, Object tag) {
        if (where != null) {
            int len = where.size();
            for (int i = 0; i < len; i++) {
                View v = ((FixedViewInfo) where.get(i)).view;
                if (!v.isRootNamespace()) {
                    v = v.findViewWithTag(tag);
                    if (v != null) {
                        return v;
                    }
                }
            }
        }
        return null;
    }

    protected View findViewByPredicateTraversal(Predicate<View> predicate, View childToSkip) {
        View v = super.findViewByPredicateTraversal(predicate, childToSkip);
        if (v == null) {
            v = findViewByPredicateInHeadersOrFooters(this.mHeaderViewInfos, predicate, childToSkip);
            if (v != null) {
                return v;
            }
            v = findViewByPredicateInHeadersOrFooters(this.mFooterViewInfos, predicate, childToSkip);
            if (v != null) {
                return v;
            }
        }
        return v;
    }

    View findViewByPredicateInHeadersOrFooters(ArrayList<FixedViewInfo> where, Predicate<View> predicate, View childToSkip) {
        if (where != null) {
            int len = where.size();
            for (int i = 0; i < len; i++) {
                View v = ((FixedViewInfo) where.get(i)).view;
                if (!(v == childToSkip || v.isRootNamespace())) {
                    v = v.findViewByPredicate(predicate);
                    if (v != null) {
                        return v;
                    }
                }
            }
        }
        return null;
    }

    @Deprecated
    public long[] getCheckItemIds() {
        if (this.mAdapter != null && this.mAdapter.hasStableIds()) {
            return getCheckedItemIds();
        }
        if (this.mChoiceMode == 0 || this.mCheckStates == null || this.mAdapter == null) {
            return new long[0];
        }
        SparseBooleanArray states = this.mCheckStates;
        int count = states.size();
        long[] ids = new long[count];
        ListAdapter adapter = this.mAdapter;
        int i = 0;
        int checkedCount = 0;
        while (i < count) {
            int checkedCount2;
            if (states.valueAt(i)) {
                checkedCount2 = checkedCount + 1;
                ids[checkedCount] = adapter.getItemId(states.keyAt(i));
            } else {
                checkedCount2 = checkedCount;
            }
            i++;
            checkedCount = checkedCount2;
        }
        if (checkedCount == count) {
            return ids;
        }
        long[] result = new long[checkedCount];
        System.arraycopy(ids, 0, result, 0, checkedCount);
        return result;
    }

    int getHeightForPosition(int position) {
        int height = super.getHeightForPosition(position);
        if (shouldAdjustHeightForDivider(position)) {
            return height + this.mDividerHeight;
        }
        return height;
    }

    private boolean shouldAdjustHeightForDivider(int itemIndex) {
        int dividerHeight = this.mDividerHeight;
        Drawable overscrollHeader = this.mOverScrollHeader;
        Drawable overscrollFooter = this.mOverScrollFooter;
        boolean drawOverscrollHeader = overscrollHeader != null;
        boolean drawOverscrollFooter = overscrollFooter != null;
        boolean drawDividers = dividerHeight > 0 && this.mDivider != null;
        if (drawDividers) {
            boolean fillForMissingDividers = isOpaque() && !super.isOpaque();
            int itemCount = this.mItemCount;
            int headerCount = this.mHeaderViewInfos.size();
            int footerLimit = itemCount - this.mFooterViewInfos.size();
            boolean isHeader = itemIndex < headerCount;
            boolean isFooter = itemIndex >= footerLimit;
            boolean headerDividers = this.mHeaderDividersEnabled;
            boolean footerDividers = this.mFooterDividersEnabled;
            if ((headerDividers || !isHeader) && (footerDividers || !isFooter)) {
                ListAdapter adapter = this.mAdapter;
                if (this.mStackFromBottom) {
                    boolean isFirstItem = itemIndex == (drawOverscrollHeader ? 1 : 0);
                    if (!isFirstItem) {
                        int previousIndex = itemIndex + NO_POSITION;
                        if (adapter.isEnabled(itemIndex) && ((headerDividers || (!isHeader && previousIndex >= headerCount)) && (isFirstItem || (adapter.isEnabled(previousIndex) && (footerDividers || (!isFooter && previousIndex < footerLimit)))))) {
                            return true;
                        }
                        if (fillForMissingDividers) {
                            return true;
                        }
                    }
                }
                boolean isLastItem = itemIndex == itemCount + NO_POSITION;
                if (!(drawOverscrollFooter && isLastItem)) {
                    int nextIndex = itemIndex + 1;
                    if (adapter.isEnabled(itemIndex) && ((headerDividers || (!isHeader && nextIndex >= headerCount)) && (isLastItem || (adapter.isEnabled(nextIndex) && (footerDividers || (!isFooter && nextIndex < footerLimit)))))) {
                        return true;
                    }
                    if (fillForMissingDividers) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(ListView.class.getName());
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(ListView.class.getName());
        info.setCollectionInfo(CollectionInfo.obtain(getCount(), 1, false, getSelectionModeForAccessibility()));
    }

    public void onInitializeAccessibilityNodeInfoForItem(View view, int position, AccessibilityNodeInfo info) {
        boolean isHeading;
        super.onInitializeAccessibilityNodeInfoForItem(view, position, info);
        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        if (lp == null || lp.viewType == -2) {
            isHeading = false;
        } else {
            isHeading = true;
        }
        info.setCollectionItemInfo(CollectionItemInfo.obtain(position, 1, 0, 1, isHeading, isItemChecked(position)));
    }
}
