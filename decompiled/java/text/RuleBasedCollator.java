package java.text;

import libcore.icu.RuleBasedCollatorICU;

public class RuleBasedCollator extends Collator {
    RuleBasedCollator(RuleBasedCollatorICU wrapper) {
        super(wrapper);
    }

    public RuleBasedCollator(String rules) throws ParseException {
        if (rules == null) {
            throw new NullPointerException("rules == null");
        }
        try {
            this.icuColl = new RuleBasedCollatorICU(rules);
        } catch (Exception e) {
            if (e instanceof ParseException) {
                throw ((ParseException) e);
            }
            throw new ParseException(e.getMessage(), -1);
        }
    }

    public CollationElementIterator getCollationElementIterator(CharacterIterator source) {
        if (source != null) {
            return new CollationElementIterator(this.icuColl.getCollationElementIterator(source));
        }
        throw new NullPointerException("source == null");
    }

    public CollationElementIterator getCollationElementIterator(String source) {
        if (source != null) {
            return new CollationElementIterator(this.icuColl.getCollationElementIterator(source));
        }
        throw new NullPointerException("source == null");
    }

    public String getRules() {
        return this.icuColl.getRules();
    }

    public Object clone() {
        return (RuleBasedCollator) super.clone();
    }

    public int compare(String source, String target) {
        if (source == null) {
            throw new NullPointerException("source == null");
        } else if (target != null) {
            return this.icuColl.compare(source, target);
        } else {
            throw new NullPointerException("target == null");
        }
    }

    public CollationKey getCollationKey(String source) {
        return this.icuColl.getCollationKey(source);
    }

    public int hashCode() {
        return this.icuColl.getRules().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof Collator) {
            return super.equals(obj);
        }
        return false;
    }
}
