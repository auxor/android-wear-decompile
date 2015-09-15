package libcore.icu;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.Format.Field;
import java.text.NumberFormat;
import java.text.ParsePosition;

public final class NativeDecimalFormat implements Cloneable {
    private static final Field[] ICU4C_FIELD_IDS;
    private static final int UNUM_CURRENCY_CODE = 5;
    private static final int UNUM_CURRENCY_SYMBOL = 8;
    private static final int UNUM_DECIMAL_ALWAYS_SHOWN = 2;
    private static final int UNUM_DECIMAL_SEPARATOR_SYMBOL = 0;
    private static final int UNUM_DEFAULT_RULESET = 6;
    private static final int UNUM_DIGIT_SYMBOL = 5;
    private static final int UNUM_EXPONENTIAL_SYMBOL = 11;
    private static final int UNUM_FORMAT_SYMBOL_COUNT = 18;
    private static final int UNUM_FORMAT_WIDTH = 13;
    private static final int UNUM_FRACTION_DIGITS = 8;
    private static final int UNUM_GROUPING_SEPARATOR_SYMBOL = 1;
    private static final int UNUM_GROUPING_SIZE = 10;
    private static final int UNUM_GROUPING_USED = 1;
    private static final int UNUM_INFINITY_SYMBOL = 14;
    private static final int UNUM_INTEGER_DIGITS = 5;
    private static final int UNUM_INTL_CURRENCY_SYMBOL = 9;
    private static final int UNUM_LENIENT_PARSE = 19;
    private static final int UNUM_MAX_FRACTION_DIGITS = 6;
    private static final int UNUM_MAX_INTEGER_DIGITS = 3;
    private static final int UNUM_MAX_SIGNIFICANT_DIGITS = 18;
    private static final int UNUM_MINUS_SIGN_SYMBOL = 6;
    private static final int UNUM_MIN_FRACTION_DIGITS = 7;
    private static final int UNUM_MIN_INTEGER_DIGITS = 4;
    private static final int UNUM_MIN_SIGNIFICANT_DIGITS = 17;
    private static final int UNUM_MONETARY_GROUPING_SEPARATOR_SYMBOL = 17;
    private static final int UNUM_MONETARY_SEPARATOR_SYMBOL = 10;
    private static final int UNUM_MULTIPLIER = 9;
    private static final int UNUM_NAN_SYMBOL = 15;
    private static final int UNUM_NEGATIVE_PREFIX = 2;
    private static final int UNUM_NEGATIVE_SUFFIX = 3;
    private static final int UNUM_PADDING_CHARACTER = 4;
    private static final int UNUM_PADDING_POSITION = 14;
    private static final int UNUM_PAD_ESCAPE_SYMBOL = 13;
    private static final int UNUM_PARSE_INT_ONLY = 0;
    private static final int UNUM_PATTERN_SEPARATOR_SYMBOL = 2;
    private static final int UNUM_PERCENT_SYMBOL = 3;
    private static final int UNUM_PERMILL_SYMBOL = 12;
    private static final int UNUM_PLUS_SIGN_SYMBOL = 7;
    private static final int UNUM_POSITIVE_PREFIX = 0;
    private static final int UNUM_POSITIVE_SUFFIX = 1;
    private static final int UNUM_PUBLIC_RULESETS = 7;
    private static final int UNUM_ROUNDING_INCREMENT = 12;
    private static final int UNUM_ROUNDING_MODE = 11;
    private static final int UNUM_SECONDARY_GROUPING_SIZE = 15;
    private static final int UNUM_SIGNIFICANT_DIGITS_USED = 16;
    private static final int UNUM_SIGNIFICANT_DIGIT_SYMBOL = 16;
    private static final int UNUM_ZERO_DIGIT_SYMBOL = 4;
    private long address;
    private String lastPattern;
    private boolean negPrefNull;
    private boolean negSuffNull;
    private transient boolean parseBigDecimal;
    private boolean posPrefNull;
    private boolean posSuffNull;

    /* renamed from: libcore.icu.NativeDecimalFormat.1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$math$RoundingMode;

        static {
            $SwitchMap$java$math$RoundingMode = new int[RoundingMode.values().length];
            try {
                $SwitchMap$java$math$RoundingMode[RoundingMode.CEILING.ordinal()] = NativeDecimalFormat.UNUM_POSITIVE_SUFFIX;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$math$RoundingMode[RoundingMode.FLOOR.ordinal()] = NativeDecimalFormat.UNUM_PATTERN_SEPARATOR_SYMBOL;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$math$RoundingMode[RoundingMode.DOWN.ordinal()] = NativeDecimalFormat.UNUM_PERCENT_SYMBOL;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$java$math$RoundingMode[RoundingMode.UP.ordinal()] = NativeDecimalFormat.UNUM_ZERO_DIGIT_SYMBOL;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$java$math$RoundingMode[RoundingMode.HALF_EVEN.ordinal()] = NativeDecimalFormat.UNUM_INTEGER_DIGITS;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$java$math$RoundingMode[RoundingMode.HALF_DOWN.ordinal()] = NativeDecimalFormat.UNUM_MINUS_SIGN_SYMBOL;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$java$math$RoundingMode[RoundingMode.HALF_UP.ordinal()] = NativeDecimalFormat.UNUM_PUBLIC_RULESETS;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$java$math$RoundingMode[RoundingMode.UNNECESSARY.ordinal()] = NativeDecimalFormat.UNUM_FRACTION_DIGITS;
            } catch (NoSuchFieldError e8) {
            }
        }
    }

    private static class FieldPositionIterator {
        private int[] data;
        private int pos;

        private FieldPositionIterator() {
            this.pos = -3;
        }

        public static FieldPositionIterator forFieldPosition(FieldPosition fp) {
            return fp != null ? new FieldPositionIterator() : null;
        }

        public boolean next() {
            if (this.data == null) {
                return false;
            }
            this.pos += NativeDecimalFormat.UNUM_PERCENT_SYMBOL;
            if (this.pos < this.data.length) {
                return true;
            }
            return false;
        }

        public int fieldId() {
            return this.data[this.pos];
        }

        public Field field() {
            return NativeDecimalFormat.ICU4C_FIELD_IDS[this.data[this.pos]];
        }

        public int start() {
            return this.data[this.pos + NativeDecimalFormat.UNUM_POSITIVE_SUFFIX];
        }

        public int limit() {
            return this.data[this.pos + NativeDecimalFormat.UNUM_PATTERN_SEPARATOR_SYMBOL];
        }

        private void setData(int[] data) {
            this.data = data;
            this.pos = -3;
        }
    }

    private static native void applyPatternImpl(long j, boolean z, String str);

    private static native long cloneImpl(long j);

    private static native void close(long j);

    private static native char[] formatDigitList(long j, String str, FieldPositionIterator fieldPositionIterator);

    private static native char[] formatDouble(long j, double d, FieldPositionIterator fieldPositionIterator);

    private static native char[] formatLong(long j, long j2, FieldPositionIterator fieldPositionIterator);

    private static native int getAttribute(long j, int i);

    private static native String getTextAttribute(long j, int i);

    private static native long open(String str, String str2, char c, char c2, String str3, char c3, String str4, String str5, String str6, char c4, String str7, char c5, String str8, char c6, char c7);

    private static native Number parse(long j, String str, ParsePosition parsePosition, boolean z);

    private static native void setAttribute(long j, int i, int i2);

    private static native void setDecimalFormatSymbols(long j, String str, char c, char c2, String str2, char c3, String str3, String str4, String str5, char c4, String str6, char c5, String str7, char c6, char c7);

    private static native void setRoundingMode(long j, int i, double d);

    private static native void setSymbol(long j, int i, String str);

    private static native void setTextAttribute(long j, int i, String str);

    private static native String toPatternImpl(long j, boolean z);

    static {
        Field[] fieldArr = new Field[UNUM_ROUNDING_MODE];
        fieldArr[UNUM_POSITIVE_PREFIX] = NumberFormat.Field.INTEGER;
        fieldArr[UNUM_POSITIVE_SUFFIX] = NumberFormat.Field.FRACTION;
        fieldArr[UNUM_PATTERN_SEPARATOR_SYMBOL] = NumberFormat.Field.DECIMAL_SEPARATOR;
        fieldArr[UNUM_PERCENT_SYMBOL] = NumberFormat.Field.EXPONENT_SYMBOL;
        fieldArr[UNUM_ZERO_DIGIT_SYMBOL] = NumberFormat.Field.EXPONENT_SIGN;
        fieldArr[UNUM_INTEGER_DIGITS] = NumberFormat.Field.EXPONENT;
        fieldArr[UNUM_MINUS_SIGN_SYMBOL] = NumberFormat.Field.GROUPING_SEPARATOR;
        fieldArr[UNUM_PUBLIC_RULESETS] = NumberFormat.Field.CURRENCY;
        fieldArr[UNUM_FRACTION_DIGITS] = NumberFormat.Field.PERCENT;
        fieldArr[UNUM_MULTIPLIER] = NumberFormat.Field.PERMILLE;
        fieldArr[UNUM_MONETARY_SEPARATOR_SYMBOL] = NumberFormat.Field.SIGN;
        ICU4C_FIELD_IDS = fieldArr;
    }

    private static int translateFieldId(FieldPosition fp) {
        int id = fp.getField();
        if (id < -1 || id > UNUM_POSITIVE_SUFFIX) {
            id = -1;
        }
        if (id != -1) {
            return id;
        }
        Field attr = fp.getFieldAttribute();
        if (attr == null) {
            return id;
        }
        for (int i = UNUM_POSITIVE_PREFIX; i < ICU4C_FIELD_IDS.length; i += UNUM_POSITIVE_SUFFIX) {
            if (ICU4C_FIELD_IDS[i].equals(attr)) {
                return i;
            }
        }
        return id;
    }

    public NativeDecimalFormat(String pattern, DecimalFormatSymbols dfs) {
        try {
            this.address = open(pattern, dfs.getCurrencySymbol(), dfs.getDecimalSeparator(), dfs.getDigit(), dfs.getExponentSeparator(), dfs.getGroupingSeparator(), dfs.getInfinity(), dfs.getInternationalCurrencySymbol(), dfs.getMinusSignString(), dfs.getMonetaryDecimalSeparator(), dfs.getNaN(), dfs.getPatternSeparator(), dfs.getPercentString(), dfs.getPerMill(), dfs.getZeroDigit());
            this.lastPattern = pattern;
        } catch (NullPointerException npe) {
            throw npe;
        } catch (RuntimeException re) {
            throw new IllegalArgumentException("syntax error: " + re.getMessage() + ": " + pattern);
        }
    }

    public NativeDecimalFormat(String pattern, LocaleData data) {
        String str = pattern;
        this.address = open(str, data.currencySymbol, data.decimalSeparator, '#', data.exponentSeparator, data.groupingSeparator, data.infinity, data.internationalCurrencySymbol, data.minusSign, data.monetarySeparator, data.NaN, data.patternSeparator, data.percent, data.perMill, data.zeroDigit);
        this.lastPattern = pattern;
    }

    public synchronized void close() {
        if (this.address != 0) {
            close(this.address);
            this.address = 0;
        }
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    public Object clone() {
        try {
            NativeDecimalFormat clone = (NativeDecimalFormat) super.clone();
            clone.address = cloneImpl(this.address);
            clone.lastPattern = this.lastPattern;
            clone.negPrefNull = this.negPrefNull;
            clone.negSuffNull = this.negSuffNull;
            clone.posPrefNull = this.posPrefNull;
            clone.posSuffNull = this.posSuffNull;
            return clone;
        } catch (Object unexpected) {
            throw new AssertionError(unexpected);
        }
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof NativeDecimalFormat)) {
            return false;
        }
        NativeDecimalFormat obj = (NativeDecimalFormat) object;
        if (obj.address == this.address) {
            return true;
        }
        if (obj.toPattern().equals(toPattern()) && obj.isDecimalSeparatorAlwaysShown() == isDecimalSeparatorAlwaysShown() && obj.getGroupingSize() == getGroupingSize() && obj.getMultiplier() == getMultiplier() && obj.getNegativePrefix().equals(getNegativePrefix()) && obj.getNegativeSuffix().equals(getNegativeSuffix()) && obj.getPositivePrefix().equals(getPositivePrefix()) && obj.getPositiveSuffix().equals(getPositiveSuffix()) && obj.getMaximumIntegerDigits() == getMaximumIntegerDigits() && obj.getMaximumFractionDigits() == getMaximumFractionDigits() && obj.getMinimumIntegerDigits() == getMinimumIntegerDigits() && obj.getMinimumFractionDigits() == getMinimumFractionDigits() && obj.isGroupingUsed() == isGroupingUsed()) {
            return true;
        }
        return false;
    }

    public String toString() {
        return getClass().getName() + "[\"" + toPattern() + "\"" + ",isDecimalSeparatorAlwaysShown=" + isDecimalSeparatorAlwaysShown() + ",groupingSize=" + getGroupingSize() + ",multiplier=" + getMultiplier() + ",negativePrefix=" + getNegativePrefix() + ",negativeSuffix=" + getNegativeSuffix() + ",positivePrefix=" + getPositivePrefix() + ",positiveSuffix=" + getPositiveSuffix() + ",maxIntegerDigits=" + getMaximumIntegerDigits() + ",maxFractionDigits=" + getMaximumFractionDigits() + ",minIntegerDigits=" + getMinimumIntegerDigits() + ",minFractionDigits=" + getMinimumFractionDigits() + ",grouping=" + isGroupingUsed() + "]";
    }

    public void setDecimalFormatSymbols(DecimalFormatSymbols dfs) {
        setDecimalFormatSymbols(this.address, dfs.getCurrencySymbol(), dfs.getDecimalSeparator(), dfs.getDigit(), dfs.getExponentSeparator(), dfs.getGroupingSeparator(), dfs.getInfinity(), dfs.getInternationalCurrencySymbol(), dfs.getMinusSignString(), dfs.getMonetaryDecimalSeparator(), dfs.getNaN(), dfs.getPatternSeparator(), dfs.getPercentString(), dfs.getPerMill(), dfs.getZeroDigit());
    }

    public void setDecimalFormatSymbols(LocaleData localeData) {
        setDecimalFormatSymbols(this.address, localeData.currencySymbol, localeData.decimalSeparator, '#', localeData.exponentSeparator, localeData.groupingSeparator, localeData.infinity, localeData.internationalCurrencySymbol, localeData.minusSign, localeData.monetarySeparator, localeData.NaN, localeData.patternSeparator, localeData.percent, localeData.perMill, localeData.zeroDigit);
    }

    public char[] formatBigDecimal(BigDecimal value, FieldPosition field) {
        FieldPositionIterator fpi = FieldPositionIterator.forFieldPosition(field);
        char[] result = formatDigitList(this.address, value.toString(), fpi);
        if (!(fpi == null || field == null)) {
            updateFieldPosition(field, fpi);
        }
        return result;
    }

    public char[] formatBigInteger(BigInteger value, FieldPosition field) {
        FieldPositionIterator fpi = FieldPositionIterator.forFieldPosition(field);
        char[] result = formatDigitList(this.address, value.toString(UNUM_MONETARY_SEPARATOR_SYMBOL), fpi);
        if (!(fpi == null || field == null)) {
            updateFieldPosition(field, fpi);
        }
        return result;
    }

    public char[] formatLong(long value, FieldPosition field) {
        FieldPositionIterator fpi = FieldPositionIterator.forFieldPosition(field);
        char[] result = formatLong(this.address, value, fpi);
        if (!(fpi == null || field == null)) {
            updateFieldPosition(field, fpi);
        }
        return result;
    }

    public char[] formatDouble(double value, FieldPosition field) {
        FieldPositionIterator fpi = FieldPositionIterator.forFieldPosition(field);
        char[] result = formatDouble(this.address, value, fpi);
        if (!(fpi == null || field == null)) {
            updateFieldPosition(field, fpi);
        }
        return result;
    }

    private static void updateFieldPosition(FieldPosition fp, FieldPositionIterator fpi) {
        int field = translateFieldId(fp);
        if (field != -1) {
            while (fpi.next()) {
                if (fpi.fieldId() == field) {
                    fp.setBeginIndex(fpi.start());
                    fp.setEndIndex(fpi.limit());
                    return;
                }
            }
        }
    }

    public void applyLocalizedPattern(String pattern) {
        applyPattern(this.address, true, pattern);
        this.lastPattern = null;
    }

    public void applyPattern(String pattern) {
        if (this.lastPattern == null || !pattern.equals(this.lastPattern)) {
            applyPattern(this.address, false, pattern);
            this.lastPattern = pattern;
        }
    }

    public AttributedCharacterIterator formatToCharacterIterator(Object object) {
        if (object == null) {
            throw new NullPointerException("object == null");
        } else if (object instanceof Number) {
            String text;
            Number number = (Number) object;
            FieldPositionIterator fpIter = new FieldPositionIterator();
            if ((number instanceof BigInteger) || (number instanceof BigDecimal)) {
                text = new String(formatDigitList(this.address, number.toString(), fpIter));
            } else if ((number instanceof Double) || (number instanceof Float)) {
                text = new String(formatDouble(this.address, number.doubleValue(), fpIter));
            } else {
                text = new String(formatLong(this.address, number.longValue(), fpIter));
            }
            AttributedString as = new AttributedString(text);
            while (fpIter.next()) {
                Field field = fpIter.field();
                as.addAttribute(field, field, fpIter.start(), fpIter.limit());
            }
            return as.getIterator();
        } else {
            throw new IllegalArgumentException("object not a Number: " + object.getClass());
        }
    }

    private int makeScalePositive(int scale, StringBuilder val) {
        if (scale >= 0) {
            return scale;
        }
        for (int i = -scale; i > 0; i--) {
            val.append('0');
        }
        return UNUM_POSITIVE_PREFIX;
    }

    public String toLocalizedPattern() {
        return toPatternImpl(this.address, true);
    }

    public String toPattern() {
        return toPatternImpl(this.address, false);
    }

    public Number parse(String string, ParsePosition position) {
        return parse(this.address, string, position, this.parseBigDecimal);
    }

    public int getMaximumFractionDigits() {
        return getAttribute(this.address, UNUM_MINUS_SIGN_SYMBOL);
    }

    public int getMaximumIntegerDigits() {
        return getAttribute(this.address, UNUM_PERCENT_SYMBOL);
    }

    public int getMinimumFractionDigits() {
        return getAttribute(this.address, UNUM_PUBLIC_RULESETS);
    }

    public int getMinimumIntegerDigits() {
        return getAttribute(this.address, UNUM_ZERO_DIGIT_SYMBOL);
    }

    public int getGroupingSize() {
        if (isGroupingUsed()) {
            return getAttribute(this.address, UNUM_MONETARY_SEPARATOR_SYMBOL);
        }
        return UNUM_POSITIVE_PREFIX;
    }

    public int getMultiplier() {
        return getAttribute(this.address, UNUM_MULTIPLIER);
    }

    public String getNegativePrefix() {
        if (this.negPrefNull) {
            return null;
        }
        return getTextAttribute(this.address, UNUM_PATTERN_SEPARATOR_SYMBOL);
    }

    public String getNegativeSuffix() {
        if (this.negSuffNull) {
            return null;
        }
        return getTextAttribute(this.address, UNUM_PERCENT_SYMBOL);
    }

    public String getPositivePrefix() {
        if (this.posPrefNull) {
            return null;
        }
        return getTextAttribute(this.address, UNUM_POSITIVE_PREFIX);
    }

    public String getPositiveSuffix() {
        if (this.posSuffNull) {
            return null;
        }
        return getTextAttribute(this.address, UNUM_POSITIVE_SUFFIX);
    }

    public boolean isDecimalSeparatorAlwaysShown() {
        return getAttribute(this.address, UNUM_PATTERN_SEPARATOR_SYMBOL) != 0;
    }

    public boolean isParseBigDecimal() {
        return this.parseBigDecimal;
    }

    public boolean isParseIntegerOnly() {
        return getAttribute(this.address, UNUM_POSITIVE_PREFIX) != 0;
    }

    public boolean isGroupingUsed() {
        return getAttribute(this.address, UNUM_POSITIVE_SUFFIX) != 0;
    }

    public void setDecimalSeparatorAlwaysShown(boolean value) {
        setAttribute(this.address, UNUM_PATTERN_SEPARATOR_SYMBOL, value ? -1 : UNUM_POSITIVE_PREFIX);
    }

    public void setCurrency(String currencySymbol, String currencyCode) {
        setSymbol(this.address, UNUM_FRACTION_DIGITS, currencySymbol);
        setSymbol(this.address, UNUM_MULTIPLIER, currencyCode);
    }

    public void setGroupingSize(int value) {
        setAttribute(this.address, UNUM_MONETARY_SEPARATOR_SYMBOL, value);
    }

    public void setGroupingUsed(boolean value) {
        setAttribute(this.address, UNUM_POSITIVE_SUFFIX, value ? -1 : UNUM_POSITIVE_PREFIX);
    }

    public void setMaximumFractionDigits(int value) {
        setAttribute(this.address, UNUM_MINUS_SIGN_SYMBOL, value);
    }

    public void setMaximumIntegerDigits(int value) {
        setAttribute(this.address, UNUM_PERCENT_SYMBOL, value);
    }

    public void setMinimumFractionDigits(int value) {
        setAttribute(this.address, UNUM_PUBLIC_RULESETS, value);
    }

    public void setMinimumIntegerDigits(int value) {
        setAttribute(this.address, UNUM_ZERO_DIGIT_SYMBOL, value);
    }

    public void setMultiplier(int value) {
        setAttribute(this.address, UNUM_MULTIPLIER, value);
    }

    public void setNegativePrefix(String value) {
        this.negPrefNull = value == null;
        if (!this.negPrefNull) {
            setTextAttribute(this.address, UNUM_PATTERN_SEPARATOR_SYMBOL, value);
        }
    }

    public void setNegativeSuffix(String value) {
        this.negSuffNull = value == null;
        if (!this.negSuffNull) {
            setTextAttribute(this.address, UNUM_PERCENT_SYMBOL, value);
        }
    }

    public void setPositivePrefix(String value) {
        boolean z;
        if (value == null) {
            z = true;
        } else {
            z = false;
        }
        this.posPrefNull = z;
        if (!this.posPrefNull) {
            setTextAttribute(this.address, UNUM_POSITIVE_PREFIX, value);
        }
    }

    public void setPositiveSuffix(String value) {
        this.posSuffNull = value == null;
        if (!this.posSuffNull) {
            setTextAttribute(this.address, UNUM_POSITIVE_SUFFIX, value);
        }
    }

    public void setParseBigDecimal(boolean value) {
        this.parseBigDecimal = value;
    }

    public void setParseIntegerOnly(boolean value) {
        int i;
        if (value) {
            i = -1;
        } else {
            i = UNUM_POSITIVE_PREFIX;
        }
        setAttribute(this.address, UNUM_POSITIVE_PREFIX, i);
    }

    private static void applyPattern(long addr, boolean localized, String pattern) {
        try {
            applyPatternImpl(addr, localized, pattern);
        } catch (NullPointerException npe) {
            throw npe;
        } catch (RuntimeException re) {
            throw new IllegalArgumentException("syntax error: " + re.getMessage() + ": " + pattern);
        }
    }

    public void setRoundingMode(RoundingMode roundingMode, double roundingIncrement) {
        int nativeRoundingMode;
        switch (AnonymousClass1.$SwitchMap$java$math$RoundingMode[roundingMode.ordinal()]) {
            case UNUM_POSITIVE_SUFFIX /*1*/:
                nativeRoundingMode = UNUM_POSITIVE_PREFIX;
                break;
            case UNUM_PATTERN_SEPARATOR_SYMBOL /*2*/:
                nativeRoundingMode = UNUM_POSITIVE_SUFFIX;
                break;
            case UNUM_PERCENT_SYMBOL /*3*/:
                nativeRoundingMode = UNUM_PATTERN_SEPARATOR_SYMBOL;
                break;
            case UNUM_ZERO_DIGIT_SYMBOL /*4*/:
                nativeRoundingMode = UNUM_PERCENT_SYMBOL;
                break;
            case UNUM_INTEGER_DIGITS /*5*/:
                nativeRoundingMode = UNUM_ZERO_DIGIT_SYMBOL;
                break;
            case UNUM_MINUS_SIGN_SYMBOL /*6*/:
                nativeRoundingMode = UNUM_INTEGER_DIGITS;
                break;
            case UNUM_PUBLIC_RULESETS /*7*/:
                nativeRoundingMode = UNUM_MINUS_SIGN_SYMBOL;
                break;
            case UNUM_FRACTION_DIGITS /*8*/:
                nativeRoundingMode = UNUM_PUBLIC_RULESETS;
                break;
            default:
                throw new AssertionError();
        }
        setRoundingMode(this.address, nativeRoundingMode, roundingIncrement);
    }
}
