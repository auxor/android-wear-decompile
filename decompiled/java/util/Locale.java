package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;
import libcore.icu.ICU;
import org.xmlpull.v1.XmlPullParser;

public final class Locale implements Cloneable, Serializable {
    public static final Locale CANADA = null;
    public static final Locale CANADA_FRENCH = null;
    public static final Locale CHINA = null;
    public static final Locale CHINESE = null;
    public static final Locale ENGLISH = null;
    public static final Locale FRANCE = null;
    public static final Locale FRENCH = null;
    public static final Locale GERMAN = null;
    public static final Locale GERMANY = null;
    private static final TreeMap<String, String> GRANDFATHERED_LOCALES = null;
    public static final Locale ITALIAN = null;
    public static final Locale ITALY = null;
    public static final Locale JAPAN = null;
    public static final Locale JAPANESE = null;
    public static final Locale KOREA = null;
    public static final Locale KOREAN = null;
    public static final Locale PRC = null;
    public static final char PRIVATE_USE_EXTENSION = 'x';
    public static final Locale ROOT = null;
    public static final Locale SIMPLIFIED_CHINESE = null;
    public static final Locale TAIWAN = null;
    public static final Locale TRADITIONAL_CHINESE = null;
    public static final Locale UK = null;
    private static final String UNDETERMINED_LANGUAGE = "und";
    public static final char UNICODE_LOCALE_EXTENSION = 'u';
    public static final Locale US = null;
    private static Locale defaultLocale = null;
    private static final ObjectStreamField[] serialPersistentFields = null;
    private static final long serialVersionUID = 9149081749638150636L;
    private transient String cachedIcuLocaleId;
    private transient String cachedLanguageTag;
    private transient String cachedToStringResult;
    private transient String countryCode;
    private transient Map<Character, String> extensions;
    private final transient boolean hasValidatedFields;
    private transient String languageCode;
    private transient String scriptCode;
    private transient Set<String> unicodeAttributes;
    private transient Map<String, String> unicodeKeywords;
    private transient String variantCode;

    public static final class Builder {
        private final Set<String> attributes;
        private final Map<Character, String> extensions;
        private final Map<String, String> keywords;
        private String language;
        private String region;
        private String script;
        private String variant;

        public Builder() {
            String str = XmlPullParser.NO_NAMESPACE;
            this.script = str;
            this.variant = str;
            this.region = str;
            this.language = str;
            this.attributes = new TreeSet();
            this.keywords = new TreeMap();
            this.extensions = new TreeMap();
        }

        public Builder setLanguage(String language) {
            this.language = normalizeAndValidateLanguage(language, true);
            return this;
        }

        private static String normalizeAndValidateLanguage(String language, boolean strict) {
            if (language == null || language.isEmpty()) {
                return XmlPullParser.NO_NAMESPACE;
            }
            String lowercaseLanguage = language.toLowerCase(Locale.ROOT);
            if (Locale.isValidBcp47Alpha(lowercaseLanguage, 2, 3)) {
                return lowercaseLanguage;
            }
            if (!strict) {
                return Locale.UNDETERMINED_LANGUAGE;
            }
            throw new IllformedLocaleException("Invalid language: " + language);
        }

        public Builder setLanguageTag(String languageTag) {
            if (languageTag == null || languageTag.isEmpty()) {
                clear();
            } else {
                Locale fromIcu = Locale.forLanguageTag(languageTag, true);
                if (fromIcu == null) {
                    throw new IllformedLocaleException("Invalid languageTag: " + languageTag);
                }
                setLocale(fromIcu);
            }
            return this;
        }

        public Builder setRegion(String region) {
            this.region = normalizeAndValidateRegion(region, true);
            return this;
        }

        private static String normalizeAndValidateRegion(String region, boolean strict) {
            if (region == null || region.isEmpty()) {
                return XmlPullParser.NO_NAMESPACE;
            }
            String uppercaseRegion = region.toUpperCase(Locale.ROOT);
            if (Locale.isValidBcp47Alpha(uppercaseRegion, 2, 2) || Locale.isUnM49AreaCode(uppercaseRegion)) {
                return uppercaseRegion;
            }
            if (!strict) {
                return XmlPullParser.NO_NAMESPACE;
            }
            throw new IllformedLocaleException("Invalid region: " + region);
        }

        public Builder setVariant(String variant) {
            this.variant = normalizeAndValidateVariant(variant);
            return this;
        }

        private static String normalizeAndValidateVariant(String variant) {
            if (variant == null || variant.isEmpty()) {
                return XmlPullParser.NO_NAMESPACE;
            }
            String normalizedVariant = variant.replace('-', '_');
            String[] arr$ = normalizedVariant.split("_");
            int len$ = arr$.length;
            int i$ = 0;
            while (i$ < len$) {
                if (isValidVariantSubtag(arr$[i$])) {
                    i$++;
                } else {
                    throw new IllformedLocaleException("Invalid variant: " + variant);
                }
            }
            return normalizedVariant;
        }

        private static boolean isValidVariantSubtag(String subTag) {
            if (subTag.length() < 5 || subTag.length() > 8) {
                if (subTag.length() == 4) {
                    char firstChar = subTag.charAt(0);
                    if (firstChar >= '0' && firstChar <= '9' && Locale.isAsciiAlphaNum(subTag)) {
                        return true;
                    }
                }
            } else if (Locale.isAsciiAlphaNum(subTag)) {
                return true;
            }
            return false;
        }

        public Builder setScript(String script) {
            this.script = normalizeAndValidateScript(script, true);
            return this;
        }

        private static String normalizeAndValidateScript(String script, boolean strict) {
            if (script == null || script.isEmpty()) {
                return XmlPullParser.NO_NAMESPACE;
            }
            if (Locale.isValidBcp47Alpha(script, 4, 4)) {
                return Locale.titleCaseAsciiWord(script);
            }
            if (!strict) {
                return XmlPullParser.NO_NAMESPACE;
            }
            throw new IllformedLocaleException("Invalid script: " + script);
        }

        public Builder setLocale(Locale locale) {
            if (locale == null) {
                throw new NullPointerException("locale == null");
            }
            String backupLanguage = this.language;
            String backupRegion = this.region;
            String backupVariant = this.variant;
            try {
                setLanguage(locale.getLanguage());
                setRegion(locale.getCountry());
                setVariant(locale.getVariant());
                this.script = locale.getScript();
                this.extensions.clear();
                this.extensions.putAll(locale.extensions);
                this.keywords.clear();
                this.keywords.putAll(locale.unicodeKeywords);
                this.attributes.clear();
                this.attributes.addAll(locale.unicodeAttributes);
                return this;
            } catch (IllformedLocaleException ifle) {
                this.language = backupLanguage;
                this.region = backupRegion;
                this.variant = backupVariant;
                throw ifle;
            }
        }

        public Builder addUnicodeLocaleAttribute(String attribute) {
            if (attribute == null) {
                throw new NullPointerException("attribute == null");
            }
            String lowercaseAttribute = attribute.toLowerCase(Locale.ROOT);
            if (Locale.isValidBcp47Alphanum(lowercaseAttribute, 3, 8)) {
                this.attributes.add(lowercaseAttribute);
                return this;
            }
            throw new IllformedLocaleException("Invalid locale attribute: " + attribute);
        }

        public Builder removeUnicodeLocaleAttribute(String attribute) {
            if (attribute == null) {
                throw new NullPointerException("attribute == null");
            } else if (Locale.isValidBcp47Alphanum(attribute.toLowerCase(Locale.ROOT), 3, 8)) {
                this.attributes.remove(attribute);
                return this;
            } else {
                throw new IllformedLocaleException("Invalid locale attribute: " + attribute);
            }
        }

        public Builder setExtension(char key, String value) {
            if (value == null || value.isEmpty()) {
                this.extensions.remove(Character.valueOf(key));
            } else {
                String normalizedValue = value.toLowerCase(Locale.ROOT).replace('_', '-');
                String[] subtags = normalizedValue.split("-");
                int minimumLength = key == Locale.PRIVATE_USE_EXTENSION ? 1 : 2;
                String[] arr$ = subtags;
                int len$ = arr$.length;
                int i$ = 0;
                while (i$ < len$) {
                    if (Locale.isValidBcp47Alphanum(arr$[i$], minimumLength, 8)) {
                        i$++;
                    } else {
                        throw new IllformedLocaleException("Invalid private use extension : " + value);
                    }
                }
                if (key == Locale.UNICODE_LOCALE_EXTENSION) {
                    this.extensions.clear();
                    this.attributes.clear();
                    Locale.parseUnicodeExtension(subtags, this.keywords, this.attributes);
                } else {
                    this.extensions.put(Character.valueOf(key), normalizedValue);
                }
            }
            return this;
        }

        public Builder clearExtensions() {
            this.extensions.clear();
            this.attributes.clear();
            this.keywords.clear();
            return this;
        }

        public Builder setUnicodeLocaleKeyword(String key, String type) {
            if (key == null) {
                throw new NullPointerException("key == null");
            }
            if (type != null || this.keywords == null) {
                String lowerCaseKey = key.toLowerCase(Locale.ROOT);
                if (lowerCaseKey.length() == 2 && Locale.isAsciiAlphaNum(lowerCaseKey)) {
                    String lowerCaseType = type.toLowerCase(Locale.ROOT).replace((CharSequence) "_", (CharSequence) "-");
                    if (Locale.isValidTypeList(lowerCaseType)) {
                        this.keywords.put(lowerCaseKey, lowerCaseType);
                    } else {
                        throw new IllformedLocaleException("Invalid unicode locale type: " + type);
                    }
                }
                throw new IllformedLocaleException("Invalid unicode locale keyword: " + key);
            }
            this.keywords.remove(key);
            return this;
        }

        public Builder clear() {
            clearExtensions();
            String str = XmlPullParser.NO_NAMESPACE;
            this.script = str;
            this.variant = str;
            this.region = str;
            this.language = str;
            return this;
        }

        public Locale build() {
            return new Locale(this.language, this.region, this.variant, this.script, this.attributes, this.keywords, this.extensions, true);
        }
    }

    static {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.util.Locale.<clinit>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.util.Locale.<clinit>():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.Locale.<clinit>():void");
    }

    public static Locale forLanguageTag(String languageTag) {
        if (languageTag != null) {
            return forLanguageTag(languageTag, false);
        }
        throw new NullPointerException("languageTag == null");
    }

    private Locale(boolean hasValidatedFields, String lowerCaseLanguageCode, String upperCaseCountryCode) {
        this.languageCode = lowerCaseLanguageCode;
        this.countryCode = upperCaseCountryCode;
        this.variantCode = XmlPullParser.NO_NAMESPACE;
        this.scriptCode = XmlPullParser.NO_NAMESPACE;
        this.unicodeAttributes = Collections.EMPTY_SET;
        this.unicodeKeywords = Collections.EMPTY_MAP;
        this.extensions = Collections.EMPTY_MAP;
        this.hasValidatedFields = hasValidatedFields;
    }

    public Locale(String language) {
        this(language, XmlPullParser.NO_NAMESPACE, XmlPullParser.NO_NAMESPACE, XmlPullParser.NO_NAMESPACE, Collections.EMPTY_SET, Collections.EMPTY_MAP, Collections.EMPTY_MAP, false);
    }

    public Locale(String language, String country) {
        this(language, country, XmlPullParser.NO_NAMESPACE, XmlPullParser.NO_NAMESPACE, Collections.EMPTY_SET, Collections.EMPTY_MAP, Collections.EMPTY_MAP, false);
    }

    public Locale(String language, String country, String variant, String scriptCode, Set<String> unicodeAttributes, Map<String, String> unicodeKeywords, Map<Character, String> extensions, boolean hasValidatedFields) {
        if (language == null || country == null || variant == null) {
            throw new NullPointerException("language=" + language + ",country=" + country + ",variant=" + variant);
        }
        if (hasValidatedFields) {
            this.languageCode = adjustLanguageCode(language);
            this.countryCode = country;
            this.variantCode = variant;
        } else if (language.isEmpty() && country.isEmpty()) {
            this.languageCode = XmlPullParser.NO_NAMESPACE;
            this.countryCode = XmlPullParser.NO_NAMESPACE;
            this.variantCode = variant;
        } else {
            this.languageCode = adjustLanguageCode(language);
            this.countryCode = country.toUpperCase(US);
            this.variantCode = variant;
        }
        this.scriptCode = scriptCode;
        if (hasValidatedFields) {
            Set<String> attribsCopy = new TreeSet((Collection) unicodeAttributes);
            Map<String, String> keywordsCopy = new TreeMap((Map) unicodeKeywords);
            Map<Character, String> extensionsCopy = new TreeMap((Map) extensions);
            addUnicodeExtensionToExtensionsMap(attribsCopy, keywordsCopy, extensionsCopy);
            this.unicodeAttributes = Collections.unmodifiableSet(attribsCopy);
            this.unicodeKeywords = Collections.unmodifiableMap(keywordsCopy);
            this.extensions = Collections.unmodifiableMap(extensionsCopy);
        } else {
            this.unicodeAttributes = unicodeAttributes;
            this.unicodeKeywords = unicodeKeywords;
            this.extensions = extensions;
        }
        this.hasValidatedFields = hasValidatedFields;
    }

    public Locale(String language, String country, String variant) {
        this(language, country, variant, XmlPullParser.NO_NAMESPACE, Collections.EMPTY_SET, Collections.EMPTY_MAP, Collections.EMPTY_MAP, false);
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (Object e) {
            throw new AssertionError(e);
        }
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Locale)) {
            return false;
        }
        Locale o = (Locale) object;
        if (this.languageCode.equals(o.languageCode) && this.countryCode.equals(o.countryCode) && this.variantCode.equals(o.variantCode) && this.scriptCode.equals(o.scriptCode) && this.extensions.equals(o.extensions)) {
            return true;
        }
        return false;
    }

    public static Locale[] getAvailableLocales() {
        return ICU.getAvailableLocales();
    }

    public String getCountry() {
        return this.countryCode;
    }

    public static Locale getDefault() {
        return defaultLocale;
    }

    public final String getDisplayCountry() {
        return getDisplayCountry(getDefault());
    }

    public String getDisplayCountry(Locale locale) {
        if (this.countryCode.isEmpty()) {
            return XmlPullParser.NO_NAMESPACE;
        }
        if (Builder.normalizeAndValidateRegion(this.countryCode, false).isEmpty()) {
            return this.countryCode;
        }
        String result = ICU.getDisplayCountry(this, locale);
        if (result == null) {
            return ICU.getDisplayCountry(this, getDefault());
        }
        return result;
    }

    public final String getDisplayLanguage() {
        return getDisplayLanguage(getDefault());
    }

    public String getDisplayLanguage(Locale locale) {
        if (this.languageCode.isEmpty()) {
            return XmlPullParser.NO_NAMESPACE;
        }
        if (UNDETERMINED_LANGUAGE.equals(Builder.normalizeAndValidateLanguage(this.languageCode, false))) {
            return this.languageCode;
        }
        String result = ICU.getDisplayLanguage(this, locale);
        if (result == null) {
            return ICU.getDisplayLanguage(this, getDefault());
        }
        return result;
    }

    public final String getDisplayName() {
        return getDisplayName(getDefault());
    }

    public String getDisplayName(Locale locale) {
        int count = 0;
        StringBuilder buffer = new StringBuilder();
        if (!this.languageCode.isEmpty()) {
            String displayLanguage = getDisplayLanguage(locale);
            if (displayLanguage.isEmpty()) {
                displayLanguage = this.languageCode;
            }
            buffer.append(displayLanguage);
            count = 0 + 1;
        }
        if (!this.scriptCode.isEmpty()) {
            if (count == 1) {
                buffer.append(" (");
            }
            String displayScript = getDisplayScript(locale);
            if (displayScript.isEmpty()) {
                displayScript = this.scriptCode;
            }
            buffer.append(displayScript);
            count++;
        }
        if (!this.countryCode.isEmpty()) {
            if (count == 1) {
                buffer.append(" (");
            } else if (count == 2) {
                buffer.append(",");
            }
            String displayCountry = getDisplayCountry(locale);
            if (displayCountry.isEmpty()) {
                displayCountry = this.countryCode;
            }
            buffer.append(displayCountry);
            count++;
        }
        if (!this.variantCode.isEmpty()) {
            if (count == 1) {
                buffer.append(" (");
            } else if (count == 2 || count == 3) {
                buffer.append(",");
            }
            String displayVariant = getDisplayVariant(locale);
            if (displayVariant.isEmpty()) {
                displayVariant = this.variantCode;
            }
            buffer.append(displayVariant);
            count++;
        }
        if (count > 1) {
            buffer.append(")");
        }
        return buffer.toString();
    }

    public final String getDisplayVariant() {
        return getDisplayVariant(getDefault());
    }

    public String getDisplayVariant(Locale locale) {
        if (this.variantCode.isEmpty()) {
            return XmlPullParser.NO_NAMESPACE;
        }
        try {
            Builder.normalizeAndValidateVariant(this.variantCode);
            String result = ICU.getDisplayVariant(this, locale);
            if (result == null) {
                result = ICU.getDisplayVariant(this, getDefault());
            }
            if (result.isEmpty()) {
                return this.variantCode;
            }
            return result;
        } catch (IllformedLocaleException e) {
            return this.variantCode;
        }
    }

    public String getISO3Country() {
        String code = ICU.getISO3Country("en-" + this.countryCode);
        if (this.countryCode.isEmpty() || !code.isEmpty()) {
            return code;
        }
        throw new MissingResourceException("No 3-letter country code for locale: " + this, "FormatData_" + this, "ShortCountry");
    }

    public String getISO3Language() {
        if (this.languageCode.isEmpty()) {
            return XmlPullParser.NO_NAMESPACE;
        }
        String code = ICU.getISO3Language(this.languageCode);
        if (this.languageCode.isEmpty() || !code.isEmpty()) {
            return code;
        }
        throw new MissingResourceException("No 3-letter language code for locale: " + this, "FormatData_" + this, "ShortLanguage");
    }

    public static String[] getISOCountries() {
        return ICU.getISOCountries();
    }

    public static String[] getISOLanguages() {
        return ICU.getISOLanguages();
    }

    public String getLanguage() {
        return this.languageCode;
    }

    public String getVariant() {
        return this.variantCode;
    }

    public String getScript() {
        return this.scriptCode;
    }

    public String getDisplayScript() {
        return getDisplayScript(getDefault());
    }

    public String getDisplayScript(Locale locale) {
        if (this.scriptCode.isEmpty()) {
            return XmlPullParser.NO_NAMESPACE;
        }
        String result = ICU.getDisplayScript(this, locale);
        if (result == null) {
            return ICU.getDisplayScript(this, getDefault());
        }
        return result;
    }

    public String toLanguageTag() {
        if (this.cachedLanguageTag == null) {
            this.cachedLanguageTag = makeLanguageTag();
        }
        return this.cachedLanguageTag;
    }

    private String makeLanguageTag() {
        String language = XmlPullParser.NO_NAMESPACE;
        String region = XmlPullParser.NO_NAMESPACE;
        String variant = XmlPullParser.NO_NAMESPACE;
        String illFormedVariantSubtags = XmlPullParser.NO_NAMESPACE;
        if (this.hasValidatedFields) {
            language = this.languageCode;
            region = this.countryCode;
            variant = this.variantCode.replace('_', '-');
        } else {
            language = Builder.normalizeAndValidateLanguage(this.languageCode, false);
            region = Builder.normalizeAndValidateRegion(this.countryCode, false);
            try {
                variant = Builder.normalizeAndValidateVariant(this.variantCode);
            } catch (IllformedLocaleException e) {
                String[] split = splitIllformedVariant(this.variantCode);
                variant = split[0];
                illFormedVariantSubtags = split[1];
            }
        }
        if (language.isEmpty()) {
            language = UNDETERMINED_LANGUAGE;
        }
        if ("no".equals(language) && "NO".equals(region) && "NY".equals(variant)) {
            language = "nn";
            region = "NO";
            variant = XmlPullParser.NO_NAMESPACE;
        }
        StringBuilder sb = new StringBuilder(16);
        sb.append(language);
        if (!this.scriptCode.isEmpty()) {
            sb.append('-');
            sb.append(this.scriptCode);
        }
        if (!region.isEmpty()) {
            sb.append('-');
            sb.append(region);
        }
        if (!variant.isEmpty()) {
            sb.append('-');
            sb.append(variant);
        }
        for (Entry<Character, String> extension : this.extensions.entrySet()) {
            if (!((Character) extension.getKey()).equals(Character.valueOf(PRIVATE_USE_EXTENSION))) {
                sb.append('-').append(extension.getKey());
                sb.append('-').append((String) extension.getValue());
            }
        }
        String privateUse = (String) this.extensions.get(Character.valueOf(PRIVATE_USE_EXTENSION));
        if (privateUse != null) {
            sb.append("-x-");
            sb.append(privateUse);
        }
        if (!illFormedVariantSubtags.isEmpty()) {
            if (privateUse == null) {
                sb.append("-x-lvariant-");
            } else {
                sb.append('-');
            }
            sb.append(illFormedVariantSubtags);
        }
        return sb.toString();
    }

    private static String[] splitIllformedVariant(String variant) {
        int i;
        String[] subTags = variant.replace('_', '-').split("-");
        String[] split = new String[]{XmlPullParser.NO_NAMESPACE, XmlPullParser.NO_NAMESPACE};
        int firstInvalidSubtag = subTags.length;
        for (i = 0; i < subTags.length; i++) {
            if (!isValidBcp47Alphanum(subTags[i], 1, 8)) {
                firstInvalidSubtag = i;
                break;
            }
        }
        if (firstInvalidSubtag != 0) {
            int firstIllformedSubtag = firstInvalidSubtag;
            for (i = 0; i < firstInvalidSubtag; i++) {
                String subTag = subTags[i];
                if (subTag.length() < 5 || subTag.length() > 8) {
                    if (subTag.length() == 4) {
                        char firstChar = subTag.charAt(0);
                        if (firstChar < '0' || firstChar > '9' || !isAsciiAlphaNum(subTag)) {
                            firstIllformedSubtag = i;
                        }
                    } else {
                        firstIllformedSubtag = i;
                    }
                } else if (!isAsciiAlphaNum(subTag)) {
                    firstIllformedSubtag = i;
                }
            }
            split[0] = concatenateRange(subTags, 0, firstIllformedSubtag);
            split[1] = concatenateRange(subTags, firstIllformedSubtag, firstInvalidSubtag);
        }
        return split;
    }

    private static String concatenateRange(String[] array, int start, int end) {
        StringBuilder builder = new StringBuilder(32);
        for (int i = start; i < end; i++) {
            if (i != start) {
                builder.append('-');
            }
            builder.append(array[i]);
        }
        return builder.toString();
    }

    public Set<Character> getExtensionKeys() {
        return this.extensions.keySet();
    }

    public String getExtension(char extensionKey) {
        return (String) this.extensions.get(Character.valueOf(extensionKey));
    }

    public String getUnicodeLocaleType(String keyWord) {
        return (String) this.unicodeKeywords.get(keyWord);
    }

    public Set<String> getUnicodeLocaleAttributes() {
        return this.unicodeAttributes;
    }

    public Set<String> getUnicodeLocaleKeys() {
        return this.unicodeKeywords.keySet();
    }

    public synchronized int hashCode() {
        return (((this.countryCode.hashCode() + this.languageCode.hashCode()) + this.variantCode.hashCode()) + this.scriptCode.hashCode()) + this.extensions.hashCode();
    }

    public static synchronized void setDefault(Locale locale) {
        synchronized (Locale.class) {
            if (locale == null) {
                throw new NullPointerException("locale == null");
            }
            String languageTag = locale.toLanguageTag();
            defaultLocale = locale;
            ICU.setDefaultLocale(languageTag);
        }
    }

    public final String toString() {
        String result = this.cachedToStringResult;
        if (result != null) {
            return result;
        }
        result = toNewString(this.languageCode, this.countryCode, this.variantCode, this.scriptCode, this.extensions);
        this.cachedToStringResult = result;
        return result;
    }

    private static String toNewString(String languageCode, String countryCode, String variantCode, String scriptCode, Map<Character, String> extensions) {
        if (languageCode.length() == 0 && countryCode.length() == 0) {
            return XmlPullParser.NO_NAMESPACE;
        }
        StringBuilder result = new StringBuilder(11);
        result.append(languageCode);
        boolean hasScriptOrExtensions = (scriptCode.isEmpty() && extensions.isEmpty()) ? false : true;
        if (!(countryCode.isEmpty() && variantCode.isEmpty() && !hasScriptOrExtensions)) {
            result.append('_');
        }
        result.append(countryCode);
        if (!variantCode.isEmpty() || hasScriptOrExtensions) {
            result.append('_');
        }
        result.append(variantCode);
        if (hasScriptOrExtensions) {
            if (!variantCode.isEmpty()) {
                result.append('_');
            }
            result.append("#");
            if (!scriptCode.isEmpty()) {
                result.append(scriptCode);
            }
            if (!extensions.isEmpty()) {
                if (!scriptCode.isEmpty()) {
                    result.append('-');
                }
                result.append(serializeExtensions(extensions));
            }
        }
        return result.toString();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        PutField fields = stream.putFields();
        fields.put("country", this.countryCode);
        fields.put("hashcode", -1);
        fields.put("language", this.languageCode);
        fields.put("variant", this.variantCode);
        fields.put("script", this.scriptCode);
        if (!this.extensions.isEmpty()) {
            fields.put("extensions", serializeExtensions(this.extensions));
        }
        stream.writeFields();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        GetField fields = stream.readFields();
        this.countryCode = (String) fields.get("country", XmlPullParser.NO_NAMESPACE);
        this.languageCode = (String) fields.get("language", XmlPullParser.NO_NAMESPACE);
        this.variantCode = (String) fields.get("variant", XmlPullParser.NO_NAMESPACE);
        this.scriptCode = (String) fields.get("script", XmlPullParser.NO_NAMESPACE);
        this.unicodeKeywords = Collections.EMPTY_MAP;
        this.unicodeAttributes = Collections.EMPTY_SET;
        this.extensions = Collections.EMPTY_MAP;
        String extensions = (String) fields.get("extensions", null);
        if (extensions != null) {
            readExtensions(extensions);
        }
    }

    private void readExtensions(String extensions) {
        Map<Character, String> extensionsMap = new TreeMap();
        parseSerializedExtensions(extensions, extensionsMap);
        this.extensions = Collections.unmodifiableMap(extensionsMap);
        if (extensionsMap.containsKey(Character.valueOf(UNICODE_LOCALE_EXTENSION))) {
            String[] subTags = ((String) extensionsMap.get(Character.valueOf(UNICODE_LOCALE_EXTENSION))).split("-");
            Map<String, String> unicodeKeywords = new TreeMap();
            Set<String> unicodeAttributes = new TreeSet();
            parseUnicodeExtension(subTags, unicodeKeywords, unicodeAttributes);
            this.unicodeKeywords = Collections.unmodifiableMap(unicodeKeywords);
            this.unicodeAttributes = Collections.unmodifiableSet(unicodeAttributes);
        }
    }

    public static String serializeExtensions(Map<Character, String> extensionsMap) {
        Iterator<Entry<Character, String>> entryIterator = extensionsMap.entrySet().iterator();
        StringBuilder sb = new StringBuilder(64);
        while (true) {
            Entry<Character, String> entry = (Entry) entryIterator.next();
            sb.append(entry.getKey());
            sb.append('-');
            sb.append((String) entry.getValue());
            if (!entryIterator.hasNext()) {
                return sb.toString();
            }
            sb.append('-');
        }
    }

    public static void parseSerializedExtensions(String extString, Map<Character, String> outputMap) {
        String[] subTags = extString.split("-");
        int[] typeStartIndices = new int[(subTags.length / 2)];
        int length = 0;
        String[] arr$ = subTags;
        int len$ = arr$.length;
        int i$ = 0;
        int count = 0;
        while (i$ < len$) {
            int count2;
            String subTag = arr$[i$];
            if (subTag.length() > 0) {
                length += subTag.length() + 1;
            }
            if (subTag.length() == 1) {
                count2 = count + 1;
                typeStartIndices[count] = length;
            } else {
                count2 = count;
            }
            i$++;
            count = count2;
        }
        int i = 0;
        while (i < count) {
            outputMap.put(Character.valueOf(extString.charAt(typeStartIndices[i] - 2)), extString.substring(typeStartIndices[i], i == count + -1 ? extString.length() : typeStartIndices[i + 1] - 3));
            i++;
        }
    }

    private static boolean isUnM49AreaCode(String code) {
        if (code.length() != 3) {
            return false;
        }
        for (int i = 0; i < 3; i++) {
            char character = code.charAt(i);
            if (character < '0' || character > '9') {
                return false;
            }
        }
        return true;
    }

    private static boolean isAsciiAlphaNum(String string) {
        for (int i = 0; i < string.length(); i++) {
            char character = string.charAt(i);
            if ((character < 'a' || character > 'z') && ((character < 'A' || character > 'Z') && (character < '0' || character > '9'))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidBcp47Alpha(String string, int lowerBound, int upperBound) {
        int length = string.length();
        if (length < lowerBound || length > upperBound) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            char character = string.charAt(i);
            if ((character < 'a' || character > 'z') && (character < 'A' || character > 'Z')) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValidBcp47Alphanum(String attributeOrType, int lowerBound, int upperBound) {
        if (attributeOrType.length() < lowerBound || attributeOrType.length() > upperBound) {
            return false;
        }
        return isAsciiAlphaNum(attributeOrType);
    }

    private static String titleCaseAsciiWord(String word) {
        try {
            byte[] chars = word.toLowerCase(ROOT).getBytes(StandardCharsets.US_ASCII);
            chars[0] = (byte) ((chars[0] + 65) - 97);
            return new String(chars, StandardCharsets.US_ASCII);
        } catch (Object uoe) {
            throw new AssertionError(uoe);
        }
    }

    private static boolean isValidTypeList(String lowerCaseTypeList) {
        for (String type : lowerCaseTypeList.split("-")) {
            if (!isValidBcp47Alphanum(type, 3, 8)) {
                return false;
            }
        }
        return true;
    }

    private static void addUnicodeExtensionToExtensionsMap(Set<String> attributes, Map<String, String> keywords, Map<Character, String> extensions) {
        if (!attributes.isEmpty() || !keywords.isEmpty()) {
            StringBuilder sb = new StringBuilder(32);
            if (!attributes.isEmpty()) {
                Iterator<String> attributesIterator = attributes.iterator();
                while (true) {
                    sb.append((String) attributesIterator.next());
                    if (!attributesIterator.hasNext()) {
                        break;
                    }
                    sb.append('-');
                }
            }
            if (!keywords.isEmpty()) {
                if (!attributes.isEmpty()) {
                    sb.append('-');
                }
                Iterator<Entry<String, String>> keywordsIterator = keywords.entrySet().iterator();
                while (true) {
                    Entry<String, String> keyWord = (Entry) keywordsIterator.next();
                    sb.append((String) keyWord.getKey());
                    if (!((String) keyWord.getValue()).isEmpty()) {
                        sb.append('-');
                        sb.append((String) keyWord.getValue());
                    }
                    if (!keywordsIterator.hasNext()) {
                        break;
                    }
                    sb.append('-');
                }
            }
            extensions.put(Character.valueOf(UNICODE_LOCALE_EXTENSION), sb.toString());
        }
    }

    public static void parseUnicodeExtension(String[] subtags, Map<String, String> keywords, Set<String> attributes) {
        String lastKeyword = null;
        List<String> subtagsForKeyword = new ArrayList();
        for (String subtag : subtags) {
            if (subtag.length() == 2) {
                if (subtagsForKeyword.size() > 0) {
                    keywords.put(lastKeyword, joinBcp47Subtags(subtagsForKeyword));
                    subtagsForKeyword.clear();
                }
                lastKeyword = subtag;
            } else if (subtag.length() > 2) {
                if (lastKeyword == null) {
                    attributes.add(subtag);
                } else {
                    subtagsForKeyword.add(subtag);
                }
            }
        }
        if (subtagsForKeyword.size() > 0) {
            keywords.put(lastKeyword, joinBcp47Subtags(subtagsForKeyword));
        } else if (lastKeyword != null) {
            keywords.put(lastKeyword, XmlPullParser.NO_NAMESPACE);
        }
    }

    private static String joinBcp47Subtags(List<String> strings) {
        int size = strings.size();
        StringBuilder sb = new StringBuilder(((String) strings.get(0)).length());
        for (int i = 0; i < size; i++) {
            sb.append((String) strings.get(i));
            if (i != size - 1) {
                sb.append('-');
            }
        }
        return sb.toString();
    }

    public static String adjustLanguageCode(String languageCode) {
        String adjusted = languageCode.toLowerCase(US);
        if (languageCode.equals("he")) {
            return "iw";
        }
        if (languageCode.equals("id")) {
            return "in";
        }
        if (languageCode.equals("yi")) {
            return "ji";
        }
        return adjusted;
    }

    private static String convertGrandfatheredTag(String original) {
        String converted = (String) GRANDFATHERED_LOCALES.get(original);
        return converted != null ? converted : original;
    }

    private static void extractVariantSubtags(String[] subtags, int startIndex, int endIndex, List<String> normalizedVariants) {
        int i = startIndex;
        while (i < endIndex) {
            String subtag = subtags[i];
            if (Builder.isValidVariantSubtag(subtag)) {
                normalizedVariants.add(subtag);
                i++;
            } else {
                return;
            }
        }
    }

    private static int extractExtensions(String[] subtags, int startIndex, int endIndex, Map<Character, String> extensions) {
        int privateUseExtensionIndex = -1;
        int extensionKeyIndex = -1;
        int i = startIndex;
        while (i < endIndex) {
            boolean parsingPrivateUse;
            String key;
            String subtag = subtags[i];
            if (privateUseExtensionIndex == -1 || extensionKeyIndex != privateUseExtensionIndex) {
                parsingPrivateUse = false;
            } else {
                parsingPrivateUse = true;
            }
            if (subtag.length() == 1 && !parsingPrivateUse) {
                if (extensionKeyIndex != -1) {
                    if (i - 1 == extensionKeyIndex) {
                        return extensionKeyIndex;
                    }
                    key = subtags[extensionKeyIndex];
                    if (extensions.containsKey(Character.valueOf(key.charAt(0)))) {
                        return extensionKeyIndex;
                    }
                    extensions.put(Character.valueOf(key.charAt(0)), concatenateRange(subtags, extensionKeyIndex + 1, i).toLowerCase(ROOT));
                }
                extensionKeyIndex = i;
                if ("x".equals(subtag)) {
                    privateUseExtensionIndex = i;
                } else if (privateUseExtensionIndex != -1) {
                    return privateUseExtensionIndex;
                }
            } else if (extensionKeyIndex == -1) {
                return i;
            } else {
                if (!isValidBcp47Alphanum(subtag, parsingPrivateUse ? 1 : 2, 8)) {
                    return i;
                }
            }
            i++;
        }
        if (extensionKeyIndex == -1) {
            return i;
        }
        if (i - 1 == extensionKeyIndex) {
            return extensionKeyIndex;
        }
        key = subtags[extensionKeyIndex];
        if (extensions.containsKey(Character.valueOf(key.charAt(0)))) {
            return extensionKeyIndex;
        }
        extensions.put(Character.valueOf(key.charAt(0)), concatenateRange(subtags, extensionKeyIndex + 1, i).toLowerCase(ROOT));
        return i;
    }

    private static Locale forLanguageTag(String tag, boolean strict) {
        String languageCode;
        String scriptCode;
        int nextSubtag;
        String regionCode;
        List<String> variants;
        Map<Character, String> extensions;
        Set<String> unicodeKeywords;
        Map<String, String> unicodeAttributes;
        String variantCode;
        StringBuilder stringBuilder;
        String[] subtags = convertGrandfatheredTag(tag).split("-");
        int lastSubtag = subtags.length;
        int i = 0;
        while (i < subtags.length) {
            String subtag = subtags[i];
            if (!subtag.isEmpty() && subtag.length() <= 8) {
                i++;
            } else if (strict) {
                throw new IllformedLocaleException("Invalid subtag at index: " + i + " in tag: " + tag);
            } else {
                lastSubtag = i - 1;
                languageCode = Builder.normalizeAndValidateLanguage(subtags[0], strict);
                scriptCode = XmlPullParser.NO_NAMESPACE;
                nextSubtag = 1;
                if (lastSubtag > 1) {
                    scriptCode = Builder.normalizeAndValidateScript(subtags[1], false);
                    if (!scriptCode.isEmpty()) {
                        nextSubtag = 1 + 1;
                    }
                }
                regionCode = XmlPullParser.NO_NAMESPACE;
                if (lastSubtag > nextSubtag) {
                    regionCode = Builder.normalizeAndValidateRegion(subtags[nextSubtag], false);
                    if (!regionCode.isEmpty()) {
                        nextSubtag++;
                    }
                }
                variants = null;
                if (lastSubtag > nextSubtag) {
                    variants = new ArrayList();
                    extractVariantSubtags(subtags, nextSubtag, lastSubtag, variants);
                    nextSubtag += variants.size();
                }
                extensions = Collections.EMPTY_MAP;
                if (lastSubtag > nextSubtag) {
                    extensions = new TreeMap();
                    nextSubtag = extractExtensions(subtags, nextSubtag, lastSubtag, extensions);
                }
                if (nextSubtag == lastSubtag && strict) {
                    String str = "Unparseable subtag: ";
                    throw new IllformedLocaleException(r18 + subtags[nextSubtag] + " from language tag: " + tag);
                }
                unicodeKeywords = Collections.EMPTY_SET;
                unicodeAttributes = Collections.EMPTY_MAP;
                if (extensions.containsKey(Character.valueOf(UNICODE_LOCALE_EXTENSION))) {
                    unicodeKeywords = new TreeSet();
                    unicodeAttributes = new TreeMap();
                    parseUnicodeExtension(((String) extensions.get(Character.valueOf(UNICODE_LOCALE_EXTENSION))).split("-"), unicodeAttributes, unicodeKeywords);
                }
                variantCode = XmlPullParser.NO_NAMESPACE;
                if (!(variants == null || variants.isEmpty())) {
                    stringBuilder = new StringBuilder(variants.size() * 8);
                    for (i = 0; i < variants.size(); i++) {
                        if (i != 0) {
                            stringBuilder.append('_');
                        }
                        stringBuilder.append((String) variants.get(i));
                    }
                    variantCode = stringBuilder.toString();
                }
                return new Locale(languageCode, regionCode, variantCode, scriptCode, unicodeKeywords, unicodeAttributes, extensions, true);
            }
        }
        languageCode = Builder.normalizeAndValidateLanguage(subtags[0], strict);
        scriptCode = XmlPullParser.NO_NAMESPACE;
        nextSubtag = 1;
        if (lastSubtag > 1) {
            scriptCode = Builder.normalizeAndValidateScript(subtags[1], false);
            if (scriptCode.isEmpty()) {
                nextSubtag = 1 + 1;
            }
        }
        regionCode = XmlPullParser.NO_NAMESPACE;
        if (lastSubtag > nextSubtag) {
            regionCode = Builder.normalizeAndValidateRegion(subtags[nextSubtag], false);
            if (regionCode.isEmpty()) {
                nextSubtag++;
            }
        }
        variants = null;
        if (lastSubtag > nextSubtag) {
            variants = new ArrayList();
            extractVariantSubtags(subtags, nextSubtag, lastSubtag, variants);
            nextSubtag += variants.size();
        }
        extensions = Collections.EMPTY_MAP;
        if (lastSubtag > nextSubtag) {
            extensions = new TreeMap();
            nextSubtag = extractExtensions(subtags, nextSubtag, lastSubtag, extensions);
        }
        if (nextSubtag == lastSubtag) {
        }
        unicodeKeywords = Collections.EMPTY_SET;
        unicodeAttributes = Collections.EMPTY_MAP;
        if (extensions.containsKey(Character.valueOf(UNICODE_LOCALE_EXTENSION))) {
            unicodeKeywords = new TreeSet();
            unicodeAttributes = new TreeMap();
            parseUnicodeExtension(((String) extensions.get(Character.valueOf(UNICODE_LOCALE_EXTENSION))).split("-"), unicodeAttributes, unicodeKeywords);
        }
        variantCode = XmlPullParser.NO_NAMESPACE;
        stringBuilder = new StringBuilder(variants.size() * 8);
        for (i = 0; i < variants.size(); i++) {
            if (i != 0) {
                stringBuilder.append('_');
            }
            stringBuilder.append((String) variants.get(i));
        }
        variantCode = stringBuilder.toString();
        return new Locale(languageCode, regionCode, variantCode, scriptCode, unicodeKeywords, unicodeAttributes, extensions, true);
    }
}
