package java.lang;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.jar.Pack200.Unpacker;

public final class StringBuffer extends AbstractStringBuilder implements Appendable, Serializable, CharSequence {
    private static final ObjectStreamField[] serialPersistentFields;
    private static final long serialVersionUID = 3388685877147921107L;

    public /* bridge */ /* synthetic */ int capacity() {
        return super.capacity();
    }

    public /* bridge */ /* synthetic */ int indexOf(String x0) {
        return super.indexOf(x0);
    }

    public /* bridge */ /* synthetic */ int lastIndexOf(String x0) {
        return super.lastIndexOf(x0);
    }

    public /* bridge */ /* synthetic */ int length() {
        return super.length();
    }

    static {
        serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("count", Integer.TYPE), new ObjectStreamField("shared", Boolean.TYPE), new ObjectStreamField("value", char[].class)};
    }

    public StringBuffer(int capacity) {
        super(capacity);
    }

    public StringBuffer(String string) {
        super(string);
    }

    public StringBuffer(CharSequence cs) {
        super(cs.toString());
    }

    public StringBuffer append(boolean b) {
        return append(b ? Unpacker.TRUE : Unpacker.FALSE);
    }

    public synchronized StringBuffer append(char ch) {
        append0(ch);
        return this;
    }

    public StringBuffer append(double d) {
        RealToString.getInstance().appendDouble(this, d);
        return this;
    }

    public StringBuffer append(float f) {
        RealToString.getInstance().appendFloat(this, f);
        return this;
    }

    public StringBuffer append(int i) {
        IntegralToString.appendInt(this, i);
        return this;
    }

    public StringBuffer append(long l) {
        IntegralToString.appendLong(this, l);
        return this;
    }

    public synchronized StringBuffer append(Object obj) {
        if (obj == null) {
            appendNull();
        } else {
            append0(obj.toString());
        }
        return this;
    }

    public synchronized StringBuffer append(String string) {
        append0(string);
        return this;
    }

    public synchronized StringBuffer append(StringBuffer sb) {
        if (sb == null) {
            appendNull();
        } else {
            synchronized (sb) {
                append0(sb.getValue(), 0, sb.length());
            }
        }
        return this;
    }

    public synchronized StringBuffer append(char[] chars) {
        append0(chars);
        return this;
    }

    public synchronized StringBuffer append(char[] chars, int start, int length) {
        append0(chars, start, length);
        return this;
    }

    public synchronized StringBuffer append(CharSequence s) {
        if (s == null) {
            appendNull();
        } else {
            append0(s, 0, s.length());
        }
        return this;
    }

    public synchronized StringBuffer append(CharSequence s, int start, int end) {
        append0(s, start, end);
        return this;
    }

    public StringBuffer appendCodePoint(int codePoint) {
        return append(Character.toChars(codePoint));
    }

    public synchronized char charAt(int index) {
        return super.charAt(index);
    }

    public synchronized int codePointAt(int index) {
        return super.codePointAt(index);
    }

    public synchronized int codePointBefore(int index) {
        return super.codePointBefore(index);
    }

    public synchronized int codePointCount(int beginIndex, int endIndex) {
        return super.codePointCount(beginIndex, endIndex);
    }

    public synchronized StringBuffer delete(int start, int end) {
        delete0(start, end);
        return this;
    }

    public synchronized StringBuffer deleteCharAt(int location) {
        deleteCharAt0(location);
        return this;
    }

    public synchronized void ensureCapacity(int min) {
        super.ensureCapacity(min);
    }

    public synchronized void getChars(int start, int end, char[] buffer, int idx) {
        super.getChars(start, end, buffer, idx);
    }

    public synchronized int indexOf(String subString, int start) {
        return super.indexOf(subString, start);
    }

    public synchronized StringBuffer insert(int index, char ch) {
        insert0(index, ch);
        return this;
    }

    public StringBuffer insert(int index, boolean b) {
        return insert(index, b ? Unpacker.TRUE : Unpacker.FALSE);
    }

    public StringBuffer insert(int index, int i) {
        return insert(index, Integer.toString(i));
    }

    public StringBuffer insert(int index, long l) {
        return insert(index, Long.toString(l));
    }

    public StringBuffer insert(int index, double d) {
        return insert(index, Double.toString(d));
    }

    public StringBuffer insert(int index, float f) {
        return insert(index, Float.toString(f));
    }

    public StringBuffer insert(int index, Object obj) {
        return insert(index, obj == null ? "null" : obj.toString());
    }

    public synchronized StringBuffer insert(int index, String string) {
        insert0(index, string);
        return this;
    }

    public synchronized StringBuffer insert(int index, char[] chars) {
        insert0(index, chars);
        return this;
    }

    public synchronized StringBuffer insert(int index, char[] chars, int start, int length) {
        insert0(index, chars, start, length);
        return this;
    }

    public synchronized StringBuffer insert(int index, CharSequence s) {
        insert0(index, s == null ? "null" : s.toString());
        return this;
    }

    public synchronized StringBuffer insert(int index, CharSequence s, int start, int end) {
        insert0(index, s, start, end);
        return this;
    }

    public synchronized int lastIndexOf(String subString, int start) {
        return super.lastIndexOf(subString, start);
    }

    public synchronized int offsetByCodePoints(int index, int codePointOffset) {
        return super.offsetByCodePoints(index, codePointOffset);
    }

    public synchronized StringBuffer replace(int start, int end, String string) {
        replace0(start, end, string);
        return this;
    }

    public synchronized StringBuffer reverse() {
        reverse0();
        return this;
    }

    public synchronized void setCharAt(int index, char ch) {
        super.setCharAt(index, ch);
    }

    public synchronized void setLength(int length) {
        super.setLength(length);
    }

    public synchronized CharSequence subSequence(int start, int end) {
        return super.substring(start, end);
    }

    public synchronized String substring(int start) {
        return super.substring(start);
    }

    public synchronized String substring(int start, int end) {
        return super.substring(start, end);
    }

    public synchronized String toString() {
        return super.toString();
    }

    public synchronized void trimToSize() {
        super.trimToSize();
    }

    private synchronized void writeObject(ObjectOutputStream out) throws IOException {
        PutField fields = out.putFields();
        fields.put("count", length());
        fields.put("shared", false);
        fields.put("value", getValue());
        out.writeFields();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        GetField fields = in.readFields();
        set((char[]) fields.get("value", null), fields.get("count", 0));
    }
}
