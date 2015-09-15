package java.text;

import java.util.Comparator;
import java.util.Locale;
import libcore.icu.ICU;
import libcore.icu.RuleBasedCollatorICU;
import org.apache.harmony.security.asn1.ASN1UTCTime;
import org.w3c.dom.traversal.NodeFilter;

public abstract class Collator implements Comparator<Object>, Cloneable {
    public static final int CANONICAL_DECOMPOSITION = 1;
    public static final int FULL_DECOMPOSITION = 2;
    public static final int IDENTICAL = 3;
    public static final int NO_DECOMPOSITION = 0;
    public static final int PRIMARY = 0;
    public static final int SECONDARY = 1;
    public static final int TERTIARY = 2;
    RuleBasedCollatorICU icuColl;

    public abstract int compare(String str, String str2);

    public abstract CollationKey getCollationKey(String str);

    public abstract int hashCode();

    Collator(RuleBasedCollatorICU icuColl) {
        this.icuColl = icuColl;
    }

    protected Collator() {
        this.icuColl = new RuleBasedCollatorICU(Locale.getDefault());
    }

    public Object clone() {
        try {
            Collator clone = (Collator) super.clone();
            clone.icuColl = (RuleBasedCollatorICU) this.icuColl.clone();
            return clone;
        } catch (Object e) {
            throw new AssertionError(e);
        }
    }

    public int compare(Object object1, Object object2) {
        return compare((String) object1, (String) object2);
    }

    public boolean equals(Object object) {
        if (!(object instanceof Collator)) {
            return false;
        }
        Collator collator = (Collator) object;
        if (this.icuColl != null) {
            return this.icuColl.equals(collator.icuColl);
        }
        if (collator.icuColl == null) {
            return true;
        }
        return false;
    }

    public boolean equals(String string1, String string2) {
        return compare(string1, string2) == 0;
    }

    public static Locale[] getAvailableLocales() {
        return ICU.getAvailableCollatorLocales();
    }

    public int getDecomposition() {
        return decompositionMode_ICU_Java(this.icuColl.getDecomposition());
    }

    public static Collator getInstance() {
        return getInstance(Locale.getDefault());
    }

    public static Collator getInstance(Locale locale) {
        if (locale != null) {
            return new RuleBasedCollator(new RuleBasedCollatorICU(locale));
        }
        throw new NullPointerException("locale == null");
    }

    public int getStrength() {
        return strength_ICU_Java(this.icuColl.getStrength());
    }

    public void setDecomposition(int value) {
        this.icuColl.setDecomposition(decompositionMode_Java_ICU(value));
    }

    public void setStrength(int value) {
        this.icuColl.setStrength(strength_Java_ICU(value));
    }

    private int decompositionMode_Java_ICU(int mode) {
        switch (mode) {
            case PRIMARY /*0*/:
                return 16;
            case SECONDARY /*1*/:
                return 17;
            default:
                throw new IllegalArgumentException("Bad mode: " + mode);
        }
    }

    private int decompositionMode_ICU_Java(int mode) {
        int javaMode = mode;
        switch (mode) {
            case NodeFilter.SHOW_ENTITY_REFERENCE /*16*/:
                return PRIMARY;
            case ASN1UTCTime.UTC_LOCAL_HMS /*17*/:
                return SECONDARY;
            default:
                return javaMode;
        }
    }

    private int strength_Java_ICU(int value) {
        switch (value) {
            case PRIMARY /*0*/:
                return PRIMARY;
            case SECONDARY /*1*/:
                return SECONDARY;
            case TERTIARY /*2*/:
                return TERTIARY;
            case IDENTICAL /*3*/:
                return 15;
            default:
                throw new IllegalArgumentException("Bad strength: " + value);
        }
    }

    private int strength_ICU_Java(int value) {
        int javaValue = value;
        switch (value) {
            case PRIMARY /*0*/:
                return PRIMARY;
            case SECONDARY /*1*/:
                return SECONDARY;
            case TERTIARY /*2*/:
                return TERTIARY;
            case ASN1UTCTime.UTC_LOCAL_HM /*15*/:
                return IDENTICAL;
            default:
                return javaValue;
        }
    }
}
