package java.util;

import dalvik.bytecode.Opcodes;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import libcore.io.IoUtils;
import org.w3c.dom.traversal.NodeFilter;
import org.xmlpull.v1.XmlPullParser;

public final class Scanner implements Closeable, Iterator<String> {
    private static final Pattern ANY_PATTERN;
    private static final Pattern BOOLEAN_PATTERN;
    private static final Pattern DEFAULT_DELIMITER;
    private static final int DEFAULT_RADIX = 10;
    private static final Pattern LINE_PATTERN;
    private static final Pattern LINE_TERMINATOR;
    private static final Pattern MULTI_LINE_TERMINATOR;
    private static final String NL = "\n|\r\n|\r|\u0085|\u2028|\u2029";
    private CharBuffer buffer;
    private int bufferLength;
    private Pattern cachedFloatPattern;
    private Pattern cachedIntegerPattern;
    private int cachedIntegerPatternRadix;
    private int cachedNextIndex;
    private Object cachedNextValue;
    private boolean closed;
    private int currentRadix;
    private DecimalFormat decimalFormat;
    private Pattern delimiter;
    private int findStartIndex;
    private Readable input;
    private boolean inputExhausted;
    private IOException lastIOException;
    private Locale locale;
    private boolean matchSuccessful;
    private Matcher matcher;
    private int preStartIndex;

    static {
        DEFAULT_DELIMITER = Pattern.compile("\\p{javaWhitespace}+");
        BOOLEAN_PATTERN = Pattern.compile("true|false", 2);
        LINE_TERMINATOR = Pattern.compile(NL);
        MULTI_LINE_TERMINATOR = Pattern.compile("(\n|\r\n|\r|\u0085|\u2028|\u2029)+");
        LINE_PATTERN = Pattern.compile(".*(\n|\r\n|\r|\u0085|\u2028|\u2029)|.+$");
        ANY_PATTERN = Pattern.compile("(?s).*");
    }

    public Scanner(File src) throws FileNotFoundException {
        this(src, Charset.defaultCharset().name());
    }

    public Scanner(File src, String charsetName) throws FileNotFoundException {
        this.buffer = CharBuffer.allocate(NodeFilter.SHOW_DOCUMENT_FRAGMENT);
        this.delimiter = DEFAULT_DELIMITER;
        this.currentRadix = DEFAULT_RADIX;
        this.locale = Locale.getDefault();
        this.findStartIndex = 0;
        this.preStartIndex = this.findStartIndex;
        this.bufferLength = 0;
        this.closed = false;
        this.matchSuccessful = false;
        this.inputExhausted = false;
        this.cachedNextValue = null;
        this.cachedNextIndex = -1;
        this.cachedFloatPattern = null;
        this.cachedIntegerPatternRadix = -1;
        this.cachedIntegerPattern = null;
        if (src == null) {
            throw new NullPointerException("src == null");
        }
        AutoCloseable fis = new FileInputStream(src);
        if (charsetName == null) {
            throw new IllegalArgumentException("charsetName == null");
        }
        try {
            initialize(new InputStreamReader((InputStream) fis, charsetName));
        } catch (UnsupportedEncodingException e) {
            IoUtils.closeQuietly(fis);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Scanner(String src) {
        this.buffer = CharBuffer.allocate(NodeFilter.SHOW_DOCUMENT_FRAGMENT);
        this.delimiter = DEFAULT_DELIMITER;
        this.currentRadix = DEFAULT_RADIX;
        this.locale = Locale.getDefault();
        this.findStartIndex = 0;
        this.preStartIndex = this.findStartIndex;
        this.bufferLength = 0;
        this.closed = false;
        this.matchSuccessful = false;
        this.inputExhausted = false;
        this.cachedNextValue = null;
        this.cachedNextIndex = -1;
        this.cachedFloatPattern = null;
        this.cachedIntegerPatternRadix = -1;
        this.cachedIntegerPattern = null;
        initialize(new StringReader(src));
    }

    public Scanner(InputStream src) {
        this(src, Charset.defaultCharset().name());
    }

    public Scanner(InputStream src, String charsetName) {
        this.buffer = CharBuffer.allocate(NodeFilter.SHOW_DOCUMENT_FRAGMENT);
        this.delimiter = DEFAULT_DELIMITER;
        this.currentRadix = DEFAULT_RADIX;
        this.locale = Locale.getDefault();
        this.findStartIndex = 0;
        this.preStartIndex = this.findStartIndex;
        this.bufferLength = 0;
        this.closed = false;
        this.matchSuccessful = false;
        this.inputExhausted = false;
        this.cachedNextValue = null;
        this.cachedNextIndex = -1;
        this.cachedFloatPattern = null;
        this.cachedIntegerPatternRadix = -1;
        this.cachedIntegerPattern = null;
        if (src == null) {
            throw new NullPointerException("src == null");
        }
        try {
            initialize(new InputStreamReader(src, charsetName));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Scanner(Readable src) {
        this.buffer = CharBuffer.allocate(NodeFilter.SHOW_DOCUMENT_FRAGMENT);
        this.delimiter = DEFAULT_DELIMITER;
        this.currentRadix = DEFAULT_RADIX;
        this.locale = Locale.getDefault();
        this.findStartIndex = 0;
        this.preStartIndex = this.findStartIndex;
        this.bufferLength = 0;
        this.closed = false;
        this.matchSuccessful = false;
        this.inputExhausted = false;
        this.cachedNextValue = null;
        this.cachedNextIndex = -1;
        this.cachedFloatPattern = null;
        this.cachedIntegerPatternRadix = -1;
        this.cachedIntegerPattern = null;
        if (src == null) {
            throw new NullPointerException("src == null");
        }
        initialize(src);
    }

    public Scanner(ReadableByteChannel src) {
        this(src, Charset.defaultCharset().name());
    }

    public Scanner(ReadableByteChannel src, String charsetName) {
        this.buffer = CharBuffer.allocate(NodeFilter.SHOW_DOCUMENT_FRAGMENT);
        this.delimiter = DEFAULT_DELIMITER;
        this.currentRadix = DEFAULT_RADIX;
        this.locale = Locale.getDefault();
        this.findStartIndex = 0;
        this.preStartIndex = this.findStartIndex;
        this.bufferLength = 0;
        this.closed = false;
        this.matchSuccessful = false;
        this.inputExhausted = false;
        this.cachedNextValue = null;
        this.cachedNextIndex = -1;
        this.cachedFloatPattern = null;
        this.cachedIntegerPatternRadix = -1;
        this.cachedIntegerPattern = null;
        if (src == null) {
            throw new NullPointerException("src == null");
        } else if (charsetName == null) {
            throw new IllegalArgumentException("charsetName == null");
        } else {
            initialize(Channels.newReader(src, charsetName));
        }
    }

    private void initialize(Readable input) {
        this.input = input;
        this.matcher = this.delimiter.matcher(XmlPullParser.NO_NAMESPACE);
        this.matcher.useTransparentBounds(true);
        this.matcher.useAnchoringBounds(false);
    }

    public void close() {
        if (!this.closed) {
            if (this.input instanceof Closeable) {
                try {
                    ((Closeable) this.input).close();
                } catch (IOException e) {
                    this.lastIOException = e;
                }
            }
            this.closed = true;
        }
    }

    public Pattern delimiter() {
        return this.delimiter;
    }

    public String findInLine(Pattern pattern) {
        checkOpen();
        checkNotNull(pattern);
        int horizonLineSeparator = 0;
        this.matcher.usePattern(MULTI_LINE_TERMINATOR);
        this.matcher.region(this.findStartIndex, this.bufferLength);
        boolean findComplete = false;
        int terminatorLength = 0;
        while (!findComplete) {
            if (this.matcher.find()) {
                horizonLineSeparator = this.matcher.start();
                terminatorLength = this.matcher.end() - this.matcher.start();
                findComplete = true;
            } else if (this.inputExhausted) {
                horizonLineSeparator = this.bufferLength;
                findComplete = true;
            } else {
                readMore();
                resetMatcher();
            }
        }
        this.matcher.usePattern(pattern);
        int oldLimit = this.buffer.limit();
        this.buffer.limit(horizonLineSeparator + terminatorLength);
        this.matcher.region(this.findStartIndex, horizonLineSeparator + terminatorLength);
        if (this.matcher.find()) {
            this.findStartIndex = this.matcher.end();
            if (horizonLineSeparator == this.matcher.end()) {
                this.findStartIndex += terminatorLength;
            }
            if (horizonLineSeparator == this.bufferLength || horizonLineSeparator + terminatorLength != this.matcher.end()) {
                this.matchSuccessful = true;
                this.buffer.limit(oldLimit);
                return this.matcher.group();
            }
            this.buffer.limit(oldLimit);
            this.matchSuccessful = false;
            return null;
        }
        this.buffer.limit(oldLimit);
        this.matchSuccessful = false;
        return null;
    }

    public String findInLine(String pattern) {
        return findInLine(Pattern.compile(pattern));
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String findWithinHorizon(java.util.regex.Pattern r9, int r10) {
        /*
        r8 = this;
        r4 = 1;
        r5 = 0;
        r8.checkOpen();
        r8.checkNotNull(r9);
        if (r10 >= 0) goto L_0x0012;
    L_0x000a:
        r4 = new java.lang.IllegalArgumentException;
        r5 = "horizon < 0";
        r4.<init>(r5);
        throw r4;
    L_0x0012:
        r6 = r8.matcher;
        r6.usePattern(r9);
        r3 = 0;
        if (r10 != 0) goto L_0x005a;
    L_0x001a:
        r1 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
    L_0x001d:
        r6 = r8.bufferLength;
        r0 = java.lang.Math.min(r1, r6);
        r6 = r8.bufferLength;
        if (r1 > r6) goto L_0x005f;
    L_0x0027:
        r2 = r4;
    L_0x0028:
        r6 = r8.matcher;
        r7 = r8.findStartIndex;
        r6.region(r7, r0);
        r6 = r8.matcher;
        r6 = r6.find();
        if (r6 == 0) goto L_0x0061;
    L_0x0037:
        if (r10 != 0) goto L_0x0041;
    L_0x0039:
        r6 = r8.matcher;
        r6 = r6.hitEnd();
        if (r6 == 0) goto L_0x0047;
    L_0x0041:
        if (r2 != 0) goto L_0x0047;
    L_0x0043:
        r6 = r8.inputExhausted;
        if (r6 == 0) goto L_0x0067;
    L_0x0047:
        r6 = r8.matcher;
        r3 = r6.group();
    L_0x004d:
        if (r3 == 0) goto L_0x0072;
    L_0x004f:
        r5 = r8.matcher;
        r5 = r5.end();
        r8.findStartIndex = r5;
        r8.matchSuccessful = r4;
    L_0x0059:
        return r3;
    L_0x005a:
        r6 = r8.findStartIndex;
        r1 = r6 + r10;
        goto L_0x001d;
    L_0x005f:
        r2 = r5;
        goto L_0x0028;
    L_0x0061:
        if (r2 != 0) goto L_0x004d;
    L_0x0063:
        r6 = r8.inputExhausted;
        if (r6 != 0) goto L_0x004d;
    L_0x0067:
        r6 = r8.inputExhausted;
        if (r6 != 0) goto L_0x001d;
    L_0x006b:
        r8.readMore();
        r8.resetMatcher();
        goto L_0x001d;
    L_0x0072:
        r8.matchSuccessful = r5;
        goto L_0x0059;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.Scanner.findWithinHorizon(java.util.regex.Pattern, int):java.lang.String");
    }

    public String findWithinHorizon(String pattern, int horizon) {
        return findWithinHorizon(Pattern.compile(pattern), horizon);
    }

    public boolean hasNext() {
        return hasNext(ANY_PATTERN);
    }

    public boolean hasNext(Pattern pattern) {
        boolean z = false;
        checkOpen();
        checkNotNull(pattern);
        this.matchSuccessful = false;
        prepareForScan();
        if (setTokenRegion()) {
            this.matcher.usePattern(pattern);
            z = false;
            if (this.matcher.matches()) {
                this.cachedNextIndex = this.findStartIndex;
                this.matchSuccessful = true;
                z = true;
            }
            recoverPreviousStatus();
        } else {
            recoverPreviousStatus();
        }
        return z;
    }

    public boolean hasNext(String pattern) {
        return hasNext(Pattern.compile(pattern));
    }

    public boolean hasNextBigDecimal() {
        if (!hasNext(getFloatPattern())) {
            return false;
        }
        try {
            this.cachedNextValue = new BigDecimal(removeLocaleInfoFromFloat(this.matcher.group()));
            return true;
        } catch (NumberFormatException e) {
            this.matchSuccessful = false;
            return false;
        }
    }

    public boolean hasNextBigInteger() {
        return hasNextBigInteger(this.currentRadix);
    }

    public boolean hasNextBigInteger(int radix) {
        if (!hasNext(getIntegerPattern(radix))) {
            return false;
        }
        try {
            this.cachedNextValue = new BigInteger(removeLocaleInfo(this.matcher.group(), Integer.TYPE), radix);
            return true;
        } catch (NumberFormatException e) {
            this.matchSuccessful = false;
            return false;
        }
    }

    public boolean hasNextBoolean() {
        return hasNext(BOOLEAN_PATTERN);
    }

    public boolean hasNextByte() {
        return hasNextByte(this.currentRadix);
    }

    public boolean hasNextByte(int radix) {
        if (!hasNext(getIntegerPattern(radix))) {
            return false;
        }
        try {
            this.cachedNextValue = Byte.valueOf(removeLocaleInfo(this.matcher.group(), Integer.TYPE), radix);
            return true;
        } catch (NumberFormatException e) {
            this.matchSuccessful = false;
            return false;
        }
    }

    public boolean hasNextDouble() {
        if (!hasNext(getFloatPattern())) {
            return false;
        }
        try {
            this.cachedNextValue = Double.valueOf(removeLocaleInfoFromFloat(this.matcher.group()));
            return true;
        } catch (NumberFormatException e) {
            this.matchSuccessful = false;
            return false;
        }
    }

    public boolean hasNextFloat() {
        if (!hasNext(getFloatPattern())) {
            return false;
        }
        try {
            this.cachedNextValue = Float.valueOf(removeLocaleInfoFromFloat(this.matcher.group()));
            return true;
        } catch (NumberFormatException e) {
            this.matchSuccessful = false;
            return false;
        }
    }

    public boolean hasNextInt() {
        return hasNextInt(this.currentRadix);
    }

    public boolean hasNextInt(int radix) {
        if (!hasNext(getIntegerPattern(radix))) {
            return false;
        }
        try {
            this.cachedNextValue = Integer.valueOf(removeLocaleInfo(this.matcher.group(), Integer.TYPE), radix);
            return true;
        } catch (NumberFormatException e) {
            this.matchSuccessful = false;
            return false;
        }
    }

    public boolean hasNextLine() {
        prepareForScan();
        String result = findWithinHorizon(LINE_PATTERN, 0);
        recoverPreviousStatus();
        if (result != null) {
            return true;
        }
        return false;
    }

    public boolean hasNextLong() {
        return hasNextLong(this.currentRadix);
    }

    public boolean hasNextLong(int radix) {
        if (!hasNext(getIntegerPattern(radix))) {
            return false;
        }
        try {
            this.cachedNextValue = Long.valueOf(removeLocaleInfo(this.matcher.group(), Integer.TYPE), radix);
            return true;
        } catch (NumberFormatException e) {
            this.matchSuccessful = false;
            return false;
        }
    }

    public boolean hasNextShort() {
        return hasNextShort(this.currentRadix);
    }

    public boolean hasNextShort(int radix) {
        if (!hasNext(getIntegerPattern(radix))) {
            return false;
        }
        try {
            this.cachedNextValue = Short.valueOf(removeLocaleInfo(this.matcher.group(), Integer.TYPE), radix);
            return true;
        } catch (NumberFormatException e) {
            this.matchSuccessful = false;
            return false;
        }
    }

    public IOException ioException() {
        return this.lastIOException;
    }

    public Locale locale() {
        return this.locale;
    }

    private void setLocale(Locale locale) {
        this.locale = locale;
        this.decimalFormat = null;
        this.cachedFloatPattern = null;
        this.cachedIntegerPatternRadix = -1;
        this.cachedIntegerPattern = null;
    }

    public MatchResult match() {
        if (this.matchSuccessful) {
            return this.matcher.toMatchResult();
        }
        throw new IllegalStateException();
    }

    public String next() {
        return next(ANY_PATTERN);
    }

    public String next(Pattern pattern) {
        checkOpen();
        checkNotNull(pattern);
        this.matchSuccessful = false;
        prepareForScan();
        if (setTokenRegion()) {
            this.matcher.usePattern(pattern);
            if (this.matcher.matches()) {
                this.matchSuccessful = true;
                return this.matcher.group();
            }
            recoverPreviousStatus();
            throw new InputMismatchException();
        }
        recoverPreviousStatus();
        throw new NoSuchElementException();
    }

    public String next(String pattern) {
        return next(Pattern.compile(pattern));
    }

    public BigDecimal nextBigDecimal() {
        checkOpen();
        Object obj = this.cachedNextValue;
        this.cachedNextValue = null;
        if (obj instanceof BigDecimal) {
            this.findStartIndex = this.cachedNextIndex;
            return (BigDecimal) obj;
        }
        try {
            return new BigDecimal(removeLocaleInfoFromFloat(next(getFloatPattern())));
        } catch (NumberFormatException e) {
            this.matchSuccessful = false;
            recoverPreviousStatus();
            throw new InputMismatchException();
        }
    }

    public BigInteger nextBigInteger() {
        return nextBigInteger(this.currentRadix);
    }

    public BigInteger nextBigInteger(int radix) {
        checkOpen();
        Object obj = this.cachedNextValue;
        this.cachedNextValue = null;
        if (obj instanceof BigInteger) {
            this.findStartIndex = this.cachedNextIndex;
            return (BigInteger) obj;
        }
        try {
            return new BigInteger(removeLocaleInfo(next(getIntegerPattern(radix)), Integer.TYPE), radix);
        } catch (NumberFormatException e) {
            this.matchSuccessful = false;
            recoverPreviousStatus();
            throw new InputMismatchException();
        }
    }

    public boolean nextBoolean() {
        return Boolean.parseBoolean(next(BOOLEAN_PATTERN));
    }

    public byte nextByte() {
        return nextByte(this.currentRadix);
    }

    public byte nextByte(int radix) {
        checkOpen();
        Object obj = this.cachedNextValue;
        this.cachedNextValue = null;
        if (obj instanceof Byte) {
            this.findStartIndex = this.cachedNextIndex;
            return ((Byte) obj).byteValue();
        }
        try {
            return Byte.parseByte(removeLocaleInfo(next(getIntegerPattern(radix)), Integer.TYPE), radix);
        } catch (NumberFormatException e) {
            this.matchSuccessful = false;
            recoverPreviousStatus();
            throw new InputMismatchException();
        }
    }

    public double nextDouble() {
        checkOpen();
        Object obj = this.cachedNextValue;
        this.cachedNextValue = null;
        if (obj instanceof Double) {
            this.findStartIndex = this.cachedNextIndex;
            return ((Double) obj).doubleValue();
        }
        try {
            return Double.parseDouble(removeLocaleInfoFromFloat(next(getFloatPattern())));
        } catch (NumberFormatException e) {
            this.matchSuccessful = false;
            recoverPreviousStatus();
            throw new InputMismatchException();
        }
    }

    public float nextFloat() {
        checkOpen();
        Object obj = this.cachedNextValue;
        this.cachedNextValue = null;
        if (obj instanceof Float) {
            this.findStartIndex = this.cachedNextIndex;
            return ((Float) obj).floatValue();
        }
        try {
            return Float.parseFloat(removeLocaleInfoFromFloat(next(getFloatPattern())));
        } catch (NumberFormatException e) {
            this.matchSuccessful = false;
            recoverPreviousStatus();
            throw new InputMismatchException();
        }
    }

    public int nextInt() {
        return nextInt(this.currentRadix);
    }

    public int nextInt(int radix) {
        checkOpen();
        Object obj = this.cachedNextValue;
        this.cachedNextValue = null;
        if (obj instanceof Integer) {
            this.findStartIndex = this.cachedNextIndex;
            return ((Integer) obj).intValue();
        }
        try {
            return Integer.parseInt(removeLocaleInfo(next(getIntegerPattern(radix)), Integer.TYPE), radix);
        } catch (NumberFormatException e) {
            this.matchSuccessful = false;
            recoverPreviousStatus();
            throw new InputMismatchException();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String nextLine() {
        /*
        r6 = this;
        r5 = 0;
        r6.checkOpen();
        r2 = r6.matcher;
        r3 = LINE_PATTERN;
        r2.usePattern(r3);
        r2 = r6.matcher;
        r3 = r6.findStartIndex;
        r4 = r6.bufferLength;
        r2.region(r3, r4);
    L_0x0014:
        r2 = r6.matcher;
        r2 = r2.find();
        if (r2 == 0) goto L_0x005c;
    L_0x001c:
        r2 = r6.inputExhausted;
        if (r2 != 0) goto L_0x0034;
    L_0x0020:
        r2 = r6.matcher;
        r2 = r2.end();
        r3 = r6.bufferLength;
        if (r2 != r3) goto L_0x0034;
    L_0x002a:
        r2 = r6.bufferLength;
        r3 = r6.buffer;
        r3 = r3.capacity();
        if (r2 >= r3) goto L_0x0068;
    L_0x0034:
        r2 = 1;
        r6.matchSuccessful = r2;
        r2 = r6.matcher;
        r2 = r2.end();
        r6.findStartIndex = r2;
        r2 = r6.matcher;
        r0 = r2.group();
        if (r0 == 0) goto L_0x005b;
    L_0x0047:
        r2 = LINE_TERMINATOR;
        r1 = r2.matcher(r0);
        r2 = r1.find();
        if (r2 == 0) goto L_0x005b;
    L_0x0053:
        r2 = r1.start();
        r0 = r0.substring(r5, r2);
    L_0x005b:
        return r0;
    L_0x005c:
        r2 = r6.inputExhausted;
        if (r2 == 0) goto L_0x0068;
    L_0x0060:
        r6.matchSuccessful = r5;
        r2 = new java.util.NoSuchElementException;
        r2.<init>();
        throw r2;
    L_0x0068:
        r2 = r6.inputExhausted;
        if (r2 != 0) goto L_0x0014;
    L_0x006c:
        r6.readMore();
        r6.resetMatcher();
        goto L_0x0014;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.util.Scanner.nextLine():java.lang.String");
    }

    public long nextLong() {
        return nextLong(this.currentRadix);
    }

    public long nextLong(int radix) {
        checkOpen();
        Object obj = this.cachedNextValue;
        this.cachedNextValue = null;
        if (obj instanceof Long) {
            this.findStartIndex = this.cachedNextIndex;
            return ((Long) obj).longValue();
        }
        try {
            return Long.parseLong(removeLocaleInfo(next(getIntegerPattern(radix)), Integer.TYPE), radix);
        } catch (NumberFormatException e) {
            this.matchSuccessful = false;
            recoverPreviousStatus();
            throw new InputMismatchException();
        }
    }

    public short nextShort() {
        return nextShort(this.currentRadix);
    }

    public short nextShort(int radix) {
        checkOpen();
        Object obj = this.cachedNextValue;
        this.cachedNextValue = null;
        if (obj instanceof Short) {
            this.findStartIndex = this.cachedNextIndex;
            return ((Short) obj).shortValue();
        }
        try {
            return Short.parseShort(removeLocaleInfo(next(getIntegerPattern(radix)), Integer.TYPE), radix);
        } catch (NumberFormatException e) {
            this.matchSuccessful = false;
            recoverPreviousStatus();
            throw new InputMismatchException();
        }
    }

    public int radix() {
        return this.currentRadix;
    }

    public Scanner skip(Pattern pattern) {
        checkOpen();
        checkNotNull(pattern);
        this.matcher.usePattern(pattern);
        this.matcher.region(this.findStartIndex, this.bufferLength);
        while (true) {
            if (!this.matcher.lookingAt()) {
                if (this.inputExhausted) {
                    break;
                }
            }
            boolean matchInBuffer;
            if (this.matcher.end() < this.bufferLength || (this.matcher.end() == this.bufferLength && this.inputExhausted)) {
                matchInBuffer = true;
            } else {
                matchInBuffer = false;
            }
            if (matchInBuffer) {
                this.matchSuccessful = true;
                this.findStartIndex = this.matcher.end();
                return this;
            }
            if (!this.inputExhausted) {
                readMore();
                resetMatcher();
            }
        }
        this.matchSuccessful = false;
        throw new NoSuchElementException();
    }

    public Scanner skip(String pattern) {
        return skip(Pattern.compile(pattern));
    }

    public String toString() {
        return getClass().getName() + "[delimiter=" + this.delimiter + ",findStartIndex=" + this.findStartIndex + ",matchSuccessful=" + this.matchSuccessful + ",closed=" + this.closed + "]";
    }

    public Scanner useDelimiter(Pattern pattern) {
        this.delimiter = pattern;
        return this;
    }

    public Scanner useDelimiter(String pattern) {
        return useDelimiter(Pattern.compile(pattern));
    }

    public Scanner useLocale(Locale l) {
        if (l == null) {
            throw new NullPointerException("l == null");
        }
        setLocale(l);
        return this;
    }

    public Scanner useRadix(int radix) {
        checkRadix(radix);
        this.currentRadix = radix;
        return this;
    }

    private void checkRadix(int radix) {
        if (radix < 2 || radix > 36) {
            throw new IllegalArgumentException("Invalid radix: " + radix);
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    private void checkOpen() {
        if (this.closed) {
            throw new IllegalStateException();
        }
    }

    private void checkNotNull(Pattern pattern) {
        if (pattern == null) {
            throw new NullPointerException("pattern == null");
        }
    }

    private void resetMatcher() {
        this.matcher.reset(this.buffer);
        this.matcher.region(this.findStartIndex, this.bufferLength);
    }

    private void prepareForScan() {
        if (this.findStartIndex >= this.buffer.capacity() / 2) {
            int oldPosition = this.buffer.position();
            this.buffer.position(this.findStartIndex);
            this.buffer.compact();
            this.buffer.position(oldPosition);
            this.bufferLength -= this.findStartIndex;
            this.findStartIndex = 0;
            this.preStartIndex = -1;
            resetMatcher();
        }
        this.preStartIndex = this.findStartIndex;
    }

    private void recoverPreviousStatus() {
        this.findStartIndex = this.preStartIndex;
    }

    private Pattern getIntegerPattern(int radix) {
        checkRadix(radix);
        if (this.decimalFormat == null) {
            this.decimalFormat = (DecimalFormat) NumberFormat.getInstance(this.locale);
        }
        if (this.cachedIntegerPatternRadix == radix) {
            return this.cachedIntegerPattern;
        }
        String digits = "0123456789abcdefghijklmnopqrstuvwxyz";
        String numeral = getNumeral("((?i)[" + digits.substring(0, radix) + "]|\\p{javaDigit})", "((?i)[" + digits.substring(1, radix) + "]|([\\p{javaDigit}&&[^0]]))");
        String regex = "(([-+]?(" + numeral + ")))|" + "(" + addPositiveSign(numeral) + ")|" + "(" + addNegativeSign(numeral) + ")";
        this.cachedIntegerPatternRadix = radix;
        this.cachedIntegerPattern = Pattern.compile(regex);
        return this.cachedIntegerPattern;
    }

    private Pattern getFloatPattern() {
        if (this.decimalFormat == null) {
            this.decimalFormat = (DecimalFormat) NumberFormat.getInstance(this.locale);
        }
        if (this.cachedFloatPattern != null) {
            return this.cachedFloatPattern;
        }
        DecimalFormatSymbols dfs = this.decimalFormat.getDecimalFormatSymbols();
        String digit = "([0-9]|(\\p{javaDigit}))";
        String numeral = getNumeral(digit, "[\\p{javaDigit}&&[^0]]");
        String decimalSeparator = "\\" + dfs.getDecimalSeparator();
        String decimalNumeral = "(" + numeral + "|" + numeral + decimalSeparator + digit + "*+|" + decimalSeparator + digit + "++)";
        String exponent = "([eE][+-]?" + digit + "+)?";
        String decimal = "(([-+]?" + decimalNumeral + "(" + exponent + "?)" + ")|" + "(" + addPositiveSign(decimalNumeral) + "(" + exponent + "?)" + ")|" + "(" + addNegativeSign(decimalNumeral) + "(" + exponent + "?)" + "))";
        String localNaN = dfs.getNaN();
        String nonNumber = "(NaN|\\Q" + localNaN + "\\E|Infinity|\\Q" + dfs.getInfinity() + "\\E)";
        this.cachedFloatPattern = Pattern.compile(decimal + "|" + "([-+]?0[xX][0-9a-fA-F]*\\.[0-9a-fA-F]+([pP][-+]?[0-9]+)?)" + "|" + ("((([-+]?(" + nonNumber + ")))|" + "(" + addPositiveSign(nonNumber) + ")|" + "(" + addNegativeSign(nonNumber) + "))"));
        return this.cachedFloatPattern;
    }

    private String getNumeral(String digit, String nonZeroDigit) {
        return "((" + digit + "++)|" + ("(" + nonZeroDigit + digit + "?" + digit + "?" + "(" + ("\\" + this.decimalFormat.getDecimalFormatSymbols().getGroupingSeparator()) + digit + digit + digit + ")+)") + ")";
    }

    private String addPositiveSign(String unsignedNumeral) {
        String positivePrefix = XmlPullParser.NO_NAMESPACE;
        String positiveSuffix = XmlPullParser.NO_NAMESPACE;
        if (!this.decimalFormat.getPositivePrefix().isEmpty()) {
            positivePrefix = "\\Q" + this.decimalFormat.getPositivePrefix() + "\\E";
        }
        if (!this.decimalFormat.getPositiveSuffix().isEmpty()) {
            positiveSuffix = "\\Q" + this.decimalFormat.getPositiveSuffix() + "\\E";
        }
        return positivePrefix + unsignedNumeral + positiveSuffix;
    }

    private String addNegativeSign(String unsignedNumeral) {
        String negativePrefix = XmlPullParser.NO_NAMESPACE;
        String negativeSuffix = XmlPullParser.NO_NAMESPACE;
        if (!this.decimalFormat.getNegativePrefix().isEmpty()) {
            negativePrefix = "\\Q" + this.decimalFormat.getNegativePrefix() + "\\E";
        }
        if (!this.decimalFormat.getNegativeSuffix().isEmpty()) {
            negativeSuffix = "\\Q" + this.decimalFormat.getNegativeSuffix() + "\\E";
        }
        return negativePrefix + unsignedNumeral + negativeSuffix;
    }

    private String removeLocaleInfoFromFloat(String floatString) {
        if (floatString.indexOf((int) Opcodes.OP_INVOKE_INTERFACE_RANGE) != -1 || floatString.indexOf(88) != -1) {
            return floatString;
        }
        int exponentIndex = floatString.indexOf((int) Opcodes.OP_SGET_CHAR);
        if (exponentIndex == -1) {
            exponentIndex = floatString.indexOf(69);
            if (exponentIndex == -1) {
                return removeLocaleInfo(floatString, Float.TYPE);
            }
        }
        String decimalNumeralString = floatString.substring(0, exponentIndex);
        return removeLocaleInfo(decimalNumeralString, Float.TYPE) + "e" + floatString.substring(exponentIndex + 1, floatString.length());
    }

    private String removeLocaleInfo(String token, Class<?> type) {
        int separatorIndex;
        DecimalFormatSymbols dfs = this.decimalFormat.getDecimalFormatSymbols();
        StringBuilder tokenBuilder = new StringBuilder(token);
        boolean negative = removeLocaleSign(tokenBuilder);
        String groupSeparator = String.valueOf(dfs.getGroupingSeparator());
        while (true) {
            separatorIndex = tokenBuilder.indexOf(groupSeparator);
            if (separatorIndex == -1) {
                break;
            }
            tokenBuilder.delete(separatorIndex, separatorIndex + 1);
        }
        separatorIndex = tokenBuilder.indexOf(String.valueOf(dfs.getDecimalSeparator()));
        StringBuilder result = new StringBuilder(XmlPullParser.NO_NAMESPACE);
        int i;
        if (type == Integer.TYPE) {
            for (i = 0; i < tokenBuilder.length(); i++) {
                if (Character.digit(tokenBuilder.charAt(i), 36) != -1) {
                    result.append(tokenBuilder.charAt(i));
                }
            }
        } else if (type != Float.TYPE) {
            throw new AssertionError("Unsupported type: " + type);
        } else if (tokenBuilder.toString().equals(dfs.getNaN())) {
            result.append("NaN");
        } else if (tokenBuilder.toString().equals(dfs.getInfinity())) {
            result.append("Infinity");
        } else {
            for (i = 0; i < tokenBuilder.length(); i++) {
                if (Character.digit(tokenBuilder.charAt(i), (int) DEFAULT_RADIX) != -1) {
                    result.append(Character.digit(tokenBuilder.charAt(i), (int) DEFAULT_RADIX));
                }
            }
        }
        if (result.length() == 0) {
            result = tokenBuilder;
        }
        if (separatorIndex != -1) {
            result.insert(separatorIndex, ".");
        }
        if (negative) {
            result.insert(0, '-');
        }
        return result.toString();
    }

    private boolean removeLocaleSign(StringBuilder tokenBuilder) {
        String positivePrefix = this.decimalFormat.getPositivePrefix();
        String positiveSuffix = this.decimalFormat.getPositiveSuffix();
        String negativePrefix = this.decimalFormat.getNegativePrefix();
        String negativeSuffix = this.decimalFormat.getNegativeSuffix();
        if (tokenBuilder.indexOf("+") == 0) {
            tokenBuilder.delete(0, 1);
        }
        if (!positivePrefix.isEmpty() && tokenBuilder.indexOf(positivePrefix) == 0) {
            tokenBuilder.delete(0, positivePrefix.length());
        }
        if (!(positiveSuffix.isEmpty() || tokenBuilder.indexOf(positiveSuffix) == -1)) {
            tokenBuilder.delete(tokenBuilder.length() - positiveSuffix.length(), tokenBuilder.length());
        }
        boolean negative = false;
        if (tokenBuilder.indexOf("-") == 0) {
            tokenBuilder.delete(0, 1);
            negative = true;
        }
        if (!negativePrefix.isEmpty() && tokenBuilder.indexOf(negativePrefix) == 0) {
            tokenBuilder.delete(0, negativePrefix.length());
            negative = true;
        }
        if (negativeSuffix.isEmpty() || tokenBuilder.indexOf(negativeSuffix) == -1) {
            return negative;
        }
        tokenBuilder.delete(tokenBuilder.length() - negativeSuffix.length(), tokenBuilder.length());
        return true;
    }

    private boolean setTokenRegion() {
        this.matcher.usePattern(this.delimiter);
        this.matcher.region(this.findStartIndex, this.bufferLength);
        int tokenStartIndex = findPreDelimiter();
        if (setHeadTokenRegion(tokenStartIndex)) {
            return true;
        }
        int tokenEndIndex = findDelimiterAfter();
        if (tokenEndIndex == -1) {
            if (this.findStartIndex == this.bufferLength) {
                return false;
            }
            tokenEndIndex = this.bufferLength;
            this.findStartIndex = this.bufferLength;
        }
        this.matcher.region(tokenStartIndex, tokenEndIndex);
        return true;
    }

    private int findPreDelimiter() {
        boolean findComplete = false;
        while (!findComplete) {
            if (this.matcher.find()) {
                findComplete = true;
                if (this.matcher.start() == this.findStartIndex && this.matcher.end() == this.bufferLength && !this.inputExhausted) {
                    readMore();
                    resetMatcher();
                    findComplete = false;
                }
            } else if (this.inputExhausted) {
                return -1;
            } else {
                readMore();
                resetMatcher();
            }
        }
        int tokenStartIndex = this.matcher.end();
        this.findStartIndex = tokenStartIndex;
        return tokenStartIndex;
    }

    private boolean setHeadTokenRegion(int findIndex) {
        boolean setSuccess = false;
        if (findIndex == -1 && this.preStartIndex != this.bufferLength) {
            int tokenStartIndex = this.preStartIndex;
            int tokenEndIndex = this.bufferLength;
            this.findStartIndex = this.bufferLength;
            this.matcher.region(tokenStartIndex, tokenEndIndex);
            setSuccess = true;
        }
        if (findIndex == -1 || this.preStartIndex == this.matcher.start()) {
            return setSuccess;
        }
        tokenStartIndex = this.preStartIndex;
        tokenEndIndex = this.matcher.start();
        this.findStartIndex = this.matcher.start();
        this.matcher.region(tokenStartIndex, tokenEndIndex);
        return true;
    }

    private int findDelimiterAfter() {
        boolean findComplete = false;
        while (!findComplete) {
            if (this.matcher.find()) {
                findComplete = true;
                if (this.matcher.start() == this.findStartIndex && this.matcher.start() == this.matcher.end()) {
                    findComplete = false;
                }
            } else if (this.inputExhausted) {
                return -1;
            } else {
                readMore();
                resetMatcher();
            }
        }
        int tokenEndIndex = this.matcher.start();
        this.findStartIndex = tokenEndIndex;
        return tokenEndIndex;
    }

    private void readMore() {
        int readCount;
        int oldPosition = this.buffer.position();
        int oldBufferLength = this.bufferLength;
        if (this.bufferLength >= this.buffer.capacity()) {
            expandBuffer();
        }
        try {
            this.buffer.limit(this.buffer.capacity());
            this.buffer.position(oldBufferLength);
            do {
                readCount = this.input.read(this.buffer);
            } while (readCount == 0);
        } catch (IOException e) {
            this.bufferLength = this.buffer.position();
            readCount = -1;
            this.lastIOException = e;
        }
        this.buffer.flip();
        this.buffer.position(oldPosition);
        if (readCount == -1) {
            this.inputExhausted = true;
        } else {
            this.bufferLength += readCount;
        }
    }

    private void expandBuffer() {
        int oldPosition = this.buffer.position();
        int oldCapacity = this.buffer.capacity();
        int oldLimit = this.buffer.limit();
        int newCapacity = oldCapacity * 2;
        char[] newBuffer = new char[newCapacity];
        System.arraycopy(this.buffer.array(), 0, newBuffer, 0, oldLimit);
        this.buffer = CharBuffer.wrap(newBuffer, 0, newCapacity);
        this.buffer.position(oldPosition);
        this.buffer.limit(oldLimit);
    }

    public Scanner reset() {
        this.delimiter = DEFAULT_DELIMITER;
        setLocale(Locale.getDefault());
        this.currentRadix = DEFAULT_RADIX;
        return this;
    }
}
