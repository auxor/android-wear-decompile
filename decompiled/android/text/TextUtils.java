package android.text;

import android.content.res.Resources;
import android.media.AudioSystem;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.SystemProperties;
import android.text.format.DateFormat;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan.Standard;
import android.text.style.BackgroundColorSpan;
import android.text.style.BulletSpan;
import android.text.style.CharacterStyle;
import android.text.style.EasyEditSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.LocaleSpan;
import android.text.style.MetricAffectingSpan;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ReplacementSpan;
import android.text.style.ScaleXSpan;
import android.text.style.SpellCheckSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuggestionRangeSpan;
import android.text.style.SuggestionSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.TtsSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Printer;
import com.android.internal.R;
import com.android.internal.content.NativeLibraryHelper;
import com.android.internal.telephony.RILConstants;
import com.android.internal.util.ArrayUtils;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Pattern;
import libcore.icu.ICU;

public class TextUtils {
    public static final int ABSOLUTE_SIZE_SPAN = 16;
    public static final int ALIGNMENT_SPAN = 1;
    public static final int ANNOTATION = 18;
    private static String ARAB_SCRIPT_SUBTAG = null;
    public static final int BACKGROUND_COLOR_SPAN = 12;
    public static final int BULLET_SPAN = 8;
    public static final int CAP_MODE_CHARACTERS = 4096;
    public static final int CAP_MODE_SENTENCES = 16384;
    public static final int CAP_MODE_WORDS = 8192;
    public static final Creator<CharSequence> CHAR_SEQUENCE_CREATOR;
    public static final int EASY_EDIT_SPAN = 22;
    static final char[] ELLIPSIS_NORMAL;
    private static final String ELLIPSIS_STRING;
    static final char[] ELLIPSIS_TWO_DOTS;
    private static final String ELLIPSIS_TWO_DOTS_STRING;
    private static String[] EMPTY_STRING_ARRAY = null;
    private static final char FIRST_RIGHT_TO_LEFT = '\u0590';
    public static final int FIRST_SPAN = 1;
    public static final int FOREGROUND_COLOR_SPAN = 2;
    private static String HEBR_SCRIPT_SUBTAG = null;
    public static final int LAST_SPAN = 24;
    public static final int LEADING_MARGIN_SPAN = 10;
    public static final int LOCALE_SPAN = 23;
    public static final int QUOTE_SPAN = 9;
    public static final int RELATIVE_SIZE_SPAN = 3;
    public static final int SCALE_X_SPAN = 4;
    public static final int SPELL_CHECK_SPAN = 20;
    public static final int STRIKETHROUGH_SPAN = 5;
    public static final int STYLE_SPAN = 7;
    public static final int SUBSCRIPT_SPAN = 15;
    public static final int SUGGESTION_RANGE_SPAN = 21;
    public static final int SUGGESTION_SPAN = 19;
    public static final int SUPERSCRIPT_SPAN = 14;
    private static final String TAG = "TextUtils";
    public static final int TEXT_APPEARANCE_SPAN = 17;
    public static final int TTS_SPAN = 24;
    public static final int TYPEFACE_SPAN = 13;
    public static final int UNDERLINE_SPAN = 6;
    public static final int URL_SPAN = 11;
    private static final char ZWNBS_CHAR = '\ufeff';
    private static Object sLock;
    private static char[] sTemp;

    public interface EllipsizeCallback {
        void ellipsized(int i, int i2);
    }

    private static class Reverser implements CharSequence, GetChars {
        private int mEnd;
        private CharSequence mSource;
        private int mStart;

        public Reverser(java.lang.CharSequence r1, int r2, int r3) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.text.TextUtils.Reverser.<init>(java.lang.CharSequence, int, int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.text.TextUtils.Reverser.<init>(java.lang.CharSequence, int, int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e8
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.text.TextUtils.Reverser.<init>(java.lang.CharSequence, int, int):void");
        }

        public char charAt(int r1) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.text.TextUtils.Reverser.charAt(int):char
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.text.TextUtils.Reverser.charAt(int):char
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.text.TextUtils.Reverser.charAt(int):char");
        }

        public void getChars(int r1, int r2, char[] r3, int r4) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.text.TextUtils.Reverser.getChars(int, int, char[], int):void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.text.TextUtils.Reverser.getChars(int, int, char[], int):void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e5
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.text.TextUtils.Reverser.getChars(int, int, char[], int):void");
        }

        public int length() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.text.TextUtils.Reverser.length():int
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.text.TextUtils.Reverser.length():int
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e3
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.text.TextUtils.Reverser.length():int");
        }

        public java.lang.CharSequence subSequence(int r1, int r2) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.text.TextUtils.Reverser.subSequence(int, int):java.lang.CharSequence
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.text.TextUtils.Reverser.subSequence(int, int):java.lang.CharSequence
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.text.TextUtils.Reverser.subSequence(int, int):java.lang.CharSequence");
        }

        public java.lang.String toString() {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.text.TextUtils.Reverser.toString():java.lang.String
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:263)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.text.TextUtils.Reverser.toString():java.lang.String
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 8 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 00e9
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1196)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 9 more
*/
            /*
            // Can't load method instructions.
            */
            throw new UnsupportedOperationException("Method not decompiled: android.text.TextUtils.Reverser.toString():java.lang.String");
        }
    }

    public interface StringSplitter extends Iterable<String> {
        void setString(String str);
    }

    public static class SimpleStringSplitter implements StringSplitter, Iterator<String> {
        private char mDelimiter;
        private int mLength;
        private int mPosition;
        private String mString;

        public SimpleStringSplitter(char delimiter) {
            this.mDelimiter = delimiter;
        }

        public void setString(String string) {
            this.mString = string;
            this.mPosition = 0;
            this.mLength = this.mString.length();
        }

        public Iterator<String> iterator() {
            return this;
        }

        public boolean hasNext() {
            return this.mPosition < this.mLength;
        }

        public String next() {
            int end = this.mString.indexOf(this.mDelimiter, this.mPosition);
            if (end == -1) {
                end = this.mLength;
            }
            String nextString = this.mString.substring(this.mPosition, end);
            this.mPosition = end + TextUtils.FIRST_SPAN;
            return nextString;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public enum TruncateAt {
        START,
        MIDDLE,
        END,
        MARQUEE,
        END_SMALL
    }

    static {
        char[] cArr = new char[FIRST_SPAN];
        cArr[0] = '\u2026';
        ELLIPSIS_NORMAL = cArr;
        ELLIPSIS_STRING = new String(ELLIPSIS_NORMAL);
        cArr = new char[FIRST_SPAN];
        cArr[0] = '\u2025';
        ELLIPSIS_TWO_DOTS = cArr;
        ELLIPSIS_TWO_DOTS_STRING = new String(ELLIPSIS_TWO_DOTS);
        CHAR_SEQUENCE_CREATOR = new Creator<CharSequence>() {
            public CharSequence createFromParcel(Parcel p) {
                int kind = p.readInt();
                String string = p.readString();
                if (string == null) {
                    return null;
                }
                if (kind == TextUtils.FIRST_SPAN) {
                    return string;
                }
                SpannableString sp = new SpannableString(string);
                while (true) {
                    kind = p.readInt();
                    if (kind == 0) {
                        return sp;
                    }
                    switch (kind) {
                        case TextUtils.FIRST_SPAN /*1*/:
                            TextUtils.readSpan(p, sp, new Standard(p));
                            break;
                        case TextUtils.FOREGROUND_COLOR_SPAN /*2*/:
                            TextUtils.readSpan(p, sp, new ForegroundColorSpan(p));
                            break;
                        case TextUtils.RELATIVE_SIZE_SPAN /*3*/:
                            TextUtils.readSpan(p, sp, new RelativeSizeSpan(p));
                            break;
                        case TextUtils.SCALE_X_SPAN /*4*/:
                            TextUtils.readSpan(p, sp, new ScaleXSpan(p));
                            break;
                        case TextUtils.STRIKETHROUGH_SPAN /*5*/:
                            TextUtils.readSpan(p, sp, new StrikethroughSpan(p));
                            break;
                        case TextUtils.UNDERLINE_SPAN /*6*/:
                            TextUtils.readSpan(p, sp, new UnderlineSpan(p));
                            break;
                        case TextUtils.STYLE_SPAN /*7*/:
                            TextUtils.readSpan(p, sp, new StyleSpan(p));
                            break;
                        case TextUtils.BULLET_SPAN /*8*/:
                            TextUtils.readSpan(p, sp, new BulletSpan(p));
                            break;
                        case TextUtils.QUOTE_SPAN /*9*/:
                            TextUtils.readSpan(p, sp, new QuoteSpan(p));
                            break;
                        case TextUtils.LEADING_MARGIN_SPAN /*10*/:
                            TextUtils.readSpan(p, sp, new LeadingMarginSpan.Standard(p));
                            break;
                        case TextUtils.URL_SPAN /*11*/:
                            TextUtils.readSpan(p, sp, new URLSpan(p));
                            break;
                        case TextUtils.BACKGROUND_COLOR_SPAN /*12*/:
                            TextUtils.readSpan(p, sp, new BackgroundColorSpan(p));
                            break;
                        case TextUtils.TYPEFACE_SPAN /*13*/:
                            TextUtils.readSpan(p, sp, new TypefaceSpan(p));
                            break;
                        case TextUtils.SUPERSCRIPT_SPAN /*14*/:
                            TextUtils.readSpan(p, sp, new SuperscriptSpan(p));
                            break;
                        case TextUtils.SUBSCRIPT_SPAN /*15*/:
                            TextUtils.readSpan(p, sp, new SubscriptSpan(p));
                            break;
                        case TextUtils.ABSOLUTE_SIZE_SPAN /*16*/:
                            TextUtils.readSpan(p, sp, new AbsoluteSizeSpan(p));
                            break;
                        case TextUtils.TEXT_APPEARANCE_SPAN /*17*/:
                            TextUtils.readSpan(p, sp, new TextAppearanceSpan(p));
                            break;
                        case TextUtils.ANNOTATION /*18*/:
                            TextUtils.readSpan(p, sp, new Annotation(p));
                            break;
                        case TextUtils.SUGGESTION_SPAN /*19*/:
                            TextUtils.readSpan(p, sp, new SuggestionSpan(p));
                            break;
                        case TextUtils.SPELL_CHECK_SPAN /*20*/:
                            TextUtils.readSpan(p, sp, new SpellCheckSpan(p));
                            break;
                        case TextUtils.SUGGESTION_RANGE_SPAN /*21*/:
                            TextUtils.readSpan(p, sp, new SuggestionRangeSpan(p));
                            break;
                        case TextUtils.EASY_EDIT_SPAN /*22*/:
                            TextUtils.readSpan(p, sp, new EasyEditSpan(p));
                            break;
                        case TextUtils.LOCALE_SPAN /*23*/:
                            TextUtils.readSpan(p, sp, new LocaleSpan(p));
                            break;
                        case TextUtils.TTS_SPAN /*24*/:
                            TextUtils.readSpan(p, sp, new TtsSpan(p));
                            break;
                        default:
                            throw new RuntimeException("bogus span encoding " + kind);
                    }
                }
            }

            public CharSequence[] newArray(int size) {
                return new CharSequence[size];
            }
        };
        sLock = new Object();
        sTemp = null;
        EMPTY_STRING_ARRAY = new String[0];
        ARAB_SCRIPT_SUBTAG = "Arab";
        HEBR_SCRIPT_SUBTAG = "Hebr";
    }

    private TextUtils() {
    }

    public static void getChars(CharSequence s, int start, int end, char[] dest, int destoff) {
        Class<? extends CharSequence> c = s.getClass();
        if (c == String.class) {
            ((String) s).getChars(start, end, dest, destoff);
        } else if (c == StringBuffer.class) {
            ((StringBuffer) s).getChars(start, end, dest, destoff);
        } else if (c == StringBuilder.class) {
            ((StringBuilder) s).getChars(start, end, dest, destoff);
        } else if (s instanceof GetChars) {
            ((GetChars) s).getChars(start, end, dest, destoff);
        } else {
            int i = start;
            int destoff2 = destoff;
            while (i < end) {
                destoff = destoff2 + FIRST_SPAN;
                dest[destoff2] = s.charAt(i);
                i += FIRST_SPAN;
                destoff2 = destoff;
            }
            destoff = destoff2;
        }
    }

    public static int indexOf(CharSequence s, char ch) {
        return indexOf(s, ch, 0);
    }

    public static int indexOf(CharSequence s, char ch, int start) {
        if (s.getClass() == String.class) {
            return ((String) s).indexOf(ch, start);
        }
        return indexOf(s, ch, start, s.length());
    }

    public static int indexOf(CharSequence s, char ch, int start, int end) {
        Class<? extends CharSequence> c = s.getClass();
        int i;
        if ((s instanceof GetChars) || c == StringBuffer.class || c == StringBuilder.class || c == String.class) {
            char[] temp = obtain(500);
            while (start < end) {
                int segend = start + 500;
                if (segend > end) {
                    segend = end;
                }
                getChars(s, start, segend, temp, 0);
                int count = segend - start;
                for (i = 0; i < count; i += FIRST_SPAN) {
                    if (temp[i] == ch) {
                        recycle(temp);
                        return i + start;
                    }
                }
                start = segend;
            }
            recycle(temp);
            return -1;
        }
        for (i = start; i < end; i += FIRST_SPAN) {
            if (s.charAt(i) == ch) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(CharSequence s, char ch) {
        return lastIndexOf(s, ch, s.length() - 1);
    }

    public static int lastIndexOf(CharSequence s, char ch, int last) {
        if (s.getClass() == String.class) {
            return ((String) s).lastIndexOf(ch, last);
        }
        return lastIndexOf(s, ch, 0, last);
    }

    public static int lastIndexOf(CharSequence s, char ch, int start, int last) {
        if (last < 0) {
            return -1;
        }
        if (last >= s.length()) {
            last = s.length() - 1;
        }
        int end = last + FIRST_SPAN;
        Class<? extends CharSequence> c = s.getClass();
        int i;
        if ((s instanceof GetChars) || c == StringBuffer.class || c == StringBuilder.class || c == String.class) {
            char[] temp = obtain(500);
            while (start < end) {
                int segstart = end - 500;
                if (segstart < start) {
                    segstart = start;
                }
                getChars(s, segstart, end, temp, 0);
                for (i = (end - segstart) - 1; i >= 0; i--) {
                    if (temp[i] == ch) {
                        recycle(temp);
                        return i + segstart;
                    }
                }
                end = segstart;
            }
            recycle(temp);
            return -1;
        }
        for (i = end - 1; i >= start; i--) {
            if (s.charAt(i) == ch) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(CharSequence s, CharSequence needle) {
        return indexOf(s, needle, 0, s.length());
    }

    public static int indexOf(CharSequence s, CharSequence needle, int start) {
        return indexOf(s, needle, start, s.length());
    }

    public static int indexOf(CharSequence s, CharSequence needle, int start, int end) {
        int nlen = needle.length();
        if (nlen == 0) {
            return start;
        }
        char c = needle.charAt(0);
        while (true) {
            start = indexOf(s, c, start);
            if (start > end - nlen || start < 0) {
                return -1;
            }
            if (regionMatches(s, start, needle, 0, nlen)) {
                return start;
            }
            start += FIRST_SPAN;
        }
    }

    public static boolean regionMatches(CharSequence one, int toffset, CharSequence two, int ooffset, int len) {
        int tempLen = len * FOREGROUND_COLOR_SPAN;
        if (tempLen < len) {
            throw new IndexOutOfBoundsException();
        }
        char[] temp = obtain(tempLen);
        getChars(one, toffset, toffset + len, temp, 0);
        getChars(two, ooffset, ooffset + len, temp, len);
        boolean match = true;
        for (int i = 0; i < len; i += FIRST_SPAN) {
            if (temp[i] != temp[i + len]) {
                match = false;
                break;
            }
        }
        recycle(temp);
        return match;
    }

    public static String substring(CharSequence source, int start, int end) {
        if (source instanceof String) {
            return ((String) source).substring(start, end);
        }
        if (source instanceof StringBuilder) {
            return ((StringBuilder) source).substring(start, end);
        }
        if (source instanceof StringBuffer) {
            return ((StringBuffer) source).substring(start, end);
        }
        char[] temp = obtain(end - start);
        getChars(source, start, end, temp, 0);
        String ret = new String(temp, 0, end - start);
        recycle(temp);
        return ret;
    }

    public static CharSequence join(Iterable<CharSequence> list) {
        return join(Resources.getSystem().getText(R.string.list_delimeter), (Iterable) list);
    }

    public static String join(CharSequence delimiter, Object[] tokens) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        Object[] arr$ = tokens;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; i$ += FIRST_SPAN) {
            Object token = arr$[i$];
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(delimiter);
            }
            sb.append(token);
        }
        return sb.toString();
    }

    public static String join(CharSequence delimiter, Iterable tokens) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (Object token : tokens) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(delimiter);
            }
            sb.append(token);
        }
        return sb.toString();
    }

    public static String[] split(String text, String expression) {
        if (text.length() == 0) {
            return EMPTY_STRING_ARRAY;
        }
        return text.split(expression, -1);
    }

    public static String[] split(String text, Pattern pattern) {
        if (text.length() == 0) {
            return EMPTY_STRING_ARRAY;
        }
        return pattern.split(text, -1);
    }

    public static CharSequence stringOrSpannedString(CharSequence source) {
        if (source == null) {
            return null;
        }
        if (source instanceof SpannedString) {
            return source;
        }
        if (source instanceof Spanned) {
            return new SpannedString(source);
        }
        return source.toString();
    }

    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        return false;
    }

    public static int getTrimmedLength(CharSequence s) {
        int len = s.length();
        int start = 0;
        while (start < len && s.charAt(start) <= ' ') {
            start += FIRST_SPAN;
        }
        int end = len;
        while (end > start && s.charAt(end - 1) <= ' ') {
            end--;
        }
        return end - start;
    }

    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) {
            return true;
        }
        if (!(a == null || b == null)) {
            int length = a.length();
            if (length == b.length()) {
                if ((a instanceof String) && (b instanceof String)) {
                    return a.equals(b);
                }
                for (int i = 0; i < length; i += FIRST_SPAN) {
                    if (a.charAt(i) != b.charAt(i)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static CharSequence getReverse(CharSequence source, int start, int end) {
        return new Reverser(source, start, end);
    }

    public static void writeToParcel(CharSequence cs, Parcel p, int parcelableFlags) {
        if (cs instanceof Spanned) {
            p.writeInt(0);
            p.writeString(cs.toString());
            Spanned sp = (Spanned) cs;
            Object[] os = sp.getSpans(0, cs.length(), Object.class);
            for (int i = 0; i < os.length; i += FIRST_SPAN) {
                Object o = os[i];
                CharacterStyle characterStyle = os[i];
                if (characterStyle instanceof CharacterStyle) {
                    characterStyle = characterStyle.getUnderlying();
                }
                if (characterStyle instanceof ParcelableSpan) {
                    ParcelableSpan ps = (ParcelableSpan) characterStyle;
                    int spanTypeId = ps.getSpanTypeId();
                    if (spanTypeId < FIRST_SPAN || spanTypeId > TTS_SPAN) {
                        Log.e(TAG, "external class \"" + ps.getClass().getSimpleName() + "\" is attempting to use the frameworks-only ParcelableSpan" + " interface");
                    } else {
                        p.writeInt(spanTypeId);
                        ps.writeToParcel(p, parcelableFlags);
                        writeWhere(p, sp, o);
                    }
                }
            }
            p.writeInt(0);
            return;
        }
        p.writeInt(FIRST_SPAN);
        if (cs != null) {
            p.writeString(cs.toString());
        } else {
            p.writeString(null);
        }
    }

    private static void writeWhere(Parcel p, Spanned sp, Object o) {
        p.writeInt(sp.getSpanStart(o));
        p.writeInt(sp.getSpanEnd(o));
        p.writeInt(sp.getSpanFlags(o));
    }

    public static void dumpSpans(CharSequence cs, Printer printer, String prefix) {
        if (cs instanceof Spanned) {
            Spanned sp = (Spanned) cs;
            Object[] os = sp.getSpans(0, cs.length(), Object.class);
            for (int i = 0; i < os.length; i += FIRST_SPAN) {
                Object o = os[i];
                printer.println(prefix + cs.subSequence(sp.getSpanStart(o), sp.getSpanEnd(o)) + ": " + Integer.toHexString(System.identityHashCode(o)) + " " + o.getClass().getCanonicalName() + " (" + sp.getSpanStart(o) + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + sp.getSpanEnd(o) + ") fl=#" + sp.getSpanFlags(o));
            }
            return;
        }
        printer.println(prefix + cs + ": (no spans)");
    }

    public static CharSequence replace(CharSequence template, String[] sources, CharSequence[] destinations) {
        int i;
        CharSequence tb = new SpannableStringBuilder(template);
        for (i = 0; i < sources.length; i += FIRST_SPAN) {
            int where = indexOf(tb, sources[i]);
            if (where >= 0) {
                tb.setSpan(sources[i], where, sources[i].length() + where, 33);
            }
        }
        for (i = 0; i < sources.length; i += FIRST_SPAN) {
            int start = tb.getSpanStart(sources[i]);
            int end = tb.getSpanEnd(sources[i]);
            if (start >= 0) {
                tb.replace(start, end, destinations[i]);
            }
        }
        return tb;
    }

    public static CharSequence expandTemplate(CharSequence template, CharSequence... values) {
        if (values.length > QUOTE_SPAN) {
            throw new IllegalArgumentException("max of 9 values are supported");
        }
        SpannableStringBuilder ssb = new SpannableStringBuilder(template);
        int i = 0;
        while (i < ssb.length()) {
            try {
                if (ssb.charAt(i) == '^') {
                    char next = ssb.charAt(i + FIRST_SPAN);
                    if (next == '^') {
                        ssb.delete(i + FIRST_SPAN, i + FOREGROUND_COLOR_SPAN);
                        i += FIRST_SPAN;
                    } else if (Character.isDigit(next)) {
                        int which = Character.getNumericValue(next) - 1;
                        if (which < 0) {
                            throw new IllegalArgumentException("template requests value ^" + (which + FIRST_SPAN));
                        } else if (which >= values.length) {
                            throw new IllegalArgumentException("template requests value ^" + (which + FIRST_SPAN) + "; only " + values.length + " provided");
                        } else {
                            ssb.replace(i, i + FOREGROUND_COLOR_SPAN, values[which]);
                            i += values[which].length();
                        }
                    }
                }
                i += FIRST_SPAN;
            } catch (IndexOutOfBoundsException e) {
            }
        }
        return ssb;
    }

    public static int getOffsetBefore(CharSequence text, int offset) {
        if (offset == 0 || offset == FIRST_SPAN) {
            return 0;
        }
        char c = text.charAt(offset - 1);
        if (c < '\udc00' || c > '\udfff') {
            offset--;
        } else {
            char c1 = text.charAt(offset - 2);
            if (c1 < '\ud800' || c1 > '\udbff') {
                offset--;
            } else {
                offset -= 2;
            }
        }
        if (text instanceof Spanned) {
            ReplacementSpan[] spans = (ReplacementSpan[]) ((Spanned) text).getSpans(offset, offset, ReplacementSpan.class);
            for (int i = 0; i < spans.length; i += FIRST_SPAN) {
                int start = ((Spanned) text).getSpanStart(spans[i]);
                int end = ((Spanned) text).getSpanEnd(spans[i]);
                if (start < offset && end > offset) {
                    offset = start;
                }
            }
        }
        return offset;
    }

    public static int getOffsetAfter(CharSequence text, int offset) {
        int len = text.length();
        if (offset == len || offset == len - 1) {
            return len;
        }
        char c = text.charAt(offset);
        if (c < '\ud800' || c > '\udbff') {
            offset += FIRST_SPAN;
        } else {
            char c1 = text.charAt(offset + FIRST_SPAN);
            if (c1 < '\udc00' || c1 > '\udfff') {
                offset += FIRST_SPAN;
            } else {
                offset += FOREGROUND_COLOR_SPAN;
            }
        }
        if (text instanceof Spanned) {
            ReplacementSpan[] spans = (ReplacementSpan[]) ((Spanned) text).getSpans(offset, offset, ReplacementSpan.class);
            for (int i = 0; i < spans.length; i += FIRST_SPAN) {
                int start = ((Spanned) text).getSpanStart(spans[i]);
                int end = ((Spanned) text).getSpanEnd(spans[i]);
                if (start < offset && end > offset) {
                    offset = end;
                }
            }
        }
        return offset;
    }

    private static void readSpan(Parcel p, Spannable sp, Object o) {
        sp.setSpan(o, p.readInt(), p.readInt(), p.readInt());
    }

    public static void copySpansFrom(Spanned source, int start, int end, Class kind, Spannable dest, int destoff) {
        if (kind == null) {
            kind = Object.class;
        }
        Object[] spans = source.getSpans(start, end, kind);
        for (int i = 0; i < spans.length; i += FIRST_SPAN) {
            int st = source.getSpanStart(spans[i]);
            int en = source.getSpanEnd(spans[i]);
            int fl = source.getSpanFlags(spans[i]);
            if (st < start) {
                st = start;
            }
            if (en > end) {
                en = end;
            }
            dest.setSpan(spans[i], (st - start) + destoff, (en - start) + destoff, fl);
        }
    }

    public static CharSequence ellipsize(CharSequence text, TextPaint p, float avail, TruncateAt where) {
        return ellipsize(text, p, avail, where, false, null);
    }

    public static CharSequence ellipsize(CharSequence text, TextPaint paint, float avail, TruncateAt where, boolean preserveLength, EllipsizeCallback callback) {
        return ellipsize(text, paint, avail, where, preserveLength, callback, TextDirectionHeuristics.FIRSTSTRONG_LTR, where == TruncateAt.END_SMALL ? ELLIPSIS_TWO_DOTS_STRING : ELLIPSIS_STRING);
    }

    public static CharSequence ellipsize(CharSequence text, TextPaint paint, float avail, TruncateAt where, boolean preserveLength, EllipsizeCallback callback, TextDirectionHeuristic textDir, String ellipsis) {
        int len = text.length();
        MeasuredText mt = MeasuredText.obtain();
        try {
            if (setPara(mt, paint, text, 0, text.length(), textDir) <= avail) {
                if (callback != null) {
                    callback.ellipsized(0, 0);
                }
                MeasuredText.recycle(mt);
                return text;
            }
            int left;
            Spanned sp;
            avail -= paint.measureText(ellipsis);
            int right = len;
            if (avail < 0.0f) {
                left = 0;
            } else if (where == TruncateAt.START) {
                right = len - mt.breakText(len, false, avail);
                left = 0;
            } else if (where == TruncateAt.END || where == TruncateAt.END_SMALL) {
                left = mt.breakText(len, true, avail);
            } else {
                right = len - mt.breakText(len, false, avail / 2.0f);
                left = mt.breakText(right, true, avail - mt.measure(right, len));
            }
            if (callback != null) {
                callback.ellipsized(left, right);
            }
            char[] buf = mt.mChars;
            if (text instanceof Spanned) {
                Spanned spanned = (Spanned) text;
            } else {
                sp = null;
            }
            int remaining = len - (right - left);
            if (preserveLength) {
                int left2;
                if (remaining > 0) {
                    left2 = left + FIRST_SPAN;
                    buf[left] = ellipsis.charAt(0);
                } else {
                    left2 = left;
                }
                for (int i = left2; i < right; i += FIRST_SPAN) {
                    buf[i] = ZWNBS_CHAR;
                }
                String str = new String(buf, 0, len);
                if (sp == null) {
                    MeasuredText.recycle(mt);
                    return str;
                }
                SpannableString ss = new SpannableString(str);
                copySpansFrom(sp, 0, len, Object.class, ss, 0);
                MeasuredText.recycle(mt);
                return ss;
            } else if (remaining == 0) {
                text = "";
                MeasuredText.recycle(mt);
                return text;
            } else if (sp == null) {
                StringBuilder stringBuilder = new StringBuilder(ellipsis.length() + remaining);
                stringBuilder.append(buf, 0, left);
                stringBuilder.append(ellipsis);
                stringBuilder.append(buf, right, len - right);
                text = stringBuilder.toString();
                MeasuredText.recycle(mt);
                return text;
            } else {
                SpannableStringBuilder ssb = new SpannableStringBuilder();
                ssb.append(text, 0, left);
                ssb.append((CharSequence) ellipsis);
                ssb.append(text, right, len);
                MeasuredText.recycle(mt);
                return ssb;
            }
        } catch (Throwable th) {
            MeasuredText.recycle(mt);
        }
    }

    public static CharSequence commaEllipsize(CharSequence text, TextPaint p, float avail, String oneMore, String more) {
        return commaEllipsize(text, p, avail, oneMore, more, TextDirectionHeuristics.FIRSTSTRONG_LTR);
    }

    public static CharSequence commaEllipsize(CharSequence text, TextPaint p, float avail, String oneMore, String more, TextDirectionHeuristic textDir) {
        MeasuredText mt = MeasuredText.obtain();
        try {
            int len = text.length();
            if (setPara(mt, p, text, 0, len, textDir) <= avail) {
                return text;
            }
            int i;
            char[] buf = mt.mChars;
            int commaCount = 0;
            for (i = 0; i < len; i += FIRST_SPAN) {
                if (buf[i] == ',') {
                    commaCount += FIRST_SPAN;
                }
            }
            int remaining = commaCount + FIRST_SPAN;
            int ok = 0;
            String okFormat = "";
            int w = 0;
            int count = 0;
            float[] widths = mt.mWidths;
            MeasuredText tempMt = MeasuredText.obtain();
            for (i = 0; i < len; i += FIRST_SPAN) {
                w = (int) (((float) w) + widths[i]);
                if (buf[i] == ',') {
                    String format;
                    count += FIRST_SPAN;
                    remaining--;
                    if (remaining == FIRST_SPAN) {
                        format = " " + oneMore;
                    } else {
                        StringBuilder append = new StringBuilder().append(" ");
                        Object[] objArr = new Object[FIRST_SPAN];
                        objArr[0] = Integer.valueOf(remaining);
                        format = append.append(String.format(more, objArr)).toString();
                    }
                    tempMt.setPara(format, 0, format.length(), textDir);
                    if (((float) w) + tempMt.addStyleRun(p, tempMt.mLen, null) <= avail) {
                        ok = i + FIRST_SPAN;
                        okFormat = format;
                    }
                }
            }
            MeasuredText.recycle(tempMt);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(okFormat);
            spannableStringBuilder.insert(0, text, 0, ok);
            MeasuredText.recycle(mt);
            return spannableStringBuilder;
        } finally {
            MeasuredText.recycle(mt);
        }
    }

    private static float setPara(MeasuredText mt, TextPaint paint, CharSequence text, int start, int end, TextDirectionHeuristic textDir) {
        Spanned sp;
        mt.setPara(text, start, end, textDir);
        if (text instanceof Spanned) {
            sp = (Spanned) text;
        } else {
            sp = null;
        }
        int len = end - start;
        if (sp == null) {
            return mt.addStyleRun(paint, len, null);
        }
        float width = 0.0f;
        int spanStart = 0;
        while (spanStart < len) {
            int spanEnd = sp.nextSpanTransition(spanStart, len, MetricAffectingSpan.class);
            width += mt.addStyleRun(paint, (MetricAffectingSpan[]) removeEmptySpans((MetricAffectingSpan[]) sp.getSpans(spanStart, spanEnd, MetricAffectingSpan.class), sp, MetricAffectingSpan.class), spanEnd - spanStart, null);
            spanStart = spanEnd;
        }
        return width;
    }

    static boolean doesNotNeedBidi(CharSequence s, int start, int end) {
        for (int i = start; i < end; i += FIRST_SPAN) {
            if (s.charAt(i) >= FIRST_RIGHT_TO_LEFT) {
                return false;
            }
        }
        return true;
    }

    static boolean doesNotNeedBidi(char[] text, int start, int len) {
        int i = start;
        int e = i + len;
        while (i < e) {
            if (text[i] >= FIRST_RIGHT_TO_LEFT) {
                return false;
            }
            i += FIRST_SPAN;
        }
        return true;
    }

    static char[] obtain(int len) {
        synchronized (sLock) {
            char[] buf = sTemp;
            sTemp = null;
        }
        if (buf == null || buf.length < len) {
            return ArrayUtils.newUnpaddedCharArray(len);
        }
        return buf;
    }

    static void recycle(char[] temp) {
        if (temp.length <= RILConstants.RIL_UNSOL_RESPONSE_RADIO_STATE_CHANGED) {
            synchronized (sLock) {
                sTemp = temp;
            }
        }
    }

    public static String htmlEncode(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i += FIRST_SPAN) {
            char c = s.charAt(i);
            switch (c) {
                case RILConstants.RIL_REQUEST_SET_CALL_FORWARD /*34*/:
                    sb.append("&quot;");
                    break;
                case RILConstants.RIL_REQUEST_GET_IMEI /*38*/:
                    sb.append("&amp;");
                    break;
                case RILConstants.RIL_REQUEST_GET_IMEISV /*39*/:
                    sb.append("&#39;");
                    break;
                case RILConstants.RIL_REQUEST_OEM_HOOK_STRINGS /*60*/:
                    sb.append("&lt;");
                    break;
                case RILConstants.RIL_REQUEST_SET_SUPP_SVC_NOTIFICATION /*62*/:
                    sb.append("&gt;");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    public static CharSequence concat(CharSequence... text) {
        if (text.length == 0) {
            return "";
        }
        if (text.length == FIRST_SPAN) {
            return text[0];
        }
        int i;
        boolean spanned = false;
        for (i = 0; i < text.length; i += FIRST_SPAN) {
            if (text[i] instanceof Spanned) {
                spanned = true;
                break;
            }
        }
        StringBuilder sb = new StringBuilder();
        for (i = 0; i < text.length; i += FIRST_SPAN) {
            sb.append(text[i]);
        }
        if (!spanned) {
            return sb.toString();
        }
        SpannableString ss = new SpannableString(sb);
        int off = 0;
        for (i = 0; i < text.length; i += FIRST_SPAN) {
            int len = text[i].length();
            if (text[i] instanceof Spanned) {
                copySpansFrom((Spanned) text[i], 0, len, Object.class, ss, off);
            }
            off += len;
        }
        return new SpannedString(ss);
    }

    public static boolean isGraphic(CharSequence str) {
        int len = str.length();
        for (int i = 0; i < len; i += FIRST_SPAN) {
            int gc = Character.getType(str.charAt(i));
            if (gc != SUBSCRIPT_SPAN && gc != ABSOLUTE_SIZE_SPAN && gc != SUGGESTION_SPAN && gc != 0 && gc != TYPEFACE_SPAN && gc != SUPERSCRIPT_SPAN && gc != BACKGROUND_COLOR_SPAN) {
                return true;
            }
        }
        return false;
    }

    public static boolean isGraphic(char c) {
        int gc = Character.getType(c);
        return (gc == SUBSCRIPT_SPAN || gc == ABSOLUTE_SIZE_SPAN || gc == SUGGESTION_SPAN || gc == 0 || gc == TYPEFACE_SPAN || gc == SUPERSCRIPT_SPAN || gc == BACKGROUND_COLOR_SPAN) ? false : true;
    }

    public static boolean isDigitsOnly(CharSequence str) {
        int len = str.length();
        for (int i = 0; i < len; i += FIRST_SPAN) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isPrintableAscii(char c) {
        return (' ' <= c && c <= '~') || c == '\r' || c == '\n';
    }

    public static boolean isPrintableAsciiOnly(CharSequence str) {
        int len = str.length();
        for (int i = 0; i < len; i += FIRST_SPAN) {
            if (!isPrintableAscii(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static int getCapsMode(CharSequence cs, int off, int reqModes) {
        if (off < 0) {
            return 0;
        }
        int mode = 0;
        if ((reqModes & CAP_MODE_CHARACTERS) != 0) {
            mode = 0 | CAP_MODE_CHARACTERS;
        }
        if ((reqModes & AudioSystem.DEVICE_OUT_ALL_USB) == 0) {
            return mode;
        }
        int i = off;
        while (i > 0) {
            char c = cs.charAt(i - 1);
            if (c != '\"' && c != DateFormat.QUOTE && Character.getType(c) != SUGGESTION_RANGE_SPAN) {
                break;
            }
            i--;
        }
        int j = i;
        while (j > 0) {
            c = cs.charAt(j - 1);
            if (c != ' ' && c != '\t') {
                break;
            }
            j--;
        }
        if (j == 0 || cs.charAt(j - 1) == '\n') {
            return mode | CAP_MODE_WORDS;
        }
        if ((reqModes & CAP_MODE_SENTENCES) == 0) {
            if (i != j) {
                return mode | CAP_MODE_WORDS;
            }
            return mode;
        } else if (i == j) {
            return mode;
        } else {
            while (j > 0) {
                c = cs.charAt(j - 1);
                if (c != '\"' && c != DateFormat.QUOTE && Character.getType(c) != EASY_EDIT_SPAN) {
                    break;
                }
                j--;
            }
            if (j <= 0) {
                return mode;
            }
            c = cs.charAt(j - 1);
            if (c != '.' && c != '?' && c != '!') {
                return mode;
            }
            if (c == '.') {
                for (int k = j - 2; k >= 0; k--) {
                    c = cs.charAt(k);
                    if (c == '.') {
                        return mode;
                    }
                    if (!Character.isLetter(c)) {
                        break;
                    }
                }
            }
            return mode | CAP_MODE_SENTENCES;
        }
    }

    public static boolean delimitedStringContains(String delimitedString, char delimiter, String item) {
        if (isEmpty(delimitedString) || isEmpty(item)) {
            return false;
        }
        int pos = -1;
        int length = delimitedString.length();
        while (true) {
            pos = delimitedString.indexOf(item, pos + FIRST_SPAN);
            if (pos == -1) {
                return false;
            }
            if (pos <= 0 || delimitedString.charAt(pos - 1) == delimiter) {
                int expectedDelimiterPos = pos + item.length();
                if (expectedDelimiterPos == length) {
                    return true;
                }
                if (delimitedString.charAt(expectedDelimiterPos) == delimiter) {
                    return true;
                }
            }
        }
    }

    public static <T> T[] removeEmptySpans(T[] spans, Spanned spanned, Class<T> klass) {
        T[] copy = null;
        int count = 0;
        for (int i = 0; i < spans.length; i += FIRST_SPAN) {
            T span = spans[i];
            if (spanned.getSpanStart(span) == spanned.getSpanEnd(span)) {
                if (copy == null) {
                    copy = (Object[]) ((Object[]) Array.newInstance(klass, spans.length - 1));
                    System.arraycopy(spans, 0, copy, 0, i);
                    count = i;
                }
            } else if (copy != null) {
                copy[count] = span;
                count += FIRST_SPAN;
            }
        }
        if (copy == null) {
            return spans;
        }
        Object[] result = (Object[]) ((Object[]) Array.newInstance(klass, count));
        System.arraycopy(copy, 0, result, 0, count);
        return result;
    }

    public static long packRangeInLong(int start, int end) {
        return (((long) start) << 32) | ((long) end);
    }

    public static int unpackRangeStartFromLong(long range) {
        return (int) (range >>> 32);
    }

    public static int unpackRangeEndFromLong(long range) {
        return (int) (4294967295L & range);
    }

    public static int getLayoutDirectionFromLocale(Locale locale) {
        if (!(locale == null || locale.equals(Locale.ROOT))) {
            String scriptSubtag = ICU.addLikelySubtags(locale).getScript();
            if (scriptSubtag == null) {
                return getLayoutDirectionFromFirstChar(locale);
            }
            if (scriptSubtag.equalsIgnoreCase(ARAB_SCRIPT_SUBTAG) || scriptSubtag.equalsIgnoreCase(HEBR_SCRIPT_SUBTAG)) {
                return FIRST_SPAN;
            }
        }
        if (SystemProperties.getBoolean("debug.force_rtl", false)) {
            return FIRST_SPAN;
        }
        return 0;
    }

    private static int getLayoutDirectionFromFirstChar(Locale locale) {
        switch (Character.getDirectionality(locale.getDisplayName(locale).charAt(0))) {
            case FIRST_SPAN /*1*/:
            case FOREGROUND_COLOR_SPAN /*2*/:
                return FIRST_SPAN;
            default:
                return 0;
        }
    }
}
