package java.util;

import java.io.Serializable;
import libcore.icu.ICU;
import libcore.icu.LocaleData;

public final class Currency implements Serializable {
    private static final HashMap<String, Currency> codesToCurrencies;
    private static final HashMap<Locale, Currency> localesToCurrencies;
    private static final long serialVersionUID = -158308464356906721L;
    private final String currencyCode;

    static {
        codesToCurrencies = new HashMap();
        localesToCurrencies = new HashMap();
    }

    private Currency(String currencyCode) {
        this.currencyCode = currencyCode;
        if (ICU.getCurrencySymbol(Locale.US, currencyCode) == null) {
            throw new IllegalArgumentException("Unsupported ISO 4217 currency code: " + currencyCode);
        }
    }

    public static Currency getInstance(String currencyCode) {
        Currency currency;
        synchronized (codesToCurrencies) {
            currency = (Currency) codesToCurrencies.get(currencyCode);
            if (currency == null) {
                currency = new Currency(currencyCode);
                codesToCurrencies.put(currencyCode, currency);
            }
        }
        return currency;
    }

    public static Currency getInstance(Locale locale) {
        synchronized (localesToCurrencies) {
            if (locale == null) {
                throw new NullPointerException("locale == null");
            }
            Currency currency = (Currency) localesToCurrencies.get(locale);
            if (currency != null) {
                return currency;
            }
            String country = locale.getCountry();
            String variant = locale.getVariant();
            if (!variant.isEmpty() && (variant.equals("EURO") || variant.equals("HK") || variant.equals("PREEURO"))) {
                country = country + "_" + variant;
            }
            String currencyCode = ICU.getCurrencyCode(country);
            if (currencyCode == null) {
                throw new IllegalArgumentException("Unsupported ISO 3166 country: " + locale);
            } else if (currencyCode.equals("XXX")) {
                return null;
            } else {
                Currency result = getInstance(currencyCode);
                localesToCurrencies.put(locale, result);
                return result;
            }
        }
    }

    public static Set<Currency> getAvailableCurrencies() {
        Set<Currency> result = new LinkedHashSet();
        for (String currencyCode : ICU.getAvailableCurrencyCodes()) {
            result.add(getInstance(currencyCode));
        }
        return result;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public String getDisplayName() {
        return getDisplayName(Locale.getDefault());
    }

    public String getDisplayName(Locale locale) {
        return ICU.getCurrencyDisplayName(locale, this.currencyCode);
    }

    public String getSymbol() {
        return getSymbol(Locale.getDefault());
    }

    public String getSymbol(Locale locale) {
        if (locale == null) {
            throw new NullPointerException("locale == null");
        }
        LocaleData localeData = LocaleData.get(locale);
        if (localeData.internationalCurrencySymbol.equals(this.currencyCode)) {
            return localeData.currencySymbol;
        }
        String symbol = ICU.getCurrencySymbol(locale, this.currencyCode);
        return symbol == null ? this.currencyCode : symbol;
    }

    public int getDefaultFractionDigits() {
        if (this.currencyCode.equals("XXX")) {
            return -1;
        }
        return ICU.getCurrencyFractionDigits(this.currencyCode);
    }

    public String toString() {
        return this.currencyCode;
    }

    private Object readResolve() {
        return getInstance(this.currencyCode);
    }
}
