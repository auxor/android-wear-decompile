package com.google.common.base;

import java.util.Arrays;

public abstract class CharMatcher implements Predicate<Character> {
    public static final CharMatcher ANY;
    public static final CharMatcher ASCII;
    public static final CharMatcher BREAKING_WHITESPACE;
    public static final CharMatcher DIGIT;
    public static final CharMatcher INVISIBLE;
    public static final CharMatcher JAVA_DIGIT;
    public static final CharMatcher JAVA_ISO_CONTROL;
    public static final CharMatcher JAVA_LETTER;
    public static final CharMatcher JAVA_LETTER_OR_DIGIT;
    public static final CharMatcher JAVA_LOWER_CASE;
    public static final CharMatcher JAVA_UPPER_CASE;
    private static final String NINES;
    public static final CharMatcher NONE;
    public static final CharMatcher SINGLE_WIDTH;
    public static final CharMatcher WHITESPACE;
    static final int WHITESPACE_SHIFT;
    final String description;

    static abstract class FastMatcher extends CharMatcher {
        FastMatcher(String str) {
            super(str);
        }

        public /* bridge */ /* synthetic */ boolean apply(Object obj) {
            return super.apply((Character) obj);
        }
    }

    /* renamed from: com.google.common.base.CharMatcher.13 */
    static final class AnonymousClass13 extends FastMatcher {
        final /* synthetic */ char val$endInclusive;
        final /* synthetic */ char val$startInclusive;

        AnonymousClass13(String str, char c, char c2) {
            this.val$startInclusive = c;
            this.val$endInclusive = c2;
            super(str);
            throw new VerifyError("bad dex opcode");
        }

        public boolean matches(char c) {
            return this.val$startInclusive <= c && c <= this.val$endInclusive;
        }
    }

    /* renamed from: com.google.common.base.CharMatcher.15 */
    static final class AnonymousClass15 extends FastMatcher {
        AnonymousClass15(String str) {
            super(str);
        }

        public boolean matches(char c) {
            return "\u2002\u3000\r\u0085\u200a\u2005\u2000\u3000\u2029\u000b\u3000\u2008\u2003\u205f\u3000\u1680\t \u2006\u2001\u202f\u00a0\f\u2009\u3000\u2004\u3000\u3000\u2028\n\u2007\u3000".charAt((1682554634 * c) >>> WHITESPACE_SHIFT) == c;
        }
    }

    /* renamed from: com.google.common.base.CharMatcher.2 */
    static final class AnonymousClass2 extends CharMatcher {
        AnonymousClass2(String str) {
            super(str);
        }

        public /* bridge */ /* synthetic */ boolean apply(Object obj) {
            return super.apply((Character) obj);
        }

        public boolean matches(char c) {
            return Character.isDigit(c);
        }
    }

    /* renamed from: com.google.common.base.CharMatcher.3 */
    static final class AnonymousClass3 extends CharMatcher {
        AnonymousClass3(String str) {
            super(str);
        }

        public /* bridge */ /* synthetic */ boolean apply(Object obj) {
            return super.apply((Character) obj);
        }

        public boolean matches(char c) {
            return Character.isLetter(c);
        }
    }

    /* renamed from: com.google.common.base.CharMatcher.4 */
    static final class AnonymousClass4 extends CharMatcher {
        AnonymousClass4(String str) {
            super(str);
        }

        public /* bridge */ /* synthetic */ boolean apply(Object obj) {
            return super.apply((Character) obj);
        }

        public boolean matches(char c) {
            return Character.isLetterOrDigit(c);
        }
    }

    /* renamed from: com.google.common.base.CharMatcher.5 */
    static final class AnonymousClass5 extends CharMatcher {
        AnonymousClass5(String str) {
            super(str);
        }

        public /* bridge */ /* synthetic */ boolean apply(Object obj) {
            return super.apply((Character) obj);
        }

        public boolean matches(char c) {
            return Character.isUpperCase(c);
        }
    }

    /* renamed from: com.google.common.base.CharMatcher.6 */
    static final class AnonymousClass6 extends CharMatcher {
        AnonymousClass6(String str) {
            super(str);
        }

        public /* bridge */ /* synthetic */ boolean apply(Object obj) {
            return super.apply((Character) obj);
        }

        public boolean matches(char c) {
            return Character.isLowerCase(c);
        }
    }

    /* renamed from: com.google.common.base.CharMatcher.7 */
    static final class AnonymousClass7 extends FastMatcher {
        AnonymousClass7(String str) {
            super(str);
        }

        public boolean matches(char c) {
            return true;
        }

        public CharMatcher or(CharMatcher charMatcher) {
            Preconditions.checkNotNull(charMatcher);
            return this;
        }
    }

    /* renamed from: com.google.common.base.CharMatcher.8 */
    static final class AnonymousClass8 extends FastMatcher {
        AnonymousClass8(String str) {
            super(str);
        }

        public boolean matches(char c) {
            return false;
        }

        public CharMatcher or(CharMatcher charMatcher) {
            return (CharMatcher) Preconditions.checkNotNull(charMatcher);
        }
    }

    private static class Or extends CharMatcher {
        final CharMatcher first;
        final CharMatcher second;

        Or(CharMatcher charMatcher, CharMatcher charMatcher2) {
            this(charMatcher, charMatcher2, "CharMatcher.or(" + charMatcher + ", " + charMatcher2 + ")");
            throw new VerifyError("bad dex opcode");
        }

        Or(CharMatcher charMatcher, CharMatcher charMatcher2, String str) {
            super(str);
            CharMatcher charMatcher3 = (CharMatcher) Preconditions.checkNotNull(charMatcher);
            throw new VerifyError("bad dex opcode");
        }

        public /* bridge */ /* synthetic */ boolean apply(Object obj) {
            return super.apply((Character) obj);
        }

        public boolean matches(char c) {
            return this.first.matches(c) || this.second.matches(c);
        }

        CharMatcher withToString(String str) {
            return new Or(this.first, this.second, str);
        }
    }

    private static class RangesMatcher extends CharMatcher {
        private final char[] rangeEnds;
        private final char[] rangeStarts;

        RangesMatcher(String str, char[] cArr, char[] cArr2) {
            super(str);
            this.rangeStarts = cArr;
            this.rangeEnds = cArr2;
            Preconditions.checkArgument(cArr.length == cArr2.length);
            for (int i = 0; i < cArr.length; i++) {
                Preconditions.checkArgument(cArr[i] <= cArr2[i]);
                if (i + 1 < cArr.length) {
                    Preconditions.checkArgument(cArr2[i] < cArr[i + 1]);
                }
            }
            throw new VerifyError("bad dex opcode");
        }

        public /* bridge */ /* synthetic */ boolean apply(Object obj) {
            return super.apply((Character) obj);
        }

        public boolean matches(char c) {
            int binarySearch = Arrays.binarySearch(this.rangeStarts, c);
            if (binarySearch < 0) {
                binarySearch = (binarySearch ^ -1) - 1;
                if (binarySearch < 0 || c > this.rangeEnds[binarySearch]) {
                    return false;
                }
            }
            return true;
        }
    }

    static {
        BREAKING_WHITESPACE = new CharMatcher() {
            public /* bridge */ /* synthetic */ boolean apply(Object obj) {
                return super.apply((Character) obj);
            }

            public boolean matches(char c) {
                switch (c) {
                    case '\t':
                    case '\n':
                    case '\u000b':
                    case '\f':
                    case '\r':
                    case ' ':
                    case '\u0085':
                    case '\u1680':
                    case '\u2028':
                    case '\u2029':
                    case '\u205f':
                    case '\u3000':
                        break;
                    case '\u2007':
                        return false;
                    default:
                        if (c < '\u2000' || c > '\u200a') {
                            return false;
                        }
                }
                return true;
            }

            public String toString() {
                return "CharMatcher.BREAKING_WHITESPACE";
            }
        };
        ASCII = inRange('\u0000', '\u007f', "CharMatcher.ASCII");
        StringBuilder stringBuilder = new StringBuilder("0\u0660\u06f0\u07c0\u0966\u09e6\u0a66\u0ae6\u0b66\u0be6\u0c66\u0ce6\u0d66\u0e50\u0ed0\u0f20\u1040\u1090\u17e0\u1810\u1946\u19d0\u1b50\u1bb0\u1c40\u1c50\ua620\ua8d0\ua900\uaa50\uff10".length());
        for (int i = 0; i < "0\u0660\u06f0\u07c0\u0966\u09e6\u0a66\u0ae6\u0b66\u0be6\u0c66\u0ce6\u0d66\u0e50\u0ed0\u0f20\u1040\u1090\u17e0\u1810\u1946\u19d0\u1b50\u1bb0\u1c40\u1c50\ua620\ua8d0\ua900\uaa50\uff10".length(); i++) {
            stringBuilder.append((char) ("0\u0660\u06f0\u07c0\u0966\u09e6\u0a66\u0ae6\u0b66\u0be6\u0c66\u0ce6\u0d66\u0e50\u0ed0\u0f20\u1040\u1090\u17e0\u1810\u1946\u19d0\u1b50\u1bb0\u1c40\u1c50\ua620\ua8d0\ua900\uaa50\uff10".charAt(i) + 9));
        }
        NINES = stringBuilder.toString();
        DIGIT = new RangesMatcher("CharMatcher.DIGIT", "0\u0660\u06f0\u07c0\u0966\u09e6\u0a66\u0ae6\u0b66\u0be6\u0c66\u0ce6\u0d66\u0e50\u0ed0\u0f20\u1040\u1090\u17e0\u1810\u1946\u19d0\u1b50\u1bb0\u1c40\u1c50\ua620\ua8d0\ua900\uaa50\uff10".toCharArray(), NINES.toCharArray());
        JAVA_DIGIT = new AnonymousClass2("CharMatcher.JAVA_DIGIT");
        JAVA_LETTER = new AnonymousClass3("CharMatcher.JAVA_LETTER");
        JAVA_LETTER_OR_DIGIT = new AnonymousClass4("CharMatcher.JAVA_LETTER_OR_DIGIT");
        JAVA_UPPER_CASE = new AnonymousClass5("CharMatcher.JAVA_UPPER_CASE");
        JAVA_LOWER_CASE = new AnonymousClass6("CharMatcher.JAVA_LOWER_CASE");
        JAVA_ISO_CONTROL = inRange('\u0000', '\u001f').or(inRange('\u007f', '\u009f')).withToString("CharMatcher.JAVA_ISO_CONTROL");
        INVISIBLE = new RangesMatcher("CharMatcher.INVISIBLE", "\u0000\u007f\u00ad\u0600\u061c\u06dd\u070f\u1680\u180e\u2000\u2028\u205f\u2066\u2067\u2068\u2069\u206a\u3000\ud800\ufeff\ufff9\ufffa".toCharArray(), " \u00a0\u00ad\u0604\u061c\u06dd\u070f\u1680\u180e\u200f\u202f\u2064\u2066\u2067\u2068\u2069\u206f\u3000\uf8ff\ufeff\ufff9\ufffb".toCharArray());
        SINGLE_WIDTH = new RangesMatcher("CharMatcher.SINGLE_WIDTH", "\u0000\u05be\u05d0\u05f3\u0600\u0750\u0e00\u1e00\u2100\ufb50\ufe70\uff61".toCharArray(), "\u04f9\u05be\u05ea\u05f4\u06ff\u077f\u0e7f\u20af\u213a\ufdff\ufeff\uffdc".toCharArray());
        ANY = new AnonymousClass7("CharMatcher.ANY");
        NONE = new AnonymousClass8("CharMatcher.NONE");
        WHITESPACE_SHIFT = Integer.numberOfLeadingZeros("\u2002\u3000\r\u0085\u200a\u2005\u2000\u3000\u2029\u000b\u3000\u2008\u2003\u205f\u3000\u1680\t \u2006\u2001\u202f\u00a0\f\u2009\u3000\u2004\u3000\u3000\u2028\n\u2007\u3000".length() - 1);
        WHITESPACE = new AnonymousClass15("WHITESPACE");
    }

    protected CharMatcher() {
        this.description = super.toString();
        throw new VerifyError("bad dex opcode");
    }

    CharMatcher(String str) {
        this.description = str;
        throw new VerifyError("bad dex opcode");
    }

    public static CharMatcher inRange(char c, char c2) {
        Preconditions.checkArgument(c2 >= c);
        return inRange(c, c2, "CharMatcher.inRange('" + showCharacter(c) + "', '" + showCharacter(c2) + "')");
    }

    static CharMatcher inRange(char c, char c2, String str) {
        return new AnonymousClass13(str, c, c2);
    }

    private static String showCharacter(char c) {
        int i = 0;
        char[] cArr = new char[]{'\\', 'u', '\u0000', '\u0000', '\u0000', '\u0000'};
        while (i < 4) {
            cArr[5 - i] = "0123456789ABCDEF".charAt(r6 & 15);
            int i2 = (char) (i2 >> 4);
            i++;
        }
        return String.copyValueOf(cArr);
    }

    @Deprecated
    public boolean apply(Character ch) {
        return matches(ch.charValue());
    }

    public abstract boolean matches(char c);

    public CharMatcher or(CharMatcher charMatcher) {
        return new Or(this, (CharMatcher) Preconditions.checkNotNull(charMatcher));
    }

    public String toString() {
        return this.description;
    }

    CharMatcher withToString(String str) {
        throw new UnsupportedOperationException();
    }
}
