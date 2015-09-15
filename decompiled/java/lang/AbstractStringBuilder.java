package java.lang;

import java.io.InvalidObjectException;
import java.util.Arrays;
import java.util.Locale;
import libcore.util.EmptyArray;
import org.w3c.dom.traversal.NodeFilter;
import org.xmlpull.v1.XmlPullParser;

abstract class AbstractStringBuilder {
    static final int INITIAL_CAPACITY = 16;
    private int count;
    private boolean shared;
    private char[] value;

    final char[] getValue() {
        return this.value;
    }

    final char[] shareValue() {
        this.shared = true;
        return this.value;
    }

    final void set(char[] val, int len) throws InvalidObjectException {
        if (val == null) {
            val = EmptyArray.CHAR;
        }
        if (val.length < len) {
            throw new InvalidObjectException("count out of range");
        }
        this.shared = false;
        this.value = val;
        this.count = len;
    }

    AbstractStringBuilder() {
        this.value = new char[INITIAL_CAPACITY];
    }

    AbstractStringBuilder(int capacity) {
        if (capacity < 0) {
            throw new NegativeArraySizeException(Integer.toString(capacity));
        }
        this.value = new char[capacity];
    }

    AbstractStringBuilder(String string) {
        this.count = string.length();
        this.shared = false;
        this.value = new char[(this.count + INITIAL_CAPACITY)];
        string._getChars(0, this.count, this.value, 0);
    }

    private void enlargeBuffer(int min) {
        int newCount = ((this.value.length >> 1) + this.value.length) + 2;
        if (min <= newCount) {
            min = newCount;
        }
        char[] newData = new char[min];
        System.arraycopy(this.value, 0, newData, 0, this.count);
        this.value = newData;
        this.shared = false;
    }

    final void appendNull() {
        int newCount = this.count + 4;
        if (newCount > this.value.length) {
            enlargeBuffer(newCount);
        }
        char[] cArr = this.value;
        int i = this.count;
        this.count = i + 1;
        cArr[i] = 'n';
        cArr = this.value;
        i = this.count;
        this.count = i + 1;
        cArr[i] = Locale.UNICODE_LOCALE_EXTENSION;
        cArr = this.value;
        i = this.count;
        this.count = i + 1;
        cArr[i] = 'l';
        cArr = this.value;
        i = this.count;
        this.count = i + 1;
        cArr[i] = 'l';
    }

    final void append0(char[] chars) {
        int newCount = this.count + chars.length;
        if (newCount > this.value.length) {
            enlargeBuffer(newCount);
        }
        System.arraycopy(chars, 0, this.value, this.count, chars.length);
        this.count = newCount;
    }

    final void append0(char[] chars, int offset, int length) {
        Arrays.checkOffsetAndCount(chars.length, offset, length);
        int newCount = this.count + length;
        if (newCount > this.value.length) {
            enlargeBuffer(newCount);
        }
        System.arraycopy(chars, offset, this.value, this.count, length);
        this.count = newCount;
    }

    final void append0(char ch) {
        if (this.count == this.value.length) {
            enlargeBuffer(this.count + 1);
        }
        char[] cArr = this.value;
        int i = this.count;
        this.count = i + 1;
        cArr[i] = ch;
    }

    final void append0(String string) {
        if (string == null) {
            appendNull();
            return;
        }
        int length = string.length();
        int newCount = this.count + length;
        if (newCount > this.value.length) {
            enlargeBuffer(newCount);
        }
        string._getChars(0, length, this.value, this.count);
        this.count = newCount;
    }

    final void append0(CharSequence s, int start, int end) {
        if (s == null) {
            s = "null";
        }
        if ((start | end) < 0 || start > end || end > s.length()) {
            throw new IndexOutOfBoundsException();
        }
        int length = end - start;
        int newCount = this.count + length;
        if (newCount > this.value.length) {
            enlargeBuffer(newCount);
        } else if (this.shared) {
            this.value = (char[]) this.value.clone();
            this.shared = false;
        }
        if (s instanceof String) {
            ((String) s)._getChars(start, end, this.value, this.count);
        } else if (s instanceof AbstractStringBuilder) {
            System.arraycopy(((AbstractStringBuilder) s).value, start, this.value, this.count, length);
        } else {
            int i = start;
            int j = this.count;
            while (i < end) {
                int j2 = j + 1;
                this.value[j] = s.charAt(i);
                i++;
                j = j2;
            }
        }
        this.count = newCount;
    }

    public int capacity() {
        return this.value.length;
    }

    public char charAt(int index) {
        if (index >= 0 && index < this.count) {
            return this.value[index];
        }
        throw indexAndLength(index);
    }

    private StringIndexOutOfBoundsException indexAndLength(int index) {
        throw new StringIndexOutOfBoundsException(this.count, index);
    }

    private StringIndexOutOfBoundsException startEndAndLength(int start, int end) {
        throw new StringIndexOutOfBoundsException(this.count, start, end - start);
    }

    final void delete0(int start, int end) {
        if (end > this.count) {
            end = this.count;
        }
        if (start < 0 || start > this.count || start > end) {
            throw startEndAndLength(start, end);
        } else if (end != start) {
            int length = this.count - end;
            if (length >= 0) {
                if (this.shared) {
                    char[] newData = new char[this.value.length];
                    System.arraycopy(this.value, 0, newData, 0, start);
                    System.arraycopy(this.value, end, newData, start, length);
                    this.value = newData;
                    this.shared = false;
                } else {
                    System.arraycopy(this.value, end, this.value, start, length);
                }
            }
            this.count -= end - start;
        }
    }

    final void deleteCharAt0(int index) {
        if (index < 0 || index >= this.count) {
            throw indexAndLength(index);
        }
        delete0(index, index + 1);
    }

    public void ensureCapacity(int min) {
        if (min > this.value.length) {
            enlargeBuffer(Math.max((this.value.length * 2) + 2, min));
        }
    }

    public void getChars(int start, int end, char[] dst, int dstStart) {
        if (start > this.count || end > this.count || start > end) {
            throw startEndAndLength(start, end);
        }
        System.arraycopy(this.value, start, dst, dstStart, end - start);
    }

    final void insert0(int index, char[] chars) {
        if (index < 0 || index > this.count) {
            throw indexAndLength(index);
        } else if (chars.length != 0) {
            move(chars.length, index);
            System.arraycopy(chars, 0, this.value, index, chars.length);
            this.count += chars.length;
        }
    }

    final void insert0(int index, char[] chars, int start, int length) {
        if (index < 0 || index > this.count || start < 0 || length < 0 || length > chars.length - start) {
            throw new StringIndexOutOfBoundsException("this.length=" + this.count + "; index=" + index + "; chars.length=" + chars.length + "; start=" + start + "; length=" + length);
        } else if (length != 0) {
            move(length, index);
            System.arraycopy(chars, start, this.value, index, length);
            this.count += length;
        }
    }

    final void insert0(int index, char ch) {
        if (index < 0 || index > this.count) {
            throw new ArrayIndexOutOfBoundsException(this.count, index);
        }
        move(1, index);
        this.value[index] = ch;
        this.count++;
    }

    final void insert0(int index, String string) {
        if (index < 0 || index > this.count) {
            throw indexAndLength(index);
        }
        if (string == null) {
            string = "null";
        }
        int min = string.length();
        if (min != 0) {
            move(min, index);
            string._getChars(0, min, this.value, index);
            this.count += min;
        }
    }

    final void insert0(int index, CharSequence s, int start, int end) {
        if (s == null) {
            s = "null";
        }
        if (((index | start) | end) < 0 || index > this.count || start > end || end > s.length()) {
            throw new IndexOutOfBoundsException();
        }
        insert0(index, s.subSequence(start, end).toString());
    }

    public int length() {
        return this.count;
    }

    private void move(int size, int index) {
        int newCount;
        if (this.value.length - this.count < size) {
            newCount = Math.max(this.count + size, (this.value.length * 2) + 2);
        } else if (this.shared) {
            newCount = this.value.length;
        } else {
            System.arraycopy(this.value, index, this.value, index + size, this.count - index);
            return;
        }
        char[] newData = new char[newCount];
        System.arraycopy(this.value, 0, newData, 0, index);
        System.arraycopy(this.value, index, newData, index + size, this.count - index);
        this.value = newData;
        this.shared = false;
    }

    final void replace0(int start, int end, String string) {
        if (start >= 0) {
            if (end > this.count) {
                end = this.count;
            }
            if (end > start) {
                int stringLength = string.length();
                int diff = (end - start) - stringLength;
                if (diff > 0) {
                    if (this.shared) {
                        char[] newData = new char[this.value.length];
                        System.arraycopy(this.value, 0, newData, 0, start);
                        System.arraycopy(this.value, end, newData, start + stringLength, this.count - end);
                        this.value = newData;
                        this.shared = false;
                    } else {
                        System.arraycopy(this.value, end, this.value, start + stringLength, this.count - end);
                    }
                } else if (diff < 0) {
                    move(-diff, end);
                } else if (this.shared) {
                    this.value = (char[]) this.value.clone();
                    this.shared = false;
                }
                string._getChars(0, stringLength, this.value, start);
                this.count -= diff;
                return;
            } else if (start == end) {
                if (string == null) {
                    throw new NullPointerException("string == null");
                }
                insert0(start, string);
                return;
            }
        }
        throw startEndAndLength(start, end);
    }

    final void reverse0() {
        if (this.count >= 2) {
            int i;
            int end;
            if (this.shared) {
                char[] newData = new char[this.value.length];
                i = 0;
                end = this.count;
                while (i < this.count) {
                    char high = this.value[i];
                    int i2 = i + 1;
                    int i3 = this.count;
                    if (i2 < r0 && high >= Character.MIN_SURROGATE && high <= Character.MAX_HIGH_SURROGATE) {
                        char low = this.value[i + 1];
                        if (low >= Character.MIN_LOW_SURROGATE && low <= Character.MAX_SURROGATE) {
                            end--;
                            newData[end] = low;
                            i++;
                        }
                    }
                    end--;
                    newData[end] = high;
                    i++;
                }
                this.value = newData;
                this.shared = false;
                return;
            }
            end = this.count - 1;
            char frontHigh = this.value[0];
            char endLow = this.value[end];
            boolean allowFrontSur = true;
            boolean allowEndSur = true;
            i = 0;
            int mid = this.count / 2;
            while (i < mid) {
                char frontLow = this.value[i + 1];
                char endHigh = this.value[end - 1];
                boolean surAtFront = allowFrontSur && frontLow >= Character.MIN_LOW_SURROGATE && frontLow <= Character.MAX_SURROGATE && frontHigh >= Character.MIN_SURROGATE && frontHigh <= Character.MAX_HIGH_SURROGATE;
                if (!surAtFront || this.count >= 3) {
                    boolean surAtEnd = allowEndSur && endHigh >= Character.MIN_SURROGATE && endHigh <= Character.MAX_HIGH_SURROGATE && endLow >= Character.MIN_LOW_SURROGATE && endLow <= Character.MAX_SURROGATE;
                    allowEndSur = true;
                    allowFrontSur = true;
                    if (surAtFront == surAtEnd) {
                        if (surAtFront) {
                            this.value[end] = frontLow;
                            this.value[end - 1] = frontHigh;
                            this.value[i] = endHigh;
                            this.value[i + 1] = endLow;
                            frontHigh = this.value[i + 2];
                            endLow = this.value[end - 2];
                            i++;
                            end--;
                        } else {
                            this.value[end] = frontHigh;
                            this.value[i] = endLow;
                            frontHigh = frontLow;
                            endLow = endHigh;
                        }
                    } else if (surAtFront) {
                        this.value[end] = frontLow;
                        this.value[i] = endLow;
                        endLow = endHigh;
                        allowFrontSur = false;
                    } else {
                        this.value[end] = frontHigh;
                        this.value[i] = endHigh;
                        frontHigh = frontLow;
                        allowEndSur = false;
                    }
                    i++;
                    end--;
                } else {
                    return;
                }
            }
            if ((this.count & 1) != 1) {
                return;
            }
            if (!allowFrontSur || !allowEndSur) {
                char[] cArr = this.value;
                if (!allowFrontSur) {
                    endLow = frontHigh;
                }
                cArr[end] = endLow;
            }
        }
    }

    public void setCharAt(int index, char ch) {
        if (index < 0 || index >= this.count) {
            throw indexAndLength(index);
        }
        if (this.shared) {
            this.value = (char[]) this.value.clone();
            this.shared = false;
        }
        this.value[index] = ch;
    }

    public void setLength(int length) {
        if (length < 0) {
            throw new StringIndexOutOfBoundsException("length < 0: " + length);
        }
        if (length > this.value.length) {
            enlargeBuffer(length);
        } else if (this.shared) {
            char[] newData = new char[this.value.length];
            System.arraycopy(this.value, 0, newData, 0, this.count);
            this.value = newData;
            this.shared = false;
        } else if (this.count < length) {
            Arrays.fill(this.value, this.count, length, '\u0000');
        }
        this.count = length;
    }

    public String substring(int start) {
        if (start < 0 || start > this.count) {
            throw indexAndLength(start);
        } else if (start == this.count) {
            return XmlPullParser.NO_NAMESPACE;
        } else {
            return new String(this.value, start, this.count - start);
        }
    }

    public String substring(int start, int end) {
        if (start < 0 || start > end || end > this.count) {
            throw startEndAndLength(start, end);
        } else if (start == end) {
            return XmlPullParser.NO_NAMESPACE;
        } else {
            return new String(this.value, start, end - start);
        }
    }

    public String toString() {
        if (this.count == 0) {
            return XmlPullParser.NO_NAMESPACE;
        }
        int wasted = this.value.length - this.count;
        if (wasted >= NodeFilter.SHOW_DOCUMENT || (wasted >= INITIAL_CAPACITY && wasted >= (this.count >> 1))) {
            return new String(this.value, 0, this.count);
        }
        this.shared = true;
        return new String(0, this.count, this.value);
    }

    public CharSequence subSequence(int start, int end) {
        return substring(start, end);
    }

    public int indexOf(String string) {
        return indexOf(string, 0);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int indexOf(java.lang.String r10, int r11) {
        /*
        r9 = this;
        r6 = -1;
        if (r11 >= 0) goto L_0x0004;
    L_0x0003:
        r11 = 0;
    L_0x0004:
        r5 = r10.length();
        if (r5 <= 0) goto L_0x0048;
    L_0x000a:
        r7 = r5 + r11;
        r8 = r9.count;
        if (r7 <= r8) goto L_0x0012;
    L_0x0010:
        r2 = r6;
    L_0x0011:
        return r2;
    L_0x0012:
        r7 = 0;
        r0 = r10.charAt(r7);
    L_0x0017:
        r2 = r11;
        r1 = 0;
    L_0x0019:
        r7 = r9.count;
        if (r2 >= r7) goto L_0x0024;
    L_0x001d:
        r7 = r9.value;
        r7 = r7[r2];
        if (r7 != r0) goto L_0x002e;
    L_0x0023:
        r1 = 1;
    L_0x0024:
        if (r1 == 0) goto L_0x002c;
    L_0x0026:
        r7 = r5 + r2;
        r8 = r9.count;
        if (r7 <= r8) goto L_0x0031;
    L_0x002c:
        r2 = r6;
        goto L_0x0011;
    L_0x002e:
        r2 = r2 + 1;
        goto L_0x0019;
    L_0x0031:
        r3 = r2;
        r4 = 0;
    L_0x0033:
        r4 = r4 + 1;
        if (r4 >= r5) goto L_0x0043;
    L_0x0037:
        r7 = r9.value;
        r3 = r3 + 1;
        r7 = r7[r3];
        r8 = r10.charAt(r4);
        if (r7 == r8) goto L_0x0033;
    L_0x0043:
        if (r4 == r5) goto L_0x0011;
    L_0x0045:
        r11 = r2 + 1;
        goto L_0x0017;
    L_0x0048:
        r6 = r9.count;
        if (r11 < r6) goto L_0x004e;
    L_0x004c:
        if (r11 != 0) goto L_0x0051;
    L_0x004e:
        r6 = r11;
    L_0x004f:
        r2 = r6;
        goto L_0x0011;
    L_0x0051:
        r6 = r9.count;
        goto L_0x004f;
        */
        throw new UnsupportedOperationException("Method not decompiled: java.lang.AbstractStringBuilder.indexOf(java.lang.String, int):int");
    }

    public int lastIndexOf(String string) {
        return lastIndexOf(string, this.count);
    }

    public int lastIndexOf(String subString, int start) {
        int subCount = subString.length();
        if (subCount > this.count || start < 0) {
            return -1;
        }
        if (subCount > 0) {
            if (start > this.count - subCount) {
                start = this.count - subCount;
            }
            char firstChar = subString.charAt(0);
            while (true) {
                int i = start;
                boolean found = false;
                while (i >= 0) {
                    if (this.value[i] == firstChar) {
                        found = true;
                        break;
                    }
                    i--;
                }
                if (!found) {
                    return -1;
                }
                int o1 = i;
                int o2 = 0;
                do {
                    o2++;
                    if (o2 >= subCount) {
                        break;
                    }
                    o1++;
                } while (this.value[o1] == subString.charAt(o2));
                if (o2 == subCount) {
                    return i;
                }
                start = i - 1;
            }
        } else {
            return start < this.count ? start : this.count;
        }
    }

    public void trimToSize() {
        if (this.count < this.value.length) {
            char[] newValue = new char[this.count];
            System.arraycopy(this.value, 0, newValue, 0, this.count);
            this.value = newValue;
            this.shared = false;
        }
    }

    public int codePointAt(int index) {
        if (index >= 0 && index < this.count) {
            return Character.codePointAt(this.value, index, this.count);
        }
        throw indexAndLength(index);
    }

    public int codePointBefore(int index) {
        if (index >= 1 && index <= this.count) {
            return Character.codePointBefore(this.value, index);
        }
        throw indexAndLength(index);
    }

    public int codePointCount(int start, int end) {
        if (start >= 0 && end <= this.count && start <= end) {
            return Character.codePointCount(this.value, start, end - start);
        }
        throw startEndAndLength(start, end);
    }

    public int offsetByCodePoints(int index, int codePointOffset) {
        return Character.offsetByCodePoints(this.value, 0, this.count, index, codePointOffset);
    }
}
