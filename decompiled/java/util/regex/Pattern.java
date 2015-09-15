package java.util.regex;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public final class Pattern implements Serializable {
    public static final int CANON_EQ = 128;
    public static final int CASE_INSENSITIVE = 2;
    public static final int COMMENTS = 4;
    public static final int DOTALL = 32;
    public static final int LITERAL = 16;
    public static final int MULTILINE = 8;
    public static final int UNICODE_CASE = 64;
    public static final int UNIX_LINES = 1;
    private static final long serialVersionUID = 5073258162644648461L;
    transient long address;
    private final int flags;
    private final String pattern;

    private static native void closeImpl(long j);

    private static native long compileImpl(String str, int i);

    public Matcher matcher(CharSequence input) {
        return new Matcher(this, input);
    }

    public String[] split(CharSequence input, int limit) {
        return Splitter.split(this, this.pattern, input.toString(), limit);
    }

    public String[] split(CharSequence input) {
        return split(input, 0);
    }

    public String pattern() {
        return this.pattern;
    }

    public String toString() {
        return this.pattern;
    }

    public int flags() {
        return this.flags;
    }

    public static Pattern compile(String regularExpression, int flags) throws PatternSyntaxException {
        return new Pattern(regularExpression, flags);
    }

    public static Pattern compile(String pattern) {
        return new Pattern(pattern, 0);
    }

    private Pattern(String pattern, int flags) throws PatternSyntaxException {
        if ((flags & CANON_EQ) != 0) {
            throw new UnsupportedOperationException("CANON_EQ flag not supported");
        } else if ((flags & -128) != 0) {
            throw new IllegalArgumentException("Unsupported flags: " + (flags & -128));
        } else {
            this.pattern = pattern;
            this.flags = flags;
            compile();
        }
    }

    private void compile() throws PatternSyntaxException {
        if (this.pattern == null) {
            throw new NullPointerException("pattern == null");
        }
        String icuPattern = this.pattern;
        if ((this.flags & LITERAL) != 0) {
            icuPattern = quote(this.pattern);
        }
        this.address = compileImpl(icuPattern, this.flags & 47);
    }

    public static boolean matches(String regularExpression, CharSequence input) {
        return new Matcher(new Pattern(regularExpression, 0), input).matches();
    }

    public static String quote(String string) {
        StringBuilder sb = new StringBuilder();
        sb.append("\\Q");
        int apos = 0;
        while (true) {
            int k = string.indexOf("\\E", apos);
            if (k < 0) {
                return sb.append(string.substring(apos)).append("\\E").toString();
            }
            sb.append(string.substring(apos, k + CASE_INSENSITIVE)).append("\\\\E\\Q");
            apos = k + CASE_INSENSITIVE;
        }
    }

    protected void finalize() throws Throwable {
        try {
            closeImpl(this.address);
        } finally {
            super.finalize();
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        compile();
    }
}
