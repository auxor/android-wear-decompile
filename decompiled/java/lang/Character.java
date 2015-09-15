package java.lang;

import com.android.dex.DexFormat;
import dalvik.bytecode.Opcodes;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.util.Arrays;
import libcore.icu.DateIntervalFormat;
import org.apache.harmony.security.asn1.ASN1Constants;
import org.w3c.dom.traversal.NodeFilter;

@FindBugsSuppressWarnings({"DM_NUMBER_CTOR"})
public final class Character implements Serializable, Comparable<Character> {
    public static final byte COMBINING_SPACING_MARK = (byte) 8;
    public static final byte CONNECTOR_PUNCTUATION = (byte) 23;
    public static final byte CONTROL = (byte) 15;
    public static final byte CURRENCY_SYMBOL = (byte) 26;
    public static final byte DASH_PUNCTUATION = (byte) 20;
    public static final byte DECIMAL_DIGIT_NUMBER = (byte) 9;
    private static final byte[] DIRECTIONALITY = null;
    public static final byte DIRECTIONALITY_ARABIC_NUMBER = (byte) 6;
    public static final byte DIRECTIONALITY_BOUNDARY_NEUTRAL = (byte) 9;
    public static final byte DIRECTIONALITY_COMMON_NUMBER_SEPARATOR = (byte) 7;
    public static final byte DIRECTIONALITY_EUROPEAN_NUMBER = (byte) 3;
    public static final byte DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR = (byte) 4;
    public static final byte DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR = (byte) 5;
    public static final byte DIRECTIONALITY_LEFT_TO_RIGHT = (byte) 0;
    public static final byte DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING = (byte) 14;
    public static final byte DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE = (byte) 15;
    public static final byte DIRECTIONALITY_NONSPACING_MARK = (byte) 8;
    public static final byte DIRECTIONALITY_OTHER_NEUTRALS = (byte) 13;
    public static final byte DIRECTIONALITY_PARAGRAPH_SEPARATOR = (byte) 10;
    public static final byte DIRECTIONALITY_POP_DIRECTIONAL_FORMAT = (byte) 18;
    public static final byte DIRECTIONALITY_RIGHT_TO_LEFT = (byte) 1;
    public static final byte DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC = (byte) 2;
    public static final byte DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING = (byte) 16;
    public static final byte DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE = (byte) 17;
    public static final byte DIRECTIONALITY_SEGMENT_SEPARATOR = (byte) 11;
    public static final byte DIRECTIONALITY_UNDEFINED = (byte) -1;
    public static final byte DIRECTIONALITY_WHITESPACE = (byte) 12;
    public static final byte ENCLOSING_MARK = (byte) 7;
    public static final byte END_PUNCTUATION = (byte) 22;
    public static final byte FINAL_QUOTE_PUNCTUATION = (byte) 30;
    public static final byte FORMAT = (byte) 16;
    public static final byte INITIAL_QUOTE_PUNCTUATION = (byte) 29;
    public static final byte LETTER_NUMBER = (byte) 10;
    public static final byte LINE_SEPARATOR = (byte) 13;
    public static final byte LOWERCASE_LETTER = (byte) 2;
    public static final byte MATH_SYMBOL = (byte) 25;
    public static final int MAX_CODE_POINT = 1114111;
    public static final char MAX_HIGH_SURROGATE = '\udbff';
    public static final char MAX_LOW_SURROGATE = '\udfff';
    public static final int MAX_RADIX = 36;
    public static final char MAX_SURROGATE = '\udfff';
    public static final char MAX_VALUE = '\uffff';
    public static final int MIN_CODE_POINT = 0;
    public static final char MIN_HIGH_SURROGATE = '\ud800';
    public static final char MIN_LOW_SURROGATE = '\udc00';
    public static final int MIN_RADIX = 2;
    public static final int MIN_SUPPLEMENTARY_CODE_POINT = 65536;
    public static final char MIN_SURROGATE = '\ud800';
    public static final char MIN_VALUE = '\u0000';
    public static final byte MODIFIER_LETTER = (byte) 4;
    public static final byte MODIFIER_SYMBOL = (byte) 27;
    public static final byte NON_SPACING_MARK = (byte) 6;
    public static final byte OTHER_LETTER = (byte) 5;
    public static final byte OTHER_NUMBER = (byte) 11;
    public static final byte OTHER_PUNCTUATION = (byte) 24;
    public static final byte OTHER_SYMBOL = (byte) 28;
    public static final byte PARAGRAPH_SEPARATOR = (byte) 14;
    public static final byte PRIVATE_USE = (byte) 18;
    public static final int SIZE = 16;
    private static final Character[] SMALL_VALUES = null;
    public static final byte SPACE_SEPARATOR = (byte) 12;
    public static final byte START_PUNCTUATION = (byte) 21;
    public static final byte SURROGATE = (byte) 19;
    public static final byte TITLECASE_LETTER = (byte) 3;
    public static final Class<Character> TYPE = null;
    public static final byte UNASSIGNED = (byte) 0;
    public static final byte UPPERCASE_LETTER = (byte) 1;
    private static final long serialVersionUID = 3786198910865385080L;
    private final char value;

    public static class Subset {
        private final String name;

        protected Subset(String name) {
            if (name == null) {
                throw new NullPointerException("name == null");
            }
            this.name = name;
        }

        public final boolean equals(Object object) {
            return object == this;
        }

        public final int hashCode() {
            return super.hashCode();
        }

        public final String toString() {
            return this.name;
        }
    }

    public static final class UnicodeBlock extends Subset {
        public static final UnicodeBlock AEGEAN_NUMBERS;
        public static final UnicodeBlock ALCHEMICAL_SYMBOLS;
        public static final UnicodeBlock ALPHABETIC_PRESENTATION_FORMS;
        public static final UnicodeBlock ANCIENT_GREEK_MUSICAL_NOTATION;
        public static final UnicodeBlock ANCIENT_GREEK_NUMBERS;
        public static final UnicodeBlock ANCIENT_SYMBOLS;
        public static final UnicodeBlock ARABIC;
        public static final UnicodeBlock ARABIC_PRESENTATION_FORMS_A;
        public static final UnicodeBlock ARABIC_PRESENTATION_FORMS_B;
        public static final UnicodeBlock ARABIC_SUPPLEMENT;
        public static final UnicodeBlock ARMENIAN;
        public static final UnicodeBlock ARROWS;
        public static final UnicodeBlock AVESTAN;
        public static final UnicodeBlock BALINESE;
        public static final UnicodeBlock BAMUM;
        public static final UnicodeBlock BAMUM_SUPPLEMENT;
        public static final UnicodeBlock BASIC_LATIN;
        public static final UnicodeBlock BATAK;
        public static final UnicodeBlock BENGALI;
        private static UnicodeBlock[] BLOCKS;
        public static final UnicodeBlock BLOCK_ELEMENTS;
        public static final UnicodeBlock BOPOMOFO;
        public static final UnicodeBlock BOPOMOFO_EXTENDED;
        public static final UnicodeBlock BOX_DRAWING;
        public static final UnicodeBlock BRAHMI;
        public static final UnicodeBlock BRAILLE_PATTERNS;
        public static final UnicodeBlock BUGINESE;
        public static final UnicodeBlock BUHID;
        public static final UnicodeBlock BYZANTINE_MUSICAL_SYMBOLS;
        public static final UnicodeBlock CARIAN;
        public static final UnicodeBlock CHAM;
        public static final UnicodeBlock CHEROKEE;
        public static final UnicodeBlock CJK_COMPATIBILITY;
        public static final UnicodeBlock CJK_COMPATIBILITY_FORMS;
        public static final UnicodeBlock CJK_COMPATIBILITY_IDEOGRAPHS;
        public static final UnicodeBlock CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT;
        public static final UnicodeBlock CJK_RADICALS_SUPPLEMENT;
        public static final UnicodeBlock CJK_STROKES;
        public static final UnicodeBlock CJK_SYMBOLS_AND_PUNCTUATION;
        public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS;
        public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A;
        public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B;
        public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C;
        public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D;
        public static final UnicodeBlock COMBINING_DIACRITICAL_MARKS;
        public static final UnicodeBlock COMBINING_DIACRITICAL_MARKS_SUPPLEMENT;
        public static final UnicodeBlock COMBINING_HALF_MARKS;
        public static final UnicodeBlock COMBINING_MARKS_FOR_SYMBOLS;
        public static final UnicodeBlock COMMON_INDIC_NUMBER_FORMS;
        public static final UnicodeBlock CONTROL_PICTURES;
        public static final UnicodeBlock COPTIC;
        public static final UnicodeBlock COUNTING_ROD_NUMERALS;
        public static final UnicodeBlock CUNEIFORM;
        public static final UnicodeBlock CUNEIFORM_NUMBERS_AND_PUNCTUATION;
        public static final UnicodeBlock CURRENCY_SYMBOLS;
        public static final UnicodeBlock CYPRIOT_SYLLABARY;
        public static final UnicodeBlock CYRILLIC;
        public static final UnicodeBlock CYRILLIC_EXTENDED_A;
        public static final UnicodeBlock CYRILLIC_EXTENDED_B;
        public static final UnicodeBlock CYRILLIC_SUPPLEMENTARY;
        public static final UnicodeBlock DESERET;
        public static final UnicodeBlock DEVANAGARI;
        public static final UnicodeBlock DEVANAGARI_EXTENDED;
        public static final UnicodeBlock DINGBATS;
        public static final UnicodeBlock DOMINO_TILES;
        public static final UnicodeBlock EGYPTIAN_HIEROGLYPHS;
        public static final UnicodeBlock EMOTICONS;
        public static final UnicodeBlock ENCLOSED_ALPHANUMERICS;
        public static final UnicodeBlock ENCLOSED_ALPHANUMERIC_SUPPLEMENT;
        public static final UnicodeBlock ENCLOSED_CJK_LETTERS_AND_MONTHS;
        public static final UnicodeBlock ENCLOSED_IDEOGRAPHIC_SUPPLEMENT;
        public static final UnicodeBlock ETHIOPIC;
        public static final UnicodeBlock ETHIOPIC_EXTENDED;
        public static final UnicodeBlock ETHIOPIC_EXTENDED_A;
        public static final UnicodeBlock ETHIOPIC_SUPPLEMENT;
        public static final UnicodeBlock GENERAL_PUNCTUATION;
        public static final UnicodeBlock GEOMETRIC_SHAPES;
        public static final UnicodeBlock GEORGIAN;
        public static final UnicodeBlock GEORGIAN_SUPPLEMENT;
        public static final UnicodeBlock GLAGOLITIC;
        public static final UnicodeBlock GOTHIC;
        public static final UnicodeBlock GREEK;
        public static final UnicodeBlock GREEK_EXTENDED;
        public static final UnicodeBlock GUJARATI;
        public static final UnicodeBlock GURMUKHI;
        public static final UnicodeBlock HALFWIDTH_AND_FULLWIDTH_FORMS;
        public static final UnicodeBlock HANGUL_COMPATIBILITY_JAMO;
        public static final UnicodeBlock HANGUL_JAMO;
        public static final UnicodeBlock HANGUL_JAMO_EXTENDED_A;
        public static final UnicodeBlock HANGUL_JAMO_EXTENDED_B;
        public static final UnicodeBlock HANGUL_SYLLABLES;
        public static final UnicodeBlock HANUNOO;
        public static final UnicodeBlock HEBREW;
        public static final UnicodeBlock HIGH_PRIVATE_USE_SURROGATES;
        public static final UnicodeBlock HIGH_SURROGATES;
        public static final UnicodeBlock HIRAGANA;
        public static final UnicodeBlock IDEOGRAPHIC_DESCRIPTION_CHARACTERS;
        public static final UnicodeBlock IMPERIAL_ARAMAIC;
        public static final UnicodeBlock INSCRIPTIONAL_PAHLAVI;
        public static final UnicodeBlock INSCRIPTIONAL_PARTHIAN;
        public static final UnicodeBlock IPA_EXTENSIONS;
        public static final UnicodeBlock JAVANESE;
        public static final UnicodeBlock KAITHI;
        public static final UnicodeBlock KANA_SUPPLEMENT;
        public static final UnicodeBlock KANBUN;
        public static final UnicodeBlock KANGXI_RADICALS;
        public static final UnicodeBlock KANNADA;
        public static final UnicodeBlock KATAKANA;
        public static final UnicodeBlock KATAKANA_PHONETIC_EXTENSIONS;
        public static final UnicodeBlock KAYAH_LI;
        public static final UnicodeBlock KHAROSHTHI;
        public static final UnicodeBlock KHMER;
        public static final UnicodeBlock KHMER_SYMBOLS;
        public static final UnicodeBlock LAO;
        public static final UnicodeBlock LATIN_1_SUPPLEMENT;
        public static final UnicodeBlock LATIN_EXTENDED_A;
        public static final UnicodeBlock LATIN_EXTENDED_ADDITIONAL;
        public static final UnicodeBlock LATIN_EXTENDED_B;
        public static final UnicodeBlock LATIN_EXTENDED_C;
        public static final UnicodeBlock LATIN_EXTENDED_D;
        public static final UnicodeBlock LEPCHA;
        public static final UnicodeBlock LETTERLIKE_SYMBOLS;
        public static final UnicodeBlock LIMBU;
        public static final UnicodeBlock LINEAR_B_IDEOGRAMS;
        public static final UnicodeBlock LINEAR_B_SYLLABARY;
        public static final UnicodeBlock LISU;
        public static final UnicodeBlock LOW_SURROGATES;
        public static final UnicodeBlock LYCIAN;
        public static final UnicodeBlock LYDIAN;
        public static final UnicodeBlock MAHJONG_TILES;
        public static final UnicodeBlock MALAYALAM;
        public static final UnicodeBlock MANDAIC;
        public static final UnicodeBlock MATHEMATICAL_ALPHANUMERIC_SYMBOLS;
        public static final UnicodeBlock MATHEMATICAL_OPERATORS;
        public static final UnicodeBlock MEETEI_MAYEK;
        public static final UnicodeBlock MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A;
        public static final UnicodeBlock MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B;
        public static final UnicodeBlock MISCELLANEOUS_SYMBOLS;
        public static final UnicodeBlock MISCELLANEOUS_SYMBOLS_AND_ARROWS;
        public static final UnicodeBlock MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS;
        public static final UnicodeBlock MISCELLANEOUS_TECHNICAL;
        public static final UnicodeBlock MODIFIER_TONE_LETTERS;
        public static final UnicodeBlock MONGOLIAN;
        public static final UnicodeBlock MUSICAL_SYMBOLS;
        public static final UnicodeBlock MYANMAR;
        public static final UnicodeBlock MYANMAR_EXTENDED_A;
        public static final UnicodeBlock NEW_TAI_LUE;
        public static final UnicodeBlock NKO;
        public static final UnicodeBlock NUMBER_FORMS;
        public static final UnicodeBlock OGHAM;
        public static final UnicodeBlock OLD_ITALIC;
        public static final UnicodeBlock OLD_PERSIAN;
        public static final UnicodeBlock OLD_SOUTH_ARABIAN;
        public static final UnicodeBlock OLD_TURKIC;
        public static final UnicodeBlock OL_CHIKI;
        public static final UnicodeBlock OPTICAL_CHARACTER_RECOGNITION;
        public static final UnicodeBlock ORIYA;
        public static final UnicodeBlock OSMANYA;
        public static final UnicodeBlock PHAGS_PA;
        public static final UnicodeBlock PHAISTOS_DISC;
        public static final UnicodeBlock PHOENICIAN;
        public static final UnicodeBlock PHONETIC_EXTENSIONS;
        public static final UnicodeBlock PHONETIC_EXTENSIONS_SUPPLEMENT;
        public static final UnicodeBlock PLAYING_CARDS;
        public static final UnicodeBlock PRIVATE_USE_AREA;
        public static final UnicodeBlock REJANG;
        public static final UnicodeBlock RUMI_NUMERAL_SYMBOLS;
        public static final UnicodeBlock RUNIC;
        public static final UnicodeBlock SAMARITAN;
        public static final UnicodeBlock SAURASHTRA;
        public static final UnicodeBlock SHAVIAN;
        public static final UnicodeBlock SINHALA;
        public static final UnicodeBlock SMALL_FORM_VARIANTS;
        public static final UnicodeBlock SPACING_MODIFIER_LETTERS;
        public static final UnicodeBlock SPECIALS;
        public static final UnicodeBlock SUNDANESE;
        public static final UnicodeBlock SUPERSCRIPTS_AND_SUBSCRIPTS;
        public static final UnicodeBlock SUPPLEMENTAL_ARROWS_A;
        public static final UnicodeBlock SUPPLEMENTAL_ARROWS_B;
        public static final UnicodeBlock SUPPLEMENTAL_MATHEMATICAL_OPERATORS;
        public static final UnicodeBlock SUPPLEMENTAL_PUNCTUATION;
        public static final UnicodeBlock SUPPLEMENTARY_PRIVATE_USE_AREA_A;
        public static final UnicodeBlock SUPPLEMENTARY_PRIVATE_USE_AREA_B;
        @Deprecated
        public static final UnicodeBlock SURROGATES_AREA;
        public static final UnicodeBlock SYLOTI_NAGRI;
        public static final UnicodeBlock SYRIAC;
        public static final UnicodeBlock TAGALOG;
        public static final UnicodeBlock TAGBANWA;
        public static final UnicodeBlock TAGS;
        public static final UnicodeBlock TAI_LE;
        public static final UnicodeBlock TAI_THAM;
        public static final UnicodeBlock TAI_VIET;
        public static final UnicodeBlock TAI_XUAN_JING_SYMBOLS;
        public static final UnicodeBlock TAMIL;
        public static final UnicodeBlock TELUGU;
        public static final UnicodeBlock THAANA;
        public static final UnicodeBlock THAI;
        public static final UnicodeBlock TIBETAN;
        public static final UnicodeBlock TIFINAGH;
        public static final UnicodeBlock TRANSPORT_AND_MAP_SYMBOLS;
        public static final UnicodeBlock UGARITIC;
        public static final UnicodeBlock UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS;
        public static final UnicodeBlock UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS_EXTENDED;
        public static final UnicodeBlock VAI;
        public static final UnicodeBlock VARIATION_SELECTORS;
        public static final UnicodeBlock VARIATION_SELECTORS_SUPPLEMENT;
        public static final UnicodeBlock VEDIC_EXTENSIONS;
        public static final UnicodeBlock VERTICAL_FORMS;
        public static final UnicodeBlock YIJING_HEXAGRAM_SYMBOLS;
        public static final UnicodeBlock YI_RADICALS;
        public static final UnicodeBlock YI_SYLLABLES;

        static {
            SURROGATES_AREA = new UnicodeBlock("SURROGATES_AREA");
            BASIC_LATIN = new UnicodeBlock("BASIC_LATIN");
            LATIN_1_SUPPLEMENT = new UnicodeBlock("LATIN_1_SUPPLEMENT");
            LATIN_EXTENDED_A = new UnicodeBlock("LATIN_EXTENDED_A");
            LATIN_EXTENDED_B = new UnicodeBlock("LATIN_EXTENDED_B");
            IPA_EXTENSIONS = new UnicodeBlock("IPA_EXTENSIONS");
            SPACING_MODIFIER_LETTERS = new UnicodeBlock("SPACING_MODIFIER_LETTERS");
            COMBINING_DIACRITICAL_MARKS = new UnicodeBlock("COMBINING_DIACRITICAL_MARKS");
            GREEK = new UnicodeBlock("GREEK");
            CYRILLIC = new UnicodeBlock("CYRILLIC");
            CYRILLIC_SUPPLEMENTARY = new UnicodeBlock("CYRILLIC_SUPPLEMENTARY");
            ARMENIAN = new UnicodeBlock("ARMENIAN");
            HEBREW = new UnicodeBlock("HEBREW");
            ARABIC = new UnicodeBlock("ARABIC");
            SYRIAC = new UnicodeBlock("SYRIAC");
            THAANA = new UnicodeBlock("THAANA");
            DEVANAGARI = new UnicodeBlock("DEVANAGARI");
            BENGALI = new UnicodeBlock("BENGALI");
            GURMUKHI = new UnicodeBlock("GURMUKHI");
            GUJARATI = new UnicodeBlock("GUJARATI");
            ORIYA = new UnicodeBlock("ORIYA");
            TAMIL = new UnicodeBlock("TAMIL");
            TELUGU = new UnicodeBlock("TELUGU");
            KANNADA = new UnicodeBlock("KANNADA");
            MALAYALAM = new UnicodeBlock("MALAYALAM");
            SINHALA = new UnicodeBlock("SINHALA");
            THAI = new UnicodeBlock("THAI");
            LAO = new UnicodeBlock("LAO");
            TIBETAN = new UnicodeBlock("TIBETAN");
            MYANMAR = new UnicodeBlock("MYANMAR");
            GEORGIAN = new UnicodeBlock("GEORGIAN");
            HANGUL_JAMO = new UnicodeBlock("HANGUL_JAMO");
            ETHIOPIC = new UnicodeBlock("ETHIOPIC");
            CHEROKEE = new UnicodeBlock("CHEROKEE");
            UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS = new UnicodeBlock("UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS");
            OGHAM = new UnicodeBlock("OGHAM");
            RUNIC = new UnicodeBlock("RUNIC");
            TAGALOG = new UnicodeBlock("TAGALOG");
            HANUNOO = new UnicodeBlock("HANUNOO");
            BUHID = new UnicodeBlock("BUHID");
            TAGBANWA = new UnicodeBlock("TAGBANWA");
            KHMER = new UnicodeBlock("KHMER");
            MONGOLIAN = new UnicodeBlock("MONGOLIAN");
            LIMBU = new UnicodeBlock("LIMBU");
            TAI_LE = new UnicodeBlock("TAI_LE");
            KHMER_SYMBOLS = new UnicodeBlock("KHMER_SYMBOLS");
            PHONETIC_EXTENSIONS = new UnicodeBlock("PHONETIC_EXTENSIONS");
            LATIN_EXTENDED_ADDITIONAL = new UnicodeBlock("LATIN_EXTENDED_ADDITIONAL");
            GREEK_EXTENDED = new UnicodeBlock("GREEK_EXTENDED");
            GENERAL_PUNCTUATION = new UnicodeBlock("GENERAL_PUNCTUATION");
            SUPERSCRIPTS_AND_SUBSCRIPTS = new UnicodeBlock("SUPERSCRIPTS_AND_SUBSCRIPTS");
            CURRENCY_SYMBOLS = new UnicodeBlock("CURRENCY_SYMBOLS");
            COMBINING_MARKS_FOR_SYMBOLS = new UnicodeBlock("COMBINING_MARKS_FOR_SYMBOLS");
            LETTERLIKE_SYMBOLS = new UnicodeBlock("LETTERLIKE_SYMBOLS");
            NUMBER_FORMS = new UnicodeBlock("NUMBER_FORMS");
            ARROWS = new UnicodeBlock("ARROWS");
            MATHEMATICAL_OPERATORS = new UnicodeBlock("MATHEMATICAL_OPERATORS");
            MISCELLANEOUS_TECHNICAL = new UnicodeBlock("MISCELLANEOUS_TECHNICAL");
            CONTROL_PICTURES = new UnicodeBlock("CONTROL_PICTURES");
            OPTICAL_CHARACTER_RECOGNITION = new UnicodeBlock("OPTICAL_CHARACTER_RECOGNITION");
            ENCLOSED_ALPHANUMERICS = new UnicodeBlock("ENCLOSED_ALPHANUMERICS");
            BOX_DRAWING = new UnicodeBlock("BOX_DRAWING");
            BLOCK_ELEMENTS = new UnicodeBlock("BLOCK_ELEMENTS");
            GEOMETRIC_SHAPES = new UnicodeBlock("GEOMETRIC_SHAPES");
            MISCELLANEOUS_SYMBOLS = new UnicodeBlock("MISCELLANEOUS_SYMBOLS");
            DINGBATS = new UnicodeBlock("DINGBATS");
            MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A = new UnicodeBlock("MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A");
            SUPPLEMENTAL_ARROWS_A = new UnicodeBlock("SUPPLEMENTAL_ARROWS_A");
            BRAILLE_PATTERNS = new UnicodeBlock("BRAILLE_PATTERNS");
            SUPPLEMENTAL_ARROWS_B = new UnicodeBlock("SUPPLEMENTAL_ARROWS_B");
            MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B = new UnicodeBlock("MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B");
            SUPPLEMENTAL_MATHEMATICAL_OPERATORS = new UnicodeBlock("SUPPLEMENTAL_MATHEMATICAL_OPERATORS");
            MISCELLANEOUS_SYMBOLS_AND_ARROWS = new UnicodeBlock("MISCELLANEOUS_SYMBOLS_AND_ARROWS");
            CJK_RADICALS_SUPPLEMENT = new UnicodeBlock("CJK_RADICALS_SUPPLEMENT");
            KANGXI_RADICALS = new UnicodeBlock("KANGXI_RADICALS");
            IDEOGRAPHIC_DESCRIPTION_CHARACTERS = new UnicodeBlock("IDEOGRAPHIC_DESCRIPTION_CHARACTERS");
            CJK_SYMBOLS_AND_PUNCTUATION = new UnicodeBlock("CJK_SYMBOLS_AND_PUNCTUATION");
            HIRAGANA = new UnicodeBlock("HIRAGANA");
            KATAKANA = new UnicodeBlock("KATAKANA");
            BOPOMOFO = new UnicodeBlock("BOPOMOFO");
            HANGUL_COMPATIBILITY_JAMO = new UnicodeBlock("HANGUL_COMPATIBILITY_JAMO");
            KANBUN = new UnicodeBlock("KANBUN");
            BOPOMOFO_EXTENDED = new UnicodeBlock("BOPOMOFO_EXTENDED");
            KATAKANA_PHONETIC_EXTENSIONS = new UnicodeBlock("KATAKANA_PHONETIC_EXTENSIONS");
            ENCLOSED_CJK_LETTERS_AND_MONTHS = new UnicodeBlock("ENCLOSED_CJK_LETTERS_AND_MONTHS");
            CJK_COMPATIBILITY = new UnicodeBlock("CJK_COMPATIBILITY");
            CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A = new UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A");
            YIJING_HEXAGRAM_SYMBOLS = new UnicodeBlock("YIJING_HEXAGRAM_SYMBOLS");
            CJK_UNIFIED_IDEOGRAPHS = new UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS");
            YI_SYLLABLES = new UnicodeBlock("YI_SYLLABLES");
            YI_RADICALS = new UnicodeBlock("YI_RADICALS");
            HANGUL_SYLLABLES = new UnicodeBlock("HANGUL_SYLLABLES");
            HIGH_SURROGATES = new UnicodeBlock("HIGH_SURROGATES");
            HIGH_PRIVATE_USE_SURROGATES = new UnicodeBlock("HIGH_PRIVATE_USE_SURROGATES");
            LOW_SURROGATES = new UnicodeBlock("LOW_SURROGATES");
            PRIVATE_USE_AREA = new UnicodeBlock("PRIVATE_USE_AREA");
            CJK_COMPATIBILITY_IDEOGRAPHS = new UnicodeBlock("CJK_COMPATIBILITY_IDEOGRAPHS");
            ALPHABETIC_PRESENTATION_FORMS = new UnicodeBlock("ALPHABETIC_PRESENTATION_FORMS");
            ARABIC_PRESENTATION_FORMS_A = new UnicodeBlock("ARABIC_PRESENTATION_FORMS_A");
            VARIATION_SELECTORS = new UnicodeBlock("VARIATION_SELECTORS");
            COMBINING_HALF_MARKS = new UnicodeBlock("COMBINING_HALF_MARKS");
            CJK_COMPATIBILITY_FORMS = new UnicodeBlock("CJK_COMPATIBILITY_FORMS");
            SMALL_FORM_VARIANTS = new UnicodeBlock("SMALL_FORM_VARIANTS");
            ARABIC_PRESENTATION_FORMS_B = new UnicodeBlock("ARABIC_PRESENTATION_FORMS_B");
            HALFWIDTH_AND_FULLWIDTH_FORMS = new UnicodeBlock("HALFWIDTH_AND_FULLWIDTH_FORMS");
            SPECIALS = new UnicodeBlock("SPECIALS");
            LINEAR_B_SYLLABARY = new UnicodeBlock("LINEAR_B_SYLLABARY");
            LINEAR_B_IDEOGRAMS = new UnicodeBlock("LINEAR_B_IDEOGRAMS");
            AEGEAN_NUMBERS = new UnicodeBlock("AEGEAN_NUMBERS");
            OLD_ITALIC = new UnicodeBlock("OLD_ITALIC");
            GOTHIC = new UnicodeBlock("GOTHIC");
            UGARITIC = new UnicodeBlock("UGARITIC");
            DESERET = new UnicodeBlock("DESERET");
            SHAVIAN = new UnicodeBlock("SHAVIAN");
            OSMANYA = new UnicodeBlock("OSMANYA");
            CYPRIOT_SYLLABARY = new UnicodeBlock("CYPRIOT_SYLLABARY");
            BYZANTINE_MUSICAL_SYMBOLS = new UnicodeBlock("BYZANTINE_MUSICAL_SYMBOLS");
            MUSICAL_SYMBOLS = new UnicodeBlock("MUSICAL_SYMBOLS");
            TAI_XUAN_JING_SYMBOLS = new UnicodeBlock("TAI_XUAN_JING_SYMBOLS");
            MATHEMATICAL_ALPHANUMERIC_SYMBOLS = new UnicodeBlock("MATHEMATICAL_ALPHANUMERIC_SYMBOLS");
            CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B = new UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B");
            CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT = new UnicodeBlock("CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT");
            TAGS = new UnicodeBlock("TAGS");
            VARIATION_SELECTORS_SUPPLEMENT = new UnicodeBlock("VARIATION_SELECTORS_SUPPLEMENT");
            SUPPLEMENTARY_PRIVATE_USE_AREA_A = new UnicodeBlock("SUPPLEMENTARY_PRIVATE_USE_AREA_A");
            SUPPLEMENTARY_PRIVATE_USE_AREA_B = new UnicodeBlock("SUPPLEMENTARY_PRIVATE_USE_AREA_B");
            ANCIENT_GREEK_MUSICAL_NOTATION = new UnicodeBlock("ANCIENT_GREEK_MUSICAL_NOTATION");
            ANCIENT_GREEK_NUMBERS = new UnicodeBlock("ANCIENT_GREEK_NUMBERS");
            ARABIC_SUPPLEMENT = new UnicodeBlock("ARABIC_SUPPLEMENT");
            BUGINESE = new UnicodeBlock("BUGINESE");
            CJK_STROKES = new UnicodeBlock("CJK_STROKES");
            COMBINING_DIACRITICAL_MARKS_SUPPLEMENT = new UnicodeBlock("COMBINING_DIACRITICAL_MARKS_SUPPLEMENT");
            COPTIC = new UnicodeBlock("COPTIC");
            ETHIOPIC_EXTENDED = new UnicodeBlock("ETHIOPIC_EXTENDED");
            ETHIOPIC_SUPPLEMENT = new UnicodeBlock("ETHIOPIC_SUPPLEMENT");
            GEORGIAN_SUPPLEMENT = new UnicodeBlock("GEORGIAN_SUPPLEMENT");
            GLAGOLITIC = new UnicodeBlock("GLAGOLITIC");
            KHAROSHTHI = new UnicodeBlock("KHAROSHTHI");
            MODIFIER_TONE_LETTERS = new UnicodeBlock("MODIFIER_TONE_LETTERS");
            NEW_TAI_LUE = new UnicodeBlock("NEW_TAI_LUE");
            OLD_PERSIAN = new UnicodeBlock("OLD_PERSIAN");
            PHONETIC_EXTENSIONS_SUPPLEMENT = new UnicodeBlock("PHONETIC_EXTENSIONS_SUPPLEMENT");
            SUPPLEMENTAL_PUNCTUATION = new UnicodeBlock("SUPPLEMENTAL_PUNCTUATION");
            SYLOTI_NAGRI = new UnicodeBlock("SYLOTI_NAGRI");
            TIFINAGH = new UnicodeBlock("TIFINAGH");
            VERTICAL_FORMS = new UnicodeBlock("VERTICAL_FORMS");
            NKO = new UnicodeBlock("NKO");
            BALINESE = new UnicodeBlock("BALINESE");
            LATIN_EXTENDED_C = new UnicodeBlock("LATIN_EXTENDED_C");
            LATIN_EXTENDED_D = new UnicodeBlock("LATIN_EXTENDED_D");
            PHAGS_PA = new UnicodeBlock("PHAGS_PA");
            PHOENICIAN = new UnicodeBlock("PHOENICIAN");
            CUNEIFORM = new UnicodeBlock("CUNEIFORM");
            CUNEIFORM_NUMBERS_AND_PUNCTUATION = new UnicodeBlock("CUNEIFORM_NUMBERS_AND_PUNCTUATION");
            COUNTING_ROD_NUMERALS = new UnicodeBlock("COUNTING_ROD_NUMERALS");
            SUNDANESE = new UnicodeBlock("SUNDANESE");
            LEPCHA = new UnicodeBlock("LEPCHA");
            OL_CHIKI = new UnicodeBlock("OL_CHIKI");
            CYRILLIC_EXTENDED_A = new UnicodeBlock("CYRILLIC_EXTENDED_A");
            VAI = new UnicodeBlock("VAI");
            CYRILLIC_EXTENDED_B = new UnicodeBlock("CYRILLIC_EXTENDED_B");
            SAURASHTRA = new UnicodeBlock("SAURASHTRA");
            KAYAH_LI = new UnicodeBlock("KAYAH_LI");
            REJANG = new UnicodeBlock("REJANG");
            CHAM = new UnicodeBlock("CHAM");
            ANCIENT_SYMBOLS = new UnicodeBlock("ANCIENT_SYMBOLS");
            PHAISTOS_DISC = new UnicodeBlock("PHAISTOS_DISC");
            LYCIAN = new UnicodeBlock("LYCIAN");
            CARIAN = new UnicodeBlock("CARIAN");
            LYDIAN = new UnicodeBlock("LYDIAN");
            MAHJONG_TILES = new UnicodeBlock("MAHJONG_TILES");
            DOMINO_TILES = new UnicodeBlock("DOMINO_TILES");
            SAMARITAN = new UnicodeBlock("SAMARITAN");
            UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS_EXTENDED = new UnicodeBlock("UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS_EXTENDED");
            TAI_THAM = new UnicodeBlock("TAI_THAM");
            VEDIC_EXTENSIONS = new UnicodeBlock("VEDIC_EXTENSIONS");
            LISU = new UnicodeBlock("LISU");
            BAMUM = new UnicodeBlock("BAMUM");
            COMMON_INDIC_NUMBER_FORMS = new UnicodeBlock("COMMON_INDIC_NUMBER_FORMS");
            DEVANAGARI_EXTENDED = new UnicodeBlock("DEVANAGARI_EXTENDED");
            HANGUL_JAMO_EXTENDED_A = new UnicodeBlock("HANGUL_JAMO_EXTENDED_A");
            JAVANESE = new UnicodeBlock("JAVANESE");
            MYANMAR_EXTENDED_A = new UnicodeBlock("MYANMAR_EXTENDED_A");
            TAI_VIET = new UnicodeBlock("TAI_VIET");
            MEETEI_MAYEK = new UnicodeBlock("MEETEI_MAYEK");
            HANGUL_JAMO_EXTENDED_B = new UnicodeBlock("HANGUL_JAMO_EXTENDED_B");
            IMPERIAL_ARAMAIC = new UnicodeBlock("IMPERIAL_ARAMAIC");
            OLD_SOUTH_ARABIAN = new UnicodeBlock("OLD_SOUTH_ARABIAN");
            AVESTAN = new UnicodeBlock("AVESTAN");
            INSCRIPTIONAL_PARTHIAN = new UnicodeBlock("INSCRIPTIONAL_PARTHIAN");
            INSCRIPTIONAL_PAHLAVI = new UnicodeBlock("INSCRIPTIONAL_PAHLAVI");
            OLD_TURKIC = new UnicodeBlock("OLD_TURKIC");
            RUMI_NUMERAL_SYMBOLS = new UnicodeBlock("RUMI_NUMERAL_SYMBOLS");
            KAITHI = new UnicodeBlock("KAITHI");
            EGYPTIAN_HIEROGLYPHS = new UnicodeBlock("EGYPTIAN_HIEROGLYPHS");
            ENCLOSED_ALPHANUMERIC_SUPPLEMENT = new UnicodeBlock("ENCLOSED_ALPHANUMERIC_SUPPLEMENT");
            ENCLOSED_IDEOGRAPHIC_SUPPLEMENT = new UnicodeBlock("ENCLOSED_IDEOGRAPHIC_SUPPLEMENT");
            CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C = new UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C");
            MANDAIC = new UnicodeBlock("MANDAIC");
            BATAK = new UnicodeBlock("BATAK");
            ETHIOPIC_EXTENDED_A = new UnicodeBlock("ETHIOPIC_EXTENDED_A");
            BRAHMI = new UnicodeBlock("BRAHMI");
            BAMUM_SUPPLEMENT = new UnicodeBlock("BAMUM_SUPPLEMENT");
            KANA_SUPPLEMENT = new UnicodeBlock("KANA_SUPPLEMENT");
            PLAYING_CARDS = new UnicodeBlock("PLAYING_CARDS");
            MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS = new UnicodeBlock("MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS");
            EMOTICONS = new UnicodeBlock("EMOTICONS");
            TRANSPORT_AND_MAP_SYMBOLS = new UnicodeBlock("TRANSPORT_AND_MAP_SYMBOLS");
            ALCHEMICAL_SYMBOLS = new UnicodeBlock("ALCHEMICAL_SYMBOLS");
            CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D = new UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D");
            UnicodeBlock[] unicodeBlockArr = new UnicodeBlock[Opcodes.OP_MUL_INT_LIT16];
            unicodeBlockArr[Character.MIN_CODE_POINT] = null;
            unicodeBlockArr[1] = BASIC_LATIN;
            unicodeBlockArr[Character.MIN_RADIX] = LATIN_1_SUPPLEMENT;
            unicodeBlockArr[3] = LATIN_EXTENDED_A;
            unicodeBlockArr[4] = LATIN_EXTENDED_B;
            unicodeBlockArr[5] = IPA_EXTENSIONS;
            unicodeBlockArr[6] = SPACING_MODIFIER_LETTERS;
            unicodeBlockArr[7] = COMBINING_DIACRITICAL_MARKS;
            unicodeBlockArr[8] = GREEK;
            unicodeBlockArr[9] = CYRILLIC;
            unicodeBlockArr[10] = ARMENIAN;
            unicodeBlockArr[11] = HEBREW;
            unicodeBlockArr[12] = ARABIC;
            unicodeBlockArr[13] = SYRIAC;
            unicodeBlockArr[14] = THAANA;
            unicodeBlockArr[15] = DEVANAGARI;
            unicodeBlockArr[Character.SIZE] = BENGALI;
            unicodeBlockArr[17] = GURMUKHI;
            unicodeBlockArr[18] = GUJARATI;
            unicodeBlockArr[19] = ORIYA;
            unicodeBlockArr[20] = TAMIL;
            unicodeBlockArr[21] = TELUGU;
            unicodeBlockArr[22] = KANNADA;
            unicodeBlockArr[23] = MALAYALAM;
            unicodeBlockArr[24] = SINHALA;
            unicodeBlockArr[25] = THAI;
            unicodeBlockArr[26] = LAO;
            unicodeBlockArr[27] = TIBETAN;
            unicodeBlockArr[28] = MYANMAR;
            unicodeBlockArr[29] = GEORGIAN;
            unicodeBlockArr[30] = HANGUL_JAMO;
            unicodeBlockArr[31] = ETHIOPIC;
            unicodeBlockArr[32] = CHEROKEE;
            unicodeBlockArr[33] = UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS;
            unicodeBlockArr[34] = OGHAM;
            unicodeBlockArr[35] = RUNIC;
            unicodeBlockArr[Character.MAX_RADIX] = KHMER;
            unicodeBlockArr[37] = MONGOLIAN;
            unicodeBlockArr[38] = LATIN_EXTENDED_ADDITIONAL;
            unicodeBlockArr[39] = GREEK_EXTENDED;
            unicodeBlockArr[40] = GENERAL_PUNCTUATION;
            unicodeBlockArr[41] = SUPERSCRIPTS_AND_SUBSCRIPTS;
            unicodeBlockArr[42] = CURRENCY_SYMBOLS;
            unicodeBlockArr[43] = COMBINING_MARKS_FOR_SYMBOLS;
            unicodeBlockArr[44] = LETTERLIKE_SYMBOLS;
            unicodeBlockArr[45] = NUMBER_FORMS;
            unicodeBlockArr[46] = ARROWS;
            unicodeBlockArr[47] = MATHEMATICAL_OPERATORS;
            unicodeBlockArr[48] = MISCELLANEOUS_TECHNICAL;
            unicodeBlockArr[49] = CONTROL_PICTURES;
            unicodeBlockArr[50] = OPTICAL_CHARACTER_RECOGNITION;
            unicodeBlockArr[51] = ENCLOSED_ALPHANUMERICS;
            unicodeBlockArr[52] = BOX_DRAWING;
            unicodeBlockArr[53] = BLOCK_ELEMENTS;
            unicodeBlockArr[54] = GEOMETRIC_SHAPES;
            unicodeBlockArr[55] = MISCELLANEOUS_SYMBOLS;
            unicodeBlockArr[56] = DINGBATS;
            unicodeBlockArr[57] = BRAILLE_PATTERNS;
            unicodeBlockArr[58] = CJK_RADICALS_SUPPLEMENT;
            unicodeBlockArr[59] = KANGXI_RADICALS;
            unicodeBlockArr[60] = IDEOGRAPHIC_DESCRIPTION_CHARACTERS;
            unicodeBlockArr[61] = CJK_SYMBOLS_AND_PUNCTUATION;
            unicodeBlockArr[62] = HIRAGANA;
            unicodeBlockArr[63] = KATAKANA;
            unicodeBlockArr[64] = BOPOMOFO;
            unicodeBlockArr[65] = HANGUL_COMPATIBILITY_JAMO;
            unicodeBlockArr[66] = KANBUN;
            unicodeBlockArr[67] = BOPOMOFO_EXTENDED;
            unicodeBlockArr[68] = ENCLOSED_CJK_LETTERS_AND_MONTHS;
            unicodeBlockArr[69] = CJK_COMPATIBILITY;
            unicodeBlockArr[70] = CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A;
            unicodeBlockArr[71] = CJK_UNIFIED_IDEOGRAPHS;
            unicodeBlockArr[72] = YI_SYLLABLES;
            unicodeBlockArr[73] = YI_RADICALS;
            unicodeBlockArr[74] = HANGUL_SYLLABLES;
            unicodeBlockArr[75] = HIGH_SURROGATES;
            unicodeBlockArr[76] = HIGH_PRIVATE_USE_SURROGATES;
            unicodeBlockArr[77] = LOW_SURROGATES;
            unicodeBlockArr[78] = PRIVATE_USE_AREA;
            unicodeBlockArr[79] = CJK_COMPATIBILITY_IDEOGRAPHS;
            unicodeBlockArr[80] = ALPHABETIC_PRESENTATION_FORMS;
            unicodeBlockArr[81] = ARABIC_PRESENTATION_FORMS_A;
            unicodeBlockArr[82] = COMBINING_HALF_MARKS;
            unicodeBlockArr[83] = CJK_COMPATIBILITY_FORMS;
            unicodeBlockArr[84] = SMALL_FORM_VARIANTS;
            unicodeBlockArr[85] = ARABIC_PRESENTATION_FORMS_B;
            unicodeBlockArr[86] = SPECIALS;
            unicodeBlockArr[87] = HALFWIDTH_AND_FULLWIDTH_FORMS;
            unicodeBlockArr[88] = OLD_ITALIC;
            unicodeBlockArr[89] = GOTHIC;
            unicodeBlockArr[90] = DESERET;
            unicodeBlockArr[91] = BYZANTINE_MUSICAL_SYMBOLS;
            unicodeBlockArr[92] = MUSICAL_SYMBOLS;
            unicodeBlockArr[93] = MATHEMATICAL_ALPHANUMERIC_SYMBOLS;
            unicodeBlockArr[94] = CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B;
            unicodeBlockArr[95] = CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT;
            unicodeBlockArr[96] = TAGS;
            unicodeBlockArr[97] = CYRILLIC_SUPPLEMENTARY;
            unicodeBlockArr[98] = TAGALOG;
            unicodeBlockArr[99] = HANUNOO;
            unicodeBlockArr[100] = BUHID;
            unicodeBlockArr[Opcodes.OP_SGET_CHAR] = TAGBANWA;
            unicodeBlockArr[Opcodes.OP_SGET_SHORT] = MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A;
            unicodeBlockArr[Opcodes.OP_SPUT] = SUPPLEMENTAL_ARROWS_A;
            unicodeBlockArr[Opcodes.OP_SPUT_WIDE] = SUPPLEMENTAL_ARROWS_B;
            unicodeBlockArr[Opcodes.OP_SPUT_OBJECT] = MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B;
            unicodeBlockArr[Opcodes.OP_SPUT_BOOLEAN] = SUPPLEMENTAL_MATHEMATICAL_OPERATORS;
            unicodeBlockArr[Opcodes.OP_SPUT_BYTE] = KATAKANA_PHONETIC_EXTENSIONS;
            unicodeBlockArr[Opcodes.OP_SPUT_CHAR] = VARIATION_SELECTORS;
            unicodeBlockArr[Opcodes.OP_SPUT_SHORT] = SUPPLEMENTARY_PRIVATE_USE_AREA_A;
            unicodeBlockArr[Opcodes.OP_INVOKE_VIRTUAL] = SUPPLEMENTARY_PRIVATE_USE_AREA_B;
            unicodeBlockArr[Opcodes.OP_INVOKE_SUPER] = LIMBU;
            unicodeBlockArr[Opcodes.OP_INVOKE_DIRECT] = TAI_LE;
            unicodeBlockArr[Opcodes.OP_INVOKE_STATIC] = KHMER_SYMBOLS;
            unicodeBlockArr[Opcodes.OP_INVOKE_INTERFACE] = PHONETIC_EXTENSIONS;
            unicodeBlockArr[115] = MISCELLANEOUS_SYMBOLS_AND_ARROWS;
            unicodeBlockArr[Opcodes.OP_INVOKE_VIRTUAL_RANGE] = YIJING_HEXAGRAM_SYMBOLS;
            unicodeBlockArr[Opcodes.OP_INVOKE_SUPER_RANGE] = LINEAR_B_SYLLABARY;
            unicodeBlockArr[Opcodes.OP_INVOKE_DIRECT_RANGE] = LINEAR_B_IDEOGRAMS;
            unicodeBlockArr[Opcodes.OP_INVOKE_STATIC_RANGE] = AEGEAN_NUMBERS;
            unicodeBlockArr[Opcodes.OP_INVOKE_INTERFACE_RANGE] = UGARITIC;
            unicodeBlockArr[121] = SHAVIAN;
            unicodeBlockArr[122] = OSMANYA;
            unicodeBlockArr[Opcodes.OP_NEG_INT] = CYPRIOT_SYLLABARY;
            unicodeBlockArr[Opcodes.OP_NOT_INT] = TAI_XUAN_JING_SYMBOLS;
            unicodeBlockArr[Opcodes.OP_NEG_LONG] = VARIATION_SELECTORS_SUPPLEMENT;
            unicodeBlockArr[Opcodes.OP_NOT_LONG] = ANCIENT_GREEK_MUSICAL_NOTATION;
            unicodeBlockArr[Float.MAX_EXPONENT] = ANCIENT_GREEK_NUMBERS;
            unicodeBlockArr[NodeFilter.SHOW_COMMENT] = ARABIC_SUPPLEMENT;
            unicodeBlockArr[Opcodes.OP_INT_TO_LONG] = BUGINESE;
            unicodeBlockArr[Opcodes.OP_INT_TO_FLOAT] = CJK_STROKES;
            unicodeBlockArr[Opcodes.OP_INT_TO_DOUBLE] = COMBINING_DIACRITICAL_MARKS_SUPPLEMENT;
            unicodeBlockArr[Opcodes.OP_LONG_TO_INT] = COPTIC;
            unicodeBlockArr[Opcodes.OP_LONG_TO_FLOAT] = ETHIOPIC_EXTENDED;
            unicodeBlockArr[Opcodes.OP_LONG_TO_DOUBLE] = ETHIOPIC_SUPPLEMENT;
            unicodeBlockArr[Opcodes.OP_FLOAT_TO_INT] = GEORGIAN_SUPPLEMENT;
            unicodeBlockArr[Opcodes.OP_FLOAT_TO_LONG] = GLAGOLITIC;
            unicodeBlockArr[Opcodes.OP_FLOAT_TO_DOUBLE] = KHAROSHTHI;
            unicodeBlockArr[Opcodes.OP_DOUBLE_TO_INT] = MODIFIER_TONE_LETTERS;
            unicodeBlockArr[Opcodes.OP_DOUBLE_TO_LONG] = NEW_TAI_LUE;
            unicodeBlockArr[Opcodes.OP_DOUBLE_TO_FLOAT] = OLD_PERSIAN;
            unicodeBlockArr[Opcodes.OP_INT_TO_BYTE] = PHONETIC_EXTENSIONS_SUPPLEMENT;
            unicodeBlockArr[Opcodes.OP_INT_TO_CHAR] = SUPPLEMENTAL_PUNCTUATION;
            unicodeBlockArr[Opcodes.OP_INT_TO_SHORT] = SYLOTI_NAGRI;
            unicodeBlockArr[Opcodes.OP_ADD_INT] = TIFINAGH;
            unicodeBlockArr[Opcodes.OP_SUB_INT] = VERTICAL_FORMS;
            unicodeBlockArr[Opcodes.OP_MUL_INT] = NKO;
            unicodeBlockArr[Opcodes.OP_DIV_INT] = BALINESE;
            unicodeBlockArr[Opcodes.OP_REM_INT] = LATIN_EXTENDED_C;
            unicodeBlockArr[Opcodes.OP_AND_INT] = LATIN_EXTENDED_D;
            unicodeBlockArr[Opcodes.OP_OR_INT] = PHAGS_PA;
            unicodeBlockArr[Opcodes.OP_XOR_INT] = PHOENICIAN;
            unicodeBlockArr[Opcodes.OP_SHL_INT] = CUNEIFORM;
            unicodeBlockArr[Opcodes.OP_SHR_INT] = CUNEIFORM_NUMBERS_AND_PUNCTUATION;
            unicodeBlockArr[Opcodes.OP_USHR_INT] = COUNTING_ROD_NUMERALS;
            unicodeBlockArr[Opcodes.OP_ADD_LONG] = SUNDANESE;
            unicodeBlockArr[Opcodes.OP_SUB_LONG] = LEPCHA;
            unicodeBlockArr[Opcodes.OP_MUL_LONG] = OL_CHIKI;
            unicodeBlockArr[Opcodes.OP_DIV_LONG] = CYRILLIC_EXTENDED_A;
            unicodeBlockArr[Opcodes.OP_REM_LONG] = VAI;
            unicodeBlockArr[Opcodes.OP_AND_LONG] = CYRILLIC_EXTENDED_B;
            unicodeBlockArr[Opcodes.OP_OR_LONG] = SAURASHTRA;
            unicodeBlockArr[Opcodes.OP_XOR_LONG] = KAYAH_LI;
            unicodeBlockArr[Opcodes.OP_SHL_LONG] = REJANG;
            unicodeBlockArr[Opcodes.OP_SHR_LONG] = CHAM;
            unicodeBlockArr[Opcodes.OP_USHR_LONG] = ANCIENT_SYMBOLS;
            unicodeBlockArr[Opcodes.OP_ADD_FLOAT] = PHAISTOS_DISC;
            unicodeBlockArr[Opcodes.OP_SUB_FLOAT] = LYCIAN;
            unicodeBlockArr[Opcodes.OP_MUL_FLOAT] = CARIAN;
            unicodeBlockArr[Opcodes.OP_DIV_FLOAT] = LYDIAN;
            unicodeBlockArr[Opcodes.OP_REM_FLOAT] = MAHJONG_TILES;
            unicodeBlockArr[Opcodes.OP_ADD_DOUBLE] = DOMINO_TILES;
            unicodeBlockArr[Opcodes.OP_SUB_DOUBLE] = SAMARITAN;
            unicodeBlockArr[Opcodes.OP_MUL_DOUBLE] = UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS_EXTENDED;
            unicodeBlockArr[Opcodes.OP_DIV_DOUBLE] = TAI_THAM;
            unicodeBlockArr[Opcodes.OP_REM_DOUBLE] = VEDIC_EXTENSIONS;
            unicodeBlockArr[Opcodes.OP_ADD_INT_2ADDR] = LISU;
            unicodeBlockArr[Opcodes.OP_SUB_INT_2ADDR] = BAMUM;
            unicodeBlockArr[Opcodes.OP_MUL_INT_2ADDR] = COMMON_INDIC_NUMBER_FORMS;
            unicodeBlockArr[Opcodes.OP_DIV_INT_2ADDR] = DEVANAGARI_EXTENDED;
            unicodeBlockArr[Opcodes.OP_REM_INT_2ADDR] = HANGUL_JAMO_EXTENDED_A;
            unicodeBlockArr[Opcodes.OP_AND_INT_2ADDR] = JAVANESE;
            unicodeBlockArr[Opcodes.OP_OR_INT_2ADDR] = MYANMAR_EXTENDED_A;
            unicodeBlockArr[Opcodes.OP_XOR_INT_2ADDR] = TAI_VIET;
            unicodeBlockArr[Opcodes.OP_SHL_INT_2ADDR] = MEETEI_MAYEK;
            unicodeBlockArr[Opcodes.OP_SHR_INT_2ADDR] = HANGUL_JAMO_EXTENDED_B;
            unicodeBlockArr[Opcodes.OP_USHR_INT_2ADDR] = IMPERIAL_ARAMAIC;
            unicodeBlockArr[Opcodes.OP_ADD_LONG_2ADDR] = OLD_SOUTH_ARABIAN;
            unicodeBlockArr[Opcodes.OP_SUB_LONG_2ADDR] = AVESTAN;
            unicodeBlockArr[Opcodes.OP_MUL_LONG_2ADDR] = INSCRIPTIONAL_PARTHIAN;
            unicodeBlockArr[Opcodes.OP_DIV_LONG_2ADDR] = INSCRIPTIONAL_PAHLAVI;
            unicodeBlockArr[Opcodes.OP_REM_LONG_2ADDR] = OLD_TURKIC;
            unicodeBlockArr[ASN1Constants.CLASS_PRIVATE] = RUMI_NUMERAL_SYMBOLS;
            unicodeBlockArr[Opcodes.OP_OR_LONG_2ADDR] = KAITHI;
            unicodeBlockArr[Opcodes.OP_XOR_LONG_2ADDR] = EGYPTIAN_HIEROGLYPHS;
            unicodeBlockArr[Opcodes.OP_SHL_LONG_2ADDR] = ENCLOSED_ALPHANUMERIC_SUPPLEMENT;
            unicodeBlockArr[Opcodes.OP_SHR_LONG_2ADDR] = ENCLOSED_IDEOGRAPHIC_SUPPLEMENT;
            unicodeBlockArr[Opcodes.OP_USHR_LONG_2ADDR] = CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C;
            unicodeBlockArr[Opcodes.OP_ADD_FLOAT_2ADDR] = MANDAIC;
            unicodeBlockArr[Opcodes.OP_SUB_FLOAT_2ADDR] = BATAK;
            unicodeBlockArr[HttpURLConnection.HTTP_OK] = ETHIOPIC_EXTENDED_A;
            unicodeBlockArr[HttpURLConnection.HTTP_CREATED] = BRAHMI;
            unicodeBlockArr[HttpURLConnection.HTTP_ACCEPTED] = BAMUM_SUPPLEMENT;
            unicodeBlockArr[HttpURLConnection.HTTP_NOT_AUTHORITATIVE] = KANA_SUPPLEMENT;
            unicodeBlockArr[HttpURLConnection.HTTP_NO_CONTENT] = PLAYING_CARDS;
            unicodeBlockArr[HttpURLConnection.HTTP_RESET] = MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS;
            unicodeBlockArr[HttpURLConnection.HTTP_PARTIAL] = EMOTICONS;
            unicodeBlockArr[Opcodes.OP_REM_DOUBLE_2ADDR] = TRANSPORT_AND_MAP_SYMBOLS;
            unicodeBlockArr[Opcodes.OP_ADD_INT_LIT16] = ALCHEMICAL_SYMBOLS;
            unicodeBlockArr[Opcodes.OP_RSUB_INT] = CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D;
            BLOCKS = unicodeBlockArr;
        }

        public static UnicodeBlock forName(String blockName) {
            if (blockName == null) {
                throw new NullPointerException("blockName == null");
            }
            int block = Character.unicodeBlockForName(blockName);
            if (block != -1) {
                return BLOCKS[block];
            }
            throw new IllegalArgumentException("Unknown block: " + blockName);
        }

        public static UnicodeBlock of(char c) {
            return of((int) c);
        }

        public static UnicodeBlock of(int codePoint) {
            Character.checkValidCodePoint(codePoint);
            int block = Character.unicodeBlockForCodePoint(codePoint);
            if (block == -1 || block >= BLOCKS.length) {
                return null;
            }
            return BLOCKS[block];
        }

        private UnicodeBlock(String blockName) {
            super(blockName);
        }
    }

    static {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.lang.Character.<clinit>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.lang.Character.<clinit>():void
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
        throw new UnsupportedOperationException("Method not decompiled: java.lang.Character.<clinit>():void");
    }

    private static native int digitImpl(int i, int i2);

    public static native byte getIcuDirectionality(int i);

    private static native String getNameImpl(int i);

    private static native int getNumericValueImpl(int i);

    private static native int getTypeImpl(int i);

    public static native boolean isAlphabetic(int i);

    private static native boolean isDefinedImpl(int i);

    private static native boolean isDigitImpl(int i);

    private static native boolean isIdentifierIgnorableImpl(int i);

    public static native boolean isIdeographic(int i);

    private static native boolean isLetterImpl(int i);

    private static native boolean isLetterOrDigitImpl(int i);

    private static native boolean isLowerCaseImpl(int i);

    private static native boolean isMirroredImpl(int i);

    private static native boolean isSpaceCharImpl(int i);

    private static native boolean isTitleCaseImpl(int i);

    private static native boolean isUnicodeIdentifierPartImpl(int i);

    private static native boolean isUnicodeIdentifierStartImpl(int i);

    private static native boolean isUpperCaseImpl(int i);

    private static native boolean isWhitespaceImpl(int i);

    private static native int toLowerCaseImpl(int i);

    private static native int toTitleCaseImpl(int i);

    private static native int toUpperCaseImpl(int i);

    private static native int unicodeBlockForCodePoint(int i);

    private static native int unicodeBlockForName(String str);

    private static native int unicodeScriptForCodePoint(int i);

    private static native int unicodeScriptForName(String str);

    public Character(char value) {
        this.value = value;
    }

    public char charValue() {
        return this.value;
    }

    private static void checkValidCodePoint(int codePoint) {
        if (!isValidCodePoint(codePoint)) {
            throw new IllegalArgumentException("Invalid code point: " + codePoint);
        }
    }

    public int compareTo(Character c) {
        return compare(this.value, c.value);
    }

    public static int compare(char lhs, char rhs) {
        return lhs - rhs;
    }

    public static Character valueOf(char c) {
        return c < '\u0080' ? SMALL_VALUES[c] : new Character(c);
    }

    public static boolean isValidCodePoint(int codePoint) {
        return codePoint >= 0 && MAX_CODE_POINT >= codePoint;
    }

    public static boolean isSupplementaryCodePoint(int codePoint) {
        return MIN_SUPPLEMENTARY_CODE_POINT <= codePoint && MAX_CODE_POINT >= codePoint;
    }

    public static boolean isHighSurrogate(char ch) {
        return MIN_SURROGATE <= ch && MAX_HIGH_SURROGATE >= ch;
    }

    public static boolean isLowSurrogate(char ch) {
        return MIN_LOW_SURROGATE <= ch && MAX_SURROGATE >= ch;
    }

    public static boolean isSurrogate(char ch) {
        return ch >= MIN_SURROGATE && ch <= MAX_SURROGATE;
    }

    public static boolean isSurrogatePair(char high, char low) {
        return isHighSurrogate(high) && isLowSurrogate(low);
    }

    public static int charCount(int codePoint) {
        return codePoint >= MIN_SUPPLEMENTARY_CODE_POINT ? MIN_RADIX : 1;
    }

    public static int toCodePoint(char high, char low) {
        return (((high & Double.MAX_EXPONENT) << 10) | (low & Double.MAX_EXPONENT)) + MIN_SUPPLEMENTARY_CODE_POINT;
    }

    public static int codePointAt(CharSequence seq, int index) {
        if (seq == null) {
            throw new NullPointerException("seq == null");
        }
        int len = seq.length();
        if (index < 0 || index >= len) {
            throw new IndexOutOfBoundsException();
        }
        int index2 = index + 1;
        char high = seq.charAt(index);
        if (index2 >= len) {
            return high;
        }
        char low = seq.charAt(index2);
        if (isSurrogatePair(high, low)) {
            return toCodePoint(high, low);
        }
        return high;
    }

    public static int codePointAt(char[] seq, int index) {
        if (seq == null) {
            throw new NullPointerException("seq == null");
        }
        int len = seq.length;
        if (index < 0 || index >= len) {
            throw new IndexOutOfBoundsException();
        }
        int index2 = index + 1;
        char high = seq[index];
        if (index2 >= len) {
            return high;
        }
        char low = seq[index2];
        if (isSurrogatePair(high, low)) {
            return toCodePoint(high, low);
        }
        return high;
    }

    public static int codePointAt(char[] seq, int index, int limit) {
        if (index < 0 || index >= limit || limit < 0 || limit > seq.length) {
            throw new IndexOutOfBoundsException();
        }
        int index2 = index + 1;
        char high = seq[index];
        if (index2 >= limit) {
            return high;
        }
        char low = seq[index2];
        if (isSurrogatePair(high, low)) {
            return toCodePoint(high, low);
        }
        return high;
    }

    public static int codePointBefore(CharSequence seq, int index) {
        if (seq == null) {
            throw new NullPointerException("seq == null");
        }
        int len = seq.length();
        if (index < 1 || index > len) {
            throw new IndexOutOfBoundsException();
        }
        index--;
        char low = seq.charAt(index);
        index--;
        if (index < 0) {
            return low;
        }
        char high = seq.charAt(index);
        if (isSurrogatePair(high, low)) {
            return toCodePoint(high, low);
        }
        return low;
    }

    public static int codePointBefore(char[] seq, int index) {
        if (seq == null) {
            throw new NullPointerException("seq == null");
        }
        int len = seq.length;
        if (index < 1 || index > len) {
            throw new IndexOutOfBoundsException();
        }
        index--;
        char low = seq[index];
        index--;
        if (index < 0) {
            return low;
        }
        char high = seq[index];
        if (isSurrogatePair(high, low)) {
            return toCodePoint(high, low);
        }
        return low;
    }

    public static int codePointBefore(char[] seq, int index, int start) {
        if (seq == null) {
            throw new NullPointerException("seq == null");
        }
        int len = seq.length;
        if (index <= start || index > len || start < 0 || start >= len) {
            throw new IndexOutOfBoundsException();
        }
        index--;
        char low = seq[index];
        index--;
        if (index < start) {
            return low;
        }
        char high = seq[index];
        if (isSurrogatePair(high, low)) {
            return toCodePoint(high, low);
        }
        return low;
    }

    public static int toChars(int codePoint, char[] dst, int dstIndex) {
        checkValidCodePoint(codePoint);
        if (dst == null) {
            throw new NullPointerException("dst == null");
        } else if (dstIndex < 0 || dstIndex >= dst.length) {
            throw new IndexOutOfBoundsException();
        } else if (!isSupplementaryCodePoint(codePoint)) {
            dst[dstIndex] = (char) codePoint;
            return 1;
        } else if (dstIndex == dst.length - 1) {
            throw new IndexOutOfBoundsException();
        } else {
            int cpPrime = codePoint - MIN_SUPPLEMENTARY_CODE_POINT;
            int low = 56320 | (cpPrime & Double.MAX_EXPONENT);
            dst[dstIndex] = (char) (55296 | ((cpPrime >> 10) & Double.MAX_EXPONENT));
            dst[dstIndex + 1] = (char) low;
            return MIN_RADIX;
        }
    }

    public static char[] toChars(int codePoint) {
        checkValidCodePoint(codePoint);
        if (isSupplementaryCodePoint(codePoint)) {
            int cpPrime = codePoint - MIN_SUPPLEMENTARY_CODE_POINT;
            int low = 56320 | (cpPrime & Double.MAX_EXPONENT);
            char[] cArr = new char[MIN_RADIX];
            cArr[MIN_CODE_POINT] = (char) (55296 | ((cpPrime >> 10) & Double.MAX_EXPONENT));
            cArr[1] = (char) low;
            return cArr;
        }
        return new char[]{(char) codePoint};
    }

    public static int codePointCount(CharSequence seq, int beginIndex, int endIndex) {
        if (seq == null) {
            throw new NullPointerException("seq == null");
        }
        int len = seq.length();
        if (beginIndex < 0 || endIndex > len || beginIndex > endIndex) {
            throw new IndexOutOfBoundsException();
        }
        int result = MIN_CODE_POINT;
        int i = beginIndex;
        while (i < endIndex) {
            if (isHighSurrogate(seq.charAt(i))) {
                i++;
                if (i < endIndex && !isLowSurrogate(seq.charAt(i))) {
                    result++;
                }
            }
            result++;
            i++;
        }
        return result;
    }

    public static int codePointCount(char[] seq, int offset, int count) {
        Arrays.checkOffsetAndCount(seq.length, offset, count);
        int endIndex = offset + count;
        int result = MIN_CODE_POINT;
        int i = offset;
        while (i < endIndex) {
            if (isHighSurrogate(seq[i])) {
                i++;
                if (i < endIndex && !isLowSurrogate(seq[i])) {
                    result++;
                }
            }
            result++;
            i++;
        }
        return result;
    }

    public static int offsetByCodePoints(CharSequence seq, int index, int codePointOffset) {
        if (seq == null) {
            throw new NullPointerException("seq == null");
        }
        int len = seq.length();
        if (index < 0 || index > len) {
            throw new IndexOutOfBoundsException();
        } else if (codePointOffset == 0) {
            return index;
        } else {
            int codePoints;
            int i;
            if (codePointOffset > 0) {
                codePoints = codePointOffset;
                i = index;
                while (codePoints > 0) {
                    codePoints--;
                    if (i >= len) {
                        throw new IndexOutOfBoundsException();
                    }
                    if (isHighSurrogate(seq.charAt(i))) {
                        int next = i + 1;
                        if (next < len && isLowSurrogate(seq.charAt(next))) {
                            i++;
                        }
                    }
                    i++;
                }
                return i;
            }
            codePoints = -codePointOffset;
            i = index;
            while (codePoints > 0) {
                codePoints--;
                i--;
                if (i < 0) {
                    throw new IndexOutOfBoundsException();
                } else if (isLowSurrogate(seq.charAt(i))) {
                    int prev = i - 1;
                    if (prev >= 0 && isHighSurrogate(seq.charAt(prev))) {
                        i--;
                    }
                }
            }
            return i;
        }
    }

    public static int offsetByCodePoints(char[] seq, int start, int count, int index, int codePointOffset) {
        Arrays.checkOffsetAndCount(seq.length, start, count);
        int end = start + count;
        if (index < start || index > end) {
            throw new IndexOutOfBoundsException();
        } else if (codePointOffset == 0) {
            return index;
        } else {
            int codePoints;
            int i;
            if (codePointOffset > 0) {
                codePoints = codePointOffset;
                i = index;
                while (codePoints > 0) {
                    codePoints--;
                    if (i >= end) {
                        throw new IndexOutOfBoundsException();
                    }
                    if (isHighSurrogate(seq[i])) {
                        int next = i + 1;
                        if (next < end && isLowSurrogate(seq[next])) {
                            i++;
                        }
                    }
                    i++;
                }
                return i;
            }
            codePoints = -codePointOffset;
            i = index;
            while (codePoints > 0) {
                codePoints--;
                i--;
                if (i < start) {
                    throw new IndexOutOfBoundsException();
                } else if (isLowSurrogate(seq[i])) {
                    int prev = i - 1;
                    if (prev >= start && isHighSurrogate(seq[prev])) {
                        i--;
                    }
                }
            }
            return i;
        }
    }

    public static int digit(char c, int radix) {
        return digit((int) c, radix);
    }

    public static int digit(int codePoint, int radix) {
        if (radix < MIN_RADIX || radix > MAX_RADIX) {
            return -1;
        }
        if (codePoint >= NodeFilter.SHOW_COMMENT) {
            return digitImpl(codePoint, radix);
        }
        int result = -1;
        if (48 <= codePoint && codePoint <= 57) {
            result = codePoint - 48;
        } else if (97 <= codePoint && codePoint <= 122) {
            result = (codePoint - 97) + 10;
        } else if (65 <= codePoint && codePoint <= 90) {
            result = (codePoint - 65) + 10;
        }
        if (result >= radix) {
            return -1;
        }
        return result;
    }

    public boolean equals(Object object) {
        return (object instanceof Character) && ((Character) object).value == this.value;
    }

    public static char forDigit(int digit, int radix) {
        if (MIN_RADIX > radix || radix > MAX_RADIX || digit < 0 || digit >= radix) {
            return MIN_VALUE;
        }
        return (char) (digit < 10 ? digit + 48 : (digit + 97) - 10);
    }

    public static String getName(int codePoint) {
        checkValidCodePoint(codePoint);
        if (getType(codePoint) == 0) {
            return null;
        }
        String result = getNameImpl(codePoint);
        if (result != null) {
            return result;
        }
        return UnicodeBlock.of(codePoint).toString().replace('_', ' ') + " " + IntegralToString.intToHexString(codePoint, true, MIN_CODE_POINT);
    }

    public static int getNumericValue(char c) {
        return getNumericValue((int) c);
    }

    public static int getNumericValue(int codePoint) {
        if (codePoint < NodeFilter.SHOW_COMMENT) {
            if (codePoint >= 48 && codePoint <= 57) {
                return codePoint - 48;
            }
            if (codePoint >= 97 && codePoint <= 122) {
                return codePoint - 87;
            }
            if (codePoint < 65 || codePoint > 90) {
                return -1;
            }
            return codePoint - 55;
        } else if (codePoint >= 65313 && codePoint <= 65338) {
            return codePoint - 65303;
        } else {
            if (codePoint < 65345 || codePoint > 65370) {
                return getNumericValueImpl(codePoint);
            }
            return codePoint - 65335;
        }
    }

    public static int getType(char c) {
        return getType((int) c);
    }

    public static int getType(int codePoint) {
        int type = getTypeImpl(codePoint);
        return type <= SIZE ? type : type + 1;
    }

    public static byte getDirectionality(char c) {
        return getDirectionality((int) c);
    }

    public static byte getDirectionality(int codePoint) {
        if (getType(codePoint) == 0) {
            return DIRECTIONALITY_UNDEFINED;
        }
        byte directionality = getIcuDirectionality(codePoint);
        if (directionality < null || directionality >= DIRECTIONALITY.length) {
            return DIRECTIONALITY_UNDEFINED;
        }
        return DIRECTIONALITY[directionality];
    }

    public static boolean isMirrored(char c) {
        return isMirrored((int) c);
    }

    public static boolean isMirrored(int codePoint) {
        return isMirroredImpl(codePoint);
    }

    public int hashCode() {
        return this.value;
    }

    public static char highSurrogate(int codePoint) {
        return (char) ((codePoint >> 10) + 55232);
    }

    public static char lowSurrogate(int codePoint) {
        return (char) ((codePoint & Double.MAX_EXPONENT) | 56320);
    }

    public static boolean isBmpCodePoint(int codePoint) {
        return codePoint >= 0 && codePoint <= DexFormat.MAX_TYPE_IDX;
    }

    public static boolean isDefined(char c) {
        return isDefinedImpl(c);
    }

    public static boolean isDefined(int codePoint) {
        return isDefinedImpl(codePoint);
    }

    public static boolean isDigit(char c) {
        return isDigit((int) c);
    }

    public static boolean isDigit(int codePoint) {
        if (48 <= codePoint && codePoint <= 57) {
            return true;
        }
        if (codePoint < 1632) {
            return false;
        }
        return isDigitImpl(codePoint);
    }

    public static boolean isIdentifierIgnorable(char c) {
        return isIdentifierIgnorable((int) c);
    }

    public static boolean isIdentifierIgnorable(int codePoint) {
        if (codePoint < 1536) {
            return (codePoint >= 0 && codePoint <= 8) || ((codePoint >= 14 && codePoint <= 27) || ((codePoint >= Float.MAX_EXPONENT && codePoint <= Opcodes.OP_REM_LONG) || codePoint == Opcodes.OP_MUL_DOUBLE));
        } else {
            return isIdentifierIgnorableImpl(codePoint);
        }
    }

    public static boolean isISOControl(char c) {
        return isISOControl((int) c);
    }

    public static boolean isISOControl(int c) {
        return (c >= 0 && c <= 31) || (c >= Float.MAX_EXPONENT && c <= Opcodes.OP_REM_LONG);
    }

    public static boolean isJavaIdentifierPart(char c) {
        return isJavaIdentifierPart((int) c);
    }

    public static boolean isJavaIdentifierPart(int codePoint) {
        if (codePoint < 64) {
            if ((287948970162897407L & (1 << codePoint)) != 0) {
                return true;
            }
            return false;
        } else if (codePoint >= NodeFilter.SHOW_COMMENT) {
            int type = getType(codePoint);
            if ((type >= 1 && type <= 5) || type == 26 || type == 23) {
                return true;
            }
            if ((type >= 9 && type <= 10) || type == 8 || type == 6) {
                return true;
            }
            if (codePoint >= 0 && codePoint <= 8) {
                return true;
            }
            if (codePoint >= 14 && codePoint <= 27) {
                return true;
            }
            if ((codePoint < Float.MAX_EXPONENT || codePoint > Opcodes.OP_REM_LONG) && type != SIZE) {
                return false;
            }
            return true;
        } else if ((-8646911290859585538L & (1 << (codePoint - 64))) == 0) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isJavaIdentifierStart(char c) {
        return isJavaIdentifierStart((int) c);
    }

    public static boolean isJavaIdentifierStart(int codePoint) {
        if (codePoint < 64) {
            if (codePoint == MAX_RADIX) {
                return true;
            }
            return false;
        } else if (codePoint >= NodeFilter.SHOW_COMMENT) {
            int type = getType(codePoint);
            if ((type >= 1 && type <= 5) || type == 26 || type == 23 || type == 10) {
                return true;
            }
            return false;
        } else if ((576460745995190270L & (1 << (codePoint - 64))) == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Deprecated
    public static boolean isJavaLetter(char c) {
        return isJavaIdentifierStart(c);
    }

    @Deprecated
    public static boolean isJavaLetterOrDigit(char c) {
        return isJavaIdentifierPart(c);
    }

    public static boolean isLetter(char c) {
        return isLetter((int) c);
    }

    public static boolean isLetter(int codePoint) {
        if ((65 <= codePoint && codePoint <= 90) || (97 <= codePoint && codePoint <= 122)) {
            return true;
        }
        if (codePoint < NodeFilter.SHOW_COMMENT) {
            return false;
        }
        return isLetterImpl(codePoint);
    }

    public static boolean isLetterOrDigit(char c) {
        return isLetterOrDigit((int) c);
    }

    public static boolean isLetterOrDigit(int codePoint) {
        if (65 <= codePoint && codePoint <= 90) {
            return true;
        }
        if (97 <= codePoint && codePoint <= 122) {
            return true;
        }
        if (48 <= codePoint && codePoint <= 57) {
            return true;
        }
        if (codePoint < NodeFilter.SHOW_COMMENT) {
            return false;
        }
        return isLetterOrDigitImpl(codePoint);
    }

    public static boolean isLowerCase(char c) {
        return isLowerCase((int) c);
    }

    public static boolean isLowerCase(int codePoint) {
        if (97 <= codePoint && codePoint <= 122) {
            return true;
        }
        if (codePoint < NodeFilter.SHOW_COMMENT) {
            return false;
        }
        return isLowerCaseImpl(codePoint);
    }

    @Deprecated
    public static boolean isSpace(char c) {
        return c == '\n' || c == '\t' || c == '\f' || c == '\r' || c == ' ';
    }

    public static boolean isSpaceChar(char c) {
        return isSpaceChar((int) c);
    }

    public static boolean isSpaceChar(int codePoint) {
        if (codePoint == 32 || codePoint == Opcodes.OP_AND_LONG) {
            return true;
        }
        if (codePoint < Modifier.SYNTHETIC) {
            return false;
        }
        if (codePoint == 5760 || codePoint == 6158) {
            return true;
        }
        if (codePoint < DateIntervalFormat.FORMAT_UTC) {
            return false;
        }
        if (codePoint > DexFormat.MAX_TYPE_IDX) {
            return isSpaceCharImpl(codePoint);
        }
        if (codePoint <= 8202 || codePoint == 8232 || codePoint == 8233 || codePoint == 8239 || codePoint == 8287 || codePoint == 12288) {
            return true;
        }
        return false;
    }

    public static boolean isTitleCase(char c) {
        return isTitleCaseImpl(c);
    }

    public static boolean isTitleCase(int codePoint) {
        return isTitleCaseImpl(codePoint);
    }

    public static boolean isUnicodeIdentifierPart(char c) {
        return isUnicodeIdentifierPartImpl(c);
    }

    public static boolean isUnicodeIdentifierPart(int codePoint) {
        return isUnicodeIdentifierPartImpl(codePoint);
    }

    public static boolean isUnicodeIdentifierStart(char c) {
        return isUnicodeIdentifierStartImpl(c);
    }

    public static boolean isUnicodeIdentifierStart(int codePoint) {
        return isUnicodeIdentifierStartImpl(codePoint);
    }

    public static boolean isUpperCase(char c) {
        return isUpperCase((int) c);
    }

    public static boolean isUpperCase(int codePoint) {
        if (65 <= codePoint && codePoint <= 90) {
            return true;
        }
        if (codePoint < NodeFilter.SHOW_COMMENT) {
            return false;
        }
        return isUpperCaseImpl(codePoint);
    }

    public static boolean isWhitespace(char c) {
        return isWhitespace((int) c);
    }

    public static boolean isWhitespace(int codePoint) {
        if ((codePoint >= 28 && codePoint <= 32) || (codePoint >= 9 && codePoint <= 13)) {
            return true;
        }
        if (codePoint < Modifier.SYNTHETIC) {
            return false;
        }
        if (codePoint == 5760 || codePoint == 6158) {
            return true;
        }
        if (codePoint < DateIntervalFormat.FORMAT_UTC || codePoint == 8199 || codePoint == 8239) {
            return false;
        }
        if (codePoint > DexFormat.MAX_TYPE_IDX) {
            return isWhitespaceImpl(codePoint);
        }
        if (codePoint <= 8202 || codePoint == 8232 || codePoint == 8233 || codePoint == 8287 || codePoint == 12288) {
            return true;
        }
        return false;
    }

    public static char reverseBytes(char c) {
        return (char) ((c << 8) | (c >> 8));
    }

    public static char toLowerCase(char c) {
        return (char) toLowerCase((int) c);
    }

    public static int toLowerCase(int codePoint) {
        if (65 > codePoint || codePoint > 90) {
            return codePoint >= ASN1Constants.CLASS_PRIVATE ? toLowerCaseImpl(codePoint) : codePoint;
        } else {
            return (char) (codePoint + 32);
        }
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    public static String toString(char value) {
        return String.valueOf(value);
    }

    public static char toTitleCase(char c) {
        return (char) toTitleCaseImpl(c);
    }

    public static int toTitleCase(int codePoint) {
        return toTitleCaseImpl(codePoint);
    }

    public static char toUpperCase(char c) {
        return (char) toUpperCase((int) c);
    }

    public static int toUpperCase(int codePoint) {
        if (97 > codePoint || codePoint > 122) {
            return codePoint >= Opcodes.OP_AND_INT_2ADDR ? toUpperCaseImpl(codePoint) : codePoint;
        } else {
            return (char) (codePoint - 32);
        }
    }
}
