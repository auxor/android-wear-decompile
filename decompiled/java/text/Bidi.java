package java.text;

import java.awt.font.NumericShaper;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Arrays;

public final class Bidi {
    public static final int DIRECTION_DEFAULT_LEFT_TO_RIGHT = -2;
    public static final int DIRECTION_DEFAULT_RIGHT_TO_LEFT = -1;
    public static final int DIRECTION_LEFT_TO_RIGHT = 0;
    public static final int DIRECTION_RIGHT_TO_LEFT = 1;
    private static final int UBIDI_LEVEL_OVERRIDE = 128;
    private static final int UBiDiDirection_UBIDI_LTR = 0;
    private static final int UBiDiDirection_UBIDI_MIXED = 2;
    private static final int UBiDiDirection_UBIDI_RTL = 1;
    private int baseLevel;
    private int direction;
    private int length;
    private byte[] offsetLevel;
    private Run[] runs;
    private boolean unidirectional;

    static class Run {
        private final int level;
        private final int limit;
        private final int start;

        public Run(int start, int limit, int level) {
            this.start = start;
            this.limit = limit;
            this.level = level;
        }

        public int getLevel() {
            return this.level;
        }

        public int getLimit() {
            return this.limit;
        }

        public int getStart() {
            return this.start;
        }
    }

    private static native void ubidi_close(long j);

    private static native int ubidi_countRuns(long j);

    private static native int ubidi_getDirection(long j);

    private static native int ubidi_getLength(long j);

    private static native byte[] ubidi_getLevels(long j);

    private static native byte ubidi_getParaLevel(long j);

    private static native Run[] ubidi_getRuns(long j);

    private static native long ubidi_open();

    private static native int[] ubidi_reorderVisual(byte[] bArr, int i);

    private static native long ubidi_setLine(long j, int i, int i2);

    private static native void ubidi_setPara(long j, char[] cArr, int i, int i2, byte[] bArr);

    public Bidi(AttributedCharacterIterator paragraph) {
        if (paragraph == null) {
            throw new IllegalArgumentException("paragraph is null");
        }
        int begin = paragraph.getBeginIndex();
        int length = paragraph.getEndIndex() - begin;
        char[] text = new char[(length + UBiDiDirection_UBIDI_RTL)];
        if (length != 0) {
            text[UBiDiDirection_UBIDI_LTR] = paragraph.first();
        } else {
            paragraph.first();
        }
        int flags = DIRECTION_DEFAULT_LEFT_TO_RIGHT;
        Object direction = paragraph.getAttribute(TextAttribute.RUN_DIRECTION);
        if (direction != null && (direction instanceof Boolean)) {
            flags = direction.equals(TextAttribute.RUN_DIRECTION_LTR) ? UBiDiDirection_UBIDI_LTR : UBiDiDirection_UBIDI_RTL;
        }
        byte[] embeddings = null;
        int textLimit = UBiDiDirection_UBIDI_RTL;
        int i = UBiDiDirection_UBIDI_RTL;
        while (i < length) {
            Object embedding = paragraph.getAttribute(TextAttribute.BIDI_EMBEDDING);
            if (embedding == null || !(embedding instanceof Integer)) {
                while (i < textLimit) {
                    text[i] = paragraph.next();
                    i += UBiDiDirection_UBIDI_RTL;
                }
            } else {
                int embLevel = ((Integer) embedding).intValue();
                if (embeddings == null) {
                    embeddings = new byte[length];
                }
                while (i < textLimit) {
                    text[i] = paragraph.next();
                    embeddings[i + DIRECTION_DEFAULT_RIGHT_TO_LEFT] = (byte) embLevel;
                    i += UBiDiDirection_UBIDI_RTL;
                }
            }
            textLimit = (paragraph.getRunLimit(TextAttribute.BIDI_EMBEDDING) - begin) + UBiDiDirection_UBIDI_RTL;
        }
        Object numericShaper = paragraph.getAttribute(TextAttribute.NUMERIC_SHAPING);
        if (numericShaper != null && (numericShaper instanceof NumericShaper)) {
            ((NumericShaper) numericShaper).shape(text, UBiDiDirection_UBIDI_LTR, length);
        }
        long bidi = 0;
        try {
            bidi = createUBiDi(text, UBiDiDirection_UBIDI_LTR, embeddings, UBiDiDirection_UBIDI_LTR, length, flags);
            readBidiInfo(bidi);
        } finally {
            ubidi_close(bidi);
        }
    }

    public Bidi(char[] text, int textStart, byte[] embeddings, int embStart, int paragraphLength, int flags) {
        if (text == null || text.length - textStart < paragraphLength) {
            throw new IllegalArgumentException();
        } else if (embeddings != null && embeddings.length - embStart < paragraphLength) {
            throw new IllegalArgumentException();
        } else if (textStart < 0) {
            throw new IllegalArgumentException("Negative textStart value " + textStart);
        } else if (embStart < 0) {
            throw new IllegalArgumentException("Negative embStart value " + embStart);
        } else if (paragraphLength < 0) {
            throw new IllegalArgumentException("Negative paragraph length " + paragraphLength);
        } else {
            long j = 0;
            try {
                j = createUBiDi(text, textStart, embeddings, embStart, paragraphLength, flags);
                readBidiInfo(j);
            } finally {
                ubidi_close(j);
            }
        }
    }

    public Bidi(String paragraph, int flags) {
        this(paragraph == null ? null : paragraph.toCharArray(), UBiDiDirection_UBIDI_LTR, null, UBiDiDirection_UBIDI_LTR, paragraph == null ? UBiDiDirection_UBIDI_LTR : paragraph.length(), flags);
    }

    private static long createUBiDi(char[] text, int textStart, byte[] embeddings, int embStart, int paragraphLength, int flags) {
        byte[] realEmbeddings = null;
        if (text == null || text.length - textStart < paragraphLength) {
            throw new IllegalArgumentException();
        }
        char[] realText = new char[paragraphLength];
        System.arraycopy(text, textStart, realText, (int) UBiDiDirection_UBIDI_LTR, paragraphLength);
        if (embeddings != null) {
            if (embeddings.length - embStart < paragraphLength) {
                throw new IllegalArgumentException();
            } else if (paragraphLength > 0) {
                realEmbeddings = new byte[paragraphLength];
                System.arraycopy(new Bidi(text, textStart, null, UBiDiDirection_UBIDI_LTR, paragraphLength, flags).offsetLevel, (int) UBiDiDirection_UBIDI_LTR, realEmbeddings, (int) UBiDiDirection_UBIDI_LTR, paragraphLength);
                for (int i = UBiDiDirection_UBIDI_LTR; i < paragraphLength; i += UBiDiDirection_UBIDI_RTL) {
                    byte e = embeddings[i];
                    if (e < null) {
                        realEmbeddings[i] = (byte) (128 - e);
                    } else if (e > null) {
                        realEmbeddings[i] = e;
                    } else {
                        realEmbeddings[i] = (byte) (realEmbeddings[i] | -128);
                    }
                }
            }
        }
        if (flags > UBiDiDirection_UBIDI_RTL || flags < DIRECTION_DEFAULT_LEFT_TO_RIGHT) {
            flags = UBiDiDirection_UBIDI_LTR;
        }
        long bidi = 0;
        boolean needsDeletion = true;
        try {
            bidi = ubidi_open();
            ubidi_setPara(bidi, realText, paragraphLength, flags, realEmbeddings);
            needsDeletion = false;
            return bidi;
        } finally {
            if (needsDeletion) {
                ubidi_close(bidi);
            }
        }
    }

    private Bidi(long pBidi) {
        readBidiInfo(pBidi);
    }

    private void readBidiInfo(long pBidi) {
        this.length = ubidi_getLength(pBidi);
        this.offsetLevel = this.length == 0 ? null : ubidi_getLevels(pBidi);
        this.baseLevel = ubidi_getParaLevel(pBidi);
        int runCount = ubidi_countRuns(pBidi);
        if (runCount == 0) {
            this.unidirectional = true;
            this.runs = null;
        } else if (runCount < 0) {
            this.runs = null;
        } else {
            this.runs = ubidi_getRuns(pBidi);
            if (runCount == UBiDiDirection_UBIDI_RTL && this.runs[UBiDiDirection_UBIDI_LTR].getLevel() == this.baseLevel) {
                this.unidirectional = true;
                this.runs = null;
            }
        }
        this.direction = ubidi_getDirection(pBidi);
    }

    public boolean baseIsLeftToRight() {
        return this.baseLevel % UBiDiDirection_UBIDI_MIXED == 0;
    }

    public Bidi createLineBidi(int lineStart, int lineLimit) {
        int dir = UBiDiDirection_UBIDI_LTR;
        if (lineStart < 0 || lineLimit < 0 || lineLimit > this.length || lineStart > lineLimit) {
            throw new IllegalArgumentException("Invalid ranges (start=" + lineStart + ", " + "limit=" + lineLimit + ", length=" + this.length + ")");
        }
        char[] text = new char[this.length];
        Arrays.fill(text, 'a');
        byte[] embeddings = new byte[this.length];
        for (int i = UBiDiDirection_UBIDI_LTR; i < embeddings.length; i += UBiDiDirection_UBIDI_RTL) {
            embeddings[i] = (byte) (-this.offsetLevel[i]);
        }
        if (!baseIsLeftToRight()) {
            dir = UBiDiDirection_UBIDI_RTL;
        }
        long j = 0;
        try {
            Bidi createEmptyLineBidi;
            j = createUBiDi(text, UBiDiDirection_UBIDI_LTR, embeddings, UBiDiDirection_UBIDI_LTR, this.length, dir);
            if (lineStart == lineLimit) {
                createEmptyLineBidi = createEmptyLineBidi(j);
            } else {
                createEmptyLineBidi = new Bidi(ubidi_setLine(j, lineStart, lineLimit));
                ubidi_close(j);
            }
            return createEmptyLineBidi;
        } finally {
            ubidi_close(j);
        }
    }

    private Bidi createEmptyLineBidi(long parent) {
        Bidi result = new Bidi(parent);
        result.length = UBiDiDirection_UBIDI_LTR;
        result.offsetLevel = null;
        result.runs = null;
        result.unidirectional = true;
        return result;
    }

    public int getBaseLevel() {
        return this.baseLevel;
    }

    public int getLength() {
        return this.length;
    }

    public int getLevelAt(int offset) {
        try {
            return this.offsetLevel[offset] & -129;
        } catch (RuntimeException e) {
            return this.baseLevel;
        }
    }

    public int getRunCount() {
        return this.unidirectional ? UBiDiDirection_UBIDI_RTL : this.runs.length;
    }

    public int getRunLevel(int run) {
        return this.unidirectional ? this.baseLevel : this.runs[run].getLevel();
    }

    public int getRunLimit(int run) {
        return this.unidirectional ? this.length : this.runs[run].getLimit();
    }

    public int getRunStart(int run) {
        return this.unidirectional ? UBiDiDirection_UBIDI_LTR : this.runs[run].getStart();
    }

    public boolean isLeftToRight() {
        return this.direction == 0;
    }

    public boolean isMixed() {
        return this.direction == UBiDiDirection_UBIDI_MIXED;
    }

    public boolean isRightToLeft() {
        return this.direction == UBiDiDirection_UBIDI_RTL;
    }

    public static void reorderVisually(byte[] levels, int levelStart, Object[] objects, int objectStart, int count) {
        if (count < 0 || levelStart < 0 || objectStart < 0 || count > levels.length - levelStart || count > objects.length - objectStart) {
            throw new IllegalArgumentException("Invalid ranges (levels=" + levels.length + ", levelStart=" + levelStart + ", objects=" + objects.length + ", objectStart=" + objectStart + ", count=" + count + ")");
        }
        byte[] realLevels = new byte[count];
        System.arraycopy(levels, levelStart, realLevels, (int) UBiDiDirection_UBIDI_LTR, count);
        int[] indices = ubidi_reorderVisual(realLevels, count);
        ArrayList<Object> result = new ArrayList(count);
        for (int i = UBiDiDirection_UBIDI_LTR; i < count; i += UBiDiDirection_UBIDI_RTL) {
            result.add(objects[indices[i] + objectStart]);
        }
        System.arraycopy(result.toArray(), (int) UBiDiDirection_UBIDI_LTR, (Object) objects, objectStart, count);
    }

    public static boolean requiresBidi(char[] text, int start, int limit) {
        if (limit < 0 || start < 0 || start > limit || limit > text.length) {
            throw new IllegalArgumentException();
        }
        if (new Bidi(text, start, null, UBiDiDirection_UBIDI_LTR, limit - start, UBiDiDirection_UBIDI_LTR).isLeftToRight()) {
            return false;
        }
        return true;
    }

    public String toString() {
        return getClass().getName() + "[direction: " + this.direction + " baseLevel: " + this.baseLevel + " length: " + this.length + " runs: " + Arrays.toString(this.runs) + "]";
    }
}
