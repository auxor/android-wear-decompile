package java.text;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;
import libcore.icu.ICU;
import libcore.icu.LocaleData;

public abstract class NumberFormat extends Format {
    public static final int FRACTION_FIELD = 1;
    public static final int INTEGER_FIELD = 0;
    private static final ObjectStreamField[] serialPersistentFields;
    private static final long serialVersionUID = -2308460125733713944L;
    private boolean groupingUsed;
    int maximumFractionDigits;
    int maximumIntegerDigits;
    int minimumFractionDigits;
    int minimumIntegerDigits;
    private boolean parseIntegerOnly;

    public static class Field extends java.text.Format.Field {
        public static final Field CURRENCY;
        public static final Field DECIMAL_SEPARATOR;
        public static final Field EXPONENT;
        public static final Field EXPONENT_SIGN;
        public static final Field EXPONENT_SYMBOL;
        public static final Field FRACTION;
        public static final Field GROUPING_SEPARATOR;
        public static final Field INTEGER;
        public static final Field PERCENT;
        public static final Field PERMILLE;
        public static final Field SIGN;
        private static final long serialVersionUID = 7494728892700160890L;

        static {
            SIGN = new Field("sign");
            INTEGER = new Field("integer");
            FRACTION = new Field("fraction");
            EXPONENT = new Field("exponent");
            EXPONENT_SIGN = new Field("exponent sign");
            EXPONENT_SYMBOL = new Field("exponent symbol");
            DECIMAL_SEPARATOR = new Field("decimal separator");
            GROUPING_SEPARATOR = new Field("grouping separator");
            PERCENT = new Field("percent");
            PERMILLE = new Field("per mille");
            CURRENCY = new Field("currency");
        }

        protected Field(String fieldName) {
            super(fieldName);
        }
    }

    public abstract StringBuffer format(double d, StringBuffer stringBuffer, FieldPosition fieldPosition);

    public abstract StringBuffer format(long j, StringBuffer stringBuffer, FieldPosition fieldPosition);

    public abstract Number parse(String str, ParsePosition parsePosition);

    protected NumberFormat() {
        this.groupingUsed = true;
        this.parseIntegerOnly = false;
        this.maximumIntegerDigits = 40;
        this.minimumIntegerDigits = FRACTION_FIELD;
        this.maximumFractionDigits = 3;
        this.minimumFractionDigits = INTEGER_FIELD;
    }

    public Object clone() {
        return super.clone();
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof NumberFormat)) {
            return false;
        }
        NumberFormat obj = (NumberFormat) object;
        if (this.groupingUsed == obj.groupingUsed && this.parseIntegerOnly == obj.parseIntegerOnly && this.maximumFractionDigits == obj.maximumFractionDigits && this.maximumIntegerDigits == obj.maximumIntegerDigits && this.minimumFractionDigits == obj.minimumFractionDigits && this.minimumIntegerDigits == obj.minimumIntegerDigits) {
            return true;
        }
        return false;
    }

    public final String format(double value) {
        return format(value, new StringBuffer(), new FieldPosition((int) INTEGER_FIELD)).toString();
    }

    public final String format(long value) {
        return format(value, new StringBuffer(), new FieldPosition((int) INTEGER_FIELD)).toString();
    }

    public StringBuffer format(Object object, StringBuffer buffer, FieldPosition field) {
        if ((object instanceof Byte) || (object instanceof Short) || (object instanceof Integer) || (object instanceof Long) || ((object instanceof BigInteger) && ((BigInteger) object).bitLength() < 64)) {
            return format(((Number) object).longValue(), buffer, field);
        }
        if (object instanceof Number) {
            return format(((Number) object).doubleValue(), buffer, field);
        }
        if (object == null) {
            throw new IllegalArgumentException("Can't format null object");
        }
        throw new IllegalArgumentException("Bad class: " + object.getClass());
    }

    public static Locale[] getAvailableLocales() {
        return ICU.getAvailableNumberFormatLocales();
    }

    public Currency getCurrency() {
        throw new UnsupportedOperationException();
    }

    public static final NumberFormat getCurrencyInstance() {
        return getCurrencyInstance(Locale.getDefault());
    }

    public static NumberFormat getCurrencyInstance(Locale locale) {
        if (locale != null) {
            return getInstance(LocaleData.get(locale).currencyPattern, locale);
        }
        throw new NullPointerException("locale == null");
    }

    public static final NumberFormat getIntegerInstance() {
        return getIntegerInstance(Locale.getDefault());
    }

    public static NumberFormat getIntegerInstance(Locale locale) {
        if (locale == null) {
            throw new NullPointerException("locale == null");
        }
        NumberFormat result = getInstance(LocaleData.get(locale).integerPattern, locale);
        result.setParseIntegerOnly(true);
        return result;
    }

    public static final NumberFormat getInstance() {
        return getNumberInstance();
    }

    public static NumberFormat getInstance(Locale locale) {
        return getNumberInstance(locale);
    }

    private static NumberFormat getInstance(String pattern, Locale locale) {
        return new DecimalFormat(pattern, locale);
    }

    public int getMaximumFractionDigits() {
        return this.maximumFractionDigits;
    }

    public int getMaximumIntegerDigits() {
        return this.maximumIntegerDigits;
    }

    public int getMinimumFractionDigits() {
        return this.minimumFractionDigits;
    }

    public int getMinimumIntegerDigits() {
        return this.minimumIntegerDigits;
    }

    public static final NumberFormat getNumberInstance() {
        return getNumberInstance(Locale.getDefault());
    }

    public static NumberFormat getNumberInstance(Locale locale) {
        if (locale != null) {
            return getInstance(LocaleData.get(locale).numberPattern, locale);
        }
        throw new NullPointerException("locale == null");
    }

    public static final NumberFormat getPercentInstance() {
        return getPercentInstance(Locale.getDefault());
    }

    public static NumberFormat getPercentInstance(Locale locale) {
        if (locale != null) {
            return getInstance(LocaleData.get(locale).percentPattern, locale);
        }
        throw new NullPointerException("locale == null");
    }

    public int hashCode() {
        int i = 1231;
        int i2 = this.groupingUsed ? 1231 : 1237;
        if (!this.parseIntegerOnly) {
            i = 1237;
        }
        return ((((i2 + i) + this.maximumFractionDigits) + this.maximumIntegerDigits) + this.minimumFractionDigits) + this.minimumIntegerDigits;
    }

    public boolean isGroupingUsed() {
        return this.groupingUsed;
    }

    public boolean isParseIntegerOnly() {
        return this.parseIntegerOnly;
    }

    public Number parse(String string) throws ParseException {
        ParsePosition pos = new ParsePosition(INTEGER_FIELD);
        Number number = parse(string, pos);
        if (pos.getIndex() != 0) {
            return number;
        }
        throw new ParseException("Unparseable number: \"" + string + "\"", pos.getErrorIndex());
    }

    public final Object parseObject(String string, ParsePosition position) {
        if (position == null) {
            throw new NullPointerException("position == null");
        }
        try {
            return parse(string, position);
        } catch (Exception e) {
            return null;
        }
    }

    public void setCurrency(Currency currency) {
        throw new UnsupportedOperationException();
    }

    public void setGroupingUsed(boolean value) {
        this.groupingUsed = value;
    }

    public void setMaximumFractionDigits(int value) {
        if (value < 0) {
            value = INTEGER_FIELD;
        }
        this.maximumFractionDigits = value;
        if (this.maximumFractionDigits < this.minimumFractionDigits) {
            this.minimumFractionDigits = this.maximumFractionDigits;
        }
    }

    public void setMaximumIntegerDigits(int value) {
        if (value < 0) {
            value = INTEGER_FIELD;
        }
        this.maximumIntegerDigits = value;
        if (this.maximumIntegerDigits < this.minimumIntegerDigits) {
            this.minimumIntegerDigits = this.maximumIntegerDigits;
        }
    }

    public void setMinimumFractionDigits(int value) {
        if (value < 0) {
            value = INTEGER_FIELD;
        }
        this.minimumFractionDigits = value;
        if (this.maximumFractionDigits < this.minimumFractionDigits) {
            this.maximumFractionDigits = this.minimumFractionDigits;
        }
    }

    public void setMinimumIntegerDigits(int value) {
        if (value < 0) {
            value = INTEGER_FIELD;
        }
        this.minimumIntegerDigits = value;
        if (this.maximumIntegerDigits < this.minimumIntegerDigits) {
            this.maximumIntegerDigits = this.minimumIntegerDigits;
        }
    }

    public void setParseIntegerOnly(boolean value) {
        this.parseIntegerOnly = value;
    }

    static {
        serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("groupingUsed", Boolean.TYPE), new ObjectStreamField("maxFractionDigits", Byte.TYPE), new ObjectStreamField("maximumFractionDigits", Integer.TYPE), new ObjectStreamField("maximumIntegerDigits", Integer.TYPE), new ObjectStreamField("maxIntegerDigits", Byte.TYPE), new ObjectStreamField("minFractionDigits", Byte.TYPE), new ObjectStreamField("minimumFractionDigits", Integer.TYPE), new ObjectStreamField("minimumIntegerDigits", Integer.TYPE), new ObjectStreamField("minIntegerDigits", Byte.TYPE), new ObjectStreamField("parseIntegerOnly", Boolean.TYPE), new ObjectStreamField("serialVersionOnStream", Integer.TYPE)};
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        byte b;
        byte b2 = Byte.MAX_VALUE;
        PutField fields = stream.putFields();
        fields.put("groupingUsed", this.groupingUsed);
        fields.put("maxFractionDigits", this.maximumFractionDigits < Float.MAX_EXPONENT ? (byte) this.maximumFractionDigits : Byte.MAX_VALUE);
        fields.put("maximumFractionDigits", this.maximumFractionDigits);
        fields.put("maximumIntegerDigits", this.maximumIntegerDigits);
        String str = "maxIntegerDigits";
        if (this.maximumIntegerDigits < Float.MAX_EXPONENT) {
            b = (byte) this.maximumIntegerDigits;
        } else {
            b = Byte.MAX_VALUE;
        }
        fields.put(str, b);
        str = "minFractionDigits";
        if (this.minimumFractionDigits < Float.MAX_EXPONENT) {
            b = (byte) this.minimumFractionDigits;
        } else {
            b = Byte.MAX_VALUE;
        }
        fields.put(str, b);
        fields.put("minimumFractionDigits", this.minimumFractionDigits);
        fields.put("minimumIntegerDigits", this.minimumIntegerDigits);
        String str2 = "minIntegerDigits";
        if (this.minimumIntegerDigits < Float.MAX_EXPONENT) {
            b2 = (byte) this.minimumIntegerDigits;
        }
        fields.put(str2, b2);
        fields.put("parseIntegerOnly", this.parseIntegerOnly);
        fields.put("serialVersionOnStream", (int) FRACTION_FIELD);
        stream.writeFields();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        GetField fields = stream.readFields();
        this.groupingUsed = fields.get("groupingUsed", true);
        this.parseIntegerOnly = fields.get("parseIntegerOnly", false);
        if (fields.get("serialVersionOnStream", (int) INTEGER_FIELD) == 0) {
            this.maximumFractionDigits = fields.get("maxFractionDigits", (byte) 3);
            this.maximumIntegerDigits = fields.get("maxIntegerDigits", (byte) 40);
            this.minimumFractionDigits = fields.get("minFractionDigits", (byte) 0);
            this.minimumIntegerDigits = fields.get("minIntegerDigits", (byte) 1);
        } else {
            this.maximumFractionDigits = fields.get("maximumFractionDigits", 3);
            this.maximumIntegerDigits = fields.get("maximumIntegerDigits", 40);
            this.minimumFractionDigits = fields.get("minimumFractionDigits", (int) INTEGER_FIELD);
            this.minimumIntegerDigits = fields.get("minimumIntegerDigits", (int) FRACTION_FIELD);
        }
        if (this.minimumIntegerDigits > this.maximumIntegerDigits || this.minimumFractionDigits > this.maximumFractionDigits) {
            throw new InvalidObjectException("min digits greater than max digits");
        } else if (this.minimumIntegerDigits < 0 || this.maximumIntegerDigits < 0 || this.minimumFractionDigits < 0 || this.maximumFractionDigits < 0) {
            throw new InvalidObjectException("min or max digits negative");
        }
    }

    public RoundingMode getRoundingMode() {
        throw new UnsupportedOperationException();
    }

    public void setRoundingMode(RoundingMode roundingMode) {
        throw new UnsupportedOperationException();
    }
}
