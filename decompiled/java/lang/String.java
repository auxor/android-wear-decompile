package java.lang;

import com.android.dex.DexFormat;
import dalvik.bytecode.Opcodes;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.Charsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Formatter;
import java.util.Locale;
import java.util.jar.Pack200.Unpacker;
import java.util.regex.Pattern;
import java.util.regex.Splitter;
import libcore.util.EmptyArray;
import org.apache.harmony.security.asn1.ASN1Constants;
import org.w3c.dom.traversal.NodeFilter;

public final class String implements Serializable, Comparable<String>, CharSequence {
    private static final char[] ASCII;
    public static final Comparator<String> CASE_INSENSITIVE_ORDER;
    private static final char REPLACEMENT_CHAR = '\ufffd';
    private static final long serialVersionUID = -6849794470754667710L;
    private final int count;
    private int hashCode;
    private final int offset;
    private final char[] value;

    private static final class CaseInsensitiveComparator implements Comparator<String>, Serializable {
        private static final long serialVersionUID = 8575799808933029326L;

        private CaseInsensitiveComparator() {
        }

        public int compare(String o1, String o2) {
            return o1.compareToIgnoreCase(o2);
        }
    }

    private native int fastIndexOf(int i, int i2);

    public native int compareTo(String str);

    public native String intern();

    static {
        CASE_INSENSITIVE_ORDER = new CaseInsensitiveComparator();
        ASCII = new char[NodeFilter.SHOW_COMMENT];
        for (int i = 0; i < ASCII.length; i++) {
            ASCII[i] = (char) i;
        }
    }

    public String() {
        this.value = EmptyArray.CHAR;
        this.offset = 0;
        this.count = 0;
    }

    @FindBugsSuppressWarnings({"DM_DEFAULT_ENCODING"})
    public String(byte[] data) {
        this(data, 0, data.length);
    }

    @Deprecated
    public String(byte[] data, int high) {
        this(data, high, 0, data.length);
    }

    public String(byte[] data, int offset, int byteCount) {
        this(data, offset, byteCount, Charset.defaultCharset());
    }

    @Deprecated
    public String(byte[] data, int high, int offset, int byteCount) {
        if ((offset | byteCount) < 0 || byteCount > data.length - offset) {
            throw failedBoundsCheck(data.length, offset, byteCount);
        }
        this.offset = 0;
        this.value = new char[byteCount];
        this.count = byteCount;
        high <<= 8;
        int i = 0;
        while (i < this.count) {
            int offset2 = offset + 1;
            this.value[i] = (char) ((data[offset] & Opcodes.OP_CONST_CLASS_JUMBO) + high);
            i++;
            offset = offset2;
        }
    }

    public String(byte[] data, int offset, int byteCount, String charsetName) throws UnsupportedEncodingException {
        this(data, offset, byteCount, Charset.forNameUEE(charsetName));
    }

    public String(byte[] data, String charsetName) throws UnsupportedEncodingException {
        this(data, 0, data.length, Charset.forNameUEE(charsetName));
    }

    public String(byte[] data, int offset, int byteCount, Charset charset) {
        if ((offset | byteCount) >= 0) {
            if (byteCount <= data.length - offset) {
                String canonicalCharsetName = charset.name();
                if (canonicalCharsetName.equals("UTF-8")) {
                    byte[] d = data;
                    char[] v = new char[byteCount];
                    int last = offset + byteCount;
                    int s = 0;
                    int idx = offset;
                    while (idx < last) {
                        int s2;
                        int idx2 = idx + 1;
                        byte b0 = d[idx];
                        if ((b0 & NodeFilter.SHOW_COMMENT) == 0) {
                            s2 = s + 1;
                            v[s] = (char) (b0 & Opcodes.OP_CONST_CLASS_JUMBO);
                        } else {
                            int i = b0 & Opcodes.OP_SHL_INT_LIT8;
                            if (r0 != 192) {
                                i = b0 & Opcodes.OP_INVOKE_DIRECT_EMPTY;
                                if (r0 != 224) {
                                    i = b0 & Opcodes.OP_INVOKE_VIRTUAL_QUICK;
                                    if (r0 != 240) {
                                        i = b0 & 252;
                                        if (r0 != 248) {
                                            i = b0 & 254;
                                            if (r0 != 252) {
                                                s2 = s + 1;
                                                v[s] = REPLACEMENT_CHAR;
                                            }
                                        }
                                    }
                                }
                            }
                            int utfCount = 1;
                            i = b0 & Opcodes.OP_INVOKE_DIRECT_EMPTY;
                            if (r0 == 224) {
                                utfCount = 2;
                            } else {
                                i = b0 & Opcodes.OP_INVOKE_VIRTUAL_QUICK;
                                if (r0 == 240) {
                                    utfCount = 3;
                                } else {
                                    i = b0 & 252;
                                    if (r0 == 248) {
                                        utfCount = 4;
                                    } else {
                                        i = b0 & 254;
                                        if (r0 == 252) {
                                            utfCount = 5;
                                        }
                                    }
                                }
                            }
                            if (idx2 + utfCount > last) {
                                s2 = s + 1;
                                v[s] = REPLACEMENT_CHAR;
                                s = s2;
                                idx = idx2;
                            } else {
                                int val = b0 & (31 >> (utfCount - 1));
                                int i2 = 0;
                                idx = idx2;
                                while (i2 < utfCount) {
                                    idx2 = idx + 1;
                                    byte b = d[idx];
                                    i = b & ASN1Constants.CLASS_PRIVATE;
                                    if (r0 != 128) {
                                        s2 = s + 1;
                                        v[s] = REPLACEMENT_CHAR;
                                        s = s2;
                                        idx = idx2 - 1;
                                        break;
                                    }
                                    val = (val << 6) | (b & 63);
                                    i2++;
                                    idx = idx2;
                                }
                                if (utfCount != 2 && val >= 55296 && val <= 57343) {
                                    s2 = s + 1;
                                    v[s] = REPLACEMENT_CHAR;
                                    s = s2;
                                } else if (val > 1114111) {
                                    s2 = s + 1;
                                    v[s] = REPLACEMENT_CHAR;
                                    s = s2;
                                } else {
                                    if (val < 65536) {
                                        s2 = s + 1;
                                        v[s] = (char) val;
                                    } else {
                                        int x = val & DexFormat.MAX_TYPE_IDX;
                                        int lo = 56320 | (x & Double.MAX_EXPONENT);
                                        s2 = s + 1;
                                        v[s] = (char) ((55296 | (((((val >> 16) & 31) - 1) & DexFormat.MAX_TYPE_IDX) << 6)) | (x >> 10));
                                        s = s2 + 1;
                                        v[s2] = (char) lo;
                                        s2 = s;
                                    }
                                    idx2 = idx;
                                }
                            }
                        }
                        s = s2;
                        idx = idx2;
                    }
                    if (s == byteCount) {
                        this.offset = 0;
                        this.value = v;
                        this.count = s;
                        return;
                    }
                    this.offset = 0;
                    this.value = new char[s];
                    this.count = s;
                    System.arraycopy(v, 0, this.value, 0, s);
                    return;
                }
                if (canonicalCharsetName.equals("ISO-8859-1")) {
                    this.offset = 0;
                    this.value = new char[byteCount];
                    this.count = byteCount;
                    Charsets.isoLatin1BytesToChars(data, offset, byteCount, this.value);
                    return;
                }
                if (canonicalCharsetName.equals("US-ASCII")) {
                    this.offset = 0;
                    this.value = new char[byteCount];
                    this.count = byteCount;
                    Charsets.asciiBytesToChars(data, offset, byteCount, this.value);
                    return;
                }
                CharBuffer cb = charset.decode(ByteBuffer.wrap(data, offset, byteCount));
                this.offset = 0;
                this.count = cb.length();
                if (this.count > 0) {
                    this.value = new char[this.count];
                    System.arraycopy(cb.array(), 0, this.value, 0, this.count);
                    return;
                }
                this.value = EmptyArray.CHAR;
                return;
            }
        }
        throw failedBoundsCheck(data.length, offset, byteCount);
    }

    public String(byte[] data, Charset charset) {
        this(data, 0, data.length, charset);
    }

    public String(char[] data) {
        this(data, 0, data.length);
    }

    public String(char[] data, int offset, int charCount) {
        if ((offset | charCount) < 0 || charCount > data.length - offset) {
            throw failedBoundsCheck(data.length, offset, charCount);
        }
        this.offset = 0;
        this.value = new char[charCount];
        this.count = charCount;
        System.arraycopy(data, offset, this.value, 0, this.count);
    }

    String(int offset, int charCount, char[] chars) {
        this.value = chars;
        this.offset = offset;
        this.count = charCount;
    }

    public String(String toCopy) {
        this.value = toCopy.value.length == toCopy.count ? toCopy.value : Arrays.copyOfRange(toCopy.value, toCopy.offset, toCopy.offset + toCopy.length());
        this.offset = 0;
        this.count = this.value.length;
    }

    public String(StringBuffer stringBuffer) {
        this.offset = 0;
        synchronized (stringBuffer) {
            this.value = stringBuffer.shareValue();
            this.count = stringBuffer.length();
        }
    }

    public String(int[] codePoints, int offset, int count) {
        if (codePoints == null) {
            throw new NullPointerException("codePoints == null");
        } else if ((offset | count) < 0 || count > codePoints.length - offset) {
            throw failedBoundsCheck(codePoints.length, offset, count);
        } else {
            this.offset = 0;
            this.value = new char[(count * 2)];
            int c = 0;
            for (int i = offset; i < offset + count; i++) {
                c += Character.toChars(codePoints[i], this.value, c);
            }
            this.count = c;
        }
    }

    public String(StringBuilder stringBuilder) {
        if (stringBuilder == null) {
            throw new NullPointerException("stringBuilder == null");
        }
        this.offset = 0;
        this.count = stringBuilder.length();
        this.value = new char[this.count];
        stringBuilder.getChars(0, this.count, this.value, 0);
    }

    public char charAt(int index) {
        if (index >= 0 && index < this.count) {
            return this.value[this.offset + index];
        }
        throw indexAndLength(index);
    }

    private StringIndexOutOfBoundsException indexAndLength(int index) {
        throw new StringIndexOutOfBoundsException(this, index);
    }

    private StringIndexOutOfBoundsException startEndAndLength(int start, int end) {
        throw new StringIndexOutOfBoundsException(this, start, end - start);
    }

    private StringIndexOutOfBoundsException failedBoundsCheck(int arrayLength, int offset, int count) {
        throw new StringIndexOutOfBoundsException(arrayLength, offset, count);
    }

    private char foldCase(char ch) {
        if (ch >= '\u0080') {
            return Character.toLowerCase(Character.toUpperCase(ch));
        }
        if ('A' > ch || ch > 'Z') {
            return ch;
        }
        return (char) (ch + 32);
    }

    public int compareToIgnoreCase(String string) {
        int o1 = this.offset;
        int o2 = string.offset;
        int end = this.offset + (this.count < string.count ? this.count : string.count);
        char[] target = string.value;
        int o22 = o2;
        int o12 = o1;
        while (o12 < end) {
            o1 = o12 + 1;
            char c1 = this.value[o12];
            o2 = o22 + 1;
            char c2 = target[o22];
            if (c1 == c2) {
                o22 = o2;
                o12 = o1;
            } else {
                int result = foldCase(c1) - foldCase(c2);
                if (result != 0) {
                    return result;
                }
                o22 = o2;
                o12 = o1;
            }
        }
        o2 = o22;
        o1 = o12;
        return this.count - string.count;
    }

    public String concat(String string) {
        if (string.count <= 0 || this.count <= 0) {
            return this.count != 0 ? this : string;
        } else {
            char[] buffer = new char[(this.count + string.count)];
            System.arraycopy(this.value, this.offset, buffer, 0, this.count);
            System.arraycopy(string.value, string.offset, buffer, this.count, string.count);
            return new String(0, buffer.length, buffer);
        }
    }

    public static String copyValueOf(char[] data) {
        return new String(data, 0, data.length);
    }

    public static String copyValueOf(char[] data, int start, int length) {
        return new String(data, start, length);
    }

    public boolean endsWith(String suffix) {
        return regionMatches(this.count - suffix.count, suffix, 0, suffix.count);
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof String)) {
            return false;
        }
        String s = (String) other;
        int count = this.count;
        if (s.count != count) {
            return false;
        }
        if (hashCode() != s.hashCode()) {
            return false;
        }
        char[] value1 = this.value;
        int offset1 = this.offset;
        char[] value2 = s.value;
        int offset2 = s.offset;
        int end = offset1 + count;
        while (offset1 < end) {
            if (value1[offset1] != value2[offset2]) {
                return false;
            }
            offset1++;
            offset2++;
        }
        return true;
    }

    @FindBugsSuppressWarnings({"ES_COMPARING_PARAMETER_STRING_WITH_EQ"})
    public boolean equalsIgnoreCase(String string) {
        if (string == this) {
            return true;
        }
        if (string == null || this.count != string.count) {
            return false;
        }
        int o1 = this.offset;
        int o2 = string.offset;
        int end = this.offset + this.count;
        char[] target = string.value;
        int o22 = o2;
        int o12 = o1;
        while (o12 < end) {
            o1 = o12 + 1;
            char c1 = this.value[o12];
            o2 = o22 + 1;
            char c2 = target[o22];
            if (c1 != c2 && foldCase(c1) != foldCase(c2)) {
                return false;
            }
            o22 = o2;
            o12 = o1;
        }
        return true;
    }

    @Deprecated
    public void getBytes(int start, int end, byte[] data, int index) {
        if (start < 0 || start > end || end > this.count) {
            throw startEndAndLength(start, end);
        }
        end += this.offset;
        try {
            int i = this.offset + start;
            int index2 = index;
            while (i < end) {
                index = index2 + 1;
                data[index2] = (byte) this.value[i];
                i++;
                index2 = index;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw failedBoundsCheck(data.length, index, end - start);
        }
    }

    public byte[] getBytes() {
        return getBytes(Charset.defaultCharset());
    }

    public byte[] getBytes(String charsetName) throws UnsupportedEncodingException {
        return getBytes(Charset.forNameUEE(charsetName));
    }

    public byte[] getBytes(Charset charset) {
        String canonicalCharsetName = charset.name();
        if (canonicalCharsetName.equals("UTF-8")) {
            return Charsets.toUtf8Bytes(this.value, this.offset, this.count);
        }
        if (canonicalCharsetName.equals("ISO-8859-1")) {
            return Charsets.toIsoLatin1Bytes(this.value, this.offset, this.count);
        }
        if (canonicalCharsetName.equals("US-ASCII")) {
            return Charsets.toAsciiBytes(this.value, this.offset, this.count);
        }
        if (canonicalCharsetName.equals("UTF-16BE")) {
            return Charsets.toBigEndianUtf16Bytes(this.value, this.offset, this.count);
        }
        ByteBuffer buffer = charset.encode(CharBuffer.wrap(this.value, this.offset, this.count).asReadOnlyBuffer());
        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);
        return bytes;
    }

    public void getChars(int start, int end, char[] buffer, int index) {
        if (start < 0 || start > end || end > this.count) {
            throw startEndAndLength(start, end);
        }
        System.arraycopy(this.value, this.offset + start, buffer, index, end - start);
    }

    void _getChars(int start, int end, char[] buffer, int index) {
        System.arraycopy(this.value, this.offset + start, buffer, index, end - start);
    }

    public int hashCode() {
        int hash = this.hashCode;
        if (hash == 0) {
            if (this.count == 0) {
                return 0;
            }
            int end = this.count + this.offset;
            char[] chars = this.value;
            for (int i = this.offset; i < end; i++) {
                hash = (hash * 31) + chars[i];
            }
            this.hashCode = hash;
        }
        return hash;
    }

    public int indexOf(int c) {
        if (c > DexFormat.MAX_TYPE_IDX) {
            return indexOfSupplementary(c, 0);
        }
        return fastIndexOf(c, 0);
    }

    public int indexOf(int c, int start) {
        if (c > DexFormat.MAX_TYPE_IDX) {
            return indexOfSupplementary(c, start);
        }
        return fastIndexOf(c, start);
    }

    private int indexOfSupplementary(int c, int start) {
        if (!Character.isSupplementaryCodePoint(c)) {
            return -1;
        }
        char[] chars = Character.toChars(c);
        return indexOf(new String(0, chars.length, chars), start);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int indexOf(java.lang.String r15) {
        /*
        r14 = this;
        r11 = -1;
        r7 = 0;
        r8 = r15.count;
        r0 = r14.count;
        if (r8 <= 0) goto L_0x0038;
    L_0x0008:
        if (r8 <= r0) goto L_0x000c;
    L_0x000a:
        r4 = r11;
    L_0x000b:
        return r4;
    L_0x000c:
        r10 = r15.value;
        r9 = r15.offset;
        r3 = r10[r9];
        r2 = r9 + r8;
    L_0x0014:
        r4 = r14.indexOf(r3, r7);
        if (r4 == r11) goto L_0x001e;
    L_0x001a:
        r12 = r8 + r4;
        if (r12 <= r0) goto L_0x0020;
    L_0x001e:
        r4 = r11;
        goto L_0x000b;
    L_0x0020:
        r12 = r14.offset;
        r5 = r12 + r4;
        r6 = r9;
        r1 = r14.value;
    L_0x0027:
        r6 = r6 + 1;
        if (r6 >= r2) goto L_0x0033;
    L_0x002b:
        r5 = r5 + 1;
        r12 = r1[r5];
        r13 = r10[r6];
        if (r12 == r13) goto L_0x0027;
    L_0x0033:
        if (r6 == r2) goto L_0x000b;
    L_0x0035:
        r7 = r4 + 1;
        goto L_0x0014;
    L_0x0038:
        if (r7 >= r0) goto L_0x003b;
    L_0x003a:
        r0 = r7;
    L_0x003b:
        r4 = r0;
        goto L_0x000b;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.lang.String.indexOf(java.lang.String):int");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int indexOf(java.lang.String r14, int r15) {
        /*
        r13 = this;
        r10 = -1;
        if (r15 >= 0) goto L_0x0004;
    L_0x0003:
        r15 = 0;
    L_0x0004:
        r7 = r14.count;
        r0 = r13.count;
        if (r7 <= 0) goto L_0x003c;
    L_0x000a:
        r11 = r7 + r15;
        if (r11 <= r0) goto L_0x0010;
    L_0x000e:
        r4 = r10;
    L_0x000f:
        return r4;
    L_0x0010:
        r9 = r14.value;
        r8 = r14.offset;
        r3 = r9[r8];
        r2 = r8 + r7;
    L_0x0018:
        r4 = r13.indexOf(r3, r15);
        if (r4 == r10) goto L_0x0022;
    L_0x001e:
        r11 = r7 + r4;
        if (r11 <= r0) goto L_0x0024;
    L_0x0022:
        r4 = r10;
        goto L_0x000f;
    L_0x0024:
        r11 = r13.offset;
        r5 = r11 + r4;
        r6 = r8;
        r1 = r13.value;
    L_0x002b:
        r6 = r6 + 1;
        if (r6 >= r2) goto L_0x0037;
    L_0x002f:
        r5 = r5 + 1;
        r11 = r1[r5];
        r12 = r9[r6];
        if (r11 == r12) goto L_0x002b;
    L_0x0037:
        if (r6 == r2) goto L_0x000f;
    L_0x0039:
        r15 = r4 + 1;
        goto L_0x0018;
    L_0x003c:
        if (r15 >= r0) goto L_0x003f;
    L_0x003e:
        r0 = r15;
    L_0x003f:
        r4 = r0;
        goto L_0x000f;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.lang.String.indexOf(java.lang.String, int):int");
    }

    public boolean isEmpty() {
        return this.count == 0;
    }

    public int lastIndexOf(int c) {
        if (c > DexFormat.MAX_TYPE_IDX) {
            return lastIndexOfSupplementary(c, Integer.MAX_VALUE);
        }
        int _count = this.count;
        int _offset = this.offset;
        char[] _value = this.value;
        for (int i = (_offset + _count) - 1; i >= _offset; i--) {
            if (_value[i] == c) {
                return i - _offset;
            }
        }
        return -1;
    }

    public int lastIndexOf(int c, int start) {
        if (c > DexFormat.MAX_TYPE_IDX) {
            return lastIndexOfSupplementary(c, start);
        }
        int _count = this.count;
        int _offset = this.offset;
        char[] _value = this.value;
        if (start >= 0) {
            if (start >= _count) {
                start = _count - 1;
            }
            for (int i = _offset + start; i >= _offset; i--) {
                if (_value[i] == c) {
                    return i - _offset;
                }
            }
        }
        return -1;
    }

    private int lastIndexOfSupplementary(int c, int start) {
        if (!Character.isSupplementaryCodePoint(c)) {
            return -1;
        }
        char[] chars = Character.toChars(c);
        return lastIndexOf(new String(0, chars.length, chars), start);
    }

    public int lastIndexOf(String string) {
        return lastIndexOf(string, this.count);
    }

    public int lastIndexOf(String subString, int start) {
        int subCount = subString.count;
        if (subCount > this.count || start < 0) {
            return -1;
        }
        if (subCount > 0) {
            if (start > this.count - subCount) {
                start = this.count - subCount;
            }
            char[] target = subString.value;
            int subOffset = subString.offset;
            int firstChar = target[subOffset];
            int end = subOffset + subCount;
            while (true) {
                int i = lastIndexOf(firstChar, start);
                if (i == -1) {
                    return -1;
                }
                int o1 = this.offset + i;
                int o2 = subOffset;
                do {
                    o2++;
                    if (o2 >= end) {
                        break;
                    }
                    o1++;
                } while (this.value[o1] == target[o2]);
                if (o2 == end) {
                    return i;
                }
                start = i - 1;
            }
        } else {
            return start < this.count ? start : this.count;
        }
    }

    public int length() {
        return this.count;
    }

    public boolean regionMatches(int thisStart, String string, int start, int length) {
        if (string == null) {
            throw new NullPointerException("string == null");
        } else if (start < 0 || string.count - start < length) {
            return false;
        } else {
            if (thisStart < 0 || this.count - thisStart < length) {
                return false;
            }
            if (length <= 0) {
                return true;
            }
            int o1 = this.offset + thisStart;
            int o2 = string.offset + start;
            char[] value1 = this.value;
            char[] value2 = string.value;
            for (int i = 0; i < length; i++) {
                if (value1[o1 + i] != value2[o2 + i]) {
                    return false;
                }
            }
            return true;
        }
    }

    public boolean regionMatches(boolean ignoreCase, int thisStart, String string, int start, int length) {
        if (!ignoreCase) {
            return regionMatches(thisStart, string, start, length);
        }
        if (string == null) {
            throw new NullPointerException("string == null");
        } else if (thisStart < 0 || length > this.count - thisStart || start < 0 || length > string.count - start) {
            return false;
        } else {
            thisStart += this.offset;
            start += string.offset;
            int end = thisStart + length;
            char[] target = string.value;
            int start2 = start;
            int thisStart2 = thisStart;
            while (thisStart2 < end) {
                thisStart = thisStart2 + 1;
                char c1 = this.value[thisStart2];
                start = start2 + 1;
                char c2 = target[start2];
                if (c1 != c2 && foldCase(c1) != foldCase(c2)) {
                    return false;
                }
                start2 = start;
                thisStart2 = thisStart;
            }
            start = start2;
            thisStart = thisStart2;
            return true;
        }
    }

    public String replace(char oldChar, char newChar) {
        char[] buffer = this.value;
        int _offset = this.offset;
        int _count = this.count;
        int idx = _offset;
        int last = _offset + _count;
        boolean copied = false;
        while (idx < last) {
            if (buffer[idx] == oldChar) {
                if (!copied) {
                    char[] newBuffer = new char[_count];
                    System.arraycopy(buffer, _offset, newBuffer, 0, _count);
                    buffer = newBuffer;
                    idx -= _offset;
                    last -= _offset;
                    copied = true;
                }
                buffer[idx] = newChar;
            }
            idx++;
        }
        return copied ? new String(0, this.count, buffer) : this;
    }

    public String replace(CharSequence target, CharSequence replacement) {
        if (target == null) {
            throw new NullPointerException("target == null");
        } else if (replacement == null) {
            throw new NullPointerException("replacement == null");
        } else {
            String targetString = target.toString();
            int matchStart = indexOf(targetString, 0);
            if (matchStart == -1) {
                return this;
            }
            String replacementString = replacement.toString();
            int targetLength = targetString.length();
            StringBuilder result;
            if (targetLength == 0) {
                result = new StringBuilder(this.count + ((this.count + 1) * replacementString.length()));
                result.append(replacementString);
                int end = this.offset + this.count;
                for (int i = this.offset; i != end; i++) {
                    result.append(this.value[i]);
                    result.append(replacementString);
                }
                return result.toString();
            }
            result = new StringBuilder(this.count);
            int searchStart = 0;
            do {
                result.append(this.value, this.offset + searchStart, matchStart - searchStart);
                result.append(replacementString);
                searchStart = matchStart + targetLength;
                matchStart = indexOf(targetString, searchStart);
            } while (matchStart != -1);
            result.append(this.value, this.offset + searchStart, this.count - searchStart);
            return result.toString();
        }
    }

    public boolean startsWith(String prefix) {
        return startsWith(prefix, 0);
    }

    public boolean startsWith(String prefix, int start) {
        return regionMatches(start, prefix, 0, prefix.count);
    }

    public String substring(int start) {
        if (start == 0) {
            return this;
        }
        if (start >= 0 && start <= this.count) {
            return new String(this.offset + start, this.count - start, this.value);
        }
        throw indexAndLength(start);
    }

    public String substring(int start, int end) {
        if (start == 0 && end == this.count) {
            return this;
        }
        if (start >= 0 && start <= end && end <= this.count) {
            return new String(this.offset + start, end - start, this.value);
        }
        throw startEndAndLength(start, end);
    }

    public char[] toCharArray() {
        char[] buffer = new char[this.count];
        System.arraycopy(this.value, this.offset, buffer, 0, this.count);
        return buffer;
    }

    public String toLowerCase() {
        return CaseMapper.toLowerCase(Locale.getDefault(), this, this.value, this.offset, this.count);
    }

    public String toLowerCase(Locale locale) {
        return CaseMapper.toLowerCase(locale, this, this.value, this.offset, this.count);
    }

    public String toString() {
        return this;
    }

    public String toUpperCase() {
        return CaseMapper.toUpperCase(Locale.getDefault(), this, this.value, this.offset, this.count);
    }

    public String toUpperCase(Locale locale) {
        return CaseMapper.toUpperCase(locale, this, this.value, this.offset, this.count);
    }

    public String trim() {
        int start = this.offset;
        int last = (this.offset + this.count) - 1;
        int end = last;
        while (start <= end && this.value[start] <= ' ') {
            start++;
        }
        while (end >= start && this.value[end] <= ' ') {
            end--;
        }
        return (start == this.offset && end == last) ? this : new String(start, (end - start) + 1, this.value);
    }

    public static String valueOf(char[] data) {
        return new String(data, 0, data.length);
    }

    public static String valueOf(char[] data, int start, int length) {
        return new String(data, start, length);
    }

    public static String valueOf(char value) {
        String s;
        if (value < '\u0080') {
            s = new String((int) value, 1, ASCII);
        } else {
            s = new String(0, 1, new char[]{value});
        }
        s.hashCode = value;
        return s;
    }

    public static String valueOf(double value) {
        return Double.toString(value);
    }

    public static String valueOf(float value) {
        return Float.toString(value);
    }

    public static String valueOf(int value) {
        return Integer.toString(value);
    }

    public static String valueOf(long value) {
        return Long.toString(value);
    }

    public static String valueOf(Object value) {
        return value != null ? value.toString() : "null";
    }

    public static String valueOf(boolean value) {
        return value ? Unpacker.TRUE : Unpacker.FALSE;
    }

    public boolean contentEquals(StringBuffer sb) {
        boolean z = false;
        synchronized (sb) {
            int size = sb.length();
            if (this.count != size) {
            } else {
                z = regionMatches(0, new String(0, size, sb.getValue()), 0, size);
            }
        }
        return z;
    }

    public boolean contentEquals(CharSequence cs) {
        if (cs == null) {
            throw new NullPointerException("cs == null");
        }
        int len = cs.length();
        if (len != this.count) {
            return false;
        }
        if (len == 0 && this.count == 0) {
            return true;
        }
        return regionMatches(0, cs.toString(), 0, len);
    }

    public boolean matches(String regularExpression) {
        return Pattern.matches(regularExpression, this);
    }

    public String replaceAll(String regularExpression, String replacement) {
        return Pattern.compile(regularExpression).matcher(this).replaceAll(replacement);
    }

    public String replaceFirst(String regularExpression, String replacement) {
        return Pattern.compile(regularExpression).matcher(this).replaceFirst(replacement);
    }

    public String[] split(String regularExpression) {
        return split(regularExpression, 0);
    }

    public String[] split(String regularExpression, int limit) {
        String[] result = Splitter.fastSplit(regularExpression, this, limit);
        return result != null ? result : Pattern.compile(regularExpression).split(this, limit);
    }

    public CharSequence subSequence(int start, int end) {
        return substring(start, end);
    }

    public int codePointAt(int index) {
        if (index >= 0 && index < this.count) {
            return Character.codePointAt(this.value, this.offset + index, this.offset + this.count);
        }
        throw indexAndLength(index);
    }

    public int codePointBefore(int index) {
        if (index >= 1 && index <= this.count) {
            return Character.codePointBefore(this.value, this.offset + index, this.offset);
        }
        throw indexAndLength(index);
    }

    public int codePointCount(int start, int end) {
        if (start >= 0 && end <= this.count && start <= end) {
            return Character.codePointCount(this.value, this.offset + start, end - start);
        }
        throw startEndAndLength(start, end);
    }

    public boolean contains(CharSequence cs) {
        if (cs != null) {
            return indexOf(cs.toString()) >= 0;
        } else {
            throw new NullPointerException("cs == null");
        }
    }

    public int offsetByCodePoints(int index, int codePointOffset) {
        return Character.offsetByCodePoints(this.value, this.offset, this.count, index + this.offset, codePointOffset) - this.offset;
    }

    public static String format(String format, Object... args) {
        return format(Locale.getDefault(), format, args);
    }

    public static String format(Locale locale, String format, Object... args) {
        if (format == null) {
            throw new NullPointerException("format == null");
        }
        return new Formatter(new StringBuilder(format.length() + (args == null ? 0 : args.length * 10)), locale).format(format, args).toString();
    }

    @FindBugsSuppressWarnings({"UPM_UNCALLED_PRIVATE_METHOD"})
    private static int indexOf(String haystackString, String needleString, int cache, int md2, char lastChar) {
        char[] haystack = haystackString.value;
        int haystackOffset = haystackString.offset;
        int haystackLength = haystackString.count;
        char[] needle = needleString.value;
        int needleOffset = needleString.offset;
        int needleLengthMinus1 = needleString.count - 1;
        int haystackEnd = haystackOffset + haystackLength;
        int i = haystackOffset + needleLengthMinus1;
        while (i < haystackEnd) {
            if (lastChar == haystack[i]) {
                int j = 0;
                while (j < needleLengthMinus1) {
                    if (needle[j + needleOffset] != haystack[(i + j) - needleLengthMinus1]) {
                        int skip = 1;
                        if (((1 << haystack[i]) & cache) == 0) {
                            skip = 1 + j;
                        }
                        i += Math.max(md2, skip);
                    } else {
                        j++;
                    }
                }
                return (i - needleLengthMinus1) - haystackOffset;
            }
            if (((1 << haystack[i]) & cache) == 0) {
                i += needleLengthMinus1;
            }
            i++;
        }
        return -1;
    }
}
