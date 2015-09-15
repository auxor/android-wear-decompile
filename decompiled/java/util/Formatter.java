package java.util;

import dalvik.bytecode.Opcodes;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.nio.charset.Charset;
import java.sql.Types;
import java.text.CharacterIterator;
import java.util.jar.Pack200.Unpacker;
import libcore.icu.DateIntervalFormat;
import libcore.icu.LocaleData;
import libcore.icu.NativeDecimalFormat;
import libcore.io.IoUtils;
import org.apache.harmony.security.asn1.ASN1Constants;
import org.apache.harmony.security.provider.crypto.SHA1Constants;
import org.w3c.dom.traversal.NodeFilter;
import org.xmlpull.v1.XmlPullParser;

public final class Formatter implements Closeable, Flushable {
    private static final char[] ZEROS;
    private static final ThreadLocal<CachedDecimalFormat> cachedDecimalFormat;
    private Object arg;
    private boolean closed;
    private FormatToken formatToken;
    private IOException lastIOException;
    private Locale locale;
    private LocaleData localeData;
    private Appendable out;

    public enum BigDecimalLayoutForm {
        SCIENTIFIC,
        DECIMAL_FLOAT
    }

    private static class CachedDecimalFormat {
        public LocaleData currentLocaleData;
        public String currentPattern;
        public NativeDecimalFormat decimalFormat;

        public NativeDecimalFormat update(LocaleData localeData, String pattern) {
            if (this.decimalFormat == null) {
                this.currentPattern = pattern;
                this.currentLocaleData = localeData;
                this.decimalFormat = new NativeDecimalFormat(this.currentPattern, this.currentLocaleData);
            }
            if (!pattern.equals(this.currentPattern)) {
                this.decimalFormat.applyPattern(pattern);
                this.currentPattern = pattern;
            }
            if (localeData != this.currentLocaleData) {
                this.decimalFormat.setDecimalFormatSymbols(localeData);
                this.currentLocaleData = localeData;
            }
            return this.decimalFormat;
        }
    }

    private static class FormatSpecifierParser {
        private String format;
        private int i;
        private int length;
        private int startIndex;

        FormatSpecifierParser(String format) {
            this.format = format;
            this.length = format.length();
        }

        FormatToken parseFormatToken(int offset) {
            this.startIndex = offset;
            this.i = offset;
            return parseArgumentIndexAndFlags(new FormatToken());
        }

        String getFormatSpecifierText() {
            return this.format.substring(this.startIndex, this.i);
        }

        private int peek() {
            return this.i < this.length ? this.format.charAt(this.i) : -1;
        }

        private char advance() {
            if (this.i >= this.length) {
                throw unknownFormatConversionException();
            }
            String str = this.format;
            int i = this.i;
            this.i = i + 1;
            return str.charAt(i);
        }

        private UnknownFormatConversionException unknownFormatConversionException() {
            throw new UnknownFormatConversionException(getFormatSpecifierText());
        }

        private FormatToken parseArgumentIndexAndFlags(FormatToken token) {
            int position = this.i;
            int ch = peek();
            if (Character.isDigit(ch)) {
                int number = nextInt();
                if (peek() == 36) {
                    advance();
                    if (number == -1) {
                        throw new MissingFormatArgumentException(getFormatSpecifierText());
                    }
                    token.setArgIndex(Math.max(0, number - 1));
                } else if (ch != 48) {
                    return parseWidth(token, number);
                } else {
                    this.i = position;
                }
            } else if (ch == 60) {
                token.setArgIndex(-2);
                advance();
            }
            while (token.setFlag(peek())) {
                advance();
            }
            ch = peek();
            if (Character.isDigit(ch)) {
                return parseWidth(token, nextInt());
            }
            if (ch == 46) {
                return parsePrecision(token);
            }
            return parseConversionType(token);
        }

        private FormatToken parseWidth(FormatToken token, int width) {
            token.setWidth(width);
            if (peek() == 46) {
                return parsePrecision(token);
            }
            return parseConversionType(token);
        }

        private FormatToken parsePrecision(FormatToken token) {
            advance();
            if (Character.isDigit(peek())) {
                token.setPrecision(nextInt());
                return parseConversionType(token);
            }
            throw unknownFormatConversionException();
        }

        private FormatToken parseConversionType(FormatToken token) {
            char conversionType = advance();
            token.setConversionType(conversionType);
            if (conversionType == 't' || conversionType == 'T') {
                token.setDateSuffix(advance());
            }
            return token;
        }

        private int nextInt() {
            long value = 0;
            while (this.i < this.length && Character.isDigit(this.format.charAt(this.i))) {
                long j = 10 * value;
                String str = this.format;
                int i = this.i;
                this.i = i + 1;
                value = j + ((long) (str.charAt(i) - 48));
                if (value > 2147483647L) {
                    return failNextInt();
                }
            }
            return (int) value;
        }

        private int failNextInt() {
            while (Character.isDigit(peek())) {
                advance();
            }
            return -1;
        }
    }

    private static class FormatToken {
        static final int DEFAULT_PRECISION = 6;
        static final int FLAGS_UNSET = 0;
        static final int FLAG_ZERO = 16;
        static final int LAST_ARGUMENT_INDEX = -2;
        static final int UNSET = -1;
        private int argIndex;
        private char conversionType;
        private char dateSuffix;
        boolean flagComma;
        boolean flagMinus;
        boolean flagParenthesis;
        boolean flagPlus;
        boolean flagSharp;
        boolean flagSpace;
        boolean flagZero;
        private int precision;
        private StringBuilder strFlags;
        private int width;

        private FormatToken() {
            this.argIndex = UNSET;
            this.conversionType = CharacterIterator.DONE;
            this.precision = UNSET;
            this.width = UNSET;
        }

        boolean isDefault() {
            return (this.flagComma || this.flagMinus || this.flagParenthesis || this.flagPlus || this.flagSharp || this.flagSpace || this.flagZero || this.width != UNSET || this.precision != UNSET) ? false : true;
        }

        boolean isPrecisionSet() {
            return this.precision != UNSET;
        }

        int getArgIndex() {
            return this.argIndex;
        }

        void setArgIndex(int index) {
            this.argIndex = index;
        }

        int getWidth() {
            return this.width;
        }

        void setWidth(int width) {
            this.width = width;
        }

        int getPrecision() {
            return this.precision;
        }

        void setPrecision(int precise) {
            this.precision = precise;
        }

        String getStrFlags() {
            return this.strFlags != null ? this.strFlags.toString() : XmlPullParser.NO_NAMESPACE;
        }

        boolean setFlag(int ch) {
            boolean dupe;
            switch (ch) {
                case NodeFilter.SHOW_ENTITY /*32*/:
                    dupe = this.flagSpace;
                    this.flagSpace = true;
                    break;
                case ASN1Constants.TAG_C_BITSTRING /*35*/:
                    dupe = this.flagSharp;
                    this.flagSharp = true;
                    break;
                case Opcodes.OP_GOTO /*40*/:
                    dupe = this.flagParenthesis;
                    this.flagParenthesis = true;
                    break;
                case Opcodes.OP_PACKED_SWITCH /*43*/:
                    dupe = this.flagPlus;
                    this.flagPlus = true;
                    break;
                case ASN1Constants.TAG_C_UTF8STRING /*44*/:
                    dupe = this.flagComma;
                    this.flagComma = true;
                    break;
                case Opcodes.OP_CMPL_FLOAT /*45*/:
                    dupe = this.flagMinus;
                    this.flagMinus = true;
                    break;
                case ASN1Constants.TAG_C_SEQUENCEOF /*48*/:
                    dupe = this.flagZero;
                    this.flagZero = true;
                    break;
                default:
                    return false;
            }
            if (dupe) {
                throw new DuplicateFormatFlagsException(String.valueOf(ch));
            }
            if (this.strFlags == null) {
                this.strFlags = new StringBuilder(7);
            }
            this.strFlags.append((char) ch);
            return true;
        }

        char getConversionType() {
            return this.conversionType;
        }

        void setConversionType(char c) {
            this.conversionType = c;
        }

        char getDateSuffix() {
            return this.dateSuffix;
        }

        void setDateSuffix(char c) {
            this.dateSuffix = c;
        }

        boolean requireArgument() {
            return (this.conversionType == '%' || this.conversionType == 'n') ? false : true;
        }

        void checkFlags(Object arg) {
            boolean allowComma = false;
            boolean allowMinus = true;
            boolean z = false;
            boolean z2 = false;
            boolean allowSharp = false;
            boolean z3 = false;
            boolean allowZero = false;
            boolean allowPrecision = true;
            boolean allowWidth = true;
            boolean allowArgument = true;
            switch (this.conversionType) {
                case Opcodes.OP_FILLED_NEW_ARRAY_RANGE /*37*/:
                    allowArgument = false;
                    allowPrecision = false;
                    break;
                case 'A':
                case Opcodes.OP_SGET_WIDE /*97*/:
                    allowZero = true;
                    z3 = true;
                    allowSharp = true;
                    z2 = true;
                    break;
                case 'B':
                case Opcodes.OP_AGET_BYTE /*72*/:
                case Opcodes.OP_SGET_OBJECT /*98*/:
                case Opcodes.OP_SPUT_WIDE /*104*/:
                    break;
                case 'C':
                case Opcodes.OP_IGET_OBJECT /*84*/:
                case Opcodes.OP_SGET_BOOLEAN /*99*/:
                case Opcodes.OP_INVOKE_VIRTUAL_RANGE /*116*/:
                    allowPrecision = false;
                    break;
                case Opcodes.OP_AGET_WIDE /*69*/:
                case Opcodes.OP_SGET_CHAR /*101*/:
                    allowZero = true;
                    z3 = true;
                    allowSharp = true;
                    z2 = true;
                    z = true;
                    break;
                case Opcodes.OP_AGET_BOOLEAN /*71*/:
                case Opcodes.OP_SPUT /*103*/:
                    allowZero = true;
                    z3 = true;
                    z2 = true;
                    z = true;
                    allowComma = true;
                    break;
                case Opcodes.OP_IGET_WIDE /*83*/:
                case 's':
                    if (arg instanceof Formattable) {
                        allowSharp = true;
                        break;
                    }
                    break;
                case Opcodes.OP_IGET_SHORT /*88*/:
                case Opcodes.OP_INVOKE_SUPER /*111*/:
                case Opcodes.OP_INVOKE_INTERFACE_RANGE /*120*/:
                    allowZero = true;
                    allowSharp = true;
                    if (arg == null || (arg instanceof BigInteger)) {
                        z3 = true;
                        z2 = true;
                        z = true;
                    }
                    allowPrecision = false;
                    break;
                case Opcodes.OP_SGET_BYTE /*100*/:
                    allowZero = true;
                    z3 = true;
                    z2 = true;
                    z = true;
                    allowComma = true;
                    allowPrecision = false;
                    break;
                case Opcodes.OP_SGET_SHORT /*102*/:
                    allowZero = true;
                    z3 = true;
                    allowSharp = true;
                    z2 = true;
                    z = true;
                    allowComma = true;
                    break;
                case Opcodes.OP_INVOKE_VIRTUAL /*110*/:
                    allowMinus = false;
                    allowWidth = false;
                    allowPrecision = false;
                    allowArgument = false;
                    break;
                default:
                    throw unknownFormatConversionException();
            }
            String mismatch = null;
            if (!allowComma && this.flagComma) {
                mismatch = ",";
            } else if (!allowMinus && this.flagMinus) {
                mismatch = "-";
            } else if (!z && this.flagParenthesis) {
                mismatch = "(";
            } else if (!z2 && this.flagPlus) {
                mismatch = "+";
            } else if (!allowSharp && this.flagSharp) {
                mismatch = "#";
            } else if (!z3 && this.flagSpace) {
                mismatch = " ";
            } else if (!allowZero && this.flagZero) {
                mismatch = "0";
            }
            if (mismatch != null) {
                if (this.conversionType == 'n') {
                    throw new IllegalFormatFlagsException(mismatch);
                }
                throw new FormatFlagsConversionMismatchException(mismatch, this.conversionType);
            } else if ((this.flagMinus || this.flagZero) && this.width == UNSET) {
                throw new MissingFormatWidthException("-" + this.conversionType);
            } else if (!allowArgument && this.argIndex != UNSET) {
                throw new IllegalFormatFlagsException("%" + this.conversionType + " doesn't take an argument");
            } else if (!allowPrecision && this.precision != UNSET) {
                throw new IllegalFormatPrecisionException(this.precision);
            } else if (!allowWidth && this.width != UNSET) {
                throw new IllegalFormatWidthException(this.width);
            } else if (this.flagPlus && this.flagSpace) {
                throw new IllegalFormatFlagsException("the '+' and ' ' flags are incompatible");
            } else if (this.flagMinus && this.flagZero) {
                throw new IllegalFormatFlagsException("the '-' and '0' flags are incompatible");
            }
        }

        public UnknownFormatConversionException unknownFormatConversionException() {
            if (this.conversionType == 't' || this.conversionType == 'T') {
                throw new UnknownFormatConversionException(String.format("%c%c", Character.valueOf(this.conversionType), Character.valueOf(this.dateSuffix)));
            }
            throw new UnknownFormatConversionException(String.valueOf(this.conversionType));
        }
    }

    static {
        ZEROS = new char[]{'0', '0', '0', '0', '0', '0', '0', '0', '0'};
        cachedDecimalFormat = new ThreadLocal<CachedDecimalFormat>() {
            protected CachedDecimalFormat initialValue() {
                return new CachedDecimalFormat();
            }
        };
    }

    private NativeDecimalFormat getDecimalFormat(String pattern) {
        return ((CachedDecimalFormat) cachedDecimalFormat.get()).update(this.localeData, pattern);
    }

    public Formatter() {
        this(new StringBuilder(), Locale.getDefault());
    }

    public Formatter(Appendable a) {
        this(a, Locale.getDefault());
    }

    public Formatter(Locale l) {
        this(new StringBuilder(), l);
    }

    public Formatter(Appendable a, Locale l) {
        this.closed = false;
        if (a == null) {
            this.out = new StringBuilder();
        } else {
            this.out = a;
        }
        this.locale = l;
    }

    public Formatter(String fileName) throws FileNotFoundException {
        this(new File(fileName));
    }

    public Formatter(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        this(new File(fileName), csn);
    }

    public Formatter(String fileName, String csn, Locale l) throws FileNotFoundException, UnsupportedEncodingException {
        this(new File(fileName), csn, l);
    }

    public Formatter(File file) throws FileNotFoundException {
        this(new FileOutputStream(file));
    }

    public Formatter(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        this(file, csn, Locale.getDefault());
    }

    public Formatter(File file, String csn, Locale l) throws FileNotFoundException, UnsupportedEncodingException {
        RuntimeException e;
        Object fout;
        UnsupportedEncodingException e2;
        this.closed = false;
        AutoCloseable fout2 = null;
        try {
            OutputStream fout3 = new FileOutputStream(file);
            try {
                this.out = new BufferedWriter(new OutputStreamWriter(fout3, csn));
                this.locale = l;
            } catch (RuntimeException e3) {
                e = e3;
                fout = fout3;
                IoUtils.closeQuietly(fout2);
                throw e;
            } catch (UnsupportedEncodingException e4) {
                e2 = e4;
                fout = fout3;
                IoUtils.closeQuietly(fout2);
                throw e2;
            }
        } catch (RuntimeException e5) {
            e = e5;
            IoUtils.closeQuietly(fout2);
            throw e;
        } catch (UnsupportedEncodingException e6) {
            e2 = e6;
            IoUtils.closeQuietly(fout2);
            throw e2;
        }
    }

    public Formatter(OutputStream os) {
        this.closed = false;
        this.out = new BufferedWriter(new OutputStreamWriter(os, Charset.defaultCharset()));
        this.locale = Locale.getDefault();
    }

    public Formatter(OutputStream os, String csn) throws UnsupportedEncodingException {
        this(os, csn, Locale.getDefault());
    }

    public Formatter(OutputStream os, String csn, Locale l) throws UnsupportedEncodingException {
        this.closed = false;
        this.out = new BufferedWriter(new OutputStreamWriter(os, csn));
        this.locale = l;
    }

    public Formatter(PrintStream ps) {
        this.closed = false;
        if (ps == null) {
            throw new NullPointerException("ps == null");
        }
        this.out = ps;
        this.locale = Locale.getDefault();
    }

    private void checkNotClosed() {
        if (this.closed) {
            throw new FormatterClosedException();
        }
    }

    public Locale locale() {
        checkNotClosed();
        return this.locale;
    }

    public Appendable out() {
        checkNotClosed();
        return this.out;
    }

    public String toString() {
        checkNotClosed();
        return this.out.toString();
    }

    public void flush() {
        checkNotClosed();
        if (this.out instanceof Flushable) {
            try {
                ((Flushable) this.out).flush();
            } catch (IOException e) {
                this.lastIOException = e;
            }
        }
    }

    public void close() {
        if (!this.closed) {
            this.closed = true;
            try {
                if (this.out instanceof Closeable) {
                    ((Closeable) this.out).close();
                }
            } catch (IOException e) {
                this.lastIOException = e;
            }
        }
    }

    public IOException ioException() {
        return this.lastIOException;
    }

    public Formatter format(String format, Object... args) {
        return format(this.locale, format, args);
    }

    public Formatter format(Locale l, String format, Object... args) {
        Locale originalLocale = this.locale;
        if (l == null) {
            try {
                l = Locale.US;
            } catch (Throwable th) {
                this.locale = originalLocale;
            }
        }
        this.locale = l;
        this.localeData = LocaleData.get(this.locale);
        doFormat(format, args);
        this.locale = originalLocale;
        return this;
    }

    private void doFormat(String format, Object... args) {
        checkNotClosed();
        FormatSpecifierParser fsp = new FormatSpecifierParser(format);
        boolean hasLastArgumentSet = false;
        int length = format.length();
        int i = 0;
        Object obj = null;
        int currentObjectIndex = 0;
        while (i < length) {
            int plainTextEnd;
            int i2;
            Object lastArgument;
            int plainTextStart = i;
            int nextPercent = format.indexOf(37, i);
            if (nextPercent == -1) {
                plainTextEnd = length;
            } else {
                plainTextEnd = nextPercent;
            }
            if (plainTextEnd > plainTextStart) {
                outputCharSequence(format, plainTextStart, plainTextEnd);
            }
            i = plainTextEnd;
            if (i < length) {
                FormatToken token = fsp.parseFormatToken(i + 1);
                Object argument = null;
                if (token.requireArgument()) {
                    int index;
                    if (token.getArgIndex() == -1) {
                        i2 = currentObjectIndex + 1;
                        index = currentObjectIndex;
                    } else {
                        index = token.getArgIndex();
                        i2 = currentObjectIndex;
                    }
                    argument = getArgument(args, index, fsp, obj, hasLastArgumentSet);
                    lastArgument = argument;
                    hasLastArgumentSet = true;
                } else {
                    lastArgument = obj;
                    i2 = currentObjectIndex;
                }
                CharSequence substitution = transform(token, argument);
                if (substitution != null) {
                    outputCharSequence(substitution, 0, substitution.length());
                }
                i = fsp.i;
            } else {
                lastArgument = obj;
                i2 = currentObjectIndex;
            }
            obj = lastArgument;
            currentObjectIndex = i2;
        }
    }

    private void outputCharSequence(CharSequence cs, int start, int end) {
        try {
            this.out.append(cs, start, end);
        } catch (IOException e) {
            this.lastIOException = e;
        }
    }

    private Object getArgument(Object[] args, int index, FormatSpecifierParser fsp, Object lastArgument, boolean hasLastArgumentSet) {
        if (index == -2 && !hasLastArgumentSet) {
            throw new MissingFormatArgumentException("<");
        } else if (args == null) {
            return null;
        } else {
            if (index < args.length) {
                return index != -2 ? args[index] : lastArgument;
            } else {
                throw new MissingFormatArgumentException(fsp.getFormatSpecifierText());
            }
        }
    }

    private CharSequence transform(FormatToken token, Object argument) {
        CharSequence result;
        this.formatToken = token;
        this.arg = argument;
        if (token.isDefault()) {
            switch (token.getConversionType()) {
                case Opcodes.OP_SGET_BYTE /*100*/:
                    boolean needLocalizedDigits = this.localeData.zeroDigit != '0';
                    if ((this.out instanceof StringBuilder) && !needLocalizedDigits) {
                        if ((this.arg instanceof Integer) || (this.arg instanceof Short) || (this.arg instanceof Byte)) {
                            IntegralToString.appendInt((StringBuilder) this.out, ((Number) this.arg).intValue());
                            return null;
                        } else if (this.arg instanceof Long) {
                            IntegralToString.appendLong((StringBuilder) this.out, ((Long) this.arg).longValue());
                            return null;
                        }
                    }
                    if ((this.arg instanceof Integer) || (this.arg instanceof Long) || (this.arg instanceof Short) || (this.arg instanceof Byte)) {
                        result = this.arg.toString();
                        if (needLocalizedDigits) {
                            return localizeDigits(result);
                        }
                        return result;
                    }
                    break;
                case 's':
                    if (this.arg == null) {
                        return "null";
                    }
                    if (!(this.arg instanceof Formattable)) {
                        return this.arg.toString();
                    }
                    break;
            }
        }
        this.formatToken.checkFlags(this.arg);
        switch (token.getConversionType()) {
            case Opcodes.OP_FILLED_NEW_ARRAY_RANGE /*37*/:
                result = transformFromPercent();
                break;
            case 'A':
            case Opcodes.OP_AGET_WIDE /*69*/:
            case Opcodes.OP_AGET_BOOLEAN /*71*/:
            case Opcodes.OP_SGET_WIDE /*97*/:
            case Opcodes.OP_SGET_CHAR /*101*/:
            case Opcodes.OP_SGET_SHORT /*102*/:
            case Opcodes.OP_SPUT /*103*/:
                result = transformFromFloat();
                break;
            case 'B':
            case Opcodes.OP_SGET_OBJECT /*98*/:
                result = transformFromBoolean();
                break;
            case 'C':
            case Opcodes.OP_SGET_BOOLEAN /*99*/:
                result = transformFromCharacter();
                break;
            case Opcodes.OP_AGET_BYTE /*72*/:
            case Opcodes.OP_SPUT_WIDE /*104*/:
                result = transformFromHashCode();
                break;
            case Opcodes.OP_IGET_WIDE /*83*/:
            case 's':
                result = transformFromString();
                break;
            case Opcodes.OP_IGET_OBJECT /*84*/:
            case Opcodes.OP_INVOKE_VIRTUAL_RANGE /*116*/:
                result = transformFromDateTime();
                break;
            case Opcodes.OP_IGET_SHORT /*88*/:
            case Opcodes.OP_SGET_BYTE /*100*/:
            case Opcodes.OP_INVOKE_SUPER /*111*/:
            case Opcodes.OP_INVOKE_INTERFACE_RANGE /*120*/:
                if (this.arg != null && !(this.arg instanceof BigInteger)) {
                    result = transformFromInteger();
                    break;
                }
                result = transformFromBigInteger();
                break;
                break;
            case Opcodes.OP_INVOKE_VIRTUAL /*110*/:
                result = System.lineSeparator();
                break;
            default:
                throw token.unknownFormatConversionException();
        }
        if (!Character.isUpperCase(token.getConversionType()) || result == null) {
            return result;
        }
        return result.toString().toUpperCase(this.locale);
    }

    private IllegalFormatConversionException badArgumentType() {
        throw new IllegalFormatConversionException(this.formatToken.getConversionType(), this.arg.getClass());
    }

    private CharSequence localizeDigits(CharSequence s) {
        int length = s.length();
        int offsetToLocalizedDigits = this.localeData.zeroDigit - 48;
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char ch = s.charAt(i);
            if (ch >= '0' && ch <= '9') {
                ch = (char) (ch + offsetToLocalizedDigits);
            }
            result.append(ch);
        }
        return result;
    }

    private CharSequence insertGrouping(CharSequence s) {
        StringBuilder result = new StringBuilder(s.length() + (s.length() / 3));
        int digitsLength = s.length();
        int i = 0;
        if (s.charAt(0) == '-') {
            digitsLength--;
            i = 0 + 1;
            result.append('-');
        }
        int headLength = digitsLength % 3;
        if (headLength == 0) {
            headLength = 3;
        }
        result.append(s, i, i + headLength);
        for (i += headLength; i < s.length(); i += 3) {
            result.append(this.localeData.groupingSeparator);
            result.append(s, i, i + 3);
        }
        return result;
    }

    private CharSequence transformFromBoolean() {
        CharSequence result;
        if (this.arg instanceof Boolean) {
            result = this.arg.toString();
        } else if (this.arg == null) {
            result = Unpacker.FALSE;
        } else {
            result = Unpacker.TRUE;
        }
        return padding(result, 0);
    }

    private CharSequence transformFromHashCode() {
        CharSequence result;
        if (this.arg == null) {
            result = "null";
        } else {
            result = Integer.toHexString(this.arg.hashCode());
        }
        return padding(result, 0);
    }

    private CharSequence transformFromString() {
        if (this.arg instanceof Formattable) {
            int flags = 0;
            if (this.formatToken.flagMinus) {
                flags = 0 | 1;
            }
            if (this.formatToken.flagSharp) {
                flags |= 4;
            }
            if (Character.isUpperCase(this.formatToken.getConversionType())) {
                flags |= 2;
            }
            ((Formattable) this.arg).formatTo(this, flags, this.formatToken.getWidth(), this.formatToken.getPrecision());
            return null;
        }
        return padding(this.arg != null ? this.arg.toString() : "null", 0);
    }

    private CharSequence transformFromCharacter() {
        if (this.arg == null) {
            return padding("null", 0);
        }
        if (this.arg instanceof Character) {
            return padding(String.valueOf(this.arg), 0);
        }
        if ((this.arg instanceof Byte) || (this.arg instanceof Short) || (this.arg instanceof Integer)) {
            int codePoint = ((Number) this.arg).intValue();
            if (Character.isValidCodePoint(codePoint)) {
                return padding(codePoint < DateIntervalFormat.FORMAT_ABBREV_MONTH ? String.valueOf((char) codePoint) : String.valueOf(Character.toChars(codePoint)), 0);
            }
            throw new IllegalFormatCodePointException(codePoint);
        }
        throw badArgumentType();
    }

    private CharSequence transformFromPercent() {
        return padding("%", 0);
    }

    private CharSequence padding(CharSequence source, int startIndex) {
        int start = startIndex;
        int width = this.formatToken.getWidth();
        int precision = this.formatToken.getPrecision();
        int length = source.length();
        if (precision >= 0) {
            length = Math.min(length, precision);
            if (source instanceof StringBuilder) {
                ((StringBuilder) source).setLength(length);
            } else {
                source = source.subSequence(0, length);
            }
        }
        if (width > 0) {
            width = Math.max(source.length(), width);
        }
        if (length >= width) {
            return source;
        }
        char paddingChar = ' ';
        if (!this.formatToken.flagZero) {
            start = 0;
        } else if (this.formatToken.getConversionType() == 'd') {
            paddingChar = this.localeData.zeroDigit;
        } else {
            paddingChar = '0';
        }
        char[] paddingChars = new char[(width - length)];
        Arrays.fill(paddingChars, paddingChar);
        boolean paddingRight = this.formatToken.flagMinus;
        StringBuilder result = toStringBuilder(source);
        if (paddingRight) {
            result.append(paddingChars);
        } else {
            result.insert(start, paddingChars);
        }
        return result;
    }

    private StringBuilder toStringBuilder(CharSequence cs) {
        return cs instanceof StringBuilder ? (StringBuilder) cs : new StringBuilder(cs);
    }

    private StringBuilder wrapParentheses(StringBuilder result) {
        result.setCharAt(0, '(');
        if (this.formatToken.flagZero) {
            this.formatToken.setWidth(this.formatToken.getWidth() - 1);
            result = (StringBuilder) padding(result, 1);
            result.append(')');
            return result;
        }
        result.append(')');
        return (StringBuilder) padding(result, 0);
    }

    private CharSequence transformFromInteger() {
        long value;
        int startIndex = 0;
        StringBuilder result = new StringBuilder();
        char currentConversionType = this.formatToken.getConversionType();
        if (this.arg instanceof Long) {
            value = ((Long) this.arg).longValue();
        } else if (this.arg instanceof Integer) {
            value = ((Integer) this.arg).longValue();
        } else if (this.arg instanceof Short) {
            value = ((Short) this.arg).longValue();
        } else if (this.arg instanceof Byte) {
            value = ((Byte) this.arg).longValue();
        } else {
            throw badArgumentType();
        }
        if (this.formatToken.flagSharp) {
            if (currentConversionType == 'o') {
                result.append("0");
                startIndex = 0 + 1;
            } else {
                result.append("0x");
                startIndex = 0 + 2;
            }
        }
        if (currentConversionType == 'd') {
            CharSequence digits = Long.toString(value);
            if (this.formatToken.flagComma) {
                digits = insertGrouping(digits);
            }
            if (this.localeData.zeroDigit != '0') {
                digits = localizeDigits(digits);
            }
            result.append(digits);
            if (value < 0) {
                if (this.formatToken.flagParenthesis) {
                    return wrapParentheses(result);
                }
                if (this.formatToken.flagZero) {
                    startIndex++;
                }
            } else if (this.formatToken.flagPlus) {
                result.insert(0, '+');
                startIndex++;
            } else if (this.formatToken.flagSpace) {
                result.insert(0, ' ');
                startIndex++;
            }
        } else {
            if (this.arg instanceof Byte) {
                value &= 255;
            } else if (this.arg instanceof Short) {
                value &= 65535;
            } else if (this.arg instanceof Integer) {
                value &= 4294967295L;
            }
            if (currentConversionType == 'o') {
                result.append(Long.toOctalString(value));
            } else {
                result.append(Long.toHexString(value));
            }
        }
        return padding(result, startIndex);
    }

    private CharSequence transformFromNull() {
        this.formatToken.flagZero = false;
        return padding("null", 0);
    }

    private CharSequence transformFromBigInteger() {
        int startIndex = 0;
        StringBuilder result = new StringBuilder();
        BigInteger bigInt = this.arg;
        char currentConversionType = this.formatToken.getConversionType();
        if (bigInt == null) {
            return transformFromNull();
        }
        boolean isNegative;
        if (bigInt.compareTo(BigInteger.ZERO) < 0) {
            isNegative = true;
        } else {
            isNegative = false;
        }
        if (currentConversionType == 'd') {
            CharSequence digits = bigInt.toString(10);
            if (this.formatToken.flagComma) {
                digits = insertGrouping(digits);
            }
            result.append(digits);
        } else if (currentConversionType == 'o') {
            result.append(bigInt.toString(8));
        } else {
            result.append(bigInt.toString(16));
        }
        if (this.formatToken.flagSharp) {
            if (isNegative) {
                startIndex = 1;
            } else {
                startIndex = 0;
            }
            if (currentConversionType == 'o') {
                result.insert(startIndex, "0");
                startIndex++;
            } else if (currentConversionType == Locale.PRIVATE_USE_EXTENSION || currentConversionType == 'X') {
                result.insert(startIndex, "0x");
                startIndex += 2;
            }
        }
        if (!isNegative) {
            if (this.formatToken.flagPlus) {
                result.insert(0, '+');
                startIndex++;
            }
            if (this.formatToken.flagSpace) {
                result.insert(0, ' ');
                startIndex++;
            }
        }
        if (isNegative && this.formatToken.flagParenthesis) {
            return wrapParentheses(result);
        }
        if (isNegative && this.formatToken.flagZero) {
            startIndex++;
        }
        return padding(result, startIndex);
    }

    private CharSequence transformFromDateTime() {
        if (this.arg == null) {
            return transformFromNull();
        }
        Calendar calendar;
        if (this.arg instanceof Calendar) {
            calendar = this.arg;
        } else {
            Date date;
            if (this.arg instanceof Long) {
                date = new Date(((Long) this.arg).longValue());
            } else if (this.arg instanceof Date) {
                date = this.arg;
            } else {
                throw badArgumentType();
            }
            calendar = Calendar.getInstance(this.locale);
            calendar.setTime(date);
        }
        StringBuilder result = new StringBuilder();
        if (appendT(result, this.formatToken.getDateSuffix(), calendar)) {
            return padding(result, 0);
        }
        throw this.formatToken.unknownFormatConversionException();
    }

    private boolean appendT(StringBuilder result, char conversion, Calendar calendar) {
        switch (conversion) {
            case 'A':
                result.append(this.localeData.longWeekdayNames[calendar.get(7)]);
                return true;
            case 'B':
                result.append(this.localeData.longMonthNames[calendar.get(2)]);
                return true;
            case 'C':
                appendLocalized(result, (long) (calendar.get(1) / 100), 2);
                return true;
            case Opcodes.OP_AGET /*68*/:
                appendT(result, 'm', calendar);
                result.append('/');
                appendT(result, 'd', calendar);
                result.append('/');
                appendT(result, 'y', calendar);
                return true;
            case Types.DATALINK /*70*/:
                appendT(result, 'Y', calendar);
                result.append('-');
                appendT(result, 'm', calendar);
                result.append('-');
                appendT(result, 'd', calendar);
                return true;
            case Opcodes.OP_AGET_BYTE /*72*/:
                appendLocalized(result, (long) calendar.get(11), 2);
                return true;
            case Opcodes.OP_AGET_CHAR /*73*/:
                appendLocalized(result, (long) to12Hour(calendar.get(10)), 2);
                return true;
            case Opcodes.OP_APUT_WIDE /*76*/:
                appendLocalized(result, (long) calendar.get(14), 3);
                return true;
            case Opcodes.OP_APUT_OBJECT /*77*/:
                appendLocalized(result, (long) calendar.get(12), 2);
                return true;
            case Opcodes.OP_APUT_BOOLEAN /*78*/:
                appendLocalized(result, ((long) calendar.get(14)) * 1000000, 9);
                return true;
            case SHA1Constants.BYTES_OFFSET /*81*/:
                appendLocalized(result, calendar.getTimeInMillis(), 0);
                return true;
            case SHA1Constants.HASH_OFFSET /*82*/:
                appendT(result, 'H', calendar);
                result.append(':');
                appendT(result, 'M', calendar);
                return true;
            case Opcodes.OP_IGET_WIDE /*83*/:
                appendLocalized(result, (long) calendar.get(13), 2);
                return true;
            case Opcodes.OP_IGET_OBJECT /*84*/:
                appendT(result, 'H', calendar);
                result.append(':');
                appendT(result, 'M', calendar);
                result.append(':');
                appendT(result, 'S', calendar);
                return true;
            case Opcodes.OP_IPUT /*89*/:
                appendLocalized(result, (long) calendar.get(1), 4);
                return true;
            case Opcodes.OP_IPUT_WIDE /*90*/:
                TimeZone timeZone = calendar.getTimeZone();
                result.append(timeZone.getDisplayName(timeZone.inDaylightTime(calendar.getTime()), 0, this.locale));
                return true;
            case Opcodes.OP_SGET_WIDE /*97*/:
                result.append(this.localeData.shortWeekdayNames[calendar.get(7)]);
                return true;
            case Opcodes.OP_SGET_OBJECT /*98*/:
            case Opcodes.OP_SPUT_WIDE /*104*/:
                result.append(this.localeData.shortMonthNames[calendar.get(2)]);
                return true;
            case Opcodes.OP_SGET_BOOLEAN /*99*/:
                appendT(result, 'a', calendar);
                result.append(' ');
                appendT(result, 'b', calendar);
                result.append(' ');
                appendT(result, 'd', calendar);
                result.append(' ');
                appendT(result, 'T', calendar);
                result.append(' ');
                appendT(result, 'Z', calendar);
                result.append(' ');
                appendT(result, 'Y', calendar);
                return true;
            case Opcodes.OP_SGET_BYTE /*100*/:
                appendLocalized(result, (long) calendar.get(5), 2);
                return true;
            case Opcodes.OP_SGET_CHAR /*101*/:
                appendLocalized(result, (long) calendar.get(5), 0);
                return true;
            case Opcodes.OP_SPUT_BOOLEAN /*106*/:
                appendLocalized(result, (long) calendar.get(6), 3);
                return true;
            case Opcodes.OP_SPUT_BYTE /*107*/:
                appendLocalized(result, (long) calendar.get(11), 0);
                return true;
            case Opcodes.OP_SPUT_CHAR /*108*/:
                appendLocalized(result, (long) to12Hour(calendar.get(10)), 0);
                return true;
            case Opcodes.OP_SPUT_SHORT /*109*/:
                appendLocalized(result, (long) (calendar.get(2) + 1), 2);
                return true;
            case Opcodes.OP_INVOKE_DIRECT /*112*/:
                result.append(this.localeData.amPm[calendar.get(9)].toLowerCase(this.locale));
                return true;
            case Opcodes.OP_INVOKE_INTERFACE /*114*/:
                appendT(result, 'I', calendar);
                result.append(':');
                appendT(result, 'M', calendar);
                result.append(':');
                appendT(result, 'S', calendar);
                result.append(' ');
                result.append(this.localeData.amPm[calendar.get(9)]);
                return true;
            case 's':
                appendLocalized(result, calendar.getTimeInMillis() / 1000, 0);
                return true;
            case 'y':
                appendLocalized(result, (long) (calendar.get(1) % 100), 2);
                return true;
            case 'z':
                long offset = (long) (calendar.get(15) + calendar.get(16));
                char sign = '+';
                if (offset < 0) {
                    sign = '-';
                    offset = -offset;
                }
                result.append(sign);
                appendLocalized(result, offset / 3600000, 2);
                appendLocalized(result, (offset % 3600000) / 60000, 2);
                return true;
            default:
                return false;
        }
    }

    private int to12Hour(int hour) {
        return hour == 0 ? 12 : hour;
    }

    private void appendLocalized(StringBuilder result, long value, int width) {
        int paddingIndex = result.length();
        char zeroDigit = this.localeData.zeroDigit;
        if (zeroDigit == '0') {
            result.append(value);
        } else {
            result.append(localizeDigits(Long.toString(value)));
        }
        int zeroCount = width - (result.length() - paddingIndex);
        if (zeroCount > 0) {
            if (zeroDigit == '0') {
                result.insert(paddingIndex, ZEROS, 0, zeroCount);
                return;
            }
            for (int i = 0; i < zeroCount; i++) {
                result.insert(paddingIndex, zeroDigit);
            }
        }
    }

    private CharSequence transformFromSpecialNumber(double d) {
        String source;
        if (Double.isNaN(d)) {
            source = "NaN";
        } else if (d == Double.POSITIVE_INFINITY) {
            if (this.formatToken.flagPlus) {
                source = "+Infinity";
            } else if (this.formatToken.flagSpace) {
                source = " Infinity";
            } else {
                source = "Infinity";
            }
        } else if (d != Double.NEGATIVE_INFINITY) {
            return null;
        } else {
            if (this.formatToken.flagParenthesis) {
                source = "(Infinity)";
            } else {
                source = "-Infinity";
            }
        }
        this.formatToken.setPrecision(-1);
        this.formatToken.flagZero = false;
        return padding(source, 0);
    }

    private CharSequence transformFromFloat() {
        if (this.arg == null) {
            return transformFromNull();
        }
        if ((this.arg instanceof Float) || (this.arg instanceof Double)) {
            double d = this.arg.doubleValue();
            if (d != d || d == Double.POSITIVE_INFINITY || d == Double.NEGATIVE_INFINITY) {
                return transformFromSpecialNumber(d);
            }
        } else if (!(this.arg instanceof BigDecimal)) {
            throw badArgumentType();
        }
        char conversionType = this.formatToken.getConversionType();
        if (!(conversionType == 'a' || conversionType == 'A' || this.formatToken.isPrecisionSet())) {
            this.formatToken.setPrecision(6);
        }
        StringBuilder result = new StringBuilder();
        switch (conversionType) {
            case 'A':
            case Opcodes.OP_SGET_WIDE /*97*/:
                transformA(result);
                break;
            case Opcodes.OP_AGET_WIDE /*69*/:
            case Opcodes.OP_SGET_CHAR /*101*/:
                transformE(result);
                break;
            case Opcodes.OP_AGET_BOOLEAN /*71*/:
            case Opcodes.OP_SPUT /*103*/:
                transformG(result);
                break;
            case Opcodes.OP_SGET_SHORT /*102*/:
                transformF(result);
                break;
            default:
                throw this.formatToken.unknownFormatConversionException();
        }
        this.formatToken.setPrecision(-1);
        int startIndex = 0;
        if (!startsWithMinusSign(result, this.localeData.minusSign)) {
            if (this.formatToken.flagSpace) {
                result.insert(0, ' ');
                startIndex = 0 + 1;
            }
            if (this.formatToken.flagPlus) {
                result.insert(0, '+');
                startIndex++;
            }
        } else if (this.formatToken.flagParenthesis) {
            return wrapParentheses(result);
        }
        char firstChar = result.charAt(0);
        if (this.formatToken.flagZero && (firstChar == '+' || startsWithMinusSign(result, this.localeData.minusSign))) {
            startIndex = this.localeData.minusSign.length();
        }
        if (conversionType == 'a' || conversionType == 'A') {
            startIndex += 2;
        }
        return padding(result, startIndex);
    }

    private static boolean startsWithMinusSign(CharSequence cs, String minusSign) {
        if (cs.length() < minusSign.length()) {
            return false;
        }
        for (int i = 0; i < minusSign.length(); i++) {
            if (minusSign.charAt(i) != cs.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private void transformE(StringBuilder result) {
        char[] chars;
        int precision = this.formatToken.getPrecision();
        String pattern = "0E+00";
        if (precision > 0) {
            StringBuilder sb = new StringBuilder("0.");
            char[] zeros = new char[precision];
            Arrays.fill(zeros, '0');
            sb.append(zeros);
            sb.append("E+00");
            pattern = sb.toString();
        }
        NativeDecimalFormat nf = getDecimalFormat(pattern);
        if (this.arg instanceof BigDecimal) {
            chars = nf.formatBigDecimal((BigDecimal) this.arg, null);
        } else {
            chars = nf.formatDouble(((Number) this.arg).doubleValue(), null);
        }
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == 'E') {
                chars[i] = 'e';
            }
        }
        result.append(chars);
        if (this.formatToken.flagSharp && precision == 0) {
            result.insert(result.indexOf("e"), this.localeData.decimalSeparator);
        }
    }

    private void transformG(StringBuilder result) {
        int precision = this.formatToken.getPrecision();
        if (precision == 0) {
            precision = 1;
        }
        this.formatToken.setPrecision(precision);
        double d = ((Number) this.arg).doubleValue();
        if (d == 0.0d) {
            this.formatToken.setPrecision(precision - 1);
            transformF(result);
            return;
        }
        boolean requireScientificRepresentation = true;
        d = Math.abs(d);
        if (Double.isInfinite(d)) {
            this.formatToken.setPrecision(this.formatToken.getPrecision() - 1);
            transformE(result);
            return;
        }
        BigDecimal b = new BigDecimal(d, new MathContext(precision));
        d = b.doubleValue();
        long l = b.longValue();
        if (d < 1.0d || d >= Math.pow(10.0d, (double) precision)) {
            l = b.movePointRight(4).longValue();
            if (d >= Math.pow(10.0d, -4.0d) && d < 1.0d) {
                requireScientificRepresentation = false;
                precision += 4 - String.valueOf(l).length();
                if (String.valueOf(b.movePointRight(precision + 1).longValue()).length() <= this.formatToken.getPrecision()) {
                    precision++;
                }
                if (((double) b.movePointRight(precision).longValue()) >= Math.pow(10.0d, (double) (precision - 4))) {
                    this.formatToken.setPrecision(precision);
                }
            }
        } else if (((double) l) < Math.pow(10.0d, (double) precision)) {
            requireScientificRepresentation = false;
            precision -= String.valueOf(l).length();
            if (precision < 0) {
                precision = 0;
            }
            if (String.valueOf(Math.round(Math.pow(10.0d, (double) (precision + 1)) * d)).length() <= this.formatToken.getPrecision()) {
                precision++;
            }
            this.formatToken.setPrecision(precision);
        }
        if (requireScientificRepresentation) {
            this.formatToken.setPrecision(this.formatToken.getPrecision() - 1);
            transformE(result);
            return;
        }
        transformF(result);
    }

    private void transformF(StringBuilder result) {
        String pattern = "0.000000";
        int precision = this.formatToken.getPrecision();
        if (this.formatToken.flagComma || precision != 6) {
            StringBuilder patternBuilder = new StringBuilder();
            if (this.formatToken.flagComma) {
                patternBuilder.append(',');
                char[] sharps = new char[2];
                Arrays.fill(sharps, '#');
                patternBuilder.append(sharps);
            }
            patternBuilder.append('0');
            if (precision > 0) {
                patternBuilder.append('.');
                for (int i = 0; i < precision; i++) {
                    patternBuilder.append('0');
                }
            }
            pattern = patternBuilder.toString();
        }
        NativeDecimalFormat nf = getDecimalFormat(pattern);
        if (this.arg instanceof BigDecimal) {
            result.append(nf.formatBigDecimal((BigDecimal) this.arg, null));
        } else {
            result.append(nf.formatDouble(((Number) this.arg).doubleValue(), null));
        }
        if (this.formatToken.flagSharp && precision == 0) {
            result.append(this.localeData.decimalSeparator);
        }
    }

    private void transformA(StringBuilder result) {
        if (this.arg instanceof Float) {
            result.append(Float.toHexString(((Float) this.arg).floatValue()));
        } else if (this.arg instanceof Double) {
            result.append(Double.toHexString(((Double) this.arg).doubleValue()));
        } else {
            throw badArgumentType();
        }
        if (this.formatToken.isPrecisionSet()) {
            int precision = this.formatToken.getPrecision();
            if (precision == 0) {
                precision = 1;
            }
            int indexOfFirstFractionalDigit = result.indexOf(".") + 1;
            int indexOfP = result.indexOf("p");
            int fractionalLength = indexOfP - indexOfFirstFractionalDigit;
            if (fractionalLength == precision) {
                return;
            }
            if (fractionalLength < precision) {
                char[] zeros = new char[(precision - fractionalLength)];
                Arrays.fill(zeros, '0');
                result.insert(indexOfP, zeros);
                return;
            }
            result.delete(indexOfFirstFractionalDigit + precision, indexOfP);
        }
    }
}
