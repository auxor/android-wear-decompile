package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.LogPrinter;
import android.util.Pair;
import android.util.Printer;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.EditorInfo;
import android.widget.RemoteViews.RemoteView;
import com.android.internal.R;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RemoteView
public class GridLayout extends ViewGroup {
    private static final int ALIGNMENT_MODE = 6;
    public static final int ALIGN_BOUNDS = 0;
    public static final int ALIGN_MARGINS = 1;
    public static final Alignment BASELINE;
    public static final Alignment BOTTOM;
    private static final int CAN_STRETCH = 2;
    public static final Alignment CENTER;
    private static final int COLUMN_COUNT = 3;
    private static final int COLUMN_ORDER_PRESERVED = 4;
    private static final int DEFAULT_ALIGNMENT_MODE = 1;
    static final int DEFAULT_CONTAINER_MARGIN = 0;
    private static final int DEFAULT_COUNT = Integer.MIN_VALUE;
    private static final boolean DEFAULT_ORDER_PRESERVED = true;
    private static final int DEFAULT_ORIENTATION = 0;
    private static final boolean DEFAULT_USE_DEFAULT_MARGINS = false;
    public static final Alignment END;
    public static final Alignment FILL;
    public static final int HORIZONTAL = 0;
    private static final int INFLEXIBLE = 0;
    private static final Alignment LEADING;
    public static final Alignment LEFT;
    static final Printer LOG_PRINTER;
    static final int MAX_SIZE = 100000;
    static final Printer NO_PRINTER;
    private static final int ORIENTATION = 0;
    public static final Alignment RIGHT;
    private static final int ROW_COUNT = 1;
    private static final int ROW_ORDER_PRESERVED = 2;
    public static final Alignment START;
    public static final Alignment TOP;
    private static final Alignment TRAILING;
    public static final int UNDEFINED = Integer.MIN_VALUE;
    static final Alignment UNDEFINED_ALIGNMENT;
    static final int UNINITIALIZED_HASH = 0;
    private static final int USE_DEFAULT_MARGINS = 5;
    public static final int VERTICAL = 1;
    int mAlignmentMode;
    int mDefaultGap;
    final Axis mHorizontalAxis;
    int mLastLayoutParamsHashCode;
    int mOrientation;
    Printer mPrinter;
    boolean mUseDefaultMargins;
    final Axis mVerticalAxis;

    public static abstract class Alignment {
        abstract int getAlignmentValue(View view, int i, int i2);

        abstract int getGravityOffset(View view, int i);

        Alignment() {
        }

        int getSizeInCell(View view, int viewSize, int cellSize) {
            return viewSize;
        }

        Bounds getBounds() {
            return new Bounds();
        }
    }

    /* renamed from: android.widget.GridLayout.5 */
    static class AnonymousClass5 extends Alignment {
        final /* synthetic */ Alignment val$ltr;
        final /* synthetic */ Alignment val$rtl;

        AnonymousClass5(Alignment alignment, Alignment alignment2) {
            this.val$ltr = alignment;
            this.val$rtl = alignment2;
        }

        int getGravityOffset(View view, int cellDelta) {
            return (!view.isLayoutRtl() ? this.val$ltr : this.val$rtl).getGravityOffset(view, cellDelta);
        }

        public int getAlignmentValue(View view, int viewSize, int mode) {
            return (!view.isLayoutRtl() ? this.val$ltr : this.val$rtl).getAlignmentValue(view, viewSize, mode);
        }
    }

    static class Bounds {
        public int after;
        public int before;
        public int flexibility;

        private Bounds() {
            reset();
        }

        protected void reset() {
            this.before = GridLayout.UNDEFINED;
            this.after = GridLayout.UNDEFINED;
            this.flexibility = GridLayout.ROW_ORDER_PRESERVED;
        }

        protected void include(int before, int after) {
            this.before = Math.max(this.before, before);
            this.after = Math.max(this.after, after);
        }

        protected int size(boolean min) {
            if (min || !GridLayout.canStretch(this.flexibility)) {
                return this.before + this.after;
            }
            return GridLayout.MAX_SIZE;
        }

        protected int getOffset(GridLayout gl, View c, Alignment a, int size, boolean horizontal) {
            return this.before - a.getAlignmentValue(c, size, gl.getLayoutMode());
        }

        protected final void include(GridLayout gl, View c, Spec spec, Axis axis, int size) {
            this.flexibility &= spec.getFlexibility();
            int before = gl.getAlignment(spec.alignment, axis.horizontal).getAlignmentValue(c, size, gl.getLayoutMode());
            include(before, size - before);
        }

        public String toString() {
            return "Bounds{before=" + this.before + ", after=" + this.after + '}';
        }
    }

    static final class Arc {
        public final Interval span;
        public boolean valid;
        public final MutableInt value;

        public Arc(Interval span, MutableInt value) {
            this.valid = GridLayout.DEFAULT_ORDER_PRESERVED;
            this.span = span;
            this.value = value;
        }

        public String toString() {
            return this.span + " " + (!this.valid ? "+>" : "->") + " " + this.value;
        }
    }

    static final class Assoc<K, V> extends ArrayList<Pair<K, V>> {
        private final Class<K> keyType;
        private final Class<V> valueType;

        private Assoc(Class<K> keyType, Class<V> valueType) {
            this.keyType = keyType;
            this.valueType = valueType;
        }

        public static <K, V> Assoc<K, V> of(Class<K> keyType, Class<V> valueType) {
            return new Assoc(keyType, valueType);
        }

        public void put(K key, V value) {
            add(Pair.create(key, value));
        }

        public PackedMap<K, V> pack() {
            int N = size();
            Object[] keys = (Object[]) ((Object[]) Array.newInstance(this.keyType, N));
            Object[] values = (Object[]) ((Object[]) Array.newInstance(this.valueType, N));
            for (int i = GridLayout.UNINITIALIZED_HASH; i < N; i += GridLayout.VERTICAL) {
                keys[i] = ((Pair) get(i)).first;
                values[i] = ((Pair) get(i)).second;
            }
            return new PackedMap(values, null);
        }
    }

    final class Axis {
        static final /* synthetic */ boolean $assertionsDisabled;
        private static final int COMPLETE = 2;
        private static final int NEW = 0;
        private static final int PENDING = 1;
        public Arc[] arcs;
        public boolean arcsValid;
        PackedMap<Interval, MutableInt> backwardLinks;
        public boolean backwardLinksValid;
        public int definedCount;
        public int[] deltas;
        PackedMap<Interval, MutableInt> forwardLinks;
        public boolean forwardLinksValid;
        PackedMap<Spec, Bounds> groupBounds;
        public boolean groupBoundsValid;
        public boolean hasWeights;
        public boolean hasWeightsValid;
        public final boolean horizontal;
        public int[] leadingMargins;
        public boolean leadingMarginsValid;
        public int[] locations;
        public boolean locationsValid;
        private int maxIndex;
        boolean orderPreserved;
        public int[] originalMeasurements;
        private MutableInt parentMax;
        private MutableInt parentMin;
        public int[] trailingMargins;
        public boolean trailingMarginsValid;

        /* renamed from: android.widget.GridLayout.Axis.1 */
        class AnonymousClass1 {
            static final /* synthetic */ boolean $assertionsDisabled;
            Arc[][] arcsByVertex;
            int cursor;
            Arc[] result;
            final /* synthetic */ Arc[] val$arcs;
            int[] visited;

            static {
                $assertionsDisabled = !GridLayout.class.desiredAssertionStatus() ? GridLayout.DEFAULT_ORDER_PRESERVED : Axis.$assertionsDisabled;
            }

            AnonymousClass1(Arc[] arcArr) {
                this.val$arcs = arcArr;
                this.result = new Arc[this.val$arcs.length];
                this.cursor = this.result.length - 1;
                this.arcsByVertex = Axis.this.groupArcsByFirstVertex(this.val$arcs);
                this.visited = new int[(Axis.this.getCount() + Axis.PENDING)];
            }

            void walk(int loc) {
                switch (this.visited[loc]) {
                    case Axis.NEW /*0*/:
                        this.visited[loc] = Axis.PENDING;
                        Arc[] arr$ = this.arcsByVertex[loc];
                        int len$ = arr$.length;
                        for (int i$ = Axis.NEW; i$ < len$; i$ += Axis.PENDING) {
                            Arc arc = arr$[i$];
                            walk(arc.span.max);
                            Arc[] arcArr = this.result;
                            int i = this.cursor;
                            this.cursor = i - 1;
                            arcArr[i] = arc;
                        }
                        this.visited[loc] = Axis.COMPLETE;
                    case Axis.PENDING /*1*/:
                        if (!$assertionsDisabled) {
                            throw new AssertionError();
                        }
                    default:
                }
            }

            Arc[] sort() {
                int N = this.arcsByVertex.length;
                for (int loc = Axis.NEW; loc < N; loc += Axis.PENDING) {
                    walk(loc);
                }
                if ($assertionsDisabled || this.cursor == -1) {
                    return this.result;
                }
                throw new AssertionError();
            }
        }

        static {
            $assertionsDisabled = !GridLayout.class.desiredAssertionStatus() ? GridLayout.DEFAULT_ORDER_PRESERVED : $assertionsDisabled;
        }

        private Axis(boolean horizontal) {
            this.definedCount = GridLayout.UNDEFINED;
            this.maxIndex = GridLayout.UNDEFINED;
            this.groupBoundsValid = $assertionsDisabled;
            this.forwardLinksValid = $assertionsDisabled;
            this.backwardLinksValid = $assertionsDisabled;
            this.leadingMarginsValid = $assertionsDisabled;
            this.trailingMarginsValid = $assertionsDisabled;
            this.arcsValid = $assertionsDisabled;
            this.locationsValid = $assertionsDisabled;
            this.hasWeightsValid = $assertionsDisabled;
            this.orderPreserved = GridLayout.DEFAULT_ORDER_PRESERVED;
            this.parentMin = new MutableInt(NEW);
            this.parentMax = new MutableInt(-100000);
            this.horizontal = horizontal;
        }

        private int calculateMaxIndex() {
            int result = -1;
            int N = GridLayout.this.getChildCount();
            for (int i = NEW; i < N; i += PENDING) {
                LayoutParams params = GridLayout.this.getLayoutParams(GridLayout.this.getChildAt(i));
                Interval span = (this.horizontal ? params.columnSpec : params.rowSpec).span;
                result = Math.max(Math.max(Math.max(result, span.min), span.max), span.size());
            }
            return result == -1 ? GridLayout.UNDEFINED : result;
        }

        private int getMaxIndex() {
            if (this.maxIndex == GridLayout.UNDEFINED) {
                this.maxIndex = Math.max(NEW, calculateMaxIndex());
            }
            return this.maxIndex;
        }

        public int getCount() {
            return Math.max(this.definedCount, getMaxIndex());
        }

        public void setCount(int count) {
            if (count != GridLayout.UNDEFINED && count < getMaxIndex()) {
                GridLayout.handleInvalidParams((this.horizontal ? "column" : "row") + "Count must be greater than or equal to the maximum of all grid indices " + "(and spans) defined in the LayoutParams of each child");
            }
            this.definedCount = count;
        }

        public boolean isOrderPreserved() {
            return this.orderPreserved;
        }

        public void setOrderPreserved(boolean orderPreserved) {
            this.orderPreserved = orderPreserved;
            invalidateStructure();
        }

        private PackedMap<Spec, Bounds> createGroupBounds() {
            Assoc<Spec, Bounds> assoc = Assoc.of(Spec.class, Bounds.class);
            int N = GridLayout.this.getChildCount();
            for (int i = NEW; i < N; i += PENDING) {
                LayoutParams lp = GridLayout.this.getLayoutParams(GridLayout.this.getChildAt(i));
                Spec spec = this.horizontal ? lp.columnSpec : lp.rowSpec;
                assoc.put(spec, GridLayout.this.getAlignment(spec.alignment, this.horizontal).getBounds());
            }
            return assoc.pack();
        }

        private void computeGroupBounds() {
            int i;
            Bounds[] values = this.groupBounds.values;
            for (i = NEW; i < values.length; i += PENDING) {
                values[i].reset();
            }
            int N = GridLayout.this.getChildCount();
            for (i = NEW; i < N; i += PENDING) {
                View c = GridLayout.this.getChildAt(i);
                LayoutParams lp = GridLayout.this.getLayoutParams(c);
                Spec spec = this.horizontal ? lp.columnSpec : lp.rowSpec;
                ((Bounds) this.groupBounds.getValue(i)).include(GridLayout.this, c, spec, this, spec.weight == 0.0f ? GridLayout.this.getMeasurementIncludingMargin(c, this.horizontal) : getOriginalMeasurements()[i] + getDeltas()[i]);
            }
        }

        public PackedMap<Spec, Bounds> getGroupBounds() {
            if (this.groupBounds == null) {
                this.groupBounds = createGroupBounds();
            }
            if (!this.groupBoundsValid) {
                computeGroupBounds();
                this.groupBoundsValid = GridLayout.DEFAULT_ORDER_PRESERVED;
            }
            return this.groupBounds;
        }

        private PackedMap<Interval, MutableInt> createLinks(boolean min) {
            Assoc<Interval, MutableInt> result = Assoc.of(Interval.class, MutableInt.class);
            Spec[] keys = getGroupBounds().keys;
            int N = keys.length;
            for (int i = NEW; i < N; i += PENDING) {
                result.put(min ? keys[i].span : keys[i].span.inverse(), new MutableInt());
            }
            return result.pack();
        }

        private void computeLinks(PackedMap<Interval, MutableInt> links, boolean min) {
            int i;
            MutableInt[] spans = links.values;
            for (i = NEW; i < spans.length; i += PENDING) {
                spans[i].reset();
            }
            Bounds[] bounds = getGroupBounds().values;
            for (i = NEW; i < bounds.length; i += PENDING) {
                int size = bounds[i].size(min);
                MutableInt valueHolder = (MutableInt) links.getValue(i);
                int i2 = valueHolder.value;
                if (!min) {
                    size = -size;
                }
                valueHolder.value = Math.max(i2, size);
            }
        }

        private PackedMap<Interval, MutableInt> getForwardLinks() {
            if (this.forwardLinks == null) {
                this.forwardLinks = createLinks(GridLayout.DEFAULT_ORDER_PRESERVED);
            }
            if (!this.forwardLinksValid) {
                computeLinks(this.forwardLinks, GridLayout.DEFAULT_ORDER_PRESERVED);
                this.forwardLinksValid = GridLayout.DEFAULT_ORDER_PRESERVED;
            }
            return this.forwardLinks;
        }

        private PackedMap<Interval, MutableInt> getBackwardLinks() {
            if (this.backwardLinks == null) {
                this.backwardLinks = createLinks($assertionsDisabled);
            }
            if (!this.backwardLinksValid) {
                computeLinks(this.backwardLinks, $assertionsDisabled);
                this.backwardLinksValid = GridLayout.DEFAULT_ORDER_PRESERVED;
            }
            return this.backwardLinks;
        }

        private void include(List<Arc> arcs, Interval key, MutableInt size, boolean ignoreIfAlreadyPresent) {
            if (key.size() != 0) {
                if (ignoreIfAlreadyPresent) {
                    for (Arc arc : arcs) {
                        if (arc.span.equals(key)) {
                            return;
                        }
                    }
                }
                arcs.add(new Arc(key, size));
            }
        }

        private void include(List<Arc> arcs, Interval key, MutableInt size) {
            include(arcs, key, size, GridLayout.DEFAULT_ORDER_PRESERVED);
        }

        Arc[][] groupArcsByFirstVertex(Arc[] arcs) {
            int i$;
            int i;
            int N = getCount() + PENDING;
            Arc[][] result = new Arc[N][];
            int[] sizes = new int[N];
            Arc[] arr$ = arcs;
            int len$ = arr$.length;
            for (i$ = NEW; i$ < len$; i$ += PENDING) {
                int i2 = arr$[i$].span.min;
                sizes[i2] = sizes[i2] + PENDING;
            }
            for (i = NEW; i < sizes.length; i += PENDING) {
                result[i] = new Arc[sizes[i]];
            }
            Arrays.fill(sizes, NEW);
            arr$ = arcs;
            len$ = arr$.length;
            for (i$ = NEW; i$ < len$; i$ += PENDING) {
                Arc arc = arr$[i$];
                i = arc.span.min;
                Arc[] arcArr = result[i];
                int i3 = sizes[i];
                sizes[i] = i3 + PENDING;
                arcArr[i3] = arc;
            }
            return result;
        }

        private Arc[] topologicalSort(Arc[] arcs) {
            return new AnonymousClass1(arcs).sort();
        }

        private Arc[] topologicalSort(List<Arc> arcs) {
            return topologicalSort((Arc[]) arcs.toArray(new Arc[arcs.size()]));
        }

        private void addComponentSizes(List<Arc> result, PackedMap<Interval, MutableInt> links) {
            for (int i = NEW; i < ((Interval[]) links.keys).length; i += PENDING) {
                include(result, ((Interval[]) links.keys)[i], ((MutableInt[]) links.values)[i], $assertionsDisabled);
            }
        }

        private Arc[] createArcs() {
            List mins = new ArrayList();
            List maxs = new ArrayList();
            addComponentSizes(mins, getForwardLinks());
            addComponentSizes(maxs, getBackwardLinks());
            if (this.orderPreserved) {
                for (int i = NEW; i < getCount(); i += PENDING) {
                    include(mins, new Interval(i, i + PENDING), new MutableInt(NEW));
                }
            }
            int N = getCount();
            include(mins, new Interval(NEW, N), this.parentMin, $assertionsDisabled);
            include(maxs, new Interval(N, NEW), this.parentMax, $assertionsDisabled);
            return (Arc[]) GridLayout.append(topologicalSort(mins), topologicalSort(maxs));
        }

        private void computeArcs() {
            getForwardLinks();
            getBackwardLinks();
        }

        public Arc[] getArcs() {
            if (this.arcs == null) {
                this.arcs = createArcs();
            }
            if (!this.arcsValid) {
                computeArcs();
                this.arcsValid = GridLayout.DEFAULT_ORDER_PRESERVED;
            }
            return this.arcs;
        }

        private boolean relax(int[] locations, Arc entry) {
            if (!entry.valid) {
                return $assertionsDisabled;
            }
            Interval span = entry.span;
            int u = span.min;
            int v = span.max;
            int candidate = locations[u] + entry.value.value;
            if (candidate <= locations[v]) {
                return $assertionsDisabled;
            }
            locations[v] = candidate;
            return GridLayout.DEFAULT_ORDER_PRESERVED;
        }

        private void init(int[] locations) {
            Arrays.fill(locations, NEW);
        }

        private String arcsToString(List<Arc> arcs) {
            String var = this.horizontal ? "x" : "y";
            StringBuilder result = new StringBuilder();
            boolean first = GridLayout.DEFAULT_ORDER_PRESERVED;
            for (Arc arc : arcs) {
                String str;
                if (first) {
                    first = $assertionsDisabled;
                } else {
                    result = result.append(", ");
                }
                int src = arc.span.min;
                int dst = arc.span.max;
                int value = arc.value.value;
                if (src < dst) {
                    str = var + dst + "-" + var + src + ">=" + value;
                } else {
                    str = var + src + "-" + var + dst + "<=" + (-value);
                }
                result.append(str);
            }
            return result.toString();
        }

        private void logError(String axisName, Arc[] arcs, boolean[] culprits0) {
            List<Arc> culprits = new ArrayList();
            List<Arc> removed = new ArrayList();
            for (int c = NEW; c < arcs.length; c += PENDING) {
                Arc arc = arcs[c];
                if (culprits0[c]) {
                    culprits.add(arc);
                }
                if (!arc.valid) {
                    removed.add(arc);
                }
            }
            GridLayout.this.mPrinter.println(axisName + " constraints: " + arcsToString(culprits) + " are inconsistent; permanently removing: " + arcsToString(removed) + ". ");
        }

        private boolean solve(Arc[] arcs, int[] locations) {
            return solve(arcs, locations, GridLayout.DEFAULT_ORDER_PRESERVED);
        }

        private boolean solve(Arc[] arcs, int[] locations, boolean modifyOnError) {
            String axisName = this.horizontal ? "horizontal" : "vertical";
            int N = getCount() + PENDING;
            boolean[] originalCulprits = null;
            for (int p = NEW; p < arcs.length; p += PENDING) {
                init(locations);
                int i = NEW;
                while (i < N) {
                    int j;
                    boolean changed = $assertionsDisabled;
                    for (j = NEW; j < arcs.length; j += PENDING) {
                        changed |= relax(locations, arcs[j]);
                    }
                    if (changed) {
                        i += PENDING;
                    } else {
                        if (originalCulprits != null) {
                            logError(axisName, arcs, originalCulprits);
                        }
                        return GridLayout.DEFAULT_ORDER_PRESERVED;
                    }
                }
                if (!modifyOnError) {
                    return $assertionsDisabled;
                }
                boolean[] culprits = new boolean[arcs.length];
                for (i = NEW; i < N; i += PENDING) {
                    int length = arcs.length;
                    for (j = NEW; j < length; j += PENDING) {
                        culprits[j] = culprits[j] | relax(locations, arcs[j]);
                    }
                }
                if (p == 0) {
                    originalCulprits = culprits;
                }
                for (i = NEW; i < arcs.length; i += PENDING) {
                    if (culprits[i]) {
                        Arc arc = arcs[i];
                        if (arc.span.min >= arc.span.max) {
                            arc.valid = $assertionsDisabled;
                            break;
                        }
                    }
                }
            }
            return GridLayout.DEFAULT_ORDER_PRESERVED;
        }

        private void computeMargins(boolean leading) {
            int[] margins = leading ? this.leadingMargins : this.trailingMargins;
            int N = GridLayout.this.getChildCount();
            for (int i = NEW; i < N; i += PENDING) {
                View c = GridLayout.this.getChildAt(i);
                if (c.getVisibility() != 8) {
                    LayoutParams lp = GridLayout.this.getLayoutParams(c);
                    Interval span = (this.horizontal ? lp.columnSpec : lp.rowSpec).span;
                    int index = leading ? span.min : span.max;
                    margins[index] = Math.max(margins[index], GridLayout.this.getMargin1(c, this.horizontal, leading));
                }
            }
        }

        public int[] getLeadingMargins() {
            if (this.leadingMargins == null) {
                this.leadingMargins = new int[(getCount() + PENDING)];
            }
            if (!this.leadingMarginsValid) {
                computeMargins(GridLayout.DEFAULT_ORDER_PRESERVED);
                this.leadingMarginsValid = GridLayout.DEFAULT_ORDER_PRESERVED;
            }
            return this.leadingMargins;
        }

        public int[] getTrailingMargins() {
            if (this.trailingMargins == null) {
                this.trailingMargins = new int[(getCount() + PENDING)];
            }
            if (!this.trailingMarginsValid) {
                computeMargins($assertionsDisabled);
                this.trailingMarginsValid = GridLayout.DEFAULT_ORDER_PRESERVED;
            }
            return this.trailingMargins;
        }

        private boolean solve(int[] a) {
            return solve(getArcs(), a);
        }

        private boolean computeHasWeights() {
            int N = GridLayout.this.getChildCount();
            for (int i = NEW; i < N; i += PENDING) {
                LayoutParams lp = GridLayout.this.getLayoutParams(GridLayout.this.getChildAt(i));
                if ((this.horizontal ? lp.columnSpec : lp.rowSpec).weight != 0.0f) {
                    return GridLayout.DEFAULT_ORDER_PRESERVED;
                }
            }
            return $assertionsDisabled;
        }

        private boolean hasWeights() {
            if (!this.hasWeightsValid) {
                this.hasWeights = computeHasWeights();
                this.hasWeightsValid = GridLayout.DEFAULT_ORDER_PRESERVED;
            }
            return this.hasWeights;
        }

        public int[] getOriginalMeasurements() {
            if (this.originalMeasurements == null) {
                this.originalMeasurements = new int[GridLayout.this.getChildCount()];
            }
            return this.originalMeasurements;
        }

        private void recordOriginalMeasurement(int i) {
            if (hasWeights()) {
                getOriginalMeasurements()[i] = GridLayout.this.getMeasurementIncludingMargin(GridLayout.this.getChildAt(i), this.horizontal);
            }
        }

        public int[] getDeltas() {
            if (this.deltas == null) {
                this.deltas = new int[GridLayout.this.getChildCount()];
            }
            return this.deltas;
        }

        private void shareOutDelta(int totalDelta, float totalWeight) {
            Arrays.fill(this.deltas, NEW);
            int N = GridLayout.this.getChildCount();
            for (int i = NEW; i < N; i += PENDING) {
                LayoutParams lp = GridLayout.this.getLayoutParams(GridLayout.this.getChildAt(i));
                float weight = (this.horizontal ? lp.columnSpec : lp.rowSpec).weight;
                if (weight != 0.0f) {
                    int delta = Math.round((((float) totalDelta) * weight) / totalWeight);
                    this.deltas[i] = delta;
                    totalDelta -= delta;
                    totalWeight -= weight;
                }
            }
        }

        private void solveAndDistributeSpace(int[] a) {
            Arrays.fill(getDeltas(), NEW);
            solve(a);
            int deltaMax = (this.parentMin.value * GridLayout.this.getChildCount()) + PENDING;
            if (deltaMax >= COMPLETE) {
                int deltaMin = NEW;
                float totalWeight = calculateTotalWeight();
                int validDelta = -1;
                boolean validSolution = GridLayout.DEFAULT_ORDER_PRESERVED;
                while (deltaMin < deltaMax) {
                    int delta = (deltaMin + deltaMax) / COMPLETE;
                    invalidateValues();
                    shareOutDelta(delta, totalWeight);
                    validSolution = solve(getArcs(), a, $assertionsDisabled);
                    if (validSolution) {
                        validDelta = delta;
                        deltaMin = delta + PENDING;
                    } else {
                        deltaMax = delta;
                    }
                }
                if (validDelta > 0 && !validSolution) {
                    invalidateValues();
                    shareOutDelta(validDelta, totalWeight);
                    solve(a);
                }
            }
        }

        private float calculateTotalWeight() {
            float totalWeight = 0.0f;
            int N = GridLayout.this.getChildCount();
            for (int i = NEW; i < N; i += PENDING) {
                LayoutParams lp = GridLayout.this.getLayoutParams(GridLayout.this.getChildAt(i));
                totalWeight += (this.horizontal ? lp.columnSpec : lp.rowSpec).weight;
            }
            return totalWeight;
        }

        private void computeLocations(int[] a) {
            if (hasWeights()) {
                solveAndDistributeSpace(a);
            } else {
                solve(a);
            }
            if (!this.orderPreserved) {
                int a0 = a[NEW];
                int N = a.length;
                for (int i = NEW; i < N; i += PENDING) {
                    a[i] = a[i] - a0;
                }
            }
        }

        public int[] getLocations() {
            if (this.locations == null) {
                this.locations = new int[(getCount() + PENDING)];
            }
            if (!this.locationsValid) {
                computeLocations(this.locations);
                this.locationsValid = GridLayout.DEFAULT_ORDER_PRESERVED;
            }
            return this.locations;
        }

        private int size(int[] locations) {
            return locations[getCount()];
        }

        private void setParentConstraints(int min, int max) {
            this.parentMin.value = min;
            this.parentMax.value = -max;
            this.locationsValid = $assertionsDisabled;
        }

        private int getMeasure(int min, int max) {
            setParentConstraints(min, max);
            return size(getLocations());
        }

        public int getMeasure(int measureSpec) {
            int mode = MeasureSpec.getMode(measureSpec);
            int size = MeasureSpec.getSize(measureSpec);
            switch (mode) {
                case GridLayout.UNDEFINED /*-2147483648*/:
                    return getMeasure(NEW, size);
                case NEW /*0*/:
                    return getMeasure(NEW, GridLayout.MAX_SIZE);
                case EditorInfo.IME_FLAG_NO_ENTER_ACTION /*1073741824*/:
                    return getMeasure(size, size);
                default:
                    if ($assertionsDisabled) {
                        return NEW;
                    }
                    throw new AssertionError();
            }
        }

        public void layout(int size) {
            setParentConstraints(size, size);
            getLocations();
        }

        public void invalidateStructure() {
            this.maxIndex = GridLayout.UNDEFINED;
            this.groupBounds = null;
            this.forwardLinks = null;
            this.backwardLinks = null;
            this.leadingMargins = null;
            this.trailingMargins = null;
            this.arcs = null;
            this.locations = null;
            this.originalMeasurements = null;
            this.deltas = null;
            this.hasWeightsValid = $assertionsDisabled;
            invalidateValues();
        }

        public void invalidateValues() {
            this.groupBoundsValid = $assertionsDisabled;
            this.forwardLinksValid = $assertionsDisabled;
            this.backwardLinksValid = $assertionsDisabled;
            this.leadingMarginsValid = $assertionsDisabled;
            this.trailingMarginsValid = $assertionsDisabled;
            this.arcsValid = $assertionsDisabled;
            this.locationsValid = $assertionsDisabled;
        }
    }

    static final class Interval {
        public final int max;
        public final int min;

        public Interval(int min, int max) {
            this.min = min;
            this.max = max;
        }

        int size() {
            return this.max - this.min;
        }

        Interval inverse() {
            return new Interval(this.max, this.min);
        }

        public boolean equals(Object that) {
            if (this == that) {
                return GridLayout.DEFAULT_ORDER_PRESERVED;
            }
            if (that == null || getClass() != that.getClass()) {
                return GridLayout.DEFAULT_USE_DEFAULT_MARGINS;
            }
            Interval interval = (Interval) that;
            if (this.max != interval.max) {
                return GridLayout.DEFAULT_USE_DEFAULT_MARGINS;
            }
            if (this.min != interval.min) {
                return GridLayout.DEFAULT_USE_DEFAULT_MARGINS;
            }
            return GridLayout.DEFAULT_ORDER_PRESERVED;
        }

        public int hashCode() {
            return (this.min * 31) + this.max;
        }

        public String toString() {
            return "[" + this.min + ", " + this.max + "]";
        }
    }

    public static class LayoutParams extends MarginLayoutParams {
        private static final int BOTTOM_MARGIN = 6;
        private static final int COLUMN = 1;
        private static final int COLUMN_SPAN = 4;
        private static final int COLUMN_WEIGHT = 6;
        private static final int DEFAULT_COLUMN = Integer.MIN_VALUE;
        private static final int DEFAULT_HEIGHT = -2;
        private static final int DEFAULT_MARGIN = Integer.MIN_VALUE;
        private static final int DEFAULT_ROW = Integer.MIN_VALUE;
        private static final Interval DEFAULT_SPAN;
        private static final int DEFAULT_SPAN_SIZE;
        private static final int DEFAULT_WIDTH = -2;
        private static final int GRAVITY = 0;
        private static final int LEFT_MARGIN = 3;
        private static final int MARGIN = 2;
        private static final int RIGHT_MARGIN = 5;
        private static final int ROW = 2;
        private static final int ROW_SPAN = 3;
        private static final int ROW_WEIGHT = 5;
        private static final int TOP_MARGIN = 4;
        public Spec columnSpec;
        public Spec rowSpec;

        static {
            DEFAULT_SPAN = new Interval(DEFAULT_ROW, -2147483647);
            DEFAULT_SPAN_SIZE = DEFAULT_SPAN.size();
        }

        private LayoutParams(int width, int height, int left, int top, int right, int bottom, Spec rowSpec, Spec columnSpec) {
            super(width, height);
            this.rowSpec = Spec.UNDEFINED;
            this.columnSpec = Spec.UNDEFINED;
            setMargins(left, top, right, bottom);
            this.rowSpec = rowSpec;
            this.columnSpec = columnSpec;
        }

        public LayoutParams(Spec rowSpec, Spec columnSpec) {
            this(DEFAULT_WIDTH, DEFAULT_WIDTH, DEFAULT_ROW, DEFAULT_ROW, DEFAULT_ROW, DEFAULT_ROW, rowSpec, columnSpec);
        }

        public LayoutParams() {
            this(Spec.UNDEFINED, Spec.UNDEFINED);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams params) {
            super(params);
            this.rowSpec = Spec.UNDEFINED;
            this.columnSpec = Spec.UNDEFINED;
        }

        public LayoutParams(MarginLayoutParams params) {
            super(params);
            this.rowSpec = Spec.UNDEFINED;
            this.columnSpec = Spec.UNDEFINED;
        }

        public LayoutParams(LayoutParams source) {
            super((MarginLayoutParams) source);
            this.rowSpec = Spec.UNDEFINED;
            this.columnSpec = Spec.UNDEFINED;
            this.rowSpec = source.rowSpec;
            this.columnSpec = source.columnSpec;
        }

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.rowSpec = Spec.UNDEFINED;
            this.columnSpec = Spec.UNDEFINED;
            reInitSuper(context, attrs);
            init(context, attrs);
        }

        private void reInitSuper(Context context, AttributeSet attrs) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewGroup_MarginLayout);
            try {
                int margin = a.getDimensionPixelSize(ROW, DEFAULT_ROW);
                this.leftMargin = a.getDimensionPixelSize(ROW_SPAN, margin);
                this.topMargin = a.getDimensionPixelSize(TOP_MARGIN, margin);
                this.rightMargin = a.getDimensionPixelSize(ROW_WEIGHT, margin);
                this.bottomMargin = a.getDimensionPixelSize(COLUMN_WEIGHT, margin);
            } finally {
                a.recycle();
            }
        }

        private void init(Context context, AttributeSet attrs) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GridLayout_Layout);
            try {
                int gravity = a.getInt(GRAVITY, GRAVITY);
                this.columnSpec = GridLayout.spec(a.getInt(COLUMN, DEFAULT_ROW), a.getInt(TOP_MARGIN, DEFAULT_SPAN_SIZE), GridLayout.getAlignment(gravity, (boolean) GridLayout.DEFAULT_ORDER_PRESERVED), a.getFloat(COLUMN_WEIGHT, 0.0f));
                this.rowSpec = GridLayout.spec(a.getInt(ROW, DEFAULT_ROW), a.getInt(ROW_SPAN, DEFAULT_SPAN_SIZE), GridLayout.getAlignment(gravity, (boolean) GridLayout.DEFAULT_USE_DEFAULT_MARGINS), a.getFloat(ROW_WEIGHT, 0.0f));
            } finally {
                a.recycle();
            }
        }

        public void setGravity(int gravity) {
            this.rowSpec = this.rowSpec.copyWriteAlignment(GridLayout.getAlignment(gravity, (boolean) GridLayout.DEFAULT_USE_DEFAULT_MARGINS));
            this.columnSpec = this.columnSpec.copyWriteAlignment(GridLayout.getAlignment(gravity, (boolean) GridLayout.DEFAULT_ORDER_PRESERVED));
        }

        protected void setBaseAttributes(TypedArray attributes, int widthAttr, int heightAttr) {
            this.width = attributes.getLayoutDimension(widthAttr, (int) DEFAULT_WIDTH);
            this.height = attributes.getLayoutDimension(heightAttr, (int) DEFAULT_WIDTH);
        }

        final void setRowSpecSpan(Interval span) {
            this.rowSpec = this.rowSpec.copyWriteSpan(span);
        }

        final void setColumnSpecSpan(Interval span) {
            this.columnSpec = this.columnSpec.copyWriteSpan(span);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return GridLayout.DEFAULT_ORDER_PRESERVED;
            }
            if (o == null || getClass() != o.getClass()) {
                return GridLayout.DEFAULT_USE_DEFAULT_MARGINS;
            }
            LayoutParams that = (LayoutParams) o;
            if (!this.columnSpec.equals(that.columnSpec)) {
                return GridLayout.DEFAULT_USE_DEFAULT_MARGINS;
            }
            if (this.rowSpec.equals(that.rowSpec)) {
                return GridLayout.DEFAULT_ORDER_PRESERVED;
            }
            return GridLayout.DEFAULT_USE_DEFAULT_MARGINS;
        }

        public int hashCode() {
            return (this.rowSpec.hashCode() * 31) + this.columnSpec.hashCode();
        }
    }

    static final class MutableInt {
        public int value;

        public MutableInt() {
            reset();
        }

        public MutableInt(int value) {
            this.value = value;
        }

        public void reset() {
            this.value = GridLayout.UNDEFINED;
        }

        public String toString() {
            return Integer.toString(this.value);
        }
    }

    static final class PackedMap<K, V> {
        public final int[] index;
        public final K[] keys;
        public final V[] values;

        private PackedMap(K[] keys, V[] values) {
            this.index = createIndex(keys);
            this.keys = compact(keys, this.index);
            this.values = compact(values, this.index);
        }

        public V getValue(int i) {
            return this.values[this.index[i]];
        }

        private static <K> int[] createIndex(K[] keys) {
            int size = keys.length;
            int[] result = new int[size];
            Map<K, Integer> keyToIndex = new HashMap();
            for (int i = GridLayout.UNINITIALIZED_HASH; i < size; i += GridLayout.VERTICAL) {
                K key = keys[i];
                Integer index = (Integer) keyToIndex.get(key);
                if (index == null) {
                    index = Integer.valueOf(keyToIndex.size());
                    keyToIndex.put(key, index);
                }
                result[i] = index.intValue();
            }
            return result;
        }

        private static <K> K[] compact(K[] a, int[] index) {
            int size = a.length;
            Object[] result = (Object[]) ((Object[]) Array.newInstance(a.getClass().getComponentType(), GridLayout.max2(index, -1) + GridLayout.VERTICAL));
            for (int i = GridLayout.UNINITIALIZED_HASH; i < size; i += GridLayout.VERTICAL) {
                result[index[i]] = a[i];
            }
            return result;
        }
    }

    public static class Spec {
        static final float DEFAULT_WEIGHT = 0.0f;
        static final Spec UNDEFINED;
        final Alignment alignment;
        final Interval span;
        final boolean startDefined;
        final float weight;

        static {
            UNDEFINED = GridLayout.spec(GridLayout.UNDEFINED);
        }

        private Spec(boolean startDefined, Interval span, Alignment alignment, float weight) {
            this.startDefined = startDefined;
            this.span = span;
            this.alignment = alignment;
            this.weight = weight;
        }

        private Spec(boolean startDefined, int start, int size, Alignment alignment, float weight) {
            this(startDefined, new Interval(start, start + size), alignment, weight);
        }

        final Spec copyWriteSpan(Interval span) {
            return new Spec(this.startDefined, span, this.alignment, this.weight);
        }

        final Spec copyWriteAlignment(Alignment alignment) {
            return new Spec(this.startDefined, this.span, alignment, this.weight);
        }

        final int getFlexibility() {
            return (this.alignment == GridLayout.UNDEFINED_ALIGNMENT && this.weight == 0.0f) ? GridLayout.UNINITIALIZED_HASH : GridLayout.ROW_ORDER_PRESERVED;
        }

        public boolean equals(Object that) {
            if (this == that) {
                return GridLayout.DEFAULT_ORDER_PRESERVED;
            }
            if (that == null || getClass() != that.getClass()) {
                return GridLayout.DEFAULT_USE_DEFAULT_MARGINS;
            }
            Spec spec = (Spec) that;
            if (!this.alignment.equals(spec.alignment)) {
                return GridLayout.DEFAULT_USE_DEFAULT_MARGINS;
            }
            if (this.span.equals(spec.span)) {
                return GridLayout.DEFAULT_ORDER_PRESERVED;
            }
            return GridLayout.DEFAULT_USE_DEFAULT_MARGINS;
        }

        public int hashCode() {
            return (this.span.hashCode() * 31) + this.alignment.hashCode();
        }
    }

    static {
        LOG_PRINTER = new LogPrinter(COLUMN_COUNT, GridLayout.class.getName());
        NO_PRINTER = new Printer() {
            public void println(String x) {
            }
        };
        UNDEFINED_ALIGNMENT = new Alignment() {
            int getGravityOffset(View view, int cellDelta) {
                return GridLayout.UNDEFINED;
            }

            public int getAlignmentValue(View view, int viewSize, int mode) {
                return GridLayout.UNDEFINED;
            }
        };
        LEADING = new Alignment() {
            int getGravityOffset(View view, int cellDelta) {
                return GridLayout.UNINITIALIZED_HASH;
            }

            public int getAlignmentValue(View view, int viewSize, int mode) {
                return GridLayout.UNINITIALIZED_HASH;
            }
        };
        TRAILING = new Alignment() {
            int getGravityOffset(View view, int cellDelta) {
                return cellDelta;
            }

            public int getAlignmentValue(View view, int viewSize, int mode) {
                return viewSize;
            }
        };
        TOP = LEADING;
        BOTTOM = TRAILING;
        START = LEADING;
        END = TRAILING;
        LEFT = createSwitchingAlignment(START, END);
        RIGHT = createSwitchingAlignment(END, START);
        CENTER = new Alignment() {
            int getGravityOffset(View view, int cellDelta) {
                return cellDelta >> GridLayout.VERTICAL;
            }

            public int getAlignmentValue(View view, int viewSize, int mode) {
                return viewSize >> GridLayout.VERTICAL;
            }
        };
        BASELINE = new Alignment() {
            int getGravityOffset(View view, int cellDelta) {
                return GridLayout.UNINITIALIZED_HASH;
            }

            public int getAlignmentValue(View view, int viewSize, int mode) {
                if (view.getVisibility() == 8) {
                    return GridLayout.UNINITIALIZED_HASH;
                }
                int baseline = view.getBaseline();
                return baseline == -1 ? GridLayout.UNDEFINED : baseline;
            }

            public Bounds getBounds() {
                return new Bounds() {
                    private int size;

                    protected void reset() {
                        super.reset();
                        this.size = GridLayout.UNDEFINED;
                    }

                    protected void include(int before, int after) {
                        super.include(before, after);
                        this.size = Math.max(this.size, before + after);
                    }

                    protected int size(boolean min) {
                        return Math.max(super.size(min), this.size);
                    }

                    protected int getOffset(GridLayout gl, View c, Alignment a, int size, boolean hrz) {
                        return Math.max(GridLayout.UNINITIALIZED_HASH, super.getOffset(gl, c, a, size, hrz));
                    }
                };
            }
        };
        FILL = new Alignment() {
            int getGravityOffset(View view, int cellDelta) {
                return GridLayout.UNINITIALIZED_HASH;
            }

            public int getAlignmentValue(View view, int viewSize, int mode) {
                return GridLayout.UNDEFINED;
            }

            public int getSizeInCell(View view, int viewSize, int cellSize) {
                return cellSize;
            }
        };
    }

    public GridLayout(Context context) {
        this(context, null);
    }

    public GridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, UNINITIALIZED_HASH);
    }

    public GridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, UNINITIALIZED_HASH);
    }

    public GridLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mHorizontalAxis = new Axis(DEFAULT_ORDER_PRESERVED, null);
        this.mVerticalAxis = new Axis(DEFAULT_USE_DEFAULT_MARGINS, null);
        this.mOrientation = UNINITIALIZED_HASH;
        this.mUseDefaultMargins = DEFAULT_USE_DEFAULT_MARGINS;
        this.mAlignmentMode = VERTICAL;
        this.mLastLayoutParamsHashCode = UNINITIALIZED_HASH;
        this.mPrinter = LOG_PRINTER;
        this.mDefaultGap = context.getResources().getDimensionPixelOffset(17104969);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GridLayout, defStyleAttr, defStyleRes);
        try {
            setRowCount(a.getInt(VERTICAL, UNDEFINED));
            setColumnCount(a.getInt(COLUMN_COUNT, UNDEFINED));
            setOrientation(a.getInt(UNINITIALIZED_HASH, UNINITIALIZED_HASH));
            setUseDefaultMargins(a.getBoolean(USE_DEFAULT_MARGINS, DEFAULT_USE_DEFAULT_MARGINS));
            setAlignmentMode(a.getInt(ALIGNMENT_MODE, VERTICAL));
            setRowOrderPreserved(a.getBoolean(ROW_ORDER_PRESERVED, DEFAULT_ORDER_PRESERVED));
            setColumnOrderPreserved(a.getBoolean(COLUMN_ORDER_PRESERVED, DEFAULT_ORDER_PRESERVED));
        } finally {
            a.recycle();
        }
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public void setOrientation(int orientation) {
        if (this.mOrientation != orientation) {
            this.mOrientation = orientation;
            invalidateStructure();
            requestLayout();
        }
    }

    public int getRowCount() {
        return this.mVerticalAxis.getCount();
    }

    public void setRowCount(int rowCount) {
        this.mVerticalAxis.setCount(rowCount);
        invalidateStructure();
        requestLayout();
    }

    public int getColumnCount() {
        return this.mHorizontalAxis.getCount();
    }

    public void setColumnCount(int columnCount) {
        this.mHorizontalAxis.setCount(columnCount);
        invalidateStructure();
        requestLayout();
    }

    public boolean getUseDefaultMargins() {
        return this.mUseDefaultMargins;
    }

    public void setUseDefaultMargins(boolean useDefaultMargins) {
        this.mUseDefaultMargins = useDefaultMargins;
        requestLayout();
    }

    public int getAlignmentMode() {
        return this.mAlignmentMode;
    }

    public void setAlignmentMode(int alignmentMode) {
        this.mAlignmentMode = alignmentMode;
        requestLayout();
    }

    public boolean isRowOrderPreserved() {
        return this.mVerticalAxis.isOrderPreserved();
    }

    public void setRowOrderPreserved(boolean rowOrderPreserved) {
        this.mVerticalAxis.setOrderPreserved(rowOrderPreserved);
        invalidateStructure();
        requestLayout();
    }

    public boolean isColumnOrderPreserved() {
        return this.mHorizontalAxis.isOrderPreserved();
    }

    public void setColumnOrderPreserved(boolean columnOrderPreserved) {
        this.mHorizontalAxis.setOrderPreserved(columnOrderPreserved);
        invalidateStructure();
        requestLayout();
    }

    public Printer getPrinter() {
        return this.mPrinter;
    }

    public void setPrinter(Printer printer) {
        if (printer == null) {
            printer = NO_PRINTER;
        }
        this.mPrinter = printer;
    }

    static int max2(int[] a, int valueIfEmpty) {
        int result = valueIfEmpty;
        int N = a.length;
        for (int i = UNINITIALIZED_HASH; i < N; i += VERTICAL) {
            result = Math.max(result, a[i]);
        }
        return result;
    }

    static <T> T[] append(T[] a, T[] b) {
        Object[] result = (Object[]) ((Object[]) Array.newInstance(a.getClass().getComponentType(), a.length + b.length));
        System.arraycopy(a, UNINITIALIZED_HASH, result, UNINITIALIZED_HASH, a.length);
        System.arraycopy(b, UNINITIALIZED_HASH, result, a.length, b.length);
        return result;
    }

    static Alignment getAlignment(int gravity, boolean horizontal) {
        switch ((gravity & (horizontal ? 7 : KeyEvent.KEYCODE_FORWARD_DEL)) >> (horizontal ? UNINITIALIZED_HASH : COLUMN_ORDER_PRESERVED)) {
            case VERTICAL /*1*/:
                return CENTER;
            case COLUMN_COUNT /*3*/:
                return horizontal ? LEFT : TOP;
            case USE_DEFAULT_MARGINS /*5*/:
                return horizontal ? RIGHT : BOTTOM;
            case SpellChecker.AVERAGE_WORD_LENGTH /*7*/:
                return FILL;
            case Gravity.START /*8388611*/:
                return START;
            case Gravity.END /*8388613*/:
                return END;
            default:
                return UNDEFINED_ALIGNMENT;
        }
    }

    private int getDefaultMargin(View c, boolean horizontal, boolean leading) {
        if (c.getClass() == Space.class) {
            return UNINITIALIZED_HASH;
        }
        return this.mDefaultGap / ROW_ORDER_PRESERVED;
    }

    private int getDefaultMargin(View c, boolean isAtEdge, boolean horizontal, boolean leading) {
        return getDefaultMargin(c, horizontal, leading);
    }

    private int getDefaultMargin(View c, LayoutParams p, boolean horizontal, boolean leading) {
        boolean isAtEdge = DEFAULT_ORDER_PRESERVED;
        if (!this.mUseDefaultMargins) {
            return UNINITIALIZED_HASH;
        }
        Spec spec = horizontal ? p.columnSpec : p.rowSpec;
        Axis axis = horizontal ? this.mHorizontalAxis : this.mVerticalAxis;
        Interval span = spec.span;
        boolean leading1 = (horizontal && isLayoutRtl()) ? !leading ? DEFAULT_ORDER_PRESERVED : DEFAULT_USE_DEFAULT_MARGINS : leading;
        if (leading1) {
            if (span.min != 0) {
                isAtEdge = DEFAULT_USE_DEFAULT_MARGINS;
            }
        } else if (span.max != axis.getCount()) {
            isAtEdge = DEFAULT_USE_DEFAULT_MARGINS;
        }
        return getDefaultMargin(c, isAtEdge, horizontal, leading);
    }

    int getMargin1(View view, boolean horizontal, boolean leading) {
        LayoutParams lp = getLayoutParams(view);
        int margin = horizontal ? leading ? lp.leftMargin : lp.rightMargin : leading ? lp.topMargin : lp.bottomMargin;
        return margin == UNDEFINED ? getDefaultMargin(view, lp, horizontal, leading) : margin;
    }

    private int getMargin(View view, boolean horizontal, boolean leading) {
        if (this.mAlignmentMode == VERTICAL) {
            return getMargin1(view, horizontal, leading);
        }
        Axis axis = horizontal ? this.mHorizontalAxis : this.mVerticalAxis;
        int[] margins = leading ? axis.getLeadingMargins() : axis.getTrailingMargins();
        LayoutParams lp = getLayoutParams(view);
        Spec spec = horizontal ? lp.columnSpec : lp.rowSpec;
        return margins[leading ? spec.span.min : spec.span.max];
    }

    private int getTotalMargin(View child, boolean horizontal) {
        return getMargin(child, horizontal, DEFAULT_ORDER_PRESERVED) + getMargin(child, horizontal, DEFAULT_USE_DEFAULT_MARGINS);
    }

    private static boolean fits(int[] a, int value, int start, int end) {
        if (end > a.length) {
            return DEFAULT_USE_DEFAULT_MARGINS;
        }
        for (int i = start; i < end; i += VERTICAL) {
            if (a[i] > value) {
                return DEFAULT_USE_DEFAULT_MARGINS;
            }
        }
        return DEFAULT_ORDER_PRESERVED;
    }

    private static void procrusteanFill(int[] a, int start, int end, int value) {
        int length = a.length;
        Arrays.fill(a, Math.min(start, length), Math.min(end, length), value);
    }

    private static void setCellGroup(LayoutParams lp, int row, int rowSpan, int col, int colSpan) {
        lp.setRowSpecSpan(new Interval(row, row + rowSpan));
        lp.setColumnSpecSpan(new Interval(col, col + colSpan));
    }

    private static int clip(Interval minorRange, boolean minorWasDefined, int count) {
        int size = minorRange.size();
        if (count == 0) {
            return size;
        }
        return Math.min(size, count - (minorWasDefined ? Math.min(minorRange.min, count) : UNINITIALIZED_HASH));
    }

    private void validateLayoutParams() {
        boolean horizontal = this.mOrientation == 0 ? DEFAULT_ORDER_PRESERVED : DEFAULT_USE_DEFAULT_MARGINS;
        Axis axis = horizontal ? this.mHorizontalAxis : this.mVerticalAxis;
        int i = axis.definedCount;
        int count = r0 != UNDEFINED ? axis.definedCount : UNINITIALIZED_HASH;
        int major = UNINITIALIZED_HASH;
        int minor = UNINITIALIZED_HASH;
        int[] maxSizes = new int[count];
        int N = getChildCount();
        for (int i2 = UNINITIALIZED_HASH; i2 < N; i2 += VERTICAL) {
            LayoutParams lp = (LayoutParams) getChildAt(i2).getLayoutParams();
            Spec majorSpec = horizontal ? lp.rowSpec : lp.columnSpec;
            Interval majorRange = majorSpec.span;
            boolean majorWasDefined = majorSpec.startDefined;
            int majorSpan = majorRange.size();
            if (majorWasDefined) {
                major = majorRange.min;
            }
            Spec minorSpec = horizontal ? lp.columnSpec : lp.rowSpec;
            Interval minorRange = minorSpec.span;
            boolean minorWasDefined = minorSpec.startDefined;
            int minorSpan = clip(minorRange, minorWasDefined, count);
            if (minorWasDefined) {
                minor = minorRange.min;
            }
            if (count != 0) {
                if (!(majorWasDefined && minorWasDefined)) {
                    while (true) {
                        if (fits(maxSizes, major, minor, minor + minorSpan)) {
                            break;
                        } else if (minorWasDefined) {
                            major += VERTICAL;
                        } else if (minor + minorSpan <= count) {
                            minor += VERTICAL;
                        } else {
                            minor = UNINITIALIZED_HASH;
                            major += VERTICAL;
                        }
                    }
                }
                procrusteanFill(maxSizes, minor, minor + minorSpan, major + majorSpan);
            }
            if (horizontal) {
                setCellGroup(lp, major, majorSpan, minor, minorSpan);
            } else {
                setCellGroup(lp, minor, minorSpan, major, majorSpan);
            }
            minor += minorSpan;
        }
    }

    private void invalidateStructure() {
        this.mLastLayoutParamsHashCode = UNINITIALIZED_HASH;
        this.mHorizontalAxis.invalidateStructure();
        this.mVerticalAxis.invalidateStructure();
        invalidateValues();
    }

    private void invalidateValues() {
        if (this.mHorizontalAxis != null && this.mVerticalAxis != null) {
            this.mHorizontalAxis.invalidateValues();
            this.mVerticalAxis.invalidateValues();
        }
    }

    protected void onSetLayoutParams(View child, android.view.ViewGroup.LayoutParams layoutParams) {
        super.onSetLayoutParams(child, layoutParams);
        if (!checkLayoutParams(layoutParams)) {
            handleInvalidParams("supplied LayoutParams are of the wrong type");
        }
        invalidateStructure();
    }

    final LayoutParams getLayoutParams(View c) {
        return (LayoutParams) c.getLayoutParams();
    }

    private static void handleInvalidParams(String msg) {
        throw new IllegalArgumentException(msg + ". ");
    }

    private void checkLayoutParams(LayoutParams lp, boolean horizontal) {
        String groupName = horizontal ? "column" : "row";
        Interval span = (horizontal ? lp.columnSpec : lp.rowSpec).span;
        if (span.min != UNDEFINED && span.min < 0) {
            handleInvalidParams(groupName + " indices must be positive");
        }
        int count = (horizontal ? this.mHorizontalAxis : this.mVerticalAxis).definedCount;
        if (count != UNDEFINED) {
            if (span.max > count) {
                handleInvalidParams(groupName + " indices (start + span) mustn't exceed the " + groupName + " count");
            }
            if (span.size() > count) {
                handleInvalidParams(groupName + " span mustn't exceed the " + groupName + " count");
            }
        }
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        if (!(p instanceof LayoutParams)) {
            return DEFAULT_USE_DEFAULT_MARGINS;
        }
        LayoutParams lp = (LayoutParams) p;
        checkLayoutParams(lp, DEFAULT_ORDER_PRESERVED);
        checkLayoutParams(lp, DEFAULT_USE_DEFAULT_MARGINS);
        return DEFAULT_ORDER_PRESERVED;
    }

    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    protected LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    private void drawLine(Canvas graphics, int x1, int y1, int x2, int y2, Paint paint) {
        if (isLayoutRtl()) {
            int width = getWidth();
            graphics.drawLine((float) (width - x1), (float) y1, (float) (width - x2), (float) y2, paint);
            return;
        }
        graphics.drawLine((float) x1, (float) y1, (float) x2, (float) y2, paint);
    }

    protected void onDebugDrawMargins(Canvas canvas, Paint paint) {
        LayoutParams lp = new LayoutParams();
        for (int i = UNINITIALIZED_HASH; i < getChildCount(); i += VERTICAL) {
            View c = getChildAt(i);
            lp.setMargins(getMargin1(c, DEFAULT_ORDER_PRESERVED, DEFAULT_ORDER_PRESERVED), getMargin1(c, DEFAULT_USE_DEFAULT_MARGINS, DEFAULT_ORDER_PRESERVED), getMargin1(c, DEFAULT_ORDER_PRESERVED, DEFAULT_USE_DEFAULT_MARGINS), getMargin1(c, DEFAULT_USE_DEFAULT_MARGINS, DEFAULT_USE_DEFAULT_MARGINS));
            lp.onDebugDraw(c, canvas, paint);
        }
    }

    protected void onDebugDraw(Canvas canvas) {
        int length;
        int i;
        Paint paint = new Paint();
        paint.setStyle(Style.STROKE);
        paint.setColor(Color.argb(50, EditorInfo.IME_MASK_ACTION, EditorInfo.IME_MASK_ACTION, EditorInfo.IME_MASK_ACTION));
        Insets insets = getOpticalInsets();
        int top = getPaddingTop() + insets.top;
        int left = getPaddingLeft() + insets.left;
        int right = (getWidth() - getPaddingRight()) - insets.right;
        int bottom = (getHeight() - getPaddingBottom()) - insets.bottom;
        int[] xs = this.mHorizontalAxis.locations;
        if (xs != null) {
            length = xs.length;
            for (i = UNINITIALIZED_HASH; i < length; i += VERTICAL) {
                int x = left + xs[i];
                drawLine(canvas, x, top, x, bottom, paint);
            }
        }
        int[] ys = this.mVerticalAxis.locations;
        if (ys != null) {
            length = ys.length;
            for (i = UNINITIALIZED_HASH; i < length; i += VERTICAL) {
                int y = top + ys[i];
                drawLine(canvas, left, y, right, y, paint);
            }
        }
        super.onDebugDraw(canvas);
    }

    protected void onViewAdded(View child) {
        super.onViewAdded(child);
        invalidateStructure();
    }

    protected void onViewRemoved(View child) {
        super.onViewRemoved(child);
        invalidateStructure();
    }

    protected void onChildVisibilityChanged(View child, int oldVisibility, int newVisibility) {
        super.onChildVisibilityChanged(child, oldVisibility, newVisibility);
        if (oldVisibility == 8 || newVisibility == 8) {
            invalidateStructure();
        }
    }

    private int computeLayoutParamsHashCode() {
        int result = VERTICAL;
        int N = getChildCount();
        for (int i = UNINITIALIZED_HASH; i < N; i += VERTICAL) {
            View c = getChildAt(i);
            if (c.getVisibility() != 8) {
                result = (result * 31) + ((LayoutParams) c.getLayoutParams()).hashCode();
            }
        }
        return result;
    }

    private void consistencyCheck() {
        if (this.mLastLayoutParamsHashCode == 0) {
            validateLayoutParams();
            this.mLastLayoutParamsHashCode = computeLayoutParamsHashCode();
        } else if (this.mLastLayoutParamsHashCode != computeLayoutParamsHashCode()) {
            this.mPrinter.println("The fields of some layout parameters were modified in between layout operations. Check the javadoc for GridLayout.LayoutParams#rowSpec.");
            invalidateStructure();
            consistencyCheck();
        }
    }

    private void measureChildWithMargins2(View child, int parentWidthSpec, int parentHeightSpec, int childWidth, int childHeight) {
        child.measure(ViewGroup.getChildMeasureSpec(parentWidthSpec, getTotalMargin(child, DEFAULT_ORDER_PRESERVED), childWidth), ViewGroup.getChildMeasureSpec(parentHeightSpec, getTotalMargin(child, DEFAULT_USE_DEFAULT_MARGINS), childHeight));
    }

    private void measureChildrenWithMargins(int widthSpec, int heightSpec, boolean firstPass) {
        int N = getChildCount();
        for (int i = UNINITIALIZED_HASH; i < N; i += VERTICAL) {
            View c = getChildAt(i);
            if (c.getVisibility() != 8) {
                LayoutParams lp = getLayoutParams(c);
                if (firstPass) {
                    measureChildWithMargins2(c, widthSpec, heightSpec, lp.width, lp.height);
                    this.mHorizontalAxis.recordOriginalMeasurement(i);
                    this.mVerticalAxis.recordOriginalMeasurement(i);
                } else {
                    boolean horizontal = this.mOrientation == 0 ? DEFAULT_ORDER_PRESERVED : DEFAULT_USE_DEFAULT_MARGINS;
                    Spec spec = horizontal ? lp.columnSpec : lp.rowSpec;
                    if (spec.alignment == FILL) {
                        Interval span = spec.span;
                        int[] locations = (horizontal ? this.mHorizontalAxis : this.mVerticalAxis).getLocations();
                        int viewSize = (locations[span.max] - locations[span.min]) - getTotalMargin(c, horizontal);
                        if (horizontal) {
                            measureChildWithMargins2(c, widthSpec, heightSpec, viewSize, lp.height);
                        } else {
                            measureChildWithMargins2(c, widthSpec, heightSpec, lp.width, viewSize);
                        }
                    }
                }
            }
        }
    }

    static int adjust(int measureSpec, int delta) {
        return MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(measureSpec + delta), MeasureSpec.getMode(measureSpec));
    }

    protected void onMeasure(int widthSpec, int heightSpec) {
        int widthSansPadding;
        int heightSansPadding;
        consistencyCheck();
        invalidateValues();
        int hPadding = getPaddingLeft() + getPaddingRight();
        int vPadding = getPaddingTop() + getPaddingBottom();
        int widthSpecSansPadding = adjust(widthSpec, -hPadding);
        int heightSpecSansPadding = adjust(heightSpec, -vPadding);
        measureChildrenWithMargins(widthSpecSansPadding, heightSpecSansPadding, DEFAULT_ORDER_PRESERVED);
        if (this.mOrientation == 0) {
            widthSansPadding = this.mHorizontalAxis.getMeasure(widthSpecSansPadding);
            measureChildrenWithMargins(widthSpecSansPadding, heightSpecSansPadding, DEFAULT_USE_DEFAULT_MARGINS);
            heightSansPadding = this.mVerticalAxis.getMeasure(heightSpecSansPadding);
        } else {
            heightSansPadding = this.mVerticalAxis.getMeasure(heightSpecSansPadding);
            measureChildrenWithMargins(widthSpecSansPadding, heightSpecSansPadding, DEFAULT_USE_DEFAULT_MARGINS);
            widthSansPadding = this.mHorizontalAxis.getMeasure(widthSpecSansPadding);
        }
        setMeasuredDimension(View.resolveSizeAndState(Math.max(widthSansPadding + hPadding, getSuggestedMinimumWidth()), widthSpec, UNINITIALIZED_HASH), View.resolveSizeAndState(Math.max(heightSansPadding + vPadding, getSuggestedMinimumHeight()), heightSpec, UNINITIALIZED_HASH));
    }

    private int getMeasurement(View c, boolean horizontal) {
        return horizontal ? c.getMeasuredWidth() : c.getMeasuredHeight();
    }

    final int getMeasurementIncludingMargin(View c, boolean horizontal) {
        if (c.getVisibility() == 8) {
            return UNINITIALIZED_HASH;
        }
        return getMeasurement(c, horizontal) + getTotalMargin(c, horizontal);
    }

    public void requestLayout() {
        super.requestLayout();
        invalidateValues();
    }

    final Alignment getAlignment(Alignment alignment, boolean horizontal) {
        if (alignment != UNDEFINED_ALIGNMENT) {
            return alignment;
        }
        return horizontal ? START : BASELINE;
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        consistencyCheck();
        int targetWidth = right - left;
        int targetHeight = bottom - top;
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        this.mHorizontalAxis.layout((targetWidth - paddingLeft) - paddingRight);
        this.mVerticalAxis.layout((targetHeight - paddingTop) - paddingBottom);
        int[] hLocations = this.mHorizontalAxis.getLocations();
        int[] vLocations = this.mVerticalAxis.getLocations();
        int N = getChildCount();
        for (int i = UNINITIALIZED_HASH; i < N; i += VERTICAL) {
            View c = getChildAt(i);
            if (c.getVisibility() != 8) {
                LayoutParams lp = getLayoutParams(c);
                Spec columnSpec = lp.columnSpec;
                Spec rowSpec = lp.rowSpec;
                Interval colSpan = columnSpec.span;
                Interval rowSpan = rowSpec.span;
                int x1 = hLocations[colSpan.min];
                int y1 = vLocations[rowSpan.min];
                int cellWidth = hLocations[colSpan.max] - x1;
                int cellHeight = vLocations[rowSpan.max] - y1;
                int pWidth = getMeasurement(c, DEFAULT_ORDER_PRESERVED);
                int pHeight = getMeasurement(c, DEFAULT_USE_DEFAULT_MARGINS);
                Alignment hAlign = getAlignment(columnSpec.alignment, (boolean) DEFAULT_ORDER_PRESERVED);
                Alignment vAlign = getAlignment(rowSpec.alignment, (boolean) DEFAULT_USE_DEFAULT_MARGINS);
                Bounds boundsX = (Bounds) this.mHorizontalAxis.getGroupBounds().getValue(i);
                Bounds boundsY = (Bounds) this.mVerticalAxis.getGroupBounds().getValue(i);
                int gravityOffsetX = hAlign.getGravityOffset(c, cellWidth - boundsX.size(DEFAULT_ORDER_PRESERVED));
                int gravityOffsetY = vAlign.getGravityOffset(c, cellHeight - boundsY.size(DEFAULT_ORDER_PRESERVED));
                int leftMargin = getMargin(c, DEFAULT_ORDER_PRESERVED, DEFAULT_ORDER_PRESERVED);
                int topMargin = getMargin(c, DEFAULT_USE_DEFAULT_MARGINS, DEFAULT_ORDER_PRESERVED);
                int rightMargin = getMargin(c, DEFAULT_ORDER_PRESERVED, DEFAULT_USE_DEFAULT_MARGINS);
                int sumMarginsX = leftMargin + rightMargin;
                int sumMarginsY = topMargin + getMargin(c, DEFAULT_USE_DEFAULT_MARGINS, DEFAULT_USE_DEFAULT_MARGINS);
                int alignmentOffsetX = boundsX.getOffset(this, c, hAlign, pWidth + sumMarginsX, DEFAULT_ORDER_PRESERVED);
                int alignmentOffsetY = boundsY.getOffset(this, c, vAlign, pHeight + sumMarginsY, DEFAULT_USE_DEFAULT_MARGINS);
                int width = hAlign.getSizeInCell(c, pWidth, cellWidth - sumMarginsX);
                int height = vAlign.getSizeInCell(c, pHeight, cellHeight - sumMarginsY);
                int dx = (x1 + gravityOffsetX) + alignmentOffsetX;
                int cx = !isLayoutRtl() ? (paddingLeft + leftMargin) + dx : (((targetWidth - width) - paddingRight) - rightMargin) - dx;
                int cy = (((paddingTop + y1) + gravityOffsetY) + alignmentOffsetY) + topMargin;
                if (!(width == c.getMeasuredWidth() && height == c.getMeasuredHeight())) {
                    c.measure(MeasureSpec.makeMeasureSpec(width, EditorInfo.IME_FLAG_NO_ENTER_ACTION), MeasureSpec.makeMeasureSpec(height, EditorInfo.IME_FLAG_NO_ENTER_ACTION));
                }
                c.layout(cx, cy, cx + width, cy + height);
            }
        }
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(GridLayout.class.getName());
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(GridLayout.class.getName());
    }

    public static Spec spec(int start, int size, Alignment alignment, float weight) {
        return new Spec(start, size, alignment, weight, null);
    }

    public static Spec spec(int start, Alignment alignment, float weight) {
        return spec(start, VERTICAL, alignment, weight);
    }

    public static Spec spec(int start, int size, float weight) {
        return spec(start, size, UNDEFINED_ALIGNMENT, weight);
    }

    public static Spec spec(int start, float weight) {
        return spec(start, (int) VERTICAL, weight);
    }

    public static Spec spec(int start, int size, Alignment alignment) {
        return spec(start, size, alignment, 0.0f);
    }

    public static Spec spec(int start, Alignment alignment) {
        return spec(start, (int) VERTICAL, alignment);
    }

    public static Spec spec(int start, int size) {
        return spec(start, size, UNDEFINED_ALIGNMENT);
    }

    public static Spec spec(int start) {
        return spec(start, (int) VERTICAL);
    }

    private static Alignment createSwitchingAlignment(Alignment ltr, Alignment rtl) {
        return new AnonymousClass5(ltr, rtl);
    }

    static boolean canStretch(int flexibility) {
        return (flexibility & ROW_ORDER_PRESERVED) != 0 ? DEFAULT_ORDER_PRESERVED : DEFAULT_USE_DEFAULT_MARGINS;
    }
}
