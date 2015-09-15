package java.text;

import java.io.Serializable;
import java.text.AttributedCharacterIterator.Attribute;

public abstract class Format implements Serializable, Cloneable {
    private static final long serialVersionUID = -299282585814624189L;

    public static class Field extends Attribute {
        private static final long serialVersionUID = 276966692217360283L;

        protected Field(String fieldName) {
            super(fieldName);
        }
    }

    public abstract StringBuffer format(Object obj, StringBuffer stringBuffer, FieldPosition fieldPosition);

    public abstract Object parseObject(String str, ParsePosition parsePosition);

    protected Format() {
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (Object e) {
            throw new AssertionError(e);
        }
    }

    public final String format(Object object) {
        return format(object, new StringBuffer(), new FieldPosition(0)).toString();
    }

    public AttributedCharacterIterator formatToCharacterIterator(Object object) {
        return new AttributedString(format(object)).getIterator();
    }

    public Object parseObject(String string) throws ParseException {
        ParsePosition position = new ParsePosition(0);
        Object result = parseObject(string, position);
        if (position.getIndex() != 0) {
            return result;
        }
        throw new ParseException("Parse failure", position.getErrorIndex());
    }

    static boolean upTo(String string, ParsePosition position, StringBuffer buffer, char stop) {
        int index = position.getIndex();
        int length = string.length();
        boolean lastQuote = false;
        boolean quote = false;
        int index2 = index;
        while (index2 < length) {
            index = index2 + 1;
            char ch = string.charAt(index2);
            if (ch == '\'') {
                if (lastQuote) {
                    buffer.append('\'');
                }
                if (quote) {
                    quote = false;
                } else {
                    quote = true;
                }
                lastQuote = true;
            } else if (ch != stop || quote) {
                lastQuote = false;
                buffer.append(ch);
            } else {
                position.setIndex(index);
                return true;
            }
            index2 = index;
        }
        position.setIndex(index2);
        index = index2;
        return false;
    }

    static boolean upToWithQuotes(String string, ParsePosition position, StringBuffer buffer, char stop, char start) {
        int index = position.getIndex();
        int length = string.length();
        int count = 1;
        boolean quote = false;
        int index2 = index;
        while (index2 < length) {
            index = index2 + 1;
            char ch = string.charAt(index2);
            if (ch == '\'') {
                quote = !quote;
            }
            if (!quote) {
                if (ch == stop) {
                    count--;
                }
                if (count == 0) {
                    position.setIndex(index);
                    return true;
                } else if (ch == start) {
                    count++;
                }
            }
            buffer.append(ch);
            index2 = index;
        }
        throw new IllegalArgumentException("Unmatched braces in the pattern");
    }
}
