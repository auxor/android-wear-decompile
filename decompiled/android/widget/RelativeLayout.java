package android.widget;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Pools.SynchronizedPool;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.IntToString;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.EditorInfo;
import android.widget.RemoteViews.RemoteView;
import com.android.internal.R;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

@RemoteView
public class RelativeLayout extends ViewGroup {
    public static final int ABOVE = 2;
    public static final int ALIGN_BASELINE = 4;
    public static final int ALIGN_BOTTOM = 8;
    public static final int ALIGN_END = 19;
    public static final int ALIGN_LEFT = 5;
    public static final int ALIGN_PARENT_BOTTOM = 12;
    public static final int ALIGN_PARENT_END = 21;
    public static final int ALIGN_PARENT_LEFT = 9;
    public static final int ALIGN_PARENT_RIGHT = 11;
    public static final int ALIGN_PARENT_START = 20;
    public static final int ALIGN_PARENT_TOP = 10;
    public static final int ALIGN_RIGHT = 7;
    public static final int ALIGN_START = 18;
    public static final int ALIGN_TOP = 6;
    public static final int BELOW = 3;
    public static final int CENTER_HORIZONTAL = 14;
    public static final int CENTER_IN_PARENT = 13;
    public static final int CENTER_VERTICAL = 15;
    private static final int DEFAULT_WIDTH = 65536;
    public static final int END_OF = 17;
    public static final int LEFT_OF = 0;
    public static final int RIGHT_OF = 1;
    private static final int[] RULES_HORIZONTAL = null;
    private static final int[] RULES_VERTICAL = null;
    public static final int START_OF = 16;
    public static final int TRUE = -1;
    private static final int VALUE_NOT_SET = Integer.MIN_VALUE;
    private static final int VERB_COUNT = 22;
    private boolean mAllowBrokenMeasureSpecs;
    private View mBaselineView;
    private final Rect mContentBounds;
    private boolean mDirtyHierarchy;
    private final DependencyGraph mGraph;
    private int mGravity;
    private boolean mHasBaselineAlignedChild;
    private int mIgnoreGravity;
    private boolean mMeasureVerticalWithPaddingMargin;
    private final Rect mSelfBounds;
    private View[] mSortedHorizontalChildren;
    private View[] mSortedVerticalChildren;
    private SortedSet<View> mTopToBottomLeftToRightSet;

    private static class DependencyGraph {
        private SparseArray<Node> mKeyNodes;
        private ArrayList<Node> mNodes;
        private ArrayDeque<Node> mRoots;

        static class Node {
            private static final int POOL_LIMIT = 100;
            private static final SynchronizedPool<Node> sPool;
            final SparseArray<Node> dependencies;
            final ArrayMap<Node, DependencyGraph> dependents;
            View view;

            Node() {
                this.dependents = new ArrayMap();
                this.dependencies = new SparseArray();
            }

            static {
                sPool = new SynchronizedPool(POOL_LIMIT);
            }

            static Node acquire(View view) {
                Node node = (Node) sPool.acquire();
                if (node == null) {
                    node = new Node();
                }
                node.view = view;
                return node;
            }

            void release() {
                this.view = null;
                this.dependents.clear();
                this.dependencies.clear();
                sPool.release(this);
            }
        }

        private DependencyGraph() {
            this.mNodes = new ArrayList();
            this.mKeyNodes = new SparseArray();
            this.mRoots = new ArrayDeque();
        }

        void clear() {
            ArrayList<Node> nodes = this.mNodes;
            int count = nodes.size();
            for (int i = RelativeLayout.LEFT_OF; i < count; i += RelativeLayout.RIGHT_OF) {
                ((Node) nodes.get(i)).release();
            }
            nodes.clear();
            this.mKeyNodes.clear();
            this.mRoots.clear();
        }

        void add(View view) {
            int id = view.getId();
            Node node = Node.acquire(view);
            if (id != RelativeLayout.TRUE) {
                this.mKeyNodes.put(id, node);
            }
            this.mNodes.add(node);
        }

        void getSortedViews(View[] sorted, int... rules) {
            ArrayDeque<Node> roots = findRoots(rules);
            int index = RelativeLayout.LEFT_OF;
            while (true) {
                Node node = (Node) roots.pollLast();
                if (node == null) {
                    break;
                }
                View view = node.view;
                int key = view.getId();
                int index2 = index + RelativeLayout.RIGHT_OF;
                sorted[index] = view;
                ArrayMap<Node, DependencyGraph> dependents = node.dependents;
                int count = dependents.size();
                for (int i = RelativeLayout.LEFT_OF; i < count; i += RelativeLayout.RIGHT_OF) {
                    Node dependent = (Node) dependents.keyAt(i);
                    SparseArray<Node> dependencies = dependent.dependencies;
                    dependencies.remove(key);
                    if (dependencies.size() == 0) {
                        roots.add(dependent);
                    }
                }
                index = index2;
            }
            if (index < sorted.length) {
                throw new IllegalStateException("Circular dependencies cannot exist in RelativeLayout");
            }
        }

        private ArrayDeque<Node> findRoots(int[] rulesFilter) {
            int i;
            SparseArray<Node> keyNodes = this.mKeyNodes;
            ArrayList<Node> nodes = this.mNodes;
            int count = nodes.size();
            for (i = RelativeLayout.LEFT_OF; i < count; i += RelativeLayout.RIGHT_OF) {
                Node node = (Node) nodes.get(i);
                node.dependents.clear();
                node.dependencies.clear();
            }
            for (i = RelativeLayout.LEFT_OF; i < count; i += RelativeLayout.RIGHT_OF) {
                node = (Node) nodes.get(i);
                int[] rules = ((LayoutParams) node.view.getLayoutParams()).mRules;
                int rulesCount = rulesFilter.length;
                for (int j = RelativeLayout.LEFT_OF; j < rulesCount; j += RelativeLayout.RIGHT_OF) {
                    int rule = rules[rulesFilter[j]];
                    if (rule > 0) {
                        Node dependency = (Node) keyNodes.get(rule);
                        if (!(dependency == null || dependency == node)) {
                            dependency.dependents.put(node, this);
                            node.dependencies.put(rule, dependency);
                        }
                    }
                }
            }
            ArrayDeque<Node> roots = this.mRoots;
            roots.clear();
            for (i = RelativeLayout.LEFT_OF; i < count; i += RelativeLayout.RIGHT_OF) {
                node = (Node) nodes.get(i);
                if (node.dependencies.size() == 0) {
                    roots.addLast(node);
                }
            }
            return roots;
        }
    }

    public static class LayoutParams extends MarginLayoutParams {
        @ExportedProperty(category = "layout")
        public boolean alignWithParent;
        private int mBottom;
        private int[] mInitialRules;
        private boolean mIsRtlCompatibilityMode;
        private int mLeft;
        private int mRight;
        @ExportedProperty(category = "layout", indexMapping = {@IntToString(from = 2, to = "above"), @IntToString(from = 4, to = "alignBaseline"), @IntToString(from = 8, to = "alignBottom"), @IntToString(from = 5, to = "alignLeft"), @IntToString(from = 12, to = "alignParentBottom"), @IntToString(from = 9, to = "alignParentLeft"), @IntToString(from = 11, to = "alignParentRight"), @IntToString(from = 10, to = "alignParentTop"), @IntToString(from = 7, to = "alignRight"), @IntToString(from = 6, to = "alignTop"), @IntToString(from = 3, to = "below"), @IntToString(from = 14, to = "centerHorizontal"), @IntToString(from = 13, to = "center"), @IntToString(from = 15, to = "centerVertical"), @IntToString(from = 0, to = "leftOf"), @IntToString(from = 1, to = "rightOf"), @IntToString(from = 18, to = "alignStart"), @IntToString(from = 19, to = "alignEnd"), @IntToString(from = 20, to = "alignParentStart"), @IntToString(from = 21, to = "alignParentEnd"), @IntToString(from = 16, to = "startOf"), @IntToString(from = 17, to = "endOf")}, mapping = {@IntToString(from = -1, to = "true"), @IntToString(from = 0, to = "false/NO_ID")}, resolveId = true)
        private int[] mRules;
        private boolean mRulesChanged;
        private int mTop;

        static /* synthetic */ int access$112(LayoutParams x0, int x1) {
            int i = x0.mLeft + x1;
            x0.mLeft = i;
            return i;
        }

        static /* synthetic */ int access$120(LayoutParams x0, int x1) {
            int i = x0.mLeft - x1;
            x0.mLeft = i;
            return i;
        }

        static /* synthetic */ int access$212(LayoutParams x0, int x1) {
            int i = x0.mRight + x1;
            x0.mRight = i;
            return i;
        }

        static /* synthetic */ int access$220(LayoutParams x0, int x1) {
            int i = x0.mRight - x1;
            x0.mRight = i;
            return i;
        }

        static /* synthetic */ int access$312(LayoutParams x0, int x1) {
            int i = x0.mBottom + x1;
            x0.mBottom = i;
            return i;
        }

        static /* synthetic */ int access$412(LayoutParams x0, int x1) {
            int i = x0.mTop + x1;
            x0.mTop = i;
            return i;
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.mRules = new int[RelativeLayout.VERB_COUNT];
            this.mInitialRules = new int[RelativeLayout.VERB_COUNT];
            this.mRulesChanged = false;
            this.mIsRtlCompatibilityMode = false;
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.RelativeLayout_Layout);
            boolean z = c.getApplicationInfo().targetSdkVersion < RelativeLayout.END_OF || !c.getApplicationInfo().hasRtlSupport();
            this.mIsRtlCompatibilityMode = z;
            int[] rules = this.mRules;
            int[] initialRules = this.mInitialRules;
            int N = a.getIndexCount();
            for (int i = RelativeLayout.LEFT_OF; i < N; i += RelativeLayout.RIGHT_OF) {
                int attr = a.getIndex(i);
                switch (attr) {
                    case RelativeLayout.LEFT_OF /*0*/:
                        rules[RelativeLayout.LEFT_OF] = a.getResourceId(attr, RelativeLayout.LEFT_OF);
                        break;
                    case RelativeLayout.RIGHT_OF /*1*/:
                        rules[RelativeLayout.RIGHT_OF] = a.getResourceId(attr, RelativeLayout.LEFT_OF);
                        break;
                    case RelativeLayout.ABOVE /*2*/:
                        rules[RelativeLayout.ABOVE] = a.getResourceId(attr, RelativeLayout.LEFT_OF);
                        break;
                    case RelativeLayout.BELOW /*3*/:
                        rules[RelativeLayout.BELOW] = a.getResourceId(attr, RelativeLayout.LEFT_OF);
                        break;
                    case RelativeLayout.ALIGN_BASELINE /*4*/:
                        rules[RelativeLayout.ALIGN_BASELINE] = a.getResourceId(attr, RelativeLayout.LEFT_OF);
                        break;
                    case RelativeLayout.ALIGN_LEFT /*5*/:
                        rules[RelativeLayout.ALIGN_LEFT] = a.getResourceId(attr, RelativeLayout.LEFT_OF);
                        break;
                    case RelativeLayout.ALIGN_TOP /*6*/:
                        rules[RelativeLayout.ALIGN_TOP] = a.getResourceId(attr, RelativeLayout.LEFT_OF);
                        break;
                    case RelativeLayout.ALIGN_RIGHT /*7*/:
                        rules[RelativeLayout.ALIGN_RIGHT] = a.getResourceId(attr, RelativeLayout.LEFT_OF);
                        break;
                    case RelativeLayout.ALIGN_BOTTOM /*8*/:
                        rules[RelativeLayout.ALIGN_BOTTOM] = a.getResourceId(attr, RelativeLayout.LEFT_OF);
                        break;
                    case RelativeLayout.ALIGN_PARENT_LEFT /*9*/:
                        rules[RelativeLayout.ALIGN_PARENT_LEFT] = a.getBoolean(attr, false) ? RelativeLayout.TRUE : RelativeLayout.LEFT_OF;
                        break;
                    case RelativeLayout.ALIGN_PARENT_TOP /*10*/:
                        rules[RelativeLayout.ALIGN_PARENT_TOP] = a.getBoolean(attr, false) ? RelativeLayout.TRUE : RelativeLayout.LEFT_OF;
                        break;
                    case RelativeLayout.ALIGN_PARENT_RIGHT /*11*/:
                        rules[RelativeLayout.ALIGN_PARENT_RIGHT] = a.getBoolean(attr, false) ? RelativeLayout.TRUE : RelativeLayout.LEFT_OF;
                        break;
                    case RelativeLayout.ALIGN_PARENT_BOTTOM /*12*/:
                        rules[RelativeLayout.ALIGN_PARENT_BOTTOM] = a.getBoolean(attr, false) ? RelativeLayout.TRUE : RelativeLayout.LEFT_OF;
                        break;
                    case RelativeLayout.CENTER_IN_PARENT /*13*/:
                        rules[RelativeLayout.CENTER_IN_PARENT] = a.getBoolean(attr, false) ? RelativeLayout.TRUE : RelativeLayout.LEFT_OF;
                        break;
                    case RelativeLayout.CENTER_HORIZONTAL /*14*/:
                        rules[RelativeLayout.CENTER_HORIZONTAL] = a.getBoolean(attr, false) ? RelativeLayout.TRUE : RelativeLayout.LEFT_OF;
                        break;
                    case RelativeLayout.CENTER_VERTICAL /*15*/:
                        rules[RelativeLayout.CENTER_VERTICAL] = a.getBoolean(attr, false) ? RelativeLayout.TRUE : RelativeLayout.LEFT_OF;
                        break;
                    case RelativeLayout.START_OF /*16*/:
                        this.alignWithParent = a.getBoolean(attr, false);
                        break;
                    case RelativeLayout.END_OF /*17*/:
                        rules[RelativeLayout.START_OF] = a.getResourceId(attr, RelativeLayout.LEFT_OF);
                        break;
                    case RelativeLayout.ALIGN_START /*18*/:
                        rules[RelativeLayout.END_OF] = a.getResourceId(attr, RelativeLayout.LEFT_OF);
                        break;
                    case RelativeLayout.ALIGN_END /*19*/:
                        rules[RelativeLayout.ALIGN_START] = a.getResourceId(attr, RelativeLayout.LEFT_OF);
                        break;
                    case RelativeLayout.ALIGN_PARENT_START /*20*/:
                        rules[RelativeLayout.ALIGN_END] = a.getResourceId(attr, RelativeLayout.LEFT_OF);
                        break;
                    case RelativeLayout.ALIGN_PARENT_END /*21*/:
                        rules[RelativeLayout.ALIGN_PARENT_START] = a.getBoolean(attr, false) ? RelativeLayout.TRUE : RelativeLayout.LEFT_OF;
                        break;
                    case RelativeLayout.VERB_COUNT /*22*/:
                        rules[RelativeLayout.ALIGN_PARENT_END] = a.getBoolean(attr, false) ? RelativeLayout.TRUE : RelativeLayout.LEFT_OF;
                        break;
                    default:
                        break;
                }
            }
            this.mRulesChanged = true;
            System.arraycopy(rules, RelativeLayout.LEFT_OF, initialRules, RelativeLayout.LEFT_OF, RelativeLayout.VERB_COUNT);
            a.recycle();
        }

        public LayoutParams(int w, int h) {
            super(w, h);
            this.mRules = new int[RelativeLayout.VERB_COUNT];
            this.mInitialRules = new int[RelativeLayout.VERB_COUNT];
            this.mRulesChanged = false;
            this.mIsRtlCompatibilityMode = false;
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams source) {
            super(source);
            this.mRules = new int[RelativeLayout.VERB_COUNT];
            this.mInitialRules = new int[RelativeLayout.VERB_COUNT];
            this.mRulesChanged = false;
            this.mIsRtlCompatibilityMode = false;
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
            this.mRules = new int[RelativeLayout.VERB_COUNT];
            this.mInitialRules = new int[RelativeLayout.VERB_COUNT];
            this.mRulesChanged = false;
            this.mIsRtlCompatibilityMode = false;
        }

        public LayoutParams(LayoutParams source) {
            super((MarginLayoutParams) source);
            this.mRules = new int[RelativeLayout.VERB_COUNT];
            this.mInitialRules = new int[RelativeLayout.VERB_COUNT];
            this.mRulesChanged = false;
            this.mIsRtlCompatibilityMode = false;
            this.mIsRtlCompatibilityMode = source.mIsRtlCompatibilityMode;
            this.mRulesChanged = source.mRulesChanged;
            this.alignWithParent = source.alignWithParent;
            System.arraycopy(source.mRules, RelativeLayout.LEFT_OF, this.mRules, RelativeLayout.LEFT_OF, RelativeLayout.VERB_COUNT);
            System.arraycopy(source.mInitialRules, RelativeLayout.LEFT_OF, this.mInitialRules, RelativeLayout.LEFT_OF, RelativeLayout.VERB_COUNT);
        }

        public String debug(String output) {
            return output + "ViewGroup.LayoutParams={ width=" + android.view.ViewGroup.LayoutParams.sizeToString(this.width) + ", height=" + android.view.ViewGroup.LayoutParams.sizeToString(this.height) + " }";
        }

        public void addRule(int verb) {
            this.mRules[verb] = RelativeLayout.TRUE;
            this.mInitialRules[verb] = RelativeLayout.TRUE;
            this.mRulesChanged = true;
        }

        public void addRule(int verb, int anchor) {
            this.mRules[verb] = anchor;
            this.mInitialRules[verb] = anchor;
            this.mRulesChanged = true;
        }

        public void removeRule(int verb) {
            this.mRules[verb] = RelativeLayout.LEFT_OF;
            this.mInitialRules[verb] = RelativeLayout.LEFT_OF;
            this.mRulesChanged = true;
        }

        private boolean hasRelativeRules() {
            return (this.mInitialRules[RelativeLayout.START_OF] == 0 && this.mInitialRules[RelativeLayout.END_OF] == 0 && this.mInitialRules[RelativeLayout.ALIGN_START] == 0 && this.mInitialRules[RelativeLayout.ALIGN_END] == 0 && this.mInitialRules[RelativeLayout.ALIGN_PARENT_START] == 0 && this.mInitialRules[RelativeLayout.ALIGN_PARENT_END] == 0) ? false : true;
        }

        private void resolveRules(int layoutDirection) {
            boolean isLayoutRtl;
            int i = RelativeLayout.RIGHT_OF;
            if (layoutDirection == RelativeLayout.RIGHT_OF) {
                isLayoutRtl = true;
            } else {
                isLayoutRtl = false;
            }
            System.arraycopy(this.mInitialRules, RelativeLayout.LEFT_OF, this.mRules, RelativeLayout.LEFT_OF, RelativeLayout.VERB_COUNT);
            if (this.mIsRtlCompatibilityMode) {
                if (this.mRules[RelativeLayout.ALIGN_START] != 0) {
                    if (this.mRules[RelativeLayout.ALIGN_LEFT] == 0) {
                        this.mRules[RelativeLayout.ALIGN_LEFT] = this.mRules[RelativeLayout.ALIGN_START];
                    }
                    this.mRules[RelativeLayout.ALIGN_START] = RelativeLayout.LEFT_OF;
                }
                if (this.mRules[RelativeLayout.ALIGN_END] != 0) {
                    if (this.mRules[RelativeLayout.ALIGN_RIGHT] == 0) {
                        this.mRules[RelativeLayout.ALIGN_RIGHT] = this.mRules[RelativeLayout.ALIGN_END];
                    }
                    this.mRules[RelativeLayout.ALIGN_END] = RelativeLayout.LEFT_OF;
                }
                if (this.mRules[RelativeLayout.START_OF] != 0) {
                    if (this.mRules[RelativeLayout.LEFT_OF] == 0) {
                        this.mRules[RelativeLayout.LEFT_OF] = this.mRules[RelativeLayout.START_OF];
                    }
                    this.mRules[RelativeLayout.START_OF] = RelativeLayout.LEFT_OF;
                }
                if (this.mRules[RelativeLayout.END_OF] != 0) {
                    if (this.mRules[RelativeLayout.RIGHT_OF] == 0) {
                        this.mRules[RelativeLayout.RIGHT_OF] = this.mRules[RelativeLayout.END_OF];
                    }
                    this.mRules[RelativeLayout.END_OF] = RelativeLayout.LEFT_OF;
                }
                if (this.mRules[RelativeLayout.ALIGN_PARENT_START] != 0) {
                    if (this.mRules[RelativeLayout.ALIGN_PARENT_LEFT] == 0) {
                        this.mRules[RelativeLayout.ALIGN_PARENT_LEFT] = this.mRules[RelativeLayout.ALIGN_PARENT_START];
                    }
                    this.mRules[RelativeLayout.ALIGN_PARENT_START] = RelativeLayout.LEFT_OF;
                }
                if (this.mRules[RelativeLayout.ALIGN_PARENT_END] != 0) {
                    if (this.mRules[RelativeLayout.ALIGN_PARENT_RIGHT] == 0) {
                        this.mRules[RelativeLayout.ALIGN_PARENT_RIGHT] = this.mRules[RelativeLayout.ALIGN_PARENT_END];
                    }
                    this.mRules[RelativeLayout.ALIGN_PARENT_END] = RelativeLayout.LEFT_OF;
                }
            } else {
                if (!((this.mRules[RelativeLayout.ALIGN_START] == 0 && this.mRules[RelativeLayout.ALIGN_END] == 0) || (this.mRules[RelativeLayout.ALIGN_LEFT] == 0 && this.mRules[RelativeLayout.ALIGN_RIGHT] == 0))) {
                    this.mRules[RelativeLayout.ALIGN_LEFT] = RelativeLayout.LEFT_OF;
                    this.mRules[RelativeLayout.ALIGN_RIGHT] = RelativeLayout.LEFT_OF;
                }
                if (this.mRules[RelativeLayout.ALIGN_START] != 0) {
                    this.mRules[isLayoutRtl ? RelativeLayout.ALIGN_RIGHT : RelativeLayout.ALIGN_LEFT] = this.mRules[RelativeLayout.ALIGN_START];
                    this.mRules[RelativeLayout.ALIGN_START] = RelativeLayout.LEFT_OF;
                }
                if (this.mRules[RelativeLayout.ALIGN_END] != 0) {
                    this.mRules[isLayoutRtl ? RelativeLayout.ALIGN_LEFT : RelativeLayout.ALIGN_RIGHT] = this.mRules[RelativeLayout.ALIGN_END];
                    this.mRules[RelativeLayout.ALIGN_END] = RelativeLayout.LEFT_OF;
                }
                if (!((this.mRules[RelativeLayout.START_OF] == 0 && this.mRules[RelativeLayout.END_OF] == 0) || (this.mRules[RelativeLayout.LEFT_OF] == 0 && this.mRules[RelativeLayout.RIGHT_OF] == 0))) {
                    this.mRules[RelativeLayout.LEFT_OF] = RelativeLayout.LEFT_OF;
                    this.mRules[RelativeLayout.RIGHT_OF] = RelativeLayout.LEFT_OF;
                }
                if (this.mRules[RelativeLayout.START_OF] != 0) {
                    this.mRules[isLayoutRtl ? RelativeLayout.RIGHT_OF : RelativeLayout.LEFT_OF] = this.mRules[RelativeLayout.START_OF];
                    this.mRules[RelativeLayout.START_OF] = RelativeLayout.LEFT_OF;
                }
                if (this.mRules[RelativeLayout.END_OF] != 0) {
                    int[] iArr = this.mRules;
                    if (isLayoutRtl) {
                        i = RelativeLayout.LEFT_OF;
                    }
                    iArr[i] = this.mRules[RelativeLayout.END_OF];
                    this.mRules[RelativeLayout.END_OF] = RelativeLayout.LEFT_OF;
                }
                if (!((this.mRules[RelativeLayout.ALIGN_PARENT_START] == 0 && this.mRules[RelativeLayout.ALIGN_PARENT_END] == 0) || (this.mRules[RelativeLayout.ALIGN_PARENT_LEFT] == 0 && this.mRules[RelativeLayout.ALIGN_PARENT_RIGHT] == 0))) {
                    this.mRules[RelativeLayout.ALIGN_PARENT_LEFT] = RelativeLayout.LEFT_OF;
                    this.mRules[RelativeLayout.ALIGN_PARENT_RIGHT] = RelativeLayout.LEFT_OF;
                }
                if (this.mRules[RelativeLayout.ALIGN_PARENT_START] != 0) {
                    this.mRules[isLayoutRtl ? RelativeLayout.ALIGN_PARENT_RIGHT : RelativeLayout.ALIGN_PARENT_LEFT] = this.mRules[RelativeLayout.ALIGN_PARENT_START];
                    this.mRules[RelativeLayout.ALIGN_PARENT_START] = RelativeLayout.LEFT_OF;
                }
                if (this.mRules[RelativeLayout.ALIGN_PARENT_END] != 0) {
                    this.mRules[isLayoutRtl ? RelativeLayout.ALIGN_PARENT_LEFT : RelativeLayout.ALIGN_PARENT_RIGHT] = this.mRules[RelativeLayout.ALIGN_PARENT_END];
                    this.mRules[RelativeLayout.ALIGN_PARENT_END] = RelativeLayout.LEFT_OF;
                }
            }
            this.mRulesChanged = false;
        }

        public int[] getRules(int layoutDirection) {
            if (hasRelativeRules() && (this.mRulesChanged || layoutDirection != getLayoutDirection())) {
                resolveRules(layoutDirection);
                if (layoutDirection != getLayoutDirection()) {
                    setLayoutDirection(layoutDirection);
                }
            }
            return this.mRules;
        }

        public int[] getRules() {
            return this.mRules;
        }

        public void resolveLayoutDirection(int layoutDirection) {
            boolean isLayoutRtl = isLayoutRtl();
            if (hasRelativeRules() && layoutDirection != getLayoutDirection()) {
                resolveRules(layoutDirection);
            }
            super.resolveLayoutDirection(layoutDirection);
        }
    }

    private class TopToBottomLeftToRightComparator implements Comparator<View> {
        final /* synthetic */ RelativeLayout this$0;

        private TopToBottomLeftToRightComparator(android.widget.RelativeLayout r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.RelativeLayout.TopToBottomLeftToRightComparator.<init>(android.widget.RelativeLayout):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.RelativeLayout.TopToBottomLeftToRightComparator.<init>(android.widget.RelativeLayout):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.RelativeLayout.TopToBottomLeftToRightComparator.<init>(android.widget.RelativeLayout):void");
        }

        /* synthetic */ TopToBottomLeftToRightComparator(android.widget.RelativeLayout r1, android.widget.RelativeLayout.AnonymousClass1 r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.RelativeLayout.TopToBottomLeftToRightComparator.<init>(android.widget.RelativeLayout, android.widget.RelativeLayout$1):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.RelativeLayout.TopToBottomLeftToRightComparator.<init>(android.widget.RelativeLayout, android.widget.RelativeLayout$1):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 0073
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.RelativeLayout.TopToBottomLeftToRightComparator.<init>(android.widget.RelativeLayout, android.widget.RelativeLayout$1):void");
        }

        public int compare(android.view.View r1, android.view.View r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.RelativeLayout.TopToBottomLeftToRightComparator.compare(android.view.View, android.view.View):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.RelativeLayout.TopToBottomLeftToRightComparator.compare(android.view.View, android.view.View):int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.RelativeLayout.TopToBottomLeftToRightComparator.compare(android.view.View, android.view.View):int");
        }

        public /* bridge */ /* synthetic */ int compare(java.lang.Object r1, java.lang.Object r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.widget.RelativeLayout.TopToBottomLeftToRightComparator.compare(java.lang.Object, java.lang.Object):int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.widget.RelativeLayout.TopToBottomLeftToRightComparator.compare(java.lang.Object, java.lang.Object):int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 6 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 7 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.widget.RelativeLayout.TopToBottomLeftToRightComparator.compare(java.lang.Object, java.lang.Object):int");
        }
    }

    public /* bridge */ /* synthetic */ android.view.ViewGroup.LayoutParams generateLayoutParams(AttributeSet x0) {
        return generateLayoutParams(x0);
    }

    static {
        RULES_VERTICAL = new int[]{ABOVE, BELOW, ALIGN_BASELINE, ALIGN_TOP, ALIGN_BOTTOM};
        RULES_HORIZONTAL = new int[]{LEFT_OF, RIGHT_OF, ALIGN_LEFT, ALIGN_RIGHT, START_OF, END_OF, ALIGN_START, ALIGN_END};
    }

    public RelativeLayout(Context context) {
        this(context, null);
    }

    public RelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, LEFT_OF);
    }

    public RelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, LEFT_OF);
    }

    public RelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mBaselineView = null;
        this.mGravity = 8388659;
        this.mContentBounds = new Rect();
        this.mSelfBounds = new Rect();
        this.mTopToBottomLeftToRightSet = null;
        this.mGraph = new DependencyGraph();
        this.mAllowBrokenMeasureSpecs = false;
        this.mMeasureVerticalWithPaddingMargin = false;
        initFromAttributes(context, attrs, defStyleAttr, defStyleRes);
        queryCompatibilityModes(context);
    }

    private void initFromAttributes(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RelativeLayout, defStyleAttr, defStyleRes);
        this.mIgnoreGravity = a.getResourceId(RIGHT_OF, TRUE);
        this.mGravity = a.getInt(LEFT_OF, this.mGravity);
        a.recycle();
    }

    private void queryCompatibilityModes(Context context) {
        boolean z;
        boolean z2 = true;
        int version = context.getApplicationInfo().targetSdkVersion;
        if (version <= END_OF) {
            z = true;
        } else {
            z = false;
        }
        this.mAllowBrokenMeasureSpecs = z;
        if (version < ALIGN_START) {
            z2 = false;
        }
        this.mMeasureVerticalWithPaddingMargin = z2;
    }

    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @RemotableViewMethod
    public void setIgnoreGravity(int viewId) {
        this.mIgnoreGravity = viewId;
    }

    public int getGravity() {
        return this.mGravity;
    }

    @RemotableViewMethod
    public void setGravity(int gravity) {
        if (this.mGravity != gravity) {
            if ((Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK & gravity) == 0) {
                gravity |= Gravity.START;
            }
            if ((gravity & KeyEvent.KEYCODE_FORWARD_DEL) == 0) {
                gravity |= 48;
            }
            this.mGravity = gravity;
            requestLayout();
        }
    }

    @RemotableViewMethod
    public void setHorizontalGravity(int horizontalGravity) {
        int gravity = horizontalGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        if ((this.mGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) != gravity) {
            this.mGravity = (this.mGravity & -8388616) | gravity;
            requestLayout();
        }
    }

    @RemotableViewMethod
    public void setVerticalGravity(int verticalGravity) {
        int gravity = verticalGravity & KeyEvent.KEYCODE_FORWARD_DEL;
        if ((this.mGravity & KeyEvent.KEYCODE_FORWARD_DEL) != gravity) {
            this.mGravity = (this.mGravity & PackageManager.INSTALL_FAILED_NO_MATCHING_ABIS) | gravity;
            requestLayout();
        }
    }

    public int getBaseline() {
        return this.mBaselineView != null ? this.mBaselineView.getBaseline() : super.getBaseline();
    }

    public void requestLayout() {
        super.requestLayout();
        this.mDirtyHierarchy = true;
    }

    private void sortChildren() {
        int count = getChildCount();
        if (this.mSortedVerticalChildren == null || this.mSortedVerticalChildren.length != count) {
            this.mSortedVerticalChildren = new View[count];
        }
        if (this.mSortedHorizontalChildren == null || this.mSortedHorizontalChildren.length != count) {
            this.mSortedHorizontalChildren = new View[count];
        }
        DependencyGraph graph = this.mGraph;
        graph.clear();
        for (int i = LEFT_OF; i < count; i += RIGHT_OF) {
            graph.add(getChildAt(i));
        }
        graph.getSortedViews(this.mSortedVerticalChildren, RULES_VERTICAL);
        graph.getSortedViews(this.mSortedHorizontalChildren, RULES_HORIZONTAL);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i;
        LayoutParams params;
        int[] rules;
        if (this.mDirtyHierarchy) {
            this.mDirtyHierarchy = false;
            sortChildren();
        }
        int myWidth = TRUE;
        int myHeight = TRUE;
        int width = LEFT_OF;
        int height = LEFT_OF;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode != 0) {
            myWidth = widthSize;
        }
        if (heightMode != 0) {
            myHeight = heightSize;
        }
        if (widthMode == 1073741824) {
            width = myWidth;
        }
        if (heightMode == 1073741824) {
            height = myHeight;
        }
        this.mHasBaselineAlignedChild = false;
        View ignore = null;
        int gravity = this.mGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK;
        boolean horizontalGravity = (gravity == Gravity.START || gravity == 0) ? false : true;
        gravity = this.mGravity & KeyEvent.KEYCODE_FORWARD_DEL;
        boolean verticalGravity = (gravity == 48 || gravity == 0) ? false : true;
        int left = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        int top = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        int right = VALUE_NOT_SET;
        int bottom = VALUE_NOT_SET;
        boolean offsetHorizontalAxis = false;
        boolean offsetVerticalAxis = false;
        if ((horizontalGravity || verticalGravity) && this.mIgnoreGravity != TRUE) {
            ignore = findViewById(this.mIgnoreGravity);
        }
        boolean isWrapContentWidth = widthMode != 1073741824;
        boolean isWrapContentHeight = heightMode != 1073741824;
        int layoutDirection = getLayoutDirection();
        if (isLayoutRtl() && myWidth == TRUE) {
            myWidth = DEFAULT_WIDTH;
        }
        View[] views = this.mSortedHorizontalChildren;
        int count = views.length;
        for (i = LEFT_OF; i < count; i += RIGHT_OF) {
            View child = views[i];
            if (child.getVisibility() != ALIGN_BOTTOM) {
                params = (LayoutParams) child.getLayoutParams();
                applyHorizontalSizeRules(params, myWidth, params.getRules(layoutDirection));
                measureChildHorizontal(child, params, myWidth, myHeight);
                if (positionChildHorizontal(child, params, myWidth, isWrapContentWidth)) {
                    offsetHorizontalAxis = true;
                }
            }
        }
        views = this.mSortedVerticalChildren;
        count = views.length;
        int targetSdkVersion = getContext().getApplicationInfo().targetSdkVersion;
        for (i = LEFT_OF; i < count; i += RIGHT_OF) {
            child = views[i];
            if (child.getVisibility() != ALIGN_BOTTOM) {
                params = (LayoutParams) child.getLayoutParams();
                applyVerticalSizeRules(params, myHeight);
                measureChild(child, params, myWidth, myHeight);
                if (positionChildVertical(child, params, myHeight, isWrapContentHeight)) {
                    offsetVerticalAxis = true;
                }
                if (isWrapContentWidth) {
                    if (isLayoutRtl()) {
                        if (targetSdkVersion < ALIGN_END) {
                            width = Math.max(width, myWidth - params.mLeft);
                        } else {
                            width = Math.max(width, (myWidth - params.mLeft) - params.leftMargin);
                        }
                    } else if (targetSdkVersion < ALIGN_END) {
                        width = Math.max(width, params.mRight);
                    } else {
                        width = Math.max(width, params.mRight + params.rightMargin);
                    }
                }
                if (isWrapContentHeight) {
                    if (targetSdkVersion < ALIGN_END) {
                        height = Math.max(height, params.mBottom);
                    } else {
                        height = Math.max(height, params.mBottom + params.bottomMargin);
                    }
                }
                if (child != ignore || verticalGravity) {
                    left = Math.min(left, params.mLeft - params.leftMargin);
                    top = Math.min(top, params.mTop - params.topMargin);
                }
                if (child != ignore || horizontalGravity) {
                    right = Math.max(right, params.mRight + params.rightMargin);
                    bottom = Math.max(bottom, params.mBottom + params.bottomMargin);
                }
            }
        }
        if (this.mHasBaselineAlignedChild) {
            for (i = LEFT_OF; i < count; i += RIGHT_OF) {
                child = getChildAt(i);
                if (child.getVisibility() != ALIGN_BOTTOM) {
                    params = (LayoutParams) child.getLayoutParams();
                    alignBaseline(child, params);
                    if (child != ignore || verticalGravity) {
                        left = Math.min(left, params.mLeft - params.leftMargin);
                        top = Math.min(top, params.mTop - params.topMargin);
                    }
                    if (child != ignore || horizontalGravity) {
                        right = Math.max(right, params.mRight + params.rightMargin);
                        bottom = Math.max(bottom, params.mBottom + params.bottomMargin);
                    }
                }
            }
        }
        if (isWrapContentWidth) {
            width += this.mPaddingRight;
            if (this.mLayoutParams != null && this.mLayoutParams.width >= 0) {
                width = Math.max(width, this.mLayoutParams.width);
            }
            width = View.resolveSize(Math.max(width, getSuggestedMinimumWidth()), widthMeasureSpec);
            if (offsetHorizontalAxis) {
                for (i = LEFT_OF; i < count; i += RIGHT_OF) {
                    child = getChildAt(i);
                    if (child.getVisibility() != ALIGN_BOTTOM) {
                        params = (LayoutParams) child.getLayoutParams();
                        rules = params.getRules(layoutDirection);
                        if (rules[CENTER_IN_PARENT] != 0 || rules[CENTER_HORIZONTAL] != 0) {
                            centerHorizontal(child, params, width);
                        } else if (rules[ALIGN_PARENT_RIGHT] != 0) {
                            int childWidth = child.getMeasuredWidth();
                            params.mLeft = (width - this.mPaddingRight) - childWidth;
                            params.mRight = params.mLeft + childWidth;
                        }
                    }
                }
            }
        }
        if (isWrapContentHeight) {
            height += this.mPaddingBottom;
            if (this.mLayoutParams != null && this.mLayoutParams.height >= 0) {
                height = Math.max(height, this.mLayoutParams.height);
            }
            height = View.resolveSize(Math.max(height, getSuggestedMinimumHeight()), heightMeasureSpec);
            if (offsetVerticalAxis) {
                for (i = LEFT_OF; i < count; i += RIGHT_OF) {
                    child = getChildAt(i);
                    if (child.getVisibility() != ALIGN_BOTTOM) {
                        params = (LayoutParams) child.getLayoutParams();
                        rules = params.getRules(layoutDirection);
                        if (rules[CENTER_IN_PARENT] != 0 || rules[CENTER_VERTICAL] != 0) {
                            centerVertical(child, params, height);
                        } else if (rules[ALIGN_PARENT_BOTTOM] != 0) {
                            int childHeight = child.getMeasuredHeight();
                            params.mTop = (height - this.mPaddingBottom) - childHeight;
                            params.mBottom = params.mTop + childHeight;
                        }
                    }
                }
            }
        }
        if (horizontalGravity || verticalGravity) {
            Rect selfBounds = this.mSelfBounds;
            selfBounds.set(this.mPaddingLeft, this.mPaddingTop, width - this.mPaddingRight, height - this.mPaddingBottom);
            Rect contentBounds = this.mContentBounds;
            Gravity.apply(this.mGravity, right - left, bottom - top, selfBounds, contentBounds, layoutDirection);
            int horizontalOffset = contentBounds.left - left;
            int verticalOffset = contentBounds.top - top;
            if (!(horizontalOffset == 0 && verticalOffset == 0)) {
                for (i = LEFT_OF; i < count; i += RIGHT_OF) {
                    child = getChildAt(i);
                    if (!(child.getVisibility() == ALIGN_BOTTOM || child == ignore)) {
                        params = (LayoutParams) child.getLayoutParams();
                        if (horizontalGravity) {
                            LayoutParams.access$112(params, horizontalOffset);
                            LayoutParams.access$212(params, horizontalOffset);
                        }
                        if (verticalGravity) {
                            LayoutParams.access$412(params, verticalOffset);
                            LayoutParams.access$312(params, verticalOffset);
                        }
                    }
                }
            }
        }
        if (isLayoutRtl()) {
            int offsetWidth = myWidth - width;
            for (i = LEFT_OF; i < count; i += RIGHT_OF) {
                child = getChildAt(i);
                if (child.getVisibility() != ALIGN_BOTTOM) {
                    params = (LayoutParams) child.getLayoutParams();
                    LayoutParams.access$120(params, offsetWidth);
                    LayoutParams.access$220(params, offsetWidth);
                }
            }
        }
        setMeasuredDimension(width, height);
    }

    private void alignBaseline(View child, LayoutParams params) {
        int[] rules = params.getRules(getLayoutDirection());
        int anchorBaseline = getRelatedViewBaseline(rules, ALIGN_BASELINE);
        if (anchorBaseline != TRUE) {
            LayoutParams anchorParams = getRelatedViewParams(rules, ALIGN_BASELINE);
            if (anchorParams != null) {
                int offset = anchorParams.mTop + anchorBaseline;
                int baseline = child.getBaseline();
                if (baseline != TRUE) {
                    offset -= baseline;
                }
                int height = params.mBottom - params.mTop;
                params.mTop = offset;
                params.mBottom = params.mTop + height;
            }
        }
        if (this.mBaselineView == null) {
            this.mBaselineView = child;
            return;
        }
        LayoutParams lp = (LayoutParams) this.mBaselineView.getLayoutParams();
        if (params.mTop < lp.mTop || (params.mTop == lp.mTop && params.mLeft < lp.mLeft)) {
            this.mBaselineView = child;
        }
    }

    private void measureChild(View child, LayoutParams params, int myWidth, int myHeight) {
        child.measure(getChildMeasureSpec(params.mLeft, params.mRight, params.width, params.leftMargin, params.rightMargin, this.mPaddingLeft, this.mPaddingRight, myWidth), getChildMeasureSpec(params.mTop, params.mBottom, params.height, params.topMargin, params.bottomMargin, this.mPaddingTop, this.mPaddingBottom, myHeight));
    }

    private void measureChildHorizontal(View child, LayoutParams params, int myWidth, int myHeight) {
        int childHeightMeasureSpec;
        int childWidthMeasureSpec = getChildMeasureSpec(params.mLeft, params.mRight, params.width, params.leftMargin, params.rightMargin, this.mPaddingLeft, this.mPaddingRight, myWidth);
        int maxHeight = myHeight;
        if (this.mMeasureVerticalWithPaddingMargin) {
            maxHeight = Math.max(LEFT_OF, (((myHeight - this.mPaddingTop) - this.mPaddingBottom) - params.topMargin) - params.bottomMargin);
        }
        if (myHeight >= 0 || this.mAllowBrokenMeasureSpecs) {
            if (params.width == TRUE) {
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, EditorInfo.IME_FLAG_NO_ENTER_ACTION);
            } else {
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, VALUE_NOT_SET);
            }
        } else if (params.height >= 0) {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(params.height, EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        } else {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(LEFT_OF, LEFT_OF);
        }
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    private int getChildMeasureSpec(int childStart, int childEnd, int childSize, int startMargin, int endMargin, int startPadding, int endPadding, int mySize) {
        int childSpecMode = LEFT_OF;
        int childSpecSize = LEFT_OF;
        if (mySize >= 0 || this.mAllowBrokenMeasureSpecs) {
            int tempStart = childStart;
            int tempEnd = childEnd;
            if (tempStart == VALUE_NOT_SET) {
                tempStart = startPadding + startMargin;
            }
            if (tempEnd == VALUE_NOT_SET) {
                tempEnd = (mySize - endPadding) - endMargin;
            }
            int maxAvailable = tempEnd - tempStart;
            if (childStart != VALUE_NOT_SET && childEnd != VALUE_NOT_SET) {
                childSpecMode = EditorInfo.IME_FLAG_NO_ENTER_ACTION;
                childSpecSize = maxAvailable;
            } else if (childSize >= 0) {
                childSpecMode = EditorInfo.IME_FLAG_NO_ENTER_ACTION;
                if (maxAvailable >= 0) {
                    childSpecSize = Math.min(maxAvailable, childSize);
                } else {
                    childSpecSize = childSize;
                }
            } else if (childSize == TRUE) {
                childSpecMode = EditorInfo.IME_FLAG_NO_ENTER_ACTION;
                childSpecSize = maxAvailable;
            } else if (childSize == -2) {
                if (maxAvailable >= 0) {
                    childSpecMode = VALUE_NOT_SET;
                    childSpecSize = maxAvailable;
                } else {
                    childSpecMode = LEFT_OF;
                    childSpecSize = LEFT_OF;
                }
            }
            return MeasureSpec.makeMeasureSpec(childSpecSize, childSpecMode);
        }
        if (childStart != VALUE_NOT_SET && childEnd != VALUE_NOT_SET) {
            childSpecSize = Math.max(LEFT_OF, childEnd - childStart);
            childSpecMode = EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        } else if (childSize >= 0) {
            childSpecSize = childSize;
            childSpecMode = EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        } else {
            childSpecSize = LEFT_OF;
            childSpecMode = LEFT_OF;
        }
        return MeasureSpec.makeMeasureSpec(childSpecSize, childSpecMode);
    }

    private boolean positionChildHorizontal(View child, LayoutParams params, int myWidth, boolean wrapContent) {
        int[] rules = params.getRules(getLayoutDirection());
        if (params.mLeft == VALUE_NOT_SET && params.mRight != VALUE_NOT_SET) {
            params.mLeft = params.mRight - child.getMeasuredWidth();
        } else if (params.mLeft != VALUE_NOT_SET && params.mRight == VALUE_NOT_SET) {
            params.mRight = params.mLeft + child.getMeasuredWidth();
        } else if (params.mLeft == VALUE_NOT_SET && params.mRight == VALUE_NOT_SET) {
            if (rules[CENTER_IN_PARENT] == 0 && rules[CENTER_HORIZONTAL] == 0) {
                if (isLayoutRtl()) {
                    params.mRight = (myWidth - this.mPaddingRight) - params.rightMargin;
                    params.mLeft = params.mRight - child.getMeasuredWidth();
                } else {
                    params.mLeft = this.mPaddingLeft + params.leftMargin;
                    params.mRight = params.mLeft + child.getMeasuredWidth();
                }
            } else if (wrapContent) {
                params.mLeft = this.mPaddingLeft + params.leftMargin;
                params.mRight = params.mLeft + child.getMeasuredWidth();
                return true;
            } else {
                centerHorizontal(child, params, myWidth);
                return true;
            }
        }
        if (rules[ALIGN_PARENT_END] != 0) {
            return true;
        }
        return false;
    }

    private boolean positionChildVertical(View child, LayoutParams params, int myHeight, boolean wrapContent) {
        int[] rules = params.getRules();
        if (params.mTop == VALUE_NOT_SET && params.mBottom != VALUE_NOT_SET) {
            params.mTop = params.mBottom - child.getMeasuredHeight();
        } else if (params.mTop != VALUE_NOT_SET && params.mBottom == VALUE_NOT_SET) {
            params.mBottom = params.mTop + child.getMeasuredHeight();
        } else if (params.mTop == VALUE_NOT_SET && params.mBottom == VALUE_NOT_SET) {
            if (rules[CENTER_IN_PARENT] == 0 && rules[CENTER_VERTICAL] == 0) {
                params.mTop = this.mPaddingTop + params.topMargin;
                params.mBottom = params.mTop + child.getMeasuredHeight();
            } else if (wrapContent) {
                params.mTop = this.mPaddingTop + params.topMargin;
                params.mBottom = params.mTop + child.getMeasuredHeight();
                return true;
            } else {
                centerVertical(child, params, myHeight);
                return true;
            }
        }
        if (rules[ALIGN_PARENT_BOTTOM] != 0) {
            return true;
        }
        return false;
    }

    private void applyHorizontalSizeRules(LayoutParams childParams, int myWidth, int[] rules) {
        childParams.mLeft = VALUE_NOT_SET;
        childParams.mRight = VALUE_NOT_SET;
        LayoutParams anchorParams = getRelatedViewParams(rules, LEFT_OF);
        if (anchorParams != null) {
            childParams.mRight = anchorParams.mLeft - (anchorParams.leftMargin + childParams.rightMargin);
        } else if (childParams.alignWithParent && rules[LEFT_OF] != 0 && myWidth >= 0) {
            childParams.mRight = (myWidth - this.mPaddingRight) - childParams.rightMargin;
        }
        anchorParams = getRelatedViewParams(rules, RIGHT_OF);
        if (anchorParams != null) {
            childParams.mLeft = anchorParams.mRight + (anchorParams.rightMargin + childParams.leftMargin);
        } else if (childParams.alignWithParent && rules[RIGHT_OF] != 0) {
            childParams.mLeft = this.mPaddingLeft + childParams.leftMargin;
        }
        anchorParams = getRelatedViewParams(rules, ALIGN_LEFT);
        if (anchorParams != null) {
            childParams.mLeft = anchorParams.mLeft + childParams.leftMargin;
        } else if (childParams.alignWithParent && rules[ALIGN_LEFT] != 0) {
            childParams.mLeft = this.mPaddingLeft + childParams.leftMargin;
        }
        anchorParams = getRelatedViewParams(rules, ALIGN_RIGHT);
        if (anchorParams != null) {
            childParams.mRight = anchorParams.mRight - childParams.rightMargin;
        } else if (childParams.alignWithParent && rules[ALIGN_RIGHT] != 0 && myWidth >= 0) {
            childParams.mRight = (myWidth - this.mPaddingRight) - childParams.rightMargin;
        }
        if (rules[ALIGN_PARENT_LEFT] != 0) {
            childParams.mLeft = this.mPaddingLeft + childParams.leftMargin;
        }
        if (rules[ALIGN_PARENT_RIGHT] != 0 && myWidth >= 0) {
            childParams.mRight = (myWidth - this.mPaddingRight) - childParams.rightMargin;
        }
    }

    private void applyVerticalSizeRules(LayoutParams childParams, int myHeight) {
        int[] rules = childParams.getRules();
        childParams.mTop = VALUE_NOT_SET;
        childParams.mBottom = VALUE_NOT_SET;
        LayoutParams anchorParams = getRelatedViewParams(rules, ABOVE);
        if (anchorParams != null) {
            childParams.mBottom = anchorParams.mTop - (anchorParams.topMargin + childParams.bottomMargin);
        } else if (childParams.alignWithParent && rules[ABOVE] != 0 && myHeight >= 0) {
            childParams.mBottom = (myHeight - this.mPaddingBottom) - childParams.bottomMargin;
        }
        anchorParams = getRelatedViewParams(rules, BELOW);
        if (anchorParams != null) {
            childParams.mTop = anchorParams.mBottom + (anchorParams.bottomMargin + childParams.topMargin);
        } else if (childParams.alignWithParent && rules[BELOW] != 0) {
            childParams.mTop = this.mPaddingTop + childParams.topMargin;
        }
        anchorParams = getRelatedViewParams(rules, ALIGN_TOP);
        if (anchorParams != null) {
            childParams.mTop = anchorParams.mTop + childParams.topMargin;
        } else if (childParams.alignWithParent && rules[ALIGN_TOP] != 0) {
            childParams.mTop = this.mPaddingTop + childParams.topMargin;
        }
        anchorParams = getRelatedViewParams(rules, ALIGN_BOTTOM);
        if (anchorParams != null) {
            childParams.mBottom = anchorParams.mBottom - childParams.bottomMargin;
        } else if (childParams.alignWithParent && rules[ALIGN_BOTTOM] != 0 && myHeight >= 0) {
            childParams.mBottom = (myHeight - this.mPaddingBottom) - childParams.bottomMargin;
        }
        if (rules[ALIGN_PARENT_TOP] != 0) {
            childParams.mTop = this.mPaddingTop + childParams.topMargin;
        }
        if (rules[ALIGN_PARENT_BOTTOM] != 0 && myHeight >= 0) {
            childParams.mBottom = (myHeight - this.mPaddingBottom) - childParams.bottomMargin;
        }
        if (rules[ALIGN_BASELINE] != 0) {
            this.mHasBaselineAlignedChild = true;
        }
    }

    private View getRelatedView(int[] rules, int relation) {
        int id = rules[relation];
        if (id == 0) {
            return null;
        }
        Node node = (Node) this.mGraph.mKeyNodes.get(id);
        if (node == null) {
            return null;
        }
        View v = node.view;
        while (v.getVisibility() == ALIGN_BOTTOM) {
            node = (Node) this.mGraph.mKeyNodes.get(((LayoutParams) v.getLayoutParams()).getRules(v.getLayoutDirection())[relation]);
            if (node == null) {
                return null;
            }
            v = node.view;
        }
        return v;
    }

    private LayoutParams getRelatedViewParams(int[] rules, int relation) {
        View v = getRelatedView(rules, relation);
        if (v == null || !(v.getLayoutParams() instanceof LayoutParams)) {
            return null;
        }
        return (LayoutParams) v.getLayoutParams();
    }

    private int getRelatedViewBaseline(int[] rules, int relation) {
        View v = getRelatedView(rules, relation);
        if (v != null) {
            return v.getBaseline();
        }
        return TRUE;
    }

    private static void centerHorizontal(View child, LayoutParams params, int myWidth) {
        int childWidth = child.getMeasuredWidth();
        int left = (myWidth - childWidth) / ABOVE;
        params.mLeft = left;
        params.mRight = left + childWidth;
    }

    private static void centerVertical(View child, LayoutParams params, int myHeight) {
        int childHeight = child.getMeasuredHeight();
        int top = (myHeight - childHeight) / ABOVE;
        params.mTop = top;
        params.mBottom = top + childHeight;
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        for (int i = LEFT_OF; i < count; i += RIGHT_OF) {
            View child = getChildAt(i);
            if (child.getVisibility() != ALIGN_BOTTOM) {
                LayoutParams st = (LayoutParams) child.getLayoutParams();
                child.layout(st.mLeft, st.mTop, st.mRight, st.mBottom);
            }
        }
    }

    public LayoutParams m129generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    protected android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    protected android.view.ViewGroup.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        if (this.mTopToBottomLeftToRightSet == null) {
            this.mTopToBottomLeftToRightSet = new TreeSet(new TopToBottomLeftToRightComparator());
        }
        int count = getChildCount();
        for (int i = LEFT_OF; i < count; i += RIGHT_OF) {
            this.mTopToBottomLeftToRightSet.add(getChildAt(i));
        }
        for (View view : this.mTopToBottomLeftToRightSet) {
            if (view.getVisibility() == 0 && view.dispatchPopulateAccessibilityEvent(event)) {
                this.mTopToBottomLeftToRightSet.clear();
                return true;
            }
        }
        this.mTopToBottomLeftToRightSet.clear();
        return false;
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(RelativeLayout.class.getName());
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(RelativeLayout.class.getName());
    }
}
