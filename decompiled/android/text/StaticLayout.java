package android.text;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.media.RemoteControlClient;
import android.text.Layout.Alignment;
import android.text.Layout.Directions;
import android.text.TextUtils.TruncateAt;
import android.text.style.LeadingMarginSpan;
import android.text.style.LeadingMarginSpan.LeadingMarginSpan2;
import android.text.style.LineHeightSpan;
import android.text.style.LineHeightSpan.WithDensity;
import android.text.style.MetricAffectingSpan;
import android.text.style.TabStopSpan;
import android.util.Log;
import com.android.internal.telephony.RILConstants;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;

public class StaticLayout extends Layout {
    private static final int CHAR_FIRST_HIGH_SURROGATE = 55296;
    private static final int CHAR_LAST_LOW_SURROGATE = 57343;
    private static final char CHAR_NEW_LINE = '\n';
    private static final char CHAR_SPACE = ' ';
    private static final char CHAR_TAB = '\t';
    private static final char CHAR_ZWSP = '\u200b';
    private static final int COLUMNS_ELLIPSIZE = 5;
    private static final int COLUMNS_NORMAL = 3;
    private static final int DESCENT = 2;
    private static final int DIR = 0;
    private static final int DIR_SHIFT = 30;
    private static final int ELLIPSIS_COUNT = 4;
    private static final int ELLIPSIS_START = 3;
    private static final double EXTRA_ROUNDING = 0.5d;
    private static final int START = 0;
    private static final int START_MASK = 536870911;
    private static final int TAB = 0;
    private static final int TAB_INCREMENT = 20;
    private static final int TAB_MASK = 536870912;
    static final String TAG = "StaticLayout";
    private static final int TOP = 1;
    private int mBottomPadding;
    private int mColumns;
    private int mEllipsizedWidth;
    private FontMetricsInt mFontMetricsInt;
    private int mLineCount;
    private Directions[] mLineDirections;
    private int[] mLines;
    private int mMaximumVisibleLineCount;
    private MeasuredText mMeasured;
    private int mTopPadding;

    private static native int[] nLineBreakOpportunities(String str, char[] cArr, int i, int[] iArr);

    public StaticLayout(CharSequence source, TextPaint paint, int width, Alignment align, float spacingmult, float spacingadd, boolean includepad) {
        this(source, TAB, source.length(), paint, width, align, spacingmult, spacingadd, includepad);
    }

    public StaticLayout(CharSequence source, TextPaint paint, int width, Alignment align, TextDirectionHeuristic textDir, float spacingmult, float spacingadd, boolean includepad) {
        this(source, TAB, source.length(), paint, width, align, textDir, spacingmult, spacingadd, includepad);
    }

    public StaticLayout(CharSequence source, int bufstart, int bufend, TextPaint paint, int outerwidth, Alignment align, float spacingmult, float spacingadd, boolean includepad) {
        this(source, bufstart, bufend, paint, outerwidth, align, spacingmult, spacingadd, includepad, null, TAB);
    }

    public StaticLayout(CharSequence source, int bufstart, int bufend, TextPaint paint, int outerwidth, Alignment align, TextDirectionHeuristic textDir, float spacingmult, float spacingadd, boolean includepad) {
        this(source, bufstart, bufend, paint, outerwidth, align, textDir, spacingmult, spacingadd, includepad, null, TAB, RILConstants.MAX_INT);
    }

    public StaticLayout(CharSequence source, int bufstart, int bufend, TextPaint paint, int outerwidth, Alignment align, float spacingmult, float spacingadd, boolean includepad, TruncateAt ellipsize, int ellipsizedWidth) {
        this(source, bufstart, bufend, paint, outerwidth, align, TextDirectionHeuristics.FIRSTSTRONG_LTR, spacingmult, spacingadd, includepad, ellipsize, ellipsizedWidth, RILConstants.MAX_INT);
    }

    public StaticLayout(CharSequence source, int bufstart, int bufend, TextPaint paint, int outerwidth, Alignment align, TextDirectionHeuristic textDir, float spacingmult, float spacingadd, boolean includepad, TruncateAt ellipsize, int ellipsizedWidth, int maxLines) {
        CharSequence spannedEllipsizer = ellipsize == null ? source : source instanceof Spanned ? new SpannedEllipsizer(source) : new Ellipsizer(source);
        super(spannedEllipsizer, paint, outerwidth, align, textDir, spacingmult, spacingadd);
        this.mMaximumVisibleLineCount = RILConstants.MAX_INT;
        this.mFontMetricsInt = new FontMetricsInt();
        if (ellipsize != null) {
            Ellipsizer e = (Ellipsizer) getText();
            e.mLayout = this;
            e.mWidth = ellipsizedWidth;
            e.mMethod = ellipsize;
            this.mEllipsizedWidth = ellipsizedWidth;
            this.mColumns = COLUMNS_ELLIPSIZE;
        } else {
            this.mColumns = ELLIPSIS_START;
            this.mEllipsizedWidth = outerwidth;
        }
        this.mLineDirections = (Directions[]) ArrayUtils.newUnpaddedArray(Directions.class, this.mColumns * DESCENT);
        this.mLines = new int[this.mLineDirections.length];
        this.mMaximumVisibleLineCount = maxLines;
        this.mMeasured = MeasuredText.obtain();
        generate(source, bufstart, bufend, paint, outerwidth, textDir, spacingmult, spacingadd, includepad, includepad, (float) ellipsizedWidth, ellipsize);
        this.mMeasured = MeasuredText.recycle(this.mMeasured);
        this.mFontMetricsInt = null;
    }

    StaticLayout(CharSequence text) {
        super(text, null, TAB, null, 0.0f, 0.0f);
        this.mMaximumVisibleLineCount = RILConstants.MAX_INT;
        this.mFontMetricsInt = new FontMetricsInt();
        this.mColumns = COLUMNS_ELLIPSIZE;
        this.mLineDirections = (Directions[]) ArrayUtils.newUnpaddedArray(Directions.class, this.mColumns * DESCENT);
        this.mLines = new int[this.mLineDirections.length];
        this.mMeasured = MeasuredText.obtain();
    }

    void generate(CharSequence source, int bufStart, int bufEnd, TextPaint paint, int outerWidth, TextDirectionHeuristic textDir, float spacingmult, float spacingadd, boolean includepad, boolean trackpad, float ellipsizedWidth, TruncateAt ellipsize) {
        int[] breakOpp = null;
        String localeLanguageTag = paint.getTextLocale().toLanguageTag();
        this.mLineCount = TAB;
        int v = TAB;
        boolean needMultiply = (spacingmult == RemoteControlClient.PLAYBACK_SPEED_1X && spacingadd == 0.0f) ? false : true;
        FontMetricsInt fm = this.mFontMetricsInt;
        int[] chooseHtv = null;
        MeasuredText measured = this.mMeasured;
        Spanned spanned = null;
        if (source instanceof Spanned) {
            spanned = (Spanned) source;
        }
        int paraStart = bufStart;
        while (paraStart <= bufEnd) {
            int paraEnd = TextUtils.indexOf(source, (char) CHAR_NEW_LINE, paraStart, bufEnd);
            if (paraEnd < 0) {
                paraEnd = bufEnd;
            } else {
                paraEnd += TOP;
            }
            int i = this.mLineCount + TOP;
            int firstWidth = outerWidth;
            int restWidth = outerWidth;
            LineHeightSpan[] chooseHt = null;
            if (spanned != null) {
                int i2;
                LeadingMarginSpan[] sp = (LeadingMarginSpan[]) Layout.getParagraphSpans(spanned, paraStart, paraEnd, LeadingMarginSpan.class);
                for (i2 = TAB; i2 < sp.length; i2 += TOP) {
                    LeadingMarginSpan lms = sp[i2];
                    firstWidth -= sp[i2].getLeadingMargin(true);
                    restWidth -= sp[i2].getLeadingMargin(false);
                    if (lms instanceof LeadingMarginSpan2) {
                        LeadingMarginSpan2 lms2 = (LeadingMarginSpan2) lms;
                        i = Math.max(i, lms2.getLeadingMarginLineCount() + getLineForOffset(spanned.getSpanStart(lms2)));
                    }
                }
                chooseHt = (LineHeightSpan[]) Layout.getParagraphSpans(spanned, paraStart, paraEnd, LineHeightSpan.class);
                if (chooseHt.length != 0) {
                    if (chooseHtv == null || chooseHtv.length < chooseHt.length) {
                        chooseHtv = ArrayUtils.newUnpaddedIntArray(chooseHt.length);
                    }
                    for (i2 = TAB; i2 < chooseHt.length; i2 += TOP) {
                        int o = spanned.getSpanStart(chooseHt[i2]);
                        if (o < paraStart) {
                            chooseHtv[i2] = getLineTop(getLineForOffset(o));
                        } else {
                            chooseHtv[i2] = v;
                        }
                    }
                }
            }
            measured.setPara(source, paraStart, paraEnd, textDir);
            char[] chs = measured.mChars;
            float[] widths = measured.mWidths;
            byte[] chdirs = measured.mLevels;
            int dir = measured.mDir;
            boolean easy = measured.mEasy;
            breakOpp = nLineBreakOpportunities(localeLanguageTag, chs, paraEnd - paraStart, breakOpp);
            int breakOppIndex = TAB;
            int width = firstWidth;
            float w = 0.0f;
            int here = paraStart;
            int ok = paraStart;
            float okWidth = 0.0f;
            int okAscent = TAB;
            int okDescent = TAB;
            int okTop = TAB;
            int okBottom = TAB;
            int fit = paraStart;
            float fitWidth = 0.0f;
            int fitAscent = TAB;
            int fitDescent = TAB;
            int fitTop = TAB;
            int fitBottom = TAB;
            float fitWidthGraphing = 0.0f;
            boolean hasTabOrEmoji = false;
            boolean hasTab = false;
            TabStops tabStops = null;
            int spanStart = paraStart;
            while (spanStart < paraEnd) {
                int spanEnd;
                if (spanned == null) {
                    spanEnd = paraEnd;
                    measured.addStyleRun(paint, spanEnd - spanStart, fm);
                } else {
                    spanEnd = spanned.nextSpanTransition(spanStart, paraEnd, MetricAffectingSpan.class);
                    measured.addStyleRun(paint, (MetricAffectingSpan[]) TextUtils.removeEmptySpans((MetricAffectingSpan[]) spanned.getSpans(spanStart, spanEnd, MetricAffectingSpan.class), spanned, MetricAffectingSpan.class), spanEnd - spanStart, fm);
                }
                int fmTop = fm.top;
                int fmBottom = fm.bottom;
                int fmAscent = fm.ascent;
                int fmDescent = fm.descent;
                int j = spanStart;
                while (j < spanEnd) {
                    char c = chs[j - paraStart];
                    if (c != '\n') {
                        if (c == '\t') {
                            if (!hasTab) {
                                hasTab = true;
                                hasTabOrEmoji = true;
                                if (spanned != null) {
                                    TabStopSpan[] spans = (TabStopSpan[]) Layout.getParagraphSpans(spanned, paraStart, paraEnd, TabStopSpan.class);
                                    if (spans.length > 0) {
                                        TabStops tabStops2 = new TabStops(TAB_INCREMENT, spans);
                                    }
                                }
                            }
                            if (tabStops != null) {
                                w = tabStops.nextTab(w);
                            } else {
                                w = TabStops.nextDefaultStop(w, TAB_INCREMENT);
                            }
                        } else if (c < CHAR_FIRST_HIGH_SURROGATE || c > CHAR_LAST_LOW_SURROGATE || j + TOP >= spanEnd) {
                            w += widths[j - paraStart];
                        } else {
                            int emoji = Character.codePointAt(chs, j - paraStart);
                            if (emoji < MIN_EMOJI || emoji > MAX_EMOJI) {
                                w += widths[j - paraStart];
                            } else {
                                Bitmap bm = EMOJI_FACTORY.getBitmapFromAndroidPua(emoji);
                                if (bm != null) {
                                    Paint whichPaint;
                                    if (spanned == null) {
                                        whichPaint = paint;
                                    } else {
                                        whichPaint = this.mWorkPaint;
                                    }
                                    w += (((float) bm.getWidth()) * (-whichPaint.ascent())) / ((float) bm.getHeight());
                                    hasTabOrEmoji = true;
                                    j += TOP;
                                } else {
                                    w += widths[j - paraStart];
                                }
                            }
                        }
                    }
                    boolean isSpaceOrTab = c == ' ' || c == '\t' || c == '\u200b';
                    if (w <= ((float) width) || isSpaceOrTab) {
                        fitWidth = w;
                        if (!isSpaceOrTab) {
                            fitWidthGraphing = w;
                        }
                        fit = j + TOP;
                        if (fmTop < fitTop) {
                            fitTop = fmTop;
                        }
                        if (fmAscent < fitAscent) {
                            fitAscent = fmAscent;
                        }
                        if (fmDescent > fitDescent) {
                            fitDescent = fmDescent;
                        }
                        if (fmBottom > fitBottom) {
                            fitBottom = fmBottom;
                        }
                        while (breakOpp[breakOppIndex] != -1 && breakOpp[breakOppIndex] < (j - paraStart) + TOP) {
                            breakOppIndex += TOP;
                        }
                        boolean isLineBreak = breakOppIndex < breakOpp.length && breakOpp[breakOppIndex] == (j - paraStart) + TOP;
                        if (isLineBreak) {
                            okWidth = fitWidthGraphing;
                            ok = j + TOP;
                            if (fitTop < okTop) {
                                okTop = fitTop;
                            }
                            if (fitAscent < okAscent) {
                                okAscent = fitAscent;
                            }
                            if (fitDescent > okDescent) {
                                okDescent = fitDescent;
                            }
                            if (fitBottom > okBottom) {
                                okBottom = fitBottom;
                            }
                        }
                    } else {
                        int endPos;
                        int above;
                        int below;
                        int top;
                        int bottom;
                        float currentTextWidth;
                        if (ok != here) {
                            endPos = ok;
                            above = okAscent;
                            below = okDescent;
                            top = okTop;
                            bottom = okBottom;
                            currentTextWidth = okWidth;
                        } else if (fit != here) {
                            endPos = fit;
                            above = fitAscent;
                            below = fitDescent;
                            top = fitTop;
                            bottom = fitBottom;
                            currentTextWidth = fitWidth;
                        } else {
                            endPos = here + TOP;
                            while (endPos < spanEnd && widths[endPos - paraStart] == 0.0f) {
                                endPos += TOP;
                            }
                            above = fmAscent;
                            below = fmDescent;
                            top = fmTop;
                            bottom = fmBottom;
                            currentTextWidth = widths[here - paraStart];
                        }
                        int ellipseEnd = endPos;
                        if (this.mMaximumVisibleLineCount == TOP && ellipsize == TruncateAt.MIDDLE) {
                            ellipseEnd = paraEnd;
                        }
                        v = out(source, here, ellipseEnd, above, below, top, bottom, v, spacingmult, spacingadd, chooseHt, chooseHtv, fm, hasTabOrEmoji, needMultiply, chdirs, dir, easy, bufEnd, includepad, trackpad, chs, widths, paraStart, ellipsize, ellipsizedWidth, currentTextWidth, paint, true);
                        here = endPos;
                        j = here - 1;
                        fit = here;
                        ok = here;
                        w = 0.0f;
                        fitWidthGraphing = 0.0f;
                        fitBottom = TAB;
                        fitTop = TAB;
                        fitDescent = TAB;
                        fitAscent = TAB;
                        okBottom = TAB;
                        okTop = TAB;
                        okDescent = TAB;
                        okAscent = TAB;
                        i--;
                        if (i <= 0) {
                            width = restWidth;
                        }
                        if (here < spanStart) {
                            measured.setPos(here);
                            spanEnd = here;
                            break;
                        } else if (this.mLineCount >= this.mMaximumVisibleLineCount) {
                            return;
                        }
                    }
                    j += TOP;
                }
                spanStart = spanEnd;
            }
            if (paraEnd != here && this.mLineCount < this.mMaximumVisibleLineCount) {
                boolean z;
                if ((((fitTop | fitBottom) | fitDescent) | fitAscent) == 0) {
                    paint.getFontMetricsInt(fm);
                    fitTop = fm.top;
                    fitBottom = fm.bottom;
                    fitAscent = fm.ascent;
                    fitDescent = fm.descent;
                }
                if (paraEnd != bufEnd) {
                    z = true;
                } else {
                    z = false;
                }
                v = out(source, here, paraEnd, fitAscent, fitDescent, fitTop, fitBottom, v, spacingmult, spacingadd, chooseHt, chooseHtv, fm, hasTabOrEmoji, needMultiply, chdirs, dir, easy, bufEnd, includepad, trackpad, chs, widths, paraStart, ellipsize, ellipsizedWidth, w, paint, z);
            }
            paraStart = paraEnd;
            if (paraEnd == bufEnd) {
                break;
            }
            paraStart = paraEnd;
        }
        if (bufEnd != bufStart) {
            if (source.charAt(bufEnd - 1) != CHAR_NEW_LINE) {
                return;
            }
        }
        if (this.mLineCount < this.mMaximumVisibleLineCount) {
            measured.setPara(source, bufStart, bufEnd, textDir);
            paint.getFontMetricsInt(fm);
            v = out(source, bufEnd, bufEnd, fm.ascent, fm.descent, fm.top, fm.bottom, v, spacingmult, spacingadd, null, null, fm, false, needMultiply, measured.mLevels, measured.mDir, measured.mEasy, bufEnd, includepad, trackpad, null, null, bufStart, ellipsize, ellipsizedWidth, 0.0f, paint, false);
        }
    }

    private int out(CharSequence text, int start, int end, int above, int below, int top, int bottom, int v, float spacingmult, float spacingadd, LineHeightSpan[] chooseHt, int[] chooseHtv, FontMetricsInt fm, boolean hasTabOrEmoji, boolean needMultiply, byte[] chdirs, int dir, boolean easy, int bufEnd, boolean includePad, boolean trackPad, char[] chs, float[] widths, int widthStart, TruncateAt ellipsize, float ellipsisWidth, float textWidth, TextPaint paint, boolean moreChars) {
        int extra;
        int i;
        int j = this.mLineCount;
        int off = j * this.mColumns;
        int want = (this.mColumns + off) + TOP;
        int[] lines = this.mLines;
        if (want >= lines.length) {
            Object grow2 = (Directions[]) ArrayUtils.newUnpaddedArray(Directions.class, GrowingArrayUtils.growSize(want));
            System.arraycopy(this.mLineDirections, TAB, grow2, TAB, this.mLineDirections.length);
            this.mLineDirections = grow2;
            int[] grow = new int[grow2.length];
            System.arraycopy(lines, TAB, grow, TAB, lines.length);
            this.mLines = grow;
            lines = grow;
        }
        if (chooseHt != null) {
            fm.ascent = above;
            fm.descent = below;
            fm.top = top;
            fm.bottom = bottom;
            for (int i2 = TAB; i2 < chooseHt.length; i2 += TOP) {
                if (chooseHt[i2] instanceof WithDensity) {
                    ((WithDensity) chooseHt[i2]).chooseHeight(text, start, end, chooseHtv[i2], v, fm, paint);
                } else {
                    chooseHt[i2].chooseHeight(text, start, end, chooseHtv[i2], v, fm);
                }
            }
            above = fm.ascent;
            below = fm.descent;
            top = fm.top;
            bottom = fm.bottom;
        }
        boolean firstLine = j == 0;
        boolean currentLineIsTheLastVisibleOne = j + TOP == this.mMaximumVisibleLineCount;
        boolean lastLine = currentLineIsTheLastVisibleOne || end == bufEnd;
        if (firstLine) {
            if (trackPad) {
                this.mTopPadding = top - above;
            }
            if (includePad) {
                above = top;
            }
        }
        if (lastLine) {
            if (trackPad) {
                this.mBottomPadding = bottom - below;
            }
            if (includePad) {
                below = bottom;
            }
        }
        if (!needMultiply || lastLine) {
            extra = TAB;
        } else {
            double ex = (double) ((((float) (below - above)) * (spacingmult - RemoteControlClient.PLAYBACK_SPEED_1X)) + spacingadd);
            if (ex >= 0.0d) {
                extra = (int) (EXTRA_ROUNDING + ex);
            } else {
                extra = -((int) ((-ex) + EXTRA_ROUNDING));
            }
        }
        lines[off + TAB] = start;
        lines[off + TOP] = v;
        lines[off + DESCENT] = below + extra;
        v += (below - above) + extra;
        lines[(this.mColumns + off) + TAB] = end;
        lines[(this.mColumns + off) + TOP] = v;
        if (hasTabOrEmoji) {
            i = off + TAB;
            lines[i] = lines[i] | TAB_MASK;
        }
        i = off + TAB;
        lines[i] = lines[i] | (dir << DIR_SHIFT);
        Directions linedirs = DIRS_ALL_LEFT_TO_RIGHT;
        if (easy) {
            this.mLineDirections[j] = linedirs;
        } else {
            this.mLineDirections[j] = AndroidBidi.directions(dir, chdirs, start - widthStart, chs, start - widthStart, end - start);
        }
        if (ellipsize != null) {
            boolean forceEllipsis = moreChars && this.mLineCount + TOP == this.mMaximumVisibleLineCount;
            boolean doEllipsis = (((this.mMaximumVisibleLineCount == TOP && moreChars) || (firstLine && !moreChars)) && ellipsize != TruncateAt.MARQUEE) || (!firstLine && ((currentLineIsTheLastVisibleOne || !moreChars) && ellipsize == TruncateAt.END));
            if (doEllipsis) {
                calculateEllipsis(start, end, widths, widthStart, ellipsisWidth, ellipsize, j, textWidth, paint, forceEllipsis);
            }
        }
        this.mLineCount += TOP;
        return v;
    }

    private void calculateEllipsis(int lineStart, int lineEnd, float[] widths, int widthStart, float avail, TruncateAt where, int line, float textWidth, TextPaint paint, boolean forceEllipsis) {
        if (textWidth > avail || forceEllipsis) {
            float ellipsisWidth = paint.measureText(where == TruncateAt.END_SMALL ? TextUtils.ELLIPSIS_TWO_DOTS : TextUtils.ELLIPSIS_NORMAL, TAB, TOP);
            int ellipsisStart = TAB;
            int ellipsisCount = TAB;
            int len = lineEnd - lineStart;
            int i;
            float sum;
            int i2;
            float w;
            if (where == TruncateAt.START) {
                i = this.mMaximumVisibleLineCount;
                if (r0 == TOP) {
                    sum = 0.0f;
                    i2 = len;
                    while (i2 >= 0) {
                        w = widths[((i2 - 1) + lineStart) - widthStart];
                        if ((w + sum) + ellipsisWidth > avail) {
                            break;
                        }
                        sum += w;
                        i2--;
                    }
                    ellipsisStart = TAB;
                    ellipsisCount = i2;
                } else if (Log.isLoggable(TAG, COLUMNS_ELLIPSIZE)) {
                    Log.w(TAG, "Start Ellipsis only supported with one line");
                }
            } else if (where == TruncateAt.END || where == TruncateAt.MARQUEE || where == TruncateAt.END_SMALL) {
                sum = 0.0f;
                i2 = TAB;
                while (i2 < len) {
                    w = widths[(i2 + lineStart) - widthStart];
                    if ((w + sum) + ellipsisWidth > avail) {
                        break;
                    }
                    sum += w;
                    i2 += TOP;
                }
                ellipsisStart = i2;
                ellipsisCount = len - i2;
                if (forceEllipsis && ellipsisCount == 0 && len > 0) {
                    ellipsisStart = len - 1;
                    ellipsisCount = TOP;
                }
            } else {
                i = this.mMaximumVisibleLineCount;
                if (r0 == TOP) {
                    float lsum = 0.0f;
                    float rsum = 0.0f;
                    int right = len;
                    float ravail = (avail - ellipsisWidth) / 2.0f;
                    right = len;
                    while (right > 0) {
                        w = widths[((right - 1) + lineStart) - widthStart];
                        if (w + rsum > ravail) {
                            break;
                        }
                        rsum += w;
                        right--;
                    }
                    float lavail = (avail - ellipsisWidth) - rsum;
                    int left = TAB;
                    while (left < right) {
                        w = widths[(left + lineStart) - widthStart];
                        if (w + lsum > lavail) {
                            break;
                        }
                        lsum += w;
                        left += TOP;
                    }
                    ellipsisStart = left;
                    ellipsisCount = right - left;
                } else if (Log.isLoggable(TAG, COLUMNS_ELLIPSIZE)) {
                    Log.w(TAG, "Middle Ellipsis only supported with one line");
                }
            }
            this.mLines[(this.mColumns * line) + ELLIPSIS_START] = ellipsisStart;
            this.mLines[(this.mColumns * line) + ELLIPSIS_COUNT] = ellipsisCount;
            return;
        }
        this.mLines[(this.mColumns * line) + ELLIPSIS_START] = TAB;
        this.mLines[(this.mColumns * line) + ELLIPSIS_COUNT] = TAB;
    }

    public int getLineForVertical(int vertical) {
        int high = this.mLineCount;
        int low = -1;
        int[] lines = this.mLines;
        while (high - low > TOP) {
            int guess = (high + low) >> TOP;
            if (lines[(this.mColumns * guess) + TOP] > vertical) {
                high = guess;
            } else {
                low = guess;
            }
        }
        if (low < 0) {
            return TAB;
        }
        return low;
    }

    public int getLineCount() {
        return this.mLineCount;
    }

    public int getLineTop(int line) {
        int top = this.mLines[(this.mColumns * line) + TOP];
        if (this.mMaximumVisibleLineCount <= 0 || line < this.mMaximumVisibleLineCount || line == this.mLineCount) {
            return top;
        }
        return top + getBottomPadding();
    }

    public int getLineDescent(int line) {
        int descent = this.mLines[(this.mColumns * line) + DESCENT];
        if (this.mMaximumVisibleLineCount <= 0 || line < this.mMaximumVisibleLineCount - 1 || line == this.mLineCount) {
            return descent;
        }
        return descent + getBottomPadding();
    }

    public int getLineStart(int line) {
        return this.mLines[(this.mColumns * line) + TAB] & START_MASK;
    }

    public int getParagraphDirection(int line) {
        return this.mLines[(this.mColumns * line) + TAB] >> DIR_SHIFT;
    }

    public boolean getLineContainsTab(int line) {
        return (this.mLines[(this.mColumns * line) + TAB] & TAB_MASK) != 0;
    }

    public final Directions getLineDirections(int line) {
        return this.mLineDirections[line];
    }

    public int getTopPadding() {
        return this.mTopPadding;
    }

    public int getBottomPadding() {
        return this.mBottomPadding;
    }

    public int getEllipsisCount(int line) {
        if (this.mColumns < COLUMNS_ELLIPSIZE) {
            return TAB;
        }
        return this.mLines[(this.mColumns * line) + ELLIPSIS_COUNT];
    }

    public int getEllipsisStart(int line) {
        if (this.mColumns < COLUMNS_ELLIPSIZE) {
            return TAB;
        }
        return this.mLines[(this.mColumns * line) + ELLIPSIS_START];
    }

    public int getEllipsizedWidth() {
        return this.mEllipsizedWidth;
    }

    void prepare() {
        this.mMeasured = MeasuredText.obtain();
    }

    void finish() {
        this.mMeasured = MeasuredText.recycle(this.mMeasured);
    }
}
