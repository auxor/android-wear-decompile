package libcore.icu;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParser;

public final class NativeBreakIterator implements Cloneable {
    private static final int BI_CHAR_INSTANCE = 1;
    private static final int BI_LINE_INSTANCE = 3;
    private static final int BI_SENT_INSTANCE = 4;
    private static final int BI_WORD_INSTANCE = 2;
    private final long address;
    private CharacterIterator charIterator;
    private String string;
    private final int type;

    private static native synchronized long cloneImpl(long j);

    private static native synchronized void closeImpl(long j);

    private static native synchronized int currentImpl(long j, String str);

    private static native synchronized int firstImpl(long j, String str);

    private static native synchronized int followingImpl(long j, String str, int i);

    private static native long getCharacterInstanceImpl(String str);

    private static native long getLineInstanceImpl(String str);

    private static native long getSentenceInstanceImpl(String str);

    private static native long getWordInstanceImpl(String str);

    private static native synchronized boolean isBoundaryImpl(long j, String str, int i);

    private static native synchronized int lastImpl(long j, String str);

    private static native synchronized int nextImpl(long j, String str, int i);

    private static native synchronized int precedingImpl(long j, String str, int i);

    private static native synchronized int previousImpl(long j, String str);

    private static native synchronized void setTextImpl(long j, String str);

    private NativeBreakIterator(long address, int type) {
        this.address = address;
        this.type = type;
        this.charIterator = new StringCharacterIterator(XmlPullParser.NO_NAMESPACE);
    }

    public Object clone() {
        NativeBreakIterator clone = new NativeBreakIterator(cloneImpl(this.address), this.type);
        clone.string = this.string;
        clone.charIterator = this.charIterator;
        return clone;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof NativeBreakIterator)) {
            return false;
        }
        NativeBreakIterator rhs = (NativeBreakIterator) object;
        if (this.type == rhs.type && this.charIterator.equals(rhs.charIterator)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return 42;
    }

    protected void finalize() throws Throwable {
        try {
            closeImpl(this.address);
        } finally {
            super.finalize();
        }
    }

    public int current() {
        return currentImpl(this.address, this.string);
    }

    public int first() {
        return firstImpl(this.address, this.string);
    }

    public int following(int offset) {
        return followingImpl(this.address, this.string, offset);
    }

    public CharacterIterator getText() {
        this.charIterator.setIndex(currentImpl(this.address, this.string));
        return this.charIterator;
    }

    public int last() {
        return lastImpl(this.address, this.string);
    }

    public int next(int n) {
        return nextImpl(this.address, this.string, n);
    }

    public int next() {
        return nextImpl(this.address, this.string, BI_CHAR_INSTANCE);
    }

    public int previous() {
        return previousImpl(this.address, this.string);
    }

    public void setText(CharacterIterator newText) {
        StringBuilder sb = new StringBuilder();
        char c = newText.first();
        while (c != CharacterIterator.DONE) {
            sb.append(c);
            c = newText.next();
        }
        setText(sb.toString(), newText);
    }

    public void setText(String newText) {
        setText(newText, new StringCharacterIterator(newText));
    }

    private void setText(String s, CharacterIterator it) {
        this.string = s;
        this.charIterator = it;
        setTextImpl(this.address, this.string);
    }

    public boolean hasText() {
        return this.string != null;
    }

    public boolean isBoundary(int offset) {
        return isBoundaryImpl(this.address, this.string, offset);
    }

    public int preceding(int offset) {
        return precedingImpl(this.address, this.string, offset);
    }

    public static NativeBreakIterator getCharacterInstance(Locale locale) {
        return new NativeBreakIterator(getCharacterInstanceImpl(locale.toLanguageTag()), BI_CHAR_INSTANCE);
    }

    public static NativeBreakIterator getLineInstance(Locale locale) {
        return new NativeBreakIterator(getLineInstanceImpl(locale.toLanguageTag()), BI_LINE_INSTANCE);
    }

    public static NativeBreakIterator getSentenceInstance(Locale locale) {
        return new NativeBreakIterator(getSentenceInstanceImpl(locale.toLanguageTag()), BI_SENT_INSTANCE);
    }

    public static NativeBreakIterator getWordInstance(Locale locale) {
        return new NativeBreakIterator(getWordInstanceImpl(locale.toLanguageTag()), BI_WORD_INSTANCE);
    }
}
