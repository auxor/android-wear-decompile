package libcore.icu;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import libcore.util.BasicLruCache;
import org.xmlpull.v1.XmlPullParser;

public final class ICU {
    private static final BasicLruCache<String, String> CACHED_PATTERNS;
    private static final int IDX_LANGUAGE = 0;
    private static final int IDX_REGION = 2;
    private static final int IDX_SCRIPT = 1;
    private static final int IDX_VARIANT = 3;
    public static final int U_BUFFER_OVERFLOW_ERROR = 15;
    public static final int U_ILLEGAL_CHAR_FOUND = 12;
    public static final int U_INVALID_CHAR_FOUND = 10;
    public static final int U_TRUNCATED_CHAR_FOUND = 11;
    public static final int U_ZERO_ERROR = 0;
    private static Locale[] availableLocalesCache;
    private static String[] isoCountries;
    private static String[] isoLanguages;

    @Deprecated
    public static native String addLikelySubtags(String str);

    private static native String[] getAvailableBreakIteratorLocalesNative();

    private static native String[] getAvailableCalendarLocalesNative();

    private static native String[] getAvailableCollatorLocalesNative();

    public static native String[] getAvailableCurrencyCodes();

    private static native String[] getAvailableDateFormatLocalesNative();

    private static native String[] getAvailableLocalesNative();

    private static native String[] getAvailableNumberFormatLocalesNative();

    private static native String getBestDateTimePatternNative(String str, String str2);

    public static native String getCldrVersion();

    public static native String getCurrencyCode(String str);

    private static native String getCurrencyDisplayName(String str, String str2);

    public static native int getCurrencyFractionDigits(String str);

    public static native int getCurrencyNumericCode(String str);

    private static native String getCurrencySymbol(String str, String str2);

    public static native String getDefaultLocale();

    private static native String getDisplayCountryNative(String str, String str2);

    private static native String getDisplayLanguageNative(String str, String str2);

    private static native String getDisplayScriptNative(String str, String str2);

    private static native String getDisplayVariantNative(String str, String str2);

    public static native String getISO3Country(String str);

    public static native String getISO3Language(String str);

    private static native String[] getISOCountriesNative();

    private static native String[] getISOLanguagesNative();

    public static native String getIcuVersion();

    @Deprecated
    public static native String getScript(String str);

    public static native String getUnicodeVersion();

    static native boolean initLocaleDataNative(String str, LocaleData localeData);

    public static native void setDefaultLocale(String str);

    private static native String toLowerCase(String str, String str2);

    private static native String toUpperCase(String str, String str2);

    static {
        CACHED_PATTERNS = new BasicLruCache(8);
    }

    public static String[] getISOLanguages() {
        if (isoLanguages == null) {
            isoLanguages = getISOLanguagesNative();
        }
        return (String[]) isoLanguages.clone();
    }

    public static String[] getISOCountries() {
        if (isoCountries == null) {
            isoCountries = getISOCountriesNative();
        }
        return (String[]) isoCountries.clone();
    }

    private static void parseLangScriptRegionAndVariants(String string, String[] outputArray) {
        int first = string.indexOf(95);
        int second = string.indexOf(95, first + IDX_SCRIPT);
        int third = string.indexOf(95, second + IDX_SCRIPT);
        if (first == -1) {
            outputArray[IDX_LANGUAGE] = string;
        } else if (second == -1) {
            outputArray[IDX_LANGUAGE] = string.substring(IDX_LANGUAGE, first);
            secondString = string.substring(first + IDX_SCRIPT);
            if (secondString.length() == 4) {
                outputArray[IDX_SCRIPT] = secondString;
            } else if (secondString.length() == IDX_REGION || secondString.length() == IDX_VARIANT) {
                outputArray[IDX_REGION] = secondString;
            } else {
                outputArray[IDX_VARIANT] = secondString;
            }
        } else if (third == -1) {
            outputArray[IDX_LANGUAGE] = string.substring(IDX_LANGUAGE, first);
            secondString = string.substring(first + IDX_SCRIPT, second);
            String thirdString = string.substring(second + IDX_SCRIPT);
            if (secondString.length() == 4) {
                outputArray[IDX_SCRIPT] = secondString;
                if (thirdString.length() == IDX_REGION || thirdString.length() == IDX_VARIANT || thirdString.isEmpty()) {
                    outputArray[IDX_REGION] = thirdString;
                } else {
                    outputArray[IDX_VARIANT] = thirdString;
                }
            } else if (secondString.isEmpty() || secondString.length() == IDX_REGION || secondString.length() == IDX_VARIANT) {
                outputArray[IDX_REGION] = secondString;
                outputArray[IDX_VARIANT] = thirdString;
            } else {
                outputArray[IDX_VARIANT] = string.substring(first + IDX_SCRIPT);
            }
        } else {
            outputArray[IDX_LANGUAGE] = string.substring(IDX_LANGUAGE, first);
            secondString = string.substring(first + IDX_SCRIPT, second);
            if (secondString.length() == 4) {
                outputArray[IDX_SCRIPT] = secondString;
                outputArray[IDX_REGION] = string.substring(second + IDX_SCRIPT, third);
                outputArray[IDX_VARIANT] = string.substring(third + IDX_SCRIPT);
                return;
            }
            outputArray[IDX_REGION] = secondString;
            outputArray[IDX_VARIANT] = string.substring(second + IDX_SCRIPT);
        }
    }

    public static Locale localeFromIcuLocaleId(String localeId) {
        int extensionsIndex = localeId.indexOf(64);
        Map<Character, String> extensionsMap = Collections.EMPTY_MAP;
        Map<String, String> unicodeKeywordsMap = Collections.EMPTY_MAP;
        Set<String> unicodeAttributeSet = Collections.EMPTY_SET;
        if (extensionsIndex != -1) {
            extensionsMap = new HashMap();
            unicodeKeywordsMap = new HashMap();
            unicodeAttributeSet = new HashSet();
            String substring = localeId.substring(extensionsIndex + IDX_SCRIPT);
            String[] split = extensionsString.split(";");
            int length = split.length;
            for (int i$ = IDX_LANGUAGE; i$ < length; i$ += IDX_SCRIPT) {
                String extension = split[i$];
                if (extension.startsWith("attribute=")) {
                    substring = extension.substring("attribute=".length());
                    String[] arr$ = unicodeAttributeValues.split("-");
                    int len$ = arr$.length;
                    for (int i$2 = IDX_LANGUAGE; i$2 < len$; i$2 += IDX_SCRIPT) {
                        unicodeAttributeSet.add(arr$[i$2]);
                    }
                } else {
                    int separatorIndex = extension.indexOf(61);
                    if (separatorIndex == IDX_SCRIPT) {
                        extensionsMap.put(Character.valueOf(extension.charAt(IDX_LANGUAGE)), extension.substring(IDX_REGION));
                    } else {
                        unicodeKeywordsMap.put(extension.substring(IDX_LANGUAGE, separatorIndex), extension.substring(separatorIndex + IDX_SCRIPT));
                    }
                }
            }
        }
        String[] outputArray = new String[]{XmlPullParser.NO_NAMESPACE, XmlPullParser.NO_NAMESPACE, XmlPullParser.NO_NAMESPACE, XmlPullParser.NO_NAMESPACE};
        if (extensionsIndex == -1) {
            parseLangScriptRegionAndVariants(localeId, outputArray);
        } else {
            parseLangScriptRegionAndVariants(localeId.substring(IDX_LANGUAGE, extensionsIndex), outputArray);
        }
        return new Locale(outputArray[IDX_LANGUAGE], outputArray[IDX_REGION], outputArray[IDX_VARIANT], outputArray[IDX_SCRIPT], unicodeAttributeSet, unicodeKeywordsMap, extensionsMap, true);
    }

    public static Locale[] localesFromStrings(String[] localeNames) {
        LinkedHashSet<Locale> set = new LinkedHashSet();
        String[] arr$ = localeNames;
        int len$ = arr$.length;
        for (int i$ = IDX_LANGUAGE; i$ < len$; i$ += IDX_SCRIPT) {
            set.add(localeFromIcuLocaleId(arr$[i$]));
        }
        return (Locale[]) set.toArray(new Locale[set.size()]);
    }

    public static Locale[] getAvailableLocales() {
        if (availableLocalesCache == null) {
            availableLocalesCache = localesFromStrings(getAvailableLocalesNative());
        }
        return (Locale[]) availableLocalesCache.clone();
    }

    public static Locale[] getAvailableBreakIteratorLocales() {
        return localesFromStrings(getAvailableBreakIteratorLocalesNative());
    }

    public static Locale[] getAvailableCalendarLocales() {
        return localesFromStrings(getAvailableCalendarLocalesNative());
    }

    public static Locale[] getAvailableCollatorLocales() {
        return localesFromStrings(getAvailableCollatorLocalesNative());
    }

    public static Locale[] getAvailableDateFormatLocales() {
        return localesFromStrings(getAvailableDateFormatLocalesNative());
    }

    public static Locale[] getAvailableDateFormatSymbolsLocales() {
        return getAvailableDateFormatLocales();
    }

    public static Locale[] getAvailableDecimalFormatSymbolsLocales() {
        return getAvailableNumberFormatLocales();
    }

    public static Locale[] getAvailableNumberFormatLocales() {
        return localesFromStrings(getAvailableNumberFormatLocalesNative());
    }

    public static String getBestDateTimePattern(String skeleton, Locale locale) {
        String pattern;
        String languageTag = locale.toLanguageTag();
        String key = skeleton + "\t" + languageTag;
        synchronized (CACHED_PATTERNS) {
            pattern = (String) CACHED_PATTERNS.get(key);
            if (pattern == null) {
                pattern = getBestDateTimePatternNative(skeleton, languageTag);
                CACHED_PATTERNS.put(key, pattern);
            }
        }
        return pattern;
    }

    public static char[] getDateFormatOrder(String pattern) {
        char[] result = new char[IDX_VARIANT];
        int resultIndex = IDX_LANGUAGE;
        boolean sawDay = false;
        boolean sawMonth = false;
        boolean sawYear = false;
        int i = IDX_LANGUAGE;
        while (i < pattern.length()) {
            char ch = pattern.charAt(i);
            if (ch == 'd' || ch == 'L' || ch == 'M' || ch == 'y') {
                int resultIndex2;
                if (ch == 'd' && !sawDay) {
                    resultIndex2 = resultIndex + IDX_SCRIPT;
                    result[resultIndex] = 'd';
                    sawDay = true;
                    resultIndex = resultIndex2;
                } else if ((ch == 'L' || ch == 'M') && !sawMonth) {
                    resultIndex2 = resultIndex + IDX_SCRIPT;
                    result[resultIndex] = 'M';
                    sawMonth = true;
                    resultIndex = resultIndex2;
                } else if (ch == 'y' && !sawYear) {
                    resultIndex2 = resultIndex + IDX_SCRIPT;
                    result[resultIndex] = 'y';
                    sawYear = true;
                    resultIndex = resultIndex2;
                }
            } else if (ch == 'G') {
                continue;
            } else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                throw new IllegalArgumentException("Bad pattern character '" + ch + "' in " + pattern);
            } else if (ch != '\'') {
                continue;
            } else if (i >= pattern.length() - 1 || pattern.charAt(i + IDX_SCRIPT) != '\'') {
                i = pattern.indexOf(39, i + IDX_SCRIPT);
                if (i == -1) {
                    throw new IllegalArgumentException("Bad quoting in " + pattern);
                }
                i += IDX_SCRIPT;
            } else {
                i += IDX_SCRIPT;
            }
            i += IDX_SCRIPT;
        }
        return result;
    }

    public static String toLowerCase(String s, Locale locale) {
        return toLowerCase(s, locale.toLanguageTag());
    }

    public static String toUpperCase(String s, Locale locale) {
        return toUpperCase(s, locale.toLanguageTag());
    }

    public static boolean U_FAILURE(int error) {
        return error > 0;
    }

    public static String getCurrencyDisplayName(Locale locale, String currencyCode) {
        return getCurrencyDisplayName(locale.toLanguageTag(), currencyCode);
    }

    public static String getCurrencySymbol(Locale locale, String currencyCode) {
        return getCurrencySymbol(locale.toLanguageTag(), currencyCode);
    }

    public static String getDisplayCountry(Locale targetLocale, Locale locale) {
        return getDisplayCountryNative(targetLocale.toLanguageTag(), locale.toLanguageTag());
    }

    public static String getDisplayLanguage(Locale targetLocale, Locale locale) {
        return getDisplayLanguageNative(targetLocale.toLanguageTag(), locale.toLanguageTag());
    }

    public static String getDisplayVariant(Locale targetLocale, Locale locale) {
        return getDisplayVariantNative(targetLocale.toLanguageTag(), locale.toLanguageTag());
    }

    public static String getDisplayScript(Locale targetLocale, Locale locale) {
        return getDisplayScriptNative(targetLocale.toLanguageTag(), locale.toLanguageTag());
    }

    public static Locale addLikelySubtags(Locale locale) {
        return Locale.forLanguageTag(addLikelySubtags(locale.toLanguageTag()).replace('_', '-'));
    }
}
