package java.lang;

import org.w3c.dom.traversal.NodeFilter;

@FindBugsSuppressWarnings({"DM_NUMBER_CTOR"})
public final class Byte extends Number implements Comparable<Byte> {
    public static final byte MAX_VALUE = Byte.MAX_VALUE;
    public static final byte MIN_VALUE = Byte.MIN_VALUE;
    public static final int SIZE = 8;
    public static final Class<Byte> TYPE = null;
    private static final Byte[] VALUES = null;
    private static final long serialVersionUID = -7183698231559129828L;
    private final byte value;

    static {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: java.lang.Byte.<clinit>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: java.lang.Byte.<clinit>():void
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
        throw new UnsupportedOperationException("Method not decompiled: java.lang.Byte.<clinit>():void");
    }

    public Byte(byte value) {
        this.value = value;
    }

    public Byte(String string) throws NumberFormatException {
        this(parseByte(string));
    }

    public byte byteValue() {
        return this.value;
    }

    public int compareTo(Byte object) {
        return compare(this.value, object.value);
    }

    public static int compare(byte lhs, byte rhs) {
        if (lhs > rhs) {
            return 1;
        }
        return lhs < rhs ? -1 : 0;
    }

    public static Byte decode(String string) throws NumberFormatException {
        byte intValue = Integer.decode(string).intValue();
        byte result = (byte) intValue;
        if (result == intValue) {
            return valueOf(result);
        }
        throw new NumberFormatException("Value out of range for byte: \"" + string + "\"");
    }

    public double doubleValue() {
        return (double) this.value;
    }

    @FindBugsSuppressWarnings({"RC_REF_COMPARISON"})
    public boolean equals(Object object) {
        return object == this || ((object instanceof Byte) && ((Byte) object).value == this.value);
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

    public static byte parseByte(String string) throws NumberFormatException {
        return parseByte(string, 10);
    }

    public static byte parseByte(String string, int radix) throws NumberFormatException {
        byte intValue = Integer.parseInt(string, radix);
        byte result = (byte) intValue;
        if (result == intValue) {
            return result;
        }
        throw new NumberFormatException("Value out of range for byte: \"" + string + "\"");
    }

    public short shortValue() {
        return (short) this.value;
    }

    public String toString() {
        return Integer.toString(this.value);
    }

    public static String toHexString(byte b, boolean upperCase) {
        return IntegralToString.byteToHexString(b, upperCase);
    }

    public static String toString(byte value) {
        return Integer.toString(value);
    }

    public static Byte valueOf(String string) throws NumberFormatException {
        return valueOf(parseByte(string));
    }

    public static Byte valueOf(String string, int radix) throws NumberFormatException {
        return valueOf(parseByte(string, radix));
    }

    public static Byte valueOf(byte b) {
        return VALUES[b + NodeFilter.SHOW_COMMENT];
    }
}
