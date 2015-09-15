package java.text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.Currency;
import java.util.Locale;
import libcore.icu.ICU;
import libcore.icu.LocaleData;
import org.xmlpull.v1.XmlPullParser;

public class DecimalFormatSymbols implements Cloneable, Serializable {
    private static final ObjectStreamField[] serialPersistentFields;
    private static final long serialVersionUID = 5772796243397350300L;
    private String NaN;
    private transient Currency currency;
    private String currencySymbol;
    private char decimalSeparator;
    private char digit;
    private transient String exponentSeparator;
    private char groupingSeparator;
    private String infinity;
    private String intlCurrencySymbol;
    private transient Locale locale;
    private String minusSign;
    private char monetarySeparator;
    private char patternSeparator;
    private char perMill;
    private String percent;
    private char zeroDigit;

    public DecimalFormatSymbols() {
        this(Locale.getDefault());
    }

    public DecimalFormatSymbols(Locale locale) {
        if (locale == null) {
            throw new NullPointerException("locale == null");
        }
        locale = LocaleData.mapInvalidAndNullLocales(locale);
        LocaleData localeData = LocaleData.get(locale);
        this.zeroDigit = localeData.zeroDigit;
        this.digit = '#';
        this.decimalSeparator = localeData.decimalSeparator;
        this.groupingSeparator = localeData.groupingSeparator;
        this.patternSeparator = localeData.patternSeparator;
        this.percent = localeData.percent;
        this.perMill = localeData.perMill;
        this.monetarySeparator = localeData.monetarySeparator;
        this.minusSign = localeData.minusSign;
        this.infinity = localeData.infinity;
        this.NaN = localeData.NaN;
        this.exponentSeparator = localeData.exponentSeparator;
        this.locale = locale;
        try {
            this.currency = Currency.getInstance(locale);
            this.currencySymbol = this.currency.getSymbol(locale);
            this.intlCurrencySymbol = this.currency.getCurrencyCode();
        } catch (IllegalArgumentException e) {
            this.currency = Currency.getInstance("XXX");
            this.currencySymbol = localeData.currencySymbol;
            this.intlCurrencySymbol = localeData.internationalCurrencySymbol;
        }
    }

    public static DecimalFormatSymbols getInstance() {
        return getInstance(Locale.getDefault());
    }

    public static DecimalFormatSymbols getInstance(Locale locale) {
        if (locale != null) {
            return new DecimalFormatSymbols(locale);
        }
        throw new NullPointerException("locale == null");
    }

    public static Locale[] getAvailableLocales() {
        return ICU.getAvailableDecimalFormatSymbolsLocales();
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (Object e) {
            throw new AssertionError(e);
        }
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof DecimalFormatSymbols)) {
            return false;
        }
        DecimalFormatSymbols obj = (DecimalFormatSymbols) object;
        if (this.currency.equals(obj.currency) && this.currencySymbol.equals(obj.currencySymbol) && this.decimalSeparator == obj.decimalSeparator && this.digit == obj.digit && this.exponentSeparator.equals(obj.exponentSeparator) && this.groupingSeparator == obj.groupingSeparator && this.infinity.equals(obj.infinity) && this.intlCurrencySymbol.equals(obj.intlCurrencySymbol) && this.minusSign.equals(obj.minusSign) && this.monetarySeparator == obj.monetarySeparator && this.NaN.equals(obj.NaN) && this.patternSeparator == obj.patternSeparator && this.perMill == obj.perMill && this.percent.equals(obj.percent) && this.zeroDigit == obj.zeroDigit) {
            return true;
        }
        return false;
    }

    public String toString() {
        return getClass().getName() + "[currency=" + this.currency + ",currencySymbol=" + this.currencySymbol + ",decimalSeparator=" + this.decimalSeparator + ",digit=" + this.digit + ",exponentSeparator=" + this.exponentSeparator + ",groupingSeparator=" + this.groupingSeparator + ",infinity=" + this.infinity + ",intlCurrencySymbol=" + this.intlCurrencySymbol + ",minusSign=" + this.minusSign + ",monetarySeparator=" + this.monetarySeparator + ",NaN=" + this.NaN + ",patternSeparator=" + this.patternSeparator + ",perMill=" + this.perMill + ",percent=" + this.percent + ",zeroDigit=" + this.zeroDigit + "]";
    }

    public Currency getCurrency() {
        return this.currency;
    }

    public String getInternationalCurrencySymbol() {
        return this.intlCurrencySymbol;
    }

    public String getCurrencySymbol() {
        return this.currencySymbol;
    }

    public char getDecimalSeparator() {
        return this.decimalSeparator;
    }

    public char getDigit() {
        return this.digit;
    }

    public char getGroupingSeparator() {
        return this.groupingSeparator;
    }

    public String getInfinity() {
        return this.infinity;
    }

    public char getMinusSign() {
        if (this.minusSign.length() == 1) {
            return this.minusSign.charAt(0);
        }
        throw new UnsupportedOperationException("Minus sign spans multiple characters: " + this.minusSign);
    }

    public String getMinusSignString() {
        return this.minusSign;
    }

    public String getPercentString() {
        return this.percent;
    }

    public char getMonetaryDecimalSeparator() {
        return this.monetarySeparator;
    }

    public String getNaN() {
        return this.NaN;
    }

    public char getPatternSeparator() {
        return this.patternSeparator;
    }

    public char getPercent() {
        if (this.percent.length() == 1) {
            return this.percent.charAt(0);
        }
        throw new UnsupportedOperationException("Percent spans multiple characters: " + this.percent);
    }

    public char getPerMill() {
        return this.perMill;
    }

    public char getZeroDigit() {
        return this.zeroDigit;
    }

    public String getExponentSeparator() {
        return this.exponentSeparator;
    }

    public int hashCode() {
        return ((((((((((((((((((((((((((this.zeroDigit + 527) * 31) + this.digit) * 31) + this.decimalSeparator) * 31) + this.groupingSeparator) * 31) + this.patternSeparator) * 31) + this.percent.hashCode()) * 31) + this.perMill) * 31) + this.monetarySeparator) * 31) + this.minusSign.hashCode()) * 31) + this.exponentSeparator.hashCode()) * 31) + this.infinity.hashCode()) * 31) + this.NaN.hashCode()) * 31) + this.currencySymbol.hashCode()) * 31) + this.intlCurrencySymbol.hashCode();
    }

    public void setCurrency(Currency currency) {
        if (currency == null) {
            throw new NullPointerException("currency == null");
        }
        this.currency = currency;
        this.intlCurrencySymbol = currency.getCurrencyCode();
        this.currencySymbol = currency.getSymbol(this.locale);
    }

    public void setInternationalCurrencySymbol(String value) {
        if (value == null) {
            this.currency = null;
            this.intlCurrencySymbol = null;
        } else if (!value.equals(this.intlCurrencySymbol)) {
            try {
                this.currency = Currency.getInstance(value);
                this.currencySymbol = this.currency.getSymbol(this.locale);
            } catch (IllegalArgumentException e) {
                this.currency = null;
            }
            this.intlCurrencySymbol = value;
        }
    }

    public void setCurrencySymbol(String value) {
        this.currencySymbol = value;
    }

    public void setDecimalSeparator(char value) {
        this.decimalSeparator = value;
    }

    public void setDigit(char value) {
        this.digit = value;
    }

    public void setGroupingSeparator(char value) {
        this.groupingSeparator = value;
    }

    public void setInfinity(String value) {
        this.infinity = value;
    }

    public void setMinusSign(char value) {
        this.minusSign = String.valueOf(value);
    }

    public void setMonetaryDecimalSeparator(char value) {
        this.monetarySeparator = value;
    }

    public void setNaN(String value) {
        this.NaN = value;
    }

    public void setPatternSeparator(char value) {
        this.patternSeparator = value;
    }

    public void setPercent(char value) {
        this.percent = String.valueOf(value);
    }

    public void setPerMill(char value) {
        this.perMill = value;
    }

    public void setZeroDigit(char value) {
        this.zeroDigit = value;
    }

    public void setExponentSeparator(String value) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }
        this.exponentSeparator = value;
    }

    static {
        serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("currencySymbol", String.class), new ObjectStreamField("decimalSeparator", Character.TYPE), new ObjectStreamField("digit", Character.TYPE), new ObjectStreamField("exponential", Character.TYPE), new ObjectStreamField("exponentialSeparator", String.class), new ObjectStreamField("groupingSeparator", Character.TYPE), new ObjectStreamField("infinity", String.class), new ObjectStreamField("intlCurrencySymbol", String.class), new ObjectStreamField("minusSign", Character.TYPE), new ObjectStreamField("monetarySeparator", Character.TYPE), new ObjectStreamField("NaN", String.class), new ObjectStreamField("patternSeparator", Character.TYPE), new ObjectStreamField("percent", Character.TYPE), new ObjectStreamField("perMill", Character.TYPE), new ObjectStreamField("serialVersionOnStream", Integer.TYPE), new ObjectStreamField("zeroDigit", Character.TYPE), new ObjectStreamField("locale", Locale.class)};
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        PutField fields = stream.putFields();
        fields.put("currencySymbol", this.currencySymbol);
        fields.put("decimalSeparator", getDecimalSeparator());
        fields.put("digit", getDigit());
        fields.put("exponential", this.exponentSeparator.charAt(0));
        fields.put("exponentialSeparator", this.exponentSeparator);
        fields.put("groupingSeparator", getGroupingSeparator());
        fields.put("infinity", this.infinity);
        fields.put("intlCurrencySymbol", this.intlCurrencySymbol);
        fields.put("minusSign", getMinusSign());
        fields.put("monetarySeparator", getMonetaryDecimalSeparator());
        fields.put("NaN", this.NaN);
        fields.put("patternSeparator", getPatternSeparator());
        fields.put("percent", getPercent());
        fields.put("perMill", getPerMill());
        fields.put("serialVersionOnStream", 3);
        fields.put("zeroDigit", getZeroDigit());
        fields.put("locale", this.locale);
        stream.writeFields();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        GetField fields = stream.readFields();
        int serialVersionOnStream = fields.get("serialVersionOnStream", 0);
        this.currencySymbol = (String) fields.get("currencySymbol", XmlPullParser.NO_NAMESPACE);
        setDecimalSeparator(fields.get("decimalSeparator", '.'));
        setDigit(fields.get("digit", '#'));
        setGroupingSeparator(fields.get("groupingSeparator", ','));
        this.infinity = (String) fields.get("infinity", XmlPullParser.NO_NAMESPACE);
        this.intlCurrencySymbol = (String) fields.get("intlCurrencySymbol", XmlPullParser.NO_NAMESPACE);
        setMinusSign(fields.get("minusSign", '-'));
        this.NaN = (String) fields.get("NaN", XmlPullParser.NO_NAMESPACE);
        setPatternSeparator(fields.get("patternSeparator", ';'));
        setPercent(fields.get("percent", '%'));
        setPerMill(fields.get("perMill", '\u2030'));
        setZeroDigit(fields.get("zeroDigit", '0'));
        this.locale = (Locale) fields.get("locale", null);
        if (serialVersionOnStream == 0) {
            setMonetaryDecimalSeparator(getDecimalSeparator());
        } else {
            setMonetaryDecimalSeparator(fields.get("monetarySeparator", '.'));
        }
        if (serialVersionOnStream == 0) {
            this.exponentSeparator = "E";
        } else if (serialVersionOnStream < 3) {
            setExponentSeparator(String.valueOf(fields.get("exponential", 'E')));
        } else {
            setExponentSeparator((String) fields.get("exponentialSeparator", (Object) "E"));
        }
        try {
            this.currency = Currency.getInstance(this.intlCurrencySymbol);
        } catch (IllegalArgumentException e) {
            this.currency = null;
        }
    }
}
