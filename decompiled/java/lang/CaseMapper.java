package java.lang;

import dalvik.bytecode.Opcodes;
import java.util.Locale;
import libcore.icu.ICU;
import libcore.icu.Transliterator;

class CaseMapper {
    private static final ThreadLocal<Transliterator> EL_UPPER;
    private static final char GREEK_CAPITAL_SIGMA = '\u03a3';
    private static final char GREEK_SMALL_FINAL_SIGMA = '\u03c2';
    private static final char LATIN_CAPITAL_I_WITH_DOT = '\u0130';
    private static final char[] upperValues;
    private static final char[] upperValues2;

    static {
        upperValues = "SS\u0000\u02bcN\u0000J\u030c\u0000\u0399\u0308\u0301\u03a5\u0308\u0301\u0535\u0552\u0000H\u0331\u0000T\u0308\u0000W\u030a\u0000Y\u030a\u0000A\u02be\u0000\u03a5\u0313\u0000\u03a5\u0313\u0300\u03a5\u0313\u0301\u03a5\u0313\u0342\u1f08\u0399\u0000\u1f09\u0399\u0000\u1f0a\u0399\u0000\u1f0b\u0399\u0000\u1f0c\u0399\u0000\u1f0d\u0399\u0000\u1f0e\u0399\u0000\u1f0f\u0399\u0000\u1f08\u0399\u0000\u1f09\u0399\u0000\u1f0a\u0399\u0000\u1f0b\u0399\u0000\u1f0c\u0399\u0000\u1f0d\u0399\u0000\u1f0e\u0399\u0000\u1f0f\u0399\u0000\u1f28\u0399\u0000\u1f29\u0399\u0000\u1f2a\u0399\u0000\u1f2b\u0399\u0000\u1f2c\u0399\u0000\u1f2d\u0399\u0000\u1f2e\u0399\u0000\u1f2f\u0399\u0000\u1f28\u0399\u0000\u1f29\u0399\u0000\u1f2a\u0399\u0000\u1f2b\u0399\u0000\u1f2c\u0399\u0000\u1f2d\u0399\u0000\u1f2e\u0399\u0000\u1f2f\u0399\u0000\u1f68\u0399\u0000\u1f69\u0399\u0000\u1f6a\u0399\u0000\u1f6b\u0399\u0000\u1f6c\u0399\u0000\u1f6d\u0399\u0000\u1f6e\u0399\u0000\u1f6f\u0399\u0000\u1f68\u0399\u0000\u1f69\u0399\u0000\u1f6a\u0399\u0000\u1f6b\u0399\u0000\u1f6c\u0399\u0000\u1f6d\u0399\u0000\u1f6e\u0399\u0000\u1f6f\u0399\u0000\u1fba\u0399\u0000\u0391\u0399\u0000\u0386\u0399\u0000\u0391\u0342\u0000\u0391\u0342\u0399\u0391\u0399\u0000\u1fca\u0399\u0000\u0397\u0399\u0000\u0389\u0399\u0000\u0397\u0342\u0000\u0397\u0342\u0399\u0397\u0399\u0000\u0399\u0308\u0300\u0399\u0308\u0301\u0399\u0342\u0000\u0399\u0308\u0342\u03a5\u0308\u0300\u03a5\u0308\u0301\u03a1\u0313\u0000\u03a5\u0342\u0000\u03a5\u0308\u0342\u1ffa\u0399\u0000\u03a9\u0399\u0000\u038f\u0399\u0000\u03a9\u0342\u0000\u03a9\u0342\u0399\u03a9\u0399\u0000FF\u0000FI\u0000FL\u0000FFIFFLST\u0000ST\u0000\u0544\u0546\u0000\u0544\u0535\u0000\u0544\u053b\u0000\u054e\u0546\u0000\u0544\u053d\u0000".toCharArray();
        upperValues2 = "\u000b\u0000\f\u0000\r\u0000\u000e\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>\u0000\u0000?@A\u0000BC\u0000\u0000\u0000\u0000D\u0000\u0000\u0000\u0000\u0000EFG\u0000HI\u0000\u0000\u0000\u0000J\u0000\u0000\u0000\u0000\u0000KL\u0000\u0000MN\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000OPQ\u0000RS\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000TUV\u0000WX\u0000\u0000\u0000\u0000Y".toCharArray();
        EL_UPPER = new ThreadLocal<Transliterator>() {
            protected Transliterator initialValue() {
                return new Transliterator("el-Upper");
            }
        };
    }

    private CaseMapper() {
    }

    public static String toLowerCase(Locale locale, String s, char[] value, int offset, int count) {
        String languageCode = locale.getLanguage();
        if (languageCode.equals("tr") || languageCode.equals("az") || languageCode.equals("lt")) {
            return ICU.toLowerCase(s, locale);
        }
        char[] newValue = null;
        int newCount = 0;
        int i = offset;
        int end = offset + count;
        while (i < end) {
            char ch = value[i];
            if (ch == LATIN_CAPITAL_I_WITH_DOT || Character.isHighSurrogate(ch)) {
                return ICU.toLowerCase(s, locale);
            }
            char newCh;
            if (ch == GREEK_CAPITAL_SIGMA && isFinalSigma(value, offset, count, i)) {
                newCh = GREEK_SMALL_FINAL_SIGMA;
            } else {
                newCh = Character.toLowerCase(ch);
            }
            if (newValue == null && ch != newCh) {
                newValue = new char[count];
                newCount = i - offset;
                System.arraycopy(value, offset, newValue, 0, newCount);
            }
            int newCount2 = newCount;
            if (newValue != null) {
                newCount = newCount2 + 1;
                newValue[newCount2] = newCh;
            } else {
                newCount = newCount2;
            }
            i++;
        }
        return newValue != null ? new String(0, newCount, newValue) : s;
    }

    private static boolean isFinalSigma(char[] value, int offset, int count, int index) {
        if (index <= offset) {
            return false;
        }
        char previous = value[index - 1];
        if (!Character.isLowerCase(previous) && !Character.isUpperCase(previous) && !Character.isTitleCase(previous)) {
            return false;
        }
        if (index + 1 >= offset + count) {
            return true;
        }
        char next = value[index + 1];
        if (Character.isLowerCase(next) || Character.isUpperCase(next) || Character.isTitleCase(next)) {
            return false;
        }
        return true;
    }

    private static int upperIndex(int ch) {
        int index = -1;
        if (ch >= Opcodes.OP_XOR_INT_LIT8) {
            if (ch <= 1415) {
                switch (ch) {
                    case Opcodes.OP_XOR_INT_LIT8 /*223*/:
                        return 0;
                    case 329:
                        return 1;
                    case 496:
                        return 2;
                    case 912:
                        return 3;
                    case 944:
                        return 4;
                    case 1415:
                        return 5;
                }
            } else if (ch >= 7830) {
                if (ch <= 7834) {
                    index = (ch + 6) - 7830;
                } else if (ch >= 8016 && ch <= 8188) {
                    index = upperValues2[ch - 8016];
                    if (index == 0) {
                        index = -1;
                    }
                } else if (ch >= 64256) {
                    if (ch <= 64262) {
                        index = (ch + 90) - 64256;
                    } else if (ch >= 64275 && ch <= 64279) {
                        index = (ch + 97) - 64275;
                    }
                }
            }
        }
        return index;
    }

    public static String toUpperCase(Locale locale, String s, char[] value, int offset, int count) {
        String languageCode = locale.getLanguage();
        if (languageCode.equals("tr") || languageCode.equals("az") || languageCode.equals("lt")) {
            return ICU.toUpperCase(s, locale);
        }
        if (languageCode.equals("el")) {
            return ((Transliterator) EL_UPPER.get()).transliterate(s);
        }
        char[] output = null;
        int o = offset;
        int end = offset + count;
        int i = 0;
        while (o < end) {
            char ch = value[o];
            if (Character.isHighSurrogate(ch)) {
                return ICU.toUpperCase(s, locale);
            }
            int i2;
            int index = upperIndex(ch);
            char[] newoutput;
            if (index == -1) {
                if (output != null && i >= output.length) {
                    newoutput = new char[((output.length + (count / 6)) + 2)];
                    System.arraycopy(output, 0, newoutput, 0, output.length);
                    output = newoutput;
                }
                char upch = Character.toUpperCase(ch);
                if (ch != upch) {
                    if (output == null) {
                        output = new char[count];
                        i2 = o - offset;
                        System.arraycopy(value, offset, output, 0, i2);
                    } else {
                        i2 = i;
                    }
                    i = i2 + 1;
                    output[i2] = upch;
                    i2 = i;
                } else {
                    if (output != null) {
                        i2 = i + 1;
                        output[i] = ch;
                    }
                    i2 = i;
                }
            } else {
                int target = index * 3;
                char val3 = upperValues[target + 2];
                if (output == null) {
                    output = new char[(((count / 6) + count) + 2)];
                    i2 = o - offset;
                    System.arraycopy(value, offset, output, 0, i2);
                } else {
                    int i3 = (val3 == '\u0000' ? 1 : 2) + i;
                    int length = output.length;
                    if (i3 >= r0) {
                        newoutput = new char[((output.length + (count / 6)) + 3)];
                        System.arraycopy(output, 0, newoutput, 0, output.length);
                        output = newoutput;
                        i2 = i;
                    } else {
                        i2 = i;
                    }
                }
                i = i2 + 1;
                output[i2] = upperValues[target];
                i2 = i + 1;
                output[i] = upperValues[target + 1];
                if (val3 != '\u0000') {
                    i = i2 + 1;
                    output[i2] = val3;
                    i2 = i;
                }
            }
            o++;
            i = i2;
        }
        if (output == null) {
            return s;
        }
        String str = (output.length == i || output.length - i < 8) ? new String(0, i, output) : new String(output, 0, i);
        return str;
    }
}
