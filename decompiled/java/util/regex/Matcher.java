package java.util.regex;

import org.xmlpull.v1.XmlPullParser;

public final class Matcher implements MatchResult {
    private long address;
    private boolean anchoringBounds;
    private int appendPos;
    private String input;
    private boolean matchFound;
    private int[] matchOffsets;
    private Pattern pattern;
    private int regionEnd;
    private int regionStart;
    private boolean transparentBounds;

    private static native void closeImpl(long j);

    private static native boolean findImpl(long j, String str, int i, int[] iArr);

    private static native boolean findNextImpl(long j, String str, int[] iArr);

    private static native int groupCountImpl(long j);

    private static native boolean hitEndImpl(long j);

    private static native boolean lookingAtImpl(long j, String str, int[] iArr);

    private static native boolean matchesImpl(long j, String str, int[] iArr);

    private static native long openImpl(long j);

    private static native boolean requireEndImpl(long j);

    private static native void setInputImpl(long j, String str, int i, int i2);

    private static native void useAnchoringBoundsImpl(long j, boolean z);

    private static native void useTransparentBoundsImpl(long j, boolean z);

    Matcher(Pattern pattern, CharSequence input) {
        this.anchoringBounds = true;
        usePattern(pattern);
        reset(input);
    }

    public Matcher appendReplacement(StringBuffer buffer, String replacement) {
        buffer.append(this.input.substring(this.appendPos, start()));
        appendEvaluated(buffer, replacement);
        this.appendPos = end();
        return this;
    }

    private void appendEvaluated(StringBuffer buffer, String s) {
        boolean escape = false;
        boolean dollar = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && !escape) {
                escape = true;
            } else if (c == '$' && !escape) {
                dollar = true;
            } else if (c < '0' || c > '9' || !dollar) {
                buffer.append(c);
                dollar = false;
                escape = false;
            } else {
                buffer.append(group(c - 48));
                dollar = false;
            }
        }
        if (escape) {
            throw new ArrayIndexOutOfBoundsException(s.length());
        }
    }

    public Matcher reset() {
        return reset(this.input, 0, this.input.length());
    }

    public Matcher reset(CharSequence input) {
        return reset(input, 0, input.length());
    }

    private Matcher reset(CharSequence input, int start, int end) {
        if (input == null) {
            throw new IllegalArgumentException("input == null");
        } else if (start < 0 || end < 0 || start > input.length() || end > input.length() || start > end) {
            throw new IndexOutOfBoundsException();
        } else {
            this.input = input.toString();
            this.regionStart = start;
            this.regionEnd = end;
            resetForInput();
            this.matchFound = false;
            this.appendPos = 0;
            return this;
        }
    }

    public Matcher usePattern(Pattern pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("pattern == null");
        }
        this.pattern = pattern;
        synchronized (this) {
            if (this.address != 0) {
                closeImpl(this.address);
                this.address = 0;
            }
            this.address = openImpl(pattern.address);
        }
        if (this.input != null) {
            resetForInput();
        }
        this.matchOffsets = new int[((groupCount() + 1) * 2)];
        this.matchFound = false;
        return this;
    }

    private void resetForInput() {
        synchronized (this) {
            setInputImpl(this.address, this.input, this.regionStart, this.regionEnd);
            useAnchoringBoundsImpl(this.address, this.anchoringBounds);
            useTransparentBoundsImpl(this.address, this.transparentBounds);
        }
    }

    public Matcher region(int start, int end) {
        return reset(this.input, start, end);
    }

    public StringBuffer appendTail(StringBuffer buffer) {
        if (this.appendPos < this.regionEnd) {
            buffer.append(this.input.substring(this.appendPos, this.regionEnd));
        }
        return buffer;
    }

    public String replaceFirst(String replacement) {
        reset();
        StringBuffer buffer = new StringBuffer(this.input.length());
        if (find()) {
            appendReplacement(buffer, replacement);
        }
        return appendTail(buffer).toString();
    }

    public String replaceAll(String replacement) {
        reset();
        StringBuffer buffer = new StringBuffer(this.input.length());
        while (find()) {
            appendReplacement(buffer, replacement);
        }
        return appendTail(buffer).toString();
    }

    public Pattern pattern() {
        return this.pattern;
    }

    public boolean find(int start) {
        if (start < 0 || start > this.input.length()) {
            throw new IndexOutOfBoundsException("start=" + start + "; length=" + this.input.length());
        }
        synchronized (this) {
            this.matchFound = findImpl(this.address, this.input, start, this.matchOffsets);
        }
        return this.matchFound;
    }

    public boolean find() {
        synchronized (this) {
            this.matchFound = findNextImpl(this.address, this.input, this.matchOffsets);
        }
        return this.matchFound;
    }

    public boolean lookingAt() {
        synchronized (this) {
            this.matchFound = lookingAtImpl(this.address, this.input, this.matchOffsets);
        }
        return this.matchFound;
    }

    public boolean matches() {
        synchronized (this) {
            this.matchFound = matchesImpl(this.address, this.input, this.matchOffsets);
        }
        return this.matchFound;
    }

    public static String quoteReplacement(String s) {
        StringBuilder result = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' || c == '$') {
                result.append('\\');
            }
            result.append(c);
        }
        return result.toString();
    }

    public MatchResult toMatchResult() {
        ensureMatch();
        return new MatchResultImpl(this.input, this.matchOffsets);
    }

    public Matcher useAnchoringBounds(boolean value) {
        synchronized (this) {
            this.anchoringBounds = value;
            useAnchoringBoundsImpl(this.address, value);
        }
        return this;
    }

    public boolean hasAnchoringBounds() {
        return this.anchoringBounds;
    }

    public Matcher useTransparentBounds(boolean value) {
        synchronized (this) {
            this.transparentBounds = value;
            useTransparentBoundsImpl(this.address, value);
        }
        return this;
    }

    private void ensureMatch() {
        if (!this.matchFound) {
            throw new IllegalStateException("No successful match so far");
        }
    }

    public boolean hasTransparentBounds() {
        return this.transparentBounds;
    }

    public int regionStart() {
        return this.regionStart;
    }

    public int regionEnd() {
        return this.regionEnd;
    }

    public boolean requireEnd() {
        boolean requireEndImpl;
        synchronized (this) {
            requireEndImpl = requireEndImpl(this.address);
        }
        return requireEndImpl;
    }

    public boolean hitEnd() {
        boolean hitEndImpl;
        synchronized (this) {
            hitEndImpl = hitEndImpl(this.address);
        }
        return hitEndImpl;
    }

    protected void finalize() throws Throwable {
        try {
            synchronized (this) {
                closeImpl(this.address);
            }
        } finally {
            super.finalize();
        }
    }

    public String toString() {
        return getClass().getName() + "[pattern=" + pattern() + " region=" + regionStart() + "," + regionEnd() + " lastmatch=" + (this.matchFound ? group() : XmlPullParser.NO_NAMESPACE) + "]";
    }

    public int end() {
        return end(0);
    }

    public int end(int group) {
        ensureMatch();
        return this.matchOffsets[(group * 2) + 1];
    }

    public String group() {
        return group(0);
    }

    public String group(int group) {
        ensureMatch();
        int from = this.matchOffsets[group * 2];
        int to = this.matchOffsets[(group * 2) + 1];
        if (from == -1 || to == -1) {
            return null;
        }
        return this.input.substring(from, to);
    }

    public int groupCount() {
        int groupCountImpl;
        synchronized (this) {
            groupCountImpl = groupCountImpl(this.address);
        }
        return groupCountImpl;
    }

    public int start() {
        return start(0);
    }

    public int start(int group) throws IllegalStateException {
        ensureMatch();
        return this.matchOffsets[group * 2];
    }
}
