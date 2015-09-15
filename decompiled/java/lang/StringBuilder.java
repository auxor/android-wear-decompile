package java.lang;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.jar.Pack200.Unpacker;

public final class StringBuilder extends AbstractStringBuilder implements Appendable, CharSequence, Serializable {
    private static final long serialVersionUID = 4383685877147921099L;

    public /* bridge */ /* synthetic */ int capacity() {
        return super.capacity();
    }

    public /* bridge */ /* synthetic */ char charAt(int x0) {
        return super.charAt(x0);
    }

    public /* bridge */ /* synthetic */ int codePointAt(int x0) {
        return super.codePointAt(x0);
    }

    public /* bridge */ /* synthetic */ int codePointBefore(int x0) {
        return super.codePointBefore(x0);
    }

    public /* bridge */ /* synthetic */ int codePointCount(int x0, int x1) {
        return super.codePointCount(x0, x1);
    }

    public /* bridge */ /* synthetic */ void ensureCapacity(int x0) {
        super.ensureCapacity(x0);
    }

    public /* bridge */ /* synthetic */ void getChars(int x0, int x1, char[] x2, int x3) {
        super.getChars(x0, x1, x2, x3);
    }

    public /* bridge */ /* synthetic */ int indexOf(String x0) {
        return super.indexOf(x0);
    }

    public /* bridge */ /* synthetic */ int indexOf(String x0, int x1) {
        return super.indexOf(x0, x1);
    }

    public /* bridge */ /* synthetic */ int lastIndexOf(String x0) {
        return super.lastIndexOf(x0);
    }

    public /* bridge */ /* synthetic */ int lastIndexOf(String x0, int x1) {
        return super.lastIndexOf(x0, x1);
    }

    public /* bridge */ /* synthetic */ int length() {
        return super.length();
    }

    public /* bridge */ /* synthetic */ int offsetByCodePoints(int x0, int x1) {
        return super.offsetByCodePoints(x0, x1);
    }

    public /* bridge */ /* synthetic */ void setCharAt(int x0, char x1) {
        super.setCharAt(x0, x1);
    }

    public /* bridge */ /* synthetic */ void setLength(int x0) {
        super.setLength(x0);
    }

    public /* bridge */ /* synthetic */ CharSequence subSequence(int x0, int x1) {
        return super.subSequence(x0, x1);
    }

    public /* bridge */ /* synthetic */ String substring(int x0) {
        return super.substring(x0);
    }

    public /* bridge */ /* synthetic */ String substring(int x0, int x1) {
        return super.substring(x0, x1);
    }

    public /* bridge */ /* synthetic */ void trimToSize() {
        super.trimToSize();
    }

    public StringBuilder(int capacity) {
        super(capacity);
    }

    public StringBuilder(CharSequence seq) {
        super(seq.toString());
    }

    public StringBuilder(String str) {
        super(str);
    }

    public StringBuilder append(boolean b) {
        append0(b ? Unpacker.TRUE : Unpacker.FALSE);
        return this;
    }

    public StringBuilder append(char c) {
        append0(c);
        return this;
    }

    public StringBuilder append(int i) {
        IntegralToString.appendInt(this, i);
        return this;
    }

    public StringBuilder append(long l) {
        IntegralToString.appendLong(this, l);
        return this;
    }

    public StringBuilder append(float f) {
        RealToString.getInstance().appendFloat(this, f);
        return this;
    }

    public StringBuilder append(double d) {
        RealToString.getInstance().appendDouble(this, d);
        return this;
    }

    public StringBuilder append(Object obj) {
        if (obj == null) {
            appendNull();
        } else {
            append0(obj.toString());
        }
        return this;
    }

    public StringBuilder append(String str) {
        append0(str);
        return this;
    }

    public StringBuilder append(StringBuffer sb) {
        if (sb == null) {
            appendNull();
        } else {
            append0(sb.getValue(), 0, sb.length());
        }
        return this;
    }

    public StringBuilder append(char[] chars) {
        append0(chars);
        return this;
    }

    public StringBuilder append(char[] str, int offset, int len) {
        append0(str, offset, len);
        return this;
    }

    public StringBuilder append(CharSequence csq) {
        if (csq == null) {
            appendNull();
        } else {
            append0(csq, 0, csq.length());
        }
        return this;
    }

    public StringBuilder append(CharSequence csq, int start, int end) {
        append0(csq, start, end);
        return this;
    }

    public StringBuilder appendCodePoint(int codePoint) {
        append0(Character.toChars(codePoint));
        return this;
    }

    public StringBuilder delete(int start, int end) {
        delete0(start, end);
        return this;
    }

    public StringBuilder deleteCharAt(int index) {
        deleteCharAt0(index);
        return this;
    }

    public StringBuilder insert(int offset, boolean b) {
        insert0(offset, b ? Unpacker.TRUE : Unpacker.FALSE);
        return this;
    }

    public StringBuilder insert(int offset, char c) {
        insert0(offset, c);
        return this;
    }

    public StringBuilder insert(int offset, int i) {
        insert0(offset, Integer.toString(i));
        return this;
    }

    public StringBuilder insert(int offset, long l) {
        insert0(offset, Long.toString(l));
        return this;
    }

    public StringBuilder insert(int offset, float f) {
        insert0(offset, Float.toString(f));
        return this;
    }

    public StringBuilder insert(int offset, double d) {
        insert0(offset, Double.toString(d));
        return this;
    }

    public StringBuilder insert(int offset, Object obj) {
        insert0(offset, obj == null ? "null" : obj.toString());
        return this;
    }

    public StringBuilder insert(int offset, String str) {
        insert0(offset, str);
        return this;
    }

    public StringBuilder insert(int offset, char[] ch) {
        insert0(offset, ch);
        return this;
    }

    public StringBuilder insert(int offset, char[] str, int strOffset, int strLen) {
        insert0(offset, str, strOffset, strLen);
        return this;
    }

    public StringBuilder insert(int offset, CharSequence s) {
        insert0(offset, s == null ? "null" : s.toString());
        return this;
    }

    public StringBuilder insert(int offset, CharSequence s, int start, int end) {
        insert0(offset, s, start, end);
        return this;
    }

    public StringBuilder replace(int start, int end, String string) {
        replace0(start, end, string);
        return this;
    }

    public StringBuilder reverse() {
        reverse0();
        return this;
    }

    public String toString() {
        return super.toString();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        set((char[]) in.readObject(), in.readInt());
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(length());
        out.writeObject(getValue());
    }
}
