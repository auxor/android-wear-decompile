package android.text;

import android.emoji.EmojiFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.text.TextUtils.TruncateAt;
import android.text.method.MetaKeyKeyListener;
import android.text.style.AlignmentSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.LeadingMarginSpan.LeadingMarginSpan2;
import android.text.style.LineBackgroundSpan;
import android.text.style.ParagraphStyle;
import android.text.style.ReplacementSpan;
import android.text.style.TabStopSpan;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import com.android.internal.widget.AutoScrollHelper;
import javax.microedition.khronos.opengles.GL10;

public abstract class Layout {
    static final Directions DIRS_ALL_LEFT_TO_RIGHT = null;
    static final Directions DIRS_ALL_RIGHT_TO_LEFT = null;
    public static final int DIR_LEFT_TO_RIGHT = 1;
    static final int DIR_REQUEST_DEFAULT_LTR = 2;
    static final int DIR_REQUEST_DEFAULT_RTL = -2;
    static final int DIR_REQUEST_LTR = 1;
    static final int DIR_REQUEST_RTL = -1;
    public static final int DIR_RIGHT_TO_LEFT = -1;
    static final EmojiFactory EMOJI_FACTORY = null;
    static final int MAX_EMOJI = 0;
    static final int MIN_EMOJI = 0;
    private static final ParagraphStyle[] NO_PARA_SPANS = null;
    static final int RUN_LENGTH_MASK = 67108863;
    static final int RUN_LEVEL_MASK = 63;
    static final int RUN_LEVEL_SHIFT = 26;
    static final int RUN_RTL_FLAG = 67108864;
    private static final int TAB_INCREMENT = 20;
    private static final Rect sTempRect = null;
    private Alignment mAlignment;
    private SpanSet<LineBackgroundSpan> mLineBackgroundSpans;
    private TextPaint mPaint;
    private float mSpacingAdd;
    private float mSpacingMult;
    private boolean mSpannedText;
    private CharSequence mText;
    private TextDirectionHeuristic mTextDir;
    private int mWidth;
    TextPaint mWorkPaint;

    /* renamed from: android.text.Layout.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$text$Layout$Alignment;

        static {
            $SwitchMap$android$text$Layout$Alignment = new int[Alignment.values().length];
            try {
                $SwitchMap$android$text$Layout$Alignment[Alignment.ALIGN_LEFT.ordinal()] = Layout.DIR_REQUEST_LTR;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$text$Layout$Alignment[Alignment.ALIGN_NORMAL.ordinal()] = Layout.DIR_REQUEST_DEFAULT_LTR;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public enum Alignment {
        ALIGN_NORMAL,
        ALIGN_OPPOSITE,
        ALIGN_CENTER,
        ALIGN_LEFT,
        ALIGN_RIGHT
    }

    public static class Directions {
        int[] mDirections;

        Directions(int[] dirs) {
            this.mDirections = dirs;
        }
    }

    static class Ellipsizer implements CharSequence, GetChars {
        Layout mLayout;
        TruncateAt mMethod;
        CharSequence mText;
        int mWidth;

        public Ellipsizer(CharSequence s) {
            this.mText = s;
        }

        public char charAt(int off) {
            char[] buf = TextUtils.obtain(Layout.DIR_REQUEST_LTR);
            getChars(off, off + Layout.DIR_REQUEST_LTR, buf, Layout.MIN_EMOJI);
            char ret = buf[Layout.MIN_EMOJI];
            TextUtils.recycle(buf);
            return ret;
        }

        public void getChars(int start, int end, char[] dest, int destoff) {
            int line1 = this.mLayout.getLineForOffset(start);
            int line2 = this.mLayout.getLineForOffset(end);
            TextUtils.getChars(this.mText, start, end, dest, destoff);
            for (int i = line1; i <= line2; i += Layout.DIR_REQUEST_LTR) {
                this.mLayout.ellipsize(start, end, i, dest, destoff, this.mMethod);
            }
        }

        public int length() {
            return this.mText.length();
        }

        public CharSequence subSequence(int start, int end) {
            char[] s = new char[(end - start)];
            getChars(start, end, s, Layout.MIN_EMOJI);
            return new String(s);
        }

        public String toString() {
            char[] s = new char[length()];
            getChars(Layout.MIN_EMOJI, length(), s, Layout.MIN_EMOJI);
            return new String(s);
        }
    }

    static class SpannedEllipsizer extends Ellipsizer implements Spanned {
        private Spanned mSpanned;

        public SpannedEllipsizer(CharSequence display) {
            super(display);
            this.mSpanned = (Spanned) display;
        }

        public <T> T[] getSpans(int start, int end, Class<T> type) {
            return this.mSpanned.getSpans(start, end, type);
        }

        public int getSpanStart(Object tag) {
            return this.mSpanned.getSpanStart(tag);
        }

        public int getSpanEnd(Object tag) {
            return this.mSpanned.getSpanEnd(tag);
        }

        public int getSpanFlags(Object tag) {
            return this.mSpanned.getSpanFlags(tag);
        }

        public int nextSpanTransition(int start, int limit, Class type) {
            return this.mSpanned.nextSpanTransition(start, limit, type);
        }

        public CharSequence subSequence(int start, int end) {
            char[] s = new char[(end - start)];
            getChars(start, end, s, Layout.MIN_EMOJI);
            SpannableString ss = new SpannableString(new String(s));
            TextUtils.copySpansFrom(this.mSpanned, start, end, Object.class, ss, Layout.MIN_EMOJI);
            return ss;
        }
    }

    static class TabStops {
        private int mIncrement;
        private int mNumStops;
        private int[] mStops;

        TabStops(int r1, java.lang.Object[] r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.text.Layout.TabStops.<init>(int, java.lang.Object[]):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.text.Layout.TabStops.<init>(int, java.lang.Object[]):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.text.Layout.TabStops.<init>(int, java.lang.Object[]):void");
        }

        float nextTab(float r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.text.Layout.TabStops.nextTab(float):float
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.text.Layout.TabStops.nextTab(float):float
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.text.Layout.TabStops.nextTab(float):float");
        }

        void reset(int r1, java.lang.Object[] r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.text.Layout.TabStops.reset(int, java.lang.Object[]):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.text.Layout.TabStops.reset(int, java.lang.Object[]):void
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
            throw new UnsupportedOperationException("Method not decompiled: android.text.Layout.TabStops.reset(int, java.lang.Object[]):void");
        }

        public static float nextDefaultStop(float h, int inc) {
            return (float) (((int) ((((float) inc) + h) / ((float) inc))) * inc);
        }
    }

    static {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.text.Layout.<clinit>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.text.Layout.<clinit>():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.Layout.<clinit>():void");
    }

    public abstract int getBottomPadding();

    public abstract int getEllipsisCount(int i);

    public abstract int getEllipsisStart(int i);

    public abstract boolean getLineContainsTab(int i);

    public abstract int getLineCount();

    public abstract int getLineDescent(int i);

    public abstract Directions getLineDirections(int i);

    public abstract int getLineStart(int i);

    public abstract int getLineTop(int i);

    public abstract int getParagraphDirection(int i);

    public abstract int getTopPadding();

    public static float getDesiredWidth(CharSequence source, TextPaint paint) {
        return getDesiredWidth(source, MIN_EMOJI, source.length(), paint);
    }

    public static float getDesiredWidth(CharSequence source, int start, int end, TextPaint paint) {
        float need = 0.0f;
        int i = start;
        while (i <= end) {
            int next = TextUtils.indexOf(source, '\n', i, end);
            if (next < 0) {
                next = end;
            }
            float w = measurePara(paint, source, i, next);
            if (w > need) {
                need = w;
            }
            i = next + DIR_REQUEST_LTR;
        }
        return need;
    }

    protected Layout(CharSequence text, TextPaint paint, int width, Alignment align, float spacingMult, float spacingAdd) {
        this(text, paint, width, align, TextDirectionHeuristics.FIRSTSTRONG_LTR, spacingMult, spacingAdd);
    }

    protected Layout(CharSequence text, TextPaint paint, int width, Alignment align, TextDirectionHeuristic textDir, float spacingMult, float spacingAdd) {
        this.mAlignment = Alignment.ALIGN_NORMAL;
        if (width < 0) {
            throw new IllegalArgumentException("Layout: " + width + " < 0");
        }
        if (paint != null) {
            paint.bgColor = MIN_EMOJI;
            paint.baselineShift = MIN_EMOJI;
        }
        this.mText = text;
        this.mPaint = paint;
        this.mWorkPaint = new TextPaint();
        this.mWidth = width;
        this.mAlignment = align;
        this.mSpacingMult = spacingMult;
        this.mSpacingAdd = spacingAdd;
        this.mSpannedText = text instanceof Spanned;
        this.mTextDir = textDir;
    }

    void replaceWith(CharSequence text, TextPaint paint, int width, Alignment align, float spacingmult, float spacingadd) {
        if (width < 0) {
            throw new IllegalArgumentException("Layout: " + width + " < 0");
        }
        this.mText = text;
        this.mPaint = paint;
        this.mWidth = width;
        this.mAlignment = align;
        this.mSpacingMult = spacingmult;
        this.mSpacingAdd = spacingadd;
        this.mSpannedText = text instanceof Spanned;
    }

    public void draw(Canvas c) {
        draw(c, null, null, MIN_EMOJI);
    }

    public void draw(Canvas canvas, Path highlight, Paint highlightPaint, int cursorOffsetVertical) {
        long lineRange = getLineRangeForDraw(canvas);
        int firstLine = TextUtils.unpackRangeStartFromLong(lineRange);
        int lastLine = TextUtils.unpackRangeEndFromLong(lineRange);
        if (lastLine >= 0) {
            drawBackground(canvas, highlight, highlightPaint, cursorOffsetVertical, firstLine, lastLine);
            drawText(canvas, firstLine, lastLine);
        }
    }

    public void drawText(Canvas canvas, int firstLine, int lastLine) {
        int previousLineBottom = getLineTop(firstLine);
        int previousLineEnd = getLineStart(firstLine);
        ParagraphStyle[] spans = NO_PARA_SPANS;
        int spanEnd = MIN_EMOJI;
        Paint paint = this.mPaint;
        CharSequence buf = this.mText;
        Alignment paraAlign = this.mAlignment;
        boolean tabStopsIsInitialized = false;
        TextLine tl = TextLine.obtain();
        int i = firstLine;
        TabStops tabStops = null;
        while (i <= lastLine) {
            TabStops tabStops2;
            int x;
            int start = previousLineEnd;
            previousLineEnd = getLineStart(i + DIR_REQUEST_LTR);
            int end = getLineVisibleEnd(i, start, previousLineEnd);
            int ltop = previousLineBottom;
            int lbottom = getLineTop(i + DIR_REQUEST_LTR);
            previousLineBottom = lbottom;
            int lbaseline = lbottom - getLineDescent(i);
            int dir = getParagraphDirection(i);
            int left = MIN_EMOJI;
            int right = this.mWidth;
            if (this.mSpannedText) {
                boolean isFirstParaLine;
                int n;
                Spanned sp = (Spanned) buf;
                int textLength = buf.length();
                if (start == 0 || buf.charAt(start + DIR_RIGHT_TO_LEFT) == '\n') {
                    isFirstParaLine = true;
                } else {
                    isFirstParaLine = false;
                }
                if (start >= spanEnd && (i == firstLine || isFirstParaLine)) {
                    spanEnd = sp.nextSpanTransition(start, textLength, ParagraphStyle.class);
                    spans = (ParagraphStyle[]) getParagraphSpans(sp, start, spanEnd, ParagraphStyle.class);
                    paraAlign = this.mAlignment;
                    for (n = spans.length + DIR_RIGHT_TO_LEFT; n >= 0; n += DIR_RIGHT_TO_LEFT) {
                        if (spans[n] instanceof AlignmentSpan) {
                            paraAlign = ((AlignmentSpan) spans[n]).getAlignment();
                            break;
                        }
                    }
                    tabStopsIsInitialized = false;
                }
                int length = spans.length;
                boolean useFirstLineMargin = isFirstParaLine;
                for (n = MIN_EMOJI; n < length; n += DIR_REQUEST_LTR) {
                    if (spans[n] instanceof LeadingMarginSpan2) {
                        if (i < getLineForOffset(sp.getSpanStart(spans[n])) + ((LeadingMarginSpan2) spans[n]).getLeadingMarginLineCount()) {
                            useFirstLineMargin = true;
                            break;
                        }
                    }
                }
                for (n = MIN_EMOJI; n < length; n += DIR_REQUEST_LTR) {
                    if (spans[n] instanceof LeadingMarginSpan) {
                        LeadingMarginSpan margin = spans[n];
                        if (dir == DIR_RIGHT_TO_LEFT) {
                            margin.drawLeadingMargin(canvas, paint, right, dir, ltop, lbaseline, lbottom, buf, start, end, isFirstParaLine, this);
                            right -= margin.getLeadingMargin(useFirstLineMargin);
                        } else {
                            margin.drawLeadingMargin(canvas, paint, left, dir, ltop, lbaseline, lbottom, buf, start, end, isFirstParaLine, this);
                            left += margin.getLeadingMargin(useFirstLineMargin);
                        }
                    }
                }
            }
            boolean hasTabOrEmoji = getLineContainsTab(i);
            if (!hasTabOrEmoji || tabStopsIsInitialized) {
                tabStops2 = tabStops;
            } else {
                if (tabStops == null) {
                    TabStops tabStops3 = new TabStops(TAB_INCREMENT, spans);
                } else {
                    tabStops.reset(TAB_INCREMENT, spans);
                    tabStops2 = tabStops;
                }
                tabStopsIsInitialized = true;
            }
            Alignment align = paraAlign;
            if (align == Alignment.ALIGN_LEFT) {
                align = dir == DIR_REQUEST_LTR ? Alignment.ALIGN_NORMAL : Alignment.ALIGN_OPPOSITE;
            } else if (align == Alignment.ALIGN_RIGHT) {
                align = dir == DIR_REQUEST_LTR ? Alignment.ALIGN_OPPOSITE : Alignment.ALIGN_NORMAL;
            }
            if (align != Alignment.ALIGN_NORMAL) {
                int max = (int) getLineExtent(i, tabStops2, false);
                if (align != Alignment.ALIGN_OPPOSITE) {
                    x = ((right + left) - (max & DIR_REQUEST_DEFAULT_RTL)) >> DIR_REQUEST_LTR;
                } else if (dir == DIR_REQUEST_LTR) {
                    x = right - max;
                } else {
                    x = left - max;
                }
            } else if (dir == DIR_REQUEST_LTR) {
                x = left;
            } else {
                x = right;
            }
            Directions directions = getLineDirections(i);
            if (directions != DIRS_ALL_LEFT_TO_RIGHT || this.mSpannedText || hasTabOrEmoji) {
                tl.set(paint, buf, start, end, dir, directions, hasTabOrEmoji, tabStops2);
                tl.draw(canvas, (float) x, ltop, lbaseline, lbottom);
            } else {
                canvas.drawText(buf, start, end, (float) x, (float) lbaseline, paint);
            }
            i += DIR_REQUEST_LTR;
            tabStops = tabStops2;
        }
        TextLine.recycle(tl);
    }

    public void drawBackground(Canvas canvas, Path highlight, Paint highlightPaint, int cursorOffsetVertical, int firstLine, int lastLine) {
        if (this.mSpannedText) {
            if (this.mLineBackgroundSpans == null) {
                this.mLineBackgroundSpans = new SpanSet(LineBackgroundSpan.class);
            }
            Spanned buffer = this.mText;
            int textLength = buffer.length();
            this.mLineBackgroundSpans.init(buffer, MIN_EMOJI, textLength);
            if (this.mLineBackgroundSpans.numberOfSpans > 0) {
                int previousLineBottom = getLineTop(firstLine);
                int previousLineEnd = getLineStart(firstLine);
                ParagraphStyle[] spans = NO_PARA_SPANS;
                int spansLength = MIN_EMOJI;
                TextPaint paint = this.mPaint;
                int spanEnd = MIN_EMOJI;
                int width = this.mWidth;
                for (int i = firstLine; i <= lastLine; i += DIR_REQUEST_LTR) {
                    int start = previousLineEnd;
                    int end = getLineStart(i + DIR_REQUEST_LTR);
                    previousLineEnd = end;
                    int ltop = previousLineBottom;
                    int lbottom = getLineTop(i + DIR_REQUEST_LTR);
                    previousLineBottom = lbottom;
                    int lbaseline = lbottom - getLineDescent(i);
                    if (start >= spanEnd) {
                        spanEnd = this.mLineBackgroundSpans.getNextTransition(start, textLength);
                        spansLength = MIN_EMOJI;
                        if (start != end || start == 0) {
                            int j = MIN_EMOJI;
                            while (j < this.mLineBackgroundSpans.numberOfSpans) {
                                if (this.mLineBackgroundSpans.spanStarts[j] < end && this.mLineBackgroundSpans.spanEnds[j] > start) {
                                    spans = (ParagraphStyle[]) GrowingArrayUtils.append((Object[]) spans, spansLength, ((LineBackgroundSpan[]) this.mLineBackgroundSpans.spans)[j]);
                                    spansLength += DIR_REQUEST_LTR;
                                }
                                j += DIR_REQUEST_LTR;
                            }
                        }
                    }
                    for (int n = MIN_EMOJI; n < spansLength; n += DIR_REQUEST_LTR) {
                        spans[n].drawBackground(canvas, paint, MIN_EMOJI, width, ltop, lbaseline, lbottom, buffer, start, end, i);
                    }
                }
            }
            this.mLineBackgroundSpans.recycle();
        }
        if (highlight != null) {
            if (cursorOffsetVertical != 0) {
                canvas.translate(0.0f, (float) cursorOffsetVertical);
            }
            canvas.drawPath(highlight, highlightPaint);
            if (cursorOffsetVertical != 0) {
                canvas.translate(0.0f, (float) (-cursorOffsetVertical));
            }
        }
    }

    public long getLineRangeForDraw(Canvas canvas) {
        synchronized (sTempRect) {
            if (canvas.getClipBounds(sTempRect)) {
                int dtop = sTempRect.top;
                int dbottom = sTempRect.bottom;
                int top = Math.max(dtop, MIN_EMOJI);
                int bottom = Math.min(getLineTop(getLineCount()), dbottom);
                if (top >= bottom) {
                    return TextUtils.packRangeInLong(MIN_EMOJI, DIR_RIGHT_TO_LEFT);
                }
                return TextUtils.packRangeInLong(getLineForVertical(top), getLineForVertical(bottom));
            }
            long packRangeInLong = TextUtils.packRangeInLong(MIN_EMOJI, DIR_RIGHT_TO_LEFT);
            return packRangeInLong;
        }
    }

    private int getLineStartPos(int line, int left, int right) {
        Alignment align = getParagraphAlignment(line);
        int dir = getParagraphDirection(line);
        if (align == Alignment.ALIGN_LEFT) {
            align = dir == DIR_REQUEST_LTR ? Alignment.ALIGN_NORMAL : Alignment.ALIGN_OPPOSITE;
        } else if (align == Alignment.ALIGN_RIGHT) {
            align = dir == DIR_REQUEST_LTR ? Alignment.ALIGN_OPPOSITE : Alignment.ALIGN_NORMAL;
        }
        if (align != Alignment.ALIGN_NORMAL) {
            TabStops tabStops = null;
            if (this.mSpannedText && getLineContainsTab(line)) {
                Spanned spanned = this.mText;
                int start = getLineStart(line);
                TabStopSpan[] tabSpans = (TabStopSpan[]) getParagraphSpans(spanned, start, spanned.nextSpanTransition(start, spanned.length(), TabStopSpan.class), TabStopSpan.class);
                if (tabSpans.length > 0) {
                    tabStops = new TabStops(TAB_INCREMENT, tabSpans);
                }
            }
            int max = (int) getLineExtent(line, tabStops, false);
            if (align != Alignment.ALIGN_OPPOSITE) {
                return ((left + right) - (max & DIR_REQUEST_DEFAULT_RTL)) >> DIR_REQUEST_LTR;
            } else if (dir == DIR_REQUEST_LTR) {
                return right - max;
            } else {
                return left - max;
            }
        } else if (dir == DIR_REQUEST_LTR) {
            return left;
        } else {
            return right;
        }
    }

    public final CharSequence getText() {
        return this.mText;
    }

    public final TextPaint getPaint() {
        return this.mPaint;
    }

    public final int getWidth() {
        return this.mWidth;
    }

    public int getEllipsizedWidth() {
        return this.mWidth;
    }

    public final void increaseWidthTo(int wid) {
        if (wid < this.mWidth) {
            throw new RuntimeException("attempted to reduce Layout width");
        }
        this.mWidth = wid;
    }

    public int getHeight() {
        return getLineTop(getLineCount());
    }

    public final Alignment getAlignment() {
        return this.mAlignment;
    }

    public final float getSpacingMultiplier() {
        return this.mSpacingMult;
    }

    public final float getSpacingAdd() {
        return this.mSpacingAdd;
    }

    public final TextDirectionHeuristic getTextDirectionHeuristic() {
        return this.mTextDir;
    }

    public int getLineBounds(int line, Rect bounds) {
        if (bounds != null) {
            bounds.left = MIN_EMOJI;
            bounds.top = getLineTop(line);
            bounds.right = this.mWidth;
            bounds.bottom = getLineTop(line + DIR_REQUEST_LTR);
        }
        return getLineBaseline(line);
    }

    public boolean isLevelBoundary(int offset) {
        int line = getLineForOffset(offset);
        Directions dirs = getLineDirections(line);
        if (dirs == DIRS_ALL_LEFT_TO_RIGHT || dirs == DIRS_ALL_RIGHT_TO_LEFT) {
            return false;
        }
        int[] runs = dirs.mDirections;
        int lineStart = getLineStart(line);
        int lineEnd = getLineEnd(line);
        if (offset == lineStart || offset == lineEnd) {
            int paraLevel;
            if (getParagraphDirection(line) == DIR_REQUEST_LTR) {
                paraLevel = MIN_EMOJI;
            } else {
                paraLevel = DIR_REQUEST_LTR;
            }
            if (((runs[(offset == lineStart ? MIN_EMOJI : runs.length + DIR_REQUEST_DEFAULT_RTL) + DIR_REQUEST_LTR] >>> RUN_LEVEL_SHIFT) & RUN_LEVEL_MASK) != paraLevel) {
                return true;
            }
            return false;
        }
        offset -= lineStart;
        for (int i = MIN_EMOJI; i < runs.length; i += DIR_REQUEST_DEFAULT_LTR) {
            if (offset == runs[i]) {
                return true;
            }
        }
        return false;
    }

    public boolean isRtlCharAt(int offset) {
        boolean z = true;
        int line = getLineForOffset(offset);
        Directions dirs = getLineDirections(line);
        if (dirs == DIRS_ALL_LEFT_TO_RIGHT) {
            return false;
        }
        if (dirs == DIRS_ALL_RIGHT_TO_LEFT) {
            return true;
        }
        int[] runs = dirs.mDirections;
        int lineStart = getLineStart(line);
        int i = MIN_EMOJI;
        while (i < runs.length) {
            int start = lineStart + runs[i];
            int limit = start + (runs[i + DIR_REQUEST_LTR] & RUN_LENGTH_MASK);
            if (offset < start || offset >= limit) {
                i += DIR_REQUEST_DEFAULT_LTR;
            } else {
                if ((((runs[i + DIR_REQUEST_LTR] >>> RUN_LEVEL_SHIFT) & RUN_LEVEL_MASK) & DIR_REQUEST_LTR) == 0) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    private boolean primaryIsTrailingPrevious(int offset) {
        int levelBefore;
        boolean z = true;
        int line = getLineForOffset(offset);
        int lineStart = getLineStart(line);
        int lineEnd = getLineEnd(line);
        int[] runs = getLineDirections(line).mDirections;
        int levelAt = DIR_RIGHT_TO_LEFT;
        int i = MIN_EMOJI;
        while (i < runs.length) {
            int start = lineStart + runs[i];
            int limit = start + (runs[i + DIR_REQUEST_LTR] & RUN_LENGTH_MASK);
            if (limit > lineEnd) {
                limit = lineEnd;
            }
            if (offset < start || offset >= limit) {
                i += DIR_REQUEST_DEFAULT_LTR;
            } else if (offset > start) {
                return false;
            } else {
                levelAt = (runs[i + DIR_REQUEST_LTR] >>> RUN_LEVEL_SHIFT) & RUN_LEVEL_MASK;
                if (levelAt == DIR_RIGHT_TO_LEFT) {
                    if (getParagraphDirection(line) != DIR_REQUEST_LTR) {
                        levelAt = MIN_EMOJI;
                    } else {
                        levelAt = DIR_REQUEST_LTR;
                    }
                }
                levelBefore = DIR_RIGHT_TO_LEFT;
                if (offset == lineStart) {
                    offset += DIR_RIGHT_TO_LEFT;
                    for (i = MIN_EMOJI; i < runs.length; i += DIR_REQUEST_DEFAULT_LTR) {
                        start = lineStart + runs[i];
                        limit = start + (runs[i + DIR_REQUEST_LTR] & RUN_LENGTH_MASK);
                        if (limit > lineEnd) {
                            limit = lineEnd;
                        }
                        if (offset < start && offset < limit) {
                            levelBefore = (runs[i + DIR_REQUEST_LTR] >>> RUN_LEVEL_SHIFT) & RUN_LEVEL_MASK;
                            break;
                        }
                    }
                } else if (getParagraphDirection(line) != DIR_REQUEST_LTR) {
                    levelBefore = MIN_EMOJI;
                } else {
                    levelBefore = DIR_REQUEST_LTR;
                }
                if (levelBefore >= levelAt) {
                    z = false;
                }
                return z;
            }
        }
        if (levelAt == DIR_RIGHT_TO_LEFT) {
            if (getParagraphDirection(line) != DIR_REQUEST_LTR) {
                levelAt = DIR_REQUEST_LTR;
            } else {
                levelAt = MIN_EMOJI;
            }
        }
        levelBefore = DIR_RIGHT_TO_LEFT;
        if (offset == lineStart) {
            offset += DIR_RIGHT_TO_LEFT;
            while (i < runs.length) {
                start = lineStart + runs[i];
                limit = start + (runs[i + DIR_REQUEST_LTR] & RUN_LENGTH_MASK);
                if (limit > lineEnd) {
                    limit = lineEnd;
                }
                if (offset < start) {
                }
            }
        } else if (getParagraphDirection(line) != DIR_REQUEST_LTR) {
            levelBefore = DIR_REQUEST_LTR;
        } else {
            levelBefore = MIN_EMOJI;
        }
        if (levelBefore >= levelAt) {
            z = false;
        }
        return z;
    }

    public float getPrimaryHorizontal(int offset) {
        return getPrimaryHorizontal(offset, false);
    }

    public float getPrimaryHorizontal(int offset, boolean clamped) {
        return getHorizontal(offset, primaryIsTrailingPrevious(offset), clamped);
    }

    public float getSecondaryHorizontal(int offset) {
        return getSecondaryHorizontal(offset, false);
    }

    public float getSecondaryHorizontal(int offset, boolean clamped) {
        return getHorizontal(offset, !primaryIsTrailingPrevious(offset), clamped);
    }

    private float getHorizontal(int offset, boolean trailing, boolean clamped) {
        return getHorizontal(offset, trailing, getLineForOffset(offset), clamped);
    }

    private float getHorizontal(int offset, boolean trailing, int line, boolean clamped) {
        int start = getLineStart(line);
        int end = getLineEnd(line);
        int dir = getParagraphDirection(line);
        boolean hasTabOrEmoji = getLineContainsTab(line);
        Directions directions = getLineDirections(line);
        TabStops tabStops = null;
        if (hasTabOrEmoji && (this.mText instanceof Spanned)) {
            TabStopSpan[] tabs = (TabStopSpan[]) getParagraphSpans((Spanned) this.mText, start, end, TabStopSpan.class);
            if (tabs.length > 0) {
                tabStops = new TabStops(TAB_INCREMENT, tabs);
            }
        }
        TextLine tl = TextLine.obtain();
        tl.set(this.mPaint, this.mText, start, end, dir, directions, hasTabOrEmoji, tabStops);
        float wid = tl.measure(offset - start, trailing, null);
        TextLine.recycle(tl);
        if (clamped && wid > ((float) this.mWidth)) {
            wid = (float) this.mWidth;
        }
        return ((float) getLineStartPos(line, getParagraphLeft(line), getParagraphRight(line))) + wid;
    }

    public float getLineLeft(int line) {
        int dir = getParagraphDirection(line);
        Alignment align = getParagraphAlignment(line);
        if (align == Alignment.ALIGN_LEFT) {
            return 0.0f;
        }
        if (align == Alignment.ALIGN_NORMAL) {
            if (dir == DIR_RIGHT_TO_LEFT) {
                return ((float) getParagraphRight(line)) - getLineMax(line);
            }
            return 0.0f;
        } else if (align == Alignment.ALIGN_RIGHT) {
            return ((float) this.mWidth) - getLineMax(line);
        } else {
            if (align != Alignment.ALIGN_OPPOSITE) {
                int left = getParagraphLeft(line);
                return (float) ((((getParagraphRight(line) - left) - (((int) getLineMax(line)) & DIR_REQUEST_DEFAULT_RTL)) / DIR_REQUEST_DEFAULT_LTR) + left);
            } else if (dir != DIR_RIGHT_TO_LEFT) {
                return ((float) this.mWidth) - getLineMax(line);
            } else {
                return 0.0f;
            }
        }
    }

    public float getLineRight(int line) {
        int dir = getParagraphDirection(line);
        Alignment align = getParagraphAlignment(line);
        if (align == Alignment.ALIGN_LEFT) {
            return ((float) getParagraphLeft(line)) + getLineMax(line);
        }
        if (align == Alignment.ALIGN_NORMAL) {
            if (dir == DIR_RIGHT_TO_LEFT) {
                return (float) this.mWidth;
            }
            return ((float) getParagraphLeft(line)) + getLineMax(line);
        } else if (align == Alignment.ALIGN_RIGHT) {
            return (float) this.mWidth;
        } else {
            if (align != Alignment.ALIGN_OPPOSITE) {
                int left = getParagraphLeft(line);
                int right = getParagraphRight(line);
                return (float) (right - (((right - left) - (((int) getLineMax(line)) & DIR_REQUEST_DEFAULT_RTL)) / DIR_REQUEST_DEFAULT_LTR));
            } else if (dir == DIR_RIGHT_TO_LEFT) {
                return getLineMax(line);
            } else {
                return (float) this.mWidth;
            }
        }
    }

    public float getLineMax(int line) {
        float margin = (float) getParagraphLeadingMargin(line);
        float signedExtent = getLineExtent(line, false);
        if (signedExtent < 0.0f) {
            signedExtent = -signedExtent;
        }
        return margin + signedExtent;
    }

    public float getLineWidth(int line) {
        float margin = (float) getParagraphLeadingMargin(line);
        float signedExtent = getLineExtent(line, true);
        if (signedExtent < 0.0f) {
            signedExtent = -signedExtent;
        }
        return margin + signedExtent;
    }

    private float getLineExtent(int line, boolean full) {
        int start = getLineStart(line);
        int end = full ? getLineEnd(line) : getLineVisibleEnd(line);
        boolean hasTabsOrEmoji = getLineContainsTab(line);
        TabStops tabStops = null;
        if (hasTabsOrEmoji && (this.mText instanceof Spanned)) {
            TabStopSpan[] tabs = (TabStopSpan[]) getParagraphSpans((Spanned) this.mText, start, end, TabStopSpan.class);
            if (tabs.length > 0) {
                tabStops = new TabStops(TAB_INCREMENT, tabs);
            }
        }
        Directions directions = getLineDirections(line);
        if (directions == null) {
            return 0.0f;
        }
        int dir = getParagraphDirection(line);
        TextLine tl = TextLine.obtain();
        tl.set(this.mPaint, this.mText, start, end, dir, directions, hasTabsOrEmoji, tabStops);
        float width = tl.metrics(null);
        TextLine.recycle(tl);
        return width;
    }

    private float getLineExtent(int line, TabStops tabStops, boolean full) {
        int start = getLineStart(line);
        int end = full ? getLineEnd(line) : getLineVisibleEnd(line);
        boolean hasTabsOrEmoji = getLineContainsTab(line);
        Directions directions = getLineDirections(line);
        int dir = getParagraphDirection(line);
        TextLine tl = TextLine.obtain();
        tl.set(this.mPaint, this.mText, start, end, dir, directions, hasTabsOrEmoji, tabStops);
        float width = tl.metrics(null);
        TextLine.recycle(tl);
        return width;
    }

    public int getLineForVertical(int vertical) {
        int high = getLineCount();
        int low = DIR_RIGHT_TO_LEFT;
        while (high - low > DIR_REQUEST_LTR) {
            int guess = (high + low) / DIR_REQUEST_DEFAULT_LTR;
            if (getLineTop(guess) > vertical) {
                high = guess;
            } else {
                low = guess;
            }
        }
        if (low < 0) {
            return MIN_EMOJI;
        }
        return low;
    }

    public int getLineForOffset(int offset) {
        int high = getLineCount();
        int low = DIR_RIGHT_TO_LEFT;
        while (high - low > DIR_REQUEST_LTR) {
            int guess = (high + low) / DIR_REQUEST_DEFAULT_LTR;
            if (getLineStart(guess) > offset) {
                high = guess;
            } else {
                low = guess;
            }
        }
        if (low < 0) {
            return MIN_EMOJI;
        }
        return low;
    }

    public int getOffsetForHorizontal(int line, float horiz) {
        float dist;
        int max = getLineEnd(line) + DIR_RIGHT_TO_LEFT;
        int min = getLineStart(line);
        Directions dirs = getLineDirections(line);
        if (line == getLineCount() + DIR_RIGHT_TO_LEFT) {
            max += DIR_REQUEST_LTR;
        }
        int best = min;
        float bestdist = Math.abs(getPrimaryHorizontal(best) - horiz);
        int i = MIN_EMOJI;
        while (true) {
            int length = dirs.mDirections.length;
            if (i >= r0) {
                break;
            }
            int here = min + dirs.mDirections[i];
            int there = here + (dirs.mDirections[i + DIR_REQUEST_LTR] & RUN_LENGTH_MASK);
            int swap = (dirs.mDirections[i + DIR_REQUEST_LTR] & RUN_RTL_FLAG) != 0 ? DIR_RIGHT_TO_LEFT : DIR_REQUEST_LTR;
            if (there > max) {
                there = max;
            }
            int high = (there + DIR_RIGHT_TO_LEFT) + DIR_REQUEST_LTR;
            int low = (here + DIR_REQUEST_LTR) + DIR_RIGHT_TO_LEFT;
            while (high - low > DIR_REQUEST_LTR) {
                int guess = (high + low) / DIR_REQUEST_DEFAULT_LTR;
                if (getPrimaryHorizontal(getOffsetAtStartOf(guess)) * ((float) swap) >= ((float) swap) * horiz) {
                    high = guess;
                } else {
                    low = guess;
                }
            }
            if (low < here + DIR_REQUEST_LTR) {
                low = here + DIR_REQUEST_LTR;
            }
            if (low < there) {
                low = getOffsetAtStartOf(low);
                dist = Math.abs(getPrimaryHorizontal(low) - horiz);
                int aft = TextUtils.getOffsetAfter(this.mText, low);
                if (aft < there) {
                    float other = Math.abs(getPrimaryHorizontal(aft) - horiz);
                    if (other < dist) {
                        dist = other;
                        low = aft;
                    }
                }
                if (dist < bestdist) {
                    bestdist = dist;
                    best = low;
                }
            }
            dist = Math.abs(getPrimaryHorizontal(here) - horiz);
            if (dist < bestdist) {
                bestdist = dist;
                best = here;
            }
            i += DIR_REQUEST_DEFAULT_LTR;
        }
        dist = Math.abs(getPrimaryHorizontal(max) - horiz);
        if (dist > bestdist) {
            return best;
        }
        bestdist = dist;
        return max;
    }

    public final int getLineEnd(int line) {
        return getLineStart(line + DIR_REQUEST_LTR);
    }

    public int getLineVisibleEnd(int line) {
        return getLineVisibleEnd(line, getLineStart(line), getLineStart(line + DIR_REQUEST_LTR));
    }

    private int getLineVisibleEnd(int line, int start, int end) {
        CharSequence text = this.mText;
        if (line == getLineCount() + DIR_RIGHT_TO_LEFT) {
            return end;
        }
        while (end > start) {
            char ch = text.charAt(end + DIR_RIGHT_TO_LEFT);
            if (ch != '\n') {
                if (ch != ' ' && ch != '\t') {
                    break;
                }
                end += DIR_RIGHT_TO_LEFT;
            } else {
                return end + DIR_RIGHT_TO_LEFT;
            }
        }
        return end;
    }

    public final int getLineBottom(int line) {
        return getLineTop(line + DIR_REQUEST_LTR);
    }

    public final int getLineBaseline(int line) {
        return getLineTop(line + DIR_REQUEST_LTR) - getLineDescent(line);
    }

    public final int getLineAscent(int line) {
        return getLineTop(line) - (getLineTop(line + DIR_REQUEST_LTR) - getLineDescent(line));
    }

    public int getOffsetToLeftOf(int offset) {
        return getOffsetToLeftRightOf(offset, true);
    }

    public int getOffsetToRightOf(int offset) {
        return getOffsetToLeftRightOf(offset, false);
    }

    private int getOffsetToLeftRightOf(int caret, boolean toLeft) {
        int line = getLineForOffset(caret);
        int lineStart = getLineStart(line);
        int lineEnd = getLineEnd(line);
        int lineDir = getParagraphDirection(line);
        boolean lineChanged = false;
        if (toLeft == (lineDir == DIR_RIGHT_TO_LEFT)) {
            if (caret == lineEnd) {
                if (line >= getLineCount() + DIR_RIGHT_TO_LEFT) {
                    return caret;
                }
                lineChanged = true;
                line += DIR_REQUEST_LTR;
            }
        } else if (caret == lineStart) {
            if (line <= 0) {
                return caret;
            }
            lineChanged = true;
            line += DIR_RIGHT_TO_LEFT;
        }
        if (lineChanged) {
            lineStart = getLineStart(line);
            lineEnd = getLineEnd(line);
            int newDir = getParagraphDirection(line);
            if (newDir != lineDir) {
                toLeft = !toLeft;
                lineDir = newDir;
            }
        }
        Directions directions = getLineDirections(line);
        TextLine tl = TextLine.obtain();
        tl.set(this.mPaint, this.mText, lineStart, lineEnd, lineDir, directions, false, null);
        caret = lineStart + tl.getOffsetToLeftRightOf(caret - lineStart, toLeft);
        tl = TextLine.recycle(tl);
        return caret;
    }

    private int getOffsetAtStartOf(int offset) {
        if (offset == 0) {
            return MIN_EMOJI;
        }
        CharSequence text = this.mText;
        char c = text.charAt(offset);
        if (c >= '\udc00' && c <= '\udfff') {
            char c1 = text.charAt(offset + DIR_RIGHT_TO_LEFT);
            if (c1 >= '\ud800' && c1 <= '\udbff') {
                offset += DIR_RIGHT_TO_LEFT;
            }
        }
        if (this.mSpannedText) {
            ReplacementSpan[] spans = (ReplacementSpan[]) ((Spanned) text).getSpans(offset, offset, ReplacementSpan.class);
            for (int i = MIN_EMOJI; i < spans.length; i += DIR_REQUEST_LTR) {
                int start = ((Spanned) text).getSpanStart(spans[i]);
                int end = ((Spanned) text).getSpanEnd(spans[i]);
                if (start < offset && end > offset) {
                    offset = start;
                }
            }
        }
        return offset;
    }

    public boolean shouldClampCursor(int line) {
        switch (AnonymousClass1.$SwitchMap$android$text$Layout$Alignment[getParagraphAlignment(line).ordinal()]) {
            case DIR_REQUEST_LTR /*1*/:
                return true;
            case DIR_REQUEST_DEFAULT_LTR /*2*/:
                if (getParagraphDirection(line) <= 0) {
                    return false;
                }
                return true;
            default:
                return false;
        }
    }

    public void getCursorPath(int point, Path dest, CharSequence editingBuffer) {
        float h2;
        dest.reset();
        int line = getLineForOffset(point);
        int top = getLineTop(line);
        int bottom = getLineTop(line + DIR_REQUEST_LTR);
        boolean clamped = shouldClampCursor(line);
        float h1 = getPrimaryHorizontal(point, clamped) - 0.5f;
        if (isLevelBoundary(point)) {
            h2 = getSecondaryHorizontal(point, clamped) - 0.5f;
        } else {
            h2 = h1;
        }
        int caps = MetaKeyKeyListener.getMetaState(editingBuffer, (int) DIR_REQUEST_LTR) | MetaKeyKeyListener.getMetaState(editingBuffer, (int) GL10.GL_EXP);
        int fn = MetaKeyKeyListener.getMetaState(editingBuffer, (int) DIR_REQUEST_DEFAULT_LTR);
        int dist = MIN_EMOJI;
        if (!(caps == 0 && fn == 0)) {
            dist = (bottom - top) >> DIR_REQUEST_DEFAULT_LTR;
            if (fn != 0) {
                top += dist;
            }
            if (caps != 0) {
                bottom -= dist;
            }
        }
        if (h1 < 0.5f) {
            h1 = 0.5f;
        }
        if (h2 < 0.5f) {
            h2 = 0.5f;
        }
        if (Float.compare(h1, h2) == 0) {
            dest.moveTo(h1, (float) top);
            dest.lineTo(h1, (float) bottom);
        } else {
            dest.moveTo(h1, (float) top);
            dest.lineTo(h1, (float) ((top + bottom) >> DIR_REQUEST_LTR));
            dest.moveTo(h2, (float) ((top + bottom) >> DIR_REQUEST_LTR));
            dest.lineTo(h2, (float) bottom);
        }
        if (caps == DIR_REQUEST_DEFAULT_LTR) {
            dest.moveTo(h2, (float) bottom);
            dest.lineTo(h2 - ((float) dist), (float) (bottom + dist));
            dest.lineTo(h2, (float) bottom);
            dest.lineTo(((float) dist) + h2, (float) (bottom + dist));
        } else if (caps == DIR_REQUEST_LTR) {
            dest.moveTo(h2, (float) bottom);
            dest.lineTo(h2 - ((float) dist), (float) (bottom + dist));
            dest.moveTo(h2 - ((float) dist), ((float) (bottom + dist)) - 0.5f);
            dest.lineTo(((float) dist) + h2, ((float) (bottom + dist)) - 0.5f);
            dest.moveTo(((float) dist) + h2, (float) (bottom + dist));
            dest.lineTo(h2, (float) bottom);
        }
        if (fn == DIR_REQUEST_DEFAULT_LTR) {
            dest.moveTo(h1, (float) top);
            dest.lineTo(h1 - ((float) dist), (float) (top - dist));
            dest.lineTo(h1, (float) top);
            dest.lineTo(((float) dist) + h1, (float) (top - dist));
        } else if (fn == DIR_REQUEST_LTR) {
            dest.moveTo(h1, (float) top);
            dest.lineTo(h1 - ((float) dist), (float) (top - dist));
            dest.moveTo(h1 - ((float) dist), ((float) (top - dist)) + 0.5f);
            dest.lineTo(((float) dist) + h1, ((float) (top - dist)) + 0.5f);
            dest.moveTo(((float) dist) + h1, (float) (top - dist));
            dest.lineTo(h1, (float) top);
        }
    }

    private void addSelection(int line, int start, int end, int top, int bottom, Path dest) {
        int linestart = getLineStart(line);
        int lineend = getLineEnd(line);
        Directions dirs = getLineDirections(line);
        if (lineend > linestart && this.mText.charAt(lineend + DIR_RIGHT_TO_LEFT) == '\n') {
            lineend += DIR_RIGHT_TO_LEFT;
        }
        for (int i = MIN_EMOJI; i < dirs.mDirections.length; i += DIR_REQUEST_DEFAULT_LTR) {
            int here = linestart + dirs.mDirections[i];
            int there = here + (dirs.mDirections[i + DIR_REQUEST_LTR] & RUN_LENGTH_MASK);
            if (there > lineend) {
                there = lineend;
            }
            if (start <= there && end >= here) {
                int st = Math.max(start, here);
                int en = Math.min(end, there);
                if (st != en) {
                    float h1 = getHorizontal(st, false, line, false);
                    float h2 = getHorizontal(en, true, line, false);
                    Path path = dest;
                    path.addRect(Math.min(h1, h2), (float) top, Math.max(h1, h2), (float) bottom, Direction.CW);
                }
            }
        }
    }

    public void getSelectionPath(int start, int end, Path dest) {
        dest.reset();
        if (start != end) {
            if (end < start) {
                int temp = end;
                end = start;
                start = temp;
            }
            int startline = getLineForOffset(start);
            int endline = getLineForOffset(end);
            int top = getLineTop(startline);
            int bottom = getLineBottom(endline);
            if (startline == endline) {
                addSelection(startline, start, end, top, bottom, dest);
                return;
            }
            float width = (float) this.mWidth;
            addSelection(startline, start, getLineEnd(startline), top, getLineBottom(startline), dest);
            if (getParagraphDirection(startline) == DIR_RIGHT_TO_LEFT) {
                dest.addRect(getLineLeft(startline), (float) top, 0.0f, (float) getLineBottom(startline), Direction.CW);
            } else {
                dest.addRect(getLineRight(startline), (float) top, width, (float) getLineBottom(startline), Direction.CW);
            }
            for (int i = startline + DIR_REQUEST_LTR; i < endline; i += DIR_REQUEST_LTR) {
                Path path = dest;
                float f = width;
                path.addRect(0.0f, (float) getLineTop(i), f, (float) getLineBottom(i), Direction.CW);
            }
            top = getLineTop(endline);
            bottom = getLineBottom(endline);
            addSelection(endline, getLineStart(endline), end, top, bottom, dest);
            if (getParagraphDirection(endline) == DIR_RIGHT_TO_LEFT) {
                dest.addRect(width, (float) top, getLineRight(endline), (float) bottom, Direction.CW);
                return;
            }
            dest.addRect(0.0f, (float) top, getLineLeft(endline), (float) bottom, Direction.CW);
        }
    }

    public final Alignment getParagraphAlignment(int line) {
        Alignment align = this.mAlignment;
        if (!this.mSpannedText) {
            return align;
        }
        AlignmentSpan[] spans = (AlignmentSpan[]) getParagraphSpans(this.mText, getLineStart(line), getLineEnd(line), AlignmentSpan.class);
        int spanLength = spans.length;
        if (spanLength > 0) {
            return spans[spanLength + DIR_RIGHT_TO_LEFT].getAlignment();
        }
        return align;
    }

    public final int getParagraphLeft(int line) {
        if (getParagraphDirection(line) == DIR_RIGHT_TO_LEFT || !this.mSpannedText) {
            return MIN_EMOJI;
        }
        return getParagraphLeadingMargin(line);
    }

    public final int getParagraphRight(int line) {
        int right = this.mWidth;
        return (getParagraphDirection(line) == DIR_REQUEST_LTR || !this.mSpannedText) ? right : right - getParagraphLeadingMargin(line);
    }

    private int getParagraphLeadingMargin(int line) {
        if (!this.mSpannedText) {
            return MIN_EMOJI;
        }
        Spanned spanned = this.mText;
        int lineStart = getLineStart(line);
        LeadingMarginSpan[] spans = (LeadingMarginSpan[]) getParagraphSpans(spanned, lineStart, spanned.nextSpanTransition(lineStart, getLineEnd(line), LeadingMarginSpan.class), LeadingMarginSpan.class);
        if (spans.length == 0) {
            return MIN_EMOJI;
        }
        int i;
        int margin = MIN_EMOJI;
        boolean isFirstParaLine = lineStart == 0 || spanned.charAt(lineStart + DIR_RIGHT_TO_LEFT) == '\n';
        boolean useFirstLineMargin = isFirstParaLine;
        for (i = MIN_EMOJI; i < spans.length; i += DIR_REQUEST_LTR) {
            if (spans[i] instanceof LeadingMarginSpan2) {
                int i2;
                if (line < getLineForOffset(spanned.getSpanStart(spans[i])) + ((LeadingMarginSpan2) spans[i]).getLeadingMarginLineCount()) {
                    i2 = DIR_REQUEST_LTR;
                } else {
                    i2 = MIN_EMOJI;
                }
                useFirstLineMargin |= i2;
            }
        }
        for (i = MIN_EMOJI; i < spans.length; i += DIR_REQUEST_LTR) {
            margin += spans[i].getLeadingMargin(useFirstLineMargin);
        }
        return margin;
    }

    static float measurePara(TextPaint paint, CharSequence text, int start, int end) {
        MeasuredText mt = MeasuredText.obtain();
        TextLine tl = TextLine.obtain();
        try {
            Directions directions;
            int dir;
            Spanned spanned;
            float metrics;
            mt.setPara(text, start, end, TextDirectionHeuristics.LTR);
            if (mt.mEasy) {
                directions = DIRS_ALL_LEFT_TO_RIGHT;
                dir = DIR_REQUEST_LTR;
            } else {
                directions = AndroidBidi.directions(mt.mDir, mt.mLevels, MIN_EMOJI, mt.mChars, MIN_EMOJI, mt.mLen);
                dir = mt.mDir;
            }
            char[] chars = mt.mChars;
            int len = mt.mLen;
            boolean hasTabs = false;
            TabStops tabStops = null;
            int margin = MIN_EMOJI;
            if (text instanceof Spanned) {
                spanned = (Spanned) text;
                LeadingMarginSpan[] arr$ = (LeadingMarginSpan[]) getParagraphSpans(r0, start, end, LeadingMarginSpan.class);
                int len$ = arr$.length;
                for (int i$ = MIN_EMOJI; i$ < len$; i$ += DIR_REQUEST_LTR) {
                    margin += arr$[i$].getLeadingMargin(true);
                }
            }
            for (int i = MIN_EMOJI; i < len; i += DIR_REQUEST_LTR) {
                if (chars[i] == '\t') {
                    hasTabs = true;
                    if (text instanceof Spanned) {
                        spanned = (Spanned) text;
                        int spanEnd = spanned.nextSpanTransition(start, end, TabStopSpan.class);
                        TabStopSpan[] spans = (TabStopSpan[]) getParagraphSpans(spanned, start, spanEnd, TabStopSpan.class);
                        if (spans.length > 0) {
                            tabStops = new TabStops(TAB_INCREMENT, spans);
                        }
                    }
                    tl.set(paint, text, start, end, dir, directions, hasTabs, tabStops);
                    metrics = ((float) margin) + tl.metrics(null);
                    return metrics;
                }
            }
            tl.set(paint, text, start, end, dir, directions, hasTabs, tabStops);
            metrics = ((float) margin) + tl.metrics(null);
            return metrics;
        } finally {
            TextLine.recycle(tl);
            MeasuredText.recycle(mt);
        }
    }

    static float nextTab(CharSequence text, int start, int end, float h, Object[] tabs) {
        float nh = AutoScrollHelper.NO_MAX;
        boolean alltabs = false;
        if (text instanceof Spanned) {
            if (tabs == null) {
                tabs = getParagraphSpans((Spanned) text, start, end, TabStopSpan.class);
                alltabs = true;
            }
            int i = MIN_EMOJI;
            while (i < tabs.length) {
                if (alltabs || (tabs[i] instanceof TabStopSpan)) {
                    int where = ((TabStopSpan) tabs[i]).getTabStop();
                    if (((float) where) < nh && ((float) where) > h) {
                        nh = (float) where;
                    }
                }
                i += DIR_REQUEST_LTR;
            }
            if (nh != AutoScrollHelper.NO_MAX) {
                return nh;
            }
        }
        return (float) (((int) ((h + 20.0f) / 20.0f)) * TAB_INCREMENT);
    }

    protected final boolean isSpanned() {
        return this.mSpannedText;
    }

    static <T> T[] getParagraphSpans(Spanned text, int start, int end, Class<T> type) {
        if (start != end || start <= 0) {
            return text.getSpans(start, end, type);
        }
        return ArrayUtils.emptyArray(type);
    }

    private char getEllipsisChar(TruncateAt method) {
        return method == TruncateAt.END_SMALL ? TextUtils.ELLIPSIS_TWO_DOTS[MIN_EMOJI] : TextUtils.ELLIPSIS_NORMAL[MIN_EMOJI];
    }

    private void ellipsize(int start, int end, int line, char[] dest, int destoff, TruncateAt method) {
        int ellipsisCount = getEllipsisCount(line);
        if (ellipsisCount != 0) {
            int ellipsisStart = getEllipsisStart(line);
            int linestart = getLineStart(line);
            for (int i = ellipsisStart; i < ellipsisStart + ellipsisCount; i += DIR_REQUEST_LTR) {
                char c;
                if (i == ellipsisStart) {
                    c = getEllipsisChar(method);
                } else {
                    c = '\ufeff';
                }
                int a = i + linestart;
                if (a >= start && a < end) {
                    dest[(destoff + a) - start] = c;
                }
            }
        }
    }
}
