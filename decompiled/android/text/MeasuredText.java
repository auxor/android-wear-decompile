package android.text;

import android.graphics.Paint.FontMetricsInt;
import android.text.style.MetricAffectingSpan;
import android.text.style.ReplacementSpan;
import com.android.internal.util.ArrayUtils;

class MeasuredText {
    private static final boolean localLOGV = false;
    private static final MeasuredText[] sCached;
    private static final Object[] sLock;
    char[] mChars;
    int mDir;
    boolean mEasy;
    int mLen;
    byte[] mLevels;
    private int mPos;
    CharSequence mText;
    int mTextStart;
    float[] mWidths;
    private TextPaint mWorkPaint;

    private MeasuredText() {
        this.mWorkPaint = new TextPaint();
    }

    static {
        sLock = new Object[0];
        sCached = new MeasuredText[3];
    }

    static MeasuredText obtain() {
        synchronized (sLock) {
            int i = sCached.length;
            do {
                i--;
                if (i < 0) {
                    return new MeasuredText();
                }
            } while (sCached[i] == null);
            MeasuredText mt = sCached[i];
            sCached[i] = null;
            return mt;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static android.text.MeasuredText recycle(android.text.MeasuredText r4) {
        /*
        r3 = 0;
        r4.mText = r3;
        r1 = r4.mLen;
        r2 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        if (r1 >= r2) goto L_0x0020;
    L_0x0009:
        r2 = sLock;
        monitor-enter(r2);
        r0 = 0;
    L_0x000d:
        r1 = sCached;	 Catch:{ all -> 0x0024 }
        r1 = r1.length;	 Catch:{ all -> 0x0024 }
        if (r0 >= r1) goto L_0x001f;
    L_0x0012:
        r1 = sCached;	 Catch:{ all -> 0x0024 }
        r1 = r1[r0];	 Catch:{ all -> 0x0024 }
        if (r1 != 0) goto L_0x0021;
    L_0x0018:
        r1 = sCached;	 Catch:{ all -> 0x0024 }
        r1[r0] = r4;	 Catch:{ all -> 0x0024 }
        r1 = 0;
        r4.mText = r1;	 Catch:{ all -> 0x0024 }
    L_0x001f:
        monitor-exit(r2);	 Catch:{ all -> 0x0024 }
    L_0x0020:
        return r3;
    L_0x0021:
        r0 = r0 + 1;
        goto L_0x000d;
    L_0x0024:
        r1 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x0024 }
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.MeasuredText.recycle(android.text.MeasuredText):android.text.MeasuredText");
    }

    void setPos(int pos) {
        this.mPos = pos - this.mTextStart;
    }

    void setPara(CharSequence text, int start, int end, TextDirectionHeuristic textDir) {
        this.mText = text;
        this.mTextStart = start;
        int len = end - start;
        this.mLen = len;
        this.mPos = 0;
        if (this.mWidths == null || this.mWidths.length < len) {
            this.mWidths = ArrayUtils.newUnpaddedFloatArray(len);
        }
        if (this.mChars == null || this.mChars.length < len) {
            this.mChars = ArrayUtils.newUnpaddedCharArray(len);
        }
        TextUtils.getChars(text, start, end, this.mChars, 0);
        if (text instanceof Spanned) {
            Spanned spanned = (Spanned) text;
            ReplacementSpan[] spans = (ReplacementSpan[]) spanned.getSpans(start, end, ReplacementSpan.class);
            for (int i = 0; i < spans.length; i++) {
                int startInPara = spanned.getSpanStart(spans[i]) - start;
                int endInPara = spanned.getSpanEnd(spans[i]) - start;
                if (startInPara < 0) {
                    startInPara = 0;
                }
                if (endInPara > len) {
                    endInPara = len;
                }
                for (int j = startInPara; j < endInPara; j++) {
                    this.mChars[j] = '\ufffc';
                }
            }
        }
        if ((textDir == TextDirectionHeuristics.LTR || textDir == TextDirectionHeuristics.FIRSTSTRONG_LTR || textDir == TextDirectionHeuristics.ANYRTL_LTR) && TextUtils.doesNotNeedBidi(this.mChars, 0, len)) {
            this.mDir = 1;
            this.mEasy = true;
            return;
        }
        int bidiRequest;
        if (this.mLevels == null || this.mLevels.length < len) {
            this.mLevels = ArrayUtils.newUnpaddedByteArray(len);
        }
        if (textDir == TextDirectionHeuristics.LTR) {
            bidiRequest = 1;
        } else if (textDir == TextDirectionHeuristics.RTL) {
            bidiRequest = -1;
        } else if (textDir == TextDirectionHeuristics.FIRSTSTRONG_LTR) {
            bidiRequest = 2;
        } else if (textDir == TextDirectionHeuristics.FIRSTSTRONG_RTL) {
            bidiRequest = -2;
        } else {
            bidiRequest = textDir.isRtl(this.mChars, 0, len) ? -1 : 1;
        }
        this.mDir = AndroidBidi.bidi(bidiRequest, this.mChars, this.mLevels, len, false);
        this.mEasy = false;
    }

    float addStyleRun(TextPaint paint, int len, FontMetricsInt fm) {
        if (fm != null) {
            paint.getFontMetricsInt(fm);
        }
        int p = this.mPos;
        this.mPos = p + len;
        if (this.mEasy) {
            return paint.getTextRunAdvances(this.mChars, p, len, p, len, this.mDir != 1, this.mWidths, p);
        }
        float totalAdvance = 0.0f;
        int level = this.mLevels[p];
        int q = p;
        int i = p + 1;
        int e = p + len;
        while (true) {
            if (i == e || this.mLevels[i] != level) {
                totalAdvance += paint.getTextRunAdvances(this.mChars, q, i - q, q, i - q, (level & 1) != 0, this.mWidths, q);
                if (i == e) {
                    return totalAdvance;
                }
                q = i;
                level = this.mLevels[i];
            }
            i++;
        }
    }

    float addStyleRun(TextPaint paint, MetricAffectingSpan[] spans, int len, FontMetricsInt fm) {
        int i;
        float wid;
        TextPaint workPaint = this.mWorkPaint;
        workPaint.set(paint);
        workPaint.baselineShift = 0;
        ReplacementSpan replacement = null;
        for (MetricAffectingSpan span : spans) {
            if (span instanceof ReplacementSpan) {
                replacement = (ReplacementSpan) span;
            } else {
                span.updateMeasureState(workPaint);
            }
        }
        if (replacement == null) {
            wid = addStyleRun(workPaint, len, fm);
        } else {
            wid = (float) replacement.getSize(workPaint, this.mText, this.mTextStart + this.mPos, (this.mTextStart + this.mPos) + len, fm);
            float[] w = this.mWidths;
            w[this.mPos] = wid;
            int e = this.mPos + len;
            for (i = this.mPos + 1; i < e; i++) {
                w[i] = 0.0f;
            }
            this.mPos += len;
        }
        if (fm != null) {
            if (workPaint.baselineShift < 0) {
                fm.ascent += workPaint.baselineShift;
                fm.top += workPaint.baselineShift;
            } else {
                fm.descent += workPaint.baselineShift;
                fm.bottom += workPaint.baselineShift;
            }
        }
        return wid;
    }

    int breakText(int limit, boolean forwards, float width) {
        float[] w = this.mWidths;
        int i;
        if (forwards) {
            i = 0;
            while (i < limit) {
                width -= w[i];
                if (width < 0.0f) {
                    break;
                }
                i++;
            }
            while (i > 0 && this.mChars[i - 1] == ' ') {
                i--;
            }
            return i;
        }
        i = limit - 1;
        while (i >= 0) {
            width -= w[i];
            if (width < 0.0f) {
                break;
            }
            i--;
        }
        while (i < limit - 1 && this.mChars[i + 1] == ' ') {
            i++;
        }
        return (limit - i) - 1;
    }

    float measure(int start, int limit) {
        float width = 0.0f;
        float[] w = this.mWidths;
        for (int i = start; i < limit; i++) {
            width += w[i];
        }
        return width;
    }
}
