package android.text;

import android.graphics.Paint.FontMetricsInt;
import android.media.AudioSystem;
import android.text.Layout.Alignment;
import android.text.Layout.Directions;
import android.text.TextUtils.TruncateAt;
import android.text.style.UpdateLayout;
import android.text.style.WrapTogetherSpan;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import java.lang.ref.WeakReference;

public class DynamicLayout extends Layout {
    private static final int BLOCK_MINIMUM_CHARACTER_LENGTH = 400;
    private static final int COLUMNS_ELLIPSIZE = 5;
    private static final int COLUMNS_NORMAL = 3;
    private static final int DESCENT = 2;
    private static final int DIR = 0;
    private static final int DIR_SHIFT = 30;
    private static final int ELLIPSIS_COUNT = 4;
    private static final int ELLIPSIS_START = 3;
    private static final int ELLIPSIS_UNDEFINED = Integer.MIN_VALUE;
    public static final int INVALID_BLOCK_INDEX = -1;
    private static final int PRIORITY = 128;
    private static final int START = 0;
    private static final int START_MASK = 536870911;
    private static final int TAB = 0;
    private static final int TAB_MASK = 536870912;
    private static final int TOP = 1;
    private static final Object[] sLock;
    private static StaticLayout sStaticLayout;
    private CharSequence mBase;
    private int[] mBlockEndLines;
    private int[] mBlockIndices;
    private int mBottomPadding;
    private CharSequence mDisplay;
    private boolean mEllipsize;
    private TruncateAt mEllipsizeAt;
    private int mEllipsizedWidth;
    private boolean mIncludePad;
    private int mIndexFirstChangedBlock;
    private PackedIntVector mInts;
    private int mNumberOfBlocks;
    private PackedObjectVector<Directions> mObjects;
    private int mTopPadding;
    private ChangeWatcher mWatcher;

    private static class ChangeWatcher implements TextWatcher, SpanWatcher {
        private WeakReference<DynamicLayout> mLayout;

        public ChangeWatcher(DynamicLayout layout) {
            this.mLayout = new WeakReference(layout);
        }

        private void reflow(CharSequence s, int where, int before, int after) {
            DynamicLayout ml = (DynamicLayout) this.mLayout.get();
            if (ml != null) {
                ml.reflow(s, where, before, after);
            } else if (s instanceof Spannable) {
                ((Spannable) s).removeSpan(this);
            }
        }

        public void beforeTextChanged(CharSequence s, int where, int before, int after) {
        }

        public void onTextChanged(CharSequence s, int where, int before, int after) {
            reflow(s, where, before, after);
        }

        public void afterTextChanged(Editable s) {
        }

        public void onSpanAdded(Spannable s, Object o, int start, int end) {
            if (o instanceof UpdateLayout) {
                reflow(s, start, end - start, end - start);
            }
        }

        public void onSpanRemoved(Spannable s, Object o, int start, int end) {
            if (o instanceof UpdateLayout) {
                reflow(s, start, end - start, end - start);
            }
        }

        public void onSpanChanged(Spannable s, Object o, int start, int end, int nstart, int nend) {
            if (o instanceof UpdateLayout) {
                reflow(s, start, end - start, end - start);
                reflow(s, nstart, nend - nstart, nend - nstart);
            }
        }
    }

    public DynamicLayout(CharSequence base, TextPaint paint, int width, Alignment align, float spacingmult, float spacingadd, boolean includepad) {
        this(base, base, paint, width, align, spacingmult, spacingadd, includepad);
    }

    public DynamicLayout(CharSequence base, CharSequence display, TextPaint paint, int width, Alignment align, float spacingmult, float spacingadd, boolean includepad) {
        this(base, display, paint, width, align, spacingmult, spacingadd, includepad, null, TAB);
    }

    public DynamicLayout(CharSequence base, CharSequence display, TextPaint paint, int width, Alignment align, float spacingmult, float spacingadd, boolean includepad, TruncateAt ellipsize, int ellipsizedWidth) {
        this(base, display, paint, width, align, TextDirectionHeuristics.FIRSTSTRONG_LTR, spacingmult, spacingadd, includepad, ellipsize, ellipsizedWidth);
    }

    public DynamicLayout(CharSequence base, CharSequence display, TextPaint paint, int width, Alignment align, TextDirectionHeuristic textDir, float spacingmult, float spacingadd, boolean includepad, TruncateAt ellipsize, int ellipsizedWidth) {
        int[] start;
        CharSequence spannedEllipsizer = ellipsize == null ? display : display instanceof Spanned ? new SpannedEllipsizer(display) : new Ellipsizer(display);
        super(spannedEllipsizer, paint, width, align, textDir, spacingmult, spacingadd);
        this.mBase = base;
        this.mDisplay = display;
        if (ellipsize != null) {
            this.mInts = new PackedIntVector(COLUMNS_ELLIPSIZE);
            this.mEllipsizedWidth = ellipsizedWidth;
            this.mEllipsizeAt = ellipsize;
        } else {
            this.mInts = new PackedIntVector(ELLIPSIS_START);
            this.mEllipsizedWidth = width;
            this.mEllipsizeAt = null;
        }
        this.mObjects = new PackedObjectVector(TOP);
        this.mIncludePad = includepad;
        if (ellipsize != null) {
            Ellipsizer e = (Ellipsizer) getText();
            e.mLayout = this;
            e.mWidth = ellipsizedWidth;
            e.mMethod = ellipsize;
            this.mEllipsize = true;
        }
        if (ellipsize != null) {
            start = new int[COLUMNS_ELLIPSIZE];
            start[ELLIPSIS_START] = ELLIPSIS_UNDEFINED;
        } else {
            start = new int[ELLIPSIS_START];
        }
        Directions[] dirs = new Directions[TOP];
        dirs[TAB] = DIRS_ALL_LEFT_TO_RIGHT;
        FontMetricsInt fm = paint.getFontMetricsInt();
        int asc = fm.ascent;
        int desc = fm.descent;
        start[TAB] = AudioSystem.DEVICE_OUT_DEFAULT;
        start[TOP] = TAB;
        start[DESCENT] = desc;
        this.mInts.insertAt(TAB, start);
        start[TOP] = desc - asc;
        this.mInts.insertAt(TOP, start);
        this.mObjects.insertAt(TAB, dirs);
        reflow(base, TAB, TAB, base.length());
        if (base instanceof Spannable) {
            if (this.mWatcher == null) {
                this.mWatcher = new ChangeWatcher(this);
            }
            Spannable sp = (Spannable) base;
            ChangeWatcher[] spans = (ChangeWatcher[]) sp.getSpans(TAB, sp.length(), ChangeWatcher.class);
            for (int i = TAB; i < spans.length; i += TOP) {
                sp.removeSpan(spans[i]);
            }
            sp.setSpan(this.mWatcher, TAB, base.length(), 8388626);
        }
    }

    private void reflow(CharSequence s, int where, int before, int after) {
        if (s == this.mBase) {
            int i;
            StaticLayout reflowed;
            int[] ints;
            CharSequence text = this.mDisplay;
            int len = text.length();
            int find = TextUtils.lastIndexOf(text, '\n', where + INVALID_BLOCK_INDEX);
            if (find < 0) {
                find = TAB;
            } else {
                find += TOP;
            }
            int diff = where - find;
            before += diff;
            after += diff;
            where -= diff;
            int look = TextUtils.indexOf(text, '\n', where + after);
            if (look < 0) {
                look = len;
            } else {
                look += TOP;
            }
            int change = look - (where + after);
            before += change;
            after += change;
            if (text instanceof Spanned) {
                Spanned sp = (Spanned) text;
                boolean again;
                do {
                    again = false;
                    Object[] force = sp.getSpans(where, where + after, WrapTogetherSpan.class);
                    for (i = TAB; i < force.length; i += TOP) {
                        int st = sp.getSpanStart(force[i]);
                        int en = sp.getSpanEnd(force[i]);
                        if (st < where) {
                            again = true;
                            diff = where - st;
                            before += diff;
                            after += diff;
                            where -= diff;
                        }
                        if (en > where + after) {
                            again = true;
                            diff = en - (where + after);
                            before += diff;
                            after += diff;
                        }
                    }
                } while (again);
            }
            int startline = getLineForOffset(where);
            int startv = getLineTop(startline);
            int endline = getLineForOffset(where + before);
            if (where + after == len) {
                endline = getLineCount();
            }
            int endv = getLineTop(endline);
            boolean islast = endline == getLineCount();
            synchronized (sLock) {
                reflowed = sStaticLayout;
                sStaticLayout = null;
            }
            if (reflowed == null) {
                reflowed = new StaticLayout(null);
            } else {
                reflowed.prepare();
            }
            reflowed.generate(text, where, where + after, getPaint(), getWidth(), getTextDirectionHeuristic(), getSpacingMultiplier(), getSpacingAdd(), false, true, (float) this.mEllipsizedWidth, this.mEllipsizeAt);
            int n = reflowed.getLineCount();
            if (where + after != len && reflowed.getLineStart(n + INVALID_BLOCK_INDEX) == where + after) {
                n += INVALID_BLOCK_INDEX;
            }
            this.mInts.deleteAt(startline, endline - startline);
            this.mObjects.deleteAt(startline, endline - startline);
            int ht = reflowed.getLineTop(n);
            int toppad = TAB;
            int botpad = TAB;
            if (this.mIncludePad && startline == 0) {
                toppad = reflowed.getTopPadding();
                this.mTopPadding = toppad;
                ht -= toppad;
            }
            if (this.mIncludePad && islast) {
                botpad = reflowed.getBottomPadding();
                this.mBottomPadding = botpad;
                ht += botpad;
            }
            this.mInts.adjustValuesBelow(startline, TAB, after - before);
            this.mInts.adjustValuesBelow(startline, TOP, (startv - endv) + ht);
            if (this.mEllipsize) {
                ints = new int[COLUMNS_ELLIPSIZE];
                ints[ELLIPSIS_START] = ELLIPSIS_UNDEFINED;
            } else {
                ints = new int[ELLIPSIS_START];
            }
            Directions[] objects = new Directions[TOP];
            for (i = TAB; i < n; i += TOP) {
                ints[TAB] = (reflowed.getLineContainsTab(i) ? TAB_MASK : TAB) | ((reflowed.getParagraphDirection(i) << DIR_SHIFT) | reflowed.getLineStart(i));
                int top = reflowed.getLineTop(i) + startv;
                if (i > 0) {
                    top -= toppad;
                }
                ints[TOP] = top;
                int desc = reflowed.getLineDescent(i);
                if (i == n + INVALID_BLOCK_INDEX) {
                    desc += botpad;
                }
                ints[DESCENT] = desc;
                objects[TAB] = reflowed.getLineDirections(i);
                if (this.mEllipsize) {
                    ints[ELLIPSIS_START] = reflowed.getEllipsisStart(i);
                    ints[ELLIPSIS_COUNT] = reflowed.getEllipsisCount(i);
                }
                this.mInts.insertAt(startline + i, ints);
                this.mObjects.insertAt(startline + i, objects);
            }
            updateBlocks(startline, endline + INVALID_BLOCK_INDEX, n);
            synchronized (sLock) {
                sStaticLayout = reflowed;
                reflowed.finish();
            }
        }
    }

    private void createBlocks() {
        int offset = BLOCK_MINIMUM_CHARACTER_LENGTH;
        this.mNumberOfBlocks = TAB;
        CharSequence text = this.mDisplay;
        while (true) {
            offset = TextUtils.indexOf(text, '\n', offset);
            if (offset < 0) {
                break;
            }
            addBlockAtOffset(offset);
            offset += BLOCK_MINIMUM_CHARACTER_LENGTH;
        }
        addBlockAtOffset(text.length());
        this.mBlockIndices = new int[this.mBlockEndLines.length];
        for (int i = TAB; i < this.mBlockEndLines.length; i += TOP) {
            this.mBlockIndices[i] = INVALID_BLOCK_INDEX;
        }
    }

    private void addBlockAtOffset(int offset) {
        int line = getLineForOffset(offset);
        if (this.mBlockEndLines == null) {
            this.mBlockEndLines = ArrayUtils.newUnpaddedIntArray(TOP);
            this.mBlockEndLines[this.mNumberOfBlocks] = line;
            this.mNumberOfBlocks += TOP;
        } else if (line > this.mBlockEndLines[this.mNumberOfBlocks + INVALID_BLOCK_INDEX]) {
            this.mBlockEndLines = GrowingArrayUtils.append(this.mBlockEndLines, this.mNumberOfBlocks, line);
            this.mNumberOfBlocks += TOP;
        }
    }

    void updateBlocks(int startLine, int endLine, int newLineCount) {
        if (this.mBlockEndLines == null) {
            createBlocks();
            return;
        }
        int firstBlock = INVALID_BLOCK_INDEX;
        int lastBlock = INVALID_BLOCK_INDEX;
        int i = TAB;
        while (true) {
            int i2 = this.mNumberOfBlocks;
            if (i >= r0) {
                break;
            }
            if (this.mBlockEndLines[i] >= startLine) {
                break;
            }
            i += TOP;
        }
        firstBlock = i;
        i = firstBlock;
        while (true) {
            i2 = this.mNumberOfBlocks;
            if (i >= r0) {
                break;
            }
            if (this.mBlockEndLines[i] >= endLine) {
                break;
            }
            i += TOP;
        }
        lastBlock = i;
        int lastBlockEndLine = this.mBlockEndLines[lastBlock];
        boolean createBlockBefore = startLine > (firstBlock == 0 ? TAB : this.mBlockEndLines[firstBlock + INVALID_BLOCK_INDEX] + TOP);
        boolean createBlock = newLineCount > 0;
        boolean createBlockAfter = endLine < this.mBlockEndLines[lastBlock];
        int numAddedBlocks = TAB;
        if (createBlockBefore) {
            numAddedBlocks = TAB + TOP;
        }
        if (createBlock) {
            numAddedBlocks += TOP;
        }
        if (createBlockAfter) {
            numAddedBlocks += TOP;
        }
        int newNumberOfBlocks = (this.mNumberOfBlocks + numAddedBlocks) - ((lastBlock - firstBlock) + TOP);
        if (newNumberOfBlocks == 0) {
            this.mBlockEndLines[TAB] = TAB;
            this.mBlockIndices[TAB] = INVALID_BLOCK_INDEX;
            this.mNumberOfBlocks = TOP;
            return;
        }
        int newFirstChangedBlock;
        if (newNumberOfBlocks > this.mBlockEndLines.length) {
            int[] blockEndLines = ArrayUtils.newUnpaddedIntArray(Math.max(this.mBlockEndLines.length * DESCENT, newNumberOfBlocks));
            int[] blockIndices = new int[blockEndLines.length];
            System.arraycopy(this.mBlockEndLines, TAB, blockEndLines, TAB, firstBlock);
            System.arraycopy(this.mBlockIndices, TAB, blockIndices, TAB, firstBlock);
            System.arraycopy(this.mBlockEndLines, lastBlock + TOP, blockEndLines, firstBlock + numAddedBlocks, (this.mNumberOfBlocks - lastBlock) + INVALID_BLOCK_INDEX);
            System.arraycopy(this.mBlockIndices, lastBlock + TOP, blockIndices, firstBlock + numAddedBlocks, (this.mNumberOfBlocks - lastBlock) + INVALID_BLOCK_INDEX);
            this.mBlockEndLines = blockEndLines;
            this.mBlockIndices = blockIndices;
        } else {
            System.arraycopy(this.mBlockEndLines, lastBlock + TOP, this.mBlockEndLines, firstBlock + numAddedBlocks, (this.mNumberOfBlocks - lastBlock) + INVALID_BLOCK_INDEX);
            System.arraycopy(this.mBlockIndices, lastBlock + TOP, this.mBlockIndices, firstBlock + numAddedBlocks, (this.mNumberOfBlocks - lastBlock) + INVALID_BLOCK_INDEX);
        }
        this.mNumberOfBlocks = newNumberOfBlocks;
        int deltaLines = newLineCount - ((endLine - startLine) + TOP);
        if (deltaLines != 0) {
            newFirstChangedBlock = firstBlock + numAddedBlocks;
            i = newFirstChangedBlock;
            while (true) {
                i2 = this.mNumberOfBlocks;
                if (i >= r0) {
                    break;
                }
                int[] iArr = this.mBlockEndLines;
                iArr[i] = iArr[i] + deltaLines;
                i += TOP;
            }
        } else {
            newFirstChangedBlock = this.mNumberOfBlocks;
        }
        this.mIndexFirstChangedBlock = Math.min(this.mIndexFirstChangedBlock, newFirstChangedBlock);
        int blockIndex = firstBlock;
        if (createBlockBefore) {
            this.mBlockEndLines[blockIndex] = startLine + INVALID_BLOCK_INDEX;
            this.mBlockIndices[blockIndex] = INVALID_BLOCK_INDEX;
            blockIndex += TOP;
        }
        if (createBlock) {
            this.mBlockEndLines[blockIndex] = (startLine + newLineCount) + INVALID_BLOCK_INDEX;
            this.mBlockIndices[blockIndex] = INVALID_BLOCK_INDEX;
            blockIndex += TOP;
        }
        if (createBlockAfter) {
            this.mBlockEndLines[blockIndex] = lastBlockEndLine + deltaLines;
            this.mBlockIndices[blockIndex] = INVALID_BLOCK_INDEX;
        }
    }

    void setBlocksDataForTest(int[] blockEndLines, int[] blockIndices, int numberOfBlocks) {
        this.mBlockEndLines = new int[blockEndLines.length];
        this.mBlockIndices = new int[blockIndices.length];
        System.arraycopy(blockEndLines, TAB, this.mBlockEndLines, TAB, blockEndLines.length);
        System.arraycopy(blockIndices, TAB, this.mBlockIndices, TAB, blockIndices.length);
        this.mNumberOfBlocks = numberOfBlocks;
    }

    public int[] getBlockEndLines() {
        return this.mBlockEndLines;
    }

    public int[] getBlockIndices() {
        return this.mBlockIndices;
    }

    public int getNumberOfBlocks() {
        return this.mNumberOfBlocks;
    }

    public int getIndexFirstChangedBlock() {
        return this.mIndexFirstChangedBlock;
    }

    public void setIndexFirstChangedBlock(int i) {
        this.mIndexFirstChangedBlock = i;
    }

    public int getLineCount() {
        return this.mInts.size() + INVALID_BLOCK_INDEX;
    }

    public int getLineTop(int line) {
        return this.mInts.getValue(line, TOP);
    }

    public int getLineDescent(int line) {
        return this.mInts.getValue(line, DESCENT);
    }

    public int getLineStart(int line) {
        return this.mInts.getValue(line, TAB) & START_MASK;
    }

    public boolean getLineContainsTab(int line) {
        return (this.mInts.getValue(line, TAB) & TAB_MASK) != 0;
    }

    public int getParagraphDirection(int line) {
        return this.mInts.getValue(line, TAB) >> DIR_SHIFT;
    }

    public final Directions getLineDirections(int line) {
        return (Directions) this.mObjects.getValue(line, TAB);
    }

    public int getTopPadding() {
        return this.mTopPadding;
    }

    public int getBottomPadding() {
        return this.mBottomPadding;
    }

    public int getEllipsizedWidth() {
        return this.mEllipsizedWidth;
    }

    public int getEllipsisStart(int line) {
        if (this.mEllipsizeAt == null) {
            return TAB;
        }
        return this.mInts.getValue(line, ELLIPSIS_START);
    }

    public int getEllipsisCount(int line) {
        if (this.mEllipsizeAt == null) {
            return TAB;
        }
        return this.mInts.getValue(line, ELLIPSIS_COUNT);
    }

    static {
        sStaticLayout = new StaticLayout(null);
        sLock = new Object[TAB];
    }
}
