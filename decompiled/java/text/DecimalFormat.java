package java.text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;
import libcore.icu.LocaleData;
import libcore.icu.NativeDecimalFormat;
import org.xmlpull.v1.XmlPullParser;

public class DecimalFormat extends NumberFormat {
    private static final Double NEGATIVE_ZERO_DOUBLE;
    private static final ObjectStreamField[] serialPersistentFields;
    private static final long serialVersionUID = 864413376551465018L;
    private transient NativeDecimalFormat ndf;
    private transient RoundingMode roundingMode;
    private transient DecimalFormatSymbols symbols;

    public DecimalFormat() {
        this.roundingMode = RoundingMode.HALF_EVEN;
        Locale locale = Locale.getDefault();
        this.symbols = new DecimalFormatSymbols(locale);
        initNative(LocaleData.get(locale).numberPattern);
    }

    public DecimalFormat(String pattern) {
        this(pattern, Locale.getDefault());
    }

    public DecimalFormat(String pattern, DecimalFormatSymbols value) {
        this.roundingMode = RoundingMode.HALF_EVEN;
        this.symbols = (DecimalFormatSymbols) value.clone();
        initNative(pattern);
    }

    DecimalFormat(String pattern, Locale locale) {
        this.roundingMode = RoundingMode.HALF_EVEN;
        this.symbols = new DecimalFormatSymbols(locale);
        initNative(pattern);
    }

    private void initNative(String pattern) {
        try {
            this.ndf = new NativeDecimalFormat(pattern, this.symbols);
            super.setMaximumFractionDigits(this.ndf.getMaximumFractionDigits());
            super.setMaximumIntegerDigits(this.ndf.getMaximumIntegerDigits());
            super.setMinimumFractionDigits(this.ndf.getMinimumFractionDigits());
            super.setMinimumIntegerDigits(this.ndf.getMinimumIntegerDigits());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(pattern);
        }
    }

    public void applyLocalizedPattern(String pattern) {
        this.ndf.applyLocalizedPattern(pattern);
        updateFieldsFromNative();
    }

    public void applyPattern(String pattern) {
        this.ndf.applyPattern(pattern);
        updateFieldsFromNative();
    }

    private void updateFieldsFromNative() {
        this.maximumIntegerDigits = this.ndf.getMaximumIntegerDigits();
        this.minimumIntegerDigits = this.ndf.getMinimumIntegerDigits();
        this.maximumFractionDigits = this.ndf.getMaximumFractionDigits();
        this.minimumFractionDigits = this.ndf.getMinimumFractionDigits();
    }

    public Object clone() {
        DecimalFormat clone = (DecimalFormat) super.clone();
        clone.ndf = (NativeDecimalFormat) this.ndf.clone();
        clone.symbols = (DecimalFormatSymbols) this.symbols.clone();
        return clone;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r6) {
        /*
        r5 = this;
        r1 = 1;
        r2 = 0;
        if (r5 != r6) goto L_0x0005;
    L_0x0004:
        return r1;
    L_0x0005:
        r3 = r6 instanceof java.text.DecimalFormat;
        if (r3 != 0) goto L_0x000b;
    L_0x0009:
        r1 = r2;
        goto L_0x0004;
    L_0x000b:
        r0 = r6;
        r0 = (java.text.DecimalFormat) r0;
        r3 = r5.ndf;
        if (r3 != 0) goto L_0x0026;
    L_0x0012:
        r3 = r0.ndf;
        if (r3 != 0) goto L_0x0024;
    L_0x0016:
        r3 = r5.getDecimalFormatSymbols();
        r4 = r0.getDecimalFormatSymbols();
        r3 = r3.equals(r4);
        if (r3 != 0) goto L_0x0004;
    L_0x0024:
        r1 = r2;
        goto L_0x0004;
    L_0x0026:
        r3 = r5.ndf;
        r4 = r0.ndf;
        r3 = r3.equals(r4);
        if (r3 == 0) goto L_0x0024;
    L_0x0030:
        goto L_0x0016;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.text.DecimalFormat.equals(java.lang.Object):boolean");
    }

    public AttributedCharacterIterator formatToCharacterIterator(Object object) {
        if (object != null) {
            return this.ndf.formatToCharacterIterator(object);
        }
        throw new NullPointerException("object == null");
    }

    private void checkBufferAndFieldPosition(StringBuffer buffer, FieldPosition position) {
        if (buffer == null) {
            throw new NullPointerException("buffer == null");
        } else if (position == null) {
            throw new NullPointerException("position == null");
        }
    }

    public StringBuffer format(double value, StringBuffer buffer, FieldPosition position) {
        checkBufferAndFieldPosition(buffer, position);
        buffer.append(this.ndf.formatDouble(value, position));
        return buffer;
    }

    public StringBuffer format(long value, StringBuffer buffer, FieldPosition position) {
        checkBufferAndFieldPosition(buffer, position);
        buffer.append(this.ndf.formatLong(value, position));
        return buffer;
    }

    public final StringBuffer format(Object number, StringBuffer buffer, FieldPosition position) {
        checkBufferAndFieldPosition(buffer, position);
        if (number instanceof BigInteger) {
            BigInteger bigInteger = (BigInteger) number;
            buffer.append(bigInteger.bitLength() < 64 ? this.ndf.formatLong(bigInteger.longValue(), position) : this.ndf.formatBigInteger(bigInteger, position));
            return buffer;
        } else if (!(number instanceof BigDecimal)) {
            return super.format(number, buffer, position);
        } else {
            buffer.append(this.ndf.formatBigDecimal((BigDecimal) number, position));
            return buffer;
        }
    }

    public DecimalFormatSymbols getDecimalFormatSymbols() {
        return (DecimalFormatSymbols) this.symbols.clone();
    }

    public Currency getCurrency() {
        return this.symbols.getCurrency();
    }

    public int getGroupingSize() {
        return this.ndf.getGroupingSize();
    }

    public String getNegativePrefix() {
        return this.ndf.getNegativePrefix();
    }

    public String getNegativeSuffix() {
        return this.ndf.getNegativeSuffix();
    }

    public String getPositivePrefix() {
        return this.ndf.getPositivePrefix();
    }

    public String getPositiveSuffix() {
        return this.ndf.getPositiveSuffix();
    }

    public int hashCode() {
        return getPositivePrefix().hashCode();
    }

    public boolean isDecimalSeparatorAlwaysShown() {
        return this.ndf.isDecimalSeparatorAlwaysShown();
    }

    public boolean isParseBigDecimal() {
        return this.ndf.isParseBigDecimal();
    }

    public void setParseIntegerOnly(boolean value) {
        super.setParseIntegerOnly(value);
        this.ndf.setParseIntegerOnly(value);
    }

    public boolean isParseIntegerOnly() {
        return this.ndf.isParseIntegerOnly();
    }

    static {
        NEGATIVE_ZERO_DOUBLE = new Double(-0.0d);
        serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("positivePrefix", String.class), new ObjectStreamField("positiveSuffix", String.class), new ObjectStreamField("negativePrefix", String.class), new ObjectStreamField("negativeSuffix", String.class), new ObjectStreamField("posPrefixPattern", String.class), new ObjectStreamField("posSuffixPattern", String.class), new ObjectStreamField("negPrefixPattern", String.class), new ObjectStreamField("negSuffixPattern", String.class), new ObjectStreamField("multiplier", Integer.TYPE), new ObjectStreamField("groupingSize", Byte.TYPE), new ObjectStreamField("groupingUsed", Boolean.TYPE), new ObjectStreamField("decimalSeparatorAlwaysShown", Boolean.TYPE), new ObjectStreamField("parseBigDecimal", Boolean.TYPE), new ObjectStreamField("roundingMode", RoundingMode.class), new ObjectStreamField("symbols", DecimalFormatSymbols.class), new ObjectStreamField("useExponentialNotation", Boolean.TYPE), new ObjectStreamField("minExponentDigits", Byte.TYPE), new ObjectStreamField("maximumIntegerDigits", Integer.TYPE), new ObjectStreamField("minimumIntegerDigits", Integer.TYPE), new ObjectStreamField("maximumFractionDigits", Integer.TYPE), new ObjectStreamField("minimumFractionDigits", Integer.TYPE), new ObjectStreamField("serialVersionOnStream", Integer.TYPE)};
    }

    public Number parse(String string, ParsePosition position) {
        Number number = this.ndf.parse(string, position);
        if (number == null) {
            return null;
        }
        if (isParseBigDecimal()) {
            if (number instanceof Long) {
                return new BigDecimal(number.longValue());
            }
            if ((number instanceof Double) && !((Double) number).isInfinite() && !((Double) number).isNaN()) {
                return new BigDecimal(number.toString());
            }
            if (number instanceof BigInteger) {
                return new BigDecimal(number.toString());
            }
            return number;
        } else if ((number instanceof BigDecimal) || (number instanceof BigInteger)) {
            return new Double(number.doubleValue());
        } else {
            if (isParseIntegerOnly() && number.equals(NEGATIVE_ZERO_DOUBLE)) {
                return Long.valueOf(0);
            }
            return number;
        }
    }

    public void setDecimalFormatSymbols(DecimalFormatSymbols value) {
        if (value != null) {
            this.symbols = (DecimalFormatSymbols) value.clone();
            this.ndf.setDecimalFormatSymbols(this.symbols);
        }
    }

    public void setCurrency(Currency currency) {
        this.symbols.setCurrency(Currency.getInstance(currency.getCurrencyCode()));
        this.ndf.setCurrency(this.symbols.getCurrencySymbol(), currency.getCurrencyCode());
    }

    public void setDecimalSeparatorAlwaysShown(boolean value) {
        this.ndf.setDecimalSeparatorAlwaysShown(value);
    }

    public void setGroupingSize(int value) {
        this.ndf.setGroupingSize(value);
    }

    public void setGroupingUsed(boolean value) {
        this.ndf.setGroupingUsed(value);
    }

    public boolean isGroupingUsed() {
        return this.ndf.isGroupingUsed();
    }

    public void setMaximumFractionDigits(int value) {
        super.setMaximumFractionDigits(value);
        this.ndf.setMaximumFractionDigits(getMaximumFractionDigits());
        setRoundingMode(this.roundingMode);
    }

    public void setMaximumIntegerDigits(int value) {
        super.setMaximumIntegerDigits(value);
        this.ndf.setMaximumIntegerDigits(getMaximumIntegerDigits());
    }

    public void setMinimumFractionDigits(int value) {
        super.setMinimumFractionDigits(value);
        this.ndf.setMinimumFractionDigits(getMinimumFractionDigits());
    }

    public void setMinimumIntegerDigits(int value) {
        super.setMinimumIntegerDigits(value);
        this.ndf.setMinimumIntegerDigits(getMinimumIntegerDigits());
    }

    public int getMultiplier() {
        return this.ndf.getMultiplier();
    }

    public void setMultiplier(int value) {
        this.ndf.setMultiplier(value);
    }

    public void setNegativePrefix(String value) {
        this.ndf.setNegativePrefix(value);
    }

    public void setNegativeSuffix(String value) {
        this.ndf.setNegativeSuffix(value);
    }

    public void setPositivePrefix(String value) {
        this.ndf.setPositivePrefix(value);
    }

    public void setPositiveSuffix(String value) {
        this.ndf.setPositiveSuffix(value);
    }

    public void setParseBigDecimal(boolean newValue) {
        this.ndf.setParseBigDecimal(newValue);
    }

    public String toLocalizedPattern() {
        return this.ndf.toLocalizedPattern();
    }

    public String toPattern() {
        return this.ndf.toPattern();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException, ClassNotFoundException {
        PutField fields = stream.putFields();
        fields.put("positivePrefix", this.ndf.getPositivePrefix());
        fields.put("positiveSuffix", this.ndf.getPositiveSuffix());
        fields.put("negativePrefix", this.ndf.getNegativePrefix());
        fields.put("negativeSuffix", this.ndf.getNegativeSuffix());
        fields.put("posPrefixPattern", (String) null);
        fields.put("posSuffixPattern", (String) null);
        fields.put("negPrefixPattern", (String) null);
        fields.put("negSuffixPattern", (String) null);
        fields.put("multiplier", this.ndf.getMultiplier());
        fields.put("groupingSize", (byte) this.ndf.getGroupingSize());
        fields.put("groupingUsed", this.ndf.isGroupingUsed());
        fields.put("decimalSeparatorAlwaysShown", this.ndf.isDecimalSeparatorAlwaysShown());
        fields.put("parseBigDecimal", this.ndf.isParseBigDecimal());
        fields.put("roundingMode", this.roundingMode);
        fields.put("symbols", this.symbols);
        fields.put("useExponentialNotation", false);
        fields.put("minExponentDigits", (byte) 0);
        fields.put("maximumIntegerDigits", this.ndf.getMaximumIntegerDigits());
        fields.put("minimumIntegerDigits", this.ndf.getMinimumIntegerDigits());
        fields.put("maximumFractionDigits", this.ndf.getMaximumFractionDigits());
        fields.put("minimumFractionDigits", this.ndf.getMinimumFractionDigits());
        fields.put("serialVersionOnStream", 4);
        stream.writeFields();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        GetField fields = stream.readFields();
        this.symbols = (DecimalFormatSymbols) fields.get("symbols", null);
        initNative(XmlPullParser.NO_NAMESPACE);
        this.ndf.setPositivePrefix((String) fields.get("positivePrefix", XmlPullParser.NO_NAMESPACE));
        this.ndf.setPositiveSuffix((String) fields.get("positiveSuffix", XmlPullParser.NO_NAMESPACE));
        this.ndf.setNegativePrefix((String) fields.get("negativePrefix", (Object) "-"));
        this.ndf.setNegativeSuffix((String) fields.get("negativeSuffix", XmlPullParser.NO_NAMESPACE));
        this.ndf.setMultiplier(fields.get("multiplier", 1));
        this.ndf.setGroupingSize(fields.get("groupingSize", (byte) 3));
        this.ndf.setGroupingUsed(fields.get("groupingUsed", true));
        this.ndf.setDecimalSeparatorAlwaysShown(fields.get("decimalSeparatorAlwaysShown", false));
        setRoundingMode((RoundingMode) fields.get("roundingMode", RoundingMode.HALF_EVEN));
        int maximumIntegerDigits = fields.get("maximumIntegerDigits", 309);
        int minimumIntegerDigits = fields.get("minimumIntegerDigits", 309);
        int maximumFractionDigits = fields.get("maximumFractionDigits", 340);
        int minimumFractionDigits = fields.get("minimumFractionDigits", 340);
        this.ndf.setMaximumIntegerDigits(maximumIntegerDigits);
        super.setMaximumIntegerDigits(this.ndf.getMaximumIntegerDigits());
        setMinimumIntegerDigits(minimumIntegerDigits);
        setMinimumFractionDigits(minimumFractionDigits);
        setMaximumFractionDigits(maximumFractionDigits);
        setParseBigDecimal(fields.get("parseBigDecimal", false));
        if (fields.get("serialVersionOnStream", 0) < 3) {
            setMaximumIntegerDigits(super.getMaximumIntegerDigits());
            setMinimumIntegerDigits(super.getMinimumIntegerDigits());
            setMaximumFractionDigits(super.getMaximumFractionDigits());
            setMinimumFractionDigits(super.getMinimumFractionDigits());
        }
    }

    public RoundingMode getRoundingMode() {
        return this.roundingMode;
    }

    public void setRoundingMode(RoundingMode roundingMode) {
        if (roundingMode == null) {
            throw new NullPointerException("roundingMode == null");
        }
        this.roundingMode = roundingMode;
        this.ndf.setRoundingMode(roundingMode, 0.0d);
    }

    public String toString() {
        return this.ndf.toString();
    }
}
