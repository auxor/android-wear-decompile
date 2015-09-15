package libcore.icu;

import java.text.CharacterIterator;
import java.text.CollationKey;
import java.text.ParseException;
import java.util.Locale;

public final class RuleBasedCollatorICU implements Cloneable {
    public static final int ALTERNATE_HANDLING = 1;
    public static final int CASE_FIRST = 2;
    public static final int CASE_LEVEL = 3;
    public static final int DECOMPOSITION_MODE = 4;
    public static final int FRENCH_COLLATION = 0;
    public static final int STRENGTH = 5;
    public static final int VALUE_ATTRIBUTE_VALUE_COUNT = 29;
    public static final int VALUE_DEFAULT = -1;
    public static final int VALUE_DEFAULT_STRENGTH = 2;
    public static final int VALUE_IDENTICAL = 15;
    public static final int VALUE_LOWER_FIRST = 24;
    public static final int VALUE_NON_IGNORABLE = 21;
    public static final int VALUE_OFF = 16;
    public static final int VALUE_ON = 17;
    public static final int VALUE_ON_WITHOUT_HANGUL = 28;
    public static final int VALUE_PRIMARY = 0;
    public static final int VALUE_QUATERNARY = 3;
    public static final int VALUE_SECONDARY = 1;
    public static final int VALUE_SHIFTED = 20;
    public static final int VALUE_TERTIARY = 2;
    public static final int VALUE_UPPER_FIRST = 25;
    private final long address;

    public RuleBasedCollatorICU(String rules) throws ParseException {
        if (rules == null) {
            throw new NullPointerException("rules == null");
        }
        this.address = NativeCollation.openCollatorFromRules(rules, VALUE_OFF, VALUE_TERTIARY);
    }

    public RuleBasedCollatorICU(Locale locale) {
        this.address = NativeCollation.openCollator(locale);
    }

    private RuleBasedCollatorICU(long address) {
        this.address = address;
    }

    public Object clone() {
        return new RuleBasedCollatorICU(NativeCollation.safeClone(this.address));
    }

    public int compare(String source, String target) {
        return NativeCollation.compare(this.address, source, target);
    }

    public int getDecomposition() {
        return NativeCollation.getAttribute(this.address, DECOMPOSITION_MODE);
    }

    public void setDecomposition(int mode) {
        NativeCollation.setAttribute(this.address, DECOMPOSITION_MODE, mode);
    }

    public int getStrength() {
        return NativeCollation.getAttribute(this.address, STRENGTH);
    }

    public void setStrength(int strength) {
        NativeCollation.setAttribute(this.address, STRENGTH, strength);
    }

    public void setAttribute(int type, int value) {
        NativeCollation.setAttribute(this.address, type, value);
    }

    public int getAttribute(int type) {
        return NativeCollation.getAttribute(this.address, type);
    }

    public CollationKey getCollationKey(String source) {
        if (source == null) {
            return null;
        }
        byte[] key = NativeCollation.getSortKey(this.address, source);
        if (key != null) {
            return new CollationKeyICU(source, key);
        }
        return null;
    }

    public String getRules() {
        return NativeCollation.getRules(this.address);
    }

    public CollationElementIteratorICU getCollationElementIterator(String source) {
        return CollationElementIteratorICU.getInstance(this.address, source);
    }

    public CollationElementIteratorICU getCollationElementIterator(CharacterIterator it) {
        return getCollationElementIterator(characterIteratorToString(it));
    }

    private String characterIteratorToString(CharacterIterator it) {
        StringBuilder result = new StringBuilder();
        char ch = it.current();
        while (ch != CharacterIterator.DONE) {
            result.append(ch);
            ch = it.next();
        }
        return result.toString();
    }

    public int hashCode() {
        return 42;
    }

    public boolean equals(String source, String target) {
        return compare(source, target) == 0;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof RuleBasedCollatorICU)) {
            return false;
        }
        RuleBasedCollatorICU rhs = (RuleBasedCollatorICU) object;
        if (getRules().equals(rhs.getRules()) && getStrength() == rhs.getStrength() && getDecomposition() == rhs.getDecomposition()) {
            return true;
        }
        return false;
    }

    protected void finalize() throws Throwable {
        try {
            NativeCollation.closeCollator(this.address);
        } finally {
            super.finalize();
        }
    }
}
