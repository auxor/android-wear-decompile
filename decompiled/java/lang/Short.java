package java.lang;

import dalvik.bytecode.Opcodes;
import org.w3c.dom.traversal.NodeFilter;

@FindBugsSuppressWarnings({"DM_NUMBER_CTOR"})
public final class Short extends Number implements Comparable<Short> {
    public static final short MAX_VALUE = Short.MAX_VALUE;
    public static final short MIN_VALUE = Short.MIN_VALUE;
    public static final int SIZE = 16;
    private static final Short[] SMALL_VALUES = null;
    public static final Class<Short> TYPE = null;
    private static final long serialVersionUID = 7515723908773894738L;
    private final short value;

    static {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.lang.Short.<clinit>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.lang.Short.<clinit>():void
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
        throw new UnsupportedOperationException("Method not decompiled: java.lang.Short.<clinit>():void");
    }

    public Short(String string) throws NumberFormatException {
        this(parseShort(string));
    }

    public Short(short value) {
        this.value = value;
    }

    public byte byteValue() {
        return (byte) this.value;
    }

    public int compareTo(Short object) {
        return compare(this.value, object.value);
    }

    public static int compare(short lhs, short rhs) {
        if (lhs > rhs) {
            return 1;
        }
        return lhs < rhs ? -1 : 0;
    }

    public static Short decode(String string) throws NumberFormatException {
        short intValue = Integer.decode(string).intValue();
        short result = (short) intValue;
        if (result == intValue) {
            return valueOf(result);
        }
        throw new NumberFormatException("Value out of range for short: \"" + string + "\"");
    }

    public double doubleValue() {
        return (double) this.value;
    }

    public boolean equals(Object object) {
        return (object instanceof Short) && ((Short) object).value == this.value;
    }

    public float floatValue() {
        return (float) this.value;
    }

    public int hashCode() {
        return this.value;
    }

    public int intValue() {
        return this.value;
    }

    public long longValue() {
        return (long) this.value;
    }

    public static short parseShort(String string) throws NumberFormatException {
        return parseShort(string, 10);
    }

    public static short parseShort(String string, int radix) throws NumberFormatException {
        short intValue = Integer.parseInt(string, radix);
        short result = (short) intValue;
        if (result == intValue) {
            return result;
        }
        throw new NumberFormatException("Value out of range for short: \"" + string + "\"");
    }

    public short shortValue() {
        return this.value;
    }

    public String toString() {
        return Integer.toString(this.value);
    }

    public static String toString(short value) {
        return Integer.toString(value);
    }

    public static Short valueOf(String string) throws NumberFormatException {
        return valueOf(parseShort(string));
    }

    public static Short valueOf(String string, int radix) throws NumberFormatException {
        return valueOf(parseShort(string, radix));
    }

    public static short reverseBytes(short s) {
        return (short) ((s << 8) | ((s >>> 8) & Opcodes.OP_CONST_CLASS_JUMBO));
    }

    public static Short valueOf(short s) {
        return (s < (short) -128 || s >= (short) 128) ? new Short(s) : SMALL_VALUES[s + NodeFilter.SHOW_COMMENT];
    }
}
