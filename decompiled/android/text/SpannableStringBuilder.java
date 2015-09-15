package android.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import java.lang.reflect.Array;
import libcore.util.EmptyArray;

public class SpannableStringBuilder implements CharSequence, GetChars, Spannable, Editable, Appendable, GraphicsOperations {
    private static final int END_MASK = 15;
    private static final int MARK = 1;
    private static final InputFilter[] NO_FILTERS;
    private static final int PARAGRAPH = 3;
    private static final int POINT = 2;
    private static final int SPAN_END_AT_END = 32768;
    private static final int SPAN_END_AT_START = 16384;
    private static final int SPAN_START_AT_END = 8192;
    private static final int SPAN_START_AT_START = 4096;
    private static final int SPAN_START_END_MASK = 61440;
    private static final int START_MASK = 240;
    private static final int START_SHIFT = 4;
    private static final String TAG = "SpannableStringBuilder";
    private InputFilter[] mFilters;
    private int mGapLength;
    private int mGapStart;
    private int mSpanCount;
    private int mSpanCountBeforeAdd;
    private int[] mSpanEnds;
    private int[] mSpanFlags;
    private int[] mSpanStarts;
    private Object[] mSpans;
    private char[] mText;

    public SpannableStringBuilder() {
        this("");
    }

    public SpannableStringBuilder(CharSequence text) {
        this(text, 0, text.length());
    }

    public SpannableStringBuilder(CharSequence text, int start, int end) {
        this.mFilters = NO_FILTERS;
        int srclen = end - start;
        if (srclen < 0) {
            throw new StringIndexOutOfBoundsException();
        }
        this.mText = ArrayUtils.newUnpaddedCharArray(GrowingArrayUtils.growSize(srclen));
        this.mGapStart = srclen;
        this.mGapLength = this.mText.length - srclen;
        TextUtils.getChars(text, start, end, this.mText, 0);
        this.mSpanCount = 0;
        this.mSpans = EmptyArray.OBJECT;
        this.mSpanStarts = EmptyArray.INT;
        this.mSpanEnds = EmptyArray.INT;
        this.mSpanFlags = EmptyArray.INT;
        if (text instanceof Spanned) {
            Spanned sp = (Spanned) text;
            Object[] spans = sp.getSpans(start, end, Object.class);
            for (int i = 0; i < spans.length; i += MARK) {
                if (!(spans[i] instanceof NoCopySpan)) {
                    int st = sp.getSpanStart(spans[i]) - start;
                    int en = sp.getSpanEnd(spans[i]) - start;
                    int fl = sp.getSpanFlags(spans[i]);
                    if (st < 0) {
                        st = 0;
                    }
                    if (st > end - start) {
                        st = end - start;
                    }
                    if (en < 0) {
                        en = 0;
                    }
                    if (en > end - start) {
                        en = end - start;
                    }
                    setSpan(false, spans[i], st, en, fl);
                }
            }
        }
    }

    public static SpannableStringBuilder valueOf(CharSequence source) {
        if (source instanceof SpannableStringBuilder) {
            return (SpannableStringBuilder) source;
        }
        return new SpannableStringBuilder(source);
    }

    public char charAt(int where) {
        int len = length();
        if (where < 0) {
            throw new IndexOutOfBoundsException("charAt: " + where + " < 0");
        } else if (where >= len) {
            throw new IndexOutOfBoundsException("charAt: " + where + " >= length " + len);
        } else if (where >= this.mGapStart) {
            return this.mText[this.mGapLength + where];
        } else {
            return this.mText[where];
        }
    }

    public int length() {
        return this.mText.length - this.mGapLength;
    }

    private void resizeFor(int size) {
        int oldLength = this.mText.length;
        if (size + MARK > oldLength) {
            char[] newText = ArrayUtils.newUnpaddedCharArray(GrowingArrayUtils.growSize(size));
            System.arraycopy(this.mText, 0, newText, 0, this.mGapStart);
            int newLength = newText.length;
            int delta = newLength - oldLength;
            int after = oldLength - (this.mGapStart + this.mGapLength);
            System.arraycopy(this.mText, oldLength - after, newText, newLength - after, after);
            this.mText = newText;
            this.mGapLength += delta;
            if (this.mGapLength < MARK) {
                new Exception("mGapLength < 1").printStackTrace();
            }
            for (int i = 0; i < this.mSpanCount; i += MARK) {
                int[] iArr;
                if (this.mSpanStarts[i] > this.mGapStart) {
                    iArr = this.mSpanStarts;
                    iArr[i] = iArr[i] + delta;
                }
                if (this.mSpanEnds[i] > this.mGapStart) {
                    iArr = this.mSpanEnds;
                    iArr[i] = iArr[i] + delta;
                }
            }
        }
    }

    private void moveGapTo(int where) {
        if (where != this.mGapStart) {
            boolean atEnd = where == length();
            int overlap;
            if (where < this.mGapStart) {
                overlap = this.mGapStart - where;
                System.arraycopy(this.mText, where, this.mText, (this.mGapStart + this.mGapLength) - overlap, overlap);
            } else {
                overlap = where - this.mGapStart;
                System.arraycopy(this.mText, (this.mGapLength + where) - overlap, this.mText, this.mGapStart, overlap);
            }
            for (int i = 0; i < this.mSpanCount; i += MARK) {
                int flag;
                int start = this.mSpanStarts[i];
                int end = this.mSpanEnds[i];
                if (start > this.mGapStart) {
                    start -= this.mGapLength;
                }
                if (start > where) {
                    start += this.mGapLength;
                } else if (start == where) {
                    flag = (this.mSpanFlags[i] & START_MASK) >> START_SHIFT;
                    if (flag == POINT || (atEnd && flag == PARAGRAPH)) {
                        start += this.mGapLength;
                    }
                }
                if (end > this.mGapStart) {
                    end -= this.mGapLength;
                }
                if (end > where) {
                    end += this.mGapLength;
                } else if (end == where) {
                    flag = this.mSpanFlags[i] & END_MASK;
                    if (flag == POINT || (atEnd && flag == PARAGRAPH)) {
                        end += this.mGapLength;
                    }
                }
                this.mSpanStarts[i] = start;
                this.mSpanEnds[i] = end;
            }
            this.mGapStart = where;
        }
    }

    public SpannableStringBuilder insert(int where, CharSequence tb, int start, int end) {
        return replace(where, where, tb, start, end);
    }

    public SpannableStringBuilder insert(int where, CharSequence tb) {
        return replace(where, where, tb, 0, tb.length());
    }

    public SpannableStringBuilder delete(int start, int end) {
        SpannableStringBuilder ret = replace(start, end, (CharSequence) "", 0, 0);
        if (this.mGapLength > length() * POINT) {
            resizeFor(length());
        }
        return ret;
    }

    public void clear() {
        replace(0, length(), (CharSequence) "", 0, 0);
    }

    public void clearSpans() {
        for (int i = this.mSpanCount - 1; i >= 0; i--) {
            Object what = this.mSpans[i];
            int ostart = this.mSpanStarts[i];
            int oend = this.mSpanEnds[i];
            if (ostart > this.mGapStart) {
                ostart -= this.mGapLength;
            }
            if (oend > this.mGapStart) {
                oend -= this.mGapLength;
            }
            this.mSpanCount = i;
            this.mSpans[i] = null;
            sendSpanRemoved(what, ostart, oend);
        }
    }

    public SpannableStringBuilder append(CharSequence text) {
        int length = length();
        return replace(length, length, text, 0, text.length());
    }

    public SpannableStringBuilder append(CharSequence text, Object what, int flags) {
        int start = length();
        append(text);
        setSpan(what, start, length(), flags);
        return this;
    }

    public SpannableStringBuilder append(CharSequence text, int start, int end) {
        int length = length();
        return replace(length, length, text, start, end);
    }

    public SpannableStringBuilder append(char text) {
        return append(String.valueOf(text));
    }

    private void change(int start, int end, CharSequence cs, int csStart, int csEnd) {
        int i;
        int replacedLength = end - start;
        int replacementLength = csEnd - csStart;
        int nbNewChars = replacementLength - replacedLength;
        for (i = this.mSpanCount - 1; i >= 0; i--) {
            int spanStart = this.mSpanStarts[i];
            if (spanStart > this.mGapStart) {
                spanStart -= this.mGapLength;
            }
            int spanEnd = this.mSpanEnds[i];
            if (spanEnd > this.mGapStart) {
                spanEnd -= this.mGapLength;
            }
            if ((this.mSpanFlags[i] & 51) == 51) {
                int ost = spanStart;
                int oen = spanEnd;
                int clen = length();
                if (spanStart > start && spanStart <= end) {
                    spanStart = end;
                    while (spanStart < clen && (spanStart <= end || charAt(spanStart - 1) != '\n')) {
                        spanStart += MARK;
                    }
                }
                if (spanEnd > start && spanEnd <= end) {
                    spanEnd = end;
                    while (spanEnd < clen && (spanEnd <= end || charAt(spanEnd - 1) != '\n')) {
                        spanEnd += MARK;
                    }
                }
                if (!(spanStart == ost && spanEnd == oen)) {
                    setSpan(false, this.mSpans[i], spanStart, spanEnd, this.mSpanFlags[i]);
                }
            }
            int flags = 0;
            if (spanStart == start) {
                flags = 0 | SPAN_START_AT_START;
            } else if (spanStart == end + nbNewChars) {
                flags = 0 | SPAN_START_AT_END;
            }
            if (spanEnd == start) {
                flags |= SPAN_END_AT_START;
            } else if (spanEnd == end + nbNewChars) {
                flags |= SPAN_END_AT_END;
            }
            int[] iArr = this.mSpanFlags;
            iArr[i] = iArr[i] | flags;
        }
        moveGapTo(end);
        if (nbNewChars >= this.mGapLength) {
            resizeFor((this.mText.length + nbNewChars) - this.mGapLength);
        }
        boolean textIsRemoved = replacementLength == 0;
        if (replacedLength > 0) {
            i = 0;
            while (i < this.mSpanCount) {
                if ((this.mSpanFlags[i] & 33) != 33 || this.mSpanStarts[i] < start || this.mSpanStarts[i] >= this.mGapStart + this.mGapLength || this.mSpanEnds[i] < start || this.mSpanEnds[i] >= this.mGapStart + this.mGapLength || (!textIsRemoved && this.mSpanStarts[i] <= start && this.mSpanEnds[i] >= this.mGapStart)) {
                    i += MARK;
                } else {
                    removeSpan(i);
                }
            }
        }
        this.mGapStart += nbNewChars;
        this.mGapLength -= nbNewChars;
        if (this.mGapLength < MARK) {
            new Exception("mGapLength < 1").printStackTrace();
        }
        TextUtils.getChars(cs, csStart, csEnd, this.mText, start);
        if (replacedLength > 0) {
            boolean atEnd = this.mGapStart + this.mGapLength == this.mText.length;
            for (i = 0; i < this.mSpanCount; i += MARK) {
                this.mSpanStarts[i] = updatedIntervalBound(this.mSpanStarts[i], start, nbNewChars, (this.mSpanFlags[i] & START_MASK) >> START_SHIFT, atEnd, textIsRemoved);
                this.mSpanEnds[i] = updatedIntervalBound(this.mSpanEnds[i], start, nbNewChars, this.mSpanFlags[i] & END_MASK, atEnd, textIsRemoved);
            }
        }
        this.mSpanCountBeforeAdd = this.mSpanCount;
        if (cs instanceof Spanned) {
            Spanned sp = (Spanned) cs;
            Object[] spans = sp.getSpans(csStart, csEnd, Object.class);
            for (i = 0; i < spans.length; i += MARK) {
                int st = sp.getSpanStart(spans[i]);
                int en = sp.getSpanEnd(spans[i]);
                if (st < csStart) {
                    st = csStart;
                }
                if (en > csEnd) {
                    en = csEnd;
                }
                if (getSpanStart(spans[i]) < 0) {
                    setSpan(false, spans[i], (st - csStart) + start, (en - csStart) + start, sp.getSpanFlags(spans[i]));
                }
            }
        }
    }

    private int updatedIntervalBound(int offset, int start, int nbNewChars, int flag, boolean atEnd, boolean textIsRemoved) {
        if (offset >= start && offset < this.mGapStart + this.mGapLength) {
            if (flag == POINT) {
                if (textIsRemoved || offset > start) {
                    return this.mGapStart + this.mGapLength;
                }
            } else if (flag == PARAGRAPH) {
                if (atEnd) {
                    return this.mGapStart + this.mGapLength;
                }
            } else if (textIsRemoved || offset < this.mGapStart - nbNewChars) {
                return start;
            } else {
                return this.mGapStart;
            }
        }
        return offset;
    }

    private void removeSpan(int i) {
        Object object = this.mSpans[i];
        int start = this.mSpanStarts[i];
        int end = this.mSpanEnds[i];
        if (start > this.mGapStart) {
            start -= this.mGapLength;
        }
        if (end > this.mGapStart) {
            end -= this.mGapLength;
        }
        int count = this.mSpanCount - (i + MARK);
        System.arraycopy(this.mSpans, i + MARK, this.mSpans, i, count);
        System.arraycopy(this.mSpanStarts, i + MARK, this.mSpanStarts, i, count);
        System.arraycopy(this.mSpanEnds, i + MARK, this.mSpanEnds, i, count);
        System.arraycopy(this.mSpanFlags, i + MARK, this.mSpanFlags, i, count);
        this.mSpanCount--;
        this.mSpans[this.mSpanCount] = null;
        sendSpanRemoved(object, start, end);
    }

    public SpannableStringBuilder replace(int start, int end, CharSequence tb) {
        return replace(start, end, tb, 0, tb.length());
    }

    public SpannableStringBuilder replace(int start, int end, CharSequence tb, int tbstart, int tbend) {
        checkRange("replace", start, end);
        int filtercount = this.mFilters.length;
        for (int i = 0; i < filtercount; i += MARK) {
            CharSequence repl = this.mFilters[i].filter(tb, tbstart, tbend, this, start, end);
            if (repl != null) {
                tb = repl;
                tbstart = 0;
                tbend = repl.length();
            }
        }
        int origLen = end - start;
        int newLen = tbend - tbstart;
        if (!(origLen == 0 && newLen == 0 && !hasNonExclusiveExclusiveSpanAt(tb, tbstart))) {
            TextWatcher[] textWatchers = (TextWatcher[]) getSpans(start, start + origLen, TextWatcher.class);
            sendBeforeTextChanged(textWatchers, start, origLen, newLen);
            boolean adjustSelection = (origLen == 0 || newLen == 0) ? false : true;
            int selectionStart = 0;
            int selectionEnd = 0;
            if (adjustSelection) {
                selectionStart = Selection.getSelectionStart(this);
                selectionEnd = Selection.getSelectionEnd(this);
            }
            change(start, end, tb, tbstart, tbend);
            if (adjustSelection) {
                if (selectionStart > start && selectionStart < end) {
                    selectionStart = start + (((selectionStart - start) * newLen) / origLen);
                    setSpan(false, Selection.SELECTION_START, selectionStart, selectionStart, 34);
                }
                if (selectionEnd > start && selectionEnd < end) {
                    selectionEnd = start + (((selectionEnd - start) * newLen) / origLen);
                    setSpan(false, Selection.SELECTION_END, selectionEnd, selectionEnd, 34);
                }
            }
            sendTextChanged(textWatchers, start, origLen, newLen);
            sendAfterTextChanged(textWatchers);
            sendToSpanWatchers(start, end, newLen - origLen);
        }
        return this;
    }

    private static boolean hasNonExclusiveExclusiveSpanAt(CharSequence text, int offset) {
        if (text instanceof Spanned) {
            Spanned spanned = (Spanned) text;
            Object[] spans = spanned.getSpans(offset, offset, Object.class);
            int length = spans.length;
            for (int i = 0; i < length; i += MARK) {
                if (spanned.getSpanFlags(spans[i]) != 33) {
                    return true;
                }
            }
        }
        return false;
    }

    private void sendToSpanWatchers(int replaceStart, int replaceEnd, int nbNewChars) {
        int i;
        for (i = 0; i < this.mSpanCountBeforeAdd; i += MARK) {
            int spanStart = this.mSpanStarts[i];
            int spanEnd = this.mSpanEnds[i];
            if (spanStart > this.mGapStart) {
                spanStart -= this.mGapLength;
            }
            if (spanEnd > this.mGapStart) {
                spanEnd -= this.mGapLength;
            }
            int spanFlags = this.mSpanFlags[i];
            int newReplaceEnd = replaceEnd + nbNewChars;
            boolean spanChanged = false;
            int previousSpanStart = spanStart;
            if (spanStart > newReplaceEnd) {
                if (nbNewChars != 0) {
                    previousSpanStart -= nbNewChars;
                    spanChanged = true;
                }
            } else if (spanStart >= replaceStart && !((spanStart == replaceStart && (spanFlags & SPAN_START_AT_START) == SPAN_START_AT_START) || (spanStart == newReplaceEnd && (spanFlags & SPAN_START_AT_END) == SPAN_START_AT_END))) {
                spanChanged = true;
            }
            int previousSpanEnd = spanEnd;
            if (spanEnd > newReplaceEnd) {
                if (nbNewChars != 0) {
                    previousSpanEnd -= nbNewChars;
                    spanChanged = true;
                }
            } else if (spanEnd >= replaceStart && !((spanEnd == replaceStart && (spanFlags & SPAN_END_AT_START) == SPAN_END_AT_START) || (spanEnd == newReplaceEnd && (spanFlags & SPAN_END_AT_END) == SPAN_END_AT_END))) {
                spanChanged = true;
            }
            if (spanChanged) {
                sendSpanChanged(this.mSpans[i], previousSpanStart, previousSpanEnd, spanStart, spanEnd);
            }
            int[] iArr = this.mSpanFlags;
            iArr[i] = iArr[i] & -61441;
        }
        for (i = this.mSpanCountBeforeAdd; i < this.mSpanCount; i += MARK) {
            spanStart = this.mSpanStarts[i];
            spanEnd = this.mSpanEnds[i];
            if (spanStart > this.mGapStart) {
                spanStart -= this.mGapLength;
            }
            if (spanEnd > this.mGapStart) {
                spanEnd -= this.mGapLength;
            }
            sendSpanAdded(this.mSpans[i], spanStart, spanEnd);
        }
    }

    public void setSpan(Object what, int start, int end, int flags) {
        setSpan(true, what, start, end, flags);
    }

    private void setSpan(boolean send, Object what, int start, int end, int flags) {
        checkRange("setSpan", start, end);
        int flagsStart = (flags & START_MASK) >> START_SHIFT;
        if (flagsStart != PARAGRAPH || start == 0 || start == length() || charAt(start - 1) == '\n') {
            int flagsEnd = flags & END_MASK;
            if (flagsEnd == PARAGRAPH && end != 0 && end != length() && charAt(end - 1) != '\n') {
                throw new RuntimeException("PARAGRAPH span must end at paragraph boundary");
            } else if (flagsStart != POINT || flagsEnd != MARK || start != end) {
                int nstart = start;
                int nend = end;
                if (start > this.mGapStart) {
                    start += this.mGapLength;
                } else if (start == this.mGapStart && (flagsStart == POINT || (flagsStart == PARAGRAPH && start == length()))) {
                    start += this.mGapLength;
                }
                if (end > this.mGapStart) {
                    end += this.mGapLength;
                } else if (end == this.mGapStart && (flagsEnd == POINT || (flagsEnd == PARAGRAPH && end == length()))) {
                    end += this.mGapLength;
                }
                int count = this.mSpanCount;
                Object[] spans = this.mSpans;
                for (int i = 0; i < count; i += MARK) {
                    if (spans[i] == what) {
                        int ostart = this.mSpanStarts[i];
                        int oend = this.mSpanEnds[i];
                        if (ostart > this.mGapStart) {
                            ostart -= this.mGapLength;
                        }
                        if (oend > this.mGapStart) {
                            oend -= this.mGapLength;
                        }
                        this.mSpanStarts[i] = start;
                        this.mSpanEnds[i] = end;
                        this.mSpanFlags[i] = flags;
                        if (send) {
                            sendSpanChanged(what, ostart, oend, nstart, nend);
                            return;
                        }
                        return;
                    }
                }
                this.mSpans = GrowingArrayUtils.append(this.mSpans, this.mSpanCount, what);
                this.mSpanStarts = GrowingArrayUtils.append(this.mSpanStarts, this.mSpanCount, start);
                this.mSpanEnds = GrowingArrayUtils.append(this.mSpanEnds, this.mSpanCount, end);
                this.mSpanFlags = GrowingArrayUtils.append(this.mSpanFlags, this.mSpanCount, flags);
                this.mSpanCount += MARK;
                if (send) {
                    sendSpanAdded(what, nstart, nend);
                    return;
                }
                return;
            } else if (send) {
                Log.e(TAG, "SPAN_EXCLUSIVE_EXCLUSIVE spans cannot have a zero length");
                return;
            } else {
                return;
            }
        }
        throw new RuntimeException("PARAGRAPH span must start at paragraph boundary");
    }

    public void removeSpan(Object what) {
        for (int i = this.mSpanCount - 1; i >= 0; i--) {
            if (this.mSpans[i] == what) {
                removeSpan(i);
                return;
            }
        }
    }

    public int getSpanStart(Object what) {
        int count = this.mSpanCount;
        Object[] spans = this.mSpans;
        for (int i = count - 1; i >= 0; i--) {
            if (spans[i] == what) {
                int where = this.mSpanStarts[i];
                if (where > this.mGapStart) {
                    return where - this.mGapLength;
                }
                return where;
            }
        }
        return -1;
    }

    public int getSpanEnd(Object what) {
        int count = this.mSpanCount;
        Object[] spans = this.mSpans;
        for (int i = count - 1; i >= 0; i--) {
            if (spans[i] == what) {
                int where = this.mSpanEnds[i];
                if (where > this.mGapStart) {
                    return where - this.mGapLength;
                }
                return where;
            }
        }
        return -1;
    }

    public int getSpanFlags(Object what) {
        int count = this.mSpanCount;
        Object[] spans = this.mSpans;
        for (int i = count - 1; i >= 0; i--) {
            if (spans[i] == what) {
                return this.mSpanFlags[i];
            }
        }
        return 0;
    }

    public <T> T[] getSpans(int queryStart, int queryEnd, Class<T> kind) {
        if (kind == null) {
            return ArrayUtils.emptyArray(kind);
        }
        int spanCount = this.mSpanCount;
        Object[] spans = this.mSpans;
        int[] starts = this.mSpanStarts;
        int[] ends = this.mSpanEnds;
        int[] flags = this.mSpanFlags;
        int gapstart = this.mGapStart;
        int gaplen = this.mGapLength;
        T[] ret = null;
        T ret1 = null;
        int i = 0;
        int count = 0;
        while (i < spanCount) {
            int count2;
            int spanStart = starts[i];
            if (spanStart > gapstart) {
                spanStart -= gaplen;
            }
            if (spanStart > queryEnd) {
                count2 = count;
            } else {
                int spanEnd = ends[i];
                if (spanEnd > gapstart) {
                    spanEnd -= gaplen;
                }
                if (spanEnd < queryStart) {
                    count2 = count;
                } else {
                    if (!(spanStart == spanEnd || queryStart == queryEnd)) {
                        if (spanStart == queryEnd) {
                            count2 = count;
                        } else if (spanEnd == queryStart) {
                            count2 = count;
                        }
                    }
                    if (!kind.isInstance(spans[i])) {
                        count2 = count;
                    } else if (count == 0) {
                        ret1 = spans[i];
                        count2 = count + MARK;
                    } else {
                        if (count == MARK) {
                            ret = (Object[]) ((Object[]) Array.newInstance(kind, (spanCount - i) + MARK));
                            ret[0] = ret1;
                        }
                        int prio = flags[i] & Spanned.SPAN_PRIORITY;
                        if (prio != 0) {
                            int j = 0;
                            while (j < count) {
                                if (prio > (getSpanFlags(ret[j]) & Spanned.SPAN_PRIORITY)) {
                                    break;
                                }
                                j += MARK;
                            }
                            System.arraycopy(ret, j, ret, j + MARK, count - j);
                            ret[j] = spans[i];
                            count2 = count + MARK;
                        } else {
                            count2 = count + MARK;
                            ret[count] = spans[i];
                        }
                    }
                }
            }
            i += MARK;
            count = count2;
        }
        if (count == 0) {
            return ArrayUtils.emptyArray(kind);
        }
        if (count == MARK) {
            Object[] ret2 = (Object[]) ((Object[]) Array.newInstance(kind, MARK));
            ret2[0] = ret1;
            return ret2;
        }
        int length = ret.length;
        if (count == r0) {
            return ret;
        }
        T[] nret = (Object[]) ((Object[]) Array.newInstance(kind, count));
        System.arraycopy(ret, 0, nret, 0, count);
        return nret;
    }

    public int nextSpanTransition(int start, int limit, Class kind) {
        int count = this.mSpanCount;
        Object[] spans = this.mSpans;
        int[] starts = this.mSpanStarts;
        int[] ends = this.mSpanEnds;
        int gapstart = this.mGapStart;
        int gaplen = this.mGapLength;
        if (kind == null) {
            kind = Object.class;
        }
        int i = 0;
        while (i < count) {
            int st = starts[i];
            int en = ends[i];
            if (st > gapstart) {
                st -= gaplen;
            }
            if (en > gapstart) {
                en -= gaplen;
            }
            if (st > start && st < limit && kind.isInstance(spans[i])) {
                limit = st;
            }
            if (en > start && en < limit && kind.isInstance(spans[i])) {
                limit = en;
            }
            i += MARK;
        }
        return limit;
    }

    public CharSequence subSequence(int start, int end) {
        return new SpannableStringBuilder(this, start, end);
    }

    public void getChars(int start, int end, char[] dest, int destoff) {
        checkRange("getChars", start, end);
        if (end <= this.mGapStart) {
            System.arraycopy(this.mText, start, dest, destoff, end - start);
        } else if (start >= this.mGapStart) {
            System.arraycopy(this.mText, this.mGapLength + start, dest, destoff, end - start);
        } else {
            System.arraycopy(this.mText, start, dest, destoff, this.mGapStart - start);
            System.arraycopy(this.mText, this.mGapStart + this.mGapLength, dest, (this.mGapStart - start) + destoff, end - this.mGapStart);
        }
    }

    public String toString() {
        int len = length();
        char[] buf = new char[len];
        getChars(0, len, buf, 0);
        return new String(buf);
    }

    public String substring(int start, int end) {
        char[] buf = new char[(end - start)];
        getChars(start, end, buf, 0);
        return new String(buf);
    }

    private void sendBeforeTextChanged(TextWatcher[] watchers, int start, int before, int after) {
        int n = watchers.length;
        for (int i = 0; i < n; i += MARK) {
            watchers[i].beforeTextChanged(this, start, before, after);
        }
    }

    private void sendTextChanged(TextWatcher[] watchers, int start, int before, int after) {
        int n = watchers.length;
        for (int i = 0; i < n; i += MARK) {
            watchers[i].onTextChanged(this, start, before, after);
        }
    }

    private void sendAfterTextChanged(TextWatcher[] watchers) {
        int n = watchers.length;
        for (int i = 0; i < n; i += MARK) {
            watchers[i].afterTextChanged(this);
        }
    }

    private void sendSpanAdded(Object what, int start, int end) {
        SpanWatcher[] recip = (SpanWatcher[]) getSpans(start, end, SpanWatcher.class);
        int n = recip.length;
        for (int i = 0; i < n; i += MARK) {
            recip[i].onSpanAdded(this, what, start, end);
        }
    }

    private void sendSpanRemoved(Object what, int start, int end) {
        SpanWatcher[] recip = (SpanWatcher[]) getSpans(start, end, SpanWatcher.class);
        int n = recip.length;
        for (int i = 0; i < n; i += MARK) {
            recip[i].onSpanRemoved(this, what, start, end);
        }
    }

    private void sendSpanChanged(Object what, int oldStart, int oldEnd, int start, int end) {
        SpanWatcher[] spanWatchers = (SpanWatcher[]) getSpans(Math.min(oldStart, start), Math.min(Math.max(oldEnd, end), length()), SpanWatcher.class);
        int n = spanWatchers.length;
        for (int i = 0; i < n; i += MARK) {
            spanWatchers[i].onSpanChanged(this, what, oldStart, oldEnd, start, end);
        }
    }

    private static String region(int start, int end) {
        return "(" + start + " ... " + end + ")";
    }

    private void checkRange(String operation, int start, int end) {
        if (end < start) {
            throw new IndexOutOfBoundsException(operation + " " + region(start, end) + " has end before start");
        }
        int len = length();
        if (start > len || end > len) {
            throw new IndexOutOfBoundsException(operation + " " + region(start, end) + " ends beyond length " + len);
        } else if (start < 0 || end < 0) {
            throw new IndexOutOfBoundsException(operation + " " + region(start, end) + " starts before 0");
        }
    }

    public void drawText(Canvas c, int start, int end, float x, float y, Paint p) {
        checkRange("drawText", start, end);
        if (end <= this.mGapStart) {
            c.drawText(this.mText, start, end - start, x, y, p);
        } else if (start >= this.mGapStart) {
            c.drawText(this.mText, start + this.mGapLength, end - start, x, y, p);
        } else {
            char[] buf = TextUtils.obtain(end - start);
            getChars(start, end, buf, 0);
            c.drawText(buf, 0, end - start, x, y, p);
            TextUtils.recycle(buf);
        }
    }

    public void drawTextRun(Canvas c, int start, int end, int contextStart, int contextEnd, float x, float y, boolean isRtl, Paint p) {
        checkRange("drawTextRun", start, end);
        int contextLen = contextEnd - contextStart;
        int len = end - start;
        if (contextEnd <= this.mGapStart) {
            c.drawTextRun(this.mText, start, len, contextStart, contextLen, x, y, isRtl, p);
        } else if (contextStart >= this.mGapStart) {
            c.drawTextRun(this.mText, start + this.mGapLength, len, contextStart + this.mGapLength, contextLen, x, y, isRtl, p);
        } else {
            char[] buf = TextUtils.obtain(contextLen);
            getChars(contextStart, contextEnd, buf, 0);
            c.drawTextRun(buf, start - contextStart, len, 0, contextLen, x, y, isRtl, p);
            TextUtils.recycle(buf);
        }
    }

    public float measureText(int start, int end, Paint p) {
        checkRange("measureText", start, end);
        if (end <= this.mGapStart) {
            return p.measureText(this.mText, start, end - start);
        }
        if (start >= this.mGapStart) {
            return p.measureText(this.mText, this.mGapLength + start, end - start);
        }
        char[] buf = TextUtils.obtain(end - start);
        getChars(start, end, buf, 0);
        float ret = p.measureText(buf, 0, end - start);
        TextUtils.recycle(buf);
        return ret;
    }

    public int getTextWidths(int start, int end, float[] widths, Paint p) {
        checkRange("getTextWidths", start, end);
        if (end <= this.mGapStart) {
            return p.getTextWidths(this.mText, start, end - start, widths);
        }
        if (start >= this.mGapStart) {
            return p.getTextWidths(this.mText, this.mGapLength + start, end - start, widths);
        }
        char[] buf = TextUtils.obtain(end - start);
        getChars(start, end, buf, 0);
        int ret = p.getTextWidths(buf, 0, end - start, widths);
        TextUtils.recycle(buf);
        return ret;
    }

    public float getTextRunAdvances(int start, int end, int contextStart, int contextEnd, boolean isRtl, float[] advances, int advancesPos, Paint p) {
        int contextLen = contextEnd - contextStart;
        int len = end - start;
        if (end <= this.mGapStart) {
            return p.getTextRunAdvances(this.mText, start, len, contextStart, contextLen, isRtl, advances, advancesPos);
        } else if (start >= this.mGapStart) {
            return p.getTextRunAdvances(this.mText, start + this.mGapLength, len, contextStart + this.mGapLength, contextLen, isRtl, advances, advancesPos);
        } else {
            char[] buf = TextUtils.obtain(contextLen);
            getChars(contextStart, contextEnd, buf, 0);
            float ret = p.getTextRunAdvances(buf, start - contextStart, len, 0, contextLen, isRtl, advances, advancesPos);
            TextUtils.recycle(buf);
            return ret;
        }
    }

    @Deprecated
    public int getTextRunCursor(int contextStart, int contextEnd, int dir, int offset, int cursorOpt, Paint p) {
        int contextLen = contextEnd - contextStart;
        if (contextEnd <= this.mGapStart) {
            return p.getTextRunCursor(this.mText, contextStart, contextLen, dir, offset, cursorOpt);
        } else if (contextStart >= this.mGapStart) {
            return p.getTextRunCursor(this.mText, contextStart + this.mGapLength, contextLen, dir, offset + this.mGapLength, cursorOpt) - this.mGapLength;
        } else {
            char[] buf = TextUtils.obtain(contextLen);
            getChars(contextStart, contextEnd, buf, 0);
            int ret = p.getTextRunCursor(buf, 0, contextLen, dir, offset - contextStart, cursorOpt) + contextStart;
            TextUtils.recycle(buf);
            return ret;
        }
    }

    public void setFilters(InputFilter[] filters) {
        if (filters == null) {
            throw new IllegalArgumentException();
        }
        this.mFilters = filters;
    }

    public InputFilter[] getFilters() {
        return this.mFilters;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Spanned) || !toString().equals(o.toString())) {
            return false;
        }
        Spanned other = (Spanned) o;
        Object[] otherSpans = other.getSpans(0, other.length(), Object.class);
        if (this.mSpanCount != otherSpans.length) {
            return false;
        }
        for (int i = 0; i < this.mSpanCount; i += MARK) {
            SpannableStringBuilder thisSpan = this.mSpans[i];
            Spanned otherSpan = otherSpans[i];
            if (thisSpan == this) {
                if (other != otherSpan || getSpanStart(thisSpan) != other.getSpanStart(otherSpan) || getSpanEnd(thisSpan) != other.getSpanEnd(otherSpan) || getSpanFlags(thisSpan) != other.getSpanFlags(otherSpan)) {
                    return false;
                }
            } else if (!thisSpan.equals(otherSpan) || getSpanStart(thisSpan) != other.getSpanStart(otherSpan) || getSpanEnd(thisSpan) != other.getSpanEnd(otherSpan) || getSpanFlags(thisSpan) != other.getSpanFlags(otherSpan)) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int hash = (toString().hashCode() * 31) + this.mSpanCount;
        for (int i = 0; i < this.mSpanCount; i += MARK) {
            SpannableStringBuilder span = this.mSpans[i];
            if (span != this) {
                hash = (hash * 31) + span.hashCode();
            }
            hash = (((((hash * 31) + getSpanStart(span)) * 31) + getSpanEnd(span)) * 31) + getSpanFlags(span);
        }
        return hash;
    }

    static {
        NO_FILTERS = new InputFilter[0];
    }
}
